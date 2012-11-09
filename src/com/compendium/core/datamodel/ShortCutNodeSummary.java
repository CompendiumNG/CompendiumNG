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

/**
 * The ShortCutNodeSummary object represents a short cut to a Node
 * in the system. The short cut can be created or deleted, it's reference
 * cannot be reset. So this class does not broadcast or listen to any property changes
 *
 * @author	rema and sajid
 */
public class ShortCutNodeSummary extends NodeSummary
								implements IShortCutNodeSummary, java.io.Serializable {


	/** The parent node this shortcut refers to.*/
	protected NodeSummary oReferredNode = null ;

	/**
	 *	This is the constructor used at the DB layer to create a new short cut node.
	 *
	 * 	@param String sNodeID, the unique identifer for this node.
	 * 	@param int nType, the type of this node.
	 * 	@param String nXNodeType, the extended node type of this node.
	 * 	@param String sOriginalID, the original unique identifer for this node.
	 *	@param String sAuthor, the author of this node.
	 *	@param Date dCreationDate, the creation date for this node.
	 *	@param Date dModificationDate, the date this node was last modified.
	 *	@param String sLabel, the label on this node.
	 *	@param String sDetail, the first page of detail for this node.
	 *	@param NodeSummary oNode, the node referenced by this node.
	 *	@param sLastModAuthor the author who last modified this node.
	 */
	public ShortCutNodeSummary(String sNodeID, int nType, String nXNodeType, String sOriginalID,
			int nState, String sAuthor, Date dCreationDate, Date dModificationDate, String sLabel, String sDetail, NodeSummary oNode, String sLastModAuthor) {

		super(sNodeID, nType , nXNodeType, sOriginalID, nState, sAuthor, dCreationDate, dModificationDate, sLabel, sDetail, sLastModAuthor);
		oReferredNode = oNode;
	}	

	/**
	 * Return a node summary object with the given id and details.
	 * If a shortcut node with the given id has already been created in this session, update its data and return that,
	 * else create a new one, and add it to the list.
	 *
	 *	@param String sNodeID, the id of the node.
	 *	@param int nType, the type of this node.
	 *	@param String sXNodeType, the extended node type id of the node - NOT CURRENTLY USED.
	 *	@param String sOriginalID, the original id of the node if it was imported.
	 *	@param String sAuthor, the author of the node.
	 *	@param Date dCreationDate, the creation date of this node.
	 *	@param Date dModificationDate, the date the node was last modified.
	 *	@param String sLabel, the label of this node.
	 *	@param String sDetail, the first page of detail for this node.
	 *	@param NodeSummary oNode, the node referenced by this node.
	 *	@param sLastModAuthor the author who last modified this node.  
 	 *  @return View, a view node object with the given id.
	 */
	public static ShortCutNodeSummary getShortCutNodeSummary(String sNodeID, int nType, String sXNodeType, String sOriginalID,
				int nState, String sAuthor, Date dCreationDate, Date dModificationDate, 
				String sLabel, String sDetail, NodeSummary oNode, String sLastModAuthor)
	{
		int i = 0;
		Vector nodeSummaryList = NodeSummary.getNodeSummaryList();
		for (i = 0; i < nodeSummaryList.size(); i++) {
			if (sNodeID.equals(((NodeSummary)nodeSummaryList.elementAt(i)).getId())) {
				break;
			}
		}

		ShortCutNodeSummary ns = null;
		if (i == nodeSummaryList.size()) {
			ns = new ShortCutNodeSummary(sNodeID, nType, sXNodeType, sOriginalID,
								 nState, sAuthor, dCreationDate, dModificationDate, sLabel, sDetail, oNode, sLastModAuthor);
			nodeSummaryList.addElement(ns);
		}
		else {
			Object obj = nodeSummaryList.elementAt(i);
			if (obj instanceof ShortCutNodeSummary) {
				ns = (ShortCutNodeSummary)obj;

				// UPDATE THE DETAILS
				if (!ns.bLabelDirty) {				
					ns.setLabelLocal(sLabel);
				}
				ns.setDetailLocal(sDetail);
				ns.setTypeLocal(nType);
				ns.setStateLocal(nState);
				ns.setAuthorLocal(sAuthor);
				ns.setCreationDateLocal(dCreationDate);
				ns.setModificationDateLocal(dModificationDate);
				ns.setOriginalIdLocal(sOriginalID);
				ns.setExtendedNodeTypeLocal(sXNodeType);
			}
			else {
				nodeSummaryList.removeElement(obj);
				ns = new ShortCutNodeSummary(sNodeID, nType, sXNodeType, sOriginalID,
								 nState, sAuthor, dCreationDate, dModificationDate, sLabel, sDetail, oNode, sLastModAuthor);
				nodeSummaryList.addElement(ns);
			}
		}
		return ns;
	}	
	
	/**
	 * Returns whether or not a specific node is a shortcut node.
	 *
	 * @return boolean, true if the given type is a shortcutnode type, else false.
	 */
	public static boolean isShortCutNodeType(int type) {
		if (type == LIST_SHORTCUT || type == MAP_SHORTCUT
			|| type == ISSUE_SHORTCUT|| type == POSITION_SHORTCUT
			|| type == ARGUMENT_SHORTCUT|| type == PRO_SHORTCUT
			|| type == CON_SHORTCUT|| type == DECISION_SHORTCUT
			|| type == REFERENCE_SHORTCUT|| type == NOTE_SHORTCUT
			|| type == MOVIEMAP_SHORTCUT)
			return true;
		else
			return false;
	}

	/**
	 *	Returns the node summary to which this short cut node points to
	 *
	 *	@return the INodeSummary of the referred node
	 */
	public NodeSummary getReferredNode() {
		return oReferredNode ;
	}

	/**
	 *	Sets the node summary of the node to which this short cut node points to, in the local data ONLY.
	 *
	 *	@param NodeSummary nodeSummary, the node summary to which this short cut points to.
	 */
	public void setReferredNode(NodeSummary nodeSummary) {
		oReferredNode = nodeSummary ;
	}
}
