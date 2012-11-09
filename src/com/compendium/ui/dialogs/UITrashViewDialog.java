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
import java.sql.SQLException;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.*;

import com.compendium.core.*;
import com.compendium.core.ICoreConstants;
import com.compendium.core.datamodel.*;

import com.compendium.ProjectCompendium;
import com.compendium.ui.*;
import com.compendium.ui.plaf.*;

/**
 * class UITrashViewDialog
 *
 * @author	Mohammed Sajid Ali / Michelle Bachler
 */
public class UITrashViewDialog extends UIDialog implements ActionListener, IUIConstants {

	/** The parent frame for this dialog.*/
	private JFrame					oParent 		= null;

	/** The main content pane for this dialog.*/
	private Container				oContentPane 	= null;

	/** The scrollpane for the list of deleted nodes.*/
	private JScrollPane				sp 				= null;

	/** The list for the deleted nodes.*/
	private UINavList				lstNodes 		= null;

	/** The button to purge selected nodes.*/
	private UIButton				pbPurge 		= null;

	/** The button to sort the node list by node label.*/
	private UIButton				pbLabel 		= null;

	/** The button to sort the node list by node creation date.*/
	private UIButton				pbCreationDate 	= null;

	/** The button to sort the node list by node modification date.*/
	private UIButton				pbModificationDate = null;

	/** The button to close the dialog.*/
	private UIButton				pbClose = null;

	/** The button to select all nodes in the dialog.*/
	private UIButton				pbSelectAll = null;

	/** The button to restore the selected nodes to the home view.*/
	private UIButton				pbRestore = null;

	/** The button to restore the select nodes to one of its former views.*/
	private UIButton				pbRestoreView = null;

	/** Activates the help opeing to the appropriate section.*/
	private UIButton				pbHelp		= null;

	/** The sorted list of deleted nodes.*/
	private Vector					vtNodes = new Vector();

	/** The list of deleted nodes.*/
	private Vector					vtResults = new Vector();

	/** The UINode trashbin node.*/
	private UINode					oUINode = null;

	/** The dialog holding the progress bar.*/
	private UIProgressDialog	oProgressDialog = null;

	/** The progress bar.*/
	private JProgressBar		oProgressBar = null;

	/** The thread to run the progress.*/
	private ProgressThread		oThread = null;

	/** The counter for the progress bar.*/
	private int 				Count = 0;

	/** The start of the label holding the view count.*/
	private String					sObjectCount 		= "Number of deleted objects : ";

	/** Holds the count of the number of deleted nodes.*/
	private JLabel				lblCount = null;

