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

import java.awt.Point;
import java.util.Date;
import java.util.Vector;
import java.sql.SQLException;

import com.compendium.core.datamodel.services.*;


/**
 * The INodePositionSummary object defines the position of a node
 * in a view. The position is defined by an X and Y coordinate
 * in the view's relative coordinate system.
 *
 * This is a lightweight version of the NodePosition object and is used
 * only to determine if a view is 'dirty' in the groupware sense - i.e.,
 * if the view has been changed by someone else.
 *
 * @author	M. Begeman
 */
public class NodePositionSummary extends PCObject implements INodePositionSummary, java.io.Serializable {

	/** the view the node associated with this object is in.*/
	protected String		sViewID 		= null;

	/** the NodeID if the node */
	protected String		sNodeID			= null;

	/** The x coordinates of the node associated with this object.*/
	protected int	 		nX				= -1;

	/** The y coordinates of the node associated with this object.*/
	protected int			nY				= -1;

	/** The date the node was last modified.*/
	protected Date			oModificationDate		= null;

	/**
	 * Constructor, creates a new position node,
	 * defining the position of the given node in the given view.
	 *
	 * @param View oView, The view in which the node is placed
	 * @param NodeSummary oNode, The node for which the position is defined
	 * @param int x, The X coordinate of the node's position
	 * @param int y, The Y coordinate of the node's position
	 * @param Date dCreated, the date this object was created.
	 * @param Date dModified, the date this object was last modified.

	 */
	public NodePositionSummary(String sNodeID, String sViewID, Date dModified, int x, int y) {
		this.sViewID = sViewID;
		this.sNodeID = sNodeID;
		this.nX = x;
		this.nY = y;
		this.oModificationDate = dModified;
	}


	/**
	 * The initialize method is called by the Model before adding the object to the cache.
	 *
	 * @param PCSession session, the session associated with this object.
	 * @param IMode model, the model this object belongs to.
	 */
	public void initialize(PCSession session, IModel model) {
		super.initialize(session, model);
	}

	/**
	 *	This method needs to be called on this object before the Model removes it from the cache.
	 */
	public void cleanUp() {
		super.cleanUp() ;
	}

	/**
	 * Returns the node for which this object defines its position
	 *
	 * @return NodeSummary, the node summary for which the this position is defined.
	 */
	public String getNodeID() {
		return this.sNodeID;
	}


	/**
	 * Returns the view in which the node is placed at the defined position
	 *
	 * @return View, the view in which the node is placed at the defined position
	 */
	public String getViewID() {
		return this.sViewID;
	}



	/**
	 * Returns the X coordinate of the nodes position in the defined view.
	 *
	 * @return int, the X coordinate of the nodes position.
	 */
	public int getXPos() {
		 return nX ;
	}


	/**
	 * Returns the Y coordinate of the nodes position in the defined view.
	 *
	 * @return int, the Y coordinate of the nodes position.
	 */
	public int getYPos() {
		return nY ;
	}

	/**
	 *	Returns the modification date of this node.
	 *
	 *	@return Date, the date when this node was last modified.
	 */
	public Date getModificationDate() {
		return oModificationDate;
	}

}