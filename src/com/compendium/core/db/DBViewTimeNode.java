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
 * The DBViewTimeNode class serves as the interface layer between the NodePositionTime objects
 * and the ViewTimeNode table in the database.
 *
 * @author	Michelle Bachler
 */
public class DBViewTimeNode {

// AUDITED

	/** SQL statement to insert a new ViewTimeNode Record into the ViewTimeNode table.*/
	public final static String INSERT_VIEWTIMENODE_QUERY =
		"INSERT INTO ViewTimeNode (ViewTimeNodeID, ViewID, NodeID, TimeToShow, TimeToHide, XPos, YPos, CreationDate, ModificationDate, CurrentStatus) "+
		"VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ";
	
	// MARK AS ACTIVE - RESTORE
	/** SQL statement to mark ViewimeNode record as active with the given ViewID and NodeID.*/
	public final static String RESTORE_VIEWTIMENODE_QUERY =
		"UPDATE ViewTimeNode "+
		"SET CurrentStatus = "+ICoreConstants.STATUS_ACTIVE+
		" WHERE ViewTimeNodeID = ?";
	
	/** SQL statement to update a record*/
	public final static String UPDATE_VIEWTIMENODE_QUERY =
		"UPDATE ViewTimeNode "+
		"SET TimeToShow = ?, TimeToHide = ?, XPos = ?, YPos = ?, ModificationDate = ?"+
		" WHERE ViewTimeNodeID = ?";
	
	/** SQL statement to delete a record*/
	public final static String DELETE_VIEWTIMENODE_QUERY =
		"DELETE FROM ViewTimeNode "+
		"WHERE ViewTimeNodeID = ?";
	
// UNAUDITED
	
	/** SQL statement to return the ViewTimeNode records with the given ViewID, NodeID and TimeToShow, if active.*/
	public final static String GET_VIEWTIMENODE_QUERY =
		"SELECT ViewID, NodeID, TimeToShow, TimeToHide, XPos, YPos, CreationDate, ModificationDate "+
		"FROM ViewTimeNode "+
		"WHERE ViewTimeNodeID = ? "+
		"AND CurrentStatus = "+ICoreConstants.STATUS_ACTIVE;
	
	/**
	 * SQL statement to join the Node and the ViewTimeNode tables to return NodePositionTime objects
	 * in a given view, if the ViewTimeNode record was active.
	 */
	public final static String GET_NODETIMES_QUERY =
		"SELECT Node.NodeID, Node.NodeType, Node.ExtendedNodeType, Node.OriginalID, Node.Author, " +
		"Node.CreationDate, Node.ModificationDate, Node.Label, Node.Detail, Node.LastModAuthor, "+
		"viewTimeNode.ViewTimeNodeID, ViewTimeNode.ViewID, ViewTimeNode.TimeToShow, ViewTimeNode.TimeToHide, ViewTimeNode.XPos, ViewTimeNode.YPos, "+
		"ViewTimeNode.CreationDate, ViewTimeNode.ModificationDate " +
		"FROM ViewTimeNode, Node " +
		"WHERE ViewTimeNode.ViewID = ? AND ( ViewTimeNode.NodeID = Node.NodeID ) "+
		"AND ViewTimeNode.CurrentStatus = "+ICoreConstants.STATUS_ACTIVE;
	
	/** SQL statement to return the ViewTimeNode records with the given ViewID, NodeID and TimeToShow, whatever the Status.*/
	public final static String GET_ANYVIEWNODE_QUERY =
		"SELECT ViewID, NodeID, TimeToShow, TimeToHide, XPos, YPos, CreationDate, ModificationDate, CurrentStatus " +
		"FROM ViewTimeNode "+
		"WHERE ViewTimeNodeID = ?";

	/** SQL statement to return whether the record with the given given ViewID, NodeID and TimeToShow exists and is marked for deletion.*/
	public final static String GET_EXISTS_QUERY =
		"SELECT CurrentStatus " +
		"FROM ViewTimeNode "+
		"WHERE ViewTimeNodeID = ?";