	/**
	 * Initializes and draw the dialog.
	 * @param parent, the parent node for this dialog.
	 * @param nodeui com.compendium.ui.plaf.nodeui, the trashbin node.
	 */
	public UITrashViewDialog(JFrame parent, NodeUI nodeui) {

	  	super(parent, true);
		oParent = parent;
		NodeUI oNodeUI = nodeui;
		oUINode = oNodeUI.getUINode();

		setTitle("TrashBin (User's Deleted Objects in " + ProjectCompendium.APP.getModel().getModelName() + ")");

		oContentPane = getContentPane();
		oContentPane.setLayout(new BorderLayout());

		GridBagLayout gb = new GridBagLayout();
		JPanel listpanel = new JPanel(gb);

		GridBagConstraints gc = new GridBagConstraints();

		gc = new GridBagConstraints();
		gc.insets = new Insets(5,5,5,5);
		gc.fill=GridBagConstraints.NONE;
		gc.anchor = GridBagConstraints.WEST;
		gc.gridwidth = GridBagConstraints.REMAINDER;
		gc.weightx=1;
		gc.weighty=1;

		listpanel.setBorder(new EmptyBorder(10,15,15,10));

		// Add label
		lblCount = new JLabel("");
		listpanel.add(lblCount);
		gb.setConstraints(lblCount, gc);

		gc.gridwidth = 1;

		// Add label sort button
		pbLabel = new UIButton("Label");
		pbLabel.addActionListener(this);
		pbLabel.setFont(new Font("Dialog", Font.PLAIN, 9));
		gb.setConstraints(pbLabel, gc);
		listpanel.add(pbLabel);

		// Add creation date sort button
		pbCreationDate = new UIButton("Create Date");
		pbCreationDate.addActionListener(this);
		pbCreationDate.setFont(new Font("Dialog", Font.PLAIN, 9));
		gb.setConstraints(pbCreationDate, gc);
		listpanel.add(pbCreationDate);

		// Add modification date sort button
		pbModificationDate = new UIButton("Mod Date");
		pbModificationDate.addActionListener(this);
		pbModificationDate.setFont(new Font("Dialog", Font.PLAIN, 9));
		gc.gridwidth = GridBagConstraints.REMAINDER;
		gb.setConstraints(pbModificationDate, gc);
		listpanel.add(pbModificationDate);

		// Create the list
		lstNodes = new UINavList(new DefaultListModel());
		lstNodes.addMouseListener(new MouseAdapter() {
  			public void mouseClicked(MouseEvent e) {
			  if(e.getClickCount() == 2)
				  onPurge();
  			}
		});

		lstNodes.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		lstNodes.setCellRenderer(new LabelListCellRenderer());
		lstNodes.setBackground(Color.white);

		// create a scroll viewer to add scroll functionality in the list view
		JScrollPane sp = new JScrollPane(lstNodes);
		sp.setPreferredSize(new Dimension(250, 200));

		// add list view to dialog
		gb.setConstraints(sp, gc);
		listpanel.add(sp);

		oContentPane.add(listpanel, BorderLayout.CENTER);
		oContentPane.add(createSideButtonPanel(), BorderLayout.EAST);
		oContentPane.add(createButtonPanel(), BorderLayout.SOUTH);

		// other initializations
		updateListView();

		pack();
		setResizable(false);
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
		gc.weighty=3;

		JPanel buttonpanel = new JPanel();
		buttonpanel.setLayout(gb);
		buttonpanel.setBorder(new EmptyBorder(15,5,15,10));

		JLabel label = new JLabel("");
		gb.setConstraints(label, gc);
		buttonpanel.add(label);

		gc.weighty=1;

		pbPurge = new UIButton("Purge");
		pbPurge.setMnemonic(KeyEvent.VK_P);
		pbPurge.addActionListener(this);
		getRootPane().setDefaultButton(pbPurge);
		gb.setConstraints(pbPurge, gc);
		buttonpanel.add(pbPurge);

		pbSelectAll = new UIButton("Select All");
		pbSelectAll.setMnemonic(KeyEvent.VK_S);
		pbSelectAll.addActionListener(this);
		gb.setConstraints(pbSelectAll, gc);
		buttonpanel.add(pbSelectAll);

		pbRestore = new UIButton("Restore Here");
		pbRestore.setMnemonic(KeyEvent.VK_O);
		pbRestore.addActionListener(this);
		gb.setConstraints(pbRestore, gc);
		buttonpanel.add(pbRestore);

		pbRestoreView = new UIButton("Restore To View...");
		pbRestoreView.setMnemonic(KeyEvent.VK_O);
		pbRestoreView.addActionListener(this);
		gb.setConstraints(pbRestoreView, gc);
		buttonpanel.add(pbRestoreView);

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
		oButtonPanel.addButton(pbClose);

		pbHelp = new UIButton("Help");
		ProjectCompendium.APP.mainHB.enableHelpOnButton(pbHelp, "basics.trashbin", ProjectCompendium.APP.mainHS);
		pbHelp.setMnemonic(KeyEvent.VK_H);
		oButtonPanel.addHelpButton(pbHelp);

		return oButtonPanel;
	}

	/**
	 * Handle button push events.
	 * @param event, the assoicated ActionEvent.
	 */
	public void actionPerformed(ActionEvent event) {

		Object source = event.getSource();
		// Handle button events
		if (source instanceof JButton) {
			if (source == pbPurge) {
				onPurge();
			}
			else if (source == pbLabel) {
				onSortBy(CoreUtilities.LABEL);
			}
			else if (source == pbCreationDate) {
				onSortBy(CoreUtilities.CREATION_DATE);
			}
			else if (source == pbModificationDate) {
				onSortBy(CoreUtilities.MODIFICATION_DATE);
			}
			else if (source == pbClose) {
				onCancel();
			}
			else if (source == pbSelectAll) {
				onSelectAll();
			}
			else if (source == pbRestore) {
				onRestore();
			}
			else if (source == pbRestoreView) {
				onRestoreView();
			}
		}
	}

