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
import java.util.*;

import javax.swing.*;
import javax.swing.border.*;

import com.compendium.LanguageProperties;
import com.compendium.ProjectCompendium;

import com.compendium.core.datamodel.*;
import com.compendium.core.datamodel.services.*;
import com.compendium.ui.*;

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

	/** The button to save current view setting over an existing workspace.*/
	private UIButton			pbSave			= null;

	/** Activates the help opening to the appropriate section.*/
	private UIButton			pbHelp			= null;

	/** The main panel with the options and list on it.*/
	private JPanel 				mainpanel		= null;

	/** The panel with the button on it.*/
	private JPanel				bottompanel		= null;

	/** the id of the user whose workspaces to maintain.*/
	private String 				sUserID		= null;

	/** The service to use to access the database.*/
	private WorkspaceService	workserv		= null;

	/** The session for the current user in the current mode.*/
	private PCSession			oSession		= null;

	/** The model for the currently open database.*/
	public IModel				oModel		= null;

	/**
	 * Constructor.
	 * @param parent the parent frame for this dialog.
	 * @param sUserID the id of the user whose Workspaces to maintain.
	 * @param model the model for the currently open database.
	 */
	public UIWorkspaceDialog(JFrame parent, String sUserID, IModel model) {

		super(parent, true);

		this.sUserID = sUserID;
		oModel = model;

		workserv = (WorkspaceService)oModel.getWorkspaceService();
		oSession = oModel.getSession();

		setResizable(false);
		setTitle(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIWorkspaceDialog.title")); //$NON-NLS-1$
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

		JLabel lblFav = new JLabel(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIWorkspaceDialog.currentWorkspaces")+":"); //$NON-NLS-1$
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

		pbDelete = new UIButton(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIWorkspaceDialog.deleteButton")); //$NON-NLS-1$
		pbDelete.setMnemonic(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIWorkspaceDialog.deleteButtonMnemonic").charAt(0));
		pbDelete.setToolTipText(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIWorkspaceDialog.deleteButtonTip")); //$NON-NLS-1$
		pbDelete.addActionListener(this);
		gc.gridy = 2;
		gc.gridx = 0;
		gc.gridwidth=1;
		gc.anchor = GridBagConstraints.WEST;
		gb.setConstraints(pbDelete, gc);
		mainpanel.add(pbDelete);

		pbSave = new UIButton(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIWorkspaceDialog.saveAsCurrentButton")); //$NON-NLS-1$
		pbSave.setMnemonic(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIWorkspaceDialog.saveAsCurrentButtonMnemonic").charAt(0)); //$NON-NLS-1$
		pbSave.setToolTipText(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIWorkspaceDialog.saveAsCurrentbuttonTip")); //$NON-NLS-1$
		pbSave.addActionListener(this);
		gc.gridy = 2;
		gc.gridx = 1;
		gc.gridwidth=1;
		gc.anchor = GridBagConstraints.EAST;
		gb.setConstraints(pbSave, gc);
		mainpanel.add(pbSave);

		// BOTTOM PANEL
		bottompanel = new JPanel();
		bottompanel.setBorder(new TitledBorder(new EtchedBorder(),
                    LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIWorkspaceDialog.addNewWorkspace"), //$NON-NLS-1$
                    TitledBorder.LEFT,
                    TitledBorder.TOP,
					new Font("Dialog", Font.BOLD, 12) )); //$NON-NLS-1$

		gb = new GridBagLayout();
		gc = new GridBagConstraints();
		bottompanel.setLayout(gb);
		gc.insets = new Insets(5,5,5,5);
		gc.anchor = GridBagConstraints.WEST;

		// the new workspace textfield box
		JLabel lblWorkspace = new JLabel(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIWorkspaceDialog.addCurrentToNew")+":"); //$NON-NLS-1$
		gc.gridy = 1;
		gc.gridwidth = 3;
		gc.weightx = 3.0;
		gb.setConstraints(lblWorkspace, gc);
		bottompanel.add(lblWorkspace);

		tfNewWorkspace = new JTextField(""); //$NON-NLS-1$
		tfNewWorkspace.setColumns(35);
		tfNewWorkspace.setMargin(new Insets(2,2,2,2));
		gc.gridy = 2;
		gc.gridwidth = 2;
		gc.weightx = 2.0;
		gb.setConstraints(tfNewWorkspace, gc);
		bottompanel.add(tfNewWorkspace);

		pbAddToList = new UIButton(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIWorkspaceDialog.addButton")); //$NON-NLS-1$
		pbAddToList.setMnemonic(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIWorkspaceDialog.addButtonMnemonic").charAt(0));
		pbAddToList.setToolTipText(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIWorkspaceDialog.addButtonTip")); //$NON-NLS-1$
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

		pbClose = new UIButton(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIWorkspaceDialog.closeButton")); //$NON-NLS-1$
		pbClose.setMnemonic(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIWorkspaceDialog.closeButtonMnemonic").charAt(0));
		pbClose.addActionListener(this);
		getRootPane().setDefaultButton(pbClose);
		oButtonPanel.addButton(pbClose);

		// Add help button
		pbHelp = new UIButton(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIWorkspaceDialog.helpButton")); //$NON-NLS-1$
		pbHelp.setMnemonic(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIWorkspaceDialog.closeButtonMnemonic").charAt(0));
		ProjectCompendium.APP.mainHB.enableHelpOnButton(pbHelp, "basics.workspaces", ProjectCompendium.APP.mainHS); //$NON-NLS-1$
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
			String sWorkspaceIDs = ""; //$NON-NLS-1$

			int count = info.length;
			for(int i=0; i<count; i++) {
				Vector next = (Vector)info[i];
				String sWorkspaceID = (String)next.elementAt(0);

				if (i < info.length-1)
					sWorkspaceIDs += "'"+sWorkspaceID+"',"; //$NON-NLS-1$ //$NON-NLS-2$
				else
					sWorkspaceIDs += "'"+sWorkspaceID+"'"; //$NON-NLS-1$ //$NON-NLS-2$
			}

			if (!sWorkspaceIDs.equals("")) { //$NON-NLS-1$
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
				ProjectCompendium.APP.displayError(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIWorkspaceDialog.selectWorkspace")); //$NON-NLS-1$
			}
		}
		else if (source.equals(pbAddToList)) {
			String sName = tfNewWorkspace.getText();
			if (sName.equals("")) { //$NON-NLS-1$
				ProjectCompendium.APP.displayError(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIWorkspaceDialog.enterName")); //$NON-NLS-1$
				requestFocus();
				tfNewWorkspace.requestFocus();
			}
			else {
				if (!ProjectCompendium.APP.createWorkspace(sName)) {
					requestFocus();
					tfNewWorkspace.requestFocus();
				}
				else {
					tfNewWorkspace.setText(""); //$NON-NLS-1$
					updateWorkspaceData();
				}
			}
		}
		else if (source.equals(pbClose)) {
			onCancel();
		}
	}
}
