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

package com.compendium.ui.popups;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;

import javax.swing.JTable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.compendium.LanguageProperties;
import com.compendium.ProjectCompendium;
import com.compendium.core.ICoreConstants;
import com.compendium.core.datamodel.NodePosition;
import com.compendium.core.datamodel.NodeSummary;
import com.compendium.ui.UIList;
import com.compendium.ui.UIUtilities;
import com.compendium.ui.dialogs.UIExportDialog;
import com.compendium.ui.dialogs.UIExportViewDialog;
import com.compendium.ui.dialogs.UIImportDialog;
import com.compendium.ui.edits.DeleteEdit;
import com.compendium.ui.plaf.ListUI;

/**
 * This class has generic methods for the right-click menu options for 
 * list and list node right-click menus
 *
 * @author	Michelle Bachler
 */
public abstract class UIBaseListPopupMenu extends UIBasePopupMenu implements ActionListener{
	
	static final Logger log = LoggerFactory.getLogger(UIBaseListPopupMenu.class);

	/** The associated ListUI object for this popup menu.*/
    ListUI 		oListUI;

	/** The associated UIList object for this popup menu.*/
    UIList 		oUIList;

	/**
	 * Constructor. 
	 * @param title the title for this popup menu.
	 */
    UIBaseListPopupMenu(String title) {
		super(title);
	}

	/**
	 * Constructor. Create the menus and items and draws the popup menu.
	 * @param title the title for this popup menu.
	 * @param listUI com.compendium.ui.plaf.ListUI, the associated list for this popup menu.
	 */
    private UIBaseListPopupMenu(String title, ListUI listUI) {
		super(title);
		setList(listUI);
		init();
	}

	void setList(ListUI listUI) {
		this.oListUI = listUI;
		this.oUIList = oListUI.getUIList();
	}
	
	/**
	 * Handles the event of an option being selected.
	 * @param evt the event associated with the option being selected.
	 */
	public void actionPerformed(ActionEvent evt) {

		//ProjectCompendium.APP.setWaitCursor();
		//Object source = evt.getSource();
		//nY = (listUI.getUIList().getNumberOfNodes() + 1) * 10;

		super.actionPerformed(evt);

		//ProjectCompendium.APP.setDefaultCursor();
	}

	/**
	 * Process a node creation request.
	 * @param nType the type of the new node to create.
	 */
	protected void createNode(int nType) {
		oListUI.createNode(nType,
						 "", //$NON-NLS-1$
						 ProjectCompendium.APP.getModel().getUserProfile().getUserName(),
						 "", //$NON-NLS-1$
						 "", //$NON-NLS-1$
						 nX,
						 nY
						 );

		oUIList.updateTable();
		oUIList.selectNode(oUIList.getNumberOfNodes() - 1, ICoreConstants.MULTISELECT);
	}

	/**
	 * Mark the currently selected Nodes as seen.
	 */
	protected void markSeen() {
		try {
			Enumeration e = oUIList.getSelectedNodes();
			for(;e.hasMoreElements();){
				NodePosition np = (NodePosition) e.nextElement();
				NodeSummary oNode = np.getNode();
				oNode.setState(ICoreConstants.READSTATE);	
			}
		}
		catch(Exception io) {
			log.info(LanguageProperties.getString(LanguageProperties.POPUPS_BUNDLE, "UIBasePopupMenu.unableMarkRead")); //$NON-NLS-1$
		}
	}

	/**
	 * Mark the currently selected nodes as unseen.
	 */
	protected void markUnseen() {
		try {
			Enumeration e = oUIList.getSelectedNodes();
			for(;e.hasMoreElements();){
				NodePosition np = (NodePosition) e.nextElement();
				NodeSummary oNode = np.getNode();
				oNode.setState(ICoreConstants.UNREADSTATE);	
			}
		}
		catch(Exception io) {
			log.info(LanguageProperties.getString(LanguageProperties.POPUPS_BUNDLE, "UIBasePopupMenu.unableMarkUnread")); //$NON-NLS-1$
		}
	}


	/**
	 * Handle a Questmap import request.
	 * @param showViewList, true if importing to mulitpl views, else false.
	 */
	public void onImportFile(boolean showViewList) {
		UIImportDialog uid = new UIImportDialog(ProjectCompendium.APP, showViewList);
		uid.setUIList(oUIList);
		uid.setVisible(true);
	}

	/**
	 * Exports the current view to a HTML outline file.
	 */
	public void onExportFile() {
		UIExportDialog export = new UIExportDialog(ProjectCompendium.APP, oUIList.getViewFrame());
		export.setVisible(true);
	}

	/**
	 * Exports the current view to a HTML Views file.
	 */
	public void onExportView() {
        UIExportViewDialog dialog2 = new UIExportViewDialog(ProjectCompendium.APP, oUIList.getViewFrame());
		UIUtilities.centerComponent(dialog2, ProjectCompendium.APP);
		dialog2.setVisible(true);
	}
	
	/**
	 * Delete the currently selected nodes.
	 */
	protected void delete() {
		DeleteEdit edit = new DeleteEdit(oUIList.getViewFrame());
		oUIList.deleteSelectedNodes(edit);
		oUIList.getViewFrame().getUndoListener().postEdit(edit);
		ProjectCompendium.APP.setTrashBinIcon();
	}	
	
	/**
	 * Clone the currently selected nodes.
	 */
	protected void cloneNodes() {
		for(Enumeration e = oUIList.getSelectedNodes();e.hasMoreElements();) {
			NodePosition oNode = (NodePosition)e.nextElement();
			ProjectCompendium.APP.setStatus(LanguageProperties.getString(LanguageProperties.POPUPS_BUNDLE, "UIBasePopupMenu.cloning") + oNode.getNode().getLabel()); //$NON-NLS-1$
			oListUI.createCloneNode(oNode.getNode());			
		}
		ProjectCompendium.APP.setStatus(""); //$NON-NLS-1$
	}
	
	/**
	 * Create Shortcuts for the selected nodes.
	 */
	protected void shortcutNodes() {
		// create shortcuts for all the selected nodes if user MULTISELECTs
		JTable list = oUIList.getList();
		int[] selectedList = list.getSelectedRows();
		oUIList.createShortCutNodes(selectedList);
		list.clearSelection();		
		list.addRowSelectionInterval(list.getRowCount()-selectedList.length, list.getRowCount()-1);
	}
}
