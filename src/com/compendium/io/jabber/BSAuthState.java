package com.compendium.io.jabber;


/*
 * BSAuthListener.java
 *
 * Project: BuddySpace
 * (C) Copyright Knowledge Media Institute 2002
 *
 *
 * Created on 16 July 2002, 17:30
 */

import org.jabber.jabberbeans.util.*;

/**
 * <code>BSAuthState</code> contains information about current phase
 * of authentication process.
 *
 * @author  Jiri Komzak, Knowledge Media Institute, Open University, United Kingdom
 */
public class BSAuthState {
    /** BSAuthState enumeration values */
    public final static int NOT_SET        =-1;
    public final static int NOT_AUTHORIZED = 0;
    public final static int AUTHORIZING1   = 1;
    public final static int AUTHORIZING2   = 2;
    public final static int AUTHORIZED     = 3;
    /** stores the value of the enumeration - initially 'not set' */
    public int value = NOT_SET;
    
    public String servedID = null;
    
    public JID jid = null;
}
