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

import java.awt.event.*;
import java.util.*;
import javax.swing.*;

import com.compendium.core.CoreUtilities;
import com.compendium.core.ICoreConstants;
import com.compendium.core.datamodel.*;

import com.compendium.ProjectCompendium;
import com.compendium.ui.*;
import com.compendium.ui.plaf.*;
import com.compendium.ui.edits.*;
import com.compendium.ui.dialogs.UIExportDialog;
import com.compendium.ui.dialogs.UIExportViewDialog;
import com.compendium.ui.dialogs.UIImportDialog;
import com.compendium.ui.dialogs.UIImportFlashMeetingXMLDialog;
import com.compendium.ui.dialogs.UIReadersDialog;

/**
 * This class draws and handles events for the right-click menu for nodes in a list.
 *
 * @author	Mohammed Sajid Ali / Michelle Bachler / Lakshmi Prabhakaran
 */
public class UINodePopupMenuForList extends JPopupMenu implements ActionListener {

	/** The generated serial verison ID  */
	private static final long serialVersionUID 		= -6985279673753398219L;

	/** The default width for this popup menu.*/
	private static final int WIDTH					= 100;

	/** The default height for this popup menu.*/
	private static final int HEIGHT					= 300;

	/** The JMenuItem to open this node's contents dialog.*/
	private JMenuItem		miMenuItemOpen			= null;

	/** The JMenuItem to perform a copy operation.*/
	private JMenuItem		miMenuItemCopy			= null;

	/** The JMenuItem to perform a cut operation.*/
	private JMenuItem		miMenuItemCut			= null;

	/** The JMenu for node type change options.*/
	private JMenu			mnuChangeType			= null;

	/** The JMenuItem to change the selected nodes to Argument nodes.*/
	private JMenuItem		miTypeArgument			= null;

	/** The JMenuItem to change the selected nodes to Con nodes.*/
	private JMenuItem		miTypeCon				= null;

	/** The JMenuItem to change the selected nodes to Issue nodes.*/
	private JMenuItem		miTypeIssue				= null;

	/** The JMenuItem to change the selected nodes to Position nodes.*/
	private JMenuItem		miTypePosition			= null;

	/** The JMenuItem to change the selected nodes to Pro nodes.*/
	private JMenuItem		miTypePro				= null;

	/** The JMenuItem to change the selected nodes to Decision nodes.*/
	private JMenuItem		miTypeDecision			= null;

	/** The JMenuItem to change the selected nodes to Note nodes.*/
	private JMenuItem		miTypeNote				= null;

	/** The JMenuItem to change the selected nodes to Refrence nodes.*/
	private JMenuItem		miTypeReference			= null;

	/** The JMenuItem to change the selected nodes to List nodes.*/
	private JMenuItem		miTypeList				= null;

	/** The JMenuItem to change the selected nodes to Map nodes.*/
	private JMenuItem		miTypeMap				= null;

	/** The JMenuItem to add the node associated with this popup to the favorites list.*/
	private JMenuItem		miFavorites				= null;
	
	/** The JMenu to send information to IX Panels.*/
	private JMenu			mnuSendToIX				= null;

	/** The JMenu to send information to a Jabber client.*/
	private JMenu			mnuSendToJabber			= null;

	/** The JMenu which holds the import options.*/
	private JMenu			mnuImport				= null;

	/** The JMenu which holds the export options.*/
	private JMenu			mnuExport				= null;

	/** The JMenu which holds the import options.*/
	private JMenu			miFileImport			= null;

	/** The JMenuItem to import Questmap into the current View.*/
	private JMenuItem		miImportCurrentView	= null;

	/** The JMenuItem to import Questmap into multiple Views.*/
	private JMenuItem		miImportMultipleViews	= null;

	/** The JMenuItem to import XML.*/
	private JMenuItem		miImportXMLView			= null;

	/** The JMenuItem to export to a HTML Outline.*/
	private JMenuItem		miExportHTMLOutline		= null;

	/** The JMenuItem to export to a HTML Views.*/
	private JMenuItem		miExportHTMLView		= null;

	/** The JMenuItem to export to XML.*/
	private JMenuItem		miExportXMLView			= null;
	
	/** The menu item to export a HTML view with the XML included.*/
	private JMenuItem		miExportHTMLViewXML		= null;
	
	/** The menu item to import Flashmeeting XML.*/
	private JMenuItem		miImportXMLFlashmeeting = null;
	
