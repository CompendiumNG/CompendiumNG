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

package com.compendium.ui;

import java.util.*;
import java.io.*;
import java.awt.*;

import com.compendium.core.datamodel.*;
import com.compendium.core.datamodel.services.*;

import com.compendium.*;

import com.compendium.ui.plaf.*;
import com.compendium.ui.*;

/**
 * UIArrange defines code to arrange a map of nodes tidily
 *
 * @author	Sajid Ali / Cheralathan Balakrishnan / Michelle Bachler / Alex Jarocha-Ernst
 */
public class UIArrange implements IUIConstants {

	/** The vertical distance between nodes when arranged.*/
	private final int				VERT_SEP = 10;

	/** The horizontal distance between .*/
	private final int				HORIZ_SEP = 30;

	/** The nodes to arrange.*/
	private	Hashtable				htNodes = new Hashtable(51);

	/** The nodes against their levels.*/
	private Hashtable				htNodesLevel = new Hashtable(51);

	/** The nodes and their children.*/
	private Hashtable				htNodesBelow = new Hashtable(51);

	/** The nodes against their ancestry.*/
	private Hashtable				htNodesAbove = new Hashtable();

	/** Used for undo operations.*/
	private Hashtable				nodePositionsCloneHashtable = new Hashtable();

	/** Used for redo operations.*/
	private Hashtable				nodePositionsCloneHashtableForRedo = new Hashtable();

	/** Nodes at level one.*/
	private	Vector					vtLevelOneNodes = new Vector(51);

	/** Links to arrange.*/
	private	Vector					vtLinks = new Vector(51);

	/** Nodes against their levels.*/
	private	Vector					vtNodesLevel = new Vector(51);

	/** Nodes against thier levels in order.*/
	private	Vector					vtNodesLevelOrdered = new Vector(51);

	/** List of node levels.*/
	private Vector					nodeLevelList = new Vector();

	/** The view service for accessing the databse.*/
	private IViewService 			vs 		= null;

	/** The session object for the current user with the current database.*/
	private PCSession 				session = null;

	/** The maximum indent level.*/
	private int						nMaxLevel = 0;
	
	/**
	 * Constructor. Does nothng.
	 */
	public UIArrange() {}

	/**
	 * Clear all the Vectors and Hashtables used to perform the arrange operations.
	 */
	public void clearData() {
		htNodesLevel.clear();
		htNodes.clear();
		htNodesBelow.clear();
		htNodesAbove.clear();
		vtLinks.removeAllElements();
		vtLevelOneNodes.removeAllElements();
		vtNodesLevel.removeAllElements();
		vtNodesLevelOrdered.removeAllElements();
		nodeLevelList.removeAllElements();
	}

	/**
	 * Process the given view for arranging.
	 * @param view com.compendium.core.datamodel.View, the view to arrange.
	 */
	public boolean processView(View view) {

		clearData();

		IModel model = ProjectCompendium.APP.getModel();
		session = model.getSession();
		vs = model.getViewService();

		Vector vtTemp = new Vector();
		try {
			vtTemp = vs.getNodePositions(session, view.getId());
		}
		catch(Exception ex) {
			ProjectCompendium.APP.displayError(LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "UIArrange.cannotGetNodes") + view.getLabel()+"." + ex.getMessage()); //$NON-NLS-1$ //$NON-NLS-2$
		}

		nodePositionsCloneHashtable.clear();

		for ( int i = 0; i < vtTemp.size(); i++) {
			nodePositionsCloneHashtable.put(
					((NodePosition)vtTemp.elementAt(i)).getNode().getId(),
					((NodePosition)vtTemp.elementAt(i)).getClone());
		}

		// GET NODES FOR THIS VIEW AND STORE INTO HASTABLE BY ID
		for(Enumeration en = vtTemp.elements();en.hasMoreElements();) {

			NodePosition nodePos = (NodePosition)en.nextElement();
			NodeSummary node = nodePos.getNode();
			String key = node.getId();
			htNodes.put(key, node);
		}

		for(Enumeration f = htNodes.elements();f.hasMoreElements();) {
			NodeSummary nodeToPrint = (NodeSummary)f.nextElement();

			//store the node into the hashtable.. the key being the nodeid, the object being the level integer
			String key = nodeToPrint.getId();
			htNodesLevel.put(key,new Integer(0));
		}

		//get the nodes and level numbers into a ht htNodesLevel
		if (!startLevelCalculation(view))
			return false;

