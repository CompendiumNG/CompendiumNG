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

package com.compendium.ui.edits;

import java.util.*;
import java.sql.SQLException;
import javax.swing.*;
import javax.swing.undo.*;

import com.compendium.*;
import com.compendium.ui.*;
import com.compendium.ui.plaf.*;

import com.compendium.core.datamodel.*;
import com.compendium.core.ICoreConstants;

/**
 * The Abstract class for undo/redo edits (paste, cut, delete operations)
 *
 * @author	Beatrix Zimmermann / Michelle Bachler
 */
public abstract class PCEdit extends AbstractUndoableEdit {

	/** Used while process to check VIews processed.*/
	private Hashtable ht_checkViews = new Hashtable(51);

	/** A list of the UINode/NodePosition objects for this edit.*/
	public Vector vtUndoNodes = new Vector(101);

	/** a List of the UILink object for this edit.*/
	public Vector vtUndoLinks = new Vector(101);

	/** Helper Vector when undoing and edit.*/
	private Vector undoNodeIndexes = new Vector();

	/** The view frame for the view being edited.*/
	public UIViewFrame oViewFrame	 = null;

	/** If a map view is being edited, the UIViewPane for the map.*/
	public UIViewPane	oViewPane;

	/**
	 * Constructor.
	 * @param viewFrame com.compendium.ui.UIViewFrame, the frame of the View being editied.
	 */
	public PCEdit (UIViewFrame viewFrame) {
		oViewFrame = viewFrame;
		if (oViewFrame instanceof UIListViewFrame) {
		} else {
			oViewPane = ((UIMapViewFrame)viewFrame).getViewPane();
		}
	}
	

	/**
	 * Add a <code>UINode</code> object to the list of nodes for this edit.
	 * @param uinode com.compendium.ui.UINode, the node to add.
	 */
	public void AddNodeToEdit (UINode uinode) {
		if(!vtUndoNodes.contains(uinode)){
			vtUndoNodes.addElement(uinode);
		}
	}
	
	/**
	 * Add a <code>NodePosition</code> object to the list of nodes, and the the undoNodeIndexes Vector.
	 * @param uinode com.compendium.core.datamodel.NodePosition, the <code>NodePosition</code> object to add.
	 * @param index, the index to set when adding the node to the undoNodeIndexes Vector.
	 */
	public void AddNodeToEdit (NodePosition node, int index) {
		if(!vtUndoNodes.contains(node)){
			vtUndoNodes.addElement(node);
			undoNodeIndexes.addElement(new Integer(index));
		}
	}

	/**
	 * Add a <code>UILink</code> object to the list of links for this edit.
	 * @param uilink com.compendium.ui.UILink, the link to add.
	 */
	public void AddLinkToEdit (UILink uilink) {
		if(!vtUndoLinks.contains(uilink)){
			vtUndoLinks.addElement(uilink);
		}
	}

	/**
	 * Undoes the last edit operation of this type.
	 *
     * @exception CannotRedoException if <code>canRedo</code> returns <code>false</code>
     * @see	#canUndo
 	 */
	public abstract void undo() throws CannotUndoException;

	/**
     * Redoes the previous edit of this type.
     *
     * @exception CannotRedoException if <code>canRedo</code> returns <code>false</code>
     * @see	#canRedo
 	 */
	public abstract void redo() throws CannotRedoException;

    /**
     * Returns true if this edit is <code>alive</code>
     * and <code>hasBeenDone</code> is <code>true</code>.
	 * HERE: always return true.
     *
     * @return true if this edit is <code>alive</code> and <code>hasBeenDone</code> is <code>true</code>
     *
     * @see	#undo
     * @see	#redo
     */
	public boolean canUndo() { return true; }

    /**
     * Returns <code>true</code> if this edit is <code>alive</code>
     * and <code>hasBeenDone</code> is <code>false</code>.
	 * HERE: always return true.
     *
     * @return <code>true</code> if this edit is <code>alive</code> and <code>hasBeenDone</code> is <code>false</code>
	 *
     * @see	#undo
     * @see	#redo
     */
	public boolean canRedo() { return true; }

    /**
     *  Abstract, should be overriden by subclasses to return the appropriate name.
     *
     *	@return the presentation name for this edit.
     */
	public abstract String getPresentationName();

