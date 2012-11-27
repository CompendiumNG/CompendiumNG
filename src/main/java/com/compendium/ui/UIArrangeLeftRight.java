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
import java.awt.*;

import com.compendium.core.datamodel.*;
import com.compendium.core.datamodel.services.*;

import com.compendium.*;

/**
 * UIArrange defines code to arrange a map of nodes tidily
 *
 * @author	Sajid Ali / Cheralathan Balakrishnan / Michelle Bachler / Alex Jarocha-Ernst / Lakshmi Prabhakaran
 */
public class UIArrangeLeftRight implements IUIArrange {

	/** The nodes to arrange.*/
	private	Hashtable				htNodes 					= new Hashtable(51);

	/** The nodes against their levels.*/
	private Hashtable				htNodesLevel 				= new Hashtable(51);

	/** The nodes and their children.*/
	private Hashtable				htNodesBelow 				= new Hashtable(51);

	/** The nodes against their ancestry.*/
	private Hashtable				htNodesAbove 				= new Hashtable();

	/** Used for undo operations.*/
	private Hashtable				nodePositionsCloneHashtable = new Hashtable();

	/** Used for redo operations.*/
	private Hashtable		nodePositionsCloneHashtableForRedo 	= new Hashtable();

	/** Nodes at level one.*/
	private	Vector					vtLevelOneNodes 			= new Vector(51);

	/** Links to arrange.*/
	private	Vector					vtLinks 					= new Vector(51);

	/** Nodes against their levels.*/
	private	Vector					vtNodesLevel 				= new Vector(51);

	/** Nodes against thier levels in order.*/
	private	Vector					vtNodesLevelOrdered 		= new Vector(51);

	/** List of node levels.*/
	private Vector					nodeLevelList 				= new Vector();

	/** The view service for accessing the databse.*/
	private IViewService 			vs 							= null;

	/** The session object for the current user with the current database.*/
	private PCSession 				session 					= null;

	/** The maximum indent level.*/
	private int						nMaxLevel	 				= 0;

	/** Node ID against Y position set or not.*/
	private Hashtable				htNodeYPositionSet 			= new Hashtable();

	/**  The number of position above the pointer position */
	private int abovePointerPos 								= 0;

	/** The number of position below the pointer position */
	private int belowPointerPos 								= 1;

	/** The number of children below the pointer position */
	private int childSizeBelow 									= 0;

	/** The number of children above the pointer position */
	private int childSizeAbove 									= 0;
	
	/** max number a node can recurse */ 
	private int recursionCount  								= 3; 

