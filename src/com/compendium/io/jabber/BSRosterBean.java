package com.compendium.io.jabber;

/*
 * BSRosterBean.java
 *
 * Project: BuddySpace
 * (C) Copyright Knowledge Media Institute 2002
 *
 *
 * Created on 17 July 2002, 11:03
 */

import java.util.*;
import org.jabber.jabberbeans.*;
import org.jabber.jabberbeans.util.*;
import org.jabber.jabberbeans.Extension.*;

/**
 * <code>BSRosterBean</code> provides roster handling.
 * It relies on <code>BSInfoQueryBean</code>, which must be set after each
 * reconnection. It uses <code>RosterBean</code>.
 *
 * @author  Jiri Komzak, Knowledge Media Institute, Open University, United Kingdom
 */
public class BSRosterBean implements RosterListener, ConnectionListener {
    private Hashtable curRoster;
    private RosterBean rosterBean = null;
    private final String name = "Roster";
    private Vector rosterListeners;
    
    /**
     * Constructor
     */
    BSRosterBean() {
        curRoster = new Hashtable();
        rosterListeners = new Vector();
    }
    
    /**
     * Constructor, which sets existing and connected <code>IQBean</code>.
     * Then <code>RosterBean</code> is created and this is registered
     * as listener for connection and roster event.
     */
    BSRosterBean(IQBean iqBean) {
        curRoster = new Hashtable();
        rosterListeners = new Vector();
        setIQBean(iqBean);
    }
    
    /**
     * Sets existing and connected <code>IQBean</code>. 
     * Then <code>RosterBean</code> is created and this is registered
     * as listener for connection and roster event.
     */
    protected void setIQBean(IQBean iqBean) {
        if (iqBean != null) {
            rosterBean = new RosterBean();
            rosterBean.setIQBean(iqBean);
            rosterBean.addRosterListener(this);
            ConnectionBean connection = iqBean.getConnection();
            if (connection != null) 
                connection.addConnectionListener(this);
        }
        curRoster.clear();
    }

    /**
     * Returns currently used <code>IQBean</code>.
     */
    protected IQBean getIQBean() {
        return (rosterBean != null)? rosterBean.getIQBean() : null;
    }
    
    /**
     * Returns currently used <code>RosterBean</code>.
     */
    protected RosterBean getRosterBean() {
        return rosterBean;
    }
    
    /**
     * Returns <code>Enumeration</code> of roster entries. The entries are of
     * <code>RosterItem</code> type.
     */
    public Enumeration entries() {
        return curRoster.elements();
    }
    
    /**
     * Frees all object bindings to allow object destroy
     */
    protected void prepareToDestroy() {
        removeAllRosterListeners();
        rosterBean = null;
    }
    
    /**
     * Sends request to server for roster refresh.
     * After the refreshed roster comes <code>replacedRoster</code>
     * is invoked.
     */
    public void refreshRoster() {
        if (rosterBean == null) {
            BSCore.logEvent(name, "error: not connected");
            return;
        }
        try {
            rosterBean.refreshRoster();
        } catch (InstantiationException e) {
            BSCore.logEvent(name, "error: roster refresh failed");
        }
    }
    
    /**
     * Clears the current roster.
     * This function is typically called after disconnecting.
     */
    public void clear() {
        curRoster.clear();
        try {
            RosterBuilder rb = new RosterBuilder();
            fireReplacedRoster(new Roster(rb));
        } catch(InstantiationException e) {}
    }
    
    /**
     * Adds contact into the roster. Returns if the request was successfuly
     * sent to server. But still the server can return error as a response.
     */
    public boolean addContact(JID jid, String nick, String group) {
        if (jid == null || rosterBean == null)
            return false;
        
        RosterItemBuilder rib = new RosterItemBuilder();
        
        // sets the group
        if (group != null && !group.equals(""))
            rib.addGroup(group);
        
        // checks if the jid is already in some other group
        String str = getRosterItemHashString(jid, "", false);
        RosterItem old = (RosterItem) curRoster.get(str);
        if (old != null) {
            Enumeration groupEnum = old.enumerateGroups();
            while (groupEnum.hasMoreElements()) {
                String gr = (String) groupEnum.nextElement();
                rib.addGroup(gr);
            }
        }
        
        // sets the nick
        if (nick != null && !nick.equals(""))
            rib.setFriendlyName(nick);
        else rib.setFriendlyName(jid.toString());
        
        // sets JID
        rib.setJID(jid);
        
        try {
            rosterBean.addRosterItem(rib.build());
            return true;
        } catch (InstantiationException e) {
            return false;
        }
    }
    
