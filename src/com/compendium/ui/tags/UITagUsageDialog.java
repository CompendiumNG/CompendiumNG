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

package com.compendium.ui.tags;

import java.util.*;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.*;

import com.compendium.*;
import com.compendium.ui.*;
import com.compendium.ui.panels.*;

import com.compendium.core.datamodel.*;
import com.compendium.core.*;
import com.compendium.ui.dialogs.UIDialog;

/**
 * Class UICodeNodePanel, displays the nodes a given code has been assigned to.
 *
 * @author	Michelle Bachler
 */
public class UITagUsageDialog extends UIDialog implements ActionListener, IUIConstants {

	/** The scrollpane for the list of nodes.*/
	private JScrollPane			sp 					= null;

	/** The list of nodes for the given code.*/
	private UINavList			lstViews 			= null;

	/** The label for the list.*/
	private JLabel 				lblViews			= null;

	/** The button to display the parent views for the selected node.*/
	private UIButton			pbView 				= null;

	/** The button to remove  the code from the selected nodes.*/
	private UIButton			pbRemove			= null;

	/** The button to close the dialog.*/
	private UIButton			pbCancel			= null;

	/** The button to open the relevant help.*/
	private UIButton			pbHelp				= null;

	/** The list of nodes for the current code.*/
	private Vector				oNodes 				= new Vector();

	/** The layout used by this dialog.*/
	private GridBagLayout 		gb	 				= null;

	/** The main panel holding the contents for this dialog.*/
	private JPanel				centerpanel			= null;

	/** The model for the currently open database.*/
	private IModel 				model				= null;

	/** The session for the current user in the current model.*/
	private PCSession 			session				= null;

	/** The oane to add the dailog contents to.*/
	private Container			oContentPane		= null;

	/** The code for whihc this dialog is displaying the usage.*/
	private Code				code				= null;
	
	/** The parent Panel for this dialog.*/
	private UITagTreePanel		oParent				= null;
		
	/**
	 * Constructor. Initializes and draws this dialog.
	 *
 	 * @param code the code for which we are displaying the usage.
	 * @param parent the parent panel which launched this dialog.
	 */
	public UITagUsageDialog(Code code, UITagTreePanel parent) {

		super(ProjectCompendium.APP, true);

		this.code = code;
		model = ProjectCompendium.APP.getModel();
		session = ProjectCompendium.APP.getModel().getSession();
		oParent = parent;

		// set title and background
		setTitle(LanguageProperties.getString(LanguageProperties.TAGS_BUNDLE, "UITagUsageDialog.nodewithtag")+code.getName()); //$NON-NLS-1$

		init();
	}