	/**
	 * Constructor. Does nothing.
	 */
	public UIArrangeLeftRight() {}

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
			ProjectCompendium.APP.displayError(LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "UIArrangeLeftRight.cannotGetNodes") + view.getLabel()+"." + ex.getMessage()); //$NON-NLS-1$ //$NON-NLS-2$
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
		if (!startLevelCalculation(view)) {
			return false;
		}
		
		return true;
	}


	/**
	 * This will arrange the nodes in the given view.
	 * @param view com.compendium.core.datamodel.View, the view to arrange.
	 * @param viewFrame com.compenduim.ui.UIViewFrame, the frame of the map view to arrange.
 	 */
	public void arrangeView(View view, UIViewFrame viewFrame) {
		UIViewPane uiViewPane = null;
		if (viewFrame instanceof UIMapViewFrame){
			uiViewPane = ((UIMapViewFrame)viewFrame).getViewPane();

			processView(view);

			//get the number of levels
			int highestLevel = 0;
			for(Enumeration e = htNodesLevel.keys();e.hasMoreElements();) {

				String nodeId = (String)e.nextElement();
				int level = ((Integer)htNodesLevel.get(nodeId)).intValue();

				if (level > highestLevel) {
					highestLevel = level;
				}
			}
			int[] horizontalSep = new int[highestLevel];
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
						horizontalSep[level-1] = width + FormatProperties.arrangeLeftHorizontalGap;
				}
			}
	// begin edit - Lakshmi (10/25/05)
	//		First sort nodes parent wise and then set Y position for the node.. This might some what minimize cross over links
			sortNodesParentwise();
			setYPositionForNodes(viewFrame);

	//end edit	- Lakshmi

			if (nodeLevelList.size() > 0) {
				compactNodesInList(viewFrame, (Vector)nodeLevelList.elementAt(0));
			}
	//begin edit - Lakshmi	(10/25/05)
	// 		This is to put all parents at the center of their children
			if(nodeLevelList.size() > 1){
				for(int i =0; i < ((Vector)nodeLevelList.get(0)).size(); i++){
					finalizeYPos(viewFrame,(String)((Vector)nodeLevelList.get(0)).get(i));
				}//end for
			}//end if
	//end edit  - Lakshmi

			//this hashtable maintains a count of nodes present on a particular level (key)
			Hashtable htCount = new Hashtable();

			for(int i=vtNodesLevel.size()-1; i >= 0; i--) {

				String nodeId = (String)vtNodesLevel.elementAt(i);
				int level = ((Integer)htNodesLevel.get(nodeId)).intValue();

				int countAtLevel = 0;
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

					double scale = uiViewPane.getScale();
					if (uiViewPane != null) {
						Point p = UIUtilities.scalePoint(width, width, scale);
						width = p.x;
					}

					newX -= ((horizontalSep[j-1]/2) + (width/2));

					int newY = uinode.getNodePosition().getYPos();
					//uinode.getNodePosition().setPos(newX, newY);
					Point ptNew = new Point( newX, newY);
					//uinode.setPosition(ptNew);

					if (uiViewPane != null) {
						Point loc = UIUtilities.transformPoint(newX ,newY, scale);
						uinode.setBounds(loc.x, loc.y,
										 uinode.getWidth(),
										 uinode.getHeight());
					}

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
		} else {
			ProjectCompendium.APP.displayError(LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "UIArrangeLeftRight.cannotArrange")); //$NON-NLS-1$
		}
	}//end arrangeView


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
				LinkProperties props = null;
				Link link = null;

				//	get links corresponding to this node
				for (Enumeration eL = vtLinks.elements(); eL.hasMoreElements();) {

					props =  (LinkProperties)eL.nextElement();
					link = props.getLink();

					// TO FIX OLD BUG WHICH SHOULD NOT LONGER HAPPEN
					if ( (link.getFrom().getId()).equals( link.getTo().getId() ) ) {
						vtLinks.remove((Object)link);
					}
					else if( (link.getFrom()).getId().equals(key) || (link.getTo()).getId().equals(key)) {

						if( (link.getFrom()).getId().equals(node.getId())) {
							nNodeLevelOne *= 0;
							break;
						}else {
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

		nLevel = calculateLevelOneNodes(view);

		// Reversing the elements of vtLinks vector into a temp vector for proper
		// ordering.. the elements in the vtLinks vector seems to be in the reverse order!
		Vector vtTemp = new Vector();
		int numOfNodes = vtLinks.size();
		vtTemp.setSize(numOfNodes);
		int pos = numOfNodes-1;
		LinkProperties props = null;
		Link link = null;

		for (Enumeration eN = vtLinks.elements(); eN.hasMoreElements();) {
			props =  (LinkProperties)eN.nextElement();
			vtTemp.setElementAt(props,pos--);
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
			
			// SOME SORT OF NODE ORDERING BY LEVEL AND Y POSITION ?
			//-- begin sort(upto the current node in for loop) for level one based on y-position
			//-- find a node's y position > current node's y position in level 1
			indexForLevel1 = 0;
			while ((indexForLevel1 < nodeListAtLevel1.size()) && (yPositionForLevel1 > secondYPositionForLevel1)) {
				secondYPositionForLevel1 = ( (NodePosition)nodePositionsCloneHashtable.get(nodeListAtLevel1.elementAt(indexForLevel1))).getYPos();
				indexForLevel1++;
			}

			//-- insert at the correct position - sorted by y position
			if (indexForLevel1 == nodeListAtLevel1.size()) {

				if (yPositionForLevel1 > secondYPositionForLevel1)
					nodeListAtLevel1.addElement(levelOneNode.getId());
				else
					nodeListAtLevel1.insertElementAt(levelOneNode.getId(), (indexForLevel1 == 0)?indexForLevel1:indexForLevel1 - 1);
			}
			else {
				nodeListAtLevel1.insertElementAt(levelOneNode.getId(), (indexForLevel1 == 0)?indexForLevel1:indexForLevel1 - 1);
			}
			//-- end sort
			// PROCESS CURRENT NODES LINKS

			for(Enumeration e = vtLinks.elements(); e.hasMoreElements();) {

				props =  (LinkProperties)e.nextElement();
				link = props.getLink();

				// check if this link is tied to the given node..
				if((link.getTo()).getId().equals(levelOneNode.getId())) {

					// get the from node from this link
					NodeSummary node = (NodeSummary)htNodes.get((link.getFrom()).getId());
					if (node != null) {

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
						//add the parent
	
						Vector vtParentNodes = new Vector();
						if (htNodesAbove.get(node.getId()) != null) {
	
	//begin edit - Lakshmi (10/25/05)
	// 				Get all parents for a node. Keeping track of all the parents, helps to sort child nodes and position them around
	//their parents if possible.
							vtParentNodes = (Vector) htNodesAbove.get(node.getId());
							for(int j=0; j<vtParentNodes.size();j++){
								String nodeAboveId = (String)vtParentNodes.get(j);
								NodePosition np = ( (NodePosition)nodePositionsCloneHashtable.get(nodeAboveId));
								int previousParentYPosition = np.getYPos();
								int currentParentYPosition =  ( (NodePosition)nodePositionsCloneHashtable.get(levelOneNode.getId())).getYPos();
								if(nodeAboveId != levelOneNode.getId()) {
									if (currentParentYPosition < previousParentYPosition) {
										vtParentNodes.insertElementAt(levelOneNode.getId(), j);
										break ;
									}else {
										vtParentNodes.add(levelOneNode.getId());
										break;
									}//end else
								} //end if
							} //end for
						} else {
							vtParentNodes.add(levelOneNode.getId());
						} //end else
						htNodesAbove.put(node.getId(), vtParentNodes);
						mainNode.setParentNode((NodeSummary)htNodes.get((String) vtParentNodes.lastElement()));
						
	// end edit	- Lakshmi
						if ((previousLevel != null) && (previousLevel.intValue() != 0)) {
							if (previousLevel.intValue() < (nLevel + 1)) {
								((Vector)nodeLevelList.elementAt(previousLevel.intValue()-1)).removeElement(node.getId());
							}
							else {
								//oldVector.addElement(node.getId());
								htNodesBelow.put(levelOneNode.getId(),oldVector);
	
								//set the node in the position vector
								vtNodesLevel.addElement(node.getId());
	
								if (!startRecursiveCalculations(node,(nLevel+1))) {
									return false;
								}
	
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
	
						if (!startRecursiveCalculations(node,(nLevel+1))) {
							return false;
						}
					}
				}
			}
		}
		return true;
	}

	
	private Hashtable nodesRecursed = new Hashtable(51);
	
	/**
	 * Helper method. This will calculate recursively all the nodes below. the given node.
	 * @param node com.compendium.core.datamodel.NodeSummary, the node to calculate for.
	 * @param LN, the level number.
	 */

	private boolean startRecursiveCalculations(NodeSummary node, int LN) {

		Integer count = new Integer(1);
		if(nodesRecursed.containsKey(node.getId())){
			count = (Integer) nodesRecursed.get(node.getId());
			if(count.intValue() > recursionCount) {
				return true;
			} else {
				count = new Integer(count.intValue() + 1);
				nodesRecursed.put(node.getId(), count);
			}
		} else {
			nodesRecursed.put(node.getId(), count);
		}
		
		NodeSummary nodeFrom = new NodeSummary();

		String sToNodeID = node.getId();
		LinkProperties props = null;
		Link link = null;

		// get the links to this node
		for(Enumeration e = vtLinks.elements(); e.hasMoreElements();) {

		  	int levelNumber = LN;

			props =  (LinkProperties)e.nextElement();
			link = props.getLink();

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

					// if (!htNodesLinks.containsKey(sFromNodeID))
					// htNodesLinks.put(sFromNodeID, new Hashtable());
					// Hashtable htLinks = htNodes.get(sFromNodeID);
					// htLinks.put(levelNumber, link);
					// htNodesLinks.put(sFromNodeID, htLinks)
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


				//add the parent

				Vector vtParentNodes = new Vector();
				if (htNodesAbove.get(oFromNode.getId()) != null) {

//begin edit -  Lakshmi (10/25/05)
//				Get all parents for a node. Keeping track of all the parents, helps to sort child nodes and position them around
//their parents if possible.
					vtParentNodes = (Vector) htNodesAbove.get(sFromNodeID);
					int currentParentYPosition =  ((NodePosition)nodePositionsCloneHashtable.get(sToNodeID)).getYPos();

					boolean isInserted = false;

					for(int j=0; j<vtParentNodes.size();j++){
						String nodeAboveId = (String)vtParentNodes.get(j);
						NodePosition np = ( (NodePosition)nodePositionsCloneHashtable.get(nodeAboveId));
						int previousParentYPosition = np.getYPos();

						if(nodeAboveId != sToNodeID) {
							if (currentParentYPosition < previousParentYPosition) {
								vtParentNodes.insertElementAt(sToNodeID, j);
								isInserted = true;
								break ;
							}//end if
						} //end if
					} // end for
					if(!isInserted){
						vtParentNodes.add(sToNodeID);
					} //end if
				} else {
					vtParentNodes.add(sToNodeID);
				} //end else
				htNodesAbove.put(sFromNodeID, vtParentNodes);
				oFromNode.setParentNode((NodeSummary)htNodes.get(vtParentNodes.lastElement()));
				
// end edit	 - Lakshmi

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

//		this hashtable maintains a vector of nodeIDs that has been compared for sorting against the nodeId (key)
		Hashtable htNodesChecked = new Hashtable();
		for (int i = 1 ; i < nodeLevelList.size(); i++) {
			Vector currentLevelList = (Vector)nodeLevelList.elementAt(i);

			for (int j = 0; j < currentLevelList.size(); j++) {

				NodeSummary nodeToSort = (NodeSummary)htNodes.get(currentLevelList.elementAt(j));
				Vector vtParentNode = (Vector) htNodesAbove.get(nodeToSort.getId());
				NodeSummary parentNodeSummary = ((NodeSummary)htNodes.get(vtParentNode.firstElement()));
				String sParentNode = parentNodeSummary.getId();


				int nIndexOfFirstParent = ((Vector)nodeLevelList.get(i -1)).indexOf(sParentNode);
				//int firstParentYPosition = ((NodePosition)nodePositionsCloneHashtable.get(sParentNode)).getYPos();

//begin edit - Lakshmi (10/25/05)
// If the child node's first parent is below the parent of the node below current(child) node,
// then	move the node below above. i.e. child nodes are positioned according to the parent's position.
//	This is to minimize the cross over links.

				Vector vtCheckedNodes = new Vector();
				if (htNodesChecked.get(nodeToSort.getId()) !=  null){
					vtCheckedNodes = (Vector) htNodesChecked.get(nodeToSort.getId());
				}// end if
				for (int k = j +1; k < currentLevelList.size(); k++) {
					NodeSummary node = (NodeSummary)htNodes.get(currentLevelList.elementAt(k));
					if (node != null) {
						if(!vtCheckedNodes.contains(node.getId())){
							Vector vtParentNodes = (Vector)htNodesAbove.get(node.getId());
							if((vtParentNodes != null) && (vtParentNodes.size() > 0)){
								for(int m = 0; m < vtParentNodes.size() ; m++){
									String sID = (String)vtParentNodes.get(m);
									NodePosition curNodeParent = (NodePosition)nodePositionsCloneHashtable.get(sID);

									if (curNodeParent != null) {

										int nIndexOfSecondParent = ((Vector)nodeLevelList.get(i -1)).indexOf(sID);

										if ((nIndexOfFirstParent > nIndexOfSecondParent ) &&(nIndexOfSecondParent > -1)){
											 String sFirstNodeID = (String)currentLevelList.elementAt(k);

											currentLevelList.remove(k);
											currentLevelList.insertElementAt(sFirstNodeID, j);

											nIndexOfFirstParent = ((Vector)nodeLevelList.get(i -1)).indexOf(((Vector)htNodesAbove.get(sFirstNodeID)).firstElement());
											nodeToSort = (NodeSummary)htNodes.get(sFirstNodeID);
											break ;
										} else if((nIndexOfFirstParent == nIndexOfSecondParent) && (sID.equals(sParentNode))){
											String sSecondNodeParent = node.getParentNode().getId();
											int nIndexOfSecondNodeParent = ((Vector)nodeLevelList.get(i -1)).indexOf(sSecondNodeParent);
											String sFirstNodeParent = nodeToSort.getParentNode().getId();
											int nIndexOfFirstNodeParent = ((Vector)nodeLevelList.get(i -1)).indexOf(sFirstNodeParent);
											if ((nIndexOfSecondNodeParent > -1) && ( nIndexOfFirstNodeParent > -1)){
												if ((nIndexOfFirstParent >= nIndexOfSecondNodeParent) && (nIndexOfFirstNodeParent > nIndexOfSecondNodeParent)) {
													String sFirstNodeID = (String)currentLevelList.elementAt(k);

													currentLevelList.remove(k);
													currentLevelList.insertElementAt(sFirstNodeID, j);

													nIndexOfFirstParent = ((Vector)nodeLevelList.get(i -1)).indexOf(((Vector)htNodesAbove.get(sFirstNodeID)).firstElement());
													nodeToSort = (NodeSummary)htNodes.get(sFirstNodeID);
													break ;
												}//end else if
											}//end if
										}//end else
									}//end if
								}//end for

// 								enter the the node id in the hashtable 	to avoid redundant comparison

								Vector vtMarkCheckedNodesForKLoop = new Vector();
								if (htNodesChecked.get(node.getId()) !=  null){
									vtMarkCheckedNodesForKLoop = (Vector) htNodesChecked.get(node.getId());
								}// end if
								vtMarkCheckedNodesForKLoop.add(nodeToSort.getId());
								htNodesChecked.put(node.getId(),vtMarkCheckedNodesForKLoop);

								Vector vtMarkCheckedNodesForJLoop = new Vector();
								if (htNodesChecked.get(nodeToSort.getId()) !=  null){
									vtMarkCheckedNodesForJLoop = (Vector) htNodesChecked.get(nodeToSort.getId());
								}// end if
								vtMarkCheckedNodesForJLoop.add(node.getId());
								htNodesChecked.put(nodeToSort.getId(),vtMarkCheckedNodesForJLoop);

							}//end if
						}//end if
					}//end for
				}//end for
			}//end for
		}//end for

// end edit  - Lakshmi

		sortNodesBelow();
		sortNodeChildWise();

	}//SortNodeParentWise

//begin edit - Lakshmi (10/25/05)
	/**
	 *  Helper method. Sort the node calculated by thier child node position.
	 *
	 */
	private void sortNodeChildWise(){

		for(int i = nodeLevelList.size() -1; i > 0 ; i-- ) {
			Vector vtLevelNodes = (Vector) nodeLevelList.get(i);
			if (vtLevelNodes == null){
				continue ;
			}
			for(int j = 0; j < vtLevelNodes.size(); j++ ) {
				String sNodeId = (String) vtLevelNodes.get(j);
				Vector vtNodesAbove = (Vector) htNodesAbove.get(sNodeId);
				if ((vtNodesAbove != null ) && (vtNodesAbove.size() > 0)) {
					int nSize = vtNodesAbove.size();
					switch (nSize) {
					case 1:

						break;
					default :
						//currently arranging if the child node is first or last child of a parent
						String sFirstParent = (String) vtNodesAbove.firstElement();
						Vector vtNodesBelowFP = (Vector) htNodesBelow.get(sFirstParent);
						int nIndex 			= vtNodesBelowFP.indexOf(sNodeId);
						int indexOfFP = ((Vector)nodeLevelList.get(i-1)).indexOf(sFirstParent);

						if ((nIndex == 0) || (nIndex == (vtNodesBelowFP.size() - 1))){
							abovePointerPos = 0;
							belowPointerPos = 1;
							childSizeBelow = 0;
							childSizeAbove = 0;
							for(int loop = 1; loop < vtNodesAbove.size(); loop++){
								String sParent = (String) vtNodesAbove.get(loop);
								int indexOfParent = ((Vector)nodeLevelList.get(i-1)).indexOf(sParent);
								if ((indexOfFP > -1) && (indexOfParent > -1)){
									Vector vtFPParent = (Vector)htNodesAbove.get(sFirstParent);
									Vector vtParent = (Vector)htNodesAbove.get(sParent);
									int indexFP = ((Vector)nodeLevelList.get(i-1)).indexOf(sFirstParent);
									if(((vtFPParent != null) && (vtParent != null))){
										int fpParentSize = vtFPParent.size();
										int parentSize = vtParent.size();
										if (((fpParentSize == 1) && (parentSize == 1))) {
											String fpParentId = (String) vtFPParent.firstElement();
											String lpParentId = (String) vtParent.firstElement();
											if(fpParentId == lpParentId){
												if(indexOfParent > (indexFP + 1)){
													swapNodes(sNodeId, sFirstParent, sParent, i, nIndex);
												}//end if
											}//end if
										}//end if
									} else if ((vtParent == null)&& (indexOfParent > (indexFP + 1))) {
										swapNodes(sNodeId, sFirstParent, sParent, i, nIndex);
									}//end if else
								}//end if
							}//end for
						}//end if
						break;
					}//end switch
				}//end if
			}//end for
		}//end for
		sortNodesBelow();
		sortNodesAbove();
	}//sortNodeChildWise


/**
 * Helper Method. To look for a node whose parent is placed above the parent of the current node in the same level and move it up.
 * @param sNodeId      	The ID of the node which is compared to other nodes in the level
 * @param sFirstParent  The first parent ID of the node
 * @param sParent		The parent ID of the comparing node
 * @param i				Level at which the node is
 * @param nIndex		Index of the node in its parent's child vector
 */
	private void swapNodes(String sNodeId, String sFirstParent, String sParent, int i, int nIndex){
		Vector vtLevelNodes = (Vector) nodeLevelList.get(i);
		Vector vtNodesBelowFP = (Vector) htNodesBelow.get(sFirstParent);
		int indexFP = ((Vector)nodeLevelList.get(i-1)).indexOf(sFirstParent);
		Vector vtNodesBelow = (Vector) htNodesBelow.get(sParent);
		if (vtNodesBelow.size() == 1){
			((Vector)nodeLevelList.get(i-1)).remove(sParent);
			if(nIndex == 0){
				((Vector)nodeLevelList.get(i-1)).insertElementAt(sParent, indexFP - abovePointerPos);
			} else{
				((Vector)nodeLevelList.get(i-1)).insertElementAt(sParent, indexFP + belowPointerPos);
			}//end if else
		} else {
			int index = vtLevelNodes.indexOf(sNodeId);
			boolean isSingleParentNodes = true;
			boolean isSet = false;
			for(int k = 0; k < vtNodesBelow.size(); k++) {
				String nodeId = (String) vtNodesBelow.get(k);
				int size = ((Vector)htNodesAbove.get(nodeId)).size();
				if ((nodeId != sNodeId )&&(size != 1)){
					isSingleParentNodes = false;
					break;
				}//end if
			}//end for
			if(isSingleParentNodes){
				((Vector)nodeLevelList.get(i-1)).remove(sParent);
				if((nIndex == 0) && (!isSet)){

					((Vector)nodeLevelList.get(i-1)).insertElementAt(sParent, indexFP - abovePointerPos);
					abovePointerPos ++;
					for(int k = 0; k < vtNodesBelow.size(); k++) {
						if(! sNodeId.equals(vtNodesBelow.get(k))) {
							((Vector)nodeLevelList.get(i)).remove(vtNodesBelow.get(k));
							((Vector)nodeLevelList.get(i)).insertElementAt(vtNodesBelow.get(k), index - childSizeAbove);
						}//end if
					}//end for
					childSizeAbove += vtNodesBelow.size() - 1;
					if(nIndex == (vtNodesBelowFP.size() - 1)) {
						isSet = true;
					}//end if
				 } else if((nIndex == (vtNodesBelowFP.size() - 1)) || (isSet == true)){
					((Vector)nodeLevelList.get(i-1)).insertElementAt(sParent, indexFP + belowPointerPos);
					belowPointerPos ++;
					for(int k = vtNodesBelow.size()-1; k >= 0; k--) {
						if(! sNodeId.equals(vtNodesBelow.get(k))) {
							((Vector)nodeLevelList.get(i)).remove(vtNodesBelow.get(k));
							((Vector)nodeLevelList.get(i)).insertElementAt(vtNodesBelow.get(k), (index + 1 + childSizeBelow));
						}//end if
					}//end for
					childSizeBelow += vtNodesBelow.size() - 1 ;
				 }//end if else
			}//end if
		}//end if else
	}//swapNodes

	/**
	 * This method is to sort parent nodes based on nodes sorted child wise.
	 */

	private void sortNodesAbove(){
		for (int i = nodeLevelList.size() -1; i >= 0  ; i--){
			Vector vtLevelNodes = (Vector) nodeLevelList.get(i);
			for(int j = 0; j < vtLevelNodes.size(); j++) {
				Vector vtNodesAbove = (Vector) htNodesAbove.get((String) vtLevelNodes.get(j));
				if(vtNodesAbove != null){
					for(int  k = vtNodesAbove.size() -1; k >= 0; k --){
						for( int l = 0; l <= k - 1; l++){
							int index = ((Vector)nodeLevelList.get(i - 1)).indexOf((String) vtNodesAbove.get(l));
							int indxBelow = ((Vector)nodeLevelList.get(i - 1)).indexOf((String) vtNodesAbove.get(l + 1));
								if((indxBelow != -1 ) && (index != -1 ) && (index > indxBelow)){
									String temp = (String) vtNodesAbove.get(l + 1);
									vtNodesAbove.remove(l+1);
									vtNodesAbove.insertElementAt(temp, l);
								}//end if
						}//end for
					}//end for
					htNodesAbove.put((String) vtLevelNodes.get(j),vtNodesAbove);
				}//end if
			}//end for
		}//end for
	}//sortNodesAbove

	/**
	 * This method is to sort child nodes below based on nodes sorted parent wise.
	 */

	private void sortNodesBelow(){
		for (int i = 0; i < nodeLevelList.size() ; i++){
			Vector vtLevelNodes = (Vector) nodeLevelList.get(i);
			for(int j = 0; j < vtLevelNodes.size(); j++) {
				Vector vtNodesBelow = (Vector) htNodesBelow.get((String) vtLevelNodes.get(j));
				if(vtNodesBelow != null){
					for(int  k = vtNodesBelow.size() -1; k >= 0; k --){
						for( int l = 0; l <= k - 1; l++){
							int index = ((Vector)nodeLevelList.get(i + 1)).indexOf((String) vtNodesBelow.get(l));
							int indxBelow = ((Vector)nodeLevelList.get(i + 1)).indexOf((String) vtNodesBelow.get(l + 1));
								if((indxBelow != -1 ) && (index != -1 ) && (index > indxBelow)){
									String temp = (String) vtNodesBelow.get(l + 1);
									vtNodesBelow.remove(l+1);
									vtNodesBelow.insertElementAt(temp, l);
								}//end if
						}//end for
					}//end for
					htNodesBelow.put((String) vtLevelNodes.get(j),vtNodesBelow);
				}//end if
			}//end for
		}//end for
	}//end sortNodesBelow

//	end edit - Lakshmi

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
					String nodeId = (String)((Vector)nodeLevelList.elementAt(0)).elementAt(i);
					yPosition = calculateYPosition(viewFrame, nodeId, yPosition);
				}
			}
		}
	}

	private Hashtable nodesYed = new Hashtable(51);
	
	/**
	 * Helper Method. This method is added to enable the child nodes to be positioned at the center relative to the parent.
	 * @param viewFrame com.compendium.ui.UIViewFrame, the frame of the node to calculate the y positions for.
	 * @param nodeId, the id of the node to calculate the y position for.
	 * @param yPosition, the starting y position.
	 * @return int, the calculated y position.
	 */
	private int calculateYPosition(UIViewFrame viewFrame, String nodeId, int yPosition) {

		Integer count = new Integer(1);
		if(nodesYed.containsKey(nodeId)){
			count = (Integer) nodesYed.get(nodeId);
			if(count.intValue() > recursionCount)
				return yPosition;
			else {
				count = new Integer(count.intValue() + 1);
				nodesYed.put(nodeId, count);
			}
		} else {
			nodesYed.put(nodeId, count);
		}
	/*	if(recursionCount > htNodes.size() || nodesYed.containsKey(nodeId)){
			return yPosition;
		} 
		nodesYed.put(nodeId, nodeId);
		recursionCount ++;
	*/
		UIViewPane viewPane = ((UIMapViewFrame)viewFrame).getViewPane();

		Vector childNodeList = (Vector) htNodesBelow.get(nodeId);
		UINode parentNode = ((UINode)viewPane.get(nodeId));

		if (childNodeList != null) {

			for (int i = 0; i < childNodeList.size(); i++) {
				yPosition = calculateYPosition(viewFrame, (String)childNodeList.elementAt(i), yPosition);
			}
			int firstChildPosition = ((UINode)viewPane.get((String)childNodeList.firstElement())).getNodePosition().getYPos();
			int lastChildPosition =  ((UINode)viewPane.get((String)childNodeList.lastElement())).getNodePosition().getYPos();

			UINode lastChildNode = (UINode)viewPane.get((String)childNodeList.lastElement()) ;
			int newYPosition = firstChildPosition + (lastChildPosition + lastChildNode.getHeight() - firstChildPosition)/2;
			newYPosition = newYPosition - parentNode.getHeight()/2;

			int nodeLevel = ((Integer)htNodesLevel.get(nodeId)).intValue();
			int indexOfNode = ((Vector)nodeLevelList.elementAt(nodeLevel - 1)).indexOf(nodeId);
			int indexOfPreviousNode = indexOfNode - 1;
			int previousNodeYPosition = 0;

			UINode node = ((UINode)viewPane.get(nodeId));

			double scale = viewPane.getScale();
			if (indexOfPreviousNode > -1) {
				UINode previousNode = ((UINode)viewPane.get((String)((Vector)nodeLevelList.elementAt(nodeLevel-1)).elementAt(indexOfPreviousNode)));
				previousNodeYPosition = previousNode.getNodePosition().getYPos();

				// ALLOW FOR FACT VIEW MIGHT BE SCALED
				int nodeHeight = previousNode.getHeight();
				Point p1 = UIUtilities.scalePoint(nodeHeight, nodeHeight, scale);
				nodeHeight = p1.x;

				if ((previousNodeYPosition + nodeHeight + FormatProperties.arrangeLeftVerticalGap) > newYPosition) {
					newYPosition = previousNodeYPosition + nodeHeight + (FormatProperties.arrangeLeftVerticalGap);
				}
			}

			Point pos = node.getNodePosition().getPos();

			node.getNodePosition().setPos(new Point(pos.x, newYPosition));
			if (newYPosition > yPosition) {
				yPosition = newYPosition;
			}
		} else {
//begin edit - Lakshmi (10/25/05)
// 				if a child node has 2 parent , on arrange the node was dropping down. To avoid this, once the node's Y position is set
// 				it is not set again.
			if((htNodeYPositionSet.get(nodeId) == null) || (((Boolean)htNodeYPositionSet.get(nodeId)).booleanValue() == false)) {
				UINode node = ((UINode)viewPane.get(nodeId));
//end edit - Lakshmi
//	begin edit, Alex Jarocha-Ernst
				//previously, this didn't do anything with the previous node of a leaf
				//this caused problems when the first node on a level had children, but
				//successive ones did not
				int nodeLevel = ((Integer)htNodesLevel.get(nodeId)).intValue();
				int indexOfNode = ((Vector)nodeLevelList.elementAt(nodeLevel - 1)).indexOf(nodeId);
				int indexOfPreviousNode = indexOfNode - 1;
				int previousNodeYPosition = 0;
				Point pos = node.getNodePosition().getPos();

				double scale = viewPane.getScale();

				if(indexOfPreviousNode > -1) {
					
					String  sPreviousNodeID = (String)((Vector)nodeLevelList.elementAt(nodeLevel-1)).elementAt(indexOfPreviousNode);
					UINode previousNode = ((UINode)viewPane.get(sPreviousNodeID));
					// BUG FIX - Lakshmi 9/15/06 for NullPointerException
					NodeSummary previousNodeSum = (NodeSummary)htNodes.get(sPreviousNodeID);
					previousNodeYPosition = previousNode.getNodePosition().getYPos();

					// ALLOW FOR FACT VIEW MIGHT BE SCALED
					int nodeHeight = previousNode.getHeight();
					Point p1 = UIUtilities.scalePoint(nodeHeight, nodeHeight, scale);
					nodeHeight = p1.x;
//begin edit - Lakshmi (10/25/05)
					// This is to put the parent at the center of its children
					Vector vtParentNodes = (Vector) htNodesAbove.get(node.getNode().getId());
					if(vtParentNodes != null) {
						String sFirstParentNodeId = (String) vtParentNodes.firstElement();
						int nLevel = ((Integer)htNodesLevel.get(sFirstParentNodeId)).intValue();
						int indexOfParent = ((Vector) nodeLevelList.get( nLevel - 1)).indexOf(sFirstParentNodeId);
						if ((indexOfParent -1 ) >= 0 ) {
							String sParentPreviousNodeId = (String)((Vector)nodeLevelList.get(nLevel - 1)).get(indexOfParent - 1);
							UINode parentPreviousNode = ((UINode)viewPane.get(sParentPreviousNodeId));
							int parentPreviousNodePos = parentPreviousNode.getNodePosition().getYPos();
							int parentPreviousNodeHt = parentPreviousNode.getHeight();

							String sPreviousNodeParentId =  previousNodeSum.getParentNode().getId();

							if (!((Vector)htNodesAbove.get(nodeId)).contains(sPreviousNodeParentId)){
								yPosition = parentPreviousNodePos + parentPreviousNodeHt + FormatProperties.arrangeLeftVerticalGap ;
							}//end if
						}//end if
					}//end if


//end edit - Lakshmi
					if ((previousNodeYPosition + nodeHeight +FormatProperties.arrangeLeftVerticalGap) > yPosition) {
						yPosition = previousNodeYPosition + nodeHeight + (FormatProperties.arrangeLeftVerticalGap);
					}
					node.getNodePosition().setPos(new Point(pos.x, yPosition));
				} else {
//end edit, Alex Jarocha-Ernst

					String sNodeId = node.getNode().getId();
					int yPos = findPreviousNodeYPos(viewPane, sNodeId);
					if(yPosition > yPos) {
						node.getNodePosition().setPos(new Point(pos.x, yPosition));
					} else {
						node.getNodePosition().setPos(new Point(pos.x, yPos));
					}

					// ALLOW FOR FACT VIEW MIGHT BE SCALED
					int nodeHeight = node.getHeight();
					Point p1 = UIUtilities.scalePoint(nodeHeight, nodeHeight, scale);
					nodeHeight = p1.x;

					yPosition += (FormatProperties.arrangeLeftVerticalGap + nodeHeight);

				}
		    }
		}//end else
		htNodeYPositionSet.put(nodeId, new Boolean(true));
		return yPosition;
	}//end calculateYPosition

	private Hashtable nodesYFinalized = new Hashtable();
	
	/**
	 * This method is to enable the child nodes to be positioned at the center relative to the parent after compact.
	 * @param viewFrame com.compendium.ui.UIViewFrame, the frame of the node to finalize the y positions for.
	 * @param sNodeID, the id of the node to finalize the y position for.
	 */
	private void finalizeYPos(UIViewFrame viewFrame, String sNodeID){

		Integer count = new Integer(1);
		if(nodesYFinalized.containsKey(sNodeID)){
			count = (Integer) nodesYFinalized.get(sNodeID);
			if(count.intValue() > recursionCount)
				return ;
			else {
				count = new Integer(count.intValue() + 1);
				nodesYFinalized.put(sNodeID, count);
			}
		} else {
			nodesYFinalized.put(sNodeID, count);
		}
	/*	if(recursionCount < htNodes.size() || !nodesYFinalized.containsKey(sNodeID)) {
			nodesYFinalized.put(sNodeID, sNodeID);
			recursionCount ++;
		} else {
			return;
		}
	*/
		UIViewPane viewPane = ((UIMapViewFrame)viewFrame).getViewPane();
		Vector vtChildNodes = (Vector)htNodesBelow.get(sNodeID);
		UINode oNode = ((UINode)viewPane.get(sNodeID));
		Point pos = oNode.getNodePosition().getPos();

		double scale = viewPane.getScale();
		
		if(vtChildNodes != null){
			for (int i =0; i< vtChildNodes.size(); i++){
				finalizeYPos(viewFrame, (String)vtChildNodes.get(i));
			}//end for
			Vector vtChildren = (Vector)htNodesBelow.get(sNodeID);

			int nodePosition = oNode.getNodePosition().getYPos();

			UINode oFirstChild = (UINode)viewPane.get((String)vtChildren.firstElement());
			UINode oLastChild = (UINode)viewPane.get((String)vtChildren.lastElement());

			String sFirstParentOfFC = (String)((Vector) htNodesAbove.get(vtChildren.firstElement())).firstElement();
			String sFirstParentOfLC = (String)((Vector) htNodesAbove.get(vtChildren.lastElement())).firstElement();

			if (sFirstParentOfFC.equals(sNodeID) && sFirstParentOfLC.equals(sNodeID)){
				int firstChildPosition = oFirstChild.getNodePosition().getYPos();
				int lastChildPosition =  oLastChild.getNodePosition().getYPos();

				int centerPosition = firstChildPosition + (lastChildPosition + oLastChild.getHeight() - firstChildPosition)/2;
				centerPosition = centerPosition - oNode.getHeight()/2;

				int nodeLevel = ((Integer)htNodesLevel.get(sNodeID)).intValue();
				int indexOfNode = ((Vector)nodeLevelList.elementAt(nodeLevel - 1)).indexOf(sNodeID);
				int indexOfPreviousNode = indexOfNode - 1;
				int previousNodeYPosition = 0;

				if (indexOfPreviousNode > -1) {
					UINode previousNode = ((UINode)viewPane.get((String)((Vector)nodeLevelList.elementAt(nodeLevel-1)).elementAt(indexOfPreviousNode)));
					previousNodeYPosition = previousNode.getNodePosition().getYPos();

					int nodeHeight = previousNode.getHeight();
					Point p2 = UIUtilities.scalePoint(nodeHeight, nodeHeight, scale);
					nodeHeight = p2.x;

					if ((previousNodeYPosition + nodeHeight + FormatProperties.arrangeLeftVerticalGap) > nodePosition) {
						nodePosition = previousNodeYPosition + nodeHeight + (FormatProperties.arrangeLeftVerticalGap);
						oNode.getNodePosition().setPos(new Point (pos.x , nodePosition));
					}//end if
				}//end if
				if(nodePosition <= centerPosition){
					oNode.getNodePosition().setPos(new Point (pos.x , centerPosition));
				} else {
					int amount = nodePosition - centerPosition ;
					//recursionCount = 0;
					getChildNodesDown(viewPane, sNodeID, amount);
				}//end else
			}//end if
		}//end if
		int nodePosition = oNode.getNodePosition().getYPos();

		int nodeLevel = ((Integer)htNodesLevel.get(sNodeID)).intValue();
		int indexOfNode = ((Vector)nodeLevelList.elementAt(nodeLevel - 1)).indexOf(sNodeID);
		int indexOfPreviousNode = indexOfNode - 1;
		int previousNodeYPosition = 0;

		if (indexOfPreviousNode > -1) {
			UINode previousNode = ((UINode)viewPane.get((String)((Vector)nodeLevelList.elementAt(nodeLevel-1)).elementAt(indexOfPreviousNode)));
			previousNodeYPosition = previousNode.getNodePosition().getYPos();

			int nodeHeight = previousNode.getHeight();
			Point p1 = UIUtilities.scalePoint(nodeHeight, nodeHeight, scale);
			nodeHeight = p1.x;

			if ((previousNodeYPosition + nodeHeight) > nodePosition) {
				nodePosition = previousNodeYPosition + nodeHeight + (FormatProperties.arrangeLeftVerticalGap);
				oNode.getNodePosition().setPos(new Point(pos.x , nodePosition));
			}//end if
		}//end if


	}//finalizeYPos

	/**
	 * Helper Method. This method helps to put parent at the center of the children.
	 * @param viewPane com.compendium.ui.UIViewPane, the frame of the node to finalize the y positions for.
	 * @param sNodeID, the id of the node whose y position has to be increased.
	 * @param amount, the amount to increase in Y position
	 */

	private void getChildNodesDown (UIViewPane viewPane, String sNodeID, int amount){

	/*	if(recursionCount > (htNodes.size() + 5 )){
			return ;
		}
		recursionCount ++;
	*/	
		UINode oNode = ((UINode)viewPane.get(sNodeID));
		Point pos = oNode.getNodePosition().getPos();
		Vector vtChildNodes = (Vector) htNodesBelow.get(sNodeID);
		if (vtChildNodes != null){
			for(int i = 0; i < vtChildNodes.size(); i++){
				getChildNodesDown(viewPane, (String)vtChildNodes.get(i), amount);
			}//end for
			Vector vtChildren = (Vector)htNodesBelow.get(sNodeID);

			int nodePosition = oNode.getNodePosition().getYPos();
			UINode lastNode = (UINode)viewPane.get((String)vtChildren.lastElement());
			int firstChildPosition = ((UINode)viewPane.get((String)vtChildren.firstElement())).getNodePosition().getYPos();
			int lastChildPosition =  lastNode.getNodePosition().getYPos();

			int centerPosition = firstChildPosition + (lastChildPosition + lastNode.getHeight() - firstChildPosition)/2;
			centerPosition = centerPosition - oNode.getHeight()/2;

			int nodeLevel = ((Integer)htNodesLevel.get(sNodeID)).intValue();
			int indexOfNode = ((Vector)nodeLevelList.elementAt(nodeLevel - 1)).indexOf(sNodeID);
			int indexOfPreviousNode = indexOfNode - 1;
			int previousNodeYPosition = 0;

			if (indexOfPreviousNode > -1) {
				UINode previousNode = ((UINode)viewPane.get((String)((Vector)nodeLevelList.elementAt(nodeLevel-1)).elementAt(indexOfPreviousNode)));
				previousNodeYPosition = previousNode.getNodePosition().getYPos();

				int nodeHeight = previousNode.getHeight();
				Point p2 = UIUtilities.scalePoint(nodeHeight, nodeHeight, viewPane.getScale());
				nodeHeight = p2.x;

				if ((previousNodeYPosition + nodeHeight + FormatProperties.arrangeLeftVerticalGap) > nodePosition) {
					nodePosition = previousNodeYPosition + nodeHeight + (FormatProperties.arrangeLeftVerticalGap);
					oNode.getNodePosition().setPos(new Point (pos.x , nodePosition));
				}//end if
			}//end if
			if(nodePosition <= centerPosition){
				oNode.getNodePosition().setPos(new Point (pos.x , centerPosition));

			}//end if */
		} else {//end if
			int yPos = oNode.getNodePosition().getYPos();
			yPos += amount;
			oNode.getNodePosition().setPos(new Point(pos.x, yPos));
		}//end else
	}//end getChildNodesDown

	/**
	 *  Helper Method. To find y position for a node so that it is placed around its parent.
	 * @param viewPane com.compendium.ui.UIViewPane, the frame of the node.
	 * @param sNodeID, the id of the node whose previous node y position has to be found.
	 *
	 * @return int, The y position of the previous node.
	 */
	private int findPreviousNodeYPos (UIViewPane viewPane, String nodeID){
		int yPos = 0;
		Vector vtParents = (Vector)htNodesAbove.get(nodeID);
		if(vtParents != null){
			String sParentId = (String) vtParents.firstElement();
			int index = ((Vector)nodeLevelList.get(((Integer)htNodesLevel.get(sParentId)).intValue() -1)).indexOf(sParentId);
			if (index > 0){
				String sPreviousNodeID = (String)((Vector)nodeLevelList.get(((Integer)htNodesLevel.get(sParentId)).intValue() -1)).get(index - 1);
				UINode previousNode = (UINode) viewPane.get(sPreviousNodeID);
				NodePosition nodePosition = previousNode.getNodePosition();
				yPos = nodePosition.getYPos() + previousNode.getHeight() + FormatProperties.arrangeLeftVerticalGap;
			} else {
				yPos = findPreviousNodeYPos(viewPane, sParentId);
			}//end else
		}//end if
		return yPos;
	}//findPreviousNodeYpos

//end edit -Lakshmi

	private Hashtable nodesCompacted = new Hashtable(51);
	private Hashtable nodesCheckedForCompact = new Hashtable(51);
	private Hashtable nodesCompact = new Hashtable(51);
	
	/**
	 * Helper Method. This method compacts the nodes in the given list.
	 * @param viewFrame com.compendium.ui.UIViewFrame, the frame of the node to compact.
	 * @param nodeList, the list of node to compact.
	 */
	private void compactNodesInList(UIViewFrame viewFrame, Vector nodeList) {

		UIViewPane viewPane = ((UIMapViewFrame)viewFrame).getViewPane();

		Hashtable compactDoneForNodes = new Hashtable();
		Vector    vtLevelChecked = new Vector();

		for (int i = 0; i < nodeList.size(); i++) {
			String nodeID = (String)nodeList.elementAt(i);
			Integer count = new Integer(1);
			if(nodesCompacted.containsKey(nodeID)){
				count = (Integer) nodesCompacted.get(nodeID);
				if(count.intValue() > recursionCount)
					continue ;
				else {
					count = new Integer(count.intValue() + 1);
					nodesCompacted.put(nodeID, count);
				}
			} else {
				nodesCompacted.put(nodeID, count);
			}
	/*		if (recursionCount < htNodes.size() || !nodesCompacted.containsKey(nodeID)) {
				nodesCompacted.put(nodeID, nodeID);
				recursionCount ++;
			} else {
				continue;
			}
	*/		
			Vector childNodeList = (Vector)htNodesBelow.get(nodeID);

			if (childNodeList != null) {
				compactNodesInList(viewFrame, childNodeList);
			}
		}
		
		int currentNodeYPosition = 0;
		int previousNodeYPosition = 0;
		int compactAmount = 0;
//begin edit - Lakshmi (10/25/05)
//		This is to center the 1st node (if its a parent)at any level .
		if ((nodeList  != null) &&(nodeList.size() >= 1)) {
			UINode node = ((UINode)viewPane.get((String)nodeList.elementAt(0)));
			Vector childNodeList = (Vector) htNodesBelow.get(nodeList.get(0));
			if(childNodeList != null){

				UINode firstNode = (UINode)viewPane.get((String)childNodeList.firstElement());
				int firstNodeYPosition = firstNode.getNodePosition().getYPos();

				UINode lastNode = (UINode)viewPane.get((String)childNodeList.lastElement());
				int lastNodeYPosition = lastNode.getNodePosition().getYPos();

				Point pos = node.getNodePosition().getPos();

				int centerPosition = firstNodeYPosition + (lastNodeYPosition + lastNode.getHeight() - firstNodeYPosition)/2;
				centerPosition = centerPosition - node.getHeight()/2;
				int level = ((Integer)htNodesLevel.get(node.getNode().getId())).intValue() - 1;
				int index = ((Vector)nodeLevelList.get(level)).indexOf(node.getNode().getId());
				if(index < 1){
					node.getNodePosition().setPos(new Point(pos.x, centerPosition));
				} else {
					UINode previousNode = (UINode)viewPane.get((String)((Vector)nodeLevelList.get(level)).get(index - 1));
					int previousNodeYPos = previousNode.getNodePosition().getYPos();
					int previousNodeHeight = previousNode.getHeight();
					if(centerPosition > previousNodeYPos + previousNodeHeight + FormatProperties.arrangeLeftVerticalGap ){
						node.getNodePosition().setPos(new Point(pos.x, centerPosition));
					} else {
						node.getNodePosition().setPos(new Point(pos.x, previousNodeYPos + previousNodeHeight + FormatProperties.arrangeLeftVerticalGap));
					}
				}//end if
			}//end if
		}//end if
//end edit - Lakshmi

		double scale = viewPane.getScale();
		// this will arrange other nodes based on the above arrangement of 1st node.
		for (int i = 1; i < nodeList.size(); i++) {
			UINode currentNode = ((UINode)viewPane.get((String)nodeList.elementAt(i)));
			UINode previousNode = ((UINode)viewPane.get((String)nodeList.elementAt(i-1)));
			currentNodeYPosition = currentNode.getNodePosition().getYPos();
			previousNodeYPosition = previousNode.getNodePosition().getYPos();

			UINode node = ((UINode)viewPane.get((String)nodeList.elementAt(i)));
			// ALLOW FOR FACT VIEW MIGHT BE SCALED
			int nodeHeight = previousNode.getHeight();
			Point p1 = UIUtilities.scalePoint(nodeHeight, nodeHeight, scale);
			nodeHeight = p1.x;

			if ((currentNodeYPosition - previousNodeYPosition - nodeHeight) > FormatProperties.arrangeLeftVerticalGap) {
				compactDoneForNodes.clear();

				compactAmount = checkCompact(viewFrame,
											 (String)nodeList.elementAt(i),
											 currentNodeYPosition - previousNodeYPosition - FormatProperties.arrangeLeftVerticalGap - nodeHeight,
											 compactDoneForNodes, vtLevelChecked);
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
				Point pos = node.getNodePosition().getPos();

				if (indexOfPreviousNode > -1) {
					previousNodeYPosition = previousNode.getNodePosition().getYPos();

					// ALLOW FOR FACT VIEW MIGHT BE SCALED
					previousNodeHeight = previousNode.getHeight();
					Point p2 = UIUtilities.scalePoint(previousNodeHeight, previousNodeHeight, scale);
					previousNodeHeight = p2.x;


					int centerPosition = firstNodeYPosition + (lastNodeYPosition + lastNode.getHeight() - firstNodeYPosition)/2;
					centerPosition = centerPosition - node.getHeight()/2;
					if ((previousNodeYPosition + previousNodeHeight + FormatProperties.arrangeLeftVerticalGap) < centerPosition) {
						node.getNodePosition().setPos(new Point(pos.x, centerPosition));
					}
					else {
						node.getNodePosition().setPos(new Point(pos.x, previousNodeYPosition + previousNodeHeight + FormatProperties.arrangeLeftVerticalGap));
					}//end else
				}//end if
			}//end if
		}//end for
	}//compactNodesInList

	/**
	 * Helper Method. This method checks the compactness of the node with the given id.
	 * @param viewFrame com.compendium.ui.UIViewFrame, the frame of the node to compact.
	 * @param nodeId, the id of the node to check.
	 * @param compactAmount, the amount to compact by.
	 * @param compactDoneForNodes, nodes already compacted.
	 */
	private int checkCompact(UIViewFrame viewFrame, String nodeId, int compactAmount, Hashtable compactDoneForNodes, Vector vtLevelChecked) {

		UIViewPane viewPane = ((UIMapViewFrame)viewFrame).getViewPane();

		compactDoneForNodes.put(nodeId, new Boolean(false));
		
		Integer count = new Integer(1);
		if(nodesCheckedForCompact.containsKey(nodeId)){
			count = (Integer) nodesCheckedForCompact.get(nodeId);
			if(count.intValue() > recursionCount)
				return compactAmount;
			else {
				count = new Integer(count.intValue() + 1);
				nodesCheckedForCompact.put(nodeId, count);
			}
		} else {
			nodesCheckedForCompact.put(nodeId, count);
		}

		//get the level of this node
		UINode currentNode = ((UINode)viewPane.get(nodeId));
		int	currentNodeYPosition = currentNode.getNodePosition().getYPos();
		int level = ((Integer)htNodesLevel.get(nodeId)).intValue();
		int indexOfCurrentNode = ((Vector)nodeLevelList.elementAt(level-1)).indexOf(nodeId);
		int indexOfPreviousNode = indexOfCurrentNode - 1;

		if (indexOfPreviousNode < 0) {
			if (currentNodeYPosition < compactAmount) {
				compactAmount = currentNodeYPosition - 20;
			}
			indexOfPreviousNode = 0;

		}
		else {
			UINode previousNode = ((UINode)viewPane.get((String)((Vector)nodeLevelList.elementAt(level-1)).elementAt(indexOfPreviousNode)));
			int previousNodeYPosition = previousNode.getNodePosition().getYPos();

			double scale = viewPane.getScale();
			
			// ALLOW FOR FACT VIEW MIGHT BE SCALED
			int nodeHeight = currentNode.getHeight();
			Point p = UIUtilities.scalePoint(nodeHeight, nodeHeight, scale);
			nodeHeight = p.x;

			// ALLOW FOR FACT VIEW MIGHT BE SCALED
			int previousNodeHeight = previousNode.getHeight();
			Point p2 = UIUtilities.scalePoint(previousNodeHeight, previousNodeHeight, scale);
			previousNodeHeight = p2.x;
			if (!vtLevelChecked.contains(htNodesLevel.get(nodeId))) {
				if ( (currentNodeYPosition - previousNodeYPosition - FormatProperties.arrangeLeftVerticalGap - previousNodeHeight) <
						compactAmount) {
						compactAmount = currentNodeYPosition - previousNodeYPosition - FormatProperties.arrangeLeftVerticalGap - previousNodeHeight;
					}//end if
			}//end if

		}//end else
		Vector childNodeList = (Vector)htNodesBelow.get(nodeId);
		if (childNodeList != null) {
			compactAmount = checkCompact(viewFrame, (String)childNodeList.elementAt(0),compactAmount, compactDoneForNodes, vtLevelChecked);
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
		Integer count = new Integer(1);
		if(nodesCompact.containsKey(nodeId)){
			count = (Integer) nodesCompact.get(nodeId);
			if(count.intValue() > recursionCount) {
				return ;
			}
			else {
				count = new Integer(count.intValue() + 1);
				nodesCompact.put(nodeId, count);
			}
		} else {
			nodesCompact.put(nodeId, count);
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

			if ((pos.y - compactAmount) < (previousNodeYPosition + previousNodeHeight + FormatProperties.arrangeLeftVerticalGap)) {
				yPosition = previousNodeYPosition + previousNodeHeight + FormatProperties.arrangeLeftVerticalGap;
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
// begin edit - Lakshmi (10/25/05)
				String sParentID = (String)((Vector) htNodesAbove.get(nodeList.elementAt(i))).firstElement();
				if (nodeId.equals(sParentID)) {
					compact(viewFrame, (String) nodeList.elementAt(i), compactAmount, compactDoneForNodes);
				}// end if
//end edit - Lakshmi
			}//end for
		}//end if
	}//compact

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
