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

import java.sql.*;
import java.util.*;

import com.compendium.core.datamodel.*;
import com.compendium.core.db.*;
import com.compendium.core.db.management.*;

/**
 *	The ExternalConnectionService class for interacting with the Database layer.
 *	The ExternalConnection service class provides services to manipuate ExternalConnection objects in the Connections table.
 *
 *	@author Michelle Bachler
 */
public class ExternalConnectionService extends ClientService implements IExternalConnectionService, java.io.Serializable {

	/**
	 *	Constructor.
	 */
	public  ExternalConnectionService() {
		super();
	}

	/**
	 * Constructor, set the name of the service.
	 *
	 * @param String sName, the name of this service.
	 */
	public ExternalConnectionService(String sName) {
		super(sName);
	}

	/**
	 * Constructor.
	 *
	 * @param String name, the unique name of this service
 	 * @param ServiceManager sm, the current ServiceManager
	 * @param DBDtabaseManager dbMgr, the current DBDatabaseManager
	 */
	public  ExternalConnectionService(String name, ServiceManager sm, DBDatabaseManager dbMgr) {
		super(name, sm, dbMgr) ;
	}

	/**
	 * Adds a new connection to the database and returns it if successful.
	 *
	 * @param PCSession session, the PCSession object for the database to use.
	 * @param ExternalConnection oConnection, the ExternalConnection object to create a record for.
	 * @return boolean, true if the creation was successful, else false.
	 * @exception java.sql.SQLException
	 */
	public boolean createExternalConnection(PCSession session, ExternalConnection oConnection) throws SQLException {

		DBConnection dbcon = getDatabaseManager().requestConnection(session.getModelName());

		boolean isSuccessful = DBExternalConnection.insert(dbcon, oConnection);

		getDatabaseManager().releaseConnection(session.getModelName(),dbcon);

		return isSuccessful;
	}

	/**
	 * Update a connection in the database and returns it if successful.
	 *
	 * @param PCSession session, the PCSession object for the database to use.
	 * @param ExternalConnection oConnection, the ExternalConnection object to update the record for.
	 * @param String sProfile, the olf profile name for the record to be updated.
	 * @param int nType, the old connection type for the record being updated.
	 * @return boolean, true if the update was successful, else false.
	 * @exception java.sql.SQLException
	 */
	public boolean updateExternalConnection( PCSession session, ExternalConnection oConnection, String sProfile, int nType) throws SQLException {

		DBConnection dbcon = getDatabaseManager().requestConnection(session.getModelName()) ;

		boolean isSuccessful = DBExternalConnection.update(dbcon, oConnection, sProfile, nType);

		getDatabaseManager().releaseConnection(session.getModelName(),dbcon);

		return isSuccessful;
	}

	/**
	 * Deletes a external connection from the database and returns true if successful
	 *
	 * @param PCSession session, the PCSession object for the database to use.
	 * @param ExternalConnection connection, the external connection to delete.
	 * @return boolean, true if the deletion was successful, else false.
	 * @exception java.sql.SQLException
	 */
	public boolean deleteExternalConnection(PCSession session, ExternalConnection oConnection) throws SQLException {

		DBConnection dbcon = getDatabaseManager().requestConnection(session.getModelName());

		boolean deleted = DBExternalConnection.delete(dbcon, oConnection);

		getDatabaseManager().releaseConnection(session.getModelName(),dbcon);

		return deleted;
	}

	/**
	 * Get all the connection of the given type for the given userid.
	 *
	 * @param PCSession session, the PCSession object for the database to use.
	 * @param String sUserID, the user id of the user whose connections to return.
	 * @param int nType, the type of connections to return.
	 * @return Vector, a list of the connections requested.
	 * @exception java.sql.SQLException
	 */
	public Vector getExternalConnections(PCSession session, String sUserID, int nType) throws SQLException  {

		DBConnection dbcon = getDatabaseManager().requestConnection(session.getModelName()) ;

		Vector connections = DBExternalConnection.getConnections(dbcon, sUserID, nType);

		getDatabaseManager().releaseConnection(session.getModelName(),dbcon);

		return connections;
	}
}
