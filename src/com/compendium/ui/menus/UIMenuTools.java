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
import java.io.*;
import java.util.*;
import javax.help.*;
import javax.swing.*;

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
public class UIMenuTools implements IUIMenu, ActionListener {
	
	/** The Tools menu.*/
	private JMenu				mnuMainMenu				= null;

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

	/** the menu item to Launch uDig if you have it installed and the uDig connection open.*/
	private JMenuItem			miUDIGLaunch			= null;

	/** The menu item to open a list of nodes not in a view - NOT CURRENTLY USED.*/
	private JMenuItem			miLimboNode				= null;
	
	/** Focus the top frame.*/
	private JMenuItem			miFocusFrames			= null;
	
	/** Focus the Tabbed Area.*/
	private JMenuItem			miFocusTabs				= null;
	
	/** The platform specific shortcut key to use.*/
	private int shortcutKey;

	/**Indicates whether this menu is draw as a Simple interface or a advance user inteerface.*/
	private boolean bSimpleInterface					= false;	
	
	/**
	 * Constructor.
	 * @param bSimple true if the simple interface should be draw, false if the advanced. 	 * 	 * 
	 */
	public UIMenuTools(boolean bSimple) {
		this.bSimpleInterface = bSimple;		
		shortcutKey = ProjectCompendium.APP.shortcutKey;

		mnuMainMenu = new JMenu("Tools");  //$NON-NLS-1$
		CSH.setHelpIDString(mnuMainMenu,"menus.tools"); //$NON-NLS-1$
		mnuMainMenu.setMnemonic(KeyEvent.VK_T);
		
		createMenuItems();
	}
	
