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

import java.awt.*;
import java.awt.geom.*;
import javax.swing.*;

import com.compendium.ui.plaf.*;
import com.compendium.core.ICoreConstants;

/**
 * This is the base class for links in Compendium maps.
 *
 * @author	Ron van Hoof / Michelle Bachler
 */
public class UILine extends JComponent {

	/** Defines the points as points in parent component's coordinate system */
	public final static int ABSOLUTE			= 0;

	/** Defines the points as points in this component's coordinate system */
	public final static int RELATIVE			= 1;

	/** A reference to the link arrow property for PropertyChangeEvents.*/
    public static final String ARROW_PROPERTY 		= "linkarrow"; //$NON-NLS-1$

	/** A reference to the link arrow property for PropertyChangeEvents.*/
    public static final String THICKNESS_PROPERTY 		= "linkthickness"; //$NON-NLS-1$

	/** The default arrow head width.*/
	protected int 		nArrowWidth				= 7;

	/** The current arrow head width - scaled.*/
	protected 	int 	nCurrentArrowWidth		= 7;

	/** The line thickness.*/
	protected int		nThickness				= 1;

	/** The current line thickness - scaled.*/
	protected 	int 	nCurrentThickness		= 1;

	/** The arrow style for this line.*/
	private int			nArrow				= ICoreConstants.ARROW_TO;
	
	/** The origin point fo the line.*/
	private Point		ptFrom				= null;

	/** The destination point of the line.*/
	private Point		ptTo				= null;

	/** the coordinate type for this line (ABSOLUTE/RELATIVE).*/
	private int			nCoordinateType		= ABSOLUTE;


	/** Is the line currently selected?*/
	private boolean		bSelected				= false;

	/** Is the line currently rolledover?*/
	private boolean		bRollover				= false;

	/** The selection line color.*/
	private Color		oSelectedColor		= Color.yellow;

	/** The minimum component width for the line.*/
	private int			nMinWidth				= nThickness;


	/**
	 * Returns the intersecting points of the line with the the given
	 * start and end point and given rectangle. If the line does not
	 * intersect with the rectangle an empty array will be returned
	 *
	 * @param r the Rectangle to check.
	 * @param a the origin point of the line to check.
	 * @param b the destination point of the line to check.
	 * @return Point[] the points the rectangle and line intersect.
	 */
	public static Point[] intersectionWithRectangle(Rectangle r, Point a, Point b) {

		Point[] pts = new Point[2];
		Point p1 = computeIntersectionWithRectangle(r, a, b);
		Point p2 = computeIntersectionWithRectangle(r, b, a);

		if (p1 != null) {
			pts[0] = p1;
			// if line intersect in only one point, don't store second point
			if ((p2 != null) && ((p1.x != p2.x) || (p1.y != p2.y)))
				pts[1] = p2;
		}
		else if (p2 != null) {
			pts[0] = p2;
		}

		return pts;
	}
	
	/**
	 * Returns the intersecting point of the line with the given from and
	 * to point with the given rectangle. To get the other intersecting point
	 * the from and to points need to be reversed.
	 *
	 * @param r the Rectangle to check.
	 * @param from the origin point of the line to check.
	 * @param to the destination point of the line to check.
	 * @return Point the point the rectangle and line intersect, else null it they don't.
	 */
	private static Point computeIntersectionWithRectangle(Rectangle r, Point from, Point to) {
		Point pt = new Point();

		if ((from.x == to.x)&& (from.y == to.y)) return null;

		//line to the right of rectangle
		if ((from.x>r.x+r.width) && (to.x>r.x+r.width)) return null;
		//line below rectangle
		if ((from.y>r.y+r.height) && (to.y>r.y+r.height)) return null;
		//line to left of rectangle
		if ((from.x<r.x) && (to.x<r.x)) return null;
		//line above rectangle
		if ((from.y<r.y) && (to.y<r.y)) return null;

		if (to.y != from.y) {
			if (r.y+r.height<=to.y) {
	   			pt.y=r.y+r.height;
	   			pt.x=from.x+(to.x-from.x)*(r.y+r.height-from.y)/(to.y-from.y);
				}
			else {
				pt.y=r.y;
				pt.x=from.x+(to.x-from.x)*(r.y-from.y)/(to.y-from.y);
			}
		}

		if (to.y==from.y || r.x>pt.x || pt.x>=r.x+r.width) {
	  		if (r.x+r.width<=to.x) {
			   	pt.y=from.y+(to.y-from.y)*(r.x+r.width-from.x)/(to.x-from.x);
	   			pt.x=r.x+r.width;
			}
			else {
	   			pt.y=from.y+(to.y-from.y)*(r.x-from.x)/(to.x-from.x);
	   			pt.x=r.x;
	  		}
		}

		// check if point is in boundaries of rectangle
		if (contains(r, pt))
			return pt;
		else
			return null;
	}

