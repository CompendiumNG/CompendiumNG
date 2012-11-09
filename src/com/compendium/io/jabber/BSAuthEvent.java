package com.compendium.io.jabber;

/*
 * BSAuthEvent.java
 *
 * Project: BuddySpace
 * (C) Copyright Knowledge Media Institute 2002
 *
 *
 * Created on 16 July 2002, 11:49
 */

import java.util.EventObject;
//import org.jabber.jabberbeans.*;
//import org.jabber.jabberbeans.Extension.*;

/**
 * <code>BSAuthEvent</code> contains information about event
 * during authentication process. It contains new current state.
 *
 * @author  Jiri Komzak, Knowledge Media Institute, Open University, United Kingdom
 */
public class BSAuthEvent extends EventObject {
    protected BSAuthState state;
    
    /**
     * Constructor.
     */
    BSAuthEvent(Object source, int state) {
        super(source);
        this.state = new BSAuthState();
        this.state.value = state;
    }
    
    /**
     * Returns <code>BSAuthState</code>.
     */
    public BSAuthState getState() {
        return state;
    }
    
}
