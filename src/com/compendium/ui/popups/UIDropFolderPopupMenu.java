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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import com.compendium.LanguageProperties;

/**
 * Popup menu for when user drops a folder into the map. Simulates modal behaviour 
 * on show().
 * 
 * @author rudolf
 */
public class UIDropFolderPopupMenu extends JPopupMenu implements ActionListener, PopupMenuListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3426019571556495552L;

	/**
	* Specifies which action can be done when dropping a file
	*/
	public enum FolderDropAction { 
		/**
		 * create a map node 
		 */
		MAP, 
		/**
		 * create a map node and recursive subdirectories
		 */
		MAPRECURSIVE, 
		/**
		 * create a reference
		 */
		LINK, 
		/**
		 * abort
		 */
		CANCEL }; 
	
	/**
	 * holds what the user selects.
	 */
	public FolderDropAction selection = FolderDropAction.CANCEL;
	
	/**
	 * The entry to select a map node
	 */
	private JMenuItem miMap = null;
	/**
	 * The entry to select a recursive add with map nodes.
	 */
	private JMenuItem miMapRecursive = null;
	/**
	 * The entry to create a reference.
	 */
	private JMenuItem miLink = null;
	/**
	 * The entry to abort.
	 */
	private JMenuItem miCancel = null;
	/**
	 * Holds the partent frame. 
	 */
	private JFrame oAppframe = null;


	/**
	 * Constructor.
	 * @param appframe the main application frame to bring to front when 
	 * 	menu is shown
	 */
	public UIDropFolderPopupMenu( JFrame appframe ) {

		oAppframe = appframe;
		
		miLink = new JMenuItem(LanguageProperties.getString(LanguageProperties.POPUPS_BUNDLE, "UIDropFolderPopupMenu.dropAsLink")); //$NON-NLS-1$
		miLink.addActionListener(this);
		add(miLink);

		miMap = new JMenuItem(LanguageProperties.getString(LanguageProperties.POPUPS_BUNDLE, "UIDropFolderPopupMenu.dropAsMapNodes")); //$NON-NLS-1$
		miMap.addActionListener(this);
		add(miMap);

		miMapRecursive = new JMenuItem(LanguageProperties.getString(LanguageProperties.POPUPS_BUNDLE, "UIDropFolderPopupMenu.dropAsMapNodesDepth")); //$NON-NLS-1$
		miMapRecursive.addActionListener(this);
		add(miMapRecursive);

		miCancel = new JMenuItem(LanguageProperties.getString(LanguageProperties.POPUPS_BUNDLE, "UIDropFolderPopupMenu.cancel")); //$NON-NLS-1$
		miCancel.addActionListener(this);
		add(miCancel);
		
		this.addPopupMenuListener(this);
	}

	/** 
	 * Show this popup at the current mouse position 
	 * @param invoker the component on which to show the popup
	 */
	public void show( Component invoker ) {
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
			System.out.println("UIDropFolderPopup.show: Interrupted."); //$NON-NLS-1$
		}
		finally {
			oAppframe.setAlwaysOnTop(false);
		}
	}

	/**
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {

		if (e.getSource() == miLink) selection = FolderDropAction.LINK;
		if (e.getSource() == miMap) selection = FolderDropAction.MAP;
		if (e.getSource() == miMapRecursive) selection = FolderDropAction.MAPRECURSIVE;
		synchronized( this ) {
			// wake up application thread - user has made his choice
			// (this is executed as a callback in the Swing thread)
			notifyAll();
		}
	}

	/** 
	 * {@inheritDoc} 
	 */
	public void popupMenuWillBecomeVisible( PopupMenuEvent event ) {}
	
	/** 
	 * {@inheritDoc} 
	 */
	public void popupMenuWillBecomeInvisible ( PopupMenuEvent event ) {}
	
	/** 
	 * {@inheritDoc} 
	 */
	public void popupMenuCanceled( PopupMenuEvent event ) {

		selection = FolderDropAction.CANCEL;
		synchronized( this ) {
			// wake up application thread - user has cancelled the menu 
			// by clicking outside the menu
			// (this is executed as a callback in the Swing thread)
			notifyAll();
		}
	}
}
