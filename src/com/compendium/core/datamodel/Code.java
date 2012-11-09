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
import java.sql.SQLException;

import com.compendium.core.ICoreConstants;
import com.compendium.core.datamodel.services.*;

/**
 * The Code object represents a tag in Project Compendium.
 * Tags are additional information elements attached to a particular node.
 * They allow you to notionally group nodes with similar ideas or concepts.
 * You can then use these tags to search for all nodes with certain tags.
 * This is useful, for example, in investigating node and concept relationships between various views.
 *
 * @author rema and sajid / Michelle Bachler
 */
public class Code extends IdObject implements ICode, java.io.Serializable{

	/** Name property name for use with property change events */
	public final static String NAME_PROPERTY				= "name";

	/** Description property name for use with property change events */
	public final static String DESCRIPTION_PROPERTY = "description";

	/** Description property name for use with property change events */
	public final static String BEHAVIOR_PROPERTY = "behavior";

	/** String holding the code name */
	protected String		sName			= "";

	/** String holding the code description */
	protected String		sDescription	= "";

	/** String holding the code behavior*/
	protected String		sBehavior		= "";

	/** Holds a list of all nodes that this code has been attached to */
	protected Hashtable		htNodes	= new Hashtable(51);

	/** A static list of Code object already created in this session.*/
	private static Vector 	codeSummaryList 		= new Vector();
		
	/**
	 * Constructor, creates a new code with the given id, author, creation date, modification date,
	 * identifying name, description, and behavior.
	 */
	public Code(String codeId, String author, Date creationDate, Date modificationDate,
												String name, String description, String behavior) {

		super(codeId, author, creationDate, modificationDate);
		sName = name ;
		sDescription = description ;
		sBehavior = behavior;
	}

	/**
	 * Constructor, creates a new code with the given id, author, creation date, modification date,
	 * identifying name, description, and behavior, and associated nodes Vector.
	 *
	 */
	/*public Code(String codeId, String author, Date creationDate, Date modificationDate,
												String name, String description, String behavior, Vector nodes) {

		super(codeId, author, creationDate, modificationDate);
		sName = name ;
		sDescription = description ;
		sBehavior = behavior;

		int count = nodes.size();
		for (int i=0; i<count; i++) {
			NodeSummary node = (NodeSummary)nodes.elementAt(i);
			if (node != null)
				htNodes.put(node.getId(), node);
		}
	}*/
	
	/**
	 * 
	 * @param sCodeID the id for this code.
	 * @param author the author for this code.
	 * @param creationDate the date this code was created.
	 * @param modificationDate the dat this code was last modified.
	 * @param name the name of this code.
	 * @param description the description of this code.
	 * @param behavior the behaviour of this code.
	 * @return a new Code instance
	 */	
	public static Code getCode(String sCodeID, String author, Date creationDate, Date modificationDate,
			String name, String description, String behavior) {

		int i = 0;
		for (i = 0; i < codeSummaryList.size(); i++) {
			if (sCodeID.equals(((Code)codeSummaryList.elementAt(i)).getId())) {
				break;
			}
		}

		Code code = null;
		if (i == codeSummaryList.size()) {
			code= new Code(sCodeID, author, creationDate, modificationDate, name, description, behavior);
			codeSummaryList.addElement(code);
		}
		else {
			code = (Code)codeSummaryList.elementAt(i);

			// UPDATE THE DETAILS
			code.setAuthorLocal(author);
			code.setCreationDateLocal(creationDate);
			code.setModificationDateLocal(modificationDate);
			code.setNameLocal(name);			
			code.setDescriptionLocal(description);
			code.setBehaviorLocal(behavior);
		}
		return code;
	}