	/** The JMenuItem to create a shortcut of the currently selected nodes, or the node associated with this popup.*/
	private JMenuItem		miMenuItemShortCut		= null;

	/** The JMenuItem to create a clone of the currently selected nodes, or the node associated with this popup.*/
	private JMenuItem		miMenuItemClone			= null;

	/** The JMenuItem to delete of the currently selected nodes, or the the node associated with this popup.*/
	private JMenuItem		miMenuItemDelete		= null;

	/** The JMenuItem to display list of previous readers  */
	private JMenuItem		miMenuItemReaders 		= null;

	/**The  JMenuItem to mark the nodes as read*/
	private JMenuItem		miMenuItemMarkSeen 		= null;

	/**The  JMenuItem to mark the nodes as unread*/
	private JMenuItem		miMenuItemMarkUnseen 	= null;

	/** The JMenuItem to open the associated nodes parent views dialog.*/
	private JMenuItem		miMenuItemViews 		= null;

	/** The JMenuItem to open the associated nodes properties dialog.*/
	private JMenuItem		miMenuItemProperties	= null;

	/** The JMenu item that holds CaliMaker search options.*/
	private JMenu			mnuClaiMaker			= null;

	/** The JMenuItem to opena browser window and run a ClaiMaker concept search for the current node's labale text.*/
	private JMenuItem		miClaiConcepts			= null;

	/** The JMenuItem to opena browser window and run a ClaiMaker neightbourhood search for the current node's labale text.*/
	private JMenuItem		miClaiNeighbourhood		= null;

	/** The JMenuItem to opena browser window and run a ClaiMaker document search for the current node's labale text.*/
	private JMenuItem		miClaiDocuments			= null;

	/** Open a broswer window and run a search on Goolge for the current node's label text.*/
	private JMenuItem		miGoogleSearch			= null;

	/** The JMenu to holds links to Reference nodes contained in the node if it is a map or a view.*/
	private JMenu			mnuRefNodes				= null;
	
	/** The menu item to create an internal reference node to this node.*/
	private JMenuItem		miInternalReference		= null;

	/** The menu to list users to to select from to sent node to thier in box.*/
	private JMenu			mnuToInBox				= null;
	
	/** The x value for the location of this popup menu.*/
	private int				nX						= 0;

	/** The y value for the location of this popup menu.*/
	private int				nY						= 0;

	/** The platform specific shortcut key used to access menus and thier options.*/
	private int shortcutKey;

	/** The NodeSummary object associated with this popup menu.*/
	private NodeSummary		oNode					= null;

	/** The NodePosition object associated with this popup menu.*/
	private NodePosition	oNodePosition			= null;

	/** The ListUI obnject associated with this popup menu.*/
	private ListUI			listui					= null;

	/** The base url string to sun claimaker searches.*/
	private String claiMakerServer 					= "";
	
