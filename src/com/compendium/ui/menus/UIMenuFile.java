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

package com.compendium.ui.menus;

import java.awt.BorderLayout;
import java.awt.event.*;
import java.util.*;
import java.sql.SQLException;
import java.io.File;

import javax.help.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import org.jabber.jabberbeans.*;
import org.jabber.jabberbeans.util.*;

import com.compendium.core.datamodel.*;
import com.compendium.core.*;
import com.compendium.core.db.DBNode;
import com.compendium.*;
import com.compendium.ui.*;
import com.compendium.io.xml.AMLXMLImport;
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
public class UIMenuFile extends UIMenu implements ActionListener, IUIConstants, ICoreConstants {

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
	
	/** The menu item to mark entire project as "Seen" */
	private JMenuItem			miMarkProjectSeen		= null;

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

	/** The menu item to import Argument Markup Language XML.*/
	private JMenuItem			miImportXMLAML = null;
	
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

	private JSeparator			separator4				= null;
	private JSeparator			separator5				= null;

	/** The platform specific shortcut key to use.*/
	private int shortcutKey;

	/**
	 * Constructor.
	 * @param bSimple true if the simple interface should be draw, false if the advanced.
	 */
	public UIMenuFile(boolean bSimple) {
		shortcutKey = ProjectCompendium.APP.shortcutKey;
		this.bSimpleInterface = bSimple;
		
		mnuMainMenu	= new JMenu(LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuFile.file"));   //$NON-NLS-1$
		CSH.setHelpIDString(mnuMainMenu,"menus.file");  //$NON-NLS-1$
		mnuMainMenu.setMnemonic((LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuFile.fileMnemonic")).charAt(0)); //$NON-NLS-1$
		
		createMenuItems(bSimple);
	}
	
	/**
	 * Create and return the File menu.
	 * @return JMenu the File menu.
	 */
	private JMenu createMenuItems(boolean bSimple) {

		miFileNew = new JMenuItem(LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuFile.new"));   //$NON-NLS-1$
		miFileNew.setAccelerator(KeyStroke.getKeyStroke( KeyEvent.VK_N, shortcutKey));
		miFileNew.setMnemonic((LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuFile.newMnemonic")).charAt(0)); //$NON-NLS-1$
		miFileNew.addActionListener(this);
		mnuMainMenu.add(miFileNew);

		miFileOpen = new JMenuItem(LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuFile.open"));   //$NON-NLS-1$
		miFileOpen.setAccelerator(KeyStroke.getKeyStroke( KeyEvent.VK_O, shortcutKey));
		miFileOpen.setMnemonic((LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuFile.openMnemonic")).charAt(0)); //$NON-NLS-1$
		miFileOpen.setEnabled(false);
		miFileOpen.addActionListener(this);
		mnuMainMenu.add(miFileOpen);

		miFileClose = new JMenuItem(LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuFile.close"));   //$NON-NLS-1$
		miFileClose.setMnemonic((LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuFile.closeMnemonic")).charAt(0)); //$NON-NLS-1$
		miFileClose.addActionListener(this);
		mnuMainMenu.add(miFileClose);

		miSystemSettings = new JMenuItem(LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuFile.systemSettings"));   //$NON-NLS-1$
		miSystemSettings.setMnemonic((LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuFile.systemSettingsMnemonic")).charAt(0)); //$NON-NLS-1$
		miSystemSettings.addActionListener(this);
		mnuMainMenu.add(miSystemSettings);

		miFileBackup = new JMenuItem(LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuFile.backup"));   //$NON-NLS-1$
		miFileBackup.setToolTipText(LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuFile.backupTip"));   //$NON-NLS-1$
		miFileBackup.setEnabled(false);
		miFileBackup.setMnemonic((LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuFile.backupMnemonic")).charAt(0)); //$NON-NLS-1$
		miFileBackup.addActionListener(this);
		mnuMainMenu.add(miFileBackup);

		separator1 = new JPopupMenu.Separator();
		mnuMainMenu.add(separator1);

		miDatabaseAdministration = new JMenuItem(LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuFile.databaseAdmin"));   //$NON-NLS-1$
		miDatabaseAdministration.setMnemonic((LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuFile.databaseAdminMnemonic")).charAt(0)); //$NON-NLS-1$
		miDatabaseAdministration.addActionListener(this);
		mnuMainMenu.add(miDatabaseAdministration);

		if (FormatProperties.nDatabaseType == ICoreConstants.MYSQL_DATABASE) {
			miFileConvert = new JMenuItem(LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuFile.convertDerbyMySQL"));   //$NON-NLS-1$
			miFileConvert.setMnemonic((LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuFile.convertMnemonic")).charAt(0)); //$NON-NLS-1$
			miFileConvert.addActionListener(this);
			mnuMainMenu.add(miFileConvert);
		}
		else {
			miFileConvert = new JMenuItem(LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuFile.convertMySQLDerby"));   //$NON-NLS-1$
			miFileConvert.setMnemonic((LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuFile.convertMnemonic")).charAt(0)); //$NON-NLS-1$
			miFileConvert.addActionListener(this);
			mnuMainMenu.add(miFileConvert);
		}


		miDatabases = new JMenuItem(LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuFile.projectManagement"));   //$NON-NLS-1$
		miDatabases.setMnemonic((LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuFile.projectManagementMnemonic")).charAt(0)); //$NON-NLS-1$
		miDatabases.addActionListener(this);
		mnuMainMenu.add(miDatabases);

		separator2 = new JPopupMenu.Separator();
		mnuMainMenu.add(separator2);
		
		// create EXPORT options
		mnuExport = new JMenu(LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuFile.export"));   //$NON-NLS-1$
		mnuExport.setMnemonic((LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuFile.exportMnemonic")).charAt(0)); //$NON-NLS-1$

		miExportXMLView = new JMenuItem(LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuFile.exportXML"));   //$NON-NLS-1$
		miExportXMLView.setMnemonic((LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuFile.exportXMLMnemonic")).charAt(0)); //$NON-NLS-1$
		miExportXMLView.addActionListener(this);
		mnuExport.add(miExportXMLView);

		//miExportPrefuseXML = new JMenuItem("Prefuse XML"); 
		//miExportPrefuseXML.setMnemonic(KeyEvent.VK_P);
		//miExportPrefuseXML.addActionListener(this);
		//mnuExport.add(miExportPrefuseXML);
				
		miExportHTMLOutline = new JMenuItem(LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuFile.exportWebOutline"));   //$NON-NLS-1$
		miExportHTMLOutline.setMnemonic((LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuFile.exportWebOutlineMnemonic")).charAt(0)); //$NON-NLS-1$
		miExportHTMLOutline.addActionListener(this);
		mnuExport.add(miExportHTMLOutline);

		miExportHTMLViews = new JMenuItem(LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuFile.exportWebMaps"));   //$NON-NLS-1$
		miExportHTMLViews.setMnemonic((LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuFile.exportWebMapsMnemonic")).charAt(0)); //$NON-NLS-1$
		miExportHTMLViews.addActionListener(this);
		mnuExport.add(miExportHTMLViews);

		miExportHTMLViewXML = new JMenuItem(LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuFile.exportPower"));  //$NON-NLS-1$
		miExportHTMLViewXML.setToolTipText(LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuFile.exportPowerTip"));  //$NON-NLS-1$
		miExportHTMLViewXML.setMnemonic((LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuFile.exportPowerMnemonic")).charAt(0)); //$NON-NLS-1$
		miExportHTMLViewXML.addActionListener(this);
		mnuExport.add(miExportHTMLViewXML);

		miSaveAsJpeg = new JMenuItem(LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuFile.exportJpeg"));   //$NON-NLS-1$
		miSaveAsJpeg.setMnemonic((LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuFile.exportJpegMnemonic")).charAt(0)); //$NON-NLS-1$
		miSaveAsJpeg.addActionListener(this);
		mnuExport.add(miSaveAsJpeg);

		mnuMainMenu.add(mnuExport);

		separator3 = new JPopupMenu.Separator();
		mnuMainMenu.add(separator3);

		// create IMPORT options
		mnuImport = new JMenu(LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuFile.import"));   //$NON-NLS-1$
		mnuImport.setMnemonic((LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuFile.importMnemonic")).charAt(0)); //$NON-NLS-1$

		miImportXMLView = new JMenuItem(LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuFile.importXML"));   //$NON-NLS-1$
		miImportXMLView.setMnemonic((LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuFile.importXMLMnemonic")).charAt(0)); //$NON-NLS-1$
		miImportXMLView.addActionListener(this);
		mnuImport.add(miImportXMLView);

		miImportXMLFlashmeeting = new JMenuItem(LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuFile.importFlashMeeting"));  //$NON-NLS-1$
		miImportXMLFlashmeeting.setMnemonic((LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuFile.importFlashMeetingMnemonic")).charAt(0)); //$NON-NLS-1$
		miImportXMLFlashmeeting.addActionListener(this);
		mnuImport.add(miImportXMLFlashmeeting);

		//miImportXMLAML = new JMenuItem("Import Argument Markup Language XML...");
		//miImportXMLAML.setMnemonic(KeyEvent.VK_F);
		//miImportXMLAML.addActionListener(this);
		//mnuImport.add(miImportXMLAML);
		
		miFileImport = new JMenu(LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuFile.importQuestmap"));   //$NON-NLS-1$
		miFileImport.setMnemonic((LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuFile.importQuestmapMnemonic")).charAt(0)); //$NON-NLS-1$
		miFileImport.addActionListener(this);

		// INCASE I WANT TO PUT FILE IMAGES BACK, KEEP REFERENCE
		//miImportCurrentView = new JMenuItem("Current View..", UIImages.get(IUIConstants.NEW_ICON));

		miImportCurrentView = new JMenuItem(LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuFile.importQuestmapCurrent"));   //$NON-NLS-1$
		miImportCurrentView.setMnemonic((LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuFile.importQuestmapCurrentMnemonic")).charAt(0)); //$NON-NLS-1$
		miImportCurrentView.addActionListener(this);
		miFileImport.add(miImportCurrentView);

		miImportMultipleViews = new JMenuItem(LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuFile.importQuestmapMultiple"));   //$NON-NLS-1$
		miImportMultipleViews.setMnemonic((LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuFile.importQuestmapMultipleMnemonic")).charAt(0)); //$NON-NLS-1$
		miImportMultipleViews.addActionListener(this);
		miFileImport.add(miImportMultipleViews);

		mnuImport.add(miFileImport);
		
		miImportImageFolder = new JMenuItem(LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuFile.importImageFolder"));  //$NON-NLS-1$
		miImportImageFolder.setMnemonic((LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuFile.importImageFolderMnemonic")).charAt(0)); //$NON-NLS-1$
		miImportImageFolder.addActionListener(this);
		mnuImport.add(miImportImageFolder);

		mnuMainMenu.add(mnuImport);
		
		separator4 = new JPopupMenu.Separator();
		mnuMainMenu.add(separator4);

		// create CONNECTION option
		mnuConnect = new JMenu(LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuFile.connections"));   //$NON-NLS-1$
		mnuConnect.setMnemonic((LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuFile.connectionsMnemonic")).charAt(0)); //$NON-NLS-1$

		miConnectToIXServer = new JMenuItem(LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuFile.connectionsIXPanel"));   //$NON-NLS-1$
		miConnectToIXServer.setMnemonic((LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuFile.connectionsIXPanelMnemonic")).charAt(0)); //$NON-NLS-1$
		miConnectToIXServer.addActionListener(this);
		mnuConnect.add(miConnectToIXServer);

		miConnectToJabberServer = new JMenuItem(LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuFile.connectionsJabber"));   //$NON-NLS-1$
		miConnectToJabberServer.setMnemonic((LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuFile.connectionsJabberMnemonic")).charAt(0)); //$NON-NLS-1$
		miConnectToJabberServer.addActionListener(this);
		mnuConnect.add(miConnectToJabberServer);

		miConnectToClaiMaker = new JMenuItem(LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuFile.connectionsClaiMaker"));   //$NON-NLS-1$
		miConnectToClaiMaker.setMnemonic((LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuFile.connectionsClaiMakerMnemonic")).charAt(0)); //$NON-NLS-1$
		miConnectToClaiMaker.addActionListener(this);
		mnuConnect.add(miConnectToClaiMaker);

		mnuMainMenu.add(mnuConnect);

		// SEND TO
		mnuSendToJabber = new JMenu(LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuFile.sendToJabber"));   //$NON-NLS-1$
		mnuSendToJabber.setMnemonic((LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuFile.sendToJabberMnemonic")).charAt(0)); //$NON-NLS-1$
		mnuSendToJabber.setEnabled(false);
		mnuMainMenu.add(mnuSendToJabber);
		ProjectCompendium.APP.drawJabberRoster(mnuSendToJabber);

		mnuSendToIX = new JMenu(LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuFile.sendToIX"));   //$NON-NLS-1$
		mnuSendToIX.setMnemonic((LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuFile.sendToIXMnemonic")).charAt(0)); //$NON-NLS-1$
		mnuSendToIX.setEnabled(false);
		mnuMainMenu.add(mnuSendToIX);
		ProjectCompendium.APP.drawIXRoster(mnuSendToIX);

		separator5 = new JPopupMenu.Separator();
		mnuMainMenu.add(separator5);
	
		miFilePrint = new JMenuItem(LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuFile.print"));   //$NON-NLS-1$
		miFilePrint.setMnemonic((LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuFile.printMnemonic")).charAt(0)); //$NON-NLS-1$
		miFilePrint.addActionListener(this);
		mnuMainMenu.add(miFilePrint);

		miFileExit = new JMenuItem(LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuFile.exit"));   //$NON-NLS-1$
		if (!ProjectCompendium.isMac) {
			mnuMainMenu.addSeparator();
			miFileExit.addActionListener(this);
			miFileExit.setMnemonic((LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuFile.exitMnemonic")).charAt(0)); //$NON-NLS-1$
			mnuMainMenu.add(miFileExit);
		}

		if (bSimple) {
			addExtenderButton();
			setDisplay(bSimple);
		}

		return mnuMainMenu;
	}

	/**
	 * Hide/show items depending on whether the user wants the simple view or simple.
	 * @param bSimple
	 */
	protected void setDisplay(boolean bSimple) {
		if (bSimple) {
			miFileNew.setVisible(false);
			miFileOpen.setVisible(false);
			miFileClose.setVisible(false);
			miSystemSettings.setVisible(false);
			miFileBackup.setVisible(false);
			separator1.setVisible(false);
			miDatabaseAdministration.setVisible(false);
			miFileConvert.setVisible(false);
			miDatabases.setVisible(false);
			miFileImport.setVisible(false);
			miImportCurrentView.setVisible(false);
			miImportMultipleViews.setVisible(false);
			mnuConnect.setVisible(false);
			mnuSendToJabber.setVisible(false);
			mnuSendToIX.setVisible(false);
			separator2.setVisible(false);
			separator3.setVisible(false);
			separator4.setVisible(false);
			separator5.setVisible(false);
		} else {
			miFileNew.setVisible(true);
			miFileOpen.setVisible(true);
			miFileClose.setVisible(true);
			miSystemSettings.setVisible(true);
			miFileBackup.setVisible(true);
			separator1.setVisible(true);
			miDatabaseAdministration.setVisible(true);
			miFileConvert.setVisible(true);
			miDatabases.setVisible(true);
			miFileImport.setVisible(true);
			miImportCurrentView.setVisible(true);
			miImportMultipleViews.setVisible(true);
			mnuConnect.setVisible(true);
			if (ProjectCompendium.APP.isJabberConnected()) {
				mnuSendToJabber.setVisible(true);
			}
			if (ProjectCompendium.APP.isIXConnected()) {
				mnuSendToIX.setVisible(true);
			}
			separator2.setVisible(true);
			separator3.setVisible(true);
			separator4.setVisible(true);
			separator5.setVisible(true);
		}
		
		setControlItemStatus(bSimple);
		
		JPopupMenu pop = mnuMainMenu.getPopupMenu();
		if (pop.isVisible()) {
			pop.setVisible(false);
			pop.setVisible(true);
			pop.requestFocus();
		}
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
			if (miFileConvert.getText().equals(LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuFile.convertMySQLDerby"))) {   //$NON-NLS-1$
				ProjectCompendium.APP.onFileConvertFromMySQL();
			}
			else {
				ProjectCompendium.APP.onFileConvertFromDerby();
			}
		}
		else if (source.equals(miFileBackup))
			ProjectCompendium.APP.onFileBackup();

		else if (source.equals(miConnectToJabberServer))
			ProjectCompendium.APP.onConnect("Jabber");  //$NON-NLS-1$
		else if (source.equals(miConnectToIXServer))
			ProjectCompendium.APP.onConnect("IXPanel");  //$NON-NLS-1$
		else if (source.equals(miConnectToClaiMaker))
			ProjectCompendium.APP.onConnect("ClaiMaker");  //$NON-NLS-1$

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
			PrefuseGraphXMLExport prefuse = new PrefuseGraphXMLExport(frame, "D:\\PrefuseText.xml");  //$NON-NLS-1$
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
		else if (source.equals(miImportXMLAML)) {
			String finalFile = ""; //$NON-NLS-1$
			
			UIFileFilter filter = new UIFileFilter(new String[] {"xml"}, "XML Files"); //$NON-NLS-1$ //$NON-NLS-2$

			UIFileChooser fileDialog = new UIFileChooser();
			fileDialog.setDialogTitle(LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuFile.xmlFilesDialogTitle")); //$NON-NLS-1$
			fileDialog.setFileFilter(filter);
			fileDialog.setApproveButtonText(LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuFile.importButton")); //$NON-NLS-1$
			fileDialog.setRequiredExtension(".xml"); //$NON-NLS-1$

		    // FIX FOR MAC - NEEDS '/' ON END TO DENOTE A FOLDER
			if (!UIImportFlashMeetingXMLDialog.lastFileDialogDir.equals("")) { //$NON-NLS-1$
				File file = new File(UIImportFlashMeetingXMLDialog.lastFileDialogDir+ProjectCompendium.sFS);
				if (file.exists()) {
					fileDialog.setCurrentDirectory(file);
				}
			}

			UIUtilities.centerComponent(fileDialog, ProjectCompendium.APP);
			int retval = fileDialog.showOpenDialog(ProjectCompendium.APP);
			if (retval == JFileChooser.APPROVE_OPTION) {
	        	if ((fileDialog.getSelectedFile()) != null) {

	            	String fileName = fileDialog.getSelectedFile().getAbsolutePath();
					File fileDir = fileDialog.getCurrentDirectory();
					String dir = fileDir.getPath();

					if (fileName != null) {
						UIImportFlashMeetingXMLDialog.lastFileDialogDir = dir;
						finalFile = fileName;
					}
				}
			}
 
			if (finalFile != null) {
				if ((new File(finalFile)).exists()) {
					DBNode.setNodesMarkedSeen(true);
					AMLXMLImport xmlImport = new AMLXMLImport(finalFile, ProjectCompendium.APP.getModel());
					xmlImport.start();	
					ProjectCompendium.APP.setStatus(""); //$NON-NLS-1$
				}	
			}
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
		else if (source.equals(miMarkProjectSeen)) {
			try {
				ProjectCompendium.APP.onMarkProjectSeen();	
			}
			catch (SQLException ex) {}
		}
			
		
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
			if (miMarkProjectSeen != null) {
				miMarkProjectSeen.setEnabled(false);
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
			if (mnuSendToIX != null) {
				mnuSendToIX.setEnabled(false);
			}
			if (mnuSendToJabber != null) {
				mnuSendToJabber.setEnabled(false);
			}
		}
		catch (Exception ex) {
			ex.printStackTrace();
			ProjectCompendium.APP.displayError("Error: (UIMenuFile.onDatabaseClose)\n\n" + ex.getMessage());  //$NON-NLS-1$
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
		if (miMarkProjectSeen != null) {
			miMarkProjectSeen.setEnabled(true);
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
		if (mnuSendToIX != null && ProjectCompendium.APP.isIXConnected()) {
			mnuSendToIX.setEnabled(true);
		}
		if (mnuSendToJabber != null && ProjectCompendium.APP.isJabberConnected()) {
			mnuSendToJabber.setEnabled(true);
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
							Thread thread = new Thread("Jabber Roster") {  //$NON-NLS-1$
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
   	        	if (sName == null || sName.equals("")) {  //$NON-NLS-1$
   	   	        	JID jid = ri.getJID();
   	   	        	sName = jid.getUsername();
   	        	}
				JMenuItem item = new JMenuItem(sName);
				final JID jid = ri.getJID();
				final NodeSummary sumnode = node;
				item.addActionListener( new ActionListener() {
					public void actionPerformed(ActionEvent evt) {
						Thread thread = new Thread("IX Roster") {  //$NON-NLS-1$
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
				miFileConvert.setText(LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuFile.convertMySQLDerby"));   //$NON-NLS-1$
			}
			else {
				miFileConvert.setText(LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuFile.convertDerbyMySQL"));   //$NON-NLS-1$
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
}
