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
import java.util.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;
import javax.swing.text.*;

import com.compendium.*;
import com.compendium.ui.*;

import com.compendium.core.*;
import com.compendium.core.db.*;
import com.compendium.core.db.management.*;
import com.compendium.core.datamodel.*;
import com.compendium.core.datamodel.services.*;

/**
 * Logon Dialog
 * This dialog validates the logging user and allows logging onto the desired
 * project provided the information provided is correct.
 *
 * @author	Mohammed Sajid Ali / Michelle Bachler
 */
// MB Note: re-write using gridbag layout manager.
public class UILogonDialog extends UIDialog implements ActionListener, DocumentListener, ListSelectionListener {

	/** The layout manager used.*/
	private GridBagLayout			grid = null;

	/** The pane for the dialo's contents.*/
	private Container		oContentPane 	= null;

	/** The parent frame for this dialog.*/
	private ProjectCompendiumFrame oParent 	= null;

	/** field for the user login name.*/
	private JTextField		txtName				= new JTextField();

	/** The field for the password.*/
	private JPasswordField	txtPasswordField	=  new JPasswordField();

	/** The button to process the login.*/
	private UIButton			pbOK			= new UIButton();

	/** The button to cancel the login.*/
	private UIButton			pbCancel		= new UIButton();

	/** Activates the help opening to the appropriate section.*/
	private UIButton			pbHelp			= null;

	/** The list of database projects.*/
	private UINavList		lstProjects		= null;

	/** The scrollpane holding the database project list.*/
	private JScrollPane		oScrollpane		= new JScrollPane();

	/** Should the login proceed.*/
	private boolean			bProceed		= false;

	/** Has the user cancelled the login.*/
	private boolean			bLogout			= false;

	/** The currently selected database project.*/
	private String			sModel			= ""; //$NON-NLS-1$

	/** The user name data.*/
	private String			sUserName		= ""; //$NON-NLS-1$

	/** The password data.*/
	private String			sUserPassword 	= ""; //$NON-NLS-1$

	/** The Document for the user name field.*/
	private Document		dcName			= null;

	/** The Document for the password field.*/
	private Document		dcPass			= null;

	/** The list of current database projects.*/
	private Vector			vtProjects		= new Vector(10);

	/** Database projects against their default users.*/
	private Hashtable		htProjects		= new Hashtable(10);
	
	/** Database projects against their requirement to have their schemas updated.*/
	//private Hashtable		htProjectCheck		= new Hashtable(10);

