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
import javax.help.*;
import javax.swing.*;

import com.compendium.*;
import com.compendium.ui.*;


/**
 * This class creates and manages the Window menu.
 *
 * @author	Michelle Bachler
 */
public class UIMenuWindows extends UIMenu implements ActionListener {

	/** The menu item to cascade all open views.*/
	private JMenuItem			miWindowCascade			= null;

	/** The menu item to tile all open views.*/
	private JMenuItem			miWindowTile			= null;

	/** The menu item to expand to full all open view windows.*/
	private JMenuItem			miWindowExpand			= null;

	/** The menu item close all open views (except the home view).*/
	private JMenuItem			miWindowCloseAll		= null;

	/** The platform specific shortcut key to use.*/
	private int shortcutKey;


	/**
	 * Constructor.
	 */
	public UIMenuWindows() {
		shortcutKey = ProjectCompendium.APP.shortcutKey;
		createMenu();
	}

	/**
	 * Create and return the Windows menu.
	 * @return JMenu the Windows menu.
	 */
	private JMenu createMenu() {
		mnuMainMenu	= new JMenu(LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuWindows.window"));  //$NON-NLS-1$
		mnuMainMenu.setMnemonic((LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuWindows.windowMnemonic")).charAt(0)); //$NON-NLS-1$
		CSH.setHelpIDString(mnuMainMenu,"menus.windows"); //$NON-NLS-1$
		refreshWindowsMenu();
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
		
		if (source.equals(miWindowCascade)) {
			ProjectCompendium.APP.onWindowCascade();
		} else if (source.equals(miWindowTile)) {
			ProjectCompendium.APP.onWindowTile();
		} else if (source.equals(miWindowExpand)) {
			ProjectCompendium.APP.onWindowExpand();
		} else if (source.equals(miWindowCloseAll)) {
			ProjectCompendium.APP.onWindowCloseAll();
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
	 * Refresh the windows menu. Called when views are opened and closed, to keep the menu up-to-date.
	 */
	public void refreshWindowsMenu() {

		mnuMainMenu.removeAll();

		miWindowCascade = new JMenuItem(LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuWindows.cascade"));  //$NON-NLS-1$
		miWindowCascade.setMnemonic((LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuWindows.cascadeMnemonic")).charAt(0)); //$NON-NLS-1$
		miWindowCascade.addActionListener(this);
		mnuMainMenu.add(miWindowCascade);

		miWindowTile = new JMenuItem(LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuWindows.tile"));  //$NON-NLS-1$
		miWindowTile.setMnemonic((LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuWindows.tileMnemonic")).charAt(0)); //$NON-NLS-1$
		miWindowTile.addActionListener(this);
		mnuMainMenu.add(miWindowTile);

		miWindowExpand = new JMenuItem(LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuWindows.expandAll"));  //$NON-NLS-1$
		miWindowExpand.setMnemonic((LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuWindows.expandAllMnemonic")).charAt(0)); //$NON-NLS-1$
		miWindowExpand.addActionListener(this);
		mnuMainMenu.add(miWindowExpand);

		miWindowCloseAll = new JMenuItem(LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuWindows.closeAll"));  //$NON-NLS-1$
		miWindowCloseAll.setMnemonic((LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuWindows.closeAllMnemonic")).charAt(0)); //$NON-NLS-1$
		miWindowCloseAll.addActionListener(this);
		mnuMainMenu.add(miWindowCloseAll);

		mnuMainMenu.addSeparator();

		JDesktopPane oDesktop = ProjectCompendium.APP.getDesktop();

		if (oDesktop != null) {
			JInternalFrame[] frames = oDesktop.getAllFrames();
			int count = frames.length;
			UIViewFrame viewFrame = null;
			JMenuItem item = null;

			for(int i = 0; i < count; i++) {
				viewFrame = (UIViewFrame)frames[i];
				String menuText = i + " " + viewFrame.getView().getLabel(); //$NON-NLS-1$
				item = new JMenuItem(menuText);
				if (i == 0) {
					item.setMnemonic(KeyEvent.VK_0);
				} else if (i == 1) {
					item.setMnemonic(KeyEvent.VK_1);
				} else if (i == 2) {
					item.setMnemonic(KeyEvent.VK_2);
				} else if (i == 3) {
					item.setMnemonic(KeyEvent.VK_3);
				} else if (i == 4) {
					item.setMnemonic(KeyEvent.VK_4);
				} else if (i == 5) {
					item.setMnemonic(KeyEvent.VK_5);
				} else if (i == 6) {
					item.setMnemonic(KeyEvent.VK_6);
				} else if (i == 7) {
					item.setMnemonic(KeyEvent.VK_7);					
				} else if (i == 8) {
					item.setMnemonic(KeyEvent.VK_8);					
				} else if (i == 9) {
					item.setMnemonic(KeyEvent.VK_9);					
				}					

				final UIViewFrame frame = viewFrame;

				item.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent event) {
						if (frame.isIcon()) {
							try {frame.setIcon(false);}
							catch(Exception ve) {ProjectCompendium.APP.displayError("Exception: (ProjectCompendiumFrame.actionPerformed) "+ve.getMessage());}  //$NON-NLS-1$
						}
						else  {
							if (frame instanceof UIMapViewFrame)
								((UIMapViewFrame)frame).setSelected(true);
							else
								((UIListViewFrame)frame).setSelected(true);
						}
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
}
