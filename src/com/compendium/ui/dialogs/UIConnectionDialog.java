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
import java.awt.*;
import java.awt.event.*;
import java.io.*;

import javax.swing.*;
import javax.swing.border.*;

import com.compendium.*;
import com.compendium.ui.*;

/**
 * UIConnectionDialog defines the dialog to connect to a server for Jabber or IX Panel communications
 *
 * @author	Michelle Bachler
 */
public class UIConnectionDialog extends UIDialog implements ActionListener, IUIConstants {


	/** The button to open a connection.*/
	private UIButton				pbConnect	= null;

	/** The button to close the connection open.*/
	private UIButton				pbDisConnect	= null;

	/** The button to close the dialog.*/
	private UIButton				pbClose	= null;

	/** The button to open the relevant help.*/
	private UIButton				pbHelp	= null;

	/** The pane for the dialog's main contents.*/
	private Container				oContentPane = null;

	/** The panel with the labels and textfield in.*/
	private JPanel					oDetailsPanel = null;

	/** The panel with the buttons in.*/
	private UIButtonPanel			oButtonPanel = null;

	/** The label for the server field .*/
	private JLabel					oServerLabel = null;

	/** The label for the user name field.*/
	private JLabel					oNameLabel = null;

	/** The label for the password field.*/
	private JLabel					oPasswordLabel = null;

	/** The label for the tag name field.*/
	private JLabel					oTagNameLabel = null;

	/** The field for the server data.*/
	private JTextField				oServerField = null;

	/** The field for the user name.*/
	private JTextField				oNameField = null;

	/** The field for the password.*/
	private JPasswordField			oPasswordField = null;

	/** The field for the tag name.*/
	private JTextField				oTagNameField = null;

	/** The server data.*/
	private String 					sServer = ""; //$NON-NLS-1$

	/** The user name .*/
	private String					sUsername = ""; //$NON-NLS-1$

	/** The password.*/
	private String 					sPassword = ""; //$NON-NLS-1$

	/** The tag name.*/
	private String					sTagName = ""; //$NON-NLS-1$

	/** The layout manager used for this dialog.*/
	private GridBagLayout			grid = null;

	/** This holds the previously stored connection details, if any.*/
	private Properties				connectionProperties = null;

	/** Are we connecting to IX?.*/
	private boolean isIX = false;

	/** Are we connection to a standard Jabber account.*/
	private boolean isJabber = false;

	/** Are we connection to CliaMaker.*/
	private boolean isClaiMaker = false;


