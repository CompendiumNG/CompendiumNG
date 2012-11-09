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
import java.awt.*;
import java.sql.SQLException;
import javax.swing.*;

import com.compendium.core.CoreUtilities;
import com.compendium.core.ICoreConstants;
import com.compendium.core.datamodel.*;

import com.compendium.ProjectCompendium;

import com.compendium.core.*;
import com.compendium.meeting.*;
import com.compendium.ui.*;
import com.compendium.ui.plaf.*;
import com.compendium.ui.edits.*;
import com.compendium.ui.dialogs.UIExportDialog;
import com.compendium.ui.dialogs.UIExportViewDialog;
import com.compendium.ui.dialogs.UIImportDialog;
import com.compendium.ui.dialogs.UIImportFlashMeetingXMLDialog;
import com.compendium.ui.dialogs.UIReadersDialog;
import com.compendium.ui.dialogs.UITrashViewDialog;
import com.compendium.io.udig.UDigClientSocket;

/**
 * This class draws and handles events for the right-cick menu for nodes in a map
 *
 * @author	Mohammed Sajid Ali / Michelle Bachler / Lakshmi Prabhakaran
 */
public class UINodePopupMenu extends JPopupMenu implements ActionListener {

	/** The generated serial version id  */
	private static final long serialVersionUID 		= 4134311162610834619L;

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

	/** The JMenuItem to purge the trashbins contents.*/
	private JMenuItem		miMenuItemEmpty			= null;

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

	/** The JMenuItem to import an external folder of image files into the current map.*/
	private JMenuItem		miImportImageFolder		= null;

	/** JMenuItem to export the current map to a Jpge file.*/
	private JMenuItem		miSaveAsJpeg			= null;

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

	/** The JMenu to send information to IX Panels.*/
	private JMenu			mnuSendToIX				= null;

	/** The JMenu to send information to a Jabber client.*/
	private JMenu			mnuSendToJabber			= null;

	/** The JMenu which holds the import options.*/
	private JMenu			mnuImport				= null;

	/** The JMenu which holds the import options.*/
	private JMenu			miFileImport			= null;

	/** The JMenuItem to import Questmap into the current View.*/
	private JMenuItem		miImportCurrentView		= null;

	/** The JMenuItem to import Questmap into multiple Views.*/
	private JMenuItem		miImportMultipleViews	= null;

	/** The JMenu which holds the export options.*/
	private JMenu			mnuExport				= null;

	/** The JMenuItem to export to a HTML Outline.*/
	private JMenuItem		miExportHTMLOutline		= null;

	/** The JMenuItem to export to a HTML Views.*/
	private JMenuItem		miExportHTMLView		= null;

	/** The JMenuItem to export to XML.*/
	private JMenuItem		miExportXMLView			= null;

	/** The JMenuItem to import XML.*/
	private JMenuItem		miImportXMLView			= null;
	
	/** The menu item to export a HTML view with the XML included.*/
	private JMenuItem		miExportHTMLViewXML		= null;	
	
	/** The menu item to import Flashmeeting XML.*/
	private JMenuItem		miImportXMLFlashmeeting = null;
	
	/** The JMenuItem to move the node's detail text into its label.*/
	private JMenuItem		miMoveDetail			= null;

	/** The JMenuItem to move the node's label text into its detail.*/
	private JMenuItem		miMoveLabel				= null;

	/** The JMenuItem to create a new map and transclude the node associated with this popup into it.*/
	private JMenuItem		miNewMap				= null;

	/** The JMenuItem to create a shortcut of the currently selected node, or the node associated with this popup.*/
	private JMenuItem		miMenuItemShortCut		= null;

	/** The JMenuItem to create a clone of the currently selected nodes, or the node associated with this popup.*/
	private JMenuItem		miMenuItemClone			= null;

	/** The JMenuItem to delete the currently selected nodes, or the node associated with this popup.*/
	private JMenuItem		miMenuItemDelete		= null;

	/** The JMenuItem to delink the currently selected nodes, or the node associated with this popup.*/
	private JMenuItem		miMenuItemDelink 		= null;

	/** The JMenuItem to display list of previous readers  */
	private JMenuItem		miMenuItemReaders 		= null;

	/** The  JMenuItem to mark the nodes as read*/
	private JMenuItem		miMenuItemMarkSeen 		= null;

	/**The  JMenuItem to mark the nodes as unread*/
	private JMenuItem		miMenuItemMarkUnseen 	= null;

	/** The JMenuItem to open the associated nodes parent views dialog.*/
	private JMenuItem		miMenuItemViews 		= null;

	/** The JMenuItem to open the associated nodes properties dialog.*/
	private JMenuItem		miMenuItemProperties	= null;

	/** The JMenuItem to add the node associated with this popup to the favorites list.*/
	private JMenuItem		miFavorites				= null;
	//private JMenuItem		miMediaStream			= null;

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

	/** The JMenuItem which communicates with the meeting replay Jabber account.*/
	private JMenuItem		miMeetingReplay			= null;

	/** The JJMenuItem which assigns the media index of the focused node to all other selected nodes.*/
	private JMenuItem		miAssignMediaIndex		= null;
	
	/** The menu item to open a map in UDIG.*/
	private JMenuItem		miUDIGMap				= null;

	/** The menu item to add node label as property to parent udig map point in UDIG.*/
	private JMenuItem		miUDIGProperty			= null;
	
	/** The menu item to format all transclusion of this node to this node's formatting.*/
	private JMenuItem		miFormatTransclusions 	= null;

	/** The menu item to format all children and submaps of this node to this node's formatting.*/
	private JMenuItem		miFormatAll 			= null;

	/** The menu item to create an internal reference node to this node.*/
	private JMenuItem		miInternalReference		= null;

	/** The menu to list users to to select from to sent node to thier in box.*/
	private JMenu			mnuToInBox				= null;
	

	/** The x value for the location of this popup menu.*/
	private int				nX						= 0;

	/** The y value for the location of this popup menu.*/
	private int				nY						= 0;

	/** The NodeUI object associated with this popup menu.*/
	private NodeUI			oNode					= null;

	/** The UIViewPane object associated with this popup menu.*/
	private UIViewPane		oViewPane				= null;

	/** The ViewPaneUI object associated with this popup menu.*/
	private ViewPaneUI		oViewPaneUI				= null;

	/** The base url string to sun claimaker searches.*/
	private String 		claiMakerServer 			= "";

	/** The platform specific shortcut key used to access menus and thier options.*/
	private int 		shortcutKey;
	
	/** Holds the check data when looping to update formats.*/
	private	Hashtable  htCheckFormatNodes 			= new Hashtable();



