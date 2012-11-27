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
import com.compendium.io.xml.FlashMeetingXMLImport;
import com.compendium.ui.*;

/**
 * UIImportXMLDialog defines the import dialog, that allows
 * the user to import an XML file into a Project Compendium view.
 *
 * @author	Michelle Bachler
  */
public class UIImportFlashMeetingXMLDialog extends UIDialog implements ActionListener, IUIConstants {

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

	/** Include the keywords.*/
	private JCheckBox			cbIncludeKeywords 	= null;

	/** Include the playlist.*/
	private JCheckBox			cbIncludePlayList 	= null;

	/** Include the urls.*/
	private JCheckBox			cbIncludeURLs 	= null;

	/** Include the attendees.*/
	private JCheckBox			cbIncludeAttendees 	= null;

	/** Include the chats.*/
	private JCheckBox			cbIncludeChats 	= null;

	/** Include the whiteboard.*/
	private JCheckBox			cbIncludeWhiteboard 	= null;

	/** Include the annotations.*/
	private JCheckBox			cbIncludeAnnotations 	= null;

	/** Include the file data.*/
	private JCheckBox			cbIncludeFileData 	= null;

	/** Include the votes.*/
	private JCheckBox			cbIncludeVotes 	= null;
	

	
	/** Select to mark all nodes seen /unseen  on import.*/
	private JCheckBox			cbMarkSeen 	= null;

	/** The parent frame for this dialog.*/
	private JFrame				oParent 		= null;

	/** The XML file to import.*/
	private File 				file 			= null;
	
