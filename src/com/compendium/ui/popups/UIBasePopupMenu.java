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

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;

import com.compendium.core.*;
import com.compendium.core.datamodel.*;

import com.compendium.ui.*;
import com.compendium.ui.menus.UIControllerMenuItem;
import com.compendium.ui.plaf.*;
import com.compendium.ui.dialogs.*;
import com.compendium.ui.stencils.*;
import com.compendium.LanguageProperties;
import com.compendium.ProjectCompendium;

/**
 * This class has generic building blocks for the right-click menu options for 
 * list, map and node right-click menus
 *
 * @author	Michelle Bachler
 */
public abstract class UIBasePopupMenu extends JPopupMenu implements ActionListener {

	/** The default width for this popup menu.*/
	protected static final int WIDTH			= 100;

	/** The default height for this popup menu.*/
	protected static final int HEIGHT			= 300;
			
	/** The JMenu item for the Nodes creation option.*/
	protected JMenu			mnuNodes			= null;	
		
	/** The JMenu which holds the import options.*/
	protected JMenu			mnuImport			= null;

	/** The JMenu which holds the import options.*/
	protected JMenu			miFileImport		= null;

	/** The JMenu which holds the export options.*/
	protected JMenu			mnuExport			= null;

	/** The JMenuItem to import Questmap into the current View.*/
	protected JMenuItem		miImportCurrentView		= null;

	/** The JMenuItem to import Questmap into multiple Views.*/
	protected JMenuItem		miImportMultipleViews	= null;

	/** The JMenuItem to export to a HTML Outline.*/
	protected JMenuItem		miExportHTMLOutline		= null;

	/** The JMenuItem to export to a HTML Views.*/
	protected JMenuItem		miExportHTMLView		= null;

	/** The JMenuItem to export to XML.*/
	protected JMenuItem		miExportXMLView			= null;
	
	/** The menu item to export a HTML view with the XML included.*/
	protected JMenuItem		miExportHTMLViewXML		= null;	

	/** The JMenuItem to import XML.*/
	protected JMenuItem		miImportXMLView			= null;
	
	/** The menu item to import Flashmeeting XML.*/
	protected JMenuItem		miImportXMLFlashmeeting = null;
		
	/** The JMenu to send information to IX Panels.*/
	protected JMenu			mnuSendToIX			= null;

	/** The JMenu to send information to a Jabber client.*/
	protected JMenu			mnuSendToJabber		= null;


	/** The JMenuItem to perform a paste operation.*/
	protected JMenuItem		miMenuItemPaste			= null;

	/** The JMenuItem to perform a copy operation.*/
	protected JMenuItem		miMenuItemCopy			= null;

	/** The JMenuItem to perform a cut operation.*/
	protected JMenuItem		miMenuItemCut			= null;
	
	/** The JMenuItem to delete the currently selected nodes.*/
	protected JMenuItem		miMenuItemDelete		= null;


	/** The  JMenuItem to mark the nodes as read*/ 
	protected JMenuItem		miMenuItemMarkSeen 		= null;

	/**The  JMenuItem to mark the nodes as unread*/
	protected JMenuItem		miMenuItemMarkUnseen 	= null;

	
	/** The JMenuItem to open this view's contents dialog.*/
	protected JMenuItem		miMenuItemOpen			= null;

	/** The JMenuItem to view the properties details for this view.*/
	protected JMenuItem		miMenuItemProperties	= null;

	/** The JMenuItem to view the parent Views for this view.*/
	protected JMenuItem		miMenuItemViews			= null;


	/** The JMenuItem to create an Argument node.*/
	protected JMenuItem		miMenuItemArgument		= null;

	/** The JMenuItem to create an Con node.*/
	protected JMenuItem		miMenuItemCon			= null;

	/** The JMenuItem to create an Issue node.*/
	protected JMenuItem		miMenuItemIssue			= null;

	/** The JMenuItem to create an Position node.*/
	protected JMenuItem		miMenuItemPosition		= null;

	/** The JMenuItem to create an Pro node.*/
	protected JMenuItem		miMenuItemPro			= null;

	/** The JMenuItem to create an Decision node.*/
	protected JMenuItem		miMenuItemDecision		= null;

	/** The JMenuItem to create an Note node.*/
	protected JMenuItem		miMenuItemNote			= null;

	/** The JMenuItem to create an Reference node.*/
	protected JMenuItem		miMenuItemReference		= null;

	/** The JMenuItem to create an List node.*/
	protected JMenuItem		miMenuItemList			= null;

	/** The JMenuItem to create an Map node.*/
	protected JMenuItem		miMenuItemMap			= null;

	/** The JMenuItem to create an Movie Map node.*/
	protected JMenuItem		miMenuItemMovieMap		= null;

	
	/** The stencil menu*/
	protected JMenu			mnuStencils				= null;

	/** The menu item to open the stencil  management dialog.*/
	protected JMenuItem		miStencilManagement		= null;

	
	/** The JMenuItem to move the selected nodes' detail text into their labels.*/
	protected JMenuItem		miMoveDetail			= null;

	/** The JMenuItem to move the selected nodes' label text into their detail.*/
	protected JMenuItem		miMoveLabel				= null;

	/** The JMenu item that holds CaliMaker search options.*/
	protected JMenu			mnuClaiMaker			= null;

	/** The JMenuItem to opena browser window and run a ClaiMaker concept search for the current node's labale text.*/
	protected JMenuItem		miClaiConcepts			= null;

	/** The JMenuItem to opena browser window and run a ClaiMaker neightbourhood search for the current node's labale text.*/
	protected JMenuItem		miClaiNeighbourhood		= null;

	/** The JMenuItem to opena browser window and run a ClaiMaker document search for the current node's labale text.*/
	protected JMenuItem		miClaiDocuments			= null;

	/** Open a broswer window and run a search on Goolge for the current node's label text.*/
	protected JMenuItem		miGoogleSearch			= null;
	
	/** The JMenuItem to create a shortcut of the currently selected node, or the node associated with this popup.*/
	protected JMenuItem		miMenuItemShortCut		= null;

	/** The JMenuItem to create a clone of the currently selected nodes, or the node associated with this popup.*/
	protected JMenuItem		miMenuItemClone			= null;

	/** The menu item to create an internal reference node to this node.*/
	protected JMenuItem		miInternalReference		= null;

	/** The menu to send node to user's in boxes.*/
	protected JMenuItem		miToInBox				= null;

