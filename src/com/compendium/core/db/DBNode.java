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

package com.compendium.core.db;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.io.*;

import javax.swing.DefaultListModel;

import com.compendium.core.datamodel.*;
import com.compendium.core.db.management.*;
import com.compendium.core.CoreUtilities;
import com.compendium.core.ICoreConstants;


/**
 * The DBNode class serves as the interface layer between the Node Services objects
 * and the Node table in the database.
 *
 * @author	rema and sajid / Michelle Bachler / Lakshmi Prabhakaran
 */
public class DBNode {

	private static String IS_VIEW_TYPE = "(Node.NodeType = "+ICoreConstants.MAPVIEW
											+" OR Node.NodeType = "+ICoreConstants.LISTVIEW
											+" OR Node.NodeType = "+ICoreConstants.MOVIEMAPVIEW
											+" OR Node.NodeType = "+ICoreConstants.INBOX
											+")";
	
	/** Indicates if a Questmap import is currently underway.*/
	private static boolean QUESTMAP_IMPORTING 		= false;

	/** Indicates if an XML import is currently underway.*/
	private static boolean COMPENDIUM_IMPORTING 	= false;

	/** Indicates if nodes should be imported as translcusions if the ids match.*/
	private static boolean IMPORT_AS_TRANSCLUDED 	= false;

	/** Indicates if imported node ids should be preserved.*/
	private static boolean PRESERVE_IMPORTED_IDS 	= false;
	
	/** Indicates if node are marked seen/unseen on import.*/
	private static boolean NODES_MARKED_SEEN 		= false;

	/** Indicates if imported node information where there is a transclusion should be store over existing data.*/
	private static boolean UPDATE_TRANSLCUDED_NODES = false;

	// AUDITED

	/** SQL statement to insert a new Node Record into the Node table.*/
	public final static String INSERT_NODE_QUERY =
		"INSERT INTO Node (NodeID, NodeType, ExtendedNodeType, OriginalID, Author, " +
		"CreationDate, ModificationDate, Label, Detail, CurrentStatus, LastModAuthor) "+
		"VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ";


	/** SQL statement to update a new Node Record in the Node table.*/
	public final static String UPDATE_NODE_QUERY =
		"UPDATE Node "+
		"SET NodeType=?, ExtendedNodeType=?, OriginalID=?, Author=?, " +
		"CreationDate=?, ModificationDate=?, Label=?, Detail=?, LastModAuthor=? "+
		"WHERE NodeID=?";
	
	/** This query physically deletes the node with the given id from the database, if marked for deletion.*/
	public final static String PURGE_NODE_QUERY =
		"DELETE "+
		"FROM Node "+
		"WHERE NodeID = ? " +
		"AND CurrentStatus = "+ICoreConstants.STATUS_DELETE; // -- if delete flag is not set to true, purge should not be allowed

	/** This query physically deletes a Detail record from the database, for the given node id.*/
	public final static String PURGE_NODEDETAIL_QUERY =
		"DELETE "+
		"FROM Detail "+
		"WHERE NodeID = ?";

	/** This query physically deletes a node record from the database, for the given node id, whatever the status.*/
	public final static String PURGE_HOMEVIEW_QUERY =
		"DELETE "+
		"FROM Node "+
		"WHERE NodeID = ?"; // -- would not have been set to delete so need separate statement from normal purge.

	/** This query physically deletes nodes from the Node table for the given Author, if marked for deletion.*/
	public final static String PURGEALL_NODE_QUERY =
		"DELETE "+
		"FROM Node "+
		"WHERE Author = ? " +
		"AND CurrentStatus = "+ICoreConstants.STATUS_DELETE; // -- if delete flag is not set to true, purge should not be allowed

	/** This marks the node entry as deleted, sets the CurrentStatus attribute only, for the given node id.*/
	public final static String DELETE_NODE_QUERY =
		"Update Node " +
		"SET currentStatus = "+ICoreConstants.STATUS_DELETE+" " +
		"WHERE NodeID = ? ";

	/** This query restores the selected node by setting its status to active.*/
	public final static String RESTORE_NODE_QUERY =
		"UPDATE Node "+
		"SET CurrentStatus ="+ICoreConstants.STATUS_ACTIVE+" "+
		"WHERE NodeID = ? " ;

	/** SQL statement to set a Node label for a node with the given id.*/
	public final static String SET_NODE_LABEL_QUERY =
		"Update Node " +
		"SET Label = ?, ModificationDate = ?, LastModAuthor = ? " +
		"WHERE NodeID = ? ";
	
	/** SQL statement to set a Node original id for a node with the given id.*/
	public final static String SET_NODE_ORIGINALID_QUERY =
		"Update Node " +
		"SET OriginalID = ?, ModificationDate = ?, LastModAuthor = ? " +
		"WHERE NodeID = ? ";

	/** SQL statement to set modification date and last mod author fields.*/
	public final static String SET_NODE_MODIFICATION_QUERY =
		"Update Node " +
		"SET ModificationDate = ?, LastModAuthor = ? " +
		"WHERE NodeID = ?";
	
	/** SQL statement to set a Node detail (page 1) for a node with the given id.*/
	public final static String SET_NODE_DETAIL_QUERY =
		"Update Node " +
		"SET Detail = ?, ModificationDate = ?, LastModAuthor = ? " +
		"WHERE NodeID = ?";

	/** SQL statement to set a Node label and detail (page 1) for a node with the given id.*/
	public final static String SET_NODE_LABEL_AND_DETAIL_QUERY =
		"Update Node " +
		"SET Label = ?, Detail = ?, ModificationDate = ?, LastModAuthor = ?  " +
		"WHERE NodeID = ? ";

	/** SQL statement to set a Node type for a node with the given id.*/
	public final static String SET_NODE_TYPE_QUERY =
		"Update Node " +
		"SET NodeType = ?, ModificationDate = ?, LastModAuthor = ? " +
		"WHERE NodeID = ? ";

	public final static String SET_NODE_CREATION_QUERY =
		"Update Node " +
		"SET CreationDate = ?, ModificationDate = ?, LastModAuthor = ? " +
		"WHERE NodeID = ? ";

	/** SQL statement to set a Node author for a node with the given id.*/
	public final static String SET_NODE_AUTHOR_QUERY =
		"Update Node " +
		"SET Author = ?, ModificationDate = ?, LastModAuthor = ?" +
		"WHERE NodeID = ? ";


	// UNAUDITED
	/** SQL statement to return the current status of the Node with the given id.*/
	public final static String GET_DELETESTATUS_QUERY =
		"SELECT CurrentStatus " +
		"FROM Node "+
		"WHERE NodeID = ?";

	/** SQL statement to return the node id of the Node with the given id - check node exists.*/
	public final static String GET_NODEEXISTS_QUERY =
		"SELECT NodeID " +
		"FROM Node "+
		"WHERE NodeID = ?";

	/** SQL statement to return a node record for the Node with the given id, if its status is active.*/
	public final static String GET_NODE_SUMMARY_QUERY =
		"SELECT NodeID, NodeType, ExtendedNodeType, OriginalID, Author, CreationDate," +
		"ModificationDate, Label, Detail, LastModAuthor "+
		"FROM Node "+
		"WHERE NodeID = ? " +
		"AND CurrentStatus = "+ICoreConstants.STATUS_ACTIVE;

	/** SQL statement to return a node record for the Node with the given id, whatever its status.*/
	public final static String GET_ANY_NODE_SUMMARY_QUERY =
		"SELECT NodeID, NodeType, ExtendedNodeType, OriginalID, Author, CreationDate," +
		"ModificationDate, Label, Detail, LastModAuthor "+
		"FROM Node "+
		"WHERE NodeID = ?";

	/** SQL statement to return a node record for the Node with the given OriginalID.*/
	public final static String GET_IMPORTED_NODE_QUERY =
		"SELECT Node.NodeID, Node.NodeType, Node.ExtendedNodeType, Node.OriginalID, Node.Author, Node.CreationDate," +
		"Node.ModificationDate, Node.Label, Node.Detail, Node.LastModAuthor "+
		"FROM Node "+
		"WHERE Node.OriginalID = ? ";

	/** SQL statement to return a node record for the Node with the given id, if it is a map or a list type.*/
	public final static String GET_VIEW_QUERY =
		GET_NODE_SUMMARY_QUERY +
		" AND "+IS_VIEW_TYPE;

	/** SQL statement to return a node record for the Node with the given id, if it is a map or list an not a home view.*/
	public final static String GET_ALLVIEWS_QUERY =
		"SELECT Node.NodeID, Node.NodeType, Node.ExtendedNodeType, Node.OriginalID, Node.Author, Node.CreationDate," +
		"Node.ModificationDate, Node.Label, Node.Detail, Node.LastModAuthor "+
		"FROM Node LEFT JOIN Users ON Node.NodeID=Users.HomeView "+
		"WHERE Node.CurrentStatus = "+ICoreConstants.STATUS_ACTIVE+" "+
		"AND "+IS_VIEW_TYPE+
		"AND Users.HomeView IS NULL";

	//	Lakshmi - 1/31/06
	/** SQL statement to return the nodes for the given view ID */
	public final static String  GET_CHILDNODES_QUERY =
		"SELECT Node.NodeID, Node.NodeType, Node.ExtendedNodeType, Node.OriginalID, Node.Author, Node.CreationDate," +
		" Node.ModificationDate, Node.Label, Node.Detail, Node.LastModAuthor " +
		" FROM Node LEFT JOIN ViewNode ON Node.NodeID = ViewNode.NodeID" +
		" WHERE ViewNode.ViewID = ? " +
		" AND Node.NodeType != "+ICoreConstants.TRASHBIN +
		" AND ViewNode.CurrentStatus = "+ICoreConstants.STATUS_ACTIVE ;
	
	/** SQL statement to return all the views to full depth for the given view ID */
	public final static String  GET_CHILDVIEWS_QUERY =
		"SELECT Node.NodeID, Node.NodeType, Node.ExtendedNodeType, Node.OriginalID, Node.Author, Node.CreationDate," +
		" Node.ModificationDate, Node.Label, Node.Detail, Node.LastModAuthor " +
		" FROM Node LEFT JOIN ViewNode ON Node.NodeID = ViewNode.NodeID" +
		" WHERE ViewNode.ViewID = ? " +
		" AND Node.NodeType != "+ICoreConstants.TRASHBIN +
		" AND "+IS_VIEW_TYPE+
		" AND ViewNode.CurrentStatus = "+ICoreConstants.STATUS_ACTIVE ;

/*	public final static String GET_DELETED_NODE_SUMMARY_QUERY =
		"SELECT NodeID, NodeType, ExtendedNodeType, OriginalID, Author, CreationDate," +
		"ModificationDate, Label, Detail "+
		"FROM Node "+
		"WHERE NodeAuthor = ? " +
		"AND CurrentStatus= "+ICoreConstants.STATUS_DELETE;
*/

	/** SQL statement to return all node records with status marked for deletion.*/
	public final static String GET_DELETED_NODE_SUMMARY_QUERY =
		"SELECT Node.NodeID, Node.NodeType, Node.ExtendedNodeType, Node.OriginalID, Node.Author, Node.CreationDate," +
		"Node.ModificationDate, Node.Label, Node.Detail, Node.LastModAuthor "+
		"FROM Node "+
		"WHERE Node.CurrentStatus = "+ICoreConstants.STATUS_DELETE;

	/** SQL statement to return the count of nodes marked for deletion **/
	public final static String GET_DELETED_NODE_COUNT_QUERY =
		"SELECT count(*) FROM Node WHERE Node.CurrentStatus = "+ICoreConstants.STATUS_DELETE;
	
	/** SQL statement to return all node ids for nodes marked for deletion.*/
	public final static String GET_ALL_DELETED_NODE_ID_QUERY =
		"SELECT NodeID "+
		"FROM Node "+
		"WHERE CurrentStatus = "+ICoreConstants.STATUS_DELETE;

	/** SQL statement to return all node ids.*/
	public final static String GET_ALL_NODE_ID_QUERY =
		"SELECT NodeID "+
		"FROM Node ";
	
	/** SQL statement to return a node record for the Node with the given id, if its status is deletion.*/
	public final static String GET_DELETED_NODE_SUMMARY_QUERY_ID =
		"SELECT NodeID, NodeType, ExtendedNodeType, OriginalID, Author, CreationDate," +
		"ModificationDate, Label, Detail, LastModAuthor "+
		"FROM Node "+
		"WHERE NodeID = ? " +
		"AND CurrentStatus = "+ICoreConstants.STATUS_DELETE;

