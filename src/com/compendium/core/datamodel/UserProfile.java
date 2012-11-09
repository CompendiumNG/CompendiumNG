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

package com.compendium.core.datamodel;

import java.sql.SQLException;

import com.compendium.core.datamodel.services.*;
import com.compendium.core.ICoreConstants;

/**
 * This class represents a Compendium user	profile
 *
 * Author Rema Natarajan /  Michelle Bachler
 */
public class UserProfile extends IdObject implements IUserProfile, java.io.Serializable {

	/** LoginName property name for use with property change events */
	public final static String USER_LOGIN_NAME = "LoginName";

	/** UserName property name for use with property change events */
	public final static String USER_NAME = "UserName";

	/** UserPassword property name for use with property change events */
	public final static String USER_PASSWORD = "UserPassword";

	/** UserDescription property name for use with property change events */
	public final static String USER_DESCRIPTION = "UserDescription";

	/** HomeView property name for use with property change events */
	public final static String HOMEVIEW = "HomeView";

	/** IsAdministrator property name for use with property change events */
	public final static String IS_ADMINISTRATOR = "IsAdministrator" ;
	
	/** IsActive property name for use with active status change events */
//	public final static String IS_ACTIVE = "IsActive" ;

	/** LinkView property name for use with property change events */
	public final static String LINKVIEW = "LinkView";
	
	/** User's ID		*/
	protected String	sUserID = "";

	/** User's log in name.*/
	protected String 	sLoginName = "" ;

	/** User's full name.*/
	protected String	sUserName = "" ;

	/** User's password.*/
	protected String	sPassword = "" ;

	/** User's description, basically any descriptive data can be put in here.*/
	protected String	sUserDescription =	"" ;

	/** User's home view reference.*/
	protected View		oHomeView = null ;

	/** 
	 * This permission attribute refers to whether this client is an
	 * administrator or an ordinary client.
	 */
	protected boolean bIsAdministrator = false ;
	
	/** Whether the given User account is active or inactive */
	protected boolean bIsActive = true;
	
	/** The View that is the user's InBox.*/
	protected View		oLinkView = null;
	
	/**
	 *	Constructor.
	 */
	public UserProfile() {
		this("", ICoreConstants.NOACCESS) ;
	}

	/**
	 * Constructor.
	 *
	 * @param String sUserID, The unique identifier for this user.
	 * @param int nPermission, the permissions on this user.
	 */
	public UserProfile(String sUserID, int nPermission) {
		super(sUserID, nPermission);
	}

	/**
	 * Constructor.
	 *
	 * @param String sUserID, The unique identifier for this user.
	 * @param int nPermission, the permissions on this user.
	 * @param String sLoginName, the name used by this user to login.
	 * @param String sUserName, the name of the user used as the author name.
	 * @param String sPassword, the password used by this user to login.
	 * @param String sUserDescription, a description of this user.
	 * @param View oHomeView, the home view of this user.
	 * @param boolean bIsAdministrator, true if this user is an administrator, else false.
	 */
//	public UserProfile(String sUserID, int nPermission, String sLoginName, String sUserName, String sPassword,
//							String sUserDescription, View oHomeView, boolean bIsAdministrator) {
//
//		super(sUserID, nPermission);
//		this.sLoginName	= sLoginName;
//		this.sUserName =  sUserName;
//		this.sUserID = sUserID;
//		this.sPassword	=	 sPassword;
//		this.sUserDescription = sUserDescription;
//		this.oHomeView	=	 oHomeView;
//		this.bIsAdministrator = bIsAdministrator;
//	}
	
	/**
	 * Constructor.
	 *
	 * @param String sUserID, The unique identifier for this user.
	 * @param int nPermission, the permissions on this user.
	 * @param String sLoginName, the name used by this user to login.
	 * @param String sUserName, the name of the user used as the author name.
	 * @param String sPassword, the password used by this user to login.
	 * @param String sUserDescription, a description of this user.
	 * @param View oHomeView, the home view of this user.
	 * @param boolean bIsAdministrator, true if this user is an administrator, else false.
	 */
//	public UserProfile(String sUserID, int nPermission, String sLoginName, String sUserName, String sPassword,
//							String sUserDescription, View oHomeView, boolean bIsAdministrator, View oLinkView) {
//
//		super(sUserID, nPermission);
//		this.sLoginName	= sLoginName;
//		this.sUserName =  sUserName;
//		this.sUserID = sUserID;
//		this.sPassword	=	 sPassword;
//		this.sUserDescription = sUserDescription;
//		this.oHomeView	=	 oHomeView;
//		this.bIsAdministrator = bIsAdministrator;
//		this.oLinkView = oLinkView;
//	}
	
