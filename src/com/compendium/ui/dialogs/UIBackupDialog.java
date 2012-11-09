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
import java.io.*;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.table.*;
import javax.swing.border.*;

import com.compendium.core.ICoreConstants;
import com.compendium.core.datamodel.*;
import com.compendium.core.db.*;

import com.compendium.LanguageProperties;
import com.compendium.ProjectCompendium;
import com.compendium.io.html.*;
import com.compendium.ui.plaf.*;
import com.compendium.ui.*;

/**
 * UIBackupDialog defines the dialog, that allows the user to backup thier dataase.
 *
 * @author	Michelle Bachler
 */
public class UIBackupDialog extends UIDialog implements ActionListener, IUIConstants {

	/** The current pane to put the dialog contents in.*/
	private Container				oContentPane = null;

	/** The button to activate the backup.*/
	private UIButton				pbBackup = null;

	/** The button to close this dialog without backing up.*/
	private UIButton				pbClose	 = null;

	/** The button to open the relevant help.*/
	private UIButton				pbHelp	 = null;

	/** Indicates if a plain sql backup should be run.*/
	private JRadioButton 			rbPlain = null;

	/** Indicates that a backup to zip file with references should be run.*/
	private JRadioButton 			rbToZip = null;

	/** Indicates if Templates files should be included **/
	private JCheckBox				cbTemplates = null;
	
	/** Indicates if Movie files should be included **/ 
	private JCheckBox				cbMovies = null;
	
	/** Indicates if deleted items should be included **/
	private JCheckBox				cbTrashbin = null;
	
	/**
	 * Indicates if the reference file paths should be preserved on export to zip,
	 * or changed to Linked Files folder.
	 */
	private JRadioButton 			rbKeepPaths = null;
	private JLabel					lblKeepPaths = null;

	/**
	 * reference file paths changed to Linked Files folder.
	 */
	private JRadioButton 			rbChangePaths = null;
	private JLabel					lblChangePaths = null;

	/** The layout manager used.*/
	private	GridBagLayout 			gb = null;

	/** The constraints used.*/
	private	GridBagConstraints 		gc = null;

	/** The parent frame for this dialog.*/
	private JFrame					oParent = null;

	/** The UIDatabaseManagementDialog, which is the dialog launching this dialog.*/
	private UIDatabaseManagementDialog dlg = null;

	/** The user friendly name for the database to backup.*/
	private String sFriendlyName = ""; //$NON-NLS-1$

	/** The real database name of the database to backup.*/
	private String sDatabaseName = ""; //$NON-NLS-1$

	/** Indicates if this dialog has been launched during another request that will then need resuming.*/
	private int nResume = -1;

	/** The counter for the gridbag layout y position.*/
	private int gridyStart = 0;

	private boolean bCancelAfter = false;

