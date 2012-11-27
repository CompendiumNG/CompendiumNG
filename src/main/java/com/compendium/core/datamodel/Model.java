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

package com.compendium.core.datamodel;

import java.util.*;
import java.net.*;
import java.awt.Font;
import java.awt.Color;
import java.util.Hashtable;
import java.sql.SQLException;

import com.compendium.core.ICoreConstants;
import com.compendium.core.datamodel.services.*;

/**
 * Model Class is the base class for the object cache for a given database
 * and also holds a set of the database service objects and project level preference settings.
 * <ul>
 * <li>Compendium data objects
 * <li>Database service objects
 * <li>User Profile object
 * <li>Method to generate Unique IDs based on the timestamp and IP address combination
 * <li>Project level preference settings
 * </ul>
 * @author	Rema and Sajid / Michelle Bachler
 */
public class Model implements java.io.Serializable, IModel {

	/** The property name of the small icon preference property*/
	public static final String	SMALL_ICONS_PROPERTY	="smallIcons";
	
	/** The property name of the hide icon preference property*/
	public static final String 	HIDE_ICONS_PROPERTY 	= "hideIcons";
	
	/** The property name of the show weight indicator preference property*/
	public static final String 	SHOW_WEIGHT_PROPERTY	= "showWeight";
	
	/** The property name of the show tags indicator preference property*/
	public static final String  SHOW_TAGS_PROPERTY		= "showTags";
	
	/** The property name of the show text indicator preference property*/
	public static final String  SHOW_TEXT_PROPERTY		= "showText";
	
	/** The property name of the show transclusion indicator preference property*/
	public static final String  SHOW_TRANS_PROPERTY		= "showTrans";
	
	/** The property name of the wrpa width preference property*/
	public static final String  LABEL_WRAP_WIDTH_PROPERTY = "labelWrapWidth";
	
	/** The property name of the font face preference property*/
	public static final String	FONTFACE_PROPERTY		= "fontface";
	
	/** The property name of the font size preference property*/
	public static final String 	FONTSIZE_PROPERTY		= "fontsize";
	
	/** The property name of the font style preference property*/
	public static final String 	FONTSTYLE_PROPERTY		= "fontstyle";

	/** The property name of the detail popup preference property*/
	public static final String 	DETAIL_POPUP_PROPERTY	= "detailPopup";
	
	/** The property name of the label popup length preference property*/
	public static final String  LABEL_POPUP_LENGTH_PROPERTY	= "labelPopupLength";
	
	/** The property name of the map border preference property*/
	public static final String 	MAP_BORDER_PROPERTY			= "mapBorder";
	
	/** The property name of the Linked Files path property */
	public static final String	LINKED_FILES_PATH_PROPERTY = "linkedFilesPath";
	
	/** The property name of the property for expanding the Linked Files sub-folders */
	public static final String LINKED_FILES_FLAT_PROPERTY = "linkedFilesFlat";
	
	
	// THE DEFAULT PROPERTY SETTINGS
	
	/** The property name of the small icon preference property*/
	public static final boolean		SMALL_ICONS_DEFAULT		= false;
	
	/** The property name of the hide icon preference property*/
	public static final boolean 	HIDE_ICONS_DEFAULT 		= false;
	
	/** The property name of the show weight indicator preference property*/
	public static final boolean 	SHOW_WEIGHT_DEFAULT		= true;
	
	/** The property name of the show tags indicator preference property*/
	public static final boolean  	SHOW_TAGS_DEFAULT		= true;
	
	/** The property name of the show text indicator preference property*/
	public static final boolean  	SHOW_TEXT_DEFAULT		= true;
	
	/** The property name of the show transclusion indicator preference property*/
	public static final boolean		SHOW_TRANS_DEFAULT		= true;
	
	/** The property name of the wrpa width preference property*/
	public static final int		  	LABEL_WRAP_WIDTH_DEFAULT = 25;
	
	/** The property name of the font face preference property*/
	public static final String		FONTFACE_DEFAULT		= "Dialog";
	
	/** The property name of the font size preference property*/
	public static final int		 	FONTSIZE_DEFAULT		= 12;
	
	/** The property name of the font style preference property*/
	public static final int		 	FONTSTYLE_DEFAULT		= Font.PLAIN;

	/** The property name of the detail popup preference property*/
	public static final boolean 	DETAIL_POPUP_DEFAULT	= false;
	
	/** The property name of the label popup length preference property*/
	public static final int  		LABEL_POPUP_LENGTH_DEFAULT	= 100;
	
	/** The property name of the map border preference property*/
	public static final boolean 	MAP_BORDER_DEFAULT			= true;	
	
	/** Default foreground colour*/
	public static final Color		FOREGROUND_DEFAULT			= Color.black;

	/** Default background colour*/
	public static final Color		BACKGROUND_DEFAULT			= Color.white;
	
	/** Default Linked Files folder */
	public static final String		LINKED_FILES_PATH_DEFAULT = "Linked Files";
	
	/** Default value for keeping Linked Files area flattened */
	public static final boolean		LINKED_FILES_FLAT_DEFAULT = false;

	// THE PROJECT PREFERENCE PROPERTIES
	
	/** The map node label wrap width.*/
	public int labelWrapWidth = LABEL_WRAP_WIDTH_DEFAULT;

	/** The map node label length at which to popup the detail box.*/
	public int labelPopupLength = LABEL_POPUP_LENGTH_DEFAULT;
	
	/** The default font face for nodes and lists.*/
	public String fontface = FONTFACE_DEFAULT;