		return true;
	}


	/**
	 * This will arrange the nodes in the given view.
	 * @param view com.compendium.core.datamodel.View, the view to arrange.
	 * @param viewFrame com.compenduim.ui.UIViewFrame, the frame of the map view to arrange.
 	 */
	public void arrangeView(View view, UIViewFrame viewFrame) {

		UIViewPane uiViewPane = null;
		if (viewFrame instanceof UIMapViewFrame)
			uiViewPane = ((UIMapViewFrame)viewFrame).getViewPane();

		processView(view);

		//get the number of levels
		int highestLevel = 0;
		for(Enumeration e = htNodesLevel.keys();e.hasMoreElements();) {

			String nodeId = (String)e.nextElement();
			int level = ((Integer)htNodesLevel.get(nodeId)).intValue();

			String label = ((NodeSummary)htNodes.get(nodeId)).getLabel();

			if (level > highestLevel) {
				highestLevel = level;
			}
		}

		int[] horizontalSep = new int[highestLevel];

		int leafcount = 0;
		int levelonecount = 0;

		int maxWidth = 0;
		int maxLevel = 0;

		//get the maxiumum width of a uinode based on its bounds
		for(Enumeration e = htNodesLevel.keys();e.hasMoreElements();) {

			String nodeId = (String)e.nextElement();

			//get the uinode and get its bounds/ccordinates
			UINode uinode = ((UINode) ((UIMapViewFrame)viewFrame).getViewPane().get(nodeId));
			int level = ((Integer)htNodesLevel.get(nodeId)).intValue();

			if (level > maxLevel) {
				maxLevel = level;
			}

			// ALLOW FOR FACT VIEW MIGHT BE SCALED
			int width = uinode.getWidth();

			if (uiViewPane != null) {
				Point p = UIUtilities.scalePoint(width, width, uiViewPane.getScale());
				width = p.x;
			}

			if (level > 0) {
				int sepLevel = horizontalSep[level-1];
				if(width > sepLevel)
					horizontalSep[level-1] = width + HORIZ_SEP;
			}
		}

		setYPositionForNodes(viewFrame);

		sortNodesParentwise();

		compactNodesInList(viewFrame, (Vector)nodeLevelList.elementAt(0));

		//this hashtable maintains a count of nodes present on a perticular level (key)
		Hashtable htCount = new Hashtable();

		for(int i=vtNodesLevel.size()-1; i >= 0; i--) {

			String nodeId = (String)vtNodesLevel.elementAt(i);
			int level = ((Integer)htNodesLevel.get(nodeId)).intValue();

			int countAtLevel = 0;
			int bufferToCenter = 0;

			int numberOfChildren = 0;
			if(htNodesBelow.containsKey(nodeId))
				numberOfChildren = ((Vector)htNodesBelow.get(nodeId)).size();

			if(htCount.containsKey(new Integer(level)))
				countAtLevel = ((Integer)htCount.get(new Integer(level))).intValue();

			countAtLevel++;
			if(numberOfChildren > 0)
				countAtLevel = countAtLevel + (new Integer((numberOfChildren+1)/2)).intValue();

			htCount.put(new Integer(level), new Integer(countAtLevel));

			//get the uinode and set its bounds/ccordinates
			UINode uinode = ((UINode) ((UIMapViewFrame)viewFrame).getViewPane().get(nodeId));
			if(uinode != null) {

				//get the x and y coordinates with the text field width in view for right centering..
				int newX = 0;
				if (view.equals(ProjectCompendium.APP.getHomeView())) {
					newX = 70;
				}
				int j = 0;
				for (j = 0; j < level; j++) {
					newX += horizontalSep[j];
				}

				// ALLOW FOR FACT VIEW MIGHT BE SCALED
				int width = uinode.getWidth();

				if (uiViewPane != null) {
					Point p = UIUtilities.scalePoint(width, width, uiViewPane.getScale());
					width = p.x;
				}

				newX -= ((horizontalSep[j-1]/2) + (width/2));

				int newY = uinode.getNodePosition().getYPos();
				Point ptNew = new Point(newX,newY);
				//uinode.setPosition(ptNew);

				Point loc = UIUtilities.transformPoint(newX, newY, uiViewPane.getScale());

				uinode.setBounds(loc.x, loc.y,
								 uinode.getWidth(),
								 uinode.getHeight());
				uinode.updateLinks();
				try {
					view.setNodePosition(nodeId, ptNew);
				}
				catch(Exception ex) {
					ex.printStackTrace();
					System.out.println("Error: (UIArrange.arrangeView) \n\n"+ex.getMessage()); //$NON-NLS-1$
				}
			}
		}

		if (uiViewPane != null)
			uiViewPane.repaint();

		Vector vtTemp = new Vector();
		try {
			vtTemp = vs.getNodePositions(session, view.getId());
		}
		catch(Exception ex) {
			ProjectCompendium.APP.displayError(LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "UIArrange.cannotGetNodes") + view.getLabel()+". " + ex.getMessage()); //$NON-NLS-1$ //$NON-NLS-2$
		}

		nodePositionsCloneHashtableForRedo.clear();

		for ( int i = 0; i < vtTemp.size(); i++) {
			nodePositionsCloneHashtableForRedo.put(
					((NodePosition)vtTemp.elementAt(i)).getNode().getId(),
					((NodePosition)vtTemp.elementAt(i)).getClone());
		}
	}


	/**
	 * Helper method. Calculate Level One Nodes here.
	 * @param view com.compendium.core.datamodel.View, the view to calculate lvel noe nodes for.
	 */
	private int calculateLevelOneNodes(View view) {

		int nLevel = 1;

		// upgrade the level with the current view
		//nLevel = nNodeLevelUp;

		IModel model = ProjectCompendium.APP.getModel();
		PCSession session = ProjectCompendium.APP.getModel().getSession();
		IViewService vs = model.getViewService();
		vtLinks = new Vector(51);

		try {
			vtLinks = vs.getLinks(session, view.getId());
		}
		catch(Exception ex) {
			ProjectCompendium.APP.displayError("Exception: (UIArrange.calculateLevelOneNodes) " + ex.getMessage()); //$NON-NLS-1$
		}

		if(htNodes.size() == 0)
			return -1;

		/**
		*	get each node and check if it is level 1 node
		*/
		for(Enumeration e = htNodes.elements();e.hasMoreElements();) {

			int nNodeLevelOne = 1;
			NodeSummary node = (NodeSummary)e.nextElement();
			String key = node.getId();

			// execute only if the node is new
			if ( ((Integer)htNodesLevel.get(key)).intValue() == 0 ) {

				/**
				*	Compare one end of the link with the current node for a match
				*		link.getTo() ------------<<------------ link.getFrom()
				*												^^^^^^^^^^^^^^
				*										nodeOther = link.getFrom()
				*/

				//	get links corresponding to this node
				for (Enumeration eL = vtLinks.elements(); eL.hasMoreElements();) {

					Link link = (Link)eL.nextElement();

					// TO FIX OLD BUG WHICH SHOULD NOT LONGER HAPPEN
					if ( (link.getFrom().getId()).equals( link.getTo().getId() ) ) {
						vtLinks.remove((Object)link);
					}
					else if( (link.getFrom()).getId().equals(key) || (link.getTo()).getId().equals(key)) {

						if( (link.getFrom()).getId().equals(node.getId())) {
							nNodeLevelOne *= 0;
							break;
						}
						else {
							nNodeLevelOne *= 1;
						}
					}
				}

				/**
				 *	if node on level one then export it.
				 */
				if(nNodeLevelOne == 1) {

					// do for new nodes (whose levels are not set)
					if(!(node.getLabel()).equals("Home Window")) { //$NON-NLS-1$

						// add to vector with level one nodes
						vtLevelOneNodes.addElement(node);

						// set the level number for this node
						htNodesLevel.put(key, new Integer(nNodeLevelOne));
					}
				}
				//System.out.println(node.getLabel() + " at level " + nNodeLevelOne);
			}
		}

		// Reversing the elements of levelonenodes vector into a temp vector for proper
		// ordering.. the elements in the levelonenodes vector seems to be in the reverse order!

		Vector vtTemp = new Vector();
		int numOfNodes = vtLevelOneNodes.size();
		vtTemp.setSize(numOfNodes);
		int pos = numOfNodes-1;

		for (Enumeration eN = vtLevelOneNodes.elements(); eN.hasMoreElements();) {
			NodeSummary node = (NodeSummary)eN.nextElement();
			vtTemp.setElementAt(node,pos--);
		}

		vtLevelOneNodes.removeAllElements();
		vtLevelOneNodes = vtTemp;

		return nLevel;
	}

	/**
	 * Helper method. Handle the level calculations,
	 * @param view com.compendium.core.datamodel.View, the view to calculate levels for.
	 */
	private boolean startLevelCalculation(View view) {

		int nLevel = 1;

		String sNodeInformation = ""; //$NON-NLS-1$

		nLevel = calculateLevelOneNodes(view);

		// Reversing the elements of vtLinks vector into a temp vector for proper
		// ordering.. the elements in the vtLinks vector seems to be in the reverse order!
		Vector vtTemp = new Vector();
		int numOfNodes = vtLinks.size();
		vtTemp.setSize(numOfNodes);
		int pos = numOfNodes-1;

		for (Enumeration eN = vtLinks.elements(); eN.hasMoreElements();) {
			Link link = (Link)eN.nextElement();
			vtTemp.setElementAt(link,pos--);
		}

		vtLinks.removeAllElements();
		vtLinks = vtTemp;

		/**
		*	For all level one nodes start recursive export
		*/
		for(int i=0;i<vtLevelOneNodes.size();i++) {

			//set the node in the position vector
			NodeSummary levelOneNode = ((NodeSummary)vtLevelOneNodes.elementAt(i));

			vtNodesLevel.addElement(levelOneNode.getId());
			int yPositionForLevel1 = ( (NodePosition)nodePositionsCloneHashtable.get(levelOneNode.getId())).getYPos();
			int secondYPositionForLevel1 = 0;
			int indexForLevel1 = 0;
			while (nodeLevelList.size() == 0) {
				nodeLevelList.addElement(new Vector());
			}
			Vector nodeListAtLevel1 = (Vector)nodeLevelList.elementAt(0);
			secondYPositionForLevel1 = 0;

			// SOME SORT OF NODE ORFERING BY LEVEL AND Y POSITION ?
			indexForLevel1 = 0;
			while ((indexForLevel1 < nodeListAtLevel1.size()) && (yPositionForLevel1 > secondYPositionForLevel1)) {
				secondYPositionForLevel1 = ( (NodePosition)nodePositionsCloneHashtable.get(nodeListAtLevel1.elementAt(indexForLevel1))).getYPos();
				indexForLevel1++;
			}

			if (indexForLevel1 == nodeListAtLevel1.size()) {

				if (yPositionForLevel1 > secondYPositionForLevel1)
					nodeListAtLevel1.addElement(levelOneNode.getId());
				else
					nodeListAtLevel1.insertElementAt(levelOneNode.getId(), (indexForLevel1 == 0)?indexForLevel1:indexForLevel1 - 1);
			}
			else {
				nodeListAtLevel1.insertElementAt(levelOneNode.getId(), (indexForLevel1 == 0)?indexForLevel1:indexForLevel1 - 1);
			}

			boolean bCheckNode = false;
			int debugCount = 0;

			// PROCESS CURRENT NODES LINKS

			for(Enumeration e = vtLinks.elements(); e.hasMoreElements();) {

				Link link = (Link)e.nextElement();

				// check if this link is tied to the given node..
				if((link.getTo()).getId().equals(levelOneNode.getId())) {

					// get the from node from this link
					NodeSummary node = (NodeSummary)htNodes.get((link.getFrom()).getId());

					Integer previousLevel = (Integer)htNodesLevel.get(node.getId());
					if ( (previousLevel != null) && (previousLevel.intValue() < (nLevel+1))) {
						htNodesLevel.put(node.getId(),new Integer(nLevel+1));
					}

					//increment the vector of nodes below this node
					Vector oldVector = null;
					if(htNodesBelow.get(levelOneNode.getId()) != null)
						oldVector = (Vector)htNodesBelow.get(levelOneNode.getId());
					else
						oldVector = new Vector();
					int yPosition = ( (NodePosition)nodePositionsCloneHashtable.get(node.getId())).getYPos();
					int secondYPosition = 0;

					int index = 0;
					boolean found = false;
					while ((index < oldVector.size()) && (yPosition > secondYPosition)) {
						secondYPosition = ( (NodePosition)nodePositionsCloneHashtable.get(oldVector.elementAt(index))).getYPos();
						if (node.getId().equals((String)oldVector.elementAt(index))) {
							found = true;
							break;
						}
						index++;
					}

					if (index == oldVector.size()) {
						if (yPosition > secondYPosition) {
							oldVector.addElement(node.getId());
						}
						else if (!found) {
							oldVector.insertElementAt(node.getId(), (index == 0)?index:index - 1);
						}
					} else if (!found) {
						oldVector.insertElementAt(node.getId(), (index == 0)?index:index - 1);
					}

					NodeSummary mainNode = (NodeSummary)htNodes.get(node.getId());
					NodeSummary parentNode = (NodeSummary)htNodes.get(levelOneNode.getId());
					mainNode.setParentNode(parentNode);

					//add the parent
					if (htNodesAbove.get(node.getId()) != null) {

						String nodeAboveId = (String)htNodesAbove.get(node.getId());
						NodeSummary nodeAbove = (NodeSummary)htNodes.get(nodeAboveId);
						NodePosition np = ( (NodePosition)nodePositionsCloneHashtable.get(nodeAboveId));
						int previousParentYPosition = np.getYPos();
						int currentParentYPosition =  ( (NodePosition)nodePositionsCloneHashtable.get(levelOneNode.getId())).getYPos();

						if (currentParentYPosition > previousParentYPosition) {
							htNodesAbove.put(node.getId(), levelOneNode.getId());
						}
					}
					else {
						htNodesAbove.put(node.getId(), levelOneNode.getId());
					}

					if ((previousLevel != null) && (previousLevel.intValue() != 0)) {
						if (previousLevel.intValue() < (nLevel + 1)) {
							((Vector)nodeLevelList.elementAt(previousLevel.intValue()-1)).removeElement(node.getId());
						}
						else {
							//oldVector.addElement(node.getId());
							htNodesBelow.put(levelOneNode.getId(),oldVector);

							//set the node in the position vector
							vtNodesLevel.addElement(node.getId());

							if (!startRecursiveCalculations(node,(nLevel+1)))
								return false;

							continue;
						}
					}

					while (nodeLevelList.size() < (nLevel + 1)) {
						nodeLevelList.addElement(new Vector());
					}

					Vector nodeListAtLevel = (Vector)nodeLevelList.elementAt(nLevel);
					secondYPosition = 0;
					index = 0;

					while ((index < nodeListAtLevel.size()) && (yPosition > secondYPosition)) {
						secondYPosition = ( (NodePosition)nodePositionsCloneHashtable.get(nodeListAtLevel.elementAt(index))).getYPos();
						index++;
					}
					if (index == nodeListAtLevel.size()) {
						if (yPosition > secondYPosition) {
							nodeListAtLevel.addElement(node.getId());
						} else {
							nodeListAtLevel.insertElementAt(node.getId(), (index == 0)?index:index - 1);
						}
					}
					else {
						nodeListAtLevel.insertElementAt(node.getId(), (index == 0)?index:index - 1);
					}

					//oldVector.addElement(node.getId());
					htNodesBelow.put(levelOneNode.getId(),oldVector);

					//set the node in the position vector
					vtNodesLevel.addElement(node.getId());

					if (!startRecursiveCalculations(node,(nLevel+1)))
						return false;
				}
			}
		}
		return true;
	}

	/**
	 * Helper method. This will calculate recursively all the nodes below. the given node.
	 * @param node com.compendium.core.datamodel.NodeSummary, the node to calculate for.
	 * @param LN, the level number.
	 */
	private boolean startRecursiveCalculations(NodeSummary node, int LN) {

		boolean bCheckNode = false;
		NodeSummary nodeFrom = new NodeSummary();

		String sToNodeID = node.getId();

		// get the links to this node
		for(Enumeration e = vtLinks.elements(); e.hasMoreElements();) {

		  	int levelNumber = LN;

			Link link = (Link)e.nextElement();

			// check if this link is tied to the given node..
			if((link.getTo()).getId().equals(sToNodeID)) {
				if (link.getTo().getId().equals(link.getFrom().getId()) ) {
					ProjectCompendium.APP.displayError(LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "UIArrange.message1a")+ //$NON-NLS-1$
							": '"+node.getLabel()+"' "+ //$NON-NLS-1$ //$NON-NLS-2$
							LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "UIArrange.message1b")+"\n\n"+ //$NON-NLS-1$ //$NON-NLS-2$
								LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "UIArrange.message1c")); //$NON-NLS-1$ //$NON-NLS-2$
					return false;
				}

				// get the from node from this link
				nodeFrom = (NodeSummary)htNodes.get((link.getFrom()).getId());
				if (nodeFrom == null) {
					continue;
				}
				levelNumber += 1;

				String sFromNodeID = nodeFrom.getId();

				// store the node in nodelevel hashtable with level info
				Integer previousLevel = (Integer)htNodesLevel.get(sFromNodeID);
				if ( (previousLevel != null) && (previousLevel.intValue() < levelNumber)) {
					htNodesLevel.put(sFromNodeID, new Integer(levelNumber));

					//if (!htNodesLinks.containsKey(sFromNodeID))
					//	htNodesLinks.put(sFromNodeID, new Hashtable());
					//Hashtable htLinks = htNodes.get(sFromNodeID);
					//htLinks.put(levelNumber, link);
					//htNodesLinks.put(sFromNodeID, htLinks)
				}

				//increment the vector of nodes below this node
				Vector oldVector = null;
				if(htNodesBelow.get(node.getId()) != null)
					oldVector = (Vector)htNodesBelow.get(node.getId());
				else
					oldVector = new Vector();

				int yPosition = ( (NodePosition)nodePositionsCloneHashtable.get(sFromNodeID)).getYPos();
				int secondYPosition = 0;

				int index = 0;
				boolean found = false;
				while ((index < oldVector.size()) && (yPosition > secondYPosition)) {

					//NodeSummary tempNode = (NodeSummary)htNodes.get(oldVector.elementAt(index));
					secondYPosition = ( (NodePosition)nodePositionsCloneHashtable.get(oldVector.elementAt(index))).getYPos();
					if (nodeFrom.getId().equals((String)oldVector.elementAt(index))) {
						found = true;
						break;
					}
					index++;

				}
				if (index == oldVector.size()) {
					if (yPosition > secondYPosition) {
						oldVector.addElement(nodeFrom.getId());
					}
					else if (!found) {
						oldVector.insertElementAt(nodeFrom.getId(), (index == 0)?index:index - 1);
					}
				}
				else if (!found) {
					oldVector.insertElementAt(nodeFrom.getId(), (index == 0)?index:index - 1);
				}

				NodeSummary oFromNode = (NodeSummary)htNodes.get(sFromNodeID);
				NodeSummary parentNode = (NodeSummary)htNodes.get(sToNodeID);
				oFromNode.setParentNode(parentNode);

				//add the parent
				if (htNodesAbove.get(nodeFrom.getId()) != null) {
					int previousParentYPosition = ( (NodePosition)nodePositionsCloneHashtable.get((String)htNodesAbove.get(sFromNodeID))).getYPos();
					int currentParentYPosition =  ( (NodePosition)nodePositionsCloneHashtable.get(sToNodeID)).getYPos();
					if (currentParentYPosition > previousParentYPosition) {
						htNodesAbove.put(sFromNodeID, sToNodeID);
					}
				} else {
					htNodesAbove.put(sFromNodeID, sToNodeID);
				}

				if ((previousLevel != null) && (previousLevel.intValue() != 0)) {
					if (previousLevel.intValue() < levelNumber) {
						((Vector)nodeLevelList.elementAt(previousLevel.intValue()-1)).removeElement(sFromNodeID);
					}
					else {
						htNodesBelow.put(sToNodeID, oldVector);

						//the maximum level number
						if(levelNumber > nMaxLevel)
							nMaxLevel = levelNumber;

						//set the node in the position vector
						vtNodesLevel.addElement(sFromNodeID);

						if (!startRecursiveCalculations(nodeFrom, levelNumber))
							return false;

						continue;
					}
				}

				while (nodeLevelList.size() < levelNumber) {
					nodeLevelList.addElement(new Vector());
				}

				Vector nodeListAtLevel = (Vector)nodeLevelList.elementAt(levelNumber - 1);
				secondYPosition = 0;

				index = 0;
				while ((index < nodeListAtLevel.size()) && (yPosition > secondYPosition)) {
					secondYPosition = ( (NodePosition)nodePositionsCloneHashtable.get(nodeListAtLevel.elementAt(index))).getYPos();
					index++;
				}
				if (index == nodeListAtLevel.size() ) {
					if (yPosition > secondYPosition) {
						nodeListAtLevel.addElement(sFromNodeID);
					} else {
						nodeListAtLevel.insertElementAt(sFromNodeID, (index == 0)?index:index - 1);
					}
				} else {
					nodeListAtLevel.insertElementAt(sFromNodeID, (index == 0)?index:index - 1);
				}

				htNodesBelow.put(sToNodeID, oldVector);

				//the maximum level number
				if(levelNumber > nMaxLevel)
					nMaxLevel = levelNumber;

				//set the node in the position vector
				vtNodesLevel.addElement(sFromNodeID);

				if (!startRecursiveCalculations(nodeFrom, levelNumber))
					return false;
			}
		}
		return true;
	}

	/**
	 * Helper method. Sort the node calculated so far by thier parents.
	 */
	private void sortNodesParentwise() {

		for (int i = 1 ; i < nodeLevelList.size(); i++) {
			Vector currentLevelList = (Vector)nodeLevelList.elementAt(i);
			for (int j = 0; j < currentLevelList.size(); j++) {
				int firstParentYPosition = ((NodePosition)nodePositionsCloneHashtable.get(
												((NodeSummary)htNodes.get(currentLevelList.elementAt(j))).getParentNode().getId())
											).getYPos();
				for (int k = j + 1; k < currentLevelList.size(); k++) {
					NodeSummary node = (NodeSummary)htNodes.get(currentLevelList.elementAt(k));
					if (node != null) {
						NodeSummary parent = node.getParentNode();
						if (parent != null) {
							String sID = parent.getId();
							NodePosition curNode = (NodePosition)nodePositionsCloneHashtable.get(sID);
							if (curNode != null) {

								int secondParentYPosition = curNode.getYPos();

								if (firstParentYPosition > secondParentYPosition) {
									String firstParent = (String)currentLevelList.elementAt(j);
									currentLevelList.setElementAt(currentLevelList.elementAt(k), j);
									currentLevelList.setElementAt(firstParent, k);
									firstParentYPosition = ((NodePosition)nodePositionsCloneHashtable.get(
															((NodeSummary)htNodes.get(currentLevelList.elementAt(j))).getParentNode().getId())
															).getYPos();
								}
							}
						}
					}
				}
			}
		}
	}

	/**
	 * This method is added to enable the child nodes to be positioned at the center relative to the parent.
	 * @param viewFrame com.compendium.ui.UIViewFrame, the frame of the nodes to set the y positions for.
	 */
	private void setYPositionForNodes(UIViewFrame viewFrame) {

		int yPosition = 20;

		// TO FIX an ArrayIndexOutOfBoundsException
		if ( nodeLevelList != null && nodeLevelList.size() > 0 ) {
			if (nodeLevelList.elementAt(0) != null && ((Vector)nodeLevelList.elementAt(0)).size() > 0) {

				for (int i = 0; i < ((Vector)nodeLevelList.elementAt(0)).size(); i++) {
					yPosition = calculateYPosition(viewFrame, (String)((Vector)nodeLevelList.elementAt(0)).elementAt(i), yPosition);
				}
			}
		}
	}

	/**
	 * Helper Method. This method is added to enable the child nodes to be positioned at the center relative to the parent.
	 * @param viewFrame com.compendium.ui.UIViewFrame, the frame of the node to calculate the y positions for.
	 * @param nodeId, the id of the node to calculate the y position for.
	 * @param yPosition, the starting y position.
	 * @return int, the calculated y position.
	 */
	private int calculateYPosition(UIViewFrame viewFrame, String nodeId, int yPosition) {

		UIViewPane viewPane = ((UIMapViewFrame)viewFrame).getViewPane();

		Vector childNodeList = (Vector) htNodesBelow.get(nodeId);
		UINode parentNode = ((UINode)viewPane.get(nodeId));

		if (childNodeList != null) {

			for (int i = 0; i < childNodeList.size(); i++) {
				UINode childNode = ((UINode)viewPane.get((String)childNodeList.elementAt(i)));
				yPosition = calculateYPosition(viewFrame, (String)childNodeList.elementAt(i), yPosition);
			}

			int firstChildPosition = ((UINode)viewPane.get((String)childNodeList.firstElement())).getNodePosition().getYPos();
			int lastChildPosition =  ((UINode)viewPane.get((String)childNodeList.lastElement())).getNodePosition().getYPos();

			int newYPosition = firstChildPosition + ((lastChildPosition - firstChildPosition) / 2);
			int nodeLevel = ((Integer)htNodesLevel.get(nodeId)).intValue();
			int indexOfNode = ((Vector)nodeLevelList.elementAt(nodeLevel - 1)).indexOf(nodeId);
			int indexOfPreviousNode = indexOfNode - 1;
			int previousNodeYPosition = 0;

			UINode node = ((UINode)viewPane.get(nodeId));

			if (indexOfPreviousNode > -1) {
				UINode previousNode = ((UINode)viewPane.get((String)((Vector)nodeLevelList.elementAt(nodeLevel-1)).elementAt(indexOfPreviousNode)));
				previousNodeYPosition = previousNode.getNodePosition().getYPos();

				// ALLOW FOR FACT VIEW MIGHT BE SCALED
				int nodeHeight = previousNode.getHeight();
				Point p1 = UIUtilities.scalePoint(nodeHeight, nodeHeight, viewPane.getScale());
				nodeHeight = p1.x;

				if ((previousNodeYPosition + nodeHeight) > newYPosition) {
					newYPosition = previousNodeYPosition + nodeHeight + (VERT_SEP);
				}
			}

			Point pos = node.getNodePosition().getPos();
			node.getNodePosition().setPos(new Point(pos.x, newYPosition));

			if (newYPosition > yPosition) {
				yPosition = newYPosition;
			}
		}
		else {
			UINode node = ((UINode)viewPane.get(nodeId));

//begin edit, Alex Jarocha-Ernst
			//previously, this didn't do anything with the previous node of a leaf
			//this caused problems when the first node on a level had children, but
			//successive ones did not
			int nodeLevel = ((Integer)htNodesLevel.get(nodeId)).intValue();
			int indexOfNode = ((Vector)nodeLevelList.elementAt(nodeLevel - 1)).indexOf(nodeId);
			int indexOfPreviousNode = indexOfNode - 1;
			int previousNodeYPosition = 0;

			if(indexOfPreviousNode > -1) {
		        UINode previousNode = ((UINode)viewPane.get((String)((Vector)nodeLevelList.elementAt(nodeLevel-1)).elementAt(indexOfPreviousNode)));
				previousNodeYPosition = previousNode.getNodePosition().getYPos();

				// ALLOW FOR FACT VIEW MIGHT BE SCALED
				int nodeHeight = previousNode.getHeight();
				Point p1 = UIUtilities.scalePoint(nodeHeight, nodeHeight, viewPane.getScale());
				nodeHeight = p1.x;

				if ((previousNodeYPosition + nodeHeight) > yPosition) {
					yPosition = previousNodeYPosition + nodeHeight + (VERT_SEP);
				}
				Point pos = node.getNodePosition().getPos();
				node.getNodePosition().setPos(new Point(pos.x, yPosition));

			} else {
//end edit, Alex Jarocha-Ernst

				Point pos = node.getNodePosition().getPos();
				node.getNodePosition().setPos(new Point(pos.x, yPosition));

				// ALLOW FOR FACT VIEW MIGHT BE SCALED
				int nodeHeight = node.getHeight();
				Point p1 = UIUtilities.scalePoint(nodeHeight, nodeHeight, viewPane.getScale());
				nodeHeight = p1.x;

				yPosition += (VERT_SEP + nodeHeight);
			}
		}

		return yPosition;
	}

	/**
	 * Helper Method. This method compacts the nodes in the given list.
	 * @param viewFrame com.compendium.ui.UIViewFrame, the frame of the node to compact.
	 * @param nodeList, the list of node to compact.
	 */
	private void compactNodesInList(UIViewFrame viewFrame, Vector nodeList) {

		UIViewPane viewPane = ((UIMapViewFrame)viewFrame).getViewPane();

		Hashtable compactDoneForNodes = new Hashtable();

		for (int i = 0; i < nodeList.size(); i++) {
			String nodeID = (String)nodeList.elementAt(i);
			UINode tmpNode = ((UINode)viewPane.get((String)nodeList.elementAt(i)));
			Vector childNodeList = (Vector)htNodesBelow.get(nodeID);
			if (childNodeList != null) {
				compactNodesInList(viewFrame, childNodeList);
			}
		}

		int currentNodeYPosition = 0;
		int previousNodeYPosition = 0;
		int compactAmount = 0;

		for (int i = 1; i < nodeList.size(); i++) {
			UINode currentNode = ((UINode)viewPane.get((String)nodeList.elementAt(i)));
			UINode previousNode = ((UINode)viewPane.get((String)nodeList.elementAt(i-1)));
			currentNodeYPosition = currentNode.getNodePosition().getYPos();
			previousNodeYPosition = previousNode.getNodePosition().getYPos();

			UINode node = ((UINode)viewPane.get((String)nodeList.elementAt(i)));

			// ALLOW FOR FACT VIEW MIGHT BE SCALED
			int nodeHeight = node.getHeight();
			Point p1 = UIUtilities.scalePoint(nodeHeight, nodeHeight, viewPane.getScale());
			nodeHeight = p1.x;

			if ((currentNodeYPosition - previousNodeYPosition - nodeHeight) > VERT_SEP) {
				compactDoneForNodes.clear();

				compactAmount = checkCompact(viewFrame,
											 (String)nodeList.elementAt(i),
											 currentNodeYPosition - previousNodeYPosition - VERT_SEP - nodeHeight,
											 compactDoneForNodes);
				if (compactAmount > 0) {
					compact(viewFrame, (String)nodeList.elementAt(i), compactAmount, compactDoneForNodes);
				}
			}

			Vector childNodeList = (Vector)htNodesBelow.get((String)nodeList.elementAt(i));

			if (childNodeList != null) {

				UINode firstNode = (UINode)viewPane.get((String)childNodeList.firstElement());
				int firstNodeYPosition = firstNode.getNodePosition().getYPos();
				UINode lastNode = (UINode)viewPane.get((String)childNodeList.lastElement());
				int lastNodeYPosition = lastNode.getNodePosition().getYPos();
				previousNodeYPosition = 0;
				int previousNodeHeight = 0;
				int level = ((Integer)htNodesLevel.get((String)nodeList.elementAt(i))).intValue();
				int indexOfCurrentNode = ((Vector)nodeLevelList.elementAt(level-1)).indexOf((String)nodeList.elementAt(i));
				int indexOfPreviousNode = indexOfCurrentNode - 1;

				if (indexOfPreviousNode > -1) {
					previousNodeYPosition = previousNode.getNodePosition().getYPos();

					// ALLOW FOR FACT VIEW MIGHT BE SCALED
					previousNodeHeight = previousNode.getHeight();
					Point p2 = UIUtilities.scalePoint(previousNodeHeight, previousNodeHeight, viewPane.getScale());
					previousNodeHeight = p2.x;

					Point pos = node.getNodePosition().getPos();
					int centerPosition = firstNodeYPosition + (lastNodeYPosition - firstNodeYPosition)/2;

					if ((previousNodeYPosition + previousNodeHeight + VERT_SEP) < centerPosition) {
						node.getNodePosition().setPos(new Point(pos.x, centerPosition));
					}
					else {
						node.getNodePosition().setPos(new Point(pos.x, previousNodeYPosition + previousNodeHeight + VERT_SEP));
					}
				}
			}
		}
	}

	/**
	 * Helper Method. This method checks the compactness of the node with the given id.
	 * @param viewFrame com.compendium.ui.UIViewFrame, the frame of the node to compact.
	 * @param nodeId, the id of the node to check.
	 * @param compactAmount, the amount to compact by.
	 * @param compactDoneForNodes, nodes already compacted.
	 */
	private int checkCompact(UIViewFrame viewFrame, String nodeId, int compactAmount, Hashtable compactDoneForNodes) {

		UIViewPane viewPane = ((UIMapViewFrame)viewFrame).getViewPane();

		compactDoneForNodes.put(nodeId, new Boolean(false));

		//get the level of this node
		UINode currentNode = ((UINode)viewPane.get(nodeId));
		int	currentNodeYPosition = currentNode.getNodePosition().getYPos();
		int level = ((Integer)htNodesLevel.get(nodeId)).intValue();
		int indexOfCurrentNode = ((Vector)nodeLevelList.elementAt(level-1)).indexOf(nodeId);
		int indexOfPreviousNode = indexOfCurrentNode - 1;

		if (indexOfPreviousNode < 0) {
			if (currentNodeYPosition < compactAmount) {
				compactAmount = currentNodeYPosition;
			}
			indexOfPreviousNode = 0;
		}
		else {
			UINode previousNode = ((UINode)viewPane.get((String)((Vector)nodeLevelList.elementAt(level-1)).elementAt(indexOfPreviousNode)));
			int previousNodeYPosition = previousNode.getNodePosition().getYPos();

			// ALLOW FOR FACT VIEW MIGHT BE SCALED
			int nodeHeight = currentNode.getHeight();
			double scale = viewPane.getScale();
			Point p = UIUtilities.scalePoint(nodeHeight, nodeHeight, scale);
			nodeHeight = p.x;

			// ALLOW FOR FACT VIEW MIGHT BE SCALED
			int previousNodeHeight = previousNode.getHeight();
			Point p2 = UIUtilities.scalePoint(previousNodeHeight, previousNodeHeight, scale);
			previousNodeHeight = p2.x;

			if ( (currentNodeYPosition - previousNodeYPosition - VERT_SEP - previousNodeHeight) <
				compactAmount) {
				compactAmount = currentNodeYPosition - previousNodeYPosition - VERT_SEP - previousNodeHeight;
			}
		}
		Vector childNodeList = (Vector)htNodesBelow.get(nodeId);
		if (childNodeList != null) {
			compactAmount = checkCompact(viewFrame, (String)childNodeList.elementAt(0),compactAmount, compactDoneForNodes);
		}
		if (compactAmount < 0) {
			compactAmount = 0;
		}
		return compactAmount;
	}

	/**
	 * Helper Method. This method compacts the node with the given id.
	 * @param viewFrame com.compendium.ui.UIViewFrame, the frame of the node to compact.
	 * @param nodeId, the id of the node to compact.
	 * @param compactAmount, the amount to compact by.
	 * @param compactDoneForNodes, nodes already compacted.
	 */
	private void compact(UIViewFrame viewFrame, String nodeId, int compactAmount, Hashtable compactDoneForNodes) {

		UIViewPane viewPane = ((UIMapViewFrame)viewFrame).getViewPane();

		Boolean isCompactDone = (Boolean)compactDoneForNodes.get(nodeId);
		if (isCompactDone != null) {
			if (isCompactDone.booleanValue() == true) {
				return;
			}
		}

		UINode node = ((UINode)viewPane.get(nodeId));
		Point pos = node.getNodePosition().getPos();
		int level = ((Integer)htNodesLevel.get(nodeId)).intValue();
		int indexOfCurrentNode = ((Vector)nodeLevelList.elementAt(level-1)).indexOf(nodeId);
		int indexOfPreviousNode = indexOfCurrentNode - 1;
		int yPosition = 0;

		if (indexOfPreviousNode > -1) {
			UINode previousNode = ((UINode)viewPane.get((String)((Vector)nodeLevelList.elementAt(level-1)).elementAt(indexOfPreviousNode)));
			int previousNodeYPosition = previousNode.getNodePosition().getYPos();

			// ALLOW FOR FACT VIEW MIGHT BE SCALED
			int previousNodeHeight = previousNode.getHeight();
			Point p = UIUtilities.scalePoint(previousNodeHeight, previousNodeHeight, viewPane.getScale());
			previousNodeHeight = p.x;

			if ((pos.y - compactAmount) < (previousNodeYPosition + previousNodeHeight + VERT_SEP)) {
				yPosition = previousNodeYPosition + previousNodeHeight + VERT_SEP;
			}
			else {
				yPosition = pos.y - compactAmount;
			}
		}
		else {
			yPosition = pos.y - compactAmount;
		}

		if (yPosition < 0)
			yPosition = 0;

		node.getNodePosition().setPos(new Point(pos.x, yPosition));
		compactDoneForNodes.put(nodeId, new Boolean(true));
		Vector nodeList = (Vector)htNodesBelow.get(nodeId);

		if (nodeList != null) {
			for (int i = 0; i < nodeList.size(); i++) {
				if (nodeId.equals((String)htNodesAbove.get(nodeList.elementAt(i)))) {
					compact(viewFrame, (String) nodeList.elementAt(i), compactAmount, compactDoneForNodes);
				}
			}
		}
	}


