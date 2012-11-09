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
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.*;

import com.compendium.core.ICoreConstants;
import com.compendium.LanguageProperties;
import com.compendium.ProjectCompendium;
import com.compendium.ui.plaf.*;
import com.compendium.ui.*;


/**
 * UIJabberMessageDialog defines the dialog to display message received from a Jabber client or IX Panel
 *
 * @author	Michelle Bachler
 */
public class UIJabberMessageDialog extends UIDialog implements ActionListener, IUIConstants {

	/** The button to create a node from a jabber message.*/
	private UIButton				pbCreate	= null;

	/** The button to close the dialog.*/
	private UIButton				pbClose	= null;

	/** The button to open the relevant help.*/
	private UIButton				pbHelp	= null;

	/** The pane for the dialog's contents.*/
	private Container				oContentPane = null;

	/** The central panel with the message details.*/
	private JPanel					oDetailsPanel = null;

	/** The panel with the buttons on.*/
	private UIButtonPanel			oButtonPanel = null;

	/** The label for the image.*/
	private JLabel					oImageLabel = null;

	/** The image for the node to create.*/
	private ImageIcon				oImage = null;

	/** Holds the jabber message text.*/
	private JTextArea				oTextArea = null;

	/** The node type for the jabber message.*/
	private int						nodeType = -1;

	/** The type of the jabber message - plain jabber or ix panel.*/
	private String 					type = ""; //$NON-NLS-1$

	/** The name of the sender of the message.*/
	private String					from = ""; //$NON-NLS-1$

	/** The jabber message.*/
	private String 					message = ""; //$NON-NLS-1$

	/** CURRENTLY NOT USED.*/
	private Vector 					children = new Vector(51);

	/** The ViewPaneUI for the current map view.*/
	private ViewPaneUI 				oViewPaneUI = null;

	/** The UIViewPane for the current map view.*/
	private UIViewPane 				oViewPane = null;

	/** The UIViewFrame for the current view.*/
	private UIViewFrame 			viewFrame = null;

	/** The node created from the jabber message.*/
	private UINode 					oNode = null;

	/** Is the jabber message from an IX panel.*/
	private boolean isIX = false;


