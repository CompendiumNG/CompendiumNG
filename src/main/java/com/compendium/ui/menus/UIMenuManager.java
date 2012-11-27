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


import java.awt.BorderLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.*;
import javax.help.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.undo.*;

import com.compendium.core.datamodel.*;
import com.compendium.core.*;
import com.compendium.*;
import com.compendium.ui.*;
import com.compendium.ui.tags.UITagTreePanel;

// ON NON-MAC PLATFORM, THIS REQUIRES AppleJavaExtensions.jar stub classes TO COMPILE
import com.apple.eawt.*;

/**
 * This class creates and managers the main frame's menu.
 *
 * @author	Michelle Bachler
 */
public class UIMenuManager implements IUIConstants, ICoreConstants {
	//private ScreenCaptureRecorder recorder = null;

	/** The HelpSet instance to use.*/
    private HelpSet 					mainHS 			= null;

	/** The HelpBroker instance to use.*/
    private HelpBroker 					mainHB			= null;

	/** The main menu bar instance.*/
	private JMenuBar					mbMenuBar		= null;

	/** The File menu.*/
	private UIMenuFile				oFile				= null;
	
	/** The Edit menu.*/
	private UIMenuEdit				oEdit				= null;

	/** The view menu.*/
	private UIMenuView				oView				= null;

	/** The Tools menu.*/
	private UIMenuTools				oTools				= null;

	/** The Favorites Menu.*/
	private UIMenuFavorites			oFavorites			= null;

	/** The Workspace menu.*/
	private UIMenuWorkspaces		oWorkspaces			= null;

	/** The Windows menu.*/
	private UIMenuWindows			oWindow				= null;

	/** The Help menu.*/
	private UIMenuHelp				oHelp				= null;
	
	/** The arrow to extend and unextend the menubar.*/
	private JMenu 					oExtender 			= null;
	
	/**Indicates whether this menu is draw as a Simple interface or a advance user inteerface.*/
	private boolean bSimpleInterface					= false;		

	/**
	 * Constructor.
	 * @param hs the HelpSet to use for menus and menuitems.
	 * @param hb the HelpBroker to use for menus and menuitems.
	 * @param isSimple indicates if the toolbars should be draw for a simple user interface, false for a complex one. 
	 */
	public UIMenuManager(HelpSet hs, HelpBroker hb, boolean isSimple) {
		mainHS = hs;
		mainHB = hb;
		bSimpleInterface = isSimple;
	}

	/**
	 * Creates and initializes the menu bar and its menus.
	 */
	public JMenuBar createMenuBar() {
		// MENU BAR
		mbMenuBar = new JMenuBar();
		return recreateMenuBar(bSimpleInterface);		
	}
	
	/**
	 * Creates and initializes the menu bar and its menus.
	 */
	public JMenuBar recreateMenuBar(boolean bSimple) {

		mbMenuBar.removeAll();
		
		// FILE MENU
		mbMenuBar.add(createFileMenu());

		// EDIT MENU
		mbMenuBar.add(createEditMenu());

		// View MENU
		mbMenuBar.add(createViewMenu());

		// TOOLS MENU
		mbMenuBar.add(createToolsMenu());

		// FAVORITES MENU
		mbMenuBar.add(createFavoritesMenu());
						
		// WORKSPACES MENU
		mbMenuBar.add(createWorkspacesMenu());
				
		// WINDOWS MENU
		mbMenuBar.add(createWindowsMenu());

		// HELP MENU
		mbMenuBar.add(createHelpMenu());
		
		// Mac l&f and menu at the top of screen, remove menu Mnemonics
		//if (ProjectCompendium.isMac && (FormatProperties.macMenuBar || (!FormatProperties.macMenuBar && !FormatProperties.macMenuUnderline)) )
		//	UIUtilities.removeMenuMnemonics(mbMenuBar.getSubElements());

		if (ProjectCompendium.isMac) {
			Application oMacApp = new Application();
			if (!oMacApp.isAboutMenuItemPresent())
				oMacApp.addAboutMenuItem();

			oMacApp.setEnabledAboutMenu(true);

			if (!oMacApp.isPreferencesMenuItemPresent())
				oMacApp.addPreferencesMenuItem();

			oMacApp.setEnabledPreferencesMenu(true);

			oMacApp.addApplicationListener(new ApplicationAdapter() {

				public void handlePreferences(ApplicationEvent e) {
					oTools.openUserOptions();
					e.setHandled(true);
				}
				public void handleAbout(ApplicationEvent e) {
					oHelp.openAbout();
					e.setHandled(true);
				}
				public void handleQuit(ApplicationEvent e) {
					oFile.exit();
					e.setHandled(true);
				}
				public void handleFileOpen(ApplicationEvent e) {
					e.setHandled(true);
				}
			});
		}		
		
		if (bSimple) {
			addExtenderButton();
		}
		return mbMenuBar;
	}