	/** The default font size for nodes and lists.*/
	public int fontsize = FONTSIZE_DEFAULT;

	/** The default font style for nodes and lists.*/
	public int fontstyle = FONTSTYLE_DEFAULT;

	/** Should we use small icons?*/
	public boolean smallIcons = SMALL_ICONS_DEFAULT;

	/** Should node icons be hidden.*/
	public boolean hideIcons = HIDE_ICONS_DEFAULT;

	/** Show node icon weight indicators.*/
	public boolean showWeightNodeIndicator = SHOW_WEIGHT_DEFAULT;

	/** Show node icon text indicators.*/
	public boolean showTextNodeIndicator = SHOW_TEXT_DEFAULT;

	/** Show node icon translcusion indicators.*/
	public boolean showTransNodeIndicator = SHOW_TRANS_DEFAULT;

	/** Show node icon tag inidicators.*/
	public boolean showTagsNodeIndicator = SHOW_TAGS_DEFAULT;
	
	/** Should the detail box be automatically popped up.*/
	public boolean detailPopup = DETAIL_POPUP_DEFAULT;

	/** Should maps with images show borders.*/
	public boolean mapBorder = MAP_BORDER_DEFAULT;
	
	/** Where to store Linked Files */
	public String linkedFilesPath = LINKED_FILES_PATH_DEFAULT;
	
	/** If Linked Files should be in an expanded or flat folder structure */
	public boolean linkedFilesFlat = LINKED_FILES_FLAT_DEFAULT;

	public Font labelFont = new Font(fontface, fontstyle, fontsize);
	
	/** The name is the unique name of this model. It is unique for a single database project.*/
	private String								sName = "";

	/** A reference to the IViewService used by this model.*/
	private IViewService					oViewService = null;

	/** A reference to the INodeService used by this model.*/
	private INodeService					oNodeService = null;

	/** A reference to the ILinkService used by this model.*/
	private ILinkService					oLinkService = null;

	/** A reference to the ICodeService used by this model.*/
	private ICodeService					oCodeService = null;

	/** A reference to the IQueryService used by this model.*/
	private IQueryService					oQueryService = null;

	/** A reference to the IUserService used by this model.*/
	private IUserService					oUserService = null;

	/** A reference to the IFavoriteService used by this model.*/
	private IFavoriteService				oFavoriteService = null;

	/** A reference to the IViewPropertyService used by this model.*/
	private IViewPropertyService			oViewPropertyService = null;

	/** A reference to the IViewLayerService used by this model.*/
	private IViewLayerService				oViewLayerService = null;

	/** A reference to the IWorkspaceService used by this model.*/
	private IWorkspaceService				oWorkspaceService = null;

	/** A reference to the ICodeGroupService used by this model.*/
	private ICodeGroupService				oCodeGroupService = null;

	/** A reference to the IGroupCodeService used by this model.*/
	private IGroupCodeService				oGroupCodeService = null;

	/** A reference to the ISystemService used by this model.*/
	private ISystemService					oSystemService = null;

	/** A reference to the IExternalconnectionService used by this model.*/
	private IExternalConnectionService		oExternalConnectionService = null;

	/** A reference to the IMeetingService used by this model.*/
	private IMeetingService					oMeetingService = null;

	/** A reference to the ILinkedFileService used by this model.*/
	private ILinkedFileService				oLinkedFileService = null;	

	/** A reference to the IMovieService used by this model.*/
	private IMovieService					oMovieService = null;
	
	/** All the user settings are stored in userprofile object.*/
	private UserProfile						oUserProfile = null;

	/** The IP address string format for this computer.*/
	private String							sInetAddress = "";

	/** The Host name of this machine.*/
	private String							sHostName = "";

	/** Stores nodesummary/views objects with their references and reference count.*/
	private NodeCache						oNodeCache = null;

	/** Stores codes and code groups.*/
	private CodeCache						oCodeCache = null;
	
	/** This holds project level preferences.*/
	private Hashtable						htProjectPreferences = null;

	/** The session for this model.*/
	private PCSession						oSession = null;

	/** The previously returned unique identifier.*/
	public static String					oldID = "";

	/** The time (in milliseconds), when this model instance was created.*/
	private Long							creationTime = null;
	
	/** A list of all UserProfiles*/
	private Vector 							vtUsers		= null;

	/** 
	 * Holds an error message, if required, from when the model is being created.
	 */
	public String sErrorMessage = "";
	
	
	/**
	 * Constructor.
	 */
	public Model() {}
	
	/**
	 * Constructor, takes the name of the database this model will use.
	 *
	 * @param String oModelName, the name of the database this model will use.
	 */
	public Model(String sModelName) {

		creationTime = new Long(System.currentTimeMillis());
		sName = sModelName;
		oNodeCache = new NodeCache();
		oCodeCache = new CodeCache();
		htProjectPreferences = new Hashtable();
	}

	/**
	 * Add an error message to the model. Used when model is being created.
	 */
	public void addErrorMessage(String sMessage) {
		sErrorMessage = sMessage;
	}
	
	/**
	 * Return the error message being held, if any.
	 */
	public String getErrorMessage() {
		return sErrorMessage;
	}