	/**
	 * 
	 * @param sCodeID the id for this code.
	 * @param author the author for this code.
	 * @param creationDate the date this code was created.
	 * @param modificationDate the dat this code was last modified.
	 * @param name the name of this code.
	 * @param description the description of this code.
	 * @param behavior the behaviour of this code.
	 * @param nodes the nodes this code is in.
	 * @return a new Code instance
	 */	
	/*public static Code getCode(String sCodeID, String author, Date creationDate, Date modificationDate,
			String name, String description, String behavior, Vector vtNodes) {

		int i = 0;
		for (i = 0; i < codeSummaryList.size(); i++) {
			if (sCodeID.equals(((Code)codeSummaryList.elementAt(i)).getId())) {
				break;
			}
		}

		Code code = null;
		if (i == codeSummaryList.size()) {
			code= new Code(sCodeID, author, creationDate, modificationDate, name, description, behavior, vtNodes);
			codeSummaryList.addElement(code);
		}
		else {
			code = (Code)codeSummaryList.elementAt(i);

			// UPDATE THE DETAILS
			code.setAuthorLocal(author);
			code.setCreationDateLocal(creationDate);
			code.setModificationDateLocal(modificationDate);
			code.setNameLocal(name);			
			code.setDescriptionLocal(description);
			code.setBehaviorLocal(behavior);
			
			int count = vtNodes.size();
			htNodes.clear();
			for (int j=0; j<count; j++) {
				NodeSummary node = (NodeSummary)vtNodes.elementAt(j);
				if (node != null)
					htNodes.put(node.getId(), node);
			}
			
		}
		return code;
	}*/

	/**
	 * Clear the list of all codes created / used in this session.
	 */
	public static void clearList() {
		codeSummaryList.removeAllElements();
	}
	
	/**
	 * The initialize method is called by the Model before adding the object to the cache.
	 *
	 * @param PCSession session, the session associated with this object.
	 * @param IMode model, the model this object belongs to.
	 */
	public void initialize(PCSession session,IModel model) {
		super.initialize(session, model) ;
	}

	/**
	 *	Clean up the variables used by this object to free memory
	 */
	public void cleanUp() {
		super.cleanUp() ;
	}

	/**
	 * Returns the identifier (unique name) of the code
	 *
	 * @return the name of the code
	 */
	public String getName() {
		return sName ;
	}

	/**
	 * Updates the unique name of the code both locally and in the database.
	 *
	 * @param name The name of the code
	 * @exception java.sql.SQLExcpetion
	 * @exception ModelSessionException
	 */
	public void setName(String sNewName) throws SQLException, ModelSessionException {

		if (sNewName.equals(sName))
			return;

		if (oModel == null)
			throw new ModelSessionException("Model is null in Code.setName");
		if (oSession == null) {
			oSession = oModel.getSession();
			if (oSession == null)
				throw new ModelSessionException("Session is null in Code.setName");
		}

		ICodeService cs = oModel.getCodeService();
		cs.setName(oSession, sId, sNewName, new Date());

		// call the setNameLocal method to fire changes locally
		setNameLocal(sNewName);
	}

	/**
	 *	Sets the name property of the class locally, and fires property changes to local listeners
	 *
	 *	@param name the new name property value
	 *	@return String value of old name
	 */
	protected String setNameLocal(String sNewName) {

		if (sNewName.equals(sName))
			return sName;

		String oldValue = sName;
		sName = sNewName;
		firePropertyChange(NAME_PROPERTY, oldValue, sNewName);
		return oldValue;
	}

	/**
	 * Returns a description about the meaning of the code
	 *
	 * @return The description about the meaning of the code
	 */
	public String getDescription() {
		return sDescription ;
	}

	/**
	 * Updates the description defining the meaning of the code both locally and in the database
	 *
	 * @param description The description about the meaning of the code
	 * @exception java.sql.SQLExcpetion
	 * @exception ModelSessionException
	 */
	public void setDescription(String sDescription) throws SQLException, ModelSessionException {

		if (sDescription.equals(sDescription)	)
			return ;

		if (oModel == null)
			throw new ModelSessionException("Model is null in Code.setDescription");
		if (oSession == null) {
			oSession = oModel.getSession();
			if (oSession == null)
				throw new ModelSessionException("Session is null in Code.setDescription");
		}

		ICodeService cs = oModel.getCodeService();
		cs.setDescription(oSession, sId, sDescription, new Date());

		setDescriptionLocal(sDescription);
	}

