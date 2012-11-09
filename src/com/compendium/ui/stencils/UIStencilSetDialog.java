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

import com.compendium.LanguageProperties;
import com.compendium.ProjectCompendium;
import com.compendium.io.html.*;
import com.compendium.ui.plaf.*;
import com.compendium.ui.*;
import com.compendium.ui.dialogs.*;

/**
 * UIStencilSetDialog defines the dialog, that allows the user to create and manage a stencil set.
 *
 * @author	Michelle Bachler
 */
public class UIStencilSetDialog extends UIDialog implements ActionListener, IUIConstants {

	/** The current pane to put the dialog contents in.*/
	private Container				oContentPane = null;

	/** The button to add a new stencil set item.*/
	private JButton					pbAdd 		= null;

	/** The button to edit an existing stencil set item.*/
	private JButton					pbEdit 		= null;

	/** The button to delete an existing stencil set item.*/
	private JButton					pbDelete 	= null;

	/** The button to save the stencil set.*/
	private JButton					pbSave 		= null;

	/** The button to close the dialog.*/
	private JButton					pbCancel 	= null;

	/** Activates the help opeing to the appropriate section.*/
	private JButton					pbHelp		= null;

	/** The layout manager used.*/
	private	GridBagLayout 			gb 			= null;

	/** The constraints used.*/
	private	GridBagConstraints 		gc 			= null;

	/** The parent frame for this dialog.*/
	private JFrame			oParent 			= null;

	/** The stencil manager for this dialog.*/
	private UIStencilDialog	oManager 			= null;

	/** The counter for the gridbag layout y position.*/
	private int 			gridyStart 			= 0;

	/** The list holding the current stencils.*/
	private UINavList		lstStencilSet		= null;

	/** The stencil set to edit / created.*/
	private UIStencilSet 	oStencilSet			= null;

	/** The text field to hold the stencil set name.*/
	private JTextField		txtName				= null;

	/** The text field to hold the stencil set tab name.*/
	private JTextField		txtTab				= null;

	/** The list of stencil set items.*/
	private Vector 			vtItems 			= null;


	/**
	 * Constructor. Initializes and sets up the dialog.
	 *
	 * @param parent the frame that is the parent for this dialog.
	 * @param manager the parent managing dialog.
	 * @param set the stencil set to edit.
	 */
	public UIStencilSetDialog(JFrame parent, UIStencilDialog manager, UIStencilSet set) {

		super(parent, true);
		oParent = parent;
		oManager = manager;
		oStencilSet = set;

		setTitle(LanguageProperties.getString(LanguageProperties.STENCILS_BUNDLE, "UIStencilSetDialog.stencilSet")); //$NON-NLS-1$

		oContentPane = getContentPane();
		gb = new GridBagLayout();
		oContentPane.setLayout(gb);

		drawDialog();

		pack();
		setResizable(false);
		return;
	}

