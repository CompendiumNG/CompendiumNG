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

package com.compendium.core.datamodel.services;

import java.util.*;
import java.sql.*;
import java.awt.Dimension;

import com.compendium.core.ICoreConstants;
import com.compendium.core.datamodel.*;
import com.compendium.core.db.*;
import com.compendium.core.db.management.*;

/**
 *	The interface for the NodeService class
 *	The NodeService class provides services to manipuate NodeSummary record data in the database.
 *
 *	@author Sajid and Rema / Michelle Bachler / Lakshmi Prabhakaran
 */
public class NodeService extends ClientService implements INodeService, java.io.Serializable {

	/** The computed serial version ID  */
	private static final long serialVersionUID = 2666615789208015303L;

	/**
	 *	constructor
	 */
	public NodeService() {
		super();
	}

	/**
	 * Constructor, set the name of the service.
	 *
	 * @param String sName, the name of this service.
	 */
	public NodeService(String sName) {
		super(sName);
	}

	/**
	 *	Constructor, set the name, ServiceManager and DBDatabaseManager for this service.
	 *
	 * @param String sName, the name of this service.
	 * @param ServiceManager sm, the ServiceManager used by this service.
	 * @param DBDatabaseManager dbMgr, the DBDatabaseManager used by this service.
	 */
	public NodeService(String sName, ServiceManager sm, DBDatabaseManager dbMgr) {
		super(sName, sm, dbMgr);
	}


	/**
	 *	Adds a new node to the database and returns it if successful
	 *
	 * @param session the session object for the database to use.
	 * @param sNodeID the id of the node being created.
	 * @param nType the integer type of the node being created.
	 * @param sXNodeType the extended node type of the node being created.
	 * @param sOriginalID the original ID if relevant else empty String.
	 * @param nPermission the permission level of the node being created.
	 * @param nState the inital state of the node being created
	 * @param sAuthor the author of this node
	 * @param sLabel the label of this node
	 * @param sDetail the primary details (page 1 of any details), for this node
	 * @param dCreationDate the creation date of this node
	 * @param dMmodificationDate the modification date of this node (same as creation date).
	 * @return NodeSummary, the node summary object representing the node created successfully, otherwise null.
	 * @exception java.sql.SQLException
	 */
	public NodeSummary createNode(PCSession session, String sNodeID, int nType, String sXNodeType,
				String sOriginalID, int nPermission, int nState, String sAuthor, String sLabel, String sDetail,
				java.util.Date dCreationDate, java.util.Date dModificationDate)
				throws SQLException {

		return createNode(session, sNodeID, nType, sXNodeType, "", sOriginalID, nPermission, nState, sAuthor, sLabel, //$NON-NLS-1$
							sDetail, dCreationDate, dModificationDate);
	}

	/**
	 *	Adds a new node to the database and returns it if successful
	 *
	 * @param PCSession session, the session object for the database to use.
	 * @param String sNodeID, the id of the node being created.
	 * @param int nType, the integer type of the node being created.
	 * @param String sXNodeType, the extended node type of the node being created.
	 * @param sImportedID, the imported ID if relevant else empty String.
	 * @param sOriginalID, the original ID if relevant else empty String.
	 * @param int nPermission, the permission level of the node being created.
	 * @param int nState, the inital state of the node being created
	 * @param String sAuthor, the author of this node
	 * @param String sLabel, the label of this node
	 * @param String sDetail, the primary details (page 1 of any details), for this node
	 * @param Date dCreationDate, the creation date of this node
	 * @param Date dMmodificationDate, the modification date of this node (same as creation date).
	 * @return NodeSummary, the node summary object representing the node created successfully, null otherwise
	 * @exception java.sql.SQLException
	 */
	
	public NodeSummary createNode(PCSession session, String sNodeID, int nType, String sXNodeType,
			String sImportedID, String sOriginalID, int nPermission, int nState, String sAuthor, String sLabel, String sDetail,
			java.util.Date dCreationDate, java.util.Date dModificationDate)
			throws SQLException {
	
		return createNode(session, sNodeID, nType, sXNodeType, "", sOriginalID, nPermission, nState, sAuthor, sLabel, //$NON-NLS-1$
				sDetail, dCreationDate, dModificationDate, "");		 //$NON-NLS-1$
	}
	
	/**
	 *	Adds a new node to the database and returns it if successful
	 *
	 * @param session, the session object for the database to use.
	 * @param String sNodeID, the id of the node being created.
	 * @param int nType, the integer type of the node being created.
	 * @param String sXNodeType, the extended node type of the node being created.
	 * @param sImportedID, the imported ID if relevant else empty String.
	 * @param sOriginalID, the original ID if relevant else empty String.
	 * @param int nPermission, the permission level of the node being created.
	 * @param int nState, the inital state of the node being created
	 * @param String sAuthor, the author of this node
	 * @param String sLabel, the label of this node
	 * @param String sDetail, the primary details (page 1 of any details), for this node
	 * @param Date dCreationDate, the creation date of this node
	 * @param Date dMmodificationDate, the modification date of this node (same as creation date).
	 * @param sLastModAuthor the author who created this node
	 * @return NodeSummary, the node summary object representing the node created successfully, null otherwise
	 * @exception java.sql.SQLException
	 */
	
	public NodeSummary createNode(PCSession session, String sNodeID, int nType, String sXNodeType,
			String sImportedID, String sOriginalID, int nPermission, int nState, String sAuthor, String sLabel, String sDetail,
			java.util.Date dCreationDate, java.util.Date dModificationDate, String sLastModAuthor)
			throws SQLException {

		DBConnection dbcon = getDatabaseManager().requestConnection(session.getModelName());

		NodeSummary node = null;
		if (sDetail == null)
			sDetail = ""; //$NON-NLS-1$

		node = DBNode.insert(dbcon, sNodeID, nType, sXNodeType, sImportedID, sOriginalID, sAuthor, sLabel, 
			sDetail, dCreationDate, dModificationDate, session.getUserID(), sLastModAuthor);

		getDatabaseManager().releaseConnection(session.getModelName(),dbcon);
		return node;
	}

	/**
	 * Deletes a node from a specific View in the database and returns true if successful
	 *
	 * @param oSession the session object for the database to use.
	 * @param oNode the id of the node to mark for deletion.
	 * @param sViewID the view the node should be deleted from.
	 * @param vtUsers a list of all UserProfile objects so we don't delete their HomeView, inbox etc.
	 * @return boolean true if the node was successfully marked for deletion, else false.
	 * @exception java.sql.SQLException
	 */
	public boolean deleteNode(PCSession oSession, NodeSummary oNode, String sViewID, Vector vtUsers) throws SQLException {

		DBConnection dbcon = getDatabaseManager().requestConnection(oSession.getModelName()) ;

		boolean deleted = DBNode.delete(dbcon, oNode, sViewID, oSession.getUserID(), vtUsers);

		getDatabaseManager().releaseConnection(oSession.getModelName(),dbcon);

		return deleted;
	}

