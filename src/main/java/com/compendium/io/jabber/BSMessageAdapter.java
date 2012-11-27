package com.compendium.io.jabber;

/*
 * BSMessageAdapter.java
 *
 * Project: BuddySpace
 * (C) Copyright Knowledge Media Institute 2003
 *
 *
 * Created on 18 February 2003, 11:18
 */

//import java.util.EventListener;
import org.jabber.jabberbeans.util.JID;
import org.jabber.jabberbeans.*;

/**
 * <code>BSMessageAdapter</code> empty implementation of
 * <code>BSMessageListener</code> interface providing
 * message events notifications.
 *
 * @author  Jiri Komzak, Knowledge Media Institute, Open University, United Kingdom
 */
public class BSMessageAdapter implements BSMessageListener {
    /** Called when message is received */
    //public void messageReceived(JID fromAddress);
    /** Called when message is received */
    public void plainMessageReceived(Message msg){}
    /** Called when message is received */
    public void chatMessageReceived(Message msg){}
    /** Called when message is received */
    public void groupchatMessageReceived(Message msg){}
    /** Called when message is received */
    public void headlineMessageReceived(Message msg){}
    /** Called when message is read (and removed from bean) */
    //public void messageRead(JID fromAddress);
    /** Called when message is read (and removed from bean) */
    public void messageRead(Message msg){}
    /** Called when message error is received */
    public void messageError(JID toAddress, String errType, String error){}
    /** Called when message error is received */
    public void messageError(Message msg){}
}
