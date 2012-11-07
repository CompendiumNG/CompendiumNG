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

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.sql.SQLException;

import javax.swing.*;
import javax.swing.border.*;

import com.compendium.ui.*;
import com.compendium.core.*;
import com.compendium.ui.dialogs.*;
import com.compendium.ui.plaf.*;
import com.compendium.LanguageProperties;
import com.compendium.ProjectCompendium;
import com.compendium.core.datamodel.*;
import com.compendium.meeting.MeetingEvent;


/**
 * Displays a list of node labels whose labels start with the passed string.
 *
 * @author	Michelle Bachler
 */
public class UIHintNodeLabelPanel extends JPanel {

	/** The list of nodes whose labels start with the passed String.*/
	private UINavList 	lstNodes 		= null;

	/** The current view pane.*/
	private UIViewPane 	oPane 			= null;

	/** The node whose label we are trying to match.*/
	private UINode 		oNode 			= null;

	/** The currently selected index of the node list.*/
	private int 		selectedIndex 	= 0;

	/** The current IModel object to use to access the database.*/
	private IModel 		model 			= null;

	/** The current PCSession object to use when accessing the database.*/
	private	PCSession 	session 		= null;

	/**
	 * Constructor. Searches the database and displays a list of node labels starting with the passed string.
	 *
	 * @param UIViewPane uiviewpane, the view pane we are to draw this panel in.
	 * @param NodeSummary node, the node whose label is being searched.
	 * @param int xPos, the x position to draw this panel.
	 * @param int yPos, the y position to draw this panel.
	 * @param String text, the node label text to search on.
	 */
	public UIHintNodeLabelPanel(UIViewPane uiviewpane, UINode node, int xPos, int yPos, String text) throws Exception {

		oPane = uiviewpane;
		oNode = node;
		
		model = oNode.getNode().getModel();
		session = oNode.getNode().getSession();
		if (model != null) {
			if (session == null)
				session = model.getSession();
		}
		else {
			model = ProjectCompendium.APP.getModel();
			session = model.getSession();
		}

		setBorder(new LineBorder(Color.black, 1));
		setLocation(xPos, yPos);

		BorderLayout layout = new BorderLayout();
		layout.setHgap(0);
		layout.setVgap(0);
		setLayout(layout);

		setBackground(Color.white);

		lstNodes = new UINavList();
		lstNodes.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION );
        NodeListCellRenderer nodeListRenderer = new NodeListCellRenderer();
		lstNodes.setCellRenderer(nodeListRenderer);
		lstNodes.setBackground(Color.white);
		lstNodes.setBorder(null);

