package com.compendium.io.jabber;

/*
 * BSCore.java
 *
 * Project: BuddySpace
 * (C) Copyright Knowledge Media Institute 2002
 *
 * Created on 24 June 2002, 18:30
 */

import java.util.*;

import org.jabber.jabberbeans.*;
import org.jabber.jabberbeans.util.*;

/**
 * <code>BSCore</code> is the main class of BuddySpace backend. It provides
 * the jabber client functionality using other classes such as
 * <code>BSConnectionBean</code>, <code>BSMessengerBean</code>,
 * <code>BSPresenceBean</code>, <code>BSInfoQueryBean</code> and other
 * specialized classes.
 *
 * @author  Jiri Komzak, Knowledge Media Institute, Open University, United Kingdom
 */
public class BSCore {
                                   
    private static Vector logListeners = new Vector();
    private BSLogger logger = null;

	static int idCounter;

    /** Creates new BSCore */
    public BSCore() {
               
         logger = new BSLogger();
    }
        
    /**
     * Returns next unique ID typically used for messages,...
     */
    public static int getNextID() {
        return idCounter++;
    }

    // *** logging functions ***
    
    /** Logs event */
    public static void logEvent(String sender, String log) {
        fireLogStatus(sender, log);
    }
    
    /** Logs message */
    public static void logMessage(String from, String subject, String body) {
        fireLogMessage(from, subject, body);
    }
    
    // *** log listeners ***
    
    /**
     * Adds <code>BSLogListener</code> to listeners for logging.
     *
     * @see #removeLogListener
     * @see #fireLogStatus
     */
    public void addLogListener(BSLogListener listener) {
        //assert listener != null : listener;
        if (listener == null) return;
        if (!logListeners.contains(listener)) {
            logListeners.addElement(listener);
        }
        if (logger != null) logger.addLogListener(listener);
    }

    /**
     * Removes <code>BSLogListener</code> from listeners for logging.
     *
     * @see #addLogListener
     * @see #fireLogStatus
     */
    public void removeLogListener(BSLogListener listener) {
        //assert listener != null : listener;
        if (listener == null) return;
        logListeners.removeElement(listener);
        if (logger != null) logger.removeLogListener(listener);
    }
    
    /**
     * Notifies <code>BSLogListener</code>s about status change.
     *
     * @see #addLogListener
     * @see #removeLogListener
     */
    private static void fireLogStatus(String source, String message) {
        for (Enumeration e = logListeners.elements(); e.hasMoreElements(); ) {
            BSLogListener listener = (BSLogListener) e.nextElement();
            listener.logStatus(source, message);
        }
    }
    
    /**
     * Notifies <code>BSLogListener</code>s about status change.
     *
     * @see #addLogListener
     * @see #removeLogListener
     */
    private static void fireLogMessage(String from, String subject, String body) {
        for (Enumeration e = logListeners.elements(); e.hasMoreElements(); ) {
            BSLogListener listener = (BSLogListener) e.nextElement();
            listener.logMessage(from, subject, body);
        }
    }    
}
