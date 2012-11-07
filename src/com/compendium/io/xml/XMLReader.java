/********************************************************************************
 *                                                                              *
 *  (c) Copyright 2010 Verizon Communications USA and The Open University UK    *
 *                                                                              *
 *  This software is freely distributed in accordance with                      *
 *  the GNU Lesser General Public (LGPL) license, version 3 or later            *
 *  as published by the Free Software Foundation.                               *
 *  For details see LGPL: http://www.fsf.org/licensing/licenses/lgpl.html       *
 *               and GPL: http://www.fsf.org/licensing/licenses/gpl-3.0.html    *
 *                                                                              *
 *  This software is provided by the copyright holders and contributors "as is" *
 *  and any express or implied warranties, including, but not limited to, the   *
 *  implied warranties of merchantability and fitness for a particular purpose  *
 *  are disclaimed. In no event shall the copyright owner or contributors be    *
 *  liable for any direct, indirect, incidental, special, exemplary, or         *
 *  consequential damages (including, but not limited to, procurement of        *
 *  substitute goods or services; loss of use, data, or profits; or business    *
 *  interruption) however caused and on any theory of liability, whether in     *
 *  contract, strict liability, or tort (including negligence or otherwise)     *
 *  arising in any way out of the use of this software, even if advised of the  *
 *  possibility of such damage.                                                 *
 *                                                                              *
 ********************************************************************************/

package com.compendium.io.xml;

import java.io.*;
import java.util.*;

import org.w3c.dom.*;
import org.apache.xerces.parsers.*;
import org.xml.sax.*;

/**
 * XMLReader has a method for reading in an xml text file and returning the Document object created
 *
 * @author	Michelle Bachler
 */
public class XMLReader implements ErrorHandler {

	/** Constructor, does nothing*/
	public XMLReader() {}

	/**
	 * Read in the given xml file and return the resultant document, or throw an exception
	 *
	 * @param String fileName, the name of the xml file to read;
	 * @return The Document object resulting from reading the xml file.
	 */
    public Document read( String fileName, boolean validate ) throws Exception {

		DOMParser parser = new DOMParser();
       	parser.setErrorHandler(this);

   		boolean setDeferredDOM   = true;

       	parser.setFeature( "http://apache.org/xml/features/dom/defer-node-expansion", setDeferredDOM ); //$NON-NLS-1$
      	parser.setFeature( "http://xml.org/sax/features/validation", validate ); //$NON-NLS-1$
       	parser.setFeature( "http://apache.org/xml/features/validation/schema", validate ); //$NON-NLS-1$

		FileInputStream file = new FileInputStream( fileName );
		InputSource input = new InputSource( (InputStream)file );

	   	Document document = null;
		try {
			parser.parse(input);			
 	    	document = parser.getDocument();
			file.close();
		}
		catch(Exception io) {
			io.printStackTrace();
		}

		return document;
    }

	/**
	 * Read in the given xml string and return the resultant document, or throw an exception
	 *
	 * @param String text, the xml text string to read;
	 * @return The Document object resulting from reading the xml text.
	 */
    public Document readText( String text, boolean validate ) throws Exception {

		DOMParser parser = new DOMParser();
       	parser.setErrorHandler(this);

   		boolean setDeferredDOM   = true;

       	parser.setFeature( "http://apache.org/xml/features/dom/defer-node-expansion", setDeferredDOM ); //$NON-NLS-1$
      	parser.setFeature( "http://xml.org/sax/features/validation", validate ); //$NON-NLS-1$
       	parser.setFeature( "http://apache.org/xml/features/validation/schema", validate ); //$NON-NLS-1$

		StringReader reader = new StringReader(text);
	   	Document document = null;
		try {
			parser.parse( new InputSource( reader ) );
 	    	document = parser.getDocument();
			reader.close();
		}
		catch(Exception io) {
			io.printStackTrace();
		}

		return document;
    }

	/**
	 * Find the child nodes of the given Node with the given tag name.
	 *
	 * @param Node node, the node whose children to search;
	 * @param String tagName, the tag name to search for.
	 *
	 * @return a Vector of Nodes with the given tag name.
	 */
	public static Vector getChildrenWithTagName(Node node, String tagName) {
		Vector data = new Vector(10);

		NodeList children = node.getChildNodes();
		if (children != null) {

			int count = children.getLength();
			for (int i=0; i< count; i++) {
				Node next = children.item(i);
				String name = next.getNodeName();
				if (name.equals(tagName))
					data.addElement(next);
			}
		}

		return data;
	}

	/**
	 * Find the first child Node of the given Node with the given tag name.
	 *
	 * @param Node node, the node whose children to search;
	 * @param String tagName, the tag name to search for.
	 *
	 * @return the first Node with the given tag name.
	 */
	public static Node getFirstChildWithTagName(Node node, String tagName) {

		Node child = null;

		NodeList children = node.getChildNodes();
		if (children != null) {

			int count = children.getLength();
			for (int i=0; i< count; i++) {
				Node next = children.item(i);
				String name = next.getNodeName();
				if (name.equals(tagName))
					return next;
			}
		}

		return child;
	}

	// ERROR HANLDER
	/**
	 * Thrown when a warning is triggered by the parser.
	 *
	 * @param SAXParseException ex, the exception being thrown by the parser.
	 */
    public void warning(SAXParseException ex) {
        System.err.println("[Warning] "+ getLocationString(ex)+": "+ ex.getMessage()); //$NON-NLS-1$ //$NON-NLS-2$
    }

	/**
	 * Thrown when an error is triggered by the parser.
	 *
	 * @param SAXParseException ex, the exception being thrown by the parser.
	 */
    public void error(SAXParseException ex) {
        System.err.println("[Error] "+ getLocationString(ex)+": "+ ex.getMessage()); //$NON-NLS-1$ //$NON-NLS-2$
    }

	/**
	 * Thrown when a fatal error is triggered by the parser.
	 *
	 * @param SAXParseException ex, the exception being thrown by the parser.
	 * @exception org.xml.sax.SAXException.
	 */
    public void fatalError(SAXParseException ex) throws SAXException {
        System.err.println("[Fatal Error] "+ getLocationString(ex)+": "+ ex.getMessage()); //$NON-NLS-1$ //$NON-NLS-2$
        throw ex;
    }

	/**
	 * Return a String representation of the SAXParseException details.
	 *
	 * @param SAXParseException ex, the exception being thrown by the parser.
	 * @return String, a String representation of rthe Exception details.
	 */
    private String getLocationString(SAXParseException ex) {

        StringBuffer str = new StringBuffer();

        String systemId = ex.getSystemId();
        if (systemId != null) {
            int index = systemId.lastIndexOf('/');
            if (index != -1)
                systemId = systemId.substring(index + 1);
            str.append(systemId);
        }
        str.append(':');
        str.append(ex.getLineNumber());
        str.append(':');
        str.append(ex.getColumnNumber());

        return str.toString();
    }
}