		final UIViewPane pane = uiviewpane;
		lstNodes.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if(e.getClickCount() == 2) {
					onTransclude();
					pane.hideLabels();
	  			}
				e.consume();
			}
		});

		lstNodes.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				int keyCode = e.getKeyCode();
				int modifiers = e.getModifiers();
				if (keyCode == KeyEvent.VK_ENTER && modifiers == 0) {
					onTransclude();
					pane.hideLabels();
					e.consume();
	  			}
				else if (keyCode == KeyEvent.VK_UP && modifiers == 0) {
					int index = lstNodes.getSelectedIndex();
					if (index == 0) {
						oNode.getUI().setEditing();
					}
					selectedIndex = index;
				}
				else if (keyCode == KeyEvent.VK_DOWN && modifiers == 0) {
					selectedIndex = lstNodes.getSelectedIndex();
				}
				else if (keyCode == KeyEvent.VK_F1 && modifiers == 0) {
					openContents();
				}
			}
		});

		JScrollPane sp = new JScrollPane(lstNodes, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		sp.setBorder(null);
		sp.setPreferredSize(new Dimension(255,100));

		if (!searchLabel(text, node.getNode().getId())) {
			throw new Exception(LanguageProperties.getString(LanguageProperties.PANELS_BUNDLE, "UIHintNodeLabelPanel.noMatches")); //$NON-NLS-1$
		}

		add(sp);
		setSize(getPreferredSize());
        validate();
	}

	/**
	 * Search the database for the given string excluding in the results the passed nodeid.
	 * Load the results into the displayed list.
	 *
	 * @param String text, the text to match against the start of node labels.
	 * @param String nodeid, the id of the node to exclude from the search results.
	 * @return boolean, true if results found and loaded, else false.
	 */
	public boolean searchLabel(String text, String nodeid) {
		try {
			String cleantext = CoreUtilities.cleanSQLText(text, FormatProperties.nDatabaseType);
			IModel model = ProjectCompendium.APP.getModel();
			Vector vtNodes = model.getQueryService().searchTransclusions(model.getSession(), cleantext, nodeid);
			if (vtNodes.size() > 0) {
				vtNodes = CoreUtilities.sortList(vtNodes);
				lstNodes.setListData(vtNodes);
				return true;
			}
		}
		catch(SQLException ex) {
			ProjectCompendium.APP.displayError("Exception:" + ex.getMessage()); //$NON-NLS-1$
		}
		return false;
	}

	/**
	 * Force the cell renderer to recalculate font size.
	 */
	public void refresh() {
        NodeListCellRenderer nodeListRenderer = new NodeListCellRenderer();
		lstNodes.setCellRenderer(nodeListRenderer);	
		lstNodes.repaint();
	}
	
	/**
	 * This class draws the elements of the list of nodes whose labels match
	 */
	private class NodeListCellRenderer extends JLabel implements ListCellRenderer {

	  	protected Border noFocusBorder;

		NodeListCellRenderer() {
        	super();
			noFocusBorder = new EmptyBorder(1, 1, 1, 1);
			setOpaque(true);
			setBorder(noFocusBorder);
			
			Font font = getFont();
			int scale = ProjectCompendium.APP.getToolBarManager().getTextZoom();
			Font newFont = new Font(font.getName(), font.getStyle(), font.getSize()+ scale);
			setFont(newFont);
		}

		public Component getListCellRendererComponent(
        	JList list,
            Object value,
            int modelIndex,
            boolean isSelected,
            boolean cellHasFocus) {

			removeAll();

			NodeSummary node = (NodeSummary)value;
			ImageIcon icon = UINode.getNodeImageSmall(node.getType());

 	 		if (isSelected) {
				setBackground(list.getSelectionBackground());
				setForeground(list.getSelectionForeground());
			}
			else {
				setBackground(list.getBackground());
				setForeground(list.getForeground());
			}

			String text = (String)node.getLabel();
			if(text.length() > 40) {
				text = text.substring(0,39);
				text += "...."; //$NON-NLS-1$
			}

			setBorder((cellHasFocus) ? UIManager.getBorder("List.focusCellHighlightBorder") : noFocusBorder); //$NON-NLS-1$

			setIcon(icon);
			setText(text);
			return this;
		}
	}

	/**
	 * Open the contents popup for the currently selected node.
	 */
	public void openContents() {

		Object obj = lstNodes.getSelectedValue();
		if (obj != null) {
			NodeSummary node = (NodeSummary)obj;
			node.initialize(session, model);
			UINodeContentDialog contentDialog = new UINodeContentDialog(ProjectCompendium.APP, node, UINodeContentDialog.CONTENTS_TAB);
			UIUtilities.centerComponent(contentDialog, ProjectCompendium.APP);
			contentDialog.setVisible(true);
		}
	}

	/**
	 * Focus the first line in the node list
	 */
	public void focusList() {
		lstNodes.requestFocus();
		lstNodes.setSelectedIndex(0);
	}

	/**
	 * Replace the passed node with the one selected from the list of Nodes with matching text.
	 */
	private void onTransclude() {

		Object obj = lstNodes.getSelectedValue();
		if (obj != null) {

			NodeSummary node = (NodeSummary)obj;
			if (oNode != null) {
				ViewPaneUI viewpaneui = oPane.getUI();
				Point loc = oNode.getNodePosition().getPos();
				int x = loc.x;
				int y = loc.y;

				// preserve links so they can be transfer to the new node.
				Vector keepLinks = new Vector();
				for(Enumeration es = oNode.getLinks();es.hasMoreElements();) {
					UILink uilink = (UILink)es.nextElement();
					keepLinks.addElement(uilink);
				}
				String oldNodeID = oNode.getNode().getId();
				
				// CHECK TO SEE IF THE SELECTED NODE IS ALREADY IN THIS VIEW
				// IF IT IS - ASK IF THEY WANT TO CREATE A SHORTCUT
				Object obj2 = oPane.get(node.getId());
				if (obj2 != null && obj2 instanceof UINode) {
					UINode uinode = (UINode)obj2;
			   		int answer = JOptionPane.showConfirmDialog(this, 			   				
			   				LanguageProperties.getString(LanguageProperties.PANELS_BUNDLE, "UIHintNodeLabelPanel.message1a")+"\n\n"+
			  				LanguageProperties.getString(LanguageProperties.PANELS_BUNDLE, "UIHintNodeLabelPanel.message1b")+"\n\n", 
			   				LanguageProperties.getString(LanguageProperties.PANELS_BUNDLE, "UIHintNodeLabelPanel.message1Title"), //$NON-NLS-1$ //$NON-NLS-2$
							JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

					if (answer == JOptionPane.YES_OPTION) {
						viewpaneui.onDelete(); // DO I REALLY WANT TO PURGE AT THIS POINT?
						UINode uiNode = viewpaneui.createShortCutNode(uinode, x, y);
						restoreLinks(keepLinks, oldNodeID, uiNode, viewpaneui);
					}
					else {
						return;
					}
				}
				else {
					node.initialize(session, model);
					viewpaneui.onDelete();
					UINode uiNode = viewpaneui.addNodeToView(node, x, y);
					restoreLinks(keepLinks, oldNodeID, uiNode, viewpaneui);
				}
			}
		}
	}
	
	/**
	 * Restore any links that where on the node that was replaced with the transclusion
	 * @param links the links to restore
	 * @param oldNodeID the id that the node had before it was replaced
	 * @param newNode the new translcuded node
	 * @param viewui the view they are all in
	 */
	private void restoreLinks(Vector links, String oldNodeID, UINode newNode, ViewPaneUI viewui) {
		int count = links.size();
		for(int i=0; i<count; i++) {
			UILink uilink = (UILink)links.elementAt(i);
			LinkProperties props = uilink.getLinkProperties();
			UINode fromNode = uilink.getFromNode();
			UINode toNode = uilink.getToNode();
			if (fromNode == null || fromNode.getNode() == null || 
					fromNode.getNode().getId().equals(oldNodeID)) {							
				viewui.createLink(newNode, toNode, UIUtilities.getLinkType(newNode), props);
			} else if (toNode == null || toNode.getNode() == null || 
					toNode.getNode().getId().equals(oldNodeID) ) {
				viewui.createLink(fromNode, newNode, UIUtilities.getLinkType(newNode), props);
			}
		}
	}
}