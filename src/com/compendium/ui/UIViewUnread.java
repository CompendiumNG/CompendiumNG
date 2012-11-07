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


package com.compendium.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.border.AbstractBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import com.compendium.LanguageProperties;
import com.compendium.ProjectCompendium;
import com.compendium.core.CoreUtilities;
import com.compendium.core.ICoreConstants;
import com.compendium.core.datamodel.IModel;
import com.compendium.core.datamodel.ModelSessionException;
import com.compendium.core.datamodel.NodePosition;
import com.compendium.core.datamodel.NodeSummary;
import com.compendium.core.datamodel.PCSession;
import com.compendium.core.datamodel.View;
import com.compendium.ui.popups.UIViewUnreadPopupMenu;


/**
 * This class is used to display unread view
 * @author Lakshmi Prabhakaran
 *
 */

public class UIViewUnread extends JPanel implements IUIConstants , TreeSelectionListener , PropertyChangeListener {
	
	/** The serial version id	 */
	private static final long serialVersionUID 					= 4469006144090220368L;

	/** The colour to use for selected text in the node label.*/
	private static final Color 	SELECTED_TEXT_COLOR 			= Color.black;

	/** The name of the project in the outline view.*/
	private String 				sProject 						= ""; //$NON-NLS-1$
	
	/** The cache model for the currently open database.*/
	private IModel				oModel							= ProjectCompendium.APP.getModel();

	/** The session for this model.*/
	private PCSession 			oSession 						= null;
		
	/** The user Id of the current user */
	private String 				userID 							= ""; //$NON-NLS-1$
	
	/** Has this panel been drawn yet?*/
	private boolean 			drawn 							= false;
	
	/** The JTree to display unread view */
	private JTree 				tree 							= null;
	
	/** The root Node of the tree */
	protected DefaultMutableTreeNode	rootNode				= null;
	
	/** Hashtable containing vector of unread nodes against the view */
	private Hashtable 			htViewsNodes 					= new Hashtable();
	
	/** Vector of the unread view nodes */
	private Vector 				vtUnreadViews					= new Vector();
	
	/** Vector of the view nodes */
	private Vector 				vtViews							= new Vector();
	
	/** node id against a list of tree nodes  */
	private  Hashtable 			htTreeNodes   					= new Hashtable();
	
	/** Node summary against their views */
	private  Hashtable 			htNodeAndViews   				= new Hashtable();
		
	/** Currently selected node */
	private UIViewOutlineTreeNode selectedNode   				= null;
	
	/** Total unread nodes */
	private int 					nUnread						= 0;
	
	/** Label to display number of unread nodes */
	private JLabel 					lblUnread					= new JLabel();
	
	/** 
	
	/**
	 * Constructor. 
	 * @param sProject, The name of the database in the unread view.
	 */
	public UIViewUnread(String sProject) {
		this.sProject 	= sProject;
		this.userID 	= oModel.getUserProfile().getId();
		oSession 		= oModel.getSession();
		
	}
	
