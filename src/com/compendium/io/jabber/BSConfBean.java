package com.compendium.io.jabber;

/*
 * BSConfBean.java
 *
 * Project: BuddySpace
 * (C) Copyright Knowledge Media Institute 2002
 *
 *
 * Created on 12 September 2002, 10:40
 */

import java.util.*;
import javax.swing.*;

import org.jabber.jabberbeans.*;
import org.jabber.jabberbeans.util.*;
import org.jabber.jabberbeans.Extension.*;

/**
 * <code>BSConfBean</code> provides conferencing handling.
 * It relies on <code>BSConnectionBean</code>, which must be set after each
 * reconnection.
 *
 * @author  Jiri Komzak, Knowledge Media Institute, Open University, United Kingdom
 */
public class BSConfBean implements PacketListener, BSPresenceListener {

    protected MessengerBean msgBean = null;
    protected BSMessengerBean bsMsgBean = null;
    protected IQBean iqBean = null;
    protected BSPresenceBean presenceBean = null;
    protected String name = "Conference";
    protected Hashtable rooms = null;
    protected Vector confListeners = null;

    /**
     * Constructor
     */
    public BSConfBean() {
        rooms = new Hashtable();
        confListeners = new Vector();
    }

    /**
     * Sets existing and connected <code>IQBean</code>.
     * Then this is registered as listener for IQ packets.
     */
    public void setIQBean(IQBean iqBean) {
        if (this.iqBean != null)
            this.iqBean.delPacketListener(this);
        this.iqBean = iqBean;
        if (iqBean != null)
            iqBean.addPacketListener(this);
    }

    /**
     * Sets existing and connected <code>MessengerBean</code>.
     * Then this is registered as listener for message packets.
     */
    public void setMessengerBean(BSMessengerBean bsMsgBean) {
        this.bsMsgBean = bsMsgBean;
        if (this.msgBean != null)
            this.msgBean.delPacketListener(this);
        if (bsMsgBean != null)
            msgBean = bsMsgBean.getMessengerBean();
        else
            msgBean = null;
        if (msgBean != null)
            msgBean.addPacketListener(this);
    }

    /**
     * Sets existing and connected <code>BSPresenceBean</code>.
     * Then this is registered as listener for presence events.
     */
    public void setPresenceBean(BSPresenceBean presenceBean) {
        if (this.presenceBean != null)
            this.presenceBean.removePresenceListener(this);
        this.presenceBean = presenceBean;
        if (presenceBean != null)
            presenceBean.addPresenceListener(this);
    }

    /**
     * Returns currently used <code>IQBean</code>.
     */
    public IQBean getIQBean() {
        return iqBean;
    }

    /**
     * Returns currently used <code>MessengerBean</code>.
     */
    public MessengerBean getMessengerBean() {
        return msgBean;
    }

    /**
     * Returns currently used <code>BSPresenceBean</code>.
     */
    public BSPresenceBean getPresenceBean() {
        return presenceBean;
    }

    /**
     * Called when disconneted.
     */
    public void disconnected() {
        if (rooms != null)
            rooms.clear();
    }

    /**
     * Frees all object bindings to allow object destroy
     */
    public void prepareToDestroy() {
        removeAllConfListeners();
        msgBean = null;
        iqBean = null;
        if (presenceBean != null) {
            presenceBean.removePresenceListener(this);
            presenceBean = null;
        }
    }

