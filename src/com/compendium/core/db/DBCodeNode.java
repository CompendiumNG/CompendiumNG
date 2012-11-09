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

import java.util.*;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.compendium.core.ICoreConstants;
import com.compendium.core.datamodel.*;
import com.compendium.core.db.management.*;

/**
 * The DBCodeNode class serves as the interface layer to the CodeNode table in
 * the database for the relationship between Codes and Nodes in the datamodel.
 * <p>
 * i.e. What Codes have been assigned to what Nodes.
 *
 * @author	rema and sajid / Michelle Bachler
 */
public class DBCodeNode {

// AUDITED

	/** SQL statement to insert a new CodeNode record (CodeID, NodeID).*/
	public final static String INSERT_NODECODE_QUERY =
		"INSERT INTO NodeCode (NodeID, CodeID) "+
		"VALUES (?, ?) ";

	/** SQL statement to delete all entries with the given CodeID.*/
	public final static String DELETE_CODE_QUERY =
		"DELETE "+
		"FROM NodeCode "+
		"WHERE CodeID = ? ";

	/** SQL statement to delete all entries that contain the given NodeID*/
	public final static String DELETE_NODE_QUERY =
		"DELETE " +
		"FROM NodeCode " +
		"WHERE NodeID = ? " ;

	/** SQL statement to delete the record for the CodeID and NodeID.*/
	public final static String DELETE_NODECODE_QUERY =
		DELETE_NODE_QUERY +
		"AND CodeID=?";

// UNAUDITED
	/** SQL statement to get all CodeIDs for the given NodeID.*/
	public final static String GET_CODES_QUERY =
		"SELECT CodeID "+
		"FROM NodeCode, Node "+
		"WHERE NodeCode.NodeID = Node.NodeID AND NodeCode.NodeID = ? AND Node.CurrentStatus = "+ICoreConstants.STATUS_ACTIVE;

	/** SQL statement to get all Active NodeIDs for the given CodeID.*/
	public final static String GET_NODES_QUERY =
		"SELECT NodeCode.NodeID " +
		"FROM NodeCode, Node  " +
		"WHERE NodeCode.NodeID = Node.NodeID AND NodeCode.CodeID = ? AND Node.CurrentStatus = "+ICoreConstants.STATUS_ACTIVE;

	/** SQL statement to get the NodeID for the given NodeID and CodeID. Used to check if a record already exists.*/
	public final static String GET_NODECODE_QUERY =
		"SELECT NodeID " +
		"FROM NodeCode " +
		"WHERE NodeID = ? AND CodeID = ?";

	/** SQL statement to get a count of all the NodeIDs for the given CodeID.*/
	public final static String GET_NODECOUNT_QUERY =
		"SELECT Count(NodeCode.NodeID) " +
		"FROM NodeCode, Node " +
		"WHERE NodeCode.NodeID = Node.NodeID AND NodeCode.CodeID = ? AND Node.CurrentStatus = "+ICoreConstants.STATUS_ACTIVE;

	/**
	 * 	Inserts a new node - code relationship record in the database and returns true if successful.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param sNodeID, the id of the node being added.
	 *	@param sCodeID, the id of the code assigned to the node.
	 *	@return boolean, true if it was successful, else false.
	 *	@throws java.sql.SQLException
	 */
	public static boolean insert(DBConnection dbcon, String sNodeID, String sCodeID)
		throws SQLException {

		Connection con = dbcon.getConnection();
		if (con == null)
			return false;


		// CHECK IF EXISITS
		PreparedStatement pstmt1 = con.prepareStatement(GET_NODECODE_QUERY);
		pstmt1.setString(1, sNodeID);
		pstmt1.setString(2, sCodeID);
		ResultSet rs = pstmt1.executeQuery();

		String nodeid = "";
		if (rs != null) {
			while (rs.next()) {
				nodeid	= rs.getString(1);
			}
		}

		// IF NODECODE EXISTS, RETURN
		if (!nodeid.equals("")) {
			return true;
		}

		PreparedStatement pstmt = con.prepareStatement(INSERT_NODECODE_QUERY);

 		pstmt.setString(1, sNodeID);
		pstmt.setString(2, sCodeID) ;

		int nRowCount = pstmt.executeUpdate();
		pstmt.close();

		if (nRowCount > 0) {
			if (DBAudit.getAuditOn())
				DBAudit.auditNodeCode(dbcon, DBAudit.ACTION_ADD, sNodeID, sCodeID);

			return true;
		} else
			return false;
	}