	/**
	 * Process the undoing of a delete.
	 */
	public void unDeleteNodes() {

		ht_checkViews.clear();

		View view = oViewFrame.getView();
		Vector vtTempNodes = new Vector();
		vtTempNodes = (Vector)vtUndoNodes.clone();

		UIList uilist = ((UIListViewFrame)oViewFrame).getUIList();

		IModel model = ProjectCompendium.APP.getModel();
		PCSession session = model.getSession();
		String sViewID = view.getId();

		for(int i=0;i<vtTempNodes.size();i++) {

			NodePosition nodePos = (NodePosition)vtTempNodes.elementAt(i);
			String sNodeID = nodePos.getNode().getId();

			boolean restored = false;
			try {
				restored = model.getNodeService().restoreNode(session, sNodeID);
				if (restored) {
					nodePos = model.getNodeService().restoreNodeView(session, sNodeID, sViewID);
				}
			}
			catch(SQLException ex) {
				System.out.println(ex.getMessage());
			}

			view.addMemberNode(nodePos);

			if (nodePos.getNode() instanceof View) {
				View deletedView = (View)nodePos.getNode();

				if (View.isListType(deletedView.getType())) {
					restoreDeletedNodes(deletedView);
				}
				else {
					restoreDeletedNodesAndLinks(deletedView);
				}
			}

			// DON'T ADD TWICE IN CASE ALREADY RESTORED FROM TRASHBIN
			if (uilist.getIndexOf(nodePos.getNode()) == -1)
				uilist.insertNode(nodePos, ((Integer)undoNodeIndexes.elementAt(i)).intValue());
		}
		uilist.updateTable();
	}

