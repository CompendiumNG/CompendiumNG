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


package com.compendium.io;

import java.util.*;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;

import com.compendium.core.ICoreConstants;
import com.compendium.core.datamodel.*;

import com.compendium.ProjectCompendium;
import com.compendium.ui.plaf.*;
import com.compendium.ui.*;


/**
 * ShorthandParser contains code for creating nodes from specific syntax in text blocks.
 * This can be either from a Jabber client messsage or the details panel of a node
 *
 * @author	Michelle Bachler
 */

// NOTE: THIS CLASS IS STILL UNDER DEVELOPMENT
public class ShorthandParser implements IUIConstants {

	/** FOR FUTURE USE */
	private Vector 					children = new Vector(51);


	/**
	 * Constructor, does nothing.
	 */
	public ShorthandParser() {}


	/**
	 * Identify and extract any nodes in the given text
	 *
	 * @param String sText, the text to parse for nodes.
	 * @return Vector, contaning the details of the node data identified.
	 */
	private Vector extractNodes(String sText) {

		Vector nodes = new Vector(51);

		String next = sText;
		String label = ""; //$NON-NLS-1$

		int count = 1;

		while(next.length() > 0) {

			int leftBracket = next.indexOf("["); //$NON-NLS-1$
			if (leftBracket == -1 || (count == 1 && leftBracket > 0) ) {
				// BREAK UP ANY PARAGRAPHS INTO SEPARATE NODES
				int para = next.indexOf("\n\n"); //$NON-NLS-1$
				if (para != -1) {
					label = next.substring(0, para);
					label = label.trim();
					if (!label.equals("")) //$NON-NLS-1$
						nodes.addElement(label);
					next = next.substring(para+2);
				}
				else {
					label = next;
					label = label.trim();
					if (!label.equals("")) //$NON-NLS-1$
						nodes.addElement(label);
					next=""; //$NON-NLS-1$
				}
			}
			else {
				while (next.length() > 0) {

					int rightBracket = next.indexOf("]"); //$NON-NLS-1$
					int nextBracket = next.indexOf("[", rightBracket); //$NON-NLS-1$

					if (nextBracket != -1) {
						label = next.substring(0, nextBracket);
						label = label.trim();
						nodes.addElement(label);
						next = next.substring(nextBracket);
					}
					else {
						label = next;
						label = label.trim();
						nodes.addElement(label);
						next = ""; //$NON-NLS-1$
					}
				}
			}


			count++;
		}

		return nodes;
	}

	/**
	 * Create Nodes from the given text.
	 *
	 * @param String text, the text to parse for possible nodes.
 	 * @param UIViewFrame frame, the frame containing the view into which to create the new nodes.
	 * @param String detail, any additional text to be added into the detail of the new nodes.
	 * @param int xPos, the X position at which to start creating the new nodes.
	 * @param int yPos, the Y position at which to start creating the new nodes.
	 */
	public void createNodes(String text, UIViewFrame frame, String detail, int xPos, int yPos) {

		Vector nodes = extractNodes(text);

		UIViewPane oViewPane = ((UIMapViewFrame)frame).getViewPane();
		oViewPane.setSelectedNode(null, ICoreConstants.DESELECTALL);
		oViewPane.setSelectedLink(null, ICoreConstants.DESELECTALL);

		int count = nodes.size();
		for (int i=0; i<count; i++) {
			createNode((String)nodes.elementAt(i), frame, detail, xPos, yPos);
			yPos += 80;
		}
	}

	/**
	 * Create Nodes from the given text and link them to the given parent node.
	 *
	 * @param String text, the text to parse for possible nodes.
 	 * @param UIViewFrame frame, the frame containing the view into which to create the new nodes.
 	 * @param UINode parentNode, the node to link all newly created nodes to
	 * @param String detail, any additional text to be added into the detail of the new nodes.
	 * @param int xPos, the X position at which to start creating the new nodes.
	 * @param int yPos, the Y position at which to start creating the new nodes.
	 */
	public void createNodesWithLinks(String text, UIViewFrame frame, UINode parentNode, String detail, int xPos, int yPos) {

		Vector nodes = extractNodes(text);

		UIViewPane oViewPane = ((UIMapViewFrame)frame).getViewPane();
		oViewPane.setSelectedNode(null, ICoreConstants.DESELECTALL);
		oViewPane.setSelectedLink(null, ICoreConstants.DESELECTALL);

		int count = nodes.size();
		for (int i=0; i<count; i++) {
			createNodeWithLink((String)nodes.elementAt(i), frame, parentNode, detail, xPos, yPos);
			yPos += 80;
		}
	}

