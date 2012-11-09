/********************************************************************************
 *                                                                              *
 *  (c) Copyright 2009 Verizon Communications USA and The Open University UK    *
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

package com.compendium.ui.linkgroups;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;
import javax.swing.plaf.ComponentUI;

import com.compendium.*;
import com.compendium.core.*;
import com.compendium.core.datamodel.*;
import com.compendium.ui.*;


/**
 * This class manages a link group.
 *
 * @author	Michelle Bachler
 * @version	1.0
 */
public class UILinkGroup extends Component { // extends Compoenent For sorting only

	/** the parent link group manager for this link group.*/
	private UILinkGroupManager 	oParent 		= null;

	/** The name of this link group.*/
	private String 				sName 			= "";

	/** The filename of this link group.*/
	private String 				sFileName 		= "";

	/** The unique if of this link group.*/
	private String 				sID 			= "";

	/** The unique if of the default link type for this group.*/
	private String 				sDefaultID 		= "";


	/** A list of the UILinkType object in this link group.*/
	private Vector 				vtItems 			= new Vector(10);

	/**
	 * Constructor. Create a new instance of UILinkGroup, for a new link group.
	 * @param oParent, the parent UILinkGroupManager object for this link group.
	 */
	public UILinkGroup(UILinkGroupManager oParent) {
		this.oParent = oParent;
	}

	/**
	 * Constructor. Create a new instance of UILinkGroup, for a a new link group.
	 *
	 * @param oParent, the parent UILinkGroupManager object for this link group.
	 * @param dbID, the unique id for this link group.
	 * @param dbDefaultID, the unique id for the default link type for this group.
	 * @param sfileName, the filename of this link group.
	 * @param sName, the name of this link group.
	 */
	public UILinkGroup(UILinkGroupManager oParent, String sID, String sDefaultID, String sFileName, String sName) {
		this.sID = sID;
		this.sDefaultID = sDefaultID;
		this.sName = sName;
		this.sFileName = sFileName;
		this.oParent = oParent;
	}

	/**
	 * Return the id associated with this link group.
	 * @return String, the id of this link group.
	 */
	public String getID() {
		return sID;
	}

	/**
	 * Set the id associated with this link group.
	 * @param sID, the id associated with this link group.
	 */
	public void setID(String sID) {
		this.sID = sID;
	}

	/**
	 * Return the id associated with the default link type for this group.
	 * @return String, the id of the default link type for this group.
	 */
	public String getDefaultLinkTypeID() {
		return sDefaultID;
	}

	/**
	 * Set the id associated with this default link type for this group.
	 * @param dbDefaultID, the id associated with this link type for this group.
	 */
	public void setDefaultLinkTypeID(String sDefaultID) {
		this.sDefaultID = sDefaultID;
	}

	/**
	 * Set the name for this link group.
	 * @param sName, the name of this link group.
	 */
	public void setName(String sName) {
		this.sName = sName;
		if (this.sFileName.equals(""))
			this.sFileName = (CoreUtilities.cleanFileName(sName))+".xml";
	}

	/**
	 * Get the name for this link group.
	 * @return String, the name of this link group.
	 */
	public String getName() {
		return sName;
	}

	/**
	 * Get the file name for this link group.
	 * @return String, the file name of this link group.
	 */
	public String getFileName() {
		return sFileName;
	}

	/**
	 * Get the current item list for this .
	 * @return Vector, the current item list for this stencil set.
	 */
	public Vector getItems() {
		return (Vector)vtItems.clone();
	}

	/**
	 * Add the given item to the list.
	 * @param item, the UILinkType to add.
	 */
	public void loadLinkType(UILinkType item) {
		vtItems.addElement(item);
	}

	/**
	 * Add the given item to the list.
	 * @param item, the UILinkType to add.
	 */
	public void addLinkType(UILinkType item) {
		if (!vtItems.contains(item)) {
			vtItems.addElement(item);
		}
		else {
			vtItems.remove(item);
			vtItems.addElement(item);
		}
	}

