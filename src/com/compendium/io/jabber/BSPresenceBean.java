package com.compendium.io.jabber;

/*
 * BSPresenceBean.java
 *
 * Project: BuddySpace
 * (C) Copyright Knowledge Media Institute 2002
 *
 *
 * Created on 18 July 2002, 12:03
 */

import java.util.*;
import org.jabber.jabberbeans.*;
import org.jabber.jabberbeans.util.*;
//import org.jabber.jabberbeans.Extension.*;

/**
 * <code>BSPresenceBean</code> provides presence handling.
 * It relies on <code>BSConnectionBean</code>, which must be set after each
 * reconnection.
 *
 * @author  Jiri Komzak, Knowledge Media Institute, Open University, United Kingdom
 */
public class BSPresenceBean implements PacketListener, ConnectionListener {
    protected Hashtable curPresences;
    protected ConnectionBean connection = null;
    protected final String name = "Presence";
    protected Vector presenceListeners;
    protected JID myJID = null;
    protected boolean connected = false;

    /**
     * Constructor
     */
    BSPresenceBean() {
        curPresences = new Hashtable();
        presenceListeners = new Vector();
    }

    /**
     * Constructor, which sets existing and connected <code>ConnectionBean</code>.
     * Then this is registered as listener for packet events.
     */
    BSPresenceBean(ConnectionBean connection) {
        this();
        setConnection(connection);
    }

    /**
     * Sets existing and connected <code>ConnectionBean</code>.
     * Then this is registered as listener for packet and connection events.
     */
    protected void setConnection(ConnectionBean connection) {
        if (this.connection != null) {
            this.connection.delPacketListener(this);
            this.connection.delConnectionListener(this);
        }
        this.connection = connection;
        if (connection != null) {
            connection.addPacketListener(this);
            connection.addConnectionListener(this);
        }
        curPresences.clear();
    }

    /**
     * Returns currently used <code>ConnectionBean</code>.
     */
    protected ConnectionBean getConnection() {
        return connection;
    }

    /**
     * Returns if connected.
     */
    public boolean isConnected() {
        return connected;
    }

    /**
     * Returns <code>Enumeration</code> of current presences.
     * Items are of <code>BSPresenceInfo</code> type. If the JID is not
     * contained in this, it is not available.
     */
    public Enumeration entries() {
        return curPresences.elements();
    }

    /**
     * Stores given presence info.
     * This method should be used for access to presence storage.
     */
    protected void storePresence(BSPresenceInfo pi) {

        JID jid;
        if (pi == null || (jid = pi.getJID()) == null)
            return;

        String str = getJIDHashString(jid, false);
        BSPresencesOfJID jp = getJIDPresences(jid);

        // if no record yet and is available
        if (jp == null && pi.isOnline()) {
            jp = new BSPresencesOfJID(pi);
            curPresences.put(str, jp);
        }
        // if there is already a record
        else {
            // changes the presence
            jp.changePresence(pi);
            // if got totally unavailable
            if (!jp.isAvailable())
                curPresences.remove(str);
        }
    }

    /**
     * Sets current JID.
     */
    public void setMyJID(JID jid) {
        myJID = jid;
    }

    /**
     * Sets current presence and sends it to server.
     */
    public void setMyPresence(boolean available, String show,
                              String status, int priority) {

        //if (myJID == null) return;
        BSPresenceInfo pi = new BSPresenceInfo(myJID, available, show, status);
        pi.setPriority(priority);
        pi.setIsMyself(true);
        storePresence(pi);
        firePresenceChanged(pi);
        sendPresence(pi, null);
    }

    /**
     * Sets <code>jid</code>'s current presence inside this bean.
     * This doesn't send anything to server, just sets internal value
     * (for internal hacks like conferencing,...)
     */
    public void setPresence(JID jid, boolean available, String show,
                            String status, int priority) {

        BSPresenceInfo pi = new BSPresenceInfo(jid, available, show, status);
        pi.setPriority(priority);

        storePresence(pi);

        firePresenceChanged(pi);
    }

