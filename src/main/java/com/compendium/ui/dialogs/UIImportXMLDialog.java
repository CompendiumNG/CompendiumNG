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

import com.compendium.LanguageProperties;
import com.compendium.ProjectCompendium;
import com.compendium.core.db.*;

import com.compendium.ui.plaf.*;
import com.compendium.ui.*;

/**
 * UIImportXMLDialog defines the import dialog, that allows
 * the user to import an XML file into a Project Compendium view.
 *
 * @author	Michelle Bachler
  */
public class UIImportXMLDialog extends UIDialog implements ActionListener, IUIConstants {

	/** The last directory the user selected to import a file from.*/
	public static String 		lastFileDialogDir = ProjectCompendium.sHOMEPATH+ProjectCompendium.sFS+"Exports"; //$NON-NLS-1$

	/** The pane for the dialog's contents.*/
	private Container			oContentPane 	= null;

	/** The button to start the import.*/
	private UIButton			pbImport		= null;

	/** The button to close the dialog.*/
	private UIButton			pbClose			= null;

	/** The button to open the help.*/
	private UIButton			pbHelp			= null;

	/** Set date as todays date and author as current user on import.*/
	private JRadioButton		rbNormal		= null;

	/** Import author and date information.*/
	private JRadioButton		rbSmart		= null;

	/** Include the importing user an creationdate details in the detail field of the node.*/
	private JCheckBox			cbInclude 	= null;

	/** Treat import nodes with id already in the database as transclusions.*/
	private JCheckBox			cbTransclude 	= null;

	/** If an node already exists, update the node in the database.*/
	private JCheckBox			cbUpdateTrans	= null;

	/** Select to preserve node ids on import.*/
	private JCheckBox			cbPreserveID 	= null;
	
	/** Select to mark all nodes seen /unseen  on import.*/
	private JCheckBox			cbMarkSeen 	= null;

	/** The map to import into.*/
	private ViewPaneUI			oViewPaneUI 	= null;

	/** The list to import into.*/
	private UIList				uiList 			= null;

	/** The parent frame for this dialog.*/
	private JFrame				oParent 		= null;

	/** The XML file to import.*/
	private File 				file 			= null;
	
