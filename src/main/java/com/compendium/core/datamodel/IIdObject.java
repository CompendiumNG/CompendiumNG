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

import java.util.Date;

/**
 * The IdObject is the generic project compendium object
 * used as the basis for any object that requires a unique
 * identification.
 * <p>
 *
 * @author	rema and sajid /  Michelle Bachler
 */
public interface IIdObject extends IPCObject, java.io.Serializable{

	/**
	 * NOTE: The permission system has not been implemented yet.
	 *
	 * Permission property name for use with property change events
	 */
	public final static String PERMISSION_PROPERTY = "permission" ;

	/** Identifier property name for use with property change events */
	public final static String ID_PROPERTY					= "id";

    /**
	 * Returns the unique identifier of this object
	 *
	 * @return String, the object's unique identifier
	 */
	public String getId() ;

	/**
	 * Sets the unique identifier of this object
	 * This is a SPECIAL operation to be used only when a new object is being generated
	 * Id values cannot be propagated as property changes.
	 *
	 * @param String id, the unique identifier of this object.
	 */
	public void setId(String id) ;

    /**
	 * NOTE: The permission system has not been implemented yet.
	 *
	 * Returns the permission of this object
	 *
	 * @return the objects permission
	 */
	public int getPermission() ;

	/**
	 * NOTE: The permission system has not been implemented yet.
	 *
	 * Sets the parmission level of this object.
	 *
	 * @param int permission, the level of permissions on this object.
	 */
	public void setPermission(int permission) ;

	/**
	 * Returns the author of this object.
	 *
	 * @return String, representing the author of this object.
	 */
	public String getAuthor() ;

	/**
	 * Set the author value in this object
	 *
	 * @param String Author, the author of this object.
	 */
	public void setAuthorLocal(String author) ;

	/**
	 * Returns the creation date of this object.
	 *
	 * @return Date, the date this object was created.
	 */
	public Date getCreationDate() ;

	/**
	 * Sets the date when this object was created.
	 *
	 * @param Date date, the date this object was created.
	 */
	public void setCreationDateLocal(Date date) ;

	/**
	 * Returns the last modification date of this object.
	 *
	 * @return Date, the date this object was last modified.
	 */
	public Date getModificationDate() ;

	/**
	 * Sets the last modification date of this object.
	 *
	 * @param Date date, the date this object was last modified.
	 */
	public void setModificationDateLocal(Date date) ;
}
