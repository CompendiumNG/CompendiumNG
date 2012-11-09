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

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;
import javax.swing.text.Document;

import com.compendium.ProjectCompendium;

import com.compendium.core.datamodel.*;
import com.compendium.core.datamodel.services.*;
import com.compendium.ui.*;
import com.compendium.ui.plaf.*;

/**
 * Opens a dialog for maintaining workspaces
 *
 * @author	Michelle Bachler
 */
public class UIWorkspaceDialog extends UIDialog implements ActionListener {

	/** The scrollpane for the list of current workspaces.*/
	private JScrollPane			sp 			= null;

	/** The list of current workspaces.*/
	private UINavList			lstWorkspaces	= null;

	/** The data for list of current workspaces for the given user.*/
	private Vector				oWorkspaces	= new Vector();

	/** To enter the name of the new workspace.*/
	private JTextField			tfNewWorkspace = null;

	/** The button to delete the selected workspaces.*/
	private UIButton			pbDelete 		= null;

	/** The button to close this dialog.*/
	private UIButton			pbClose 		= null;

	/** The button to add a new workspace.*/
	private UIButton			pbAddToList		= null;

	/** The button to save current view setting over an exisitng workspace.*/
	private UIButton			pbSave			= null;

	/** Activates the help opeing to the appropriate section.*/
	private UIButton			pbHelp			= null;

	/** The main panel with the optins and list on it.*/
	private JPanel 				mainpanel		= null;

	/** The panel with the button on it.*/
	private JPanel				bottompanel		= null;

	/** The parent frame for this dialog.*/
	private JFrame				oParent 		= null;

	/** the id of the wuse whoes workspaces to maintain.*/
	private String 				sUserID		= null;

	/** The service to use to access the databse.*/
	private WorkspaceService	workserv		= null;

	/** The session for the current user in the current mode.*/
	private PCSession			oSession		= null;

	/** The model for the currently open database.*/
	public IModel				oModel		= null;

	/**
	 * Constructor.
	 * @param parent, the parent frame for this dialog.
	 * @param sUserID, the id of the user whose Workspaces to maintain.
	 * @param model com.compendium.core.datamodel.IModel, the model for the currently open database.
	 */
	public UIWorkspaceDialog(JFrame parent, String sUserID, IModel model) {

		super(parent, true);

		this.sUserID = sUserID;
		oParent = parent;
		oModel = model;

		workserv = (WorkspaceService)oModel.getWorkspaceService();
		oSession = oModel.getSession();

		setResizable(false);
		setTitle("Workspaces Management");
		getContentPane().setLayout(new BorderLayout());

		drawDialog();

		getContentPane().add(bottompanel, BorderLayout.NORTH);
		getContentPane().add(mainpanel, BorderLayout.CENTER);
		getContentPane().add(createButtonPanel(), BorderLayout.SOUTH);

		pack();
	}

	/**
	 * Draw the contents of the dialog.
	 */
	private void drawDialog() {

		mainpanel = new JPanel();
		mainpanel.setBorder(new EmptyBorder(10,10,10,10));

		GridBagLayout gb = new GridBagLayout();
		GridBagConstraints gc = new GridBagConstraints();
		mainpanel.setLayout(gb);
		gc.insets = new Insets(5,5,5,5);
		gc.anchor = GridBagConstraints.WEST;

		JLabel lblFav = new JLabel("Current Workspaces:");
		gc.gridy = 0;
		gc.gridx = 0;
		gc.gridwidth=4;
		gb.setConstraints(lblFav, gc);
		mainpanel.add(lblFav);

		// Create the list
		lstWorkspaces = new UINavList(new DefaultListModel());
		lstWorkspaces.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		lstWorkspaces.setBackground(Color.white);
		DefaultListCellRenderer listRenderer = new DefaultListCellRenderer() {

			public Component getListCellRendererComponent(
   		     	JList list,
   		        Object value,
            	int modelIndex,
            	boolean isSelected,
            	boolean cellHasFocus)
            {

				Vector info = (Vector) value;
				setText( (String)info.elementAt(1) );

 		 		if (isSelected) {
					setBackground( list.getSelectionBackground() );
					setForeground( list.getSelectionForeground() );
				}
				else {
					setBackground( list.getBackground() );
					setForeground( list.getForeground() );
				}

				return this;
			}
		};
		lstWorkspaces.setCellRenderer(listRenderer);

        updateWorkspaceData();

		JScrollPane sp = new JScrollPane(lstWorkspaces);
		sp.setPreferredSize(new Dimension(350,200));
		gc.gridy = 1;
		gc.gridx = 0;
		gc.gridwidth=4;
		gb.setConstraints(sp, gc);
		mainpanel.add(sp);

		pbDelete = new UIButton("Delete");
		pbDelete.addActionListener(this);
		pbDelete.setToolTipText("Delete the selected Workspace(s)");
		gc.gridy = 2;
		gc.gridx = 0;
		gc.gridwidth=1;
		gc.anchor = GridBagConstraints.WEST;
		gb.setConstraints(pbDelete, gc);
		mainpanel.add(pbDelete);

		pbSave = new UIButton("Save As Current");
		pbSave.addActionListener(this);
		pbSave.setToolTipText("Save the current workspace settings to the selected Workspace name");
		gc.gridy = 2;
		gc.gridx = 1;
		gc.gridwidth=1;
		gc.anchor = GridBagConstraints.EAST;
		gb.setConstraints(pbSave, gc);
		mainpanel.add(pbSave);

		// BOTTOM PANEL
		bottompanel = new JPanel();
		bottompanel.setBorder(new TitledBorder(new EtchedBorder(),
                    "Add New Workspace",
                    TitledBorder.LEFT,
                    TitledBorder.TOP,
					new Font("Dialog", Font.BOLD, 12) ));

		gb = new GridBagLayout();
		gc = new GridBagConstraints();
		bottompanel.setLayout(gb);
		gc.insets = new Insets(5,5,5,5);
		gc.anchor = GridBagConstraints.WEST;

		// the new workspace textfield box
		JLabel lblWorkspace = new JLabel("Add current Views as New Workspace named:");
		gc.gridy = 1;
		gc.gridwidth = 3;
		gc.weightx = 3.0;
		gb.setConstraints(lblWorkspace, gc);
		bottompanel.add(lblWorkspace);

		tfNewWorkspace = new JTextField("");
		tfNewWorkspace.setColumns(35);
		tfNewWorkspace.setMargin(new Insets(2,2,2,2));
		gc.gridy = 2;
		gc.gridwidth = 2;
		gc.weightx = 2.0;
		gb.setConstraints(tfNewWorkspace, gc);
		bottompanel.add(tfNewWorkspace);

		pbAddToList = new UIButton("Add");
		pbDelete.setToolTipText("Add a new Workspace consisting of the open views with the name entered");
		pbAddToList.addActionListener(this);
		pbAddToList.setEnabled(true);
		gc.gridwidth = 1;
		gc.weightx = 1.0;
		gb.setConstraints(pbAddToList, gc);
		bottompanel.add(pbAddToList);
	}

