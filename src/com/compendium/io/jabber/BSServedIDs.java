package com.compendium.io.jabber;

/*
 * BSServedIDs.java
 *
 * Project: BuddySpace
 * (C) Copyright Knowledge Media Institute 2002
 *
 *
 * Created on 25 November 2002, 8:27
 */

import java.util.*;
import org.jabber.jabberbeans.*;
//import org.jabber.jabberbeans.util.*;
//import org.jabber.jabberbeans.Extension.*;

/**
 * <code>BSServedIDs</code> provides storing of currently served PacketIDs.
 *
 * @author  Jiri Komzak, Knowledge Media Institute, Open University, United Kingdom
 */
public class BSServedIDs {
    protected Vector ids;
    
    /**
     * Constructor
     */
    public BSServedIDs() {
        ids = new Vector();
    }
    
    /**
     * Returns if given ID is served.
     */
    public boolean contains(String id) {
        Enumeration idEnum = ids.elements();
        while (idEnum.hasMoreElements()) {
            PacketID packetID = (PacketID) idEnum.nextElement();
            if ((id == null && packetID.getID() == null) ||
                (id != null && id.equals(packetID.getID())))
                
                return true;
        }
        return false;
    }
    
    /**
     * Adds ID.
     */
    public void add(PacketID id) {
        ids.add(id);
    }
    
    /**
     * Removes ID.
     */
    public void remove(String id) {
        Enumeration idEnum = ids.elements();
        while (idEnum.hasMoreElements()) {
            PacketID packetID = (PacketID) idEnum.nextElement();
            if ((id == null && packetID.getID() == null) ||
                (id != null && id.equals(packetID.getID()))) {
                    
                ids.remove(packetID);
                return;
            }
        }
    }
}