	/**
	 *	Marks the status of a node-code associations with the given CodeID as deleted and returns true if successful.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param sCodeID, the id of the code to delete records for.
	 *	@return boolean, true if it was successful, else false.
	 *	@throws java.sql.SQLException
	 */
	public static boolean delete(DBConnection dbcon, String sCodeID) throws SQLException {
		Connection con = dbcon.getConnection() ;
		if (con == null)
			return false;

		// IF AUDITING, STORE DATA
		Vector data = null;
		if (DBAudit.getAuditOn()) {
			data = DBCodeNode.getNodeIDs(dbcon, sCodeID);
		}

		PreparedStatement pstmt = con.prepareStatement(DELETE_CODE_QUERY) ;
		pstmt.setString(1, sCodeID) ;

		int nRowCount = pstmt.executeUpdate() ;
		pstmt.close();

		if (nRowCount > 0) {
			if (DBAudit.getAuditOn() && data != null) {
				int jcount = data.size();
				for (int j=0; j<jcount; j++) {
					DBAudit.auditNodeCode(dbcon, DBAudit.ACTION_DELETE, (String)data.elementAt(j), sCodeID);
				}
			}

			return true;
		}
		else
			return false;
	}

	/**
	 *	Marks the status of a node-code association of all node-codes with the given nodeId as deleted and returns true if successful
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param sNodeID, the id of the node to delete records for.
	 *	@return boolean, true if it was successful, else false.
	 *	@throws java.sql.SQLException
	 */
	public static boolean deleteNode(DBConnection dbcon, String sNodeID) throws SQLException {

		Connection con = dbcon.getConnection() ;
		if (con == null)
			return false;

		// IF AUDITING, STORE DATA
		Vector data = null;
		if (DBAudit.getAuditOn()) {
			data = DBCodeNode.getCodeIDs(dbcon, sNodeID);
		}

		PreparedStatement pstmt = con.prepareStatement(DELETE_NODE_QUERY);
		pstmt.setString(1, sNodeID) ;

		int nRowCount = pstmt.executeUpdate() ;
		pstmt.close();

		if (nRowCount > 0) {
			if (DBAudit.getAuditOn() && data != null) {
				int jcount = data.size();
				for (int j=0; j<jcount; j++) {
					DBAudit.auditNodeCode(dbcon, DBAudit.ACTION_DELETE, sNodeID, (String)data.elementAt(j));
				}
			}

			return true ;
		}
		else
			return false ;
	}

	/**
	 *	Marks the status of a node-code association, with the given CodeID and NodeID, as deleted and returns successful
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param sNodeID, the id of the node to delete the record for.
	 *	@param sCodeID, the id of the code to delete the record for.
	 *	@return boolean, true if it was successful, else false.
	 *	@throws java.sql.SQLException
	 */
	public static boolean deleteNodeCode(DBConnection dbcon, String sNodeID, String sCodeID) throws SQLException {

		Connection con = dbcon.getConnection() ;
		if (con == null)
			return false;

		PreparedStatement pstmt = con.prepareStatement(DELETE_NODECODE_QUERY) ;

		pstmt.setString(1, sNodeID) ;
		pstmt.setString(2, sCodeID) ;

		int nRowCount = pstmt.executeUpdate() ;
		pstmt.close();

		if (nRowCount > 0) {
			if (DBAudit.getAuditOn())
				DBAudit.auditNodeCode(dbcon, DBAudit.ACTION_DELETE, sNodeID, sCodeID);

			return true ;
		}
		else
			return false ;
	}

