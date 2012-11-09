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

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.Document;

import com.compendium.core.ICoreConstants;
import com.compendium.core.datamodel.NodePosition;
import com.compendium.core.datamodel.NodeSummary;
import com.compendium.core.datamodel.View;

import com.compendium.ProjectCompendium;
import com.compendium.ui.plaf.*;
import com.compendium.ui.*;
import com.compendium.ui.dialogs.*;

/**
 * This class draws and handles the events for a List's right-click popup menu.
 *
 * @author	Mohammed Sajid Ali / Michelle Bachler / Lakshmi Prabhakaran
 */
public class UIViewPopupMenuForList extends JPopupMenu implements ActionListener{

	/** The default width for this popup menu.*/
	private static final int WIDTH		= 100;

	/** The default height for this popup menu.*/
	private static final int HEIGHT		= 300;

	/** The JMenu item for the Nodes creation option.*/
	private JMenu			mnuNodes				= null;

	/** The JMenuItem to perform a paste operation.*/
	private JMenuItem		miMenuItemPaste			= null;

	/** The JMenuItem to perform a copy operation.*/
	private JMenuItem		miMenuItemCopy			= null;

	/** The JMenuItem to perform a cut operation.*/
	private JMenuItem		miMenuItemCut			= null;

	/** The JMenuItem to open this view's contents dialog.*/
	private JMenuItem		miMenuItemOpen			= null;
	
	/** The  JMenuItem to mark the nodes as read*/ 
	private JMenuItem		miMenuItemMarkSeen 		= null;

	/**The  JMenuItem to mark the nodes as unread*/
	private JMenuItem		miMenuItemMarkUnseen 	= null;

	/** The JMenuItem to view the properties details for this view.*/
	private JMenuItem		miMenuItemProperties	= null;

	/** The JMenuItem to view the parent Views for this view.*/
	private JMenuItem		miMenuItemViews			= null;

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

	/** The JMenu which holds the import options.*/
	private JMenu			mnuImport				= null;

	/** The JMenu which holds the export options.*/
	private JMenu			mnuExport				= null;

	/** The JMenu which holds the import options.*/
	private JMenu			miFileImport			= null;

	/** The JMenuItem to import Questmap into the current View.*/
	private JMenuItem		miImportCurrentView		= null;

	/** The JMenuItem to import Questmap into multiple Views.*/
	private JMenuItem		miImportMultipleViews	= null;

	/** The JMenuItem to import XML.*/
	private JMenuItem		miImportXMLView			= null;
	
	/** The menu item to import Flashmeeting XML.*/
	private JMenuItem		miImportXMLFlashmeeting = null;
	
	/** The JMenuItem to export to a HTML Outline.*/
	private JMenuItem		miExportHTMLOutline		= null;

	/** The JMenuItem to export to a HTML Views.*/
	private JMenuItem		miExportHTMLView		= null;

	/** The JMenuItem to export to XML.*/
	private JMenuItem		miExportXMLView			= null;
	
	/** The menu item to export a HTML view with the XML included.*/
	private JMenuItem		miExportHTMLViewXML		= null;	
	
	/** The JMenu to send information to IX Panels.*/
	private JMenu			mnuSendToIX				= null;

	/** The JMenu to send information to a Jabber client.*/
	private JMenu			mnuSendToJabber			= null;

	/** The JMenu to holds links to Reference nodes contained in the current view.*/
	private JMenu			mnuRefNodes				= null;

	/** The x value for the location of this popup menu.*/
	private int				nX						= 0;

	/** The y value for the location of this popup menu.*/
	private int				nY						= 0;

	/** The height for this popup menu.*/
	private int				nHeight					= HEIGHT;

	/** The width for this popup menu.*/
	private int				nWidth					= WIDTH;

	/** The associated ListUI object for this popup menu.*/
	private ListUI listUI;

	/** The platform specific shortcut key used to access menus and thier options.*/
	private int shortcutKey;


