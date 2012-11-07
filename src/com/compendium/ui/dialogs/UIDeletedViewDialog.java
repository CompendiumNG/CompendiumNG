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
import java.io.*;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.*;

import com.compendium.LanguageProperties;
import com.compendium.ProjectCompendium;

import com.compendium.ui.plaf.*;
import com.compendium.ui.*;

import com.compendium.core.datamodel.*;
import com.compendium.core.datamodel.services.INodeService;
import com.compendium.core.*;

/**
 * Displays a list of all views that the associated node has been deleted from.
 * Allows  the associated node to be restored to selected views if desired.
 *
 * @author	Michelle Bachler
 */
public class UIDeletedViewDialog extends UIDialog implements ActionListener, IUIConstants {

	/** The pane to draw this dialog's contents in.*/
	private Container			oContentPane		= null;

	/** The scrollpane holding the list of view the node has been deleted from.*/
	private JScrollPane			sp 					= null;

	/** The list of view that the associated node has been deleted from.*/
	private UINavList			lstViews 			= null;

	/** The label for the list of views the node has been deleted from.*/
	private JLabel 				lblViews			= null;

	/** The button to restored the associated node to the selected views.*/
	private UIButton			pbRestore			= null;

	/** The button to select all view in the list.*/
	private UIButton			pbSelectAll 		= null;

	/** The button to view the linking info for a node in a view - not currently used.*/
	private UIButton			pbViewInfo 			= null;

	/** The button to cancel this dialog.*/
	private UIButton			pbCancel			= null;

	/** The button to open the relevant help.*/
	private UIButton			pbHelp				= null;

	/** The Vector holds the View objects used as the data for the list.*/
	private Vector				oViews 				= new Vector();

	/** The associated node that we are displayed the views deleted from for.*/
	private NodeSummary			oNode 				= null;

	/** A list of all user's home view so they can be eliminated from the list.*/
	private Hashtable			oHomeViews 			= null;

	/** The UITrashViewDialog if that was the dialog that launched this dialog.*/
	private UITrashViewDialog	oBin				= null;

	/** The layout mananger used for the contents of this dialog.*/
	private GridBagLayout 		gb	 				= null;

	/** The panel holdsing the view list and inner buttons.*/
	private JPanel				centerpanel			= null;

	/** The main panel holding the center panel and cancel button.*/
	private JPanel				mainpanel			= null;

	/** The title of this dialog.*/
	private String				sTitle				= LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIDeletedViewDialog.removedFromTitle")+": "; //$NON-NLS-1$


 	/**
	 * Initializes and sets up the dialog.
	 *
	 * @param parent, the parent frame for this dialog.
	 * @param node com.compendium.core.datamodel.NodeSummary, the associated node.
	 * @param bin com.compendium.ui.dialogs.UITrashViewDialog, the dialog which opened this dialog.
	 */
	public UIDeletedViewDialog(JFrame parent, NodeSummary node, UITrashViewDialog bin) {

	  	super(parent, true);
		oNode = node;
		oBin = bin;
		initDialog(node);
	}

 	/**
	 * Initializes and sets up the dialog
	 *
	 * @param parent, the parent frame for this dialog.
	 * @param node com.compendium.core.datamodel.NodeSummary, the associated node.
	 */
	public UIDeletedViewDialog(JDialog parent, NodeSummary node) {

	  	super(parent, true);
		oNode = node;
		initDialog(node);
	}

 	/**
	 * Initializes the dialog
	 *
	 * @param node com.compendium.core.datamodel.NodeSummary, the associated node.
	 */
	private void initDialog(NodeSummary node) {

		setTitle(sTitle+node.getLabel());

		oContentPane = getContentPane();
		oContentPane.setLayout(new BorderLayout());

		init(node);
		oContentPane.add(mainpanel, BorderLayout.CENTER);

		pack();
		setResizable(false);
	}