	/**
	 * Sort the list of deleted nodes by the given field.
	 * @param sortCriteria, the field to sort the list by.
	 */
	private void onSortBy(int sortCritieria) {

		((DefaultListModel)lstNodes.getModel()).removeAllElements();

		//sort the vector
		vtNodes = CoreUtilities.sortList(vtNodes, sortCritieria);

		for(Enumeration e = vtNodes.elements();e.hasMoreElements();) {

			NodeSummary node = (NodeSummary)e.nextElement();

			//System.out.println("Processing " + node.getLabel());
			ImageIcon img = null;
			img = UINode.getNodeImageSmall(node.getType());

			//trim text to fit the label for the timebeing since the label comes out of the scrollbar window
			StringBuffer sb = new StringBuffer(40);
			sb.ensureCapacity(40);
			String text = "";

			text = node.getLabel();
			if(text.length() > 40) {
				text = text.substring(0,36);
				text += "...";
			}
			else {
				int i=0;String padString = "";
				int pad = 40 - (node.getLabel()).length();
				while(i++ < pad) {
					text += " ";
				}
			}

			String nodelabel = text;

			String creationDate = "";
			String modificationDate = "";
			if(sortCritieria == CoreUtilities.CREATION_DATE)
				creationDate = UIUtilities.getSimpleDateFormat("dd, MMMM, yyyy").format(node.getCreationDate()).toString();
			else
			if(sortCritieria == CoreUtilities.MODIFICATION_DATE)
				modificationDate = UIUtilities.getSimpleDateFormat("dd, MMMM, yyyy").format(node.getModificationDate()).toString();

			//text = nodelabel + "    " + creationDate + "     " + modificationDate;

			sb.insert(0,nodelabel);
			sb.append(creationDate);
			sb.append(modificationDate);

			//JLabel label = new JLabel(text,img,SwingConstants.LEFT);
			JLabel label = new JLabel(sb.toString(),img,SwingConstants.CENTER);
			label.addMouseListener(new MouseAdapter() {
  				public void mouseClicked(MouseEvent e) {
			  		if(e.getClickCount() == 2)
				  		onPurge();
  				}
			});
			label.setToolTipText(text);
			((DefaultListModel)lstNodes.getModel()).addElement(label);
		}

		lstNodes.setSelectedIndex(0);
	}

	/**
	 * The thread class that runs the progress bar.
	 */
	private class ProgressThread extends Thread {

		public ProgressThread() {
	  		oProgressDialog = new UIProgressDialog(ProjectCompendium.APP,"Purging Nodes...", "Purge completed");
	  		oProgressDialog.showDialog(oProgressBar);
	  		oProgressDialog.setModal(true);
		}

		public void run() {
	  		oProgressDialog.setVisible(true);
		}
	}

	/**
	 * Check if the user has cancelled the restore.
	 */
	private boolean checkProgress() {

	  	if (oProgressDialog.isCancelled()) {

			int result = JOptionPane.showConfirmDialog(oProgressDialog,
							"Do you want to Cancel Purging",
							"Cancel Purge",
							JOptionPane.YES_NO_OPTION);

			if (result == JOptionPane.YES_OPTION) {
				oProgressDialog.setVisible(false);
				return true;
			}
	  		else {
				oProgressDialog.setCancelled(false);
			  return false;
	  		}
		}
		return false;
	}

	/**
	 * Start the progress bar for the purge request, the start the purge.
	 * @see #processPurge
	 */
	public void onPurge() {

		//get the optionpane dialog
		int resp = JOptionPane.showConfirmDialog(ProjectCompendium.APP, "Do you really want to purge from trashbin?");

		if((resp == JOptionPane.NO_OPTION) || (resp == JOptionPane.CANCEL_OPTION) || (resp == JOptionPane.CLOSED_OPTION))
			return ;

  		oProgressBar = new JProgressBar();
  		oProgressBar.setMinimum(0);
  		oProgressBar.setMaximum(100);

		oThread = new ProgressThread();
		oThread.start();

		Thread thread = new Thread("UITrashViewDialog: Purge") {
			public void run() {
				processPurge();
			}
		};
		thread.start();
	}

