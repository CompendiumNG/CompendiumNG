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
import java.io.*;
import java.util.*;
import javax.help.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import com.compendium.core.*;
import com.compendium.*;
import com.compendium.ui.*;
import com.compendium.ui.dialogs.*;
import com.compendium.ui.stencils.*;
import com.compendium.ui.linkgroups.*;

import com.compendium.meeting.*;

/**
 * This class creates and manages the Tools menu.
 *
 * @author	Michelle Bachler
 */
public class UIMenuTools extends UIMenu implements ActionListener {
	
	/** The stencil menu*/
	private JMenu				mnuStencils				= null;

	/** The menu item to open the stencil  management dialog.*/
	private JMenuItem			miStencilManagement		= null;

	/** The menu item to open the link group management dialog.*/
	private JMenuItem			miLinkGroupManagement	= null;

	/** The menu item to restore the deafault link group data.*/
	private JMenuItem			miLinkGroupDefault	= null;

	/** The menu item to open the User Management dialog.*/
	private JMenuItem			miUsers					= null;

	/** The menu item to open the tag (code) maintenance dialog.*/
	private JMenuItem			miCodes					= null;

	/** The menu item to open the user option settings dialog.*/
	private JMenuItem			miOptions				= null;

	/** The menu item to open the project option settings dialog.*/
	private JMenuItem			miProjectOptions			= null;

	/** The menu item to open all code popups for nodes in the view - NOT CURRENTLY USED.*/
	private JMenuItem			miShowCodes				= null;

	/** The menu item to hide all code popups for nodes in the view - NOT CURRENTLY USED.*/
	private JMenuItem			miHideCodes				= null;

	/** The Template menu*/
	private JMenu				mnuTemplates			= null;

	/** The scribble menu*/
	private JMenu				mnuScribble				= null;

	/** The menu item to add the scribble pad layer to the view.*/
	private JMenuItem			miShowScribblePad		= null;

	/** The menu item to remove the scribble pad layer from the view.*/
	private JMenuItem			miHideScribblePad		= null;

	/** The menu item to save the scribble pad layer to the database.*/
	private JMenuItem			miSaveScribblePad		= null;

	/** The menu item to clear the contents of the scribble pad layer.*/
	private JMenuItem			miClearScribblePad		= null;

	/** Starts the Screen Capture.*/
	private JMenuItem			miStartScreenCapture	= null;

	/** Stop the Screen Capture.*/
	private JMenuItem			miStopScreenCapture		= null;

	/** The menu with the memetic project options.*/
	private JMenu				mnuMemetic				= null;

	/** Used to open the dialog to start recording a meeting.*/
	private JMenuItem			miMeetingRecording		= null;

	/** Used to open the dialog to start replaying a meeting.*/
	private JMenuItem			miMeetingReplay			= null;

	/** Used to open the dialog to enter the url and port info needed for Arena and the triplstore.*/
	private JMenuItem			miMeetingSetup			= null;

	/** Used to open the dialog to upload recorded meeting data stored to a file.*/
	private JMenuItem			miMeetingUpload			= null;

	/** Refresh all data cached by recalling from the database.*/
	private JMenuItem			miRefreshCache			= null;

	/** The menu item to open a list of nodes not in a view - NOT CURRENTLY USED.*/
	private JMenuItem			miLimboNode				= null;
	
	/** Focus the top frame.*/
	private JMenuItem			miFocusFrames			= null;
	
	/** Focus the Tabbed Area.*/
	private JMenuItem			miFocusTabs				= null;
	
	/** Open a file browser for files which are saved in the database */
	private JMenuItem			miLinkedFilesFileBrowser 	= null;
	
	/** The platform specific shortcut key to use.*/
	private int shortcutKey;

	/** The HelpSet instance to use.*/
    private HelpSet 					mainHS 			= null;

	/** The HelpBroker instance to use.*/
    private HelpBroker 					mainHB			= null;
	