	/**
	 * Constructor. Create the menus and items and draws the popup menu.
	 * @param title, the title for this popup menu.
	 * @param listui com.compendium.ui.plaf.ListUI, the associated list for this popup menu.
	 * @param nodePos com.compendium.core.datamodel.NodePosition, the associated node position for this popup menu.
	 */
	public UINodePopupMenuForList(String title, ListUI listui, NodePosition nodePos) {
		super(title);

		shortcutKey = ProjectCompendium.APP.shortcutKey;

		this.oNodePosition = nodePos;
		this.oNode = this.oNodePosition.getNode();
		this.listui = listui;

		miMenuItemOpen = new JMenuItem("Contents");
		miMenuItemOpen.addActionListener(this);
		miMenuItemOpen.setMnemonic(KeyEvent.VK_O);
		add(miMenuItemOpen);

		int nType = oNode.getType();

		/*
		if ( (nType != ICoreConstants.MAPVIEW && nType != ICoreConstants.LISTVIEW) && nType <= 10 ) {

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
			miTypeReference.setMnemonic(KeyEvent.VK_P);
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
			miTypeArgument.setMnemonic(KeyEvent.VK_D);
			mnuChangeType.add(miTypeArgument);
		}
		*/

		addSeparator();

		String sSource = oNode.getSource();
		
		if (!sSource.startsWith(ICoreConstants.sINTERNAL_REFERENCE)) {
			miInternalReference = new JMenuItem("Create Internal Reference Node");
			miInternalReference.setToolTipText("Create a Reference node with an internal reference to this node");
			miInternalReference.setMnemonic(KeyEvent.VK_I);
			miInternalReference.addActionListener(this);
			add(miInternalReference);
				
			mnuToInBox = new JMenu("Send To Inbox Of...");
			mnuToInBox.setToolTipText("Send Internal Reference node pointing to this node to selected User's Inbox");
			mnuToInBox.setMnemonic(KeyEvent.VK_B);
			mnuToInBox.addActionListener(this);		

			View oHomeView = ProjectCompendium.APP.getHomeView();
			boolean isHomeView = false;
			if (oHomeView.getId().equals(listui.getUIList().getView())) {
				isHomeView = true;
			}
	
			IModel oModel = ProjectCompendium.APP.getModel();	
			UserProfile up = null;
			
			if (isHomeView) {
				up = oModel.getUserProfile();
				final View foView = up.getLinkView();				
				final UserProfile fup = up;
				JMenuItem item = new JMenuItem(up.getUserName());
				item.setToolTipText("Add as 'Go To' node in your Inbox");				
				item.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						onCreateInternalLinkInView(foView, fup);
					}
				});
				mnuToInBox.add(item);	
			} else {
				Vector vtUsers = oModel.getUsers();
				int count = vtUsers.size();
				for (int i=0; i<count; i++) {
					up = (UserProfile)vtUsers.elementAt(i);
					JMenuItem item = new JMenuItem(up.getUserName());
					item.setToolTipText("Add as 'Go To' node in this user's Inbox");
					
					final View foView = up.getLinkView();				
					final UserProfile fup = up;
					item.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							onCreateInternalLinkInView(foView, fup);
						}
					});
					mnuToInBox.add(item);	
				}
			}
			
			add(mnuToInBox);
			
			addSeparator();
		}
		
		if (nType == ICoreConstants.MAPVIEW || nType == ICoreConstants.MAP_SHORTCUT ||
			nType == ICoreConstants.LISTVIEW || nType == ICoreConstants.LIST_SHORTCUT ) {

			View view = (View)oNode;
			try {view.initializeMembers();}
			catch(Exception ex) {}

			Vector refNodes = view.getReferenceNodes();

			int count = refNodes.size();
			if (count > 0) {

				Vector sortedRefs = UIUtilities.sortReferences(refNodes);
				count = sortedRefs.size();
				if (count > 0) {
					mnuRefNodes = new JMenu("References ...");
					mnuRefNodes.addActionListener(this);
					mnuRefNodes.setMnemonic(KeyEvent.VK_R);
					add(mnuRefNodes);

					for (int i=0; i<count; i++) {
						JMenuItem miMenuItem = (JMenuItem)sortedRefs.elementAt(i);
						mnuRefNodes.add(miMenuItem);
					}

					addSeparator();
				}
			}
		}

		miMenuItemCopy = new JMenuItem("Copy", UIImages.get(IUIConstants.COPY_ICON));
		miMenuItemCopy.setAccelerator(KeyStroke.getKeyStroke( KeyEvent.VK_C, shortcutKey));
		miMenuItemCopy.addActionListener(this);
		miMenuItemCopy.setMnemonic(KeyEvent.VK_C);
		add(miMenuItemCopy);

		miMenuItemCut = new JMenuItem("Cut", UIImages.get(IUIConstants.CUT_ICON));
		miMenuItemCut.setAccelerator(KeyStroke.getKeyStroke( KeyEvent.VK_X, shortcutKey));
		miMenuItemCut.addActionListener(this);
		miMenuItemCut.setMnemonic(KeyEvent.VK_U);
		add(miMenuItemCut);

		miMenuItemDelete = new JMenuItem("Delete", UIImages.get(IUIConstants.DELETE_ICON));
		miMenuItemDelete.setAccelerator(KeyStroke.getKeyStroke( KeyEvent.VK_DELETE, 0));
		miMenuItemDelete.addActionListener(this);
		miMenuItemDelete.setMnemonic(KeyEvent.VK_D);
		add(miMenuItemDelete);

		addSeparator();

		// create IMPORT options
		mnuImport = new JMenu("Import");
		mnuImport.setMnemonic(KeyEvent.VK_I);

		miImportXMLView = new JMenuItem("XML File...");
		miImportXMLView.setMnemonic(KeyEvent.VK_X);
		miImportXMLView.addActionListener(this);

		miImportXMLFlashmeeting = new JMenuItem("FlashMeeting XML...");
		miImportXMLFlashmeeting.setMnemonic(KeyEvent.VK_F);
		miImportXMLFlashmeeting.addActionListener(this);
		mnuImport.add(miImportXMLFlashmeeting);
		
		mnuImport.add(miImportXMLView);

		miFileImport = new JMenu("Questmap File...");
		miFileImport.setMnemonic(KeyEvent.VK_Q);
		miFileImport.addActionListener(this);

		miImportCurrentView = new JMenuItem("Current View..");
		miImportCurrentView.setMnemonic(KeyEvent.VK_C);
		miImportCurrentView.addActionListener(this);
		miFileImport.add(miImportCurrentView);

		miImportMultipleViews = new JMenuItem("Multiple Views..");
		miImportMultipleViews.setMnemonic(KeyEvent.VK_M);
		miImportMultipleViews.addActionListener(this);
		miFileImport.add(miImportMultipleViews);

		mnuImport.add(miFileImport);

		add(mnuImport);

		// create EXPORT options
		mnuExport = new JMenu("Export");
		mnuExport.setMnemonic(KeyEvent.VK_X);

		miExportXMLView = new JMenuItem("XML File...");
		miExportXMLView.setMnemonic(KeyEvent.VK_X);
		miExportXMLView.addActionListener(this);
		mnuExport.add(miExportXMLView);

		miExportHTMLOutline = new JMenuItem("Web Outline...");
		miExportHTMLOutline.setMnemonic(KeyEvent.VK_O);
		miExportHTMLOutline.addActionListener(this);
		mnuExport.add(miExportHTMLOutline);

		miExportHTMLView = new JMenuItem("Web Maps...");
		miExportHTMLView.setMnemonic(KeyEvent.VK_M);
		miExportHTMLView.addActionListener(this);
		mnuExport.add(miExportHTMLView);

		miExportHTMLViewXML = new JMenuItem("Power Export...");
		miExportHTMLViewXML.setToolTipText("Integrated Web Map and Outline Export with XML zip export inlcuded");
		miExportHTMLViewXML.setMnemonic(KeyEvent.VK_P);
		miExportHTMLViewXML.addActionListener(this);
		mnuExport.add(miExportHTMLViewXML);
		
		add(mnuExport);
		addSeparator();
		
		miGoogleSearch = new JMenuItem("Search Google");
		miGoogleSearch.addActionListener(this);
		miGoogleSearch.setMnemonic(KeyEvent.VK_G);
		add(miGoogleSearch);

		// SEND TO OPTIONS
		boolean addSep = false;
		if (ProjectCompendium.APP.jabber != null &&
									ProjectCompendium.APP.jabber.getRoster().hasMoreElements()) {
			addSep = true;
			mnuSendToJabber = new JMenu("Send To Jabber");
			mnuSendToJabber.setEnabled(false);
			mnuSendToJabber.setMnemonic(KeyEvent.VK_J);
			add(mnuSendToJabber);
			ProjectCompendium.APP.drawJabberRoster( mnuSendToJabber, oNode );
		}

		if (ProjectCompendium.APP.ixPanel != null &&
									ProjectCompendium.APP.ixPanel.getRoster().hasMoreElements()) {
			addSep = true;
			mnuSendToIX = new JMenu("Send To IX");
			mnuSendToIX.setEnabled(false);
			mnuSendToIX.setMnemonic(KeyEvent.VK_X);
			add(mnuSendToIX);
			ProjectCompendium.APP.drawIXRoster( mnuSendToIX, oNode );
		}

		if ( addSep )
			addSeparator();

		miMenuItemShortCut = new JMenuItem("Shortcut");
		miMenuItemShortCut.addActionListener(this);
		miMenuItemShortCut.setMnemonic(KeyEvent.VK_S);
		add(miMenuItemShortCut);
		if( oNode instanceof ShortCutNodeSummary)
			miMenuItemShortCut.setEnabled(false);

		miMenuItemClone = new JMenuItem("Clone");
		miMenuItemClone.addActionListener(this);
		miMenuItemClone.setMnemonic(KeyEvent.VK_L);
