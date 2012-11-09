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

import java.awt.*;
import java.util.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.SQLException;
import java.sql.ResultSet;

import com.compendium.core.datamodel.*;
import com.compendium.core.db.management.*;
import com.compendium.core.CoreUtilities;
import com.compendium.core.ICoreConstants;

/**
 * The DBViewNode class serves as the interface layer between the NodePosition objects
 * and the ViewNode table in the database.
 *
 * @author	Rema and Sajid / Michelle Bachler
 */
public class DBViewNode {

// AUDITED

	/** SQL statement to insert a new ViewNode Record into the ViewNode table.*/
	public final static String INSERT_VIEWNODE_QUERY =
		"INSERT INTO ViewNode (ViewID, NodeID, XPos, YPos, CreationDate, ModificationDate, CurrentStatus) "+
		"VALUES (?, ?, ?, ?, ?, ?, ?) ";

	/** SQL statement to insert a new ViewNode Record into the ViewNode table.*/
	public final static String INSERT_VIEWNODE_WITH_FORMATTING_QUERY =
		"INSERT INTO ViewNode (ViewID, NodeID, XPos, YPos, CreationDate, ModificationDate, CurrentStatus, " +
		"ShowTags, ShowText, ShowTrans, ShowWeight, SmallIcon, HideIcon, LabelWrapWidth, "+
		"FontSize, FontFace, FontStyle, Foreground, Background) " +
		"VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? ,?, ?, ?) ";

	/** SQL statement to update the node formatting for all transcluded nodes to match the given formatting.*/
	public final static String UPDATE_TRANSCLUSION_FORMATTING_QUERY =
		"UPDATE ViewNode "+
		"SET ModificationDate=?, ShowTags=?, ShowText=?, ShowTrans=?, "+
		"ShowWeight=?, SmallIcon=?, HideIcon=?, LabelWrapWidth=?, "+
		"FontSize=?, FontFace=?, FontStyle=?, Foreground=?, Background=? " +
		"WHERE NodeID=? AND CurrentStatus="+ICoreConstants.STATUS_ACTIVE;
	
	/** SQL statement to update the node formatting for just the given node to the given formatting.*/
	public final static String UPDATE_FORMATTING_QUERY =
		"UPDATE ViewNode "+
		"SET ModificationDate=?, ShowTags=?, ShowText=?, ShowTrans=?, "+
		"ShowWeight=?, SmallIcon=?, HideIcon=?, LabelWrapWidth=?, "+
		"FontSize=?, FontFace=?, FontStyle=?, Foreground=?, Background=? " +
		"WHERE NodeID=? AND ViewID=? AND CurrentStatus="+ICoreConstants.STATUS_ACTIVE;
	
	// MARK FOR DELETION
	/** SQL statement to mark ViewNode record for deletion with the given ViewID and NodeID.*/
	public final static String DELETE_VIEWNODE_QUERY =
		"UPDATE ViewNode "+
		"SET CurrentStatus = "+ICoreConstants.STATUS_DELETE+
		" WHERE ViewID = ? AND NodeID = ?";

	/** SQL statement to mark ViewNode records for deletion with the given ViewID.*/
	public final static String DELETE_VIEW_QUERY =
		"UPDATE ViewNode "+
		"SET CurrentStatus = "+ICoreConstants.STATUS_DELETE+
		" WHERE ViewID = ? ";

	/** SQL statement to mark ViewNode records for deletion with the given NodeID.*/
	public final static String DELETE_NODE_QUERY =
		"UPDATE ViewNode "+
		"SET CurrentStatus = "+ICoreConstants.STATUS_DELETE+
		" WHERE NodeID = ? ";

	// MARK AS ACTIVE - RESTORE
	/** SQL statement to mark ViewNode record as active with the given ViewID and NodeID.*/
	public final static String RESTORE_VIEWNODE_QUERY =
		"UPDATE ViewNode "+
		"SET CurrentStatus = "+ICoreConstants.STATUS_ACTIVE+
		" WHERE ViewID = ? AND NodeID = ?";

	/** SQL statement to mark ViewNode records as active with the given ViewID.*/
	public final static String RESTORE_VIEW_QUERY =
		"UPDATE ViewNode "+
		"SET CurrentStatus = "+ICoreConstants.STATUS_ACTIVE+
		" WHERE ViewID = ?";

	/** SQL statement to mark ViewNode records as active with the given NodeID.*/
	public final static String RESTORE_NODE_QUERY =
		"UPDATE ViewNode "+
		"SET CurrentStatus = "+ICoreConstants.STATUS_ACTIVE+
		" WHERE NodeID = ?";

	// PURGE
	/** SQL statement to delete the ViewNode record with the given ViewID and NodeID if marked for deletion.*/
	public final static String PURGE_VIEWNODE_QUERY =
		"DELETE "+
		"FROM ViewNode "+
		"WHERE ViewID = ? AND NodeID = ? AND CurrentStatus = "+ICoreConstants.STATUS_DELETE;

	/** SQL statement to delete the ViewNode records with the given ViewID if marked for deletion.*/
	public final static String PURGE_VIEW_QUERY =
		"DELETE "+
		"FROM ViewNode "+
		"WHERE ViewID = ? AND CurrentStatus = "+ICoreConstants.STATUS_DELETE;

	/** SQL statement to delete the ViewNode record with the given ViewID and NodeID.*/
	public final static String PURGE_HOMEVIEW_QUERY =
		"DELETE "+
		"FROM ViewNode "+
		"WHERE ViewID = ? OR NodeID = ?";

	/** SQL statement to update the Position for the ViewNode record with the given ViewID and NodeID.*/
	public final static String SET_NODE_POSITION_QUERY =
		"UPDATE ViewNode " +
		"SET XPos = ?, YPos = ?, ModificationDate = ? " +
		"WHERE ViewID = ? AND NodeID = ? "+
		"AND CurrentStatus = "+ICoreConstants.STATUS_ACTIVE;

// UNAUDITED

	/** SQL statement to return the ViewNode records with the given ViewID and NodeID, if active.*/
	public final static String GET_VIEWNODE_QUERY =
		"SELECT ViewID, NodeID, XPos, YPos, CreationDate, ModificationDate, "+
		"ShowTags, ShowText, ShowTrans, ShowWeight, SmallIcon, HideIcon, LabelWrapWidth, "+
		"FontSize, FontFace, FontStyle, Foreground, Background " +
		"FROM ViewNode "+
		"WHERE ViewID = ? AND NodeID = ? "+
		"AND CurrentStatus = "+ICoreConstants.STATUS_ACTIVE;

	/** SQL statement to return the ViewNode records with the given ViewID and NodeID, whatever the Status.*/
	public final static String GET_ANYVIEWNODE_QUERY =
		"SELECT ViewID, NodeID, XPos, YPos, CreationDate, ModificationDate, CurrentStatus " +
		"ShowTags, ShowText, ShowTrans, ShowWeight, SmallIcon, HideIcon, LabelWrapWidth, "+
		"FontSize, FontFace, FontStyle, Foreground, Background " +				
		"FROM ViewNode "+
		"WHERE ViewID = ? AND NodeID = ?";

	/** SQL statement to return the ViewNode records with the given ViewID marked for deletion.*/
	public final static String GET_DELETEDVIEW_QUERY =
		"SELECT ViewID, NodeID, XPos, YPos, CreationDate, ModificationDate, CurrentStatus " +
		"FROM ViewNode "+
		"WHERE ViewID = ?"+
		"AND CurrentStatus = "+ICoreConstants.STATUS_DELETE;

	/** SQL statement to return the ViewNode records with the given NodeID.*/
	public final static String GET_NODENODE_QUERY =
		"SELECT ViewID, NodeID, XPos, YPos, CreationDate, ModificationDate, CurrentStatus " +
		"ShowTags, ShowText, ShowTrans, ShowWeight, SmallIcon, HideIcon, LabelWrapWidth, "+
		"FontSize, FontFace, FontStyle, Foreground, Background " +		
		"FROM ViewNode "+
		"WHERE NodeID = ?";

	/** SQL statement to return the ViewNode records with the given ViewID.*/
	public final static String GET_NODEIDS_QUERY =
		"SELECT ViewID, NodeID, XPos, YPos, CreationDate, ModificationDate, CurrentStatus " +
		"FROM ViewNode "+
		"WHERE ViewID = ?";

	/** SQL statement to return the ViewIDs for the given NodeID, if active.*/
	public final static String GET_VIEWS_QUERY =
		"SELECT ViewID " +
		"FROM ViewNode " +
		"WHERE NodeID = ? "+
		"AND CurrentStatus = "+ICoreConstants.STATUS_ACTIVE;

	/** SQL statement to return the ViewIDs for the given NodeID, if active.*/
	public final static String GET_ACTIVEVIEWS_QUERY =
		"SELECT ViewID " +
		"FROM ViewNode " +
		"WHERE NodeID = ? "+
		"AND CurrentStatus = "+ICoreConstants.STATUS_ACTIVE;

	/** SQL statement to return a count of the ViewIDs for the given NodeID, if active.*/
	public final static String GET_ACTIVEVIEWSCOUNT_QUERY =
		"SELECT Count(ViewID) " +
		"FROM ViewNode " +
		"WHERE NodeID = ? "+
		"AND CurrentStatus = "+ICoreConstants.STATUS_ACTIVE;

	/**
	 * SQL statement to return the ViewIDs for the given NodeID,
	 * if the ViewNode is marked for deletion, but the Node is still active.
	 */
	public final static String GET_DELETEDVIEWS_QUERY =
		"SELECT ViewNode.ViewID " +
		"FROM ViewNode LEFT JOIN Node ON ViewNode.ViewID=Node.NodeID " +
		"WHERE ViewNode.NodeID = ? "+
		"AND ViewNode.CurrentStatus = "+ICoreConstants.STATUS_DELETE+
		" AND Node.CurrentStatus = "+ICoreConstants.STATUS_ACTIVE;

	/**
	 * SQL statement to return a count of the ViewIDs for the given NodeID,
	 * if the ViewNode is marked for deletion, but the Node is still active.
	 */
	public final static String GET_DELETEDVIEWSCOUNT_QUERY =
		"SELECT Count(ViewNode.ViewID) " +
		"FROM ViewNode LEFT JOIN Node ON ViewNode.ViewID=Node.NodeID " +
		"WHERE ViewNode.NodeID = ? "+
		"AND ViewNode.CurrentStatus = "+ICoreConstants.STATUS_DELETE+
		" AND Node.CurrentStatus = "+ICoreConstants.STATUS_ACTIVE;

