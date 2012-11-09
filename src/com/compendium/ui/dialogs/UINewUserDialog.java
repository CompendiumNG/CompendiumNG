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

package com.compendium.ui.dialogs;

import java.util.*;
import java.io.*;

import java.awt.*;
import java.awt.Container;
import java.awt.Color;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.Document;

import com.compendium.ProjectCompendium;
import com.compendium.ui.*;
import com.compendium.ui.panels.*;
import com.compendium.core.datamodel.*;

/**
 * The dialog to add a new user to the current database.
 *
 * @author	Mohammed S Ali / Michelle Bachler
 */
public class UINewUserDialog extends UIDialog implements ActionListener {

	/** The pane to add the dialog content to.*/
	private Container		oContentPane		= null;

	/** The parent frame for this dialog.*/
	private JFrame			oParent				= null;

	/** The button to assign a user to a group - NOT USED AT PRESENT.*/
	public UIButton			pbGroup				= null;

	/** The button to add a new user.*/
	public UIButton			pbOK				= null;

	/** The button to cancel the dialog without adding a new user.*/
	public UIButton			pbCancel			= null;

	/** The button to open the relevant help.*/
	public UIButton			pbHelp				= null;

	/** The panel with the fields and labels etc, for adding a new user.*/
	private	UINewUserPanel  userPanel			= null;

	/** The UserProfile of the new user.*/
	private UserProfile		oUserProfileUpdate 	= null;


	/**
	 * Constructor. Loads the appropriate panel.
	 * @param parent, the parent frame for this dialog.
	 */
	public UINewUserDialog(JFrame parent) {

		super(parent, true);
		oParent = parent;

		setResizable(false);
		setTitle("New User");

		oContentPane = getContentPane();

		userPanel = new UINewUserPanel();

		oContentPane.setLayout(new BorderLayout());
		oContentPane.add(userPanel, BorderLayout.CENTER);
		oContentPane.add(createButtonPanel(), BorderLayout.SOUTH);

		pack();
	}

	/**
	 * Constructor. Loads the appropriate panel.
	 * @param parent, the parent frame for this dialog.
	 * @param up com.compendium.core.datamodel.UserProfile, the profile of the user to add.
	 */
	public UINewUserDialog(JFrame parent, UserProfile up){

		super(parent, true);
		oParent = parent;

		setResizable(false);
		setTitle("Modify User");

		oContentPane = getContentPane();

		userPanel = new UINewUserPanel(up);

		oContentPane.setLayout(new BorderLayout());
		oContentPane.add(userPanel, BorderLayout.CENTER);
		oContentPane.add(createButtonPanel(), BorderLayout.SOUTH);

		pack();
	}

	/**
	 * Create the panel with the main dialog buttons.
	 */
	public UIButtonPanel createButtonPanel() {

		UIButtonPanel oButtonPanel = new UIButtonPanel();

		pbOK = new UIButton("OK");
		pbOK.setMnemonic(KeyEvent.VK_O);
		pbOK.addActionListener(this);
		getRootPane().setDefaultButton(pbOK);
		oButtonPanel.addButton(pbOK);

		pbCancel = new UIButton("Cancel");
		pbCancel.setMnemonic(KeyEvent.VK_C);
		pbCancel.addActionListener(this);
		oButtonPanel.addButton(pbCancel);

		pbHelp = new UIButton("Help");
		pbHelp.setMnemonic(KeyEvent.VK_H);
		ProjectCompendium.APP.mainHB.enableHelpOnButton(pbHelp, "basics.users", ProjectCompendium.APP.mainHS);
		oButtonPanel.addHelpButton(pbHelp);

		return oButtonPanel;
	}

	/**
	 * Handles a button push action.
	 * @param evt, the associated ActionEvent.
	 */
	public void actionPerformed(ActionEvent evt) {
		Object source = evt.getSource();
		if (source == pbOK)
			onUpdate();
		else if (source == pbGroup)
			onGroup();
		else if (source == pbCancel)
			onCancel();
	}

	/**
	 * Used to assign a user to a group - DOES NOTHING AT PRESENT.
	 */
	public void onGroup() {
		//	UIGroupDialog dialog = new UIGroupDialog(oParent,this, txtUserName.getText());
		//	dialog.setVisible(true);
	}

	/**
	 * Add a new user to the database.
	 */
	public void onUpdate() {
		if (userPanel.addNewUser()) {
			onCancel();
		}
	}
}
