/********************************************************************************
 *                                                                              *
 *  (c) Copyright 2009 Verizon Communications USA and The Open University UK    *
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


package com.compendium.ui.stencils;

import java.util.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.table.*;
import javax.swing.border.*;
import javax.swing.event.*;

import com.compendium.core.*;
import com.compendium.core.datamodel.*;
import com.compendium.core.db.*;

import com.compendium.ProjectCompendium;
import com.compendium.io.html.*;
import com.compendium.ui.plaf.*;
import com.compendium.ui.*;
import com.compendium.ui.dialogs.*;

/**
 * UIStencilItemDialog defines the dialog that allows the user to create and manage stencil set items.
 *
 * @author	Michelle Bachler
 */
public class UIStencilItemDialog extends UIDialog implements ActionListener, ItemListener, IUIConstants {

	/** The last director browsed to when looking for external references.*/
	private static String lastFileDialogDir = "";

	/** The current pane to put the dialog contents in.*/
	private Container				oContentPane 	= null;

	/** The button to close the dialog.*/
	private JButton					pbCancel 		= null;

	/** The button to save the stencil set.*/
	private JButton					pbSave 			= null;

	/** The button to open a file browser for the image field.*/
	private JButton					pbBrowse		= null;

	/** The button to open a file browser for the palette image field.*/
	private JButton					pbBrowse2		= null;

	/** The button to open a file browser for the template file field.*/
	private JButton					pbBrowse3		= null;

	/** The button to open a file browser for the background image field.*/
	private JButton					pbBrowse4		= null;

	/** Activates the help opeing to the appropriate section.*/
	private JButton					pbHelp		= null;

	/** The choicebox listing the current allowed node types.*/
	private JComboBox				cbTypes			= null;

	/** The choicebox listing the shortcut keys.*/
	private JComboBox				cbShortcut			= null;

	/** The layout manager used.*/
	private	GridBagLayout 			gb 				= null;

	/** The parent frame for this dialog.*/
	private JFrame					oParent 		= null;

	/** The stencil manager for this dialog.*/
	private UIStencilSetDialog		oManager 		= null;

	/** The counter for the gridbag layout y position.*/
	private int 					gridyStart 		= 0;

	/** The stencil set to edit / created.*/
	private DraggableStencilIcon 	oItem			= null;

	/** The text field to hold the stencil item label.*/
	private JTextField				txtLabel			= null;

	/** The text field to hold the stencil item tool tip text.*/
	private JTextField				txtToolTip			= null;

	/** The textfield for the node images.*/
	private JTextField				txtImage		= null;

	/** The textfield for the palette images.*/
	private JTextField				txtPaletteImage	= null;

	/** The textfield for the background images.*/
	private JTextField				txtBackgroundImage	= null;

	/** The textfield for the template file.*/
	private JTextField				txtTemplate	= null;

	/** The label for the background text field.*/
	private JLabel					lblBackgroundLabel	= null;
	
	/** The label for the template text field.*/
	private JLabel					lblTemplateLabel = null;

	/** The list holding the codes to assign.*/
	private UINavList				lstCodes		= null;

	/** The scrollpane to hold the codes list.*/
	private JScrollPane 			oScrollPane		= null;

	/** The list of available Codes.*/
	private Vector					vtCodes 		= new Vector();

	/**
	 * Constructor. Initializes and sets up the dialog.
	 * Used when editing a stencils set.
	 *
	 * @param parent the frame that is the parent for this dialog.
	 * @param manager the parent managing dialog.
	 * @param set the stencil set to edit.
	 */
	public UIStencilItemDialog(JFrame parent, UIStencilSetDialog manager, DraggableStencilIcon item) {

		super(parent, true);
		oParent = parent;
		oManager = manager;
		oItem = item;

		setTitle("Stencil Set Item");

		oContentPane = getContentPane();
		gb = new GridBagLayout();
		oContentPane.setLayout(gb);

		drawDialog();

		pack();
		setResizable(false);
		return;
	}