//		miMenuItemClone.setEnabled(false);
		add(miMenuItemClone);
		addSeparator();

		miFavorites = new JMenuItem("Bookmark Node");
		miFavorites.addActionListener(this);
		miFavorites.setMnemonic(KeyEvent.VK_F);
		add(miFavorites);

		addSeparator();		
		
		if (ProjectCompendium.APP.isClaiMakerConnected()) {

			claiMakerServer = ProjectCompendium.APP.getClaiMakerServer();

			mnuClaiMaker = new JMenu("Search ClaiMaker");
			mnuClaiMaker.setMnemonic(KeyEvent.VK_A);			
			add(mnuClaiMaker);

			miClaiConcepts = new JMenuItem("Concepts");
			miClaiConcepts.addActionListener(this);
			mnuClaiMaker.setMnemonic(KeyEvent.VK_A);
			mnuClaiMaker.add(miClaiConcepts);

			miClaiNeighbourhood = new JMenuItem("Conceptual Neighbourhood");
			miClaiNeighbourhood.addActionListener(this);
			miClaiNeighbourhood.setMnemonic(KeyEvent.VK_N);
			mnuClaiMaker.add(miClaiNeighbourhood);

			miClaiDocuments = new JMenuItem("Documents");
			miClaiDocuments.addActionListener(this);
			miClaiDocuments.setMnemonic(KeyEvent.VK_D);
			mnuClaiMaker.add(miClaiDocuments);

			addSeparator();
		}
		
		miMenuItemReaders = new JMenuItem("Readers");
		miMenuItemReaders.addActionListener(this);
		miMenuItemReaders.setMnemonic(KeyEvent.VK_R);
		miMenuItemReaders.setEnabled(true);
		add(miMenuItemReaders);
		
		miMenuItemMarkSeen = new JMenuItem("Mark Seen");
		miMenuItemMarkSeen.addActionListener(this);
		miMenuItemMarkSeen.setMnemonic(KeyEvent.VK_M);
		miMenuItemMarkSeen.setEnabled(true);
		add(miMenuItemMarkSeen);

		miMenuItemMarkUnseen = new JMenuItem("Mark Unseen");
		miMenuItemMarkUnseen.addActionListener(this);
		miMenuItemMarkUnseen.setMnemonic(KeyEvent.VK_N);
		add(miMenuItemMarkUnseen);
		miMenuItemMarkUnseen.setEnabled(true);
	
		int state = oNode.getState();
		if(state == ICoreConstants.READSTATE){
			miMenuItemMarkSeen.setEnabled(false);
		} else if(state == ICoreConstants.UNREADSTATE) {
			miMenuItemMarkUnseen.setEnabled(false);
		}
		addSeparator();

		
		miMenuItemProperties = new JMenuItem("Properties");
		miMenuItemProperties.addActionListener(this);
		miMenuItemProperties.setMnemonic(KeyEvent.VK_P);
		add(miMenuItemProperties);

		miMenuItemViews = new JMenuItem("Views");
		miMenuItemViews.addActionListener(this);
		miMenuItemViews.setMnemonic(KeyEvent.VK_W);
		add(miMenuItemViews);

		/**
		 * If on the Mac OS and the Menu bar is at the top of the OS screen, remove the menu shortcut Mnemonics.
		 */
		if (ProjectCompendium.isMac && (FormatProperties.macMenuBar || (!FormatProperties.macMenuBar && !FormatProperties.macMenuUnderline)) )
			UIUtilities.removeMenuMnemonics(getSubElements());

		pack();
		setSize(WIDTH,HEIGHT);
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
		ProjectCompendium.APP.setWaitCursor();

		Object source = evt.getSource();
		if(source.equals(miMenuItemDelete)) {
			DeleteEdit edit = new DeleteEdit(listui.getUIList().getViewFrame());
			listui.getUIList().deleteSelectedNodes(edit);
			listui.getUIList().getViewFrame().getUndoListener().postEdit(edit);
			//Thread thread = new Thread() {
			//	public void run() {
					ProjectCompendium.APP.setTrashBinIcon();
			//	}
			//};
			//thread.start();						
		} else if (source.equals(miInternalReference)) {
			onCreateInternalLink();
		} else if(source.equals(miMenuItemShortCut)) {
			// create shortcuts for all the selected nodes if user MULTISELECTs
			JTable list = listui.getUIList().getList();
			int[] selectedList = list.getSelectedRows();
			listui.getUIList().createShortCutNodes(selectedList);
			list.clearSelection();
			list.addRowSelectionInterval(list.getRowCount(),
										 list.getRowCount() + selectedList.length - 1);
		} else if(source.equals(miMenuItemClone)) {
			listui.createCloneNode(oNode);
		} else if (source.equals(miFavorites)) {
			onCreateBookmark();
			listui.getUIList().getList().requestFocus();
		} else if (source.equals(miImportCurrentView)) {
			onImportFile(false);
		} else if (source.equals(miImportMultipleViews)) {
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
		} else if(source.equals(miMenuItemCopy)) {
			listui.copyToClipboard();
		}
		else if(source.equals(miMenuItemCut))	{
			listui.cutToClipboard();
		}
		else if(source.equals(miMenuItemOpen)) {
			if (oNode instanceof View) {
				UIViewFrame oUIViewFrame = ProjectCompendium.APP.addViewToDesktop((View)oNode, oNode.getLabel());
				oUIViewFrame.setNavigationHistory(listui.getUIList().getViewFrame().getChildNavigationHistory());
			} else {
				listui.getUIList().showEditDialog(oNodePosition);
			}
		}
		else if(source.equals(miMenuItemReaders)) {
//			Lakshmi (4/19/06) - code added to display Readers list Dialog
			String nodeId = oNode.getId();
			UIReadersDialog readers = new UIReadersDialog(ProjectCompendium.APP, nodeId);
			UIUtilities.centerComponent(readers, ProjectCompendium.APP);
			readers.setVisible(true);
		}		
		else if(source.equals(miMenuItemMarkSeen)) {
			try {
				Enumeration e = listui.getUIList().getSelectedNodes();
				for(;e.hasMoreElements();){
					NodePosition np = (NodePosition) e.nextElement();
					NodeSummary oNode = np.getNode();
					oNode.setState(ICoreConstants.READSTATE);	
				}
			}
			catch(Exception io) {
				System.out.println("Unable to mark as read");
			}
		}
		else if(source.equals(miMenuItemMarkUnseen)) {
			try {
				Enumeration e = listui.getUIList().getSelectedNodes();
				for(;e.hasMoreElements();){
					NodePosition np = (NodePosition) e.nextElement();
					NodeSummary oNode = np.getNode();
					oNode.setState(ICoreConstants.UNREADSTATE);	
				}
			}
			catch(Exception io) {
				System.out.println("Unable to mark as un-read");
			}
		}
		else if (source.equals(miGoogleSearch)) {
			String sLabel = oNode.getLabel();
			try {
				sLabel = CoreUtilities.cleanURLText(sLabel);
			} catch (Exception e) {}
			ExecuteControl.launch( "http://www.google.com/search?hl=en&lr=&ie=UTF-8&oe=UTF-8&q="+sLabel );
		}
		else if (source.equals(miClaiConcepts)) {
			String sLabel = oNode.getLabel();
			try {
				sLabel = CoreUtilities.cleanURLText(sLabel);
			} catch (Exception e) {}
			ExecuteControl.launch( claiMakerServer+"search-concept.php?op=search&inputWord="+sLabel );
		}
		else if (source.equals(miClaiNeighbourhood)) {
			String sLabel = oNode.getLabel();
			try {
				sLabel = CoreUtilities.cleanURLText(sLabel);
			} catch (Exception e) {}
			ExecuteControl.launch( claiMakerServer+"discover/neighborhood.php?op=search&concept="+sLabel );
		}
		else if (source.equals(miClaiDocuments)) {
			String sLabel = oNode.getLabel();
			try {
				sLabel = CoreUtilities.cleanURLText(sLabel);
			} catch (Exception e) {}
			ExecuteControl.launch( claiMakerServer+"search-document.php?op=search&Title="+sLabel );
		}

		else if(source.equals(miMenuItemViews)) {
			listui.getUIList().showViewsDialog(oNodePosition);
		}
		else if(source.equals(miMenuItemProperties)) {
			listui.getUIList().showPropertiesDialog(oNodePosition);
		}

		/*
		else if(source.equals(miTypeIssue)) {
			oNode.getUINode().setType(ICoreConstants.ISSUE);
			oViewPane.setSelectedNode(oNode.getUINode(),ICoreConstants.MULTISELECT);
			oNode.getUINode().requestFocus();
		}
		else if(source.equals(miTypePosition)) {
			oNode.getUINode().setType(ICoreConstants.POSITION);
			oViewPane.setSelectedNode(oNode.getUINode(),ICoreConstants.MULTISELECT);
			oNode.getUINode().requestFocus();
		}
		else if(source.equals(miTypeMap)) {

			if (oNode.getUINode().setType(ICoreConstants.MAPVIEW)) {

				View currentView = oViewPane.getView();
				NodeSummary nodeSum = oNode.getUINode().getNode();

				String nodeId = nodeSum.getId();
 				String xNodeType = nodeSum.getExtendedNodeType();
				String sOriginalID = nodeSum.getOriginalID();
				int permission = ICoreConstants.WRITEVIEWNODE;
				int state = nodeSum.getState();
				String author = nodeSum.getAuthor();
				Date creationDate = nodeSum.getCreationDate();
				Date modificationDate = nodeSum.getModificationDate();
				String label = nodeSum.getLabel();
				String detail = nodeSum.getDetail();

				View newView = currentView.createView(nodeId, ICoreConstants.MAPVIEW, xNodeType, sOriginalID,
								 permission, state, author, creationDate,
								 modificationDate, label, detail);

				newView.setModel(ProjectCompendium.APP.getModel());
				newView.setSession(ProjectCompendium.APP.getModel().getSession());

				oNode.getUINode().setNode(newView);
				oViewPane.setSelectedNode(oNode.getUINode(),ICoreConstants.MULTISELECT);
				oNode.getUINode().requestFocus();
			}
		}
		else if(source.equals(miTypeList)) {

			if (oNode.getUINode().setType(ICoreConstants.LISTVIEW)) {

				View currentView = oViewPane.getView();
				NodeSummary nodeSum = oNode.getUINode().getNode();

				String nodeId = nodeSum.getId();
 				String xNodeType = nodeSum.getExtendedNodeType();
				String sOriginalID = nodeSum.getOriginalID();
				int permission = ICoreConstants.WRITEVIEWNODE;
				int state = nodeSum.getState();
				String author = nodeSum.getAuthor();
				Date creationDate = nodeSum.getCreationDate();
				Date modificationDate = nodeSum.getModificationDate();
				String label = nodeSum.getLabel();
				String detail = nodeSum.getDetail();

				View newView = currentView.createView(nodeId, ICoreConstants.LISTVIEW, xNodeType, sOriginalID,
									 permission, state, author, creationDate,
									 modificationDate, label, detail);

				newView.setModel(ProjectCompendium.APP.getModel());
				newView.setSession(ProjectCompendium.APP.getModel().getSession());

				oNode.getUINode().setNode(newView);
				oViewPane.setSelectedNode(oNode.getUINode(),ICoreConstants.MULTISELECT);
				oNode.getUINode().requestFocus();
			}
		}
		else if(source.equals(miTypePro)) {
			oNode.getUINode().setType(ICoreConstants.PRO);
			oViewPane.setSelectedNode(oNode.getUINode(),ICoreConstants.MULTISELECT);
			oNode.getUINode().requestFocus();
		}
		else if(source.equals(miTypeCon)) {
			oNode.getUINode().setType(ICoreConstants.CON);
			oViewPane.setSelectedNode(oNode.getUINode(),ICoreConstants.MULTISELECT);
			oNode.getUINode().requestFocus();
		}
		else if(source.equals(miTypeArgument)) {
			oNode.getUINode().setType(ICoreConstants.ARGUMENT);
			oViewPane.setSelectedNode(oNode.getUINode(),ICoreConstants.MULTISELECT);
			oNode.getUINode().requestFocus();
		}
		else if(source.equals(miTypeDecision)) {
			oNode.getUINode().setType(ICoreConstants.DECISION);
			oViewPane.setSelectedNode(oNode.getUINode(),ICoreConstants.MULTISELECT);
			oNode.getUINode().requestFocus();
		}
		else if(source.equals(miTypeNote)) {
			oNode.getUINode().setType(ICoreConstants.NOTE);
			oViewPane.setSelectedNode(oNode.getUINode(),ICoreConstants.MULTISELECT);
			oNode.getUINode().requestFocus();
		}
		else if(source.equals(miTypeReference)) {
			oNode.getUINode().setType(ICoreConstants.REFERENCE);
			oViewPane.setSelectedNode(oNode.getUINode(),ICoreConstants.MULTISELECT);
			oNode.getUINode().requestFocus();
		}
		*/

		ProjectCompendium.APP.setDefaultCursor();
	}
	
	
	/**
	 * Create a Reference node with internal link to this node 
	 * and put it in the inbox for the given View and user.
	 * @param view the view to add the reference to.
	 * @param up the UserProfile for the chosen user
	 */
	private void onCreateInternalLinkInView(View view, UserProfile up) {				
		if (view == null) {
			view = ProjectCompendium.APP.createInBox(up);
			if (view == null) {				
				ProjectCompendium.APP.displayError("Could not get reference to user's Inbox");						
				return;
			}
		}
		
		UIList uilist = listui.getUIList();
		
		IModel model = ProjectCompendium.APP.getModel();
		UserProfile currentUser = model.getUserProfile();		
		view.initialize(model.getSession(), model);
		
		String sRef = ICoreConstants.sINTERNAL_REFERENCE+uilist.getView().getId()+"/"+oNode.getId();
		String sAuthor = ProjectCompendium.APP.getModel().getUserProfile().getUserName();
		try{
			NodePosition node = view.addMemberNode(ICoreConstants.REFERENCE,
					 "",
					 "",
					 sAuthor,
					 "GO TO: "+oNode.getLabel(),
					 "( "+uilist.getView().getLabel()+" )\n\n"+				 
					 "Sent by "+currentUser.getUserName(),
					 0, ((view.getNodeCount() + 1) *10));
			
			node.getNode().setSource(sRef, "", sAuthor);
			node.getNode().setState(ICoreConstants.UNREADSTATE);
			view.setState(ICoreConstants.MODIFIEDSTATE);
		} catch (Exception e) {
			ProjectCompendium.APP.displayError("Could not add to InBox due to:\n\n"+e.getMessage());			
		}
		
		// If the view is open (it's your in box and you have it open), refresh the view.
		UIViewFrame oViewFrame = ProjectCompendium.APP.getInternalFrame(view);
		if (oViewFrame != null) {
			UIListViewFrame listFrame = (UIListViewFrame)oViewFrame;
			listFrame.getUIList().updateTable();
		}	
		
		ProjectCompendium.APP.refreshNodeIconIndicators(ProjectCompendium.APP.getInBoxID());
	}	
	
	/**
	 * Create a Reference node with internal link to this node.
	 */
	private void onCreateInternalLink() {

		UIList uilist = listui.getUIList();
		View view = uilist.getView();		
		
		String sRef = ICoreConstants.sINTERNAL_REFERENCE+view.getId()+"/"+oNode.getId();
		String sAuthor = ProjectCompendium.APP.getModel().getUserProfile().getUserName();

		// Do all calculations at 100% scale and then scale back down if required.
		if (uilist != null) {
			try {			
				// CREATE NEW NODE RIGHT OF THE GIVEN NODE WITH THE GIVEN LABEL
				
				NodePosition nodePos = listui.createNode(ICoreConstants.REFERENCE,
								 "",
								 ProjectCompendium.APP.getModel().getUserProfile().getUserName(),
								 "GO TO: "+oNode.getLabel(),
								 "( "+view.getLabel()+" )",
								 0, ((view.getNodeCount()+1)*10)
								 );
				
				nodePos.getNode().setSource(sRef, "", sAuthor);
				
				uilist.updateTable();
			} catch (Exception e) {
				ProjectCompendium.APP.displayError("Could not create internal link due to:\n\n"+e.getMessage());							
			}				
		}
	}		
	
	/**
	 * Create a Reference node with internal link to this node.
	 */
	private void onCreateBookmark() {
		View view = listui.getUIList().getView();				
		ProjectCompendium.APP.createFavorite(oNode.getId(), view.getId(), view.getLabel()+"&&&"+oNode.getLabel(), oNode.getType());
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
	 * Handle the canceling of this popup. Set is to invisible.
	 */
	public void onCancel() {
		setVisible(false);
	}
	
	/**
	 * Handle a Questmap import request.
	 * @param showViewList, true if importing to mulitpl views, else false.
	 */
	public void onImportFile(boolean showViewList) {

		UIImportDialog uid = new UIImportDialog(ProjectCompendium.APP, showViewList);
		uid.setUIList(listui.getUIList());
		uid.setVisible(true);
	}

	/**
	 * Exports the current view to a HTML outline file.
	 */
	public void onExportFile() {

		UIExportDialog export = new UIExportDialog(ProjectCompendium.APP, listui.getUIList().getViewFrame());
		export.setVisible(true);
	}

	/**
	 * Exports the current view to an XML file.
	 */
	public void onXMLExport(boolean multipleViews) {
		ProjectCompendium.APP.onFileXMLExport(multipleViews);
	}

	/**
	 * Imports an XML file into the current view.
	 */
	public void onXMLImport() {
		ProjectCompendium.APP.onFileXMLImport();
	}

	/**
	 * Exports the current view to a HTML Views file.
	 */
	public void onExportView() {
        UIExportViewDialog dialog2 = new UIExportViewDialog(ProjectCompendium.APP, listui.getUIList().getViewFrame());
		UIUtilities.centerComponent(dialog2, ProjectCompendium.APP);
		dialog2.setVisible(true);
	}	
}
