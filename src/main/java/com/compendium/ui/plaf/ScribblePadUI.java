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

package com.compendium.ui.plaf;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.awt.datatransfer.*;

import java.io.*;
import java.beans.*;
import java.util.*;

import javax.swing.*;
import javax.swing.undo.*;
import javax.swing.border.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;

import com.compendium.core.datamodel.*;
import com.compendium.*;
import com.compendium.ui.*;
import com.compendium.ui.edits.*;
import com.compendium.ui.dialogs.*;

/**
 * The UI class for the UIScribblePad Component
 * THE CLASS IS STILL UNDER DEVELOPMENT
 *
 * @author	Michelle Bachler
 */
public	class ScribblePadUI extends ComponentUI implements MouseListener, MouseMotionListener, KeyListener {

	/** The UIScribblePad instance associated with this UI.*/
	protected 	UIScribblePad				oScribblePad;

	/** The UIViewPane instance associated with this UI.*/
	protected	UIViewPane					oViewPane;

	/** The MouseListener used by this UI.*/
	private		MouseListener						oMouseListener;

	/** The MouseMotionListener used by this UI.*/
	private		MouseMotionListener					oMouseMotionListener;

	/** The KeyListener use by this UI.*/
	private		KeyListener							oKeyListener;

	/** The data of the pencil scribble drawn so far.*/
	private		Vector	data	=  new Vector(51);

	/** The data of the shapes scribble drawn so far.*/
	private		Vector	shapes	=  new Vector(51);

	private 	Vector 	lastScribble = null;

	/** The mouse pressed x position of the mouse pointer.*/
 	private 	int 		start_x;

	/** The mouse Pressed y position of the mouse pointer.*/
	private 	int 		start_y;

	/** The last x position of the mouse pointer.*/
 	private 	int 		last_x;

	/** The last y position of the mouse pointer.*/
	private 	int 		last_y;

	/** Indicates if the mouse is currently being dragged.*/
	private		boolean		bDragging 			= false;

	/** The color to draw the scribble.*/
  	private 	Color 		current_colour 		= Color.black; // Store the current color.

	/** The tool to use to draw the scribble.*/
	private 	int 		current_tool		= UIScribblePad.PENCIL;

	/** The thickness of the lines being drawn.*/
	private 	int 		current_thickness	= 1;


	/**
	 * Constructor.
	 * @param c, the component this is the ui.
	 */
  	public ScribblePadUI(JComponent c) {
		super();
		oScribblePad = (UIScribblePad)c;
		oViewPane = oScribblePad.getViewPane();
  	}

	/**
	 * Create a new ScribblePadUI instance.
	 * @param c, the component this is the ui to install for.
	 */
  	public static ComponentUI createUI(JComponent c) {
		ScribblePadUI scribbleui = new ScribblePadUI(c);
	  	return scribbleui;
  	}

	/**
	 * Run any install instructions for installing this UI.
	 * @param c, the component this is the ui for.
	 */
	public void installUI(JComponent c) {
		super.installUI(c);
		oScribblePad = (UIScribblePad)c;
		installListeners(c);
	}

	/**
	 * Install any Listener classes required by this UI.
	 * @param c, the component to install the listeners for.
	 */
	protected void installListeners(JComponent c) {
		if ( (oMouseListener = createMouseListener( c )) != null ) {
	    	c.addMouseListener( oMouseListener );
		}
		if ( (oMouseMotionListener = createMouseMotionListener( c )) != null ) {
	    	c.addMouseMotionListener( oMouseMotionListener );
		}
		if ( (oKeyListener = createKeyListener( c )) != null ) {
			c.addKeyListener( oKeyListener );
		}
	}

	/**
	 * Just returns this class as the MouseListener.
	 * @param c, the component to create the MouseLisener for.
	 * @return MouseListener, the listener to use.
	 */
  	protected MouseListener createMouseListener( JComponent c ) {
		return this;
  	}

