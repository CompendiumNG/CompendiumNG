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


package com.compendium.core.datamodel;

import java.awt.Point;
import java.util.*;
import java.sql.SQLException;

import com.compendium.core.datamodel.services.*;
import com.compendium.core.db.*;
import com.compendium.core.ICoreConstants;

/**
 * The View object is a node that represents a collection of nodes and links.
 * The visual representation of the nodes and links depends on the type of the view.
 *
 * @author	Rema Natarajan / Michelle Bachler 
 */
public class View extends NodeSummary implements IView, java.io.Serializable {

	/** children property name for use with property change events */
	public final static String CHILDREN_PROPERTY = "children";

	/** link removed property name for use with property change events */
	public final static String LINK_REMOVED = "linkremoved";

	/** link added property name for use with property change events */
	public final static String LINK_ADDED = "linkadded";

	/** node removed property name for use with property change events */
	public final static String NODE_REMOVED = "noderemoved";

	/** node added property name for use with property change events */
	public final static String NODE_ADDED = "nodeadded";

	/** node transcluded property name for use with property change events */
	public final static String NODE_TRANSCLUDED = "nodetranscluded";

	/** The list of property change listeners registered with this object.*/
	protected Vector	vListeners			= null;

	/** A List of all the <code>NodeSummary</code> objects in this view.*/
	protected Hashtable htMemberNodes 		= new Hashtable(51);

	/** A List of all the <code>Link</code> objects in this view.*/
	protected Hashtable htMemberLinks 		= new Hashtable(51);

	/** An indication as to whether this view has loaded its node and link information from the database.*/
	protected boolean 	bMembersInitialized 	= false;

	/** A list of all links deleted from this view in this session.*/
	private Vector deletedLinks = new Vector();

	/** A list of all nodes deleted from this view in this session.*/
	private Vector deletedNodes = new Vector();

	/** A count of thie nodes in this view.*/
	private int	preInitializedNodeCount = -1;

	/** Holds the view layer data such as background, grid, shapes and scribbles.*/
	private ViewLayer		oViewLayer = null;

	/**
	 *	Constructor, takes in only the id value.
	 *
	 *	@param sNodeID String, the id of the view object.
	 */
	public View(String sNodeID) {
		super(sNodeID);
	}

	/**
	 *	Constructor, creates a View object.
	 *
	 *	@param String sViewID, the id of the view node.
	 *	@param nType int, the type of this node.
	 *	@param sXNodeType String, the extended node type id of the node - NOT CURRENTLY USED.
	 *	@param sOriginalID String, the original id of the node if it was imported.
	 *	@param sAuthor String, the author of this node.
	 *	@param dCreationDate Date, the creation date of this node.
	 *	@param dModificationDate Date, the date the node was last modified.
	 */
	protected View(String sViewID, int nType, String sXNodeType, String sOriginalID,
					int nState, String sAuthor, Date dCreationDate, Date dModificationDate, 
					String sLabel, String sDetail)
	{
		super( sViewID,  nType,  sXNodeType,  sOriginalID, nState, sAuthor,  dCreationDate,  dModificationDate,  sLabel, sDetail);
	}

	/**
	 *	Constructor, creates a View object.
	 *
	 *	@param String sViewID, the id of the view node.
	 *	@param int nType, the type of this node.
	 *	@param String sXNodeType, the extended node type id of the node - NOT CURRENTLY USED.
	 *	@param String sOriginalID, the original id of the node if it was imported.
	 *	@param int nPermission, the permissions in this node - NOT CURRENTLY USED.
	 *	@param int nState, the state of this node: not read (0) read (1), modified since last read (2).
	 *	@param String sAuthor, the author of the node.
	 *	@param Date dCreationDate, the creation date of this node.
	 *	@param Date dModificationDate, the date the node was last modified.
	 *	@param String sLabel, the label of this node.
	 *	@param String sDetail, the first page of detail for this node.
	 */
	protected View(String sViewID, int nType, String sXNodeType, String sOriginalID, int nPermission,
							int nState, String sAuthor, Date dCreationDate, Date dModificationDate,
							String sLabel, String sDetail) {

		super(sViewID,  nType,  sXNodeType,  sOriginalID, nPermission, nState, sAuthor,  
				dCreationDate,  dModificationDate,  sLabel, sDetail);
	}
	
	/**
	 *	Constructor, creates a View object.
	 *
	 *	@param String sViewID, the id of the view node.
	 *	@param nType int, the type of this node.
	 *	@param sXNodeType String, the extended node type id of the node - NOT CURRENTLY USED.
	 *	@param sOriginalID String, the original id of the node if it was imported.
	 *	@param sAuthor String, the author of this node.
	 *	@param dCreationDate Date, the creation date of this node.
	 *	@param dModificationDate Date, the date the node was last modified.
	 *	@param sLastModAuthor the author who last modified this object.*
	 */
	protected View(String sViewID, int nType, String sXNodeType, String sOriginalID,
					int nState, String sAuthor, Date dCreationDate, Date dModificationDate, 
					String sLabel, String sDetail, String sLastModAuthor)
	{
		super( sViewID,  nType,  sXNodeType,  sOriginalID, nState, sAuthor,  dCreationDate,  
				dModificationDate,  sLabel, sDetail, sLastModAuthor);
	}
	
	/**
	 *	Constructor, creates a View object.
	 *
	 *	@param sViewID the id of the view node.
	 *	@param nType the type of this node.
	 *	@param sXNodeType the extended node type id of the node - NOT CURRENTLY USED.
	 *	@param sOriginalID the original id of the node if it was imported.
	 *	@param nPermission the permissions in this node - NOT CURRENTLY USED.
	 *	@param nState the state of this node: not read (0) read (1), modified since last read (2).
	 *	@param sAuthor the author of the node.
	 *	@param dCreationDate the creation date of this node.
	 *	@param dModificationDate the date the node was last modified.
	 *	@param sLabel the label of this node.
	 *	@param sDetail the first page of detail for this node.
	 *	@param sLastModAuthor the author who last modified this object.
	 */
	protected View(String sViewID, int nType, String sXNodeType, String sOriginalID, int nPermission,
							int nState, String sAuthor, Date dCreationDate, Date dModificationDate,
							String sLabel, String sDetail, String sLastModAuthor) {

		super(sViewID,  nType,  sXNodeType,  sOriginalID, nPermission, nState, sAuthor,  dCreationDate,  
				dModificationDate, sLabel, sDetail, sLastModAuthor);
	}	

	/**
	 *	Creates a new View object.
	 *
	 *	@param String sViewID, the id of the view node.
	 *	@param int nType, the type of this node.
	 *	@param String sXNodeType, the extended node type id of the node - NOT CURRENTLY USED.
	 *	@param String sOriginalID, the original id of the node if it was imported.
	 *	@param int nPermission, the permissions in this node - NOT CURRENTLY USED.
	 *	@param int nState, the state of this node: not read (0) read (1), modified since last read (2).
	 *	@param String sAuthor, the author of the node.
	 *	@param Date dCreationDate, the creation date of this node.
	 *	@param Date dModificationDate, the date the node was last modified.
	 *	@param String sLabel, the label of this node.
	 *	@param String sDetail, the first page of detail for this node.
	 */
	public View createView(String sViewID, int nType, String sXNodeType, String sOriginalID, int nPermission,
							int nState, String sAuthor, Date dCreationDate, Date dModificationDate,
							String sLabel, String sDetail)
	{
		return new View( sViewID,  nType,  sXNodeType,  sOriginalID, nPermission, nState, sAuthor,  dCreationDate,  dModificationDate,  sLabel, sDetail);
	}

	/**
	 * Returns whether or not the passed node type is a view node type.
	 *
	 * @return boolean, true if the given type is a view type, false otherwise.
	 */
	public static boolean isViewType(int type) {
		if (type == MAPVIEW || type == LISTVIEW)
			return true;
		else
			return false;
	}

	/**
	 * Return a view object with the given id.
	 * If a view node with the given id has already been created in this session, return that,
	 * else create a new one, and add it to the list.
	 *
	 * @param String id, the id of the node to return/create.
	 * @return View, a view node object with the given id.
	 */
	public static View getView(String id) {

		int i = 0;
		Vector nodeSummaryList = NodeSummary.getNodeSummaryList();
		for (i = 0; i < nodeSummaryList.size(); i++) {
			if (id.equals(((NodeSummary)nodeSummaryList.elementAt(i)).getId())) {
				break;
			}
		}

		View ns = null;
		if (i == nodeSummaryList.size()) {
			ns = new View(id);
			nodeSummaryList.addElement(ns);
		}
		else {
			Object obj = nodeSummaryList.elementAt(i);
			if (obj instanceof View) {
				ns = (View)obj;
			}
			else {
				nodeSummaryList.removeElement(obj);
				ns = new View(id);
				nodeSummaryList.addElement(ns);
			}
		}
		return ns;
	}

