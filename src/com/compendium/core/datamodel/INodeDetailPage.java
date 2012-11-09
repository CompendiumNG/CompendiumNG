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
 * The INodeDetailPage holds the information for a page of node detail text
 *
 * @author	27/02/03 Michelle Bachler
 * @version	1.0
 */
public interface INodeDetailPage {

	/**
	 *	Gets the page number of this page of detail text
	 *
	 *	@return the page number of this page of text.
	 */
	public int getPageNo();

	/**
	 *	Sets the page number of this page of text.
	 *
	 *	@param int pageNo, the page number for this page of tex.
	 */
	public void setPageNo(int pageNo);

	/**
	 *	Gets the author of this page.
	 *
	 *	@return String, the author of this page.
	 */
	public String getAuthor();

	/**
	 *	Sets the author for this page, in the local data ONLY.
	 *
	 *	@param String author, the author for this page.
	 */
	public void setAuthor(String sAuthor);

	/**
	 *	Gets the node id associated with this page.
	 *
	 *	@return String, the node id associated with this page.
	 */
	public String getNodeID();

	/**
	 *	Sets the node id for this page, in the local data ONLY.
	 *
	 *	@param String nodeID, the node id for this page.
	 */
	public void setNodeID(String nodeID);

	/**
	 *	Gets the detail text
	 *
	 *	@return String, detail text for this page.
	 */
	public String getText();

	/**
	 *	Sets the text for this page, in the local data ONLY.
	 *
	 *	@param String text, the text for this page.
	 */
	public void setText(String text);

	/**
	 *	Sets the date when this node was created, in the local data ONLY.
	 *
	 *	@param Date date, the creation date of this object.
	 */
	public void setCreationDate(Date date);

	/**
	 * Returns the creation date of this object.
	 * @return Date, the creation date of this object.
	 */
	public Date getCreationDate();

	/**
	 * Sets the Modification Date of this object, in the local data ONLY.
	 * @param Date date, the date this object was last modified.
	 */
	public void setModificationDate(Date date);

	/**
	 * Returns the ModificationDate date of this object.
	 * @return Date, the ModificationDate date of this object.
	 */
	public Date getModificationDate();
}
