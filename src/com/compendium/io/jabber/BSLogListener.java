package com.compendium.io.jabber;

/*
 * BSLogListener.java
 *
 * Project: BuddySpace
 * (C) Copyright Knowledge Media Institute 2002
 *
 *
 * Created on 16 July 2002, 16:12
 */

/**
 * Interface for logging events in jabber client.
 *
 * @author  Jiri Komzak, Knowledge Media Institute, Open University, United Kingdom
 */
public interface BSLogListener {
    /** Called to log status change or event */
    public void logStatus(String source, String message);
    /** Called to log sent packet */
    public void logSentXML(String message);
    /** Called to log packet sent failure */
    public void logSendFailedXML(String message);
    /** Called to log received packet */
    public void logReceivedXML(String message);
    /** Called to log received message */
    public void logMessage(String from, String subject, String body);
}
