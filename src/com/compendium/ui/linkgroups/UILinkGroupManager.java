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
import java.io.*;
import java.util.*;
import java.sql.SQLException;
import java.net.*;

import javax.help.*;
import javax.help.Map.ID;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;
import javax.swing.plaf.ComponentUI;

import org.w3c.dom.*;

import com.compendium.core.*;
import com.compendium.*;

import com.compendium.ui.*;
import com.compendium.ui.panels.*;
import com.compendium.io.*;
import com.compendium.io.xml.*;

import com.compendium.core.datamodel.*;


/**
 * This class manages all the stencils
 *
 * @author	Michelle Bachler
 * @version	1.0
 */
public class UILinkGroupManager implements IUIConstants, ICoreConstants {

	/**A reference to the system file path separator*/
	private final static String	sFS					= System.getProperty("file.separator");

	/** A list of all stencils.*/
	private Hashtable htLinkGroups = new Hashtable(10);

	/** The parent frame for this class.*/
	private ProjectCompendiumFrame	oParent			= null;

	/** The HelpSet to use for toolbar help.*/
	private HelpSet 				mainHS 			= null;

	/** The HelpBroker to use for toolbar help.*/
	private HelpBroker 				mainHB			= null;

	/** Holds references against the link type id of all UILinkType objects loaded.*/
	private Hashtable				htLinkTypes		= new Hashtable(51);

	/** Holds the data for the tree of groups and their link types.*/
    DefaultMutableTreeNode 			topNode = new DefaultMutableTreeNode();

	/**
	 * Constructor. Create a new instance of UIStencilManager, with the given proerties.
	 * @param parent com.compendium.ui.ProjectCompendiumFrame, the parent frame for the application.
	 * @param hs, the helpset to use.
	 * @param hb, the HelpBroker to use.
	 */
	public UILinkGroupManager(ProjectCompendiumFrame parent, HelpSet hs, HelpBroker hb) {

		oParent = parent;
		mainHS = hs;
		mainHB = hb;
	}

	/**
	 * Update the look and feel of all the htLinkGroups
	 */
	public void updateLAF() {

	}

	/**
	 * Return the link type for the given id else null.
	 * @param sID, the id of the link type to return.
	 * @return UILinkType, the link type if found else null;
	 */
	public UILinkType getLinkType(String sID) {
		if (htLinkTypes.containsKey(sID))
			return (UILinkType)htLinkTypes.get(sID);

		return null;
	}

	/**
	 * Return a list of all the link groups currently existing.
	 * @param Vector, list of all the link groups.
	 */
	public Vector getLinkGroups() {

		Vector groups = new Vector(htLinkGroups.size());
		for(Enumeration e = htLinkGroups.elements();e.hasMoreElements();) {
	       	UILinkGroup group = (UILinkGroup)e.nextElement();
			groups.addElement(group);
		}
		return groups;
	}

	/**
	 * Return the link group with the given id.
	 * @param sID, the id of the link group.
	 * @return UILinkGroup, the group, if found, else null.
	 */
	public UILinkGroup getLinkGroup(String sID) {
		UILinkGroup group = null;
		for(Enumeration e = htLinkGroups.elements();e.hasMoreElements();) {
	       	UILinkGroup item = (UILinkGroup)e.nextElement();
			if (item.getID().equals(sID))
				return item;
		}
		return group;
	}

	/**
	 * Check the passed link group name to see if it already exists.
	 * @param sName, the name to check.
	 * @return boolean, true if the name has already been used, else false;
	 */
	public boolean checkName(String sName) {
		boolean exists =  false;
		for(Enumeration e = htLinkGroups.keys();e.hasMoreElements();) {
	       	String name = (String)e.nextElement();
			if (sName.equals(name))
				return true;
		}
		return exists;
	}

