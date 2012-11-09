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
import java.util.*;

import com.compendium.core.db.management.*;

/**
 * The DBCodeGroup class serves as the interface layer between the CodeGroup data
 * and the CodeGroup table in the database.
 *
 * @author	Michelle Bachler
 */
public class DBCodeGroup {

	// AUDITED
	/** SQL statement to insert a new CodeGroup record into the database.*/
	public final static String INSERT_CODEGROUP_QUERY =
		"INSERT INTO CodeGroup (CodeGroupID, Author, Name, CreationDate, ModificationDate) "+
		"VALUES (?, ?, ?, ?, ?) ";

	/** SQL statement to update the name of the code group with the given CodeGroupID.*/
	public final static String UPDATE_NAME_QUERY =
		"UPDATE CodeGroup " +
		"SET Name = ?, ModificationDate = ? " +
		"WHERE CodeGroupID = ? ";

	/** SQL statement to delete a CodeGroup record with the given CodeGroupID.*/
	public final static String DELETE_CODEGROUP_QUERY =
		"DELETE "+
		"FROM CodeGroup "+
		"WHERE CodeGroupID = ? ";

	// UNAUDITED
	/** SQL statement to return the CodeGroup record for the given CodeGroupID.*/
	public final static String GET_CODEGROUP_QUERY =
		"SELECT CodeGroupID, Name, Author, CreationDate, ModificationDate "+
		"FROM CodeGroup "+
		"WHERE CodeGroupID = ? ";

	/** SQL statement to return the id and names for all the CodeGroup records ordered by Name.*/
	public final static String GET_CODEGROUPS_QUERY =
		"SELECT CodeGroupID, Name "+
		"FROM CodeGroup ORDER BY Name";

	/**
	 *	Inserts a new codegroup in the database and return if successful.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param sCodeGroupID, the id of the new code group being added.
	 *	@param sAuthor, the author of the new code group.
	 *	@param sName, the name of the new code group.
	 * 	@param dCreationDate java.util.Date, the creation date of the new code group.
	 *	@param dModificationDate java.util.Date, the last modification date for the new code group (same as creation).
	 *	@return boolean, true if it was successful, else false.
	 *	@throws java.sql.SQLException
	 */
	public static boolean insert(DBConnection dbcon, String sCodeGroupID, String sAuthor, String sName,
								Date dCreationDate, Date dModificationDate) throws SQLException{

		Connection con = dbcon.getConnection();
		if (con == null)
			return false;

		PreparedStatement pstmt = con.prepareStatement(INSERT_CODEGROUP_QUERY);

		double created = new Long(dCreationDate.getTime()).doubleValue();
		double modified = new Long(dModificationDate.getTime()).doubleValue();

 		pstmt.setString(1, sCodeGroupID);
		pstmt.setString(2, sAuthor);
		pstmt.setString(3, sName) ;
		pstmt.setDouble(4, created);
		pstmt.setDouble(5, modified);

		int nRowCount = pstmt.executeUpdate();
		pstmt.close() ;

		if (nRowCount > 0) {
			if (DBAudit.getAuditOn()) {
				DBAudit.auditCodeGroup(dbcon, DBAudit.ACTION_ADD, sCodeGroupID, sAuthor, sName, "", created, modified);
			}
			return true;
		}
		else
			return false;
	}

	/**
	 *	Update a codegroup name in the database and return if successful.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param sCodeGroupID, the id of the code group whose name to update.
	 *	@param sName, the new name of the code group.
	 *	@param dModificationDate java.util.Date, the modification date for the code group record.
	 *	@param sUserID, the id of the user making the modification.
	 *	@return boolean, true if it was successful, else false.
	 *	@throws java.sql.SQLException
	 */
	public static boolean setName(DBConnection dbcon, String sCodeGroupID, String sName, Date modificationDate, String sUserID) throws SQLException{

		Connection con = dbcon.getConnection();
		if (con == null)
			return false;

		PreparedStatement pstmt = con.prepareStatement(UPDATE_NAME_QUERY);
		double modified = new Long(modificationDate.getTime()).doubleValue();
		pstmt.setString(1, sName) ;
		pstmt.setDouble(2, modified);
 		pstmt.setString(3, sCodeGroupID);

		int nRowCount = pstmt.executeUpdate();
		pstmt.close() ;

		if (nRowCount > 0) {
			if (DBAudit.getAuditOn()) {
				Vector data = DBCodeGroup.getCodeGroup(dbcon, sCodeGroupID);
				double created = ((Double)data.elementAt(3)).doubleValue();
				DBAudit.auditCodeGroup(dbcon, DBAudit.ACTION_EDIT, sCodeGroupID, sUserID, sName, "", created, modified);
			}

			return true;
		}
		else
			return false;
	}

