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

import com.compendium.core.datamodel.*;
import com.compendium.core.db.management.*;

/**
 * THIS CLASS IS CURRENTLY NOT USED AND THEREFORE ITS INTERFACE CANNOT BE GUARENTEED
 *
 * The DBExtendedTypeCode class serves as the interface layer to the ExtendedTypeCode table in
 * the database for the relationship between ExtendedNodeTypes and Codes in the datamodel.
 *
 * @author	Rema Natarajan
 * @version	1.0
 */
public class DBExtendedTypeCode {

	/** insert code for a particular x node type*/
	public final static String INSERT_XCODE_QUERY =
		"INSERT INTO ExtendedTypeCode (ExtendedNodeTypeID, CodeID) "+
		"VALUES (?, ?) ";

	/** delete the particular code for the x node type*/
	public final static String DELETE_XCODE_QUERY =
		"DELETE * "+
		"FROM ExtendedTypeCode "+
		"WHERE ExtendedNodeTypeID = ? " +
		"AND CodeID = ? ";

	/** delete the x node type's code entries, basically all occurences of the given x node type*/
	public final static String DELETE_XTYPE_QUERY =
		"DELETE * " +
		"FROM ExtendedTypeCode " +
		"WHERE ExtendedNodeTypeID = ? " ;

	/** delete all occurences of the given code */
	public final static String DELETE_CODE_QUERY =
		"DELETE * " +
		"FROM ExtendedTypeCode " +
		"WHERE codeID = ? " ;


	/** get codes for the given x node type*/
	public final static String GET_CODES_FOR_XTYPE_QUERY =
		"SELECT CodeID "+
		"FROM ExtendedTypeCode "+
		"WHERE ExtendedNodeTypeID = ? ";

	/** get all the x types that reference the given code */
	public final static String GET_XTYPES_FOR_CODE_QUERY =
		"SELECT ExtendedNodeTypeID " +
		"FROM ExtendedTypeCode " +
		"WHERE CodeID = ? " ;


	/**
	 * Inserts a new code for an x node type in the database and returns true if successful.
	 */
	public static boolean insert(DBConnection dbcon, String xNodeTypeId, String codeId)
		throws SQLException{
		Connection con = dbcon.getConnection();
		if (con == null)
			return false;

		PreparedStatement pstmt = con.prepareStatement(INSERT_XCODE_QUERY);

 		pstmt.setString(1, xNodeTypeId);
		pstmt.setString(2, codeId) ;

		int nRowCount = pstmt.executeUpdate();
		pstmt.close();

		if (nRowCount > 0) {
			return true;
		} else
			return false;
	}

	/**
	 *	Deletes the particular code for the given xNodeType from the database and returns true if
	 *	successful
	 */
	public static boolean deleteXCode(DBConnection dbcon, String xNodeTypeId, String codeId) throws SQLException {
		Connection con = dbcon.getConnection() ;
		if (con == null)
			return false;

		PreparedStatement pstmt = con.prepareStatement(DELETE_XCODE_QUERY) ;

		pstmt.setString(1, xNodeTypeId) ;
		pstmt.setString(2, codeId) ;

		int nRowCount = pstmt.executeUpdate() ;
		pstmt.close();

		if (nRowCount > 0)
			return true ;
		else
			return false ;
	}

	/**
	 *	Deletes the xNodeType  with the given Id from the table, removes all the entries and returns true if
	 *	successful
	 */
	public static boolean deleteXType(DBConnection dbcon, String xNodeTypeId) throws SQLException {
		Connection con = dbcon.getConnection() ;
		if (con == null)
			return false;

		PreparedStatement pstmt = con.prepareStatement(DELETE_XTYPE_QUERY) ;

		pstmt.setString(1, xNodeTypeId) ;

		int nRowCount = pstmt.executeUpdate() ;
		pstmt.close();

		if (nRowCount > 0)
			return true ;
		else
			return false ;
	}

	/**
	 *	Deletes the codes with the given codeId, basically removes the given code from the table and returns true if
	 *	successful
	 */
	public static boolean deleteCode(DBConnection dbcon, String codeId) throws SQLException {
		Connection con = dbcon.getConnection() ;
		if (con == null)
			return false;

		PreparedStatement pstmt = con.prepareStatement(DELETE_CODE_QUERY) ;

		pstmt.setString(1, codeId) ;

		int nRowCount = pstmt.executeUpdate() ;
		pstmt.close();

		if (nRowCount > 0)
			return true ;
		else
			return false ;
	}
}
