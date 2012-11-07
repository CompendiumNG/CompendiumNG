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
 * The DBExternalConnection class holds methods to interact with reconds in a Connections table of a Compendium database.
 *
 * @author	Michelle Bachler
 */
public class DBExternalConnection {

	// AUDITED
	/** SQL statement to add a new Connection record into the Connections   table.*/
	public final static String INSERT_CONNECTION_QUERY =
		"INSERT INTO Connections " + //$NON-NLS-1$
		"(UserID, Profile, Type, Server, Login, Password, Name, Port, Resource) " + //$NON-NLS-1$
		"VALUES (?, ?, ?, ? ,? ,?, ?, ?, ?)"; //$NON-NLS-1$

	/** SQL statement to update a Connection record in the Connections table.*/
	public final static String UPDATE_CONNECTION_QUERY =
		"UPDATE Connections " + //$NON-NLS-1$
		"SET Profile=?, Type=?, Server=?. Login=?, Password=?, Name=?, Port=?, Resource=? " + //$NON-NLS-1$
		"WHERE UserID=? AND Profile=? AND Type=?"; //$NON-NLS-1$

	/** SQL statement to delete the Connection in the Connections table.*/
	public final static String DELETE_CONNECTION_QUERY =
		"DELETE From Connections " + //$NON-NLS-1$
		"WHERE UserID = ? AND Profile=? AND Type=?"; //$NON-NLS-1$

	/** SQL statement to update the name field for a Connection in the Connections table.*/
	public final static String UPDATE_CONNECTION_NAME_QUERY =
		"UPDATE Connections " + //$NON-NLS-1$
		"SET Name=? " + //$NON-NLS-1$
		"WHERE UserID = ? AND Profile=? AND Type=?"; //$NON-NLS-1$

	// UNAUDITED
	/** SQL statement to return all the connections for a given user id and type.*/
	public final static String SELECT_CONNECTIONS_QUERY =
		"SELECT * " + //$NON-NLS-1$
		"FROM Connections "+ //$NON-NLS-1$
		"WHERE UserID = ? AND Type=? Order By Profile"; //$NON-NLS-1$


	/**
	 *  Insert a new connection record and return if successful.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param DBExternalConnection connection, the connection to insert.
	 *	@return boolean, true if it was successful, else false.
	 *	@throws java.sql.SQLException
	 */
	public static boolean insert(DBConnection dbcon, ExternalConnection connection) throws SQLException {

		Connection con = dbcon.getConnection();
		if (con == null)
			return false;

		PreparedStatement pstmt = con.prepareStatement(INSERT_CONNECTION_QUERY);
		pstmt.setString(1, connection.getUserID());
		pstmt.setString(2, connection.getProfile());
		pstmt.setInt(3, connection.getType());
		pstmt.setString(4, connection.getServer());
		pstmt.setString(5, connection.getLogin());
		pstmt.setString(6, connection.getPassword());
		pstmt.setString(7, connection.getName());
		pstmt.setInt(8, connection.getPort());
		pstmt.setString(9, connection.getResource());

		int nRowCount = pstmt.executeUpdate();
		pstmt.close() ;

		if (nRowCount > 0) {
			if (DBAudit.getAuditOn())
				DBAudit.auditExternalConnection(dbcon, DBAudit.ACTION_ADD, connection);
			return true;
		}
		return false;
	}

	/**
	 *  Update the connection record and return if successful.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param DBExternalConnection connection, the connection to insert.
	 *	@param String sProfile, the olf profile name for the record to be updated.
	 *	@param int nType, the old connection type for the record being updated.
	 *	@return boolean, true if it was successful, else false.
	 *	@throws java.sql.SQLException
	 */
	public static boolean update(DBConnection dbcon, ExternalConnection connection, String sProfile, int nType) throws SQLException {

		Connection con = dbcon.getConnection();
		if (con == null)
			return false;

		PreparedStatement pstmt = con.prepareStatement(UPDATE_CONNECTION_QUERY);
		pstmt.setString(1, connection.getProfile());
		pstmt.setInt(2, connection.getType());
		pstmt.setString(3, connection.getServer());
		pstmt.setString(4, connection.getLogin());
		pstmt.setString(5, connection.getPassword());
		pstmt.setString(6, connection.getName());
		pstmt.setInt(7, connection.getPort());
		pstmt.setString(8, connection.getResource());

		// Primary Key Fields
		pstmt.setString(9, connection.getUserID());
		pstmt.setString(10, sProfile);
		pstmt.setInt(11, nType);


		int nRowCount = pstmt.executeUpdate();
		pstmt.close() ;

		if (nRowCount > 0) {
			if (DBAudit.getAuditOn())
				DBAudit.auditExternalConnection(dbcon, DBAudit.ACTION_EDIT, connection);
			return true;
		}
		return false;
	}


	/**
	 *  Delete the given connection.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param DBExternalConnection connection, the connection to delete.
	 *	@return boolean, true if it was successful, else false.
	 *	@throws java.sql.SQLException
	 */
	public static boolean delete(DBConnection dbcon, ExternalConnection connection) throws SQLException {

		Connection con = dbcon.getConnection();
		if (con == null)
			return false;

		PreparedStatement pstmt = con.prepareStatement(DELETE_CONNECTION_QUERY);
		pstmt.setString(1, connection.getUserID());
		pstmt.setString(2, connection.getProfile());
		pstmt.setInt(3, connection.getType());

		int nRowCount = pstmt.executeUpdate();
		pstmt.close() ;

		if (nRowCount > 0) {
			if (DBAudit.getAuditOn())
				DBAudit.auditExternalConnection(dbcon, DBAudit.ACTION_DELETE, connection);
			return true;
		}
		return false;
	}

	/**
	 *  Return a list of connections for the given user and of the given type.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param String sUserID, the user whose connections to get.
	 * 	@param int nType, the type of connections to return.
	 *	@return Vector a list of the requested connections.
	 *	@throws java.sql.SQLException
	 */
	public static Vector getConnections(DBConnection dbcon, String sUserID, int nType) throws SQLException {

		Vector connections  = new Vector();

		Connection con = dbcon.getConnection();
		if (con == null)
			return connections;

		PreparedStatement pstmt = con.prepareStatement(SELECT_CONNECTIONS_QUERY);
		pstmt.setString(1, sUserID);
		pstmt.setInt(2, nType) ;
		ResultSet rs = pstmt.executeQuery();

		if (rs != null) {
			while (rs.next()) {
				String sProfile = rs.getString(2);
				String sServer = rs.getString(4);
				String sLogin = rs.getString(5);
				String sPassword = rs.getString(6);
				String sName = rs.getString(7);
				int nPort = rs.getInt(8);
				String sResource = rs.getString(9);

				ExternalConnection connection = new ExternalConnection(sUserID, sProfile, nType, sServer, sLogin, sPassword, sName, nPort, sResource);

				connections.addElement(connection);
			}
		}
		pstmt.close() ;

		return connections;
	}
}
