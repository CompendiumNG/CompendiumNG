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
 *	The interface for the ViewLayerService class
 *	The ViewLayer service class provides services to manipulate ViewLayer objects.
 *
 *	@author Michelle Bachler
 */
public class ViewLayerService extends ClientService implements IViewLayerService, java.io.Serializable {

	/**
	 *	Constructor.
	 */
	public  ViewLayerService() {
		super();
	}

	/**
	 * Constructor, set the name of the service.
	 *
	 * @param String sName, the name of this service.
	 */
	public ViewLayerService(String sName) {
		super(sName);
	}

	/**
	 * Constructor.
	 *
	 * @param String name, the unique name of this service
 	 * @param ServiceManager sm, the current ServiceManager
	 * @param DBDtabaseManager dbMgr, the current DBDatabaseManager
	 */
	public  ViewLayerService(String name, ServiceManager sm, DBDatabaseManager dbMgr) {
		super(name, sm, dbMgr) ;
	}

	/**
	 * Adds a new view layer to the database and returns it if successful.
	 *
	 * @param PCSession session, the PCSession object for the database to use.
	 * @param ViewLayer view, the ViewLayer object to create a record for.
	 * @return boolean, true if the creation was successful, else false.
	 * @exception java.sql.SQLException
	 */
	public boolean createViewLayer( PCSession session, ViewLayer view) throws SQLException {

		DBConnection dbcon = getDatabaseManager().requestConnection(session.getModelName());

		boolean isSuccessful = DBViewLayer.insert(dbcon, view);

		getDatabaseManager().releaseConnection(session.getModelName(),dbcon);

		return isSuccessful;
	}

	/**
	 * Update a view layer to the database and returns it if successful.
	 *
	 * @param PCSession session, the PCSession object for the database to use.
	 * @param ViewLayer view, the ViewLayer object to update the record for.
	 * @return boolean, true if the update was successful, else false.
	 * @exception java.sql.SQLException
	 */
	public boolean updateViewLayer( PCSession session, ViewLayer view) throws SQLException {

		DBConnection dbcon = getDatabaseManager().requestConnection(session.getModelName()) ;

		boolean isSuccessful = DBViewLayer.update(dbcon, view);

		getDatabaseManager().releaseConnection(session.getModelName(),dbcon);

		return isSuccessful;
	}

	/**
	 * Deletes a view layer from the database and returns true if successful
	 *
	 * @param PCSession session, the PCSession object for the database to use.
	 * @param String sViewID, the id of the view for the view layer to delete.
	 * @return boolean, true if the update was successful, else false.
	 * @exception java.sql.SQLException
	 */
	public boolean deleteViewLayer(PCSession session, String sViewID) throws SQLException {

		DBConnection dbcon = getDatabaseManager().requestConnection(session.getModelName());

		boolean deleted = DBViewLayer.delete(dbcon, sViewID);

		getDatabaseManager().releaseConnection(session.getModelName(),dbcon);

		return deleted;
	}

	/**
	 * Get the view layer record for the given user and view id.
	 *
	 * @param PCSession session, the PCSession object for the database to use.
	 * @param String sViewID, the id of the view for the view layer to return.
	 * @return ViewLayer, the ViewLayer record for the user id and view id given.
	 * @exception java.sql.SQLException
	 */
	public ViewLayer getViewLayer(PCSession session, String sViewID) throws SQLException  {

		DBConnection dbcon = getDatabaseManager().requestConnection(session.getModelName()) ;

		ViewLayer viewLayer = DBViewLayer.getViewLayer(dbcon, sViewID);

		getDatabaseManager().releaseConnection(session.getModelName(),dbcon);

		return viewLayer;
	}
}
