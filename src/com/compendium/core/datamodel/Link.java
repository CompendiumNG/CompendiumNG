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
import com.compendium.core.ICoreConstants;

/**
 * A Link represents a relationship between two nodes. The
 * link in itself does not have any meaning. A type can be
 * given to the node to give it some meaning.
 *
 * @author	rema and sajid / Michelle Bachler
 */
public class Link extends IdObject implements java.io.Serializable, ILink {

	/** Label property name for use with property change events */
	public final static String LABEL_PROPERTY		= "linklabel";

	/** Type property name for use with property change events */
	public final static String TYPE_PROPERTY		= "linktype";

	/** A static list of Link object already created in this session.*/
	private static Vector 	linkSummaryList 		= new Vector();


	/** The type of this Link.*/
	protected String			sType				= "";

	/** The original ID for this link, if imported.*/
	protected String			sOriginalID			= "";

	/** The NodeSummary object the link has been drawn from.*/
	protected NodeSummary		oFrom				= null;

	/** The NodeSummary object that the link has been drawn to.*/
	protected NodeSummary		oTo					= null;

	/** The label of this link.*/
	protected String 			sLabel 				= "";


	/**
	 *	Constructor.
	 *
	 * @param String sLinkID, the unique identifier for this Link.
	 * @param java.util.Date dCreationDate, the date the Link was created.
	 * @param java.util.Date dModificationDate, the date the link was last modified.
	 * @param String sAuthor, the name of the porson who created this Link.
	 * @param String sType, the type of this link.
	 * @param String sOriginalID, the original ID of this link if/when imported.
	 * @param sLabel, the label for this Link.
	 */
	public Link(String sLinkID, java.util.Date dCreationDate, java.util.Date dModificationDate,
		String sAuthor, String sType, String sOriginalID, String sLabel) {

		super (sLinkID, -1,  sAuthor, dCreationDate, dModificationDate) ;
		this.sType = sType;
		this.sOriginalID = sOriginalID;
		this.sLabel = sLabel;
	}

	/**
	 *	Constructor.
	 *
	 * @param String sLinkID, the unique identifier for this Link.
	 * @param java.util.Date dCreationDate, the date the Link was created.
	 * @param java.util.Date dModificationDate, the date the link was last modified.
	 * @param String sAuthor, the name of the porson who created this Link.
	 * @param String sType, the type of this link.
	 * @param String sOriginalID, the original ID of this link if/when imported.
	 * @param NodeSummary oFrom, the node the Link is coming from.
	 * @param NodeSummary oTo, the node the Link is going to.
	 * @param sLabel, the label for this Link.
	 */
	public Link(String sLinkID, java.util.Date dCreationDate, java.util.Date dModificationDate,
				String sAuthor, String sType, String sOriginalID, NodeSummary oFrom, NodeSummary oTo,
				String sLabel) {

		// call super, passing -1 for permission for now. can be set later in the procedure
		// that calls the constructor
		super (sLinkID, -1,  sAuthor, dCreationDate, dModificationDate);
		this.sType = sType;
		this.sOriginalID = sOriginalID;
		this.oFrom = oFrom;
		this.oTo = oTo;
		this.sLabel = sLabel;
	}

	/**
	 * Return a link object with the given id and details.
	 * If a link with the given id has already been created in this session, update its data and return that,
	 * else create a new one, and add it to the list.
	 *
	 * @param String sLinkID, the unique identifier for this Link.
	 * @param java.util.Date dCreationDate, the date the Link was created.
	 * @param java.util.Date dModificationDate, the date the link was last modified.
	 * @param String sAuthor, the name of the porson who created this Link.
	 * @param Stirng sType, the type of this link.
	 * @param String sOriginalID, the original ID of this link if/when imported.
	 * @param sLabel, the label for this Link.
	 */
	public static Link getLink(String sLinkID, java.util.Date dCreationDate, java.util.Date dModificationDate,
		String sAuthor, String sType, String sOriginalID, String sLabel) {

		int i = 0;
		for (i = 0; i < linkSummaryList.size(); i++) {
			if (sLinkID.equals(((Link)linkSummaryList.elementAt(i)).getId())) {
				break;
			}
		}

		Link link = null;
		if (i == linkSummaryList.size()) {
			link = new Link(sLinkID, dCreationDate, dModificationDate, sAuthor, sType, sOriginalID, sLabel);
			linkSummaryList.addElement(link);
		}
		else {
			link = (Link)linkSummaryList.elementAt(i);

			// UPDATE THE DETAILS
			link.setTypeLocal(sType);
			link.setAuthorLocal(sAuthor);
			link.setOriginalIDLocal(sOriginalID);
			link.setLabelLocal(sLabel);
			link.setCreationDateLocal(dCreationDate);
			link.setModificationDateLocal(dModificationDate);
		}
		return link;
	}