	/**
	 * SQL statement to join the node and the ViewNode tables to return NodePosition objects
	 * in a given view, if the ViewNode record was active.
	 */
	public final static String GET_NODEPOSITIONS_QUERY =
		"SELECT Node.NodeID, Node.NodeType, Node.ExtendedNodeType, Node.OriginalID, Node.Author, " +
		"Node.CreationDate, Node.ModificationDate, Node.Label, Node.Detail, Node.LastModAuthor, "+
		"ViewNode.ViewID, ViewNode.XPos, ViewNode.YPos, ViewNode.CreationDate, ViewNode.ModificationDate, " +
		"ViewNode.ShowTags, ViewNode.ShowText, ViewNode.ShowTrans, ViewNode.ShowWeight, ViewNode.SmallIcon, " +
		"ViewNode.HideIcon, ViewNode.LabelWrapWidth, ViewNode.FontSize, ViewNode.FontFace, " +
		"ViewNode.FontStyle, ViewNode.Foreground, ViewNode.Background "+
		"FROM ViewNode, Node " +
		"WHERE ViewID = ? AND ( ViewNode.NodeID = Node.NodeID ) "+
		"AND ViewNode.CurrentStatus = "+ICoreConstants.STATUS_ACTIVE;

	/**
	 * SQL statement to grab all the active nodes in a specific view
	 */
	public final static String GET_NODEPOSITIONS_SUMMARY_QUERY =
		"SELECT Node.NodeID, Node.ModificationDate, " +
		"ViewNode.ViewID, ViewNode.XPos, ViewNode.YPos "+
		"FROM ViewNode, Node " +
		"WHERE ViewNode.ViewID = ? AND ( ViewNode.NodeID = Node.NodeID ) "+
		"AND ViewNode.CurrentStatus = "+ICoreConstants.STATUS_ACTIVE;

	/**
	 * SQL statement to join the node and the ViewNode tables to return Node data
	 * for nodes that have been modified by someone else since we last loaded the view
	 * from the database.
	 */
	public final static String GET_VIEWMODIFICATION_QUERY =
		"SELECT Node.NodeID, Node.ModificationDate, Node.LastModAuthor, ViewNode.CurrentStatus " +
		"FROM ViewNode, Node " +
		"WHERE ( ViewNode.ViewID = ? )" +
		"AND ( ViewNode.NodeID = Node.NodeID ) "+
		"AND ( ViewNode.CurrentStatus = " + ICoreConstants.STATUS_ACTIVE + " ) " +
		"AND (Node.LastModAuthor <> ? ) " +
		"AND ( Node.ModificationDate > ? )";
	
	/**
	 * SQL statement to return a count of NodeIDs in the ViewNode tables with the given ViewID, if active.
	 */
	public final static String GET_NODECOUNT_QUERY =
		"SELECT Count(NodeID) "+
		"FROM ViewNode "+
		"WHERE ViewID = ? "+
		"AND CurrentStatus = "+ICoreConstants.STATUS_ACTIVE;	
	
	/**
	 *  Inserts a new viewnode record in the database and returns a NodePosition object representing this record.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param view com.compendium.core.datamodel.View, the view to insert the node into.
	 *	@param node com.compendium.core.datamodel.NodeSummary, the node to insert into the view.
	 *	@param x, the x position of the node in the view.
	 *  @param y, the y position of the node in the view.
	 *  @param creation, the creation date for this nodeview record.
	 *  @param modification, the last modified date for this viewnode record.
	 *	@return NodePosition object.
	 *	@throws java.sql.SQLException
	 */
	public static NodePosition insert(DBConnection dbcon, View view, NodeSummary node, int x, int y,
						java.util.Date creation, java.util.Date modification, String userID) throws SQLException  {

		Connection con = dbcon.getConnection();
		if (con == null)
			return null;

		String sViewID = view.getId();
		String sNodeID = node.getId();

		// CHECK ENTRY NOT ALREADY THERE AND POSSIBLY JUST MARKED FOR DELETION
		// IF IT IS, RESTORE IT JUST TO BE ON THE SAFE SIDE
		NodePosition nodePos = getAnyNodePosition(dbcon, view.getId(), node.getId(), userID);
		if (nodePos != null) {			
			restore(dbcon, sViewID, sNodeID, userID);
			return nodePos;
		}

		PreparedStatement pstmt = con.prepareStatement(INSERT_VIEWNODE_QUERY);

		pstmt.setString(1, sViewID);
		pstmt.setString(2, sNodeID);
		pstmt.setInt(3, x);
		pstmt.setInt(4, y);
		pstmt.setDouble(5, creation.getTime());
		pstmt.setDouble(6, modification.getTime());
		pstmt.setInt(7, ICoreConstants.STATUS_ACTIVE);

		int nRowCount = pstmt.executeUpdate();

		pstmt.close();

		NodePosition pos = null;
		if (nRowCount > 0) {
			pos = new NodePosition(view, node, x, y, creation, modification);
			if (DBAudit.getAuditOn()) {
				DBAudit.auditViewNode(dbcon, DBAudit.ACTION_ADD, pos);
			}
		}

		return pos;
	}

	/**
	 *  Inserts a new viewnode record in the database and returns a NodePosition object representing this record.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param view the view to insert the node into.
	 *	@param node the node to insert into the view.
	 *	@param x the x position of the node in the view.
	 *  @param y the y position of the node in the view.
	 *  @param creation the creation date for this nodeview record.
	 *  @param modification the last modified date for this viewnode record.
	 *  @param sUserID the current user.
	 * 	@param bShowTags true if this node has the tags indicator draw.
	 *	@param bShowText true if this node has the text indicator drawn
	 * 	@param bShowTrans true if this node has the transclusion indicator drawn
	 * 	@param bShowWeight true if this node has the weight indicator displayed
	 * 	@param bSmallIcon true if this node is using a small icon
	 * 	@param bHideIcons true if this node is not displaying its icon
	 * 	@param nWrapWidth the node label wrap width used for this node in this view.
	 * 	@param nFontSize	the font size used for this node in this view
	 * 	@param sFontFace the font face used for this node in this view
	 * 	@param nFontStyle the font style used for this node in this view
	 * 	@param nForeground the foreground color used for this node in this view
	 * 	@param nBackground the background color used for this node in this view.
	 *	@return NodePosition object.
	 *	@throws java.sql.SQLException
	 */
	public static NodePosition insert(DBConnection dbcon, View view, NodeSummary node, int x, int y,
						java.util.Date creation, java.util.Date modification, String sUserID, boolean bShowTags,
						boolean bShowText, boolean bShowTrans, boolean bShowWeight, boolean bSmallIcon,
						boolean bHideIcon, int nWrapWidth, int nFontSize, String sFontFace,
						int nFontStyle, int nForeground, int nBackground) throws SQLException  {

		Connection con = dbcon.getConnection();
		if (con == null)
			return null;

		String sViewID = view.getId();
		String sNodeID = node.getId();

		// CHECK ENTRY NOT ALREADY THERE AND POSSIBLY JUST MARKED FOR DELETION
		// IF IT IS, RESTORE IT JUST TO BE ON THE SAFE SIDE
		NodePosition nodePos = getAnyNodePosition(dbcon, sViewID, sNodeID, sUserID);
		if (nodePos != null) {
			restore(dbcon, sViewID, sNodeID, sUserID);

			if (DBNode.getImporting() && DBNode.getUpdateTranscludedNodes()) {
				if ( updateFormatting(dbcon, sViewID, sNodeID,
						modification, bShowTags, bShowText, bShowTrans, bShowWeight, bSmallIcon,
						bHideIcon, nWrapWidth, nFontSize, sFontFace,
						nFontStyle, nForeground, nBackground, sUserID)) {
								
					nodePos.setFontFace(sFontFace);
					nodePos.setFontSize(nFontSize);
					nodePos.setFontStyle(nFontStyle);
					nodePos.setBackground(nBackground);
					nodePos.setForeground(nForeground);
					nodePos.setLabelWrapWidth(nWrapWidth);
					nodePos.setShowSmallIcon(bSmallIcon);
					nodePos.setHideIcon(bHideIcon);
					nodePos.setShowTags(bShowTags);
					nodePos.setShowText(bShowText);
					nodePos.setShowTrans(bShowTrans);
					nodePos.setShowWeight(bShowWeight);	
				}
			}
	
			return nodePos;
		}

		PreparedStatement pstmt = con.prepareStatement(INSERT_VIEWNODE_WITH_FORMATTING_QUERY);
		
		pstmt.setString(1, sViewID);
		pstmt.setString(2, sNodeID);
		pstmt.setInt(3, x);
		pstmt.setInt(4, y);
		pstmt.setDouble(5, creation.getTime());
		pstmt.setDouble(6, modification.getTime());
		pstmt.setInt(7, ICoreConstants.STATUS_ACTIVE);
		pstmt.setString(8, (bShowTags ? "Y" : "N") );
		pstmt.setString(9, (bShowText ? "Y" : "N") );
		pstmt.setString(10, (bShowTrans ? "Y" : "N") );
		pstmt.setString(11, (bShowWeight ? "Y" : "N") );
		pstmt.setString(12, (bSmallIcon ? "Y" : "N") );
		pstmt.setString(13, (bHideIcon ? "Y" : "N") );
		pstmt.setInt(14, nWrapWidth);
		pstmt.setInt(15, nFontSize);
		pstmt.setString(16, sFontFace);
		pstmt.setInt(17, nFontStyle);
		pstmt.setInt(18, nForeground);
		pstmt.setInt(19, nBackground);		

		int nRowCount = pstmt.executeUpdate();

		pstmt.close();

		NodePosition pos = null;
		if (nRowCount > 0) {
			pos = new NodePosition(view, node, x, y, creation, modification, bShowTags, 
					bShowText, bShowTrans, bShowWeight, bSmallIcon, bHideIcon, nWrapWidth,
					nFontSize, sFontFace, nFontStyle, nForeground, nBackground);
			if (DBAudit.getAuditOn()) {
				DBAudit.auditViewNode(dbcon, DBAudit.ACTION_ADD, pos);
			}
		}

		return pos;
	}
	
