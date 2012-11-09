package com.compendium.io.jabber;

/*
 * BSRegListener.java
 *
 * Project: BuddySpace
 * (C) Copyright Knowledge Media Institute 2002
 *
 *
 * Created on 5 August 2002, 18:15
 */

//import java.util.EventListener;

import org.jabber.jabberbeans.*;

/**
 * <code>BSRegListener</code> is interface you can implement to get
 * registration events notifications.
 *
 * @author  Jiri Komzak, Knowledge Media Institute, Open University, United Kingdom
 */
public interface BSRegListener { //extends EventListener {
    /** Called when registration failes */
    public void regError(InfoQuery iq, String id);
    /** Called when registered */
    public void registered(InfoQuery iq, String id);
    /** Called when registration information is needed */
    public void regInfosNeeded(InfoQuery iq, String id);
}
