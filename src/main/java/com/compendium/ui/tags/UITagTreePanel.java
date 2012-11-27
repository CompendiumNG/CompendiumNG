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

import java.io.IOException;
import java.util.*;
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.dnd.*;
import java.awt.event.*;
import java.sql.SQLException;

import javax.swing.*;
import javax.swing.tree.*;
import javax.swing.event.*;
import javax.swing.border.*;
import javax.swing.plaf.basic.BasicTreeUI;

import com.compendium.*;
import com.compendium.ui.*;
import com.compendium.ui.plaf.ViewPaneUI;

import com.compendium.core.CoreUtilities;
import com.compendium.core.ICoreConstants;
import com.compendium.core.datamodel.*;

import com.compendium.meeting.*;

/**
 * Draws the panel for assigning codes.
 *
 * @author	Mohammed Sajid Ali / Michelle Bachler
 */
public class UITagTreePanel extends JPanel implements ActionListener, ListSelectionListener, DragSourceListener, 
											DragGestureListener, DropTargetListener, Transferable{

	/** The scrollpane for the code tree.*/
	private JScrollPane		sp					= null;

	/** The label for the tree of codes.*/
	public  JLabel			lblCodesList		= null;

	/** The button to cancel the parnt dialog.*/
	private UIButton		pbCancel			= null;

	/** The button to expand all folder in the tree.*/
	private UIButton		pbExpand			= null;

	/** The button the to open the relevant help.*/
	public	UIButton		pbHelp				= null;

	/** The button to add a new tag.*/
	private UIButton		pbNewTag			= null;

	/** The button to add a new Group.*/
	private UIButton		pbNewGroup			= null;

	/** The button to move the working area from right to bottom left and back.*/
	private UIButton		pbMove				= null;

	/** The list of nodes for the current code.*/
	private Vector				oNodes 				= new Vector();

	/** The working list of nodes to tag.*/
	private UIWorkingList		oWorkingList		= null;
	
	/** The scrollpane that the list is in.*/
	private JScrollPane 		oWorkingScroll		= null;
	
	/** The label for the list.*/
	private JLabel 				lblViews			= null;

	/** The labe for the nodes working area.*/
	private JLabel 				lblNodes			= null;

	/** The button to deselect all nodes in the lists.*/
	private UIButton			pbDeselectAll			= null;	

	/** The button to insert the selected nodes into the current view.*/
	private UIButton			pbInsert			= null;	

	/** The main panel holding the list and tree.*/
	private JPanel			centerpanel			= null;

	/** The textfield to enter new code / group names.*/
	private JTextField		tfNewCode		= null;	
	
	/** the JTree holding the code and code grouping information to assign codes from.*/
	private JTree				tree			= null;

	/** The tree model.*/
	private DefaultTreeModel	treemodel		= null;

	/** The top node of the tree contents.*/
	private DefaultMutableTreeNode top			= null;
	
	/** The current Model.*/
	private IModel 				model			= null;		
	
	private UITagTreePanel		me				= null;
		
	/** The DragSource object associated with this draggable item.*/
	private DragSource 			dragSource;
	
	private boolean 	isFilter = false;
	
    private int hotspot = new JCheckBox().getPreferredSize().width; 
    
    /** The main split pain.*/
    private JSplitPane oSplitter = null;
    
	/** The data flavors supported by this class.*/
    public static final 		DataFlavor[] supportedFlavors = { null };
	static    {
		try { supportedFlavors[0] = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType+"; class=com.compendium.ui.tags.UITagTreePanel", null); } //$NON-NLS-1$
		catch (Exception ex) { ex.printStackTrace(); }
	}
	
	private int nOrientation = JSplitPane.VERTICAL_SPLIT;

	/** Holds a list of all the currently displayed tree renderers.*/
	private Hashtable htAllRenderers = new Hashtable(51);
	
	
	/** The row to have a yellow border*/
	private int highlightRow = -2;
	
	/**
	 * Constructor.
	 */
	public UITagTreePanel() {
		super();
		model = ProjectCompendium.APP.getModel();		
		showCodePanel();	
		me = this;
		updateSelectionListView();	
		tfNewCode.requestFocus();
	}

	/**
	 * Draw the panel contents.
	 */
	private void showCodePanel() {
		
		setLayout(new BorderLayout());
		setFont(ProjectCompendium.APP.currentDefaultFont);
		
		JPanel leftpanel = new JPanel(new BorderLayout());
		
		// TOP PANEL
		JPanel toppanel = createTopPanel();
		leftpanel.add(toppanel, BorderLayout.NORTH);
		
		String sOrientation = FormatProperties.getFormatProp("tagsViewOrientation"); //$NON-NLS-1$
		if (sOrientation != null && sOrientation.equals("horizontal")) { //$NON-NLS-1$
			nOrientation = JSplitPane.HORIZONTAL_SPLIT;
		}
		
		// MAIN PANEL
		centerpanel = new JPanel(new BorderLayout());
		sp = createTree();
		sp.addMouseListener(oWorkingList);
		tree.addMouseListener(new MouseAdapter() {
			public void mouseEntered(MouseEvent e) {
				oWorkingList.mouseClicked(e);
			}
		});		
		centerpanel.add(sp, BorderLayout.CENTER);

		JPanel buttonpanel = new JPanel();
		pbExpand = new UIButton(LanguageProperties.getString(LanguageProperties.TAGS_BUNDLE, "UITagTreePanel.expandallButton")); //$NON-NLS-1$
		pbExpand.setMnemonic(LanguageProperties.getString(LanguageProperties.TAGS_BUNDLE, "UITagTreePanel.expandallButtonMnemonic").charAt(0));		
		pbExpand.addActionListener(this);
		pbExpand.setEnabled(true);
		buttonpanel.add(pbExpand);
		centerpanel.add(buttonpanel, BorderLayout.SOUTH);

		leftpanel.add(centerpanel, BorderLayout.CENTER);
		
		JPanel rightpanel = createUsagePanel();

		oSplitter = new JSplitPane(nOrientation, true, leftpanel, rightpanel) {
			boolean isPainted = false;
			boolean hasProportionalLocation = false;
			double proportionalLocation;
			public void setDividerLocation(double proportionalLocation) {
				if (!isPainted) {
					hasProportionalLocation = true;
			        this.proportionalLocation = proportionalLocation;
			    } else {
			    	super.setDividerLocation(proportionalLocation);
			    }
			}
			public void paint(Graphics g) {
				if (!isPainted) {
					if (hasProportionalLocation) {
						super.setDividerLocation(proportionalLocation);
					}
			        isPainted = true;
			    }
			    super.paint(g);
			}			
		};
		oSplitter.setOneTouchExpandable(true);
		oSplitter.setDividerSize(8);
		oSplitter.setContinuousLayout(true);
		oSplitter.setMinimumSize(new Dimension(200, oSplitter.getPreferredSize().height));
		oSplitter.setDividerLocation(0.5);

		add(oSplitter, BorderLayout.CENTER);
		add(createButtonPanel(), BorderLayout.SOUTH);

		addFocusListener( new FocusListener() {
        	public void focusGained(FocusEvent e) {
				tree.requestFocus();
			}
            public void focusLost(FocusEvent e) {

			}
		});

		validate();		
	}
	
	private JPanel createUsagePanel() {
		JPanel panel = new JPanel(new BorderLayout());

		// Add labels
		JPanel labelpanel = new JPanel(new BorderLayout());
		
		JPanel panel1 = new JPanel(new BorderLayout());
		panel1.setBorder(new EmptyBorder(5,5,0,5));		
		lblViews = new JLabel(LanguageProperties.getString(LanguageProperties.TAGS_BUNDLE, "UITagTreePanel.workingarea")); //$NON-NLS-1$
		lblViews.setAlignmentX(JLabel.LEFT_ALIGNMENT);
		panel1.add(lblViews, BorderLayout.CENTER);
		
		JPanel panel2 = new JPanel(new BorderLayout());
		panel2.setBorder(new EmptyBorder(5,5,5,5));		
		lblNodes = new JLabel(LanguageProperties.getString(LanguageProperties.TAGS_BUNDLE, "UITagTreePanel.nodes")); //$NON-NLS-1$
		lblNodes.setAlignmentX(JLabel.LEFT_ALIGNMENT);		
		panel2.add(lblNodes, BorderLayout.CENTER);
		
		labelpanel.add(panel1, BorderLayout.NORTH);
		labelpanel.add(panel2, BorderLayout.CENTER);
		
		oWorkingList = new UIWorkingList();
		oWorkingList.getList().getSelectionModel().addListSelectionListener(this);
		
		oWorkingScroll = new JScrollPane(oWorkingList.getList());
		oWorkingScroll.addMouseListener(oWorkingList);
		
		//if (this.nOrientation == JSplitPane.HORIZONTAL_SPLIT) {
			Dimension size = tree.getPreferredSize();
			oWorkingScroll.setPreferredSize(new Dimension(250, size.height));
		//} else {
		//	oWorkingScroll.setPreferredSize(new Dimension(250, 250));			
		//}
				
		JPanel buttonpanel = new JPanel();
		
		pbInsert = new UIButton(LanguageProperties.getString(LanguageProperties.TAGS_BUNDLE, "UITagTreePanel.insertButton")); //$NON-NLS-1$
		pbInsert.setMnemonic(LanguageProperties.getString(LanguageProperties.TAGS_BUNDLE, "UITagTreePanel.insertButtonMnemonic").charAt(0));
		pbInsert.addActionListener(this);
		buttonpanel.add(pbInsert);
		
		pbDeselectAll = new UIButton(LanguageProperties.getString(LanguageProperties.TAGS_BUNDLE, "UITagTreePanel.deselectallButton")); //$NON-NLS-1$
		pbDeselectAll.setMnemonic(LanguageProperties.getString(LanguageProperties.TAGS_BUNDLE, "UITagTreePanel.deselectallButtonMnemonic").charAt(0));		
		pbDeselectAll.addActionListener(this);
		buttonpanel.add(pbDeselectAll);

		//buttonpanel.add(pbSelectAll);
		buttonpanel.add(pbDeselectAll);
		
		panel.add(labelpanel, BorderLayout.NORTH);		
		panel.add(oWorkingScroll, BorderLayout.CENTER);		
		panel.add(buttonpanel, BorderLayout.SOUTH);
		
		return panel;
	}
	
	private JPanel createTopPanel() {
		JPanel toppanel = new JPanel(new BorderLayout());
		
		JPanel newpanel = new JPanel(new BorderLayout());
		newpanel.setBorder(new EmptyBorder(5,5,0,5));
		
		tfNewCode = new JTextField(""); //$NON-NLS-1$
		tfNewCode.setColumns(20);
		tfNewCode.setMargin(new Insets(2,2,2,2));
		newpanel.add(tfNewCode, BorderLayout.CENTER);
		tfNewCode.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {	
				int keyCode = e.getKeyCode();
				int modifiers = e.getModifiers();
				if (keyCode == KeyEvent.VK_ENTER && modifiers == 0) {
					onAddTags(tfNewCode.getText());
				}
			}
		});
		
		JPanel buttonpanel = new JPanel();
	
		pbNewTag = new UIButton(LanguageProperties.getString(LanguageProperties.TAGS_BUNDLE, "UITagTreePanel.maketagButton")){ public boolean hasFocus(){ return true; }}; //$NON-NLS-1$
		pbNewTag.setToolTipText(LanguageProperties.getString(LanguageProperties.TAGS_BUNDLE, "UITagTreePanel.maketagButtonTip"));		 //$NON-NLS-1$
		pbNewTag.setMnemonic(LanguageProperties.getString(LanguageProperties.TAGS_BUNDLE, "UITagTreePanel.maketagButtonMnemonic").charAt(0));		
		pbNewTag.addActionListener(this);
		pbNewTag.setFocusPainted(true);
		buttonpanel.add(pbNewTag);

		pbNewGroup = new UIButton(LanguageProperties.getString(LanguageProperties.TAGS_BUNDLE, "UITagTreePanel.makegroupButton")); //$NON-NLS-1$
		pbNewGroup.setToolTipText(LanguageProperties.getString(LanguageProperties.TAGS_BUNDLE, "UITagTreePanel.makegroupButtonTip"));				 //$NON-NLS-1$
		pbNewGroup.setMnemonic(LanguageProperties.getString(LanguageProperties.TAGS_BUNDLE, "UITagTreePanel.makegroupButtonMnemonic").charAt(0));
		pbNewGroup.addActionListener(this);
		buttonpanel.add(pbNewGroup);

		toppanel.add(newpanel, BorderLayout.CENTER);		
		toppanel.add(buttonpanel, BorderLayout.SOUTH);
						
		return toppanel;
	}

	/**
	 * Create and return the button panel.
	 */
	private JPanel createButtonPanel() {

		JPanel oButtonPanel = new JPanel();

		pbCancel = new UIButton(LanguageProperties.getString(LanguageProperties.TAGS_BUNDLE, "UITagTreePanel.closeButton")); //$NON-NLS-1$
		pbCancel.setMnemonic(LanguageProperties.getString(LanguageProperties.TAGS_BUNDLE, "UITagTreePanel.closeButtonMnemonic").charAt(0));
		pbCancel.addActionListener(this);
		oButtonPanel.add(pbCancel);

		String sOrientation = FormatProperties.getFormatProp("tagsViewOrientation"); //$NON-NLS-1$
		if (sOrientation == null || (!sOrientation.equals("vertical") && !sOrientation.equals("horizontal"))) { //$NON-NLS-1$ //$NON-NLS-2$
			sOrientation = "vertical"; //$NON-NLS-1$
		} 
		
		if (nOrientation == JSplitPane.HORIZONTAL_SPLIT)	{
			pbMove = new UIButton(LanguageProperties.getString(LanguageProperties.TAGS_BUNDLE, "UITagTreePanel.verticalsplitButton")); //$NON-NLS-1$
			pbMove.setToolTipText(LanguageProperties.getString(LanguageProperties.TAGS_BUNDLE, "UITagTreePanel.verticalsplitButtonTip"));		 //$NON-NLS-1$
			pbMove.setMnemonic(LanguageProperties.getString(LanguageProperties.TAGS_BUNDLE, "UITagTreePanel.verticalsplitButtonMnemonic").charAt(0));
			pbMove.addActionListener(this);
		} else  {
			pbMove = new UIButton(LanguageProperties.getString(LanguageProperties.TAGS_BUNDLE, "UITagTreePanel.horizontalsplitButton")); //$NON-NLS-1$
			pbMove.setToolTipText(LanguageProperties.getString(LanguageProperties.TAGS_BUNDLE, "UITagTreePanel.horizontalsplitButtonTip"));		 //$NON-NLS-1$
			pbMove.setMnemonic(LanguageProperties.getString(LanguageProperties.TAGS_BUNDLE, "UITagTreePanel.horizontalsplitButtonMnemonic").charAt(0));
			pbMove.addActionListener(this);
		}			

		oButtonPanel.add(pbMove);
		
		pbHelp = new UIButton(LanguageProperties.getString(LanguageProperties.TAGS_BUNDLE, "UITagTreePanel.helpButton")); //$NON-NLS-1$
		pbHelp.setMnemonic(LanguageProperties.getString(LanguageProperties.TAGS_BUNDLE, "UITagTreePanel.helpButtonMnemonic").charAt(0));
		ProjectCompendium.APP.mainHB.enableHelpOnButton(pbHelp, "tag.assignment", ProjectCompendium.APP.mainHS); //$NON-NLS-1$
		oButtonPanel.add(pbHelp);

		return oButtonPanel;
	}

	public void valueChanged(ListSelectionEvent e) {
		checkTagsForSelectedNodes();
    }
	
	public void hideHint() {
		if (oWorkingList != null) {
			oWorkingList.hideHint();
		}
	}
	
	/**
	 * Check the tags that correspond to the currently selected nodes in the working area. 
	 */
	private void checkTagsForSelectedNodes() {
		Hashtable selectedCodes = new Hashtable(51);
		Hashtable universalCodes = new Hashtable(51);
		
 		JTable table = oWorkingList.getList();
        int[] rows = table.getSelectedRows();
        
        int row = 0;
        NodeSummary node = null;
        Enumeration codes = null;
        Code code = null;
        String sCodeID = ""; //$NON-NLS-1$
        Integer num = null;
        for (int i=0; i<rows.length; i++) {
        	row = rows[i];
        	node = oWorkingList.getNodeAt(row); 
        	// If something has changed that drastically, abort.
        	if (node == null) {
        		return;
        	}
        	for (codes = node.getCodes(); codes.hasMoreElements(); ) {
        		code = (Code)codes.nextElement();
        		sCodeID = code.getId();
        		if (!selectedCodes.containsKey(sCodeID)) {
        			selectedCodes.put(sCodeID, code);
        		} 
        		if (!universalCodes.containsKey(code.getId())) {
        			universalCodes.put(sCodeID, new Integer(1));
        		} else {
        			num = (Integer)universalCodes.get(sCodeID);
        			int inum = num.intValue();
        			inum = inum+1;
        			universalCodes.put(sCodeID, new Integer(inum)); 
        		}

        	}
        }

        DefaultMutableTreeNode treenode = null;
        CheckNode check = null;
        int inum = 0;
        for (Enumeration e = top.preorderEnumeration(); e.hasMoreElements();) {
        	treenode = (DefaultMutableTreeNode)e.nextElement();        	
        	check = (CheckNode)treenode.getUserObject();
        	if (check.getData() instanceof Code) {
        		code = (Code)check.getData();
        		sCodeID = code.getId();
        		if (selectedCodes.containsKey(sCodeID)) {
               		if (universalCodes.containsKey(code.getId())) {
            			num = (Integer)universalCodes.get(sCodeID);
            			inum = num.intValue();
            		}        			
        			check.setChecked(true);  
        			if (inum == rows.length) {
        				check.setUniversal(true);
        			} else {
        				check.setUniversal(false);
        			}
        			DefaultMutableTreeNode parent = (DefaultMutableTreeNode)treenode.getParent();							  	    				
        			if (!tree.isExpanded(new TreePath(parent.getPath()))) {
        				tree.expandPath(new TreePath(parent.getPath()));
        			}
        			// tree.scrollPathToVisible(path);
        		} else {
        			check.setChecked(false);
        			check.setUniversal(false);
        		}
        	}
        }
	
        tree.validate();
        tree.repaint();
 	}
	
	/**
	 * Updates the list of nodes for the currently selected tags based on the selected group.
	 */
	private void updateFilterListGroupView() {
		
		pbDeselectAll.setText(LanguageProperties.getString(LanguageProperties.TAGS_BUNDLE, "UITagTreePanel.selectallButton")); //$NON-NLS-1$
		pbDeselectAll.setMnemonic(LanguageProperties.getString(LanguageProperties.TAGS_BUNDLE, "UITagTreePanel.selectallButtonMnemonic").charAt(0));		
		
		isFilter = true;		
		deselectInView();
		
		removeAllNodes();		
		
		Hashtable htNodesCheck = new Hashtable();
	
		Vector filterNodes = new Vector(51);								
		String userID = model.getUserProfile().getId();
		try {
			filterNodes = new Vector(51);						
			TreePath path = tree.getSelectionPath();
			if (path != null) {
				Code code = null;
				Vector nodes = null;
				NodeSummary node = null;
				DefaultMutableTreeNode treenode = null;	
				int countj = 0;
				String sNodeID = ""; //$NON-NLS-1$
	     		treenode = (DefaultMutableTreeNode)path.getLastPathComponent();
	     		if (treenode.getUserObject() instanceof CheckNode) {
	     			CheckNode check = (CheckNode)treenode.getUserObject();
	     			if (check.getData() instanceof Vector) {
	     				Vector group = (Vector)check.getData();
						Hashtable htCodeGroup = model.getCodeGroup((String)group.elementAt(0));
						Hashtable children = (Hashtable)htCodeGroup.get("children"); //$NON-NLS-1$
						int count = children.size();
						for (Enumeration e=children.elements(); e.hasMoreElements();) {
							code = (Code)e.nextElement();
			     			nodes = model.getCodeService().getNodes(model.getSession(), code.getId(), userID);
			     			countj = nodes.size();	     				     			
			     			for (int j=0; j< countj; j++) {
			     				node = (NodeSummary)nodes.elementAt(j);
			     				if (node != null) {
				     				node.initialize(model.getSession(), model);
				     				sNodeID = node.getId();
				     				if (!htNodesCheck.containsKey(sNodeID)) {
				     					htNodesCheck.put(sNodeID, node);	 
				     					filterNodes.addElement(node);
				     				}
			     				}
				     		}	
						}
	     			}
				}
			}
		}
		catch(Exception ex) {
			ex.printStackTrace();
			ProjectCompendium.APP.displayError(LanguageProperties.getString(LanguageProperties.TAGS_BUNDLE, "UITagTreePanel.errorusage")+":\n\n"+ex.getMessage()); //$NON-NLS-1$
		}
		
		if (filterNodes.size() == 0) {
			oNodes = filterNodes;		
			updateViewCount();
			oWorkingList.refreshTable(oNodes);				
		} else {
			filterNodes = CoreUtilities.sortList(filterNodes);
			oNodes = filterNodes;		
			updateViewCount();
			oWorkingList.refreshTable(oNodes);
		}
	}
	
	
	/**
	 * Updates the list of nodes for the currently selected tags.
	 */
	private void updateFilterListView() {
		
		pbDeselectAll.setText(LanguageProperties.getString(LanguageProperties.TAGS_BUNDLE, "UITagTreePanel.selectallButton")); //$NON-NLS-1$
		pbDeselectAll.setMnemonic(LanguageProperties.getString(LanguageProperties.TAGS_BUNDLE, "UITagTreePanel.selectallButtonMnemonic").charAt(0));		
		
		isFilter = true;		
		deselectInView();
		
		removeAllNodes();		
		
		Hashtable htNodesCheck = new Hashtable();
	
		Vector filterNodes = new Vector(51);								
		String userID = model.getUserProfile().getId();
		try {
			Vector allNodes = new Vector(51);		
			filterNodes = new Vector(51);						
			TreePath[] paths = tree.getSelectionPaths();
			if (paths != null) {
				Vector allVectors = new Vector(paths.length);
							
				TreePath path = null;
				Code code = null;
				Vector nodes = null;
				NodeSummary node = null;
				DefaultMutableTreeNode treenode = null;	
				int countj = 0;
				String sNodeID = ""; //$NON-NLS-1$
				for (int i=0; i<paths.length; i++) {
					path = paths[i];
		     		treenode = (DefaultMutableTreeNode)path.getLastPathComponent();
		     		if (treenode.getUserObject() instanceof CheckNode) {
		     			CheckNode check = (CheckNode)treenode.getUserObject();
		     			if (check.getData() instanceof Code) {
			     			code = (Code)check.getData();
			     			nodes = model.getCodeService().getNodes(model.getSession(), code.getId(), userID);
			     			countj = nodes.size();	     				     			
			     			for (int j=0; j< countj; j++) {
			     				node = (NodeSummary)nodes.elementAt(j);
			     				if (node != null) {
				     				node.initialize(model.getSession(), model);
				     				sNodeID = node.getId();
				     				if (i==0) {
				     					htNodesCheck.put(sNodeID, new Integer(1));	     					
				     				} else if (htNodesCheck.containsKey(sNodeID)) {
				     					Integer num = (Integer)htNodesCheck.get(sNodeID);	
				     					int iNum = num.intValue();
				     					iNum = iNum+1;
				     					htNodesCheck.put(sNodeID, new Integer(iNum));	  
				     				}
				     				if (!allNodes.contains(node)) {
				     					allNodes.addElement(node);
				     				}
			     				}
			     			}
		     			}
		     		}			
				}
				
				if (allNodes.size() == 0) {
					oNodes = filterNodes;		
					updateViewCount();
					oWorkingList.refreshTable(oNodes);				
					return;			
				}
				
				if (paths.length == 1) {
					filterNodes = allNodes;
				} else {			
					int count = allNodes.size();
					for (int i=0; i<count; i++) {
						node = (NodeSummary)allNodes.elementAt(i);
						sNodeID = node.getId();				
						if (htNodesCheck.containsKey(sNodeID)) {
							Integer num = (Integer)htNodesCheck.get(sNodeID);	
							int iNum = num.intValue();			
							if (iNum == paths.length) {
								filterNodes.addElement(node);
							}
						}
					}
				}
				
				filterNodes = CoreUtilities.sortList(filterNodes);
			}
		}
		catch(Exception ex) {
			ex.printStackTrace();
			ProjectCompendium.APP.displayError(LanguageProperties.getString(LanguageProperties.TAGS_BUNDLE, "UITagTreePanel.errorusage2")+":\n\n"+ex.getMessage()); //$NON-NLS-1$
		}

		oNodes = filterNodes;		
		updateViewCount();
		oWorkingList.refreshTable(oNodes);
	}

	private void deselectInView() {
		UIViewFrame frame = ProjectCompendium.APP.getCurrentFrame();		
		if (frame != null) {
			if (frame instanceof UIMapViewFrame) {
				UIMapViewFrame mapFrame = (UIMapViewFrame)frame;
				UIViewPane pane = mapFrame.getViewPane();
				if (pane != null) {
					pane.setSelectedNode(null, ICoreConstants.DESELECTALL);
					pane.setSelectedLink(null, ICoreConstants.DESELECTALL);
				}
			} else if (frame instanceof UIListViewFrame) {
				UIListViewFrame listFrame = (UIListViewFrame)frame;
				UIList list = listFrame.getUIList();
				if (list != null) {
					list.deselectAll();
				}				
			}
		}		
	}
	
	/**
	 * Updates the list of nodes for the currently selected nodes in the current view.
	 */
	private void updateSelectionListView() {
		isFilter = false;		
		removeAllNodes();				
		Vector allNodes = new Vector(51);				
		Hashtable htNodesCheck = new Hashtable();		
		UIViewFrame frame = ProjectCompendium.APP.getCurrentFrame();		
		if (frame != null) {
			if (frame instanceof UIMapViewFrame) {
				UIMapViewFrame mapFrame = (UIMapViewFrame)frame;
				UIViewPane pane = mapFrame.getViewPane();
				if (pane != null) {
					Enumeration e = pane.getSelectedNodes();
					UINode node = null;
					String sNodeID = ""; //$NON-NLS-1$
					for (Enumeration en=e; en.hasMoreElements();) {
						node = (UINode)en.nextElement();
						sNodeID = node.getNode().getId();
						if (!sNodeID.equals(ProjectCompendium.APP.getTrashBinID()) 
								&& !sNodeID.equals(ProjectCompendium.APP.getInBoxID())) {
							allNodes.addElement(node.getNode());
						}
					}
				}
				
			} else if (frame instanceof UIListViewFrame) {
				UIListViewFrame listFrame = (UIListViewFrame)frame;
				UIList list = listFrame.getUIList();
				if (list != null) {
					Enumeration e = list.getSelectedNodes();
					NodePosition nodePos = null; 
					for (Enumeration en=e; en.hasMoreElements();) {
						nodePos = (NodePosition)en.nextElement();
						allNodes.addElement(nodePos.getNode());
					}
				}				
			}
		}
				
		/*if (allNodes.size() == 0) {
			return;			
		}*/
					
		allNodes = CoreUtilities.sortList(allNodes);

		oNodes = allNodes;		
		updateViewCount();
		oWorkingList.refreshTable(oNodes);
	}
	
	/**
	 * Updates the number of occurences for the given node.
	 */
	public void updateViewCount() {
		if (isFilter) {
			lblNodes.setText(LanguageProperties.getString(LanguageProperties.TAGS_BUNDLE, "UITagTreePanel.filterednodes") +": "+ String.valueOf(oNodes.size())); //$NON-NLS-1$
		} else {
			lblNodes.setText(LanguageProperties.getString(LanguageProperties.TAGS_BUNDLE, "UITagTreePanel.selectednodes") +": "+ String.valueOf(oNodes.size())); //$NON-NLS-1$
		}
	}

	/**
	 * This is a convenience method to delete all the views in the hashtables and vectors.
	 */
	public void removeAllNodes() {

		((UITagsListTableModel)((TableSorter)oWorkingList.getList().getModel()).getModel()).removeAllElements();
		oNodes.removeAllElements();
		oWorkingList.getList().revalidate();
		oWorkingList.getList().repaint();
		oWorkingScroll.repaint();
	}
	
	/**
	 * Repaint the tree in reflection to a visual change liske setting the default group.
	 */
	public void refresh() {
		tree.validate();
		tree.repaint();
	}
	
	
	/**
	 * Update the codes and code groups displayed in the tree.
	 */
	public void updateTreeData() {
		
		model = ProjectCompendium.APP.getModel();		
		Enumeration e = tree.getExpandedDescendants(new TreePath(tree.getModel().getRoot()));
		
		top = getTreeData();
    	treemodel = new DefaultTreeModel(top);

		//tree.setLargeModel(true);

		tree.setRootVisible(false);
     	tree.setModel(treemodel);
		tree.repaint();
		sp.repaint();
     	     	
     	TreePath path = null;
     	String sGroupID = ""; //$NON-NLS-1$
     	DefaultMutableTreeNode node = null;
     	CheckNode check = null;
     	for (Enumeration en = e; en.hasMoreElements();) {
     		path = (TreePath)en.nextElement();
     		node = (DefaultMutableTreeNode)path.getLastPathComponent();
     		check = (CheckNode)node.getUserObject();
     		if (check.getData() instanceof Vector) {
     			sGroupID = (String)((Vector)check.getData()).elementAt(0);
     			node = findCodeGroup(sGroupID);
     			if (node != null) {
     				tree.expandPath(new TreePath(node.getPath()));
     			}
      		}
     	}
	}

	/**
	 * find the node for the given code group id
	 * @param sID the id of the node to find.
	 * @param DefaultMutableTreeNode the tree node if found else null.
	 */
    public DefaultMutableTreeNode findCodeGroup(String sID) {

		DefaultMutableTreeNode node = null;
        for (Enumeration e = top.preorderEnumeration(); e.hasMoreElements();) {
            node = (DefaultMutableTreeNode)e.nextElement();
            CheckNode check = (CheckNode)node.getUserObject();
            if (check.getData() instanceof Vector) {
            	Vector data = (Vector)check.getData();
            	String sGroupID = (String)data.elementAt(0);
            	if (sGroupID.equals(sID))
            		break;
            }
		}
    	return node;
	}

	/**
	 * Create the JTree of codes and code groupings.
	 */
	private JScrollPane createTree() {

		top = getTreeData();

		if (top != null) {

			// CREATE TREE
   	     	tree = new JTree(top);
   	    	treemodel = new DefaultTreeModel(top);
			tree.setRootVisible(false);
   	     	tree.setModel(treemodel);
   	     	tree.setFont(ProjectCompendium.APP.currentDefaultFont);
   	     	   	     	   	     	
   		    CheckBoxNodeRenderer renderer = new CheckBoxNodeRenderer();
   		    tree.setCellRenderer(renderer);

   		    tree.setCellEditor(new CheckBoxNodeEditor(tree));
   		    tree.setEditable(true);
 
   		    //UIDraggableTreeCellRenderer renderer = new UIDraggableTreeCellRenderer();   	     	
   	     	tree.setCellRenderer(renderer);
   			tree.setShowsRootHandles(true);   			
   	        tree.setToggleClickCount(4);   	          
   			tree.getSelectionModel().setSelectionMode
   	        	(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);

  	     	MouseListener[] mice = tree.getMouseListeners();
   	     	MouseListener mouse = null;
   	     	for( int i=0; i<mice.length; i++){
   	     		mouse = mice[i];
       			tree.removeMouseListener(mouse);
   	     	}   	        			
   			
   			dragSource = new DragSource();
   			dragSource.createDefaultDragGestureRecognizer((JComponent)tree, DnDConstants.ACTION_COPY_OR_MOVE, this);   	
  		    DropTarget dropTarget = new DropTarget((JComponent)tree, this);   
  	     	
    	    // Enable tool tips.
   			ToolTipManager.sharedInstance().registerComponent(tree);
   			
			tree.expandRow(0);
			
			/*tree.addFocusListener(new FocusListener() {
				public void focusGained(FocusEvent e) {
					tree.setSelectionRow(0);
				}				
				public void focusLost(FocusEvent e) {
					tree.clearSelection();
				}
			});*/
			
			MouseListener ml = new MouseAdapter() {
     			public void mouseReleased(MouseEvent e) {
					boolean isRightMouse = SwingUtilities.isRightMouseButton(e);
					boolean isLeftMouse = SwingUtilities.isLeftMouseButton(e);
					if (ProjectCompendium.isMac &&
						(e.getButton() == 3 && e.isShiftDown())) {
						isRightMouse = true;
						isLeftMouse = false;
					}
					     									
					if (tree.isEditing()) {
						tree.stopEditing();
					}				
					
					/*if (!tree.hasFocus()) {
						tree.requestFocus();
					}*/
					
					int nClicks = e.getClickCount();
        			TreePath path = tree.getPathForLocation(e.getX(), e.getY());
        			//int row = tree.getRowForLocation(e.getX(), e.getY());
					if (path != null) {
	  					DefaultMutableTreeNode thenode = (DefaultMutableTreeNode)path.getLastPathComponent();
						CheckNode check = (CheckNode)thenode.getUserObject();		
						if (check.getData() instanceof Code) {
							Code code = (Code)check.getData();		
							if (isRightMouse) {
								if(nClicks == 1) {
									tree.setSelectionPath(path);
									DefaultMutableTreeNode parent = (DefaultMutableTreeNode)thenode.getParent();
									String sCodeGroupID = ""; //$NON-NLS-1$
									if (parent != null) {
										CheckNode group = (CheckNode)parent.getUserObject();
										if (group.isGroup()) {									
											sCodeGroupID = (String)((Vector)group.getData()).elementAt(0);
										}
									}
									UITagTreeLeafPopupMenu pop = new UITagTreeLeafPopupMenu(me, code, sCodeGroupID);
									pop.show(tree, e.getX(), e.getY());																				
								}																
							} else if (isLeftMouse) {
								if (nClicks == 1) {		
									if(e.getX() < tree.getPathBounds(path).x + hotspot) {
										if (oWorkingList.getList().getSelectedRowCount() > 0) {
											check.setChecked(!check.isChecked());
											if (check.isChecked()) {
												onAddCodeToNodes(code);
												checkTagsForSelectedNodes();
											} else {
												onRemoveCodeFromNodes(code);
												if (isFilter) {
													updateFilterListView();
												}												
												checkTagsForSelectedNodes();
											}
											tree.repaint();											
										} else {
											ProjectCompendium.APP.displayMessage(LanguageProperties.getString(LanguageProperties.TAGS_BUNDLE, "UITagTreePanel.selectnodes"), LanguageProperties.getString(LanguageProperties.TAGS_BUNDLE, "UITagTreePanel.tags")); //$NON-NLS-1$ //$NON-NLS-2$
										}
									} else {
										boolean isSelected = tree.isPathSelected(path);										
										if (isSelected && !e.isShiftDown()) {
											tree.startEditingAtPath(path);
										} else {
											if (!e.isShiftDown()) {
												tree.clearSelection();	
											}							
										
											if (isSelected && e.isShiftDown()) {
												tree.removeSelectionPath(path);
											} else if (!isSelected) {
												tree.addSelectionPath(path);
											}
											clearChecks();
											tree.validate();
											tree.repaint();											
											updateFilterListView();
										}
									} 									
								}
	   		          		}
						} else {
							if (isRightMouse) {						
								UITagTreeGroupPopupMenu pop = new UITagTreeGroupPopupMenu(me, (Vector)check.getData());
								pop.show(tree, e.getX(), e.getY());
							} else if (isLeftMouse) {
								boolean isSelected = tree.isPathSelected(path);
								if (isSelected) {
									tree.startEditingAtPath(path);
								} else {
									tree.clearSelection();	
									tree.addSelectionPath(path);
									updateFilterListGroupView();									
									checkTagsForSelectedNodes();
									tree.repaint();
								}
							}
						}
					} else {
						/*if (!isRightMouse) {
							// have they clicked the folder expand/collpase icon?
							int mouseX = e.getX();
							path = tree.getClosestPathForLocation(mouseX, e.getY());
							if(path != null){
							    int                     boxWidth;
							    Insets                  i = tree.getInsets();
							    BasicTreeUI ui = (BasicTreeUI)tree.getUI();
							    if(ui.getExpandedIcon() != null)
							    	boxWidth = ui.getExpandedIcon().getIconWidth();
							    else
							    	boxWidth = 8;

							    int depthOffset = 0;
								if(tree.isRootVisible()) {
								    if(tree.getShowsRootHandles())
								    	depthOffset = 1;
								    else
								    	depthOffset = 0;
								}
								else if(!tree.getShowsRootHandles())
								    depthOffset = -1;
								else
								    depthOffset = 0;							    
							    int nRowX = (ui.getLeftChildIndent()+ui.getRightChildIndent()) * ((path.getPathCount() - 1) + depthOffset);
							    int boxLeftX = nRowX - ui.getRightChildIndent() - boxWidth / 2;

				                boxLeftX += i.left;
							    int boxRightX = boxLeftX + boxWidth;

							    if (mouseX >= boxLeftX && mouseX <= boxRightX) {
							    	if (tree.isExpanded(path)) {
										tree.collapsePath(path);
									} else {
										tree.expandPath(path);
									}							    	
							    }
							}														
						}*/
					}
     			}
				
     			public void mousePressed(MouseEvent e) {     				     		
					boolean isRightMouse = SwingUtilities.isRightMouseButton(e);
					boolean isLeftMouse = SwingUtilities.isLeftMouseButton(e);
					if (ProjectCompendium.isMac &&
						(e.getButton() == 3 && e.isShiftDown())) {
						isRightMouse = true;
						isLeftMouse = false;
					}
					
	       			TreePath path = tree.getPathForLocation(e.getX(), e.getY());
					
					if (!isRightMouse && path == null) {
						// have they clicked the folder expand/collpase icon?
						int mouseX = e.getX();
						path = tree.getClosestPathForLocation(mouseX, e.getY());
						if(path != null){
						    int                     boxWidth;
						    Insets                  i = tree.getInsets();
						    BasicTreeUI ui = (BasicTreeUI)tree.getUI();
						    if(ui.getExpandedIcon() != null)
						    	boxWidth = ui.getExpandedIcon().getIconWidth();
						    else
						    	boxWidth = 8;

						    int depthOffset = 0;
							if(tree.isRootVisible()) {
							    if(tree.getShowsRootHandles())
							    	depthOffset = 1;
							    else
							    	depthOffset = 0;
							}
							else if(!tree.getShowsRootHandles())
							    depthOffset = -1;
							else
							    depthOffset = 0;							    
						    int nRowX = (ui.getLeftChildIndent()+ui.getRightChildIndent()) * ((path.getPathCount() - 1) + depthOffset);
						    int boxLeftX = nRowX - ui.getRightChildIndent() - boxWidth / 2;

			                boxLeftX += i.left;
						    int boxRightX = boxLeftX + boxWidth;

						    if (mouseX >= boxLeftX && mouseX <= boxRightX) {
						    	if (tree.isExpanded(path)) {
									tree.collapsePath(path);
								} else {
									tree.expandPath(path);
								}							    	
						    }
						}														
					}
     			}
 			};
 			tree.addMouseListener(ml);

 			tree.addKeyListener(new KeyAdapter() { 				
 				
				public void keyPressed(KeyEvent e) {						
					int keyCode = e.getKeyCode();
					int modifiers = e.getModifiers();
					if(keyCode == KeyEvent.VK_DELETE) {
						TreePath path = tree.getSelectionPath();
	     				if (path != null) {
							DefaultMutableTreeNode node = (DefaultMutableTreeNode)path.getLastPathComponent();		
							CheckNode check = (CheckNode)node.getUserObject();
							if(check.getData() instanceof Code) {	
								Code code = (Code)check.getData();									
								onDeleteCode(code);
							}
						}
						e.consume();	     				
			 		}
					else if (keyCode == KeyEvent.VK_ENTER ) {
	             		TreePath path = tree.getSelectionPath();
	     				if (path != null) {
		  					int row = tree.getRowForPath(path);
							boolean isSelected = tree.isPathSelected(path);
							if (isSelected) {
								tree.startEditingAtPath(path);
							} else {
								if (modifiers != KeyEvent.VK_CONTROL) {
									tree.clearSelection();	
								}										
								if (tree.isRowSelected(row)) {
									tree.removeSelectionRow(row);
								} else {
									tree.addSelectionRow(row);											
								}
								tree.repaint();
							}
						}					
					} else if (keyCode == KeyEvent.VK_SPACE && modifiers == 0) {
						TreePath path = tree.getSelectionPath();
	     				if (path != null) {
							clearChecks();
							tree.validate();
							tree.repaint();											
							updateFilterListView();
						}					
	     			} else if (keyCode == KeyEvent.VK_SPACE && modifiers != KeyEvent.VK_SHIFT) {
						TreePath path = null;
						DefaultMutableTreeNode thenode = null;
						CheckNode check = null;
						Code code =  null;
						boolean updateFilterList = false;
						TreePath[] paths = tree.getSelectionPaths();
						if (oWorkingList.getList().getSelectedRowCount() > 0) {
	     					int count = paths.length;
	     					for (int i=0; i<count; i++) {
	     						path = paths[i];
			  					thenode = (DefaultMutableTreeNode)path.getLastPathComponent();
								check = (CheckNode)thenode.getUserObject();								
								if (check.getData() instanceof Code) {
									code = (Code)check.getData();			     					
									check.setChecked(!check.isChecked());
									if (check.isChecked()) {
										onAddCodeToNodes(code);
									} else {
										onRemoveCodeFromNodes(code);
										updateFilterList = true;
									}
									tree.repaint();		
								}
	     					}
	     					
							checkTagsForSelectedNodes();
							if (isFilter && updateFilterList) {
								updateFilterListView();
							}												
						} else {
							ProjectCompendium.APP.displayMessage(LanguageProperties.getString(LanguageProperties.TAGS_BUNDLE, "UITagTreePanel.selectnodes"), LanguageProperties.getString(LanguageProperties.TAGS_BUNDLE, "UITagTreePanel.tags")); //$NON-NLS-1$ //$NON-NLS-2$
						}					
	     			}
				}
            });            
		}

		sp = new JScrollPane(tree);		
		Dimension size = tree.getPreferredSize();		
		sp.setPreferredSize(new Dimension(250, size.height));
		return sp;
    }
	
	/**
	 * Clear the checked state for all CheckNode objects.
	 */
	public void clearChecks() {
		DefaultMutableTreeNode node = null;
		CheckNode check = null;
        for (Enumeration e = top.preorderEnumeration(); e.hasMoreElements();) {
            node = (DefaultMutableTreeNode)e.nextElement();
			check = (CheckNode)node.getUserObject();
			check.setChecked(false);
		}
	}	
	
	/**
	 * Process a button push event.
	 * @param evt the associated ActionEvent for the button push.
	 */
	public void actionPerformed(ActionEvent evt) {

		Object source = evt.getSource();
		if (source == pbExpand) {
			JButton button = (JButton)source;
			if (button.getText().equals(LanguageProperties.getString(LanguageProperties.TAGS_BUNDLE, "UITagTreePanel.expandallButton")))	{ //$NON-NLS-1$
				expandAllTree();
				button.setText(LanguageProperties.getString(LanguageProperties.TAGS_BUNDLE, "UITagTreePanel.collapseallButton")); //$NON-NLS-1$
				button.setMnemonic(LanguageProperties.getString(LanguageProperties.TAGS_BUNDLE, "UITagTreePanel.collapseallButtonMnemonic").charAt(0));		
			}
			else {
				collapseAllTree();
				button.setText(LanguageProperties.getString(LanguageProperties.TAGS_BUNDLE, "UITagTreePanel.expandallButton")); //$NON-NLS-1$
				button.setMnemonic(LanguageProperties.getString(LanguageProperties.TAGS_BUNDLE, "UITagTreePanel.expandallButtonMnemonic").charAt(0));						
			}
		} else if (source == pbMove) {
			JButton button = (JButton)source;
			if (button.getText().equals(LanguageProperties.getString(LanguageProperties.TAGS_BUNDLE, "UITagTreePanel.verticalsplitButton")))	{ //$NON-NLS-1$
				oSplitter.setOrientation(JSplitPane.VERTICAL_SPLIT);
				oSplitter.setDividerLocation(0.5);				
				FormatProperties.setFormatProp("tagsViewOrientation", "vertical"); //$NON-NLS-1$ //$NON-NLS-2$
				FormatProperties.saveFormatProps();
				ProjectCompendium.APP.oSplitter.resetToPreferredSizes();							
				button.setText(LanguageProperties.getString(LanguageProperties.TAGS_BUNDLE, "UITagTreePanel.horizontalsplitButton")); //$NON-NLS-1$
				button.setToolTipText(LanguageProperties.getString(LanguageProperties.TAGS_BUNDLE, "UITagTreePanel.horizontalsplitButtonTip"));				 //$NON-NLS-1$
				button.setMnemonic(LanguageProperties.getString(LanguageProperties.TAGS_BUNDLE, "UITagTreePanel.horizontalsplitButtonMnemonic").charAt(0));		
			}
			else  {
				oSplitter.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
				oSplitter.setDividerLocation(0.5);
				FormatProperties.setFormatProp("tagsViewOrientation", "horizontal"); //$NON-NLS-1$ //$NON-NLS-2$
				FormatProperties.saveFormatProps();				
				button.setText(LanguageProperties.getString(LanguageProperties.TAGS_BUNDLE, "UITagTreePanel.verticalsplitButton")); //$NON-NLS-1$
				ProjectCompendium.APP.oSplitter.resetToPreferredSizes();							
				button.setToolTipText(LanguageProperties.getString(LanguageProperties.TAGS_BUNDLE, "UITagTreePanel.verticalsplittipButtonTip"));				 //$NON-NLS-1$
				button.setMnemonic(LanguageProperties.getString(LanguageProperties.TAGS_BUNDLE, "UITagTreePanel.verticalsplitButtonMnemonic").charAt(0));						
			}			
		} else if (source == pbCancel) {
			hideHint();
			ProjectCompendium.APP.getMenuManager().removeTagsView(true);			
		} else if (source == pbNewTag) {
			onAddTags(tfNewCode.getText());
		} else if (source == pbNewGroup) {
			onAddGroups(tfNewCode.getText());		
		} else if (source == pbDeselectAll) {
			JButton button = (JButton)source;
			if (button.getText().equals(LanguageProperties.getString(LanguageProperties.TAGS_BUNDLE, "UITagTreePanel.deselectallButton")))	{ //$NON-NLS-1$
				oWorkingList.deselectAll();
				clearChecks();
				button.setText(LanguageProperties.getString(LanguageProperties.TAGS_BUNDLE, "UITagTreePanel.selectallButton")); //$NON-NLS-1$
				button.setMnemonic(LanguageProperties.getString(LanguageProperties.TAGS_BUNDLE, "UITagTreePanel.selectallButtonMnemonic").charAt(0));		
			}
			else {
				oWorkingList.getList().selectAll();
				button.setText(LanguageProperties.getString(LanguageProperties.TAGS_BUNDLE, "UITagTreePanel.deselectallButton")); //$NON-NLS-1$
				button.setMnemonic(LanguageProperties.getString(LanguageProperties.TAGS_BUNDLE, "UITagTreePanel.deselectallButtonMnemonic").charAt(0));						
			}			
		} else if (source == pbInsert) {
			try {
				onInsert();
			}
			catch(Exception ex) {
				ex.printStackTrace();
				System.out.println("Error: (UISearchResultDialog.actionPerformed) \n\n"+ex.getMessage()); //$NON-NLS-1$
			}
		}
	}	
	
	/**
	 * Insert the selected nodes in the current view.
	 */
	private void onInsert() throws Exception {
		
		int [] selection = oWorkingList.getList().getSelectedRows();
		if (selection.length <=0) {
			ProjectCompendium.APP.displayError(LanguageProperties.getString(LanguageProperties.TAGS_BUNDLE, "UITagTreePanel.pleaseselectnodes")); //$NON-NLS-1$
			return;
		}
		
		UIViewFrame activeFrame = ProjectCompendium.APP.getCurrentFrame();
		ProjectCompendium.APP.setWaitCursor();
		activeFrame.setCursor(new Cursor(java.awt.Cursor.WAIT_CURSOR));
		this.setCursor(new Cursor(java.awt.Cursor.WAIT_CURSOR));

		int i=0;
		String sNodeID = ""; //$NON-NLS-1$
		NodeSummary node = null;	
		
		Vector addedNodes = new Vector(51);
		
		if (activeFrame instanceof UIListViewFrame) {

			// deselect nodes and links so pasted ones are only ones seleted - bz
			((UIListViewFrame)activeFrame).getUIList().deselectAll();

			int index = -1;
			UIList list = list = ((UIListViewFrame)activeFrame).getUIList();
			View listview = list.getView();
			int nodeCount = listview.getNumberOfNodes();;
			NodePosition[] nps = new NodePosition[selection.length];
			NodePosition nodePos = null;
			for(i=0;i<selection.length;i++) {
				node = (NodeSummary)oWorkingList.getNodeAt(selection[i]);
				if (node != null) {
					sNodeID = node.getId();
					nodePos = list.getNode(sNodeID);
					if (nodePos == null) { 				
						int xpos = 100;
						int ypos = ( nodeCount + 1) * 10;
						nodeCount++;
						Date date = new Date();
						NodePosition np = new NodePosition(listview, node, xpos, ypos, date, date);
						nps[i]=np;						
						try {
							nodePos = listview.addNodeToView(node, xpos, ypos);
						}
						catch (Exception e) {
							e.printStackTrace();
							ProjectCompendium.APP.displayError("Error: (UITagTreePanel.onInsert1)\n\n" + e.getMessage()); //$NON-NLS-1$
							System.out.flush();
						}
					}
					else {
						addedNodes.addElement(new Integer(list.getIndexOf(nodePos.getNode())));
					}
				}
			}
			int len = listview.getNumberOfNodes();
			// FORCE SORTER UPDATE
			((ListTableModel)((TableSorter)list.getList().getModel()).getModel()).refreshTable();
			
			int count = addedNodes.size();
			for (int j=0;j<count; j++) {
				list.selectNode(((Integer)addedNodes.elementAt(j)).intValue(), ICoreConstants.MULTISELECT);
			}				
			list.insertNodes(nps, len);
			for (int k=0; k<nps.length; k++) {
				list.selectNode(k+len, ICoreConstants.MULTISELECT);				
			}			
		}
		else {	
			UIViewPane uiviewpane = ((UIMapViewFrame)activeFrame).getViewPane();
			ViewPaneUI viewpaneui = uiviewpane.getUI();
			for(i=0;i<selection.length;i++) {
				node = (NodeSummary)oWorkingList.getNodeAt(selection[i]);
				if (node != null) {
					sNodeID = node.getId();				
					if (uiviewpane.get(sNodeID) == null) {				
						int hPos = activeFrame.getHorizontalScrollBarPosition();
						int vPos = activeFrame.getVerticalScrollBarPosition();		
						int xpos = hPos + ViewPaneUI.LEFTOFFSET;
						int ypos = vPos + ((i+1)*ViewPaneUI.INTERNODE_DISTANCE);
						try {
							UINode uinode = viewpaneui.addNodeToView(node,xpos,ypos);
							addedNodes.addElement(uinode);
						}
						catch (Exception e) {
							e.printStackTrace();
							ProjectCompendium.APP.displayError("Error: (UITagTreePanel.onInsert2)\n\n" + e.getMessage()); //$NON-NLS-1$
							System.out.flush();
						}												
					} else {
						UINode uinode = (UINode)uiviewpane.get(sNodeID);
						addedNodes.addElement(uinode);
					}
				}
			}
			
			int count = addedNodes.size();
			UINode uinode = null;
			for (int j=0;j<count; j++) {
				uinode = (UINode)addedNodes.elementAt(j);
				uinode.setRollover(false);
				uinode.setSelected(true);
				uiviewpane.setSelectedNode(uinode, ICoreConstants.MULTISELECT);				
			}
		}
		
		this.setCursor(Cursor.getDefaultCursor());
		activeFrame.setCursor(Cursor.getDefaultCursor());
		ProjectCompendium.APP.setDefaultCursor();
	}	
	
	private void onAddTags(String tags) {
		
		if (tags.equals("")) { //$NON-NLS-1$
			ProjectCompendium.APP.displayError(LanguageProperties.getString(LanguageProperties.TAGS_BUNDLE, "UITagTreePanel.messageentertagname")); //$NON-NLS-1$
			tfNewCode.requestFocus();
		}
		
		Vector vtNewCodes = new Vector();
		int num = 0;
		StringTokenizer st = new StringTokenizer(tags,","); //$NON-NLS-1$
		while(st.hasMoreTokens()) {
			String c = st.nextToken();
			if (c.length() > 50) {
				ProjectCompendium.APP.displayError(LanguageProperties.getString(LanguageProperties.TAGS_BUNDLE, "UITagTreePanel.warning50charsA")+"\n\n"+LanguageProperties.getString(LanguageProperties.TAGS_BUNDLE, "UITagTreePanel.warning50charsB")); //$NON-NLS-1$
				tfNewCode.requestFocus();				
				return;
			}
			vtNewCodes.addElement(c.trim());
		}

		IModel model = ProjectCompendium.APP.getModel();
		PCSession session = ProjectCompendium.APP.getModel().getSession();
		String author = model.getUserProfile().getUserName();
		Date creationDate = new Date();
		Date modificationDate = creationDate;
		String description = LanguageProperties.getString(LanguageProperties.TAGS_BUNDLE, "UITagTreePanel.nodesc"); //$NON-NLS-1$
		String behavior = LanguageProperties.getString(LanguageProperties.TAGS_BUNDLE, "UITagTreePanel.nobehaviour"); //$NON-NLS-1$
		int count = vtNewCodes.size();
		for(int i=0;i<count;i++) {
			try {
				String name = (String)vtNewCodes.elementAt(i);
				String sCodeID = model.getUniqueID();

				if (ProjectCompendium.APP.getModel().codeNameExists(sCodeID, name)) {
					ProjectCompendium.APP.displayMessage(LanguageProperties.getString(LanguageProperties.TAGS_BUNDLE, "UITagTreePanel.warningtagexists")+name+"\n\n", LanguageProperties.getString(LanguageProperties.TAGS_BUNDLE, "UITagTreePanel.tagmaintenance")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				}
				else {
					//UPDATE DATABASE
					Code code = model.getCodeService().createCode(session, sCodeID, author, creationDate,
													 modificationDate, name, description, behavior);
					// UPDATE MODEL
					model.addCode(code);
					onAddCodeToNodes(code);
					updateTreeData();						
				}
			}
			catch(SQLException ex) {
				ProjectCompendium.APP.displayError("Exception: (UICodeMaintPanel.onAdd)\n\n)" + ex.getMessage()); //$NON-NLS-1$
			}
		}

		checkTagsForSelectedNodes();
		ProjectCompendium.APP.updateCodeChoiceBoxData();		
		tfNewCode.setText("");	 //$NON-NLS-1$
	}
	
	private void onAddGroups(String groups) {
		
		Vector vtNewCodes = new Vector();
		int num = 0;
		StringTokenizer st = new StringTokenizer(groups,","); //$NON-NLS-1$
		while(st.hasMoreTokens()) {
			String c = st.nextToken();
			if (c.length() > 100) {
				ProjectCompendium.APP.displayError(LanguageProperties.getString(LanguageProperties.TAGS_BUNDLE, "UITagTreePanel.warning100charsA")+"\n\n"+LanguageProperties.getString(LanguageProperties.TAGS_BUNDLE, "UITagTreePanel.warning100charsB")); //$NON-NLS-1$
				tfNewCode.requestFocus();				
				return;
			}			
			vtNewCodes.addElement(c.trim());
		}

		IModel model = ProjectCompendium.APP.getModel();
		PCSession session = ProjectCompendium.APP.getModel().getSession();
		String sAuthor = model.getUserProfile().getUserName();
		Date date = new Date();

		for(int i=0;i<vtNewCodes.size();i++) {
			String name = (String)vtNewCodes.elementAt(i);			
			try {
				String sCodeGroupID = model.getUniqueID();

				//ADD NEW CODE TO DATABASE
				(model.getCodeGroupService()).createCodeGroup(model.getSession(), sCodeGroupID, sAuthor, name, date, date);

				// UPDATE MODEL
				Vector group = new Vector(2);
				group.addElement(sCodeGroupID);
				group.addElement(name);
				model.addCodeGroup(sCodeGroupID, group);
				
				updateTreeData();			
			}
			catch(SQLException ex) {
				ProjectCompendium.APP.displayError("Error: (UICodeMaintPanel.onAdd)" + ex.getMessage()); //$NON-NLS-1$
			}
		}

		tfNewCode.setText("");	 //$NON-NLS-1$
	}	
	
	/**
	 * Handle a request to delete the selected codes.
	 */
	public void onDeleteCode(Code code) {

		IModel model = ProjectCompendium.APP.getModel();
		PCSession session = model.getSession();

		try {
			// CHECK FOR ASSOCIATIONS
			int count = model.getCodeService().getNodeCount(session, code.getId());
			if (count > 0) {
				ProjectCompendium.APP.displayMessage(LanguageProperties.getString(LanguageProperties.TAGS_BUNDLE, "UITagTreePanel.warningassociatednodes"), LanguageProperties.getString(LanguageProperties.TAGS_BUNDLE, "UITagTreePanel.deletetag")); //$NON-NLS-1$ //$NON-NLS-2$
				return;
			}
			else {				
				int response = JOptionPane.showConfirmDialog(ProjectCompendium.APP, LanguageProperties.getString(LanguageProperties.TAGS_BUNDLE, "UITagTreePanel.deletetagconfirmation")+": "+code.getName()+"?", //$NON-NLS-1$ //$NON-NLS-2$
						LanguageProperties.getString(LanguageProperties.TAGS_BUNDLE, "UITagTreePanel.deletetag"), JOptionPane.YES_NO_OPTION); //$NON-NLS-1$
				if (response == JOptionPane.NO_OPTION || response == JOptionPane.CLOSED_OPTION) {
					return;
				} else {
					String sCodeID = code.getId();
					
					// UPDATE DATABASE
					model.getCodeService().delete(session, sCodeID);
					
					// UPDATE MODEL
					model.removeCode(code);
					
					updateTreeData();
					
					ProjectCompendium.APP.updateCodeChoiceBoxData();					
				}
			}
		}
		catch(Exception ex) {
			ProjectCompendium.APP.displayError("Error: (UICodeCodeMaintPanel.onDeleteCode)\n\n" + ex.getMessage()); //$NON-NLS-1$
		}
	}		

	/**
	 * Handle a request to delete the selected codes.
	 */
	public void onRemoveCodeFromGroup(Code code, String sCodeGroupID) {

		IModel model = ProjectCompendium.APP.getModel();
		PCSession session = model.getSession();

		try {
			// UPDATE DATABASE
			model.getGroupCodeService().delete(session, code.getId(), sCodeGroupID );

			// UPDATE MODEL
			model.removeCodeGroupCode(sCodeGroupID, code.getId());
			
			if (sCodeGroupID.equals(ProjectCompendium.APP.getActiveCodeGroup())) {
				ProjectCompendium.APP.updateCodeChoiceBoxData();
			}
			
			updateTreeData();
		}
		catch(Exception ex) {
			ProjectCompendium.APP.displayError("Error: (UICodeCodeMaintPanel.onRemoveCodeFromGroup)" + ex.getMessage()); //$NON-NLS-1$
		}
	}			
	
	/**
	 * Create tree of nodes for the tree on the code assinment panel.
	 *
	 * @return DefaultMutableTreeNode, the top tree node for the tree.
	 */
	public DefaultMutableTreeNode getTreeData() {
		
		// TOP NEEDS TO MATCH THE STRUCTURE OF THE REST OF THE TREE DATA
		Vector topdata = new Vector(2);
		topdata.addElement(new String("")); //$NON-NLS-1$
		topdata.addElement(new String(LanguageProperties.getString(LanguageProperties.TAGS_BUNDLE, "UITagTreePanel.treetoplabel"))); //$NON-NLS-1$
		DefaultMutableTreeNode activeGroupNode = null;
		CheckNode check = new CheckNode(topdata);
        DefaultMutableTreeNode top = new DefaultMutableTreeNode(check);

		try {
			Vector order = new Vector(51);
			Hashtable groups = model.getCodeGroups();

			DefaultMutableTreeNode group = null;
			DefaultMutableTreeNode code = null;
			check = null;
			
			// ADD CODE GROUPS
			for(Enumeration e = groups.elements();e.hasMoreElements();) {
				Hashtable nextGroup = (Hashtable)e.nextElement();

				if (nextGroup.containsKey("group")) { //$NON-NLS-1$
					Vector groupdata = (Vector)nextGroup.get("group"); //$NON-NLS-1$
					String sCodeGroupID = (String)groupdata.elementAt(0);
					check = new CheckNode(groupdata);
					group = new DefaultMutableTreeNode(check);

					Hashtable children = null;

					if (nextGroup.containsKey("children")) { //$NON-NLS-1$
						children = (Hashtable)nextGroup.get("children"); //$NON-NLS-1$

						// ADD ALL CHILD CODES FOR THIS GROUP
						Vector childorder = new Vector(51);

						for(Enumeration e2 = children.elements();e2.hasMoreElements();) {
							Code nextcode = (Code)e2.nextElement();
							check = new CheckNode(nextcode);
							code = new DefaultMutableTreeNode(check);
							childorder.addElement(code);
						}

						childorder = UIUtilities.sortList(childorder);

						if (childorder != null) {
							int count  = childorder.size();
							for (int i=0; i<count; i++) {
								group.add((DefaultMutableTreeNode)childorder.elementAt(i));
							}
						}
					}

					// DON'T SHOW EMPTY GROUPS
					//if (children != null && children.size() > 0) {
						// MAKE SURE ACTIVE GROUPS IS AT THE TOP OF THE TREE LATER
						//if (sCodeGroupID.equals(ProjectCompendium.APP.getActiveCodeGroup()))
						//	activeGroupNode = group;
						//else
							order.addElement(group);
					//}
				}
		    }

			// ADD NODES TO ROOT NODE
			int jcount  = order.size();
			if (jcount > 0)
				order = UIUtilities.sortList(order);

			// ADD THE ACTIVE GROUP AT THE TOP
			if (activeGroupNode != null)
				top.add(activeGroupNode);

			// ADD ALL OTHER GROUPS
			for (int j=0; j<jcount; j++)
				top.add((DefaultMutableTreeNode)order.elementAt(j));

			// ADD ALL CODES NOT IN GROUPS
			Hashtable ungroupedCodes = model.getUngroupedCodes();

			Vector sortedUngroupedCodes = new Vector(51);
			for(Enumeration un = ungroupedCodes.elements();un.hasMoreElements();) {
				sortedUngroupedCodes.addElement((Code)un.nextElement());
			}
			sortedUngroupedCodes = CoreUtilities.sortList(sortedUngroupedCodes);

			int lcount = sortedUngroupedCodes.size();
			for (int l=0; l<lcount; l++) {
				check = new CheckNode((Code)sortedUngroupedCodes.elementAt(l));
				top.add(new DefaultMutableTreeNode(check));
			}
		}
		catch (Exception io) {
			io.printStackTrace();
		}

		this.top = top;
		return top;
	}
	
	/**
 	 * Indicates when nodes and link are selected and deselected
  	 * @param selected true for selected false for deselected.
	 */
	public void setNodeOrLinkSelected(boolean selected) {
		setNodeSelected(selected);
	}

	/**
 	 * Indicates when nodes on a view are selected and deselected.
  	 * @param selected true for selected false for deselected.
	 */
	public void setNodeSelected(boolean selected) {
		if (selected) {			
			tree.clearSelection();
			updateSelectionListView();			
			oWorkingList.getList().selectAll();
			pbDeselectAll.setText(LanguageProperties.getString(LanguageProperties.TAGS_BUNDLE, "UITagTreePanel.deselectallButton")); //$NON-NLS-1$
			pbDeselectAll.setMnemonic(LanguageProperties.getString(LanguageProperties.TAGS_BUNDLE, "UITagTreePanel.deselectallButtonMnemonic").charAt(0));									
		} else {
			if (!isFilter) {
				tree.clearSelection();
				updateSelectionListView();								
				clearChecks();
				refresh();
			}
		}
	}

	/**
	 * Add the given code to the code list.
	 * 
	 * @param vCodeList the list of codes to add the code to.
	 * @param newCodem the code to add, if not already there.
	 */
	public boolean addCodeToList (Vector vCodeList, Code newCode) {

		boolean found = false;

		for(Enumeration e = vCodeList.elements();e.hasMoreElements();) {

			Code code = (Code)e.nextElement();
			if (code.getName().equals(newCode.getName())) {
				found = true;
				break;
			}
		}
		if(!found){
			vCodeList.addElement(newCode);
		}
		return (!found);
	}

	/**
	 * Remove the given code from the code list.
	 *
	 * @param vCodeList, the list of codes to remove the code from.
	 * @param newCodem, the code to remove, if there.
	 */
	public void removeCodeFromList (Vector vCodeList, Code removedCode) {
		if(!vCodeList.contains(removedCode)){
			vCodeList.removeElement(removedCode);
		}
	}

    /**
	 * Expand all tree folders.
	 */
    public void expandAllTree() {
    	for (Enumeration e = top.children(); e.hasMoreElements();) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode)e.nextElement();
			tree.expandPath(new TreePath( ((DefaultTreeModel)tree.getModel()).getPathToRoot(node) ));
		}
		tree.repaint();
		sp.repaint();
	}

    /**
	 * Collapse all tree folders.
	 */
    public void collapseAllTree() {
    	for (Enumeration e = top.children(); e.hasMoreElements();) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode)e.nextElement();
			tree.collapsePath(new TreePath( ((DefaultTreeModel)tree.getModel()).getPathToRoot(node) ));
		}
		tree.repaint();
		sp.repaint();
	}

	/**
	 * Return the font size to its default and then appliy the passed text zoom.
	 * (To the default specificed by the user in the Project Options)
	 */
	public void onReturnTextAndZoom(int zoom) {
		Font font = ProjectCompendium.APP.currentDefaultFont;
		Font newFont = new Font(font.getName(), font.getStyle(), font.getSize()+zoom);			
		tree.setFont(newFont);
		FontMetrics metrics = tree.getFontMetrics(newFont);
		tree.setRowHeight(metrics.getHeight());				
		
		this.oWorkingList.onReturnTextAndZoom(zoom);
	}
	
	/**
	 * Return the font size to its default 
	 * (To the default specificed by the user in the Project Options)
	 */
	public void onReturnTextToActual() {
		tree.setFont(ProjectCompendium.APP.currentDefaultFont);
		FontMetrics metrics = tree.getFontMetrics(ProjectCompendiumFrame.currentDefaultFont);
		tree.setRowHeight(metrics.getHeight());				
		
		this.oWorkingList.onReturnTextToActual();
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
		
		this.oWorkingList.onIncreaseTextSize();
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
		
		this.oWorkingList.onReduceTextSize();
	}
	
    /**
     * Add the given code to the currently selected nodes
     * @param oCode the code to add
     */
    public void onAddCodeToNodes(Code oCode) {

		UIViewFrame frame = ProjectCompendium.APP.getCurrentFrame();
		
		if (oWorkingList.getNumberOfSelectedNodes() > 0) {
            int[] rows = oWorkingList.getList().getSelectedRows();
            int row = 0;
            NodeSummary node = null;
            for (int i=0; i<rows.length; i++) {
            	row = rows[i];
            	node = oWorkingList.getNodeAt(row);
            	if (node != null ){
					try {
						if (node.addCode(oCode)) {	
	
							// IF WE ARE RECORDING or REPLAYING A MEETING, RECORD A TAG ADDED EVENT.
							if (ProjectCompendium.APP.oMeetingManager != null && ProjectCompendium.APP.oMeetingManager.captureEvents()) {
								ProjectCompendium.APP.oMeetingManager.addEvent(
										new MeetingEvent(ProjectCompendium.APP.oMeetingManager.getMeetingID(),
														 ProjectCompendium.APP.oMeetingManager.isReplay(),
														 MeetingEvent.TAG_ADDED_EVENT,
														 frame.getView(),
														 node,
														 oCode));							
							}
						}
					}
					catch(Exception ex) {
						ex.printStackTrace();
						ProjectCompendium.APP.displayError("Error: (UITagTreePanel.onAddCodeToNodes1)\n\n"+ex.getMessage()); //$NON-NLS-1$
					}
            	}
            }
 		}
		
		// INCASE NEED TO UPDATE 'T' INDICATORS
		ProjectCompendium.APP.refreshIconIndicators();
	}

	/**
	 * Remove the given code from all selected nodes.
	 * @param oCode the code to remove.
	 */
	public void onRemoveCodeFromNodes(Code oCode) {

		UIViewFrame frame = ProjectCompendium.APP.getCurrentFrame();
		
		if (oWorkingList.getNumberOfSelectedNodes() > 0) {
            int[] rows = oWorkingList.getList().getSelectedRows();
            int row = 0;
            NodeSummary node = null;
            for (int i=0; i<rows.length; i++) {
            	row = rows[i];
            	node = oWorkingList.getNodeAt(row);
            	if (node != null) {
					try {
						if (node.hasCode(oCode.getName())) {
							if (node.removeCode(oCode)) {	
			
								// IF WE ARE RECORDING or REPLAYING A MEETING, RECORD A TAG REMOVED EVENT.
								if (ProjectCompendium.APP.oMeetingManager != null && ProjectCompendium.APP.oMeetingManager.captureEvents()) {
									ProjectCompendium.APP.oMeetingManager.addEvent(
											new MeetingEvent(ProjectCompendium.APP.oMeetingManager.getMeetingID(),
															 ProjectCompendium.APP.oMeetingManager.isReplay(),
															 MeetingEvent.TAG_REMOVED_EVENT,
															 frame.getView(),
															 node,
															 oCode));							
								}
							}
						}
					}
					catch (NoSuchElementException e) {
						e.printStackTrace();					
					}
					catch(Exception ex) {
						ex.printStackTrace();
						ProjectCompendium.APP.displayError("Error: (UITagTreePanel.onRemoveCodeFromNodes1)\n\n"+ex.getMessage()); //$NON-NLS-1$
					}
            	}
            }
 		}
				
		// INCASE NEED TO UPDATE 'T' INDICATORS
		ProjectCompendium.APP.refreshIconIndicators();
	}
		
	/**
	 * Can be used to clean up variables used ot help with garbage collection.
	 */
	public void cleanUp() {
		sp 					= null;
		lblCodesList		= null;
		pbCancel			= null;
		pbExpand			= null;
		centerpanel			= null;
		tree				= null;
		treemodel			= null;
		top = null;
	}		
      
