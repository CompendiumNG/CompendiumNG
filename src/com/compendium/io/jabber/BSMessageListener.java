package com.compendium.io.jabber;

/*
 * BSMessageListener.java
 *
 * Project: BuddySpace
 * (C) Copyright Knowledge Media Institute 2002
 *
 *
 * Created on 19 July 2002, 16:00
 */

//import java.util.EventListener;
import org.jabber.jabberbeans.util.JID;
import org.jabber.jabberbeans.*;

/**
 * <code>BSMessageListener</code> is interface you can implement to get
 * message events notifications.
 *
 * @author  Jiri Komzak, Knowledge Media Institute, Open University, United Kingdom
 */
public interface BSMessageListener {//extends EventListener {
    /** Called when message is received */
    //public void messageReceived(JID fromAddress);
    /** Called when message is received */
    public void plainMessageReceived(Message msg);
    /** Called when message is received */
    public void chatMessageReceived(Message msg);
    /** Called when message is received */
    public void groupchatMessageReceived(Message msg);
    /** Called when message is received */
    public void headlineMessageReceived(Message msg);
    /** Called when message is read (and removed from bean) */
    //public void messageRead(JID fromAddress);
    /** Called when message is read (and removed from bean) */
    public void messageRead(Message msg);
    /** Called when message error is received */
    public void messageError(JID toAddress, String errType, String error);
    /** Called when message error is received */
    public void messageError(Message msg);
}