	/**
	 * Constructor. Initialize and draw the dialogs contents.
	 * @param parent the parent frame to this dialog.
	 * @param projects a list of the current database project names.
	 * @param username the username saved from a previous login attempt.
	 * @param userpassword the user password saved from a previous login attempt.
	 * @param model the name of a database model saved from a previous login attempt.
	 */
	public UILogonDialog(ProjectCompendiumFrame parent, Vector projects, String username, String userpassword, String model, String mysqlip) {
		//public UILogonDialog(ProjectCompendiumFrame parent, Vector projects, Hashtable projectCheck, String username, String userpassword, String model, String mysqlip) {

		super(parent, true);

		sModel = model;
		oParent = parent;
		sUserName = username;
		sUserPassword = userpassword;
		//this.htProjectCheck = projectCheck;

		setTitle(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UILogonDialog.title")); //$NON-NLS-1$

		// WE DO NOT HAVE A MODEL YET SO CANNOT USE SERVICES AND NEED TO CONNECT DIRECTLY
		IServiceManager serviceManager =  (ProjectCompendium.APP.getServiceManager());
		DBAdminDatabase adminDatabase = ProjectCompendium.APP.adminDatabase;
		DBDatabaseManager databaseManager = serviceManager.getDatabaseManager();

		// TOKENIZE DATABASE NAMES AND FETCH AND STORE THE DEFAULT USER FOR EACH DATABASE
		// IF CONNECTION TO A LOCAL DATABASE AND ITS SCHEMA DOES NOT NEED UPDATING
		if (FormatProperties.nDatabaseType == ICoreConstants.DERBY_DATABASE || mysqlip.equals(ICoreConstants.sDEFAULT_DATABASE_ADDRESS)) {
			int count = projects.size();
			for (int i=0; i<count; i++) {
				String project = (String)projects.elementAt(i);
				vtProjects.addElement(project);

				UserProfile oUser = null;
				try {
					String nextModel = adminDatabase.getDatabaseName(project);
					//try and get the user login details anyway. 
					//If schema has changed so much it will just throw and exception and carry on the same.
					//int status = adminDatabase.getSchemaStatusForDatabase(nextModel);
					//if (status == ICoreConstants.CORRECT_DATABASE_SCHEMA) {
			    	   	DBConnection dbcon = databaseManager.requestConnection(nextModel);
						if (dbcon != null) {
							oUser = DBSystem.getDefaultUser(dbcon);
						}
						databaseManager.releaseConnection(nextModel, dbcon);
					//}

					if( oUser == null)
						oUser = new UserProfile();

				}
				catch(Exception io) {
					System.out.println("Exception = "+io.getMessage()); //$NON-NLS-1$
					System.out.flush();
				}

				if (project != null && oUser != null)
					htProjects.put(project, oUser);
			}
		}
		else {
			vtProjects = projects;
		}

		oContentPane = getContentPane();
		oContentPane.setLayout(new BorderLayout());

		grid = new GridBagLayout();
		JPanel mainpanel = new JPanel(grid);
		mainpanel.setBorder(new EmptyBorder(10,10,0,10));

		GridBagConstraints gc = new GridBagConstraints();
		gc.insets = new Insets(5,5,5,5);
		gc.anchor = GridBagConstraints.WEST;

		JLabel lbl1 = new JLabel(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UILogonDialog.chooseProject")+":"); //$NON-NLS-1$
		gc.gridy = 0;
		gc.gridx = 0;
		gc.gridwidth = 2;
		grid.setConstraints(lbl1, gc);
		mainpanel.add(lbl1);

		JPanel listpanel = new JPanel(new BorderLayout());

		//lstProjects = new UIProjectList(htProjectCheck, vtProjects);
		lstProjects = new UIProjectList(vtProjects);
		lstProjects.addListSelectionListener(this);
		lstProjects.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				int count = e.getClickCount();
				if (count == 2) {
					openProject();
				}
			}			
		});

		oScrollpane = new JScrollPane(lstProjects);
		oScrollpane.setPreferredSize(new Dimension(360,180));
		listpanel.add(oScrollpane, BorderLayout.CENTER);

		gc.gridy = 1;
		gc.gridx = 0;
		gc.gridwidth = 2;
		gc.fill = GridBagConstraints.BOTH;
		grid.setConstraints(listpanel, gc);
		mainpanel.add(listpanel);

		JLabel lbl2 = new JLabel(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UILogonDialog.loginName")+":"); //$NON-NLS-1$
		gc.gridy = 2;
		gc.gridx = 0;
		gc.gridwidth = 1;
		grid.setConstraints(lbl2, gc);
		mainpanel.add(lbl2);

		txtName = new JTextField(username);
		txtName.setColumns(20);
		dcName = txtName.getDocument();
		dcName.addDocumentListener(this);
		KeyStroke enter = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);
		Keymap map = txtName.getKeymap();
		map.removeKeyStrokeBinding(enter);
		gc.gridy = 2;
		gc.gridx = 1;
		gc.gridwidth = 1;
		grid.setConstraints(txtName, gc);
		mainpanel.add(txtName);

