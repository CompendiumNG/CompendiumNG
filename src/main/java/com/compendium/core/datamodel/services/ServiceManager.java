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

import java.io.*;
import java.util.*;
import java.sql.*;

import com.compendium.core.db.management.*;
import com.compendium.core.datamodel.*;
import com.compendium.core.ICoreConstants;
import com.compendium.core.CoreUtilities;

/**
 *	The ServiceManager class, manages the Services which are used to access talk to the database.
 *
 *	@author Sajid and Rema / Michelle Bachler
 */
public class ServiceManager implements IServiceManager, java.io.Serializable {

	/** The current maximum service load on a given Service object.*/
	private static final int SERVICELOAD				= 5;

	/** int representing a ViewService.*/
	private static final int VIEWSERVICE 				= 1;

	/** int representing a NodeService.*/
	private static final int NODESERVICE 				= 2;

	/** int representing a LinkService.*/
	private static final int LINKSERVICE 				= 3;

	/** int representing a CodeService.*/
	private static final int CODESERVICE 				= 4;

	/** int representing a QueryService.*/
	private static final int QUERYSERVICE				= 6;

	/** int representing a UserService.*/
	private static final int USERSERVICE 				= 7;

	/** int representing a FavoriteService.*/
	private static final int FAVORITESERVICE			= 8;

	/** int representing a ViewPropertyService.*/
	private static final int VIEWPROPERTYSERVICE		= 9;

	/** int representing a WorkspaceService.*/
	private static final int WORKSPACESERVICE			= 10;

	/** int representing a CodeGroupService.*/
	private static final int CODEGROUPSERVICE			= 11;

	/** int representing a GroupCodeService.*/
	private static final int GROUPCODESERVICE			= 12;

	/** int representing a SystemService.*/
	private static final int SYSTEMSERVICE				= 13;

	/** int representing a ViewLayerService.*/
	private static final int VIEWLAYERSERVICE			= 14;

	/** int representing a ExternalConnectionService.*/
	private static final int EXTERNALCONNECTIONSERVICE	= 15;

	/** int representing a MeetingService.*/
	private static final int MEETINGSERVICE				= 16;

	/** int representing a LinkedFileService */
	private static final int LINKEDFILESERVICE			= 17;

	/** int representing a MovieService */
	private static final int MOVIESERVICE				= 18;

	/** The current load count on the <code>ViewService</code> object.*/
	private static int viewCount 				= 0;

	/** The current load count on the <code>NodeService</code> object.*/
	private static int nodeCount 				= 0;

	/** The current load count on the <code>LinkService</code> object.*/
	private static int linkCount 				= 0;

	/** The current load count on the <code>CodeService</code> object.*/
	private static int codeCount 				= 0;

	/** The current load count on the <code>QueryService</code> object.*/
	private static int queryCount 				= 0;

	/** The current load count on the <code>UserService</code> object.*/
	private static int userCount 				= 0;

	/** The current load count on the <code>FavoriteService</code> object.*/
	private static int favoriteCount 			= 0;

	/** The current load count on the <code>ViewPropertyService</code> object.*/
	private static int viewpropertyCount 		= 0;

	/** The current load count on the <code>ViewLayerService</code> object.*/
	private static int viewlayerCount 			= 0;

	/** The current load count on the <code>WorkspaceService</code> object.*/
	private static int workspaceCount 			= 0;

	/** The current load count on the <code>CodeGroupService</code> object.*/
	private static int codegroupCount 			= 0;

	/** The current load count on the <code>GroupCodeService</code> object.*/
	private static int groupcodeCount 			= 0;

	/** The current load count on the <code>SystemService</code> object.*/
	private static int systemCount 				= 0;

	/** The current load count on the <code>ExternalConnectionService</code> object.*/
	private static int externalConnectionCount 	= 0;

	/** The current load count on the <code>MeetingService</code> object.*/
	private static int meetingCount 	= 0 ;

	/** The current load count on the <code>LinkedFileService</code> object.*/
	private static int linkedFileCount 	= 0 ;

	/** The current load count on the <code>MovieService</code> object.*/
	private static int movieCount 	= 0 ;

	/** The models returned to clients - session ID is the key, model as the first object.*/
	private Hashtable htModels = new Hashtable(51);

	/** The service manager creates the database manager that is used by all services*/
	private DBDatabaseManager oDbMgr = null ;

	// Store a service object and count of clients serviced by that object.
	// the argument is the maximum LOAD on each service
	/** The <code>ServiceCache</code> instance holding <code>ViewService</code> objects and their LOAD counts.*/
	private static ServiceCache oViewServiceCache = new ServiceCache(SERVICELOAD);

	/** The <code>ServiceCache</code> instance holding <code>NodeService</code> objects and their LOAD counts.*/
	private static ServiceCache oNodeServiceCache = new ServiceCache(SERVICELOAD);

	/** The <code>ServiceCache</code> instance holding <code>LinkService</code> objects and their LOAD counts.*/
	private static ServiceCache oLinkServiceCache = new ServiceCache(SERVICELOAD);

	/** The <code>ServiceCache</code> instance holding <code>CodeService</code> objects and their LOAD counts.*/
	private static ServiceCache oCodeServiceCache = new ServiceCache(SERVICELOAD);

	/** The <code>ServiceCache</code> instance holding <code>QueryService</code> objects and their LOAD counts.*/
	private static ServiceCache oQueryServiceCache = new ServiceCache(SERVICELOAD);

	/** The <code>ServiceCache</code> instance holding <code>UserService</code> objects and their LOAD counts.*/
	private static ServiceCache oUserServiceCache = new ServiceCache(SERVICELOAD);

	/** The <code>ServiceCache</code> instance holding <code>FavoriteService</code> objects and their LOAD counts.*/
	private static ServiceCache oFavoriteServiceCache = new ServiceCache(SERVICELOAD);

