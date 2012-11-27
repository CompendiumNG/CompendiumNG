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
 *	The interface for the WorkspaceService class
 *	The Workspace service class provides remote services to manipuate Workspace objects.
 *
 *	@author Michelle Bachler
 */

public class WorkspaceService extends ClientService implements IWorkspaceService, java.io.Serializable {

	/**
	 *	Constructor.
	 */
	public  WorkspaceService() {
		super();
	}

	/**
	 * Constructor, set the name of the service.
	 *
	 * @param String sName, the name of this service.
	 */
	public WorkspaceService(String name) {
		super(name);
	}

	/**
	 * Constructor.
	 *
	 * @param String name, the unique name of this service
 	 * @param ServiceManager sm, the current ServiceManager
	 * @param DBDtabaseManager dbMgr, the current DBDatabaseManager
	 */
	public  WorkspaceService(String name, ServiceManager sm,  DBDatabaseManager dbMgr) {
		super(name, sm, dbMgr) ;
	}

	/**
	 * Adds a new workspace and returns it if successful.
	 *
	 * @param PCSession session, the PCSession object for the database to use.
	 * @param String sWorkspaceID, the workspace id of the workspace to be created.
	 * @param String sUserID, the user id of the user creating this workspace.
	 * @param String sName, the name of this workspace.
	 * @param Vector vtViews, a list of View object composing this workspace.
	 * @return boolean, true if the creation of the workspace record was successful, else false.
	 * @exception java.sql.SQLException
	 */
	public boolean createWorkspace( PCSession session, String sWorkspaceID, String sUserID, String sName, Vector vtViews) throws SQLException {

		DBConnection dbcon = getDatabaseManager().requestConnection(session.getModelName()) ;

		boolean isSuccessful = DBWorkspace.insert(dbcon, sWorkspaceID, sUserID, sName, vtViews);

		getDatabaseManager().releaseConnection(session.getModelName(),dbcon);

		return isSuccessful;
	}

	/**
	 * Update a workspace and returns it if successful.
	 *
	 * @param PCSession session, the PCSession object for the database to use.
	 * @param String sWorkspaceID, the workspace id of the workspace to be updated.
	 * @param String sUserID, the user id of the user whose workspace this is.
	 * @param String sName, the name of this workspace.
	 * @param Vector vtViews, a list of View object composing this workspace.
	 * @return boolean, true if the updating of the workspace record was successful, else false.
	 * @exception java.sql.SQLException
	 */
	public boolean updateWorkspace( PCSession session, String sWorkspaceID, String sUserID, String sName, Vector vtViews) throws SQLException {

		DBConnection dbcon = getDatabaseManager().requestConnection(session.getModelName()) ;

		boolean isSuccessful = DBWorkspace.update(dbcon, sWorkspaceID, sUserID, sName, vtViews);

		getDatabaseManager().releaseConnection(session.getModelName(),dbcon);

		return isSuccessful;
	}

	/**
	 * Deletes one or more workspaces from the database and returns true if successful
	 *
	 * @param PCSession session, the PCSession object for the database to use.
	 * @param String sUserID, the user id of the user whose workspace/s these are.
	 * @param String sWorkspaceIDs, the workspace ids of the workspaces to be deleted.
	 * @return boolean, true if the deletion of the workspace/s was successful, else false.
	 * @exception java.sql.SQLException
	 */
	public boolean deleteWorkspaces(PCSession session, String sUserID, String sWorkspaceIDs) throws SQLException {

		DBConnection dbcon = getDatabaseManager().requestConnection(session.getModelName());

		boolean deleted = DBWorkspace.delete(dbcon, sUserID, sWorkspaceIDs);

		getDatabaseManager().releaseConnection(session.getModelName(),dbcon);

		return deleted;
	}

	/**
	 * Deletes all workspaces from the database for the given user id and returns true if successful
	 *
	 * @param PCSession session, the PCSession object for the database to use.
	 * @param String sUserID, the user id of the user whose workspaces to delete.
	 * @return boolean, true if the deletion of the workspaces was successful, else false.
	 * @exception java.sql.SQLException
	 */
	public boolean deleteAllWorkspace(PCSession session, String sUserID) throws SQLException {

		DBConnection dbcon = getDatabaseManager().requestConnection(session.getModelName());

		boolean deleted = DBWorkspace.deleteAll(dbcon, sUserID);

		getDatabaseManager().releaseConnection(session.getModelName(),dbcon);

		return deleted;
	}

	/**
	 * Returns all workspace views for the given workspace id
	 *
	 * @param PCSession session, the PCSession object for the database to use.
	 * @param String sWorkspaceID, the workspace ids whose workspace views to return.
	 * @return Vector, a list of the WorkspaceView records for the given Workspace id.
	 * @exception java.sql.SQLException
	 */
	public Vector getWorkspaceViews(PCSession session, String sWorkspaceID) throws SQLException  {

		DBConnection dbcon = getDatabaseManager().requestConnection(session.getModelName()) ;

		Vector vtWorkspace = DBWorkspace.getWorkspaceViews(dbcon, sWorkspaceID);

		getDatabaseManager().releaseConnection(session.getModelName(),dbcon);

		return vtWorkspace;
	}

	/**
	 * Returns all the workspace data for the given user id
	 *
	 * @param PCSession session, the PCSession object for the database to use.
	 * @param String sUserID, the user id of the user whose workspaces to return.
	 * @return Vector, a list of the Workspace records for the given user.
	 * @exception java.sql.SQLException
	 */
	public Vector getWorkspaces(PCSession session, String sUserID) throws SQLException  {

		DBConnection dbcon = getDatabaseManager().requestConnection(session.getModelName()) ;

		Vector vtWorkspace = DBWorkspace.getWorkspaces(dbcon, sUserID);

		getDatabaseManager().releaseConnection(session.getModelName(),dbcon);

		return vtWorkspace;
	}
}