	/**
	 * If true, redraw the simple form of this menu, else redraw the complex form.
	 * @param isSimple true for the simple menu, false for the advanced.
	 */
	public void setIsSimple(boolean isSimple) {
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
	 * Create and return the Tools menu.
	 * @return JMenu the Tools menu.
	 */
	private JMenu createMenuItems() {

		miUsers = new JMenuItem("User Manager...");  //$NON-NLS-1$
		miUsers.setMnemonic(KeyEvent.VK_U);
		miUsers.addActionListener(this);
		mnuMainMenu.add(miUsers);

		miCodes = new JMenuItem("Tags...");  //$NON-NLS-1$
		miCodes.setMnemonic(KeyEvent.VK_T);
		miCodes.addActionListener(this);
		//mnuMainMenu.add(miCodes);

		mnuMainMenu.addSeparator();
			
		mnuTemplates = new JTemplateMenu();
		mnuTemplates.setText("Templates"); 
		//CSH.setHelpIDString(mnuMemetic,"menus.memetic");
		mnuTemplates.setMnemonic(KeyEvent.VK_P);
			
		/*mnuTemplates.addMouseListener(new MouseAdapter() {
			public void mouseEntered(MouseEvent e) {
				File main = new File("Templates");
				File templates[] = main.listFiles();
				if (templates.length > 0) {			
					mnuTemplates.removeAll();
					processTemplateFolder(templates, mnuTemplates);
				}
			}
		});*/
				
		//processTemplateFolder(templates, mnuTemplates);
			
		mnuMainMenu.add(mnuTemplates);
		
		mnuMainMenu.addSeparator();

		mnuMemetic = new JMenu("memetic");  //$NON-NLS-1$
		CSH.setHelpIDString(mnuMemetic,"menus.memetic"); //$NON-NLS-1$
		mnuMemetic.setMnemonic(KeyEvent.VK_M);
		mnuMainMenu.add(mnuMemetic);

		//miMeetingSetup = new JMenuItem("Access Grid Meeting Setup");
		//miMeetingSetup.setMnemonic(KeyEvent.VK_P);
		//miMeetingSetup.addActionListener(this);
		//mnuMemetic.add(miMeetingSetup);

		miMeetingRecording = new JMenuItem("Manual Stop");  //$NON-NLS-1$
		miMeetingRecording.setToolTipText("Use this ONLY if Meeting Manager is unable to stop Compendium recording");  //$NON-NLS-1$
		miMeetingRecording.setMnemonic(KeyEvent.VK_S);
		miMeetingRecording.addActionListener(this);
		mnuMemetic.add(miMeetingRecording);

		//miMeetingReplay = new JMenuItem("Replay Access Grid Meeting");
		//miMeetingReplay.setMnemonic(KeyEvent.VK_P);
		//miMeetingReplay.addActionListener(this);
		//mnuMemetic.add(miMeetingReplay);

		miMeetingUpload = new JMenuItem("Manual Upload");  //$NON-NLS-1$
		miMeetingUpload.setToolTipText("Use this if the auto-upload procedure fails");  //$NON-NLS-1$
		miMeetingUpload.setMnemonic(KeyEvent.VK_U);
		miMeetingUpload.addActionListener(this);
		CSH.setHelpIDString(miMeetingUpload,"menus.memetic");		 //$NON-NLS-1$
		mnuMemetic.add(miMeetingUpload);

		mnuMainMenu.addSeparator();			
		miUDIGLaunch = new JMenuItem("Launch UDig");  //$NON-NLS-1$
		miUDIGLaunch.setMnemonic(KeyEvent.VK_U);
		miUDIGLaunch.addActionListener(this);
		mnuMainMenu.add(miUDIGLaunch);
		if (FormatProperties.startUDigCommunications) {
			miUDIGLaunch.setEnabled(true);
		} else {
			miUDIGLaunch.setEnabled(false);
		}

		mnuMainMenu.addSeparator();

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
		miStencilManagement = new JMenuItem("Manage Stencils...");  //$NON-NLS-1$
		miStencilManagement.setMnemonic(KeyEvent.VK_S);
		miStencilManagement.addActionListener(this);
		mnuMainMenu.add(miStencilManagement);

		mnuStencils	= new JMenu("Open Stencil");  //$NON-NLS-1$
		mnuStencils.setMnemonic(KeyEvent.VK_E);
		mnuMainMenu.add(mnuStencils);
		createStencilMenu();

		mnuMainMenu.addSeparator();

		miLinkGroupManagement = new JMenuItem("Manage Link Groups...");  //$NON-NLS-1$
		miLinkGroupManagement.setMnemonic(KeyEvent.VK_L);
		miLinkGroupManagement.addActionListener(this);
		mnuMainMenu.add(miLinkGroupManagement);

		miLinkGroupDefault = new JMenuItem("Restore Default Link Group");  //$NON-NLS-1$
		miLinkGroupDefault.setMnemonic(KeyEvent.VK_G);
		miLinkGroupDefault.addActionListener(this);
		mnuMainMenu.add(miLinkGroupDefault);

		mnuMainMenu.addSeparator();

		mnuScribble	= new JMenu("Scribble Pad"); 
		mnuScribble.setMnemonic(KeyEvent.VK_A);
		mnuMainMenu.add(mnuScribble);

		miShowScribblePad = new JMenuItem("Activate Scribble Pad");  //$NON-NLS-1$
		miShowScribblePad.setMnemonic(KeyEvent.VK_A);
		miShowScribblePad.setEnabled(false);
		miShowScribblePad.addActionListener(this);
		mnuScribble.add(miShowScribblePad);

		miHideScribblePad = new JMenuItem("De-activate Scribble Pad");  //$NON-NLS-1$
		miHideScribblePad.setMnemonic(KeyEvent.VK_D);
		miHideScribblePad.setEnabled(false);
		miHideScribblePad.addActionListener(this);
		mnuScribble.add(miHideScribblePad);

		miSaveScribblePad = new JMenuItem("Save Scribble Pad");  //$NON-NLS-1$
		miSaveScribblePad.setEnabled(false);
		miSaveScribblePad.setMnemonic(KeyEvent.VK_V);
		miSaveScribblePad.addActionListener(this);
		mnuScribble.add(miSaveScribblePad);

		miClearScribblePad = new JMenuItem("Clear Scribble Pad");  //$NON-NLS-1$
		miClearScribblePad.setMnemonic(KeyEvent.VK_C);
		miClearScribblePad.setEnabled(false);
		miClearScribblePad.addActionListener(this);
		mnuScribble.add(miClearScribblePad);

		mnuMainMenu.addSeparator();

		//miLimboNode = new JMenuItem("Show Lost Nodes..."); //$NON-NLS-1$
		//miLimboNode.setMnemonic(KeyEvent.VK_I);
		//miLimboNode.addActionListener(this);
		//mnuMainMenu.add(miLimboNode);

		//mnuMainMenu.addSeparator();
		//miShowCodes = new JMenuItem("Show Tags");
		//miShowCodes.setMnemonic(KeyEvent.VK_W);
		//miShowCodes.addActionListener(this);
		//mnuMainMenu.add(miShowCodes);

		//miHideCodes = new JMenuItem("Hide Tags");
		//miHideCodes.setMnemonic(KeyEvent.VK_H);
		//miHideCodes.addActionListener(this);
		//mnuMainMenu.add(miHideCodes);

		//miRefreshCache = new JMenuItem("Refresh Data"); //$NON-NLS-1$
		//miRefreshCache.setMnemonic(KeyEvent.VK_U);
		//miRefreshCache.addActionListener(this);
		//mnuMainMenu.add(miRefreshCache);

		//mnuMainMenu.addSeparator();

		if (ProjectCompendium.isMac)
			miProjectOptions = new JMenuItem("Project Preferences...");  
		else
			miProjectOptions = new JMenuItem("Project Options...");  
		miProjectOptions.setMnemonic(KeyEvent.VK_J);
		miProjectOptions.addActionListener(this);
		mnuMainMenu.add(miProjectOptions);
		
		if (ProjectCompendium.isMac)
			miOptions = new JMenuItem("User Preferences...");  //$NON-NLS-1$
		else
			miOptions = new JMenuItem("User Options...");  //$NON-NLS-1$
		miOptions.setMnemonic(KeyEvent.VK_O);
		miOptions.addActionListener(this);
		mnuMainMenu.add(miOptions);

		mnuMainMenu.addSeparator();

		miFocusFrames = new JMenuItem("Focus The Desktop Area");  
		miFocusFrames.setToolTipText("Will move the focus to the current top view frame"); 
		miFocusFrames.setAccelerator(KeyStroke.getKeyStroke( KeyEvent.VK_F11, shortcutKey));
		miFocusFrames.setMnemonic(KeyEvent.VK_F);
		miFocusFrames.addActionListener(this);
		mnuMainMenu.add(miFocusFrames);
		
		miFocusTabs = new JMenuItem("Focus The Left Tabbed Area"); 
		miFocusFrames.setToolTipText("Will move the focus to the left tabbed frame if open");		 
		miFocusTabs.setAccelerator(KeyStroke.getKeyStroke( KeyEvent.VK_F12, shortcutKey));
		miFocusTabs.setMnemonic(KeyEvent.VK_C);
		miFocusTabs.addActionListener(this);
		mnuMainMenu.add(miFocusTabs);
		
		return mnuMainMenu;
	}
	
	/**
	 * Create the menus holding the currently available stemplate sets.
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
			
			if (nextFile.isDirectory()) {
				File subs[] = nextFile.listFiles();
				if (subs.length > 0) {
					JMenu mnuSubMenu = new JMenu(sName);
					mnuNext.add(mnuSubMenu);
					processTemplateFolder(subs, mnuSubMenu);
				}
			}
			else {
				if ((sName.toLowerCase()).endsWith(".xml")) { 
					JMenuItem item = new JMenuItem(sName);
					ActionListener oAction = new ActionListener() {
						public void actionPerformed(ActionEvent evt) {
							ProjectCompendium.APP.onTemplateImport(nextFile.getAbsolutePath());
						}
					};
					item.addActionListener(oAction);
					mnuNext.add(item);
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
				File main = new File("Templates"); 
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
		else if (source.equals(miUDIGLaunch)) {
			if (ProjectCompendium.APP.oUDigCommunicationManager != null) {
				ProjectCompendium.APP.oUDigCommunicationManager.launchUDig();
			}
		}
		else if (source.equals(miUsers))
			ProjectCompendium.APP.onUsers();
		else if (source.equals(miMeetingRecording)) {
			if (ProjectCompendium.APP.oMeetingManager != null) {
				Thread thread = new Thread("UIMenuManager-StoprRecording") { //$NON-NLS-1$
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
				ProjectCompendium.APP.displayError("Recording not started."); //$NON-NLS-1$
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
			ProjectCompendium.APP.displayError("" + ex.getMessage()); 
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
			} else {
				miUsers.setEnabled(false);
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

	/**
	 * Enable/disable the file open menu item.
	 * @param enabled true to enable, false to disable.
	 */
	public void setUDigEnablement(boolean enabled) {
		if (miUDIGLaunch != null) {
			miUDIGLaunch.setEnabled(enabled);
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
	
	/**
	 * Return a reference to the main menu.
	 * @return JMenu a reference to the main menu.
	 */
	public JMenu getMenu() {
		return mnuMainMenu;
	}	
}
