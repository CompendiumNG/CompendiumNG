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

package com.compendium.ui.menus;

import java.awt.event.*;
import java.util.*;
import javax.help.*;
import javax.swing.*;

import com.compendium.*;
import com.compendium.ui.*;

/**
 * This class creates and manages the Workspaces menu.
 *
 * @author	Michelle Bachler
 */
public class UIMenuWorkspaces extends UIMenu implements ActionListener {

	/** The Workspace menu.*/
	protected UIScrollableMenu	mnuMainMenu			= null;

	/** The menu item to open the workspace maintenance dialog.*/
	private JMenuItem			miWorkspaceMaint		= null;

	/**
	 * Constructor.
	 */
	public UIMenuWorkspaces() {
		createMenu();
	}

	/**
	 * Create and return the Workspaces menu.
	 * @return JMenu the Workspaces menu.
	 */
	private JMenu createMenu() {
		mnuMainMenu = new UIScrollableMenu(LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuWorkspaces.workspaces"), 3);  //$NON-NLS-1$
		mnuMainMenu.setMnemonic((LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuWorkspaces.workspacesMnemonic")).charAt(0)); //$NON-NLS-1$
		CSH.setHelpIDString(mnuMainMenu,"menus.workspace"); //$NON-NLS-1$
		return mnuMainMenu;
	}

	/**
	 * Handles most menu action event for this application.
	 *
	 * @param evt the generated action event to be handled.
	 */
	public void actionPerformed(ActionEvent evt) {

		ProjectCompendium.APP.setWaitCursor();

		Object source = evt.getSource();

		if (source.equals(miWorkspaceMaint)) {
			ProjectCompendium.APP.onWorkspaceMaintenace();
		}

		ProjectCompendium.APP.setDefaultCursor();
	}

	/**
	 * Hide/show items depending on whether the user wants the simple view or simple.
 	 * Does Nothing in this class.
	 * @param bSimple
	 */
	public void setDisplay(boolean bSimple){} 

	/**
	 * Updates the menus when a database project is closed.
	 */
	public void onDatabaseClose() {

		mnuMainMenu.setEnabled(false);
	}

	/**
	 * Updates the menus when a database projects is opened.
	 */
	public void onDatabaseOpen() {

		if (ProjectCompendium.APP.getModel() != null) {
			mnuMainMenu.setEnabled(true);
		}
	}

	/**
	 * Refresh the workspaces menu with the new Vector of workspaces.
	 * @param workspaces the list of workspaces to refresh the menu with.
	 * @param sUserID the is of the user whose workspaces these are.
	 */
	public void refreshWorkspaceMenu(Vector workspaces, String sUserID) {

		mnuMainMenu.removeAll();

		
		miWorkspaceMaint = new JMenuItem(LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuWorkspaces.manageWorkspaces")); //$NON-NLS-1$
		miWorkspaceMaint.setMnemonic((LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuWorkspaces.manageWorkspacesMnemonic")).charAt(0)); //$NON-NLS-1$
		miWorkspaceMaint.addActionListener(this);
		mnuMainMenu.add(miWorkspaceMaint);
		mnuMainMenu.addSeparator();

		if (workspaces != null && workspaces.size() > 0) {
			int count = workspaces.size();

			for (int i=0; i< count; i++) {

				Vector info = (Vector)workspaces.elementAt(i);
				String sLabel = (String)info.elementAt(1);

				String sHint = sLabel;
				if (sLabel.length() > 30) {
					sLabel = sLabel.substring(0,30) + "..."; //$NON-NLS-1$
				}

				JMenuItem item = new JMenuItem(sLabel);

				item.setToolTipText(sHint);

				final String  sWorkspaceID = (String)info.elementAt(0);
				final String fsUserID = sUserID;
				item.addActionListener( new ActionListener() {
					public void actionPerformed(ActionEvent evt) {
						Thread thread = new Thread("Refresh Workspaces") { //$NON-NLS-1$
							public void run() {
								ProjectCompendium.APP.addWorkspace(sWorkspaceID, fsUserID);
							}
						};
						thread.start();
					}
				});

				mnuMainMenu.add(item);
			}
		}
	}

	/**
 	 * Enable/disable cut copy and delete menu items.
 	 * Does Nothing.
  	 * @param selected true for enabled, false for disabled.
	 */
	public void setNodeOrLinkSelected(boolean selected) {}

	/**
 	 * Indicates when nodes on a view are selected and deselected.
 	 * Does Nothing.
  	 * @param selected true for selected false for deselected.
	 */
	public void setNodeSelected(boolean selected) {}
	
	/**
	 * Update the look and feel of the menu.
	 */
	public void updateLAF() {
		if (mnuMainMenu != null)
			SwingUtilities.updateComponentTreeUI(mnuMainMenu);
	}		
	
	/**
	 * Return a reference to the main menu.
	 * @return JMenu a reference to the main menu.
	 */
	public JMenu getMenu() {
		return mnuMainMenu;
	}	
}
