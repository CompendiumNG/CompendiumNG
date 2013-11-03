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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * The Favorite object holds information about a user favorite / Bookmark item.
 * Note: This is a holding container only and does not currently write to the database
 *
 * @author	Michelle Bachler
 */
public class Favorite implements java.io.Serializable {
	/**
	 * class's own logger
	 */
	final Logger log = LoggerFactory.getLogger(getClass());
	//public final static long 	serialVersionUID	=	
	
	/** The user id of the user who created this Favorite record.*/
    private String			sUserID							= "";

	/** The View id for this Favorite record.*/
    private String			sViewID							= "";

	/** The Node id for this Favorite record.*/
    private String			sNodeID							= "";

	/** The node label.*/
    private String 			sLabel							= "";
	
	/** The type of the favorite node.*/
    private int 				nType							= -1;
	
	/** The date the favorite was created.*/
    private Date				dCreationDate					= null;
	
	/** The date the favorite was modified. NOT USED */
    private Date				dModificationDate				= null;

	/**
	 * Creates a new empty Favorite object.
	 */
	public Favorite() {}

	/**
	 * Create a new Favorite instance initalized with the passed parameters.
	 * @param sUserID the id of the user who created this favorite
	 * @param sNodeID the node id associated with the favorite
	 * @param sViewID the view id associated with the favorite
	 * @param sLabel the label associated with the favorite
	 * @param nType the node type associated with the favorite
	 * @param creation the creation date associated with the favorite
	 * @param modification the modification date associated with the favorite
	 */
	public Favorite(String sUserID, String sNodeID, String sViewID, String sLabel, int nType, Date creation, Date modification) {
		this.sUserID = sUserID;
		this.sViewID = sViewID;
		this.sNodeID = sNodeID;
		this.sLabel = sLabel;
		this.nType = nType;
		this.dCreationDate = creation;
		this.dModificationDate = modification;		
	}

	/**
	 * Returns the User id associated with this View
	 *
	 * @return the String of the User id associated with this View
	 */
	public String getUserID() {
		return sUserID;
	}

	/**
	 * Sets the View id object associated with this Favorite
	 *
	 * @param sUserID the User id associated with this Favorite
	 */
	public void setUserID(String sUserID) {
		this.sUserID = sUserID;
	}

	/**
	 * Returns the View id associated with this Favorite
	 *
	 * @return the String of the View id associated with this Favorite
	 */
	public String getViewID() {
		return sViewID;
	}

	/**
	 * Sets the View id object associated with this Favorite
	 *
	 * @param sViewID the View id associated with this Favorite
	 */
	public void setViewID(String sViewID) {
		this.sViewID = sViewID;
	}	

	/**
	 * Sets the Node id object associated with this Favorite
	 *
	 * @param sNodeID the Node id associated with this Favorite
	 */
	public void setNodeD(String sNodeID) {
		this.sNodeID = sNodeID;
	}

	/**
	 * Returns the label associated with this Favorite
	 *
	 * @return the Label of the Node associated with this Favorite
	 */
	public String getLabel() {
		return sLabel;
	}

	/**
	 * Sets the label associated with this Favorite
	 *
	 * @param sLabel the label associated with this Favorite
	 */
	public void setLabel(String sLabel) {
		this.sLabel = sLabel;
	}

	/**
	 * Returns the Node id associated with this Favorite
	 *
	 * @return the String of the Node id associated with this Favorite
	 */
	public String getNodeID() {
		return sNodeID;
	}	
	/**
	 * Returns the node type
	 *
	 * @return the node type
	 */
	public int getType() {
		return nType;
	}

	/**
	 * Sets the node type of this Favorite
	 *
	 * @param nType the node type of this Favorite
	 */
	public void setType(int nType) {
		this.nType = nType;
	}

	/**
	 * Sets the date when this object was created.
	 *
	 * @param date the date this object was created.
	 */
	public void setCreationDateLocal(Date date) {
		dCreationDate = date ;
	}

	/**
	 * Returns the creation date of this object.
	 *
	 * @return Date the date this object was created.
	 */
	public Date getCreationDate() {
		return dCreationDate;
	}

	/**
	 * Sets the last modification date of this object.
	 *
	 * @param  date the date this object was last modified.
	 */
	public void setModificationDateLocal(Date date) {
		dModificationDate = date;
	}

	/**
	 * Returns the last modification date of this object.
	 *
	 * @return Date the date this object was last modified.
	 */
	public Date getModificationDate() {
		return dModificationDate;
	}
}
