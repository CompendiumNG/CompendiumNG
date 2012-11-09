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
import java.util.*;

import com.compendium.core.datamodel.*;
import com.compendium.core.db.management.*;

/**
 * The DBGroupCode class serves as the interface layer between the GroupCode data
 * and the GroupCode table in the database.
 * <p>
 * The GroupCode table holds the relationships between the CodeGroup and Code tables.
 * i.e. what codes have been assigned to what code groups.
 *
 * @author	Michelle Bachler
 */
public class DBGroupCode {

	// AUDITED
	/** SQL statement to insert a new GroupCode record into the database.*/
	public final static String INSERT_GROUPCODE_QUERY =
		"INSERT INTO GroupCode (CodeID, CodeGroupID, Author, CreationDate, ModificationDate) "+
		"VALUES (?, ?, ?, ?, ?) ";

	/** SQL statement to delete a GroupCode record with the given CodeID and CodeGroupID.*/
	public final static String DELETE_QUERY =
		"DELETE "+
		"FROM GroupCode "+
		"WHERE CodeID = ? AND CodeGroupID = ?";

	// UNAUDITED
	/** SQL statement to return GroupCode information for the given CodeID.*/
	public final static String GET_CODEGROUPS_QUERY =
		"SELECT GroupCode.CodeGroupID, CodeGroup.Description"+
		"FROM GroupCode INNER JOIN CodeGroup ON GroupCode.CodeGroupID = CodeGroup.CodeGroupID "+
		"WHERE CodeID = ? ORDER BY CodeGroup.Description";

	/** SQL statement to return Code information for the given GroupCodeID.*/
	public final static String GET_GROUPCODES_QUERY =
		"SELECT GroupCode.CodeID, Code.Name "+
		"FROM GroupCode INNER JOIN Code ON GroupCode.CodeID = Code.CodeID "+
		"WHERE CodeGroupID = ? ORDER BY GroupCode.CodeID, Code.Name";

	/** SQL statement to return the GroupCode data for the given CodeID.*/
	public final static String GET_CODEGROUPSDATA_QUERY =
		"SELECT CodeGroupID, Author, CreationDate, ModificationDate "+
		"FROM GroupCode "+
		"WHERE CodeID = ?";

	/** SQL statement to return the GroupCode data for the given CodeGroupID.*/
	public final static String GET_GROUPCODESDATA_QUERY =
		"SELECT CodeID, Author, CreationDate, ModificationDate "+
		"FROM GroupCode "+
		"WHERE CodeGroupID = ? ORDER BY CodeID";

	/** SQL statement to return the GroupCode data for the given CodeGroupID and CodeID.*/
	public final static String GET_GROUPCODE_QUERY =
		"SELECT Author, CreationDate, ModificationDate "+
		"FROM GroupCode"+
		"WHERE CodeGroupID = ? AND CodeID = ?";

	/** SQL statement to return all the CodeIDs and Code names for Codes not in any group.*/
	public final static String GET_NON_GROUP_CODES_QUERY =
		"SELECT Code.CodeID, Code.Name "+
		"FROM Code LEFT JOIN GroupCode ON Code.CodeID=GroupCode.CodeID "+
		"WHERE GroupCode.CodeID IS NULL "+	//Code.CodeID NOT IN ( SELECT GroupCode.CodeID FROM GroupCode ) "+
		"ORDER BY Code.Name";


	/**
	 * 	Inserts a new groupcode record in the database and return if successful.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param sCodeID, the code id for the new groupcode record.
	 *	@param sCodeGroupID, the id of code group the code is being added to.
	 * 	@param sAuthor, the author who added the code to the code group.
	 * 	@param dCreationDate java.util.Date, the creation date of the code being added to the group.
	 *	@param dModificationDate java.util.Date, the last modification date for the code being added to the group (same as creation).
	 *	@return boolean, true if it was successful, else false.
	 *	@throws java.sql.SQLException
	 */
	public static boolean insert(DBConnection dbcon, String sCodeID, String sCodeGroupID, String sAuthor,
									Date dCreationDate, Date dModificationDate) throws SQLException {

		Connection con = dbcon.getConnection();
		if (con == null)
			return false;

		PreparedStatement pstmt = con.prepareStatement(INSERT_GROUPCODE_QUERY);

 		pstmt.setString(1, sCodeID);
		pstmt.setString(2, sCodeGroupID);
		pstmt.setString(3, sAuthor);
		pstmt.setDouble(4, new Long(dCreationDate.getTime()).doubleValue());
		pstmt.setDouble(5, new Long(dModificationDate.getTime()).doubleValue());

		int nRowCount = pstmt.executeUpdate();
		pstmt.close() ;

		if (nRowCount > 0) {
			if (DBAudit.getAuditOn())
				DBAudit.auditGroupCode(dbcon, DBAudit.ACTION_ADD, sCodeID, sCodeGroupID, sAuthor, dCreationDate.getTime(), dModificationDate.getTime());

			return true;
		}
		else
			return false;
	}

