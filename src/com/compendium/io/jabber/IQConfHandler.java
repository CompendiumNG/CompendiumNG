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
 *
 * j.komzak
 * Changed into IQConfHandler
 */

package com.compendium.io.jabber;

/*
 * IQConfHandler.java
 *
 * Project: BuddySpace
 * (C) Copyright Knowledge Media Institute 2002
 *
 *
 * Created on 12 September 2002, 10:20
 */

import org.jabber.jabberbeans.Extension.*;
import org.jabber.jabberbeans.sax.SubHandler;
import org.xml.sax.SAXException;
import org.xml.sax.AttributeList;

/**
 * Handler class to build jabber:iq:conf objects
 *
 * @author  Jiri Komzak, Knowledge Media Institute, Open University, United Kingdom
 */
public class IQConfHandler
      extends SubHandler
{
    /** used to capture data between element tags */
    private StringBuffer elementChars;

    /** builder for IQConf objects */
    private IQConfBuilder builder;

    /** elements within the jabber:iq:conf namespace */
    private static final String elementType[]=
    {
	"nick", 
	"secret", 
	"privacy", 
	"name", 
     "id" 
    };

    /**
     * Creates a new <code>IQConfHandler</code> instance.
     */
    public IQConfHandler()
    {
	builder = new IQConfBuilder();
    }


    /**
     * This is an exact copy of the characters function in the main handler
     *
     * @param ch character string detected
     * @param start start position
     * @param length length of string
     * @exception SAXException thrown on error
     */
    public void characters(char[] ch, int start, int length)
	throws SAXException
    {elementChars.append(ch,start,length);}

    /**
     * Gets called when the underlying engine decides to pass an entity and
     * all sub-entities off to your subhandler.<p>
     *
     * Upon seeing the element that this subhandler handles, we call this
     * constructor, passing in the attributes.
     *
     * @param name name of the element which we are handling.
     * @param attributes list of attributes on this element
     */
    protected void startHandler(String name,AttributeList attributes)
	throws SAXException
    {
	elementChars=new StringBuffer();
	builder.reset();
    }

    /**
     * <code>handleStartElement</code> is overloaded by the new class to
     * provide logic to handle the element code.
     *
     * @param name a <code>String</code> value
     * @param attributes an <code>AttributeList</code> value
     * @exception SAXException if an error occurs
     */
    protected void handleStartElement(String name,
				      AttributeList attributes)
	throws SAXException
    {
	//we have no attributes, so all we do is make sure the buffer is
	//reset for reading in character data.
	elementChars = new StringBuffer();
    }

    /**
     * <code>handleEndElement</code> is overloaded by the new class to
     * provide logic to handle element code.
     *
     * @param name a <code>String</code> value
     * @exception SAXException if an error occurs
     */
    protected void handleEndElement(String name)
	throws SAXException
    {
        if (name.equals(elementType[0]))
	    builder.addNick(new String(elementChars));
	else if (name.equals(elementType[1]))
	    builder.setPassword(new String(elementChars));
	else if (name.equals(elementType[2]))
	    builder.setPrivacy(true);
	else if (name.equals(elementType[3]))
	    builder.setRoomName(new String(elementChars));
        else if (name.equals(elementType[4]))
	    builder.setID(new String(elementChars));
    }

    /**
     * Stophandler is the same as end element, except that it is called saying
     * that the subhandler is no longer in scope.
     *
     * @param Object a value being returned to the parent - the parent is
     * meant to interpret this result.
     */
    protected Object stopHandler(String name)
	throws SAXException
    {
	try
	{
	    return builder.build();
	}
	catch (InstantiationException e)
	{
	    // if there is a problem building, throw a SAXException with the
	    // error internal.
	    e.fillInStackTrace();
	    throw new SAXException(e);
	}
    }

    /**
     * <code>receiveChildData</code> is called when a child handler exits,
     * returning control to this code. The now-defunct handler along with the
     * data object are both returned.
     *
     * @param subHandler a <code>SubHandler</code> value
     * @param o an <code>Object</code> value
     */
    protected void receiveChildData(SubHandler subHandler,Object o)
    {//do nothing, since we have no children.
    }
}
