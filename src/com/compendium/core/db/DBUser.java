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
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;

import com.compendium.core.datamodel.*;
import com.compendium.core.db.management.*;
import com.compendium.core.ICoreConstants;

/**
 * The DBUser class serves as the interface layer between the UserProfile  objects
 * and the User table in the database.
 *
 * @author	Rema Natarajan / Michelle Bachler
 */
public class DBUser {

	// TO DO: The performance of the database access can be improved by
	//				storing the prepared statements on a per connection basis.

	// AUDITED
	/** SQL statement to insert a new user profile in the User table.*/
//	public final static String INSERT_USER_QUERY =
//		"INSERT INTO Users (UserID, Author, CreationDate, ModificationDate, " +
//		"Login, Name, Password, Description, " +
//		"HomeView, IsAdministrator) "+
//		"VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ";

	/** SQL statement to insert a new user profile in the User table with the User Link View field (inbox).*/
	public final static String INSERT_USER_WITH_LINKVIEW_QUERY =
		"INSERT INTO Users (UserID, Author, CreationDate, ModificationDate, " + //$NON-NLS-1$
		"Login, Name, Password, Description, " + //$NON-NLS-1$
		"HomeView, IsAdministrator, CurrentStatus, LinkView) "+ //$NON-NLS-1$
		"VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) "; //$NON-NLS-1$
	
	/** SQL statement to update the user profiles.*/
//	public final static String UPDATE_USER_QUERY =
//		"UPDATE Users " +
//		"SET Author = ?, CreationDate = ?, ModificationDate = ?, " +
//		"Login = ?, Name = ?, Password = ?, Description = ?, HomeView = ?, IsAdministrator = ? " +
//		"WHERE UserID = ? ";

	/** SQL statement to update the user profiles including the link view field.*/
	public final static String UPDATE_USER_WITH_LINKVIEW_QUERY =
		"UPDATE Users " + //$NON-NLS-1$
		"SET Author = ?, CreationDate = ?, ModificationDate = ?, " + //$NON-NLS-1$
		"Login = ?, Name = ?, Password = ?, Description = ?, HomeView = ?, IsAdministrator = ?, LinkView = ?, CurrentStatus = ? " + //$NON-NLS-1$
		"WHERE UserID = ? "; //$NON-NLS-1$
	
	/** SQL statement to update a users home view id.*/
	public final static String UPDATE_HOMEVIEW_QUERY =
		"UPDATE Users " + //$NON-NLS-1$
		"SET HomeView = ? "+ //$NON-NLS-1$
		"WHERE UserID = ?"; //$NON-NLS-1$

	/** SQL statement to update a users link view id (inbox).*/
	public final static String UPDATE_LINKVIEW_QUERY =
		"UPDATE Users " + //$NON-NLS-1$
		"SET LinkView = ? "+ //$NON-NLS-1$
		"WHERE UserID = ?"; //$NON-NLS-1$
	
	/** SQL statement to delete a user profile from the User table.*/
	public final static String DELETE_USER_QUERY =
		"DELETE "+ //$NON-NLS-1$
		"FROM Users "+ //$NON-NLS-1$
		"WHERE UserID = ? "; //$NON-NLS-1$


	// UNAUDITED
	
	/** SQL statement to update a users Status (active or inactive). */
	public final static String UPDATE_CURRENTSTATUS_QUERY =
		"UPDATE Users " + //$NON-NLS-1$
		"SET CurrentStatus = ? "+ //$NON-NLS-1$
		"WHERE UserID = ?"; //$NON-NLS-1$
	
	/** SQL statement to get user profile for the user with the given user login name and password, only when user logs on.*/
	public final static String GET_USER_QUERY =
		"SELECT UserID, Author, CreationDate, ModificationDate, " + //$NON-NLS-1$
		"Login, Name, Password, Description, HomeView, IsAdministrator, CurrentStatus, LinkView "+ //$NON-NLS-1$
		"FROM Users "+ //$NON-NLS-1$
		"WHERE Login = ? AND Password = ?"; //$NON-NLS-1$

	/** SQL statement to get user profile for the user with the given user id.*/
	public final static String GET_USER_FROM_ID_QUERY =
		"SELECT UserID, Author, CreationDate, ModificationDate, " + //$NON-NLS-1$
		"Login, Name, Password, Description, HomeView, IsAdministrator, CurrentStatus, LinkView "+ //$NON-NLS-1$
		"FROM Users "+ //$NON-NLS-1$
		"WHERE UserID = ?"; //$NON-NLS-1$