	/**
	 * Identified the node type and create a new node of that type
	 *
 	 * @param String text, the text to parse to create the new node.
	 * @param UIViewFrame frame, the frame containing the view into which to create the new node.
	 * @param String detail, any additional text to be added into the detail of the new node.
	 * @param int xPos, the X position at which to create the new node.
	 * @param int yPos, the Y position at which to create the new node.
	 */
	private void createNode(String text, UIViewFrame viewFrame, String detail, int xPos, int yPos) {

		int	nodeType = getNodeType(text);

		if (nodeType > -1 && text.length() >= 1) {

			int fromPos = 3;
			String oldType = text.substring(0,1);
			if (oldType.equals("+") || oldType.equals("-") || oldType.equals("?") || oldType.equals("!")) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
				fromPos = 1;

			text = text.substring(fromPos);
			text = text.trim();
		}
		else {
			nodeType = ICoreConstants.NOTE;
		}

		if (viewFrame == null)
			viewFrame = ProjectCompendium.APP.getCurrentFrame();

		if (viewFrame instanceof UIMapViewFrame) {
			addToMap( viewFrame, nodeType, text, detail, xPos, yPos);
		}
		else {
			addToList( ((UIListViewFrame)viewFrame).getUIList(), nodeType, text, detail);
		}
	}


	/**
	 * Identified the node type and create a new node of that type
	 *
 	 * @param String text, the text to parse to create the new node.
	 * @param UIViewFrame frame, the frame containing the view into which to create the new node.
 	 * @param UINode parentNode, the node to link all newly created nodes to
	 * @param String detail, any additional text to be added into the detail of the new node.
	 * @param int xPos, the X position at which to create the new node.
	 * @param int yPos, the Y position at which to create the new node.
	 */
	private void createNodeWithLink(String text, UIViewFrame viewFrame, UINode parentNode, String detail, int xPos, int yPos) {

		int nodeType = getNodeType(text);

		if (nodeType > -1 && text.length() >= 1) {

			int fromPos = 3;
			String oldType = text.substring(0,1);
			if (oldType.equals("+") || oldType.equals("-") || oldType.equals("?") || oldType.equals("!")) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
				fromPos = 1;

			text = text.substring(fromPos);
			text = text.trim();
		}
		else {
			nodeType = ICoreConstants.NOTE;
		}

		if (viewFrame != null) {
			if (viewFrame instanceof UIMapViewFrame) {
				addToMapWithLink( viewFrame, parentNode, nodeType, text, detail, xPos, yPos);
			}
			else {
				addToList( ((UIListViewFrame)viewFrame).getUIList(), nodeType, text, detail);
			}
		}
	}

	/**
	 * Return the node type associated with the starting character/s of given String.
	 *
	 * @param String text, the text to analyse.
	 * @return int, an integer representing the node type associated with starting character/s of the given String.
	 */
	private int getNodeType(String text) {

		int nodeType = -1;

		String charType = ""; //$NON-NLS-1$
		String oldType = ""; //$NON-NLS-1$

		if (text.length() == 2) {
			charType = text.substring(0,1);
		}
		else if (text.length() >= 3) {
			charType = text.substring(0,3);
		}
		
		if (!oldType.equals("") || !charType.equals("")) { //$NON-NLS-1$ //$NON-NLS-2$
			nodeType = UINodeTypeManager.getTypeForShortcutKey(charType);
		}
		else {
			nodeType = ICoreConstants.NOTE;
		}

		return nodeType;
	}

	/*
	UNDER DEVELOPMENT
	private void extractChildren(String message) {

		while( message.length > 0) {

			String charType = message.substring(0,3);

			if (charType.equals("[+]"))
				nodeType = ICoreConstants.PRO;
			else if (charType.equals("[-]"))
				nodeType = ICoreConstants.CON;
			else if (charType.equals("[?]"))
				nodeType = ICoreConstants.ISSUE;
			else if (charType.equals("[!]"))
				nodeType = ICoreConstants.POSITION;

			else if (charType.equals("[I]"))
				nodeType = ICoreConstants.ISSUE
			else if (charType.equals("[Q]"))
				nodeType = ICoreConstants.ISSUE
			else if (charType.equals("[P]"))
				nodeType = ICoreConstants.POSITION

			else if (charType.equals("[D]"))
				nodeType = ICoreConstants.DECISION
			else if (charType.equals("[N]"))
				nodeType = ICoreConstants.NOTE;
			else if (charType.equals("[R]"))
					nodeType = IUIConstants.REFERENCE;
			else if (charType.equals("[A]"))
				nodeType = IUIConstants.ARGUMENT;
		}
	}
	*/

