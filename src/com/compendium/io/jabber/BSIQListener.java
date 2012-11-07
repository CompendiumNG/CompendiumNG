package com.compendium.io.jabber;

/*
 * BSIQListener.java
 *
 * Project: BuddySpace
 * (C) Copyright Knowledge Media Institute 2002
 *
 *
 * Created on 23 August 2002, 15:32
 */

//import java.util.EventListener;
import org.jabber.jabberbeans.util.JID;

/**
 * <code>BSIQListener</code> is interface you can implement to get
 * special iq events notifications.
 *
 * @author  Jiri Komzak, Knowledge Media Institute, Open University, United Kingdom
 */
public interface BSIQListener { //extends EventListener {
    /** Called when a IQ with OOB is received */
    public void oobReceived(String url, JID jid);
}