	/** SQL statement to get user name for the user with the given user id.*/
	public final static String GET_USERNAME_FROM_ID_QUERY =
		"SELECT Name FROM Users "+ //$NON-NLS-1$
		"WHERE UserID = ?"; //$NON-NLS-1$
	
	/** SQL statement to get user profile for the user with the given userid.*/
	public final static String GET_USERDATA_QUERY =
		"SELECT UserID, Author, CreationDate, ModificationDate, " + //$NON-NLS-1$
		"Login, Name, Password, Description, HomeView, IsAdministrator, CurrentStatus, LinkView "+ //$NON-NLS-1$
		"FROM Users "+ //$NON-NLS-1$
		"WHERE UserID = ?"; //$NON-NLS-1$

	/** SQL statement to get all the user home views.*/
	public final static String GET_HOMEVIEW_QUERY =
		"Select HomeView, Name " + //$NON-NLS-1$
		"FROM Users"; //$NON-NLS-1$

	/** SQL statement to get all the user link views.*/
	public final static String GET_LINKVIEW_QUERY =
		"Select LinkView, Name " + //$NON-NLS-1$
		"FROM Users"; //$NON-NLS-1$
	
	/** SQL statement to get all user profiles.*/
	public final static String GET_ALL_USERS =
		"SELECT UserID, Author, CreationDate, ModificationDate, " + //$NON-NLS-1$
		"Login, Name, Password, Description, HomeView, IsAdministrator, CurrentStatus, LinkView "+ //$NON-NLS-1$
		"FROM Users " + //$NON-NLS-1$
		"ORDER BY CurrentStatus, Name"; //$NON-NLS-1$


	/**
	 * 	Inserts a new user profile in the table and returns it.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param sUserID, the id of the new user.
	 *	@param sAuthor, the author name of the new user.
	 * 	@param dCreationDate java.util.Date, the creation date for the new user.
	 *	@param dModificationDate java.util.Date, the last modification date for the new user (same as creation).
	 *	@param sLoginName, the login name of the new user.
	 *	@param sUserName, the user name of the new user.
	 *	@param sPassword, the password for the new user.
	 *	@param sUserDescription, the description of the new user.
	 *	@param sHomeViewID, the id of the new user's home view.
	 *  @param isAdministrator, is this new user an administrator?
	 *	@return boolean, true if it was successful, else false.
	 *	@throws java.sql.SQLException
	 */
//	public static UserProfile insert(DBConnection dbcon, String sUserID, String sAuthor, java.util.Date dCreationDate,
//			java.util.Date dModificationDate, String sLoginName, String sUserName, String sPassword,
//			String sUserDescription, String sHomeViewID, boolean isAdministrator)
//				throws SQLException {
//
//		return insert(dbcon, sUserID, sAuthor, dCreationDate,
//				dModificationDate, sLoginName, sUserName, sPassword,
//				sUserDescription, sHomeViewID, isAdministrator, "", ICoreConstants.STATUS_ACTIVE);
//	}

