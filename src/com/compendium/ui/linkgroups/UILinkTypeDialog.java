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

package com.compendium.ui.linkgroups;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import com.compendium.ProjectCompendium;
import com.compendium.ui.*;
import com.compendium.ui.dialogs.*;

/**
 * UILinkTypeDialog defines the dialog that allows the user to create and manage link types.
 *
 * @author	Michelle Bachler
 */
public class UILinkTypeDialog extends UIDialog implements ActionListener, IUIConstants {

	/** The last director browsed to when looking for external references.*/
	private static String lastFileDialogDir = "";

	/** The current pane to put the dialog contents in.*/
	private Container				oContentPane 	= null;

	/** The button to close the dialog.*/
	private JButton					pbCancel 		= null;

	/** The button to save the stencil set.*/
	private JButton					pbSave 			= null;

	/** The button to open a file browser for the colour field.*/
	private JButton					pbBrowse		= null;

	/** Activates the help opeing to the appropriate section.*/
	private JButton					pbHelp		= null;

	/** The layout manager used.*/
	private	GridBagLayout 			gb 				= null;

	/** The constraints used.*/
	private	GridBagConstraints 		gc 				= null;

	/** The parent frame for this dialog.*/
	private JFrame					oParent 		= null;

	/** The stencil manager for this dialog.*/
	private UILinkGroupDialog		oManager 		= null;

	/** The counter for the gridbag layout y position.*/
	private int 					gridyStart 		= 0;

	/** The stencil set to edit / created.*/
	private UILinkType 	oType						= null;

	/** The text field to hold the link type label.*/
	private JTextField				txtName			= null;

	/** The text field to hold the link type colour.*/
	private JTextField				txtColour			= null;

	/** The text area to hold the link label.*/
	private UITextArea				txtLabel		= null;

	private Color					oColour			= Color.black;