	/**
	 * Draws the contents of this panel.
	 */
	public void draw() {
		
		ProjectCompendium.APP.setWaitCursor();
		setLayout(new BorderLayout());
		
		UIViewOutlineTreeNode node = new UIViewOutlineTreeNode(sProject, -1);
		rootNode = new DefaultMutableTreeNode(node);
		
		DefaultTreeModel treeModel = new DefaultTreeModel(rootNode);
		tree = new JTree(treeModel);
		tree.setFont(ProjectCompendiumFrame.currentDefaultFont);
		
		createTree();
		// Create a tree that allows one selection at a time.	
		tree.getSelectionModel().setSelectionMode
          						(TreeSelectionModel.SINGLE_TREE_SELECTION);
		tree.setEditable(false);
		tree.setShowsRootHandles(true);
		tree.setToggleClickCount(4);
        
		// Set the icon for nodes.
		TreeNodeRenderer renderer = new TreeNodeRenderer();
        tree.setCellRenderer(renderer);
       
        // Enable tool tips.
        ToolTipManager.sharedInstance().registerComponent(tree);
       
         
       // Listen for the changes.
        tree.addTreeSelectionListener(this);
        tree.addFocusListener(new FocusAdapter(){
			public void focusGained(FocusEvent arg0) {
				TreePath path = tree.getSelectionPath();
				if(path != null){
					DefaultMutableTreeNode treeNode = (((DefaultMutableTreeNode) path.getLastPathComponent()));
					UIViewOutlineTreeNode node =(UIViewOutlineTreeNode)treeNode.getUserObject();
					if( treeNode != rootNode){
						setStatus(node.getObject());
					}
				}else{
					ProjectCompendium.APP.setStatus(""); //$NON-NLS-1$
				}
			}
			public void focusLost(FocusEvent arg0) {
				ProjectCompendium.APP.setStatus(""); //$NON-NLS-1$
			}
		}); 
		
		 tree.addKeyListener(new KeyAdapter(){
			public void keyPressed(KeyEvent e){
		 		if(tree.isFocusOwner() && selectedNode != null){
		 			if (selectedNode != null && selectedNode != rootNode.getUserObject()){
		 				if((e.getKeyCode() == KeyEvent.VK_F12 && e.getModifiers() == 0)){
			 				onMarkSeenUnseen(selectedNode.getObject(), ICoreConstants.READSTATE);
							e.consume();
						} else if((e.getModifiers() == Event.SHIFT_MASK && e.getKeyCode() == KeyEvent.VK_F12)){ 
			 				onMarkSeenUnseen(selectedNode.getObject(), ICoreConstants.UNREADSTATE);
			 				e.consume();
						}
		 			}
		 		}
			} 
		}); 	
		
		tree.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent evt) {
				TreePath selPath = tree.getPathForLocation(evt.getX(), evt.getY());
				UIViewOutlineTreeNode  parentNode = null;
				UIViewOutlineTreeNode  childNode = null;
				DefaultMutableTreeNode  treeNode = null;
				
    			boolean isRightMouse = SwingUtilities.isRightMouseButton(evt);
    			boolean isLeftMouse = SwingUtilities.isLeftMouseButton(evt);
    			if (ProjectCompendium.isMac &&
    					(evt.getButton() == 3 && evt.isShiftDown()) ||
    					(evt.getButton() == 1 && evt.isAltDown())) {

    				isRightMouse = true;
    				isLeftMouse = false;
    			}
 				
				if(selPath != null ) {
					treeNode = (((DefaultMutableTreeNode) selPath.getLastPathComponent()));
					childNode =(UIViewOutlineTreeNode)treeNode.getUserObject();
					
					if(!treeNode.equals(rootNode)){
						
						if(tree.isFocusOwner() && isLeftMouse){
        				
        					NodeSummary node = childNode.getObject();
        	
        					//setStatus
        					setStatus(node);
	    					if (evt.getClickCount() == 2){
		    					// Open the view if it is a Map/ List other wise open the parent view.
		        		  		if( View.isViewType(childNode.getType())) {
		        					parentNode = childNode;
		        				} else {
		        					DefaultMutableTreeNode parent = null;
		        					Object[] nodes = (Object[]) selPath.getPath();
		            					
		        					// parent view is the node at level 1
		        					parent = (DefaultMutableTreeNode)nodes[1];
		        					parentNode = (UIViewOutlineTreeNode) parent.getUserObject();
		        					
		        				}
		    					View parentView = View.getView(parentNode.getId());
		    					NodeSummary child = (NodeSummary)childNode.getObject();
		    					openView(parentView, child);
		    				}
	    				
		    			} else if(tree.isFocusOwner() && isRightMouse){
		    				tree.setSelectionPath(selPath);
		    				NodeSummary node = (NodeSummary)childNode.getObject();
		    				if ((node.getId()).equals(ProjectCompendium.APP.getInBoxID())){
		    					return ;
		    				}
		    				
		    				UIViewUnreadPopupMenu popup = new UIViewUnreadPopupMenu (LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "UIViewUnread.popupMenuTitle"), node, UIViewUnread.this); //$NON-NLS-1$
		    				popup.show(tree, evt.getX(),evt.getY());
		    			}
	   				}
    				tree.requestFocus();
    				evt.consume();
    			} 
        	}
        }); 

		 //keep all nodes expanded
		tree.expandPath(tree.getPathForRow(0));
		int count = rootNode.getChildCount();
		for(int i = count; i >= 1  ; i--){
			if(tree.getPathForRow(i) != null && (!rootNode.getChildAt(i-1).isLeaf())){
				tree.expandPath(tree.getPathForRow(i));
			}
		}
        Dimension size = tree.getPreferredSize();

        JScrollPane oScrollPane = new JScrollPane(tree);
		oScrollPane.setBackground(Color.white);
		
		oScrollPane.setPreferredSize(new Dimension(300, size.height));
		add(oScrollPane, BorderLayout.CENTER);

		JPanel panel = new JPanel();
		GridBagLayout grid = new GridBagLayout();
		panel.setLayout(grid);
		
		GridBagConstraints gc = new GridBagConstraints();
		gc.insets = new Insets(5,5,5,5);
		gc.anchor = GridBagConstraints.WEST;
		
		int y = 0;
		
		JLabel label = new JLabel(LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "UIViewUnread.totalUnread")+"=");  //$NON-NLS-1$ //$NON-NLS-2$
		label.setBounds(10,10,10,10);
		gc.gridy = y;
		gc.gridx = 0;
		grid.setConstraints(label, gc);
		panel.add(label);
		
		gc.gridx = 1;
		grid.setConstraints(lblUnread, gc);
		panel.add(lblUnread);
		lblUnread.setText(String.valueOf(nUnread));
		
		gc.gridx 		= 2;
		gc.gridheight	= 2;
		gc.weightx		= 1;
		gc.anchor 		= GridBagConstraints.EAST;
		JPanel oButtonPanel = createButtonPanel();
		grid.setConstraints(oButtonPanel, gc);
		
		panel.add(oButtonPanel);
		
		add(panel, BorderLayout.SOUTH);
		
		drawn = true;
		ProjectCompendium.APP.setDefaultCursor();
	}
	
	/**
	 * Create and return the button panel.
	 */
	private JPanel createButtonPanel() {

		JPanel oButtonPanel = new JPanel();

		UIButton pbRefresh = new UIButton(LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "UIViewUnread.refreshButton")); //$NON-NLS-1$
		pbRefresh.setMnemonic(LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "UIViewUnread.refreshButtonMnemonic").charAt(0)); //$NON-NLS-1$
		pbRefresh.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ProjectCompendium.APP.setWaitCursor();
				refresh();
				ProjectCompendium.APP.setDefaultCursor();
			}
		});
		oButtonPanel.add(pbRefresh);
		
		UIButton pbCancel = new UIButton(LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "UIViewUnread.closeButton")); //$NON-NLS-1$
		pbCancel.setMnemonic(LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "UIViewUnread.closeButtonMnemonic").charAt(0)); //$NON-NLS-1$
		pbCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ProjectCompendium.APP.getMenuManager().removeUnreadView(true);
			}
		});
		oButtonPanel.add(pbCancel);

		return oButtonPanel;
	}
	
	/**
	 * Return the font size to its default and then appliy the passed text zoom.
	 * (To the default specificed by the user in the Project Options)
	 */
	public void onReturnTextAndZoom(int zoom) {
		Font font = ProjectCompendiumFrame.currentDefaultFont;
		Font newFont = new Font(font.getName(), font.getStyle(), font.getSize()+zoom);			
		tree.setFont(newFont);
		FontMetrics metrics = tree.getFontMetrics(newFont);
		tree.setRowHeight(metrics.getHeight());								
	}

	/**
	 * Return the font size to its default 
	 * (To the default specificed by the user in the Project Options)
	 */
	public void onReturnTextToActual() {
		tree.setFont(ProjectCompendiumFrame.currentDefaultFont);
		FontMetrics metrics = tree.getFontMetrics(ProjectCompendiumFrame.currentDefaultFont);
		tree.setRowHeight(metrics.getHeight());						
	}
	
	/**
	 * Increase the currently dislayed font size by one point.
	 */
	public void onIncreaseTextSize() {
		Font font = tree.getFont();
		Font newFont = new Font(font.getName(), font.getStyle(), font.getSize()+1);			
		tree.setFont(newFont);
		FontMetrics metrics = tree.getFontMetrics(newFont);
		tree.setRowHeight(metrics.getHeight());								
	}
	
	/**
	 * Reduce the currently dislayed font size by one point.
	 */
	public void onReduceTextSize() {
		Font font = tree.getFont();
		Font newFont = new Font(font.getName(), font.getStyle(), font.getSize()-1);			
		tree.setFont(newFont);
		FontMetrics metrics = tree.getFontMetrics(newFont);
		tree.setRowHeight(metrics.getHeight());								
	}	
	
	/**
	 * returns true if the panel is drawn.
	 * @return Returns the drawn.
	 */
	public boolean isDrawn() {
		return drawn;
	}
	
	/**
	 * adds nodes to the tree depending on the option selected
	 *
	 */
	public void createTree(){
		
		rootNode.removeAllChildren();
		nUnread = 0;
		
		try {
			View homeView = oModel.getUserProfile().getHomeView();
			homeView.initialize(oSession, oModel);
			vtViews.add(homeView);
			getAllNodes(homeView);
			
			
		} catch (SQLException e) {
			e.printStackTrace();
		} 
		
		for(int i = 0; i< vtViews.size(); i++){
			View view = (View) vtViews.get(i);
			view.initialize(oSession, oModel);
			if(htViewsNodes.containsKey(view)){
				UIViewOutlineTreeNode treeNode = new UIViewOutlineTreeNode(view);
				DefaultMutableTreeNode oTreeNode = new DefaultMutableTreeNode(treeNode);
				
	        	// add to listeners
				if(!view.getListenerList().contains(this)){
					view.addPropertyChangeListener(this);
				}
				
				if(view.getState() == ICoreConstants.UNREADSTATE)
					nUnread ++ ;
				
				Vector nodes = new Vector();
				nodes.add(oTreeNode);
				htTreeNodes.put(view.getId(), nodes);
				
				UIArrangeLeftRight arrange = new UIArrangeLeftRight();
				if (view != null)
					arrange.processView(view);
				
				Vector vtNodes = (Vector) htViewsNodes.get(view); 
				for(int j = 0;j < vtNodes.size(); j++){
					NodeSummary node = (NodeSummary) vtNodes.get(j);
					node.initialize(oSession, oModel);
					UIViewOutlineTreeNode childNode = new UIViewOutlineTreeNode(node);
					DefaultMutableTreeNode childTreeNode = new DefaultMutableTreeNode(childNode);
					
					
		        	// add to listeners
					if(!node.getListenerList().contains(this)){
						node.addPropertyChangeListener(this);
					}
					if(node.getState() == ICoreConstants.UNREADSTATE){
						nUnread ++ ;
						oTreeNode.add(childTreeNode);
					}
					if(htTreeNodes.containsKey(node.getId())){
						Vector vtTreeNodes = (Vector) htTreeNodes.get(node.getId());
						if(!vtTreeNodes.contains(childTreeNode))
							vtTreeNodes.add(childTreeNode);
					} else {
						Vector vtTreeNodes = new Vector();
						vtTreeNodes.add(childTreeNode);
						htTreeNodes.put(node.getId(), vtTreeNodes);
					}
				}
				if((view.getState() == ICoreConstants.UNREADSTATE) || oTreeNode.getChildCount() > 0 ){
					if(view.equals(ProjectCompendium.APP.getInBoxView())){
						rootNode.insert(oTreeNode, 0);
					} else {
						rootNode.add(oTreeNode);
					}
				}
			}
		}
		lblUnread.setText(String.valueOf(nUnread));
	}
	
	/**
	 * 
	 * @param view
	 * @return
	 * @throws SQLException
	 */
 	public Hashtable getAllNodes(NodeSummary view) throws SQLException{
		
		String viewID = view.getId();
		
		Vector viewNodes = new Vector();
		Enumeration nodes = oModel.getNodeService().getChildNodes(oSession, viewID);
		 int count = 0;
		 for(Enumeration e = nodes; e.hasMoreElements();){
			NodeSummary nsum = (NodeSummary)e.nextElement();
			if(View.isViewType(nsum.getType())) {
				if(!vtViews.contains(nsum)){
					vtViews.add(nsum);
					viewNodes.add(nsum);
				}
				if(!htViewsNodes.containsKey(nsum)){
					htViewsNodes.put(nsum, new Vector());
				}
			} else {
				//store all views against the nodes. It is used when adding a node in the unread view due to state change
				if(!htNodeAndViews.containsKey(nsum)){
					Vector vtNodes = new Vector();
					vtNodes.add(view);
					htNodeAndViews.put(nsum, vtNodes);
				} else {
					Vector vtNodes = (Vector) htNodeAndViews.get(nsum);
					if(!vtNodes.contains(view))
						vtNodes.add(view);
				}//end if
				
				//it is used for populating the tree
				if(!htViewsNodes.containsKey(view)){
					Vector vtNodes = new Vector();
					vtNodes.add(nsum);
					htViewsNodes.put(view, vtNodes);
				} else {
					Vector vtNodes = (Vector) htViewsNodes.get(view);
					vtNodes.add(nsum);
				}//end if
			}// end else
		 } //end for
		 
		 for(int i = 0; i < viewNodes.size(); i++){
			 NodeSummary childView = (View) viewNodes.get(i);
			 getAllNodes(childView);
		 }
		return htViewsNodes;
	}
	
	/**
	 * Opens the given view and highlights the given node 
	 * @param viewSum, View to be opened
	 * @param ns, NodeSummary of the node to be highlighted
	 */
	
	public void openView(View viewSum, NodeSummary  ns) {
		 try {
			View view = viewSum;
			view.initialize(oSession, oModel);
			view.initializeMembers();
			UIViewFrame viewFrame = ProjectCompendium.APP.addViewToDesktop(view, view.getLabel());
			viewFrame.setNavigationHistory(viewFrame.getChildNavigationHistory());
			
			if(!viewSum.equals(ns)){
				
				if(viewFrame instanceof UIMapViewFrame) {
					UIMapViewFrame mapViewFrame = (UIMapViewFrame) viewFrame;
					UIViewPane viewPane = mapViewFrame.getViewPane();
					//get the uinode components in the viewpane layer
					Component nodearray[] = viewPane.getComponentsInLayer((UIViewPane.NODE_LAYER).intValue());
					int i=0; UINode uinode = null;
					while(i < nodearray.length) {
						uinode = (UINode)nodearray[i++];
						if(uinode.getNode().equals(ns)){
							
							viewPane.setSelectedNode(uinode, ICoreConstants.SINGLESELECT);
							if(uinode.isFocusOwner() == false) {
								uinode.setFocusable(true);
							}
							if(uinode.isSelected() == false)
								uinode.setSelected(true);
							
							
							JViewport port = mapViewFrame.getViewport();
							Point nodePos = uinode.getNodePosition().getPos();

							Dimension dim = port.getExtentSize();
							int portX = (int) ((dim.width/2) - uinode.getWidth());
							int portY = (int) ((dim.height/2) - uinode.getHeight());

							Point parentPos = SwingUtilities.convertPoint((Component)viewPane, nodePos.x, nodePos.y, port);

							int hAdjust = parentPos.x - portX;
							int vAdjust = parentPos.y - portY;

							int currentV = mapViewFrame.getVerticalScrollBarPosition();
							int currentH = mapViewFrame.getHorizontalScrollBarPosition();
							mapViewFrame.setVerticalScrollBarPosition(currentV + vAdjust, false);
							mapViewFrame.setHorizontalScrollBarPosition(currentH + hAdjust, false);

							viewPane.setZoom(1.0);
							viewPane.scale();
							break;
						}
					}
					
				} else if(viewFrame instanceof UIListViewFrame){
					UIListViewFrame listViewFrame = (UIListViewFrame) viewFrame;
					int rowIndex = listViewFrame.getUIList().getIndexOf(ns);
					
					listViewFrame.getUIList().selectNode(rowIndex, ICoreConstants.SINGLESELECT);
					
					JTable table = listViewFrame.list;
					JScrollPane scrollPane = listViewFrame.getScrollPane();
					JViewport port = listViewFrame.getViewport();
					
					Dimension dim = scrollPane.getSize();
					
					int x = 0 ;
					int y = (rowIndex * table.getRowHeight())-(dim.height/2);
					
					if (y < 0){
						y = 0;
					}
					Point nodePos = new Point (0, y );
					port.setViewPosition(nodePos);
				}
			}
			
			Vector history = new Vector();
			history.addElement(view.getLabel());
			viewFrame.setNavigationHistory(history);
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ModelSessionException e) {
			e.printStackTrace();
		} 
		
	}
	/**
	 * Displays the author, creation date and detail in the status bar 
	 * @param oNode NodeSummary of the node 
	 */
	private void setStatus(NodeSummary oNode){
		String sStatus = ""; //$NON-NLS-1$
		String author = oNode.getAuthor();
		String creationDate = (UIUtilities.getSimpleDateFormat("dd, MMMM, yyyy h:mm a").format(oNode.getCreationDate()).toString()); //$NON-NLS-1$
		
		
		String showtext = author + " " + creationDate +", " + //$NON-NLS-1$ //$NON-NLS-2$
						 oNode.getDetail();

		if (showtext != null) {
			showtext = showtext.replace('\n',' ');
			showtext = showtext.replace('\r',' ');
			showtext = showtext.replace('\t',' ');
			sStatus = showtext;
		}

		ProjectCompendium.APP.setStatus(sStatus);
	}
	
	/**
	 * Returns to the list of nodes associated with the given id
	 * @return Vector, list of nodes 
	 */
	
	public Vector getTreeNode(String id) {
		Vector v = new Vector();
		if(htTreeNodes.containsKey(id)){
			v = (Vector) htTreeNodes.get(id) ;
		}
		return v;
	}
	/**
	 * Marks seen/unseen for the given node
	 * @param node com.compendium.code.datamodel.NodeSummary, the node associated with this menu.
	 * @param state, the state to set
	 */
	public void onMarkSeenUnseen(NodeSummary node, int state){
		try {
			String nodeID = node.getId();
			String homeID = ProjectCompendium.APP.getHomeView().getId();
			String inboxID = ProjectCompendium.APP.getInBoxID();
			if(!(nodeID.equals(homeID) || nodeID.equals(inboxID))) {
				node.setState(state);
			}
			
		} catch(Exception io) {
			if(state == ICoreConstants.READSTATE)
				System.out.println(LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "UIViewUnread.unableToMarkSeen")); //$NON-NLS-1$
			else 
				System.out.println(LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "UIViewUnread.unableToMarkUnseen")); //$NON-NLS-1$
		}
		
		
	}
	
	/**
	 * Marks seen/unseen for whole view
	 * @param view com.compendium.code.datamodel.View, the view associated with this menu.
	 * @param state, the state to set
	 */
	public void onMarkAll(NodeSummary view, int state){
		try {
			String viewID = view.getId();
			String homeID = ProjectCompendium.APP.getHomeView().getId();
			String inboxID = ProjectCompendium.APP.getInBoxID();
			if(!(viewID.equals(homeID) || viewID.equals(inboxID))) {
				view.setState(state);
			}
			Enumeration nodes = ProjectCompendium.APP.getModel().getNodeService().getChildNodes(
					ProjectCompendium.APP.getModel().getSession(), view.getId());

			for(Enumeration e = nodes;e.hasMoreElements();) {

				NodeSummary  nodeSummary = (NodeSummary)e.nextElement();
				String nodeID = nodeSummary.getId();
				if(!(nodeID.equals(homeID) || nodeID.equals(inboxID))) {
					nodeSummary.setState(state);
				}
			}
		} catch(Exception io) {
			if(state == ICoreConstants.READSTATE)
				System.out.println(LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "UIViewUnread.unableToMarkSeen")); //$NON-NLS-1$
			else 
				System.out.println(LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "UIViewUnread.unableToMarkUnseen")); //$NON-NLS-1$
		}
	}
	
	/**
	 * Repaints the view frames.
	 * @param views , The views to be refreshed
	 */
	
	private void reopenViews(Vector views) {
		
		for(int i = 0; i < views.size(); i++){
			View view = (View)views.get(i);
			UIViewFrame internalFrame = ProjectCompendium.APP.getInternalFrame(view);
	    	if(internalFrame != null){
	    		ProjectCompendium.APP.getAllFrames().remove(internalFrame);
	    		internalFrame.dispose();
	    		
	    		internalFrame = ProjectCompendium.APP.addViewToDesktop(view, view.getLabel());
	    		try {
					internalFrame.setClosed(false);
				} catch (PropertyVetoException e) {
					e.printStackTrace();
				}
	    		if(view.equals(ProjectCompendium.APP.getHomeView())){
	    			String label = "  " +oModel.getUserProfile().getUserName() + "\'s " + view.getLabel(); //$NON-NLS-1$ //$NON-NLS-2$
	    			internalFrame.setTitle(label);
	    			internalFrame.setClosable(false);
	    		} 
	    		// internalFrame.setNavigationHistory(internalFrame.getChildNavigationHistory());
	    	}
		}
	}//end of reopenViews
	
	
	/**
	 * Refreshes the unread view
	 *
	 */
	
	public void refresh(){
		
		int nCount = rootNode.getChildCount();
		
		for(int i = nCount-1; i >= 0; i--) {
			DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) rootNode.getChildAt(i);
			int nChild = treeNode.getChildCount();
			for(int j = nChild-1; j >= 0 ; j--){
				DefaultMutableTreeNode child = (DefaultMutableTreeNode) treeNode.getChildAt(j);
				UIViewOutlineTreeNode  childNode = (UIViewOutlineTreeNode) child.getUserObject();
				if(childNode.getState() == ICoreConstants.READSTATE ||
						childNode.getState() == ICoreConstants.MODIFIEDSTATE){
					child.removeFromParent();
				} 
			}
			if(treeNode.getChildCount() <= 0){
				UIViewOutlineTreeNode  node = (UIViewOutlineTreeNode) treeNode.getUserObject();
				if(node.getState() == ICoreConstants.READSTATE ||
						node.getState() == ICoreConstants.MODIFIEDSTATE){
					treeNode.removeFromParent();
				}	
			}
		}
