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

package com.compendium.ui.toolbars.system;

import javax.swing.JFrame;

/**
 * This interface contains the required methods to be supported by any class
 * wishing to be a UIToolBarManager
 *
 * @author	Michelle Bachler
 * @version	1.0
 */
public interface IUIToolBarManager {

	/**
	 * Returns the JFrame in which is to be the parent of the floating toolbars
	 *
	 * @return JFrame which represents the parent of the floating toolbars
	 */
	public JFrame getToolBarFloatFrame();

	/**
	 * Returns the toolbar controller for the top edge of the container
	 *
	 * @return UIToolBarController object for the top edge of the container
	 */
	public UIToolBarController getTopToolBarController();

	/**
	 * Returns the toolbar controller for the bottom edge of the container
	 *
	 * @return UIToolBarController object for the bottom edge of the container
	 */
	public UIToolBarController getBottomToolBarController();

	/**
	 * Returns the toolbar controller for the left edge of the container
	 *
	 * @return UIToolBarController object for the left hand side of the container
	 */
	public UIToolBarController getLeftToolBarController();

	/**
	 * Returns the toolbar controller for the right edge of the container
	 *
	 * @return UIToolBarController object for the right hand side of the container
	 */
	public UIToolBarController getRightToolBarController();

	/**
	 * Returns a xml string containing the toolbar data from the toolbar controllers
	 * The <code>UIToolBarController</code> class has a <code>toXML</code> method that can be used to get this information.
	 *
	 * @return a String object containing formatted xml representation of the toolbar data
	 */
	public String getToolbarXML();

	/**
	 * Tell the manager that a toolbar is now floating by passing the UIToolBarFloater object to it.
	 *
	 * @param UIToolBarFloater floater, the floating toolbar to add.
	 */
	public void addFloatingToolBar(UIToolBarFloater floater);

	/**
	 * Tell the manager that a toolbar is now docked by removing the UIToolBarFloater object from it.
	 *
	 * @param UIToolBarFloater floater, the floating toolbar to remove.
	 */
	public void removeFloatingToolBar(UIToolBarFloater floater);

}
