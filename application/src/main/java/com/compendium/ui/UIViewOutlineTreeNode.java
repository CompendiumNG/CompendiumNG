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

import com.compendium.ProjectCompendium;
import com.compendium.core.ICoreConstants;
import com.compendium.core.datamodel.NodeSummary;
import com.compendium.core.datamodel.View;

/**
 * This class is used to create objects for tree nodes in outline view.
 * @author Lakshmi Prabhakaran
 *
 */

public class UIViewOutlineTreeNode {
	
	/** NodeSummary of the node associated with this object */
	private NodeSummary  userObject 			= null;
	
	/** The label of this node.*/
	private String label 						= "";
	
	/** The reference if, any for this node.*/
	private String sReference					= ""; //$NON-NLS-1$
	
	/** The type of this node.*/
	private int type 							= -1;
	
	/** The type of this node.*/
	private int state							= 0;
	
	/**
	 * constructor. For creating the root node for views and nodes option
	 *  
	 */	
	public UIViewOutlineTreeNode(String s, int type){
		this.label = s;
		this.type = type;
		this.state = ICoreConstants.READSTATE;
	}
	
	/**
	 * Constructor. Creates the UIViewOutlineTreeNode object for the given NodeSummary
	 * @param NodeSummary obj, a node summary object
	 * 
	 */
	public UIViewOutlineTreeNode(NodeSummary obj){
		if (obj != null) {
			this.userObject = obj;
			this.label  = obj.getLabel();
			this.type = obj.getType();
			this.state = obj.getState();
			this.sReference = obj.getSource();
			userObject.initialize(ProjectCompendium.APP.getModel().getSession(), ProjectCompendium.APP.getModel());
		}
	}
	
	/**
	 * Gets the ID of the Node.
	 * @return String, the ID of the node 
	 */
	public String getId(){
		String s  = "";
		if (userObject != null) {
			s = userObject.getId();
		}
		return s;
	}

	/**
	 * Gets the NodeSummary of this object
	 * @return NodeSummary, the nodesummary of this object
	 */
	public NodeSummary getObject(){
		return userObject;
	}
	
	/**
	 * Gets the external reference string for this item if node if reference node.
	 * @return
	 */
	public String getReference() {
		return this.sReference;
	}
	
	/**
	 * Sets the external reference string for this item if node if reference node.
	 * @param sRef the reference to set.
	 */
	public void setReference(String sRef) {
		this.sReference = sRef;
	}
	
	/**
	 * Gets the label of this node
	 * @return String, the label of the node
	 */
	public String getLabel(){
		return label;
	}
	
	/**
	 * Sets the label of this node locally
	 */
	public void setLabel(String label) {
		this.label = label;
	}
	
	
	/**
	 * Gets the type of this node
	 * @return String, the type of the node
	 */
	public int getType(){
		return type;
	}
	
	/**
	 * Sets the type of this node
	 */
	public void setType(int type){
		this.type = type;
	}
	

	/**
	 * Sets the NodeSummary of this object
	 * @param NodeSummary, the nodesummary of this object
	 */
	public void setObject(NodeSummary ns){
		userObject = ns;
	}
	/**
	 * String representation of this Object.
	 */
	public String toString(){
		View homeView = ProjectCompendium.APP.getModel().getUserProfile().getHomeView();
		if(userObject != null && userObject.equals(homeView) && label.equals("Home Window")){ //$NON-NLS-1$
			return ProjectCompendium.APP.getModel().getUserProfile().getUserName() + "\'s " + label ; //$NON-NLS-1$
		}
		return label;
	}

	/**
	 * @return Returns the state.
	 */
	public int getState() {
		return state;
	}

	/**
	 * @param state The state to set.
	 */
	public void setState(int state) {
		this.state = state;
	}
	
	
}