	/**
	 * Draws the contents of this dialog.
	 */
	private void drawDialog() {

		GridBagConstraints gc = new GridBagConstraints();
		gc.insets = new Insets(5,5,5,5);
		gc.anchor = GridBagConstraints.WEST;

		JLabel lblLabel = new JLabel("Node Label:");
		lblLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		gc.gridy = gridyStart;
		gc.gridx = 0;
		gb.setConstraints(lblLabel, gc);
		oContentPane.add(lblLabel);

		txtLabel = new JTextField(oItem.getLabel());
		txtLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		txtLabel.setColumns(20);
		txtLabel.setMargin(new Insets(2,2,2,2));
		txtLabel.setSize(txtLabel.getPreferredSize());
		gc.gridy = gridyStart;
		gridyStart++;
		gc.gridx = 1;
		gc.gridwidth = 2;
		gc.fill=GridBagConstraints.HORIZONTAL;
		gb.setConstraints(txtLabel, gc);
		oContentPane.add(txtLabel);

		JLabel lblTip = new JLabel("Icon Hint:");
		lblTip.setFont(new Font("Dialog", Font.PLAIN, 12));
		gc.gridy = gridyStart;
		gc.gridx = 0;
		gb.setConstraints(lblTip, gc);
		oContentPane.add(lblTip);

		txtToolTip = new JTextField(oItem.getToolTip());
		txtToolTip.setFont(new Font("Dialog", Font.PLAIN, 12));
		txtToolTip.setColumns(20);
		txtToolTip.setMargin(new Insets(2,2,2,2));
		txtToolTip.setSize(txtToolTip.getPreferredSize());
		gc.gridy = gridyStart;
		gridyStart++;
		gc.gridx = 1;
		gc.gridwidth = 2;
		gc.fill=GridBagConstraints.HORIZONTAL;
		gb.setConstraints(txtToolTip, gc);
		oContentPane.add(txtToolTip);

		gc.gridwidth = 1;
		gc.fill=GridBagConstraints.NONE;

		JLabel lbl = new JLabel("Node Type ");
		lbl.setFont(new Font("Dialog", Font.PLAIN, 12));
		gc.gridy = gridyStart;
		gc.gridx = 0;
		gb.setConstraints(lbl, gc);
		oContentPane.add(lbl);

		cbTypes = new JComboBox();
        cbTypes.setOpaque(true);
		cbTypes.setEditable(false);
		cbTypes.setEnabled(true);
		cbTypes.setMaximumRowCount(3);
		cbTypes.setFont( new Font("Dialog", Font.PLAIN, 12 ));
		cbTypes.addItem(new String("Reference"));
		cbTypes.addItem(new String("Map"));
		cbTypes.addItem(new String("List"));
		cbTypes.addItemListener(this);
		gc.gridy = gridyStart;
		gridyStart++;
		gc.gridx = 1;
		gc.gridwidth = 2;
		gb.setConstraints(cbTypes, gc);
		oContentPane.add(cbTypes);

		gc.gridwidth = 1;

		lblBackgroundLabel = new JLabel("Background:");
		lblBackgroundLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		lblBackgroundLabel.setEnabled(false);
		gc.gridy = gridyStart;
		gc.gridx = 0;
		gb.setConstraints(lblBackgroundLabel, gc);
		oContentPane.add(lblBackgroundLabel);

		String sBackgroundImage = oItem.getBackgroundImage();

		txtBackgroundImage = new JTextField(sBackgroundImage);
		txtBackgroundImage.setFont(new Font("Dialog", Font.PLAIN, 12));
		txtBackgroundImage.setColumns(23);
		txtBackgroundImage.setMargin(new Insets(2,2,2,2));
		txtBackgroundImage.setSize(txtBackgroundImage.getPreferredSize());
		txtBackgroundImage.setEnabled(false);

		gc.gridy = gridyStart;
		gc.gridx = 1;
		gc.fill=GridBagConstraints.HORIZONTAL;
		gc.weightx=4.0;
		gb.setConstraints(txtBackgroundImage, gc);
		oContentPane.add(txtBackgroundImage);

		pbBrowse3 = new UIButton("./.");
		pbBrowse3.setFont(new Font("Dialog", Font.BOLD, 14));
		pbBrowse3.setMargin(new Insets(0,0,0,0));
		pbBrowse3.setToolTipText("Browse");
		pbBrowse3.addActionListener(this);
		pbBrowse3.setEnabled(false);

		gc.gridy = gridyStart;
		gridyStart++;
		gc.gridx = 2;
		gc.weightx=0.0;
		gc.fill=GridBagConstraints.NONE;
		gb.setConstraints(pbBrowse3, gc);
		oContentPane.add(pbBrowse3);

		lblTemplateLabel = new JLabel("Template:");
		lblTemplateLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		lblTemplateLabel.setEnabled(false);
		gc.gridy = gridyStart;
		gc.gridx = 0;
		gb.setConstraints(lblTemplateLabel, gc);
		oContentPane.add(lblTemplateLabel);
		
		String sTemplate = oItem.getTemplate();

		txtTemplate = new JTextField(sTemplate);
		txtTemplate.setFont(new Font("Dialog", Font.PLAIN, 12));
		txtTemplate.setColumns(23);
		txtTemplate.setMargin(new Insets(2,2,2,2));
		txtTemplate.setSize(txtTemplate.getPreferredSize());
		txtTemplate.setEnabled(false);

		gc.gridy = gridyStart;
		gc.gridx = 1;
		gc.fill=GridBagConstraints.HORIZONTAL;
		gc.weightx=4.0;
		gb.setConstraints(txtTemplate, gc);
		oContentPane.add(txtTemplate);

		pbBrowse4 = new UIButton("./.");
		pbBrowse4.setFont(new Font("Dialog", Font.BOLD, 14));
		pbBrowse4.setMargin(new Insets(0,0,0,0));
		pbBrowse4.setToolTipText("Browse");
		pbBrowse4.addActionListener(this);
		pbBrowse4.setEnabled(false);

		gc.gridy = gridyStart;
		gridyStart++;
		gc.gridx = 2;
		gc.weightx=0.0;
		gc.fill=GridBagConstraints.NONE;
		gb.setConstraints(pbBrowse4, gc);
		oContentPane.add(pbBrowse4);

		int nType = oItem.getNodeType();
		if (nType == ICoreConstants.MAPVIEW)
			cbTypes.setSelectedIndex(1);
		else if (nType == ICoreConstants.LISTVIEW)
			cbTypes.setSelectedIndex(2);
		else
			cbTypes.setSelectedIndex(0);

		JLabel lbl2 = new JLabel("Shortcut: ALT + ");
		lbl2.setFont(new Font("Dialog", Font.PLAIN, 12));
		gc.gridy = gridyStart;
		gc.gridx = 0;
		gb.setConstraints(lbl2, gc);
		oContentPane.add(lbl2);

		cbShortcut = new JComboBox();
        cbShortcut.setOpaque(true);
		cbShortcut.setEditable(false);
		cbShortcut.setEnabled(true);
		cbShortcut.setMaximumRowCount(11);
		cbShortcut.setFont( new Font("Dialog", Font.PLAIN, 12 ));
		cbShortcut.addItem(new String("None"));
		cbShortcut.addItem(new String("0"));
		cbShortcut.addItem(new String("1"));
		cbShortcut.addItem(new String("2"));
		cbShortcut.addItem(new String("3"));
		cbShortcut.addItem(new String("4"));
		cbShortcut.addItem(new String("5"));
		cbShortcut.addItem(new String("6"));
		cbShortcut.addItem(new String("7"));
		cbShortcut.addItem(new String("8"));
		cbShortcut.addItem(new String("9"));
		gc.gridy = gridyStart;
		gridyStart++;
		gc.gridx = 1;
		gc.gridwidth = 2;
		gb.setConstraints(cbShortcut, gc);
		oContentPane.add(cbShortcut);

		int nShortcut = oItem.getShortcut();
		if (nShortcut == -1)
			cbShortcut.setSelectedIndex(0);
		else
			cbShortcut.setSelectedIndex(nShortcut+1);

		gc.gridwidth = 1;

		JLabel lblImage = new JLabel("Node Image:");
		lblImage.setFont(new Font("Dialog", Font.PLAIN, 12));
		gc.gridy = gridyStart;
		gc.gridx = 0;
		gb.setConstraints(lblImage, gc);
		oContentPane.add(lblImage);

		String sImage = oItem.getImage();

		txtImage = new JTextField(sImage);
		txtImage.setFont(new Font("Dialog", Font.PLAIN, 12));
		txtImage.setColumns(23);
		txtImage.setMargin(new Insets(2,2,2,2));
		txtImage.setSize(txtImage.getPreferredSize());

		gc.gridy = gridyStart;
		gc.gridx = 1;
		gc.fill=GridBagConstraints.HORIZONTAL;
		gc.weightx=4.0;
		gb.setConstraints(txtImage, gc);
		oContentPane.add(txtImage);

		pbBrowse = new UIButton("./.");
		pbBrowse.setFont(new Font("Dialog", Font.BOLD, 14));
		pbBrowse.setMargin(new Insets(0,0,0,0));
		pbBrowse.setToolTipText("Browse");
		pbBrowse.addActionListener(this);
		gc.gridy = gridyStart;
		gridyStart++;
		gc.gridx = 2;
		gc.weightx=0.0;
		gc.fill=GridBagConstraints.NONE;
		gb.setConstraints(pbBrowse, gc);
		oContentPane.add(pbBrowse);

		JLabel lblPaletteImage = new JLabel("Palette Image:");
		lblPaletteImage.setFont(new Font("Dialog", Font.PLAIN, 12));
		gc.gridy = gridyStart;
		gc.gridx = 0;
		gb.setConstraints(lblPaletteImage, gc);
		oContentPane.add(lblPaletteImage);

		String sPaletteImage = oItem.getPaletteImage();

		txtPaletteImage = new JTextField(sPaletteImage);
		txtPaletteImage.setFont(new Font("Dialog", Font.PLAIN, 12));
		txtPaletteImage.setColumns(23);
		txtPaletteImage.setMargin(new Insets(2,2,2,2));
		txtPaletteImage.setSize(txtPaletteImage.getPreferredSize());
		gc.gridy = gridyStart;
		gc.gridx = 1;
		gc.fill=GridBagConstraints.HORIZONTAL;
		gc.weightx=4.0;
		gb.setConstraints(txtPaletteImage, gc);
		oContentPane.add(txtPaletteImage);

		pbBrowse2 = new UIButton("./.");
		pbBrowse2.setFont(new Font("Dialog", Font.BOLD, 14));
		pbBrowse2.setMargin(new Insets(0,0,0,0));
		pbBrowse2.setToolTipText("Browse");
		pbBrowse2.addActionListener(this);
		gc.gridy = gridyStart;
		gridyStart++;
		gc.gridx = 2;
		gc.weightx=0.0;
		gc.fill=GridBagConstraints.NONE;
		gb.setConstraints(pbBrowse2, gc);
		oContentPane.add(pbBrowse2);

		JLabel lbl3 = new JLabel("(If left blank uses node image)");
		lbl3.setFont(new Font("Dialog", Font.PLAIN, 10));
		gc.gridy = gridyStart;
		gridyStart++;
		gc.gridx = 0;
		gc.gridwidth = 3;
		gc.anchor = GridBagConstraints.CENTER;
		gc.weightx = 0;
		gb.setConstraints(lbl3, gc);
		oContentPane.add(lbl3);

		gc.anchor = GridBagConstraints.WEST;

		JLabel lblCodes = new JLabel("Tags: ");
		lblCodes.setFont(new Font("Dialog", Font.PLAIN, 12));
		gc.gridy = gridyStart;
		gridyStart++;
		gc.gridx = 0;
		gc.gridwidth = 3;
		gc.weightx = 0;
		gb.setConstraints(lblCodes, gc);
		oContentPane.add(lblCodes);

		initCodesList();
		lstCodes = new UINavList(vtCodes);
		lstCodes.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        CodeListCellRenderer codeListRenderer = new CodeListCellRenderer();
		lstCodes.setCellRenderer(codeListRenderer);

		// SELECTED THE ASSIGNED TAGS
		Vector vtTags = oItem.getTags();
		int count = vtTags.size();
		int[] sels = new int[vtCodes.size()];

		for (int i=0; i<count; i++) {
			Vector tag = (Vector)vtTags.elementAt(i);
			String name = (String)tag.elementAt(1);

			int jcount = vtCodes.size();
			for (int j=1; j<jcount; j++) {
				Code code = (Code)vtCodes.elementAt(j);
				if (name.equals(code.getName())) {
					sels[j] = j;
					j=jcount;
				}
			}
		}
		lstCodes.setSelectedIndices(sels);

		if (count == 0 && vtCodes.size() > 0)
			lstCodes.setSelectedIndex(0);

		oScrollPane = new JScrollPane(lstCodes);
		oScrollPane.setPreferredSize(new Dimension(220, 180));
		gc.gridy = gridyStart;
		gridyStart++;
		gc.gridx = 0;
		gc.fill = GridBagConstraints.BOTH;
		gb.setConstraints(oScrollPane, gc);
		oContentPane.add(oScrollPane);

		JSeparator sep = new JSeparator();
		gc.gridy = gridyStart;
		gridyStart++;
		gc.gridx = 0;
		gc.anchor = GridBagConstraints.WEST;
		gc.fill = GridBagConstraints.BOTH;
		gb.setConstraints(sep, gc);
		oContentPane.add(sep);

		gc.gridwidth=1;
		gc.fill = GridBagConstraints.NONE;

		pbSave = new UIButton("Save");
		pbSave.addActionListener(this);
		gc.gridy = gridyStart;
		gc.gridx = 0;
		gc.anchor = GridBagConstraints.CENTER;
		gb.setConstraints(pbSave, gc);
		oContentPane.add(pbSave);

		pbCancel = new UIButton("Cancel");
		pbCancel.addActionListener(this);
		gc.gridx = 1;
		gc.anchor = GridBagConstraints.EAST;
		gb.setConstraints(pbCancel, gc);
		oContentPane.add(pbCancel);

		pbHelp = new UIButton("Help");
		ProjectCompendium.APP.mainHB.enableHelpOnButton(pbHelp, "basics.stencils", ProjectCompendium.APP.mainHS);
		gc.gridx = 2;
		gc.anchor = GridBagConstraints.EAST;
		gb.setConstraints(pbHelp, gc);
		oContentPane.add(pbHelp);
	}

