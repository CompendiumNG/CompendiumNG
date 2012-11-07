/*
 *   License
 *
 * The contents of this file are subject to the Jabber Open Source License
 * Version 1.0 (the "License").  You may not copy or use this file, in either
 * source code or executable form, except in compliance with the License.  You
 * may obtain a copy of the License at http://www.jabber.com/license/ or at
 * http://www.opensource.org/.  
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied.  See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 *   Copyrights
 *
 * Portions created by or assigned to Jabber.com, Inc. are 
 * Copyright (c) 2000 Jabber.com, Inc.  All Rights Reserved.  Contact
 * information for Jabber.com, Inc. is available at http://www.jabber.com/.
 *
 * Portions Copyright (c) 1999-2000 David Waite 
 *
 *   Acknowledgements
 * 
 * Special thanks to the Jabber Open Source Contributors for their
 * suggestions and support of Jabber.
 * 
 *   Changes
 *
 * @author  David Waite <a href="mailto:dwaite@jabber.com">
 *                      <i>&lt;dwaite@jabber.com&gt;</i></a>
 * @author  $Author: jkk25 $
 * @version $Revision: 1.2 $
 *
 * j.komzak
 * Changed into IQConfBuilder
 */

package com.compendium.io.jabber;

/*
 * IQConfBuilder.java
 *
 * Project: BuddySpace
 * (C) Copyright Knowledge Media Institute 2002
 *
 *
 * Created on 12 September 2002, 10:20
 */

import java.util.*;

import org.jabber.jabberbeans.*;
import org.jabber.jabberbeans.Extension.*;

/**
 * <code>IQConfBuilder</code> is the class used for building an
 * IQConf.
 *
 * @author  Jiri Komzak, Knowledge Media Institute, Open University, United Kingdom
 */
public class IQConfBuilder
    implements ExtensionBuilder
{
    private String password;

    private boolean privacy;

    private String roomName;

    private Vector nicks;
    
    private String id;

    /**
     * Creates a new <code>IQConfBuilder</code> instance.
     */
    public IQConfBuilder() {
        nicks = new Vector();
        reset(); 
    }

    /**
     * <code>reset</code> restores the variables to their default state, so
     * that this builder may be reused.
     */
    public void reset() { 
	password = roomName = id = null;
        privacy = false;
	nicks.clear();
    }

    /**
     * <code>getPassword</code> returns the value of the password attribute
     */
    public String getPassword()
    {
        return password;
    }

    /**
     * <code>getPrivacy</code> returns the value of the privacy attribute
     */
    public boolean getPrivacy()
    {
        return privacy;
    }

    /**
     * <code>getRoomName</code> returns the value of the room name attribute
     */
    public String getRoomName()
    {
        return roomName;
    }

    /**
     * <code>getNicks</code> returns the value of the nicks attribute
     */
    public Vector getNicks()
    {
        return nicks;
    }
    
    /**
     * <code>getID</code> returns the value of the id attribute
     */
    public String getID()
    {
        return id;
    }

    /**
     * <code>setPassword</code> sets the value of the password attribute
     */
    public void setPassword(String password)
    { this.password = password; }

    /**
     * <code>setPrivacy</code> sets the value of the privacy attribute
     */
    public void setPrivacy(boolean privacy)
    { this.privacy = privacy; }

    /**
     * <code>setRoomName</code> sets the value of the room name attribute
     */
    public void setRoomName(String roomName)
    { this.roomName = roomName; }

    /**
     * <code>addNick</code> adds a nick into nicks attribute
     */
    public void addNick(String nick) {
        if (!nicks.contains(nick))
            nicks.add(nick);
    }

    /**
     * <code>removeNick</code> removes a nick into nicks attribute
     */
    public void removeNick(String nick) {
        nicks.remove(nick);
    }

    /**
     * <code>setID</code> sets the value of the id attribute
     */
    public void setID(String id)
    { this.id = id; }

    /**
     * <code>build</code> generates a new extension object based on the 
     * information stored in this class.
     *
     * @return an <code>Extension</code> value
     * @exception InstantiationException if insufficient or malformed
     * information was provided
     */
    public Extension build()
        throws InstantiationException
    { return new IQConf(this); }
}
