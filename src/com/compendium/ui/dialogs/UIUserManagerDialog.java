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
import java.awt.*;
import java.io.*;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.*;
import java.sql.SQLException;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;
import javax.swing.text.Document;

import com.compendium.ProjectCompendium;

import com.compendium.ui.*;
import com.compendium.core.datamodel.*;
import com.compendium.core.db.management.*;

/**
 * This dialog manages user accounts.
 *
 * @author	Mohammed S Ali / Michelle Bachler
 */
public class UIUserManagerDialog extends UIDialog implements ActionListener, ItemListener, ListSelectionListener {

	/** The pane for the dialog's content.*/
	private Container		oContentPane		= null;

	/** The parent frame for this dialog.*/
	private JFrame			oParent				= null;

	/** The list of current users.*/
	private UINavList		lstUsers			= null;

	/** The data for the list of current users.*/
	private Vector			vtUsers				= new Vector();

	/** The button to edit the selected user's details.*/
	private UIButton			pbUpdate			= null;

	/** The button to delete the selected user.*/
	private UIButton			pbDelete			= null;

	/** The button to create a new user.*/
	private UIButton			pbNewUser			= null;

	/** The button to close this dialog.*/
	private UIButton			pbClose				= null;

	/** The button to save single user options.*/
	private UIButton			pbSave				= null;

	/** The button to cancel the dialog without saving.*/
	private UIButton			pbCancel			= null;

	/** Activates the help opeing to the appropriate section.*/
	private UIButton			pbHelp		= null;

	/** Set the current usr as the default user of the database.*/
	private	JCheckBox		defaultUser			= null;

	/** Set the current database as the default databsae.*/
	private JCheckBox		defaultDatabase		= null;

	/** Has another default database already been set?.*/
	private	boolean			allowDefaultDatabase = false;

	/** The MouseListener for the list.*/
	private MouseAdapter 	mouseClick			= null;

	/** Holds the panels for this dialog.*/
	private JTabbedPane		oTabbedPane			= null;
	
	/** The currently logged in user.*/
	private UserProfile		oCurrentUser		= null;

	/**
	 * Constructor.
	 * @param parent, the parent frame of the dialog.
	 */
	public UIUserManagerDialog(JFrame parent) {

		super(parent, true);
		oParent = parent;

		setTitle("User Manager");

		oContentPane = getContentPane();
		oContentPane.setLayout(new BorderLayout());

		oTabbedPane = new JTabbedPane();
		oTabbedPane.add(createMainPanel(), "Users");
		oTabbedPane.add(createSinglePanel(), "Single User System Options");

		oContentPane.add(oTabbedPane, BorderLayout.CENTER);
		oContentPane.add(createButtonPanel(), BorderLayout.SOUTH);
		
		oCurrentUser = ProjectCompendium.APP.getModel().getUserProfile();

		// update the users lists..
		updateUsersList();

		setResizable(false);
		pack();
	}

	/**
	 * Create the central panel of the main tabbed pane for the user list.
	 */
	private JPanel createMainPanel() {

		JPanel panel = new JPanel(new BorderLayout());
		panel.setBorder(new EmptyBorder(10,10,10,10));

		JLabel lblUsername = new JLabel("Login Name  (Author Name)");
		mouseClick = new MouseAdapter() {
		  	public void mouseClicked(MouseEvent e) {

				if(e.getClickCount() == 2) {

					int index = lstUsers.getSelectedIndex();
					UserProfile up = (UserProfile)vtUsers.elementAt(index);

					UINewUserDialog dialog = new UINewUserDialog(oParent,up);
					dialog.setVisible(true);

					//update the list
					updateUsersList();
	  			}
			}
  		};
		panel.add(lblUsername, BorderLayout.NORTH);

		// Create the list
		lstUsers = new UINavList(new DefaultListModel());
		lstUsers.setSelectionMode(ListSelectionModel.SINGLE_SELECTION );
		lstUsers.setBackground(Color.white);
		lstUsers.addMouseListener(mouseClick);
		lstUsers.addListSelectionListener(this);
		lstUsers.setCellRenderer(new UserListCellRenderer());

		// create a scroll viewer to add scroll functionality
		JScrollPane sp = new JScrollPane(lstUsers);

		panel.add(sp, BorderLayout.CENTER);

		JPanel upperButtonPanel = new JPanel();
		upperButtonPanel.setBorder(new EmptyBorder(0,0,10,0));

		// Add update button
		pbUpdate = new UIButton("Modify...");
		pbUpdate.setMnemonic(KeyEvent.VK_M);
		pbUpdate.addActionListener(this);
		upperButtonPanel.add(pbUpdate);

		// Add delete button
		pbDelete = new UIButton("Delete");
		pbDelete.setMnemonic(KeyEvent.VK_D);
		pbDelete.addActionListener(this);
		upperButtonPanel.add(pbDelete);

		// Add new user button
		pbNewUser = new UIButton("New...");
		pbNewUser.setMnemonic(KeyEvent.VK_N);
		pbNewUser.addActionListener(this);
		upperButtonPanel.add(pbNewUser);

		panel.add(upperButtonPanel, BorderLayout.SOUTH);

		return panel;
	}

