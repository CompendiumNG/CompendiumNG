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
 * <p>
 * The DBUserGroup class serves as the interface layer to the UserGroup table in
 * the database for the relationship between Users and Groups in the datamodel.
 *
 * @author	Rema Natarajan
 * @version	1.0
 */
public class DBUserGroup {

	/** insert a particular group id that a given user id belongs to*/
	public final static String INSERT_USERGROUP_QUERY =
		"INSERT INTO GroupUser (UserID, GroupID) "+
		"VALUES (?, ?) ";

	/** delete all entries for the given user id*/
	public final static String DELETE_USER_QUERY =
		"DELETE "+
		"FROM GroupUser "+
		"WHERE UserID = ? ";

	/** delete all entries for the given group id*/
	public final static String DELETE_GROUP_QUERY =
		"DELETE " +
		"FROM GroupUser " +
		"WHERE groupID = ? " ;

	/** delete the given user id for the given group id - user is no longer part of the group*/
	public final static String DELETE_USERGROUP_QUERY =
		DELETE_USER_QUERY +
		"AND GroupID = ? " ;

	/** get all users who belong to the given group id*/
	public final static String GET_USERS_QUERY =
		"SELECT UserID "+
		"FROM GroupUser "+
		"WHERE GroupID = ? ";

	/** get all groups to which the given user (user id) belongs */
	public final static String GET_GROUPS_QUERY =
		"SELECT GroupID " +
		"FROM GroupUser " +
		"WHERE UserID = ? " ;


	/**
	 * Inserts a new user-group relationship in the table and returns true if successful.
	 */
	public static boolean insert(DBConnection dbcon, String userId, String groupId)
		throws SQLException{
		Connection con = dbcon.getConnection();
		if (con == null)
			return false;

		PreparedStatement pstmt = con.prepareStatement(INSERT_USERGROUP_QUERY);

 		pstmt.setString(1, userId);
		pstmt.setString(2, groupId) ;

		int nRowCount = pstmt.executeUpdate();
		pstmt.close();

		if (nRowCount > 0) {
			return true;
		} else
			return false;
	}

	/**
	 *	Deletes all entries for the given user id from the table and returns true if
	 *	successful
	 */
	public static boolean deleteUser(DBConnection dbcon, String userId) throws SQLException {
		Connection con = dbcon.getConnection() ;
		if (con == null)
			return false;

		PreparedStatement pstmt = con.prepareStatement(DELETE_USER_QUERY) ;

		pstmt.setString(1, userId) ;

		int nRowCount = pstmt.executeUpdate() ;
		pstmt.close();

		if (nRowCount > 0)
			return true ;
		else
			return false ;
	}

	/**
	 *	Deletes all entries with the given groupId from the table and returns true if
	 *	successful
	 */
	public static boolean deleteGroup(DBConnection dbcon, String groupId) throws SQLException {
		Connection con = dbcon.getConnection() ;
		if (con == null)
			return false;

		PreparedStatement pstmt = con.prepareStatement(DELETE_GROUP_QUERY) ;

		pstmt.setString(1, groupId) ;

		int nRowCount = pstmt.executeUpdate() ;
		pstmt.close();

		if (nRowCount > 0)
			return true ;
		else
			return false ;
	}

	/**
	 *	Deletes the entry with the given userId and groupId from the table and returns true if
	 *	successful
	 */
	public static boolean deleteUserGroup(DBConnection dbcon, String userId, String groupId) throws SQLException {
		Connection con = dbcon.getConnection();
		if (con == null)
			return false;

		PreparedStatement pstmt = con.prepareStatement(DELETE_USERGROUP_QUERY) ;

		pstmt.setString(1, userId);
		pstmt.setString(2, groupId);

		int nRowCount = pstmt.executeUpdate();
		pstmt.close();

		if (nRowCount > 0)
			return true;
		else
			return false;
	}
}
