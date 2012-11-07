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

import java.sql.SQLException;
import java.util.*;

import com.compendium.core.datamodel.services.*;

/**
 * IModel Class is the interface to the Model class which is the base class for the object cache for a given database
 * and also holds a set of the database service objects.
 * <ul>
 * <li>Compendium data objects
 * <li>Database service objects
 * <li>User Profile object
 * <li>Method to generate Unique IDs based on the timestamp and IP address combination
 * </ul>
 * @author	Rema and Sajid / Michelle Bachler
 */
public interface IModel {

	/**
	 * Add an error message to the model. Used when model is being created.
	 */
	public void addErrorMessage(String sMessage);
	
	/**
	 * Return the error message
	 */
	public String getErrorMessage();
	
	/**
	 * The initialize function is used to initialize various operations related to the model.
	 */
	public void initialize() throws java.net.UnknownHostException, SQLException;

	/**
	 * Load the project level preferences.
	 * @throws SQLExcpetion
	 */
	public void loadProjectPreferences() throws SQLException;
	
	/**
	 * Save all project preferneces to the database.
	 * @return true if all went well.
	 */
	public boolean saveProjectPreferences() throws SQLException;

	/**
	 * Return the value for the given preference.
	 * @param sPreference the preference property name.
	 * @return
	 */
	//public String getProjectPreference(String sPreference);
	
	/**
	 * Set the value for the given preference both locally AND in the DATABASE
	 * @param sPreference the preference property name.
	 * @param sValue the preference property value.
	 */
	public void setProjectPreference(String sPreference, String sValue) throws SQLException;
	
	/**
	 *	Returns the model name of this model object.
	 *
	 *	@return String, the model name or "".
	 */
	public String getModelName();

	/**
	 * Returns the linkedFilesPath value
	 * 
	 * @return String, the value of the linkedFilesPath setting
	 */
	public String getlinkedFilesPath();
	
	/**
	 * Returns the linkedFilesFlat value
	 * 
	 * @return boolean, the value of the linkedFilesFlat setting
	 */
	public boolean getlinkedFilesFlat();
	
	/**
	 * Set the session id for this model.
	 *
	 * @param PCSession session, the session id for this model.
	 */
	public void setSession(PCSession session);

	/**
	 * Get the session id for this model.
	 *
	 * @return PCSession, the session id for this model.
	 */
	public PCSession getSession();

	/**
	 * Get the IP Address for this machine.
	 *
	 * @return String, the IPAddress for this machine.
	 */
	public String getMyIP();

	/**
	 * Get the host name for this machine.
	 *
	 * @return String, the host name for this machine.
	 */
	public String getMyName();

	/**
	 * The model object generates globally unique Ids for all objects CREATED by this application.
	 */
	public String getUniqueID();

	/**
	 *	Sets the view service.
	 * 	This method can only be used by the ServiceManager when creating the model object initially.
	 *
	 *	@param VS, the IViewService reference.
	 */
	public void setViewService(IViewService VS);

	/**
	 *	Returns the view service.
	 *
	 *	@return reference if set, null otherwise.
	 */
	public IViewService getViewService();

	/**
	 *	Sets the node service.
	 * 	This method can only be used by the ServiceManager when creating the model object initially.
	 *
	 *	@param NS, the INodeService reference.
	 */
	public void setNodeService(INodeService NS);

	/**
	 *	Returns the node service.
	 *
	 *	@return reference if set, null otherwise.
	 */
	public INodeService getNodeService();

	/**
	 *	Sets the link service.
	 * 	This method can only be used by the ServiceManager when creating the model object initially.
	 *
	 *	@param LS, the ILinkService reference.
	 */
	public void setLinkService(ILinkService LS);

	/**
	 *	Returns the link service.
	 *
	 *	@return reference if set, null otherwise.
	 */
	public ILinkService getLinkService();

	/**
	 *	Sets the Code service.
	 * 	This method can only be used by the ServiceManager when creating the model object initially.
	 *
	 *	@param CS, the ICodeService reference.
	 */
	public void setCodeService(ICodeService CS);

	/**
	 *	Returns the code service.
	 *
	 *	@return reference if set, null otherwise.
	 */
	public ICodeService getCodeService();

	/**
	 *	Sets the query service.
	 * 	This method can only be used by the ServiceManager when creating the model object initially.
	 *
	 *	@param QS, the IQueryService reference.
	 */
	public void setQueryService(IQueryService QS);

