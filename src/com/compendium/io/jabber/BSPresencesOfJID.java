package com.compendium.io.jabber;

/*
 * BSPresencesOfJID.java
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
 * <code>BSPresencesOfJID</code> contains presence information
 * about all resources the given <code>JID</code> is logged from.
 * It includes list <code>BSPresenceInfo</code> records.
 *
 * @author  Jiri Komzak, Knowledge Media Institute, Open University, United Kingdom
 */
public class BSPresencesOfJID {
    protected Hashtable presences = null;
    protected JID jid = null;
    
    /**
     * Constructs <code>BSPresencesOfJID</code> from <code>Presence</code>
     * packet.
     */
    public BSPresencesOfJID(Presence p) {
        presences = new Hashtable();
        
        if (p == null) return;
        
        JID jid = p.getFromAddress();
        if (jid == null) return;
        
        this.jid = BSPresenceBean.getJidWithoutRes(jid);
        
        setPresence(p);
    }
    
    /**
     * Constructs <code>BSPresencesOfJID</code>.
     */
    public BSPresencesOfJID(BSPresenceInfo pi) {
        presences = new Hashtable();
        
        JID jid;
        if (pi == null || (jid = pi.getJID()) == null) return;
        
        this.jid = BSPresenceBean.getJidWithoutRes(jid);
        
        setPresence(pi);
    }

    /**
     * Changes presence of <code>JID</code> according to given <code>Presence</code>
     * packet.
     */
    public void changePresence(Presence p) {
        if (p == null) return;
        
        JID jid = p.getFromAddress();
        if (jid == null || 
            !this.jid.equals(BSPresenceBean.getJidWithoutRes(jid))) return;
        
        setPresence(p);
    }
    
    /**
     * Changes presence of <code>JID</code> according to given <code>Presence</code>
     * packet.
     */
    public void changePresence(BSPresenceInfo pi) {
        if (pi == null || jid == null || 
            !jid.equals(BSPresenceBean.getJidWithoutRes(pi.getJID()))) return;
        
        setPresence(pi);
    }
    
    /**
     * Sets presence according to given <code>Presence</code> packet.
     * This is internal function and doesn't test JID,...
     */
    protected void setPresence(Presence p) {
        JID jidTmp = p.getFromAddress();
        String type = p.getType();
        
        String resource = jidTmp.getResource();
        if (resource == null) 
            resource = new String("no resource");
        
        if (type != null && type.equals("unavailable")) {
            presences.remove(resource);
        }
        else {
            BSPresenceInfo pi = new BSPresenceInfo(p);
            presences.put(resource, pi);
        }
    }
    
    /**
     * Sets presence.
     * This is internal function and doesn't test JID,...
     */
    protected void setPresence(BSPresenceInfo pi) {
        
        JID jid;
        if (pi == null || (jid = pi.getJID()) == null) return;
        
        String resource = jid.getResource();
        if (resource == null) 
            resource = new String("no resource");
        
        if (!pi.isOnline()) {
            presences.remove(resource);
        }
        else {
            presences.put(resource, pi);
        }
    }
    
    /**
     * Returns true if the <code>JID</code> is available at least from one
     * resource. It means if the hashtable is not empty.
     */
    public boolean isAvailable() {
        return !presences.isEmpty();
    }
    
    /**
     * Returns the "most online" <code>BSPresenceInfo</code>.
     */
    public BSPresenceInfo getBestPresence() {
        
        if (presences.isEmpty())
            return null;
        
        Enumeration e = presences.elements();
        BSPresenceInfo pi = null;
        
        int bestPresence = 0;
        while (e != null && e.hasMoreElements()) {
            BSPresenceInfo curPi = (BSPresenceInfo) e.nextElement();
            int curPresence;
            if (BSPresenceInfo.SHOW_XA.equals(curPi.getShow()))
                curPresence = 1;
            else if (BSPresenceInfo.SHOW_AWAY.equals(curPi.getShow()))
                curPresence = 2;
            else if (BSPresenceInfo.SHOW_DND.equals(curPi.getShow()))
                curPresence = 3;
            else if (BSPresenceInfo.SHOW_CHAT.equals(curPi.getShow()))
                curPresence = 5;
            else
                curPresence = 4;
            
            if (curPresence > bestPresence) {
                pi = curPi;
                bestPresence = curPresence;
            }
        }
        
        return pi;
    }
    
    /**
     * Compares the given presences and returns the one "more online".
     */
    public static BSPresenceInfo getBetterPresence(BSPresenceInfo p1, BSPresenceInfo p2) {
        
        if (p1 == null || !p1.isOnline()) return p2;
        if (p2 == null || !p2.isOnline()) return p1;
        
        int p1Pr, p2Pr;
        
        if (BSPresenceInfo.SHOW_XA.equals(p1.getShow())) p1Pr = 1;
        else if (BSPresenceInfo.SHOW_AWAY.equals(p1.getShow())) p1Pr = 2;
        else if (BSPresenceInfo.SHOW_DND.equals(p1.getShow())) p1Pr = 3;
        else if (BSPresenceInfo.SHOW_CHAT.equals(p1.getShow())) p1Pr = 5;
        else p1Pr = 4;
        
        if (BSPresenceInfo.SHOW_XA.equals(p2.getShow())) p2Pr = 1;
        else if (BSPresenceInfo.SHOW_AWAY.equals(p2.getShow())) p2Pr = 2;
        else if (BSPresenceInfo.SHOW_DND.equals(p2.getShow())) p2Pr = 3;
        else if (BSPresenceInfo.SHOW_CHAT.equals(p2.getShow())) p2Pr = 5;
        else p2Pr = 4;
        
        if (p1Pr > p2Pr) return p1;
        else return p2;
    }
    
    /**
     * Returns <code>BSPresenceInfo</code> of JID. If the JID has resource,
     * returns presence for the resource. Otherwise returns the best presence.
     */
    public BSPresenceInfo getJIDPresence(JID jid) {
        if (jid == null || 
            !this.jid.equals(BSPresenceBean.getJidWithoutRes(jid)) ||
            presences.isEmpty())
            return null;
        
        String resource = jid.getResource();
        if (resource == null || resource.equals(""))
            return getBestPresence();
        
        BSPresenceInfo result = (BSPresenceInfo) presences.get(resource);
        return result;
    }
    
    /**
     * Returns <code>Enumeration</code> of all <code>BSPresenceInfo</code>s
     * which are available. If the JID is not available, returns null.
     */
    public Enumeration getAllPresences() {
        return presences.elements();
    }
    
}
