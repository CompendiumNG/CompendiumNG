package com.compendium.io.jabber;

/*
 * BSAuthListener.java
 *
 * Project: BuddySpace
 * (C) Copyright Knowledge Media Institute 2002
 *
 *
 * Created on 16 July 2002, 17:35
 */

import java.util.EventListener;

/**
 * <code>BSAuthListener</code> is interface you can implement to get
 * authentication events notifications.
 *
 * @author  Jiri Komzak, Knowledge Media Institute, Open University, United Kingdom
 */
public interface BSAuthListener extends EventListener {
    /** Called when authentication error occured */
    public void authError(BSAuthEvent ae);
    
    /** Called when authentication succeeded */
    public void authorized(BSAuthEvent ae);
    
    /** Called when authentication moved, but still in progress */
    public void authorizing(BSAuthEvent ae);
}