	/**
	 *	Returns the query service.
	 *
	 *	@return reference if set, null otherwise.
	 */
	public IQueryService getQueryService();

	/**
	 *	Sets the user service.
	 * 	This method can only be used by the ServiceManager when creating the model object initially.
	 *
	 *	@param US, the IUserService reference.
	 */
	public void setUserService(IUserService US);

	/**
	 *	Returns the user service.
	 *
	 *	@return reference if set, null otherwise.
	 */
	public IUserService getUserService();

	/**
	 *	Sets the Favorite service.
	 * 	This method can only be used by the ServiceManager when creating the model object initially.
	 *
	 *	@param FS, the IFavoriteService reference.
	 */
	public void setFavoriteService(IFavoriteService FS);

	/**
	 *	Returns the favorite service.
	 *
	 *	@return reference if set, null otherwise.
	 */
	public IFavoriteService getFavoriteService();

	/**
	 *	Sets the view property code service.
	 * 	This method can only be used by the ServiceManager when creating the model object initially.
	 *
	 *	@param VS, the IViewProperyService reference.
	 */
	public void setViewPropertyService(IViewPropertyService VS);

	/**
	 *	Returns the view property service.
	 *
	 *	@return reference if set, null otherwise.
	 */
	public IViewPropertyService getViewPropertyService();

	/**
	 *	Sets the view layer service.
	 * 	This method can only be used by the ServiceManager when creating the model object initially.
	 *
	 *	@param VS, the IViewLayerService reference.
	 */
	public void setViewLayerService(IViewLayerService VS);

	/**
	 *	Returns the view layer service.
	 *
	 *	@return reference if set, null otherwise.
	 */
	public IViewLayerService getViewLayerService();

	/**
	 *	Sets the workspace service.
	 * 	This method can only be used by the ServiceManager when creating the model object initially.
	 *
	 *	@param WS, the IWorkspaceService reference.
	 */
	public void setWorkspaceService(IWorkspaceService WS);

	/**
	 *	Returns the workspace service.
	 *
	 *	@return reference if set, null otherwise.
	 */
	public IWorkspaceService getWorkspaceService();

	/**
	 *	Sets the code group service.
	 * 	This method can only be used by the ServiceManager when creating the model object initially.
	 *
	 *	@param CS, the ICodeGroupService reference.
	 */
	public void setCodeGroupService(ICodeGroupService CS);

	/**
	 *	Returns the code group service.
	 *
	 *	@return reference if set, null otherwise.
	 */
	public ICodeGroupService getCodeGroupService();

	/**
	 *	Sets the group code service.
	 * 	This method can only be used by the ServiceManager when creating the model object initially.
	 *
	 *	@param GS, the IGroupCodeService reference.
	 */
	public void setGroupCodeService(IGroupCodeService GS);

	/**
	 *	Returns the group code service.
	 *
	 *	@return reference if set, null otherwise.
	 */
	public IGroupCodeService getGroupCodeService();

	/**
	 *	Sets the system service.
	 * 	This method can only be used by the ServiceManager when creating the model object initially.
	 *
	 *	@param SS, the ISystemService reference.
	 */
	public void setSystemService(ISystemService GS);

	/**
	 *	Returns the system service.
	 *
	 *	@return reference if set, null otherwise.
	 */
	public ISystemService getSystemService();

	/**
	 *	Sets the external connection service.
	 * 	This method can only be used by the ServiceManager when creating the model object initially.
	 *
	 *	@param ES, the IExternalConnectionService reference.
	 */
	public void setExternalConnectionService(IExternalConnectionService ES);

	/**
	 *	Returns the external connection service.
	 *
	 *	@return reference if set, null otherwise.
	 */
	public IExternalConnectionService getExternalConnectionService();

	/**
	 *	Sets the meeting connection service.
	 * 	This method can only be used by the ServiceManager when creating the model object initially.
	 *
	 *	@param MS, the IMeetingService reference.
	 */
	public void setMeetingService(IMeetingService MS);

	/**
	 *	Returns the meeting service.
	 *
	 *	@return reference if set, null otherwise.
	 */
	public IMeetingService getMeetingService();

