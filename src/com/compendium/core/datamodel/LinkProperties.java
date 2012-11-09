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

import java.awt.Color;
import java.awt.Point;
import java.util.Date;
import java.util.Vector;
import java.sql.SQLException;

import com.compendium.core.ICoreConstants;
import com.compendium.core.datamodel.services.*;


/**
 * The ILinkProperties object holds the view, link and link formatting options for the link
 * in a view. 
 * Sometimes used to hold just default formatting options for new links about to be created. 
 * Hence empty constructor.
 *
 * @author	Michelle Bachler
 */
public class LinkProperties extends PCObject implements ILinkProperties, java.io.Serializable {

	/** Arrow Type property name for use with property change events */
	public final static String ARROWTYPE_PROPERTY = "arrowtype";

	/** Link Style property name for use with property change events */
	public final static String LINKSTYLE_PROPERTY = "linkstyle";

	/** Link Dashed property name for use with property change events */
	public final static String LINKDASHED_PROPERTY = "linkdashed";

	/** Link Style property name for use with property change events */
	public final static String LINKWEIGHT_PROPERTY = "linkweight";

	/** Link Style property name for use with property change events */
	public final static String LINKCOLOUR_PROPERTY = "linkcolour";

	/** Font Face property name for use with property change events */
	public final static String FONTFACE_PROPERTY = "fontface";

	/** Font Style property name for use with property change events */
	public final static String FONTSTYLE_PROPERTY = "fontstyle";

	/** Font Size property name for use with property change events */
	public final static String FONTSIZE_PROPERTY = "fontsize";

	/** Text Foreground property name for use with property change events */
	public final static String TEXT_FOREGROUND_PROPERTY = "textforeground";

	/** Text Background property name for use with property change events */
	public final static String TEXT_BACKGROUND_PROPERTY = "textbackground";

	/** Label wrap width property name for use with property change events */
	public final static String WRAP_WIDTH_PROPERTY = "labelwrapwidth";

	/** the view the node associated with this object is in.*/
	protected View			oView 		= null;

	/** The node associated with this object*/
	protected Link	oLink 	= null;

	/** The date this object was created.*/
	protected Date			oCreationDate			= null;

	/** The date this object was last modified.*/
	protected Date			oModificationDate		= null;	

	/** Indicates the arrow type used on this link.*/
	protected int				nArrowType						= ICoreConstants.ARROW_TO;

	/** Indicates the link style used on this link.*/
	protected int				nLinkStyle						= ICoreConstants.STRAIGHT_LINK;

	/** Indicates the label font style for nodes.*/
	protected int				nLinkDashed						= ICoreConstants.PLAIN_LINE;

	/** Indicates the label font style for nodes.*/
	protected int				nLinkWeight						= 1;

	/** Indicates the label font style for nodes.*/
	protected int				nLinkColour						= 0;

	
	/** Indicates the label wrap width for this map.*/
	protected int				nLabelWrapWidth					=-1;  // picks up default from Model
		
	/** Indicates the label font size for this map.*/
	protected int				nFontSize						=-1; // picks up default from Model
	
	/** Indicates the label font face for nodes.*/
	protected String			sFontFace						=""; // picks up default from Model
	
	/** Indicates the label font style for nodes.*/
	protected int				nFontStyle						=-1; // picks up default from Model

	/** Indicates the label font style for nodes.*/
	protected int				nForeground						=Color.black.getRGB();
	
	/** Indicates the label font style for nodes.*/
	protected int				nBackground						=Color.white.getRGB(); 


	/**
	 * Constructor, creates a new link properties object
	 */
	public LinkProperties() {}

	/**
	 * Constructor, creates a new link properties object
	 *
	 * @param View oView The view in which the node is placed
	 * @param Link oLink The link for which these properties are defined
	 * @param Date dCreated, the date this object was created.
	 * @param Date dModified, the date this object was last modified.	 
	 */
	public LinkProperties(View oView, Link oLink, Date dCreated, Date dModified) {
		this.oView = oView;
		this.oLink = oLink;
		this.oCreationDate = dCreated;
		this.oModificationDate = dModified;
	}

