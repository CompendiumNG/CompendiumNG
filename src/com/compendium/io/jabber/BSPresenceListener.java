package com.compendium.io.jabber;

/*
 * BSPresenceListener.java
 *
 * Project: BuddySpace
 * (C) Copyright Knowledge Media Institute 2002
 *
 *
 * Created on 18 July 2002, 16:10
 */

//import java.util.EventListener;
import org.jabber.jabberbeans.util.JID;

/**
 * <code>BSPresenceListener</code> is interface you can implement to get
 * presence changes notifications.
 *
 * @author  Jiri Komzak, Knowledge Media Institute, Open University, United Kingdom
 */
public interface BSPresenceListener {//extends EventListener {
    /** Called then presence is changed */
    public void presenceChanged(BSPresenceInfo pi);
    /** Called when subscription request was received */
    public void subscriptionRequested(JID jid);
    /** Called when subscription request was approved */
    public void subscriptionApproved(JID jid);
    /** Called after disconnecting - no presence info is available */
    public void presencesCleared();
}
