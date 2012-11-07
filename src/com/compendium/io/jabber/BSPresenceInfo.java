package com.compendium.io.jabber;

/*
 * BSPresenceBean.java
 *
 * Project: BuddySpace
 * (C) Copyright Knowledge Media Institute 2002
 *
 *
 * Created on 18 July 2002, 15:32
 */

import java.util.*;
import org.jabber.jabberbeans.*;
import org.jabber.jabberbeans.util.*;
//import org.jabber.jabberbeans.Extension.*;

/**
 * <code>BSPresenceInfo</code> contains presence information.
 * It includes <code>JID</code>, availability, show and status information.
 *
 * @author  Jiri Komzak, Knowledge Media Institute, Open University, United Kingdom
 */
public class BSPresenceInfo {

    protected String show = null;
    protected String status = null;
    protected int priority = 0;
    protected JID jid = null;
    protected boolean available = false;
    protected boolean myself = false;
    protected boolean lurker = false;
    
    /** types */
    public final static String TYPE_AVAILABLE   = "available";
    public final static String TYPE_UNAVAILABLE = "unavailable";

    /** presence show constants */
    public final static String SHOW_ONLINE = "";
    public final static String SHOW_AWAY   = "away";
    public final static String SHOW_CHAT   = "chat";
    public final static String SHOW_DND    = "dnd";
    public final static String SHOW_XA     = "xa";
    
    /** constants for friendly show text */
    public static String FRIENDLY_SHOW_ONLINE  = "Online";
    public static String FRIENDLY_SHOW_CHAT    = "Free for chat";
    public static String FRIENDLY_SHOW_AWAY    = "Away";
    public static String FRIENDLY_SHOW_XA      = "Extended away";
    public static String FRIENDLY_SHOW_DND     = "Do not disturb";
    public static String FRIENDLY_SHOW_OFFLINE = "Offline";
    public static String FRIENDLY_SHOW_LURKER  = "Not in roster";
    //public static String FRIENDLY_SHOW_MYSELF  = "Myself";
    
    /**
     * Constructs <code>BSPresenceInfo</code> from <code>Presence</code>
     * packet.
     */
    public BSPresenceInfo(Presence p) {
        if (p == null) return;
        
        jid = p.getFromAddress();
        if (jid == null) return;
        String type = p.getType();
        
        available = (type == null || type.equals("") || type.equals(TYPE_AVAILABLE));
        if (available)
            show = p.getStateShow();
        status = p.getStatus();
    }

    /**
     * Constructor
     */
    public BSPresenceInfo(JID jid, boolean available, String show, String status) {
        this.jid = jid;
        this.available = available;
        if (SHOW_ONLINE.equals(show) || SHOW_CHAT.equals(show) ||
            SHOW_AWAY.equals(show)   || SHOW_XA.equals(show) ||
            SHOW_DND.equals(show))
            this.show = show;
        else
            this.show = null;
        this.status = status;
    }
    
    /** Returns new presence packet representing this. */
    public Presence getPresencePacket(JID toAddress) 
                    throws java.lang.InstantiationException {
                        
        PresenceBuilder pb = new PresenceBuilder();
        if (toAddress != null)
            pb.setToAddress(toAddress);
        if (available) {
            pb.setPriority(priority);
            if (show != null)
                pb.setStateShow(show);
            if (status != null)
                pb.setStatus(status);
        }
        else {
            pb.setType(TYPE_UNAVAILABLE);
            if (status != null)
                pb.setStatus(status);
        }
        return (Presence) pb.build();
    }
    
    /** Returns if buddy is available/on-line */
    public boolean isOnline() {
        return available;
    }
    
    /** Returns show */
    public String getShow() {
        return show;
    }
    
    /** Returns status */
    public String getStatus() {
        return status;
    }
    
    /** Returns friendly form of show for displaying */
    public String getFriendlyShow() {
        if (lurker && !available)
            return FRIENDLY_SHOW_LURKER;
        else if (!available)
            return FRIENDLY_SHOW_OFFLINE;
        else if (show == null || SHOW_ONLINE.equals(show))
            return FRIENDLY_SHOW_ONLINE;
        else if (SHOW_CHAT.equals(show))
            return FRIENDLY_SHOW_CHAT;
        else if (SHOW_AWAY.equals(show))
            return FRIENDLY_SHOW_AWAY;
        else if (SHOW_XA.equals(show))
            return FRIENDLY_SHOW_XA;
        else if (SHOW_DND.equals(show))
            return FRIENDLY_SHOW_DND;
        else
            return FRIENDLY_SHOW_LURKER;
    }
    
    /**
     * Sets if the buddy is in roster.
     * Typically used to set that the buddy is NOT in roster.
     */
    public void setIsInRoster(boolean inRoster) {
        lurker = !inRoster;
    }
    
    /**
     * Returns if the buddy is in roster.
     */
    public boolean isInRoster() {
        return !lurker;
    }
    
    /**
     * Sets if the buddy is myself it means the currently logged one.
     */
    public void setIsMyself(boolean isMyself) {
        myself = isMyself;
    }
    
    /**
     * Returns if the buddy is myself it means the currently logged one.
     */
    public boolean isMyself() {
        return myself;
    }
    
    /**
     * Sets priority level being set by this presence.
     */
    public void setPriority(int priority) {
        this.priority = priority;
    }
    
    /**
     * Returns priority level being set by this presence.
     */
    public int getPriority() {
        return priority;
    }
    
    /** Returns JID of the buddy */
    public JID getJID() {
        return jid;
    }
    
    /** Sets JID of the buddy */
    public void setJID(JID jid) {
        this.jid = jid;
    }
    
}