    /**
     * Creates or joins a conference room.
     */
    public void createRoom(String roomName, String server, String nick) {
        JID roomJID = new JID(roomName, server, nick);

        if (iqBean == null || presenceBean == null) {
            fireError(roomJID, "", "IQ or presence bean not set");
            BSCore.logEvent(name, "error: IQ or presence bean not set");
            return;
        }

        BSPresenceInfo pi = new BSPresenceInfo(null, true, null, null);

        // creates room info
        BSConfRoomInfo ri = new BSConfRoomInfo(roomJID);
        String str = BSPresenceBean.getJIDHashString(roomJID, false);
        if (rooms.get(str.toLowerCase()) != null) {
            fireError(roomJID, "", "already in room");
            BSCore.logEvent(name, "warning: already in room - cancelling");
            return;
        }
        rooms.put(str.toLowerCase(), ri);

        // sends first presence into the room
        sendPresence(pi, roomJID);
        // sets my presence in room
        ri.setMyPresence(pi.isOnline(), pi.getShow(), pi.getStatus());

        // sends initial IQ into room
        iqSetupRoom(roomName, roomJID, nick, ri);

        // blocks messages in messengerBean - everything will be handled in here
        if (bsMsgBean != null) {
            //bsMsgBean.startBlocking(roomJID);
            bsMsgBean.startBlockingGroupChat();
        }
    }

    /** Sends presence to the jid (room) */
    public void sendPresence(BSPresenceInfo pi, JID roomAddress) {
        if (presenceBean != null) {
            presenceBean.sendPresence(pi, roomAddress);
        }
    }

    /** Leaves the room */
    public void leaveRoom(JID roomJID) {
        String str = BSPresenceBean.getJIDHashString(roomJID, false);
        BSConfRoomInfo ri = (BSConfRoomInfo) rooms.remove(str.toLowerCase());
        if (ri != null) {
            if (ri.state == BSConfRoomInfo.LOGGED ||
                ri.state == BSConfRoomInfo.LOGGING2) {
                sendPresence(new BSPresenceInfo(null, false, null, null), roomJID);
            }
            return;
        }
        if (bsMsgBean != null) {
            //bsMsgBean.stopBlocking(roomJID);
            bsMsgBean.stopBlockingGroupChat();
        }
    }

    /**
     * Sends message to the room.
     * @param roomAddress is JID of room
     * @param body is body of message
     */
    public void sendMessage(JID roomAddress, String body) {

        if (msgBean == null || roomAddress == null) {
            fireError(roomAddress, "", "cannot send message");
            return;
        }

        MessageBuilder msgBuilder = new MessageBuilder();
        if (body != null)
            msgBuilder.setBody(body);
        msgBuilder.setToAddress(roomAddress);
        msgBuilder.setType("groupchat");
        try {
            //msgBean.send((Message)msgBuilder.build());
            msgBean.getConnection().send(msgBuilder.build());
        } catch (InstantiationException e) {
            BSCore.logEvent(name, "error: message builder failed");
            fireError(roomAddress, "", "message builder failed");
            return;
        }
    }

    /**
     * Sends the initial IQ-set to the room.
     */
    private void iqSetupRoom(String roomName, JID roomAddress, String nick,
                             BSConfRoomInfo roomInfo) {

        if (iqBean == null) return;

        roomInfo.servedID = new String("conf_" + BSCore.getNextID());

        InfoQueryBuilder iqBuilder = new InfoQueryBuilder();
        IQConfBuilder iqConfBuilder = new IQConfBuilder();
        iqConfBuilder.addNick(nick);
        iqConfBuilder.setRoomName(roomName);
        iqConfBuilder.setPrivacy(false);

        try {
            iqBuilder.addExtension(iqConfBuilder.build());
            iqBuilder.setType("set");
            iqBuilder.setIdentifier(roomInfo.servedID);
            iqBuilder.setToAddress(roomAddress);
            //iqBean.send((InfoQuery)iqBuilder.build());
            iqBean.getConnection().send(iqBuilder.build());
        } catch (InstantiationException e) {
            BSCore.logEvent(name, "error: IQ builder failed");
            fireError(roomAddress, "", "IQ builder failed");
            roomInfo.servedID = null;
            roomInfo.setState(BSConfRoomInfo.NOT_LOGGED);
            return;
        }
        roomInfo.setState(BSConfRoomInfo.LOGGING2);
    }