	/**
	 * Constructor. Create the menus and items and draws the popup menu.
	 * @param title the title for this popup menu.
	 * @param listUI com.compendium.ui.plaf.ListUI, the associated list for this popup menu.
	 */
	public UIViewPopupMenuForList(String title, ListUI listUI) {

		super(title);

		shortcutKey = ProjectCompendium.APP.shortcutKey;

		this.listUI = listUI;
		View view = listUI.getUIList().getView();
		String sViewID = view.getId();
		String sInBoxID = ProjectCompendium.APP.getInBoxID();
			
		Vector refNodes = view.getReferenceNodes();

		mnuNodes = new JMenu("Create Node");
		//mnuNodes.setMnemonic(KeyEvent.VK_N);

		miMenuItemIssue = new JMenuItem("Q,?,/  Question Node", UIImages.getNodeIcon(IUIConstants.ISSUE_SM_ICON)); // issue renamed to question
		miMenuItemIssue.addActionListener(this);
		miMenuItemIssue.setMnemonic(KeyEvent.VK_Q);
		mnuNodes.add(miMenuItemIssue);
		miMenuItemPosition = new JMenuItem("I,A,P,!,1  Answer Node", UIImages.getNodeIcon(IUIConstants.POSITION_SM_ICON)); //position renamed to answer
		miMenuItemPosition.addActionListener(this);
		miMenuItemPosition.setMnemonic(KeyEvent.VK_A);
		mnuNodes.add(miMenuItemPosition);
		mnuNodes.addSeparator();

		miMenuItemMap = new JMenuItem("M  Map Node", UIImages.getNodeIcon(IUIConstants.MAP_SM_ICON));
		miMenuItemMap.addActionListener(this);
		miMenuItemMap.setMnemonic(KeyEvent.VK_M);
		mnuNodes.add(miMenuItemMap);
		miMenuItemList = new JMenuItem("L  List Node", UIImages.getNodeIcon(IUIConstants.LIST_SM_ICON));
		miMenuItemList.addActionListener(this);
		miMenuItemList.setMnemonic(KeyEvent.VK_L);
		mnuNodes.add(miMenuItemList);
		mnuNodes.addSeparator();

		miMenuItemPro = new JMenuItem("+,=  Pro Node", UIImages.getNodeIcon(IUIConstants.PRO_SM_ICON));
		miMenuItemPro.addActionListener(this);
		miMenuItemPro.setMnemonic(KeyEvent.VK_P);
		mnuNodes.add(miMenuItemPro);
		miMenuItemCon = new JMenuItem("-  Con Node", UIImages.getNodeIcon(IUIConstants.CON_SM_ICON));
		miMenuItemCon.addActionListener(this);
		miMenuItemCon.setMnemonic(KeyEvent.VK_C);
		mnuNodes.add(miMenuItemCon);
		mnuNodes.addSeparator();

		miMenuItemReference = new JMenuItem("R  Reference Node", UIImages.getNodeIcon(IUIConstants.REFERENCE_SM_ICON));
		miMenuItemReference.addActionListener(this);
		miMenuItemReference.setMnemonic(KeyEvent.VK_R);
		mnuNodes.add(miMenuItemReference);

		miMenuItemNote = new JMenuItem("N  Note Node", UIImages.getNodeIcon(IUIConstants.NOTE_SM_ICON));
		miMenuItemNote.addActionListener(this);
		miMenuItemNote.setMnemonic(KeyEvent.VK_N);
		mnuNodes.add(miMenuItemNote);
		miMenuItemDecision = new JMenuItem("D  Decision Node", UIImages.getNodeIcon(IUIConstants.DECISION_SM_ICON));
		miMenuItemDecision.addActionListener(this);
		miMenuItemDecision.setMnemonic(KeyEvent.VK_D);
		mnuNodes.add(miMenuItemDecision);
		miMenuItemArgument = new JMenuItem("U  Argument Node", UIImages.getNodeIcon(IUIConstants.ARGUMENT_SM_ICON));
		miMenuItemArgument.addActionListener(this);
		miMenuItemArgument.setMnemonic(KeyEvent.VK_U);
		mnuNodes.add(miMenuItemArgument);

		add(mnuNodes);

		addSeparator();

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

		//
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

		miMenuItemPaste = new JMenuItem("Paste", UIImages.get(IUIConstants.PASTE_ICON));
		miMenuItemPaste.setAccelerator(KeyStroke.getKeyStroke( KeyEvent.VK_V, shortcutKey));
		miMenuItemPaste.addActionListener(this);
		miMenuItemPaste.setMnemonic(KeyEvent.VK_A);
		add(miMenuItemPaste);

		addSeparator();
		//

		// create IMPORT options
		mnuImport = new JMenu("Import");
		mnuImport.setMnemonic(KeyEvent.VK_I);

		miImportXMLView = new JMenuItem("XML File...");
		miImportXMLView.setMnemonic(KeyEvent.VK_X);
		miImportXMLView.addActionListener(this);

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

		miImportXMLFlashmeeting = new JMenuItem("FlashMeeting XML...");
		miImportXMLFlashmeeting.setMnemonic(KeyEvent.VK_F);
		miImportXMLFlashmeeting.addActionListener(this);
		mnuImport.add(miImportXMLFlashmeeting);

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
		
		miMenuItemMarkSeen = new JMenuItem("Mark Seen");
		miMenuItemMarkSeen.addActionListener(this);
		miMenuItemMarkSeen.setMnemonic(KeyEvent.VK_M);
		miMenuItemMarkSeen.setEnabled(false);
		add(miMenuItemMarkSeen);

		miMenuItemMarkUnseen = new JMenuItem("Mark Unseen");
		miMenuItemMarkUnseen.addActionListener(this);
		miMenuItemMarkUnseen.setMnemonic(KeyEvent.VK_N);
		add(miMenuItemMarkUnseen);
		miMenuItemMarkUnseen.setEnabled(false);
		
		Enumeration e = listUI.getUIList().getSelectedNodes();
		for(;e.hasMoreElements();){
			NodePosition np = (NodePosition) e.nextElement();
			NodeSummary oNode = np.getNode();
			int state = oNode.getState();
			if(state == ICoreConstants.READSTATE){
				miMenuItemMarkUnseen.setEnabled(true);
			} else if(state == ICoreConstants.UNREADSTATE) {
				miMenuItemMarkSeen.setEnabled(true);
			}
		}

		// SEND TO OPTIONS
		boolean addSep = false;
		if (ProjectCompendium.APP.jabber != null &&
									ProjectCompendium.APP.jabber.getRoster().hasMoreElements()) {
			addSep = true;
			mnuSendToJabber = new JMenu("Send To Jabber");
			mnuSendToJabber.setMnemonic(KeyEvent.VK_J);
			mnuSendToJabber.setEnabled(false);
			add(mnuSendToJabber);

			ProjectCompendium.APP.drawJabberRoster( mnuSendToJabber );
		}

		if (ProjectCompendium.APP.ixPanel != null &&
									ProjectCompendium.APP.ixPanel.getRoster().hasMoreElements()) {

			addSep = true;
			mnuSendToIX = new JMenu("Send To IX");
			mnuSendToIX.setMnemonic(KeyEvent.VK_X);
			mnuSendToIX.setEnabled(false);
			add(mnuSendToIX);
			ProjectCompendium.APP.drawIXRoster( mnuSendToIX );
		}

		// END IMPORT / EXPORT OPTIONS

		if ( addSep )
		addSeparator();

		if (!sInBoxID.equals(sViewID)) {
			miMenuItemOpen = new JMenuItem("Contents");
			miMenuItemOpen.addActionListener(this);
			miMenuItemOpen.setMnemonic(KeyEvent.VK_O);
			add(miMenuItemOpen);
	
			miMenuItemProperties = new JMenuItem("Properties");
			miMenuItemProperties.setMnemonic(KeyEvent.VK_P);
			miMenuItemProperties.addActionListener(this);
			add(miMenuItemProperties);
	
			miMenuItemViews = new JMenuItem("Views");
			miMenuItemViews.setMnemonic(KeyEvent.VK_W);
			miMenuItemViews.addActionListener(this);
			add(miMenuItemViews);
		}

	 	// If on the Mac OS and the Menu bar is at the top of the OS screen, remove the menu shortcut Mnemonics.
		if (ProjectCompendium.isMac && (FormatProperties.macMenuBar || (!FormatProperties.macMenuBar && !FormatProperties.macMenuUnderline)) )
			UIUtilities.removeMenuMnemonics(getSubElements());

		pack();
		setSize(nWidth,nHeight);
	}

