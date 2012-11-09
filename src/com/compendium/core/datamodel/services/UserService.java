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

package com.compendium.core.datamodel.services;

import java.sql.*;
import java.util.*;

import com.compendium.core.datamodel.*;
import com.compendium.core.db.*;
import com.compendium.core.db.management.*;
import com.compendium.core.ICoreConstants;

/**
 *	The interface for the UserService class
 *	The user service class provides remote services to manipuate user objects.
 *
 *	@author Sajid and Rema / Michelle Bachler
 */
public class UserService extends ClientService implements IUserService, java.io.Serializable {


	/**
	 *	Constructor.
	 */
	public  UserService() {
		super() ;
	}

	/**
	 * Constructor, set the name of the service.
	 *
	 * @param String sName, the name of this service.
	 */
	public  UserService(String sName) {
		super(sName);
	}

	/**
	 * Constructor.
	 *
	 * @param String name, the unique name of this service
 	 * @param ServiceManager sm, the current ServiceManager
	 * @param DBDtabaseManager dbMgr, the current DBDatabaseManager
	 */
	public  UserService(String name, ServiceManager sm,  DBDatabaseManager dbMgr) {
		super(name, sm, dbMgr) ;
	}

	/**
	 * Gets a Vector of all user profiles currently in the database.
	 * Generally used when Administrator makes changes to user profiles.
	 *
	 * @param String modelName, the name of the database to access.
	 * @exception java.sql.SQLException
	 */
	public Vector getUsers(String modelName, String userID) throws SQLException {

		DBConnection dbcon = getDatabaseManager().requestConnection(modelName) ;

		Vector users = DBUser.getUsers(dbcon, userID);

		getDatabaseManager().releaseConnection(modelName,dbcon);

		return users;
	}

	/**
	 * Gets a Hashtable of all user homeview ids currently in the database.
	 *
	 * @param PCSession session, the PCSession object for the database to use.
	 * @return a hastable mapping HomeView id to user name.	 * 
	 * @exception java.sql.SQLException
	 */
	public Hashtable getHomeViews(PCSession session) throws SQLException {

		DBConnection dbcon = getDatabaseManager().requestConnection(session.getModelName());

		Hashtable homeviews = DBUser.getHomeViews(dbcon);

		getDatabaseManager().releaseConnection(session.getModelName(), dbcon);

		return homeviews;
	}

	/**
	 * Gets a Hashtable of all user link view (inboxes) ids currently in the database.
	 *
	 * @param PCSession session, the PCSession object for the database to use.
	 * @return a hastable mapping LinkView id to user name.
	 * @exception java.sql.SQLException
	 */
	public Hashtable getLinkViews(PCSession session) throws SQLException {

		DBConnection dbcon = getDatabaseManager().requestConnection(session.getModelName());

		Hashtable homeviews = DBUser.getLinkViews(dbcon);

		getDatabaseManager().releaseConnection(session.getModelName(), dbcon);

		return homeviews;
	}
	
	/**
	 * Inserts a new user in the database.
	 *
	 * @param PCSession session, the PCSession object for the database to use.
	 * @param String sUserID, the user id the identifier.
	 * @param String sAuthor, the author name of this user.
	 * @param Date dCreationDate, the creation date of this user record.
	 * @param Date dModificationDate, the date this user record was last modified.
	 * @param String sLoginName, the login name of this user record.
	 * @param string sUserName, the user name of this record.
	 * @param String sPassword, the pasword of this user record.
	 * @param String sDescription, the description of this user.
	 * @param String sHomeViewID, the identifier of the View record that is this user's home view.
	 * @param boolean bIsAdministrator, true if this user is an administrator, else false.
	 * @exception java.sql.SQLException
	 */
//	public UserProfile insertUserProfile(PCSession session, String sUserID, String sAuthor, java.util.Date dCreationDate,
//			java.util.Date dModificationDate, String sLoginName, String sUserName, String sPassword,
//			String sUserDescription, String sHomeViewID, boolean bIsAdministrator)
//			throws SQLException {
//
//		return insertUserProfile(session, sUserID, sAuthor, dCreationDate,
//				dModificationDate, sLoginName, sUserName, sPassword,
//				sUserDescription, sHomeViewID, bIsAdministrator, "", ICoreConstants.STATUS_ACTIVE);
//	}