	/**
	 * The initialize function is used to initialize various operations related to the model.
	 */
	public void initialize() throws java.net.UnknownHostException, SQLException {

		// get the InetAddress for the particular machine and assign the stripped down String version
		// of the InetAddress in the variable which is used all the time to generate new IDs
		InetAddress netAddress = null;
		sInetAddress = "";
		sHostName		= "";

		try {
			netAddress = InetAddress.getLocalHost() ;
			sHostName = (InetAddress.getLocalHost()).getHostName();
		}
		catch(java.net.UnknownHostException e) {}

		String add = netAddress.getHostAddress() ;
		
		// FOR IPv6
		String sSplitter = ".";
		if (add.indexOf(":") != -1) {
			sSplitter = ":";
		}
		StringTokenizer st = new StringTokenizer(add, sSplitter);
		while(st.hasMoreTokens()) {
			sInetAddress += st.nextToken();
		}
		
		loadProjectPreferences();
		loadUsers();
	}

	/**
	 * Load the project level preferences.
	 * @throws SQLExcpetion
	 */
	public void loadProjectPreferences() throws SQLException {
		htProjectPreferences = getSystemService().getProperties(oSession);	
		
		String sProperty  = "";
		for (Enumeration e = htProjectPreferences.keys(); e.hasMoreElements();) {
			sProperty = (String)e.nextElement();
			
			if (sProperty.equals(SMALL_ICONS_PROPERTY)) {
				smallIcons = new Boolean((String)htProjectPreferences.get(sProperty)).booleanValue();				
			} else if (sProperty.equals(HIDE_ICONS_PROPERTY)) {
				hideIcons = new Boolean((String)htProjectPreferences.get(sProperty)).booleanValue();								
			} else if (sProperty.equals(SHOW_WEIGHT_PROPERTY)) {
				showWeightNodeIndicator = new Boolean((String)htProjectPreferences.get(sProperty)).booleanValue();								
			} else if (sProperty.equals(SHOW_TAGS_PROPERTY)) {
				showTagsNodeIndicator = new Boolean((String)htProjectPreferences.get(sProperty)).booleanValue();								
			} else if (sProperty.equals(SHOW_TEXT_PROPERTY)) {
				showTextNodeIndicator = new Boolean((String)htProjectPreferences.get(sProperty)).booleanValue();								
			} else if (sProperty.equals(SHOW_TRANS_PROPERTY)) {
				showTransNodeIndicator = new Boolean((String)htProjectPreferences.get(sProperty)).booleanValue();							
			} else if (sProperty.equals(LABEL_WRAP_WIDTH_PROPERTY)) {
				try { labelWrapWidth = Integer.valueOf((String)htProjectPreferences.get(sProperty)).intValue(); }
				catch(NumberFormatException nfe) {}				
			} else if (sProperty.equals(FONTFACE_PROPERTY)) {
				fontface = (String)htProjectPreferences.get(sProperty);
			} else if (sProperty.equals(FONTSIZE_PROPERTY)) {
				try { fontsize = Integer.valueOf((String)htProjectPreferences.get(sProperty)).intValue(); }
				catch(NumberFormatException nfe) {}								
			} else if (sProperty.equals(FONTSTYLE_PROPERTY)) {
				try { fontstyle = Integer.valueOf((String)htProjectPreferences.get(sProperty)).intValue(); }
				catch(NumberFormatException nfe) {}				
			} else if (sProperty.equals(DETAIL_POPUP_PROPERTY)) {
				detailPopup = new Boolean((String)htProjectPreferences.get(sProperty)).booleanValue();								
			} else if (sProperty.equals(LABEL_POPUP_LENGTH_PROPERTY)) {
				try { labelPopupLength = Integer.valueOf((String)htProjectPreferences.get(sProperty)).intValue(); }
				catch(NumberFormatException nfe) {}				
			} else if (sProperty.equals(MAP_BORDER_PROPERTY)) {
				mapBorder = new Boolean((String)htProjectPreferences.get(sProperty)).booleanValue();				
			} else if (sProperty.equals(LINKED_FILES_PATH_PROPERTY)) {
				linkedFilesPath = (String)htProjectPreferences.get(sProperty);
			} else if (sProperty.equals(LINKED_FILES_FLAT_PROPERTY)) {
				linkedFilesFlat = new Boolean((String)htProjectPreferences.get(sProperty)).booleanValue();
			}
			
			labelFont = new Font(fontface, fontstyle, fontsize);			
		}
	}
	
	/**
	 * Save all project preferneces to the database.
	 * @return true if all went well.
	 */
	public boolean saveProjectPreferences() throws SQLException {
		return getSystemService().insertProperties(oSession, htProjectPreferences);
	}

	/**
	 * Return the value for the given preference.
	 * @param sPreference the preference property name.
	 * @return
	 */
	/*public String getProjectPreference(String sPreference) {
		String sValue = "";
		if (htProjectPreferences.containsKey(sPreference)) {
			sValue = (String)htProjectPreferences.get(sPreference);
		}
		return sValue;
	}*/
	
