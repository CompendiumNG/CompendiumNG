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
import java.beans.*;
import java.sql.SQLException;

import com.compendium.core.ICoreConstants;
import com.compendium.core.datamodel.services.*;

/**
 * The ViewLayer object holds additional information about the visual state of a JInternalFrame with a <code>View</code> inside.
 * Note: This is a holding container only and does not currently write to the database
 *
 * @author	Michelle Bachler
 */
public class ViewLayer extends PCObject implements java.io.Serializable {

	/** The user id of the user who created this ViewLayer record.*/
	//protected String			sUserID							= "";

	/** The View id for this ViewLayer record.*/
	protected String			sViewID							= "";

	/** The scribble data.*/
	protected String			sScribble						= "";

	/** The background colour for this view.*/
	protected int				nBackgroundColor 				= Color.white.getRGB();

	/** The background image for this view.*/
	protected String			sBackgroundImage 				= "";

	/** The background movie for this view.*/
	protected String			sBackgroundMovie				= "";
	
	
	/** The grid layout details.*/
	protected String			sGrid							= "";

	/** The shapes on the view details.*/
	protected String			sShapes							= "";

	/**
	 * Constructor, creates a new ViewLayout
	 */
	public ViewLayer() {}

	/**
	 * The initialize method is called by the Model before adding the object to the cache.
	 *
	 * @param PCSession session, the session associated with this object.
	 * @param IMode model, the model this object belongs to.
	 */
	public void initialize(PCSession session, IModel model) {
		super.initialize(session, model);
	}

	/**
	 *	This method needs to be called on this object before the Model removes it from the cache.
	 */
	public void cleanUp() {
		super.cleanUp() ;
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
	 * @param String sViewID, the View id associated with this View
	 */
	public void setViewID(String sViewID) {
		this.sViewID = sViewID;
	}

	/**
	 * Sets the scribble pad data associated with this View.
	 *
	 * @param String sScribble, the scribble pad data associated with this View.
	 */
	public void setScribble(String sScribble) {
		this.sScribble = sScribble;
	}

	/**
	 * Returns the scribble pad data associated with this View.
	 *
	 * @return String, the scribble pad data associated with this View.
	 */
	public String getScribble() {
		return sScribble;
	}

	/**
	 * Sets the background image associated with this View.
	 *
	 * @param sBackgroundImage the background image associated with this View.
	 */
	public void setBackgroundImage(String sBackgroundImage) {
		this.sBackgroundImage = sBackgroundImage;
	}

	/**
	 * Returns the background image associated with this View.
	 *
	 * @return String the background image associated with this View.
	 */
	public String getBackgroundImage() {
		return sBackgroundImage;
	}


	/**
	 * Sets the background color associated with this View.
	 *
	 * @param sBackgroundColor the background color associated with this View.
	 */
	public void setBackgroundColor(int nBackgroundColor) {
		this.nBackgroundColor = nBackgroundColor;
	}

	/**
	 * Returns the background color associated with this View.
	 *
	 * @return String the background color associated with this View.
	 */
	public int getBackgroundColor() {
		if (nBackgroundColor == 0) {
			nBackgroundColor = Color.white.getRGB();
		}
		return nBackgroundColor;
	}

	/**
	 * Returns the grid data associated with this View.
	 *
	 * @return String, the grid data associated with this View.
	 */
	public String getGrid() {
		return sGrid;
	}

	/**
	 * Sets the grid data associated with this View.
	 *
	 * @param String sGrid, the grid data associated with this View.
	 */
	public void setGrid(String sGrid) {
		this.sGrid = sGrid;
	}

	/**
	 * Returns the shapes data for shapes on this view.
	 *
	 * @return String, the shapes data for shapes on this view.
	 */
	public String getShapes() {
		return sShapes;
	}

	/**
	 * Sets the shapes data for shapes on this view.
	 *
	 * @param String sShapes, the shapes data for shapes on this view.
	 */
	public void setShapes(String sShapes) {
		this.sShapes = sShapes;
	}

	/**
	 * Update this record in the database.
	 *
	 * @exception java.sql.SQLException
	 * @exception ModelSessionException
	 */
	public void update() throws SQLException, ModelSessionException {
		if (oModel == null)
			throw new ModelSessionException("Model is null in View.initializeMembers");
		if (oSession == null) {
			oSession = oModel.getSession();
			if (oSession == null)
				throw new ModelSessionException("Session is null in Viwe.initializeMembers");
		}

		oModel.getViewLayerService().updateViewLayer(oModel.getSession(), this);
	}
}
