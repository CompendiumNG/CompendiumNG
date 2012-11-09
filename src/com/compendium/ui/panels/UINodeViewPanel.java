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

package com.compendium.ui.panels;

import java.util.*;
import java.io.*;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.*;

import com.compendium.core.*;
import com.compendium.core.datamodel.*;

import com.compendium.LanguageProperties;
import com.compendium.ProjectCompendium;
import com.compendium.ui.plaf.*;
import com.compendium.ui.*;
import com.compendium.ui.dialogs.*;

/**
 * Displays a list of all the parent views for a given node and processes related options.
 *
 * @author	Beatrix Zimmermann / Michelle Bachler
 */
public class UINodeViewPanel extends JPanel implements ActionListener, IUIConstants {

	/** The parent frame for the dialog this panel is in.*/
	private JFrame				oParent 			= null;

	/** The scrollpane for the list of views.*/
	private JScrollPane			sp 					= null;

	/** The list of parent views.*/
	private UINavList			lstViews 			= null;

	/** The label for the list of views.*/
	private JLabel 				lblViews2			= null;

	/** The button to open the selected parent views.*/
	private UIButton			pbView				= null;

	/** The button to select all the views in the list.*/
	private UIButton			pbSelectAll 		= null;

	/** The button to view linking info for the selected view and current node.*/
	private UIButton			pbViewInfo 			= null;

	/** The button to view parent view that the current node has been deleted from.*/
	private UIButton			pbDeletedViews		= null;

	/** The button to cancel this view list.*/
	private UIButton			pbCancel			= null;

	/** The button to open the relevant help.*/
	private UIButton			pbHelp				= null;

	/** The data for the list of views (conatins View objects).*/
	private Vector				oViews 				= new Vector();

	/** The node that this panel is listing the parent views for.*/
	private NodeSummary			oNode 				= null;

	/** A list of all the home views mapped to thier id, so they are not diaplyed in the views list.*/
	private Hashtable			htUserViews 			= null;

	/** The tabbed pane this view is in.*/
	private UINodeContentDialog oParentDialog		= null;

	/** The parent dialog this panel is in.. sometimes.*/
	private JDialog				oDialog				= null;

	/** The layou manage used to layout the contents of this panel*/
	private GridBagLayout 		gb	 				= null;

	/** the main panel the contents are in in this panel.*/
	private JPanel				centerpanel			= null;

	/** The title for this panel.*/
	private String				sTitle				= LanguageProperties.getString(LanguageProperties.PANELS_BUNDLE, "UINodeViewPanel.title"); //$NON-NLS-1$

	/**
	 * Constrcutor.
	 * @param parent, the parent frame for the parent dialog of this panel.
	 * @param node com.compendium.core.datamodel.NodeSummary, the node to display the containing views for.
	 * @param tabbedPane com.compendium.ui.dialogs.UIContentDailog, the parent dialog this panel is in.
	 */
	public UINodeViewPanel(JFrame parent, NodeSummary node, UINodeContentDialog tabbedPane) {
		super();

		oParent = parent;
		oNode = node;
		oParentDialog = tabbedPane;

		init(node);
	}

	/**
	 * Constrcutor.
	 * @param parent, the parent frame for the parent dialog of this panel.
	 * @param node com.compendium.core.ui.UINode, the node to display the containing views for when in a map.
	 * @param tabbedPane com.compendium.ui.dialogs.UIContentDailog, the parent dialog this panel is in.
	 */
	public UINodeViewPanel(JFrame parent, UINode uinode, UINodeContentDialog tabbedPane) {
		super();

		oParent = parent;
		oNode = uinode.getNode();
		oParentDialog = tabbedPane;

		init(oNode);
	}

