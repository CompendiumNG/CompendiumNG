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

package com.compendium.ui.panels;

import java.util.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;

import com.compendium.core.*;
import com.compendium.core.datamodel.*;
import com.compendium.core.datamodel.services.IViewService;
import com.compendium.LanguageProperties;
import com.compendium.ProjectCompendium;
import com.compendium.SystemProperties;
import com.compendium.ui.IUIConstants;
import com.compendium.ui.UIImages;
import com.compendium.ui.FormatProperties;

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
	
	/** Indicates this user ID is Active.*/
	private JRadioButton	rbActiveYes		= null;

	/** Indicates this user ID is Inactive.*/
	private JRadioButton	rbActiveNo		= null;

	/** The UserProfile object associated with this user.*/
	private UserProfile		oUserProfileUpdate = null;

	/** Is this user automatically an adminiatrtor.*/
	private boolean autoAdmin = false;

	/** Whether to draw the simple form or the complex one.*/
	private boolean			drawSimpleForm = false;

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
		drawPanel();
		setUserProfile(up);
	}

	/**
	 * Draw the contents of the panel.
	 */
	public void drawPanel() {

		if (!ProjectCompendium.APP.projectsExist() && SystemProperties.createDefaultProject) {
			drawSimpleForm = true;
		} else {
			drawSimpleForm = false;
		}

		setBorder(new EmptyBorder(10,10,10,10));

		GridBagLayout gb = new GridBagLayout();
		GridBagConstraints gc = new GridBagConstraints();
		setLayout(gb);

		gc.anchor = GridBagConstraints.WEST;
		gc.insets = new Insets(3,3,3,3);

		int y=0;

		String sAuthorLabel = LanguageProperties.getString(LanguageProperties.PANELS_BUNDLE, "UINewUserPanel.userName"); //$NON-NLS-1$
		if (drawSimpleForm) {
			sAuthorLabel = LanguageProperties.getString(LanguageProperties.PANELS_BUNDLE, "UINewUserPanel.fullName"); //$NON-NLS-1$
		}
		JLabel lblAuthor = new JLabel(sAuthorLabel+": * ");
		gc.gridy = y;
		gb.setConstraints(lblAuthor, gc);
		add(lblAuthor);

		txtAuthorName = new JTextField(""); //$NON-NLS-1$
		txtAuthorName.setColumns(20);
		lblAuthor.setLabelFor(txtAuthorName);
		gc.gridy = y;
		y++;
		gb.setConstraints(txtAuthorName, gc);
		add(txtAuthorName);

		if (!drawSimpleForm) {
			JLabel lblDesc = new JLabel(LanguageProperties.getString(LanguageProperties.PANELS_BUNDLE, "UINewUserPanel.description")+":"); //$NON-NLS-1$
			gc.gridy = y;
			gb.setConstraints(lblDesc, gc);
			add(lblDesc);

			txtDesc = new JTextField(""); //$NON-NLS-1$
			txtDesc.setColumns(20);
			lblDesc.setLabelFor(txtDesc);
			gc.gridy = y;
			y++;
			gb.setConstraints(txtDesc, gc);
			add(txtDesc);

			JLabel lblLabel = new JLabel(LanguageProperties.getString(LanguageProperties.PANELS_BUNDLE, "UINewUserPanel.loginName")+": * "); //$NON-NLS-1$
			gc.gridy = y;
			gb.setConstraints(lblLabel, gc);
			add(lblLabel);

			txtUserName = new JTextField(""); //$NON-NLS-1$
			txtUserName.setColumns(20);
			lblLabel.setLabelFor(txtUserName);
			gc.gridy = y;
			y++;
			gb.setConstraints(txtUserName, gc);
			add(txtUserName);

			JLabel lblPassword = new JLabel(LanguageProperties.getString(LanguageProperties.PANELS_BUNDLE, "UINewUserPanel.password")+": * "); //$NON-NLS-1$
			gc.gridy = y;
			gb.setConstraints(lblPassword, gc);
			add(lblPassword);

			pfPassword = new JPasswordField(""); //$NON-NLS-1$
			pfPassword.setColumns(20);
			lblPassword.setLabelFor(pfPassword);
			gc.gridy = y;
			y++;
			gb.setConstraints(pfPassword, gc);
			add(pfPassword);

			JLabel lblConfPassword = new JLabel(LanguageProperties.getString(LanguageProperties.PANELS_BUNDLE, "UINewUserPanel.condirmPassword")+": * "); //$NON-NLS-1$
			gc.gridy = y;
			gb.setConstraints(lblConfPassword , gc);
			add(lblConfPassword);

			pfConfPassword = new JPasswordField(""); //$NON-NLS-1$
			pfConfPassword.setColumns(20);
			lblConfPassword.setLabelFor(pfConfPassword);
			gc.gridy = y;
			y++;
			gb.setConstraints(pfConfPassword, gc);
			add(pfConfPassword);

			//Radio button for the User to have Admin priveldges
			JLabel lblAdmin = new JLabel(LanguageProperties.getString(LanguageProperties.PANELS_BUNDLE, "UINewUserPanel.administrator")+":"); //$NON-NLS-1$
			gc.gridy = y;
			gb.setConstraints(lblAdmin, gc);

			if (!autoAdmin)
				add(lblAdmin);

			JPanel panel = new JPanel();
			rbAdminYes = new JRadioButton(LanguageProperties.getString(LanguageProperties.PANELS_BUNDLE, "UINewUserPanel.yes")); //$NON-NLS-1$

			if (!autoAdmin)
				panel.add(rbAdminYes);
			else
				rbAdminYes.setSelected(true);


			rbAdminNo = new JRadioButton(LanguageProperties.getString(LanguageProperties.PANELS_BUNDLE, "UINewUserPanel.no")); //$NON-NLS-1$
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
		
			//Radio button for the User Active/Inactive status
			JLabel lblActive = new JLabel(LanguageProperties.getString(LanguageProperties.PANELS_BUNDLE, "UINewUserPanel.userStatus")+":"); //$NON-NLS-1$
			gc.gridy = y;
			gb.setConstraints(lblActive, gc);

			add(lblActive);

			JPanel panel2 = new JPanel();
			rbActiveYes = new JRadioButton(LanguageProperties.getString(LanguageProperties.PANELS_BUNDLE, "UINewUserPanel.active")); //$NON-NLS-1$
			panel2.add(rbActiveYes);
			rbActiveYes.setSelected(true);

			rbActiveNo = new JRadioButton(LanguageProperties.getString(LanguageProperties.PANELS_BUNDLE, "UINewUserPanel.inactive")); //$NON-NLS-1$
			rbActiveNo.setSelected(false);
			panel2.add(rbActiveNo);

			gc.gridy = y;
			y++;
			gb.setConstraints(panel2, gc);
			add(panel2);

			ButtonGroup rgGroup2 = new ButtonGroup();
			rgGroup2.add(rbActiveYes);
			rgGroup2.add(rbActiveNo);	
		}	
	}

	/**
	 * Set the UserProfile for this panel to use.
	 * @param up com.compendium.core.datamodel.UserProfile, the profile for this panel to use.
	 */
	private void setUserProfile(UserProfile up) {

		oUserProfileUpdate = up;

		if (txtUserName != null) {
			txtUserName.setText(up.getLoginName());
		}
		if (txtAuthorName != null) {
			txtAuthorName.setText(up.getUserName());
		}
		if (txtDesc != null) {
			txtDesc.setText(up.getUserDescription());
		}
		if (pfPassword != null) {
			pfPassword.setText(up.getPassword());
		}
		if (pfConfPassword != null) {
			pfConfPassword.setText(up.getPassword());
		}

		if (rbAdminYes != null && rbAdminNo != null) {
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
		
		if (rbActiveYes != null && rbActiveNo != null) {
			if (up.isActive()) {
				rbActiveYes.setSelected(true);
				rbActiveNo.setSelected(false);
			} else {
				rbActiveYes.setSelected(false);
				rbActiveNo.setSelected(true);
			}
		}
	}


	/**
	 * Return the UserProfile for a new user created based on the data currently in this panel.
	 * Invoked only from the "Create a New Project" dialog
	 * @return com.compendium.core.datamodel.UserProfile, the new user created.
	 */
	public UserProfile getNewUserData() {

		String sAuthor = ""; //$NON-NLS-1$
		String sUserName = ""; //$NON-NLS-1$
		String sDescription = ""; //$NON-NLS-1$
		String sPassword = ""; //$NON-NLS-1$
		int iActiveStatus = 0;
		boolean bIsAdmin = false;			

		if (!drawSimpleForm) {
			sAuthor = txtAuthorName.getText();
			sUserName = txtUserName.getText();
			sDescription = txtDesc.getText();
			sPassword = new String(pfPassword.getPassword());
			bIsAdmin = rbAdminYes.isSelected();
			if (rbActiveYes.isSelected()){
				iActiveStatus= ICoreConstants.STATUS_ACTIVE;
			} else{
				iActiveStatus =ICoreConstants.STATUS_INACTIVE;
			}			
		} else {
			sAuthor = txtAuthorName.getText();
			sUserName = sAuthor; //$NON-NLS-1$
			sDescription = SystemProperties.defaultProjectName;
			sPassword = sAuthor; //$NON-NLS-1$
			bIsAdmin = true;	
			iActiveStatus=ICoreConstants.STATUS_ACTIVE;						
		}
		
		UserProfile newUp = new UserProfile("-1", ICoreConstants.WRITEVIEWNODE, //$NON-NLS-1$
											sUserName, 
											sAuthor, 
											sPassword,
											sDescription, 
											null, 
											bIsAdmin,
											null,
											iActiveStatus);
				return newUp;
	}

	/**
	 * Test the user data entered in this panel is valid.
	 */
	public boolean testUserData() {

		boolean bError = false;
		String sErrorString = ""; //$NON-NLS-1$

		if(txtAuthorName.getText().length() < 1)	{
			bError = true;
			sErrorString = LanguageProperties.getString(LanguageProperties.PANELS_BUNDLE, "UINewUserPanel.message1"); //$NON-NLS-1$
		}

		if (!drawSimpleForm) {
			String passwordString = new String(pfPassword.getPassword());
			String confirmString = new String(pfConfPassword.getPassword());

			//match the passwords and create a user by the given param and groups
			if(txtUserName.getText().length() < 1) {
				bError = true;
				sErrorString = LanguageProperties.getString(LanguageProperties.PANELS_BUNDLE, "UINewUserPanel.message2"); //$NON-NLS-1$
			}
			else if(!passwordString.equals(confirmString)) {
				bError = true;
				sErrorString = LanguageProperties.getString(LanguageProperties.PANELS_BUNDLE, "UINewUserPanel.message3")+"!"; //$NON-NLS-1$
			}
			else if( passwordString.length() < 1) {
				bError = true;
				sErrorString = LanguageProperties.getString(LanguageProperties.PANELS_BUNDLE, "UINewUserPanel.message4"); //$NON-NLS-1$
				//sErrorString = "'Password' field length should be more than five characters!";
			}
			else if(confirmString.length() < 1) {
				bError = true;
				sErrorString = LanguageProperties.getString(LanguageProperties.PANELS_BUNDLE, "UINewUserPanel.message5"); //$NON-NLS-1$
				//sErrorString = "'Confirm Password' field length should be more than five characters!";
			}
		}

		if(bError) {
			ProjectCompendium.APP.displayError(sErrorString, LanguageProperties.getString(LanguageProperties.PANELS_BUNDLE, "UINewUserPanel.errorTitle")); //$NON-NLS-1$
			return false;
		}
		else {
			return  true;
		}
	}

	/**
	 * Invoked when user clicks OK.  Add a new user to the database and return if successful.
	 * @return boolean, true if the new user was added to the database, else false.
	 */
	public boolean addNewUser() {

		if (!testUserData()) {
			return false;
		}
		else {
			String sAuthor = ""; //$NON-NLS-1$
			String sUserName = ""; //$NON-NLS-1$
			String sDescription = ""; //$NON-NLS-1$
			String sPassword = ""; //$NON-NLS-1$
			boolean bIsAdmin = false;	
			int iActiveStatus = 0;
					
			if (!drawSimpleForm) {
				sAuthor = txtAuthorName.getText();
				sUserName = txtUserName.getText();
				sDescription = txtDesc.getText();
				sPassword = new String(pfPassword.getPassword());
				bIsAdmin = rbAdminYes.isSelected();
				if (rbActiveYes.isSelected()) {
					iActiveStatus = ICoreConstants.STATUS_ACTIVE;
				} else {
					iActiveStatus = ICoreConstants.STATUS_INACTIVE;
				}				
			} else {
				sAuthor = txtAuthorName.getText();
				sUserName = "compendium"; //$NON-NLS-1$
				sDescription = SystemProperties.defaultProjectName;
				sPassword = "compendium"; //$NON-NLS-1$
				bIsAdmin = true;		
				iActiveStatus = ICoreConstants.STATUS_ACTIVE;						
			}
			return createNewUser(sAuthor, sUserName, sDescription, sPassword, bIsAdmin, iActiveStatus);		
		}
	}

	/**
	 * Create a new user from the given data.
	 *
	 * @param author, the author name for the user.
	 * @param login, the login name for the user.
	 * @param desc, the description of the user.
	 * @param password, the password for the user.
	 * @param isAdministrator, true if the user is an administrator, else false.
	 * @return boolean, true if the new user was successfully created, else false.
	 */
	public boolean createNewUser(String author, String login, String desc, String password, boolean isAdministrator, int iActiveStatus) {

		UserProfile up = null;
		Date date = new Date();
		View view = null;
		View oInboxNode = null;
		String userId = ""; //$NON-NLS-1$

		//check if the user profile is being created or an already present one is being updated
		if(oUserProfileUpdate != null) {
			userId = oUserProfileUpdate.getId();
			view = oUserProfileUpdate.getHomeView();
			oInboxNode = oUserProfileUpdate.getLinkView();
//			ProjectCompendium.APP.getModel().updateUserProfile(userId, author, login, desc, password, isAdministrator, iActiveStatus);
		}
		else {
			userId = ProjectCompendium.APP.getModel().getUniqueID();
		}
		
		Model oModel = (Model)ProjectCompendium.APP.getModel();

		/////////////////////////////////////////////////////////////////////////////////
		//Begin: create a home view for the user if the user doesn't have one (new user)
		/////////////////////////////////////////////////////////////////////////////////
		if(view == null) {
			try {
				view = (View)oModel.getNodeService().createNode(ProjectCompendium.APP.getModel().getSession(),
																				ProjectCompendium.APP.getModel().getUniqueID(),
																				ICoreConstants.MAPVIEW,
																				"", //$NON-NLS-1$
																				"", //$NON-NLS-1$
																				ICoreConstants.WRITEVIEWNODE,
																				ICoreConstants.READSTATE, 
																				author,
																				"Home Window", //$NON-NLS-1$
																				"Home Window of " + txtAuthorName.getText(), //$NON-NLS-1$
																				date,
																				date
																				);
				// add new user to list of Codes - bz
				PCSession session = ProjectCompendium.APP.getModel().getSession();
				String codeauthor = oModel.getUserProfile().getUserName();
				Date creationDate = new Date();
				Date modificationDate = creationDate;
				String description = "No Description"; //$NON-NLS-1$
				String behavior = "No Behavior"; //$NON-NLS-1$
				String name = author;
				String codeId = oModel.getUniqueID();
				//add to the DB
				Code code = oModel.getCodeService().createCode(session, codeId, codeauthor, creationDate, modificationDate, name, description, behavior);
				oModel.addCode(code);
				
				// add dropbox
				if (view != null) {
					String sLinkViewID = ""; //$NON-NLS-1$
					sLinkViewID = oModel.getUniqueID();
					oInboxNode = (View)oModel.getNodeService().createNode(oModel.getSession(),
							sLinkViewID,
							ICoreConstants.LISTVIEW,
							"", //$NON-NLS-1$
							"", //$NON-NLS-1$
							ICoreConstants.WRITEVIEWNODE,
							ICoreConstants.READSTATE,
							author,
							"Inbox", //$NON-NLS-1$
							"Inbox of " + author, //$NON-NLS-1$
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
					oInboxNode.setSource("", CoreUtilities.unixPath(UIImages.getPathString(IUIConstants.INBOX)), author); //$NON-NLS-1$
				}
			}
			catch (Exception e) {
				ProjectCompendium.APP.displayError("(UINewUserPanel.createNewUser)\n\n"+e.getMessage()); //$NON-NLS-1$
			}
		}

		////////////////////////////////////////////////////////
		//End: create a home view for the user
		////////////////////////////////////////////////////////

		String homeViewId = view.getId();		
		String linkViewId = ""; //$NON-NLS-1$
		linkViewId = oInboxNode.getId();
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
															 linkViewId,				// the user's link view (InBox).
															 iActiveStatus);			// int if the User is Active or Inactive
		    if(up == null) {
				String prob = LanguageProperties.getString(LanguageProperties.PANELS_BUNDLE, "UINewUserPanel.message6"); //$NON-NLS-1$
				ProjectCompendium.APP.displayError(prob, LanguageProperties.getString(LanguageProperties.PANELS_BUNDLE, "UINewUserPanel.message6Title")); //$NON-NLS-1$
			}
			else {
				ProjectCompendium.APP.getModel().updateUserProfile(up);
				return true;
			}
		}
		catch(Exception ex) {
			ProjectCompendium.APP.displayError("(UINewUserPanel.createNewUser B)\n\n"+ex.getMessage()); //$NON-NLS-1$
		}
		return false;
	}
}
