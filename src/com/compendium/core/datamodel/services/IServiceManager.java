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

import com.compendium.core.datamodel.*;
import com.compendium.core.db.*;
import com.compendium.core.db.management.*;

/**
 *	The interface for the CodeService class
 *	The code service class provides remote services to manipuate code objects.
 *
 *	@author Sajid and Rema / Michelle Bachler
 */
public interface IServiceManager {

	/**
	 * A user logging on to a specific project (after he enters userID and password for a particular model)
	 *
	 * @exception java.sql.SQLException, this is thrown if connection to database fails or user profile cannot be found.
	 */
	public Model registerUser(String modelName, String userID, String password) throws SQLException;

	/**
	 * called when the user wants to leave the database project.
	 * @param session com.compendium.datamodel.PCSession, the Sesion object for the current database session.
	 */
	public boolean unregisterUser(PCSession session);

	/**
	 * Recreate the ServiceCache objects, and zero out the service load counts.
	 */
	public void cleanUp();

	/**
	 *	Return the <code>DBDatabaseManager</code> Object, used by this ServiceManager.
	 */
	public DBDatabaseManager getDatabaseManager();

	/**
	 * Clean up the Service instances when the application is closed.
	 *
	 * @param sessionId, the id of the session running when the application was closed.
	 * @param sUserID, the id of the user whose was logged in when the application was closed.
	 */
	public void cleanupServices(String sessionId,String userName);

	/**
	 * Convienence Method to get the Model based on the session id
	 * @param sessionId, the is of the session to return the IModel for.
	 * @return com.compendium.core.datamodel.IModel, the model which corresponds tp the given session id.
	 */
	public IModel getModel(String sessionId);

	 /**
	  * Terminate all Database Connections for the given database model.
	  */
	public boolean removeAllConnections(String modelName);

	/**
	 * Look for a free view service in the relevant cache, else create a new service, and return it.
	 * @return com.compendium.core.datamodel.services.IViewService.
	 */
	public IViewService getViewService();

	/**
	 * Look for a free node service in the relevant cache, else create a new service, and return it.
	 * @return com.compendium.core.datamodel.services.INodeService.
	 */
	public INodeService getNodeService();

	/**
	 * Look for a free code service in the relevant cache, else create a new service, and return it.
	 * @return com.compendium.core.datamodel.services.ICodeService.
	 */
	public ICodeService getCodeService();

	/**
	 * Look for a free link service in the relevant cache, else create a new service, and return it.
	 * @return com.compendium.core.datamodel.services.ILinkService.
	 */
	public ILinkService getLinkService();

	/**
	 * Look for a free query service in the relevant cache, else create a new service, and return it.
	 * @return com.compendium.core.datamodel.services.IQueryService.
	 */
	public IQueryService getQueryService();

	/**
	 * Look for a free user service in the relevant cache, else create a new service, and return it.
	 * @return com.compendium.core.datamodel.services.IUserService.
	 */
	public IUserService getUserService();

	/**
	 * Look for a free favorite service in the relevant cache, else create a new service, and return it.
	 * @return com.compendium.core.datamodel.services.IFavoriteService.
	 */
	public IFavoriteService getFavoriteService();

	/**
	 * Look for a free view property service in the relevant cache, else create a new service, and return it.
	 * @return com.compendium.core.datamodel.services.IViewPropertyService.
	 */
	public IViewPropertyService getViewPropertyService();

	/**
	 * Look for a free view layer service in the relevant cache, else create a new service, and return it.
	 * @return com.compendium.core.datamodel.services.IViewLayerService.
	 */
	public IViewLayerService getViewLayerService();

	/**
	 * Look for a free workspace service in the relevant cache, else create a new service, and return it.
	 * @return com.compendium.core.datamodel.services.IWorkspaceService.
	 */
	public IWorkspaceService getWorkspaceService();

	/**
	 * Look for a free code group service in the relevant cache, else create a new service, and return it.
	 * @return com.compendium.core.datamodel.services.ICodeGroupService.
	 */
	public ICodeGroupService getCodeGroupService();

	/**
	 * Look for a free group code service in the relevant cache, else create a new service, and return it.
	 * @return com.compendium.core.datamodel.services.IGroupCodeService.
	 */
	public IGroupCodeService getGroupCodeService();

	/**
	 * Look for a free system service in the relevant cache, else create a new service, and return it.
	 * @return com.compendium.core.datamodel.services.ISystemService.
	 */
	public ISystemService getSystemService();


	/**
	 * Look for a free external connection service in the relevant cache, else create a new service, and return it.
	 * @return com.compendium.core.datamodel.services.IExternalconnectionService.
	 */
	public IExternalConnectionService getExternalConnectionService();

	/**
	 * Look for a free external meeting service in the relevant cache, else create a new service, and return it.
	 * @return com.compendium.core.datamodel.services.IMeetingService.
	 */
	public IMeetingService getMeetingService();
	
	/**
	 * Look for a free linked file service in the relevant cache, else create a new service, and return it.
	 * @return com.compendium.core.datamodel.services.ILinkedFileService
	 */
	public ILinkedFileService getLinkedFileService();
		
	/**
	 * Look for a free movie service in the relevant cache, else create a new service, and return it.
	 * @return com.compendium.core.datamodel.services.IMovieService
	 */
	public IMovieService getMovieService();	
}
