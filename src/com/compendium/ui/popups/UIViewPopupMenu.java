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

import com.compendium.core.*;
import com.compendium.core.datamodel.*;

import com.compendium.ui.*;
import com.compendium.ui.plaf.*;
import com.compendium.ui.dialogs.*;
import com.compendium.ui.stencils.*;
import com.compendium.ProjectCompendium;

import com.compendium.io.jabber.*;
import com.compendium.io.udig.UDigClientSocket;

/**
 * This class draws the right-click menu for map views
 *
 * @author	Mohammed Sajid Ali / Michelle Bachler / Lakshmi Prabhakaran
 */
public class UIViewPopupMenu extends JPopupMenu implements ActionListener{

	/** The default width for this popup menu.*/
	private static final int WIDTH		= 100;

	/** The default height for this popup menu.*/
	private static final int HEIGHT		= 300;

	/** The JMenu for node type change options.*/
	private JMenu			mnuChangeType		= null;

	/** The JMenuItem to change the selected nodes to Argument nodes.*/
	private JMenuItem		miTypeArgument		= null;

	/** The JMenuItem to change the selected nodes to Con nodes.*/
	private JMenuItem		miTypeCon			= null;

	/** The JMenuItem to change the selected nodes to Issue nodes.*/
	private JMenuItem		miTypeIssue			= null;

	/** The JMenuItem to change the selected nodes to Position nodes.*/
	private JMenuItem		miTypePosition		= null;

	/** The JMenuItem to change the selected nodes to Pro nodes.*/
	private JMenuItem		miTypePro			= null;

	/** The JMenuItem to change the selected nodes to Decision nodes.*/
	private JMenuItem		miTypeDecision		= null;

	/** The JMenuItem to change the selected nodes to Note nodes.*/
	private JMenuItem		miTypeNote			= null;

	/** The JMenuItem to change the selected nodes to Refrence nodes.*/
	private JMenuItem		miTypeReference		= null;

	/** The JMenuItem to change the selected nodes to List nodes.*/
	private JMenuItem		miTypeList			= null;

	/** The JMenuItem to change the selected nodes to Map nodes.*/
	private JMenuItem		miTypeMap			= null;

	/** The JMenu item for the Nodes creation option.*/
	private JMenu			mnuNodes				= null;

	/** The JMenuItem to perform a paste operation.*/
	private JMenuItem		miMenuItemPaste			= null;

	/** The JMenuItem to perform a copy operation.*/
	private JMenuItem		miMenuItemCopy			= null;

	/** The JMenuItem to perform a cut operation.*/
	private JMenuItem		miMenuItemCut			= null;

	/** The JMenu to perform a arrange operation.*/
	private JMenu			mnuArrange		= null;

	/** The JMenuItem to perform a arrange operation.*/
	private JMenuItem	miMenuItemLeftRightArrange	= null;

	/** The JMenuItem to perform a arrange operation.*/
	private JMenuItem    miMenuItemTopDownArrange	= null;

	/** The JMenu to perform a arrange operation.*/
	private JMenu			mnuViewAlign		= null;

	/** The JMenuItem to perform a arrange operation.*/
	private JMenuItem	miMenuItemAlignTop	= null;

	/** The JMenuItem to perform a arrange operation.*/
	private JMenuItem    miMenuItemAlignMiddle	= null;

	/** The JMenuItem to perform a arrange operation.*/
	private JMenuItem	miMenuItemAlignBottom	= null;

	/** The JMenuItem to perform a arrange operation.*/
	private JMenuItem    miMenuItemAlignLeft	= null;

	/** The JMenuItem to perform a arrange operation.*/
	private JMenuItem	miMenuItemAlignCenter	= null;

	/** The JMenuItem to perform a arrange operation.*/
	private JMenuItem    miMenuItemAlignRight	= null;

	/** The JMenuItem to open this view's contents dialog.*/
	private JMenuItem		miMenuItemOpen			= null;

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
	
	/** The menu item to export a HTML view with the XML included.*/
	private JMenuItem		miExportHTMLViewXML		= null;	

	/** The JMenuItem to import XML.*/
	private JMenuItem		miImportXMLView			= null;
	
	/** The menu item to import Flashmeeting XML.*/
	private JMenuItem		miImportXMLFlashmeeting = null;
	
	/** The JMenuItem to import an external folder of image files into the current map.*/
	private JMenuItem		miImportImageFolder		= null;

	/** JMenuItem to export the current map to a Jpge file.*/
	private JMenuItem		miSaveAsJpeg			= null;

