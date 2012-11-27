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

import com.compendium.core.datamodel.*;
import com.compendium.core.db.management.*;

/**
 * The DBCode class serves as the interface layer between the RCode objects
 * and the Code table in the database.
 *
 * @author	rema and sajid / Michelle Bachler
 */
public class DBCode {

	// AUDITED
	/** SQL statement to insert a new Code Record into the Code table.*/
	public final static String INSERT_CODE_QUERY =
		"INSERT INTO Code (CodeID, Author, CreationDate, ModificationDate, Name, Description, Behavior) "+
		"VALUES (?, ?, ?, ?, ?, ?, ?) ";

	/** SQL statement to update a Code name for the given CodeID.*/
	public final static String UPDATE_NAME_QUERY =
		"UPDATE Code " +
		"SET Name = ?, ModificationDate = ? " +
		"WHERE CodeID = ? ";

	/** SQL statement to update a Code description with for given CodeID.*/
	public final static String UPDATE_DESCRIPTION_QUERY =
		"UPDATE Code " +
		"SET Description = ?, ModificationDate = ? " +
		"WHERE CodeID = ? ";

	/** SQL statement to update a Code behavior with for given CodeID.*/
	public final static String UPDATE_BEHAVIOR_QUERY =
		"UPDATE Code " +
		"SET Behavior = ?, ModificationDate = ? " +
		"WHERE CodeID = ? ";

	/** SQL statement to delete a Code with the given CodeID.*/
	public final static String DELETE_CODE_QUERY =
		"DELETE "+
		"FROM Code "+
		"WHERE CodeID = ? ";

	// UNAUDITED
	/** SQL statement to return the Code with the given CodeID.*/
	public final static String GET_CODE_QUERY =
		"SELECT CodeID, Author, CreationDate, ModificationDate, Name, Description, Behavior "+
		"FROM Code "+
		"WHERE CodeID = ? ";

	/** SQL statement to return the Code with the given name.*/
	public final static String GET_CODE_NAME_QUERY =
		"SELECT CodeID, Author, CreationDate, ModificationDate, Name, Description, Behavior "+
		"FROM Code "+
		"WHERE Name = ? ";

	/** SQL statement to return the Codes with the given name.*/
	public final static String GET_CODE_ID_QUERY =
		"SELECT CodeID "+
		"FROM Code "+
		"WHERE Name = ? ";

	/** SQL statement to return all  Code in the Code table.*/
	public final static String GET_CODES_QUERY =
		"SELECT CodeID, Author, CreationDate, ModificationDate, Name, Description, Behavior "+
		"FROM Code ";

	/**
	 * 	Inserts a new code in the database and associates it with the given NodeID if not null.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param sNodeID, the id of the node to add this code to if not null.
	 *	@param sCodeID, the id of the new code.
	 * 	@param sAuthor, the author who created this code.
	 * 	@param dCreationDate java.util.Date, the creation date of the code
	 *	@param dModificationDate java.util.Date, the last modification date for the code (same as creation).
	 *	@param sName, the name of the new code.
	 *	@param sDescription, the description for the new code.
	 *  @param sBehavior, the behavior for the new code.
	 *	@return boolean, true if it was successful, else false.
	 *	@throws java.sql.SQLException
	 */
	public static Code insert(DBConnection dbcon, String sNodeID, String sCodeID, String sAuthor, java.util.Date dCreationDate,
			java.util.Date dModificationDate, String sName, String sDescription, String sBehavior)
			throws SQLException{

		Connection con = dbcon.getConnection();
		if (con == null)
			return null;

		PreparedStatement pstmt = con.prepareStatement(INSERT_CODE_QUERY);

 		pstmt.setString(1, sCodeID);
		pstmt.setString(2, sAuthor);
		pstmt.setDouble(3, new Long(dCreationDate.getTime()).doubleValue());
		pstmt.setDouble(4, new Long(dModificationDate.getTime()).doubleValue());
		pstmt.setString(5, sName);
		pstmt.setString(6, sDescription);
		pstmt.setString(7, sBehavior);

		int nRowCount = pstmt.executeUpdate();

		// close pstmt to save resources
		pstmt.close() ;
		Code code = null;

		if (nRowCount > 0) {
			code = Code.getCode(sCodeID, sAuthor, dCreationDate, dModificationDate, sName, sDescription, sBehavior);

			// add to nodecode table if the nodeId is not null
			boolean added = false;
			if(sNodeID != null) {

				added = DBCodeNode.insert(dbcon, sNodeID, sCodeID);
				if(!added)
					return null;
				else {
					if (DBAudit.getAuditOn())
						DBAudit.auditCode(dbcon, DBAudit.ACTION_ADD, code);

					return code;
				}
			}
			return code;
		}
		else
			return null;
	}

