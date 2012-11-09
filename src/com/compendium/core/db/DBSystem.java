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
 * The DBSystem class holds methods to interact with the records in a System database table.
 *
 * @author	Michelle Bachler
 */
public class DBSystem {

	// AUDITED

	/** SQL statement to add a new System record into the System table.*/
	public final static String INSERT_PROPERTY_QUERY =
		"INSERT INTO System " +
		"(Property, Contents) " +
		"VALUES (?, ?)";

	/** SQL statement to update a new System record into the System table.*/
	public final static String UPDATE_PROPERTY_QUERY =
		"UPDATE System " +
		"SET Contents = ? " +
		"WHERE Property = ?";

	/** SQL statement to update the default CodeGroup in the system table.*/
	public final static String UPDATE_CODEGROUP_QUERY =
		"UPDATE System " +
		"SET Contents = ? " +
		"WHERE Property = 'codegroup'";

	/** SQL statement to update the default Link group in the system table.*/
	public final static String UPDATE_LINKGROUP_QUERY =
		"UPDATE System " +
		"SET Contents = ? " +
		"WHERE Property = 'linkgroup'";

	/** SQL statement to update the database version number in the system table.*/
	public final static String UPDATE_VERSION_QUERY =
		"UPDATE System " +
		"SET Contents = ? " +
		"WHERE Property = 'version'";

	/** SQL statement to update the default user in the system table.*/
	public final static String UPDATE_USER_QUERY =
		"UPDATE System " +
		"SET Contents = ? " +
		"WHERE Property = 'defaultuser'";

	// UNAUDITED
	/** SQL statement to return the default code group from the System table.*/
	public final static String SELECT_CODEGROUP_QUERY =
		"SELECT Contents " +
		"FROM System "+
		"WHERE Property = 'codegroup'";

	/** SQL statement to return the default link group from the System table.*/
	public final static String SELECT_LINKGROUP_QUERY =
		"SELECT Contents " +
		"FROM System "+
		"WHERE Property = 'linkgroup'";

	/** SQL statement to return the default user from the system table.*/
	public final static String SELECT_USER_QUERY =
		"SELECT Contents " +
		"FROM System "+
		"WHERE Property = 'defaultuser'";

	/** SQL statement to select all the propeties from the system table.*/
	public final static String SELECT_ALL_QUERY =
		"SELECT * " +
		"FROM System";

	
	/**
	 *  Update the project level properties.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param properties the hashtable containing the key and value pairs to save to the database.
	 *	@return boolean true if it was successful, else false.
	 *	@throws java.sql.SQLException
	 */
	public static boolean insertProperties(DBConnection dbcon, Hashtable properties) throws SQLException {

		Connection con = dbcon.getConnection();
		if (con == null)
			return false;

		String sKey = "";
		String sValue = "";
		for (Enumeration e = properties.keys(); e.hasMoreElements();) {
			sKey = (String)e.nextElement();
			sValue = (String)properties.get(sKey);
			insertProperty(dbcon, sKey, sValue);
		}
		
		return true;
	}	

	/**
	 *  Update the default user property and return if successful.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param sProperty the property name.
	 *	@param sValue the property value.
	 *	@return boolean, true if it was successful, else false.
	 *	@throws java.sql.SQLException
	 */
	public static boolean insertProperty(DBConnection dbcon, String sProperty, String sValue) throws SQLException {

		Connection con = dbcon.getConnection();
		if (con == null)
			return false;

		PreparedStatement pstmt = con.prepareStatement(UPDATE_PROPERTY_QUERY);
		pstmt.setString(1, sValue);
		pstmt.setString(2, sProperty);

		int nRowCount = pstmt.executeUpdate();
		pstmt.close() ;

		if (nRowCount > 0) {
			if (DBAudit.getAuditOn())
				DBAudit.auditSystem(dbcon, DBAudit.ACTION_EDIT, sProperty, sValue);
			return true;
		}
		else {
			// ADD THIS NEW PROPERTY
			pstmt = con.prepareStatement(INSERT_PROPERTY_QUERY);
			pstmt.setString(1, sProperty);
			pstmt.setString(2, sValue);

			nRowCount = pstmt.executeUpdate();
			pstmt.close() ;
			if (nRowCount > 0) {
				if (DBAudit.getAuditOn())
					DBAudit.auditSystem(dbcon, DBAudit.ACTION_EDIT, sProperty, sValue);
				return true;
			}
			else {
				return false;
			}
		}
	}
	
