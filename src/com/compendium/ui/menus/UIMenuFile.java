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


package com.compendium.ui.menus;

import java.awt.event.*;
import java.util.*;

import javax.help.*;
import javax.swing.*;

import org.jabber.jabberbeans.*;
import org.jabber.jabberbeans.util.*;

import com.compendium.core.datamodel.*;
import com.compendium.core.*;
import com.compendium.*;
import com.compendium.ui.*;
import com.compendium.ui.dialogs.UIImportFlashMeetingXMLDialog;
import com.compendium.ui.dialogs.UISystemSettingsDialog;

import com.compendium.io.xml.PrefuseGraphXMLExport;

// ON NON-MAC PLATFORM, THIS REQUIRES AppleJavaExtensions.jar stub classes TO COMPILE
import com.apple.eawt.*;

/**
 * This class creates the file menu.
 *
 * @author	Michelle Bachler
 */
public class UIMenuFile implements IUIMenu, ActionListener, IUIConstants, ICoreConstants {

	/** The File menu.*/
	private JMenu				mnuMainMenu				= null;

	private JMenuItem			miSystemSettings		= null;

	/** The menu holding the convert database options.*/
	private JMenuItem			mnuMainMenuConvert			= null;

	/** The menu item to open a database project.*/
	private JMenuItem			miFileOpen				= null;

	/** The menu item to crate a new database project.*/
	private JMenuItem			miFileNew				= null;

	/** The menu item to convert an MySQL database to the Derby database format.*/
	private JMenuItem			miFileConvert		= null;

	/** The menu item to convert an Derby database to the MySQL database format.*/
	private JMenuItem			miFileConvertFromDerby		= null;

	/** The menu item to open the database management dialog.*/
	private JMenuItem			miDatabases				= null;

	/** The menu item to open the database administration dialog.*/
	private JMenuItem			miDatabaseAdministration	= null;

	/** The menu item to open the backup dialog.*/
	private JMenuItem			miFileBackup			= null;

	/** The menu item to close the curent database project.*/
	private JMenuItem			miFileClose				= null;

// IMPORT MENU
	/** The import menu.*/
	private JMenu				mnuImport				= null;

	/** The Questmap import menu.*/
	private JMenu				miFileImport			= null;

	/** The menu item to import from Questmap into the current view.*/
	private JMenuItem			miImportCurrentView		= null;

	/** The menu item to import from questmap into selected views.*/
	private JMenuItem			miImportMultipleViews	= null;

	/** The menu item to import an XML file.*/
	private JMenuItem			miImportXMLView			= null;

	/** The menu item to import an image folder.*/
	private JMenuItem			miImportImageFolder		= null;

	/** The menu item to import Flashmeeting XML.*/
	private JMenuItem			miImportXMLFlashmeeting = null;

// EXPORT MENU
	/** The Export menu.*/
	private JMenu				mnuExport				= null;

	/** The menu item to export to a HTML Outline file.*/
	private JMenuItem			miExportHTMLOutline		= null;

	/** The menu item to export to HTML Views (with image maps).*/
	private JMenuItem			miExportHTMLViews		= null;

	/** The menu item to export to XML.*/
	private JMenuItem			miExportXMLView			= null;
	
	/** The menu item to export a HTML view with the XML included.*/
	private JMenuItem			miExportHTMLViewXML		= null;

	/** The menu item to export to a Prefuse XML file format.*/
	private JMenuItem			miExportPrefuseXML		= null;

	/** The menu item to save current amp as a jpg.*/
	private JMenuItem			miSaveAsJpeg			= null;

	/** The menu to send a message to a Jabber client.*/
	private JMenu				mnuSendToJabber			= null;

	/** The menu to send a message to an IX Panel.*/
	private JMenu				mnuSendToIX				= null;

	/** NOT CURRENTLY USED.*/
	private JMenuItem			miFilePageSetup			= null;