	/**
	 *  Update the formatting properties for the given node in all its views.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *  @param sNodeID the node id of the node to update the formatting in all view for.
	 *  @param modification the last modified date for this viewnode record.
	 * 	@param bShowTags true if this node has the tags indicator draw.
	 *	@param bShowText true if this node has the text indicator drawn
	 * 	@param bShowTrans true if this node has the transclusion indicator drawn
	 * 	@param bShowWeight true if this node has the weight indicator displayed
	 * 	@param bSmallIcon true if this node is using a small icon
	 * 	@param bHideIcons true if this node is not displaying its icon
	 * 	@param nWrapWidth the node label wrap width used for this node in this view.
	 * 	@param nFontSize	the font size used for this node in this view
	 * 	@param sFontFace the font face used for this node in this view
	 * 	@param nFontStyle the font style used for this node in this view
	 * 	@param nForeground the foreground color used for this node in this view
	 * 	@param nBackground the background color used for this node in this view.
	 * 	@param sUserID the id of the current user.
	 *	@return boolean, true if it was successful, else false.
	 *	@throws java.sql.SQLException
	 */
	public static boolean updateTransclusionFormatting(DBConnection dbcon, String sNodeID, 
						java.util.Date modification, boolean bShowTags,
						boolean bShowText, boolean bShowTrans, boolean bShowWeight, boolean bSmallIcon,
						boolean bHideIcon, int nWrapWidth, int nFontSize, String sFontFace,
						int nFontStyle, int nForeground, int nBackground, String sUserID) throws SQLException  {

		Connection con = dbcon.getConnection();
		if (con == null)
			return false;

		Vector vtNodes = new Vector();
		if (DBAudit.getAuditOn()) {
			vtNodes = getNodeNodes(dbcon, sNodeID, sUserID);
		}
		
		PreparedStatement pstmt = con.prepareStatement(UPDATE_TRANSCLUSION_FORMATTING_QUERY);

		pstmt.setDouble(1, modification.getTime());
		pstmt.setString(2, (bShowTags ? "Y" : "N") );
		pstmt.setString(3, (bShowText ? "Y" : "N") );
		pstmt.setString(4, (bShowTrans ? "Y" : "N") );
		pstmt.setString(5, (bShowWeight ? "Y" : "N") );
		pstmt.setString(6, (bSmallIcon ? "Y" : "N") );
		pstmt.setString(7, (bHideIcon ? "Y" : "N") );
		pstmt.setInt(8, nWrapWidth);
		pstmt.setInt(9, nFontSize);
		pstmt.setString(10, sFontFace);
		pstmt.setInt(11, nFontStyle);
		pstmt.setInt(12, nForeground);
		pstmt.setInt(13, nBackground);		
		pstmt.setString(14, sNodeID);
		
		int nRowCount = pstmt.executeUpdate();

		pstmt.close();

		if (nRowCount > 0) {
			if (DBAudit.getAuditOn()) {
				int count = vtNodes.size();
				NodePosition pos = null;
				for (int i=0; i<count;i++) {
					pos = (NodePosition)vtNodes.elementAt(i);				
					DBAudit.auditViewNode(dbcon, DBAudit.ACTION_EDIT, pos);
				}
			}
			return true;
		}

		return false;
	}	
	 
	/**
	 *  Update the formatting for the given node in the given view.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *  @param sViewID the node id of the view the node to update is in.
	 *  @param sNodeID the id of the node to update the formatting for
	 *  @param modification the last modified date for this viewnode record.
	 * 	@param bShowTags true if this node has the tags indicator draw.
	 *	@param bShowText true if this node has the text indicator drawn
	 * 	@param bShowTrans true if this node has the transclusion indicator drawn
	 * 	@param bShowWeight true if this node has the weight indicator displayed
	 * 	@param bSmallIcon true if this node is using a small icon
	 * 	@param bHideIcons true if this node is not displaying its icon
	 * 	@param nWrapWidth the node label wrap width used for this node in this view.
	 * 	@param nFontSize	the font size used for this node in this view
	 * 	@param sFontFace the font face used for this node in this view
	 * 	@param nFontStyle the font style used for this node in this view
	 * 	@param nForeground the foreground color used for this node in this view
	 * 	@param nBackground the background color used for this node in this view.
	 * 	@param sUserID the id of the current user.
	 *	@return boolean, true if it was successful, else false.
	 *	@throws java.sql.SQLException
	 */
	public static boolean updateFormatting(DBConnection dbcon, String sViewID, String sNodeID,
						java.util.Date modification, boolean bShowTags,
						boolean bShowText, boolean bShowTrans, boolean bShowWeight, boolean bSmallIcon,
						boolean bHideIcon, int nWrapWidth, int nFontSize, String sFontFace,
						int nFontStyle, int nForeground, int nBackground, String sUserID) throws SQLException  {

		Connection con = dbcon.getConnection();
		if (con == null)
			return false;
		
		NodePosition pos = null;
		if (DBAudit.getAuditOn()) {
			pos = getNodePosition(dbcon, sViewID, sNodeID, sUserID);
		}
				
		PreparedStatement pstmt = con.prepareStatement(UPDATE_FORMATTING_QUERY);

		pstmt.setDouble(1, modification.getTime());
		pstmt.setString(2, (bShowTags ? "Y" : "N") );
		pstmt.setString(3, (bShowText ? "Y" : "N") );
		pstmt.setString(4, (bShowTrans ? "Y" : "N") );
		pstmt.setString(5, (bShowWeight ? "Y" : "N") );
		pstmt.setString(6, (bSmallIcon ? "Y" : "N") );
		pstmt.setString(7, (bHideIcon ? "Y" : "N") );
		pstmt.setInt(8, nWrapWidth);
		pstmt.setInt(9, nFontSize);
		pstmt.setString(10, sFontFace);
		pstmt.setInt(11, nFontStyle);
		pstmt.setInt(12, nForeground);
		pstmt.setInt(13, nBackground);		
		pstmt.setString(14, sNodeID);
		pstmt.setString(15, sViewID);		
		
		int nRowCount = pstmt.executeUpdate();
		
		pstmt.close();

		if (nRowCount > 0) {
			if (DBAudit.getAuditOn()) {
				DBAudit.auditViewNode(dbcon, DBAudit.ACTION_EDIT, pos);
			}		
			
			return true;		
		}			
	
		return false;
	}		
	
	/**
	 *	Mark a node view as deleted, and returns true if successful.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param sViewID, the id of the view in the ViewNode record to mark as deleted.
	 *	@param sNodeID, the id of the node in the ViewNode record to mark as deleted.
	 *	@return boolean, true if it was successful, else false.
	 *	@throws java.sql.SQLException
	 */
	public static boolean delete(DBConnection dbcon, String sViewID, String sNodeID, String userID) throws SQLException	{

		Connection con = dbcon.getConnection() ;
		if (con == null)
			return false ;

		// IF AUDITING, STORE DATA
		NodePosition pos = null;
		if (DBAudit.getAuditOn()) {
			pos = DBViewNode.getNodePosition(dbcon, sViewID, sNodeID, userID);
		}

		PreparedStatement pstmt = con.prepareStatement(DELETE_VIEWNODE_QUERY) ;
		pstmt.setString(1, sViewID) ;
		pstmt.setString(2, sNodeID) ;

		int nRowCount = pstmt.executeUpdate();
		pstmt.close();
		if (nRowCount > 0) {
			if (DBAudit.getAuditOn() && pos != null) {
				DBAudit.auditViewNode(dbcon, DBAudit.ACTION_DELETE, pos);
			}

			return true;
		}
		return false;
	}

	/**
	 *	Mark a ViewNode record as active, and returns true if successful.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param sViewID, the id of the view in the ViewNode record to mark as active.
	 *	@param sNodeID, the id of the node in the ViewNode record to mark as active.
	 *	@return boolean, true if it was successful, else false.
	 *	@throws java.sql.SQLException
	 */
	public static NodePosition restore(DBConnection dbcon, String sViewID, String sNodeID, String userID) throws SQLException	{

		NodePosition pos = null;

		Connection con = dbcon.getConnection() ;
		if (con == null)
			return pos ;

		PreparedStatement pstmt = con.prepareStatement(RESTORE_VIEWNODE_QUERY) ;
		pstmt.setString(1, sViewID);
		pstmt.setString(2, sNodeID);

		int nRowCount = pstmt.executeUpdate();
		pstmt.close();

		if (nRowCount > 0) {
			pos = DBViewNode.getNodePosition(dbcon, sViewID, sNodeID, userID);

			if (DBAudit.getAuditOn() && pos != null) {
				DBAudit.auditViewNode(dbcon, DBAudit.ACTION_RESTORE, pos);
			}
			return pos;
		}
		return pos;
	}

	/**
	 *	Restore all nodes in the given view, and returns true if successful
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param sViewID, the id of the view to mark records as active for.
	 *	@return boolean, true if it was successful, else false.
	 *	@throws java.sql.SQLException
	 */
	public static boolean restoreView(DBConnection dbcon, String sViewID, String userID) throws SQLException	{

		Connection con = dbcon.getConnection() ;
		if (con == null)
			return false ;

		// STORE DATA FOR RESTORING NODES AND IF AUDITING
		Vector data = getViewNodes(dbcon, sViewID);

		PreparedStatement pstmt = con.prepareStatement(RESTORE_VIEW_QUERY) ;
		pstmt.setString(1, sViewID);

		int nRowCount = pstmt.executeUpdate();

		pstmt.close();
		if (nRowCount > 0 && data != null) {

			// RESTORE ALL NODES ASSOCIATED WITH THIS VIEW
			int count = data.size();
			NodePosition nodePos = null;

			for (int i=0; i< count; i++) {
				Vector next = (Vector)data.elementAt(i);
				String sNodeID = (String)next.elementAt(1);
				DBNode.restore(dbcon, sNodeID, userID);

				// AUDIT IF REQUIRED
				if (DBAudit.getAuditOn()) {
					int nX = ((Integer)next.elementAt(2)).intValue();
					int nY = ((Integer)next.elementAt(3)).intValue();
					Date created = (Date)next.elementAt(4);
					Date modified = (Date)next.elementAt(5);

					View view = View.getView(sViewID);
					NodeSummary node = DBNode.getNodeSummary(dbcon, sNodeID, userID);
					nodePos = new NodePosition(view, node, nX, nY, created, modified);

					DBAudit.auditViewNode(dbcon, DBAudit.ACTION_RESTORE, nodePos);
				}
			}

			return true;
		}
		return false;
	}

	/**
	 *	Purge a node view, and returns true if successful.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param sViewID, the id of the view in the ViewNode record to delete.
	 *	@param sNodeID, the id of the node in the ViewNode record to delete.
	 *	@return boolean, true if it was successful, else false.
	 *	@throws java.sql.SQLException
	 */
	public static boolean purge(DBConnection dbcon, String sViewID, String sNodeID, String userID) throws SQLException	{

		Connection con = dbcon.getConnection() ;
		if (con == null)
			return false ;

		// IF AUDITING, STORE DATA
		NodePosition pos = null;
		if (DBAudit.getAuditOn()) {
			pos = DBViewNode.getNodePosition(dbcon, sViewID, sNodeID, userID);
		}

		PreparedStatement pstmt = con.prepareStatement(PURGE_VIEWNODE_QUERY) ;
		pstmt.setString(1, sViewID);
		pstmt.setString(2, sNodeID);

		int nRowCount = pstmt.executeUpdate();
		pstmt.close();
		if (nRowCount > 0) {
			if (DBAudit.getAuditOn() && pos != null) {
				DBAudit.auditViewNode(dbcon, DBAudit.ACTION_PURGE, pos);
			}

			return true;
		}
		return false;
	}

