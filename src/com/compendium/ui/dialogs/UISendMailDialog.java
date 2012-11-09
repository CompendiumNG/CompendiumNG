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
import java.net.URLEncoder;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.table.*;
import javax.swing.border.*;

import com.compendium.core.*;
import com.compendium.core.datamodel.*;
import com.compendium.core.db.*;

import com.compendium.LanguageProperties;
import com.compendium.ProjectCompendium;
import com.compendium.io.html.*;
import com.compendium.ui.plaf.*;
import com.compendium.ui.*;
import com.compendium.ui.dialogs.*;
import com.compendium.ui.dialogs.UISearchDialog.ToggleSelectionModel;

/**
 * UISendMailDialog defines the dialog that allows the user to send mail to a group of people.
 *
 * @author	M. Begeman
 */
public class UISendMailDialog extends UIDialog implements ActionListener, IUIConstants {

	
	/** The parent frame for this dialog.*/
	private JFrame					oParent			= null;
	
	/** The current pane to put the dialog contents in.*/
	private Container				oContentPane 	= null;

	/** The button to send the email.*/
	private JButton					pbSend 		= null;

	/** The button to close the dialog.*/
	private JButton					pbClose 		= null;

	/** The layout manager used.*/
	private	GridBagLayout 			gb 				= null;

	/** The constraints used.*/
	private	GridBagConstraints 		gc 				= null;

	/** The text area to hold the Message label.*/
	private JLabel					lblMessage			= null;

	/** The text are to hold any user-entered mail message.*/
	private static UITextArea		txtMessage			= null;
	
	/** The text area to hold the node label.*/
	private JLabel					lblToList			= null;
	
	/** The list of potentials users to send mail to.*/
	private UINavList				lstUsers			= null;
	
	/** Holds the list of selected users to send mail to.*/
	private Vector					vtUsers			= new Vector();
	
	/** The ID of the View the node being mailed is in */
	private static		View		oContainingView = null;
	
	/** The NodeSummary object for the node being mailed */
	private static		NodeSummary	oNode = null;