	/** SQL statement to return a node record for the Node with a given label or detail.*/
	public final static String SEARCH_NODE_QUERY =
		"Select NodeID, NodeType, ExtendedNodeType, OriginalID, Author, CreationDate," +
		"ModificationDate, Label, Detail  " +
		"FROM Node " +
		"WHERE Label LIKE  ? OR Detail LIKE ? ";
	
	/** SQL statement to count the number of nodes in the node table (mlb 11/07) */
	public final static String COUNT_NODE_QUERY =
		"Select Count(*) from Node";

	/** SQL statement to count the number of views in the node table (mlb 11/07) */
	public final static String COUNT_VIEW_QUERY =
		"Select Count(*) from Node where NodeType = 1 or Nodetype = 2";
	
	/** SQL statement to count the number of active parents a node has (mlb 01/08) */
	public final static String COUNT_PARENTS_QUERY =
		"Select Count(*) from ViewNode, Node " +
		"where ViewNode.ViewID = Node.NodeID " +
		"AND ViewNode.NodeID = ? " +
		"AND ViewNode.CurrentStatus = " + ICoreConstants.STATUS_ACTIVE + " " +
		"AND Node.CurrentStatus = " + ICoreConstants.STATUS_ACTIVE;
	
	/** SQL statement to return all node records for nodes not currently in a view.*/
	public final static String GET_LIMBO_NODE_QUERY =
		"SELECT NodeID, NodeType, ExtendedNodeType, OriginalID, Author, CreationDate," +
		"ModificationDate, Label, Detail, LastModAuthor "+
		"FROM Node "+
		"WHERE NodeID NOT IN (SELECT NodeID FROM ViewNode WHERE CurrentStatus = "+ICoreConstants.STATUS_ACTIVE+") "+
		"AND NodeID NOT IN (SELECT HomeView FROM Users) "+
		"AND Node.CurrentStatus = "+ICoreConstants.STATUS_ACTIVE;

	/** NODE DETAIL **/

	// AUDITED
	/** SQL statement to insert a node detail inot the database.*/
	public final static String INSERT_NODE_DETAIL_PAGE_QUERY =
		"INSERT INTO NodeDetail (NodeID, Author, PageNo, CreationDate, ModificationDate, Detail) "+
		"VALUES (?, ?, ?, ?, ?, ?)";

	/** SQL statement to set the detail page of a node with the given id for the given page no.*/
	public final static String SET_NODE_DETAIL_PAGE_QUERY =
		"Update NodeDetail " +
		"SET Detail = ?, CreationDate = ?, ModificationDate = ? " +
		"WHERE NodeID = ? AND PageNo = ?";

	/** SQL statement to delete a node detail page for the given node id and page no.*/
	public final static String DELETE_DETAIL_PAGE_QUERY =
		"DELETE " +
		"FROM NodeDetail "+
		"WHERE NodeID = ? AND PageNo = ?";

	// UNAUDITED
	/** SQL statement to return a node detail page record for given node id and page no.*/
	public final static String GET_NODE_DETAIL_PAGE_QUERY =
		"SELECT Detail, Author, CreationDate, ModificationDate "+
		"FROM NodeDetail "+
		"WHERE NodeID = ? AND PageNo = ?";

	/** SQL statement to return all node detail pages for the given node id.*/
	public final static String GET_ALL_DETAIL_PAGES_QUERY =
		"SELECT Detail, Author, CreationDate, ModificationDate, PageNo "+
		"FROM NodeDetail "+
		"WHERE NodeID = ?";

	/** SQL statement to return the current highest page no for the detail pages for a given node id.*/
	public final static String CHECK_PAGENO_QUERY =
		"SELECT MAX(PageNo) "+
		"FROM NodeDetail "+
		"WHERE NodeID = ?";

	/** SQL statement to return the page no for a NodeDetail record with the given node id and page no.*/
	public final static String GET_PAGENO_QUERY =
		"SELECT PageNo "+
		"FROM NodeDetail "+
		"WHERE NodeID = ? AND PageNo = ?";



	/**
	 * Set importing from Compendium XML.
	 * @param importing, indicates if an XML import is currently underway.
	 */
	public static void setImporting(boolean importing) {
		COMPENDIUM_IMPORTING = importing;
	}

	/**
	 * Return if importing mode true on or false.
	 * @return boolean, true if an XML import is currently underway, else false.
	 */
	public static boolean getImporting() {
		return COMPENDIUM_IMPORTING;
	}

	/**
	 * Set importing from questmap.
	 * @param importing, indicates if a questmap import is currently underway.
	 */
	public static void setQuestmapImporting(boolean importing) {
		QUESTMAP_IMPORTING = importing;
	}

	/**
	 * Return if importing mode true on or false.
	 * @return boolean, true if a questmap import is currently underway, else false.
	 */
	public static boolean getQuestmapImporting() {
		return QUESTMAP_IMPORTING;
	}

	/**
	 * Set IMPORT_AS_TRANSCLUDED true or false.
	 * @param importAsTranscluded, set if nodes being inserted should be treated as transclusions.
	 */
	public static void setImportAsTranscluded(boolean importAsTranscluded) {
		IMPORT_AS_TRANSCLUDED = importAsTranscluded;
	}

	/**
	 * Return if IMPORT_AS_TRANSCLUDED mode true on or false.
	 * @return boolean, true if node being inserted should be treated as translcusions, else false.
	 */
	public static boolean getImportAsTranscluded() {
		return IMPORT_AS_TRANSCLUDED;
	}

	/**
	 * Set PRESERVE_IMPORTED_IDS true or false.
	 * @param preserveIds, indicates if nodes being inserted should keep thier original ids.
	 */
	public static void setPreserveImportedIds(boolean preserveIds) {
		PRESERVE_IMPORTED_IDS = preserveIds;
	}

	/**
	 * Return if PRESERVE_IMPORTED_IDS mode true on or false.
	 * @return boolean, true if nodes being imported should keep thier original ids, else false.
	 */
	public static boolean getPreserveImportedIds() {
		return PRESERVE_IMPORTED_IDS;
	}

	/**
	 * Set UPDATE_TRANSLCUDED_NODES true or false.
	 * @param update, indicates if nodes being inserted as transclusions should update the data of the main node.
	 */
	public static void setUpdateTranscludedNodes(boolean update) {
		UPDATE_TRANSLCUDED_NODES = update;
	}

	/**
	 * Return if UPDATE_TRANSLCUDED_NODES mode true on or false.
	 * @return true if nodes being iserted as transclusions should update the data of the main node, else false.
	 */
	public static boolean getUpdateTranscludedNodes() {
		return UPDATE_TRANSLCUDED_NODES;
	}
	
	/**
	 * Return true if the NODES_MARKED_SEEN mode is on or false
	 * @return true if the NODES_MARKED_SEEN mode is on .
	 */
	public static boolean isNodesMarkedSeen() {
		return NODES_MARKED_SEEN;
	}

	/**
	 * To mark the nodes seen
	 * @param seen set if nodes to be imported should be in seen state
	 */
	public static void setNodesMarkedSeen(boolean seen) {
		NODES_MARKED_SEEN = seen;
	}

	/**
	 * Restore all the import settings to their defaults
	 */
	public static void restoreImportSettings() {
		COMPENDIUM_IMPORTING = false;
		QUESTMAP_IMPORTING = false;
		UPDATE_TRANSLCUDED_NODES = false;
		PRESERVE_IMPORTED_IDS = false;
		IMPORT_AS_TRANSCLUDED = false;
		NODES_MARKED_SEEN    = false;
	}


	/**
	 *  Inserts a new node in the database, creates a new NodeSummary object and returns it.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param id, the node id.
	 * 	@param type, the node type.
	 * 	@param xNodeType, the extended node type - not currently used.
	 * 	@param importedId, the current id of the node being imported.
	 *	@param sOriginalID, the original imported of this link.
	 *	@param author, the author of this link.
	 *	@param label, the label of the node.
	 *	@param detail, the first detail page of the node.
	 *	@param creationDate, the date of creation of the node.
	 *	@param modificationDate, the date of modification of the node.
	 *	@return com.compendium.core.datamode.INodeSummary, the node object.
	 *	@throws java.sql.SQLException
	 */	
	// Lakshmi (4/20/06) - Added userID as param to pass to DBNoderUserState for update 
	public static NodeSummary insert(DBConnection dbcon, String id, int type, String xNodeType,
				String importedId, String sOriginalID, String author, String label, String detail,
				java.util.Date creationDate, java.util.Date modificationDate, String userID )
				throws SQLException {

		return insert(dbcon, id, type, xNodeType, importedId, sOriginalID, author, label, detail,
				creationDate, modificationDate, userID, "");
	}

	/**
	 *  Inserts a new node in the database, creates a new NodeSummary object and returns it.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param id, the node id.
	 * 	@param type, the node type.
	 * 	@param xNodeType, the extended node type - not currently used.
	 * 	@param importedId, the current id of the node being imported.
	 *	@param sOriginalID, the original imported of this link.
	 *	@param author, the author of this link.
	 *	@param label, the label of the node.
	 *	@param detail, the first detail page of the node.
	 *	@param creationDate, the date of creation of the node.
	 *	@param modificationDate, the date of modification of the node.
	 *	@param userID the id of the user making this insert.
	 *	@param sLastMdoAuthor the author who made the last modification to this node.
	 *	@return com.compendium.core.datamode.INodeSummary, the node object.
	 *	@throws java.sql.SQLException
	 */	
	// Lakshmi (4/20/06) - Added userID as param to pass to DBNoderUserState for update 
	// MB (09.June.2006) - Added sLastModAuthor
	public static NodeSummary insert(DBConnection dbcon, String id, int type, String xNodeType,
				String importedId, String sOriginalID, String author, String label, String detail,
				java.util.Date creationDate, java.util.Date modificationDate, String userID, String sLastModAuthor )
				throws SQLException {

		Connection con = dbcon.getConnection();
		if (con == null)
			return null;

		NodeSummary node = null;
		boolean deleteFlag = false ; // -- the delete flag is automatically set to false.
		int nState = ICoreConstants.READSTATE;
		
		// ARE WE IMPORTING THIS NODE?
		if (COMPENDIUM_IMPORTING || QUESTMAP_IMPORTING) {

			// IF IMPORTING FROM QUESTMAP
			if (QUESTMAP_IMPORTING) { //importedId.equals("") && sOriginalID.startsWith("QM")) {
				node = (NodeSummary)DBNode.getImportedNode(dbcon, sOriginalID, userID);
				if (node != null)
					restore(dbcon, node.getId(), userID);
			}

			// IF IMPORTING FROM COMPENDIUM XML AND YOUR PRESERVING NODE IDS, 
			// SEE IF WE HAVE THE NODE ALREADY BUT DELETED.
			else if (COMPENDIUM_IMPORTING ) {
				node = DBNode.getAnyNodeSummary(dbcon, importedId, userID);
				if (node != null) {
					restore(dbcon, node.getId(), userID);
				}
			}

			// IF IMPORTING WITH TRANSCLUSION
			if ( (node != null) && IMPORT_AS_TRANSCLUDED) {
				if (UPDATE_TRANSLCUDED_NODES) {
					NodeSummary updatednode = update(dbcon, node.getId(), type, xNodeType, 
							importedId, sOriginalID, author, label, detail, 
							creationDate, modificationDate, userID, sLastModAuthor);
					if (updatednode != null)
						return updatednode;
					else
						return node;
				}
				else {
					return node;
				}
			}

			if (PRESERVE_IMPORTED_IDS && !QUESTMAP_IMPORTING) {
				id = importedId;
			}
			if(!NODES_MARKED_SEEN){
				nState = ICoreConstants.UNREADSTATE;
			}
		}

		// Think about adding this. What would be the consequences?
		// FINALLY CHECK IF A NODE WITH THAT ID ALREADY EXISTS
		/*if (doesNodeExist(dbcon, id)) {
			restore(dbcon, id);
			node = DBNode.getNodeSummary(dbcon, id);
			return node;
		}*/

		PreparedStatement pstmt = con.prepareStatement(INSERT_NODE_QUERY);

		pstmt.setString(1, id) ;
		pstmt.setInt(2, type);
		pstmt.setString(3, xNodeType) ;
		pstmt.setString(4, sOriginalID);
		pstmt.setString(5, author);
		pstmt.setDouble(6, new Long(creationDate.getTime()).doubleValue());
		pstmt.setDouble(7, new Long(modificationDate.getTime()).doubleValue());
		pstmt.setString(8, author);		

		if (!label.equals("")) {
			//ByteArrayInputStream bArrayLabel = new ByteArrayInputStream(label.getBytes());
			//pstmt.setAsciiStream(8, bArrayLabel, bArrayLabel.available());

			// ACCOMODATES UNICODE
			StringReader reader = new StringReader(label);
			pstmt.setCharacterStream(8, reader, label.length());
		}
		else {
			pstmt.setString(8, "");
		}
		if (!detail.equals("")) {
			//ByteArrayInputStream bArrayDetail = new ByteArrayInputStream(detail.getBytes());
			//pstmt.setAsciiStream(9, bArrayDetail, bArrayDetail.available());

			// ACCOMODATES UNICODE
			StringReader reader = new StringReader(detail);
			pstmt.setCharacterStream(9, reader, detail.length());

		}
		else {
			pstmt.setString(9, "");
		}

		pstmt.setBoolean(10, deleteFlag) ; //-- set the delete flag to false by default
		pstmt.setString(11, sLastModAuthor);		

		int nRowCount = 0;
		try {
			nRowCount = pstmt.executeUpdate();
		} catch (Exception e){
			e.printStackTrace();
		}

		pstmt.close();
		node = null ;

		if (nRowCount >0) {
			if (View.isViewType(type)) {
				// include the state information as param to set - as it is newly created node the state is READ
			  	node = View.getView(id, type, xNodeType, sOriginalID, 
			  			nState, author, creationDate, modificationDate, label, detail, sLastModAuthor);
			} else if (ShortCutNodeSummary.isShortCutNodeType(type)) {
				//include the state information as param to set - as it is newly created node the state is READ
				node = ShortCutNodeSummary.getShortCutNodeSummary(id, type, xNodeType, sOriginalID, 
						nState, author, creationDate, modificationDate, label, detail, null, sLastModAuthor);
			} else {
				// include the state information as param to set - as it is newly created node the state is READ
				node = NodeSummary.getNodeSummary(id, type, xNodeType, sOriginalID, 
						nState, author, creationDate, modificationDate, label, detail, sLastModAuthor) ;
			}

// mlb: Disable creating 'unread' NodeUserState entries for all users.  Rather, just insert an entry for current user
//		And since .insert() will do an update if the record exists, we don't need the following update.
			
//			boolean status = DBNodeUserState.insertStateForAllUsers(dbcon,id, ICoreConstants.UNREADSTATE);
			DBNodeUserState.insert(dbcon, id, userID, nState);
			
			//UPDATE FOR THE CURRENT USER AS READ - THEY CREATED IT AFTER ALL!
			//WE NEED TO PASS THE USERID, NOT JUST THE AUTHOR
//			if(nState != ICoreConstants.UNREADSTATE)
//				DBNodeUserState.updateUser(dbcon, id, userID, ICoreConstants.UNREADSTATE, nState);
			
			if (DBAudit.getAuditOn())
				DBAudit.auditNode(dbcon, DBAudit.ACTION_ADD, node);

			return node;
		}

		return null;
	}
	