	/**
	 *	Purge node views with the given viewid, and returns true if successful.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param sViewID, the id of the view in the ViewNode table to delete records for.
	 *	@return boolean, true if it was successful, else false.
	 *	@throws java.sql.SQLException
	 */
	public static boolean purgeView(DBConnection dbcon, String sViewID, String userID) throws SQLException	{

		Connection con = dbcon.getConnection() ;
		if (con == null)
			return false ;

		// IF AUDITING, STORE DATA
		Vector data = null;
		if (DBAudit.getAuditOn()) {
			data = DBViewNode.getDeletedViewNodes(dbcon, sViewID, userID);
		}

		PreparedStatement pstmt = con.prepareStatement(PURGE_VIEW_QUERY) ;
		pstmt.setString(1, sViewID);

		int nRowCount = pstmt.executeUpdate();
		pstmt.close();
		if (nRowCount > 0) {
			if (DBAudit.getAuditOn() && data != null) {
				int count = data.size();
				for (int i=0; i<count; i++) {
					DBAudit.auditViewNode(dbcon, DBAudit.ACTION_PURGE, (NodePosition)data.elementAt(i));
				}
			}

			return true;
		}
		return false;
	}

	/**
	 *	Purge all node views with the given home viewid, and returns true if successful
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param sViewID, the id of the home view to delete records for.
	 *	@return boolean, true if it was successful, else false.
	 *	@throws java.sql.SQLException
	 */
	public static boolean purgeHomeView(DBConnection dbcon, String sViewID, String userID) throws SQLException	{

		Connection con = dbcon.getConnection() ;
		if (con == null)
			return false ;

		// IF AUDITING, STORE DATA
		Vector data = null;
		if (DBAudit.getAuditOn()) {
			data = DBViewNode.getDeletedViewNodes(dbcon, sViewID, userID);
		}

		PreparedStatement pstmt = con.prepareStatement(PURGE_HOMEVIEW_QUERY) ;
		pstmt.setString(1, sViewID);
		pstmt.setString(2, sViewID);

		int nRowCount = pstmt.executeUpdate();
		pstmt.close();
		if (nRowCount > 0) {
			if (DBAudit.getAuditOn() && data != null) {
				int count = data.size();
				for (int i=0; i<count; i++) {
					DBAudit.auditViewNode(dbcon, DBAudit.ACTION_PURGE, (NodePosition)data.elementAt(i));
				}
			}

			return true;
		}
		return false;
	}

	/**
	 *	Mark all the records for a given View as deleted and returns true if successful.
	 *	@param dbcon the DBConnection object to access the database with.
	 *	@param sViewID the id of the view in the ViewNode table to mark records as deleted for.
	 *  @param sUserID the id of the current user.
	 *  @param vtUsers a list of all UserProfile objects so we don't delete their HomeView, inbox etc.
	 *	@return boolean, true if it was successful, else false.
	 *	@throws java.sql.SQLException
	 */
	public static boolean deleteView(DBConnection dbcon, String sViewID, String sUserID, Vector vtUsers) throws SQLException {

		Connection con = dbcon.getConnection() ;
		if (con == null)
			return false ;

		//delete the undertlying nodes and links only if the view doesnt appear in more than one views

		Vector vtViews = new Vector(51);
		for(Enumeration e = DBViewNode.getViews(dbcon, sViewID, sUserID);e.hasMoreElements();) {
			View view = (View)e.nextElement();
			if (view == null) {
				continue;
			}
			// don't count the situation when the view contains itself - bz
			// this fix will not work though when a map contains a map that contains
			// the first map.  Need some kind of recursion here. - bz
			if (!view.getId().equals(sViewID))
				vtViews.addElement(view);
		}

		if(vtViews.size() > 1)
			return false;

		//get all the nodes in the view and delete them from the DBNode Table
		// what if we hold off doing this for now ... bz
		Vector data2 =  DBViewNode.getNodePositions(dbcon, sViewID, sUserID);
		int count2 = data2.size();
		for(int i=0; i<count2; i++) {
			NodePosition nodePos = (NodePosition)data2.elementAt(i);
			DBNode.delete(dbcon, nodePos.getNode(), sViewID, sUserID, vtUsers);
		}

		//get all the links in the view and delete them from the DBLink Table
		Vector links = DBViewLink.getLinks(dbcon, sViewID);
		int count3 = links.size();
		for(int i=0; i< count3; i++) {
			Link link = (Link)links.elementAt(i);
			String linkId = link.getId();
			DBLink.delete(dbcon, linkId, sViewID);
		}

		// IF AUDITING, STORE DATA
		Vector data = null;
		if (DBAudit.getAuditOn()) {
			data = DBViewNode.getNodePositions(dbcon, sViewID, sUserID);
		}

		//* don't delete view-node structural data for now either! - bz
		PreparedStatement pstmt = con.prepareStatement(DELETE_VIEW_QUERY) ;

		pstmt.setString(1, sViewID) ;

		int nRowCount = pstmt.executeUpdate();
		pstmt.close();
		if (nRowCount > 0) {
			if (DBAudit.getAuditOn() && data != null) {
				int count = data.size();
				for (int i=0; i<count; i++) {
					NodePosition pos = (NodePosition)data.elementAt(i);
					DBAudit.auditViewNode(dbcon, DBAudit.ACTION_DELETE, pos);
				}
			}

			return true;
		}
		return false;
	}


// SETTERS

	/**
	 *	Sets the NodePosition in the given view
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param sViewID, the id of the view the node is in.
	 *	@param sNodeID, the id of the node to set the position for.
	 *	@param oPoint, the new position of the node in the view.
	 *	@return boolean, true if it was successful, else false.
	 *	@throws java.sql.SQLException
	 */
	public static boolean setNodePosition(DBConnection dbcon, String sViewID, String sNodeID, Point oPoint, String userID)
			throws SQLException {

		Connection con = dbcon.getConnection();
		if (con == null)
			return false;

		// IF AUDITING, STORE DATA
		NodePosition pos = null;
		if (DBAudit.getAuditOn()) {
			pos = DBViewNode.getNodePosition(dbcon, sViewID, sNodeID, userID);
		}

		PreparedStatement pstmt = con.prepareStatement(SET_NODE_POSITION_QUERY);

		pstmt.setInt(1, oPoint.x);
		pstmt.setInt(2, oPoint.y);
		pstmt.setDouble(3, (new Date()).getTime());
		pstmt.setString(4, sViewID);
		pstmt.setString(5, sNodeID);

		int nRowCount = pstmt.executeUpdate();

		pstmt.close();
		if (nRowCount > 0) {
			if (DBAudit.getAuditOn() && pos != null) {
				DBAudit.auditViewNode(dbcon, DBAudit.ACTION_EDIT, pos);
			}
			return true;
		}
		return false;
	}

	/**
	 * Set the font size for the given NodePosition objects
	 * @param dbcon the database connection
	 * @param sViewID the relevant view
	 * @param vtPositions the node position objects to set hte font size for
	 * @param nFontSize the font size to set
	 * @return if the font size was set.
	 * @throws SQLException
	 */
	public static boolean setFontSize(DBConnection dbcon, String sViewID, Vector vtPositions, int nFontSize) throws SQLException {

		Connection con = dbcon.getConnection();
		if (con == null)
			return false;
		
		String sNodesList = extractNodeIDs(vtPositions);
		 
		Statement stmt = con.createStatement();
		String sQuery = "UPDATE ViewNode " +
		"SET FontSize="+nFontSize+
		" WHERE ViewID='"+sViewID+
		"' AND ViewNode.CurrentStatus = "+ICoreConstants.STATUS_ACTIVE+
		" AND NodeID IN ("+sNodesList+")";
		
		int nRowCount = stmt.executeUpdate(sQuery);
		stmt.close();
		if (nRowCount > 0) {			
			if (DBAudit.getAuditOn()) {
				auditNodePositions(dbcon, vtPositions, DBAudit.ACTION_EDIT);
			}
			return true;
		}
		return false;
	}

	/**
	 * Set the font face for the given NodePosition objects
	 * @param dbcon the database connection
	 * @param sViewID the relevant view
	 * @param vtPositions the node position objects to set the font face for
	 * @param nFontFace the font face to set
	 * @return if the font face was set.
	 * @throws SQLException
	 */
	public static boolean setFontFace(DBConnection dbcon, String sViewID, Vector vtPositions, String nFontFace) throws SQLException {

		Connection con = dbcon.getConnection();
		if (con == null)
			return false;
		
		String sNodesList = extractNodeIDs(vtPositions);

		Statement stmt = con.createStatement();
		String sQuery = "UPDATE ViewNode " +
		"SET FontFace='"+nFontFace+
		"' WHERE ViewID='"+sViewID+
		"' AND ViewNode.CurrentStatus = "+ICoreConstants.STATUS_ACTIVE+
		" AND NodeID IN ("+sNodesList+")";
		
		int nRowCount = stmt.executeUpdate(sQuery);
		stmt.close();
		if (nRowCount > 0) {			
			if (DBAudit.getAuditOn()) {
				auditNodePositions(dbcon, vtPositions, DBAudit.ACTION_EDIT);
			}
			return true;
		}
		return false;
	}	
	
	/**
	 * Set the font style for the given NodePosition objects
	 * @param dbcon the database connection
	 * @param sViewID the relevant view
	 * @param vtPositions the node position objects to set the font style for
	 * @param nFontStyle the font style to set
	 * @return if the font style was set.
	 * @throws SQLException
	 */
	public static boolean setFontStyle(DBConnection dbcon, String sViewID, Vector vtPositions, int nFontStyle) throws SQLException {

		Connection con = dbcon.getConnection();
		if (con == null)
			return false;
		
		String sNodesList = extractNodeIDs(vtPositions);

		Statement stmt = con.createStatement();
		String sQuery = "UPDATE ViewNode " +
		"SET FontStyle="+nFontStyle+
		" WHERE ViewID='"+sViewID+
		"' AND ViewNode.CurrentStatus = "+ICoreConstants.STATUS_ACTIVE+
		" AND NodeID IN ("+sNodesList+")";
		
		int nRowCount = stmt.executeUpdate(sQuery);
		stmt.close();
		if (nRowCount > 0) {			
			if (DBAudit.getAuditOn()) {
				auditNodePositions(dbcon, vtPositions, DBAudit.ACTION_EDIT);
			}
			return true;
		}
		return false;
	}
	
