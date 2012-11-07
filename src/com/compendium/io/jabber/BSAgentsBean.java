package com.compendium.io.jabber;

/*
 * BSAgentsBean.java
 *
 * Project: BuddySpace
 * (C) Copyright Knowledge Media Institute 2002
 *
 *
 * Created on 7 August 2002, 10:03
 */

import java.util.*;
import org.jabber.jabberbeans.*;
import org.jabber.jabberbeans.Extension.*;

/**
 * <code>BSAgentsBean</code> provides agents functionality handling.
 * It relies on <code>BSInfoQueryBean</code>, which must be set after each
 * reconnection.
 *
 * @author  Jiri Komzak, Knowledge Media Institute, Open University, United Kingdom
 */
public class BSAgentsBean implements PacketListener {
    protected String servedID = null;
    protected IQBean iqBean = null;
    protected IQAgents agents = null;
    protected final String name = "Agents"; 
    protected Vector agentsListeners;
    
    /**
     * Constructor
     */
    public BSAgentsBean() {
        //agents = new Hashtable();
        agentsListeners = new Vector();
    }
    
    /**
     * Constructor, which sets existing and connected <code>IQBean</code>.
     * Then this is registered as listener for IQ packets.
     */
    public BSAgentsBean(IQBean iqBean) {
        this();
        setIQBean(iqBean);
    }
    
    /**
     * Sets existing and connected <code>IQBean</code>.
     * Then this is registered as listener for IQ packets.
     */
    public void setIQBean(IQBean iqBean) {
        if (this.iqBean != null)
            this.iqBean.delPacketListener(this);
        this.iqBean = iqBean;
        if (iqBean != null)
            iqBean.addPacketListener(this);
        servedID = null;
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
    public void prepareToDestroy() {
        if (iqBean != null)
            iqBean.delPacketListener(this);
        removeAllAgentsListeners();
        iqBean = null;
    }
    
    /**
     * Sends request for agents list.
     */
    public boolean getAgents() {
        
        if (iqBean == null || iqBean.getConnection() == null) {
            BSCore.logEvent(name, "error: not connected"); 
            servedID = null;
            return false;
        }

        // gets agents list
        servedID = new String("GET_AGENTS_" + String.valueOf(BSCore.getNextID())); 
        InfoQueryBuilder iqBuilder = new InfoQueryBuilder();
        IQAgentsBuilder iqAgentsBuilder = new IQAgentsBuilder();
        
        try {
            iqBuilder.addExtension(iqAgentsBuilder.build());
            iqBuilder.setType("get"); 
            iqBuilder.setIdentifier(servedID);
            //iqBean.send((InfoQuery)iqBuilder.build());
            iqBean.getConnection().send(iqBuilder.build());
        } catch (InstantiationException e) {
            BSCore.logEvent(name, "error: IQ builder failed"); 
            servedID = null;
            return false;
        }
        
        BSCore.logEvent(name, "getting agents list"); 
        return true;
    }
    
    /**
     * Returns <code>IQAgents</code> extension.
     */
    public IQAgents agents() {
        return agents;
    }
    
    /**
     * Returns <code>Enumeration</code> of agents.
     */
    public Enumeration agentsEnumeration() {
        // reads the agents and stores into hashtable
        Vector agentsVector = new Vector();
        Enumeration agentsEnum = agents.agents();
        while (agentsEnum.hasMoreElements()) {
            Agent a = (Agent) agentsEnum.nextElement();
            agentsVector.add(a);
        }
        return agentsVector.elements();
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
        if (iq.getIdentifier() == null || !iq.getIdentifier().equals(servedID)) {
            return;
        }
        
        if ((new String("result")).equals(iq.getType())) 
            handleResult(iq);
        else if ((new String("error")).equals(iq.getType())) 
            handleError(iq);
        else if ((new String("set")).equals(iq.getType())) 
            handleSet(iq);
    }
    
    /**
     * Invoked when a IQ packet send failes.
     */
    public void sendFailed(PacketEvent pe) {
    }
    
    /**
     * Invoked when a IQ packet is sent.
     */
    public void sentPacket(PacketEvent pe) {
    }

    // *** infoQuery handling ***
    
    /**
     * Handles <code>InfoQuery</code> packet, if it does contain an error.
     * Before this is called it checks if that is response on the sent IQ
     * agents packet.
     */
    protected void handleError(InfoQuery iq) {
        BSCore.logEvent(name, "error " + iq.getErrorCode() + ": " + iq.getErrorText());  
        servedID = null;
        fireAgentsError(iq);
    }
    
    /**
     * Handles <code>InfoQuery</code> packet, if it does contain a result.
     * Before this is called it checks if that is response on the sent IQ
     * agents packet.
     */
    protected void handleResult(InfoQuery iq) {
        Enumeration extensions = iq.Extensions();
        
        // for all extensions
        while (extensions != null && extensions.hasMoreElements()) {
            Extension ext = (Extension) extensions.nextElement();
            // if that is a agents extension
            if (ext instanceof IQAgents) {
                agents = (IQAgents) ext;
                // fires that agents list arrived
                BSCore.logEvent(name, "agents list received"); 
                fireAgentsListReceived();
            }
            else {
                BSCore.logEvent(name, "error: unexpected IQ extension"); 
                servedID = null;
            }
        }
    }
    
    /**
     * Handles <code>InfoQuery</code> packet, if it IQ-set.
     * Before this is called it checks if that is response on the sent IQ
     * agents packet.
     */
    protected void handleSet(InfoQuery iq) {
        return;
    }
    
    // *** agents listeners ***
    
    /**
     * Adds <code>BSAgentsListener</code> to listeners notified when
     * agents event occures.
     *
     * @see #removeAgentsListener
     * @see #removeAllAgentsListeners
     * @see #fireAgentsListReceived
     */
    public void addAgentsListener(BSAgentsListener listener) {
        //assert listener != null : listener;
        if (listener == null) return;
        if (!agentsListeners.contains(listener)) {
            agentsListeners.addElement(listener);
        }
    }

    /**
     * Removes <code>BSAgentsListener</code> to listeners notified when
     * agents event occures.
     *
     * @see #addAgentsListener
     * @see #removeAllAgentsListeners
     * @see #fireAgentsListReceived
     */
    public void removeAgentsListener(BSAgentsListener listener) {
        //assert listener != null : listener;
        if (listener == null) return;
        agentsListeners.removeElement(listener);
    }

    /**
     * Removes all listeners notified when agents event occures.
     * This can be used before to free dependencies and allow dispose of
     * all objects.
     *
     * @see #addAgentsListener
     * @see #removeAgentsListener
     * @see #fireAgentsListReceived
     */
    public void removeAllAgentsListeners() {
        agentsListeners.clear();
    }

    /**
     * Notifies agents listeners when agents list arrives.
     *
     * @see #addAgentsListener
     * @see #removeAgentsListener
     * @see #removeAllAgentsListeners
     */
    protected void fireAgentsListReceived() {
        for (Enumeration e = agentsListeners.elements(); e.hasMoreElements(); ) {
            BSAgentsListener listener = (BSAgentsListener) e.nextElement();
            
            listener.agentsListReceived();
        }
    }
    
    /**
     * Notifies agents listeners when error arrives.
     *
     * @see #addAgentsListener
     * @see #removeAgentsListener
     * @see #removeAllAgentsListeners
     */
    protected void fireAgentsError(InfoQuery iq) {
        for (Enumeration e = agentsListeners.elements(); e.hasMoreElements(); ) {
            BSAgentsListener listener = (BSAgentsListener) e.nextElement();
            
            listener.agentsError(iq);
        }
    }
    
}