	/**
	 * Add a link group to this manager's list.
	 * @param oLinkGroup, the stencil to add.
	 */
	public void addLinkGroup(String sOldName, UILinkGroup oLinkGroup) {

		if (!htLinkGroups.containsKey(sOldName)) {
			htLinkGroups.put(oLinkGroup.getName(), oLinkGroup);
		}
		else {
			htLinkGroups.remove(sOldName);
			htLinkGroups.put(oLinkGroup.getName(), oLinkGroup);
		}

		Vector items = oLinkGroup.getItems();
		int count = items.size();
		for (int i=0; i<count; i++) {
			UILinkType type = (UILinkType)items.elementAt(i);
			htLinkTypes.put(type.getID(), type);
		}

		refreshTree();
	}

	/**
	 * Remove a link group from this manager's list.
	 * @param oLinkGroup, the link group to remove.
	 * @return boolean, true if added, else false.
	 */
	public boolean removeLinkGroup(UILinkGroup oLinkGroup) {
		boolean removed = false;
		String sName = oLinkGroup.getName();
		if (htLinkGroups.containsKey(sName)) {
			htLinkGroups.remove(sName);
			removed = true;
		}

		refreshTree();

		return removed;
	}

	/**
	 * Refresh the link group tree of data to reflect current data state.
	 */
	public void refreshTree() {

		topNode = null;

		UILinkGroup oLinkGroup = new UILinkGroup(this);
		oLinkGroup.setName("Link Groups");
        topNode = new DefaultMutableTreeNode(oLinkGroup);

		Vector groups = getLinkGroups();
		int count = groups.size();

		DefaultMutableTreeNode link = null;
		DefaultMutableTreeNode groupNode = null;

		for (int i=0; i<count; i++) {
			UILinkGroup group = (UILinkGroup)groups.elementAt(i);
		    groupNode = new DefaultMutableTreeNode(group);

			Vector items = group.getItems();
			int jcount = items.size();
			for (int j=0; j<jcount; j++) {
				UILinkType item = (UILinkType)items.elementAt(j);
                link = new DefaultMutableTreeNode(item);
                groupNode.add(link);
			}

		    topNode.add(groupNode);
		}

		UILinkEditPanel.setLinkTypeTopTreeNode(topNode);
	}

