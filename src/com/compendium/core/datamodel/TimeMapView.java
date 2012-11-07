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

package com.compendium.core.datamodel;

import java.util.*;
import java.sql.SQLException;

import com.compendium.core.datamodel.services.*;

/**
 * The View object is a node that represents a collection of nodes and links with related timespans.
 * The visual representation of the nodes and links depends on the type of the view.
 *
 * @author	Michelle Bachler 
 */
public class TimeMapView extends View implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** time added property name for use with property change events */
	public final static String TIME_ADDED_PROPERTY = "timeadded";

	/** time removed property name for use with property change events */
	public final static String TIME_REMOVED_PROPERTY = "timeremoved";

	/** time changed property name for use with property change events */
	public final static String TIME_CHANGED_PROPERTY = "timechanged";

	/** A List of all the time spans for this node. */
	protected Hashtable htMemberNodeTimes 		= new Hashtable(51);

	/**
	 *	Constructor, takes in only the id value.
	 *
	 *	@param sNodeID String, the id of the view object.
	 */
	public TimeMapView(String sNodeID) {
		super(sNodeID);
	}

	/**
	 *	Constructor, creates a TimeMapView object.
	 *
	 *	@param String sViewID, the id of the view node.
	 *	@param nType int, the type of this node.
	 *	@param sXNodeType String, the extended node type id of the node - NOT CURRENTLY USED.
	 *	@param sOriginalID String, the original id of the node if it was imported.
	 *	@param sAuthor String, the author of this node.
	 *	@param dCreationDate Date, the creation date of this node.
	 *	@param dModificationDate Date, the date the node was last modified.
	 */
	protected TimeMapView(String sViewID, int nType, String sXNodeType, String sOriginalID,
					int nState, String sAuthor, Date dCreationDate, Date dModificationDate, 
					String sLabel, String sDetail)
	{
		super( sViewID,  nType,  sXNodeType,  sOriginalID, nState, sAuthor,  dCreationDate,  dModificationDate,  sLabel, sDetail);
	}

	/**
	 *	Constructor, creates a TimeMapView object.
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
	protected TimeMapView(String sViewID, int nType, String sXNodeType, String sOriginalID, int nPermission,
							int nState, String sAuthor, Date dCreationDate, Date dModificationDate,
							String sLabel, String sDetail) {

		super(sViewID,  nType,  sXNodeType,  sOriginalID, nPermission, nState, sAuthor,  
				dCreationDate,  dModificationDate,  sLabel, sDetail);
	}
	
	/**
	 *	Constructor, creates a TimeMapView object.
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
	protected TimeMapView(String sViewID, int nType, String sXNodeType, String sOriginalID,
					int nState, String sAuthor, Date dCreationDate, Date dModificationDate, 
					String sLabel, String sDetail, String sLastModAuthor)
	{
		super( sViewID,  nType,  sXNodeType,  sOriginalID, nState, sAuthor,  dCreationDate,  
				dModificationDate,  sLabel, sDetail, sLastModAuthor);
	}
	
	/**
	 *	Constructor, creates a TimeMapView object.
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
	protected TimeMapView(String sViewID, int nType, String sXNodeType, String sOriginalID, int nPermission,
							int nState, String sAuthor, Date dCreationDate, Date dModificationDate,
							String sLabel, String sDetail, String sLastModAuthor) {

		super(sViewID,  nType,  sXNodeType,  sOriginalID, nPermission, nState, sAuthor,  dCreationDate,  
				dModificationDate, sLabel, sDetail, sLastModAuthor);
	}	

	/**
	 * Return a TimeMapView object with the given id.
	 * If a view node with the given id has already been created in this session, return that,
	 * else create a new one, and add it to the list.
	 *
	 * @param String id, the id of the node to return/create.
	 * @return View, a view node object with the given id.
	 */
	public static TimeMapView getView(String id) {

		int i = 0;
		Vector nodeSummaryList = NodeSummary.getNodeSummaryList();
		for (i = 0; i < nodeSummaryList.size(); i++) {
			if (id.equals(((NodeSummary)nodeSummaryList.elementAt(i)).getId())) {
				break;
			}
		}

		TimeMapView ns = null;
		if (i == nodeSummaryList.size()) {
			ns = new TimeMapView(id);
			nodeSummaryList.addElement(ns);
		}
		else {
			Object obj = nodeSummaryList.elementAt(i);
			if (obj instanceof TimeMapView) {
				ns = (TimeMapView)obj;
			}
			else {
				nodeSummaryList.removeElement(obj);
				ns = new TimeMapView(id);
				nodeSummaryList.addElement(ns);
			}
		}
		return ns;
	}

	/**
	 * Return a TimeMapView object with the given id and details.
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
	public static TimeMapView getView(String sViewID, int nType, String sXNodeType, String sOriginalID,
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

		TimeMapView ns = null;
		if (i == nodeSummaryList.size()) {
			ns = new TimeMapView(sViewID, nType, sXNodeType, sOriginalID,
								 nState,
								 sAuthor, dCreationDate,
								 dModificationDate, sLabel, sDetail);
			nodeSummaryList.addElement(ns);
		}
		else {
			Object obj = nodeSummaryList.elementAt(i);
			if (obj instanceof TimeMapView) {
				ns = (TimeMapView)obj;

				// UPDATE THE DETAILS
				if (!ns.bLabelDirty) {
					ns.setLabelLocal(sLabel);
				}
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
				ns = new TimeMapView(sViewID, nType, sXNodeType, sOriginalID,
								 nState, sAuthor, dCreationDate,
								 dModificationDate, sLabel, sDetail);
				nodeSummaryList.addElement(ns);
			}
		}
		return ns;
	}

	/**
	 * Return a TimeMapView object with the given id and details.
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
	public static TimeMapView getView(String sViewID, int nType, String sXNodeType, String sOriginalID,
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

		TimeMapView ns = null;
		if (i == nodeSummaryList.size()) {
			ns = new TimeMapView(sViewID, nType, sXNodeType, sOriginalID,
								 nState,
								 sAuthor, dCreationDate,
								 dModificationDate, sLabel, sDetail, sLastModAuthor);
			nodeSummaryList.addElement(ns);
		}
		else {
			Object obj = nodeSummaryList.elementAt(i);
			if (obj instanceof TimeMapView) {
				ns = (TimeMapView)obj;

				// UPDATE THE DETAILS
				if (!ns.bLabelDirty) {
					ns.setLabelLocal(sLabel);
				}
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
				ns = new TimeMapView(sViewID, nType, sXNodeType, sOriginalID,
								 nState, sAuthor, dCreationDate,
								 dModificationDate, sLabel, sDetail);
				nodeSummaryList.addElement(ns);
			}
		}
		return ns;
	}
	
	/**
	 * Return a TimeMapView object with the given id and details.
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
	public static TimeMapView getView(String sViewID, int nType, String sXNodeType, String sOriginalID, int nPermission,
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

		TimeMapView ns = null;
		if (i == nodeSummaryList.size()) {
			ns = new TimeMapView(sViewID, nType, sXNodeType, sOriginalID,
								 nPermission, nState, sAuthor, dCreationDate,
								 dModificationDate, sLabel, sDetail);
			nodeSummaryList.addElement(ns);
		}
		else {
			Object obj = nodeSummaryList.elementAt(i);
			if (obj instanceof TimeMapView) {
				ns = (TimeMapView)obj;

				// UPDATE THE DETAILS
				if (!ns.bLabelDirty) {				
					ns.setLabelLocal(sLabel);
				}
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
				ns = new TimeMapView(sViewID, nType, sXNodeType, sOriginalID,
								 nPermission, nState, sAuthor, dCreationDate,
								 dModificationDate, sLabel, sDetail);
				nodeSummaryList.addElement(ns);
			}
		}
		return ns;
	}

	/**
	 * Override.
	 * Loads all the nodes and node times and links into this view from the DATABASE.
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

			super.initializeMembers();
			bMembersInitialized = false;
			
			Vector vtNodeTimes = oModel.getViewService().getNodeTimes(oModel.getSession(), this.getId());

			for(Enumeration e = vtNodeTimes.elements(); e.hasMoreElements();) {
				NodePositionTime nodeTime = (NodePositionTime)e.nextElement();
				nodeTime.initialize(oModel.getSession(), oModel);
				NodeSummary node1 = nodeTime.getNode();
				int xPos = nodeTime.getXPos();
				int yPos = nodeTime.getYPos();
				nodeTime.setView(this);
				
				if (htMemberNodeTimes.containsKey(node1.getId())){
					Hashtable times = (Hashtable)htMemberNodeTimes.get(node1.getId());
					times.put(nodeTime.getId(), nodeTime);
					htMemberNodeTimes.put(node1.getId(), times);
				} else {
					Hashtable times = new Hashtable();
					times.put(nodeTime.getId(), nodeTime);
					htMemberNodeTimes.put(node1.getId(), times);
				}
				
				node1.initialize(oModel.getSession(), oModel);	
			}
		}
		bMembersInitialized = true;
	}
	
	/**
	 * Clear all data associated with this View and reloads it from scratch from the database.
	 * 
	 * @exception java.sql.SQLException
	 * @exception ModelSessionException	 
	 * */
	public void reloadViewData() throws SQLException, ModelSessionException {
		this.htMemberNodeTimes.clear();
		super.reloadViewData();
	}

	/**
	 * Get the Hashtable of <code>NodePositionTime</code>objects for the given node id
	 * @param sNodeID the node id to return the time points for
	 * @return Hashtable of NodePositionTime objects for the given node id
	 */
	public Hashtable<String, NodePositionTime> getTimesForNode(String sNodeID) {
		if (this.htMemberNodeTimes.containsKey(sNodeID)) {
			return (Hashtable<String, NodePositionTime>)htMemberNodeTimes.get(sNodeID);
		}
		
		return new Hashtable<String, NodePositionTime>(0);
	}
	
	/**
	 * Adds a new node time span to the given node, both locally and in the DATABASE.
	 *
	 * @param sNodeID the id of the node to add the time record for
	 * @param nShow the time (in milliseconds) to show the node
	 * @param nHide the time (in milliseconds) to hide the node
	 * @param x The X coordinate of the node in the view
	 * @param y The Y coordinate of the node in the view
	 * @return the NodePositionTime object if the node was successfully added, otherwise null.
	 * @exception java.sql.SQLException
	 * @exception ModelSessionException
	 */
	public NodePositionTime addNodeTime(String sNodeID,
										long nShow,
										long nHide,
										int x,
										int y) throws SQLException, ModelSessionException {

		
		if (oModel == null)
			throw new ModelSessionException("Model is null in View.addMemberNode-2");
		if (oSession == null) {
			oSession = oModel.getSession();
			if (oSession == null)
				throw new ModelSessionException("Session is null in View.addMemberNode-2");
		}


		//Create the NodePositionTime in the ViewTimeNode table
  		IViewService vs = getModel().getViewService();
		String sViewTimeNodeID = oModel.getUniqueID();

 		NodePositionTime nodeTime = vs.addNodeTime(oModel.getSession(), sViewTimeNodeID, this, getNode(sNodeID), nShow, nHide, x , y);

		//Local Hashtable update
		if (htMemberNodeTimes.containsKey(sNodeID)){
			Hashtable times = (Hashtable)htMemberNodeTimes.get(sNodeID);
			times.put(sViewTimeNodeID, nodeTime);
			htMemberNodeTimes.put(sNodeID, times);
		} else {
			Hashtable<String, NodePositionTime> times = new Hashtable<String, NodePositionTime>();
			times.put(sViewTimeNodeID, nodeTime);
			htMemberNodeTimes.put(sNodeID, times);
		}

		firePropertyChange(TIME_ADDED_PROPERTY, nodeTime, nodeTime);

		return nodeTime;
	}
	
	/**
	 * Update a node time span, both locally and in the DATABASE.
	 *
	 * @param sViewTimeNodeID the id of the time point to update
	 * @param sNodeID the id of the node to add the time record for
	 * @param nShow the time (in milliseconds) to show the node
	 * @param nHide the time (in milliseconds) to hide the node
	 * @param x The X coordinate of the node in the view
	 * @param y The Y coordinate of the node in the view
	 * @return the NodePositionTime object if the node was successfully updated, otherwise null.
	 * @exception java.sql.SQLException
	 * @exception ModelSessionException
	 */
	public NodePositionTime updateNodeTime(String sViewTimeNodeID,
										String sNodeID, 
										long nShow,
										long nHide,
										int x,
										int y) throws SQLException, ModelSessionException {

		
		if (oModel == null)
			throw new ModelSessionException("Model is null in View.addMemberNode-2");
		if (oSession == null) {
			oSession = oModel.getSession();
			if (oSession == null)
				throw new ModelSessionException("Session is null in View.addMemberNode-2");
		}


		//Create the NodePositionTime in the ViewTimeNode table
  		IViewService vs = getModel().getViewService();

 		NodePositionTime nodeTime = vs.updateNodeTime(oModel.getSession(), sViewTimeNodeID, this, getNode(sNodeID), nShow, nHide, x , y);

		NodePositionTime oldPos = null;
		//Replace
		if (htMemberNodeTimes.containsKey(sNodeID)){
			Hashtable times = (Hashtable)htMemberNodeTimes.get(sNodeID);
			oldPos = (NodePositionTime)times.get(sViewTimeNodeID);
			times.put(sViewTimeNodeID, nodeTime);
			htMemberNodeTimes.put(sNodeID, times);
		} else {
			Hashtable times = new Hashtable();
			times.put(sViewTimeNodeID, nodeTime);
			htMemberNodeTimes.put(sNodeID, times);
		}

		firePropertyChange(TIME_CHANGED_PROPERTY, oldPos, nodeTime);

		return nodeTime;
	}

	/**
	 * Delete a node time span from the given node, both locally and in the DATABASE.
	 *
	 * @param sViewTimeNodeID the id of the time point to delete
	 * @param sNodeID the id of the node to add the time record for
	 * @exception java.sql.SQLException
	 * @exception ModelSessionException
	 */
	public void deleteNodeTime(String sViewTimeNodeID, String sNodeID) 
						throws SQLException, ModelSessionException { 
		
		if (oModel == null)
			throw new ModelSessionException("Model is null in View.addMemberNode-2");
		if (oSession == null) {
			oSession = oModel.getSession();
			if (oSession == null)
				throw new ModelSessionException("Session is null in View.addMemberNode-2");
		}

  		IViewService vs = getModel().getViewService();
 		vs.deleteNodeTime(oModel.getSession(), sViewTimeNodeID);

		//Local Hashtable update
		if (htMemberNodeTimes.containsKey(sNodeID)){
			Hashtable times = (Hashtable)htMemberNodeTimes.get(sNodeID);
			times.remove(sViewTimeNodeID);
		} 

		firePropertyChange(TIME_REMOVED_PROPERTY, sViewTimeNodeID, sViewTimeNodeID);
	}
	
	/**
	 * Clear all the node time data as well as call super to clear nodes and links etc.
	 *
	 * @exception java.sql.SQLException
	 * @exception ModelSessionException
	 */
	public void clearViewForTypeChange() throws SQLException, ModelSessionException {
		clearTimes();
		super.clearViewForTypeChange();
	}
	
	/**
	 * Clear all the node time data. (used for node type change)
	 *
	 * @exception java.sql.SQLException
	 * @exception ModelSessionException
	 */
	public void clearTimes() throws SQLException, ModelSessionException {

		// delete the associated node times before nodes removed
		Hashtable<String, NodePositionTime> times = null;
		NodePositionTime item = null;
		for (Enumeration e = getPositions(); e.hasMoreElements();) {
			NodePosition pos = (NodePosition)e.nextElement();
			times = getTimesForNode(pos.getNode().getId());
			for (Enumeration time = times.elements(); e.hasMoreElements();) {
				item = (NodePositionTime)time.nextElement();
				deleteNodeTime(item.getId(), pos.getNode().getId());
			}
		}
	}		
}
