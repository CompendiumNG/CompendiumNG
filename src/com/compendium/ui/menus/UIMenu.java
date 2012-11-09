/******************************************************************************
 *                                                                            *
/*  (c) Copyright 2010 Verizon Communications USA and The Open University UK    *
 *                                                                            *
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
 *                                                                            *
 ******************************************************************************/

package com.compendium.ui.menus;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JSeparator;

import com.compendium.ProjectCompendium;

/**
 * This abstract class implements the interface IUIMenu that all menus should 
 * implement and methods for actions that are common to many menus. These can 
 * be shared by, or overrriden in subclasses.
 *
 * @author	Andrew Brasher
 */
public abstract class UIMenu implements IUIMenu {
	
	/** The main  menu for the implementing class i.e. the file, edit, view etc. menu.*/
	protected JMenu				mnuMainMenu				= null;
	
	/** The item with the extender arrow.*/
	protected UIControllerMenuItem oExtender			= null;	
	
	/**Indicates whether this menu is draw as a Simple interface or a advance user interface.*/
	protected boolean bSimpleInterface					= false;
	
	/** Menu separators: all menus with separators have at least 3. Menu classes with more need to declare 	them e.g. separator4 **/
	protected JSeparator			separator1			= null;
	protected JSeparator			separator2			= null;
	protected JSeparator			separator3			= null;

	/**
	 * Return a reference to the main menu.
	 * @return JMenu a reference to the main menu.
	 */
	public JMenu getMenu() {
		return mnuMainMenu;
	}

	/**  Updates the menu when a database project is closed. **/
	public abstract void onDatabaseClose();

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
 	 * Does nothing here, override for a particular action e.g. for the edit menu
 	 * in class UIMenuEdit.
  	 * @param selected true for enabled, false for disabled.
	 */
	public void setNodeOrLinkSelected(boolean selected) {
	}
	
	/**
	 * Set the arrow on the menu to toggle up and down;
	 * @param bSimple
	 */
	protected void setControlItemStatus(boolean bSimple) {
		if (bSimple) {
			oExtender.pointDown();
		} else {
			oExtender.pointUp();
		}
	}
	
	/**
	 * Hide/show items depending on whether the user wants the simple view or simple.
	 * @param bSimple
	 */
	protected abstract void setDisplay(boolean bSimple); 
	
	/**
	 *  Add the button for extending the menu from simple to full.
	 *  This calls setDisplay to hide advanced menu items
	 */
	protected void addExtenderButton() {
		if (oExtender == null) {
			oExtender = new UIControllerMenuItem();	
			oExtender.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					setDisplay(!oExtender.isDown());
					oExtender.setFocus();
				}
			});
			mnuMainMenu.add(oExtender);				
		}
	}

	/**
	 * If true, redraw the simple form of this menu, else redraw the complex form.
	 * @param isSimple
	 */
	public void setIsSimple(boolean bSimple) {
		bSimpleInterface = bSimple;
		if (bSimple) {
			addExtenderButton();
		} else {
			if (oExtender != null) {
				this.mnuMainMenu.remove(oExtender);
			}
		}
		setDisplay(bSimple);
		onDatabaseOpen();				
	}
}
