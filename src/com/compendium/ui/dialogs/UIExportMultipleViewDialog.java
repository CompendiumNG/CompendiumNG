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
import java.io.*;
import java.sql.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;
import javax.swing.table.*;

import com.compendium.*;
import com.compendium.core.datamodel.*;
import com.compendium.core.datamodel.services.*;
import com.compendium.io.html.*;

import com.compendium.ui.plaf.*;
import com.compendium.ui.*;
import com.compendium.ui.panels.*;

/**
 * UIExportMultipleViewDialog displays a list of views opened, to select for export.
 *
 * @author	Michelle Bachler
 */
public class UIExportMultipleViewDialog extends UIDialog implements ActionListener, IUIConstants {

	/** The button to save the selected views.*/
	private UIButton				pbSave			= null;

	/** The button to close the dialog.*/
	private UIButton				pbClose			= null;

	/** The button to select all the views in the list.*/
	private UIButton				pbSelectAll 	= null;

	/** The button to open the relevant help.*/
	private UIButton				pbHelp		 	= null;

	/** the parent JDialog for this dialog.*/
	private UIDialog				oParent			= null;

	/** Check to select all the view in the current map.*/
	private JCheckBox				cbCurrentViewMaps = null;

	/** The current View when this dialog was opened.*/
	private View					currentView 	= null;

	/** The UIViewPanel which display the list of Views.*/
	private UIViewPanel 			viewsPanel 		= null;

	/** The current pane to draw the content for this dialog in.*/
	private Container				oContentPane 	= null;


	/**
	 * Constrcutor. Initializes and sets up the dialog.
	 * @param parent, the JDialog that is the parent for this dialog.
	 */
	public UIExportMultipleViewDialog(UIDialog parent) {

		super(parent, true);
	  	this.setTitle(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIExportMultipleViewDialog.viewsToExport")); //$NON-NLS-1$
	  	oParent = parent;
	  	String userID = ProjectCompendium.APP.getModel().getUserProfile().getId();

		JPanel mainPanel = new JPanel(new BorderLayout());

		oContentPane = getContentPane();
		oContentPane.setLayout(new BorderLayout());

		viewsPanel = new UIViewPanel(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIExportMultipleViewDialog.selectViews")+":" , userID); //$NON-NLS-1$
		mainPanel.add(viewsPanel, BorderLayout.NORTH);

		// Add export button
		UIButtonPanel oButtonPanel = new UIButtonPanel();

		pbSave = new UIButton(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIExportMultipleViewDialog.okButton")); //$NON-NLS-1$
		pbSave.setMnemonic(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIExportMultipleViewDialog.okButtonMnemonic").charAt(0)); //$NON-NLS-1$
		pbSave.addActionListener(this);
		getRootPane().setDefaultButton(pbSave);
		oButtonPanel.addButton(pbSave);

		pbSelectAll = new UIButton(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIExportMultipleViewDialog.selectAllButton")); //$NON-NLS-1$
		pbSelectAll.setMnemonic(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIExportMultipleViewDialog.selectAllButtonMnemonic").charAt(0)); //$NON-NLS-1$
		pbSelectAll.addActionListener(this);
		oButtonPanel.addButton(pbSelectAll);

		pbClose = new UIButton(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIExportMultipleViewDialog.cancelButton")); //$NON-NLS-1$
		pbClose.setMnemonic(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIExportMultipleViewDialog.cancelButtonMnemonic").charAt(0)); //$NON-NLS-1$
		pbClose.addActionListener(this);
		oButtonPanel.addButton(pbClose);

		pbHelp = new UIButton(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIExportMultipleViewDialog.helpButton")); //$NON-NLS-1$
		pbHelp.setMnemonic(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIExportMultipleViewDialog.helpButtonMnemonic").charAt(0)); //$NON-NLS-1$
		ProjectCompendium.APP.mainHB.enableHelpOnButton(pbHelp, "io.export_html_outline", ProjectCompendium.APP.mainHS); //$NON-NLS-1$
		oButtonPanel.addHelpButton(pbHelp);

		oContentPane.add(mainPanel, BorderLayout.CENTER);
		oContentPane.add(oButtonPanel, BorderLayout.SOUTH);

		pack();
		setResizable(false);
	}



	/******* EVENT HANDLING METHODS *******/

	/**
	 * Handle action events coming from the buttons.
	 * @param evt, the associated ActionEvent.
	 */
	public void actionPerformed(ActionEvent evt) {

		Object source = evt.getSource();

		// Handle button events
		if (source instanceof JButton) {
			if (source == pbSave) {
				onSave();
			}
			else if (source == pbSelectAll) {
				onSelectAll();
			}
			else if (source == pbClose) {
				onCancel();
			}
		}
	}

	/**
	 * Set the current view.
	 * @param view com.compendium.core.datamodel.View, the current view.
	 */
	public void setCurrentView(View view) {
		currentView = view;
	}

	/**
	 * Select All the views in the views panel list.
	 */
	private void onSelectAll() {
		viewsPanel.onSelectAll();
	}

	/**
	 * Process a save request. (just hides this dialog).
	 */
	public void onSave() {
		setVisible(false);
		if (oParent instanceof UIExportDialog) {
			UIExportDialog dlg = (UIExportDialog)oParent;
			dlg.updateViewsList();

		} else if (oParent instanceof UIExportViewDialog) {
			UIExportViewDialog dlg = (UIExportViewDialog)oParent;
			dlg.updateViewsList();			
		}
	}

	/**
	 * Return the table holding the list of Views.
	 * @return JTable, holding the list of views.
	 */
	public JTable getTable() {
		return viewsPanel.getTable();
	}

	/**
	 * Override superclass to clear the current table selection if the dialog is closed without saving.
	 */
	public void onCancel() {
		getTable().clearSelection();
		setVisible(false);
	}
}