	/**
	 * 	Inserts a new user profile in the table and returns it.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param sUserID the id of the new user.
	 *	@param sAuthor the author name of the new user.
	 * 	@param dCreationDate the creation date for the new user.
	 *	@param dModificationDate the last modification date for the new user (same as creation).
	 *	@param sLoginName the login name of the new user.
	 *	@param sUserName the user name of the new user.
	 *	@param sPassword the password for the new user.
	 *	@param sUserDescription the description of the new user.
	 *	@param sHomeViewID the id of the new user's home view.
	 *  @param isAdministrator is this new user an administrator?
	 *  @param sLinkViewID the id of the user's inbox.
	 *	@return boolean, true if it was successful, else false.
	 *	@throws java.sql.SQLException
	 */
	public static UserProfile insert(DBConnection dbcon, String sUserID, String sAuthor, java.util.Date dCreationDate,
			java.util.Date dModificationDate, String sLoginName, String sUserName, String sPassword,
			String sUserDescription, String sHomeViewID, boolean isAdministrator, String sLinkViewID, int iActiveStatus)
				throws SQLException {

		Connection con = dbcon.getConnection();
		if (con == null)
			return null;

		//check if this user already exists, do an update on the record.
		UserProfile up = getUser(dbcon, sUserID);
		if(up != null) {
			up = DBUser.update(dbcon, sUserID, sAuthor, dCreationDate,
						dModificationDate, sLoginName, sUserName, sPassword,
						sUserDescription, sHomeViewID, isAdministrator, sLinkViewID, iActiveStatus);
			return up;
		}

		PreparedStatement pstmt = con.prepareStatement(INSERT_USER_WITH_LINKVIEW_QUERY);

		String admin = "N"; //$NON-NLS-1$
		if (isAdministrator)
			admin = "Y"; //$NON-NLS-1$

		pstmt.setString(1, sUserID);
		pstmt.setString(2, sAuthor);
		pstmt.setDouble(3, new Long(dCreationDate.getTime()).doubleValue());
		pstmt.setDouble(4, new Long(dModificationDate.getTime()).doubleValue());
		pstmt.setString(5, sLoginName);
		pstmt.setString(6, sUserName);
		pstmt.setString(7, sPassword);
		pstmt.setString(8, sUserDescription);
		pstmt.setString(9, sHomeViewID);
		pstmt.setString(10, admin);
		pstmt.setInt(11, iActiveStatus);
		pstmt.setString(12, sLinkViewID);

		int nRowCount = pstmt.executeUpdate();
		pstmt.close();

		if (nRowCount >0) {

			//TODO: get the home view object from the database with the home view id
			View homeView = (View)DBNode.getNodeSummary(dbcon, sHomeViewID, sUserID);

			//TODO: get the link view object from the database with the link view id
			View linkView = (View)DBNode.getNodeSummary(dbcon, sLinkViewID, sUserID);

			//TODO: get the last saved sessions based on the string retrieved from the db
			View [] lastSavedSessionViews = null;

			//TODO: set the right permissions
			int permission = ICoreConstants.WRITEVIEWNODE;
			up = new UserProfile(sUserID, permission, sLoginName, sUserName, sPassword,
								 sUserDescription, homeView, isAdministrator, linkView, iActiveStatus);
			up.setAuthorLocal(sAuthor);

			if (DBAudit.getAuditOn())
				DBAudit.auditUser(dbcon, DBAudit.ACTION_ADD, up);

			return up;
		} else
			return up;
	}	
	
	/**
	 * Updates a user profile in the table and returns it.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param sUserID, the id of the user to update.
	 *	@param sAuthor, the author name of the user.
	 * 	@param dCreationDate java.util.Date, the creation date for the user.
	 *	@param dModificationDate java.util.Date, the last modification date for the user.
	 *	@param sLoginName, the login name of the user.
	 *	@param sUserName, the user name of the user.
	 *	@param sPassword, the password for the user.
	 *	@param sUserDescription, the description of the user.
	 *	@param sHomeViewID, the id of the user's home view.
	 *  @param isAdministrator, is this user an administrator?
	 *	@return boolean, true if it was successful, else false.
	 *	@throws java.sql.SQLException
	 */
//	public static UserProfile update(DBConnection dbcon, String sUserID, String sAuthor, java.util.Date dCreationDate,
//			java.util.Date dModificationDate, String sLoginName, String sUserName, String sPassword,
//			String sUserDescription, String sHomeViewID, boolean isAdministrator, int iActiveStatus)
//				throws SQLException {
//
//		return update(dbcon, sUserID, sAuthor, dCreationDate,
//				dModificationDate, sLoginName, sUserName, sPassword,
//				sUserDescription, sHomeViewID, isAdministrator, "", iActiveStatus);
//	}
	