    /**
     * Sends given presence to given JID
     */
    public void sendPresence(BSPresenceInfo pi, JID toAddress) {
        if (connection == null || pi == null) return;

        try {
            Presence p = pi.getPresencePacket(toAddress);
            connection.send(p);
        } catch (InstantiationException e) {
            BSCore.logEvent(name, "presence builder failed\n");
        }
    }

    /**
     * Sends subscription request to given JID.
     */
    public void sendSubscriptionRequest(JID jid) {
        if (connection == null || jid == null) return;

        PresenceBuilder presenceBuilder = new PresenceBuilder();
        presenceBuilder.setToAddress(jid);
        presenceBuilder.setType("subscribe");

        try {
            connection.send(presenceBuilder.build());
        } catch (InstantiationException e) {
            BSCore.logEvent(name, "presence builder failed\n");
        }
    }

    /**
     * Sends subscription confirmation to given JID.
     */
    public void sendSubscriptionApproved(JID jid) {
        if (connection == null || jid == null) return;

        PresenceBuilder presenceBuilder = new PresenceBuilder();
        presenceBuilder.setToAddress(jid);
        presenceBuilder.setType("subscribed");

        try {
            connection.send(presenceBuilder.build());
        } catch (InstantiationException e) {
            BSCore.logEvent(name, "presence builder failed\n");
        }
    }

    /**
     * Returns <code>BSPresencesOfJID</code> for given JID.
     */
    protected BSPresencesOfJID getJIDPresences(JID jid) {
        // gets list of presences for jid without resource
        String str = getJIDHashString(jid, false);
        return (BSPresencesOfJID) curPresences.get(str);
    }

    /**
     * Returns <code>BSPresenceInfo</code> for specified JID without resource.
     * The "best" presence of all resources is returned.
     */
    public BSPresenceInfo getPresence(JID jid) {
        if (jid == null)
            return null;

        // gets list of presences for jid without resource
        BSPresencesOfJID jp = getJIDPresences(jid);
        BSPresenceInfo pi;

        // no list for jid
        if (jp == null) {
            pi = null;
        }
        // else some list, gets the best presence
        else {
            pi = jp.getBestPresence();
        }

        // if that's my JID and my resource
        JID jidWORes = getJidWithoutRes(jid);
        JID myJIDWORes = getJidWithoutRes(myJID);
        if (myJIDWORes != null && myJIDWORes.equals(jidWORes)) {
            if (pi == null)
                pi = new BSPresenceInfo(myJID, false, null, null);
            else
                pi = new BSPresenceInfo(pi.getJID(), pi.isOnline(),
                                        pi.getShow(), pi.getStatus());
            pi.setIsMyself(true);
        }

        return pi;
    }

    /**
     * Returns <code>BSPresenceInfo</code> for specified JID with resource.
     * If resource is empty returns the best presence for JID withour resource.
     */
    public BSPresenceInfo getResourcePresence(JID jid) {
        if (jid == null)
            return null;

        // gets list of presences for jid without resource
        BSPresencesOfJID jp = getJIDPresences(jid);
        BSPresenceInfo pi;
        // if no list
        if (jp == null) {
            pi = null;
        }
        else {
            pi = jp.getJIDPresence(jid);
        }
        return pi;
    }

    /**
     * Returns Enumeration of <code>BSPresenceInfo</code>s
     * for all resources of specified JID.
     */
    public Enumeration getAllPresences(JID jid) {
        if (jid == null) {
            return (new Vector()).elements();
        }

        // gets list of presences for jid without resource
        BSPresencesOfJID jp = getJIDPresences(jid);
        // if no list
        if (jp == null) {
            return (new Vector()).elements();
        }
        else {
            return jp.getAllPresences();
        }
    }

