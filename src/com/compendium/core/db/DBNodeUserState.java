/********************************************************************************
 *                                                                              *
 *  (c) Copyright 2009 Verizon Communications USA and The Open University UK    *
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

import java.util.Vector;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.compendium.core.ICoreConstants;
import com.compendium.core.db.management.*;

/**
 * The DBNodeUserState class serves as the interface layer to the NodeUserState table in
 * the database for the relationship between nodes states and the users in the datamodel.
 *
 * @author	Rema Natarajan / Michelle Bachler / Lakshmi Prabhakaran
 */
public class DBNodeUserState {

	// AUDITED
	/** SQL statement to isert a particular node user state for a given node, for a given user.*/
	public final static String INSERT_STATE_QUERY =
		"INSERT INTO NodeUserState (NodeID, UserID, State) "+
		"VALUES (?, ?, ?) ";

	/** SQL statement to update all users' state, for a node.*/
	public final static String UPDATE_USERS_QUERY =
		"UPDATE NodeUserState " +
		"SET State = ? " +
		"WHERE NodeID = ? " +
		"AND State =  ? " ;

	/** SQL statement to update a node user state record that already exists in the table.*/
	public final static String UPDATE_USER_QUERY =
		"UPDATE NodeUserState " +
		"SET State = ? " +
		"WHERE NodeID = ? " +
		"AND UserID = ? " +
		"AND State = ? ";

	// UNAUDITED
	/** SQL statement to get all users (UserID only) in the database.*/
	public final static String GET_ALL_USERS_QUERY =
		"SELECT UserID "+
		"FROM Users";

	/** SQL statement to get info for given state and NodeID.*/
	public final static String GET_USERIDS_QUERY =
		"SELECT UserID "+
		"FROM NodeUserState "+
		"WHERE NodeID = ? AND State = ?";
	
	/** SQL statement to get info for given state and NodeID.*/
	public final static String GET_USERID_QUERY =
		"SELECT UserID "+
		"FROM NodeUserState "+
		"WHERE NodeID = ? ";

	/**
	 * SQL statement to get UserID, state info for the given NodeID.
	 */
	public final static String GET_USER_QUERY = "SELECT UserID, State " + "FROM NodeUserState " + "WHERE NodeID = ?";

	/** SQL statement to get NodeIDs for given user.*/
	public final static String GET_NODES_QUERY =
		"SELECT NodeID, state "+
		"FROM NodeUserState "+
		"WHERE UserID = ? ";

	/** SQL statement to get state for given user for given node.*/
	public final static String GET_STATE_QUERY =
		"SELECT State "+
		"FROM NodeUserState "+
		"WHERE NodeID = ? " +
		"AND UserID = ? ";


	/**
	 *  Inserts a new State in the database and returns true if successful.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param sNodeID, the id of the node to insert the state for.
	 *	@param sUserID, the id of the user to insert the state for.
	 *	@param state, the state to set for that node and user.
	 *	@return boolean, true if it was successful, else false.
	 *	@throws java.sql.SQLException
	 */
	public static  boolean insert(DBConnection dbcon, String sNodeID, String sUserID, int state) throws SQLException{

		Connection con = dbcon.getConnection();
		if (con == null)
			return false;

		PreparedStatement pstmt = con.prepareStatement(INSERT_STATE_QUERY);

		// CHECK IF RE RECORD EXISTS FIRST AND IF EXISTS - UPDATE
		int nState = getStateInfo(dbcon, sNodeID, sUserID);
		
		if( nState != -1){
			return updateUser(dbcon, sNodeID, sUserID, nState, state);
		} else {
			//do the insert here
	 		pstmt.setString(1, sNodeID);
			pstmt.setString(2, sUserID) ;
			pstmt.setInt(3, state) ;
	
			int nRowCount = pstmt.executeUpdate();
			pstmt.close();
	
			if (nRowCount > 0) {
				if (DBAudit.getAuditOn()) {
					DBAudit.auditNodeUserState(dbcon, DBAudit.ACTION_ADD, sNodeID, sUserID, state);
				}
	
				return true;
			}
			else
				return false;
		}
	}

	/**
	 *	Method to insert the given state -- for all users who presently exist in the database
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param sNodeID, the id of the node to insert the state for.
	 *	@param state, the state to set for that node for all users.
	 *	@return boolean, true if it was successful, else false.
	 *	@throws java.sql.SQLException
	 */
	
