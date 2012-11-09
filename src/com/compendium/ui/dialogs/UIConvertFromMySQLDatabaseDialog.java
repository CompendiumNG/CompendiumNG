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

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.sql.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;
import javax.help.*;

import com.compendium.*;
import com.compendium.ui.*;


import com.compendium.core.*;
import com.compendium.core.datamodel.*;
import com.compendium.core.datamodel.services.*;
import com.compendium.core.db.management.*;
import com.compendium.core.CoreUtilities;

/**
 * This dialog allows the user to select MySQL databases and import them to Derby
 *
 * @author	Michelle Bachler
 */
public class UIConvertFromMySQLDatabaseDialog extends UIDialog implements ActionListener, DBProgressListener {

	/** The pane to put this dialog's contents in.*/
	private Container		oContentPane = null;

	/** The parent frame for this node.*/
	private JFrame 			oParent = null;

	/** The list of MySQL projects to convert.*/
	private UIProjectList	lstProjects	= null;

	/** The scrollpane to put the list of MySQL projects in.*/
	private JScrollPane		oScrollpane	= new JScrollPane();

	/** The button to start the conversion.*/
	private UIButton 		pbConvert	= null;

	/** The button to start the conversion of all the database projects to Derby.*/
	private UIButton 		pbConvertAll	= null;

	/** The button to cancel the dialog.*/
	private UIButton		pbCancel	= null;

	/** The button to open the relevant help.*/
	private UIButton		pbHelp	= null;

	/** Holds the list of Access projects to convert.*/
	private Vector			vtProjects	= new Vector();

	/** holds the list of existing Derby database projects to check names against.*/
	private Vector			vtDerbyProjects	= new Vector();

	/** The progress dialog instance.*/
	private UIProgressDialog	oProgressDialog 	= null;

	/** The progress bar displayed in the progress dialog.*/
	private JProgressBar		oProgressBar 		= null;

	/** The progress thread class which runs the progress dialog.*/
	private ProgressThread		oThread 			= null;

	/** Counter used by the progress bar.*/
	private int					nCount = 0;

	/** Holds a reference to this class for use in inner threads.*/
	private UIConvertFromMySQLDatabaseDialog manager = null;

	/** The current MySQL connection profile object.*/
	private ExternalConnection oConnection = null;

	/** Holds a list of all current connection profiles.*/
	private Vector oConnectionProfiles = null;

	/** Database projects against their requirement to have thier schemas updated.*/
	//private Hashtable		htProjectCheck		= new Hashtable(10);

	/** Holds a list of the currently connected profile names*/
	private JComboBox			oProfiles				= null;