	/**
	 * Constructor, creates a new link properties object with the passed properties
	 *
	 * @param oView The view in which the node is placed
	 * @param oNode The node for which the position is defined
	 * @param x The X coordinate of the node's position
	 * @param y The Y coordinate of the node's position
	 * @param dCreated, the date this object was created.
	 * @param dModified, the date this object was last modified.
	 * @param nLabelWrapWidth The wrap width for the link label
	 * @param nArrowType The arrow head type to use
	 * @param nLinkStyle The style of the link, straight, square, curved
	 * @param LinkDashed The style of the line fill, plain, dashed etc.
	 * @param LinkWeight The thickness of the line
	 * @param LinkColour The colour of the line
	 * @param nFontSize The font size for the link label
	 * @param sFontFace The font face for the link label
	 * @param nFontStyle The font style for the link label
	 * @param nForeground The foreground colour for the link label
	 * @param nBackground The background colour for the link label	 
	 */
	public LinkProperties(View oView, Link oLink, Date dCreated, Date dModified,
			int nLabelWrapWidth, int nArrowType, int nLinkStyle, int nLinkDashed, int nLinkWeight, int nLinkColour,
			int nFontSize, String sFontFace, int nFontStyle, int nForeground, int nBackground) {
		this.oView = oView;
		this.oLink = oLink;
		this.oCreationDate = dCreated;
		this.oModificationDate = dModified;
		this.nLabelWrapWidth = nLabelWrapWidth;
		this.nArrowType = nArrowType;
		this.nLinkStyle = nLinkStyle;
		this.nLinkDashed = nLinkDashed;
		this.nLinkWeight = nLinkWeight;
		this.nLinkColour = nLinkColour;		
		this.nFontSize = nFontSize;
		this.sFontFace = sFontFace;
		this.nFontStyle = nFontStyle;
		this.nForeground = nForeground;
		this.nBackground = nBackground;
	}
	
	/**
	 * The initialize method is called by the Model before adding the object to the cache.
	 *
	 * @param PCSession session, the session associated with this object.
	 * @param IMode model, the model this object belongs to.
	 */
	public void initialize(PCSession session, IModel model) {
		super.initialize(session, model);

		// Load and initialize the related MediaIndex objects.
		if (sFontFace.equals("")) {
			sFontFace = ((Model)model).fontface;
		}
		if (nFontStyle == -1) {
			nFontStyle = ((Model)model).fontstyle;
		}
		if (nFontSize == -1) {
			nFontSize = ((Model)model).fontsize;
		}
		if (nLabelWrapWidth == -1) {
			nLabelWrapWidth = ((Model)model).labelWrapWidth;
		}
	}

	/**
	 *	This method needs to be called on this object before the Model removes it from the cache.
	 */
	public void cleanUp() {
		super.cleanUp() ;
	}

	/**
	 * Return a new NodePosition object with the properties of this node.
	 */
	public LinkProperties getClone() {
		return new LinkProperties(oView, oLink,oCreationDate, oModificationDate, 
				nLabelWrapWidth, nArrowType, nLinkStyle, nLinkDashed, nLinkWeight, nLinkColour, 
				nFontSize, sFontFace, nFontStyle, nForeground, nBackground);
	}

	/**
	 * Returns the link for which this object defines the properties
	 *
	 * @return the link for which this object defines the properties.
	 */
	public Link getLink() {
		return oLink;
	}

	/**
	 * Sets the link for which this object defines the properties, in the local data ONLY.
	 *
	 * @param the link for which this object defines the properties.
	 */
	public void setLink(Link oLink) {
		this.oLink = oLink;
	}

	/**
	 * Returns the view in which the node is placed at the defined position
	 *
	 * @return View, the view in which the node is placed at the defined position
	 */
	public View getView() {
		return oView;
	}