	/**
	 * Pruge all the selected nodes from the database permenantly.
	 */
	private void processPurge() {

		ProjectCompendium.APP.setWaitCursor();

		Vector vtDeletedNodes = new Vector();

		int [] selection = lstNodes.getSelectedIndices();

		boolean audioPlayed = false;

		int count = selection.length;
  		oProgressBar.setMaximum(count);

		for(int i=0;i<selection.length;i++) {

			if (checkProgress())
				return;

			if (i < vtNodes.size()) { // SHOULDN'T BE NEEDED, BUT IT FELL OVER

				boolean deleted = false;
				NodeSummary node = (NodeSummary)vtNodes.elementAt(selection[i]);
				try {
					deleted = ProjectCompendium.APP.getModel().getNodeService().purgeNode(ProjectCompendium.APP.getModel().getSession(), node.getId());
					//if node cannot be deleted then inform the user
					if(!deleted) {
						//popup the error message
						JOptionPane oOptionPane = new JOptionPane("Cannot delete " + node.getLabel());
						JDialog oDialog = oOptionPane.createDialog(oContentPane,"Delete Error..");
						oDialog.setModal(true);
						oDialog.setVisible(true);
						oDialog.dispose();
					}
					else {
						//mark the corresponding node in vtNodes vector as deleted
						vtDeletedNodes.addElement(node.getId());

						//play audio once
						if(!audioPlayed) {
							ProjectCompendium.APP.getAudioPlayer().playAudio(UIAudio.PURGING_ACTION);
							audioPlayed = true;
						}
					}
				}
				catch(SQLException ex) {
					ProjectCompendium.APP.displayError("Exception: (UITrashViewDialog.onPurge) " + ex.getMessage());
				}
			}

			Count = i+1;
			oProgressBar.setValue(Count);
			oProgressDialog.setStatus(Count);
		}

		// UPDATE TRASHBIN LIST
		updateListView();

		ProjectCompendium.APP.setDefaultCursor();

		oProgressDialog.setVisible(false);
		oProgressDialog.dispose();

		if(vtNodes.size() == 0)
			onCancel();
	}

	/**
	 * Open an instance of UIDeletedViewDialog, for the user to select a view to restore to.
	 * @see com.compendium.ui.dialogs.UIDeletedViewDialog
	 */
	private void onRestoreView() {
		int index = lstNodes.getSelectedIndex();
		if (index != -1) {
			NodeSummary oNode = (NodeSummary)vtNodes.elementAt(index);
			UIDeletedViewDialog dialog = new UIDeletedViewDialog(oParent, oNode, this);
			dialog.setVisible(true);
		}
	}

	/**
	 * Restore the selected nodes to the home view.
	 */
	private void onRestore() {

		ProjectCompendium.APP.setWaitCursor();

		View homeView = ProjectCompendium.APP.getHomeView();
		UIViewFrame homeFrame = ProjectCompendium.APP.getInternalFrame(homeView);
		View view = homeFrame.getView();

		int [] selection = lstNodes.getSelectedIndices();
		for(int i=0; i< selection.length;i++) {
			NodeSummary node = (NodeSummary)vtNodes.elementAt(selection[i]);
			ProjectCompendium.APP.restore(node, view);
		}

		// UPDATE TRASHBIN LIST
		updateListView();

		// INCASE TRANSCLUSION NUMBERS NEED UPDATING
		ProjectCompendium.APP.refreshIconIndicators();

		ProjectCompendium.APP.setDefaultCursor();
	}

	/**
	 * Updates the list of deleted views from the database.
	 */
	public void updateListView() {

		((DefaultListModel)lstNodes.getModel()).removeAllElements();
		vtNodes.removeAllElements();

		IModel model = ProjectCompendium.APP.getModel();
		PCSession session = model.getSession();
		try {
			vtResults = ProjectCompendium.APP.getModel().getNodeService().getDeletedNodeSummary(session);
		}
		catch(SQLException re) {}

		//change the icon depending upon if the trashbin is full or not
		if(vtResults.size() > 0) {
			ImageIcon img = UIImages.getNodeIcon(IUIConstants.TRASHBINFULL_ICON);
			oUINode.setIcon(img);
		}
		else {
			ImageIcon img = UIImages.getNodeIcon(IUIConstants.TRASHBIN_ICON);
			oUINode.setIcon(img);
		}

		lblCount.setText(sObjectCount + String.valueOf(vtResults.size()));

		if(vtResults.size() < 1)
			return;

		//sort the vector
		vtResults = CoreUtilities.sortList(vtResults);

		for(Enumeration e = vtResults.elements();e.hasMoreElements();) {

			NodeSummary node = (NodeSummary)e.nextElement();
			node.initialize(session, model);

			ImageIcon img = null;
			img = UINode.getNodeImageSmall(node.getType());

			//trim text to fit the label for the timebeing since the label comes out of the scrollbar window
			String text = node.getLabel();

			if(text.length() > 40) {
				text = text.substring(0,39);
				text += "....";
			}

			JLabel label = new JLabel(text,img,SwingConstants.LEFT);
			label.addMouseListener(new MouseAdapter() {
  				public void mouseClicked(MouseEvent e) {
				  if(e.getClickCount() == 2)
					  onPurge();
  				}
			});
			label.setToolTipText(text);
			((DefaultListModel)lstNodes.getModel()).addElement(label);
			vtNodes.addElement(node);
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
}