	public static boolean insertStateForAllUsers(DBConnection dbcon, String sNodeID, int state) throws SQLException {

		Connection con = dbcon.getConnection();
		if (con == null)
			return false;

		// first get all users right now in the database
		PreparedStatement pstmt = con.prepareStatement(GET_ALL_USERS_QUERY) ;

		ResultSet rs = pstmt.executeQuery();
		boolean sucess = false;
		if (rs != null) {
			pstmt = con.prepareStatement(INSERT_STATE_QUERY) ;

			// insert a new record in NodeUserState for every user.
			while (rs.next()) {

				String	sUserID = rs.getString(1);
				// CHECK IF RE RECORD EXISTS FIRST AND IF EXISTS - UPDATE
				int nState = getStateInfo(dbcon, sNodeID, sUserID);
				
				if(nState != -1){
					sucess = updateUser(dbcon, sNodeID, sUserID, nState, state);
					if (! sucess) {
						break;
					}
				} else {
					//do the insert here
	 				pstmt.setString(1, sNodeID);
					pstmt.setString(2, sUserID) ;
					pstmt.setInt(3, state) ;
					int nRowCount = pstmt.executeUpdate();
	
					if (!(nRowCount > 0)) {
						sucess = false;
						break;
					}
	
					if (DBAudit.getAuditOn())
						DBAudit.auditNodeUserState(dbcon, DBAudit.ACTION_ADD, sNodeID, sUserID, state);
				}
			}
		}
		else {
			sucess = false;
		}
		pstmt.close();

		return sucess;
	}
	
	/**
	 *	Method to insert the given state -- for all users who do not have state info in the database
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param sNodeID, the id of the node to insert the state for.
	 *	@param state, the state to set for that node for all users.
	 *	@return boolean, true if it was successful, else false.
	 *	@throws java.sql.SQLException
	 */
	
	public static boolean insertForUsersWithNoStateInfo(DBConnection dbcon, String sNodeID, int state) throws SQLException {

		Connection con = dbcon.getConnection();
		if (con == null)
			return false;

		// first get all users right now in the database
		PreparedStatement pstmt = con.prepareStatement(GET_ALL_USERS_QUERY) ;

		ResultSet rs = pstmt.executeQuery();
		boolean sucess = false;
		if (rs != null) {
			pstmt = con.prepareStatement(INSERT_STATE_QUERY) ;

			// insert a new record in NodeUserState for every user.
			while (rs.next()) {

				String	sUserID = rs.getString(1);
				// CHECK IF RE RECORD EXISTS FIRST AND IF EXISTS - UPDATE
				int nState = getStateInfo(dbcon, sNodeID, sUserID);
				
				if(nState == -1){
					
					//do the insert here
	 				pstmt.setString(1, sNodeID);
					pstmt.setString(2, sUserID) ;
					pstmt.setInt(3, state) ;
					int nRowCount = pstmt.executeUpdate();
	
					if (!(nRowCount > 0)) {
						sucess = false;
						break;
					}
	
					if (DBAudit.getAuditOn())
						DBAudit.auditNodeUserState(dbcon, DBAudit.ACTION_ADD, sNodeID, sUserID, state);
				}
			}
		}
		else {
			sucess = false;
		}
		pstmt.close();

		return sucess;
	}


	/**
	 *	Updates the status of all users for a node in the table and return if successful.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param sNodeID, the id of the node to set the state for.
	 *	@param oldState, the old state value for that node in this table.
	 *	@param newState, the new state value that is used for replacement.
	 *	@return boolean, true if it was successful, else false.
	 *	@throws java.sql.SQLException
	 */
	public static boolean updateUsers(DBConnection dbcon, String sNodeID, int oldState, int newState) throws SQLException {
		
		if(newState == oldState)
			return false;
		
		Connection con = dbcon.getConnection();
		if (con == null)
			return false;
		
		
		PreparedStatement pstmt = con.prepareStatement(UPDATE_USERS_QUERY);

		pstmt.setInt(1, newState) ;
 		pstmt.setString(2, sNodeID);
		pstmt.setInt(3, oldState) ;

		int nRowCount = pstmt.executeUpdate();

		// close pstmt to save resources
		pstmt.close();

		if (nRowCount > 0) {
			if (DBAudit.getAuditOn()) {
				Vector data = DBNodeUserState.getUserIDs(dbcon, sNodeID, oldState);
 				int count = data.size();
				for (int i=0; i<count; i++) {
					DBAudit.auditNodeUserState(dbcon, DBAudit.ACTION_EDIT, sNodeID, (String)data.elementAt(0), newState);
				}
			}

			return true;
		}
		else
			return false;
	}

	/**
	 *	Updates a node user state record in the table and returns if successful.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param sNodeID, the id of the node to set the state for.
	 *	@param sNodeID, the id of the user to set the state for.
	 *	@param oldState, the old state value for that node in this table.
	 *	@param newState, the new state value that is used for replacement.
	 *	@return boolean, true if it was successful, else false.
	 *	@throws java.sql.SQLException
	 */
	public static boolean updateUser(DBConnection dbcon, String sNodeID, String sUserID, int oldState, int newState)
				throws SQLException {

		if(newState == oldState)
			return false;
		
		Connection con = dbcon.getConnection();
		if (con == null)
			return false;
		
		
		PreparedStatement pstmt = con.prepareStatement(UPDATE_USER_QUERY);

		pstmt.setInt(1, newState) ;
 		pstmt.setString(2, sNodeID);
		pstmt.setString(3, sUserID) ;
		pstmt.setInt(4, oldState) ;

		int nRowCount = pstmt.executeUpdate();

		pstmt.close() ;
		if (nRowCount > 0) {
			if (DBAudit.getAuditOn()) {
				DBAudit.auditNodeUserState(dbcon, DBAudit.ACTION_EDIT, sNodeID, sUserID, newState);
			}

			return true;
		}
		else
			return false;
	}

// UNAUDITED

