package com.compendium.io.jabber;

/*
 * BSMessengerBean.java
 *
 * Project: BuddySpace
 * (C) Copyright Knowledge Media Institute 2002
 *
 *
 * Created on 18 July 2002, 18:31
 */

import java.util.*;

import org.jabber.jabberbeans.*;
import org.jabber.jabberbeans.util.*;
import org.jabber.jabberbeans.Extension.*;

/**
 * <code>BSMessengerBean</code> provides message handling.
 * It relies on <code>BSConnectionBean</code>, which must be set after each
 * reconnection. It uses <code>MessengerBean</code>.
 *
 * @author  Jiri Komzak, Knowledge Media Institute, Open University, United Kingdom
 */
public class BSMessengerBean implements PacketListener {
    //??? protected Hashtable chatThreads = null;
    private MessengerBean msgBean = null;
    private String name = "Messanger";
    private Hashtable unreadMsgs = null;
    private Hashtable msgTimeStamps = null;
    private Vector msgListeners = null;
    private Vector blockedJIDs = null;
    private boolean blockGroupChat = false;

    public static final String PLAIN_MESSAGE = "plain";

    /**
     * Constructor
     */
    BSMessengerBean() {
        //chatThreads = new Hashtable();
        unreadMsgs = new Hashtable();
        msgTimeStamps = new Hashtable();
        msgListeners = new Vector();
        blockedJIDs = new Vector();
    }

    /**
     * Constructor, which sets existing and connected <code>ConnectionBean</code>.
     * Then <code>MessengerBean</code> is created and this is registered
     * as listener for message packets.
     */
    BSMessengerBean(ConnectionBean connection) {
        this();
        setConnection(connection);
    }

    /**
     * Sets existing and connected <code>ConnectionBean</code>.
     * Then <code>MessengerBean</code> is created and this is registered
     * as listener for message packets.
     */
    protected void setConnection(ConnectionBean connection) {
        if (msgBean != null)
            msgBean.delPacketListener(this);
        msgBean = new MessengerBean(connection);
        if (msgBean != null)
            msgBean.addPacketListener(this);
    }

    /**
     * Returns currently used <code>ConnectionBean</code>.
     */
    protected ConnectionBean getConnection() {
        return (msgBean != null)? msgBean.getConnection() : null;
    }

    /**
     * Returns currently used <code>MessengerBean</code>.
     */
    public MessengerBean getMessengerBean() {
        return msgBean;
    }

    /**
     * Frees all object bindings to allow object destroy
     */
    protected void prepareToDestroy() {
        if (msgBean != null)
            msgBean.delPacketListener(this);
        removeAllMessageListeners();
        msgBean = null;
    }

    /**
     * Sends given message packet.
     */
    public boolean sendMessage(Message msg) {
        if (msg == null) {
            fireMessageError(null, "---", "Null message cannot be sent");
            return false;
        }
        if (msgBean == null || msgBean.getConnection() == null) {
            fireMessageError(msg.getToAddress(), "---", "Not connected");
            return false;
        }

        msgBean.getConnection().send(msg);
        return true;
    }

    /**
     * Sends message of given <code>type</code> with <code>subject</code>,
     * <code>body</code> and <code>thread</code> to given <code>jid</code>.
     * Returns if sending was successfull.
     */
    public boolean sendMessage(JID jid, String type, String subject, String body,
                               String thread) {
        MessageBuilder msgBuilder = new MessageBuilder();
        if (body != null)
            msgBuilder.setBody(body);
        if (jid != null)
            msgBuilder.setToAddress(jid);
        if (subject != null)
            msgBuilder.setSubject(subject);
        if (type != null && (type.equals("chat") || type.equals("groupchat") ||
            type.equals("headline")))
            msgBuilder.setType(type);
        if (thread != null)
            msgBuilder.setThread(thread);
        try {
            //msgBean.send((Message)msgBuilder.build());
            if (!sendMessage((Message)msgBuilder.build()))
                return false;
        } catch (InstantiationException e) {
            //BSCore.logEvent(name, "error: message builder failed");
            fireMessageError(jid, "---", "Message builder failed");
            return false;
        }

        return true;
    }

