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

package com.compendium.ui.dialogs;

import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.sql.SQLException;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;
import javax.help.*;

import com.compendium.*;
import com.compendium.core.*;
import com.compendium.core.datamodel.*;
import com.compendium.core.db.DBNode;
import com.compendium.core.db.management.*;
import com.compendium.ui.*;
import com.compendium.ui.panels.*;
import com.compendium.ui.plaf.ViewPaneUI;

/**
 * UINewDatabaseDialog defines the dialog to allow a user to create a new database
 *
 * @author	Michelle Bachler
 */
public class UINewDatabaseDialog extends UIDialog implements ActionListener, ItemListener, IUIConstants, DBProgressListener {

	/** The button to create a new database.*/
	private UIButton				pbCreate	= null;

	/** The button to cancel the dialog.*/
	private UIButton				pbCancel	= null;

	/** Activates the help opening to the appropriate section.*/
	private UIButton				pbHelp		= null;

	/** The parent frame for this dialog.*/
	private JFrame					oParent	= null;

	/** Then content pane for this dialog.*/
	private Container				oContentPane = null;

	/** The panel holding the main text fields.*/
	private JPanel					oDetailsPanel = null;

	/** The panel holding the bottom buttons.*/
	private UIButtonPanel			oButtonPanel = null;

	/** The label for the name of the new database.*/
	private JLabel					oNameLabel = null;

	/** The field for entering the name of the new dialog.*/
	private JTextField				oNameField = null;

	/** The check box to make this new database the default database.*/
	private JCheckBox 				oDefaultDatabase = null;

	/** The check box to make this new user the default user for this new database.*/
	private JCheckBox 				oDefaultUser = null;

	/** The panel for entering the main user for the new database.*/
	private UINewUserPanel			userPanel = null;

	/** The layout manager used.*/
	private GridBagLayout			grid = null;

	/** Reference to self for use in threads.*/
	private UINewDatabaseDialog 	manager = null;

	/** The dialog holding the progress bar.*/
	private UIProgressDialog	oProgressDialog = null;

	/** The progress bar.*/
	private JProgressBar		oProgressBar = null;

	/** The thread running the progress dialog.*/
	private ProgressThread		oThread = null;

	/** The progress counter used with the progress bar.*/
	private int					nCount = 0;

	/** The username to use when connecting to MySQL to create the new database.*/
	private String sDatabaseLogin = ICoreConstants.sDEFAULT_DATABASE_USER;

	/** The password to use when connecting to MySQL to create the new database.*/
	private String sDatabasePassword = ICoreConstants.sDEFAULT_DATABASE_PASSWORD;

	/** The ip address or hostname to use when connecting to MySQL to create the new database.*/
	private String sDatabaseIP = ICoreConstants.sDEFAULT_DATABASE_ADDRESS;

	/** The data for the list of existing database projects.*/
	private Vector			vtProjects	= new Vector();
	
	/** Whether to draw the simple form or the complex one.*/
	private boolean			drawSimpleForm = false;


