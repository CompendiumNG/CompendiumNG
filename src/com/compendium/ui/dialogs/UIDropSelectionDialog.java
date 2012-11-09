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

import java.util.*;
import java.lang.*;
import java.io.*;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;
import javax.swing.table.*;

import com.compendium.core.ICoreConstants;
import com.compendium.core.datamodel.*;
import com.compendium.core.datamodel.services.*;

import com.compendium.ProjectCompendium;
import com.compendium.io.xml.*;
import com.compendium.ui.plaf.*;
import com.compendium.ui.*;


/**
 * UIDropSelectionDialog defines the drag and drop dialog, that allows
 * the user to select the drag origin, and therefore the type of drop processing to perform
 *
 * @author	Michelle Bachler
 */
public class UIDropSelectionDialog extends UIDialog implements ActionListener {

	/** The pane to put the dialog's contents in.*/
	private Container			oContentPane = null;

	/** The parent frame for this dialog.*/
	private JFrame				oParent	= null;

	/** The button to process the drop.*/
	private JButton				pbProcess	= null;

	/** The button to cancel the drop and close the dialog.*/
	private JButton				pbCancel	= null;

	/** The button to process the drop as Word text - NOT IMPLEMENTED YET.*/
	private JRadioButton		rbWord = null;

	/** The button to process the drop as Excelt text - SPECIFIC FORMAT ONLY, SEE HELP DOCS.*/
	private	JRadioButton		rbExcel = null;

	/** The button to process the text as a note node.*/
	private JRadioButton		rbPlain = null;

	/** The title for this dialog.*/
	private String				sTitle = "Drag and Drop Selection";

	/** The String of data being dropped.*/
	private String 				dropData = "";

	/** The UIViewPane instance if the target view is a map.*/
	private UIViewPane 			uiViewPane = null;

	/** The ViewPaneUI instance if the target view is a map.*/
	private ViewPaneUI			viewPaneUI = null;

	/** The UIList instance if the target view is a list.*/
	private UIList				uiList = null;

	/** The ListUI instance if the target view is a list.*/
	private ListUI				listUI = null;

	/** The x position of the drop.*/
	private int					nX = 0;

	/** The y position of the drop.*/
	private int 				nY = 0;

	/** The current author used for any newly created nodes.*/
	private String 				author = null;



	/**
	 * Initializes and sets up the dialog. Used when the drop target is a map view.
	 *
	 * @param parent, the parent frame for this dialog.
	 * @param pane com.compendium.ui.UIViewPane, the drop target view.
	 * @param x, the x position for the drop.
	 * @param y, the y position for the drop.
	 */
	public UIDropSelectionDialog(JFrame parent, UIViewPane pane, String data, int x, int y) {

		super(parent, true);

	  	oParent = parent;
		uiViewPane = pane;
		viewPaneUI = pane.getUI();
		dropData = data;
		nX = x;
		nY = y;

	  	setTitle(sTitle);
		drawDialog();
	}

	/**
	 * Initializes and sets up the dialog. Used when the drop target is a list view.
	 *
	 * @param parent, the parent frame for this dialog.
	 * @param list com.compendium.ui.UIList, the drop target view.
	 * @param x, the x position for the drop.
	 * @param y, the y position for the drop.
	 */
	public UIDropSelectionDialog(JFrame parent, UIList list, String data, int x, int y) {

		super(parent, true);

	  	oParent = parent;
		uiList = list;
		listUI = list.getListUI();
		dropData = data;
		nX = x;
		nY = y;

	  	setTitle(sTitle);
		drawDialog();
	}

