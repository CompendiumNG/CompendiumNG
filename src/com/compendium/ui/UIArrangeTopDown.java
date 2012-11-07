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
public class UIArrangeTopDown implements IUIArrange {

	/** The nodes to arrange.*/
	private	Hashtable				htNodes 				= new Hashtable(51);

	/** The nodes against their levels.*/
	private Hashtable				htNodesLevel 			= new Hashtable(51);

	/** The nodes and their children.*/
	private Hashtable				htNodesBelow 			= new Hashtable(51);

	/** The nodes against their ancestry.*/
	private Hashtable				htNodesAbove 			= new Hashtable();

	/** Used for undo operations.*/
	private Hashtable				nodePositionsCloneHashtable = new Hashtable();

	/** Used for redo operations.*/
	private Hashtable				nodePositionsCloneHashtableForRedo = new Hashtable();

	/** Nodes at level one.*/
	private	Vector					vtLevelOneNodes 		= new Vector(51);

	/** Links to arrange.*/
	private	Vector					vtLinks 				= new Vector(51);

	/** Nodes against their levels.*/
	private	Vector					vtNodesLevel 			= new Vector(51);

	/** Nodes against thier levels in order.*/
	private	Vector					vtNodesLevelOrdered 	= new Vector(51);

	/** List of node levels.*/
	private Vector					nodeLevelList 			= new Vector();

	/** The view service for accessing the databse.*/
	private IViewService 			vs 						= null;

	/** The session object for the current user with the current database.*/
	private PCSession 				session 				= null;

	/** The maximum indent level.*/
	private int						nMaxLevel 				= 0;

	/** Node ID against X position set or not.*/
	private Hashtable				htNodeXPositionSet 		= new Hashtable();

	/**  The number of position above the pointer position */
	private int 				abovePointerPos 			= 0;

	/** The number of position below the pointer position */
	private int 				belowPointerPos 			= 1;

	/** The number of children below the pointer position */
	private int 				childSizeBelow 				= 0;

	/** The number of children above the pointer position */
	private int 				childSizeAbove 				= 0;
	
	/** max number a node can recurse */ 
	private int 				recursionCount  			= 3; 

	/**
	 * Constructor. Does nothing.
	 */
	public UIArrangeTopDown() {}

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
			int[] verticalSep = new int[highestLevel];
			int maxLevel = 0;

			//get the maxiumum width of a uinode based on its bounds
			for(Enumeration e = htNodesLevel.keys();e.hasMoreElements();) {

				String nodeId = (String)e.nextElement();

				//get the uinode and get its bounds/ccordinates
				UINode uinode = ((UINode) ((UIMapViewFrame)viewFrame).getViewPane().get(nodeId));
				if (uinode == null) {
					continue;
				}

				int level = ((Integer)htNodesLevel.get(nodeId)).intValue();

				if (level > maxLevel) {
					maxLevel = level;
				}

				// ALLOW FOR FACT VIEW MIGHT BE SCALED
				int height = uinode.getHeight();

				if (uiViewPane != null) {
					Point p = UIUtilities.scalePoint(height, height, uiViewPane.getScale());
					height = p.y;
				}

				if (level > 0) {
					int sepLevel = verticalSep[level-1];
					if(height > sepLevel)
						verticalSep[level-1] = height + FormatProperties.arrangeTopVerticalGap;
				}
			}

			// First sort nodes parent wise and then set Y position for the node.. This might some what minimize cross over links
			sortNodesParentwise();
			setXPositionForNodes(viewFrame);

			if (nodeLevelList.size() > 0) {
				compactNodesInList(viewFrame, (Vector)nodeLevelList.elementAt(0));
			}

	//		This is to put all parents at the center of their children
			if(nodeLevelList.size() > 1){
				for(int i =0; i < ((Vector)nodeLevelList.get(0)).size(); i++){
					finalizeXPos(viewFrame,(String)((Vector)nodeLevelList.get(0)).get(i));
				}
			}
	//end edit  - Lakshmi */

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
					int newY = 0;
					if (view.equals(ProjectCompendium.APP.getHomeView())) {
						newY = 40;
					}
					int j = 0;
					for (j = 0; j < level; j++) {
						newY += verticalSep[j];
					}

					// ALLOW FOR FACT VIEW MIGHT BE SCALED
					int height = uinode.getHeight();

					if (uiViewPane != null) {
						Point p = UIUtilities.scalePoint(height, height, uiViewPane.getScale());
						height = p.x;
					}

					newY -= ((verticalSep[j-1]/2) + (height/2));

					int newX = uinode.getNodePosition().getXPos();
					//uinode.getNodePosition().setPos(newX, newY);
					Point ptNew = new Point(newX, newY);
					//uinode.setPosition(ptNew);

