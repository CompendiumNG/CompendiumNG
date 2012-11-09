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

package com.compendium.ui.toolbars;

import com.compendium.ui.toolbars.system.*;


/**
 * This class is the interface of all Compendium toolbar classes
 *
 * @author	Michelle Bachler
 * @version	1.0
 */
public interface IUIToolBar {

	
	/**
	 * Update the look and feel of all the toolbars
	 */
	public void updateLAF();

	/**
	 * Enable/disable the toolbar.
	 * @param enabled true to enable, false to disable.
	 */
	public void setEnabled(boolean enabled);
	
	/**
	 * Updates the menu when a new database project is opened.
	 */
	public void onDatabaseOpen(); 

	/**
	 * Updates the menu when the current database project is closed.
	 */
	public void onDatabaseClose(); 
	/**
 	 * Enable the toobar icons.
 	 * @param selected, true to enable, false to disable.
	 */
	public void setNodeSelected(boolean selected); 
	
	/**
 	 * Does Nothing
 	 * @param selected, true to enable, false to disable.
	 */
	public void setNodeOrLinkSelected(boolean selected);
	
	/**
	 * Return the toolbar object.
	 * @return the toolbar object.
	 */
	public UIToolBar getToolBar();
	
	/**
	 * Return if this toolbar is active by default, or if it must be switched on by the user.
	 * @return true if the toolbar is active by default, else false.
	 */
	public boolean getDefaultActiveState();
	
	/**
	 * Return a unique integer identifier for this toolbar type.
	 * @return a unique integer identifier for this toolbar type.
	 */
	public int getType();
}