	/**
	 *  Inserts a new viewtimenode record in the database and returns a NodePositionTime object representing this record.
	 *
	 *	@param dbcon the DBConnection object to access the database with.
	 *	@param view the view to insert the node into.
	 *	@param node the node to insert into the view.
	 *	@param nTimeToShow the time to show the node.
	 *	@param nTimeToHide the time to hide the node.
	 *	@param x the x position of the node in the view.
	 *  @param y the y position of the node in the view.
	 *  @param userID the id of the current user.
	 *	@return NodePositionTime object.
	 *	@throws java.sql.SQLException
	 */
	public static NodePositionTime insert(DBConnection dbcon, String sViewTimeNodeID, View view, NodeSummary node, 
				long nTimeToShow, long nTimeToHide, int x, int y, String userID) throws SQLException  {

		Connection con = dbcon.getConnection();
		if (con == null)
			return null;

		Date now = new Date();
		double time = now.getTime();
		
		String sViewID = view.getId();
		String sNodeID = node.getId();

		PreparedStatement pstmt = con.prepareStatement(INSERT_VIEWTIMENODE_QUERY);

		pstmt.setString(1, sViewTimeNodeID);
		pstmt.setString(2, sViewID);
		pstmt.setString(3, sNodeID);
		pstmt.setLong(4, nTimeToShow);
		pstmt.setLong(5, nTimeToHide);
		pstmt.setInt(6, x);
		pstmt.setInt(7, y);
		pstmt.setDouble(8, time);
		pstmt.setDouble(9, time);
		pstmt.setInt(10, ICoreConstants.STATUS_ACTIVE);

		int nRowCount = pstmt.executeUpdate();

		pstmt.close();

		NodePositionTime pos = null;
		if (nRowCount > 0) {
			pos = new NodePositionTime(sViewTimeNodeID, view, node, nTimeToShow, nTimeToHide, x, y, now, now);
			if (DBAudit.getAuditOn()) {
				DBAudit.auditViewTimeNode(dbcon, DBAudit.ACTION_ADD, pos);
			}
		}

		return pos;
	}

	/**
	 *  Update a viewtimenode record in the database and returns a NodePositionTime object representing this record.
	 *
	 *	@param dbcon the DBConnection object to access the database with.
	 *	@param sViewTimeNodeID the unique id for this new record.
	 *	@param view the view to insert the node into.
	 *	@param node the node to insert into the view.
	 *	@param nTimeToShow the time to show the node.
	 *	@param nTimeToHide the time to hide the node.
	 *	@param x the x position of the node in the view.
	 *  @param y the y position of the node in the view.
	 *  @param userID the id of the current user.
	 *	@return NodePosition object.
	 *	@throws java.sql.SQLException
	 */
	public static NodePositionTime update(DBConnection dbcon, String sViewTimeNodeID, 
				View view, NodeSummary node, long nTimeToShow, long nTimeToHide, int x, int y,
						String sUserID) throws SQLException  {

		Connection con = dbcon.getConnection();
		if (con == null)
			return null;

		//String sViewID = view.getId();
		//String sNodeID = node.getId();

		PreparedStatement pstmt = con.prepareStatement(UPDATE_VIEWTIMENODE_QUERY);

		double time = new Date().getTime();
		
		pstmt.setLong(1, nTimeToShow);
		pstmt.setLong(2, nTimeToHide);
		pstmt.setInt(3, x);
		pstmt.setInt(4, y);
		pstmt.setDouble(5, time);
		pstmt.setString(6, sViewTimeNodeID);

		int nRowCount = pstmt.executeUpdate();

		pstmt.close();

		NodePositionTime pos = null;
		if (nRowCount > 0) {
			pos = DBViewTimeNode.getNodeTime(dbcon, sViewTimeNodeID, sUserID);
			if (DBAudit.getAuditOn()) {
				DBAudit.auditViewTimeNode(dbcon, DBAudit.ACTION_EDIT, pos);
			}
		}

		return pos;
	}
	
	/**
	 *	Delete a ViewTimeNode record.
	 *
	 *	@param dbcon the DBConnection object to access the database with.
	 *	@param sViewTimeNodeID the id of the record to delete.
	 *  @param userID the id of the current user.
	 *	@throws java.sql.SQLException
	 */
	public static void delete(DBConnection dbcon, String sViewTimeNodeID, String userID) throws SQLException	{

		Connection con = dbcon.getConnection() ;
		if (con == null)
			throw new SQLException("Connection is null");

		PreparedStatement pstmt = con.prepareStatement(DELETE_VIEWTIMENODE_QUERY) ;
		pstmt.setString(1, sViewTimeNodeID);
		pstmt.executeUpdate();
		pstmt.close();
	}

	
// GETTERS
	/**
	 *	Returns the NodePosition for the given active node reference in the given view.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param sViewID, the id of the view the node is in.
	 *	@param sNodeID, the id of the node to return the position for.
	 *  @param userID the id of the current user.
	 *	@return com.compendium.core.statamodel.NodePosition, the position of the node in the view.
	 *	@throws java.sql.SQLException
	 */
	public static NodePositionTime getNodeTime(DBConnection dbcon, String sViewTimeNodeID, String userID) throws SQLException {

		Connection con = dbcon.getConnection();
		if (con == null)
			return null;

		PreparedStatement pstmt = con.prepareStatement(GET_VIEWTIMENODE_QUERY);

		pstmt.setString(1, sViewTimeNodeID);

		ResultSet rs = null;
		
		try {
			rs = pstmt.executeQuery();
		} catch (Exception e){
			e.printStackTrace();
		}

		NodePositionTime nodePos = null;
		if (rs != null) {
			while (rs.next()) {
				String	sViewId		= rs.getString(1);
				String	sNodeId		= rs.getString(2);
				long	nShow		= rs.getLong(3);
				long	nHide		= rs.getLong(4);
				int		nX			= rs.getInt(5);
				int		nY			= rs.getInt(6);
				Date	created		= new Date(new Double(rs.getLong(7)).longValue());
				Date	modified	= new Date(new Double(rs.getLong(8)).longValue());
				
				View view = MovieMapView.getView(sViewId);
				NodeSummary node = DBNode.getNodeSummary(dbcon, sNodeId, userID);

				nodePos = new NodePositionTime(sViewTimeNodeID, view, node, nShow, nHide, nX, nY, created, modified);
			}
		}

		pstmt.close();
		return nodePos;
	}
	
