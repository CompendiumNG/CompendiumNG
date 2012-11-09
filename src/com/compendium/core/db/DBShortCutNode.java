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
import java.util.Enumeration;
import java.util.Vector;

import com.compendium.core.datamodel.*;
import com.compendium.core.db.management.*;

/**
 * The DBShortCutNode class serves as the interface layer to the ShortCutNodes table in
 * the database
 *
 * @author	Rema Natarajan / Michelle Bachler
 */
public class DBShortCutNode {

	// AUDITED

	/**
	 * 	SQL statement to insert a particular ShortCutNode record.
	 *	NodeID -- the short cut node id.
	 *	ReferneceID -- the node that this short cut references.
	 */
	public final static String INSERT_SHORTCUTNODE_QUERY =
		"INSERT INTO ShortCutNode (NodeID, ReferenceID) "+
		"VALUES (?, ?) ";

	// UNAUDITED

	/** SQL statement to return all the ShortCutNodes for the given reference node are retrieved.*/
	public final static String GET_SHORTCUTNODES_QUERY =
		"SELECT NodeID "+
		"FROM ShortCutNode "+
		"WHERE ReferenceID = ? ";

	/** SQL statement to return The particular reference node id for the given node id.*/
	public final static String GET_SHORTCUTNODE_QUERY =
		"SELECT ReferenceID "+
		"FROM ShortCutNode "+
		"WHERE NodeID = ? ";


	/** there are no update methods for this table, as all the attribs are part of the primary key */


	/**
	 * Inserts a new shortcut for the referenced node in the database and returns true if successful.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param sNodeID, the id of the new shortcut node.
	 * 	@param sReferenceID, the id of the node it references.
	 *	@return boolean, true if it was successful, else false.
	 *	@throws java.sql.SQLException
	 */
	public static boolean insert(DBConnection dbcon, String sNodeID, String sReferenceID) throws SQLException {

		Connection con = dbcon.getConnection();
		if (con == null)
			throw new SQLException("No Database Connection");

		PreparedStatement pstmt = con.prepareStatement(INSERT_SHORTCUTNODE_QUERY);

 		pstmt.setString(1, sNodeID);
		pstmt.setString(2, sReferenceID) ;

		int nRowCount = pstmt.executeUpdate();
		pstmt.close();

		if (nRowCount > 0) {
			if (DBAudit.getAuditOn() )
				DBAudit.auditSystem(dbcon, DBAudit.ACTION_ADD, sNodeID, sReferenceID);
			return true;
		}
		else
			return false;
	}

	/**
	 *	Retrieve all the ShortCutNodes of a particular parent node.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@return Enumeration, a list of the <code>NodeSummary</code> objects referenceing the given reference node id.
	 *	@throws java.sql.SQLException
	 */
	public static Enumeration getShortCutNodes(DBConnection dbcon, String sReferenceID, String userID)
			throws SQLException	{

		Connection con = dbcon.getConnection();
		if (con == null)
			throw new SQLException("No Database Connection");

		PreparedStatement pstmt = con.prepareStatement(GET_SHORTCUTNODES_QUERY) ;

		pstmt.setString(1, sReferenceID);

		ResultSet rs = pstmt.executeQuery();

		Vector vtShortCutNodes = new Vector(51);
		if (rs != null) {
			while (rs.next()) {
				String	shortcutId = rs.getString(1) ;

				// call the DBNode class to retrieve the NodeSummary object and add it to
				// the enumeration

				// This check should not be needed but would cause a never-ending loop
				// if it ever happened.
				if (!shortcutId.equals(sReferenceID)) {
					NodeSummary node = (NodeSummary)DBNode.getNodeSummary(dbcon, shortcutId, userID) ;
					vtShortCutNodes.addElement(node);
				}
			}
		}

		pstmt.close();

		return vtShortCutNodes.elements();
	}

	/**
	 *	Retrieve a particular shortcut for the given id.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@return com.compendium.core.datamodel.NodeSummary, the <code>NodeSummary</code> object for the given NodeID, else null.
	 *	@throws java.sql.SQLException
	 */
	public static NodeSummary getShortCutNode(DBConnection dbcon, String sNodeID, String userID) throws SQLException {

		Connection con = dbcon.getConnection();
		if (con == null)
			throw new SQLException("No Database Connection");

		PreparedStatement pstmt = con.prepareStatement(GET_SHORTCUTNODE_QUERY) ;
		pstmt.setString(1, sNodeID);

		ResultSet rs = pstmt.executeQuery();

		NodeSummary node = null ;
		if (rs != null) {
			while (rs.next()) {
				String	shortcutId = rs.getString(1);

				// call the DBNode class to retrieve the NodeSummary object

				// This check should not be needed but would cause a neverending loop
				// if it ever happened.
				if (!shortcutId.equals(sNodeID) && !shortcutId.equals("")) {
					node = (NodeSummary)DBNode.getNodeSummary(dbcon, shortcutId, userID) ;
				}
			}
		}

		pstmt.close();

		return node;
	}
}
