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
 *	The IPermission interface is provided for the Permission object.
 *	The Permission object represents the permission for a group to an object in the
 *  database representing a pc compendium object.
 *
 *  @author	Rema Natarajan / Michelle Bachler
 */
public interface IPermission extends  java.io.Serializable{


	/**
	 *	Returns the id of the object for which this permission is assigned.
	 *
	 *	@return the name, or "".
	 */
	public String getObjectId();

	/**
	 *	Sets the object id for the object for which this permission is assigned.
	 *
	 *	@param name the String group name.
	 */
	public void setObjectId(String id);

	/**
	 *	Returns the id of the group for which this permission is assigned.
	 *
	 *	@return the  group id.
	 */
	public String getGroupId();

	/**
	 *	Sets the group id for the group for which this permission is assigned.
	 *
	 *	@param id the String group id.
	 */
	public void setGroupId(String id);

	/**
	 *	Returns the permission value.
	 *
	 *	@return the permission.
	 */
	public int getPermission();

	/**
	 *	Sets the permission. this is the only property that can be altered on the client
	 *	setting notifies the service, and fires property changes to GUI listeners
	 *
	 *	@param permission the String permission.
	 */
	public void setPermission(int permission);
}