	/**
	 * Restores a node in the database and returns true if successful
	 *
	 * @param PCSession session, the session object for the database to use.
	 * @param NodeSummary oNode, the id of the node to restore.
	 * @return boolean, true if the node was successfully restored, else false.
	 * @exception java.sql.SQLException
	 */
	public boolean restoreNode(PCSession session, String sNodeID) throws SQLException {

		DBConnection dbcon = getDatabaseManager().requestConnection(session.getModelName());

		boolean restored = DBNode.restore(dbcon, sNodeID, session.getUserID());

		getDatabaseManager().releaseConnection(session.getModelName(),dbcon);

		return restored;
	}

	/**
	 * Returns if the node with the given node id has been marked for deletion
	 *
	 * @param PCSession session, the session object for the database to use.
	 * @param NodeSummary oNode, the id of the node to check.
	 * @return boolean, true if the node is marked for deletion, else false.
	 * @exception java.sql.SQLException
	 */
	public boolean isMarkedForDeletion(PCSession session, String sNodeID) throws SQLException {

		DBConnection dbcon = getDatabaseManager().requestConnection(session.getModelName());

		boolean deleted = DBNode.isMarkedForDeletion(dbcon, sNodeID);

		getDatabaseManager().releaseConnection(session.getModelName(),dbcon);

		return deleted;
	}

	/**
	 * Returns if the node with the given node id exists
	 *
	 * @param PCSession session, the session object for the database to use.
	 * @param NodeSummary oNode, the id of the node to check.
	 * @return boolean, true if the node with the given id exists, else false.
	 * @exception java.sql.SQLException
	 */
	public boolean doesNodeExist(PCSession session, String sNodeID) throws SQLException {

		DBConnection dbcon = getDatabaseManager().requestConnection(session.getModelName());

		boolean exists = DBNode.doesNodeExist(dbcon, sNodeID);

		getDatabaseManager().releaseConnection(session.getModelName(),dbcon);

		return exists;
	}

	/**
	 * Purges a ViewNode record from the database and returns true if successful
	 *
	 * @param PCSession session, the session object for the database to use.
	 * @param String sNodeID, the node id of the node to purge in the given view.
	 * @param String sViewID, the view in which to purge the given node.
	 * @return boolean, true if the node was succesfully purged from the given view, else false.
	 * @exception java.sql.SQLException
	 */
	public boolean purgeViewNode(PCSession session, String sViewID, String sNodeID) throws SQLException {

		DBConnection dbcon = getDatabaseManager().requestConnection(session.getModelName());

		boolean deleted = DBViewNode.purge(dbcon, sViewID, sNodeID, session.getUserID());

		getDatabaseManager().releaseConnection(session.getModelName(),dbcon);

		return deleted;
	}

	/**
	 * Purges a home view node from the database and returns true if successful
	 *
	 * @param PCSession session, the session object for the database to use.
	 * @param String sNodeID, the node id of home view to purge.
	 * @return boolean, true if the node was succesfully purged, else false.
	 * @exception java.sql.SQLException
	 */
	public boolean purgeHomeView(PCSession session, String sNodeID) throws SQLException {

		DBConnection dbcon = getDatabaseManager().requestConnection(session.getModelName());

		boolean	deleted = DBNode.purgeHomeView(dbcon, sNodeID, session.getUserID());

		getDatabaseManager().releaseConnection(session.getModelName(),dbcon);

		return deleted;
	}

	/**
	 * Purges a node from the database and returns true if successful
	 *
	 * @param PCSession session, the session object for the database to use.
	 * @param String sNodeID, the node id of home view to purge.
	 * @return boolean, true if the node was succesfully purged, else false.
	 * @exception java.sql.SQLException
	 */
	public boolean purgeNode(PCSession session, String sNodeID) throws SQLException {

		DBConnection dbcon = getDatabaseManager().requestConnection(session.getModelName());

		boolean deleted = DBNode.purge(dbcon, sNodeID, session.getUserID());

		getDatabaseManager().releaseConnection(session.getModelName(),dbcon);

		return deleted;
	}

	/**
	 * Purges ViewNode entries for the given view, from the database and returns true if successful
	 *
	 * @param PCSession session, the session object for the database to use.
	 * @param String sViewID, the view id of view to purge ViewNode entries for.
	 * @return boolean, true if the ViewNode table was succesfully purged for the given view, else false.
	 * @exception java.sql.SQLException
	 */
	public boolean purgeView(PCSession session, String sViewID) throws SQLException {

		DBConnection dbcon = getDatabaseManager().requestConnection(session.getModelName());

		boolean	deleted = DBViewNode.purgeView(dbcon, sViewID, session.getUserID());

		getDatabaseManager().releaseConnection(session.getModelName(),dbcon);

		return deleted;
	}

	/**
	 * Purges all nodes marked for deletion with the author as the given author
	 * from the database and returns true if successful
	 *
	 * @param PCSession session, the session object for the database to use.
	 * @param String sAuthor, the author whose nodes to purge.
	 * @return boolean, true if the author's nodes were succesfully purged, else false.
	 * @exception java.sql.SQLException
	 */
	public boolean purgeAllNodes(PCSession session, String sAuthor) throws SQLException {

		DBConnection dbcon = getDatabaseManager().requestConnection(session.getModelName());

		boolean deleted = DBNode.purgeAll(dbcon, sAuthor, session.getUserID());

		getDatabaseManager().releaseConnection(session.getModelName(),dbcon);

		return deleted;
	}

	/**
	 * Deletes a view from the database and returns true if successful
	 *
	 * @param PCSession session, the session object for the database to use.
	 * @param String sViewID, the view id of view to purge ViewNode entries for.
	 * @param vtUsers a list of all UserProfile objects so we don't delete their HomeView, inbox etc.	 * 
	 * @return boolean, true if the ViewNode table was succesfully purged for the given view, else false.
	 * @exception java.sql.SQLException
	 */
	public boolean deleteView(PCSession session, String sViewID, Vector vtUsers) throws SQLException {

		DBConnection dbcon = getDatabaseManager().requestConnection(session.getModelName());

		boolean deleted = DBViewNode.deleteView(dbcon, sViewID, session.getUserID(), vtUsers);

		getDatabaseManager().releaseConnection(session.getModelName(),dbcon);

		return deleted;
	}

	/**
	 * Restores all ViewNode and Node records for the given View id and returns true if successful
	 *
	 * @param PCSession session, the session object for the database to use.
	 * @param String sViewID, the view id of view to restore all the ViewNode entries for.
	 * @return boolean, true if the ViewNode entries were succesfully restored for the given view, else false.
	 * @exception java.sql.SQLException
	 */
	public boolean restoreView(PCSession session, String sViewID) throws SQLException {

		DBConnection dbcon = getDatabaseManager().requestConnection(session.getModelName());

		boolean restored = DBViewNode.restoreView(dbcon, sViewID, session.getUserID());

		getDatabaseManager().releaseConnection(session.getModelName(),dbcon);

		return restored;
	}

