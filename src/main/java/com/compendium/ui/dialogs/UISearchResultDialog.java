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
import java.sql.SQLException;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.*;

import com.compendium.core.*;
import com.compendium.core.datamodel.*;

import com.compendium.LanguageProperties;
import com.compendium.ProjectCompendium;
import com.compendium.ui.*;
import com.compendium.ui.plaf.*;

/**
 * Displays the results from a search.
 *
 * @author	Mohammed Sajid Ali / Michelle Bachler
 */
public class UISearchResultDialog extends UIDialog implements ActionListener, IUIConstants {

	/** The parent frame for this dialog.*/
	private JFrame					oFrame 			= null;

	/** The scrollpane for the results list.*/
	private JScrollPane				sp 				= null;

	/** The search results list of nodes.*/
	private UINavList				lstNodes 		= null;

	/** The button to close the dialog.*/
	private UIButton				pbClose 		= null;

	/** The button to select all the nodes in the list.*/
	private UIButton				pbSelectAll 	= null;

	/** The button to insert the selected nods into the current view.*/
	private UIButton				pbInsert 		= null;

	/** The button to return to the search dialog.*/
	private UIButton				pbSearch 		= null;

	/** The button to open the relevant help.*/
	private UIButton				pbHelp	 		= null;

	/** 
	 * The button to open the selected node. 
	 * For views open the view, for others open the contents.
	 * */
	private UIButton			pbView 			= null;
	
	/** The list of nodes added to the JList.*/
	private Vector					vtNodes 		= new Vector();

	/** The search reulsts.*/
	private Vector					vtResults 		= new Vector();

	/** A reference to the parent search dialog.*/
	private UISearchDialog			oParent 		= null;

	/** The layout manager used on this dialog.*/
	private GridBagLayout 			gb 				= null;

	/** The constraints used with the layout manager.*/
	private GridBagConstraints 		gc 				= null;
	
	/** Lists all the user home and inbox views for filtering.*/
	private Hashtable				htUserViews		= null;


	/**
	 * Initializes and draws the dialog.
	 * @param frame, the parent frame for this dialog.
	 * @param parent, the search dialog that created the results.
	 * @param results, the search results list.
	 */
	public UISearchResultDialog(JFrame frame, UISearchDialog parent, Vector results) {
		this(frame, parent, results, LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UISearchResultDialog.searchResultsTitle")); //$NON-NLS-1$
	}

	/**
	 * Initializes and draws the dialog.
	 * @param frame, the parent frame for this dialog.
	 * @param parent, the search dialog that created the results.
	 * @param results, the search results list.
	 * @param sTitle, the title for this dialog.
	 */
	public UISearchResultDialog(JFrame frame, UISearchDialog parent, Vector results, String sTitle) {

	 	super(frame, true);
		oParent = parent;
		oFrame = frame;
		vtResults = results;
		setTitle(sTitle);

		drawDialog();
	}

	/**
	 * Initializes and sets up the dialog for LimboNodes dialog only.
	 * @param frame, the parent frame for this dialog.
	 * @param results, the search results list.
	 * @param sTitle, the title for this dialog.
	 */
	public UISearchResultDialog(JFrame frame, Vector results, String sTitle) {

	 	super(frame, true);
		oFrame = frame;
		vtResults = results;
		setTitle(sTitle);

		drawDialog();
	}

	/**
	 * Draw the contents of the dialog.
	 */
	private void drawDialog() {

		Container oContentPane = getContentPane();
		oContentPane.setLayout(new BorderLayout());

		// LIST PANEL
		JPanel listpanel = new JPanel(new BorderLayout());
		listpanel.setBorder(new EmptyBorder(10,15,15,10));

		JLabel lblViews = new JLabel();
		listpanel.add(lblViews, BorderLayout.NORTH);

		// Create the list
		lstNodes = new UINavList(new DefaultListModel());

		lstNodes.addMouseListener(new MouseAdapter() {
  			public void mouseClicked(MouseEvent e) {
			  	if(e.getClickCount() == 2) {
					openContents();
				}
  			}
		});

		lstNodes.addKeyListener(new KeyAdapter() {
  			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER)
					openContents();
  			}
		});