    /**
     * Invoked when a message or iq packet is received.
     */
    public void receivedPacket(PacketEvent pe) {

        if (pe.getPacket() instanceof Message) {
            Message msg = (Message) pe.getPacket();
            handleMessage(msg);
            return;
        }

        else if (pe.getPacket() instanceof InfoQuery) {
            InfoQuery iq = (InfoQuery) pe.getPacket();
            handleInfoQuery(iq);
            return;
        }

        else {
            //BSCore.logEvent(name, "warning: non-map packet received");
            return;
        }
    }

    /** Handles received IQ packet */
    private void handleInfoQuery(InfoQuery iq) {
        JID roomJID = iq.getFromAddress();
        String type = iq.getType();

        if (type == null) {
            fireError(roomJID, "", "IQ packet without type");
            BSCore.logEvent(name, "error: IQ packet without type");
            return;
        }

        // gets room info for given roomJID
        String str = BSPresenceBean.getJIDHashString(roomJID, false);
        BSConfRoomInfo ri = (BSConfRoomInfo) rooms.get(str.toLowerCase());

        if (ri == null) return;

        if (type.equals("result"))
            handleIQResult(iq, ri);
        else if (type.equals("set"))
            handleIQSet(iq, ri);
        else if (type.equals("error"))
            handleIQError(iq, ri);
        else if (type.equals("get"))
            ;//handleIQGet(iq, ri);
        else {
            fireError(roomJID, "", "IQ packet with unknown type");
            BSCore.logEvent(name, "error: IQ packet with unknown type");
        }
    }

    /** Handles IQ error */
    private void handleIQError(InfoQuery iq, BSConfRoomInfo ri) {
        if (ri.state == BSConfRoomInfo.LOGGING2)
            ri.setState(BSConfRoomInfo.NOT_LOGGED);

        fireError(ri.roomJID, iq.getErrorCode(), iq.getErrorText());
    }

    /** Handles received message */
    private void handleMessage(Message msg) {
        maybeHandleInvitation(msg);

        // gets room info for JID room
        JID jid = msg.getFromAddress();
        String str = BSPresenceBean.getJIDHashString(jid, false);
        BSConfRoomInfo ri = (BSConfRoomInfo) rooms.get(str.toLowerCase());

        if (ri == null) return;

        String type = msg.getType();
        if (type != null && type.equals("error")) {
            //fireError(ri.roomJID, msg.getErrorCode(), msg.getErrorText());
        }

        else {
            handleGroupchatMsg(msg, ri);
        }
    }

    // handles message to the room
    private void handleGroupchatMsg(Message msg, BSConfRoomInfo ri) {
        JID jid = msg.getFromAddress();
        String str = BSPresenceBean.getJIDHashString(jid, true);
        String nick = (String) ri.nicks.get(str);

        String type = msg.getType();
        // if message to the room
        if (type != null && type.equals("groupchat")) {
            if (nick != null)
                fireGroupMessage(jid, nick, msg.getBody());
            else
                fireRoomMessage(jid, msg.getBody());
        }
        else {
            if (type == null || type.equals(""))
                type = new String("msg");
            // if message from room
            if (nick != null)
                firePrivateMessage(jid, nick, msg.getBody());
            // else private message
            else
                firePrivateMessage(jid, "[room]", msg.getBody());
        }
    }

    /** Tries to handle message as invitation */
    private void maybeHandleInvitation(Message msg) {
        String type = msg.getType();
        if (type != null && !type.equals("")) return;

        Enumeration exts = msg.Extensions();
        while (exts.hasMoreElements()) {
            Extension e = (Extension) exts.nextElement();
            if (e instanceof XConference) {
                JID roomJID = ((XConference) e).getAddress();
                fireInvitation(msg.getFromAddress(), roomJID, msg.getSubject(), msg.getBody());
            }
        }
    }