	/** The JMenuItem to add the node associated with this popup to the favorites list.*/
	protected JMenuItem		miFavorites				= null;
		
	/** The JMenuItem to display list of previous readers  */
	protected JMenuItem		miMenuItemReaders 		= null;
		
	/** The JMenu for node type change options.*/
	protected JMenu			mnuChangeType		= null;
	
	/** The JMenuItem to change the selected nodes to Argument nodes.*/
	protected JMenuItem		miTypeArgument		= null;

	/** The JMenuItem to change the selected nodes to Con nodes.*/
	protected JMenuItem		miTypeCon			= null;

	/** The JMenuItem to change the selected nodes to Issue nodes.*/
	protected JMenuItem		miTypeIssue			= null;

	/** The JMenuItem to change the selected nodes to Position nodes.*/
	protected JMenuItem		miTypePosition		= null;

	/** The JMenuItem to change the selected nodes to Pro nodes.*/
	protected JMenuItem		miTypePro			= null;

	/** The JMenuItem to change the selected nodes to Decision nodes.*/
	protected JMenuItem		miTypeDecision		= null;

	/** The JMenuItem to change the selected nodes to Note nodes.*/
	protected JMenuItem		miTypeNote			= null;

	/** The JMenuItem to change the selected nodes to Reference nodes.*/
	protected JMenuItem		miTypeReference		= null;

	/** The JMenuItem to change the selected nodes to List nodes.*/
	protected JMenuItem		miTypeList			= null;

	/** The JMenuItem to change the selected nodes to Map nodes.*/
	protected JMenuItem		miTypeMap			= null;

	/** The JMenuItem to change the selected nodes to Movie Map nodes.*/
	protected JMenuItem		miTypeMovieMap			= null;


	/** The x value for the location of this popup menu.*/
	protected int			nX						= 0;

	/** The y value for the location of this popup menu.*/
	protected int			nY						= 0;

	/** Should the Mark Seen menu item be displayed?*/
	protected boolean		showMarkSeen			= false;
	
	/** Should the Mark Unseen menu item be displayed?*/
	protected boolean		showMarkUnseen			= false;
	
	/** The JMenu to holds links to Reference nodes contained in the current view.*/
	protected JMenu			mnuRefNodes				= null;
			

	/** The base url string to sun claimaker searches.*/
	protected String 		claiMakerServer 			= ""; //$NON-NLS-1$

	/** The item with the extender arrow.*/
	protected UIControllerMenuItem oExtender		= null;

	/** The platform specific shortcut key used to access menus and their options.*/
	protected int shortcutKey;

	/**
	 * Constructor. 
	 * @param title the title for this popup menu.
	 */
	public UIBasePopupMenu(String title) {
		super(title);
		shortcutKey = ProjectCompendium.APP.shortcutKey;
	}

	/**
	 * Constructor. Create the menus and items and draws the popup menu.
	 * @param title, the title for this popup menu.
	 * @param viewpaneUI com.compendium.ui.plaf.ViewPaneUI, the associated map for this popup menu.
	 */
	public UIBasePopupMenu(String title, ViewPaneUI viewpaneUI) {
		super(title);
		init();
	}
		
	/**
	 * Subclasses need to implement this class
	 * @param viewpaneUI
	 */
	protected abstract void init();
	
	/**
	 * Add the Contents/Views/Properties menu items.
	 */
	protected void addContentsMenuItems() {
		addContents();
		addProperties();
		addViews();
	}
	
	/**
	 * Add the menu item to open the Contents tab of the contents dialog.
	 */
	protected void addContents() {
		miMenuItemOpen = new JMenuItem(LanguageProperties.getString(LanguageProperties.POPUPS_BUNDLE, "UIBasePopupMenu.contents")); //$NON-NLS-1$
		miMenuItemOpen.addActionListener(this);
		add(miMenuItemOpen);
	}

	/**
	 * Add the menu item to open the Properties tab of the contents dialog.
	 */
	protected void addProperties() {
		miMenuItemProperties = new JMenuItem(LanguageProperties.getString(LanguageProperties.POPUPS_BUNDLE, "UIBasePopupMenu.properties")); //$NON-NLS-1$
		miMenuItemProperties.addActionListener(this);
		add(miMenuItemProperties);
	}
	
	/**
	 * Add the menu item to open the Views tab of the contents dialog.
	 */
	protected void addViews() {
		miMenuItemViews = new JMenuItem(LanguageProperties.getString(LanguageProperties.POPUPS_BUNDLE, "UIBasePopupMenu.views")); //$NON-NLS-1$
		miMenuItemViews.addActionListener(this);
		add(miMenuItemViews);		
	}
	
	/**
	 * Create a References menu listing the references passed in.
	 * @param refNodes the references to add.
	 */
	protected void addReferences(Vector refNodes) {
		int count = refNodes.size();
		if (count > 0) {
			Vector sortedRefs = UIUtilities.sortReferences(refNodes);
			count = sortedRefs.size();
			if (count > 0) {
				mnuRefNodes = new JMenu(LanguageProperties.getString(LanguageProperties.POPUPS_BUNDLE, "UIBasePopupMenu.references")); //$NON-NLS-1$
				add(mnuRefNodes);
				for (int i=0; i<count; i++) {
					JMenuItem miMenuItem = (JMenuItem)sortedRefs.elementAt(i);
					mnuRefNodes.add(miMenuItem);
				}
	
				addSeparator();
			}
		}
	}
	
	/**
	 * Add a menu item to create an internal reference node / nodes.
	 */
	protected void addInternalReference() {
		miInternalReference = new JMenuItem(LanguageProperties.getString(LanguageProperties.POPUPS_BUNDLE, "UIBasePopupMenu.createIRN")); //$NON-NLS-1$
		miInternalReference.setToolTipText(LanguageProperties.getString(LanguageProperties.POPUPS_BUNDLE, "UIBasePopupMenu.createIRNTip")); //$NON-NLS-1$
		miInternalReference.addActionListener(this);
		add(miInternalReference);		
	}
	
	/**
	 * Add option to send node(s) to one or more inbox.
	 */
	protected void addSendToInbox() {
		miToInBox = new JMenuItem(LanguageProperties.getString(LanguageProperties.POPUPS_BUNDLE, "UIBasePopupMenu.sentToInbox")); //$NON-NLS-1$
		miToInBox.setToolTipText(LanguageProperties.getString(LanguageProperties.POPUPS_BUNDLE, "UIBasePopupMenu.sentToInboxTip")); //$NON-NLS-1$
		miToInBox.addActionListener(this);
		add(miToInBox);					
	}
	