	/** The <code>ServiceCache</code> instance holding <code>ViewPropertyService</code> objects and their LOAD counts.*/
	private static ServiceCache oViewPropertyServiceCache = new ServiceCache(SERVICELOAD);

	/** The <code>ServiceCache</code> instance holding <code>ViewLayerService</code> objects and their LOAD counts.*/
	private static ServiceCache oViewLayerServiceCache = new ServiceCache(SERVICELOAD);

	/** The <code>ServiceCache</code> instance holding <code>WorkspaceService</code> objects and their LOAD counts.*/
	private static ServiceCache oWorkspaceServiceCache = new ServiceCache(SERVICELOAD);

	/** The <code>ServiceCache</code> instance holding <code>CodeGroupService</code> objects and their LOAD counts.*/
	private static ServiceCache oCodeGroupServiceCache = new ServiceCache(SERVICELOAD);

	/** The <code>ServiceCache</code> instance holding <code>GroupCodeService</code> objects and their LOAD counts.*/
	private static ServiceCache oGroupCodeServiceCache = new ServiceCache(SERVICELOAD);

	/** The <code>ServiceCache</code> instance holding <code>SystemService</code> objects and their LOAD counts.*/
	private static ServiceCache oSystemServiceCache = new ServiceCache(SERVICELOAD);

	/** The <code>ServiceCache</code> instance holding <code>ExternalConnectionService</code> objects and their LOAD counts.*/
	private static ServiceCache oExternalConnectionServiceCache = new ServiceCache(SERVICELOAD);

	/** The <code>ServiceCache</code> instance holding <code>MeetingService</code> objects and their LOAD counts.*/
	private static ServiceCache oMeetingServiceCache = new ServiceCache(SERVICELOAD);

	/** The <code>ServiceCache</code> instance holding <code>LinkedFileService</code> objects and their LOAD counts.*/
	private static ServiceCache oLinkedFileServiceCache = new ServiceCache(SERVICELOAD);

	/** The <code>ServiceCache</code> instance holding <code>MovieService</code> objects and their LOAD counts.*/
	private static ServiceCache oMovieServiceCache = new ServiceCache(SERVICELOAD);

	/** This contains the session ID and the userID for the session.*/
	private UserSessionCache 	oUserSessionCache	= new UserSessionCache();


	/**
	 *	Constructor
	 *
	 * @param nDatabaseType, the type of the database being used
	 */
	public ServiceManager(int nDatabaseType) {
		super();
		oDbMgr = new DBDatabaseManager(nDatabaseType);
	}

	/**
	 *	Constructor
	 *
	 * @param nDatabaseType, the type of the database being used
	 * @param sUserName, the name to use when login in to the database. This is passed on to DBDatabaseManager
	 * @param sPassword, the password to use when login in to the database. This is passed on to DBDatabaseManager
	 */
	public ServiceManager(int nDatabaseType, String sUserName, String sPassword) {
		super();
		oDbMgr = new DBDatabaseManager(nDatabaseType, sUserName, sPassword);
	}

	/**
	 *	Constructor
	 *
	 * @param nDatabaseType, the type of the database being used
	 * @param sUserName, the name to use when login in to the database. This is passed on to DBDatabaseManager
	 * @param sPassword, the password to use when login in to the database. This is passed on to DBDatabaseManager
	 * @param sDatabaseIP, the IP address of the server machine used when accessing the database. This is passed on to DBDatabaseManager.
	 * The default if 'localhost'.
	 */
	public ServiceManager(int nDatabaseType, String sUserName, String sPassword, String sDatabaseIP) {
		super();
		oDbMgr = new DBDatabaseManager(nDatabaseType, sUserName, sPassword, sDatabaseIP);
	}

	/**
	 *	Return the <code>DBDatabaseManager</code> Object, used by this ServiceManager.
	 */
	public DBDatabaseManager getDatabaseManager() {
		return oDbMgr;
	}

	/**
	 * A user logging on to a specific project (after he enters userID and password for a particular model)
	 *
	 * @exception java.sql.SQLException, this is thrown if connection to database fails or user profile cannot be found.
	 */
	public Model registerUser(String modelName, String loginName, String password) throws SQLException {

		Model model = new Model();

		UserProfile up = ((UserService)getUserService()).getUserProfile(modelName, loginName, password);

		if(up != null) {
			if (up.isActive()) {
				String userID = up.getId();
	
				// get a new session id for this user
				String sessionID = Model.getStaticUniqueID();
	
				// add the session id to the UserSessionCache since a user can have different sessions
				oUserSessionCache.put(loginName, sessionID);
	
				// create a session object which will be used during the lifetime of the user session
				PCSession session = new PCSession(sessionID, modelName, userID);
	
				// get model for the user from the database
				model = createModel(session, up) ;
	
				// add model to hashtable with the session ID as the key
				// this hashtable is used when a user logs off, the services held by user are released
				if(!htModels.containsKey((model.getSession()).getSessionID()))
					htModels.put( (model.getSession()).getSessionID(), model);
			} else {
				model.addErrorMessage("This user account has been deactivated.");
			}
		} else {
			model.addErrorMessage("User data could not be loaded");
		}

		return model;
	}

	/**
	 * called when the user wants to leave the database project.
	 * @param session com.compendium.datamodel.PCSession, the Sesion object for the current database session.
	 */
	public boolean unregisterUser(PCSession session) {

		oUserSessionCache.remove(session.getUserID(), session.getSessionID());
		return true;
	}