	/**
	 * Constructor. Initializes and draws the contents of this dialog.
	 *
	 * @param parent, the parent frame for this dialog.
	 * @param projects, the list of current MySQL databse projects.
	 * @param sMySQLName, the MySQL database username used to access MySQL.
	 * @param sMySQLPassword, the MySQL database password used to access MySQL.
	 * @param sMySQLIP, the MySQL database ip address or host name used to access MySQL.
	 */
	public UIConvertFromMySQLDatabaseDialog(JFrame parent, Vector connections) {

		super(parent, true);

		oParent = parent;
		manager = this;
		oConnectionProfiles = connections;

		setTitle("Convert MySQL Projects To Derby Projects");

	    CSH.setHelpIDString(this,"basic.databases");

		oContentPane = getContentPane();
		oContentPane.setLayout(new BorderLayout());

		if (connections.size() == 1) {
			this.oConnection = (ExternalConnection)oConnectionProfiles.elementAt(0);
		}
		else {
			createProfilesChoiceBox();
		}

		//For checking new project name against when converting.
		vtDerbyProjects = ProjectCompendium.APP.adminDerbyDatabase.getDatabaseProjects();

		JPanel labelpanel = new JPanel(new BorderLayout());
		labelpanel.setBorder(new EmptyBorder(5,5,5,5));

		if (oProfiles != null) {
			JPanel profilepanel = new JPanel();
			profilepanel.setBorder(new EmptyBorder(5,5,15,5));
			profilepanel.add(oProfiles);
			labelpanel.add(profilepanel, BorderLayout.NORTH);
		}

		// create label and text box for model name
		JLabel label = new JLabel("Choose a MySQL Database Project To Convert To Derby");
		labelpanel.add(label, BorderLayout.SOUTH);
		oContentPane.add(labelpanel, BorderLayout.NORTH);

		String sProjects = "";
		if (oConnection != null) {
			ServiceManager oManager = new ServiceManager(ICoreConstants.MYSQL_DATABASE, oConnection.getLogin(), oConnection.getPassword(), oConnection.getServer());
			DBAdminDatabase oAdminDatabase = new DBAdminDatabase(oManager, oConnection.getLogin(), oConnection.getPassword(), oConnection.getServer());

			//this.htProjectCheck = oAdminDatabase.getProjectSchemaStatus();
			oAdminDatabase.loadDatabaseProjects();
			vtProjects = oAdminDatabase.getDatabaseProjects();
		}

		//lstProjects = new UIProjectList(this.htProjectCheck, this.vtProjects);
		lstProjects = new UIProjectList(this.vtProjects);
		oScrollpane = new JScrollPane(lstProjects);
		oScrollpane.setPreferredSize(new Dimension(300,200));

		JPanel listpanel = new JPanel();
		listpanel.setBorder(new EmptyBorder(5,5,5,5));
		listpanel.add(oScrollpane);

		oContentPane.add(listpanel, BorderLayout.CENTER);

		UIButtonPanel oButtonPanel = new UIButtonPanel();

		pbConvert = new UIButton("Convert One");
		pbConvert.setMnemonic(KeyEvent.VK_V);
		pbConvert.addActionListener(this);
		getRootPane().setDefaultButton(pbConvert);
		oButtonPanel.addButton(pbConvert);

		pbConvertAll = new UIButton("Convert All");
		pbConvertAll.setMnemonic(KeyEvent.VK_A);
		pbConvertAll.addActionListener(this);
		oButtonPanel.addButton(pbConvertAll);

		pbCancel = new UIButton("Cancel");
		pbCancel.setMnemonic(KeyEvent.VK_C);
		pbCancel.addActionListener(this);
		oButtonPanel.addButton(pbCancel);

		pbHelp = new UIButton("Help");
		pbHelp.setMnemonic(KeyEvent.VK_H);
		ProjectCompendium.APP.mainHB.enableHelpOnButton(pbHelp, "basics.databases-mysql", ProjectCompendium.APP.mainHS);
		oButtonPanel.addHelpButton(pbHelp);

		oContentPane.add(oButtonPanel, BorderLayout.SOUTH);

		oProgressBar = new JProgressBar();
		oProgressBar.setMinimum(0);
		oProgressBar.setMaximum(100);

		setResizable(false);
		pack();
	}

	/**
	 * Update the contents od the project list based on the current global connection object.
	 */
	private void updateProjectList() {

		if (oConnection != null) {
			ServiceManager oManager = new ServiceManager(ICoreConstants.MYSQL_DATABASE, oConnection.getLogin(), oConnection.getPassword(), oConnection.getServer());
			DBAdminDatabase oAdminDatabase = new DBAdminDatabase(oManager, oConnection.getLogin(), oConnection.getPassword(), oConnection.getServer());

			//this.htProjectCheck = oAdminDatabase.getProjectSchemaStatus();
			oAdminDatabase.loadDatabaseProjects();
			vtProjects = oAdminDatabase.getDatabaseProjects();
			//lstProjects.updateProjectList(vtProjects, htProjectCheck);
			lstProjects.updateProjectList(vtProjects);
		}
		else {
			((DefaultListModel)lstProjects.getModel()).removeAllElements();
			lstProjects.validate();
			lstProjects.repaint();
		}
	}

