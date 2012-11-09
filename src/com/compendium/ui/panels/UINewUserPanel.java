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

package com.compendium.ui.panels;

import java.util.*;
import java.io.*;
import java.sql.SQLException;

import java.awt.*;
import java.awt.Container;
import java.awt.Color;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;
import javax.swing.text.Document;

import com.compendium.core.*;
import com.compendium.core.datamodel.*;
import com.compendium.core.datamodel.services.IViewService;
import com.compendium.ProjectCompendium;
import com.compendium.ui.IUIConstants;
import com.compendium.ui.UIImages;

/**
 * The panel to create a new user.
 *
 * @author	Michelle Bachler
 */
// ORIGIANLLY THE BASE FOR THIS CODE CAME FROM UINewUserDialog
public class UINewUserPanel extends JPanel {

	/** The field to hold the user name.*/
	public JTextField		txtUserName		= null;

	/** The field to use the author name.*/
	public JTextField		txtAuthorName	= null;

	/** The field to hold the the user description.*/
	public JTextField		txtDesc			= null;

	/** The field to hold the user password.*/
	public JPasswordField	pfPassword		= null;

	/** The field to hold the user confirmation password.*/
	public JPasswordField	pfConfPassword	= null;

	/** Indicates this user is an administrator.*/
	private JRadioButton	rbAdminYes		= null;

	/** Indicates this use is not an administrator.*/
	private JRadioButton	rbAdminNo		= null;

	/** The UserProfile object associated with this user.*/
	private UserProfile		oUserProfileUpdate = null;

	/** Is this user automatically an adminiatrtor.*/
	private boolean autoAdmin = false;

	/**
	 * Constructors
	 */
	public UINewUserPanel() {
		drawPanel();
	}

	/**
	 * Constructors, takes whether the account is automatically an administrator or not.
	 */
	public UINewUserPanel(boolean autoAdmin) {
		this.autoAdmin = autoAdmin;
		drawPanel();
	}

	/**
	 * Constructors.
	 * @param up com.compendium.core.datamodel.UserProfile, the UserProfile of the user being edited.
	 */
	public UINewUserPanel(UserProfile up) {
		oUserProfileUpdate = up;
		drawPanel();
		setUserProfile(up);
	}

	/**
	 * Draw the contetns of the panel.
	 */
	public void drawPanel() {

		setBorder(new EmptyBorder(10,10,10,10));

		GridBagLayout gb = new GridBagLayout();
		GridBagConstraints gc = new GridBagConstraints();
		setLayout(gb);

		gc.anchor = GridBagConstraints.WEST;
		gc.insets = new Insets(3,3,3,3);

		int y=0;

		JLabel lblAuthor = new JLabel("Author Name: * ");
		gc.gridy = y;
		gb.setConstraints(lblAuthor, gc);
		add(lblAuthor);

		txtAuthorName = new JTextField("");
		txtAuthorName.setColumns(20);
		lblAuthor.setLabelFor(txtAuthorName);
		gc.gridy = y;
		y++;
		gb.setConstraints(txtAuthorName, gc);
		add(txtAuthorName);

		JLabel lblDesc = new JLabel("Description:");
		gc.gridy = y;
		gb.setConstraints(lblDesc, gc);
		add(lblDesc);

		txtDesc = new JTextField("");
		txtDesc.setColumns(20);
		lblDesc.setLabelFor(txtDesc);
		gc.gridy = y;
		y++;
		gb.setConstraints(txtDesc, gc);
		add(txtDesc);

		JLabel lblLabel = new JLabel("Login Name: * ");
		gc.gridy = y;
		gb.setConstraints(lblLabel, gc);
		add(lblLabel);

		txtUserName = new JTextField("");
		txtUserName.setColumns(20);
		lblLabel.setLabelFor(txtUserName);
		gc.gridy = y;
		y++;
		gb.setConstraints(txtUserName, gc);
		add(txtUserName);

		JLabel lblPassword = new JLabel("Password: * ");
		gc.gridy = y;
		gb.setConstraints(lblPassword, gc);
		add(lblPassword);

		pfPassword = new JPasswordField("");
		pfPassword.setColumns(20);
		lblPassword.setLabelFor(pfPassword);
		gc.gridy = y;
		y++;
		gb.setConstraints(pfPassword, gc);
		add(pfPassword);

		JLabel lblConfPassword = new JLabel("Confirm Password: * ");
		gc.gridy = y;
		gb.setConstraints(lblConfPassword , gc);
		add(lblConfPassword);

		pfConfPassword = new JPasswordField("");
		pfConfPassword.setColumns(20);
		lblConfPassword.setLabelFor(pfConfPassword);
		gc.gridy = y;
		y++;
		gb.setConstraints(pfConfPassword, gc);
		add(pfConfPassword);

		//Radio button for the User to have Admin priveldges
		JLabel lblAdmin = new JLabel("Administrator:");
		gc.gridy = y;
		gb.setConstraints(lblAdmin, gc);

		if (!autoAdmin)
			add(lblAdmin);

		JPanel panel = new JPanel();
		rbAdminYes = new JRadioButton("Yes");

		if (!autoAdmin)
			panel.add(rbAdminYes);
		else
			rbAdminYes.setSelected(true);


		rbAdminNo = new JRadioButton("No");
		rbAdminNo.setSelected(true);

		if (!autoAdmin)
			panel.add(rbAdminNo);

		gc.gridy = y;
		y++;
		gb.setConstraints(panel, gc);
		add(panel);

		ButtonGroup rgGroup = new ButtonGroup();
		rgGroup.add(rbAdminYes);
		rgGroup.add(rbAdminNo);
	}