 	/**
	 * Draw the dialog contents.
	 *
	 * @param node com.compendium.core.datamodel.NodeSummary, the associated node.
	 */
	public void init(NodeSummary node) {

		mainpanel = new JPanel(new BorderLayout());

		centerpanel = new JPanel();
		centerpanel.setBorder(new EmptyBorder(10,10,10,10));

		gb = new GridBagLayout();
		centerpanel.setLayout(gb);
		GridBagConstraints gc = new GridBagConstraints();
		//gc.fill = GridBagConstraints.BOTH;
		gc.anchor = GridBagConstraints.WEST;
		gc.insets = new Insets(5,5,5,5);
		gc.gridx = 0;

		// Add label
		JLabel lblView = new JLabel(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIDeletedViewDialog.deleteFrom")+":"); //$NON-NLS-1$
		gc.gridy = 0;
		gc.gridwidth=3;
		gc.weightx=1;
		gb.setConstraints(lblView, gc);
		centerpanel.add(lblView);

		// Create the list
		lstViews = new UINavList(new DefaultListModel());
		lstViews.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
	  			if(e.getClickCount() == 2)
		  			onRestore();
  			}
		});

		lstViews.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		lstViews.setCellRenderer(new ViewListCellRenderer());
		lstViews.setBackground(Color.white);
		JScrollPane sp = new JScrollPane(lstViews);
		sp.setPreferredSize(new Dimension(260,180));

		gc.gridy = 1;
		gc.fill = GridBagConstraints.BOTH;
		gb.setConstraints(sp, gc);
		centerpanel.add(sp);

		lblViews = new JLabel(""); //$NON-NLS-1$
		gc.gridy = 2;
		gc.fill = GridBagConstraints.NONE;
		gb.setConstraints(lblViews, gc);
		centerpanel.add(lblViews);

		// Add import button
		pbRestore= new UIButton(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIDeletedViewDialog.restoreButton")); //$NON-NLS-1$
		pbRestore.setMnemonic(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIDeletedViewDialog.restoreButtonMnemonic").charAt(0));
		pbRestore.addActionListener(this);
		gc.gridy = 3;
		gc.gridwidth=1;
		gc.weightx=0;
		gc.anchor = GridBagConstraints.NORTHWEST;
		gb.setConstraints(pbRestore, gc);
		centerpanel.add(pbRestore);

		// Add select all button
		pbSelectAll = new UIButton(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIDeletedViewDialog.selectAllButton")); //$NON-NLS-1$
		pbSelectAll.setMnemonic(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIDeletedViewDialog.selectAllButtonMnemonic").charAt(0));
		pbSelectAll.addActionListener(this);
		gc.gridy = 3;
		gc.gridx = 1;
		gb.setConstraints(pbSelectAll, gc);
		centerpanel.add(pbSelectAll);

		// Add information button
		/*
		pbRestoreInfo = new UIButton("Linking Info");
		pbViewInfo.addActionListener(this);
		gc.gridy = 3;
		gc.gridx = 2;
		gc.weightx=1;
		gc.weighty=1;
		gb.setConstraints(pbViewInfo, gc);
		centerpanel.add(pbViewInfo);
		*/

		// other initializations
		updateListView(node);

		// BUTTON PANEL
		UIButtonPanel oButtonPanel = new UIButtonPanel();

		pbCancel = new UIButton(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIDeletedViewDialog.closeButton")); //$NON-NLS-1$
		pbCancel.setMnemonic(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIDeletedViewDialog.closeButtonMnemonic").charAt(0));
		pbCancel.addActionListener(this);
		getRootPane().setDefaultButton(pbCancel);
		oButtonPanel.addButton(pbCancel);

		pbHelp = new UIButton(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIDeletedViewDialog.helpButton")); //$NON-NLS-1$
		pbHelp.setMnemonic(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIDeletedViewDialog.helpButtonMnemonic").charAt(0));
		ProjectCompendium.APP.mainHB.enableHelpOnButton(pbHelp, "node.views-deletedviews", ProjectCompendium.APP.mainHS); //$NON-NLS-1$
		oButtonPanel.addHelpButton(pbHelp);

		mainpanel.add(centerpanel, BorderLayout.CENTER);
		mainpanel.add(oButtonPanel, BorderLayout.SOUTH);
	}


	/******* EVENT HANDLING METHODS *******/

	/**
	 * Handle button push events.
	 * @param event, the associated ActionEvent.
	 */
	public void actionPerformed(ActionEvent event) {
		Object source = event.getSource();
		// Handle button events
		if (source instanceof JButton) {
			if (source == pbRestore) {
				onRestore();
			}
			else if (source == pbSelectAll) {
				onSelectAll();
			}
			else if (source == pbViewInfo) {
				onViewInformation();
			}
			else if (source == pbCancel) {
				onCancel();
			}
		}
	}

	/**
	 * Process a request to restore the associated node to the selected views.
	 */
	public void onRestore() {

		UIViewFrame activeFrame = ProjectCompendium.APP.getCurrentFrame();
		ProjectCompendium.APP.setWaitCursor();
		activeFrame.setCursor(new Cursor(java.awt.Cursor.WAIT_CURSOR));
		this.setCursor(new Cursor(java.awt.Cursor.WAIT_CURSOR));

		int [] selection = lstViews.getSelectedIndices();
		for(int i=0;i<selection.length;i++) {
			View view = (View)oViews.elementAt(selection[i]);

			//not other peoples homeviews.
			if ( !oHomeViews.containsKey(view.getId()) || view.getId().equals(ProjectCompendium.APP.getHomeView().getId()) ) {
				ProjectCompendium.APP.restore(oNode, view);
			}
		}

		// UPDATE TRASHBIN LIST
		if (oBin != null)
			oBin.updateListView();


		// INCASE TRANSLCUSION INDICATORS NEED UPDATING
		ProjectCompendium.APP.refreshIconIndicators();

		this.setCursor(Cursor.getDefaultCursor());
		activeFrame.setCursor(Cursor.getDefaultCursor());
		ProjectCompendium.APP.setDefaultCursor();

		onCancel();
	}

	/**
	 * Updates the list view with the parent views the given node has been deleted from.
	 * This method used in displaying the containing views for the given node.
	 * @param node com.compendium.core.datamodel.NodeSummary, the associated node.
	 */
	public void updateListView(NodeSummary node) {

		Vector views = null;
		removeAllViews();

		//get the node Id
		String nodeId = node.getId();

		//get the session object & views
		IModel model = ProjectCompendium.APP.getModel();
		PCSession session = model.getSession();

		try {
			views = ProjectCompendium.APP.getModel().getNodeService().getDeletedViews(session, nodeId);

			if (views.size() == 0)
				return;

			oHomeViews = ProjectCompendium.APP.getModel().getUserService().getHomeViews(session);

			//sort the vector
			views = CoreUtilities.sortList(views);

			for(Enumeration e = views.elements();e.hasMoreElements();) {

				View view = (View)e.nextElement();
				view.initialize(session, model);

				String viewId = view.getId();

				ImageIcon img = UINodeTypeManager.getNodeImageSmall(view.getType());
				String text = view.getLabel();

				JLabel label = new JLabel(text,img,SwingConstants.LEFT);

				if (oHomeViews.containsKey(viewId)) {

					label.setText( text + " - " + oHomeViews.get(viewId)); //$NON-NLS-1$

					//appear to disable other peoples homeviews.
					if (!viewId.equals(ProjectCompendium.APP.getHomeView().getId())) {
						label.setFont(new Font("Helvetica", Font.ITALIC, 12)); //$NON-NLS-1$
						label.setForeground(Color.gray);
						label.validate();
					}
				}

				label.addMouseListener(new MouseAdapter() {
					public void mouseClicked(MouseEvent e) {
	  					if(e.getClickCount() == 2)
		  					onRestore();
  					}
				});

				label.setToolTipText(text);
				((DefaultListModel)lstViews.getModel()).addElement(label);
				oViews.addElement(view);
			}

			lstViews.setSelectedIndex(0);

			updateViewCount();
		}
		catch(Exception ex) {
			ex.printStackTrace();
			ProjectCompendium.APP.displayError("Exception: (UINodeViewPanel.updateListView) " + ex.getMessage()); //$NON-NLS-1$
		}
	}

	/**
	 * Updates the number of occurences for the given node.
	 */
	public void updateViewCount() {
		lblViews.setText(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIDeletedViewDialog.numOccurences") +":"+ String.valueOf(oViews.size())); //$NON-NLS-1$
	}

	/**
	 * This is a convenience method to delete all the views in the hashtables and vectors.
	 */
	public void removeAllViews() {

		((DefaultListModel)lstViews.getModel()).removeAllElements();
		oViews.removeAllElements();
	}

	/**
	 * Select All the views in the list.
	 */
	private void onSelectAll() {
		int size = oViews.size();
		lstViews.setSelectionInterval(0,size-1);
	}

	/**
	 * Opens up the View Linking information dialog. NOT CURRENTLY USED.
	 */
	private void onViewInformation() {
		int index = lstViews.getSelectedIndex();
		View view = (View)oViews.elementAt(index);
		onLinkingInformation();
	}

	/**
	 * Shows the Additional Linking Information Dialog box. NOT CURRENTLY USED.
	 */
	private void onLinkingInformation() {

		int [] selection = lstViews.getSelectedIndices();
		String total = ""; //$NON-NLS-1$
		String delimiter = "#"; //$NON-NLS-1$
		Vector vtTable = new Vector(51);

		//counter for containing views
		int contViewCount = 0;
		//counter for fromNodes, and toNodes in 'this' containing view
		int fromNodeCount = 0;
		int toNodeCount = 0;

		//sort the vector
		oViews = CoreUtilities.sortList(oViews);

		//get the information for all the selected views
		for(int i=0;i<selection.length;i++)
		{
			contViewCount++;

			View view = (View)oViews.elementAt(selection[i]);

			//not other peoples homeviews.
			if (!oHomeViews.containsKey(view.getId()) ||  view.getId().equals(ProjectCompendium.APP.getHomeView().getId())) {

				UIViewFrame viewFrame = ProjectCompendium.APP.addViewToDesktop(view,view.getLabel());
				Vector history = new Vector();
				history.addElement(new String(sTitle));
				viewFrame.setNavigationHistory(history);

				UIViewPane viewPane = ((UIMapViewFrame)viewFrame).getViewPane();

				total += view.getLabel();

				//get the ui node object corresponding to the viewframe
				UINode uinode = null;
				uinode = (UINode)viewPane.get(oNode.getId());
				//System.out.println(uinode);
				if(uinode == null)
					break;

				//get the uilinks of this UINode
				int count = 0;
				for(Enumeration e = uinode.getLinks();e.hasMoreElements();)
				{
					//generate vector for one row
					Vector vtRow = new Vector(51);

					//add the containing view once to the first column if it has node has more
					//than one row information (just a formatting style)
					count++;
					if(count > 1)
					{
						vtRow.addElement(" "); //$NON-NLS-1$
					}else
					{
						//get the containing view in the first column
						vtRow.addElement(view.getLabel());
					}//end if

					//get the from node into the second column
					UILink uilink = (UILink)e.nextElement();
					String fromNode = uilink.getFromNode().getText();
					//if the node in question is the fromnode then ignore
					if(fromNode.equals(uinode.getText()))
						fromNode = ""; //$NON-NLS-1$
					vtRow.addElement(fromNode);
					//increment the fromNode counter if there was a fromNode!
					if(!fromNode.equals("")) //$NON-NLS-1$
					{
						fromNodeCount++;
					}//end if

					//get the to node into the third column
					String toNode = uilink.getToNode().getText();
					//if the node in question is the tonode then ignore
					if(toNode.equals(uinode.getText()))
						toNode = ""; //$NON-NLS-1$
					vtRow.addElement(toNode);
					//increment the fromNode counter if there was a fromNode!
					if(!toNode.equals("")) //$NON-NLS-1$
					{
						toNodeCount++;
					}//end if

					//the 'total' string is for copying to the clipboard
					total += delimiter + fromNode + delimiter + toNode + delimiter + "\n"; //$NON-NLS-1$

					//add this row vector to the table vector
					vtTable.addElement(vtRow);
				}
			}
			else {
				// IF THE ONLY SELECTED VIEW IS A HOMEVIEW OF SOMEONE ELSE THEN SHOW NOTHING
				if (selection.length == 1)
					return;
			}
		}

		Vector vtColNames = new Vector(51);
		String viewName = LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIDeletedViewDialog.ContainingViewColumn") + " (" + String.valueOf(contViewCount) + ")"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		String fromNode = LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIDeletedViewDialog.fromNodeColumn") + " (" + String.valueOf(fromNodeCount) + ")"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		String toNode = LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIDeletedViewDialog.toNodeColumn") + " (" + String.valueOf(toNodeCount) + ")"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		vtColNames.addElement(viewName);
		vtColNames.addElement(fromNode);
		vtColNames.addElement(toNode);

		UILinkingInfoDialog message = new UILinkingInfoDialog(ProjectCompendium.APP,total);
		message.setTitle(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIDeletedViewDialog.linkingInfoTitle") + oNode.getLabel()); //$NON-NLS-1$
		message.addTable(vtTable,vtColNames);
		message.setVisible(true);
	}

	/**
	 * Helper class to render the element in the views list.
	 */
	public class ViewListCellRenderer extends JLabel implements ListCellRenderer {

	  	protected Border noFocusBorder;

		/*
		 * Constructors
		 */
		public ViewListCellRenderer() {
			super();
			noFocusBorder = new EmptyBorder(1, 1, 1, 1);
			setOpaque(true);
			setBorder(noFocusBorder);
	  	}

		public Component getListCellRendererComponent(JList list,
													Object value,            // value to display
													int index,               // cell index
													boolean isSelected,      // is the cell selected
													boolean cellHasFocus ) { // the list and the cell have the focus

			JLabel lbl = (JLabel)value;

			if (isSelected) {
				setBackground(list.getSelectionBackground());
				setForeground(list.getSelectionForeground());
			}
			else {
				setBackground(list.getBackground());
				setForeground(lbl.getForeground());
			}

			setText(lbl.getText());
			setFont(lbl.getFont());

			setBorder((cellHasFocus) ? UIManager.getBorder("List.focusCellHighlightBorder") : noFocusBorder); //$NON-NLS-1$

			return this;
		}
	}
}
