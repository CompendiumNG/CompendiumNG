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

/**
 * This holds basic shape data for the scribble layer.
 *
 * @author	Michelle Bachler
 */
public class UIShape {

	/** The x position for the shape.*/
	private int 		x			= 0;

	/** The y position for the shape.*/
	private int 		y			= 0;

	/** The shape color.*/
	private Color		oColour		= Color.black;

	/** The height of the shape.*/
	private int			nWidth		= 0;

	/** The width of the shape.*/
	private int 		nHeight		= 0;

	/** Oval or Rectangle.*/
	private int			shape_type	= 0;

	/** The thicness to draw the lines of this shape.*/
	private int 		nThickness	= 1;

	/**
	 * A shape with the given information.
	 *
	 * @param x, the x position for the shape.
	 * @param y, the y position for the shape.
	 * @param width, the width of the shape.
	 * @param height, the height of the shape.
	 * @param type, the type of the shape.
	 * @param colour, the colour to draw this shape.
	 * @param thickness, the line thickness to use.
	 */
	public UIShape(int x, int y, int width, int height, int type, Color colour, int thickness) {
		this.x = x;
		this.y = y;
		this.nWidth = width;
		this.nHeight = height;
		this.shape_type = type;
		this.oColour = colour;
		this.nThickness = thickness;
	}

	/**
	 * Return the x position of this shape.
	 * @return int, the x position of this shape.
	 */
	public int getX() {
		return x;
	}

	/**
	 * Return the y position of this shape.
	 * @return int, the y position of this shape.
	 */
	public int getY() {
		return y;
	}

	/**
	 * Return the width of this shape.
	 * @return int, the width of this shape.
	 */
	public int getWidth() {
		return nWidth;
	}

	/**
	 * Return the height of this shape.
	 * @return int, the height of this shape.
	 */
	public int getHeight() {
		return nHeight;
	}

	/**
	 * Return the type of this shape.
	 * @return int, the type of this shape.
	 */
	public int getType() {
		return shape_type;
	}

	/**
	 * Return the colour of this shape.
	 * @return int, the colour of this shape.
	 */
	public Color getColour() {
		return oColour;
	}

	/**
	 * Return the line thickness of this shape.
	 * @return int, the thickness of this shape.
	 */
	public int getThickness() {
		return nThickness;
	}
}
