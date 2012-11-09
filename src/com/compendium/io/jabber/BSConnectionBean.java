package com.compendium.io.jabber;

/*
 * BSConnectionBean.java
 *
 * Project: BuddySpace
 * (C) Copyright Knowledge Media Institute 2002
 *
 *
 * Created on 15 July 2002, 17:40
 */

import java.util.*;

import org.jabber.jabberbeans.*;

//import edu.ou.kmi.buddyspace.xml.*;

/**
 * <code>BSConnectionBean</code> is the main bean for BuddySpaceBeans.
 * A <code>BSConnectionBean</code> mantains connection to a jabber server.
 * All the other beans are dependent on this and has to be reset
 * (typically using bean.<code>setConnection</code> function) after a new connection
 * is setablished (<code>BSConnectionBean.connect</code> function).
 * The class is based on <code>ConnectionBean</code> and implements
 * <code>ConnectionListener</code> from <code>JabberBeans</code>.
 *
 * @author  Jiri Komzak, Knowledge Media Institute, Open University, United Kingdom
 */
public class BSConnectionBean implements ConnectionListener, PacketListener {
    private ConnectionBean connection;
    private ConnectionEvent.EState connState;
    private final String name = "Connection"; 
    private Vector connectionListeners;
    
    //private boolean aboutDisconnect = false;
    
    /**
     * Constructor
     */
    BSConnectionBean() {
        connection = null;
        connState = ConnectionEvent.STATE_DISCONNECTED;
        connectionListeners = new Vector();
    }
    
    /**
     * Creates new connection to the specified <code>hostName</host>
     *
     *@return <code>false</code> if the connection cannot be established
     */
    public boolean connect(String hostName, int port) {
        connection = new ConnectionBean();
        connection.addConnectionListener(this);
        connection.addPacketListener(this);
        
        try {
            java.net.InetAddress host;
            host = java.net.InetAddress.getByName(hostName);
            connection.connect(host, port);
            //connected = true;
        } catch (java.net.UnknownHostException e) {
            //eventsTextArea.append("ERROR: unknown host\n");
            BSCore.logEvent(name, "error: unknown host"); 
            //fireConnectionError("Unknown host");
            notifyConnectionListeners(new ConnectionEvent(this, 
                                      ConnectionEvent.STATE_DISCONNECTED, 
                                      connState, 
                                      ConnectionEvent.REASON_IO_ERROR));
            disconnect();
            return false;
        } catch (java.io.IOException e) {
            //eventsTextArea.append("ERROR: IO error while connecting\n");
            BSCore.logEvent(name, "error: IO error while connecting"); 
            //fireConnectionError("IO error while connecting");
            notifyConnectionListeners(new ConnectionEvent(this, 
                                      ConnectionEvent.STATE_DISCONNECTED, 
                                      connState, 
                                      ConnectionEvent.REASON_IO_ERROR));
            disconnect();
            return false;
        }
        return true;
    }
    
    /**
     * Creates new connection to the specified <code>hostName</host>
     *
     *@return <code>false</code> if the connection cannot be established
     */
    public boolean connect(String hostName) {
        
        return connect(hostName, ConnectionBean.DEFAULT_PORT);
    }
    
    /**
     * Closes current connection
     */
    public void disconnect() {
        if (connection != null) {
            //PacketBuilder packBuilder = new PacketBuilder();
            //packBuilder.setContent("</stream:stream>");
            //connection.send(packBuilder.build());
            //Packet p = packBuilder.build();
            //String s = p.toString();
            connState = connection.getConnectionState();
            if (connState == ConnectionEvent.STATE_CONNECTED) {
                XMLStreamFooter f = new XMLStreamFooter();
                connection.send(f);
                if (connection != null)
                    connection.disconnect();
            }
            else {
                connection = null;
                BSCore.logEvent(name, "diconnected"); 
            }
        }
    }
    
