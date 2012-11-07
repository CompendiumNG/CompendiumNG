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
 * This class extends NodePosition to add the dimension of time span to a node.
 * It represents one timespan for a node's visibility in a map.
 *
 * @author	Michelle Bachler
 */
public class NodePositionTime extends NodePosition implements java.io.Serializable {


	/** Position property name for use with property change events */
	//public final static String HIDE_ICON_PROPERTY = "hideicons";

	protected long		nTimeToShow = 0;
	
	protected long		nTimeToHide	= 0;
	
	protected String 	sViewTimeNodeID = "Unknown";

	/**
	 * Constructor, creates a new position node,
	 * defining the position of the given node in the given view.
	 *
	 * @param View The view in which the node is placed
	 * @param oNode The node for which the position is defined
	 * @param nShowTime  the time to show the node, in milliseconds
	 * @param nHideTime the time to hide the node, in milliseconds
	 * @param x The X coordinate of the node's position
	 * @param y The Y coordinate of the node's position
	 * @param dCreated the date this object was created.
	 * @param dModified the date this object was last modified.
	 
	 */
	public NodePositionTime(String sViewTimeNodeID, View oView, NodeSummary oNode, long nShowTime, long nHideTime, int x, int y, Date dCreated, Date dModified) {
		super(oView, oNode, x, y, dCreated, dModified);
		this.sViewTimeNodeID = sViewTimeNodeID;
		nTimeToShow = nShowTime;
		nTimeToHide = nHideTime;
	}	

	/**
	 * Return a new NodePositionTime object with the properties of this node.
	 */
	public NodePositionTime getClone() {
		return new NodePositionTime(sViewTimeNodeID, oView, oNodeSummary, nTimeToHide, nTimeToShow, nX, nY, oCreationDate, oModificationDate);
	}

	public String getId() {
		return sViewTimeNodeID;
	}
	
	public void setTimeToShow(long time) {
		nTimeToShow = time;
	}
	
	public long getTimeToShow() {
		return nTimeToShow;
	}
	
	public void setTimeToHide(long time) {
		nTimeToHide = time;
	}
	
	public long getTimeToHide() {
		return nTimeToHide;
	}
	
	/**
	 * Sets the X coordinate of the nodes position in the defined view, in the local data ONLY.
	 * and fires a PropertyChangeEvent.
	 *
	 * @param int x, the X coordinate of the nodes position.
	 */
	public void setXPos(int x) {
		Point oldPoint = new Point(nX, nY);
		nX = x;
		//firePropertyChange(POSITION_PROPERTY, oldPoint, new Point(nX, nY));
	}

	/**
	 * Sets the Y coordinate of the nodes position in the defined view, in the local data ONLY.
	 * and fires a PropertyChangeEvent.
	 *
	 * @param int y, the Y coordinate of the nodes position.
	 */
	public void setYPos(int y) {
		Point oldPoint = new Point(nX, nY);
		nY = y;
		//firePropertyChange(POSITION_PROPERTY, oldPoint, new Point(nX, nY));
	}

	/**
	 * Sets the nodes position in the defined view, in the local data ONLY.
	 * and fires a PropertyChangeEvent.
	 *
	 * @param int x, the X coordinate of the node's position.
	 * @param int y, the Y coordinate of the node's position.
	 */
	public void setPos(int x, int y) {

		Point oldPoint = new Point(nX, nY);
		nX = x;
		nY = y;
		//firePropertyChange(POSITION_PROPERTY, oldPoint, new Point(x, y));
	}

	/**
	 * Sets the nodes position in the defined view, in the local data ONLY.
	 *
	 * @param Point oPoint, The node's position.
	 */
	public void setPos(Point oPoint) {
		Point oldPoint = new Point(nX, nY);

		nX = oPoint.x;
		nY = oPoint.y;

		//firePropertyChange(POSITION_PROPERTY, oldPoint, oPoint);
	}
	
	/**
	 * Check to see if the passed time is inside this span or this span is inside it
	 * In which case there is a overlap.
	 * @param newTime the time to check
	 * @return true if there is an overlap with this time span, else false;
	 */
	public boolean checkForOverlap(long hide, long show) {
		boolean overlap = false;
		
		// is the start or stop times inside this span
		if ( ((show >= nTimeToShow && show < nTimeToHide) || 
			 (hide > nTimeToShow && hide <= nTimeToHide)) ) {
			overlap = true;
			
		// is this span inside the passed span
		} else if ( (nTimeToShow >= show && nTimeToShow < hide) || 
				(nTimeToHide > show && nTimeToHide <= hide) ) {
			overlap = true;
		}
		
		return overlap;
	}
	
}
