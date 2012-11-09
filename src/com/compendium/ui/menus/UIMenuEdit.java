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
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.*;
import java.util.Enumeration;
import java.util.Vector;

import javax.help.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.MenuKeyListener;
import javax.swing.plaf.basic.BasicMenuUI;
import javax.swing.undo.*;

import com.compendium.*;
import com.compendium.ui.*;
import com.compendium.ui.toolbars.system.UIToolBarControllerRow;

/**
 * This class creates and manages the Edit menu.
 *
 * @author	Michelle Bachler
 */
public class UIMenuEdit extends UIMenu implements ActionListener {
	
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
	private JMenuItem			miMenuSearch				= null;
	
	/** The platform specific shortcut key to use.*/
	private int shortcutKey;
		
	
	/**
	 * Constructor.
	 * @param bSimple true if the simple interface should be draw, false if the advanced. 
	 */
	public UIMenuEdit(boolean bSimple) {
		shortcutKey = ProjectCompendium.APP.shortcutKey;
		this.bSimpleInterface = bSimple;		
		
		mnuMainMenu	= new JMenu(LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuEdit.edit"));  //$NON-NLS-1$
		CSH.setHelpIDString(mnuMainMenu,"menus.edit"); 
		mnuMainMenu.setMnemonic((LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuEdit.editMnemonic")).charAt(0)); //$NON-NLS-1$
		
		createMenuItems(bSimple);
	}
		
	/**
	 * Create and return the Edit menu.
	 * @return JMenu the Edit menu.
	 */
	private JMenu createMenuItems(boolean bSimple) {

		//Undo
		miEditUndo = new JMenuItem(LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuEdit.undo"));  //$NON-NLS-1$
		miEditUndo.setAccelerator(KeyStroke.getKeyStroke( KeyEvent.VK_Z, shortcutKey));
		miEditUndo.setMnemonic((LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuEdit.undoMnemonic")).charAt(0)); //$NON-NLS-1$
		miEditUndo.addActionListener(this);
		mnuMainMenu.add(miEditUndo);
		miEditUndo.setEnabled(false);

		//Redo
		miEditRedo = new JMenuItem(LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuEdit.redo"));  //$NON-NLS-1$
		miEditRedo.setAccelerator(KeyStroke.getKeyStroke( KeyEvent.VK_Y, shortcutKey));
		miEditRedo.setMnemonic((LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuEdit.redoMnemonic")).charAt(0)); //$NON-NLS-1$
		miEditRedo.addActionListener(this);
		mnuMainMenu.add(miEditRedo);
		miEditRedo.setEnabled(false);

		mnuMainMenu.addSeparator();

		//Cut
		miEditCut = new JMenuItem(LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuEdit.cut"));  //$NON-NLS-1$
		miEditCut.setAccelerator(KeyStroke.getKeyStroke( KeyEvent.VK_X, shortcutKey));
		miEditCut.setMnemonic((LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuEdit.cutMnemonic")).charAt(0)); //$NON-NLS-1$
		miEditCut.addActionListener(this);
		miEditCut.setEnabled(false);
		mnuMainMenu.add(miEditCut);

		miEditCopy = new JMenuItem(LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuEdit.copy"));  //$NON-NLS-1$
		miEditCopy.setAccelerator(KeyStroke.getKeyStroke( KeyEvent.VK_C, shortcutKey));
		miEditCopy.setMnemonic((LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuEdit.copyMnemonic")).charAt(0)); //$NON-NLS-1$
		miEditCopy.addActionListener(this);
		miEditCopy.setEnabled(false);
		mnuMainMenu.add(miEditCopy);

		miEditPaste = new JMenuItem(LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuEdit.paste"));  //$NON-NLS-1$
		miEditPaste.setAccelerator(KeyStroke.getKeyStroke( KeyEvent.VK_V, shortcutKey));
		miEditPaste.setMnemonic((LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuEdit.pasteMnemonic")).charAt(0)); //$NON-NLS-1$
		miEditPaste.addActionListener(this);
		miEditPaste.setEnabled(false);
		mnuMainMenu.add(miEditPaste);

		mnuMainMenu.addSeparator();

		miEditExternalCopy = new JMenuItem(LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuEdit.copyAnotherProject"));  //$NON-NLS-1$
		miEditExternalCopy.setMnemonic((LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuEdit.copyAnotherProjectMnemonic")).charAt(0)); //$NON-NLS-1$
		miEditExternalCopy.addActionListener(this);
		miEditExternalCopy.setEnabled(false);
		mnuMainMenu.add(miEditExternalCopy);
	
		miEditExternalPaste = new JMenuItem(LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuEdit.pasteAnotherProject"));  //$NON-NLS-1$
		miEditExternalPaste.setMnemonic((LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuEdit.pasteAnotherProjectMnemonic")).charAt(0)); //$NON-NLS-1$
		miEditExternalPaste.addActionListener(this);
		miEditExternalPaste.setEnabled(false);
		mnuMainMenu.add(miEditExternalPaste);
	
		separator1 = new JPopupMenu.Separator();
		mnuMainMenu.add(separator1);

		miEditSelectAll = new JMenuItem(LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuEdit.selectAll"));  //$NON-NLS-1$
		miEditSelectAll.setAccelerator(KeyStroke.getKeyStroke( KeyEvent.VK_A, shortcutKey));
		miEditSelectAll.setMnemonic((LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuEdit.selectAllMnemonic")).charAt(0)); //$NON-NLS-1$
		miEditSelectAll.addActionListener(this);
		mnuMainMenu.add(miEditSelectAll);

		miEditDelete = new JMenuItem(LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuEdit.delete"));  //$NON-NLS-1$
		miEditDelete.setAccelerator(KeyStroke.getKeyStroke( KeyEvent.VK_DELETE, 0));
		miEditDelete.setMnemonic((LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuEdit.deleteMnemonic")).charAt(0)); //$NON-NLS-1$
		miEditDelete.addActionListener(this);
		miEditDelete.setEnabled(false);
		mnuMainMenu.add(miEditDelete);

		mnuMainMenu.addSeparator();

		miMenuSearch = new JMenuItem(LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuEdit.search"));  //$NON-NLS-1$
		miMenuSearch.setAccelerator(KeyStroke.getKeyStroke( KeyEvent.VK_F, shortcutKey));
		miMenuSearch.setMnemonic((LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuEdit.searchMnemonic")).charAt(0)); //$NON-NLS-1$
		miMenuSearch.addActionListener(this);
		mnuMainMenu.add(miMenuSearch);
		
		if (bSimple) {		
			addExtenderButton();
			setDisplay(bSimple);
		}
		
		return mnuMainMenu;
	}
	
	/**
	 * Hide/show items depending on whether the user wants the simple view or simple.
	 * @param bSimple
	 */
	protected void setDisplay(boolean bSimple) {
		if (bSimple) {
			miEditExternalCopy.setVisible(false);
			miEditExternalPaste.setVisible(false);
			separator1.setVisible(false);
		} else {
			miEditExternalCopy.setVisible(true);
			miEditExternalPaste.setVisible(true);
			separator1.setVisible(true);
		}
		
		setControlItemStatus(bSimple);
		
		JPopupMenu pop = mnuMainMenu.getPopupMenu();
		if (pop.isVisible()) {
			pop.setVisible(false);
			pop.setVisible(true);
			pop.requestFocus();
		}
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
		else if (source.equals(miMenuSearch))
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
	 * Creates an XML string representation of the data in this object.
	 *
	 * @return String, an XML string representation of this object.
	 */
	public String toXML() {

		StringBuffer data = new StringBuffer(100);

		data.append("<menu>"); //$NON-NLS-1$
		data.append("\n"); //$NON-NLS-1$

		Component[] comps = this.getMenu().getComponents();
		int count = comps.length;
		for (int i=0; i<count; i++) {
			Component comp = comps[i];
			if (comp instanceof JMenu) {
				JMenu menu = (JMenu)comp;
				String label = menu.getText();
				data.append("\t<menu>\n"); //$NON-NLS-1$
				data.append("\t</menu>\n"); //$NON-NLS-1$
			} else if (comp instanceof JMenuItem) {
				JMenuItem item = (JMenuItem)comp;
				String label = item.getText();
				data.append("\t<menuitem>\n"); //$NON-NLS-1$
				data.append("\t</menuitem>\n"); //$NON-NLS-1$
			} else if (comp instanceof JSeparator) {
				data.append("\t<separator>\n"); //$NON-NLS-1$
				data.append("\t</separator>\n"); //$NON-NLS-1$
			}			
		}				
		
		data.append("</menu>\n"); //$NON-NLS-1$

		return data.toString();
	}	
}
