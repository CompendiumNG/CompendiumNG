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

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.sql.*;
import java.beans.*;

import javax.swing.*;
import javax.swing.border.*;

import com.compendium.*;
import com.compendium.ui.*;

import com.compendium.core.*;
import com.compendium.core.db.management.*;

/**
 * This dialog allows the user to select a current database and copy it to another named database
 *
 * @author	Michelle Bachler
 */
public class UIDatabaseManagementDialog extends UIDialog implements ActionListener, DBProgressListener {

	// trying to get around thread synchronization problem
	/** Indicates that an action does not need to be resumed.*/
	public static int		RESUME_NONE		= 0;

	/** Indicates that a delete action needs to be resumed, after backing up.*/
	public static int		RESUME_DELETE	= 1;

	/** Indicates that a restore action needs to be resumed after backing up.*/
	public static int		RESUME_RESTORE	= 2;

	/** The pane for the dialog's contents to be put in.*/
	private Container		oContentPane = null;

	/* the parent froame for this dialog.*/
	private JFrame 			oParent = null;

	/** The list of current SQL database you can opne.*/
	private UIProjectList		lstProjects	= null;

	/** The scrollpane that the database list is in.*/
	private JScrollPane		oScrollpane	= new JScrollPane();

	/** The button to open the backup dialog.*/
	private JButton			pbBackup	= null;

	/** The button to restore an existing database from a backup file.*/
	private JButton			pbRestore	= null;

	/** The button to restore a backup file as a new database.*/
	private JButton			pbRestoreNew= null;

	/** The button to copy an existing database to a new database instance.*/
	private JButton			pbCopy		= null;

	/** The button to delete the selected database.*/
	private JButton 		pbDelete	= null;

	/** The button to edit the selected database user friendly name.*/
	private JButton			pbEdit		= null;

	/** the button to cancel this dialog.*/
	private UIButton		pbCancel	= null;

	/** The button to open the help for Project Management.*/
	private UIButton		pbHelp		= null;

	/** A pointer to the backup dialog so that it can be closed properly on exit. (bug on the Mac)*/
	private	UIBackupDialog oBackupDialog= null;

	/** The data for the list of existing database projects.*/
	private Vector			vtProjects	= new Vector();

	/** The current instance of the database administration class required by this dialog.*/
	public DBAdminDatabase 	databaseAdmin	= null;

	/** The system database name of the currently selected database project.*/
	private String			sDatabaseName	= ""; //$NON-NLS-1$

	/** The user specified name for the currently selected database project.*/
	private String 			sFriendlyName	= ""; //$NON-NLS-1$

	/** The progress dialog holding the progress.*/
	private UIProgressDialog	oProgressDialog = null;

	/** The progress bar held in the dialog.*/
	private JProgressBar		oProgressBar = null;

	/** the thread that runs the progress bar.*/
	private ProgressThread		oThread = null;

	/** The counter used by the progress bar.*/
	private int					nCount = 0;

	/** A reference to this class for use in inner threads.*/
	private UIDatabaseManagementDialog manager = null;

	/** The mysql login name.*/
	private String mysqlname = ICoreConstants.sDEFAULT_DATABASE_USER;

	/** The mysql password.*/
	private String mysqlpassword = ICoreConstants.sDEFAULT_DATABASE_PASSWORD;

	/** The mysql ip address or host name.*/
	private String mysqlip = ICoreConstants.sDEFAULT_DATABASE_ADDRESS;

	/** Database projects against their requirement to have thier schemas updated.*/
	//private Hashtable		htProjectCheck		= new Hashtable(10);

	/**
	 * Constructor use when backup code called direct from Menu without displaying this dialog.
	 *
	 * @param parent, the parent frame to this dailog.
	 * @param admin com.compendium.core.db.management.DBAdminDatabase, the class required to perform database administration tasks.
	 * @param sMySQLName, the login name to use when connection to MySQL.
	 * @param sMySQLPassword, the password to use when connection to MySQL.
	 * @param sMySQLIP, the ip address or hostname to use to connect to MySQL.
	 */
	public UIDatabaseManagementDialog(JFrame parent, DBAdminDatabase admin, String sMySQLName, String sMySQLPassword, String sMySQLIP) {
		super(parent, false);

		manager = this;
		databaseAdmin = admin;
		oParent = parent;

		mysqlname = sMySQLName;
		mysqlpassword = sMySQLPassword;
		if (sMySQLIP != null && !sMySQLIP.equals("")) //$NON-NLS-1$
			mysqlip = sMySQLIP;

		oProgressBar = new JProgressBar();
		oProgressBar.setMinimum(0);
		oProgressBar.setMaximum(100);
	}