	/**
	 *  Updates a node in the database, creates and returns the NodeSummary object.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param id, the node id.
	 * 	@param type, the node type.
	 * 	@param xNodeType, the extended node type - not currently used.
	 * 	@param importedId, the current id of the node being imported.
	 *	@param sOriginalID, the original imported of this link.
	 *	@param author, the author of this link.
	 *	@param label, the label of the node.
	 *	@param detail, the first detail page of the node.
	 *	@param creationDate, the date of creation of the node.
	 *	@param modificationDate, the date of modification of the node.
	 *	@return com.compendium.core.datamode.INodeSummary, the node object.
	 *	@throws java.sql.SQLException
	 */
	public static NodeSummary update(DBConnection dbcon, String id, int type, String xNodeType,
				String importedId, String sOriginalID, String author, String label, String detail,
				java.util.Date creationDate, java.util.Date modificationDate, String userID )
				throws SQLException {

		return update(dbcon, id, type, xNodeType, importedId, sOriginalID, author, label, detail,
				creationDate, modificationDate, userID, "");
	}

	/**
	 *  Updates a node in the database, creates and returns the NodeSummary object.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param id, the node id.
	 * 	@param type, the node type.
	 * 	@param xNodeType, the extended node type - not currently used.
	 * 	@param importedId, the current id of the node being imported.
	 *	@param sOriginalID, the original imported of this link.
	 *	@param author, the author of this link.
	 *	@param label, the label of the node.
	 *	@param detail, the first detail page of the node.
	 *	@param creationDate, the date of creation of the node.
	 *	@param modificationDate, the date of modification of the node.
	 *	@param userID the id of the user making this update.
	 *	@param sLastModAuthorName the author who last modified this node. 
	 *	@return com.compendium.core.datamode.INodeSummary, the node object.
	 *	@throws java.sql.SQLException
	 */
	public static NodeSummary update(DBConnection dbcon, String id, int type, String xNodeType,
				String importedId, String sOriginalID, String author, String label, String detail,
				java.util.Date creationDate, java.util.Date modificationDate, String userID, String sLastModAuthor )
				throws SQLException {

		Connection con = dbcon.getConnection();
		if (con == null)
			return null;

		//if(label.length() < 1)
		//	label = ICoreConstants.NOLABEL_STRING;

		//if(detail.length() < 1)
		//	detail = "";

		PreparedStatement pstmt = con.prepareStatement(UPDATE_NODE_QUERY);

		pstmt.setInt(1, type);
		pstmt.setString(2, xNodeType) ;
		pstmt.setString(3, sOriginalID);
		pstmt.setString(4, author);
		pstmt.setDouble(5, new Long(creationDate.getTime()).doubleValue());
		pstmt.setDouble(6, new Long(modificationDate.getTime()).doubleValue());

		if (!label.equals("")) {
			//ByteArrayInputStream bArrayLabel = new ByteArrayInputStream(label.getBytes());
			//pstmt.setAsciiStream(7, bArrayLabel, bArrayLabel.available());

			// ACCOMODATES UNICODE
			StringReader reader = new StringReader(label);
			pstmt.setCharacterStream(7, reader, label.length());
		}
		else {
			pstmt.setString(7, "");
		}
		if (!detail.equals("")) {
			//ByteArrayInputStream bArrayDetail = new ByteArrayInputStream(detail.getBytes());
			//pstmt.setAsciiStream(8, bArrayDetail, bArrayDetail.available());

			// ACCOMODATES UNICODE
			StringReader reader = new StringReader(detail);
			pstmt.setCharacterStream(8, reader, detail.length());
		}
		else {
			pstmt.setString(8, "");
		}
		pstmt.setString(9, sLastModAuthor);

		pstmt.setString(10, id);

		int nRowCount = 0;
		try {
			nRowCount = pstmt.executeUpdate();
		} catch (Exception e){
			e.printStackTrace();
		}

		pstmt.close();
		NodeSummary node = null;

		if (nRowCount >0) {
			node = DBNode.getAnyNodeSummary(dbcon, id, userID);

			// put Modified state for the users who have read state
			boolean status = DBNodeUserState.updateUsers(dbcon,id, ICoreConstants.READSTATE, ICoreConstants.MODIFIEDSTATE);

			// put read state for the user who has modified
			DBNodeUserState.updateUser(dbcon, id, userID, ICoreConstants.MODIFIEDSTATE, ICoreConstants.READSTATE);
			
			//set the state in NodeSummary
			node.setStateLocal(ICoreConstants.READSTATE);
			
			if (DBAudit.getAuditOn())
				DBAudit.auditNode(dbcon, DBAudit.ACTION_ADD, node);
		}
		return node;
	}
	
	/**
	 *	Deletes the node with the given id from the database.
	 *	This function marks the node as deleted in the database.
	 *	The physical deletion happens with the 'purge' method.
	 *
	 *	@param dbcon the DBConnection object to access the database with.
	 *	@param node the node Summary reference.
	 *	@param sViewID the id of the node's containing view from which it is to be deleted.
	 *	@param userID the id of the user who is doing this insert.
	 *  @param vtUsers a list of all UserProfile objects so we don't delete their HomeView, inbox etc.
	 *	@return boolean value, returns true if this was the last instance of the node and therefore it was marked for deletion,
	 							else returns false if just removed from view.
	 *	@exception java.sql.SQLException
	 */
	public static boolean delete(DBConnection dbcon, NodeSummary node, String sViewID, String userID, Vector vtUsers) throws SQLException {

		Connection con = dbcon.getConnection();
		if (con == null)
			return false;

		String sNodeID = node.getId();
				
		// Check that this is not a HomeView or Inbox being deleted
		// THESE CANNOT BE MARKED FOR DELETION OR THE USER CAN'T GET IN AGAIN
		for(Enumeration e = vtUsers.elements();e.hasMoreElements();) {
			UserProfile up = (UserProfile)e.nextElement();
			if (up.getHomeView() != null) {					
				// Do this first so next test doesn't fail if no home window
				if (up.getHomeView().getId() == sNodeID) {
					DBViewNode.delete(dbcon, sViewID, sNodeID, userID);  // Remove from view but don't delete
					return false;
				}
			}
			if (up.getLinkView() != null) {					// Do this first so next test doesn't fail if no mailbox
				if (up.getLinkView().getId() == sNodeID) {
					return false;
				}
			}
		}
		
// Note: The above code was added in v1.6 to replace the following code, now commented out.
//		 The code below requires 2 database calls, the code above uses in-memory data to
//		 determine the same thing.		
/**		
		// CHECK THAT ITS NOT A HOMEVIEW / INBOX 
		// THESE CANNOT BE MARKED FOR DELETION OR THE USER CAN'T GET IN AGAIN
		Hashtable homeviews = DBUser.getHomeViews(dbcon);
		if (homeviews.containsKey(sNodeID)) {
			// remove from view, but don't delete.
			DBViewNode.delete(dbcon, sViewID, sNodeID, userID);
			return false;
		}		
		
		Hashtable linkviews = DBUser.getLinkViews(dbcon);
		if (linkviews.containsKey(sNodeID)) {
			return false;
		}
 */
		// DELETE THE ASSOCIATED VIEWNODE FOR GIVEN VIEW
		boolean deleted = DBViewNode.delete(dbcon, sViewID, sNodeID, userID);
		
		//CHECK IF THIS IS THE LAST INSTANCE AND THEREFORE THE NODE SHOULD BE MARKED FOR DELETION
		int occurence = DBViewNode.getActiveViewCount(dbcon, sNodeID);		
		
		boolean hasUniqueAncestry = false;
		if (occurence > 0) {
			if ( View.isViewType(node.getType()) ) {
				hasUniqueAncestry = DBViewNode.hasUniqueAncestry(dbcon, sViewID, (View)node, userID);
			}
		}

		if ( occurence == 0 || hasUniqueAncestry) {			
			//set CurrentStatus to marked for deletion
			PreparedStatement pstmt = con.prepareStatement(DELETE_NODE_QUERY);
			pstmt.setString(1, sNodeID) ;

			int nRowCount = 0;
			try {
				nRowCount = pstmt.executeUpdate();
			} catch (Exception e){
				e.printStackTrace();
			}
			pstmt.close();
			if (nRowCount > 0) {
				if (DBAudit.getAuditOn()) {
					node = DBNode.getNodeSummary(dbcon, sNodeID, userID);
					DBAudit.auditNode(dbcon, DBAudit.ACTION_DELETE, node);
				}
				return true	;
			}
			else {
				return false;
			}
		}
		else {
			return deleted;
		}
	}

	/**
	 *  Restores the node with the given id from the database
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param sNodeID, the node Summary id of the node to restore.
	 *	@return boolean value, the success or failure of the restore operation.
	 *	@exception java.sql.SQLException
	 */
	public static boolean restore(DBConnection dbcon, String sNodeID, String userID) throws SQLException {

		Connection con = dbcon.getConnection();
		if (con == null)
			return false;

		PreparedStatement pstmt = con.prepareStatement(RESTORE_NODE_QUERY);
		pstmt.setString(1, sNodeID) ;
		int nRowCount = 0;
		try {
			nRowCount = pstmt.executeUpdate();
		} catch (Exception e){
			e.printStackTrace();
		}
		pstmt.close();

		if (nRowCount > 0) {
			if (DBAudit.getAuditOn()) {
				try {
					NodeSummary node = DBNode.getNodeSummary(dbcon, sNodeID, userID);
					DBAudit.auditNode(dbcon, DBAudit.ACTION_RESTORE, node);
				}
				catch(Exception ex) {
					System.out.println("FAILED SAVING AUDIT in DBNODE.RESTORE for NODEID = "+sNodeID);
					ex.printStackTrace();
				}
			}
			return true;
		}
		else {
			return false;
		}
	}

