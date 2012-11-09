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


/**
 * The ViewProperty object holds additional information about the visual state of a JInternalFrame with a <code>View</code> inside.
 * Note: This is a holding container only and does not currently write to the database
 *
 * @author	Michelle Bachler
 */
public class ViewProperty implements java.io.Serializable {

	//public final static long 	serialVersionUID	=	
	
	/** The user id of the user who created this ViewProperty record.*/
	protected String			sUserID							= "";

	/** The View id for this ViewProperty record.*/
	protected String			sViewID							= "";

	/** The horizontal scrollbar position of the view in this ViewProperty record.*/
	protected int				nHorizontalScrollBarPosition	= 0;

	/** The vertical scrollbar position of the view in this ViewProperty record.*/
	protected int				nVerticalScrollBarPosition		= 0;

	/** The width of the view in this ViewProperty record.*/
	protected int				nWidth							= 300;

	/** The height if the view in this ViewProperty record.*/
	protected int				nHeight							= 300;

	/** The x position of the view in this ViewProperty record.*/
	protected int				nXPos							= 0;

	/** The the y position of the view in this ViewProperty record.*/
	protected int				nYPos							= 0;

	/** Whether the view in this ViewProperty record is iconified.*/
	protected boolean			bIsIcon							= false;

	/** Whether the view in this ViewProperty record is maximized.*/
	protected boolean			bIsMaximum						= false;
	
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
	public boolean getIsIcon() {
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
	public boolean getIsMaximum() {
		return bIsMaximum;
	}

	/**
	 * Sets the boolean representing whether this View has been maximized
	 *
	 * @param bIsMaximum the boolean representing whether this View has been maximized
	 */
	public void setIsMaximum(boolean bIsMaximum) {
		this.bIsMaximum = bIsMaximum;
	}
	
	/**
	 * Returns whether this View should show the tags node indicators.
	 *
	 * @return a boolean representing whether this View should show the tags node indicators.
	 */
	//public boolean getShowTags() {
	//	return bShowTags;
	//}

	/**
	 * Sets the boolean representing whether this View should show the tags node indicators.
	 *
	 * @param bShowTags the boolean representing whether this View should show the tags node indicators.
	 */
	//public void setShowTags(boolean bShowTags) {
	//	this.bShowTags = bShowTags;
	//}	
	
	/**
	 * Returns whether this View should show the detail text node indicators.
	 *
	 * @return a boolean representing whether this View should show the detail text node indicators.
	 */
	//public boolean getShowText() {
	//	return bShowText;
	//}

	/**
	 * Sets the boolean representing whether this View should show the detail text node indicators.
	 *
	 * @param bShowText the boolean representing whether this View should show the detail text node indicators.
	 */
	//public void setShowText(boolean bShowText) {
	//	this.bShowText = bShowText;
	//}	

	/**
	 * Returns whether this View should show the transclusion history node indicators.
	 *
	 * @return a boolean representing whether this View should show the transclusion history node indicators.
	 */
	//public boolean getShowTrans() {
	//	return bShowText;
	//}

	/**
	 * Sets the boolean representing whether this View should show the transclusion history node indicators.
	 *
	 * @param bShowTrans the boolean representing whether this View should show the transclusion history node indicators.
	 */
	//public void setShowTrans(boolean bShowTrans) {
	//	this.bShowTrans = bShowTrans;
	//}	
	
	/**
	 * Returns whether this View should show the map weight node indicators.
	 *
	 * @return a boolean representing whether this View should show the map weight node indicators.
	 */
	//public boolean getShowWeight() {
	//	return bShowWeight;
	//}

	/**
	 * Sets the boolean representing whether this View should show the map weight node indicators.
	 *
	 * @param bShowWeight the boolean representing whether this View should show the map weight node indicators.
	 */
	//public void setShowWeight(boolean bShowWeight) {
	//	this.bShowWeight = bShowWeight;
	//}	
	
	/**
	 * Returns whether this View should show the map weight node indicators.
	 *
	 * @return a boolean representing whether this View should show the map weight node indicators.
	 */
	//public boolean getShowSmallIcons() {
	//	return bShowSmallIcons;
	//}

	/**
	 * Sets the boolean representing whether this View should show small node icons.
	 *
	 * @param bShowSmallIcons the boolean representing whether this View should show small node icons.
	 */
	//public void setShowSmallIcons(boolean bShowSmallIcons) {
	//	this.bShowSmallIcons = bShowSmallIcons;
	//}
	
	/**
	 * Returns whether this View should hide node icons.
	 *
	 * @return a boolean representing whether this View should hide node icons.
	 */
	//public boolean getHideIcons() {
	//	return bHideIcons;
	//}

	/**
	 * Sets the boolean representing whether this View should hide node icons.
	 *
	 * @param bHideIcons the boolean representing whether this View should hide node icons.
	 */
	//public void setHideIcons(boolean bHideIcons) {
	//	this.bHideIcons = bHideIcons;
	//}	
	
	/**
	 * Returns the label default length before going to details of this View
	 *
	 * @return the int of the label default length before going to details of this View
	 */
	//public int getLabelLength() {
	//	return nLabelLength;
	//}

	/**
	 * Sets the label default length before going to details of this View
	 *
	 * @param nWidth the label default length before going to details of this View
	 */
	//public void setLabelLength(int nWidth) {
	//	this.nLabelLength = nWidth;
	//}
	
	/**
	 * Returns the label wrap width of this View
	 *
	 * @return the int of the label wrap width of this View
	 */
	//public int getLabelWrapWidth() {
	//	return nLabelWrapWidth;
	//}

	/**
	 * Sets the label wrap width of this View
	 *
	 * @param nWidth the label wrap width of this View
	 */
	//public void setLabelWrapWidth(int nWidth) {
	//	this.nLabelWrapWidth = nWidth;
	//}
	
	/**
	 * Returns the font size for this View
	 *
	 * @return the int of the font size for this View
	 */
	//public int getFontSize() {
	//	return nFontSize;
	//}

	/**
	 * Sets the font size for this View
	 *
	 * @param nWidth the font size for this View
	 */
	//public void setFontSize(int nFontSize) {
	//	this.nFontSize = nFontSize;
	//}
	
	/**
	 * Returns the the font face for node labels in this View
	 *
	 * @return String the the font face for node labels in this View
	 */
	//public String getFontFace() {
	//	return sFontFace;
	//}

	/**
	 * Sets the the font face for node labels in this View
	 *
	 * @param sFontFace the font face for node labels in this View
	 */
	//public void setFontFace(String sFontFace) {
	//	this.sFontFace = sFontFace;
	//}	
	
	/**
	 * Returns the font style for this View
	 *
	 * @return int font style for this View
	 */
	//public int getFontStyle() {
	//	return nFontStyle;
	//}

	/**
	 * Sets the font style for this View
	 *
	 * @param nStyle the font style for this View
	 */
	//public void setFontStyle(int nStyle) {
	//	this.nFontStyle = nStyle;
	//}
}