	/**
	 * Constructor.
	 * @param bSimple true if the simple interface should be draw, false if the advanced. 	 * 	 * 
	 * @param hs the HelpSet to use for menus and menuitems.
	 * @param hb the HelpBroker to use for menus and menuitems.
	 */
	public UIMenuTools(boolean bSimple, HelpSet hs, HelpBroker hb) {
		this.mainHS = hs;
		this.mainHB = hb;
		this.bSimpleInterface = bSimple;		
		shortcutKey = ProjectCompendium.APP.shortcutKey;

		mnuMainMenu = new JMenu(LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuTools.tools"));   //$NON-NLS-1$
		CSH.setHelpIDString(mnuMainMenu,"menus.tools");  //$NON-NLS-1$
		mnuMainMenu.setMnemonic((LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuTools.toolsMnemonic")).charAt(0)); //$NON-NLS-1$
		
		createMenuItems(bSimple);
	}
	
	/**
	 * Create and return the Tools menu.
	 * @return JMenu the Tools menu.
	 */
	private JMenu createMenuItems(boolean bSimple) {

		miUsers = new JMenuItem(LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuTools.userManager")); //$NON-NLS-1$
		miUsers.setMnemonic((LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuTools.userManagerMnemonic")).charAt(0)); //$NON-NLS-1$
		miUsers.addActionListener(this);
		mnuMainMenu.add(miUsers);

		miLinkedFilesFileBrowser = new JMenuItem(LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuTools.linkedFilesBrowser")); //$NON-NLS-1$
		miLinkedFilesFileBrowser.setMnemonic((LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuTools.linkedFilesBrowserMnemonic")).charAt(0)); //$NON-NLS-1$
		miLinkedFilesFileBrowser.addActionListener(this);
		mnuMainMenu.add(miLinkedFilesFileBrowser);

		//miCodes = new JMenuItem(LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuTools.tags")); //$NON-NLS-1$
		//miCodes.setMnemonic((LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuTools.tagsMnemonic")).charAt(0)); //$NON-NLS-1$
		//miCodes.addActionListener(this);
		//mnuMainMenu.add(miCodes);

		mnuMainMenu.addSeparator();
			
		mnuTemplates = new JTemplateMenu();
		mnuTemplates.setText(LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuTools.templates"));  //$NON-NLS-1$
		mnuTemplates.setMnemonic((LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuTools.templatesMnemonic")).charAt(0)); //$NON-NLS-1$
		mnuMainMenu.add(mnuTemplates);
	
		separator1 = new JPopupMenu.Separator();
		mnuMainMenu.add(separator1);

		mnuMemetic = new JMenu(LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuTools.memetic"));   //$NON-NLS-1$
		CSH.setHelpIDString(mnuMemetic,"menus.memetic");  //$NON-NLS-1$
		mnuMemetic.setMnemonic((LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuTools.memeticMnemonic")).charAt(0)); //$NON-NLS-1$
		mnuMainMenu.add(mnuMemetic);

		//miMeetingSetup = new JMenuItem("Access Grid Meeting Setup");
		//miMeetingSetup.setMnemonic(KeyEvent.VK_P);
		//miMeetingSetup.addActionListener(this);
		//mnuMemetic.add(miMeetingSetup);

		miMeetingRecording = new JMenuItem(LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuTools.memeticManualStop"));  //$NON-NLS-1$
		miMeetingRecording.setToolTipText(LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuTools.memeticManualStopTip"));   //$NON-NLS-1$
		miMeetingRecording.setMnemonic((LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuTools.memeticManualStopMnemonic")).charAt(0)); //$NON-NLS-1$
		miMeetingRecording.addActionListener(this);
		mnuMemetic.add(miMeetingRecording);

		//miMeetingReplay = new JMenuItem("Replay Access Grid Meeting");
		//miMeetingReplay.setMnemonic(KeyEvent.VK_P);
		//miMeetingReplay.addActionListener(this);
		//mnuMemetic.add(miMeetingReplay);

		miMeetingUpload = new JMenuItem(LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuTools.memeticManualUpload"));   //$NON-NLS-1$
		miMeetingUpload.setToolTipText(LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuTools.memeticManualUploadTip"));  //$NON-NLS-1$
		miMeetingUpload.setMnemonic((LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuTools.memeticManualUploadMnemonic")).charAt(0)); //$NON-NLS-1$
		miMeetingUpload.addActionListener(this);
		CSH.setHelpIDString(miMeetingUpload,"menus.memetic");		 //$NON-NLS-1$
		mnuMemetic.add(miMeetingUpload);

		separator2 = new JPopupMenu.Separator();
		mnuMainMenu.add(separator2);
		
		/*
		miStartScreenCapture = new JMenuItem("Start Screen Capture");
		miStartScreenCapture.setMnemonic(KeyEvent.VK_N);
		miStartScreenCapture.addActionListener(this);
		mnuMainMenu.add(miStartScreenCapture);

		miStopScreenCapture = new JMenuItem("Stop Screen Capture");
		miStopScreenCapture.setMnemonic(KeyEvent.VK_P);
		miStopScreenCapture.addActionListener(this);
		mnuMainMenu.add(miStopScreenCapture);

		mnuMainMenu.addSeparator();
		*/

		// STENCILS
		miStencilManagement = new JMenuItem(LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuTools.stencilsManage"));   //$NON-NLS-1$
		miStencilManagement.setMnemonic((LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuTools.stencilsManageMnemonic")).charAt(0)); //$NON-NLS-1$
		miStencilManagement.addActionListener(this);
		mnuMainMenu.add(miStencilManagement);

		mnuStencils	= new JMenu(LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuTools.stencilsOpen"));   //$NON-NLS-1$
		mnuStencils.setMnemonic((LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuTools.stencilsOpenMnemonic")).charAt(0)); //$NON-NLS-1$
		mnuMainMenu.add(mnuStencils);
		createStencilMenu();

		mnuMainMenu.addSeparator();

		miLinkGroupManagement = new JMenuItem(LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuTools.linkGroupsManage"));   //$NON-NLS-1$
		miLinkGroupManagement.setMnemonic((LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuTools.linkGroupsManageMnemonic")).charAt(0)); //$NON-NLS-1$
		miLinkGroupManagement.addActionListener(this);
		mnuMainMenu.add(miLinkGroupManagement);

		miLinkGroupDefault = new JMenuItem(LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuTools.linkGroupsDefault"));   //$NON-NLS-1$
		miLinkGroupDefault.setMnemonic((LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuTools.linkGroupsDefaultMnemonic")).charAt(0)); //$NON-NLS-1$
		miLinkGroupDefault.addActionListener(this);
		mnuMainMenu.add(miLinkGroupDefault);

		mnuMainMenu.addSeparator();

		mnuScribble	= new JMenu(LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuTools.scribblePad"));  //$NON-NLS-1$
		mnuScribble.setMnemonic((LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuTools.scribblePadMnemonic")).charAt(0)); //$NON-NLS-1$
		mnuMainMenu.add(mnuScribble);

		miShowScribblePad = new JMenuItem(LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuTools.scribblePadActivate"));  //$NON-NLS-1$
		miShowScribblePad.setMnemonic((LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuTools.scribblePadActivateMnemonic")).charAt(0)); //$NON-NLS-1$
		miShowScribblePad.setEnabled(false);
		miShowScribblePad.addActionListener(this);
		mnuScribble.add(miShowScribblePad);

		miHideScribblePad = new JMenuItem(LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuTools.scribblePadDeactivate"));  //$NON-NLS-1$
		miHideScribblePad.setMnemonic((LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuTools.scribblePadDeactivateMnemonic")).charAt(0)); //$NON-NLS-1$
		miHideScribblePad.setEnabled(false);
		miHideScribblePad.addActionListener(this);
		mnuScribble.add(miHideScribblePad);

		miSaveScribblePad = new JMenuItem(LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuTools.scribblePadSave"));   //$NON-NLS-1$
		miSaveScribblePad.setEnabled(false);
		miSaveScribblePad.setMnemonic((LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuTools.scribblePadSaveMnemonic")).charAt(0)); //$NON-NLS-1$
		miSaveScribblePad.addActionListener(this);
		mnuScribble.add(miSaveScribblePad);

		miClearScribblePad = new JMenuItem(LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuTools.scribblePadClear"));   //$NON-NLS-1$
		miClearScribblePad.setMnemonic((LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuTools.scribblePadClearMnemonic")).charAt(0)); //$NON-NLS-1$
		miClearScribblePad.setEnabled(false);
		miClearScribblePad.addActionListener(this);
		mnuScribble.add(miClearScribblePad);

		mnuMainMenu.addSeparator();

		/*if (!bSimpleInterface) {		
		//miLimboNode = new JMenuItem("Show Lost Nodes..."); 
		//miLimboNode.setMnemonic(KeyEvent.VK_I);
		//miLimboNode.addActionListener(this);
		//mnuMainMenu.add(miLimboNode);
		}*/

		//mnuMainMenu.addSeparator();
		//miShowCodes = new JMenuItem("Show Tags");
		//miShowCodes.setMnemonic(KeyEvent.VK_W);
		//miShowCodes.addActionListener(this);
		//mnuMainMenu.add(miShowCodes);

		//miHideCodes = new JMenuItem("Hide Tags");
		//miHideCodes.setMnemonic(KeyEvent.VK_H);
		//miHideCodes.addActionListener(this);
		//mnuMainMenu.add(miHideCodes);

		//miRefreshCache = new JMenuItem("Refresh Data"); 
		//miRefreshCache.setMnemonic(KeyEvent.VK_U);
		//miRefreshCache.addActionListener(this);
		//mnuMainMenu.add(miRefreshCache);

		//mnuMainMenu.addSeparator();

		if (ProjectCompendium.isMac)
			miProjectOptions = new JMenuItem(LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuTools.projectOptionsMac"));   //$NON-NLS-1$
		else
			miProjectOptions = new JMenuItem(LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuTools.projectOptions"));   //$NON-NLS-1$
		miProjectOptions.setMnemonic((LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuTools.projectOptionsMnemonic")).charAt(0)); //$NON-NLS-1$
		miProjectOptions.addActionListener(this);
		mnuMainMenu.add(miProjectOptions);
		
		if (ProjectCompendium.isMac)
			miOptions = new JMenuItem(LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuTools.userOptionsMac"));   //$NON-NLS-1$
		else
			miOptions = new JMenuItem(LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuTools.userOptions"));   //$NON-NLS-1$
		miOptions.setMnemonic((LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuTools.userOptionsMnemonic")).charAt(0)); //$NON-NLS-1$
		miOptions.addActionListener(this);
		mnuMainMenu.add(miOptions);

		mnuMainMenu.addSeparator();

		miFocusFrames = new JMenuItem(LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuTools.focusFrame"));   //$NON-NLS-1$
		miFocusFrames.setToolTipText(LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuTools.focusFrameTip"));  //$NON-NLS-1$
		miFocusFrames.setAccelerator(KeyStroke.getKeyStroke( KeyEvent.VK_F11, shortcutKey));
		miFocusFrames.setMnemonic((LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuTools.focusFrameMnemonic")).charAt(0)); //$NON-NLS-1$
		miFocusFrames.addActionListener(this);
		mnuMainMenu.add(miFocusFrames);
		
		miFocusTabs = new JMenuItem(LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuTools.focusLeftTabs"));  //$NON-NLS-1$
		miFocusTabs.setToolTipText(LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuTools.focusLeftTabsTip"));		  //$NON-NLS-1$
		miFocusTabs.setAccelerator(KeyStroke.getKeyStroke( KeyEvent.VK_F12, shortcutKey));
		miFocusTabs.setMnemonic((LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuTools.focusLeftTabsMnemonic")).charAt(0)); //$NON-NLS-1$
		miFocusTabs.addActionListener(this);
		mnuMainMenu.add(miFocusTabs);
		
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
			mnuTemplates.setVisible(false);
			separator1.setVisible(false);
			mnuMemetic.setVisible(false);
			separator2.setVisible(false);

		} else {
			mnuTemplates.setVisible(true);
			separator1.setVisible(true);
			mnuMemetic.setVisible(true);
			separator2.setVisible(true);
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
	 * Create the menus holding the currently available template sets.
	 */
	private void processTemplateFolder(File[] templates, JMenu mnuNext) {
		
		Vector vtTemplates = new Vector(templates.length);
		for (int i=0; i< templates.length; i++) {
			vtTemplates.add(templates[i]);
		}

		vtTemplates = CoreUtilities.sortList(vtTemplates);

		int count = vtTemplates.size();
		for (int i=0; i< count; i++) {
			final File nextFile = (File)vtTemplates.elementAt(i);
			String sName = nextFile.getName();
			sName = sName.replace("_", " ");
			
			if (nextFile.isDirectory()) {
				File subs[] = nextFile.listFiles();
				if (subs.length > 0) {
					JMenu mnuSubMenu= null;				
					if (subs.length > 20) {
						mnuSubMenu = new UIScrollableMenu(sName, 0, 20);
					} else {
						mnuSubMenu = new JMenu(sName);
					}
					mnuNext.add(mnuSubMenu);
					processTemplateFolder(subs, mnuSubMenu);
				}
			}
			else {
				if ((sName.toLowerCase()).endsWith(".xml")) {  //$NON-NLS-1$
					String sShortName = sName.substring(0, sName.length()-4);
					sShortName = sShortName.replace("_", " ");
					JMenuItem item = new JMenuItem(sShortName);
					ActionListener oAction = new ActionListener() {
						public void actionPerformed(ActionEvent evt) {
							ProjectCompendium.APP.onTemplateImport(nextFile.getAbsolutePath());
						}
					};
					item.addActionListener(oAction);
					mnuNext.add(item);
				} else if ((sName.toLowerCase()).endsWith(".html")) {  //$NON-NLS-1$
					String sShortName = sName.substring(0, sName.length()-5);
					sShortName = sShortName.replace("_", " ");
					ImageIcon icon = UIImages.createImageIcon(UIImages.sPATH+"template-help.png");
					JMenuItem item = new JMenuItem(sShortName, icon);
					item.setToolTipText(LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuTools.templatehelp"));
					ActionListener oAction = new ActionListener() {
						public void actionPerformed(ActionEvent evt) {
							ExecuteControl.launch( nextFile.getAbsolutePath() );
						}
					};
					item.addActionListener(oAction);
					if (sShortName.equals(mnuNext.getText())) {
						try {mnuNext.insert(item, 0);} 
						catch (Exception e) { System.out.println("Exception:"+e.getLocalizedMessage());}
					} else {
						mnuNext.add(item);
					}
				}
			}
		}
	}
	
	/**
	 * Create the menu holding the currently available stencil sets.
	 */
	public void createStencilMenu() {

		if (mnuStencils == null) {
			return;
		}
		
		Vector vtStencils = ProjectCompendium.APP.oStencilManager.getStencilNames();
		vtStencils = CoreUtilities.sortList(vtStencils);

		mnuStencils.removeAll();
		int count = vtStencils.size();
		for (int i=0; i < count; i++) {
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

	/** TO GET DYNAMIC MENUS*/
	private class JTemplateMenu extends JMenu {
				
		public void setPopupMenuVisible(boolean vis) {
			if (vis) {
				removeAll();
				JMenuItem miHelpHelp = new JMenuItem(LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuTools.templateHelp"));   //$NON-NLS-1$
				miHelpHelp.setMnemonic((LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuTools.templateHelpMnemonic")).charAt(0)); //$NON-NLS-1$
				add(miHelpHelp);
				if (mainHB != null && mainHS != null) {
					if (miHelpHelp != null) {
						mainHB.enableHelpOnButton(miHelpHelp, "basics.templates", mainHS);  //$NON-NLS-1$
					}
				}
				File main = new File("Templates");  //$NON-NLS-1$
				File templates[] = main.listFiles();
				if (templates.length > 0) {			
					processTemplateFolder(templates, this);
				}
			}
			
			super.setPopupMenuVisible(vis);
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

		if (source.equals(miStencilManagement)) {
			UIStencilDialog dlg = new UIStencilDialog(ProjectCompendium.APP, ProjectCompendium.APP.oStencilManager);
			UIUtilities.centerComponent(dlg, ProjectCompendium.APP);
			dlg.setVisible(true);
		}
		else if (source.equals(miLinkGroupManagement)) {
			UILinkManagementDialog dlg = new UILinkManagementDialog(ProjectCompendium.APP, ProjectCompendium.APP.oLinkGroupManager);
			UIUtilities.centerComponent(dlg, ProjectCompendium.APP);
			dlg.setVisible(true);
		}
		else if (source.equals(miLinkGroupDefault)) {
			ProjectCompendium.APP.oLinkGroupManager.createDefaultLinkGroup();
		}
		else if (source.equals(miLinkGroupManagement)) {
			ProjectCompendium.APP.oLinkGroupManager.createDefaultLinkGroup();
			ProjectCompendium.APP.oLinkGroupManager.refreshTree();
		}
		//else if (source.equals(miRefreshCache))
		//	ProjectCompendium.APP.reloadProjectData();
		else if (source.equals(miUsers))
			ProjectCompendium.APP.onUsers();
		else if (source.equals(miLinkedFilesFileBrowser))
			ProjectCompendium.APP.onLinkedFilesBrowser();
		else if (source.equals(miMeetingRecording)) {
			if (ProjectCompendium.APP.oMeetingManager != null) {
				Thread thread = new Thread("UIMenuManager-StopRecording") { //$NON-NLS-1$
					public void run() {
						ProjectCompendium.APP.setWaitCursor();

						if (ProjectCompendium.APP.oMeetingManager.isReplay()) {
							ProjectCompendium.APP.oMeetingManager.stopReplayRecording();
						}
						else {
							ProjectCompendium.APP.oMeetingManager.stopRecording();
						}
						ProjectCompendium.APP.setDefaultCursor();
					}
				};
				thread.start();
			}
			else {
				ProjectCompendium.APP.displayError(LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuTools.memeticMessage1"));  //$NON-NLS-1$
			}
			/*try {
				oParent.oMeetingManager = new MeetingManager(MeetingManager.RECORDING);
				UIMeetingRecorderDialog dlg = new UIMeetingRecorderDialog(oParent.oMeetingManager);
				dlg.setVisible(true);
			} catch (AccessGridDataException ex) {
				oParent.displayError(ex.getMessage());
			}*/
		}
		/*else if (source.equals(miMeetingReplay)) {
			try {
				oParent.oMeetingManager = new MeetingManager(MeetingManager.REPLAY);
				UIMeetingReplayDialog dlg = new UIMeetingReplayDialog(oParent.oMeetingManager);
				dlg.setVisible(true);
			} catch (AccessGridDataException ex) {
				oParent.displayError(ex.getMessage());
			}
		}*/
		else if (source.equals(miMeetingUpload)) {
			try {
				MeetingManager oMeetingManager = ProjectCompendium.APP.oMeetingManager;
				if (ProjectCompendium.APP.oMeetingManager == null)
					oMeetingManager = new MeetingManager();
				oMeetingManager.uploadRecording();
			} catch (AccessGridDataException ex) {
				ProjectCompendium.APP.displayError(ex.getMessage());
			}
		}
		/*else if (source.equals(miMeetingSetup)) {
			UIMeetingSetupDialog dlg = new UIMeetingSetupDialog(oParent);
			dlg.setVisible(true);
		}*/

		/*else if (source.equals(miStartScreenCapture)) {

			Dimension dim = ProjectCompendium.APP.getSize();
			Point p = ProjectCompendium.APP.getLocation();
			recorder = new ScreenCaptureRecorder(p.x, p.y, dim.width, dim.height, (float)1.0, "Michelle.mov");
			recorder.setUpRecorder();
			recorder.start();

			//ProjectCompendium.APP.getDesktop().add(player);
			//player.setVisible(true);
			//ProjectCompendium.APP.getDesktop().moveToFront(player);
		}
		else if (source.equals(miStopScreenCapture)) {
			recorder.stop();
		}*/

		else if (source.equals(miCodes))
			ProjectCompendium.APP.onCodes();
		else if (source.equals(miShowCodes))
			ProjectCompendium.APP.onShowCodes();
		else if (source.equals(miHideCodes))
			ProjectCompendium.APP.onHideCodes();

		else if (source.equals(miShowScribblePad)) {
			ProjectCompendium.APP.onShowScribblePad();
			miShowScribblePad.setEnabled(false);
			miHideScribblePad.setEnabled(true);
			miSaveScribblePad.setEnabled(true);
			miClearScribblePad.setEnabled(true);
		}
		else if (source.equals(miHideScribblePad)) {
			ProjectCompendium.APP.onHideScribblePad();
			miShowScribblePad.setEnabled(true);
			miHideScribblePad.setEnabled(false);
			miSaveScribblePad.setEnabled(false);
			miClearScribblePad.setEnabled(false);
		}
		else if (source.equals(miSaveScribblePad))
			ProjectCompendium.APP.onSaveScribblePad();
		else if (source.equals(miClearScribblePad))
			ProjectCompendium.APP.onClearScribblePad();
		else if (source.equals(miProjectOptions)) {
			UIProjectOptionsDialog dialog = new UIProjectOptionsDialog(ProjectCompendium.APP, ProjectCompendium.APP.getModel());
			dialog.setVisible(true);
		}
		else if (source.equals(miOptions)) {
			UIOptionsDialog dialog = new UIOptionsDialog(ProjectCompendium.APP);
			dialog.setVisible(true);
		} else if (source.equals(miFocusFrames)) {
		    JDesktopPane pane = ProjectCompendium.APP.getDesktop();
			JInternalFrame frame = pane.getSelectedFrame();
			if (frame instanceof UIMapViewFrame) {
				UIMapViewFrame mapframe = (UIMapViewFrame)frame;
				mapframe.getViewPane().requestFocus();
			} else if (frame instanceof UIListViewFrame) {
				UIListViewFrame listframe = (UIListViewFrame)frame;
				listframe.getUIList().getList().requestFocus();
			}
		} else if (source.equals(miFocusTabs)) {
			JTabbedPane oTabbedPane = ProjectCompendium.APP.oTabbedPane;
			if (oTabbedPane.getTabCount() > 0) {
				oTabbedPane.requestFocus();
			}
		}

		ProjectCompendium.APP.setDefaultCursor();
	}

	/**
	 * Updates the menus when a database project is closed.
	 */
	public void onDatabaseClose() {

		try {
			mnuMainMenu.setEnabled(false);
			if (miCodes != null) {
				miCodes.setEnabled(true);
			}
		}
		catch (Exception ex) {
			ex.printStackTrace();
			ProjectCompendium.APP.displayError("" + ex.getMessage());  //$NON-NLS-1$
		}
	}

	/**
	 * Updates the menus when a database projects is opened.
	 */
	public void onDatabaseOpen() {
		if (ProjectCompendium.APP.getModel() != null) {
			mnuMainMenu.setEnabled(true);
			
			if (ProjectCompendium.APP.getModel().getUserProfile().isAdministrator()) {
				miUsers.setEnabled(true);
				miProjectOptions.setEnabled(true);
			} else {
				miUsers.setEnabled(false);
				miProjectOptions.setEnabled(false);
			}
		}
	}

	/**
 	 * Enable/disable menu items when nodes or links selected / deselected.
 	 * Does Nothing here
  	 * @param selected true for enabled, false for disabled.
	 */
	public void setNodeOrLinkSelected(boolean selected) {}

	/**
 	 * Indicates when nodes on a view are selected and deselected.
 	 * Does Nothing.
  	 * @param selected true for selected false for deselected.
	 */
	public void setNodeSelected(boolean selected) {}
	
	/**
	 * Enable/disable the scribblepad option.
	 * @param enabled true to enable, false to disable.
	 */
	public void setScribblePadEnabled(boolean enabled) {
		if (mnuScribble != null) {
			miShowScribblePad.setEnabled(enabled);
			if (!enabled) {
				miHideScribblePad.setEnabled(false);
				miSaveScribblePad.setEnabled(false);
				miClearScribblePad.setEnabled(false);
			}
		}
	}

	/**
	 * Activate/Deactivate the scribblepad layer options.
	 * @param enabled true to enable, false to disable.
	 */
	public void setScribblePadActive(boolean enabled) {
		
		if (mnuScribble != null) {
			miShowScribblePad.setEnabled(!enabled);
			miHideScribblePad.setEnabled(enabled);
			miSaveScribblePad.setEnabled(enabled);
			miClearScribblePad.setEnabled(enabled);
		}
	}

	public void openUserOptions() {
		if (miOptions != null) {
			miOptions.doClick();
		}
	}
	
	/**
	 * Update the look and feel of the menu.
	 */
	public void updateLAF() {
		if (mnuMainMenu != null)
			SwingUtilities.updateComponentTreeUI(mnuMainMenu);
	}	
}