	/**
	 * Restores a node from the database to the given view and returns true if successful
	 *
	 * @param PCSession session, the session object for the database to use.
	 * @param String sNodeID, the node id of node to restore.
	 * @param String sViewID, the view id of view in which to restore the node.
	 * @return NodePosition, the restored Node or null.
	 * @exception java.sql.SQLException
	 */
	public NodePosition restoreNodeView(PCSession session, String sNodeID, String sViewID) throws SQLException {

		DBConnection dbcon = getDatabaseManager().requestConnection(session.getModelName()) ;

		NodePosition pos = DBViewNode.restore(dbcon, sViewID, sNodeID, session.getUserID());

		getDatabaseManager().releaseConnection(session.getModelName(),dbcon);

		return pos;
	}

	/**
	 * Returns an Enumeration of all nodes for the given view ID.
	 *
	 * @param PCSession session, the session object for the database to use.
	 * @param String viewID, the id of the view 
	 * @return Enumeration, of all active nodes in given view ID (ones not marked for deletion).
	 * @exception java.sql.SQLException
	 * @author Lakshmi Prabhakaran
	 * @date 1/31/06
	 */
	public Enumeration getChildNodes(PCSession session, String viewID) throws SQLException {

		DBConnection dbcon = getDatabaseManager().requestConnection(session.getModelName()) ;

		Enumeration nodes = DBNode.getChildNodes(dbcon, viewID, session.getUserID());

		getDatabaseManager().releaseConnection(session.getModelName(),dbcon);

		return nodes;
	}
	
	/**
	 * Returns an Vector of all nodes for the given view ID.
	 *
	 * @param PCSession session, the session object for the database to use.
	 * @param String viewID, the id of the view 
	 * @return Vector, of all active nodes in given view ID (ones not marked for deletion).
	 * @exception java.sql.SQLException
	 * @author Lakshmi Prabhakaran
	 * @date 1/31/06
	 */
	public Vector getChildViews (PCSession session, String viewID) throws SQLException {

		DBConnection dbcon = getDatabaseManager().requestConnection(session.getModelName()) ;
		Vector nodes = new Vector();
		nodes = DBNode.getChildViews(dbcon, viewID, session.getUserID(), nodes);

		getDatabaseManager().releaseConnection(session.getModelName(),dbcon);

		return nodes;
	}
	
	/**
	 * Returns an Enumeration of all view nodes to full depth for the given view ID.
	 *
	 * @param PCSession session, the session object for the database to use.
	 * @param String viewID, the id of the view 
	 * @return Vector, of all active nodes in given view ID (ones not marked for deletion).
	 * @exception java.sql.SQLException
	 * @author Lakshmi Prabhakaran
	 * @date 10/19/06
	 */
	public Vector getAllChildViews(PCSession session, String viewID) throws SQLException {

		DBConnection dbcon = getDatabaseManager().requestConnection(session.getModelName()) ;

		Vector nodes = DBNode.getAllChildViews(dbcon, viewID, session.getUserID());

		getDatabaseManager().releaseConnection(session.getModelName(),dbcon);

		return nodes;
	}


	/**
	 * Returns deleted ViewNode entries for the given node id from the database
	 *
	 * @param PCSession session, the session object for the database to use.
	 * @param String sNodeID, the node id of node to return the deleted ViewNode entries for.
	 * @return Vector, a list of the NodePosition objects for the ViewNode entries requested.
	 * @exception java.sql.SQLException
	 */
	public Vector getDeletedViews(PCSession session, String sNodeID) throws SQLException {

		DBConnection dbcon = getDatabaseManager().requestConnection(session.getModelName()) ;

		Vector vtNodes = DBViewNode.getDeletedViews(dbcon, sNodeID, session.getUserID());

		getDatabaseManager().releaseConnection(session.getModelName(),dbcon);

		return vtNodes;
	}


	/**
	 * Returns count of number of active views the given node has been deleted from
	 *
	 * @param PCSession session, the session object for the database to use.
	 * @param String sNodeID, the node id of node to return the deleted ViewNode entries count for.
	 * @return int, a count of the deleted ViewNode entries for the given node id.
	 * @exception java.sql.SQLException
	 */
	public int getDeletedViewCount(PCSession session, String sNodeID) throws SQLException {

		DBConnection dbcon = getDatabaseManager().requestConnection(session.getModelName()) ;

		int count = DBViewNode.getDeletedViewCount(dbcon, sNodeID);

		getDatabaseManager().releaseConnection(session.getModelName(),dbcon);

		return count;
	}

	/**
	 * Returns a node given its ID
	 *
	 * @param PCSession session, the session object for the database to use.
	 * @param String sNodeID, the node id of node to return.
	 * @return NodeSummary, the NodeSummary object for the given node id.
	 * @exception java.sql.SQLException
	 */
	public NodeSummary getNodeSummary(PCSession session, String sNodeID) throws SQLException {

		DBConnection dbcon = getDatabaseManager().requestConnection(session.getModelName()) ;

		NodeSummary node = DBNode.getNodeSummary(dbcon, sNodeID, session.getUserID());

		getDatabaseManager().releaseConnection(session.getModelName(),dbcon);

		return node;
	}

	/**
	 * Returns a View given its ID
	 *
	 * @param PCSession session, the session object for the database to use.
	 * @param String sViewID, the view id of view to return.
	 * @return IView, the IView object for the given view id.
	 * @exception java.sql.SQLException
	 */
	public IView getView(PCSession session, String sViewID) throws SQLException {

		DBConnection dbcon = getDatabaseManager().requestConnection(session.getModelName()) ;

		IView view = DBNode.getView(dbcon, sViewID, session.getUserID());

		getDatabaseManager().releaseConnection(session.getModelName(),dbcon);

		return view;
	}

	/**
	 * Returns a vector of parent views for the given node id.
	 *
	 * @param PCSession session, the session object for the database to use.
	 * @param String sNodeID, the node id of node to return the parent view for.
	 * @return Vector, of parent Views for the node with the given node id.
	 * @exception java.sql.SQLException
	 */
	public Vector getViews(PCSession session, String sNodeID) throws SQLException {

		DBConnection dbcon = getDatabaseManager().requestConnection(session.getModelName()) ;

		Vector vtViews = new Vector(51);
		int i =0;

		Enumeration views = DBViewNode.getViews(dbcon, sNodeID, session.getUserID());
		for(Enumeration e = views;e.hasMoreElements();) {
			View view = (View)e.nextElement();
			vtViews.addElement(view);
			i++;
		}

		getDatabaseManager().releaseConnection(session.getModelName(),dbcon);

		return vtViews;
	}

