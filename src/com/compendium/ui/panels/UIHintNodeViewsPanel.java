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


package com.compendium.ui.panels;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;
import javax.swing.border.*;

import com.compendium.ui.*;
import com.compendium.ProjectCompendium;
import com.compendium.core.CoreUtilities;
import com.compendium.core.datamodel.*;


/**
 * Displays the given node's parent views.
 *
 * @author	Michelle Bachler
 */
public class UIHintNodeViewsPanel extends JPanel {

	/** A list of the parent views for the passed node.*/
	protected Vector views = null;

	/** A JList object displaying the list of parent views for the passed node.*/
	protected UINavList lstViews = null;

	/** A reference to the current frame.*/
	protected UIViewFrame frame = null;

	/** The title for this hint.*/
	protected String sTitle = "Node Rollover Views";
	
	/** The user Id of the current user */
	protected String userID = "";
	
	/** The list of user views.*/
	protected Hashtable htUserViews = null;
	
	/** The node the views list if for.*/
	protected NodeSummary oNode = null;
	
	/** the current view.*/
	protected View currentView = null;
	
	/**
	 * Constructor. Loads the given node's parent views, and paints them in this panel.
	 *
	 * @param NodeSummary node, the node whose parent views to put in this panel in a JTextArea.
	 * @param int xPos, the x position to draw this panel.
	 * @param int yPos, the y position to draw this panel.
	 */
	public UIHintNodeViewsPanel(NodeSummary node, int xPos, int yPos) {

		setBorder(new LineBorder(Color.gray, 1));
		setLocation(xPos, yPos);
		
		//this.userID = userID;

		//JTextArea area = new JTextArea();

		//WANT THE TOOLTIP FONT / BACKGROUND
		JToolTip tool = new JToolTip();

		setBackground(tool.getBackground());

		lstViews = new UINavList(new DefaultListModel());
		lstViews.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		lstViews.setCellRenderer(new ViewListCellRenderer());
		lstViews.setBackground(tool.getBackground());
		lstViews.setBorder(null);
		
		Font font = tool.getFont();
		int scale = ProjectCompendium.APP.getToolBarManager().getTextZoom();
		Font newFont = new Font(font.getName(), font.getStyle(), font.getSize()+ scale);
		lstViews.setFont(newFont);
		
		JScrollPane sp = new JScrollPane(lstViews);
		sp.setBorder(null);

		frame = ProjectCompendium.APP.getCurrentFrame();
		currentView = frame.getView();
		int count = 0;
		JLabel label = null;

		try {
			views = node.getMultipleViews();
			views = CoreUtilities.sortList(views);
			IModel model = ProjectCompendium.APP.getModel();
			PCSession session = model.getSession();

			htUserViews = ProjectCompendium.APP.getModel().getUserViews();
			oNode = node;
			count = views.size();
			String sViewID = "";
			for(int i=0; i < count; i++) {

				final View view = (View)views.elementAt(i);
				sViewID = view.getId();
				view.initialize(session, model);

				String text = view.getLabel();
				text = text.trim();

				if (text.equals("")) {
					text = "-- Unlabelled View --";
				}

				String htmlText = text;
				if (htUserViews.containsKey(sViewID)) {
					if (sViewID.equals(ProjectCompendium.APP.getInBoxID())) {
						htmlText = "<html><u>"+text+" - "+htUserViews.get(sViewID)+"</u></html>";
					} else {
						htmlText = text + " - " + htUserViews.get(sViewID);
					}
				}
				else {
					if (!currentView.getId().equals(sViewID)) {
						htmlText = "<html><u>"+text+"</u></html>";
					}
				}

				label = new JLabel(htmlText);
				label.setBackground(tool.getBackground());
				label.setForeground(tool.getForeground());
				label.setFont(newFont);
				((DefaultListModel)lstViews.getModel()).addElement(label);
			}
		}
		catch(Exception ex) {
			System.out.println("Error: (UIHintNodeViewsPanel) "+ex.getMessage());
		}

		if (count <= 20) {
			lstViews.setVisibleRowCount(count);
		}
		else {
			lstViews.setVisibleRowCount(20);
		}

		lstViews.addMouseListener(createMouseListener());
		add(sp);
		setSize(getPreferredSize());
        validate();
	}

	/**
	 * The mouse listener for the view selection and exiting behaviours.
	 * @return a mouse listener.
	 */
	protected MouseListener createMouseListener() {
		MouseListener mouse = new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				int index = lstViews.locationToIndex( e.getPoint());
				String id = "";
				View view = (View)views.elementAt(index);
				id = view.getId();
				if ( !id.equals(currentView.getId())) {
					if (!htUserViews.containsKey(view.getId()) 							
						|| id.equals(ProjectCompendium.APP.getInBoxID())) {
						UIViewFrame oUIViewFrame = ProjectCompendium.APP.addViewToDesktop(view, view.getLabel());						
						Vector history = new Vector();
						history.addElement(new String(sTitle));
						oUIViewFrame.setNavigationHistory(history);
						UIUtilities.focusNodeAndScroll(oNode, oUIViewFrame);
						
						if (frame instanceof UIMapViewFrame) {
							UIViewPane pane = ((UIMapViewFrame)frame).getViewPane();
							pane.hideViews();
						} else {
							UIList list = ((UIListViewFrame)frame).getUIList();
							list.hideHint();
						}						
					}
				}
			}
			
			public void mouseExited(MouseEvent evt) {
				if (frame instanceof UIMapViewFrame) {
					UIViewPane pane = ((UIMapViewFrame)frame).getViewPane();
					pane.hideViews();
				} else {
					UIList list = ((UIListViewFrame)frame).getUIList();
					list.hideHint();
				}
			}			
		};
		return mouse;
	}
	
	/**
	 * Helper class to render the element in the views list.
	 */
	public class ViewListCellRenderer implements ListCellRenderer {

	  	protected Border noFocusBorder;

		/*
		 * Constructors
		 */
		public ViewListCellRenderer() {
			noFocusBorder = new EmptyBorder(1, 1, 1, 1);
	  	}

		public Component getListCellRendererComponent(JList list,
													Object value,            // value to display
													int index,               // cell index
													boolean isSelected,      // is the cell selected
													boolean cellHasFocus ) { // the list and the cell have the focus

			JLabel lbl = (JLabel)value;

			JToolTip tool = new JToolTip();
			lbl.setOpaque(true);
			lbl.setBorder(null);

			return lbl;
		}
	}
}