		lstNodes.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		lstNodes.setCellRenderer(new ThisListCellRenderer());
		lstNodes.setBackground(Color.white);

		// create a scroll viewer to add scroll functionality in the list view
		JScrollPane sp = new JScrollPane(lstNodes);
		listpanel.add(sp, BorderLayout.CENTER);
		sp.setPreferredSize(new Dimension(350,196));

		oContentPane.add(listpanel, BorderLayout.CENTER);
		oContentPane.add(createSideButtonPanel(), BorderLayout.EAST);
		oContentPane.add(createButtonPanel(), BorderLayout.SOUTH);

		updateListView();

		lblViews.setText(String.valueOf(vtNodes.size()) + " " +LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UISearchResultDialog.nodesFound")); //$NON-NLS-1$
		pack();
	}

	/**
	 * Draw the panel of buttons for this dialog.
	 */
	private JPanel createSideButtonPanel() {

		JPanel buttonpanel = new JPanel();
		buttonpanel.setBorder(new EmptyBorder(15,5,15,10));

		gb = new GridBagLayout();
		buttonpanel.setLayout(gb);

		gc = new GridBagConstraints();
		gc.insets = new Insets(5,5,5,5);
		gc.fill=GridBagConstraints.HORIZONTAL;
		gc.anchor = GridBagConstraints.CENTER;
		gc.gridwidth = GridBagConstraints.REMAINDER;
		gc.weightx=1;
		gc.weighty=1;

		pbSelectAll = new UIButton(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UISearchResultDialog.selectAllButton")); //$NON-NLS-1$
		pbSelectAll.setMnemonic(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UISearchResultDialog.selectAllButtonMnemonic").charAt(0)); //$NON-NLS-1$
		if (vtResults.size() == 0)
			pbSelectAll.setEnabled(false);
		else
			pbSelectAll.setEnabled(true);

		pbSelectAll.addActionListener(this);
		gb.setConstraints(pbSelectAll, gc);
		buttonpanel.add(pbSelectAll);

		pbInsert = new UIButton(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UISearchResultDialog.insertIntoViewButton")); //$NON-NLS-1$
		pbInsert.setMnemonic(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UISearchResultDialog.insertIntoViewButtonMnemonic").charAt(0)); //$NON-NLS-1$
		if (vtResults.size() == 0)
			pbInsert.setEnabled(false);
		else
			pbInsert.setEnabled(true);

		pbInsert.addActionListener(this);
		gb.setConstraints(pbInsert, gc);
		buttonpanel.add(pbInsert);

		pbView = new UIButton(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UISearchResultDialog.openButton")); //$NON-NLS-1$
		pbView.setMnemonic(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UISearchResultDialog.openButtonMnemonic").charAt(0)); //$NON-NLS-1$
		if (vtResults.size() == 0)
			pbView.setEnabled(false);
		else
			pbView.setEnabled(true);
		pbView.addActionListener(this);
		gb.setConstraints(pbView, gc);
		buttonpanel.add(pbView);
		
		if (oParent != null) {
			pbSearch = new UIButton(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UISearchResultDialog.searchAgainButton")); //$NON-NLS-1$
			pbSearch.setMnemonic(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UISearchResultDialog.searchAgainButtonMnemonic").charAt(0)); //$NON-NLS-1$
			pbSearch.addActionListener(this);
			gb.setConstraints(pbSearch, gc);
			buttonpanel.add(pbSearch);
		}

		return buttonpanel;
	}

	/**
	 * Create the panel of buttons.
	 */
	private UIButtonPanel createButtonPanel() {

		UIButtonPanel oButtonPanel = new UIButtonPanel();

		pbClose = new UIButton(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UISearchResultDialog.closeButton")); //$NON-NLS-1$
		pbClose.setMnemonic(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UISearchResultDialog.closeButtonMnemonic").charAt(0)); //$NON-NLS-1$
		pbClose.addActionListener(this);
		getRootPane().setDefaultButton(pbClose);
		oButtonPanel.addButton(pbClose);

		pbHelp = new UIButton(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UISearchResultDialog.helpButton")); //$NON-NLS-1$
		ProjectCompendium.APP.mainHB.enableHelpOnButton(pbHelp, "basics.search-results", ProjectCompendium.APP.mainHS); //$NON-NLS-1$
		pbHelp.setMnemonic(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UISearchResultDialog.helpButtonMnemonic").charAt(0)); //$NON-NLS-1$
		oButtonPanel.addHelpButton(pbHelp);

		return oButtonPanel;
	}


	/******* EVENT HANDLING METHODS *******/

	/**
	 * Handle the button push events.
	 * @param event, the associated ACtionEvent object.
	 */
	public void actionPerformed(ActionEvent event) {
		Object source = event.getSource();

		// Handle button events
		if (source instanceof JButton) {
			if (source == pbSearch && oParent != null) {
				setVisible(false);
				oParent.setVisible(true);
				oParent.requestFocus();
				dispose();
			}
			else if (source == pbView) {
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
					ex.printStackTrace();
					System.out.println("Error: (UISearchResultDialog.actionPerformed) \n\n"+ex.getMessage()); //$NON-NLS-1$
				}
			}
		}
	}

	/**
	 * Open the select views.
	 */
	public void onView() {
		int [] selection = lstNodes.getSelectedIndices();
		String sViewID = ""; //$NON-NLS-1$
		for(int i=0;i<selection.length;i++) {
			NodeSummary node = (NodeSummary)vtNodes.elementAt(selection[i]);
			if (node instanceof View) {
				View view = (View)node;
				sViewID = view.getId();
				if (!htUserViews.containsKey(sViewID)) { 			
					UIViewFrame viewFrame = ProjectCompendium.APP.addViewToDesktop(view, view.getLabel());
					Vector history = new Vector();
					history.addElement(new String(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UISearchResultDialog.searchResults"))); //$NON-NLS-1$
					viewFrame.setNavigationHistory(history);
				}
			} else {
				String sNodeID = node.getId();				
				if (!htUserViews.containsKey(sNodeID)) { 
					UINodeContentDialog contentDialog = new UINodeContentDialog(this, node, UINodeContentDialog.CONTENTS_TAB);
					UIUtilities.centerComponent(contentDialog, ProjectCompendium.APP);
					contentDialog.setVisible(true);
				}
			}
		}
	}

	/**
	 * Open the contents popup for the currently selected node.
	 */
	public void openContents() {

		int selection = lstNodes.getSelectedIndex();
		NodeSummary node = (NodeSummary)vtNodes.elementAt(selection);
		String sNodeID = node.getId();
		
		if (!htUserViews.containsKey(sNodeID)) { 
			UINodeContentDialog contentDialog = new UINodeContentDialog(this, node, UINodeContentDialog.CONTENTS_TAB);
			UIUtilities.centerComponent(contentDialog, ProjectCompendium.APP);
			contentDialog.setVisible(true);
		}
	}

	/**
	 * Updates the list view with search results list.
	 */
	private void updateListView() {

		//sort the vector
		vtResults = CoreUtilities.sortList(vtResults);
		if (vtResults != null) {

			htUserViews = ProjectCompendium.APP.getModel().getUserViews();
			String id = ""; //$NON-NLS-1$
			for(Enumeration e = vtResults.elements();e.hasMoreElements();) {
				NodeSummary node = (NodeSummary)e.nextElement();
				id = node.getId();		
				node.initialize(ProjectCompendium.APP.getModel().getSession(), ProjectCompendium.APP.getModel());
				ImageIcon img = null;
				img = UINode.getNodeImageSmall(node.getType());
				JLabel label = new JLabel(img, SwingConstants.LEFT);
				
				String text = node.getLabel();
				if (text.equals("")) { //$NON-NLS-1$
					text = "-- "+LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UISearchResultDialog.unlabelledNode")+" --"; //$NON-NLS-1$
				}
				
				if (htUserViews.containsKey(id)) {					
					label.setText( text + " - " + ((String)htUserViews.get(id)) ); //$NON-NLS-1$
					label.setFont(new Font("Helvetica", Font.ITALIC, 12)); //$NON-NLS-1$
					label.setForeground(Color.gray);
					label.validate();					
				} else {
					label.setFont(new Font("Helvetica", Font.PLAIN, 12));					 //$NON-NLS-1$
					label.setText(text);
				}
				
				((DefaultListModel)lstNodes.getModel()).addElement(label);
				vtNodes.addElement(node);				
			}
		}
		lstNodes.setSelectedIndex(0);
	}

	/**
	 * Select All the nodes in the list.
	 */
	private void onSelectAll() {
		int size = 0;
		if(vtResults != null)
			size = vtResults.size();
		lstNodes.setSelectionInterval(0,size-1);
	}

	/**
	 * Insert the selected nodes in the current view.
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

		if (activeFrame instanceof UIListViewFrame) {

			// deselect nodes and links so pasted ones are only ones seleted - bz
			((UIListViewFrame)activeFrame).getUIList().deselectAll();
			int [] selection = lstNodes.getSelectedIndices();

			int index = -1;
			UIList list = ((UIListViewFrame)activeFrame).getUIList();
			View listview = list.getView();
			int nodeCount = listview.getNumberOfNodes();;

			NodePosition[] nps = new NodePosition[selection.length];
			String sNodeID = ""; //$NON-NLS-1$
			for(i=0;i<selection.length;i++) {

				NodeSummary node = (NodeSummary)vtNodes.elementAt(selection[i]);
				sNodeID = node.getId();
				if (!htUserViews.containsKey(sNodeID)) { 				
					node.initialize(session, model);
	
					// CHECK IF NODE WAS DELETED
					boolean deleted = model.getNodeService().isMarkedForDeletion(session, sNodeID);
	
					index = list.getIndexOf(node);
					if(index == -1) {
						// IF NODE WAS DELETED, RESTORE IT
						if (deleted) {
							ProjectCompendium.APP.restoreNode(node, listview);
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
								ProjectCompendium.APP.displayError("Exception: (UISearchResultsDialog.onInsert) \n" + e.getMessage()); //$NON-NLS-1$
								System.out.flush();
							}
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
			ViewPaneUI viewpaneui = uiviewpane.getUI();

			// deselect nodes and links so pasted ones are only ones seleted - bz
			uiviewpane.setSelectedNode(null, ICoreConstants.DESELECTALL);
			uiviewpane.setSelectedLink(null, ICoreConstants.DESELECTALL);

			int [] selection = lstNodes.getSelectedIndices();

			String sNodeID = ""; //$NON-NLS-1$
			NodeSummary node = null;			
			for(i=0;i<selection.length;i++) {

				node = (NodeSummary)vtNodes.elementAt(selection[i]);
				sNodeID = node.getId();

				if (!htUserViews.containsKey(sNodeID)) {				
					node.initialize(session, model);

					// CHECK IF NODE WAS DELETED
					boolean deleted = model.getNodeService().isMarkedForDeletion(session, sNodeID);
	
					UINode uiNodeInView = null;
					if (!deleted) {
						//add the node to the view if it isn't already in there
						uiNodeInView = (UINode)uiviewpane.get(sNodeID);
					}
	
					if(uiNodeInView == null) {
						// IF NODE WAS DELETED, RESTORE IT
						if (deleted) {
							ProjectCompendium.APP.restoreNode(node, view);
						}
						else {
							// GET CURRENT SCROLL POSITION AND ADD THIS TO POSITIONING INFO
							int hPos = activeFrame.getHorizontalScrollBarPosition();
							int vPos = activeFrame.getVerticalScrollBarPosition();
	
							int xpos = hPos + ViewPaneUI.LEFTOFFSET;
							int ypos = vPos + ((i+1)*ViewPaneUI.INTERNODE_DISTANCE);
	
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
		ProjectCompendium.APP.refreshIconIndicators();

		onCancel();
	}

	/**
	 * Handle the dialog closing action. Override superclass to do disposing of parent.
	 */
	public void onCancel() {

		if (oParent != null)
			oParent.dispose();

		setVisible(false);
		dispose();
	}
	
	/**
	 * Helper class the render the list.
	 */
	public class ThisListCellRenderer extends JLabel implements ListCellRenderer {

	  	protected Border noFocusBorder;

		/*
		 * Constructors
		 */
		public ThisListCellRenderer() {
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

			setBorder((cellHasFocus) ? UIManager.getBorder("List.focusCellHighlightBorder") : noFocusBorder); //$NON-NLS-1$

			return this;
		}
	}	
}
