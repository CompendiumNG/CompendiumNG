package com.compendium.io.jabber;

/*
 * BSAgentsListener.java
 *
 * Project: BuddySpace
 * (C) Copyright Knowledge Media Institute 2002
 *
 *
 * Created on 7 August 2002, 10:44
 */

//import java.util.EventListener;

import org.jabber.jabberbeans.*;

/**
 * <code>BSAgentsListener</code> is interface you can implement to get
 * agents events notifications.
 *
 * @author  Jiri Komzak, Knowledge Media Institute, Open University, United Kingdom
 */
public interface BSAgentsListener { //extends EventListener {
    public void agentsListReceived();
    public void agentsError(InfoQuery iq);
}