	/**
	 * Return a node summary object with the given id and details.
	 * If a view node with the given id has already been created in this session, update its data and return that,
	 * else create a new one, and add it to the list.
	 *
	 *	@param String sViewID, the id of the view node.
	 *	@param int nType, the type of this node.
	 *	@param String sXNodeType, the extended node type id of the node - NOT CURRENTLY USED.
	 *	@param String sOriginalID, the original id of the node if it was imported.
	 *	@param String sAuthor, the author of the node.
	 *	@param Date dCreationDate, the creation date of this node.
	 *	@param Date dModificationDate, the date the node was last modified.
	 *	@param String sLabel, the label of this node.
	 *	@param String sDetail, the first page of detail for this node.
	 *  @return View, a view node object with the given id.
	 */
	public static View getView(String sViewID, int nType, String sXNodeType, String sOriginalID,
				int nState, String sAuthor, Date dCreationDate, Date dModificationDate, 
				String sLabel, String sDetail)
	{
		int i = 0;
		Vector nodeSummaryList = NodeSummary.getNodeSummaryList();
		for (i = 0; i < nodeSummaryList.size(); i++) {
			if (sViewID.equals(((NodeSummary)nodeSummaryList.elementAt(i)).getId())) {
				break;
			}
		}

		View ns = null;
		if (i == nodeSummaryList.size()) {
			ns = new View(sViewID, nType, sXNodeType, sOriginalID,
								 nState,
								 sAuthor, dCreationDate,
								 dModificationDate, sLabel, sDetail);
			nodeSummaryList.addElement(ns);
		}
		else {
			Object obj = nodeSummaryList.elementAt(i);
			if (obj instanceof View) {
				ns = (View)obj;

				// UPDATE THE DETAILS
				ns.setLabelLocal(sLabel);
				ns.setDetailLocal(sDetail);
				ns.setTypeLocal(nType);
				ns.setStateLocal(nState);
				ns.setAuthorLocal(sAuthor);
				ns.setCreationDateLocal(dCreationDate);
				ns.setModificationDateLocal(dModificationDate);
				ns.setOriginalIdLocal(sOriginalID);
				ns.setExtendedNodeTypeLocal(sXNodeType);
			}
			else {
				nodeSummaryList.removeElement(obj);
				ns = new View(sViewID, nType, sXNodeType, sOriginalID,
								 nState, sAuthor, dCreationDate,
								 dModificationDate, sLabel, sDetail);
				nodeSummaryList.addElement(ns);
			}
		}
		return ns;
	}

	/**
	 * Return a node summary object with the given id and details.
	 * If a view node with the given id has already been created in this session, update its data and return that,
	 * else create a new one, and add it to the list.
	 *
	 *	@param sViewID the id of the view node.
	 *	@param nType the type of this node.
	 *	@param sXNodeType the extended node type id of the node - NOT CURRENTLY USED.
	 *	@param sOriginalID the original id of the node if it was imported.
	 *	@param sAuthor the author of the node.
	 *	@param dCreationDate the creation date of this node.
	 *	@param dModificationDate the date the node was last modified.
	 *	@param sLabel the label of this node.
	 *	@param sDetail the first page of detail for this node.
	 *	@param sLastModAuthor the author who last modified this object.
	 *  @return View, a view node object with the given id.
	 */
	public static View getView(String sViewID, int nType, String sXNodeType, String sOriginalID,
				int nState, String sAuthor, Date dCreationDate, Date dModificationDate, 
				String sLabel, String sDetail, String sLastModAuthor)
	{
		int i = 0;
		Vector nodeSummaryList = NodeSummary.getNodeSummaryList();
		for (i = 0; i < nodeSummaryList.size(); i++) {
			if (sViewID.equals(((NodeSummary)nodeSummaryList.elementAt(i)).getId())) {
				break;
			}
		}

		View ns = null;
		if (i == nodeSummaryList.size()) {
			ns = new View(sViewID, nType, sXNodeType, sOriginalID,
								 nState,
								 sAuthor, dCreationDate,
								 dModificationDate, sLabel, sDetail, sLastModAuthor);
			nodeSummaryList.addElement(ns);
		}
		else {
			Object obj = nodeSummaryList.elementAt(i);
			if (obj instanceof View) {
				ns = (View)obj;

				// UPDATE THE DETAILS
				ns.setLabelLocal(sLabel);
				ns.setDetailLocal(sDetail);
				ns.setTypeLocal(nType);
				ns.setStateLocal(nState);
				ns.setAuthorLocal(sAuthor);
				ns.setCreationDateLocal(dCreationDate);
				ns.setModificationDateLocal(dModificationDate);
				ns.setOriginalIdLocal(sOriginalID);
				ns.setExtendedNodeTypeLocal(sXNodeType);
				ns.setLastModificationAuthorLocal(sLastModAuthor);				
			}
			else {
				nodeSummaryList.removeElement(obj);
				ns = new View(sViewID, nType, sXNodeType, sOriginalID,
								 nState, sAuthor, dCreationDate,
								 dModificationDate, sLabel, sDetail);
				nodeSummaryList.addElement(ns);
			}
		}
		return ns;
	}
	
	/**
	 * Return a node summary object with the given id and details.
	 * If a view node with the given id has already been created in this session, update its data and return that,
	 * else create a new one, and add it to the list.
	 *
	 *	@param String sViewID, the id of the view node.
	 *	@param int nType, the type of this node.
	 *	@param String sXNodeType, the extended node type id of the node - NOT CURRENTLY USED.
	 *	@param String sOriginalID, the original id of the node if it was imported.
	 *	@param int nPermission, the permissions in this node - NOT CURRENTLY USED.
	 *	@param int nState, the state of this node: not read (0) read (1), modified since last read (/2).
	 *	@param String sAuthor, the author of the node.
	 *	@param Date dCreationDate, the creation date of this node.
	 *	@param Date dModificationDate, the date the node was last modified.
	 *	@param String sLabel, the label of this node.
	 *	@param String sDetail, the first page of detail for this node.
	 *  @return View, a view node object with the given id.
	 */
	public static View getView(String sViewID, int nType, String sXNodeType, String sOriginalID, int nPermission,
							int nState, String sAuthor, Date dCreationDate, Date dModificationDate,
							String sLabel, String sDetail)
	{
		int i = 0;
		Vector nodeSummaryList = NodeSummary.getNodeSummaryList();
		for (i = 0; i < nodeSummaryList.size(); i++) {
			if (sViewID.equals(((NodeSummary)nodeSummaryList.elementAt(i)).getId())) {
				break;
			}
		}

		View ns = null;
		if (i == nodeSummaryList.size()) {
			ns = new View(sViewID, nType, sXNodeType, sOriginalID,
								 nPermission, nState, sAuthor, dCreationDate,
								 dModificationDate, sLabel, sDetail);
			nodeSummaryList.addElement(ns);
		}
		else {
			Object obj = nodeSummaryList.elementAt(i);
			if (obj instanceof View) {
				ns = (View)obj;

				// UPDATE THE DETAILS
				ns.setLabelLocal(sLabel);
				ns.setDetailLocal(sDetail);
				ns.setTypeLocal(nType);
				ns.setAuthorLocal(sAuthor);
				ns.setCreationDateLocal(dCreationDate);
				ns.setModificationDateLocal(dModificationDate);
				ns.setOriginalIdLocal(sOriginalID);
				ns.setExtendedNodeTypeLocal(sXNodeType);
				ns.setPermissionLocal(nPermission);
				ns.setStateLocal(nState);
			}
			else {
				nodeSummaryList.removeElement(obj);
				ns = new View(sViewID, nType, sXNodeType, sOriginalID,
								 nPermission, nState, sAuthor, dCreationDate,
								 dModificationDate, sLabel, sDetail);
				nodeSummaryList.addElement(ns);
			}
		}
		return ns;
	}

	/**
	 * The initialize method adds the model and session object to this object.
	 *
	 * @param PCSession session, the session associated with this object.
	 * @param IMode model, the model this object belongs to.
	 */
	public void initialize(PCSession session,IModel model) {
		super.initialize(session,model);
	}