	/** The menu item to print the current map.*/
	private JMenuItem			miFilePrint				= null;

	/** The menu item to exit the application.*/
	private JMenuItem			miFileExit				= null;

// CONNECTIONS MENU
	/** The Connection menu.*/
	private JMenu				mnuConnect				= null;

	/** The menu item to open a Jabber connection to an IX Panel.*/
	private JMenuItem			miConnectToIXServer		= null;

	/** The menu item to open a Jabber connection to a Jabber client.*/
	private JMenuItem			miConnectToJabberServer	= null;

	/** The menu item to open a Jabber connection for Compendium P2P operations*/
	private JMenuItem			miConnectToPeerToPeer	= null;

	/** The menu item to enter the ClaiMaker url.*/
	private JMenuItem			miConnectToClaiMaker	= null;

	/** The menu item to open a Jabber connection to a Media Player instance.*/
	private JMenuItem			miConnectToMediaServer	= null;

// PEER_TO_PEER
	/** The Peer To Peer menu*.
	private JMenu				mnuPeerToPeer			= null;

	/** The menu item to open the bradcasters control dialog.*/
	private JMenuItem			miPTPbroadcast			= null;


	/** The platform specific shortcut key to use.*/
	private int shortcutKey;
	
	/**Indicates whether this menu is draw as a Simple interface or a advance user inteerface.*/
	private boolean bSimpleInterface					= false;

	/**
	 * Constructor.
	 * @param bSimple true if the simple interface should be draw, false if the advanced.
	 */
	public UIMenuFile(boolean bSimple) {
		shortcutKey = ProjectCompendium.APP.shortcutKey;
		this.bSimpleInterface = bSimple;
		
		mnuMainMenu	= new JMenu("File");  //$NON-NLS-1$
		CSH.setHelpIDString(mnuMainMenu,"menus.file"); //$NON-NLS-1$
		mnuMainMenu.setMnemonic(KeyEvent.VK_F);
		
		createMenuItems();
	}

	/**
	 * If true, redraw the simple form of this menu, else redraw the complex form.
	 * @param isSimple
	 */public void setIsSimple(boolean isSimple) {
		bSimpleInterface = isSimple;
		recreateMenu();
	}

	/**
	 * Redraw the menu items
	 */
	private void recreateMenu() {
		mnuMainMenu.removeAll();
		createMenuItems();
		onDatabaseOpen();		
	}
	
