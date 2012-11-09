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
import java.awt.Dimension;
import java.sql.SQLException;

import com.compendium.core.ICoreConstants;
import com.compendium.core.datamodel.services.*;

/**
 * The Node object represents a hyperlinkable node that
 * has as its minimum properties a type, label and detailed description.
 *
 * @author	rema and sajid / Michelle Bachler / Lakshmi Prabhakaran
 */
public class NodeSummary extends	IdObject
						 implements INodeSummary,
									java.io.Serializable {

	/** Imported identifier property name for use with property change events */
	public final static String NODE_ORIGINAL_ID_PROPERTY	= "originalId";

	/** Type property name for use with property change events */
	public final static String NODE_TYPE_PROPERTY			= "type";

	/** Extended node type property name for use with property change events */
	public final static String EXTENDED_NODE_TYPE_PROPERTY 	= "extendednodetypeproperty" ;

	/** State property name for use with property change events */
	public final static String STATE_PROPERTY 				= "state" ;

	/** Label property name for use with property change events */
	public final static String LABEL_PROPERTY				= "label";

	/** Last Modification Author property name for use with property change events */
	public final static String LAST_MOD_AUTHOR_PROPERTY		= "lastmodificationauthor";

	/** View number property name for use with property change events */
	public final static String VIEW_NUM_PROPERTY 			= "viewnum";

	/** Tag property name for use with property change events */
	public final static String TAG_PROPERTY 				= "tag";

	/** Detail property name for use with property change events */
	public final static String DETAIL_PROPERTY 				= "detail";

	/** Image property name for use with property change events */
	public final static String IMAGE_PROPERTY 				= "image";

	/** Image size property name for use with property change events */
	public final static String IMAGE_SIZE_PROPERTY 				= "imagesize";

	/** Source property name for use with property change events */
	public final static String SOURCE_PROPERTY 				= "source";


	/** A static list of NodeSummary object already created in this session.*/
	private static Vector 	nodeSummaryList 		= new Vector();

	/**
	 * The type of this node.
	 * @see com.compendium.core.ICoreConstants for more details.
	 */
	protected int		nType				= -1;

	/** The extended node type of this node. NOT CURRENTLY USED.*/
	protected String 	sExtendedNodeType 	= "" ;

	/** The original unique id of this node (if node imported from elsewhere).*/
	protected String	sOriginalID			= "";

	/** The current state of this node: not read (0) read (1), modified since last read (2).*/
	protected int 		nState 				= -1;

	/** The label of this node.*/
	protected String 	sLabel 				= "";
	
	/** Whether the label still needs to be written to the database */
	protected Boolean	bLabelDirty 		= false;

	/** The codes (tags) added to this node.*/
	protected Hashtable htCodes 			= new Hashtable();
	
	/** A flag that lets us know if the codes have been fetched from the DB */
	protected boolean bCodesFetched			= false;

	/** The shortcut nodes pointing to this node.*/
	protected Hashtable htShortCutNodes 	= new Hashtable();

	/** The reference source string for this node.*/
	protected String sSource = "";

	/** The image associated with this node.*/
	protected String sImage = "";

	/** The width of the image associated with this node.*/
	protected int nImageWidth = 0;

	/** The height of the image associated with this node.*/
	protected int nImageHeight = 0;

	/** The first page of detail for this node.*/
	protected String sDetail = "";

	/** A count of the views this node is in.*/
	private	int				nMultipleViewsCount 	= 0;

	/** Is this node in more than one view.*/
	private	boolean 		bInMultipleViews 		= false;

	/** The parent node of this node.*/
	private NodeSummary 	parent 					= null;

	/** A list of all the details pages associated with this node.*/
	private Vector			detailPages				= null;
	
	/** Holds the name of the person who last modified this node.*/
	private String 			sLastModificationAuthor	= "";
	
	/** Holds the last modified date for this node **/
	private Date			dModificationDate		= null;

	/**
	 *	Constructor, creates an empty NodeSummary object.
	 */
	public NodeSummary() {}

	/**
	 *	Constructor, creates a NodeSummary object with only the Id value filled in
	 *	This constructor is maily used to retrieve the id value of the NodeSummary
	 *	to the client, where the Model object should be queried to get the actual
	 *	NodeSummary object.
	 *
	 *	@param String sNodeID, the id of the NodeSummary
	 */
	protected NodeSummary(String sNodeID) {
		super(sNodeID, -1, null, null, null ) ;
	}

	/**
	 *	Constructor, creates a NodeSummary object.
	 *
	 *	@param String sNodeID, the id of the NodeSummary.
	 *	@param int nType, the type of this node.
	 *	@param String sXNodeType, the extended node type id of the node - NOT CURRENTLY USED.
	 *	@param String sOriginalID, the original id of the node if it was imported.
	 *	@param String sAuthor, the author of this node.
	 *	@param Date dCreationDate, the creation date of this node.
	 *	@param Date dModificationDate, the date the node was last modified.
	 */
	protected NodeSummary(String sNodeID, int nType, String sXNodeType, String sOriginalID,
							int nState, String sAuthor, Date dCreationDate, Date dModificationDate, 
							String sLabel, String sDetail) {

		this(sNodeID, nType, sXNodeType, sOriginalID, -1, nState, sAuthor, dCreationDate, 
				dModificationDate, sLabel, sDetail);		
	}

	/**
	 *	Constructor, creates a NodeSummary object.
	 *
	 *	@param sNodeID the id of the NodeSummary.
	 *	@param nType the type of this node.
	 *	@param sXNodeType the extended node type id of the node - NOT CURRENTLY USED.
	 *	@param sOriginalID the original id of the node if it was imported.
	 *	@param sAuthor the author of this node.
	 *	@param dCreationDate the creation date of this node.
	 *	@param dModificationDate the date the node was last modified.
	 *	@param sLastModAuthor the name of the author who last modified this node.
	 */
	protected NodeSummary(String sNodeID, int nType, String sXNodeType, String sOriginalID,
							int nState, String sAuthor, Date dCreationDate, Date dModificationDate, 
							String sLabel, String sDetail, String sLastModAuthor ) {

		this(sNodeID, nType, sXNodeType, sOriginalID, -1, nState, sAuthor, dCreationDate, 
				dModificationDate, sLabel, sDetail, sLastModAuthor);		
	}
	
	/**
	 *	Constructor, creates a NodeSummary object.
	 *
	 *	@param String sNodeID, the id of the NodeSummary.
	 *	@param int nType, the type of this node.
	 *	@param String sXNodeType, the extended node type id of the node - NOT CURRENTLY USED.
	 *	@param String sOriginalID, the original id of the node if it was imported.
	 *	@param int nPermission, the permissions in this node - NOT CURRENTLY USED.
	 *	@param int nState, the state of this node: not read (1) read (2), modified since last read (3).
	 *	@param String sAuthor, the author of the node.
	 *	@param Date dCreationDate, the creation date of this node.
	 *	@param Date dModificationDate, the date the node was last modified.
	 *	@param String sLabel, the label of this node.
	 *	@param String sDetail, the first page of detail for this node.
	 */
	protected NodeSummary(String sNodeID, int nType, String sXNodeType, String sOriginalID, int nPermission,
							int nState, String sAuthor, Date dCreationDate, Date dModificationDate,
							String sLabel, String sDetail) {

		this(sNodeID, nType, sXNodeType, sOriginalID, nPermission, nState, sAuthor, dCreationDate, 
				dModificationDate, sLabel, sDetail, "");
	}
	
	/**
	 *	Constructor, creates a NodeSummary object.
	 *
	 *	@param String sNodeID, the id of the NodeSummary.
	 *	@param int nType, the type of this node.
	 *	@param String sXNodeType, the extended node type id of the node - NOT CURRENTLY USED.
	 *	@param String sOriginalID, the original id of the node if it was imported.
	 *	@param int nPermission, the permissions in this node - NOT CURRENTLY USED.
	 *	@param int nState, the state of this node: not read (1) read (2), modified since last read (3).
	 *	@param String sAuthor, the author of the node.
	 *	@param Date dCreationDate, the creation date of this node.
	 *	@param Date dModificationDate, the date the node was last modified.
	 *	@param String sLabel, the label of this node.
	 *	@param String sDetail, the first page of detail for this node.
	 *	@param sLastModAuthor the name of the author who last modified this node.
	 */
	protected NodeSummary(String sNodeID, int nType, String sXNodeType, String sOriginalID, int nPermission,
							int nState, String sAuthor, Date dCreationDate, Date dModificationDate,
							String sLabel, String sDetail, String sLastModAuthor) {
						
		super(sNodeID, nPermission, sAuthor, dCreationDate, dModificationDate);

		this.sOriginalID = sOriginalID;
		this.nType = nType;
		this.sExtendedNodeType = sXNodeType;
		this.nState = nState;
		this.sLabel = sLabel;
		this.sDetail = sDetail;
		this.sLastModificationAuthor = sLastModAuthor;
		this.dModificationDate = dModificationDate;
	}	

	/**
	 * Returns true if the node already has a nodesummary object in the cache
	 * 
	 * @param String sNodeID, the id of the node to return/create.
	 * @return True if we already have the NodeSummary object in the cache.
	 */
	public static boolean bIsInCache(String sNodeID) {
		int i = 0;

		for (i = 0; i < nodeSummaryList.size(); i++) {
			if (sNodeID.equals(((NodeSummary)nodeSummaryList.elementAt(i)).getId())) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Return a node summary object with the given id.
	 * If a node with the given id has already been created in this session, return that,
	 * else create a new one, and add it to the list.
	 *
	 * @param String sNodeID, the id of the node to return/create.
	 * @return NodeSummary, a node summary object with the given id.
	 */
	public static NodeSummary getNodeSummary(String sNodeID) {
		int i = 0;

		for (i = 0; i < nodeSummaryList.size(); i++) {
			if (sNodeID.equals(((NodeSummary)nodeSummaryList.elementAt(i)).getId())) {
				break;
			}
		}

		NodeSummary ns = null;
		if (i == nodeSummaryList.size()) {
			ns = new NodeSummary(sNodeID);
			nodeSummaryList.addElement(ns);
		}
		else {
			Object obj = nodeSummaryList.elementAt(i);
			ns = (NodeSummary)obj;
		}

		return ns;
	}

	/**
	 * Return a node summary object with the given id and details.
	 * If a node with the given id has already been created in this session, update its data and return that,
	 * else create a new one, and add it to the list.
	 *
	 *	@param String sNodeID, the id of the NodeSummary.
	 *	@param int nType, the type of this node.
	 *	@param String sXNodeType, the extended node type id of the node - NOT CURRENTLY USED.
	 *	@param String sOriginalID, the original id of the node if it was imported.
	 *	@param String sAuthor, the author of the node.
	 *	@param Date dCreationDate, the creation date of this node.
	 *	@param Date dModificationDate, the date the node was last modified.
	 *	@param String sLabel, the label of this node.
	 *	@param String sDetail, the first page of detail for this node.
	 * @return NodeSummary, a node summary object with the given id.
	 */
	//Used by: DBNODE/DBSEARCH/DBVIEWNODE/TRASHBIN NODE
	public static synchronized NodeSummary getNodeSummary(String sNodeID, int nType, String sXNodeType, String sOriginalID,
				int state, String sAuthor, Date dCreationDate, Date dModificationDate, String sLabel, String sDetail) {

		NodeSummary ns = null;

		int i = 0;
		for (i = 0; i < nodeSummaryList.size(); i++) {
			ns = (NodeSummary)nodeSummaryList.elementAt(i);
			if (sNodeID.equals(ns.getId())) {
				break;
			}
		}

		if (i == nodeSummaryList.size()) {
			ns = new NodeSummary(sNodeID, nType, sXNodeType, sOriginalID,
						state, sAuthor, dCreationDate,
								 dModificationDate, sLabel, sDetail);
			nodeSummaryList.addElement(ns);
		}
		else {
			Object obj = nodeSummaryList.elementAt(i);
			if (obj instanceof View) {
				nodeSummaryList.removeElement(obj);
				ns = new NodeSummary(sNodeID, nType, sXNodeType, sOriginalID,
								 state, sAuthor, dCreationDate,
								 dModificationDate, sLabel, sDetail);
				nodeSummaryList.addElement(ns);
			}
			else {
				ns = (NodeSummary)obj;

				// UPDATE THE DETAILS
				if (!ns.bLabelDirty) {
					ns.setLabelLocal(sLabel);
				}
				ns.setDetailLocal(sDetail);
				ns.setTypeLocal(nType);
				ns.setStateLocal(state);
				ns.setAuthorLocal(sAuthor);
				ns.setCreationDateLocal(dCreationDate);
				ns.setModificationDateLocal(dModificationDate);
				ns.setOriginalIdLocal(sOriginalID);
				ns.setExtendedNodeTypeLocal(sXNodeType);
			}
		}
		return ns;
	}

	/**
	 * Return a node summary object with the given id and details.
	 * If a node with the given id has already been created in this session, update its data and return that,
	 * else create a new one, and add it to the list.
	 *
	 *	@param sNodeID the id of the NodeSummary.
	 *	@param nType the type of this node.
	 *	@param sXNodeType the extended node type id of the node - NOT CURRENTLY USED.
	 *	@param sOriginalID the original id of the node if it was imported.
	 *	@param sAuthor the author of the node.
	 *	@param dCreationDate the creation date of this node.
	 *	@param dModificationDate the date the node was last modified.
	 *	@param sLabel the label of this node.
	 *	@param sDetail the first page of detail for this node.
	 *	@param sLastModAuthor the name of the author who last modified this node.
	 *	@return NodeSummary, a node summary object with the given id.
	 */
	//Used by: DBNODE/DBSEARCH/DBVIEWNODE/TRASHBIN NODE
	public static synchronized NodeSummary getNodeSummary(String sNodeID, int nType, String sXNodeType, String sOriginalID,
				int state, String sAuthor, Date dCreationDate, Date dModificationDate, String sLabel, 
				String sDetail, String sLastModAuthor) {

		NodeSummary ns = null;

		int i = 0;
		for (i = 0; i < nodeSummaryList.size(); i++) {
			ns = (NodeSummary)nodeSummaryList.elementAt(i);
			if (sNodeID.equals(ns.getId())) {
				break;
			}
		}
		if (i == nodeSummaryList.size()) {
			ns = new NodeSummary(sNodeID, nType, sXNodeType, sOriginalID,
						state, sAuthor, dCreationDate, dModificationDate, sLabel, sDetail, sLastModAuthor);
			nodeSummaryList.addElement(ns);
		}
		else {
			Object obj = nodeSummaryList.elementAt(i);
			if (obj instanceof View) {
				nodeSummaryList.removeElement(obj);
				ns = new NodeSummary(sNodeID, nType, sXNodeType, sOriginalID,
								 state, sAuthor, dCreationDate,
								 dModificationDate, sLabel, sDetail, sLastModAuthor);
				nodeSummaryList.addElement(ns);
			}
			else {
				ns = (NodeSummary)obj;

				// UPDATE THE DETAILS
				if (!ns.bLabelDirty) {				
					ns.setLabelLocal(sLabel);
				}
				ns.setDetailLocal(sDetail);
				ns.setTypeLocal(nType);
				ns.setStateLocal(state);
				ns.setAuthorLocal(sAuthor);
				ns.setCreationDateLocal(dCreationDate);
				ns.setModificationDateLocal(dModificationDate);
				ns.setOriginalIdLocal(sOriginalID);
				ns.setExtendedNodeTypeLocal(sXNodeType);
				ns.setLastModificationAuthorLocal(sLastModAuthor);				
			}
		}
		return ns;
	}
	
	/**
	 * Remove the given node from the node list.
	 *
	 * @param NodeSummary node, the node to remove from the node list.
	 */
	public static void removeNodeSummaryListItem(NodeSummary node) {
		String id = node.getId();
		int count = nodeSummaryList.size();
		for (int i = 0; i < count ; i++) {
			if (id.equals(((NodeSummary)nodeSummaryList.elementAt(i)).getId())) {
				nodeSummaryList.removeElementAt(i);
				return;
			}
		}
	}

	/**
	 * Return the list of all nodes created / used in this session.
	 */
	public static Vector getNodeSummaryList() {
		return nodeSummaryList;
	}

	/**
	 * Clear the list of all nodes created / used in this session.
	 */
	public static void clearList() {
		nodeSummaryList.removeAllElements();
	}

	/**
	 * The initialize method adds the model and session object to this object.
	 * Also, load all the codes associated with this node.
	 *
	 * @param PCSession session, the session associated with this object.
	 * @param IMode model, the model this object belongs to.
	 */
	public void initialize(PCSession session, IModel model) {
		super.initialize(session, model);
		try {loadCodes();}
		catch(Exception ex) {System.out.println("Unable to load codes for node "+sId+" : "+ex.getMessage());}
	}

	/**
	 *	This method needs to be called on this object before the Model removes it from the cache.
	 */
	public void cleanUp() {
		super.cleanUp() ;
	}

	/**
	 * Returns the object's original id. When
	 * importing maps we need to make sure
	 * that objects with the same id end up
	 * as the same object.
	 *
	 * @return String, the original imported identifier of this object.
	 */
	public String getOriginalID() {
		return sOriginalID;
	}

	/**
	 * Sets the original imported id of this object, both locally and in the DATABASE.
	 * <p>
	 * This is the unique identifier for this object as used in the Original Map.
	 *
	 * @param sOriginalID the original imported id of this object.
	 * @param sLastModAuthor the author name of the person who made this modification. 
	 * @exception java.sql.SQLException
	 * @exception java.sql.ModelSessionException
	 */
	public void setOriginalID(String sOriginalID, String sLastModAuthor) throws SQLException, ModelSessionException {

		if (this.sOriginalID.equals(sOriginalID))
			return;

		if (oModel == null)
			throw new ModelSessionException("Model is null in NodeSummary.setOriginalID");
		if (oSession == null) {
			oSession = oModel.getSession();
			if (oSession == null)
				throw new ModelSessionException("Session is null in NodeSummary.setOriginalID");
		}
		
		Date date = new Date();
		setModificationDateLocal(date);
		setLastModificationAuthorLocal(sLastModAuthor);
		INodeService ns = oModel.getNodeService() ;
		ns.setOriginalID(oSession, sId, this.sOriginalID, sOriginalID, date, sLastModAuthor);
		setOriginalIdLocal(sOriginalID) ;
	}

	/**
	 *	Sets the imported id property for the object and fires property change to local listeners
	 *
	 *	@param String sOriginalID, the original imported value.
	 *	@return String, the old Value of the imported Id property.
	 */
	protected String setOriginalIdLocal(String sOriginalID) {

		if (this.sOriginalID.equals(sOriginalID))
			return "";

		String oldValue = sOriginalID;
		this.sOriginalID = sOriginalID;

		firePropertyChange(NODE_ORIGINAL_ID_PROPERTY, oldValue, sOriginalID);
		return oldValue ;
	}


	/**
	 * Returns the node type.
	 * @return int, the node type.
	 */
	public int getType() {
		return nType;
	}

	/**
	 * Sets the node type, both locally and in the DATABASE.
	 *
	 * @param int type the node type.
	 * @param sLastModAuthor the author name of the person who made this modification. 
	 * @exception java.sql.SQLException
	 * @exception java.sql.ModelSessionException
	 */
	public void setType(int type, String sLastModAuthor) throws SQLException, ModelSessionException {

		if (nType == type)
			return;

		if (oModel == null)
			throw new ModelSessionException("Model is null in NodeSummary.setType");
		if (oSession == null) {
			oSession = oModel.getSession();
			if (oSession == null)
				throw new ModelSessionException("Session is null in NodeSummary.setType");
		}
		
		Date date = new Date();
		setModificationDateLocal(date);
		setLastModificationAuthorLocal(sLastModAuthor);		
		INodeService ns = oModel.getNodeService();
		ns.setType(oSession, sId, nType, type, date, sLastModAuthor);
		setTypeLocal(type) ;
  	}

	/**
	 *	Sets the node type and fires changes to local listeners.
	 *
	 *	@param int type, the integer representing the type value.
	 *	@return int, the old value
	 */
	protected int setTypeLocal(int type) {

		if (nType == type)
			return nType;

		int oldValue = nType;
		nType = type;

		firePropertyChange(NODE_TYPE_PROPERTY, oldValue, nType);
		return oldValue;
	}

	/**
	 *	Returns the extended type for the node - NOT CURRENTLY USED.
	 *	@return String, the extended type for the node.
	 */
	public String getExtendedNodeType() {
		return sExtendedNodeType ;
	}

 	/**
	 *	Sets the extended node typefor the node, both locally and in the DATABASE.
	 *
	 *	@param String name, the extended node type of the node.
	 * 	@param sLastModAuthor the author name of the person who made this modification.
	 *	@exception java.sql.SQLException
	 *	@exception java.sql.ModelSessionException
	 */
	public void setExtendedNodeType(String name, String sLastModAuthor) throws SQLException, ModelSessionException {

		if (sExtendedNodeType.equals(name))
			return ;

		if (oModel == null)
			throw new ModelSessionException("Model is null in NodeSummary.setExtendedType");
		if (oSession == null) {
			oSession = oModel.getSession();
			if (oSession == null)
				throw new ModelSessionException("Session is null in NodeSummary.setExtendedType");
		}
		
		Date date = new Date();
		setModificationDateLocal(date);
		setLastModificationAuthorLocal(sLastModAuthor);				
		INodeService ns = oModel.getNodeService() ;
		ns.setExtendedNodeType(oSession, sId, sExtendedNodeType, name, date, sLastModAuthor);
		setExtendedNodeTypeLocal(name) ;
	}

 	/**
	 *	Sets the extended node typefor the node and fires changes to local listeners only.
	 *
	 *	@param String name, the extended node type of the node.
	 *	@return String, the old extended type name.
	 */
	protected String setExtendedNodeTypeLocal(String name) {
		if (sExtendedNodeType.equals(name))
			return "";

		String oldValue = sExtendedNodeType ;
		sExtendedNodeType = name ;
		firePropertyChange(EXTENDED_NODE_TYPE_PROPERTY, oldValue , sExtendedNodeType) ;
		return oldValue ;
	}


	/**
	 * 	Sets the creation date for this node, both locally and in the DATABASE.
	 *
	 * 	@param Date creation is the node creation date.
	 * 	@param sLastModAuthor the author name of the person who made this modification.	 
	 * 	@exception java.sql.SQLException
	 * 	@exception java.sql.ModelSessionException
	 */
	public void setCreationDate(Date creation, String sLastModAuthor) throws SQLException, ModelSessionException {

		if (oCreationDate == creation)
			return;

		if (oModel == null)
			throw new ModelSessionException("Model is null in inNodeSummary.setCreationDate");
		if (oSession == null) {
			oSession = oModel.getSession();
			if (oSession == null)
				throw new ModelSessionException("Session is null in NodeSummary.setCreationDate");
		}
		
		Date date = new Date();
		setModificationDateLocal(date);		
		setLastModificationAuthorLocal(sLastModAuthor);		
		INodeService ns = oModel.getNodeService() ;
		ns.setCreationDate(oSession, sId, creation, date, sLastModAuthor);
		super.setCreationDateLocal(creation);
  	}

	/**
	 * Sets the author for this node, both locally and in the DATABASE.
	 *
	 * @param String sAuthor, the author of this node.
	 * @param sLastModAuthor the author name of the person who made this modification.  
	 * @exception java.sql.SQLException
	 * @exception java.sql.ModelSessionException
	 */
	public void setAuthor(String sAuthor, String sLastModAuthor) throws SQLException, ModelSessionException {

		if (oAuthor == sAuthor)
			return;

		if (oModel == null)
			throw new ModelSessionException("Model is null in NodeSummary.setAuthor");
		if (oSession == null) {
			oSession = oModel.getSession();
			if (oSession == null)
				throw new ModelSessionException("Session is null in NodeSummary.setAuthor");
		}
		
		Date date = new Date();
		setModificationDateLocal(date);
		setLastModificationAuthorLocal(sLastModAuthor);						
		INodeService ns = oModel.getNodeService() ;
		ns.setAuthor(oSession, sId, sAuthor, date, sLastModAuthor);
		super.setAuthorLocal(sAuthor);
  	}

 	/**
	 * Returns the Node STATE: not read (1) read (2), modified since last read (3).
	 * <p>
	 *
	 *	@return int, the state value.
	 */
	public int getState() {
		return nState ;
	}
	
	
 	/**
 	 *	Sets the Node STATE Variable, both locally and in the DATABASE.
	 *
	 *	@param int state, the int state value: not read (0) read (1), modified since last read (2).
	 *	@exception java.sql.SQLException
	 *	@exception java.sql.ModelSessionException
	 */
	public void setState(int state) throws SQLException, ModelSessionException {

		if (state == nState)
			return;

		if (oModel == null)
			throw new ModelSessionException("Model is null in NodeSummary.setState");
		if (oSession == null) {
			oSession = oModel.getSession();
			if (oSession == null)
				throw new ModelSessionException("Session is null in NodeSummary.setState");
		}

		// CAN'T CHANGE THE STATE OF YOUR HOME VIEW
		if (oModel.getUserProfile() != null &&
				getId().equals(oModel.getUserProfile().getHomeView().getId())) {
			return;
		}
		
		Date date = new Date();
		//setModificationDateLocal(date);
		INodeService ns = oModel.getNodeService();
		
		if (state == ICoreConstants.MODIFIEDSTATE && !(this instanceof View)) {
			//Lakshmi (4/20/06) - The current user has modified the node which means the user has read. 
			state = ICoreConstants.READSTATE;
		}
		
		ns.setState(oSession, sId, nState, state, date);

		setStateLocal(state);
	}

 	/**
 	 *	Sets the Node STATE Variable, locally only.
	 *
	 *	@param int state, the int state value: not read (1) read (2), modified since last read (3).
	 */
	public void setStateLocal(int state) {
		
		if (state == nState)
			return;

		int oldValue = nState;
		nState = state;
		
		firePropertyChange(STATE_PROPERTY, oldValue, nState);
	}

	/**
	 * Return the node's label.
	 * @param String, the label of this node.
	 */
	public String getLabel() {
		return sLabel ;
	}

	/**
	 * Sets the label of this node, both locally and in the DATABASE.
	 *
	 * @param String label, The label of this node .
	 * @param sLastModAuthor the author name of the person who made this modification.   
	 * @exception java.sql.SQLException
	 * @exception java.sql.ModelSessionException
	 */
	public void setLabel(String label, String sLastModAuthor) throws SQLException, ModelSessionException {

		if (label.equals(sLabel))
			return;

		if (oModel == null)
			throw new ModelSessionException("Model is null in NodeSummary.setLabel");
		if (oSession == null) {
			oSession = oModel.getSession();
			if (oSession == null)
				throw new ModelSessionException("Session is null in NodeSummary.setLabel");
		}
				
		Date date = new Date();
		setModificationDateLocal(date);
		setLastModificationAuthorLocal(sLastModAuthor);		
		setLabelLocal(label) ;
		
		INodeService ns = oModel.getNodeService() ;	
		ns.setLabel(oSession, sId, label, date, sLastModAuthor);
		bLabelDirty = false;
	}

	/**
	 * Sets the label of this node locally.
	 *
	 *	@param String label, the label of this node.
	 */
	public void setLabelLocal(String label) {
		if (label.equals(sLabel))
			return;

		String oldValue = sLabel;
		sLabel = label;
		if (sLabel == null)
			sLabel = "";

		firePropertyChange(LABEL_PROPERTY, oldValue, sLabel);
		bLabelDirty = true;	
		return;
	}
	
	/**
	 * Force the node's label to the database if dirty.
	 * @param sLastModAuthor the author name of the person who made this modification.
	 * @exception java.sql.SQLException
	 * @exception java.sql.ModelSessionException
	 */
	public boolean flushLabel(String sLastModAuthor) throws SQLException, ModelSessionException {
		
		if (bLabelDirty == false) 
			return false;

		if (oModel == null)
			throw new ModelSessionException("Model is null in NodeSummary.setLabel");
		if (oSession == null) {
			oSession = oModel.getSession();
			if (oSession == null)
				throw new ModelSessionException("Session is null in NodeSummary.setLabel");
		}
		Date date = new Date();
		setModificationDateLocal(date);
		INodeService ns = oModel.getNodeService() ;
		ns.setLabel(oSession, sId, sLabel, date, sLastModAuthor);
		bLabelDirty = false;
		return true;
	}
	

	/**
	 * Return the node's label.
	 * @param String, the label of this node.
	 */
	public String getLastModificationAuthor() {
		return sLastModificationAuthor;
	}
	
	/**
	 * Return the node's last modified date
	 */
	public Date getLastModifiedDate() {
		return dModificationDate;
	}

	/**
	 * Sets the label of this node locally.
	 *
	 *	@param String label, the label of this node.
	 *	@return String, the old value of the label.
	 */
	protected String setLastModificationAuthorLocal(String sAuthor) {
		if (this.sLastModificationAuthor.equals(sAuthor))
			return "";

		String oldValue = sAuthor;
		this.sLastModificationAuthor = sAuthor;
		if (sAuthor == null)
			sAuthor = "";

		firePropertyChange(LAST_MOD_AUTHOR_PROPERTY, oldValue, sAuthor) ;
		return oldValue;
	}
	
	/**
	 *	Returns the first detail page of this node.
	 *
	 *	@return String, the first detail page of this node.
	 */
	public String getDetail() {
		return sDetail;
	}

	/**
	 *	Sets the first page of details of this node, both locally and in the DATABASE.
	 * 	Fires property change to listeners and calls service to update db.
	 *
	 *	@param detail, the first page of detail for this node.
	 *	@param sAuthor the author of these detail pages.
	 *  @param sLastModAuthor the author name of the person who made this modification.   
	 *	@exception java.sql.SQLException
	 *	@exception java.sql.ModelSessionException
	 */
	public void setDetail(String detail, String sAuthor, String sLastModAuthor) throws SQLException, ModelSessionException {

		if (sDetail == null)
			sDetail = "";
		if (detail == null)
			detail = "";

		if (oModel == null)
			throw new ModelSessionException("Model is null in NodeSummary.setDetail");
		if (oSession == null) {
			oSession = oModel.getSession();
			if (oSession == null)
				throw new ModelSessionException("Session is null in NodeSummary.setDetail");
		}

		if (sDetail.equals(detail))
			return;

		INodeService nodeService = oModel.getNodeService();
		Date date = new Date();
		setModificationDateLocal(date);
		setLastModificationAuthorLocal(sLastModAuthor);		
				
		String oldValue = this.sDetail;
		nodeService.setDetail(oSession, sId, oldValue, detail, sAuthor, date, sLastModAuthor);

		this.sDetail = detail;

		if (detailPages != null && detailPages.size() > 0) {
			NodeDetailPage page1 = (NodeDetailPage)detailPages.elementAt(0);
			page1.setModificationDate(date);
			page1.setText(this.sDetail);
			detailPages.setElementAt(page1, 0);
		}
		setModificationDateLocal(date);		// Do it again because it gets overwritten w/old value by lower level code

		firePropertyChange(DETAIL_PROPERTY, oldValue, detail);
	}

	/**
	 *	Sets the first page of details of this node locally.
	 *
	 *	@param String detail, the first page of detail for this node.
	 */
	protected void setDetailLocal(String detail) {

		if (sDetail == null)
			sDetail = "";
		if (detail == null)
			detail = "";

		if (sDetail.equals(detail))
			return;

		sDetail = detail;

		// PAGE ONE STORED ON DETAIL FIELD - HISTORICAL
		if (detailPages != null && detailPages.size() > 0) {
			NodeDetailPage page1 = (NodeDetailPage)detailPages.elementAt(0);
			page1.setText(this.sDetail);
			detailPages.setElementAt(page1, 0);
		}
	}

	/**
	 * Gets all the detail pages of this node.
	 *
	 * @return Vector, of <code>NodeDetailPage</code> objects for the detail pages of this node.
	 * @exception java.sql.SQLException
	 * @exception java.sql.ModelSessionException
	 */
	public Vector getDetailPages(String sAuthor) throws SQLException, ModelSessionException {

		if (detailPages == null) {
			if (oModel == null)
				throw new ModelSessionException("Model is null in NodeSummary.getDetailPages");
			if (oSession == null) {
				oSession = oModel.getSession();
				if (oSession == null)
					throw new ModelSessionException("Session is null in NodeSummary.getDetailPages");
			}

			INodeService ns = oModel.getNodeService();
			detailPages = ns.getAllDetailPages(oSession, sId);

			if (detailPages.isEmpty()) {
				NodeDetailPage page = new NodeDetailPage(getId(), sAuthor, sDetail, 1, getCreationDate(), getModificationDate());
				detailPages.addElement( page );
			}
		}

		int count = detailPages.size();
		Vector newPages = new Vector(detailPages.size());
		for (int i=0; i<count; i++) {
			NodeDetailPage page = (NodeDetailPage)detailPages.elementAt(i);
			newPages.addElement(page.clone());
		}

		return newPages;
  	}

	/**
	 * Set the detail pages of this node, both locally and in the DATABASE.
	 *
	 * @return Vector, of <code>NodeDetailPage</code> objects for the detail pages of this node.
	 * @param sLastModAuthor the author name of the person who made this modification.
	 * @exception java.sql.SQLException
	 * @exception java.sql.ModelSessionException
	 */
	public void setDetailPages(Vector pages, String sAuthor, String sLastModAuthor) throws SQLException, ModelSessionException {

		if (oModel == null)
			throw new ModelSessionException("Model is null in NodeSummary.getDetailPages");
		if (oSession == null) {
			oSession = oModel.getSession();
			if (oSession == null)
				throw new ModelSessionException("Session is null in NodeSummary.getDetailPages");
		}

		INodeService ns = oModel.getNodeService();
		Date date = new Date();
		setModificationDateLocal(date);
		setLastModificationAuthorLocal(sLastModAuthor);		

		Vector oldPages = detailPages;
		ns.setAllDetailPages(oSession, sId, sAuthor, oldPages, pages, date, sLastModAuthor);

		// Required to cover pages being new Vector or actual detailPages when passed in.
		// Solves all conflicts.
		int count = pages.size();
		Vector newPages = new Vector(pages.size());
		for (int i=0; i<count; i++) {
			NodeDetailPage page = (NodeDetailPage)pages.elementAt(i);
			if (i == 0) {
				sDetail = page.getText();
			}

			newPages.addElement(page.clone());
		}
		detailPages = newPages;

		setModificationDateLocal(date);		// Do it again because it gets overwritten w/old value by lower level code

		firePropertyChange(DETAIL_PROPERTY, oldPages, detailPages);
 	}

	/**
	 * Used by arrange methods to set the logical parent of this node determined from linkage.
	 * @param NodeSummary ns, the node that is the linkage parent of this node.
	 */
	public void setParentNode(NodeSummary ns) {
		parent = ns;
	}

	/**
	 * Return the logical parent of this node determined from linkage.
	 * @param NodeSummary, the node that is the linkage parent of this node.
	 */
	public NodeSummary getParentNode() {
		return parent;
	}

	//////////////////////////////////////////////////////////////////////
	// Number of views methods
	//////////////////////////////////////////////////////////////////////

	/**
	 * Returns true if this node is contained in mulitple views
	 *
	 * @return boolean, true if this node is contained in mulitple views
	 */
	public boolean isInMultipleViews() {
		return bInMultipleViews;
	}

	/**
	 * Return a count of the number of views this node is in.
	 */
	public int getViewCount() {
		return nMultipleViewsCount;
	}

	/**
	 * Finds out from the Database how many view this node is now in and update local count.
	 * @return boolean, true if this node is in more than one view, else false.
	 */
	public boolean updateMultipleViews() {

		int oldValue = nMultipleViewsCount;

		// this updates the number of views and returns the count found.
		try {
			nMultipleViewsCount = getNumOfMultipleViews();
		}
		catch(Exception io) {}

		if(nMultipleViewsCount > 1)
			bInMultipleViews = true;
		else
			bInMultipleViews = false;

		if (oldValue != nMultipleViewsCount) {
			firePropertyChange(VIEW_NUM_PROPERTY, oldValue, nMultipleViewsCount);
		}
		return bInMultipleViews;
	}
	
	
	/**
	 * Decrements the view count in the nodesummary object and fires appropriate property changes.
	 * This is (currently) only called from View.removeMemberNode() when a node gets deleted/cut.
	 * In this context, we know the data in the database has just been adjusted and an in-memory
	 * decrement w/out re-querying the database gives an answer we're confident in.
	 * 
	 * NB: Thought this was a good optimization over the UpdateMultipleViews() code above, but
	 * in fact it does not factor in the case when a view contains itself, in which case during
	 * a deletion its nMultipleViewsCount may drop by 2 or more.
	 */
//	public void decrementViewCount() {
//		int oldValue = nMultipleViewsCount;
//		nMultipleViewsCount--;
//		if(nMultipleViewsCount > 1)
//			bInMultipleViews = true;
//		else
//			bInMultipleViews = false;
//		firePropertyChange(VIEW_NUM_PROPERTY, oldValue, nMultipleViewsCount);
//	}

	/**
	 *  Finds out from the Database if this node is contained in multiple views
	 *  and if so return them.
	 *	@return Vector, of parent views this node is in.
	 *	@exception java.sql.SQLException
	 *	@exception java.sql.ModelSessionException
	 */
	public Vector getMultipleViews() throws SQLException, ModelSessionException {

		Vector views = null;

		if (oModel == null)
			throw new ModelSessionException("Model is null in NodeSummary.getMultipleViews");
		if (oSession == null) {
			oSession = oModel.getSession();
			if (oSession == null)
				throw new ModelSessionException("Session is null in NodeSummary.getMultipleViews");
		}

		views = oModel.getNodeService().getViews(oSession, this.getId());
		return views;
	}

	/**
	 *  Finds out from the Database if this node is contained in multiple view and return the count.
	 *	@return int the number indicating the containing views.
	 *	@exception java.sql.SQLException
	 *	@exception java.sql.ModelSessionException
	 */
	private int getNumOfMultipleViews() throws SQLException, ModelSessionException {

		if (oModel == null)
			throw new ModelSessionException("Model is null in NodeSummary.getNumOfMultipleViews");
		if (oSession == null) {
			oSession = oModel.getSession();
			if (oSession == null)
				throw new ModelSessionException("Session is null in NodeSummary.getNumOfMultipleViews");
		}

		int count = 0;
// MLB: The following two lines originally were used to return the number of parents
// a node has.  This was a terrible performance hit - it results in six database calls
// per node, as it essentially builds the parent view from scratch.  It was replaced
// by iGetParentCount() which gets the data directly with one DB call.  Performance testing
// shows a 50%+ speedup in opening maps & nodes as a result.
		
//		Vector views = oModel.getNodeService().getViews(oSession,this.getId());		// Old/original code
//		count = views.size();														// Old/original code
		count = oModel.getNodeService().iGetParentCount(oSession, this.getId());	// New & improved!
		return count;
	}

	/**
	 *	Add the code to the given NodeSummary, both locally and in the DATABASE.
	 *
	 *	@param code the Code Reference to be added to the NodeSummary.
	 *	@return boolean true if successfully added, else false.
	 *	@exception java.util.NoSuchElementException
	 *	@exception java.sql.SQLException
	 *	@exception java.sql.ModelSessionException
	 */
	public boolean addCode(Code code) throws NoSuchElementException, SQLException, ModelSessionException {

		String id = code.getId();
		if (htCodes.containsKey(id))
			return false;

		boolean added = false;

		if (oModel == null)
			throw new ModelSessionException("Model is null in NodeSummary.addCode");
		if (oSession == null) {
			oSession = oModel.getSession();
			if (oSession == null)
				throw new ModelSessionException("Session is null in NodeSummary.addCode");
		}

		if (code != null)  {
			INodeService ns = oModel.getNodeService();
			added = ns.addCode(oSession, sId, code);
			if (added) {
				htCodes.put(id, code);
				firePropertyChange(TAG_PROPERTY, "", "added");
			}
			
			return added;
		}
		else {
			throw new NoSuchElementException();
		}
	}

	/**
	 *	Add codes to the given NodeSummary, both locally and in the DATABASE.
	 *
	 *	@param codes a list of codes to be added to the NodeSummary.
	 *	@return boolean true if successfully added, else false.
	 *	@exception java.util.NoSuchElementException
	 *	@exception java.sql.SQLException
	 *	@exception java.sql.ModelSessionException
	 */
	public boolean addCodes(Vector codes) throws NoSuchElementException, SQLException, ModelSessionException {

		boolean added = true;
		for(Enumeration e = codes.elements();e.hasMoreElements();) {
			Code code = (Code)e.nextElement();
			boolean add = addCode(code);
			added = added & add;
		}

		firePropertyChange(TAG_PROPERTY, "", "added");

		return added;
	}

	/**
	 * Removes the reference to the code with the given name, both locally and from the DATABASE.
	 *
	 * @param code  the code to be removed.
	 * @return boolean true if it was successfully removed, else false.
	 * @exception java.util.NoSuchElementException
	 * @exception java.sql.SQLException
	 * @exception java.sql.ModelSessionException
	 */
	public boolean removeCode(Code code) throws NoSuchElementException, SQLException, ModelSessionException  {

		code = (Code)htCodes.remove(code.getId());
		boolean deleted = false;

		if (oModel == null)
			throw new ModelSessionException("Model is null in NodeSummary.removeCode");
		if (oSession == null) {
			oSession = oModel.getSession();
			if (oSession == null)
				throw new ModelSessionException("Session is null in NodeSummary.removeCode");
		}

		if (code != null) {
			INodeService ns = oModel.getNodeService() ;
			deleted = ns.removeCode(oSession, sId, code.getId());

			firePropertyChange(TAG_PROPERTY, "", "removed");
			return deleted;
		}
		else {
			throw new NoSuchElementException();
		}
	}

	/**
	 * Returns a count of all the codes referenced by this node.
	 *
	 * @return int, a count of all the codes referenced by this node.
	 */
	public int getCodeCount() {

		return htCodes.size();
	}

	/**
	 *  Load all the codes referenced by this node, from the DATABASE.
	 *
	 *	@exception java.sql.SQLException
	 *	@exception java.sql.ModelSessionException
	 */
	public void loadCodes() throws SQLException, ModelSessionException {
		Vector codes = new Vector(51);

		if (!bCodesFetched) {
			if (oModel == null)
				throw new ModelSessionException("Model is null in NodeSummary.getCodes");
			if (oSession == null) {
				oSession = oModel.getSession();
				if (oSession == null)
					throw new ModelSessionException("Session is null in NodeSummary.getCodes");
			}
	
			INodeService ns = oModel.getNodeService() ;
			codes = ns.getCodes(oSession, sId) ;
			for(Enumeration e = codes.elements();e.hasMoreElements();) {
				Code code = (Code)e.nextElement();
				if(!htCodes.containsKey(code.getId()))
					htCodes.put(code.getId(),code);
			}
			bCodesFetched = true;
		}
	}

	/**
	 * Returns all the codes referenced by this node.
	 *
	 * @return an Enumeration of all the codes referenced by this node.
	 */
	public Enumeration getCodes() {

		return htCodes.elements();
	}

	/**
	 * Return if this node has the given code.
	 *
	 * @param sCodeName the name to check for.
	 */
	public boolean hasCode(String sCodeName) {

		for(Enumeration e = htCodes.elements();e.hasMoreElements();) {
			Code code = (Code)e.nextElement();
			if(code.getName().equals(sCodeName)) {
				return true;
			}
		}

		return false;
	}

	/**
	 *	Add the shortcut to the given NodeSummary, both locally and in the DATABASE.
	 *
	 *	@param shortcutnode the shortcut node to add.
	 *	@exception java.sql.SQLException
	 *	@exception java.sql.ModelSessionException
	 */
	public boolean addShortCutNode(NodeSummary shortcutnode) throws NoSuchElementException, SQLException, ModelSessionException {

		String id = shortcutnode.getId();
		boolean added = false;

		if (oModel == null)
			throw new ModelSessionException("Model is null in NodeSummary.getShortCutNode");
		if (oSession == null) {
			oSession = oModel.getSession();
			if (oSession == null)
				throw new ModelSessionException("Session is null in NodeSummary.getShortCutNode");
		}

		if (shortcutnode != null) {
			INodeService ns = oModel.getNodeService();
			added = ns.addShortCutNode(oSession, shortcutnode.getId(), sId);

			if (added) {
				if (!htShortCutNodes.containsKey(id))
					htShortCutNodes.put(id, shortcutnode);
			}
		}

		return added;
	}

	/**
	 * Returns all the shortcut nodes referenced by this node.
	 * @return Vector of all the shortcut nodes referenced by this node.
	 * @exception java.sql.SQLException
	 * @exception java.sql.ModelSessionException
	 */
	public Vector getShortCutNodes() throws SQLException, ModelSessionException {

		Vector nodes = new Vector(51);

		if (oModel == null)
			throw new ModelSessionException("Model is null in NodeSummary.getShortCutNodes");
		if (oSession == null) {
			oSession = oModel.getSession();
			if (oSession == null)
				throw new ModelSessionException("Session is null in NodeSummary.getShortCutNodes");
		}

		// update db
		INodeService ns = oModel.getNodeService() ;
		nodes = ns.getShortCutNodes(oSession, sId) ;

		return nodes;
	}

	/**
	 * Returns the number of shortcut nodes referenced by this node.
	 *
	 * @return int, the number of shortcut nodes referenced by this node.
	 */
	public int getNumberOfShortCutNodes() {
		return htShortCutNodes.size();
	}

	/**
	 *	Sets the reference source and image pointed to by this node, both locally and in the DATABASE.
	 *
	 *	@param source the path of the external reference file or url attached to this node.
	 *	@param image the image used for the icon of this node.
	 *  @param sLastModAuthor the author name of the person who made this modification.
	 *	@exception java.sql.SQLException
	 *	@exception java.sql.ModelSessionException
	 */
	public void setSource(String source, String image, String sLastModAuthor) throws SQLException, ModelSessionException {

		if (oModel == null)
			throw new ModelSessionException("Model is null in NodeSummary.getSource");
		if (oSession == null) {
			oSession = oModel.getSession();
			if (oSession == null)
				throw new ModelSessionException("Session is null in NodeSummary.setSource");
		}

		Date date = new Date();
		setModificationDateLocal(date);
		setLastModificationAuthorLocal(sLastModAuthor);		
				
		oModel.getNodeService().setReference(oSession, getId(), source, image, date, sLastModAuthor);

		if (!sSource.equals(source)) {
			firePropertyChange(SOURCE_PROPERTY, sSource, source);
		}
		if (!sImage.equals(image)) {
			firePropertyChange(IMAGE_PROPERTY, sImage, image);
		}

		sSource = source;
		sImage = image;
	}

	/**
	 *	Sets the reference source and image pointed to by this node, both locally and in the DATABASE.
	 *
	 *	@param source the path of the external reference file or url attached to this node.
	 *	@param image the image used for the icon of this node.
	 *	@param oImageSize the size (width and height) to draw the image (0,0 means thumbnail image as usual).
	 *  @param sLastModAuthor the author name of the person who made this modification.
	 *	@exception java.sql.SQLException
	 *	@exception java.sql.ModelSessionException
	 */
	public void setSource(String source, String image, Dimension oImageSize, String sLastModAuthor) throws SQLException, ModelSessionException {

		if (oModel == null)
			throw new ModelSessionException("Model is null in NodeSummary.getSource");
		if (oSession == null) {
			oSession = oModel.getSession();
			if (oSession == null)
				throw new ModelSessionException("Session is null in NodeSummary.setSource");
		}

		Date date = new Date();
		setModificationDateLocal(date);
		setLastModificationAuthorLocal(sLastModAuthor);		
				
		oModel.getNodeService().setReference(oSession, getId(), source, image, oImageSize, date, sLastModAuthor);

		if (!sSource.equals(source)) {
			firePropertyChange(SOURCE_PROPERTY, sSource, source);
		}
		if (!sImage.equals(image)) {
			firePropertyChange(IMAGE_PROPERTY, sImage, image);
		}

		if (oImageSize.width != nImageWidth || oImageSize.height != nImageHeight) {
			Dimension oldDim = new Dimension(nImageWidth, nImageHeight);	
			nImageHeight = oImageSize.height;
			nImageWidth = oImageSize.width;
			firePropertyChange(IMAGE_SIZE_PROPERTY, oldDim, oImageSize);
		}
		
		sSource = source;
		sImage = image;
	}
	
	/**
	 *	Sets the width and height for the image associated with this node.
	 *
	 *	@param nWidth the width of the image.
	 *	@param nHeight the height of the image
	 *  @param sLastModAuthor the author name of the person who made this modification.
	 *	@exception java.sql.SQLException
	 *	@exception java.sql.ModelSessionException
	 */
	public void setImageSize(int nWidth, int nHeight, String sLastModAuthor) throws SQLException, ModelSessionException {
		setImageSize(new Dimension(nWidth, nHeight), sLastModAuthor);
	}
	
	/**
	 *	Sets the dimension (width and height) for the image associated with this node locally and in the database.
	 *
	 *	@param oSize the dimension of the image.
	 *  @param sLastModAuthor the author name of the person who made this modification.
	 *	@exception java.sql.SQLException
	 *	@exception java.sql.ModelSessionException
	 */
	public void setImageSize(Dimension oSize, String sLastModAuthor) throws SQLException, ModelSessionException {

		if (oModel == null)
			throw new ModelSessionException("Model is null in NodeSummary.getSource");
		if (oSession == null) {
			oSession = oModel.getSession();
			if (oSession == null)
				throw new ModelSessionException("Session is null in NodeSummary.setSource");
		}

		if (oSize.width != nImageWidth || oSize.height != nImageHeight) {
		
			Date date = new Date();
			setModificationDateLocal(date);
			setLastModificationAuthorLocal(sLastModAuthor);		
					
			oModel.getNodeService().setImageSize(oSession, getId(), oSize, date, sLastModAuthor);
	
			Dimension oldDim = new Dimension(nImageWidth, nImageHeight);	
			nImageHeight = oSize.height;
			nImageWidth = oSize.width;
			firePropertyChange(IMAGE_SIZE_PROPERTY, oldDim, oSize);
		}
	}	

	/**
	 *	Sets the image path for this node locally only.
	 *
	 *	@param String image, the image path for this node.
	 */
	public void setLocalImage(String image)  {
		sImage = image;
	}

	/**
	 *	Sets the reference source path for this node locally only.
	 *
	 *	@param String source, the source of the reference for this node.
	 */
	public void setLocalSource(String source)  {
		sSource = source;
	}

	/**
	 *	Sets the dimension (width and height) for the image associated with this node locally only.
	 *
	 *	@param oSize the dimension of the image.
	 */
	public void setLocalImageSize(Dimension oSize)  {
		nImageHeight = oSize.height;
		nImageWidth = oSize.width;
	}		
	
	/**
	 *	Gets the source String representing the reference pointed to by this node
	 *
	 *	@return String, the source of reference.
	 */
	public String getSource() {
		return sSource;
	}

	/**
	 *	Gets the image String representing the image linked to by this node.
	 *
	 *	@return the image of reference
	 */
	public String getImage() {
		return sImage;
	}

	/**
	 *	Gets the image size for the image linked to by this node.
	 *
	 *	@return the image size
	 */
	public Dimension getImageSize() {
		return new Dimension(nImageWidth, nImageHeight);
	}

	/**
	 * Returns a string representation of this node.
	 *
	 * @param String, a string representation of this node.
	 */
	public String toString() {
		return super.toString() + " Id is " + sId;
	}
}
