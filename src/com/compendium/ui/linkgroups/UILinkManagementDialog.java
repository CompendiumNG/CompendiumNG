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

package com.compendium.ui.linkgroups;

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
 * UILinkManagementDialog defines the dialog, that allows the user to create and manage their link groups.
 *
 * @author	Michelle Bachler
 */
public class UILinkManagementDialog extends UIDialog implements ActionListener, IUIConstants {

	/**A reference to the system file path separator*/
	private final static String	sFS					= System.getProperty("file.separator"); //$NON-NLS-1$

	/** The current pane to put the dialog contents in.*/
	private Container				oContentPane 	= null;

	/** The button to create a new stencil set.*/
	private JButton					pbCreate 		= null;

	/** The button to edit an existing stencil set.*/
	private JButton					pbEdit 			= null;

	/** The button to duplicate the selected group.*/
	private JButton					pbCopy 			= null;

	/** The button to delete an existing stencil set.*/
	private JButton					pbDelete 		= null;

	/** The button to close the dialog.*/
	private JButton					pbClose 		= null;

	/** The button to set the currently selected link group as the default.*/
	private JButton					pbDefault		= null;

	/** Activates the help opeing to the appropriate section.*/
	private JButton					pbHelp		= null;

	/** The layout manager used.*/
	private	GridBagLayout 			gb 				= null;

	/** The constraints used.*/
	private	GridBagConstraints 		gc 				= null;

	/** The parent frame for this dialog.*/
	private JFrame					oParent			= null;

	/** The stencil manager for this dialog.*/
	private UILinkGroupManager		oManager 		= null;

	/** The counter for the gridbag layout y position.*/
	private int 					gridyStart 		= 0;

	/** The list holding the current stencils.*/
	private UINavList		lstLinkGroups			= null;

	/** The list of current stencil sets.*/
	private Vector 			vtLinkGroups 			= null;

	/** The default link group.*/
	private String			sDefaultGroupID			= ""; //$NON-NLS-1$


	/**
	 * Constructor. Initializes and sets up the dialog.
	 *
	 * @param parent, the frame that is the parent for this dialog.
	 * @param manager, the link group manager instance.
	 */
	public UILinkManagementDialog(JFrame parent, UILinkGroupManager manager) {

		super(parent, true);
		oParent = parent;
		oManager = manager;

		setTitle(LanguageProperties.getString(LanguageProperties.LINKGROUPS_BUNDLE, "UILinkManagementDialog.linkGroups")); //$NON-NLS-1$

		sDefaultGroupID = ProjectCompendium.APP.getActiveLinkGroup();

		oContentPane = getContentPane();
		gb = new GridBagLayout();
		oContentPane.setLayout(gb);

		drawDialog();

		setResizable(false);
		pack();
	}