	/**
	 * This contains method is a workaround for a bug in the contains method of Rectangle.
	 * It does not include the right and bottom sides as part of the rectangle.
	 *
	 * @param r, the Rectangle to check for the point in.
	 * @param p, the point for check for.
	 * @return boolean, true if the given rectangle contains the given point, else false.
	 */
	private static boolean contains(Rectangle r, Point p) {
		return (p.x >= r.x) && ((p.x - r.x) <= r.width) && (p.y >= r.y) && ((p.y-r.y) <= r.height);
	}

	/**
	 * Creates a line with the given to and from points in the coordinate
	 * system defined by the nType and adds arrows as defined by nArrow.
	 * @param ptFrom, the origin point for the line.
	 * @param ptTo, the destination point for the line.
	 * @param arrow, the arrow style for this line.
	 * @param nCoordinateType, the coordinate type for this line (ABSOLUTE / RELATIVE).
	 */
	public UILine(Point ptFrom, Point ptTo, int nArrow, int nCoordinateType) {
		setFrom(ptFrom);
		setTo(ptTo);
		setArrow(nArrow);
		setCoordinateType(nCoordinateType);
		updateUI();
	}

	/**
	 * Creates a line with the given to and from points in the coordinate
	 * system of its parent (absolute) with the given arrow.
	 * @param ptFrom, the origin point for the line.
	 * @param ptTo, the destination point for the line.
	 * @param arrow, the arrow style for this line.
	 */
	public UILine(Point ptFrom, Point ptTo, int nArrow) {
		this(ptFrom, ptTo, nArrow, ABSOLUTE);
	}

	/**
	 * Creates a line with the given to and from points in the coordinate
	 * system of its parent (absolute) and no arrows.
	 * @param ptFrom, the origin point for the line.
	 * @param ptTo, the destination point for the line.
	 */
	public UILine(Point ptFrom, Point ptTo) {
		this(ptFrom, ptTo, ICoreConstants.ARROW_TO, ABSOLUTE);
	}

	/**
	 * Creates a line with an absolute coordinate system (of the parent) and no arrows.
	 */
	public UILine() {
		this(null, null, ICoreConstants.NO_ARROW, ABSOLUTE);
	}

  	/**
   	 * Returns the L&F object that renders this component.
   	 *
   	 * @return LineUI object
   	 */
  	public LineUI getUI() {
		return (LineUI)ui;
  	}

  	/**
   	 * Sets the L&F object that renders this component.
   	 *
   	 * @param ui,  the LineUI L&F object
   	 */
  	public void setUI(LineUI ui) {
		super.setUI(ui);
  	}

  	/**
   	* Notification from the UIFactory that the L&F
   	* has changed.
   	*
   	* @see JComponent#updateUI
   	*/
   	public void updateUI() {
		//setUI((LinkUI)UIManager.getUI(this));
		setUI(new LineUI());
		invalidate();
  	}

 	/**
   	* Returns a string that specifies the name of the l&f class
   	* that renders this component.
   	*
   	* @return String "LineUI"
   	*
   	* @see JComponent#getUIClassID
   	* @see UIDefaults#getUI
   	*/
  	public String getUIClassID() {
		return "LineUI"; //$NON-NLS-1$
  	}

	/**
	 * Return the origin point for this line.
	 * @return Point, the origin point for this line.
	 */
	public Point getFrom() {
		return ptFrom;
	}

	/**
	 * Set the origin point for this line. Fires a property change event.
	 * @param pt, the origin point for this line.
	 */
	public void setFrom(Point pt) {
		if (ptFrom == null || pt.x != ptFrom.x || pt.y != ptFrom.y) {
			Point oldValue = ptFrom;
			ptFrom = pt;
			firePropertyChange("from", oldValue, ptFrom); //$NON-NLS-1$

			repaint();
		}
	}

	/**
	 * Return the destination point for this line.
	 * @return Point, the destination point for this line.
	 */
	public Point getTo() {
		return ptTo;
	}

	/**
	 * Set the destination point for this line. Fires a property change event.
	 * @param pt, the destination point for this line.
	 */
	public void setTo(Point pt) {
 		if (ptTo == null || pt.x != ptTo.x || pt.y != ptTo.y) {
			Point oldValue = ptTo;
			ptTo = pt;
			firePropertyChange("to", oldValue, ptTo); //$NON-NLS-1$

			repaint();
		}
	}

	/**
	 * Return the current arrow head style for this line.
	 * @return int, the arrow head style.
	 * @see com.compendium.core.ICoreConstants#NO_ARROW
	 * @see com.compendium.core.ICoreConstants#ARROW_TO
	 * @see com.compendium.core.ICoreConstants#ARROW_FROM
	 * @see com.compendium.core.ICoreConstants#ARROW_TO_AND_FROM
	 */
	public int getArrow() {
		return nArrow;
	}

