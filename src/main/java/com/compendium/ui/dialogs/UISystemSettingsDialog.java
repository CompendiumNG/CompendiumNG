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

import java.util.Properties;

import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Insets;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import java.io.IOException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JFrame;
import javax.swing.JButton;

import com.compendium.LanguageProperties;
import com.compendium.ProjectCompendium;
import com.compendium.ui.UIButton;
import com.compendium.ui.UIButtonPanel;
import com.compendium.ui.dialogs.UIDialog;

/**
 * UISystemSettingsDialog defines the dialog to enter Proxy setting for your current machine. *
 * @author	Michelle Bachler
 */
public class UISystemSettingsDialog extends UIDialog implements ActionListener {

	/** The file holding the data.*/
	public final static String		SETUPFILE = "System"+ProjectCompendium.sFS+"resources"+ProjectCompendium.sFS+"AccessGrid.properties"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

	/** The button to save data.*/
	private UIButton				pbSave		= null;

	/** The button to close the dialog without saving.*/
	private UIButton				pbCancel	= null;

	/** The button is used to open the relevant help.*/
	private UIButton				pbHelp 	= null;

	/** The panel with the labels and textfield in.*/
	private JPanel					oDetailsPanel = null;

	/** The panel with the buttons in.*/
	private JPanel					oButtonPanel = null;

	/** The field for the local proxy host data.*/
	private JTextField				txtLocalProxyHostField = null;

	/** The field for the local proxy port data.*/
	private JTextField				txtLocalProxyPortField = null;

	/** The local proxy host data.*/
	private String 					sLocalProxyHost = ""; //$NON-NLS-1$

	/** The local proxy port data.*/
	private String 					sLocalProxyPort = ""; //$NON-NLS-1$

	/** This holds the previously stored access grid connection details, if any.*/
	private Properties				oConnectionProperties = null;