	/**
	 * Called by the register method for a client to get his model object set for a new project.
	 * Create an instance of each service for use by the Model.
	 * @param session com.compendium.datamodel.PCSession, the Sesion object for the current database session.
	 * @param up com.compendium.datamodel.UserProfile, the user registering.
	 */
	private Model createModel(PCSession session, UserProfile up) {

		// get the client name
		String clientName = session.getUserID();

		// get the model name
		String modelName = session.getModelName();

		// initialize model object and return it

		// create a model for the time being since cannot retrieve a live model from db now
		Model model = new Model(modelName);

		UserService userService = (UserService)getUserService();
		model.setUserService((IUserService)userService);
		model.setUserProfile(up);
		model.setSession(session);

		// SERVICES FOR THIS MODEL

		ViewService viewService = (ViewService)getViewService();
		viewService.addSession(session) ;
		model.setViewService((IViewService)viewService);

		NodeService nodeService = (NodeService)getNodeService();
		nodeService.addSession(session) ;
		model.setNodeService((INodeService)nodeService);

		LinkService linkService = (LinkService)getLinkService();
		linkService.addSession(session) ;
		model.setLinkService((ILinkService)linkService);

		CodeService codeService = (CodeService)getCodeService();
		codeService.addSession(session) ;
		model.setCodeService((ICodeService)codeService);

		QueryService queryService = (QueryService)getQueryService();
		queryService.addSession(session) ;
		model.setQueryService((IQueryService)queryService);

		////////////// This service were already assigned to the client
		////////////// If for some reason, they were removed then assign again
		if(model.getUserService() == null) {
			userService = (UserService)getUserService();
			userService.addSession(session) ;
			model.setUserService((IUserService)userService);
		}

		FavoriteService favoriteService = (FavoriteService) getFavoriteService();
		favoriteService.addSession(session);
		model.setFavoriteService((IFavoriteService)favoriteService);

		ViewPropertyService viewpropertyService = (ViewPropertyService) getViewPropertyService();
		viewpropertyService.addSession(session);
		model.setViewPropertyService((IViewPropertyService)viewpropertyService);

		ViewLayerService viewlayerService = (ViewLayerService) getViewLayerService();
		viewlayerService.addSession(session);
		model.setViewLayerService((IViewLayerService)viewlayerService);

		WorkspaceService workspaceService = (WorkspaceService) getWorkspaceService();
		workspaceService.addSession(session);
		model.setWorkspaceService((IWorkspaceService)workspaceService);

		CodeGroupService codegroupService = (CodeGroupService) getCodeGroupService();
		codegroupService.addSession(session);
		model.setCodeGroupService((ICodeGroupService)codegroupService);

		GroupCodeService groupcodeService = (GroupCodeService) getGroupCodeService();
		groupcodeService.addSession(session);
		model.setGroupCodeService((IGroupCodeService)groupcodeService);

		SystemService systemService = (SystemService) getSystemService();
		systemService.addSession(session);
		model.setSystemService((ISystemService)systemService);

		ExternalConnectionService externalConnectionService = (ExternalConnectionService) getExternalConnectionService();
		externalConnectionService.addSession(session);
		model.setExternalConnectionService((IExternalConnectionService)externalConnectionService);

		MeetingService meetingService = (MeetingService) getMeetingService();
		meetingService.addSession(session);
		model.setMeetingService((IMeetingService)meetingService);

		LinkedFileService linkedFileService = (LinkedFileService) getLinkedFileService();
		linkedFileService.addSession(session);
		model.setLinkedFileService((ILinkedFileService)linkedFileService);

		MovieService movieService = (MovieService) getMovieService();
		movieService.addSession(session);
		model.setMovieService((IMovieService)movieService);

		//printServicesStatus();

		return model;
	}

	/**
	 * Prints the status of each service (load).
	 */
	public void printServicesStatus() {

		oViewServiceCache.printServiceStatus();
		oNodeServiceCache.printServiceStatus();
		oLinkServiceCache.printServiceStatus();
		oCodeServiceCache.printServiceStatus();
		oUserServiceCache.printServiceStatus();
		oQueryServiceCache.printServiceStatus();
		oFavoriteServiceCache.printServiceStatus();
		oViewPropertyServiceCache.printServiceStatus();
		oViewLayerServiceCache.printServiceStatus();
		oWorkspaceServiceCache.printServiceStatus();
		oCodeGroupServiceCache.printServiceStatus();
		oGroupCodeServiceCache.printServiceStatus();
		oSystemServiceCache.printServiceStatus();
		oExternalConnectionServiceCache.printServiceStatus();
		oMeetingServiceCache.printServiceStatus();
		oLinkedFileServiceCache.printServiceStatus();
		oMovieServiceCache.printServiceStatus();
	}

	/**
	 * Recreate the ServiceCache objects, and zero out the service load counts.
	 */
	public void cleanUp() {

		oViewServiceCache = new ServiceCache(SERVICELOAD);
		oNodeServiceCache = new ServiceCache(SERVICELOAD);
		oLinkServiceCache = new ServiceCache(SERVICELOAD);
		oCodeServiceCache = new ServiceCache(SERVICELOAD);
		oQueryServiceCache = new ServiceCache(SERVICELOAD);
		oUserServiceCache = new ServiceCache(SERVICELOAD);
		oFavoriteServiceCache = new ServiceCache(SERVICELOAD);
		oViewPropertyServiceCache = new ServiceCache(SERVICELOAD);
		oViewLayerServiceCache = new ServiceCache(SERVICELOAD);
		oWorkspaceServiceCache = new ServiceCache(SERVICELOAD);
		oCodeGroupServiceCache = new ServiceCache(SERVICELOAD);
		oGroupCodeServiceCache = new ServiceCache(SERVICELOAD);
		oSystemServiceCache = new ServiceCache(SERVICELOAD);
		oExternalConnectionServiceCache = new ServiceCache(SERVICELOAD);
		oMeetingServiceCache = new ServiceCache(SERVICELOAD);
		oLinkedFileServiceCache = new ServiceCache(SERVICELOAD);
		oMovieServiceCache = new ServiceCache(SERVICELOAD);
		
		viewCount = 0;
		nodeCount = 0;
		linkCount = 0;
		codeCount = 0;
		queryCount = 0;
		userCount = 0 ;
		favoriteCount = 0;
		viewpropertyCount = 0;
		viewlayerCount = 0;
		workspaceCount = 0;
		codegroupCount = 0;
		groupcodeCount = 0;
		systemCount = 0;
		externalConnectionCount = 0;
		meetingCount = 0;
		linkedFileCount = 0;
		movieCount = 0;
	}

