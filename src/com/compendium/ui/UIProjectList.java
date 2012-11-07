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

package com.compendium.ui;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.sql.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;
import javax.help.*;

import com.compendium.*;
import com.compendium.ui.*;


import com.compendium.core.*;
import com.compendium.core.datamodel.*;
import com.compendium.core.db.management.*;
import com.compendium.core.CoreUtilities;

/**
 * This object draws a list of projects with schema status indicators
 *
 * @author	Michelle Bachler
 */
public class UIProjectList extends UINavList {


	/** Holds the list of Derby projects to convert.*/
	private Vector			vtProjects	= new Vector();

	/** Database projects against their requirement to have thier schemas updated.*/
	//private Hashtable		htProjectCheck		= new Hashtable(10);

	/**
	 * Constructor. Initializes and draws the contents of this list.
	 *
	 * @param vtProjects, the list of current projects.
	 */
	public UIProjectList(Vector vtProjects) {
	//public UIProjectList(Hashtable htProjectCheck, Vector vtProjects) {

		super(new DefaultListModel());

		//this.htProjectCheck = htProjectCheck;
		this.vtProjects = vtProjects;

		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		setCellRenderer(new StatusListCellRenderer());
		addMouseMotionListener( new MouseMotionListener() {
			public void mouseMoved(java.awt.event.MouseEvent e) {
				JList list = (JList)e.getSource();

				Point p = e.getPoint();

				int dialogWidth = (new Double(list.getWidth()*0.95)).intValue();
				if (p.getX() > dialogWidth) {
					int index = locationToIndex(p);
					JLabel label = (JLabel)getModel().getElementAt(index);
					setToolTipText(label.getToolTipText());
				}
				else {
					setToolTipText(null);
				}
			}

			public void mouseDragged(java.awt.event.MouseEvent e) {}
		});

		updateProjectList();
	}

	/**
	 * Update the list contents.
	 */
	public void updateProjectList() {
		updateProjectList(null, null, ""); //$NON-NLS-1$
	}

	/**
	 * Update the list contents.
	 * @param vtData, the data to use to update the list contents.
	 */
	public void updateProjectList(Vector vtData) {
		updateProjectList(vtData, null, ""); //$NON-NLS-1$
	}

	/**
	 * Update the list contents.
	 *
	 * @param vtData, the data to use to update the list contents.
	 * @param htCheckData, the data to use to check the project status'.
	 */
	public void updateProjectList(Vector vtData, Hashtable htCheckData) {
		updateProjectList(vtData, htCheckData, ""); //$NON-NLS-1$
	}


	/**
	 * Update the list contents.
	 *
	 * @param vtData, the data to use to update the list contents.
	 * @param htCheckData, the data to use to check the project status'.
	 * @param sModal, the name of the project to select, else and empty string.
	 */
	public void updateProjectList(Vector vtData, Hashtable htCheckData, String sModel) {

		if (vtData != null)
			vtProjects = vtData;

		//if (htCheckData != null)
		//	htProjectCheck = htCheckData;

		((DefaultListModel)getModel()).removeAllElements();

		JLabel selectedLabel = null;

		for(Enumeration e = vtProjects.elements();e.hasMoreElements();) {

			String project = (String)e.nextElement();
			ImageIcon img = null;
			img = UIImages.getNodeIcon(IUIConstants.MAP_SM_ICON);
			JLabel label = new JLabel(project,img,SwingConstants.LEFT);

			if (project.equals(sModel))
				selectedLabel = label;

			/*if (htProjectCheck.containsKey(project)) {
				int status = ((Integer)htProjectCheck.get(project)).intValue();
				if (status == ICoreConstants.NEWER_DATABASE_SCHEMA) {
					label.setToolTipText(IUIConstants.PROJECT_SCHEMA_NEWER);
				}
				else if (status == ICoreConstants.OLDER_DATABASE_SCHEMA) {
					label.setToolTipText(IUIConstants.PROJECT_SCHEMA_OLDER);
				}
				else if (status == ICoreConstants.CORRECT_DATABASE_SCHEMA) {
					label.setToolTipText(IUIConstants.PROJECT_SCHEMA_CORRECT);
				}
				else {
					label.setToolTipText(IUIConstants.PROJECT_SCHEMA_UNKNOWN);
				}
			}
			else {
				label.setToolTipText(IUIConstants.PROJECT_SCHEMA_UNKNOWN);
			}*/

			((DefaultListModel)getModel()).addElement(label);
		}

		if (selectedLabel == null && vtProjects.size() > 0)
			setSelectedIndex(0);
		else if (vtProjects.size() > 0)
			setSelectedValue(selectedLabel, true);
	}

	/**
	 * Helper class to render the elements of the list.
	 */
	private class StatusListCellRenderer extends JPanel implements ListCellRenderer {

		JLabel label		=	null;
		protected Border noFocusBorder;

		StatusListCellRenderer() {
			super();
			noFocusBorder = new EmptyBorder(1, 1, 1, 1);
			setBorder(noFocusBorder);

			setLayout(new BorderLayout());
			label = new JLabel();
			label.setOpaque(true);
		}

		public Component getListCellRendererComponent(
			JList list,
			Object value,
			int modelIndex,
			boolean isSelected,
			boolean cellHasFocus)
			{

			removeAll();

			label = (JLabel)value;
			JLabel statusLabel = new JLabel();
			statusLabel.setOpaque(true);
			statusLabel.setFont(new Font("Monospaced", Font.PLAIN, 12)); //$NON-NLS-1$

			String sName = label.getText();

			if (isSelected) {
				setBackground(list.getSelectionBackground());
				setForeground(list.getSelectionForeground());

				label.setBackground(list.getSelectionBackground());

				if (ProjectCompendium.APP.getDefaultDatabase().equals(sName))
					label.setForeground(IUIConstants.DEFAULT_COLOR);
				else
					label.setForeground(list.getSelectionForeground());

				statusLabel.setBackground(list.getSelectionBackground());
				statusLabel.setForeground(list.getSelectionForeground());
			}
			else {
				setBackground(list.getBackground());
				setForeground(list.getForeground());

				label.setBackground(list.getBackground());

				if (ProjectCompendium.APP.getDefaultDatabase().equals(sName))
					label.setForeground(IUIConstants.DEFAULT_COLOR);
				else
					label.setForeground(list.getForeground());

				statusLabel.setBackground(list.getBackground());
				statusLabel.setForeground(list.getForeground());
			}

			/*if (htProjectCheck.containsKey(sName)) {
				int status = ((Integer)htProjectCheck.get(sName)).intValue();
				if (status == ICoreConstants.NEWER_DATABASE_SCHEMA) {
					statusLabel.setIcon( UIImages.get(IUIConstants.RED_LIGHT_ICON));
					label.setEnabled(false);
				}
				else if (status == ICoreConstants.OLDER_DATABASE_SCHEMA) {
					statusLabel.setIcon( UIImages.get(IUIConstants.YELLOW_LIGHT_ICON));
				}
				else if (status == ICoreConstants.CORRECT_DATABASE_SCHEMA) {
					statusLabel.setIcon( UIImages.get(IUIConstants.GREEN_LIGHT_ICON));
				}
				else {
					statusLabel.setText("Unknown Status");
				}
			}
			else {
				statusLabel.setText("Unknown Status");
			}*/

			add(statusLabel, BorderLayout.EAST);

			setBorder((cellHasFocus) ? UIManager.getBorder("List.focusCellHighlightBorder") : noFocusBorder); //$NON-NLS-1$
			add(label, BorderLayout.CENTER);
			return this;
		}
	}
}