	/**
	 * Return a count of the nodes in this view.
	 * If the view has not been initialized yet, it gets the count from the database.
	 *
	 * @return int, a count of the nodes in this view.
	 * @exception ModelSessionException
	 */
	public int getNodeCount() throws ModelSessionException {

		if (isMembersInitialized()) {
			return getNumberOfNodes();
		}
		else {
			if (preInitializedNodeCount == -1) {
				try {
					if (oModel == null)
						throw new ModelSessionException("Model is null in View.getNodecount");
					if (oSession == null) {
						oSession = oModel.getSession();
						if (oSession == null)
							throw new ModelSessionException("Session is null in Viwe.getNodeCount");
					}

					preInitializedNodeCount = oModel.getViewService().getNodeCount(oSession, this.getId());
				}
				catch(Exception ex) {
					return 0;
				}
			}
			return preInitializedNodeCount;
		}
	}

	/**
	 * Return true if this view has loaded its node and link data from the database, else false.
	 */
	public boolean isMembersInitialized() {
		return bMembersInitialized;
	}

	/**
	 * Set whether this view has loaded its node and link data from the database.
	 * @param boolean init, true if this view has loaded its node and link data from the database, else false.
	 */
	public void setIsMembersInitialized(boolean init) {
		bMembersInitialized = init;
		if (init == false)
			preInitializedNodeCount = -1;
	}

	/**
	 * Loads all the nodes and links into this view from the DATABASE.
	 *
	 * @exception java.sql.SQLException
	 * @exception ModelSessionException
	 */
	public void initializeMembers() throws SQLException, ModelSessionException {

		if (!bMembersInitialized) {

			if (oModel == null)
				throw new ModelSessionException("Model is null in View.initializeMembers");
			if (oSession == null) {
				oSession = oModel.getSession();
				if (oSession == null)
					throw new ModelSessionException("Session is null in View.initializeMembers");
			}

			Vector vtNodePos = oModel.getViewService().getNodePositions(oModel.getSession(), this.getId());

			for(Enumeration e = vtNodePos.elements(); e.hasMoreElements();) {
				NodePosition nodePos = (NodePosition)e.nextElement();
				nodePos.initialize(oModel.getSession(), oModel);
				NodeSummary node1 = nodePos.getNode();
				int xPos = nodePos.getXPos();
				int yPos = nodePos.getYPos();
				nodePos.setView(this);

				addMemberNode(nodePos);

				node1.initialize(oModel.getSession(), oModel);
			}

			//Get Links DO AFTER GET NODES SO APPROPRIATE NodeSummary entries created.
			Vector vtLinks = oModel.getViewService().getLinks(oModel.getSession(),this.getId());
			for(Enumeration e = vtLinks.elements(); e.hasMoreElements();) {
				Link link = (Link)e.nextElement();

				if (link != null) {
					link = addMemberLink(link);
					link.initialize(oModel.getSession(), oModel);

					// AS DONE AFTER NODES CREATED LINKS SHOULD HAVE CORRECT NODES IN
					/*try {
						//NodeSummary nodeFrom = (NodeSummary)oModel.getNodeSummary(link.getFrom());
						//NodeSummary nodeTo = (NodeSummary)oModel.getNodeSummary(link.getTo());

						NodeSummary nodeFrom = NodeSummary.getNodeSummary(link.getFrom().getId());
						NodeSummary nodeTo = NodeSummary.getNodeSummary(link.getTo().getId());

						link.setFrom(nodeFrom);
						link.setTo(nodeTo);
					}
					catch (NoSuchElementException ex) {
						//System.out.println("NODE CONNECTED TO THIS LINK DNE!!! Skipping Link ... ");
					}*/
				}
			}

			loadViewLayer();
		}
		bMembersInitialized = true;
	}

	/**
	 * Set the scribble data associated with this view.
	 * @param sScribble, the scribble data associated with this View.
	 */
	public void setScribble(String sScribble) {
		if (oViewLayer == null) {
			try {
				loadViewLayer();
			}catch (Exception ex) {System.out.println("Exception: View.setScribble: "+ex.getMessage());}
		}
		if (oViewLayer != null)
			oViewLayer.setScribble(sScribble);
	}

	/**
	 * Set the background image associated with this view.
	 * @param sBackground, the background image associated with this View.
	 */
	public void setBackground(String sBackground) {

		if (oViewLayer == null) {
			try {
				loadViewLayer();
			}
			catch (Exception ex) {
				System.out.println("Exception: View.setBackground: "+ex.getMessage());
			}
		}
		if (oViewLayer != null) {
			oViewLayer.setBackground(sBackground);
		}
	}

	/**
	 * Set the grid data associated with this view.
	 * @param sGrid, the grid data associated with this View.
	 */
	public void setGrid(String sGrid) {
		if (oViewLayer == null) {
			try {
				loadViewLayer();
			}catch (Exception ex) {System.out.println("Exception: View.setGrid: "+ex.getMessage());}
		}
		if (oViewLayer != null)
			oViewLayer.setGrid(sGrid);
	}

	/**
	 * Set the shapes data associated with this view.
	 * @param sShapes, the shapes data associated with this View.
	 */
	public void setShapes(String sShapes) {
		if (oViewLayer == null) {
			try {
				loadViewLayer();
			}catch (Exception ex) {System.out.println("Exception: View.setShape: "+ex.getMessage());}
		}
		if (oViewLayer != null)
			oViewLayer.setShapes(sShapes);
	}

	/**
	 * Load the ViewLayer.
	 *
	 * @exception java.sql.SQLException
	 * @exception ModelSessionException
	 */
	public void loadViewLayer() throws SQLException, ModelSessionException {

		if (oModel == null)
			throw new ModelSessionException("Model is null in View.loadViewLayer");
		if (oSession == null) {
			oSession = oModel.getSession();
			if (oSession == null)
				throw new ModelSessionException("Session is null in View.loadViewLayer");
		}

		oViewLayer = oModel.getViewLayerService().getViewLayer(oSession, oModel.getUserProfile().getId(), this.getId());
		if (oViewLayer == null) {
			oViewLayer = new ViewLayer();
			oViewLayer.setUserID(oModel.getUserProfile().getId());
			oViewLayer.setViewID(this.getId());
			oModel.getViewLayerService().createViewLayer(oSession, oModel.getUserProfile().getId(), oViewLayer);
		}

		oViewLayer.initialize(	oSession, oModel );
	}

	/**
	 * Update the ViewLayer in the database.
	 *
	 * @exception java.sql.SQLException
	 * @exception ModelSessionException
	 */
	public void updateViewLayer() throws SQLException, ModelSessionException {
		if (oViewLayer != null)
			oViewLayer.update();
	}

	/**
	 * Return the ViewLayer associated with this View.
	 * @return ViewLayer, the ViewLayer associated with this View.
	 */
	public ViewLayer getViewLayer() {
		return oViewLayer;
	}

	/**
	 * Return the number of nodes in this view.
	 */
	public int getNumberOfNodes() {
		return htMemberNodes.size();
	}

	/**
	 * Return the number of links in this view.
	 */
	public int getNumberOfLinks() {
		return htMemberLinks.size();
	}

	/**
	 * Adds a new node with the given properties to this view at
	 * the given x and y coordinate, both locally and in the DATABASE.
	 *
	 * @param nType the type of this node.
	 * @param sXNodeType the extended node type id of the node - NOT CURRENTLY USED.
	 * @param sOriginalID the original id of the node if it was imported.
	 * @param sAuthor the author of the node.
	 * @param sLabel the label of this node.
	 * @param sDetail the first page of detail for this node.
	 * @param x The X coordinate of the node in the view
	 * @param y The Y coordinate of the node in the view
	 * @return the NodePosition object if the node was successfully added, otherwise null.
	 * @exception java.sql.SQLException
	 * @exception ModelSessionException
	 */
	public NodePosition addMemberNode(  int nType,
										String sXNodeType,
										String sOriginalID,
										String sAuthor,
										String sLabel,
										String sDetail,
										int x,
										int y) throws SQLException, ModelSessionException {

		return addMemberNode( nType, sXNodeType, "", sOriginalID, sAuthor, sLabel, sDetail,	x,  y);
	}

