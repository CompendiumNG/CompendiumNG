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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Vector;

import com.compendium.core.datamodel.*;
import com.compendium.core.db.management.*;

/**
 * THIS CLASS IS CURRENTLY NOT USED AND THEREFORE ITS INTERFACE CANNOT BE GUARENTEED
 *
 * The DBClone class serves as the interface layer to the Clones table in
 * the database
 *
 * @author	Rema Natarajan
 */
public class DBClone {

	/** a particular Clone for a node in a view is being inserted*/
	public final static String INSERT_CLONE_QUERY =
		"INSERT INTO Clone (ParentNodeID, ChildNodeID) "+
		"VALUES (?, ?) ";

	/** a particular Clone for a node in a view is being deleted*/
	public final static String DELETE_CLONE_QUERY =
		"DELETE "+
		"FROM Clone "+
		"WHERE ParentNodeID = ? " +
		"AND ChildNodeID = ? ";

	/** the parent node is being deleted, hence all the clone entries have to be removed*/
	public final static String DELETE_PARENTNODE_QUERY =
		"DELETE " +
		"FROM Clone " +
		"WHERE ParentNodeID = ? " ;

	/** all the clones for the given parent node are retrieved*/
	public final static String GET_CLONES_QUERY =
		"SELECT ChildNodeID "+
		"FROM Clone "+
		"WHERE ParentNodeID = ? ";

	/** the particular clone for the given parent node is retrieved*/
	public final static String GET_CLONE_QUERY =
		"SELECT ChildNodeID "+
		"FROM Clone "+
		"WHERE ParentNodeID = ? " +
		"AND ChildNodeID = ? ";

	/** there are no update methods for this table, as all the attribs are part of the primary key */

	/**
	 * Inserts a new clone for the parent node in the database and returns true if successful.
	 */
	public static boolean insert(DBConnection dbcon, String parentId, String childId)
		throws SQLException{
		Connection con = dbcon.getConnection();
		if (con == null)
			return false;

		PreparedStatement pstmt = con.prepareStatement(INSERT_CLONE_QUERY);

 		pstmt.setString(1, parentId);
		pstmt.setString(2, childId) ;

		int nRowCount = pstmt.executeUpdate();
		pstmt.close();

		if (nRowCount > 0) {
			return true;
		} else
			return false;
	}

	/**
	 *	Deletes the clone for the given parent node and returns true if
	 *	successful
	 */
	public static boolean deleteClone(DBConnection dbcon, String parentId, String childId)
		throws SQLException {
		Connection con = dbcon.getConnection() ;
		if (con == null)
			return false;

		PreparedStatement pstmt = con.prepareStatement(DELETE_CLONE_QUERY) ;

		pstmt.setString(1, parentId) ;
		pstmt.setString(2, childId) ;

		int nRowCount = pstmt.executeUpdate() ;
		pstmt.close();

		if (nRowCount > 0)
			return true ;
		else
			return false ;
	}

	/**
	 *	Deletes the parent node with the given nodeId with all the clone entries from the table and returns true if
	 *	successful
	 */
	public static boolean deleteParentNode(DBConnection dbcon, String parentId) throws SQLException {
		Connection con = dbcon.getConnection() ;
		if (con == null)
			return false;

		PreparedStatement pstmt = con.prepareStatement(DELETE_PARENTNODE_QUERY) ;

		pstmt.setString(1, parentId) ;

		int nRowCount = pstmt.executeUpdate() ;
		pstmt.close();

		if (nRowCount > 0)
			return true ;
		else
			return false ;
	}

	/**
	 *	Retrieve all the clones of a particular parent node
	 *	the return value is an enumeration of Clone objects (NodeSummaries for the Clone objects)
	 *	returns null if there are no entries for the parent node in the table.
	 */
	public static Enumeration getClones(DBConnection dbcon, String parentId, String userID)
			throws SQLException	{

		Connection con = dbcon.getConnection() ;
		if (con == null)
			return null ;

		PreparedStatement pstmt = con.prepareStatement(GET_CLONES_QUERY) ;

		pstmt.setString(1, parentId) ;

		ResultSet rs = pstmt.executeQuery();

		Vector vtClones = new Vector(51);
		if (rs != null) {
			while (rs.next()) {
				String	cloneId			= rs.getString(1) ;

				// call the DBNode class to retrieve the NodeSummary object and add it to
				// the enumeration
				NodeSummary node = (NodeSummary)DBNode.getNodeSummary(dbcon,cloneId, userID) ;
				vtClones.addElement(node);
			}
		}

		pstmt.close();

		return vtClones.elements();
	}

	/**
	 *	Retrieve the particular clone of a particular parent node
	 *	the return value is the Clone object (NodeSummary for the Clone object)
	 *	returns null if there are no entries for the parent node in the table.
	 */
	public static NodeSummary getClone(DBConnection dbcon, String parentId, String childId, String userID)	throws SQLException	{
		Connection con = dbcon.getConnection() ;
		if (con == null)
			return null ;

		PreparedStatement pstmt = con.prepareStatement(GET_CLONE_QUERY) ;

		pstmt.setString(1, parentId) ;
		pstmt.setString(2, childId) ;

		ResultSet rs = pstmt.executeQuery();

		NodeSummary node = null ;
		if (rs != null) {
			while (rs.next()) {
				String	cloneId			= rs.getString(1) ;

				// call the DBNode class to retrieve the NodeSummary object
				node = (NodeSummary)DBNode.getNodeSummary(dbcon,cloneId, userID) ;
			}
		}

		pstmt.close();

		return node;
	}
}