	/**
	 * Constrcutor. Ude by the Tags Panel which displays node's views with tags assigned.
	 * @param parent, the parent frame for the parent dialog of this panel.
	 * @param node com.compendium.core.datamodel.NodeSummary, the node to display the containing views for.
	 * @param dialog, the parent dialog this panel is in.
	 */
	public UINodeViewPanel(JFrame parent, NodeSummary node, JDialog dialog) {
		super();

		oParent = parent;
		oNode = node;
		oDialog = dialog;

		init(node);
	}

	/**
	 * Initialize and draw this panels contents for the given node.
	 * @param node com.compendium.core.ui.UINode, the node to display the containing views for when in a map.
	 */
	public void init(NodeSummary node) {

		setLayout(new BorderLayout());

		centerpanel = new JPanel();
		centerpanel.setBorder(new EmptyBorder(10,10,10,10));

		gb = new GridBagLayout();
		centerpanel.setLayout(gb);
		GridBagConstraints gc = new GridBagConstraints();
		gc.anchor = GridBagConstraints.WEST;
		gc.insets = new Insets(5,5,5,5);
		gc.gridx = 0;

		// Add label
		JLabel lblViews = new JLabel(LanguageProperties.getString(LanguageProperties.PANELS_BUNDLE, "UINodeViewPanel.selectViews")+":"); //$NON-NLS-1$
		gc.gridy = 0;
		gc.gridwidth=3;
		gc.weightx=1;
		gb.setConstraints(lblViews, gc);
		centerpanel.add(lblViews);

		// Create the list
		lstViews = new UINavList(new DefaultListModel());
		lstViews.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
	  			if(e.getClickCount() == 2)
		  			onView();
  			}
		});

		lstViews.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		lstViews.setCellRenderer(new ViewListCellRenderer());
		lstViews.setBackground(Color.white);
		JScrollPane sp = new JScrollPane(lstViews);
		sp.setPreferredSize(new Dimension(250, 200));
		gc.gridy = 1;
		gc.fill = GridBagConstraints.BOTH;
		gb.setConstraints(sp, gc);
		centerpanel.add(sp);

		lblViews2 = new JLabel(""); //$NON-NLS-1$
		gc.gridy = 2;
		gc.fill = GridBagConstraints.NONE;
		gb.setConstraints(lblViews2, gc);
		centerpanel.add(lblViews2);

		// Add import button
		pbView = new UIButton(LanguageProperties.getString(LanguageProperties.PANELS_BUNDLE, "UINodeViewPanel.openButton")); //$NON-NLS-1$
		pbView.setMnemonic(LanguageProperties.getString(LanguageProperties.PANELS_BUNDLE, "UINodeViewPanel.openButtonMnemonic").charAt(0)); //$NON-NLS-1$
		pbView.addActionListener(this);

		gc.gridy = 3;
		gc.gridwidth=1;
		gc.weightx=0;
		gc.anchor = GridBagConstraints.NORTHWEST;
		gb.setConstraints(pbView, gc);
		centerpanel.add(pbView);

		// Add select all button
		pbSelectAll = new UIButton(LanguageProperties.getString(LanguageProperties.PANELS_BUNDLE, "UINodeViewPanel.selectAllButton")); //$NON-NLS-1$
		pbSelectAll.setMnemonic(LanguageProperties.getString(LanguageProperties.PANELS_BUNDLE, "UINodeViewPanel.selectAllButtonMnemonic").charAt(0)); //$NON-NLS-1$
		pbSelectAll.addActionListener(this);
		gc.gridy = 3;
		gc.gridx = 1;
		gb.setConstraints(pbSelectAll, gc);
		centerpanel.add(pbSelectAll);

		// Add information button
		pbViewInfo = new UIButton(LanguageProperties.getString(LanguageProperties.PANELS_BUNDLE, "UINodeViewPanel.linkingInfoButton")); //$NON-NLS-1$
		pbViewInfo.setMnemonic(LanguageProperties.getString(LanguageProperties.PANELS_BUNDLE, "UINodeViewPanel.linkingInfoButtonMnemonic").charAt(0)); //$NON-NLS-1$
		pbViewInfo.addActionListener(this);
		gc.gridy = 3;
		gc.gridx = 2;
		gc.weightx=1;
		gc.weighty=1;
		gb.setConstraints(pbViewInfo, gc);
		centerpanel.add(pbViewInfo);

		// DeletedViews button
		int count = 0;
		try {
			count = ProjectCompendium.APP.getModel().getNodeService().getDeletedViewCount(ProjectCompendium.APP.getModel().getSession(), node.getId());
		}catch(Exception ex){}

		pbDeletedViews = new UIButton(LanguageProperties.getString(LanguageProperties.PANELS_BUNDLE, "UINodeViewPanel.viewDeletedButton")+" ("+count+")"); //$NON-NLS-1$ //$NON-NLS-2$
		pbDeletedViews.setMnemonic(LanguageProperties.getString(LanguageProperties.PANELS_BUNDLE, "UINodeViewPanel.viewDeletedButtonMnemonic").charAt(0)); //$NON-NLS-1$ //$NON-NLS-2$
		pbDeletedViews.addActionListener(this);
		gc.gridy = 5;
		gc.gridx = 0;
		gc.gridwidth=3;
		gb.setConstraints(pbDeletedViews, gc);
		centerpanel.add(pbDeletedViews);

		// other initializations
		updateListView(node);

		add(centerpanel, BorderLayout.CENTER);
		add(createButtonPanel(), BorderLayout.SOUTH);
	}

	/**
	 * Create and return the button panel.
	 */
	private UIButtonPanel createButtonPanel() {

		UIButtonPanel oButtonPanel = new UIButtonPanel();

		pbCancel = new UIButton(LanguageProperties.getString(LanguageProperties.PANELS_BUNDLE, "UINodeViewPanel.closeButton")); //$NON-NLS-1$
		pbCancel.setMnemonic(LanguageProperties.getString(LanguageProperties.PANELS_BUNDLE, "UINodeViewPanel.closeButtonMnemonic").charAt(0));
		pbCancel.addActionListener(this);
		oButtonPanel.addButton(pbCancel);

		pbHelp = new UIButton(LanguageProperties.getString(LanguageProperties.PANELS_BUNDLE, "UINodeViewPanel.helpButton")); //$NON-NLS-1$
		pbHelp.setMnemonic(LanguageProperties.getString(LanguageProperties.PANELS_BUNDLE, "UINodeViewPanel.helpButtonMnemonic").charAt(0));
		ProjectCompendium.APP.mainHB.enableHelpOnButton(pbHelp, "node.views-view", ProjectCompendium.APP.mainHS); //$NON-NLS-1$
		oButtonPanel.addHelpButton(pbHelp);

		return oButtonPanel;
	}

	/**
	 * Set the default button for the parent dialog to be this panel's default button.
	 */
	public void setDefaultButton() {
		//oParentDialog.getRootPane().setDefaultButton(pbCancel);
	}

	/**
	 * Updates the list view with the view vector.
	 * This method used in displaying the containing views for the given node.
	 * @param node com.compendium.core.datamodel.NodeSummary, the node to display the containing views for.
	 */
	public void updateListView(NodeSummary node) {

		Vector views = null;
		//clean all the hashtables .. since this dialog was uesd primarily to get all views in the model
		removeAllViews();

		//get the node Id
		String nodeId = node.getId();

		//get the session object & views
		IModel model = ProjectCompendium.APP.getModel();
		PCSession session = model.getSession();

		try {
			views = ProjectCompendium.APP.getModel().getNodeService().getViews(session, nodeId);
			if (views.size() == 0)
				return;

			htUserViews = ProjectCompendium.APP.getModel().getUserViews();
			
			//htUserViews = ProjectCompendium.APP.getModel().getUserService().getHomeViews(session);

			//sort the vector
			views = CoreUtilities.sortList(views);

			for(Enumeration e = views.elements();e.hasMoreElements();) {

				View view = (View)e.nextElement();
				if (view == null)
					continue;

				view.initialize(session, model);

				String viewId = view.getId();
				ImageIcon img = UINodeTypeManager.getNodeImageSmall(view.getType());

				String text = view.getLabel();
				if (text.equals("")) { //$NON-NLS-1$
					text = LanguageProperties.getString(LanguageProperties.PANELS_BUNDLE, "UINodeViewPanel.unlabelledView"); //$NON-NLS-1$
				}

				JLabel label = new JLabel(text,img,SwingConstants.LEFT);
				label.setFont(new Font("Helvetica", Font.PLAIN, 12));					 //$NON-NLS-1$

				if (htUserViews.containsKey(viewId)) {

					label.setText( text + " - " + htUserViews.get(viewId)); //$NON-NLS-1$

					//appear to disable other peoples homeviews and inboxes.
					if (!viewId.equals(ProjectCompendium.APP.getInBoxID())) {						
						label.setFont(new Font("Helvetica", Font.ITALIC, 12)); //$NON-NLS-1$
						label.setForeground(Color.gray);
						label.validate();
					} else {
						label.addMouseListener(new MouseAdapter() {
							public void mouseClicked(MouseEvent e) {
								if(e.getClickCount() == 2)
									onView();
							}
						});
					}
				} else {
					label.addMouseListener(new MouseAdapter() {
						public void mouseClicked(MouseEvent e) {
							if(e.getClickCount() == 2)
								onView();
						}
					});
				}

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
		lblViews2.setText(LanguageProperties.getString(LanguageProperties.PANELS_BUNDLE, "UINodeViewPanel.numberOfOccurences") + String.valueOf(oViews.size())); //$NON-NLS-1$
	}

	/**
	 * This is a convenience method to delete all the views in the hashtables and vectors.
	 */
	public void removeAllViews() {

		((DefaultListModel)lstViews.getModel()).removeAllElements();
		oViews.removeAllElements();
	}
	
	/**
	 * Handle button push events.
 	 * @param event, the associated ActionEvent.
	 */
	public void actionPerformed(ActionEvent event) {

		Object source = event.getSource();
		// Handle button events
		if (source instanceof JButton) {
			if (source == pbView) {
				onView();
			}
			else if (source == pbSelectAll) {
				onSelectAll();
			}
			else if (source == pbViewInfo) {
				onViewInformation();
			}
			else if (source == pbDeletedViews) {
				UIDeletedViewDialog dialog = null;
				if (oParentDialog != null)
					dialog = new UIDeletedViewDialog((JDialog)oParentDialog, oNode);
				else
					dialog = new UIDeletedViewDialog((JDialog)oDialog, oNode);
				dialog.setVisible(true);
			}
			else if (source == pbCancel) {
				if (oParentDialog != null) {
					oParentDialog.onCancel();
				}
				else {
					oDialog.setVisible(false);
					oDialog.dispose();
				}
			}
		}
	}

	/**
	 * Open the selected parent views and select the current node.
	 */
	public void onView() {

		int [] selection = lstViews.getSelectedIndices();

		for(int i=0;i<selection.length;i++) {
			View view = (View)oViews.elementAt(selection[i]);
			String sViewID = view.getId();
			//not other peoples homeviews or inboxes or your homeview.
			if ( !htUserViews.containsKey(sViewID) || sViewID.equals(ProjectCompendium.APP.getInBoxID())) {
				UIViewFrame viewFrame = ProjectCompendium.APP.addViewToDesktop(view, view.getLabel());
				Vector history = new Vector();
				history.addElement(new String(sTitle));
				viewFrame.setNavigationHistory(history);
				UIUtilities.focusNodeAndScroll(oNode, viewFrame);		
			}
		}
	}

	/**
	 * Select All the views in the list.
	 */
	private void onSelectAll() {
		int size = oViews.size();
		lstViews.setSelectionInterval(0,size-1);
	}

	/**
	 * Display linking information for the node in its selected parent views.
	 */
	private void onViewInformation() {
		int index = lstViews.getSelectedIndex();
		View view = (View)oViews.elementAt(index);
		onLinkingInformation();
	}

	/**
	 * Shows the Additional Linking Information Dialog box.
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
		String sViewID = ""; //$NON-NLS-1$
		//get the information for all the selected views
		for(int i=0;i<selection.length;i++) {

			contViewCount++;

			View view = (View)oViews.elementAt(selection[i]);
			sViewID = view.getId();
			
			if (View.isListType(view.getType())) {
				ProjectCompendium.APP.displayMessage(LanguageProperties.getString(LanguageProperties.PANELS_BUNDLE, "UINodeViewPanel.message1"), LanguageProperties.getString(LanguageProperties.PANELS_BUNDLE, "UINodeViewPanel.message1Title")); //$NON-NLS-1$ //$NON-NLS-2$
				return;
			}
			
			//not other peoples homeviews.
			if (!htUserViews.containsKey(sViewID) ||  
					sViewID.equals(ProjectCompendium.APP.getInBoxID()) ) {

				// GET VIEW WITHOUT OPENING VIEW
				UIViewFrame viewFrame = ProjectCompendium.APP.getViewFrame(view, view.getLabel());
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
				for(Enumeration e = uinode.getLinks();e.hasMoreElements();) {

					//generate vector for one row
					Vector vtRow = new Vector(51);

					//add the containing view once to the first column if it has node has more
					//than one row information (just a formatting style)
					count++;
					if(count > 1) {
						vtRow.addElement(" "); //$NON-NLS-1$
					}
					else {
						//get the containing view in the first column
						vtRow.addElement(view.getLabel());
					}

					//get the from node into the second column
					UILink uilink = (UILink)e.nextElement();
					String fromNode = uilink.getFromNode().getText();
					//if the node in question is the fromnode then ignore
					if(fromNode.equals(uinode.getText()))
						fromNode = ""; //$NON-NLS-1$

					vtRow.addElement(fromNode);
					//increment the fromNode counter if there was a fromNode!
					if(!fromNode.equals("")) { //$NON-NLS-1$
						fromNodeCount++;
					}

					//get the to node into the third column
					String toNode = uilink.getToNode().getText();
					//if the node in question is the tonode then ignore
					if(toNode.equals(uinode.getText()))
						toNode = ""; //$NON-NLS-1$

					vtRow.addElement(toNode);
					//increment the fromNode counter if there was a fromNode!
					if(!toNode.equals("")) { //$NON-NLS-1$
						toNodeCount++;
					}

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
		String viewName = LanguageProperties.getString(LanguageProperties.PANELS_BUNDLE, "UINodeViewPanel.containingView") + " (" + String.valueOf(contViewCount) + ")"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		String fromNode = LanguageProperties.getString(LanguageProperties.PANELS_BUNDLE, "UINodeViewPanel.fromNode") + " (" + String.valueOf(fromNodeCount) + ")"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		String toNode = LanguageProperties.getString(LanguageProperties.PANELS_BUNDLE, "UINodeViewPanel.toNode") + " (" + String.valueOf(toNodeCount) + ")"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		vtColNames.addElement(viewName);
		vtColNames.addElement(fromNode);
		vtColNames.addElement(toNode);

		UILinkingInfoDialog message = new UILinkingInfoDialog(oParent,total);
		message.setTitle(LanguageProperties.getString(LanguageProperties.PANELS_BUNDLE, "UINodeViewPanel.linkingInfoFor") + oNode.getLabel()); //$NON-NLS-1$
		message.addTable(vtTable,vtColNames);
		message.setVisible(true);
	}

	/**
	 * Helper class the render the parent views list.
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