	/**
	 *	Delete the given code, groupcode record from the database and returns it if successful
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param sCodeID, the code id for the record to delete.
	 *	@param sCodeGroupID, the id of code group for the record to delete.
	 *	@return boolean, true if it was successful, else false.
	 *	@throws java.sql.SQLException
	 */
	public static boolean delete(DBConnection dbcon, String sCodeID, String sCodeGroupID) throws SQLException {

		boolean deleted = false;

		Connection con = dbcon.getConnection() ;
		if (con == null)
			return false;

		// IF AUDITING, SAVE DATA
		Vector data = null;
		if (DBAudit.getAuditOn())
			data = DBGroupCode.getGroupCode(dbcon, sCodeGroupID, sCodeID);

		PreparedStatement pstmt = con.prepareStatement(DELETE_QUERY);
		pstmt.setString(1, sCodeID) ;
		pstmt.setString(2, sCodeGroupID) ;

		int nRowCount = pstmt.executeUpdate() ;
		pstmt.close();

		if (nRowCount > 0) {
			if (DBAudit.getAuditOn() && data != null) {
				String sAuthor = (String)data.elementAt(0);
				double created = ((Double)data.elementAt(1)).doubleValue();
				double modified = ((Double)data.elementAt(2)).doubleValue();
				DBAudit.auditGroupCode(dbcon, DBAudit.ACTION_ADD, sCodeID, sCodeGroupID, sAuthor, created, modified);
			}

			deleted = true;
		}
		else
			deleted = false ;

		return (deleted);
	}

	/**
	 *	Retrieves all the GroupCodes data for the given CodeID from the database.
	 * 	i.e. What groups is this code in?
	 *	<p>
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param sCodeID, the code id to retrieve the GroupCode records for.
	 *	<p>
	 *	@return Vector, element 1: the CodeGroupID (String); element 2: the CodeGroup description (String).
	 *	@throws java.sql.SQLException
	 */
	public static Vector getCodeGroups(DBConnection dbcon, String sCodeID) throws SQLException {

		Vector vtCodeGroups = new Vector(51);

		Connection con = dbcon.getConnection();
		if (con == null)
			return vtCodeGroups;

		PreparedStatement pstmt = con.prepareStatement(GET_CODEGROUPS_QUERY);
		pstmt.setString(1, sCodeID);
		ResultSet rs = pstmt.executeQuery();
		if (rs != null) {
			while (rs.next()) {
				Vector item = new Vector(2);
 				item.addElement((String) rs.getString(1));
				item.addElement((String) rs.getString(2)) ;
				vtCodeGroups.addElement(item);
			}
		}
		pstmt.close();
		return vtCodeGroups;
	}

	/**
	 * 	Retrieves All the Codes Info in the given CodeGroup from the database.
	 * 	i.e. What codes are in the given code group?
	 *	<p>
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param sCodeGroupID, the codegroup id to retrieve the codes for.
	 *	<p>
	 *	@return Vector, element 1: the CodeID (String); element 2: the Code name (String).
	 *	@throws java.sql.SQLException
	 */
	public static Vector getGroupCodes(DBConnection dbcon, String sCodeGroupID) throws SQLException {

		Connection con = dbcon.getConnection();
		if (con == null)
			return null;

		PreparedStatement pstmt = con.prepareStatement(GET_GROUPCODES_QUERY);
		pstmt.setString(1, sCodeGroupID);

		ResultSet rs = pstmt.executeQuery();

		Vector vtGroupCodes = new Vector(51);
		if (rs != null) {
			while (rs.next()) {
				String sCodeID = (String)rs.getString(1);
				Code code = DBCode.getCode(dbcon, sCodeID);
				vtGroupCodes.addElement(code);
			}
		}
		pstmt.close();
		return vtGroupCodes;
	}