	/**
	 * Generate a new unique service name for the given service type.
	 *
	 * @param serviceType, the type of service to generate the new name for.
	 * @return java.lang.String, the new unqiue name for the given service.
	 */
	private String generateServiceName(int serviceType) {
		String name = "";

		switch (serviceType) {
		case VIEWSERVICE:
			name = "viewService_" + ++viewCount;
			break;
		case NODESERVICE:
			name = "nodeService_" + ++nodeCount;
			break;
		case LINKSERVICE:
			name = "linkService_" + ++linkCount;
			break;
		case CODESERVICE:
			name = "codeService_" + ++codeCount;
			break;
		case QUERYSERVICE:
			name = "queryService_" + ++queryCount;
			break ;
		case USERSERVICE:
			name = "userService_" + ++userCount;
			break ;
		case FAVORITESERVICE:
			name = "favoriteService_" + ++favoriteCount;
			break ;
		case VIEWPROPERTYSERVICE:
			name = "viewpropertyService_" + ++viewpropertyCount;
			break ;
		case VIEWLAYERSERVICE:
			name = "viewlayerService_" + ++viewlayerCount;
			break ;
		case WORKSPACESERVICE:
			name = "workspaceService_" + ++workspaceCount;
			break ;
		case CODEGROUPSERVICE:
			name = "codegroupService_" + ++codegroupCount;
			break ;
		case GROUPCODESERVICE:
			name = "groupcodeService_" + ++groupcodeCount;
			break ;
		case SYSTEMSERVICE:
			name = "systemService_" + ++systemCount;
			break ;
		case EXTERNALCONNECTIONSERVICE:
			name = "externalConnectionService_" + ++externalConnectionCount;
			break ;
		case MEETINGSERVICE:
			name = "meetingService_" + ++meetingCount;
			break ;
		case LINKEDFILESERVICE:
			name = "linkedFileService_" + ++linkedFileCount;
			break ;
		case MOVIESERVICE:
			name = "movieService_" + ++movieCount;
			break ;
		}
		return name;
	}


// THE SERVICES SECTION

	/**
	 * Look for a free view service in the relevant cache, else create a new service, and return it.
	 * @return com.compendium.core.datamodel.services.IViewService.
	 */
	public IViewService getViewService() {

		ViewService view = null;
		String sName = "";

		// look for a free view service, else create new service
		if(oViewServiceCache.isEmpty()) {
			sName = generateServiceName(VIEWSERVICE);
			view = new ViewService(sName, this, oDbMgr);
			oViewServiceCache.put(sName, (IService)view, new Integer(1));
		}
		else {
			// check for a view service that can support a new client
			Vector v = oViewServiceCache.getLowestCount();

			if (v != null ) {

				int count = ((Integer)v.elementAt(1)).intValue() ;
				count++ ;
				view = (ViewService)v.elementAt(1) ;

				oViewServiceCache.put(view.getServiceName(), view, new Integer(count));
				return (IViewService)v.elementAt(1) ;
			}

			// no free services available, so create new one
			sName = generateServiceName(VIEWSERVICE);
			view = new ViewService(sName, this, oDbMgr) ;
			oViewServiceCache.put(sName, (IService)view, new Integer(1));
		}

		return (IViewService)view;
	}

	/**
	 * Look for a free node service in the relevant cache, else create a new service, and return it.
	 * @return com.compendium.core.datamodel.services.INodeService.
	 */
	public INodeService getNodeService() {

		String sName = "";
		NodeService node = null;

		// look for a free node service, else create new service
		if(oNodeServiceCache.isEmpty()) {

			sName = generateServiceName(NODESERVICE);
			node = new NodeService(sName, this, oDbMgr) ;
			oNodeServiceCache.put(sName,node, new Integer(1));
		}
		else {
			// check for a node service that can support a new client
			Vector v = oNodeServiceCache.getLowestCount() ;
			if (v != null ) {
				int count = ((Integer)v.elementAt(2)).intValue() ;
				count++ ;
				node = (NodeService)v.elementAt(1) ;
				oNodeServiceCache.put(node.getServiceName(), node, new Integer(count));
				return (INodeService)v.elementAt(1) ;

			}
			// no free services available, so create new one
			sName = generateServiceName(NODESERVICE);
			node = new NodeService(sName, this, oDbMgr) ;
			oNodeServiceCache.put(sName,node,new Integer(1));
		}

		return node;
	}

	/**
	 * Look for a free code service in the relevant cache, else create a new service, and return it.
	 * @return com.compendium.core.datamodel.services.ICodeService.
	 */
	public ICodeService getCodeService() {

		String sName = "";
		CodeService code = null;

		// look for a free code service, else create new service
		if(oCodeServiceCache.isEmpty()) {

			sName = generateServiceName(CODESERVICE);
			code = new CodeService(sName, this, oDbMgr) ;
			oCodeServiceCache.put(sName,code, new Integer(1));
		}
		else {
			// check for a code service that can support a new client
			Vector v = oCodeServiceCache.getLowestCount() ;
			if (v!= null ) {
				int count = ((Integer)v.elementAt(2)).intValue() ;
				count++ ;
				code = (CodeService)v.elementAt(1) ;
				oCodeServiceCache.put(code.getServiceName(), code, new Integer(count));
				return (ICodeService)v.elementAt(1) ;
			}
			// no free services available, so create new one
			sName = generateServiceName(CODESERVICE);
			code = new CodeService(sName, this, oDbMgr) ;
			oCodeServiceCache.put(sName,code,new Integer(1));
		}

		return code;
	}