	/**
	 * Updates a user profile in the table and returns it.
	 *
	 *	@param DBConnection the DBConnection object to access the database with.
	 *	@param sUserID the id of the user to update.
	 *	@param sAuthor the author name of the user.
	 * 	@param dCreationDate the creation date for the user.
	 *	@param dModificationDate the last modification date for the user.
	 *	@param sLoginName the login name of the user.
	 *	@param sUserName the user name of the user.
	 *	@param sPassword the password for the user.
	 *	@param sUserDescription the description of the user.
	 *	@param sHomeViewID the id of the user's home view.
	 *  @param isAdministrator is this user an administrator?
	 *  @param sLinkViewID the id of the user's inbox.
	 *	@return boolean true if it was successful, else false.
	 *	@throws java.sql.SQLException
	 */
	public static UserProfile update(DBConnection dbcon, String sUserID, String sAuthor, java.util.Date dCreationDate,
			java.util.Date dModificationDate, String sLoginName, String sUserName, String sPassword,
			String sUserDescription, String sHomeViewID, boolean isAdministrator, String sLinkViewID, int iActiveStatus)
				throws SQLException {

		Connection con = dbcon.getConnection();
		if (con == null)
			return null;

		PreparedStatement pstmt = con.prepareStatement(UPDATE_USER_WITH_LINKVIEW_QUERY);

		String admin = "N"; //$NON-NLS-1$
		if (isAdministrator)
			admin = "Y"; //$NON-NLS-1$

		pstmt.setString(1, sAuthor);
		pstmt.setDouble(2, new Long(dCreationDate.getTime()).doubleValue());
		pstmt.setDouble(3, new Long(dModificationDate.getTime()).doubleValue());
		pstmt.setString(4, sLoginName);
		pstmt.setString(5, sUserName);
		pstmt.setString(6, sPassword);
		pstmt.setString(7, sUserDescription);
		pstmt.setString(8, sHomeViewID);
		pstmt.setString(9, admin);
		pstmt.setString(10, sLinkViewID);	
		pstmt.setInt(11, iActiveStatus);
		pstmt.setString(12, sUserID);

		int nRowCount = pstmt.executeUpdate();
		pstmt.close();

		UserProfile up = null;
		if (nRowCount >0) {

			//Get the home view object from the database with the home view id
			View homeView = (View)DBNode.getNodeSummary(dbcon, sHomeViewID, sUserID);
			
			//Get the link view object from the database with the link view id
			View linkView = (View)DBNode.getNodeSummary(dbcon, sLinkViewID, sUserID);

			//TODO: get the last saved sessions based on the string retrieved from the db
			View [] lastSavedSessionViews = null;

			//TODO: set the right permissions
			int permission = ICoreConstants.WRITEVIEWNODE;

			up = new UserProfile(sUserID, permission, sLoginName, sUserName, sPassword,
								 sUserDescription, homeView, isAdministrator, linkView, iActiveStatus);
			up.setAuthorLocal(sAuthor);

			if (DBAudit.getAuditOn())
				DBAudit.auditUser(dbcon, DBAudit.ACTION_EDIT, up);

			return up;
		}
		else
			return up;
	}	

	/**
	 *  Deletes a user profile from the table for the given USerID.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param sUserID, the id of the user to delete.
	 *	@return boolean, true if it was successful, else false.
	 *	@throws java.sql.SQLException
	 */
	public static boolean delete(DBConnection dbcon, String sUserID) throws SQLException {

		Connection con = dbcon.getConnection();
		if (con == null)
			return false;

		// IF AUDITING, STORE USER DATA
		UserProfile up = null;
		if (DBAudit.getAuditOn())
			up = DBUser.getUser(dbcon, sUserID);

		PreparedStatement pstmt = con.prepareStatement(DELETE_USER_QUERY);
		pstmt.setString(1, sUserID) ;
		int nRowCount = pstmt.executeUpdate();
		pstmt.close();

		UserProfile user = null ;
		if (nRowCount >0) {
			if (DBAudit.getAuditOn())
				DBAudit.auditUser(dbcon, DBAudit.ACTION_DELETE, up);

			return true;
		} else
			return false;
	}

	/**
	 * 	Sets the home view for the given user id and returns if successful.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param sUserID, the id of the user whose home view to set.
	 *	@param sViewID, the id of the user's home view.
	 *	@return boolean, true if it was successful, else false.
	 *	@throws java.sql.SQLException
	 */
	public static boolean setHomeView(DBConnection dbcon, String sUserID, String sViewID) throws SQLException{

		Connection con = dbcon.getConnection();
		if (con == null)
			return false;

		PreparedStatement pstmt = con.prepareStatement(UPDATE_HOMEVIEW_QUERY);
		pstmt.setString(1, sViewID);
		pstmt.setString(2, sUserID);

		int nRowCount = pstmt.executeUpdate();
		pstmt.close();

		if (nRowCount >0) {
			if (DBAudit.getAuditOn()) {
				UserProfile up = getUser(dbcon, sUserID);
				DBAudit.auditUser(dbcon, DBAudit.ACTION_EDIT, up);
			}
			return true;
		}

		return false;
	}
	
