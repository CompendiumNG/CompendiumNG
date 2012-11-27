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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;

import com.compendium.LanguageProperties;
import com.compendium.ProjectCompendium;
import com.compendium.core.ICoreConstants;
import com.compendium.core.datamodel.NodeSummary;
import com.compendium.core.datamodel.View;
import com.compendium.ui.UIViewUnread;


/**
 * This class draws and handles events for the right-cick menu for nodes in a unread view
 * @author Lakshmi Prabhakaran
 *
 */
public class UIViewUnreadPopupMenu extends JPopupMenu implements ActionListener {

	/** The serial version id */
	private static final long serialVersionUID 			= 758151357124084935L;

	/** The default width for this popup menu.*/
	private static final int WIDTH						= 100;
	
	/** The default height for this popup menu.*/
	private static final int HEIGHT						= 300;
	
	/** The  JMenuItem to mark the node as read*/
	private JMenuItem		miMenuItemMarkSeen 			= null;

	/**The  JMenuItem to mark the node as unread*/
	private JMenuItem		miMenuItemMarkUnseen 		= null;
	
	/** The  JMenuItem to mark the whole view as read*/
	private JMenuItem		miMenuItemMarkViewSeen 		= null;

	/**The  JMenuItem to mark the whole view as unread*/
	private JMenuItem		miMenuItemMarkViewUnseen 	= null;
	
	/** The NodeSummary object associated with this popup menu.*/
	private NodeSummary			oNode					= null;
	
	/** The UIViewUnread object associated with this popup menu.*/
	private UIViewUnread		unreadView				= null;
		
	/**
	 * Constructor. Create the menus and items and draws the popup menu.
	 * @param title, the title for this popup menu.
	 * @param node com.compendium.core.datamodel.NodeSummary, the associated node for this popup menu.
	 */
	public UIViewUnreadPopupMenu(String title, NodeSummary node, UIViewUnread unread) {
		super(title);

		setNode(node);
		unreadView = unread;
		
		int nType = getNode().getType();
		
		miMenuItemMarkSeen = new JMenuItem(LanguageProperties.getString(LanguageProperties.POPUPS_BUNDLE, "UIBasePopupMenu.markSeen")); //$NON-NLS-1$
		miMenuItemMarkSeen.setAccelerator(KeyStroke.getKeyStroke( KeyEvent.VK_F12, 0));
		miMenuItemMarkSeen.addActionListener(this);
		add(miMenuItemMarkSeen);				
		
		miMenuItemMarkUnseen = new JMenuItem(LanguageProperties.getString(LanguageProperties.POPUPS_BUNDLE, "UIBasePopupMenu.markUnseen")); //$NON-NLS-1$
		miMenuItemMarkUnseen.setAccelerator(KeyStroke.getKeyStroke(  KeyEvent.VK_F12, 1));
		miMenuItemMarkUnseen.addActionListener(this);
		add(miMenuItemMarkUnseen);		
		
		if(node.equals(ProjectCompendium.APP.getHomeView())){
			miMenuItemMarkUnseen.setEnabled(false);
		}
		// if node is in read state enable mark unseen and disable mark seen and vice versa
		int state = getNode().getState();
		
		if(state == ICoreConstants.READSTATE){
			miMenuItemMarkSeen.setEnabled(false);
		} else if(state == ICoreConstants.UNREADSTATE) {
			miMenuItemMarkUnseen.setEnabled(false);
		}
		
		
		if (View.isViewType(nType) || View.isShortcutViewType(nType) ) {
			addSeparator();
			miMenuItemMarkViewSeen = new JMenuItem(LanguageProperties.getString(LanguageProperties.POPUPS_BUNDLE, "UIBasePopupMenu.markSeenAll")); //$NON-NLS-1$
			miMenuItemMarkViewSeen.addActionListener(this);
			add(miMenuItemMarkViewSeen);				
			
			miMenuItemMarkViewUnseen = new JMenuItem(LanguageProperties.getString(LanguageProperties.POPUPS_BUNDLE, "UIBasePopupMenu.makrUnseenAll")); //$NON-NLS-1$
			miMenuItemMarkViewUnseen.addActionListener(this);
			add(miMenuItemMarkViewUnseen);
		}
		pack();
		setSize(WIDTH,HEIGHT);
		
	}

	/**
	 * @param node The Node to set.
	 */
	public void setNode(NodeSummary node) {
		oNode = node;
	}
	/**
	 * @return Returns the Node.
	 */
	public NodeSummary getNode() {
		return oNode;
	}
	
	/**
	* Handles the event of an option being selected.
	 * @param evt, the event associated with the option being selected.
	 */
	public void actionPerformed(ActionEvent evt) {
		Object source = evt.getSource();
		ProjectCompendium.APP.setWaitCursor();
		
		if(source.equals(miMenuItemMarkSeen)) {
			unreadView.onMarkSeenUnseen(oNode, ICoreConstants.READSTATE);
		} else if(source.equals(miMenuItemMarkUnseen)) {
			unreadView.onMarkSeenUnseen(oNode, ICoreConstants.UNREADSTATE);
		} else if(source.equals(miMenuItemMarkViewSeen)) {
			unreadView.onMarkAll(oNode, ICoreConstants.READSTATE);
		} else if(source.equals(miMenuItemMarkViewUnseen)) {
			unreadView.onMarkAll(oNode, ICoreConstants.UNREADSTATE);
		}
		ProjectCompendium.APP.setDefaultCursor();
		onCancel();
	}
	
	/**
	 * Handle the canceling of this popup. Set is to invisible.
	 */
	public void onCancel() {
		setVisible(false);
	}
}