	/**
	 * Adds a new node with the given properties to this view at
	 * the given x and y coordinate, both locally and in the DATABASE.
	 *
	 * @param nType the type of this node.
	 * @param sXNodeType the extended node type id of the node - NOT CURRENTLY USED.
	 * @param sImportedID the imported id,
	 * @param sOriginalID the original id of the node.
	 * @param sAuthor the author of the node.
	 * @param sLabel the label of this node.
	 * @param sDetail the first page of detail for this node.
	 * @param x The X coordinate of the node in the view.
	 * @param y The Y coordinate of the node in the view.
	 * @exception java.sql.SQLException
	 * @exception ModelSessionException
	 */
	public NodePosition addMemberNode(  int nType,
										String sXNodeType,
										String sImportedId,
										String sOriginalID,
										String sAuthor,
										String sLabel,
										String sDetail,
										int x,
										int y) throws SQLException, ModelSessionException {

		if (oModel == null)
			throw new ModelSessionException("Model is null in View.addMemberNode-2");
		if (oSession == null) {
			oSession = oModel.getSession();
			if (oSession == null)
				throw new ModelSessionException("Session is null in View.addMemberNode-2");
		}

		// get unique id first
		String sNodeID = getModel().getUniqueID() ;
		Date date = new Date();

		return addMemberNode( sNodeID, nType, sXNodeType, sImportedId, sOriginalID, sAuthor, date, 
				date, sLabel, sDetail, x, y, date, date);
	}

	
	/**
	 * Adds a new node with the given properties to this view at
	 * the given x and y coordinate, both locally and in the DATABASE.
	 *
	 * @param nType the type of this node.
	 * @param sXNodeType the extended node type id of the node - NOT CURRENTLY USED.
	 * @param sImportedID the imported id,
	 * @param sOriginalID the original id of the node.
	 * @param sAuthor the author of the node.
	 * @param creationDate the date the node was created.
	 * @param modDate the date the node was last modified.
	 * @param sLabel the label of this node.
	 * @param sDetail the first page of detail for this node.
	 * @param x The X coordinate of the node in the view.
	 * @param y The Y coordinate of the node in the view.
	 * @param transCreationDate, the date the node was put into the view.
	 * @param transModDate, the date the view-node data was last modified.
	 * @exception java.sql.SQLException
	 * @exception ModelSessionException
	 */
	public NodePosition addMemberNode(  int nType,
										String sXNodeType,
										String sImportedId,
										String sOriginalID,
										String sAuthor,
										Date creationDate,
										Date modDate,
										String sLabel,
										String sDetail,
										int x,
										int y,
										Date transCreationDate,
										Date transModDate) throws SQLException, ModelSessionException {

		if (oModel == null)
			throw new ModelSessionException("Model is null in View.addMemberNode-2");
		if (oSession == null) {
			oSession = oModel.getSession();
			if (oSession == null)
				throw new ModelSessionException("Session is null in View.addMemberNode-2");
		}

		// get unique id first
		String sNodeID = getModel().getUniqueID() ;

		return addMemberNode( sNodeID, nType, sXNodeType, sImportedId, sOriginalID, sAuthor, creationDate, modDate,
								sLabel, sDetail, x, y, transCreationDate, transModDate);
	}

	/**
	 * Adds a new node with the given properties to this view at
	 * the given x and y coordinate, both locally and in the DATABASE.
	 *
	 * @param sNodeID, The node id of the node to be added to the view
	 * @param int nType, the type of this node.
	 * @param String sXNodeType, the extended node type id of the node - NOT CURRENTLY USED.
	 * @param String sImportedID, the imported id,
	 * @param String sOriginalID, the original id of the node.
	 * @param String sAuthor, the author of the node.
	 * @param creationDate, the date the node was created.
	 * @param modDate, the date the node was last modified.
	 * @param String sLabel, the label of this node.
	 * @param String sDetail, the first page of detail for this node.
	 * @param x, The X coordinate of the node in the view.
	 * @param y, The Y coordinate of the node in the view.
	 * @param transCreationDate, the date the node was put into the view.
	 * @param transModDate, the date the view-node data was last modified.
	 * @exception java.sql.SQLException
	 * @exception ModelSessionException
	 */
	public NodePosition addMemberNode(  String sNodeID,
										int nType,
										String sXNodeType,
										String sImportedId,
										String sOriginalID,
										String sAuthor,
										String sLabel,
										String sDetail,
										int x,
										int y) throws SQLException, ModelSessionException {

		Date date = new Date();

		return addMemberNode( sNodeID, nType, sXNodeType, sImportedId, sOriginalID, sAuthor, date, date,
								sLabel, sDetail, x, y, date, date);
	}

	/**
	 * Adds a new node with the given properties to this view at
	 * the given x and y coordinate, both locally and in the DATABASE.
	 *
	 * @param sNodeID, The node id of the node to be added to the view
	 * @param nType the type of this node.
	 * @param sXNodeType the extended node type id of the node - NOT CURRENTLY USED.
	 * @param sImportedID the imported id,
	 * @param sOriginalID the original id of the node.
	 * @param sAuthor the author of the node.
	 * @param creationDate the date the node was created.
	 * @param modDate the date the node was last modified.
	 * @param sLabel the label of this node.
	 * @param sDetail the first page of detail for this node.
	 * @param x The X coordinate of the node in the view.
	 * @param y The Y coordinate of the node in the view.
	 * @param transCreationDate the date the node was put into the view.
	 * @param transModDate the date the view-node data was last modified.
	 * @exception java.sql.SQLException
	 * @exception ModelSessionException
	 */
	public NodePosition addMemberNode(  String sNodeID,
										int nType,
										String sXNodeType,
										String sImportedId,
										String sOriginalID,
										String sAuthor,
										Date creationDate,
										Date modDate,
										String sLabel,
										String sDetail,
										int x,
										int y,
										Date transCreationDate,
										Date transModDate) throws SQLException, ModelSessionException {

		if (oModel == null)
			throw new ModelSessionException("Model is null in View.addMemberNode-2");
		if (oSession == null) {
			oSession = oModel.getSession();
			if (oSession == null)
				throw new ModelSessionException("Session is null in View.addMemberNode-2");
		}
		
		Model model = (Model)oModel;
		
		return addMemberNode( sNodeID, nType, sXNodeType, sImportedId, sOriginalID, sAuthor, creationDate, 
				modDate, sLabel, sDetail, x, y, transCreationDate, transModDate, sAuthor, 
				model.showTagsNodeIndicator, model.showTextNodeIndicator, model.showTransNodeIndicator,
				model.showWeightNodeIndicator, model.smallIcons, model.hideIcons,
				model.labelWrapWidth, model.fontsize, model.fontface, model.fontstyle,
				model.FOREGROUND_DEFAULT.getRGB(), model.BACKGROUND_DEFAULT.getRGB());
		}

