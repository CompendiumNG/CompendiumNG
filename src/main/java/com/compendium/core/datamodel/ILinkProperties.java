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
 * The ILinkProperties object holds the link and link label formatting options for the link
 * in a view.
 *
 * @author	Michelle Bachler
 */
public interface ILinkProperties extends IPCObject {
	
	/**
	 * The initialize method is called by the Model before adding the object to the cache.
	 *
	 * @param PCSession session, the session associated with this object.
	 * @param IMode model, the model this object belongs to.
	 */
	public void initialize(PCSession session, IModel model);

	/**
	 *	This method needs to be called on this object before the Model removes it from the cache.
	 */
	public void cleanUp();

	/**
	 * Return a new NodePosition object with the properties of this node.
	 */
	public ILinkProperties getClone();

	/**
	 * Returns the link for which this object defines the properties
	 *
	 * @return the link for which this object defines the properties.
	 */
	public Link getLink();

	/**
	 * Sets the link for which this object defines the properties, in the local data ONLY.
	 *
	 * @param the link for which this object defines the properties.
	 */
	public void setLink(Link oLink);

	/**
	 * Returns the view in which the node is placed at the defined position
	 *
	 * @return View, the view in which the node is placed at the defined position
	 */
	public View getView();

	/**
	 * Set the view in which the node is placed at the defined position.
	 * @param View oView, the view in which this node is placed.
	 */
	public void setView(View oView);

	/**
	 *	Sets the date when this node was created, in the local data ONLY.
	 *
	 *	@param Date date, the creation date of this object.
	 */
	public void setCreationDate(Date date);

	/**
	 *	Returns the creation date of this object.
	 *
	 *	@return Date, the date when this object was created.
	 */
	public Date getCreationDate();

	/**
	 * Sets the ModificationDate date of this object, in the local data ONLY.
	 *
	 * @param Date date, the date this object was last modified.
	 */
	public void setModificationDate(Date date);

	/**
	 *	Returns the modification date of this node.
	 *
	 *	@return Date, the date when this node was last modified.
	 */
	public Date getModificationDate();

	/**
	 * Returns the label wrap width of this View
	 *
	 * @return the int of the label wrap width of this View
	 */
	public int getLabelWrapWidth();

	/**
	 * Sets the label wrap width of this View, in the local data ONLY.
	 *
	 * @param nWidth the label wrap width of this View
	 */
	public void setLabelWrapWidth(int nWidth);

	/**
	 * Returns the arrow type for this View
	 *
	 * @return the arrow type for this link in this view
	 */
	public int getArrowType();

	/**
	 * Sets the arrow type for this link in this View, in the local data ONLY.
	 *
	 * @param nArrowType the arrow style for this link in this view
	 */
	public void setArrowType(int nArrowType);

	/**
	 * Returns the link style for this link in this View
	 *
	 * @return the link style for this link in this View.
	 */
	public int getLinkStyle();

	/**
	 * Sets the link style for this link in this View, in the local data ONLY.
	 *
	 * @param nLinkStyle link style for this link in this View
	 */
	public void setLinkStyle(int nLinkStyle);
	
	/**
	 * Returns the link dashed for this link in this View
	 *
	 * @return the link dashed for this link in this View.
	 */
	public int getLinkDashed();

	/**
	 * Sets the link dashed for this link in this View, in the local data ONLY.
	 *
	 * @param nLinkDashed link dashed for this link in this View
	 */
	public void setLinkDashed(int nLinkDashed);
	
	/**
	 * Returns the link weight for this link in this View
	 *
	 * @return the link weight for this link in this View.
	 */
	public int getLinkWeight();

	/**
	 * Sets the link weight for this link in this View, in the local data ONLY.
	 *
	 * @param nLinkWeight link weight for this link in this View
	 */
	public void setLinkWeight(int nLinkWeight);
	
	/**
	 * Returns the link colour for this link in this View
	 *
	 * @return the link colour for this link in this View.
	 */
	public int getLinkColour();

	/**
	 * Sets the link colour for this link in this View, in the local data ONLY.
	 *
	 * @param nLinkWeight link colour for this link in this View
	 */
	public void setLinkColour(int nLinkColour);

	/**
	 * Returns the font size for this View
	 *
	 * @return the int of the font size for this View
	 */
	public int getFontSize();

	/**
	 * Sets the font size for this View, in the local data ONLY.
	 *
	 * @param nWidth the font size for this View
	 */
	public void setFontSize(int nFontSize);
	
	/**
	 * Returns the the font face for node labels in this View
	 *
	 * @return String the the font face for node labels in this View
	 */
	public String getFontFace();

	/**
	 * Sets the the font face for node labels in this View, in the local data ONLY.
	 *
	 * @param sFontFace the font face for node labels in this View
	 */
	public void setFontFace(String sFontFace);
	
	/**
	 * Returns the font style for this View
	 *
	 * @return int font style for this View
	 */
	public int getFontStyle();

	/**
	 * Sets the font style for this View, in the local data ONLY.
	 *
	 * @param nStyle the font style for this View
	 */
	public void setFontStyle(int nStyle);
	
	/**
	 * Returns the text foreground for this Node in this View
	 *
	 * @return int text foreground for this node in this view
	 */
	public int getForeground();
	
	/**
	 * Sets the text foreground for this Node in this View, in the local data ONLY.
	 *
	 * @param nForeground the text foreground for this Node in this View
	 */
	public void setForeground(int nFore);
	
	/**
	 * Returns the text background for this Node in this View
	 *
	 * @return int text background for this node in this view
	 */
	public int getBackground();
	
	/**
	 * Sets the text background for this Node in this View, in the local data ONLY.
	 *
	 * @param nBackground the text background for this Node in this View
	 */
	public void setBackground(int nBackground);
}
