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

import java.awt.Point;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import com.compendium.LanguageProperties;
import com.compendium.ProjectCompendium;
import com.compendium.core.datamodel.IModel;
import com.compendium.core.datamodel.NodePosition;
import com.compendium.core.datamodel.PCSession;
import com.compendium.core.datamodel.View;
import com.compendium.core.datamodel.services.IViewService;

/**
 * UIAlign defines code to align the selected nodes of a map.
 * @author Lakshmi Prabhakaran
 *
 */
public class UIAlign {
	
	/** The value for the Top Align option.*/
	public static final String 	TOP 								= "top"; //$NON-NLS-1$
	
	/** The value for the Center Align option.*/
	public static final String 	CENTER 								= "center"; //$NON-NLS-1$
	
	/** The value for the Bottom Align option.*/
	public static final String 	BOTTOM 								= "bottom"; //$NON-NLS-1$
	
	/** The value for the Left Align option.*/
	public static final String 	LEFT 								= "left"; //$NON-NLS-1$
	
	/** The value for the Middle Align option.*/
	public static final String 	MIDDLE 								= "middle"; //$NON-NLS-1$
	
	/** The value for the Right Align option.*/
	public static final String 	RIGHT 								= "right"; //$NON-NLS-1$
	
	/** The view service for accessing the databse.*/
	private IViewService 		vs 									= null;

	/** The session object for the current user with the current database.*/
	private PCSession 			session 							= null;
	
	/** Used for undo operations.*/
	private Hashtable			nodePositionsCloneHashtable 		= new Hashtable();

	/** Used for redo operations.*/
	private Hashtable			nodePositionsCloneHashtableForRedo 	= new Hashtable();
	
	/** The selected align option.*/
	private String 				alignOption 						= null;
	
	/** The selected nodes to perform align.*/
	private Vector 				vtSelectedNodes 					= new Vector();
	
	/**
	 * Constructor.
	 * @param option
	 */
	
	public UIAlign(String option){
		this.alignOption = option;
	}