	/**
	 * Set the value for the given preference both locally AND in the DATABASE
	 * @param sProperty the preference property name.
	 * @param sValue the preference property value.
	 */
	public void setProjectPreference(String sProperty, String sValue) throws SQLException {
		htProjectPreferences.put(sProperty, sValue);
		
		if (sProperty.equals(SMALL_ICONS_PROPERTY)) {
			smallIcons = new Boolean(sValue).booleanValue();				
		} else if (sProperty.equals(HIDE_ICONS_PROPERTY)) {
			hideIcons = new Boolean(sValue).booleanValue();								
		} else if (sProperty.equals(SHOW_WEIGHT_PROPERTY)) {
			showWeightNodeIndicator = new Boolean(sValue).booleanValue();								
		} else if (sProperty.equals(SHOW_TAGS_PROPERTY)) {
			showTagsNodeIndicator = new Boolean(sValue).booleanValue();								
		} else if (sProperty.equals(SHOW_TEXT_PROPERTY)) {
			showTextNodeIndicator = new Boolean(sValue).booleanValue();								
		} else if (sProperty.equals(SHOW_TRANS_PROPERTY)) {
			showTransNodeIndicator = new Boolean(sValue).booleanValue();
		} else if (sProperty.equals(MAP_BORDER_PROPERTY)) {
			mapBorder = new Boolean(sValue).booleanValue();				
		} else if (sProperty.equals(FONTFACE_PROPERTY)) {
			fontface = sValue;
		} else if (sProperty.equals(FONTSIZE_PROPERTY)) {
			try { fontsize = Integer.valueOf(sValue).intValue(); }
			catch(NumberFormatException nfe) {System.out.println(nfe.getMessage());}								
		} else if (sProperty.equals(FONTSTYLE_PROPERTY)) {
			try { fontstyle = Integer.valueOf(sValue).intValue(); }
			catch(NumberFormatException nfe) {System.out.println(nfe.getMessage());}				
		} else if (sProperty.equals(DETAIL_POPUP_PROPERTY)) {
			detailPopup = new Boolean(sValue).booleanValue();	
		} else if (sProperty.equals(LABEL_WRAP_WIDTH_PROPERTY)) {
			try { labelWrapWidth = Integer.valueOf(sValue).intValue(); }
			catch(NumberFormatException nfe) {System.out.println(nfe.getMessage());}							
		} else if (sProperty.equals(LABEL_POPUP_LENGTH_PROPERTY)) {
			try { labelPopupLength = Integer.valueOf(sValue).intValue(); }
			catch(NumberFormatException nfe) {System.out.println(nfe.getMessage());}			
		} else if (sProperty.equals(LINKED_FILES_PATH_PROPERTY)) {
			linkedFilesPath = sValue;
		} else if (sProperty.equals(LINKED_FILES_FLAT_PROPERTY)) {
			linkedFilesFlat = new Boolean(sValue).booleanValue();
		}
		
		getSystemService().insertProperty(oSession, sProperty, sValue);
	}
	
	/**
	 *	Returns the model name of this model object.
	 *
	 *	@return String, the model name or "".
	 */
	public String getModelName() {
		return sName;
	}
	
	/**
	 * Returns the linkedFilesPath value
	 * 
	 * @return String, the value of the linkedFilesPath setting
	 */
	public String getlinkedFilesPath() {
		return linkedFilesPath;
	}
	
	/**
	 * Returns the linkedFilesFlat value. 
	 * True, means only use the base LinkedFiles path.
	 * False, means use the project/user subfolders
	 * 
	 * @return boolean, the value of the linkedFilesFlat setting
	 */
	public boolean getlinkedFilesFlat() {
		return linkedFilesFlat;
	}

	/**
	 * Set the session id for this model.
	 *
	 * @param PCSession session, the session id for this model.
	 */
	public void setSession(PCSession session) {
		oSession =  session;
	}

	/**
	 * Get the session id for this model.
	 *
	 * @return PCSession, the session id for this model.
	 */
	public PCSession getSession() {
		return oSession;
	}

	/**
	 * Get the creation time for this model, in milliseconds.
	 *
	 * @return Long, the creation time for this model, in milliseconds.
	 */
	public Long getCreationTime() {
		return creationTime;
	}

	/**
	 * Get the IP Address for this machine.
	 *
	 * @return String, the IPAddress for this machine.
	 */
	public String getMyIP() {
		return sInetAddress;
	}

	/**
	 * Get the host name for this machine.
	 *
	 * @return String, the host name for this machine.
	 */
	public String getMyName() {
		return sHostName;
	}

	/**
	 * The model object generates globally unique Ids for all objects CREATED by this application.
	 */
	public synchronized String getUniqueID() {

		long timestamp = System.currentTimeMillis() ;
		String stamp = (new Long(timestamp)).toString() ;
		String newID = sInetAddress + stamp;

		//	Generating timestamps using System.currentTimeMilli gives us timestamps withing milliseconds
		//	and for fast operations performed (CPU) which are in micro/nanoseconds will result in same
		//	timestamps!
		while(newID.equals(oldID)) {
			timestamp = System.currentTimeMillis() ;
			stamp = (new Long(timestamp)).toString() ;
			newID = sInetAddress + stamp;
		}
		oldID = newID;

		return newID;
	}

	/**
	 * The model object generates globally unique Ids for all objects CREATED by this application,
	 * when model not instantiated
	 */
	public static String getStaticUniqueID() {

		InetAddress netAddress = null;
		String sInetAddress = "";

		try {
			netAddress = InetAddress.getLocalHost() ;
		}
		catch(java.net.UnknownHostException e) {}

		String add = netAddress.getHostAddress();		
		// FOR IPv6
		String sSplitter = ".";
		if (add.indexOf(":") != -1) {
			sSplitter = ":";
		}				
		StringTokenizer st = new StringTokenizer(add,sSplitter);
		while(st.hasMoreTokens()) {
			sInetAddress += st.nextToken();
		}

		long timestamp = System.currentTimeMillis() ;
		String stamp = (new Long(timestamp)).toString() ;
		String newID = sInetAddress + stamp;

		while(newID.equals(oldID)) {
			timestamp = System.currentTimeMillis() ;
			stamp = (new Long(timestamp)).toString() ;
			newID = sInetAddress + stamp;
		}
		oldID = newID;

		return newID;
	}

// THE SERVICES

