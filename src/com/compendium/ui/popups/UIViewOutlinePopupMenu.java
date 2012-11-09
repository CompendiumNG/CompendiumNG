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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.Date;
import java.util.Enumeration;
import java.util.NoSuchElementException;
import java.util.Vector;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;

import com.compendium.LanguageProperties;
import com.compendium.ProjectCompendium;
import com.compendium.core.ICoreConstants;
import com.compendium.core.datamodel.Code;
import com.compendium.core.datamodel.IModel;
import com.compendium.core.datamodel.ModelSessionException;
import com.compendium.core.datamodel.NodeSummary;
import com.compendium.core.datamodel.PCSession;
import com.compendium.core.datamodel.View;
import com.compendium.ui.UIMapViewFrame;
import com.compendium.ui.UINode;
import com.compendium.ui.UIUtilities;
import com.compendium.ui.UIViewOutline;
import com.compendium.ui.dialogs.UINodeContentDialog;
import com.compendium.ui.stencils.DraggableStencilIcon;
import com.compendium.ui.FormatProperties;

/**
 * This class draws and handles events for the right-click menu for nodes in a outline view
 * 
 * @author Lakshmi Prabhakaran / Michelle Bachler
 */
public class UIViewOutlinePopupMenu extends UIBasePopupMenu implements ActionListener {

	/** The serial version id */
	private static final long serialVersionUID 			= -4851797575525807200L;
		
	/** The  JMenuItem to mark the whole view as read. */
	protected JMenuItem		miMenuItemMarkViewSeen 		= null;

	/**The  JMenuItem to mark the whole view as unread. */
	protected JMenuItem		miMenuItemMarkViewUnseen 	= null;
	
	/** The JMenuItem to open this node's contents dialog.*/
	protected JMenuItem		miMenuItemReference 		= null;
	
	/** The JMenu to list the associated nodes parent views.*/
	protected JMenu			mnuViews 					= null;
	
	/** The JMenu to list the associated nodes tags.*/
	protected JMenu			mnuTags 					= null;
	
	/** The NodeSummary object associated with this popup menu.*/
	protected NodeSummary		oNode					= null;
	
	/** The outline view object. */
	protected UIViewOutline		outline 				= null;

	/** A separator that can be turned off if required by simple menu.*/
	protected JSeparator		separator1				= null;

	protected boolean			isLevelOneNode			= false;
	
	/**
	 * Constructor. Create the menus and items and draws the popup menu.
	 * @param title, the title for this popup menu.
	 * @param node com.compendium.core.datamodel.NodeSummary, the associated node for this popup menu.
	 */
	public UIViewOutlinePopupMenu(String title, NodeSummary node, UIViewOutline outlineView, boolean isLevelOneNode) {
		super(title);
		this.isLevelOneNode = isLevelOneNode;
		setNode(node);
		setOutline(outlineView);
		init();
	}
	
