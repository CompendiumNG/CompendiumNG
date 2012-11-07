package com.compendium.io.jabber;

/*
 * BSPresenceAdapter.java
 *
 * Project: BuddySpace
 * (C) Copyright Knowledge Media Institute 2003
 *
 *
 * Created on 18 February 2003, 11:12
 */

//import java.util.EventListener;
import org.jabber.jabberbeans.util.JID;

/**
 * <code>BSPresenceAdapter</code> empty implementation of <code>BSPresenceListener</code>
 * interface for presence changes notifications.
 *
 * @author  Jiri Komzak, Knowledge Media Institute, Open University, United Kingdom
 */
public class BSPresenceAdapter implements BSPresenceListener {
    /** Called then presence is changed */
    public void presenceChanged(BSPresenceInfo pi) {}
    /** Called when subscription request was received */
    public void subscriptionRequested(JID jid) {}
    /** Called when subscription request was approved */
    public void subscriptionApproved(JID jid) {}
    /** Called after disconnecting - no presence info is available */
    public void presencesCleared() {}
}