	/**
	 *	Sets the view service.
	 * 	This method can only be used by the ServiceManager when creating the model object initially.
	 *
	 *	@param VS, the IViewService reference.
	 */
	public void setViewService(IViewService VS) {
		if (oViewService == VS)
			return;
		oViewService = VS;
	}

	/**
	 *	Returns the view service.
	 *
	 *	@return reference if set, null otherwise.
	 */
	public IViewService getViewService() {
		return oViewService;
	}

	/**
	 *	Sets the node service.
	 * 	This method can only be used by the ServiceManager when creating the model object initially.
	 *
	 *	@param NS, the INodeService reference.
	 */
	public void setNodeService(INodeService NS) {
		oNodeService = NS;
	}

	/**
	 *	Returns the node service.
	 *
	 *	@return reference if set, null otherwise.
	 */
	public INodeService getNodeService() {
		return oNodeService;
	}

	/**
	 *	Sets the link service.
	 * 	This method can only be used by the ServiceManager when creating the model object initially.
	 *
	 *	@param LS, the ILinkService reference.
	 */
	public void setLinkService(ILinkService LS) {
		oLinkService = LS;
	}

	/**
	 *	Returns the link service.
	 *
	 *	@return reference if set, null otherwise.
	 */
	public ILinkService getLinkService() {
		return oLinkService;
	}

	/**
	 *	Sets the code service.
	 * 	This method can only be used by the ServiceManager when creating the model object initially.
	 *
	 *	@param CS, the ICodeService reference.
	 */
	public void setCodeService(ICodeService CS) {
		oCodeService = CS;
	}

	/**
	 *	Returns the code service.
	 *
	 *	@return reference if set, null otherwise.
	 */
	public ICodeService getCodeService() {
		return oCodeService;
	}

	/**
	 *	Sets the query service.
	 * 	This method can only be used by the ServiceManager when creating the model object initially.
	 *
	 *	@param QS, the IQueryService reference.
	 */
	public void setQueryService(IQueryService QS) {
		oQueryService = QS;
	}

	/**
	 *	Returns the query service.
	 *
	 *	@return reference if set, null otherwise.
	 */
	public IQueryService getQueryService() {
		return oQueryService;
	}

	/**
	 *	Sets the User service.
	 * 	This method can only be used by the ServiceManager when creating the model object initially.
	 *
	 *	@param US, the IUserService reference.
	 */
	public void setUserService(IUserService US) {
		oUserService = US;
	}

	/**
	 *	Returns the user service.
	 *
	 *	@return reference if set, null otherwise.
	 */
	public IUserService getUserService() {
		return oUserService;
	}

	/**
	 *	Sets the favorite service.
	 * 	This method can only be used by the ServiceManager when creating the model object initially.
	 *
	 *	@param FS, the IFavoriteService reference.
	 */
	public void setFavoriteService(IFavoriteService FS) {
		oFavoriteService = FS;
	}

	/**
	 *	Returns the favorite service.
	 *
	 *	@return reference if set, null otherwise.
	 */
	public IFavoriteService getFavoriteService() {
		return oFavoriteService;
	}

	/**
	 *	Sets the view property service.
	 * 	This method can only be used by the ServiceManager when creating the model object initially.
	 *
	 *	@param VS, the IViewPropertyService reference.
	 */
	public void setViewPropertyService(IViewPropertyService VS) {
		oViewPropertyService = VS;
	}

	/**
	 *	Returns the view property service.
	 *
	 *	@return reference if set, null otherwise.
	 */
	public IViewPropertyService getViewPropertyService() {
		return oViewPropertyService;
	}

	/**
	 *	Sets the view layer service.
	 * 	This method can only be used by the ServiceManager when creating the model object initially.
	 *
	 *	@param VS, the IViewLayerService reference.
	 */
	public void setViewLayerService(IViewLayerService VS) {
		oViewLayerService = VS;
	}

	/**
	 *	Returns the view layer service.
	 *
	 *	@return reference if set, null otherwise.
	 */
	public IViewLayerService getViewLayerService() {
		return oViewLayerService;
	}

	/**
	 *	Sets the workspace service.
	 * 	This method can only be used by the ServiceManager when creating the model object initially.
	 *
	 *	@param WS, the IWorkspaceService reference.
	 */
	public void setWorkspaceService(IWorkspaceService WS) {
		oWorkspaceService = WS;
	}

	/**
	 *	Returns the workspace service.
	 *
	 *	@return reference if set, null otherwise.
	 */
	public IWorkspaceService getWorkspaceService() {
		return oWorkspaceService;
	}

	/**
	 *	Sets the code group service.
	 * 	This method can only be used by the ServiceManager when creating the model object initially.
	 *
	 *	@param CS, the ICodeGroupService reference.
	 */
	public void setCodeGroupService(ICodeGroupService CS) {
		oCodeGroupService = CS;
	}

	/**
	 *	Returns the code group service.
	 *
	 *	@return reference if set, null otherwise.
	 */
	public ICodeGroupService getCodeGroupService() {
		return oCodeGroupService;
	}

	/**
	 *	Sets the group code service.
	 * 	This method can only be used by the ServiceManager when creating the model object initially.
	 *
	 *	@param GS, the IGroupCodeService reference.
	 */
	public void setGroupCodeService(IGroupCodeService GS) {
		oGroupCodeService = GS;
	}