	/**
	 * Inserts a new user in the database.
	 *
	 * @param session the PCSession object for the database to use.
	 * @param sUserID the user id the identifier.
	 * @param sAuthor the author name of this user.
	 * @param dCreationDate the creation date of this user record.
	 * @param dModificationDate the date this user record was last modified.
	 * @param sLoginName the login name of this user record.
	 * @param sUserName the user name of this record.
	 * @param sPassword the pasword of this user record.
	 * @param sDescription the description of this user.
	 * @param sHomeViewID the identifier of the View record that is this user's home view.
	 * @param bIsAdministrator true if this user is an administrator, else false.
	 * @param sLinkViewID the identifier of this user's inbox.
	 * @exception java.sql.SQLException
	 */
	public UserProfile insertUserProfile(PCSession session, String sUserID, String sAuthor, java.util.Date dCreationDate,
			java.util.Date dModificationDate, String sLoginName, String sUserName, String sPassword,
			String sUserDescription, String sHomeViewID, boolean bIsAdministrator, String sLinkViewID, int iActiveStatus)
			throws SQLException {

		String modelName = session.getModelName();

		DBConnection dbcon = getDatabaseManager().requestConnection(modelName) ;

		UserProfile up = DBUser.insert(	dbcon, sUserID, sAuthor, dCreationDate,
								dModificationDate, sLoginName, sUserName, sPassword,
								sUserDescription, sHomeViewID, bIsAdministrator, sLinkViewID, iActiveStatus);

		getDatabaseManager().releaseConnection(modelName,dbcon);

		return up;
	}	
	
	/**
	 * This method returns the user profile for a given user for a given project.
	 *
	 * @param sModelName the name of the database to access.
	 * @param sLoginName the login name of this user record.
	 * @param sPassword the pasword of this user record.
	 * @return Userprofile for the given login anme and password.
	 * @param The session object that contains user and project information
	 *
	 * @exception java.sql.SQLException
	 */
	public UserProfile getUserProfile(String sModelName, String sLoginName, String sPassword) throws SQLException {

		DBConnection dbcon = getDatabaseManager().requestConnection(sModelName) ;

		UserProfile up = DBUser.getUserProfile(dbcon, sLoginName, sPassword);

		getDatabaseManager().releaseConnection(sModelName, dbcon);

		return up;
	}

	/**
	 *	This method returns the user profile for a given user id for a given project
	 *
	 *	@return the Userprofile
	 *	@param The session object that contains user and project information
	 *	@param The id of the user
	 *
	 *	@exception java.sql.SQLException
	 */
	public UserProfile getUserProfileFromID(PCSession session, String id) throws SQLException {

		String modelName = session.getModelName();

		DBConnection dbcon = getDatabaseManager().requestConnection(modelName) ;

		UserProfile up = DBUser.getUserProfileFromID(dbcon, id);

		getDatabaseManager().releaseConnection(modelName,dbcon);

		return up;
	}

	/**
	 * Deletes the user with the given id from the database.
	 *
	 * @param session the PCSession object for the database to use.
	 * @param sUserID the identifier of the user to delete.
	 * @exception java.sql.SQLException
	 */
	public boolean deleteUserProfile(PCSession session, String sUserID) throws SQLException {

		String modelName = session.getModelName();

		DBConnection dbcon = getDatabaseManager().requestConnection(modelName) ;

		boolean deleted = DBUser.delete(dbcon, sUserID);

		getDatabaseManager().releaseConnection(modelName,dbcon);

		return deleted;
	}

	/**
	 * Sets the home view and returns if successful.
	 *
	 * @param session the PCSession object for the database to use.
	 * @param sUserID the identifier of the user to set the home view for.
	 * @param sViewID the id of the view which is the user's home view.
	 * @exception java.sql.SQLException
	 */
	public boolean setHomeView(PCSession session, String sUserID, String sViewID) throws SQLException {

		String modelName = session.getModelName();

		DBConnection dbcon = getDatabaseManager().requestConnection(modelName) ;

		boolean successful = DBUser.setHomeView(dbcon, sUserID, sViewID);

		getDatabaseManager().releaseConnection(modelName,dbcon);

		return successful;
	}