	/**
	 * Constructor.
	 *
	 * @param String sUserID, The unique identifier for this user.
	 * @param int nPermission, the permissions on this user.
	 * @param String sLoginName, the name used by this user to login.
	 * @param String sUserName, the name of the user used as the author name.
	 * @param String sPassword, the password used by this user to login.
	 * @param String sUserDescription, a description of this user.
	 * @param View oHomeView, the home view of this user.
	 * @param boolean bIsAdministrator, true if this user is an administrator, else false.
	 */
	public UserProfile(String sUserID, int nPermission, String sLoginName, String sUserName, String sPassword,
							String sUserDescription, View oHomeView, boolean bIsAdministrator, View oLinkView, int iActiveStatus) {

		super(sUserID, nPermission);
		this.sLoginName	= sLoginName;
		this.sUserName =  sUserName;
		this.sUserID = sUserID;
		this.sPassword	=	 sPassword;
		this.sUserDescription = sUserDescription;
		this.oHomeView	=	 oHomeView;
		this.bIsAdministrator = bIsAdministrator;
		this.oLinkView = oLinkView;
		if (iActiveStatus == ICoreConstants.STATUS_ACTIVE) {
			this.bIsActive = true;
		} else {
			this.bIsActive = false;
		}
	}

	/**
	 *	Returns the User Login name of the user.
	 *
	 * @return String, the name used by this user to login.
	 */
	public String getLoginName() {
		return sLoginName ;
	}

	/**
	 *  Sets the user login name, currently in the local data ONLY.
	 *
	 *	@param String sLoginName, the String representing the new login name for the user
	 */
	public void setLoginName(String sLoginName) {

		if (sLoginName.equals(this.sLoginName))
			return ;

		setLoginNameLocal(sLoginName) ;
		//String oldValue = setLoginNameLocal(sLoginName) ;

		// call UserService to update the db
		/*
		IUserService us = Model.getUserService() ;
		us.setLoginName(oSession, sId, oldValue, sLoginName) ;
		*/
	}

	/**
	 *  Sets the user login name in the local data
	 *	This method additionally fires property changes to local listeners
	 *
	 *	@param sLoginName, the String representing the new login name for the user
	 *	@return the old value
	 */
	protected String setLoginNameLocal(String sLoginName) {

		if (sLoginName.equals(this.sLoginName))
			return sLoginName;

		String oldValue = this.sLoginName;
		this.sLoginName = sLoginName ;
		firePropertyChange(USER_LOGIN_NAME, oldValue, sLoginName) ;
		return oldValue;
	}

	/**
	 * Returns the User name of the user.
	 *
	 * @return String, the name of the user used as the author name.
	 */
	public String getUserName() {
		return sUserName ;
	}
	
	/**
	 * Returns the User ID of the user.
	 *
	 * @return String, the ID of the user.
	 */
	public String getUserID() {
		return sUserID ;
	}

	/**
	 * Sets the userName, currently in the local data ONLY.
	 *
	 * @param sUserName, the String name of the user
	 */
	public void setUserName(String sUserName) {

		if (sUserName.equals(this.sUserName))
			return;

		String oldValue = setUserNameLocal(sUserName);

		// call UserService to update the db
		/*
		IUserService us = Model.getUserService();
		us.setUserName(oSession, sId, oldValue, sUserName);
		*/
	}

	/**
	 *  Sets the user name and firesPropertychange event to local listeners
	 *
	 *	@param sUserName, the String name of the user.
	 *	@return String old value of the user name.
	 */
	protected String setUserNameLocal(String sUserName) {

		if (sUserName.equals(this.sUserName))
			return this.sUserName;

		String oldValue = this.sUserName;
		this.sUserName = sUserName;
		firePropertyChange(USER_NAME, oldValue, sUserName);
		return oldValue;
	}

	/**
	 * Returns the User Password of the user
	 *
	 * @return String, the password used by this user to login.
	 */
	public String getPassword() {
		return sPassword;
	}

	/**
	 *  Sets the user password, currently in the local data ONLY.
	 *
	 *	@param sPassword, the password used by this user to login.
	 */
	public void setPassword(String sPassword) {

		if (sPassword.equals(this.sPassword))
			return;

		String oldValue = setPasswordLocal(sPassword);

		/*
		// call UserService to update the db
		IUserService us = Model.getUserService();
		us.setPassword(oSession, sId, oldValue, sPassword);
		*/
	}

	/**
	 *  Sets the user password in the local data and firesPropertychange event
	 *
	 *	@param sPassword, the password used by this user to login.
	 *	@return String the old value of the password.
	 */
	protected String setPasswordLocal(String sPassword) {

		if (sPassword.equals(this.sPassword))
			return this.sPassword;

		String oldValue = this.sPassword;
		this.sPassword = sPassword;
		firePropertyChange(USER_PASSWORD, oldValue, sPassword);
		return oldValue;
	}

	/**
	 * Returns the User description of the user.
	 *
	 * @return String, a description of this user.
	 */
	public String getUserDescription() {
		return sUserDescription ;
	}

	/**
	 *  Sets the userDescription, currently in the local data ONLY.
	 *
	 *	@param sUserDes, a description of this user.
	 */
	public void setUserDescription(String sUserDes) {

		if (sUserDes.equals(sUserDescription))
			return ;

		String oldValue = setUserDescriptionLocal(sUserDes);

		// call UserService to update the db
		/*
		IUserService us = Model.getUserService();
		us.setUserDescription(oSession, sId, oldValue, sUserDescription);
		*/
	}

