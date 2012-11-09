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



package com.compendium.ui.popups;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;

import com.compendium.ProjectCompendium;
import com.compendium.core.ICoreConstants;
import com.compendium.core.datamodel.NodeSummary;
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
	
	/** The x value for the location of this popup menu.*/
	private int					nX						= 0;

	/** The y value for the location of this popup menu.*/
	private int					nY						= 0;
	
	/** The UIViewUnread object associated with this popup menu.*/
	private UIViewUnread		unreadView				= null;
	
	/** The platform specific shortcut key used to access menus and thier options.*/
	private int 			shortcutKey                  = 0;
	
	/**
	 * Constructor. Create the menus and items and draws the popup menu.
	 * @param title, the title for this popup menu.
	 * @param node com.compendium.core.datamodel.NodeSummary, the associated node for this popup menu.
	 */
	public UIViewUnreadPopupMenu(String title, NodeSummary node, UIViewUnread unread) {
		super(title);

		shortcutKey = ProjectCompendium.APP.shortcutKey;
		setNode(node);
		unreadView = unread;
		
		int nType = getNode().getType();
		
		miMenuItemMarkSeen = new JMenuItem("Mark Seen");
		miMenuItemMarkSeen.setAccelerator(KeyStroke.getKeyStroke( KeyEvent.VK_F12, 0));
		miMenuItemMarkSeen.addActionListener(this);
		miMenuItemMarkSeen.setMnemonic(KeyEvent.VK_M);
		add(miMenuItemMarkSeen);				
		
		miMenuItemMarkUnseen = new JMenuItem("Mark Unseen");
		miMenuItemMarkUnseen.setAccelerator(KeyStroke.getKeyStroke(  KeyEvent.VK_F12, 1));
		miMenuItemMarkUnseen.addActionListener(this);
		miMenuItemMarkUnseen.setMnemonic(KeyEvent.VK_N);
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
		
		
		if (nType == ICoreConstants.MAPVIEW || nType == ICoreConstants.MAP_SHORTCUT ||
				nType == ICoreConstants.LISTVIEW || nType == ICoreConstants.LIST_SHORTCUT ) {
			addSeparator();
			miMenuItemMarkViewSeen = new JMenuItem("Mark Seen All");
			miMenuItemMarkViewSeen.addActionListener(this);
			miMenuItemMarkViewSeen.setMnemonic(KeyEvent.VK_S);
			add(miMenuItemMarkViewSeen);				
			
			miMenuItemMarkViewUnseen = new JMenuItem("Mark Unseen All");
			miMenuItemMarkViewUnseen.addActionListener(this);
			miMenuItemMarkViewUnseen.setMnemonic(KeyEvent.VK_U);
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
	 * Set the location to draw this popup menu at.
	 * @param x, the x position of this popup's location.
	 * @param y, the y position of this popup's location.
	 */
	public void setCoordinates(int x,int y) {
		nX = x;
		nY = y;
	}
	
	/**
	* Handles the event of an option being selected.
	 * @param evt, the event associated with the option being selected.
	 */
	public void actionPerformed(ActionEvent evt) {
		Object source = evt.getSource();
		String nodeID = oNode.getId();
		String homeID = ProjectCompendium.APP.getHomeView().getId();
		
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