	/**
	 * Constructor. Create the menus and items and draws the popup menu.
	 * @param title, the title for this popup menu.
	 * @param nodeui com.compendium.ui.plaf.NodeUI, the associated node for this popup menu.
	 */
	public UINodePopupMenu(String title, NodeUI nodeui) {
		super(title);

		UIViewPane oViewPane = nodeui.getUINode().getViewPane();

		shortcutKey = ProjectCompendium.APP.shortcutKey;
		setNode(nodeui);
		
		int nType = nodeui.getUINode().getNode().getType();

		/*String sId = nodeui.getUINode().getNode().getId();		
		String sInbox = ProjectCompendium.APP.getInBoxID();
		if (sId.equals(sInbox)) {
			miMenuItemOpen = new JMenuItem("Contents");
			miMenuItemOpen.setMnemonic(KeyEvent.VK_O);
			miMenuItemOpen.addActionListener(this);
			add(miMenuItemOpen);

			miMenuItemProperties = new JMenuItem("Properties");
			miMenuItemProperties.addActionListener(this);
			miMenuItemProperties.setMnemonic(KeyEvent.VK_P);
			add(miMenuItemProperties);

			miMenuItemViews = new JMenuItem("Views");
			miMenuItemViews.addActionListener(this);
			miMenuItemViews.setMnemonic(KeyEvent.VK_V);
			add(miMenuItemViews);			
			
			if (ProjectCompendium.isMac && (FormatProperties.macMenuBar 
					|| (!FormatProperties.macMenuBar && !FormatProperties.macMenuUnderline)) ) {
				UIUtilities.removeMenuMnemonics(getSubElements());
			}
			
			pack();
			setSize(WIDTH,HEIGHT);
			
			return;
		}
		
		if( nType == ICoreConstants.TRASHBIN) {

			miMenuItemOpen = new JMenuItem("Open Trashbin");
			miMenuItemOpen.setMnemonic(KeyEvent.VK_O);
			miMenuItemOpen.addActionListener(this);
			add(miMenuItemOpen);
			addSeparator();

			miMenuItemEmpty = new JMenuItem("Purge Trashbin Contents");
			miMenuItemEmpty.setMnemonic(KeyEvent.VK_P);
			miMenuItemEmpty.addActionListener(this);
			add(miMenuItemEmpty);

			if (ProjectCompendium.isMac && (FormatProperties.macMenuBar 
					|| (!FormatProperties.macMenuBar && !FormatProperties.macMenuUnderline)) ) {
				UIUtilities.removeMenuMnemonics(getSubElements());
			}
			
			pack();
			setSize(WIDTH,HEIGHT);
			return;
		}*/

		if (ProjectCompendium.APP.oMeetingManager != null && ProjectCompendium.APP.oMeetingManager.captureEvents()) {
			addSeparator();
			miAssignMediaIndex = new JMenuItem("Assign Video Index");
			miAssignMediaIndex.setToolTipText("Assign the Video Index of this node to the other select nodes");
			miAssignMediaIndex.addActionListener(this);
			add(miAssignMediaIndex);

			if (ProjectCompendium.APP.oMeetingManager.getMeetingType() == MeetingManager.REPLAY) {
				miMeetingReplay = new JMenuItem("Replay Video");
				miMeetingReplay.addActionListener(this);
				add(miMeetingReplay);
			}
			addSeparator();
		}

		miMenuItemOpen = new JMenuItem("Contents");
		miMenuItemOpen.setMnemonic(KeyEvent.VK_O);
		miMenuItemOpen.addActionListener(this);
		add(miMenuItemOpen);

		/*if (ProjectCompendium.APP.media != null) {
			addSeparator();
			miMediaStream = new JMenuItem("Replay Media Stream");
			miMediaStream.addActionListener(this);
			add(miMediaStream);
			addSeparator();
		}*/

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

		miNewMap = new JMenuItem("Transclude To New Map");
		miNewMap.setMnemonic(KeyEvent.VK_R);
		miNewMap.addActionListener(this);
		add(miNewMap);

		String sSource = nodeui.getUINode().getNode().getSource();
		
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
			if (oHomeView.getId().equals(oViewPane.getView().getId())) {
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
		}
		
		miFormatTransclusions = new JMenuItem("Apply Format To All Transclusions");
		miFormatTransclusions.setToolTipText("Apply this node's formatting to all its transclusions");
		miFormatTransclusions.setMnemonic(KeyEvent.VK_F);
		miFormatTransclusions.addActionListener(this);
		add(miFormatTransclusions);

		if (nType == ICoreConstants.MAPVIEW || nType == ICoreConstants.MAP_SHORTCUT ||
				nType == ICoreConstants.LISTVIEW || nType == ICoreConstants.LIST_SHORTCUT ) {

			miFormatAll = new JMenuItem("Apply Format To Full Depth");
			miFormatAll.setToolTipText("Apply this node's formatting to all its child nodes and views and their child nodes and views etc..");
			//miFormatAll.setMnemonic(KeyEvent.VK_F);
			miFormatAll.addActionListener(this);
			add(miFormatAll);
		}

		addSeparator();

		View parentView = oViewPane.getView();
		String sParentSource = parentView.getSource();
		if (sParentSource.startsWith("UDIG") && FormatProperties.startUDigCommunications) {
			miUDIGProperty = new JMenuItem("Add Label To Parent Map Point", UIImages.get(IUIConstants.UDIG_ICON));
			miUDIGProperty.addActionListener(this);
			miUDIGProperty.setMnemonic(KeyEvent.VK_I);
			add(miUDIGProperty);	
			addSeparator();
		}
		
		if (nType == ICoreConstants.MAPVIEW || nType == ICoreConstants.MAP_SHORTCUT ||
			nType == ICoreConstants.LISTVIEW || nType == ICoreConstants.LIST_SHORTCUT ) {

			View view = null;
			if (nType == ICoreConstants.MAP_SHORTCUT || nType == ICoreConstants.LIST_SHORTCUT) {
				view = (View)(((ShortCutNodeSummary)nodeui.getUINode().getNode()).getReferredNode());
			}
			else {
				view = (View)nodeui.getUINode().getNode();
			} 

			sSource = view.getSource();
			if (sSource.startsWith("UDIG") && FormatProperties.startUDigCommunications) {
				miUDIGMap = new JMenuItem("Open uDig Map", UIImages.get(IUIConstants.UDIG_ICON));
				miUDIGMap.addActionListener(this);
				miUDIGMap.setMnemonic(KeyEvent.VK_M);
				add(miUDIGMap);	
				addSeparator();
			}
			
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
		miMenuItemCopy.setMnemonic(KeyEvent.VK_C);
		miMenuItemCopy.addActionListener(this);
		add(miMenuItemCopy);

		miMenuItemCut = new JMenuItem("Cut", UIImages.get(IUIConstants.CUT_ICON));
		miMenuItemCut.setAccelerator(KeyStroke.getKeyStroke( KeyEvent.VK_X, shortcutKey));
		miMenuItemCut.setMnemonic(KeyEvent.VK_U);
		miMenuItemCut.addActionListener(this);
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
		mnuImport.add(miImportXMLView);

		miImportXMLFlashmeeting = new JMenuItem("FlashMeeting XML...");
		miImportXMLFlashmeeting.setMnemonic(KeyEvent.VK_F);
		miImportXMLFlashmeeting.addActionListener(this);
		mnuImport.add(miImportXMLFlashmeeting);
		
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

		miImportImageFolder = new JMenuItem("Image Folder Into Current Map...");
		miImportImageFolder.setMnemonic(KeyEvent.VK_I);
		miImportImageFolder.addActionListener(this);
		mnuImport.add(miImportImageFolder);

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
		
		miSaveAsJpeg = new JMenuItem("Jpeg File...");
		miSaveAsJpeg.setMnemonic(KeyEvent.VK_J);
		miSaveAsJpeg.addActionListener(this);
		mnuExport.add(miSaveAsJpeg);

		add(mnuExport);
		addSeparator();
		
		miGoogleSearch = new JMenuItem("Search Google");
		miGoogleSearch.addActionListener(this);
		miGoogleSearch.setMnemonic(KeyEvent.VK_G);
		add(miGoogleSearch);

		// SEND TO OPTIONS

		boolean addSep = false;
		if (ProjectCompendiumFrame.jabber != null &&
								ProjectCompendiumFrame.jabber.getRoster().hasMoreElements()) {
			addSep = true;
			mnuSendToJabber = new JMenu("Send To Jabber");
			mnuSendToJabber.setMnemonic(KeyEvent.VK_J);
			mnuSendToJabber.setEnabled(false);
			add(mnuSendToJabber);
			ProjectCompendium.APP.drawJabberRoster( mnuSendToJabber, oNode.getUINode().getNode() );
		}

		if (ProjectCompendiumFrame.ixPanel != null &&
								ProjectCompendiumFrame.ixPanel.getRoster().hasMoreElements()) {
			addSep = true;
			mnuSendToIX = new JMenu("Send To IX");
			mnuSendToIX.setMnemonic(KeyEvent.VK_X);
			mnuSendToIX.setEnabled(false);
			add(mnuSendToIX);
			ProjectCompendium.APP.drawIXRoster( mnuSendToIX, oNode.getUINode().getNode() );
		}

		if ( addSep )
			addSeparator();

		miMenuItemShortCut = new JMenuItem("Shortcut");
		miMenuItemShortCut.addActionListener(this);
		miMenuItemShortCut.setMnemonic(KeyEvent.VK_S);
		add(miMenuItemShortCut);
		if( (nodeui.getUINode()).getNode() instanceof ShortCutNodeSummary)
			miMenuItemShortCut.setEnabled(false);

		miMenuItemClone = new JMenuItem("Clone");
		miMenuItemClone.addActionListener(this);
		miMenuItemClone.setMnemonic(KeyEvent.VK_L);
//		miMenuItemClone.setEnabled(false);
		add(miMenuItemClone);

		addSeparator();

		miMenuItemDelink = new JMenuItem("Delink");
		miMenuItemDelink.addActionListener(this);
		miMenuItemDelink.setMnemonic(KeyEvent.VK_K);
		add(miMenuItemDelink);

		miFavorites = new JMenuItem("Bookmark Node");
		miFavorites.addActionListener(this);
		//miFavorites.setMnemonic(KeyEvent.VK_A);
		add(miFavorites);

		addSeparator();

		if (ProjectCompendium.APP.isClaiMakerConnected()) {

			claiMakerServer = ProjectCompendium.APP.getClaiMakerServer();

			mnuClaiMaker = new JMenu("Search ClaiMaker");
			mnuClaiMaker.setMnemonic(KeyEvent.VK_A);
			add(mnuClaiMaker);

			miClaiConcepts = new JMenuItem("Concepts");
			miClaiConcepts.addActionListener(this);
			miClaiConcepts.setMnemonic(KeyEvent.VK_C);
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

		miMoveDetail = new JMenuItem("Move Detail Into Label");
		miMoveDetail.addActionListener(this);
		miMoveDetail.setMnemonic(KeyEvent.VK_E);
		add(miMoveDetail);

		miMoveLabel = new JMenuItem("Move Label Into Detail");
		miMoveLabel.addActionListener(this);
		miMoveLabel.setMnemonic(KeyEvent.VK_V);
		add(miMoveLabel);

		addSeparator();
		
		miMenuItemReaders = new JMenuItem("Readers");
		miMenuItemReaders.addActionListener(this);
		miMenuItemReaders.setMnemonic(KeyEvent.VK_R);
		add(miMenuItemReaders);
		
		miMenuItemMarkSeen = new JMenuItem("Mark Seen");
		miMenuItemMarkSeen.addActionListener(this);
		miMenuItemMarkSeen.setMnemonic(KeyEvent.VK_M);
		add(miMenuItemMarkSeen);				
		
		miMenuItemMarkUnseen = new JMenuItem("Mark Unseen");
		miMenuItemMarkUnseen.addActionListener(this);
		miMenuItemMarkUnseen.setMnemonic(KeyEvent.VK_N);
		add(miMenuItemMarkUnseen);		
		//Lakshmi (4/25/06) - if node is in read state enable mark unseen
		// and disable mark seen and vice versa
		int state = oNode.getUINode().getNode().getState();
		
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
	 * @param node com.compendium.ui.plaf.NodeUI, the node associated with this popup menu.
	 */
	public void setNode(NodeUI node) {
		oNode = node;
	}

	/**
	 * Handles the event of an option being selected.
	 * @param evt, the event associated with the option being selected.
	 */
	public void actionPerformed(ActionEvent evt) {

		Object source = evt.getSource();

		ProjectCompendium.APP.setWaitCursor();

		if (source.equals(miMeetingReplay)) {
			ProjectCompendium.APP.oMeetingManager.sendMeetingReplay(oNode);
			oNode.getUINode().requestFocus();
		} else if (source.equals(miImportCurrentView)) {
			onImportFile(false);
		} else if (source.equals(miImportMultipleViews)) {
			onImportFile(true);
		} else if (source.equals(miImportImageFolder)) {
			ProjectCompendium.APP.onFileImportImageFolder(oViewPane.getViewFrame());
		} else if (source.equals(miSaveAsJpeg)) {
			ProjectCompendium.APP.onSaveAsJpeg();
		} else if (source.equals(miExportHTMLOutline)) {
			onExportFile();
		} else if (source.equals(miExportHTMLView)) {
			onExportView();
		} else if (source.equals(miExportXMLView)) {
			onXMLExport(false);
		} else if (source.equals(miImportXMLView)) {
			onXMLImport();		
		} else if (source.equals(miExportHTMLViewXML)) {
			ProjectCompendium.APP.onFileExportPower();
		} else if (source.equals(miImportXMLFlashmeeting)) {
			UIImportFlashMeetingXMLDialog dlg = new UIImportFlashMeetingXMLDialog(ProjectCompendium.APP);
			UIUtilities.centerComponent(dlg, ProjectCompendium.APP);
			dlg.setVisible(true);							
		} else if (source.equals(miInternalReference)) {
			onCreateInternalLink();
		}		
		else if (source.equals(miUDIGMap)) {
			if (ProjectCompendium.APP.oUDigCommunicationManager != null) {
				String mapid = oNode.getUINode().getNode().getSource();
				mapid = mapid.substring(5);
				ProjectCompendium.APP.oUDigCommunicationManager.openMap(mapid);
			}
		}
		else if (source.equals(miUDIGProperty)) {
			if (ProjectCompendium.APP.oUDigCommunicationManager != null) {
				String sNotSent = "";
				String sDate = CoreCalendar.getCurrentDateStringFull();
				String sDetail = "";
				IModel model = ProjectCompendium.APP.getModel();				
				String sAuthor = model.getUserProfile().getUserName();
				Code oCode = null;				
				try {
					oCode = CoreUtilities.checkCreateCode("UDIG", model, model.getSession(), sAuthor);
				} catch(Exception e) {
					ProjectCompendium.APP.displayError("Unable to create and add 'UDIG' tag due to: \n"+e.getMessage());
				}

				if(oViewPane.getNumberOfSelectedNodes() > 1) {
					String sData = "";					
					Vector vtMatches = new Vector(10);					
					for(Enumeration e = oViewPane.getSelectedNodes();e.hasMoreElements();) {
						UINode uinode = (UINode)e.nextElement();
						String sLabel = uinode.getText();
						int index = sLabel.indexOf("=");
						int last = sLabel.lastIndexOf("=");
						if (index == -1) {
							sNotSent+=sLabel+"\n";
						} else if (index != last) {
							sNotSent+=sLabel+"\n";					
						} else {
							if (sData.equals("")) {
								sData = sLabel;
							} else {
								sData += "%%"+sLabel;
							}
							vtMatches.add(uinode);
						}		
					}	
					
					if (!sData.equals("")) {
						View view = oViewPane.getView();
						String mapid = view.getSource();
						mapid = mapid.substring(5);
						String reply = ProjectCompendium.APP.oUDigCommunicationManager.addProperty(mapid+"&&"+sData);						
						if (reply.equals(UDigClientSocket.OK)) {
							int count = vtMatches.size();
							for(int i=0; i<count; i++) {
								UINode uinode = (UINode)vtMatches.elementAt(i);									
								sDetail = uinode.getNode().getDetail();
								sDetail += "\nLabel: "+uinode.getText()+" Sent To UDIG: "+sDate;
								try {
									uinode.getNode().setDetail(sDetail, sAuthor, sAuthor);
									if (oCode != null) {
										uinode.getNode().addCode(oCode);
									}								
								} catch (Exception io) {								
									ProjectCompendium.APP.displayError("Problem encountered: \n"+io.getMessage());								
								}										
							}
						}
					}
					if (!sNotSent.equals("")) {
						ProjectCompendium.APP.displayError("Label was not correctly formated.\n'Key = Value' format expected.\n\n"+sNotSent);
					}					
				}
				else {
					UINode uinode = oNode.getUINode();
					String sLabel = uinode.getText();
					int index = sLabel.indexOf("=");
					int last = sLabel.lastIndexOf("=");
					if (index == -1) {
						sNotSent+=sLabel+"\n";
					} else if (index != last) {
						sNotSent+=sLabel+"\n";					
					} else {
						View view = oViewPane.getView();
						String mapid = view.getSource();
						mapid = mapid.substring(5);
						String reply = ProjectCompendium.APP.oUDigCommunicationManager.addProperty(mapid+"&&"+sLabel);
						if (reply.equals(UDigClientSocket.OK)) {
							sDetail = uinode.getNode().getDetail();
							sDetail += "\nLabel: "+uinode.getText()+" Sent To UDIG: "+sDate;
							try {
								uinode.getNode().setDetail(sDetail, sAuthor, sAuthor);
								if (oCode != null) {
									uinode.getNode().addCode(oCode);
								}								
							} catch (Exception io) {								
								ProjectCompendium.APP.displayError("Problem encountered: \n"+io.getMessage());								
							}
						}
					}
					
					if (!sNotSent.equals("")) {
						ProjectCompendium.APP.displayError("Label was not correctly formated.\n'Key = Value' format expected.\n\n"+sNotSent);
					}
				}					
			}
		}				
		else if (source.equals(miAssignMediaIndex)) {
			onAssignMediaIndex();
		}
		else if(source.equals(miMenuItemDelete)) {

			// delete all the selected nodes if user MULTISELECTs otherwise
			// delete node in focus
		 	// record the effect of the deletion
			// need to pass to this method the info you need to recreate the nodes/links

			DeleteEdit edit = new DeleteEdit(oViewPane.getViewFrame());
			if(oViewPane.getNumberOfSelectedNodes() >= 1) {
				oViewPane.deleteSelectedNodesAndLinks(edit);
			}
			else {
				oNode.deleteNodeAndLinks(oNode.getUINode(), edit);
			}

			// notify the listeners
			oViewPane.getViewFrame().getUndoListener().postEdit(edit);

			//Thread thread = new Thread() {
			//	public void run() {
					ProjectCompendium.APP.setTrashBinIcon();
			//	}
			//};
			//thread.start();
		}
		else if (source.equals(miFormatTransclusions)) {			
	   		int answer = JOptionPane.showConfirmDialog(this, "This action cannot be undone.\n\nAre you sure you wish to apply this node's formatting to all its transclusions?\n", "Warning",
					JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);

			if (answer == JOptionPane.YES_OPTION) {							
				NodePosition pos = oNode.getUINode().getNodePosition();
				IModel oModel = ProjectCompendium.APP.getModel();
				PCSession oSession = oModel.getSession();
				try {
					oModel.getViewService().updateTransclusionFormatting(oSession, pos.getNode().getId(),
						new Date(), pos.getShowTags(), pos.getShowText(), pos.getShowTrans(), 
						pos.getShowWeight(), pos.getShowSmallIcon(), pos.getHideIcon(),
						pos.getLabelWrapWidth(), pos.getFontSize(), pos.getFontFace(),
						pos.getFontStyle(), pos.getForeground(), pos.getBackground());
				} catch (SQLException e) {
					ProjectCompendium.APP.displayError("The node formats could node be updated due to: \n\n"+e.getMessage());
				}
				
				try {
					NodeSummary node = oNode.getUINode().getNode();
					JInternalFrame[] frames = ProjectCompendium.APP.getDesktop().getAllFrames();
					for(int i=0; i<frames.length; i++) {
						UIViewFrame viewFrame = (UIViewFrame)frames[i];
						if (viewFrame instanceof UIMapViewFrame) {
							UIViewPane pane = ((UIMapViewFrame)viewFrame).getViewPane();
							UINode uinode = (UINode)pane.get(node.getId());
							if (uinode != null) {
								NodePosition npos = uinode.getNodePosition();
								npos.setShowTags(pos.getShowTags());
								npos.setShowText(pos.getShowText());
								npos.setShowTrans(pos.getShowTrans());
								npos.setShowWeight(pos.getShowWeight());
								npos.setShowSmallIcon(pos.getShowSmallIcon());
								npos.setHideIcon(pos.getHideIcon());
								npos.setLabelWrapWidth(pos.getLabelWrapWidth());
								npos.setFontSize(pos.getFontSize());
								npos.setFontStyle(pos.getFontStyle());
								npos.setFontFace(pos.getFontFace());
								npos.setForeground(pos.getForeground());
								npos.setBackground(pos.getBackground());							
							}
						}
					}
				} catch(Exception ex) {
					ex.printStackTrace();					
				}
			}
		}
		else if (source.equals(miFormatAll)) {			
	   		int answer = JOptionPane.showConfirmDialog(this, "This action cannot be undone.\n\nAre you sure you wish to apply this node's formatting to all\nits child nodes and views and their nodes and views to full depth?\n", "Warning",
					JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);

			if (answer == JOptionPane.YES_OPTION) {							
				View view = (View)oNode.getUINode().getNode();
				NodePosition mainpos = oNode.getUINode().getNodePosition();		
				
				String sFontFace = mainpos.getFontFace();
				int nFontSize = mainpos.getFontSize();
				int nFontStyle = mainpos.getFontStyle();
				int nBackground = mainpos.getBackground();
				int nForeground = mainpos.getForeground();
				int nWrapWidth = mainpos.getLabelWrapWidth();
				boolean bShowTags = mainpos.getShowTags();
				boolean bShowText = mainpos.getShowText();
				boolean bShowTrans = mainpos.getShowTrans();
				boolean bShowWeight = mainpos.getShowWeight();
				boolean bSmallIcon = mainpos.getShowSmallIcon();
				boolean bHideIcon = mainpos.getHideIcon();
				
				IModel model = ProjectCompendium.APP.getModel();
				PCSession session = model.getSession();

				String sViewID = view.getId();
				
				htCheckFormatNodes.clear();
				htCheckFormatNodes.put(sViewID, sViewID);
				
				try {
					Vector vtNodes = new Vector();
					Enumeration e = view.getPositions();		
					int count = vtNodes.size();
					
					NodePosition pos = null;
					String sNextID = ""; 		
					for (Enumeration nodes = e; nodes.hasMoreElements();) {
						pos = (NodePosition) nodes.nextElement();
						sNextID = pos.getNode().getId();
					
						model.getViewService().updateFormatting(session, sViewID, sNextID,
									new Date(), bShowTags, bShowText, bShowTrans, bShowWeight, 
										bSmallIcon, bHideIcon, nWrapWidth, nFontSize, sFontFace,
											nFontStyle, nForeground, nBackground);
										
						pos.setBackground(nBackground);
						pos.setFontFace(sFontFace);
						pos.setFontStyle(nFontStyle);
						pos.setFontSize(nFontSize);
						pos.setForeground(nForeground);
						pos.setHideIcon(bHideIcon);
						pos.setLabelWrapWidth(nWrapWidth);
						pos.setShowSmallIcon(bSmallIcon);
						pos.setShowTags(bShowTags);
						pos.setShowText(bShowText);
						pos.setShowTrans(bShowTrans);
						pos.setShowWeight(bShowWeight);
						
						if (pos.getNode() instanceof View && !htCheckFormatNodes.containsKey(sNextID)) {
							this.setFormattting((View)pos.getNode(), bShowTags, bShowText, bShowTrans, bShowWeight, 
									bSmallIcon, bHideIcon, nWrapWidth, nFontSize, sFontFace,
									nFontStyle, nForeground, nBackground);
						}
					}
					
				} catch (SQLException e) {
					ProjectCompendium.APP.displayError("The node formats could node be updated due to: \n\n"+e.getMessage());
				} catch (ModelSessionException ex) {
					ProjectCompendium.APP.displayError("The node formats could node be updated due to: \n\n"+ex.getMessage());					
				}
			}
		}		
		else if(source.equals(miMenuItemShortCut)) {

			int nOffset = 55;

			Vector uinodes = new Vector(50);
			UIViewPane oViewPane = oNode.getUINode().getViewPane();
			ViewPaneUI oViewPaneUI = oViewPane.getViewPaneUI();

			// create shortcuts for all the selected nodes if user MULTISELECTs
			if(oViewPane.getNumberOfSelectedNodes() > 1) {

				for(Enumeration e = oViewPane.getSelectedNodes();e.hasMoreElements();) {

					UINode uinode = (UINode)e.nextElement();
					NodeUI nodeui = (uinode.getUI());
					ProjectCompendium.APP.setStatus("Making shortcut of  " + nodeui.getUINode().getNode().getLabel());

					int x = uinode.getX();
					int y = uinode.getY();
					UINode tmpuinode = oViewPaneUI.createShortCutNode(uinode, x+nOffset, y+nOffset);
					uinodes.addElement(tmpuinode);
				}

				oViewPane.setSelectedNode(null, ICoreConstants.DESELECTALL);
				oViewPane.setSelectedLink(null, ICoreConstants.DESELECTALL);

				for(int i=0;i<uinodes.size();i++) {

					UINode uinode = (UINode)uinodes.elementAt(i);
					uinode.requestFocus();
					uinode.setSelected(true);
					oViewPane.setSelectedNode(uinode,ICoreConstants.MULTISELECT);
				}

				ProjectCompendium.APP.setStatus("");
				oViewPane.getViewPaneUI().redraw();
			}
			else {
				UINode uinode = oNode.getUINode();
				int x = uinode.getX();
				int y = uinode.getY();

				Point pos = new Point(x, y);

				// MOVE THE MOUSE POINTER TO THE CORRECT POSITION
				try {
					Point mousepos = new Point(pos.x, pos.y);
					SwingUtilities.convertPointToScreen(mousepos, oViewPane);
					Robot rob = new Robot();

					// MOVE X AN Y FOR CUSRER SO NOT RIGHT ON EDGE OF NODE
					mousepos.x += 20;
					mousepos.y += 20;

					rob.mouseMove( mousepos.x+nOffset, mousepos.y+nOffset);
				}
				catch(AWTException ex) {}

				UINode shortNode = oViewPaneUI.createShortCutNode(uinode, pos.x+nOffset, pos.y+nOffset);

				oViewPane.setSelectedNode(null, ICoreConstants.DESELECTALL);
				oViewPane.setSelectedLink(null, ICoreConstants.DESELECTALL);

				shortNode.requestFocus();
				shortNode.setSelected(true);
				oViewPane.setSelectedNode(shortNode, ICoreConstants.SINGLESELECT);
			}
		}
		else if(source.equals(miMenuItemClone)) {

			int nOffset = 55;

			Hashtable cloneNodes = new Hashtable();
			Vector uinodes = new Vector(50);

			UIViewPane oViewPane = oNode.getUINode().getViewPane();
			ViewPaneUI oViewPaneUI = oViewPane.getViewPaneUI();

			if(oViewPane.getNumberOfSelectedNodes() > 1) {

				//delink all selected nodes if any
				for(Enumeration e = oViewPane.getSelectedNodes();e.hasMoreElements();) {
					UINode uinode = (UINode)e.nextElement();
					NodeUI nodeui = (uinode.getUI());
					ProjectCompendium.APP.setStatus("Cloning  " + nodeui.getUINode().getNode().getLabel());
					int x = uinode.getX();
					int y = uinode.getY();

					UINode tmpuinode = oViewPaneUI.createCloneNode(uinode, x+nOffset, y+nOffset);

					cloneNodes.put(uinode,tmpuinode);
					uinodes.addElement(tmpuinode);
				}

				oViewPane.setSelectedNode(null, ICoreConstants.DESELECTALL);

				for(int i=0;i<uinodes.size();i++) {
					UINode uinode = (UINode)uinodes.elementAt(i);
					uinode.requestFocus();
					uinode.setSelected(true);
					oViewPane.setSelectedNode(uinode,ICoreConstants.MULTISELECT);
				}
				ProjectCompendium.APP.setStatus("");
			}
			else {
				// clone the node
				UINode uinode = oNode.getUINode();
				int x = uinode.getX();
				int y = uinode.getY();

				Point pos = new Point(x, y);

				// MOVE THE MOUSE POINTER TO THE CORRECT POSITION
				try {
					Point mousepos = new Point(pos.x, pos.y);
					SwingUtilities.convertPointToScreen(mousepos, oViewPane);
					Robot rob = new Robot();

					// MOVE X AN Y FOR CUSRER SO NOT RIGHT ON EDGE OF NODE
					mousepos.x += 20;
					mousepos.y += 20;

					rob.mouseMove( mousepos.x+nOffset, mousepos.y+nOffset);
				}
				catch(AWTException ex) {}

				UINode cloneNode = oViewPaneUI.createCloneNode(uinode, pos.x+nOffset, pos.y+nOffset);

				oViewPane.setSelectedNode(null, ICoreConstants.DESELECTALL);
				oViewPane.setSelectedLink(null, ICoreConstants.DESELECTALL);

				cloneNode.requestFocus();
				cloneNode.setSelected(true);
				oViewPane.setSelectedNode(cloneNode, ICoreConstants.SINGLESELECT);
			}

			if (oViewPane.getNumberOfSelectedLinks() > 0) {
				Vector linkList = new Vector();
				for(Enumeration e = oViewPane.getSelectedLinks();e.hasMoreElements();) {
					UILink link = (UILink)e.nextElement();
					UINode uiFrom = link.getFromNode();
					UINode uiTo = link.getToNode();
					if ((cloneNodes.get(uiFrom) != null) && (cloneNodes.get(uiTo) != null) ) {
						UILink tmpLink = (uiFrom.getUI()).createLink(
									(UINode)cloneNodes.get(uiFrom),
									(UINode)cloneNodes.get(uiTo),
									link.getLink().getType(),
									link.getLink().getArrow());
						linkList.addElement(tmpLink);
					}
				}
				oViewPane.setSelectedLink(null, ICoreConstants.DESELECTALL);

				for(int i=0;i<linkList.size();i++) {
					UILink uiLink = (UILink)linkList.elementAt(i);
					uiLink.setSelected(true);
					oViewPane.setSelectedLink(uiLink,ICoreConstants.MULTISELECT);
				}
			}
			else {
				//System.out.println("Number of selected links is zero");
			}
		}
		else if(source.equals(miMenuItemCopy)) {

			UIViewPane oViewPane = oNode.getUINode().getViewPane();
			UINode uinode = oNode.getUINode();
			if (uinode.isSelected()) {
				uinode.getViewPane().getViewPaneUI().copyToClipboard(null);
			}
			else {
				uinode.getViewPane().getViewPaneUI().copyToClipboard(oNode);
			}
			uinode.requestFocus();
		}
		else if(source.equals(miMenuItemCut)) {
			UIViewPane oViewPane = oNode.getUINode().getViewPane();
			//select the node first and then cut it to clipboard.
			UINode uinode = oNode.getUINode();
			if (uinode.isSelected()) {
				uinode.getViewPane().getViewPaneUI().cutToClipboard(null);
			}
			else {
				uinode.getViewPane().getViewPaneUI().cutToClipboard(oNode);
			}
		}
		else if(source.equals(miMenuItemOpen)) {
			// open the node
			if(oNode.getUINode().getNode().getType() == ICoreConstants.TRASHBIN) {
				UITrashViewDialog dlgTrash = new UITrashViewDialog(ProjectCompendium.APP, oNode);
				UIUtilities.centerComponent(dlgTrash, ProjectCompendium.APP);
				dlgTrash .setVisible(true);
			}
			else {
				oNode.openEditDialog(false);
			}
			oNode.getUINode().requestFocus();
		}
		else if(source.equals(miMenuItemDelink)) {

			UIViewPane oViewPane = oNode.getUINode().getViewPane();
			// delete all the selected nodes if user MULTISELECTs
			if(oViewPane.getNumberOfSelectedNodes() > 1)
			{

				//delink all selected nodes if any
				for(Enumeration e = oViewPane.getSelectedNodes();e.hasMoreElements();) {
					UINode uinode = (UINode)e.nextElement();
					NodeUI nodeui = (uinode.getUI());
					ProjectCompendium.APP.setStatus("Delinking " + nodeui.getUINode().getNode().getLabel());

					nodeui.delink();
				}
				ProjectCompendium.APP.setStatus("");
			}
			else {
				// delink the node form all its links
				oNode.delink();
			}
			oNode.getUINode().requestFocus();
		}
		else if (source.equals(miFavorites)) {
			onCreateBookmark();
			oNode.getUINode().requestFocus();
		}
		else if (source.equals(miGoogleSearch)) {
			String sLabel = oNode.getUINode().getText();
			try {
				sLabel = CoreUtilities.cleanURLText(sLabel);
			} catch (Exception e) {}
			ExecuteControl.launch( "http://www.google.com/search?hl=en&lr=&ie=UTF-8&oe=UTF-8&q="+sLabel );
			oNode.getUINode().requestFocus();
		}
		else if (source.equals(miClaiConcepts)) {
			String sLabel = oNode.getUINode().getText();
			try {
				sLabel = CoreUtilities.cleanURLText(sLabel);
			} catch (Exception e) {}
			ExecuteControl.launch( claiMakerServer+"search-concept.php?op=search&inputWord="+sLabel );
			oNode.getUINode().requestFocus();
		}
		else if (source.equals(miClaiNeighbourhood)) {
			String sLabel = oNode.getUINode().getText();
			try {
				sLabel = CoreUtilities.cleanURLText(sLabel);
			} catch (Exception e) {}
			ExecuteControl.launch( claiMakerServer+"discover/neighborhood.php?op=search&concept="+sLabel );
			oNode.getUINode().requestFocus();
		}
		else if (source.equals(miClaiDocuments)) {
			String sLabel = oNode.getUINode().getText();
			try {
				sLabel = CoreUtilities.cleanURLText(sLabel);
			} catch (Exception e) {}
			ExecuteControl.launch( claiMakerServer+"search-document.php?op=search&Title="+sLabel );
			oNode.getUINode().requestFocus();
		}

		/*else if (source.equals(miMediaStream)) {
			NodeSummary nodeSum = oNode.getUINode().getNode();
			ProjectCompendium.APP.sendMediaStream(nodeSum);
			oNode.getUINode().requestFocus();
		}*/

		else if (source.equals(miMoveDetail)) {

			final UIViewPane oViewPane = oNode.getUINode().getViewPane();
			Thread thread = new Thread("UINodePopup.moveDetail") {
				public void run() {

					int count = 0;
					for(Enumeration e = oViewPane.getSelectedNodes();e.hasMoreElements();) {
						count++;
						UINode uinode = (UINode)e.nextElement();
						uinode.onMoveDetails();
					}

					// IF THERE ARE NO SELECTED NODE, TREAT IT AS THE CURRENT NODE
					if (count == 0) {
						oNode.getUINode().onMoveDetails();
					}

					ProjectCompendium.APP.setStatus("");
					oViewPane.getViewPaneUI().redraw();
					oNode.getUINode().requestFocus();
				}
			};
			thread.start();
		}
		else if (source.equals(miMoveLabel)) {

			final UIViewPane oViewPane = oNode.getUINode().getViewPane();

			Thread thread = new Thread("UINodePopup.moveLabel") {
				public void run() {
					int count = 0;
					for(Enumeration e = oViewPane.getSelectedNodes();e.hasMoreElements();) {
						count++;
						UINode uinode = (UINode)e.nextElement();
						uinode.onMoveLabel();
					}

					// IF THERE ARE NO SELECTED NODE, TREAT IT AS THE CURRENT NODE
					if (count == 0) {
						oNode.getUINode().onMoveLabel();
					}

					ProjectCompendium.APP.setStatus("");
					oViewPane.repaint();
					//oViewPane.getViewPaneUI().redraw();
					oNode.getUINode().requestFocus();
				}
			};
			thread.start();
		}
		else if(source.equals(miMenuItemViews)) {
			(oNode.getUINode()).showViewsDialog();
		}
		else if(source.equals(miMenuItemReaders)) {
			//Lakshmi (4/19/06) - code added to display Readers list Dialog
			String nodeId = oNode.getUINode().getNode().getId();
			UIReadersDialog readers = new UIReadersDialog(ProjectCompendium.APP, nodeId);
			UIUtilities.centerComponent(readers, ProjectCompendium.APP);
			readers.setVisible(true);
		}		
		else if(source.equals(miMenuItemMarkSeen)) {
			try {
				Enumeration e = oViewPane.getSelectedNodes();
				for(;e.hasMoreElements();){
					UINode node = (UINode) e.nextElement();
					NodeSummary oNode = node.getNode();
					oNode.setState(ICoreConstants.READSTATE);
				}
			}
			catch(Exception io) {
				System.out.println("Unable to mark as read");
			}			
			/*try {
				(oNode.getUINode()).getNode().setState(ICoreConstants.READSTATE);						
			} catch(Exception io) {
				System.out.println("Unable to mark as read");
			}*/
		}
		else if(source.equals(miMenuItemMarkUnseen)) {
			try {
				Enumeration e = oViewPane.getSelectedNodes();
				for(;e.hasMoreElements();){
					UINode node = (UINode) e.nextElement();
					NodeSummary oNode = node.getNode();
					oNode.setState(ICoreConstants.UNREADSTATE);
				}
			}
			catch(Exception io) {
				System.out.println("Unable to mark as un-read");
			}
			
			/*try {
				(oNode.getUINode()).getNode().setState(ICoreConstants.UNREADSTATE);
			} catch(Exception io) {
				System.out.println("Unable to mark as un-read");
			}*/
		}
		else if(source.equals(miMenuItemProperties)) {
			// show its properties
			(oNode.getUINode()).showPropertiesDialog();
		}
		else if(source.equals(miMenuItemEmpty)) {
			// empty the contents of the trashbin
			try {
				IModel model = ProjectCompendium.APP.getModel();
				PCSession session = model.getSession();
				String sAuthor = model.getUserProfile().getUserName();
				Vector vtNodes = model.getNodeService().getDeletedNodeSummary(session);
				if (vtNodes.size() > 0) {
					boolean deleted = ProjectCompendium.APP.getModel().getNodeService().purgeAllNodes(session, sAuthor);
					if (deleted)
						ProjectCompendium.APP.setTrashBinEmptyIcon();
				}
			}
			catch(Exception ex) {
				ProjectCompendium.APP.displayError("Exception: (UINodePopupMenu:actionPerformed) \n" + ex.getMessage());
			}
		}
		else if(source.equals(miTypeIssue)) {
			onChangeType(ICoreConstants.ISSUE);
		}
		else if(source.equals(miTypePosition)) {
			onChangeType(ICoreConstants.POSITION);
		}
		else if(source.equals(miTypeMap)) {

			int count = 0;
			for(Enumeration e = oViewPane.getSelectedNodes();e.hasMoreElements();) {
				count++;
				UINode uinode = (UINode)e.nextElement();
				onChangeTypeToMap(uinode);
			}

			// IF THERE ARE NO SELECTED NODE, TREAT IT AS THE CURRENT NODE
			if (count == 0) {
				onChangeTypeToMap(oNode.getUINode());
			}
		}
		else if(source.equals(miTypeList)) {

			int count = 0;
			for(Enumeration e = oViewPane.getSelectedNodes();e.hasMoreElements();) {
				count++;
				UINode uinode = (UINode)e.nextElement();
				onChangeTypeToList(uinode);
			}

			// IF THERE ARE NO SELECTED NODE, TREAT IT AS THE CURRENT NODE
			if (count == 0) {
				onChangeTypeToList(oNode.getUINode());
			}
		}
		else if(source.equals(miTypePro)) {
			onChangeType(ICoreConstants.PRO);
		}
		else if(source.equals(miTypeCon)) {
			onChangeType(ICoreConstants.CON);
		}
		else if(source.equals(miTypeArgument)) {
			onChangeType(ICoreConstants.ARGUMENT);
		}
		else if(source.equals(miTypeDecision)) {
			onChangeType(ICoreConstants.DECISION);
		}
		else if(source.equals(miTypeNote)) {
			onChangeType(ICoreConstants.NOTE);
		}
		else if(source.equals(miTypeReference)) {
			onChangeType(ICoreConstants.REFERENCE);
		}
		else if (source.equals(miNewMap)) {
			onNewMap();
		}

		ProjectCompendium.APP.setDefaultCursor();
	}

	/**
	 * Set the formatting of the past view and its child nodes and views to full depth to the pass paramters.
	 * @param view
	 * @param bShowTags
	 * @param bShowText
	 * @param bShowTrans
	 * @param bShowWeight
	 * @param bSmallIcon
	 * @param bHideIcon
	 * @param nWrapWidth
	 * @param nFontSize
	 * @param sFontFace
	 * @param nFontStyle
	 * @param nForeground
	 * @param nBackground
	 */
	private void setFormattting(View view, boolean bShowTags,
			boolean bShowText, boolean bShowTrans, boolean bShowWeight, boolean bSmallIcon,
			boolean bHideIcon, int nWrapWidth, int nFontSize, String sFontFace,
			int nFontStyle, int nForeground, int nBackground) throws ModelSessionException, SQLException {
		
		IModel model = ProjectCompendium.APP.getModel();
		PCSession session = model.getSession();
		
		if (!view.isMembersInitialized()) {
			view.initialize(model.getSession(), model);
			view.initializeMembers();				
		}
		
		String sViewID = view.getId();
		htCheckFormatNodes.put(sViewID, sViewID);
		
		Enumeration e = view.getPositions();					
		NodePosition pos = null;
		String sNextID = ""; 		
		for (Enumeration nodes = e; nodes.hasMoreElements();) {
			pos = (NodePosition) nodes.nextElement();
			sNextID = pos.getNode().getId();
			
			model.getViewService().updateFormatting(session, sViewID, sNextID,
			new Date(), bShowTags, bShowText, bShowTrans, bShowWeight, 
			bSmallIcon, bHideIcon, nWrapWidth, nFontSize, sFontFace,
			nFontStyle, nForeground, nBackground);
							
			pos.setBackground(nBackground);
			pos.setFontFace(sFontFace);
			pos.setFontStyle(nFontStyle);
			pos.setFontSize(nFontSize);
			pos.setForeground(nForeground);
			pos.setHideIcon(bHideIcon);
			pos.setLabelWrapWidth(nWrapWidth);
			pos.setShowSmallIcon(bSmallIcon);
			pos.setShowTags(bShowTags);
			pos.setShowText(bShowText);
			pos.setShowTrans(bShowTrans);
			pos.setShowWeight(bShowWeight);
			
			if (pos.getNode() instanceof View && !htCheckFormatNodes.containsKey(sNextID)) {
				this.setFormattting((View)pos.getNode(), bShowTags, bShowText, bShowTrans, bShowWeight, 
						bSmallIcon, bHideIcon, nWrapWidth, nFontSize, sFontFace,
						nFontStyle, nForeground, nBackground);
			}
		}
	}
	
	/**
	 * Change the selected nodes/current node to the given node type.
	 * (Map and List node types have separate functions).
	 *
	 * @param type, the type to change the selected nodes to.
	 * @see #onChangeTypeToList
	 * @see #onChangeTypeToMap
	 */
	private void onChangeType(int type) {

		int count = 0;
		for(Enumeration e = oViewPane.getSelectedNodes();e.hasMoreElements();) {
			count++;
			UINode uinode = (UINode)e.nextElement();
			uinode.setType(type);
			oViewPane.setSelectedNode(uinode, ICoreConstants.MULTISELECT);
			uinode.requestFocus();
		}

		// IF THERE ARE NO SELECTED NODE, TREAT IT AS THE CURRENT NODE
		if (count == 0) {
			UINode uinode = oNode.getUINode();
			uinode.setType(type);
			oViewPane.setSelectedNode(uinode,ICoreConstants.MULTISELECT);
			uinode.requestFocus();
		}
	}

	/**
	 * Assign the media index of the focused node to other selected nodes.
	 */
	private void onAssignMediaIndex() {

		UINode uiNode = oNode.getUINode();
		NodePosition oNodePos = uiNode.getNodePosition();
		IModel model = ProjectCompendium.APP.getModel(); 
		oNodePos.initialize(model.getSession(), model);		
		String id = uiNode.getNode().getId();
		String meetingid = ProjectCompendium.APP.oMeetingManager.getMeetingID();
		MediaIndex index = oNodePos.getMediaIndex(meetingid);

		if (index != null) {
			Date date = index.getMediaIndex();

			try {
				int count = 0;
				for(Enumeration e = oViewPane.getSelectedNodes();e.hasMoreElements();) {
					count++;
					UINode uinode = (UINode)e.nextElement();
					NodePosition nodePos = uinode.getNodePosition();
					nodePos.initialize(model.getSession(), model);
					if (!id.equals(nodePos.getNode().getId())) {
						MediaIndex ind = nodePos.getMediaIndex(meetingid);
						ind.setMediaIndex(date);
					}
				}
			}
			catch(Exception ex) {
				ProjectCompendium.APP.displayError("The following problem was encounter when saving the Media indexes\n\n"+ex.getMessage());
			}
		}
		else {
			ProjectCompendium.APP.displayError("Unable to retrieve MediaIndex of current node");
		}
	}

	/**
	 * Change the given node to a list type.
	 * @param uinode com.compendium.ui.UINode, the node to change the type for.
	 */
	private void onChangeTypeToList(UINode uinode) {

		if (uinode.setType(ICoreConstants.LISTVIEW)) {
			oViewPane.setSelectedNode(uinode,ICoreConstants.MULTISELECT);
			uinode.requestFocus();
		}
	}

	/**
	 * Change the given node to a map type.
	 * @param uinode com.compendium.ui.UINode, the node to change the type for.
	 */
	private void onChangeTypeToMap(UINode uinode) {

		if (uinode.setType(ICoreConstants.MAPVIEW)) {
			oViewPane.setSelectedNode(uinode,ICoreConstants.MULTISELECT);
			uinode.requestFocus();
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
	 * Set the UIViewPane associated with this popup menu.
	 * @param viewPane com.compendium.ui.UIViewPane, the UIViewPane associated with this popup menu.
	 */
	public void setViewPane(UIViewPane viewPane) {
		oViewPane = viewPane;
	}

	/**
	 * Handle the canceling of this popup. Set is to invisible.
	 */
	public void onCancel() {
		setVisible(false);
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
		
		UINode uinode = oNode.getUINode();		

		IModel model = ProjectCompendium.APP.getModel();
		UserProfile currentUser = model.getUserProfile();
		
		view.initialize(model.getSession(), model);
		
		UIViewPane oViewPane = uinode.getViewPane();		
		String sRef = ICoreConstants.sINTERNAL_REFERENCE+oViewPane.getView().getId()+"/"+uinode.getNode().getId();
		String sAuthor = ProjectCompendium.APP.getModel().getUserProfile().getUserName();
		try{
			NodePosition node = view.addMemberNode(ICoreConstants.REFERENCE,
					 "",
					 "",
					 sAuthor,
					 "GO TO: "+uinode.getText(),
					 "( "+oViewPane.getView().getLabel()+" )\n\n"+				 
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

		UINode uinode = oNode.getUINode();		
		double scale = uinode.getScale();

		UINode newNode = null;

		UIViewPane oViewPane = uinode.getViewPane();
		View view = oViewPane.getView();
		
		String sRef = ICoreConstants.sINTERNAL_REFERENCE+view.getId()+"/"+uinode.getNode().getId();

		// Do all calculations at 100% scale and then scale back down if required.
		if (oViewPane != null) {
			
			if (scale != 1.0) {
				oViewPane.scaleNode(uinode, 1.0);
			}
			
			ViewPaneUI oViewPaneUI = oViewPane.getViewPaneUI();
			if (oViewPaneUI != null) {

				int parentHeight = uinode.getHeight();
				int parentWidth = uinode.getWidth();

				Point loc = uinode.getNodePosition().getPos();
				loc.x += parentWidth;
				loc.x += 100;

				// CREATE NEW NODE RIGHT OF THE GIVEN NODE WITH THE GIVEN LABEL
				newNode = oViewPaneUI.createNode(ICoreConstants.REFERENCE,
								 "",
								 ProjectCompendium.APP.getModel().getUserProfile().getUserName(),
								 "GO TO: "+uinode.getText(),
								 "( "+view.getLabel()+" )",
								 loc.x,
								 loc.y,
								 sRef
								 );
				
				if (scale != 1.0) {
					oViewPane.scaleNode(newNode, 1.0);
				}

				//Adjust y location for height variation so new node centered.
				int childHeight = newNode.getHeight();

				int locy = 0;
				if (parentHeight > childHeight) {
					locy = loc.y + ((parentHeight-childHeight)/2);
				}
				else if (childHeight > parentHeight) {
					locy = loc.y - ((childHeight-parentHeight)/2);
				}

				if (locy > 0 && locy != loc.y) {
					loc.y = locy;
					(newNode.getNodePosition()).setPos(loc);
					try {
						oViewPane.getView().setNodePosition(newNode.getNode().getId(), loc);
					}
					catch(Exception ex) {
						System.out.println(ex.getMessage());
					}
				}
				if (scale != 1.0) {
					oViewPane.scaleNode(newNode, scale);
				}
			}
			
			if (scale != 1.0) {
				oViewPane.scaleNode(uinode, scale);
			}			
		}
	}		
	
	/**
	 * Create a Reference node with internal link to this node.
	 */
	private void onCreateBookmark() {

		UINode uinode = oNode.getUINode();	
		NodeSummary node = uinode.getNode();
		UIViewPane oViewPane = uinode.getViewPane();
		View view = oViewPane.getView();		
		ProjectCompendium.APP.createFavorite(node.getId(), view.getId(), view.getLabel()+"&&&"+node.getLabel(), node.getType());
	}
	
	/**
	 * Transclude this node into a new map and link map to this node.
	 */
	private void onNewMap() {

		int count = 0;
		boolean thisNodeIncluded = false;

		for(Enumeration e = oViewPane.getSelectedNodes();e.hasMoreElements();) {
			count++;
			UINode uinode = (UINode)e.nextElement();

			UINode newMap = UIUtilities.createNodeAndLinkRight(uinode, ICoreConstants.MAPVIEW, 100, uinode.getText(), ProjectCompendium.APP.getModel().getUserProfile().getUserName(), ICoreConstants.EXPANDS_ON_LINK);

			if (newMap != null) {
				int x = 10;
				int y = 250;
				try {
					((View)newMap.getNode()).addNodeToView(uinode.getNode(), x, y);
				}
				catch(Exception ex) {
					System.out.println("Error: (UINodePopupMenu.onNewMap)\n\n"+ex.getMessage());
				}
			}
		}
	}
	
	/**
	 * Handle a Questmap import request.
	 * @param showViewList, true if importing to mulitpl views, else false.
	 */
	public void onImportFile(boolean showViewList) {
		UIImportDialog uid = new UIImportDialog(ProjectCompendium.APP, showViewList);
		uid.setViewPaneUI(oViewPaneUI);
		uid.setVisible(true);
	}

	/**
	 * Exports the current view to a HTML outline file.
	 */
	public void onExportFile() {
		UIExportDialog export = new UIExportDialog(ProjectCompendium.APP, oViewPane.getViewFrame());
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

        UIExportViewDialog dialog2 = new UIExportViewDialog(ProjectCompendium.APP, oViewPane.getViewFrame());
        UIUtilities.centerComponent(dialog2, ProjectCompendium.APP);
		dialog2.setVisible(true);
	}	
}