	/**
	 * Remove the given item to the list.
	 * @param item, the UILinkType to remove.
	 */
	public void removeLinkType(UILinkType item) {
		if (vtItems.contains(item)) {
			vtItems.remove(item);
		}
	}

	/**
	 * Returns a xml string containing the link group.
	 *
	 * @return a String object containing formatted xml representation of the link group data.
	 */
	public String getXML() {

		StringBuffer data = new StringBuffer(600);

		data.append("<?xml version=\"1.0\"?>\n");
		data.append("<!DOCTYPE linkgroup [\n");
		data.append("<!ELEMENT linkgroup (#PCDATA | linktypes)*>\n");
		data.append("<!ATTLIST linkgroup\n");
		data.append("name CDATA #REQUIRED\n");
		data.append("id CDATA #REQUIRED\n");
		data.append("default CDATA #REQUIRED\n");
		data.append(">\n");
		data.append("<!ELEMENT linktypes (#PCDATA | linktype)*>\n");
		data.append("<!ELEMENT linktype (#PCDATA)>\n");
		data.append("<!ATTLIST linktype\n");
		data.append("id CDATA #REQUIRED\n");
		data.append("name CDATA #REQUIRED\n");
		data.append("colour CDATA #REQUIRED\n");
		data.append("label CDATA #REQUIRED\n");
		data.append(">\n");
		data.append("]>\n\n");

		data.append("<linkgroup name=\""+CoreUtilities.cleanSQLText(sName, FormatProperties.nDatabaseType)+"\" id=\""+sID+"\" default=\""+sDefaultID.toString()+"\">\n");
		data.append("\t<linktypes>\n");

		int count = vtItems.size();
		for (int i=0; i<count; i++) {
			UILinkType item = (UILinkType)vtItems.elementAt(i);
			data.append(item.getXML());
		}

		data.append("\t</linktypes>\n");
		data.append("</linkgroup>\n");

		return data.toString();
	}

	/**
	 * Delete this link group.
	 */
	public void delete() {
		File file = new File("LinkGroups"+ProjectCompendium.sFS+sFileName);
		if (!CoreUtilities.deleteFile(file)) {
			ProjectCompendium.APP.displayError("Unable to find the link group file:\n\n"+sFileName+"\n\nto delete it.\n\n");
		}
	}

	/**
	 * Rename the link group xml file after a name change and save data.
	 * @param sNewName, The new name for this stencil set.
	 */
	public void saveToNew(String sNewName) {

		// DELETE THE OLD FILE
		delete();

		// SAVE THE XML TO THE NEW FILE
		sFileName = (CoreUtilities.cleanFileName(sNewName))+".xml";
		saveLinkGroupData();
	}

	/**
	 * Save this link group to an xml file.
	 */
	public void saveLinkGroupData() {

		// SAVE THE XML FILE
		String data = getXML();
		try {
			FileWriter fileWriter = new FileWriter("System"+ProjectCompendium.sFS+"resources"+ProjectCompendium.sFS+"LinkGroups"+ProjectCompendium.sFS+sFileName);
			fileWriter.write(data);
			fileWriter.close();
		}
		catch (IOException e) {
			ProjectCompendium.APP.displayError("Exception: (UILinkGroup.saveLinkGroupData) \n\n" + e.getMessage());
		}
	}

	/**
	 * Make a duplicate of this object but with a new id.
	 */
	public UILinkGroup duplicate(String sNewName) {

		String id = ProjectCompendium.APP.getModel().getUniqueID();

		UILinkGroup oLinkGroup = new UILinkGroup(oParent);
		oLinkGroup.setID(id);
		oLinkGroup.setName(sNewName);

		Vector items = getItems();
		int count = items.size();
		for (int i=0; i<count; i++) {
			UILinkType type = (UILinkType)items.elementAt(i);
			UILinkType dup = type.duplicate();
			if ( (type.getID()).equals(sDefaultID)) {
				oLinkGroup.setDefaultLinkTypeID(dup.getID());
			}
			oLinkGroup.loadLinkType(dup);
		}

		return oLinkGroup;
	}
}