	/**
	 * Helper class that renders the code lists.
	 */
	private class CodeListCellRenderer extends JLabel implements ListCellRenderer {

	  	protected Border noFocusBorder;

		CodeListCellRenderer() {
        	super();
			noFocusBorder = new EmptyBorder(1, 1, 1, 1);
			setOpaque(true);
			setBorder(noFocusBorder);
		}

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

			String fulltext = "";
			if (value instanceof Code) {
				Code code = (Code)value;
				fulltext = code.getName();
			}
			else {
				fulltext = (String)value;
			}
			String text = fulltext;
			if(text.length() > 40) {
				text = text.substring(0,39);
				text += "....";
			}

			setBorder((cellHasFocus) ? UIManager.getBorder("List.focusCellHighlightBorder") : noFocusBorder);
			setToolTipText(fulltext);
			setText(text);
			return this;
		}
	}

	/**
	 * Retrieve all codes from the model.
	 */
	public void initCodesList() {
		vtCodes.removeAllElements();
		Hashtable allCodes = null;
		if (allCodes == null)
			allCodes = ProjectCompendium.APP.getModel().getCodesCheck();

		IModel model = ProjectCompendium.APP.getModel();
		PCSession session = model.getSession();

		for(Enumeration e = allCodes.elements();e.hasMoreElements();) {
			Code code = (Code)e.nextElement();
			vtCodes.addElement(code);
		}
		vtCodes = CoreUtilities.sortList(vtCodes);
		vtCodes.insertElementAt(new String("** No Tags **"), 0);
	}

	/**
	 * Listener for checkbox changes.
	 * @param e the associated ItemEvent object.
	 */
	public void itemStateChanged(ItemEvent e) {

		Object source = e.getItemSelectable();
		if (source == cbTypes) {
			int index = cbTypes.getSelectedIndex();
			if (index == 1) {
				txtBackgroundImage.setEnabled(true);
				lblBackgroundLabel.setEnabled(true);
				pbBrowse3.setEnabled(true);
				
				txtTemplate.setEnabled(true);
				lblTemplateLabel.setEnabled(true);
				pbBrowse4.setEnabled(true);				
			}
			else if (index == 2) {
				txtTemplate.setEnabled(true);
				lblTemplateLabel.setEnabled(true);
				pbBrowse4.setEnabled(true);	
			}
			else {
				txtBackgroundImage.setText("");
				txtBackgroundImage.setEnabled(false);
				lblBackgroundLabel.setEnabled(false);
				pbBrowse3.setEnabled(false);
				
				txtTemplate.setEnabled(false);
				lblTemplateLabel.setEnabled(false);
				pbBrowse4.setEnabled(false);				
				
			}
		}
	}

	/**
	 * Handle action events coming from the buttons.
	 * @param evt the associated ACtionEvent.
	 */
	public void actionPerformed(ActionEvent evt) {
		Object source = evt.getSource();

		// Handle button events
		if (source instanceof JButton) {
			if (source == pbBrowse) {
				onBrowse();
			} else if (source == pbBrowse2) {
				onBrowse2();
			} else if (source == pbBrowse3) {
				onBrowse3();
			} else if (source == pbBrowse4) {
				onBrowse4();
			} else if (source == pbSave) {
				onSave();
			} else if (source == pbCancel) {
				onCancel();
			}
		}
	}

	/**
	 * Open the file browser dialog for the user to select a node image.
	 */
	private void onBrowse() {

		JFileChooser fileDialog = new JFileChooser();
		fileDialog.setDialogTitle("Select Node image...");
		fileDialog.setDialogType(JFileChooser.OPEN_DIALOG);

		if (!UIStencilItemDialog.lastFileDialogDir.equals("")) {
			// FIX FOR MAC - NEEDS '/' ON END TO DENOTE A FOLDER
			File file = new File(UIStencilItemDialog.lastFileDialogDir+ProjectCompendium.sFS);
			if (file.exists()) {
				fileDialog.setCurrentDirectory(file);
			}
		}

		UIUtilities.centerComponent(fileDialog, oParent);
		int retval = fileDialog.showDialog(this, null);

		if (retval == JFileChooser.APPROVE_OPTION) {
        	if ((fileDialog.getSelectedFile()) != null) {

            	String fileName = fileDialog.getSelectedFile().getAbsolutePath();
				File fileDir = fileDialog.getCurrentDirectory();
				String dir = fileDir.getPath();

				if (fileName != null) {
					if ( UIImages.isImage(fileName)) {
						UIStencilItemDialog.lastFileDialogDir = dir;
						txtImage.setText(fileName);

						Dimension size = UIImages.getImageSize(fileName);
						if (size.width > UIImages.MAX_DIM || size.height > UIImages.MAX_DIM) {
							ProjectCompendium.APP.displayMessage("Warning: This image exceeds the maximum recomended image size of 96x96 pixels", "Image File");
						}
					}
					else {
						ProjectCompendium.APP.displayMessage("You must select an image file (.jpg, .gif, .png)", "Image File");
					}
				}
			}
		}
	}

	/**
	 * Open the file browser dialog for the user to select a palette image.
	 */
	private void onBrowse2() {

		JFileChooser fileDialog = new JFileChooser();
		fileDialog.setDialogTitle("Select palette image...");
		fileDialog.setDialogType(JFileChooser.OPEN_DIALOG);

		if (!UIStencilItemDialog.lastFileDialogDir.equals("")) {
			// FIX FOR MAC - NEEDS '/' ON END TO DENOTE A FOLDER
			File file = new File(UIStencilItemDialog.lastFileDialogDir+ProjectCompendium.sFS);
			if (file.exists()) {
				fileDialog.setCurrentDirectory(file);
			}
		}

		UIUtilities.centerComponent(fileDialog, oParent);
		int retval = fileDialog.showDialog(this, null);

		if (retval == JFileChooser.APPROVE_OPTION) {
        	if ((fileDialog.getSelectedFile()) != null) {

            	String fileName = fileDialog.getSelectedFile().getAbsolutePath();
				File fileDir = fileDialog.getCurrentDirectory();
				String dir = fileDir.getPath();

				if (fileName != null) {
					if ( UIImages.isImage(fileName)) {
						UIStencilItemDialog.lastFileDialogDir = dir;
						txtPaletteImage.setText(fileName);

						Dimension size = UIImages.getImageSize(fileName);
						if (size.width > UIImages.MAX_DIM || size.height > UIImages.MAX_DIM) {
							ProjectCompendium.APP.displayMessage("Warning: This image exceeds the maximum recomended palette image size of 96x96 pixels", "Palette Image File");
						}
					}
					else {
						ProjectCompendium.APP.displayMessage("You must select an image file (.jpg, .gif, .png)", "Image File");
					}
				}
			}
		}
	}

	/**
	 * Open the file browser dialog for the user to select a background image.
	 */
	private void onBrowse3() {

		JFileChooser fileDialog = new JFileChooser();
		fileDialog.setDialogTitle("Select background image...");
		fileDialog.setDialogType(JFileChooser.OPEN_DIALOG);

		if (!UIStencilItemDialog.lastFileDialogDir.equals("")) {
			// FIX FOR MAC - NEEDS '/' ON END TO DENOTE A FOLDER
			File file = new File(UIStencilItemDialog.lastFileDialogDir+ProjectCompendium.sFS);
			if (file.exists()) {
				fileDialog.setCurrentDirectory(file);
			}
		}

		UIUtilities.centerComponent(fileDialog, oParent);
		int retval = fileDialog.showDialog(this, null);

		if (retval == JFileChooser.APPROVE_OPTION) {
        	if ((fileDialog.getSelectedFile()) != null) {

            	String fileName = fileDialog.getSelectedFile().getAbsolutePath();
				File fileDir = fileDialog.getCurrentDirectory();
				String dir = fileDir.getPath();

				if (fileName != null) {
					if ( UIImages.isImage(fileName)) {
						UIStencilItemDialog.lastFileDialogDir = dir;
						txtBackgroundImage.setText(fileName);
					}
					else {
						ProjectCompendium.APP.displayMessage("You must select an image file (.jpg, .gif, .png)", "Background Image File");
					}
				}
			}
		}
	}

	/**
	 * Open the file browser dialog for the user to select a template file.
	 */
	private void onBrowse4() {

		JFileChooser fileDialog = new JFileChooser();
		fileDialog.setDialogTitle("Select template...");
		fileDialog.setDialogType(JFileChooser.OPEN_DIALOG);

		// FIX FOR MAC - NEEDS '/' ON END TO DENOTE A FOLDER
		File file = new File("Templates"+ProjectCompendium.sFS);
		if (file.exists()) {
			fileDialog.setCurrentDirectory(file);
		}

		UIUtilities.centerComponent(fileDialog, oParent);
		int retval = fileDialog.showDialog(this, null);

		if (retval == JFileChooser.APPROVE_OPTION) {
        	if ((fileDialog.getSelectedFile()) != null) {

            	String fileName = fileDialog.getSelectedFile().getAbsolutePath();
				File fileDir = fileDialog.getCurrentDirectory();
				String dir = fileDir.getPath();

				if (fileName != null) {
					String sFileCheckName = fileName.toLowerCase(); 
					if ( sFileCheckName.endsWith(".xml")) {
						UIStencilItemDialog.lastFileDialogDir = dir;
						txtTemplate.setText(fileName);
					}
					else {
						ProjectCompendium.APP.displayMessage("You must select a template file (.xml)", "Template File");
					}
				}
			}
		}
	}

	/**
	 * Open the dialog to create a new stencil set.
	 */
	public void onSave()  {
		String sLabel = txtLabel.getText();
		String sToolTip = txtToolTip.getText();

		String sImage = txtImage.getText();
		if (sImage.equals("")) {
			ProjectCompendium.APP.displayMessage("You must select a node image", "Missing Image");
			txtImage.requestFocus();
			return;
		}

		String sPaletteImage = txtPaletteImage.getText();
		String sBackgroundImage = txtBackgroundImage.getText();
		String sTemplate = txtTemplate.getText();		
		String sType = (String)cbTypes.getSelectedItem();
		String sShortcut = (String)cbShortcut.getSelectedItem();

		int nType = ICoreConstants.REFERENCE;
		if (sType.equals("Reference"))
			nType = ICoreConstants.REFERENCE;
		else if (sType.equals("Map"))
			nType = ICoreConstants.MAPVIEW;
		else if (sType.equals("List"))
			nType = ICoreConstants.LISTVIEW;


 		int [] selected = lstCodes.getSelectedIndices();
		Vector codes = new Vector(10);
		for(int i=0;i<selected.length;i++) {
			if (selected[i] != 0) {
				Vector inner = new Vector(7);
				Code newCode = (Code)vtCodes.elementAt(selected[i]);
				inner.addElement(newCode.getId());
				inner.addElement(newCode.getName());
				inner.addElement(newCode.getDescription());
				inner.addElement(newCode.getBehavior());
				inner.addElement(newCode.getAuthor());
				inner.addElement(newCode.getCreationDate());
				inner.addElement(newCode.getModificationDate());
				codes.addElement(inner);
			}
		}

		oItem.setLabel(sLabel);
		oItem.setToolTip(sToolTip);
		oItem.setNodeType(nType);
		if (!sShortcut.equals("None"))
			oItem.setShortcut((new Integer(sShortcut)).intValue());

		oItem.setNodeType(nType);
		oItem.setImage(sImage);
		oItem.setPaletteImage(sPaletteImage);
		
		//CHECK IF A BACKGROUND IMAGE HAS BEEN REMOVED AND THE FILE NEEDS REMOVING
		//String sOldBackgroundImage = oItem.getBackgroundImage();
		//if (sBackgroundImage.equals("") && !sOldBackgroundImage.equals("")) {
		//	
		//}
		
		oItem.setBackgroundImage(sBackgroundImage);
		oItem.setTemplate(sTemplate);		
		oItem.setTags(codes);

		if (sPaletteImage.equals("")) {
			oItem.setIcon(UIImages.thumbnailIcon(sImage));
		} else {
			oItem.setIcon(UIImages.thumbnailIcon(sPaletteImage));
		}

		oManager.addItem(oItem);

		onCancel();
	}

	/**
	 * Handle the enter key action. Override superclass to do nothing.
	 */
	public void onEnter() {}
}