	/**
	 * Just returns this class as the MouseMotionListener.
	 * @param c, the component to create the MouseMotionLisener for.
	 * @return MouseMotionListener, the listener to use.
	 */
	protected MouseMotionListener createMouseMotionListener( JComponent c ) {
		return this;
	}

	/**
	 * Just returns this class as the KeyListener.
	 * @param c, the component to create the KeyLisener for.
	 * @return KeyListener, the listener to use.
	 */
	protected KeyListener createKeyListener(JComponent c) {
		return this;
	}

	/**
	 * Run any uninstall instructions for uninstalling this UI.
	 * @param c, the component this is the ui to uninstall for.
	 */
	public void uninstallUI(JComponent c) {
		uninstallListeners(c);
		oScribblePad = null;
		super.uninstallUI(c);
	}

	/**
	 * Uninstall any Listener classes used by this UI.
	 * @param c, the component to uninstall the listeners for.
	 */
	protected void uninstallListeners(JComponent c) {
		if ( oMouseMotionListener!= null ) {
	    	c.removeMouseMotionListener( oMouseMotionListener );
		}
		if ( oMouseListener!= null ) {
	    	c.removeMouseListener( oMouseListener );
		}

		oMouseListener = null;
		oMouseMotionListener = null;
	}

	/**
	 * Undo the last paint operation - if a pencil scribble.
	 */
	public void undo(Vector vtShapes) {

		UIShape test = (UIShape)vtShapes.elementAt(0);

		// SHOULD BE THE LAST THING ADDED SO TEST THIS FIRST
		Vector last = (Vector)data.lastElement();
		UIShape shape = (UIShape)last.elementAt(0);
		if (shape.equals(test)) {
			data.removeElement(last);
		}
		else {
			int count = data.size();
			for (int i=0; i<count; i++) {
				Vector next = (Vector)data.elementAt(i);
				shape = (UIShape)next.elementAt(0);
				if (shape.equals(test)) {
					data.removeElement(next);
					break;
				}
			}
		}

		oScribblePad.repaint();
	}

	/**
	 * Redo the last undo - if a pencil scribble.
	 */
	public void redo(Vector vtShapes) {
		data.addElement(vtShapes);
		oScribblePad.repaint();
	}

	/**
	 * Undo the last paint operation - if other tool.
	 */
	public void undo(UIShape shape) {
		shapes.removeElement(shape);
		oScribblePad.repaint();
	}

	/**
	 * Redo the last undo - if other tool.
	 */
	public void redo(UIShape shape) {
		shapes.addElement(shape);
		oScribblePad.repaint();
	}

	/**
	 * Set the pencil data.
	 * @param data, the pencil data.
	 */
	public void setPencilData(Vector data) {
		this.data = data;
	}

	/**
	 * Get the pencil data.
	 * @param Vector, the pencil data.
	 */
	public Vector getPencilData() {
		return this.data;
	}

	/**
	 * Set the shapes data.
	 * @param data, the shapes data.
	 */
	public void setShapesData(Vector data) {
		this.shapes = data;
	}

	/**
	 * Get the shapes data.
	 * @return Vector, the shapes data.
	 */
	public Vector getShapesData() {
		return this.shapes;
	}

	/**
	 * Set the currently selected tool.
	 * @param tool, the tool to use.
	 */
	public void setTool(int tool) {
		current_tool = tool;
	}

	/**
	 * Set the currently selected colour.
	 * @param colour, the colour to draw.
	 */
	public void setColour(Color colour) {
		this.current_colour = colour;
	}

	/**
	 * Set the current line thickness.
	 * @param thickness, the thickness to draw lines.
	 */
	public void setThickness(int thickness) {
		this.current_thickness = thickness;
	}