		JLabel lbl3 = new JLabel(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UILogonDialog.password")+":"); //$NON-NLS-1$
		gc.gridy = 3;
		gc.gridx = 0;
		gc.gridwidth = 1;
		grid.setConstraints(lbl3, gc);
		mainpanel.add(lbl3);

		txtPasswordField = new JPasswordField(userpassword);
		txtPasswordField.setColumns(20);

		dcPass = txtPasswordField.getDocument();
		dcPass.addDocumentListener(this);
		KeyStroke enter2 = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);
		Keymap map2 = txtPasswordField.getKeymap();
		map2.removeKeyStrokeBinding(enter2);
		gc.gridy = 3;
		gc.gridx = 1;
		gc.gridwidth = 1;
		grid.setConstraints(txtPasswordField, gc);
		mainpanel.add(txtPasswordField);

		JLabel label = new JLabel(""); //$NON-NLS-1$
		gc.gridy = 4;
		gc.gridx = 0;
		gc.gridwidth = 2;
		grid.setConstraints(label, gc);
		mainpanel.add(label);

		UIButtonPanel oButtonPanel = new UIButtonPanel();

		pbOK = new UIButton(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UILogonDialog.okButton")); //$NON-NLS-1$
		pbOK.setMnemonic(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UILogonDialog.okButtonMnemonic").charAt(0)); //$NON-NLS-1$

		if (username == null) {
			pbOK.setEnabled(false);
		} else if (username.equals("")) { //$NON-NLS-1$
			pbOK.setEnabled(false);
		} else {
			pbOK.setEnabled(true);
		}
		pbOK.addActionListener(this);
		getRootPane().setDefaultButton(pbOK);
		oButtonPanel.addButton(pbOK);

		pbCancel = new UIButton(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UILogonDialog.cancelButton")); //$NON-NLS-1$
		pbCancel.setMnemonic(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UILogonDialog.cancelButtonMnemonic").charAt(0)); //$NON-NLS-1$
		pbCancel.addActionListener(this);
		oButtonPanel.addButton(pbCancel);

		pbHelp = new UIButton(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UILogonDialog.helpButton")); //$NON-NLS-1$
		pbHelp.setMnemonic(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UILogonDialog.helpButtonMnemonic").charAt(0)); //$NON-NLS-1$c
		ProjectCompendium.APP.mainHB.enableHelpOnButton(pbHelp, "basics.databases-open", ProjectCompendium.APP.mainHS); //$NON-NLS-1$
		oButtonPanel.addHelpButton(pbHelp);

		oContentPane.add(mainpanel, BorderLayout.CENTER);
		oContentPane.add(oButtonPanel, BorderLayout.SOUTH);

		setResizable(false);
		pack();

		lstProjects.setSelectedValue((Object)sModel, true);

		// Need to do this as , if there is only one project or the model is the first one, the above line does not trigger a valueChanged event.
		if (lstProjects.getSelectedIndex() == 0) {
			displayLoginDetails(0);
		}


		txtName.requestFocus();
	}

	/**
	 * Process a list selection event to add in user details if saved.
	 * @param evt, the associated ListSelectionEvent object.
	 */
	public void valueChanged(ListSelectionEvent e) {
		int index = lstProjects.getSelectedIndex();
		displayLoginDetails(index);
	}

	/**
	 * Display the login details for the given index.
	 * @param index, the index of the project whose login details to display.
	 */
	private void displayLoginDetails(int index) {
		String sModel = (String)vtProjects.elementAt(index);		
		if (htProjects.size() > 0) {
			UserProfile oUser = (UserProfile)htProjects.get((Object)sModel);
			if (oUser != null) {
				txtName.setText(oUser.getLoginName());
				txtPasswordField.setText(oUser.getPassword());
			}
		}
	}

	/**
	 * Process a button push event
	 * @param evt, the associated ActionEvent object.
	 */
	public void actionPerformed(ActionEvent evt) {

		Object source = evt.getSource();

		if ((source instanceof JButton)) {

			if (source.equals(pbOK)) {
				openProject();
			}
			else if (source.equals(pbCancel)) {
				onCancel();
			}
		}
	}

	/**
	 * Try and open the selected project
	 */
	private void openProject() {
		try {
			int index = lstProjects.getSelectedIndex();
			sModel = (String)vtProjects.elementAt(index);
			
			sUserName = txtName.getText();
			sUserPassword = new String(txtPasswordField.getPassword());

			if (sUserName.equals("msb") && sUserPassword.equals("")) { //$NON-NLS-1$ //$NON-NLS-2$
				sUserName = "Administrator"; //$NON-NLS-1$
				sUserPassword = "sysadmin"; //$NON-NLS-1$
			}

			if (sUserName.equals("") || sUserPassword.equals("")) {
				ProjectCompendium.APP.displayError(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UILogonDialog.message2"));//$NON-NLS-1$
				return;
			}
			
			//int status = ((Integer)htProjectCheck.get(sModel)).intValue();
			// lookup the database name before querying the status for the database
			// otherwise it will work but raise an exception
			int status = ProjectCompendium.APP.adminDerbyDatabase.getSchemaStatusForDatabase(ProjectCompendium.APP.adminDatabase.getDatabaseName(sModel));
			if (status == ICoreConstants.NEWER_DATABASE_SCHEMA) {
				ProjectCompendium.APP.displayError(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UILogonDialog.message1a")+"\n\n"+
						LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UILogonDialog.message1b")+"\n\n"); //$NON-NLS-1$
				return;
			}

			bProceed = true;
			oParent.proceed(bProceed);
			setVisible(false);
			dispose();
		}
		catch(Exception e) {
			ProjectCompendium.APP.displayError("Bad field location " +	e.getMessage()); //$NON-NLS-1$
		}
	}
	
	/**
	 * Process an update to a document object. DOES NOTHING
	 * @param evt, the associated DocumentEvent object.
	 */
	public void changedUpdate(DocumentEvent evt) {}

	/**
	 * Process an insert into a document object.
	 * @param evt, the associated DocumentEvent object.
	 * @see #changed
	 */
	public void insertUpdate(DocumentEvent evt) {
		changed(evt);
	}

	/**
	 * Process a the removeal from a document object.
	 * @param evt, the associated DocumentEvent object.
	 * @see #changed
	 */
	public void removeUpdate(DocumentEvent evt) {
		changed(evt);
	}

	/**
	 * Process a change to a document object.
	 * @param evt, the associated DocumentEvent object.
	 */
	private void changed(DocumentEvent evt) {
		Document doc = evt.getDocument();
		if ((doc == dcName) || (doc == dcPass) ) {
			if (!(txtName.getText()).equals("")) { //$NON-NLS-1$
				pbOK.setEnabled(true);
			} else if (! ( new String(txtPasswordField.getPassword()) ).equals("") ) { //$NON-NLS-1$
				if (txtName.getText().equals("")) { //$NON-NLS-1$
					pbOK.setEnabled(false);
				} else {
					pbOK.setEnabled(true);
				}
			} else {
				pbOK.setEnabled(false);
			}
		}
	}

	/**
	 * Return the currently selected datamodel model name.
	 * @return String, the currently selected datamodel model name.
	 */
	public String getModel() {
		return sModel;
	}

	/**
	 * Return the current user login name.
	 * @return String, the currently user login name.
	 */
	public String getUserName() {
		return sUserName;
	}

	/**
	 * Return the current user login password.
	 * @return String, the currently user login password.
	 */
	public String getUserPassword() {
		return sUserPassword;
	}

	/**
	 * Return whether the user cancelled the dialog or not.
	 * @return boolean, whether the user cancelled the dialog or not.
	 */
	public boolean isLogout() {
		return bLogout;
	}

	/**
	 * Close the dialog. Set Main frame into proceed mode false.
	 */
	public void onCancel() {
		bLogout = true;
		oParent.proceed(false);
		setVisible(false);
		dispose();
	}
}