					if (uiViewPane != null) {
						Point loc = UIUtilities.transformPoint(newX, newY, uiViewPane.getScale());
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
						System.out.println("Error: (UIArrangeTopDown.arrangeView) \n\n"+ex.getMessage()); //$NON-NLS-1$
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
				ProjectCompendium.APP.displayError(LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "UIArrangeTopDown.noNodes") + view.getLabel()+". " + ex.getMessage()); //$NON-NLS-1$ //$NON-NLS-2$
			}

			nodePositionsCloneHashtableForRedo.clear();

			for ( int i = 0; i < vtTemp.size(); i++) {
				nodePositionsCloneHashtableForRedo.put(
						((NodePosition)vtTemp.elementAt(i)).getNode().getId(),
						((NodePosition)vtTemp.elementAt(i)).getClone());
			}
		} else {
			ProjectCompendium.APP.displayError(LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "UIArrangeTopDown.errorTopDown")); //$NON-NLS-1$
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
			ProjectCompendium.APP.displayError("Exception: (UIArrangeTopDown.calculateLevelOneNodes) " + ex.getMessage()); //$NON-NLS-1$
		}

		if(htNodes.size() == 0)
			return -1;

		//	get each node and check if it is level 1 node
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
			int xPositionForLevel1 = ( (NodePosition)nodePositionsCloneHashtable.get(levelOneNode.getId())).getXPos();
			int secondXPositionForLevel1 = 0;
			int indexForLevel1 = 0;
			while (nodeLevelList.size() == 0) {
				nodeLevelList.addElement(new Vector());
			}
			Vector nodeListAtLevel1 = (Vector)nodeLevelList.elementAt(0);
			secondXPositionForLevel1 = 0;
			// SOME SORT OF NODE ORDERING BY LEVEL AND Y POSITION ?
			//-- begin sort(upto the current node in for loop) for level one based on y-position
			//-- find a node's y position > current node's y position in level 1
			indexForLevel1 = 0;
			while ((indexForLevel1 < nodeListAtLevel1.size()) && (xPositionForLevel1 > secondXPositionForLevel1)) {
				secondXPositionForLevel1 = ( (NodePosition)nodePositionsCloneHashtable.get(nodeListAtLevel1.elementAt(indexForLevel1))).getXPos();
				indexForLevel1++;
			}

			//-- insert at the correct position - sorted by y position
			if (indexForLevel1 == nodeListAtLevel1.size()) {

				if (xPositionForLevel1 > secondXPositionForLevel1)
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
					int xPosition = ( (NodePosition)nodePositionsCloneHashtable.get(node.getId())).getXPos();
					int secondXPosition = 0;

					int index = 0;
					boolean found = false;
					while ((index < oldVector.size()) && (xPosition > secondXPosition)) {
						secondXPosition = ( (NodePosition)nodePositionsCloneHashtable.get(oldVector.elementAt(index))).getXPos();
						if (node.getId().equals((String)oldVector.elementAt(index))) {
							found = true;
							break;
						}
						index++;
					}

					if (index == oldVector.size()) {
						if (xPosition > secondXPosition) {
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
							int previousParentXPosition = np.getXPos();
							int currentParentXPosition =  ( (NodePosition)nodePositionsCloneHashtable.get(levelOneNode.getId())).getXPos();
							if(nodeAboveId != levelOneNode.getId()) {
								if (currentParentXPosition < previousParentXPosition) {
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

							if (!startRecursiveCalculations(node,(nLevel+1)))
								return false;

							continue;
						}
					}

					while (nodeLevelList.size() < (nLevel + 1)) {
						nodeLevelList.addElement(new Vector());
					}

					Vector nodeListAtLevel = (Vector)nodeLevelList.elementAt(nLevel);
					secondXPosition = 0;
					index = 0;

					while ((index < nodeListAtLevel.size()) && (xPosition > secondXPosition)) {
						secondXPosition = ( (NodePosition)nodePositionsCloneHashtable.get(nodeListAtLevel.elementAt(index))).getXPos();
						index++;
					}
					if (index == nodeListAtLevel.size()) {
						if (xPosition > secondXPosition) {
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
	}//end startLevelCalculation
	
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
			if(count.intValue() > recursionCount)
				return true;
			else {
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

				int xPosition = ( (NodePosition)nodePositionsCloneHashtable.get(sFromNodeID)).getXPos();
				int secondXPosition = 0;

				int index = 0;
				boolean found = false;
				while ((index < oldVector.size()) && (xPosition > secondXPosition)) {

					//NodeSummary tempNode = (NodeSummary)htNodes.get(oldVector.elementAt(index));
					secondXPosition = ( (NodePosition)nodePositionsCloneHashtable.get(oldVector.elementAt(index))).getXPos();
					if (nodeFrom.getId().equals((String)oldVector.elementAt(index))) {
						found = true;
						break;
					}
					index++;

				}
				if (index == oldVector.size()) {
					if (xPosition > secondXPosition) {
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

//				Get all parents for a node. Keeping track of all the parents, helps to sort child nodes and position them around
//their parents if possible.
					vtParentNodes = (Vector) htNodesAbove.get(sFromNodeID);
					int currentParentXPosition =  ((NodePosition)nodePositionsCloneHashtable.get(sToNodeID)).getXPos();

					boolean isInserted = false;

					for(int j=0; j<vtParentNodes.size();j++){
						String nodeAboveId = (String)vtParentNodes.get(j);
						NodePosition np = ( (NodePosition)nodePositionsCloneHashtable.get(nodeAboveId));
						int previousParentXPosition = np.getXPos();

						if(nodeAboveId != sToNodeID) {
							if (currentParentXPosition < previousParentXPosition) {
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
				secondXPosition = 0;

				index = 0;
				while ((index < nodeListAtLevel.size()) && (xPosition > secondXPosition)) {
					secondXPosition = ( (NodePosition)nodePositionsCloneHashtable.get(nodeListAtLevel.elementAt(index))).getXPos();
					index++;
				}
				if (index == nodeListAtLevel.size() ) {
					if (xPosition > secondXPosition) {
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
			}//end else
		}//end for
		return true;
	}//end startRecursiveCalculations

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
												}
											}
										}
									} else if ((vtParent == null)&& (indexOfParent > (indexFP + 1))) {
										swapNodes(sNodeId, sFirstParent, sParent, i, nIndex);
									}
								}
							}
						}
						break;
					}
				}
			}
		}
		sortNodesBelow();
		sortNodesAbove();
	}

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
			}
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
				}
			}
			
			if(isSingleParentNodes){
				((Vector)nodeLevelList.get(i-1)).remove(sParent);
				if((nIndex == 0) && (!isSet)){

					((Vector)nodeLevelList.get(i-1)).insertElementAt(sParent, indexFP - abovePointerPos);
					abovePointerPos ++;
					
					for(int k = 0; k < vtNodesBelow.size(); k++) {
						if(! sNodeId.equals(vtNodesBelow.get(k))) {
							((Vector)nodeLevelList.get(i)).remove(vtNodesBelow.get(k));
							((Vector)nodeLevelList.get(i)).insertElementAt(vtNodesBelow.get(k), index - childSizeAbove);
						}
					}
					
					childSizeAbove += vtNodesBelow.size() - 1;
					if(nIndex == (vtNodesBelowFP.size() - 1)) {
						isSet = true;
					}
				 } else if((nIndex == (vtNodesBelowFP.size() - 1)) || (isSet == true)){
					((Vector)nodeLevelList.get(i-1)).insertElementAt(sParent, indexFP + belowPointerPos);
					belowPointerPos ++;
					for(int k = vtNodesBelow.size()-1; k >= 0; k--) {
						if(! sNodeId.equals(vtNodesBelow.get(k))) {
							((Vector)nodeLevelList.get(i)).remove(vtNodesBelow.get(k));
							((Vector)nodeLevelList.get(i)).insertElementAt(vtNodesBelow.get(k), (index + 1 + childSizeBelow));
						}
					}
					childSizeBelow += vtNodesBelow.size() - 1 ;
				 }
			}
		}
	}

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
							}
						}
					}
					htNodesAbove.put((String) vtLevelNodes.get(j),vtNodesAbove);
				}
			}
		}
	}

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
							}
						}
					}
					htNodesBelow.put((String) vtLevelNodes.get(j),vtNodesBelow);
				}
			}
		}
	}
	
