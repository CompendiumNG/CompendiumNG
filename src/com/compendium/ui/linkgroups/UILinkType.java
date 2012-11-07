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

package com.compendium.ui.linkgroups;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.awt.dnd.*;
import java.awt.datatransfer.*;
import java.util.*;
import java.io.*;

import javax.swing.*;

import com.compendium.*;
import com.compendium.core.CoreUtilities;
import com.compendium.core.ICoreConstants;
import com.compendium.core.datamodel.Model;
import com.compendium.ui.FormatProperties;

/**
 * This class holds the information for a link type.
 *
 * @author	Michelle Bachler
 */
public class UILinkType extends Component { // ONLY EXTENDS COMPONENT FOR SORTING

	/** The name of this link type.*/
	private String 				sName 			= ""; //$NON-NLS-1$

	/** The colour to draw this link type.*/
	private Color 				oColour 		= Color.black;

	/** The unique if of this link type.*/
	private String 				sID 			= ""; //$NON-NLS-1$

	/** Indicates whether to assign the name as the label when this link type is chosen.*/
	private String 				sLabel 			= ""; //$NON-NLS-1$
	
	/** Indicates the arrow type used on this link.*/
	protected int				nArrowType						= ICoreConstants.ARROW_TO;

	/** Indicates the link style used on this link.*/
	protected int				nLinkStyle						= ICoreConstants.STRAIGHT_LINK;

	/** Indicates the label font style for nodes.*/
	protected int				nLinkDashed						= ICoreConstants.PLAIN_LINE;

	/** Indicates the label font style for nodes.*/
	protected int				nLinkWeight						= 1;
	
	/** Indicates the label wrap width for this map.*/
	//protected int				nLabelWrapWidth					=-1;  // picks up default from Model
		
	/** Indicates the label font size for this map.*/
	//protected int				nFontSize						=-1; // picks up default from Model
	
	/** Indicates the label font face for nodes.*/
	//protected String			sFontFace						=""; // picks up default from Model
	
	/** Indicates the label font style for nodes.*/
	//protected int				nFontStyle						=-1; // picks up default from Model

	/** Indicates the label font style for nodes.*/
	//protected int				nForeground						=-1; // picks up default from Model

	/** Indicates the label font style for nodes.*/
	//protected int				nBackground						=-1; // picks up default from Model

	/**
	 * The Constructor.
	 */
  	public UILinkType() {}

	/**
	 * The Constructor.
	 *
	 * @param sName, the name of this link type.
	 * @param oColour, the colour associated with this link type.
	 * @param sID the unique id for this link type.
	 * @param sLabel the label for this link type.
	 */
  	public UILinkType(String sName, Color oColour, String sID, String sLabel) {
		this.sName = sName;
		this.oColour = oColour;
		this.sID = sID;
		this.sLabel = sLabel;
  	}

	/**
	 * Return the id associated with this link type.
	 * @return String, the id of this link type.
	 */
	public String getID() {
		return sID;
	}

	/**
	 * Set the id associated with this link type.
	 * @param sID the id associated with this link type.
	 */
	public void setID(String sID) {
		this.sID = sID;
	}

	/**
	 * Return the name associated with this link type.
	 * @return String, the name of this link type.
	 */
	public String getName() {
		return sName;
	}

	/**
	 * Set the name associated with this link type.
	 * @param sName the name associated with this link type.
	 */
	public void setName(String sName) {
		this.sName = sName;
	}

	/**
	 * Set the colour associated with this link type.
	 * @param oColour the colour associated with this link type.
	 */
	public void setColour(Color oColour) {
		this.oColour = oColour;
	}

	/**
	 * Return the colour associated with this link type.
	 * @return Color, the colour associated with this link type.
	 */
	public Color getColour() {
		return oColour;
	}

	/**
	 * Return the label for this link type.
	 * @return String, the label for this link type.
	 */
	public String getLabel() {
		return sLabel;
	}