	/**
	 * Set whether to show tags indicators for the given NodePosition objects
	 * @param dbcon the database connection
	 * @param sViewID the relevant view
	 * @param vtPositions the node position objects to set whether to showing tags indicators for
	 * @param bShow whether to show tags indicators
	 * @return if the property was set.
	 * @throws SQLException
	 */
	public static boolean setShowTagsIndicator(DBConnection dbcon, String sViewID, Vector vtPositions, boolean bShow) throws SQLException {

		Connection con = dbcon.getConnection();
		if (con == null)
			return false;
		
		String sNodesList = extractNodeIDs(vtPositions);
		
		Statement stmt = con.createStatement();
		String sQuery = "UPDATE ViewNode " +
		"SET ShowTags='"+(bShow ? "Y" : "N")+
		"' WHERE ViewID='"+sViewID+
		"' AND ViewNode.CurrentStatus = "+ICoreConstants.STATUS_ACTIVE+
		" AND NodeID IN ("+sNodesList+")";

		int nRowCount = stmt.executeUpdate(sQuery);

		stmt.close();
		if (nRowCount > 0) {			
			if (DBAudit.getAuditOn()) {
				auditNodePositions(dbcon, vtPositions, DBAudit.ACTION_EDIT);
			}
			return true;
		}
		return false;
	}	
	
	/**
	 * Set whether to show text indicators for the given NodePosition objects
	 * @param dbcon the database connection
	 * @param sViewID the relevant view
	 * @param vtPositions the node position objects to set whether to showing text indicators for
	 * @param bShow whether to show text indicators
	 * @return if the property was set.
	 * @throws SQLException
	 */
	public static boolean setShowTextIndicator(DBConnection dbcon, String sViewID, Vector vtPositions, boolean bShow) throws SQLException {

		Connection con = dbcon.getConnection();
		if (con == null)
			return false;
		
		String sNodesList = extractNodeIDs(vtPositions);
		
		Statement stmt = con.createStatement();
		String sQuery = "UPDATE ViewNode " +
		"SET ShowText='"+(bShow ? "Y" : "N")+
		"' WHERE ViewID='"+sViewID+
		"' AND ViewNode.CurrentStatus = "+ICoreConstants.STATUS_ACTIVE+
		" AND NodeID IN ("+sNodesList+")";

		int nRowCount = stmt.executeUpdate(sQuery);

		stmt.close();
		if (nRowCount > 0) {			
			if (DBAudit.getAuditOn()) {
				auditNodePositions(dbcon, vtPositions, DBAudit.ACTION_EDIT);
			}
			return true;
		}
		return false;
	}		
	
	/**
	 * Set whether to show weight indicators for the given NodePosition objects
	 * @param dbcon the database connection
	 * @param sViewID the relevant view
	 * @param vtPositions the node position objects to set whether to showing weight indicators for
	 * @param bShow whether to show weight indicators
	 * @return if the property was set.
	 * @throws SQLException
	 */
	public static boolean setShowWeightIndicator(DBConnection dbcon, String sViewID, Vector vtPositions, boolean bShow) throws SQLException {

		Connection con = dbcon.getConnection();
		if (con == null)
			return false;
		
		String sNodesList = extractNodeIDs(vtPositions);
		
		Statement stmt = con.createStatement();
		String sQuery = "UPDATE ViewNode " +
		"SET ShowWeight='"+(bShow ? "Y" : "N")+
		"' WHERE ViewID='"+sViewID+
		"' AND ViewNode.CurrentStatus = "+ICoreConstants.STATUS_ACTIVE+
		" AND NodeID IN ("+sNodesList+")";
		
		int nRowCount = stmt.executeUpdate(sQuery);

		stmt.close();
		if (nRowCount > 0) {			
			if (DBAudit.getAuditOn()) {
				auditNodePositions(dbcon, vtPositions, DBAudit.ACTION_EDIT);
			}
			return true;
		}
		return false;
	}
	
	/**
	 * Set whether to show transclusion indicators for the given NodePosition objects
	 * @param dbcon the database connection
	 * @param sViewID the relevant view
	 * @param vtPositions the node position objects to set whether to showing transclusion indicators for
	 * @param bShow whether to show transclusion indicators
	 * @return if the property was set.
	 * @throws SQLException
	 */
	public static boolean setShowTransIndicator(DBConnection dbcon, String sViewID, Vector vtPositions, boolean bShow) throws SQLException {

		Connection con = dbcon.getConnection();
		if (con == null)
			return false;
		
		String sNodesList = extractNodeIDs(vtPositions);
		
		Statement stmt = con.createStatement();
		String sQuery = "UPDATE ViewNode " +
		"SET ShowTrans='"+(bShow ? "Y" : "N")+
		"' WHERE ViewID='"+sViewID+
		"' AND ViewNode.CurrentStatus = "+ICoreConstants.STATUS_ACTIVE+
		" AND NodeID IN ("+sNodesList+")";

		int nRowCount = stmt.executeUpdate(sQuery);

		stmt.close();
		if (nRowCount > 0) {			
			if (DBAudit.getAuditOn()) {
				auditNodePositions(dbcon, vtPositions, DBAudit.ACTION_EDIT);
			}
			return true;
		}
		return false;
	}
	
	/**
	 * Set whether to show small icons for the given NodePosition objects
	 * @param dbcon the database connection
	 * @param sViewID the relevant view
	 * @param vtPositions the node position objects to set whether to showing small icons for
	 * @param bSmall whether to show small icons
	 * @return if the property was set.
	 * @throws SQLException
	 */
	public static boolean setShowSmallIcons(DBConnection dbcon, String sViewID, Vector vtPositions, boolean bSmall) throws SQLException {

		Connection con = dbcon.getConnection();
		if (con == null)
			return false;
		
		String sNodesList = extractNodeIDs(vtPositions);

		Statement stmt = con.createStatement();
		String sQuery = "UPDATE ViewNode " +
		"SET SmallIcon='"+(bSmall ? "Y" : "N")+
		"' WHERE ViewID='"+sViewID+
		"' AND ViewNode.CurrentStatus = "+ICoreConstants.STATUS_ACTIVE+
		" AND NodeID IN ("+sNodesList+")";

		int nRowCount = stmt.executeUpdate(sQuery);

		stmt.close();
		if (nRowCount > 0) {			
			if (DBAudit.getAuditOn()) {
				auditNodePositions(dbcon, vtPositions, DBAudit.ACTION_EDIT);
			}
			return true;
		}
		return false;
	}
	
	/**
	 * Set whether to hide icons for the given NodePosition objects
	 * @param dbcon the database connection
	 * @param sViewID the relevant view
	 * @param vtPositions the node position objects to set whether to hide icons for
	 * @param bHide whether to hide icons
	 * @return if the property was set.
	 * @throws SQLException
	 */
	public static boolean setHideIcons(DBConnection dbcon, String sViewID, Vector vtPositions, boolean bHide) throws SQLException {

		Connection con = dbcon.getConnection();
		if (con == null)
			return false;
		
		String sNodesList = extractNodeIDs(vtPositions);
		
		Statement stmt = con.createStatement();
		String sQuery = "UPDATE ViewNode " +
		"SET HideIcon='"+(bHide ? "Y" : "N")+
		"' WHERE ViewID='"+sViewID+
		"' AND ViewNode.CurrentStatus = "+ICoreConstants.STATUS_ACTIVE+
		" AND NodeID IN ("+sNodesList+")";

		int nRowCount = stmt.executeUpdate(sQuery);

		stmt.close();
		if (nRowCount > 0) {			
			if (DBAudit.getAuditOn()) {
				auditNodePositions(dbcon, vtPositions, DBAudit.ACTION_EDIT);
			}
			return true;
		}
		return false;
	}
	
	/**
	 * Set the wrap width for the given NodePosition objects
	 * @param dbcon the database connection
	 * @param sViewID the relevant view
	 * @param vtPositions the node position objects to set the wrap width for
	 * @param nWidth the wrap width to set
	 * @return if the property was set.
	 * @throws SQLException
	 */
	public static boolean setWrapWidth(DBConnection dbcon, String sViewID, Vector vtPositions, int nWidth) throws SQLException {

		Connection con = dbcon.getConnection();
		if (con == null)
			return false;
		
		String sNodesList = extractNodeIDs(vtPositions);
		
		Statement stmt = con.createStatement();
		String sQuery = "UPDATE ViewNode " +
		"SET LabelWrapWidth="+nWidth+
		" WHERE ViewID='"+sViewID+
		"' AND ViewNode.CurrentStatus = "+ICoreConstants.STATUS_ACTIVE+
		" AND NodeID IN ("+sNodesList+")";

		int nRowCount = stmt.executeUpdate(sQuery);

		stmt.close();
		if (nRowCount > 0) {			
			if (DBAudit.getAuditOn()) {
				auditNodePositions(dbcon, vtPositions, DBAudit.ACTION_EDIT);
			}
			return true;
		}
		return false;
	}
	
	/**
	 * Set the text foreground colour width for the given NodePosition objects
	 * @param dbcon the database connection
	 * @param sViewID the relevant view
	 * @param vtPositions the node position objects to set the text foreground colour for
	 * @param nColor the colour to set the text foreground to
	 * @return if the property was set.
	 * @throws SQLException
	 */
	public static boolean setTextForeground(DBConnection dbcon, String sViewID, Vector vtPositions, int nColour) throws SQLException {

		Connection con = dbcon.getConnection();
		if (con == null)
			return false;
		
		String sNodesList = extractNodeIDs(vtPositions);
		
		Statement stmt = con.createStatement();
		String sQuery = "UPDATE ViewNode " +
		"SET Foreground="+nColour+
		" WHERE ViewID='"+sViewID+
		"' AND ViewNode.CurrentStatus = "+ICoreConstants.STATUS_ACTIVE+
		" AND NodeID IN ("+sNodesList+")";

		int nRowCount = stmt.executeUpdate(sQuery);

		stmt.close();
		if (nRowCount > 0) {			
			if (DBAudit.getAuditOn()) {
				auditNodePositions(dbcon, vtPositions, DBAudit.ACTION_EDIT);
			}
			return true;
		}
		return false;
	}
	
