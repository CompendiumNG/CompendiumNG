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

package com.compendium.ui.stencils;

import java.util.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.table.*;
import javax.swing.border.*;

import com.compendium.core.*;
import com.compendium.core.datamodel.*;
import com.compendium.core.db.*;

import com.compendium.ProjectCompendium;
import com.compendium.io.html.*;
import com.compendium.ui.plaf.*;
import com.compendium.ui.*;
import com.compendium.ui.dialogs.*;

/**
 * UIStencilDialog defines the dialog, that allows the user to create and manage their stencil sets.
 *
 * @author	Michelle Bachler
 */
public class UIStencilDialog extends UIDialog implements ActionListener, IUIConstants {

	/** The current pane to put the dialog contents in.*/
	private Container				oContentPane = null;

	/** The button to create a new stencil set.*/
	private JButton					pbCreate = null;

	/** The button to edit an existing stencil set.*/
	private JButton					pbEdit = null;

	/** The button to delete an existing stencil set.*/
	private JButton					pbDelete = null;

	/** The button to close the dialog.*/
	private JButton					pbClose = null;

	/** The button to duplicate the selected stencil set.*/
	private JButton					pbCopy 	= null;

	/** Activates the help opeing to the appropriate section.*/
	private JButton					pbHelp		= null;

	/** The layout manager used.*/
	private	GridBagLayout 			gb = null;

	/** The constraints used.*/
	private	GridBagConstraints 		gc = null;

	/** The parent frame for this dialog.*/
	private JFrame					oParent = null;

	/** The stencil manager for this dialog.*/
	private UIStencilManager		oManager = null;

	/** The counter for the gridbag layout y position.*/
	private int gridyStart = 0;

	/** The list holding the current stencils.*/
	private UINavList		lstStencils			= null;

	/** The list of current stencil sets.*/
	private Vector 			vtStencils 			= null;

	/**
	 * Constructor. Initializes and sets up the dialog.
	 *
	 * @param parent the frame that is the parent for this dialog.
	 * @param manager the stencil manager instance.
	 */
	public UIStencilDialog(JFrame parent, UIStencilManager manager) {

		super(parent, true);
		oParent = parent;
		oManager = manager;

		setTitle("Stencils");

		oContentPane = getContentPane();
		gb = new GridBagLayout();
		oContentPane.setLayout(gb);

		drawDialog();

		pack();
		setResizable(false);
	}

