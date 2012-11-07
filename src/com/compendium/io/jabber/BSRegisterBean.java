package com.compendium.io.jabber;

/*
 * BSRegisterBean.java
 *
 * Project: BuddySpace
 * (C) Copyright Knowledge Media Institute 2002
 *
 *
 * Created on 5 August 2002, 14:59
 */

import java.util.*;
import org.jabber.jabberbeans.*;
import org.jabber.jabberbeans.util.*;
import org.jabber.jabberbeans.Extension.*;
import org.jabber.jabberbeans.Extension.Extension;

/**
 * <code>BSRegisterBean</code> provides new account registration handling.
 * It relies on <code>BSInfoQueryBean</code>, which must be set after each
 * reconnection.
 *
 * @author  Jiri Komzak, Knowledge Media Institute, Open University, United Kingdom
 */
public class BSRegisterBean implements PacketListener {

    private IQBean iqBean = null;
    private final String name = "Registration";
    private Vector regListeners;
    private Hashtable servedTasks;
    
    /**
     * Constructor
     */
    BSRegisterBean() {
        servedTasks = new Hashtable();
        regListeners = new Vector();
    }
    
    /**
     * Constructor, which sets existing and connected <code>IQBean</code>.
     * Then this is registered as listener for IQ packets.
     */
    BSRegisterBean(IQBean iqBean) {
        this();
        setIQBean(iqBean);
    }
    
    /**
     * Sets existing and connected <code>IQBean</code>.
     * Then this is registered as listener for IQ packets.
     */
    protected void setIQBean(IQBean iqBean) {
        if (this.iqBean != null)
            this.iqBean.delPacketListener(this);
        this.iqBean = iqBean;
        if (iqBean != null)
            iqBean.addPacketListener(this);
        
        servedTasks.clear();
    }
    
    /**
     * Returns currently used <code>IQBean</code>.
     */
    protected IQBean getIQBean() {
        return iqBean;
    }
    
    /**
     * Frees all object bindings to allow object destroy
     */
    protected void prepareToDestroy() {
        removeAllRegListeners();
        if (iqBean != null)
            iqBean.delPacketListener(this);
        iqBean = null;
        servedTasks.clear();
    }
    
    /**
     * Sets current state in registration process for given jid.
     */
    private void setState(JID jid, int state, String servedID) {
        String jidStr = (jid == null)? "@@@" : BSPresenceBean.getJIDHashString(jid, true);
        BSRegState task = (BSRegState) servedTasks.get(jidStr);
        if (task == null) {
            task = new BSRegState();
            servedTasks.put(jidStr, task);
        }
        task.jid = jid;
        task.value = state;
        task.servedID = servedID;
    }
    
    /**
     * Returns state for given JID.
     */
    private BSRegState getState(JID jid) {
        String jidStr = (jid == null)? "@@@" : BSPresenceBean.getJIDHashString(jid, true);
        BSRegState task = (BSRegState) servedTasks.get(jidStr);
        return task;
    }
    
    /**
     * Invokes registration.
     */
    public boolean register(JID jid, PacketID packetID) {
        
        if (iqBean == null || iqBean.getConnection() == null) {
            BSCore.logEvent(name, "error: not connected");
            return false;
        }
        
        // starts logging in
        String id = new String("BS_REG_" + String.valueOf(BSCore.getNextID()));
        if (packetID != null) packetID.setID(id);
        setState(jid, BSRegState.REGISTERING1, id);
        InfoQueryBuilder iqBuilder = new InfoQueryBuilder();
        IQRegisterBuilder iqRegBuilder = new IQRegisterBuilder();
        
        try {
            iqBuilder.addExtension(iqRegBuilder.build());
            iqBuilder.setType("get");
            iqBuilder.setIdentifier(id);
            if (jid != null) 
                iqBuilder.setToAddress(jid);
            iqBean.getConnection().send(iqBuilder.build());
        } catch (InstantiationException e) {
            BSCore.logEvent(name, "error: IQ builder failed");
            setState(jid, BSRegState.NOT_REGISTERED, null);
            return false;
        }
        
        BSCore.logEvent(name, "registration phase 1");
        return true;
    }
    
