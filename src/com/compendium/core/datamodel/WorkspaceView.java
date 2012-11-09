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

import java.beans.*;

import com.compendium.core.ICoreConstants;
import com.compendium.core.datamodel.services.*;

/**
 * The WorkspaceView object represents a open ViewFrames in Compendium with scrollbar positions and reference to child <code>View</code>
 * Note: This class is a holding container only and does not currently write to the database
 *
 * @author	Michelle Bachler
 */
public class WorkspaceView implements java.io.Serializable {

	/** The worspace id for this workspace view.*/
	protected String			sWorkspaceID					= "";

	/** The view id for this workspace view.*/
	protected String			sViewID							= "";

	/** The horizontal scrollbar position for this workspace view.*/
	protected int				nHorizontalScrollBarPosition	= 0;

	/** The vertical scrollbar position for this workspace view.*/
	protected int				nVerticalScrollBarPosition		= 0;

	/** The width of the view in this workspace view.*/
	protected int				nWidth							= 300;

	/** The height if the view in this workspace view.*/
	protected int				nHeight							= 300;

	/** The x position of the view in this workspace view.*/
	protected int				nXPos							= 0;

	/** The y position of the view in this workspace view.*/
	protected int				nYPos							= 0;

	/** Indicates if the view was iconfied in this workspace view.*/
	protected boolean			bIsIcon							= false;

	/** Indicates if the view was maximized in this workspace view.*/
	protected boolean			bIsMaximum						= false;


	/**
	 * Constructor, creates a new workspace view
	 */
	public WorkspaceView() {}

	/**
	 * Returns the WorkspaceID of the parent Workspace associated with this WorkspaceView
	 *
	 * @return the WorkspaceID of the parent Workspace associated with this WorkspaceView
	 */
	public String getWorkspaceID() {
		return sWorkspaceID;
	}

	/**
	 * Sets the WorkspaceID of the parent Workspace associated with this WorkspaceView, in the local data ONLY.
	 *
	 * @param String sWorkspaceID, the WorkspaceID of the parent Workspace associated with this WorkspaceView
	 */
	public void setWorkspaceID(String sWorkspaceID) {
		this.sWorkspaceID = sWorkspaceID;
	}

	/**
	 * Returns the View id associated with this WorkspaceView
	 *
	 * @return the String of the View id associated with this WorkspaceView
	 */
	public String getViewID() {
		return sViewID;
	}

	/**
	 * Sets the View id object associated with this WorkspaceView, in the local data ONLY.
	 *
	 * @param String sViewID, the View id associated with this WorkspaceView
	 */
	public void setViewID(String sViewID) {
		this.sViewID = sViewID;
	}

	/**
	 * Returns the horizontal ScrollBar position associated with this WorkspaceView
	 *
	 * @return the int of the horizontal ScrollBar position associated with this WorkspaceView
	 */
	public int getHorizontalScrollBarPosition() {
		return nHorizontalScrollBarPosition;
	}

	/**
	 * Sets the horizontal ScrollBar position associated with this WorkspaceView, in the local data ONLY.
	 *
	 * @param String nHPos, the horizontal ScrollBar position associated with this WorkspaceView
	 */
	public void setHorizontalScrollBarPosition(int nHPos) {
		this.nHorizontalScrollBarPosition = nHPos;
	}

	/**
	 * Returns the vertical scrollBar position associated with this WorkspaceView
	 *
	 * @return the int of the vertical scrollBar position associated with this WorkspaceView
	 */
	public int getVerticalScrollBarPosition() {
		return nVerticalScrollBarPosition ;
	}

	/**
	 * Sets the vertical scrollBar position object associated with this WorkspaceView, in the local data ONLY.
	 *
	 * @param int nVPos, the vertical scrollBar position associated with this WorkspaceView
	 */
	public void setVerticalScrollBarPosition(int nVPos) {
		this.nVerticalScrollBarPosition = nVPos;
	}

	/**
	 * Returns the width of this WorkspaceView
	 *
	 * @return the int of the width of this WorkspaceView
	 */
	public int getWidth() {
		return nWidth;
	}

	/**
	 * Sets the width of this WorkspaceView, in the local data ONLY.
	 *
	 * @param int nWidth, the width of this WorkspaceView
	 */
	public void setWidth(int nWidth) {
		if (nWidth != 0)
			this.nWidth = nWidth;
	}

	/**
	 * Returns the height of this WorkspaceView
	 *
	 * @return the int of the height of this WorkspaceView
	 */
	public int getHeight() {
		return nHeight;
	}

	/**
	 * Sets the height of this WorkspaceView, in the local data ONLY.
	 *
	 * @param int nHeight, the height of this WorkspaceView
	 */
	public void setHeight(int nHeight) {
		if (nHeight != 0)
			this.nHeight = nHeight;
	}

	/**
	 * Returns the x position of this WorkspaceView
	 *
	 * @return the int of the x position of this WorkspaceView
	 */
	public int getXPosition() {
		return nXPos;
	}

	/**
	 * Sets the the x position of this WorkspaceView, in the local data ONLY.
	 *
	 * @param int nXPos, the the x position of this WorkspaceView
	 */
	public void setXPosition(int nXPos) {
		this.nXPos = nXPos;
	}

	/**
	 * Returns the y position of this WorkspaceView
	 *
	 * @return the int of the y position of this WorkspaceView
	 */
	public int getYPosition() {
		return nYPos;
	}

	/**
	 * Sets the the y position of this WorkspaceView, in the local data ONLY.
	 *
	 * @param int nYPos, the the y position of this WorkspaceView
	 */
	public void setYPosition(int nYPos) {
		this.nYPos = nYPos;
	}

	/**
	 * Returns whether this WorkspaceView has been iconified
	 *
	 * @return a boolean representing whether this WorkspaceView has been iconified
	 */
	public boolean getIsIcon() {
		return bIsIcon;
	}

	/**
	 * Sets the boolean representing whether this WorkspaceView has been iconified, in the local data ONLY.
	 *
	 * @param boolean bIsIcon, the boolean representing whether this WorkspaceView has been iconified
	 */
	public void setIsIcon(boolean bIsIcon) {
		this.bIsIcon = bIsIcon;
	}

	/**
	 * Returns whether this WorkspaceView has been maximized
	 *
	 * @return a boolean representing whether this WorkspaceView has been maximized
	 */
	public boolean getIsMaximum() {
		return bIsMaximum;
	}

	/**
	 * Sets the boolean representing whether this WorkspaceView has been maximized, in the local data ONLY.
	 *
	 * @param boolean bIsMaximum, the boolean representing whether this WorkspaceView has been maximized
	 */
	public void setIsMaximum(boolean bIsMaximum) {
		this.bIsMaximum = bIsMaximum;
	}
}