	/**
	 * Adds a new node with the given properties to this view at
	 * the given x and y coordinate, both locally and in the DATABASE.
	 *
	 * @param nType the type of this node.
	 * @param sXNodeType the extended node type id of the node - NOT CURRENTLY USED.
	 * @param sImportedID the imported id,
	 * @param sOriginalID the original id of the node.
	 * @param sAuthor the author of the node.
	 * @param creationDate the date the node was created.
	 * @param modDate the date the node was last modified.
	 * @param sLabel the label of this node.
	 * @param sDetail the first page of detail for this node.
	 * @param x The X coordinate of the node in the view.
	 * @param y The Y coordinate of the node in the view.
	 * @param transCreationDate the date the node was put into the view.
	 * @param transModDate the date the view-node data was last modified.
	 * @param sLastModAuthor the author name of the current user.
	 * @param bShowTags true if this node has the tags indicator draw.
	 * @param bShowText true if this node has the text indicator drawn
	 * @param bShowTrans true if this node has the transclusion indicator drawn
	 * @param bShowWeight true if this node has the weight indicator displayed
	 * @param bSmallIcon true if this node is using a small icon
	 * @param bHideIcons true if this node is not displaying its icon
	 * @param nWrapWidth the node label wrap width used for this node in this view.
	 * @param nFontSize	the font size used for this node in this view
	 * @param sFontFace the font face used for this node in this view
	 * @param nFontStyle the font style used for this node in this view
	 * @param nForeground the foreground color used for this node in this view
	 * @param nBackground the background color used for this node in this view.
	 * 
	 * @exception java.sql.SQLException
	 * @exception ModelSessionException
	 */
	public NodePosition addMemberNode( int nType,
										String sXNodeType,
										String sImportedId,
										String sOriginalID,
										String sAuthor,
										Date creationDate,
										Date modDate,
										String sLabel,
										String sDetail,
										int x,
										int y,
										Date transCreationDate,
										Date transModDate,
										String sLastModAuthor,
										boolean bShowTags, 
										boolean bShowText, 
										boolean bShowTrans, 
										boolean bShowWeight, 
										boolean bSmallIcon, 
										boolean bHideIcon, 
										int 	nWrapWidth, 
										int 	nFontSize, 
										String 	sFontFace, 
										int 	nFontStyle, 
										int 	nForeground, 
										int 	nBackground) throws SQLException, ModelSessionException {	
				
		String sNodeID = getModel().getUniqueID() ;
		
		return addMemberNode( sNodeID, nType, sXNodeType, sImportedId, sOriginalID, sAuthor, creationDate, 
				modDate, sLabel, sDetail, x, y, transCreationDate, transModDate, sAuthor, 
				bShowTags, bShowText, bShowTrans, bShowWeight, bSmallIcon, bHideIcon,
				nWrapWidth, nFontSize, sFontFace, nFontStyle, nForeground, nBackground);			
	}
	
	
	/**
	 * Adds a new node with the given properties to this view at
	 * the given x and y coordinate, both locally and in the DATABASE.
	 *
	 * @param sNodeID The node id of the node to be added to the view
	 * @param nType the type of this node.
	 * @param sXNodeType the extended node type id of the node - NOT CURRENTLY USED.
	 * @param sImportedID the imported id,
	 * @param sOriginalID the original id of the node.
	 * @param sAuthor the author of the node.
	 * @param creationDate the date the node was created.
	 * @param modDate the date the node was last modified.
	 * @param sLabel the label of this node.
	 * @param sDetail the first page of detail for this node.
	 * @param x The X coordinate of the node in the view.
	 * @param y The Y coordinate of the node in the view.
	 * @param transCreationDate the date the node was put into the view.
	 * @param transModDate the date the view-node data was last modified.
	 * @param sLastModAuthor the author name of the current user.
	 * @param bShowTags true if this node has the tags indicator draw.
	 * @param bShowText true if this node has the text indicator drawn
	 * @param bShowTrans true if this node has the transclusion indicator drawn
	 * @param bShowWeight true if this node has the weight indicator displayed
	 * @param bSmallIcon true if this node is using a small icon
	 * @param bHideIcons true if this node is not displaying its icon
	 * @param nWrapWidth the node label wrap width used for this node in this view.
	 * @param nFontSize	the font size used for this node in this view
	 * @param sFontFace the font face used for this node in this view
	 * @param nFontStyle the font style used for this node in this view
	 * @param nForeground the foreground color used for this node in this view
	 * @param nBackground the background color used for this node in this view.
	 * 
	 * @exception java.sql.SQLException
	 * @exception ModelSessionException
	 */
	public NodePosition addMemberNode(  String sNodeID,	int type,
										String sXNodeType,
										String sImportedId,
										String sOriginalID,
										String sAuthor,
										Date creationDate,
										Date modDate,
										String sLabel,
										String sDetail,
										int x,
										int y,
										Date transCreationDate,
										Date transModDate,
										String sLastModAuthor,
										boolean bShowTags, 
										boolean bShowText, 
										boolean bShowTrans, 
										boolean bShowWeight, 
										boolean bSmallIcon, 
										boolean bHideIcon, 
										int 	nWrapWidth, 
										int 	nFontSize, 
										String 	sFontFace, 
										int 	nFontStyle, 
										int 	nForeground, 
										int 	nBackground) throws SQLException, ModelSessionException {
		
		if (oModel == null)
			throw new ModelSessionException("Model is null in View.addMemberNode-2");
		if (oSession == null) {
			oSession = oModel.getSession();
			if (oSession == null)
				throw new ModelSessionException("Session is null in View.addMemberNode-2");
		}

		// decide what permission to give depending on whether it is a view type or a node type
		int permission = -1 ;

		if ( type == ICoreConstants.MAPVIEW || type == ICoreConstants.LISTVIEW )
			permission = ICoreConstants.WRITEVIEWNODE ;
		else // it is of type node
			permission = ICoreConstants.WRITE ;

		// set state property for node summary object
		int state = 0 ;

  		INodeService ns = oModel.getNodeService();
		INodeSummary node = null ;
		
		if (oModel == null) {
			throw new ModelSessionException("oModel is null in View.addMemberNode-2b");			
		} else if (ns == null) {
			throw new ModelSessionException("NodeService is null in View.addMemberNode-2c");						
		}
		
		node = ns.createNode(oModel.getSession(), sNodeID, type, sXNodeType, sImportedId, sOriginalID,
								 permission, state, sAuthor, sLabel, sDetail,
								 creationDate, modDate, sLastModAuthor);

		node.setModel(oModel);

		//Create the NodePosition in the View Node table
  		IViewService vs = oModel.getViewService() ;
 		NodePosition nodePos = vs.addMemberNode(oModel.getSession(), this, (NodeSummary)node, x , y, 
							transCreationDate, transModDate, bShowTags, bShowText, bShowTrans, bShowWeight, 
							bSmallIcon, bHideIcon, nWrapWidth, nFontSize, sFontFace, nFontStyle, 
							nForeground, nBackground);

		int oldChildCount = htMemberNodes.size();
		
		//Local Hashtable update
		htMemberNodes.put(node.getId(),nodePos);

		//update the view count for this NodeSumary object
		node.updateMultipleViews();

		firePropertyChange(CHILDREN_PROPERTY, oldChildCount, htMemberNodes.size());
		firePropertyChange(NODE_ADDED, nodePos, nodePos);

		return nodePos;
	}
	
	/**
	 * Replace a node in this view.
	 *
	 * @param NodePosition oPos, the node to be replaced in the view, in the local data ONLY.
	 * @return NodePosition, the passed object.
	 */
	public NodePosition replaceMemberNode(NodePosition oPos)  {

		htMemberNodes.put(oPos.getNode().getId(), oPos);
		return oPos;
	}

	/**
	 * Adds a node to this view locally only.
	 *
	 * @param NodePosition nodePos, the node to be added to the view, in the local data ONLY.
	 * @return NodePosition, the nodePosition if the node was successfully added.
	 * @see INodeSummary
	 */
	public NodePosition addMemberNode(NodePosition nPos)  {

		int oldChildCount = htMemberNodes.size();

		if(htMemberNodes.get(nPos.getNode().getId()) == null) {
			htMemberNodes.put(nPos.getNode().getId(),nPos);
		}

		//update the view count for this NodeSumary object
		nPos.getNode().updateMultipleViews();

		firePropertyChange(CHILDREN_PROPERTY, oldChildCount, htMemberNodes.size());
		firePropertyChange(NODE_TRANSCLUDED, nPos, nPos);

		return nPos;
	}

	/**
	 * Adds a node with the given properties to this view at
	 * the given x and y coordinate, both locally and in the DATABASE.
	 *
	 * @param NodeSumary node, The node to be added to the view.
	 * @param int x, The X coordinate of the node in the view.
	 * @param int y, The Y coordinate of the node in the view.
	 * @param bShowTags true if this node has the tags indicator draw.
	 * @param bShowText true if this node has the text indicator drawn
	 * @param bShowTrans true if this node has the transclusion indicator drawn
	 * @param bShowWeight true if this node has the weight indicator displayed
	 * @param bSmallIcon true if this node is using a small icon
	 * @param bHideIcons true if this node is not displaying its icon
	 * @param nWrapWidth the node label wrap width used for this node in this view.
	 * @param nFontSize	the font size used for this node in this view
	 * @param sFontFace the font face used for this node in this view
	 * @param nFontStyle the font style used for this node in this view
	 * @param nForeground the foreground color used for this node in this view
	 * @param nBackground the background color used for this node in this view.
	 *  
	 * @return NodePosition, the node if the node was successfully added, null otherwise.
	 * @exception java.sql.SQLException
	 * @exception ModelSessionException
	 * @see INodeSummary
	 */
	public NodePosition addNodeToView(NodeSummary node,
										int x,
										int y,
										boolean bShowTags, 
										boolean bShowText, 
										boolean bShowTrans, 
										boolean bShowWeight, 
										boolean bSmallIcon, 
										boolean bHideIcon, 
										int 	nWrapWidth, 
										int 	nFontSize, 
										String 	sFontFace, 
										int 	nFontStyle, 
										int 	nForeground, 
										int 	nBackground) throws SQLException, ModelSessionException {

		if (oModel == null)
			throw new ModelSessionException("Model is null in View.addNodeToView");
		if (oSession == null) {
			oSession = oModel.getSession();
			if (oSession == null)
				throw new ModelSessionException("Session is null in View.addNodeToView");
		}

  		IViewService vs = oModel.getViewService();
		Date creationDate = new Date();
		Date lastModified = creationDate;
		
		Model model = (Model)oModel;
				
		INodePosition nodePos = vs.addMemberNode(oSession, this, node, x , y, creationDate, lastModified, 
				bShowTags, bShowText, bShowTrans, bShowWeight, 
				bSmallIcon, bHideIcon, nWrapWidth, nFontSize, sFontFace, nFontStyle, 
				nForeground, nBackground);

		if (nodePos != null) {

			int oldChildCount = htMemberNodes.size();

			//Local Hashtable update
			htMemberNodes.put(node.getId(), nodePos);

			//update the view count for this NodeSumary object
			node.updateMultipleViews();

			firePropertyChange(CHILDREN_PROPERTY, oldChildCount, htMemberNodes.size());
			firePropertyChange(NODE_TRANSCLUDED, nodePos, nodePos);
		}

		return (NodePosition)nodePos ;
	}
	