/*		
		for (Enumeration e = htTreeNodes.keys(); e.hasMoreElements();){
			NodeSummary node = NodeSummary.getNodeSummary((String)e.nextElement());
			node.removePropertyChangeListener(this);
		}
		
		rootNode.removeAllChildren();
		htViewsNodes.clear();
		htTreeNodes.clear();
		vtViews.clear();
		vtUnreadViews.clear();
		
		createTree();
*/		
		((DefaultTreeModel)tree.getModel()).reload(rootNode);
		tree.repaint();
		
		//keep all nodes expanded
		tree.expandPath(tree.getPathForRow(0));
		int count = rootNode.getChildCount();
		for(int i = count; i >= 1  ; i--){
			if(tree.getPathForRow(i) != null && (!rootNode.getChildAt(i-1).isLeaf())){
				tree.expandPath(tree.getPathForRow(i));
			}
		}
		
	}//end of refresh
	
	/**
	 * To remove the given node from the given View in the unread view
	 * @param view the View in which node is present
	 * @param ns the nodesummary of the node
	 */
	public void removeNode(View view, NodeSummary ns){
		
		if(ns instanceof View){
			Vector vtNodes = (Vector) getTreeNode(ns.getId());
			for(int i = 0; i < vtNodes.size(); i++) {
				DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) vtNodes.get(i);
				if(treeNode.getParent() != null) {
					if (!treeNode.isLeaf()){
						int count = treeNode.getChildCount();
						for(int j = 0; j < count; j++){
							DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) treeNode.getChildAt(j);
							UIViewOutlineTreeNode  child = (UIViewOutlineTreeNode) childNode.getUserObject();
							NodeSummary nodeSum = child.getObject();
							treeNode.remove(childNode);
								Vector childNodes = (Vector) getTreeNode(ns.getId());
								childNodes.remove(childNode);
								Vector v =((Vector)htNodeAndViews.get(ns));
								v.remove(view);
								((DefaultTreeModel)tree.getModel()).reload(childNode);
						}
					} else {
						rootNode.remove(treeNode);
						Vector childNodes = (Vector) getTreeNode(ns.getId());
						childNodes.remove(treeNode);
						Vector v =((Vector)htNodeAndViews.get(ns));
						v.remove(view);
					}
				}
			}
		} else {
			Vector vtNodes = (Vector) getTreeNode(view.getId());
			for(int i = 0; i < vtNodes.size(); i++) {
				DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) vtNodes.get(i);
				UIViewOutlineTreeNode node = (UIViewOutlineTreeNode) treeNode.getUserObject();
				
				if (!treeNode.isLeaf()){
					int count = treeNode.getChildCount();
					for(int j = 0; j < count; j++){
						DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) treeNode.getChildAt(j);
						UIViewOutlineTreeNode  child = (UIViewOutlineTreeNode) childNode.getUserObject();
						NodeSummary nodeSum = child.getObject();
						if(ns.equals(nodeSum)){
							treeNode.remove(childNode);
							Vector childNodes = (Vector) getTreeNode(ns.getId());
							childNodes.remove(childNode);
							Vector v =((Vector)htNodeAndViews.get(ns));
							v.remove(view);
							((DefaultTreeModel)tree.getModel()).reload(childNode);
						}
					}
					if (treeNode.getChildCount() == 0  && view.getState() != ICoreConstants.UNREADSTATE){
						rootNode.remove(treeNode);
						((DefaultTreeModel)tree.getModel()).reload(rootNode);
					}
				}
			}
		}
		if(ns.getState() == ICoreConstants.UNREADSTATE){
			nUnread --;
			lblUnread.setText(String.valueOf(nUnread));
		}
	}
	
	public void addNode(DefaultMutableTreeNode treeNode, NodeSummary ns, View oView){
		if(ns instanceof View){
			int index = rootNode.getIndex(treeNode);
			if(index == -1){
				if(ns.equals(ProjectCompendium.APP.getInBoxView())){
					rootNode.insert(treeNode, 0);
				} else {
					rootNode.add(treeNode);
				}
				((DefaultTreeModel)tree.getModel()).reload(rootNode);
				if(!treeNode.isLeaf())
					tree.expandPath(new TreePath(treeNode.getPath()));
			}
		} else {
			
			Vector viewNodes = getTreeNode(oView.getId());
			for(int j =0; j < viewNodes.size(); j ++){
				DefaultMutableTreeNode viewNode = (DefaultMutableTreeNode) viewNodes.get(j);
				int index = rootNode.getIndex(viewNode);
				if(index == -1){
					if(oView.equals(ProjectCompendium.APP.getInBoxView())){
						rootNode.insert(viewNode, 0);
					} else {
						rootNode.add(viewNode);
					}
					((DefaultTreeModel)tree.getModel()).reload(rootNode);
				}
				viewNode.add(treeNode);
				((DefaultTreeModel)tree.getModel()).reload(viewNode);
				tree.expandPath(new TreePath(viewNode.getPath()));
			}
			
		}
	}
	
	/**
	 * Called whenever the value of the selection changes to update status
	 */
	public void valueChanged(TreeSelectionEvent arg0) {
		TreePath path = arg0.getPath();
		DefaultMutableTreeNode node = (DefaultMutableTreeNode)path.getLastPathComponent();
		selectedNode = ((UIViewOutlineTreeNode) node.getUserObject());
		
		UIViewOutlineTreeNode treeNode = (UIViewOutlineTreeNode)(node.getUserObject());
		if(treeNode.getObject() != null )	
			setStatus(treeNode.getObject());
    	else 
	       	ProjectCompendium.APP.setStatus(""); //$NON-NLS-1$
	  	
	}
	
	/**
	 * Handles property change events.
	 * @param evt, the associated PropertyChangeEvent object.
	 */
	public void propertyChange(PropertyChangeEvent evt) {
		Object obj = evt.getSource();
		String prop = evt.getPropertyName();
		Object oldvalue = evt.getOldValue();
		Object newvalue = evt.getNewValue();
		
		// get the expanded tree descendants.
		Enumeration e = tree.getExpandedDescendants(new TreePath(rootNode.getPath()));
		
		if(prop.equals(NodeSummary.IMAGE_PROPERTY)){
			NodeSummary ns = (NodeSummary) obj;
			
			Vector nodes = getTreeNode(ns.getId());
			for(int i =0; i < nodes.size(); i++){
				DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) nodes.get(i);
				UIViewOutlineTreeNode node = (UIViewOutlineTreeNode) treeNode.getUserObject();
				node.setReference(ns.getSource());
			}
		} 
		else if(prop.equals(NodeSummary.STATE_PROPERTY)){
			NodeSummary ns = (NodeSummary) obj;
			int newState = ((Integer) newvalue).intValue();
			int oldState = ((Integer) oldvalue).intValue();
						
			Vector nodes = getTreeNode(ns.getId());
			for(int i =0; i < nodes.size(); i++){
				DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) nodes.get(i);
				UIViewOutlineTreeNode node = (UIViewOutlineTreeNode) treeNode.getUserObject();
				if(!node.getObject().equals(ProjectCompendium.APP.getHomeView())){ //treeNode != rootNode.getFirstChild()
					node.setState(newState);
				}
				if(newState == ICoreConstants.UNREADSTATE){
					nUnread ++;
					lblUnread.setText(String.valueOf(nUnread));
				} else if (oldState == ICoreConstants.UNREADSTATE){
					nUnread --;
					lblUnread.setText(String.valueOf(nUnread));
				}
				if(ns instanceof View){
					if ((newState == ICoreConstants.UNREADSTATE) && (treeNode.getParent() == null)){
						addNode(treeNode, ns, null);
						((DefaultTreeModel)tree.getModel()).reload(treeNode);
					}
				} else {
					Vector vtNodes = (Vector)htNodeAndViews.get(ns);
					View view = (View) vtNodes.get(i);
					if(view != null){
						if ((newState == ICoreConstants.UNREADSTATE) && (treeNode.getParent() == null)){
							addNode(treeNode, ns, view);
							((DefaultTreeModel)tree.getModel()).reload(treeNode);
						}
					}
				}
			}
		} else if(prop.equals(View.NODE_REMOVED)){
			View oView = (View) obj;
			NodeSummary ns = (NodeSummary) newvalue;
			removeNode(oView, ns);
			
		} else if(prop.equals(NodeSummary.LABEL_PROPERTY)){
			NodeSummary ns = (NodeSummary) obj;
			String newLabel = newvalue.toString();
			Vector vtNodes = (Vector) getTreeNode(ns.getId());
			for(int i = 0; i < vtNodes.size(); i++){
				DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) vtNodes.get(i);
				UIViewOutlineTreeNode node = (UIViewOutlineTreeNode)treeNode.getUserObject();
				node.setLabel(newLabel);
				((DefaultTreeModel)tree.getModel()).reload(treeNode);
			
			}
		} else if((prop.equals(View.NODE_ADDED)) || (prop.equals(View.NODE_TRANSCLUDED))){
			NodePosition np = (NodePosition) newvalue;
			View oView = np.getView();
			NodeSummary ns = np.getNode();
			ns.initialize(oSession, oModel);
			
			UIArrangeLeftRight arrange = new UIArrangeLeftRight();
			if (oView != null)
				arrange.processView(oView);
			
			UIViewOutlineTreeNode node = new UIViewOutlineTreeNode(ns);
			DefaultMutableTreeNode treeNode = new DefaultMutableTreeNode(node);
			
			boolean isAlreadyAdded = true;
			//store all views against the nodes. It is used when adding a node in the unread view due to state change
			if(!htNodeAndViews.containsKey(ns)){
				Vector vtNodes = new Vector();
				vtNodes.add(oView);
				htNodeAndViews.put(ns, vtNodes);
			} else {
				Vector vtNodes = (Vector) htNodeAndViews.get(ns);
				if(!vtNodes.contains(oView)){
					vtNodes.add(oView);
					isAlreadyAdded = false;  //if the node is already for the view don't add to vtTreeNodes 
				}
			}//end if
			
			if(htTreeNodes.containsKey(ns.getId())){
				Vector vtTreeNodes = (Vector) htTreeNodes.get(ns.getId());
				if(!vtTreeNodes.contains(treeNode) && !isAlreadyAdded) {
					vtTreeNodes.add(treeNode);
					if(ns.getState() == ICoreConstants.UNREADSTATE && treeNode.getParent() == null){
						addNode(treeNode, ns, oView);
						((DefaultTreeModel)tree.getModel()).reload(treeNode);
						nUnread ++;
						lblUnread.setText(String.valueOf(nUnread));
					}
				}
			} else {
				Vector vtTreeNodes = new Vector();
				vtTreeNodes.add(treeNode);
				htTreeNodes.put(ns.getId(), vtTreeNodes);
				
				if(ns.getState() == ICoreConstants.UNREADSTATE && treeNode.getParent() == null){
					addNode(treeNode, ns, oView);
					((DefaultTreeModel)tree.getModel()).reload(treeNode);
				}
			}
			
			// add to listeners
			if(!ns.getListenerList().contains(this)){
				ns.addPropertyChangeListener(this);
			}
		} else if (prop.equals(NodeSummary.NODE_TYPE_PROPERTY)){
			NodeSummary ns = (NodeSummary) obj;
			int newType = Integer.parseInt(newvalue.toString());
			int oldType = Integer.parseInt(oldvalue.toString());
			
			Vector vtNodes = (Vector) getTreeNode(ns.getId());
			for(int i = 0; i < vtNodes.size(); i++){
				DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) vtNodes.get(i);
				UIViewOutlineTreeNode node = (UIViewOutlineTreeNode)treeNode.getUserObject();
				node.setType(newType);
			}
		}
		// expand all the expanded tree nodes
		if(e != null){
			for (;e.hasMoreElements();){
				TreePath path = (TreePath)e.nextElement();
				if(tree.getRowForPath(path) != -1)
					tree.expandPath(path);
			}
		}
		tree.repaint();
		
	}
	
	/**
	 * Create and show the right-click node popup menu for the given node.
	 * @param node com.compendium.core.datamodel.NodeSummary, the node to create the popup for.
	 * @param x, the x position of the mouse event that triggered this request.
	 * @param y, the y position of the mouse event that triggered this request.
	 */
	public UIViewUnreadPopupMenu showPopupMenu(NodeSummary node,  int x, int y) {
	
		UIViewUnreadPopupMenu popup = new UIViewUnreadPopupMenu ("Popup Menu", node, this); //$NON-NLS-1$
				
	    Point point = new Point (x, y);
	    
	    int realX = Math.abs(point.x - getX())+ 50;
	    int realY = Math.abs(point.y - getY()) + 20;
	    
	    if(realX > this.getWidth()){
	    	realX = this.getWidth() - 20; 
	    } 
	    
	    popup.show(tree, realX, realY);
	   
	    return popup;
	}
	
	/** 
	 * Remove all listeners before closing view
	 *
	 */
	public void cleanUp(){
		for(Enumeration e = htTreeNodes.elements(); e.hasMoreElements();){
			Vector nodes = (Vector) e.nextElement();
			for(int i =0; i< nodes.size(); i ++){
				DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) nodes.get(i);
				UIViewOutlineTreeNode  outlineNode = (UIViewOutlineTreeNode) treeNode.getUserObject();
				NodeSummary node = outlineNode.getObject();
				if(node.getListenerList().contains(this)){
					node.removePropertyChangeListener(this);
				}
			}
			
		}
	}
	
	/**
	 * The inner class that defines the requirements for an object that displays a tree node
	 * @author Lakshmi Prabhakaran
	 *
	 */
	private class TreeNodeRenderer extends DefaultTreeCellRenderer {
        
		private static final long serialVersionUID = 7969295074672629921L;
		
		Icon imageIcon ;
		UIViewOutlineTreeNode treeNode = null;
		
        /** Sets the value of the current tree cell to value. 
         *  
         *  @return the Component that the renderer uses to draw the value
         */
        public Component getTreeCellRendererComponent( JTree tree, Object value, boolean sel,
                            boolean expanded, boolean leaf, int row, boolean hasFocus) {

        	Component c =  super.getTreeCellRendererComponent(tree, value, sel,
                            expanded, leaf, row, hasFocus);
            
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
            
            treeNode = (UIViewOutlineTreeNode)(node.getUserObject());
	            
	        String  toolTip = treeNode.getLabel();
	        int type = treeNode.getType();
	        int state = treeNode.getState();
	     
	        //set the font
        	setFont(tree.getFont());
        	setForeground(SELECTED_TEXT_COLOR);
        	
        	if (type == ICoreConstants.REFERENCE) {
        		imageIcon = UINode.getReferenceImageSmall(treeNode.getReference());
		   } else if (type == -1) {
		    	imageIcon = getOpenIcon();			    				    	
		    } else {
		    	imageIcon = UINode.getNodeImageSmall(type);
        	}
            
        	if (treeNode.getObject() != null) {
        		if(treeNode.getObject().getId().equals(ProjectCompendium.APP.getInBoxID())) {
        			imageIcon = UIImages.get(IUIConstants.INBOX_SM);
        		}
        	}
        	setIcon(imageIcon);
            setToolTipText(toolTip);
            
            // set border color	
	        setBorder(new NodeBorder(state, imageIcon));
         return this;
	}
        
	public String getToolTipText(MouseEvent e){
	       if(treeNode.getObject() != null ){	
				// set status info
				setStatus(treeNode.getObject());
    		} else {
	        	ProjectCompendium.APP.setStatus(""); //$NON-NLS-1$
	        }
			return this.getToolTipText();
        }

	}
	
	/**
	 * This border class paints the border for this node.
	 */
	private class NodeBorder extends AbstractBorder {
		
		int state;
		Icon imageIcon;
		
		public NodeBorder(int state, Icon icon){
			this.state = state;
			imageIcon = icon;
		}

		public void paintBorder (Component c, Graphics g, int x, int y, int width, int height) {

			if (state == ICoreConstants.UNREADSTATE) {
				Color oldColor = g.getColor();
				g.setColor(UNREAD_BORDER_COLOR);
				int iconWidth = imageIcon.getIconWidth();
				g.draw3DRect(x + (iconWidth + 3) , y + 1, width - (iconWidth + 5), height-2, true);
				g.setColor(oldColor);
				
			}
			else if (state == ICoreConstants.MODIFIEDSTATE) {
				Color oldColor = g.getColor();
				g.setColor(MODIFIED_BORDER_COLOR);
				int iconWidth = imageIcon.getIconWidth();
				g.draw3DRect(x + (iconWidth +3) , y + 1, width - (iconWidth + 5), height-2, true);
				g.setColor(oldColor);
			} else {
				Color oldColor = g.getColor();
				g.setColor(oldColor);
			}
			
		}
	}
}