	/**
	 *  Retrieves the user ids for the given node id and state from the database.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param sNodeID, the id of the node to retrieve the user ids for.
	 *	@param state, the state value to retireve the user ids for.
	 *	@return Vector, of user ids of given state and node found, else empty.
	 *	@throws java.sql.SQLException
	 */
	public static Vector getUserIDs(DBConnection dbcon, String sNodeID, int state) throws SQLException {

		Vector items = new Vector(51);

		Connection con = dbcon.getConnection();
		if (con == null)
			return items;

		PreparedStatement pstmt = con.prepareStatement(GET_USERIDS_QUERY);
		pstmt.setString(1, sNodeID);
		pstmt.setInt(2, state);

		ResultSet rs = pstmt.executeQuery();
		if (rs != null) {
			while (rs.next()) {
				items.addElement(rs.getString(1));
			}
		}
		pstmt.close();
		return items;
	}

	/**
	 * 	Retrieves the user ids and states for the given sNodeID from the database.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param sNodeID, the id of the node to retrieve the user ids and states for.
	 *	@return Vector, of Vectors where the inner Vectors hold: 0=UserID, 1=State.
	 *	@throws java.sql.SQLException
	 */
	public static Vector getUsers(DBConnection dbcon,  String sNodeID) throws SQLException {

		Vector items = new Vector(51);

		Connection con = dbcon.getConnection();
		if (con == null)
			return items;

		PreparedStatement pstmt = con.prepareStatement(GET_USER_QUERY);
		pstmt.setString(1, sNodeID);
		ResultSet rs = pstmt.executeQuery();

		if (rs != null) {
			while (rs.next()) {
				Vector item = new Vector(2);
				item.addElement(rs.getString(1));
				item.addElement(new Integer(rs.getInt(2)));
				items.addElement(item);
			}
		}
		pstmt.close();
		return items;
	}

	/**
	 * 	Retrieves the node ids and states for the given sUserID from the database.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param sUserID, the id of the user to retrieve the node ids and states for.
	 *	@return Vector, of Vectors where the inner Vectors hold: 0=NodeID, 1=State.
	 *	@throws java.sql.SQLException
	 */
	public static Vector getNodes(DBConnection dbcon,  String sUserID) throws SQLException {

		Vector items = new Vector(51);

		Connection con = dbcon.getConnection();
		if (con == null)
			return items;

		PreparedStatement pstmt = con.prepareStatement(GET_NODES_QUERY);
		pstmt.setString(1, sUserID);
		ResultSet rs = pstmt.executeQuery();

		if (rs != null) {
			while (rs.next()) {
				Vector item = new Vector(2);
				item.addElement(rs.getString(1));
				item.addElement(new Integer(rs.getInt(2)));
				items.addElement(item);
			}
		}
		pstmt.close();
		return items;
	}

	/**
	 *  Retrieves the state (int value) for the given object for the given user from the database and returns it.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param sNodeID, the id of the node to retrieve the state for.
	 *	@param sUserID, the id opf the user to retrieve the state for.
	 *	@return int, the sdtate for the given user id and node id.
	 *	@throws java.sql.SQLException`
	 */
	public static int get(DBConnection dbcon, String sNodeID, String sUserID) throws SQLException {
		Connection con = dbcon.getConnection();
		if (con == null)
			return -1;

		PreparedStatement pstmt = con.prepareStatement(GET_STATE_QUERY);

		pstmt.setString(1, sNodeID);
		pstmt.setString(2, sUserID);

		ResultSet rs = pstmt.executeQuery();

		int nState = -1 ;
		if (rs != null) {
			while (rs.next()) {
				nState = rs.getInt(1);
			}
		}
		
		if(nState == -1){
			insertForUsersWithNoStateInfo(dbcon, sNodeID, ICoreConstants.UNREADSTATE);
			nState = ICoreConstants.UNREADSTATE;
		}
		pstmt.close();
		return nState;
	}
	
	/**
	 *  Retrieves the state (int value) for the given object for the given user from the database and returns it.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param sNodeID, the id of the node to retrieve the state for.
	 *	@param sUserID, the id opf the user to retrieve the state for.
	 *	@return int, the sdtate for the given user id and node id.
	 *	@throws java.sql.SQLException`
	 */
	private static int getStateInfo(DBConnection dbcon, String sNodeID, String sUserID) throws SQLException {
		Connection con = dbcon.getConnection();
		if (con == null)
			return -1;

		PreparedStatement pstmt = con.prepareStatement(GET_STATE_QUERY);

		pstmt.setString(1, sNodeID);
		pstmt.setString(2, sUserID);

		ResultSet rs = pstmt.executeQuery();

		int state = -1;
		if (rs != null) {
			while (rs.next()) {
				state = rs.getInt(1);
			}
		}
		pstmt.close();
		return state;
	}
}