	/**
	 * Create and return the File menu.
	 * @return JMenu the File menu.
	 */
	private JMenu createMenuItems() {

		miFileNew = new JMenuItem("New...");  //$NON-NLS-1$
		miFileNew.setAccelerator(KeyStroke.getKeyStroke( KeyEvent.VK_N, shortcutKey));
		miFileNew.setMnemonic(KeyEvent.VK_N);
		miFileNew.addActionListener(this);
		mnuMainMenu.add(miFileNew);

		miFileOpen = new JMenuItem("Open...");  //$NON-NLS-1$
		miFileOpen.setAccelerator(KeyStroke.getKeyStroke( KeyEvent.VK_O, shortcutKey));
		miFileOpen.setMnemonic(KeyEvent.VK_O);
		miFileOpen.setEnabled(false);
		miFileOpen.addActionListener(this);
		mnuMainMenu.add(miFileOpen);

		miFileClose = new JMenuItem("Close");  //$NON-NLS-1$
		miFileClose.setMnemonic(KeyEvent.VK_C);
		miFileClose.addActionListener(this);
		mnuMainMenu.add(miFileClose);

		miSystemSettings = new JMenuItem("System Settings...");  //$NON-NLS-1$
		miSystemSettings.setMnemonic(KeyEvent.VK_S);
		miSystemSettings.addActionListener(this);
		mnuMainMenu.add(miSystemSettings);

		miFileBackup = new JMenuItem("Backup...");  //$NON-NLS-1$
		miFileBackup.setToolTipText("Backup the current project");  //$NON-NLS-1$
		miFileBackup.setEnabled(false);
		miFileBackup.setMnemonic(KeyEvent.VK_B);
		miFileBackup.addActionListener(this);
		mnuMainMenu.add(miFileBackup);

		mnuMainMenu.addSeparator();

		miDatabaseAdministration = new JMenuItem("Database Administration...");  //$NON-NLS-1$
		miDatabaseAdministration.setMnemonic(KeyEvent.VK_A);
		miDatabaseAdministration.setDisplayedMnemonicIndex(10);
		miDatabaseAdministration.addActionListener(this);
		mnuMainMenu.add(miDatabaseAdministration);

		if (FormatProperties.nDatabaseType == ICoreConstants.MYSQL_DATABASE) {
			miFileConvert = new JMenuItem("Convert From Derby To MySQL...");  //$NON-NLS-1$
			miFileConvert.setMnemonic(KeyEvent.VK_M);
			miFileConvert.setDisplayedMnemonicIndex(6);
			miFileConvert.addActionListener(this);
			mnuMainMenu.add(miFileConvert);
		}
		else {
			miFileConvert = new JMenuItem("Convert From MySQL To Derby...");  //$NON-NLS-1$
			miFileConvert.setMnemonic(KeyEvent.VK_M);
			miFileConvert.setDisplayedMnemonicIndex(6);
			miFileConvert.addActionListener(this);
			mnuMainMenu.add(miFileConvert);
		}


		miDatabases = new JMenuItem("Project Management...");  //$NON-NLS-1$
		miDatabases.setMnemonic(KeyEvent.VK_M);
		miDatabases.addActionListener(this);
		mnuMainMenu.add(miDatabases);

		mnuMainMenu.addSeparator();

		// create EXPORT options
		mnuExport = new JMenu("Export");  //$NON-NLS-1$
		mnuExport.setMnemonic(KeyEvent.VK_E);

		miExportXMLView = new JMenuItem("XML File...");  //$NON-NLS-1$
		miExportXMLView.setMnemonic(KeyEvent.VK_X);
		miExportXMLView.addActionListener(this);
		mnuExport.add(miExportXMLView);

		//miExportPrefuseXML = new JMenuItem("Prefuse XML"); 
		//miExportPrefuseXML.setMnemonic(KeyEvent.VK_P);
		//miExportPrefuseXML.addActionListener(this);
		//mnuExport.add(miExportPrefuseXML);
				
		miExportHTMLOutline = new JMenuItem("Web Outline...");  //$NON-NLS-1$
		miExportHTMLOutline.setMnemonic(KeyEvent.VK_O);		
		miExportHTMLOutline.addActionListener(this);
		mnuExport.add(miExportHTMLOutline);

		miExportHTMLViews = new JMenuItem("Web Maps...");  //$NON-NLS-1$
		miExportHTMLViews.setMnemonic(KeyEvent.VK_W);		
		miExportHTMLViews.addActionListener(this);
		mnuExport.add(miExportHTMLViews);

		miExportHTMLViewXML = new JMenuItem("Power Export..."); 
		miExportHTMLViewXML.setToolTipText("Integrated Web Map and Outline Export with XML zip export inlcuded"); 
		miExportHTMLViewXML.setMnemonic(KeyEvent.VK_P);
		miExportHTMLViewXML.addActionListener(this);
		mnuExport.add(miExportHTMLViewXML);

		miSaveAsJpeg = new JMenuItem("Jpeg File...");  //$NON-NLS-1$
		miSaveAsJpeg.setMnemonic(KeyEvent.VK_J);
		miSaveAsJpeg.addActionListener(this);
		mnuExport.add(miSaveAsJpeg);

		mnuMainMenu.add(mnuExport);

		mnuMainMenu.addSeparator();

		// create IMPORT options
		mnuImport = new JMenu("Import");  //$NON-NLS-1$
		mnuImport.setMnemonic(KeyEvent.VK_I);

		miImportXMLView = new JMenuItem("XML File...");  //$NON-NLS-1$
		miImportXMLView.setMnemonic(KeyEvent.VK_X);
		miImportXMLView.addActionListener(this);
		mnuImport.add(miImportXMLView);

		miImportXMLFlashmeeting = new JMenuItem("FlashMeeting XML..."); 
		miImportXMLFlashmeeting.setMnemonic(KeyEvent.VK_F);
		miImportXMLFlashmeeting.addActionListener(this);
		mnuImport.add(miImportXMLFlashmeeting);

		miFileImport = new JMenu("Questmap File...");  //$NON-NLS-1$
		miFileImport.setMnemonic(KeyEvent.VK_Q);
		miFileImport.addActionListener(this);

		// INCASE I WANT TO PUT FILE IMAGES BACK, KEEP ON REFERENCE
		//miImportCurrentView = new JMenuItem("Current View..", UIImages.get(IUIConstants.NEW_ICON));

		miImportCurrentView = new JMenuItem("Current View...");  //$NON-NLS-1$
		miImportCurrentView.addActionListener(this);
		miFileImport.add(miImportCurrentView);

		miImportMultipleViews = new JMenuItem("Multiple Views...");  //$NON-NLS-1$
		miImportMultipleViews.addActionListener(this);
		miFileImport.add(miImportMultipleViews);

		mnuImport.add(miFileImport);

		miImportImageFolder = new JMenuItem("Image Folder into Current Map..."); //$NON-NLS-1$
		miImportImageFolder.setMnemonic(KeyEvent.VK_I);
		miImportImageFolder.addActionListener(this);
		mnuImport.add(miImportImageFolder);

		mnuMainMenu.add(mnuImport);
		mnuMainMenu.addSeparator();

		// create CONNECTION option
		mnuConnect = new JMenu("Connections");  //$NON-NLS-1$
		mnuConnect.setMnemonic(KeyEvent.VK_T);

		miConnectToIXServer = new JMenuItem("IX Panel Connection...");  //$NON-NLS-1$
		miConnectToIXServer.addActionListener(this);
		mnuConnect.add(miConnectToIXServer);

		miConnectToJabberServer = new JMenuItem("Jabber Connection...");  //$NON-NLS-1$
		miConnectToJabberServer.addActionListener(this);
		mnuConnect.add(miConnectToJabberServer);

		miConnectToClaiMaker = new JMenuItem("ClaiMaker Connection...");  //$NON-NLS-1$
		miConnectToClaiMaker.addActionListener(this);
		mnuConnect.add(miConnectToClaiMaker);

		mnuMainMenu.add(mnuConnect);

		// SEND TO
		mnuSendToJabber = new JMenu("Send To Jabber");  //$NON-NLS-1$
		mnuSendToJabber.setMnemonic(KeyEvent.VK_J);

		mnuSendToJabber.setEnabled(false);
		mnuMainMenu.add(mnuSendToJabber);
		ProjectCompendium.APP.drawJabberRoster(mnuSendToJabber);

		mnuSendToIX = new JMenu("Send To IX");  //$NON-NLS-1$
		mnuSendToIX.setMnemonic(KeyEvent.VK_D);
		mnuSendToIX.setEnabled(false);
		mnuMainMenu.add(mnuSendToIX);
		ProjectCompendium.APP.drawIXRoster(mnuSendToIX);
		mnuMainMenu.addSeparator();

		miFilePrint = new JMenuItem("Print...");  //$NON-NLS-1$
		miFilePrint.setMnemonic(KeyEvent.VK_P);
		miFilePrint.addActionListener(this);
		mnuMainMenu.add(miFilePrint);

		miFileExit = new JMenuItem("Exit");  //$NON-NLS-1$
		miFileExit.addActionListener(this);

		if (!ProjectCompendium.isMac) {
			mnuMainMenu.addSeparator();
			miFileExit.setMnemonic(KeyEvent.VK_X);
			mnuMainMenu.add(miFileExit);
		}

		return mnuMainMenu;
	}