	/**
	 *  Purges the node with the given id from the database - completely removes the record.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param sNodeID, the node Summary id of the node to delete from the Node table.
	 *	@return boolean value, the success or failure of the purge operation
	 *	@exception java.sql.SQLException
	 */
	public static boolean purge(DBConnection dbcon, String sNodeID, String userID) throws SQLException {

		Connection con = dbcon.getConnection();
		if (con == null)
			return false;

		// IF AUDITING, STORE NODE DATA
		NodeSummary node = null;
		if (DBAudit.getAuditOn())
			node = DBNode.getNodeSummary(dbcon, sNodeID, userID);

		PreparedStatement pstmt = con.prepareStatement(PURGE_NODE_QUERY);
		pstmt.setString(1, sNodeID);
		int nRowCount = 0;
		try {
			nRowCount = pstmt.executeUpdate();
		} catch (Exception e){
			e.printStackTrace();
		}
		pstmt.close();

		if (nRowCount > 0) {
			if (DBAudit.getAuditOn() && node != null)
				DBAudit.auditNode(dbcon, DBAudit.ACTION_PURGE, node);

			return true;
		}
		else
			return false;
	}

	/**
	 *  Purges the home view node with the given id from the database.
	 *  Used when a user is deleted from the database.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param sNodeID, the node Summary id of the home view to delete.
	 *	@return boolean value, the success or failure of the purge operation.
	 *	@exception java.sql.SQLException
	 */
	public static boolean purgeHomeView(DBConnection dbcon, String sNodeID, String userID) throws SQLException {

		Connection con = dbcon.getConnection();
		if (con == null)
			return false;

		// PURGE DETAILS
		DBViewNode.purgeHomeView(dbcon, sNodeID, userID);

		// IF AUDITING, STORE NODE DATA
		NodeSummary node = null;
		if (DBAudit.getAuditOn())
			node = DBNode.getNodeSummary(dbcon, sNodeID, userID);

		PreparedStatement pstmt = con.prepareStatement(PURGE_HOMEVIEW_QUERY);
		pstmt.setString(1, sNodeID);
		int nRowCount = 0;
		try {
			nRowCount = pstmt.executeUpdate();
		} catch (Exception e){
			e.printStackTrace();
		}
		pstmt.close();

		if (nRowCount > 0) {
			if (DBAudit.getAuditOn() && node != null)
				DBAudit.auditNode(dbcon, DBAudit.ACTION_DELETE, node);

			return true;
		}
		else
			return false;
	}

	/**
	 *	Purges All nodes from the database that belong to the user with the given author name.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param sAuthor, the name of the author to purge nodes for.
	 *	@return boolean value, the success or failure of the purge operation.
	 *	@exception java.sql.SQLException
	 */
	public static boolean purgeAll(DBConnection dbcon, String sAuthor, String userID) throws SQLException {

		Connection con = dbcon.getConnection();
		if (con == null)
			return false;

		// if the node is marked deleted, then delete, else do not.
		// note that the node will be marked deleted only if during the purge operation it has
		// actually been marked deleted (setting the CurrentStatus) as all the references to the node
		// were removed

		// IF AUDITING, STORE DATA
		Vector data = null;
		if (DBAudit.getAuditOn())
			data = DBNode.getDeletedNodeSummary(dbcon, userID);

		PreparedStatement pstmt = con.prepareStatement(PURGEALL_NODE_QUERY);
		pstmt.setString(1, sAuthor);
		int nRowCount = 0;
		try {
			nRowCount = pstmt.executeUpdate();
		} catch (Exception e){
			e.printStackTrace();
		}
		pstmt.close();

		if (nRowCount > 0) {
			if (DBAudit.getAuditOn() && data != null) {
				int count = data.size();
				for (int i=0; i<count; i++) {
					NodeSummary node = (NodeSummary)data.elementAt(i);
					DBAudit.auditNode(dbcon, DBAudit.ACTION_DELETE, node);
				}
			}

			return true;
		}
		else {
			return false;
		}
	}

	/**
	 *  Sets the label of the node in the database and returns if successful.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param sNodeID, the id of the node to set the label for.
	 *	@param sLabel, the new label of the node.
	 *	@param dModificationDate java.util.Date, the date for this modification to the node record.
	 *  @param sLastModAuthor the author name of the person who made this modification. 
	 *  @param sUserID the id of the current user logged in. 
	 *	@return boolean value, the success or failure of the operation.
	 *	@exception java.sql.SQLException
	 */
	public static boolean setLabel(DBConnection dbcon, String sNodeID, String sLabel, 
			java.util.Date dModificationDate, String sLastModAuthor, String sUserID) throws SQLException {

		Connection con = dbcon.getConnection();
		if (con == null)
			return false;

		double date = new Long(dModificationDate.getTime()).doubleValue();
		PreparedStatement pstmt = con.prepareStatement(SET_NODE_LABEL_QUERY);

		// THIS IS NOW A MEMO FIELD SO SAME PROBLEM WITH 256 CUT OFF AS FOR setDetail - mb
		if (!sLabel.equals("")) {
			//ByteArrayInputStream bArrayLabel = new ByteArrayInputStream(label.getBytes());
			//pstmt.setAsciiStream(1, bArrayLabel, bArrayLabel.available());

			// ACCOMODATES UNICODE
			StringReader reader = new StringReader(sLabel);
			pstmt.setCharacterStream(1, reader, sLabel.length());
		}
		else {
			pstmt.setString(1, "");
		}

		pstmt.setDouble(2, date);
		pstmt.setString(3, sLastModAuthor);
		pstmt.setString(4, sNodeID);

		int nRowCount = 0;
		try {
			nRowCount = pstmt.executeUpdate();
		} catch (Exception e){
			e.printStackTrace();
		}
		pstmt.close();

		if (nRowCount > 0) {

			NodeSummary node = DBNode.getNodeSummary(dbcon, sNodeID, sUserID);
			
			if (DBAudit.getAuditOn()) {
				DBAudit.auditNode(dbcon, DBAudit.ACTION_EDIT, node);
			}
			
			// Lakshmi (4/24/06) modify the state info to READ for the current user both in DB and local
			if ( !getImporting() && !getQuestmapImporting() ) {				
				// at this point update the NodeUserState table with the update
				// all users that have read the node previously should have their
				// state changed to 'modified'
				boolean updated = DBNodeUserState.updateUsers(dbcon, sNodeID, ICoreConstants.READSTATE, ICoreConstants.MODIFIEDSTATE);
				
				int state = node.getState();
				if(state == ICoreConstants.UNREADSTATE){
					DBNodeUserState.updateUser(dbcon, sNodeID, sUserID, ICoreConstants.UNREADSTATE, ICoreConstants.READSTATE);
				} else  {
					DBNodeUserState.updateUser(dbcon, sNodeID, sUserID, ICoreConstants.MODIFIEDSTATE, ICoreConstants.READSTATE);
				}
				node.setStateLocal(ICoreConstants.READSTATE);
			}
			return true;
		}
		else
			return false ;
	}
	
	/**
	 *  Sets the detail of the node in the database and returns true.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param sNodeID, the id of the node to set the detail for.
	 *	@param sDetail, the new detail page (page one) of the node.
	 *	@param sAuthor, the author of the current change.
	 *	@param dModificationDate java.util.Date, the date for this modification to the node record.
	 *  @param sLastModAuthor the author name of the person who made this modification. 
	 *  @param sUserID the id of the current user logged in. 
	 *	@return boolean value, the success or failure of the operation.
	 *	@exception java.sql.SQLException
	 */
	public static boolean setDetail(DBConnection dbcon, String sNodeID, String sDetail, 
			String sAuthor, java.util.Date dModificationDate, String sLastModAuthor, String sUserID) throws SQLException {

		Connection con = dbcon.getConnection();
		if (con == null)
			return false;

		// UPDATE PAGE ONE OF THE NODEDETAILPAGE
		NodeSummary node = DBNode.getNodeSummary(dbcon, sNodeID, sUserID);
		double date = new Long(dModificationDate.getTime()).doubleValue();

		NodeDetailPage page = new NodeDetailPage();
		page.setNodeID(sNodeID);
		page.setAuthor(sAuthor);
		page.setPageNo(1);
		page.setText(sDetail);
		page.setCreationDate(node.getCreationDate());
		page.setModificationDate(dModificationDate);

		setDetailPage(dbcon, sAuthor, page, dModificationDate, sLastModAuthor, sUserID);

		PreparedStatement pstmt = con.prepareStatement(SET_NODE_DETAIL_QUERY);

		if (!sDetail.equals("")) {
			//ByteArrayInputStream bArrayDetail = new ByteArrayInputStream(detail.getBytes());
			//pstmt.setAsciiStream(1, bArrayDetail, bArrayDetail.available());

			// ACCOMODATES UNICODE
			StringReader reader = new StringReader(sDetail);
			pstmt.setCharacterStream(1, reader, sDetail.length());
		}
		else {
			pstmt.setString(1, "");
		}

		pstmt.setDouble(2, date);
		pstmt.setString(3, sLastModAuthor);		
		pstmt.setString(4, sNodeID);

		int nRowCount = 0;
		try {
			nRowCount = pstmt.executeUpdate();
		} catch (Exception e){
			e.printStackTrace();
		}
		pstmt.close();

		if (nRowCount >0) {

			if (DBAudit.getAuditOn()) {
				DBAudit.auditNode(dbcon, DBAudit.ACTION_EDIT, node);
			}

			
			// Lakshmi (4/24/06) modify the state info to READ for the current user both in DB and local
			if(!getImporting() && !getQuestmapImporting()) {
				//	 at this point update the NodeUserState table with the update
				// all users that have read the node previously should have their
				// state changed to 'modified'
				boolean updated = DBNodeUserState.updateUsers(dbcon, sNodeID, ICoreConstants.READSTATE, ICoreConstants.MODIFIEDSTATE) ;
				
				int state = node.getState();
				if(state == ICoreConstants.UNREADSTATE){
					DBNodeUserState.updateUser(dbcon, sNodeID, sUserID, ICoreConstants.UNREADSTATE, ICoreConstants.READSTATE);
				} else  {
					DBNodeUserState.updateUser(dbcon, sNodeID, sUserID, ICoreConstants.MODIFIEDSTATE, ICoreConstants.READSTATE);
				}
				node.setStateLocal(ICoreConstants.READSTATE);
			}
			return true;
		}
		else
			return false;
	}

