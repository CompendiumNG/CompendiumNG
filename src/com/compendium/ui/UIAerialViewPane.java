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

package com.compendium.ui;

import java.awt.print.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.datatransfer.*;
import java.awt.dnd.*;
import java.awt.image.*;
import java.awt.print.*;
import java.awt.geom.*;

import java.beans.*;
import java.sql.SQLException;
import java.util.*;
import java.io.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.undo.*;
import javax.help.*;
import javax.imageio.*;

import com.compendium.ProjectCompendium;
import com.compendium.ui.plaf.*;
import com.compendium.ui.popups.*;
import com.compendium.ui.edits.*;
import com.compendium.ui.dialogs.*;
import com.compendium.ui.panels.*;
import com.compendium.ui.*;

import com.compendium.core.datamodel.*;
import com.compendium.core.datamodel.services.IViewService;
import com.compendium.core.ICoreConstants;

/**
 * This class subclasses UIAerialViewPane for use as the maps Aerial view
 *
 * @author	Michelle Bachler
 */
public class UIAerialViewPane extends UIViewPane {

	/** The parent frame for this aerial view.*/
	private UIMapViewFrame	 oMapFrame				= null;

	//private WireFrame		wireFrame				= null;

	/** A reference to the layer holding the scribble notes, when sitting in front of the nodes.*/
	//public final static Integer	WIRE_LAYER		= new Integer(490);

	
	/**
	 * Create a new instance of UIAerialViewPane.
	 * @param view com.compendum.core.datamodel.View, the associated view to this pane.
	 * @param viewFrame com.compendium.ui.UIMapViewFrame, the pernt frame for this aerial view.
	 */
	public UIAerialViewPane(View view, UIMapViewFrame viewframe) {

		super(view, viewframe);
		oMapFrame = viewframe;
		
		//wireFrame = new WireFrame();
		//wireFrame.setLocation(0,0);
		//wireFrame.setSize(1000, 1000);
		//add(wireFrame, WIRE_LAYER);
		//CSH.setHelpIDString(this,"node.views");
	}

	/**
	 * Handle a property change event.
	 * @param evt, the associated PropertyChangeEvent object.
	 */
	public void propertyChange(PropertyChangeEvent evt) {

	    String prop = evt.getPropertyName();

		Object source = evt.getSource();
	    Object oldvalue = evt.getOldValue();
	    Object newvalue = evt.getNewValue();

		if (source instanceof View) {
		    if (prop.equals(View.LINK_ADDED)) {
				LinkProperties link = (LinkProperties)newvalue;
				oMapFrame.addParentLink(link);
			}
		    else if (prop.equals(View.LINK_REMOVED)) {
		    	LinkProperties link = (LinkProperties)newvalue;
				oMapFrame.removeParentLink(link.getLink());
			}
		    else if (prop.equals(View.NODE_ADDED)) {
				NodePosition oNodePos = (NodePosition)newvalue;
				oMapFrame.addParentNode(oNodePos);		
			}
		    else if (prop.equals(View.NODE_TRANSCLUDED)) {
				NodePosition oNodePos = (NodePosition)newvalue;
				oMapFrame.addParentNode(oNodePos);
			}
		    else if (prop.equals(View.NODE_REMOVED)) {
				NodeSummary node = (NodeSummary)newvalue;
				oMapFrame.removeParentNode(node);
			}
		}
		else if (source instanceof UINode) {
		    if (prop.equals(UINode.ROLLOVER_PROPERTY)) {
				UINode node = (UINode)source;
				oMapFrame.setParentRolloverNode(node, ((Boolean)newvalue).booleanValue());
			}
		    //else if (prop.equals(UINode.TYPE_PROPERTY)) {
			//	UINode node = (UINode)source;
			//	oMapFrame.setParentNodeType(node, ((Integer)newvalue).intValue());
			//}
		  	else if (prop.equals(NodePosition.POSITION_PROPERTY)) {
				UINode uinode = (UINode)source;
				Point oPoint = (Point)newvalue;
				Point transPoint = UIUtilities.scalePoint(oPoint.x, oPoint.y, getScale());

				// CHECK THAT THIS NODE WAS NOT THE ONE ORIGINATING THE EVENT
				Point location = uinode.getLocation();
				if (location.x != transPoint.x && location.y != transPoint.y) {
					uinode.setBounds(transPoint.x, transPoint.y, uinode.getWidth(), uinode.getHeight());
					uinode.updateLinks();

					oMapFrame.rescaleAerial(transPoint);
				}
			}
		}
	}
	
	//class WireFrame extends JComponent {
				
	//	public void WireFrame() {}
		
		/**
		 * This method was overridden.
		 */
		/*@Override public void paintComponent(Graphics graphics) {		
			Rectangle visible = oMapFrame.getViewport().getVisibleRect();
			int frameX = oMapFrame.getHorizontalScrollBarPosition();
			int frameY = oMapFrame.getVerticalScrollBarPosition();
			int frameWidth=visible.width;
			int frameHeight=visible.height;
			System.out.println("frameXa="+frameX);
			System.out.println("frameYa="+frameY);
			System.out.println("frameWidtha="+frameWidth);
			System.out.println("frameHeighta="+frameHeight);

			Point newLocation = UIUtilities.transformPoint(frameX, frameY, 1.0);		
			frameX = newLocation.x;
			frameY = newLocation.y;
			Point newSize = UIUtilities.transformPoint(frameWidth, frameHeight, 1.0);
			frameWidth = newSize.x;
			frameHeight =  newSize.y;
			
			System.out.println("frameXb="+frameX);
			System.out.println("frameYb="+frameY);
			System.out.println("frameWidthb="+frameWidth);
			System.out.println("frameHeightb="+frameHeight);

			//scale the values of the visible area to the current aerial view.
			double currentScale = getZoom();

			newLocation = UIUtilities.transformPoint(frameX, frameY, currentScale);		
			frameX = newLocation.x;
			frameY = newLocation.y;
			newSize = UIUtilities.transformPoint(frameWidth, frameHeight, currentScale);
			frameWidth = newSize.x;
			frameHeight =  newSize.y;
			
			System.out.println("frameXc="+frameX);
			System.out.println("frameYc="+frameY);
			System.out.println("frameWidthc="+frameWidth);
			System.out.println("frameHeightc="+frameHeight);
			
			// then draw frame
			graphics.setColor(Color.black);
			graphics.drawRect(frameX, frameY, frameWidth, frameHeight);
		}*/
	//}

	/**
	 * Sets the selection mode for the given node
	 *
	 * @param node com.compendium.ui.UINode, the node to set the selection mode for.
	 * @param mode, the mode to set.
	 */
	public void setSelectedNode(UINode node, int mode) {
		oMapFrame.setParentSelectedNode(node, mode);
		super.processSelectedNode(node, mode);
	}

	/**
	 * Sets the selection mode for the given link
	 *
	 * @param node com.compendium.ui.UILink, the link to set the selection mode for.
	 * @param mode, the mode to set.
	 */
	public void setSelectedLink(UILink link, int mode) {

		oMapFrame.setParentSelectedLink(link, mode);
		super.processSelectedLink(link, mode);
	}

	/**
	 * Clean up variable in this object.
	 * Just calls super.cleanUp.
	 */
	public void cleanUp() {
		super.cleanUp();
	}
}
