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

/**
 * The INodePosition object defines the position of a node
 * in a view. The position is defined by an X and Y coordinate
 * in the view's relative coordinate system.
 *
 * @author	rema and sajid
 */
public interface INodePosition extends IPCObject  {

	/** X position property name for use with property change events */
	public final static String XPOS_PROPERTY = "xpos";

	/** Y position property name for use with property change events */
	public final static String YPOS_PROPERTY = "ypos";

	/** Position property name for use with property change events */
	public final static String POS_PROPERTY  = "pos";

	/**
	 * Returns the node for which this object defines its position
	 *
	 * @return NodeSummary, the node summary for which the this position is defined.
	 */
	public NodeSummary getNode() ;

	/**
	 * Sets the node for which this object defines its position, in the local data ONLY.
	 *
	 * @param NodeSummary oNode, the node for which this object defines its position
	 */
	public void setNode(NodeSummary oNode);

	/**
	 * Returns the view in which the node is placed at the defined position
	 *
	 * @return View, the view in which the node is placed at the defined position
	 */
	public View getView();

	/**
	 * Set the view in which the node is placed at the defined position.
	 * @param View oView, the view in which this node is placed.
	 */
	public void setView(View oView);

	/**
	 * Returns the X coordinate of the nodes position in the defined view.
	 *
	 * @return int, the X coordinate of the nodes position.
	 */
	public int getXPos() ;

	/**
	 * Sets the X coordinate of the nodes position in the defined view, in the local data ONLY.
	 * and fires a PropertyChangeEvent.
	 *
	 * @param int x, the X coordinate of the nodes position.
	 */
	public void setXPos(int x) ;

	/**
	 * Returns the Y coordinate of the nodes position in the defined view.
	 *
	 * @return int, the Y coordinate of the nodes position.
	 */
	public int getYPos() ;

	/**
	 * Sets the Y coordinate of the nodes position in the defined view, in the local data ONLY.
	 * and fires a PropertyChangeEvent.
	 *
	 * @param int y, the Y coordinate of the nodes position.
	 */
	public void setYPos(int y) ;

	/**
	 * Returns the nodes position in the defined view.
	 *
	 * @return Point, a point object representing the node's position.
	 */
	public Point getPos() ;

	/**
	 * Sets the nodes position in the defined view, in the local data ONLY.
	 * and fires a PropertyChangeEvent.
	 *
	 * @param int x, the X coordinate of the node's position.
	 * @param int y, the Y coordinate of the node's position.
	 */
	public void setPos(int x, int y) ;

	/**
	 * Sets the nodes position in the defined view, in the local data ONLY.
	 *
	 * @param Point oPoint, The node's position.
	 */
	public void setPos(Point p);

	/**
	 *	Returns the creation date of this object.
	 *
	 *	@return Date, the date when this object was created.
	 */
	public Date getCreationDate();

	/**
	 *	Sets the date when this node was created, in the local data ONLY.
	 *
	 *	@param Date date, the creation date of this object.
	 */
	public void setCreationDate(Date date);

	/**
	 * Sets the ModificationDate date of this object, in the local data ONLY.
	 *
	 * @param Date date, the date this object was last modified.
	 */
	public void setModificationDate(Date date);

	/**
	 *	Returns the modification date of this node.
	 *
	 *	@return Date, the date when this node was last modified.
	 */
	public Date getModificationDate();
}