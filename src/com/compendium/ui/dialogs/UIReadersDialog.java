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

import java.sql.SQLException;
import java.util.*;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.*;

import com.compendium.LanguageProperties;
import com.compendium.ProjectCompendium;
import com.compendium.core.datamodel.UserProfile;
import com.compendium.ui.*;

/**
 * Shows a list of all readers
 *
 * @author	Lakshmi Prabhakaran
 */
public class UIReadersDialog extends UIDialog implements ActionListener {

	/** The serial version ID for this class */
	private static final long serialVersionUID = 4318672301209151609L;

	/** The main content pane for this dialog.*/
	private Container			oContentPane 	= null;

	/** The list of views.*/
	private UINavList			lstViews 		= null;

	/** The button to close the dialog.*/
	private UIButton			pbClose 		= null;

	/** The data for the list of Views.*/
	private Vector				vtViews 		= new Vector();

	/** The title for this dialog.*/
	private String				sTitle			= LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIReadersDialog.title"); //$NON-NLS-1$
	
	/** The ID of the node whose readers are required.*/
	private String				sNodeID			=  null;


 	/**
	 * Initializes and sets up the dialog.
	 * @param parent, the parent frame for this dialog.
	 */
	public UIReadersDialog(JFrame parent, String nodeId) {

	  	super(parent, true);
		
		setNodeID(nodeId);
		setTitle(sTitle);
		drawDialog();
	}

	/**
	 * Draw the contents of the dialog.
	 */
	private void drawDialog() {

		oContentPane = getContentPane();
		
		BoxLayout layout = new BoxLayout(oContentPane, BoxLayout.Y_AXIS);
		oContentPane.setLayout(layout);

		JPanel readersPanel = new JPanel(new BorderLayout());
		readersPanel.setBorder(new EmptyBorder(10,10,10,10));

		JLabel lblReaders = new JLabel(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIReadersDialog.readersList")+":"); //$NON-NLS-1$
		readersPanel.add(lblReaders, BorderLayout.NORTH);

		// Create the list
		lstViews = new UINavList(new DefaultListModel());
		
		lstViews.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		lstViews.setCellRenderer(new LabelListCellRenderer());
		lstViews.setBackground(Color.white);

		// create a scroll viewer to add scroll functionality in the list view
		JScrollPane sp = new JScrollPane(lstViews);
		sp.setPreferredSize(new Dimension(200,150));
		readersPanel.add(sp, BorderLayout.CENTER);

		oContentPane.add(readersPanel);
		oContentPane.add(createButtonPanel());

		updateListView();

		pack();
	}

	/**
	 * Create the panel of buttons.
	 */
	private JPanel createButtonPanel() {

		JPanel oButtonPanel = new JPanel();
		
		pbClose = new UIButton(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIReadersDialog.closeButton")); //$NON-NLS-1$
		pbClose.setMnemonic(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIReadersDialog.closeButtonMnemonic").charAt(0)); //$NON-NLS-1$
		pbClose.addActionListener(this);
		getRootPane().setDefaultButton(pbClose); 
		oButtonPanel.add(pbClose, BorderLayout.CENTER);

		return oButtonPanel;
	}

	/**
	 * Handle button push events.
	 * @param event, the associated ActionEvent object.
	 */
	public void actionPerformed(ActionEvent event) {

		Object source = event.getSource();
		// Handle button events
		if (source instanceof JButton) {
			if (source == pbClose) {
				onCancel();
			}
		}
	}

	/**
	 * Gets the User Names of all the object's readers and puts them into the dialog.
	 */
	private void updateListView() {

		((DefaultListModel)lstViews.getModel()).removeAllElements();
		vtViews.removeAllElements();

		String readers = ""; //$NON-NLS-1$
		Vector readerIDs = new Vector();
		UserProfile up = null;
		
		// Get the list of readers (ID's) from the database
		try {
			readerIDs = ProjectCompendium.APP.getModel().getNodeService().getReaderIDs(ProjectCompendium.APP.getModel().getSession(), sNodeID);
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		
		// Get the existing list of UserProfile objects
		Vector userProfiles = ProjectCompendium.APP.getModel().getUsers();
		Vector readernames = new Vector();
		
		// For each ID, find its corresponding UserProfile, and extract the User Name
		for(Enumeration id = readerIDs.elements();id.hasMoreElements();) {
			String sReaderID = (String) id.nextElement();
			for(Enumeration id2 = userProfiles.elements();id2.hasMoreElements();) {
				up = (UserProfile)id2.nextElement();
				if (sReaderID.compareTo(up.getUserID())== 0) {
					readernames.addElement(up.getUserName());
				}
			}
		}
		Collections.sort(readernames);  // Sort the readers list, then stuff it in the display
		for(Enumeration id = readernames.elements(); id.hasMoreElements();) {
			((DefaultListModel)lstViews.getModel()).addElement(id.nextElement());
		}
		lstViews.setSelectedIndex(0);
	}

	/**
	 * @param nodeID The NodeID to set.
	 */
	public void setNodeID(String nodeID) {
		sNodeID = nodeID;
	}

}