    /**
     * Changes contact in roster. Returns if the request was successfuly
     * sent to server. But still the server can return error as a response.
     * When the group or oldGroup is null, it means that no group is used
     * (the contact is not in any group).
     */
    public boolean changeContact(JID jid, String nick, String group, String oldGroup) {
        if (jid == null || rosterBean == null)
            return false;
        
        RosterItemBuilder rib = new RosterItemBuilder();
        
        // goes through groups and changes the right one
        boolean changed = false;
        String str = getRosterItemHashString(jid, "", false);
        RosterItem old = (RosterItem) curRoster.get(str);
        if (old != null) {
            Enumeration groupEnum = old.enumerateGroups();
            // if there was no group before
            if (!groupEnum.hasMoreElements() && oldGroup == null) {
                rib.addGroup(group);
                changed = true;
            }
            // if there were groups
            while (groupEnum.hasMoreElements()) {
                String gr = (String) groupEnum.nextElement();
                if (!changed && gr.equals(oldGroup)) {
                    // if there is a new group
                    if (group != null)
                        rib.addGroup(group);
                    changed = true;
                }
                else {
                    rib.addGroup(gr);
                }
            }
        }
        else {
            // sets the group
            if (group != null && !group.equals(""))
                rib.addGroup(group);
        }
        
        // sets the nick
        if (nick != null && !nick.equals(""))
            rib.setFriendlyName(nick);
        else rib.setFriendlyName(jid.toString());
        
        // sets JID
        rib.setJID(jid);
        
        try {
            rosterBean.addRosterItem(rib.build());
            return true;
        } catch (InstantiationException e) {
            return false;
        }
    }
    
    /**
     * Deletes contact with JID from given group in the roster.
     * Returns if the request was successfuly
     * sent to server. But still the server can return error as a response.
     */
    public boolean deleteJIDFromGroup(JID jid, String group) {
        if (jid == null || rosterBean == null || group == null)
            return false;
        
        RosterItemBuilder rib = new RosterItemBuilder();
        
        String str = getRosterItemHashString(jid, "", false);
        RosterItem oldItem = (RosterItem) curRoster.get(str);
        
        rib.copyItem(oldItem);
        Vector groupsVec = rib.getGroups();
        boolean changed = groupsVec.remove(group);
        if (groupsVec.size() == 0) {
            try {
                rosterBean.delRosterItem(rib.build());
                return true;
            } catch (InstantiationException e) {
                return false;
            }
        }
        
        try {
            rosterBean.addRosterItem(rib.build());
            return true;
        } catch (InstantiationException e) {
            return false;
        }
    }
    
    /**
     * Returns true if there is a contact in roster with given JID.
     */
    public boolean isJIDInRoster(JID jid) {
        String str = getRosterItemHashString(jid, "", false);
        RosterItem old = (RosterItem) curRoster.get(str);
        return old != null;
    }
    
    /**
     * Returns true if there is subscription to presence of given JID.
     */
    public boolean isSubscriptionToJID(JID jid) {
        String str = getRosterItemHashString(jid, "", false);
        RosterItem old = (RosterItem) curRoster.get(str);
        if (old == null) return false;
        String subscr = old.getSubscriptionType();
        return (subscr != null && (subscr.equals("to") || subscr.equals("both")));
    }
    
    /**
     * Returns <code>Iterator</code> over all groups in roster.
     */
    public Iterator getGroups() {
        Enumeration contacts = curRoster.elements();
        //Hashtable groups = new Hashtable();
        TreeSet groups = new TreeSet();
        while (contacts.hasMoreElements()) {
            RosterItem ri = (RosterItem) contacts.nextElement();
            Enumeration itemGroups = ri.enumerateGroups();
            // for all groups of roster element
            while (itemGroups != null && itemGroups.hasMoreElements()) {
                String gr = (String) itemGroups.nextElement();
                groups.add(gr);
            }
        }
        return groups.iterator();
    }
    
    /**
     * Returns <code>Enumeration</code> over all JIDs in given group.
     */
    public Enumeration getJIDsInGroup(String groupName) {
        Vector groupJids = new Vector();
        
        Enumeration contacts = curRoster.elements();
        while (contacts.hasMoreElements()) {
            RosterItem ri = (RosterItem) contacts.nextElement();
            Enumeration itemGroups = ri.enumerateGroups();
            
            // for all groups of roster element
            while (itemGroups != null && itemGroups.hasMoreElements()) {
                String gr = (String) itemGroups.nextElement();
                
                if (((groupName == null || groupName.equals("")) && gr == null)
                    || (groupName != null && groupName.equals(gr))) {
                    JID j = ri.getJID();
                    
                    if (!groupJids.contains(j))
                        groupJids.add(j);
                }
            }
        }
        
        return groupJids.elements();
    }

    // *** help functions ***
    
    /**
     * Returns string used as a key in roster hashtable.
     */
    public String getRosterItemHashString(JID jid, String group, boolean useGroup) {
        if (jid == null)
            return (group != null)? group : "";
        
        String res = new String((useGroup? group + "/" : "")
                                + jid.getUsername() + "@" + jid.getServer());
        return res;
    }