	/**
	 *	Sets the linked file service.
	 * 	This method can only be used by the ServiceManager when creating the model object initially.
	 *  @author Sebastian Ehrich 
	 *	@param lfs the ILinkedFileService reference.
	 */
	public void setLinkedFileService(ILinkedFileService lfs);

	/**
	 *	Returns the linked file service.
	 *	@author Sebastian Ehrich 
	 *	@return reference if set, null otherwise.
	 */
	public ILinkedFileService getLinkedFileService();

	/**
	 *	Sets the movie service.
	 * 	This method can only be used by the ServiceManager when creating the model object initially.
	 *	@param lfs the IMovieService reference.
	 */
	public void setMovieService(IMovieService ms);

	/**
	 *	Returns the movie service.
	 *	@return reference if set, null otherwise.
	 */
	public IMovieService getMovieService();

	/**
	 *	Returns the user profile object associated with this model.
	 *
	 *	@return UserProfile, the object or null.
	 */
	public UserProfile getUserProfile();

	/**
	 *	Sets the user profile object associated with this model.
	 *
	 *	@param UserProfile up, The user profile object.
	 */
	public void setUserProfile(UserProfile up);

	/**
	 * Get the list of all users (UserProfile)
	 * @return Vector of all users
	 */
	public Vector getUsers();
	
	/**
	 * Load the list of all users.
	 */
	public void loadUsers() throws SQLException;	
	
	/**
	 * Get the list of all users home view and inbox ids
	 * @return Hashtable of all user home and link ids as the keys and user names as the values.
	 */
	public Hashtable getUserViews();
	
	/**
	 * Remove a specific User Profile from the vtUsers list.  This gets called when 
	 * a user ID gets deleted via the UIUserManagerDialog
	 * 
	 * @param String sUserID - UserID of the person being removed
	 */
	public void removeUserProfile(String sUserID);
	
	
	/** 
	 * Updates info for the given User in the in-memory UserProfile cache
	 * 
	 * @param UserProfile up - tehUserProfile to update (or add)
	 */
	public void updateUserProfile(UserProfile up);

	
// NODES - NODE CACHE NOT USED AT PRESENT


	/**
	 *	Adds a PCObject to the node cache.
	 *
	 *	@param PCObject object, the object to add.
	 *	@return boolean, true if the object was successfully added, else false.
	 */
	//public boolean addObject(PCObject object);

	/**
	 *	Removes a PCObject from the node cache.
	 *
	 *	@param PCObject object, the object to remove.
	 *	@return boolean, true if the object was successfully removed, else false.
	 */
	//public boolean removeObject(PCObject object);

	/**
	 *	Returns the number of references to the object in the node cache.
	 *
	 * 	@param PCObject object, the object to count the references for.
	 * 	@return int, the number of references to the object in the node cache.
	 */
	//public int referencesToObject(PCObject object);

	/**
	 * Returns a list of Views in the node cache.
	 *
	 * @return Enumeration, a list of Views in the node cache.
	 */
	//public Enumeration getViews();

	/**
	 * Returns a NodeSummary object from the cache with the given id.
	 *
	 * @param String sID, the id of the node to return from the cache.
	 * @return NodeSummary, the node from the cache with the given id, else null.
	 */
	//public NodeSummary getNodeSummary(String sID);

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
	//public NodeSummary getNodeSummary(NodeSummary node) throws NoSuchElementException;

	/**
	 *	This method updates the node cache for the NodeSummary with the appropriate action:
	 *	updating the reference count or by adding a new entry in the cache.
	 *
	 * 	@param NodeSummary[] nodes, the nodes to update in the cache.
	 *	@return NodeSummary[], the updated nodes.
	 */
	//public NodeSummary[] addNodeSummaries(NodeSummary[] nodes);

	/**
	 *	This method takes in a Node Summary and adds it to the node cache.
	 *	and returns a reference to the object.
	 *
	 *	@param NodeSummary node, the node to add to the cache.
	 *	@param NodeSummary, the node added to the cache, or the cached version if already there.
	 */
	//public NodeSummary addNodeSummary(NodeSummary node);

	/**
	 *This method returns a View reference from the node cache, if a node with that id exitis in the cache.
	 *
	 * @param String sID, the id of the view to return from the cache.
	 * @return View, the view from the cache with the given id, else null.
	 */
	//public View getView(String sID);


// CODES

