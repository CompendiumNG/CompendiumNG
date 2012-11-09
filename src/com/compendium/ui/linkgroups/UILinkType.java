/********************************************************************************
 *                                                                              *
 *  (c) Copyright 2009 Verizon Communications USA and The Open University UK    *
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
import com.compendium.ui.FormatProperties;

/**
 * This class holds the information for a link type.
 *
 * @author	Michelle Bachler
 */
public class UILinkType extends Component { // ONLY EXTENDS COMPONENT FOR SORTING

	/** The name of this link type.*/
	private String 				sName 			= "";

	/** The colour to draw this link type.*/
	private Color 				oColour 		= Color.black;

	/** The unique if of this link type.*/
	private String 				sID 			= "";

	/** Indicates whether to assign the name as the label when this link type is chosen.*/
	private String 				sLabel 			= "";


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
	 * Returns a xml string containing the link type.
	 *
	 * @return a String object containing formatted xml representation of the link type data.
	 */
	public String getXML() {

		StringBuffer data = new StringBuffer(200);

		data.append("\t\t<linktype id=\""+sID+"\" ");
		data.append("name=\""+CoreUtilities.cleanSQLText(sName, FormatProperties.nDatabaseType)+"\" ");
		data.append("colour=\""+oColour.getRGB()+"\" ");
		data.append("label=\""+CoreUtilities.cleanSQLText(sLabel, FormatProperties.nDatabaseType)+"\"/>\n");

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