	/**
	 * Set the text background colour width for the given NodePosition objects
	 * @param dbcon the database connection
	 * @param sViewID the relevant view
	 * @param vtPositions the node position objects to set the text background colour for
	 * @param nColor the colour to set the text background to
	 * @return if the property was set.
	 * @throws SQLException
	 */
	public static boolean setTextBackground(DBConnection dbcon, String sViewID, Vector vtPositions, int nColour) throws SQLException {

		Connection con = dbcon.getConnection();
		if (con == null)
			return false;
		
		String sNodesList = extractNodeIDs(vtPositions);
		
		Statement stmt = con.createStatement();
		String sQuery = "UPDATE ViewNode " +
		"SET Background="+nColour+
		" WHERE ViewID='"+sViewID+
		"' AND ViewNode.CurrentStatus = "+ICoreConstants.STATUS_ACTIVE+
		" AND NodeID IN ("+sNodesList+")";

		int nRowCount = stmt.executeUpdate(sQuery);

		stmt.close();
		if (nRowCount > 0) {			
			if (DBAudit.getAuditOn()) {
				auditNodePositions(dbcon, vtPositions, DBAudit.ACTION_EDIT);
			}
			return true;
		}
		return false;
	}
	
	/**
	 * Process the given list of NodePosition object for audit
	 * @param dbcon the database connection
	 * @param vtPositions the list of NodePosition objects to process
	 * @param type the audit type (e.g. DBAudit.EDIT etc)
	 * @throws SQLException
	 */
	private static void auditNodePositions(DBConnection dbcon, Vector vtPositions, int type) throws SQLException {
		int count = vtPositions.size();
		NodePosition position = null;				
		for (int j=0; j<count; j++) {					
			position = (NodePosition)vtPositions.elementAt(j);
			DBAudit.auditViewNode(dbcon, type, position);
		}		
	}
	
	/**
	 * Processes the list of NodePositions and produce a comma separated list of the node ids.
	 * @param vtPositions the list of NodePositions to process.
	 * @return the comma separates string of node ids.
	 */
	private static String extractNodeIDs(Vector vtPositions) {
		String sNodesList = "";		
		int count = vtPositions.size();
		NodePosition position = null;
		for (int i=0; i<count; i++) {
			position = (NodePosition)vtPositions.elementAt(i);
			if (i == (count-1)) {
				sNodesList += "'"+position.getNode().getId()+"'";
			} else {
				sNodesList += "'"+position.getNode().getId()+"',";
			}
		}
		return sNodesList;
	}
	
// GETTERS
	/**
	 *	Returns the NodePosition for the given active node reference in the given view.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param sViewID, the id of the view the node is in.
	 *	@param sNodeID, the id of the node to return the position for.
	 *	@return com.compendium.core.statamodel.NodePosition, the position of the node in the view.
	 *	@throws java.sql.SQLException
	 */
	public static NodePosition getNodePosition(DBConnection dbcon, String sViewID, String sNodeID, String userID) throws SQLException {

		Connection con = dbcon.getConnection();
		if (con == null)
			return null;

		PreparedStatement pstmt = con.prepareStatement(GET_VIEWNODE_QUERY);

		pstmt.setString(1, sViewID);
		pstmt.setString(2, sNodeID);

		ResultSet rs = null;
		
		try {
			rs = pstmt.executeQuery();
		} catch (Exception e){
			e.printStackTrace();
		}

		NodePosition nodePos = null;
		if (rs != null) {
			while (rs.next()) {
				String	sViewId		= rs.getString(1);
				String	sNodeId		= rs.getString(2);
				int		nX			= rs.getInt(3);
				int		nY			= rs.getInt(4);
				Date	created		= new Date(new Double(rs.getLong(5)).longValue());
				Date	modified	= new Date(new Double(rs.getLong(6)).longValue());

				boolean bShowTags 	= false; 
				String sShowTags	= rs.getString(7);
				if (sShowTags.equals("Y")) {
					bShowTags = true;
				}
				boolean bShowText	= false;
				String sShowText	= rs.getString(8);
				if (sShowText.equals("Y")) {
					bShowText = true;
				}
				boolean bShowTrans	= false;
				String sShowTrans	= rs.getString(9);
				if (sShowTrans.equals("Y")) {
					bShowTrans = true;
				}
				boolean bShowWeight = false;
				String sShowWeight	= rs.getString(10);
				if (sShowWeight.equals("Y")) {
					bShowWeight = true;
				}
				boolean bShowSmallIcon = false;
				String sShowSmallIcon = rs.getString(11);
				if (sShowSmallIcon.equals("Y")) {
					bShowSmallIcon = true;
				}
				boolean bHideIcon		= false;
				String sHideIcon		= rs.getString(12);
				if (sHideIcon.equals("Y")) {
					bHideIcon = true;
				}
				int	nLabelWrapWidth	= rs.getInt(13);
				int	nFontSize		= rs.getInt(14);
				String sFontFace	= rs.getString(15);
				int	nFontStyle		= rs.getInt(16);
				int	nForeground		= rs.getInt(17);
				int	nBackground		= rs.getInt(18);
				
				View view = View.getView(sViewId);
				NodeSummary node = DBNode.getNodeSummary(dbcon, sNodeId, userID);

				nodePos = new NodePosition(view, node, nX, nY, created, modified, bShowTags, bShowText, 
						bShowTrans, bShowWeight, bShowSmallIcon, bHideIcon, nLabelWrapWidth, 
						nFontSize, sFontFace, nFontStyle, nForeground, nBackground);
			}
		}

		pstmt.close();
		return nodePos;
	}

	/**
	 *	Returns the NodePosition for the given node reference in the given view regardless of its current status.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param sViewID, the id of the view the node is in.
	 *	@param sNodeID, the id of the node to return the position for.
	 *	@return com.compendium.core.statamodel.NodePosition, the position of the node in the view.
	 *	@throws java.sql.SQLException
	 */
	public static NodePosition getAnyNodePosition(DBConnection dbcon, String sViewID, String sNodeID, String userID) throws SQLException {
		Connection con = dbcon.getConnection();
		if (con == null)
			return null;

		PreparedStatement pstmt = con.prepareStatement(GET_ANYVIEWNODE_QUERY);

		pstmt.setString(1, sViewID);
		pstmt.setString(2, sNodeID);

		ResultSet rs = null;
		
		try {
			rs = pstmt.executeQuery();
		} catch (Exception e){
			e.printStackTrace();
		}

		NodePosition nodePos = null;
		if (rs != null) {
			while (rs.next()) {
				String	sViewId		= rs.getString(1);
				String	sNodeId		= rs.getString(2);
				int		nX			= rs.getInt(3);
				int		nY			= rs.getInt(4);
				Date	created		= new Date(new Double(rs.getLong(5)).longValue());
				Date	modified	= new Date(new Double(rs.getLong(6)).longValue());
				
				boolean bShowTags 	= false; 
				String sShowTags	= rs.getString(7);
				if (sShowTags.equals("Y")) {
					bShowTags = true;
				}
				boolean bShowText	= false;
				String sShowText	= rs.getString(8);
				if (sShowText.equals("Y)")) {
					bShowText = true;
				}
				boolean bShowTrans	= false;
				String sShowTrans	= rs.getString(9);
				if (sShowTrans.equals("Y")) {
					bShowTrans = true;
				}
				boolean bShowWeight = false;
				String sShowWeight	= rs.getString(10);
				if (sShowWeight.equals("Y")) {
					bShowTrans = true;
				}
				boolean bShowSmallIcon = false;
				String sShowSmallIcon = rs.getString(11);
				if (sShowSmallIcon.equals("Y")) {
					bShowSmallIcon = true;
				}
				boolean bHideIcon		= false;
				String sHideIcon		= rs.getString(12);
				if (sHideIcon.equals("Y")) {
					bHideIcon = true;
				}
				int	nLabelWrapWidth	= rs.getInt(13);
				int	nFontSize		= rs.getInt(14);
				String sFontFace	= rs.getString(15);
				int	nFontStyle		= rs.getInt(16);
				int	nForeground		= rs.getInt(17);
				int	nBackground		= rs.getInt(18);
				
				View view = View.getView(sViewId);
				NodeSummary node = DBNode.getNodeSummary(dbcon, sNodeId, userID);

				nodePos = new NodePosition(view, node, nX, nY, created, modified, bShowTags, bShowText, 						
						bShowTrans, bShowWeight, bShowSmallIcon, bHideIcon, nLabelWrapWidth, 
						nFontSize, sFontFace, nFontStyle, nForeground, nBackground);
			}
		}

		pstmt.close();
		return nodePos;
	}

	/**
	 *	Returns the NodePositions for the given node reference.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param sNodeID, the id of the node to return the positions for.
	 *	@return Vector, a list of <code>NodePosition</code>, for the given node.
	 *	@throws java.sql.SQLException
	 */
	public static Vector getNodeNodes(DBConnection dbcon, String sNodeID, String userID) throws SQLException {

		Vector positions = new Vector(51);

		Connection con = dbcon.getConnection();
		if (con == null)
			return positions;

		PreparedStatement pstmt = con.prepareStatement(GET_NODENODE_QUERY);
		pstmt.setString(1, sNodeID) ;
		ResultSet rs = null;
		
		try {
			rs = pstmt.executeQuery();
		} catch (Exception e){
			e.printStackTrace();
		}

		NodePosition nodePos = null;
		if (rs != null) {
			while (rs.next()) {
				String	sViewId		= rs.getString(1) ;
				String	sNodeId		= rs.getString(2) ;
				int		nX			= rs.getInt(3);
				int		nY			= rs.getInt(4);
				Date	created		= new Date(new Double(rs.getLong(5)).longValue());
				Date	modified	= new Date(new Double(rs.getLong(6)).longValue());
				int		status		= rs.getInt(7);

				// FORMATTING PROPERTIES
				String  showTags 	= rs.getString(8);
				boolean bShowTags = false;
				if (showTags.equals("Y"))
					bShowTags = true;
				String  showText 	= rs.getString(9);
				boolean bShowText = false;
				if (showText.equals("Y"))
					bShowText = true;
				String  showTrans 	= rs.getString(10);
				boolean bShowTrans = false;
				if (showTrans.equals("Y"))
					bShowTrans = true;
				String  showWeight 	= rs.getString(11);
				boolean bShowWeight = false;
				if (showWeight.equals("Y"))
					bShowWeight = true;
				String  smallIcon 	= rs.getString(12);
				boolean bSmallIcon = false;
				if (smallIcon.equals("Y"))
					bSmallIcon = true;
				String  hideIcon 	= rs.getString(13);
				boolean bHideIcon = false;
				if (hideIcon.equals("Y"))
					bHideIcon = true;
				int	nLabelWrapWidth		= rs.getInt(14);
				int	nFontSize			= rs.getInt(15);
				String sFontFace		= rs.getString(16);
				int	nFontStyle			= rs.getInt(17);
				int	nForeground			= rs.getInt(18);
				int	nBackground			= rs.getInt(19);
				
				View view = View.getView(sViewId);
				NodeSummary node = DBNode.getNodeSummary(dbcon, sNodeId, userID);
				nodePos = new NodePosition(view, node, nX, nY, created, modified, bShowTags, bShowText,
						bShowTrans, bShowWeight, bSmallIcon, bHideIcon, nLabelWrapWidth, nFontSize, sFontFace,
						nFontStyle, nForeground, nBackground);

				positions.addElement(nodePos);
			}
		}

		pstmt.close();
		return positions;
	}