	/**
	 *  Update the default user property and return if successful.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param sUserID, the id of the new default user.
	 *	@return boolean, true if it was successful, else false.
	 *	@throws java.sql.SQLException
	 */
	public static boolean setDefaultUser(DBConnection dbcon, String sUserID) throws SQLException {

		Connection con = dbcon.getConnection();
		if (con == null)
			return false;

		PreparedStatement pstmt = con.prepareStatement(UPDATE_USER_QUERY);
		pstmt.setString(1, sUserID) ;

		int nRowCount = pstmt.executeUpdate();
		pstmt.close() ;

		if (nRowCount > 0) {
			if (DBAudit.getAuditOn())
				DBAudit.auditSystem(dbcon, DBAudit.ACTION_EDIT, "defaultuser", sUserID);
			return true;
		}
		else {
			// ADD THIS NEW PROPERTY
			pstmt = con.prepareStatement(INSERT_PROPERTY_QUERY);
			pstmt.setString(1, "defaultuser");
			pstmt.setString(2, sUserID) ;

			nRowCount = pstmt.executeUpdate();
			pstmt.close() ;
			if (nRowCount > 0) {
				if (DBAudit.getAuditOn())
					DBAudit.auditSystem(dbcon, DBAudit.ACTION_EDIT, "defaultuser", sUserID);
				return true;
			}
			else
				return false;
		}
	}

	/**
	 * Update the default codegroup in the system table and return if successful.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param sCodeGroupID, the id of the new default code group.
	 *	@return boolean, true if it was successful, else false.
	 *	@throws java.sql.SQLException
	 */
	public static boolean setCodeGroup(DBConnection dbcon, String sCodeGroupID) throws SQLException {

		Connection con = dbcon.getConnection();
		if (con == null)
			return false;

		PreparedStatement pstmt = con.prepareStatement(UPDATE_CODEGROUP_QUERY);
		pstmt.setString(1, sCodeGroupID) ;

		int nRowCount = pstmt.executeUpdate();
		pstmt.close() ;

		if (nRowCount > 0) {
			if (DBAudit.getAuditOn())
				DBAudit.auditSystem(dbcon, DBAudit.ACTION_EDIT, "codegroup", sCodeGroupID);
			return true;
		}
		else {
			// ADD THIS NEW PROPERTY
			pstmt = con.prepareStatement(INSERT_PROPERTY_QUERY);
			pstmt.setString(1, "codegroup");
			pstmt.setString(2, sCodeGroupID) ;

			nRowCount = pstmt.executeUpdate();
			pstmt.close() ;
			if (nRowCount > 0) {
				if (DBAudit.getAuditOn())
					DBAudit.auditSystem(dbcon, DBAudit.ACTION_EDIT, "codegroup", sCodeGroupID);
				return true;
			}
			else
				return false;
		}
	}

	/**
	 *  Update the default link group in the system table and return if successful.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param sLinkGroupID, the id of the new default link group.
	 *	@return boolean, true if it was successful, else false.
	 *	@throws java.sql.SQLException
	 */
	public static boolean setLinkGroup(DBConnection dbcon, String sLinkGroupID) throws SQLException {

		Connection con = dbcon.getConnection();
		if (con == null)
			return false;

		PreparedStatement pstmt = con.prepareStatement(UPDATE_LINKGROUP_QUERY);
		pstmt.setString(1, sLinkGroupID) ;

		int nRowCount = pstmt.executeUpdate();
		pstmt.close() ;

		if (nRowCount > 0) {
			if (DBAudit.getAuditOn())
				DBAudit.auditSystem(dbcon, DBAudit.ACTION_EDIT, "linkgroup", sLinkGroupID);
			return true;
		}
		else {
			// ADD THIS NEW PROPERTY
			pstmt = con.prepareStatement(INSERT_PROPERTY_QUERY);
			pstmt.setString(1, "linkgroup");
			pstmt.setString(2, sLinkGroupID) ;

			nRowCount = pstmt.executeUpdate();
			pstmt.close() ;
			if (nRowCount > 0) {
				if (DBAudit.getAuditOn())
					DBAudit.auditSystem(dbcon, DBAudit.ACTION_EDIT, "linkgroup", sLinkGroupID);
				return true;
			}
			else
				return false;
		}
	}