	/**
	 *  Sets the user description in the local data and firesPropertychange event
	 *
	 *	@param sUserDes, a description of this user.
	 *	@return the old Value the description of this user.
	 */
	protected String setUserDescriptionLocal(String sUserDes) {
		if (sUserDes.equals(sUserDescription))
			return sUserDescription;

		String oldValue = sUserDescription;
		sUserDescription = 	sUserDes;
		firePropertyChange(USER_DESCRIPTION, oldValue, sUserDescription);
		return oldValue;
	}

	/**
	 *	Returns the home view of the user.
	 *
	 *	@return IView, the home view of the user.
	 */
	public View getHomeView() {
		return oHomeView ;
	}

	/**
	 *  Sets the home view, both locally and in the DATABASE.
	 *
	 *	@param oHomeView, the home view
	 *	@exception java.sql.SQLException
	 */
	public void setHomeView(View oHomeView) throws SQLException, ModelSessionException {

		if (oHomeView == this.oHomeView)
			return;

		if (oModel == null)
			throw new ModelSessionException("Model is null in UserProfile.setHomeView");
		if (oSession == null)
			throw new ModelSessionException("Session is null in UserProfile.setHomeView");

		// call UserService to update the db
		IUserService us = oModel.getUserService() ;
		us.setHomeView(oSession, sId, oHomeView.getId()) ;
		View oldValue = setHomeViewLocal(oHomeView);
	}

	/**
	 *  Sets the home view in the local data and firesPropertychange event
	 *
	 *	@param oHomeView, the home view
	 *	@return View, the old view
	 */
	protected View setHomeViewLocal(View oHomeView) {

		if (oHomeView == this.oHomeView)
			return this.oHomeView;

		View oldValue = this.oHomeView;
		this.oHomeView = oHomeView;
		firePropertyChange(HOMEVIEW, oldValue, this.oHomeView);
		return oldValue;
	}

	/**
	 *	Returns the link view of the user (Inbox).
	 *
	 *	@return IView, the link view of the user.
	 */
	public View getLinkView() {
		return oLinkView ;
	}

	/**
	 *  Sets the link view, both locally and in the DATABASE.
	 *
	 *	@param oLinkView, the link view (inbox)
	 *	@exception java.sql.SQLException
	 */
	public void setLinkView(View oLinkView) throws SQLException, ModelSessionException {

		if (oLinkView == this.oLinkView)
			return;

		if (oModel == null)
			throw new ModelSessionException("Model is null in UserProfile.setLinkView");
		if (oSession == null)
			throw new ModelSessionException("Session is null in UserProfile.setLinkView");

		// call UserService to update the db
		IUserService us = oModel.getUserService() ;
		us.setLinkView(oSession, sId, oLinkView.getId()) ;
		setLinkViewLocal(oLinkView);
	}

	/**
	 *  Sets the link view in the local data and firesPropertychange event
	 *
	 *	@param oLinkView, the home view
	 *	@return View, the old view
	 */
	protected View setLinkViewLocal(View oLinkView) {

		if (oLinkView == this.oLinkView)
			return this.oLinkView;

		View oldValue = this.oLinkView;
		this.oLinkView = oLinkView;
		firePropertyChange(LINKVIEW, oldValue, this.oLinkView);
		return oldValue;
	}
	
	/**
	 *	Returns true if this user has administrator previledges
	 *
	 *	@return boolean, true if this user is an administrator, else false.
	 */
	public boolean isAdministrator() {
		return bIsAdministrator;
	}

	/**
	 * Sets the priviledges in the local data ONLY.
	 *
	 * @param boolean bIsAdmin, true if this user is an administrator, else false.
	 */
	public void setAdministrator(boolean bIsAdmin) {

		if (bIsAdministrator == bIsAdmin)
			return ;

		boolean oldValue = setAdministratorLocal(bIsAdmin) ;

		/*
		// call UserService to update the db
		IUserService us = Model.getUserService() ;
		us.setAdministrator(oSession, sId, oldValue, bIsAdministrator) ;
		*/
	}

	/**
	 *	Sets the previledges, fires property change to local listeners
	 *
	 *	@param boolean bIsAdmin, true if this user is an administrator, else false.
	 *	@return boolean, the old value
	 */
	protected boolean setAdministratorLocal(boolean bIsAdmin) {
		if (bIsAdministrator == bIsAdmin)
			return bIsAdministrator;

		boolean oldValue = bIsAdministrator;
		bIsAdministrator = 	bIsAdmin;

		firePropertyChange(IS_ADMINISTRATOR, oldValue, bIsAdministrator);
		return oldValue;
	}
	
	/**
	 * Returns true is the user account is Active
	 * 
	 * @return boolean, true if this user account is Active, else false.
	 */
	public boolean isActive() {
		return bIsActive;
	}

}