    /** Handles IQ result */
    private void handleIQResult(InfoQuery iq, BSConfRoomInfo ri) {
        String id = iq.getIdentifier();

        if (ri.state == BSConfRoomInfo.LOGGING2 &&
            ri.servedID != null && ri.servedID.equals(id)) {
                setLogged(iq, ri);
        }
        else if (ri.state == BSConfRoomInfo.CHANGING_NICK &&
            ri.servedID != null && ri.servedID.equals(id)) {
                //setNickChanged(iq, ri);
        }
        else {
            fireError(ri.roomJID, "", "unexpected IQ result");
            BSCore.logEvent(name, "warning: unexpected IQ result");
        }
    }

    /** Handles IQ set */
    private void handleIQSet(InfoQuery iq, BSConfRoomInfo ri) {
        Enumeration exts = iq.Extensions();

        // for all extensions
        while (exts.hasMoreElements()) {
            Extension e = (Extension) exts.nextElement();
            // if browse extension
            if (e instanceof IQBrowse) {
                IQBrowse b = (IQBrowse) e;
                String category = b.getCategory();
                // if item is user category
                if (category.equals("user"))
                    handleIQBrowseUser(b, ri);
                // else if item is conference category
                else if (category.equals("conference")) {
                    String extName = b.getName();
                    String type = b.getType();
                    fireRoomMessage(ri.roomJID, "room \"" + extName + "\" is of type \"" + type + "\"");
                    Enumeration enumeration = b.children();
                    // for all elements inside
                    while (enumeration.hasMoreElements()) {
                        BrowseItem bi = (BrowseItem) enumeration.nextElement();
                        category = bi.getCategory();
                        // if user
                        if (category.equals("user"))
                            handleIQBrowseUser(bi, ri);
                    } // for all BrowseItems
                } //if conference
            } //if IQBrowse
        } //for all extensions
    }

    /** Handles IQ browse user item */
    private void handleIQBrowseUser(BrowseItem b, BSConfRoomInfo ri) {
        String extName = b.getName();
        JID jid = b.getJID();
        String type = b.getType();
        String hashJID = BSPresenceBean.getJIDHashString(jid, true);
        // if type == remove
        if (type != null && type.equals("remove")) {
            // removes the user
            extName = (String) ri.nicks.get(hashJID);
            //ri.nicks.remove(hashJID);
            presenceBean.setPresence(jid, false, null, null, 0);
            //fireRoomMessage(ri.roomJID, extName + " left the room");
        }
        // else adds the user
        else {
            ri.nicks.put(hashJID, extName);
            presenceBean.setPresence(jid, true, null, null, 0);
            //fireRoomMessage(ri.roomJID, extName + " entered the room");
        }
    }

    /** Sets logged after receiving IQ result for creating/joining IQ set */
    private void setLogged(InfoQuery iq, BSConfRoomInfo ri) {
        Enumeration exts = iq.Extensions();

        while (exts.hasMoreElements()) {
            Extension e = (Extension) exts.nextElement();
            // if conference extension, sets information about the room
            if (e instanceof IQConf) {
                IQConf c = (IQConf) e;
                ri.setRoomName(c.getRoomName());
                ri.setMyJID(JID.fromString(c.getID()));
                fireSetMyJID(ri.roomJID, JID.fromString(c.getID()));
                fireRoomMessage(ri.roomJID, "logged into room \"" + c.getRoomName() + "\"");
                ri.setState(BSConfRoomInfo.LOGGED);
            }
        }
    }

    /** Sends invitation into given room to given JID */
    public void sendInvitation(JID toAddress, JID roomJID, String subject, String body) {
        if (toAddress == null || roomJID == null)
            return;

        XConferenceBuilder confBuilder = new XConferenceBuilder();
        confBuilder.setAddress(roomJID);
        MessageBuilder msgBuilder = new MessageBuilder();
        msgBuilder.setToAddress(toAddress);
        if (body != null)
            msgBuilder.setBody(body);
        if (subject != null)
            msgBuilder.setSubject(subject);
        try {
            msgBuilder.addExtension(confBuilder.build());
            //msgBean.send((Message)msgBuilder.build());
            msgBean.getConnection().send(msgBuilder.build());
        } catch (InstantiationException e) {
            fireError(roomJID, "", "message builder failed");
            BSCore.logEvent(name, "error: message builder failed");
            return;
        }
    }