	protected void init() {
		boolean bSimple = FormatProperties.simpleInterface;		

		mnuTags = new JMenu(LanguageProperties.getString(LanguageProperties.POPUPS_BUNDLE, "UIViewOutlinePopupMenu.tags")); //$NON-NLS-1$
		mnuTags.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				
				ProjectCompendium.APP.setWaitCursor();
				ProjectCompendium.APP.getMenuManager().addTagsView(true);
				try {
					Vector views = oNode.getMultipleViews();
					View selectedView = (View)outline.getSelectedView();
					if(!selectedView.equals(oNode)){
						UIUtilities.jumpToNode(selectedView.getId(), oNode.getId(), LanguageProperties.getString(LanguageProperties.POPUPS_BUNDLE, "UIViewOutlinePopupMenu.outlineView")); //$NON-NLS-1$
					} else {
						selectedView = (View) views.get(0); //get any view and focus the node
						UIUtilities.jumpToNode(selectedView.getId(), oNode.getId(), LanguageProperties.getString(LanguageProperties.POPUPS_BUNDLE, "UIViewOutlinePopupMenu.outlineView")); //$NON-NLS-1$
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
		
		Enumeration codes = oNode.getCodes();
		
		if (codes != null && codes.hasMoreElements()){
			for (; codes.hasMoreElements();) {
				final Code code = (Code) codes.nextElement();
				final JCheckBoxMenuItem list = new JCheckBoxMenuItem(code.getName());
				list.setSelected(true);
				list.setToolTipText(LanguageProperties.getString(LanguageProperties.POPUPS_BUNDLE, "UIViewOutlinePopupMenu.messageRemoveA") +code.getName() +" "+ LanguageProperties.getString(LanguageProperties.POPUPS_BUNDLE, "UIViewOutlinePopupMenu.messageRemoveB") +oNode.getLabel()); //$NON-NLS-1$ //$NON-NLS-2$
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
		
		addContents();
				
		createNodeTypeChangeMenu();
				
		addSeparator();
		
		this.addCopy(shortcutKey);
		this.addCut(shortcutKey);	
		if(isLevelOneNode){
			miMenuItemCut.setEnabled(false);
		}

		if (oNode instanceof View){
			addPaste(shortcutKey);
			miMenuItemPaste.setEnabled(false);
			if(ProjectCompendium.APP.isPasteEnabled)
				miMenuItemPaste.setEnabled(true);
		}		
		addDelete(shortcutKey);

		separator1 = new JPopupMenu.Separator();
		add(separator1);

		addSeenUnseen();
		
		// if node is in read state enable mark unseen and disable mark seen and vice versa
		int state = getNode().getState();	
		if(state == ICoreConstants.READSTATE){				
			showMarkUnseen = true;
		} else if(state == ICoreConstants.UNREADSTATE) {
			showMarkSeen = true;
		} else {
			showMarkUnseen = true;
			showMarkSeen = true;
		}

		addSeparator();
		
		int nType = getNode().getType();		
		if (View.isViewType(nType) || View.isShortcutViewType(nType)) {		
			miMenuItemMarkViewSeen = new JMenuItem(LanguageProperties.getString(LanguageProperties.POPUPS_BUNDLE, "UIBasePopupMenu.markSeenAll")); //$NON-NLS-1$
			miMenuItemMarkViewSeen.addActionListener(this);
			add(miMenuItemMarkViewSeen);				
					
			miMenuItemMarkViewUnseen = new JMenuItem(LanguageProperties.getString(LanguageProperties.POPUPS_BUNDLE, "UIBasePopupMenu.markUnseenAll")); //$NON-NLS-1$
			miMenuItemMarkViewUnseen.addActionListener(this);
			add(miMenuItemMarkViewUnseen);
				
			addSeparator();		
		}
		
		addProperties();		
		
		mnuViews = new JMenu(LanguageProperties.getString(LanguageProperties.POPUPS_BUNDLE, "UIViewOutlinePopupMenu.views")); //$NON-NLS-1$
		mnuViews.addActionListener(this);
		
		try {
			Vector views = oNode.getMultipleViews();
			if (views != null && views.size() > 0){
				for (int i = 0; i < views.size(); i++) {
					final View view = (View) views.get(i);
					final String nodeId = oNode.getId();
					JMenuItem item = new JMenuItem(view.getLabel());
					item.setToolTipText(view.getLabel());				
					item.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							ProjectCompendium.APP.setWaitCursor();
							UIUtilities.jumpToNode(view.getId(),nodeId , LanguageProperties.getString(LanguageProperties.POPUPS_BUNDLE, "UIViewOutlinePopupMenu.outlineView") ); //$NON-NLS-1$
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
			String path = oNode.getSource();

			if (path != null || !path.equals("")) { //$NON-NLS-1$
				addSeparator();
				miMenuItemReference = new JMenuItem(LanguageProperties.getString(LanguageProperties.POPUPS_BUNDLE, "UIViewOutlinePopupMenu.openReference")); //$NON-NLS-1$
				miMenuItemReference.addActionListener(this);
				add(miMenuItemReference);
			}
		}
						
		if (bSimple) {		
			addExtenderButton();
			setDisplay(bSimple);
		}

		pack();
		setSize(WIDTH,HEIGHT);		
	}

	/**
	 * Hide/show items depending on whether the user wants the simple view or simple.
	 * @param bSimple
	 */
	protected void setDisplay(boolean bSimple) {
		if (bSimple) {
			separator1.setVisible(false);
			miMenuItemMarkSeen.setVisible(false);
			miMenuItemMarkUnseen.setVisible(false);	
			if (miMenuItemMarkViewSeen != null) {
				miMenuItemMarkViewSeen.setVisible(false);
			}
			if (miMenuItemMarkViewUnseen != null) {
				miMenuItemMarkViewUnseen.setVisible(false);
			}
			if (miMenuItemProperties != null) {
				miMenuItemProperties.setVisible(false);	
			}
		} else {
			if (showMarkSeen) {
				miMenuItemMarkSeen.setVisible(true);
			}
			if (showMarkUnseen) {
				miMenuItemMarkUnseen.setVisible(true);	
			}
			if (miMenuItemMarkViewSeen != null) {
				miMenuItemMarkViewSeen.setVisible(true);
			}
			if (miMenuItemMarkViewUnseen != null) {
				miMenuItemMarkViewUnseen.setVisible(true);
			}
			if (showMarkUnseen || showMarkSeen) {
				separator1.setVisible(true);
			}		
			if (miMenuItemProperties != null) {
				miMenuItemProperties.setVisible(true);	
			}
		}
		
		if(oNode.equals(ProjectCompendium.APP.getHomeView())){
			miMenuItemCopy.setEnabled(false);
			miMenuItemCut.setEnabled(false);
			miMenuItemDelete.setEnabled(false);
			if (miMenuItemMarkUnseen !=  null) {
				miMenuItemMarkUnseen.setEnabled(false);
			}
			mnuChangeType.setEnabled(false);
		}
		
		setControlItemStatus(bSimple);
		
		if (isVisible()) {
			setVisible(false);
			setVisible(true);
			requestFocus();
		}
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
	* Handles the event of an option being selected.
	 * @param evt, the event associated with the option being selected.
	 */
	public void actionPerformed(ActionEvent evt) {
		Object source = evt.getSource();

		ProjectCompendium.APP.setWaitCursor();
		if(source.equals(miMenuItemReference)) {
			outline.openReference(oNode, null);
		} else if(source.equals(miMenuItemMarkViewSeen)) {
			outline.onMarkAll((View)getNode(), ICoreConstants.READSTATE);
		} else if(source.equals(miMenuItemMarkViewUnseen)) {
			outline.onMarkAll((View)getNode(), ICoreConstants.UNREADSTATE);
		}
		
		ProjectCompendium.APP.setDefaultCursor();
		onCancel();
	}
	
	
	/**
	 * Mark the currently selected Nodes as seen.
	 */
	protected void markSeen() {
		outline.onMarkSeenUnseen(getNode(), ICoreConstants.READSTATE);	}

	/**
	 * Mark the currently selected nodes as unseen.
	 */
	protected void markUnseen() {
		outline.onMarkSeenUnseen(getNode(), ICoreConstants.UNREADSTATE);
	}

	/**
	 * Open the contents dialog for the given context on the Contents tab.
	 */
	protected void openContents() {
		outline.openContents(oNode, UINodeContentDialog.CONTENTS_TAB);
	}
	
	/**
	 * Open the contents dialog for the given context on the properties tab.
	 */
	protected void openProperties() {
		outline.openContents(oNode, UINodeContentDialog.PROPERTIES_TAB);
	}

	/**
	 * Cut the selected node(s)
	 */
	protected void cut() {
		outline.onCut();
	}
	
 	/**
	 * Copy the selected node(s)
	 */
	protected void copy() {
		outline.onCopy();
	}
	
	/**
	 * Paste the selected node(s)
	 */
	protected void paste() {
		outline.onPaste();
	}

	/**
	 * Delete the currently selected nodes.
	 */
	protected void delete() {
		outline.onDelete();
	}

	/**
	 * Change the type of the selected nodes to the given stencil item
	 * Subclasses should implement this method.
	 */
	protected  void changeStencilType(DraggableStencilIcon item) {
		String sImage = item.getImage();
		String sBackgroundImage = item.getBackgroundImage();
		String sTemplate = item.getTemplate();
		String sLabel = item.getLabel();
		
		int nType = item.getNodeType();
		Vector vtTags = item.getTags();
		String sAuthor=	outline.getAuthor();
			
		boolean changeType = false;
		if (oNode.getType() != nType) {
			try {
				oNode.setType(nType, outline.getAuthor());
				changeType = true;
			} catch (Exception e) {
				//e.printStackTrace();
				System.out.println(" unable to change node type."); //$NON-NLS-1$
			}		
		} else {
			changeType = true;
		}
		
		if (changeType) {			
			try {
				// ADD LABEL IF NODE HAS NO LABEL
				if (oNode.getLabel().equals("")) {
					oNode.setLabel(sLabel, sAuthor);
				}
				
				// ADD REFERENCE IMAGE
				oNode.setSource(oNode.getSource(), sImage, sAuthor); //$NON-NLS-1$
			}
			catch(Exception ex) {
				System.out.println("error in UIViewOutlinePopupMenu.createNodeFromStencil) \n\n"+ex.getMessage()); //$NON-NLS-1$
			}			
			
			// ADD THE TAGS
			IModel oModel = ProjectCompendium.APP.getModel();
			PCSession oSession = oModel.getSession();

			NodeSummary nodeSum = oNode;
			int count = vtTags.size();
			for(int i=0; i<count;i++) {
				Vector data = (Vector)vtTags.elementAt(i);
				String sID = (String)data.elementAt(0);
				String sName = (String)data.elementAt(1);
				String sTheAuthor = (String)data.elementAt(2);
				String sDescription = (String)data.elementAt(3);
				String sBehavior = (String)data.elementAt(4);
				Date dCreated = (Date)data.elementAt(5);
				Date dLastModified = (Date)data.elementAt(6);
				Code codeObj = null;
				try {
					// CHECK IF ALREADY IN DATABASE
					Vector existingCodesForName = (oModel.getCodeService()).getCodeIDs(oSession, sName);
					if (existingCodesForName.size() == 0) {
						codeObj = oModel.getCodeService().createCode(oSession, sID, sTheAuthor, dCreated,
																 dLastModified, sName, sDescription, sBehavior);
						oModel.addCode(codeObj);
					}
					else {
						String existingCodeID = (String)existingCodesForName.elementAt(0);
						codeObj = oModel.getCodeService().getCode(oSession, existingCodeID);
					}
					nodeSum.addCode(codeObj);
				}
				catch(Exception ex) { System.out.println("Unable to add tag = "+codeObj.getName()+"\n\ndue to:\n\n"+ex.getMessage()); } //$NON-NLS-1$ //$NON-NLS-2$
			}
			
			// ADD BACKGROUND IMAGE AND TEMPLATE IF REQUIRED
			if (View.isViewType(oNode.getType())) {
				View view  = (View)oNode;			
				if (sBackgroundImage != null && !sBackgroundImage.equals("")) { //$NON-NLS-1$
					try {
						view.setBackgroundImage(sBackgroundImage);
						view.updateViewLayer();
					}
					catch(Exception ex) {
						System.out.println("error in UIViewPane.createNodeFromStencil) \n\n"+ex.getMessage()); //$NON-NLS-1$
					}
				} 
				if (sTemplate != null && !sTemplate.equals("")) {				 //$NON-NLS-1$
					UIMapViewFrame mapFrame = null;
					try {
						view.initializeMembers();					
						mapFrame = new UIMapViewFrame(view, view.getLabel());
					}
					catch(Exception ex) {
						ex.printStackTrace();
					}				
					if (mapFrame != null) {
						ProjectCompendium.APP.onTemplateImport(sTemplate, mapFrame.getViewPane());
					}
				}		
			}	
		} 
	}
	
	/**
	 * Change the selected node to the given node type.
	 */
	protected void changeType(int nNewType) {
		 int nOldType = oNode.getType();
			if (nOldType == nNewType)
				return;

		    if ( (nOldType == ICoreConstants.LISTVIEW || nOldType == ICoreConstants.MAPVIEW)
			 	&& (nNewType != ICoreConstants.LISTVIEW && nNewType != ICoreConstants.MAPVIEW) ) {

				int response = JOptionPane.showConfirmDialog(ProjectCompendium.APP, LanguageProperties.getString(LanguageProperties.POPUPS_BUNDLE, "UIViewOutlinePopupMenu.warningMessageA") + "\n"+//$NON-NLS-1$
						LanguageProperties.getString(LanguageProperties.POPUPS_BUNDLE, "UIViewOutlinePopupMenu.warningMessageB")+"\n\n" + //$NON-NLS-1$
						LanguageProperties.getString(LanguageProperties.POPUPS_BUNDLE, "UIViewOutlinePopupMenu.warningMessageC"), //$NON-NLS-1$
						LanguageProperties.getString(LanguageProperties.POPUPS_BUNDLE, "UIViewOutlinePopupMenu.warningMessageTitle")+oNode.getLabel(), JOptionPane.YES_NO_OPTION); //$NON-NLS-1$
				if (response == JOptionPane.NO_OPTION || response == JOptionPane.CLOSED_OPTION) {
			    	return;
				}
		}
		try {
			oNode.setType(nNewType, outline.getAuthor());
		} catch (Exception e) {
			//e.printStackTrace();
			System.out.println(" unable to change node type."); //$NON-NLS-1$
		}		
	}
}