	/**
	 * Draw the button to extend or contract the menu bar.
	 * @param bSimple if simple, contract else extend.
	 */
	private void addExtenderButton() {
		oExtender = new JMenu();
		oExtender.setIcon(UIImages.get(IUIConstants.RIGHT_ARROW_ICON));
		oExtender.setName("right");
		oFavorites.getMenu().setVisible(false);
		oWorkspaces.getMenu().setVisible(false);
		oExtender.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				Thread thread = new Thread("UIMenuManager.extend") {
					public void run() {
						toggleMenuBar();
					}
				};
				thread.start();
			}
		});
		oExtender.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					toggleMenuBar();
				}
			}
		});
		
		mbMenuBar.add(oExtender);
	}
	
	/**
	 * Extend/collapse the menubar depending on current status. 
	 */
	private void toggleMenuBar() {
		if (oExtender.getName().equals("right")) {
			oFavorites.getMenu().setVisible(true);
			oWorkspaces.getMenu().setVisible(true);
			oExtender.setIcon(UIImages.get(IUIConstants.LEFT_ARROW_ICON));
			oExtender.setName("left");
		} else {
			oFavorites.getMenu().setVisible(false);
			oWorkspaces.getMenu().setVisible(false);
			oExtender.setIcon(UIImages.get(IUIConstants.RIGHT_ARROW_ICON));
			oExtender.setName("right");
		}
	}
	

	/**
	 * If true, redraw the simple form of this menubar else redraw the complex form.
	 * @param isSimple
	 */
	public void setIsSimple(boolean bSimple) {
		bSimpleInterface = bSimple;

		if (bSimple) {
			if (oExtender == null) {
				addExtenderButton();				
			}
		} else {
			mbMenuBar.remove(oExtender);
			oFavorites.getMenu().setVisible(true);
			oWorkspaces.getMenu().setVisible(true);
		}
		
		oFile.setIsSimple(bSimple);
		oEdit.setIsSimple(bSimple);
		oView.setIsSimple(bSimple);
		oTools.setIsSimple(bSimple);
		oHelp.setIsSimple(bSimple);
	}
	
	/**
	 * Create and return the File menu.
	 * @return JMenu the File menu.
	 */
	private JMenu createFileMenu() {
		oFile = new UIMenuFile(bSimpleInterface);		
		return oFile.getMenu();
	}

	/**
	 * Create and return the Edit menu.
	 * @return JMenu the Edit menu.
	 */
	private JMenu createEditMenu() {
		oEdit = new UIMenuEdit(bSimpleInterface);
		return oEdit.getMenu();
	}

	/**
	 * Create and return the Map menu.
	 * @return JMenu the Map menu.
	 */
	private JMenu createViewMenu() {
		oView = new UIMenuView(bSimpleInterface);
		return oView.getMenu();
	}

	/**
	 * Create and return the Tools menu.
	 * @return JMenu the Tools menu.
	 */
	private JMenu createToolsMenu() {
		oTools = new UIMenuTools(bSimpleInterface, mainHS, mainHB);
		return oTools.getMenu();
	}
	
	/**
	 * Create and return the Favorites menu.
	 * @return JMenu, the Favorites menu.
	 */
	private JMenu createFavoritesMenu() {
		oFavorites = new UIMenuFavorites();
		return oFavorites.getMenu();
	}

	/**
	 * Create and return the Workspaces menu.
	 * @return JMenu, the Workspaces menu.
	 */
	private JMenu createWorkspacesMenu() {
		oWorkspaces = new UIMenuWorkspaces();
		return oWorkspaces.getMenu();
	}

	/**
	 * Create and return the Windows menu.
	 * @return JMenu, the Windows menu.
	 */
	private JMenu createWindowsMenu() {
		oWindow = new UIMenuWindows();
		return oWindow.getMenu();
	}

	/**
	 * Create and return the Help menu.
	 * @return JMenu, the Help menu.
	 */
	private JMenu createHelpMenu() {
		oHelp = new UIMenuHelp(bSimpleInterface, mainHS, mainHB);
		return oHelp.getMenu();
	}
	
