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
import java.lang.*;
import java.io.*;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;
import javax.swing.table.*;

import com.compendium.core.datamodel.*;
import com.compendium.core.datamodel.services.*;

import com.compendium.LanguageProperties;
import com.compendium.ProjectCompendium;
import com.compendium.io.xml.*;
import com.compendium.ui.plaf.*;
import com.compendium.ui.*;
import com.compendium.ui.dialogs.*;


/**
 * UIExportXMLDialog defines the export dialog, that allows
 * the user to export PC Map/List Views to an XML document
 *
 * @author	Michelle Bachler
 */
public class UIExportXMLDialog extends UIDialog implements ActionListener, ItemListener {

	/** The default directory to export to.*/
	private static String		exportDirectory = ProjectCompendium.sHOMEPATH+ProjectCompendium.sFS+"Exports"; //$NON-NLS-1$

	/** The parent frame for this dialog.*/
	private JFrame				oParent			= null;

	/** The button to start the export.*/
	private UIButton			pbExport		= null;

	/** The button to close the dialog without exporting.*/
	private UIButton			pbClose			= null;

	/** The button to open the help.*/
	private UIButton			pbHelp			= null;


	/** Whether to export view to thier full depth.*/
	private JRadioButton		rbAllDepths 	= null;

	/** Whether to only export to the current map depth.*/
	private	JRadioButton		rbCurrentDepth 	= null;

	/** Whether to export all node on the current map.*/
	private JRadioButton		rbAllNodes 		= null;

	/** Whether to export only the selected nodes on the current map.*/
	private	JRadioButton		rbSelectedNodes = null;

	/** Whether to export to a zip file.*/
	private JCheckBox       	cbToZip			= null;

	/** Whether to export to a zip file with stencil and linkgroup data.*/
	private JCheckBox       	cbWithStencilsAndLinkGroups		= null;

	/** Whether to export meeting and media index data.*/
	private JCheckBox       	cbWithMeetings	= null;

	/** Indicates if Movie files should be included **/ 
	private JCheckBox				cbMovies = null;

	/** The file browser dialog to specify the export file.*/
	private	FileDialog			fdgExport 		= null;

	/** The view frame of the view being exported.*/
	private UIViewFrame 		uiViewFrame 	= null;

	/** Loaded export option properties.*/
	private Properties		optionsProperties = null;

	/** The depth to export to.*/
	private int				depth 				= 0;

	/** Holds whether to export to a zip file.*/
	private boolean 		bToZip 				= false;

	/** Indicates whether to export selected views only.*/
	private boolean			bSelectedViewsOnly 	= false;