	/**
	 * Draws the contents of this dialog.
	 */
	public void drawDialog() {

		author = ProjectCompendium.APP.getModel().getUserProfile().getUserName();

		oContentPane = getContentPane();
		GridBagLayout gb = new GridBagLayout();
		GridBagConstraints gc = new GridBagConstraints();
		oContentPane .setLayout(gb);

		gc.insets = new Insets(5,10,5,5);
		gc.anchor = GridBagConstraints.WEST;
		gc.gridwidth=2;

		int y=0;

		rbPlain = new JRadioButton("Process drop as Plain Text");
		rbPlain.setSelected(true);
		gc.gridy = y;
		y++;
		gb.setConstraints(rbPlain, gc);
		oContentPane .add(rbPlain);

		rbExcel = new JRadioButton("Process drop as Excel Text");
		rbExcel.setSelected(false);
		gc.gridy = y;
		y++;
		gb.setConstraints(rbExcel, gc);
		oContentPane .add(rbExcel);

		rbWord = new JRadioButton("Process drop as Word Text");
		rbWord.setSelected(false);
		rbWord.setEnabled(false);
		gc.gridy = y;
		y++;
		gb.setConstraints(rbWord, gc);
		oContentPane .add(rbWord);

		ButtonGroup group1 = new ButtonGroup();
		group1.add(rbPlain);
		group1.add(rbExcel);
		group1.add(rbWord);

		gc.insets = new Insets(15,10,5,5);

		// Add export button
		pbProcess = new UIButton("Process Drop...");
		pbProcess.addActionListener(this);
		pbProcess.requestFocus();
		getRootPane().setDefaultButton(pbProcess);
		gc.gridy = y;
		gc.gridwidth=1;
		gc.anchor = GridBagConstraints.WEST;
		gb.setConstraints(pbProcess, gc);
		oContentPane .add(pbProcess);

		// Add close button
		pbCancel = new UIButton("Cancel");
		pbCancel.addActionListener(this);
		gc.gridy = y;
		gc.anchor = GridBagConstraints.EAST;
		gc.gridwidth=1;
		gb.setConstraints(pbCancel, gc);
		oContentPane .add(pbCancel);

		pack();
		setResizable(false);
	}

	/**
	 * Handle button push events.
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
	 * Handle the drop of text from an external application.
	 */
	public void onProcessDrop() {

		if (rbWord.isSelected())
			processAsWord();
		else if (rbExcel.isSelected())
			processAsExcel();
		else
			processAsPlain();

		onCancel();
	}

	/**
	 * Process the dropped text as plain text and just create a note node.
	 */
	public void processAsPlain() {

		if (viewPaneUI != null) {
			UINode node = addNodeToMap(viewPaneUI, dropData, ICoreConstants.NOTE, nX, nY);
		}
		else if (listUI != null) {
			String detail = "";
			//if (dropData.length() > 100) {
			//	detail = dropData.substring(100);
			//	dropData = dropData.substring(0, 100);
			//}

			NodePosition node = listUI.createNode(ICoreConstants.NOTE,
				 "",
				 ProjectCompendium.APP.getModel().getUserProfile().getUserName(),
				 dropData,
				 detail,
				 nX,
				 nY
				 );

			uiList.updateTable();
			uiList.selectNode(uiList.getNumberOfNodes() - 1, ICoreConstants.MULTISELECT);
		}
	}

	/**
	 * Processes a line of dropped text as an Excel spreadsheet row.
	 */
	private Vector parseExcelRow(String line) {

		Vector row = new Vector(10);

		while(line.length() > 0) {

			int inner = line.indexOf("\t");
			if (inner != -1) {
				String item = line.substring(0, inner);

				if (inner < line.length())
					line = line.substring(inner+1);

				row.addElement(item.trim());
				//System.out.println("item = "+item);
			}
			else {
				//System.out.println("item = "+line.trim());

				row.addElement(line.trim());
				line = "";
			}
			line.trim();
		}

		return row;
	}

	/**
	 * Parses the dropped text as Excel data.
	 */
	private Vector parseExcelData() {

		String data = dropData;

		Vector table = new Vector(10);
		Vector row = null;

		int maxLength = 0;

		while (data.length() > 0) {
			int index = data.indexOf("\n");

			if (index == -1) {
			    index = data.indexOf("\r");
			    if (index != -1) {

				String line = data.substring(0, index);
				System.out.println("row: "+line);

				row = parseExcelRow(line);
				table.addElement(row);

				if (index < data.length())
					data = data.substring(index+1);
			    }
			    else {
					String line = data.trim();

				data = "";
				//System.out.println("row: "+line);

				row = parseExcelRow(line);
				table.addElement(row);
			    }
			}
			else {
			    String line = data.substring(0, index);
			    //System.out.println("row: "+line);

			    row = parseExcelRow(line);
			    table.addElement(row);

			    if (index < data.length())
				data = data.substring(index+1);
			}
			data.trim();
		}

		// PAD ROWS IF REQUIRED

		return table;
	}

