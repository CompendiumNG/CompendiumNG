package com.compendium.io.jabber;

/*
 * BSAuthorizationBean.java
 *
 * Project: BuddySpace
 * (C) Copyright Knowledge Media Institute 2002
 *
 *
 * Created on 16 July 2002, 11:49
 */

import java.util.*;
import org.jabber.jabberbeans.*;
import org.jabber.jabberbeans.util.*;
import org.jabber.jabberbeans.Extension.*;

/**
 * <code>BSAuthorizationBean</code> provides athentication handling.
 * It relies on <code>BSInfoQueryBean</code>, which must be set after each
 * reconnection.
 *
 * @author  Jiri Komzak, Knowledge Media Institute, Open University, United Kingdom
 */
public class BSAuthorizationBean implements PacketListener {
    private IQBean iqBean = null;
    private BSAuthState state;
    private String user = null;
    private String password = null;
    private String resource = null;
    private final String name = "Authorization";
    private Vector authListeners;
    
    /**
     * Constructor
     */
    BSAuthorizationBean() {
        state = new BSAuthState();
        state.value = BSAuthState.NOT_AUTHORIZED;
        authListeners = new Vector();
    }
    
    /**
     * Constructor, which sets existing and connected <code>IQBean</code>.
     * Then this is registered as listener for IQ packets.
     */
    BSAuthorizationBean(IQBean iqBean) {
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
        
        state.value = BSAuthState.NOT_AUTHORIZED;
        state.servedID = null;
        state.jid = null;
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
        removeAllAuthListeners();
        if (iqBean != null)
            iqBean.delPacketListener(this);
        iqBean = null;
    }
    
    /**
     * Sets current state in authentication process.
     */
    private void setState(JID jid, int state, String id) {
        this.state.value = state;
        this.state.jid = jid;
        this.state.servedID = id;
        notifyAuthListeners(new BSAuthEvent(this, state));
    }
    
    /**
     * Invokes authentication of given <code>user</code>.
     */
    protected void authorize(String user, String password, String resource) {
        this.user = user;
        this.password = password;
        this.resource = resource;
        
        // returns when not set properly
        if (iqBean == null || iqBean.getConnection() == null) {
            BSCore.logEvent(name, "error: not connected");
            setState(null, BSAuthState.NOT_AUTHORIZED, null);
            return;
        }

        // starts loging in
        String id = new String("BS_AUTH_" + String.valueOf(BSCore.getNextID()));
        setState(null, BSAuthState.AUTHORIZING1, id);
        InfoQueryBuilder iqBuilder = new InfoQueryBuilder();
        IQAuthBuilder iqAuthBuilder = new IQAuthBuilder();
        iqAuthBuilder.setUsername(user);
        
        try {
            iqBuilder.addExtension(iqAuthBuilder.build());
            iqBuilder.setType("get");
            iqBuilder.setIdentifier(id);
            //iqBean.send((InfoQuery)iqBuilder.build());
            iqBean.getConnection().send(iqBuilder.build());
        } catch (InstantiationException e) {
            BSCore.logEvent(name, "error: IQ builder failed");
            setState(null, BSAuthState.NOT_AUTHORIZED, null);
        }
        
        BSCore.logEvent(name, "authentication phase 1");
    }
    
    /**
     * Sends user information including password.
     * This is done during the second phase of authentication process.
     */
    private void sendPassword(IQAuth extension) {
        
        // should ask user for the info according to extension
        
        this.sendPassword(user, password, resource);
    }
    