	/** The JMenu to send information to IX Panels.*/
	private JMenu			mnuSendToIX				= null;

	/** The JMenu to send information to a Jabber client.*/
	private JMenu			mnuSendToJabber			= null;

	/** The JMenu to holds links to Reference nodes contained in the current view.*/
	private JMenu			mnuRefNodes				= null;

	/** The JMenuItem to open the Link edit dialog for the selected links.*/
	private JMenuItem		miEditLink				= null;

	/** The JMenuItem to move the selected nodes' detail text inot their labels.*/
	private JMenuItem		miMoveDetail			= null;

	/** The JMenuItem to move the selected nodes' label text inot their detail.*/
	private JMenuItem		miMoveLabel				= null;
	
	/** The  JMenuItem to mark the nodes as read*/ 
	private JMenuItem		miMenuItemMarkSeen 		= null;

	/**The  JMenuItem to mark the nodes as unread*/
	private JMenuItem		miMenuItemMarkUnseen 	= null;


	/** The stencil menu*/
	private JMenu			mnuStencils				= null;

	/** The menu item to open the stencil  management dialog.*/
	private JMenuItem		miStencilManagement		= null;
	
	/** The menu item to add node label as property to parent udig map point in UDIG.*/
	private JMenuItem		miUDIGProperty			= null;	

	/** The x value for the location of this popup menu.*/
	private int				nX						= 0;

	/** The y value for the location of this popup menu.*/
	private int				nY						= 0;

	/** The height for this popup menu.*/
	private int				nHeight					= HEIGHT;

	/** The width for this popup menu.*/
	private int				nWidth					= WIDTH;

	/** The ViewPaneUI associated with this popup menu.*/
	private ViewPaneUI		oViewPaneUI			= null;

	/** The UIViewPane associated with this popup menu.*/
	private UIViewPane		oViewPane			= null;

	/** The platform specific shortcut key used to access menus and thier options.*/
	private int shortcutKey;