    /**
     * Returns Enumeration of <code>BSPresenceInfo</code>s
     * for all my resources except this one.
     */
    public Enumeration getMyResourcesPresences() {
        if (myJID == null) {
            return (new Vector()).elements();
        }

        // gets list of presences for jid without resource
        BSPresencesOfJID jp = getJIDPresences(myJID);
        // if no list
        if (jp == null) {
            return (new Vector()).elements();
        }
        else {
            Enumeration enumeration = jp.getAllPresences();
            Vector v = new Vector();
            while (enumeration.hasMoreElements()) {
                BSPresenceInfo pi = (BSPresenceInfo) enumeration.nextElement();
                //if (!myJID.equals(pi.getJID()))
                    v.add(pi);
            }
            return v.elements();
        }
    }

    /**
     * Clears the current presences.
     * This function is typically called after disconnecting.
     */
    public void clear() {
        curPresences.clear();
        firePresencesCleared();
    }

    /**
     * Frees all object bindings to allow object destroy
     */
    protected void prepareToDestroy() {
        removeAllPresenceListeners();
        if (connection != null) {
            connection.delPacketListener(this);
            connection.delConnectionListener(this);
            connection = null;
        }
    }

    // *** HELPING METHODS ***

    /**
     * Returns string used as a key in roster hashtable.
     */
    public static String getJIDHashString(JID jid, boolean useResource) {
        if (jid == null)
            return "";

        String username = jid.getUsername();
        String result;
        if (username != null)
            result = new String(username + "@" + jid.getServer());
        else
            result = new String(jid.getServer());
        if (useResource && jid.getResource() != null)
            result = result + "/" + jid.getResource();
        return result;
    }

    /**
     * Returns JID without resource.
     */
    public static JID getJidWithoutRes(JID jid) {
        return (jid == null)? null :
                              new JID(jid.getUsername(), jid.getServer(), null);
    }

    // *** LISTENER METHODS ***

    /**
     * Invoked when a packet arrives.
     */
    public void receivedPacket(PacketEvent pe) {
        if (!(pe.getPacket() instanceof Presence)) {
            return;
        }

        Presence p = (Presence) pe.getPacket();
        String type = p.getType();
        JID jid = p.getFromAddress();

        // if JID is null
        if (jid == null)
            return;

        if (type != null && type.equals("subscribe")) {
            fireSubscriptionRequested(jid);
            return;
        }

        if (type != null && type.equals("subscribed")) {
            fireSubscriptionApproved(jid);
            return;
        }

        if (type != null && type.equals("error")) {
            firePresenceError(p);
            return;
        }

        if (type != null && !type.equals("available") &&
            !type.equals("") && !type.equals("unavailable"))
            return;

        // gets list of presences of jid without resource
        String str = this.getJIDHashString(jid, false);
        BSPresencesOfJID jp = getJIDPresences(jid);

        // if unavailable
        if (type != null && type.equals("unavailable")) {
            if (jp == null) return;
            // changes the presence
            jp.changePresence(p);
            // if got totally unavailable
            if (!jp.isAvailable())
                curPresences.remove(str);
        }

        // if no record yet and is not unavailable
        else if (jp == null && (type == null || !type.equals("unavailable"))) {
            jp = new BSPresencesOfJID(p);
            curPresences.put(str, jp);
        }

        // if there is already a record
        else {
            // changes the presence
            jp.changePresence(p);
            // if got totally unavailable
            if (!jp.isAvailable()) {
                curPresences.remove(str);
            }
        }

        firePresenceChanged(new BSPresenceInfo(p));
    }

    /**
     * Invoked when a packet send failes.
     */
    public void sendFailed(PacketEvent pe) {
    }

    /**
     * Invoked when a packet is sent.
     */
    public void sentPacket(PacketEvent pe) {
    }

    /**
     * Invoked when connection changes.
     * If disconnected, clears the presences and fires their change.
     */
    public void connectionChanged(ConnectionEvent ce) {
        ConnectionEvent.EState connState = ce.getState();
        if (connState != ConnectionEvent.STATE_CONNECTED) {
            myJID = null;
            connected = false;
            curPresences.clear();
            firePresencesCleared();
        }
        else
            connected = true;
    }

    // *** presence listeners ***