	/**
	 * Draws the scribble line / or shapes on the given graphics context.
	 *
	 * @param g, the Graphics object for this pain method to use.
	 * @param c, the component to paint.
	 */
	public void paint(Graphics g, JComponent c) {

		double  scale = oViewPane.getScale();
		
	    Graphics2D g2d = (Graphics2D)g;
	    
		// DRAW POINTS
		int count = data.size();
		Point prev = null;
		Point current = null;
		for (int i=0; i<count; i++) {
			Vector next = (Vector)data.elementAt(i);
			prev = null;

			int jcount = next.size();
			for (int j=0; j<jcount; j++) {
				UIShape shape = (UIShape)next.elementAt(j);
				g.setColor(shape.getColour());
				int thickness = shape.getThickness();
			    //g2d.setStroke(new BasicStroke(thickness));
				Point thicknessScaled = UIUtilities.transformPoint(thickness, thickness, scale);			
				g2d.setStroke(new BasicStroke(thicknessScaled.x));

				if (j==0)
					prev = UIUtilities.transformPoint(shape.getX(), shape.getY(), scale);
					//prev = new Point(shape.getX(), shape.getY());
				else {
					current = UIUtilities.transformPoint(shape.getX(), shape.getY(), scale);			
					//current = new Point(shape.getX(), shape.getY());
		   			g.drawLine(prev.x, prev.y, current.x, current.y);
					prev = current;
				}
			}
		}

		// DRAW SHAPES
		int jcount = shapes.size();
		for (int j=0; j<jcount; j++) {
			UIShape shape = (UIShape)shapes.elementAt(j);
			int type = shape.getType();
			g.setColor(shape.getColour());
			int thickness = shape.getThickness();
		    //g2d.setStroke(new BasicStroke(thickness));
			Point thicknessScaled = UIUtilities.transformPoint(thickness, thickness, scale);			
			g2d.setStroke(new BasicStroke(thicknessScaled.x));

			Point pos = UIUtilities.transformPoint(shape.getX(), shape.getY(), scale);			
			Point width = UIUtilities.transformPoint(shape.getWidth(), shape.getWidth(), scale);			
			Point height = UIUtilities.transformPoint(shape.getHeight(), shape.getHeight(), scale);			

			if (type == UIScribblePad.OVAL) {
				g.drawOval(pos.x, pos.y, width.x, height.x);
				//g.drawOval(shape.getX(), shape.getY(), shape.getWidth(), shape.getHeight());
			}
			else if (type == UIScribblePad.RECTANGLE) {
				g.drawRect(pos.x, pos.y, width.x, height.x);
				//g.drawRect(shape.getX(), shape.getY(), shape.getWidth(), shape.getHeight());
			}
			else if (type == UIScribblePad.LINE) {
	   			g.drawLine(pos.x, pos.y, width.x, height.x);
	   			//g.drawLine(shape.getX(), shape.getY(), shape.getWidth(), shape.getHeight());
			}
		}
	}

	/**
	 * Clear all the data elements used to draw the scribble.
	 */
	public void clear() {
		data.removeAllElements();
		shapes.removeAllElements();
	}

	// EVENT HANDLERS

	/**
	 * Handles the initiation of drag and drop events.
	 * @param evt, the MouseEvent generated.
	 */
  	public void mousePressed(MouseEvent evt) {

		/*
		if (current_tool == UIScribblePad.NO_TOOL) {
			UINode node = oViewPane.getUI().isMouseOnANode(evt);
			if (node != null) {
				((NodeUI)node.getUI()).mousePressed(evt);
			}
			else {
				UILink link = oViewPane.getUI().isMouseOnALink(evt);
				if (link != null) {
					((LinkUI)link.getUI()).mousePressed(evt);
				}
				else {
					((ViewPaneUI)oViewPane.getUI()).mousePressed(evt);
				}
			}
			return;
		}*/

		if (oViewPane.isScribblePadBack()) {
			((ViewPaneUI)oViewPane.getUI()).mousePressed(evt);
			return;
		}

	    last_x = -1;
		last_y = -1;

		// start dragging if left or right mouse button is pressed
		boolean isLeftMouse = SwingUtilities.isLeftMouseButton(evt);
		if (isLeftMouse) {
			int x = evt.getX();
			int y = evt.getY();

			start_x = x;
			start_y = y;

		    last_x = x;
			last_y = y;

			bDragging = true;
			if (current_tool == UIScribblePad.PENCIL) {
				UIShape shape = new UIShape(x, y, 0, 0, current_tool, current_colour, current_thickness);
				lastScribble = new Vector(51);
				lastScribble.addElement(shape);
			}
		}
		evt.consume();
  	}