	/**
	 * Restore deleted nodes and links.
	 * @exception CannotUndoException.
	 */
	public void unDeleteNodesAndLinks() throws CannotUndoException {

		ht_checkViews.clear();

		//get the active frame which will give the view to be searched
		ProjectCompendium.APP.setWaitCursor();

		if (View.isListType(oViewFrame.getView().getType())) {
			unDeleteNodes();
			ProjectCompendium.APP.setDefaultCursor();
			return;
		}

		//restore all the nodes
		Vector vtTempNodes = new Vector();
		vtTempNodes = (Vector)vtUndoNodes.clone();

		IModel model = oViewPane.getView().getModel();
		PCSession session = model.getSession();
		String sViewID = oViewFrame.getView().getId();
		UIViewPane viewpane = ((UIMapViewFrame)oViewFrame).getViewPane();

		for(int i=0;i<vtTempNodes.size();i++) {

			boolean restored = false;
			UINode uinode = (UINode)vtTempNodes.elementAt(i);
			NodeSummary node = uinode.getNode();
			NodePosition nodepos = uinode.getNodePosition();
			node.initialize(model.getSession(),model);
			String sNodeID = node.getId();

			try {
				restored = model.getNodeService().restoreNode(session, sNodeID);
				if (restored) {
					nodepos = model.getNodeService().restoreNodeView(session, sNodeID, sViewID);
				}


				// REPLACE ANY EXISITING INSTANCES I.E. GENERATED FROM A RESTORE
				// NEED TO IMPLEMENT THIS PROPERLY (once restored can not longer undo delete)
				UINode newUINode = (UINode)viewpane.get(sNodeID);
				if (newUINode != null)
					viewpane.removeObject(sNodeID);

				viewpane.getUI().addNode(uinode);
				oViewFrame.getView().addMemberNode(nodepos);

				if (uinode.getNode() instanceof View) {
					View deletedView = (View)uinode.getNode();
					if (View.isListType(deletedView.getType()))
						restoreDeletedNodes(deletedView);
					else
						restoreDeletedNodesAndLinks(deletedView);
				}

				if(!restored) {
					System.out.println("Cannot restore" + node.getLabel() +" Node may have been purged from the DB"); //$NON-NLS-1$ //$NON-NLS-2$
				}
				else {
					uinode.getUI().refreshBounds();
				}
			}
			catch(SQLException ex) {
				ProjectCompendium.APP.displayError("Error: (PCEdit.unDeleteNodesAndLinks)\n\n" + ex.getLocalizedMessage()); //$NON-NLS-1$
			}
		}

		// RESTORE ALL LINKS IF THIS VIEW IS A MAP
		if (View.isMapType(oViewFrame.getView().getType())) {

			Vector vtTempLinks = new Vector();
			vtTempLinks = (Vector)vtUndoLinks.clone();

			for(int i=0; i < vtTempLinks.size(); i++) {

				boolean restored = false;
				UILink uilink = (UILink)vtTempLinks.elementAt(i);

				// RESTORE THE LINK AND THE VIEWLINK
				try {
					String sLinkID = uilink.getLink().getId();
					restored = model.getLinkService().restoreLink(session, sLinkID);
					restored = model.getViewService().restoreLink(session, sViewID, sLinkID);
				}
				catch(Exception ex) {
					ProjectCompendium.APP.displayError("Error: (PCEdit.unDeleteNodesAndLinks-2)\n\n" + ex.getLocalizedMessage()); //$NON-NLS-1$
				}

				boolean wasSelected = uilink.isSelected();
				if (restored) {
					Link link = uilink.getLink();

					//add the link to the view if it isn't already in there
					UILink uiLinkInView = (UILink)oViewPane.get(link.getId());
					if (uiLinkInView == null) {
						// RESTORE LINK TO VIEW AND ASSOCIATED NODES						
						oViewFrame.getView().addMemberLink(uilink.getLinkProperties());
						UILink newuilink = viewpane.getUI().addLink(uilink.getLinkProperties());
						vtUndoLinks.removeElement(uilink);
						vtUndoLinks.addElement(newuilink);
						if (uilink.isSelected())
							oViewPane.setSelectedLink(newuilink, ICoreConstants.MULTISELECT);
					}
					else {
						uilink = uiLinkInView;
						if (uiLinkInView.isSelected())
							oViewPane.setSelectedLink(uilink, ICoreConstants.MULTISELECT);
					}
				}
				else { //create new one

					String id = oViewPane.getView().getModel().getUniqueID();

					String type = uilink.getLink().getType();
					UINode uifrom = uilink.getFromNode();
					UINode uito = uilink.getToNode();
					NodeSummary from = uilink.getFromNode().getNode();
					NodeSummary to	= uilink.getToNode().getNode();
					int permission = ICoreConstants.WRITE;
					String sOriginalID = id;

					try {
						//add the link to the datamodel view
						LinkProperties linkProps = (LinkProperties)oViewPane.getView().addMemberLink(type,
																sOriginalID,
																ProjectCompendium.APP.getModel().getUserProfile().getUserName(),
																from,
																to,
																uilink.getText(),
																uilink.getLinkProperties());
						
						linkProps.getLink().initialize(oViewPane.getView().getModel().getSession(), oViewPane.getView().getModel());

						//create a link in UI layer - what about deleting the old object???
						UILink newuilink = new UILink(linkProps.getLink(), linkProps, uifrom, uito);
						oViewPane.add(newuilink, (UIViewPane.LINK_LAYER));
						newuilink.setBounds(uilink.getPreferredBounds());

						uifrom.addLink(newuilink);
						uito.addLink(newuilink);

						newuilink.setSelected(uilink.isSelected());
						if (newuilink.isSelected()) {
							oViewPane.removeLink(uilink);
							oViewPane.setSelectedLink(newuilink,ICoreConstants.MULTISELECT);
						}

						vtUndoLinks.removeElement(uilink);
						vtUndoLinks.addElement(newuilink);
					}
					catch(Exception ex) {
						System.out.println("Error: (PCEdit.unDeleteNodesAndLinks-3) "+ex.getMessage()); //$NON-NLS-1$
					}
				}
			}
		}
		ProjectCompendium.APP.setDefaultCursor();
	}

	/**
	 * Re-Delete nodes.
	 */
	public void reDeleteNodes() {

		View parentView = oViewFrame.getView();
		Vector vtTempNodes = new Vector();
		vtTempNodes = (Vector)vtUndoNodes.clone();

		for(int i=0;i<vtTempNodes.size();i++) {

			boolean deleted = false;
			try {
				NodePosition nodePos = (NodePosition)vtTempNodes.elementAt(i);
				deleted = parentView.removeMemberNode(nodePos.getNode());
				((UIListViewFrame)oViewFrame).getUIList().deleteNode(((Integer)undoNodeIndexes.elementAt(i)).intValue());

				// IF NODE IS A VIEW AND IF NODE WAS ACTUALLY LAST INSTANCE AND WAS DELETED, DELETE CHILDREN
				if (nodePos.getNode() instanceof View && deleted) {
					View childView = (View)nodePos.getNode();
					UIViewFrame childViewFrame = ProjectCompendium.APP.getViewFrame(childView, childView.getLabel());
					if (childViewFrame instanceof UIMapViewFrame)
						((UIMapViewFrame)childViewFrame).deleteChildren(childView);
					else
						((UIListViewFrame)childViewFrame).deleteChildren(childView);
				}
			}
			catch(Exception ex) {
				System.out.println("Error (PCEdit.reDeleteNodes) \n\n"+ex.getMessage()); //$NON-NLS-1$
			}
		}
		((UIListViewFrame)oViewFrame).getUIList().updateTable();
	}