	/**
	 * Initializes and sets up the dialog.
	 * @param parent, the parent frame for this dialog.
	 * @param sType, the type of connection dialog to draw.
	 */
	public UISystemSettingsDialog(JFrame parent) {

		super(parent, true);

	  	this.setTitle(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UISystemSettingsDialog.title")); //$NON-NLS-1$

		Container oContentPane = getContentPane();
		oContentPane.setLayout(new BorderLayout());

		loadProperties();

		oDetailsPanel = new JPanel();
		oDetailsPanel.setBorder(new EmptyBorder(10,10,10,10));
		GridBagLayout oGridBagLayout = new GridBagLayout();
		oDetailsPanel.setLayout(oGridBagLayout);

		GridBagConstraints gc = new GridBagConstraints();
		gc.insets = new Insets(5,5,5,5);
		gc.anchor = GridBagConstraints.WEST;
		gc.gridwidth = GridBagConstraints.REMAINDER;

		JLabel oLabel = new JLabel(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UISystemSettingsDialog.message1a")); //$NON-NLS-1$
		oGridBagLayout.setConstraints(oLabel, gc);
		oDetailsPanel.add(oLabel);
		oLabel = new JLabel(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UISystemSettingsDialog.message1b")); //$NON-NLS-1$
		oGridBagLayout.setConstraints(oLabel, gc);
		oDetailsPanel.add(oLabel);
		oLabel = new JLabel(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UISystemSettingsDialog.message1c")); //$NON-NLS-1$
		oGridBagLayout.setConstraints(oLabel, gc);
		oDetailsPanel.add(oLabel);
		oLabel = new JLabel("("+LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UISystemSettingsDialog.message1d")+"):"); //$NON-NLS-1$
		oGridBagLayout.setConstraints(oLabel, gc);
		oDetailsPanel.add(oLabel);

		oLabel = new JLabel(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UISystemSettingsDialog.proxyAddress")+": "); //$NON-NLS-1$
		gc.gridwidth = 1;
		oGridBagLayout.setConstraints(oLabel, gc);
		oDetailsPanel.add(oLabel);

		txtLocalProxyHostField = new JTextField(sLocalProxyHost);
		txtLocalProxyHostField.setColumns(30);
		gc.gridwidth = GridBagConstraints.REMAINDER;
		oGridBagLayout.setConstraints(txtLocalProxyHostField, gc);
		oDetailsPanel.add(txtLocalProxyHostField);

		oLabel = new JLabel(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UISystemSettingsDialog.proxyPort")+": "); //$NON-NLS-1$
		gc.gridwidth = 1;
		oGridBagLayout.setConstraints(oLabel, gc);
		oDetailsPanel.add(oLabel);

		txtLocalProxyPortField = new JTextField(sLocalProxyPort);
		txtLocalProxyPortField.setColumns(6);
		gc.gridwidth = GridBagConstraints.REMAINDER;
		oGridBagLayout.setConstraints(txtLocalProxyPortField, gc);
		oDetailsPanel.add(txtLocalProxyPortField);

		oContentPane.add(oDetailsPanel, BorderLayout.CENTER);
		oContentPane.add(createButtonPanel(), BorderLayout.SOUTH);

		pack();

		setResizable(false);
	}

	/**
	 * Create the panel with the buttons for this dialog.
	 * @return a JPanel holding the buttons for this dialog.
	 */
    private UIButtonPanel createButtonPanel() {

		UIButtonPanel oButtonPanel = new UIButtonPanel();

		pbSave = new UIButton(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UISystemSettingsDialog.saveButton")); //$NON-NLS-1$
		pbSave.setMnemonic(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UISystemSettingsDialog.saveButtonMnemonic").charAt(0)); //$NON-NLS-1$
		pbSave.addActionListener(this);
		getRootPane().setDefaultButton(pbSave);
		oButtonPanel.addButton(pbSave);

		pbCancel = new UIButton(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UISystemSettingsDialog.cancelButton")); //$NON-NLS-1$
		pbCancel.setMnemonic(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UISystemSettingsDialog.cancelButtonMnemonic").charAt(0)); //$NON-NLS-1$
		pbCancel.addActionListener(this);
		oButtonPanel.addButton(pbCancel);

		pbHelp = new UIButton(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UISystemSettingsDialog.helpButton")); //$NON-NLS-1$
		pbHelp.setMnemonic(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UISystemSettingsDialog.helpButtonMnemonic").charAt(0)); //$NON-NLS-1$
		ProjectCompendium.APP.mainHB.enableHelpOnButton(pbHelp, "basics.settings", ProjectCompendium.APP.mainHS); //$NON-NLS-1$
		oButtonPanel.addHelpButton(pbHelp);

		return oButtonPanel;
	}

	/**
	 * Handle action events coming from the buttons.
	 * @param evt the associated ActionEvent.
	 */
	public void actionPerformed(ActionEvent evt) {

		Object source = evt.getSource();
		if (source instanceof JButton) {

			if (source == pbSave) {
				onSave();
			} else if (source == pbCancel) {
				onCancel();
			}
		}
	}

	/**
	 * Save the Access Grid data to a property file.
	 */
	public void onSave() {

		try {
			String sNewLocalProxyHost = txtLocalProxyHostField.getText();
			String sNewLocalProxyPort = txtLocalProxyPortField.getText();

			boolean bChangedData = false;
			if (!sNewLocalProxyHost.equals("") && !sNewLocalProxyHost.equals(sLocalProxyHost)) { //$NON-NLS-1$
				oConnectionProperties.put("localproxyhost", sNewLocalProxyHost); //$NON-NLS-1$
				bChangedData = true;
			}

			if (!sNewLocalProxyPort.equals("") && !sNewLocalProxyPort.equals(sLocalProxyPort)) { //$NON-NLS-1$
				oConnectionProperties.put("localproxyport", sNewLocalProxyPort); //$NON-NLS-1$
				bChangedData = true;
			}

			if (bChangedData) {
				oConnectionProperties.store(new FileOutputStream(SETUPFILE), "Access Grid Details"); //$NON-NLS-1$

				System.setProperty("proxySet", "true"); //$NON-NLS-1$ //$NON-NLS-2$
				System.setProperty("http.proxyHost", sNewLocalProxyHost); //$NON-NLS-1$
				System.setProperty("http.proxyPort", sNewLocalProxyPort); //$NON-NLS-1$
			}
		} catch (IOException e) {
			ProjectCompendium.APP.displayError(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UISystemSettingsDialog.errorSavingData")+"\n\n"+e.getMessage()); //$NON-NLS-1$
		}

		onCancel();
	}

	/**
	 * Load the previously stored Access Grid data.
	 */
	private void loadProperties() {

		File optionsFile = new File(SETUPFILE);
		oConnectionProperties = new Properties();

		if (optionsFile.exists()) {
			try {
				oConnectionProperties.load(new FileInputStream(SETUPFILE));
				String value = oConnectionProperties.getProperty("localproxyhost"); //$NON-NLS-1$
				if (value != null) {
					sLocalProxyHost = value;
				}
				value = oConnectionProperties.getProperty("localproxyport"); //$NON-NLS-1$
				if (value != null) {
					sLocalProxyPort = value;
				}
			} catch (IOException e) {
				System.out.println("Unabloe to load System Settings data due to: "+e.getMessage()); //$NON-NLS-1$
			}
		}
	}
}
