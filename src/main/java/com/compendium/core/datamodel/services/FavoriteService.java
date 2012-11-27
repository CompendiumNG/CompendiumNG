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
 *	The Favorite service class provides remote services to manipuate Favorite data in the database.
 *
 *	@author Michelle Bachler
 */

public class FavoriteService extends ClientService implements IFavoriteService, java.io.Serializable {

	/**
	 *	Constructor.
	 */
	public  FavoriteService() {
		super();
	}

	/**
	 * Constructor, set the name of the service.
	 *
	 * @param String sName, the name of this service.
	 */
	public FavoriteService(String sName) {
		super(sName);
	}

	/**
	 *	Constructor, set the name, ServiceManager and DBDatabaseManager for this service.
	 *
	 * @param String sName, the name of this service.
	 * @param ServiceManager sm, the ServiceManager used by this service.
	 * @param DBDatabaseManager dbMgr, the DBDatabaseManager used by this service.
	 */
	public  FavoriteService(String sName, ServiceManager sm,  DBDatabaseManager dbMgr) {
		super(sName, sm, dbMgr) ;
	}

	/**
	 * Adds a new Favorite to the database and returns it if successful
	 *
	 * @param PCSession session, the PCSession object for the database to use.
	 * @param String sUserID, the user id of the person who created this favorite.
	 * @param String sNodeID, the node id of the favorite node.
	 * @param String sViewID, the node id of the favorite node. 
	 * @param String sLabel, the label of the node.
	 * @param int nType, the node type.
	 * @return the new Favorite object or null if something went wrong.
	 * @exception java.sql.SQLException
	 */
	public Favorite createFavorite( PCSession session, String sUserID, String sNodeID, String sViewID, String sLabel, int nType ) throws SQLException {

		DBConnection dbcon = getDatabaseManager().requestConnection(session.getModelName()) ;

		Favorite fav = DBFavorite.insert(dbcon, sUserID, sNodeID, sViewID, sLabel, nType);

		getDatabaseManager().releaseConnection(session.getModelName(),dbcon);

		return fav;
	}

	/**
	 * Deletes one of more favorites from the database and returns true if successful
	 *
	 * @param session the PCSession object for the database to use.
	 * @param sUserID the user id of the person who created this favorite.
	 * @param vtFavorites the list of favorites to delete.
	 * @exception java.sql.SQLException
	 */
	public void deleteFavorites(PCSession session, String sUserID, Vector vtFavorites) throws SQLException {

		DBConnection dbcon = getDatabaseManager().requestConnection(session.getModelName());

		DBFavorite.delete(dbcon, sUserID, vtFavorites);

		getDatabaseManager().releaseConnection(session.getModelName(),dbcon);
	}


	/**
	 * Deletes all favorites from the database for the given user id and returns true if successful
	 *
	 * @param PCSession session, the PCSession object for the database to use.
	 * @param String sUserID, the user id of the person whose favorites to delete.
	 * @return boolean, indicating if the deletion of the Favorites was successful.
	 * @exception java.sql.SQLException
	 */
	public boolean deleteAllFavorites(PCSession session, String sUserID) throws SQLException {

		DBConnection dbcon = getDatabaseManager().requestConnection(session.getModelName());

		boolean deleted = DBFavorite.deleteAll(dbcon, sUserID);

		getDatabaseManager().releaseConnection(session.getModelName(),dbcon);

		return deleted;
	}

	/**
	 * Returns all the favorites for the given user
	 *
	 * @param PCSession session, the PCSession object for the database to use.
	 * @param String sUserID, the user id of the person whose favorites to return.
	 * @return Vector, of all the favorites for the given user
	 * @exception java.sql.SQLException
	 */
	public Vector getFavorites(PCSession session, String sUserID) throws SQLException  {

		DBConnection dbcon = getDatabaseManager().requestConnection(session.getModelName()) ;

		Vector vtFavorites = DBFavorite.getFavorites(dbcon, sUserID);

		getDatabaseManager().releaseConnection(session.getModelName(),dbcon);

		return vtFavorites;
	}
}