	/**
	 * ReDelete nodes and links.
	 * @exception CannotUndoException.
	 */
	public void reDeleteNodesAndLinks() throws CannotRedoException {

		ProjectCompendium.APP.setWaitCursor();

		if (View.isListType(oViewFrame.getView().getType())) {
			reDeleteNodes();
			ProjectCompendium.APP.setDefaultCursor();
			return;
		}

		//remove all the nodes again
		Vector vtTempNodes = new Vector();
		vtTempNodes = (Vector)vtUndoNodes.clone();

		//remove all the links again
		Vector vtTempLinks = new Vector();
		vtTempLinks = (Vector)vtUndoLinks.clone();

		for(int i=0;i<vtTempLinks.size();i++) {
			boolean restored = false;
			UILink uilink = (UILink)vtTempLinks.elementAt(i);
			LinkUI linkui = (LinkUI)uilink.getUI();
			linkui.deleteLink(uilink);
		}

		//re-delete the nodes
		for(int i=0;i<vtTempNodes.size();i++) {

			boolean restored = false;
			UINode uinode = (UINode)vtTempNodes.elementAt(i);
			View parentView = oViewPane.getView();

			boolean deleted = uinode.getUI().deleteNodeAndLinks(uinode, this);

			// IF NODE IS A VIEW AND IF NODE WAS ACTUALLY LAST INSTANCE AND WAS DELETED, DELETE CHILDREN
			if ((uinode.getNode() instanceof View) && deleted) {
				View childView = (View)uinode.getNode();
				UIViewFrame childViewFrame = ProjectCompendium.APP.getViewFrame(childView, childView.getLabel());
				if (childViewFrame instanceof UIMapViewFrame)
					((UIMapViewFrame)childViewFrame).deleteChildren(childView);
				else
					((UIListViewFrame)childViewFrame).deleteChildren(childView);
			}
		}
		ProjectCompendium.APP.setDefaultCursor();
	}

	/**
	 * Restore deleted nodes.
	 * @param deletedView com.compendium.datamodel.View, the View to restore.
	 * @exception CannotUndoException.
	 */
	public void restoreDeletedNodes(View deletedView) {

		try {
			Vector deletedNodes = deletedView.getDeletedNodes();
			UIViewFrame deletedUIViewFrame = ProjectCompendium.APP.getViewFrame(deletedView, deletedView.getLabel());

			UIList uilist = ((UIListViewFrame)deletedUIViewFrame).getUIList();

			IModel model = oViewPane.getView().getModel();
			PCSession session = model.getSession();

			for (int i = 0; i < deletedNodes.size(); i++) {

				NodePosition np = (NodePosition)deletedNodes.elementAt(i);

				String sNodeID = np.getNode().getId();
				String sViewID = deletedView.getId();

				boolean restored = model.getNodeService().restoreNode(session, sNodeID);
				if (restored) {
					np = model.getNodeService().restoreNodeView(session, sNodeID, sViewID);
				}

				if (np != null) {
					deletedView.addMemberNode(np);

					if (np.getNode() instanceof View) {

						// DON'T RESTORE THE SAME VIEW TWICE
						if (!ht_checkViews.containsKey(sNodeID)) {
							ht_checkViews.put(sNodeID, np.getNode());

							View view = (View)np.getNode();
							if (View.isListType(view.getType())) {
								restoreDeletedNodes(view);
							} else {
								restoreDeletedNodesAndLinks(view);
							}
						}
					}
					// DON'T ADD THE SAME NODE TWICE TO THE SAME LIST
					if (uilist.getIndexOf(np.getNode()) == -1) { 
						uilist.insertNode(np, np.getYPos()/10 - 1);
					}
				}
			}

			uilist.updateTable();
			deletedNodes.removeAllElements();
		}
		catch(Exception e) {
			ProjectCompendium.APP.displayError("Error: (PCEdit.restoreDeletedNodes)\n\n" + e.getLocalizedMessage()); //$NON-NLS-1$
		}
	}