	/**
	 * Handles mouse click operations.
	 * @param evt, the MouseEvent generated.
	 */
  	public void mouseClicked(MouseEvent evt) {

		/*
		if (current_tool == UIScribblePad.NO_TOOL) {
			UINode node = oViewPane.getUI().isMouseOnANode(evt);
			if (node != null) {
				((NodeUI)node.getUI()).mouseClicked(evt);
			}
			else {
				UILink link = oViewPane.getUI().isMouseOnALink(evt);
				if (link != null) {
					((LinkUI)link.getUI()).mouseClicked(evt);
				}
				else {
					((ViewPaneUI)oViewPane.getUI()).mouseClicked(evt);
				}
			}
			return;
		}
		*/
		if (oViewPane.isScribblePadBack()) {
			((ViewPaneUI)oViewPane.getUI()).mouseClicked(evt);
			return;
		}
		else {
			evt.consume();
		}
	}

	/**
	 * Handles drag and drop finish operations.
	 * @param evt, the MouseEvent generated.
	 */
  	public void mouseReleased(MouseEvent evt) {

		/*
		if (current_tool == UIScribblePad.NO_TOOL) {
			UINode node = oViewPane.getUI().isMouseOnANode(evt);
			if (node != null) {
				((NodeUI)node.getUI()).mouseReleased(evt);
			}
			else {
				UILink link = oViewPane.getUI().isMouseOnALink(evt);
				if (link != null) {
					((LinkUI)link.getUI()).mouseReleased(evt);
				}
				else {
					((ViewPaneUI)oViewPane.getUI()).mouseReleased(evt);
				}
			}
			return;
		}*/

		if (oViewPane.isScribblePadBack()) {
			((ViewPaneUI)oViewPane.getUI()).mouseReleased(evt);
			return;
		}

		boolean isLeftMouse = SwingUtilities.isLeftMouseButton(evt);
		if (isLeftMouse && bDragging) {
			bDragging = false;
			int stop_x = evt.getX();
			int stop_y = evt.getY();

			if (current_tool == UIScribblePad.PENCIL) {
				UIShape shape = new UIShape(stop_x, stop_y, 0, 0, current_tool, current_colour, current_thickness);
				lastScribble.addElement(shape);

				data.addElement((Vector)lastScribble.clone());
				DrawEdit edit = new DrawEdit(oScribblePad, (Vector)lastScribble.clone());
				lastScribble.removeAllElements();
				oViewPane.getViewFrame().getUndoListener().postEdit(edit);
				oViewPane.getViewFrame().refreshUndoRedo();
			}
			else if (current_tool == UIScribblePad.OVAL || current_tool == UIScribblePad.RECTANGLE) {
				int width = 0;
				int height = 0;
				int draw_x = 0;
				int draw_y = 0;
				if (start_x > stop_x) {
					width = start_x-stop_x;
					draw_x = stop_x;
				}
				else {
					width = stop_x-start_x;
					draw_x = start_x;
				}
				if (start_y > stop_y) {
					height = start_y-stop_y;
					draw_y = stop_y;
				}
				else {
					height = stop_y-start_y;
					draw_y = start_y;
				}

				UIShape shape = new UIShape(draw_x, draw_y, width, height, current_tool, current_colour, current_thickness);
				shapes.addElement(shape);
				DrawEdit edit = new DrawEdit(oScribblePad, shape);
				oViewPane.getViewFrame().getUndoListener().postEdit(edit);
				oViewPane.getViewFrame().refreshUndoRedo();
			}
			else if (current_tool == UIScribblePad.LINE) {

				UIShape shape = new UIShape(start_x, start_y, stop_x, stop_y, current_tool, current_colour, current_thickness);
				shapes.addElement(shape);
				DrawEdit edit = new DrawEdit(oScribblePad, shape);
				oViewPane.getViewFrame().getUndoListener().postEdit(edit);
				oViewPane.getViewFrame().refreshUndoRedo();
			}
		}

	    last_x = 0;
		last_y = 0;

		evt.consume();
	}