	/**
	 * Return a link object with the given id and details.
	 * If a link with the given id has already been created in this session, update its data and return that,
	 * else create a new one, and add it to the list.
	 *
	 * @param String sLinkID, the unique identifier for this Link.
	 * @param java.util.Date dCreationDate, the date the Link was created.
	 * @param java.util.Date dModificationDate, the date the link was last modified.
	 * @param String sAuthor, the name of the porson who created this Link.
	 * @param String sType, the type of this link.
	 * @param String sOriginalID, the original ID of this link if/when imported.
	 * @param NodeSummary oFrom, the node the Link is coming from.
	 * @param NodeSummary oTo, the node the Link is going to.
	 * @param sLabel, the label for this Link.
	 */
	public static Link getLink(String sLinkID, java.util.Date dCreationDate, java.util.Date dModificationDate,
				String sAuthor, String sType, String sOriginalID, NodeSummary oFrom, NodeSummary oTo,
				String sLabel) {

		int i = 0;
		for (i = 0; i < linkSummaryList.size(); i++) {
			if (sLinkID.equals(((Link)linkSummaryList.elementAt(i)).getId())) {
				break;
			}
		}

		Link link = null;
		if (i == linkSummaryList.size()) {
			link = new Link(sLinkID, dCreationDate, dModificationDate, sAuthor, sType, sOriginalID, oFrom, oTo, sLabel);
			linkSummaryList.addElement(link);
		}
		else {
			link = (Link)linkSummaryList.elementAt(i);

			// UPDATE THE DETAILS
			link.setTypeLocal(sType);
			link.setAuthorLocal(sAuthor);
			link.setOriginalIDLocal(sOriginalID);
			link.setLabelLocal(sLabel);
			link.setCreationDateLocal(dCreationDate);
			link.setModificationDateLocal(dModificationDate);
			link.setToLocal(oTo);
			link.setFromLocal(oFrom);
		}
		return link;
	}

	/**
	 * Clear the list of all links created / used in this session.
	 */
	public static void clearList() {
		linkSummaryList.removeAllElements();
	}
	
	/**
	 * Remove the given link from the link list.
	 *
	 * @param Link link, the link to remove from the link list.
	 */
	public static void removeLinkSummaryListItem(Link link) {
		String id = link.getId();
		int count = linkSummaryList.size();
		for (int i = 0; i < count ; i++) {
			if (id.equals(((Link)linkSummaryList.elementAt(i)).getId())) {
				linkSummaryList.removeElementAt(i);
				return;
			}
		}
	}


	/**
	 *	The initialize method is called by the Model before adding the
	 *	object to the client
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
	 * Return the link's label.
	 * @param String, the label of this link.
	 */
	public String getLabel() {
		return sLabel ;
	}

	/**
	 * Sets the label of this link, both locally and in the DATABASE.
	 *
	 * @param String label, The label of this link .
	 * @exception java.sql.SQLException
	 * @exception java.sql.ModelSessionException
	 */
	public void setLabel(String label) throws SQLException, ModelSessionException {

		if (label.equals(sLabel))
			return;

		if (oModel == null)
			throw new ModelSessionException("Model is null in Link.setLabel");
		if (oSession == null) {
			oSession = oModel.getSession();
			if (oSession == null)
				throw new ModelSessionException("Session is null in Link.setLabel");
		}

		Date date = new Date();
		setModificationDateLocal(date);
		ILinkService ls = oModel.getLinkService();
		ls.setLabel(oSession, sId, label, date);

		setLabelLocal(label) ;
  	}

