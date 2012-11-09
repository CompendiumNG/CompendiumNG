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
 * The NodeDetailPage holds the information for a page of node detail text.
 *
 * @author	Michelle Bachler
 */
public class NodeDetailPage implements INodeDetailPage {

	/** The page number for this page of text.*/
	protected int	 		nPageNo				= -1;

	/** The date this object was created.*/
	protected Date			oCreationDate		= null;

	/** The date this object was last modified.*/
	protected Date			oModificationDate	= null;

	/** The author for this page.*/
	protected String		sAuthor				= "";

	/** The node id associated with this page.*/
	protected String		sNodeID				= "";

	/** The text for this page.*/
	protected String		sText				= "";

	/**
	 * Constructor
	 */
	public NodeDetailPage() {}

	/**
	 *	Constructor
	 *
	 *	@param String nodeID, the node id associated with this page.
	 *	@param String author, the author for this page.
	 *	@param String text, the text for this page.
	 *	@param int pageNo, the page number for this page of text.
	 *	@param Date created, the date this object was created.
	 *	@param Date modified, the date this object was last modified.
	 */
	public NodeDetailPage(String nodeID, String author, String text, int pageNo, Date created, Date modified) {
		nPageNo = pageNo;
		sNodeID = nodeID;
		sAuthor = author;
		sText	= text;
		oCreationDate = created;
		oModificationDate = modified;
	}

	public Object clone() {
		return new NodeDetailPage(sNodeID, sAuthor, sText, nPageNo, oCreationDate, oModificationDate);
	}

	/**
	 *	Gets the page number of this page of detail text
	 *
	 *	@return the page number of this page of text.
	 */
	public int getPageNo() {
		return nPageNo;
	}

	/**
	 *	Sets the page number of this page of text, in the local data ONLY.
	 *
	 *	@param int pageNo, the page number for this page of text.
	 */
	public void setPageNo(int pageNo) {
		nPageNo = pageNo;
	}

	/**
	 *	Gets the author of this page.
	 *
	 *	@return String, the author of this page.
	 */
	public String getAuthor() {
		return sAuthor;
	}

	/**
	 *	Sets the author for this page, in the local data ONLY.
	 *
	 *	@param String author, the author for this page.
	 */
	public void setAuthor(String author) {
		sAuthor = author;
	}

	/**
	 *	Gets the node id associated with this page.
	 *
	 *	@return String, the node id associated with this page.
	 */
	public String getNodeID() {
		return sNodeID;
	}

	/**
	 *	Sets the node id for this page, in the local data ONLY.
	 *
	 *	@param String nodeID, the node id for this page.
	 */
	public void setNodeID(String nodeID) {
		sNodeID = nodeID;
	}

	/**
	 *	Gets the detail text
	 *
	 *	@return String, detail text for this page.
	 */
	public String getText() {
		return sText;
	}

	/**
	 *	Sets the text for this page, in the local data ONLY.
	 *
	 *	@param String text, the text for this page.
	 */
	public void setText(String text) {
		sText = text;
	}

	/**
	 *	Sets the date when this node was created, in the local data ONLY.
	 *
	 *	@param Date date, the creation date of this object.
	 */
	public void setCreationDate(Date date) {
		oCreationDate = date;
	}

	/**
	 * Returns the creation date of this object.
	 * @return Date, the creation date of this object.
	 */
	public Date getCreationDate() {
		return oCreationDate;
	}

	/**
	 * Sets the Modification Date of this object, in the local data ONLY.
	 * @param Date date, the date this object was last modified.
	 */
	public void setModificationDate(Date date) {
		oModificationDate = date;
	}

	/**
	 * Returns the ModificationDate date of this object.
	 * @return Date, the ModificationDate date of this object.
	 */
	public Date getModificationDate() {
		return oModificationDate;
	}
}