	/**
	 * 	Sets the link view for the given user id and returns if successful.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param sUserID, the id of the user whose link view to set.
	 *	@param sViewID, the id of the user's link view.
	 *	@return boolean, true if it was successful, else false.
	 *	@throws java.sql.SQLException
	 */
	public static boolean setLinkView(DBConnection dbcon, String sUserID, String sViewID) throws SQLException{

		Connection con = dbcon.getConnection();
		if (con == null)
			return false;

		PreparedStatement pstmt = con.prepareStatement(UPDATE_LINKVIEW_QUERY);
		pstmt.setString(1, sViewID);
		pstmt.setString(2, sUserID);

		int nRowCount = pstmt.executeUpdate();
		pstmt.close();

		if (nRowCount >0) {
			if (DBAudit.getAuditOn()) {
				UserProfile up = getUser(dbcon, sUserID);
				DBAudit.auditUser(dbcon, DBAudit.ACTION_EDIT, up);
			}
			return true;
		}

		return false;
	}	

// UNAUDITED
	
	/**
	 * 	Sets the link view for the given user id and returns if successful.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param sUserID, the id of the user whose link view to set.
	 *	@param iCurrentStatus, the User's Status (active/inactive).
	 *	@return boolean, true if it was successful, else false.
	 *	@throws java.sql.SQLException
	 */
	public static boolean setCurrentStatus(DBConnection dbcon, String sUserID, int iCurrentStatus) throws SQLException{

		Connection con = dbcon.getConnection();
		if (con == null)
			return false;

		PreparedStatement pstmt = con.prepareStatement(UPDATE_CURRENTSTATUS_QUERY);
		pstmt.setInt(1, iCurrentStatus);
		pstmt.setString(2, sUserID);

		int nRowCount = pstmt.executeUpdate();
		pstmt.close();

		if (nRowCount >0) {
			return true;
		}
		return false;
	}	

	/**
	 *  Returns all the home view ids from the database
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@return java.util.Hashtable, a list of all the home view id in the User table.
	 *  (key=viewid value=name)*
	 *	@exception java.sql.SQLException
	 */
	public static Hashtable getHomeViews(DBConnection dbcon) throws SQLException {

		Hashtable views = new Hashtable(51);

		Connection con = dbcon.getConnection();
		if (con == null)
			return null;

		PreparedStatement pstmt = con.prepareStatement(GET_HOMEVIEW_QUERY);
		ResultSet rs = pstmt.executeQuery();

		if (rs != null) {
			while (rs.next()) {
				String	sId	= rs.getString(1);
				String sName = rs.getString(2);
				views.put(sId, sName);
			}
		}
		pstmt.close();

		return views;
	}

	/**
	 *  Returns all the link view ids from the database;
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@return java.util.Hashtable, a list of all the link view id in the User table.
	 *  (key=viewid value=name)
	 *	@exception java.sql.SQLException
	 */
	public static Hashtable getLinkViews(DBConnection dbcon) throws SQLException {

		Hashtable views = new Hashtable(51);

		Connection con = dbcon.getConnection();
		if (con == null)
			return null;

		PreparedStatement pstmt = con.prepareStatement(GET_LINKVIEW_QUERY);
		ResultSet rs = pstmt.executeQuery();

		if (rs != null) {
			while (rs.next()) {
				String	sId	= rs.getString(1);
				String sName = rs.getString(2);				
				if (sId != null) {				
					views.put(sId, sName);
				}
			}
		}
		pstmt.close();

		return views;
	}	
	