	/**
	 * Initializes and sets up the dialog.
	 * @param parent, the parent frame for this dialog.
	 * @param sType, the type of connection dialog to draw.
	 */
	public UIConnectionDialog(JFrame parent, String sType) {

		super(parent, true);

		if (sType.equals("IXPanel")) { //$NON-NLS-1$
			isIX = true;
		  	this.setTitle("IX Panel "+LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIConnectionDialog.titleEnd")); //$NON-NLS-1$
		}
		else if (sType.equals("Jabber")) { //$NON-NLS-1$
			isJabber = true;
		  	this.setTitle("Jabber "+LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIConnectionDialog.titleEnd")); //$NON-NLS-1$
		}
		else {
			isClaiMaker = true;
		  	this.setTitle("ClaiMaker "+LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIConnectionDialog.titleEnd")); //$NON-NLS-1$
		}

		oContentPane = getContentPane();
		oContentPane.setLayout(new BorderLayout());

		loadProperties();

		oDetailsPanel = new JPanel();
		oDetailsPanel.setBorder(new EmptyBorder(10,10,10,10));
		grid = new GridBagLayout();
		oDetailsPanel.setLayout(grid);

		GridBagConstraints gc = new GridBagConstraints();
		gc.insets = new Insets(5,5,5,5);
		gc.anchor = GridBagConstraints.WEST;

		oServerLabel = new JLabel(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIConnectionDialog.server")+": "); //$NON-NLS-1$
		grid.setConstraints(oServerLabel, gc);

		oServerField = new JTextField(sServer);
		oServerField.setColumns(30);
		gc.gridwidth = GridBagConstraints.REMAINDER;
		grid.setConstraints(oServerField, gc);

		gc = new GridBagConstraints();
		gc.insets = new Insets(5,5,5,5);
		gc.anchor = GridBagConstraints.WEST;

		if (!isClaiMaker) {
			oNameLabel = new JLabel(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIConnectionDialog.userName")+": "); //$NON-NLS-1$
			grid.setConstraints(oNameLabel, gc);

			oNameField = new JTextField(sUsername);
			oNameField.setColumns(20);
			gc.gridwidth = GridBagConstraints.REMAINDER;
			grid.setConstraints(oNameField, gc);

			gc = new GridBagConstraints();
			gc.insets = new Insets(5,5,5,5);
			gc.anchor = GridBagConstraints.WEST;

			oPasswordLabel = new JLabel(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIConnectionDialog.password")+": "); //$NON-NLS-1$
			grid.setConstraints(oPasswordLabel, gc);

			oPasswordField = new JPasswordField(sPassword);
			oPasswordField.setColumns(20);
			gc.gridwidth = GridBagConstraints.REMAINDER;
			grid.setConstraints(oPasswordField, gc);

			gc = new GridBagConstraints();
			gc.insets = new Insets(5,5,5,5);
			gc.anchor = GridBagConstraints.WEST;

			oTagNameLabel = new JLabel(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIConnectionDialog.sendName")+": "); //$NON-NLS-1$
			grid.setConstraints(oTagNameLabel, gc);

			oTagNameField = new JTextField(sTagName);
			oTagNameField.setColumns(30);
			gc.gridwidth = GridBagConstraints.REMAINDER;
			grid.setConstraints(oTagNameField, gc);

			oDetailsPanel.add(oServerLabel);
			oDetailsPanel.add(oServerField);
			oDetailsPanel.add(oNameLabel);
			oDetailsPanel.add(oNameField);
			oDetailsPanel.add(oPasswordLabel);
			oDetailsPanel.add(oPasswordField);
			oDetailsPanel.add(oTagNameLabel);
			oDetailsPanel.add(oTagNameField);
		}
		else {
			oDetailsPanel.add(oServerLabel);
			oDetailsPanel.add(oServerField);
		}

		oButtonPanel = new UIButtonPanel();

		pbConnect = new UIButton(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIConnectionDialog.connectButton")); //$NON-NLS-1$
		pbConnect.setMnemonic(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIConnectionDialog.connectButtonMnemonic").charAt(0)); //$NON-NLS-1$);
		pbConnect.addActionListener(this);
		oButtonPanel.addButton(pbConnect);

		pbDisConnect = new UIButton(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIConnectionDialog.disconnectButton")); //$NON-NLS-1$
		pbDisConnect.setMnemonic(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIConnectionDialog.disconnectButtonMnemonic").charAt(0)); //$NON-NLS-1$);
		pbDisConnect.addActionListener(this);
		oButtonPanel.addButton(pbDisConnect);

		pbClose = new UIButton(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIConnectionDialog.closeButton")); //$NON-NLS-1$
		pbClose.setMnemonic(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIConnectionDialog.closeButtonMnemonic").charAt(0)); //$NON-NLS-1$);
		pbClose.addActionListener(this);
		oButtonPanel.addButton(pbClose);

		pbHelp = new UIButton(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIConnectionDialog.helpButton")); //$NON-NLS-1$
		pbHelp.setMnemonic(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIConnectionDialog.helpButtonMnemonic").charAt(0)); //$NON-NLS-1$);
		oButtonPanel.addHelpButton(pbHelp);

		if (isIX) {
			ProjectCompendium.APP.mainHB.enableHelpOnButton(pbHelp, "connections.ixpanels", ProjectCompendium.APP.mainHS); //$NON-NLS-1$
			if (ProjectCompendium.APP.isIXConnected()) {
				pbConnect.setEnabled(false);
				getRootPane().setDefaultButton(pbDisConnect);
			}
			else {
				pbDisConnect.setEnabled(false);
				getRootPane().setDefaultButton(pbConnect);
			}
		}
		else if (isJabber) {
			ProjectCompendium.APP.mainHB.enableHelpOnButton(pbHelp, "connections.jabber", ProjectCompendium.APP.mainHS); //$NON-NLS-1$
			if (ProjectCompendium.APP.isJabberConnected()) {
				pbConnect.setEnabled(false);
				getRootPane().setDefaultButton(pbDisConnect);
			}
			else {
				pbDisConnect.setEnabled(false);
				getRootPane().setDefaultButton(pbConnect);
			}
		}
		else {
			ProjectCompendium.APP.mainHB.enableHelpOnButton(pbHelp, "connections.claimaker", ProjectCompendium.APP.mainHS); //$NON-NLS-1$
			if (ProjectCompendium.APP.isClaiMakerConnected()) {
				pbConnect.setEnabled(false);
				getRootPane().setDefaultButton(pbDisConnect);
			}
			else {
				pbDisConnect.setEnabled(false);
				getRootPane().setDefaultButton(pbConnect);
			}
		}


		//oContentPane.setBorder(new EmptyBorder(10,10,10,10));
		oContentPane.add(oDetailsPanel, BorderLayout.CENTER);
		oContentPane.add(oButtonPanel, BorderLayout.SOUTH);

		pack();

		setResizable(false);
	}

	/**
	 * Handle action events coming from the buttons.
	 * @param evt, the associated ActionEvent.
	 */
	public void actionPerformed(ActionEvent evt) {

		Object source = evt.getSource();
		if (source instanceof JButton) {

			if (source == pbConnect)
				onConnect();
			else if (source == pbDisConnect)
				onDisconnect();
			else if (source == pbClose)
				onCancel();
		}
	}

	/**
	 * Establish a connection of the current type with the entered information.
	 */
	public void onConnect() {

		if (isClaiMaker) {
			String server = oServerField.getText();
			if ( !server.equals("") ) { //$NON-NLS-1$
				ProjectCompendium.APP.openClaiMakerConnection(server);
				onCancel();
			}
			else {
				ProjectCompendium.APP.displayError(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIConnectionDialog.errorServerDetails")); //$NON-NLS-1$
			}
		}
		else {
			String server = oServerField.getText();
			String username = oNameField.getText();
			String password = new String(oPasswordField.getPassword());

			if ( (!server.equals("") && !username.equals("") && !password.equals("")) ) { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				if (isIX)
					ProjectCompendium.APP.openIXPanelConnection(server, username, password);
				else if (isJabber)
					ProjectCompendium.APP.openJabberConnection(server, username, password);

				onCancel();
			}
			else {
				ProjectCompendium.APP.displayError(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIConnectionDialog.errorMissingData")); //$NON-NLS-1$
			}
		}
	}

	/**
	 * Disconnect from the current connection.
	 */
	public void onDisconnect() {

		if (isIX)
			ProjectCompendium.APP.closeIXPanelConnection();
		else if (isJabber)
			ProjectCompendium.APP.closeJabberConnection();
		else
			ProjectCompendium.APP.closeClaiMakerConnection();

		onCancel();
	}

	/**
	 * Load the previously stored connection data.
	 */
	private void loadProperties() {

		File optionsFile = new File("System"+ProjectCompendium.sFS+"resources"+ProjectCompendium.sFS+"Connection.properties"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		connectionProperties = new Properties();

		if (optionsFile.exists()) {
			try {
				connectionProperties.load(new FileInputStream("System"+ProjectCompendium.sFS+"resources"+ProjectCompendium.sFS+"Connection.properties")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

				if (isIX) {
					String value = connectionProperties.getProperty("ixserver"); //$NON-NLS-1$
					if (value != null)
						sServer = value;

					value = connectionProperties.getProperty("ixusername"); //$NON-NLS-1$
					if (value != null)
						sUsername = value;

					value = connectionProperties.getProperty("ixpassword"); //$NON-NLS-1$
					if (value != null)
						sPassword = value;

					value = connectionProperties.getProperty("ixtagname"); //$NON-NLS-1$
					if (value != null)
						sTagName = value;
				}
				else if (isJabber) {

					String value = connectionProperties.getProperty("jabberserver"); //$NON-NLS-1$
					if (value != null)
						sServer = value;

					value = connectionProperties.getProperty("jabberusername"); //$NON-NLS-1$
					if (value != null)
						sUsername = value;

					value = connectionProperties.getProperty("jabberpassword"); //$NON-NLS-1$
					if (value != null)
						sPassword = value;

					value = connectionProperties.getProperty("jabbertagname"); //$NON-NLS-1$
					if (value != null)
						sTagName = value;
				}
				else {
					String value = connectionProperties.getProperty("claimakerserver"); //$NON-NLS-1$
					if (value != null)
						sServer = value;
				}
			}
			catch (IOException e) {}
		}
	}

	/**
	 * Handle the close action. Stored the currently entered data and closes the dialog.
	 */
	public void onCancel() {

		setVisible(false);

		try {
			if (isIX) {
				if (!(oServerField.getText()).equals("")) //$NON-NLS-1$
					connectionProperties.put("ixserver", oServerField.getText()); //$NON-NLS-1$
				if (!(oNameField.getText()).equals("")) //$NON-NLS-1$
					connectionProperties.put("ixusername", oNameField.getText()); //$NON-NLS-1$
				if (!(new String(oPasswordField.getPassword())).equals("")) //$NON-NLS-1$
					connectionProperties.put("ixpassword", new String(oPasswordField.getPassword())); //$NON-NLS-1$
				if (!(oTagNameField.getText()).equals("")) { //$NON-NLS-1$
					connectionProperties.put("ixtagname", oTagNameField.getText()); //$NON-NLS-1$
				}
			}
			else if (isJabber) {
				if (!(oServerField.getText()).equals("")) //$NON-NLS-1$
					connectionProperties.put("jabberserver", oServerField.getText()); //$NON-NLS-1$
				if (!(oNameField.getText()).equals("")) //$NON-NLS-1$
					connectionProperties.put("jabberusername", oNameField.getText()); //$NON-NLS-1$
				if (!(new String(oPasswordField.getPassword())).equals("")) //$NON-NLS-1$
					connectionProperties.put("jabberpassword", new String(oPasswordField.getPassword())); //$NON-NLS-1$
				if (!(oTagNameField.getText()).equals("")) { //$NON-NLS-1$
					connectionProperties.put("jabbertagname", oTagNameField.getText()); //$NON-NLS-1$
				}

			}
			else {
				if (!(oServerField.getText()).equals("")) //$NON-NLS-1$
					connectionProperties.put("claimakerserver", oServerField.getText()); //$NON-NLS-1$
			}
			connectionProperties.store(new FileOutputStream("System"+ProjectCompendium.sFS+"resources"+ProjectCompendium.sFS+"Connection.properties"), "Connection Details"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		}
		catch (IOException e) {
			ProjectCompendium.APP.displayError(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIConnectionDialog.errorSavingProperties")); //$NON-NLS-1$
		}

		dispose();
	}
}