	/**
	 * Handles most menu action event for this application.
	 *
	 * @param evt the generated action event to be handled.
	 */
	public void actionPerformed(ActionEvent evt) {

		ProjectCompendium.APP.setWaitCursor();

		Object source = evt.getSource();

		if (source.equals(miSystemSettings)) {
			UISystemSettingsDialog dlg = new UISystemSettingsDialog(ProjectCompendium.APP);
			UIUtilities.centerComponent(dlg, ProjectCompendium.APP);
			dlg.setVisible(true);
		} else if (source.equals(miFileNew)) {
			ProjectCompendium.APP.onFileNew();
		} else if (source.equals(miFileOpen)) {
			ProjectCompendium.APP.onFileOpen();
		} else if (source.equals(miFileClose)) {
			ProjectCompendium.APP.onFileClose();
		}
		else if (source.equals(miFileConvert)) {
			if (miFileConvert.getText().equals("Convert From MySQL To Derby...")) {  //$NON-NLS-1$
				ProjectCompendium.APP.onFileConvertFromMySQL();
			}
			else {
				ProjectCompendium.APP.onFileConvertFromDerby();
			}
		}
		else if (source.equals(miFileBackup))
			ProjectCompendium.APP.onFileBackup();

		else if (source.equals(miConnectToJabberServer))
			ProjectCompendium.APP.onConnect("Jabber"); //$NON-NLS-1$
		else if (source.equals(miConnectToIXServer))
			ProjectCompendium.APP.onConnect("IXPanel"); //$NON-NLS-1$
		else if (source.equals(miConnectToClaiMaker))
			ProjectCompendium.APP.onConnect("ClaiMaker"); //$NON-NLS-1$

		else if (source.equals(miImportCurrentView))
			ProjectCompendium.APP.onFileImport(false);
		else if (source.equals(miImportMultipleViews))
			ProjectCompendium.APP.onFileImport(true);
		else if (source.equals(miImportImageFolder))
			ProjectCompendium.APP.onFileImportImageFolder();

		else if (source.equals(miExportHTMLOutline))
			ProjectCompendium.APP.onFileExportHTMLOutline();
		else if (source.equals(miExportHTMLViews))
			ProjectCompendium.APP.onFileExportHTMLView();

		else if (source.equals(miExportXMLView))
			ProjectCompendium.APP.onFileXMLExport(false);
		else if (source.equals(miExportPrefuseXML)) {
			UIViewFrame frame = ProjectCompendium.APP.getCurrentFrame();
			PrefuseGraphXMLExport prefuse = new PrefuseGraphXMLExport(frame, "D:\\PrefuseText.xml"); 
			prefuse.start();
		}
		else if (source.equals(miExportHTMLViewXML))
			ProjectCompendium.APP.onFileExportPower();								
		else if (source.equals(miImportXMLView))
			ProjectCompendium.APP.onFileXMLImport();
		else if (source.equals(miImportXMLFlashmeeting)) {
			UIImportFlashMeetingXMLDialog dlg = new UIImportFlashMeetingXMLDialog(ProjectCompendium.APP);
			UIUtilities.centerComponent(dlg, ProjectCompendium.APP);
			dlg.setVisible(true);
		}		
		else if (source.equals(miSaveAsJpeg))
			ProjectCompendium.APP.onSaveAsJpeg();

		else if (source.equals(miFilePageSetup))
			ProjectCompendium.APP.onFilePageSetup();
		else if (source.equals(miFilePrint))
			ProjectCompendium.APP.onFilePrint();
		else if (source.equals(miFileExit)) {
			ProjectCompendium.APP.onExit();
		}
		else if (source.equals(miDatabaseAdministration)) {
			ProjectCompendium.APP.onFileDatabaseAdmin();
		}
		else if (source.equals(miDatabases))
			ProjectCompendium.APP.onDatabases();

		ProjectCompendium.APP.setDefaultCursor();
	}

