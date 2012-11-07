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
import java.lang.*;
import java.io.*;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;
import javax.swing.table.*;

import com.compendium.LanguageProperties;
import com.compendium.ProjectCompendium;
import com.compendium.io.xml.*;
import com.compendium.ui.plaf.*;
import com.compendium.ui.*;

import com.compendium.core.datamodel.*;
import com.compendium.core.datamodel.services.*;
import com.compendium.core.ICoreConstants;
import com.compendium.core.CoreUtilities;


/**
 * UIDropFileDialog defines a drag and drop dialog, that allows
 * the user to select the type of drop processing to perform on the file, e.g. XML
 *
 * @author	Michelle Bachler
 */
public class UIDropFileDialog extends UIDialog implements ActionListener {

	/** The pane to add the content for this dialog to.*/
	private Container			oContentPane = null;

	/** The parent frame for this dialog.*/
	private JFrame				oParent	= null;

	/** The button to begin the drop processing.*/
	private JButton				pbProcess	= null;

	/** The button to cancel the drop.*/
	private JButton				pbCancel	= null;

	/** The button to select if the drop should be processed as a Word import.*/
	private JRadioButton		rbWord = null;

	/** The button to select if the drop should be processed as an XML import.*/
	private	JRadioButton		rbXML = null;

	/** The button to select if the drop should be processed as a Zip file containing an Compendium XML file to unpack.*/
	private	JRadioButton		rbXMLZip = null;

	/** The button to select of the drop should be processed as a reference node.*/
	private JRadioButton		rbReference = null;

	/** The title for this dialog.*/
	private String				sTitle = LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIDropFileDialog.dndFile"); //$NON-NLS-1$

	/** The author name for the current user.*/
	private String 				author = ""; //$NON-NLS-1$

	/** The current UIViewpane, if the current view is a map.*/
	private UIViewPane 			uiViewPane = null;

	/** The current ViewPaneUI, if the current view is a map.*/
	private ViewPaneUI			viewPaneUI = null;

	/** The current UIList, if the current view is a list.*/
	private UIList				uiList = null;

	/** The current ListUI, if the current view is a list.*/
	private ListUI				listUI = null;

	/** Th x position of the drop.*/
	private int					nX = 0;

	/** The y position of the drop.*/
	private int 				nY = 0;

	/** The name of the file dropped.*/
	private File 				file = null;


	/**
	 * Constructor for a map view drop. Initializes and sets up the dialog.
	 * @param parent, the parent frame for this dialog.
	 * @param pane com.compendium.ui.UIViewPane, the current view.
	 * @param file, the file being dropped.
	 * @param x, the x position of the drop.
	 * @param y, the y position of the drop.
	 */
	public UIDropFileDialog(JFrame parent, UIViewPane pane, File file, int x, int y) {

		super(parent, true);

	  	oParent = parent;
		uiViewPane = pane;
		viewPaneUI = pane.getUI();
		this.file = file;
		nX = x;
		nY = y;

	  	setTitle(sTitle);
		drawDialog();
	}

	/**
	 * Constructor for a list view drop. Initializes and sets up the dialog.
	 * @param parent, the parent frame for this dialog.
	 * @param list com.compendium.ui.UIList, the current view.
	 * @param file, the file being dropped.
	 * @param x, the x position of the drop.
	 * @param y, the y position of the drop.
	 */
	public UIDropFileDialog(JFrame parent, UIList list, File file, int x, int y) {

		super(parent, true);

	  	oParent = parent;
		uiList = list;
		listUI = list.getListUI();
		this.file = file;
		nX = x;
		nY = y;

	  	setTitle(sTitle);
		drawDialog();
	}

