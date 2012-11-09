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
import java.io.*;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.*;

import com.compendium.core.*;
import com.compendium.core.ICoreConstants;
import com.compendium.core.datamodel.*;

import com.compendium.ProjectCompendium;
import com.compendium.ui.plaf.*;
import com.compendium.ui.*;

/**
 * Shows a list of all views in the database.
 *
 * @author	Mohammed Sajid Ali / Michelle Bachler
 */
public class UISelectViewDialog extends UIDialog implements ActionListener {

	/** The parent frame for this dialog.*/
	private JFrame				oParent 		= null;

	/** The main content pane for this dialog.*/
	private Container			oContentPane 	= null;

	/** The scrollpane for the list of views.*/
	private JScrollPane			sp 				= null;

	/** The list of views.*/
	private UINavList			lstViews 		= null;

	/** The button to open the selected views.*/
	private UIButton			pbView 			= null;

	/** The button to close the dialog.*/
	private UIButton			pbClose 		= null;

	/** The button to select all the views in the dialog.*/
	private UIButton			pbSelectAll 	= null;

	/** The button to open the relevant help.*/
	private UIButton			pbHelp	 	= null;

	/** The button to insert the selected views into the current view.*/
	private JButton				pbInsert 		= null;

	/** The data for the list of Views.*/
	private Vector				vtViews 		= new Vector();

	/** The title for this dialog.*/
	private String				sTitle			= "All Views";

	/** Lists all the user home and inbox views for filtering.*/
	private Hashtable				htUserViews		= null;


 	/**
	 * Initializes and sets up the dialog.
	 * @param parent, the parent frame for this dialog.
	 */
	public UISelectViewDialog(JFrame parent) {

	  	super(parent, true);
		oParent = parent;

		setTitle(sTitle);
		drawDialog();
	}