	/**
	 * Update the database version in the system table and return if successful.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param sVersion, the new database version number.
	 *	@return boolean, true if it was successful, else false.
	 *	@throws java.sql.SQLException
	 */
	public static boolean setDatabaseVersion(DBConnection dbcon, String sVersion) throws SQLException {

		Connection con = dbcon.getConnection();
		if (con == null)
			return false;

		PreparedStatement pstmt = con.prepareStatement(UPDATE_VERSION_QUERY);
		pstmt.setString(1, sVersion) ;

		int nRowCount = pstmt.executeUpdate();
		pstmt.close();

		if (nRowCount > 0) {
			if (DBAudit.getAuditOn())
				DBAudit.auditSystem(dbcon, DBAudit.ACTION_EDIT, "version", sVersion);
			return true;
		}
		else
			return false;
	}

	/**
	 *  Return the active code group.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@return java.lang.String, the current default code group.
	 *	@throws java.sql.SQLException
	 */
	public static String getCodeGroup(DBConnection dbcon) throws SQLException {

		String sCodeGroup = "";

		Connection con = dbcon.getConnection();
		if (con == null)
			return sCodeGroup;

		PreparedStatement pstmt = con.prepareStatement(SELECT_CODEGROUP_QUERY);
		ResultSet rs = pstmt.executeQuery();

		if (rs != null) {
			while (rs.next())
				sCodeGroup = rs.getString(1) ;
		}
		pstmt.close() ;

		return sCodeGroup;
	}

	/**
	 *  Return the active link group.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@return java.lang.String, the current default link group.
	 *	@throws java.sql.SQLException
	 */
	public static String getLinkGroup(DBConnection dbcon) throws SQLException {

		String sLinkGroup = "";

		Connection con = dbcon.getConnection();
		if (con == null)
			return sLinkGroup;

		PreparedStatement pstmt = con.prepareStatement(SELECT_LINKGROUP_QUERY);
		ResultSet rs = pstmt.executeQuery();

		if (rs != null) {
			while (rs.next())
				sLinkGroup = rs.getString(1) ;
		}
		pstmt.close() ;

		return sLinkGroup;
	}

	/**
	 *  Return the default user.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@return java.lang.String, the current default user.
	 *	@throws java.sql.SQLException
	 */
	public static UserProfile getDefaultUser(DBConnection dbcon) throws SQLException {

		UserProfile oUser = null;

		Connection con = dbcon.getConnection();
		if (con == null)
			return oUser;

		PreparedStatement pstmt = con.prepareStatement(SELECT_USER_QUERY);
		ResultSet rs = pstmt.executeQuery();

		String sUserID = "";
		if (rs != null) {
			while (rs.next())
				sUserID = rs.getString(1) ;

			if (!sUserID.equals(""))
				oUser = DBUser.getUser(dbcon, sUserID);
		}
		pstmt.close() ;

		return oUser;
	}
	
	/**
	 *  Return the project level preferences.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@throws java.sql.SQLException
	 */
	public static Hashtable getProperties(DBConnection dbcon) throws SQLException {

		Hashtable table = new Hashtable();
		Connection con = dbcon.getConnection();
		if (con == null)
			return table;

		PreparedStatement pstmt = con.prepareStatement(SELECT_ALL_QUERY);
		ResultSet rs = pstmt.executeQuery();

		String sProperty = "";
		String sValue = "";		
		
		if (rs != null) {
			while (rs.next()) {
				sProperty = rs.getString(1);
				sValue = rs.getString(2);
				table.put(sProperty, sValue);
			}
		}
		pstmt.close();

		return table;
	}	
}