	/**
	 * Create and return the button panel.
	 */
	private UIButtonPanel createButtonPanel() {

		UIButtonPanel oButtonPanel = new UIButtonPanel();

		// Add close button
		pbClose = new UIButton("Close");
		pbClose.setMnemonic(KeyEvent.VK_C);
		pbClose.addActionListener(this);
		getRootPane().setDefaultButton(pbClose);
		oButtonPanel.addButton(pbClose);

		// Add help button
		pbHelp = new UIButton("Help");
		pbHelp.setMnemonic(KeyEvent.VK_H);
		ProjectCompendium.APP.mainHB.enableHelpOnButton(pbHelp, "basics.users", ProjectCompendium.APP.mainHS);
		oButtonPanel.addHelpButton(pbHelp);

		return oButtonPanel;
	}

	/**
	 * Create the second panel for the single user options tabbedpane panel.
	 */
	private JPanel createSinglePanel() {

		JPanel panel = new JPanel(new BorderLayout());

		panel.setBorder(new EmptyBorder(10,10,10,10));
		GridBagLayout gb = new GridBagLayout();
		panel.setLayout(gb);

		int y=0;

		GridBagConstraints gc = new GridBagConstraints();
		gc.insets = new Insets(5,5,5,5);
		gc.anchor = GridBagConstraints.WEST;

		defaultUser = new JCheckBox("Set current user as default user for this project");
		defaultUser.addItemListener(this);
		defaultUser.setSelected(false);

		if ((ProjectCompendium.APP.getDefaultDatabase()).equals("") || ProjectCompendium.APP.isDefaultDatabase())
			allowDefaultDatabase = true;

		UserProfile oDefaultUser = null;
		UserProfile oUser = null;
		try {
			oDefaultUser = ProjectCompendium.APP.getServiceManager().getSystemService().getDefaultUser(ProjectCompendium.APP.getModel().getSession());
			oUser = ProjectCompendium.APP.getModel().getUserProfile();

			if (oDefaultUser != null && (oUser.getId()).equals(oDefaultUser.getId())) {
				defaultUser.setSelected(true);
				//if (allowDefaultDatabase)
				//	defaultDatabase.setEnabled(true);
			}
		}
		catch(Exception io) {}

		gc.gridy = y;
		gc.gridwidth=0;
		y++;
		gb.setConstraints(defaultUser, gc);
		panel.add(defaultUser);

		if (allowDefaultDatabase) {
			defaultDatabase = new JCheckBox("Set current database as default project for Compendium");

			if (ProjectCompendium.APP.isDefaultDatabase()) {
				defaultDatabase.setSelected(true);
			}
			gc.gridy = y;
			y++;
			gb.setConstraints(defaultDatabase, gc);
			panel.add(defaultDatabase);
		}
		else {
			JLabel label = new JLabel("Your current default project is: "+ProjectCompendium.APP.getDefaultDatabase());
			gc.gridy = y;
			y++;
			gb.setConstraints(label, gc);
			panel.add(label);
		}

		JLabel spacer = new JLabel(" ");
		gc.gridy = y;
		y++;
		gc.weighty = 2;
		gb.setConstraints(spacer, gc);
		panel.add(spacer);

		JPanel buttonpanel = new JPanel();

		pbSave = new UIButton("Save");
		pbSave.addActionListener(this);
		buttonpanel.add(pbSave);

		gc.gridy = y;
		y++;
		gc.anchor = GridBagConstraints.SOUTH;
		gb.setConstraints(buttonpanel, gc);
		panel.add(buttonpanel);

		return panel;
	}

	/**
	 * Listener for checkbox changes.
	 * @param e, the associated ItemEvent object.
	 */
	public void itemStateChanged(ItemEvent e) {

		Object source = e.getItemSelectable();
		if (source == defaultUser) {
			if (!defaultUser.isSelected()) {
				if (allowDefaultDatabase) {
					defaultDatabase.setSelected(false);
					defaultDatabase.setEnabled(false);
				}
			}
			else {
				if (allowDefaultDatabase) {
					defaultDatabase.setEnabled(true);
					defaultDatabase.setSelected(false);
				}
			}
		}
	}