	/**
	 * Look for a free link service in the relevant cache, else create a new service, and return it.
	 * @return com.compendium.core.datamodel.services.ILinkService.
	 */
	public ILinkService getLinkService() {

		String sName = "";
		LinkService link = null;

		// look for a free link service, else create new service
		if(oLinkServiceCache.isEmpty()) {

			sName = generateServiceName(LINKSERVICE);
			link = new LinkService(sName, this, oDbMgr);
			oLinkServiceCache.put(sName,link, new Integer(1));
		}
		else {
			// check for a link service that can support a new client
			Vector v = oLinkServiceCache.getLowestCount();

			if (v!= null ) {
				int count = ((Integer)v.elementAt(2)).intValue();
				count++ ;
				link =  (LinkService)v.elementAt(1) ;
				oLinkServiceCache.put(link.getServiceName(), link, new Integer(count));
				return (ILinkService)v.elementAt(1) ;
			}

			// no free services available, so create new one
			sName = generateServiceName(LINKSERVICE);
			link = new LinkService(sName, this, oDbMgr) ;
			oLinkServiceCache.put(sName,link,new Integer(1));
		}

		return (ILinkService)link;
	}

	/**
	 * Look for a free query service in the relevant cache, else create a new service, and return it.
	 * @return com.compendium.core.datamodel.services.IQueryService.
	 */
	public IQueryService getQueryService() {

		String sName = "";
		QueryService query = null;

		if(oQueryServiceCache.isEmpty()) {

			sName = generateServiceName(QUERYSERVICE);
			query = new QueryService(sName, this, oDbMgr) ;
			oQueryServiceCache.put(sName,query, new Integer(1));
			return (IQueryService)query;
		}
		else {
			// check for a query service that can support a new client
			Vector v = oQueryServiceCache.getLowestCount() ;
			if (v!= null ) {
				int count = ((Integer)v.elementAt(2)).intValue() ;
				count++ ;
				query =  (QueryService)v.elementAt(1) ;
				oQueryServiceCache.put(query.getServiceName(), query, new Integer(count));
				return (IQueryService)v.elementAt(1) ;

			}
			// no free services available, so create new one
			sName = generateServiceName(QUERYSERVICE);
			query = new QueryService(sName, this, oDbMgr) ;
			oQueryServiceCache.put(sName,query,new Integer(1));
		}

		return (IQueryService)query;
	}

	/**
	 * Look for a free user service in the relevant cache, else create a new service, and return it.
	 * @return com.compendium.core.datamodel.services.IUserService.
	 */
	public IUserService getUserService() {

		String sName = "";
		UserService user = null;

		if(oUserServiceCache.isEmpty()) {

			sName = generateServiceName(USERSERVICE);
			user = new UserService(sName, this, oDbMgr) ;
			oUserServiceCache.put(sName, user, new Integer(1));
		}
		else {
			// check for a user service that can support a new client
			Vector v = oUserServiceCache.getLowestCount() ;
			if (v != null ) {
				int count = ((Integer)v.elementAt(2)).intValue() ;
				count++ ;
				user = (UserService)v.elementAt(1) ;
				oUserServiceCache.put(user.getServiceName(), user, new Integer(count));
				return (IUserService)v.elementAt(1) ;
			}
			// no free services available, so create new one
			//System.out.println("new user service generated ");
			sName = generateServiceName(USERSERVICE);
			user = new UserService(sName, this, oDbMgr) ;
			oUserServiceCache.put(sName,user,new Integer(1));
		}

		return (IUserService)user;
	}

	/**
	 * Look for a free Favorite service in the relevant cache, else create a new service, and return it.
	 * @return com.compendium.core.datamodel.services.IFavoriteService.
	 */
	public IFavoriteService getFavoriteService() {

		String sName = "";
		FavoriteService favorite = null;

		if(oFavoriteServiceCache.isEmpty()) {

			sName = generateServiceName(FAVORITESERVICE);
			favorite = new FavoriteService(sName, this, oDbMgr) ;
			oFavoriteServiceCache.put(sName, favorite, new Integer(1));
		}
		else {

			// check for a favorite service that can support a new client
			Vector v = oFavoriteServiceCache.getLowestCount() ;

			if (v != null ) { // implies returned a service

				int count = ((Integer)v.elementAt(2)).intValue() ;
				count++ ;
				favorite = (FavoriteService)v.elementAt(1) ;
				oFavoriteServiceCache.put(favorite.getServiceName(), favorite, new Integer(count));
				return (IFavoriteService)v.elementAt(1) ;
			}

			// no free services available, so create new one
			sName = generateServiceName(FAVORITESERVICE);
			favorite = new FavoriteService(sName, this, oDbMgr) ;
			oFavoriteServiceCache.put(sName, favorite,new Integer(1));
		}

		return (IFavoriteService)favorite;
	}

	/**
	 * Look for a free view property service in the relevant cache, else create a new service, and return it.
	 * @return com.compendium.core.datamodel.services.IViewPropertyService.
	 */
	public IViewPropertyService getViewPropertyService() {

		String sName = "";
		ViewPropertyService viewproperty = null;

		if(oViewPropertyServiceCache.isEmpty()) {

			sName = generateServiceName(VIEWPROPERTYSERVICE);
			viewproperty = new ViewPropertyService(sName, this, oDbMgr) ;
			oViewPropertyServiceCache.put(sName, viewproperty, new Integer(1));
		}
		else {

			// check for a view property service that can support a new client
			Vector v = oViewPropertyServiceCache.getLowestCount() ;

			if (v != null ) { // implies returned a service

				int count = ((Integer)v.elementAt(2)).intValue() ;
				count++ ;
				viewproperty = (ViewPropertyService)v.elementAt(1) ;
				oViewPropertyServiceCache.put(viewproperty.getServiceName(), viewproperty, new Integer(count));
				return (IViewPropertyService)v.elementAt(1) ;
			}

			// no free services available, so create new one
			sName = generateServiceName(VIEWPROPERTYSERVICE);
			viewproperty = new ViewPropertyService(sName, this, oDbMgr) ;
			oViewPropertyServiceCache.put(sName, viewproperty,new Integer(1));
		}

		return (IViewPropertyService)viewproperty;
	}


