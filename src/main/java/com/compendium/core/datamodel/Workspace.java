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

import java.util.*;
import java.beans.*;
import java.sql.SQLException;

import com.compendium.core.ICoreConstants;
import com.compendium.core.datamodel.services.*;

/**
 * The Workspace object represents a collection of open views, thier positions, size and state (icon/maximized).
 *
 * @author	Michelle Bachler
 */
public class Workspace extends IdObject implements java.io.Serializable{

	/** The name of the worksapce.*/
	protected String		sName				= "";

	/** A collection of the WorkspaceView objects in this workspace.*/
	protected Vector		vtWorkspaceViews	= new Vector(51);

	/**
	 * Constructor, creates a new empty workspace.
	 */
	public Workspace() {
		this("", "", "", null);
	}

	/**
	 * Constructor, creates a new workspace with the given identifying name, and the given child workspace views
	 *
	 * @param String sWorkspaceID, the unique identifier for this workspace.
	 * @param String sName, the name of this workspace.
	 * @param String sAuthor, the author of this workspace.
	 * @param Vector vtWorkspaceViews, a collection of the WorkspaceView objects that make up this workspace.
	 */
	public Workspace(String sWorkspaceID, String sName, String sAuthor, Vector vtWorkspaceViews) {

		super(sWorkspaceID, sAuthor, new Date(), new Date());
		this.sName = sName;

		int count = vtWorkspaceViews.size();
		for (int i=0; i<count; i++) {
			WorkspaceView view = (WorkspaceView)vtWorkspaceViews.elementAt(i);
			if (view != null)
				this.vtWorkspaceViews.addElement(view);
		}
	}

	/**
	 * The initialize method is called by the Model before adding the object to the client.
	 *
	 * @param PCSession session, the session object for this workspace.
	 * @param IModel model, the model for the database used for this object.
	 */
	public void initialize(PCSession session, IModel model) {
		super.initialize(session, model) ;
	}

	/**
	 * Clean up the object.
	 */
	public void cleanUp() {
		super.cleanUp();
		sName = null;
		vtWorkspaceViews.removeAllElements();
		vtWorkspaceViews = null;
	}

	/**
	 * Returns the identifier (unique name) of the Workspace
	 *
	 * @return the name of the Workspace
	 */
	public String getName() {
		return sName ;
	}

	/**
	 * Sets the identifier (unique name) of the Workspace, in the local data ONLY.
	 *
	 * @param name The name of the Workspace
	 */
	public void setName(String name) {

		if (name.equals(sName))
			return ;

		sName = name;
	}

	/**
	 * Returns all the WorkspaceViews in this Workspace.
	 *
	 * @return a vector of the workspaceviews
	 */
	public Vector getWorkspaceViews() {
		return vtWorkspaceViews;
	}

	/**
	 * Save this workspace to the DATABASE.
	 *
	 * @param boolean update, whether to update an existing Workspace or create a new one.
	 * @param String sUserID, the id of the current user.
	 * @exception java.sql.SQLException
	 */
	public void saveWorkspace(boolean update, String sUserID) throws SQLException {

		IWorkspaceService ws = oModel.getWorkspaceService() ;

		if (update) {
			ws.updateWorkspace(oSession, getId(), sUserID, sName, vtWorkspaceViews);
		}
		else {
			ws.createWorkspace(oSession, getId(), sUserID, sName, vtWorkspaceViews);
		}
	}
}