// VIEW MENU REDIRECTS
	
	/**
	 * Return the font size to its default and then appliy the passed text zoom.
	 * (To the default specificed by the user in the Project Options)
	 */
	public void onReturnTextAndZoom(int zoom) {
		if (oView != null) {
			oView.onReturnTextAndZoom(zoom);
		}	
	}

	/**
	 * Return the font size to its default 
	 * (To the default specificed by the user in the Project Options)
	 */
	public void onReturnTextToActual() {
		if (oView != null) {
			oView.onReturnTextToActual();
		}	
	}
	
	/**
	 * Increase the currently dislayed font size by one point.
	 */
	public void onIncreaseTextSize() {
		if (oView != null) {
			oView.onIncreaseTextSize();
		}	
	}
	
	/**
	 * Reduce the currently dislayed font size by one point.
	 */
	public void onReduceTextSize() {
		if (oView != null) {
			oView.onReduceTextSize();
		}					
	}
	
	/**
	 * Remove the Outline View from the tabbed pane.
	 * @param store indicates whether to store the change to the properties file.
	 */
	public void removeOutlineView(boolean store) {
		oView.removeOutlineView(store);
	}
	
	/**
	 * open the outline view of the type specified.
	 * @param sType the type of outline view to open
	 * @param store indicates whether to store the change to the properties file.
	 */
	public void addOutlineView(String sType, boolean store) {
		oView.addOutlineView(sType, store);
	}
	
	/**
	 * Remove the unread View from the tabbed pane.
	 * @param store indicates whether to store the change to the properties file.
	 */
	public void removeUnreadView(boolean store){
		oView.removeUnreadView(store);
	}
	
	/**
	 * open the unread view.
	 * @param store indicates whether to store the change to the properties file.
	 * @throws SQLException 
	 */
	public void addUnreadView(boolean store) throws SQLException{
		oView.addUnreadView(store);
	}
	
	/**
	 * Remove the tags View from the tabbed pane.
	 * @param store indicates whether to store the change to the properties file.
	 */
	public void removeTagsView(boolean store){
		oView.removeTagsView(store);
	}
	
	/**
	 * open the tags view.
	 * @param store indicates whether to store the change to the properties file.
	 */
	public void addTagsView(boolean store) {
		oView.addTagsView(store);
	}
	
	/**
	 * Zoom the current map to the next level (75/50/25/full);
	 */
	public void onZoomNext() {
		oView.onZoomNext();
	}

	/**
	 * Zoom the current map using the given scale.
	 * @param scale, the zoom scaling factor.
	 */
	public void onZoomTo(double scale) {
		oView.onZoomTo(scale);
	}

	/**
	 * Zoom the current map to fit it all on the visible view.
	 */
	public void onZoomToFit() {
		oView.onZoomToFit();
	}

	/**
	 * Zoom the current map back to normal and focus on the last selected node.
	 */
	public void onZoomRefocused() {
		oView.onZoomRefocused();
	}
	
	/**
	 * Record the state of the image rollover option.
	 */
	public void onImageRollover() {
		oView.onImageRollover();
	}

	/**
	 * Set the auto Search Label setting on and off.
	 */
	public void onSearchLabel() {
		oView.onSearchLabel();
	}	
	
	/**
	 * Select/unselect the given toolbar.
	 * @param enabled true to select, false to unselect.
	 */
	public void setToolbar(int nToolbar, boolean selected) {
		oView.setToolbar(nToolbar, selected);
	}
		
	/**
	 * Gets the outline view object
	 * @return UIViewOutline, the UIViewOutline object
	 */
	public UIViewOutline getOutlineView() {
		return oView.getOutlineView();
	}

	/**
	 * @return Returns the unreadView.
	 */
	public UIViewUnread getUnreadView() {
		return oView.getUnreadView();
	}

	/**
	 * @param outlineView The outlineView to set.
	 */
	public void setOutlineView(UIViewOutline outlineView) {
		oView.setOutlineView(outlineView);
	}

	/**
	 * @param unreadView The unreadView to set.
	 */
	public void setUnreadView(UIViewUnread unreadView) {
		oView.setUnreadView(unreadView);
	}		
		
	/**
	 * Select/unselect the aerial view.
	 * @param enabled true to select, false to unselect.
	 */
	public void setAerialView(boolean selected) {
		oView.setAerialView(selected);
	}

	/**
	 * Select/unselect the image rollover .
	 * @param enabled true to enable, false to disable.
	 */
	public void updateImageRollover(boolean enabled) {
		oView.updateImageRollover(enabled);
	}	
	
	/**
	 * Enable/disable the map menu and its components.
	 * @param enabled, true to enable, false to disable.
	 */
	public void setMapMenuEnabled(boolean enabled) {
		oView.setMapMenuEnabled(enabled);
	}
	
	
	// FILE MENU REDIRECTS
	
	/**
	 * Enable/disable the convert database menu option as appropriate.
	 */
	public void enableConvertMenuOptions() {
		oFile.enableConvertMenuOptions();
	}
	
	/**
	 * Draw the roster menu list for a Jabber connection.
	 *
	 * @param menu the menu to add the options to.
	 * @param node the node, associated with this menu.
	 * - only applies if request activated from node right-click menu, else value will be null.
	 * @param rosterEntries the roster entries to create menu items for.
	 */
	public void drawJabberRoster(JMenu menu, NodeSummary node, Enumeration rosterEntries) {
		oFile.drawJabberRoster(menu, node, rosterEntries);
	}

	/**
	 * Draw the roster menu list for the IX panel connection.
	 *
	 * @param menu the menu to add the options to.
	 * @param node the node, associated with this menu.
	 * - only applies if request activated from node right-click menu, else value will be null.
	 * @param rosterEntries the roster entries to create menu items for.
	 */
	public void drawIXRoster(JMenu menu, NodeSummary node, Enumeration rosterEntries) {
		oFile.drawIXRoster(menu, node, rosterEntries);
	}

	/**
	 * Enable/disable the jabber menu item.
	 * @param enabled true to enable, false to disable.
	 */
	public void setJabberMenuEnablement(boolean enabled) {
		oFile.setJabberMenuEnablement(enabled);
	}

	/**
	 * Enable/disable the jabber menu item.
	 * @param enabled true to enable, false to disable.
	 */
	public void setIXMenuEnablement(boolean enabled) {
		oFile.setIXMenuEnablement(enabled);
	}	

	/**
	 * Enable/disable the file open menu item.
	 * @param enabled true to enable, false to disable.
	 */
	public void setFileOpenEnablement(boolean enabled) {
		oFile.setFileOpenEnablement(enabled);
	}
	
	
	// EDIT MENU REDIRECTS
	/**
	 * Enable/disable the paste menu item.
	 * @param enabled, true to enable, false to disable.
	 */
	public void setPasteEnabled(boolean enabled) {
		oEdit.setPasteEnabled(enabled);
	}

	/**
	 * Enable/disable the external paste menu item.
	 * @param enabled, true to enable, false to disable.
	 */
	public void setExternalPasteEnablement(boolean enabled) {
		oEdit.setExternalPasteEnablement(enabled);
	}
	
	/**
	 * Refreshes the undo/redo buttons for the last action performed.
	 */
	public void refreshUndoRedo(UndoManager oUndoManager) {
		oEdit.refreshUndoRedo(oUndoManager);
	}

	
	// TOOLS MENU REDIRECTS
	
	/**
	 * Enable/disable the scribblepad option.
	 * @param enabled true to enable, false to disable.
	 */
	public void setScribblePadEnabled(boolean enabled) {
		oTools.setScribblePadEnabled(enabled);
	}

	/**
	 * Activate/Deactivate the scribblepad layer options.
	 * @param enabled true to enable, false to disable.
	 */
	public void setScribblePadActive(boolean enabled) {
		oTools.setScribblePadActive(enabled);
	}	
	
	/**
	 * Create the menu holding the currently available stencil sets.
	 */
	public void createStencilMenu() {
		oTools.createStencilMenu();
	}
	
	// HELP MENU REDIRECTS
	public void setWelcomeEnabled(boolean enable) {
		oHelp.setWelcomeEnabled(enable);
	}
	
	