    /**
     * Sends plain message with given <code>body</code> and
     * <code>subject</code> to given <code>jid</code>.
     * Returns if sending was successfull.
     */
    public boolean sendMessage(JID jid, String body, String subject) {
        return sendMessage(jid, null, subject, body, null);
    }

    /**
     * Sends plain message with given <code>body</code> and
     * <code>subject</code> to given <code>jid</code>.
     * Returns if sending was successfull.
     */
    public boolean sendMessage(JID jid, String body) {
        return sendMessage(jid, null, null, body, null);
    }

    /**
     * Sends chat message with given <code>body</code> and <code>thread</code>
     * to given <code>jid</code>.
     * Returns if sending was successfull.
     */
    public boolean sendChatMessage(JID jid, String body, String thread) {
        return sendMessage(jid, "chat", null, body, thread);
    }

    /**
     * Returns the <code>Enumeration</code> of message packets for given
     * <code>jid</code> (or all JIDs if <code>jid</code> is null) of any from
     * given <code>types</code>. Plain message type is specified by
     * <code>BSMessengerBean.PLAIN_MESSAGE</code>.
     */
    public Enumeration getMessagePackets(JID jid, Vector types) {
        Vector msgs = new Vector();

        if (types == null || types.isEmpty()) return msgs.elements();

        // if messages for all jids
        if (jid == null) {
            Enumeration jids = unreadMsgs.elements();
            // for all jids
            while (jids.hasMoreElements()) {
                Vector jidsMsgs = (Vector) jids.nextElement();
                Enumeration msgsEnum = jidsMsgs.elements();
                // for all jid's messages
                while (msgsEnum.hasMoreElements()) {
                    Message m = (Message) msgsEnum.nextElement();
                    if ((m.getType() == null && types.contains(PLAIN_MESSAGE)) ||
                        (m.getType() != null && types.contains(m.getType())))
                        msgs.add(m);
                }
            }
            return msgs.elements();
        }

        // else jid specified
        String str = BSPresenceBean.getJIDHashString(jid, false);
        Vector jidsMsgs = (Vector) unreadMsgs.get(str);

        // if no messages
        if (jidsMsgs == null || jidsMsgs.isEmpty()) return msgs.elements();

        Enumeration msgsEnum = jidsMsgs.elements();
        // for all jid's messages
        while (msgsEnum.hasMoreElements()) {
            Message m = (Message) msgsEnum.nextElement();
            if ((jid.getResource() == null || jid.equals(m.getFromAddress())) &&
                (m.getType() == null && types.contains(PLAIN_MESSAGE)) ||
                (m.getType() != null && types.contains(m.getType())))
                msgs.add(m);
        }

        return msgs.elements();
    }

    /**
     * Returns the <code>Enumeration</code> of message packets of
     * given <code>type</code>.
     */
    public Enumeration getMessagePackets(String type) {
        Vector types = new Vector();
        if (type == null || type.equals(""))
            types.add(PLAIN_MESSAGE);
        else
            types.add(type);

        return getMessagePackets(null, types);
    }

    /**
     * Returns the <code>Enumeration</code> of message packets for given
     * <code>jid</code> (or all JIDs if <code>jid</code> is null) of
     * given <code>type</code>.
     */
    public Enumeration getMessagePackets(JID jid, String type) {
        Vector types = new Vector();
        if (type == null || type.equals(""))
            types.add(PLAIN_MESSAGE);
        else
            types.add(type);

        return getMessagePackets(jid, types);
    }

    /**
     * Returns the <code>Enumeration</code> of message packets for given
     * <code>jid</code> (or all JIDs if <code>jid</code> is null) of any type.
     */
    public Enumeration getMessagePackets(JID jid) {
        Vector types = new Vector();
        types.add(PLAIN_MESSAGE);
        types.add(new String("chat"));
        types.add(new String("headline"));
        types.add(new String("groupchat"));

        return getMessagePackets(jid, types);
    }

    /** Returns time-stamp when the given message was received */
    public Date getTimeStamp(Message msg) {
        return (Date) msgTimeStamps.get(msg);
    }