	/**
	 * Draws the contents of this dialog.
	 */
	private void drawDialog() {

		GridBagConstraints gc = new GridBagConstraints();
		gc.insets = new Insets(5,5,5,5);
		gc.anchor = GridBagConstraints.WEST;

		JLabel lblName = new JLabel(LanguageProperties.getString(LanguageProperties.STENCILS_BUNDLE, "UIStencilSetDialog.fullName")); //$NON-NLS-1$
		lblName.setFont(new Font("Dialog", Font.PLAIN, 12)); //$NON-NLS-1$
		gc.gridy = gridyStart;
		gc.gridx = 0;
		gb.setConstraints(lblName, gc);
		oContentPane.add(lblName);

		txtName = new JTextField(oStencilSet.getName());
		txtName.setFont(new Font("Dialog", Font.PLAIN, 12)); //$NON-NLS-1$
		txtName.setColumns(20);
		txtName.setMargin(new Insets(2,2,2,2));
		txtName.setSize(txtName.getPreferredSize());
		gc.gridy = gridyStart;
		gridyStart++;
		gc.gridx = 1;
		gc.gridwidth=2;
		gc.fill=GridBagConstraints.HORIZONTAL;
		gb.setConstraints(txtName, gc);
		oContentPane.add(txtName);

		JLabel lblTab = new JLabel(LanguageProperties.getString(LanguageProperties.STENCILS_BUNDLE, "UIStencilSetDialog.tabName")); //$NON-NLS-1$
		lblTab.setFont(new Font("Dialog", Font.PLAIN, 12)); //$NON-NLS-1$
		gc.gridy = gridyStart;
		gc.gridx = 0;
		gc.gridwidth=1;
		gb.setConstraints(lblTab, gc);
		oContentPane.add(lblTab);

		txtTab = new JTextField(oStencilSet.getTabName());
		txtTab.setFont(new Font("Dialog", Font.PLAIN, 12)); //$NON-NLS-1$
		txtTab.setColumns(10);
		txtTab.setMargin(new Insets(2,2,2,2));
		txtTab.setSize(txtTab.getPreferredSize());
		gc.gridy = gridyStart;
		gridyStart++;
		gc.gridx = 1;
		gc.gridwidth=2;
		gc.fill=GridBagConstraints.HORIZONTAL;
		gb.setConstraints(txtTab, gc);
		oContentPane.add(txtTab);

		gc.fill = GridBagConstraints.NONE;

		JLabel lbl = new JLabel(LanguageProperties.getString(LanguageProperties.STENCILS_BUNDLE, "UIStencilSetDialog.stecnilSetItem")); //$NON-NLS-1$
		lbl.setFont(new Font("Dialog", Font.PLAIN, 12)); //$NON-NLS-1$
		gc.gridy = gridyStart;
		gridyStart++;
		gc.gridx = 0;
		gc.weightx = 0;
		gc.gridwidth=3;
		gb.setConstraints(lbl, gc);
		oContentPane.add(lbl);

		vtItems = oStencilSet.getItems();
		vtItems = CoreUtilities.sortList(vtItems);

		lstStencilSet = new UINavList(vtItems);
		lstStencilSet.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        StencilListCellRenderer stencilListRenderer = new StencilListCellRenderer();
		lstStencilSet.setCellRenderer(stencilListRenderer);
		lstStencilSet.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent evt) {
				if (evt.getClickCount() == 2) {
					onEdit();
				}
			}
		});
		//lstStencilSet.addMouseListener(createMouseListener());

		JScrollPane sp = new JScrollPane(lstStencilSet);
		sp.setPreferredSize(new Dimension(220,220));
		gc.gridy = gridyStart;
		gridyStart++;
		gc.gridx = 0;
		gc.gridwidth=3;
		gc.fill = GridBagConstraints.BOTH;
		gb.setConstraints(sp, gc);
		oContentPane.add(sp);

		gc.fill = GridBagConstraints.NONE;

		pbAdd = new UIButton(LanguageProperties.getString(LanguageProperties.STENCILS_BUNDLE, "UIStencilSetDialog.addItem")); //$NON-NLS-1$
		pbAdd.addActionListener(this);
		gc.gridy = gridyStart;
		gc.gridx = 0;
		gc.gridwidth = 1;
		gc.anchor = GridBagConstraints.WEST;
		gb.setConstraints(pbAdd, gc);
		oContentPane.add(pbAdd);

		pbEdit = new UIButton(LanguageProperties.getString(LanguageProperties.STENCILS_BUNDLE, "UIStencilSetDialog.edit")); //$NON-NLS-1$
		pbEdit.addActionListener(this);
		gc.gridy = gridyStart;
		gc.gridx = 1;
		gc.gridwidth=1;
		gc.weightx=1.0;
		gc.anchor = GridBagConstraints.EAST;
		gb.setConstraints(pbEdit, gc);
		oContentPane.add(pbEdit);

		pbDelete = new UIButton(LanguageProperties.getString(LanguageProperties.STENCILS_BUNDLE, "UIStencilSetDialog.delete")); //$NON-NLS-1$
		pbDelete.addActionListener(this);
		gc.gridy = gridyStart;
		gridyStart++;
		gc.gridx = 2;
		gc.gridwidth=1;
		gc.weightx=2.0;
		gc.anchor = GridBagConstraints.EAST;
		gb.setConstraints(pbDelete, gc);
		oContentPane.add(pbDelete);

		JSeparator sep = new JSeparator();
		gc.gridy = gridyStart;
		gridyStart++;
		gc.gridx = 0;
		gc.anchor = GridBagConstraints.WEST;
		gc.fill = GridBagConstraints.BOTH;
		gc.gridwidth = 3;
		gc.weightx=1.0;
		gb.setConstraints(sep, gc);
		oContentPane.add(sep);

		gc.fill = GridBagConstraints.NONE;

		pbSave = new UIButton(LanguageProperties.getString(LanguageProperties.STENCILS_BUNDLE, "UIStencilSetDialog.save")); //$NON-NLS-1$
		pbSave.addActionListener(this);
		gc.gridy = gridyStart;
		gc.gridx = 0;
		gc.gridwidth=1;
		gc.weightx=1.0;
		gc.anchor = GridBagConstraints.WEST;
		gb.setConstraints(pbSave, gc);
		oContentPane.add(pbSave);

		pbCancel = new UIButton(LanguageProperties.getString(LanguageProperties.STENCILS_BUNDLE, "UIStencilSetDialog.cancel")); //$NON-NLS-1$
		pbCancel.addActionListener(this);
		gc.gridx = 1;
		gc.gridwidth=1;
		gc.weightx=2.0;
		gc.anchor = GridBagConstraints.EAST;
		gb.setConstraints(pbCancel, gc);
		oContentPane.add(pbCancel);

		pbHelp = new UIButton(LanguageProperties.getString(LanguageProperties.STENCILS_BUNDLE, "UIStencilSetDialog.help")); //$NON-NLS-1$
		ProjectCompendium.APP.mainHB.enableHelpOnButton(pbHelp, "basics.stencils", ProjectCompendium.APP.mainHS); //$NON-NLS-1$
		gc.gridx = 2;
		gc.gridwidth=1;
		gc.weightx=1.0;
		gc.anchor = GridBagConstraints.EAST;
		gb.setConstraints(pbHelp, gc);
		oContentPane.add(pbHelp);
	}

	/**
	 * Helper class that renders the stencil set lists.
	 */
	private class StencilListCellRenderer extends JLabel implements ListCellRenderer {

		StencilListCellRenderer() {
        	super();
			setOpaque(true);
		}

		public Component getListCellRendererComponent(
        	JList list,
            Object value,
            int modelIndex,
            boolean isSelected,
            boolean cellHasFocus)
            {

			DraggableStencilIcon dicon = (DraggableStencilIcon)value;

			String sImage = dicon.getPaletteImage();
			if (sImage.equals("")) //$NON-NLS-1$
				sImage = dicon.getImage();

			ImageIcon icon = UIImages.thumbnailIcon(sImage);
			String sName = dicon.getToolTip();

 	 		if (isSelected) {
				this.setBackground(list.getSelectionBackground());
				this.setForeground(list.getSelectionForeground());
			}
			else {
				this.setBackground(list.getBackground());
				this.setForeground(list.getForeground());
			}

			setIcon(icon);
			int nShortcut = dicon.getShortcut();
			if (nShortcut == -1)
				setText(sName);
			else
				setText(sName+" ALT + "+nShortcut); //$NON-NLS-1$
			return this;
		}
	}

	/**
	 * Add the given item to the set.
	 * @param oIcon the item to add to the set.
	 */
	public void addItem(DraggableStencilIcon oIcon) {
		oStencilSet.addStencilItem(oIcon);
		refreshStencilSet();
	}

	/**
	 * Remove the given item from the set.
	 * @param oIcon the item to remove from the set.
	 */
	public void removeItem(DraggableStencilIcon oIcon) {
		oStencilSet.removeStencilItem(oIcon);
		refreshStencilSet();
	}

	/**
	 * Refresh the list of stencil icons
	 */
	public void refreshStencilSet() {
		vtItems = oStencilSet.getItems();
		vtItems = CoreUtilities.sortList(vtItems);
		lstStencilSet.setListData(vtItems);
	}

	/**
	 * Handle action events coming from the buttons.
	 * @param evt the associated ACtionEvent.
	 */
	public void actionPerformed(ActionEvent evt) {
		Object source = evt.getSource();

		// Handle button events
		if (source instanceof JButton) {
			if (source == pbAdd) {
				onAdd();
			}
			else if (source == pbEdit) {
				onEdit();
			}
			else if (source == pbDelete) {
				onDelete();
			}
			else if (source == pbSave) {
				onSave();
			}
			else if (source == pbCancel) {
				oManager.loadFile(oStencilSet);
				onCancel();
			}
		}
	}

	/**
	 * Open the dialog to create a new stencil set.
	 */
	public void onAdd()  {
		DraggableStencilIcon oItem = new DraggableStencilIcon();
		UIStencilItemDialog dlg = new UIStencilItemDialog(oParent, this, oItem);
		UIUtilities.centerComponent(dlg, oParent);
		dlg.setVisible(true);
	}


	/**
	 * Open the dialog to edit the given stencil set item.
	 * @param oItem the DraggableStencilIcon to edit.
	 */
	public void onAutoEdit(DraggableStencilIcon oItem)  {
		lstStencilSet.setSelectedValue(oItem, true);
		if (lstStencilSet.getSelectedIndex() > -1) {
			UIStencilItemDialog dlg = new UIStencilItemDialog(oParent, this, oItem);
			UIUtilities.centerComponent(dlg, oParent);
			dlg.setVisible(true);
		}
		else
			ProjectCompendium.APP.displayMessage(LanguageProperties.getString(LanguageProperties.STENCILS_BUNDLE, "UIStencilSetDialog.notFound"), LanguageProperties.getString(LanguageProperties.STENCILS_BUNDLE, "UIStencilSetDialog.noFoundTitle")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * Open the dialog to edit the selected stencil set item.
	 */
	public void onEdit()  {
		int index = lstStencilSet.getSelectedIndex();
		if (index > -1) {
			DraggableStencilIcon oItem = (DraggableStencilIcon)lstStencilSet.getSelectedValue();
			UIStencilItemDialog dlg = new UIStencilItemDialog(oParent, this, oItem);
			UIUtilities.centerComponent(dlg, oParent);
			dlg.setVisible(true);
		}
		else {
			ProjectCompendium.APP.displayMessage(LanguageProperties.getString(LanguageProperties.STENCILS_BUNDLE, "UIStencilSetDialog.selectItem"), LanguageProperties.getString(LanguageProperties.STENCILS_BUNDLE, "UIStencilSetDialog.noSelection")); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	/**
	 * Open the dialog to create a new stencil set.
	 */
	public void onDelete()  {
		int index = lstStencilSet.getSelectedIndex();
		if (index > -1) {
			DraggableStencilIcon oItem = (DraggableStencilIcon)lstStencilSet.getSelectedValue();
			removeItem(oItem);
		}
		else {
			ProjectCompendium.APP.displayMessage(LanguageProperties.getString(LanguageProperties.STENCILS_BUNDLE, "UIStencilSetDialog.selectItem"), LanguageProperties.getString(LanguageProperties.STENCILS_BUNDLE, "UIStencilSetDialog.noSelection")); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	/**
	 * Open the dialog to create a new stencil set.
	 */
	public void onSave()  {
		String newName = txtName.getText();
		String oldName = oStencilSet.getName();

		if (!newName.equals("")) { //$NON-NLS-1$
			if (!newName.equals(oldName) && oManager.checkName(newName)) {
				ProjectCompendium.APP.displayMessage(LanguageProperties.getString(LanguageProperties.STENCILS_BUNDLE, "UIStencilSetDialog.stencilSetExists"), LanguageProperties.getString(LanguageProperties.STENCILS_BUNDLE, "UIStencilSetDialog.duplicateName")); //$NON-NLS-1$ //$NON-NLS-2$
				txtName.requestFocus();
			}
			else {
				// IF YOU HAVE CHANGED THE STENCIL SET NAME, THE FOLDERS AND PATHS WILL NEED CHANGING
				if (!oldName.equals("") && !newName.equals(oldName)) { //$NON-NLS-1$
					oStencilSet.setName(newName);
					oStencilSet.setTabName(txtTab.getText());
					oStencilSet.saveToNew(newName);
				}
				else {
					oStencilSet.setName(newName);
					oStencilSet.setTabName(txtTab.getText());
					oStencilSet.saveStencilData();
				}
				oManager.updateData(oldName, oStencilSet);
				onCancel();
			}
		}
		else {
			ProjectCompendium.APP.displayMessage(LanguageProperties.getString(LanguageProperties.STENCILS_BUNDLE, "UIStencilSetDialog.warningGiveName"), LanguageProperties.getString(LanguageProperties.STENCILS_BUNDLE, "UIStencilSetDialog.missingName")); //$NON-NLS-1$ //$NON-NLS-2$
			txtName.requestFocus();
		}
	}

	/**
	 * Handle the enter key action. Override superclass to do nothing.
	 */
	public void onEnter() {}
}