	/**
	 * Returns an Enumeration of all active views in the database.
	 *
	 * @param PCSession session, the session object for the database to use.
	 * @return Enumeration, of all active Views (ones not marked for deletion).
	 * @exception java.sql.SQLException
	 */
	public Enumeration getAllActiveViews(PCSession session) throws SQLException {

		DBConnection dbcon = getDatabaseManager().requestConnection(session.getModelName()) ;

		Enumeration views = DBNode.getAllViews(dbcon, session.getUserID());

		getDatabaseManager().releaseConnection(session.getModelName(),dbcon);

		return views;
	}

	/**
	 * Returns the node marked for deletion with the given node id
	 *
	 * @param PCSession session, the session object for the database to use.
	 * @param String sNodeID, the node id of node to return.
	 * @return NodeSummary, of node marked for deletion for the given node id or null.
	 * @exception java.sql.SQLException
	 */
	public NodeSummary getDeletedNodeSummaryId(PCSession session, String sNodeID) throws SQLException {

		DBConnection dbcon = getDatabaseManager().requestConnection(session.getModelName()) ;

		NodeSummary node = DBNode.getDeletedNodeSummaryId(dbcon, sNodeID, session.getUserID());

		getDatabaseManager().releaseConnection(session.getModelName(),dbcon);

		return node;
	}

	/**
	 * Returns all deleted nodes from the database.
	 *
	 * @param PCSession session, the session object for the database to use.
	 * @return Vector, of NodeSummary objects which have been marked for deletion.
	 * @exception java.sql.SQLException
	 */
	public Vector getDeletedNodeSummary(PCSession session) throws SQLException {

		DBConnection dbcon = getDatabaseManager().requestConnection(session.getModelName()) ;

		Vector vtNodes = DBNode.getDeletedNodeSummary(dbcon, session.getUserID());

		getDatabaseManager().releaseConnection(session.getModelName(),dbcon);

		return vtNodes;
	}
	
	/**
	 * Returns a count of all deleted nodes from the database.
	 *
	 * @param PCSession session, the session object for the database to use.
	 * @return Vector, of NodeSummary objects which have been marked for deletion.
	 * @exception java.sql.SQLException
	 */
	public int iGetDeletedNodeCount(PCSession session) throws SQLException {

		DBConnection dbcon = getDatabaseManager().requestConnection(session.getModelName()) ;

		int iCount = DBNode.iGetDeletedNodeCount(dbcon);

		getDatabaseManager().releaseConnection(session.getModelName(),dbcon);

		return iCount;
	}

	/**
	 * Returns a Vector of nodes given a keyword
	 *
	 * @param PCSession session, the session object for the database to use.
	 * @param String sKeyword, the key word to search for in the node label or detail fields.
	 * @return Vector, of NodeSummary objects with the given keyword in thier label or detail fields.
	 * @exception java.sql.SQLException
	 */
	public Vector searchNode(PCSession session, String sKeyword) throws SQLException {

		DBConnection dbcon = getDatabaseManager().requestConnection(session.getModelName()) ;

		Vector vtNodes = new Vector(51);
		NodeSummary node = null ;
		for(Enumeration e = DBNode.searchNode(dbcon, sKeyword, session.getUserID()); e.hasMoreElements();) {
			node = (NodeSummary)e.nextElement();
			vtNodes.addElement(node);
		}

		getDatabaseManager().releaseConnection(session.getModelName(),dbcon);

		return vtNodes;
	}

	/**
	 * Sets the value for original imported Id for this node id.
	 *
	 * @param session the session object for the database to use.
	 * @param sNodeID the node id of node whose origianl id to set.
	 * @param oldValue the original original id of the node.
	 * @param newValue the new original id of the node.
	 * @param dModificationDate the date this node is being modified.
	 * @param sLastModAuthor the author name of the person who made this modification. 
	 * 
	 * @exception java.sql.SQLException
	 */
	public void setOriginalID(PCSession session, String sNodeID, String oldValue, 
			String newValue, java.util.Date dModificationDate, String sLastModAuthor) throws SQLException {
		if (oldValue == newValue)
			return;

		String modelName = session.getModelName();

		DBConnection dbcon = getDatabaseManager().requestConnection(session.getModelName());

		DBNode.setOriginalID(dbcon, sNodeID, newValue, dModificationDate, sLastModAuthor, session.getUserID());

		getDatabaseManager().releaseConnection(session.getModelName(),dbcon);
	}

	/**
	 *	Sets the value for the node type and fires and property change
	 *
	 * @param PCSession session, the session object for the database to use.
	 * @param String sKeyword, the key word to search for in the node label or detail fields.
	 * @param int oldValue, the old value for the node type.
	 * @param int newValue, the new value for the node type.
	 * @param java.util.Date modificationDate, the modification date of the type change.
	 * @param sLastModAuthor the author name of the person who made this modification. 
	 * 
	 * @exception java.sql.SQLException
	 */
	public void setType(PCSession session, String sNodeID, int oldValue, int newValue, 
			java.util.Date dModificationDate, String sLastModAuthor) throws SQLException {

		if (oldValue == newValue)
			return;

		String modelName = session.getModelName();

		DBConnection dbcon = getDatabaseManager().requestConnection(session.getModelName());

		boolean typeChanged = DBNode.setType(dbcon, sNodeID, oldValue, newValue, 
				dModificationDate, sLastModAuthor, session.getUserID());

		getDatabaseManager().releaseConnection(session.getModelName(),dbcon);
	}

	/**
	 * Sets the creation date for the node with the given node id.
	 *
	 * @param PCSession session, the session object for the database to use.
	 * @param String sNodeID, the id of the node to set the creation date for.
	 * @param java.util.Date dCreationDate, the creation date for the node.
	 * @param java.util.Date dModificationDate, the modification date for this change. 
	 * @param sLastModAuthor the author name of the person who made this modification. 
	 * 
	 * @return boolean, true if the creation date was successfuly set, else false.
	 * @exception java.sql.SQLException
	 */
	public boolean setCreationDate(PCSession session, String sNodeID, 
			java.util.Date dCreationDate, java.util.Date dModificationDate, 
			String sLastModAuthor) throws SQLException {

		String modelName = session.getModelName() ;

		DBConnection dbcon = getDatabaseManager().requestConnection(session.getModelName()) ;

		boolean	changed = DBNode.setCreationDate(dbcon, sNodeID, dCreationDate, 
				dModificationDate, sLastModAuthor, session.getUserID());

		getDatabaseManager().releaseConnection(session.getModelName(),dbcon);

		return changed;
	}
	
	/**
	 * Sets the author for the node
	 *
	 * @param PCSession session, the session object for the database to use.
	 * @param String sNodeID, the id of the node to set the author for.
	 * @param String sAuthor, the author to set for the node.
	 * @param java.util.Date dModificationDate, the modification date for the author change.
	 * @param sLastModAuthor the author name of the person who made this modification. 
	 * @return boolean, true if the author was successfuly set, else false.
	 * @exception java.sql.SQLException
	 */
	public boolean setAuthor(PCSession session, String sNodeID, String sAuthor, 
			java.util.Date modificationDate, String sLastModAuthor) throws SQLException {

		String modelName = session.getModelName() ;

		DBConnection dbcon = getDatabaseManager().requestConnection(session.getModelName()) ;

		boolean changed = DBNode.setAuthor(dbcon, sNodeID, sAuthor, modificationDate, sLastModAuthor, session.getUserID());

		getDatabaseManager().releaseConnection(session.getModelName(),dbcon);

		return changed;
	}