//	 INNER CLASSES
		
	/**
	 * Inner class to render a tree item. 
	 * @author Michelle Bachler
	 */
	private class CheckBoxNodeRenderer extends JPanel implements TreeCellRenderer {
		
		private CheckNode box = null;
		private JCheckBox cbCheckBox = null;
		private JLabel label = new JLabel();		
		private JTextField field = new JTextField();
		
		private Border border = null;
		
		private Icon leafIcon = null;
		private Icon openIcon = null;
		private Icon closedIcon = null;
		
		private Color oHighlight = Color.yellow;
		private Border oMainBorder = null;
		
		protected BorderLayout layout = null;

		Color selectionBorderColor, selectionForeground, selectionBackground,
		textForeground, textBackground;
		
		protected JCheckBox getCheckBox() {
			return cbCheckBox;
		}

		protected JTextField getField() {
			return field;
		}

		protected CheckNode getNode() {
			return box;
		}
		
		protected Border getFieldBorder() {
			return border;
		}		
		
		protected void setDefaultColors() {
			field.setForeground(textForeground);
			field.setBackground(textBackground);
			label.setForeground(selectionForeground);
			label.setBackground(selectionBackground);				
			setBackground(textBackground);							
		}
		
		public CheckBoxNodeRenderer() {
        	setFont(ProjectCompendium.APP.currentDefaultFont);
			oMainBorder = getBorder();
			layout = new BorderLayout();
			layout.setHgap(5);
			setLayout(layout);
			add(field, BorderLayout.CENTER);
			field.setEditable(false);
			border = field.getBorder();
			field.setBorder(null);
			
 			DefaultTreeCellRenderer rend = new DefaultTreeCellRenderer();
			leafIcon = rend.getLeafIcon();
			openIcon = rend.getOpenIcon();
			closedIcon = rend.getClosedIcon();
			
			selectionForeground = UIManager.getColor("List.selectionForeground"); //$NON-NLS-1$
			selectionBackground = UIManager.getColor("List.selectionBackground"); //$NON-NLS-1$
			textForeground = UIManager.getColor("List.textForeground"); //$NON-NLS-1$
			textBackground = UIManager.getColor("List.textBackground"); //$NON-NLS-1$
		}
		
		public Component getTreeCellRendererComponent(JTree tree, Object value,
				boolean selected, boolean expanded, boolean leaf, int row,
				boolean hasFocus) {
						
			if (cbCheckBox != null) {
				remove(cbCheckBox);
				cbCheckBox = null;
			}
			remove(label);
			setBorder(oMainBorder);
			
			DefaultMutableTreeNode node = (DefaultMutableTreeNode)value;			
			Object userObject = node.getUserObject();
			box = (CheckNode)userObject;			
			if (!box.isGroup()) {	
			    Code code = (Code)box.getData();

				cbCheckBox = new JCheckBox();
		    	cbCheckBox.setSelected(box.isChecked());	
		    	cbCheckBox.setFont(tree.getFont());

		    	if (box.isChecked()) {
		    		if (box.isUniversal()) {
		    			cbCheckBox.setBackground(Color.orange);		    			
		    		} else {
		    			cbCheckBox.setBackground(IUIConstants.DEFAULT_COLOR);
		    		}
		    	} else {
		    		cbCheckBox.setBackground(tree.getBackground());
		    	}
			    add(cbCheckBox, BorderLayout.WEST);

				String text = code.getName();
				int count = 0;
				try {
					count = (model.getCodeService()).getNodeCount(model.getSession(), code.getId());
				}
				catch(Exception ex) {
					ex.printStackTrace();
				}	
				
				// HACK: for window/Mac bug. Leave space at end else when number grows it disappears off right edge
				// Something wrong as layout manager should adjust really
				field.setText(text+"  ("+count+")       ");	 //$NON-NLS-1$ //$NON-NLS-2$
	        	field.setFont(tree.getFont());

				if (selected) {
					field.setForeground(selectionForeground);
					field.setBackground(selectionBackground);
				} else {
					field.setForeground(textForeground);
					field.setBackground(textBackground);
				}
				setToolTipText(LanguageProperties.getString(LanguageProperties.TAGS_BUNDLE, "UITagTreePanel.clickltip")); //$NON-NLS-1$
				
				layout.setHgap(2);
				
				layout.invalidateLayout(this);
				layout.layoutContainer(this);
			} else {
				add(label, BorderLayout.WEST);
				Icon icon = null;
				if (box.isGroup()) {
					if (expanded)
						icon = openIcon;
					else
						icon = closedIcon;	
				}/* else if (leaf) {
					//icon = leafIcon;
					icon = null;
				}	*/
				label.setIcon(icon);
				label.setForeground(textForeground);
				label.setBackground(textBackground);				

				Vector group = (Vector)box.getData();
				field.setText((String)group.elementAt(1));
	        	field.setFont(tree.getFont());

				if ( (ProjectCompendium.APP.getActiveCodeGroup()).equals((String)group.elementAt(0)) && !selected ) {
					field.setForeground(IUIConstants.DEFAULT_COLOR);
					field.setBackground(textBackground);				
				} else {												
					if (selected) {
						field.setForeground(selectionForeground);
						field.setBackground(selectionBackground);
					} else {
						field.setForeground(textForeground);
						field.setBackground(textBackground);
					}					
				}		
				
				setToolTipText(LanguageProperties.getString(LanguageProperties.TAGS_BUNDLE, "UITagTreePanel.clicktip2")); //$NON-NLS-1$
				if (row == highlightRow) {
					setBorder(new LineBorder(oHighlight, 2));
				}				
			}
			
			setBackground(textBackground);				
			
			htAllRenderers.put(new Integer(row), this);

			return this;
		}
	}	
	
	private class CheckBoxNodeEditor extends AbstractCellEditor implements TreeCellEditor {
		
		CheckBoxNodeRenderer renderer = new CheckBoxNodeRenderer();
		CheckNode check = null;
		
		JTree tree;
		
		public CheckBoxNodeEditor(JTree tree) {
			this.tree = tree;
		}
		
		public Object getCellEditorValue() {
			return check;
		}
		
		public boolean shouldSelectCell(EventObject anEvent) {
			return false;
		}
		
		public boolean stopCellEditing() {
			check.setText(renderer.getField().getText());
			updateTreeData();
			if (isFilter) {
				updateFilterListView();				
			} else {
				updateSelectionListView();	
			}
			return true;
		}
		
		public boolean isCellEditable(EventObject event) {
			return true;
		}
		
		public Component getTreeCellEditorComponent(JTree tree, Object value,
				boolean selected, boolean expanded, boolean leaf, int row) {
			
			Component editor = renderer.getTreeCellRendererComponent(tree, value,
					selected, expanded, leaf, row, true);
			
			if (renderer.getCheckBox() != null) {
				renderer.getCheckBox().setEnabled(false);
			}
			check = renderer.getNode();
			JTextField field = renderer.getField();
			field.setText(check.getText());
			field.setBorder(renderer.getFieldBorder());
			field.setEditable(true);			
			int cols = field.getColumns();
			if (cols < 30) {
				field.setColumns(30);
			}
			
			final JTree ftree = tree;
			final JTextField ffield = field;
			field.addKeyListener(new KeyAdapter() {
				public void keyPressed(KeyEvent e) {															
					int keyCode = e.getKeyCode();
					int modifiers = e.getModifiers();
					if (keyCode == KeyEvent.VK_ENTER && modifiers == 0) {
						if (ftree.isEditing()) {
							ftree.stopEditing();
						}				
					}										
				}
				
				public void keyTyped(KeyEvent e) {
					char keyChar = e.getKeyChar();
					char[] key = {keyChar};
					String sKeyPressed = new String(key);
					int modifiers = e.getModifiers();

					//JViewport viewport = sp.getViewport();
					//Rectangle nodeBounds = ffield.getBounds();
					//Point parentPos = SwingUtilities.convertPoint(ftree, nodeBounds.x, nodeBounds.y, viewport);					
					//sp.scrollRectToVisible(new Rectangle(parentPos.x+nodeBounds.width-5, parentPos.y, 5, 5));
					
					if (ProjectCompendium.isMac && modifiers == ProjectCompendium.APP.shortcutKey) {
						return;
					} else {
						if (( Character.isLetterOrDigit(keyChar) || sKeyPressed.equals(" ")  || //$NON-NLS-1$
									IUIConstants.NAVKEYCHARS.indexOf(sKeyPressed) != -1) ) {
							 String sText = ffield.getText();
							 if (check.isGroup() && sText.length()== 100) {
								 ffield.setText(sText.substring(0,100));
								 e.consume();
							 } else if (!check.isGroup() && sText.length() == 50) {
								 ffield.setText(sText.substring(0,50));
								 e.consume();
							 }
						}
					}					
				}
			});
			
			field.addFocusListener(new FocusListener(){
				public void focusGained(FocusEvent e){}				
				public void focusLost(FocusEvent e){
					ftree.stopEditing();
				}
			});
			
        	field.setFont(tree.getFont());

			renderer.setDefaultColors();				

			return editor;
		}	
	}		 
	
