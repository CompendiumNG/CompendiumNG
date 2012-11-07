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
import java.text.*;
import java.util.*;

import javax.swing.*;
import javax.swing.border.*;

import com.compendium.*;
import com.compendium.core.*;
import com.compendium.core.datamodel.*;
import com.compendium.core.datamodel.services.*;
import com.compendium.core.db.management.*;
import com.compendium.ui.*;

/**
 * This class allows to user to manage MySQL Connection Profiles
 * This basically gives a user a convenient method for setting the MySQL property information
 * required by Compendium to access a remote MySQL Server,
 * or the local MySQL server using a username and password other than 'root' and 'null'.
 * It reads and writes to the MySQL.properties file.
 *
 * @author	Michelle Bachler
 */
public class UIDatabaseAdministrationDialog extends UIDialog implements ActionListener, ItemListener {

	private Container			oContentPane			= null;

	private UIButton			pbSave					= null;
	private UIButton			pbNew					= null;
	private UIButton			pbDelete				= null;
	private UIButton			pbEdit					= null;

	private UIButton			pbConnect				= null;
	private UIButton			pbClose					= null;
	private UIButton			pbHelp					= null;

	/** Turn on display of full data source path in title bar.*/
	private JCheckBox			rbDisplayFullPath		= null;

	private JPanel				oDetailsPanel 			= null;
	private JPanel				oDatabaseTypePanel		= null;
	private JPanel				oMySQLPanel 			= null;

	private JLabel				oServerLabel 			= null;
	private JLabel				oNameLabel 				= null;
	private JLabel				oPasswordLabel 			= null;
	private JLabel				oConfirmPasswordLabel 	= null;
	private JLabel				oPortLabel 				= null;
	private JLabel				oInfoLabel				= null;

	private JTextField			oServerField 			= null;
	private JTextField			oNameField 				= null;
	private JPasswordField		oPasswordField 			= null;
	private JPasswordField		oConfirmPasswordField 	= null;
	private JTextField			oPortField			 	= null;

	private JComboBox			oProfiles				= null;

	private JRadioButton		rbDerby					= null;
	private JRadioButton		rbMySQL					= null;

	private String				sProfile				= ""; //$NON-NLS-1$
	private int					nType					= -1;
	private int					nNewType				= -1;

	private GridBagLayout		oGrid 					= null;

	private Vector 				oData					= null;

	private ExternalConnection  oCurrentConnection		= null;

	private int 				nInitialIndex 			= 0;

	/** Indicates if the current profile is new and has not been saved yet.*/
	private boolean isNew = false;

	/** Local reference to the Derby administration object.*/
	private DBAdminDerbyDatabase adminDatabase			= null;