	/**
	 *  Sets the detail of the node in the database and returns true.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param sAuthor, the author of the current change.
	 *	@param oDetail com.compendium.core.datamodel.NodeDetailPage, the new page of detail to add to the NodeDetail table.
	 *	@param dModificationDate java.util.Date, the date for this modification to the node record.
	 *  @param sLastModAuthor the author name of the person who made this modification. 
	 *  @param sUserID the id of the current user logged in. 
	 *	@return boolean value, the success or failure of the operation.
	 *	@exception java.sql.SQLException
	 */
	public static boolean setDetailPage(DBConnection dbcon, String sAuthor, 
			NodeDetailPage oDetail, java.util.Date dModificationDate, String sLastModAuthor, String sUserID) throws SQLException {

		Connection con = dbcon.getConnection();
		if (con == null)
			return false;

		String sNodeID = oDetail.getNodeID();
		int pageNo = oDetail.getPageNo();
		String detailText = (String)oDetail.getText();

		double date = new Long(dModificationDate.getTime()).doubleValue();

		NodeSummary node = DBNode.getNodeSummary(dbcon, sNodeID, sUserID);
		if (pageNo == 1) {

			PreparedStatement pstmt1 = con.prepareStatement(SET_NODE_DETAIL_QUERY);
			if (!detailText.equals("")) {
				//ByteArrayInputStream bArrayDetail = new ByteArrayInputStream(detailText.getBytes());
				//pstmt1.setAsciiStream(1, bArrayDetail, bArrayDetail.available());

				// ACCOMODATES UNICODE
				StringReader reader = new StringReader(detailText);
				pstmt1.setCharacterStream(1, reader, detailText.length());
			}
			else {
				pstmt1.setString(1, "");
			}

			pstmt1.setDouble(2, date);
			pstmt1.setString(3, sLastModAuthor);
			pstmt1.setString(4, sNodeID);

			int nRowCount = 0;
			try {
				nRowCount = pstmt1.executeUpdate();
			} catch (Exception e){
				e.printStackTrace();
			}
			pstmt1.close();

			if (nRowCount >0) {
				if (DBAudit.getAuditOn()) {
					DBAudit.auditNode(dbcon, DBAudit.ACTION_EDIT, node);
				}

				
				// Lakshmi (4/24/06) modify the state info to READ for the current user both in DB and local
				if(!getImporting() && !getQuestmapImporting()) {
					
					boolean updated = DBNodeUserState.updateUsers(dbcon, sNodeID, ICoreConstants.READSTATE, ICoreConstants.MODIFIEDSTATE) ;
					int state = node.getState();
					
					if(state == ICoreConstants.UNREADSTATE){
						DBNodeUserState.updateUser(dbcon, sNodeID, sUserID, ICoreConstants.UNREADSTATE, ICoreConstants.READSTATE);
					} else  {
						DBNodeUserState.updateUser(dbcon, sNodeID, sUserID, ICoreConstants.MODIFIEDSTATE, ICoreConstants.READSTATE);
					}
					node.setStateLocal(ICoreConstants.READSTATE);
				}
			}
			else
				return false;
		}

		// CHECK IF THIS IS AN UPDATE OR AN INSERT
		PreparedStatement pstmt1 = con.prepareStatement(GET_PAGENO_QUERY);
		pstmt1.setString(1, sNodeID);
		pstmt1.setInt(2, pageNo);
		ResultSet rs = null;
		try {
			rs = pstmt1.executeQuery();
		} catch (Exception e){
			e.printStackTrace();
		}

		int num = 0;
		if (rs != null) {
			while (rs.next()) {
				num	= rs.getInt(1);
			}
		}

		// IF THE PAGE EXISTS UPDATE, ELSE INSERT NEW ONE
		if (num > 0) {
			PreparedStatement pstmt = con.prepareStatement(SET_NODE_DETAIL_PAGE_QUERY);

			if (!detailText.equals("")) {
				//ByteArrayInputStream bArrayDetail = new ByteArrayInputStream(detailText.getBytes());
				//pstmt.setAsciiStream(1, bArrayDetail, bArrayDetail.available());

				// ACCOMODATES UNICODE
				StringReader reader = new StringReader(detailText);
				pstmt.setCharacterStream(1, reader, detailText.length());
			}
			else {
				pstmt.setString(1, "");
			}

			double enteredDate = new Long(oDetail.getCreationDate().getTime()).doubleValue();

			pstmt.setDouble(2, enteredDate);
			pstmt.setDouble(3, date);
			pstmt.setString(4, sNodeID);
			pstmt.setInt(5, pageNo);

			int nRowCount = 0;
			try {
				nRowCount = pstmt.executeUpdate();
			} catch (Exception e){
				e.printStackTrace();
			}
			pstmt.close();

			if (nRowCount > 0) {
				if (DBAudit.getAuditOn()) {
					DBAudit.auditNodeDetail(dbcon, DBAudit.ACTION_EDIT, oDetail, sAuthor);
				}

				// Lakshmi (4/24/06) modify the state info to READ for the current user both in DB and local
				if(!getImporting() && !getQuestmapImporting()) {
					boolean updated = DBNodeUserState.updateUsers(dbcon, sNodeID, ICoreConstants.READSTATE, ICoreConstants.MODIFIEDSTATE) ;
					
					int state = node.getState();
					if(state == ICoreConstants.UNREADSTATE){
						DBNodeUserState.updateUser(dbcon, sNodeID, sUserID, ICoreConstants.UNREADSTATE, ICoreConstants.READSTATE);
					} else  {
						DBNodeUserState.updateUser(dbcon, sNodeID, sUserID, ICoreConstants.MODIFIEDSTATE, ICoreConstants.READSTATE);
					}
					node.setStateLocal(ICoreConstants.READSTATE);
				}

				return true;
			}
			else {
				return false ;
			}
		}
		else {
			PreparedStatement pstmt = con.prepareStatement(INSERT_NODE_DETAIL_PAGE_QUERY);

			pstmt.setString(1, sNodeID);
			pstmt.setString(2, sAuthor);
			pstmt.setInt(3, pageNo);
			pstmt.setDouble(4, date);
			pstmt.setDouble(5, date);

			if (!detailText.equals("")) {
				//ByteArrayInputStream bArrayDetail = new ByteArrayInputStream(detailText.getBytes());
				//pstmt.setAsciiStream(6, bArrayDetail, bArrayDetail.available());

				// ACCOMODATES UNICODE
				StringReader reader = new StringReader(detailText);
				pstmt.setCharacterStream(6, reader, detailText.length());
			}
			else {
				pstmt.setString(6, "");
			}

			int nRowCount = 0;
			try {
				nRowCount = pstmt.executeUpdate();
			} catch (Exception e){
				e.printStackTrace();
			}
			pstmt.close();

			if (nRowCount > 0) {
				
				if (DBAudit.getAuditOn()) {
					DBAudit.auditNodeDetail(dbcon, DBAudit.ACTION_EDIT, oDetail, sAuthor);
				}

				// Lakshmi (4/24/06) modify the state info to READ for the current user both in DB and local
				if(!getImporting() && !getQuestmapImporting()) {

					boolean updated = DBNodeUserState.updateUsers(dbcon, sNodeID, ICoreConstants.READSTATE, ICoreConstants.MODIFIEDSTATE) ;					
					int state = node.getState();
					if(state == ICoreConstants.UNREADSTATE){
						DBNodeUserState.updateUser(dbcon, sNodeID, sUserID, ICoreConstants.UNREADSTATE, ICoreConstants.READSTATE);
					} else  {
						DBNodeUserState.updateUser(dbcon, sNodeID, sUserID, ICoreConstants.MODIFIEDSTATE, ICoreConstants.READSTATE);
					}
					node.setStateLocal(ICoreConstants.READSTATE);
				}
				return true;
			}
			else
				return false;
		}
	}

	/**
	 *  Delete the detail page of the node in the database.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param sAuthor, the author of the current change.
	 *	@param sDetail com.compendium.core.datamodel.NodeDetailPage, the page of detail to delete from the NodeDetail table.
	 *	@return boolean value, the success or failure of the operation.
	 *	@exception java.sql.SQLException
	 */
	public static boolean deleteDetailPage(DBConnection dbcon, String sAuthor, NodeDetailPage sDetail, String userID) throws SQLException {

		Connection con = dbcon.getConnection();
		if (con == null)
			return false;

		// CHECK IF THIS IS AN UPDATE OR AN INSERT
		String sNodeID = sDetail.getNodeID();
		PreparedStatement pstmt = con.prepareStatement(DELETE_DETAIL_PAGE_QUERY);
		pstmt.setString(1, sNodeID);
		pstmt.setInt(2, sDetail.getPageNo());

		int rowCount = 0;
		try {
			rowCount = pstmt.executeUpdate();
		} catch (Exception e){
			e.printStackTrace();
		}
		if (rowCount > 0) {
			NodeSummary node = DBNode.getNodeSummary(dbcon, sNodeID, userID);
			
			if (DBAudit.getAuditOn()) {
				DBAudit.auditNodeDetail(dbcon, DBAudit.ACTION_DELETE, sDetail, sAuthor );
			}


			
			// Lakshmi (4/24/06) modify the state info to READ for the current user both in DB and local
			if(!getImporting() && !getQuestmapImporting()) {
				boolean updated = DBNodeUserState.updateUsers(dbcon, sNodeID, ICoreConstants.READSTATE, ICoreConstants.MODIFIEDSTATE) ;
				
				int state = node.getState();
				if(state == ICoreConstants.UNREADSTATE){
					DBNodeUserState.updateUser(dbcon, sNodeID, userID, ICoreConstants.UNREADSTATE, ICoreConstants.READSTATE);
				} else  {
					DBNodeUserState.updateUser(dbcon, sNodeID, userID, ICoreConstants.MODIFIEDSTATE, ICoreConstants.READSTATE);
				}
				node.setStateLocal(ICoreConstants.READSTATE);
			}
			return true;
		}

		return false;
	}

	/**
	 *  Sets a list of detail pages for the given node in the database and returns if successful.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param sNodeID, the id of the node to set the detail pages for.
	 *	@param sAuthor, the author of the current change.
	 *	@param oldDetails, the old pages of detail.
	 *	@param details, the new pages of detail to add to the NodeDetail table.
	 *	@param dModificationDate java.util.Date, the date for this modification to the node record.
	 *  @param sLastModAuthor the author name of the person who made this modification. 
	 *  @param sUserID the id of the current user logged in. 
	 *	@return boolean value, the success or failure of the operation.
	 *	@exception java.sql.SQLException
	 */
	public static boolean setAllDetailPages(DBConnection dbcon, String sNodeID, String sAuthor, 
			Vector oldDetails, Vector details, java.util.Date dModificationDate, String sLastModAuthor, 
			String sUserID) throws SQLException {

		Connection con = dbcon.getConnection();
		if (con == null)
			return false;

		PreparedStatement pstmt1 = con.prepareStatement(CHECK_PAGENO_QUERY);
		pstmt1.setString(1, sNodeID);
		ResultSet rs = null;
		try {
			rs = pstmt1.executeQuery();
		} catch (Exception e){
			e.printStackTrace();
		}

		int maxPage = 0;
		if (rs != null) {
			while (rs.next()) {
				maxPage	= rs.getInt(1);
			}
		}

		String detail = "";
		NodeDetailPage inner = null;

		int count = details.size();
		for (int i=0; i<count; i++) {
			inner = (NodeDetailPage)details.elementAt(i);
			DBNode.setDetailPage(dbcon, sAuthor, inner, dModificationDate, sLastModAuthor, sUserID);
		}

		// REMOVE ANY EXCESS PAGES
		if (maxPage > 0 && count-1 < maxPage) {
			for (int j=count; j <= maxPage; j++ ) {
				if (oldDetails != null && j < oldDetails.size()) {
					NodeDetailPage pg = (NodeDetailPage)oldDetails.elementAt(j);
					pg.setPageNo(j+1);
					DBNode.deleteDetailPage(dbcon, sAuthor, pg, sUserID);
				}
			}
		}

		return true;
	}

	/**
	 *  Sets the label and detail of the node in the database and returns if successful.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param sNodeID, the id of the node to set the label and detail for.
	 *	@param sLabel, the new node label.
	 *	@param sDetail, the new page one of node detail.
	 *	@param dModificationDate java.util.Date, the date for this modification to the node record.
	 *  @param sLastModAuthor the author name of the person who made this modification. 
	 *  @param sUserID the id of the current user logged in. 
	 *	@return boolean value, the success or failure of the operation.
	 *	@exception java.sql.SQLException
	 */
	public static boolean setLabelAndDetail(DBConnection dbcon, String sNodeID, 
			String sLabel, String sDetail, java.util.Date dModificationDate, String sLastModAuthor, String sUserID) throws SQLException {

		Connection con = dbcon.getConnection();
		if (con == null)
			return false;

		double date = new Long(dModificationDate.getTime()).doubleValue();
		PreparedStatement pstmt = con.prepareStatement(SET_NODE_LABEL_AND_DETAIL_QUERY);

		// THIS IS NOW A MEMO FIELD SO SAME PROBLEM WITH 256 CUT OFF AS FOR Detail below - mb
		if (sLabel != "") {
			//ByteArrayInputStream bArrayLabel = new ByteArrayInputStream(label.getBytes());
			//pstmt.setAsciiStream(1, bArrayLabel, bArrayLabel.available());

			// ACCOMODATES UNICODE
			StringReader reader = new StringReader(sLabel);
			pstmt.setCharacterStream(1, reader, sLabel.length());
		}
		else {
			pstmt.setString(1, "");
		}

		//cannot use setString for this field because it seems to have
		//a cutoff of 256 bytes.  Used setAsciiStream instead. -- bz
		if (sDetail != "") {
			//ByteArrayInputStream bArrayDetail = new ByteArrayInputStream(sDetail.getBytes());
			//pstmt.setAsciiStream(2, bArrayDetail, bArrayDetail.available());

			// ACCOMODATES UNICODE
			StringReader reader = new StringReader(sDetail);
			pstmt.setCharacterStream(2, reader, sDetail.length());
		}
		else {
			pstmt.setString(2, "");
		}

		pstmt.setDouble(3, date);
		pstmt.setString(4, sLastModAuthor);
		pstmt.setString(5, sNodeID);

		int nRowCount = 0;
		try {
			nRowCount = pstmt.executeUpdate();
		} catch (Exception e){
			e.printStackTrace();
		}
		pstmt.close();

		if (nRowCount > 0) {
			NodeSummary node = DBNode.getNodeSummary(dbcon, sNodeID, sUserID);
			
			if (DBAudit.getAuditOn()) {
				DBAudit.auditNode(dbcon, DBAudit.ACTION_EDIT, node);
			}

			// Lakshmi (4/24/06) modify the state info to READ for the current user both in DB and local
			if(!getImporting() && !getQuestmapImporting()) {
				
				boolean updated = DBNodeUserState.updateUsers(dbcon, sNodeID, ICoreConstants.READSTATE, ICoreConstants.MODIFIEDSTATE) ;
				int state = node.getState();
				
				if(state == ICoreConstants.UNREADSTATE){
					DBNodeUserState.updateUser(dbcon, sNodeID, sUserID, ICoreConstants.UNREADSTATE, ICoreConstants.READSTATE);
				} else  {
					DBNodeUserState.updateUser(dbcon, sNodeID, sUserID, ICoreConstants.MODIFIEDSTATE, ICoreConstants.READSTATE);
				}
				node.setStateLocal(ICoreConstants.READSTATE);
			}
			return true;
		}
		else
			return false;
	}