    /**
     * Adds <code>BSPresenceListener</code> to listeners notified when
     * presence state of some of buddies changes.
     *
     * @see #removePresenceListener
     * @see #removeAllPresenceListeners
     * @see #firePresenceChanged
     * @see #fireSubscriptionRequested
     * @see #fireSubscriptionApproved
     */
    public void addPresenceListener(BSPresenceListener listener) {
        //assert listener != null : listener;
        if (listener == null) return;
        if (!presenceListeners.contains(listener)) {
            presenceListeners.addElement(listener);
        }
    }

    /**
     * Removes <code>BSPresenceListener</code> to listeners notified when
     * presence state of some of buddies changes.
     *
     * @see #addPresenceListener
     * @see #removeAllPresenceListeners
     * @see #firePresenceChanged
     * @see #fireSubscriptionRequested
     * @see #fireSubscriptionApproved
     */
    public void removePresenceListener(BSPresenceListener listener) {
        //assert listener != null : listener;
        if (listener == null) return;
        presenceListeners.removeElement(listener);
    }

    /**
     * Removes all listeners notified when presence state of some of buddies changes.
     * This can be used before to free dependencies and allow dispose of
     * all objects.
     *
     * @see #addPresenceListener
     * @see #removePresenceListener
     * @see #firePresenceChanged
     * @see #fireSubscriptionRequested
     * @see #fireSubscriptionApproved
     */
    public void removeAllPresenceListeners() {
        presenceListeners.clear();
    }

    /**
     * Notifies presence listeners about presence state of some of buddies change.
     *
     * @see #addPresenceListener
     * @see #removePresenceListener
     * @see #removeAllPresenceListeners
     * @see #fireSubscriptionRequested
     * @see #fireSubscriptionApproved
     */
    private void firePresenceChanged(BSPresenceInfo pi) {
        for (Enumeration e = presenceListeners.elements(); e.hasMoreElements(); ) {
            BSPresenceListener listener = (BSPresenceListener) e.nextElement();
            listener.presenceChanged(pi);
        }
    }

    /**
     * Notifies presence listeners about presence subscription request.
     *
     * @see #addPresenceListener
     * @see #removePresenceListener
     * @see #removeAllPresenceListeners
     * @see #firePresenceChanged
     * @see #fireSubscriptionApproved
     */
    private void fireSubscriptionRequested(JID jid) {
        for (Enumeration e = presenceListeners.elements(); e.hasMoreElements(); ) {
            BSPresenceListener listener = (BSPresenceListener) e.nextElement();
            listener.subscriptionRequested(jid);
        }
    }

    /**
     * Notifies presence listeners about presence subscription confirmation.
     *
     * @see #addPresenceListener
     * @see #removePresenceListener
     * @see #removeAllPresenceListeners
     * @see #firePresenceChanged
     * @see #fireSubscriptionRequested
     */
    private void fireSubscriptionApproved(JID jid) {
        for (Enumeration e = presenceListeners.elements(); e.hasMoreElements(); ) {
            BSPresenceListener listener = (BSPresenceListener) e.nextElement();
            listener.subscriptionApproved(jid);
        }
    }

    /**
     * Notifies presence listeners about clearing of all presences.
     *
     * @see #addPresenceListener
     * @see #removePresenceListener
     * @see #removeAllPresenceListeners
     * @see #firePresenceChanged
     * @see #fireSubscriptionRequested
     */
    private void firePresencesCleared() {
        for (Enumeration e = presenceListeners.elements(); e.hasMoreElements(); ) {
            BSPresenceListener listener = (BSPresenceListener) e.nextElement();
            listener.presencesCleared();
        }
    }

    /**
     * Notifies presence listeners about presence error.
     *
     * @see #addPresenceListener
     * @see #removePresenceListener
     * @see #removeAllPresenceListeners
     * @see #firePresenceChanged
     * @see #fireSubscriptionRequested
     */
    private void firePresenceError(Presence p) {
        for (Enumeration e = presenceListeners.elements(); e.hasMoreElements(); ) {
            BSPresenceListener listener = (BSPresenceListener) e.nextElement();
            //listener.presenceError(p);
        }
    }

}