	/**
	 *	Returns the deleted NodePositions for the given view reference.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param sViewID, the id of the view to return the records marked for deletion for.
	 *	@return Vector, a list of <code>NodePosition</code> for records marked for deletion in the given View.
	 *	@throws java.sql.SQLException
	 */
	public static Vector getDeletedViewNodes(DBConnection dbcon, String sViewID, String userID) throws SQLException {

		Vector positions = new Vector(51);

		Connection con = dbcon.getConnection();
		if (con == null)
			return positions;

		PreparedStatement pstmt = con.prepareStatement(GET_DELETEDVIEW_QUERY);
		pstmt.setString(1, sViewID) ;
		ResultSet rs = null;
		
		try {
			rs = pstmt.executeQuery();
		} catch (Exception e){
			e.printStackTrace();
		}

		NodePosition nodePos = null;
		if (rs != null) {
			while (rs.next()) {
				String	sViewId		= rs.getString(1) ;
				String	sNodeId		= rs.getString(2) ;
				int		nX			= rs.getInt(3);
				int		nY			= rs.getInt(4);
				Date	created		= new Date(new Double(rs.getLong(5)).longValue());
				Date	modified	= new Date(new Double(rs.getLong(6)).longValue());
				int		status		= rs.getInt(7);
				
				View view = View.getView(sViewId);
				NodeSummary node = DBNode.getNodeSummary(dbcon, sNodeId, userID);
				nodePos = new NodePosition(view, node, nX, nY, created, modified) ;

				positions.addElement(nodePos);
			}
		}

		pstmt.close();
		return positions;
	}

	/**
	 *	Returns the array of Node data for the given view id.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param sViewID, the id of the view to return node data for.
	 *	@return Vector, of Vector. Each inner Vector conatins:
	 *  <li>NodeID - String
	 * 	<li>ViewID - String
	 *	<li>xPos - Integer
	 *	<li>yPos - Integer
	 *  <li>CreationDate - Double (milliseconds)
	 * 	<li>ModificationDate - Double (milliseconds)
	 *	<li>Status - Integer
	 *	@throws java.sql.SQLException
	 */
	public static Vector getViewNodes(DBConnection dbcon, String sViewID) throws SQLException {

		Vector positions = new Vector(51);

		Connection con = dbcon.getConnection();
		if (con == null) {
			return positions;
		}

		PreparedStatement pstmt = con.prepareStatement(GET_NODEIDS_QUERY);
		pstmt.setString(1, sViewID) ;
		ResultSet rs = null;
		
		try {
			rs = pstmt.executeQuery();
		} catch (Exception e){
			e.printStackTrace();
		}

		if (rs != null) {
			while (rs.next()) {
				Vector inner = new Vector();
				inner.addElement(rs.getString(1));
				inner.addElement(rs.getString(2));
				inner.addElement(new Integer(rs.getInt(3)));
				inner.addElement(new Integer(rs.getInt(4)));
				inner.addElement(new Date(new Double(rs.getLong(5)).longValue()));
				inner.addElement(new Date(new Double(rs.getLong(6)).longValue()));
				inner.addElement(new Integer(rs.getInt(7)));

				positions.addElement(inner);
			}
		}
		pstmt.close();
		return positions;
	}

	/**
	 *	Returns the array of View objects for the given node references, if the record Status is active.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param sNodeID, the id of the node to return Views for.
	 *	@return Enumeration, a list of <code>View</code> objects the node is in.
	 *	@throws java.sql.SQLException
	 */
	public static Enumeration getViews(DBConnection dbcon, String sNodeID, String userID) throws SQLException {

		Vector vtViews = new Vector(51);

		Connection con = dbcon.getConnection();
		if (con == null) {
			return null;
		}

		PreparedStatement pstmt = con.prepareStatement(GET_VIEWS_QUERY);

		pstmt.setString(1, sNodeID) ;

		ResultSet rs = null;
		
		try {
			rs = pstmt.executeQuery();
		} catch (Exception e){
			e.printStackTrace();
		}

// Orignal code commented out.  The code that follows fetches views from the NodeSummary cache if available
// This is for performance improvement.  Some risk may exist that the NS cache is out of date with the DB?  -mlb		
/*		if (rs != null) {
			while (rs.next()) {
				String	sViewId	= rs.getString(1) ;
				IView view = DBNode.getView(dbcon, sViewId, userID);
				if (view != null)
					vtViews.addElement(view);
			}
		}
*/
		
		if (rs != null) {
			while (rs.next()) {
				String	sViewId	= rs.getString(1) ;
				if (NodeSummary.bIsInCache(sViewId)) {
					vtViews.addElement(NodeSummary.getNodeSummary(sViewId));
				} else {
					IView view = DBNode.getView(dbcon, sViewId, userID);
					if (view != null)
						vtViews.addElement(view);
				}
			}
		}
		pstmt.close();
		return vtViews.elements();
	}

	/**
	 *	Returns the count Views this node is still active in.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param sNodeID, the id of the node to return the View count for.
	 *	@return int, a count of the Views the given node is in.
	 *	@throws java.sql.SQLException
	 */
	public static int getActiveViewCount(DBConnection dbcon, String sNodeID) throws SQLException {

		Connection con = dbcon.getConnection();
		if (con == null) {
			return -1;
		}

		PreparedStatement pstmt = con.prepareStatement(GET_ACTIVEVIEWSCOUNT_QUERY);
		pstmt.setString(1, sNodeID);

		ResultSet rs = null;
		
		try {
			rs = pstmt.executeQuery();
		} catch (Exception e){
			e.printStackTrace();
		}
		int count = 0;
		if (rs != null) {
			while (rs.next()) {
				count = rs.getInt(1) ;
			}
		}
		pstmt.close();
		return count;
	}

	/**
	 *	Returns the array of View objects for the given node references, if the record Status is active.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param sNodeID, the id of the node to return Views for.
	 *	@return Enumeration, a list of <code>View</code> objects objects for the given Node id.
	 *	@throws java.sql.SQLException
	 */
	public static Enumeration getActiveViews(DBConnection dbcon, String sNodeID, String userID) throws SQLException {

		Vector vtViews = new Vector(51);

		Connection con = dbcon.getConnection();
		if (con == null) {
			return null;
		}

		PreparedStatement pstmt = con.prepareStatement(GET_ACTIVEVIEWS_QUERY);
		pstmt.setString(1, sNodeID);

		ResultSet rs = null;
		try {
			rs = pstmt.executeQuery();
		} catch (Exception e){
			e.printStackTrace();
		}
		if (rs != null) {
			while (rs.next()) {
				String	sViewId	= rs.getString(1) ;
				IView view = DBNode.getView(dbcon, sViewId, userID);
				vtViews.addElement(view);
			}
		}
		pstmt.close();
		return vtViews.elements();
	}

	/**
	 *	Returns a vector of View objects for the node given, if the Status is marked as deleted.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param sNodeID, the id of the node to return Views for.
	 *	@return Enumeration, a list of <code>View</code> objects for the given Node id.
	 *	@throws java.sql.SQLException
	 */
	public static Vector getDeletedViews(DBConnection dbcon, String nodeId, String userID) throws SQLException {

		Vector vtViews = new Vector(51);

		Connection con = dbcon.getConnection();
		if (con == null) {
			return null;
		}

		PreparedStatement pstmt = con.prepareStatement(GET_DELETEDVIEWS_QUERY);
		pstmt.setString(1, nodeId) ;

		ResultSet rs = null;
		try {
			rs = pstmt.executeQuery();
		} catch (Exception e){
			e.printStackTrace();
		}

		if (rs != null) {
			while (rs.next()) {
				String	sViewId	= rs.getString(1) ;
				IView view = DBNode.getView(dbcon, sViewId, userID);
				vtViews.addElement(view);
			}
		}
		pstmt.close();
		return vtViews;
	}

	/**
	 *	Returns a count of the number of View the node given id has been marked for deletion in.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param sNodeID, the id of the node to count the Views for.
	 *	@return int, a count of the list of number of View the node given id has been marked for deletion in.
	 *	@throws java.sql.SQLException
	 */
	public static int getDeletedViewCount(DBConnection dbcon, String nodeId) throws SQLException {

		int count = 0;

		Connection con = dbcon.getConnection();
		if (con == null) {
			return count;
		}

		PreparedStatement pstmt = con.prepareStatement(GET_DELETEDVIEWSCOUNT_QUERY);
		pstmt.setString(1, nodeId) ;

		ResultSet rs = null;
		try {
			rs = pstmt.executeQuery();
		} catch (Exception e){
			e.printStackTrace();
		}
		if (rs != null) {
			while (rs.next()) {
				count = rs.getInt(1);
			}
		}
		pstmt.close();
		return count;
	}

