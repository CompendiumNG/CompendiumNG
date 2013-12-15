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

import com.compendium.ProjectCompendium;
import com.compendium.core.ICoreConstants;
import com.compendium.ui.IUIConstants;
import com.compendium.ui.UIImages;
import com.compendium.ui.UINode;
import com.compendium.ui.UINodeTypeManager;
import com.compendium.ui.UIUtilities;
import com.compendium.ui.UIViewPane;

public class UINodeLinkingPopupMenu extends JPopupMenu implements ActionListener {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2049135719763796185L;

	/** The default width for this popup menu.*/
	private static final int WIDTH					= 100;

	/** The default height for this popup menu.*/
	private static final int HEIGHT					= 300;

	/**
	 * holds node node type the user selects
	 */
    private int selection = ICoreConstants.POSITION;
	
	/** The JMenuItem to create an Argument node.*/
	private JMenuItem		miMenuItemArgument		= null;

	/** The JMenuItem to create an Con node.*/
	private JMenuItem		miMenuItemCon			= null;

	/** The JMenuItem to create an Issue node.*/
	private JMenuItem		miMenuItemIssue			= null;

	/** The JMenuItem to create an Position node.*/
	private JMenuItem		miMenuItemPosition		= null;

	/** The JMenuItem to create an Pro node.*/
	private JMenuItem		miMenuItemPro			= null;

	/** The JMenuItem to create an Decision node.*/
	private JMenuItem		miMenuItemDecision		= null;

	/** The JMenuItem to create an Note node.*/
	private JMenuItem		miMenuItemNote			= null;

	/** The JMenuItem to create an Reference node.*/
	private JMenuItem		miMenuItemReference		= null;

	/** The JMenuItem to create an List node.*/
	private JMenuItem		miMenuItemList			= null;

	/** The JMenuItem to create an Map node.*/
	private JMenuItem		miMenuItemMap			= null;

	/** The JMenuItem to create a Time View Map node.*/
	private JMenuItem		miMenuItemTimeMap	= null;

	private int 			oDirection				= -1;
	
	/**
	 * Holds the parent frame. 
	 */
	private JFrame oAppframe = null;

	private UIViewPane		oViewPane = null;
	
	private UINode			oNode = null;

	/**
	 * Constructor.
	 * @param appframe the main application frame to bring to front when 
	 * 	menu is shown
	 */
	public UINodeLinkingPopupMenu( JFrame appframe, int direction, UIViewPane pane, UINode node ) {
		oNode = node;
		oViewPane = pane;
		oDirection = direction;
		
		oAppframe = appframe;
		
		miMenuItemIssue = new JMenuItem(UINodeTypeManager.convertNoteTypeToString(ICoreConstants.ISSUE), UIImages.getNodeIcon(IUIConstants.ISSUE_SM_ICON)); // issue renamed to question
		miMenuItemIssue.addActionListener(this);
		miMenuItemIssue.setMnemonic(UINodeTypeManager.getMnemonicForNodeType(ICoreConstants.ISSUE));
		add(miMenuItemIssue);
		
		miMenuItemPosition = new JMenuItem(UINodeTypeManager.convertNoteTypeToString(ICoreConstants.POSITION), UIImages.getNodeIcon(IUIConstants.POSITION_SM_ICON)); //position renamed to answer
		miMenuItemPosition.addActionListener(this);
		miMenuItemPosition.setMnemonic(UINodeTypeManager.getMnemonicForNodeType(ICoreConstants.POSITION));
		add(miMenuItemPosition);
		
		addSeparator();

		miMenuItemMap = new JMenuItem(UINodeTypeManager.convertNoteTypeToString(ICoreConstants.MAPVIEW), UIImages.getNodeIcon(IUIConstants.MAP_SM_ICON));
		miMenuItemMap.addActionListener(this);
		miMenuItemMap.setMnemonic(UINodeTypeManager.getMnemonicForNodeType(ICoreConstants.MAPVIEW));
		add(miMenuItemMap);

		miMenuItemList = new JMenuItem(UINodeTypeManager.convertNoteTypeToString(ICoreConstants.LISTVIEW), UIImages.getNodeIcon(IUIConstants.LIST_SM_ICON));
		miMenuItemList.addActionListener(this);
		miMenuItemList.setMnemonic(UINodeTypeManager.getMnemonicForNodeType(ICoreConstants.LISTVIEW));
		add(miMenuItemList);
		addSeparator();
				
		miMenuItemPro = new JMenuItem(UINodeTypeManager.convertNoteTypeToString(ICoreConstants.PRO), UIImages.getNodeIcon(IUIConstants.PRO_SM_ICON));
		miMenuItemPro.addActionListener(this);
		miMenuItemPro.setMnemonic(UINodeTypeManager.getMnemonicForNodeType(ICoreConstants.PRO));
		add(miMenuItemPro);
		
		miMenuItemCon = new JMenuItem(UINodeTypeManager.convertNoteTypeToString(ICoreConstants.CON), UIImages.getNodeIcon(IUIConstants.CON_SM_ICON));
		miMenuItemCon.addActionListener(this);
		miMenuItemCon.setMnemonic(UINodeTypeManager.getMnemonicForNodeType(ICoreConstants.CON));
		add(miMenuItemCon);
		
		addSeparator();

		miMenuItemReference = new JMenuItem(UINodeTypeManager.convertNoteTypeToString(ICoreConstants.REFERENCE), UIImages.getNodeIcon(IUIConstants.REFERENCE_SM_ICON));
		miMenuItemReference.addActionListener(this);
		miMenuItemReference.setMnemonic(UINodeTypeManager.getMnemonicForNodeType(ICoreConstants.REFERENCE));
		add(miMenuItemReference);

		miMenuItemNote = new JMenuItem(UINodeTypeManager.convertNoteTypeToString(ICoreConstants.NOTE), UIImages.getNodeIcon(IUIConstants.NOTE_SM_ICON));
		miMenuItemNote.addActionListener(this);
		miMenuItemNote.setMnemonic(UINodeTypeManager.getMnemonicForNodeType(ICoreConstants.NOTE));
		add(miMenuItemNote);
		
		miMenuItemDecision = new JMenuItem(UINodeTypeManager.convertNoteTypeToString(ICoreConstants.DECISION), UIImages.getNodeIcon(IUIConstants.DECISION_SM_ICON));
		miMenuItemDecision.addActionListener(this);
		miMenuItemDecision.setMnemonic(UINodeTypeManager.getMnemonicForNodeType(ICoreConstants.DECISION));
		add(miMenuItemDecision);
		
		miMenuItemArgument = new JMenuItem(UINodeTypeManager.convertNoteTypeToString(ICoreConstants.ARGUMENT), UIImages.getNodeIcon(IUIConstants.ARGUMENT_SM_ICON));
		miMenuItemArgument.addActionListener(this);
		miMenuItemArgument.setMnemonic(UINodeTypeManager.getMnemonicForNodeType(ICoreConstants.ARGUMENT));
		add(miMenuItemArgument);
		
		pack();
		setSize(WIDTH,HEIGHT);
	}