	/**
	 * Set the label for this link type.
	 * @param sLabel the label for this link type.
	 */
	public void setLabel(String sLabel) {
		this.sLabel = sLabel;
	}

	/**
	 * Returns the arrow type for this link type
	 *
	 * @return the arrow type for this link type
	 */
	public int getArrowType() {
		return nArrowType;
	}

	/**
	 * Sets the arrow type for this link type.
	 *
	 * @param nArrowType the arrow style for this link type
	 */
	public void setArrowType(int nArrowType) {			
		this.nArrowType = nArrowType;
	}

	/**
	 * Returns the link style for this link type
	 *
	 * @return the link style for this link type.
	 */
	public int getLinkStyle() {
		return nLinkStyle;
	}

	/**
	 * Sets the link style for this link type.
	 *
	 * @param nLinkStyle link style for this link type
	 */
	public void setLinkStyle(int nLinkStyle) {			
		this.nLinkStyle = nLinkStyle;
	}
	
	/**
	 * Returns the link dashed for this link type
	 *
	 * @return the link dashed for this link type.
	 */
	public int getLinkDashed() {
		return nLinkDashed;
	}

	/**
	 * Sets the link dashed for this link type.
	 *
	 * @param nLinkDashed link dashed for this link type
	 */
	public void setLinkDashed(int nLinkDashed) {			
		this.nLinkDashed = nLinkDashed;
	}
	
	/**
	 * Returns the link weight for this link type
	 *
	 * @return the link weight for this link type.
	 */
	public int getLinkWeight() {
		return nLinkWeight;
	}

	/**
	 * Sets the link weight for this link type.
	 *
	 * @param nLinkWeight link weight for this link type
	 */
	public void setLinkWeight(int nLinkWeight) {			
		this.nLinkWeight = nLinkWeight;
	}

	/**
	 * Returns the label wrap width of this link type
	 *
	 * @return the int of the label wrap width of this link type
	 */
	public int getLabelWrapWidth() {
		//if (nLabelWrapWidth == -1) {
			return Model.LABEL_WRAP_WIDTH_DEFAULT;
		//} else {
		//	return nLabelWrapWidth;
		//}
	}

	/**
	 * Sets the label wrap width of this link type.
	 *
	 * @param nWidth the label wrap width of this link type
	 */
	//public void setLabelWrapWidth(int nWidth) {
	//	this.nLabelWrapWidth = nWidth;
	//}

	/**
	 * Returns the font size for this link type
	 *
	 * @return the int of the font size for this link type
	 */
	public int getFontSize() {
		//if (nFontSize == -1) {
			return Model.FONTSIZE_DEFAULT;
		//} else {
		//	return nFontSize;
		//}
	}

	/**
	 * Sets the font size for this link type.
	 *
	 * @param nWidth the font size for this link type
	 */
	//public void setFontSize(int nFontSize) {			
	//	this.nFontSize = nFontSize;
	//}
	
	/**
	 * Returns the the font face for this link type
	 *
	 * @return String the the font face for this link type
	 */
	public String getFontFace() {
		//if (sFontFace.equals("")) {
			return Model.FONTFACE_DEFAULT;
		//} else {
		//	return sFontFace;
		//}
	}

	/**
	 * Sets the the font face for this link type.
	 *
	 * @param sFontFace the font face for this link type
	 */
	//public void setFontFace(String sFontFace) {
	//	this.sFontFace = sFontFace;
	//}	
	
	/**
	 * Returns the font style for this link type
	 *
	 * @return int font style for this link type
	 */
	public int getFontStyle() {
		//if (nFontStyle == -1) {
			return Model.FONTSTYLE_DEFAULT;
		//} else {
		//	return nFontStyle;
		//}
	}

	/**
	 * Sets the font style for this link type.
	 *
	 * @param nStyle the font style for this link type
	 */
	//public void setFontStyle(int nStyle) {
	//	this.nFontStyle = nStyle;
	//}	
	
