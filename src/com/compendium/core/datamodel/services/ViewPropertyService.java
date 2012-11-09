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
import java.awt.Font;

import com.compendium.core.datamodel.*;
import com.compendium.core.db.*;
import com.compendium.core.db.management.*;

/**
 *	The interface for the ViewPropertyService class
 *	The ViewProperty service class provides services to manipuate ViewProperty objects.
 *
 *	@author Michelle Bachler
 */
public class ViewPropertyService extends ClientService implements IViewPropertyService, java.io.Serializable {

	/**
	 *	Constructor.
	 */
	public  ViewPropertyService() {
		super();
	}

	/**
	 * Constructor, set the name of the service.
	 *
	 * @param String sName, the name of this service.
	 */
	public ViewPropertyService(String sName) {
		super(sName);
	}

	/**
	 * Constructor.
	 *
	 * @param String name, the unique name of this service
 	 * @param ServiceManager sm, the current ServiceManager
	 * @param DBDtabaseManager dbMgr, the current DBDatabaseManager
	 */
	public  ViewPropertyService(String name, ServiceManager sm, DBDatabaseManager dbMgr) {
		super(name, sm, dbMgr) ;
	}

	/**
	 * Adds a new view property to the database and returns it if successful.
	 *
	 * @param PCSession session, the PCSession object for the database to use.
	 * @param String sUserID, the user id of the user creating ths view property.
	 * @param ViewProperty view, the ViewProperty object to create a record for.
	 * @return boolean, true if the creation was successful, else false.
	 * @exception java.sql.SQLException
	 */
	public boolean createViewProperty( PCSession session, String sUserID, ViewProperty view) throws SQLException {

		DBConnection dbcon = getDatabaseManager().requestConnection(session.getModelName());

		boolean isSuccessful = DBViewProperty.insert(dbcon, sUserID, view);

		getDatabaseManager().releaseConnection(session.getModelName(),dbcon);

		return isSuccessful;
	}

	/**
	 * Update a view property to the database and returns it if successful.
	 *
	 * @param PCSession session, the PCSession object for the database to use.
	 * @param String sUserID, the user id of the userupdating this view property.
	 * @param ViewProperty view, the ViewProperty object to update the record for.
	 * @return boolean, true if the update was successful, else false.
	 * @exception java.sql.SQLException
	 */
	public boolean updateViewProperty( PCSession session, String sUserID, ViewProperty view) throws SQLException {

		DBConnection dbcon = getDatabaseManager().requestConnection(session.getModelName()) ;

		boolean isSuccessful = DBViewProperty.update(dbcon, sUserID, view);

		getDatabaseManager().releaseConnection(session.getModelName(),dbcon);

		return isSuccessful;
	}

	/**
	 * Deletes a view property from the database and returns true if successful
	 *
	 * @param PCSession session, the PCSession object for the database to use.
	 * @param String sUserID, the user id of the user updating this view property.
	 * @param String sViewID, the id of the view for the view property to delete.
	 * @return boolean, true if the update was successful, else false.
	 * @exception java.sql.SQLException
	 */
	public boolean deleteViewProperty(PCSession session, String sUserID, String sViewID) throws SQLException {

		DBConnection dbcon = getDatabaseManager().requestConnection(session.getModelName());

		boolean deleted = DBViewProperty.delete(dbcon, sUserID, sViewID);

		getDatabaseManager().releaseConnection(session.getModelName(),dbcon);

		return deleted;
	}

	/**
	 * Get the view property record for the given user and view id.
	 *
	 * @param PCSession session, the PCSession object for the database to use.
	 * @param String sUserID, the user id of the user whose view property to return.
	 * @param String sViewID, the id of the view for the view property to return.
	 * @return ViewProperty, the ViewProperty record for the user id and view id given.
	 * @exception java.sql.SQLException
	 */
	public ViewProperty getViewPosition(PCSession session, String sUserID, String sViewID) throws SQLException  {

		DBConnection dbcon = getDatabaseManager().requestConnection(session.getModelName()) ;

		ViewProperty viewPosition = DBViewProperty.getViewPosition(dbcon, sUserID, sViewID);

		getDatabaseManager().releaseConnection(session.getModelName(),dbcon);

		return viewPosition;
	}
}
