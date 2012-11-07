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

package com.compendium.core.datamodel;

import java.util.*;
import java.beans.*;

import com.compendium.core.*;


/**
 * This object store the nodes and views being used in the current session
 *
 * @author	Michelle Bachler
 */
public class NodeCache implements PropertyChangeListener {

	/** Stores the nodes and views against thier ids*/
	private Hashtable						htNodeSummary	= null;

	/** Stores the node/view ids against thier use count*/
	private Hashtable						htNodeSummaryCount = null;


	/**
	 * Constructor.
	 */
	public NodeCache() {
		htNodeSummary	= new Hashtable(51);
		htNodeSummaryCount = new Hashtable(51);
	}

	/**
	 * Adds a node/view to the cache
	 * If the node/view is already present it increases its reference by one
	 *
	 * @param PCObject object, the NodeSummary or View to add to the cache.
	 * @return boolean, true if the object passed was successfully added (was a NodeSummary or View Object).
	 */
	public boolean put(PCObject object) {

		if(object instanceof View)	{

			View view = (View)object;

			String sViewID = view.getId();
			if(!htNodeSummary.containsKey(sViewID)) {
				htNodeSummary.put(sViewID, view);
				view.addPropertyChangeListener((PropertyChangeListener)this);
				htNodeSummaryCount.put(sViewID, new Integer(1));
				return true;
			}
			else {
				int referenceCount = ((Integer)htNodeSummaryCount.get(sViewID)).intValue();
				referenceCount++;
				htNodeSummaryCount.put(sViewID, new Integer(referenceCount));
				return true;
			}
		}
		else if(object instanceof NodeSummary) {

			NodeSummary node = (NodeSummary)object;

			String sNodeID = node.getId();
			if(!htNodeSummary.containsKey(sNodeID)) {
				htNodeSummary.put(sNodeID, node);
				node.addPropertyChangeListener((PropertyChangeListener)this);
				htNodeSummaryCount.put(sNodeID, new Integer(1));
				return true;
			}
			else {
				int referenceCount = ((Integer)htNodeSummaryCount.get(sNodeID)).intValue();
				referenceCount++;
				htNodeSummaryCount.put(sNodeID, new Integer(referenceCount));
				return true;
			}
		}
		else {
			return false;
		}
	}

	/**
	 * Removes a node/view from the cache.
	 * If the node/view is already present it decreases its reference by one.
	 * If the referencecount goes to zero it removes it from the cache.
	 *
	 * @param PCObject object, the NodeSummary or View to remove from to the cache.
	 * @return boolean, true if the object passed was successfully removed (was a NodeSummary or View Object and was found).
	 */
	public boolean remove(PCObject object) {

		if(object instanceof View) {

			View view = (View)object;
			String sViewID = view.getId();
			if(!htNodeSummary.containsKey(sViewID)) {
				return false;
			}
			else {
				int referenceCount = ((Integer)htNodeSummaryCount.get(sViewID)).intValue();
				referenceCount--;

				if(referenceCount == 0) {
					htNodeSummary.remove(sViewID);
					view.removePropertyChangeListener((PropertyChangeListener)this);
					htNodeSummaryCount.remove(sViewID);
					return true;
				}
				else {
					htNodeSummaryCount.put(sViewID, new Integer(referenceCount));
					return true;
				}
			}
		}
		else if(object instanceof NodeSummary) {

			NodeSummary node = (NodeSummary)object;
			String sNodeID = node.getId();
			if(!htNodeSummary.containsKey(sNodeID)) {
				return false;
			}
			else {
				int referenceCount = ((Integer)htNodeSummaryCount.get(sNodeID)).intValue();
				referenceCount--;
				if(referenceCount == 0) {
					htNodeSummary.remove(sNodeID);
					node.removePropertyChangeListener((PropertyChangeListener)this);
					htNodeSummaryCount.remove(sNodeID);
					return true;
				}
				else {
					htNodeSummaryCount.put(sNodeID, new Integer(referenceCount));
					return true;
				}
			}
		}
		else {
			return false;
		}
	}

