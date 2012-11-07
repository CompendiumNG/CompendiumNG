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
import java.io.File;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.NoSuchElementException;
import java.util.Vector;

import javax.swing.Icon;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.border.AbstractBorder;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellEditor;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreeCellEditor;
import javax.swing.tree.TreeNode;
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
import com.compendium.meeting.MeetingEvent;
import com.compendium.meeting.MeetingManager;
import com.compendium.ui.dialogs.UINodeContentDialog;
import com.compendium.ui.plaf.ListUI;
import com.compendium.ui.plaf.NodeUI;
import com.compendium.ui.plaf.ViewPaneUI;
import com.compendium.ui.popups.UIViewOutlinePopupMenu;

/**
 * This class is used to display outline view.
 * Holds the data for and handles the events of the tree node in the outline view.
 * @author Lakshmi Prabhakaran
 */

public class UIViewOutline extends JPanel implements IUIConstants, ActionListener, TreeExpansionListener, 
				TreeSelectionListener, PropertyChangeListener, TreeWillExpandListener {
	
	/** The serial version id  */
	private static final long serialVersionUID 					= -673517173364176061L;

	/** the object of this class */
	public static UIViewOutline me 								= null; 
	
	/** The scrollpane for the stencil set.*/
	private JScrollPane			oScrollPane 					= null;
	
	/** Has this panel been drawn yet?*/
	private boolean 			drawn 							= false;
	
	/** The string value of this outline view.*/
	private String 				sMode 							= ""; //$NON-NLS-1$

	/** The JTree to display outline view */
	private JTree 				tree 							= null;
	
	/** List of Parent Node IDs against Child nodes  */
	private Hashtable			htNodes							= new Hashtable();
	
	/** A list of listeners objects */
	private static Vector 		viewListener 					= new Vector();
		
	/** The cache model for the currently open database.*/
	private IModel				oModel							= ProjectCompendium.APP.getModel();

	/** The session for this model.*/
	private PCSession 			oSession 						= oModel.getSession();
		
	/** The root Node of the tree */
	 protected static DefaultMutableTreeNode	rootNode		= null;
	
	/** The tree Model for the JTree. */
	private DefaultTreeModel  	treeModel						= null;
	
	/** The name of the project in the outline view.*/
	private String 				sProject 						= ""; //$NON-NLS-1$
	
	/** A list of nodes against nodeSummary */
	private  Hashtable 			htTreeNodes   					= new Hashtable();
	
	/** A list of nodes against parent node */
	private Hashtable 			htNodeParent   					= new Hashtable();
	
	/** Currently selected node */
	private UIViewOutlineTreeNode selectedNode   				= null;
	
	/** The view in which the currently selected node exists. */
	private View 				selectedView   					= null; 


	/** Currently selected node's NodeSummary */
	private NodeSummary 		selectedNodeSummary   			= null; 
	
	/** The author name of the current user.*/
	private String 				sAuthor 						= ""; //$NON-NLS-1$
	
	/** The node right-click popup menu associated with this node - null if one has not been opened yet.*/
	private UIViewOutlinePopupMenu			popup				= null;
	
	/** The button to close the outline view.*/
	private UIButton		pbCancel			= null;
	
	/**
	 * Constructor. Create a new instance of UIViewOutline, for a outline view.
	 * @param name, the name of the Tab
	 * @param sProject, The name of the project in the outline view.
	 */
	public UIViewOutline(String sProject, String name) {
		this.sProject = sProject;
		this.sMode = name;
		setAuthor(oModel.getUserProfile().getUserName());
		UIViewOutline.me = this;
	}

	/**
	 * Draws the contents of this panel.
	 */
	public void draw() {
		ProjectCompendium.APP.setWaitCursor();
		setLayout(new BorderLayout());
		
		UIViewOutlineTreeNode node = new UIViewOutlineTreeNode(sProject, -1);
		rootNode = new DefaultMutableTreeNode(node);
		
		treeModel = new DefaultTreeModel(rootNode);
		tree = new JTree(treeModel);
		tree.setFont(ProjectCompendiumFrame.currentDefaultFont);
		
		addNodesToTree();
		
		// Create a tree that allows one selection at a time.	
		tree.getSelectionModel().setSelectionMode
          						(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
		
		tree.setEditable(true);
		tree.setShowsRootHandles(true);
        tree.setToggleClickCount(4);
                
        //keep root node expanded
        tree.expandPath(tree.getPathForRow(0));
        
		// Set the icon for nodes.
		TreeNodeRenderer renderer = new TreeNodeRenderer();
        tree.setCellRenderer(renderer);

        // Enable tool tips.
       ToolTipManager.sharedInstance().registerComponent(tree);
       
       // Set the cell editor to modify nodes
       TreeCellEditor cellEditor = new TreeNodeEditor(tree, (DefaultTreeCellRenderer)tree.getCellRenderer());
       tree.setCellEditor(cellEditor);
  
       // Listen for the changes.
		treeModel.addTreeModelListener(new OutlineTreeModelListener());
		tree.addTreeSelectionListener(this);
		tree.addTreeWillExpandListener(this);
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
		 			int modifier = e.getModifiers();
		 			int shortcutKey = ProjectCompendium.APP.shortcutKey;
		 			int keyCode = e.getKeyCode();
		 			
		 			if(modifier == shortcutKey){
		 				switch (keyCode) {
		 					case KeyEvent.VK_C: {
		 						onCopy();
		 						break;
		 					}
		 					case KeyEvent.VK_X: {
		 						 onCut();
		 						break ;
		 					}
		 					case KeyEvent.VK_V: {
		 						onPaste();
		 						break;
		 					}
		 				}
		 			} else if((keyCode == KeyEvent.VK_DELETE  && modifier == 0) &&
		 					selectedNode != rootNode.getUserObject()){
		 				onDelete();
		 				
		 			} else if((keyCode == KeyEvent.VK_F12 && modifier == 0) &&
		 					selectedNode != rootNode.getUserObject()){
		 				onMarkSeenUnseen(selectedNodeSummary, ICoreConstants.READSTATE);
					} else if((modifier == Event.SHIFT_MASK && keyCode == KeyEvent.VK_F12) &&
		 					selectedNode != rootNode.getUserObject()){
		 				onMarkSeenUnseen(selectedNodeSummary, ICoreConstants.UNREADSTATE);
 					}
		 			e.consume();
					if(popup != null)
						popup.onCancel();
					return ;
		 		}
			} 
		}); 	
		
		tree.addMouseListener(new MouseAdapter(){
			
			public void mouseExited(MouseEvent evt){
				ProjectCompendium.APP.setStatus(""); //$NON-NLS-1$
			}
			
        	public void mouseClicked(MouseEvent evt) {
        	
        		TreePath selPath = tree.getPathForLocation(evt.getX(), evt.getY());
    			UIViewOutlineTreeNode  parentNode = null;
    			UIViewOutlineTreeNode  childNode = null;
    			DefaultMutableTreeNode  treeNode = null;
    			selectedView = null;
    			selectedNode = null;
    			boolean isLevelOneNode = true;
    			
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
    				selectedNode = childNode ;	
        		
    				if(!treeNode.equals(rootNode)){
    					
    					DefaultMutableTreeNode parent = null;
						
    					if(sMode.equals(DISPLAY_VIEWS_AND_NODES)){
    						Object[] nodes = (Object[]) selPath.getPath();
    						if(nodes.length > 2){
    							// parent view is the node at level 1
    							parent = (DefaultMutableTreeNode)nodes[1];
    							parentNode = (UIViewOutlineTreeNode) parent.getUserObject();
    							isLevelOneNode = false;
    							selectedView = View.getView(parentNode.getId());
    							
    						} else {
    							selectedView = View.getView(childNode.getId());
    						}
    						if(View.isViewType(childNode.getType())) {
            					parentNode = childNode;
            				} 
    					}
        				else {
        					if(!childNode.getObject().equals(ProjectCompendium.APP.getHomeView())){
	        					parent = (DefaultMutableTreeNode)treeNode.getParent();
		        				parentNode = (UIViewOutlineTreeNode) parent.getUserObject();
		        				selectedView = View.getView(parentNode.getId());
		        				isLevelOneNode = false;
        					}
	        				if(View.isViewType(childNode.getType())) {
            					parentNode = childNode; 
            				}
        				}
    					View parentView = View.getView(parentNode.getId());
    					NodeSummary child = (NodeSummary)childNode.getObject();
    					setStatus(child);
        				if(isLeftMouse){
  	        				// if single click open the view, 
        					// if double click open its contents. 
        					//which also opens view first as single click processed too.
	    					if (evt.getClickCount() == 2){		    					
		    					int type = child.getType();
		    					if (type == ICoreConstants.REFERENCE || type == ICoreConstants.REFERENCE_SHORTCUT) {
		    						openReference(child, parentView);
		    					} else {
		    						openContents(child, UINodeContentDialog.CONTENTS_TAB);
		    					}
		    				} else if(evt.getClickCount() == 1){
		        				// Open the view if it is a Map/ List other wise open the parent view.		    					
		    					openView(parentView, child);
		    				}	    				
		    			} else if(isRightMouse){
		    				tree.setSelectionPath(selPath);
		    				NodeSummary node = (NodeSummary)childNode.getObject();
		    				String sNodeID = node.getId();
		    				
		    				if (sNodeID.equals(ProjectCompendium.APP.getInBoxID())){
		    					return ;
		    				}
		    				
		    				popup = new UIViewOutlinePopupMenu("Popup Menu", node,  UIViewOutline.this, isLevelOneNode); //$NON-NLS-1$
		    				popup.show(tree, evt.getX()+ 20, evt.getY());
			        	}
    				}
    				tree.requestFocus();
    				evt.consume();
    				
        		}
        	}
        });

        Dimension size = tree.getPreferredSize();
        
		oScrollPane = new JScrollPane(tree);
		oScrollPane.setBackground(Color.white);
		oScrollPane.setPreferredSize(new Dimension(300, size.height));
		add(oScrollPane, BorderLayout.CENTER);
		add(createButtonPanel(), BorderLayout.SOUTH);
		
		updateTreeSelection();
		drawn = true;
		ProjectCompendium.APP.setDefaultCursor();
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
	 * Create and return the button panel.
	 */
	private JPanel createButtonPanel() {

		JPanel oButtonPanel = new JPanel();

		pbCancel = new UIButton(LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "UIViewOutline.closeButton")); //$NON-NLS-1$
		pbCancel.setMnemonic(LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "UIViewOutline.closeButtonMnemonic").charAt(0)); //$NON-NLS-1$
		pbCancel.addActionListener(this);
		oButtonPanel.add(pbCancel);

		return oButtonPanel;
	}
	
	/**
	 * Get the name for this  tab.
	 * @return String, the name of this tab.
	 */
	public String getObjectName() {
		return sMode;
	}

	/**
	 * Sets the name for this tab.
	 */
	public void  setObjectName(String s ) {
		 sMode = s;
	}
	
	
	/**
	 * @return Returns the rootNode.
	 */
	public static DefaultMutableTreeNode getRootNode() {
		return rootNode;
	}

	/**
	 * Has this panel been drawn yet?
	 * @return boolean, true if the panel has been drawn, else false.
	 */
	public boolean isDrawn() {
		return drawn;
	}

	/**
	 * Refresh the tree
	 *
	 */
	public void refreshTree(){
		tree.repaint();
	}
	
	/**
	 * adds nodes to the tree depending on the option selected
	 *
	 */
	public void addNodesToTree(){
		rootNode.removeAllChildren();
		// Create the nodes according the option selected
		if(sMode.equals(DISPLAY_VIEWS_AND_NODES)){
			
			createViewsAndNodes();
	       	
	    } else if (sMode.equals(DISPLAY_VIEWS_ONLY)) {
	    		    		    	
	    	View homeView = oModel.getUserProfile().getHomeView();
			homeView.initialize(oSession,oModel);
			
			UIViewOutlineTreeNode top = new UIViewOutlineTreeNode(homeView);
			DefaultMutableTreeNode topNode = new DefaultMutableTreeNode(top);
						
			if( !homeView.getListenerList().contains(this)){
				homeView.addPropertyChangeListener(this);
			}
			addToTreeNodes(topNode, top.getId());
			rootNode.add(topNode);
			createViewNodes(topNode, true);
	    }    
        tree.repaint();
    	treeModel.reload(rootNode);
    
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
			//	Bug Fix - State change doesn't reflect in List views immediately.
				Vector views = node.getMultipleViews();
				if(node instanceof View){
					views.add(node);
				}
				refreshViews(views);
			}
			
		} catch(Exception io) {
			if(state == ICoreConstants.READSTATE)
				System.out.println("Unable to mark as seen"); //$NON-NLS-1$
			else 
				System.out.println("Unable to mark as un-seen"); //$NON-NLS-1$
		}
		
		
	}
	
	/**
	 * Marks seen/unseen for whole view
	 * @param view com.compendium.code.datamodel.View, the view associated with this menu.
	 * @param state, the state to set
	 */
	public void onMarkAll(View view, int state){
		try {
			String viewID = view.getId();
			String homeID = ProjectCompendium.APP.getHomeView().getId();
			String inboxID = ProjectCompendium.APP.getInBoxID();
			if(!(viewID.equals(homeID) || viewID.equals(inboxID))) {
				view.setState(state);
				
				Vector views = new Vector();
				views.add(view);
				refreshViews(views);
			}
			Enumeration nodes = oModel.getNodeService().getChildNodes(oSession, view.getId());

			for(Enumeration e = nodes;e.hasMoreElements();) {

				NodeSummary  nodeSummary = (NodeSummary)e.nextElement();
				String nodeID = nodeSummary.getId();
				if(!(nodeID.equals(homeID) || nodeID.equals(inboxID))) {
					nodeSummary.setState(state);
					Vector views = nodeSummary.getMultipleViews();
					if(nodeSummary instanceof View){
						views.add(nodeSummary);
					}
					refreshViews(views);
				}
			}
		} catch(Exception io) {
			if(state == ICoreConstants.READSTATE)
				System.out.println("Unable to mark as seen"); //$NON-NLS-1$
			else 
				System.out.println("Unable to mark as un-seen"); //$NON-NLS-1$
		}
	}
	
	/**
	 * Repaints the view frames.
	 * @param views , The views to be refreshed
	 */
	
	private void refreshViews(Vector views) {
		
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
	}
	
	/**
	 * Copies the selected node to clipboard
	 */
	
	public void onCopy(){
		
		TreePath treePath = tree.getSelectionPath();
		DefaultMutableTreeNode node = (DefaultMutableTreeNode)tree.getLastSelectedPathComponent();
		UIViewOutlineTreeNode treeNode = (UIViewOutlineTreeNode)node.getUserObject();
		
		NodeSummary oNode = treeNode.getObject();
		if(oNode.equals(ProjectCompendium.APP.getHomeView()) || oNode.getId().equals(ProjectCompendium.APP.getInBoxID())){
			return ;
		}
			
		if(selectedView == null || selectedView.equals(oNode)){
			Vector parentViews;
			try {
				parentViews = oNode.getMultipleViews();
				selectedView = (View) parentViews.get(0);
			} catch (SQLException e) {
				e.printStackTrace();
			} catch (ModelSessionException e) {
				e.printStackTrace();
			}
		}
		UIViewFrame oViewFrame = ProjectCompendium.APP.getViewFrame(selectedView, selectedView.getLabel());
		UIViewFrame currentFrame = ProjectCompendium.APP.getCurrentFrame();
		
		if (oViewFrame instanceof UIMapViewFrame) {
			UIViewPane oPane = ((UIMapViewFrame)oViewFrame).getViewPane();
			UINode uinode = (UINode) oPane.get(oNode.getId());
			uinode.setSelected(true);
			oPane.setSelectedNode(uinode, ICoreConstants.SINGLESELECT);
			NodeUI nodeUI = uinode.getUI();
			if (uinode != null) {
				if (uinode.isSelected()) {
					oPane.getUI().copyToClipboard(null);
				}
				else {
					uinode.getViewPane().getUI().copyToClipboard(nodeUI);
				}
				uinode.setSelected(false);
				uinode.requestFocus(); 
			}
		} else {
			UIListViewFrame oListViewFrame = (UIListViewFrame)oViewFrame;
			UIList oUIList = oListViewFrame.getUIList();
			oUIList.selectNode(oUIList.getIndexOf(oNode), ICoreConstants.SINGLESELECT);
			ListUI listui = oUIList.getListUI();
			listui.copyToClipboard();
			oUIList.deselectAll();
		}
		if(!currentFrame.equals(oViewFrame)){
			oViewFrame.moveToBack();
		}
	}// End of function OnCopy()
	
	/**
	 * Cuts the selected node to clipboard
	 */
	
	public void onCut(){
		
		TreePath treePath = tree.getSelectionPath();
		DefaultMutableTreeNode node = (DefaultMutableTreeNode)tree.getLastSelectedPathComponent();
		UIViewOutlineTreeNode treeNode = (UIViewOutlineTreeNode)node.getUserObject();
		
		NodeSummary oNode = treeNode.getObject();
		if(oNode.equals(ProjectCompendium.APP.getHomeView()) || oNode.getId().equals(ProjectCompendium.APP.getInBoxID())){
			return ;
		}
		if(selectedView == null ||selectedView.equals(oNode)){
			Vector parentViews;
			try {
				parentViews = oNode.getMultipleViews();
				selectedView = (View) parentViews.get(0);
			} catch (SQLException e) {
				e.printStackTrace();
			} catch (ModelSessionException e) {
				e.printStackTrace();
			}
		}
		UIViewFrame oViewFrame = ProjectCompendium.APP.getViewFrame(selectedView, selectedView.getLabel());
		UIViewFrame currentFrame = ProjectCompendium.APP.getCurrentFrame();
		
		if (oViewFrame instanceof UIMapViewFrame) {
			UIViewPane oPane = ((UIMapViewFrame)oViewFrame).getViewPane();
			UINode uinode = (UINode) oPane.get(oNode.getId());
			uinode.setSelected(true);
			oPane.setSelectedNode(uinode, ICoreConstants.SINGLESELECT);
			NodeUI nodeUI = uinode.getUI();
			if (uinode != null) {
				if (uinode.isSelected()) {
					oPane.getUI().cutToClipboard(null);
				}
				else {
					uinode.getViewPane().getUI().cutToClipboard(nodeUI);
				}
				uinode.requestFocus(); 
			}
		} else {
			UIListViewFrame oListViewFrame = (UIListViewFrame)oViewFrame;
			UIList oUIList = oListViewFrame.getUIList();
			oUIList.selectNode(oUIList.getIndexOf(oNode), ICoreConstants.SINGLESELECT);
			ListUI listui = oUIList.getListUI();
			listui.cutToClipboard();
		}
		if(!currentFrame.equals(oViewFrame)){
			oViewFrame.moveToBack();
		}
	}// End of function OnCut()
	
	/**
	 * Paste the node from clipboard
	 */
	
	public void onPaste(){
		
		TreePath treePath = tree.getSelectionPath();
		DefaultMutableTreeNode node = (DefaultMutableTreeNode)tree.getLastSelectedPathComponent();
		UIViewOutlineTreeNode treeNode = (UIViewOutlineTreeNode)node.getUserObject();
		
		NodeSummary oNode = treeNode.getObject();
		if(oNode.getId().equals(ProjectCompendium.APP.getInBoxID()) || (!(oNode instanceof View))){
			return ;
		}
				
		ProjectCompendium.APP.ht_pasteCheck.clear();
		
		UIViewFrame oViewFrame = ProjectCompendium.APP.addViewToDesktop((View)oNode, oNode.getLabel());
		
		if (oViewFrame instanceof UIMapViewFrame) {
			
			ViewPaneUI paneUI = ( ((UIMapViewFrame)oViewFrame).getViewPane().getUI());

			paneUI.pasteFromClipboard();
			ProjectCompendium.APP.scaleAerialToFit(); // will refresh aerial view after paste 
		} else {
			((UIListViewFrame)oViewFrame).getUIList().getListUI().pasteFromClipboard();
		}
		try {
			Vector views = new Vector();
			views.add(oNode);
			refreshViews(views);
		} catch (Exception e) {
			
		}
		
	}// End of function OnPaste()
	/**
	 * Deletes the selected node
	 *
	 */	
	public void onDelete(){
		
		TreePath treePath = tree.getSelectionPath();
		DefaultMutableTreeNode node = (DefaultMutableTreeNode)tree.getLastSelectedPathComponent();
		UIViewOutlineTreeNode treeNode  =(UIViewOutlineTreeNode)node.getUserObject();
		
		// root node and Home window cannot be deleted. 
		if(!(node.equals(rootNode) || (treeNode.getObject()).equals(ProjectCompendium.APP.getHomeView()) ||
				(treeNode.getId()).equals(ProjectCompendium.APP.getInBoxID()))) {
			
			NodeSummary nodeSum 	= treeNode.getObject();
			nodeSum.initialize(oSession, oModel);
			
			try {
			    Vector parentViews = nodeSum.getMultipleViews();
			    if(parentViews.size() == 1 && nodeSum instanceof View){
			    	// if the deleted node is a view, close the view if open.
			    	UIViewFrame frame = ProjectCompendium.APP.getInternalFrame((View)nodeSum);
			    	if(frame != null){
				    	ProjectCompendium.APP.getAllFrames().remove(frame);
				    	frame.dispose();
			    	}
				}
			    if(sMode.equals(DISPLAY_VIEWS_ONLY)){
					
					DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode)node.getParent();
					UIViewOutlineTreeNode parentTreeNode =(UIViewOutlineTreeNode)parentNode.getUserObject();
					NodeSummary parentSum = parentTreeNode.getObject();
					
					View view = View.getView(parentSum.getId());
					view.initialize(oSession, oModel);
					Vector v = new Vector();
					v.add(view);
					deleteSelectedNode(v, nodeSum);
					
				} else if(sMode.equals(DISPLAY_VIEWS_AND_NODES)) {
					DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode)node.getParent();
					if (!parentNode.equals(rootNode)){
						Object[] nodes = (Object[]) treePath.getPath();
    					
						DefaultMutableTreeNode parentView = (DefaultMutableTreeNode)nodes[1];
    					UIViewOutlineTreeNode parentTreeNode =(UIViewOutlineTreeNode)parentView.getUserObject();
						NodeSummary parentSum = parentTreeNode.getObject();
						
						View view = View.getView(parentSum.getId());
						view.initialize(oSession, oModel);
						Vector v = new Vector();
						v.add(view);
						deleteSelectedNode(v, nodeSum);
					
					} else if(parentViews.size() == 1){
						deleteSelectedNode(parentViews, nodeSum);
					} else{
						int response = JOptionPane.showConfirmDialog(ProjectCompendium.APP, 
								LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "UIViewOutline.message1a")+"\n"+ //$NON-NLS-1$ //$NON-NLS-2$
								LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "UIViewOutline.message1b")+"\n"+ //$NON-NLS-1$ //$NON-NLS-2$
								LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "UIViewOutline.message1c")+"\n\n"+ //$NON-NLS-1$ //$NON-NLS-2$
								LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "UIViewOutline.message1d"), //$NON-NLS-1$
							    LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "UIViewOutline.message1e")+nodeSum.getLabel(), JOptionPane.YES_NO_OPTION); //$NON-NLS-1$
						if (response == JOptionPane.YES_OPTION) {
							deleteSelectedNode(parentViews, nodeSum);
						}
					}
				}
			} catch (SQLException e1) {
				e1.printStackTrace();
			} catch (ModelSessionException e1) {
				e1.printStackTrace();
			}
		}
	}
	/**
	 * Deletes the given node from the given views
	 * @param view View where the node to be delete exists
	 * @param node NodeSummary to be deleted
	 * @throws ModelSessionException 
	 * @throws SQLException 
	 * @throws NoSuchElementException 
	 */
	public void deleteSelectedNode(Vector views, NodeSummary node) {
		
		for(int i= 0; i <views.size(); i ++){
			View view = (View) views.get(i);
			UIViewFrame frame = ProjectCompendium.APP.getViewFrame(view, view.getLabel());
	    	if(frame instanceof UIMapViewFrame){
	    		UIViewPane pane = ((UIMapViewFrame)frame).getViewPane();
	    		UINode uiNode = (UINode)pane.get(node.getId());
	    		pane.setSelectedNode(uiNode,ICoreConstants.SINGLESELECT);
	    		pane.getUI().onDelete(); 
	    	} else {
	    		UIList list = ((UIListViewFrame)frame).getUIList();
	    		list.deselectAll();
	    		list.selectNode(list.getIndexOf(node), ICoreConstants.SINGLESELECT);
	    		list.getListUI().onDelete();
	    	}
	    }
		refreshViews(views);
	}
	
	/**
	 * To create nodes for the outline view tree  - view and nodes options
	 */
	public DefaultMutableTreeNode createViewsAndNodes() {
		
		cleanUp();
		removeChildNodes(rootNode);
		rootNode.removeAllChildren();
		
		View root = oModel.getUserProfile().getHomeView();
		root.initialize(oSession, oModel);
		try {
			root.initializeMembers();
			UIViewOutlineTreeNode top = new UIViewOutlineTreeNode(root);
			DefaultMutableTreeNode topNode = new DefaultMutableTreeNode(top);
			
			if( !root.getListenerList().contains(this)){
				root.addPropertyChangeListener(this);
			}
			
			addToTreeNodes(topNode, top.getId());
			rootNode.add(topNode);
			
			DefaultMutableTreeNode dummyNode = new DefaultMutableTreeNode("dummy"); //$NON-NLS-1$
			if(root.getNodeCount()> 0){
				topNode.add(dummyNode);
			}
			
			Vector views = null;
			try {
				views = oModel.getNodeService().getAllChildViews(oSession, root.getId());
				
			} catch(Exception e) {
				e.printStackTrace();
			}
			
			for (int i = 0; i< views.size(); i++){
	    		View ns = (View)views.get(i);
	    		
	    		DefaultMutableTreeNode childNode =  null;
	    		
	    		UIViewOutlineTreeNode child = new UIViewOutlineTreeNode(ns);
				childNode = new DefaultMutableTreeNode(child);
				
				if(!ns.getListenerList().contains(this)){
					ns.addPropertyChangeListener(this);
				}
				rootNode.add(childNode);
	    		addToTreeNodes(childNode, ns.getId());
	    		
	    		if(ns.getNodeCount() > 0){
	    			dummyNode = new DefaultMutableTreeNode("dummy"); //$NON-NLS-1$
	    			childNode.add(dummyNode);
	    		}
	    	}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ModelSessionException e) {
			e.printStackTrace();
		}
		return rootNode;
	}
	
	/**
	 * To update the given tree node
	 * @param parent, node of the tree where child nodes have to be added
	 */
	
	public void updateNodes(DefaultMutableTreeNode parent) {
		
		removeChildNodes(parent);
		parent.removeAllChildren();
		
		NodeSummary nodeSum = ((UIViewOutlineTreeNode)parent.getUserObject()).getObject();
		TreeNode[] parentNodes = (TreeNode[])parent.getPath();
		
		String id = ((UIViewOutlineTreeNode)((DefaultMutableTreeNode)parentNodes[1]).getUserObject()).getId();
		
		//parent - child relationship based on the links
	
		UIArrangeLeftRight arrange = new UIArrangeLeftRight();
		View view = View.getView(id);
		view.initialize(oSession, oModel);
		
		if (view != null){
			arrange.processView(view);
			
			Hashtable htNodesId = arrange.getNodes();
			Hashtable htNodesLevel = arrange.getNodesLevel();
			Hashtable htNodesBelow = arrange.getNodesBelow();
	
			Vector nodeLevelList = arrange.getNodeLevelList();
			Vector nodesAdded = new Vector();
			
			if (nodeLevelList.size() > 0) {
				
				// CYCLE THROUGH NODES SORTED BY YPOS AND PRINT THEM AND THIER CHILDREN
				for(Enumeration f = ((Vector)nodeLevelList.elementAt(0)).elements();f.hasMoreElements();) {
					String nodeToAddId = (String)f.nextElement();
					NodeSummary nodeToAdd = (NodeSummary) htNodesId.get(nodeToAddId);
					
					DefaultMutableTreeNode childNode =  null;
		    		
		    		UIViewOutlineTreeNode child = new UIViewOutlineTreeNode(nodeToAdd);
					childNode = new DefaultMutableTreeNode(child);
					
					if(!nodeToAdd.getListenerList().contains(this)){
						nodeToAdd.addPropertyChangeListener(this);
					}
					
					parent.add(childNode);
					nodesAdded.add(nodeToAdd);
		    		addToTreeNodes(childNode, nodeToAddId);
					
		    		nodesAdded = recursiveNodeAddition(childNode, htNodesBelow, htNodesId, nodesAdded);
					
				}//end for
			}//end if
			//temp Fix - If any node has been left out because of complex linkage then add them as level 1
			try {
				if(nodesAdded.size() < view.getNodeCount()){
					for(Enumeration e = htNodesId.keys(); e.hasMoreElements(); ){
						String nodeToAddId =  (String)e.nextElement();
						NodeSummary nodeToAdd = (NodeSummary) htNodesId.get(nodeToAddId); 
						
						if(!nodesAdded.contains(nodeToAdd)) {
							UIViewOutlineTreeNode child = new UIViewOutlineTreeNode(nodeToAdd);
							DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(child);
							
							if(!nodeToAdd.getListenerList().contains(this)){
								nodeToAdd.addPropertyChangeListener(this);
							}
							
							parent.add(childNode);
							nodesAdded.add(nodeToAdd);
				    		addToTreeNodes(childNode, nodeToAddId);
						}//end if
					}//end for
				}//end if
			} catch (ModelSessionException e) {
				e.printStackTrace();
			}//end try
		}// end if view != null
   }//end updateNodes
	
	
	//private Hashtable nodesRecursed = new Hashtable(51);
	
	/**
	 * Add the child nodes to the parent. (parent - Child relationship is based on links)
	 * @param node, DefaultMutableTreeNode whose children has to added
	 * @param htNodesBelow, Hashtable of nodes against their child nodes
	 */
	private Vector recursiveNodeAddition(DefaultMutableTreeNode node, Hashtable htNodesBelow, Hashtable htNodes, Vector nodesAdded){
		
		UIViewOutlineTreeNode treeNode = (UIViewOutlineTreeNode) node.getUserObject();
		NodeSummary nodeSum = treeNode.getObject();
		String nodeId = treeNode.getId();
		
		Vector nodeChildren = (Vector)htNodesBelow.get(nodeId);
		if (nodeChildren != null) {
			for (int i = 0; i < nodeChildren.size(); i++) {
				String nodeToAddId = (String)nodeChildren.elementAt(i);
				NodeSummary nodeToAdd = (NodeSummary) htNodes.get(nodeToAddId);
				
				UIViewOutlineTreeNode child = new UIViewOutlineTreeNode(nodeToAdd);
	    		DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(child);
				
				if(!nodeToAdd.getListenerList().contains(this)){
					nodeToAdd.addPropertyChangeListener(this);
				}
				
				node.add(childNode);
				
	    		addToTreeNodes(childNode, nodeToAddId);
	    		if(!nodesAdded.contains(nodeToAdd)) {
	    			nodesAdded.add(nodeToAdd);
	    			nodesAdded = recursiveNodeAddition(childNode, htNodesBelow, htNodes, nodesAdded);
	    		}
			}
		}
		return nodesAdded;
	}
	
	
	/**
	 * To create nodes for the outline view for Views Only option
	 * @param node, node of the tree where child nodes have to be added
	 * @param getChild, boolean true, if child nodes has to be added to the node
	 */
	public DefaultMutableTreeNode createViewNodes(DefaultMutableTreeNode parent, boolean getChild) {
		
		if(parent.equals(rootNode)){
			cleanUp();
			
			parent = (DefaultMutableTreeNode) parent.getFirstChild();
			htNodes.clear();
			htNodeParent.clear();
			htTreeNodes.clear();
			
			NodeSummary parentSummary = ((UIViewOutlineTreeNode)parent.getUserObject()).getObject();
			String id = parentSummary.getId();
			
			addToTreeNodes(parent, id);
		}
		
		NodeSummary parentSummary = ((UIViewOutlineTreeNode)parent.getUserObject()).getObject();
		String id = parentSummary.getId();
				
		removeChildNodes(parent);
		parent.removeAllChildren();
		
		if(getChild){
    	    htNodes.remove(parentSummary.getId());
   			Vector vtChildNodes = new Vector();;
			try {
				vtChildNodes = oModel.getNodeService().getChildViews(oSession, id);
				htNodes.put(id, vtChildNodes);
				// find next level to display expand/collapse icon
				for (int i = 0; i < vtChildNodes.size(); i++ ){
					String viewId = ((View) vtChildNodes.get(i)).getId();
					Vector temp =  oModel.getNodeService().getChildViews(oSession, viewId);
					htNodes.put(viewId, temp);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
    		
        }
 		Vector vtNodes = ((Vector) htNodes.get(id));
 		
 		for (Enumeration e = vtNodes.elements() ; e.hasMoreElements();){
    		NodeSummary ns = (NodeSummary)e.nextElement();
    		String nodeId = ns.getId();
    		
    		UIViewOutlineTreeNode child = new UIViewOutlineTreeNode(ns);
        	DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(child);
        	
        	if(!ns.getListenerList().contains(this)){
				ns.addPropertyChangeListener(this);
			}
        	
        	addToTreeNodes(childNode, nodeId);
    			
			//put child node against the parent, it is useful when displaying views only option
			// when a node type is changed from other types to View node. 
			htNodeParent.put(childNode, parent);
			parent.add(childNode);
			
			//check if any views contains itself (even at several levels deep)
			boolean isRepeat = false;
		    TreeNode[] path = childNode.getPath();
		    for(int j = 0; j < path.length - 1; j ++){
		    	DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) path[j];
		    	NodeSummary parentNodeSummary = ((UIViewOutlineTreeNode)parentNode.getUserObject()).getObject();
		    	if(ns.equals(parentNodeSummary)){
		    		isRepeat = true ;
		    	}
		    }
		    
			if((htNodes.containsKey(nodeId)) && (!isRepeat)) {
        		Vector vtChildNodes = ((Vector) htNodes.get(nodeId));
        		if(vtChildNodes.size() > 0){
					DefaultMutableTreeNode dummy = new DefaultMutableTreeNode("dummy"); //$NON-NLS-1$
					childNode.add(dummy);
				}
			}
       }
 		treeModel.reload(parent);
        return parent;
   }

	/**
	 * Open the contents popup for the currently selected node.
	 */
	public void openContents(NodeSummary node, int tab) {

		String sNodeID = node.getId();
		if (!sNodeID.equals(ProjectCompendium.APP.getHomeView().getId()) &&
				!sNodeID.equals(ProjectCompendium.APP.getInBoxID())) {
			UINodeContentDialog contentDialog = new UINodeContentDialog(ProjectCompendium.APP, node, tab);
			UIUtilities.centerComponent(contentDialog, ProjectCompendium.APP);
			contentDialog.setVisible(true);
		}
	}
	
	/**
	 * Open a reference node.
	 * @param node
	 * @param view
	 */
	public void openReference(NodeSummary node, View view){
		String path = node.getSource();

		if (path == null || path.equals("")) { //$NON-NLS-1$
			openContents(node, UINodeContentDialog.CONTENTS_TAB);
		} else if (path.startsWith(ICoreConstants.sINTERNAL_REFERENCE)) {
			path = path.substring(ICoreConstants.sINTERNAL_REFERENCE.length());
			int ind = path.indexOf("/"); //$NON-NLS-1$
			if (ind != -1) {
				String sGoToViewID = path.substring(0, ind);
				String sGoToNodeID = path.substring(ind+1);		
				IModel model = ProjectCompendium.APP.getModel();
				String history = "Outline View "; //$NON-NLS-1$
				UIUtilities.jumpToNode(sGoToViewID, sGoToNodeID, history);
			}
		} else if (path.startsWith("http:") || path.startsWith("https:") || path.startsWith("www.")) { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			if (ExecuteControl.launch( path ) == null) {
				openContents(node, UINodeContentDialog.CONTENTS_TAB);
			} else {
				// IF WE ARE RECORDING A MEETING, RECORD A REFERENCE LAUNCHED EVENT.
				if (view != null && (ProjectCompendium.APP.oMeetingManager != null && ProjectCompendium.APP.oMeetingManager.captureEvents()
						&& (ProjectCompendium.APP.oMeetingManager.getMeetingType() == MeetingManager.RECORDING))) {

					ProjectCompendium.APP.oMeetingManager.addEvent(
						new MeetingEvent(ProjectCompendium.APP.oMeetingManager.getMeetingID(),
										 ProjectCompendium.APP.oMeetingManager.isReplay(),
										 MeetingEvent.REFERENCE_LAUNCHED_EVENT,
										 view,
										 node));
				}
			}		
		} else {
			File file = new File(path);
			String sPath = path;
			if (file.exists()) {
				sPath = file.getAbsolutePath();
			}
			// It the reference is not a file, just pass the path as is, as it is probably a special type of url.
			if (ExecuteControl.launch( sPath ) == null) {
				openContents(node, UINodeContentDialog.CONTENTS_TAB);
			} else {
				// IF WE ARE RECORDING A MEETING, RECORD A REFERENCE LAUNCHED EVENT.
				if (view != null &&(ProjectCompendium.APP.oMeetingManager != null && ProjectCompendium.APP.oMeetingManager.captureEvents()
						&& (ProjectCompendium.APP.oMeetingManager.getMeetingType() == MeetingManager.RECORDING))) {

					ProjectCompendium.APP.oMeetingManager.addEvent(
						new MeetingEvent(ProjectCompendium.APP.oMeetingManager.getMeetingID(),
										 ProjectCompendium.APP.oMeetingManager.isReplay(),
										 MeetingEvent.REFERENCE_LAUNCHED_EVENT,
										 view,
										 node));
				}
			}			
		}
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
				
				UIUtilities.focusNodeAndScroll(ns, viewFrame);
			}
			
			Vector history = new Vector();
			history.addElement( "Outline View "); //$NON-NLS-1$
			viewFrame.setNavigationHistory(history);
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ModelSessionException e) {
			e.printStackTrace();
		} 
		
	}
	/**
	 * Returns the list of listeners
	 * @return Vector, list of listener object
	 */
	public static Vector getViewListener() {
		return viewListener;
	}
	
	/**
	 * Adds to the list of listeners
	 * @return Vector, list of listener object 
	 */
	public static Vector addToViewListener(NodeSummary view) {
		if(!viewListener.contains(view))
			viewListener.add(view);
		return viewListener;
	} 
	
	/**
	 * Returns to the list of nodes associated with the given id
	 * @return Vector, list of nodes 
	 */
	
	public Vector getTreeNode(String id) {
		Vector v = new Vector();
		if(htTreeNodes.containsValue(id)){
			for(Enumeration e = htTreeNodes.keys();e.hasMoreElements();){
				DefaultMutableTreeNode node = (DefaultMutableTreeNode)e.nextElement();
				String nodeId = (String) htTreeNodes.get(node) ;
				if(nodeId.equals(id)){
					v.add(node);
				}
			}
		}
		return v;
	}
	
	/**
	 * Removes the given node from the list 
	 * @param node, DefaultMutableTreeNode, the node to be removed
	 */
	public void removeTreeNode(DefaultMutableTreeNode node) {
		htTreeNodes.remove(node);
	}
	
	/**
	 * Removes the all child nodes for the given node recursively 
	 * @param node, DefaultMutableTreeNode, the node whose children to be removed
	 */
	public void removeChildNodes(DefaultMutableTreeNode node) {
		int count = node.getChildCount();
		for(int i=0; i < count; i++){
			DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) node.getChildAt(i);
			if(childNode.getUserObject() instanceof String){
				node.remove(childNode);
				return ;
			} else {
				removeChildNodes(childNode);
				removeTreeNode(childNode);
			}
			for (Enumeration e = htNodeParent.keys(); e.hasMoreElements();){
				DefaultMutableTreeNode child = (DefaultMutableTreeNode) e.nextElement();
				DefaultMutableTreeNode parent = (DefaultMutableTreeNode)htNodeParent.get(child);
				if(node.equals(parent)) {
					htNodeParent.remove(child);
				}
			}
		}
	}
	/**
	 * Removes the node from the outline view that is removed from main view 
	 * @param ns, NodeSummary of the view node whose child has been removed
	 */
	
	public void removeChildViews(NodeSummary ns){
		UIArrangeLeftRight arrange = new UIArrangeLeftRight();
		if (ns != null && ns instanceof View){
			View view = (View) ns;
			arrange.processView(view);
						
			Hashtable htNodesId = arrange.getNodes();
			Hashtable htNodesLevel = arrange.getNodesLevel();
			Hashtable htNodesBelow = arrange.getNodesBelow();
	
			Vector nodeLevelList = arrange.getNodeLevelList();
			
			for(Enumeration f = htNodesId.keys();f.hasMoreElements();) {
				String nodeId = (String)f.nextElement();
				NodeSummary node = (NodeSummary)htNodesId.get(nodeId);
				if(View.isViewType(node.getType())) {
					try {
						if(node.getMultipleViews().size() == 1) {
							Vector viewNodes = getTreeNode(nodeId);
							for(int i =0; i< viewNodes.size(); i++){
								DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) viewNodes.get(i);
								if((parentNode.getParent() != null) && (parentNode.getParent().equals(rootNode))) {
									NodeSummary nodeSum = ((UIViewOutlineTreeNode)parentNode.getUserObject()).getObject();
									removeChildViews(nodeSum);
									removeTreeNode(parentNode);
									rootNode.remove(parentNode);
								}
							}
						}
					} catch (SQLException e) {
						e.printStackTrace();
					} catch (ModelSessionException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	/**
	 * Adds the given node to the table
	 * @param node, the key object for the hashtable
	 * @param s, String , the id - the value object for the hashtable
	 * @return Hashtable, the updated the hashtable
	 */ 
	public Hashtable addToTreeNodes( DefaultMutableTreeNode node, String s) {
		htTreeNodes.put(node, s);
		return htTreeNodes;
	}
	
	/**
	 * Returns the Tree
	 * @return JTree, the outline view tree
	 */
	public JTree getTree() {
		return tree;
	}

	/**
	 * Return the treeModel
	 * @return DefaultTreeModel, the treeModel associated with the Tree
	 */
	public DefaultTreeModel getTreeModel() {
		return treeModel;
	}
	
	/**
 	 * Indicates when nodes on a view are selected and deselected.
  	 * @param selected true for selected false for deselected.
	 */
	public void setNodeSelected(boolean selected) {
		tree.clearSelection();
		if (selected) {			
			updateTreeSelection();						
		} 
	}
	
	/**
	 * Updates the list of nodes for the currently selected nodes in the current view.
	 */
	private void updateTreeSelection() {
		
		Vector allNodes = new Vector(51);				
		Hashtable htNodesCheck = new Hashtable();		
		UIViewFrame frame = ProjectCompendium.APP.getCurrentFrame();
		NodeSummary parentView = null;
		if (frame != null) {
			if (frame instanceof UIMapViewFrame) {
				UIMapViewFrame mapFrame = (UIMapViewFrame)frame;
				UIViewPane pane = mapFrame.getViewPane();
				if (pane != null) {
					parentView = pane.getView();
					Enumeration e = pane.getSelectedNodes();
					UINode node = null;
					NodeSummary oNode = null;
					String sNodeID = ""; //$NON-NLS-1$
					for (Enumeration en=e; en.hasMoreElements();) {
						node = (UINode)en.nextElement();
						sNodeID = node.getNode().getId();
						oNode = node.getNode();
						if (!sNodeID.equals(ProjectCompendium.APP.getTrashBinID())) {
							allNodes.addElement(oNode);
						}
					}
				}
				
			} else if (frame instanceof UIListViewFrame) {
				UIListViewFrame listFrame = (UIListViewFrame)frame;
				UIList list = listFrame.getUIList();
				parentView = list.getView();
				if (list != null) {
					Enumeration e = list.getSelectedNodes();
					NodePosition nodePos = null; 
					for (Enumeration en=e; en.hasMoreElements();) {
						nodePos = (NodePosition)en.nextElement();
						allNodes.addElement(nodePos.getNode());
					}
				}				
			}
			for(int k = 0; k < allNodes.size(); k ++){
				NodeSummary node = (NodeSummary) allNodes.get(k);				
				String sNodeID = node.getId();
				Vector v = getTreeNode(sNodeID);
				for(int i = 0; i < v.size(); i++){
					DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) v.get(i);
					TreePath selectedNodePath = new TreePath(treeNode.getPath());
					if(sMode.equals(DISPLAY_VIEWS_AND_NODES)){
						DefaultMutableTreeNode topLevelNode = (DefaultMutableTreeNode) treeNode.getParent();
						UIViewOutlineTreeNode  topLevelOutlineNode = (UIViewOutlineTreeNode)topLevelNode.getUserObject();
						TreePath topLevelPath = new TreePath(topLevelNode.getPath());
						if(topLevelNode.equals(rootNode)) { 
							tree.addSelectionPath(selectedNodePath);
							
						} else {
							Vector parentViews = getTreeNode(parentView.getId());
							for(int j = 0; j < parentViews.size(); j ++){
								DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode)parentViews.get(j);
								TreePath parentPath = new TreePath(parentNode.getPath());
								if(!tree.isCollapsed(parentPath)){
									if(parentNode.isNodeDescendant(treeNode)){
										tree.addSelectionPath(selectedNodePath);
									}
								}
							}
						}
					} else if(sMode.equals(DISPLAY_VIEWS_ONLY)){
						DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) treeNode.getParent();
						if(parentNode != null ){
							UIViewOutlineTreeNode  parentOutlineNode = (UIViewOutlineTreeNode)parentNode.getUserObject();
							if(parentOutlineNode.getObject().equals(parentView)){
								TreePath parentPath = new TreePath(parentNode.getPath());
								if(!tree.isCollapsed(parentPath)){
									tree.addSelectionPath(selectedNodePath);
								}
							}
						}
					}
				}
			}
		}
	}
	/**
	 * Removes the node from teh outline view for views only option
	 * @param ns  NodeSummary of the node to be removed
	 * @param oView View in which the node is.
	 */
	public void removeNodeForViewsOnly(NodeSummary ns, View oView){
		
		Vector nodes = getTreeNode(ns.getId());
		Vector parentIds = new Vector();
				
		for(int i=0;i< nodes.size(); i++){
			DefaultMutableTreeNode child = (DefaultMutableTreeNode)nodes.get(i);
			DefaultMutableTreeNode parent = (DefaultMutableTreeNode)child.getParent();
			String parentId = ((UIViewOutlineTreeNode)parent.getUserObject()).getId(); 
			parentIds.add(parentId);
			if(oView.getId().equals(parentId)){
				parent.remove(child);
				removeTreeNode(child);
				htNodeParent.remove(child);
				viewListener.remove(((UIViewOutlineTreeNode)parent.getUserObject()).getObject());
				treeModel.reload(parent);
			}
		}
	}
	
	/**
	 * Removes the node from the outline view for views and nodes options  
	 * @param ns  NodeSummary of the node to be removed 
	 */
	public void removeNodeForViewsAndNodes(NodeSummary ns){
		try {
			// if the node is in multiple view just create the nodes and reload the root
			// to place the nodes in correct order.
			if(ns.getMultipleViews().size() >= 1){
				createViewsAndNodes();
				treeModel.reload(rootNode);
				tree.repaint();
				return;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ModelSessionException e) {
			e.printStackTrace();
		}
		//if the node is a view and is not in mulitple views., then deleted the view node from root
		
		if(ns instanceof View){
			Vector parents = getTreeNode(ns.getId());
			if  (parents == null || parents.size() <= 0){
				return;
			}
			for (int i=0; i<parents.size(); i++){
				DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) parents.get(i);
				if((parentNode.getParent() != null) && (parentNode.getParent().equals(rootNode))) {
					removeChildNodes(parentNode);
					removeChildViews(ns);
					removeTreeNode(parentNode);
					
					rootNode.remove(parentNode);
					treeModel.reload(rootNode);
				} 
				viewListener.remove(ns);
			}
		}
	}
	
	
	public void addNodeForViewsOnly(NodeSummary ns, View oView){
		if((ns.getType()!= ICoreConstants.TRASHBIN)){
			
			Vector nodes = getTreeNode(ns.getId());
			Vector parentIds = new Vector();
			Vector viewIds = new Vector();

			// check if the newly added node is in other views
			for(int i=0;i< nodes.size(); i++){
				DefaultMutableTreeNode child = (DefaultMutableTreeNode)nodes.get(i);
				DefaultMutableTreeNode parent = (DefaultMutableTreeNode)child.getParent();
				String parentId = ((UIViewOutlineTreeNode)parent.getUserObject()).getId(); 
				parentIds.add(parentId);
			}
			
			//Just to make sure the newly node isn't in the current view
			if(!parentIds.contains(oView.getId())){
				Vector parentNodes = getTreeNode(oView.getId());
				for (int i=0; i<parentNodes.size(); i++){
					DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) parentNodes.get(i);
					UIViewOutlineTreeNode treeNode = new UIViewOutlineTreeNode(ns);
					DefaultMutableTreeNode node = new DefaultMutableTreeNode(treeNode);
					
					if(!ns.getListenerList().contains(this)){
						ns.addPropertyChangeListener(this);
					}
					parentNode.add(node);
					addToTreeNodes(node,treeNode.getId());
					htNodeParent.put(node, parentNode);
					treeModel.reload(parentNode);
				}
			}
		}
	}

	public void addNodeForViewsAndNodes(NodeSummary ns , View oView){
		if((ns.getType()!= ICoreConstants.TRASHBIN)){
			if(ns instanceof View ){
				createViewsAndNodes();
				View view = (View)ns;
				view.initialize(oSession, oModel);
				if(!ns.getListenerList().contains(this)){
					ns.addPropertyChangeListener(this);
					addToViewListener(ns);
				}
				treeModel.reload(rootNode);
			} else {
				Vector parentNodes = getTreeNode(oView.getId());
				for (int i=0; i<parentNodes.size(); i++){
					DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) parentNodes.get(i);
					if(parentNode.getParent() != null && parentNode.getParent().equals(rootNode)){
						updateNodes(parentNode);
						treeModel.reload(parentNode);
						break;
					}
				}
			}
	 	}
	}
	/**
	 * Create and show the right-click node popup menu for the given node.
	 * @param node com.compendium.core.datamodel.NodeSummary, the node to create the popup for.
	 * @param x, the x position of the mouse event that triggered this request.
	 * @param y, the y position of the mouse event that triggered this request.
	 */
	public UIViewOutlinePopupMenu showPopupMenu(NodeSummary node, int x, int y, boolean isLevelOneNode) {

		UIViewOutlinePopupMenu popup = new UIViewOutlinePopupMenu(LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "UIViewOutline.popupMenu"), node,  this, isLevelOneNode); //$NON-NLS-1$
	   
	    Dimension dim = ProjectCompendium.APP.getScreenSize();
	    int screenWidth = dim.width - 50; //to accomodate for the scrollbar
	    int screenHeight = dim.height ; //to accomodate for the menubar...

	    Point point = new Point(x,y);
	    int realX = Math.abs(point.x - getX())+ 50;
	    int realY = Math.abs(point.y - getY())+ 20;

	    int endXCoordForPopUpMenu = realX + popup.getWidth();
	    int endYCoordForPopUpMenu = realY + popup.getHeight();

	    int offsetX = (screenWidth) - endXCoordForPopUpMenu;
	    int offsetY = (screenHeight) - endYCoordForPopUpMenu;

	    if(offsetX > 0)
		offsetX = 0;
	    if(offsetY > 0)
		offsetY = 0;

	  //  popup.setCoordinates(realX+offsetX, realY+offsetY);
	  //   popup.show(tree, realX+offsetX, realY+offsetY);
	    popup.setCoordinates(point.x  + 50 , point.y);
	    popup.show(tree, point.x +50 , point.y);	   

	    return popup;
	   
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
				
		if(prop.equals(NodeSummary.STATE_PROPERTY)){
			NodeSummary ns = (NodeSummary) obj;
			int newState = ((Integer) newvalue).intValue();
			Vector nodes = getTreeNode(ns.getId());
			for(int i =0; i <nodes.size(); i++){
				DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) nodes.get(i);
				if(treeNode != rootNode.getFirstChild()){
					UIViewOutlineTreeNode outlineTreeNode = (UIViewOutlineTreeNode) treeNode.getUserObject();
					outlineTreeNode.setState(newState);
				}
			}
			refreshTree();
		} else
		if(prop.equals(View.CHILDREN_PROPERTY)){
		} else if(((prop.equals(View.LINK_REMOVED)|| prop.equals(View.LINK_ADDED))||prop.equals(View.NODE_REMOVED))){
			View oView = (View) obj;
			if (sMode.equals(DISPLAY_VIEWS_AND_NODES)) {
				if(prop.equals(View.NODE_REMOVED)){
					NodeSummary ns = (NodeSummary) newvalue;
					removeNodeForViewsAndNodes(ns);	
				}
				
				Vector parentNodes = getTreeNode(oView.getId());
				if  (parentNodes == null || parentNodes.size() <= 0){
					return;
				}
				// Update the parent view.
				for (int i=0; i<parentNodes.size(); i++){
					DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) parentNodes.get(i);
					TreeNode[] path = parentNode.getPath();
					TreePath parentPath = new TreePath(path);
					if ((parentNode.getParent() != null) && 
					  (parentNode.getParent().equals(rootNode)) && 
					   tree.isExpanded(parentPath)) {
						
						updateNodes(parentNode);
						treeModel.reload(parentNode);
					}
				}
			} else if ((sMode.equals(DISPLAY_VIEWS_ONLY)) && prop.equals(View.NODE_REMOVED)){
				NodeSummary ns = (NodeSummary) newvalue; 
				if(View.isViewType(ns.getType())) {
					if(prop.equals(View.NODE_REMOVED)){
						removeNodeForViewsOnly(ns, oView);
					}
				}
			}
		} else  if(prop.equals(NodeSummary.LABEL_PROPERTY)){
			NodeSummary ns = (NodeSummary) obj;
			if(sMode.equals(DISPLAY_VIEWS_AND_NODES) ||
	    	   	(sMode.equals(DISPLAY_VIEWS_ONLY) && 
	    	   	View.isViewType(ns.getType()))) {
				Vector vtNodes = getTreeNode(ns.getId());
				for(int i=0; i< vtNodes.size(); i++){
					DefaultMutableTreeNode node = (DefaultMutableTreeNode)vtNodes.get(i);
					if(node.getUserObject() instanceof UIViewOutlineTreeNode){
						((UIViewOutlineTreeNode)node.getUserObject()).setLabel(newvalue.toString());
					}
					treeModel.reload(node);
				}
			}
		} else if(prop.equals(View.NODE_ADDED)) {
			NodePosition np = (NodePosition) newvalue;
			View oView = np.getView();
			
			NodeSummary ns = np.getNode();
			if(sMode.equals(DISPLAY_VIEWS_AND_NODES)){
				addNodeForViewsAndNodes(ns, oView);
			} else if (sMode.equals(DISPLAY_VIEWS_ONLY) && View.isViewType(ns.getType())) {
				addNodeForViewsOnly(ns, oView);
    		}
			
		}else if (prop.equals(View.NODE_TRANSCLUDED)){
			NodePosition np = (NodePosition) newvalue;
			View oView = (View)obj;
			NodeSummary ns = np.getNode();
			
    		if(sMode.equals(DISPLAY_VIEWS_AND_NODES)){
    			if((ns.getType()!= ICoreConstants.TRASHBIN)){
    				Vector v = getTreeNode(ns.getId());
    				Vector parentNodes = getTreeNode(oView.getId());
    			/*	
    				if(ns instanceof View ){
    					if(v.size() == 0){
	    					createViewsAndNodes();
	    					View view = (View)ns;
	    					view.initialize(oSession, oModel);
	    					if(!ns.getListenerList().contains(this)){
	    						ns.addPropertyChangeListener(this);
	    						addToViewListener(ns);
	    					}
	    					treeModel.reload(rootNode);
    					}
    				} */
					for (int i = 0; i < parentNodes.size(); i++){
						DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) parentNodes.get(i);
						TreeNode[] path = parentNode.getPath();
						TreePath treePath = new TreePath(path);
						if(parentNode.getParent() != null && parentNode.getParent().equals(rootNode)){
							if(v == null || v.size() == 0){
								updateNodes(parentNode);
								treeModel.reload(parentNode);
								tree.repaint();
							}
							for(int j = 0; j < v.size(); j++){
								DefaultMutableTreeNode child = (DefaultMutableTreeNode) v.get(j);
								if((!parentNode.isNodeDescendant(child)) && tree.isExpanded(treePath)){
									updateNodes(parentNode);
									treeModel.reload(parentNode);
									tree.repaint();
									//tree.collapsePath(treePath);
								}
							}
						}
					}
					
				}
    		} else if (sMode.equals(DISPLAY_VIEWS_ONLY) && View.isViewType(ns.getType())){
				//System.out.println("IN NODE TRANSCLUDED "+ns.getLabel()+ ", type:" +ns.getType());
				if((ns.getType() == ICoreConstants.TRASHBIN)){
					return ;
				}
				Vector nodes = getTreeNode(ns.getId());
				Vector parentIds = new Vector();
				//	check if the newly added node is in other views
				for(int i=0;i< nodes.size(); i++){
					DefaultMutableTreeNode child = (DefaultMutableTreeNode)nodes.get(i);
					DefaultMutableTreeNode parent = (DefaultMutableTreeNode) child.getParent();
					String parentId = ((UIViewOutlineTreeNode)parent.getUserObject()).getId(); 
					parentIds.add(parentId);
				}
					
				//Just to make sure the newly node isn't in the current view
				if(!parentIds.contains(oView.getId())){
					Vector parentNodes = getTreeNode(oView.getId());
					for (int i=0; i<parentNodes.size(); i++){
						DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) parentNodes.get(i);
						UIViewOutlineTreeNode treeNode = new UIViewOutlineTreeNode(ns);
						DefaultMutableTreeNode node = new DefaultMutableTreeNode(treeNode);
						
						if(!ns.getListenerList().contains(this)){
							ns.addPropertyChangeListener(this);
						}
						
						parentNode.add(node);
						addToTreeNodes(node,treeNode.getId());
						if(View.isViewType(ns.getType())){
							createViewNodes(node, true);
							treeModel.reload(node);
							
						}
						htNodeParent.put(node, parentNode);
						treeModel.reload(parentNode);
					}
				}
			}
		} else if (prop.equals(NodeSummary.SOURCE_PROPERTY)){
			NodeSummary ns = (NodeSummary) obj;
			
			if(sMode.equals(DISPLAY_VIEWS_AND_NODES) ||
	    	   	(sMode.equals(DISPLAY_VIEWS_ONLY) && View.isViewType(ns.getType()))) {
				Vector vtNodes = getTreeNode(ns.getId());
				for(int i=0; i< vtNodes.size(); i++){
					DefaultMutableTreeNode node = (DefaultMutableTreeNode)vtNodes.get(i);
					if(node.getUserObject() instanceof UIViewOutlineTreeNode){
						((UIViewOutlineTreeNode)node.getUserObject()).setReference(newvalue.toString());
					}
					treeModel.reload(node);
				}
			}
		} else if (prop.equals(NodeSummary.NODE_TYPE_PROPERTY)){
			
			NodeSummary ns = (NodeSummary) obj;
			int newType = Integer.parseInt(newvalue.toString());
			int oldType = Integer.parseInt(oldvalue.toString());
			
			Vector vtNodes = getTreeNode(ns.getId());
			try {
				// if the old type is different from the new type, obtain the nodesummary
				// from the database as it is not update in NodeSummary list yet.
			
				NodeSummary nodeSum = oModel.getNodeService().getNodeSummary(oSession, ns.getId());
				nodeSum.initialize(oSession, oModel);
				if(sMode.equals(DISPLAY_VIEWS_AND_NODES)){
					if(View.isViewType(newType)){
						for(int i=0; i< vtNodes.size(); i++){
						DefaultMutableTreeNode node = (DefaultMutableTreeNode)vtNodes.get(i);
						UIViewOutlineTreeNode treeNode = ((UIViewOutlineTreeNode)node.getUserObject());
						if(!View.isViewType(treeNode.getType())){
							createViewsAndNodes();
							treeModel.reload(rootNode);
							
							for (;e.hasMoreElements();){
		    					TreePath path = (TreePath)e.nextElement();
		    					if(tree.getRowForPath(path) != -1)
		    						tree.expandPath(path);
		    				}
							
							break;
							
						} else {
							treeNode.setObject(nodeSum);
							treeNode.setType(newType);
							treeModel.reload(node);
						}
					}
					
					if(vtNodes.size() == 0){
						createViewsAndNodes();
						treeModel.reload(rootNode);
					}
					if(!nodeSum.getListenerList().contains(this)){
						nodeSum.addPropertyChangeListener(this);
						addToViewListener(nodeSum);
					}
					tree.repaint();
					
				} else {
					for(int i=0; i< vtNodes.size(); i++){
						DefaultMutableTreeNode node = (DefaultMutableTreeNode)vtNodes.get(i);
						UIViewOutlineTreeNode treeNode = ((UIViewOutlineTreeNode)node.getUserObject());
						//System.out.println(treeNode.getType());
						if(node.getParent() != null && node.getParent().equals(rootNode)){
							removeChildNodes(node);
							rootNode.remove(node);
							treeModel.reload(node);
						} else {
							treeNode.setObject(nodeSum);
							treeNode.setType(newType);
							treeModel.reload(node);
						}
					}
					tree.repaint();
				}
			} else if(sMode.equals(DISPLAY_VIEWS_ONLY)){
				
					// if the new type is map / list 
					if(View.isViewType(newType)){
						for(int i=0; i< vtNodes.size(); i++){
							DefaultMutableTreeNode node = (DefaultMutableTreeNode)vtNodes.get(i);
							UIViewOutlineTreeNode treeNode = ((UIViewOutlineTreeNode)node.getUserObject());
							if(treeNode.getType() != newType){
								if (sMode.equals(DISPLAY_VIEWS_ONLY)) {
									treeNode.setObject(nodeSum);
									treeNode.setType(newType);
									if(!View.isViewType(oldType)){
										DefaultMutableTreeNode parent = (DefaultMutableTreeNode)htNodeParent.get(node);
										parent.add(node);
										treeModel.reload(parent);
									} 
								} else {
									treeNode.setObject(nodeSum);
									treeNode.setType(newType);
									treeModel.reload(node);
								}
							}
						}
						if(!nodeSum.getListenerList().contains(this)){
							nodeSum.addPropertyChangeListener(this);
							addToViewListener(nodeSum);
						}
					} else {
						for(int i=0; i< vtNodes.size(); i++){
							DefaultMutableTreeNode node = (DefaultMutableTreeNode)vtNodes.get(i);
							UIViewOutlineTreeNode treeNode = ((UIViewOutlineTreeNode)node.getUserObject());
							if(treeNode.getType() != newType){
								if (sMode.equals(DISPLAY_VIEWS_ONLY)) {
									treeNode.setObject(nodeSum);
									treeNode.setType(newType);
									if(View.isViewType(oldType)){
										DefaultMutableTreeNode parent = (DefaultMutableTreeNode)node.getParent();
										parent.remove(node);
										//removeTreeNode(node);
										htNodeParent.put(node, parent);
										treeModel.reload(parent);
									} 
								} else {
									treeNode.setObject(nodeSum);
									treeNode.setType(newType);
									treeModel.reload(node);
								}
							}
						}
						if(!nodeSum.getListenerList().contains(this)){
							nodeSum.addPropertyChangeListener(this);
							addToViewListener(nodeSum);
						}
					}
				}
				tree.repaint();
			} catch (SQLException ex) {
				ex.printStackTrace();
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
	}
	
	/**
	 * Handles the tree will expand events
	 * @param evt, the associated TreeExpansionEvent object.
	 */
		public void treeWillExpand(TreeExpansionEvent evt) throws ExpandVetoException  {
			ProjectCompendium.APP.setWaitCursor();
			
			TreePath selPath = evt.getPath();
			if(selPath != null ){
				if(sMode.equals(DISPLAY_VIEWS_AND_NODES)) {
					TreePath rootPath = tree.getPathForRow(0);
					if(selPath != rootPath){
						DefaultMutableTreeNode node = (DefaultMutableTreeNode)selPath.getLastPathComponent();
						NodeSummary ns = ((UIViewOutlineTreeNode)node.getUserObject()).getObject();
						
						if((node != null &&  node.getParent() !=null) && node.getParent().equals(rootNode)){
							updateNodes(node);
							treeModel.reload(node);
						}
					} else if(selPath == rootPath) {
						createViewsAndNodes();
						treeModel.reload();
					}
				} else if(sMode.equals(DISPLAY_VIEWS_ONLY)) {
					DefaultMutableTreeNode node = (DefaultMutableTreeNode)selPath.getLastPathComponent();
					node = createViewNodes (node, true);
					treeModel.reload(node);
				}
			}
			ProjectCompendium.APP.setDefaultCursor();
		}

		/**
		 * Handles the tree will collapse events
		 * @param evt, the associated TreeExpansionEvent object.
		 */
		public void treeWillCollapse(TreeExpansionEvent evt) throws ExpandVetoException {
		}
		
		
		/**
		 *  Called whenever the value of the tree selection changes.
		 */
		public void valueChanged(TreeSelectionEvent arg0) {
			TreePath path = arg0.getPath();
			DefaultMutableTreeNode node = (DefaultMutableTreeNode)path.getLastPathComponent();
			selectedNode = ((UIViewOutlineTreeNode) node.getUserObject());
			selectedNodeSummary = selectedNode.getObject();
			
			UIViewOutlineTreeNode treeNode = (UIViewOutlineTreeNode)(node.getUserObject());
			if(treeNode.getObject() != null )	
				setStatus(treeNode.getObject());
	    	else 
		       	ProjectCompendium.APP.setStatus(""); //$NON-NLS-1$
			
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
		 *	This class configures an editor in a tree.
		 * @author Lakshmi Prabhakaran
		 *
		 */
		private class TreeNodeEditor extends DefaultTreeCellEditor {
			
			UIViewOutlineTreeNode treeNode = null;
			
			/** constructor **/
			public TreeNodeEditor(JTree tree, DefaultTreeCellRenderer cellRenderer) {
	        	super(tree, cellRenderer);
	        	super.renderer = cellRenderer;
	        }
			/**
			 * 
			 */
			public Component getTreeCellEditorComponent(JTree tree,
								                    Object value,
								                    boolean isSelected,
								                    boolean expanded,
								                    boolean leaf,
								                    int row){
				Icon imageIcon = renderer.getDefaultClosedIcon();
				Component c = super.getTreeCellEditorComponent(tree, value, isSelected,
                        expanded, leaf, row);
        
		        DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
		        		        
		        treeNode = (UIViewOutlineTreeNode)(node.getUserObject());
		        
		        String  toolTip = treeNode.getLabel();
		        int type = treeNode.getType();
		        int state = treeNode.getState();
		        NodeSummary nodeSum = treeNode.getObject();
		        		       		        
		        ((JTextField)editingComponent).setEditable(true);
		        ((JTextField)editingComponent).setFont(ProjectCompendium.APP.currentDefaultFont);
			    //	set uneditable for project name and home window cell
			    if(node.equals(rootNode) || (node.equals(rootNode.getFirstChild()))){
			    	((JTextField)editingComponent).setEditable(false);
			    } else if (nodeSum != null && nodeSum.getId().equals(ProjectCompendium.APP.getInBoxID())) {
			    	((JTextField)editingComponent).setEditable(false);			    	
				} else {
			    	((JTextField)editingComponent).addFocusListener(new FocusAdapter(){
			    		public void focusGained(FocusEvent arg0) {
							if( treeNode.getObject() != null){
								setStatus(treeNode.getObject());
							}else{
								ProjectCompendium.APP.setStatus(""); //$NON-NLS-1$
							}
							
						}
						public void focusLost(FocusEvent arg0) {
							ProjectCompendium.APP.setStatus(""); //$NON-NLS-1$
						}
			    	});
			    } 
		       				    
			    if (type == ICoreConstants.REFERENCE) {
			    	imageIcon = UINode.getReferenceImageSmall(treeNode.getReference());
			    } else {
			    	imageIcon = UINode.getNodeImageSmall(type);
			    }
	            
	        	if (treeNode.getObject() != null) {
	        		if(treeNode.getObject().getId().equals(ProjectCompendium.APP.getInBoxID())) {
	        			imageIcon = UIImages.get(IUIConstants.INBOX_SM);
	        		}
	        	}
			    
		        setToolTipText(toolTip);
		        super.editingIcon = imageIcon;
		        
		        // set border color	
		        setBorder(new NodeBorder(state, imageIcon));
		        
		        return c;
		        
			}
			
		
		}
		
		/**
		 * The tree Model listener for the tree associated with the outer class 
		 * @author Lakshmi Prabhakaran
		 *
		 */
		private class OutlineTreeModelListener implements TreeModelListener {

			public OutlineTreeModelListener(){}
			
			/**
			 * Handles Tree nodes changed event
			 * @param e, the event associated with TreeModelEvent
			 */
			public void treeNodesChanged(TreeModelEvent e) {
				
				DefaultMutableTreeNode node;
				DefaultMutableTreeNode parent = (DefaultMutableTreeNode) (e.getTreePath().getLastPathComponent());
				try {
			            int index = e.getChildIndices()[0];
			            
			            node = (DefaultMutableTreeNode) (parent.getChildAt(index));
			            
			            String newvalue = node.getUserObject().toString();
			            
			            node.setUserObject(selectedNode);
			           
			            selectedNodeSummary.setLabel(newvalue, sAuthor);
			           
			            treeModel.reload(node.getParent());
			            tree.repaint();
			       
			        } catch (Exception ex) {
			        	ex.printStackTrace();
					}
					
			}
		
			public void treeNodesInserted(TreeModelEvent arg0) {
			}

			public void treeNodesRemoved(TreeModelEvent arg0) {
			}

			public void treeStructureChanged(TreeModelEvent arg0) {
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

		/**
		 * @return Returns the sAuthor.
		 */
		public String getAuthor() {
			return sAuthor;
		}

		/**
		 * @param author The sAuthor to set.
		 */
		public void setAuthor(String author) {
			sAuthor = author;
		}

		/**
		 * @return Returns the selectedNodeSummary.
		 */
		public NodeSummary getSelectedNodeSummary() {
			return selectedNodeSummary;
		}
		
		/**
		 * @return Returns the selectedView.
		 */
		public NodeSummary getSelectedView() {
			return selectedView;
		}

		public void actionPerformed(ActionEvent arg0) {
			Object source = arg0.getSource();
			if (source == pbCancel) {
				ProjectCompendium.APP.getMenuManager().removeOutlineView(true);
			}
		}
		
		/** 
		 * Remove all listeners before closing view
		 *
		 */
		public void cleanUp(){
			for(Enumeration e = htTreeNodes.keys(); e.hasMoreElements();){
				DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) e.nextElement();
				UIViewOutlineTreeNode  outlineNode = (UIViewOutlineTreeNode) treeNode.getUserObject();
				NodeSummary node = outlineNode.getObject();
				if(node.getListenerList().contains(this)){
					node.removePropertyChangeListener(this);
				}
			}
		}

		public void treeExpanded(TreeExpansionEvent arg0) {
			updateTreeSelection();
		}

		public void treeCollapsed(TreeExpansionEvent arg0) {
			updateTreeSelection();
		}	
}