	/**
	 *	Returns the array of NodePosition objects in the given view.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param sViewID, the id of the View to return the NodePositions for.
	 *	@return Vector, a list of <code>NodePosition</code> objects for the given view id.
	 *	@throws java.sql.SQLException
	 */
	public static Vector getNodePositions(DBConnection dbcon, String sViewID, String userID) throws SQLException {

		Connection con = dbcon.getConnection();
		if (con == null)
			return null;

		PreparedStatement pstmt = con.prepareStatement(GET_NODEPOSITIONS_QUERY);
		pstmt.setString(1, sViewID);

		ResultSet rs = null;
		
		try {
			rs = pstmt.executeQuery(); 
		} catch (Exception e){
			e.printStackTrace();
		}

		Vector vtNodePos = new Vector(51);
		NodeSummary node = null ;

		if (rs != null) {
			View view = View.getView(sViewID);
			while (rs.next()) {
				node = DBNode.processNode(dbcon, rs, userID);
				
				String sViewId 		= rs.getString(11);	
				int			nX		= rs.getInt(12);
				int			nY		= rs.getInt(13);
				Date	created		= new Date(new Double(rs.getLong(14)).longValue());
				Date	modified	= new Date(new Double(rs.getLong(15)).longValue());
				
				// FORMATTING PROPERTIES
				String  showTags 	= rs.getString(16);
				boolean bShowTags = false;
				if (showTags.equals("Y"))
					bShowTags = true;
				String  showText 	= rs.getString(17);
				boolean bShowText = false;
				if (showText.equals("Y"))
					bShowText = true;
				String  showTrans 	= rs.getString(18);
				boolean bShowTrans = false;
				if (showTrans.equals("Y"))
					bShowTrans = true;
				String  showWeight 	= rs.getString(19);
				boolean bShowWeight = false;
				if (showWeight.equals("Y"))
					bShowWeight = true;
				String  smallIcon 	= rs.getString(20);
				boolean bSmallIcon = false;
				if (smallIcon.equals("Y"))
					bSmallIcon = true;
				String  hideIcon 	= rs.getString(21);
				boolean bHideIcon = false;
				if (hideIcon.equals("Y"))
					bHideIcon = true;
				int	nLabelWrapWidth		= rs.getInt(22);
				int	nFontSize			= rs.getInt(23);
				String sFontFace		= rs.getString(24);
				int	nFontStyle			= rs.getInt(25);
				int	nForeground			= rs.getInt(26);
				int	nBackground			= rs.getInt(27);
								
				// now that the node summary object is generated, create the node position object
				NodePosition nodePos = new NodePosition(view, node, nX, nY, created, modified, bShowTags, bShowText,
						bShowTrans, bShowWeight, bSmallIcon, bHideIcon, nLabelWrapWidth, nFontSize, sFontFace,
						nFontStyle, nForeground, nBackground) ;

				vtNodePos.addElement(nodePos);
			}
		}

		pstmt.close();
		return vtNodePos;
	}

	/**
	 *	Returns the array of NodePosition Summary objects in the given view.  Used to see if the view has been changed
	 *  by someone else.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param sViewID, the id of the View to return the NodePositions for.
	 *	@return Vector, a list of <code>NodePositionSummary</code> objects for the given view id.
	 *	@throws java.sql.SQLException
	 */
	public static Vector getNodePositionsSummary(DBConnection dbcon, String sViewID, String userID) throws SQLException {

		Connection con = dbcon.getConnection();
		if (con == null)
			return null;

		PreparedStatement pstmt = con.prepareStatement(GET_NODEPOSITIONS_SUMMARY_QUERY);
		pstmt.setString(1, sViewID);

		ResultSet rs = null;

		try {
			rs = pstmt.executeQuery();
		} catch (Exception e){
			e.printStackTrace();
		}

		Vector vtNodePosSummary = new Vector(51);
		NodeSummary node = null ;

		if (rs != null) {
			while (rs.next()) {
				String	sNodeId		= rs.getString(1);
				Date	oMDate		= new Date(new Double(rs.getLong(2)).longValue());
				String sViewId 		= rs.getString(3);
				int			nX		= rs.getInt(4);
				int			nY		= rs.getInt(5);

				NodePositionSummary nodePosSummary = new NodePositionSummary(sNodeId, sViewId, oMDate, nX, nY);

				vtNodePosSummary.addElement(nodePosSummary);
			}
		}

		pstmt.close();
		return vtNodePosSummary;
	}

	/**
	 *	Returns TRUE if the given view has been modified since it was last loaded.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param sViewID, the id of the View to check.
	 *	@param sUserName the current users's name
	 *	@param dLastViewModDate - date od last modification by another user that we're aware of
	 *	@return Boolean, TRUE if the view is 'dirty'.
	 *	@throws java.sql.SQLException
	 */
	public static Boolean bIsViewDirty(DBConnection dbcon, String sViewID, String sUserName, Date dLastViewModDate) throws SQLException {

		Connection con = dbcon.getConnection();
		if (con == null)
			return null;

		PreparedStatement pstmt = con.prepareStatement(GET_VIEWMODIFICATION_QUERY);
		pstmt.setString(1, sViewID);
		pstmt.setString(2, sUserName);
		pstmt.setDouble(3, dLastViewModDate.getTime());

		ResultSet rs = null;
		
		try {
			rs = pstmt.executeQuery();
		} catch (Exception e){
			e.printStackTrace();
		}
		
		if (rs != null) {
			int iDirtyNodeCount = 0;
			while (rs.next()) {
				String sID = rs.getString(1);
				Double dModDate = rs.getDouble(2);
				String sModAuthor = rs.getString(3);
				iDirtyNodeCount++;
			}
			if (iDirtyNodeCount > 0) {
				return true;
			}
		}
		return false;
	}

	/**
	 *	Returns the int of the count of NodePosition objects for the given view.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param sViewID, the id of the View to return the Node count for.
	 *	@return int, a count of the nodes in the given view id.
	 *	@throws java.sql.SQLException
	 */
	public static int getNodeCount(DBConnection dbcon, String sViewID) throws SQLException {

		Connection con = dbcon.getConnection();
		if (con == null)
			return 0;


		PreparedStatement pstmt = con.prepareStatement(GET_NODECOUNT_QUERY);
		pstmt.setString(1, sViewID);

		ResultSet rs = null;
		try {
			rs = pstmt.executeQuery();
		} catch (Exception e){
			e.printStackTrace();
		}

		int count = 0;
		if (rs != null) {
			while (rs.next()) {
				count = rs.getInt(1);
			}
		}

		pstmt.close();
		return count;
	}

	/**
	 * Does the View with the given id contain itself?
	 *
	 * @param dbCon com.compendium.core.db.management.DBConnection, the connectio to use to access the database.
	 * @param sViewID, the view id of the View to check.
	 * @return boolean, true if the View with the given id contains itself, else false.
	 * @exception java.sql.SQLException
 	 */
	public static boolean isViewContainsItself(DBConnection dbCon, String sViewID) throws SQLException {

		Connection con = dbCon.getConnection();

		PreparedStatement pstmt = con.prepareStatement(GET_VIEWNODE_QUERY);

		pstmt.setString(1, sViewID);
		pstmt.setString(2, sViewID);

		ResultSet rs = null;
		try {
			rs = pstmt.executeQuery();
		} catch (Exception e){
			e.printStackTrace();
		}
		int i = 0;

		if (rs != null) {
			while (rs.next()) {
				i++;
			}
		}
		pstmt.close();
		if (i > 0) {
			return true;
		}
		return false;
	}

	/** A Hashtable used to keep temporary data while checking node heirachy, in the 'checkParent' method.*/
	private static Hashtable htCheckViews = new Hashtable(51);

	/** A Hashtable of child maps for a view while being processed 'getChildMaps' method.*/
	private static Hashtable htChildMaps = new Hashtable(51);

	/**
	 * Check if the given view is uniquely in itself and nowhere else.
	 *
	 * @param dbCon com.compendium.core.db.management.DBConnection, the connectio to use to access the database.
	 * @param sParentID, the view id of the parent to the given View to check.
	 * @param oView com.compendium.core.datamodel.View, the View to check.
	 * @return boolean, true if the View with the given id is uniquely in itself, else false.
	 * @exception java.sql.SQLException
 	 */
	public static boolean hasUniqueAncestry(DBConnection dbcon, String sParentID, View oView, String userID) throws SQLException {

		htCheckViews.clear();
		htChildMaps.clear();

		boolean shouldDelete = false;

		String sViewID = oView.getId();
		if (getChildMaps(dbcon, oView, sViewID, userID)) {

			Enumeration views = DBViewNode.getActiveViews(dbcon, sViewID, userID);
			for(Enumeration e = views; e.hasMoreElements();) {
				View iview = (View)e.nextElement();

				if (iview != null && !iview.getId().equals(sParentID)) {
					checkParent(dbcon, (NodeSummary)iview, userID);
				}
			}

			/*
			for(Enumeration en = htCheckViews.elements();en.hasMoreElements();) {
				View jview = (View)en.nextElement();
				System.out.println("ANCESTER VIEW = "+jview.getId());
			}
			for(Enumeration en = htChildMaps.elements();en.hasMoreElements();) {
				View jview = (View)en.nextElement();
				System.out.println("CHILD VIEW = "+jview.getId());
			}
			*/

			// COMPARE ALL CHILD MAPS AGAINST ANCESTRY LIST
			// IF ANY VIEW IN THE ANCESTRY LIST IS NOT IN THE CHILDMAPS LIST
			// THEN THIS VIEW IS NOT UNIQUELY IN ITSELF
			int count = 0;
			boolean isUnique = true;
			for(Enumeration en = htCheckViews.elements();en.hasMoreElements();) {
				count++;
				View jview = (View)en.nextElement();
				if (!htChildMaps.containsKey(jview.getId())) {
					isUnique = false;
					break;
				}
			}
			if (count == 0)
				isUnique = false;

			if (isUnique)
				shouldDelete = true;
		}

		return shouldDelete;
	}

	/**
	 * Check the parent nodes of the given node, and store relevant view details.
	 *
	 * @param dbCon com.compendium.core.db.management.DBConnection, the connectio to use to access the database.
	 * @param sNodeID, the view id of the node to check.
	 * @return boolean, true if the View with the given id contains itself, else false.
	 * @exception java.sql.SQLException
 	 */
	public static void checkParent(DBConnection dbcon, NodeSummary sNodeID, String userID) throws SQLException {

		Enumeration views = DBViewNode.getActiveViews(dbcon, sNodeID.getId(), userID);
		for(Enumeration e = views;e.hasMoreElements();) {
			View view = (View)e.nextElement();
			if (view != null && !htCheckViews.containsKey(view.getId())) {
				htCheckViews.put(view.getId(), view);
				checkParent(dbcon, (NodeSummary)view, userID);
			}
		}
	}

	/**
	 * Store the child maps for the given node id, and return if the given node conatains itself.
	 *
	 * @param dbCon com.compendium.core.db.management.DBConnection, the connectio to use to access the database.
	 * @param view com.compendium.core.datamodel.View, the view to process.
	 * @param checkNodeID, the node id of the node to check.
	 * @return boolean, true if the View with the given id contains itself, else false.
	 * @exception java.sql.SQLException
 	 */
	public static boolean getChildMaps(DBConnection dbcon, View view, String checkNodeID, String userID) throws SQLException {

		boolean containsSelf = false;

		Vector nodePositions = DBViewNode.getNodePositions(dbcon, view.getId(), userID);
		int count = nodePositions.size();
		for(int i=0; i<count; i++) {
			NodeSummary node = (NodeSummary)((NodePosition)nodePositions.elementAt(i)).getNode();
			if ( View.isViewType(node.getType()) ) {
				if (!htChildMaps.containsKey((Object)node.getId())) {
					htChildMaps.put(node.getId(), (View)node);
					containsSelf = getChildMaps(dbcon, (View)node, checkNodeID, userID);
				}
			}

			if (node.getId().equals(checkNodeID))
				containsSelf = true;
		}
		return containsSelf;
	}
}
