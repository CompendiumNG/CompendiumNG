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

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import com.compendium.LanguageProperties;
import com.compendium.ProjectCompendium;
import com.compendium.ui.UIButton;
import com.compendium.ui.UIButtonPanel;
import com.compendium.ui.UILinkedFilesTable;

/**
 * View database stored and local Linked files.
 * @author Sebastian Ehrich, alterations Michelle Bachler
 */
public class UILinkedFilesBrowser extends UIDialog implements ActionListener {


	/**
	 * Auto generated UID 
	 */
	private static final long serialVersionUID = -5856981546500045895L;

	/** The pane for the dialog's content.*/
	private Container		oContentPane		= null;

	/** The parent frame for this dialog.*/
	private JFrame			oParent				= null;	
		
	/** The button to extract the selected file(s)
	 * and save it to the file system */	
	private UIButton 		pbExtract 			= null;
	
	/** The button to delete the selected file(s) */
	private UIButton		pbDelete			= null;
	
	/** The button to close the dialog */
	private UIButton		pbClose				= null;
				
	/** The table to list the linked files */
	private UILinkedFilesTable 	tblLinkFiles	= null;
		
	public UILinkedFilesBrowser(JFrame parent) {
		super(parent, true);

		this.setSize(new Dimension(800,800));
		this.setMinimumSize(new Dimension(200,200));
		oParent = parent;		
		setTitle(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UILinkedFilesBrowser.linkedFilesBrowserTitle"));				 //$NON-NLS-1$
		oContentPane = getContentPane();
		oContentPane.setLayout(new BorderLayout());
		oContentPane.add(createMainPanel(), BorderLayout.CENTER);
		oContentPane.add(createButtonPanel(), BorderLayout.SOUTH);

		tblLinkFiles.updateFileTable();
		
		pack();
	}
	
	private JPanel createMainPanel() {
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBorder(new EmptyBorder(10,10,10,10));

		JLabel lblFiles = new JLabel(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UILinkedFilesBrowser.projectLinkedFile")+":");		 //$NON-NLS-1$
		panel.add(lblFiles, BorderLayout.NORTH);
		
		tblLinkFiles = new UILinkedFilesTable(this);				
		JScrollPane sp = new JScrollPane(tblLinkFiles.getTable());

		panel.add(sp, BorderLayout.CENTER);
		
		JPanel upperButtonPanel = new JPanel();
		upperButtonPanel.setBorder(new EmptyBorder(0,0,10,0));

		// Add update button
		pbExtract = new UIButton(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UILinkedFilesBrowser.extractButton")); //$NON-NLS-1$
		pbExtract.setMnemonic(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UILinkedFilesBrowser.extractButtonMnemonic").charAt(0));
		pbExtract.addActionListener(this);
		upperButtonPanel.add(pbExtract);

		// Add delete button
		pbDelete = new UIButton(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UILinkedFilesBrowser.deleteButton")); //$NON-NLS-1$
		pbDelete.setMnemonic(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UILinkedFilesBrowser.deleteButtonMnemonic").charAt(0));
		pbDelete.addActionListener(this);
		upperButtonPanel.add(pbDelete);

		panel.add(upperButtonPanel, BorderLayout.SOUTH);		
		
		return panel;
	}
	
	/**
	 * Create and return the button panel.
	 */
	private UIButtonPanel createButtonPanel() {

		UIButtonPanel oButtonPanel = new UIButtonPanel();

		// Add close button
		pbClose = new UIButton(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UILinkedFilesBrowser.closeButton")); //$NON-NLS-1$
		pbClose.setMnemonic(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UILinkedFilesBrowser.closeButtonMnemonic").charAt(0));
		pbClose.addActionListener(this);
		getRootPane().setDefaultButton(pbClose);
		oButtonPanel.addButton(pbClose);
		return oButtonPanel;
	}	
		
	/**
	 * Handles button push events.
	 * @param e the associated ActionEvent object.
	 */
	public void actionPerformed(ActionEvent evt) {
		Object source = evt.getSource();
		// Handle button events
		if (source instanceof JButton) {

			if (source == pbExtract) {
				onExtract();
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
	 * Triggered when the delete button is pressed.
	 */
	private void onDelete() {
		int[] selectedFiles = tblLinkFiles.getTable().getSelectedRows();		
		if (selectedFiles.length < 1) {
			ProjectCompendium.APP.displayMessage(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UILinkedFilesBrowser.selectFileToDelete"), LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UILinkedFilesBrowser.deleteFilesTitle")); //$NON-NLS-1$ //$NON-NLS-2$
		} else {			
			// ask for confirmation
			int response = JOptionPane.showConfirmDialog(oParent, LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UILinkedFilesBrowser.areYouSure"), //$NON-NLS-1$
					LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UILinkedFilesBrowser.deleteFilesTitle"), JOptionPane.YES_NO_OPTION); //$NON-NLS-1$
			if (response == JOptionPane.YES_OPTION) { 		
				tblLinkFiles.onDelete();
			}	
		}
	}
	
	/**
	 * Triggered when the extract button is pressed.
	 *
	 */
	private void onExtract() {
		int[] selectedFiles = tblLinkFiles.getTable().getSelectedRows();
		if (selectedFiles.length < 1) {
			ProjectCompendium.APP.displayMessage(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UILinkedFilesBrowser.9"), LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UILinkedFilesBrowser.extractFilesTitle")); //$NON-NLS-1$ //$NON-NLS-2$
		} else {			
			// open dialog to select the directory in which to extract the
			// files to
			JFileChooser dirChooser = new JFileChooser();
			dirChooser.setDialogTitle(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UILinkedFilesBrowser.chooseDir")); //$NON-NLS-1$
			dirChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			dirChooser.setAcceptAllFileFilterUsed(false);
		    if (dirChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {		    	
		    	File chosenDir = dirChooser.getSelectedFile();		    
		    	tblLinkFiles.onExtract(chosenDir);
		    }	
		}
	}
}