	/**
	 *  Returns a user profile in the table for the given login name and password.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@return com.compendium.core.datamodel.UserProfile, the UserProfile for the given login nam and password, else null.
	 *	@exception java.sql.SQLException
	 */
	public static UserProfile getUserProfile(DBConnection dbcon, String sLoginName, String sPassword) throws SQLException {

		Connection con = dbcon.getConnection();
		if (con == null)
			return null;

		PreparedStatement pstmt = con.prepareStatement(GET_USER_QUERY);
		pstmt.setString(1, sLoginName) ;
		pstmt.setString(2, sPassword) ;

		ResultSet rs = pstmt.executeQuery();

		UserProfile up = null ;
		if (rs != null) {

			while (rs.next()) {

				String  userId			= rs.getString(1);
				String	author			= rs.getString(2);
				double  creationdate 	= rs.getDouble(3);
				double  moddate			= rs.getDouble(4);
				sLoginName				= rs.getString(5);
				String	userName		= rs.getString(6);
				sPassword				= rs.getString(7);
				String	userDesc		= rs.getString(8);
				String	homeViewId		= rs.getString(9);
				String admin 			= rs.getString(10);
				int		iCurrentStatus 	= rs.getInt(11);
				String	linkViewId		= rs.getString(12);

				boolean isAdministrator = false;
				if (admin.equals("Y")) //$NON-NLS-1$
					isAdministrator = true;

				// for permission, give the user the max permissions since it is his object!
				int permission = ICoreConstants.WRITEVIEWNODE;

				//get the homeview for the user given the homeviewId
				View homeView = (View)DBNode.getNodeSummary(dbcon, homeViewId, userId);
				View linkView = null;
				if (linkViewId != null) {					
					linkView = (View)DBNode.getNodeSummary(dbcon, linkViewId, userId);					
				}

				up = new UserProfile(userId, permission, sLoginName, userName, sPassword,
									 userDesc, homeView, isAdministrator, linkView, iCurrentStatus);
				up.setAuthorLocal(author);
				up.setCreationDateLocal(new Date(new Double(creationdate).longValue()));
				up.setModificationDateLocal(new Date(new Double(moddate).longValue()));				
			}
		}
		pstmt.close();
		return up;
	}

	/**
	 * Returns a user profile in the table.
	 * Only this API doesnt take a session object as this is called before a session object is created
	 */
	public static UserProfile getUserProfileFromID(DBConnection dbcon, String sUserID) throws SQLException {

		Connection con = dbcon.getConnection();
		if (con == null)
			return null;

		PreparedStatement pstmt = con.prepareStatement(GET_USER_FROM_ID_QUERY);
		pstmt.setString(1, sUserID) ;

		ResultSet rs = pstmt.executeQuery();

		UserProfile up = null ;
		if (rs != null) {

			while (rs.next()) {

				String  userId		= rs.getString(1) ;
				String	author		= rs.getString(2) ;
				double  creationdate = rs.getDouble(3) ;
				double  moddate		= rs.getDouble(4) ;
				String 	loginName	= rs.getString(5) ;
				String	userName	= rs.getString(6) ;
				String 	password	= rs.getString(7) ;
				String	userDesc	= rs.getString(8) ;
				String	homeViewId	= rs.getString(9) ;
				String 	admin 		= rs.getString(10);
				int		iCurrentStatus 	= rs.getInt(11);
				String	linkViewId		= rs.getString(12);

				boolean isAdministrator = false;
				if (admin.equals("Y")) //$NON-NLS-1$
					isAdministrator = true;

				// for permission, give the user the max permissions since it is his object!
				int permission = ICoreConstants.WRITEVIEWNODE;

				//get the homeview for the user given the homeviewId
				View homeView = (View)DBNode.getNodeSummary(dbcon, homeViewId, sUserID);
				
				View linkView = null;
				if (linkViewId != null) {					
					linkView = (View)DBNode.getNodeSummary(dbcon, linkViewId, sUserID);					
				}				

				up = new UserProfile(userId, permission, loginName, userName, password,
									 userDesc, homeView, isAdministrator, linkView, iCurrentStatus);
				up.setAuthorLocal(author);
				up.setCreationDateLocal(new Date(new Double(creationdate).longValue()));
				up.setModificationDateLocal(new Date(new Double(moddate).longValue()));				
			}
		}
		pstmt.close();
		return up;
	}
	
	/**
	 * Returns the user name associated with the given UserID.					// mlb: Jan. 08
	 *	@param DBConnection, the DBConnection object to access the database with.
	 *	@param String sUserID - The UserID of the User record we want
	 *	@return String sUserName - the Name field from the User table for the given UserID
	 *	@exception java.sql.SQLException
	 */
	public static String getUserNameFromID(DBConnection dbcon, String sUserID) throws SQLException {

		Connection con = dbcon.getConnection();
		if (con == null)
			return null;

		PreparedStatement pstmt = con.prepareStatement(GET_USERNAME_FROM_ID_QUERY);
		pstmt.setString(1, sUserID) ;

		ResultSet rs = pstmt.executeQuery();

		String sUserName = ""; //$NON-NLS-1$
		if (rs != null) {

			while (rs.next()) {
				sUserName = rs.getString(1) ;
			}
		}
		pstmt.close();
		return sUserName;
	}

