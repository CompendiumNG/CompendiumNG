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
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.*;

import com.compendium.ProjectCompendium;
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
	private String				sTitle			= "Readers";
	
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

		JLabel lblReaders = new JLabel("Readers List:");
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

		
		pbClose = new UIButton("Close");
		pbClose.setMnemonic(KeyEvent.VK_C);
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
	 * Updates the list view with the available views from the database.
	 */
	private void updateListView() {

		((DefaultListModel)lstViews.getModel()).removeAllElements();
		vtViews.removeAllElements();
		Vector vtSort = new Vector(51);

		try {
			Vector users = ProjectCompendium.APP.getModel().getNodeService().getReaders(ProjectCompendium.APP.getModel().getSession(), sNodeID);
			for(Enumeration e = users.elements();e.hasMoreElements();) {
				((DefaultListModel)lstViews.getModel()).addElement(e.nextElement());
			}

			lstViews.setSelectedIndex(0);
		}
		catch(Exception io) {
			ProjectCompendium.APP.displayError("Exception: (UIReadersDialog.updateListView) \n");//+io.getMessage());
		}
	}

	/**
	 * @param nodeID The NodeID to set.
	 */
	public void setNodeID(String nodeID) {
		sNodeID = nodeID;
	}

}