	/**
	 * Adds a node with the given properties to this view at
	 * the given x and y coordinate, both locally and in the DATABASE.
	 *
	 * @param NodeSumary node, The node to be added to the view.
	 * @param int x, The X coordinate of the node in the view.
	 * @param int y, The Y coordinate of the node in the view.
	 * @return NodePosition, the node if the node was successfully added, null otherwise.
	 * @exception java.sql.SQLException
	 * @exception ModelSessionException
	 * @see INodeSummary
	 */
	public NodePosition addNodeToView(NodeSummary node,
										int x,
										int y) throws SQLException, ModelSessionException {

		if (oModel == null)
			throw new ModelSessionException("Model is null in View.addNodeToView");
		if (oSession == null) {
			oSession = oModel.getSession();
			if (oSession == null)
				throw new ModelSessionException("Session is null in View.addNodeToView");
		}

  		IViewService vs = oModel.getViewService();
		Date creationDate = new Date();
		Date lastModified = creationDate;
		
		Model model = (Model)oModel;
		
		INodePosition nodePos = vs.addMemberNode(oSession, this, node, x , y, creationDate, lastModified, 
				model.showTagsNodeIndicator, model.showTextNodeIndicator, model.showTransNodeIndicator,
				model.showWeightNodeIndicator, model.smallIcons, model.hideIcons,
				model.labelWrapWidth, model.fontsize, model.fontface, model.fontstyle,
				model.FOREGROUND_DEFAULT.getRGB(), model.BACKGROUND_DEFAULT.getRGB());

		if (nodePos != null) {

			int oldChildCount = htMemberNodes.size();

			//Local Hashtable update
			htMemberNodes.put(node.getId(), nodePos);

			//update the view count for this NodeSumary object
			node.updateMultipleViews();

			firePropertyChange(CHILDREN_PROPERTY, oldChildCount, htMemberNodes.size());
			firePropertyChange(NODE_TRANSCLUDED, nodePos, nodePos);
		}

		return (NodePosition)nodePos ;
	}

	/**
	 * Removes the given node from this view, both locally and from the DATABASE.
	 *
	 * @param NodeSummary node, The node to be removed from this view.
	 * @return boolean, true if the node was removed, else false.
	 * @exception java.util.NoSuchElementException
	 * @exception java.sql.SQLException
	 * @exception ModelSessionException
	 */
	public boolean removeMemberNode(NodeSummary node) throws NoSuchElementException, SQLException, ModelSessionException {

		if (oModel == null)
			throw new ModelSessionException("Model is null in View.removeMemberNode");
		if (oSession == null) {
			oSession = oModel.getSession();
			if (oSession == null)
				throw new ModelSessionException("Session is null in View.removeMemberNode");
		}

		// now call NodeService with all parameters
  		INodeService ns = oModel.getNodeService();

  		//try and remove the node from the DB
		boolean deleted = false;
		deleted = ns.deleteNode(getSession(), node, this.getId()) ;
		
		if (deleted) {
			int oldChildCount = htMemberNodes.size();
	
			//remove the node from the hashtable
			if(htMemberNodes.containsKey(node.getId())) {
				htMemberNodes.remove(node.getId());
			}
	
			//update the view count for this NodeSumary object
			node.updateMultipleViews();
	
			firePropertyChange(CHILDREN_PROPERTY, oldChildCount, htMemberNodes.size());
			firePropertyChange(NODE_REMOVED, node, node);
		}

		//removeFromDatamodel() in NodeUI uses the return value to set the trashbin full/empty icon.
		//deleted is set to true if the node is actually deleted and not just removed from a single view.
		return deleted;
	}

	/**
	 * Returns all relations between this view and its nodes.
	 * @param Enumeration, a list of all relations between this view and its nodes.
	 */
	public Enumeration getPositions() {
		return htMemberNodes.elements();
	}

	/**
	 * Sets the position of the given node in this view, both locally and in the DATABASE.
	 *
	 * @param String sNodeID, the id of the node to set the new position for.
	 * @param Point oPoint, the new position of the node.
	 * @return boolean, true if database was updated successfully, else false.
	 * @exception java.util.NoSuchElementException
	 * @exception java.sql.SQLException
	 * @exception ModelSessionException
	 */
	public boolean setNodePosition(String sNodeID, Point oPoint) throws NoSuchElementException, SQLException, ModelSessionException {

		if (oModel == null)
			throw new ModelSessionException("Model is null in View.setNodePosition");
		if (oSession == null) {
			oSession = oModel.getSession();
			if (oSession == null)
				throw new ModelSessionException("Session is null in View.setNodePosition");
		}

		// now call ViewService with all parameters
  		IViewService vs = oModel.getViewService() ;
		boolean updated = false;

		//Local Hashtable update
		if(htMemberNodes.get(sNodeID) == null) {
			throw new NoSuchElementException("Node with id "+sNodeID+" could not be found in this view");
		}
		else {
			updated = vs.setNodePosition(oSession, this.getId(), sNodeID, oPoint);
			NodePosition nodePos = (NodePosition)htMemberNodes.get(sNodeID);
			htMemberNodes.remove(sNodeID);
			nodePos.setPos(oPoint);
			htMemberNodes.put(sNodeID,nodePos);
		}
		return updated;
	}

	/**
	 * Does this view contain this node?
	 *
	 * @param NodeSummary oNode, the node to be checked in the view.
	 * @return boolean, true if this view contains this node, else false.
	 */
	public boolean containsNodeSummary(NodeSummary oNode)  {

		if(htMemberNodes.containsKey(oNode.getId()) )
			return true;

		return false;
	}

	/**
	 * Does this view contain this node?
	 *
	 * @param NodePosition oPos, the node to be checked in the view.
	 * @return boolean true if this view contains this node, else false.
	 */
	public boolean containsNode(NodePosition oPos)  {

		if(htMemberNodes.containsKey(oPos.getNode().getId()) )
			return true;

		return false;
	}

	/**
	 * Does this view contain the node with the given OriginalID?
	 *
	 * @param String sOriginalID, the id to check against.
	 * @return boolean true if this view contains this node with the given OriginalID, else false.
	 */
	public boolean containsOriginalID(String sOriginalID)  {

		for (Enumeration e = htMemberNodes.elements(); e.hasMoreElements(); ) {
			NodePosition pos = (NodePosition) e.nextElement();
			NodeSummary node = pos.getNode();
			String sID = node.getOriginalID();
			if (sOriginalID.equals(sID)) {
				return true;
			}
		}

		return false;
	}


	/**
	 * Return the <code>NodePosition</code> with the given id.
	 *
	 * @param String sID, the node id of the <code>NodePosition</code> to find.
	 * @return the NodePosition if found, else null.
	 */
	public NodePosition getNodePosition(String sID)  {

		if(htMemberNodes.containsKey(sID) ) {
			NodePosition pos = (NodePosition) htMemberNodes.get(sID);
			return pos;
		}
		return null;
	}

	/**
	 * Return the node with the given id
	 *
	 * @param String The node id of the node to find
	 * @return the node if found, else null
	 */
	public NodeSummary getNode(String sID)  {

		NodeSummary node = null;
		if(htMemberNodes.containsKey(sID) ) {
			NodePosition pos = (NodePosition) htMemberNodes.get(sID);
			return pos.getNode();
		}
		return node;
	}

	/**
	 * Get all the Reference Nodes in this view.
	 *
	 * @return Vector, of reference nodes in this view.
	 */
	public Vector getReferenceNodes()  {

		Vector refNodes = new Vector(51);
		for (Enumeration e = htMemberNodes.elements(); e.hasMoreElements(); ) {
			NodePosition pos = (NodePosition) e.nextElement();
			NodeSummary node = pos.getNode();
			int type = node.getType();
			if (type == ICoreConstants.REFERENCE || type == ICoreConstants.REFERENCE_SHORTCUT)
				refNodes.addElement(node);
		}

		return refNodes;
	}

	/**
	 * Adds a new link with the given properties to this view, both locally and in the DATABASE.
	 *
	 * @param type The link type of the link to be added to the view
	 * @param sOriginalID, The original imported id
	 * @param String author, the author of the link.
	 * @param from The link's originating node
	 * @param to The link's destination node
	 * @param arrow, the type of arrow to draw.
	 * @return the link if the link was successfully added, null otherwise
	 * @exception java.sql.SQLException
	 * @exception ModelSessionException
	 * @see com.compendium.core.datamodel.Link
	 * @see com.compendium.core.datamodel.INodeSummary
	 */
	public ILink addMemberLink(String type, String sOriginalID, String author, INodeSummary from, INodeSummary to, int arrow)
						throws SQLException, ModelSessionException {

		// get unique id first
		String linkId = oModel.getUniqueID();

		return addMemberLink(linkId, type, "", sOriginalID, author, from, to, arrow);
	}

