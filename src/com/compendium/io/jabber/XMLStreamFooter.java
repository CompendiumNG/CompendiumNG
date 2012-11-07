package com.compendium.io.jabber;
/*
 * XMLStreamFooter.java
 *
 * Project: BuddySpace
 * (C) Copyright Knowledge Media Institute 2002
 *
 *
 * Created on 7 August 2002, 13:21
 */

import org.jabber.jabberbeans.Packet;

/**
 * Class which represents an XMLStream footer - this closes whole
 * communication with jabber server. It is <code>&lt/stream:stream&gt</code>.
 *
 * @author  Jiri Komzak, Knowledge Media Institute, Open University, United Kingdom
 */
public class XMLStreamFooter implements Packet {
    
    /**
     * <code>appendItem</code> appends the XML representation of the
     * XML footer to the specified <code>StringBuffer</code>.
     *
     * @param retval The <code>StringBuffer</code> to append to
     */
    public void appendItem(StringBuffer retval) {
        retval.append("</stream:stream>");
    }
    
    /**
     * <code>toString</code> returns string representation of the tag.
     */
    public String toString() {
        return new String("</stream:stream>");
    }
}