	/**
	 *	Deletes the code with the given CodeID from the database and returns true if successful.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param sCodeID, the id of the code to delete.
	 *	@return boolean, true if it was successful, else false.
	 *	@throws java.sql.SQLException
	 */
	public static boolean delete(DBConnection dbcon, String sCodeID) throws SQLException {

		boolean deleted = false;

		Connection con = dbcon.getConnection() ;
		if (con == null)
			return deleted;

		// STORE DATA BEFORE DELETED, FOR AUDIT
		Code code = null;
		if (DBAudit.getAuditOn()) {
			code = DBCode.getCode(dbcon, sCodeID);
		}

		PreparedStatement pstmt = null;
		pstmt = con.prepareStatement(DELETE_CODE_QUERY);
		pstmt.setString(1, sCodeID) ;

		int nRowCount = pstmt.executeUpdate() ;
		pstmt.close();

		if (nRowCount > 0) {
			if (DBAudit.getAuditOn()) {
				DBAudit.auditCode(dbcon, DBAudit.ACTION_DELETE, code);
			}
			deleted = true;
		}
		else
			deleted = false;

		return (deleted);
	}

	/**
	 * 	Update a code name of the code with the particulr sCodeID and return if successful.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param String sCodeID, the code id for the particular code object.
	 *	@param String sName, the value of the code name.
	 * 	@param Date dModificationDate, the modification date.
	 *	@return boolean, true if the update was successful, else false.
	 *	@exception throws java.sql.SQLException
	 */
	public static boolean setName(DBConnection dbcon, String sCodeID, String sName, Date dModificationDate) throws SQLException{

		Connection con = dbcon.getConnection();
		if (con == null)
			return false;

		PreparedStatement pstmt = con.prepareStatement(UPDATE_NAME_QUERY);

		pstmt.setString(1, sName) ;
		pstmt.setDouble(2, new Long(dModificationDate.getTime()).doubleValue());
 		pstmt.setString(3, sCodeID);

		int nRowCount = pstmt.executeUpdate();
		pstmt.close() ;

		if (nRowCount > 0) {
			if (DBAudit.getAuditOn()) {
				Code code = DBCode.getCode(dbcon, sCodeID);
				DBAudit.auditCode(dbcon, DBAudit.ACTION_EDIT, code);
			}
			return true;
		}
		else
			return false;
	}

	/**
	 *	Update the description of the code with the particular sCodeID and return if successful
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param String sCodeID, the code id for the particular code object.
	 *	@param String sDescription, the value of the code description.
	 * 	@param Date dModificationDate, the modification date.
	 *	@return boolean, true if the update was successful, else false.
	 *	@exception throws java.sql.SQLException
	 */
	public static boolean setDescription(DBConnection dbcon, String sCodeID, String sDescription, Date dModificationDate) throws SQLException{

		Connection con = dbcon.getConnection();
		if (con == null)
			return false;

		PreparedStatement pstmt = con.prepareStatement(UPDATE_DESCRIPTION_QUERY);

		pstmt.setString(1, sDescription) ;
		pstmt.setDouble(2, new Long(dModificationDate.getTime()).doubleValue());
 		pstmt.setString(3, sCodeID);

		int nRowCount = pstmt.executeUpdate();
		pstmt.close() ;

		if (nRowCount > 0) {
			if (DBAudit.getAuditOn()) {
				Code code = DBCode.getCode(dbcon, sCodeID);
				DBAudit.auditCode(dbcon, DBAudit.ACTION_EDIT, code);
			}
			return true;
		}
		else
			return false;
	}

	/**
	 *	Update the behavior of the code with the particular sCodeID and return if successful
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param String sCodeID, the code id for the particular code object.
	 *	@param String sBehavior, the value of the code behavior.
	 * 	@param Date dModificationDate, the modification date.
	 *	@return boolean, true if the update was successful, else false.
	 *	@exception throws java.sql.SQLException
	 */
	public static boolean setBehavior(DBConnection dbcon, String sCodeID, String sBehavior, Date dModificationDate) throws SQLException{

		Connection con = dbcon.getConnection();
		if (con == null)
			return false;

		PreparedStatement pstmt = con.prepareStatement(UPDATE_BEHAVIOR_QUERY);

		pstmt.setString(1, sBehavior) ;
		pstmt.setDouble(2, new Long(dModificationDate.getTime()).doubleValue());
 		pstmt.setString(3, sCodeID);

		int nRowCount = pstmt.executeUpdate();
		pstmt.close() ;

		if (nRowCount > 0) {
			if (DBAudit.getAuditOn()) {
				Code code = DBCode.getCode(dbcon, sCodeID);
				DBAudit.auditCode(dbcon, DBAudit.ACTION_EDIT, code);
			}
			return true;
		}
		else
			return false;
	}

// GETTERS