	/**
	 * Adds a new link with the given properties to this view, both locally and in the DATABASE.
	 *
	 * @param id, the link id of this link.
	 * @param type The link type of the link to be added to the view.
	 * @param sOriginalID, The original imported id.
	 * @param String author, the author of the link.
	 * @param from The link's originating node.
	 * @param to The link's destination node.
	 * @param arrow, the type of arrow to draw.
	 * @return the link if the link was successfully added, null otherwise
	 * @exception java.sql.SQLException
	 * @exception ModelSessionException
	 * @see com.compendium.core.datamodel.Link
	 * @see com.compendium.core.datamodel.INodeSummary
	 */
	public ILink addMemberLink(String linkId, String type, String sImportedID, String sOriginalID, String author,
									INodeSummary from, INodeSummary to, int arrow)
										throws SQLException, ModelSessionException {

		return addMemberLink(linkId, type, sImportedID, sOriginalID, author, from, to, "", arrow);
	}

	/**
	 * Adds a new link with the given properties to this view, both locally and in the DATABASE.
	 *
	 * @param type The link type of the link to be added to the view
	 * @param sOriginalID, The original imported id
	 * @param String author, the author of the link.
	 * @param from The link's originating node
	 * @param to The link's destination node
	 * @param sLabel, the label for this link.
	 * @param arrow, the type of arrow to draw.
	 * @return the link if the link was successfully added, null otherwise
	 * @exception java.sql.SQLException
	 * @exception ModelSessionException
	 * @see com.compendium.core.datamodel.Link
	 * @see com.compendium.core.datamodel.INodeSummary
	 */
	public ILink addMemberLink(String type, String sOriginalID, String author, INodeSummary from, INodeSummary to, String sLabel, int arrow)
						throws SQLException, ModelSessionException {

		// get unique id first
		String linkId = oModel.getUniqueID();

		return addMemberLink(linkId, type, "", sOriginalID, author, from, to, "", arrow);
	}

	/**
	 * Adds a new link with the given properties to this view, both locally and in the DATABASE.
	 *
	 * @param type The link type of the link to be added to the view.
	 * @param sImportedID, the imported id.
	 * @param sOriginalID, The original imported id
	 * @param String author, the author of the link.
	 * @param from The link's originating node.
	 * @param to The link's destination node.
	 * @param sLabel, the label for this link.
	 * @param arrow, the type of arrow to draw.
	 * @return the link if the link was successfully added, null otherwise
	 * @exception java.sql.SQLException
	 * @exception ModelSessionException
	 * @see com.compendium.core.datamodel.Link
	 * @see com.compendium.core.datamodel.INodeSummary
	 */
	public ILink addMemberLink(String type, String sImportedID, String sOriginalID, String author, INodeSummary from, INodeSummary to, String sLabel, int arrow)
						throws SQLException, ModelSessionException {

		// get unique id first
		String linkId = oModel.getUniqueID();

		return addMemberLink(linkId, type, sImportedID, sOriginalID, author, from, to, sLabel, arrow);
	}

	/**
	 * Adds a new link with the given properties to this view, both locally and in the DATABASE.
	 *
	 * @param id, the link id of this link.
	 * @param type The link type of the link to be added to the view.
	 * @param String sImportedID, the imported id,
	 * @param sOriginalID, The original imported id.
	 * @param String author, the author of the link.
	 * @param from The link's originating node.
	 * @param to The link's destination node.
	 * @param sLabel, the label for this link.
	 * @param arrow, the type of arrow to draw.
	 * @return the link if the link was successfully added, null otherwise
	 * @exception java.sql.SQLException
	 * @exception ModelSessionException
	 * @see com.compendium.core.datamodel.Link
	 * @see com.compendium.core.datamodel.INodeSummary
	 */
	public ILink addMemberLink(String linkId, String type, String sImportedID, String sOriginalID, String author,
									INodeSummary from, INodeSummary to, String sLabel, int arrow)
										throws SQLException, ModelSessionException {

		if (oModel == null)
			throw new ModelSessionException("Model is null in View.addMemberLink");
		if (oSession == null) {
			oSession = oModel.getSession();
			if (oSession == null)
				throw new ModelSessionException("Session is null in View.addMemberLink");
		}

		Date creationDate 	= new Date();
		Date modificationDate = creationDate;
		String fromId 		= from.getId();
		String toId 		= to.getId();
		String viewId 		= this.getId();

		// now call LinkService with all parameters
  		ILinkService ls = oModel.getLinkService();
		ILink link = null ;
		link = ls.createLink(oSession,
								linkId,
								creationDate,
								modificationDate,
								author,
								type,
								sImportedID,
								sOriginalID,
								fromId,
								toId,
								sLabel,
								arrow);

		//Add the link to ViewLink table

		// Call ViewService with all parameters
		boolean added = false;
  		IViewService vs = oModel.getViewService() ;
		added = vs.addMemberLink(oSession, this.getId(), link.getId()) ;

		//Local Hashtable update
		if(htMemberLinks.get(link.getId()) == null) {
			htMemberLinks.put(link.getId(),link);
		}
		else {
			htMemberLinks.remove(link.getId());
			htMemberLinks.put(link.getId(),link);
		}

		//set the from and to nodes references as the link from the db doesnt have node references
		link.setFrom((NodeSummary)from);
		link.setTo((NodeSummary)to);

		firePropertyChange(LINK_ADDED, link, link);

		return link;
	}

	/**
	 * Adds a link to this view.
	 *
	 * @param link, The link to be added to the view
	 * @return true if link was successfully added, else false.
	 * @see Link
	 */
	public boolean addLinkToView(Link link) throws SQLException, ModelSessionException {

		if (link == null) {
			return false;
		}
		
		if (oModel == null)
			throw new ModelSessionException("Model is null in View.addLinkToView");
		if (oSession == null) {
			oSession = oModel.getSession();
			if (oSession == null)
				throw new ModelSessionException("Session is null in View.addLinkToView");
		}

  		IViewService vs = oModel.getViewService() ;
		try {
			boolean added = vs.addMemberLink(oSession, getId(), link.getId());
			if (added) {
				if (!htMemberLinks.containsKey(link.getId())) {
					htMemberLinks.put(link.getId(), link);
				}
				else {
					htMemberLinks.remove(link.getId());
					htMemberLinks.put(link.getId(),link);
				}
				firePropertyChange(LINK_ADDED, link, link);
				return true;
			}
		}
		catch (SQLException ex) {
			ex.printStackTrace();
			System.out.println("The link: "+link.getLabel()+", could not be added.\nIt may already be in this view");
		}

		return false;
	}

	/*
	 * Adds a link to this view, in the local data ONLY.
	 *
	 * @param Link link, the link to be added to the view.
	 * @return Link, the link if the link was successfully added, null otherwise.
	 * @see ILink
	 */
	public Link addMemberLink(Link link) {

		if (link != null) {
			if(htMemberLinks.get(link.getId()) == null) {
				htMemberLinks.put(link.getId(),link);
			}
			else {
				htMemberLinks.remove(link.getId());
				htMemberLinks.put(link.getId(),link);
			}
		}

		firePropertyChange(LINK_ADDED, link, link);

		return link;
	}

	/**
	 * Removes a link from this view, both locally and from the DATABASE.
	 *
	 * @param Link link, the link to be removed from this view.
	 * @return boolean, true if the link was successfully removed, else false.
	 * @exception java.util.NoSuchElementException
	 * @exception java.sql.SQLException
	 * @exception ModelSessionException
	 * @see ILink
	 */
	public boolean removeMemberLink(Link link) throws NoSuchElementException, SQLException, ModelSessionException {

		if (oModel == null)
			throw new ModelSessionException("Model is null in View.removeMemberLink");
		if (oSession == null) {
			oSession = oModel.getSession();
			if (oSession == null)
				throw new ModelSessionException("Session is null in View.removeMemberLink");
		}

		boolean deleted = false;

		// now call LinkService with all parameters
  		ILinkService ls = oModel.getLinkService() ;

		// mark the link as deleted from the ViewLinks Table
		ls.deleteView(oSession, getId(), link.getId());

		// mark the link as deleted in Link table
		deleted = ls.deleteLink(oSession, link.getId(), getId());

		//remove the link from the hashtable
		if(deleted) {
			if(htMemberLinks.containsKey(link.getId())) {
				htMemberLinks.remove(link.getId());
			}
		}

		firePropertyChange(LINK_ADDED, link, link);

		return deleted;
	}