    /**
     * Sends user information needed for registration.
     * This is done during the second phase of registration process.
     */
    public boolean sendInfos(JID jid, PacketID packetID, Hashtable regInfos) {
        if (iqBean == null || iqBean.getConnection() == null) {
            BSCore.logEvent(name, "error: not connected");
            return false;
        }
        // sends the required information
        String id = new String("BS_REG_" + String.valueOf(BSCore.getNextID()));
        if (packetID != null) packetID.setID(id);
        setState(jid, BSRegState.REGISTERING2, id);
        InfoQueryBuilder iqBuilder = new InfoQueryBuilder();
        IQRegisterBuilder iqRegBuilder = new IQRegisterBuilder();
        
        Enumeration names = regInfos.keys();
        while (names.hasMoreElements()) {
            String n = (String) names.nextElement();
            String v = (String) regInfos.get(n);
            iqRegBuilder.set(n, v);
        }
        
        try {
            iqBuilder.addExtension(iqRegBuilder.build());
            iqBuilder.setType("set");
            iqBuilder.setIdentifier(id);
            if (jid != null) iqBuilder.setToAddress(jid);
            iqBean.getConnection().send(iqBuilder.build());
        } catch (InstantiationException e) {
            BSCore.logEvent(name, "error: IQ builder failed");
            setState(jid, BSRegState.NOT_REGISTERED, null);
            return false;
        }
        
        // will wait for register response
        BSCore.logEvent(name, "registration phase 2");
        return true;
    }
    
    /**
     * Directly sends user information needed for registration. Doesn't
     * ask for needed information and sends only username and password.
     */
    public boolean sendInfos(String username, String password, PacketID packetID) {
        Hashtable values = new Hashtable();
        values.put("username", username);
        values.put("password", password);
        
        return sendInfos(null, packetID, values);
    }
    
    // *** packet handling ***
    
    /**
     * Invoked when a IQ packet is received.
     */
    public void receivedPacket(PacketEvent pe) {
        if (!(pe.getPacket() instanceof InfoQuery)) {
            BSCore.logEvent(name, "warning: nonIQ packet received");
            return;
        }
        
        InfoQuery iq = (InfoQuery) pe.getPacket();
        BSRegState task = getState(iq.getFromAddress());
        if (task == null || task.servedID == null || 
            !task.servedID.equals(iq.getIdentifier()))
            return;
        
        if (iq.getType() != null && iq.getType().equals("result"))
            handleResult(iq, task);
        else if (iq.getType() != null && iq.getType().equals("error"))
            handleError(iq, task);
        else if (iq.getType() != null && iq.getType().equals("set"))
            handleSet(iq, task);
    }
    
    /**
     * Invoked when a IQ packet send failes.
     */
    public void sendFailed(PacketEvent pe) { }
    
    /**
     * Invoked when a IQ packet is sent.
     */
    public void sentPacket(PacketEvent pe) { }

    // *** infoQuery handling ***
    
    /**
     * Handles <code>InfoQuery</code> packet, if it does contain an error.
     * Before this is called it checks if that is response on the sent IQ
     * authentication packet.
     */
    private void handleError(InfoQuery iq, BSRegState task) {
        BSCore.logEvent(name, "error " + iq.getErrorCode() + ": " + iq.getErrorText());
        setState(task.jid, BSRegState.NOT_REGISTERED, null);
        fireError(iq, iq.getIdentifier());
    }
    
    /**
     * Handles <code>InfoQuery</code> packet, if it does contain a result.
     * Before this is called it checks if that is response on the sent IQ
     * authentication packet.
     */
    private void handleResult(InfoQuery iq, BSRegState task) {
        String id = iq.getIdentifier();
        
        if (task.value == BSRegState.REGISTERING1) {
            // calls to enter the required values
            fireRegInfosRequired(iq, id);
        }
        
        else if (task.value == BSRegState.REGISTERING2) {
            setState(iq.getFromAddress(), BSRegState.REGISTERED, null);
            fireRegistered(iq, id);
        }
        
        else {
            BSCore.logEvent(name, "error: unexpected IQ result");
            //servedID = null;
            //setState(BSAuthState.NOT_REGISTERED);
        }
    }
    