	/**
	 * Handles button push events.
	 * @param e, the associated ActionEvent object.
	 */
	public void actionPerformed(ActionEvent evt) {
		Object source = evt.getSource();
		// Handle button events
		if (source instanceof JButton) {

			if (source == pbUpdate) {
				onUpdate();
			}
			else if (source == pbSave) {
				onSave();
				onCancel();
			}
			else if (source == pbDelete) {
				onDelete();
			}
			else if (source == pbNewUser) {
				onNewUser();
			}
			else if (source == pbClose || source == pbCancel) {
				onCancel();
			}
		}
	}
	
	/** 
	 * Listens for changes to the user list selection.
	 */
	public void valueChanged(ListSelectionEvent e) {
		int index = lstUsers.getSelectedIndex();
		if (index > -1) {
			UserProfile up = (UserProfile)vtUsers.elementAt(index);
			if (up.getId().equals(oCurrentUser.getId())) {
				pbDelete.setEnabled(false);
			} else {
				pbDelete.setEnabled(true);
			}
		}
	}

	/**
	 * Update the user list after a change.
	 */
	private void updateUsersList() {

		((DefaultListModel)lstUsers.getModel()).removeAllElements();
		vtUsers.removeAllElements();

		IModel model = ProjectCompendium.APP.getModel();
		String modelName = model.getModelName();
		String userID = model.getUserProfile().getId() ;

		try {
			for(Enumeration e = (model.getUserService().getUsers(modelName, userID)).elements();e.hasMoreElements();) {
				UserProfile up = (UserProfile)e.nextElement();
				((DefaultListModel)lstUsers.getModel()).addElement(up);
				vtUsers.addElement(up);
			}
		}
		catch(SQLException ex) {
			ProjectCompendium.APP.displayError("Exception: " + ex.getMessage());
		}

		lstUsers.setSelectedIndex(0);
	}

	/**
	 * Handle the save options request.
	 */
	public void onSave() {

		if (defaultUser.isSelected()) {
			IModel model = ProjectCompendium.APP.getModel();
			ProjectCompendium.APP.setDefaultUser( model.getUserProfile().getId() );
		}

		if (defaultDatabase != null) {
			if (defaultDatabase.isSelected() && defaultUser.isSelected())
				ProjectCompendium.APP.setDefaultDatabase(ProjectCompendium.APP.sFriendlyName);
			else
				ProjectCompendium.APP.setDefaultDatabase(new String(""));
		}
	}

	/**
	 * Handle the update action, launches the UINewUserDialog with selected user profile.
	 */
	public void onUpdate() {

		int index = lstUsers.getSelectedIndex();
		UserProfile up = (UserProfile)vtUsers.elementAt(index);

		UINewUserDialog dialog = new UINewUserDialog(oParent,up);
		UIUtilities.centerComponent(dialog, this);
		dialog.setVisible(true);

		//update the list
		updateUsersList();
		
		try {
			ProjectCompendium.APP.getModel().loadUsers();
		} catch (Exception e) {
			System.out.println("Unable to reload User List");
		}		
	}