	/**
	 * Replace the given oldObject with the given newObject.
	 * @param PCObject oldObject, the NodeSummary or View to replace in the cache.
	 * @param PCObject newObject, the NodeSummary or View to replace with in the cache.
	 * @return boolean, true if the object was replaced (was a NodeSummary or View Object and was found).
	 */
	public boolean replace(PCObject oldObject, PCObject newObject) {

		NodeSummary node = (NodeSummary)oldObject;
		String sID = node.getId();
		if(!htNodeSummary.containsKey(sID)) {
			return false;
		}
		else {
			//node.removePropertyChangeListener((PropertyChangeListener)this); // BREAKS LOOP SENDING CHANGE EVENTS
			htNodeSummary.put(sID, newObject);
			((NodeSummary)newObject).addPropertyChangeListener((PropertyChangeListener)this);
			return true;
		}
	}

	/**
	 * Returns the number of references to the node/view else if no object found returns -1.
	 *
	 * @param PCObject object, the NodeSummary or View to get the reference count for.
	 * @return int, the count of the use of the node or view else -1.
	 */
	public int getCount(PCObject object) {

		if(object instanceof View) {

			View view = (View)object;
			String sViewID = view.getId();
			if(htNodeSummaryCount.containsKey(sViewID)) {
				int referenceCount = ((Integer)htNodeSummaryCount.get(sViewID)).intValue();
				return referenceCount;
			}
			else {
				return -1;
			}
		}
		else if(object instanceof NodeSummary) {
			NodeSummary node = (NodeSummary)object;
			String sNodeID = node.getId();
			if(htNodeSummaryCount.containsKey(sNodeID)) {
				int referenceCount = ((Integer)htNodeSummaryCount.get(sNodeID)).intValue();
				return referenceCount;
			}
			else {
				return -1;
			}
		}
		else {
			return -1;
		}
	}

	/**
	 * Returns a list of View objects in the cache.
	 *
	 * @return Enumeration, a list of the IView object help in the cache.
	 */
	public Enumeration getViews() {

		Vector views = new Vector();
		for(Enumeration e = htNodeSummary.elements();e.hasMoreElements();) {

			NodeSummary node = (NodeSummary)e.nextElement();
			if(node instanceof View)
				views.addElement(node);
		}
		return views.elements();
	}

	/**
	 * Returns the node/view with the given id.
	 *
	 * @return NodeSummary, the node/view with the given id.
	 */
	public NodeSummary getNode(String sID) {

		if(htNodeSummary.containsKey(sID)); {
			return (NodeSummary)htNodeSummary.get(sID);
		}
	}

	/**
	 * This method is to be called by links to get the model's from and to nodeSummary
	 * to update themselves for property change events for from and to node changes.
	 * The method takes the id of the input nodeSummary and checks it against its own
	 * hashtable and then returns the nodeSummary with that id. If it does not exist
	 * it throws a NoSuchElement exception.
	 *
	 * @param NodeSummary oNode, the node for which the return the node with the same node id.
	 * @return NodeSummary with the same node id as the passed NodeSummary object.
	 * @exception NoSuchElementException, if a node with the same id as the passed node was node found.
	 */
	public NodeSummary getNode(NodeSummary oNode) throws NoSuchElementException {

		String sNodeID = oNode.getId() ;

		if(!htNodeSummary.containsKey(sNodeID)) {
			throw new NoSuchElementException("Node " + sNodeID + " not found in Model ");
		}
		else {
			return (NodeSummary)htNodeSummary.get(sNodeID);
		}
	}


	/**
	 * This method updates the cache for an array of NodeSummary objects.
	 * For each NodeSummary object it calles <code>addNode</code>.
	 *
	 * @param NodeSummary[] oNodes, the array of nodes to add to the cache.
	 * @return NodeSummary[], an array of nodes added to the cache.
	 */
	public NodeSummary[] addNodes(NodeSummary[] oNodes) {

		NodeSummary[] nodeReferences = new NodeSummary[oNodes.length];

		for(int i=0;i< oNodes.length; i++) {
			nodeReferences[i] = addNode(oNodes[i]) ;
		}
		return nodeReferences;
	}