	/**
	 * Constructor. Initializes and sets up the dialog.
	 *
	 * @param parent, the rame that is the parent for this dialog.
	 * @param dlg, the dialog that launched this dialog and is responsible for it.
	 * @param sFriendlyName, the user givn name for the database being backed up.
	 * @param sDatabaseName, the system given name for the database being backed up.
	 * @param nResumeAction, Indicates if this dialog has been launched during another request that will then need resuming.
	 */
	public UIBackupDialog(JFrame parent, UIDatabaseManagementDialog dlg, String sFriendlyName, String sDatabaseName, int nResumeAction, boolean bCancelAfter ) {

		super(parent, true);
		oParent = parent;
		this.dlg = dlg;
		this.nResume = nResume;
		this.sDatabaseName = sDatabaseName;
		this.sFriendlyName = sFriendlyName;
		this.bCancelAfter = bCancelAfter;

		setTitle(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIBackupDialog.backupTitle")); //$NON-NLS-1$

		oContentPane = getContentPane();
		oContentPane.setLayout(new BorderLayout());

		drawDialog();

		pack();
		setResizable(false);
		return;
	}

	/**
	 * Draws the contents of this dialog.
	 */
	private void drawDialog() {

		gb = new GridBagLayout();
		JPanel oMainPanel = new JPanel(gb);

		GridBagConstraints gc = new GridBagConstraints();
		gc.insets = new Insets(5,5,5,5);
		gc.anchor = GridBagConstraints.WEST;

		rbPlain = new JRadioButton(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIBackupDialog.backupToSQL")); //$NON-NLS-1$
		rbPlain.setToolTipText(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIBackupDialog.backupToSQLTip")); //$NON-NLS-1$
		rbPlain.setSelected(true);
		gc.gridy = gridyStart;
		gridyStart++;
		gb.setConstraints(rbPlain, gc);
		oMainPanel.add(rbPlain);

		rbToZip = new JRadioButton(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIBackupDialog.backupWithRefs")); //$NON-NLS-1$
		rbToZip.setToolTipText(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIBackupDialog.backupWithRefsTip")); //$NON-NLS-1$
		rbToZip.addItemListener( new ItemListener() {
			public void itemStateChanged(java.awt.event.ItemEvent e) {
				if (rbToZip.isSelected()) {
					rbKeepPaths.setEnabled(true);
					rbChangePaths.setEnabled(true);
					lblKeepPaths.setEnabled(true);
					lblChangePaths.setEnabled(true);
					cbTemplates.setEnabled(true);
					cbMovies.setEnabled(true);
				}
				else {
					rbKeepPaths.setEnabled(false);
					rbChangePaths.setEnabled(false);
					lblKeepPaths.setEnabled(false);
					lblChangePaths.setEnabled(false);
					rbKeepPaths.setSelected(false);
					cbTemplates.setEnabled(false);
					cbMovies.setSelected(false);
					cbMovies.setEnabled(false);
				}
			}
		});

		gc.gridy = gridyStart;
		gridyStart++;
		gb.setConstraints(rbToZip, gc);
		oMainPanel.add(rbToZip);

		ButtonGroup rgGroup = new ButtonGroup();
		rgGroup.add(rbPlain);
		rgGroup.add(rbToZip);

		GridBagLayout gb2 = new GridBagLayout();
		JPanel oInnerPanel = new JPanel(gb2);
		oInnerPanel.setBorder(new EmptyBorder(0,20,0,0));
		int innergridyStart = 0;

		rbChangePaths = new JRadioButton(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIBackupDialog.zipInotLinkedFilesRadio")); //$NON-NLS-1$
		rbChangePaths.setEnabled(false);
		rbChangePaths.setSelected(true);
		gc.gridy = innergridyStart;
		innergridyStart++;
		gb2.setConstraints(rbChangePaths, gc);
		oInnerPanel.add(rbChangePaths);

		lblChangePaths = new JLabel(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIBackupDialog.changePathsLabel")); //$NON-NLS-1$
		lblChangePaths.setEnabled(false);
		gc.gridy = innergridyStart;
		innergridyStart++;
		gb2.setConstraints(lblChangePaths, gc);
		oInnerPanel.add(lblChangePaths);

		rbKeepPaths = new JRadioButton("\t\t"+LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIBackupDialog.leavePaths")); //$NON-NLS-1$
		rbKeepPaths.setEnabled(false);
		rbKeepPaths.setSelected(false);
		gc.gridy = innergridyStart;
		innergridyStart++;
		gb2.setConstraints(rbKeepPaths, gc);
		oInnerPanel.add(rbKeepPaths);

		lblKeepPaths = new JLabel("\t\t"+LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIBackupDialog.leavePathsLabel")); //$NON-NLS-1$
		lblKeepPaths.setEnabled(false);
		gc.gridy = innergridyStart;
		innergridyStart++;
		gb2.setConstraints(lblKeepPaths, gc);
		oInnerPanel.add(lblKeepPaths);

		cbMovies = new JCheckBox(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIBackupDialog.backupWithMovies"));//$NON-NLS-1$
		cbMovies.setEnabled(false);
		cbMovies.setSelected(false);
		gc.gridy = innergridyStart;
		innergridyStart++;
		gb2.setConstraints(cbMovies, gc);
		oInnerPanel.add(cbMovies);

		cbTemplates = new JCheckBox(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIBackupDialog.backupWithTemplates"));//$NON-NLS-1$
		cbTemplates.setEnabled(false);
		cbTemplates.setSelected(false);
		gc.gridy = innergridyStart;
		innergridyStart++;
		gb2.setConstraints(cbTemplates, gc);
		oInnerPanel.add(cbTemplates);


		gc.gridy = gridyStart;
		gridyStart++;
		gb.setConstraints(oInnerPanel, gc);
		oMainPanel.add(oInnerPanel);		
		
		cbTrashbin = new JCheckBox(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIBackupDialog.backupWithTrash"));//$NON-NLS-1$
		cbTrashbin.setEnabled(true);
		cbTrashbin.setSelected(false);
		gc.gridy = gridyStart;
		gridyStart++;
		gb.setConstraints(cbTrashbin, gc);
		oMainPanel.add(cbTrashbin);
		
		ButtonGroup rgGroup2 = new ButtonGroup();
		rgGroup2.add(rbKeepPaths);
		rgGroup2.add(rbChangePaths);

		UIButtonPanel oButtonPanel = new UIButtonPanel();

		pbBackup = new UIButton(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIBackupDialog.backupButton")); //$NON-NLS-1$
		pbBackup.addActionListener(this);
		pbBackup.setMnemonic(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIBackupDialog.backupButtonMnemonic").charAt(0));
		getRootPane().setDefaultButton(pbBackup); // If changes, change onEnter method too.
		oButtonPanel.addButton(pbBackup);

		pbClose = new UIButton(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIBackupDialog.cancelButton")); //$NON-NLS-1$
		pbClose.setMnemonic(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIBackupDialog.cancelButtonMnemonic").charAt(0));
		pbClose.addActionListener(this);
		oButtonPanel.addButton(pbClose);

		pbHelp = new UIButton(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIBackupDialog.helpButton")); //$NON-NLS-1$
		pbHelp.setMnemonic(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIBackupDialog.helpButtonMnemonic").charAt(0));
		ProjectCompendium.APP.mainHB.enableHelpOnButton(pbHelp, "basics.databases-backup", ProjectCompendium.APP.mainHS); //$NON-NLS-1$
		oButtonPanel.addHelpButton(pbHelp);

		oContentPane.add(oMainPanel, BorderLayout.CENTER);
		oContentPane.add(oButtonPanel, BorderLayout.SOUTH);
	}

	/**
	 * Handle action events coming from the buttons.
	 * @param evt, the associated ACtionEvent.
	 */
	public void actionPerformed(ActionEvent evt) {
		Object source = evt.getSource();

		// Handle button events
		if (source instanceof JButton) {
			if (source == pbBackup) {
				onBackup();
				onCancel();
			}
			else if (source == pbClose) {
				onCancel();
			}
		}
	}

	/**
	 * Process a backup request.
	 */
	public void onBackup()  {

		if (rbPlain.isSelected()) {
			dlg.onBackupPlain(sFriendlyName, sDatabaseName, nResume, bCancelAfter);
		}
		else if (rbToZip.isSelected() ) {
			dlg.onBackupZip(sFriendlyName, sDatabaseName, nResume, rbKeepPaths.isSelected(), bCancelAfter, cbMovies.isSelected(), cbTemplates.isSelected(), cbTrashbin.isSelected());
		}
	}
}
