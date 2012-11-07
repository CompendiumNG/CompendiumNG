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

import com.compendium.core.ICoreConstants;

/**
 * The IdObject is the generic compendium object
 * used as the basis for any object that requires a unique
 * identification.
 * <p>
 *
 * @author	rema and sajid / Michelle Bachler
 */
public class IdObject extends PCObject implements IIdObject, java.io.Serializable {

	/** The unique id of this object.*/
	protected String	sId						= "";

	/**
	 * NOTE: The permission system has not been implemented yet.
	 *
	 * This property is the permission level that is assigned to this object FOR THIS CLIENT.
	 * So this value is unique for this object for different clients.
	 * The various possible values are:
	 * No access, read, write , readviewnode, writeview, writeviewnode : refer ICoreConstants class
	 */
	protected int 		nPermission 			= ICoreConstants.NOACCESS;

	/** The creation date of this object.*/
	protected Date		oCreationDate			= null;

	/** The modification date of this object.*/
	protected Date		oModificationDate		= null;

	/** The author of this object.*/
	protected String	oAuthor					= null;


	/**
	 * Constructor, does nothing.
	 */
	public IdObject() {}

	/**
	 * Constructor, creates a new identifier object with
	 * the given unique identifier, and permission.
	 * The creation and modification dates are set ot the current time.
	 * The author is set as an empty String.
	 *
	 * @param String is, the unique idetifier of this object.
	 * @param int permission, the permission level on this object - NOT CURRENTLY USED.
	 */
	public IdObject(String id, int permission) {
		this(id, permission, "", new Date(), new Date());
	}

	/**
	 *	Constructor, creates a new identifier object
	 *	with the unique id,  author, creation date and modification date
	 *
	 * @param String is, the unique idetifier of this object.
	 * @param String author, the author of this object.
	 * @param Date creationDate, the date this object was created.
	 * @param Date modificationDate, the date this object was last modified.
	 */
	public IdObject(String id, String author, Date creationDate, Date modificationDate) {
		oCreationDate = creationDate ;
		oModificationDate = modificationDate ;
		oAuthor = author ;
		sId = id ;
	}

	/**
	 *	Constructor, creates a new identifier object
	 *	with the unique id, permission, and author, creation date and modification date
	 *
	 * @param String is, the unique idetifier of this object.
	 * @param int permission, the permission level on this object - NOT CURRENTLY USED.
	 * @param String author, the author of this object.
	 * @param Date creationDate, the date this object was created.
	 * @param Date modificationDate, the date this object was last modified.
	 */
	public IdObject(String id, int permission, String author, Date creationDate, Date modificationDate) {
		oCreationDate = creationDate ;
		oModificationDate = modificationDate ;
		oAuthor = author;
		sId = id ;
		nPermission = permission ;
	}

	/**
	 * The initialize method is a constructor-like method that needs to be called
	 * to initialize the object to operate on the client side cache
	 *
	 * @param PCSession session, the session identifier for this object.
	 * @param IModel model, the current model in use with this object.
	 */
	public void initialize(PCSession session, IModel model) {
		// the super method initializes these values, and creates property change support for the class.
		super.initialize(session, model);
	}

	/**
	 *	This method is a method to be called to free all resources used by the object
	 *	before releasing it from the cache
	 */
	public void cleanUp() {
		super.cleanUp() ;
	}

    /**
	 * Returns the unique identifier of this object
	 *
	 * @return String, the object's unique identifier
	 */
	public String getId() {
		return sId;
	}

	/**
	 * Sets the unique identifier of this object
	 * This is a SPECIAL operation to be used only when a new object is being generated
	 * Id values cannot be propagated as property changes.
	 *
	 * @param String id, the unique identifier of this object.
	 */
	public void setId(String id) {
		sId = id;
	}

    /**
	 * NOTE: The permission system has not been implemented yet.
	 *
	 * Returns the permission of this object
	 *
	 * @return the objects permission
	 */
	public int getPermission() {
		return nPermission;
	}

	/**
	 * NOTE: The permission system has not been implemented yet.
	 *
	 * Sets the parmission level of this object.
	 *
	 * @param int permission, the level of permissions on this object.
	 */
	public void setPermission(int permission) {

		if (permission == nPermission)
			return;

		setPermissionLocal(permission);
	}

	/**
	 * NOTE: The permission system has not been implemented yet.
	 *
	 * Set the permission property of the object. Will eventual fire an event.
	 *
	 *	@param permission The integer permission value
	 */
	protected void setPermissionLocal(int permission) {
		if (permission == nPermission)
			return ;
		nPermission = permission;
	}

	/**
	 * Set the author value in this object
	 *
	 * @param String Author, the author of this object.
	 */
	public void setAuthorLocal(String author) {
		oAuthor = author;
	}

	/**
	 * Returns the author of this object.
	 *
	 * @return String, representing the author of this object.
	 */
	public String getAuthor() {
		return oAuthor;
	}

	/**
	 * Sets the date when this object was created.
	 *
	 * @param Date date, the date this object was created.
	 */
	public void setCreationDateLocal(Date date) {
		oCreationDate = date ;
	}

	/**
	 * Returns the creation date of this object.
	 *
	 * @return Date, the date this object was created.
	 */
	public Date getCreationDate() {
		return oCreationDate;
	}

	/**
	 * Sets the last modification date of this object.
	 *
	 * @param Date date, the date this object was last modified.
	 */
	public void setModificationDateLocal(Date date) {
		oModificationDate = date;
	}

	/**
	 * Returns the last modification date of this object.
	 *
	 * @return Date, the date this object was last modified.
	 */
	public Date getModificationDate() {
		return oModificationDate;
	}
}