	/**
	 * performs aligning 
	 * @param viewFrame com.compenduim.ui.UIViewFrame, the frame of the map view to align.
 	 */
	public void alignNodes(UIViewFrame viewFrame){
		View view =  viewFrame.getView();
		UIViewPane uiViewPane = ((UIMapViewFrame)viewFrame).getViewPane();
		
		if(uiViewPane.getNumberOfSelectedNodes() > 1){
			for (Enumeration e = uiViewPane.getSelectedNodes(); e.hasMoreElements() ;) {
				UINode node = (UINode) e.nextElement();
				if(node != null)
					vtSelectedNodes.add(node);
			}//end for
			Vector 	vtTemp	 = processNodes(view);
			
			for(int i = vtTemp.size()-1; i >= 0; i--) {
				String nodeId = ((NodePosition)vtTemp.elementAt(i)).getNode().getId();
				UINode uinode = ((UINode) ((UIMapViewFrame)viewFrame).getViewPane().get(nodeId));
				if(uinode != null) {

					// ALLOW FOR FACT VIEW MIGHT BE SCALED
					int width = uinode.getWidth();

					if (uiViewPane != null) {
						Point p = UIUtilities.scalePoint(width, width, uiViewPane.getScale());
						width = p.x;
					}
					Point p = uinode.getNodePosition().getPos();
					if (uiViewPane != null) {
						Point loc = UIUtilities.transformPoint(p.x, p.y, uiViewPane.getScale());
						uinode.setBounds(loc.x, loc.y,
										 uinode.getWidth(),
										 uinode.getHeight());
					}

					uinode.updateLinks();

					try {
						view.setNodePosition(nodeId, p);
					}
					catch(Exception ex) {
						ex.printStackTrace();
						System.out.println("Error: (UIAlign.alignNodes) \n\n"+ex.getMessage()); //$NON-NLS-1$
					}
				}//end if
			}//end for
				
			if (uiViewPane != null)
				uiViewPane.repaint();

			vtTemp.clear();
			try {
				vtTemp = vs.getNodePositions(session, view.getId());
			} catch(Exception ex) {
				ProjectCompendium.APP.displayError(LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "UIAlign.cannotGetNodes") + view.getLabel()+". " + ex.getMessage()); //$NON-NLS-1$ //$NON-NLS-2$
			}//end catch

			nodePositionsCloneHashtableForRedo.clear();

			for ( int i = 0; i < vtTemp.size(); i++) {
				nodePositionsCloneHashtableForRedo.put(
						((NodePosition)vtTemp.elementAt(i)).getNode().getId(),
						((NodePosition)vtTemp.elementAt(i)).getClone());
			}//end for
		}//end if
	}//alignNodes
	
	/**
	 * process the nodes to align.
	 * @param view com.compendium.core.datamodel.View, the view to align.
	 */
	private Vector processNodes(View view){
		
		Vector vtTemp = new Vector();
		IModel model = ProjectCompendium.APP.getModel();
		session = model.getSession();
		vs = model.getViewService();
		try {
			vtTemp = vs.getNodePositions(session, view.getId());
		}
		catch(Exception ex) {
			ProjectCompendium.APP.displayError(LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "UIAlign.cannotGetNodes") + view.getLabel()+"." + ex.getMessage()); //$NON-NLS-1$ //$NON-NLS-2$
		}

		nodePositionsCloneHashtable.clear();

		for ( int i = 0; i < vtTemp.size(); i++) {
			nodePositionsCloneHashtable.put(
					((NodePosition)vtTemp.elementAt(i)).getNode().getId(),
					((NodePosition)vtTemp.elementAt(i)).getClone());
		}
		if(alignOption.equals(TOP)){
			alignTop();
		}else if (alignOption.equals(MIDDLE)){
			alignMiddle();
		}else if (alignOption.equals(BOTTOM)){
			alignBottom();
		}else if (alignOption.equals(LEFT)){
			alignLeft();
		} else if (alignOption.equals(CENTER)){
			alignCenter();
		}else if (alignOption.equals(RIGHT)){
			alignRight();
		}//end else
		return vtTemp;
	}//processNodes
	
	/**
	 * To perform right align
	 */
	
	private void alignRight() {
		double rightXPos = 0.0;
		for(int i = 0; i < vtSelectedNodes.size() ; i++){
			UINode uiNode = (UINode) vtSelectedNodes.get(i);
			double scale = uiNode.getScale();
			double xPos = uiNode.getNodePosition().getXPos();
			if (i == 0 || rightXPos < (xPos + (uiNode.getWidth()/ scale))){
				rightXPos = xPos + (uiNode.getWidth()/ scale);
			}//end if
		}//end for
		for(int i = 0; i < vtSelectedNodes.size() ; i++){
			UINode node = (UINode) vtSelectedNodes.get(i);
			double scale = node.getScale();
			Point p = node.getNodePosition().getPos();
			Point pt = new Point ();
			pt.setLocation(rightXPos - (node.getWidth()/ scale), (double)p.y);
			node.getNodePosition().setPos(pt);
			node.setLocation(pt);
		}//end for
	
	}//alignRight
	
	/**
	 * To perform center align
	 */
	private void alignCenter() {
		double sXPos = 0;
		double lXPos = 0;
		double center = 0;
		for(int i = 0; i < vtSelectedNodes.size() ; i++){
			UINode uiNode = (UINode) vtSelectedNodes.get(i);
			double scale = uiNode.getScale();
			Point p = uiNode.getNodePosition().getPos();
			double xPos = uiNode.getNodePosition().getXPos();
			double width = (uiNode.getWidth()/ scale);
			if(i == 0 || sXPos > xPos){
				sXPos = xPos;
			}//end if
			if(i == 0 || lXPos < (xPos +width) ) {
				lXPos = xPos + width;
			}
		}//end for
		
		center = (sXPos + lXPos) /2 ;
		//To check if it is already centered
		boolean isCenter = true;
		
		for(int i = 0; i < vtSelectedNodes.size() ; i++){
			UINode node = (UINode) vtSelectedNodes.get(i);
			double scale = node.getScale();
			Point p = node.getNodePosition().getPos();
			double width = (node.getWidth()/ scale);
			if((node.getLocation().getX() + ( width / 2)) != center) {
				isCenter = false;
			}//end if
		}//end for
		
		if(!isCenter){
			// To put the nodes at the center
			for(int i = 0; i < vtSelectedNodes.size() ; i++){
				UINode node = (UINode) vtSelectedNodes.get(i);
				double scale = node.getScale();
				double width = (node.getWidth()/ scale) ;
				Point p = node.getNodePosition().getPos();
				Point pt = new Point();
				pt.setLocation(center - ( width /2) ,p.y);
				node.getNodePosition().setPos(pt);
				node.setLocation(pt);
			}//end for
		}//end if
		
	}//alignCenter
	
	/**
	 * To perform left align
	 */
	private void alignLeft() {
		double leftXPos = 0;
		for(int i = 0; i < vtSelectedNodes.size() ; i++){
			UINode uiNode = (UINode) vtSelectedNodes.get(i);
			double scale = uiNode.getScale();
			double xPos = uiNode.getNodePosition().getXPos();
			if (i == 0 || leftXPos > xPos){
				leftXPos = xPos;
			}//end if
		}//end for
		for(int i = 0; i < vtSelectedNodes.size() ; i++){
			UINode node = (UINode) vtSelectedNodes.get(i);
			double scale = node.getScale();
			Point p = node.getNodePosition().getPos();
			Point pt = new Point();
			pt.setLocation(leftXPos ,p.y);
			node.getNodePosition().setPos(pt);
			node.setLocation(pt);
		}//end for
		
	}//alignLeft
	
	/**
	 * To perform bottom align
	 */
	private void alignBottom() {
		double bottomYPos = -1;
		for(int i = 0; i < vtSelectedNodes.size() ; i++){
			UINode uiNode = (UINode) vtSelectedNodes.get(i);
			double scale = uiNode.getScale();
			double yPos = uiNode.getNodePosition().getYPos();
			double height = (int)(uiNode.getHeight()/scale) ;
			if (i == 0 || bottomYPos < (yPos + height)){
				bottomYPos = yPos + height;
			}//end if
		}//end for
		for(int i = 0; i < vtSelectedNodes.size() ; i++){
			UINode node = (UINode) vtSelectedNodes.get(i);
			double scale = node.getScale();
			Point p = node.getNodePosition().getPos();
			double height = (node.getHeight()/scale) ;
			Point pt = new Point();
			pt.setLocation(p.x , bottomYPos - height);
			node.getNodePosition().setPos(pt);
			node.setLocation(pt);
		}//end for
		
	}//alignBottom

	/**
	 * To perform middle align
	 */
	
	private void alignMiddle() {
		
		double sYPos = 0;
		double lYPos = 0;
		double middle = 0;
		for(int i = 0; i < vtSelectedNodes.size() ; i++){
			UINode uiNode = (UINode) vtSelectedNodes.get(i);
			double scale = uiNode.getScale();
			double yPos = uiNode.getNodePosition().getYPos();
			double height = (uiNode.getHeight()/scale);
			if(i == 0 || sYPos > yPos){
				sYPos = yPos;
			}//end if
			if(i == 0 || lYPos < (yPos + height) ) {
				lYPos = yPos + height;
			}
		}//end for
		middle = (lYPos + sYPos)/2 ;
		
		//To check if it is already in the middle
		boolean isMiddle = true;
		for(int i = 0; i < vtSelectedNodes.size() ; i++){
			UINode node = (UINode) vtSelectedNodes.get(i);
			double scale = node.getScale();
			double height = (node.getHeight()/scale) ;
			Point p = node.getNodePosition().getPos();
			if((p.x + (height / 2))!= middle) {
				isMiddle = false;
			}//end if
		}//end for
		
		if(!isMiddle){
			// To put the nodes at the center
			for(int i = 0; i < vtSelectedNodes.size() ; i++){
				UINode node = (UINode) vtSelectedNodes.get(i);
				double scale = node.getScale();
				double height = node.getHeight()/scale ;
				Point p = node.getNodePosition().getPos();
				Point pt = new Point();
				pt.setLocation(p.x,(middle - (height/2)));
				node.getNodePosition().setPos(pt);
				node.setLocation(pt);
			}//end for
		}//end if
	}//alignMiddle
	
	/**
	 * To perform Top align
	 */
	private void alignTop(){
		double topYPos = -1;
		for(int i = 0; i < vtSelectedNodes.size() ; i++){
			UINode uiNode = (UINode) vtSelectedNodes.get(i);
			double scale = uiNode.getScale();
			double yPos = uiNode.getNodePosition().getYPos();
			if (i == 0 || topYPos > yPos){
				topYPos = yPos;
			}//end if
		}//end for
		for(int i = 0; i < vtSelectedNodes.size() ; i++){
			UINode node = (UINode) vtSelectedNodes.get(i);
			double scale = node.getScale();
			Point p = node.getNodePosition().getPos();
			Point pt = new Point();
			pt.setLocation(p.x , topYPos);
			node.getNodePosition().setPos(pt);
			node.setLocation(pt);
		}//end for
	}//align Top
	
	
