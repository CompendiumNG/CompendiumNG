package com.compendium.io.jabber;

/*
 * BSLoginListener.java
 *
 * Project: BuddySpace
 * (C) Copyright Knowledge Media Institute 2002
 *
 *
 * Created on 21 November 2002, 16:27
 */

import org.jabber.jabberbeans.*;

/**
 * <code>BSLoginListener</code> is interface you can implement to get
 * login events notifications from BSCore.
 *
 * @author  Jiri Komzak, Knowledge Media Institute, Open University, United Kingdom
 */
public interface BSLoginListener {
    /** Called when login failes */
    public void loginError(InfoQuery iq);
    /** Called when login failes */
    public void loginError(String error);
    /** Called when registered */
    public void loginRegistered();
    /** Called when authorized */
    public void loginAuthorized();
}