	/**
	 * Sets the label of this node.
	 *
	 * @param PCSession session, the current session object.
	 * @param String sNodeID, the id of the node whose label to change
	 * @param String sLabel, the label of this node.
	 * @param java.util.Date dModificationDate, the modification date for the label change.
	 * @param sLastModAuthor the author name of the person who made this modification. 
	 * 
	 * @exception java.sql.SQLException
	 */
	public void setLabel(PCSession session, String sNodeID, String sLabel, 
			java.util.Date dModificationDate, String sLastModAuthor) throws SQLException {

		String modelName = session.getModelName();

		DBConnection dbcon = getDatabaseManager().requestConnection(session.getModelName());

		boolean labelChanged = DBNode.setLabel(dbcon, sNodeID, sLabel, dModificationDate, 
				sLastModAuthor, session.getUserID());

		getDatabaseManager().releaseConnection(session.getModelName(),dbcon);
	}
	
	/**
	 * Associated the given Code with the node with the given node id.
	 *
	 * @param PCSession session, the current session object.
	 * @param String sNodeID, the id of the node whose label to change.
	 * @param Code code,  the code to be added.
	 * @return true if it was successfully added, else false.
	 * @exception java.sql.SQLException
	 */
	public boolean addCode(PCSession session, String sNodeID, Code code) throws SQLException {

		String modelName = session.getModelName() ;

		DBConnection dbcon = getDatabaseManager().requestConnection(session.getModelName()) ;

		boolean added = DBCodeNode.insert(dbcon, sNodeID, code.getId());

		getDatabaseManager().releaseConnection(session.getModelName(),dbcon);

		return added;
	}

	/**
	 * Removes the reference to the given codeId from the given nodeId.
	 *
	 * @param PCSession session, the current session object.
	 * @param String sNodeID, the id of the node whose code to remove.
	 * @param Code sCodeID, the code to be removed.
	 * @return true if it was successfully removed, else false.
	 * @exception java.util.NoSuchElementException
	 * @exception java.sql.SQLException
	 */
	public boolean removeCode(PCSession session, String sNodeID, String sCodeID) throws NoSuchElementException, SQLException {
		String modelName = session.getModelName();

		DBConnection dbcon = getDatabaseManager().requestConnection(modelName) ;

		boolean deleted = DBCodeNode.deleteNodeCode(dbcon, sNodeID, sCodeID);

		getDatabaseManager().releaseConnection(modelName,dbcon);

		return deleted;
	}

	/**
	 * Returns all the codes referenced by this node
	 *
	 * @param PCSession session, the current session object.
	 * @param String sNodeID, the id of the node whose codes to return.
	 * @return Vector, all the Codes referenced by this node.
	 * @exception java.sql.SQLException
	 */
	public Vector getCodes(PCSession session, String sNodeID) throws SQLException {

		String modelName = session.getModelName() ;

		DBConnection dbcon = getDatabaseManager().requestConnection(session.getModelName()) ;

		Vector vtCodes = DBCodeNode.getCodes(dbcon, sNodeID);

		getDatabaseManager().releaseConnection(session.getModelName(), dbcon);

		return vtCodes ;
	}

	/**
	 * Adds a new shortcut node.
	 *
	 * @param PCSession session, the current session object.
	 * @param String sNodeID, the id of the new shortcut node.
	 * @param String sReferenceID, the id of the node it references.
	 * @return true if it was successfully added, else false;
	 * @exception java.sql.SQLException
	 */
	public boolean addShortCutNode(PCSession session, String sNodeID, String sReferenceID) throws SQLException {

		String modelName = session.getModelName();

		DBConnection dbcon = getDatabaseManager().requestConnection(session.getModelName());

		boolean added = DBShortCutNode.insert(dbcon, sNodeID, sReferenceID);

		getDatabaseManager().releaseConnection(session.getModelName(),dbcon);

		return added;
	}

	/**
	 * Returns all the ShortCut Nodes referenced by this node
	 *
	 * @param PCSession session, the current session object.
	 * @param String sReferenceID, the id of the node to get the shortcuts for.
	 * @return Vector of all the shortcut nodes referenced by this node.
	 * @exception java.sql.SQLException
	 */
	public Vector getShortCutNodes(PCSession session, String sReferenceID) throws SQLException {

		String modelName = session.getModelName() ;

		DBConnection dbcon = getDatabaseManager().requestConnection(session.getModelName()) ;

		Vector vtShortCutNodes = new Vector(51);
		for(Enumeration e  = DBShortCutNode.getShortCutNodes(dbcon, sReferenceID, session.getUserID());e.hasMoreElements();) {
			NodeSummary node = (NodeSummary)e.nextElement();
			vtShortCutNodes.addElement(node);
		}

		getDatabaseManager().releaseConnection(session.getModelName(),dbcon);

		return vtShortCutNodes ;
	}

	/**
	 * Returns all the Nodes not assigned to any view.
	 *
	 * @param PCSession session, the current session object.
	 * @return Vector of all nodes not in a view.
	 * @exception java.sql.SQLException
	 */
	public Vector getLimboNodes(PCSession session) throws SQLException {

		String modelName = session.getModelName() ;

		DBConnection dbcon = getDatabaseManager().requestConnection(session.getModelName()) ;

		Vector vtLimboNodes  = DBNode.getLimboNodes(dbcon, session.getUserID());

		getDatabaseManager().releaseConnection(session.getModelName(),dbcon);

		return vtLimboNodes;
	}

	/**
	 * Returns ShortCut Node referenced by this node
	 *
	 * @param PCSession session, the current session object.
	 * @param String sNodeID, the id of the shortcut node to get.
	 * @return NodeSummary, the shortcut node with the given id.
	 * @exception java.sql.SQLException
	 */
	public NodeSummary getShortCutNode(PCSession session, String sNodeID) throws SQLException {

		String modelName = session.getModelName() ;

		DBConnection dbcon = getDatabaseManager().requestConnection(session.getModelName()) ;

		NodeSummary node = DBShortCutNode.getShortCutNode(dbcon, sNodeID, session.getUserID());

		getDatabaseManager().releaseConnection(session.getModelName(),dbcon);

		return node;
	}


