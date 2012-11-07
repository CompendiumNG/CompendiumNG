package com.compendium.io.jabber;

/*
 * BSConfListener.java
 *
 * Project: BuddySpace
 * (C) Copyright Knowledge Media Institute 2002
 *
 *
 * Created on 12 September 2002, 11:15
 */

//import java.util.EventListener;
//import edu.ou.kmi.buddyspace.xml.*;

import org.jabber.jabberbeans.util.*;

/**
 * <code>BSConfListener</code> is interface you can implement to get
 * conference events notifications.
 *
 * @author  Jiri Komzak, Knowledge Media Institute, Open University, United Kingdom
 */
public interface BSConfListener {//extends EventListener {
    /** Called when message from room arrived */
    public void roomMessage(JID roomJID, String body);
    /** Called when groupchat message arrived */
    public void groupMessage(JID fromAddress, String nick, String body);
    /** Called when private message arrived */
    public void privateMessage(JID fromAddress, String nick, String body);
    /** Called when invitation into a room arrived */
    public void invitation(JID fromAddress, JID roomJID, String subject, String body);
    /** Called when presence of a nick in a room has changed */
    public void presenceChanged(JID fromAddress, String nick, BSPresenceInfo pi);
    /** Called when state of room changed */
    public void stateChanged(JID roomJID, int state);
    /** Called when an error occured */
    public void error(JID roomJID, String errCode, String errMsg);
    /** Called when myJID is set */
    public void setMyJID(JID roomJID, JID myJID);
}