	/**
	 * Initialises and sets up the dialog.
	 * @param parent the parent frame for this dialog.
	 * @param projects a list of the current database project names.
	 * @param sMySQLName the username to use when connecting to MySQL to create the new database.
	 * @param sMySQLPassword the password to use when connecting to MySQL to create the new database.
	 * @param sMySQLIP the ip address or hostname to use when connecting to the database to create the new project.
	 */
	public UINewDatabaseDialog(JFrame parent, Vector projects, String sMySQLName, String sMySQLPassword, String sMySQLIP) {

		super(parent, true);
		if (!ProjectCompendium.APP.projectsExist() && SystemProperties.createDefaultProject) {
			drawSimpleForm = true;
		} else {
			drawSimpleForm = false;
		}
		
		if (!drawSimpleForm) {
			this.setTitle(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UINewDatabaseDialog.createNewProjectTitle")); //$NON-NLS-1$
		} else {
			this.setTitle(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UINewDatabaseDialog.compendiumSetupTitle"));			 //$NON-NLS-1$
		}

	  	//this.setTitle(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UINewDatabaseDialog.createNewProjectTitle")); //$NON-NLS-1$

	    CSH.setHelpIDString(this,"basic.databases"); //$NON-NLS-1$

	  	oParent = parent;
		manager = this;

		this.sDatabaseLogin = sMySQLName;
		this.sDatabasePassword = sMySQLPassword;
		if (sMySQLIP != null && !sMySQLIP.equals("")) { //$NON-NLS-1$
			this.sDatabaseIP = sMySQLIP;
		}

		vtProjects = projects;

		oContentPane = getContentPane();
		JPanel oMainPanel = new JPanel(new BorderLayout());
		oMainPanel.setBorder(new EmptyBorder(5,5,5,5));
		oContentPane.setLayout(new BorderLayout());
		oContentPane.add(oMainPanel);

		oDetailsPanel = new JPanel();
		oDetailsPanel.setBorder(new EmptyBorder(10,10,5,10));
		grid = new GridBagLayout();
		oDetailsPanel.setLayout(grid);

		GridBagConstraints gc = new GridBagConstraints();
		gc.insets = new Insets(5,5,5,5);
		gc.anchor = GridBagConstraints.WEST;

		if (!drawSimpleForm) {
			oNameLabel = new JLabel(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UINewDatabaseDialog.projectName")+": * "); //$NON-NLS-1$
			grid.setConstraints(oNameLabel, gc);

			oNameField = new JTextField();
			oNameField.setColumns(25);
			oNameLabel.setLabelFor(oNameField);
			gc.gridwidth = GridBagConstraints.REMAINDER;
			grid.setConstraints(oNameField, gc);

			oDetailsPanel.add(oNameLabel);
			oDetailsPanel.add(oNameField);

			oDefaultDatabase = new JCheckBox(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UINewDatabaseDialog.setAsDefault")); //$NON-NLS-1$
			oDefaultDatabase.addItemListener(this);
			grid.setConstraints(oDefaultDatabase, gc);
			oDetailsPanel.add(oDefaultDatabase);

			//oDefaultUser = new JCheckBox(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UINewDatabaseDialog.defaultUser")); //$NON-NLS-1$
			//oDefaultUser.addItemListener(this);
			//grid.setConstraints(oDefaultUser, gc);
			//oDetailsPanel.add(oDefaultUser);

			//JLabel label = new JLabel(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UINewDatabaseDialog.administrator")); //$NON-NLS-1$
			//grid.setConstraints(label, gc);
			//oDetailsPanel.add(label);
		}
		
		oButtonPanel = new UIButtonPanel();

		pbCreate = new UIButton(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UINewDatabaseDialog.createButton")); //$NON-NLS-1$
		pbCreate.setMnemonic(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UINewDatabaseDialog.createButtonMnemonic").charAt(0)); //$NON-NLS-1$
		pbCreate.addActionListener(this);
		getRootPane().setDefaultButton(pbCreate);
		oButtonPanel.addButton(pbCreate);

		if (!drawSimpleForm) {
			pbCancel = new UIButton(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UINewDatabaseDialog.cancelButton")); //$NON-NLS-1$
			pbCancel.setMnemonic(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UINewDatabaseDialog.cancelButtonMnemonic").charAt(0)); //$NON-NLS-1$
			pbCancel.addActionListener(this);
			oButtonPanel.addButton(pbCancel);

			pbHelp = new UIButton(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UINewDatabaseDialog.helpButton")); //$NON-NLS-1$
			pbHelp.setMnemonic(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UINewDatabaseDialog.helpButtonMnemonic").charAt(0)); //$NON-NLS-1$
			ProjectCompendium.APP.mainHB.enableHelpOnButton(pbHelp, "basics.databasescreate", ProjectCompendium.APP.mainHS); //$NON-NLS-1$
			oButtonPanel.addHelpButton(pbHelp);
		}
		
		JPanel oHoldingPanel = new JPanel(new BorderLayout()); 
		userPanel = new UINewUserPanel(true);
		userPanel.setBorder(new TitledBorder(new BevelBorder(BevelBorder.LOWERED), LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UINewDatabaseDialog.administrator")));
		oHoldingPanel.add(userPanel, BorderLayout.CENTER);
		
		if (!drawSimpleForm) {
			oDefaultUser = new JCheckBox(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UINewDatabaseDialog.defaultUser")); //$NON-NLS-1$
			oDefaultUser.addItemListener(this);
			oDefaultUser.setSelected(true);
			grid.setConstraints(oDefaultUser, gc);
			oHoldingPanel.add(oDefaultUser, BorderLayout.SOUTH);
		}

		oMainPanel.add(oDetailsPanel, BorderLayout.NORTH);
		oMainPanel.add(oHoldingPanel, BorderLayout.CENTER);
		oMainPanel.add(oButtonPanel, BorderLayout.SOUTH);

		oProgressBar = new JProgressBar();
		oProgressBar.setMinimum(0);
		oProgressBar.setMaximum(100);

		pack();

		setResizable(false);
	}

	/**
	 * Listener for checkbox changes.
	 * @param e, the associated ItemEvent object.
	 */
	public void itemStateChanged(ItemEvent e) {

		Object source = e.getItemSelectable();
		if (source == oDefaultDatabase) {
			if (oDefaultDatabase.isSelected()) {
				oDefaultUser.setSelected(true);
			}
			else {
				oDefaultUser.setSelected(false);
			}
		}
		else if (source == oDefaultUser) {
			if (!oDefaultUser.isSelected())
				oDefaultDatabase.setSelected(false);
		}
	}

	/**
	 * Handle button push events.
	 * @param evt, the assoicated ActionEvent object.
	 */
	public void actionPerformed(ActionEvent evt) {

		Object source = evt.getSource();
		if (source instanceof JButton) {

			if (source == pbCreate) {
				onCreate();
			}
			else if (source == pbCancel) {
				onCancel();
			}
		}
	}

	/**
	 * Create a new database using the entered data.
	 */
	public void onCreate() {

		if (!userPanel.testUserData()) {
			return;
		}

		final UserProfile oUser = userPanel.getNewUserData();
		String sNewName = "";	 //$NON-NLS-1$
		if (!drawSimpleForm) {
			sNewName = (oNameField.getText()).trim();
		} else {
			sNewName = SystemProperties.defaultProjectName;
		}

		if (sNewName == null || sNewName.equals("")) { //$NON-NLS-1$
			ProjectCompendium.APP.displayError(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UINewDatabaseDialog.erroNoName")); //$NON-NLS-1$
			oNameField.requestFocus();
		}
		else {

			int count = vtProjects.size();
			for (int i=0; i<count; i++) {
				String next = (String)vtProjects.elementAt(i);
				if (next.equals(sNewName)) {
					ProjectCompendium.APP.displayMessage(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UINewDatabaseDialog.errorMessage1A")+" '"+sNewName+"' "+
							LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UINewDatabaseDialog.errorMessage1B")+"\n\n"+
							LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UINewDatabaseDialog.errorMessage1C")+"\n", LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UINewDatabaseDialog.newDatabaseTitle")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					oNameField.requestFocus();
					return;
				}
			}

			boolean bIsDefaultUser = false;
			if (!drawSimpleForm) {
				bIsDefaultUser = oDefaultUser.isSelected();			
			} else {
				bIsDefaultUser = true;
			}

			boolean bIsDefaultDatabase = false;
			if (!drawSimpleForm) {
				bIsDefaultDatabase =oDefaultDatabase.isSelected();		
			} else {
				bIsDefaultDatabase = true;
			}
			
			final String fsNewName = sNewName;
			final boolean fbIsDefaultUser = bIsDefaultUser;
			final boolean fbIsDefaultDatabase = bIsDefaultDatabase;

			Thread thread = new Thread("UINewDatabaseDialog") { //$NON-NLS-1$
				public void run() {
					setVisible(false);
					try {
						DBNewDatabase newDatabase = new DBNewDatabase(FormatProperties.nDatabaseType, ProjectCompendium.APP.adminDatabase, oUser, fbIsDefaultUser, sDatabaseLogin, sDatabasePassword, sDatabaseIP);
						newDatabase.addProgressListener((DBProgressListener)manager);

						oThread = new ProgressThread(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UINewDatabaseDialog.progressThreadMessage"), LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UINewDatabaseDialog.progressThreadTitle")); //$NON-NLS-1$ //$NON-NLS-2$
						oThread.start();						
						String sHomeViewID = newDatabase.createNewDatabase(fsNewName);
						newDatabase.removeProgressListener((DBProgressListener)manager);

						ProjectCompendium.APP.updateProjects();

						if (fbIsDefaultDatabase) {
							ProjectCompendium.APP.setDefaultDatabase(fsNewName);
						}
						
						if (openProject(fsNewName, oUser.getLoginName(), oUser.getPassword())) {
							loadDefaultData(sHomeViewID);
						}
						
						onCancel();
					}
					catch(DBDatabaseNameException ex) { // WOULD NEVER HAPPEN, BUT MUST STILL BE HANDLED
						progressComplete();
						ProjectCompendium.APP.displayMessage(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UINewDatabaseDialog.nameClashA")+"\n\n"+
								LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UINewDatabaseDialog.nameClashB")+"\n", LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UINewDatabaseDialog.newProject")); //$NON-NLS-1$ //$NON-NLS-2$
						onCancel();
					}
					catch(DBDatabaseTypeException ex) {
						progressComplete();
						ProjectCompendium.APP.displayMessage(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UINewDatabaseDialog.unknownDatabaseType")+":\n\n"+ex.getMessage(), LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UINewDatabaseDialog.newProject")); //$NON-NLS-1$ //$NON-NLS-2$
						onCancel();
					}
					catch(DBProjectListException ex) {
						progressComplete();
						ProjectCompendium.APP.displayMessage(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UINewDatabaseDialog.errorLoadingProjectList")+":\n\n"+ex.getMessage(), LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UINewDatabaseDialog.newProject")); //$NON-NLS-1$ //$NON-NLS-2$
						onCancel();
					}
					catch(IOException ex) {
						progressComplete();
						ProjectCompendium.APP.displayError(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UINewDatabaseDialog.errorLoadingDefaultDataA")+"\n"+
								LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UINewDatabaseDialog.errorLoadingDefaultDataB")+":\n\n"+ex.getMessage(), LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UINewDatabaseDialog.newProject")); //$NON-NLS-1$ //$NON-NLS-2$
						onCancel();
					}
					catch(ClassNotFoundException ex) {
						progressComplete();
						ProjectCompendium.APP.displayError(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UINewDatabaseDialog.errorConnectingMySQL")+":\n\n"+ex.getMessage(), LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UINewDatabaseDialog.newProjectTitle")); //$NON-NLS-1$ //$NON-NLS-2$
						onCancel();
					}
					catch(SQLException ex) {
						progressComplete();
						//ex.printStackTrace();
						ProjectCompendium.APP.displayError(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UINewDatabaseDialog.errorCreatingProject")+":\n\n"+ex.getMessage(), LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UINewDatabaseDialog.newProjectTitle")); //$NON-NLS-1$ //$NON-NLS-2$
						onCancel();
					}
				}
			};
			thread.start();
		}
	}
	
	/**
	 * Open the new project and log the user in.
	 */
	private boolean openProject(String sDatabase, String sUserName, String sUserPassword) {
		ProjectCompendium.APP.setWaitCursor();

		boolean bDefaultLoginSucessful = false;

		ProjectCompendium.APP.sFriendlyName = sDatabase;
		String sModel = null;
		try {
			sModel = ProjectCompendium.APP.adminDatabase.getDatabaseName(sDatabase);
		} catch (Exception e) {}

		if (sModel == null) {
			ProjectCompendium.APP.displayError(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UINewDatabaseDialog.newProjectLost")+sDatabase); //$NON-NLS-1$
		}
		else {
			try {
				DBDatabaseManager databaseManager = ProjectCompendium.APP.oServiceManager.getDatabaseManager();
				databaseManager.openProject(sModel);
		       	DBConnection dbcon = databaseManager.requestConnection(sModel);
				bDefaultLoginSucessful = ProjectCompendium.APP.validateUser(sModel, sUserName, sUserPassword);
				if (bDefaultLoginSucessful) {
					if (FormatProperties.nDatabaseType == ICoreConstants.MYSQL_DATABASE) {
						ProjectCompendium.APP.setTitle(ICoreConstants.MYSQL_DATABASE, ProjectCompendium.APP.oCurrentMySQLConnection.getServer(), FormatProperties.sDatabaseProfile, sDatabase);
					}
					else {
						ProjectCompendium.APP.setDerbyTitle(sDatabase);
					}
					ProjectCompendium.APP.initializeForProject();				
					ProjectCompendium.APP.setDefaultCursor();
				} else {
					ProjectCompendium.APP.displayError(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UINewDatabaseDialog.errorLogin")+sDatabase); //$NON-NLS-1$
				}

				databaseManager.releaseConnection(sModel, dbcon);
			}
			catch(Exception ex) {
				ex.printStackTrace();
			}
		}

		return bDefaultLoginSucessful;
	}	

	/**
	 * Load the default data if any is specified in the System.ini file.
	 *
	 * @param sHomeViewID the id of the home view to load the data into.
	 * @return true if completely successful.
	 * @exception IOException if there is an IO or Zip error.
	 */
    private boolean loadDefaultData(String sHomeViewID) throws IOException {

    	String defaultDataPath = SystemProperties.projectDefaultDataFile;
    	if (!defaultDataPath.equals("")) { //$NON-NLS-1$
			String sXMLFile = defaultDataPath;

    		if (defaultDataPath.endsWith(".zip")) { //$NON-NLS-1$
				ZipFile zipFile = new ZipFile(defaultDataPath);
				Enumeration entries = zipFile.entries();
				ZipEntry entry = null;
				String sTemp = ""; //$NON-NLS-1$
		
				while(entries.hasMoreElements()) {
					entry = (ZipEntry)entries.nextElement();
					sTemp = entry.getName();
					if (sTemp.endsWith(".xml") && sTemp.startsWith("Exports")) { //$NON-NLS-1$ //$NON-NLS-2$
						sXMLFile = sTemp;
					}
					// AVOID Thumbs.db files
					if (sTemp.endsWith(".db")) { //$NON-NLS-1$
						continue;
					}
		
					int len = 0;
					byte[] buffer = new byte[1024];
					InputStream in = zipFile.getInputStream(entry);
					
					String sFileName = ""; //$NON-NLS-1$
					String sLinkedFiles = "Linked Files/"; //$NON-NLS-1$
					if (sTemp.startsWith(sLinkedFiles)) {
						sFileName = UIUtilities.sGetLinkedFilesLocation() + sTemp.substring(sLinkedFiles.length());
					} else {
						sFileName = entry.getName();
					}
					File file = new File(sFileName);
		            if (file.getParentFile() != null) {
		            	file.getParentFile().mkdirs();
		            }
		            
					OutputStream out = new BufferedOutputStream(new FileOutputStream(sFileName));
					while((len = in.read(buffer)) >=0) {
						out.write(buffer, 0, len);
					}
					in.close();
					out.close();
				}
		
				zipFile.close();
    		}
    		
			// IMPORT THE XML
			if (!sXMLFile.equals("") && sXMLFile.endsWith(".xml")) { //$NON-NLS-1$ //$NON-NLS-2$
				File oXMLFile = new File(sXMLFile);
				if (oXMLFile.exists()) {
					boolean importAuthorAndDate = false;
					boolean includeOriginalAuthorDate = false;
					boolean preserveIDs = true;
					boolean transclude = true;
					boolean updateTranscludedNodes = false;

					File oXMLFile2 = new File(sXMLFile);
					if (oXMLFile2.exists()) {
						DBNode.setImportAsTranscluded(transclude);
						DBNode.setPreserveImportedIds(preserveIDs);
						DBNode.setUpdateTranscludedNodes(updateTranscludedNodes);
						DBNode.setNodesMarkedSeen(true);

						UIViewFrame frame = ProjectCompendium.APP.getCurrentFrame();
						if (frame instanceof UIMapViewFrame) {
							UIMapViewFrame mapFrame = (UIMapViewFrame)frame;
							UIViewPane oViewPane = mapFrame.getViewPane();
							ViewPaneUI oViewPaneUI = oViewPane.getUI();
							if (oViewPaneUI != null) {
								oViewPaneUI.setSmartImport(importAuthorAndDate);
								oViewPaneUI.onImportXMLFile(sXMLFile, includeOriginalAuthorDate);
							}
						} else if (frame instanceof UIListViewFrame){
							UIListViewFrame listFrame = (UIListViewFrame)frame;
							UIList uiList = listFrame.getUIList();
							if (uiList != null) {
								uiList.getListUI().setSmartImport(importAuthorAndDate);
								uiList.getListUI().onImportXMLFile(sXMLFile, includeOriginalAuthorDate);
							}
						}
						return true;
					}
				} else {
					ProjectCompendium.APP.displayError(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UINewDatabaseDialog.missingFileA")+"\n\n"+
							LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UINewDatabaseDialog.missingFileB")+"\n"); //$NON-NLS-1$
					return false;
				}
			} else {
				return true; // there is allowed to be no default data to load.
			}
			return false;
    	}
		return true;
	}	
    
	/**
	 * This thread runs the progress dialog.
	 */
	private class ProgressThread extends Thread {

		public ProgressThread(String sTitle, String sFinal) {
	  		oProgressDialog = new UIProgressDialog(ProjectCompendium.APP, sTitle, sFinal);
	  		oProgressDialog.showDialog(oProgressBar, false);
	  		oProgressDialog.setModal(true);
		}

		public void run() {
	  		oProgressDialog.setVisible(true);
			while(oProgressDialog.isVisible());
		}
	}

	/**
	 * Set the amount of progress items being counted.
	 *
	 * @param int nCount, the amount of progress items being counted.
	 */
    public void progressCount(int nCount) {
		oProgressBar.setMaximum(nCount);
	}

	/**
	 * Indicate that progress has been updated.
	 *
	 * @param int nCount, the current position of the progress in relation to the inital count
	 * @param String sMessage, the message to display to the user
	 */
    public void progressUpdate(int nIncrement, String sMessage) {
		nCount += nIncrement;
		oProgressBar.setValue(nCount);
		oProgressDialog.setMessage(sMessage);
		oProgressDialog.setStatus(nCount);
	}

	/**
	 * Indicate that progress has complete.
	 */
    public void progressComplete() {
		this.nCount = 0;
		oProgressDialog.setVisible(false);
		oProgressDialog.dispose();
	}

	/**
	 * Indicate that progress has had a problem.
	 *
	 * @param String sMessage, the message to display to the user.
	 */
    public void progressAlert(String sMessage) {
		progressComplete();
		ProjectCompendium.APP.displayError(sMessage);
	}
	    
    /**
     * Override to not allow the user to cancel the dialog if in simple interface.
     */
    public void onCancel() {
    	if (!drawSimpleForm) {
    		super.onCancel();
    	}
    }	
}