	/**
	 *	Sets the description property of the code locally, and fires property change to local listeners
	 *
	 *	@param String description
	 *	@return String value of old Description
	 */
	protected String setDescriptionLocal(String description) {

		if (description.equals(sDescription))
			return sDescription;

		String oldValue = sDescription ;
		sDescription = description ;
		firePropertyChange(DESCRIPTION_PROPERTY, oldValue, sDescription);
		return oldValue ;
	}

	/**
	 * Returns the behavior of the code.
	 * NOTE: The concept of code behavior has not been implemented yet.
	 *
	 * @return String, a string representing the behvior of the code.
	 */
	public String getBehavior() {
		return sBehavior ;
	}

	/**
	 * Updates the behavior of the code both locally and in the database.
	 * NOTE: The concept of code behavior has not been implemented yet.
	 *
	 * @param String behavior, the behavior to set.
	 * @exception java.sql.SQLExcpetion
	 * @exception ModelSessionException
	 */
	public void setBehavior(String sBehavior) throws SQLException, ModelSessionException {

		if (sBehavior.equals(sBehavior))
			return ;

		if (oModel == null)
			throw new ModelSessionException("Model is null in Code.setBehaviour");
		if (oSession == null) {
			oSession = oModel.getSession();
			if (oSession == null)
				throw new ModelSessionException("Session is null in Code.setBehaviour");
		}

		ICodeService cs = oModel.getCodeService();
		cs.setBehavior(oSession, sId, sBehavior, new Date());

		setBehaviorLocal(sBehavior);
	}

	/**
	 *	Sets the beahvior property of the bean locally and fires the change to local property change listeners
	 *
	 *	@param behavior The String behavior property value
	 *	@return String, the old Value
	 */
	protected String setBehaviorLocal(String behavior) {

		if (behavior.equals(sBehavior))
			return sBehavior;

		String oldValue = sBehavior ;
		sBehavior = behavior ;
		firePropertyChange(BEHAVIOR_PROPERTY, oldValue, sBehavior);
		return oldValue ;
	}

	/**
	 * Adds a reference to the given node. The given node references
	 * this code.
	 * <p>
	 * This method is not to be called directly. This method will be
	 * called by the addCode method defined in Node</p>
	 *
	 * @param node The node referencing this code
	 * @return true if the node was successfully added, false otherwise
	 * @see com.compendium.core.datamodel.NodeSummary#addCode
	 */
	/*protected boolean addNode(INodeSummary node) {
		if (htNodes.containsKey(node.getId()))
			return false;

		htNodes.put(node.getId(), node);
		return true;
	}*/

	/**
	 * Removes the node.
	 * <p>
	 * This method is not to be called directly. This method will be
	 * called by the removeCode method defined in Node</p>
	 *
	 * @param node The node to be removed
	 * @return if was successfully removed
	 */
	/*protected boolean removeNode(NodeSummary node)	throws NoSuchElementException {
		return removeNode(node.getId());
	}*/

	/**
	 * Removes the node with the given id.
	 * <p>
	 * This method is not to be called directly. This method will be
	 * called by the removeCode method defined in Node</p>
	 *
	 * @param id The id of the node to be removed
	 * @return true if the node was successfully removed
	 */
	/*protected boolean removeNode(String id) {

		if (htNodes.containsKey(id))
			return false;

		htNodes.remove(id);
		return true;
	}*/

	/**
	 * Returns all the nodes that have a reference to this code.
	 *
	 * @return a Vector of the nodes that have a reference to this code
	 */
	/*public Vector getNodes() {
		Vector nodes = new Vector(51);
		for (Enumeration e = htNodes.elements(); e.hasMoreElements();) {
			nodes.addElement((INodeSummary)e.nextElement());
		}
		return nodes;
	}*/
}