	/**
	 *  Sets the type of the node in the database and returns if successful.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param sNodeID, the id of the node to set the type for.
	 *	@param nOldType, the original type of this node.
	 *	@param nNewType, the new type to set for this node.
	 *	@param dModificationDate java.util.Date, the date for this modification to the node record.
	 *  @param sLastModAuthor the author name of the person who made this modification. 
	 *  @param sUserID the id of the current user logged in. 
	 *	@return boolean value, the success or failure of the operation.
	 *	@exception java.sql.SQLException
	 */
	public static boolean setType(DBConnection dbcon, String sNodeID, int nOldType, int nNewType, 
			java.util.Date dModificationDate, String sLastModAuthor, String sUserID) throws SQLException {
		Connection con = dbcon.getConnection();
		if (con == null)
			return false;

		// IF THIS WAS A REFERENCE NODE OR A MAP OR LIST, AND IS NOW NOT A REFERENCE OR A MAP OR A LIST
		// DELETE THE ENTRY FROM THE REFERENCE TABLE
		if ( (nOldType == ICoreConstants.REFERENCE || View.isViewType(nOldType))
			&& (!View.isViewType(nNewType) && nNewType != ICoreConstants.REFERENCE) ) {

			boolean referenceDelete = DBReferenceNode.delete(dbcon, sNodeID);
		}

		double date = new Long(dModificationDate.getTime()).doubleValue();
		PreparedStatement pstmt = con.prepareStatement(SET_NODE_TYPE_QUERY);

		pstmt.setInt(1, nNewType);
		pstmt.setDouble(2, date);
		pstmt.setString(3, sLastModAuthor);
		pstmt.setString(4, sNodeID);

		int nRowCount = 0;
		try {
			nRowCount = pstmt.executeUpdate();
		} catch (Exception e){
			e.printStackTrace();
		}
		pstmt.close();

		if (nRowCount >0) {
			NodeSummary node = DBNode.getNodeSummary(dbcon, sNodeID, sUserID);
			
			if (DBAudit.getAuditOn()) {
				DBAudit.auditNode(dbcon, DBAudit.ACTION_EDIT, node);
			}

			// Lakshmi (4/24/06) modify the state info to READ for the current user both in DB and local
			if(!getImporting() && !getQuestmapImporting()) {
				boolean updated = DBNodeUserState.updateUsers(dbcon, sNodeID, ICoreConstants.READSTATE, ICoreConstants.MODIFIEDSTATE) ;
				int state = node.getState();
				if(state == ICoreConstants.UNREADSTATE){
					DBNodeUserState.updateUser(dbcon, sNodeID, sUserID, ICoreConstants.UNREADSTATE, ICoreConstants.READSTATE);
				} else  {
					DBNodeUserState.updateUser(dbcon, sNodeID, sUserID, ICoreConstants.MODIFIEDSTATE, ICoreConstants.READSTATE);
				}
				node.setStateLocal(ICoreConstants.READSTATE);
			}
			return true;
		}
		else
			return false ;
	}

	/**
	 *  Sets the original id of the node in the database and returns if successful.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param sNodeID, the id of the node to set the type for.
	 *	@param nOriginalID, the original id to set for this node.
	 *	@param dModificationDate java.util.Date, the date for this modification to the node record.
	 *  @param sLastModAuthor the author name of the person who made this modification. 
	 *  @param sUserID the id of the current user logged in. 
	 *
	 *	@return boolean value, the success or failure of the operation.
	 *	@exception java.sql.SQLException
	 */
	public static boolean setOriginalID(DBConnection dbcon, String sNodeID, String sOriginalID, 
			java.util.Date dModificationDate, String sLastModAuthor, String userID) throws SQLException {
		
		Connection con = dbcon.getConnection();
		if (con == null)
			return false;

		PreparedStatement pstmt = con.prepareStatement(SET_NODE_ORIGINALID_QUERY);

		pstmt.setString(1, sOriginalID);
		pstmt.setDouble(2, dModificationDate.getTime());
		pstmt.setString(3, sNodeID);

		int nRowCount = 0;
		try {
			nRowCount = pstmt.executeUpdate();
		} catch (Exception e){
			e.printStackTrace();
		}
		pstmt.close();

		if (nRowCount >0) {
			NodeSummary node = DBNode.getNodeSummary(dbcon, sNodeID, userID);
			
			if (DBAudit.getAuditOn()) {
				DBAudit.auditNode(dbcon, DBAudit.ACTION_EDIT, node);
			}

			// Lakshmi (4/24/06) modify the state info to READ for the current user both in DB and local
			if(!getImporting() && !getQuestmapImporting()) {
				boolean updated = DBNodeUserState.updateUsers(dbcon, sNodeID, ICoreConstants.READSTATE, ICoreConstants.MODIFIEDSTATE) ;
				int state = node.getState();
				if(state == ICoreConstants.UNREADSTATE){
					DBNodeUserState.updateUser(dbcon, sNodeID, userID, ICoreConstants.UNREADSTATE, ICoreConstants.READSTATE);
				} else  {
					DBNodeUserState.updateUser(dbcon, sNodeID, userID, ICoreConstants.MODIFIEDSTATE, ICoreConstants.READSTATE);
				}
				node.setStateLocal(ICoreConstants.READSTATE);
			}
			return true;
		}
		else
			return false;
	}


	/**
	 *  Sets the creation date of the node in the database and returns if successful.
	 *
	 *	@param dbcon, the DBConnection object to access the database with.
	 *	@param sNodeID the id of the node to set the creation date for.
	 *	@param dCreationDate, the new creation date for the node record.
	 *	@param dModificationDate the date of this modification.
	 *  @param sLastModAuthor the author name of the person who made this modification. 
	 *  @param sUserID the id of the current user logged in. 	 
	 *	@return boolean value, the success or failure of the operation.
	 *	@exception java.sql.SQLException
	 */
	public static boolean setCreationDate(DBConnection dbcon, String sNodeID, 
			Date dCreationDate, Date dModificationDate, String sLastModAuthor, String sUserID) throws SQLException {

		Connection con = dbcon.getConnection();
		if (con == null)
			return false;

		double date = new Long((new Date()).getTime()).doubleValue();
		double creationDate = new Long(dCreationDate.getTime()).doubleValue();

		PreparedStatement pstmt = con.prepareStatement(SET_NODE_CREATION_QUERY);

		pstmt.setDouble(1, creationDate);
		pstmt.setDouble(2, date);
		pstmt.setString(3, sLastModAuthor);
		pstmt.setString(4, sNodeID);

		int nRowCount = 0;
		try {
			nRowCount = pstmt.executeUpdate();
		} catch (Exception e){
			e.printStackTrace();
		}
		pstmt.close();

		if (nRowCount >0) {
			
			NodeSummary node = DBNode.getNodeSummary(dbcon, sNodeID, sUserID);
			if (DBAudit.getAuditOn()) {
				DBAudit.auditNode(dbcon, DBAudit.ACTION_EDIT, node);
			}




			// Lakshmi (4/24/06) modify the state info to READ for the current user both in DB and local
			if(!getImporting() && !getQuestmapImporting()) {
				boolean updated = DBNodeUserState.updateUsers(dbcon, sNodeID, ICoreConstants.READSTATE, ICoreConstants.MODIFIEDSTATE) ;
				int state = node.getState();
				if(state == ICoreConstants.UNREADSTATE){
					DBNodeUserState.updateUser(dbcon, sNodeID, sUserID, ICoreConstants.UNREADSTATE, ICoreConstants.READSTATE);
				} else  {
					DBNodeUserState.updateUser(dbcon, sNodeID, sUserID, ICoreConstants.MODIFIEDSTATE, ICoreConstants.READSTATE);
				}
				node.setStateLocal(ICoreConstants.READSTATE);
			}
			return true;
		}
		else
			return false ;
	}

	/**
	 *  Sets the modification date of the node in the database and returns if successful.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param sNodeID, the id of the node to set the modification date for.
	 *	@param dModificationDate java.util.Date, the new modification date for the node record.
	 *  @param sLastModAuthor the author name of the person who made this modification. 
	 *  @param sUserID the id of the current user logged in. *
	 *	@return boolean value, the success or failure of the operation.
	 *	@exception java.sql.SQLException
	 */
	public static boolean setModified(DBConnection dbcon, String sNodeID, 
			Date dModificationDate, String sLastModAuthor, String sUserID) throws SQLException {

		Connection con = dbcon.getConnection();
		if (con == null)
			return false;

		double date = new Long((new Date()).getTime()).doubleValue();
		double modDate = new Long(dModificationDate.getTime()).doubleValue();

		PreparedStatement pstmt = con.prepareStatement(SET_NODE_MODIFICATION_QUERY);
		pstmt.setDouble(1, modDate);
		pstmt.setString(2, sLastModAuthor);
		pstmt.setString(3, sNodeID);

		int nRowCount = 0;
		try {
			nRowCount = pstmt.executeUpdate();
		} catch (Exception e){
			e.printStackTrace();
		}
		pstmt.close();

		if (nRowCount >0) {
			NodeSummary node = DBNode.getNodeSummary(dbcon, sNodeID, sUserID);
			if (DBAudit.getAuditOn()) {
				DBAudit.auditNode(dbcon, DBAudit.ACTION_EDIT, node);
			}

			
			// Lakshmi (4/24/06) modify the state info to READ for the current user both in DB and local
			if(!getImporting() && !getQuestmapImporting()) {
				boolean updated = DBNodeUserState.updateUsers(dbcon, sNodeID, ICoreConstants.READSTATE, ICoreConstants.MODIFIEDSTATE) ;
				int state = node.getState();
				if(state == ICoreConstants.UNREADSTATE){
					DBNodeUserState.updateUser(dbcon, sNodeID, sUserID, ICoreConstants.UNREADSTATE, ICoreConstants.READSTATE);
				} else  {
					DBNodeUserState.updateUser(dbcon, sNodeID, sUserID, ICoreConstants.MODIFIEDSTATE, ICoreConstants.READSTATE);
				}
				node.setStateLocal(ICoreConstants.READSTATE);
			}
			return true;
		}
		else
			return false;
	}

	/**
	 *  Sets the author of the node in the database and returns if successful.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param sNodeID, the id of the node to set the type for.
	 *	@param sAuthor, the new author for this node.
	 *	@param dModificationDate java.util.Date, the date for this modification to the node record.
	 * @param sLastModAuthor the author name of the person who made this modification. 
	 * @param sUserID the id of the current user logged in. 	 
	 *	@return boolean value, the success or failure of the operation.
	 *	@exception java.sql.SQLException
	 */
	public static boolean setAuthor(DBConnection dbcon, String sNodeID, String sAuthor, 
			java.util.Date dModificationDate, String sLastModAuthor, String sUserID) throws SQLException {

		Connection con = dbcon.getConnection();
		if (con == null)
			return false;

		double date = new Long(dModificationDate.getTime()).doubleValue();

		PreparedStatement pstmt = con.prepareStatement(SET_NODE_AUTHOR_QUERY);
		pstmt.setString(1, sAuthor);
		pstmt.setDouble(2, date);
		pstmt.setString(3, sLastModAuthor);
		pstmt.setString(4, sNodeID);

		int nRowCount = 0;
		try {
			nRowCount = pstmt.executeUpdate();
		} catch (Exception e){
			e.printStackTrace();
		}
		pstmt.close();

		if (nRowCount >0) {
			NodeSummary node = DBNode.getNodeSummary(dbcon, sNodeID, sUserID);
			if (DBAudit.getAuditOn()) {
				DBAudit.auditNode(dbcon, DBAudit.ACTION_EDIT, node);
			}

			// Lakshmi (4/24/06) modify the state info to READ for the current user both in DB and local
			if(!getImporting() && !getQuestmapImporting()) {
				boolean updated = DBNodeUserState.updateUsers(dbcon, sNodeID, ICoreConstants.READSTATE, ICoreConstants.MODIFIEDSTATE) ;
				int state = node.getState();
				if(state == ICoreConstants.UNREADSTATE){
					DBNodeUserState.updateUser(dbcon, sNodeID, sUserID, ICoreConstants.UNREADSTATE, ICoreConstants.READSTATE);
				} else  {
					DBNodeUserState.updateUser(dbcon, sNodeID, sUserID, ICoreConstants.MODIFIEDSTATE, ICoreConstants.READSTATE);
				}
				node.setStateLocal(ICoreConstants.READSTATE);
			}
			return true;
		}
		else
			return false ;
	}

// READS

