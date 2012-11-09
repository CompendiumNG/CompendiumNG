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


package com.compendium.ui.dialogs;

import java.util.*;
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
import com.compendium.core.db.management.*;
import com.compendium.ui.*;
import com.compendium.ui.panels.*;

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

	/** Activates the help opeing to the appropriate section.*/
	private UIButton				pbHelp		= null;

	/** The parent frame for this dialog.*/
	private JFrame					oParent	= null;

	/** Thne content pane for this dialog.*/
	private Container				oContentPane = null;

	/** The panel holding the main textfields.*/
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

	/** The thread running the progress dailog.*/
	private ProgressThread		oThread = null;

	/** The progress counter used with the progress bar.*/
	private int					nCount = 0;

	/** The usename to use when connecting to MySQL to create the new database.*/
	private String mysqlname = ICoreConstants.sDEFAULT_DATABASE_USER;

	/** The password to use when connecting to MySQL to create the new database.*/
	private String mysqlpassword = ICoreConstants.sDEFAULT_DATABASE_PASSWORD;

	/** The ip address or hostname to use when connecting to MySQL to create the new database.*/
	private String mysqlip = ICoreConstants.sDEFAULT_DATABASE_ADDRESS;

	/** The data for the list of existing database projects.*/
	private Vector			vtProjects	= new Vector();


	/**
	 * Initializes and sets up the dialog.
	 * @param parent the parent frame for this dialog.
	 * @param projects a list of the current database project names.
	 * @param sMySQLName the usename to use when connecting to MySQL to create the new database.
	 * @param sMySQLPassword the password to use when connecting to MySQL to create the new database.
	 * @param sMySQLIP the ip address or hostname to use when connecting to the database to create the new project.
	 */
	public UINewDatabaseDialog(JFrame parent, Vector projects, String sMySQLName, String sMySQLPassword, String sMySQLIP) {

		super(parent, true);

	  	this.setTitle("Create a New Project");

	    CSH.setHelpIDString(this,"basic.databases");

	  	oParent = parent;
		manager = this;

		this.mysqlname = sMySQLName;
		this.mysqlpassword = sMySQLPassword;
		if (sMySQLIP != null && !sMySQLIP.equals("")) {
			this.mysqlip = sMySQLIP;
		}

		vtProjects = projects;

		oContentPane = getContentPane();
		oContentPane.setLayout(new BorderLayout());

		oDetailsPanel = new JPanel();
		oDetailsPanel.setBorder(new EmptyBorder(10,10,5,10));
		grid = new GridBagLayout();
		oDetailsPanel.setLayout(grid);

		GridBagConstraints gc = new GridBagConstraints();
		gc.insets = new Insets(5,5,5,5);
		gc.anchor = GridBagConstraints.WEST;

		oNameLabel = new JLabel("Enter Project Name: * ");
		grid.setConstraints(oNameLabel, gc);

		oNameField = new JTextField();
		oNameField.setColumns(25);
		oNameLabel.setLabelFor(oNameField);
		gc.gridwidth = GridBagConstraints.REMAINDER;
		grid.setConstraints(oNameField, gc);

		oDetailsPanel.add(oNameLabel);
		oDetailsPanel.add(oNameField);

		oDefaultDatabase = new JCheckBox("Make this your default project");
		oDefaultDatabase.addItemListener(this);
		grid.setConstraints(oDefaultDatabase, gc);
		oDetailsPanel.add(oDefaultDatabase);

		oDefaultUser = new JCheckBox("Make the user below, the default user");
		oDefaultUser.addItemListener(this);
		grid.setConstraints(oDefaultUser, gc);
		oDetailsPanel.add(oDefaultUser);

		JLabel label = new JLabel("Project Administrator User Details:");
		grid.setConstraints(label, gc);
		oDetailsPanel.add(label);

		oButtonPanel = new UIButtonPanel();

		pbCreate = new UIButton("Create");
		pbCreate.setMnemonic(KeyEvent.VK_R);
		pbCreate.addActionListener(this);
		getRootPane().setDefaultButton(pbCreate);
		oButtonPanel.addButton(pbCreate);

		pbCancel = new UIButton("Cancel");
		pbCancel.setMnemonic(KeyEvent.VK_C);
		pbCancel.addActionListener(this);
		oButtonPanel.addButton(pbCancel);

		pbHelp = new UIButton("Help");
		pbHelp.setMnemonic(KeyEvent.VK_H);
		ProjectCompendium.APP.mainHB.enableHelpOnButton(pbHelp, "basics.databasescreate", ProjectCompendium.APP.mainHS);
		oButtonPanel.addHelpButton(pbHelp);

		userPanel = new UINewUserPanel(true);

		oContentPane.add(oDetailsPanel, BorderLayout.NORTH);
		oContentPane.add(userPanel, BorderLayout.CENTER);
		oContentPane.add(oButtonPanel, BorderLayout.SOUTH);

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

		final String sNewName = (oNameField.getText()).trim();
		if (!userPanel.testUserData()) {
			return;
		}

		final UserProfile oUser = userPanel.getNewUserData();

		if (sNewName == null || sNewName.equals("")) {
			ProjectCompendium.APP.displayError("You must enter a name for the new Project");
			oNameField.requestFocus();
		}
		else {

			int count = vtProjects.size();
			for (int i=0; i<count; i++) {
				String next = (String)vtProjects.elementAt(i);
				if (next.equals(sNewName)) {
					ProjectCompendium.APP.displayMessage("A database named '"+sNewName+"' already exists.\n\nPlease enter another name.", "New Database");
					oNameField.requestFocus();
					return;
				}
			}

			Thread thread = new Thread("UINewDatabaseDialog") {
				public void run() {
					setVisible(false);
					try {
						DBNewDatabase newDatabase = new DBNewDatabase(FormatProperties.nDatabaseType, ProjectCompendium.APP.adminDatabase, oUser, oDefaultUser.isSelected(), mysqlname, mysqlpassword, mysqlip);

						newDatabase.addProgressListener((DBProgressListener)manager);

						oThread = new ProgressThread("Creating new Project..", "New Project Created");
						oThread.start();
						newDatabase.createNewDatabase(sNewName);
						newDatabase.removeProgressListener((DBProgressListener)manager);

						ProjectCompendium.APP.updateProjects();

						if (oDefaultDatabase.isSelected()) {
							ProjectCompendium.APP.setDefaultDatabase(sNewName);
						}
						ProjectCompendium.APP.onFileOpen();
						onCancel();
					}
					catch(DBDatabaseNameException ex) { // WOULD NEVER HAPPEN, BUT MUST STILL BE HANDLED
						progressComplete();
						ProjectCompendium.APP.displayMessage("A low level database name clash has occurred.n\nThis operation cannot be completed", "New Project");
						onCancel();
					}
					catch(DBDatabaseTypeException ex) {
						progressComplete();
						ProjectCompendium.APP.displayMessage("A database of unknown type could not be created."+ex.getMessage(), "New Project");
						onCancel();
					}
					catch(IOException ex) {
						progressComplete();
						ProjectCompendium.APP.displayError("There was a problem loading the default data into the new project.\nThe file could not be found due to:\n\n"+ex.getMessage(), "New Project");
						onCancel();
					}
					catch(ClassNotFoundException ex) {
						progressComplete();
						ProjectCompendium.APP.displayError("There was a problem connecting to the MySQL project due to:\n\n"+ex.getMessage(), "New Project");
						onCancel();
					}
					catch(SQLException ex) {
						progressComplete();
						//ex.printStackTrace();
						ProjectCompendium.APP.displayError("There was a problem creating the new project or user data due to:\n\n"+ex.getMessage(), "New Project");
						onCancel();
					}
				}
			};
			thread.start();
		}
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
}