//	******** UNDO / REDO **************//

	/**
	 * Undo the last align for the given frame
	 * @param viewFrame com.compendium.ui.UIViewFrame, the frame to undo the align for.
	 */
	public void undoAlign(UIViewFrame viewFrame) {

		UIViewPane pane = ((UIMapViewFrame)viewFrame).getViewPane();
		for(Enumeration e = nodePositionsCloneHashtable.keys();
				e.hasMoreElements();)	{
			String nodeID = (String)e.nextElement();
			UINode uinode = (UINode)pane.get(nodeID);
			NodePosition np = (NodePosition)nodePositionsCloneHashtable.get(nodeID);

			Point loc = UIUtilities.transformPoint(np.getXPos(), np.getYPos(), pane.getScale());

			uinode.setBounds(loc.x, loc.y,
							 uinode.getWidth(),
							 uinode.getHeight());
			uinode.updateLinks();

			try {
				viewFrame.getView().setNodePosition(nodeID, np.getPos());
			}
			catch(Exception ex) {
				System.out.println("Error: (UIAlign.undoAlign) \n\n"+ex.getMessage()); //$NON-NLS-1$
			}
		}
		pane.repaint();
	}//undoAlign

	/**
	 * Redo the last align for the given frame
	 * @param viewFrame com.compendium.ui.UIViewFrame, the frame to undo the align for.
	 */
	public void redoAlign(UIViewFrame viewFrame) {

		UIViewPane pane = ((UIMapViewFrame)viewFrame).getViewPane();

		for(Enumeration e = nodePositionsCloneHashtableForRedo.keys();
				e.hasMoreElements();)	{
			String nodeID = (String)e.nextElement();
			UINode uinode = (UINode)pane.get(nodeID);
			NodePosition np = (NodePosition)nodePositionsCloneHashtableForRedo.get(nodeID);

			Point loc = UIUtilities.transformPoint(np.getXPos(), np.getYPos(), pane.getScale());

			uinode.setBounds(loc.x, loc.y,
							 uinode.getWidth(),
							 uinode.getHeight());
			uinode.updateLinks();

			try {
				viewFrame.getView().setNodePosition(nodeID, np.getPos());
			}
			catch(Exception ex) {
				System.out.println("Error: (UIAlign.redoAlign) \n\n"+ex.getMessage()); //$NON-NLS-1$
			}
		}
		pane.repaint();
	}//redoAlign

}//UIAlign
