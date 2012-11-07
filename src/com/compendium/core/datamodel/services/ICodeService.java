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
 *	The interface for the CodeService class
 *	The CodeService class provides remote services to manipuate code objects in the database.
 *
 *	@author Sajid and Rema /  Michelle Bachler
 */
public interface ICodeService extends IService {

	/**
	 * Adds a new code to the database and returns it if successful
	 *
	 * @param PCSession session, the PCSession object for the database to use.
	 * @param String sCodeID, the code id for the particular code object
	 * @param String sAuthor, the author of this code
	 * @param Date dCreationDate, the creation date of this code
	 * @param Date dModificationDate, the modification date
	 * @param String sName, the value of the code name
	 * @param String sDescription, the value of the code description
	 * @param String sBehavior, the value of the code behavior
	 *
	 * @return Code, the newly created Code object from the data placed in the database.
	 * @exception java.sql.SQLException
	 */
	public Code createCode(PCSession session, String sCodeID, String sAuthor, java.util.Date dCreationDate,
			java.util.Date dModificationDate, String sName, String sDescription, String sBehavior)
			throws SQLException;

	/**
	 * Delete a code with the given code id from the database and returns it if successful
	 *
	 * @param PCSession session, the PCSession object for the database to use.
	 * @param String sCodeID, the code id for the particular code object
	 * @return boolean, indicating if the deletion of the code was successful.
	 * @exception java.sql.SQLException, if something went wrong.
	 */
	public boolean delete(PCSession session, String sCodeID) throws SQLException;

	/**
	 * Returns all the codes in the DB
	 *
	 * @param PCSession session, the PCSession object for the database to use.
	 * @return a Vector of all the codes in the Database
	 * @exception java.sql.SQLException
	 */
	public Vector getCodes(PCSession session) throws SQLException;

	/**
	 * Returns all the codes for the given code name
	 *
	 * @param PCSession session, the PCSession object for the database to use.
	 * @param String sName, the value of the code name
	 * @return a Vector of all the codes for the given code name
	 * @exception java.sql.SQLException
	 */
	public Vector getCodeIDs(PCSession session, String name) throws SQLException;

	/**
	 * Returns the Code Object for the given code id
	 *
	 * @param PCSession session, the PCSession object for the database to use.
	 * @param String sCodeID, the code id for the particular code object
	 * @return a Code object for the given code id
	 * @exception java.sql.SQLException
	 */
	public Code getCode(PCSession session, String codeId) throws SQLException;

	/**
	 * Returns the Code Object for the given code name
	 *
	 * @param PCSession session, the PCSession object for the database to use.
	 * @param String sName, the name of the code to return
	 * @return a Code object for the given code name
	 * @exception java.sql.SQLException
	 */
	public Code getCodeForName(PCSession session, String sName)	throws SQLException;

	/**
	 * Returns all the nodes associated with the given sCodeIS
	 *
	 * @param PCSession session, the PCSession object for the database to use.
	 * @param sCodeID, the code id for the particular code object
	 * @return a Vector of all the nodes associated with given sCodeID in the DB
	 * @exception java.sql.SQLException
	 */
	public Vector getNodes(PCSession session, String sCodeID, String userID) throws SQLException;

	/**
	 * Returns a count of the nodes associated with the given sCodeIS
	 *
	 * @param PCSession session, the PCSession object for the database to use.
	 * @param sCodeID, the code id for the particular code object
	 * @return a count of the nodes associated with given sCodeID in the DB
	 * @exception java.sql.SQLException
	 */
	public int getNodeCount(PCSession session, String sCodeID) throws SQLException;

	/**
	 * CURRENTLY NOT IMPLEMENTED.<br>
	 * Gets the name of the code with the particular code Id
	 *
	 * @param PCSession session, the PCSession object for the database to use.
	 * @param codeId, the code id for the particular code object
	 * @return the name of the code
	 * @exception throws java.sql.SQLException
	 */
	public String getName(PCSession session, String codeId) throws SQLException;

	/**
	 * Updates the code name for the code object with the particular sCodeID
	 *
	 * @param PCSession session, the PCSession object for the database to use.
	 * @param String sCodeID, the code id for the particular code object
	 * @param String sName, the new code name
	 * @param Date dModificationDate, the modification date
	 * @exception throws java.sql.SQLException
	 */
	public boolean setName(PCSession session, String sCodeID, String sName, java.util.Date dModificationDate) throws SQLException;

	/**
	 * CURRENTLY NOT IMPLEMENTED.<br>
	 * Gets the description of the code with the particular codeID
	 *
	 * @param PCSession session, the PCSession object for the database to use.
	 * @param codeId, the code id for the particular code object
	 * @return the description of the code
	 * @exception throws java.sql.SQLException
	 */
	public String getDescription(PCSession session, String codeId) throws SQLException;

	/**
	 * Udaptes the description of the code with the particular sCodeID
	 *
	 * @param PCSession session, the PCSession object for the database to use.
	 * @param String sCodeID, the code id for the particular code object
	 * @param String sDescription, the value of the code description
	 * @param Date dModificationDate, the modification date
	 * @exception throws java.sql.SQLException
	 */
	public boolean setDescription(PCSession session, String sCodeID, String sDescription, java.util.Date dModificationDate) throws SQLException;

	/**
	 * CURRENTLY NOT IMPLEMENTED.<br>
	 * Gets the behavior of the code with the particular codeID
	 *
	 * @param PCSession session, the PCSession object for the database to use.
	 * @param codeId, the code id for the particular code object
	 * @return the behavior of the code
	 * @exception throws java.sql.SQLException
	 */
	public String getBehavior(PCSession session, String codeId) throws SQLException;

	/**
	 * Udaptes the behavior of the code with the particular sCodeID
	 *
	 * @param PCSession session, the PCSession object for the database to use.
	 * @param String sCodeID, the code id for the particular code object
	 * @param String sBehavior, the value of the code behavior
	 * @param Date dModificationDate, the modification date
	 * @exception throws java.sql.SQLException
	 */
	public boolean setBehavior(PCSession session, String sCodeId, String sBehavior, java.util.Date dModificationDate) throws SQLException;
}