//////////////////////////////////////////////////////////////////////////
	
	/**
	 * Refresh the workspaces menu with the new Vector of workspaces.
	 * @param workspaces the list of workspaces to refresh the menu with.
	 * @param sUserID the is of the user whose workspaces these are.
	 */
	public void refreshWorkspaceMenu(Vector workspaces, String sUserID) {
		if (oWorkspaces != null ) {
			oWorkspaces.refreshWorkspaceMenu(workspaces, sUserID);
		}
	}

	/**
	 * Refresh the favorites menu with the new Vector of favorites.
	 * @param favorites the list of favorites to refresh the menu with.
	 */
	public void refreshFavoritesMenu(Vector favorites) {
		if (oFavorites != null) {
			oFavorites.refreshFavoritesMenu(favorites);
		}
	}

	/**
	 * Refresh the windows menu. Called when views are opened and closed, to keep the menu up-to-date.
	 */
	public void refreshWindowsMenu() {
		if (oWindow != null) {
			oWindow.refreshWindowsMenu();
		}
	}	

	/**
	 * Updates the menus look and feels.
	 */
	public void updateLAF() {
		if (oFile != null ) {
			oFile.updateLAF();
		}
		if (oEdit != null) {
			oEdit.updateLAF();
		}
		if (oView != null) {
			oView.updateLAF();
		}
		if (oTools != null ) {
			oTools.updateLAF();
		}		
		if (oFavorites != null) {
			oFavorites.updateLAF();
		}
		if (oWorkspaces != null) {
			oWorkspaces.updateLAF();
		}
		if (oWindow != null) {
			oWindow.updateLAF();
		}
		if (oHelp != null) {
			oHelp.updateLAF();
		}
	}
		
	/**
	 * Updates the menus when a database project is closed.
	 */
	public void onDatabaseClose() {		
		if (oFile != null) {
			oFile.onDatabaseClose();
		}
		if (oEdit != null) {
			oEdit.onDatabaseClose();
		}
		if (oView != null) {
			oView.onDatabaseClose();
		}
		if (oTools != null ){
			oTools.onDatabaseClose();
		}		
		if (oFavorites != null) {
			oFavorites.onDatabaseClose();
		}
		if (oWorkspaces != null) {
			oWorkspaces.onDatabaseClose();
		}
		if (oWindow != null) {
			oWindow.onDatabaseClose();
		}
		if (oHelp != null) {
			oHelp.onDatabaseClose();
		}
	}

	/**
	 * Updates the menus when a database projects is opened.
	 */
	public void onDatabaseOpen() {
		if (oFile != null ) {
			oFile.onDatabaseOpen();
		}
		if (oEdit != null) {
			oEdit.onDatabaseOpen();
		}
		if (oView != null) {
			oView.onDatabaseOpen();
		}
		if (oTools != null ) {
			oTools.onDatabaseOpen();
		}		
		if (oFavorites != null) {
			oFavorites.onDatabaseOpen();
		}
		if (oWorkspaces != null) {
			oWorkspaces.onDatabaseOpen();
		}
		if (oWindow != null) {
			oWindow.onDatabaseOpen();
		}
		if (oHelp != null) {
			oHelp.onDatabaseOpen();
		}
	}
	
	/**
 	 * For menus to know when nodes are selected and deselected and adjust accordingly.
  	 * @param selected true for enabled false for disabled.
	 */
	public void setNodeSelected(boolean selected) {
		
		if (oFile != null) {
			oFile.setNodeSelected(selected);
		}
		if (oEdit != null) {
			oEdit.setNodeSelected(selected);
		}
		if (oView != null) {
			oView.setNodeSelected(selected);
		}
		if (oTools != null) {
			oTools.setNodeSelected(selected);
		}		
		if (oFavorites != null) {
			oFavorites.setNodeSelected(selected);
		}
		if (oWorkspaces != null) {
			oWorkspaces.setNodeSelected(selected);
		}
		if (oWindow != null) {
			oWindow.setNodeSelected(selected);
		}
		if (oHelp != null) {
			oHelp.setNodeSelected(selected);
		}
	}	
	
	/**
 	 * Enable/disable cut copy and delete menu items.
  	 * @param selected, true for enabled, false for disabled.
	 */
	public void setNodeOrLinkSelected(boolean selected) {
		
		if (oFile != null) {
			oFile.setNodeOrLinkSelected(selected);
		}
		if (oEdit != null) {
			oEdit.setNodeOrLinkSelected(selected);
		}
		if (oView != null) {
			oView.setNodeOrLinkSelected(selected);
		}
		if (oTools != null) {
			oTools.setNodeOrLinkSelected(selected);
		}		
		if (oFavorites != null) {
			oFavorites.setNodeOrLinkSelected(selected);
		}
		if (oWorkspaces != null) {
			oWorkspaces.setNodeOrLinkSelected(selected);
		}
		if (oWindow != null) {
			oWindow.setNodeOrLinkSelected(selected);
		}
		if (oHelp != null) {
			oHelp.setNodeOrLinkSelected(selected);
		}
	}

	/**
	 * Return a reference to the Windows menu.
	 * @return JMenu a reference to the Windows menu, or null if window menu does not exist.
	 */
	public JMenu getWindowsMenu() {
		if (oWindow != null) {
			return oWindow.getMenu();
		}
		return null;
	}

	/**
	 * Return a reference to the Favorites menu.
	 * @return JMenu a reference to the favorites menu, or null if favorites menu does not exist.
	 */
	public JMenu getFavoritesMenu() {
		if (oFavorites != null) {
			return oFavorites.getMenu();
		}
		return null;
	}

	/**
	 * Return a reference to the Workspaces menu.
	 * @return JMenu a reference to the Workspaces menu, or null if workspaces menu does not exist.
	 */
	public JMenu getWorkspacesMenu() {
		if (oWorkspaces != null) {
			return oWorkspaces.getMenu();
		}
		return null;
	}
}
