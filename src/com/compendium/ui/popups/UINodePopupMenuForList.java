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

import java.awt.event.*;
import javax.swing.*;

import com.compendium.core.CoreUtilities;
import com.compendium.core.ICoreConstants;
import com.compendium.core.datamodel.*;

import com.compendium.LanguageProperties;
import com.compendium.ProjectCompendium;
import com.compendium.ui.*;
import com.compendium.ui.plaf.*;
import com.compendium.ui.dialogs.UIReadersDialog;
import com.compendium.ui.dialogs.UISendMailDialog;

/**
 * This class draws and handles events for the right-click menu for nodes in a list.
 *
 * @author	Mohammed Sajid Ali / Michelle Bachler / Lakshmi Prabhakaran
 */
public class UINodePopupMenuForList extends UIBaseListPopupMenu implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** A separator that can be turned off if required by simple menu.*/
	private JSeparator		separator1				= null;

	/** A separator that can be turned off if required by simple menu.*/
	private JSeparator		separator2				= null;

	/** The NodeSummary object associated with this popup menu.*/
	private NodeSummary		oNode					= null;

	/** The NodePosition object associated with this popup menu.*/
	private NodePosition	oNodePosition			= null;


	/**
	 * Constructor. 
	 * @param title the title for this popup menu.
	 */
	public UINodePopupMenuForList(String title) {
		super(title);
	}

	/**
	 * Constructor. Create the menus and items and draws the popup menu.
	 * @param title the title for this popup menu.
	 * @param listui the associated list for this popup menu.
	 * @param nodePos the associated node position for this popup menu.
	 */
	public UINodePopupMenuForList(String title, ListUI listui, NodePosition nodePos) {
		super(title);
		this.oNodePosition = nodePos;
		this.oNode = this.oNodePosition.getNode();
		setList(listui);
		init(); 
	}
	
	protected void init() {
		boolean bSimple = FormatProperties.simpleInterface;

		addContents();
		addSeparator();

		String sSource = oNode.getSource();
		if (!sSource.startsWith(ICoreConstants.sINTERNAL_REFERENCE)) {
			addInternalReference();			
			addSendToInbox();
			addSeparator();
		}
		
		int nType = oNode.getType();
		if (View.isViewType(nType) || 
				View.isShortcutViewType(nType)) {

			View view = (View)oNode;
			try {view.initializeMembers();}
			catch(Exception ex) {}

			addReferences(view.getReferenceNodes());
		}

		addCopy(shortcutKey);
		addCut(shortcutKey);
		addDelete(shortcutKey);
		
		addSeparator();

		addImportMenu();
		addExportMenu();
		
		addSeparator();
		
		addGoogleSearch();
		
		addJabberAndIXPanelMenus();

		addShortcut();
		addClone();
		
		addSeparator();

		addBookmark();
		
		separator1 = new JPopupMenu.Separator();
		add(separator1);
		
		addClaiMakerMenu();
		
		addReaders();
		
		addSeenUnseen();
	
		int state = oNode.getState();
		if(state == ICoreConstants.READSTATE){				
			showMarkUnseen = true;
		} else if(state == ICoreConstants.UNREADSTATE) {
			showMarkSeen = true;
		} else {
			showMarkUnseen = true;
			showMarkSeen = true;
		}

		separator2 = new JPopupMenu.Separator();
		add(separator2);
		
		addProperties();
		addViews();
		
		/**
		 * If on the Mac OS and the Menu bar is at the top of the OS screen, remove the menu shortcut Mnemonics.
		 */
		if (ProjectCompendium.isMac && (FormatProperties.macMenuBar || (!FormatProperties.macMenuBar && !FormatProperties.macMenuUnderline)) )
			UIUtilities.removeMenuMnemonics(getSubElements());

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
			miImportXMLFlashmeeting.setVisible(false);
			miFileImport.setVisible(false);
			miImportCurrentView.setVisible(false);
			miImportMultipleViews.setVisible(false);
			miFavorites.setVisible(false);
			miMenuItemReaders.setVisible(false);
			miMenuItemMarkSeen.setVisible(false);
			miMenuItemMarkUnseen.setVisible(false);		
			miMenuItemProperties.setVisible(false);		
			separator1.setVisible(false);		
			separator2.setVisible(false);		
		} else {
			miImportXMLFlashmeeting.setVisible(true);
			miFileImport.setVisible(true);
			miImportCurrentView.setVisible(true);
			miImportMultipleViews.setVisible(true);
			miFavorites.setVisible(true);
			miMenuItemReaders.setVisible(true);
			if (showMarkSeen) {
				miMenuItemMarkSeen.setVisible(true);
			}
			if (showMarkUnseen) {
				miMenuItemMarkUnseen.setVisible(true);	
			}
			miMenuItemProperties.setVisible(true);				
			separator1.setVisible(true);		
			separator2.setVisible(true);		
		}
		
		setControlItemStatus(bSimple);
		
		if (isVisible()) {
			setVisible(false);
			setVisible(true);
			requestFocus();
		}
	}

	/**
	 * Set the node associated with this popup menu.
	 * @param node com.compendium.core.datamodel.NodeSummary, the node associated with this popup menu.
	 */
	public void setNode(NodeSummary node) {
		oNode = node;
	}

	/**
	 * Handles the event of an option being selected.
	 * @param evt, the event associated with the option being selected.
	 */
	public void actionPerformed(ActionEvent evt) {
		super.actionPerformed(evt);
	}
	
	/**
	 * Search Google using this node's label.
	 */
	protected void searchGoogle() {
		String sLabel = oNode.getLabel();
		try {
			sLabel = CoreUtilities.cleanURLText(sLabel);
		} catch (Exception e) {}
		ExecuteControl.launch( "http://www.google.com/search?hl=en&lr=&ie=UTF-8&oe=UTF-8&q="+sLabel ); //$NON-NLS-1$
	}	
	
	/**
	 * Search ClaiMaker concepts using this node's label.
	 */
	protected void searchClaiMakerConcepts() {
		String sLabel = oNode.getLabel();
		try {
			sLabel = CoreUtilities.cleanURLText(sLabel);
		} catch (Exception e) {}
		ExecuteControl.launch( claiMakerServer+"search-concept.php?op=search&inputWord="+sLabel ); //$NON-NLS-1$
	}

	/**
	 * Search ClaiMaker neighbourhood using this node's label.
	 */
	protected void searchClaiMakerNeighbourhood() {
		String sLabel = oNode.getLabel();
		try {
			sLabel = CoreUtilities.cleanURLText(sLabel);
		} catch (Exception e) {}
		ExecuteControl.launch( claiMakerServer+"discover/neighborhood.php?op=search&concept="+sLabel ); //$NON-NLS-1$
	}

	/**
	 * Search ClaiMaker documents using this node's label.
	 */
	protected void searchClaiMakerDocuments() {
		String sLabel = oNode.getLabel();
		try {
			sLabel = CoreUtilities.cleanURLText(sLabel);
		} catch (Exception e) {}
		ExecuteControl.launch( claiMakerServer+"search-document.php?op=search&Title="+sLabel ); //$NON-NLS-1$
	}
	
	/**
	 * Display a list of all users who have read this node.
	 */
	protected void displayReaders() {
		//Lakshmi (4/19/06) - code added to display Readers list Dialog
		String nodeId = oNode.getId();
		UIReadersDialog readers = new UIReadersDialog(ProjectCompendium.APP, nodeId);
		UIUtilities.centerComponent(readers, ProjectCompendium.APP);
		readers.setVisible(true);			
	}
		
	/**
	 * Open the contents dialog for the given context.
	 */
	protected void openContents() {
		if (oNode instanceof View) {
			UIViewFrame oUIViewFrame = ProjectCompendium.APP.addViewToDesktop((View)oNode, oNode.getLabel());
			oUIViewFrame.setNavigationHistory(oUIList.getViewFrame().getChildNavigationHistory());
		} else {
			oUIList.showEditDialog(oNodePosition);
		}
	}

	/**
	 * Open the contents dialog for the given context on the properties tab.
	 * Subclasses must implement this method.
	 */
	protected void openProperties() {
		oUIList.showPropertiesDialog(oNodePosition);
	}

	/**
	 * Open the contents dialog for the given context on the views tab.
	 * Subclasses must implement this method.
	 */
	protected void openViews() {
		oUIList.showViewsDialog(oNodePosition);
	}
	
	/**
	 * Create a recipient dialog box so the user can choose multiple people to send the INR to
	*/
	protected void sendToInbox() {
			
		View oInBoxView = ProjectCompendium.APP.getInBoxView();
		if (oInBoxView.getId().equals(oUIList.getView().getId())) {
			JOptionPane.showMessageDialog(this, 
					LanguageProperties.getString(LanguageProperties.POPUPS_BUNDLE, "UIBasePopupMenu.inBoxError"), //$NON-NLS-1$
					LanguageProperties.getString(LanguageProperties.POPUPS_BUNDLE, "UIBasePopupMenu.inBoxErrorTitle"), //$NON-NLS-1$
					JOptionPane.INFORMATION_MESSAGE);
			return;
		} 
		
		UISendMailDialog dlg = new UISendMailDialog(ProjectCompendium.APP, 
				oUIList.getView(), oNode);
		UIUtilities.centerComponent(dlg, ProjectCompendium.APP);
		dlg.setVisible(true);
	}	
	
	/**
	 * Create a Reference node with internal link to this node.
	 */
	protected void createInternalLink() {

		View view = oUIList.getView();				
		String sRef = ICoreConstants.sINTERNAL_REFERENCE+view.getId()+"/"+oNode.getId(); //$NON-NLS-1$
		String sAuthor = ProjectCompendium.APP.getModel().getUserProfile().getUserName();

		// Do all calculations at 100% scale and then scale back down if required.
		if (oUIList != null) {
			try {			
				// CREATE NEW NODE RIGHT OF THE GIVEN NODE WITH THE GIVEN LABEL				
				NodePosition nodePos = oListUI.createNode(ICoreConstants.REFERENCE,
								 "", //$NON-NLS-1$
								 ProjectCompendium.APP.getModel().getUserProfile().getUserName(),
								 LanguageProperties.getString(LanguageProperties.POPUPS_BUNDLE, "UIBasePopupMenu.goto")+": "+oNode.getLabel(), //$NON-NLS-1$ //$NON-NLS-2$
								 LanguageProperties.getString(LanguageProperties.POPUPS_BUNDLE, "UIBasePopupMenu.inview")+": "+view.getLabel(), //$NON-NLS-1$ //$NON-NLS-2$
								 0, ((view.getNodeCount()+1)*10)
								 );
				
				nodePos.getNode().setSource(sRef, "", sAuthor); //$NON-NLS-1$
				
				oUIList.updateTable();
			} catch (Exception e) {
				ProjectCompendium.APP.displayError(LanguageProperties.getString(LanguageProperties.POPUPS_BUNDLE, "UIBasePopupMenu.errorMessageIternalLink")+"\n\n"+e.getLocalizedMessage());							 //$NON-NLS-1$
			}				
		}
	}		
	
	/**
	 * Create a Reference node with internal link to this node.
	 */
	protected void createBookmark() {
		View view = oUIList.getView();				
		ProjectCompendium.APP.createFavorite(oNode.getId(), view.getId(), view.getLabel()+"&&&"+oNode.getLabel(), oNode.getType()); //$NON-NLS-1$
		oUIList.getList().requestFocus();
	}	
}