	/**
	 * Invoked when a mouse is dragged (pressed and moved).
	 * @param evt, the MouseEvent generated.
	 */
	public void mouseDragged(MouseEvent evt) {

		/*if (current_tool == UIScribblePad.NO_TOOL) {
			UINode node = oViewPane.getUI().isMouseOnANode(evt);
			if (node != null) {
				((NodeUI)node.getUI()).mouseDragged(evt);
			}
			else {
				UILink link = oViewPane.getUI().isMouseOnALink(evt);
				if (link != null) {
					((LinkUI)link.getUI()).mouseDragged(evt);
				}
				else {
					((ViewPaneUI)oViewPane.getUI()).mouseDragged(evt);
				}
			}
			return;
		}*/
		if (oViewPane.isScribblePadBack()) {
			((ViewPaneUI)oViewPane.getUI()).mouseDragged(evt);
			return;
		}

		boolean isLeftMouse = SwingUtilities.isLeftMouseButton(evt);
		if (isLeftMouse && bDragging) {

		   	Graphics g = oScribblePad.getGraphics();
		    Graphics2D g2d = (Graphics2D)g;
 			g2d.setStroke(new BasicStroke(current_thickness));

			g.setColor(current_colour);

			int x = evt.getX();
			int y = evt.getY();

			if (current_tool == UIScribblePad.PENCIL && last_x != -1 && last_y != -1) {
	   		 	g.drawLine(last_x, last_y, x, y);
				UIShape shape = new UIShape(x, y, 0, 0, current_tool, current_colour, current_thickness);
				lastScribble.addElement(shape);
			}
			else if (current_tool == UIScribblePad.OVAL || current_tool == UIScribblePad.RECTANGLE) {
				if (oScribblePad != null) {
					RepaintManager mgr = RepaintManager.currentManager(oScribblePad);
					mgr.addDirtyRegion(oScribblePad,0,0, oScribblePad.getWidth(),oScribblePad.getHeight());
					mgr.paintDirtyRegions();
				}

				int width = 0;
				int height = 0;
				int draw_x = 0;
				int draw_y = 0;
				if (start_x > x) {
					width = start_x-x;
					draw_x = x;
				}
				else {
					width = x-start_x;
					draw_x = start_x;
				}
				if (start_y > y) {
					height = start_y-y;
					draw_y = y;
				}
				else {
					height = y-start_y;
					draw_y = start_y;
				}
				if (current_tool == UIScribblePad.OVAL) {
					g.drawOval(draw_x, draw_y, width, height);
				}
				else if (current_tool == UIScribblePad.RECTANGLE) {
					g.drawRect(draw_x, draw_y, width, height);
				}
			}
			else if (current_tool == UIScribblePad.LINE) {
				if (oScribblePad != null) {
					RepaintManager mgr = RepaintManager.currentManager(oScribblePad);
					mgr.addDirtyRegion(oScribblePad,0,0, oScribblePad.getWidth(),oScribblePad.getHeight());
					mgr.paintDirtyRegions();
				}

				g.drawLine(start_x, start_y, x, y);
			}

	    	last_x = x;
	    	last_y = y;
		}

		evt.consume();
	}

