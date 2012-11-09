/********************************************************************************
 *                                                                              *
 *  (c) Copyright 2009 Verizon Communications USA and The Open University UK    *
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
import javax.swing.undo.*;

import com.compendium.*;
import com.compendium.ui.*;

/**
 * This class creates and manages the Edit menu.
 *
 * @author	Michelle Bachler
 */
public class UIMenuEdit implements IUIMenu, ActionListener {

	/** The Edit menu.*/
	private JMenu				mnuMainMenu					= null;

	/** The menu item to undo the last stored undoable action.*/
	private JMenuItem			miEditUndo				= null;

	/** The menu item to redo the last stored redoable action.*/
	private JMenuItem			miEditRedo				= null;

	/** The menu item to cut the selected items from the current view.*/
	private JMenuItem			miEditCut				= null;

	/** The menu item to copy the selected items from the current view.*/
	private JMenuItem			miEditCopy				= null;

	/** The menu item to copy the selected items and thier children for an external paste.*/
	private JMenuItem			miEditExternalCopy		= null;

	/** The menu item to paste nodes and links copied with the external copy command.*/
	private JMenuItem			miEditExternalPaste		= null;

	/** The menu item to paste any node and links on the cipbaord into the current view.*/
	private JMenuItem			miEditPaste				= null;

	/** The menu item to delete all selected nodes and links from the current view.*/
	private JMenuItem			miEditDelete			= null;

	/** The menu item to select all nodes and links in the current view.*/
	private JMenuItem			miEditSelectAll			= null;

	/** The menu item to open the search dialog.*/
	private JMenuItem			miSearch				= null;

	/** The platform specific shortcut key to use.*/
	private int shortcutKey;
	
	/**Indicates whether this menu is draw as a Simple interface or a advance user inteerface.*/
	private boolean bSimpleInterface					= false;
	
	
	/**
	 * Constructor.
	 * @param bSimple true if the simple interface should be draw, false if the advanced. 
	 */
	public UIMenuEdit(boolean bSimple) {
		shortcutKey = ProjectCompendium.APP.shortcutKey;
		this.bSimpleInterface = bSimple;		
		
		mnuMainMenu	= new JMenu("Edit"); 
		CSH.setHelpIDString(mnuMainMenu,"menus.edit"); //$NON-NLS-1$
		mnuMainMenu.setMnemonic(KeyEvent.VK_E);
		
		createMenuItems();
	}
	
	/**
	 * If true, redraw the simple form of this menu, else redraw the complex form.
	 * @param isSimple true for the simple menu, false for the advanced.
	 */public void setIsSimple(boolean isSimple) {
		bSimpleInterface = isSimple;
		recreateMenu();
	}

	/**
	 * Redraw the menu items
	 */
	private void recreateMenu() {
		mnuMainMenu.removeAll();
		createMenuItems();
		onDatabaseOpen();				
	}
	