    /**
     * Returns the oldest unread message body for given <code>jid</code> and
     * removes the message from list of unread. Then it notifies listeners
     * that the message was read.
     */
    public String popFirstMessage(JID jid) {
        Message msg = popFirstMessagePacket(jid);

        //return new String("<" + jid.toString() + "> " + msg.getBody());
        if (msg == null) return null;
        String body = msg.getBody();
        return (body != null)? new String(body) : new String("");
    }

    /**
     * Returns the oldest unread message packet for given <code>jid</code> and
     * removes the message from list of unread. Then it notifies listeners
     * that the message was read.
     */
    public Message popFirstMessagePacket(JID jid) {
        if (jid == null) return null;

        String str = BSPresenceBean.getJIDHashString(jid, false);
        Vector jidsMsgs = (Vector) unreadMsgs.get(str);

        if (jidsMsgs == null || jidsMsgs.isEmpty()) return null;

        Message msg = null;

        // if no resource
        if (jid.getResource() == null || jid.getResource().equals("")) {
            try {
                msg = (Message) jidsMsgs.firstElement();
            } catch (NoSuchElementException e) {
                msg = null;
            }
        }
        // else some resource
        else {
            Enumeration msgs = jidsMsgs.elements();
            while (msgs.hasMoreElements()) {
                Message m = (Message) msgs.nextElement();
                if (jid.equals(m.getFromAddress()))
                    msg = m;
            }
        }

        // removes the read message
        jidsMsgs.remove(msg);
        fireMessageRead(msg);

        return msg;
    }

    /**
     * Deletes given message from unread messages and fires messageRead event.
     */
    public void deleteMessagePacket(Message msg) {
        JID jid;
        if (msg == null || (jid = msg.getFromAddress()) == null) return;

        String str = BSPresenceBean.getJIDHashString(jid, false);
        Vector jidsMsgs = (Vector) unreadMsgs.get(str);

        if (jidsMsgs == null || jidsMsgs.isEmpty() || !jidsMsgs.contains(msg))
            return;

        jidsMsgs.remove(msg);
        fireMessageRead(msg);
    }

    /**
     * Returns if there is some unread message for given <code>jid</code>.
     */
    public boolean isMessageWaiting(JID jid) {
        if (jid == null) return false;

        // gets list of messages for jid without resource
        String str = BSPresenceBean.getJIDHashString(jid, false);
        Vector jidsMsgs = (Vector) unreadMsgs.get(str);

        // if the list is empty
        if (jidsMsgs == null || jidsMsgs.isEmpty())
            return false;

        // if no resource
        if (jid.getResource() == null || jid.getResource().equals(""))
            return true;

        // looks for messages for specified resource
        Enumeration msgs = jidsMsgs.elements();
        while (msgs.hasMoreElements()) {
            Message m = (Message) msgs.nextElement();
            if (jid.equals(m.getFromAddress()))
                return true;
        }

        return false;
    }

    /**
     * Invoked when a message packet is received.
     */
    public void receivedPacket(PacketEvent pe) {
        if (!(pe.getPacket() instanceof Message)) {
            //BSCore.logEvent(name, "warning: non-message packet received");
            return;
        }

        Message msg = (Message) pe.getPacket();
        if ((new String("error")).equals(msg.getType()))
            handleError(msg);
        else {
			System.out.println("About to handle message");
            handleMessage(msg);
		}
    }

    /**
     * Stores message time-stamp.
     */
    protected void storeMessageTimeStamp(Message msg) {
        Date timeStamp = null;
        Enumeration extEnum = msg.Extensions();
        while (extEnum.hasMoreElements()) {
            Object o = extEnum.nextElement();
            if (o instanceof XDelay)
                timeStamp = History.getTime((XDelay)o);
        }
        msgTimeStamps.put(msg, (timeStamp != null)? timeStamp: new Date());
    }

