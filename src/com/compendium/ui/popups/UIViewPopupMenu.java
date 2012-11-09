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

import com.compendium.core.*;
import com.compendium.core.datamodel.*;

import com.compendium.ui.*;
import com.compendium.ui.plaf.*;
import com.compendium.ProjectCompendium;

/**
 * This class draws the right-click menu for map views
 *
 * @author	Mohammed Sajid Ali / Michelle Bachler / Lakshmi Prabhakaran
 */
public class UIViewPopupMenu extends UIBaseMapPopupMenu implements ActionListener{
		
	/** A separator that can be turned off if required by simple menu.*/
	private JSeparator		separator1				= null;

	/** A separator that can be turned off if required by simple menu.*/
	private JSeparator		separator2				= null;
	
	/**
	 * Constructor. 
	 * @param title the title for this popup menu.
	 */
	public UIViewPopupMenu(String title) {
		super(title);
	}

	/**
	 * Constructor. Create the menus and items and draws the popup menu.
	 * @param title, the title for this popup menu.
	 * @param viewpaneUI com.compendium.ui.plaf.ViewPaneUI, the associated map for this popup menu.
	 */
	public UIViewPopupMenu(String title, ViewPaneUI viewpaneUI) {
		super(title);
		setViewPaneUI(viewpaneUI);
		init();
	}
		
	protected void init() {
		boolean bSimple = FormatProperties.simpleInterface;		

		createArrangeMenu();		
		createViewAlignMenu();

		// This is done this way so we can hide/show as required.
		separator1 = new JPopupMenu.Separator();
		add(separator1);

		addNodeCreationMenu();
		
		addSeparator();		

		createNodeTypeChangeMenu();
		
		addSeparator();

		addStencilMenuItems();

		separator1 = new JPopupMenu.Separator();
		add(separator1);

		View view = oViewPane.getView();
		addReferences(view.getReferenceNodes());
				
		addCutCopyPaste(shortcutKey);
		addDelete(shortcutKey);
		
		addSeparator();

		addImportMenu();
		addImportImage();
		addExportMenu();
		addSaveAsJPEG();
		
		addSeparator();
				
		addDelink();
		addSeparator();

		addJabberAndIXPanelMenus();
		
		addShortcut();
		addClone();
		addSeparator();

		addMoveLabelDetails();
		
		separator2 = new JPopupMenu.Separator();
		add(separator2);
	
		addSeenUnseen();
		
		//Lakshmi (4/25/06) - if node is in read state enable mark unseen
		// and disable mark seen and vice versa
		Enumeration e = oViewPaneUI.getViewPane().getSelectedNodes();
		for (;e.hasMoreElements();){
			UINode node = (UINode)e.nextElement();
			NodeSummary oNode = node.getNode();
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
		
		if (!(oViewPaneUI.getViewPane().getView().getId()).equals(ProjectCompendium.APP.getHomeView().getId())) {
			addSeparator();
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
		setSize(HEIGHT,WIDTH);
	}
		
	/**
	 * Hide/show items depending on whether the user wants the simple view or simple.
	 * @param bSimple
	 */
	protected void setDisplay(boolean bSimple) {
		if (bSimple) {
			mnuViewAlign.setVisible(false);
			separator1.setVisible(false);
			miStencilManagement.setVisible(false);
			miStencilManagement.setVisible(false);
			separator2.setVisible(false);
			miImportXMLFlashmeeting.setVisible(false);
			miFileImport.setVisible(false);
			miImportCurrentView.setVisible(false);
			miImportMultipleViews.setVisible(false);
			miMenuItemMarkSeen.setVisible(false);
			miMenuItemMarkUnseen.setVisible(false);	
			if (miMenuItemProperties != null) {
				miMenuItemProperties.setVisible(false);	
			}
		} else {
			mnuViewAlign.setVisible(true);
			separator1.setVisible(true);
			miStencilManagement.setVisible(true);
			miStencilManagement.setVisible(true);
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
			if (showMarkUnseen || showMarkSeen) {
				separator2.setVisible(true);
			}
		
			if (miMenuItemProperties != null) {
				miMenuItemProperties.setVisible(true);	
			}
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
		oViewPaneUI.getViewPane().getViewFrame().showEditDialog();
	}

	/**
	 * Open the contents dialog for the given context on the properties tab.
	 * Subclasses must implement this method.
	 */
	protected void openProperties() {
		oViewPaneUI.getViewPane().getViewFrame().showPropertiesDialog();
	}

	/**
	 * Open the contents dialog for the given context on the views tab.
	 * Subclasses must implement this method.
	 */
	protected void openViews() {
		oViewPaneUI.getViewPane().getViewFrame().showViewsDialog();
	}	
}