	/**
	 * Restore deleted nodes and links.
	 * @param deletedView com.compendium.datamodel.View, the view to restore.
	 * @exception CannotUndoException.
	 */
	public void restoreDeletedNodesAndLinks(View deletedView) {

		try {

			IModel model = ProjectCompendium.APP.getModel();
			PCSession session = model.getSession();

			UIViewFrame deletedUIViewFrame = ProjectCompendium.APP.getViewFrame(deletedView, deletedView.getLabel());
			String sViewID = deletedView.getId();

			Vector deletedNodes = deletedView.getDeletedNodes();
			ViewPaneUI deletedViewPaneUI = ((UIMapViewFrame)deletedUIViewFrame).getViewPane().getUI();

			final int count = deletedNodes.size();
			for (int i = 0; i < count; i++) {

				NodePosition np = (NodePosition)deletedNodes.elementAt(i);
				String sNodeID = np.getNode().getId();

				boolean restored = model.getNodeService().restoreNode(session, sNodeID);
				if (restored) {
					np = model.getNodeService().restoreNodeView(session, sNodeID, sViewID);
				}

				if (np != null) {
					// REPLACE ANY EXISITING INSTANCES I.E. GENERATED FROM A RESTORE
					// NEED TO IMPLEMENT THIS PROPERLY (once restoerd can not longer undo delete)
					UINode newUINode = (UINode) ((UIMapViewFrame)deletedUIViewFrame).getViewPane().get(sNodeID);
					if (newUINode != null)
						((UIMapViewFrame)deletedUIViewFrame).getViewPane().removeObject(sNodeID);

					deletedViewPaneUI.addNode(np);
					deletedView.addMemberNode(np);

					if (np.getNode() instanceof View) {

						// DON'T RESTORE THE SAME VIEW TWICE
						if (!ht_checkViews.containsKey(sNodeID)) {
							ht_checkViews.put(sNodeID, np.getNode());

							View view = (View)np.getNode();

							if (View.isListType(view.getType()))
								restoreDeletedNodes(view);
							else
								restoreDeletedNodesAndLinks(view);
						}
					}
				}
			}

			Vector deletedLinks = deletedView.getDeletedLinks();
			for (int i = 0; i < deletedLinks.size(); i++) {

				LinkProperties linkProps = (LinkProperties)deletedLinks.elementAt(i);
				Link link = linkProps.getLink();
				String sLinkID = link.getId();

				// RESTORE THE LINK AND THE VIEWLINK
				boolean restored = model.getLinkService().restoreLink(session, sLinkID);
				restored = model.getViewService().restoreLink(session, sViewID, sLinkID);

				if (restored) {
					//add the link to the view if it isn't already in there
					UILink newuilink = (UILink)oViewPane.get(sLinkID);
					if (newuilink == null) {
						oViewFrame.getView().addMemberLink(linkProps);
						newuilink = deletedViewPaneUI.addLink(linkProps);
					}
					if (newuilink.isSelected()) {
						oViewPane.setSelectedLink(newuilink, ICoreConstants.MULTISELECT);
					}
				}
				else { //create new one
					String type = link.getType();
					UINode uifrom = deletedViewPaneUI.getUINode(link.getFrom().getId());
					UINode uito = deletedViewPaneUI.getUINode(link.getTo().getId());
					NodeSummary from = link.getFrom();
					NodeSummary to	= link.getTo();
					int permission = ICoreConstants.WRITE;
					String sOriginalID = ""; //$NON-NLS-1$

					//add the link to the datamodel view
					LinkProperties innerlinkProps = (LinkProperties)deletedView.addMemberLink(type,
														sOriginalID,
														ProjectCompendium.APP.getModel().getUserProfile().getUserName(),
														from,
														to,
														link.getLabel(),
														linkProps);

					link.initialize(deletedView.getModel().getSession(),	deletedView.getModel());

					//create a link in UI layer - what about deleting the old object???
					UILink newuilink = new UILink(link, innerlinkProps, uifrom, uito);

					((UIMapViewFrame)deletedUIViewFrame).getViewPane().add(newuilink, (UIViewPane.LINK_LAYER));

					//newuilink.setBounds(uilink.getPreferredBounds());
					uifrom.addLink(newuilink);
					uito.addLink(newuilink);
					deletedViewPaneUI.addLink(innerlinkProps);
				}
			}
			deletedNodes.removeAllElements();
			deletedLinks.removeAllElements();
		}
		catch(Exception e) {
			e.printStackTrace();
			ProjectCompendium.APP.displayError("Error: (PCEdit.restoreDeletedNodesAndLinks)\n\n" + e.getLocalizedMessage()); //$NON-NLS-1$
		}
	}
}