//******** UNDO / REDO **************//

	/**
	 * Undo the last arrange for the given frame
	 * @param viewFrame com.compendium.ui.UIViewFrame, the frame to undo the arrange for.
	 */
	public void undoArrange(UIViewFrame viewFrame) {

		UIViewPane pane = ((UIMapViewFrame)viewFrame).getViewPane();
		for(Enumeration e = nodePositionsCloneHashtable.keys();
				e.hasMoreElements();)	{
			String nodeID = (String)e.nextElement();
			UINode uinode = (UINode)pane.get(nodeID);
			NodePosition np = (NodePosition)nodePositionsCloneHashtable.get(nodeID);

			Point loc = UIUtilities.transformPoint(np.getXPos(), np.getYPos(), pane.getScale());

			uinode.setBounds(loc.x, loc.y,
							 uinode.getWidth(),
							 uinode.getHeight());
			uinode.updateLinks();

			try {
				viewFrame.getView().setNodePosition(nodeID, np.getPos());
			}
			catch(Exception ex) {
				System.out.println("Error: (UIArrange.undoArrange) \n\n"+ex.getMessage()); //$NON-NLS-1$
			}
		}
		pane.repaint();
	}

	/**
	 * Redo the last arrange for the given frame
	 * @param viewFrame com.compendium.ui.UIViewFrame, the frame to undo the arrange for.
	 */
	public void redoArrange(UIViewFrame viewFrame) {

		UIViewPane pane = ((UIMapViewFrame)viewFrame).getViewPane();

		for(Enumeration e = nodePositionsCloneHashtableForRedo.keys();
				e.hasMoreElements();)	{
			String nodeID = (String)e.nextElement();
			UINode uinode = (UINode)pane.get(nodeID);
			NodePosition np = (NodePosition)nodePositionsCloneHashtableForRedo.get(nodeID);

			Point loc = UIUtilities.transformPoint(np.getXPos(), np.getYPos(), pane.getScale());

			uinode.setBounds(loc.x, loc.y,
							 uinode.getWidth(),
							 uinode.getHeight());
			uinode.updateLinks();

			try {
				viewFrame.getView().setNodePosition(nodeID, np.getPos());
			}
			catch(Exception ex) {
				System.out.println("Error: (UIArrange.redoArrange) \n\n"+ex.getMessage()); //$NON-NLS-1$
			}
		}
		pane.repaint();
	}


	/**
	 * Return the list of node levels.
	 * @return Vector, the node level list.
	 */
	public Vector getNodeLevelList() {
		return nodeLevelList;
	}

	/**
	 * Return the list of nodes below.
	 * @return Hashtable, the nodes below list.
	 */
	public Hashtable getNodesBelow() {
		return htNodesBelow;
	}

	/**
	 * Return the list of nodes.
	 * @return Hashtable, the nodes.
	 */
	public Hashtable getNodes() {
		return htNodes;
	}

	/**
	 * Return the list of nodes mapped to levels.
	 * @return Hashtable, the list of nodes mapped to levels.
	 */
	public Hashtable getNodesLevel() {
		return htNodesLevel;
	}
}
