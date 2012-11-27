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

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.awt.dnd.*;
import java.awt.datatransfer.*;
import java.sql.SQLException;
import java.util.*;
import java.io.*;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;

import com.compendium.ProjectCompendium;
import com.compendium.core.datamodel.*;
import com.compendium.meeting.MeetingEvent;
import com.compendium.ui.IUIConstants;
import com.compendium.ui.UIViewFrame;

/**
 * This class create a stencil icon which can be dragged and creat a node.
 *
 * @author	Michelle Bachler
 */
public class UIDraggableTreeCellRenderer extends DefaultTreeCellRenderer 
								implements DragSourceListener, DragGestureListener, DropTargetListener,
											Transferable, MouseListener {

	private Icon leafIcon = null;
	private Icon openIcon = null;
	private Icon closedIcon = null;
	
	/** The DragSource object associated with this draggable item.*/
	private DragSource 			dragSource;

	/** The drop target object associated with this draggable item.*/
	private DropTarget dropTarget = null;
	
	/** The tooltip text.*/
	private String 				sTip 			= ""; //$NON-NLS-1$

	private Code				oCode			= null;
		
	private IModel model = null;
	private PCSession session = null;
	
	private JTree	tree = null;
	private DefaultMutableTreeNode oTreeNode = null;
	private Vector  vtGroupItem = null;
	
	private boolean				isGroup			= false;

	/** The data flavors supported by this class.*/
    public static final 		DataFlavor[] supportedFlavors = { null };
	static    {
		try { supportedFlavors[0] = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType); }
		catch (Exception ex) { ex.printStackTrace(); }
	}

	/**
	 * The Constructor.
	 */
  	public UIDraggableTreeCellRenderer() {
  		
  		model = ProjectCompendium.APP.getModel();
  		session = model.getSession();
  		
		dragSource = new DragSource();
		dragSource.createDefaultDragGestureRecognizer((Component)this, DnDConstants.ACTION_COPY, this);
		
	    dropTarget = new DropTarget((JComponent)this, this);
		
		// GET ICONS
		DefaultTreeCellRenderer check = new DefaultTreeCellRenderer();
		leafIcon = check.getLeafIcon();
		openIcon = check.getOpenIcon();
		closedIcon = check.getClosedIcon();
  	}
  	
  	/**
  	 * Update the code in the database with the new label
  	 */
  	/*private void updateCodeLabel(String label) {
  		try {
  			oCode.setName(label);
  		} catch (Exception io) {
  			// message
  		}
  	}*/
  	
	public Component getTreeCellRendererComponent(JTree tree, Object value,
			boolean selected, boolean expanded, boolean leaf, int row,
			boolean hasFocus) {				
		
		oTreeNode = (DefaultMutableTreeNode) value;
		
       	super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
		
 		String text = ""; //$NON-NLS-1$
		this.tree = tree;

		isGroup = false;
		
 		if (oTreeNode.getUserObject() instanceof Vector) {
			Vector item = (Vector)oTreeNode.getUserObject();
			String sCodeGroupID = (String)item.elementAt(0);
			isGroup = true;
			// DISTIGUISH THE ACTIVE GROUP
			if ( (ProjectCompendium.APP.getActiveCodeGroup()).equals(sCodeGroupID) )
				setForeground(IUIConstants.DEFAULT_COLOR);

			setText((String)item.elementAt(1));
		} else {
			oCode = (Code)oTreeNode.getUserObject();
			text = oCode.getName();
			try {
				int count = (model.getCodeService()).getNodeCount(model.getSession(), oCode.getId());
				setText(text+" ("+count+")");					 //$NON-NLS-1$ //$NON-NLS-2$
			}
			catch(Exception ex) {
				ex.printStackTrace();
				//ProjectCompendium.APP.displayError("Exception: (UICodeMaintPanel.getListCellRendererComponent) \nUnable to calculate usage for "+code.getName() +"\n"+ex.getMessage());
			}				
		}

		// YOU CAN'T SELECT A GROUP HEADING!
		if (!leaf) {
			selected = false;
			tree.removeSelectionRow(row);
		}

		Icon icon = null;
		if (isGroup) {
			if (expanded)
				icon = openIcon;
			else
				icon = closedIcon;			
		} else if (leaf) {
			icon = leafIcon;
		}

		setIcon(icon);
		//repaint();

		return this;
	}
  	
	/**
	 * Return the code associated with this draggable item.
	 * @return Code the code associated with this draggable item.
	 */
	public Code getCode() {
		return oCode;
	}

	// CODE METHODS
	/**
	 * Check that a code with the given text does not already exist.
	 * Return true if a match found, else false.
	 * @param codeID the id of the code to ignore.
	 * @param text the text to check.
	 * @return boolean if the code with the given name already exists.
	 */
	private boolean checkCode(String codeID, String text) {

		/*int count = vtCodesSort.size();
		Code code = null;
		String innertext = "";
		for(int i=0; i<count; i++) {
			code = (Code)vtCodesSort.elementAt(i);
			innertext = code.getName();
			if (innertext.equals(text) && !code.getId().equals(codeID))
				return true;
		}*/

		return false;
	}

	/**
	 * Handle a request to save an edit on a code.
	 */
	/*public void onSaveCode() {

		if (oCode != null) {
			try {
				String sName = txtField.getText();
				if (!sName.equals("")) {
					String sCodeID = oCode.getId();

					//CHECK NAME DOES NOT ALREADY EXIST
					if (checkCode(sCodeID, sName)) {
						ProjectCompendium.APP.displayMessage("You already have a tag called "+sName+"\n\nPlease try again\n\n", "Save Tags");
						txtField.setEditable(true);
						txtField.requestFocus();
					}
					else {
						txtField.setEnabled(false);
						oCode.setName(sName); // Updates Database

						// UPDATE MODEL
						model.replaceCode(oCode);
					}
				}
				else {
					ProjectCompendium.APP.displayMessage("You must give this new tag a name", "Save Tags");									
					txtField.requestFocus();
				}
			}
			catch(Exception ex) {
				ProjectCompendium.APP.displayError("Exception: (UICodeCodeMaintPanel.onSaveCode) " + ex.getMessage());
			}
		}
	}*/

	/**
	 * Handle a request to delete the code.
	 */
	/*public void onDeleteCode() {

		try {
			// CHECK FOR ASSOCIATIONS
			int count = model.getCodeService().getNodeCount(session, oCode.getId());
			if (count > 0) {
				ProjectCompendium.APP.displayMessage("There are still nodes associated with this tag, so it cannot be deleted", "Delete Tag");
				return;
			}
			else {
				String sCodeID = oCode.getId();

				// UPDATE DATABASE
				model.getCodeService().delete(session, sCodeID);

				// UPDATE MODEL
				model.removeCode(oCode);
			}
		}
		catch(Exception ex) {
			ProjectCompendium.APP.displayError("Exception: (UICodeCodeMaintPanel.onDeleteCode) " + ex.getMessage());
		}
	}*/

	
	// CODE GROUP METHODS
	
	/**
	 * Add the given code to the current code group
	 */
	private void onAddToGroup(Code code) {

		if (vtGroupItem == null) {
			return;			
		}
		
		String sCodeGroupID = ""; //$NON-NLS-1$
		Vector group = vtGroupItem;
		sCodeGroupID = (String)group.elementAt(0);

		try {
			// ONLY ADD IF IT WAS NOT AN EXISITING CODE
			// check model
			//if ( !htExistingCodes.containsKey(code.getId()) ) {
				String sAuthor = model.getUserProfile().getUserName();
				Date date = new Date();

				// UPDATE DATABASE
				model.getGroupCodeService().createGroupCode(session, code.getId(), sCodeGroupID, sAuthor, date, date);

				// UPDATE MODEL
				model.addCodeGroupCode(sCodeGroupID, code.getId(), code);
			//}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void onRemoveFromGroup(Code code) {
		if (vtGroupItem == null) {
			return;			
		}

		String sCodeGroupID = ""; //$NON-NLS-1$
		Vector group = vtGroupItem;
		sCodeGroupID = (String)group.elementAt(0);
		
		try {
			// ONLY REMOVE, IF IT WAS AN EXISTING CODE
			// CHECK MODEL
			//if ( htExistingCodes.containsKey(oldcode.getId()) ) {

				// UPDATE DATABASE
				model.getGroupCodeService().delete(session, code.getId(), sCodeGroupID );
				// UPDATE MODEL
				model.removeCodeGroupCode(sCodeGroupID, code.getId());
			//}
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}

		// YOU YOU ARE ADDING OR REMOVING FROM THE ACTIVEGROUP,
		// REFRESH THE CHOICE BOX ON THE MAIN TOOLBAR
		if (sCodeGroupID.equals(ProjectCompendium.APP.getActiveCodeGroup())) {
			ProjectCompendium.APP.updateCodeChoiceBoxData();
		}

		//oParentDialog.updateTreeData();
	}
	
	/**
	 * Set the current code group as the active group.
	 */
	private void onActiveGroup() {

		String newActive = (String)vtGroupItem.elementAt(0);
		String oldActive = ProjectCompendium.APP.getActiveCodeGroup();
		if (oldActive.equals(newActive)) {
			if (ProjectCompendium.APP.setActiveCodeGroup("")) { //$NON-NLS-1$
				//oParentDialog.updateTreeData();
			}
		}
		else if (ProjectCompendium.APP.setActiveCodeGroup(newActive)) {
			//oParentDialog.updateTreeData();
		}
	}	
	
	/**
	 * Process the adding of a new code group.
	 */
	/*private void onAddGroup() {

		try {
			String sName = txtField.getText();
			String sCodeGroupID = (String)vtGroupItem.elementAt(0);
			Date date = new Date();
			String sAuthor = model.getUserProfile().getUserName();

			//ADD NEW CODE TO DATABASE
			(model.getCodeGroupService()).createCodeGroup(session, sCodeGroupID, sAuthor, sName, date, date);

			// UPDATE MODEL
			Vector group = new Vector(2);
			group.addElement(sCodeGroupID);
			group.addElement(sName);
			model.addCodeGroup(sCodeGroupID, group);
		}
		catch(Exception ex) {
			ProjectCompendium.APP.displayError("Exception: (UICodeGroupMaintPanel.onAddGroup) " + ex.getMessage());
		}

		//ProjectCompendium.APP.updateGroupChoiceBoxData();
		//oParentDialog.updateTreeData();
	}*/

	/**
	 * Process the saving of an edited code group name.
	 */
	/*public void onEditGroup() {

		try {
			String sName = txtField.getText();
			String sCodeGroupID = (String)vtGroupItem.elementAt(0);
			String sUserID = model.getUserProfile().getId();
			
			// UPDATE DATABASE
			(model.getCodeGroupService()).setName(session, sCodeGroupID, sName, new Date(), sUserID);

			// UPDATE MODEL
			model.replaceCodeGroupName(sCodeGroupID, sName);

			//update toolbar codes box
			//??? ProjectCompendium.APP.updateCodeChoiceBoxData();

			//ProjectCompendium.APP.updateGroupChoiceBoxData();
			//oParentDialog.updateTreeData();
		}
		catch(Exception ex) {
			//ex.printStackTrace();
			ProjectCompendium.APP.displayError("Exception: (DraggableTabItem.onSaveGroup) " + ex.getMessage());
		}
	}*/

	/**
	 * Process deleting the selected code groups.
	 */
	/*public void onDeleteGroup() {

		try {
			String sCodeGroupID = (String)vtGroupItem.elementAt(0);

			// UPDATE DATABASE
			(model.getCodeGroupService()).deleteCodeGroup(session, sCodeGroupID);

			// UPDATE MODEL
			model.removeCodeGroup(sCodeGroupID);

			//update toolbar codes box
			if (ProjectCompendium.APP.getActiveCodeGroup().equals(sCodeGroupID)) {
				ProjectCompendium.APP.setActiveCodeGroup("");
			}

			//updateGroupList();
			ProjectCompendium.APP.updateCodeChoiceBoxData();
			//oParent.updateTreeData();
		}
		catch(Exception ex) {
			ex.printStackTrace();
			ProjectCompendium.APP.displayError("Exception: (UICodeGroupMaintPanel.onDeleteGroup) " + ex.getMessage());
		}
	}*/
	
	// MOUSE LISTENER METHODS
	/**
	 * Handles the single and double click events.
	 * @param evt the associated MouseEvent.
	 */
  	public void mouseClicked(MouseEvent evt) {
		boolean isRightMouse = SwingUtilities.isRightMouseButton(evt);
		boolean isLeftMouse = SwingUtilities.isLeftMouseButton(evt);
		if (ProjectCompendium.isMac &&
			(evt.getButton() == 3 && evt.isShiftDown())) {
			isRightMouse = true;
			isLeftMouse = false;
		}

		/*if (isRightMouse) {
			showPopupMenu(evt.getX(),evt.getY());
		}
		else {
			if (evt.getSource() instanceof JTextField) {
				txtField.setEditable(true);
				txtField.setBackground(Color.WHITE);
			}
		}*/
  	}
  	
  	/**
  	 * Open right-click menu for this item.
  	 */
  	private void showPopupMenu(int x, int y) {
  		
  	}
  	
	/**
	 * Handles mouse pressed events
	 * @param evt, the associated MouseEvent.
	 */
  	public void mousePressed(MouseEvent evt) {}
  	
	/**
	 * Handles mouse released events
	 * @param evt the associated MouseEvent.
	 */
  	public void mouseReleased(MouseEvent evt) {}
  	  	
	/**
	 * Visualizes when a mouse enters the node.
	 * Also, display any detail text for the node in the status bar.
	 * @param evt, the associated MouseEvent.
	 */
  	public void mouseEntered(MouseEvent evt) {}
  	
	/**
	 * Visualizes when a mouse exits the node.
	 * @param evt the associated MouseEvent.
	 */
  	public void mouseExited(MouseEvent evt) {}
	
	
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
		return flavor.isMimeTypeEqual(DataFlavor.javaJVMLocalObjectMimeType);
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
		if (flavor.isMimeTypeEqual(DataFlavor.javaJVMLocalObjectMimeType))
			return this;
		else return null;
	}

	
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
	    if (in instanceof MouseEvent) {
			MouseEvent evt = (MouseEvent)in;
			boolean isLeftMouse = SwingUtilities.isLeftMouseButton(evt);

		    if (isLeftMouse && !evt.isAltDown()) {
				dragSource.startDrag(e, DragSource.DefaultCopyDrop, this, this);
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
	 * HERE THE METHOD DOES NOTHING.
     *
     * @param e the <code>DragSourceDragEvent</code>
     */
	public void dragOver(DragSourceDragEvent e) {}

    /**
     * Called when the user has modified the drop gesture.
     * This method is invoked when the state of the input
     * device(s) that the user is interacting with changes.
     * Such devices are typically the mouse buttons or keyboard
     * modifiers that the user is interacting with.
	 * HERE THE METHOD DOES NOTHING.
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
	public void dropActionChanged(DropTargetDragEvent e) {
	    //System.out.println("IN dropActionChanged of Target");
	}

    /**
     * Called when a drag operation is ongoing, while the mouse pointer is still
     * over the operable part of the drop site for the <code>DropTarget</code>
     * registered with this listener.
     * <P>HERE DOES NOTHING</P>
     * @param e the <code>DropTargetDragEvent</code>
     */
	public void dragOver(DropTargetDragEvent e) {
	     //System.out.println("dragtargetdrag event at "+e.getLocation());
	}

    /**
     * Called while a drag operation is ongoing, when the mouse pointer has
     * exited the operable part of the drop site for the
     * <code>DropTarget</code> registered with this listener.
     * <P>HERE DOES NOTHING</P>
     * @param e the <code>DropTargetEvent</code>
     */
	public void dragExit(DropTargetEvent e) {
	    //System.out.println("In drag exit of Target");
	}

    /**
     * Called while a drag operation is ongoing, when the mouse pointer enters
     * the operable part of the drop site for the <code>DropTarget</code>
     * registered with this listener.
     * <P>HERE DOES NOTHING</P>
     * @param e the <code>DropTargetDragEvent</code>
     */
	public void dragEnter(DropTargetDragEvent e) {
	    //System.out.println("dragEnter - about to accept DnDConstants.ACTION_LINK");
	    //e.acceptDrag(DnDConstants.ACTION_LINK);
	    //e.acceptDrag(DnDConstants.ACTION_MOVE);
	}

    /**
     * Called when the drag operation has terminated with a drop on
     * the operable part of the drop site for the <code>DropTarget</code>
     * registered with this listener.
     * <p>
	 * Process a drop for createing links - CURRENTLY ONLY ON THE MAC (WINDOW/LINUX use mouse events).
     * <P>
     * @param e the <code>DropTargetDropEvent</code>
     */
	public void drop(DropTargetDropEvent e) {
		if (!isGroup) {
			return;
		} else {
			DropTarget drop = (DropTarget)e.getSource();

		    if (drop.getComponent() instanceof UIDraggableTreeCellRenderer) {
		    	UIDraggableTreeCellRenderer item = (UIDraggableTreeCellRenderer)drop.getComponent();
		    	//System.out.println("item="+item); //$NON-NLS-1$
				/*try {
					Transferable trans = e.getTransferable();
					Object obj = trans.getTransferData(DataFlavor.javaJVMLocalObjectMimeType);
	
					if (obj != null && (obj instanceof UIDraggableTagItem)) {
						
					}
					    String path = (String)obj;
					    int index = path.indexOf("/");
					    String sViewID = path.substring(0, index);
					    sNodeID = path.substring(index+1);
	
						View view  = oNode.getModel().getView(sViewID);
						if (view != null) {
							oViewFrame = ProjectCompendium.APP.getViewFrame(view, view.getLabel());
							oViewPane = ((UIMapViewFrame)oViewFrame).getViewPane();
					    	if (oViewPane != null) {
								Object obj2 = oViewPane.get(sNodeID);
								if (obj2 instanceof UINode) {
						    		uinode = (UINode)obj2;
								}
							}
				    	}
					}
				}
				catch(IOException io) {
					io.printStackTrace();
				}
				catch(UnsupportedFlavorException io) {
					io.printStackTrace();
				}*/
		    }
		}
	}	
}