	/**
	 * Initializes and draws the dialog.
	 * @param parent the parent frame for this dialog.
	 * @param sFrom the name of the person who sent the jabber message.
	 * @param sMessage the jabber message.
	 * @param sType the type of the message - jabber or ix panel message.
	 */
	public UIJabberMessageDialog(JFrame parent, String sFrom, String sMessage, String sType) {

		super(parent, true);

		this.message = sMessage;
		this.from = sFrom;
		this.type = sType;

		if (sType.equals("IXPanel")) //$NON-NLS-1$
			isIX = true;

		if (isIX)
		  	this.setTitle("IX Panel "+LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIJabberMessageDialog.titleEnd")); //$NON-NLS-1$
		else
		  	this.setTitle("Jabber "+LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIJabberMessageDialog.titleEnd")); //$NON-NLS-1$

		oContentPane = getContentPane();
		oContentPane.setLayout(new BorderLayout());

		sMessage = extractMessageItems();

		oDetailsPanel = new JPanel(new BorderLayout());

		JPanel imagePanel = new JPanel();
		imagePanel.setBorder(new EmptyBorder(10,10,10,0));

		oImageLabel = new JLabel( oImage );
		oImageLabel.setVerticalAlignment(SwingConstants.TOP);
		imagePanel.add(oImageLabel);

		oDetailsPanel.add(imagePanel, BorderLayout.WEST);

		oTextArea = new JTextArea(sMessage);
		oTextArea.setEditable(false);
		oTextArea.setFont(new Font("Dialog", Font.PLAIN, 14)); //$NON-NLS-1$
		oTextArea.setBackground(oDetailsPanel.getBackground());
		oTextArea.setColumns(30);
		oTextArea.setLineWrap(true);
		oTextArea.setWrapStyleWord(true);

		JPanel textPanel = new JPanel();
		textPanel.setBorder(new EmptyBorder(10,10,20,10));
		textPanel.add(oTextArea);

		oDetailsPanel.add(textPanel, BorderLayout.CENTER);

		oButtonPanel = new UIButtonPanel();

		pbCreate = new UIButton(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIJabberMessageDialog.addToMapButton")); //$NON-NLS-1$
		pbCreate.setMnemonic(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIJabberMessageDialog.addToMapButtonMnemonic").charAt(0));
		pbCreate.addActionListener(this);
		getRootPane().setDefaultButton(pbCreate);
		oButtonPanel.addButton(pbCreate);

		if (nodeType == -1)
			pbCreate.setEnabled(false);

		pbClose = new UIButton(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIJabberMessageDialog.closeButton")); //$NON-NLS-1$
		pbClose.setMnemonic(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIJabberMessageDialog.closeButtonMnemonic").charAt(0));
		pbClose.addActionListener(this);
		oButtonPanel.addButton(pbClose);

		pbHelp = new UIButton(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIJabberMessageDialog.helpButton")); //$NON-NLS-1$
		pbHelp.setMnemonic(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIJabberMessageDialog.helpButtonMnemonic").charAt(0));
		ProjectCompendium.APP.mainHB.enableHelpOnButton(pbHelp, "connections.jabber", ProjectCompendium.APP.mainHS); //$NON-NLS-1$
		oButtonPanel.addHelpButton(pbHelp);

		oContentPane.add(oDetailsPanel, BorderLayout.CENTER);
		oContentPane.add(oButtonPanel, BorderLayout.SOUTH);

		this.
       	validate();
        pack();
        validate();
        repaint();

		setResizable(false);
	}

	/**
	 * Extract the message elements from the message and return the root message
	 * @return String, the root message.
	 */
	private String extractMessageItems() {

		String charType = message.substring(0,3);
		String oldType = message.substring(0,1);

		int fromPos = 3;

		if (oldType.equals("+")) //$NON-NLS-1$
			nodeType = ICoreConstants.PRO;
		else if (oldType.equals("-")) //$NON-NLS-1$
			nodeType = ICoreConstants.CON;
		else if (oldType.equals("?")) //$NON-NLS-1$
			nodeType = ICoreConstants.ISSUE;
		else if (oldType.equals("!")) //$NON-NLS-1$
			nodeType = ICoreConstants.POSITION;

		if (oldType.equals("+") || oldType.equals("-") || oldType.equals("?") || oldType.equals("!")) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			fromPos = 1;

		if (charType.equals("[+]")) //$NON-NLS-1$
			nodeType = ICoreConstants.PRO;
		else if (charType.equals("[-]")) //$NON-NLS-1$
			nodeType = ICoreConstants.CON;
		else if (charType.equals("[?]")) //$NON-NLS-1$
			nodeType = ICoreConstants.ISSUE;
		else if (charType.equals("[!]")) //$NON-NLS-1$
			nodeType = ICoreConstants.POSITION;

		else if (charType.equals("[?]") || charType.equalsIgnoreCase("[I]") //$NON-NLS-1$ //$NON-NLS-2$
				|| charType.equalsIgnoreCase("[Q]")) //$NON-NLS-1$
			nodeType = ICoreConstants.ISSUE;
		else if (charType.equals("[!]") || charType.equalsIgnoreCase("[P]") //$NON-NLS-1$ //$NON-NLS-2$
				|| charType.equalsIgnoreCase("[A]")) //$NON-NLS-1$
			nodeType = ICoreConstants.POSITION;

		else if (charType.equalsIgnoreCase("[D]")) //$NON-NLS-1$
			nodeType = ICoreConstants.DECISION;
		else if (charType.equalsIgnoreCase("[N]")) //$NON-NLS-1$
			nodeType = ICoreConstants.NOTE;
		else if (charType.equalsIgnoreCase("[R]")) //$NON-NLS-1$
			nodeType = ICoreConstants.REFERENCE;
		else if (charType.equalsIgnoreCase("[U]")) //$NON-NLS-1$
			nodeType = ICoreConstants.ARGUMENT;

		else if (charType.equalsIgnoreCase("[M]")) { //$NON-NLS-1$
			nodeType = ICoreConstants.MAPVIEW;
			String thisMessage = message.substring(fromPos);
			int nextBracket = thisMessage.indexOf("["); //$NON-NLS-1$
			if (nextBracket != -1) {
				//extractChildren(message);
				message = message.substring(fromPos, nextBracket-1);
			}
		}
		else if (charType.equalsIgnoreCase("[L]")) { //$NON-NLS-1$
			nodeType = ICoreConstants.LISTVIEW;
			String thisMessage = message.substring(fromPos);
			int nextBracket = thisMessage.indexOf("["); //$NON-NLS-1$
			if (nextBracket != -1) {
				//extractChildren(message);
				message = message.substring(fromPos, nextBracket-1);
			}
		}

		String sMessage = ""; //$NON-NLS-1$
		if (nodeType > -1) {
			message = message.substring(fromPos);
			message = message.trim();
			sMessage = from+" "+LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIJabberMessageDialog.says")+":\n\n"+message; //$NON-NLS-1$
			oImage = UIImages.getNodeImage(nodeType);
		}
		else {
			nodeType = ICoreConstants.NOTE;
			sMessage = from+" "+LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIJabberMessageDialog.says")+":\n\n"+message; //$NON-NLS-1$
			oImage = UIImages.getNodeImage(nodeType);
		}

		return sMessage;
	}

	/*
	private void extractChildren(String message) {

		while( message.length > 0) {

			String charType = message.substring(0,3);

			if (charType.equals("[+]"))
				nodeType = ICoreConstants.PRO;
			else if (charType.equals("[-]"))
				nodeType = ICoreConstants.CON;
			else if (charType.equals("[?]"))
				nodeType = ICoreConstants.ISSUE;
			else if (charType.equals("[!]"))
				nodeType = ICoreConstants.POSITION;

			else if (charType.equals("[I]"))
				nodeType = ICoreConstants.ISSUE
			else if (charType.equals("[Q]"))
				nodeType = ICoreConstants.ISSUE
			else if (charType.equals("[P]"))
				nodeType = ICoreConstants.POSITION

			else if (charType.equals("[D]"))
				nodeType = ICoreConstants.DECISION
			else if (charType.equals("[N]"))
				nodeType = ICoreConstants.NOTE;
			else if (charType.equals("[R]"))
					nodeType = IUIConstants.REFERENCE;
			else if (charType.equals("[U]"))
				nodeType = IUIConstants.ARGUMENT;
		}
	}
	*/

	/**
	 * Handle action events coming from the buttons.
	 * @param evt, the associated ActionEvent object.
	 */
	public void actionPerformed(ActionEvent evt) {

		Object source = evt.getSource();
		if (source instanceof JButton) {

			if (source == pbCreate) {
				onCreate();
			}
			else if (source == pbClose)
				onCancel();
		}
	}

	/**
	 * Handle the creation of a new icon on the current map from the jabber message.
	 */
	private void onCreate() {

		viewFrame = ProjectCompendium.APP.getCurrentFrame();

		if (viewFrame instanceof UIMapViewFrame) {
			addToView( ((UIMapViewFrame)viewFrame).getViewPane());
		}
		else {
			addToList( ((UIListViewFrame)viewFrame).getUIList());
		}

		if (oNode != null && oViewPane != null) {
			oViewPane.setSelectedNode(oNode,ICoreConstants.MULTISELECT);
			oNode.setSelected(true);
			oNode.requestFocus();
		}
		onCancel();
	}

	/**
	 * Create the relevant node and add it to the given map view.
	 * @param view com.compendium.ui.UIViewPane, the view to add the new node to.
	 */
	private UINode addToView(UIViewPane view) {

		String label = ""; //$NON-NLS-1$
		String detail = ""; //$NON-NLS-1$
		if (message.length() > 100) {
			label = message.substring(0,100);
			detail = message.substring(101);
		}
		else
			label= message;

		detail += "\n\n"+LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIJabberMessageDialog.sentFrom")+" "+from; //$NON-NLS-1$

		oNode = null;
		oViewPane = view;
		oViewPaneUI = view .getUI();

		int nX = (viewFrame.getWidth()/2)-60;
		int nY = (viewFrame.getHeight()/2)-60;

		// GET CURRENT SCROLL POSITION AND ADD THIS TO POSITIONING INFO
		int hPos = viewFrame.getHorizontalScrollBarPosition();
		int vPos = viewFrame.getVerticalScrollBarPosition();

		nX = nX + hPos;
		nY = nY + vPos;

		oNode = oViewPaneUI.createNode(nodeType,
										 "", //$NON-NLS-1$
										 ProjectCompendium.APP.getModel().getUserProfile().getUserName(),
										 label,
										 detail,
										 nX,
										 nY,
										 ProjectCompendium.APP.getModel().getUserProfile().getId());
		return oNode;
	}

	/**
	 * Create the relevant node and add it to the given list view.
	 * @param view com.compendium.ui.UIList, the view to add the new node to.
	 */
	private void addToList(UIList view) {

		String label = ""; //$NON-NLS-1$
		String detail = ""; //$NON-NLS-1$
		if (message.length() > 100) {
			label = message.substring(0,100);
			detail = message.substring(101);
		}
		else
			label= message;

		detail += LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIJabberMessageDialog.sentFrom")+from; //$NON-NLS-1$

		ListUI listUI = view.getListUI();

		int nY = (listUI.getUIList().getNumberOfNodes() + 1) * 10;
		int nX = 0;

		listUI.createNode(nodeType,
							 "", //$NON-NLS-1$
							 ProjectCompendium.APP.getModel().getUserProfile().getUserName(),
							 label,
							 detail,
							 nX,
							 nY);

		UIList uiList = listUI.getUIList();
		uiList.updateTable();
		uiList.selectNode(uiList.getNumberOfNodes() - 1, ICoreConstants.MULTISELECT);
	}
}
