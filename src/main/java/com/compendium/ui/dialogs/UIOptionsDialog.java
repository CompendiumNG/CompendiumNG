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
import javax.swing.event.*;
import javax.swing.border.*;

import com.compendium.*;
import com.compendium.core.*;
import com.compendium.ui.*;

/**
 * This class draws the options dialog and handles storing/setting the user's chosen options.
 *
 * @author	Michelle Bachler
 */
public class UIOptionsDialog extends UIDialog implements ActionListener, ItemListener {

	/** The choicebox with the zoom options.*/
	private JComboBox 		cbZoom					= null;

	/** The parent frame for this dialog.*/
	private Container		oParent					= null;

	/** The button to cancel this dialog.*/
	public	UIButton		pbCancel				= null;

	/** The button to update the user settings.*/
	public	UIButton		pbUpdate				= null;

	/** Activates the help opening to the appropriate section.*/
	private UIButton		pbHelp					= null;

	/** Turn audio feedback on.*/
	private JRadioButton	rbAudioOn				= null;

	/** Turn audio feedback off.*/
	private JRadioButton	rbAudioOff				= null;


	/** Drop files as link to original */
	private JRadioButton	rbDnDDropFileAsLink			= null;

	/** Drop files as copy of original */
	private JRadioButton	rbDnDDropFileAsCopy			= null;

	/** Always prompt whether to link or to copy files */
	private JRadioButton	rbDnDDropFilePrompt			= null;
	
	/** Copy dropped files to database */
	private JRadioButton	rbDnDCopyFileToDB			= null;
	
	/** Copy dropped files to special "Linked Files" folder */
	private JRadioButton	rbDnDCopyFileToFolder		= null;

	/** Panel with DnD copy options */
	private JPanel		 	oDnDCopyFilePanel			= null;
	
	/** Border for panel with DnD copy options */
	private TitledBorder 	oDnDCopyBorder				= null;
	
	/** Drop folders as link to original */
	private JRadioButton	rbDnDDropFolderAsLink		= null;

	/** Drop folders as map nodes with content */
	private JRadioButton	rbDnDDropFolderAsMap		= null;

	/** Drop folders recursively as map nodes with content */
	private JRadioButton	rbDnDDropFolderAsMapRecursively		= null;

	/** Always prompt whether to link folders or to add them as map nodes/recursively */
	private JRadioButton	rbDnDDropFolderPrompt		= null;
	
	/** Don't prompt if dropping text, just process as plain text. */
	private JCheckBox		cbDnDTextPrompt			= null;
	
	/** Should images rollover be scaled?*/
	private JCheckBox		rbImageRolloverScale 	= null;

	/** Should menu bar be at top of screen in a Mac OS?*/
	private JCheckBox		rbMenuPosition 	= null;

	/** Should menu shortcuts be displayed as underlining?*/
	private JCheckBox		rbMenuUnderline 	= null;

	/** Should an email be sent when something goes in your inbox. */
	private JCheckBox		rbInboxEmail		= null;

	/** Simple Interface enalbed or disabled.*/
	private JCheckBox		rbAdvancedInterface 	= null;
	
	/** Holds the detail rollover length.*/
	private JTextField		txtCursorMoveDistance = null;

	/** Holds the detail rollover length.*/
	private JTextField		txtDetailRolloverLength = null;


	/** Holds the detail rollover length.*/
	private JTextField		txtLeftVerticalGap = null;

	/** Holds the detail rollover length.*/
	private JTextField		txtLeftHorizontalGap = null;

	/** Holds the detail rollover length.*/
	private JTextField		txtTopVerticalGap = null;

	/** Holds the detail rollover length.*/
	private JTextField		txtTopHorizontalGap = null;

	/** The choicebox listing the current look and feels.*/
	private JComboBox		cbLandF			= null;

	/** The choicebox listing the current icons sets.*/
	private JComboBox		cbIconSets			= null;

	/** Use kfmclient to open external references?*/
	private JCheckBox		rbKFMClient			= null;

	/** Should single click for opening nodes be enabled?*/
	private JCheckBox		rbSingleClick 		= null;

	
	/** Holds the various panels with options.*/
	private JTabbedPane		TabbedPane			= null;