	/**
	 * Set the view in which the node is placed at the defined position.
	 * @param View oView, the view in which this node is placed.
	 */
	public void setView(View oView) {
		this.oView = oView;
	}

	/**
	 *	Sets the date when this node was created, in the local data ONLY.
	 *
	 *	@param Date date, the creation date of this object.
	 */
	public void setCreationDate(Date date) {
		oCreationDate = date;
	}

	/**
	 *	Returns the creation date of this object.
	 *
	 *	@return Date, the date when this object was created.
	 */
	public Date getCreationDate() {
		return oCreationDate;
	}

	/**
	 * Sets the ModificationDate date of this object, in the local data ONLY.
	 *
	 * @param Date date, the date this object was last modified.
	 */
	public void setModificationDate(Date date) {
		oModificationDate = date;
	}

	/**
	 *	Returns the modification date of this node.
	 *
	 *	@return Date, the date when this node was last modified.
	 */
	public Date getModificationDate() {
		return oModificationDate;
	}

	/**
	 * Returns the label wrap width of this View
	 *
	 * @return the int of the label wrap width of this View
	 */
	public int getLabelWrapWidth() {
		if (nLabelWrapWidth == -1) {
			return Model.LABEL_WRAP_WIDTH_DEFAULT;
		} else {
			return nLabelWrapWidth;
		}
	}

	/**
	 * Sets the label wrap width of this View, in the local data ONLY.
	 *
	 * @param nWidth the label wrap width of this View
	 */
	public void setLabelWrapWidth(int nWidth) {
		int oldWrapWidth = this.nLabelWrapWidth;
		this.nLabelWrapWidth = nWidth;
		firePropertyChange(WRAP_WIDTH_PROPERTY, oldWrapWidth, nWidth);												
	}

	/**
	 * Returns the arrow type for this View
	 *
	 * @return the arrow type for this link in this view
	 */
	public int getArrowType() {
		return nArrowType;
	}

	/**
	 * Sets the arrow type for this link in this View, in the local data ONLY.
	 *
	 * @param nArrowType the arrow style for this link in this view
	 */
	public void setArrowType(int nArrowType) {			
		int oldArrowType = this.nArrowType;
		this.nArrowType = nArrowType;
		firePropertyChange(ARROWTYPE_PROPERTY, oldArrowType, nArrowType);										
	}

	/**
	 * Returns the link style for this link in this View
	 *
	 * @return the link style for this link in this View.
	 */
	public int getLinkStyle() {
		return nLinkStyle;
	}

	/**
	 * Sets the link style for this link in this View, in the local data ONLY.
	 *
	 * @param nLinkStyle link style for this link in this View
	 */
	public void setLinkStyle(int nLinkStyle) {			
		int oldLinkStyle = this.nLinkStyle;
		this.nLinkStyle = nLinkStyle;
		firePropertyChange(LINKSTYLE_PROPERTY, oldLinkStyle, nLinkStyle);										
	}
	
	/**
	 * Returns the link dashed for this link in this View
	 *
	 * @return the link dashed for this link in this View.
	 */
	public int getLinkDashed() {
		return nLinkDashed;
	}

	/**
	 * Sets the link dashed for this link in this View, in the local data ONLY.
	 *
	 * @param nLinkDashed link dashed for this link in this View
	 */
	public void setLinkDashed(int nLinkDashed) {			
		int oldLinkDashed = this.nLinkDashed;
		this.nLinkDashed = nLinkDashed;
		firePropertyChange(LINKDASHED_PROPERTY, oldLinkDashed, nLinkDashed);										
	}
	
	/**
	 * Returns the link weight for this link in this View
	 *
	 * @return the link weight for this link in this View.
	 */
	public int getLinkWeight() {
		return nLinkWeight;
	}