	/**
	 * Create the profiles choicebox.
	 */
	private JComboBox createProfilesChoiceBox() {

		oProfiles = new JComboBox();
        oProfiles.setOpaque(true);
		oProfiles.setEditable(false);
		oProfiles.setEnabled(true);
		oProfiles.setMaximumRowCount(30);
		oProfiles.setFont( new Font("Dialog", Font.PLAIN, 12 ));

        updateProfilesChoiceBoxData();

		DefaultListCellRenderer comboRenderer = new DefaultListCellRenderer() {
			public Component getListCellRendererComponent(
   		     	JList list,
   		        Object value,
            	int modelIndex,
            	boolean isSelected,
            	boolean cellHasFocus)
            {
 		 		if (isSelected) {
					setBackground(list.getSelectionBackground());
					setForeground(list.getSelectionForeground());
				}
				else {
					setBackground(list.getBackground());
					setForeground(list.getForeground());
				}

				if (value instanceof ExternalConnection) {
					ExternalConnection connection = (ExternalConnection)value;
					setText((String)connection.getProfile());
				}
				else {
					setText((String)value);
				}

				return this;
			}
		};

		oProfiles.setRenderer(comboRenderer);

		ActionListener choiceaction = new ActionListener() {
        	public void actionPerformed(ActionEvent e) {

            	Thread choiceThread = new Thread("UIDatabaseAdministrationDialog: createProfilesChoiceBox") {
                	public void run() {
						if (oProfiles != null && oProfiles.getSelectedItem() instanceof ExternalConnection) {
	                		ExternalConnection connection = (ExternalConnection)oProfiles.getSelectedItem();

							// LOAD PROJECT LIST
							oConnection = connection;
							updateProjectList();
							pbConvert.setEnabled(true);
							pbConvertAll.setEnabled(true);
						}
						else {
							// CLEAR PROJECT LIST
							oConnection = null;
							updateProjectList();
							pbConvert.setEnabled(false);
							pbConvertAll.setEnabled(false);
						}
                	}
               	};
	            choiceThread.start();
        	}
		};
        oProfiles.addActionListener(choiceaction);

		return oProfiles;
	}

	/**
	 * Update the data in the profiles choicebox.
	 */
	public void updateProfilesChoiceBoxData() {
		try {
			Vector profiles = oConnectionProfiles;
			profiles = CoreUtilities.sortList(profiles);
			profiles.insertElementAt((Object) new String("< Select MySQL Profile >"), 0);
			DefaultComboBoxModel comboModel = new DefaultComboBoxModel(profiles);
			oProfiles.setModel(comboModel);
			oProfiles.setSelectedIndex(0);
		}
		catch(Exception ex) {
			ProjectCompendium.APP.displayError("Exception: (UIConvertFromMySQLDatabaseDialog.updateProfileChoiceBoxData) " + ex.getMessage());
		}
	}

	/**
	 * Handles a button push event.
	 * @param evt, the associated ACtionEvent.
	 */
	public void actionPerformed(ActionEvent evt) {

		Object source = evt.getSource();

		if ((source instanceof JButton)) {

			if (source.equals(pbConvert)) {
				onConvert();
			}
			if (source.equals(pbConvertAll)) {
				Thread thread = new Thread("UIConvertFromMySQLDatabaseDialog") {
					public void run() {

						if (vtProjects.size() > 0) {
							convertAll();
							ProjectCompendium.APP.updateProjects();
							ProjectCompendium.APP.onFileOpen();
						}
						else {
							ProjectCompendium.APP.displayMessage("There are no MySQL database projects to convert", "MySQL Database Project Convertion");
						}
						onCancel();
					}
				};
				thread.start();
			}
			else if (source.equals(pbCancel)) {
				onCancel();
			}
		}
	}

	/**
	 * Processes a request to convert the MySQL database selected from the list.
	 */
	private void onConvert() {
		Thread thread = new Thread("UIConvertFromMySQLDatabaseDialog") {
			public void run() {
				int index = lstProjects.getSelectedIndex();
				if (vtProjects.size() > 0) {
					String sName = (String)vtProjects.elementAt(index);
					processConversion(sName, false);
					ProjectCompendium.APP.updateProjects();
					ProjectCompendium.APP.onFileOpen();
				}
				else {
					ProjectCompendium.APP.displayMessage("There are no MySQL database projects to convert", "MySQL Database Project Convertion");
				}
				onCancel();
			}
		};
		thread.start();
	}

	/**
	 * Convert all the MySQL database projects to Derby in one go.
	 */
	private void convertAll() {

		ProjectCompendium.APP.displayMessage("Depending on the number and size of your MySQL database projects\nthis may take some time.\n\nA separate progress bar will appear for each database being converted.\n\nYou might like to go and have a nice cup of tea!\n", "Database Conversion");

		int count = vtProjects.size();
		String sName = "";
		for (int i=0; i<count; i++) {
			sName = (String)vtProjects.elementAt(i);
			processConversion(sName, true);
		}
	}