	/**
	 * Set the arrow head style for this line. Fires a property change event.
	 * @param arrow, the arrow head style for this line.
	 * @see com.compendium.core.ICoreConstants#NO_ARROW
	 * @see com.compendium.core.ICoreConstants#ARROW_TO
	 * @see com.compendium.core.ICoreConstants#ARROW_FROM
	 * @see com.compendium.core.ICoreConstants#ARROW_TO_AND_FROM
	 */
	public void setArrow(int arrow) {
		if (nArrow == arrow)
			return;

		Integer oldValue = new Integer(nArrow);
		nArrow = arrow;
		firePropertyChange(ARROW_PROPERTY, oldValue, new Integer(arrow));

		repaint();
	}

	/** 
	 * Return the current arrow head width - after scaling 
	 */
	public int getCurrentArrowHeadWidth() {
		return this.nCurrentArrowWidth;
	}
	
	/**
	 * Return the current line thickness for this line.
	 * @return int, the current line thickness.
	 */
	public int getLineThickness() {
		return nThickness;
	}

	/**
	 * Set the line thickness for this line. Fires a property change event.
	 * @param thickness, the thickness for this line.
	 */
	public void setLineThickness(int thickness) {
		if (nThickness == thickness)
			return;

		int oldValue = nThickness;
		nThickness = thickness;
		nArrowWidth = thickness;
		
		firePropertyChange(THICKNESS_PROPERTY, oldValue, nThickness);

		repaint();
	}

	/** 
	 * Return the current line thickness - after scaling 
	 */
	public int getCurrentLineThickness() {
		return this.nCurrentThickness;
	}

	/**
	 * return the coordinate type for this line (ABSOLUTE / RELATIVE).
	 * @return int, the coordinate type for this line.
	 */
	public int getCoordinateType() {
		return nCoordinateType;
	}

	/**
	 * Set the Coordinate type for this line. Fires a property change event.
	 * @param type, the coordinate type for this line (ABSOLUTE / RELATIVE).
	 */
	public void setCoordinateType(int type) {
		if (nCoordinateType == type)
			return;

		int oldValue = nCoordinateType;
		nCoordinateType = type;
		firePropertyChange("coordinatetype", oldValue, nCoordinateType); //$NON-NLS-1$

		repaint();
	}

	/**
	 * Return the selection status for this line.
	 * @return boolean, the selection status for this line.
	 */
	public boolean isSelected() {
		return bSelected;
	}

	/**
	 * Set the selection status for this line. Fires a property change event.
	 * @param selected, is the line currently selected or not?
	 */
	public void setSelected(boolean selected) {
		if (bSelected == selected)
			return;

		boolean oldValue = bSelected;
		bSelected = selected;
		firePropertyChange("selected", oldValue, bSelected); //$NON-NLS-1$

		repaint();
	}

	/**
	 * Return the rollover status for this line.
	 * @return boolean, the rollover status for this line.
	 */
	public boolean isRollover() {
		return bRollover;
	}

	/**
	 * Set the rollover status for this line. Fires a property change event.
	 * @param rollover, the rollover status for this line.
	 */
	public void setRollover(boolean rollover) {
		if (bRollover == rollover)
			return;

		boolean oldValue = bRollover;
		bRollover = rollover;
		firePropertyChange("rollover", oldValue, bRollover); //$NON-NLS-1$

		repaint();
	}

	/**
	 * Return the current selection color for this line.
	 * @return Color, the current selection color for this line.
	 */
	public Color getSelectedColor() {
		return oSelectedColor;
	}

	/**
	 * Set the selection colour for this line. Fires a property change event.
	 * @param c, the new selection color for this line.
	 */
	public void setSelectedColor(Color c) {
		Color oldValue = oSelectedColor;
		oSelectedColor = c;
		firePropertyChange("selectedcolor", oldValue, oSelectedColor); //$NON-NLS-1$

		repaint();
	}

	/**
	 * Return the current minimum width for this line.
	 * @return the minimum width for this line.
	 */
	public int getMinWidth() {
		return nMinWidth;
	}

	/**
	 * Set the minimum width for this line.  Fires a property change event.
	 * @param width the new width for this line.
	 */
	public void setMinWidth(int width) {
		if (nMinWidth == width)
			return;

		int oldValue = nMinWidth;
		nMinWidth = width;
		firePropertyChange("minwidth", oldValue, nMinWidth); //$NON-NLS-1$

		repaint();
	}

	/**
	 * Override to always return false.
	 * @return boolean false.
	 */
	public boolean isOpaque() {
		return false;
	}