	/**
	 * Sets the label of this link locally.
	 *
	 *	@param String label, the label of this link.
	 *	@return String, the old value of the label.
	 */
	protected String setLabelLocal(String label) {
		if (label.equals(sLabel))
			return "";

		String oldValue = sLabel;
		sLabel = label;
		if (sLabel == null)
			sLabel = "";

		firePropertyChange(LABEL_PROPERTY, oldValue, sLabel) ;
		return oldValue;
	}

	/**
	 * Returns type of this link.
	 *
	 * @return String, link type.
	 */
	public String getType() {
		return sType;
	}

	/**
	 * Sets the link type in local data and the database.
	 *
	 * @param sType, the link type for this link.
	 * @exception java.sql.SQLException
	 * @exception com.compendium.core.datamodel.ModelSessionException
	 */
	public void setType(String sType) throws SQLException, ModelSessionException {

		if (this.sType.equals(sType))
			return;
		
		if (oModel == null)
			throw new ModelSessionException("Model is null in Link.setType");

		if (oSession == null)
			throw new ModelSessionException("Session is null in Link.setType");

		// call link service to update database
		ILinkService ls = oModel.getLinkService();
		ls.setType(oSession, sId, this.sType, sType);
		
		setTypeLocal(sType);
	}

	/**
	 *  Sets the link type in the local data
	 *
	 *  @param String type, the link type.
	 *	@return String, the old value of the link type.
	 */
	protected String setTypeLocal(String type) {

		if (this.sType.equals(type))
			return "";

		String oldValue = this.sType;
		this.sType = type;

		firePropertyChange(TYPE_PROPERTY, oldValue, this.sType);

		return oldValue;
	}

	/**
	 * Returns the object's original id. When
	 * importing maps we need to make sure
	 * that objects with the same id end up
	 * as the same project compendium object.
	 *
	 * @return Stirng, the original identifier of this object
	 */
	public String getOriginalID() {
		return sOriginalID;
	}

	/**
	 * Sets the original imported id of this object, in the local data ONLY.
	 *
	 * @param String sOriginalID, the original id of this object.
	 */
	public void setOriginalID(String sOriginalID) {

		if (this.sOriginalID.equals(sOriginalID))
			return;

		setOriginalIDLocal(sOriginalID);
	}

	/**
	 * Sets the imported id of this object in the Local DATA. This is the
	 * unique identifier for this object as used in the original map.
	 *
	 *  @param String sOriginalID the original imported id of this object
	 *	@return the old value of the id
	 */
	protected String setOriginalIDLocal(String sOriginalID) {

		if (this.sOriginalID.equals(sOriginalID))
			return "";

		String oldValue = sOriginalID;
		this.sOriginalID = sOriginalID;
		return oldValue ;
	}

	/**
	 * Returns the node from which this link originates.
	 *
	 * @return NodeSummary, the node from which this link originates
	 */
	public NodeSummary getFrom() {
		return oFrom ;
	}

	/**
	 * Sets this link's originating node in the local data ONLY.
	 *
	 * @param NodeSummary node, The node from which this link originates
	 */
	public void setFrom(NodeSummary node) {

		if (node == oFrom)
			return ;
		setFromLocal(node) ;
	}

	/**
	 *  Sets this link's originating node, in the local Data.
	 *
	 *  @param NodeSummary node, The node from which this link originates
	 *	@return NodeSummary, the old value of the originating node.
	 */
	protected NodeSummary setFromLocal(NodeSummary node) {
		if (node == oFrom)
			return null;
		NodeSummary oldValue = oFrom ;
		oFrom = node ;
		return oldValue ;
	}

	/**
	 * Returns the destination node of this link.
	 *
	 * @return NodeSummary, the destination node of this link.
	 */
	public NodeSummary getTo() {
		return oTo ;
	}

	/**
	 * Sets this link's destination node, in the local data ONLY.
	 *
	 * @param NodeSummary node, The destination node of this link
	 */
	public void setTo(NodeSummary node) {

		if (node == oTo)
			return ;
		setToLocal(node) ;
	}

	/**
	 *  Sets this link's destination node in the local data.
	 *
	 *  @param NodeSummary node, The destination node of this link
	 *	@return NodeSummary, the old value of the destination node.
	 */
	protected NodeSummary setToLocal(NodeSummary node) {
		if (node == oTo)
			return null;
		NodeSummary oldValue = oTo ;
		oTo = node ;
		return oldValue ;
	}
}