	/**
	 *  Returns a user profile in the table for the given user id.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param sUserID, the id of the user to return.
	 *	@return com.compendium.core.datamodel.UserProfile, the UserProfile for the given UserID, else null.
	 *	@exception java.sql.SQLException
	 */
	public static UserProfile getUser(DBConnection dbcon, String sUserID) throws SQLException {

		Connection con = dbcon.getConnection();
		if (con == null)
			return null;

		PreparedStatement pstmt = con.prepareStatement(GET_USERDATA_QUERY);
		pstmt.setString(1, sUserID) ;

		ResultSet rs = pstmt.executeQuery();

		UserProfile up = null ;
		if (rs != null) {

			if (rs.next()) {

				String  userId		= rs.getString(1) ;
				String	author		= rs.getString(2) ;
				double  creationdate = rs.getDouble(3) ;
				double  moddate		= rs.getDouble(4) ;
				String 	loginName	= rs.getString(5) ;
				String	userName	= rs.getString(6) ;
				String	password	= rs.getString(7) ;
				String	userDesc	= rs.getString(8) ;
				String	homeViewId	= rs.getString(9) ;
				String 	admin 		= rs.getString(10) ;
				int		iCurrentStatus 	= rs.getInt(11);
				String	linkViewId		= rs.getString(12);

				boolean isAdministrator = false;
				if (admin.equals("Y")) //$NON-NLS-1$
					isAdministrator = true;

				// for permission, give the user the max permissions since it is his object!
				int permission = ICoreConstants.WRITEVIEWNODE;

				//get the homeview for the user given the homeviewId
				View homeView = (View)DBNode.getNodeSummary(dbcon, homeViewId, sUserID);
				
				View linkView = null;
				if (linkViewId != null) {					
					linkView = (View)DBNode.getNodeSummary(dbcon, linkViewId, sUserID);					
				}

				up = new UserProfile(userId, permission, loginName, userName, password,
									 userDesc, homeView, isAdministrator, linkView, iCurrentStatus);
				up.setAuthorLocal(author);
				up.setCreationDateLocal(new Date(new Double(creationdate).longValue()));
				up.setModificationDateLocal(new Date(new Double(moddate).longValue()));				
			}
		}
		pstmt.close();
		return up;
	}

	/**
	 *  Returns a Vector of all the user in the database.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@return Vector, a list of <code>UserProfile</code> objects, for all the suers in the User table.
	 *	@exception java.sql.SQLException
	 */
	public static Vector getUsers(DBConnection dbcon, String userID) throws SQLException {

		Connection con = dbcon.getConnection();
		if (con == null)
			return null;

		PreparedStatement pstmt = con.prepareStatement(GET_ALL_USERS);
		ResultSet rs = pstmt.executeQuery();

		Vector users = new Vector(51);
		if (rs != null) {

			while (rs.next()) {

				String  userId		= rs.getString(1);
				String	author		= rs.getString(2);
				double  creationdate = rs.getDouble(3);
				double  moddate		= rs.getDouble(4);
				String  loginName	= rs.getString(5);
				String	userName	= rs.getString(6);
				String  password	= rs.getString(7);
				String	userDesc	= rs.getString(8);
				String	homeViewId	= rs.getString(9);
				String  admin 		= rs.getString(10);
				int		iCurrentStatus 	= rs.getInt(11);
				String	linkViewId		= rs.getString(12);
				
				boolean isAdministrator = false;
				if (admin.equals("Y")) //$NON-NLS-1$
					isAdministrator = true;

				// for permission, give the user the max permissions since it is his object!
				int permission = ICoreConstants.WRITEVIEWNODE;
				//get the homeview for the user given the homeviewId
				View homeView = (View)DBNode.getNodeSummary(dbcon, homeViewId, userID);

				View linkView = null;
				if (linkViewId != null) {					
					linkView = (View)DBNode.getNodeSummary(dbcon, linkViewId, userID);					
				}
				
				UserProfile up = new UserProfile(userId, permission, loginName, userName, password,
									 userDesc, homeView, isAdministrator, linkView, iCurrentStatus);
				up.setAuthorLocal(author);
				up.setCreationDateLocal(new Date(new Double(creationdate).longValue()));
				up.setModificationDateLocal(new Date(new Double(moddate).longValue()));

				users.addElement(up);
			}
		}

		pstmt.close();

		return users;
	}
}
