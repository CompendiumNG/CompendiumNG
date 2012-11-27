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

import java.util.*;
import java.sql.SQLException;

import com.compendium.core.datamodel.PCSession;
import com.compendium.core.datamodel.*;
import com.compendium.core.db.management.*;

/**
 *	The interface for the CodeGroupService class
 *	The CodeGroupService class provides services to manipuate code group data in the database.
 *
 *	@author Michelle Bachler
 */

public interface ICodeGroupService extends IService {

	/**
	 * Adds a new code group to the database and returns it if successful
	 *
	 * @param PCSession session, the session object for the database to use.
	 * @param String sCodeGroupID, the id of the new code group to add.
	 * @param String sAuthor, the author of the new code group.
	 * @param String sName, the name of the new code group.
	 * @param java.util.Date dCreationDate, the creation date of the new code group.
	 * @param java.util.Date dModificationDate, the modification date of the new code group.
	 *
	 * @return boolean, indicates if the new code group was successfully added.
	 * @exception java.sql.SQLException, if something went wrong adding the new code group to the database.
	 */
	public boolean createCodeGroup(PCSession session, String sCodeGroupID, String sAuthor, String sName,
									java.util.Date dCreationDate, java.util.Date dModificationDate) throws SQLException;

	/**
	 * Delete a codegroup from the database and returns if it is successful.
	 *
	 * @param PCSession session, the session object for the database to use.
	 * @param String sCodeGroupID, the id of the code group to delete.
	 *
	 * @return boolean, indicates if the code group was successfully deleted.
	 * @exception java.sql.SQLException
	 */
	public boolean deleteCodeGroup(PCSession session, String sCodeGroupID) throws SQLException;

	/**
	 * Set the Name of the group to the given name.
	 *
	 * @param PCSession session, the session object for the database to use.
	 * @param String sCodeGroupID, the id of the code group to set the name of.
	 * @param String sName, the new name of the code group.
	 * @param java.util.Date dModificationDate, the modification date of the code group name.
	 * @param String sUserID, the id of the user setting the name of the code group.
	 *
	 * @return boolean, indicates if the new name was successfully added.
	 * @exception java.sql.SQLException, if something went wrong setting the new name in the database.
	 */
	public boolean setName(PCSession session, String sCodeGroupID, String sName,
							java.util.Date dModificationDate, String sUserID) throws SQLException;

	/**
	 * Returns the CodeGroup information from the Database for the given CodeGroupID
	 *
	 * @param PCSession session, the session object for the database to use.
	 * @param String sCodeGroupID, the id of the code group to get.
	 *
	 * @return Vector, of information on the CodeGroup in the Database with the given CodeGroupID
	 * @exception java.sql.SQLException
	 */
	public Vector getCodeGroup(PCSession session, String sCodeGroupID) throws SQLException;

	/**
	 * Returns all the code groups from the Database.
	 *
	 * @param PCSession session, the session object for the database to use.
	 *
	 * @return a Vector of information on all the CodeGroups in the Database
	 * @exception java.sql.SQLException
	 */
	public Vector getCodeGroups(PCSession session) throws SQLException;
}
