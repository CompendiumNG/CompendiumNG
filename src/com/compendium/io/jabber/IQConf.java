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
 * Changed into IQConf
 */

package com.compendium.io.jabber;

/*
 * IQConf.java
 *
 * Project: BuddySpace
 * (C) Copyright Knowledge Media Institute 2002
 *
 *
 * Created on 12 September 2002, 10:40
 */

import java.util.*;

import org.jabber.jabberbeans.*;
import org.jabber.jabberbeans.Extension.*;

/**
 * An <code>IQConf</code> object represents the jabber:iq:conf
 * namespace.
 *
 * @author  Jiri Komzak, Knowledge Media Institute, Open University, United Kingdom
 */
public class IQConf
    extends XMLData
    implements QueryExtension
{
    private String password;

    private boolean privacy;

    private String roomName;
    
    private Vector nicks;
    
    private String id;

    /** Creates a new <code>IQConf</code> instance.
     */
    public IQConf(IQConfBuilder builder)
        throws InstantiationException
    {
        password = builder.getPassword();
        privacy = builder.getPrivacy();

        roomName = builder.getRoomName();
        nicks = (Vector) builder.getNicks();
        
        id = builder.getID();
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
     * <code>appendItem</code> appends the XML representation of the
     * current packet data to the specified <code>StringBuffer</code>.
     *
     * @param retval The <code>StringBuffer</code> to append to
     */
    public void appendItem(StringBuffer retval)
    {
        retval.append("<query xmlns=\"jabber:iq:conference\">");
        
	Enumeration e = nicks.elements();
        while (e.hasMoreElements()) {
            String n = (String) e.nextElement();
            appendChild(retval, "nick", n);
        }
        
        if (password != null)
            appendChild(retval, "secret", password);
	appendChild(retval, "privacy", privacy);
	if (roomName != null)
            appendChild(retval, "name", roomName);
	if (id != null)
            appendChild(retval, "id", id);
	
	retval.append("</query>");
    }
}