	/**
	 *	Retrieves All the CodeGroups Info with the given CodeID from the database.
	 * 	i.e. What groupcode records are there for the given CodeID?
	 *	<p>
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param sCodeID, the code id to retrieve the groupcode records for.
	 *	<p>
	 *	@return java.util.Vector, a Vector of Vectors of groupcode data. Each inner Vector contains:
	 *  <li>CodeGroupID - String
	 * 	<li>Author - String
	 *  <li>CreationDate - Double (milliseconds)
	 * 	<li>ModificationDate - Double (milliseconds)
	 *	@throws java.sql.SQLException
	 */
	public static Vector getCodeGroupData(DBConnection dbcon, String sCodeID) throws SQLException {

		Connection con = dbcon.getConnection();
		if (con == null)
			return null;

		PreparedStatement pstmt = con.prepareStatement(GET_CODEGROUPSDATA_QUERY);
		pstmt.setString(1, sCodeID);

		ResultSet rs = pstmt.executeQuery();

		Vector vtGroupCodes = new Vector(51);
		if (rs != null) {
			while (rs.next()) {
				Vector item = new Vector(2);

 				item.addElement((String) rs.getString(1));
				item.addElement((String) rs.getString(2)) ;
				item.addElement(new Double(rs.getDouble(3)));
				item.addElement(new Double(rs.getDouble(4)));

				vtGroupCodes.addElement(item);
			}
		}
		pstmt.close();
		return vtGroupCodes;
	}

	/**
	 *	Retrieves All the CodeGroups Info with the given CodeGroupID from the database.
	 * 	i.e. What groupcode records are there for the CodeGroupID?
	 *	<p>
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param sCodeGroupID, the codegroup id to retrieve the groupcode records for.
	 *	<p>
	 *	@return java.util.Vector, a Vector of Vectors of groupcode data. Each inner Vector contains:
	 *  <li>CodeID - String
	 * 	<li>Author - String
	 *  <li>CreationDate - Double (milliseconds)
	 * 	<li>ModificationDate - Double (milliseconds)
	 *	@throws java.sql.SQLException
	 */
	public static Vector getGroupCodeData(DBConnection dbcon, String sCodeGroupID) throws SQLException {

		Connection con = dbcon.getConnection();
		if (con == null)
			return null;

		PreparedStatement pstmt = con.prepareStatement(GET_GROUPCODESDATA_QUERY);
		pstmt.setString(1, sCodeGroupID);

		ResultSet rs = pstmt.executeQuery();

		Vector vtGroupCodes = new Vector(51);
		if (rs != null) {
			while (rs.next()) {
				Vector item = new Vector(2);

 				item.addElement((String) rs.getString(1));
				item.addElement((String) rs.getString(2)) ;
				item.addElement(new Double(rs.getDouble(3)));
				item.addElement(new Double(rs.getDouble(4)));

				vtGroupCodes.addElement(item);
			}
		}
		pstmt.close();
		return vtGroupCodes;
	}

	/**
	 * 	Retrieves the groupcode with the given CodeGroup and code ids.
	 *	<p>
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param sCodeGroupID, the codegroup id to retrieve the groupcode record for.
	 *	@param sCodeID, the code id to retrieve the groupcode record for.
	 *	<p>
	 *	@return java.util.Vector, a Vector of Vectors of groupcode data. Each inner Vector contains:
	 * 	<li>Author - String
	 *  <li>CreationDate - Double (milliseconds)
	 * 	<li>ModificationDate - Double (milliseconds)
	 *	@throws java.sql.SQLException
	 */
	public static Vector getGroupCode(DBConnection dbcon, String sCodeGroupID, String sCodeID) throws SQLException {

		Connection con = dbcon.getConnection();
		if (con == null)
			return null;

		PreparedStatement pstmt = con.prepareStatement(GET_GROUPCODE_QUERY);
		pstmt.setString(1, sCodeGroupID);
		pstmt.setString(2, sCodeID);

		ResultSet rs = pstmt.executeQuery();

		Vector vtGroupCodes = new Vector(51);
		if (rs != null) {
			if (rs.next()) {
				vtGroupCodes.addElement(rs.getString(1));
				vtGroupCodes.addElement(new Double(rs.getDouble(2)));
				vtGroupCodes.addElement(new Double(rs.getDouble(3)));
			}
		}
		pstmt.close();
		return vtGroupCodes;
	}

	/**
	 * Retrieves All the Codes Info not in a group from the database.
	 *	<p>
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	<p>
	 *	@return java.util.Vector, a Vector of <code>Code</code> objects not in any group:
	 *	@throws java.sql.SQLException
	 * 	@see com.compendium.core.datamodel.Code
	 */
	public static Vector getUngroupedCodes(DBConnection dbcon) throws SQLException {

		Connection con = dbcon.getConnection();
		if (con == null)
			return null;

		PreparedStatement pstmt = con.prepareStatement(GET_NON_GROUP_CODES_QUERY);
		ResultSet rs = pstmt.executeQuery();

		Vector vtCodes = new Vector(51);
		if (rs != null) {
			while (rs.next()) {
				String sCodeID = (String)rs.getString(1);
				Code code = DBCode.getCode(dbcon, sCodeID);
				vtCodes.addElement(code);
			}
		}
		pstmt.close();
		return vtCodes;
	}
}
