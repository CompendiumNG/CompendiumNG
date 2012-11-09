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

import java.sql.*;

import com.compendium.core.datamodel.*;
import com.compendium.core.db.management.*;

/**
 * THIS CLASS IS CURRENTLY NOT USED AND THEREFORE ITS INTERFACE CANNOT BE GUARENTEED
 *
 * The DBPermission class serves as the interface layer between the Permission objects
 * and the Permission table in the database.
 *
 * @author	rema and sajid
 */
public class DBPermission {

	/** insert the permission for the given object for the given group*/
	public final static String INSERT_PERMISSION_QUERY =
		"INSERT INTO Permission (ItemID, GroupID, Permission) "+
		"VALUES (?, ?, ?) ";

	/** delete the permission for the given object for the given group*/
	public final static String DELETE_PERMISSION_QUERY =
		"DELETE "+
		"FROM Permission "+
		"WHERE ItemID = ? " +
		"AND GroupID = ? " ;

	/** delete all permission entries for all objects for the given group: coz the group has been deleted*/
	public final static String DELETE_GROUP_QUERY =
		"DELETE "+
		"FROM Permission "+
		"WHERE GroupID = ? " ;

	/** delete all permission entries for the given object for all groups: coz the object has been deleted*/
	public final static String DELETE_OBJECT_QUERY =
		"DELETE "+
		"FROM Permission "+
		"WHERE ItemID = ? " ;

	/** get permission for the given group id for the given object id*/
	public final static String GET_PERMISSION_QUERY =
		"SELECT ItemID, GroupID, Permission" +
		"FROM Permission "+
		"WHERE ItemID = ? " +
		"AND GroupID = ? " ;

	/** R012599 updates a node user state record that already exists in the table */
	public final static String UPDATE_PERMISSION_QUERY =
		"UPDATE Permission " +
		"SET ItemID = ?, GroupID = ?, Permission = ? " +
		"WHERE ItemID = ? " +
		"AND GroupID = ? " ;


	/**
	 * Inserts a new Permission in the database and returns it.
	 */
	public static Permission insert(DBConnection dbcon, String objectId, String groupId, int perm)
		throws SQLException{
		Connection con = dbcon.getConnection();
		if (con == null)
			return null;

		PreparedStatement pstmt = con.prepareStatement(INSERT_PERMISSION_QUERY);

 		pstmt.setString(1, objectId);
		pstmt.setString(2, groupId) ;
		pstmt.setInt(3, perm) ;

		int nRowCount = pstmt.executeUpdate();
		pstmt.close();

		Permission permission = null;
		if (nRowCount > 0) {
			permission = new Permission(objectId, groupId, perm) ;
			return permission;
		} else
			return null;
	}

	/**
	 *	Deletes the permission for the given object for the given group id and returns true if
	 *	successful
	 */
	public static boolean delete(DBConnection dbcon, String objectId, String groupId) throws SQLException {
		Connection con = dbcon.getConnection() ;
		if (con == null)
			return false;

		PreparedStatement pstmt = con.prepareStatement(DELETE_PERMISSION_QUERY) ;

		pstmt.setString(1, objectId) ;
		pstmt.setString(2, groupId) ;

		int nRowCount = pstmt.executeUpdate() ;
		pstmt.close();

		if (nRowCount > 0)
			return true ;
		else
			return false ;
	}

	/**
	 *	Deletes all entries for the particular group id from the table and returns true if
	 *	successful
	 */
	public static boolean deleteGroup(DBConnection dbcon, String groupId) throws SQLException {
		Connection con = dbcon.getConnection() ;
		if (con == null)
			return false;

		PreparedStatement pstmt = con.prepareStatement(DELETE_GROUP_QUERY) ;

		pstmt.setString(1, groupId);

		int nRowCount = pstmt.executeUpdate();
		pstmt.close();

		if (nRowCount > 0)
			return true;
		else
			return false;
	}

	/**
	 *	Deletes all entries for the particular object id from the table and returns true if
	 *	successful
	 */
	public static boolean deleteObject(DBConnection dbcon, String objectId) throws SQLException {
		Connection con = dbcon.getConnection();
		if (con == null)
			return false;

		PreparedStatement pstmt = con.prepareStatement(DELETE_OBJECT_QUERY) ;

		pstmt.setString(1, objectId) ;

		int nRowCount = pstmt.executeUpdate() ;
		pstmt.close();

		if (nRowCount > 0)
			return true;
		else
			return false;
	}

	/**
	 * Retrieves the Permission for the given object for the given group from the database and returns it.
	 */
	public static Permission get(DBConnection dbcon, String objectId, String groupId) throws SQLException {
		Connection con = dbcon.getConnection();
		if (con == null)
			return null;

		PreparedStatement pstmt = con.prepareStatement(GET_PERMISSION_QUERY);

		pstmt.setString(1, objectId);
		pstmt.setString(2, groupId);

		ResultSet rs = pstmt.executeQuery();

		Permission permission = null;
		if (rs != null) {
			while (rs.next()) {
				String	sObjectId	= rs.getString(1);
				String	sDescription = rs.getString(2);
				int	nPermission = rs.getInt(3);

				permission = new Permission(sObjectId, sDescription, nPermission );
			}
		}
		pstmt.close();
		return permission;
	}

	/**
	 *	Updates a node user state record in the table and returns boolean value true/flse depending on
	 *	success state.
	 */
	public static boolean update(DBConnection dbcon, String objectId, String groupId, int perm) throws SQLException {
		Connection con = dbcon.getConnection();
		if (con == null)
			return false;

		PreparedStatement pstmt = con.prepareStatement(UPDATE_PERMISSION_QUERY);

 		pstmt.setString(1, objectId);	 // -- should stay same as before
		pstmt.setString(2, groupId) ; // -- should stay same as before
		pstmt.setInt(3, perm) ;

 		pstmt.setString(4, objectId);
		pstmt.setString(5, groupId) ;

		int nRowCount = pstmt.executeUpdate();

		// close pstmt to save resources
		pstmt.close() ;
		if (nRowCount > 0)
			return true;
		else
			return false;
	}
}