	/**
	 * Constructor for standard Project Managment dialog.
	 *
	 * @param parent the parent frame to this dialog.
	 * @param htProjectCheck used to check the status of the project schema and display info in list.
	 * @param admin the class required to perform database administration tasks.
	 * @param projects the list of existing MySQL database projects.
	 * @param sMySQLName the login name to use when connection to MySQL.
	 * @param sMySQLPassword the password to use when connection to MySQL.
	 * @param sMySQLIP the ip address or hostname to use to connect to MySQL.
	 */
	public UIDatabaseManagementDialog(JFrame parent, DBAdminDatabase admin, Vector projects, String sMySQLName, String sMySQLPassword, String sMySQLIP) {
		//public UIDatabaseManagementDialog(JFrame parent, Hashtable htProjectCheck, DBAdminDatabase admin, Vector projects, String sMySQLName, String sMySQLPassword, String sMySQLIP) {

		super(parent, true);

		manager = this;
		oParent = parent;
		databaseAdmin = admin;
		//this.htProjectCheck = htProjectCheck;

		mysqlname = sMySQLName;
		mysqlpassword = sMySQLPassword;
		if (sMySQLIP != null && !sMySQLIP.equals("")) //$NON-NLS-1$
			mysqlip = sMySQLIP;

		setTitle(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIDatabaseManagementDialog.projectManagement")); //$NON-NLS-1$
		vtProjects = projects;

		oContentPane = getContentPane();
		oContentPane.setLayout(new BorderLayout());

		// create label and text box for model name
		JLabel label = new JLabel(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIDatabaseManagementDialog.currentProjects")+":"); //$NON-NLS-1$
		label.setFont(new Font("Arial", Font.PLAIN, 12)); //$NON-NLS-1$
		JPanel labelpanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		labelpanel.setBorder(new EmptyBorder(5,5,0,5));
		labelpanel.add(label);
		oContentPane.add(labelpanel, BorderLayout.NORTH);

		//lstProjects = new UIProjectList(this.htProjectCheck, vtProjects);
		lstProjects = new UIProjectList(vtProjects);

		oScrollpane = new JScrollPane(lstProjects);
		oScrollpane.setPreferredSize(new Dimension(300,225));

		JPanel listpanel = new JPanel();
		listpanel.setBorder(new EmptyBorder(0,5,5,5));
		listpanel.add(oScrollpane);

		JPanel buttonpanel = new JPanel();
		GridBagLayout gb = new GridBagLayout();
		buttonpanel.setLayout(gb);

		GridBagConstraints gc = new GridBagConstraints();
		gc.fill = GridBagConstraints.BOTH;
		gc.anchor = GridBagConstraints.WEST;
		gc.gridheight=1;

		buttonpanel.setBorder(new EmptyBorder(0,5,5,5));

		int y=0;

		pbEdit = new UIButton(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIDatabaseManagementDialog.editNameButton")); //$NON-NLS-1$
		pbEdit.setToolTipText(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIDatabaseManagementDialog.editNameButtonTip")); //$NON-NLS-1$
		pbEdit.setMnemonic(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIDatabaseManagementDialog.editNameButtonMnemonic").charAt(0));
		pbEdit.addActionListener(this);
		gc.gridy = y;
		gc.insets = new Insets(0,5,5,5);
		y++;
		gb.setConstraints(pbEdit, gc);
		buttonpanel.add(pbEdit);

		gc.insets = new Insets(5,5,5,5);

		pbCopy = new UIButton(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIDatabaseManagementDialog.copyButton")); //$NON-NLS-1$
		pbCopy.setToolTipText(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIDatabaseManagementDialog.copyButtonTip")); //$NON-NLS-1$
		pbCopy.setMnemonic(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIDatabaseManagementDialog.copyButtonMnemonic").charAt(0));
		pbCopy.addActionListener(this);
		gc.gridy = y;
		y++;
		gb.setConstraints(pbCopy, gc);
		buttonpanel.add(pbCopy);

		JSeparator sep = new JSeparator();
		gc.gridy = y;
		y++;
		gb.setConstraints(sep, gc);
		buttonpanel.add(sep);

		pbBackup = new UIButton(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIDatabaseManagementDialog.backupButton")); //$NON-NLS-1$
		pbBackup.setToolTipText(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIDatabaseManagementDialog.backupButtonTip")); //$NON-NLS-1$
		pbBackup.setMnemonic(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIDatabaseManagementDialog.backupButtonMnemonic").charAt(0));
		pbBackup.addActionListener(this);
		gc.gridy = y;
		y++;
		gb.setConstraints(pbBackup, gc);
		buttonpanel.add(pbBackup);

		pbRestore = new UIButton(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIDatabaseManagementDialog.restoreToButton")); //$NON-NLS-1$
		pbRestore.setToolTipText(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIDatabaseManagementDialog.restoreToButtonTip")); //$NON-NLS-1$
		pbRestore.setMnemonic(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIDatabaseManagementDialog.restoreToButtonMnemonic").charAt(0));
		pbRestore.addActionListener(this);
		gc.gridy = y;
		y++;
		gb.setConstraints(pbRestore, gc);
		buttonpanel.add(pbRestore);

		pbRestoreNew = new UIButton(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIDatabaseManagementDialog.restoreAsNewButton")); //$NON-NLS-1$
		pbRestoreNew.setToolTipText(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIDatabaseManagementDialog.restoreAsNewButtonTip")); //$NON-NLS-1$
		pbRestoreNew.setMnemonic(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIDatabaseManagementDialog.restoreAsNewButtonMnemonic").charAt(0));
		pbRestoreNew.addActionListener(this);
		gc.gridy = y;
		y++;
		gb.setConstraints(pbRestoreNew, gc);
		buttonpanel.add(pbRestoreNew);

		sep = new JSeparator();
		gc.gridy = y;
		y++;
		gb.setConstraints(sep, gc);
		buttonpanel.add(sep);

		pbDelete = new UIButton(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIDatabaseManagementDialog.deleteButton")); //$NON-NLS-1$
		//pbDelete.setEnabled(false);

		pbDelete.setToolTipText(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIDatabaseManagementDialog.deleteButtonTip")); //$NON-NLS-1$
		pbDelete.setMnemonic(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIDatabaseManagementDialog.deleteButtonMnemonic").charAt(0));
		pbDelete.addActionListener(this);
		gc.gridy = y;
		y++;
		gb.setConstraints(pbDelete, gc);
		buttonpanel.add(pbDelete);

		UIButtonPanel oButtonPanel = new UIButtonPanel();

		pbCancel = new UIButton(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIDatabaseManagementDialog.closeButton")); //$NON-NLS-1$
		pbCancel.setMnemonic(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIDatabaseManagementDialog.closeButtonMnemonic").charAt(0));
		pbCancel.addActionListener(this);
		getRootPane().setDefaultButton(pbCancel);
		oButtonPanel.addButton(pbCancel);

		pbHelp = new UIButton(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIDatabaseManagementDialog.helpButton")); //$NON-NLS-1$
		pbHelp.setMnemonic(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIDatabaseManagementDialog.helpButtonMnemonic").charAt(0));
		ProjectCompendium.APP.mainHB.enableHelpOnButton(pbHelp, "basics.databases", ProjectCompendium.APP.mainHS); //$NON-NLS-1$
		oButtonPanel.addHelpButton(pbHelp);

		oContentPane.add(buttonpanel, BorderLayout.EAST);
		oContentPane.add(oButtonPanel, BorderLayout.SOUTH);
		oContentPane.add(listpanel, BorderLayout.CENTER);

		updateProjectList();

		oProgressBar = new JProgressBar();
		oProgressBar.setMinimum(0);
		oProgressBar.setMaximum(100);

		setResizable(false);
		pack();
	}

	/**
	 * Handle button push events.
	 * @param evt, the associated ActionEvent object.
	 */
	public void actionPerformed(ActionEvent evt) {

		final Object source = evt.getSource();

		if ((source instanceof JButton)) {

			if (source.equals(pbCancel)) {
				onCancel();
			}
			else {
				if (source.equals(pbRestoreNew)) {
					onRestoreNew();
				}
				else if (source.equals(pbCopy)) {
					int index = lstProjects.getSelectedIndex();
					if (index < 0) {
						ProjectCompendium.APP.displayError(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIDatabaseManagementDialog.selectProject")); //$NON-NLS-1$
						return;
					}
					
					try {
						final String sFriendlyName = (String)vtProjects.elementAt(index);
						final String sDatabaseName = databaseAdmin.getDatabaseName(sFriendlyName);
	
						// THREAD NEEDED DUE TO PROGRESS BAR DRAWN IN THE METHOD
						Thread thread = new Thread("DatabaseManagermentDialog.onCopy") { //$NON-NLS-1$
							public void run() {
								onCopy(sFriendlyName, sDatabaseName);
							}
						};
						thread.start();
					} catch (DBProjectListException ex) {
						ProjectCompendium.APP.displayError(ex.getMessage());
					}
				}
				else {
					int index = lstProjects.getSelectedIndex();
					if (index < 0) {
						ProjectCompendium.APP.displayError(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIDatabaseManagementDialog.selectProject")); //$NON-NLS-1$
						return;
					}

					try {
						final String sFriendlyName = (String)vtProjects.elementAt(index);
						final String sDatabaseName = databaseAdmin.getDatabaseName(sFriendlyName);
	
						// MAKE THEM LOG IN AND CHECK THEY ARE AN ADMINISTRATOR ON THAT DATABASE
	        			final JTextField usernameField = new JTextField(15);
				        final JPasswordField passwordField = new JPasswordField(15);
	
		     			Object[] fields = {LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIDatabaseManagementDialog.message1a")+sFriendlyName+"\n"+LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIDatabaseManagementDialog.message1b")+"\n", //$NON-NLS-1$ //$NON-NLS-2$
	                            " ", //$NON-NLS-1$
	                            LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIDatabaseManagementDialog.username")+": ", usernameField, //$NON-NLS-1$
	                            LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIDatabaseManagementDialog.password")+": ", passwordField}; //$NON-NLS-1$
	
	         			final String okButton = LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIDatabaseManagementDialog.okButton"); //$NON-NLS-1$
	         			final String cancelButton = LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIDatabaseManagementDialog.cancelButton"); //$NON-NLS-1$
	         			Object[] options = {okButton, cancelButton};
	
	         			final JOptionPane optionPane = new JOptionPane(fields,
	                                      JOptionPane.PLAIN_MESSAGE,
	                                      JOptionPane.OK_CANCEL_OPTION,
	                                      null,
	                                      options,
	                                      options[0]);
	
						final JDialog dlg = new JDialog(this, true);
	
				        optionPane.addPropertyChangeListener(new PropertyChangeListener() {
				        	public void propertyChange(PropertyChangeEvent e) {
				            	String prop = e.getPropertyName();
	
				                if (isVisible() && (e.getSource() == optionPane)
				                    && (prop.equals(JOptionPane.VALUE_PROPERTY) ||
				                       prop.equals(JOptionPane.INPUT_VALUE_PROPERTY))) {
				                    Object value = optionPane.getValue();
	
				                    if (value == JOptionPane.UNINITIALIZED_VALUE) {
				                        return;
				                    }
				                    optionPane.setValue(JOptionPane.UNINITIALIZED_VALUE);
	
				                    if (value.equals(okButton)) {
				                        String login = usernameField.getText();
				                        String password = new String(passwordField.getPassword());
	
										dlg.setVisible(false);
										dlg.dispose();
	
										if (databaseAdmin != null
											&& databaseAdmin.isAdministrator(sDatabaseName, login, password)) {
	
											if (source.equals(pbEdit)) {
												onEdit(sFriendlyName, sDatabaseName);
											}
											else if (source.equals(pbBackup)) {
												onBackup(sFriendlyName, sDatabaseName, RESUME_NONE, false);
											}
											//else if (source.equals(pbBackupZip)) {
											//	onBackupZip(sFriendlyName, sDatabaseName, RESUME_NONE);
											//}
											else if (source.equals(pbRestore)) {
												onRestore(sFriendlyName, sDatabaseName);
											}
											else if (source.equals(pbDelete)) {
												onDelete(sFriendlyName, sDatabaseName);
											}
										}
										else {
											ProjectCompendium.APP.displayError(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIDatabaseManagementDialog.errorLoginA")+"\n"+
													LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIDatabaseManagementDialog.errorLoginB")+"\n"); //$NON-NLS-1$
										}
				                    }
									dlg.setVisible(false);
									dlg.dispose();
				            	}
				        	}
				        });
	
						dlg.getContentPane().add(optionPane);
						dlg.pack();
						dlg.setSize(dlg.getPreferredSize());
						UIUtilities.centerComponent(dlg, this);
						dlg.setVisible(true);
					} catch (DBProjectListException ex) {
						ProjectCompendium.APP.displayError(ex.getMessage());
					}
				}
			}
		}
	}

	/**
	 * Edit the name of the selected database.
	 * @param sFriendlyName, the user assigned name for the selected database project to edit the name of.
	 * @param sDatabaseName, the system assigned name for the selected database project to edit the name of.
	 */
	private void onEdit(String sFriendlyName, String sDatabaseName) {

		boolean bNameExists = true;

		while(bNameExists) {
	   		String sNewName = JOptionPane.showInputDialog(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIDatabaseManagementDialog.newName"), sFriendlyName); //$NON-NLS-1$
			sNewName = sNewName.trim();

			bNameExists = false;
			if (!sNewName.equals("") && !sNewName.equals(sFriendlyName)) { //$NON-NLS-1$

				int count = vtProjects.size();
				for (int i=0; i<count; i++) {
					String next = (String)vtProjects.elementAt(i);
					if (next.equals(sNewName)) {
						bNameExists = true;
						break;
					}
				}

				if (!bNameExists) {
					try {
						databaseAdmin.editFriendlyName(sFriendlyName, sNewName);
	
						// UPDATE THE DEFAULT DATABASE
						String sOldDefault = ProjectCompendium.APP.getDefaultDatabase();
						if (sOldDefault.equals(sFriendlyName)) {
							ProjectCompendium.APP.setDefaultDatabase(sNewName);
						}
	
						// UPDATE MAIN FRAME DATA AND LOCAL LIST
						ProjectCompendium.APP.updateProjects();					
						vtProjects.removeAllElements();					
						vtProjects = ProjectCompendium.APP.getProjects();					 
						updateProjectList();
					} catch (DBProjectListException ex) {
						ProjectCompendium.APP.displayError(ex.getMessage());
					}

				}
				else {
			   		JOptionPane.showMessageDialog(this, LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIDatabaseManagementDialog.errorProjectExistsA")+sNewName+LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIDatabaseManagementDialog.errorProjectExistsB"), //$NON-NLS-1$ //$NON-NLS-2$
										 LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIDatabaseManagementDialog.warning"),JOptionPane.WARNING_MESSAGE); //$NON-NLS-1$
				}
			}
		}
	}

	/**
	 * Copy the database with the given database name to another database Name.
	 * @param sFriendlyName, the user assigned name for the selected database project to copy.
	 * @param sDatabaseName, the system assigned name for the selected database project to copy.
	 */
	private void onCopy(String sFriendlyName, String sDatabaseName) {

		DBConnection dbcon = ProjectCompendium.APP.getServiceManager().getDatabaseManager().requestConnection(sDatabaseName);

		int status = ProjectCompendium.APP.adminDatabase.getSchemaStatusForDatabase(sDatabaseName);
		if (status == ICoreConstants.OLDER_DATABASE_SCHEMA) {
			if (!DatabaseUpdate.updateDatabase(ProjectCompendium.APP.adminDatabase, dbcon, ProjectCompendium.APP, sDatabaseName)) {
				ProjectCompendium.APP.getServiceManager().getDatabaseManager().releaseConnection(sDatabaseName, dbcon);
				return;
			}
		}
		else if (status == ICoreConstants.NEWER_DATABASE_SCHEMA) {
			ProjectCompendium.APP.displayMessage(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIDatabaseManagementDialog.errorNeedsNewerVersion"), LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIDatabaseManagementDialog.copyProject")); //$NON-NLS-1$ //$NON-NLS-2$
			return;
		}

		ProjectCompendium.APP.getServiceManager().getDatabaseManager().releaseConnection(sDatabaseName, dbcon);

		boolean bNameExists = true;
		while(bNameExists) {
	   		String sNewName = JOptionPane.showInputDialog(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIDatabaseManagementDialog.enterProjectName")); //$NON-NLS-1$
			sNewName = sNewName.trim();

			bNameExists = false;

			if (sNewName.equals("")) { //$NON-NLS-1$
			   	JOptionPane.showMessageDialog(this, LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIDatabaseManagementDialog.errorNoNameNoCopy")+"\n"); //$NON-NLS-1$
			}
			else if (sNewName.equals(sFriendlyName)) {
				JOptionPane.showMessageDialog(this, LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIDatabaseManagementDialog.errorSameName")+"\n"); //$NON-NLS-1$
				bNameExists = true;
			}
			else {

				int count = vtProjects.size();
				for (int i=0; i<count; i++) {
					String next = (String)vtProjects.elementAt(i);
					if (next.equals(sNewName)) {
						bNameExists = true;
						break;
					}
				}

				if (!bNameExists) {

					final String fsDatabaseName = sDatabaseName;
					final String fsNewName = sNewName;
					final String fsFriendlyName = sFriendlyName;

					Thread thread = new Thread("UIDatabaseManagementDialog.actionPerformed-Copy") { //$NON-NLS-1$
						public void run() {
							DBCopyDatabase copy = new DBCopyDatabase(FormatProperties.nDatabaseType, ProjectCompendium.APP.adminDatabase, mysqlname, mysqlpassword, mysqlip);
							try {
								copy.addProgressListener((DBProgressListener)manager);
								oThread = new ProgressThread(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIDatabaseManagementDialog.copyingProject"), LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIDatabaseManagementDialog.copyComplete")); //$NON-NLS-1$ //$NON-NLS-2$
								oThread.start();
								copy.copyDatabase(fsDatabaseName, fsNewName, fsFriendlyName);
								copy.removeProgressListener((DBProgressListener)manager);
							}
							catch(DBDatabaseNameException ex) { // WOULD NEVER HAPPEN, BUT MUST STILL BE HANDLED
								progressComplete();
								ProjectCompendium.APP.displayMessage(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIDatabaseManagementDialog.errorNameClashA")+"\n\n"+
										LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIDatabaseManagementDialog.errorNameClash"), LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIDatabaseManagementDialog.newProjectB")+"\n"); //$NON-NLS-1$ //$NON-NLS-2$
								return;
							}
							catch(DBDatabaseTypeException ex) {
								progressComplete();
								ProjectCompendium.APP.displayError(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIDatabaseManagementDialog.errorConnecitonFailure")+":\n\n"+ex.getMessage()); //$NON-NLS-1$
								return;
							}
							catch(DBProjectListException ex) {
								progressComplete();
								ProjectCompendium.APP.displayError(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIDatabaseManagementDialog.errorRetrievalFailure")+":\n\n"+ex.getMessage()); //$NON-NLS-1$
								return;
							}
							catch(ClassNotFoundException ex) {
								progressComplete();
								ProjectCompendium.APP.displayError(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIDatabaseManagementDialog.errorConnectionFailure2")+":\n\n"+ex.getMessage()); //$NON-NLS-1$
								return;
							}
							catch(SQLException ex) {
								ex.printStackTrace();
								progressComplete();
								ProjectCompendium.APP.displayError(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIDatabaseManagementDialog.errorCopyFailure")+":\n\n"+ex.getMessage()); //$NON-NLS-1$
								return;
							}

							// UPDATE MAIN FRAME DATA AND LOCAL LIST
							ProjectCompendium.APP.updateProjects();
							vtProjects.removeAllElements();
							vtProjects = ProjectCompendium.APP.getProjects();
							updateProjectList();
						}
					};
					thread.start();
				}
				else {
			   		JOptionPane.showMessageDialog(this, LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIDatabaseManagementDialog.errorProjectExistsA")+
			   				" '"+sNewName+"' "+
			   				LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIDatabaseManagementDialog.errorProjectExistsB")+"\n"+ //$NON-NLS-1$ //$NON-NLS-2$
			   				LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIDatabaseManagementDialog.errorProjectExistsC"), //$NON-NLS-1$ //$NON-NLS-2$
										 LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIDatabaseManagementDialog.warning"),JOptionPane.WARNING_MESSAGE); //$NON-NLS-1$
				}
			}
		}
	}

	/**
	 * Backup the database with the given database name
	 *
	 * @param String sFriendlyName, the name the user chooses
	 * @param String sDatabaseName, the actual name of the database (after the users chosen name has been 'cleaned' and dated)
	 * @param int resumeAction (if backup was called from 'Delete' or 'Restore To', action to resume after backup (thread syn issue).
	 * @param boolean bCloseAfter, should this dialog be closed when the action is complete.
     */
	public void onBackup(String sFriendlyName, String sDatabaseName, int nResumeAction, boolean bCloseAfter) {
		oBackupDialog = new UIBackupDialog(ProjectCompendium.APP, this, sFriendlyName, sDatabaseName, nResumeAction, bCloseAfter);
		UIUtilities.centerComponent(oBackupDialog, ProjectCompendium.APP);
		oBackupDialog.setVisible(true);
	}

	/**
	 * Backup the database with the given database name
	 *
	 * @param String sFriendlyName, the name the user chooses
	 * @param String sDatabaseName, the actual name of the database (after the users chosen name has been 'cleaned' and dated)
	 * @param int resumeAction (if backup was called from 'Delete' or 'Restore To', action to resume after backup (thread syn issue).
	 * @param boolean bCloseAfter, should this dialog be closed when the action is complete.
	 */
	public void onBackupPlain(String sFriendlyName, String sDatabaseName, int nResumeAction, boolean bCloseAfter) {

		DBConnection dbcon = ProjectCompendium.APP.getServiceManager().getDatabaseManager().requestConnection(sDatabaseName);

		int status = ProjectCompendium.APP.adminDatabase.getSchemaStatusForDatabase(sDatabaseName);
		if (status == ICoreConstants.OLDER_DATABASE_SCHEMA) {
			if (!DatabaseUpdate.updateDatabase(ProjectCompendium.APP.adminDatabase, dbcon, ProjectCompendium.APP, sDatabaseName)) {
				ProjectCompendium.APP.getServiceManager().getDatabaseManager().releaseConnection(sDatabaseName, dbcon);
				return;
			}
		}
		else if (status == ICoreConstants.NEWER_DATABASE_SCHEMA) {
			ProjectCompendium.APP.displayMessage(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIDatabaseManagementDialog.errorNewerVersion")+"\n", LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIDatabaseManagementDialog.backupProject")); //$NON-NLS-1$ //$NON-NLS-2$
			return;
		}

		ProjectCompendium.APP.getServiceManager().getDatabaseManager().releaseConnection(sDatabaseName, dbcon);

		UIFileFilter filter = new UIFileFilter(new String[] {"sql"}, "SQL Files"); //$NON-NLS-1$ //$NON-NLS-2$

		UIFileChooser backupFileDialog = new UIFileChooser();
		backupFileDialog.setDialogTitle(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIDatabaseManagementDialog.backupFileName")); //$NON-NLS-1$
		backupFileDialog.setFileFilter(filter);
		backupFileDialog.setApproveButtonText(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIDatabaseManagementDialog.backupButton2")); //$NON-NLS-1$
		backupFileDialog.setRequiredExtension(".sql"); //$NON-NLS-1$

		// FIX FOR MAC - NEEDS '/' ON END TO DENOTE A FOLDER
		// AND MUST USE ABSOUTE PATH, AS RELATIVE PATH REMOVES THE '/'
		File filepath = new File(""); //$NON-NLS-1$
		String sPath = filepath.getAbsolutePath();
		File file = new File(sPath+ProjectCompendium.sFS+"Backups"+ProjectCompendium.sFS); //$NON-NLS-1$
		if (file.exists()) {
			backupFileDialog.setCurrentDirectory(file);
		}

		String fileName = ""; //$NON-NLS-1$
		int retval = backupFileDialog.showSaveDialog(ProjectCompendium.APP);

		if (retval == JFileChooser.APPROVE_OPTION) {
        	if ((backupFileDialog.getSelectedFile()) != null) {

            	fileName = backupFileDialog.getSelectedFile().getAbsolutePath();

				if (fileName != null) {
					if ( !fileName.toLowerCase().endsWith(".sql") ) { //$NON-NLS-1$
						fileName = fileName+".sql"; //$NON-NLS-1$
					}

					final String fsFileName = fileName;
					final String fsFriendlyName = sFriendlyName;
					final String fsDatabaseName = sDatabaseName;
					final int fnResumeAction = nResumeAction;
					final boolean fbCloseAfter = bCloseAfter;
					Thread thread = new Thread("UIDatabaseManagementDialog.actionPerformed-Backup") { //$NON-NLS-1$
						public void run() {
							//System.out.println("About to backup");
							DBBackupDatabase backup = new DBBackupDatabase(FormatProperties.nDatabaseType, mysqlname, mysqlpassword, mysqlip);
							try {
								backup.addProgressListener((DBProgressListener)manager);
								oThread = new ProgressThread(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIDatabaseManagementDialog.progressBackingUp"), LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIDatabaseManagementDialog.backupComplete")); //$NON-NLS-1$ //$NON-NLS-2$
								oThread.start();
								backup.backupDatabase(fsDatabaseName, new File(fsFileName), false);
								backup.removeProgressListener((DBProgressListener)manager);
							}
							catch(IOException ex) {
								progressComplete();
								ProjectCompendium.APP.displayError(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIDatabaseManagementDialog.errorWritingFile")+ex.getMessage()); //$NON-NLS-1$
								if (fbCloseAfter) {
									onCancel();
								}
								return;
							}
							catch(DBDatabaseTypeException ex) {
								progressComplete();
								ProjectCompendium.APP.displayError(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIDatabaseManagementDialog.errorConnectToProject")+ex.getMessage()); //$NON-NLS-1$
								if (fbCloseAfter) {
									onCancel();
								}
								return;
							}
							catch(ClassNotFoundException ex) {
								progressComplete();
								ProjectCompendium.APP.displayError(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIDatabaseManagementDialog.errorConnectToProject")+":\n\n"+ex.getMessage()); //$NON-NLS-1$
								if (fbCloseAfter) {
									onCancel();
								}
								return;
							}
							catch(SQLException ex) {
								progressComplete();
								//ProjectCompendium.APP.displayError("Your database was unable to be backedup.\nPlease contact Compendium support staff.\n");
								ex.printStackTrace();
								ProjectCompendium.APP.displayError(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIDatabaseManagementDialog.errorReadingData")+":\n\n"+ex.getMessage()); //$NON-NLS-1$
								if (fbCloseAfter) {
									onCancel();
								}
								return;
							}

							// if backup was called from delete or restore, continue the process.
							if (fnResumeAction == RESUME_DELETE) {
								doDelete(fsFriendlyName, fsDatabaseName, true);
							}
							else if (fnResumeAction == RESUME_RESTORE) {
								doRestore(fsFriendlyName, fsDatabaseName, true);
							}

							if (fbCloseAfter) {
								onCancel();
							}
						}
					};
					thread.start();
				}
			}
		}
	}

	/**
	 * Backup the database with the given database name and all associated resources.
	 *
	 * @param sFriendlyName, the user assigned name for the selected database project to backup.
	 * @param sDatabaseName, the system assigned name for the selected database project to backup.
	 * @param int resumeAction (if backup was called from 'Delete' or 'Restore To', action to resume after backup (thread syn issue).
	 * @param boolean bCloseAfter, should this dialog be closed when the action is complete.
	 */
	public void onBackupZip(String sFriendlyName, String sDatabaseName, int nResumeAction, boolean bKeepPaths, boolean bCloseAfter, boolean bIncludeMovies, boolean bIncludeTemplates, boolean bIncludeTrash) {

		DBConnection dbcon = ProjectCompendium.APP.getServiceManager().getDatabaseManager().requestConnection(sDatabaseName);

		int status = ProjectCompendium.APP.adminDatabase.getSchemaStatusForDatabase(sDatabaseName);
		if (status == ICoreConstants.OLDER_DATABASE_SCHEMA) {
			if (!DatabaseUpdate.updateDatabase(ProjectCompendium.APP.adminDatabase, dbcon, ProjectCompendium.APP, sDatabaseName)) {
				ProjectCompendium.APP.getServiceManager().getDatabaseManager().releaseConnection(sDatabaseName, dbcon);
				return;
			}
		}
		else if (status == ICoreConstants.NEWER_DATABASE_SCHEMA) {
			ProjectCompendium.APP.displayMessage(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIDatabaseManagementDialog.errorNewerVersion"), LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIDatabaseManagementDialog.backupProject")); //$NON-NLS-1$ //$NON-NLS-2$
			return;
		}

		ProjectCompendium.APP.getServiceManager().getDatabaseManager().releaseConnection(sDatabaseName, dbcon);

		UIFileFilter filter = new UIFileFilter(new String[] {"zip"}, "Zip Files"); //$NON-NLS-1$ //$NON-NLS-2$

		UIFileChooser backupFileDialog = new UIFileChooser();
		backupFileDialog.setDialogTitle(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIDatabaseManagementDialog.enterFileName")); //$NON-NLS-1$
		backupFileDialog.setFileFilter(filter);
		backupFileDialog.setApproveButtonText(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIDatabaseManagementDialog.backupButton2")); //$NON-NLS-1$
		backupFileDialog.setRequiredExtension(".zip"); //$NON-NLS-1$

		// FIX FOR MAC - NEEDS '/' ON END TO DENOTE A FOLDER
		// AND MUST USE ABSOUTE PATH, AS RELATIVE PATH REMOVES THE '/'
		File filepath = new File(""); //$NON-NLS-1$
		String sPath = filepath.getAbsolutePath();
		File file = new File(sPath+ProjectCompendium.sFS+"Backups"+ProjectCompendium.sFS); //$NON-NLS-1$
		if (file.exists()) {
			backupFileDialog.setCurrentDirectory(file);
		}

		String fileName = ""; //$NON-NLS-1$
		int retval = backupFileDialog.showSaveDialog(ProjectCompendium.APP);

		if (retval == JFileChooser.APPROVE_OPTION) {
        	if ((backupFileDialog.getSelectedFile()) != null) {

            	fileName = backupFileDialog.getSelectedFile().getAbsolutePath();

				if (fileName != null) {
					if ( !fileName.toLowerCase().endsWith(".zip") ) { //$NON-NLS-1$
						fileName = fileName+".zip"; //$NON-NLS-1$
					}

					final String fsFileName = fileName;
					final String fsFriendlyName = sFriendlyName;
					final String fsDatabaseName = sDatabaseName;
					final int fnResumeAction = nResumeAction;
					final boolean fbKeepPaths = bKeepPaths;
					final boolean fbCloseAfter = bCloseAfter;
					final boolean fbIncludeMovies = bIncludeMovies;
					final boolean fbIncludeTemplates = bIncludeTemplates;
					final boolean fbIncludeTrash = bIncludeTrash;
					
					Thread thread = new Thread("UIDatabaseManagementDialog.actionPerformed-Backup") { //$NON-NLS-1$
						public void run() {
							DBBackupDatabase backup = new DBBackupDatabase(FormatProperties.nDatabaseType, mysqlname, mysqlpassword, mysqlip);
							try {
								backup.addProgressListener((DBProgressListener)manager);
								oThread = new ProgressThread(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIDatabaseManagementDialog.backingUpProject"), LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIDatabaseManagementDialog.backupComplete")); //$NON-NLS-1$ //$NON-NLS-2$
								oThread.start();
								backup.backupDatabaseToZip(fsDatabaseName, fsFriendlyName, new File(fsFileName), false, fbKeepPaths, fbIncludeMovies, fbIncludeTemplates, fbIncludeTrash, ProjectCompendium.APP.getModel().getUserProfile());
								backup.removeProgressListener((DBProgressListener)manager);
								boolean bNotFound = backup.getNotFound();
								if (bNotFound) {
									String sMessage = LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIDatabaseManagementDialog.filesNotFoundA")+"\n\n"+//$NON-NLS-1$
									LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIDatabaseManagementDialog.filesNotFoundB")+"\n\n"; //$NON-NLS-1$
									ProjectCompendium.APP.displayMessage(sMessage, LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIDatabaseManagementDialog.backupFinished")); //$NON-NLS-1$
								}
							}
							catch(IOException ex) {
								progressComplete();
								ProjectCompendium.APP.displayError(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIDatabaseManagementDialog.errorWritingFile")+":\n\n"+ex.getMessage()); //$NON-NLS-1$
								if (fbCloseAfter) {
									onCancel();
								}
								return;
							}
							catch(DBDatabaseTypeException ex) {
								progressComplete();
								ProjectCompendium.APP.displayError(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIDatabaseManagementDialog.errorConnectToProject")+":\n\n"+ex.getMessage()); //$NON-NLS-1$
								if (fbCloseAfter) {
									onCancel();
								}
								return;
							}
							catch(ClassNotFoundException ex) {
								progressComplete();
								ProjectCompendium.APP.displayError(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIDatabaseManagementDialog.errorConnectToProject")+":\n\n"+ex.getMessage()); //$NON-NLS-1$
								return;
							}
							catch(SQLException ex) {
								//ProjectCompendium.APP.displayError("Your project was unable to be backedup.\nPlease contact Compendium support staff.\n");
								progressComplete();
								ProjectCompendium.APP.displayError(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIDatabaseManagementDialog.errorReadingData2")+":\n\n"+ex.getMessage()); //$NON-NLS-1$
								if (fbCloseAfter) {
									onCancel();
								}
								return;
							}

							// if backup was called from delete or restore, continue the process.
							if (fnResumeAction == RESUME_DELETE) {
								doDelete(fsFriendlyName, fsDatabaseName, true);
							}
							else if (fnResumeAction == RESUME_RESTORE) {
								doRestore(fsFriendlyName, fsDatabaseName, true);
							}

							if (fbCloseAfter) {
								onCancel();
							}
						}
					};
					thread.start();
				}
			}
		}
	}

	/**
	 * Check for backup before Restoring a database from a file.
	 *
	 * @param sFriendlyName, the user assigned name for the selected database project to restore over.
	 * @param sDatabaseName, the system assigned name for the selected database project to restore over.
	 */
	private void onRestore(String sFriendlyName, String sDatabaseName) {

   		int answer = JOptionPane.showConfirmDialog(this, LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIDatabaseManagementDialog.messageDeleteA")+" '"+sFriendlyName+"' "+LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIDatabaseManagementDialog.messageDeleteB")+"\n", LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIDatabaseManagementDialog.warning"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);

		if (answer == JOptionPane.YES_OPTION) {
			onBackup(sFriendlyName, sDatabaseName, RESUME_RESTORE, false);
		}
		else if (answer == JOptionPane.NO_OPTION) {
			doRestore(sFriendlyName, sDatabaseName, false);
		}
	}

	/**
	 * Restore a database from a file
	 *
	 * @param sFriendlyName, the user assigned name for the selected database project to restore over.
	 * @param sDatabaseName, the system assigned name for the selected database project to restore over.
	 * @param bHasBackedUp, has the user backed up this database before doing the restore?
	 */
	private void doRestore(String sFriendlyName, String sDatabaseName, boolean bHasBackedUp) {

		boolean bSure = false;
		int answer = -1;

		UIFileFilter filter = new UIFileFilter(new String[] {"sql"}, "SQL Files"); //$NON-NLS-1$ //$NON-NLS-2$

		UIFileChooser fileDialog = new UIFileChooser();
		fileDialog.setDialogTitle(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIDatabaseManagementDialog.enterFileNameRestore")); //$NON-NLS-1$
		fileDialog.setFileFilter(filter);
		fileDialog.setApproveButtonText(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIDatabaseManagementDialog.restoreButton")); //$NON-NLS-1$
		fileDialog.setRequiredExtension(".sql"); //$NON-NLS-1$

		// FIX FOR MAC - NEEDS '/' ON END TO DENOTE A FOLDER
		// AND MUST USE ABSOUTE PATH, AS RELATIVE PATH REMOVES THE '/'
		File filepath = new File(""); //$NON-NLS-1$
		String sPath = filepath.getAbsolutePath();
		File file = new File(sPath+ProjectCompendium.sFS+"Backups"+ProjectCompendium.sFS); //$NON-NLS-1$
		if (file.exists()) {
			fileDialog.setCurrentDirectory(file);
		}

		String fileName = ""; //$NON-NLS-1$
		UIUtilities.centerComponent(fileDialog, this);
		int retval = fileDialog.showOpenDialog(this);

		if (retval == JFileChooser.APPROVE_OPTION) {
        	if ((fileDialog.getSelectedFile()) != null) {

 	           	fileName = fileDialog.getSelectedFile().getAbsolutePath();
				if (fileName != null) {
					File choosenFile = new File(fileName);
					if (choosenFile.exists()) {

						if (!bHasBackedUp) {
					   		answer = JOptionPane.showConfirmDialog(this, LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIDatabaseManagementDialog.warningDataLostB")+"\n"+
					   				LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIDatabaseManagementDialog.warningDataLostB")+"\n", LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIDatabaseManagementDialog.warning"), //$NON-NLS-1$ //$NON-NLS-2$
							JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);

					    	if (answer == JOptionPane.OK_OPTION) {
								bSure = true;
							}
						}

						if (bHasBackedUp || bSure) {

							final String fsFileName = fileName;
							final String fsDatabaseName = sDatabaseName;
							Thread thread = new Thread("UIDatabaseManagementDialog.actionPerformed-Restore") { //$NON-NLS-1$
								public void run() {
									DBRestoreDatabase restore = new DBRestoreDatabase(FormatProperties.nDatabaseType, ProjectCompendium.APP.adminDatabase, mysqlname, mysqlpassword, mysqlip);

									restore.addProgressListener((DBProgressListener)manager);
									oThread = new ProgressThread(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIDatabaseManagementDialog.restoringProject"), LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIDatabaseManagementDialog.restorationComplete")); //$NON-NLS-1$ //$NON-NLS-2$
									oThread.start();
									restore.restoreDatabase(fsDatabaseName, new File(fsFileName), true);
									restore.removeProgressListener((DBProgressListener)manager);

									// UPDATE MAIN FRAME DATA AND LOCAL LIST
									ProjectCompendium.APP.updateProjects();
									vtProjects.removeAllElements();									
									vtProjects = ProjectCompendium.APP.getProjects();
									updateProjectList();
								}
							};
							thread.start();
						}
					}
				}
			}
		}
	}

	/**
	 * Restore a database from a file to a newly created database.
	 */
	private void onRestoreNew() {

		boolean bNameExists = true;

		UIFileFilter filter = new UIFileFilter(new String[] {"sql"}, "SQL Files"); //$NON-NLS-1$ //$NON-NLS-2$

		UIFileChooser fileDialog = new UIFileChooser();
		fileDialog.setDialogTitle(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIDatabaseManagementDialog.enterFileNameRestore")); //$NON-NLS-1$
		fileDialog.setFileFilter(filter);
		fileDialog.setApproveButtonText(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIDatabaseManagementDialog.restoreButton")); //$NON-NLS-1$
		fileDialog.setRequiredExtension(".sql"); //$NON-NLS-1$

		// FIX FOR MAC - NEEDS '/' ON END TO DENOTE A FOLDER
		// AND MUST USE ABSOUTE PATH, AS RELATIVE PATH REMOVES THE '/'
		File filepath = new File(""); //$NON-NLS-1$
		String sPath = filepath.getAbsolutePath();
		File file = new File(sPath+ProjectCompendium.sFS+"Backups"+ProjectCompendium.sFS); //$NON-NLS-1$
		if (file.exists()) {
			fileDialog.setCurrentDirectory(file);
		}

		String fileName = ""; //$NON-NLS-1$
		UIUtilities.centerComponent(fileDialog, this);
		int retval = fileDialog.showOpenDialog(this);

		if (retval == JFileChooser.APPROVE_OPTION) {
	       	if ((fileDialog.getSelectedFile()) != null) {

	           	fileName = fileDialog.getSelectedFile().getAbsolutePath();
				if (fileName != null) {
					File choosenFile = new File(fileName);

					if (choosenFile.exists()) {
						while(bNameExists) {

					   		String sNewName = JOptionPane.showInputDialog(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIDatabaseManagementDialog.enterNewName")); //$NON-NLS-1$

							bNameExists = false;

							if (sNewName != null && !sNewName.equals("")) { //$NON-NLS-1$
								sNewName = sNewName.trim();

								int count = vtProjects.size();
								for (int i=0; i<count; i++) {
									String next = (String)vtProjects.elementAt(i);
									if (next.equals(sNewName)) {
										bNameExists = true;
										break;
									}
								}

								if (!bNameExists) {

									final File fsFile = choosenFile;
									final String fsDatabaseName = sNewName;

									Thread thread = new Thread("UIDatabaseManagementDialog.actionPerformed-RestoreNew") { //$NON-NLS-1$
										public void run() {
											try {
												DBRestoreDatabase restore = new DBRestoreDatabase(FormatProperties.nDatabaseType, ProjectCompendium.APP.adminDatabase, mysqlname, mysqlpassword, mysqlip);

												restore.addProgressListener((DBProgressListener) manager);
												oThread = new ProgressThread(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIDatabaseManagementDialog.progressRestoringProject"), LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIDatabaseManagementDialog.restorationComplete")); //$NON-NLS-1$ //$NON-NLS-2$
												oThread.start();
												restore.restoreDatabaseAsNew(fsDatabaseName, fsFile, true);
												restore.removeProgressListener((DBProgressListener) manager);

												// UPDATE MAIN FRAME DATA AND LOCAL LIST
												ProjectCompendium.APP.updateProjects();
												vtProjects.removeAllElements();												
												vtProjects = ProjectCompendium.APP.getProjects();
												updateProjectList();
											}
											catch(DBDatabaseNameException ex) { // SHOULD NEVER HAPPEN, BUT MUST STILL BE HANDLED
												progressComplete();
												ProjectCompendium.APP.displayMessage(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIDatabaseManagementDialog.errorNameClashA")+"\n\n"+
														LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIDatabaseManagementDialog.errorNameClashB")+"\n", LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIDatabaseManagementDialog.restoreToNewProject")); //$NON-NLS-1$ //$NON-NLS-2$
												return;
											}
											catch(DBDatabaseTypeException ex) {
												progressComplete();
												ProjectCompendium.APP.displayError(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIDatabaseManagementDialog.errorConnectToProject")+":\n\n"+ex.getMessage()); //$NON-NLS-1$
												return;
											}
											catch(ClassNotFoundException ex) {
												progressComplete();
												ProjectCompendium.APP.displayError(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIDatabaseManagementDialog.errorConnectToProject")+":\n\n"+ex.getMessage()); //$NON-NLS-1$
												return;
											}
											catch(SQLException ex) {
												ex.printStackTrace();
												progressComplete();
												ProjectCompendium.APP.displayError(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIDatabaseManagementDialog.errorRestoring")+":\n\n"+ex.getMessage()); //$NON-NLS-1$
												return;
											} catch (DBProjectListException ex) {
												progressComplete();
												ProjectCompendium.APP.displayError(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIDatabaseManagementDialog.errorConnectToProject")+":\n\n"+ex.getMessage()); //$NON-NLS-1$
												return;
											}
										}
									};
									thread.start();
								}
								else {
							   		JOptionPane.showMessageDialog(this, LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIDatabaseManagementDialog.errorProjectExistsA")+" '"+sNewName+"' "+
							   				LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIDatabaseManagementDialog.errorProjectExistsB")+"\n"+ //$NON-NLS-1$ //$NON-NLS-2$
							   				LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIDatabaseManagementDialog.errorProjectExistsC")+"\n", //$NON-NLS-1$ //$NON-NLS-2$
												 LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIDatabaseManagementDialog.warning"),JOptionPane.WARNING_MESSAGE); //$NON-NLS-1$
								}
							}
							else {
			   					JOptionPane.showMessageDialog(this, LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIDatabaseManagementDialog.errorNoProjectName")+"\n"); //$NON-NLS-1$
							}
						}
					}
				}
			}
		}
	}

	/**
	 * Check for backup before Delete the selected database.
	 *
	 * @param sFriendlyName, the user assigned name for the selected database project to delete.
	 * @param sDatabaseName, the system assigned name for the selected database project to delete.
	 */
	private void onDelete(String sFriendlyName, String sDatabaseName) {

		// CHECK AGAINST DEFAULT
		if ((ProjectCompendium.APP.getDefaultDatabase()).equals(sFriendlyName)) {
	  		int answer = JOptionPane.showConfirmDialog(this, "'"+sFriendlyName+"' "+LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIDatabaseManagementDialog.warningDefaultProjectA")+
	  				"\n\n"+LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIDatabaseManagementDialog.warningDefaultProjectB")+"\n", LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIDatabaseManagementDialog.warning")+
	  				"\n\n", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);

			if (answer == JOptionPane.NO_OPTION) {
				return;
			}
			else if (answer == JOptionPane.YES_OPTION) {
				ProjectCompendium.APP.setDefaultDatabase(""); //$NON-NLS-1$
				ProjectCompendium.APP.displayMessage(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIDatabaseManagementDialog.restoreDefaultProject"), LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIDatabaseManagementDialog.defaultProject")); //$NON-NLS-1$ //$NON-NLS-2$
			}
		}

  		int answer = JOptionPane.showConfirmDialog(this, "'"+sFriendlyName+"' "+LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIDatabaseManagementDialog.aboutToDelete")+"\n", LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIDatabaseManagementDialog.warning"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);

		String fileName = null;
		if (answer == JOptionPane.YES_OPTION) {
			onBackup(sFriendlyName, sDatabaseName, RESUME_DELETE, false);
		}
		else if (answer == JOptionPane.NO_OPTION) {
			doDelete(sFriendlyName, sDatabaseName, false);
		}
	}

	/**
	 * Delete the selected database
	 *
	 * @param sFriendlyName, the user assigned name for the selected database project to delete.
	 * @param sDatabaseName, the system assigned name for the selected database project to delete.
	 * @param bHasBackedUp, has the user backuped up the data before doing this delete?
	 */
	private void doDelete(String sFriendlyName, String sDatabaseName, boolean bHasBackedUp) {

		boolean bSure = false;

		int answer = -1;
		if (!bHasBackedUp) {
	   		answer = JOptionPane.showConfirmDialog(this, LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIDatabaseManagementDialog.warningDataLost2A")+"\n"+
	   				LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIDatabaseManagementDialog.warningDataLost2B")+"\n", LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIDatabaseManagementDialog.warning"), //$NON-NLS-1$ //$NON-NLS-2$
						JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);

	    	if (answer == JOptionPane.OK_OPTION) {
				bSure = true;
			}
		}

		if (bHasBackedUp || bSure) {
			try {
				databaseAdmin.deleteDatabase(sFriendlyName, sDatabaseName);
			}
			catch (SQLException ex) {
				ProjectCompendium.APP.displayError(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIDatabaseManagementDialog.errorDeletingProjectA")+"' "+sFriendlyName+"' "+LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIDatabaseManagementDialog.errorDeletingProjectB")+":\n\n"+ex.getMessage()); //$NON-NLS-1$ //$NON-NLS-2$
			} 
			catch (DBProjectListException ex) {
				ProjectCompendium.APP.displayError(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIDatabaseManagementDialog.errorDeletingProjectA")+" '"+sFriendlyName+"' "+LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIDatabaseManagementDialog.errorDeletingProjectB")+":\n\n"+ex.getMessage()); //$NON-NLS-1$ //$NON-NLS-2$
			}
		
			// UPDATE MAIN FRAME DATA AND LOCAL LIST
			ProjectCompendium.APP.updateProjects();
			vtProjects.removeAllElements();			
			vtProjects = ProjectCompendium.APP.getProjects();
			updateProjectList();
   		}
	}

	/**
	 * Reload the list of database projects after a change has occurred.
	 */
	private void updateProjectList() {
		//htProjectCheck = databaseAdmin.getProjectSchemaStatus();
		//lstProjects.updateProjectList(vtProjects, htProjectCheck);
		lstProjects.updateProjectList(vtProjects);		
	}

	/**
	 * Override, to check for backup dialog being disposed of correctly.
	 */
	public void onCancel() {
		if (oBackupDialog != null) {
			oBackupDialog.dispose();
			oBackupDialog = null;
		}

		setVisible(false);
		dispose();
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
	 * @param int nCount, the current position of the progress in relation to the inital count.
	 * @param String sMessage, the message to display to the user.
	 */
    public void progressUpdate(int nIncrement, String sMessage) {
		nCount += nIncrement;
		oProgressBar.setValue(nCount);
		oProgressDialog.setMessage(sMessage);
		oProgressDialog.setStatus(nCount);
	}

	/**
	 * Indicate that progress has completed.
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