 	// NODE DETAIL ROUTINES
	/**
	 * Sets the detailed description of this node (page 1 of the detail).
	 *
	 * @param PCSession session, the current session object.
	 * @param String sNodeID, the node id of the node to set the detail for.
	 * @param String oldValue, the old value of the node detail (page 1).
	 * @param String newValue, the new value of the node detail (page 1).
	 * @param String sAuthor, the author of the change.
	 * @param java.util.Date dModificationDate, the modification date for the change.
	 * @param sLastModAuthor the author name of the person who made this modification. 
	 * @exception java.sql.SQLException
	 */
	public void setDetail(PCSession session, String sNodeID, String oldValue, 
			String newValue, String sAuthor, java.util.Date dModificationDate, 
			String sLastModAuthor) throws SQLException {

		String modelName = session.getModelName() ;

		DBConnection dbcon = getDatabaseManager().requestConnection(session.getModelName()) ;

		boolean	labelChanged = DBNode.setDetail(dbcon, sNodeID, newValue, sAuthor, 
				dModificationDate, sLastModAuthor, session.getUserID());

		getDatabaseManager().releaseConnection(session.getModelName(),dbcon);
	}

	/**
	 * Sets a given page of the node detail.
	 *
	 * @param PCSession session, the current session object.
	 * @param String sAuthor, the author of the change.
	 * @param NodeDetailPage oDetail, the node detail page to set.
	 * @param java.util.Date dModificationDate, the modification date for the change.
	 * @param sLastModAuthor the author name of the person who made this modification. 
	 * @return boolean, inidcating whether the update was successful.
	 * @exception java.sql.SQLException
	 */
	public boolean setDetailPage(PCSession session, String sAuthor, NodeDetailPage oDetail, 
			java.util.Date dModificationDate, String sLastModAuthor) throws SQLException {

		String modelName = session.getModelName() ;

		DBConnection dbcon = getDatabaseManager().requestConnection(session.getModelName()) ;

		boolean detailChanged = DBNode.setDetailPage(dbcon, sAuthor, oDetail, 
				dModificationDate, sLastModAuthor, session.getUserID());

		getDatabaseManager().releaseConnection(session.getModelName(),dbcon);

		return detailChanged;
	}

	/**
	 * Gets a specific page of the detailed description of this node
	 *
	 * @param PCSession session, the current session object.
	 * @param String sNodeID, the node id of the node whose page to get
	 * @param int nPageNo, the page of detail to get for the node.
	 * @return NdeoDetailPage, the page or detail for the request node and page number.
	 * @exception java.sql.SQLException
	 */
	public NodeDetailPage getDetailPage(PCSession session, String sNodeID, int nPageNo) throws SQLException {

		String modelName = session.getModelName() ;

		DBConnection dbcon = getDatabaseManager().requestConnection(session.getModelName()) ;

		NodeDetailPage detail = DBNode.getDetailPage(dbcon, sNodeID, nPageNo);

		getDatabaseManager().releaseConnection(session.getModelName(),dbcon);

		return detail;
	}

	/**
	 * Gets all the details pages of this node
	 *
	 * @param PCSession session, the current session object.
	 * @param String sNodeID, the node id of the node whose detail pages to get.
	 * @return Vector, of all the NodeDetailPage objects for the given node id.
	 * @exception java.sql.SQLException
	 */
	public Vector getAllDetailPages(PCSession session, String sNodeID) throws SQLException {

		String modelName = session.getModelName();

		DBConnection dbcon = getDatabaseManager().requestConnection(session.getModelName()) ;

		Vector details = DBNode.getAllDetailPages(dbcon, sNodeID);

		getDatabaseManager().releaseConnection(session.getModelName(),dbcon);

		return details;
	}

	/**
	 * Sets all the pages of the detailed description of this node
	 *
	 * @param PCSession session, the current session object.
	 * @param String sNodeID, the node id of the node whose detail pages to set.
	 * @param Vector oldDetails, a vector of all the current detail pages.
	 * @param Vector newDetails, a vector of the new details pages for the given nodeid.
	 * @param java.util.Date dModificationDate, the modification date for the change.
	 * @param sLastModAuthor the author name of the person who made this modification. 
	 * @return boolean inidcating whether the update was successful.
	 * @exception java.sql.SQLException
	 */
	public boolean setAllDetailPages(PCSession session, String sNodeID, String sAuthor, 
			Vector oldDetails, Vector newDetails, java.util.Date dModificationDate, String sLastModAuthor) throws SQLException {

		String modelName = session.getModelName() ;

		DBConnection dbcon = getDatabaseManager().requestConnection(session.getModelName()) ;

		boolean wasUpdated = DBNode.setAllDetailPages(dbcon, sNodeID, sAuthor, oldDetails, 
				newDetails, dModificationDate, sLastModAuthor, session.getUserID());

		getDatabaseManager().releaseConnection(session.getModelName(),dbcon);

		return wasUpdated;
	}


//METHODS FOR REFERENCE NODE DETAIL

	/**
	 * Sets the Reference Node Source Path
	 *
	 * @param session the current session object.
	 * @param sNodeID the node id of the node whose reference and image to set.
	 * @param sPath the path of the external reference.
	 * @param sImage the path of the external image.
	 * @param dModificationDate the modification date for the change.
	 * @param sLastModAuthor the author name of the person who made this modification. 
	 * @exception java.sql.SQLException
	 */
	public boolean setReference(PCSession session, String sNodeID, String sPath, 
			String sImage, java.util.Date dModificationDate, String sLastModAuthor) throws SQLException {

		String modelName = session.getModelName() ;
		DBConnection dbcon = getDatabaseManager().requestConnection(modelName) ;

		boolean pathAdded = DBReferenceNode.setReference(dbcon, sNodeID, sPath, sImage, dModificationDate, 
																sLastModAuthor, session.getUserID());

		getDatabaseManager().releaseConnection(modelName,dbcon);

		return pathAdded;
	}

	/**
	 * Sets the Reference Node Source Path
	 *
	 * @param session the current session object.
	 * @param sNodeID the node id of the node whose reference and image to set.
	 * @param sPath the path of the external reference.
	 * @param sImage the path of the external image.
	 * @param oImageSize the size to draw the image. 
	 * @param dModificationDate the modification date for the change.
	 * @param sLastModAuthor the author name of the person who made this modification. 
	 * @exception java.sql.SQLException
	 */
	public boolean setReference(PCSession session, String sNodeID, String sPath, 
			String sImage, Dimension oImageSize, java.util.Date dModificationDate, String sLastModAuthor) throws SQLException {

		String modelName = session.getModelName();
		DBConnection dbcon = getDatabaseManager().requestConnection(modelName);

		boolean pathAdded = DBReferenceNode.setReference(dbcon, sNodeID, sPath, sImage, oImageSize, dModificationDate, 
																sLastModAuthor, session.getUserID());

		getDatabaseManager().releaseConnection(modelName,dbcon);

		return pathAdded;
	}

	/**
	 * Gets the Reference Node Source Path
	 *
	 * @param PCSession session, the current session object.
	 * @param String sNodeID, the node id of the node whose reference to get.
	 * @return String, the external reference for this node, or an empty string.
	 * @exception java.sql.SQLException
	 */
	public String getReference(PCSession session, String sNodeID) throws SQLException {
		String modelName = session.getModelName();
		DBConnection dbcon = getDatabaseManager().requestConnection(modelName);

		String path = path = DBReferenceNode.getReference(dbcon, sNodeID);

		getDatabaseManager().releaseConnection(modelName ,dbcon);

		return path;
	}

