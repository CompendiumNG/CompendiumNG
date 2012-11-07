package com.compendium.io.jabber;

/*
 * BSInfoQueryBean.java
 *
 * Project: BuddySpace
 * (C) Copyright Knowledge Media Institute 2002
 *
 *
 * Created on 16 July 2002, 12:31
 */

import java.util.*;
import org.jabber.jabberbeans.*;
import org.jabber.jabberbeans.util.*;
import org.jabber.jabberbeans.Extension.*;

/**
 * <code>BSInfoQueryBean</code> handles IQ part of jabber protocol.
 * It relies on <code>BSConnectionBean</code> which provides actual connection.
 * <code>BSInfoQuery</code>.<code>setConnection</code> function must be called
 * after each connection establishment.
 *
 * @author  Jiri Komzak, Knowledge Media Institute, Open University, United Kingdom
 */
public class BSInfoQueryBean implements PacketListener {
    private Hashtable servedIDs = null;
    private IQBean iqBean = null;
    private final String name = "IQ"; 
    private Vector iqListeners = null;
    
    /**
     * Constructor
     */
    BSInfoQueryBean() {
         servedIDs = new Hashtable();
         iqListeners = new Vector();
    }
    
    /**
     * Constructor, which sets existing and connected <code>ConnectionBean</code>.
     * Then <code>IQBean</code> is created and this is registered
     * as listener for IQ packets.
     */
    BSInfoQueryBean(ConnectionBean connection) {
         servedIDs = new Hashtable();
         iqListeners = new Vector();
         setConnection(connection);
    }
    
    /**
     * Sets existing and connected <code>ConnectionBean</code>.
     * Then <code>IQBean</code> is created and this is registered
     * as listener for IQ packets.
     */
    protected void setConnection(ConnectionBean connection) {
         iqBean = new IQBean(connection);
         //iqBean.setConnection(connection);
         iqBean.addPacketListener(this);
         servedIDs = null;
    }
    
    /**
     * Returns currently used <code>ConnectionBean</code>.
     */
    protected ConnectionBean getConnection() {
        return (iqBean != null)? iqBean.getConnection() : null;
    }
    
    /**
     * Returns currently used <code>IQBean</code>.
     */
    public IQBean getIQBean() {
        return iqBean;
    }
    
    /**
     * Frees all object bindings to allow object destroy
     */
    protected void prepareToDestroy() {
        //removeAllIQListeners();
        iqBean = null;
    }
    
    /**
     * Invoked when a message packet is received.
     */
    public void receivedPacket(PacketEvent packetEvent) {
        if (!(packetEvent.getPacket() instanceof InfoQuery))
            return;
        
        tryOOB(packetEvent);
    }
    
    /**
     * Invoked when a message packet send failes.
     */
    public void sendFailed(PacketEvent packetEvent) {
    }
    
    /**
     * Invoked when a message packet is sent.
     */
    public void sentPacket(PacketEvent packetEvent) {
    }
    
    /**
     * Checks if some OOB data extension was received; if
     * so calls fireOOBReceived.
     */
    private void tryOOB(PacketEvent packetEvent) {
        InfoQuery iq = (InfoQuery) packetEvent.getPacket();
        
        String type = iq.getType();
        if (!(new String("set")).equals(type)) 
            return;
        
        Enumeration exts = iq.Extensions();
        while (exts.hasMoreElements()) {
            Extension e = (Extension) exts.nextElement();
            if (e instanceof OOB) {
                OOB oob = (OOB) e;
                String url = oob.getURL();
                JID jid = iq.getFromAddress();
                //BSCore.downloadURL(url, jid);
                fireOOBReceived(url, jid);
            }
        }
    }
    
    /**
     * Sends packet including OOB extension. Does transfer the data!
     */
    public void sendOOB(JID jid, String url) {
        if (url == null || jid == null) return;
        if (iqBean == null || iqBean.getConnection() == null) {
            BSCore.logEvent(name, "error: not connected"); 
            //servedID = null;
            return;
        }

        // gets agents list
        //servedID = new String("OOB_" + String.valueOf(BSCore.getNextID()));
        InfoQueryBuilder iqBuilder = new InfoQueryBuilder();
        OOBBuilder oobBuilder = new OOBBuilder();
        oobBuilder.setIQ(true);
        oobBuilder.setURL(url);
        
        try {
            iqBuilder.addExtension(oobBuilder.build());
            iqBuilder.setType("set"); 
            //iqBuilder.setIdentifier(servedID);
            iqBuilder.setToAddress(jid);
            //iqBean.send((InfoQuery)iqBuilder.build());
            iqBean.getConnection().send(iqBuilder.build());
        } catch (InstantiationException e) {
            BSCore.logEvent(name, "error: IQ builder failed"); 
            //servedID = null;
        }
        
        BSCore.logEvent(name, "iq-oob sent"); 
    }
    
    /**
     * Adds <code>BSIQListener</code>
     */
    public void addIQListener(BSIQListener listener) {
        //assert listener != null : listener;
        if (listener == null) return;
        if (!iqListeners.contains(listener)) {
            iqListeners.addElement(listener);
        }
    }
    
    /**
     * Notifies all <code>BSIQListener</code>s that an OOB extension
     * was received.
     */
    private void fireOOBReceived(String url, JID jid) {
        for (Enumeration e = iqListeners.elements(); e.hasMoreElements(); ) {
            BSIQListener listener = (BSIQListener) e.nextElement();
            
            listener.oobReceived(url, jid);
        }
    }
    
}