    /**
     * Sends user information including password.
     * This way the first phase when asking for required data is omitted
     * and data is sent directly.
     */
    protected void sendPassword(String user, String password, String resource) {
        this.user = user;
        this.password = password;
        this.resource = resource;
        
        // returns when not set properly
        if (iqBean == null || iqBean.getConnection() == null) {
            BSCore.logEvent(name, "error: not connected");
            setState(null, BSAuthState.NOT_AUTHORIZED, null);
            return;
        }
        // sends the authorization request
        //eventsTextArea.append("RECEIVED: iq auth result\n");
        String id = new String("BS_AUTH_" + String.valueOf(BSCore.getNextID()));
        setState(null, BSAuthState.AUTHORIZING2, id);
        InfoQueryBuilder iqBuilder = new InfoQueryBuilder();
        IQAuthBuilder iqAuthBuilder = new IQAuthBuilder();
        iqAuthBuilder.setUsername(user);
        iqAuthBuilder.setPassword(password);
        iqAuthBuilder.setResource(resource);
        
        try {
            iqBuilder.addExtension(iqAuthBuilder.build());
            iqBuilder.setType("set");
            iqBuilder.setIdentifier(id);
            //iqBean.send((InfoQuery)iqBuilder.build());
            iqBean.getConnection().send(iqBuilder.build());
        } catch (InstantiationException e) {
            BSCore.logEvent(name, "error: IQ builder failed");
            setState(null, BSAuthState.NOT_AUTHORIZED, null);
            return;
        }
        
        // will wait for auth response
        BSCore.logEvent(name, "authentication phase 2");
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
        //if no ID or packet with that ID not expected
        if (state.servedID == null || !state.servedID.equals(iq.getIdentifier())) {
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
     * authentication packet.
     */
    private void handleError(InfoQuery iq) {
        BSCore.logEvent(name, "error " + iq.getErrorCode() + ": " + iq.getErrorText());
        setState(null, BSAuthState.NOT_AUTHORIZED, null);
    }
    
    /**
     * Handles <code>InfoQuery</code> packet, if it does contain a result.
     * Before this is called it checks if that is response on the sent IQ
     * authentication packet.
     */
    private void handleResult(InfoQuery iq) {
        Enumeration extensions = iq.Extensions();
        
        // when in first phase of authentication
        if (state.value == BSAuthState.AUTHORIZING1) {
            while (extensions != null && extensions.hasMoreElements()) {
                Extension ext = (Extension) extensions.nextElement();
                if (ext instanceof IQAuth)
                    sendPassword((IQAuth)ext);
                else {
                    BSCore.logEvent(name, "error: unexpected IQ extension");
                    setState(null, BSAuthState.NOT_AUTHORIZED, null);
                }
            }
        }
        
        // when in second phase of authentication
        else if (state.value == BSAuthState.AUTHORIZING2) {
            if (extensions == null || !extensions.hasMoreElements()) {
                BSCore.logEvent(name, "authenticated");
                setState(null, BSAuthState.AUTHORIZED, null);
            }
            else {
                BSCore.logEvent(name, "error: IQ extension not expected");
                setState(null, BSAuthState.NOT_AUTHORIZED, null);
            }
        }
        
        else {
            BSCore.logEvent(name, "error: unexpected IQ result");
            //servedID = null;
            //setState(BSAuthState.NOT_AUTHORIZED);
        }
    }
    
    /**
     * Handles <code>InfoQuery</code> packet, if it IQ-set.
     * Before this is called it checks if that is response on the sent IQ
     * authentication packet.
     */
    private void handleSet(InfoQuery iq) {
        return;
    }
    
    /**
     * Invoked when authentication succeeded.
     */
    private void authorized() {
        return;
    }
    
    // *** authorization listeners ***
    /**
     * Adds <code>BSAuthListener</code> to listeners notified when
     * authentication state changes.
     *
     * @see #removeAuthListener
     * @see #removeAllAuthListeners
     * @see #notifyAuthListeners
     */
    public void addAuthListener(BSAuthListener listener) {
        //assert listener != null : listener;
        if (listener == null) return;
        if (!authListeners.contains(listener)) {
            authListeners.addElement(listener);
        }
    }

    /**
     * Removes <code>BSAuthListener</code> to listeners notified when
     * authentication state changes.
     *
     * @see #addAuthListener
     * @see #removeAllAuthListeners
     * @see #notifyAuthListeners
     */
    public void removeAuthListener(BSAuthListener listener) {
        //assert listener != null : listener;
        if (listener == null) return;
        authListeners.removeElement(listener);
    }

    /**
     * Removes all listeners notified when authentication state changes.
     * This can be used before to free dependencies and allow dispose of
     * all objects.
     *
     * @see #addAuthListener
     * @see #removeAuthListener
     * @see #notifyAuthListeners
     */
    public void removeAllAuthListeners() {
        authListeners.clear();
    }

    /**
     * Notifies authentication listeners when
     * authentication state changes.
     *
     * @see #addAuthListener
     * @see #removeAuthListener
     * @see #removeAllAuthListeners
     */
    private void notifyAuthListeners(BSAuthEvent ae) {
        for (Enumeration e = authListeners.elements(); e.hasMoreElements(); ) {
            BSAuthListener listener = (BSAuthListener) e.nextElement();
            
            switch (ae.getState().value) {
                case BSAuthState.NOT_AUTHORIZED:
                    listener.authError(ae);
                    break;
                    
                case BSAuthState.AUTHORIZING1:
                case BSAuthState.AUTHORIZING2:
                    listener.authorizing(ae);
                    break;
                    
                case BSAuthState.AUTHORIZED:
                    listener.authorized(ae);
                    break;
            }
        }
    }
    
}