	/** 
	 * Show this popup at the current mouse position 
	 * @param invoker the component on which to show the popup
	 */
	public void show( Component invoker ) {
		int x = MouseInfo.getPointerInfo().getLocation().x - invoker.getLocationOnScreen().x;
		int y = MouseInfo.getPointerInfo().getLocation().y - invoker.getLocationOnScreen().y;

		oAppframe.setAlwaysOnTop(true);
		show( invoker, x, y );
		repaint(); // without this, menu sometimes gets drawn incompletely
		oAppframe.setAlwaysOnTop(false);
	}

	/**
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {

		Object source = e.getSource();

		if(source.equals(miMenuItemArgument)) {
			selection = ICoreConstants.ARGUMENT;
		}
		else if(source.equals(miMenuItemCon)) {
			selection = ICoreConstants.CON;
		}
		else if(source.equals(miMenuItemIssue)) {
			selection = ICoreConstants.ISSUE;
		}
		else if(source.equals(miMenuItemPosition)) {
			selection = ICoreConstants.POSITION;
		}
		else if(source.equals(miMenuItemPro))	{
			selection = ICoreConstants.PRO;
		}
		else if(source.equals(miMenuItemDecision)) {
			selection = ICoreConstants.DECISION;
		}
		else if(source.equals(miMenuItemNote)) {
			selection = ICoreConstants.NOTE;
		}
		else if(source.equals(miMenuItemReference)) {
			selection = ICoreConstants.REFERENCE;
		}
		else if(source.equals(miMenuItemList)) {
			selection = ICoreConstants.LISTVIEW;
		}
		else if(source.equals(miMenuItemMap)) {
			selection = ICoreConstants.MAPVIEW;
		}

		if (oDirection == UIUtilities.DIRECTION_RIGHT) {
			UINode node = UIUtilities.createNodeAndLinkRight(oNode, selection, 100, "", ProjectCompendium.APP.getModel().getUserProfile().getUserName()); //$NON-NLS-1$
			oViewPane.setSelectedNode(node, ICoreConstants.SINGLESELECT);
			node.getUI().setEditing();				
		} else if (oDirection == UIUtilities.DIRECTION_LEFT) {
			UINode node = UIUtilities.createNodeAndLinkLeft(oNode, selection, 100, "", ProjectCompendium.APP.getModel().getUserProfile().getUserName()); //$NON-NLS-1$
			oViewPane.setSelectedNode(node, ICoreConstants.SINGLESELECT);
			node.getUI().setEditing();	
		} else if (oDirection == UIUtilities.DIRECTION_UP) {
			UINode node = UIUtilities.createNodeAndLinkUp(oNode, selection, 100, "", ProjectCompendium.APP.getModel().getUserProfile().getUserName()); //$NON-NLS-1$
			oViewPane.setSelectedNode(node, ICoreConstants.SINGLESELECT);
			node.getUI().setEditing();	
		} else if (oDirection == UIUtilities.DIRECTION_DOWN) {
			UINode node = UIUtilities.createNodeAndLinkDown(oNode, selection, 100, "", ProjectCompendium.APP.getModel().getUserProfile().getUserName()); //$NON-NLS-1$
			oViewPane.setSelectedNode(node, ICoreConstants.SINGLESELECT);
			node.getUI().setEditing();	
		}
		
		this.setVisible(false);
	}
}