    public Hashtable getRequiredInfos(InfoQuery iq) {
        Hashtable requiredInfos = new Hashtable();
        
        if (iq == null) return requiredInfos;
        
        Enumeration extensions = iq.Extensions();
        
        // for all extensions
        while (extensions != null && extensions.hasMoreElements()) {
            Extension ext = (Extension) extensions.nextElement();
            // if that is a register extension
            if (ext instanceof IQRegister) {
                // reads the tags names and values into a hashtable
                Enumeration names = ((IQRegister)ext).getNames();
                while (names.hasMoreElements()) {
                    String n = (String) names.nextElement();
                    String v = ((IQRegister)ext).getValue(n);
                    requiredInfos.put(n, v);
                }
            }
            else if (ext instanceof org.jabber.jabberbeans.Extension.IQRegister) {
                // reads the tags names and values into a hashtable
                Enumeration names = ((org.jabber.jabberbeans.Extension.IQRegister)ext).getNames();
                while (names.hasMoreElements()) {
                    String n = (String) names.nextElement();
                    String v = ((org.jabber.jabberbeans.Extension.IQRegister)ext).getValue(n);
                    requiredInfos.put(n, v);
                }
            }
            // if that is a register extension
            /*else if (ext instanceof edu.ou.kmi.buddyspace.xml.IQRegister) {
                // reads the tags names and values into a hashtable
                Enumeration names = ((edu.ou.kmi.buddyspace.xml.IQRegister)ext).getNames();
                while (names.hasMoreElements()) {
                    String n = (String) names.nextElement();
                    String v = ((edu.ou.kmi.buddyspace.xml.IQRegister)ext).getValue(n);
                    requiredInfos.put(n, v);
                }
            }*/
        }
        
        return requiredInfos;
    }
    
    /**
     * Handles <code>InfoQuery</code> packet, if it IQ-set.
     * Before this is called it checks if that is response on the sent IQ
     * authentication packet.
     */
    private void handleSet(InfoQuery iq, BSRegState task) {
        return;
    }
    
    // *** registration listeners ***
    /**
     * Adds <code>BSRegListener</code> to listeners notified when
     * registration state changes.
     *
     * @see #removeRegListener
     * @see #removeAllRegListeners
     * @see #notifyRegListeners
     */
    public void addRegListener(BSRegListener listener) {
        //assert listener != null : listener;
        if (listener == null) return;
        if (!regListeners.contains(listener)) {
            regListeners.addElement(listener);
        }
    }

    /**
     * Removes <code>BSRegListener</code> to listeners notified when
     * registration state changes.
     *
     * @see #addRegListener
     * @see #removeAllRegListeners
     * @see #notifyRegListeners
     */
    public void removeRegListener(BSRegListener listener) {
        //assert listener != null : listener;
        if (listener == null) return;
        regListeners.removeElement(listener);
    }

    /**
     * Removes all listeners notified when registration state changes.
     * This can be used before to free dependencies and allow dispose of
     * all objects.
     *
     * @see #addRegListener
     * @see #removeRegListener
     * @see #notifyRegListeners
     */
    public void removeAllRegListeners() {
        regListeners.clear();
    }

    /**
     * Notifies registration listeners.
     *
     * @see #addRegListeners
     * @see #removeRegListener
     * @see #removeAllRegListeners
     */
    protected void fireRegInfosRequired(InfoQuery iq, String id) {
        for (Enumeration e = regListeners.elements(); e.hasMoreElements(); ) {
            BSRegListener listener = (BSRegListener) e.nextElement();
            listener.regInfosNeeded(iq, id);
        }
    }
    
    protected void fireRegistered(InfoQuery iq, String id) {
        for (Enumeration e = regListeners.elements(); e.hasMoreElements(); ) {
            BSRegListener listener = (BSRegListener) e.nextElement();
            listener.registered(iq, id);
        }
    }
    
    protected void fireError(InfoQuery iq, String id) {
        for (Enumeration e = regListeners.elements(); e.hasMoreElements(); ) {
            BSRegListener listener = (BSRegListener) e.nextElement();
            listener.regError(iq, id);
        }
    }
    
}