	/**
	 *	Returns whether a current record exists.
	 *
	 *	@param dbcon the DBConnection object to access the database with.
	 *	@param sViewTimeNodeID the id of the record to check.
	 *	@return boolean true if the record exists, else false.
	 *	@throws java.sql.SQLException
	 */
	public static boolean exists(DBConnection dbcon, String sViewTimeNodeID) throws SQLException {
		Connection con = dbcon.getConnection();
		if (con == null)
			throw new SQLException("Connection null");

		PreparedStatement pstmt = con.prepareStatement(GET_EXISTS_QUERY);

		pstmt.setString(1, sViewTimeNodeID);
		ResultSet rs = null;
		
		try {
			rs = pstmt.executeQuery();
		} catch (Exception e){
			e.printStackTrace();
		}

		if (rs != null) {
			return true;
		}

		pstmt.close();

		return false;
	}
	
	/**
	 *	Returns the NodePositionTime for the given node reference in the given view regardless of its current status.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param sViewTimeNodeID the id of the record to get.
	 *	@return com.compendium.core.statamodel.NodePosition, the position of the node in the view.
	 *	@throws java.sql.SQLException
	 */
	/*public static NodePositionTime getAnyNodeTime(DBConnection dbcon, String sViewTimeNodeID, String userID) throws SQLException {
		Connection con = dbcon.getConnection();
		if (con == null)
			throw new SQLException("Connection null");

		PreparedStatement pstmt = con.prepareStatement(GET_ANYVIEWNODE_QUERY);

		pstmt.setString(1, sViewTimeNodeID);

		ResultSet rs = null;
		
		try {
			rs = pstmt.executeQuery();
		} catch (Exception e){
			e.printStackTrace();
		}

		NodePositionTime nodePos = null;
		if (rs != null) {
			while (rs.next()) {
				String	sViewId		= rs.getString(1);
				String	sNodeId		= rs.getString(2);
				long	nShow		= rs.getLong(3);
				long	nHide		= rs.getLong(4);
				int		nX			= rs.getInt(5);
				int		nY			= rs.getInt(6);
				Date	created		= new Date(new Double(rs.getLong(7)).longValue());
				Date	modified	= new Date(new Double(rs.getLong(8)).longValue());
								
				View view = MovieMapView.getView(sViewId);
				NodeSummary node = DBNode.getNodeSummary(dbcon, sNodeId, userID);

				nodePos = new NodePositionTime(sViewTimeNodeID, view, node, nShow, nHide, nX, nY, created, modified);
			}
		}

		pstmt.close();
		return nodePos;
	}*/
	
	/**
	 *	Returns the array of NodePositionTime objects in the given view.
	 *
	 *	@param dbcon the DBConnection object to access the database with.
	 *	@param sViewID the id of the View to return the NodePositions for.
	 *  @param userID the id of the current user.
	 *	@return Vector a list of <code>NodePosition</code> objects for the given view id.
	 *	@throws java.sql.SQLException
	 */
	public static Vector getNodeTimes(DBConnection dbcon, String sViewID, String userID) throws SQLException {

		Connection con = dbcon.getConnection();
		if (con == null)
			throw new SQLException("Connection null");

		PreparedStatement pstmt = con.prepareStatement(GET_NODETIMES_QUERY);
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
			View view = MovieMapView.getView(sViewID);
			while (rs.next()) {
				node = DBNode.processNode(dbcon, rs, userID);
				
				String sViewTimeNodeID 	= rs.getString(11);	
				String sViewId 		= rs.getString(12);	
				long nShow			= rs.getLong(13);
				long nHide			= rs.getLong(14);
				int			nX		= rs.getInt(15);
				int			nY		= rs.getInt(16);
				Date	created		= new Date(new Double(rs.getLong(17)).longValue());
				Date	modified	= new Date(new Double(rs.getLong(18)).longValue());				
								
				// now that the node summary object is generated, create the node position object
				NodePosition nodePos = new NodePositionTime(sViewTimeNodeID, view, node, nShow, nHide, nX, nY, created, modified) ;

				vtNodePos.addElement(nodePos);
			}
		}

		pstmt.close();
		return vtNodePos;
	}
}
