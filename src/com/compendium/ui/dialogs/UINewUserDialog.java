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

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import com.compendium.LanguageProperties;
import com.compendium.ProjectCompendium;
import com.compendium.ui.*;
import com.compendium.ui.movie.UIMovieMapViewFrame;
import com.compendium.ui.movie.UIMovieMapViewPane;
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


	/**
	 * Constructor. Loads the appropriate panel.
	 * @param parent, the parent frame for this dialog.
	 */
	public UINewUserDialog(JFrame parent) {

		super(parent, true);
		setResizable(false);
		setTitle(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UINewUserDialog.newUsertitle")); //$NON-NLS-1$

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
		setResizable(false);
		setTitle(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UINewUserDialog.modifyUserTitle")); //$NON-NLS-1$

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

		pbOK = new UIButton(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UINewUserDialog.okButton")); //$NON-NLS-1$
		pbOK.setMnemonic(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UINewUserDialog.okButtonMnemonic").charAt(0));
		pbOK.addActionListener(this);
		getRootPane().setDefaultButton(pbOK);
		oButtonPanel.addButton(pbOK);

		pbCancel = new UIButton(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UINewUserDialog.cancelButton")); //$NON-NLS-1$
		pbCancel.setMnemonic(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UINewUserDialog.cancelButtonMnemonic").charAt(0));
		pbCancel.addActionListener(this);
		oButtonPanel.addButton(pbCancel);

		pbHelp = new UIButton(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UINewUserDialog.helpButton")); //$NON-NLS-1$
		pbHelp.setMnemonic(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UINewUserDialog.helpButtonMnemonic").charAt(0));
		ProjectCompendium.APP.mainHB.enableHelpOnButton(pbHelp, "basics.users", ProjectCompendium.APP.mainHS); //$NON-NLS-1$
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
