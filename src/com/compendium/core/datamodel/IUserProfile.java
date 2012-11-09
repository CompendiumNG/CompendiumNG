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

/**
 *	This interface represents a project compendium user	profile
 */

/*
 * Author Rema Natarajan / Michelle Bachler
 */
public interface IUserProfile {

	/**
	 *	Returns the User Login name of the user.
	 *
	 * @return String, the name used by this user to login to Compendium.
	 */
	public String getLoginName() ;

	/**
	 *  Sets the user login name, currently in the local data ONLY.
	 *
	 *	@param String sLoginName, the String representing the new login name for the user
	 */
	public void setLoginName(String loginName) ;

	/**
	 * Returns the User name of the user.
	 *
	 * @return String, the name of the user used as the author name.
	 */
	public String getUserName()	;

	/**
	 * Sets the userName, currently in the local data ONLY.
	 *
	 * @param sUserName, the String name of the user
	 */
	public void setUserName(String userName) ;

	/**
	 * Returns the User Password of the user
	 *
	 * @return String, the password used by this user to login.
	 */
	public String getPassword()	;

	/**
	 *  Sets the user password, currently in the local data ONLY.
	 *
	 *	@param sPassword, the String password
	 */
	public void setPassword(String password) ;

	/**
	 * Returns the User description of the user.
	 *
	 * @return String, a description of this user.
	 */
	public String getUserDescription() ;

	/**
	 *  Sets the userDescription, currently in the local data ONLY.
	 *
	 *	@param sUserDes, a description of this user.
	 */
	public void setUserDescription(String userDes) ;

	/**
	 *	Returns the home view of the user.
	 *
	 *	@return IView, the home view of the user.
	 */
	public View getHomeView() ;

	/**
	 *  Sets the home view, both locally and in the DATABASE.
	 *
	 *	@param oHomeView, the home view
	 *	@exception java.sql.SQLException
	 */
	public void setHomeView(View hv) throws SQLException, ModelSessionException;

	/**
	 *	Returns the link view of the user (Inbox).
	 *
	 *	@return IView, the link view of the user.
	 */
	public View getLinkView();

	/**
	 *  Sets the link view, both locally and in the DATABASE.
	 *
	 *	@param oLinkView, the link view (inbox)
	 *	@exception java.sql.SQLException
	 */
	public void setLinkView(View oLinkView) throws SQLException, ModelSessionException;
	
	/**
	 *	Returns true if this user has administrator previledges
	 *
	 *	@return boolean, true is the administrator has previledges, false otherwise
	 */
	public boolean isAdministrator() ;

	/**
	 * Sets the priviledges in the local data ONLY.
	 *
	 * @param boolean bIsAdmin, true if this user is an administrator, else false.
	 */
	public void setAdministrator(boolean b)	;

}