	/**
	 *  Returns all the nodes for the given view id
	 * @param dbcon DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 * @param viewID the id of view whose child nodes are required.
	 * @param sUserID the id of the current user.
	 * @return Enumeration, a list of all <code>View</code> objects in the Home window.
	 * @throws SQLException
	 */	
	/*
	 * Lakshmi - 1/31/06
	 */
	public static Enumeration getChildNodes(DBConnection dbcon, String viewID, String sUserID) throws SQLException {

		Vector vtChildNodes = new Vector();
		Connection con = dbcon.getConnection();
		if (con == null)
			return null;
		
		PreparedStatement pstmt = con.prepareStatement(GET_CHILDNODES_QUERY);
		pstmt.setString(1, viewID);
		ResultSet rs = null;
		try {
			rs = pstmt.executeQuery();
		} catch (Exception e){
			e.printStackTrace();
		}
				
		NodeSummary nodeSummary =  null;
		try {
		if (rs != null) {
			while (rs.next()) {
				nodeSummary = processNode(dbcon, rs, sUserID);
				vtChildNodes.add(nodeSummary);
			}
		}
		
		} catch (Exception e){
			e.printStackTrace();
		}
		pstmt.close(); 
		return vtChildNodes.elements();
	}
	
	/**
	 *  Returns the views in the given view id
	 * @param dbcon DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 * @param viewID the id of view whose child nodes are required.
	 * @return Enumeration, a list of all <code>View</code> objects in the Home window.
	 * @throws SQLException
	 */	
	/*
	 * Lakshmi - 10/19/06
	 */
	public static Vector getChildViews(DBConnection dbcon, String viewID, String sUserID, Vector vtChildViews) throws SQLException {

		View view =  null;
		Connection con = dbcon.getConnection();
		if (con == null)
			return null;
		
		PreparedStatement pstmt = con.prepareStatement(GET_CHILDVIEWS_QUERY);
		pstmt.setString(1, viewID);
		ResultSet rs = null;
		try {
			rs = pstmt.executeQuery();
		} catch (Exception e){
			e.printStackTrace();
		}
		
		try {
			if (rs != null) {
				while (rs.next()) {
					view  = (View)processNode(dbcon, rs, sUserID);
					if(!vtChildViews.contains(view)) {
						vtChildViews.add(view);
					}
				}
			}
		} catch (Exception e){
			e.printStackTrace();
		}
		pstmt.close(); 
		return vtChildViews;
	}
	
	/** Used to check for maps within maps and stop never-ending loop.*/
	private static Hashtable htCheckViews = null;
	
	/**
	 *  Returns all views to full depth from the given view id
	 * @param dbcon DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 * @param viewID the id of view whose child nodes are required.
	 * @return Vector, a list of all <code>View</code> objects in the Home window.
	 * @throws SQLException
	 */	
	/*
	 * Lakshmi - 1/31/06
	 */
	public static Vector getAllChildViews(DBConnection dbcon, String viewID, String userID) throws SQLException {
		 
		htCheckViews = new Hashtable(51);
		
		Vector views = new Vector();		
		Vector temp = new Vector();
		
		temp = getChildViews(dbcon, viewID, userID, temp);
		views.addAll(temp);
		
		htCheckViews.put(viewID, viewID);		
		
		boolean stop = false;
		
		while(!stop) {
			Vector nextLevel = new Vector();
			for(int i = 0; i < temp.size(); i++ ){
				View view = ((View)temp.get(i));
				viewID = view.getId();
				if (!htCheckViews.containsKey(viewID)) {
					nextLevel = getChildViews(dbcon, viewID, userID, nextLevel);
					htCheckViews.put(viewID, viewID);
				} 
			}
			
			if(nextLevel.size() <= 0){
				stop = true;
			} else {
				temp.clear();
				temp = nextLevel;
				for(int i = 0; i < temp.size(); i ++){
					View view = ((View)temp.get(i));
					if(!views.contains(view)){
						views.addElement(view);	
					} else {
						temp.remove(view);
					}
				}				
			}
		}
		return views;
	}
	
	/**
	 *  Returns all the Nodes not assigned to any View.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@return Vector, of all nodes not currently assigned to a View.
	 *	@exception java.sql.SQLException
	 */
	public static Vector getLimboNodes(DBConnection dbcon, String userID) throws SQLException {

		Connection con = dbcon.getConnection();
		Vector nodes = new Vector(51);

		if (con == null)
			return nodes;

		Statement pstmt = con.createStatement();
		ResultSet rs = null;
		try {
			rs = pstmt.executeQuery(GET_LIMBO_NODE_QUERY);
		} catch (Exception e){
			e.printStackTrace();
		}

		NodeSummary node = null;
		if (rs != null) {

			while (rs.next()) {
				node = processNode(dbcon, rs, userID);				
				nodes.addElement(node);
			}
		}
		pstmt.close();

		return nodes;
	}

	/**
	 *  Retrieves the node summary with the given id from the database and returns it, if its status is active.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param sNodeID, the id of the node to return.
	 *	@return com.compendium.core.datamodel.NodeSummary, the node for the given id.
	 *	@exception java.sql.SQLException
	 */
	public static NodeSummary getNodeSummary(DBConnection dbcon, String sNodeID, String userID) throws SQLException {

		Connection con = dbcon.getConnection();

		if (con == null)
			return null;

		PreparedStatement pstmt = con.prepareStatement(GET_NODE_SUMMARY_QUERY);
		pstmt.setString(1, sNodeID);
		ResultSet rs = null;
		try {
			rs = pstmt.executeQuery();
		} catch (Exception e){
			e.printStackTrace();
		}

		NodeSummary node = null;
		if (rs != null) {

			while (rs.next()) {
				node = processNode(dbcon, rs, userID);				
			}
		}
		

		if (pstmt != null)
			pstmt.close();

		return node;
	}


	/**
	 *  Retrieves the node summary with the given id from the database and returns it whatever its status.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param sNodeID, the id of the node to return.
	 *	@return com.compendium.core.datamodel.NodeSummary, the node for the given id.
	 *	@exception java.sql.SQLException
	 */
	public static NodeSummary getAnyNodeSummary(DBConnection dbcon, String sNodeID, String userID) throws SQLException {

		Connection con = dbcon.getConnection();

		if (con == null)
			return null;

		PreparedStatement pstmt = con.prepareStatement(GET_ANY_NODE_SUMMARY_QUERY);
		pstmt.setString(1, sNodeID);
		ResultSet rs = null;
		try {
			rs = pstmt.executeQuery();
		} catch (Exception e){
			e.printStackTrace();
		}

		NodeSummary node = null;
		if (rs != null) {

			while (rs.next()) {
				node = processNode(dbcon, rs, userID);				
			}
		}
		
		if (pstmt != null)
			pstmt.close();

		return node;
	}

	/**
	 *  Retrieves the deleted node summary for the node with the given id from the database, if it is marked for deletion.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param sNodeID, the id of the node to return.
	 *	@return com.compendium.core.datamodel.NodeSummary, the node for the given id.
	 *	@exception java.sql.SQLException
	 */
	public static NodeSummary getDeletedNodeSummaryId(DBConnection dbcon, String sNodeID, String userID) throws SQLException {

		Connection con = dbcon.getConnection();
		if (con == null)
			return null;

		PreparedStatement pstmt = con.prepareStatement(GET_DELETED_NODE_SUMMARY_QUERY_ID);
		pstmt.setString(1, sNodeID);

		ResultSet rs = null;
		try {
			rs = pstmt.executeQuery();
		} catch (Exception e){
			e.printStackTrace();
		}

		NodeSummary node = null;
		if (rs != null) {

			while (rs.next()) {
				node = processNode(dbcon, rs, userID);				
			}
		}
	
		pstmt.close();
		return node;
	}

	/**
	 *  Returns all the Views from the database.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@return Enumeration, a list of all <code>View</code> objects in the database.
	 *	@exception java.sql.SQLException
	 */
	public static Enumeration getAllViews(DBConnection dbcon, String userID) throws SQLException {

		Vector views = new Vector(51);

		Connection con = dbcon.getConnection();
		if (con == null)
			return null;

		PreparedStatement pstmt = con.prepareStatement(GET_ALLVIEWS_QUERY);
		ResultSet rs = null;
		try {
			rs = pstmt.executeQuery();
		} catch (Exception e){
			e.printStackTrace();
		}

	  	View view = null;
		if (rs != null) {
			while (rs.next()) {
				view = (View)processNode(dbcon, rs, userID);				
				views.addElement(view);
			}
		}
		pstmt.close();

		return views.elements();
	}

	/**
	 *  Returns the view with the given id from the database and returns it
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param sNodeID, the id of the view node to return.
	 *	@return com.compendium.core.datamodel.IView, the view for the given id.
	 *	@exception java.sql.SQLException
	 */
	public static IView getView(DBConnection dbcon, String sNodeID, String userID) throws SQLException {

		Connection con = dbcon.getConnection();
		if (con == null)
			return null;

		PreparedStatement pstmt = con.prepareStatement(GET_VIEW_QUERY);
		pstmt.setString(1, sNodeID);

		ResultSet rs = null;
		try {
			rs = pstmt.executeQuery();
		} catch (Exception e){
			e.printStackTrace();
		}

	  	View view = null;
		if (rs != null) {
			while (rs.next()) {
				view = (View)processNode(dbcon, rs, userID);				
			}
		}
		pstmt.close();
		return view;
	}

	/**
	 *  Retrieves the node with the given imported id from the database and returns it.
 	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param sOriginalID, the original id of the node to return.
	 *	@return com.compendium.core.datamodel.NodeSummary, the node for the given original id.
	 *	@exception java.sql.SQLException
	 */
 	public static INodeSummary getImportedNode(DBConnection dbcon, String sOriginalID, String userID) throws SQLException {

		Connection con = dbcon.getConnection();
		if (con == null)
			return null;

		PreparedStatement pstmt = con.prepareStatement(GET_IMPORTED_NODE_QUERY);
		pstmt.setString(1, sOriginalID);

		ResultSet rs = null;
		try {
			rs = pstmt.executeQuery();
		} catch (Exception e){
			e.printStackTrace();
		}

		NodeSummary node = null;
		if (rs != null) {

			while (rs.next()) {
				node = processNode(dbcon, rs, userID);
			}
		}
		pstmt.close();
		return node;
	}

	/**
	 *  Retrieves the deleted node summary objects from the database.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@return Vector, a list of all nodes that have been marked for deletion in the database.
	 *	@exception java.sql.SQLException
	 */
	public static Vector getDeletedNodeSummary(DBConnection dbcon, String userID) throws SQLException {

		Connection con = dbcon.getConnection();
		if (con == null)
			return null;

		PreparedStatement pstmt = con.prepareStatement(GET_DELETED_NODE_SUMMARY_QUERY);
		ResultSet rs = null;
		try {
			rs = pstmt.executeQuery();
		} catch (Exception e){
			e.printStackTrace();
		}
		Vector vtNodes = new Vector(51);
		NodeSummary node = null;

		if (rs != null) {

			while (rs.next()) {
				node = processNode(dbcon, rs, userID);				
				vtNodes.addElement(node);
			}
		}
		pstmt.close();
		return vtNodes;
	}