	/**
	 * Constructor. Initializes and sets up the dialog.
	 *
	 * @param parent, the frame that is the parent for this dialog.
	 * @param containingView, the view the node being sent resides in
	 * @param ns, NodeSummary object for the node being sent
	 */
	public UISendMailDialog(JFrame parent, View containingView, NodeSummary ns) {

		super(parent, true);
		oParent = parent;

		oContainingView = containingView;
		oNode = ns;
		
		setTitle(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UISendMailDialog.cMailTitle")); //$NON-NLS-1$

		oContentPane = getContentPane();
		gb = new GridBagLayout();
		oContentPane.setLayout(gb);

		drawDialog();
		initUsersList();

		
		setResizable(false);
		pack();
	}

	/**
	 * Draws the contents of this dialog.
	 */
	private void drawDialog() {

		int	iGridy = 0;
		
		GridBagConstraints gc = new GridBagConstraints();
		gc.insets = new Insets(5,5,5,5);
		gc.gridx = 0;
		gc.gridy = iGridy;
		gc.gridwidth = 2;
		gc.anchor = GridBagConstraints.WEST;

		lblToList = new JLabel(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UISendMailDialog.selectMail")); //$NON-NLS-1$
		gb.setConstraints(lblToList, gc);
		oContentPane.add(lblToList);
		iGridy++;
		
		lstUsers = new UINavList(new DefaultListModel());
		lstUsers.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		lstUsers.setCellRenderer(new LabelListCellRenderer());
		lstUsers.setBackground(Color.white);

		JScrollPane sp1 = new JScrollPane(lstUsers);
		sp1.setPreferredSize(new Dimension(400,200));
		gc.gridy=iGridy;
		gc.fill = GridBagConstraints.BOTH;
		gb.setConstraints(sp1, gc);
		oContentPane.add(sp1);
		iGridy++;
		
		if (FormatProperties.emailInbox) {
			lblMessage = new JLabel(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UISendMailDialog.label")); //$NON-NLS-1$
			gc.gridy=iGridy;
			gc.fill = GridBagConstraints.NONE;
			gb.setConstraints(lblMessage, gc);
			oContentPane.add(lblMessage);
			iGridy++;
			
			txtMessage = new UITextArea(50, 50);
			txtMessage.setAutoscrolls(true);
			JScrollPane sp2 = new JScrollPane(txtMessage);
			sp2.setPreferredSize(new Dimension(400, 200));
			gc.gridy=iGridy;
			gc.fill = GridBagConstraints.BOTH;
			gb.setConstraints(sp2, gc);
			oContentPane.add(sp2);
			iGridy++;
		}
		
		gc.fill = GridBagConstraints.NONE;

		pbSend = new UIButton(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UISendMailDialog.sendButton")); //$NON-NLS-1$
		pbSend.setMnemonic(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UISendMailDialog.sendButtonMnemonic").charAt(0)); //$NON-NLS-1$
		pbSend.addActionListener(this);
		gc.gridy = iGridy;
		gc.anchor = GridBagConstraints.WEST;
		gb.setConstraints(pbSend, gc);
		oContentPane.add(pbSend);
		
		pbClose = new UIButton(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UISendMailDialog.cancelButton")); //$NON-NLS-1$
		pbClose.setMnemonic(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UISendMailDialog.cancelButtonMnemonic").charAt(0)); //$NON-NLS-1$
		pbClose.addActionListener(this);
		gc.gridx = 1;
		gc.anchor = GridBagConstraints.CENTER;
		gb.setConstraints(pbClose, gc);
		oContentPane.add(pbClose);
	}
	
	/**
	 * Fill in the dialog with a list of all Active users.  
	 */
	private void initUsersList() {
		((DefaultListModel)lstUsers.getModel()).removeAllElements();
		vtUsers.removeAllElements();
		
		for(Enumeration e = (ProjectCompendium.APP.getModel().getUsers()).elements();e.hasMoreElements();) {
			UserProfile up = (UserProfile)e.nextElement();

			if (up.isActive()) {
				String authorName = up.getUserName();
				String displayText = authorName;
				if (authorName.equals("")) { //$NON-NLS-1$
					continue;
				}
	
				if(displayText.length() > 40) {
					displayText = displayText.substring(0,39);
					displayText += "...."; //$NON-NLS-1$
				}
	
				JLabel lblAuthorsList = new JLabel(displayText,SwingConstants.LEFT);
				lblAuthorsList.setToolTipText(authorName);
				((DefaultListModel)lstUsers.getModel()).addElement(lblAuthorsList);
				vtUsers.addElement(up);
			}
		}
	}

	/**
	 * Handle action events coming from the buttons.
	 * @param evt, the associated ACtionEvent.
	 */
	public void actionPerformed(ActionEvent evt) {
		Object source = evt.getSource();

		// Handle button events
		if (source instanceof JButton) {
			if (source == pbSend) {
				onSend();
			}
			else if (source == pbClose) {
				onCancel();
			}
		}
	}

	/**
	 * Send mail to the selected people.
	 */
	private void onSend()  {

		String sToList = null;
		int [] selected = lstUsers.getSelectedIndices();
		for(int i=0;i<selected.length;i++) {
			UserProfile up = (UserProfile)vtUsers.elementAt(selected[i]);
			onCreateInternalLinkInView(up);
			String sToName = ""; //$NON-NLS-1$
			if (up.getUserDescription().startsWith("email:")) { //$NON-NLS-1$
				sToName = up.getUserDescription().substring(6);  	//remainder after the 'email:' prefix
			} else {
				sToName = up.getUserName();
			}
			if (sToList == null) {
				sToList = sToName;
			} else {
				sToList = sToList + "," + sToName; //$NON-NLS-1$
			}
		}
		if (sToList != null) {
			sendDesktopMail(sToList);					// Send the mail
			onCancel();									// Clean up
		} else {
			JOptionPane.showMessageDialog(this, LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UISendMailDialog.errorNoReceipient")); //$NON-NLS-1$
		}
	}
	
	/**
	 * Close the dialog.
	 */
	public void onCancel() {
		oContainingView = null;
		oNode = null;
		if (txtMessage != null) {
			txtMessage = null;
		}
		setVisible(false);
		dispose();
	}

	/**
	 * Handle the enter key action. Override superclass to do nothing.
	 */
	public void onEnter() {}
	
	/**
	 * Sends an email message via the user's desktop mail client
	 */
	public static void sendDesktopMail(String sTo) {
		
		if (FormatProperties.emailInbox) {
	        try {
		        String URIString = null;

		        URIString = "mailto:" + sTo;						//Stuff username(s) into the To: field //$NON-NLS-1$
		        URIString = URIString + "?SUBJECT="; //$NON-NLS-1$
		        String sSubject = LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UISendMailDialog.cmailSubject")+" - "; //$NON-NLS-1$
		        if (oNode.getLabel().length() > 0) {
		        	sSubject = sSubject + oNode.getLabel();
		        } else {
		        	sSubject = sSubject + "(no label)"; //$NON-NLS-1$
		        }
		        URIString = URIString + URLEncoder.encode(sSubject, "UTF-8"); //$NON-NLS-1$
		        URIString = URIString + "&BODY="; //$NON-NLS-1$
		        
		        String URIBody = "";
				if (txtMessage != null) {
					URIBody = txtMessage.getText();
				}

		        if (URIBody.length() > 0) URIBody = URIBody + "\n-----\n"; //$NON-NLS-1$
	//	        URIBody = URIBody + "Node detail:\n\n";						// Begin aggregating the message body string.  This will

		        if (oNode.getDetail().length() == 0) {
		        	URIBody = URIBody + "<<no node detail>>\n"; //$NON-NLS-1$
		        } else {
		        	URIBody = URIBody + oNode.getDetail();
		        	URIBody = URIBody + "\n"; //$NON-NLS-1$
		        }

		        URIString= URIString + URLEncoder.encode(URIBody, "UTF-8"); //$NON-NLS-1$
	        	URIString = URIString.replace("+", "%20");			// Required since UTF-8 turns spaces into + signs //$NON-NLS-1$ //$NON-NLS-2$

	        	if (URIString.length() > 240) {						// This limit avoids various desktop mail limits/bugs
	        		URIString = URIString.substring(0, 240);		// Shorten the entire string to 240 bytes
	        		int iLastPercent = URIString.indexOf('%', 235);	// Shorten it some more if we cut an encoding (like %20) in half
	        		if (iLastPercent > 0) URIString = URIString.substring(0, iLastPercent);
	        		URIString = URIString + "%0A...";				// Add an ellipses to indicate we cut content from the body //$NON-NLS-1$
	        	}

//	        	System.out.println("Sending desktop email...");			// For debugging
//	        	System.out.println(URIString);							// For debugging
//	        	System.out.println("String length: " + URIString.length());

	        	ExecuteControl.launch(URIString);					// Send the mailto: URL to the operating system
	        }
	        catch (java.io.UnsupportedEncodingException UEEex) {
	        	System.out.println("Unsupported encoding exception."); //$NON-NLS-1$
	        	System.out.println(UEEex.getMessage());
	        	UEEex.printStackTrace();
	        }
	    }
	}
	
	/**
	 * Create a Reference node with internal link to this node
	 * and put it in the inbox for the given View and user.
	 * @param view the view to add the reference to.
	 * @param up the UserProfile for the chosen user
	 */
	private void onCreateInternalLinkInView(UserProfile up) {

		View view = up.getLinkView();
		if (view == null) {
			view = ProjectCompendium.APP.createInBox(up);
			if (view == null) {
				ProjectCompendium.APP.displayError(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UISendMailDialog.errorGettingReference")); //$NON-NLS-1$
				return;
			}
		}
		

		IModel model = ProjectCompendium.APP.getModel();
		UserProfile currentUser = model.getUserProfile();

		view.initialize(model.getSession(), model);

		String sRef = ICoreConstants.sINTERNAL_REFERENCE+oContainingView.getId()+"/"+oNode.getId(); //$NON-NLS-1$
		String sAuthor = ProjectCompendium.APP.getModel().getUserProfile().getUserName();
		String sMessageText = "";
		if (txtMessage != null) {
			sMessageText = txtMessage.getText();
		}
		try{
			NodePosition node = view.addMemberNode(ICoreConstants.REFERENCE,
					 "", //$NON-NLS-1$
					 "", //$NON-NLS-1$
					 sAuthor,
					 "GO TO: "+oNode.getLabel(), //$NON-NLS-1$
					 LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UISendMailDialog.from")+": "+currentUser.getUserName()+"\n"+ //$NON-NLS-1$ //$NON-NLS-2$
					 LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UISendMailDialog.message")+": "+sMessageText+"\n"+ //$NON-NLS-1$ //$NON-NLS-2$
					 "-------\n"+ //$NON-NLS-1$
					 LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UISendMailDialog.linkToNodeIn")+ //$NON-NLS-1$
					 "( "+oContainingView.getLabel()+" )\n\n", //$NON-NLS-1$ //$NON-NLS-2$
					 0, ((view.getNodeCount() + 1) *10));

			node.getNode().setSource(sRef, "", sAuthor); //$NON-NLS-1$
			node.getNode().setState(ICoreConstants.UNREADSTATE);
			view.setState(ICoreConstants.MODIFIEDSTATE);
		} catch (Exception e) {
			ProjectCompendium.APP.displayError(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UISendMailDialog.errorAddingToInbox")+":\n\n"+e.getMessage()); //$NON-NLS-1$
		}

		// If the view is open (it's your in box and you have it open), refresh the view.
		UIViewFrame oViewFrame = ProjectCompendium.APP.getInternalFrame(view);
		if (oViewFrame != null) {
			UIListViewFrame listFrame = (UIListViewFrame)oViewFrame;
			listFrame.getUIList().updateTable();
		}

		ProjectCompendium.APP.refreshNodeIconIndicators(ProjectCompendium.APP.getInBoxID());
	}
}
