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

import com.compendium.core.datamodel.*;
import com.compendium.*;
import com.compendium.ui.*;

/**
 * This class creates and manages the Favorites menu.
 *
 * @author	Michelle Bachler
 */
public class UIMenuFavorites extends UIMenu implements ActionListener {

	protected UIScrollableMenu	mnuMainMenu			= null;

	private JMenuItem			miFavoriteMaint			= null;

	/**
	 * Constructor.
	 */
	public UIMenuFavorites() {
		createMenu();
	}

	/**
	 * Create and return the Favorites menu.
	 * @return JMenu the Favorites menu.
	 */
	private JMenu createMenu() {		
		mnuMainMenu = new UIScrollableMenu(LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuFavorites.bookmarks"), 2);  //$NON-NLS-1$
		mnuMainMenu.setMnemonic((LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuFavorites.bookmarksMnemonic")).charAt(0)); //$NON-NLS-1$
		CSH.setHelpIDString(mnuMainMenu,"menus.favorite"); //$NON-NLS-1$
		return mnuMainMenu;
	}

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
 	 * Enable/disable menu items when nodes or links selected / deselected.
 	 * Does Nothing in this class.
  	 * @param selected true for enabled, false for disabled.
	 */
	public void setNodeOrLinkSelected(boolean selected) {}
	
	/**
 	 * Indicates when nodes on a view are selected and deselected.
 	 * Does Nothing in this class.
  	 * @param selected true for selected false for deselected.
	 */
	public void setNodeSelected(boolean selected) {}
	
	/**
	 * Hide/show items depending on whether the user wants the simple view or simple.
 	 * Does Nothing in this class.
	 * @param bSimple
	 */
	public void setDisplay(boolean bSimple){} 
	
	/**
	 * Handles most menu action event for this application.
	 *
	 * @param evt the generated action event to be handled.
	 */
	public void actionPerformed(ActionEvent evt) {

		ProjectCompendium.APP.setWaitCursor();

		Object source = evt.getSource();
	
		if (source.equals(miFavoriteMaint)) {
			ProjectCompendium.APP.onFavoriteMaintenace();
		}
		
		ProjectCompendium.APP.setDefaultCursor();		
	}
	
	
	/**
	 * Refresh the favorites menu with the new Vector of favorites.
	 * @param favorites the list of favorites to refresh the menu with.
	 */
	public void refreshFavoritesMenu(Vector favorites) {

		mnuMainMenu.removeAll();		

		miFavoriteMaint = new JMenuItem((LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuFavorites.manageBookmarks"))); //$NON-NLS-1$
		miFavoriteMaint.setMnemonic((LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuFavorites.manageBookmarksMnemonic")).charAt(0)); //$NON-NLS-1$
		miFavoriteMaint.addActionListener(this);
		mnuMainMenu.add(miFavoriteMaint);
		
		mnuMainMenu.addSeparator();
		
		if (favorites != null && favorites.size() > 0) {
			int count = favorites.size();
			Favorite fav = null;
			int index = 0;
			String sNodeLabel = "";  //$NON-NLS-1$
			String sViewLabel = "";  //$NON-NLS-1$
			
			Vector vtOldFavorites = new Vector();
			String sViewID = "";  //$NON-NLS-1$
			JMenuItem item = null;

			for (int i=0; i< count; i++) {

				fav = (Favorite)favorites.elementAt(i);

				sViewID = fav.getViewID();								
				
				String sLabel = fav.getLabel();
				String hint = "";  //$NON-NLS-1$

				index = sLabel.indexOf("&&&");  //$NON-NLS-1$
				if (index != -1) {
					sViewLabel = sLabel.substring(0, index);
					sNodeLabel = sLabel.substring(index+3);
					hint = sNodeLabel+" ( "+sViewLabel+" )";   //$NON-NLS-1$ //$NON-NLS-2$
				} else {
					sNodeLabel = sLabel;
					hint = sNodeLabel;
				}
				
				int nType = fav.getType();

				if (sNodeLabel.length() > 30) {
					sNodeLabel = sNodeLabel.substring(0,30) + "..."; //$NON-NLS-1$
				}

				if (nType > -1) {
					if (sViewID == null || sViewID.equals("")) {  //$NON-NLS-1$
						item = new JMenuItem(sNodeLabel, UINode.getNodeImageSmall(nType));
					} else {
						item = new JMenuItem(sNodeLabel, UIImages.getReferenceIcon(IUIConstants.REFERENCE_INTERNAL_SM_ICON));						
					}
				}
				else
					item = new JMenuItem(sNodeLabel);

				item.setToolTipText(hint);

				final Favorite ffav = fav;
				item.addActionListener( new ActionListener() {
					public void actionPerformed(ActionEvent evt) {
						Thread thread = new Thread("Refresh Favorites") { //$NON-NLS-1$
							public void run() {
								ProjectCompendium.APP.addFavorite(ffav);
							}
						};
						thread.start();
					}
				});
				
				if ( sViewID == null || sViewID.equals("")) {  //$NON-NLS-1$
					vtOldFavorites.add(item);
				} else {						
					mnuMainMenu.add(item);
				}
			}
			
			int oldcount = vtOldFavorites.size();
			if ( oldcount > 0) {
				
				if (count > oldcount) {
					mnuMainMenu.addSeparator();
				}
				
				for (int i=0; i< oldcount; i++) {
					item = (JMenuItem)vtOldFavorites.elementAt(i);		
					if (item != null && !item.getText().equals("")) {  //$NON-NLS-1$
						mnuMainMenu.add(item);
					}
				}
			}
		}
	}

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
