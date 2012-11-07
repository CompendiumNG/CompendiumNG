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
 * The INodePositionSummary object defines the position of a node
 * in a view. The position is defined by an X and Y coordinate
 * in the view's relative coordinate system.
 *
 * @author	M. Begeman
 */
public interface INodePositionSummary extends IPCObject  {


	/**
	 * Returns the node for which this object defines its position
	 *
	 * @return NodeSummary, the node summary for which the this position is defined.
	 */
	public String getNodeID() ;


	/**
	 * Returns the view in which the node is placed at the defined position
	 *
	 * @return View, the view in which the node is placed at the defined position
	 */
	public String getViewID();


	/**
	 * Returns the X coordinate of the nodes position in the defined view.
	 *
	 * @return int, the X coordinate of the nodes position.
	 */
	public int getXPos() ;

	/**
	 * Returns the Y coordinate of the nodes position in the defined view.
	 *
	 * @return int, the Y coordinate of the nodes position.
	 */
	public int getYPos() ;


	/**
	 *	Returns the modification date of this node.
	 *
	 *	@return Date, the date when this node was last modified.
	 */
	public Date getModificationDate();
}