	/**
	 * Create and return the Edit menu.
	 * @return JMenu the Edit menu.
	 */
	private JMenu createMenuItems() {

		//Undo
		miEditUndo = new JMenuItem("Undo"); 
		miEditUndo.setAccelerator(KeyStroke.getKeyStroke( KeyEvent.VK_Z, shortcutKey));
		miEditUndo.setMnemonic(KeyEvent.VK_U);
		miEditUndo.addActionListener(this);
		mnuMainMenu.add(miEditUndo);
		miEditUndo.setEnabled(false);

		//Redo
		miEditRedo = new JMenuItem("Redo"); 
		miEditRedo.setAccelerator(KeyStroke.getKeyStroke( KeyEvent.VK_Y, shortcutKey));
		miEditRedo.setMnemonic(KeyEvent.VK_R);
		miEditRedo.addActionListener(this);
		mnuMainMenu.add(miEditRedo);
		miEditRedo.setEnabled(false);

		mnuMainMenu.addSeparator();

		//Cut
		miEditCut = new JMenuItem("Cut"); 
		miEditCut.setAccelerator(KeyStroke.getKeyStroke( KeyEvent.VK_X, shortcutKey));
		miEditCut.setMnemonic(KeyEvent.VK_T);
		miEditCut.addActionListener(this);
		miEditCut.setEnabled(false);
		mnuMainMenu.add(miEditCut);

		miEditCopy = new JMenuItem("Copy"); 
		miEditCopy.setAccelerator(KeyStroke.getKeyStroke( KeyEvent.VK_C, shortcutKey));
		miEditCopy.setMnemonic(KeyEvent.VK_C);
		miEditCopy.addActionListener(this);
		miEditCopy.setEnabled(false);
		mnuMainMenu.add(miEditCopy);

		miEditPaste = new JMenuItem("Paste"); 
		miEditPaste.setAccelerator(KeyStroke.getKeyStroke( KeyEvent.VK_V, shortcutKey));
		miEditPaste.setMnemonic(KeyEvent.VK_P);
		miEditPaste.addActionListener(this);
		miEditPaste.setEnabled(false);
		mnuMainMenu.add(miEditPaste);

		mnuMainMenu.addSeparator();

		miEditExternalCopy = new JMenuItem("Copy to Another Project"); 
		miEditExternalCopy.setMnemonic(KeyEvent.VK_A);
		miEditExternalCopy.addActionListener(this);
		miEditExternalCopy.setEnabled(false);
		mnuMainMenu.add(miEditExternalCopy);
	
		miEditExternalPaste = new JMenuItem("Paste from Another Project"); 
		miEditExternalPaste.setMnemonic(KeyEvent.VK_F);
		miEditExternalPaste.addActionListener(this);
		miEditExternalPaste.setEnabled(false);
		mnuMainMenu.add(miEditExternalPaste);
	
		mnuMainMenu.addSeparator();

		miEditSelectAll = new JMenuItem("Select All"); 
		miEditSelectAll.setAccelerator(KeyStroke.getKeyStroke( KeyEvent.VK_A, shortcutKey));
		miEditSelectAll.setMnemonic(KeyEvent.VK_S);
		miEditSelectAll.addActionListener(this);
		mnuMainMenu.add(miEditSelectAll);

		miEditDelete = new JMenuItem("Delete"); 
		miEditDelete.setAccelerator(KeyStroke.getKeyStroke( KeyEvent.VK_DELETE, 0));
		miEditDelete.setMnemonic(KeyEvent.VK_D);
		miEditDelete.addActionListener(this);
		miEditDelete.setEnabled(false);
		mnuMainMenu.add(miEditDelete);

		mnuMainMenu.addSeparator();

		miSearch = new JMenuItem("Search..."); 
		miSearch.setAccelerator(KeyStroke.getKeyStroke( KeyEvent.VK_F, shortcutKey));
		miSearch.setMnemonic(KeyEvent.VK_S);
		miSearch.addActionListener(this);
		mnuMainMenu.add(miSearch);

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

		if (source.equals(miEditUndo))
			ProjectCompendium.APP.onEditUndo();
		else if (source.equals(miEditRedo))
			ProjectCompendium.APP.onEditRedo();
		else if (source.equals(miEditCut))
			ProjectCompendium.APP.onEditCut();
		else if (source.equals(miEditCopy))
			ProjectCompendium.APP.onEditCopy();
		else if (source.equals(miEditPaste))
			ProjectCompendium.APP.onEditPaste();

		else if (source.equals(miEditExternalCopy))
			ProjectCompendium.APP.onEditExternalCopy();
		else if (source.equals(miEditExternalPaste))
			ProjectCompendium.APP.onEditExternalPaste();

		else if (source.equals(miEditDelete))
			ProjectCompendium.APP.onEditDelete();
		else if (source.equals(miEditSelectAll))
			ProjectCompendium.APP.onEditSelectAll();
		else if (source.equals(miSearch))
			ProjectCompendium.APP.onSearch();

		ProjectCompendium.APP.setDefaultCursor();
	}

	/**
	 * Updates the menu when a database project is closed.
	 */
	public void onDatabaseClose() {

		try {
			setNodeOrLinkSelected(false);
			mnuMainMenu.setEnabled(false);
		}
		catch (Exception ex) {
			ex.printStackTrace();
			ProjectCompendium.APP.displayError("Exception: (UIMenuManager.onDatabaseClose) " + ex.getMessage()); 
		}
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
 	 * Enable/disable cut copy and delete menu items.
  	 * @param selected true for enabled, false for disabled.
	 */
	public void setNodeOrLinkSelected(boolean selected) {
				
		if (miEditCopy != null) {
			miEditCopy.setEnabled(selected);
		}
		if (miEditExternalCopy != null) {
			miEditExternalCopy.setEnabled(selected);
		}
		if (miEditCut != null) {
			miEditCut.setEnabled(selected);
		}
		if (miEditDelete != null) {
			miEditDelete.setEnabled(selected);
		}
	}
	
	/**
 	 * Indicates when nodes on a view are selected and deselected.
 	 * Does Nothing.
  	 * @param selected true for selected false for deselected.
	 */
	public void setNodeSelected(boolean selected) {}
	
	/**
	 * Enable/disable the paste menu item.
	 * @param enabled true to enable, false to disable.
	 */
	public void setPasteEnabled(boolean enabled) {
		if (miEditPaste != null) {
			miEditPaste.setEnabled(enabled);
		}
	}

	/**
	 * Enable/disable the external paste menu item.
	 * @param enabled true to enable, false to disable.
	 */
	public void setExternalPasteEnablement(boolean enabled) {
		if (miEditExternalPaste != null) {
			miEditExternalPaste.setEnabled( enabled);
		}
	}

	/**
	 * Refreshes the undo/redo buttons for the last action performed.
	 */
	public void refreshUndoRedo(UndoManager oUndoManager) {

		if (miEditUndo != null) {
			miEditUndo.setText(oUndoManager.getUndoPresentationName());
			miEditUndo.setEnabled(oUndoManager.canUndo());
		}
		if (miEditRedo != null) {
			miEditRedo.setText(oUndoManager.getRedoPresentationName());
			miEditRedo.setEnabled(oUndoManager.canRedo());
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