    /** called if a packet is not successfully sent (for instance, if the connection
     * dies while the packet is queued, or a packet is sent while disconnected).
     *
     * @param pe PacketEvent for the failed send
     */
    public void sendFailed(PacketEvent pe) {
    }

    /** called whenever a local client sends a packet, after the sending
     * is successful
     *
     * @param pe PacketEvent that has just been sent
     */
    public void sentPacket(PacketEvent pe) {
    }

    /** Returns users inside given room */
    public Enumeration getRoster(JID roomJID) {
        if (presenceBean == null) {
            return (new Vector()).elements();
        }

        roomJID = JID.fromString(roomJID.toString().toLowerCase());
        return presenceBean.getAllPresences(roomJID);
    }

    /** Returns nick of given JID */
    public String getNick(JID jid) {
        String nick = null;
        String str = BSPresenceBean.getJIDHashString(jid, false);
        BSConfRoomInfo ri = (BSConfRoomInfo) rooms.get(str.toLowerCase());
        if (ri != null) {
            str = BSPresenceBean.getJIDHashString(jid, true);
            nick = (String) ri.nicks.get(str);
        }
        if (nick == null)
            nick = jid.getResource();
        return nick;
    }

    // *** presence events handling ***

    /** <code>BSPresenceListener</code> function - gets nick and sends farther */
    public void presenceChanged(BSPresenceInfo pi) {
        if (pi == null || pi.getJID() == null) return;

        String hashJID;

        hashJID = BSPresenceBean.getJIDHashString(pi.getJID(), false);
        BSConfRoomInfo ri = (BSConfRoomInfo) rooms.get(hashJID.toLowerCase());
        if (ri == null) return;

        //hashJID = BSPresenceBean.getJIDHashString(pi.getJID(), true);
        //String extName = (String) ri.nicks.get(hashJID);
        String extName = getNick(pi.getJID());

        firePresenceChanged(pi.getJID(), extName, pi);
    }

    /** <code>BSPresenceListener</code> function - clears all presences */
    public void presencesCleared() {
        if (presenceBean == null) return;
        // for all rooms
        Enumeration ris = rooms.elements();
        while (ris.hasMoreElements()) {
            BSConfRoomInfo ri = (BSConfRoomInfo) ris.nextElement();
            // for all presences in room
            Enumeration presences = getRoster(ri.roomJID);
            while (presences.hasMoreElements()) {
                BSPresenceInfo pi = (BSPresenceInfo) presences.nextElement();
                presenceBean.setPresence(pi.getJID(), false, null, null, 0);
                BSPresenceInfo newPi = new BSPresenceInfo(pi.getJID(), false, null, null);
                firePresenceChanged(pi.getJID(), getNick(pi.getJID()), newPi);
            }
        }
    }

    /** <code>BSPresenceListener</code> function - empty implementation */
    public void subscriptionApproved(JID jid) { }
    /** <code>BSPresenceListener</code> function - empty implementation */
    public void subscriptionRequested(JID jid) { }

    // *** conference listeners ***

    /**
     * Adds <code>BSConfListener</code> from listeners notified when
     * conference event appears.
     *
     * @see #removeConfListener
     * @see #removeAllConfListeners
     * @see #fireConfReceived
     */
    public void addConfListener(BSConfListener listener) {
        //assert listener != null : listener;
        if (listener == null) return;
        if (!confListeners.contains(listener)) {
            confListeners.addElement(listener);
        }
    }

