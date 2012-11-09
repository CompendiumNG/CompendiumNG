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

import java.util.*;

/**
 *	NOTE: THIS CLASS IS NOT BEING USED YET AND IS THEREFORE NOT COMPLETED
 * 	<p>
 *	The Group object represents the a group that belongs to a project
 *	a user with administrator priviledges can modify properties for a group object
 *	when a single administrator is modifying the groups, other administrators cannot access the data</p>
 *
 * @author	Rema Natarajan / Michelle Bachler
 */
public class Group extends IdObject  implements IGroup {

	/** Holds the name of the user group.*/
	protected String sName = "" ;

	/** Holds the description of the user group.*/
	protected String sDescription = "" ;

	/** Contains the UserProfile objects of the users who belong to this group.*/
	protected Hashtable htMembers = new Hashtable(10) ;



	/**
	 * Constructor, takes the user group id, author, creation date, modification date, name and description.
	 */
	public Group(String id, String author, java.util.Date creationDate, java.util.Date modificationDate,
									String name, String description) {

		super(id, author, creationDate, modificationDate) ;
		sName = name;
		sDescription = description;
	}

	/**
	 *	The initialize method is called by the Model before adding the
	 *	object to the client
	 */
	public void initialize(PCSession session, IModel model) {
		super.initialize(session, model);
	}

	/**
	 *	Clean up the variables used by this object to free memory
	 */
	public void cleanUp() {
		oSession = null ;
		oModel = null ;
	}

	/**
	 *	Returns the group name for the user Group object.
	 *
	 *	@return group name, or ""
	 */
	public String getName() {
		return sName ;
	}

	/**
	 *	Returns the description for the user Group object.
	 *
	 *	@return the description, or ""
	 */
	public String getDescription() {
		return sDescription ;
	}
}