	/**
	 *	Deletes the codegroup with the given sCodeGroupID from the database and returns true if successful
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param sCodeGroupID, the id of the code group to delete.
	 *	@return boolean, true if it was successful, else false.
	 *	@throws java.sql.SQLException
	 */
	public static boolean delete(DBConnection dbcon, String sCodeGroupID) throws SQLException {

		boolean deleted = false;

		Connection con = dbcon.getConnection() ;
		if (con == null)
			return false;

		// IF AUDITITNG, SAVE DATA
		Vector data = null;
		if (DBAudit.getAuditOn())
			data = DBCodeGroup.getCodeGroup(dbcon, sCodeGroupID);

		PreparedStatement pstmt = null;
		pstmt = con.prepareStatement(DELETE_CODEGROUP_QUERY) ;
		pstmt.setString(1, sCodeGroupID) ;
		int nRowCount = pstmt.executeUpdate() ;
		pstmt.close();

		if (nRowCount > 0) {
			if (DBAudit.getAuditOn() && data != null) {
				String sName = (String)data.elementAt(1);
				String sAuthor = (String)data.elementAt(2);
				double created = ((Double)data.elementAt(3)).doubleValue();
				double modified = ((Double)data.elementAt(4)).doubleValue();
				DBAudit.auditCodeGroup(dbcon, DBAudit.ACTION_DELETE, sCodeGroupID, sAuthor, sName, "", created, modified);
			}
		}
		else
			deleted = false ;

		return (deleted);
	}


	/**
	 * 	Retrieves the CodeGroup with the given id from the database and returns it.
	 *	<p>
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param sCodeGroupID, the code group id of the record to retrieve.
	 *	<p>
	 *	@return java.util.Vector, a Vector of code group data:
	 *  <li>CodeGroupID - String
	 * 	<li>Name - String
	 * 	<li>Author - String
	 *  <li>CreationDate - Double (milliseconds)
	 * 	<li>ModificationDate - Double (milliseconds)
	 *	@throws java.sql.SQLException
	 */
	public static Vector getCodeGroup(DBConnection dbcon, String sCodeGroupID) throws SQLException {

		Vector item  = null;

		Connection con = dbcon.getConnection();
		if (con == null)
			return item;

		PreparedStatement pstmt = con.prepareStatement(GET_CODEGROUP_QUERY);
		pstmt.setString(1, sCodeGroupID);
		ResultSet rs = pstmt.executeQuery();

		if (rs != null) {
			if (rs.next()) {
 				item = new Vector(5);
				item.addElement((String)rs.getString(1));
				item.addElement((String)rs.getString(2)) ;
				item.addElement((String)rs.getString(3)) ;
				item.addElement( new Double(rs.getDouble(4)) );
				item.addElement( new Double(rs.getDouble(5)) );
			}
		}
		pstmt.close();
		return item;
	}

	/**
	 * 	Retrieves All CodeGroups from the database (Used by the Administrator).
	 *	<p>
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	<p>
	 *	@return java.util.Vector, a Vector of VEctors of code group data. Each inner Vector contains:
	 *  <li>CodeGroupID - String
	 * 	<li>Name - String
	 *	@throws java.sql.SQLException
	 */
	public static Vector getCodeGroups(DBConnection dbcon) throws SQLException {

		Connection con = dbcon.getConnection();
		if (con == null)
			return null;

		PreparedStatement pstmt = con.prepareStatement(GET_CODEGROUPS_QUERY);
		ResultSet rs = pstmt.executeQuery();

		Vector vtCodeGroups = new Vector(51);
		if (rs != null) {
			while (rs.next()) {
				Vector item = new Vector(2);

				item.addElement((String)rs.getString(1));
				item.addElement((String)rs.getString(2)) ;

				vtCodeGroups.addElement(item);
			}
		}
		pstmt.close();
		return vtCodeGroups;
	}
}
