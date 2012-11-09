package com.compendium.io.jabber;

/*
 * BSRegState.java
 *
 * Project: BuddySpace
 * (C) Copyright Knowledge Media Institute 2002
 *
 *
 * Created on 5 August 2002, 18:14
 */

import org.jabber.jabberbeans.util.*;

/**
 * <code>BSRegState</code> contains information about current phase
 * of registration process.
 *
 * @author  Jiri Komzak, Knowledge Media Institute, Open University, United Kingdom
 */
public class BSRegState {
    /** BSRegState enumeration values */
    public final static int NOT_SET          =-1;
    public final static int NOT_REGISTERED   = 0;
    public final static int REGISTERING1     = 1;
    public final static int REG_INFOS_NEEDED = 3;
    public final static int REGISTERING2     = 4;
    public final static int REGISTERED       = 5;
    /** stores the value of the enumeration - initially 'not set' */
    public int value = NOT_SET;
    
    public String servedID = null;
    
    public JID jid = null;
}