	/**
	 * Set the UserProfile for this panel to use.
	 * @param up com.compendium.core.datamodel.UserProfile, the profile for this panel to use.
	 */
	private void setUserProfile(UserProfile up) {

		oUserProfileUpdate = up;
		txtUserName.setText(up.getLoginName());
		txtAuthorName.setText(up.getUserName());
		txtDesc.setText(up.getUserDescription());
		pfPassword.setText(up.getPassword());
		pfConfPassword.setText(up.getPassword());

		boolean admin = up.isAdministrator();
		if(!admin) {
			rbAdminYes.setSelected(false);
			rbAdminNo.setSelected(true);
		}
		if(admin) {
			rbAdminYes.setSelected(true);
			rbAdminNo.setSelected(false);
		}
	}


	/**
	 * Return the UserProfile for a new user created based on the data currently in this panel.
	 * @return com.compendium.core.datamodel.UserProfile, the new user created.
	 */
	public UserProfile getNewUserData() {

		UserProfile newUp = new UserProfile("-1", ICoreConstants.WRITEVIEWNODE,
											txtUserName.getText(),
											txtAuthorName.getText(),
											new String(pfPassword.getPassword()),
											txtDesc.getText(),
											null,
											rbAdminYes.isSelected());

		return newUp;
	}

	/**
	 * Test the user data entered in this panel is valid.
	 */
	public boolean testUserData() {

		boolean bError = false;
		String sErrorString = "";

		String passwordString = new String(pfPassword.getPassword());
		String confirmString = new String(pfConfPassword.getPassword());

		//match the passwords and create a user by the given param and groups
		if(txtUserName.getText().length() < 1) {
			bError = true;
			sErrorString = "Please give a valid Login Name (Required for Login ID)";
		}
		else if(txtAuthorName.getText().length() < 1)	{
			bError = true;
			sErrorString = "Please give a valid Author Name (Required for Authoring PC)";
		}
		else if(!passwordString.equals(confirmString)) {
			bError = true;
			sErrorString = "'Password' and 'Confirm Password' fields don't match!";
		}
		else if( passwordString.length() < 1) {
			bError = true;
			sErrorString = "You must enter at least one character for a password";
			//sErrorString = "'Password' field length should be more than five characters!";
		}
		else if(confirmString.length() < 1) {
			bError = true;
			sErrorString = "You must enter at least one character for a password confirmation";
			//sErrorString = "'Confirm Password' field length should be more than five characters!";
		}

		if(bError) {
			ProjectCompendium.APP.displayError(sErrorString, "Error Messages (New User)");
			return false;
		}
		else {
			return  true;
		}
	}

	/**
	 * Add a new user to the database and return if successful.
	 * @return boolean, true if the new user was added to the database, else false.
	 */
	public boolean addNewUser() {

		if (!testUserData()) {
			return false;
		}
		else {
			return createNewUser(txtAuthorName.getText(), txtUserName.getText(),
									txtDesc.getText(), new String(pfPassword.getPassword()), rbAdminYes.isSelected());
		}
	}