	/**
	 * Constructor. Create the menus and items and draws the popup menu.
	 * @param title, the title for this popup menu.
	 * @param viewpaneUI com.compendium.ui.plaf.ViewPaneUI, the associated map for this popup menu.
	 */
	public UIViewPopupMenu(String title, ViewPaneUI viewpaneUI) {

		super(title);

		setViewPaneUI(viewpaneUI);

		View view = viewpaneUI.getViewPane().getView();
		Vector refNodes = view.getReferenceNodes();

		shortcutKey = ProjectCompendium.APP.shortcutKey;

// Begin edit, Lakshmi (11/3/05)
// include Top - Down and Left - Right Option in Arrange Menu.
		mnuArrange = new JMenu("Arrange");
		mnuArrange.setMnemonic(KeyEvent.VK_R);
		mnuArrange.addActionListener(this);

		miMenuItemLeftRightArrange = new JMenuItem("Left To Right");
		miMenuItemLeftRightArrange.addActionListener(this);
		miMenuItemLeftRightArrange.setMnemonic(KeyEvent.VK_R);
		mnuArrange.add(miMenuItemLeftRightArrange);

		mnuArrange.addSeparator();

		miMenuItemTopDownArrange = new JMenuItem("Top-Down");
		miMenuItemTopDownArrange.addActionListener(this);
		miMenuItemTopDownArrange.setMnemonic(KeyEvent.VK_W);
		mnuArrange.add(miMenuItemTopDownArrange);

		add(mnuArrange);

		mnuViewAlign = new JMenu("Align");
		mnuViewAlign.setMnemonic(KeyEvent.VK_A);
		mnuViewAlign.setEnabled(true);

		miMenuItemAlignLeft = new JMenuItem("Left");
		miMenuItemAlignLeft.addActionListener(this);
		miMenuItemAlignLeft.setMnemonic(KeyEvent.VK_L);
		mnuViewAlign.add(miMenuItemAlignLeft);

		miMenuItemAlignCenter = new JMenuItem("Center");
		miMenuItemAlignCenter.addActionListener(this);
		miMenuItemAlignCenter.setMnemonic(KeyEvent.VK_C);
		mnuViewAlign.add(miMenuItemAlignCenter);

		miMenuItemAlignRight = new JMenuItem("Right");
		miMenuItemAlignRight.addActionListener(this);
		miMenuItemAlignRight.setMnemonic(KeyEvent.VK_R);
		mnuViewAlign.add(miMenuItemAlignRight);

		mnuViewAlign.addSeparator();

		miMenuItemAlignTop = new JMenuItem("Top");
		miMenuItemAlignTop.addActionListener(this);
		miMenuItemAlignTop.setMnemonic(KeyEvent.VK_T);
		mnuViewAlign.add(miMenuItemAlignTop);

		miMenuItemAlignMiddle = new JMenuItem("Middle");
		miMenuItemAlignMiddle.addActionListener(this);
		miMenuItemAlignMiddle.setMnemonic(KeyEvent.VK_M);
		mnuViewAlign.add(miMenuItemAlignMiddle);

		miMenuItemAlignBottom = new JMenuItem("Bottom");
		miMenuItemAlignBottom.addActionListener(this);
		miMenuItemAlignBottom.setMnemonic(KeyEvent.VK_B);
		mnuViewAlign.add(miMenuItemAlignBottom);

		add(mnuViewAlign);

		addSeparator();

		mnuNodes = new JMenu("Create Node");
		mnuNodes.setMnemonic(KeyEvent.VK_N);

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

		mnuChangeType = new JMenu("Change Type To ...");
		mnuChangeType.addActionListener(this);
		mnuChangeType.setMnemonic(KeyEvent.VK_Y);
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

		miStencilManagement = new JMenuItem("Manage Stencils...");
		miStencilManagement.setMnemonic(KeyEvent.VK_G);
		miStencilManagement.addActionListener(this);
		add(miStencilManagement);

		mnuStencils	= new JMenu("Open Stencil");
		mnuStencils.setMnemonic(KeyEvent.VK_E);
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
		
		String sParentSource = view.getSource();
		if (sParentSource.startsWith("UDIG") && FormatProperties.startUDigCommunications) {
			miUDIGProperty = new JMenuItem("Add Selected Labels to Parent map point", UIImages.get(IUIConstants.UDIG_ICON));
			miUDIGProperty.addActionListener(this);
			miUDIGProperty.setMnemonic(KeyEvent.VK_I);
			add(miUDIGProperty);	
			addSeparator();
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

		miMenuItemPaste = new JMenuItem("Paste", UIImages.get(IUIConstants.PASTE_ICON));
		miMenuItemPaste.setAccelerator(KeyStroke.getKeyStroke( KeyEvent.VK_V, shortcutKey));
		miMenuItemPaste.addActionListener(this);
		miMenuItemPaste.setMnemonic(KeyEvent.VK_A);
		add(miMenuItemPaste);

		addSeparator();
		//

		miEditLink = new JMenuItem("Edit Links");
		miEditLink.setMnemonic(KeyEvent.VK_L);
		miEditLink.addActionListener(this);
		//add(miEditLink);
		//addSeparator();

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

		// END IMPORT / EXPORT / SEND OPTIONS

		if ( addSep )
			addSeparator();

		miMoveDetail = new JMenuItem("Move Detail Into Label");
		miMoveDetail.addActionListener(this);
		miMoveDetail.setMnemonic(KeyEvent.VK_E);
		add(miMoveDetail);

		miMoveLabel = new JMenuItem("Move Label Into Detail");
		miMoveLabel.addActionListener(this);
		miMoveLabel.setMnemonic(KeyEvent.VK_V);
		add(miMoveLabel);
	
		addSeparator();
		
		// Mark seen and unseen for multiple nodes.
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
		
		//Lakshmi (4/25/06) - if node is in read state enable mark unseen
		// and disable mark seen and vice versa
		Enumeration e = viewpaneUI.getViewPane().getSelectedNodes();
		for (;e.hasMoreElements();){
			UINode node = (UINode)e.nextElement();
			NodeSummary oNode = node.getNode();
			int state = oNode.getState();
			if(state == ICoreConstants.READSTATE){
				miMenuItemMarkUnseen.setEnabled(true);
			} else if(state == ICoreConstants.UNREADSTATE) {
				miMenuItemMarkSeen.setEnabled(true);
			} else {
				miMenuItemMarkUnseen.setEnabled(true);
				miMenuItemMarkSeen.setEnabled(true);
			}
		}
		
		if (!(viewpaneUI.getViewPane().getView().getId()).equals(ProjectCompendium.APP.getHomeView().getId())) {

			addSeparator();

			miMenuItemOpen = new JMenuItem("Contents");
			miMenuItemOpen.addActionListener(this);
			miMenuItemOpen.setMnemonic(KeyEvent.VK_O);
			add(miMenuItemOpen);

			miMenuItemProperties = new JMenuItem("Properties");
			miMenuItemProperties.addActionListener(this);
			miMenuItemProperties.setMnemonic(KeyEvent.VK_P);
			add(miMenuItemProperties);

			miMenuItemViews = new JMenuItem("Views");
			miMenuItemViews.addActionListener(this);
			miMenuItemViews.setMnemonic(KeyEvent.VK_W);
			add(miMenuItemViews);
		}

	 	// If on the Mac OS and the Menu bar is at the top of the OS screen, remove the menu shortcut Mnemonics.
		if (ProjectCompendium.isMac && (FormatProperties.macMenuBar || (!FormatProperties.macMenuBar && !FormatProperties.macMenuUnderline)) )
			UIUtilities.removeMenuMnemonics(getSubElements());

		pack();
		setSize(nWidth,nHeight);
	}

	/**
	 * Set the associated ViewPaneUI.
	 * @param viewpaneUI com.compendium.ui.plaf.ViewPaneUI, the associated map for this popup menu.
	 */
	public void setViewPaneUI(ViewPaneUI viewpaneUI) {
		oViewPaneUI = viewpaneUI;
	}

	/**
	 * Handles the event of an option being selected.
	 * @param evt, the event associated with the option being selected.
	 */
	public void actionPerformed(ActionEvent evt) {
		ProjectCompendium.APP.setWaitCursor();

		Object source = evt.getSource();

		if(source.equals(miImportCurrentView)) {
			onImportFile(false);
		}
		else if (source.equals(miUDIGProperty)) {
			if (ProjectCompendium.APP.oUDigCommunicationManager != null) {
				String sNotSent = "";
				String sDate = CoreCalendar.getCurrentDateStringFull();
				String sDetail = "";
				IModel model = ProjectCompendium.APP.getModel();				
				String sAuthor = oViewPane.getCurrentAuthor();
				
				Code oCode = null;				
				try {
					oCode = CoreUtilities.checkCreateCode("UDIG", model, model.getSession(), sAuthor);
				} catch(Exception e) {
					ProjectCompendium.APP.displayError("Unable to create and add 'UDIG' tag due to: \n"+e.getMessage());
				}

				if(oViewPane.getNumberOfSelectedNodes() > 0) {
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
			}
		}						
		else if(source.equals(miImportMultipleViews)) {
			onImportFile(true);
		}
		else if(source.equals(miMenuItemCopy)) {
			ProjectCompendium.APP.onEditCopy();
		}
		else if(source.equals(miMenuItemCut)) {
			ProjectCompendium.APP.onEditCut();
		}
		else if(source.equals(miMenuItemPaste)) {
			ProjectCompendium.APP.onEditPaste();
		}
// begin edit, Lakshmi (11/3/05)

		else if(source.equals(miMenuItemTopDownArrange)) {
			ProjectCompendium.APP.onViewArrange(IUIArrange.TOPDOWN);
		}
		else if(source.equals(miMenuItemLeftRightArrange)) {
			ProjectCompendium.APP.onViewArrange(IUIArrange.LEFTRIGHT);
		}
		else if(source.equals(miMenuItemAlignTop))
			ProjectCompendium.APP.onViewAlign(UIAlign.TOP);
		else if(source.equals(miMenuItemAlignCenter))
			ProjectCompendium.APP.onViewAlign(UIAlign.CENTER);
		else if(source.equals(miMenuItemAlignBottom))
			ProjectCompendium.APP.onViewAlign(UIAlign.BOTTOM);
		else if(source.equals(miMenuItemAlignRight))
			ProjectCompendium.APP.onViewAlign(UIAlign.RIGHT);
		else if(source.equals(miMenuItemAlignMiddle))
			ProjectCompendium.APP.onViewAlign(UIAlign.MIDDLE);
		else if(source.equals(miMenuItemAlignLeft))
			ProjectCompendium.APP.onViewAlign(UIAlign.LEFT);

//end edit
		else if (source.equals(miStencilManagement)) {
			UIStencilDialog dlg = new UIStencilDialog(ProjectCompendium.APP, ProjectCompendium.APP.oStencilManager);
			UIUtilities.centerComponent(dlg, ProjectCompendium.APP);
			dlg.setVisible(true);
		}
		else if (source.equals(miImportCurrentView))
			onImportFile(false);
		else if (source.equals(miImportMultipleViews))
			onImportFile(true);
		else if (source.equals(miImportImageFolder))
			ProjectCompendium.APP.onFileImportImageFolder(oViewPane.getViewFrame());

		else if (source.equals(miSaveAsJpeg))
			ProjectCompendium.APP.onSaveAsJpeg();
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
		} else if(source.equals(miMenuItemOpen)) {
			oViewPaneUI.getViewPane().getViewFrame().showEditDialog();
		}
		// Lakshmi (4/25/06) - To Mark seen /unseen for multiple nodes
		else if(source.equals(miMenuItemMarkSeen)) {
			try {
				Enumeration e = oViewPaneUI.getViewPane().getSelectedNodes();
				for(;e.hasMoreElements();){
					UINode node = (UINode) e.nextElement();
					NodeSummary oNode = node.getNode();
					oNode.setState(ICoreConstants.READSTATE);
				
				}
			}
			catch(Exception io) {
				System.out.println("Unable to mark as read");
			}
		}
		else if(source.equals(miMenuItemMarkUnseen)) {
			try {
				Enumeration e = oViewPaneUI.getViewPane().getSelectedNodes();
				for(;e.hasMoreElements();){
					UINode node = (UINode) e.nextElement();
					NodeSummary oNode = node.getNode();
					oNode.setState(ICoreConstants.UNREADSTATE);
				}
			}
			catch(Exception io) {
				System.out.println("Unable to mark as un-read");
			}
		}
		else if(source.equals(miMenuItemProperties)) {
			oViewPaneUI.getViewPane().getViewFrame().showPropertiesDialog();
		}
		else if(source.equals(miMenuItemViews)) {
			oViewPaneUI.getViewPane().getViewFrame().showViewsDialog();
		}
		else if (source.equals(miMoveDetail)) {
			for(Enumeration e = oViewPane.getSelectedNodes();e.hasMoreElements();) {
				UINode uinode = (UINode)e.nextElement();
				uinode.onMoveDetails();
			}

			ProjectCompendium.APP.setStatus("");
			oViewPane.getViewPaneUI().redraw();
		}
		else if (source.equals(miMoveLabel)) {
			for(Enumeration e = oViewPane.getSelectedNodes();e.hasMoreElements();) {
				UINode uinode = (UINode)e.nextElement();
				uinode.onMoveLabel();
			}

			ProjectCompendium.APP.setStatus("");
			oViewPane.getViewPaneUI().redraw();
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
		else if(source.equals(miMenuItemPro))	{
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
		else if(source.equals(miTypeIssue)) {
			onChangeType(ICoreConstants.ISSUE);
		}
		else if(source.equals(miTypePosition)) {
			onChangeType(ICoreConstants.POSITION);
		}
		else if(source.equals(miTypeMap)) {

			for(Enumeration e = oViewPane.getSelectedNodes();e.hasMoreElements();) {

				UINode uinode = (UINode)e.nextElement();

				if (uinode.setType(ICoreConstants.MAPVIEW)) {
					oViewPane.setSelectedNode(uinode,ICoreConstants.MULTISELECT);
					uinode.requestFocus();
				}
			}
		}
		else if(source.equals(miTypeList)) {

			for(Enumeration e = oViewPane.getSelectedNodes();e.hasMoreElements();) {

				UINode uinode = (UINode)e.nextElement();

				if (uinode.setType(ICoreConstants.LISTVIEW)) {
					oViewPane.setSelectedNode(uinode,ICoreConstants.MULTISELECT);
					uinode.requestFocus();
				}
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

		ProjectCompendium.APP.setDefaultCursor();
	}


	/**
	 * Process a node type request.
	 * @param type, the new type to set the selected nodes to.
	 */
	private void onChangeType(int type) {

		for(Enumeration e = oViewPane.getSelectedNodes();e.hasMoreElements();) {

			UINode uinode = (UINode)e.nextElement();
			uinode.setType(type);
			oViewPane.setSelectedNode(uinode, ICoreConstants.MULTISELECT);
			uinode.requestFocus();
		}
	}

	/**
	 * Process a node creation request.
	 * @param nType, the type of the new node to create.
	 */
	private void createNode(int nType) {

		// MOVE THE MOUSE POINTER TO THE CORRECT POSITION
		try {
			Point pos = new Point(nX, nY);
			SwingUtilities.convertPointToScreen(pos, oViewPane);
			Robot rob = new Robot();
			rob.mouseMove(pos.x, pos.y);
		}
		catch(AWTException ex) {}

		// MOVE X AN Y FOR NODE OUT A BIT SO MOUSEPOINTER NOT RIGHT ON EDGE
		nX -= 20;
		nY -= 20;

		oViewPaneUI.addNewNode(nType, nX, nY);
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
