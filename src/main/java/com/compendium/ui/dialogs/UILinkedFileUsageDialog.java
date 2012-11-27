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
 * Displays the nodes that a given linked file is referenced on.
 *
 * @author	Michelle Bachler
 */
public class UILinkedFileUsageDialog extends UIDialog implements ActionListener, IUIConstants {

	/** The search results list of nodes.*/
	private UINavList				lstNodes 		= null;

	/** The button to close the dialog.*/
	private UIButton				pbClose 		= null;

	/** The button to open the relevant help.*/
	private UIButton				pbHelp	 		= null;
	
	/** The list of nodes added to the JList.*/
	private Vector					vtNodes 		= new Vector();

	/** The search results.*/
	private Vector					vtResults 		= new Vector();

	/** A reference to the parent search dialog.*/
	private UISearchDialog			oParent 		= null;
	
	/** Lists all the user home and inbox views for filtering.*/
	private Hashtable				htUserViews		= null;

	/**
	 * Initializes and sets up the dialog for LimboNodes dialog only.
	 * @param frame, the parent frame for this dialog.
	 * @param results, the search results list.
	 * @param sTitle, the title for this dialog.
	 */
	public UILinkedFileUsageDialog(JDialog oParent, String source, Vector results) {
	 	super(oParent, true);
		vtResults = results;		
		setTitle(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UILinkedFileUsageDialog.usageFor")+": "+new File(source).getName()); //$NON-NLS-1$
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
		oContentPane.add(createButtonPanel(), BorderLayout.SOUTH);

		updateListView();

		lblViews.setText(String.valueOf(vtNodes.size()) +" "+LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UILinkedFileUsageDialog.nodesFound")); //$NON-NLS-1$
		pack();
	}

	/**
	 * Create the panel of buttons.
	 */
	private UIButtonPanel createButtonPanel() {

		UIButtonPanel oButtonPanel = new UIButtonPanel();

		pbClose = new UIButton(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UILinkedFileUsageDialog.close")); //$NON-NLS-1$
		pbClose.setMnemonic(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UILinkedFileUsageDialog.closeMnemonic").charAt(0));
		pbClose.addActionListener(this);
		getRootPane().setDefaultButton(pbClose);
		oButtonPanel.addButton(pbClose);

		//pbHelp = new UIButton("Help");
		//ProjectCompendium.APP.mainHB.enableHelpOnButton(pbHelp, "basics.search-results", ProjectCompendium.APP.mainHS);
		//pbHelp.setMnemonic(KeyEvent.VK_H);
		//oButtonPanel.addHelpButton(pbHelp);

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
			if (source == pbClose) {
				onCancel();
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
		//vtResults = CoreUtilities.sortList(vtResults);
		if (vtResults != null) {
			htUserViews = ProjectCompendium.APP.getModel().getUserViews();
			String id = ""; //$NON-NLS-1$
			for(Enumeration e = vtResults.elements();e.hasMoreElements();) {
				NodeSummary node = (NodeSummary)e.nextElement();
				System.out.println("node="+node); //$NON-NLS-1$
				if (node != null) {
					id = node.getId();		
					node.initialize(ProjectCompendium.APP.getModel().getSession(), ProjectCompendium.APP.getModel());
					ImageIcon img = null;
					img = UINode.getNodeImageSmall(node.getType());
					JLabel label = new JLabel(img, SwingConstants.LEFT);
					
					String text = node.getLabel();
					if (text.equals("")) { //$NON-NLS-1$
						text = "-- "+LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UILinkedFileUsageDialog.unlabelledNode")+" --"; //$NON-NLS-1$
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
		}
		lstNodes.setSelectedIndex(0);
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