	/**
	 * Updates the menu when a database project is closed.
	 */
	public void onDatabaseClose() {

		try {
			if (miFileOpen != null) {
				miFileOpen.setEnabled(true);
			}
			if (miFileNew != null) {
				miFileNew.setEnabled(true);
			}
			if (miFileClose != null) {
				miFileClose.setEnabled(false);
			}
			if (miFileBackup != null) {
				miFileBackup.setEnabled(false);
			}
			if (miFilePrint != null) {
				miFilePrint.setEnabled(false);
			}
			if (mnuImport != null) {
				mnuImport.setEnabled(false);
			}
			if (mnuExport != null) {
				mnuExport.setEnabled(false);
			}
			if (mnuConnect != null) {
				mnuConnect.setEnabled(false);
			}
		}
		catch (Exception ex) {
			ex.printStackTrace();
			ProjectCompendium.APP.displayError("Error: (UIMenuFile.onDatabaseClose)\n\n" + ex.getMessage()); //$NON-NLS-1$
		}
	}

	/**
	 * Updates the menu when a database project is opened.
	 */
	public void onDatabaseOpen() {

		if (miFileOpen != null) {
			miFileOpen.setEnabled(false);
		}
		if (miFileNew != null) {
			miFileNew.setEnabled(false);
		}
		if (miFileClose != null) {
			miFileClose.setEnabled(true);
		}
		if (miFileBackup != null) {
			if (ProjectCompendium.APP.getModel() != null) {				
				boolean bUserAdmin = ProjectCompendium.APP.getModel().getUserProfile().isAdministrator();				
				miFileBackup.setEnabled(bUserAdmin);
			}
		}
		if (miFilePrint != null) {
			miFilePrint.setEnabled(true);
		}
		if (mnuImport != null) {
			mnuImport.setEnabled(true);
		}
		if (mnuExport != null) {
			mnuExport.setEnabled(true);
		}
		if (mnuConnect != null) {
			mnuConnect.setEnabled(true);
		}
	}

