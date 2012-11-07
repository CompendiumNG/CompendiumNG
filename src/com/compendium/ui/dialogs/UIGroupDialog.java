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
import java.io.*;
import java.awt.Container;
import java.awt.Color;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.Document;

import com.compendium.LanguageProperties;
import com.compendium.ProjectCompendium;
import com.compendium.ui.*;
import com.compendium.core.datamodel.NodeSummary;
import com.compendium.core.datamodel.Group;


/**
 * THIS CLASS IS NOT CURRENTLY USED OR COMPLETED
 * <p>
 * Assign a user to one or more user groups.
 *
 * @author	Mohammed Sajid Ali / Michelle Bachler
 * @version	1.0
 */
public class UIGroupDialog extends UIDialog implements ActionListener, DocumentListener, MouseListener {

	private static final int WIDTH			= 450;
	private static final int HEIGHT			= 525;
	private	final static int TOPOFFSET		= 12;
	private	final static int LEFTOFFSET		= 12;
	private	final static int RIGHTOFFSET	= 12;
	private	final static int PBHEIGHT		= 24;
	private	final static int PBWIDTH		= 84;

	public	JButton			pbOK				= null;
	public  JButton			pbAdd				= null;
	public  JButton			pbRemove			= null;
	public  JButton			pbAddToList			= null;

	private Container		oParent				= null;
	private Container		oContentPane		= null;
	private JScrollPane		sp					= null;
	private JScrollPane		sp1					= null;
	private UINavList		lstGroups			= null;
	private UINavList		lstGroupsAdded		= null;
	public  JLabel			lblGroupsList		= null;
	public  JLabel			lblGroupsAddedList 	= null;
	private JTextField		tfNewGroup			= null;
	private JRadioButton	rbLabel				= null;
	private JRadioButton	rbDetail			= null;
	private Document		oLabelDoc			= null;
	private Document		oDetailDoc			= null;
	private boolean			bLabelChange		= false;
	private boolean			bDetailChange		= false;
	private int				nHeight				= HEIGHT;
	private int				nWidth				= WIDTH;
	private Vector			vtGroups			= new Vector();
	private Vector			vtGroupsAdded		= new Vector();
	private JDialog			oDialog				= null;
	private String			sUser				= ""; //$NON-NLS-1$