    // *** roster events ***
    
    /**
     * The changedRoster event is fired when there has been a change to the
     * roster state.
     */
    public void changedRoster(Roster roster) {
        boolean changed = false;
        BSCore.logEvent(name, "changed roster");
        Enumeration items = roster.items();
        if (items != null && items.hasMoreElements())
            changed = true;
        // for all items
        while (items != null && items.hasMoreElements()) {
            RosterItem i = (RosterItem) items.nextElement();
            //RosterItem old = (RosterItem) currentRoster.put(i.getJID(), i);
            RosterItem old = (RosterItem) curRoster.put(getRosterItemHashString(i.getJID(), "", false), i);
            if (old != null) {
                BSCore.logEvent(name, new String(old.getJID().toString() + " removed"));
                BSCore.logEvent(name, "=> refreshing roster");
                refreshRoster();
                changed = false;
            }
        }
        if (changed) fireChangedRoster(roster);
        //showRoster();
    }

    /**
     * The replacedRoster event is fired when a completely new roster has
     * replaced the existing one.
     */
    public void replacedRoster(Roster roster) {
        BSCore.logEvent(name, "replaced roster");
        curRoster.clear();
        Enumeration items = roster.items();
        // for all items
        while (items != null && items.hasMoreElements()) {
            RosterItem i = (RosterItem) items.nextElement();
            //curRoster.put(i.getJID(), i);
            curRoster.put(getRosterItemHashString(i.getJID(), "", false), i);
        }
        fireReplacedRoster(roster);
        //showRoster();
    }
    
    /**
     * Returns friendly name of the <code>JID</code> according to appropriate
     * <code>RosterItem</code>.
     */
    public String getFriendlyName(JID jid) {
        if (jid == null) return null;
        
        String str = getRosterItemHashString(jid, "", false);
        RosterItem item = (RosterItem) curRoster.get(str);
        
        if (item == null) return null;
        
        return item.getFriendlyName();
    }
    
    // *** roster listeners ***
    
    /**
     * Adds <code>RosterListener</code> from listeners notified when
     * roster changes.
     *
     * @see #removeRosterListener
     * @see #removeAllRosterListeners
     * @see #fireChangedRoster
     * @see #fireReplacedRoster
     */
    public void addRosterListener(RosterListener listener) {
        //assert listener != null : listener;
        if (listener == null) return;
        if (!rosterListeners.contains(listener)) {
            rosterListeners.addElement(listener);
        }
    }

    /**
     * Removes <code>RosterListener</code> from listeners notified when
     * roster changes.
     *
     * @see #addRosterListener
     * @see #removeAllRosterListeners
     * @see #fireChangedRoster
     * @see #fireReplacedRoster
     */
    public void removeRosterListener(RosterListener listener) {
        //assert listener != null : listener;
        if (listener == null) return;
        rosterListeners.removeElement(listener);
    }

    /**
     * Removes all listeners notified when roster changes.
     * This can be used before to free dependencies and allow dispose of
     * all objects.
     *
     * @see #addRosterListener
     * @see #removeRosterListener
     * @see #fireChangedRoster
     * @see #fireReplacedRoster
     */
    public void removeAllRosterListeners() {
        rosterListeners.clear();
    }

    /**
     * Notifies roster listeners about roster change.
     *
     * @see #addRosterListener
     * @see #removeRosterListener
     * @see #removeAllRosterListeners
     * @see #fireReplacedRoster
     */
    private void fireChangedRoster(Roster roster) {
        for (Enumeration e = rosterListeners.elements(); e.hasMoreElements(); ) {
            RosterListener listener = (RosterListener) e.nextElement();
            listener.changedRoster(roster);
        }
    }
    
    /**
     * Notifies roster listeners about roster replace.
     *
     * @see #addRosterListener
     * @see #removeRosterListener
     * @see #removeAllRosterListeners
     * @see #fireChangedRoster
     */
    private void fireReplacedRoster(Roster roster) {
        for (Enumeration e = rosterListeners.elements(); e.hasMoreElements(); ) {
            RosterListener listener = (RosterListener) e.nextElement();
            listener.replacedRoster(roster);
        }
    }
    
    /**
     * Invoked when connection changes. 
     * If disconnected, clears the roster and fires its change.
     */
    public void connectionChanged(ConnectionEvent ce) {
        ConnectionEvent.EState connState = ce.getState();
        if (connState != ConnectionEvent.STATE_CONNECTED) {
            curRoster.clear();
            RosterBuilder rb = new RosterBuilder();
            try {
                Roster r = new Roster(rb);
                fireReplacedRoster(new Roster(rb));
            } catch (java.lang.InstantiationException e) {
                return;
            }
        }
    }
    
}