	/**
	 * Constructor for UIDatabaseAdministration class
	 */
	public UIDatabaseAdministrationDialog(JFrame oParent, int nType, ExternalConnection connection) {

		super(oParent, true);
		this.nType = nType;
		setTitle(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIDatabaseAdministrationDialog.databaseConnectionsTitle")); //$NON-NLS-1$
		oCurrentConnection = connection;

		oContentPane = getRootPane().getContentPane();
		oContentPane.setLayout(new BorderLayout());

		adminDatabase = ProjectCompendium.APP.adminDerbyDatabase;

		drawScreen(connection);

		pack();
	}

	/**
	 * Draw the main frame screen.
	 */
	private void drawScreen(ExternalConnection connection) {

		oDatabaseTypePanel = new JPanel(new GridLayout(3,1));

		rbDisplayFullPath = new JCheckBox(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIDatabaseAdministrationDialog.fullPath")); //$NON-NLS-1$
		rbDisplayFullPath.addItemListener(this);
		oDatabaseTypePanel.add(rbDisplayFullPath);

		if (FormatProperties.displayFullPath)
			rbDisplayFullPath.setSelected(true);

		rbDerby = new JRadioButton(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIDatabaseAdministrationDialog.defaultDatabase")); //$NON-NLS-1$
		rbMySQL = new JRadioButton(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIDatabaseAdministrationDialog.mysqlDatabase")); //$NON-NLS-1$
		rbDerby.addItemListener(this);
		rbMySQL.addItemListener(this);
		ButtonGroup group = new ButtonGroup();
		group.add(rbDerby);
		group.add(rbMySQL);
		oDatabaseTypePanel.add(rbDerby);
		oDatabaseTypePanel.add(rbMySQL);

		oMySQLPanel = new JPanel();
		oMySQLPanel.setBorder(new EtchedBorder());
		oGrid = new GridBagLayout();
		oMySQLPanel.setLayout(oGrid);

		GridBagConstraints gc = new GridBagConstraints();
		gc.insets = new Insets(5,5,5,5);
		gc.anchor = GridBagConstraints.WEST;

		gc.gridwidth = 2;
		createProfilesChoiceBox();
		oGrid.setConstraints(oProfiles, gc);

		pbEdit = new UIButton("..."); //$NON-NLS-1$
		gc.gridwidth = 1;
		pbEdit.addActionListener(this);
		oGrid.setConstraints(pbEdit, gc);

		pbNew = new UIButton(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIDatabaseAdministrationDialog.newButton")); //$NON-NLS-1$
		pbNew.setMnemonic(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIDatabaseAdministrationDialog.newButtonMnemonic").charAt(0)); //$NON-NLS-1$
		pbNew.addActionListener(this);
		gc.gridwidth = GridBagConstraints.REMAINDER;
		oGrid.setConstraints(pbNew, gc);

		//oInfoLabel = new JLabel("If the Host address is left blank, 'localhost' will be used");
		//oInfoLabel.setFont(new Font("sans serif", Font.PLAIN, 10));
		//gc.gridwidth=GridBagConstraints.REMAINDER;
		//oGrid.setConstraints(oInfoLabel, gc);

		oServerLabel = new JLabel(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIDatabaseAdministrationDialog.mySQLHostAddress")+": "); //$NON-NLS-1$
		gc.gridwidth = 1;
		oGrid.setConstraints(oServerLabel, gc);

		oServerField = new JTextField();
		oServerField.setColumns(20);
		gc.gridwidth = GridBagConstraints.REMAINDER;
		oGrid.setConstraints(oServerField, gc);

		oNameLabel = new JLabel(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIDatabaseAdministrationDialog.mySQLUserName")+": "); //$NON-NLS-1$
		gc.gridwidth = 1;
		oGrid.setConstraints(oNameLabel, gc);

		oNameField = new JTextField();
		oNameField.setColumns(20);
		gc.gridwidth = GridBagConstraints.REMAINDER;
		oGrid.setConstraints(oNameField, gc);

		oPasswordLabel = new JLabel(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIDatabaseAdministrationDialog.mySQLPassword")+": "); //$NON-NLS-1$
		gc.gridwidth = 1;
		oGrid.setConstraints(oPasswordLabel, gc);

		oPasswordField = new JPasswordField();
		oPasswordField.setColumns(20);
		gc.gridwidth = GridBagConstraints.REMAINDER;
		oGrid.setConstraints(oPasswordField, gc);

		oConfirmPasswordLabel = new JLabel(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIDatabaseAdministrationDialog.confirmPassword")+": "); //$NON-NLS-1$
		gc.gridwidth = 1;
		oGrid.setConstraints(oConfirmPasswordLabel, gc);

		oConfirmPasswordField = new JPasswordField();
		oConfirmPasswordField.setColumns(20);
		gc.gridwidth = GridBagConstraints.REMAINDER;
		oGrid.setConstraints(oConfirmPasswordField, gc);

		//gc = new GridBagConstraints();
		//gc.insets = new Insets(5,5,5,5);
		//gc.anchor = GridBagConstraints.WEST;

		//oPortLabel = new JLabel("Port: ");
		//oGrid.setConstraints(oPortLabel, gc);

		//oPortField = new JTextField(sPort);
		//oPortField.setColumns(20);
		//gc.gridwidth = GridBagConstraints.REMAINDER;
		//oGrid.setConstraints(oPortField, gc);

		pbSave = new UIButton(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIDatabaseAdministrationDialog.saveButton")); //$NON-NLS-1$
		pbSave.setMnemonic(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIDatabaseAdministrationDialog.saveButtonMnemonic").charAt(0));
		pbSave.addActionListener(this);
		gc.gridwidth = 1;
		oGrid.setConstraints(pbSave, gc);

		pbDelete = new UIButton(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIDatabaseAdministrationDialog.deleteButton")); //$NON-NLS-1$
		pbDelete.setMnemonic(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIDatabaseAdministrationDialog.deleteButtonMnemonic").charAt(0));
		pbDelete.addActionListener(this);
		gc.gridwidth = GridBagConstraints.REMAINDER;
		oGrid.setConstraints(pbDelete, gc);

		oMySQLPanel.add(oProfiles);
		//oMySQLPanel.add(pbEdit);
		oMySQLPanel.add(pbNew);
		//oMySQLPanel.add(oInfoLabel);
		oMySQLPanel.add(oServerLabel);
		oMySQLPanel.add(oServerField);
		oMySQLPanel.add(oNameLabel);
		oMySQLPanel.add(oNameField);
		oMySQLPanel.add(oPasswordLabel);
		oMySQLPanel.add(oPasswordField);
		oMySQLPanel.add(oConfirmPasswordLabel);
		oMySQLPanel.add(oConfirmPasswordField);
		oMySQLPanel.add(pbSave);
		oMySQLPanel.add(pbDelete);

		oDetailsPanel = new JPanel(new BorderLayout());
		oDetailsPanel.setBorder(new EmptyBorder(10,10,10,10));
		oDetailsPanel.add(oDatabaseTypePanel, BorderLayout.NORTH);
		oDetailsPanel.add(oMySQLPanel, BorderLayout.CENTER);

		UIButtonPanel oButtonPanel = new UIButtonPanel();

		pbConnect = new UIButton(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIDatabaseAdministrationDialog.connectButton")); //$NON-NLS-1$
		pbConnect.setMnemonic(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIDatabaseAdministrationDialog.connectButtonMnemonic").charAt(0));
		pbConnect.addActionListener(this);
		pbConnect.setEnabled(false);
		pbConnect.addItemListener(this);
		oButtonPanel.addButton(pbConnect);

		pbClose = new UIButton(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIDatabaseAdministrationDialog.closeButton")); //$NON-NLS-1$
		pbClose.setMnemonic(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIDatabaseAdministrationDialog.closeButtonMnemonic").charAt(0));
		pbClose.addActionListener(this);
		oButtonPanel.addButton(pbClose);

		// Add help button
		pbHelp = new UIButton(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIDatabaseAdministrationDialog.helpButton")); //$NON-NLS-1$
		pbHelp.setMnemonic(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIDatabaseAdministrationDialog.helpButtonMnemonic").charAt(0));
		ProjectCompendium.APP.mainHB.enableHelpOnButton(pbHelp, "basics.admin", ProjectCompendium.APP.mainHS); //$NON-NLS-1$
		oButtonPanel.addHelpButton(pbHelp);

		oContentPane.add(oDetailsPanel, BorderLayout.CENTER);
		oContentPane.add(oButtonPanel, BorderLayout.SOUTH);

		if (connection != null && nType == ICoreConstants.MYSQL_DATABASE) {
			int count = oProfiles.getItemCount();
			sProfile = connection.getProfile();
			boolean bFound = false;

			for (int i=0; i<count; i++) {
				Object obj = oProfiles.getItemAt(i);
				if (obj instanceof ExternalConnection) {
					ExternalConnection con = (ExternalConnection)obj;
					if (sProfile.equals(con.getProfile())) {
						nInitialIndex = i;
						oProfiles.setSelectedIndex(i);
						bFound = true;
						break;
					}
				}
			}

			if (bFound) {
				oServerField.setText(connection.getServer());
				oNameField.setText(connection.getLogin());
				oPasswordField.setText(connection.getPassword());
				oConfirmPasswordField.setText(connection.getPassword());
				pbConnect.setEnabled(true);
			}
		}

		if (nType==ICoreConstants.DERBY_DATABASE)
			rbDerby.setSelected(true);
		else
			rbMySQL.setSelected(true);

		if (pbConnect.isEnabled()) {
			getRootPane().setDefaultButton(pbConnect);
		} else {
			getRootPane().setDefaultButton(pbClose);
		}
	}

	/**
	 * Listener for checkbox changes.
	 * @param e, the associated ItemEvent object.
	 */
	public void itemStateChanged(ItemEvent e) {

		Object source = e.getItemSelectable();

		if (source == pbConnect) {
			if (pbConnect.isEnabled()) {
				getRootPane().setDefaultButton(pbConnect);
			} else {
				getRootPane().setDefaultButton(pbClose);
			}
		}
		else if (source == rbDerby && rbDerby.isSelected()) {
			nNewType = ICoreConstants.DERBY_DATABASE;
			oServerLabel.setEnabled(false);
			oNameLabel.setEnabled(false);
			oPasswordLabel.setEnabled(false);
			oConfirmPasswordLabel.setEnabled(false);
			//oPortLabel.setEnabled(false);
			oServerField.setEnabled(false);
			oNameField.setEnabled(false);
			oPasswordField.setEnabled(false);
			oConfirmPasswordField.setEnabled(false);
			//PortField.setEnabled(false);
			oProfiles.setEnabled(false);
			pbDelete.setEnabled(false);
			pbNew.setEnabled(false);
			pbEdit.setEnabled(false);
			pbSave.setEnabled(false);
			pbConnect.setEnabled(false);
			//oInfoLabel.setEnabled(false);

			if (nType==ICoreConstants.MYSQL_DATABASE)
				pbConnect.setEnabled(true);
		}
		else if (source == rbMySQL && rbMySQL.isSelected()) {
			nNewType = ICoreConstants.MYSQL_DATABASE;

			oProfiles.setEnabled(true);
			pbNew.setEnabled(true);

			if (oProfiles.getModel().getSize() == 2) {
				oProfiles.setSelectedIndex(1);
			}

			if (oProfiles.getSelectedIndex() > 0) {
				oServerLabel.setEnabled(true);
				oNameLabel.setEnabled(true);
				oPasswordLabel.setEnabled(true);
				oConfirmPasswordLabel.setEnabled(true);
				//oPortLabel.setEnabled(true);
				oServerField.setEnabled(true);
				oNameField.setEnabled(true);
				oPasswordField.setEnabled(true);
				oConfirmPasswordField.setEnabled(true);
				//PortField.setEnabled(true);
				pbDelete.setEnabled(true);
				pbEdit.setEnabled(true);
				pbSave.setEnabled(true);
				pbConnect.setEnabled(true);
			}
		}
		else if (source == rbDisplayFullPath) {
			if (rbDisplayFullPath.isSelected()) {
				FormatProperties.displayFullPath = true;
				FormatProperties.setFormatProp("displayFullPath", "true"); //$NON-NLS-1$ //$NON-NLS-2$
				FormatProperties.saveFormatProps();
			}
			else {
				FormatProperties.displayFullPath = false;
				FormatProperties.setFormatProp("displayFullPath", "false"); //$NON-NLS-1$ //$NON-NLS-2$
				FormatProperties.saveFormatProps();
			}

			if (nType == ICoreConstants.MYSQL_DATABASE) {
				ProjectCompendium.APP.setTitle(ICoreConstants.MYSQL_DATABASE, oCurrentConnection.getServer(), oCurrentConnection.getProfile(), ""); //$NON-NLS-1$
			}
			else {
				ProjectCompendium.APP.setDerbyTitle(""); //$NON-NLS-1$
			}
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
		oProfiles.setFont( new Font("Dialog", Font.PLAIN, 12 )); //$NON-NLS-1$

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

					// DISTIGUISH THE CURRENT PROFILE
					//if ( (ProjectCompendium.APP.getActiveCodeGroup()).equals((String)group.elementAt(0)) )
					//	setForeground(IUIConstants.DEFAULT_COLOR);

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

            	Thread choiceThread = new Thread("UIDatabaseAdministrationDialog: createProfilesChoiceBox") { //$NON-NLS-1$
                	public void run() {

						if (oProfiles != null && oProfiles.getSelectedItem() instanceof ExternalConnection) {

							oServerLabel.setEnabled(true);
							oNameLabel.setEnabled(true);
							oPasswordLabel.setEnabled(true);
							oConfirmPasswordLabel.setEnabled(true);
							//oPortLabel.setEnabled(true);
							oServerField.setEnabled(true);
							oNameField.setEnabled(true);
							oPasswordField.setEnabled(true);
							oConfirmPasswordField.setEnabled(true);
							//PortField.setEnabled(true);
							pbDelete.setEnabled(true);
							pbEdit.setEnabled(true);
							pbSave.setEnabled(true);
							//oInfoLabel.setEnabled(true);
							pbConnect.setEnabled(true);

	                		ExternalConnection connection = (ExternalConnection)oProfiles.getSelectedItem();

							// FILL DATA INTO FIELDS
							sProfile = connection.getProfile();

							oServerField.setText(connection.getServer());
							oNameField.setText(connection.getLogin());
							oPasswordField.setText(connection.getPassword());
							oConfirmPasswordField.setText(connection.getPassword());
						}
						else {
							oServerField.setText(""); //$NON-NLS-1$
							oNameField.setText(""); //$NON-NLS-1$
							oPasswordField.setText(""); //$NON-NLS-1$
							oConfirmPasswordField.setText(""); //$NON-NLS-1$

							oServerLabel.setEnabled(false);
							oNameLabel.setEnabled(false);
							oPasswordLabel.setEnabled(false);
							oConfirmPasswordLabel.setEnabled(false);
							//oPortLabel.setEnabled(false);
							oServerField.setEnabled(false);
							oNameField.setEnabled(false);
							oPasswordField.setEnabled(false);
							oConfirmPasswordField.setEnabled(false);
							//PortField.setEnabled(false);

							pbDelete.setEnabled(false);
							pbEdit.setEnabled(false);
							pbSave.setEnabled(false);
							pbConnect.setEnabled(false);
							//oInfoLabel.setEnabled(false);
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
			Vector profiles = adminDatabase.getMySQLConnections();
			profiles = CoreUtilities.sortList(profiles);
			profiles.insertElementAt((Object) new String("< Select Database Profile >"), 0); //$NON-NLS-1$
			oData = profiles;
			DefaultComboBoxModel comboModel = new DefaultComboBoxModel(profiles);
			oProfiles.setModel(comboModel);
			oProfiles.setSelectedIndex(0);
		}
		catch(Exception ex) {
			ProjectCompendium.APP.displayError("Exception: (UIDatabaseAdministrationDialog.updateProfileChoiceBoxData) " + ex.getMessage()); //$NON-NLS-1$
		}
	}

	/**
	 * Handle action events coming from the
	 * import and close buttons.
	 *
	 * @param ActionEvent evt, the ActionEvent object.
	 */
	public void actionPerformed(ActionEvent evt) {

		Object source = evt.getSource();
		if (source instanceof JButton) {

			if (source == pbEdit) {
				onEdit();
			}
			else if (source == pbNew) {
				onNew();
			}
			else if (source == pbSave) {
				onSave();
			}
			else if (source == pbDelete) {
				onDelete();
			}
			else if (source == pbConnect) {
				onConnect();
				onCancel();
			}
			else if (source == pbClose)
				onCancel();
		}
	}

	/**
	 * Ask the user for the new name of the current Connection profile and save.
 	 */
	private void onEdit() {

		if (oProfiles.getSelectedIndex() > 0) {

	   		ExternalConnection con = (ExternalConnection)oProfiles.getSelectedItem();

		   	String sNewName = JOptionPane.showInputDialog(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIDatabaseAdministrationDialog.editProfileName"), con.getProfile()); //$NON-NLS-1$
			sNewName = sNewName.trim();

			// CHECK NAME
			int count = oData.size();
			String sProfile = ""; //$NON-NLS-1$
			for (int i=0; i<count; i++) {
				Object obj = oData.elementAt(i);
				if (obj instanceof ExternalConnection) {
					ExternalConnection connection = (ExternalConnection)obj;
					sProfile = connection.getProfile();
					if (sProfile.equals(sNewName)) {
						ProjectCompendium.APP.displayError(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIDatabaseAdministrationDialog.profileExistsA")+"\n"+
								LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIDatabaseAdministrationDialog.profileExistsB")); //$NON-NLS-1$
						pbEdit.doClick();
						return;
					}
				}
			}

			con.setProfile(sNewName);
			DefaultComboBoxModel comboModel = new DefaultComboBoxModel(oData);
			oProfiles.setModel(comboModel);
			oProfiles.setSelectedItem(con);


			pbDelete.setEnabled(false);
			pbNew.setEnabled(false);
			pbConnect.setEnabled(false);
			oProfiles.setEnabled(false);
		}
	}

	/**
	 * Ask the user for the name of the new Connection profile and create and empty blank ExternalConnection object
 	 */
	private void onNew() {

		isNew = true;

		ExternalConnection con = new ExternalConnection();

	   	String sNewName = JOptionPane.showInputDialog(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIDatabaseAdministrationDialog.newName")); //$NON-NLS-1$
		sNewName = sNewName.trim();

		// CHECK NAME
		int count = oData.size();
		String sProfile = ""; //$NON-NLS-1$
		for (int i=0; i<count; i++) {
			Object obj = oData.elementAt(i);
			if (obj instanceof ExternalConnection) {
				ExternalConnection connection = (ExternalConnection)obj;
				sProfile = connection.getProfile();
				if (sProfile.equals(sNewName)) {
					ProjectCompendium.APP.displayError(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIDatabaseAdministrationDialog.profileExists")); //$NON-NLS-1$
					pbNew.doClick();
					return;
				}
			}
		}

		con.setProfile(sNewName);
		con.setType(ICoreConstants.MYSQL_DATABASE);
		con.setServer("localhost"); //$NON-NLS-1$
		con.setLogin("root"); //$NON-NLS-1$

		oData.removeElementAt(0);
		oData.addElement(con);
		oData = CoreUtilities.sortList(oData);
		oData.insertElementAt((Object) new String("< Select Database Profile >"), 0); //$NON-NLS-1$

		DefaultComboBoxModel comboModel = new DefaultComboBoxModel(oData);
		oProfiles.setModel(comboModel);
		oProfiles.setSelectedItem(con);

		pbDelete.setEnabled(false);
		pbNew.setEnabled(false);
		pbConnect.setEnabled(false);
		oProfiles.setEnabled(false);
	}

	/**
	 * Check that the fields have been completed correctly and inform the user if they have not.
	 * If they have, save the entered properties into the database.
 	 */
	private boolean onSave() {

		boolean successful = false;

		if (oProfiles.getSelectedIndex() > 0) {
			try {
				ExternalConnection connection = (ExternalConnection)oProfiles.getSelectedItem();
				String server = oServerField.getText();
				String username = oNameField.getText();
				String password = new String(oPasswordField.getPassword());
				String passwordConfirm = new String(oConfirmPasswordField.getPassword());

				if (server.equals("localhost") && username.equals("root") && password.equals("")) { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					password = ""; //$NON-NLS-1$
					passwordConfirm = ""; //$NON-NLS-1$
				}

				if ( !(server.equals("localhost") && username.equals("root")) && password.equals("")) { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					ProjectCompendium.APP.displayMessage(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIDatabaseAdministrationDialog.enterPassword"), LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIDatabaseAdministrationDialog.connectionProfile")); //$NON-NLS-1$ //$NON-NLS-2$
					oPasswordField.requestFocus();
					return false;
				}
				if (!(server.equals("localhost") && username.equals("root")) && passwordConfirm.equals("")) { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					ProjectCompendium.APP.displayMessage(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIDatabaseAdministrationDialog.enterPasswordConfirmation"), LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIDatabaseAdministrationDialog.connectionProfile")); //$NON-NLS-1$ //$NON-NLS-2$
					oConfirmPasswordField.requestFocus();
					return false;
				}
				if (!(server.equals("localhost") && username.equals("root")) && !password.equals(passwordConfirm)) { //$NON-NLS-1$ //$NON-NLS-2$
					ProjectCompendium.APP.displayMessage(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIDatabaseAdministrationDialog.passwordMissmatchA")+"\n\n"+
							LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIDatabaseAdministrationDialog.passwordMissmatchB")+"\n", LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIDatabaseAdministrationDialog.connectionProfile")); //$NON-NLS-1$ //$NON-NLS-2$
					oPasswordField.requestFocus();
					return false;
				}
				if (server.equals("")) { //$NON-NLS-1$
					ProjectCompendium.APP.displayMessage(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIDatabaseAdministrationDialog.enterServer"), LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIDatabaseAdministrationDialog.connectionProfile")); //$NON-NLS-1$ //$NON-NLS-2$
					oServerField.requestFocus();
					return false;
				}
				if (username.equals("")) { //$NON-NLS-1$
					ProjectCompendium.APP.displayMessage(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIDatabaseAdministrationDialog.enterUserName"), LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIDatabaseAdministrationDialog.connectionProfile")); //$NON-NLS-1$ //$NON-NLS-2$
					oNameField.requestFocus();
					return false;
				}

				connection.setServer(server);
				connection.setPassword(password);
				connection.setLogin(username);
				connection.setType(ICoreConstants.MYSQL_DATABASE);

				if (isNew) {
					adminDatabase.insertConnection(connection);
					if (nType == ICoreConstants.DERBY_DATABASE) {
						ProjectCompendium.APP.getToolBarManager().updateProfilesChoiceBoxData(0);
					}
					else {
						ProjectCompendium.APP.getToolBarManager().updateProfilesChoiceBoxData(nInitialIndex);
					}
				}
				else {
					adminDatabase.updateConnection(connection, sProfile, ICoreConstants.MYSQL_DATABASE);
				}

				successful = true;
			}
			catch(Exception ex) {
				ex.printStackTrace();
				ProjectCompendium.APP.displayError(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIDatabaseAdministrationDialog.errorMessage1")+":\n\n"+ex.getMessage()); //$NON-NLS-1$
			}

			pbDelete.setEnabled(true);
			pbConnect.setEnabled(true);
			oProfiles.setEnabled(true);
			pbNew.setEnabled(true);
			isNew = false;
		}

		return successful;
	}

	/**
	 * Delete the selected connection profile from the database
	 * If they have, save the entered properties into the database.
 	 */
	private boolean onDelete() {
		if (oProfiles.getSelectedIndex() > 0) {
			try {
				ExternalConnection connection = (ExternalConnection)oProfiles.getSelectedItem();

				if (oCurrentConnection != null && oCurrentConnection.getProfile().equals(connection.getProfile())) {
					ProjectCompendium.APP.displayError(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIDatabaseAdministrationDialog.cannotDeleteCurrentConnectionA")+"\n\n"+
							LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIDatabaseAdministrationDialog.cannotDeleteCurrentConnectionB")+"\n"); //$NON-NLS-1$
				}
				else {
					adminDatabase.deleteConnection(connection);

					oData.remove(connection);
					DefaultComboBoxModel comboModel = new DefaultComboBoxModel(oData);
					oProfiles.setModel(comboModel);
					oProfiles.setSelectedIndex(0);

					oServerField.setText(""); //$NON-NLS-1$
					oNameField.setText(""); //$NON-NLS-1$
					oPasswordField.setText(""); //$NON-NLS-1$
					oConfirmPasswordField.setText(""); //$NON-NLS-1$

					if (nType == ICoreConstants.DERBY_DATABASE) {
						ProjectCompendium.APP.getToolBarManager().updateProfilesChoiceBoxData(0);
					}
					else {
						ProjectCompendium.APP.getToolBarManager().updateProfilesChoiceBoxData(nInitialIndex);
					}
				}

				return true;
			}
			catch(Exception ex) {
				ProjectCompendium.APP.displayError(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIDatabaseAdministrationDialog.errorMessage2")+":\n\n"+ex.getMessage()); //$NON-NLS-1$
			}
		}
		return false;
	}

	/**
	 * Store the current database preference.
 	 */
	private boolean onConnect() {

		if (nType != nNewType && nNewType == ICoreConstants.DERBY_DATABASE) {
			if (ProjectCompendium.APP.setDerbyDatabaseProfile()) {
				return true;
			}
		}
		else {
			int index = oProfiles.getSelectedIndex();
			if (index > 0) {
				ExternalConnection connection = (ExternalConnection)oProfiles.getSelectedItem();
				if (ProjectCompendium.APP.setMySQLDatabaseProfile(connection)) {
					ProjectCompendium.APP.getToolBarManager().updateProfilesChoiceBoxData(index);
					return true;
				}
			}
		}
		return false;
	}
}