	/**
	 * Draws the contents of this dialog.
	 */
	private void drawDialog() {

		GridBagConstraints gc = new GridBagConstraints();
		gc.insets = new Insets(5,5,5,5);
		gc.anchor = GridBagConstraints.WEST;

		pbCreate = new UIButton(LanguageProperties.getString(LanguageProperties.LINKGROUPS_BUNDLE, "UILinkManagementDialog.createNew")); //$NON-NLS-1$
		pbCreate.addActionListener(this);
		gc.gridy = gridyStart;
		gridyStart++;
		gc.gridwidth = 3;
		gc.anchor = GridBagConstraints.WEST;
		gb.setConstraints(pbCreate, gc);
		oContentPane.add(pbCreate);

		JLabel lbl = new JLabel(LanguageProperties.getString(LanguageProperties.LINKGROUPS_BUNDLE, "UILinkManagementDialog.currentGroup")); //$NON-NLS-1$
		lbl.setFont(new Font("Dialog", Font.PLAIN, 12)); //$NON-NLS-1$
		gc.gridy = gridyStart;
		gridyStart++;
		gc.gridx = 0;
		gc.weightx = 0;
		gb.setConstraints(lbl, gc);
		oContentPane.add(lbl);

		vtLinkGroups = oManager.getLinkGroups();
		vtLinkGroups = CoreUtilities.sortList(vtLinkGroups);
		lstLinkGroups = new UINavList(vtLinkGroups);
        LinkGroupCellRenderer linkGroupListRenderer = new LinkGroupCellRenderer();
		lstLinkGroups.setCellRenderer(linkGroupListRenderer);
		lstLinkGroups.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION );
 		lstLinkGroups.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent evt) {
				if (evt.getClickCount() == 2) {
					onEdit();
				}
			}
		});

		JScrollPane sp = new JScrollPane(lstLinkGroups);
		sp.setPreferredSize(new Dimension(220,180));
		gc.gridy = gridyStart;
		gridyStart++;
		gc.gridx = 0;
		gc.fill = GridBagConstraints.BOTH;
		gb.setConstraints(sp, gc);
		oContentPane.add(sp);

		gc.gridwidth=1;
		gc.fill = GridBagConstraints.NONE;

		pbEdit = new UIButton(LanguageProperties.getString(LanguageProperties.LINKGROUPS_BUNDLE, "UILinkManagementDialog.edit")); //$NON-NLS-1$
		pbEdit.addActionListener(this);
		gc.gridy = gridyStart;
		gc.gridx = 0;
		gc.anchor = GridBagConstraints.WEST;
		gb.setConstraints(pbEdit, gc);
		oContentPane.add(pbEdit);

		pbCopy = new UIButton(LanguageProperties.getString(LanguageProperties.LINKGROUPS_BUNDLE, "UILinkManagementDialog.duplicate")); //$NON-NLS-1$
		pbCopy.addActionListener(this);
		gc.gridy = gridyStart;
		gc.gridx = 1;
		gc.anchor = GridBagConstraints.WEST;
		gb.setConstraints(pbCopy, gc);
		oContentPane.add(pbCopy);

		pbDelete = new UIButton(LanguageProperties.getString(LanguageProperties.LINKGROUPS_BUNDLE, "UILinkManagementDialog.delete")); //$NON-NLS-1$
		pbDelete.addActionListener(this);
		gc.gridy = gridyStart;
		gc.gridx = 2;
		gc.anchor = GridBagConstraints.EAST;
		gb.setConstraints(pbDelete, gc);
		oContentPane.add(pbDelete);

		gridyStart++;

		pbDefault = new UIButton(LanguageProperties.getString(LanguageProperties.LINKGROUPS_BUNDLE, "UILinkManagementDialog.setActive")); //$NON-NLS-1$
		pbDefault.addActionListener(this);
		gc.gridy = gridyStart;
		gridyStart++;
		gc.gridx = 0;
		gc.gridwidth=2;
		gc.anchor = GridBagConstraints.WEST;
		gb.setConstraints(pbDefault, gc);
		oContentPane.add(pbDefault);

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

		pbClose = new UIButton(LanguageProperties.getString(LanguageProperties.LINKGROUPS_BUNDLE, "UILinkManagementDialog.close")); //$NON-NLS-1$
		pbClose.addActionListener(this);
		gc.gridy = gridyStart;
		gc.anchor = GridBagConstraints.CENTER;
		gb.setConstraints(pbClose, gc);
		oContentPane.add(pbClose);

		// Add help button
		pbHelp = new UIButton(LanguageProperties.getString(LanguageProperties.LINKGROUPS_BUNDLE, "UILinkManagementDialog.help")); //$NON-NLS-1$
		ProjectCompendium.APP.mainHB.enableHelpOnButton(pbHelp, "node.linkgroups", ProjectCompendium.APP.mainHS); //$NON-NLS-1$
		gc.anchor = GridBagConstraints.EAST;
		gb.setConstraints(pbHelp, gc);
		oContentPane.add(pbHelp);
	}

	/**
	 * Helper class that renders the stencil set lists.
	 */
	private class LinkGroupCellRenderer extends JLabel implements ListCellRenderer {

		LinkGroupCellRenderer() {
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

			UILinkGroup group = (UILinkGroup)value;

			Font font = getFont();
			if (!sDefaultGroupID.equals("") && sDefaultGroupID.equals(group.getID()) ) { //$NON-NLS-1$
				this.setFont(new Font("ARIAL", Font.ITALIC, font.getSize())); //$NON-NLS-1$
			}
			else {
				this.setFont(new Font("ARIAL", Font.PLAIN, font.getSize())); //$NON-NLS-1$
			}
 	 		if (isSelected) {
				this.setBackground(list.getSelectionBackground());
				this.setForeground(list.getSelectionForeground());
			}
			else {
				this.setBackground(list.getBackground());
				this.setForeground(list.getForeground());
			}

			String sName = group.getName();
			setText(sName);
			return this;
		}
	}

	/**
	 * Check the passed stencil set name to see if it already exists.
	 * @param sName, the name to check.
	 * @return boolean, true if the name has already been used, else false;
	 */
	public boolean checkName(String sName) {
		return oManager.checkName(sName);
	}

	/**
	 * Update the local data and the LinkGroupManager with the new/edited set
	 * @param oLinkGroup, the UILinkGroup to update.
	 */
	public void updateData(String sOldName, UILinkGroup oLinkGroup) {
		oManager.addLinkGroup(sOldName, oLinkGroup);
		refreshLinkGroups();
	}

	/**
	 * Restore the original data by reloading the link group.
	 * @param oLinkGroup, the UILinkGroup to reload.
	 */
	public void loadFile(UILinkGroup oLinkGroup) {
		String sFileName = oLinkGroup.getFileName();
		try {
			oManager.loadFile("System"+ProjectCompendium.sFS+"resources"+sFS+"LinkGroups"+sFS+sFileName, sFileName); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			refreshLinkGroups();
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Refresh the list of link groups, and the main menu.
	 */
	public void refreshLinkGroups() {
		vtLinkGroups = oManager.getLinkGroups();
		vtLinkGroups = CoreUtilities.sortList(vtLinkGroups);
		lstLinkGroups.setListData(vtLinkGroups);
	}

	/**
	 * Handle action events coming from the buttons.
	 * @param evt, the associated ACtionEvent.
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
			else if (source == pbDefault) {
				onDefault();
			}
			else if (source == pbClose) {
				onCancel();
			}
		}
	}

	/**
	 * Open the dialog to create a new link group.
	 */
	public void onCreate()  {
		UILinkGroup oLinkGroup = new UILinkGroup(oManager);
		String id = ProjectCompendium.APP.getModel().getUniqueID();
		oLinkGroup.setID( id );
		UILinkGroupDialog dlg = new UILinkGroupDialog(oParent, this, oLinkGroup);
		UIUtilities.centerComponent(dlg, oParent);
		dlg.setVisible(true);
	}

	/**
	 * Open the dialog to edit the selected link group.
	 */
	public void onEdit()  {
		int index = lstLinkGroups.getSelectedIndex();
		if (index> -1) {
			UILinkGroup oLinkGroup = (UILinkGroup)lstLinkGroups.getSelectedValue();
			UILinkGroupDialog dlg = new UILinkGroupDialog(oParent, this, oLinkGroup);
			UIUtilities.centerComponent(dlg, oParent);
			dlg.setVisible(true);
		}
		else {
			ProjectCompendium.APP.displayMessage(LanguageProperties.getString(LanguageProperties.LINKGROUPS_BUNDLE, "UILinkManagementDialog.message1"), LanguageProperties.getString(LanguageProperties.LINKGROUPS_BUNDLE, "UILinkManagementDialog.noSelection")); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	/**
	 * Copy the selected link group.
	 */
	public void onCopy()  {
		int index = lstLinkGroups.getSelectedIndex();
		if (index > -1) {

			String sNewName = ""; //$NON-NLS-1$
			boolean bNameExists = false;
			while(!bNameExists) {
	   			sNewName = JOptionPane.showInputDialog(LanguageProperties.getString(LanguageProperties.LINKGROUPS_BUNDLE, "UILinkManagementDialog.message2")); //$NON-NLS-1$
				sNewName = sNewName.trim();

				bNameExists = false;
				if (!sNewName.equals("")) { //$NON-NLS-1$
					if (oManager.checkName(sNewName))
						ProjectCompendium.APP.displayMessage(LanguageProperties.getString(LanguageProperties.LINKGROUPS_BUNDLE, "UILinkManagementDialog.message3"), LanguageProperties.getString(LanguageProperties.LINKGROUPS_BUNDLE, "UILinkManagementDialog.message3Title")); //$NON-NLS-1$ //$NON-NLS-2$
					else
						bNameExists = true;
				}
				else {
					ProjectCompendium.APP.displayMessage(LanguageProperties.getString(LanguageProperties.LINKGROUPS_BUNDLE, "UILinkManagementDialog.message4"), LanguageProperties.getString(LanguageProperties.LINKGROUPS_BUNDLE, "UILinkManagementDialog.message4Title")); //$NON-NLS-1$ //$NON-NLS-2$
				}
			}

			UILinkGroup oLinkGroup = (UILinkGroup)lstLinkGroups.getSelectedValue();
			UILinkGroup duplicate = oLinkGroup.duplicate(sNewName);
			duplicate.saveLinkGroupData();
			updateData(sNewName, duplicate);
		}
		else {
			ProjectCompendium.APP.displayMessage(LanguageProperties.getString(LanguageProperties.LINKGROUPS_BUNDLE, "UILinkManagementDialog.message1"), LanguageProperties.getString(LanguageProperties.LINKGROUPS_BUNDLE, "UILinkManagementDialog.noSelection")); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	/**
	 * Delete the selected link group.
	 */
	public void onDelete()  {
		int index = lstLinkGroups.getSelectedIndex();
		if (index> -1) {
			UILinkGroup oLinkGroup = (UILinkGroup)lstLinkGroups.getSelectedValue();
			oLinkGroup.delete();
			oManager.removeLinkGroup(oLinkGroup);

			if (sDefaultGroupID == oLinkGroup.getID()) {
				sDefaultGroupID = ""; //$NON-NLS-1$
				ProjectCompendium.APP.setActiveLinkGroup(sDefaultGroupID);
			}

			refreshLinkGroups();
		}
		else {
			ProjectCompendium.APP.displayMessage(LanguageProperties.getString(LanguageProperties.LINKGROUPS_BUNDLE, "UILinkManagementDialog.message1"), LanguageProperties.getString(LanguageProperties.LINKGROUPS_BUNDLE, "UILinkManagementDialog.noSelection")); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	/**
	 * Save the current item as the default.
	 */
	public void onDefault()  {
		int index = lstLinkGroups.getSelectedIndex();
		if (index > -1) {
			UILinkGroup oGroup = (UILinkGroup)lstLinkGroups.getSelectedValue();
			if (!sDefaultGroupID.equals("") && sDefaultGroupID.equals(oGroup.getID())) //$NON-NLS-1$
				sDefaultGroupID = ""; //$NON-NLS-1$
			else
				sDefaultGroupID = oGroup.getID();

			ProjectCompendium.APP.setActiveLinkGroup(sDefaultGroupID);
			lstLinkGroups.repaint();
		}
		else {
			ProjectCompendium.APP.displayMessage(LanguageProperties.getString(LanguageProperties.LINKGROUPS_BUNDLE, "UILinkManagementDialog.message5"), LanguageProperties.getString(LanguageProperties.LINKGROUPS_BUNDLE, "UILinkManagementDialog.noSelection")); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	/**
	 * Handle the enter key action. Override superclass to do nothing.
	 */
	public void onEnter() {}
}
