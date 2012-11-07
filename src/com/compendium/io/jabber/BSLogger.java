package com.compendium.io.jabber;

/*
 * BSLogger.java
 *
 * Project: BuddySpace
 * (C) Copyright Knowledge Media Institute 2002
 *
 *
 * Created on 16 July 2002, 16:12
 */

import java.util.*;

import org.jabber.jabberbeans.*;

/**
 * <code>BSLogger</code> class logs sent and received packets through given 
 * connection and the connection state. 
 * To listen to logs register your <code>BSLogListener</code> by
 * <code>addLogListener</code>.
 *
 * @author  Jiri Komzak, Knowledge Media Institute, Open University, United Kingdom
 */
public class BSLogger implements PacketListener/*, ConnectionListener*/ {
    private ConnectionBean connection = null;
    private final String name = "Logger";
    private Vector logListeners = null;
    
    /** Constructor */
    BSLogger() {
        logListeners = new Vector();
    }
    
    /** Constructor, which sets <code>ConnectionBean</code> */
    BSLogger(ConnectionBean connection) {
        logListeners = new Vector();
        setConnection(connection);
    }
    
    /** Sets <code>ConnectionBean</code> */
    protected void setConnection(ConnectionBean connection) {
        this.connection = connection;
        if (connection != null) {
            connection.addPacketListener(this);
            //connection.addConnectionListener(this);
        }
    }
    
    /** Returns currently used <code>ConnectionBean</code> */
    protected ConnectionBean getConnection() {
        return connection;
    }
    
    /** PacketListener function - logs received packet */
    public void receivedPacket(PacketEvent pe) {
        fireLogReceivedXML(pe.getPacket().toString());
    }
    
    /** PacketListener function - logs packet sent failure */
    public void sendFailed(PacketEvent pe) {
        fireLogSendFailedXML(pe.getPacket().toString());
    }
    
    /** PacketListener function - logs sent packet */
    public void sentPacket(PacketEvent pe) {
        fireLogSentXML(pe.getPacket().toString());
    }
    
    /*public void connectionChanged(ConnectionEvent ce) {
        ConnectionEvent.EState connState = ce.getState();
        if (connState == ConnectionEvent.STATE_CONNECTED) {
            fireLogStatus(name, "Connected");
        }
        else if (connState == ConnectionEvent.STATE_CONNECTING) {
            fireLogStatus(name, "Connecting");
        }
        else if (connState == ConnectionEvent.STATE_DISCONNECTED) {
            fireLogStatus(name, "Disconnected");
        }
    }*/
    
    
    // *** log listeners ***
    /**
     * Adds <code>BSLogListener</code> to listeners for logging.
     *
     * @see #removeAuthListener
     * @see #fireLogStatus
     * @see #fireLogSentXML
     * @see #fireLogReceivedXML
     */
    public void addLogListener(BSLogListener listener) {
        //assert listener != null : listener;
        if (listener == null) return;
        if (!logListeners.contains(listener)) {
            logListeners.addElement(listener);
        }
    }

    /**
     * Removes <code>BSLogListener</code> from listeners for logging.
     *
     * @see #addAuthListener
     * @see #fireLogStatus
     * @see #fireLogSentXML
     * @see #fireLogReceivedXML
     */
    public void removeLogListener(BSLogListener listener) {
        //assert listener != null : listener;
        if (listener == null) return;
        logListeners.removeElement(listener);
    }
    
    /**
     * Notifies <code>BSLogListener</code>s about status change.
     *
     * @see #addAuthListener
     * @see #removeAuthListener
     * @see #fireLogSentXML
     * @see #fireLogReceivedXML
     */
    private void fireLogStatus(String source, String message) {
        for (Enumeration e = logListeners.elements(); e.hasMoreElements(); ) {
            BSLogListener listener = (BSLogListener) e.nextElement();
            listener.logStatus(source, message);
        }
    }
    
    /**
     * Notifies <code>BSLogListener</code>s about sent XML.
     *
     * @see #addAuthListener
     * @see #removeAuthListener
     * @see #fireLogStatus
     * @see #fireLogReceivedXML
     */
    private void fireLogSentXML(String message) {
        for (Enumeration e = logListeners.elements(); e.hasMoreElements(); ) {
            BSLogListener listener = (BSLogListener) e.nextElement();
            listener.logSentXML(message);
        }
    }
    
    /**
     * Notifies <code>BSLogListener</code>s about send-failed XML.
     *
     * @see #addAuthListener
     * @see #removeAuthListener
     * @see #fireLogStatus
     * @see #fireLogReceivedXML
     */
    private void fireLogSendFailedXML(String message) {
        for (Enumeration e = logListeners.elements(); e.hasMoreElements(); ) {
            BSLogListener listener = (BSLogListener) e.nextElement();
            listener.logSendFailedXML(message);
        }
    }
    
    /**
     * Notifies <code>BSLogListener</code>s about received XML.
     *
     * @see #addAuthListener
     * @see #removeAuthListener
     * @see #fireLogStatus
     * @see #fireLogSentXML
     */
    private void fireLogReceivedXML(String message) {
        for (Enumeration e = logListeners.elements(); e.hasMoreElements(); ) {
            BSLogListener listener = (BSLogListener) e.nextElement();
            listener.logReceivedXML(message);
        }
    }
    
}