	/**
	 * Constructor.
	 * @param parent, the parent frame for this dialog.
	 */
	public UIOptionsDialog(JFrame parent) {

		super(parent, true);
		oParent = parent;

		Container oContentPane = getContentPane();
		oContentPane.setLayout(new BorderLayout());

		TabbedPane = new JTabbedPane();

		if (ProjectCompendium.isMac) {
			setTitle(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIOptionsDialog.userPreferences")); //$NON-NLS-1$
		}
		else {
			setTitle(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIOptionsDialog.userOptions")); //$NON-NLS-1$
		}

		TabbedPane.add(createDndFilesPanel(), LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIOptionsDialog.dndFiles")); //$NON-NLS-1$
		TabbedPane.add(createDndFolderPanel(), LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIOptionsDialog.dndFolders")); //$NON-NLS-1$
		TabbedPane.add(createRolloverPanel(), LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIOptionsDialog.mapAndRollover")); //$NON-NLS-1$
		TabbedPane.add(createOtherPanel(), LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIOptionsDialog.audioZoom")); //$NON-NLS-1$
		TabbedPane.add(createArrangePanel(), LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIOptionsDialog.arrange")); //$NON-NLS-1$
		TabbedPane.add(createMiscPanel(), LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIOptionsDialog.misc"));						 //$NON-NLS-1$

		JPanel buttonpanel = createButtonPanel();

		oContentPane.add(TabbedPane, BorderLayout.CENTER);
		oContentPane.add(buttonpanel, BorderLayout.SOUTH);

		pack();
		setResizable(false);
	}

	/**
	 * Create the panel with the audio and zoom options.
	 */
	public JPanel createOtherPanel() {

		JPanel panel = new JPanel();
		GridBagLayout gb = new GridBagLayout();
		panel.setLayout(gb);

		GridBagConstraints gc = new GridBagConstraints();
		gc.insets = new Insets(5,5,5,5);
		gc.anchor = GridBagConstraints.WEST;
		gc.gridwidth=1;

		JLabel lblAudio = new JLabel(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIOptionsDialog.audioFeedback")+":"); //$NON-NLS-1$
		gc.gridy = 0;
		gc.gridx = 0;
		gb.setConstraints(lblAudio, gc);
		panel.add(lblAudio);

		rbAudioOn = new JRadioButton(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIOptionsDialog.on")); //$NON-NLS-1$
		rbAudioOn.addActionListener(this);
		gc.gridy = 0;
		gc.gridx = 1;
		gc.anchor = GridBagConstraints.EAST;
		gb.setConstraints(rbAudioOn, gc);
		panel.add(rbAudioOn);

		gc.anchor = GridBagConstraints.WEST;

		rbAudioOff = new JRadioButton(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIOptionsDialog.off")); //$NON-NLS-1$
		rbAudioOff.addActionListener(this);
		gc.gridy = 0;
		gc.gridx = 2;
		gb.setConstraints(rbAudioOff, gc);
		panel.add(rbAudioOff);

		boolean audioOn = ProjectCompendium.APP.getAudioPlayer().getAudio();
		if(audioOn)
			rbAudioOn.setSelected(true);
		else
			rbAudioOff.setSelected(true);

		ButtonGroup rgGroup = new ButtonGroup();
		rgGroup.add(rbAudioOn);
		rgGroup.add(rbAudioOff);

		JLabel lbl = new JLabel(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIOptionsDialog.initialZoomLevel")); //$NON-NLS-1$
		gc.gridy = 2;
		gc.gridx = 0;
		gc.gridwidth=2;
		gb.setConstraints(lbl, gc);
		panel.add(lbl);

		createZoomChoiceBox();
		gc.gridx = 2;
		gc.gridwidth=1;
		gb.setConstraints(cbZoom, gc);
		panel.add(cbZoom);

		return panel;
	}

	/**
	 * Create a new choicbox for zoom options.
	 * @return JComboBox, the choicbox for the zoom options.
	 */
	public JComboBox createZoomChoiceBox() {

		cbZoom = new JComboBox();
        cbZoom.setOpaque(true);
		cbZoom.setEditable(false);
		cbZoom.setEnabled(true);
		cbZoom.setMaximumRowCount(4);
		cbZoom.setFont( new Font("Dialog", Font.PLAIN, 10 )); //$NON-NLS-1$

		cbZoom.addItem(new String("100%")); //$NON-NLS-1$
		cbZoom.addItem(new String("75%")); //$NON-NLS-1$
		cbZoom.addItem(new String("50%")); //$NON-NLS-1$
		cbZoom.addItem(new String("25%")); //$NON-NLS-1$

		cbZoom.validate();

		double zoom = FormatProperties.zoomLevel;
		if (zoom == 1.0)
			cbZoom.setSelectedIndex(0);
		else if (zoom == 0.75)
			cbZoom.setSelectedIndex(1);
		else if (zoom == 0.50)
			cbZoom.setSelectedIndex(2);
		else if (zoom == 0.25)
			cbZoom.setSelectedIndex(3);

		DefaultListCellRenderer zoomRenderer = new DefaultListCellRenderer() {
			public Component getListCellRendererComponent(
   		     	JList list,
   		        Object value,
            	int modelIndex,
            	boolean isSelected,
            	boolean cellHasFocus)
            {
				if (list != null) {
	 		 		if (isSelected) {
						setBackground(list.getSelectionBackground());
						setForeground(list.getSelectionForeground());
					}
					else {
						setBackground(list.getBackground());
						setForeground(list.getForeground());
					}
				}

				setText((String) value);
				return this;
			}
		};
		cbZoom.setRenderer(zoomRenderer);

		return cbZoom;
	}

	/**
	 * Create the panel with the Drag and Drop options.
	 */
	/**
	 * Create the panel with the Drag and Drop options for files.
	 */
	private JPanel createDndFilesPanel() {

		JPanel dropaspanel = new JPanel();
        dropaspanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIOptionsDialog.dropFileAs")), //$NON-NLS-1$
                BorderFactory.createEmptyBorder(5,5,5,5)));


		oDnDCopyFilePanel = new JPanel();
		oDnDCopyBorder = BorderFactory.createTitledBorder(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIOptionsDialog.coptyFilesTo")+":"); //$NON-NLS-1$
        oDnDCopyFilePanel.setBorder(BorderFactory.createCompoundBorder(
        		oDnDCopyBorder,
                BorderFactory.createEmptyBorder(5,5,5,5)));

		GridBagLayout gbd = new GridBagLayout();

		oDnDCopyFilePanel.setLayout(gbd);
		dropaspanel.setLayout(gbd);

		int y=0;

		GridBagConstraints gc = new GridBagConstraints();
		gc.insets = new Insets(5,5,5,5);
		gc.anchor = GridBagConstraints.NORTHWEST;
		gc.fill = GridBagConstraints.NONE;
		gc.weightx = 1; // fill excess horizontal space, so buttons are aligned left

		rbDnDDropFileAsLink = new JRadioButton(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIOptionsDialog.linkToOriginal")); //$NON-NLS-1$
		gc.gridy = y;
		y++;
		gbd.setConstraints(rbDnDDropFileAsLink, gc);
		dropaspanel.add(rbDnDDropFileAsLink);
		rbDnDDropFileAsLink.addItemListener(this);
		
		rbDnDDropFileAsCopy = new JRadioButton(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIOptionsDialog.copyOfOriginal")); //$NON-NLS-1$
		gc.gridy = y;
		y++;
		gbd.setConstraints(rbDnDDropFileAsCopy, gc);
		dropaspanel.add(rbDnDDropFileAsCopy);

		rbDnDDropFilePrompt = new JRadioButton(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIOptionsDialog.alwaysPrompt")); //$NON-NLS-1$
		gc.gridy = y;
		y++;
		gc.weighty = 1; // fill rest of vertical space
		gbd.setConstraints(rbDnDDropFilePrompt, gc);
		dropaspanel.add(rbDnDDropFilePrompt);

		ButtonGroup dropGroup = new ButtonGroup();
		dropGroup.add(rbDnDDropFileAsLink);
		dropGroup.add(rbDnDDropFileAsCopy);
		dropGroup.add(rbDnDDropFilePrompt);

		rbDnDCopyFileToDB = new JRadioButton(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIOptionsDialog.database")); //$NON-NLS-1$
		gc.gridy = 0;
		y++;
		gc.weighty = 0; 
		gbd.setConstraints(rbDnDCopyFileToDB, gc);
		oDnDCopyFilePanel.add(rbDnDCopyFileToDB);
		
		rbDnDCopyFileToFolder = new JRadioButton(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIOptionsDialog.linkedFilesDir")); //$NON-NLS-1$
		gc.gridy = y;
		y++;
		gc.weighty = 1; // fill rest of vertical space
		gbd.setConstraints(rbDnDCopyFileToFolder, gc);
		oDnDCopyFilePanel.add(rbDnDCopyFileToFolder);

		ButtonGroup copyGroup = new ButtonGroup();
		copyGroup.add(rbDnDCopyFileToDB);
		copyGroup.add(rbDnDCopyFileToFolder);

		// default settings
		DragAndDropProperties props = FormatProperties.dndProperties;
		if (props.dndFileCopy) {
			rbDnDDropFileAsCopy.setSelected(true);
		}
		else {
			rbDnDDropFileAsLink.setSelected(true);
		}
		
		if (props.dndFileCopyDatabase) {
			rbDnDCopyFileToDB.setSelected(true);
		}
		else {
			rbDnDCopyFileToFolder.setSelected(true);
		}
		
		if (props.dndFilePrompt) {
			rbDnDDropFilePrompt.setSelected(true);
		}


        cbDnDTextPrompt = new JCheckBox(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIOptionsDialog.dndTextPrompt")); //$NON-NLS-1$
		if (props.dndNoTextChoice) {
			cbDnDTextPrompt.setSelected(true);
		}
		
		// create main dnd files panel
        JPanel dndpanel = new JPanel(new BorderLayout());
        //dndpanel.setLayout(new BoxLayout(dndpanel, BoxLayout.X_AXIS));
        dndpanel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
        
        dndpanel.add(dropaspanel, BorderLayout.WEST);
        dndpanel.add(oDnDCopyFilePanel, BorderLayout.EAST);
        dndpanel.add(cbDnDTextPrompt, BorderLayout.SOUTH);
        
		return dndpanel;
	}

	/**
	 * Create the panel with the Drag and Drop options for folders.
	 */
	private JPanel createDndFolderPanel() {

		JPanel dropaspanel = new JPanel();
        dropaspanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIOptionsDialog.dropFolderAs")+":"), //$NON-NLS-1$
                BorderFactory.createEmptyBorder(5,5,5,5)));

		GridBagLayout gb = new GridBagLayout();
		dropaspanel.setLayout(gb);

		int y=0;

		GridBagConstraints gc = new GridBagConstraints();
		gc.insets = new Insets(5,5,5,5);
		gc.anchor = GridBagConstraints.NORTHWEST;
		gc.weightx = 1; // fill excess horizontal space, so buttons are aligned left

		rbDnDDropFolderAsLink = new JRadioButton(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIOptionsDialog.linkToOriginalFolder")); //$NON-NLS-1$
		gc.gridy = y;
		y++;

		gb.setConstraints(rbDnDDropFolderAsLink, gc);
		dropaspanel.add(rbDnDDropFolderAsLink);
		rbDnDDropFolderAsLink.addItemListener(this);
		
		rbDnDDropFolderAsMap = new JRadioButton(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIOptionsDialog.mapNode")); //$NON-NLS-1$
		gc.gridy = y;
		y++;

		gb.setConstraints(rbDnDDropFolderAsMap, gc);
		dropaspanel.add(rbDnDDropFolderAsMap);

		rbDnDDropFolderAsMapRecursively = new JRadioButton(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIOptionsDialog.mapNodeRecursively")); //$NON-NLS-1$
		gc.gridy = y;
		y++;

		gb.setConstraints(rbDnDDropFolderAsMapRecursively, gc);
		dropaspanel.add(rbDnDDropFolderAsMapRecursively);

		rbDnDDropFolderPrompt = new JRadioButton(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIOptionsDialog.alwaysPrompt")); //$NON-NLS-1$
		gc.gridy = y;
		y++;

		gc.weighty = 1; // fill rest of vertical space on last button
		gb.setConstraints(rbDnDDropFolderPrompt, gc);
		dropaspanel.add(rbDnDDropFolderPrompt);
		
		ButtonGroup dropGroup = new ButtonGroup();
		dropGroup.add(rbDnDDropFolderAsLink);
		dropGroup.add(rbDnDDropFolderAsMap);
		dropGroup.add(rbDnDDropFolderAsMapRecursively);
		dropGroup.add(rbDnDDropFolderPrompt);

		// default settings
		DragAndDropProperties props = FormatProperties.dndProperties;

		rbDnDDropFolderAsLink.setSelected(!props.dndFolderMap);
		rbDnDDropFolderAsMap.setSelected(props.dndFolderMap);
		rbDnDDropFolderAsMapRecursively.setSelected(props.dndFolderMapRecursively);
		rbDnDDropFolderPrompt.setSelected(props.dndFolderPrompt);

		// create main dnd folder panel
        JPanel dndpanel = new JPanel();
        dndpanel.setLayout(new BoxLayout(dndpanel, BoxLayout.PAGE_AXIS));
        dndpanel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));

        dndpanel.add(dropaspanel);
 		
		return dndpanel;
	}

	private void setEnabledDnDFileCopyPanel(boolean enabled) {
		rbDnDCopyFileToDB.setEnabled(enabled);
		rbDnDCopyFileToFolder.setEnabled(enabled);
		oDnDCopyBorder.setTitleColor(
				enabled? SystemColor.textText:SystemColor.textInactiveText);
		oDnDCopyFilePanel.repaint();
		if (FormatProperties.dndProperties.dndFileCopyDatabase) {
			rbDnDCopyFileToDB.setSelected(true);
		}
		else {
			rbDnDCopyFileToFolder.setSelected(true);
		}
	}
	
	/**
	 * Create the panel with rollover options.
	 */
	public JPanel createRolloverPanel() {

		JPanel panel = new JPanel();
		GridBagLayout gb = new GridBagLayout();
		panel.setLayout(gb);

		GridBagConstraints gc = new GridBagConstraints();
		gc.insets = new Insets(5,5,5,5);
		gc.anchor = GridBagConstraints.WEST;
		gc.gridwidth=2;

		int y=0;

		JLabel label = new JLabel(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIOptionsDialog.cursorMoveMentDistance")+": "); //$NON-NLS-1$
		gc.gridy = y;
		gc.gridwidth=1;
		gb.setConstraints(label, gc);
		panel.add(label);

		txtCursorMoveDistance = new JTextField(5);
		gc.gridy = y;
		y++;
		gc.gridwidth=1;
		gb.setConstraints(txtCursorMoveDistance, gc);
		panel.add(txtCursorMoveDistance);

		if (FormatProperties.cursorMovementDistance > 0)
			txtCursorMoveDistance.setText(new Integer(FormatProperties.cursorMovementDistance).toString());

		rbImageRolloverScale = new JCheckBox(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIOptionsDialog.scaleBigImages")); //$NON-NLS-1$
		gc.gridy = y;
		y++;
		gc.gridwidth=2;
		gb.setConstraints(rbImageRolloverScale, gc);
		panel.add(rbImageRolloverScale);

		if (FormatProperties.scaleImageRollover)
			rbImageRolloverScale.setSelected(true);

		JLabel label2 = new JLabel(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIOptionsDialog.detailRolloverLength")+": "); //$NON-NLS-1$
		gc.gridy = y;
		gc.gridwidth=1;
		gb.setConstraints(label2, gc);
		panel.add(label2);

		txtDetailRolloverLength = new JTextField(5);
		gc.gridy = y;
		y++;
		gc.gridwidth=1;
		gb.setConstraints(txtDetailRolloverLength, gc);
		panel.add(txtDetailRolloverLength);

		if (FormatProperties.detailRolloverLength > 0)
			txtDetailRolloverLength.setText(new Integer(FormatProperties.detailRolloverLength).toString());

		// check box for single click		
		rbSingleClick = new JCheckBox(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIOptionsDialog.singleClickMapOpen")); //$NON-NLS-1$
		rbSingleClick.addItemListener(this);
		gc.gridy =y;
		gc.gridwidth = 2;		
		gb.setConstraints(rbSingleClick, gc);
		panel.add(rbSingleClick);
		
		rbSingleClick.setSelected(FormatProperties.singleClick);
		
		return panel;
	}

	/**
	 * Create the panel with arrange options.
	 */
	public JPanel createArrangePanel() {

		JPanel panel = new JPanel();
		GridBagLayout gb = new GridBagLayout();
		panel.setLayout(gb);

		GridBagConstraints gc = new GridBagConstraints();
		gc.insets = new Insets(5,5,5,5);
		gc.anchor = GridBagConstraints.WEST;
		gc.gridwidth=2;

		int y=0;

		JLabel label = new JLabel(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIOptionsDialog.leftToRightVerticalGap")+": "); //$NON-NLS-1$
		gc.gridy = y;
		gc.gridwidth=1;
		gb.setConstraints(label, gc);
		panel.add(label);

		txtLeftVerticalGap = new JTextField(5);
		gc.gridy = y;
		y++;
		gc.gridwidth=1;
		gb.setConstraints(txtLeftVerticalGap, gc);
		panel.add(txtLeftVerticalGap);

		if (FormatProperties.arrangeLeftVerticalGap > 0)
			txtLeftVerticalGap.setText(new Integer(FormatProperties.arrangeLeftVerticalGap).toString());

		label = new JLabel(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIOptionsDialog.leftToRightHorizontalGap")+": "); //$NON-NLS-1$
		gc.gridy = y;
		gc.gridwidth=1;
		gb.setConstraints(label, gc);
		panel.add(label);

		txtLeftHorizontalGap = new JTextField(5);
		gc.gridy = y;
		y++;
		gc.gridwidth=1;
		gb.setConstraints(txtLeftHorizontalGap, gc);
		panel.add(txtLeftHorizontalGap);

		if (FormatProperties.arrangeLeftHorizontalGap > 0)
			txtLeftHorizontalGap.setText(new Integer(FormatProperties.arrangeLeftHorizontalGap).toString());


		label = new JLabel(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIOptionsDialog.topDownVerticalGap")+": "); //$NON-NLS-1$
		gc.gridy = y;
		gc.gridwidth=1;
		gb.setConstraints(label, gc);
		panel.add(label);

		txtTopVerticalGap = new JTextField(5);
		gc.gridy = y;
		y++;
		gc.gridwidth=1;
		gb.setConstraints(txtTopVerticalGap, gc);
		panel.add(txtTopVerticalGap);

		if (FormatProperties.cursorMovementDistance > 0)
			txtTopVerticalGap.setText(new Integer(FormatProperties.arrangeTopVerticalGap).toString());

		label = new JLabel(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIOptionsDialog.topDownHorizontalGap")+": "); //$NON-NLS-1$
		gc.gridy = y;
		gc.gridwidth=1;
		gb.setConstraints(label, gc);
		panel.add(label);

		txtTopHorizontalGap = new JTextField(5);
		gc.gridy = y;
		y++;
		gc.gridwidth=1;
		gb.setConstraints(txtTopHorizontalGap, gc);
		panel.add(txtTopHorizontalGap);

		if (FormatProperties.cursorMovementDistance > 0)
			txtTopHorizontalGap.setText(new Integer(FormatProperties.arrangeTopHorizontalGap).toString());

		return panel;
	}

	/**
	 * Create the panel with the Mac OS options.
	 */
	public JPanel createMacPanel() {

		JPanel panel = new JPanel();
		GridBagLayout gb = new GridBagLayout();
		panel.setLayout(gb);

		GridBagConstraints gc = new GridBagConstraints();
		gc.insets = new Insets(5,5,5,5);
		gc.anchor = GridBagConstraints.WEST;
		gc.gridwidth=1;

		JLabel label = new JLabel(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIOptionsDialog.restartMessage")); //$NON-NLS-1$
		gc.gridy = 0;
		gc.gridwidth=4;
		gb.setConstraints(label, gc);
		panel.add(label);

		JLabel label2 = new JLabel(" "); //$NON-NLS-1$
		gc.gridy = 1;
		gc.gridwidth=4;
		gb.setConstraints(label2, gc);
		panel.add(label2);

		rbMenuPosition = new JCheckBox(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIOptionsDialog.putBarAtTop")); //$NON-NLS-1$
		rbMenuPosition.addItemListener(this);
		gc.gridy = 2;
		gc.gridwidth=4;
		gb.setConstraints(rbMenuPosition, gc);
		panel.add(rbMenuPosition);

		JLabel label3 = new JLabel(" "); //$NON-NLS-1$
		gc.gridy = 3;
		gc.gridwidth=1;
		gb.setConstraints(label3, gc);
		panel.add(label3);

		rbMenuUnderline = new JCheckBox(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIOptionsDialog.activateShortcuts")); //$NON-NLS-1$
		gc.gridy = 3;
		gc.gridx = 2;
		gc.gridwidth=4;
		gb.setConstraints(rbMenuUnderline, gc);
		panel.add(rbMenuUnderline);

		rbMenuUnderline.setSelected(FormatProperties.macMenuUnderline);
		rbMenuPosition.setSelected(FormatProperties.macMenuBar);

		return panel;
	}

	/**
	 * Create the panel with various misc options.
	 */
	public JPanel createMiscPanel() {

		JPanel panel = new JPanel();
		GridBagLayout gb = new GridBagLayout();
		panel.setLayout(gb);

		GridBagConstraints gc = new GridBagConstraints();
		gc.insets = new Insets(5,5,5,5);
		gc.anchor = GridBagConstraints.WEST;
		gc.gridwidth=1;
		gc.gridx=0;
		gc.gridy=0;

		JLabel label = new JLabel(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIOptionsDialog.iconSets")); //$NON-NLS-1$
		gb.setConstraints(label, gc);
		panel.add(label);

		gc.gridx=1;		
		createIconSetChoiceBox();
		gb.setConstraints(cbIconSets, gc);
		panel.add(cbIconSets);
		
		gc.gridy=1;
		gc.gridx=0;		

		label = new JLabel(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIOptionsDialog.lookAndFeel")); //$NON-NLS-1$
		gb.setConstraints(label, gc);
		panel.add(label);

		gc.gridx=1;		
		createLandFChoiceBox();
		gb.setConstraints(cbLandF, gc);
		panel.add(cbLandF);

		gc.gridy=2;
		gc.gridx=0;		
		gc.gridwidth=2;
		gc.fill = GridBagConstraints.HORIZONTAL;
	
		JSeparator sep = new JSeparator();
		gb.setConstraints(sep, gc);
		panel.add(sep);

		gc.gridy=3;
		gc.fill = GridBagConstraints.NONE;

		rbAdvancedInterface = new JCheckBox(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIOptionsDialog.advancedUserInterface")); //$NON-NLS-1$
		rbAdvancedInterface.addItemListener(this);
		gb.setConstraints(rbAdvancedInterface, gc);
		panel.add(rbAdvancedInterface);
		
		rbAdvancedInterface.setSelected(!FormatProperties.simpleInterface);
		
		gc.gridy=4;
		gc.gridx=0;		
		gc.gridwidth=2;
		gc.fill = GridBagConstraints.HORIZONTAL;
	
		sep = new JSeparator();
		gb.setConstraints(sep, gc);
		panel.add(sep);

		gc.gridy=5;
		gc.fill = GridBagConstraints.NONE;

		rbInboxEmail = new JCheckBox(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIOptionsDialog.emailInbox")); //$NON-NLS-1$
		rbInboxEmail.addItemListener(this);
		gb.setConstraints(rbInboxEmail, gc);
		panel.add(rbInboxEmail);
		
		rbInboxEmail.setSelected(FormatProperties.emailInbox);

		if (ProjectCompendium.isLinux) {
			rbKFMClient = new JCheckBox(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIOptionsDialog.kmfclient")); //$NON-NLS-1$
			rbKFMClient.addItemListener(this);
			gc.gridy=6;
			gc.fill = GridBagConstraints.NONE;		
			gb.setConstraints(rbKFMClient, gc);
			panel.add(rbKFMClient);
		
			rbKFMClient.setSelected(FormatProperties.useKFMClient);
		}				
		
		return panel;
	}
	
	
	/**
	 * Create the Look and Feel choicebox.
	 */
	private JComboBox createLandFChoiceBox() {

		cbLandF = new JComboBox();
		cbLandF.setOpaque(true);
		cbLandF.setEditable(false);
		cbLandF.setEnabled(true);
		cbLandF.setMaximumRowCount(20);
		cbLandF.setFont( new Font("Dialog", Font.PLAIN, 12 )); //$NON-NLS-1$

		Vector lafs = new Vector(10);
		int selectedIndex = 0;
		
		boolean KunstDetected = false;
		
		UIManager.LookAndFeelInfo[] looks = UIManager.getInstalledLookAndFeels();
		LookAndFeel current = UIManager.getLookAndFeel();
		String currentLook = ""; //$NON-NLS-1$
		if (current != null ) {
			currentLook = current.getClass().getName();			
			// WHEN FIRST INSTALL ON LINUX ITS EMPTY
			if (FormatProperties.currentLookAndFeel.equals("")) { //$NON-NLS-1$
	    		FormatProperties.currentLookAndFeel = currentLook;
				FormatProperties.setFormatProp( "LAF", FormatProperties.currentLookAndFeel ); //$NON-NLS-1$
				FormatProperties.saveFormatProps();	    			
			}
		} else {
			currentLook = FormatProperties.currentLookAndFeel;
		}
			
		String look = ""; //$NON-NLS-1$
		for (int i=0; i< looks.length; i++) {
			
			if (looks[i].getName().equals("Kunststoff")) { //$NON-NLS-1$
				KunstDetected = true;	
				if (!ProjectCompendium.isMac) {
					lafs.addElement(looks[i]);
				}				
			} else {
				lafs.addElement(looks[i]);
			}

			look = looks[i].getClassName();
			if (look.equals(currentLook)) {
				selectedIndex = i;
			}
		}
		
		if (!KunstDetected && !ProjectCompendium.isMac) {
			if (!ProjectCompendium.isMac) {
				lafs.addElement("Kunststoff"); //$NON-NLS-1$
				if ((FormatProperties.currentLookAndFeel).equals("Kunststoff")) { //$NON-NLS-1$
					selectedIndex = lafs.size()-1;
				}				
			}
		}		
		
		DefaultComboBoxModel comboModel = new DefaultComboBoxModel(lafs);
		cbLandF.setModel(comboModel);
		cbLandF.setSelectedIndex(selectedIndex);
		
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

 		 		if (value instanceof String) {
 		 			setText((String)value);
 		 		} else {
 		 			UIManager.LookAndFeelInfo look = (UIManager.LookAndFeelInfo)value; 		 		
 		 			setText(look.getName());
 		 		}

				return this;
			}
		};

		cbLandF.setRenderer(comboRenderer);
		
		return cbLandF;
	}
	
	/**
	 * Create the code group choicebox.
	 */
	private JComboBox createIconSetChoiceBox() {

		cbIconSets = new JComboBox();
		cbIconSets.setOpaque(true);
		cbIconSets.setEditable(false);
		cbIconSets.setEnabled(true);
		cbIconSets.setMaximumRowCount(20);
		cbIconSets.setFont( new Font("Dialog", Font.PLAIN, 12 )); //$NON-NLS-1$
						
		File main = new File("Skins"); //$NON-NLS-1$
		File skins[] = main.listFiles();
		Vector vtSkins = new Vector(skins.length);
		for (int i=0; i< skins.length; i++) {
			vtSkins.add(skins[i]);
		}
		vtSkins = CoreUtilities.sortList(vtSkins);
		Vector vtFinalSkins = new Vector(vtSkins.size());
		
		int selectedItem = 0;
		int count = vtSkins.size();
		String skinName = "";	 //$NON-NLS-1$
		int indexcount = 0;
		for (int i=0; i< count; i++) {
			File nextSkin = (File)vtSkins.elementAt(i);
			if (nextSkin.isDirectory()) {
				skinName = nextSkin.getName();
				if (FormatProperties.skin.equals(skinName)) {
					selectedItem = indexcount;
				}
				vtFinalSkins.addElement(skinName);
				indexcount++;
			}
		}

		DefaultComboBoxModel comboModel = new DefaultComboBoxModel(vtFinalSkins);
		cbIconSets.setModel(comboModel);
		cbIconSets.setSelectedIndex(selectedItem);
		
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
 		 		
				setText((String) value);

				return this;
			}
		};
		cbIconSets.setRenderer(comboRenderer);

		return cbIconSets;
	}
	
	/**
	 * Records the fact that a checkbox / radio button state has been changed and stores the new data.
	 * @param e, the associated ItemEvent.
	 */
	public void itemStateChanged(ItemEvent e) {

		Object source = e.getItemSelectable();
		if (source == rbMenuPosition) {
			if (rbMenuPosition.isSelected()) {
				rbMenuUnderline.setEnabled(false);
				//rbMenuUnderline.setSelected(false);
			}
			else {
				rbMenuUnderline.setEnabled(true);
				//rbMenuUnderline.setSelected(false);
			}
		}
	}

	/**
	 * Create the button panel.
	 */
	private UIButtonPanel createButtonPanel() {

		UIButtonPanel panel = new UIButtonPanel();

		pbUpdate = new UIButton(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIOptionsDialog.updateButton")); //$NON-NLS-1$
		pbUpdate.setMnemonic(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIOptionsDialog.updateButtonMnemonic").charAt(0)); //$NON-NLS-1$
		pbUpdate.addActionListener(this);
		getRootPane().setDefaultButton(pbUpdate);
		panel.addButton(pbUpdate);

		pbCancel = new UIButton(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIOptionsDialog.cancelButton")); //$NON-NLS-1$
		pbCancel.setMnemonic(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIOptionsDialog.cancelButtonMnemonic").charAt(0)); //$NON-NLS-1$
		pbCancel.addActionListener(this);
		panel.addButton(pbCancel);

		// Add help button
		pbHelp = new UIButton(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIOptionsDialog.helpButton")); //$NON-NLS-1$
		pbHelp.setMnemonic(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIOptionsDialog.helpButtonMnemonic").charAt(0)); //$NON-NLS-1$
		ProjectCompendium.APP.mainHB.enableHelpOnButton(pbHelp, "basics.options", ProjectCompendium.APP.mainHS); //$NON-NLS-1$
		panel.addHelpButton(pbHelp);

		return panel;
	}

	/**
	 * Process button pushes.
	 * @param evt, the associated ActionEvent object.
	 */
	public void actionPerformed(ActionEvent evt) {
		Object source = evt.getSource();

		if (source == pbCancel)
			onCancel();
		else
		if (source == pbUpdate)
			onUpdate();
	}

	/**
	 * Save the users options and update where necessary.
	 */
	public void onUpdate() {

		try {
			String dndChoice = "off"; //$NON-NLS-1$
			boolean scaleRollover = false;

    		String skinName = (String)cbIconSets.getSelectedItem();
    		if (!skinName.equals(FormatProperties.skin)) {
    			ProjectCompendium.APP.onFormatSkin(skinName);										
    		}
			
    		Object obj = cbLandF.getSelectedItem();
    		String className = ""; //$NON-NLS-1$
    		if (obj instanceof String) {
       			className = (String)obj;
    		} else if (obj instanceof UIManager.LookAndFeelInfo) {    			    		    			
	    		UIManager.LookAndFeelInfo look = (UIManager.LookAndFeelInfo)cbLandF.getSelectedItem();
	    		className = look.getClassName();
    		}
    		
	    	if (!className.equals(FormatProperties.currentLookAndFeel)) {
	    		if (className.equals("Kunststoff")) { //$NON-NLS-1$
	    			className = "com.incors.plaf.kunststoff.KunststoffLookAndFeel"; //$NON-NLS-1$
	    		} 

	    		FormatProperties.currentLookAndFeel = className;
				FormatProperties.setFormatProp( "LAF", FormatProperties.currentLookAndFeel ); //$NON-NLS-1$
				FormatProperties.saveFormatProps();	    			
	    		
				ProjectCompendium.APP.displayMessage(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIOptionsDialog.lookAndFeelMessage"), LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIOptionsDialog.lookAndFeelUpdate")); //$NON-NLS-1$ //$NON-NLS-2$
     		}
	    				
			if (!txtDetailRolloverLength.getText().equals("")) { //$NON-NLS-1$
				try {
					int len = new Integer(txtDetailRolloverLength.getText()).intValue();
					FormatProperties.detailRolloverLength = len;
					FormatProperties.setFormatProp("detailrolloverlength", new Integer(FormatProperties.detailRolloverLength).toString()); //$NON-NLS-1$
				}
				catch(NumberFormatException e) {
					ProjectCompendium.APP.displayMessage(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIOptionsDialog.detailRolloverLengthInvalid")+"\n"+
							LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIOptionsDialog.pleaseTryAgain")+"\n", LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIOptionsDialog.optionsError")); //$NON-NLS-1$ //$NON-NLS-2$
					txtDetailRolloverLength.requestFocus();
				}
			}

			if (!txtCursorMoveDistance.getText().equals("")) { //$NON-NLS-1$
				try {
					int len = new Integer(txtCursorMoveDistance.getText()).intValue();
					FormatProperties.cursorMovementDistance = len;
					FormatProperties.setFormatProp(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIOptionsDialog.cursorLengthInvalid"), new Integer(FormatProperties.cursorMovementDistance).toString()); //$NON-NLS-1$
				}
				catch(NumberFormatException e) {
					ProjectCompendium.APP.displayMessage(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIOptionsDialog.cursorDistanceInvalidA")+"\n"+
							LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIOptionsDialog.pleaseTryAgain")+"\n", LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIOptionsDialog.optionsError")); //$NON-NLS-1$ //$NON-NLS-2$
					txtCursorMoveDistance.requestFocus();
				}
			}

			if (rbImageRolloverScale.isSelected()) {
				scaleRollover = true;
				FormatProperties.setFormatProp( "scaleImageRollover", "true" ); //$NON-NLS-1$ //$NON-NLS-2$
			}
			else
				FormatProperties.setFormatProp( "scaleImageRollover", "false" ); //$NON-NLS-1$ //$NON-NLS-2$

			FormatProperties.scaleImageRollover = scaleRollover;

			DragAndDropProperties dndprops = FormatProperties.dndProperties;
			
			dndprops.dndFileCopy = rbDnDDropFileAsCopy.isSelected();
			FormatProperties.setFormatProp("dndFileCopy",  //$NON-NLS-1$
					rbDnDDropFileAsCopy.isSelected()? "true":"false"); //$NON-NLS-1$ //$NON-NLS-2$

			dndprops.dndFileCopyDatabase = rbDnDCopyFileToDB.isSelected();
			FormatProperties.setFormatProp("dndFileCopyDatabase",  //$NON-NLS-1$
					rbDnDCopyFileToDB.isSelected()? "true":"false"); //$NON-NLS-1$ //$NON-NLS-2$

			dndprops.dndFilePrompt = rbDnDDropFilePrompt.isSelected();
			FormatProperties.setFormatProp("dndFilePrompt",  //$NON-NLS-1$
					rbDnDDropFilePrompt.isSelected()? "true":"false"); //$NON-NLS-1$ //$NON-NLS-2$

			dndprops.dndFolderMap = rbDnDDropFolderAsMap.isSelected();
			FormatProperties.setFormatProp("dndFolderMap",  //$NON-NLS-1$
					rbDnDDropFolderAsMap.isSelected()? "true":"false"); //$NON-NLS-1$ //$NON-NLS-2$

			dndprops.dndFolderMapRecursively = rbDnDDropFolderAsMapRecursively.isSelected();
			FormatProperties.setFormatProp("dndFolderMapRecursively",  //$NON-NLS-1$
					rbDnDDropFolderAsMapRecursively.isSelected()? "true":"false"); //$NON-NLS-1$ //$NON-NLS-2$

			dndprops.dndFolderPrompt = rbDnDDropFolderPrompt.isSelected();
			FormatProperties.setFormatProp("dndFolderPrompt",  //$NON-NLS-1$
					rbDnDDropFolderPrompt.isSelected()? "true":"false"); //$NON-NLS-1$ //$NON-NLS-2$

			dndprops.dndNoTextChoice = cbDnDTextPrompt.isSelected();
			FormatProperties.setFormatProp("dndNoTextChoice",  //$NON-NLS-1$
					cbDnDTextPrompt.isSelected()? "true":"false"); //$NON-NLS-1$ //$NON-NLS-2$

			FormatProperties.dndProperties = dndprops;
			
			if (rbSingleClick.isSelected()) {
				FormatProperties.singleClick = true;
				FormatProperties.setFormatProp( "singleClick", "true" ); //$NON-NLS-1$ //$NON-NLS-2$
			}
			else {
				FormatProperties.singleClick = false;
				FormatProperties.setFormatProp( "singleClick", "false" ); //$NON-NLS-1$ //$NON-NLS-2$
			}			
		
			if (rbKFMClient != null && rbKFMClient.isSelected()) {
				FormatProperties.useKFMClient = true;
				FormatProperties.setFormatProp( "kfmclient", "true" ); //$NON-NLS-1$ //$NON-NLS-2$
			}
			else {
				FormatProperties.useKFMClient = false;
				FormatProperties.setFormatProp( "kfmclient", "false" ); //$NON-NLS-1$ //$NON-NLS-2$
			}			
									
			if (rbInboxEmail != null && rbInboxEmail.isSelected()) {
				FormatProperties.emailInbox = true;
				FormatProperties.setFormatProp( "emailInbox", "true" ); //$NON-NLS-1$ //$NON-NLS-2$
			}
			else {
				FormatProperties.emailInbox = false;
				FormatProperties.setFormatProp( "emailInbox", "false" ); //$NON-NLS-1$ //$NON-NLS-2$
			}			

			FormatProperties.setFormatProp( "dndFiles", dndChoice ); //$NON-NLS-1$

			int ind = cbZoom.getSelectedIndex();
			if (ind == 0) // 100%
				FormatProperties.zoomLevel = 1.0;
			else if (ind == 1) // 75%
				FormatProperties.zoomLevel = 0.75;
			else if (ind == 2) // 50%
				FormatProperties.zoomLevel = 0.50;
			else if (ind == 3) // 25%
				FormatProperties.zoomLevel = 0.25;

			/*if (ProjectCompendium.isMac) {
				boolean oldMenuBar = FormatProperties.macMenuBar;
				FormatProperties.macMenuBar = rbMenuPosition.isSelected();
				if (rbMenuPosition.isSelected())
					FormatProperties.setFormatProp( "macmenubar", "true" );
				else
					FormatProperties.setFormatProp( "macmenubar", "false" );

				boolean oldMenuUnderline = FormatProperties.macMenuUnderline;
				FormatProperties.macMenuUnderline = rbMenuUnderline.isSelected();
				if (rbMenuUnderline.isSelected() && !rbMenuPosition.isSelected())
					FormatProperties.setFormatProp( "macmenuunderline", "true" );
				else
					FormatProperties.setFormatProp( "macmenuunderline", "false" );
			}*/

			FormatProperties.setFormatProp( "zoom", new Double(FormatProperties.zoomLevel).toString() ); //$NON-NLS-1$

			if (!txtLeftHorizontalGap.getText().equals("")) { //$NON-NLS-1$
				try {
					int len = new Integer(txtLeftHorizontalGap.getText()).intValue();
					FormatProperties.arrangeLeftHorizontalGap = len;
					FormatProperties.setFormatProp("arrangeLeftHorizontalGap", new Integer(FormatProperties.arrangeLeftHorizontalGap).toString()); //$NON-NLS-1$
				}
				catch(NumberFormatException e) {
					ProjectCompendium.APP.displayMessage(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIOptionsDialog.leftRightHorizontalGapInvalid")+"\n"+
							LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIOptionsDialog.pleaseTryAgain")+"\n", LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIOptionsDialog.optionsError")); //$NON-NLS-1$ //$NON-NLS-2$
					txtLeftHorizontalGap.requestFocus();
				}
			}

			if (!txtLeftVerticalGap.getText().equals("")) { //$NON-NLS-1$
				try {
					int len = new Integer(txtLeftVerticalGap.getText()).intValue();
					FormatProperties.arrangeLeftVerticalGap = len;
					FormatProperties.setFormatProp("arrangeLeftVerticalGap", new Integer(FormatProperties.arrangeLeftVerticalGap).toString()); //$NON-NLS-1$
				}
				catch(NumberFormatException e) {
					ProjectCompendium.APP.displayMessage(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIOptionsDialog.leftRightVerticalGapInvalid")+"\n"+
							LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIOptionsDialog.pleaseTryAgain")+"\n", LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIOptionsDialog.optionsError")); //$NON-NLS-1$ //$NON-NLS-2$
					txtLeftVerticalGap.requestFocus();
				}
			}

			if (!txtTopHorizontalGap.getText().equals("")) { //$NON-NLS-1$
				try {
					int len = new Integer(txtTopHorizontalGap.getText()).intValue();
					FormatProperties.arrangeTopHorizontalGap = len;
					FormatProperties.setFormatProp("arrangeTopHorizontalGap", new Integer(FormatProperties.arrangeTopHorizontalGap).toString()); //$NON-NLS-1$
				}
				catch(NumberFormatException e) {
					ProjectCompendium.APP.displayMessage(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIOptionsDialog.horizontalGapInvalid")+"\n"+
							LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIOptionsDialog.pleaseTryAgain")+"\n", LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIOptionsDialog.optionsError")); //$NON-NLS-1$ //$NON-NLS-2$
					txtTopHorizontalGap.requestFocus();
				}
			}

			if (!txtTopVerticalGap.getText().equals("")) { //$NON-NLS-1$
				try {
					int len = new Integer(txtTopVerticalGap.getText()).intValue();
					FormatProperties.arrangeTopVerticalGap = len;
					FormatProperties.setFormatProp("arrangeTopVerticalGap", new Integer(FormatProperties.arrangeTopVerticalGap).toString()); //$NON-NLS-1$
				}
				catch(NumberFormatException e) {
					ProjectCompendium.APP.displayMessage(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIOptionsDialog.verticalNumberInvalid")+"\n"+
							LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIOptionsDialog.pleaseTryAgain")+"\n", LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIOptionsDialog.optionsError")); //$NON-NLS-1$ //$NON-NLS-2$
					txtTopVerticalGap.requestFocus();
				}
			}
			
			if (rbAdvancedInterface.isSelected() && FormatProperties.simpleInterface) {
				FormatProperties.simpleInterface = false;
				FormatProperties.setFormatProp( "simpleInterface", "false" );	 //$NON-NLS-1$ //$NON-NLS-2$
				
				// MUST DO MENU FIRST AS TOOLBAR NEEDS TO SET THINGS ON MENU
				ProjectCompendium.APP.getMenuManager().setIsSimple(!rbAdvancedInterface.isSelected());
				ProjectCompendium.APP.getToolBarManager().setIsSimple(!rbAdvancedInterface.isSelected());							
			} else if (!rbAdvancedInterface.isSelected() && !FormatProperties.simpleInterface){
				FormatProperties.simpleInterface = true;
				FormatProperties.setFormatProp( "simpleInterface", "true" ); //$NON-NLS-1$ //$NON-NLS-2$
				
				// MUST DO MENU FIRST AS TOOLBAR NEEDS TO SET THINGS ON MENU				
				ProjectCompendium.APP.getMenuManager().setIsSimple(!rbAdvancedInterface.isSelected());		
				ProjectCompendium.APP.getToolBarManager().setIsSimple(!rbAdvancedInterface.isSelected());
				
				// MUST MAKE SURE THAT THE CORRECT DATABASE IS OPEN.
				if (!(ProjectCompendium.APP.getProjectName()).equals(SystemProperties.defaultProjectName) ||
					(FormatProperties.nDatabaseType != ICoreConstants.DERBY_DATABASE)) {
					ProjectCompendium.APP.setDerbyDatabaseProfile();										
				}
			}
			
			FormatProperties.saveFormatProps();

			ProjectCompendium.APP.onViewRefresh();
		}
		catch(Exception e) {

			e.printStackTrace();
			return;
		}

		ProjectCompendium.APP.getAudioPlayer().setAudio(rbAudioOn.isSelected());

		onCancel();
	}
}
