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

/**
 * A Link represents a relationship between two nodes. The
 * link in itself does not have any meaning. A type can be
 * given to the node to give it some meaning.
 *
 * @author	rema and sajid / Michelle Bachler
 */
public interface ILink extends IIdObject {

	/**
	 * Return the link's label.
	 * @param String, the label of this link.
	 */
	public String getLabel();

	/**
	 * Sets the label of this link, both locally and in the DATABASE.
	 *
	 * @param String label, The label of this link.
	 * @exception java.sql.SQLException
	 * @exception java.sql.ModelSessionException
	 */
	public void setLabel(String label) throws SQLException, ModelSessionException;

	/**
	 * Returns type of this link.
	 *
	 * @return String, link type.
	 */
	public String getType() ;

	/**
	 * Sets the link type in local data and the database.
	 *
	 * @param sType the project compendium object type
	 * @exception java.sql.SQLException
	 * @exception com.compendium.core.datamodel.ModelSessionException
	 */
	public void setType(String sType) throws SQLException, ModelSessionException;

	/**
	 * Returns the object's original imported id. When
	 * importing maps we need to make sure
	 * that objects with the same id end up
	 * as the same project compendium object.
	 *
	 * @return String, the original identifier of this object
	 */
	public String getOriginalID() ;

	/**
	 * Sets the original imported id of this object, in the local data ONLY.
	 *
	 * @param String sOriginalID, the original id of this object.
	 */
	public void setOriginalID(String sOriginalID) ;

	/**
	 * Returns the node from which this link originates.
	 *
	 * @return NodeSummary, the node from which this link originates
	 */
	public NodeSummary getFrom() ;

	/**
	 * Sets this link's originating node in the local data ONLY.
	 *
	 * @param NodeSummary node, The node from which this link originates
	 */
	public void setFrom(NodeSummary node) ;

	/**
	 * Returns the destination node of this link.
	 *
	 * @return NodeSummary, the destination node of this link.
	 */
	public NodeSummary getTo() ;

	/**
	 * Sets this link's destination node, in the local data ONLY.
	 *
	 * @param NodeSummary node, The destination node of this link
	 */
	public void setTo(NodeSummary node);
}