	/**
	 * Draw the roster menu list for a Jabber connection.
	 *
	 * @param menu, the menu to add the options to.
	 * @param node com.compendium.core.datamodel.NodeSummary, the node, associated with this menu.
	 * - only applies if request activated from node right-click menu, else value will be null.
	 * @param rosterEntries, the roster entries to create menu items for.
	 */
	public void drawJabberRoster(JMenu menu, NodeSummary node, Enumeration rosterEntries) {
		if (menu == null)
			menu = mnuSendToJabber;

		if (rosterEntries.hasMoreElements() && menu != null) {

			menu.removeAll();
			boolean itemAdded = false;

	        while (rosterEntries != null && rosterEntries.hasMoreElements()) {

   	        	RosterItem ri = (RosterItem) rosterEntries.nextElement();

             	// gets the presence of jid
				//BSPresenceInfo pi = jabber.getPresence().getPresence(ri.getJID());
                //" (" + pi.show + "-" + pi.status + ")");
				//pi.jid.getResource();

				JMenuItem item = new JMenuItem(ri.getFriendlyName());
				final JID jid = ri.getJID();
				final NodeSummary sumnode = node;
				item.addActionListener( new ActionListener() {
						public void actionPerformed(ActionEvent evt) {
							Thread thread = new Thread("Jabber Roster") { //$NON-NLS-1$
								public void run() {
									if (sumnode != null)
										ProjectCompendium.APP.processNodeToJabber(jid, sumnode);
									else
										ProjectCompendium.APP.toJabber( jid );
								}
							};
							thread.start();
						}
					});
				menu.add(item);
				itemAdded = true;
        	}
			if (itemAdded);
			menu.setEnabled(true);
		}
	}
	
