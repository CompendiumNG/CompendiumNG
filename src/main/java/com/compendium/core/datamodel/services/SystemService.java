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
import java.sql.*;

import com.compendium.core.datamodel.*;
import com.compendium.core.db.*;
import com.compendium.core.db.management.*;

/**
 *	The interface for the SystemService class
 *	The system service class provides remote services to manipuate systemdata.
 *
 *	@author Michelle Bachler
 */

public class SystemService extends ClientService implements ISystemService, java.io.Serializable {


	/**
	 *	Constructor.
	 */
	public SystemService() {
		super();
	}

	/**
	 * Constructor, set the name of the service.
	 *
	 * @param String sName, the name of this service.
	 */
	public SystemService(String name) {
		super(name);
	}

	/**
	 *	Constructor, set the name, ServiceManager and DBDatabaseManager for this service.
	 *
	 * @param String sName, the name of this service.
	 * @param ServiceManager sm, the ServiceManager used by this service.
	 * @param DBDatabaseManager dbMgr, the DBDatabaseManager used by this service.
	 */
	public SystemService(String name, ServiceManager sm, DBDatabaseManager dbMgr) {
		super(name, sm, dbMgr);
	}
	
	/**
	 *  Update the project level properties.
	 *
	 * 	@param PCSession session, the PCSession object for the database to use.
	 *	@param properties the hashtable containing the key and value pairs to save to the database.
	 *	@return boolean true if it was successful, else false.
	 *	@throws java.sql.SQLException
	 */
	public boolean insertProperties(PCSession session, Hashtable properties) throws SQLException {

		DBConnection dbcon = getDatabaseManager().requestConnection(session.getModelName()) ;

		boolean successful = DBSystem.insertProperties(dbcon, properties);

		getDatabaseManager().releaseConnection(session.getModelName(),dbcon);

		return successful;
	}
	
	/**
	 *  Update the default user property and return if successful.
	 *
	 * 	@param PCSession session, the PCSession object for the database to use.
	 *	@param sProperty the property name.
	 *	@param sValue the property value.
	 *	@return boolean, true if it was successful, else false.
	 *	@throws java.sql.SQLException
	 */
	public boolean insertProperty(PCSession session, String sProperty, String sValue) throws SQLException {
		DBConnection dbcon = getDatabaseManager().requestConnection(session.getModelName()) ;

		boolean successful = DBSystem.insertProperty(dbcon, sProperty, sValue);

		getDatabaseManager().releaseConnection(session.getModelName(),dbcon);

		return successful;
	}

	/**
	 *  Return the project level preferences.
	 *
	 * 	@param PCSession session, the PCSession object for the database to use.
	 *	@throws java.sql.SQLException
	 */
	public Hashtable getProperties(PCSession session) throws SQLException {
		DBConnection dbcon = getDatabaseManager().requestConnection(session.getModelName()) ;

		Hashtable table = DBSystem.getProperties(dbcon);

		getDatabaseManager().releaseConnection(session.getModelName(),dbcon);

		return table;
	}
	
	/**
	 * Update the active codegroup id and return if successful
	 *
	 * @param PCSession session, the PCSession object for the database to use.
	 * @param sCodeGroupID, the id of the default code group.
	 * @return boolean, if the database update is successful, else false.
	 * @exception java.sql.SQLException
	 */
	public boolean setCodeGroup(PCSession session, String sCodeGroupID) throws SQLException {

		DBConnection dbcon = getDatabaseManager().requestConnection(session.getModelName()) ;

		boolean successful = DBSystem.setCodeGroup(dbcon, sCodeGroupID);

		getDatabaseManager().releaseConnection(session.getModelName(),dbcon);

		return successful;
	}

	/**
	 * Returns the active codegroup id.
	 *
	 * @param PCSession session, the PCSession object for the database to use.
	 * @return String, representing the active code group id.
	 * @exception java.sql.SQLException
	 */
	public String getCodeGroup(PCSession session) throws SQLException {

		DBConnection dbcon = getDatabaseManager().requestConnection(session.getModelName()) ;

		String sCodeGroup = DBSystem.getCodeGroup(dbcon);

		getDatabaseManager().releaseConnection(session.getModelName(),dbcon);

		return sCodeGroup;
	}

	/**
	 * Update the active link group id and return if successful
	 *
	 * @param PCSession session, the PCSession object for the database to use.
	 * @param sLinkGroupID, the id of the default link group.
	 * @return boolean, if the database update is successful, else false.
	 * @exception java.sql.SQLException
	 */
	public boolean setLinkGroup(PCSession session, String sLinkGroupID) throws SQLException {

		DBConnection dbcon = getDatabaseManager().requestConnection(session.getModelName()) ;

		boolean successful = DBSystem.setLinkGroup(dbcon, sLinkGroupID);

		getDatabaseManager().releaseConnection(session.getModelName(),dbcon);

		return successful;
	}

	/**
	 * Returns the active link group id.
	 *
	 * @param PCSession session, the PCSession object for the database to use.
	 * @return String, representing the active link group id.
	 * @exception java.sql.SQLException
	 */
	public String getLinkGroup(PCSession session) throws SQLException {

		DBConnection dbcon = getDatabaseManager().requestConnection(session.getModelName()) ;

		String sLinkGroup = DBSystem.getLinkGroup(dbcon);

		getDatabaseManager().releaseConnection(session.getModelName(),dbcon);

		return sLinkGroup;
	}

	/**
	 * Update the default user id and return if successful
	 *
	 * @param PCSession session, the PCSession object for the database to use.
	 * @param sUserID, the id of the default user.
	 * @return boolean, if the database update successful, else false.
	 * @exception java.sql.SQLException
	 */
	public boolean setDefaultUser(PCSession session, String sUserID) throws SQLException {

		DBConnection dbcon = getDatabaseManager().requestConnection(session.getModelName()) ;

		boolean successful = DBSystem.setDefaultUser(dbcon, sUserID);

		getDatabaseManager().releaseConnection(session.getModelName(),dbcon);

		return successful;
	}

	/**
	 * Returns the <code>UserProfile</code> object for the default user of the current database.
	 *
	 * @param PCSession session, the PCSession object for the database to use.
	 * @return UserProfile, the <code>UserProfile</code> record for the default user.
	 * @exception java.sql.SQLException
	 */
	public UserProfile getDefaultUser(PCSession session) throws SQLException {

		DBConnection dbcon = getDatabaseManager().requestConnection(session.getModelName()) ;

		UserProfile oUser = DBSystem.getDefaultUser(dbcon);

		getDatabaseManager().releaseConnection(session.getModelName(),dbcon);

		return oUser;
	}
}
