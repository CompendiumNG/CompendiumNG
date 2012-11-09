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
 *	NOTE: THIS CLASS IS NOT BEING USED YET AND IS THEREFORE NOT COMPLETED
 *
 *	The IGroup interface is provided for the Group object.
 *	The Group object represents the a group that belongs to a project
 *	a user with administrator priviledges can modify properties for a group object
 *	when a single administrator is modifying the groups, other administrators cannot access the data
 *
 * @author	Rema Natarajan
 */
public interface IGroup extends java.io.Serializable{

	/**
	 *	Returns the group name for the user Group object
	 *
	 *	@return group name, or ""
	 */
	public String getName() ;

	/**
	 *	Returns the description for the user Group object
	 *
	 *	@return the description, or ""
	 */
	public String getDescription() ;
}