	/**
	 * Handles the event of an option being selected.
	 * @param evt the event associated with the option being selected.
	 */
	public void actionPerformed(ActionEvent evt) {

		ProjectCompendium.APP.setWaitCursor();

		Object source = evt.getSource();
		nY = (listUI.getUIList().getNumberOfNodes() + 1) * 10;

		if(source.equals(miMenuItemCopy)) {
			ProjectCompendium.APP.onEditCopy();
		}
		else if(source.equals(miMenuItemCut)) {
			ProjectCompendium.APP.onEditCut();
		}
		else if(source.equals(miMenuItemPaste)) {
			ProjectCompendium.APP.onEditPaste();
		}
		else if (source.equals(miImportCurrentView))
			onImportFile(false);
		else if (source.equals(miImportMultipleViews))
			onImportFile(true);

		else if (source.equals(miExportHTMLOutline))
			onExportFile();
		else if (source.equals(miExportHTMLView))
			onExportView();

		else if (source.equals(miExportXMLView)) {
			onXMLExport(false); 
		} else if (source.equals(miExportHTMLViewXML)) {
			ProjectCompendium.APP.onFileExportPower();		
		} else if (source.equals(miImportXMLView)) {
			onXMLImport();
		} else if (source.equals(miImportXMLFlashmeeting)) {
			UIImportFlashMeetingXMLDialog dlg = new UIImportFlashMeetingXMLDialog(ProjectCompendium.APP);
			UIUtilities.centerComponent(dlg, ProjectCompendium.APP);
			dlg.setVisible(true);									
		} else if(source.equals(miMenuItemMarkSeen)) {
			try {
				Enumeration e = listUI.getUIList().getSelectedNodes();
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
				Enumeration e = listUI.getUIList().getSelectedNodes();
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

		else if(source.equals(miMenuItemOpen)) {
			listUI.getUIList().getViewFrame().showEditDialog();
		}
		else if(source.equals(miMenuItemProperties)) {
			listUI.getUIList().getViewFrame().showPropertiesDialog();
		}
		else if(source.equals(miMenuItemViews)) {
			listUI.getUIList().getViewFrame().showViewsDialog();
		}
		else if(source.equals(miMenuItemArgument)) {
			createNode(ICoreConstants.ARGUMENT);
		}
		else if(source.equals(miMenuItemCon)) {
			createNode(ICoreConstants.CON);
		}
		else if(source.equals(miMenuItemIssue)) {
			createNode(ICoreConstants.ISSUE);
		}
		else if(source.equals(miMenuItemPosition)) {
			createNode(ICoreConstants.POSITION);
		}
		else if(source.equals(miMenuItemPro)) {
			createNode(ICoreConstants.PRO);
		}
		else if(source.equals(miMenuItemDecision)) {
			createNode(ICoreConstants.DECISION);
		}
		else if(source.equals(miMenuItemNote)) {
			createNode(ICoreConstants.NOTE);
		}
		else if(source.equals(miMenuItemReference)) {
			createNode(ICoreConstants.REFERENCE);
		}
		else if(source.equals(miMenuItemList)) {
			createNode(ICoreConstants.LISTVIEW);
		}
		else if(source.equals(miMenuItemMap)) {
			createNode(ICoreConstants.MAPVIEW);
		}

		ProjectCompendium.APP.setDefaultCursor();
	}

	/**
	 * Process a node creation request.
	 * @param nType the type of the new node to create.
	 */
	private void createNode(int nType) {
		listUI.createNode(nType,
						 "",
						 ProjectCompendium.APP.getModel().getUserProfile().getUserName(),
						 "",
						 "",
						 nX,
						 nY
						 );

		UIList uiList = listUI.getUIList();
		uiList.updateTable();
		uiList.selectNode(uiList.getNumberOfNodes() - 1, ICoreConstants.MULTISELECT);
	}

	/**
	 * Set the location to draw this popup menu at.
	 * @param x the x position of this popup's location.
	 * @param y the y position of this popup's location.
	 */
	public void setCoordinates(int x,int y) {
		nX = x;
		nY = y;
	}

	/**
	 * Handle the cancelleing of this popup. Set is to invisible.
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
		uid.setUIList(listUI.getUIList());
		uid.setVisible(true);
	}

	/**
	 * Exports the current view to a HTML outline file.
	 */
	public void onExportFile() {

		UIExportDialog export = new UIExportDialog(ProjectCompendium.APP, listUI.getUIList().getViewFrame());
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
        UIExportViewDialog dialog2 = new UIExportViewDialog(ProjectCompendium.APP, listUI.getUIList().getViewFrame());
		UIUtilities.centerComponent(dialog2, ProjectCompendium.APP);
		dialog2.setVisible(true);
	}
}