	/**
	 * Sets the image width and height.
	 *
	 * @param session the current session object.
	 * @param sNodeID the id of the node whose image size to change.
	 * @param oSize the new size for the image.
	 * @param dModificationDate, the modification date for the change.
	 * @param sLastModAuthor the author name of the person who made this modification. 
	 * @exception java.sql.SQLException
	 */
	public boolean setImageSize(PCSession session, String sNodeID, Dimension oSize,	
			java.util.Date dModificationDate, String sLastModAuthor) throws SQLException {

		String modelName = session.getModelName();
		DBConnection dbcon = getDatabaseManager().requestConnection(modelName) ;

		boolean pathAdded = DBReferenceNode.setImageSize(dbcon, sNodeID, oSize, dModificationDate, sLastModAuthor, session.getUserID());

		getDatabaseManager().releaseConnection(modelName,dbcon);

		return pathAdded;
	}
	
	/**
	 * Gets the image widht and height for the given node.
	 *
	 * @param session the current session object.
	 * @param sNodeID the id of the node whose image size to change.
	 * @exception java.sql.SQLException
	 */
	public Dimension getImageSize(PCSession session, String sNodeID) throws SQLException {
		String modelName = session.getModelName();
		DBConnection dbcon = getDatabaseManager().requestConnection(modelName) ;

		Dimension dim = DBReferenceNode.getImageSize(dbcon, sNodeID);

		getDatabaseManager().releaseConnection(modelName,dbcon);

		return dim;
	}	
	
	/**
	 * Gets the Reference Node Image
	 *
	 * @param PCSession session, the current session object.
	 * @param String sNodeID, the node id of the node whose image to get.
	 * @return String, the image for this node, or an empty string.
	 * @exception java.sql.SQLException
	 */
	public String getImage(PCSession session, String sNodeID) throws SQLException {
		String modelName = session.getModelName();
		DBConnection dbcon = getDatabaseManager().requestConnection(modelName) ;

		String image = DBReferenceNode.getImage(dbcon, sNodeID);

		getDatabaseManager().releaseConnection(modelName,dbcon);

		return image;
	}

	/**
	 * Gets a list of all the references and images in the database
	 *
	 * @param PCSession session, the current session object.
	 * @return Hashtable of all external references in the database.
	 * @exception java.sql.SQLException
	 */
	public Hashtable<String,Integer> getAllSources(PCSession session) throws SQLException {
		String modelName = session.getModelName();
		DBConnection dbcon = getDatabaseManager().requestConnection(modelName) ;

		Hashtable<String,Integer> sources = DBReferenceNode.getAllSources(dbcon);

		getDatabaseManager().releaseConnection(modelName,dbcon);

		return sources;
	}

	/**
	 * Gets a unique list of all the references and images in the database
	 *
	 * @param PCSession session, the current session object.
	 * @return Vector of all external references in the database.
	 * @exception java.sql.SQLException
	 */
	public Vector getAllSourcesUnique(PCSession session) throws SQLException {
		String modelName = session.getModelName();
		DBConnection dbcon = getDatabaseManager().requestConnection(modelName) ;

		Vector sources = DBReferenceNode.getAllSourcesUnique(dbcon);

		getDatabaseManager().releaseConnection(modelName,dbcon);

		return sources;
	}

	/**
	 *  Gets a unique list of all the references (files only) and images in the database and a count of their use.
	 *
	 * @param PCSession session, the current session object.
	 * @param source the source to return the nodes for.
	 * @return a unique list of the node using the given source.
	 * @throws java.sql.SQLException
	 */
	public Vector<NodeSummary> getNodesForSource(PCSession session, String source, String sUserID) throws SQLException {
		String modelName = session.getModelName();
		DBConnection dbcon = getDatabaseManager().requestConnection(modelName) ;

		Vector sources = DBReferenceNode.getNodesForSource(dbcon, source, sUserID);

		getDatabaseManager().releaseConnection(modelName,dbcon);

		return sources;
	}
	
	/**
	 * 
	 * Returns the Node state.
	 *
	 * @param PCSession session, the session object for the database to use.
	 * @param String sNodeID, the node id of the node to get the state for.
	 * @return int, the current state of the node. read - 1 unread - 0 modified - 2
	 * @exception java.sql.SQLException
	 */
	
	// Lakshmi (4/20/06)
	public int getState(PCSession session, String nodeID) throws SQLException {

		int state = -1;
		String modelName = session.getModelName();
		DBConnection dbcon = getDatabaseManager().requestConnection(modelName) ;

		state = DBNodeUserState.get(dbcon, nodeID, modelName);
		
		getDatabaseManager().releaseConnection(modelName,dbcon);		
		return state;
	}

	/**
	 * Sets a change of state of the node.
	 *
	 * @param PCSession session, the session object for the database to use.
	 * @param String sNodeID, the node id of the node to set the state for.
	 * @param int oldValue, the old value for the state.
	 * @param int newValue, the new value for the state.
	 * @param java.util.Date modificationDate, the modification date of the state change.
	 * @exception java.sql.SQLException
	 */
	
	// Lakshmi (4/20/06)
	public void setState(PCSession session, String sNodeID, int oldValue, int newValue, 
			java.util.Date dModificationDate) throws SQLException {
		
		String modelName = session.getModelName();		
		DBConnection dbcon = getDatabaseManager().requestConnection(modelName) ;
		
		// update db, this is an update to the history table here
		DBNodeUserState.updateUser(dbcon, sNodeID, session.getUserID(), oldValue, newValue);
		
		getDatabaseManager().releaseConnection(modelName,dbcon);		
	}

// METHODS NOT COMPLETED

	/**
	 * CURRENTLY NOT IMPLEMENTED.
	 * <p>
	 *  
	 * Gets the last modification author of this node.
	 *
	 * @param session the current session object.
	 * @param sNodeID the id of the node whose label to change
	 * @exception java.sql.SQLException
	 */
	public String getLastModificationAuthor(PCSession session, String sNodeID) throws SQLException {
		//String modelName = session.getModelName() ;
		// get from db and return		
		return ""; //$NON-NLS-1$
	}

	
	/**
	 * CURRENTLY NOT IMPLEMENTED.
	 * <p>
	 * Returns the label value for the requested nodeId
	 *
	 * @param PCSession session, the session object for the database to use.
	 * @param String sNodeID, the node id of the node to get the label for.
	 * @return String, the label of the node.
	 * @exception java.sql.SQLException
	 */
	public String getLabel(PCSession session, String sNodeID) throws SQLException {
		//String modelName = session.getModelName() ;
		// get from db and return
		return ""; //$NON-NLS-1$
	}