	/**
	 * Purges a link from this view, both locally and in the DATABASE, (permenantly removes the record from the database).
	 *
	 * @param Link link, the link to be removed from this view.
	 * @return boolean, true if the purge was successful, else false.
	 * @exception java.util.NoSuchElementException
	 * @exception java.sql.SQLException
	 * @exception ModelSessionException
	 * @see ILink
	 */
	public boolean purgeMemberLink(Link link) throws NoSuchElementException, SQLException, ModelSessionException {

		if (oModel == null)
			throw new ModelSessionException("Model is null in View.purgeMemberLink");
		if (oSession == null) {
			oSession = oModel.getSession();
			if (oSession == null)
				throw new ModelSessionException("Session is null in View.purgeMemberLink");
		}

		boolean deleted = false;

		// now call LinkService with all parameters
  		ILinkService ls = oModel.getLinkService() ;

		// purge the link from Link table
		deleted = ls.purgeLink(oSession, link.getId(), getId());

		//remove the link from the hashtable
		if(deleted) {
			if(htMemberLinks.containsKey(link.getId())) {
				htMemberLinks.remove(link.getId());
			}
		}

		firePropertyChange(LINK_REMOVED, link, link);

		return deleted;
	}

	/**
	 * Purges all links from this view, both locally and in the DATABASE, (permenantly removes the records from the database).
	 *
	 * @return boolean, true if the purge was successful, else false.
	 * @exception java.util.NoSuchElementException
	 * @exception java.sql.SQLException
	 * @exception ModelSessionException
	 * @see ILink
	 */
	public boolean purgeAllLinks() throws NoSuchElementException, SQLException, ModelSessionException {

		if (oModel == null)
			throw new ModelSessionException("Model is null in View.purgeAllLinks");
		if (oSession == null) {
			oSession = oModel.getSession();
			if (oSession == null)
				throw new ModelSessionException("Session is null in View.purgeAllLinks");
		}

		boolean deleted = false;

		// now call LinkService with all parameters
  		ILinkService ls = oModel.getLinkService();

		// purge the links from Link table
		deleted = ls.purgeAllLinks(oSession, getId());

		//remove the links from the hashtable
		if(deleted) {
			htMemberLinks.clear();
		}

		return deleted;
	}

	/**
	 * Returns if this view contains the link with the given id.
	 * @param sLinkID, the id to check for
	 * @return boolean, true if the view contains the link, else false.
	 */
	public boolean containsLink(String sLinkID) {
		return htMemberLinks.containsKey(sLinkID);
	}

	/**
	 * Returns all the links in this view.
	 *
	 * @return Enumeration, an array of all the links in this view.
	 * @see ILink
	 */
	public Enumeration getLinks() {
		return htMemberLinks.elements();
	}

	/**
	 * Returns all the links in this view for the node with the given id.
	 *
	 * @param sNodeID, the id of the node to get the links for.
	 * @return Vector, an array of all the links for the node.
	 * @see ILink
	 */
	public Vector getLinksForNode(String sNodeID) {

		Vector vtMatches = new Vector();
		int count = htMemberLinks.size();
		NodeSummary oFromNode = null;
		NodeSummary oToNode = null;

		for (Enumeration e = htMemberLinks.elements(); e.hasMoreElements(); ) {
			Link link = (Link)e.nextElement();
			oFromNode = link.getFrom();
			if (oFromNode.getId().equals(sNodeID)) {
				vtMatches.addElement(link);

			}
			else {
				oToNode = link.getTo();
				if (oToNode.getId().equals(sNodeID)) {
					vtMatches.addElement(link);
				}
			}
		}


		return vtMatches;
	}

	/**
	 * Returns the node with the earliest creation date.
	 */
	public NodeSummary getFirstNode() throws SQLException, ModelSessionException {
		if (!isMembersInitialized())
			initializeMembers();

		Enumeration nodes = getPositions();

		long nextTime = 0;
		NodeSummary firstNode = null;
		for(Enumeration e = nodes;e.hasMoreElements();) {
			NodePosition nodePos = (NodePosition)e.nextElement();
			NodeSummary node = nodePos.getNode();
			long time = node.getCreationDate().getTime();
			if (nextTime == 0 || time < nextTime) {
				nextTime = time;
				firstNode = node;
			}
		}

		return firstNode;
	}

	/**
	 * Return the label of this node.
	 */
  	public String toString() {
		return getLabel();
  	}

	/**
	 * Add the given Link to the list of deleted links.
	 * @param Link deletedLink, the link to add to the list of the deleted links.
	 */
	public void addDeletedLink(Link deletedLink) {
		deletedLinks.addElement(deletedLink);
	}

	/**
	 * Add the given NodePosition to the list of deleted nodes.
	 * @param NodePosition deletedNode, the node to be added to the list.
	 */
	public void addDeletedNode(NodePosition deletedNode) {
		deletedNodes.addElement(deletedNode);
	}

	/**
	 * Return a list of all deleted links.
	 * @return java.util.Vector, the links deleted.
	 */
	public Vector getDeletedLinks() {
		return deletedLinks;
	}

	/**
	 * Return a list of all deleted nodes.
	 * @return java.util.Vector, of all deleted nodes.
	 */
	public Vector getDeletedNodes() {
		return deletedNodes;
	}

	/**
	 * Delete all nodes and purge links in this view as view is about to become another node type, both locally and in the DATABASE.
	 *
	 * @exception java.sql.SQLException
	 * @exception ModelSessionException
	 */
	public void clearViewForTypeChange() throws SQLException, ModelSessionException {

		if (oModel == null)
			throw new ModelSessionException("Model is null in View.clearViewForTypeChange");
		if (oSession == null) {
			oSession = oModel.getSession();
			if (oSession == null)
				throw new ModelSessionException("Session is null in View.clearViewForTypeChange");
		}

		for (Enumeration e = getPositions(); e.hasMoreElements();) {
			NodePosition pos = (NodePosition)e.nextElement();
			NodeSummary node = pos.getNode();

			// IF NODE ALREADY DELETED, DON'T TRY AND DELETE CHILDREN AGAIN
			// NEED TO CATCH NEVERENDING LOOP WHEN NODE CONTAINS ITSELF SOMEWHERE IN CHILDREN TREE
			boolean wasDeleted = false;
			if (oModel.getNodeService().isMarkedForDeletion(oSession, node.getId())) {
				wasDeleted = true;
			}

			boolean deleted = removeMemberNode(pos.getNode());

			// IF NODE IS A VIEW AND IF NODE WAS ACTUALLY LAST INSTANCE AND HAS NOT ALREADY BEEN DELETED, DELETE CHILDREN
			if (node instanceof View && !wasDeleted && deleted) {
				View childView = (View)node;
				childView.clearViewForTypeChange();
			}
		}

		for(Enumeration et = htMemberLinks.elements();et.hasMoreElements();) {
			Link link = (Link)et.nextElement();
			purgeMemberLink(link);
		}
 	}

	/**
	 * Store child nodes (FOR COPY/PASTE TO OTHER DATABASE)
	 * using deleted node and links vectors as these are used by paste function.
	 *
	 * @param Hashtable copyCheck, the hashtable of nodes checked.
	 */
	public void storeChildren(Hashtable copyCheck) {

		// CHECK FOR DUPLICATION TO PREVENT NEVER ENDING LOOP
		if (!copyCheck.containsKey(getId())) {
			copyCheck.put(getId(), this);

			if (!isMembersInitialized()) {
				try {initializeMembers();}
				catch(Exception ex) {}
			}

			for (Enumeration e = getPositions(); e.hasMoreElements();) {
				NodePosition pos = (NodePosition)e.nextElement();
				addDeletedNode(pos);
				if (pos.getNode() instanceof View) {
					View view = (View)pos.getNode();
					view.storeChildren(copyCheck);
				}
			}
			for (Enumeration e2 = getLinks(); e2.hasMoreElements();) {
				Link deletedLink = (Link)e2.nextElement();
				addDeletedLink(deletedLink);
			}
		}
	}

	/**
	 * @return Returns the Nodesummary of the member nodes.
	 */
	// Lakshmi (5/11/06) - It is used in setting the state of view node.
	// The state of the view node depends on teh states of the contained nodes.
	public Vector getMemberNodes() {
		Vector vtMembers = new Vector();
		for(Enumeration e = htMemberNodes.elements(); e.hasMoreElements();){
			NodePosition np = (NodePosition) e.nextElement();
			NodeSummary node = np.getNode();
			vtMembers.add(node);
		}
		return vtMembers;
	}
}