	/**
	 *	Returns the group code service.
	 *
	 *	@return reference if set, null otherwise.
	 */
	public IGroupCodeService getGroupCodeService() {
		return oGroupCodeService;
	}

	/**
	 *	Sets the system service.
	 * 	This method can only be used by the ServiceManager when creating the model object initially.
	 *
	 *	@param SS, the ISystemService reference.
	 */
	public void setSystemService(ISystemService SS) {
		oSystemService = SS;
	}

	/**
	 *	Returns the system service.
	 *
	 *	@return reference if set, null otherwise.
	 */
	public ISystemService getSystemService() {
		return oSystemService;
	}

	/**
	 *	Sets the external connection service.
	 * 	This method can only be used by the ServiceManager when creating the model object initially.
	 *
	 *	@param ES, the IExternalConnectionService reference.
	 */
	public void setExternalConnectionService(IExternalConnectionService ES) {
		oExternalConnectionService = ES;
	}

	/**
	 *	Returns the external connection service.
	 *
	 *	@return reference if set, null otherwise.
	 */
	public IExternalConnectionService getExternalConnectionService() {
		return oExternalConnectionService;
	}

	/**
	 *	Sets the meeting connection service.
	 * 	This method can only be used by the ServiceManager when creating the model object initially.
	 *
	 *	@param MS, the IMeetingService reference.
	 */
	public void setMeetingService(IMeetingService MS) {
		oMeetingService = MS;
	}

	/**
	 *	Returns the meeting service.
	 *
	 *	@return reference if set, null otherwise.
	 */
	public IMeetingService getMeetingService() {
		return oMeetingService;
	}
	
	/**
	 *	Returns the linked file service.
	 *  @author Sebastian Ehrich 
	 *  @return a reference to a LinkedFileService
	 */
	public ILinkedFileService getLinkedFileService() {
		return oLinkedFileService;
	}
	
	/**
	 *	Sets the linked file service.
	 * 	This method can only be used by the ServiceManager when creating the model object initially.
	 *  @author Sebastian Ehrich 
	 *  @param lfs A reference to a ILinkedFileService	
	 */
	public void setLinkedFileService(ILinkedFileService lfs) {
		oLinkedFileService = lfs;
	}
	
	/**
	 *	Returns the movie service.
	 *  @return a reference to a LinkedFileService
	 */
	public IMovieService getMovieService() {
		return oMovieService;
	}
	
	/**
	 *	Sets the movie service.
	 *  @param lfs A reference to a IMovieService	
	 */
	public void setMovieService(IMovieService ms) {
		oMovieService = ms;
	}

// USER SETTINGS

	/**
	 * Get the list of all users home view and inbox ids
	 * @return Hashtable of all user home and link ids as the keys and user names as the values.
	 */
	public Hashtable getUserViews() {
		
		int count = vtUsers.size();
		Hashtable htIDs = new Hashtable( (count*2) );
		
		UserProfile up = null;
		View home = null;
		View inbox = null;
		for (int i=0; i<count; i++) {
			up = (UserProfile)vtUsers.elementAt(i);
			home = up.getHomeView();
			if (home != null) {
				htIDs.put(home.getId(), up.getUserName());
			}
			inbox = up.getLinkView();
			if (inbox != null) {
				htIDs.put(inbox.getId(), up.getUserName());
			}			
		}

		return htIDs;
	}

	/**
	 * Get the list of all users (UserProfile)
	 * @return Vector of all users
	 */
	public Vector getUsers() {
		return vtUsers;
	}
	
	/**
	 * Remove a specific User Profile from the vtUsers list.  This gets called when 
	 * a user ID gets deleted via the UIUserManagerDialog
	 * 
	 * @param String sUserID - User ID of the person being removed
	 */
	public void removeUserProfile(String sUserID) {
		int count = vtUsers.size();
				
		UserProfile up = null;
		for (int i=0; i<count; i++) {
			up = (UserProfile)vtUsers.elementAt(i);
			if (up.getUserID().equals(sUserID)) {
				vtUsers.removeElementAt(i);
				return;
			}
		}
	}
	
	/** 
	 * Updates info for the given User in the in-memory UserProfile cache
	 * 
	 * @param UserProfile upNew - The UserProfile to update (or add) to the local cache
	 */
	public void updateUserProfile(UserProfile upNew) {
		
		int count = vtUsers.size();
		boolean	upFound = false;
		
		UserProfile up = null;
		for (int i=0; i<count; i++) {
			up = (UserProfile)vtUsers.elementAt(i);
			if (up.getUserID().equals(upNew.getUserID())) {
				upFound = true;
				vtUsers.removeElementAt(i);
				vtUsers.insertElementAt(upNew, i);
				break;
			}
		}
		if (!upFound) {
			vtUsers.addElement(upNew);
		}
	}
	
	
	/**
	 * Load the list of all users.
	 * 
	 * In 1.5.2 this reloaded user info from the database each time (no 'if' clause).  To optimize
	 * performance, this was changed to only load from the database once.  The UIUserManagerDialog
	 * code that adds, deleted and modifies users was changed to modify the data in the vtUser vector
	 * so this always contains a correct user list.
	 * 
	 */
	public void loadUsers() throws SQLException {
		if (vtUsers == null) {
			vtUsers = getUserService().getUsers(sName, oUserProfile.getId());
		}
	}
	
	/**
	 *	Returns the user profile object associated with this model.
	 *
	 *	@return UserProfile, the object or null.
	 */
	public UserProfile getUserProfile() {
		return oUserProfile;
	}

