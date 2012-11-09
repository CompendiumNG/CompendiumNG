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

/**
 * The Node object represents a hyperlinkable node that
 * has as its minimum properties a type, label and detailed description.
 *
 * @author	rema and sajid / Michelle Bachler
 */
public interface INodeSummary extends IIdObject {

	/**
	 * The initialize method is called by the Model before adding the object to the cache.
	 * Also, load all the codes associated with this node.
	 *
	 * @param PCSession session, the session associated with this object.
	 * @param IMode model, the model this object belongs to.
	 */
	public void initialize(PCSession session, IModel model);

	/**
	 * Returns the object's original id. When
	 * importing maps we need to make sure
	 * that objects with the same id end up
	 * as the same object.
	 *
	 * @return String, the original imported identifier of this object.
	 */
	public String getOriginalID();

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
	public void setOriginalID(String sOriginalID, String sLastModAuthor) throws SQLException, ModelSessionException;

	/**
	 * Returns the node type.
	 * @return int, the node type.
	 */
	public int getType();

	/**
	 * Sets the node type, both locally and in the DATABASE.
	 *
	 * @param int type the node type.
	 * @param sLastModAuthor the author name of the person who made this modification. 
	 * @exception java.sql.SQLException
	 * @exception java.sql.ModelSessionException
	 */
	public void setType(int type, String sLastModAuthor) throws SQLException, ModelSessionException;

	/**
	 *	Returns the extended type for the node - NOT CURRENTLY USED.
	 *	@return String, the extended type for the node.
	 */
	public String getExtendedNodeType();

 	/**
	 *	Sets the extended node typefor the node, both locally and in the DATABASE.
	 *
	 *	@param String name, the extended node type of the node.
	 * 	@param sLastModAuthor the author name of the person who made this modification.
	 *	@exception java.sql.SQLException
	 *	@exception java.sql.ModelSessionException
	 */
	public void setExtendedNodeType(String name, String sLastModAuthor) throws SQLException, ModelSessionException;

	/**
	 * 	Sets the creation date for this node, both locally and in the DATABASE.
	 *
	 * 	@param Date creation is the node creation date.
	 * 	@param sLastModAuthor the author name of the person who made this modification. 
	 * 	@exception java.sql.SQLException
	 * 	@exception java.sql.ModelSessionException
	 */
	public void setCreationDate(Date creation, String sLastModAuthor) throws SQLException, ModelSessionException;

	/**
	 * Sets the author for this node, both locally and in the DATABASE.
	 *
	 * @param String sAuthor, the author of this node.
	 * @param sLastModAuthor the author name of the person who made this modification.  
	 * @exception java.sql.SQLException
	 * @exception java.sql.ModelSessionException
	 */
	public void setAuthor(String sAuthor, String sLastModAuthor) throws SQLException, ModelSessionException;

 	/**
	 * Returns the Node STATE: not read (0) read (1), modified since last read (2).
	 * <p>
	 * NOT CURRENTLY USED.
	 *
	 *	@return int, the state value.
	 */
	public int getState();

 	/**
 	 *	Sets the Node STATE Variable, both locally and in the DATABASE.
	 *
	 *	@param int state, the int state value: not read (0) read (1), modified since last read (2).
	 *	@exception java.sql.SQLException
	 *	@exception java.sql.ModelSessionException
	 */
	public void setState(int state) throws SQLException, ModelSessionException;

	/**
	 * Return the node's label.
	 * @param String, the label of this node.
	 */
	public String getLabel();

	/**
	 * Sets the label of this node, both locally and in the DATABASE.
	 *
	 * @param String label, The label of this node .
	 * @param sLastModAuthor the author name of the person who made this modification.   
	 * @exception java.sql.SQLException
	 * @exception java.sql.ModelSessionException
	 */
	public void setLabel(String label, String sLastModAuthor) throws SQLException, ModelSessionException;

	/**
	 * Return the node's label.
	 * @param String, the label of this node.
	 */
	public String getLastModificationAuthor();
	
	/**
	 *	Returns the first detail page of this node.
	 *
	 *	@return String, the first detail page of this node.
	 */
	public String getDetail();

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
	public void setDetail(String detail, String sAuthor, String sLastModAuthor) throws SQLException, ModelSessionException;

	/**
	 * Gets all the detail pages of this node.
	 *
	 * @return Vector, of <code>NodeDetailPage</code> objects for the detail pages of this node.
	 * @exception java.sql.SQLException
	 * @exception java.sql.ModelSessionException
	 */
	public Vector getDetailPages(String sAuthor) throws SQLException, ModelSessionException;

	/**
	 * Set the detail pages of this node, both locally and in the DATABASE.
	 *
	 * @return Vector, of <code>NodeDetailPage</code> objects for the detail pages of this node.
	 * @param sLastModAuthor the author name of the person who made this modification.
	 * @exception java.sql.SQLException
	 * @exception java.sql.ModelSessionException
	 */
	public void setDetailPages(Vector pages, String sAuthor, String sLastModAuthor) throws SQLException, ModelSessionException;

	/**
	 * Returns true if this node is contained in mulitple views
	 *
	 * @return boolean, true if this node is contained in mulitple views
	 */
	public boolean isInMultipleViews();