	/**
	 * Processes a request to convert the MySQL database with the given name.
	 *
	 * @param sName, the name of the MySQL database to convert.
	 */
	private void processConversion(String sName, boolean ignoreFirst) {

		boolean bNameExists = true;
		boolean firstLoop = true;
		String sNewName = "";

		while(bNameExists) {

			if (ignoreFirst && firstLoop)
				sNewName = sName;
			else {
	   			sNewName = JOptionPane.showInputDialog("Enter the new name for this project", sName);
				sNewName = sNewName.trim();
			}

			bNameExists = false;
			if (!sNewName.equals("")) {

				int count = vtDerbyProjects.size();
				for (int i=0; i<count; i++) {
					String next = (String)vtDerbyProjects.elementAt(i);
					if (next.equals(sNewName)) {
						bNameExists = true;
						break;
					}
				}

				if (!bNameExists) {
					final String sfFriendlyName = sName;
					final String sfToName = sNewName;

					if (isVisible())
						setVisible(false);

					ServiceManager oManager = new ServiceManager(ICoreConstants.MYSQL_DATABASE, oConnection.getLogin(), oConnection.getPassword(), oConnection.getServer());
					DBAdminDatabase oAdminDatabase = new DBAdminDatabase(oManager, oConnection.getLogin(), oConnection.getPassword(), oConnection.getServer());
					String sFromName = oAdminDatabase.getDatabaseName(sfFriendlyName);					
					int status = oAdminDatabase.getSchemaStatusForDatabase(sFromName);

					/*int status = -1;
					if (htProjectCheck.containsKey(sName)) {
						status = ((Integer)htProjectCheck.get(sfFriendlyName)).intValue();
					}*/

					if (status == ICoreConstants.OLDER_DATABASE_SCHEMA) {
						if (!UIDatabaseUpdate.updateDatabase(oAdminDatabase, ProjectCompendium.APP, sFromName)) {
							return;
						}
					}
					else if (status == ICoreConstants.NEWER_DATABASE_SCHEMA) {
						ProjectCompendium.APP.displayMessage("This project cannot be converted as it requires a newer version of Compendium.n\n", "Convert Project");
						return;
					}
					else if (status == -1) {
						ProjectCompendium.APP.displayMessage("This project cannot be converted as its current data structure cannot be determined.n\n", "Convert Project");
						return;
					}


					DBConvertMySQLToDerbyDatabase converter = new DBConvertMySQLToDerbyDatabase(ProjectCompendium.APP.adminDerbyDatabase, oConnection.getLogin(), oConnection.getPassword(), oConnection.getServer());
					try {
						converter.addProgressListener((DBProgressListener)manager);
						oThread = new ProgressThread("Converting Project: "+sfFriendlyName, "Database Conversion Completed");
						oThread.start();
						converter.copyDatabase(sFromName, sfToName, sfFriendlyName);
						converter.removeProgressListener((DBProgressListener)manager);
					}
					catch (SQLException ex) {
						ProjectCompendium.APP.displayError("Error: Database project "+sfFriendlyName+" could not be converted due to:\n\n"+ex.getMessage());
						ex.printStackTrace();
						progressComplete();
						return;
					}
					catch(ClassNotFoundException ex) {
						ProjectCompendium.APP.displayError("Error: Failed to connect to database project "+sfFriendlyName+" due to: \n\n"+ex.getMessage());
						progressComplete();
						return;
					}
					catch(DBDatabaseNameException ex) {
						ProjectCompendium.APP.displayError("Error: Database project "+sfFriendlyName+" could not be converted due to: \n\n"+ex.getMessage());
						progressComplete();
						return;
					}
					catch(DBDatabaseTypeException ex) {
						ProjectCompendium.APP.displayError(ex.getMessage());
						progressComplete();
						return;
					}
				}
				else {
			   		JOptionPane.showMessageDialog(this, "A project named '"+sNewName+"' already exists,\nPlease enter a new name.\n",
										 "Warning",JOptionPane.WARNING_MESSAGE);
					firstLoop = false;
				}
			}
		}
	}

	/**
	 * Draws the progress dialog.
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
		this.nCount = 0;
		oProgressBar.setValue(0);
		oProgressDialog.setStatus(0);
	}

	/**
	 * Indicate that progress has been updated.
	 *
	 * @param int nIncrement, the current position of the progress in relation to the inital count
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