	/**
	 * Returns the intersecting point of this line with the
	 * given line. If the two lines are parallel null will be
	 * returned.
	 *
	 * @param line the line to check.
	 * @return Point the intersecting point of this line with the given line, else null if thye do not intercept.
	 */
	public Point intersectionWithLine(UILine line) {
		// calculate the slopes
		double dSlope1 = slope();
		double dSlope2 = line.slope();

		// check if lines are parallel;
		if (dSlope1 == dSlope2)
			return null;

		// calculate the y interceptions
		double dYIntercept1 = yIntercept();
		double dYIntercept2 = line.yIntercept();

		// calculate the intersecting point
		double dX = (dYIntercept2 - dYIntercept1)/(dSlope1 - dSlope2);
		double dY = dSlope1 * ptFrom.x + dYIntercept1;

		return new Point(new Double(dX).intValue(), new Double(dY).intValue());
	}

	/**
	 * Returns the intersecting points of this line with the
	 * given rectangle. If the line does not intersect with the
	 * rectangle an empty array will be returned.
	 *
	 * @param r, the Rectangle to return the interception points for.
	 * @return Point[], the the intersecting points of this line with the given rectangle.
	 */
	public Point[] intersectionWithRectangle(Rectangle r) {
		Point[] pts = new Point[2];
		Point p1 = computeIntersectionWithRectangle(r, ptFrom, ptTo);
		Point p2 = computeIntersectionWithRectangle(r, ptTo, ptFrom);

		if (p1 != null && p2 != null) {
			pts[0] = p1;
			// if line intersect in only one point, don't store second point
			if ((p1.x != p2.x) || (p1.y != p2.y))
				pts[1] = p2;
		}
		return pts;
	}

	/**
	 * Calculates the slope (m) of the line.
	 */
	public double slope() {
		return (ptTo.y - ptFrom.y)/(ptTo.x - ptFrom.x);
	}

	/**
	 * Calculates the y-intercept
	 */
	public double yIntercept() {
		return ptFrom.y - slope() * ptFrom.x;
	}

	/**
	 * Checks whether the given point (pt) is on or close to the
	 * straight line formed by ptFrom and ptTo. The parameter 'd'
	 * defines the tolerance (max. allowable distance from the point
	 * to the line.
	 *
	 * @param pt, the point to check.
	 * @param d, the tolerance for the check.
	 * @return boolean true if the given point (pt) is on or close to the
	 * line formed by ptFrom and ptTo, else false.
	 */
	public boolean onLine(Point pt, int d) {

		// To allow for new line thickness add half the thickness to the tolerance
		d += nCurrentThickness/2;
		
		int x1, y1, x2, y2, xp, yp;
		int xmin, ymin, xmax, ymax;
		double x, y, slope, dd, D2, dx, dy;

		x1 = ptFrom.x;
		y1 = ptFrom.y;
		x2 = ptTo.x;
		y2 = ptTo.y;
		xp = pt.x;
		yp = pt.y;

		// if point close to start point
		if (Math.abs(xp - x1) <= d && Math.abs(yp - y1) <= d) {
			return true;
		}

		// if point close to end point
		if (Math.abs(xp - x2) <= d && Math.abs(yp - y2) <= d) {
			return true;
		}

		if (x1 < x2) {
			xmin = x1 - d;
			xmax = x2 + d;
		}
		else {
			xmin = x2 - d;
			xmax = x1 + d;
		}

		// check if point is outside of the line x boundaries
		if (xp < xmin || xmax < xp)
			return false;

		if (y1 < y2) {
			ymin = y1 - d;
			ymax = y2 + d;
		}
		else {
			ymin = y2 - d;
			ymax = y1 + d;
		}

		// check if point is outside of the line y boundaries
		if (yp < ymin || ymax < yp)
			return false;

		// check if we are dealing with horizontal, vertical or angled line
		// and determine the desired x and y coordinate for the point on the line.
		if (x2 == x1) {
			x = x1;
			y = yp;
		}
		else if (y1 == y2) {
			x = xp;
			y = y1;
		}
		else {
			slope = ((double)(x2 - x1)) / ((double)(y2-y1));
			y = (slope * (xp - x1 + slope * y1) + yp) / (1 + slope * slope);
			x = ((double) x1) + slope * (y - y1);
		}

		// determine if the point is on or close enough to the line.
		dx = ((double)xp) - x;
		dy = ((double)yp) - y;
		D2 = dx * dx + dy * dy;
		dd = d * d;

		if (D2 <= dd)
			return true;

		return false;
	}

	/**
	 * Return the preferred bounds for this object.
	 * @return Rectangle, the preferred bounds for this object.
	 */
	public Rectangle getPreferredBounds() {
		if (getUI() != null)
			return getUI().getPreferredBounds(this);
		else
			return getBounds();
	}
}