	/**
	 * Return a count of the number of views this node is in.
	 */
	public int getViewCount();

	/**
	 * Finds out from the Database how many view this node is now in and upadte local count.
	 * @return boolean, true if this node is in more than one view, else false.
	 */
	public boolean updateMultipleViews();

	/**
	 *	Add the code to the given NodeSummary, both locally and in the DATABASE.
	 *
	 *	@param Code code, the Code Reference to be added to the NodeSummary.
	 *	@return boolean, true if successfully added, else false.
	 *	@exception java.util.NoSuchElementException
	 *	@exception java.sql.SQLException
	 *	@exception java.sql.ModelSessionException
	 */
	public boolean addCode(Code code) throws NoSuchElementException, SQLException, ModelSessionException;

	/**
	 *	Add codes to the given NodeSummary, both locally and in the DATABASE.
	 *
	 *	@param Vector codes, a list of codes to be added to the NodeSummary.
	 *	@return boolean, true if successfully added, else false.
	 *	@exception java.util.NoSuchElementException
	 *	@exception java.sql.SQLException
	 *	@exception java.sql.ModelSessionException
	 */
	public boolean addCodes(Vector codes) throws NoSuchElementException, SQLException, ModelSessionException;

	/**
	 * Removes the reference to the code with the given name, both locally and from the DATABASE.
	 *
	 * @param Code code,  the code to be removed.
	 * @return boolean true if it was successfully removed, else false.
	 * @exception java.util.NoSuchElementException
	 * @exception java.sql.SQLException
	 * @exception java.sql.ModelSessionException
	 */
	public boolean removeCode(Code code) throws NoSuchElementException, SQLException, ModelSessionException;

	/**
	 * Returns all the codes referenced by this node.
	 *
	 * @return an Enumeration of all the codes referenced by this node.
	 */
	public Enumeration getCodes() throws SQLException, ModelSessionException;

	/**
	 *  Load all the codes referenced by this node, from the DATABASE.
	 *
	 *	@exception java.sql.SQLException
	 *	@exception java.sql.ModelSessionException
	 */
	public void loadCodes() throws SQLException, ModelSessionException;

	/**
	 * Return if this node has the given code.
	 *
	 * @param sCodeName, the name to check for.
	 */
	public boolean hasCode(String sCodeName);

	/**
	 *	Add the shortcut to the given NodeSummary, both locally and in the DATABASE.
	 *
	 *	@param NodeSummary shortcutnode, the shortcut node to add.
	 *	@exception java.sql.SQLException
	 *	@exception java.sql.ModelSessionException
	 */
	public boolean addShortCutNode(NodeSummary node) throws NoSuchElementException, SQLException, ModelSessionException;

	/**
	 * Returns all the shortcut nodes referenced by this node.
	 *
	 * @return Vector, of all the shortcut nodes referenced by this node.
	 * @exception java.sql.SQLException
	 * @exception java.sql.ModelSessionException
	 */
	public Vector getShortCutNodes() throws SQLException, ModelSessionException;

	/**
	 * Returns the number of shortcut nodes referenced by this node.
	 *
	 * @return int, the number of shortcut nodes referenced by this node.
	 */
	public int getNumberOfShortCutNodes();

	/**
	 *	Sets the reference source and image pointed to by this node, both locally and in the DATABASE.
	 *
	 *	@param String source, the path of the external reference file or url attached to this node.
	 *	@param String image, the image used for the icon of this node.
	 *  @param sLastModAuthor the author name of the person who made this modification.*
	 *	@exception java.sql.SQLException
	 *	@exception java.sql.ModelSessionException
	 */
	public void setSource(String source, String image, String sLastModAuthor) throws SQLException, ModelSessionException;

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
	public void setSource(String source, String image, Dimension oImageSize, String sLastModAuthor) throws SQLException, ModelSessionException;
	
	/**
	 *	Gets the source String representing the reference pointed to by this node
	 *
	 *	@return String, the source of reference.
	 * 	@exception java.sql.SQLException
	 */
	public String getSource();

	/**
	 *	Gets the image String representing the image linked to by this node.
	 *
	 *	@return the image of reference
	 *	@exception java.sql.SQLException
	 */
	public String getImage();
	
	/**
	 *	Sets the width and height for the image associated with this node.
	 *
	 *	@param nWidth the width of the image.
	 *	@param nHeight the height of the image
	 *  @param sLastModAuthor the author name of the person who made this modification.
	 *	@exception java.sql.SQLException
	 *	@exception java.sql.ModelSessionException
	 */
	public void setImageSize(int nWidth, int nHeight, String sLastModAuthor) throws SQLException, ModelSessionException;
	
	/**
	 *	Sets the dimension (width and height) for the image associated with this node locally and in the database.
	 *
	 *	@param oSize the dimension of the image.
	 *  @param sLastModAuthor the author name of the person who made this modification.
	 *	@exception java.sql.SQLException
	 *	@exception java.sql.ModelSessionException
	 */
	public void setImageSize(Dimension oSize, String sLastModAuthor) throws SQLException, ModelSessionException;	

	/**
	 *	Gets the image size for the image linked to by this node.
	 *
	 *	@return the image size
	 */
	public Dimension getImageSize();
}
