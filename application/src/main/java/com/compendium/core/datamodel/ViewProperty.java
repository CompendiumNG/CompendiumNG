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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The ViewProperty object holds additional information about the visual state of a JInternalFrame with a <code>View</code> inside.
 * Note: This is a holding container only and does not currently write to the database
 *
 * @author	Michelle Bachler
 */
public class ViewProperty implements java.io.Serializable {
	/**
	 * class's own logger
	 */
	final Logger log = LoggerFactory.getLogger(getClass());
	//public final static long 	serialVersionUID	=	
	
	/** The user id of the user who created this ViewProperty record.*/
    private String			sUserID							= "";

	/** The View id for this ViewProperty record.*/
    private String			sViewID							= "";

	/** The horizontal scrollbar position of the view in this ViewProperty record.*/
    private int				nHorizontalScrollBarPosition	= 0;

	/** The vertical scrollbar position of the view in this ViewProperty record.*/
    private int				nVerticalScrollBarPosition		= 0;

	/** The width of the view in this ViewProperty record.*/
    private int				nWidth							= 300;

	/** The height if the view in this ViewProperty record.*/
    private int				nHeight							= 300;

	/** The x position of the view in this ViewProperty record.*/
    private int				nXPos							= 0;

	/** The the y position of the view in this ViewProperty record.*/
    private int				nYPos							= 0;

	/** Whether the view in this ViewProperty record is iconified.*/
    private boolean			bIsIcon							= false;

	/** Whether the view in this ViewProperty record is maximized.*/
    private boolean			bIsMaximum						= false;
	
	/** Whether to display the Tags node indicator.*/
	//protected boolean 			bShowTags						= false;
	
	/** Whether to display the detail text node indicator.*/
	//protected boolean 			bShowText						= false;
	
	/** Whether to display the map weight node indicator.*/
	//protected boolean			bShowWeight						= false;
	
	/** Whether to show the parent view (transclusion history) node indicator.*/
	//protected boolean 			bShowTrans						= false;
	
	/** Whether to show small node icons.*/
	//protected boolean			bShowSmallIcons					= false;

	/** Whether to hide node icons.*/
	//protected boolean			bHideIcons						= false;

	/** Indicates the label length before employing auto popup details box.*/
	//protected int				nLabelLength					= 100;
	
	/** Indicates the label wrap width for this map.*/
	//protected int				nLabelWrapWidth					= 15;
	
	/** Indicates the llabel fon size for this map.*/
	//protected int				nFontSize						= 12;
	
	/** Indicates the label font face for nodes.*/
	//protected String			sFontFace						="Arial";
	
	/** Indicates the label font style for nodes.*/
	//protected int				nFontStyle						=0;


	/**
	 * Constructor, creates a new ViewProperty
	 */
	public ViewProperty() {}

	/**
	 * Returns the User id associated with this View
	 *
	 * @return the String of the User id associated with this View
	 */
	public String getUserID() {
		return sUserID;
	}

	/**
	 * Sets the View id object associated with this View
	 *
	 * @param sUserID the User id associated with this View
	 */
	public void setUserID(String sUserID) {
		this.sUserID = sUserID;
	}

	/**
	 * Returns the View id associated with this View
	 *
	 * @return the String of the View id associated with this View
	 */
	public String getViewID() {
		return sViewID;
	}

	/**
	 * Sets the View id object associated with this View
	 *
	 * @param sViewID the View id associated with this View
	 */
	public void setViewID(String sViewID) {
		this.sViewID = sViewID;
	}

	/**
	 * Returns the horizontal ScrollBar position associated with this View
	 *
	 * @return the int of the horizontal ScrollBar position associated with this ViewProperty
	 */
	public int getHorizontalScrollBarPosition() {
		return nHorizontalScrollBarPosition;
	}

	/**
	 * Sets the horizontal ScrollBar position associated with this View
	 *
	 * @param nHPos the horizontal ScrollBar position associated with this ViewProperty
	 */
	public void setHorizontalScrollBarPosition(int nHPos) {
		this.nHorizontalScrollBarPosition = nHPos;
	}

	/**
	 * Returns the vertical scrollBar position associated with this View
	 *
	 * @return the int of the vertical scrollBar position associated with this View
	 */
	public int getVerticalScrollBarPosition() {
		return nVerticalScrollBarPosition ;
	}

	/**
	 * Sets the vertical scrollBar position object associated with this View
	 *
	 * @param nVPos the vertical scrollBar position associated with this View
	 */
	public void setVerticalScrollBarPosition(int nVPos) {
		this.nVerticalScrollBarPosition = nVPos;
	}

	/**
	 * Returns the width of this View
	 *
	 * @return the int of the width of this View
	 */
	public int getWidth() {
		return nWidth;
	}

	/**
	 * Sets the width of this View
	 *
	 * @param nWidth the width of this View
	 */
	public void setWidth(int nWidth) {
		if (nWidth != 0)
			this.nWidth = nWidth;
	}

	/**
	 * Returns the height of this View
	 *
	 * @return the int of the height of this View
	 */
	public int getHeight() {
		return nHeight;
	}

	/**
	 * Sets the height of this View
	 *
	 * @param nHeight the height of this View
	 */
	public void setHeight(int nHeight) {
		if (nHeight != 0)
		this.nHeight = nHeight;
	}

	/**
	 * Returns the x position of this View
	 *
	 * @return the int of the x position of this View
	 */
	public int getXPosition() {
		return nXPos;
	}

	/**
	 * Sets the the x position of this View
	 *
	 * @param nXPos the the x position of this View
	 */
	public void setXPosition(int nXPos) {
		this.nXPos = nXPos;
	}

	/**
	 * Returns the y position of this View
	 *
	 * @return the int of the y position of this View
	 */
	public int getYPosition() {
		return nYPos;
	}

	/**
	 * Sets the the y position of this View
	 *
	 * @param nYPos the the y position of this View
	 */
	public void setYPosition(int nYPos) {
		this.nYPos = nYPos;
	}

	/**
	 * Returns whether this View has been iconified
	 *
	 * @return a boolean representing whether this View has been iconified
	 */
	public boolean isIcon() {
		return bIsIcon;
	}

	/**
	 * Sets the boolean representing whether this View has been iconified
	 *
	 * @param bIsIcon the boolean representing whether this View has been iconified
	 */
	public void setIsIcon(boolean bIsIcon) {
		this.bIsIcon = bIsIcon;
	}

	/**
	 * Returns whether this View has been maximized
	 *
	 * @return a boolean representing whether this View has been maximized
	 */
	public boolean isMaxized() {
		return bIsMaximum;
	}

	/**
	 * Sets the boolean representing whether this View has been maximized
	 *
	 * @param bIsMaximum the boolean representing whether this View has been maximized
	 */
	public void setMaximized(boolean bIsMaximum) {
		this.bIsMaximum = bIsMaximum;
	}
	
}