	/**
	 *	CURRENTLY NOT IMPLEMENTED.
	 *	<p>
	 *	Returns the home view of the user (AOK)
	 *
	 * @param session the PCSession object for the database to use.
	 * @param sUserID the id of the user whose home view to return.
	 * @return IView the hove view for the given user id.
	 * @exception java.sql.SQLException
	 */
	public IView getHomeView(PCSession session, String sUserID) throws SQLException {
		// String modelName = session.getModelName();
		// get from db and return
		return null;
	}

	/**
	 * Sets the link view (inbox) and returns if successful.
	 *
	 * @param session the PCSession object for the database to use.
	 * @param sUserID the identifier of the user to set the link view id for.
	 * @param sViewID the id of the view which is the user's inbox.
	 * @exception java.sql.SQLException
	 */
	public boolean setLinkView(PCSession session, String sUserID, String sViewID) throws SQLException {

		String modelName = session.getModelName();

		DBConnection dbcon = getDatabaseManager().requestConnection(modelName) ;

		boolean successful = DBUser.setLinkView(dbcon, sUserID, sViewID);

		getDatabaseManager().releaseConnection(modelName, dbcon);

		return successful;
	}


	/**
	 *	CURRENTLY NOT IMPLEMENTED.
	 *	<p>
	 *	Returns the link view of the user (AOK)
	 *
	 * @param PCSession session, the PCSession object for the database to use.
	 * @param String sUserID, the id of the user whose home view to return.
	 * @return IView, the hove view for the given user id.
	 * @exception java.sql.SQLException
	 */
	public IView getLinkView(PCSession session, String sUserID) throws SQLException {
		// String modelName = session.getModelName();
		// get from db and return
		return null;
	}	
	
	/**
	 *	CURRENTLY NOT IMPLEMENTED.
	 *	<p>
	 * This method is called by the permission objects to register change of permission for
	 * a single object for a group in the project. The object can be a node type or view type only.
	 *
	 * @param PCSession session, the PCSession object for the database to use.
	 * @param String sGroupID, the id of the group to set the permissions against.
	 * @param String sObjectID, the id of the object to set the permissions against.
	 * @param String sOldValue, the old permission value.
	 * @param String sNewValue, the new permission value.
	 * @exception java.sql.SQLException
	 */
	public void setPermission(PCSession session, String sGroupID, String sObjectID, String sOldValue, String sNewValue)
			throws SQLException {
		// String modelName = session.getModelName() ;
		// call db and update
	}

	/**
	 * CURRENTLY NOT IMPLEMENTED.
	 * <p>
	 * Returns the User Login name of the user.
	 *
	 * @param PCSession session, the PCSession object for the database to use.
	 * @param String sUserID, the id of the user whose login name to return.
	 * @return String, the login name for the given user id.
	 * @exception java.sql.SQLException
	 */
	public String getLoginName(PCSession session, String sUserID) throws SQLException {
		// String modelName = session.getModelName() ;
		// get from db and return
		return null ;
	}

	/**
	 *	CURRENTLY NOT IMPLEMENTED.
	 *	<p>
	 * Sets the user login name of the user with the given id.
	 *
	 * @param PCSession session, the PCSession object for the database to use.
	 * @param String sUserID, the id of the user whose login name to set.
	 * @param String oldValue, the old login name value.
	 * @param String newValue, the new value to set the login name to.
	 * @exception java.sql.SQLException
	 */
	public void setLoginName(PCSession session, String sUserID, String oldValue, String newValue) throws SQLException {
		// String modelName = session.getModelName() ;
		// call db and update
	}

	/**
	 *	CURRENTLY NOT IMPLEMENTED.
	 *	<p>
	 *	returns the User Login name of the user
	 *
	 *	@exception java.sql.SQLException
	 */
	public String getUserName(PCSession session, String userId) throws SQLException {
		// String modelName = session.getModelName() ;
		// get from db and return
		return null ;
	}

	/**
	 *	CURRENTLY NOT IMPLEMENTED.
	 *	<p>
	 * Sets the user name of the user with the given id.
	 *
	 * @param PCSession session, the PCSession object for the database to use.
	 * @param String sUserID, the id of the user whose user name to set.
	 * @param String oldValue, the old user name value.
	 * @param String newValue, the new value to set the user name to.
	 * @exception java.sql.SQLException
	 */
	public void setUserName(PCSession session, String sUserID, String oldValue, String newValue) throws SQLException {
		// String modelName = session.getModelName() ;
		// call db and update
	}