//	end edit - Lakshmi

	/**
	 * This method is added to enable the child nodes to be positioned at the center relative to the parent.
	 * @param viewFrame com.compendium.ui.UIViewFrame, the frame of the nodes to set the y positions for.
	 */
	private void setXPositionForNodes(UIViewFrame viewFrame) {
		int xPosition = 70;

		// TO FIX an ArrayIndexOutOfBoundsException
		if ( nodeLevelList != null && nodeLevelList.size() > 0 ) {
			if (nodeLevelList.elementAt(0) != null && ((Vector)nodeLevelList.elementAt(0)).size() > 0) {

				for (int i = 0; i < ((Vector)nodeLevelList.elementAt(0)).size(); i++) {
					xPosition = calculateXPosition(viewFrame, (String)((Vector)nodeLevelList.elementAt(0)).elementAt(i), xPosition);
				}
			}
		}
	}

	private Hashtable nodesXed = new Hashtable(51);
	
	/**
	 * Helper Method. This method is added to enable the child nodes to be positioned at the center relative to the parent.
	 * @param viewFrame com.compendium.ui.UIViewFrame, the frame of the node to calculate the y positions for.
	 * @param nodeId, the id of the node to calculate the y position for.
	 * @param yPosition, the starting y position.
	 * @return int, the calculated y position.
	 */
	private int calculateXPosition(UIViewFrame viewFrame, String nodeId, int xPosition) {

		Integer count = new Integer(1);
		if(nodesXed.containsKey(nodeId)){
			count = (Integer) nodesXed.get(nodeId);
			if(count.intValue() > recursionCount)
				return xPosition;
			else {
				count = new Integer(count.intValue() + 1);
				nodesXed.put(nodeId, count);
			}
		} else {
			nodesXed.put(nodeId, count);
		}
		
		UIViewPane viewPane = ((UIMapViewFrame)viewFrame).getViewPane();
		Vector childNodeList = (Vector) htNodesBelow.get(nodeId);
		UINode parentNode = ((UINode)viewPane.get(nodeId));
		double scale = viewPane.getScale();
		
		if (childNodeList != null) {

			for (int i = 0; i < childNodeList.size(); i++) {
				xPosition = calculateXPosition(viewFrame, (String)childNodeList.elementAt(i), xPosition);
			}

			UINode lastChildNode = (UINode)viewPane.get((String)childNodeList.lastElement());
			int firstChildPosition = ((UINode)viewPane.get((String)childNodeList.firstElement())).getNodePosition().getXPos();
			int lastChildPosition =  lastChildNode.getNodePosition().getXPos();

			int newXPosition = firstChildPosition + (lastChildPosition + lastChildNode.getWidth() - firstChildPosition)/2;
			newXPosition = newXPosition - parentNode.getWidth()/2;

			int nodeLevel = ((Integer)htNodesLevel.get(nodeId)).intValue();
			int indexOfNode = ((Vector)nodeLevelList.elementAt(nodeLevel - 1)).indexOf(nodeId);
			int indexOfPreviousNode = indexOfNode - 1;
			int previousNodeXPosition = 0;

			UINode node = ((UINode)viewPane.get(nodeId));

			if (indexOfPreviousNode > -1) {
				UINode previousNode = ((UINode)viewPane.get((String)((Vector)nodeLevelList.elementAt(nodeLevel-1)).elementAt(indexOfPreviousNode)));
				previousNodeXPosition = previousNode.getNodePosition().getXPos();

				// ALLOW FOR FACT VIEW MIGHT BE SCALED
				int nodeWidth = previousNode.getWidth();
				Point p1 = UIUtilities.scalePoint(nodeWidth, nodeWidth, scale);
				nodeWidth = p1.y;

				if ((previousNodeXPosition + nodeWidth + FormatProperties.arrangeTopHorizontalGap) > newXPosition) {
					newXPosition = previousNodeXPosition + nodeWidth + (FormatProperties.arrangeTopHorizontalGap);
				}
			}

			Point pos = node.getNodePosition().getPos();
			node.getNodePosition().setPos(new Point( newXPosition, pos.y));
			if (newXPosition > xPosition) {
				xPosition = newXPosition;
			}
		} else {
// 			if a child node has 2 parent , on arrange the node was dropping down. To avoid this, once the node's Y position is set
// 			it is not set again.
			if((htNodeXPositionSet.get(nodeId) == null) || (((Boolean)htNodeXPositionSet.get(nodeId)).booleanValue() == false)) {
				UINode node = ((UINode)viewPane.get(nodeId));

				int nodeLevel = ((Integer)htNodesLevel.get(nodeId)).intValue();
				int indexOfNode = ((Vector)nodeLevelList.elementAt(nodeLevel - 1)).indexOf(nodeId);
				int indexOfPreviousNode = indexOfNode - 1;
				int previousNodeXPosition = 0;
				Point pos = node.getNodePosition().getPos();
				if(indexOfPreviousNode > -1) {

					String  sPreviousNodeID = (String)((Vector)nodeLevelList.elementAt(nodeLevel-1)).elementAt(indexOfPreviousNode);
					UINode previousNode = ((UINode)viewPane.get(sPreviousNodeID));
					//BUG FIX - Lakshmi 9/15/06 for NullPointerException
					 
					NodeSummary previousNodeSum = (NodeSummary)htNodes.get(sPreviousNodeID);
					previousNodeXPosition = previousNode.getNodePosition().getXPos();

					// ALLOW FOR FACT VIEW MIGHT BE SCALED
					int nodeWidth = previousNode.getWidth();
					Point p1 = UIUtilities.scalePoint(nodeWidth, nodeWidth, scale);
					nodeWidth = p1.y;

					// This is to put the parent at the center of its children
					Vector vtParentNodes = (Vector) htNodesAbove.get(node.getNode().getId());
					if(vtParentNodes != null) {
						String sFirstParentNodeId = (String) vtParentNodes.firstElement();
						int nLevel = ((Integer)htNodesLevel.get(sFirstParentNodeId)).intValue();
						int indexOfParent = ((Vector) nodeLevelList.get( nLevel - 1)).indexOf(sFirstParentNodeId);
						if ((indexOfParent -1 ) >= 0 ) {
							String sParentPreviousNodeId = (String)((Vector)nodeLevelList.get(nLevel - 1)).get(indexOfParent - 1);
							UINode parentPreviousNode = ((UINode)viewPane.get(sParentPreviousNodeId));
							int parentPreviousNodePos = parentPreviousNode.getNodePosition().getXPos();
							int parentPreviousNodeWth = parentPreviousNode.getWidth();

							String sPreviousNodeParentId =  previousNodeSum.getParentNode().getId();

							if (!((Vector)htNodesAbove.get(nodeId)).contains(sPreviousNodeParentId)){
								xPosition = parentPreviousNodePos + parentPreviousNodeWth + FormatProperties.arrangeTopHorizontalGap ;
							}
						}
					}

					if ((previousNodeXPosition + nodeWidth) > xPosition) {
						xPosition = previousNodeXPosition + nodeWidth + (FormatProperties.arrangeTopHorizontalGap );
					}
					node.getNodePosition().setPos(new Point( xPosition, pos.y));
				} else {
					String sNodeId = node.getNode().getId();
					int xPos = findPreviousNodeXPos(viewPane, sNodeId);
					if(xPosition > xPos) {
						node.getNodePosition().setPos(new Point(xPosition , pos.y));
					} else {
						node.getNodePosition().setPos(new Point(xPos, pos.y));
					}
					// ALLOW FOR FACT VIEW MIGHT BE SCALED
					int nodeWidth = node.getWidth();
					Point p1 = UIUtilities.scalePoint(nodeWidth, nodeWidth, scale);
					nodeWidth = p1.y;

					xPosition += (FormatProperties.arrangeTopHorizontalGap + nodeWidth);

				}
		    }
		}
		htNodeXPositionSet.put(nodeId, new Boolean(true));
		return xPosition;
	}
	
	private Hashtable nodesXFinalized = new Hashtable();
	
	/**
	 * This method is to enable the child nodes to be positioned at the center relative to the parent after compact.
	 * @param viewFrame com.compendium.ui.UIViewFrame, the frame of the node to finalize the y positions for.
	 * @param sNodeID, the id of the node to finalize the y position for.
	 */
	private void finalizeXPos(UIViewFrame viewFrame, String sNodeID){

		Integer count = new Integer(1);
		if(nodesXFinalized.containsKey(sNodeID)){
			count = (Integer) nodesXFinalized.get(sNodeID);
			if(count.intValue() > recursionCount)
				return;
			else {
				count = new Integer(count.intValue() + 1);
				nodesXFinalized.put(sNodeID, count);
			}
		} else {
			nodesXFinalized.put(sNodeID, count);
		}
		UIViewPane viewPane = ((UIMapViewFrame)viewFrame).getViewPane();
		Vector vtChildNodes = (Vector)htNodesBelow.get(sNodeID);
		UINode oNode = ((UINode)viewPane.get(sNodeID));
		Point pos = oNode.getNodePosition().getPos();
		double scale = viewPane.getScale();
		
		if(vtChildNodes != null){
			for (int i =0; i< vtChildNodes.size(); i++){
				finalizeXPos(viewFrame, (String)vtChildNodes.get(i));
			}
			Vector vtChildren = (Vector)htNodesBelow.get(sNodeID);

			int nodePosition = oNode.getNodePosition().getXPos();

			UINode oFirstChild = (UINode)viewPane.get((String)vtChildren.firstElement());
			UINode oLastChild = (UINode)viewPane.get((String)vtChildren.lastElement());

			String sFirstParentOfFC = (String)((Vector) htNodesAbove.get(vtChildren.firstElement())).firstElement();
			String sFirstParentOfLC = (String)((Vector) htNodesAbove.get(vtChildren.lastElement())).firstElement();

			if (sFirstParentOfFC.equals(sNodeID) && sFirstParentOfLC.equals(sNodeID)){
				int firstChildPosition = oFirstChild.getNodePosition().getXPos();
				int lastChildPosition =  oLastChild.getNodePosition().getXPos();

				int centerPosition = firstChildPosition + (lastChildPosition + oLastChild.getWidth() - firstChildPosition)/2;
				centerPosition = centerPosition - oNode.getWidth()/2;

				int nodeLevel = ((Integer)htNodesLevel.get(sNodeID)).intValue();
				int indexOfNode = ((Vector)nodeLevelList.elementAt(nodeLevel - 1)).indexOf(sNodeID);
				int indexOfPreviousNode = indexOfNode - 1;
				int previousNodeXPosition = 0;
				if (indexOfPreviousNode > -1) {
					UINode previousNode = ((UINode)viewPane.get((String)((Vector)nodeLevelList.elementAt(nodeLevel-1)).elementAt(indexOfPreviousNode)));
					previousNodeXPosition = previousNode.getNodePosition().getXPos();

					int nodeWidth = previousNode.getWidth();
					Point p2 = UIUtilities.scalePoint(nodeWidth, nodeWidth, scale);
					nodeWidth = p2.y;

					if ((previousNodeXPosition + nodeWidth + FormatProperties.arrangeTopHorizontalGap ) > nodePosition) {
						nodePosition = previousNodeXPosition + nodeWidth + FormatProperties.arrangeTopHorizontalGap ;
						oNode.getNodePosition().setPos(new Point (nodePosition , pos.y));
					}
				}
				if(nodePosition <= centerPosition){
					oNode.getNodePosition().setPos(new Point (centerPosition , pos.y));
				} else {
					int amount = nodePosition - centerPosition ;
					getChildNodesDown(viewPane, sNodeID, amount);
				}
			}
		}
		int nodePosition = oNode.getNodePosition().getXPos();

		int nodeLevel = ((Integer)htNodesLevel.get(sNodeID)).intValue();
		int indexOfNode = ((Vector)nodeLevelList.elementAt(nodeLevel - 1)).indexOf(sNodeID);
		int indexOfPreviousNode = indexOfNode - 1;
		int previousNodeXPosition = 0;

		if (indexOfPreviousNode > -1) {
			UINode previousNode = ((UINode)viewPane.get((String)((Vector)nodeLevelList.elementAt(nodeLevel-1)).elementAt(indexOfPreviousNode)));
			previousNodeXPosition = previousNode.getNodePosition().getXPos();

			int nodeWidth = previousNode.getWidth();
			Point p2 = UIUtilities.scalePoint(nodeWidth, nodeWidth, scale);
			nodeWidth = p2.y;
			if ((previousNodeXPosition + nodeWidth + FormatProperties.arrangeTopHorizontalGap) > nodePosition) {
				nodePosition = previousNodeXPosition + nodeWidth + FormatProperties.arrangeTopHorizontalGap ;
				oNode.getNodePosition().setPos(new Point (nodePosition , pos.y));
			}
		}
	}

	/**
	 * Helper Method. This method helps to put parent at the center of the children.
	 * @param viewPane com.compendium.ui.UIViewPane, the frame of the node to finalize the y positions for.
	 * @param sNodeID, the id of the node whose y position has to be increased.
	 * @param amount, the amount to increase in Y position
	 */

	private void getChildNodesDown (UIViewPane viewPane, String sNodeID, int amount){

		UINode oNode = ((UINode)viewPane.get(sNodeID));
		Point pos = oNode.getNodePosition().getPos();
		Vector vtChildNodes = (Vector) htNodesBelow.get(sNodeID);
		if (vtChildNodes != null){
			
			for(int i = 0; i < vtChildNodes.size(); i++){
				getChildNodesDown(viewPane, (String)vtChildNodes.get(i), amount);
			}
			
			Vector vtChildren = (Vector)htNodesBelow.get(sNodeID);

			int nodePosition = oNode.getNodePosition().getXPos();
			UINode lastNode = (UINode)viewPane.get((String)vtChildren.lastElement());
			int firstChildPosition = ((UINode)viewPane.get((String)vtChildren.firstElement())).getNodePosition().getXPos();
			int lastChildPosition =  lastNode.getNodePosition().getXPos();

			int centerPosition = firstChildPosition + (lastChildPosition + lastNode.getWidth() - firstChildPosition)/2;
			centerPosition = centerPosition - oNode.getWidth()/2;

			int nodeLevel = ((Integer)htNodesLevel.get(sNodeID)).intValue();
			int indexOfNode = ((Vector)nodeLevelList.elementAt(nodeLevel - 1)).indexOf(sNodeID);
			int indexOfPreviousNode = indexOfNode - 1;
			int previousNodeXPosition = 0;

			if (indexOfPreviousNode > -1) {
				UINode previousNode = ((UINode)viewPane.get((String)((Vector)nodeLevelList.elementAt(nodeLevel-1)).elementAt(indexOfPreviousNode)));
				previousNodeXPosition = previousNode.getNodePosition().getXPos();

				int nodeWidth = previousNode.getWidth();
				Point p2 = UIUtilities.scalePoint(nodeWidth, nodeWidth, viewPane.getScale());
				nodeWidth = p2.y;

				if ((previousNodeXPosition + nodeWidth + FormatProperties.arrangeTopHorizontalGap) > nodePosition) {
					nodePosition = previousNodeXPosition + nodeWidth + FormatProperties.arrangeTopHorizontalGap ;
					oNode.getNodePosition().setPos(new Point (nodePosition , pos.y));
				}
			}
			if(nodePosition <= centerPosition){
				oNode.getNodePosition().setPos(new Point (centerPosition , pos.y));

			}
		} else {
			int xPos = oNode.getNodePosition().getXPos();
			xPos += amount;
			oNode.getNodePosition().setPos(new Point(xPos , pos.y));
		}
	}

	/**
	 *  Helper Method. To find y position for a node so that it is placed around its parent.
	 * @param viewPane com.compendium.ui.UIViewPane, the frame of the node.
	 * @param sNodeID, the id of the node whose previous node y position has to be found.
	 *
	 * @return int, The y position of the previous node.
	 */
	private int findPreviousNodeXPos (UIViewPane viewPane, String nodeID){
		int xPos = 0;
		Vector vtParents = (Vector)htNodesAbove.get(nodeID);
		if(vtParents != null){
			String sParentId = (String) vtParents.firstElement();
			int index = ((Vector)nodeLevelList.get(((Integer)htNodesLevel.get(sParentId)).intValue() -1)).indexOf(sParentId);
			if (index > 0){
				String sPreviousNodeID = (String)((Vector)nodeLevelList.get(((Integer)htNodesLevel.get(sParentId)).intValue() -1)).get(index - 1);
				UINode previousNode = (UINode) viewPane.get(sPreviousNodeID);
				NodePosition nodePosition = previousNode.getNodePosition();
				xPos = nodePosition.getXPos() + previousNode.getWidth() + FormatProperties.arrangeTopHorizontalGap ;
			} else {
				xPos = findPreviousNodeXPos(viewPane, sParentId);
			}
		}
		return xPos;
	}

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
			Vector childNodeList = (Vector)htNodesBelow.get(nodeID);

			if (childNodeList != null) {
				compactNodesInList(viewFrame, childNodeList);
			}
		}
		int currentNodeXPosition = 0;
		int previousNodeXPosition = 0;
		int compactAmount = 0;

		double scale = viewPane.getScale();
		