	/**
	 * Add a menu item to bookmark node(s).
	 */
	protected void addBookmark() {
		miFavorites = new JMenuItem(LanguageProperties.getString(LanguageProperties.POPUPS_BUNDLE, "UIBasePopupMenu.bookmark")); //$NON-NLS-1$
		miFavorites.addActionListener(this);
		add(miFavorites);		
	}
	
	/**
	 * Add a menu item to view node readers.
	 */
	protected void addReaders() {
		miMenuItemReaders = new JMenuItem(LanguageProperties.getString(LanguageProperties.POPUPS_BUNDLE, "UIBasePopupMenu.readers")); //$NON-NLS-1$
		miMenuItemReaders.addActionListener(this);
		miMenuItemReaders.setEnabled(true);
		add(miMenuItemReaders);		
	}
	
	/**
	 * Add the MoveDetailToLabel and MoveLabelToDetail options.
	 */
	protected void addMoveLabelDetails() {
		miMoveDetail = new JMenuItem(LanguageProperties.getString(LanguageProperties.POPUPS_BUNDLE, "UIBasePopupMenu.detailToLabel")); //$NON-NLS-1$
		miMoveDetail.addActionListener(this);
		add(miMoveDetail);

		miMoveLabel = new JMenuItem(LanguageProperties.getString(LanguageProperties.POPUPS_BUNDLE, "UIBasePopupMenu.labelToDetail")); //$NON-NLS-1$
		miMoveLabel.addActionListener(this);
		add(miMoveLabel);		
	}

	/**
	 * Add cut/copy/paste menu items
	 */
	protected void addCutCopyPaste(int shortcutKey) {
		addCopy(shortcutKey);
		addCut(shortcutKey);
		addPaste(shortcutKey);
	}
	
	/**
	 * Add the copy menu item.
	 * @param shortcutKey The platform specific shortcut key used to access menus and their options
	 */
	protected void addCopy(int shortcutKey) {
		miMenuItemCopy = new JMenuItem(LanguageProperties.getString(LanguageProperties.POPUPS_BUNDLE, "UIBasePopupMenu.copy"), UIImages.get(IUIConstants.COPY_ICON)); //$NON-NLS-1$
		miMenuItemCopy.setAccelerator(KeyStroke.getKeyStroke( KeyEvent.VK_C, shortcutKey));
		miMenuItemCopy.addActionListener(this);
		add(miMenuItemCopy);
	}

	/**
	 * Add the cut menu item.
	 * @param shortcutKey The platform specific shortcut key used to access menus and their options
	 */
	protected void addCut(int shortcutKey) {
		miMenuItemCut = new JMenuItem(LanguageProperties.getString(LanguageProperties.POPUPS_BUNDLE, "UIBasePopupMenu.cut"), UIImages.get(IUIConstants.CUT_ICON)); //$NON-NLS-1$
		miMenuItemCut.setAccelerator(KeyStroke.getKeyStroke( KeyEvent.VK_X, shortcutKey));
		miMenuItemCut.addActionListener(this);
		add(miMenuItemCut);
	}

	/**
	 * Add the paste menu item.
	 * @param shortcutKey The platform specific shortcut key used to access menus and their options
	 */
	protected void addPaste(int shortcutKey) {
		miMenuItemPaste = new JMenuItem(LanguageProperties.getString(LanguageProperties.POPUPS_BUNDLE, "UIBasePopupMenu.paste"), UIImages.get(IUIConstants.PASTE_ICON)); //$NON-NLS-1$
		miMenuItemPaste.setAccelerator(KeyStroke.getKeyStroke( KeyEvent.VK_V, shortcutKey));
		miMenuItemPaste.addActionListener(this);
		add(miMenuItemPaste);	
	}
	
	/**
	 * Add the menu item to delete the selected nodes.
	 * @param shortcutKey The platform specific shortcut key used to access menus and their options
	 */
	protected void addDelete(int shortcutKey) {
		miMenuItemDelete = new JMenuItem(LanguageProperties.getString(LanguageProperties.POPUPS_BUNDLE, "UIBasePopupMenu.delete"), UIImages.get(IUIConstants.DELETE_ICON)); //$NON-NLS-1$
		miMenuItemDelete.setAccelerator(KeyStroke.getKeyStroke( KeyEvent.VK_DELETE, 0));
		miMenuItemDelete.addActionListener(this);
		add(miMenuItemDelete);
		
	}
	
	/**
	 * Add the Claimeker menu if connection exists;
	 */
	protected void addClaiMakerMenu() {
		if (ProjectCompendium.APP.isClaiMakerConnected()) {
			claiMakerServer = ProjectCompendium.APP.getClaiMakerServer();
			mnuClaiMaker = new JMenu(LanguageProperties.getString(LanguageProperties.POPUPS_BUNDLE, "UIBasePopupMenu.searchClaiMaker")); //$NON-NLS-1$
			add(mnuClaiMaker);

			miClaiConcepts = new JMenuItem(LanguageProperties.getString(LanguageProperties.POPUPS_BUNDLE, "UIBasePopupMenu.concepts")); //$NON-NLS-1$
			miClaiConcepts.addActionListener(this);
			mnuClaiMaker.add(miClaiConcepts);

			miClaiNeighbourhood = new JMenuItem(LanguageProperties.getString(LanguageProperties.POPUPS_BUNDLE, "UIBasePopupMenu.neighbourhood")); //$NON-NLS-1$
			miClaiNeighbourhood.addActionListener(this);
			mnuClaiMaker.add(miClaiNeighbourhood);

			miClaiDocuments = new JMenuItem(LanguageProperties.getString(LanguageProperties.POPUPS_BUNDLE, "UIBasePopupMenu.documents")); //$NON-NLS-1$
			miClaiDocuments.addActionListener(this);
			mnuClaiMaker.add(miClaiDocuments);
			addSeparator();
		}		
	}
	
	/**
	 * Add the option to search Google
	 */
	protected void addGoogleSearch() {
		miGoogleSearch = new JMenuItem(LanguageProperties.getString(LanguageProperties.POPUPS_BUNDLE, "UIBasePopupMenu.searchGoogle")); //$NON-NLS-1$
		miGoogleSearch.addActionListener(this);
		add(miGoogleSearch);
	}
		