	/**
	 *	Sets the user profile object associated with this model.
	 *
	 *	@param UserProfile up, The user profile object.
	 */
	public void setUserProfile(UserProfile up) {

		if (oUserProfile == up)
			return ;

		//UserProfile oldValue = oUserProfile ;
		oUserProfile = up;
	}


// NODE CACHE METHODS

// NODE CACHE IS NOT USED AT PRESENT

	/**
	 *	Adds a PCObject to the node cache.
	 *
	 *	@param PCObject object, the object to add.
	 *	@return boolean, true if the object was successfully added, else false.
	 */
	//public boolean addObject(PCObject object) {
	//
	//	return oNodeCache.put(object);
	//}

	/**
	 *	Removes a PCObject from the node cache.
	 *
	 *	@param PCObject object, the object to remove.
	 *	@return boolean, true if the object was successfully removed, else false.
	 */
	//public boolean removeObject(PCObject object) {
	//
	//	return oNodeCache.remove(object);
	//}

	/**
	 *	Returns the number of references to the object in the node cache.
	 *
	 * 	@param PCObject object, the object to count the references for.
	 * 	@return int, the number of references to the object in the node cache.
	 */
	//public int referencesToObject(PCObject object) {
	//
	//	return oNodeCache.getCount(object);
	//}

	/**
	 * Returns a list of Views in the node cache.
	 *
	 * @return Enumeration, a list of Views in the node cache.
	 */
	//public Enumeration getViews() {
	//
	//	return oNodeCache.getViews();
	//}

	/**
	 * Returns a INodeSummary object from the cache with the given id.
	 *
	 * @param String sID, the id of the node to return from the cache.
	 * @return NodeSummary, the node from the cache with the given id, else null.
	 */
	//public NodeSummary getNodeSummary(String sID) {
	//
	//	return oNodeCache.getNode(sID);
	//}

	/**
	 *	This method is to be called by links to get the model's from and to nodeSummary
	 *	to update themselves for property change events for from and to node changes
	 *	the method takes the id of the input nodeSummary and checks it against its own
	 *	hashtable and then returns the nodeSummary with that id. if it does not exist
	 *	it throws a NoSuchElement exception.
	 *
	 *	@param NodeSummary node, the node to get the cached node version of.
	 *	@param NodeSummary, the cached version of the node given.
	 */
	//public NodeSummary getNodeSummary(NodeSummary node) throws NoSuchElementException {
	//
	//	return oNodeCache.getNode(node);
	//}

	/**
	 *This method returns a View reference from the node cache, if a node with that id exitis in the cache.
	 *
	 * @param String sID, the id of the view to return from the cache.
	 * @return View, the view from the cache with the given id, else null.
	 */
	//public View getView(String sID) {
	//
	//	return oNodeCache.getView(sID);
	//}

	/**
	 *	This method updates the node cache for the NodeSummary with the appropriate action:
	 *	updating the reference count or by adding a new entry in the cache.
	 *
	 * 	@param NodeSummary[] nodes, the nodes to update in the cache.
	 *	@return NodeSummary[], the updated nodes.
	 */
	//public NodeSummary[] addNodeSummaries(NodeSummary[] nodes) {
	//
	//	return oNodeCache.addNodes(nodes);
	//}

	/**
	 *	This method takes in a Node Summary and adds it to the node cache.
	 *	and returns a reference to the object.
	 *
	 *	@param NodeSummary node, the node to add to the cache.
	 *	@param NodeSummary, the node added to the cache, or the cached version if already there.
	 */
	//public NodeSummary addNodeSummary(NodeSummary node) {

	//	return oNodeCache.addNode(node);
	//}

// CODE CACHE METHODS

// CODES

	/**
	 * Checks if a code with the passed name already exists in the data 
	 * and does not have the the given code id.
	 * @param sCodeID the code id to ignore
	 * @param sName the code name to check.
	 * @return true if a duplicate named code exists, else false
	 */
	public boolean codeNameExists(String sCodeID, String sName) {
		return oCodeCache.codeNameExists(sCodeID, sName);
	}		
	
	/**
	 *  Load all the codes for the current project into the code cache.
	 */
	public void loadAllCodes() throws Exception {
		Vector vtCodes = getCodeService().getCodes(getSession());
		oCodeCache.initializeCodeCache(vtCodes);
	}

	/**
	 *	Returns a Code object with the given CodeID.
	 *
	 * 	@param String sCodeID, the id of the code to return from the cache.
	 * 	@return Code, the code with the given code id in the cache, else null.
	 */
	public Code getCode(String sCodeID) {
		return oCodeCache.getCode(sCodeID);
	}

	/**
	 *	Get all the codes in the code cache as a Hashtable.
	 */
	public Hashtable getCodesCheck() {
		return oCodeCache.getCodesCheck();
	}

	/**
	 *	Get all the codes in the code cache as an Enumeration
	 */
	public Enumeration getCodes() {
		return oCodeCache.getCodes();
	}

	/**
	 *	Add a code to the code cache.
	 *
	 *	@param Code code, the code to add to the code cache.
	 *	@return boolean, true if the code was added to the cache, else false.
	 */
	public boolean addCode(Code code) {
		return oCodeCache.addCode(code);
	}

	/**
	 *	Remove a code from the code cache.
	 *
	 *	@param Code code, the code to remove from the code cache.
	 */
	public void removeCode(Code code) {
		oCodeCache.removeCode(code);
	}