	/**
	 * Draws the contents of this dialog.
	 */
	private void drawDialog() {

		GridBagConstraints gc = new GridBagConstraints();
		gc.insets = new Insets(5,5,5,5);
		gc.anchor = GridBagConstraints.WEST;

		pbCreate = new UIButton("Create New Stencil Set");
		pbCreate.addActionListener(this);
		gc.gridy = gridyStart;
		gridyStart++;
		gc.gridwidth = 3;
		gc.anchor = GridBagConstraints.WEST;
		gb.setConstraints(pbCreate, gc);
		oContentPane.add(pbCreate);

		JLabel lbl = new JLabel("Current Stencils:");
		lbl.setFont(new Font("Dialog", Font.PLAIN, 12));
		gc.gridy = gridyStart;
		gridyStart++;
		gc.gridx = 0;
		gc.weightx = 0;
		gb.setConstraints(lbl, gc);
		oContentPane.add(lbl);

		vtStencils = oManager.getStencilNames();
		vtStencils = CoreUtilities.sortList(vtStencils);
		lstStencils = new UINavList(vtStencils);
		lstStencils.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION );
 		lstStencils.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent evt) {
				if (evt.getClickCount() == 2) {
					onEdit();
				}
			}
		});

		JScrollPane sp = new JScrollPane(lstStencils);
		sp.setPreferredSize(new Dimension(220,180));
		gc.gridy = gridyStart;
		gridyStart++;
		gc.gridx = 0;
		gc.fill = GridBagConstraints.BOTH;
		gb.setConstraints(sp, gc);
		oContentPane.add(sp);

		gc.gridwidth=1;
		gc.fill = GridBagConstraints.NONE;

		pbEdit = new UIButton("Edit");
		pbEdit.addActionListener(this);
		gc.gridy = gridyStart;
		gc.gridx = 0;
		gc.anchor = GridBagConstraints.WEST;
		gb.setConstraints(pbEdit, gc);
		oContentPane.add(pbEdit);

		pbCopy = new UIButton("Duplicate");
		pbCopy.addActionListener(this);
		gc.gridy = gridyStart;
		gc.gridx = 1;
		gc.anchor = GridBagConstraints.WEST;
		gb.setConstraints(pbCopy, gc);
		oContentPane.add(pbCopy);

		pbDelete = new UIButton("Delete");
		pbDelete.addActionListener(this);
		gc.gridy = gridyStart;
		gc.gridx = 2;
		gridyStart++;
		gc.anchor = GridBagConstraints.EAST;
		gb.setConstraints(pbDelete, gc);
		oContentPane.add(pbDelete);

		JSeparator sep = new JSeparator();
		gc.gridy = gridyStart;
		gc.gridwidth = 3;
		gc.gridx = 0;
		gridyStart++;
		gc.anchor = GridBagConstraints.WEST;
		gc.fill = GridBagConstraints.BOTH;
		gb.setConstraints(sep, gc);
		oContentPane.add(sep);

		gc.fill = GridBagConstraints.NONE;

		pbClose = new UIButton("Close");
		pbClose.addActionListener(this);
		gc.gridy = gridyStart;
		gc.gridx=0;
		gc.anchor = GridBagConstraints.CENTER;
		gb.setConstraints(pbClose, gc);
		oContentPane.add(pbClose);

		// Add help button
		pbHelp = new UIButton("Help");
		ProjectCompendium.APP.mainHB.enableHelpOnButton(pbHelp, "basics.stencils", ProjectCompendium.APP.mainHS);
		gc.gridx=1;
		gc.anchor = GridBagConstraints.EAST;
		gb.setConstraints(pbHelp, gc);
		oContentPane.add(pbHelp);
	}

	/**
	 * Check the passed stencil set name to see if it already exists.
	 * @param sName the name to check.
	 * @return boolean true if the name has already been used, else false;
	 */
	public boolean checkName(String sName) {
		return oManager.checkName(sName);
	}

	/**
	 * Update the local data and the StencilManager with the new/edited set
	 * @param oStencilSet the UIStencilSet to update.
	 */
	public void updateData(String sOldName, UIStencilSet oStencilSet) {
		oManager.addStencilSet(sOldName, oStencilSet);
		oStencilSet.refreshStencilSet();
		refreshStencils();
	}

	/**
	 * restore the original data by reloading the stencil set.
	 * @param oStencilSet the UIStencilSet to reload.
	 */
	public void loadFile(UIStencilSet oStencilSet) {
		String sFolderName = oStencilSet.getFolderName();
		String sFileName = oStencilSet.getFileName();
		try {
			oManager.loadFile("Stencils"+ProjectCompendium.sFS+sFolderName+ProjectCompendium.sFS+sFileName, sFileName, sFolderName);
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Refresh the list of stencils, and the main menu.
	 */
	public void refreshStencils() {
		ProjectCompendium.APP.refreshStencilMenu();
		vtStencils = oManager.getStencilNames();
		vtStencils = CoreUtilities.sortList(vtStencils);
		lstStencils.setListData(vtStencils);
	}

	/**
	 * Handle action events coming from the buttons.
	 * @param evt the associated ACtionEvent.
	 */
	public void actionPerformed(ActionEvent evt) {
		Object source = evt.getSource();

		// Handle button events
		if (source instanceof JButton) {
			if (source == pbCreate) {
				onCreate();
			}
			else if (source == pbEdit) {
				onEdit();
			}
			else if (source == pbCopy) {
				onCopy();
			}
			else if (source == pbDelete) {
				onDelete();
			}
			else if (source == pbClose) {
				onCancel();
			}
		}
	}

	/**
	 * Open the dialog to create a new stencil set.
	 */
	public void onCreate()  {
		UIStencilSet oStencilSet = new UIStencilSet(oManager);
		UIStencilSetDialog dlg = new UIStencilSetDialog(oParent, this, oStencilSet);
		UIUtilities.centerComponent(dlg, oParent);
		dlg.setVisible(true);
	}

	/**
	 * Open the dialog to edit the stencil set passed.
	 * @param oSet the UIStencilSet to edit.
	 * @param oItem the DraggableStencilIcon to edit, or null.
	 */
	public void onAutoEdit(UIStencilSet oSet, DraggableStencilIcon oItem)  {
		String sName = (String)oSet.getName();
		lstStencils.setSelectedValue(sName, true);
		UIStencilSet oStencilSet = oManager.getStencilSet(sName);
		UIStencilSetDialog dlg = new UIStencilSetDialog(oParent, this, oStencilSet);
		UIUtilities.centerComponent(dlg, oParent);

		if (oItem != null) {
			if (lstStencils.getSelectedIndex() > -1)
				dlg.onAutoEdit(oItem);
			else
				ProjectCompendium.APP.displayMessage("Stencil Set not found", "Stencil Management");
		}
		dlg.setVisible(true);
	}

	/**
	 * Open the dialog to edit the selected stencil set.
	 */
	public void onEdit()  {
		int index = lstStencils.getSelectedIndex();
		if (index> -1) {
			String sName = (String)lstStencils.getSelectedValue();
			UIStencilSet oStencilSet = oManager.getStencilSet(sName);
			UIStencilSetDialog dlg = new UIStencilSetDialog(oParent, this, oStencilSet);
			UIUtilities.centerComponent(dlg, oParent);
			dlg.setVisible(true);
		}
		else {
			ProjectCompendium.APP.displayMessage("Please select a stencil set first", "No Selection Made");
		}
	}

	/**
	 * Copy the selected link group.
	 */
	public void onCopy()  {
		int index = lstStencils.getSelectedIndex();
		if (index > -1) {

			String sNewName = "";
			boolean bNameExists = false;
			while(!bNameExists) {
	   			sNewName = JOptionPane.showInputDialog("Enter the name for the new Stencil Set");
				sNewName = sNewName.trim();

				bNameExists = false;
				if (!sNewName.equals("")) {
					if (oManager.checkName(sNewName))
						ProjectCompendium.APP.displayMessage("You already have a Stencil Set with that name, please try again", "Duplicate Name");
					else
						bNameExists = true;
				}
				else {
					ProjectCompendium.APP.displayMessage("Please enter a Stencil Set name", "No Name");
				}
			}

			String sName = (String)lstStencils.getSelectedValue();
			UIStencilSet oStencilSet = oManager.getStencilSet(sName);
			UIStencilSet duplicate = oStencilSet.duplicate(sNewName);
			duplicate.saveStencilData();
			updateData(sNewName, duplicate);
		}
		else {
			ProjectCompendium.APP.displayMessage("Please select a Stencil Set first", "No Selection Made");
		}
	}

	/**
	 * Delete the selected stencil set.
	 */
	public void onDelete()  {
		int index = lstStencils.getSelectedIndex();
		if (index> -1) {
			String sName = (String)lstStencils.getSelectedValue();
			UIStencilSet oStencilSet = oManager.getStencilSet(sName);
			oStencilSet.delete();
			oManager.removeStencilSet(oStencilSet);
			refreshStencils();
		}
		else {
			ProjectCompendium.APP.displayMessage("Please select a stencil set first", "No Selection Made");
		}
	}

	/**
	 * Handle the enter key action. Override superclass to do nothing.
	 */
	public void onEnter() {}
}