	/**
	 * Initializes and sets up the dialog.
	 * @param parent, the parent view for this doalog.
	 */
	public UIImportFlashMeetingXMLDialog(JFrame parent) {

	  	super(parent, true);
		oParent = parent;

		setTitle(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIImportFlashMeetingXMLDialog.importFlashMeetingXMLTitle")); //$NON-NLS-1$

		oContentPane = getContentPane();
		drawDialog();
	}

	/**
	 * Initializes and sets up the dialog.
	 * @param parent, the parent view for this doalog.
	 * @param file, the xml file to import.
	 */
	public UIImportFlashMeetingXMLDialog(JFrame parent, File file) {

	  	super(parent, true);
		oParent = parent;

		setTitle(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIImportFlashMeetingXMLDialog.importFlashMeetingXMLTitle")); //$NON-NLS-1$
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
		gc.anchor = GridBagConstraints.WEST;
		oCenterPanel.setLayout(gb);
		gc.insets = new Insets(5,5,5,5);
		
		int y = 0;
		
		cbIncludeKeywords = new JCheckBox(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIImportFlashMeetingXMLDialog.importKeywordData")); //$NON-NLS-1$
		cbIncludeKeywords.setSelected(true);
		cbIncludeKeywords.addActionListener(this);
		gc.gridy = y;
		y++;
		gb.setConstraints(cbIncludeKeywords, gc);
		oCenterPanel.add(cbIncludeKeywords);

		cbIncludePlayList = new JCheckBox(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIImportFlashMeetingXMLDialog.importPlayListData")); //$NON-NLS-1$
		cbIncludePlayList.setSelected(true);
		cbIncludePlayList.addActionListener(this);
		gc.gridy = y;
		y++;
		gb.setConstraints(cbIncludePlayList, gc);
		oCenterPanel.add(cbIncludePlayList);
		
		cbIncludeURLs = new JCheckBox(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIImportFlashMeetingXMLDialog.importURLData")); //$NON-NLS-1$
		cbIncludeURLs.setSelected(true);
		cbIncludeURLs.addActionListener(this);
		gc.gridy = y;
		y++;
		gb.setConstraints(cbIncludeURLs, gc);
		oCenterPanel.add(cbIncludeURLs);
		
		cbIncludeAttendees = new JCheckBox(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIImportFlashMeetingXMLDialog.importAttendeeData")); //$NON-NLS-1$
		cbIncludeAttendees.setSelected(true);
		cbIncludeAttendees.addActionListener(this);
		gc.gridy = y;
		y++;
		gb.setConstraints(cbIncludeAttendees, gc);
		oCenterPanel.add(cbIncludeAttendees);
		
		cbIncludeChats = new JCheckBox(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIImportFlashMeetingXMLDialog.imortChatData")); //$NON-NLS-1$
		cbIncludeChats.setSelected(true);
		cbIncludeChats.addActionListener(this);
		gc.gridy = y;
		y++;
		gb.setConstraints(cbIncludeChats, gc);
		oCenterPanel.add(cbIncludeChats);
		
		cbIncludeWhiteboard = new JCheckBox(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIImportFlashMeetingXMLDialog.importWhiteBoardData")); //$NON-NLS-1$
		cbIncludeWhiteboard.setSelected(true);
		cbIncludeWhiteboard.addActionListener(this);
		gc.gridy = y;
		y++;
		gb.setConstraints(cbIncludeWhiteboard, gc);
		oCenterPanel.add(cbIncludeWhiteboard);

		cbIncludeAnnotations = new JCheckBox(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIImportFlashMeetingXMLDialog.importAnnotationData")); //$NON-NLS-1$
		cbIncludeAnnotations.setSelected(true);
		cbIncludeAnnotations.addActionListener(this);
		gc.gridy = y;
		y++;
		gb.setConstraints(cbIncludeAnnotations, gc);
		oCenterPanel.add(cbIncludeAnnotations);

		cbIncludeFileData = new JCheckBox(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIImportFlashMeetingXMLDialog.importFileData")); //$NON-NLS-1$
		cbIncludeFileData.setSelected(true);
		cbIncludeFileData.addActionListener(this);
		gc.gridy = y;
		y++;
		gb.setConstraints(cbIncludeFileData, gc);
		oCenterPanel.add(cbIncludeFileData);

		cbIncludeVotes = new JCheckBox(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIImportFlashMeetingXMLDialog.importVotingData")); //$NON-NLS-1$
		cbIncludeVotes.setSelected(true);
		cbIncludeVotes.addActionListener(this);
		gc.gridy = y;
		y++;
		gb.setConstraints(cbIncludeVotes, gc);
		oCenterPanel.add(cbIncludeVotes);
	
		// Add spacer label
		JLabel spacer = new JLabel(" "); //$NON-NLS-1$
		gc.gridy = y;
		y++;
		gb.setConstraints(spacer, gc);
		oCenterPanel.add(spacer);

		//flag to mark seen/unseen on import
		cbMarkSeen = new JCheckBox(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIImportFlashMeetingXMLDialog.markSeen")); //$NON-NLS-1$
		cbMarkSeen.setSelected(true);
		cbMarkSeen.addActionListener(this);		
		gc.gridy = y;
		y++;
		gb.setConstraints(cbMarkSeen, gc);
		oCenterPanel.add(cbMarkSeen);
		
		// Add spacer label
		spacer = new JLabel(" "); //$NON-NLS-1$
		gc.gridy = y;
		y++;
		gb.setConstraints(spacer, gc);
		oCenterPanel.add(spacer);

		gc.gridwidth=1;

		UIButtonPanel oButtonPanel = new UIButtonPanel();

		pbImport = new UIButton(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIImportFlashMeetingXMLDialog.importMainButton")); //$NON-NLS-1$
		pbImport.setMnemonic(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIImportFlashMeetingXMLDialog.importMainButtonMnemonic").charAt(0));
		pbImport.addActionListener(this);
		getRootPane().setDefaultButton(pbImport);
		oButtonPanel.addButton(pbImport);

		pbClose = new UIButton(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIImportFlashMeetingXMLDialog.cancelButton")); //$NON-NLS-1$
		pbClose.setMnemonic(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIImportFlashMeetingXMLDialog.cancelButtonMnemonic").charAt(0));
		pbClose.addActionListener(this);
		oButtonPanel.addButton(pbClose);

		pbHelp = new UIButton(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIImportFlashMeetingXMLDialog.helpButton")); //$NON-NLS-1$
		pbHelp.setMnemonic(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIImportFlashMeetingXMLDialog.helpButtonMnemonic").charAt(0));
		ProjectCompendium.APP.mainHB.enableHelpOnButton(pbHelp, "io.import_flashmeeting_xml", ProjectCompendium.APP.mainHS); //$NON-NLS-1$
		oButtonPanel.addHelpButton(pbHelp);

		// other initializations
		oContentPane.setLayout(new BorderLayout());
		oContentPane.add(oCenterPanel, BorderLayout.CENTER);
		oContentPane.add(oButtonPanel, BorderLayout.SOUTH);

		pack();
		setResizable(false);
		return;
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
	}

	/**
	 * Handle the import action request.
	 */
	public void onImport()  {
		
		String finalFile = ""; //$NON-NLS-1$

		if (file == null) {
			UIFileFilter filter = new UIFileFilter(new String[] {"xml"}, "XML Files"); //$NON-NLS-1$ //$NON-NLS-2$

			UIFileChooser fileDialog = new UIFileChooser();
			fileDialog.setDialogTitle(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIImportFlashMeetingXMLDialog.chooseFile2")); //$NON-NLS-1$
			fileDialog.setFileFilter(filter);
			fileDialog.setApproveButtonText(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIImportFlashMeetingXMLDialog.importButton")); //$NON-NLS-1$
			fileDialog.setRequiredExtension(".xml"); //$NON-NLS-1$

		    // FIX FOR MAC - NEEDS '/' ON END TO DENOTE A FOLDER
			if (!UIImportFlashMeetingXMLDialog.lastFileDialogDir.equals("")) { //$NON-NLS-1$
				File file = new File(UIImportFlashMeetingXMLDialog.lastFileDialogDir+ProjectCompendium.sFS);
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
						UIImportFlashMeetingXMLDialog.lastFileDialogDir = dir;
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
				Vector choices = new Vector();
				
				if (cbIncludeKeywords.isSelected()) {
					choices.addElement(FlashMeetingXMLImport.KEYWORDS_LABEL);
				}
				if (cbIncludeAttendees.isSelected()) {
					choices.addElement(FlashMeetingXMLImport.ATTENDEE_LABEL);
				}
				if (cbIncludePlayList.isSelected()) {
					choices.addElement(FlashMeetingXMLImport.PLAYLIST_LABEL);
				}
				if (cbIncludeURLs.isSelected()) {
					choices.addElement(FlashMeetingXMLImport.URL_LABEL);
				}
				if (cbIncludeChats.isSelected()) {
					choices.addElement(FlashMeetingXMLImport.CHAT_LABEL);
				}
				if (cbIncludeWhiteboard.isSelected()) {
					choices.addElement(FlashMeetingXMLImport.WHITEBOARD_LABEL);
				}
				if (cbIncludeFileData.isSelected()) {
					choices.addElement(FlashMeetingXMLImport.FILEDATA_LABEL);
				}
				if (cbIncludeAnnotations.isSelected()) {
					choices.addElement(FlashMeetingXMLImport.ANNOTATIONS_LABEL);
				}
				if (cbIncludeVotes.isSelected()) {
					choices.addElement(FlashMeetingXMLImport.VOTING_LABEL);
				}

				DBNode.setNodesMarkedSeen(cbMarkSeen.isSelected());

				FlashMeetingXMLImport xmlImport = new FlashMeetingXMLImport(finalFile, ProjectCompendium.APP.getModel(), choices);
				xmlImport.start();	

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