	/**
	 *	CURRENTLY NOT IMPLEMENTED.
	 *	<p>
	 * Returns the User Password of the user
	 *
	 * @param PCSession session, the PCSession object for the database to use.
	 * @param String sUserID, the id of the user whose password to return.
	 * @return String, the password for the given user id.
	 * @exception java.sql.SQLException
	 */
	public String getPassword(PCSession session, String sUserID) throws SQLException {
		// String modelName = session.getModelName() ;
		// get from db and return
		return null ;
	}

	/**
	 *	CURRENTLY NOT IMPLEMENTED.
	 *	<p>
	 * Sets the user password of the user with the given id.
	 *
	 * @param PCSession session, the PCSession object for the database to use.
	 * @param String sUserID, the id of the user whose password to set.
	 * @param String oldValue, the old password value.
	 * @param String newValue, the new value to set the password to.
	 * @exception java.sql.SQLException
	 */
	public void setPassword(PCSession session, String sUserID, String oldValue, String newValue) throws SQLException {
		// String modelName = session.getModelName() ;
		// call db and update
	}	//setPassword

	/**
	 *	CURRENTLY NOT IMPLEMENTED.
	 *	<p>
	 * Returns the User description of the user
	 *
	 * @param PCSession session, the PCSession object for the database to use.
	 * @param String sUserID, the id of the user whose description to return.
	 * @return String, the description for the given user id.
	 * @exception java.sql.SQLException
	 */
	public String getUserDescription(PCSession session, String sUserID) throws SQLException {
		// String modelName = session.getModelName() ;
		// get from db and return
		return null;
	}

	/**
	 * CURRENTLY NOT IMPLEMENTED.
	 * <p>
	 * Sets the userDescription of the user with the given id.
	 *
	 * @param PCSession session, the PCSession object for the database to use.
	 * @param String sUserID, the id of the user whose description to set.
	 * @param String oldValue, the old description value.
	 * @param String newValue, the new value to set the description to.
	 * @exception java.sql.SQLException
	 */
	public void setUserDescription(PCSession session, String sUserID, String oldValue, String newValue) throws SQLException {
		// String modelName = session.getModelName() ;
		// call db and update
	}

	/**
	 * CURRENTLY NOT IMPLEMENTED.
	 * <p>
	 * Returns true if this user has administrator previledges
	 *
	 * @param PCSession session, the PCSession object for the database to use.
	 * @param String sUserID, the id of the user whose administration status to return.
	 * @return boolean, true if the user is an administrator, else false.
	 * @exception java.sql.SQLException
	 */
	public boolean isAdministrator(PCSession session, String sUserID) throws SQLException {
		// String modelName = session.getModelName();
		// get from db and return
		return false ;
	}

	/**
	 * CURRENTLY NOT IMPLEMENTED.
	 * <p>
	 * Sets the adminiatrator status of the user with the given id.
	 *
	 * @param PCSession session, the PCSession object for the database to use.
	 * @param String sUserID, the id of the user whose administration status to set.
	 * @param boolean oldValue, the old administrator value.
	 * @param boolean newValue, the new value to set the administrator status to.
	 * @exception java.sql.SQLException
	 */
	public void setAdministrator(PCSession session, String sUserID, boolean oldValue, boolean newValue) throws SQLException {
		// String modelName = session.getModelName() ;
		// call db and update
	}
	
	/**
	 * CURRENTLY NOT IMPLEMENTED.
	 * 
	 * 	Sets the CurrentStatus field for the given user id and returns if successful.
	 *
	 *	@param PCSession session, the PCSession object for the database to use.
	 *	@param sUserID, the id of the user whose link view to set.
	 *	@param iCurrentStatus, the User's Status (active/inactive).
	 *	@return boolean, true if it was successful, else false.
	 *	@throws java.sql.SQLException
	 */
	public boolean setCurrentStatus(PCSession session, String sUserID, int iCurrentStatus) throws SQLException {
		return true;
	}
}