	/**
	 * Look for a free view layer service in the relevant cache, else create a new service, and return it.
	 * @return com.compendium.core.datamodel.services.IViewLayerService.
	 */
	public IViewLayerService getViewLayerService() {

		String sName = "";
		ViewLayerService viewlayer = null;

		if(oViewLayerServiceCache.isEmpty()) {

			sName = generateServiceName(VIEWLAYERSERVICE);
			viewlayer = new ViewLayerService(sName, this, oDbMgr) ;
			oViewLayerServiceCache.put(sName, viewlayer, new Integer(1));
		}
		else {

			// check for a view layer service that can support a new client
			Vector v = oViewLayerServiceCache.getLowestCount();

			if (v != null ) { // implies returned a service

				int count = ((Integer)v.elementAt(2)).intValue();
				count++ ;
				viewlayer = (ViewLayerService)v.elementAt(1);
				oViewLayerServiceCache.put(viewlayer.getServiceName(), viewlayer, new Integer(count));
				return (IViewLayerService)v.elementAt(1) ;
			}

			// no free services available, so create new one
			sName = generateServiceName(VIEWLAYERSERVICE);
			viewlayer = new ViewLayerService(sName, this, oDbMgr) ;
			oViewLayerServiceCache.put(sName, viewlayer, new Integer(1));
		}

		return (IViewLayerService)viewlayer;
	}

	/**
	 * Look for a free workspace service in the relevant cache, else create a new service, and return it.
	 * @return com.compendium.core.datamodel.services.IWorkspaceService.
	 */
	public IWorkspaceService getWorkspaceService() {

		String sName = "";
		WorkspaceService workspace = null;

		if(oWorkspaceServiceCache.isEmpty()) {

			sName = generateServiceName(WORKSPACESERVICE);
			workspace = new WorkspaceService(sName, this, oDbMgr) ;
			oWorkspaceServiceCache.put(sName, workspace, new Integer(1));
		}
		else {

			// check for a view property service that can support a new client
			Vector v = oWorkspaceServiceCache.getLowestCount() ;

			if (v != null ) { // implies returned a service

				int count = ((Integer)v.elementAt(2)).intValue() ;
				count++ ;
				workspace = (WorkspaceService)v.elementAt(1) ;
				oWorkspaceServiceCache.put(workspace.getServiceName(), workspace, new Integer(count));
				return (IWorkspaceService)v.elementAt(1) ;
			}

			// no free services available, so create new one
			sName = generateServiceName(WORKSPACESERVICE);
			workspace = new WorkspaceService(sName, this, oDbMgr) ;
			oWorkspaceServiceCache.put(sName, workspace,new Integer(1));
		}

		return (IWorkspaceService)workspace;
	}

	/**
	 * Look for a free code gourp service in the relevant cache, else create a new service, and return it.
	 * @return com.compendium.core.datamodel.services.ICodeGroupService.
	 */
	public ICodeGroupService getCodeGroupService() {

		String sName = "";
		CodeGroupService codegroup = null;

		if(oCodeGroupServiceCache.isEmpty()) {

			sName = generateServiceName(CODEGROUPSERVICE);
			codegroup = new CodeGroupService(sName, this, oDbMgr) ;
			oCodeGroupServiceCache.put(sName, codegroup, new Integer(1));
		}
		else {

			// check for a view property service that can support a new client
			Vector v = oCodeGroupServiceCache.getLowestCount() ;

			if (v != null ) { // implies returned a service

				int count = ((Integer)v.elementAt(2)).intValue() ;
				count++ ;
				codegroup = (CodeGroupService)v.elementAt(1) ;
				oCodeGroupServiceCache.put(codegroup.getServiceName(), codegroup, new Integer(count));
				return (ICodeGroupService)v.elementAt(1) ;
			}

			// no free services available in the hashtable, so create new one
			sName = generateServiceName(CODEGROUPSERVICE);
			codegroup = new CodeGroupService(sName, this, oDbMgr) ;
			oCodeGroupServiceCache.put(sName, codegroup,new Integer(1));
		}

		return (ICodeGroupService)codegroup;
	}

	/**
	 * Look for a free group code service in the relevant cache, else create a new service, and return it.
	 * @return com.compendium.core.datamodel.services.IGroupCodeService.
	 */
	public IGroupCodeService getGroupCodeService() {

		String sName = "";
		GroupCodeService groupcode = null;

		if(oGroupCodeServiceCache.isEmpty()) {

			sName = generateServiceName(GROUPCODESERVICE);
			groupcode = new GroupCodeService(sName, this, oDbMgr) ;
			oGroupCodeServiceCache.put(sName, groupcode, new Integer(1));
		}
		else {

			// check for a view property service that can support a new client
			Vector v = oGroupCodeServiceCache.getLowestCount() ;

			if (v != null ) { // implies returned a service

				int count = ((Integer)v.elementAt(2)).intValue() ;
				count++ ;
				groupcode = (GroupCodeService)v.elementAt(1) ;
				oGroupCodeServiceCache.put(groupcode.getServiceName(), groupcode, new Integer(count));
				return (IGroupCodeService)v.elementAt(1) ;
			}

			// no free services available in the hashtable, so create new one
			sName = generateServiceName(GROUPCODESERVICE);
			groupcode = new GroupCodeService(sName, this, oDbMgr) ;
			oGroupCodeServiceCache.put(sName, groupcode,new Integer(1));
		}

		return (IGroupCodeService)groupcode;
	}