	/**
	 * Load the link group data from the XML files where it has been save.
	 */
	public void loadLinkGroups() {
		htLinkGroups.clear();

		try {
			File main = new File("System"+sFS+"resources"+sFS+"LinkGroups");
			File oLinkGroups[] = main.listFiles();

			for (int i=0; i< oLinkGroups.length; i++) {
				File nextLinkGroup = oLinkGroups[i];
				String linkGroupName = nextLinkGroup.getName();
				if (linkGroupName.endsWith(".xml")) {
					loadFile(nextLinkGroup.getAbsolutePath(), linkGroupName);
				}
			}

			refreshTree();
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Load the link group data for the given filename.
	 * @param sFullPath, the path of the file to load.
	 * @param sFileName, the filename for this link group.
	 */
	public void loadFile(String sFullPath, String sFileName) throws Exception {
		XMLReader reader = new XMLReader();
		Document document = reader.read(sFullPath, true);
		Node data = document.getDocumentElement();

		if (document == null)
			System.out.println("Link Group data could not be loaded for "+sFileName);

		NamedNodeMap attrs = null;

		if (data == null)
			throw new Exception("Link Group "+sFileName+" could not be loaded");

		storeLinkGroup(data, sFileName);
	}

	/**
	 * Store link group data against name.
	 * @param data, the Node XML data object for the link group to create.
	 * @param sFileName, the filename for this link group.
	 */
	private void storeLinkGroup(Node node, String sFileName) {
		NamedNodeMap attrs = node.getAttributes();
		Attr name = (Attr)attrs.getNamedItem("name");
		String sName = new String(name.getValue());

		Attr oID = (Attr)attrs.getNamedItem("id");
		String sID = oID.getValue();

		Attr oDefaultID = (Attr)attrs.getNamedItem("default");
		String sDefaultID = oDefaultID.getValue();

		UILinkGroup oLinkGroup = createLinkGroup(node, sID, sDefaultID, sName, sFileName);
		addLinkGroup(sName, oLinkGroup);
	}

	/**
	 * Create a link group panel from the given data.
	 * @param node, the Node XML data object for the link group to create
	 * @param nGroupID, the id og the group.
	 * @param nDefaultTypeID, the id of the default link type for the group.
	 * @param sName, the name of the link group.
	 * @param sFileName, the filename for this link group.
	 */
	private UILinkGroup createLinkGroup(Node node, String sGroupID, String sDefaultTypeID, String sName, String sFileName) {

		UILinkGroup oLinkGroup = null;

		if (node.hasChildNodes()) {

			oLinkGroup = new UILinkGroup(this, sGroupID, sDefaultTypeID, sFileName, sName);
			Node items = XMLReader.getFirstChildWithTagName(node, "linktypes");

			Vector vtItems = XMLReader.getChildrenWithTagName(items, "linktype");
			if (vtItems.size() > 0) {
				int count = vtItems.size();
				for (int i=0; i<count; i++) {
					Node child = (Node)vtItems.elementAt(i);
					NamedNodeMap attrs = child.getAttributes();

					Attr oID = (Attr)attrs.getNamedItem("id");
					String sID = oID.getValue();

					Attr oLinkName = (Attr)attrs.getNamedItem("name");
					String sLinkName = new String(oLinkName.getValue());

					Attr oColour = (Attr)attrs.getNamedItem("colour");
					int nColour = (new Integer(oColour.getValue())).intValue();

					Color oCol = new Color(nColour);

					Attr oLabel = (Attr)attrs.getNamedItem("label");
					String sLabel = new String(oLabel.getValue());

					UILinkType item = new UILinkType(sLinkName, oCol, sID, sLabel);
					htLinkTypes.put(sID, item);

					oLinkGroup.loadLinkType(item);
				}
			}
		}
		return oLinkGroup;
	}

	/**
	 * Create the default link group with the default link types.
	 */
	public void createDefaultLinkGroup() {

		StringBuffer data = new StringBuffer(1200);

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
		data.append("]>\n");

		data.append("<linkgroup name=\"Issue-Based Information System (IBIS)\" id=\"1\" default=\"39\">\n");
		data.append("\t<linktypes>\n");		
		data.append("\t\t<linktype id=\"39\" name=\"Responds To\" colour=\"-13434727\" label=\"\"/>\n");
		data.append("\t\t<linktype id=\"40\" name=\"Supports\" colour=\"-16711936\" label=\"\"/>\n");
		data.append("\t\t<linktype id=\"41\" name=\"Objects To\" colour=\"-65536\" label=\"\"/>\n");
		data.append("\t\t<linktype id=\"42\" name=\"Challenges\" colour=\"-20561\" label=\"\"/>\n");
		data.append("\t\t<linktype id=\"43\" name=\"Specializes\" colour=\"-16776961\" label=\"\"/>\n");
		data.append("\t\t<linktype id=\"44\" name=\"Expands On\" colour=\"-14336\" label=\"\"/>\n");
		data.append("\t\t<linktype id=\"45\" name=\"Related To\" colour=\"-16777216\" label=\"\"/>\n");
		data.append("\t\t<linktype id=\"46\" name=\"About\" colour=\"-16711681\" label=\"\"/>\n");
		data.append("\t\t<linktype id=\"47\" name=\"Resolves\" colour=\"-8355712\" label=\"\"/>\n");
		data.append("\t</linktypes>\n");
		data.append("</linkgroup>\n");

		try {
			FileWriter fileWriter = new FileWriter("System"+sFS+"resources"+sFS+"LinkGroups"+sFS+"Default.xml");
			fileWriter.write(data.toString());
			fileWriter.close();
			loadFile("System"+sFS+"resources"+sFS+"LinkGroups"+sFS+"Default.xml", "Default.xml");
		}
		catch (Exception e) {
			ProjectCompendium.APP.displayError("Exception: (UILinkGroup.createDefaultLinkGroup) \n\n" + e.getMessage());
		}
	}
}