    /**
     * Handles <code>Message</code> packet, if it doesn't contain an error.
     */
    private void handleMessage(Message msg) {
        String type = msg.getType();

        if (blockGroupChat && type != null && type.equals("groupchat")) {
            return;
		}

        JID jid = msg.getFromAddress();

        // returns if message from blocked jid is received
        JID noResJID = new JID(jid.getUsername(), jid.getServer(), null);
        if (blockedJIDs.contains(noResJID))
            return;

        String body = msg.getBody();

        BSCore.logMessage(jid.toString(), msg.getSubject(), body);

        // adds the message into unread messages
        String str = BSPresenceBean.getJIDHashString(jid, false);
        Vector jidsMsgs = (Vector) unreadMsgs.get(str);
        if (jidsMsgs == null) {
            jidsMsgs = new Vector();
            unreadMsgs.put(str, jidsMsgs);
        }
        jidsMsgs.add(msg);

        storeMessageTimeStamp(msg);

        //fireMessageReceived(jid);
        fireMessageReceived(msg);
    }

    /**
     * Handles <code>Message</code> packet, if it does contain an error.
     */
    private void handleError(Message msg) {
        //BSCore.logEvent(name, "error " + msg.getErrorCode() + ": " + msg.getErrorText());

        JID jid = msg.getFromAddress();

        storeMessageTimeStamp(msg);

        //fireMessageError(jid, msg.getErrorCode(), msg.getErrorText());
        fireMessageError(msg);
    }

    /**
     * Invoked when a message packet send failes.
     */
    public void sendFailed(PacketEvent pe) {
        //BSCore.logEvent(name, "error: message send failed");
    }

    /**
     * Invoked when a message packet is sent.
     */
    public void sentPacket(PacketEvent pe) {
    }

    /**
     * Starts blocking messages from given JID and its resources.
     */
    public void startBlocking(JID jid) {
        JID noResJID = new JID(jid.getUsername(), jid.getServer(), null);
        if (!blockedJIDs.contains(noResJID))
            blockedJIDs.add(noResJID);
    }

    /**
     * Stops blocking messages from given JID and its resources.
     */
    public void stopBlocking(JID jid) {
        JID noResJID = new JID(jid.getUsername(), jid.getServer(), null);
        if (blockedJIDs.contains(noResJID))
            blockedJIDs.remove(noResJID);
    }

    /**
     * Starts blocking groupchat messages.
     */
    public void startBlockingGroupChat() {
        blockGroupChat = true;
    }

    /**
     * Stops blocking blocking groupchat messages.
     */
    public void stopBlockingGroupChat() {
        blockGroupChat = false;
    }

    // *** message listeners ***

    /**
     * Adds <code>MessageListener</code> from listeners notified when
     * unread messages change.
     *
     * @see #removeMessageListener
     * @see #removeAllMessageListeners
     * @see #fireMessageReceived
     * @see #fireMessageRead
     * @see #fireMessageError
     */
    public void addMessageListener(BSMessageListener listener) {
        //assert listener != null : listener;
        if (listener == null) return;
        if (!msgListeners.contains(listener)) {
            msgListeners.addElement(listener);
        }
    }

    /**
     * Removes <code>MessageListener</code> from listeners notified when
     * unread messages change.
     *
     * @see #addMessageListener
     * @see #removeAllMessageListeners
     * @see #fireMessageReceived
     * @see #fireMessageRead
     * @see #fireMessageError
     */
    public void removeMessageListener(BSMessageListener listener) {
        //assert listener != null : listener;
        if (listener == null) return;
        msgListeners.removeElement(listener);
    }

    /**
     * Removes all listeners notified when unread messages change.
     * This can be used before to free dependencies and allow dispose of
     * all objects.
     *
     * @see #addMessageListener
     * @see #removeMessageListener
     * @see #fireMessageReceived
     * @see #fireMessageRead
     * @see #fireMessageError
     */
    public void removeAllMessageListeners() {
        msgListeners.clear();
    }

    /**
     * Notifies message listeners that message was received from
     * <code>fromAddress</code>.
     *
     * @see #addMessageListener
     * @see #removeMessageListener
     * @see #removeAllMessageListeners
     * @see #fireMessageRead
     * @see #fireMessageError
     */
    private void fireMessageReceived(Message msg) {
        if (msg == null) return;
        String type = msg.getType();
        for (Enumeration e = msgListeners.elements(); e.hasMoreElements(); ) {
            BSMessageListener listener = (BSMessageListener) e.nextElement();
            //listener.messageRead(msg.getFromAddress());
            if (type == null || type.equals("") || type.equals(PLAIN_MESSAGE))
                listener.plainMessageReceived(msg);
            else if (type.equals("chat"))
                listener.chatMessageReceived(msg);
            else if (type.equals("groupchat"))
                listener.groupchatMessageReceived(msg);
            else if (type.equals("headline"))
                listener.headlineMessageReceived(msg);
        }
    }