	/**
	 * Add the menu item to clone the selected nodes.
	 */
	protected void addClone() {
		miMenuItemClone = new JMenuItem(LanguageProperties.getString(LanguageProperties.POPUPS_BUNDLE, "UIBasePopupMenu.clone")); //$NON-NLS-1$
		miMenuItemClone.addActionListener(this);
		add(miMenuItemClone);
	}
	
	/**
	 * Add the menu item to shortcut the selected nodes
	 */
	protected void addShortcut() {
		miMenuItemShortCut = new JMenuItem(LanguageProperties.getString(LanguageProperties.POPUPS_BUNDLE, "UIBasePopupMenu.shortcut")); //$NON-NLS-1$
		miMenuItemShortCut.addActionListener(this);
		add(miMenuItemShortCut);
	}
	
	/**
	 * Add Mark Seen / Mark Unseen menu items
	 */
	protected void addSeenUnseen() {
		// Mark seen and unseen for multiple nodes.
		miMenuItemMarkSeen = new JMenuItem(LanguageProperties.getString(LanguageProperties.POPUPS_BUNDLE, "UIBasePopupMenu.markSeen")); //$NON-NLS-1$
		miMenuItemMarkSeen.setAccelerator(KeyStroke.getKeyStroke( KeyEvent.VK_F12, 0));
		miMenuItemMarkSeen.addActionListener(this);
		miMenuItemMarkSeen.setEnabled(false);
		add(miMenuItemMarkSeen);

		miMenuItemMarkUnseen = new JMenuItem(LanguageProperties.getString(LanguageProperties.POPUPS_BUNDLE, "UIBasePopupMenu.markUnseen")); //$NON-NLS-1$
		miMenuItemMarkUnseen.setAccelerator(KeyStroke.getKeyStroke( KeyEvent.VK_F12, 1));
		miMenuItemMarkUnseen.addActionListener(this);
		add(miMenuItemMarkUnseen);
		miMenuItemMarkUnseen.setEnabled(false);
	}
	
