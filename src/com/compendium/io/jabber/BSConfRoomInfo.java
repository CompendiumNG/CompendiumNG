package com.compendium.io.jabber;

/*
 * BSConfRoomInfo.java
 *
 * Project: BuddySpace
 * (C) Copyright Knowledge Media Institute 2002
 *
 *
 * Created on 13 September 2002, 10:17
 */

import java.util.*;

import org.jabber.jabberbeans.util.*;

/**
 * <code>BSConfRoomInfo</code> provides information about a conference room.
 * In includes hashtable of nicks, room name and JID, myPresence, etc.
 *
 * @author  Jiri Komzak, Knowledge Media Institute, Open University, United Kingdom
 */
public class BSConfRoomInfo {
    public JID roomJID;
    public String name;
    public Hashtable nicks;
    public String servedID;
    public int state;
    public BSPresenceInfo myPresence;

    public static int NOT_LOGGED    = 0;
    public static int LOGGING1      = 1;
    public static int LOGGING2      = 2;
    public static int LOGGED        = 3;
    public static int CHANGING_NICK = 4;

    /** Constructor */
    public BSConfRoomInfo(JID roomJID) {
        this.roomJID = roomJID;
        name = null;
        nicks = new Hashtable();
        servedID = null;
        state = NOT_LOGGED;
        myPresence = new BSPresenceInfo(null, false, null, null);
    }

    /** Sets my presence in room */
    public void setMyPresence(boolean available, String show, String status) {
        myPresence = new BSPresenceInfo(myPresence.getJID(), available, show, status);
    }

    /** Sets room state (while joining) */
    public void setState(int state) {
        this.state = state;
    }

    /** Sets room name */
    public void setRoomName(String name) {
        this.name = name;
    }

    /** Sets my JID in room */
    public void setMyJID(JID myJID) {
        myPresence.setJID(myJID);
    }
}
