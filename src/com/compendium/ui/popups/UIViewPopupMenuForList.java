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

import java.awt.event.*;
import java.util.*;
import javax.swing.*;

import com.compendium.core.ICoreConstants;
import com.compendium.core.datamodel.NodePosition;
import com.compendium.core.datamodel.NodeSummary;
import com.compendium.core.datamodel.View;

import com.compendium.ProjectCompendium;
import com.compendium.ui.plaf.*;
import com.compendium.ui.*;

/**
 * This class draws and handles the events for a List's right-click popup menu.
 *
 * @author	Mohammed Sajid Ali / Michelle Bachler / Lakshmi Prabhakaran
 */
public class UIViewPopupMenuForList extends UIBaseListPopupMenu implements ActionListener{

	/** A separator that can be turned off if required by simple menu.*/
	private JSeparator		separator1				= null;

	/**
	 * Constructor. Create the menus and items and draws the popup menu.
	 * @param title the title for this popup menu.
	 * @param listUI com.compendium.ui.plaf.ListUI, the associated list for this popup menu.
	 */
	public UIViewPopupMenuForList(String title, ListUI listUI) {
		super(title);
		setList(listUI);
		init();
	}
	
	protected void init() {
		boolean bSimple = FormatProperties.simpleInterface;
		
		View view = oUIList.getView();
		String sViewID = view.getId();
		String sInBoxID = ProjectCompendium.APP.getInBoxID();
			
		addNodeCreationMenu();
		addSeparator();
		
		addReferences(view.getReferenceNodes());

		addCutCopyPaste(shortcutKey);
		addDelete(shortcutKey);
		
		addSeparator();

		addImportMenu();
		addExportMenu();
		
		addSeparator();
		
		addJabberAndIXPanelMenus();

		addShortcut();
		addClone();
		addSeparator();
				
		addSeenUnseen();
					
		Enumeration e = oUIList.getSelectedNodes();
		for(;e.hasMoreElements();){
			NodePosition np = (NodePosition) e.nextElement();
			NodeSummary oNode = np.getNode();
			int state = oNode.getState();
			if(state == ICoreConstants.READSTATE){				
				showMarkUnseen = true;
			} else if(state == ICoreConstants.UNREADSTATE) {
				showMarkSeen = true;
			} else {
				showMarkUnseen = true;
				showMarkSeen = true;
			}
		}
			
		separator1 = new JPopupMenu.Separator();
		add(separator1);

		if (!sInBoxID.equals(sViewID)) {
			addContentsMenuItems();
		}

	 	// If on the Mac OS and the Menu bar is at the top of the OS screen, remove the menu shortcut Mnemonics.
		if (ProjectCompendium.isMac && (FormatProperties.macMenuBar || (!FormatProperties.macMenuBar && !FormatProperties.macMenuUnderline)) )
			UIUtilities.removeMenuMnemonics(getSubElements());

		if (bSimple) {		
			addExtenderButton();
			setDisplay(bSimple);
		}

		pack();
		setSize(WIDTH,HEIGHT);
	}
	
	/**
	 * Hide/show items depending on whether the user wants the simple view or simple.
	 * @param bSimple
	 */
	protected void setDisplay(boolean bSimple) {
		if (bSimple) {
			miImportXMLFlashmeeting.setVisible(false);
			miFileImport.setVisible(false);
			miImportCurrentView.setVisible(false);
			miImportMultipleViews.setVisible(false);
			miMenuItemMarkSeen.setVisible(false);
			miMenuItemMarkUnseen.setVisible(false);		
			separator1.setVisible(false);
			miMenuItemProperties.setVisible(false);	
		} else {
			miImportXMLFlashmeeting.setVisible(true);
			miFileImport.setVisible(true);
			miImportCurrentView.setVisible(true);
			miImportMultipleViews.setVisible(true);
			if (showMarkSeen) {
				miMenuItemMarkSeen.setVisible(true);
			}
			if (showMarkUnseen) {
				miMenuItemMarkUnseen.setVisible(true);	
			}
			separator1.setVisible(true);
			miMenuItemProperties.setVisible(true);				
		}		
		setControlItemStatus(bSimple);
		
		if (isVisible()) {
			setVisible(false);
			setVisible(true);
			requestFocus();
		}
	}

	/**
	 * Handles the event of an option being selected.
	 * @param evt the event associated with the option being selected.
	 */
	public void actionPerformed(ActionEvent evt) {
		super.actionPerformed(evt);
	}	
	
	/**
	 * Open the contents dialog for the given context.
	 */
	protected void openContents() {
		oUIList.getViewFrame().showEditDialog();
	}

	/**
	 * Open the contents dialog for the given context on the properties tab.
	 * Subclasses must implement this method.
	 */
	protected void openProperties() {
		oUIList.getViewFrame().showPropertiesDialog();
	}

	/**
	 * Open the contents dialog for the given context on the views tab.
	 * Subclasses must implement this method.
	 */
	protected void openViews() {
		oUIList.getViewFrame().showViewsDialog();
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

		UIList uiList = oUIList;
		uiList.updateTable();
		uiList.selectNode(uiList.getNumberOfNodes() - 1, ICoreConstants.MULTISELECT);
	}	
}