	/**
	 * Checks if a code with the passed name already exists in the data 
	 * and does not have the the given code id.
	 * @param sCodeID the code id to ignore
	 * @param sName the code name to check.
	 * @return true if a duplicate named code exists, else false
	 */
	public boolean codeNameExists(String sCodeID, String name);

	/**
	 *  Load all the codes for the current project into the code cache.
	 */
	public void loadAllCodes() throws Exception;

	/**
	 *	Returns a Code object with the given CodeID.
	 *
	 * 	@param String sCodeID, the id of the code to return from the cache.
	 * 	@return Code, the code with the given code id in the cache, else null.
	 */
	public Code getCode(String codeID) ;

	/**
	 *	Get all the codes in the code cache as a Hashtable.
	 */
	public Hashtable getCodesCheck();

	/**
	 *	Get all the codes in the code cache as an Enumeration
	 */
	public Enumeration getCodes();

	/**
	 *	Add a code to the code cache.
	 *
	 *	@param Code code, the code to add to the code cache.
	 *	@return boolean, true if the code was added to the cache, else false.
	 */
	public boolean addCode(Code code);

	/**
	 *	Remove a code from the code cache.
	 *
	 *	@param Code code, the code to remove from the code cache.
	 */
	public void removeCode(Code code);

	/**
	 *	Replace a code in the code cache with the given code, where the code id's match.
	 *
	 *	@param Code code, the code which to replace in the cache.
	 */
	public void replaceCode(Code code);


// CODE GROUPS

	/**
	 *  Load a set of all code groups for the current project
	 *  from the database and store in the code cache.
	 */
	public void loadAllCodeGroups() throws Exception;

	/**
	 * Returns the Hashtable of information for the code group with the given id.
	 * Currently the hastable contains two element keys:
	 * 'children' is mapped to a Hashtable of all Code object in the code group (codeid, code).
	 * 'group' is mapped to a Vector of code group information: 0=CodeGroupID, 1=Name.
	 *
	 *	@param String sCodeGroupID, the id of the code group to get from the code cache.
	 *	@return Hashtable, containing the code group information if found, else empty.
	 */
	public Hashtable getCodeGroup(String sCodeGroupID);

	/**
	 *	Return all the ungrouped codes in the code cache.
	 *
	 *	@return Hashtable, all the ungrouped codes in the code cache.
	 */
	public Hashtable getUngroupedCodes();

	/**
	 *	Gets all the codesgroups from the code cache.
	 *
	 *	@return Hashtable, containing hashtables of information about all the code groups in the code cache.
	 * Currently the hastable contains two element keys:
	 * 'children' is mapped to a Hashtable of all Code object in the code group (codeid, code).
	 * 'group' is mapped to a Vector of code group information: 0=CodeGroupID, 1=Name.
	 */
	public Hashtable getCodeGroups();

	/**
	 * This method adds in a code group to the cache.
	 *
	 * @param String sCodeGroupID, the id of the code group to add to the cache.
	 * @param Vector vtGroup, the Vector of information about the code group.
	 * Currently the elements in the Vector are: 0=CodeGroupID, 1=Name
	 */
	public void addCodeGroup(String sCodeGroupID, Vector group);

	/**
	 * This method replace a codegroup name for the given codegroup id in the cache.
	 *
	 * @param String sCodeGroupID, the id of the code group whose name to replace.
	 * @param String sName, the new name of the code group.
	 */
	public void addCodeGroupCode(String sCodeGroupID, String sCodeID, Code code);

	/**
	 * This method adds in a code into code group in the cache.
	 *
	 * @param String sCodeGroupID, the id of the code group to add the code to
	 * @param String sCodeID, the id of the code to add.
	 * @param Code code, the Code object to add to the code group with the given code group id.
	 */
	public void removeCodeGroup(String sCodeGroupID);

	/**
	 * This method removes a code group with the given id from the cache.
	 *
	 * @param String sCodeGroupID, the id of the cide group to remove from the cache.
	 */
	public void replaceCodeGroupName(String sCodeGroupID, String sName);

	/**
	 * This method removes a code from a certain code group in the cache.
	 *
	 * @param String sCodeGroupID, the id of the code group to remove the code from.
	 * @param String sCodeID, the id of the code to remove from the code group.
	 */
	public void removeCodeGroupCode(String sCodeGroupID, String sCodeID);

	/**
	 * Help to clear up variables used by this object to assist with garbage collection.
	 */
	public void cleanUp();
	
}