	/**
	 * Process the dropped text as Excel text and process as per specified rules.
	 * See Compendium help files for details.
	 */
	private void processAsExcel() {

		Vector table = parseExcelData();

		Vector topRow = (Vector)table.elementAt(0);

		if (viewPaneUI != null) {

			int firstLevelCount = table.size()-1;
			int secondLevelCount = topRow.size()-1;

			int ySpacer = 80;
			int xSpacer = 300;

			int x2 = 10 + (xSpacer*2);
			int y2 = 0;

			int x1 = 10 + xSpacer;
			int y1 = ((firstLevelCount * secondLevelCount * ySpacer) / 2) - ((firstLevelCount*80)/2);

			int x0 = 10;
			int y0 = y1 + ( ((firstLevelCount*80)/2) - (ySpacer/2));

			int x3 = 10 + (xSpacer*3);

			try {

				String rootLabel = (String)topRow.elementAt(0);
				UINode rootMap = addNodeToMap(viewPaneUI, rootLabel, ICoreConstants.MAPVIEW, nX, nY);

				IView rootView = (IView)rootMap.getNode();

				NodePosition rootQuestion = addNodeToView(rootView, rootLabel, ICoreConstants.ISSUE, x0, y0);
				INodeSummary rootNode = rootQuestion.getNode();

				for (int i=1; i<= firstLevelCount; i++) {

					Vector row = (Vector)table.elementAt(i);
					String label = (String)row.elementAt(0);

					INodePosition nodePos1 = addNodeToView(rootView, label, ICoreConstants.ISSUE, x1, y1);
					INodeSummary nodeSum1 = nodePos1.getNode();

					rootView.addMemberLink(ICoreConstants.DEFAULT_LINK, "", author, nodeSum1, rootNode, ICoreConstants.ARROW_TO);

					for (int j=1; j<=secondLevelCount; j++) {
						String label2 = (String)topRow.elementAt(j);

						INodePosition nodePos2 = addNodeToView(rootView, label2, ICoreConstants.ISSUE, x2, y2);
						INodeSummary nodeSum2 = nodePos2.getNode();
						rootView.addMemberLink(ICoreConstants.DEFAULT_LINK, "", author, nodeSum2, nodeSum1, ICoreConstants.ARROW_TO);

						String label3 = (String)row.elementAt(j);

						INodePosition nodePos3 = addNodeToView(rootView, label3, ICoreConstants.POSITION, x3, y2);
						INodeSummary nodeSum3 = nodePos3.getNode();
						rootView.addMemberLink(ICoreConstants.DEFAULT_LINK, "", author, nodeSum3, nodeSum2, ICoreConstants.ARROW_TO);

						y2 += ySpacer;
					}

					y1 += ySpacer;
				}
			}
			catch(Exception ex) {
				ex.printStackTrace();
			}
		}
		else if (listUI != null) {

		}
	}

	/**
	 * TO BE IMPLEMENTED.
	 */
	private void processAsWord() {

	}

	/**
	 * Create a new node with the given attributes.
	 *
	 * @param view com.compendium.core.datamodel.View, the view to add the node to.
	 * @param label, the label for the new node.
	 * @param type, the type of the new node.
	 * @param x, the x position for the new node.
	 * @param y, the y position for the new node.
	 */
	private NodePosition addNodeToView(IView view, String label, int type, int x, int y) throws Exception {

		NodePosition pos = null;
		pos = view.addMemberNode(type, "", "", author, label, "", x, y);
		return pos;
	}

	/**
	 * Create a new node in the given view with the given attributes.
	 *
	 * @param viewPane com.compendium.ui.plaf.ViewPaneUI, the view to add the node to.
	 * @param text, the label for the new node.
	 * @param type, the type of the new node.
	 * @param x, the x position for the new node.
	 * @param y, the y position for the new node.
	 */
	private UINode addNodeToMap(ViewPaneUI viewPane, String text, int type, int x, int y) {

		UINode node = viewPane.addNewNode(type, x, y);
		try {
			node.setText(text);
			node.getUI().refreshBounds();
		}
		catch(Exception ex) {
			System.out.println("Error: (UIDropSelectionDialog.addNodeToMap)\n\n"+ex.getMessage());
		}

		return node;
	}
}