	/**
	 * Create and return the button panel.
	 */
	private UIButtonPanel createButtonPanel() {

		UIButtonPanel oButtonPanel = new UIButtonPanel();

		pbClose = new UIButton("Close");
		pbClose.setMnemonic(KeyEvent.VK_C);
		pbClose.addActionListener(this);
		getRootPane().setDefaultButton(pbClose);
		oButtonPanel.addButton(pbClose);

		// Add help button
		pbHelp = new UIButton("Help");
		pbHelp.setMnemonic(KeyEvent.VK_H);
		ProjectCompendium.APP.mainHB.enableHelpOnButton(pbHelp, "basics.workspaces", ProjectCompendium.APP.mainHS);
		oButtonPanel.addHelpButton(pbHelp);

		return oButtonPanel;
	}

	/**
	 * Updates the list of Workspaces with those of the current user.
	 */
	public void updateWorkspaceData() {

		oWorkspaces.removeAllElements();
		oWorkspaces = null;

		try { oWorkspaces = workserv.getWorkspaces(oSession, sUserID); }
		catch(Exception io) {}

		DefaultListModel listModel = new DefaultListModel();
		if (oWorkspaces != null && oWorkspaces.size() > 0) {

			int count = oWorkspaces.size();
			for (int i=0; i < count; i++) {
				listModel.addElement(oWorkspaces.elementAt(i));
			}

			lstWorkspaces.setModel(listModel);
		}
		else {
			lstWorkspaces.setModel(listModel);
		}
	}

	/**
	 * Process a button push event.
	 * @param evt, the associated ActionEvent object.
	 */
	public void actionPerformed(ActionEvent evt) {
		Object source = evt.getSource();

		if(source.equals(pbDelete)) {

			Object[] info = lstWorkspaces.getSelectedValues();
			String sWorkspaceIDs = "";

			int count = info.length;
			for(int i=0; i<count; i++) {
				Vector next = (Vector)info[i];
				String sWorkspaceID = (String)next.elementAt(0);

				if (i < info.length-1)
					sWorkspaceIDs += "'"+sWorkspaceID+"',";
				else
					sWorkspaceIDs += "'"+sWorkspaceID+"'";
			}

			if (!sWorkspaceIDs.equals("")) {
				ProjectCompendium.APP.deleteWorkspaces(sWorkspaceIDs);
				updateWorkspaceData();
			}

			//onCancel();
		}
		if(source.equals(pbSave)) {
			Vector info = (Vector)lstWorkspaces.getSelectedValue();
			if (info != null) {
				String sWorkspaceID = (String)info.elementAt(0);
				String sName = (String)info.elementAt(1);
				ProjectCompendium.APP.updateWorkspace(sWorkspaceID, sName);
				onCancel();
			}
			else {
				ProjectCompendium.APP.displayError("You must select a workspace to save to");
			}
		}
		else if (source.equals(pbAddToList)) {
			String sName = tfNewWorkspace.getText();
			if (sName.equals("")) {
				ProjectCompendium.APP.displayError("You must enter a name for this Workspace");
				requestFocus();
				tfNewWorkspace.requestFocus();
			}
			else {
				if (!ProjectCompendium.APP.createWorkspace(sName)) {
					requestFocus();
					tfNewWorkspace.requestFocus();
				}
				else {
					tfNewWorkspace.setText("");
					updateWorkspaceData();
				}
			}
		}
		else if (source.equals(pbClose)) {
			onCancel();
		}
	}
}