	/**
	 * Invoked when a mouse is moved in a component.
	 * Does nothing.
	 * @param evt, the MouseEvent generated.
	 */
  	public void mouseMoved(MouseEvent evt) {
		/*if (current_tool == UIScribblePad.NO_TOOL) {
			UINode node = oViewPane.getUI().isMouseOnANode(evt);
			if (node != null) {
				((NodeUI)node.getUI()).mouseMoved(evt);
			}
			else {
				UILink link = oViewPane.getUI().isMouseOnALink(evt);
				if (link != null) {
					((LinkUI)link.getUI()).mouseMoved(evt);
				}
				else {
					((ViewPaneUI)oViewPane.getUI()).mouseMoved(evt);
				}
			}
			return;
		}*/
		if (oViewPane.isScribblePadBack()) {
			((ViewPaneUI)oViewPane.getUI()).mouseMoved(evt);
			return;
		}
		else {
			evt.consume();
		}
	}

	/**
	 * Invoked when a mouse is entered.
	 * Does nothing.
	 * @param evt, the MouseEvent generated.
	 */
  	public void mouseEntered(MouseEvent evt) {
		/*if (current_tool == UIScribblePad.NO_TOOL) {
			UINode node = oViewPane.getUI().isMouseOnANode(evt);
			if (node != null) {
				((NodeUI)node.getUI()).mouseEntered(evt);
			}
			else {
				UILink link = oViewPane.getUI().isMouseOnALink(evt);
				if (link != null) {
					((LinkUI)link.getUI()).mouseEntered(evt);
				}
				else {
					((ViewPaneUI)oViewPane.getUI()).mouseEntered(evt);
				}
			}
			return;
		}*/

		if (oViewPane.isScribblePadBack()) {
			((ViewPaneUI)oViewPane.getUI()).mouseEntered(evt);
			return;
		}
		else {
			evt.consume();
		}
	}

	/**
	 * Invoked when a mouse is exited.
	 * Does nothing.
	 * @param evt, the MouseEvent generated.
	 */
  	public void mouseExited(MouseEvent evt) {

		/*
		if (current_tool == UIScribblePad.NO_TOOL) {
			UINode node = oViewPane.getUI().isMouseOnANode(evt);
			if (node != null) {
				((NodeUI)node.getUI()).mouseExited(evt);
			}
			else {
				UILink link = oViewPane.getUI().isMouseOnALink(evt);
				if (link != null) {
					((LinkUI)link.getUI()).mouseExited(evt);
				}
				else {
					((ViewPaneUI)oViewPane.getUI()).mouseExited(evt);
				}
			}
			return;
		}*/

		if (oViewPane.isScribblePadBack()) {
			((ViewPaneUI)oViewPane.getUI()).mouseExited(evt);
			return;
		}
		else {
			evt.consume();
		}
	}

	/**
	 * Invoked when a key is pressed in a component.
	 * Does nothing.
	 * @param evt, the MouseEvent generated.
	 */
	public void keyPressed(KeyEvent evt) {
		//if (oViewPane.isScribblePadBack()) {
		if (current_tool == UIScribblePad.NO_TOOL) {
			((ViewPaneUI)oViewPane.getUI()).keyPressed(evt);
		}
		else {
			evt.consume();
		}
	}

	/**
	 * Invoked when a key is released in a component.
	 * Does nothing.
	 * @param evt, the MouseEvent generated.
	 */
  	public void keyReleased(KeyEvent evt) {
		//if (oViewPane.isScribblePadBack()) {
		if (current_tool == UIScribblePad.NO_TOOL) {
			((ViewPaneUI)oViewPane.getUI()).keyReleased(evt);
		}
		else {
			evt.consume();
		}
	}

	/**
	 * Invoked when a key is typed in a component.
	 * Does nothing.
	 * @param evt, the MouseEvent generated.
	 */
 	public void keyTyped(KeyEvent evt) {
		//if (oViewPane.isScribblePadBack()) {
		if (current_tool == UIScribblePad.NO_TOOL) {
			((ViewPaneUI)oViewPane.getUI()).keyTyped(evt);
		}
		else {
			evt.consume();
		}
	}
}
