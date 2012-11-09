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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.NoSuchElementException;
import java.util.Vector;

import javax.swing.DefaultListModel;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.border.EmptyBorder;

import com.compendium.ProjectCompendium;
import com.compendium.core.ICoreConstants;
import com.compendium.core.datamodel.Code;
import com.compendium.core.datamodel.ModelSessionException;
import com.compendium.core.datamodel.NodeSummary;
import com.compendium.core.datamodel.View;
import com.compendium.ui.IUIConstants;
import com.compendium.ui.UIImages;
import com.compendium.ui.UIUtilities;
import com.compendium.ui.UIViewOutline;
import com.compendium.ui.dialogs.UINodeContentDialog;

/**
 * This class draws and handles events for the right-cick menu for nodes in a outline view
 * @author Lakshmi Prabhakaran
 *
 */
public class UIViewOutlinePopupMenu extends JPopupMenu implements ActionListener {

	/** The serial version id */
	private static final long serialVersionUID 			= -4851797575525807200L;

	/** The default width for this popup menu.*/
	private static final int WIDTH						= 100;
	
	/** The default height for this popup menu.*/
	private static final int HEIGHT						= 300;
	
	/** The  JMenuItem to mark the node as read. */
	private JMenuItem		miMenuItemMarkSeen 			= null;

	/**The  JMenuItem to mark the node as unread. */
	private JMenuItem		miMenuItemMarkUnseen 		= null;
	
	/** The  JMenuItem to mark the whole view as read. */
	private JMenuItem		miMenuItemMarkViewSeen 		= null;

	/**The  JMenuItem to mark the whole view as unread. */
	private JMenuItem		miMenuItemMarkViewUnseen 	= null;
	
	/** The JMenuItem to open this node's contents dialog.*/
	private JMenuItem		miMenuItemOpen				= null;
	
	/** The JMenuItem to open this node's contents dialog.*/
	private JMenuItem		miMenuItemReference 		= null;

	/** The JMenuItem to perform a copy operation.*/
	private JMenuItem		miMenuItemCopy				= null;

	/** The JMenuItem to perform a cut operation.*/
	private JMenuItem		miMenuItemCut				= null;
	
	/** The JMenu for node type change options.*/
	private JMenu			mnuChangeType				= null;

	/** The JMenuItem to change the selected nodes to Argument nodes.*/
	private JMenuItem		miTypeArgument				= null;

	/** The JMenuItem to change the selected nodes to Con nodes.*/
	private JMenuItem		miTypeCon					= null;

	/** The JMenuItem to change the selected nodes to Issue nodes.*/
	private JMenuItem		miTypeIssue					= null;

	/** The JMenuItem to change the selected nodes to Position nodes.*/
	private JMenuItem		miTypePosition				= null;

	/** The JMenuItem to change the selected nodes to Pro nodes.*/
	private JMenuItem		miTypePro					= null;

	/** The JMenuItem to change the selected nodes to Decision nodes.*/
	private JMenuItem		miTypeDecision				= null;

	/** The JMenuItem to change the selected nodes to Note nodes.*/
	private JMenuItem		miTypeNote					= null;

	/** The JMenuItem to change the selected nodes to Refrence nodes.*/
	private JMenuItem		miTypeReference				= null;

	/** The JMenuItem to change the selected nodes to List nodes.*/
	private JMenuItem		miTypeList					= null;

	/** The JMenuItem to change the selected nodes to Map nodes.*/
	private JMenuItem		miTypeMap					= null;
	
	/**The  JMenuItem to delete the node. */
	private JMenuItem		miMenuItemDelete 			= null;
	
	/**The  JMenuItem to paste the node. */
	private JMenuItem		miMenuItemPaste 			= null;
	
	/** The JMenu to list the associated nodes parent views.*/
	private JMenu				mnuViews 			= null;
	
	/** The JMenu to list the associated nodes tags.*/
	private JMenu				mnuTags 			= null;