// DRAG AND DROP METHODS
  	
  	//  TRANSFERABLE  	
   /**
     * Returns an array of DataFlavor objects indicating the flavors the data
     * can be provided in.
     * @return an array of data flavors in which this data can be transferred
     */
	public DataFlavor[] getTransferDataFlavors() {
		return supportedFlavors;
	}

    /**
     * Returns whether or not the specified data flavor is supported for
     * this object.
     * @param flavor the requested flavor for the data
     * @return boolean indicating whether or not the data flavor is supported
     */
	public boolean isDataFlavorSupported(DataFlavor flavor) {
		return flavor.isMimeTypeEqual(DataFlavor.javaJVMLocalObjectMimeType+"; class=com.compendium.ui.tags.UITagTreePanel"); //$NON-NLS-1$
	}

    /**
     * Returns an object which represents the data to be transferred.  The class
     * of the object returned is defined by the representation class of the flavor.
     *
     * @param flavor the requested flavor for the data
     * @see DataFlavor#getRepresentationClass
     * @exception IOException                if the data is no longer available in the requested flavor.
     * @exception UnsupportedFlavorException if the requested data flavor is not supported.
     */
	public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
		if (flavor.isMimeTypeEqual(DataFlavor.javaJVMLocalObjectMimeType+"; class=com.compendium.ui.tags.UITagTreePanel")) //$NON-NLS-1$
			return this;
		else return null;
	}

	
	private TreePath[] sourcepaths = null;
	
	//	SOURCE
    /**
     * A <code>DragGestureRecognizer</code> has detected
     * a platform-dependent drag initiating gesture and
     * is notifying this listener
     * in order for it to initiate the action for the user.
     * <P>
     * @param e the <code>DragGestureEvent</code> describing
     * the gesture that has just occurred
     */
	public void dragGestureRecognized(DragGestureEvent e) {
	    InputEvent in = e.getTriggerEvent();
	    sourcepaths = null;
	    int action = e.getDragAction();	    
	    if (in instanceof MouseEvent) {
    		sourcepaths = tree.getSelectionPaths();
 	    	if (action == DnDConstants.ACTION_COPY) {
				e.startDrag(DragSource.DefaultCopyDrop, this, this);
			} else {
				e.startDrag(DragSource.DefaultMoveDrop, this, this);								
	    	}
		}
	}

    /**
     * This method is invoked to signify that the Drag and Drop
     * operation is complete. The getDropSuccess() method of
     * the <code>DragSourceDropEvent</code> can be used to
     * determine the termination state. The getDropAction() method
     * returns the operation that the drop site selected
     * to apply to the Drop operation. Once this method is complete, the
     * current <code>DragSourceContext</code> and
     * associated resources become invalid.
	 * HERE THE METHOD DOES NOTHING.
     *
     * @param e the <code>DragSourceDropEvent</code>
     */
	public void dragDropEnd(DragSourceDropEvent e) {}

    /**
     * Called as the cursor's hotspot enters a platform-dependent drop site.
     * This method is invoked when all the following conditions are true:
     * <UL>
     * <LI>The cursor's hotspot enters the operable part of a platform-
     * dependent drop site.
     * <LI>The drop site is active.
     * <LI>The drop site accepts the drag.
     * </UL>
	 * HERE THE METHOD DOES NOTHING.
     *
     * @param e the <code>DragSourceDragEvent</code>
     */
	public void dragEnter(DragSourceDragEvent e) {}

    /**
     * Called as the cursor's hotspot exits a platform-dependent drop site.
     * This method is invoked when any of the following conditions are true:
     * <UL>
     * <LI>The cursor's hotspot no longer intersects the operable part
     * of the drop site associated with the previous dragEnter() invocation.
     * </UL>
     * OR
     * <UL>
     * <LI>The drop site associated with the previous dragEnter() invocation
     * is no longer active.
     * </UL>
     * OR
     * <UL>
     * <LI> The current drop site has rejected the drag.
     * </UL>
	 * HERE THE METHOD DOES NOTHING.
     *
     * @param e the <code>DragSourceEvent</code>
     */
	public void dragExit(DragSourceEvent e) {}

    /**
     * Called as the cursor's hotspot moves over a platform-dependent drop site.
     * This method is invoked when all the following conditions are true:
     * <UL>
     * <LI>The cursor's hotspot has moved, but still intersects the
     * operable part of the drop site associated with the previous
     * dragEnter() invocation.
     * <LI>The drop site is still active.
     * <LI>The drop site accepts the drag.
     * </UL>
     *
     * @param e the <code>DragSourceDragEvent</code>
     */
	public void dragOver(DragSourceDragEvent e) {
		int action = e.getUserAction();
		DragSourceContext context = e.getDragSourceContext();
		Point loc = e.getLocation();

		SwingUtilities.convertPointFromScreen(loc, tree);

		tree.scrollRectToVisible(new Rectangle(loc.x-20, loc.y-20, 40, 40));
		TreePath path = tree.getPathForLocation(loc.x, loc.y);			
	    int row = tree.getRowForPath(path);
	       		
		if (ProjectCompendium.isMac) {
			if (action == DnDConstants.ACTION_COPY) {
				context.setCursor(DragSource.DefaultCopyDrop);			
			} else if (action == DnDConstants.ACTION_MOVE) {
				context.setCursor(DragSource.DefaultMoveDrop);			
			}										
		} else {
			if (path != null) {
				DefaultMutableTreeNode thenode = (DefaultMutableTreeNode)path.getLastPathComponent();
				CheckNode check = (CheckNode)thenode.getUserObject();		
				if (check.isGroup()) {	
					if (action == DnDConstants.ACTION_COPY) {
						context.setCursor(DragSource.DefaultCopyDrop);			
					} else if (action == DnDConstants.ACTION_MOVE) {
						context.setCursor(DragSource.DefaultMoveDrop);			
					}
					if (highlightRow != row) { 
						highlightRow = row;
						Thread thread = new Thread() {
							public void run() {
								tree.repaint();
							}
						};
						thread.start();
					} 	
				} else {
					if (highlightRow != -2) { 
						highlightRow = -2;							
						Thread thread = new Thread() {
							public void run() {
								tree.repaint();
							}
						};
						thread.start();
					}
					
					if (action == DnDConstants.ACTION_COPY) {
						context.setCursor(DragSource.DefaultCopyNoDrop);			
					} else if (action == DnDConstants.ACTION_MOVE) {
						context.setCursor(DragSource.DefaultMoveNoDrop);			
					}				
				}
			} else {
				if (highlightRow != -2) { 
					highlightRow = -2;							
					Thread thread = new Thread() {
						public void run() {
							tree.repaint();
						}
					};
					thread.start();
				}
				
				if (action == DnDConstants.ACTION_COPY) {
					context.setCursor(DragSource.DefaultCopyNoDrop);			
				} else if (action == DnDConstants.ACTION_MOVE) {
					context.setCursor(DragSource.DefaultMoveNoDrop);			
				}							
			}
		}
	}

    /**
     * Called when the user has modified the drop gesture.
     * This method is invoked when the state of the input
     * device(s) that the user is interacting with changes.
     * Such devices are typically the mouse buttons or keyboard
     * modifiers that the user is interacting with.
    *
     * @param e the <code>DragSourceDragEvent</code>
     */
	public void dropActionChanged(DragSourceDragEvent e) {}
	
	
	//TARGET
	/**
     * Called if the user has modified
     * the current drop gesture.
     * <P>HERE DOES NOTHING</P>
     * @param e the <code>DropTargetDragEvent</code>
     */
	public void dropActionChanged(DropTargetDragEvent e) {}

    /**
     * Called when a drag operation is ongoing, while the mouse pointer is still
     * over the operable part of the drop site for the <code>DropTarget</code>
     * registered with this listener.
     * <P>HERE DOES NOTHING</P>
     * @param e the <code>DropTargetDragEvent</code>
     */
	public void dragOver(DropTargetDragEvent e) {
 	}

    /**
     * Called while a drag operation is ongoing, when the mouse pointer has
     * exited the operable part of the drop site for the
     * <code>DropTarget</code> registered with this listener.
     * <P>HERE DOES NOTHING</P>
     * @param e the <code>DropTargetEvent</code>
     */
	public void dragExit(DropTargetEvent e) {}

    /**
     * Called while a drag operation is ongoing, when the mouse pointer enters
     * the operable part of the drop site for the <code>DropTarget</code>
     * registered with this listener.
     * <P>HERE DOES NOTHING</P>
     * @param e the <code>DropTargetDragEvent</code>
     */
	public void dragEnter(DropTargetDragEvent e) {}

    /**
     * Called when the drag operation has terminated with a drop on
     * the operable part of the drop site for the <code>DropTarget</code>
     * registered with this listener.
     * @param e the <code>DropTargetDropEvent</code>
     */
	public void drop(DropTargetDropEvent e) {
  		//if (highlightRow != -2) {
        	//tree.repaint(tree.getRowBounds(highlightRow));
        //	highlightRow = -2;	
        //	tree.repaint();
        //}		
		
		DropTarget drop = (DropTarget)e.getSource();

		DropTargetContext context = e.getDropTargetContext();		
		context.getComponent().setCursor(new Cursor(java.awt.Cursor.DEFAULT_CURSOR));
		
		try {
		    Transferable transferable = e.getTransferable();
		    if (!transferable.isDataFlavorSupported(new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType+"; class=com.compendium.ui.tags.UITagTreePanel", null))) { //$NON-NLS-1$
		    	e.rejectDrop();
		    	return;
		    }		
		} catch(Exception ec) {
 			ec.printStackTrace(); 			 		
		}
		
		Point droploc = e.getLocation();		
	    if (drop.getComponent() instanceof JTree) {	    		    
	    	JTree tree = (JTree)drop.getComponent();   	
   			TreePath targetpath = tree.getPathForLocation(droploc.x, droploc.y);			   			
 			if (sourcepaths != null && targetpath != null) {
 				DefaultMutableTreeNode targetnode = (DefaultMutableTreeNode)targetpath.getLastPathComponent();
				CheckNode check = (CheckNode)targetnode.getUserObject();					
				if (check.getData() instanceof Code) {
			    	e.rejectDrop();					
					return;
				} else {		
					e.acceptDrop(e.getDropAction());
					
					DefaultMutableTreeNode parent = null;
					CheckNode group = null;
					Vector codegroup = null;
					String sCodeGroupID = "";					 //$NON-NLS-1$
					Code code = null;
					String sAuthor = ""; //$NON-NLS-1$
					Hashtable htCodeGroup = null;
					Date date = new Date();
	 									
					DefaultMutableTreeNode sourcenode = null;				
					CheckNode sourcecheck = null;				
					
	 				for (int i=0; i<sourcepaths.length; i++ ) {
		 				sourcenode = (DefaultMutableTreeNode)sourcepaths[i].getLastPathComponent();
						sourcecheck = (CheckNode)sourcenode.getUserObject();	
						if (sourcecheck.getData() instanceof Code) {
							try {
								codegroup = (Vector)check.getData();
								sCodeGroupID = (String)codegroup.elementAt(0);								
								code = (Code)sourcecheck.getData();
								sAuthor = model.getUserProfile().getUserName();
				
								htCodeGroup = model.getCodeGroup(sCodeGroupID);
								if (htCodeGroup.containsKey("children")) { //$NON-NLS-1$
									Hashtable children = (Hashtable)htCodeGroup.get("children"); //$NON-NLS-1$
									if (!children.containsKey(code.getId())) {
										
										model.getGroupCodeService().createGroupCode(model.getSession(), code.getId(), sCodeGroupID, sAuthor, date, date);
										model.addCodeGroupCode(sCodeGroupID, code.getId(), code);
				
										if (e.getDropAction() == DnDConstants.ACTION_MOVE) {
											parent = (DefaultMutableTreeNode)sourcenode.getParent();
											String sParentCodeGroupID = ""; //$NON-NLS-1$
											if (parent != null) {
												group = (CheckNode)parent.getUserObject();
												if (group.isGroup()) {									
													sParentCodeGroupID = (String)((Vector)group.getData()).elementAt(0);
													model.getGroupCodeService().delete(model.getSession(), code.getId(), sParentCodeGroupID);
													model.removeCodeGroupCode(sParentCodeGroupID, code.getId());
												}
											}
										}
														
										// YOU YOU ARE ADDING OR REMOVING FROM THE ACTIVEGROUP,
										// REFRESH THE CHOICE BOX ON THE MAIN TOOLBAR
										if (sCodeGroupID.equals(ProjectCompendium.APP.getActiveCodeGroup())) {
											ProjectCompendium.APP.updateCodeChoiceBoxData();
										}
										
										this.updateTreeData();
											
							     		DefaultMutableTreeNode node = findCodeGroup(sCodeGroupID);
							     		if (node != null) {
							     			tree.expandPath(new TreePath(node.getPath()));
							     		}									
									} else {
										//ProjectCompendium.APP.displayError("That tag is already in that group");
									}
								}							
							} catch (SQLException se) {
								ProjectCompendium.APP.displayError("Error (UITagTreePanel.drop)\n\n"+se.getMessage()); //$NON-NLS-1$
							}
						}						
					}
				}
 			}
	    }			
		e.dropComplete(true);							 				
	}		
}