	/**
	 * Create a new suer from the given data.
	 *
	 * @param author, the author name for the user.
	 * @param login, the login name for the user.
	 * @param desc, the description of the user.
	 * @param password, the password for the user.
	 * @param isAdministrator, true if the user is an administrator, else false.
	 * @return boolean, true if the new user was successfully created, else false.
	 */
	public boolean createNewUser(String author, String login, String desc, String password, boolean isAdministrator) {

		UserProfile up = null;
		Date date = new Date();
		View view = null;
		View oInboxNode = null;
		String userId = "";

		//check if the user profile is being created or an already present one is being updated
		if(oUserProfileUpdate != null) {
			userId = oUserProfileUpdate.getId();
			view = oUserProfileUpdate.getHomeView();
		}
		else {
			userId = ProjectCompendium.APP.getModel().getUniqueID();
		}
		
		Model oModel = (Model)ProjectCompendium.APP.getModel();

		/////////////////////////////////////////////////////////////////////////////////
		//Begin: create a home view for the user if the user doesnt have one (new user)
		/////////////////////////////////////////////////////////////////////////////////
		if(view == null) {
			try {
				view = (View)oModel.getNodeService().createNode(ProjectCompendium.APP.getModel().getSession(),
																				ProjectCompendium.APP.getModel().getUniqueID(),
																				ICoreConstants.MAPVIEW,
																				"",
																				"",
																				ICoreConstants.WRITEVIEWNODE,
																				ICoreConstants.READSTATE, 
																				author,
																				"Home Window",
																				"Home Window of " + txtAuthorName.getText(),
																				date,
																				date
																				);
				// add new user to list of Codes - bz
				PCSession session = ProjectCompendium.APP.getModel().getSession();
				String codeauthor = oModel.getUserProfile().getUserName();
				Date creationDate = new Date();
				Date modificationDate = creationDate;
				String description = "No Description";
				String behavior = "No Behavior";
				String name = author;
				String codeId = oModel.getUniqueID();
				//add to the DB
				Code code = oModel.getCodeService().createCode(session, codeId, codeauthor, creationDate, modificationDate, name, description, behavior);
				oModel.addCode(code);
				
				// add dropbox
				if (view != null) {
					String sLinkViewID = "";
					sLinkViewID = oModel.getUniqueID();
					oInboxNode = (View)oModel.getNodeService().createNode(oModel.getSession(),
							sLinkViewID,
							ICoreConstants.LISTVIEW,
							"",
							"",
							ICoreConstants.WRITEVIEWNODE,
							ICoreConstants.READSTATE,
							author,
							"Inbox",
							"Inbox of " + author,
							date,
							date
							);
								
					oInboxNode.initialize(oModel.getSession(), oModel);					
			  		IViewService vs = oModel.getViewService() ;
					NodePosition oLinkPos = vs.addMemberNode(oModel.getSession(), view, (NodeSummary)oInboxNode, 
							0, 75, date, date,  false, false, false, true, false, false, 
							oModel.labelWrapWidth, oModel.fontsize, oModel.fontface, 
							oModel.fontstyle, oModel.FOREGROUND_DEFAULT.getRGB(), oModel.BACKGROUND_DEFAULT.getRGB());
					oLinkPos.initialize(oModel.getSession(),oModel);				
					oInboxNode.setSource("", CoreUtilities.unixPath(UIImages.getPathString(IUIConstants.INBOX)), author);
				}
			}
			catch (Exception e) {
				ProjectCompendium.APP.displayError("(UINewUserPanel.createNewUser)\n\n"+e.getMessage());
			}
		}

		////////////////////////////////////////////////////////
		//End: create a home view for the user
		////////////////////////////////////////////////////////

		String homeViewId = view.getId();		
		String linkViewId = "";
		if (oInboxNode != null) {
			linkViewId = oInboxNode.getId();
		} else {
			linkViewId = ProjectCompendium.APP.getInBoxID();
		}
		//add the user to the project
		try {
			up = ProjectCompendium.APP.getModel().getUserService().insertUserProfile(ProjectCompendium.APP.getModel().getSession(),
															 userId, 					// userid
															 oModel.getUserProfile().getUserName(), // String the current author
															 date, 						// creation date
															 date, 						// modification date
															 login,						// String loginName
															 author,					// String userName
															 password,					// String password
															 desc,						// String userDescription
															 homeViewId,				// String homeViewId
															 isAdministrator,			// boolean isAdministrator
															 linkViewId);				// the user's link view (InBox).
		    if(up == null) {
				String prob = "Cannot create user (check if user already exists)";
				ProjectCompendium.APP.displayError(prob, "Information (Operation Failed)");
			}
			else {
				return true;
			}
		}
		catch(Exception ex) {
			ProjectCompendium.APP.displayError("(UINewUserPanel.createNewUser B)\n\n"+ex.getMessage());
		}
		return false;
	}
}