	/**
	 * Sets the link weight for this link in this View, in the local data ONLY.
	 *
	 * @param nLinkWeight link weight for this link in this View
	 */
	public void setLinkWeight(int nLinkWeight) {			
		int oldLinkWeight = this.nLinkWeight;
		this.nLinkWeight = nLinkWeight;
		firePropertyChange(LINKWEIGHT_PROPERTY, oldLinkWeight, nLinkWeight);										
	}
	
	/**
	 * Returns the link colour for this link in this View
	 *
	 * @return the link colour for this link in this View.
	 */
	public int getLinkColour() {
		return nLinkColour;
	}

	/**
	 * Sets the link colour for this link in this View, in the local data ONLY.
	 *
	 * @param nLinkWeight link colour for this link in this View
	 */
	public void setLinkColour(int nLinkColour) {			
		int oldLinkColour = this.nLinkColour;
		this.nLinkColour = nLinkColour;
		firePropertyChange(LINKCOLOUR_PROPERTY, oldLinkColour, nLinkColour);										
	}

	/**
	 * Returns the font size for this link
	 *
	 * @return the int of the font size for this link
	 */
	public int getFontSize() {
		if (nFontSize == -1) {
			return Model.FONTSIZE_DEFAULT;
		} else {
			return nFontSize;
		}
	}

	/**
	 * Sets the font size for this link, in the local data ONLY.
	 *
	 * @param nWidth the font size for this link
	 */
	public void setFontSize(int nFontSize) {			
		int oldFontSize = this.nFontSize;
		this.nFontSize = nFontSize;
		firePropertyChange(FONTSIZE_PROPERTY, oldFontSize, nFontSize);										
	}
	
	/**
	 * Returns the the font face for this link in this View
	 *
	 * @return String the the font face for this link in this View
	 */
	public String getFontFace() {
		if (sFontFace.equals("")) {
			return Model.FONTFACE_DEFAULT;
		} else {
			return sFontFace;
		}
	}

	/**
	 * Sets the the font face for this link in this View, in the local data ONLY.
	 *
	 * @param sFontFace the font face for this link in this View
	 */
	public void setFontFace(String sFontFace) {
		String oldFontFace = this.sFontFace;
		this.sFontFace = sFontFace;
		firePropertyChange(FONTFACE_PROPERTY, oldFontFace, sFontFace);								
	}	
	
	/**
	 * Returns the font style for this View
	 *
	 * @return int font style for this View
	 */
	public int getFontStyle() {
		if (nFontStyle == -1) {
			return Model.FONTSTYLE_DEFAULT;
		} else {
			return nFontStyle;
		}
	}

	/**
	 * Sets the font style for this link, in the local data ONLY.
	 *
	 * @param nStyle the font style for this link
	 */
	public void setFontStyle(int nStyle) {
		int oldStyle = this.nFontStyle;
		this.nFontStyle = nStyle;
		firePropertyChange(FONTSTYLE_PROPERTY, oldStyle, nStyle);						
	}	
	
	/**
	 * Returns the text foreground for this link in this View
	 *
	 * @return int text foreground for this link in this view
	 */
	public int getForeground() {
		return this.nForeground;
	}

	/**
	 * Sets the text foreground for this link in this View, in the local data ONLY.
	 *
	 * @param nForeground the text foreground for this link in this View
	 */
	public void setForeground(int nFore) {
		int oldForeground = this.nForeground;
		this.nForeground = nFore;
		firePropertyChange(TEXT_FOREGROUND_PROPERTY, oldForeground, nFore);				
	}	
	
	/**
	 * Returns the text background for this link in this View
	 *
	 * @return int text background for this link in this view
	 */
	public int getBackground() {
		return this.nBackground;
	}

	/**
	 * Sets the text background for this link in this View, in the local data ONLY.
	 *
	 * @param nBackground the text background for this link in this View
	 */
	public void setBackground(int nBackground) {	
		int oldBackground = this.nBackground;
		this.nBackground = nBackground;
		firePropertyChange(TEXT_BACKGROUND_PROPERTY, oldBackground, nBackground);		
	}			
}