	/**
	 * Initialize and draw the dialog.
	 * @param parent, the parent frame for this dialog.
	 */
	public UIExportXMLDialog(JFrame parent) {

		super(parent, true);

	  	oParent = parent;

	  	setTitle(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIExportXMLDialog.exportToXMlTitle")); //$NON-NLS-1$

		Container oContentPane = getContentPane();
		oContentPane.setLayout(new BorderLayout());

		JPanel oMainPanel = new JPanel();
		GridBagLayout gb = new GridBagLayout();
		GridBagConstraints gc = new GridBagConstraints();
		oMainPanel.setLayout(gb);

		gc.insets = new Insets(5,10,5,5);
		gc.anchor = GridBagConstraints.WEST;
		gc.gridwidth=2;

		int y=0;

		rbAllNodes = new JRadioButton(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIExportXMLDialog.allNodes")); //$NON-NLS-1$
		rbAllNodes.setSelected(true);
		rbAllNodes.addActionListener(this);
		gc.gridy = y;
		gc.weightx=1;
		y++;
		gb.setConstraints(rbAllNodes, gc);
		oMainPanel.add(rbAllNodes);

		rbSelectedNodes = new JRadioButton(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIExportXMLDialog.selectedNodes")); //$NON-NLS-1$
		rbSelectedNodes.setSelected(false);
		rbSelectedNodes.addActionListener(this);
		gc.gridy = y;
		gc.weightx=10;
		y++;
		gb.setConstraints(rbSelectedNodes, gc);
		oMainPanel.add(rbSelectedNodes);

		ButtonGroup group1 = new ButtonGroup();
		group1.add(rbAllNodes);
		group1.add(rbSelectedNodes);

		JSeparator sep = new JSeparator();
		gc.gridy = y;
		gc.weightx=11;
		y++;
		gc.fill = GridBagConstraints.HORIZONTAL;
		gb.setConstraints(sep, gc);
		oMainPanel.add(sep);
		gc.fill = GridBagConstraints.NONE;

		rbCurrentDepth = new JRadioButton(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIExportXMLDialog.currentDepth")); //$NON-NLS-1$
		rbCurrentDepth.setSelected(true);
		rbCurrentDepth.addActionListener(this);
		gc.gridy = y;
		gc.weightx=1;
		y++;
		gb.setConstraints(rbCurrentDepth, gc);
		oMainPanel.add(rbCurrentDepth);

		rbAllDepths = new JRadioButton(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIExportXMLDialog.fullDepth")); //$NON-NLS-1$
		rbAllDepths.setSelected(false);
		rbAllDepths.addActionListener(this);
		gc.gridy = y;
		gc.weightx=10;
		y++;
		gb.setConstraints(rbAllDepths, gc);
		oMainPanel.add(rbAllDepths);

		ButtonGroup rgGroup = new ButtonGroup();
		rgGroup.add(rbAllDepths);
		rgGroup.add(rbCurrentDepth);

		sep = new JSeparator();

		gc.gridy = y;
		gc.weightx=11;
		y++;
		gc.fill = GridBagConstraints.HORIZONTAL;
		gb.setConstraints(sep, gc);
		oMainPanel.add(sep);
		gc.fill = GridBagConstraints.NONE;

 		if (!FormatProperties.simpleInterface) {
	      	cbWithMeetings = new JCheckBox(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIExportXMLDialog.inlcludeMedaIndexes")); //$NON-NLS-1$
	      	cbWithMeetings.setSelected(false);
			gc.gridy = y;
			gc.weightx=5;
			y++;
			gb.setConstraints(cbWithMeetings, gc);
 	     	oMainPanel.add(cbWithMeetings);

			sep = new JSeparator();
		}
		
		gc.gridy = y;
		gc.weightx=11;
		y++;
		gc.fill = GridBagConstraints.HORIZONTAL;
		gb.setConstraints(sep, gc);
		oMainPanel.add(sep);
		gc.fill = GridBagConstraints.NONE;

      	cbToZip = new JCheckBox(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIExportXMLDialog.exportToZip")); //$NON-NLS-1$
      	cbToZip.addItemListener(this);
      	cbToZip.setSelected(false);
		gc.gridy = y;
		gc.weightx=10;
		y++;
		gb.setConstraints(cbToZip, gc);
      	oMainPanel.add(cbToZip);

		GridBagLayout gb2 = new GridBagLayout();
		JPanel oInnerPanel = new JPanel(gb2);
		oInnerPanel.setBorder(new EmptyBorder(0,20,0,0));
		int innergridyStart = 0;
      	
      	cbWithStencilsAndLinkGroups = new JCheckBox(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIExportXMLDialog.includeStencils")); //$NON-NLS-1$
      	cbWithStencilsAndLinkGroups.setEnabled(false);
      	cbWithStencilsAndLinkGroups.setSelected(false);
		gc.gridy = innergridyStart;
		gc.weightx=5;
		innergridyStart++;
		gb2.setConstraints(cbWithStencilsAndLinkGroups, gc);
		oInnerPanel.add(cbWithStencilsAndLinkGroups);

		cbMovies = new JCheckBox(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIBackupDialog.backupWithMovies"));//$NON-NLS-1$
		cbMovies.setEnabled(false);
		cbMovies.setSelected(false);
		gc.gridy = innergridyStart;
		gc.weightx=5;
		innergridyStart++;
		gb2.setConstraints(cbMovies, gc);
		oInnerPanel.add(cbMovies);

		gc.gridy = y;
		gc.weightx=5;
		y++;
		gb.setConstraints(oInnerPanel, gc);
		oMainPanel.add(oInnerPanel);
     	
      	
		gc.insets = new Insets(15,10,10,10);
		gc.weightx=1;

		oContentPane.add(oMainPanel, BorderLayout.CENTER);
		oContentPane.add(createButtonPanel(), BorderLayout.SOUTH);

		loadProperties();
		applyLoadedProperties();

		pack();

		setResizable(false);
		return;
	}

	private JPanel createButtonPanel() {

		UIButtonPanel oButtonPanel = new UIButtonPanel();

		pbExport = new UIButton(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIExportXMLDialog.exportButton")); //$NON-NLS-1$
		pbExport.setMnemonic(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIExportXMLDialog.exportButtonMnemonic").charAt(0));
		pbExport.addActionListener(this);
		getRootPane().setDefaultButton(pbExport);
		oButtonPanel.addButton(pbExport);

		pbClose = new UIButton(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIExportXMLDialog.closeButton")); //$NON-NLS-1$
		pbClose.setMnemonic(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIExportXMLDialog.closeButtonMenmonic").charAt(0));
		pbClose.addActionListener(this);
		oButtonPanel.addButton(pbClose);

		pbHelp = new UIButton(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIExportXMLDialog.helpButton")); //$NON-NLS-1$
		pbHelp.setMnemonic(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIExportXMLDialog.helpButtonMnemonic").charAt(0));
		ProjectCompendium.APP.mainHB.enableHelpOnButton(pbHelp, "io.export_xml", ProjectCompendium.APP.mainHS); //$NON-NLS-1$
		oButtonPanel.addHelpButton(pbHelp);

		return oButtonPanel;
	}

	/**
	 * Listener for checkbox changes.
	 * @param e, the associated ItemEvent object.
	 */
	public void itemStateChanged(ItemEvent e) {

		Object source = e.getItemSelectable();
		if (source == cbToZip) {
			if (cbToZip.isSelected()) {
				cbWithStencilsAndLinkGroups.setEnabled(true);
				cbMovies.setEnabled(true);
			}
			else {
				cbWithStencilsAndLinkGroups.setSelected(false);
				cbWithStencilsAndLinkGroups.setEnabled(false);
				cbMovies.setSelected(false);
				cbMovies.setEnabled(false);
			}
		}
		else if (source == rbAllDepths && rbAllDepths.isSelected()) {
			depth = 2;
		}
		else if (source == rbCurrentDepth && rbCurrentDepth.isSelected()) {
			depth = 0;
		}
		else if (source == rbAllNodes && rbAllNodes.isSelected()) {
			bSelectedViewsOnly = false;
		}
		else if (source == rbSelectedNodes && rbSelectedNodes.isSelected()) {
			bSelectedViewsOnly = true;
		}
	}

	/**
	 * Handle action events coming from the buttons.
	 * @param evt, the associated ActionEvent object.
	 */
	public void actionPerformed(ActionEvent evt) {

		Object source = evt.getSource();
		if (source instanceof JButton) {
			if (source == pbExport) {
				onExport();
				saveProperties();
			}
			else if (source == pbClose) {
				onCancel();
			}
		}
	}

	/**
	 * Set the current view frame to export.
	 * @param view com.compendium.ui.UIViewFrame, the current view to export.
	 */
	public void setCurrentView(UIViewFrame view) {
		uiViewFrame = view;
	}

	/**
	 * Handle the export action.
	 */
	public void onExport() {

		String fileName = ""; //$NON-NLS-1$
		String directory = ""; //$NON-NLS-1$
		boolean toZip = cbToZip.isSelected();
		if (toZip) {
			UIFileFilter filter = new UIFileFilter(new String[] {"zip"}, "ZIP Files"); //$NON-NLS-1$ //$NON-NLS-2$

			UIFileChooser fileDialog = new UIFileChooser();
			fileDialog.setDialogTitle(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIExportXMLDialog.enterFileName")); //$NON-NLS-1$
			fileDialog.setFileFilter(filter);
			fileDialog.setApproveButtonText(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIExportXMLDialog.saveButton")); //$NON-NLS-1$
			fileDialog.setApproveButtonMnemonic(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIExportXMLDialog.saveButtonMnemonic").charAt(0)); //$NON-NLS-1$
			fileDialog.setRequiredExtension(".zip"); //$NON-NLS-1$

		    // FIX FOR MAC - NEEDS '/' ON END TO DENOTE A FOLDER
		    File file = new File(exportDirectory+ProjectCompendium.sFS);
		    if (file.exists()) {
				fileDialog.setCurrentDirectory(file);
			}

			UIUtilities.centerComponent(fileDialog, ProjectCompendium.APP);
			int retval = fileDialog.showSaveDialog(ProjectCompendium.APP);
			if (retval == JFileChooser.APPROVE_OPTION) {
	        	if ((fileDialog.getSelectedFile()) != null) {

	            	fileName = fileDialog.getSelectedFile().getAbsolutePath();
					File fileDir = fileDialog.getCurrentDirectory();
					exportDirectory = fileDir.getPath();

					if (fileName != null) {
						if ( !fileName.toLowerCase().endsWith(".zip") ) { //$NON-NLS-1$
							fileName = fileName+".zip"; //$NON-NLS-1$
						}
						this.requestFocus();
						setCursor(new Cursor(Cursor.WAIT_CURSOR));

						setVisible(false);
						boolean selectedOnly = rbSelectedNodes.isSelected();
						boolean allDepths = rbAllDepths.isSelected();
						boolean withStencilsAndLinkGroups = cbWithStencilsAndLinkGroups.isSelected();
						boolean withMovies = cbMovies.isSelected();
						boolean withMeetings = false;
						if (cbWithMeetings != null) {
							withMeetings = cbWithMeetings.isSelected();
						}
						
						XMLExport export = new XMLExport(uiViewFrame, fileName, allDepths, selectedOnly, toZip, 
								withStencilsAndLinkGroups, withMovies, withMeetings, true);
						export.start();

						dispose();
					}
				}
			}
		}
		else {
			UIFileFilter filter = new UIFileFilter(new String[] {"xml"}, "XML Files"); //$NON-NLS-1$ //$NON-NLS-2$

			UIFileChooser fileDialog = new UIFileChooser();
			fileDialog.setDialogTitle(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIExportXMLDialog.enterFileName")); //$NON-NLS-1$
			fileDialog.setFileFilter(filter);
			fileDialog.setApproveButtonText(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIExportXMLDialog.saveButton")); //$NON-NLS-1$
			fileDialog.setRequiredExtension(".xml"); //$NON-NLS-1$

		    // FIX FOR MAC - NEEDS '/' ON END TO DENOTE A FOLDER
		    File file = new File(exportDirectory+ProjectCompendium.sFS);
		    if (file.exists()) {
				fileDialog.setCurrentDirectory(file);
			}

			UIUtilities.centerComponent(fileDialog, ProjectCompendium.APP);
			int retval = fileDialog.showSaveDialog(ProjectCompendium.APP);
			if (retval == JFileChooser.APPROVE_OPTION) {
	        	if ((fileDialog.getSelectedFile()) != null) {

	            	fileName = fileDialog.getSelectedFile().getAbsolutePath();
					File fileDir = fileDialog.getCurrentDirectory();
					exportDirectory = fileDir.getPath();

					if (fileName != null) {
						if ( !fileName.toLowerCase().endsWith(".xml") ) { //$NON-NLS-1$
							fileName = fileName+".xml"; //$NON-NLS-1$
						}
						this.requestFocus();
						setCursor(new Cursor(Cursor.WAIT_CURSOR));

						setVisible(false);
						boolean selectedOnly = rbSelectedNodes.isSelected();
						boolean allDepths = rbAllDepths.isSelected();
						boolean withStencilsAndLinkGroups = cbWithStencilsAndLinkGroups.isSelected();
						boolean withMovies = false;
						boolean withMeetings = false;
						if (cbWithMeetings != null) {
							withMeetings = cbWithMeetings.isSelected();
						}
						XMLExport export = new XMLExport(uiViewFrame, fileName, allDepths, selectedOnly, toZip, withStencilsAndLinkGroups, withMovies, withMeetings, true);
						export.start();

						dispose();
					}
				}
			}
		}
	}
	
	/**
	 * Apply the loaded export properties to the interface elements.
	 */
	private void applyLoadedProperties() {

		if (depth == 2) {
			rbAllDepths.setSelected(true);
		}
		else if (depth == 1) {
			rbCurrentDepth.setSelected(true);
		}
		else {
			rbCurrentDepth.setSelected(true);
		}

		cbToZip.setSelected(bToZip);

		rbSelectedNodes.setSelected(bSelectedViewsOnly);		
		if (!bSelectedViewsOnly)
			rbAllNodes.setSelected(true);	
	}	
	
	/**
	 * Load the saved properties for exporting.
	 */
	private void loadProperties() {

		File optionsFile = new File(UIExportViewDialog.EXPORT_OPTIONS_FILE_NAME);
		optionsProperties = new Properties();
		if (optionsFile.exists()) {
			try {
				optionsProperties.load(new FileInputStream(UIExportViewDialog.EXPORT_OPTIONS_FILE_NAME));

				String value = optionsProperties.getProperty("zip"); //$NON-NLS-1$
				if (value != null) {
					if (value.toLowerCase().equals("yes")) { //$NON-NLS-1$
						bToZip = true;
					} else {
						bToZip = false;
					}
				}

				value = optionsProperties.getProperty("depth"); //$NON-NLS-1$
				if (value != null) {
					if (value.equals("1")) { //$NON-NLS-1$
						depth = 1;
					} else if (value.equals("2")) { //$NON-NLS-1$
						depth = 2;
					} else {
						depth = 0;
					}
				}

				value = optionsProperties.getProperty("selectedviewsonly"); //$NON-NLS-1$
				if (value != null) {
					if (value.toLowerCase().equals("yes")) { //$NON-NLS-1$
						bSelectedViewsOnly = true;
					}
					else {
						bSelectedViewsOnly = false;
					}
				}				
			} catch (IOException e) {
				ProjectCompendium.APP.displayError("Error reading export options properties. Default values will be used"); //$NON-NLS-1$
			}
		}
	}
	
	/**
	 * Save Properties.
	 */
	private void saveProperties() {
		try {
			if (bToZip == true) {
				optionsProperties.put("zip", "yes"); //$NON-NLS-1$ //$NON-NLS-2$
			} else {
				optionsProperties.put("zip", "no"); //$NON-NLS-1$ //$NON-NLS-2$
			}

			if (depth == 2) {
				optionsProperties.put("depth", "2"); //$NON-NLS-1$ //$NON-NLS-2$
			} else if (depth == 1) {
				optionsProperties.put("depth", "1"); //$NON-NLS-1$ //$NON-NLS-2$
			} else {
				optionsProperties.put("depth", "0"); //$NON-NLS-1$ //$NON-NLS-2$
			}

			if (bSelectedViewsOnly == true) {
				optionsProperties.put("selectedviewsonly", "yes"); //$NON-NLS-1$ //$NON-NLS-2$
			} else {
				optionsProperties.put("selectedviewsonly", "no"); //$NON-NLS-1$ //$NON-NLS-2$
			}

			optionsProperties.store(new FileOutputStream(UIExportViewDialog.EXPORT_OPTIONS_FILE_NAME), LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIExportViewDialog.exportOptions")); //$NON-NLS-1$
		}
		catch (IOException e) {
			ProjectCompendium.APP.displayError(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIExportViewDialog.ioError")); //$NON-NLS-1$
		}
	}
	
	/**
	 * Handle the close action. Save settings and close the export dialog.
	 */
	public void onCancel() {

		setVisible(false);
		ProjectCompendium.APP.setDefaultCursor();

		dispose();
	}
}