	/**
	 * Returns the text foreground for this link type
	 *
	 * @return int text foreground for this link type
	 */
	public int getLinkLabelForeground() {
		//if (nForeground == 0) {
			return (Model.FOREGROUND_DEFAULT).getRGB();
		//} else {
		//	return this.nForeground;
		//}
	}

	/**
	 * Sets the text foreground for this link type.
	 *
	 * @param nForeground the text foreground for this link type
	 */
	//public void setLinkLabelForeground(int nFore) {
	//	this.nForeground = nFore;
	//}	
	
	/**
	 * Returns the text background for this link type
	 *
	 * @return int text background for this link type
	 */
	public int getLinkLabelBackground() {
		//if (nBackground == 0) {
			return (Model.BACKGROUND_DEFAULT).getRGB();
		//} else {
		//	return this.nBackground;
		//}
	}

	/**
	 * Sets the text background for this link type.
	 *
	 * @param nBackground the text background for this link type
	 */
	//public void setLinkLabelBackground(int nBackground) {	
	//	this.nBackground = nBackground;
	//}			
	
	/**
	 * Returns a xml string containing the link type.
	 *
	 * @return a String object containing formatted xml representation of the link type data.
	 */
	public String getXML() {

		StringBuffer data = new StringBuffer(200);

		data.append("\t\t<linktype id=\""+sID+"\" "); //$NON-NLS-1$ //$NON-NLS-2$
		data.append("name=\""+CoreUtilities.cleanSQLText(sName, FormatProperties.nDatabaseType)+"\" "); //$NON-NLS-1$ //$NON-NLS-2$
		data.append("colour=\""+oColour.getRGB()+"\" "); //$NON-NLS-1$ //$NON-NLS-2$
		data.append("label=\""+CoreUtilities.cleanSQLText(sLabel, FormatProperties.nDatabaseType)+"\" "); //$NON-NLS-1$ //$NON-NLS-2$
		data.append("arrowtype=\""+ String.valueOf(nArrowType) +"\" ");	 //$NON-NLS-1$ //$NON-NLS-2$
		data.append("linkstyle=\""+ String.valueOf(nLinkStyle) +"\" ");	 //$NON-NLS-1$ //$NON-NLS-2$
		data.append("linkdashed=\""+ String.valueOf(nLinkDashed) +"\" ");	 //$NON-NLS-1$ //$NON-NLS-2$
		data.append("linkweight=\""+ String.valueOf(nLinkWeight) +"\" />\n");	 //$NON-NLS-1$ //$NON-NLS-2$
		
		// not until stencils do this too!
		//data.append("labelWrapWidth=\""+ String.valueOf(nLabelWrapWidth) +"\" ");	 //$NON-NLS-1$ //$NON-NLS-2$
		//data.append("fontsize=\""+ String.valueOf(nFontSize) +"\" "); //$NON-NLS-1$ //$NON-NLS-2$
		//data.append("fontface=\""+ sFontFace +"\" "); //$NON-NLS-1$ //$NON-NLS-2$
		//data.append("fontstyle=\""+ String.valueOf(nFontStyle) +"\" "); //$NON-NLS-1$ //$NON-NLS-2$
		//data.append("foreground=\""+ String.valueOf(nForeground) +"\" "); //$NON-NLS-1$ //$NON-NLS-2$
		//data.append("background=\""+ String.valueOf(nBackground) +"\">\n"); //$NON-NLS-1$ //$NON-NLS-2$

		return data.toString();
	}

	/**
	 * Make a duplicate of this object but with a new id.
	 */
	public UILinkType duplicate() {
		String id = ProjectCompendium.APP.getModel().getUniqueID();
		UILinkType oLinkType = new UILinkType(sName, oColour, id, sLabel);
		return oLinkType;
	}
}
