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

import java.awt.Component;
import java.awt.MouseInfo;

import javax.swing.JFrame;
import javax.swing.JPopupMenu;
import javax.swing.event.PopupMenuEvent;

/**
 * Base class for popup menus.
 * @author rudolf
 *
 */
public class UIPopupMenu extends JPopupMenu {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1230200820967163146L;
	/**
	 * The frame of the parent.
	 */
	protected JFrame oAppframe = null;

	/**
	 * Constructor.
	 * @param appframe the main application frame to bring to front when 
	 * 	menu is shown
	 */
	public UIPopupMenu( JFrame appframe ) {
		super();
		oAppframe = appframe;
	}

	/** 
	 * Show this popup at the current mouse position 
	 * @param invoker the component on which to show the popup
	 */
	public void show(Component invoker) {
		int x = MouseInfo.getPointerInfo().getLocation().x - invoker.getLocationOnScreen().x;
		int y = MouseInfo.getPointerInfo().getLocation().y - invoker.getLocationOnScreen().y;
	
		// bring application to front so that the popup menu won't get obscured
		oAppframe.setAlwaysOnTop(true); 
		show( invoker, x, y );
		repaint(); // without this, menu sometimes gets drawn incompletely
		try {
			synchronized( this ) {
				// wait until the user has made his choice -> actionPerformed()
				// (this is executed in the application thread)
				wait();
			}
		}
		catch( InterruptedException ex ) {
			System.out.println("UIDropFilePopup.show: Interrupted."); //$NON-NLS-1$
		}
		finally {
			oAppframe.setAlwaysOnTop(false);
		}
	}

	/**
	 * When the popup menu becomes visible.
	 * @param event The event that occured
	 */
	public void popupMenuWillBecomeVisible(PopupMenuEvent event) {}

	/**
	 * When the popup menu becomes invisible. 
	 * @param event The event that occured
	 */
	public void popupMenuWillBecomeInvisible(PopupMenuEvent event) {}

	/**
	 * When the popup menu is canceled. 
	 * @param event The event that occured
	 */
	public void popupMenuCanceled(PopupMenuEvent event) {
	
		synchronized( this ) {
			// wake up application thread - user has cancelled the menu 
			// by clicking outside the menu
			// (this is executed as a callback in the Swing thread)
			notifyAll();
		}
	}
}