	/**
	 * Draw the roster menu list for the IX panel connection.
	 *
	 * @param menu the menu to add the options to.
	 * @param node the node, associated with this menu.
	 * - only applies if request activated from node right-click menu, else value will be null.
	 * @param rosterEntries the roster entries to create menu items for.
	 */
	public void drawIXRoster(JMenu menu, NodeSummary node, Enumeration rosterEntries) {

		if (menu == null)
			menu = mnuSendToIX;

		if (rosterEntries.hasMoreElements() && menu != null) {
			menu.removeAll();

			boolean itemAdded = false;

	        while (rosterEntries != null && rosterEntries.hasMoreElements()) {

   	        	RosterItem ri = (RosterItem) rosterEntries.nextElement();
   	        	String sName = ri.getFriendlyName();
   	        	if (sName == null || sName.equals("")) { 
   	   	        	JID jid = ri.getJID();
   	   	        	sName = jid.getUsername();
   	        	}
				JMenuItem item = new JMenuItem(sName);
				final JID jid = ri.getJID();
				final NodeSummary sumnode = node;
				item.addActionListener( new ActionListener() {
					public void actionPerformed(ActionEvent evt) {
						Thread thread = new Thread("IX Roster") { //$NON-NLS-1$
							public void run() {
								if (sumnode != null)
									ProjectCompendium.APP.processNodeToIX( jid, sumnode );
								else
									ProjectCompendium.APP.toIXPanel( jid );
							}
						};
						thread.start();
					}
				});
				menu.add(item);
				itemAdded = true;
 			}

			if (itemAdded)
				menu.setEnabled(true);
		}
	}

	/**
 	 * Enable/disable  menu items when nodes or links selected.
 	 * Does nothing.
  	 * @param selected true for enabled false for disabled.
	 */
	public void setNodeOrLinkSelected(boolean selected) {}

	/**
 	 * Indicates when nodes on a view are selected and deselected.
 	 * Does Nothing.
  	 * @param selected true for selected false for deselected.
	 */
	public void setNodeSelected(boolean selected) {}
	
	/**
	 * Enable/disable the jabber menu item.
	 * @param enabled true to enable, false to disable.
	 */
	public void setJabberMenuEnablement(boolean enabled) {
		if (mnuSendToJabber != null) {
			mnuSendToJabber.setEnabled(enabled);
		}
	}

	/**
	 * Enable/disable the jabber menu item.
	 * @param enabled true to enable, false to disable.
	 */
	public void setIXMenuEnablement(boolean enabled) {
		if (mnuSendToIX != null) {
			mnuSendToIX.setEnabled(enabled);
		}
	}

	/**
	 * Enable/disable the convert database menu option as appropriate.
	 */
	public void enableConvertMenuOptions() {

		if (miFileConvert != null) {
			if (FormatProperties.nDatabaseType == ICoreConstants.DERBY_DATABASE) {
				miFileConvert.setText("Convert From MySQL To Derby...");  //$NON-NLS-1$
			}
			else {
				miFileConvert.setText("Convert From Derby To MySQL...");  //$NON-NLS-1$
			}
		}
	}

	/**
	 * Enable/disable the file open menu item.
	 * @param enabled true to enable, false to disable.
	 */
	public void setFileOpenEnablement(boolean enabled) {
		if (miFileOpen != null) {
			miFileOpen.setEnabled( enabled );
		}
	}
	
	/**
	 * Exit the application.
	 */
	public void exit() {
		miFileExit.doClick();
	}
	
	/**
	 * Update the look and feel of the menu.
	 */
	public void updateLAF() {
		if (mnuMainMenu != null)
			SwingUtilities.updateComponentTreeUI(mnuMainMenu);
	}	
	
	/**
	 * Return a reference to the main menu.
	 * @return JMenu a reference to the main menu.
	 */
	public JMenu getMenu() {
		return mnuMainMenu;
	}	
}
