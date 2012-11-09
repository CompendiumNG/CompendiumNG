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
 * The DBGroup class serves as the interface layer between the Group objects
 * and the Group table in the database.
 *
 * @author	Rema Natarajan
 * @version	1.0
 */
public class DBGroup {

	/** insert a group into the table*/
	public final static String INSERT_GROUP_QUERY =
		"INSERT INTO UserGroup (GroupID, UserID, CreationDate, ModificationDate, Name, Description) "+
		"VALUES (?, ?, ?, ?, ?, ?) ";

	/** delete a group with the given group id from the table*/
	public final static String DELETE_GROUP_QUERY =
		"DELETE "+
		"FROM UserGroup "+
		"WHERE GroupID = ? ";

	/** get the group with the given group id*/
	public final static String GET_GROUP_QUERY =
		"SELECT GroupID, UserID, CreationDate, ModificationDate, Name, Description"+
		"FROM UserGroup "+
		"WHERE GroupID = ? ";

	/** R012599 updates a group record that already exists in the table */
	public final static String UPDATE_GROUP_QUERY =
		"UPDATE UserGroup " +
		"SET GroupID = ?, UserID = ?, CreationDate = ?, ModificationDate = ?,  " +
		"GroupName = ?, GroupDescription = ? " +
		"WHERE GroupID = ? ";

	/**
	 * Inserts a new Group in the database and returns it.
	 */
	public static Group insert(DBConnection dbcon, String id, String author, java.util.Date creationDate,
		java.util.Date modificationDate, String name, String description)
		throws SQLException{

		Connection con = dbcon.getConnection();
		if (con == null)
			return null;

		PreparedStatement pstmt = con.prepareStatement(INSERT_GROUP_QUERY);

 		pstmt.setString(1, id);
		pstmt.setString(2, author) ;
		pstmt.setDouble(3, new Long(creationDate.getTime()).doubleValue());
		pstmt.setDouble(4, new Long(modificationDate.getTime()).doubleValue());
		pstmt.setString(5, name) ;
		pstmt.setString(6, description);

		int nRowCount = pstmt.executeUpdate();
		pstmt.close();

		Group group = null;
		if (nRowCount > 0) {
			group = new Group(id, author, creationDate, modificationDate, name, description) ;
			// cache the node
			//dbcon.addNode(node);
			return group;
		} else
			return null;
	}

	/**
	 *	Deletes the Group with the given ID from the database and returns true if
	 *	successful
	 */
	public static boolean delete(DBConnection dbcon, String GroupID) throws SQLException {
		Connection con = dbcon.getConnection() ;
		if (con == null)
			return false;

		PreparedStatement pstmt = con.prepareStatement(DELETE_GROUP_QUERY) ;

		pstmt.setString(1, GroupID) ;

		int nRowCount = pstmt.executeUpdate() ;
		pstmt.close();

		if (nRowCount > 0)
			return true ;
		else
			return false ;
	}

	/**
	 * Retrieves the Group with the given id from the database and returns it.
	 */
	public static Group get(DBConnection dbcon, String GroupID) throws SQLException {
		Connection con = dbcon.getConnection();
		if (con == null)
			return null;

		PreparedStatement pstmt = con.prepareStatement(GET_GROUP_QUERY);

		pstmt.setString(1, GroupID);

		ResultSet rs = pstmt.executeQuery();

		Group group = null;
		if (rs != null) {
			while (rs.next()) {
				String	sGroupID	= rs.getString(1);
				String	sAuthor = rs.getString(2) ;
				Date		oCDate	= new Date(new Double(rs.getLong(3)).longValue());
				Date		oMDate	= new Date(new Double(rs.getLong(4)).longValue());
				String	sName = rs.getString(5);
				String	sDescription = rs.getString(6);

				group = new Group(sGroupID, sAuthor, oCDate, oMDate, sName, sDescription);
				pstmt.close();
				return group;
			}
		}
		pstmt.close();
		return group;
	}

	/**
	 *	R012599
	 *	Updates a group record in the table and returns boolean value true/flse depending on
	 *	success state.
	 */
	public static boolean update(DBConnection dbcon, String id, String author, java.util.Date creationDate,
		java.util.Date modificationDate, String name, String description)
			throws SQLException {
		Connection con = dbcon.getConnection();
		if (con == null)
			return false;

		PreparedStatement pstmt = con.prepareStatement(UPDATE_GROUP_QUERY);

  		pstmt.setString(1, id);
		pstmt.setString(2, author) ;
		pstmt.setDouble(3, new Long(creationDate.getTime()).doubleValue());
		pstmt.setDouble(4, new Long(modificationDate.getTime()).doubleValue());
		pstmt.setString(5, name) ;
		pstmt.setString(6, description);

  		pstmt.setString(7, id);
		int nRowCount = pstmt.executeUpdate();

		// close pstmt to save resources
		pstmt.close() ;
		if (nRowCount > 0)
			return true;
		else
			return false;
	}
}