	/**
	 * Look for a free system service in the relevant cache, else create a new service, and return it.
	 * @return com.compendium.core.datamodel.services.ISystemService.
	 */
	public ISystemService getSystemService() {

		String sName = "";
		SystemService system = null;

		if(oSystemServiceCache.isEmpty()) {
			sName = generateServiceName(SYSTEMSERVICE);
			system = new SystemService(sName, this, oDbMgr) ;
			oSystemServiceCache.put(sName, system, new Integer(1));
		}
		else {
			// check for a view property service that can support a new client
			Vector v = oSystemServiceCache.getLowestCount() ;

			if (v != null ) { // implies returned a service

				int count = ((Integer)v.elementAt(2)).intValue() ;
				count++ ;
				system = (SystemService)v.elementAt(1) ;
				oSystemServiceCache.put(system.getServiceName(), system, new Integer(count));
				return (ISystemService)v.elementAt(1) ;
			}

			// no free services available, so create new one
			sName = generateServiceName(SYSTEMSERVICE);
			system = new SystemService(sName, this, oDbMgr) ;
			oSystemServiceCache.put(sName, system,new Integer(1));
		}

		return (ISystemService)system;
	}

	/**
	 * Look for a free external connection service in the relevant cache, else create a new service, and return it.
	 * @return com.compendium.core.datamodel.services.IExternalConnnectionService.
	 */
	public IExternalConnectionService getExternalConnectionService() {

		String sName = "";
		ExternalConnectionService connection = null;

		if(oExternalConnectionServiceCache.isEmpty()) {
			sName = generateServiceName(EXTERNALCONNECTIONSERVICE);
			connection = new ExternalConnectionService(sName, this, oDbMgr) ;
			oExternalConnectionServiceCache.put(sName, connection, new Integer(1));
		}
		else {
			// check for a external connection service that can support a new client
			Vector v = oExternalConnectionServiceCache.getLowestCount() ;

			if (v != null ) { // implies returned a service

				int count = ((Integer)v.elementAt(2)).intValue() ;
				count++ ;
				connection = (ExternalConnectionService)v.elementAt(1) ;
				oExternalConnectionServiceCache.put(connection.getServiceName(), connection, new Integer(count));
				return (IExternalConnectionService)v.elementAt(1) ;
			}

			// no free services available, so create new one
			sName = generateServiceName(EXTERNALCONNECTIONSERVICE);
			connection = new ExternalConnectionService(sName, this, oDbMgr) ;
			oExternalConnectionServiceCache.put(sName, connection, new Integer(1));
		}

		return (IExternalConnectionService)connection;
	}

	/**
	 * Look for a free external meeting service in the relevant cache, else create a new service, and return it.
	 * @return com.compendium.core.datamodel.services.IMeetingService.
	 */
	public IMeetingService getMeetingService() {

		String sName = "";
		MeetingService meeting = null;

		if(oMeetingServiceCache.isEmpty()) {
			sName = generateServiceName(MEETINGSERVICE);
			meeting = new MeetingService(sName, this, oDbMgr) ;
			oMeetingServiceCache.put(sName, meeting, new Integer(1));
		}
		else {
			// check for a meeting service that can support a new client
			Vector v = oMeetingServiceCache.getLowestCount() ;

			if (v != null ) { // implies returned a service

				int count = ((Integer)v.elementAt(2)).intValue() ;
				count++ ;
				meeting = (MeetingService)v.elementAt(1) ;
				oMeetingServiceCache.put(meeting.getServiceName(), meeting, new Integer(count));
				return (IMeetingService)v.elementAt(1) ;
			}

			// no free services available, so create new one
			sName = generateServiceName(MEETINGSERVICE);
			meeting = new MeetingService(sName, this, oDbMgr) ;
			oMeetingServiceCache.put(sName, meeting, new Integer(1));
		}

		return (IMeetingService)meeting;
	}
	
	/**
	 * Look for a free linked file service in the relevant cache, else create a new service, and return it.
	 * @return com.compendium.core.datamodel.services.ILinkedFileService
	 */
	public ILinkedFileService getLinkedFileService() {

		String sName = "";
		LinkedFileService lf = null;
		if(oLinkedFileServiceCache.isEmpty()) {
			sName = generateServiceName(LINKEDFILESERVICE);
			lf = new LinkedFileService(sName, this, oDbMgr) ;
			oLinkedFileServiceCache.put(sName, lf, new Integer(1));
		}
		else {
			// check for a linked file service that can support a new client
			Vector v = oLinkedFileServiceCache.getLowestCount() ;

			if (v != null ) { // implies returned a service
				int count = ((Integer)v.elementAt(2)).intValue() ;
				count++ ;
				lf = (LinkedFileService)v.elementAt(1) ;
				oLinkedFileServiceCache.put(lf.getServiceName(), lf, new Integer(count));
				return (ILinkedFileService)v.elementAt(1) ;
			}

			// no free services available, so create new one
			sName = generateServiceName(LINKEDFILESERVICE);
			lf = new LinkedFileService(sName, this, oDbMgr) ;
			oLinkedFileServiceCache.put(sName, lf, new Integer(1));
		}

		return (ILinkedFileService)lf;
	}
		
	/**
	 * Look for a free movie service in the relevant cache, else create a new service, and return it.
	 * @return com.compendium.core.datamodel.services.IMovieService
	 */
	public IMovieService getMovieService() {

		String sName = "";
		MovieService ms = null;
		if(oMovieServiceCache.isEmpty()) {
			sName = generateServiceName(MOVIESERVICE);
			ms = new MovieService(sName, this, oDbMgr) ;
			oMovieServiceCache.put(sName, ms, new Integer(1));
		}
		else {
			// check for a movie service that can support a new client
			Vector v = oMovieServiceCache.getLowestCount() ;

			if (v != null ) { // implies returned a service
				int count = ((Integer)v.elementAt(2)).intValue() ;
				count++ ;
				ms = (MovieService)v.elementAt(1) ;
				oMovieServiceCache.put(ms.getServiceName(), ms, new Integer(count));
				return (IMovieService)v.elementAt(1) ;
			}

			// no free services available, so create new one
			sName = generateServiceName(LINKEDFILESERVICE);
			ms = new MovieService(sName, this, oDbMgr) ;
			oMovieServiceCache.put(sName, ms, new Integer(1));
		}

		return (IMovieService)ms;
	}