	/**
	 * CURRENTLY NOT IMPLEMENTED.
	 * <p>
	 * Returns the extended node type name for the given node id.
	 *
	 * @param PCSession session, the session object for the database to use.
	 * @param String sNodeID, the node id of node to return the extended node type for.
	 * @return String, the extended node type for the given node id.
	 * @exception java.sql.SQLException
	 */
	public String getExtendedNodeType(PCSession session, String sNodeID) throws SQLException {
		//String modelName = session.getModelName() ;
		//get from db and return
		return ""; //$NON-NLS-1$
	}

	/**
	 * CURRENTLY NOT IMPLEMENTED.
	 * <p>
	 * Sets the value for the extended node type and fires and property change
	 *
	 * @param PCSession session, the session object for the database to use.
	 * @param String sNodeID, the node id of the node to set the extended type for.
	 * @param int oldValue, the old value for the extended node type.
	 * @param int newValue, the new value for the extended node type.
	 * @param java.util.Date modificationDate, the modification date of the extended node type change.
	 * @param sLastModAuthor the author name of the person who made this modification. 
	 * @exception java.sql.SQLException
	 */
	public void setExtendedNodeType(PCSession session, String sNodeID, String oldValue, 
			String newValue, java.util.Date dModificationDate, String sLastModAuthor) throws SQLException {
		//String modelName = session.getModelName() ;
		//update db
	}

	/**
	 * CURRENTLY NOT IMPLEMENTED.<br>
	 * Returns the original imported id for this node id.
	 *
	 * @param PCSession session, the session object for the database to use.
	 * @param String sNodeID, the node id of node whose origianl id to return.
	 * @return String, the original id of the node.
	 * @exception java.sql.SQLException
	 */
	public String getOriginalID(PCSession session, String sNodeID) throws SQLException {

		//String modelName = session.getModelName() ;
		//get from db and return
		return new String(""); //$NON-NLS-1$
	}

	/**
	 * CURRENTLY NOT IMPLEMENTED.<br>
	 * Returns the node type for the requested node id.
	 *
	 * @param PCSession session, the session object for the database to use.
	 * @param String sNodeID, the node id of node whose type to return.
	 * @return int, the type of the node.
	 * @exception java.sql.SQLException
	 */
	public int getType(PCSession session, String sNodeID) throws SQLException {
		//String modelName = session.getModelName() ;
		//get from db and return
		return -1;
	}
	
	/**
	 * 
	 * @param session
	 * @param nodeID
	 * @return
	 * @throws SQLException
	 */
	//Lakshmi (4/19/06)
//	public Vector getReaders(PCSession session, String nodeID) throws SQLException {
//
//		DBConnection dbcon = getDatabaseManager().requestConnection(session.getModelName()) ;
//		Vector users = new Vector(); 
//		
//		int state = ICoreConstants.READSTATE;
//
//		Vector nodes = DBNodeUserState.getUserIDs(dbcon, nodeID, state);
//
//		for(int i = 0; i < nodes.size(); i ++){
//			String userID = (String)(nodes.get(i));
//			users.add(DBUser.getUserNameFromID(dbcon, userID));
//		}
//		getDatabaseManager().releaseConnection(session.getModelName(),dbcon);
//
//		return users;
//	}
	
	/**
	 * 
	 * @param session
	 * @param nodeID
	 * @return
	 * @throws SQLException
	 */
	//Lakshmi (4/19/06)
	public Vector getReaderIDs(PCSession session, String nodeID) throws SQLException {

		DBConnection dbcon = getDatabaseManager().requestConnection(session.getModelName()) ;
		
// Changed this so that readers returns people who have seen the node, even if the node has 
// been modified since they last saw it (per J. Conklin)		
//		int state = ICoreConstants.READSTATE;
//		Vector userIDs = DBNodeUserState.getUserIDs(dbcon, nodeID, state);

		Vector userIDs = DBNodeUserState.getReaderIDs(dbcon, nodeID);
		
		getDatabaseManager().releaseConnection(session.getModelName(),dbcon);

		return userIDs;
	}
	
	/**
	 * Counts the number of nodes in the Node table (mlb 11/07)
	 *
	 * @param PCSession session, the current session object.
	 * @return long - the number of nodes in the Node table.
	 * @exception java.sql.SQLException
	 */
	public long lGetNodeCount(PCSession session) throws SQLException {

		long lNodeCount = 0;
		String modelName = session.getModelName() ;
		DBConnection dbcon = getDatabaseManager().requestConnection(session.getModelName()) ;

		lNodeCount = DBNode.lGetNodeCount(dbcon);

		getDatabaseManager().releaseConnection(session.getModelName(),dbcon);

		return lNodeCount;
	}
	
	/**
	 * Counts the number of nodes in the View table (mlb 11/07)
	 *
	 * @param PCSession session, the current session object.
	 * @return long - the number of nodes in the Node table.
	 * @exception java.sql.SQLException
	 */
	public long lGetViewCount(PCSession session) throws SQLException {

		long lViewCount = 0;
		String modelName = session.getModelName() ;
		DBConnection dbcon = getDatabaseManager().requestConnection(session.getModelName()) ;

		lViewCount = DBNode.lGetViewCount(dbcon);

		getDatabaseManager().releaseConnection(session.getModelName(),dbcon);

		return lViewCount;
	}
	
	/**
	 * Gets the number of NodeUserStaterecords for the given user. (mlb 11/07)
	 *
	 * @param PCSession session, the session object for the database to use.
	 * @exception java.sql.SQLException
	 */
	
	public long lGetStateCount(PCSession session) throws SQLException {
		
		long lRecordCount = 0;
		String modelName = session.getModelName();		
		DBConnection dbcon = getDatabaseManager().requestConnection(modelName) ;
		
		lRecordCount = DBNodeUserState.lGetStateCount(dbcon, session.getUserID());
		
		getDatabaseManager().releaseConnection(modelName,dbcon);
		return lRecordCount;
	}
	
	/**
	 * Counts the number of parents of the given node (mlb 01/08)
	 *
	 * @param PCSession session, the current session object.
	 * @param sNodeID - the node to find the count of its parents
	 * @return long - the number of nodes in the Node table.
	 * @exception java.sql.SQLException
	 */
	public int iGetParentCount(PCSession session, String sNodeID) throws SQLException {

		int iViewCount = 0;
		String modelName = session.getModelName() ;
		DBConnection dbcon = getDatabaseManager().requestConnection(session.getModelName()) ;

		iViewCount = DBNode.iGetParentCount(dbcon, sNodeID);

		getDatabaseManager().releaseConnection(session.getModelName(),dbcon);

		return iViewCount;
	}

	/**
	 * Sets the state of all nodes in the current project as "Seen" for the current user
	 * 
	 * @param PCSession session, the current session object.
	 * @exception java.sql.SQLException
	 */
	public void vMarkProjectSeen(PCSession session) throws SQLException {
		
		DBConnection dbcon = getDatabaseManager().requestConnection(session.getModelName());
		DBNode.vMarkProjectSeen(dbcon, session.getUserID());
		getDatabaseManager().releaseConnection(session.getModelName(),dbcon);
		
	}
}


