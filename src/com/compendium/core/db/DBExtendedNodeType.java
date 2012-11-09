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

import com.compendium.core.db.management.*;
import com.compendium.core.datamodel.*;

/**
 * THIS CLASS IS CURRENTLY NOT USED AND THEREFORE ITS INTERFACE CANNOT BE GUARENTEED
 *
 * The DBExtendedNodeType class serves as the interface layer between the ExtendedNodeType objects
 * and the ExtendedNodeType table in the database.
 *
 * @author	Rema Natarajan
  */
public class DBExtendedNodeType{

	/** inserts an extended node type */
	public final static String INSERT_XNODETYPE_QUERY =
		"INSERT INTO ExtendedNodeType (ExtendedNodeTypeID, Author, CreationDate, ModificationDate, "+
		"Name, Description, BaseNodeType, Icon )"+
		"VALUES (?, ?, ?, ?, ?, ?, ?, ?) ";

	/** deletes the extended node type record */
	public final static String DELETE_XNODETYPE_QUERY =
		"DELETE * "+
		"FROM ExtendedNodeType "+
		"WHERE ExtendedNodeTypeID = ? ";

	/** get the record for the extended node type */
	public final static String GET_XNODETYPE_QUERY =
		"SELECT ExtendedNodeTypeID, Author, CreationDate, ModificationDate," +
		"Name, Description, BaseNodeType, Icon "+
		"FROM ExtendedNodeType "+
		"WHERE ExtendedNodeTypeID = ? ";

	/** R012599 updates a extendednodetype record that already exists in the table */
	public final static String UPDATE_XNODETYPE_QUERY =
		"UPDATE ExtendedNodeType " +
		"SET ExtendedNodeTypeID = ?, Author = ?, CreationDate = ?, ModificationDate = ?,  " +
		"Name = ?, Description = ?, BaseNodeType = ?, Icon = ?" +
		"WHERE ExtendedNodeTypeID = ? ";


	/**
	 * Inserts a new extended node type in the database and returns it.
	 */
	public static ExtendedNodeType insert(DBConnection dbcon, String id, String author, java.util.Date creationDate,
		java.util.Date modificationDate, String name, String description, int baseNodeType, String icon)
		throws SQLException{
		Connection con = dbcon.getConnection();
		if (con == null)
			return null;

		PreparedStatement pstmt = con.prepareStatement(INSERT_XNODETYPE_QUERY);

 		pstmt.setString(1, id);
		pstmt.setString(2, author) ;
		pstmt.setDouble(3, new Long(creationDate.getTime()).doubleValue());
		pstmt.setDouble(4, new Long(modificationDate.getTime()).doubleValue());
		pstmt.setString(5, name) ;
		pstmt.setString(6, description);
		pstmt.setInt(7, baseNodeType);
		pstmt.setString(8, icon) ;

		int nRowCount = pstmt.executeUpdate();
		pstmt.close();

		ExtendedNodeType xNodeType = null;
		if (nRowCount > 0) {
			xNodeType = new ExtendedNodeType(id, author, creationDate, modificationDate,
				name, description, baseNodeType, icon) ;
			// cache the node
			//dbcon.addNode(node);
			return xNodeType;
		} else
			return null;
	} // insert

	/**
	 *	Deletes the extended node type with the given ID from the database and returns true if
	 *	successful
	 */
	public static boolean delete(DBConnection dbcon, String xNodeTypeId) throws SQLException {
		Connection con = dbcon.getConnection() ;
		if (con == null)
			return false;

		PreparedStatement pstmt = con.prepareStatement(DELETE_XNODETYPE_QUERY) ;

		pstmt.setString(1, xNodeTypeId) ;

		int nRowCount = pstmt.executeUpdate() ;
		pstmt.close();

		if (nRowCount > 0)
			return true ;
		else
			return false ;
	}

	/**
	 * Retrieves the extended node type with the given id from the database and returns it.
	 */
	public static ExtendedNodeType get(DBConnection dbcon, String xNodeTypeId) throws SQLException {
		Connection con = dbcon.getConnection();
		if (con == null)
			return null;

		PreparedStatement pstmt = con.prepareStatement(GET_XNODETYPE_QUERY);

		pstmt.setString(1, xNodeTypeId);

		ResultSet rs = pstmt.executeQuery();

		ExtendedNodeType xNodeType = null;
		if (rs != null) {
			while (rs.next()) {
				String	sXNodeTypeId	= rs.getString(1);
				String	sAuthor = rs.getString(2) ;
				Date		oCDate	= new Date(new Double(rs.getLong(3)).longValue());
				Date		oMDate	= new Date(new Double(rs.getLong(4)).longValue());
				String	sName = rs.getString(5);
				String	sDescription = rs.getString(6);
				int	nBaseNodeType = rs.getInt(7);
				String	sIcon = rs.getString(8);

				xNodeType = new ExtendedNodeType(sXNodeTypeId, sAuthor, oCDate, oMDate,
					sName, sDescription, nBaseNodeType, sIcon);

				pstmt.close();
				return xNodeType;
			}
		}
		pstmt.close();
		return xNodeType;
	}

	/**
	 *	Updates a extended node type record in the table and returns boolean value true/flse depending on
	 *	success state.
	 */
	public static boolean update(DBConnection dbcon, String id, String author, java.util.Date creationDate,
		java.util.Date modificationDate, String name, String description, int baseNodeType, String icon)
			throws SQLException {
		Connection con = dbcon.getConnection();
		if (con == null)
			return false;

		PreparedStatement pstmt = con.prepareStatement(UPDATE_XNODETYPE_QUERY);

 		pstmt.setString(1, id);
		pstmt.setString(2, author) ;
		pstmt.setDouble(3, new Long(creationDate.getTime()).doubleValue());
		pstmt.setDouble(4, new Long(modificationDate.getTime()).doubleValue());
		pstmt.setString(5, name) ;
		pstmt.setString(6, description);
		pstmt.setInt(7, baseNodeType);
		pstmt.setString(8, icon) ;

 		pstmt.setString(9, id);
		int nRowCount = pstmt.executeUpdate();

		// close pstmt to save resources
		pstmt.close() ;
		if (nRowCount > 0)
		{
			return true;
		} else
			return false;
	}
}