	/**
	 * Initialize and draw this panel.
	 */
	private void init() {

		oContentPane = getContentPane();
		oContentPane.setLayout(new BorderLayout());

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
		JLabel lblNodes = new JLabel(LanguageProperties.getString(LanguageProperties.TAGS_BUNDLE, "UITagUsageDialog.nodes")); //$NON-NLS-1$
		gc.gridy = 0;
		gc.gridwidth=3;
		gc.weightx=1;
		gb.setConstraints(lblNodes, gc);
		centerpanel.add(lblNodes);

		// Create the list
		lstViews = new UINavList(new DefaultListModel());
		lstViews.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
	  			if(e.getClickCount() == 2)
		  			onViewNode();
  			}
		});

		lstViews.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		lstViews.setCellRenderer(new LabelListCellRenderer());
		lstViews.setBackground(Color.white);
		JScrollPane sp = new JScrollPane(lstViews);
		sp.setPreferredSize(new Dimension(300,200));
		gc.gridy = 1;
		gc.fill = GridBagConstraints.BOTH;
		gb.setConstraints(sp, gc);
		centerpanel.add(sp);

		lblViews = new JLabel(""); //$NON-NLS-1$
		gc.gridy = 2;
		gc.fill = GridBagConstraints.NONE;
		gb.setConstraints(lblViews, gc);
		centerpanel.add(lblViews);

		pbView = new UIButton(LanguageProperties.getString(LanguageProperties.TAGS_BUNDLE, "UITagUsageDialog.showviews")); //$NON-NLS-1$
		pbView.addActionListener(this);
		gc.gridy = 3;
		gc.gridwidth=1;
		gc.weightx=0;
		gc.anchor = GridBagConstraints.NORTHWEST;
		gb.setConstraints(pbView, gc);
		centerpanel.add(pbView);

		pbRemove = new UIButton(LanguageProperties.getString(LanguageProperties.TAGS_BUNDLE, "UITagUsageDialog.removetag")); //$NON-NLS-1$
		pbRemove.addActionListener(this);
		gc.gridy = 3;
		gc.gridwidth=1;
		gc.weightx=1;
		gc.anchor = GridBagConstraints.NORTHEAST;
		gb.setConstraints(pbRemove, gc);
		centerpanel.add(pbRemove);

		// other initializations
		updateListView();

		// BUTTON PANEL
		UIButtonPanel oButtonPanel = new UIButtonPanel();

		pbCancel = new UIButton(LanguageProperties.getString(LanguageProperties.TAGS_BUNDLE, "UITagUsageDialog.closeButton")); //$NON-NLS-1$
		pbCancel.setMnemonic(LanguageProperties.getString(LanguageProperties.TAGS_BUNDLE, "UITagUsageDialog.closeButtonMenmonic").charAt(0));
		pbCancel.addActionListener(this);
		getRootPane().setDefaultButton(pbCancel);
		oButtonPanel.addButton(pbCancel);

		pbHelp = new UIButton(LanguageProperties.getString(LanguageProperties.TAGS_BUNDLE, "UITagUsageDialog.helpButton")); //$NON-NLS-1$
		pbHelp.setMnemonic(LanguageProperties.getString(LanguageProperties.TAGS_BUNDLE, "UITagUsageDialog.helpButtonMnemonic").charAt(0));
		ProjectCompendium.APP.mainHB.enableHelpOnButton(pbHelp, "tag.maintenance-usage", ProjectCompendium.APP.mainHS); //$NON-NLS-1$
		oButtonPanel.addHelpButton(pbHelp);

		oContentPane.add(centerpanel, BorderLayout.CENTER);
		oContentPane.add(oButtonPanel, BorderLayout.SOUTH);

		setResizable(false);
		pack();
	}

	/**
	 * Handle button push events.
	 * @param event the associated ActionEvent.
	 */
	public void actionPerformed(ActionEvent event) {
		Object source = event.getSource();

		// Handle button events
		if (source instanceof JButton) {
			if (source == pbView) {
				onViewNode();
			}
			else if (source == pbRemove) {
				onRemoveCode();
			}
			else if (source == pbCancel) {
				onCancel();
			}
		}
	}

	/**
	 * Handle the show node views action.
	 */
	private void onViewNode() {

		int selection = lstViews.getSelectedIndex();
		NodeSummary node = (NodeSummary)oNodes.elementAt(selection);
		NodeViewDialog nodeview = new NodeViewDialog(this, node);
		nodeview.setVisible(true);
	}

	/**
	 * Helper class to diaply the parent view for a node.
	 */
	private class NodeViewDialog extends UIDialog {

		public NodeViewDialog(UITagUsageDialog parent, NodeSummary node) {
			super((JDialog)parent, true);

			setTitle(LanguageProperties.getString(LanguageProperties.TAGS_BUNDLE, "UITagUsageDialog.viewfornode")+node.getLabel()); //$NON-NLS-1$

			Container oContentPane = getContentPane();
			oContentPane.setLayout(new BorderLayout());
			UINodeViewPanel panel = new UINodeViewPanel(ProjectCompendium.APP, node, this);
			panel.setDefaultButton();
			oContentPane.add(panel, BorderLayout.CENTER);
			setResizable(false);
			pack();
		}
	}

	/**
	 * Handles the removing of the current code from the selected nodes.
	 */
	private void onRemoveCode() {

		int selection = lstViews.getSelectedIndex();

		try {
			NodeSummary node = (NodeSummary)oNodes.elementAt(selection);
			node.removeCode(code);
			updateListView();
			
			if (oParent != null) {
				oParent.updateTreeData();
			} 
		}
		catch(Exception ex) {
			ProjectCompendium.APP.displayError("Error: (UICodeNodeDialog.removeCode)\n\n"+ex.getMessage()); //$NON-NLS-1$
		}
	}

	/**
	 * Updates the list view with the view vector.
	 * This method used in displaying the containing views for the given node.
	 */
	public void updateListView() {

		String userID = model.getUserProfile().getId();
		try {
			Vector nodes = model.getCodeService().getNodes(session, code.getId(), userID);
			if (nodes.size() == 0)
				return;

			removeAllViews();
			nodes = CoreUtilities.sortList(nodes);

			int count = nodes.size();
			for(int i=0; i<count; i++) {

				NodeSummary node = (NodeSummary) nodes.elementAt(i);
				node.initialize(session, model);

				ImageIcon img = null;

				int nType = node.getType();
				img = UINode.getNodeImageSmall(nType);

				String text = node.getLabel();
				JLabel label = new JLabel(text,img,SwingConstants.LEFT);

				label.setToolTipText(text);
				((DefaultListModel)lstViews.getModel()).addElement(label);
				oNodes.addElement(node);
			}
		}
		catch(Exception ex) {
			ProjectCompendium.APP.displayError(LanguageProperties.getString(LanguageProperties.TAGS_BUNDLE, "UITagUsageDialog.exceptionusage")+code.getName()); //$NON-NLS-1$
		}

		lstViews.setSelectedIndex(0);
		updateViewCount();
	}

	/**
	 * Updates the number of occurences for the given node.
	 */
	public void updateViewCount() {
		lblViews.setText(LanguageProperties.getString(LanguageProperties.TAGS_BUNDLE, "UITagUsageDialog.occurences") +": "+ String.valueOf(oNodes.size())); //$NON-NLS-1$
	}

	/**
	 * This is a convenience method to delete all the views in the hashtables and vectors.
	 */
	public void removeAllViews() {

		((DefaultListModel)lstViews.getModel()).removeAllElements();
		oNodes.removeAllElements();
	}
}