	/**
	 * Initializes and sets up the dialog.
	 * @param parent, the parent view for this doalog.
	 */
	public UIImportXMLDialog(JFrame parent) {

	  	super(parent, true);
		oParent = parent;

		setTitle(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIImportXMLDialog.importXMLTitle")); //$NON-NLS-1$

		oContentPane = getContentPane();
		drawDialog();
	}

	/**
	 * Initializes and sets up the dialog.
	 * @param parent, the parent view for this doalog.
	 * @param file, the xml file to import.
	 */
	public UIImportXMLDialog(JFrame parent, File file) {

	  	super(parent, true);
		oParent = parent;

		setTitle(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIImportXMLDialog.importXMLTitle")); //$NON-NLS-1$
		this.file = file;

		oContentPane = getContentPane();
		drawDialog();
	}

	/**
	 * Draw the contents of the dialog.
	 */
	private void drawDialog() {

		JPanel oCenterPanel = new JPanel();

		GridBagLayout gb = new GridBagLayout();
		GridBagConstraints gc = new GridBagConstraints();
		oCenterPanel.setLayout(gb);
		gc.insets = new Insets(5,5,5,5);

		//get the import profile form the main frame
		Vector profile = ProjectCompendium.APP.getImportProfile();
		boolean normalImport = ((Boolean)profile.elementAt(0)).booleanValue();
		boolean includeInDetail = ((Boolean)profile.elementAt(1)).booleanValue();
		boolean preserveIDs = ((Boolean)profile.elementAt(2)).booleanValue();
		boolean transclude = ((Boolean)profile.elementAt(3)).booleanValue();

		rbSmart = new JRadioButton(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIImportXMLDialog.importAuthorDate")); //$NON-NLS-1$
		rbSmart.setSelected(true);
		rbSmart.addActionListener(this);
		gc.anchor = GridBagConstraints.WEST;
		gc.gridwidth=GridBagConstraints.REMAINDER;
		gb.setConstraints(rbSmart, gc);
		oCenterPanel.add(rbSmart);

		rbNormal = new JRadioButton(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIImportXMLDialog.setAuthorAs")+ProjectCompendium.APP.getModel().getUserProfile().getUserName()+" "+LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIImportXMLDialog.allDateToday")); //$NON-NLS-1$ //$NON-NLS-2$
		rbNormal.setSelected(false);
		rbNormal.addActionListener(this);
		gc.insets = new Insets(5,5,0,5);
		gc.gridy = 1;
		gb.setConstraints(rbNormal, gc);
		oCenterPanel.add(rbNormal);

		ButtonGroup rgGroup = new ButtonGroup();
		rgGroup.add(rbNormal);
		rgGroup.add(rbSmart);

		cbInclude = new JCheckBox(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIImportXMLDialog.includeAuthorDate")); //$NON-NLS-1$
		cbInclude.setSelected(includeInDetail);
		cbInclude.addActionListener(this);
		cbInclude.setEnabled(false);
		gc.gridy = 2;
		gc.insets = new Insets(0,30,5,5);
		gb.setConstraints(cbInclude, gc);
		oCenterPanel.add(cbInclude);

		cbTransclude = new JCheckBox(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIImportXMLDialog.preserveEmbeds")); //$NON-NLS-1$
		cbTransclude.setSelected(transclude);
		cbTransclude.addActionListener(this);
		gc.insets = new Insets(5,5,5,5);
		gc.gridy = 3;
		gb.setConstraints(cbTransclude, gc);
		oCenterPanel.add(cbTransclude);

		cbUpdateTrans = new JCheckBox(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIImportXMLDialog.overwriteNodes")); //$NON-NLS-1$
		if (cbTransclude.isSelected())
			cbUpdateTrans.setEnabled(true);
		else
			cbUpdateTrans.setEnabled(false);
		gc.insets = new Insets(0,30,5,5);
		gc.gridy = 4;
		gb.setConstraints(cbUpdateTrans, gc);
		oCenterPanel.add(cbUpdateTrans);

		cbPreserveID = new JCheckBox(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIImportXMLDialog.preserveimportedIDs")); //$NON-NLS-1$
		cbPreserveID.setSelected(preserveIDs);
		if (preserveIDs)
			cbTransclude.setSelected (true);

		cbPreserveID.addActionListener(this);
		gc.insets = new Insets(5,5,5,5);
		gc.gridy = 5;
		gb.setConstraints(cbPreserveID, gc);
		oCenterPanel.add(cbPreserveID);
		
		//flag to mark seen/unseen on import
		cbMarkSeen = new JCheckBox(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIImportXMLDialog.markSeen")); //$NON-NLS-1$
		cbMarkSeen.setSelected(true);
		cbMarkSeen.addActionListener(this);
		
		gc.insets = new Insets(5,5,5,5);
		gc.gridy = 6;
		gb.setConstraints(cbMarkSeen, gc);
		oCenterPanel.add(cbMarkSeen);
		

		// Add spacer label
		JLabel spacer = new JLabel(" "); //$NON-NLS-1$
		gc.gridy = 7;
		gb.setConstraints(spacer, gc);
		oCenterPanel.add(spacer);

		gc.gridwidth=1;

		UIButtonPanel oButtonPanel = new UIButtonPanel();

		pbImport = new UIButton(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIImportXMLDialog.importXMLButton")); //$NON-NLS-1$
		pbImport.setMnemonic(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIImportXMLDialog.importXMLButtonMnemonic").charAt(0)); //$NON-NLS-1$
		pbImport.addActionListener(this);
		getRootPane().setDefaultButton(pbImport);
		oButtonPanel.addButton(pbImport);

		pbClose = new UIButton(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIImportXMLDialog.cancelButton")); //$NON-NLS-1$
		pbClose.setMnemonic(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIImportXMLDialog.cancelButtonMnemonic").charAt(0)); //$NON-NLS-1$
		pbClose.addActionListener(this);
		oButtonPanel.addButton(pbClose);

		pbHelp = new UIButton(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIImportXMLDialog.helpButton")); //$NON-NLS-1$
		pbHelp.setMnemonic(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIImportXMLDialog.helpButtonMnemonic").charAt(0)); //$NON-NLS-1$
		ProjectCompendium.APP.mainHB.enableHelpOnButton(pbHelp, "io.import_xml", ProjectCompendium.APP.mainHS); //$NON-NLS-1$
		oButtonPanel.addHelpButton(pbHelp);

		// other initializations
		oContentPane.setLayout(new BorderLayout());
		oContentPane.add(oCenterPanel, BorderLayout.CENTER);
		oContentPane.add(oButtonPanel, BorderLayout.SOUTH);

		pack();
		setResizable(false);
		return;
	}

	/**
	 * Set the ViewPaneUI when importing into a map.
	 * @param vpUI, ViewPaneUI to import into.
	 */
	public void setViewPaneUI(ViewPaneUI vpUI) {
		oViewPaneUI = vpUI;
	}

	/**
	 * Set the UIList when importing into a list.
	 * @param list, UIList to import into.
	 */
	public void setUIList(UIList list) {
		uiList = list;
	}


	/******* EVENT HANDLING METHODS *******/

	/**
	 * Handle the button push events.
	 * @param evt, the associated ActionEvent object.
	 */
	public void actionPerformed(ActionEvent evt) {

		Object source = evt.getSource();

		// Handle button events
		if (source instanceof JButton) {
			if (source == pbImport) {

				Thread thread = new Thread("UIImportXMLDialog: Import") { //$NON-NLS-1$
					public void run() {
						onImport();
					}
				};
				thread.run();

			} else
			if (source == pbClose) {
				onCancel();
			}
		}

		if (source.equals(cbTransclude)) {
			if (cbTransclude.isSelected()) {
				cbUpdateTrans.setEnabled(true);
			}
			else {
				cbUpdateTrans.setEnabled(false);
				cbUpdateTrans.setSelected(false);
				cbPreserveID.setSelected(false);
			}
		}
		else if (source.equals(cbPreserveID)) {
			if (cbPreserveID.isSelected()) {
				cbTransclude.setSelected(true);
				cbUpdateTrans.setEnabled(true);
			}
			else {
				cbTransclude.setSelected(false);
				cbUpdateTrans.setEnabled(false);
				cbUpdateTrans.setSelected(false);
			}
		}
		else if (source instanceof JRadioButton) {
			if (source == rbNormal && rbNormal.isSelected()) {
				cbInclude.setEnabled(true);
				cbInclude.setSelected(true);
			}
			else if (source == rbSmart && rbSmart.isSelected()) {
				cbInclude.setSelected(false);
				cbInclude.setEnabled(false);
			}
		}
	}

	/**
	 * Handle the import action request.
	 */
	public void onImport()  {

		//set the import profile
		boolean normalProfile 	= rbNormal.isSelected();
		boolean includeInDetail = cbInclude.isSelected();
		boolean preserveIDs 	= cbPreserveID.isSelected();
		boolean transclude 		= cbTransclude.isSelected();
		boolean markseen 		= cbMarkSeen.isSelected();
		
		ProjectCompendium.APP.setImportProfile(normalProfile, includeInDetail, preserveIDs, transclude);

		DBNode.setImportAsTranscluded(cbTransclude.isSelected());
		DBNode.setPreserveImportedIds(cbPreserveID.isSelected());
		DBNode.setUpdateTranscludedNodes(cbUpdateTrans.isSelected());
		DBNode.setNodesMarkedSeen(markseen);
		
		String finalFile = ""; //$NON-NLS-1$

		if (file == null) {
			UIFileFilter filter = new UIFileFilter(new String[] {"xml"}, LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIImportXMLDialog.xmlFileType")); //$NON-NLS-1$ //$NON-NLS-2$

			UIFileChooser fileDialog = new UIFileChooser();
			fileDialog.setDialogTitle(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIImportXMLDialog.chooseFile2")); //$NON-NLS-1$
			fileDialog.setFileFilter(filter);
			fileDialog.setApproveButtonText(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIImportXMLDialog.importButton")); //$NON-NLS-1$
			fileDialog.setRequiredExtension(".xml"); //$NON-NLS-1$

		    // FIX FOR MAC - NEEDS '/' ON END TO DENOTE A FOLDER
			if (!UIImportXMLDialog.lastFileDialogDir.equals("")) { //$NON-NLS-1$
				File file = new File(UIImportXMLDialog.lastFileDialogDir+ProjectCompendium.sFS);
				if (file.exists()) {
					fileDialog.setCurrentDirectory(file);
				}
			}

			UIUtilities.centerComponent(fileDialog, ProjectCompendium.APP);
			int retval = fileDialog.showOpenDialog(ProjectCompendium.APP);
			if (retval == JFileChooser.APPROVE_OPTION) {
	        	if ((fileDialog.getSelectedFile()) != null) {

	            	String fileName = fileDialog.getSelectedFile().getAbsolutePath();
					File fileDir = fileDialog.getCurrentDirectory();
					String dir = fileDir.getPath();

					if (fileName != null) {
						UIImportXMLDialog.lastFileDialogDir = dir;
						finalFile = fileName;
					}
				}
			}
  		}
		else {
			finalFile = file.getAbsolutePath();
		}

		if (finalFile != null) {
			if ((new File(finalFile)).exists()) {
				setVisible(false);

				if (oViewPaneUI != null) {
					oViewPaneUI.setSmartImport(rbSmart.isSelected());
					oViewPaneUI.onImportXMLFile(finalFile, includeInDetail);
				} else if (uiList != null) {
					uiList.getListUI().setSmartImport(rbSmart.isSelected());
					uiList.getListUI().onImportXMLFile(finalFile, includeInDetail);
				}

				dispose();
				ProjectCompendium.APP.setStatus(""); //$NON-NLS-1$
			}
  		}
	}

	/**
	 * Handle the close action. Closes the import dialog.
	 */
	public void onCancel() {
		DBNode.restoreImportSettings();
		setVisible(false);
		dispose();
	}
}