	/**
	 *	Retrieves All Code ids for the given code name.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param String sName, the code name to retreive all the code ids for.
	 *	@return Vector, a list of Code ids for the given code name.
	 *	@throws java.sql.SQLException
	 */
	public static Vector getCodeIDs(DBConnection dbcon, String sName) throws SQLException {

		Connection con = dbcon.getConnection();
		if (con == null)
			return null;

		PreparedStatement pstmt = con.prepareStatement(GET_CODE_ID_QUERY);
		pstmt.setString(1, sName);

		ResultSet rs = pstmt.executeQuery();

		Vector vtCodes = new Vector(51);
		Code code = null;
		if (rs != null) {
			while (rs.next()) {
				String	sCodeID	= rs.getString(1);
				vtCodes.addElement(sCodeID);
			}
		}
		pstmt.close();

		return vtCodes;
	}

	/**
	 * Returns the Code Object for the given code name
	 *
	 * @param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 * @param String sName, the name of the code to return
	 * @return a Code object for the given code name
	 * @exception java.sql.SQLException
	 */
	public static Code getCodeForName(DBConnection dbcon, String sName)	throws SQLException {
		Connection con = dbcon.getConnection();
		if (con == null)
			return null;

		PreparedStatement pstmt = con.prepareStatement(GET_CODE_NAME_QUERY);
		pstmt.setString(1, sName);

		ResultSet rs = pstmt.executeQuery();

		Code oCode = null;
		if (rs != null) {
			while (rs.next()) {
				String	sCodeID	= rs.getString(1);
				String	sAuthor = rs.getString(2) ;
				Date	oCDate	= new Date(new Double(rs.getLong(3)).longValue());
				Date	oMDate	= new Date(new Double(rs.getLong(4)).longValue());
				//String	sName = rs.getString(5);
				String	sDescription = rs.getString(6);
				String	sBehavior = rs.getString(7);

				oCode = Code.getCode(sCodeID, sAuthor, oCDate, oMDate, sName, sDescription, sBehavior);
			}
		}
		pstmt.close();

		return oCode;
	}

	/**
	 * 	Retrieves the Code with the given id from the database and returns it.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param String sCodeID, the id of the Code to returnr.
	 *	@return com.compendium.core.datamodel.Code, the <code>Code</code> with the given id, else null.
	 *	@throws java.sql.SQLException
	 */
	public static Code getCode(DBConnection dbcon, String CodeID) throws SQLException {

		Connection con = dbcon.getConnection();
		if (con == null)
			return null;

		PreparedStatement pstmt = con.prepareStatement(GET_CODE_QUERY);
		pstmt.setString(1, CodeID);
		ResultSet rs = pstmt.executeQuery();

		Code code = null;
		if (rs != null) {
			while (rs.next()) {
				String	sCodeID	= rs.getString(1);
				String	sAuthor = rs.getString(2) ;
				Date	oCDate	= new Date(new Double(rs.getLong(3)).longValue());
				Date	oMDate	= new Date(new Double(rs.getLong(4)).longValue());
				String	sName = rs.getString(5);
				String	sDescription = rs.getString(6);
				String	sBehavior = rs.getString(7);

				//Vector	nodes = DBCodeNode.getNodes(dbcon, sCodeID);

				code = Code.getCode(sCodeID, sAuthor, oCDate, oMDate, sName, sDescription, sBehavior);

                pstmt.close();
				return code;
			}
		}
		pstmt.close();
		return code;
	}


	/**
	 * Retrieves All Codes from the database (Used by the Administrator).
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@return Vector, a list of <code>Code</code> objects for all the code records in the Code table.
	 *	@throws java.sql.SQLException
	 */
	public static Vector getCodes(DBConnection dbcon) throws SQLException {
		Connection con = dbcon.getConnection();
		if (con == null)
			return null;

		PreparedStatement pstmt = con.prepareStatement(GET_CODES_QUERY);
		ResultSet rs = pstmt.executeQuery();

		Vector vtCodes = new Vector(51);
		Code code = null;
		if (rs != null) {
			while (rs.next()) {
				String	sCodeID	= rs.getString(1);
				String	sAuthor = rs.getString(2) ;
				Date	oCDate	= new Date(new Double(rs.getLong(3)).longValue());
				Date 	oMDate	= new Date(new Double(rs.getLong(4)).longValue());
				String	sName = rs.getString(5);
				String	sDescription = rs.getString(6);
				String	sBehavior = rs.getString(7);

				//Vector	nodes = DBCodeNode.getNodes(dbcon, sCodeID);

				code = Code.getCode(sCodeID, sAuthor, oCDate, oMDate, sName, sDescription, sBehavior);
				vtCodes.addElement(code);
			}
		}
		pstmt.close();
		return vtCodes;
	}
}