	/**
	 *  Retrieves the details page for the given nodeid and page number.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param sNodeID, the id of the view node to return the detail page for.
	 *	@param nPageNode, the number of the detail page to return.
	 *	@return com.compendium.core.datamodel.NodeDetailPage, the page of detail as requested else null.
	 *	@exception java.sql.SQLException
	 */
	public static NodeDetailPage getDetailPage(DBConnection dbcon, String sNodeID, int nPageNo) throws SQLException {

		Connection con = dbcon.getConnection();
		if (con == null)
			return null;

		PreparedStatement pstmt = con.prepareStatement(GET_NODE_DETAIL_PAGE_QUERY);
		pstmt.setString(1, sNodeID);
		pstmt.setInt(2, nPageNo);

		ResultSet rs = null;
		try {
			rs = pstmt.executeQuery();
		} catch (Exception e){
			e.printStackTrace();
		}

		NodeDetailPage detail = null;
		if (rs != null) {
			while (rs.next()) {
				String text = rs.getString(1);
				String sAuthor = rs.getString(2);
				Date oCDate	= new Date(new Double(rs.getLong(3)).longValue());
				Date oMDate	= new Date(new Double(rs.getLong(4)).longValue());

				detail = new NodeDetailPage(sNodeID, sAuthor, text, nPageNo, oCDate, oMDate);
			}
		}
		pstmt.close();
		return detail;
	}

	/**
	 *  Retrieves the details pages for the given node identifier.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param sNodeID, the id of the view node to return the detail pages for.
	 *	@return Vector, of <code>NodeDetailPage</code> objects, the pages of detail for the given node.
	 *	@exception java.sql.SQLException
	 */
	public static Vector getAllDetailPages(DBConnection dbcon, String sNodeID) throws SQLException {

		Connection con = dbcon.getConnection();
		if (con == null)
			return null;

		PreparedStatement pstmt = con.prepareStatement(GET_ALL_DETAIL_PAGES_QUERY);
		pstmt.setString(1, sNodeID);

		ResultSet rs = null;
		try {
			rs = pstmt.executeQuery();
		} catch (Exception e){
			e.printStackTrace();
		}

		Vector details = new Vector();
		if (rs != null) {

			while (rs.next()) {

				String text = rs.getString(1) ;
				String userID = rs.getString(2);
				Date oCDate	= new Date(new Double(rs.getLong(3)).longValue());
				Date oMDate	= new Date(new Double(rs.getLong(4)).longValue());
				int pageNo = rs.getInt(5);

				NodeDetailPage detail = new NodeDetailPage(sNodeID, userID, text, pageNo, oCDate, oMDate);

				details.addElement(detail);
			}
		}
		pstmt.close();
		return details;
	}


	/**
	 *  Return if the given node has been marked for deletion.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param sNodeID, the id of the node to check.
	 *	@return boolean, true if the given node has been marked for deletion.
	 *	@exception java.sql.SQLException
	 */
	public static boolean isMarkedForDeletion(DBConnection dbcon, String sNodeID) throws SQLException {

		Connection con = dbcon.getConnection();
		if (con == null)
			return false;

		PreparedStatement pstmt = con.prepareStatement(GET_DELETESTATUS_QUERY);
		pstmt.setString(1, sNodeID);

		ResultSet rs = null;
		try {
			rs = pstmt.executeQuery();
		} catch (Exception e){
			e.printStackTrace();
		}
		if (rs != null) {
			if (rs.next()) {
				int status = rs.getInt(1);
				if (status == ICoreConstants.STATUS_DELETE)
					return true;
				else
					return false;
			}
		}
		pstmt.close();
		return false;
	}

	/**
	 *  Return if the given node exists.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param sNodeID, the id of the view node to return the detail page for.
	 *	@return boolean, true if a node with the given id exists, else false.
	 *	@exception java.sql.SQLException
	 */
	public static boolean doesNodeExist(DBConnection dbcon, String sNodeID) throws SQLException {

		Connection con = dbcon.getConnection();
		if (con == null)
			return false;

		PreparedStatement pstmt = con.prepareStatement(GET_NODEEXISTS_QUERY);
		pstmt.setString(1, sNodeID) ;

		ResultSet rs = null;
		try {
			rs = pstmt.executeQuery();
		} catch (Exception e){
			e.printStackTrace();
		}
		if (rs != null) {
			while (rs.next()) {
				String	sId	= rs.getString(1) ;
				return true;
			}
		}
		pstmt.close();
		return false;
	}


	/**
	 *  Searches the Node Table for the given string and retirves the node(s) whose label of detail contain the given keyword.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param sKeyword, the keyword to check for.
	 *	@return Enumeration, list of <code>NodeSummary</code> objects whose label or detail contain the given keyword.
	 *	@exception java.sql.SQLException
	 */
	public static Enumeration searchNode(DBConnection dbcon, String sKeyword, String userID) throws SQLException {

		Connection con = dbcon.getConnection();
		if (con == null)
			return null;

		PreparedStatement pstmt = con.prepareStatement(SEARCH_NODE_QUERY);

		pstmt.setString(1,  "%" + sKeyword + "%");
		pstmt.setString(2,  "%" + sKeyword + "%");

		ResultSet rs = null;

		try {
			rs = pstmt.executeQuery();
		} catch (Exception e){
			e.printStackTrace();
		}

		Vector vtNodes = new Vector(51);
		NodeSummary node = null;

		if (rs != null) {
			while (rs.next()) {
				node = processNode(dbcon, rs, userID);				
				vtNodes.addElement(node);
			}
		}
		pstmt.close();

		return vtNodes.elements();
	}
	/**
	 *  Returns the total number of nodes in the Node table.	// Added by mlb 11/07
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@return long, count of nodes in the nodetable.
	 *	@exception java.sql.SQLException
	 */
	public static long lGetNodeCount(DBConnection dbcon) throws SQLException {

		long	nodecount = 0;
		Connection con = dbcon.getConnection();
		if (con == null)
			return 0;

		PreparedStatement pstmt = con.prepareStatement(COUNT_NODE_QUERY);
		ResultSet rs = null;

		try {
			rs = pstmt.executeQuery();
		} catch (Exception e){
			e.printStackTrace();
		}

		try {
			if (rs != null) {
				rs.next();
				nodecount = rs.getLong(1);
			}
		} 
		catch (Exception e){
			System.out.println("Node count failed");
			e.printStackTrace();
		}
		pstmt.close();
		return nodecount;
	}
	
	/**
	 *  Returns the total number of views (Maps/Lists in the Node table.	// Added by mlb 11/07
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@return long, count of nodes in the nodetable.
	 *	@exception java.sql.SQLException
	 */
	public static long lGetViewCount(DBConnection dbcon) throws SQLException {

		long	lViewCount = 0;
		Connection con = dbcon.getConnection();
		if (con == null)
			return 0;

		PreparedStatement pstmt = con.prepareStatement(COUNT_VIEW_QUERY);
		ResultSet rs = null;
		try {
			rs = pstmt.executeQuery();
		} catch (Exception e){
			e.printStackTrace();
		}

		try {
			if (rs != null) {
				rs.next();
				lViewCount = rs.getLong(1);
			}
		} 
		catch (Exception e){
			System.out.println("View count failed");
			e.printStackTrace();
		}
		pstmt.close();
		return lViewCount;
	}
	
	/**
	 *  Returns the number of parents the current node has.	// Added by mlb 01/08
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *  @param sNodeID - the node to search for the parents of
	 *	@return int, count of the number of this node's parents.
	 *	@exception java.sql.SQLException
	 */
	public static int iGetParentCount(DBConnection dbcon, String sNodeID) throws SQLException {
		int iCount = 0;
		
		Connection con = dbcon.getConnection();
		if (con == null)
			return 0;

		PreparedStatement pstmt = con.prepareStatement(COUNT_PARENTS_QUERY);
		pstmt.setString(1, sNodeID) ;

		ResultSet rs = null;
		try {
			rs = pstmt.executeQuery();
		} catch (Exception e){
			e.printStackTrace();
		}
		if (rs != null) {
			rs.next();
			iCount = rs.getInt(1);
		}
		pstmt.close();
		return iCount;
	}
	
	/**
	 *  Returns the number of nodes marked for deletion in the database.	// Added by mlb 01/08
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@return int, count of the number of this node's parents.
	 *	@exception java.sql.SQLException
	 */
	public static int iGetDeletedNodeCount(DBConnection dbcon) throws SQLException {
		int iCount = 0;
		
		Connection con = dbcon.getConnection();
		if (con == null)
			return 0;

		PreparedStatement pstmt = con.prepareStatement(GET_DELETED_NODE_COUNT_QUERY);

		ResultSet rs = null;
		try {
			rs = pstmt.executeQuery();
		} catch (Exception e){
			e.printStackTrace();
		}
		if (rs != null) {
			rs.next();
			iCount = rs.getInt(1);
		}
		pstmt.close();
		return iCount;
	}
	
	/**
	 *  Marks all nodes in the current project as Seen by the current user.	// Added by mlb 02/08
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param sUserID - theUserID of the current user
	 *	@exception java.sql.SQLException
	 */
	public static void vMarkProjectSeen(DBConnection dbcon, String sUserID) throws SQLException {
		DBNodeUserState.vMarkProjectSeen(dbcon, sUserID);
	}
	
	
	/**
	 * 	Helper method to extract and build a node object from a result set item.
	 *  the ResultSet item is expected to contains the follow fields in the following order:
	 *  NodeID, Type, ExtendedNodeType, OriginalID, Author, CreationDate, ModificationDate,
	 *  Label, DEtail, LastModAuthor.
	 *
	 *	@param dbcon the DBConnection object to access the database with.
	 *  @param rs the ResultsSet to process to create the link from a query.
	 *  @param sUserID the id of the user getting this data.
	 *	@return com.compendium.core.datamodel.NodeSummary, the NodeSummary object created from the passed data.
	 */
	protected static NodeSummary processNode(DBConnection dbcon, ResultSet rs, String sUserID) throws SQLException {
		
		NodeSummary node = null;
		
		String	sId			= rs.getString(1);
		int		nType		= rs.getInt(2);
		String 	sXNodeType 	= rs.getString(3);
		String	sOriginalID	= rs.getString(4);
		String	sAuthor		= rs.getString(5);
		Date	oCDate		= new Date(new Double(rs.getLong(6)).longValue());
		Date	oMDate		= new Date(new Double(rs.getLong(7)).longValue());
		String	sLabel		= rs.getString(8);
		String	sDetail		= rs.getString(9);
		String	sLastModAuthor = rs.getString(10);

		if (sLastModAuthor == null) {
			sLastModAuthor=sAuthor;
		}
		
// mlb: possible performance improvement here.  Disabled for now because I'm not 100% confident
// that all changes to a node are reflected in the NodeSummary object before they go to the database		
//		if (NodeSummary.bIsInCache(sId)) {
//			return NodeSummary.getNodeSummary(sId);
//		}
		
		int nState = DBNodeUserState.get(dbcon, sId, sUserID);
		
		if (View.isViewType(nType)) {
			node = View.getView(sId, nType, sXNodeType, sOriginalID, 
					nState, sAuthor, oCDate, oMDate, sLabel, sDetail, sLastModAuthor);
//			node.setLocalImage(DBReferenceNode.getImage(dbcon, sId));
//			node.setLocalSource(DBReferenceNode.getReference(dbcon, sId));
//			node.setLocalImageSize(DBReferenceNode.getImageSize(dbcon, sId));
			DBReferenceNode.getIRIS(dbcon, sId, node);
		}
		else if(ShortCutNodeSummary.isShortCutNodeType(nType)) {
			NodeSummary refNode = DBShortCutNode.getShortCutNode(dbcon, sId, sUserID);
			node = ShortCutNodeSummary.getShortCutNodeSummary(sId, nType, sXNodeType, sOriginalID, 
					nState, sAuthor, oCDate, oMDate, sLabel, sDetail, null, sLastModAuthor);
			((ShortCutNodeSummary)node).setReferredNode(refNode);
		}
		else {
			node = NodeSummary.getNodeSummary(sId, nType, sXNodeType, sOriginalID, 
					nState, sAuthor, oCDate, oMDate, sLabel, sDetail, sLastModAuthor);
			if (nType == ICoreConstants.REFERENCE) {
//				node.setLocalImage(DBReferenceNode.getImage(dbcon, sId));
//				node.setLocalSource(DBReferenceNode.getReference(dbcon, sId));
//				node.setLocalImageSize(DBReferenceNode.getImageSize(dbcon, sId));
				DBReferenceNode.getIRIS(dbcon, sId, node);
			}
		}
		
		return node;
	}
}