    /**
     * Removes <code>BSConfListener</code> from listeners notified when
     * conference event appears.
     *
     * @see #addConfListener
     * @see #removeAllConfListeners
     * @see #fireConfReceived
     */
    public void removeConfListener(BSConfListener listener) {
        //assert listener != null : listener;
        if (listener == null) return;
        confListeners.removeElement(listener);
    }

    /**
     * Removes all listeners notified when conference event appears.
     * This can be used before to free dependencies and allow dispose of
     * all objects.
     *
     * @see #addConfListener
     * @see #removeConfListener
     * @see #fireConfReceived
     */
    public void removeAllConfListeners() {
        confListeners.clear();
    }

    /**
     * Notifies conference listeners that groupchat message arrived.
     *
     * @see #addConfListener
     * @see #removeConfListener
     * @see #removeAllConfListeners
     */
    protected void fireGroupMessage(JID fromAddress, String nick, String body) {
        for (Enumeration e = confListeners.elements(); e.hasMoreElements(); ) {
            BSConfListener listener = (BSConfListener) e.nextElement();
            listener.groupMessage(fromAddress, nick, body);
        }
    }

    /**
     * Notifies conference listeners that message from room arrived.
     *
     * @see #addConfListener
     * @see #removeConfListener
     * @see #removeAllConfListeners
     */
    protected void fireRoomMessage(JID roomJID, String body) {
        for (Enumeration e = confListeners.elements(); e.hasMoreElements(); ) {
            BSConfListener listener = (BSConfListener) e.nextElement();
            listener.roomMessage(roomJID, body);
        }
    }

    /**
     * Notifies conference listeners that private message arrived.
     *
     * @see #addConfListener
     * @see #removeConfListener
     * @see #removeAllConfListeners
     */
    protected void firePrivateMessage(JID fromAddress, String nick, String body) {
        for (Enumeration e = confListeners.elements(); e.hasMoreElements(); ) {
            BSConfListener listener = (BSConfListener) e.nextElement();
            listener.privateMessage(fromAddress, nick, body);
        }
    }

    /**
     * Notifies conference listeners that presence of nick in room changed.
     *
     * @see #addConfListener
     * @see #removeConfListener
     * @see #removeAllConfListeners
     */
    protected void firePresenceChanged(JID fromAddress, String nick, BSPresenceInfo pi) {
        for (Enumeration e = confListeners.elements(); e.hasMoreElements(); ) {
            BSConfListener listener = (BSConfListener) e.nextElement();
            listener.presenceChanged(fromAddress, nick, pi);
        }
    }

    /**
     * Notifies conference listeners that invitation into room arrived.
     *
     * @see #addConfListener
     * @see #removeConfListener
     * @see #removeAllConfListeners
     */
    protected void fireInvitation(JID fromAddress, JID roomJID,
                                  String subject, String body) {

        for (Enumeration e = confListeners.elements(); e.hasMoreElements(); ) {
            BSConfListener listener = (BSConfListener) e.nextElement();
            listener.invitation(fromAddress, roomJID, subject, body);
        }
    }

    /**
     * Notifies conference listeners that error occured.
     *
     * @see #addConfListener
     * @see #removeConfListener
     * @see #removeAllConfListeners
     */
    protected void fireError(JID roomJID, String errCode, String errMsg) {
        for (Enumeration e = confListeners.elements(); e.hasMoreElements(); ) {
            BSConfListener listener = (BSConfListener) e.nextElement();
            listener.error(roomJID, errCode, errMsg);
        }
    }


    /**
     * Notifies conference listeners about myJID in conference.
     *
     * @see #addConfListener
     * @see #removeConfListener
     * @see #removeAllConfListeners
     */
    protected void fireSetMyJID(JID roomJID, JID myJID) {
        for (Enumeration e = confListeners.elements(); e.hasMoreElements(); ) {
            BSConfListener listener = (BSConfListener) e.nextElement();
            listener.setMyJID(roomJID, myJID);
        }
    }
}
