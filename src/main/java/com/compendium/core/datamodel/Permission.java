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

/**
 * THIS CLASS IS NOT CURRENTLY USED
 * <p>
 * The Permission object represents the permission for a group to an object in the
 * database representing a pc compendium object.
 *
 * @author	Rema Natarajan / Michelle Bachler
 */
public class Permission extends PCObject implements java.io.Serializable {

	// the super class Id object is used for the group id value.
	protected String sObjectId = "" ;
	protected String sGroupId = "" ;
	protected int nPermission = -1 ;

	/**
	 *	Contructor.
	 */
	public Permission() {
		this("","",-1) ;
	}

	/**
	 *	Contructor.
	 */
	public Permission(String objectId, String groupId, int permission) {
		sObjectId = objectId;
		sGroupId = groupId;
		nPermission	= permission;
	}

	/**
	 *	This method initializes this object on the client.
	 */
	public void initialize(PCSession session, IModel model) {
		super.initialize(session, model);
	}

	/**
	 * This method should be called on the client before releasing it for garbage collection.
	 */
	public void finalize() {

	}

	/**
	 *	returns the id of the object for which this permission is assigned.
	 *
	 *	@return the name, or "".
	 */
	public String getObjectId() {
		return sObjectId;
	}

	/**
	 *	Sets the object id for the object for which this permission is assigned.
	 *
	 *	@param name the String group name.
	 */
	public void setObjectId(String id) {
		sObjectId = id;
	}

	/**
	 *	Returns the name of the group for which this permission is assigned.
	 *
	 *	@return the name, or "".
	 */
	public String getGroupId() {
		return sGroupId;
	}

	/**
	 *	Sets the group name for the group for which this permission is assigned.
	 *
	 *	@param name the String group name.
	 */
	public void setGroupId(String id) {
		sGroupId = id;
	}

	/**
	 *	Returns the permission value.
	 */
	public int getPermission() {
		return nPermission;
	}

	/**
	 *	Sets the permission. this is the only property that can be altered on the client.
	 *
	 *	@param permission the String permission.
	 */
	public void setPermission(int permission) {
		/*
		if (nPermission == permission)
			return ;
		int oldValue = nPermission ;
		nPermission = permission ;

		// notify server to update database
		IUserService userService = oModel.getUserService() ;
		userService.setPermission(oSession, sId, sObjectId, oldValue, nPermission) ;
		*/
	}
}