	/**
	 * Handle the delete action. Deletes the user profile after a warning message.
	 */
	public void onDelete() {

		String userName = "";
		boolean deleted = false;

		int index = lstUsers.getSelectedIndex();
		UserProfile up = (UserProfile)vtUsers.elementAt(index);
		String userId = up.getId();

		//cannot delete the last administrator so check - or yourself!

		if (up.isAdministrator()) {

			if (ProjectCompendium.APP.getModel().getUserProfile().getId().equals(userId)) {
				JOptionPane oOptionPane = new JOptionPane("Sorry, you cannot delete yourself.");
				JDialog oDialog = oOptionPane.createDialog(oContentPane,"User Manager Error..");
				oDialog.setModal(true);
				oDialog.setVisible(true);
				return;				
			}
			
			// THIS CHECK IS NOW REDUNDANT?
			// is this the last administrator account?
			boolean isLast = true;
			int count = vtUsers.size();
			for (int i=0; i< count; i++) {
				UserProfile up2 = (UserProfile)vtUsers.elementAt(i);
				if (!up.equals(up2) && up2.isAdministrator()) {
					isLast = false;
					break;
				}
			}

			if (isLast) {
				JOptionPane oOptionPane = new JOptionPane("Sorry, you cannot delete the last administrator account.");
				JDialog oDialog = oOptionPane.createDialog(oContentPane,"User Manager Error..");
				oDialog.setModal(true);
				oDialog.setVisible(true);
				return;
			}
		}

		userName = up.getUserName();
		String homeviewID = up.getHomeView().getId();

		int response = JOptionPane.showConfirmDialog(oParent, "Do you really want to delete " + userName + " ?",
														"Delete User", JOptionPane.YES_NO_OPTION);

		if (response == JOptionPane.YES_OPTION) {
			PCSession session = ProjectCompendium.APP.getModel().getSession();

			try {
				// DELETE THE USER ENTRY
				deleted = ProjectCompendium.APP.getModel().getUserService().deleteUserProfile(session, userId);

				if (deleted) {
					// REMOVE THEIR HOME VIEW AND THE ASSOCIATED VIEWPROPERTIES, NODEVIEW, LINK ENTRIES ETC
					ProjectCompendium.APP.getModel().getNodeService().purgeHomeView(session, homeviewID);
				}

			}
			catch(SQLException ex) {
				ex.printStackTrace();
				ProjectCompendium.APP.displayError("Exception:" + ex.getMessage());
			}

			if(deleted) {

				//int response = JOptionPane.showConfirmDialog(oParent, "Do you want to also delete the code " + userName + " from DB?",
				//									"Delete User", JOptionPane.YES_NO_OPTION);

				//if (response == JOptionPane.YES_OPTION) {
				//		IModel model = ProjectCompendium.APP.getModel();

						//remove code from the DB
						//MB: this needs some thought as codes are shared
						// BESIDE THESE FUNCTIONS WORK ON CODENAME NOT USERNAME
						// SO THEY WHERE DOING NOTHING ANYWAY
						/*
						try {
							boolean deleted2 = model.getCodeService().deleteCode(session, userName);
						}
						catch(SQLException ex) {
							ProjectCompendium.APP.displayError("Exception:" + ex.getMessage());
						}
						model.removeCodebyName(userName);
						*/
				//}
				ProjectCompendium.APP.displayMessage("User " + userName + " has been deleted", "Delete User");
			}

			//update the list since user removed from db
			updateUsersList();
		}
	}

	/**
	 * Handle the new user action.
	 */
	public void onNewUser() {
		UINewUserDialog dialog = new UINewUserDialog(oParent);
		UIUtilities.centerComponent(dialog, this);
		dialog.setVisible(true);

		//update the list since a new user added to the list
		updateUsersList();
		
		try {
			ProjectCompendium.APP.getModel().loadUsers();
		} catch (Exception e) {
			System.out.println("Unable to reload User List");
		}
	}
	
	/**
	 * Helper class to render the elements of the list.
	 */
	private class UserListCellRenderer extends JLabel implements ListCellRenderer {

		/** Serial versio n id.*/
		private static final long serialVersionUID = -3969818770418445027L;

		/** Unfocused border for the label.*/
		protected Border noFocusBorder;

		UserListCellRenderer() {
			super();
			setOpaque(true);
			noFocusBorder = new EmptyBorder(1, 1, 1, 1);
			setBorder(noFocusBorder);
		}

		public Component getListCellRendererComponent(
			JList list,
			Object value,
			int modelIndex,
			boolean isSelected,
			boolean cellHasFocus)
			{
			UserProfile up = (UserProfile)value;

			if (isSelected) {
				setBackground(list.getSelectionBackground());

				if (up.getId().equals(oCurrentUser.getId()))
					setForeground(IUIConstants.DEFAULT_COLOR);
				else
					setForeground(list.getSelectionForeground());
			}
			else {
				setBackground(list.getBackground());
				if (up.getId().equals(oCurrentUser.getId()))
					setForeground(IUIConstants.DEFAULT_COLOR);
				else
					setForeground(list.getForeground());

			}
			
			ImageIcon img = null;
			img = UIImages.get(IUIConstants.NEW_ICON);

			String loginName = up.getLoginName();
			String authorName = up.getUserName();
			if(authorName.length() > 20) {
				authorName = authorName.substring(0,19);
				authorName += "....";
			}
	
			String displayText = loginName + "  (" + authorName + ")";
			setText(displayText);
			setIcon(img);
			this.setHorizontalAlignment(SwingConstants.LEFT);
			setBorder((cellHasFocus) ? UIManager.getBorder("List.focusCellHighlightBorder") : noFocusBorder);
			return this;
		}
	}	
}