    /**
     * Frees all object bindings to allow object destroy
     */
    protected void prepareToDestroy() {
         disconnect();
         removeAllConnectionListeners();
    }
    
    /**
     * Handles changes of connection state. 
     * See ConnectionListener in JabberBeans.
     */ 
    public void connectionChanged(ConnectionEvent ce) {
        connState = ce.getState();
        if (connState == ConnectionEvent.STATE_CONNECTED) {
            //aboutDisconnect = false;
            BSCore.logEvent(name, "connected"); 
            //connectButton.setText("Disconnect");
        }
        else if (connState == ConnectionEvent.STATE_CONNECTING)
            BSCore.logEvent(name, "connecting"); 
        else if (connState == ConnectionEvent.STATE_DISCONNECTED) {
            //aboutDisconnect = false;
            connection = null;
            BSCore.logEvent(name, "diconnected"); 
            //connectButton.setText("Connect");
        }
        else {
            BSCore.logEvent(name, "unknown"); 
            //connectButton.setText("Disconnect");
        }
        notifyConnectionListeners(new ConnectionEvent(this, connState, 
                                                      ce.getOldState(), ce.getReason()));
    }
    
    // *** connection listeners ***
    
    /**
     * Adds <code>ConnectionListener</code> to listeners notified when
     * connection state changes.
     *
     * @see #removeConnectionListener
     * @see #removeAllConnectionListeners
     * @see #notifyConnectionListeners
     */
    public void addConnectionListener(ConnectionListener listener) {
        //assert listener != null : listener;
        if (listener == null) return;
        if (!connectionListeners.contains(listener)) {
            connectionListeners.addElement(listener);
        }
    }

    /**
     * Removes <code>ConnectionListener</code> from listeners notified when
     * connection state changes.
     *
     * @see #addConnectionListener
     * @see #removeAllConnectionListeners
     * @see #notifyConnectionListeners
     */
    public void removeConnectionListener(ConnectionListener listener) {
        //assert listener != null : listener;
        if (listener == null) return;
        connectionListeners.removeElement(listener);
    }

    /**
     * Removes all listeners notified when connection state changes.
     * This can be used before to free dependencies and allow dispose of
     * all objects.
     *
     * @see #addConnectionListener
     * @see #removeConnectionListener
     * @see #notifyConnectionListeners
     */
    public void removeAllConnectionListeners() {
        connectionListeners.clear();
    }

    /**
     * Notifies connection listeners about connection state change.
     *
     * @see #addConnectionListener
     * @see #removeConnectionListener
     * @see #removeAllConnectionListeners
     */
    private void notifyConnectionListeners(ConnectionEvent ce) {
        for (Enumeration e = connectionListeners.elements(); e.hasMoreElements(); ) {
            ConnectionListener listener = (ConnectionListener) e.nextElement();
            listener.connectionChanged(ce);
        }
    }
    
    // *** other ***
    /**
     * Returns current connection state.
     */
    public ConnectionEvent.EState getState() {
        return connState;
    }
    
    /**
     * Returns current <code>ConnectionBean</code>.
     * Which is typically used for initialization of other beans.
     */
    public ConnectionBean getConnection() {
        return connection;
    }
    
    /** PacketListener method */
    public void receivedPacket(PacketEvent pe) {
        //BSCore.logReceived(name, pe.getPacket().toString());
    }
    
    /** PacketListener method */
    public void sendFailed(PacketEvent pe) {
        //BSCore.logSendFailed(name, pe.getPacket().toString());
        connState = ConnectionEvent.STATE_DISCONNECTED;
        notifyConnectionListeners(new ConnectionEvent(this, connState, 
                                                      ConnectionEvent.STATE_CONNECTED,
                                                      ConnectionEvent.REASON_UNKNOWN));
        connection = null;
        BSCore.logEvent(name, "diconnected"); 
    }
    
    /** PacketListener method */
    public void sentPacket(PacketEvent pe) {
        //BSCore.logSent(name, pe.getPacket().toString());
    }
    
}