	/**
	 * Adds a NodeSummary object to cache or, if already in the cache, increments its use count.
	 *
	 * @param NodeSummary oNode, the node to add to the cache.
	 * @return NodeSummary, the node added to the cache.
	 */
	public NodeSummary addNode(NodeSummary oNode) {

		String sNodeID = oNode.getId();

		if(!htNodeSummary.contains(sNodeID)) {
			htNodeSummary.put(sNodeID, oNode);

			oNode.addPropertyChangeListener((PropertyChangeListener)this);

			htNodeSummaryCount.put(sNodeID, new Integer(1));
			return oNode;
		}
		else {
			int count = ((Integer)htNodeSummaryCount.get(sNodeID)).intValue();
			count++ ;
			htNodeSummaryCount.put(sNodeID, new Integer(count));
			return oNode;
		}
	}

	/**
	 * This method returns an View object with the given id, if the view is in the cache
	 *
	 * @param String sID, the id of the View to look for in the cache.
	 * @return View, the IView object with the given id if found, else null.
	 */
	public View getView(String sID) {

		if(htNodeSummary.containsKey(sID) && htNodeSummary.get(sID) instanceof View) {
			return (View)htNodeSummary.get(sID);
		}
		else {
			return null;
		}
	}

	/**
	 * Handle a PropertyChangeEvent.
	 * @param evt, the associated PropertyChangeEvent to handle.
	 */
	public void propertyChange(PropertyChangeEvent evt) {

	    String prop = evt.getPropertyName();
   		Object source = evt.getSource();
	    Object oldvalue = evt.getOldValue();
	    Object newvalue = evt.getNewValue();

		if (source instanceof NodeSummary) {

		    if (prop.equals(NodeSummary.NODE_TYPE_PROPERTY)) {

				NodeSummary fireNode = (NodeSummary)source;
				String sID = fireNode.getId();

				NodeSummary oldnode = (NodeSummary)getNode(sID);
				NodeSummary newnode = NodeSummary.getNodeSummary(sID);

				int nNewType = ((Integer)newvalue).intValue();
				int nOldType = ((Integer)oldvalue).intValue();

				// IF THE NODE SHOULD CHANGE CLASS AND HAS NOT YET, CHANGE IT.
				// ONLY WANT THE DATABASE READ TO HAPPEN ONCE.
				// (DEPENDS ON THREAD SPEED THOUGH)
				// AFTER THAT, THE NEW OBJECT CAN BE RETRIEVED FROM CACHE
				String oldClassName = oldnode.getClass().getName();
				String newClassName = newnode.getClass().getName();

			   	if ( (nOldType > ICoreConstants.PARENT_SHORTCUT_DISPLACEMENT && nNewType <=
	    					ICoreConstants.PARENT_SHORTCUT_DISPLACEMENT)

	    			|| ( View.isViewType(nOldType) 
		 					&& !View.isViewType(nNewType))

		 			|| ( !View.isViewType(nOldType)
		 					&& View.isViewType(nNewType) ) ) {

					// IF NOT BEEN RECREATED YET, DO IT.
					if (oldClassName.equals(newClassName)) {

						try {
							IModel model = oldnode.getModel();
							newnode = model.getNodeService().getNodeSummary(model.getSession(), oldnode.getId());
							newnode.initialize(model.getSession(), model);
						}
						catch(Exception ex) {
							ex.printStackTrace();
							System.out.println("Exception (NodeCache.propertyChange)\n\n"+ex.getMessage());
						}
					}
				}

				// If the new object is not the same as the old object e.g. switched from/to View.
				// Replace the object held in the cache under that node id.
				if (!oldnode.equals(newnode) && newnode != null) {
					replace((PCObject)oldnode, (PCObject)newnode);
				}
			}
		}
	}

	/**
	 * Clear the Cache.
	 */
	public void clear() {

		htNodeSummary.clear();
		htNodeSummaryCount.clear();
	}

	/**
	 * Null all variables to help with garbage collection.
	 */
	public void cleanUp() {

		htNodeSummary.clear();
		htNodeSummary = null;

		htNodeSummaryCount.clear();
		htNodeSummaryCount = null;
	}
}