	/**
	 *	Replace a code in the code cache with the given code, where the code id's match.
	 *
	 *	@param Code code, the code which to replace in the cache.
	 */
	public void replaceCode(Code code) {
		oCodeCache.replaceCode(code);
	}

// CODE GROUPS

	/**
	 *  Load a set of all code groups for the current project
	 *  from the database and store in the code cache.
	 */
	public void loadAllCodeGroups() throws Exception {

		PCSession session = getSession();

		// Get the codegroups available in the DB first
		Vector vtCodeGroups = getCodeGroupService().getCodeGroups(session);

		for(Enumeration e = vtCodeGroups.elements();e.hasMoreElements();) {
			Vector group = (Vector)e.nextElement();
			String sCodeGroupID = (String)group.elementAt(0);
			oCodeCache.addCodeGroup(sCodeGroupID, group);

			if (!sCodeGroupID.equals("")) {
				Vector codes = getGroupCodeService().getGroupCodes(session, sCodeGroupID);
				int jcount = codes.size();
				for (int j=0; j<jcount; j++) {
					Code code = (Code)codes.elementAt(j);
					oCodeCache.addCodeGroupCode(sCodeGroupID, code.getId(), code);
				}
			}
		}

		// Initialise Ungrouped codes hashtable
		Vector vtUngroupedCodes = getGroupCodeService().getUngroupedCodes(session);
		oCodeCache.addUngroupedCodes(vtUngroupedCodes);
	}

	/**
	 * Returns the Hashtable of information for the code group with the given id.
	 * Currently the hastable contains two element keys:
	 * 'children' is mapped to a Hastable of all Code object in the code group (codeid, code).
	 * 'group' is mapped to a Vector of code group information: 0=CodeGroupID, 1=Name.
	 *
	 *	@param String sCodeGroupID, the id of the code group to get from the code cache.
	 *	@return Hashtable, containing the code group information if found, else empty.
	 */
	public Hashtable getCodeGroup(String sCodeGroupID) {
		return oCodeCache.getCodeGroup(sCodeGroupID);
	}

	/**
	 *	Return all the ungrouped codes in the code cache.
	 *
	 *	@return Hashtable, all the ungrouped codes in the code cache.
	 */
	public Hashtable getUngroupedCodes() {
		return oCodeCache.getUngroupedCodes();
	}

	/**
	 *	Gets all the codesgroups from the code cache.
	 *
	 *	@return Hashtable, containing hashtables of information about all the code groups in the code cache.
	 * Currently the hastable contains two element keys:
	 * 'children' is mapped to a Hashtable of all Code object in the code group (codeid, code).
	 * 'group' is mapped to a Vector of code group information: 0=CodeGroupID, 1=Name.
	 */
	public Hashtable getCodeGroups() {
		return oCodeCache.getCodeGroups();
	}

	/**
	 * This method adds in a code group to the cache.
	 *
	 * @param String sCodeGroupID, the id of the code group to add to the cache.
	 * @param Vector vtGroup, the Vector of information about the code group.
	 * Currently the elements in the Vector are: 0=CodeGroupID, 1=Name
	 */
	public void addCodeGroup(String sCodeGroupID, Vector vtGroup) {
		oCodeCache.addCodeGroup(sCodeGroupID, vtGroup);
	}

	/**
	 * This method replace a codegroup name for the given codegroup id in the cache.
	 *
	 * @param String sCodeGroupID, the id of the code group whose name to replace.
	 * @param String sName, the new name of the code group.
	 */
	public void replaceCodeGroupName(String sCodeGroupID, String sName) {
		oCodeCache.replaceCodeGroupName(sCodeGroupID, sName);
	}

	/**
	 * This method adds in a code into code group in the cache.
	 *
	 * @param String sCodeGroupID, the id of the code group to add the code to
	 * @param String sCodeID, the id of the code to add.
	 * @param Code code, the Code object to add to the code group with the given code group id.
	 */
	public void addCodeGroupCode(String sCodeGroupID, String sCodeID, Code code) {
		oCodeCache.addCodeGroupCode(sCodeGroupID, sCodeID, code);
	}

	/**
	 * This method removes a code group with the given id from the cache.
	 *
	 * @param String sCodeGroupID, the id of the cide group to remove from the cache.
	 */
	public void removeCodeGroup(String sCodeGroupID) {

		oCodeCache.removeCodeGroup(sCodeGroupID);
	}

	/**
	 * This method removes a code from a certain code group in the cache.
	 *
	 * @param String sCodeGroupID, the id of the code group to remove the code from.
	 * @param String sCodeID, the id of the code to remove from the code group.
	 */
	public void removeCodeGroupCode(String sCodeGroupID, String sCodeID) {

		oCodeCache.removeCodeGroupCode(sCodeGroupID, sCodeID);
	}

	/**
	 * Help to clear up variables used by this object to assist with garbage collection.
	 */
	public void cleanUp() {

		oViewService = null;
		oNodeService = null;
		oLinkService = null;
		oCodeService = null;
		oQueryService = null;
		oUserService = null;
		oFavoriteService = null;
		oViewPropertyService = null;
		oWorkspaceService = null;
		oCodeGroupService = null;
		oGroupCodeService = null;
		oSystemService = null;
		oUserProfile = null;

		if (oNodeCache != null) {
			oNodeCache.cleanUp();
		}
		if (oCodeCache != null) {
			oCodeCache.cleanUp();
		}

		sHostName 		= null;
		sInetAddress 	= null;
		sName			= null;
		oSession 		= null;
		oldID 			= null;
	}
}