	/**
	 * Draw the contents of the dialog.
	 */
	private void drawDialog() {

		oContentPane = getContentPane();
		oContentPane.setLayout(new BorderLayout());

		JPanel listpanel = new JPanel(new BorderLayout());
		listpanel.setBorder(new EmptyBorder(10,15,15,10));

		JLabel lblViews = new JLabel("Select view(s) to open:");
		listpanel.add(lblViews, BorderLayout.NORTH);

		// Create the list
		lstViews = new UINavList(new DefaultListModel());
		lstViews.addMouseListener(new MouseAdapter() {
  			public void mouseClicked(MouseEvent e) {
	  			if(e.getClickCount() == 2)
		 			openContents();
  			}
		});

		lstViews.addKeyListener(new KeyAdapter() {
  			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER)
					openContents();
  			}
		});

		lstViews.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		lstViews.setCellRenderer(new ViewListCellRenderer());
		lstViews.setBackground(Color.white);

		// create a scroll viewer to add scroll functionality in the list view
		JScrollPane sp = new JScrollPane(lstViews);
		sp.setPreferredSize(new Dimension(350,196));
		listpanel.add(sp, BorderLayout.CENTER);

		oContentPane.add(listpanel, BorderLayout.CENTER);
		oContentPane.add(createSideButtonPanel(), BorderLayout.EAST);
		oContentPane.add(createButtonPanel(), BorderLayout.SOUTH);

		updateListView();

		pack();
	}

	/**
	 * Draw the panel of buttons for this dialog.
	 */
	private JPanel createSideButtonPanel() {

		GridBagLayout gb = new GridBagLayout();
		GridBagConstraints gc = new GridBagConstraints();

		gc = new GridBagConstraints();
		gc.insets = new Insets(5,5,5,5);
		gc.fill=GridBagConstraints.HORIZONTAL;
		gc.anchor = GridBagConstraints.CENTER;
		gc.gridwidth = GridBagConstraints.REMAINDER;
		gc.weightx=1;
		gc.weighty=1;

		JPanel buttonpanel = new JPanel();
		buttonpanel.setLayout(gb);
		buttonpanel.setBorder(new EmptyBorder(15,5,15,10));

		pbSelectAll = new UIButton("Select All");
		pbSelectAll.setMnemonic(KeyEvent.VK_S);
		pbSelectAll.addActionListener(this);
		gb.setConstraints(pbSelectAll, gc);
		buttonpanel.add(pbSelectAll);

		pbInsert = new UIButton("Insert into View");
		pbInsert.setMnemonic(KeyEvent.VK_I);
		pbInsert.addActionListener(this);
		gb.setConstraints(pbInsert, gc);
		buttonpanel.add(pbInsert);

		pbView = new UIButton("Open");
		pbView.setMnemonic(KeyEvent.VK_O);
		pbView.addActionListener(this);
		gb.setConstraints(pbView, gc);
		buttonpanel.add(pbView);

		return buttonpanel;
	}

	/**
	 * Create the panel of buttons.
	 */
	private UIButtonPanel createButtonPanel() {

		UIButtonPanel oButtonPanel = new UIButtonPanel();

		pbClose = new UIButton("Close");
		pbClose.setMnemonic(KeyEvent.VK_C);
		pbClose.addActionListener(this);
		getRootPane().setDefaultButton(pbClose); // If this changes, change onEnter method too.
		oButtonPanel.addButton(pbClose);

		pbHelp = new UIButton("Help");
		ProjectCompendium.APP.mainHB.enableHelpOnButton(pbHelp, "node.views", ProjectCompendium.APP.mainHS);
		pbHelp.setMnemonic(KeyEvent.VK_H);
		oButtonPanel.addHelpButton(pbHelp);

		return oButtonPanel;
	}

	/**
	 * Handle button push events.
	 * @param event, the associated ActionEvent object.
	 */
	public void actionPerformed(ActionEvent event) {

		Object source = event.getSource();
		// Handle button events
		if (source instanceof JButton) {
			if (source == pbView) {
				onView();
			}
			else if (source == pbClose) {
				onCancel();
			}
			else if (source == pbSelectAll) {
				onSelectAll();
			}
			else if (source == pbInsert) {
				try {
					onInsert();
				}
				catch(Exception ex) {
					System.out.println("Error: (UISelectViewDialog.onInsert) \n\n"+ex.getMessage());
				}
				onCancel();
			}
		}
	}

	/**
	 * Updates the list view with the available views from the database.
	 */
	private void updateListView() {

		((DefaultListModel)lstViews.getModel()).removeAllElements();
		vtViews.removeAllElements();
		Vector vtSort = new Vector(51);

		try {
			Enumeration views = ProjectCompendium.APP.getModel().getNodeService().getAllActiveViews(ProjectCompendium.APP.getModel().getSession());

			htUserViews = ProjectCompendium.APP.getModel().getUserViews();
			
			for(Enumeration e = views;e.hasMoreElements();) {
				View view = (View)e.nextElement();
				vtSort.addElement(view);
			}

			//sort the vector
			vtSort = CoreUtilities.sortList(vtSort);
			String id = "";
			for(Enumeration e = vtSort.elements();e.hasMoreElements();) {

				View view = (View)e.nextElement();
				id = view.getId();
				view.initialize(ProjectCompendium.APP.getModel().getSession(), ProjectCompendium.APP.getModel());
				
				ImageIcon img = null;
				if (view.getType() == ICoreConstants.LISTVIEW)
					img = UIImages.getNodeIcon(IUIConstants.LIST_SM_ICON);
				else
					img = UIImages.getNodeIcon(IUIConstants.MAP_SM_ICON);

				//trim text to fit the label for the timebeing since the label comes out of the scrollbar window
				String text = view.getLabel();

				if (text.equals("")) {
					text = "-- Unlabelled View --";
				}

				JLabel label = new JLabel(img, SwingConstants.LEFT);
				
				if (htUserViews.containsKey(id)) {					
					label.setText( text + " - " + ((String)htUserViews.get(id)) );
					label.setFont(new Font("Helvetica", Font.ITALIC, 12));
					label.setForeground(Color.gray);
					label.validate();					
				} else {
					label.setFont(new Font("Helvetica", Font.PLAIN, 12));					
					label.setText(text);
				}
								
				label.setToolTipText(text);

				((DefaultListModel)lstViews.getModel()).addElement(label);

				vtViews.addElement(view);
			}

			lstViews.setSelectedIndex(0);
		}
		catch(Exception io) {
			ProjectCompendium.APP.displayError("Exception: (UISelectViewDialog.updateListView) \n");//+io.getMessage());
		}
	}

	/**
	 * Open the contents popup for the currently selected node.
	 */
	public void openContents() {

		int selection = lstViews.getSelectedIndex();
		NodeSummary node = (NodeSummary)vtViews.elementAt(selection);
		String sNodeID = node.getId();
		
		if (!htUserViews.containsKey(sNodeID)) { 
			UINodeContentDialog contentDialog = new UINodeContentDialog(this, node, UINodeContentDialog.CONTENTS_TAB);
			UIUtilities.centerComponent(contentDialog, ProjectCompendium.APP);
			contentDialog.setVisible(true);
		}
	}
	
	/**
	 * Select All the views in the list.
	 */
	private void onSelectAll() {
		int size = vtViews.size();
		lstViews.setSelectionInterval(0,size-1);
	}

	/**
	 * Insert the selected views in the active window .
	 */
	private void onInsert() throws Exception {

		UIViewFrame activeFrame = ProjectCompendium.APP.getCurrentFrame();
		ProjectCompendium.APP.setWaitCursor();
		activeFrame.setCursor(new Cursor(java.awt.Cursor.WAIT_CURSOR));
		this.setCursor(new Cursor(java.awt.Cursor.WAIT_CURSOR));

		int i=0;

		IModel model = ProjectCompendium.APP.getModel();
		PCSession session = model.getSession();

		View view = activeFrame.getView();

		if (activeFrame.getView().getType() == ICoreConstants.LISTVIEW) {

			UIList list = ((UIListViewFrame)activeFrame).getUIList();

			// deselect nodes and links so pasted ones are only ones seleted - bz
			list.deselectAll();
			int [] selection = lstViews.getSelectedIndices();
			NodePosition[] nps = new NodePosition[selection.length];
			View listview = list.getView();			
			int nodeCount = listview.getNumberOfNodes();;

			for(i=0;i<selection.length;i++) {

				NodeSummary node = (NodeSummary)vtViews.elementAt(selection[i]);
				String sNodeID = node.getId();
				if (!htUserViews.containsKey(sNodeID)) { 
					node.initialize(session,model);

					// CHECK IF NODE WAS DELETED
					boolean deleted = model.getNodeService().isMarkedForDeletion(session, sNodeID);
					int index = list.getIndexOf(node);
					if(index == -1) {
						// IF NODE WAS DELETED, RESTORE IT
						if (deleted) {
							ProjectCompendium.APP.restoreNode(node, view);
						}
						else {
							int xpos = 100;
							int ypos = ( nodeCount + 1) * 10;
							nodeCount++;
							Date date = new Date();
							NodePosition np = new NodePosition(listview, node, xpos, ypos, date, date);
							nps[i]=np;
								
							try {
								listview.addNodeToView(node, xpos, ypos);
							}
							catch (Exception e) {
								e.printStackTrace();
								ProjectCompendium.APP.displayError("Exception: (UISearchResultsDialog.onInsert) \n" + e.getMessage());
								System.out.flush();
							}
							
							/*Date date = new Date();
							NodePosition np = new NodePosition(list.getView(), node, xpos, ypos, date, date);
	
							try {
								list.getView().addNodeToView(np.getNode(), np.getXPos(), (list.getNumberOfNodes() + 1) * 10);
							   	list.insertNode(np, list.getNumberOfNodes());
							}
							catch (Exception e) {
								ProjectCompendium.APP.displayError("Exception: (UISelectViewDialog.onInsert) \n" + e.getMessage());
							}*/
						}
					}
					else {
						//select node that was found in view
						list.selectNode(index, ICoreConstants.MULTISELECT);
					}
				}
			}
		   	list.insertNodes(nps, listview.getNumberOfNodes());				
		}
		else {
			UIViewPane uiviewpane = ((UIMapViewFrame)activeFrame).getViewPane();
			ViewPaneUI viewpaneui = uiviewpane.getViewPaneUI();

			// deselect nodes and links so pasted ones are only ones seleted - bz
			uiviewpane.setSelectedNode(null, ICoreConstants.DESELECTALL);
			uiviewpane.setSelectedLink(null, ICoreConstants.DESELECTALL);

			int [] selection = lstViews.getSelectedIndices();

			for(i=0;i<selection.length;i++) {

				NodeSummary node = (NodeSummary)vtViews.elementAt(selection[i]);
				String sNodeID = node.getId();
				if (!htUserViews.containsKey(sNodeID)) {				
					node.initialize(session,model);

					// CHECK IF NODE WAS DELETED
					boolean deleted = model.getNodeService().isMarkedForDeletion(session, sNodeID);
	
					UINode uiNodeInView = null;
					if (!deleted) {
						//add the node to the view if it isn't already in there
						uiNodeInView = (UINode)uiviewpane.get(sNodeID);
					}
	
					if(uiNodeInView == null) {
						// GET CURRENT SCROLL POSITION AND ADD THIS TO POSITIONING INFO
						int hPos = activeFrame.getHorizontalScrollBarPosition();
						int vPos = activeFrame.getVerticalScrollBarPosition();
	
						int xpos = hPos + ViewPaneUI.LEFTOFFSET;
						int ypos = vPos + ((i+1)*ViewPaneUI.INTERNODE_DISTANCE);
	
						// IF NODE WAS DELETED, RESTORE IT
						if (deleted) {
							ProjectCompendium.APP.restoreNode(node, view);
						}
						else {
							UINode newnode = viewpaneui.addNodeToView(node,xpos,ypos);
							newnode.setSelected(true);
							uiviewpane.setSelectedNode(newnode,ICoreConstants.MULTISELECT);
						}
					}
					else {
						//select node that was found in view
						uiNodeInView.setSelected(true);
						uiviewpane.setSelectedNode(uiNodeInView,ICoreConstants.MULTISELECT);
					}
				}
			}
		}

		this.setCursor(Cursor.getDefaultCursor());
		activeFrame.setCursor(Cursor.getDefaultCursor());
		ProjectCompendium.APP.setDefaultCursor();

		onCancel();
	}


	/**
	 * Open the select views.
	 */
	public void onView() {

		int [] selection = lstViews.getSelectedIndices();
		String sViewID = "";
		for(int i=0;i<selection.length;i++) {
			View view = (View)vtViews.elementAt(selection[i]);
			sViewID = view.getId();
			if (!htUserViews.containsKey(sViewID)) { 			
				UIViewFrame viewFrame = ProjectCompendium.APP.addViewToDesktop(view, view.getLabel());
				Vector history = new Vector();
				history.addElement(new String(sTitle));
				viewFrame.setNavigationHistory(history);
			}
		}

		onCancel();
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
			setIcon(lbl.getIcon());

			setBorder((cellHasFocus) ? UIManager.getBorder("List.focusCellHighlightBorder") : noFocusBorder);

			return this;
		}
	}	
}