	/**
	 * Constructor
	 */
	public UIGroupDialog(JFrame parent, JDialog dialog, String user) {

		super(parent, true);
		oParent = parent;
		oDialog = dialog;
		sUser = user;

		setResizable(false);

		setTitle(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIGroupDialog.groupAssignmentTitle")); //$NON-NLS-1$

		oContentPane = getContentPane();
		oContentPane.setLayout(null);

		showGroupPalette();
	}

	public void showGroupPalette() {

		// Add label
		JLabel lblUser = new JLabel(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIGroupDialog.user")+": " + sUser); //$NON-NLS-1$
		oContentPane.add(lblUser);
		lblUser.setBounds(LEFTOFFSET,TOPOFFSET,100,24);

		// Add label
		JLabel lblGroups = new JLabel(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIGroupDialog.groups")+":"); //$NON-NLS-1$
		oContentPane.add(lblGroups);
		lblGroups.setBounds(LEFTOFFSET,TOPOFFSET+48,70,24);

		// Create the list
		lstGroups = new UINavList(new DefaultListModel());
		lstGroups.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION );
		lstGroups.setCellRenderer(new LabelListCellRenderer());
		lstGroups.setBackground(Color.white);
		lstGroups.addMouseListener(this);

		sp = new JScrollPane(lstGroups);
		oContentPane.add(sp);
		sp.setBounds(LEFTOFFSET,TOPOFFSET + 72,200,200);


		// Add label
		lblGroups = new JLabel(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIGroupDialog.groupsAdded")+":"); //$NON-NLS-1$
		oContentPane.add(lblGroups);
		lblGroups.setBounds(LEFTOFFSET+200,TOPOFFSET+48,70,24);

		// Create the list
		lstGroupsAdded = new UINavList(new DefaultListModel());
		lstGroupsAdded.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION );
		lstGroupsAdded.setCellRenderer(new LabelListCellRenderer());
		lstGroupsAdded.setBackground(Color.white);
		lstGroupsAdded.addMouseListener(this);

		sp1 = new JScrollPane(lstGroupsAdded);
		oContentPane.add(sp1);
		sp1.setBounds(LEFTOFFSET+200,TOPOFFSET + 72,200,200);

		// the new group textfield box
		lblGroups = new JLabel(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIGroupDialog.newGroup")+":"); //$NON-NLS-1$
		oContentPane.add(lblGroups);
		lblGroups.setBounds(LEFTOFFSET,350,200,24);
		tfNewGroup = new JTextField(""); //$NON-NLS-1$
		tfNewGroup.setBounds(LEFTOFFSET,374,200,24);
		oContentPane.add(tfNewGroup);

		pbAddToList = new UIButton(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIGroupDialog.addToListButton")); //$NON-NLS-1$
		pbAddToList.addActionListener(this);
		pbAddToList.setBounds(LEFTOFFSET+200,374,120,24);
		oContentPane.add(pbAddToList);

		pbAdd = new UIButton(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIGroupDialog.addButton")); //$NON-NLS-1$
		pbAdd.setMnemonic(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIGroupDialog.addButtonMnemonic").charAt(0));
		pbAdd.addActionListener(this);
		pbAdd.setBounds(LEFTOFFSET,300,84,24);
		pbAdd.setEnabled(true);
		oContentPane.add(pbAdd);

		pbRemove = new UIButton(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIGroupDialog.removeButton")); //$NON-NLS-1$
		pbRemove.setMnemonic(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIGroupDialog.removeButtonMnemonic").charAt(0));
		pbRemove.addActionListener(this);
		pbRemove.setBounds(LEFTOFFSET+200,300,84,24);
		pbAdd.setEnabled(false);
		oContentPane.add(pbRemove);

		pbOK = new UIButton(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIGroupDialog.okButton")); //$NON-NLS-1$
		pbOK.setMnemonic(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIGroupDialog.okButtonMnemonic").charAt(0));
		pbOK.addActionListener(this);
		pbOK.setBounds(WIDTH-RIGHTOFFSET-PBWIDTH,468,PBWIDTH,PBHEIGHT);
		oContentPane.add(pbOK);

		// update the groups lists..
		updateGroupsList();
		updateGroupsAddedList();

		pack();
		setSize(WIDTH,HEIGHT);
	}

	public void changedUpdate(DocumentEvent evt) {}

	public void insertUpdate(DocumentEvent evt) {
		changed(evt);
	}

	public void removeUpdate(DocumentEvent evt) {
		changed(evt);
	}

	private void changed(DocumentEvent evt) {

	}


	/**
	 * Handle a button push event.
	 * @param evt, the associated ActionEvent.
	 */
	public void actionPerformed(ActionEvent evt) {

		Object source = evt.getSource();
		if (source == pbOK)
			onCancel();
		if (source == pbAdd)
			onAdd();
		if (source == pbRemove)
			onRemove();
		if (source == pbAddToList)
			onAddToList();
	}

	public void onAdd() {

	}

	public void onAddToList() {

	}

	public void onRemove() {

	}

	private void updateGroupsList() {

	}

	public void updateGroupsAddedList() {

	}

  	/**
   	 * Invoked when the mouse has been clicked on a component.
	 * @param evt, the associated MouseEvent.
   	 */
  	public void mouseClicked(MouseEvent e) {

		if(e.getClickCount() == 2) {

			Object source = e.getSource();
		  	if(source == lstGroups)
				onAdd();
		  	if(source == lstGroupsAdded)
				onRemove();
	  	}
		if(lstGroups.hasFocus()) {
			pbAdd.setEnabled(true);
			pbRemove.setEnabled(false);
			lstGroupsAdded.clearSelection();
		}
		else if(lstGroupsAdded.hasFocus()) {
			pbRemove.setEnabled(true);
			pbAdd.setEnabled(false);
			lstGroups.clearSelection();
		}
  	}

  	/**
   	 * Invoked when a mouse button has been pressed on a component - DOES NOTHING.
	 * @param evt, the associated MouseEvent.
   	 */
  	public void mousePressed(MouseEvent e) {}

  	/**
   	 * Invoked when a mouse button has been released on a component - DOES NOTHING
	 * @param evt, the associated MouseEvent.
   	 */
  	public void mouseReleased(MouseEvent e) {}

  	/**
   	 * Invoked when the mouse enters a component - DOES NOTHING.
	 * @param evt, the associated MouseEvent.
   	 */
  	public void mouseEntered(MouseEvent e) {}

  	/**
   	 * Invoked when the mouse exits a component - DOES NOTHING.
	 * @param evt, the associated MouseEvent.

   	 */
  	public void mouseExited(MouseEvent e) {}

	/**
	 * Invoked when a mouse is dragged in a component - DOES NOTHING.
	 * @param evt, the associated MouseEvent.
	 */
	public void mouseDragged(MouseEvent evt) {}

	/**
	 * Handle the enter key action. Override superclass to do nothing.
	 */
	public void onEnter() {}
}