	/**
	 * Constructor. Initializes and sets up the dialog.
	 * Used when editing a stencils set.
	 *
	 * @param parent the frame that is the parent for this dialog.
	 * @param manager the parent managing dialog.
	 * @param oType the link type to edit / create.
	 */
	public UILinkTypeDialog(JFrame parent, UILinkGroupDialog manager, UILinkType oType) {

		super(parent, true);
		oParent = parent;
		oManager = manager;
		this.oType = oType;

		setTitle("Link Type");

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

		JLabel lblLabel = new JLabel("Name");
		lblLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		gc.gridy = gridyStart;
		gc.gridx = 0;
		gb.setConstraints(lblLabel, gc);
		oContentPane.add(lblLabel);

		txtName = new JTextField(oType.getName());
		txtName.setFont(new Font("Dialog", Font.PLAIN, 12));
		txtName.setColumns(20);
		txtName.setMargin(new Insets(2,2,2,2));
		txtName.setSize(txtName.getPreferredSize());
		gc.gridy = gridyStart;
		gridyStart++;
		gc.gridx = 1;
		gc.gridwidth = 2;
		gc.fill=GridBagConstraints.HORIZONTAL;
		gb.setConstraints(txtName, gc);
		oContentPane.add(txtName);

		gc.fill=GridBagConstraints.NONE;

		JLabel lblAuto = new JLabel("Label");
		lblAuto.setFont(new Font("Dialog", Font.PLAIN, 12));
		gc.gridy = gridyStart;
		gc.gridx = 0;
		gc.gridwidth = 1;
		gb.setConstraints(lblAuto, gc);
		oContentPane.add(lblAuto);

		txtLabel = new UITextArea(30, 20);
		txtLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		txtLabel.setText(oType.getLabel());
		txtLabel.setAutoscrolls(true);
		txtLabel.addFocusListener( new FocusListener() {
        	public void focusGained(FocusEvent e) {
 				txtLabel.setCaretPosition(txtLabel.getCaretPosition());
			}
            public void focusLost(FocusEvent e) {}
		});
		JScrollPane scrollpane2 = new JScrollPane(txtLabel);
		scrollpane2.setPreferredSize(new Dimension(100,50));
		gc.gridy = gridyStart;
		gridyStart++;
		gc.gridx = 1;
		gc.gridwidth=2;
		gc.fill=GridBagConstraints.HORIZONTAL;
		gb.setConstraints(scrollpane2, gc);
		oContentPane.add(scrollpane2);

		gc.fill=GridBagConstraints.NONE;

		JLabel lblColour = new JLabel("Link Colour ");
		lblColour.setFont(new Font("Dialog", Font.PLAIN, 12));
		gc.gridy = gridyStart;
		gc.gridx = 0;
		gc.gridwidth = 1;
		gb.setConstraints(lblColour, gc);
		oContentPane.add(lblColour);

		oColour = oType.getColour();

		txtColour = new JTextField();
		txtColour.setBackground(oColour);
		txtColour.setFont(new Font("Dialog", Font.PLAIN, 12));
		txtColour.setColumns(4);
		txtColour.setEditable(false);
		txtColour.setMargin(new Insets(2,2,2,2));
		txtColour.setSize(txtColour.getPreferredSize());
		txtColour.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				int clickCount = e.getClickCount();
				if (clickCount == 2) {
					pbBrowse.doClick();
				}
			}
		});
		gc.gridx = 1;
		gc.gridwidth = 1;
		gc.fill=GridBagConstraints.HORIZONTAL;
		gb.setConstraints(txtColour, gc);
		oContentPane.add(txtColour);

		pbBrowse = new UIButton("Choose");
		pbBrowse.setToolTipText("Open the Colour chooser dialog");
		pbBrowse.addActionListener(this);
		gc.gridy = gridyStart;
		gridyStart++;
		gc.gridx = 2;
		gc.gridwidth = 1;
		gc.fill=GridBagConstraints.NONE;
		gb.setConstraints(pbBrowse, gc);
		oContentPane.add(pbBrowse);

		JSeparator sep = new JSeparator();
		gc.gridy = gridyStart;
		gridyStart++;
		gc.gridx = 0;
		gc.gridwidth = 3;
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
		gc.gridwidth = 1;
		gc.weightx=1.0;
		gc.anchor = GridBagConstraints.WEST;
		gb.setConstraints(pbSave, gc);
		oContentPane.add(pbSave);

		pbCancel = new UIButton("Cancel");
		pbCancel.addActionListener(this);
		gc.gridy = gridyStart;
		gc.gridx = 1;
		gc.gridwidth = 1;
		gc.weightx=2.0;
		gc.anchor = GridBagConstraints.EAST;
		gb.setConstraints(pbCancel, gc);
		oContentPane.add(pbCancel);

		pbHelp = new UIButton("Help");
		ProjectCompendium.APP.mainHB.enableHelpOnButton(pbHelp, "node.linkgroups", ProjectCompendium.APP.mainHS);
		gc.gridx = 2;
		gc.gridwidth=1;
		gc.weightx=1.0;
		gc.anchor = GridBagConstraints.EAST;
		gb.setConstraints(pbHelp, gc);
		oContentPane.add(pbHelp);
	}

	/**
	 * Handle action events coming from the buttons.
	 * @param evt the associated ActionEvent.
	 */
	public void actionPerformed(ActionEvent evt) {
		Object source = evt.getSource();

		// Handle button events
		if (source instanceof JButton) {
			if (source == pbBrowse) {
				onBrowse();
			}
			else if (source == pbSave) {
				onSave();
			}
			else if (source == pbCancel) {
				onCancel();
			}
		}
	}

	/**
	 * Open the color chooser dialog for the user to select a link colour.
	 */
	private void onBrowse() {

		UIColorChooserDialog dlg = new UIColorChooserDialog(ProjectCompendium.APP, oColour);
		dlg.setVisible(true);
		Color colour = dlg.getColour();
		dlg.dispose();
		if (colour != null) {
			txtColour.setBackground(colour);
			oColour = colour;
		}
	}

	/**
	 * Open the dialog to create a new stencil set.
	 */
	public void onSave()  {
		String oldName = oType.getName();
		String sName = txtName.getText();
		if (sName.equals("")) {
			ProjectCompendium.APP.displayMessage("You must give this link type a name", "No Name");
			txtName.requestFocus();
			return;
		}
		else if (!sName.equals(oldName) && oManager.checkName(sName)) {
			ProjectCompendium.APP.displayMessage("You already have a link type in this group with that name, please try again", "Duplicate Name");
			txtName.requestFocus();
			return;
		}
		String sLabel = txtLabel.getText();

		oType.setName(sName);
		oType.setColour(oColour);
		oType.setLabel(sLabel);

		oManager.addLinkType(oType);
		onCancel();
	}

	/**
	 * Handle the enter key action. Override superclass to do nothing.
	 */
	public void onEnter() {}
}