	protected void addStencilMenuItems() {
		miStencilManagement = new JMenuItem(LanguageProperties.getString(LanguageProperties.POPUPS_BUNDLE, "UIBasePopupMenu.manageStencils")); //$NON-NLS-1$
		miStencilManagement.addActionListener(this);
		add(miStencilManagement);

		mnuStencils	= new JMenu(LanguageProperties.getString(LanguageProperties.POPUPS_BUNDLE, "UIBasePopupMenu.openStencil")); //$NON-NLS-1$
		add(mnuStencils);
		Vector vtStencils = ProjectCompendium.APP.oStencilManager.getStencilNames();
		vtStencils = CoreUtilities.sortList(vtStencils);
		int icount = vtStencils.size();
		for (int i=0; i < icount; i++) {
			final String sName = (String)vtStencils.elementAt(i);
			JMenuItem item = new JMenuItem(sName);
			ActionListener oAction = new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					ProjectCompendium.APP.oStencilManager.openStencilSet(sName);
				}
			};
			item.addActionListener(oAction);
			mnuStencils.add(item);
		}		
	}
	
	/**
	 * Add the node creation submenu and items
	 */
	protected void addNodeCreationMenu() {
		mnuNodes = new JMenu(LanguageProperties.getString(LanguageProperties.POPUPS_BUNDLE, "UIBasePopupMenu.createNode")); //$NON-NLS-1$

		miMenuItemIssue = new JMenuItem(UINodeTypeManager.getShortcutKeyForType(ICoreConstants.ISSUE)+"  "+UINodeTypeManager.getNodeTypeDescription(ICoreConstants.ISSUE), UIImages.getNodeIcon(IUIConstants.ISSUE_SM_ICON)); // issue renamed to question //$NON-NLS-1$
		miMenuItemIssue.addActionListener(this);
		miMenuItemIssue.setMnemonic(UINodeTypeManager.getMnemonicForNodeType(ICoreConstants.ISSUE));
		mnuNodes.add(miMenuItemIssue);
		
		miMenuItemPosition = new JMenuItem(UINodeTypeManager.getShortcutKeyForType(ICoreConstants.POSITION)+"  "+UINodeTypeManager.getNodeTypeDescription(ICoreConstants.POSITION), UIImages.getNodeIcon(IUIConstants.POSITION_SM_ICON)); //position renamed to answer //$NON-NLS-1$
		miMenuItemPosition.addActionListener(this);
		miMenuItemPosition.setMnemonic(UINodeTypeManager.getMnemonicForNodeType(ICoreConstants.POSITION));
		mnuNodes.add(miMenuItemPosition);
		mnuNodes.addSeparator();

		miMenuItemMap = new JMenuItem(UINodeTypeManager.getShortcutKeyForType(ICoreConstants.MAPVIEW)+"  "+UINodeTypeManager.getNodeTypeDescription(ICoreConstants.MAPVIEW), UIImages.getNodeIcon(IUIConstants.MAP_SM_ICON)); //$NON-NLS-1$
		miMenuItemMap.addActionListener(this);
		miMenuItemMap.setMnemonic(UINodeTypeManager.getMnemonicForNodeType(ICoreConstants.MAPVIEW));
		mnuNodes.add(miMenuItemMap);

		miMenuItemMovieMap = new JMenuItem(UINodeTypeManager.getShortcutKeyForType(ICoreConstants.MOVIEMAPVIEW)+"  "+UINodeTypeManager.getNodeTypeDescription(ICoreConstants.MOVIEMAPVIEW), UIImages.getNodeIcon(IUIConstants.MOVIEMAP_SM_ICON)); //$NON-NLS-1$
		miMenuItemMovieMap.addActionListener(this);
		miMenuItemMovieMap.setMnemonic(UINodeTypeManager.getMnemonicForNodeType(ICoreConstants.MOVIEMAPVIEW));
		mnuNodes.add(miMenuItemMovieMap);

		miMenuItemList = new JMenuItem(UINodeTypeManager.getShortcutKeyForType(ICoreConstants.LISTVIEW)+"  "+UINodeTypeManager.getNodeTypeDescription(ICoreConstants.LISTVIEW), UIImages.getNodeIcon(IUIConstants.LIST_SM_ICON)); //$NON-NLS-1$
		miMenuItemList.addActionListener(this);
		miMenuItemList.setMnemonic(UINodeTypeManager.getMnemonicForNodeType(ICoreConstants.LISTVIEW));
		mnuNodes.add(miMenuItemList);
		mnuNodes.addSeparator();
				
		miMenuItemPro = new JMenuItem(UINodeTypeManager.getShortcutKeyForType(ICoreConstants.PRO)+"  "+UINodeTypeManager.getNodeTypeDescription(ICoreConstants.PRO), UIImages.getNodeIcon(IUIConstants.PRO_SM_ICON)); //$NON-NLS-1$
		miMenuItemPro.addActionListener(this);
		miMenuItemPro.setMnemonic(UINodeTypeManager.getMnemonicForNodeType(ICoreConstants.PRO));
		mnuNodes.add(miMenuItemPro);
		
		miMenuItemCon = new JMenuItem(UINodeTypeManager.getShortcutKeyForType(ICoreConstants.CON)+"  "+UINodeTypeManager.getNodeTypeDescription(ICoreConstants.CON), UIImages.getNodeIcon(IUIConstants.CON_SM_ICON)); //$NON-NLS-1$
		miMenuItemCon.addActionListener(this);
		miMenuItemCon.setMnemonic(UINodeTypeManager.getMnemonicForNodeType(ICoreConstants.CON));
		mnuNodes.add(miMenuItemCon);
		mnuNodes.addSeparator();

		miMenuItemReference = new JMenuItem(UINodeTypeManager.getShortcutKeyForType(ICoreConstants.REFERENCE)+"  "+UINodeTypeManager.getNodeTypeDescription(ICoreConstants.REFERENCE), UIImages.getNodeIcon(IUIConstants.REFERENCE_SM_ICON)); //$NON-NLS-1$
		miMenuItemReference.addActionListener(this);
		miMenuItemReference.setMnemonic(UINodeTypeManager.getMnemonicForNodeType(ICoreConstants.REFERENCE));
		mnuNodes.add(miMenuItemReference);

		miMenuItemNote = new JMenuItem(UINodeTypeManager.getShortcutKeyForType(ICoreConstants.NOTE)+"  "+UINodeTypeManager.getNodeTypeDescription(ICoreConstants.NOTE), UIImages.getNodeIcon(IUIConstants.NOTE_SM_ICON)); //$NON-NLS-1$
		miMenuItemNote.addActionListener(this);
		miMenuItemNote.setMnemonic(UINodeTypeManager.getMnemonicForNodeType(ICoreConstants.NOTE));
		mnuNodes.add(miMenuItemNote);
		
		miMenuItemDecision = new JMenuItem(UINodeTypeManager.getShortcutKeyForType(ICoreConstants.DECISION)+"  "+UINodeTypeManager.getNodeTypeDescription(ICoreConstants.DECISION), UIImages.getNodeIcon(IUIConstants.DECISION_SM_ICON)); //$NON-NLS-1$
		miMenuItemDecision.addActionListener(this);
		miMenuItemDecision.setMnemonic(UINodeTypeManager.getMnemonicForNodeType(ICoreConstants.DECISION));
		mnuNodes.add(miMenuItemDecision);
		
		miMenuItemArgument = new JMenuItem(UINodeTypeManager.getShortcutKeyForType(ICoreConstants.ARGUMENT)+"  "+UINodeTypeManager.getNodeTypeDescription(ICoreConstants.ARGUMENT), UIImages.getNodeIcon(IUIConstants.ARGUMENT_SM_ICON)); //$NON-NLS-1$
		miMenuItemArgument.addActionListener(this);
		miMenuItemArgument.setMnemonic(UINodeTypeManager.getMnemonicForNodeType(ICoreConstants.ARGUMENT));
		mnuNodes.add(miMenuItemArgument);


		add(mnuNodes);
	}
	
	/**
	 * Add the Import submenu and items
	 */
	protected void addImportMenu() {
		mnuImport = new JMenu(LanguageProperties.getString(LanguageProperties.POPUPS_BUNDLE, "UIBasePopupMenu.import")); //$NON-NLS-1$

		miImportXMLView = new JMenuItem(LanguageProperties.getString(LanguageProperties.POPUPS_BUNDLE, "UIBasePopupMenu.xmlFile")); //$NON-NLS-1$
		miImportXMLView.addActionListener(this);
		mnuImport.add(miImportXMLView);

		miImportXMLFlashmeeting = new JMenuItem(LanguageProperties.getString(LanguageProperties.POPUPS_BUNDLE, "UIBasePopupMenu.flashmeeting")); //$NON-NLS-1$
		miImportXMLFlashmeeting.addActionListener(this);
		mnuImport.add(miImportXMLFlashmeeting);

		miFileImport = new JMenu(LanguageProperties.getString(LanguageProperties.POPUPS_BUNDLE, "UIBasePopupMenu.questmapFile")); //$NON-NLS-1$
		miFileImport.addActionListener(this);

		miImportCurrentView = new JMenuItem(LanguageProperties.getString(LanguageProperties.POPUPS_BUNDLE, "UIBasePopupMenu.currentView")); //$NON-NLS-1$
		miImportCurrentView.addActionListener(this);
		miFileImport.add(miImportCurrentView);

		miImportMultipleViews = new JMenuItem(LanguageProperties.getString(LanguageProperties.POPUPS_BUNDLE, "UIBasePopupMenu.multipleViews")); //$NON-NLS-1$
		miImportMultipleViews.addActionListener(this);
		miFileImport.add(miImportMultipleViews);

		mnuImport.add(miFileImport);

		add(mnuImport);	
	}
	
	/**
	 * Add the Export sub menu and items.
	 */
	protected void addExportMenu() {
		mnuExport = new JMenu(LanguageProperties.getString(LanguageProperties.POPUPS_BUNDLE, "UIBasePopupMenu.export")); //$NON-NLS-1$

		miExportXMLView = new JMenuItem(LanguageProperties.getString(LanguageProperties.POPUPS_BUNDLE, "UIBasePopupMenu.xmlFile")); //$NON-NLS-1$
		miExportXMLView.addActionListener(this);
		mnuExport.add(miExportXMLView);
			
		miExportHTMLOutline = new JMenuItem(LanguageProperties.getString(LanguageProperties.POPUPS_BUNDLE, "UIBasePopupMenu.webOutline")); //$NON-NLS-1$
		miExportHTMLOutline.addActionListener(this);
		mnuExport.add(miExportHTMLOutline);

		miExportHTMLView = new JMenuItem(LanguageProperties.getString(LanguageProperties.POPUPS_BUNDLE, "UIBasePopupMenu.webMaps")); //$NON-NLS-1$
		miExportHTMLView.addActionListener(this);
		mnuExport.add(miExportHTMLView);

		miExportHTMLViewXML = new JMenuItem(LanguageProperties.getString(LanguageProperties.POPUPS_BUNDLE, "UIBasePopupMenu.powerExport")); //$NON-NLS-1$
		miExportHTMLViewXML.setToolTipText(LanguageProperties.getString(LanguageProperties.POPUPS_BUNDLE, "UIBasePopupMenu.powerExportTip")); //$NON-NLS-1$
		miExportHTMLViewXML.addActionListener(this);
		mnuExport.add(miExportHTMLViewXML);

		add(mnuExport);		
	}
	
	/**
	 * Create the node type change submenu and items
	 */
	protected void createNodeTypeChangeMenu() {	
		mnuChangeType = new JMenu(LanguageProperties.getString(LanguageProperties.POPUPS_BUNDLE, "UIBaseMapPopupMenu.changeType")); //$NON-NLS-1$
		add(mnuChangeType);

		JMenu mnuStencils = new JMenu(LanguageProperties.getString(LanguageProperties.STENCILS_BUNDLE, "UIStencilDialog.stencils")); //$NON-NLS-1$
		mnuChangeType.add(mnuStencils);

		//add stencils
		Vector stencils = ProjectCompendium.APP.oStencilManager.getStencilNames();
		int count = stencils.size();
		String name = "";
		JMenu next = null;
		JMenuItem nextItem = null;
		for (int i=0; i<count; i++) {
			name = (String)stencils.elementAt(i);
			next = new JMenu(name);
			UIStencilSet set = ProjectCompendium.APP.oStencilManager.getStencilSet(name);
			Vector items = set.getItems();
			items = UIUtilities.sortList(items);
			int countj = items.size();
			for (int j=0; j<countj; j++) {
				final DraggableStencilIcon item = (DraggableStencilIcon)items.elementAt(j);
				nextItem = new JMenuItem(item.getName());
				nextItem.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						changeStencilType(item);
					}
				});
				next.add(nextItem);
			}
			if (countj > 0) {
				mnuStencils.add(next);
			}
		}
		
		miTypeIssue = new JMenuItem(UINodeTypeManager.convertNoteTypeToString(ICoreConstants.ISSUE), UIImages.getNodeIcon(IUIConstants.ISSUE_SM_ICON)); // issue renamed to question //$NON-NLS-1$
		miTypeIssue.addActionListener(this);
		miTypeIssue.setMnemonic(UINodeTypeManager.getMnemonicForNodeType(ICoreConstants.ISSUE));
		mnuChangeType.add(miTypeIssue);
	
		miTypePosition = new JMenuItem(UINodeTypeManager.convertNoteTypeToString(ICoreConstants.POSITION), UIImages.getNodeIcon(IUIConstants.POSITION_SM_ICON)); //position renamed to answer //$NON-NLS-1$
		miTypePosition.addActionListener(this);
		miTypePosition.setMnemonic(UINodeTypeManager.getMnemonicForNodeType(ICoreConstants.POSITION));
		mnuChangeType.add(miTypePosition);
	
		miTypeMap = new JMenuItem(UINodeTypeManager.convertNoteTypeToString(ICoreConstants.MAPVIEW), UIImages.getNodeIcon(IUIConstants.MAP_SM_ICON)); //$NON-NLS-1$
		miTypeMap.addActionListener(this);
		miTypeMap.setMnemonic(UINodeTypeManager.getMnemonicForNodeType(ICoreConstants.MAPVIEW));
		mnuChangeType.add(miTypeMap);

		miTypeMovieMap = new JMenuItem(UINodeTypeManager.convertNoteTypeToString(ICoreConstants.MOVIEMAPVIEW), UIImages.getNodeIcon(IUIConstants.MOVIEMAP_SM_ICON)); //$NON-NLS-1$
		miTypeMovieMap.addActionListener(this);
		miTypeMovieMap.setMnemonic(UINodeTypeManager.getMnemonicForNodeType(ICoreConstants.MOVIEMAPVIEW));
		mnuChangeType.add(miTypeMovieMap);

		miTypeList = new JMenuItem(UINodeTypeManager.convertNoteTypeToString(ICoreConstants.LISTVIEW), UIImages.getNodeIcon(IUIConstants.LIST_SM_ICON)); //$NON-NLS-1$
		miTypeList.addActionListener(this);
		miTypeList.setMnemonic(UINodeTypeManager.getMnemonicForNodeType(ICoreConstants.LISTVIEW));
		mnuChangeType.add(miTypeList);
	
		miTypePro = new JMenuItem(UINodeTypeManager.convertNoteTypeToString(ICoreConstants.PRO), UIImages.getNodeIcon(IUIConstants.PRO_SM_ICON)); //$NON-NLS-1$
		miTypePro.addActionListener(this);
		miTypePro.setMnemonic(UINodeTypeManager.getMnemonicForNodeType(ICoreConstants.PRO));
		mnuChangeType.add(miTypePro);
	
		miTypeCon = new JMenuItem(UINodeTypeManager.convertNoteTypeToString(ICoreConstants.CON), UIImages.getNodeIcon(IUIConstants.CON_SM_ICON)); //$NON-NLS-1$
		miTypeCon.addActionListener(this);
		miTypeCon.setMnemonic(UINodeTypeManager.getMnemonicForNodeType(ICoreConstants.CON));
		mnuChangeType.add(miTypeCon);
	
		miTypeReference = new JMenuItem(UINodeTypeManager.convertNoteTypeToString(ICoreConstants.REFERENCE), UIImages.getNodeIcon(IUIConstants.REFERENCE_SM_ICON)); //$NON-NLS-1$
		miTypeReference.addActionListener(this);
		miTypeReference.setMnemonic(UINodeTypeManager.getMnemonicForNodeType(ICoreConstants.REFERENCE));
		mnuChangeType.add(miTypeReference);
	
		miTypeNote = new JMenuItem(UINodeTypeManager.convertNoteTypeToString(ICoreConstants.NOTE), UIImages.getNodeIcon(IUIConstants.NOTE_SM_ICON)); //$NON-NLS-1$
		miTypeNote.addActionListener(this);
		miTypeNote.setMnemonic(UINodeTypeManager.getMnemonicForNodeType(ICoreConstants.NOTE));
		mnuChangeType.add(miTypeNote);
	
		miTypeDecision = new JMenuItem(UINodeTypeManager.convertNoteTypeToString(ICoreConstants.DECISION), UIImages.getNodeIcon(IUIConstants.DECISION_SM_ICON)); //$NON-NLS-1$
		miTypeDecision.addActionListener(this);
		miTypeDecision.setMnemonic(UINodeTypeManager.getMnemonicForNodeType(ICoreConstants.DECISION));
		mnuChangeType.add(miTypeDecision);
	
		miTypeArgument = new JMenuItem(UINodeTypeManager.convertNoteTypeToString(ICoreConstants.ARGUMENT), UIImages.getNodeIcon(IUIConstants.ARGUMENT_SM_ICON)); //$NON-NLS-1$
		miTypeArgument.addActionListener(this);
		miTypeArgument.setMnemonic(UINodeTypeManager.getMnemonicForNodeType(ICoreConstants.ARGUMENT));
		mnuChangeType.add(miTypeArgument);
	}				
	
	/**
	 * Add the Jabber and IXPanel menus if required.
	 */
	protected void addJabberAndIXPanelMenus() {

		boolean addSep = false;
		if (ProjectCompendium.APP.jabber != null &&
						ProjectCompendium.APP.jabber.getRoster().hasMoreElements()) {
			addSep = true;
			mnuSendToJabber = new JMenu(LanguageProperties.getString(LanguageProperties.POPUPS_BUNDLE, "UIBasePopupMenu.sentToJabber")); //$NON-NLS-1$
			mnuSendToJabber.setEnabled(false);
			add(mnuSendToJabber);
	
			ProjectCompendium.APP.drawJabberRoster( mnuSendToJabber );
		}

		if (ProjectCompendium.APP.ixPanel != null &&
						ProjectCompendium.APP.ixPanel.getRoster().hasMoreElements()) {

			addSep = true;
			mnuSendToIX = new JMenu(LanguageProperties.getString(LanguageProperties.POPUPS_BUNDLE, "UIBasePopupMenu.sentToIX")); //$NON-NLS-1$
			mnuSendToIX.setEnabled(false);
			add(mnuSendToIX);
			ProjectCompendium.APP.drawIXRoster( mnuSendToIX );
		}
		if ( addSep )
			addSeparator();		
	}
	
	/**
	 * Add the button that extends and contracts this menu
	 */
	protected void addExtenderButton() {
		if (oExtender == null) {
			oExtender = new UIControllerMenuItem();	
			oExtender.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					setDisplay(!oExtender.isDown());
					oExtender.setFocus();
				}
			});
			add(oExtender);	
		}
	}
	
	/**
	 * Hide/show items depending on whether the user wants the simple view or simple.
	 * @param bSimple
	 */
	protected abstract void setDisplay(boolean bSimple);

	/**
	 * Set the arrow on the menu to toggle up and down;
	 * @param bSimple
	 */
	protected void setControlItemStatus(boolean bSimple) {
		if (bSimple) {
			oExtender.pointDown();
		} else {
			oExtender.pointUp();
		}
	}		

	/**
	 * Handles the event of an option being selected.
	 * @param evt, the event associated with the option being selected.
	 */
	public void actionPerformed(ActionEvent evt) {
		ProjectCompendium.APP.setWaitCursor();

		Object source = evt.getSource();

		if (source.equals(miStencilManagement)) {
			UIStencilDialog dlg = new UIStencilDialog(ProjectCompendium.APP, ProjectCompendium.APP.oStencilManager);
			UIUtilities.centerComponent(dlg, ProjectCompendium.APP);
			dlg.setVisible(true);
		} else if(source.equals(miImportCurrentView)) {
			onImportFile(false);
		} else if(source.equals(miImportMultipleViews)) {
			onImportFile(true);
		} else if (source.equals(miExportHTMLOutline)) {
			onExportFile();
		} else if (source.equals(miExportHTMLView)) {
			onExportView();
		} else if (source.equals(miExportXMLView)) {
			onXMLExport(false);
		} else if (source.equals(miExportHTMLViewXML)) {
			ProjectCompendium.APP.onFileExportPower();			
		} else if (source.equals(miImportXMLView)) {
			onXMLImport();
		} else if (source.equals(miImportXMLFlashmeeting)) {
			UIImportFlashMeetingXMLDialog dlg = new UIImportFlashMeetingXMLDialog(ProjectCompendium.APP);
			UIUtilities.centerComponent(dlg, ProjectCompendium.APP);
			dlg.setVisible(true);							
		} else if(source.equals(miMenuItemOpen)) {
			openContents();
		} else if(source.equals(miMenuItemProperties)) {
			openProperties();
		} else if(source.equals(miMenuItemViews)) {
			openViews();					
		} else if(source.equals(miMenuItemCopy)) {
			copy();
		} else if(source.equals(miMenuItemCut)) {
			cut();
		} else if(source.equals(miMenuItemPaste)) {
			paste();
		} else if(source.equals(miMenuItemDelete)) {
			delete();
		} else if(source.equals(miMenuItemArgument)) {
			createNode(ICoreConstants.ARGUMENT);
		} else if(source.equals(miMenuItemCon)) {
			createNode(ICoreConstants.CON);
		} else if(source.equals(miMenuItemIssue)) {
			createNode(ICoreConstants.ISSUE);
		} else if(source.equals(miMenuItemPosition)) {
			createNode(ICoreConstants.POSITION);
		} else if(source.equals(miMenuItemPro))	{
			createNode(ICoreConstants.PRO);
		} else if(source.equals(miMenuItemDecision)) {
			createNode(ICoreConstants.DECISION);
		} else if(source.equals(miMenuItemNote)) {
			createNode(ICoreConstants.NOTE);
		} else if(source.equals(miMenuItemReference)) {
			createNode(ICoreConstants.REFERENCE);
		} else if(source.equals(miMenuItemList)) {
			createNode(ICoreConstants.LISTVIEW);
		} else if(source.equals(miMenuItemMap)) {
			createNode(ICoreConstants.MAPVIEW);
		} else if(source.equals(miMenuItemMovieMap)) {
			createNode(ICoreConstants.MOVIEMAPVIEW);
		} else if(source.equals(miMenuItemMarkSeen)) {
			markSeen();
		} else if(source.equals(miMenuItemMarkUnseen)) {
			markUnseen();
		} else if (source.equals(miMoveDetail)) {
			moveDetailToLabel();
		} else if (source.equals(miGoogleSearch)) {
			searchGoogle();
		} else if (source.equals(miClaiConcepts)) {
			searchClaiMakerConcepts();
		} else if (source.equals(miClaiNeighbourhood)) {
			searchClaiMakerNeighbourhood();
		} else if (source.equals(miClaiDocuments)) {
			searchClaiMakerDocuments();
		} else if (source.equals(miMoveLabel)) {
			moveLabelToDetail();
		} else if(source.equals(miMenuItemShortCut)) {
			shortcutNodes();
		} else if(source.equals(miMenuItemClone)) {
			cloneNodes();
		} else if (source.equals(miInternalReference)) {
			createInternalLink();
		} else if (source.equals(miToInBox)) {
			sendToInbox();
		} else if (source.equals(miFavorites)) {
			createBookmark();
		} else if(source.equals(miMenuItemReaders)) {
			displayReaders();
		} else if(source.equals(miTypeIssue)) {
			changeType(ICoreConstants.ISSUE);
		} else if(source.equals(miTypePosition)) {
			changeType(ICoreConstants.POSITION);
		} else if(source.equals(miTypeMap)) {
			changeType(ICoreConstants.MAPVIEW);
		} else if(source.equals(miTypeMovieMap)) {
			changeType(ICoreConstants.MOVIEMAPVIEW);			
		} else if(source.equals(miTypeList)) {
			changeType(ICoreConstants.LISTVIEW);
		} else if(source.equals(miTypePro)) {
			changeType(ICoreConstants.PRO);
		} else if(source.equals(miTypeCon)) {
			changeType(ICoreConstants.CON);
		} else if(source.equals(miTypeArgument)) {
			changeType(ICoreConstants.ARGUMENT);
		} else if(source.equals(miTypeDecision)) {
			changeType(ICoreConstants.DECISION);
		} else if(source.equals(miTypeNote)) {
			changeType(ICoreConstants.NOTE);
		} else if(source.equals(miTypeReference)) {
			changeType(ICoreConstants.REFERENCE);
		}
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
	 * Handle the cancelling of this popup. Set is to invisible.
	 */
	public void onCancel() {
		setVisible(false);
	}
	
	/**
	 * Exports the current view to an XML file.
	 */
	protected void onXMLExport(boolean multipleViews) {
		ProjectCompendium.APP.onFileXMLExport(multipleViews);
	}

	/**
	 * Imports an XML file into the current view.
	 */
	protected void onXMLImport() {
		ProjectCompendium.APP.onFileXMLImport();
	}
	
	/**
	 * Cut the selected node(s)
	 */
	protected void cut() {
		ProjectCompendium.APP.onEditCut();
	}
	
 	/**
	 * Copy the selected node(s)
	 */
	protected void copy() {
		ProjectCompendium.APP.onEditCopy();
	}
	
	/**
	 * Paste the selected node(s)
	 */
	protected void paste() {
		ProjectCompendium.APP.onEditPaste();		
	}
	
	/**
	 * Change the type of the selected nodes to the given type
	 * Subclasses should implement this method.
	 */
	protected  void changeType(int nType) {}

	/**
	 * Change the type of the selected nodes to the given stencil item
	 * Subclasses should implement this method.
	 */
	protected  void changeStencilType(DraggableStencilIcon item) {}

	/**
	 * Delete the currently selected nodes.
	 * Subclasses should implement this method.
	 */
	protected void delete() {}

	/**
	 * Create Shortcuts for the selected nodes.
	 * Subclasses should implement this method.
	 */
	protected void shortcutNodes() {}

	/**
	 * Clone the currently selected nodes.
	 * Subclasses should implement this method.
	 */
	protected void cloneNodes() {}

	/**
	 * Search Google using this node's label.
	 * Subclasses should implement this method.
	 */
	protected void searchGoogle() {}
	
	/**
	 * Search ClaiMaker concepts using this node's label.
	 * Subclasses should implement this method.
	 */
	protected void searchClaiMakerConcepts() {}

	/**
	 * Search ClaiMaker neighbourhood using this node's label.
	 * Subclasses should implement this method.
	 */
	protected void searchClaiMakerNeighbourhood() {}

	/**
	 * Search ClaiMaker documents using this node's label.
	 * Subclasses should implement this method.
	 */
	protected void searchClaiMakerDocuments() {}
		
	/**
	 * Move the first page of details of the currently selected nodes into their respective labels.
	 * Subclasses should implement this method.
	 */
	protected void moveDetailToLabel() {}

	/**
	 * Move the labels of the currently selected nodes into their respective details pages.
	 * Subclasses should implement this method.
	 */
	protected void moveLabelToDetail() {}

	/**
	 * Open the contents dialog for the given context on the Contents tab.
	 * Subclasses should implement this method.
	 */
	protected void openContents() {}

	/**
	 * Open the contents dialog for the given context on the properties tab.
	 * Subclasses should implement this method.
	 */
	protected void openProperties() {}

	/**
	 * Open the contents dialog for the given context on the views tab.
	 * Subclasses should implement this method.
	 */
	protected void openViews() {}

	/**
	 * Process a node creation request.
	 * Subclasses should implement this method.
	 * @param nType the type of the new node to create.
	 */
	protected void createNode(int nType) {}
	
	/**
	 * Handle a Questmap import request. 
	 * Subclasses should implement this method.
	 * @param showViewList, true if importing to mulitple views, else false.
	 */
	protected void onImportFile(boolean showViewList) {}

	/**
	 * Exports the current view to a HTML outline file.
	 * Subclasses should implement this method.
	 */
	protected void onExportFile() {}

	/**
	 * Exports the current view to a HTML Views file.
	 * Subclasses should implement this method.
	 */
	protected void onExportView() {}
		
	/**
	 * Mark the currently selected Nodes as seen.
	 * Subclasses should implement this method.
	 */
	protected void markSeen() {}

	/**
	 * Mark the currently selected nodes as unseen.
	 * Subclasses should implement this method.
	 */
	protected void markUnseen() {}
	
	/**
	 * Create a Reference node with internal link to this node.
	 * Subclasses should implement this method.
	 */
	protected void createInternalLink() {}
	
	/**
	 * Handle sending node(s) to one or more user's inbox.
	 * Subclasses should implement this method.
	 */
	protected void sendToInbox() {}
	
	/**
	 * Bookmark the current node(s)
	 * Subclasses should implement this method.
	 */
	protected void createBookmark() {}	
	
	/**
	 * Display user's who have read the current node(s).
	 * Subclasses should implement this method.
	 */
	protected void displayReaders() {}		
	
}
