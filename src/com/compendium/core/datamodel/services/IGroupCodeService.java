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

/**
 *	The interface for the GroupCodeService class
 *	The GroupCode service class provides remote services to manipuate GroupCode records in the database.
 *
 *	@author Michelle Bachler
 */

public interface IGroupCodeService extends IService {

	/**
	 * Adds a new GroupCode record, assigning a Code to a CodeGroup and returns it if successful
	 *
	 * @param PCSession session, the session object for the database to use.
	 * @param String sCodeID, the id of the code to add to the given CodeGroup.
	 * @param String sCodeGroupID, the id of the CodeGroup the code is in.
	 * @param String sAuthor, the author of the Code to CodeGroup assignment.
	 * @param java.util.Date dCreationDate, the creation date of the new Code to CodeGroup assignment.
	 * @param java.util.Date dModificationDate, the modification date of the new Code to CodeGroup assignment.
	 *
	 * @return boolean, indicates if the new Code to CodeGroup assignment record was successfully added.
	 * @exception java.sql.SQLException
	 */
	public boolean createGroupCode(PCSession session, String sCodeID, String sCodeGroupID, String sAuthor,
									java.util.Date dCreationDate, java.util.Date dModificationDate) throws SQLException;

	/**
	 * Delete the given code, groupcode record from the database and returns it if successful
	 *
	 * @param PCSession session, the session object for the database to use.
	 * @param String sCodeID, the id of the code to remove from the given CodeGroup.
	 * @param String sCodeGroupID, the id of the CodeGroup to remove the code from.
	 *
	 * @return boolean, indicates if the Code was removed from the CodeGroup successfully.
	 * @exception java.sql.SQLException
	 */
	public boolean delete(PCSession session, String sCodeID, String sCodeGroupID) throws SQLException;

	/**
	 * Returns the CodeGroups from the Database for the given CodeID
	 *
	 * @param PCSession session, the session object for the database to use.
	 * @param String sCodeID, the id of the code to return all the groups for.
	 * @return a Vector of information on the CodeGroups in the Database containing the given CodeID
	 * @exception java.sql.SQLException
	 */
	public Vector getCodeGroups(PCSession session, String sCodeID) throws SQLException;

	/**
	 * Returns all the Codes in the Database in the CodeGroup with the given CodeGroupID
	 *
	 * @param PCSession session, the session object for the database to use.
	 * @param String sCodeGroupID, the id of the CodeGroup to return the Codes for.
	 * @return a Vector of all the Codes in the Database for the given CodeGroupID
	 * @exception java.sql.SQLException
	 */
	public Vector getGroupCodes(PCSession session, String sCodeGroupID) throws SQLException;

	/**
	 * Returns all the Codes not in any group from the Database
	 *
	 * @param PCSession session, the session object for the database to use.
	 * @return a Vector of all the Codes not in any code group
	 * @exception java.sql.SQLException
	 */
	public Vector getUngroupedCodes(PCSession session) throws SQLException;
}