	/**
	 *	Returns a vector of the <code>Code</code> objects with the given NodeID.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param sNodeID, the id of the node to return the Codes for.
	 *	@return Vector, of Codes for the given node id.
	 *	@throws java.sql.SQLException
	 */
	public static Vector getCodes(DBConnection dbcon, String sNodeID) throws SQLException {
		Connection con = dbcon.getConnection() ;
		if (con == null)
			return null;

		PreparedStatement pstmt = con.prepareStatement(GET_CODES_QUERY) ;
		pstmt.setString(1, sNodeID);

		ResultSet rs = pstmt.executeQuery();

		Vector vtCodes = new Vector(51);

		if (rs != null) {
			while (rs.next()) {
				String	sCodeId	= rs.getString(1);
				Code code = DBCode.getCode(dbcon, sCodeId);
				vtCodes.addElement(code);
			}
		}
		pstmt.close();
		return vtCodes;
	}

	/**
	 *	Returns a vector of the Code ids for the given NodeID.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param sNodeID, the id of the node to return the code ids for.
	 *	@return Vector, of Code ids for the given node id.
	 *	@throws java.sql.SQLException
	 */
	public static Vector getCodeIDs(DBConnection dbcon, String sNodeID) throws SQLException {
		Connection con = dbcon.getConnection() ;
		if (con == null)
			return null;

		PreparedStatement pstmt = con.prepareStatement(GET_CODES_QUERY) ;
		pstmt.setString(1, sNodeID);

		ResultSet rs = pstmt.executeQuery();

		Vector vtCodes = new Vector(51);

		if (rs != null) {
			while (rs.next()) {
				String	sCodeID	= rs.getString(1);
				vtCodes.addElement(sCodeID);
			}
		}
		pstmt.close();
		return vtCodes;
	}

	/**
	 *	Returns a vector of the <code>NodeSummary</code> objects for the given code id.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param sCodeID, the id of the code to return the nodes for.
	 *	@return Vector, of Nodes for the given code id, else false.
	 *	@throws java.sql.SQLException
	 */
	public static Vector getNodes(DBConnection dbcon, String sCodeID, String userID) throws SQLException {

		Connection con = dbcon.getConnection() ;
		if (con == null)
			return null;

		PreparedStatement pstmt = con.prepareStatement(GET_NODES_QUERY) ;
		pstmt.setString(1, sCodeID);

		ResultSet rs = pstmt.executeQuery();

		Vector vtNodes = new Vector(51);

		if (rs != null) {

			while (rs.next()) {
				String	sNodeId	= rs.getString(1);
				NodeSummary node = null;
				node = DBNode.getNodeSummary(dbcon, sNodeId, userID);
				vtNodes.addElement(node);
			}
		}
		pstmt.close();

		return vtNodes;
	}

	/**
	 *	Returns a Vector of the node ids for the given code id.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param sCodeID, the id of the code to return the node ids for.
	 *	@return Vector, of node ids for the given code id.
	 *	@throws java.sql.SQLException
	 */
	public static Vector getNodeIDs(DBConnection dbcon, String sCodeID) throws SQLException {

		Connection con = dbcon.getConnection() ;
		if (con == null)
			return null;

		PreparedStatement pstmt = con.prepareStatement(GET_NODES_QUERY) ;
		pstmt.setString(1, sCodeID);

		ResultSet rs = pstmt.executeQuery();

		Vector vtNodes = new Vector(51);
		if (rs != null) {

			while (rs.next()) {
				String	sNodeID	= rs.getString(1);
				vtNodes.addElement( sNodeID );
			}
		}
		pstmt.close();

		return vtNodes;
	}

	/**
	 *	Returns a count of the node's with the given codeId
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param sCodeID, the id of the code to count the nodes for.
	 *	@return int, a count of the nodes which have been assigned the given code id.
	 *	@throws java.sql.SQLException
	 */
	public static int getNodeCount(DBConnection dbcon, String sCodeID) throws SQLException {

		int count=0;

		Connection con = dbcon.getConnection() ;
		if (con == null)
			return count;

		PreparedStatement pstmt = con.prepareStatement(GET_NODECOUNT_QUERY) ;
		pstmt.setString(1, sCodeID);
		ResultSet rs = pstmt.executeQuery();

		if (rs != null) {
			while (rs.next()) {
				count = rs.getInt(1);
			}
		}
		pstmt.close();

		return count;
	}
}
