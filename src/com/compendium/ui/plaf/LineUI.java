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

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;

import com.compendium.ui.*;
import com.compendium.core.ICoreConstants;
import com.compendium.ProjectCompendium;

/**
 * The class which is the base class for handles the drawing of lines.
 *
 * @author	Ron van Hoof / Michelle Bachler
 */
public  class LineUI extends ComponentUI
				implements MouseListener, MouseMotionListener, KeyListener {

	protected final static int		ARROW_ORIENTATION_FREE = 0;
	protected final static int		ARROW_ORIENTATION_VERTICAL = 1;
	protected final static int		ARROW_ORIENTATION_HORIZONTAL = 2;

	/** The selection color used byt his UI for painting selected lines (links).*/
	private static final Color SELECTED_COLOR = Color.yellow;

	/** The UILine instance associated with this UI.*/
	protected	UILine								oLine;

	/** The MouseListener used by this UI.*/
	private		MouseListener						oMouseListener;

	/** The MouseMotionListener used by this UI.*/
	private		MouseMotionListener					oMouseMotionListener;

	/** The KeyListener use by this UI.*/
	private		KeyListener							oKeyListener;

	/**
	 * Create a new LineUI instance.
	 * @param c, the component this is the ui to install for.
	 */
 	public static ComponentUI createUI(JComponent c) {
		return new LineUI();
	}

	/**
	 * Run any install instructions for installing this UI.
	 * @param c, the component this is the ui for.
	 */
	public void installUI(JComponent c) {
		super.installUI(c);
		oLine = (UILine)c;
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
		oLine = null;
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
	 * Draws the line on the given graphics context.
	 *
	 * @param g, the Graphics object for this pain method to use.
	 * @param c, the component to paint.
	 */
	public void paint(Graphics g, JComponent c) {

		UILine line = null;
		if (c instanceof UILine) {
			line = (UILine)c;
		} else {
			return;
		}

		// get from and to points
		Point from = line.getFrom();
		Point to   = line.getTo();

		//--------

		// draw the line
		//Integer fromX = new Integer(from.x);
		//Integer fromY = new Integer(from.y);
		//Integer toX = new Integer(to.x);
		//Integer toY = new Integer(to.y);
		//System.out.println("Drawing line (" + fromX.toString() + ", " + fromY.toString()+ ")---> (" + toX.toString() + ", " + toY.toString()+ ")\n");

		//---------

		// if one of the points is missing don't draw line
		if (from == null || to == null)
			return;

		// determine relative to and from points
		Point f = new Point();
		Point t = new Point();
		if (oLine.getCoordinateType() == UILine.RELATIVE) {
			// coordinates already relative to this components coordinate system
			f = from;
			t = to;
		}
		else {
			// calculate the relative coordinates by converting the coordinates from
			// the parents coordinate system to this components coordinate system
			Container parent = line.getParent();
			if (parent != null) {
				f = SwingUtilities.convertPoint(parent, from, line);
				t = SwingUtilities.convertPoint(parent, to, line);
			}
			else {
				return;
			}
		}

		// set color of line
		if (line.isSelected())
			g.setColor(line.getSelectedColor());
		else
			g.setColor(line.getForeground());

		// draw the line
		/*
		Integer fromX = new Integer(f.x);
		Integer fromY = new Integer(f.y);
		Integer toX = new Integer(t.x);
		Integer toY = new Integer(t.y);
		System.out.println("Drawing line (" + fromX.toString() + ", " + fromY.toString()+ ")---> (" + toX.toString() + ", " + toY.toString()+ ")\n");
		*/

		g.drawLine(f.x, f.y, t.x, t.y);

		// draw arrow(s) if necessary

		switch (line.getArrow()) {
			case ICoreConstants.NO_ARROW: {
				break;
			}
			case ICoreConstants.ARROW_TO: {
 				drawArrow(g, f, t, line.getCurrentArrowHeadWidth());
				break;
			}
			case ICoreConstants.ARROW_FROM: {
				drawArrow(g, t, f, line.getCurrentArrowHeadWidth());
				break;
			}
			case ICoreConstants.ARROW_TO_AND_FROM: {
				drawArrow(g, f, t, line.getCurrentArrowHeadWidth());
				drawArrow(g, t, f, line.getCurrentArrowHeadWidth());
				break;
			}
		}

		if (line.isRollover())
			paintRollover(g,c);

	} // paint

	// Nice barbed type arrow heads, but needs tweaking for size and link style
	/*private static int yCor(int len, double dir) {return (int)(len * Math.cos(dir));}
	private static int xCor(int len, double dir) {return (int)(len * Math.sin(dir));}
	public static void drawArrow(Graphics2D g2d, int xCenter, int yCenter, int x, int y, float stroke) {
		double aDir=Math.atan2(xCenter-x,yCenter-y);
		Polygon tmpPoly=new Polygon();
	    int i1=12+(int)(stroke*2);
	    int i2=6+(int)stroke;							// make the arrow head the same size regardless of the length length
	    tmpPoly.addPoint(x,y);							// arrow tip
	    tmpPoly.addPoint(x+xCor(i1,aDir+.5),y+yCor(i1,aDir+.5));
	    tmpPoly.addPoint(x+xCor(i2,aDir),y+yCor(i2,aDir));
	    tmpPoly.addPoint(x+xCor(i1,aDir-.5),y+yCor(i1,aDir-.5));
	    tmpPoly.addPoint(x,y);							// arrow tip
	    g2d.drawPolygon(tmpPoly);
	    g2d.fillPolygon(tmpPoly);						// remove this line to leave arrow head unpainted
	}*/

	
	/**
	 * CURRENLTY DOES NOTHING.
	 */
	public void paintRollover(Graphics g, JComponent c) {} // paintRollover

	protected void drawArrow (Graphics g, Point a, Point b, int width, int orientation, int lineWidth) {
		int unitx;
		int unity;
		int xpts[] = new int[3];
		int ypts[] = new int[3];
		
		xpts[0]=b.x;
		ypts[0]=b.y;

		if (orientation == ARROW_ORIENTATION_FREE) {
			
			float ratioWidth = (width)*(lineWidth); //CORSAIRE_EVOL previously only (width) was used
			if (ratioWidth > 4*width) {
				ratioWidth = 4*width; // avoid useless disproportion
			}

			double hypo = Math.sqrt((a.x-b.x)*(a.x-b.x)+(a.y-b.y)*(a.y-b.y));
			unitx = (int)(ratioWidth*(b.x-a.x)/hypo);
			unity = (int)(ratioWidth*(b.y-a.y)/hypo);

			if (Math.abs(unitx) > 4*lineWidth) {
				unitx = (int)(4*lineWidth*Math.signum(unitx*1.0));
			}
			if (Math.abs(unity) > 4*lineWidth) {
				unity = (int)(4*lineWidth*Math.signum(unity*1.0));
			}

			xpts[1]=b.x-(int)(unitx-unity/2);
			ypts[1]=b.y-(int)(unity+unitx/2);
			xpts[2]=b.x-(int)(unitx-unity/2+unity);
			ypts[2]=b.y-(int)(unity+unitx/2-unitx);

			//System.out.println("Drawing arrow (" + xpts[0] + ", " + ypts[0] + ")---> (" + xpts[1] + ", ла
			//	+ ypts[1] + ")---> (" + xpts[2] + ", " + ypts[2] + ")\n");
		
// Start Code by Corsaire
		} else if (orientation == ARROW_ORIENTATION_HORIZONTAL) {
			// the arrow's basis is equals 4 * (the connected line width)
			// the sign is useful to set the arrow's nose in the right direction
			if ((b.x-a.x) > 0)
				unitx = 4*lineWidth;
			else
				unitx = -4*lineWidth;

			// Y orientation is symetric, so no sign to take into account
			unity = 2*lineWidth;

			xpts[1]=b.x-unitx;
			ypts[1]=b.y-unity;
			xpts[2]=b.x-unitx;
			ypts[2]=b.y+unity;
		} else if (orientation == ARROW_ORIENTATION_VERTICAL) {
			// X orientation is symetric, so no sign to take into account
			unitx = 2*lineWidth;

			// the arrow's basis is equals 4 * (the connected line width)
			// the sign is useful to set the arrow's nose in the right direction
			if ((b.y-a.y) > 0)
				unity = 4*lineWidth;
			else
				unity = -4*lineWidth;

			xpts[1]=b.x-unitx;
			ypts[1]=b.y-unity;
			xpts[2]=b.x+unitx;
			ypts[2]=b.y-unity;
		}
		
		g.drawPolygon(xpts,ypts,3);
		g.fillPolygon(xpts,ypts,3);
		
// End Code by Corsaire
	}


	/**
	 * Draws a one arrow at the end of the line. The line is given by two points.
	 * The arrow is given by its width.
	 *
	 * @param g the Graphics object to use for drawing.
	 * @param a one end pf the line.
	 * @param b the other end of the line.
	 * @param width the width of the arrow head to draw.
	 */
	protected void drawArrow(Graphics g, Point a, Point b, int width)	{

		double hypo;
		int unitx;
		int unity;
		int xpts[];
		int ypts[];

		hypo=Math.sqrt((a.x-b.x)*(a.x-b.x)+(a.y-b.y)*(a.y-b.y));
		unitx=(int)(width*(b.x-a.x)/hypo);
		unity=(int)(width*(b.y-a.y)/hypo);
		xpts=new int[3]; ypts=new int[3];
		xpts[0]=b.x; ypts[0]=b.y;
		xpts[1]=b.x-unitx-unity/2; ypts[1]=b.y-unity+unitx/2;
		xpts[2]=b.x-unitx-unity/2+unity; ypts[2]=b.y-unity+unitx/2-unitx;

		//System.out.println("Drawing arrow (" + xpts[0] + ", " + ypts[0] + ")---> (" + xpts[1] + ", " + ypts[1] + ")---> (" + xpts[2] + ", " + ypts[2] + ")\n");

		g.drawPolygon(xpts,ypts,3);
		g.fillPolygon(xpts,ypts,3);
	}

	/**
	 * Return the preferred size of this component.
	 * @param c, the component to return the preferred size for.
	 * @return Rectangle, the preferred size of this component.
	 */
	public Dimension getPreferredSize(JComponent c) {
		UILine line = (UILine)c;
		Point from = line.getFrom();
		Point to	 = line.getTo();

		// if one of the points is missing no line could be drawn
		if (from == null || to == null)
			return new Dimension(0,0);

		int width		= Math.abs(from.x - to.x);
		int height	= Math.abs(from.y - to.y);

		// make sure that the width and height are large enough
		// to fit a possible arrow, the line thickness and takes into account the minimum width
		int w = Math.max(line.getLineThickness(), line.getMinWidth());
		if ((line.getArrow() != ICoreConstants.NO_ARROW) && (line.getCurrentArrowHeadWidth() > w))
			w = line.getCurrentArrowHeadWidth();

		width += w;
		height += w;

		return new Dimension(width, height);
	}

	/**
	 * Return the minimum size of this component.
	 * @param c, the component to return the minimum size for.
	 * @return Rectangle, the minimum size of this component.
	 * @see #getPreferredSize
	 */
	public Dimension getMinimumSize(JComponent c) {
		return getPreferredSize(c);
	}

	/**
	 * Return the maximum size of this component.
	 * @param c, the component to return the maximum size for.
	 * @return Rectangle, the maximum size of this component.
	 * @see #getPreferredSize
	 */
	public Dimension getMaximumSize(JComponent c) {
		return getPreferredSize(c);
	}

	/**
	 * Return the prefrerred bounds of this component.
	 * @param c, the component to return the preferred bounds for.
	 * @return Rectangle, the preferred bounds of this component.
	 */
	public Rectangle getPreferredBounds(JComponent c) {

		UILine line = (UILine)c;
		Point from = line.getFrom();
		Point to	 = line.getTo();

		if (from == null || to == null)
			return new Rectangle(0,0,0,0);

		Dimension size = getPreferredSize(c);

		// determine absolute to and from points in parent's coordinate system
		Point f = new Point(0,0);
		Point t = new Point(0,0);
		if (oLine.getCoordinateType() == UILine.ABSOLUTE) {
			// coordinates already relative to this components coordinate system
			f = from;
			t = to;
		}
		else {
			// calculate the absolute coordinates by converting the coordinates from
			// this components coordinate system to the parents coordinate system
			Container parent = line.getParent();
			if (parent != null) {
				f = SwingUtilities.convertPoint(line, from, parent);
				t = SwingUtilities.convertPoint(line, to, parent);
			}
		}

		int x = Math.min(f.x, t.x);
		int y = Math.min(f.y, t.y);

		// give room for possible arrow and/or line thickness
		int w = Math.max(line.getLineThickness(), line.getMinWidth());
		if ((line.getArrow() != ICoreConstants.NO_ARROW) && (line.getCurrentArrowHeadWidth() > w))
			w = line.getCurrentArrowHeadWidth();

		x -= w/2;
		y -= w/2;

		return new Rectangle(x, y, size.width, size.height);
	}


	/***** EVENT MOUSE HANDLING METHODS *****/

	/**
	 * Invoked when a mouse is pressed.
	 * @param evt, the MouseEvent generated.
	 */
	public void mousePressed(MouseEvent evt) {
		if (oLine != null)
			oLine.getParent().dispatchEvent(evt);
	}

	/**
	 * Invoked when a mouse is clicked.
	 * @param evt, the MouseEvent generated.
	 */
	public void mouseClicked(MouseEvent evt) {
		if (oLine != null)
			oLine.getParent().dispatchEvent(evt);
	}

	/**
	 * Invoked when a mouse is released.
	 * @param evt, the MouseEvent generated.
	 */
	public void mouseReleased(MouseEvent evt) {
		if (oLine != null)
			oLine.getParent().dispatchEvent(evt);
	}

	/**
	 * Invoked when a mouse is entered.
	 * @param evt, the MouseEvent generated.
	 */
	public void mouseEntered(MouseEvent evt) {
		if (oLine != null)
			oLine.getParent().dispatchEvent(evt);
	}

	/**
	 * Invoked when a mouse is exited.
	 * @param evt, the MouseEvent generated.
	 */
	public void mouseExited(MouseEvent evt) {
		if (oLine != null)
			oLine.getParent().dispatchEvent(evt);
	}

	/**
	 * Invoked when a mouse is dragged (pressed and moved).
	 * @param evt, the MouseEvent generated.
	 */
	public void mouseDragged(MouseEvent evt) {
		if (oLine != null)
			oLine.getParent().dispatchEvent(evt);
	}

	/**
	 * Invoked when a mouse is moved in a component.
	 * @param evt, the MouseEvent generated.
	 */
	public void mouseMoved(MouseEvent evt) {
		if (oLine != null)
			oLine.getParent().dispatchEvent(evt);
	}

	/**
	 * Invoked when a key is pressed in a component.
	 * @param evt, the MouseEvent generated.
	 */
	public void keyPressed(KeyEvent evt) {
		if (oLine != null)
			oLine.getParent().dispatchEvent(evt);
	}

	/**
	 * Invoked when a key is released in a component.
	 * @param evt, the MouseEvent generated.
	 */
	public void keyReleased(KeyEvent evt) {
		if (oLine != null)
			oLine.getParent().dispatchEvent(evt);
	}

	/**
	 * Invoked when a key is typed in a component.
	 * @param evt, the MouseEvent generated.
	 */
	public void keyTyped(KeyEvent evt) {
		if (oLine != null)
			oLine.getParent().dispatchEvent(evt);
	}
}