	/**
	 * Draw the contents of the dialog.
	 */
	private void drawDialog() {

		author = ProjectCompendium.APP.getModel().getUserProfile().getUserName();

		oContentPane = getContentPane();
		GridBagLayout gb = new GridBagLayout();
		GridBagConstraints gc = new GridBagConstraints();
		oContentPane .setLayout(gb);

		gc.insets = new Insets(5,10,5,5);
		gc.anchor = GridBagConstraints.WEST;
		gc.gridwidth=2;

		int y=0;

		rbReference = new JRadioButton(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIDropFileDialog.processDropRefNodeRadio")); //$NON-NLS-1$
		rbReference.setSelected(true);
		gc.gridy = y;
		y++;
		gb.setConstraints(rbReference, gc);
		oContentPane .add(rbReference);

		rbXML = new JRadioButton(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIDropFileDialog.processDropXMLRadio")); //$NON-NLS-1$
		rbXML.setSelected(false);
		gc.gridy = y;
		y++;
		gb.setConstraints(rbXML, gc);
		oContentPane .add(rbXML);

		rbXMLZip = new JRadioButton(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIDropFileDialog.processDropXMLZipRadio")); //$NON-NLS-1$
		rbXMLZip.setSelected(false);
		gc.gridy = y;
		y++;
		gb.setConstraints(rbXMLZip, gc);
		oContentPane .add(rbXMLZip);

		//rbWord = new JRadioButton("Process drop as Word Text");
		//rbWord.setSelected(false);
		//rbWord.setEnabled(false);
		//gc.gridy = y;
		//y++;
		//gb.setConstraints(rbWord, gc);
		//oContentPane .add(rbWord);

		ButtonGroup group1 = new ButtonGroup();
		group1.add(rbReference);
		group1.add(rbXML);
		group1.add(rbXMLZip);
		//group1.add(rbWord);

		gc.insets = new Insets(15,10,5,5);

		// Add export button
		pbProcess = new UIButton(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIDropFileDialog.processDropButton")); //$NON-NLS-1$
		pbProcess.setMnemonic(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIDropFileDialog.processDropButtonMnemonic").charAt(0));
		pbProcess.addActionListener(this);
		pbProcess.requestFocus();
		getRootPane().setDefaultButton(pbProcess);
		gc.gridy = y;
		gc.gridwidth=1;
		gc.anchor = GridBagConstraints.WEST;
		gb.setConstraints(pbProcess, gc);
		oContentPane .add(pbProcess);

		// Add close button
		pbCancel = new UIButton(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIDropFileDialog.cancelButton")); //$NON-NLS-1$
		pbCancel.setMnemonic(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIDropFileDialog.cancelButtonMnemonic").charAt(0));
		pbCancel.addActionListener(this);
		gc.gridy = y;
		gc.anchor = GridBagConstraints.EAST;
		gc.gridwidth=1;
		gb.setConstraints(pbCancel, gc);
		oContentPane .add(pbCancel);

		pack();
		setResizable(false);
		return;
	}

	/**
	 * Handle action events coming from the buttons.
	 * @param evt, the associated ActionEvent.
	 */
	public void actionPerformed(ActionEvent evt) {

		Object source = evt.getSource();
		if (source instanceof JButton) {
			if (source == pbProcess) {
				onProcessDrop();
			}
			else if (source == pbCancel) {
				onCancel();
			}
		}
	}

	/**
	 * Handle the processing of the drop.
	 */
	public void onProcessDrop() {

		if (rbXML.isSelected()) {
			processAsXML();
		}
		else if (rbXMLZip.isSelected()) {
			processAsZippedXML();
		}
		else {
			processAsReference();
			onCancel();
		}
	}

	/**
	 * Process the drop as a reference node drop.
	 */
	private void processAsReference() {
		uiViewPane.createNode(uiViewPane.getView(), file, nX, nY, FormatProperties.dndProperties.clone());
	}

	/**
	 * Process the drop as an XML import.
	 */
	private void processAsXML() {
		setVisible(false);
		ProjectCompendium.APP.onFileXMLImport(file);
		dispose();
	}

	/**
	 * Process the drop as a zipped XML import.
	 */
	private void processAsZippedXML() {
		setVisible(false);
		try {
			UIUtilities.unzipXMLZipFile(file.getAbsolutePath(), true);
		} catch(IOException io) {
			ProjectCompendium.APP.displayError(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIDropFileDialog.errorUnableToProcess")+":\n\n"+io.getMessage()); //$NON-NLS-1$
		}
		dispose();
	}
}