    /**
     * Notifies message listeners that a message from
     * <code>fromAddress</code> was read.
     *
     * @see #addMessageListener
     * @see #removeMessageListener
     * @see #removeAllMessageListeners
     * @see #fireMessageReceived
     * @see #fireMessageError
     */
    private void fireMessageRead(Message msg) {
        for (Enumeration e = msgListeners.elements(); e.hasMoreElements(); ) {
            BSMessageListener listener = (BSMessageListener) e.nextElement();
            //listener.messageRead(msg.getFromAddress());
            listener.messageRead(msg);
        }
    }

    /**
     * Notifies message listeners that a message to
     * <code>toAddress</code> was not delivered because of <code>error</code>.
     *
     * @see #addMessageListener
     * @see #removeMessageListener
     * @see #removeAllMessageListeners
     * @see #fireMessageReceived
     * @see #fireMessageRead
     */
    private void fireMessageError(Message msg) {
        for (Enumeration e = msgListeners.elements(); e.hasMoreElements(); ) {
            BSMessageListener listener = (BSMessageListener) e.nextElement();
            /*listener.messageError(msg.getFromAddress(),
                                  msg.getErrorCode(), msg.getErrorText());*/
            listener.messageError(msg);
        }
    }

    /**
     * Notifies message listeners that a message to
     * <code>toAddress</code> was not delivered because of <code>error</code>.
     *
     * @see #addMessageListener
     * @see #removeMessageListener
     * @see #removeAllMessageListeners
     * @see #fireMessageReceived
     * @see #fireMessageRead
     */
    private void fireMessageError(JID toAddress, String errType, String error) {
        for (Enumeration e = msgListeners.elements(); e.hasMoreElements(); ) {
            BSMessageListener listener = (BSMessageListener) e.nextElement();
            listener.messageError(toAddress, errType, error);
        }
    }


    /**
     * Returns the newest unread message for given <code>jid</code> and
     * removes the message from list of unread. Then it notifies listeners
     * that the message was read.
     */
    /*public String popLastMessage(JID jid) {
        if (jid == null) return null;

        String str = BSPresenceBean.getJIDHashString(jid, false);
        Vector jidsMsgs = (Vector) unreadMsgs.get(str);

        if (jidsMsgs == null || jidsMsgs.isEmpty()) return null;

        Message msg = null;

        // if no resource
        if (jid.getResource() == null || jid.getResource().equals("")) {
            try {
                msg = (Message) jidsMsgs.lastElement();
            } catch (NoSuchElementException e) {
                msg = null;
            }
        }
        // else some resource
        else {
            Enumeration msgs = jidsMsgs.elements();
            while (msgs.hasMoreElements()) {
                Message m = (Message) msgs.nextElement();
                if (jid.equals(m.getFromAddress()))
                    msg = m;
            }
        }

        // removes the message
        jidsMsgs.remove(msg);
        fireMessageRead(jid);

        //return new String("<" + jid.toString() + "> " + msg.getBody());
        return new String(msg.getBody());
    }*/

    /**
     * Sends message with given <code>body</code> and <code>subject</code>
     * to given <code>jid</code>.
     */
    /*public void sendMessage(JID jid, String body, String subject, String type) {
        MessageBuilder msgBuilder = new MessageBuilder();
        if (body != null)
            msgBuilder.setBody(body);
        if (jid != null)
            msgBuilder.setToAddress(jid);
        if (subject != null)
            msgBuilder.setSubject(subject);
        if (type != null && (type.equals("chat") || type.equals("groupchat")))
            msgBuilder.setType(type);
        try {
            //msgBean.send((Message)msgBuilder.build());
            msgBean.getConnection().send(msgBuilder.build());
        } catch (InstantiationException e) {
            //BSCore.logEvent(name, "error: message builder failed");
            fireMessageError(jid, "---", "Message builder failed");
            return;
        }
    }*/

}