//		This is to center the 1st node (if its a parent)at any level .
		if ((nodeList  != null) &&(nodeList.size() >= 1)) {
			UINode node = ((UINode)viewPane.get((String)nodeList.elementAt(0)));
			Vector childNodeList = (Vector) htNodesBelow.get(nodeList.get(0));
			if(childNodeList != null){

				UINode firstNode = (UINode)viewPane.get((String)childNodeList.firstElement());
				int firstNodeXPosition = firstNode.getNodePosition().getXPos();

				UINode lastNode = (UINode)viewPane.get((String)childNodeList.lastElement());
				int lastNodeXPosition = lastNode.getNodePosition().getXPos();

				Point pos = node.getNodePosition().getPos();

				int centerPosition = firstNodeXPosition + (lastNodeXPosition + lastNode.getWidth() - firstNodeXPosition)/2;
				centerPosition = centerPosition - node.getWidth()/2;

				int level = ((Integer)htNodesLevel.get(node.getNode().getId())).intValue() - 1;
				int index = ((Vector)nodeLevelList.get(level)).indexOf(node.getNode().getId());
				if(index < 1){
					node.getNodePosition().setPos(new Point(centerPosition , pos.y));
				} else {
					UINode previousNode = (UINode)viewPane.get((String)((Vector)nodeLevelList.get(level)).get(index - 1));
					int previousNodeXPos = previousNode.getNodePosition().getXPos();
					int previousNodeWidth = previousNode.getWidth();
					if(centerPosition > previousNodeXPos + previousNodeWidth + FormatProperties.arrangeTopHorizontalGap ){
						node.getNodePosition().setPos(new Point(centerPosition , pos.y));
					} else {
						node.getNodePosition().setPos(new Point( previousNodeXPos + previousNodeWidth + FormatProperties.arrangeTopHorizontalGap, pos.y ));
					}
				}
			}
		}
		
		// this will arrange other nodes based on the above arrangement of 1st node.
		for (int i = 1; i < nodeList.size(); i++) {
			UINode currentNode = ((UINode)viewPane.get((String)nodeList.elementAt(i)));
			UINode previousNode = ((UINode)viewPane.get((String)nodeList.elementAt(i-1)));
			currentNodeXPosition = currentNode.getNodePosition().getXPos();
			previousNodeXPosition = previousNode.getNodePosition().getXPos();

			UINode node = ((UINode)viewPane.get((String)nodeList.elementAt(i)));
			// ALLOW FOR FACT VIEW MIGHT BE SCALED
			int nodeWidth = previousNode.getWidth();
			Point p1 = UIUtilities.scalePoint(nodeWidth, nodeWidth, scale);
			nodeWidth = p1.y;

			if ((currentNodeXPosition - previousNodeXPosition - nodeWidth) > FormatProperties.arrangeTopHorizontalGap) {
				compactDoneForNodes.clear();

				compactAmount = checkCompact(viewFrame,
											 (String)nodeList.elementAt(i),
											 currentNodeXPosition - previousNodeXPosition - FormatProperties.arrangeTopHorizontalGap  - nodeWidth,
											 compactDoneForNodes, vtLevelChecked);
				if (compactAmount > 0) {
					compact(viewFrame, (String)nodeList.elementAt(i), compactAmount, compactDoneForNodes);
				}
			}

			Vector childNodeList = (Vector)htNodesBelow.get((String)nodeList.elementAt(i));
			if (childNodeList != null) {
				UINode firstNode = (UINode)viewPane.get((String)childNodeList.firstElement());
				int firstNodeXPosition = firstNode.getNodePosition().getXPos();
				UINode lastNode = (UINode)viewPane.get((String)childNodeList.lastElement());
				int lastNodeXPosition = lastNode.getNodePosition().getXPos();
				previousNodeXPosition = 0;
				int previousNodeWidth = 0;
				int level = ((Integer)htNodesLevel.get((String)nodeList.elementAt(i))).intValue();
				int indexOfCurrentNode = ((Vector)nodeLevelList.elementAt(level-1)).indexOf((String)nodeList.elementAt(i));
				int indexOfPreviousNode = indexOfCurrentNode - 1;
				Point pos = node.getNodePosition().getPos();

				if (indexOfPreviousNode > -1) {
					previousNodeXPosition = previousNode.getNodePosition().getXPos();

					// ALLOW FOR FACT VIEW MIGHT BE SCALED
					previousNodeWidth = previousNode.getWidth();
					Point p2 = UIUtilities.scalePoint(previousNodeWidth, previousNodeWidth, scale);
					previousNodeWidth = p2.y;

					int centerPosition = firstNodeXPosition + (lastNodeXPosition + lastNode.getWidth() - firstNodeXPosition)/2;
					centerPosition = centerPosition - node.getWidth()/2;

					if ((previousNodeXPosition + previousNodeWidth + FormatProperties.arrangeTopHorizontalGap ) < centerPosition) {
						node.getNodePosition().setPos(new Point( centerPosition , pos.y));
					}
					else {
						node.getNodePosition().setPos(new Point(previousNodeXPosition + previousNodeWidth + FormatProperties.arrangeTopHorizontalGap , pos.y));
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
	private int checkCompact(UIViewFrame viewFrame, String nodeId, int compactAmount, Hashtable compactDoneForNodes, Vector vtLevelChecked) {

		UIViewPane viewPane = ((UIMapViewFrame)viewFrame).getViewPane();
		double scale = viewPane.getScale();
		
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
		int	currentNodeXPosition = currentNode.getNodePosition().getXPos();
		int level = ((Integer)htNodesLevel.get(nodeId)).intValue();
		int indexOfCurrentNode = ((Vector)nodeLevelList.elementAt(level-1)).indexOf(nodeId);
		int indexOfPreviousNode = indexOfCurrentNode - 1;

		if (indexOfPreviousNode < 0) {
			if (currentNodeXPosition < compactAmount) {
				compactAmount = currentNodeXPosition - 70;
			}
			indexOfPreviousNode = 0;

		}
		else {
			UINode previousNode = ((UINode)viewPane.get((String)((Vector)nodeLevelList.elementAt(level-1)).elementAt(indexOfPreviousNode)));
			int previousNodeXPosition = previousNode.getNodePosition().getXPos();

			// ALLOW FOR FACT VIEW MIGHT BE SCALED
			int nodeWidth = currentNode.getWidth();
			Point p = UIUtilities.scalePoint(nodeWidth, nodeWidth, scale);
			nodeWidth = p.y;

			// ALLOW FOR FACT VIEW MIGHT BE SCALED
			int previousNodeWidth = previousNode.getWidth();
			Point p2 = UIUtilities.scalePoint(previousNodeWidth, previousNodeWidth, scale);
			previousNodeWidth = p2.y;
			if (!vtLevelChecked.contains(htNodesLevel.get(nodeId))) {
				if ( (currentNodeXPosition - previousNodeXPosition - FormatProperties.arrangeTopHorizontalGap  - previousNodeWidth) <
						compactAmount) {
					compactAmount = currentNodeXPosition - previousNodeXPosition - FormatProperties.arrangeTopHorizontalGap  - previousNodeWidth;
				}
			}
		}
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
		int xPosition = 0;

		if (indexOfPreviousNode > -1) {
			UINode previousNode = ((UINode)viewPane.get((String)((Vector)nodeLevelList.elementAt(level-1)).elementAt(indexOfPreviousNode)));
			int previousNodeXPosition = previousNode.getNodePosition().getXPos();

			// ALLOW FOR FACT VIEW MIGHT BE SCALED
			int previousNodeWidth = previousNode.getWidth();
			Point p = UIUtilities.scalePoint(previousNodeWidth, previousNodeWidth, viewPane.getScale());
			previousNodeWidth = p.y;

			if ((pos.x - compactAmount) < (previousNodeXPosition + previousNodeWidth + FormatProperties.arrangeTopHorizontalGap )) {
				xPosition = previousNodeXPosition + previousNodeWidth + FormatProperties.arrangeTopHorizontalGap ;
			}
			else {
				xPosition = pos.x - compactAmount;
			}
		}
		else {
			xPosition = pos.x - compactAmount;
		}

		if (xPosition < 0)
			xPosition = 0;
		node.getNodePosition().setPos(new Point(xPosition, pos.y));

		compactDoneForNodes.put(nodeId, new Boolean(true));
		Vector nodeList = (Vector)htNodesBelow.get(nodeId);

		if (nodeList != null) {
			for (int i = 0; i < nodeList.size(); i++) {
				String sParentID = (String)((Vector) htNodesAbove.get(nodeList.elementAt(i))).firstElement();
				if (nodeId.equals(sParentID)) {
					compact(viewFrame, (String) nodeList.elementAt(i), compactAmount, compactDoneForNodes);
				}
			}
		}
	}
	
//	******** UNDO / REDO **************//

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
				System.out.println("Error: (UIArrangeTopDown.undoArrange) \n\n"+ex.getMessage()); //$NON-NLS-1$
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
				System.out.println("Error: (UIArrangeTopDown.redoArrange) \n\n"+ex.getMessage()); //$NON-NLS-1$
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