	/**
	 * Clean up the Service instances when the application is closed.
	 *
	 * @param sessionId, the id of the session running when the application was closed.
	 * @param sUserID, the id of the user whose was logged in when the application was closed.
	 */
	public synchronized void cleanupServices(String sessionId, String sUserID) {

		IModel model = (IModel)htModels.get(sessionId);

		try {
			/////////////// remove the assigned view service
			IViewService vs = (IViewService)model.getViewService();
			if (vs != null) {
				oViewServiceCache.remove((IService)vs);
			}

			///////////////// remove the assigned node service
			INodeService ns = (INodeService)model.getNodeService();
			if(ns != null) {
				oNodeServiceCache.remove((IService) ns);
			}

			///////////////// remove the assigned code service
			ICodeService cs = (ICodeService)model.getCodeService();
			if(cs != null) {
				oCodeServiceCache.remove((IService) cs);
			}

			///////////////// remove the assigned link service
			ILinkService ls = (ILinkService)model.getLinkService();
			if(ls != null) {
				oLinkServiceCache.remove((IService)ls);
			}

			///////////////// remove the assigned query service
			IQueryService qs = (IQueryService)model.getQueryService();
			if(qs != null) {
				oQueryServiceCache.remove((IService) qs);
			}

			///////////////// remove the assigned user service
			IUserService us = (IUserService)model.getUserService();
			if(us != null) {
				oUserServiceCache.remove((IService)us);
			}

			///////////////// remove the assigned favorite service
			IFavoriteService fs = (IFavoriteService)model.getFavoriteService();
			if(fs != null) {
				oFavoriteServiceCache.remove((IService)fs);
			}

			///////////////// remove the assigned viewproperty service
			IViewPropertyService vps = (IViewPropertyService)model.getViewPropertyService();
			if(vps != null) {
				oViewPropertyServiceCache.remove((IService)vps);
			}

			///////////////// remove the assigned viewlayer service
			IViewLayerService vls = (IViewLayerService)model.getViewLayerService();
			if(vls != null) {
				oViewLayerServiceCache.remove((IService)vls);
			}

			///////////////// remove the assigned workspace service
			IWorkspaceService wps = (IWorkspaceService)model.getWorkspaceService();
			if(wps != null) {
				oWorkspaceServiceCache.remove((IService)wps);
			}

			///////////////// remove the assigned codegroup service
			ICodeGroupService cgs = (ICodeGroupService)model.getCodeGroupService();
			if(cgs != null) {
				oCodeGroupServiceCache.remove((IService)cgs);
			}

			///////////////// remove the assigned groupcode service
			IGroupCodeService ggs = (IGroupCodeService)model.getGroupCodeService();
			if(ggs != null) {
				oGroupCodeServiceCache.remove((IService)ggs);
			}

			///////////////// remove the assigned system service
			ISystemService sys = (ISystemService)model.getSystemService();
			if(sys != null) {
				oSystemServiceCache.remove((IService)sys);
			}

			///////////////// remove the assigned external connection service
			IExternalConnectionService ecs = (IExternalConnectionService)model.getExternalConnectionService();
			if(ecs != null) {
				oExternalConnectionServiceCache.remove((IService)ecs);
			}

			///////////////// remove the assigned meeting service
			IMeetingService ms = (IMeetingService)model.getMeetingService();
			if(ms != null) {
				oMeetingServiceCache.remove((IService)ms);
			}

			///////////////// remove the assigned linked file service
			ILinkedFileService lf = (ILinkedFileService)model.getLinkedFileService();
			if(lf != null) {
				oLinkedFileServiceCache.remove((IService)lf);
			}

			///////////////// remove the assigned movie service
			IMovieService mvs = (IMovieService)model.getMovieService();
			if(mvs != null) {
				oMovieServiceCache.remove((IService)mvs);
			}

			//CLEANUP THE STORAGE AREA ( Hashtables and Vectors )

			//remove the user from the session
			oUserSessionCache.remove(sUserID, model.getSession().getSessionID());
		}
		catch(Exception ex) {
			ex.printStackTrace();
			System.out.println("Exception trying to clearup services");
		}

		//remove all the dbconnections if the user was the last one 'using' the db connections
		int modelCount = 0;
		for(Enumeration e = htModels.elements();e.hasMoreElements();) {
			Model m = (Model)e.nextElement();
			if(model.getModelName().equals(m.getModelName())) {
				modelCount++;
			}
		}

		//CLOSING ALL CONNECTIONS WAS CAUSING PROBLEMS.
		//AS THIS IS NOW ONLY CALLED WHEN APPLICATION EXITED (not client-server anymore)
		//LET THEM JUST BE GARBAGE COLLECTED
		//if(modelCount < 2) {
		//	oDbMgr.removeAllConnections(model.getModelName());
		//}

		//remove the model from the hashtable
		htModels.remove(model.getSession().getSessionID());

		//print the service status
		//printServicesStatus();
	}

	/**
	 * Convienence Method to get the Model based on the session id
	 * @param sessionId, the is of the session to return the IModel for.
	 * @return com.compendium.core.datamodel.IModel, the model which corresponds tp the given session id.
	 */
	 public IModel getModel(String sessionId) {
		return (IModel)htModels.get(sessionId);
	 }

	 /**
	  * Terminate all Database Connections for the given database model.
	  */
	 public boolean removeAllConnections(String modelName) {
		 return oDbMgr.removeAllConnections(modelName);
	 }
}