	/** The JMenuItem to open the associated nodes properties dialog.*/
	private JMenuItem		miMenuItemProperties		= null;
	
	/** The NodeSummary object associated with this popup menu.*/
	private NodeSummary			oNode					= null;
	
	/** The view object associated with this NodeSummary .*/
	//private View				oView					= null;
	
	/** The x value for the location of this popup menu.*/
	private int					nX						= 0;

	/** The y value for the location of this popup menu.*/
	private int					nY						= 0;

	/** The outline view object. */
	private UIViewOutline			outline 			= null;

	/** The platform specific shortcut key used to access menus and thier options.*/
	private int shortcutKey;
	
	/**
	 * Constructor. Create the menus and items and draws the popup menu.
	 * @param title, the title for this popup menu.
	 * @param node com.compendium.core.datamodel.NodeSummary, the associated node for this popup menu.
	 */
	public UIViewOutlinePopupMenu(String title, NodeSummary node, UIViewOutline outlineView, boolean isLevelOneNode) {
		super(title);

		setNode(node);
		setOutline(outlineView);
		
		int nType = getNode().getType();
		shortcutKey = ProjectCompendium.APP.shortcutKey;
		
		mnuTags = new JMenu("Tags");
		mnuTags.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				
				ProjectCompendium.APP.setWaitCursor();
				ProjectCompendium.APP.getMenuManager().addTagsView(true);
				try {
					Vector views = oNode.getMultipleViews();
					View selectedView = (View)outline.getSelectedView();
					if(!selectedView.equals(oNode)){
						UIUtilities.jumpToNode(selectedView.getId(), oNode.getId(), "Outline- View");
					} else {
						selectedView = (View) views.get(0); //get any view and focus the node
						UIUtilities.jumpToNode(selectedView.getId(), oNode.getId(), "Outline- View");
					}
				} catch (SQLException e1) {
					e1.printStackTrace();
				} catch (ModelSessionException e1) {
					e1.printStackTrace();
				}
				e.consume();
				onCancel();
				ProjectCompendium.APP.setDefaultCursor();
			}
		});
		
		mnuTags.setMnemonic(KeyEvent.VK_T);
		Enumeration codes = oNode.getCodes();
		
		if (codes != null && codes.hasMoreElements()){
			for (; codes.hasMoreElements();) {
				final Code code = (Code) codes.nextElement();
				final JCheckBoxMenuItem list = new JCheckBoxMenuItem(code.getName());
				list.setSelected(true);
				list.setToolTipText("Clicking will remove " +code.getName() + " tag for the node " +oNode.getLabel());
				list.addActionListener(new ActionListener() {

					public void actionPerformed(ActionEvent arg0) {
						list.setSelected(false);
						try {
							oNode.removeCode(code);
						} catch (NoSuchElementException e) {
							e.printStackTrace();
						} catch (SQLException e) {
							e.printStackTrace();
						} catch (ModelSessionException e) {
							e.printStackTrace();
						}
						
					}
					
				});
				 mnuTags.add(list);
			}
				
		}
		add(mnuTags);
		
		miMenuItemOpen = new JMenuItem("Contents");
		miMenuItemOpen.setMnemonic(KeyEvent.VK_O);
		miMenuItemOpen.addActionListener(this);
		add(miMenuItemOpen);
		
		mnuChangeType = new JMenu("Change Type To ...");
		mnuChangeType.setMnemonic(KeyEvent.VK_Y);
		mnuChangeType.addActionListener(this);
		add(mnuChangeType);

		miTypeIssue = new JMenuItem("Question"); // issue renamed to question
		miTypeIssue.addActionListener(this);
		miTypeIssue.setMnemonic(KeyEvent.VK_Q);
		mnuChangeType.add(miTypeIssue);

		miTypePosition = new JMenuItem("Answer"); //position renamed to answer
		miTypePosition.addActionListener(this);
		miTypePosition.setMnemonic(KeyEvent.VK_A);
		mnuChangeType.add(miTypePosition);

		miTypeMap = new JMenuItem("Map");
		miTypeMap.addActionListener(this);
		miTypeMap.setMnemonic(KeyEvent.VK_M);
		mnuChangeType.add(miTypeMap);

		miTypeList = new JMenuItem("List");
		miTypeList.addActionListener(this);
		miTypeList.setMnemonic(KeyEvent.VK_L);
		mnuChangeType.add(miTypeList);

		miTypePro = new JMenuItem("Pro");
		miTypePro.addActionListener(this);
		miTypePro.setMnemonic(KeyEvent.VK_P);
		mnuChangeType.add(miTypePro);

		miTypeCon = new JMenuItem("Con");
		miTypeCon.addActionListener(this);
		miTypeCon.setMnemonic(KeyEvent.VK_C);
		mnuChangeType.add(miTypeCon);

		miTypeReference = new JMenuItem("Reference");
		miTypeReference.addActionListener(this);
		miTypeReference.setMnemonic(KeyEvent.VK_R);
		mnuChangeType.add(miTypeReference);

		miTypeNote = new JMenuItem("Note");
		miTypeNote.addActionListener(this);
		miTypeNote.setMnemonic(KeyEvent.VK_N);
		mnuChangeType.add(miTypeNote);

		miTypeDecision = new JMenuItem("Decision");
		miTypeDecision.addActionListener(this);
		miTypeDecision.setMnemonic(KeyEvent.VK_D);
		mnuChangeType.add(miTypeDecision);

		//mnuChangeType.addSeparator();
		miTypeArgument = new JMenuItem("Argument");
		miTypeArgument.addActionListener(this);
		miTypeArgument.setMnemonic(KeyEvent.VK_U);
		mnuChangeType.add(miTypeArgument);
		
		addSeparator();
		
		miMenuItemCopy = new JMenuItem("Copy", UIImages.get(IUIConstants.COPY_ICON));
		miMenuItemCopy.setAccelerator(KeyStroke.getKeyStroke( KeyEvent.VK_C, shortcutKey));
		miMenuItemCopy.setMnemonic(KeyEvent.VK_C);
		miMenuItemCopy.addActionListener(this);
		miMenuItemCopy.setEnabled(true);
		add(miMenuItemCopy);

		miMenuItemCut = new JMenuItem("Cut", UIImages.get(IUIConstants.CUT_ICON));
		miMenuItemCut.setAccelerator(KeyStroke.getKeyStroke( KeyEvent.VK_X, shortcutKey));
		miMenuItemCut.setMnemonic(KeyEvent.VK_U);
		miMenuItemCut.addActionListener(this);
		miMenuItemCut.setEnabled(true);
		add(miMenuItemCut);
		
		miMenuItemPaste = new JMenuItem("Paste", UIImages.get(IUIConstants.PASTE_ICON));
		miMenuItemPaste.setAccelerator(KeyStroke.getKeyStroke( KeyEvent.VK_V, shortcutKey));
		miMenuItemPaste.setMnemonic(KeyEvent.VK_P);
		miMenuItemPaste.addActionListener(this);
		if (oNode  instanceof View){
			miMenuItemPaste.setEnabled(false);
			if(ProjectCompendium.APP.isPasteEnabled)
				miMenuItemPaste.setEnabled(true);
			add(miMenuItemPaste);
		}
		

		miMenuItemDelete = new JMenuItem("Delete", UIImages.get(IUIConstants.DELETE_ICON));
		miMenuItemDelete.setAccelerator(KeyStroke.getKeyStroke( KeyEvent.VK_DELETE, 0));
		miMenuItemDelete.addActionListener(this);
		miMenuItemDelete.setMnemonic(KeyEvent.VK_D);
		add(miMenuItemDelete);

		addSeparator();
		
		miMenuItemMarkSeen = new JMenuItem("Mark Seen");
		miMenuItemMarkSeen.setAccelerator(KeyStroke.getKeyStroke( KeyEvent.VK_F12, 0));
		miMenuItemMarkSeen.addActionListener(this);
		miMenuItemMarkSeen.setMnemonic(KeyEvent.VK_M);
		add(miMenuItemMarkSeen);				
		
		miMenuItemMarkUnseen = new JMenuItem("Mark Unseen");
		miMenuItemMarkUnseen.setAccelerator(KeyStroke.getKeyStroke( KeyEvent.VK_F12, 1));
		miMenuItemMarkUnseen.addActionListener(this);
		miMenuItemMarkUnseen.setMnemonic(KeyEvent.VK_N);
		add(miMenuItemMarkUnseen);		
		// if node is in read state enable mark unseen and disable mark seen and vice versa
		int state = getNode().getState();
		
		if(state == ICoreConstants.READSTATE){
			miMenuItemMarkSeen.setEnabled(false);
		} else if(state == ICoreConstants.UNREADSTATE) {
			miMenuItemMarkUnseen.setEnabled(false);
		}
		
		addSeparator();
		if (nType == ICoreConstants.MAPVIEW || nType == ICoreConstants.MAP_SHORTCUT ||
				nType == ICoreConstants.LISTVIEW || nType == ICoreConstants.LIST_SHORTCUT ) {
			
			miMenuItemMarkViewSeen = new JMenuItem("Mark Seen All");
			miMenuItemMarkViewSeen.addActionListener(this);
			miMenuItemMarkViewSeen.setMnemonic(KeyEvent.VK_S);
			add(miMenuItemMarkViewSeen);				
			
			miMenuItemMarkViewUnseen = new JMenuItem("Mark Unseen All");
			miMenuItemMarkViewUnseen.addActionListener(this);
			miMenuItemMarkViewUnseen.setMnemonic(KeyEvent.VK_U);
			add(miMenuItemMarkViewUnseen);
			
			addSeparator();
		}
		
		miMenuItemProperties = new JMenuItem("Properties");
		miMenuItemProperties.addActionListener(this);
		miMenuItemProperties.setMnemonic(KeyEvent.VK_P);
		add(miMenuItemProperties);

		mnuViews = new JMenu("Views");
		mnuViews.addActionListener(this);
		mnuViews.setMnemonic(KeyEvent.VK_V);
		
		try {
			Vector views = node.getMultipleViews();
			if (views != null && views.size() > 0){
				for (int i = 0; i < views.size(); i++) {
					final View view = (View) views.get(i);
					final String nodeId = node.getId();
					JMenuItem item = new JMenuItem(view.getLabel());
					item.setToolTipText(view.getLabel());				
					item.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							ProjectCompendium.APP.setWaitCursor();
							UIUtilities.jumpToNode(view.getId(),nodeId , "Outline view" );
							ProjectCompendium.APP.setDefaultCursor();
						}
					});
					mnuViews.add(item);
				}
				add(mnuViews);
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
		} catch (ModelSessionException e1) {
			e1.printStackTrace();
		}
		
		if(nType == ICoreConstants.REFERENCE || nType == ICoreConstants.REFERENCE_SHORTCUT){
			String path = node.getSource();

			if (path != null || !path.equals("")) {
				addSeparator();
				miMenuItemReference = new JMenuItem("Open Reference");
				miMenuItemReference.addActionListener(this);
				miMenuItemReference.setMnemonic(KeyEvent.VK_R);
				add(miMenuItemReference);
			}
		}
		if(isLevelOneNode){
			miMenuItemCut.setEnabled(false);
		}
		
		if(node.equals(ProjectCompendium.APP.getHomeView())){
			miMenuItemCopy.setEnabled(false);
			miMenuItemCut.setEnabled(false);
			miMenuItemDelete.setEnabled(false);
			miMenuItemMarkUnseen.setEnabled(false);
			mnuChangeType.setEnabled(false);
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
	 * @return Returns the outline.
	 */
	public UIViewOutline getOutline() {
		return outline;
	}

	/**
	 * @param outline The outline to set.
	 */
	public void setOutline(UIViewOutline outline) {
		this.outline = outline;
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

		ProjectCompendium.APP.setWaitCursor();
		if(source.equals(miMenuItemDelete)) {
			outline.onDelete();
		} else if(source.equals(miMenuItemCopy)) {
			outline.onCopy();
		} else if(source.equals(miMenuItemCut)) {
			outline.onCut();
		} else if(source.equals(miMenuItemPaste)) {
			outline.onPaste();
		} else if(source.equals(miMenuItemOpen)) {
			// open the node contents
			outline.openContents(oNode, UINodeContentDialog.CONTENTS_TAB);
		} else if(source.equals(miMenuItemReference)) {
			// open the Reference
			outline.openReference(oNode, null);
		} else if(source.equals(miMenuItemMarkSeen)) {
			outline.onMarkSeenUnseen(getNode(), ICoreConstants.READSTATE);
		} else if(source.equals(miMenuItemMarkUnseen)) {
			outline.onMarkSeenUnseen(getNode(), ICoreConstants.UNREADSTATE);
		} else if(source.equals(miMenuItemMarkViewSeen)) {
			outline.onMarkAll((View)getNode(), ICoreConstants.READSTATE);
		} else if(source.equals(miMenuItemMarkViewUnseen)) {
			outline.onMarkAll((View)getNode(), ICoreConstants.UNREADSTATE);
		} else if(source.equals(miMenuItemProperties)) {
			// show its properties
			outline.openContents(oNode, UINodeContentDialog.PROPERTIES_TAB);
		} else if(source.equals(miTypeIssue)) {
			onChangeType(ICoreConstants.ISSUE);
		} else if(source.equals(miTypePosition)) {
			onChangeType(ICoreConstants.POSITION);
		} else if(source.equals(miTypeMap)) {
			onChangeType(ICoreConstants.MAPVIEW);
		} else if(source.equals(miTypeList)) {
			onChangeType(ICoreConstants.LISTVIEW);
			
		} else if(source.equals(miTypePro)) {
			onChangeType(ICoreConstants.PRO);
		} else if(source.equals(miTypeCon)) {
			onChangeType(ICoreConstants.CON);
		} else if(source.equals(miTypeArgument)) {
			onChangeType(ICoreConstants.ARGUMENT);
		} else if(source.equals(miTypeDecision)) {
			onChangeType(ICoreConstants.DECISION);
		} else if(source.equals(miTypeNote)) {
			onChangeType(ICoreConstants.NOTE);
		} else if(source.equals(miTypeReference)) {
			onChangeType(ICoreConstants.REFERENCE);
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

	/**
	 * Change the selected node to the given node type.
	 */
	private void onChangeType(int nNewType) {
		 int nOldType = oNode.getType();
			if (nOldType == nNewType)
				return ;

		    boolean changeType = true;

		    if ( (nOldType == ICoreConstants.LISTVIEW || nOldType == ICoreConstants.MAPVIEW)
			 	&& (nNewType != ICoreConstants.LISTVIEW && nNewType != ICoreConstants.MAPVIEW) ) {

				int response = JOptionPane.showConfirmDialog(ProjectCompendium.APP, "WARNING! Nodes inside Maps/Lists will be deleted.\n" +
						"If they are not transcluded in another Map/List, they will be placed in the trashbin.\n\n" +
						"Are you sure you still want to continue?",
						"Change Type - "+oNode.getLabel(), JOptionPane.YES_NO_OPTION);
				if (response == JOptionPane.NO_OPTION || response == JOptionPane.CLOSED_OPTION) {
			    	return ;
				}
		}
		try {
			oNode.setType(nNewType, outline.getAuthor());
		} catch (Exception e) {
			//e.printStackTrace();
			System.out.println(" unable to change node type.");
		}
		
	}
	
}