	/**
	 * Create a new node and add it to a map
	 *
	 * @param UIViewFrame frame, the frame containing the map into which to place the new node.
	 * @param int nodeType, the type of the new node to be created
 	 * @param String text, the text for the label of the new node.
	 * @param String detail, any additional text to be added into the detail of the new node.
	 * @param int xPos, the X position at which to create the new node.
	 * @param int yPos, the Y position at which to create the new node.
	 */
	private UINode addToMap(UIViewFrame viewFrame, int nodeType, String text, String additionalDetail, int xPos, int yPos) {

		String label = ""; //$NON-NLS-1$
		String detail = ""; //$NON-NLS-1$

		int para = text.indexOf("\n\n"); //$NON-NLS-1$
		if (para != -1) {
			label = text.substring(0,para);
			label = label.trim();
			detail = text.substring(para+1);
			detail = detail.trim();
		}
		else if (text.length() > 100) {
			label = text.substring(0,100);
			detail = text.substring(100);
		}
		else
			label = text;

		if (additionalDetail != "") { //$NON-NLS-1$
			detail = detail+"\n\n"+additionalDetail; //$NON-NLS-1$
		}

		UINode oNode = null;
		UIViewPane view = ((UIMapViewFrame)viewFrame).getViewPane();
		ViewPaneUI oViewPaneUI = view.getUI();

		int nX = xPos;
		int nY = yPos;

		if (nX == 0 && nY == 0) {
			nX = (viewFrame.getWidth()/2)-60;
			nY = (viewFrame.getHeight()/2)-60;

			// GET CURRENT SCROLL POSITION AND ADD THIS TO POSITIONING INFO
			int hPos = viewFrame.getHorizontalScrollBarPosition();
			int vPos = viewFrame.getVerticalScrollBarPosition();

			nX = nX + hPos;
			nY = nY + vPos;
		}

		oNode = oViewPaneUI.createNode(nodeType,
										 "", //$NON-NLS-1$
										 ProjectCompendium.APP.getModel().getUserProfile().getUserName(),
										 label,
										 detail,
										 nX,
										 nY);

		view.setSelectedNode(oNode,ICoreConstants.MULTISELECT);
		oNode.setSelected(true);
		oNode.setRollover(false);

		return oNode;
	}

	/**
	 * Work out the link type from the node type (and for Argument nodes the drag start position->type)
	 *
	 * @param UINode uinode, the UINode object to get a link type for.
	 * @return String, the integer representing the link type for the given UINode.
	 */
	private String getLinkType(UINode uinode) {
		int nodeType = uinode.getType();

		if ( nodeType == ICoreConstants.CON || nodeType == ICoreConstants.CON_SHORTCUT)
			return ICoreConstants.OBJECTS_TO_LINK;
		else if (nodeType == ICoreConstants.PRO || nodeType == ICoreConstants.PRO)
			return ICoreConstants.SUPPORTS_LINK;

		return ICoreConstants.DEFAULT_LINK;
	}

	/**
	 * Create a new node and add it to a map with link to given parent
	 *
	 * @param UIViewFrame frame, the frame containing the map into which to place the new node.
 	 * @param UINode parentNode, the node to link all newly created nodes to
	 * @param String nodeType, the type of the new node to be created
 	 * @param String text, the text for the label of the new node.
	 * @param String detail, any additional text to be added into the detail of the new node.
	 * @param int xPos, the X position at which to create the new node.
	 * @param int yPos, the Y position at which to create the new node.
	 */
	private UINode addToMapWithLink(UIViewFrame viewFrame, UINode parentNode, int nodeType, String text, String additionalDetail, int xPos, int yPos) {

		UINode oNode = addToMap(viewFrame, nodeType, text, additionalDetail, xPos, yPos);

		ViewPaneUI viewPaneUI = ((UIMapViewFrame)viewFrame).getViewPane().getUI();
		if (viewPaneUI != null) {
			String type = getLinkType(oNode);
			LinkProperties props = UIUtilities.getLinkProperties(type);
			viewPaneUI.createLink(oNode, parentNode, type, props);
		}

		return oNode;
	}


	/**
	 * Create a new node and add it to a list
	 *
	 * @param UIViewFrame frame, the frame containing the list into which to place the new node.
	 * @param int nodeType, the type of the new node to be created
 	 * @param String text, the text for the label of the new node.
	 * @param String detail, any additional text to be added into the detail of the new node.
	 * @param int xPos, the X position at which to create the new node.
	 * @param int yPos, the Y position at which to create the new node.
	 */
	private void addToList(UIList view, int nodeType, String text, String additionalDetail) {

		String label = ""; //$NON-NLS-1$
		String detail = ""; //$NON-NLS-1$

		int para = text.indexOf("\n\n"); //$NON-NLS-1$
		if (para != -1) {
			label = text.substring(0,para);
			label = label.trim();
			detail = text.substring(para+1);
			detail = detail.trim();
		}
		else if (text.length() > 100) {
			label = text.substring(0,100);
			detail = text.substring(100);
		}
		else
			label= text;

		if (additionalDetail != "") { //$NON-NLS-1$
			detail += additionalDetail;
		}

		ListUI listUI = view.getListUI();

		int nY = (listUI.getUIList().getNumberOfNodes() + 1) * 10;
		int nX = 0;

		listUI.createNode(nodeType,
							 "", //$NON-NLS-1$
							 ProjectCompendium.APP.getModel().getUserProfile().getUserName(),
							 label,
							 detail,
							 nX,
							 nY
							 );

		UIList uiList = listUI.getUIList();
		uiList.updateTable();
		uiList.selectNode(uiList.getNumberOfNodes() - 1, ICoreConstants.MULTISELECT);
	}
}
