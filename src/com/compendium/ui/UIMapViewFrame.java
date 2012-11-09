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

import java.beans.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.dnd.*;
import java.awt.geom.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;
import javax.swing.plaf.*;
import javax.help.*;

import com.compendium.LanguageProperties;
import com.compendium.ProjectCompendium;
import com.compendium.ui.plaf.*;
import com.compendium.core.*;
import com.compendium.core.datamodel.*;
import com.compendium.ui.dialogs.UIAerialDialog;


/**
 * Has additional methods specifically for Map Frames.
 *
 * @author	Mohammed Sajid Ali / Michelle Bachler
 */
public class UIMapViewFrame extends UIViewFrame {

	/** The UIViewPane associated with this map frame.*/
	protected UIViewPane		oViewPane		= null;

	/** The Aerial view dialog associated with this map frame.*/
	protected UIAerialDialog	oAerialDialog 	= null;

	/** The aerial view pane associated with this map frame.*/
	protected UIAerialViewPane	oAerialViewPane = null;

	/** Does a node in the view have the focus when zooming?*/
	protected boolean			isFocusedNode 	= false;

	/** The base of the current title for this frame.*/
	protected String 			sBaseTitle 		= new String("[Map]: "); //$NON-NLS-1$

	/** The title for this view.*/
	protected String 			sTitle			= ""; //$NON-NLS-1$

	/**
	 * Constructor. Create a new instance of this class.
	 * @param view com.compendium.core.datamodel.View, the view associated with this frame.
	 */
	public UIMapViewFrame (View view) {
		this(view, UIViewFrame.getViewLabel(view));
	}

	/**
	 * Constructor. Create a new instance of this class.
	 * @param view com.compendium.core.datamodel.View, the view associated with this frame.
	 * @param title, the title for this frame.
	 */
	public UIMapViewFrame (View view, String title) {
		super(view, title);
		init(view);
	}

	/**
	 * Override to add a default title stub to start of title.
	 *
	 * @param sTitle, the title to add.
	 */
	public void setTitle(String sTitle) {
		this.sTitle = sTitle;
		if (!sTitle.startsWith(sBaseTitle)) {
			super.setTitle(sBaseTitle+sTitle);
		}
		else {
			super.setTitle(sTitle);
		}
	}

	/**
	 * Initialize and draw this frame.
	 * @param view com.compendium.core.datamodel.View, the view associated with this frame.
	 */
	protected void init(View view) {

		oContentPane.setLayout(new BorderLayout());
		this.oView = view;

		oViewPane = new UIViewPane(view, this);

		updateFrameIcon();

		// A Workaround since the scrollbar never sizes on the JLayeredPane for some reason
		// therefore created a panel and added the viewpane to it and finally added the panel
		// to the scrollpane
		// the setPreferredSize is for the scrollpane to resize .  a high number
		// By overriding getPreferredSize in the JPanel, as the JScrollpane calls to find out how big
		// the JPanel is .

		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.setPreferredSize(new Dimension(30000,30000));
		panel.add(oViewPane, BorderLayout.CENTER);

		scrollpane = new JScrollPane(panel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
												 JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		(scrollpane.getVerticalScrollBar()).setUnitIncrement(50);
		(scrollpane.getHorizontalScrollBar()).setUnitIncrement(50);

		oViewport = scrollpane.getViewport();

		CSH.setHelpIDString(this,"node.views"); //$NON-NLS-1$

		horizontalBar = scrollpane.getHorizontalScrollBar();
		verticalBar = scrollpane.getVerticalScrollBar();

		oContentPane.add(scrollpane, BorderLayout.CENTER);

		setTitle(title);

		this.setVisible(true);
	}


////////////////// METHODS FOR AERIAL VIEW //////////////////////////

	/**
	 * Create the aerial view dialog with the given bounds.
	 * @param dialogBounds, the bounds for the aerial view dialog.
	 */
	private UIAerialDialog createAerialDialog(Rectangle dialogBounds) {

		oAerialDialog = new UIAerialDialog(ProjectCompendium.APP, this, oView, dialogBounds);
		oAerialViewPane = oAerialDialog.getViewPane();

		if (dialogBounds != null) {
			oAerialDialog.setLocation(dialogBounds.x, dialogBounds.y);
		}
		else {
			Point frameloc = ProjectCompendium.APP.getLocation();
			//Point p = SwingUtilities.convertPoint(ProjectCompendium.APP.getDesktop(),frameloc.x, frameloc.y, ProjectCompendium.APP);
			//SwingUtilities.convertPointToScreen(frameloc,  ProjectCompendium.APP);
			oAerialDialog.setLocation( frameloc.x+3, frameloc.y+( ProjectCompendium.APP.getHeight()-oAerialDialog.getHeight()-25 ) );
		}
		return oAerialDialog;
	}

	/**
	 * Show the aerial view dialog. Create it is not already created.
	 * @param dialogBounds, the bounds for the aerial view dialog.
	 */
	public UIAerialDialog showArialView(Rectangle dialogBounds) {
		if (oAerialDialog == null)
			createAerialDialog(dialogBounds);

		oAerialDialog.setVisible(true);
		return oAerialDialog;
	}

	/**
	 * Destroy the aerial view dialog and clean up associated references.
	 */
	public void destroyAerialView() {

		if (oAerialDialog != null) {
			oAerialViewPane.cleanUp();
			oAerialViewPane = null;
			oAerialDialog = null;
		}
	}

	/**
	 * Cancel and then destroy the aerial view dialog.
	 * @see #destroyAerialView
	 */
	public void cancelAerialView() {

		ProjectCompendium.APP.cancelAerialView();
		destroyAerialView();
	}

	/**
	 * Update the font used in the aerial view dialog.
	 * @param labelFont, the font to use in the aerial view dialog.
	 */
	public void updateAerialLabelFonts(Font labelFont) {
		if (oAerialViewPane == null)
			return;

		AffineTransform trans=new AffineTransform();
		double currentScale = oAerialViewPane.getZoom();
		trans.setToScale(currentScale, currentScale);

		//Font font2 = font.deriveFont(trans);
		// work around for Mac BUG and deriveFont
		Point p1 = new Point(labelFont.getSize(), labelFont.getSize());
		try {
		    p1 = (Point)trans.transform(p1, new Point(0, 0));
		}
		catch(Exception e) {
		    System.out.println("can't convert font size in MapFrame \n\n"+e.getMessage()); //$NON-NLS-1$
		}
		Font font2 = new Font(labelFont.getName() , labelFont.getStyle(), p1.x);

		Component array[] = oAerialViewPane.getComponentsInLayer((UIViewPane.NODE_LAYER).intValue());

		UINode uinode = null;
		for (int j = 0; j < array.length; j++) {
			uinode = (UINode)array[j];
			uinode.setLabelFont( font2 );
		}

		oAerialViewPane.repaint();
		oAerialViewPane.validate();
	}

	/**
	 * Update the icons in the aerial view.
	 * @param refreshFrameIcons, whether to also update the Frame icon.
	 */
	public void refreshAerialIcons(boolean refreshFrameIcons) {
		if (oAerialViewPane == null)
			return;

		Component array[] = oAerialViewPane.getComponentsInLayer((UIViewPane.NODE_LAYER).intValue());

		UINode uinode = null;
		for (int j = 0; j < array.length; j++) {
			uinode = (UINode)array[j];
			int nType = uinode.getNode().getType();
			ImageIcon icon = null;
			NodeSummary node = (NodeSummary)uinode.getNode();

			if (nType == ICoreConstants.REFERENCE || nType == ICoreConstants.REFERENCE_SHORTCUT) {
				String image  = node.getImage();
				if ( image != null && !image.equals("")) //$NON-NLS-1$
					uinode.setReferenceIcon( image );
				else {
					uinode.setReferenceIcon( node.getSource() );
				}
			}
			else if (View.isViewType(nType) || View.isShortcutViewType(nType)) {
				String image  = node.getImage();
				if ( image != null && !image.equals("")) //$NON-NLS-1$
					uinode.setReferenceIcon( image );
				else {
					icon = UINode.getNodeImage(node.getType(), uinode.getNodePosition().getShowSmallIcon());
					uinode.refreshIcon( icon );
				}
			}
			else {
				icon = UINode.getNodeImage(node.getType(), uinode.getNodePosition().getShowSmallIcon());
				uinode.refreshIcon( icon );
			}
			uinode.updateLinks();
		}
		oAerialViewPane.repaint();
		oAerialViewPane.validate();
	}

	/**
	 * Update the icon indicator of a specific nodeID.
	 * @param sNodeID, the id of the node whose indicators to refresh.
	 */
	public void refreshAerialNodeIconIndicators(String sNodeID) {
		if (oAerialViewPane == null)
			return;

		oAerialViewPane.refreshNodeIconIndicators(sNodeID);
		oAerialViewPane.repaint();
		oAerialViewPane.validate();
	}

	/**
	 * Update the icon indicator in the aerial view.
	 */
	public void refreshAerialIconIndicators() {
		if (oAerialViewPane == null)
			return;

		oAerialViewPane.refreshIconIndicators();
		oAerialViewPane.repaint();
		oAerialViewPane.validate();
	}

	/**
	 * Update the given node in the aerial view.
	 * @param node com.compendium.core.datamodel.NodeSummary, the node to update.
	 */
	public void refreshNode(NodeSummary node) {
		if (oAerialDialog != null)
			oAerialDialog.refreshNode(node);
	}

	/**
	 * Rescales the aerial view.
	 *
	 * @param oPoint, The Point object to needed to test if rescale required.
	 */
	public void rescaleAerial(Point oPoint) {
		if (oAerialDialog != null)
			oAerialDialog.rescale(oPoint);
	}

	/**
	 * Adds a node to the aerial view.
	 *
	 * @param oNodePos com.compendium.core.datamodel.NodePosition, The NodePosition object to add.
	 */
	public void addAerialNode(NodePosition oNodePos) {

		if (oNodePos == null || oAerialDialog == null)
			return;

		UINode oNode = (UINode)oAerialViewPane.get(oNodePos.getNode().getId());
		if (oNode == null) {
			oAerialViewPane.getUI().addNode(oNodePos);
		}
	}

	/**
	 * Remove a node from the aerial view.
	 *
	 * @param oNode com.compendium.core.datamodel.NodeSummary, The NodeSummary object to remove.
	 */
	public void removeAerialNode(NodeSummary oNode) {

		if (oNode == null || oAerialDialog == null)
			return;

		UINode uinode = (UINode)oAerialViewPane.get(oNode.getId());
		if (uinode != null) {
			oAerialViewPane.remove(uinode);
			oAerialViewPane.paintImmediately(uinode.getBounds());
		}
	}

	/**
	 * Sets the selection status for the given node on the aerial view.
	 *
	 * @param node com.compendium.ui.UINode, The ui node in the view.
	 * @param mode, the selection mode.
	 */
	public void setAerialSelectedNode(UINode node, int mode) {

		if (oAerialDialog == null)
			return;

		if (node == null) {
			oAerialViewPane.processSelectedNode(node, mode);
		}
		else {
			UINode oNode = (UINode)oAerialViewPane.get(node.getNode().getId());
			if (oNode != null) {
				oNode.setSelected(true);
				oAerialViewPane.processSelectedNode(oNode, mode);
			}
		}
	}

	/**
	 * Sets the rollover status of the given node in the aerial view.
	 *
	 * @param node com.compendium.ui.UINode, the node to set the rollover status for.
	 * @param rollover,  the rollover status of the node.
	 */
	public void setAerialRolloverNode(UINode node, boolean rollover) {

		if (node == null || oAerialDialog == null)
			return;

		UINode oNode = (UINode)oAerialViewPane.get(node.getNode().getId());
		if (oNode != null) {
			oNode.setRollover(rollover);
		}
	}

	/**
	 * Sets the node type of the given node in the aerial view
	 *
	 * @param node com.compendium.ui.UINode, the node to set the type for
	 */
	/*public void setAerialNodeType(UINode node, int type) {

		if (node == null || oAerialDialog == null)
			return;

		UINode oNode = (UINode)oAerialViewPane.get(node.getNode().getId());
		if (oNode != null) {
			//oNode.setLocalType(type);
			//oNode.refreshIcon(oNode.getIcon());
		}
	}*/

	/**
	 * Adds a link to the aerial view.
	 *
	 * @param oLinkProps  The Link object to add.
	 */
	public void addAerialLink(LinkProperties oLinkProps) {
		if (oLinkProps == null || oAerialDialog == null)
			return;

		Link oLink = oLinkProps.getLink();
		UILink oUILink = (UILink)oAerialViewPane.get(oLink.getId());
		if (oUILink == null) {
			UINode oFromNode = (UINode)oAerialViewPane.get(oLink.getFrom().getId());
			UINode oToNode = (UINode)oAerialViewPane.get(oLink.getTo().getId());
			if (oFromNode != null && oToNode != null) {
				if (!oFromNode.containsLink(oToNode)) {
					oAerialViewPane.getUI().addLink(oLinkProps);
				}
			}
		}
	}

	/**
	 * Remove a link from the aerial view.
	 *
	 * @param oLink  The Link object to remove.
	 */
	public void removeAerialLink(Link oLink) {
		if (oLink == null || oAerialDialog == null)
			return;

		UILink oUILink = (UILink)oAerialViewPane.get(oLink.getId());
		if (oUILink != null) {
			UINode fromNode = oUILink.getFromNode();
			UINode toNode = oUILink.getToNode();
			if (fromNode != null && toNode != null) {
				fromNode.removeLink(oUILink);
				toNode.removeLink(oUILink);
				oAerialViewPane.remove(oUILink);
			}
		}
	}

	/**
	 * Sets the selection mode for the given link in the aerial view.
	 *
	 * @param link com.compendium.ui.UILink, The ui link in the view.
	 * @param mode, the selection mode.
	 */
	public void setAerialSelectedLink(UILink link, int mode) {

		if (oAerialDialog == null)
			return;

		if (link == null) {
			oAerialViewPane.processSelectedLink(link, mode);
		}
		else {
			UILink oLink = (UILink)oAerialViewPane.get(link.getLink().getId());
			if (oLink != null) {
				oLink.setSelected(true);
				oAerialViewPane.processSelectedLink(oLink, mode);
			}
		}
	}

	/**
	 * Adds a node to the main view.
	 *
	 * @param oNodePos com.compendium.core.datamodel.NodePosition, the NodePosition object to add.
	 */
	public void addParentNode(NodePosition oNodePos) {

		if (oNodePos == null)
			return;

		UINode oNode = (UINode)oViewPane.get(oNodePos.getNode().getId());
		if (oNode == null) {
			oViewPane.getUI().addNode(oNodePos);
		}
	}

	/**
	 * Remove a node from the main view.
	 *
	 * @param oNode com.compendium.core.datamodel.NodeSummary, the NodeSummary object to remove.
	 */
	public void removeParentNode(NodeSummary oNode) {

		if (oNode == null)
			return;

		UINode uinode = (UINode)oViewPane.get(oNode.getId());
		if (uinode != null) {
			oViewPane.remove(uinode);
			oViewPane.paintImmediately(uinode.getBounds());
			//getView().getModel().removeObject(oNode);
			if(oNode instanceof View) {
				ProjectCompendium.APP.removeView((View)oNode);
			}
		}
	}

	/**
	 * Sets the selection mode for the given node in the main view
	 *
	 * @param node com.compendium.uiUINode, the node to set the selection mode for.
	 * @param mode, the selection mode.
	 */
	public void setParentSelectedNode(UINode node, int mode) {

		if (node == null) {
			oViewPane.processSelectedNode(node, mode);
		}
		else {
			UINode oNode = (UINode)oViewPane.get(node.getNode().getId());
			if (oNode != null) {

				JViewport viewport = getViewport();
				Point nodePos = oNode.getLocation();
				Point parentPos = SwingUtilities.convertPoint((Component)oViewPane, nodePos.x, nodePos.y, viewport);

				Rectangle parentBounds = new Rectangle(parentPos);
				viewport.scrollRectToVisible( new Rectangle( parentPos.x-5, parentPos.y-5, oNode.getWidth()+10, oNode.getHeight()+10 ) );

				oNode.setSelected(true);
				oViewPane.processSelectedNode(oNode, mode);
			}
		}
	}

	/**
	 * Sets the rollover status for the given node in the main view.
	 *
	 * @param node com.compendium.ui.UINode, The node to set the rollover status for.
	 * @param rollover, the rollover status to set.
	 */
	public void setParentRolloverNode(UINode node, boolean rollover) {

		if (node == null)
			return;

		UINode oNode = (UINode)oViewPane.get(node.getNode().getId());
		if (oNode != null) {
			oNode.setRollover(rollover);
		}
	}

	/**
	 * Sets the uinode type on the main view
	 *
	 * @param node The ui node in the view
	 */
	/*public void setParentNodeType(UINode node, int type) {

		if (node == null)
			return;

		UINode oNode = (UINode)oViewPane.get(node.getNode().getId());
		if (oNode != null) {
			//oNode.setLocalType(type);
			//oNode.refreshIcon(oNode.getIcon());
		}
	}*/

	/**
	 * Adds a link to the main view
	 *
	 * @param oLinkProps The Link object to add
	 */
	public void addParentLink(LinkProperties oLinkProps) {

		if (oLinkProps == null)
			return;

		Link oLink = oLinkProps.getLink();
		UILink oUILink = (UILink)oViewPane.get(oLink.getId());
		if (oUILink == null) {
			UINode oFromNode = (UINode)oViewPane.get(oLink.getFrom().getId());
			UINode oToNode = (UINode)oViewPane.get(oLink.getTo().getId());
			if (oFromNode != null && oToNode != null) {
				if (!oFromNode.containsLink(oToNode)) {
					oViewPane.getUI().addLink(oLinkProps);
				}
			}
		}
	}

	/**
	 * Remove a link from the main view
	 *
	 * @param oLink The Link object to remove
	 */
	public void removeParentLink(Link oLink) {

		if (oLink == null)
			return;

		UILink oUILink = (UILink)oViewPane.get(oLink.getId());
		if (oUILink != null) {
			UINode fromNode = oUILink.getFromNode();
			UINode toNode = oUILink.getToNode();
			if (fromNode != null && toNode != null) {
				fromNode.removeLink(oUILink);
				toNode.removeLink(oUILink);
				oViewPane.remove(oUILink);
			}
		}
	}

	/**
	 * Sets the uilink selected on the main view
	 *
	 * @param link The ui link in the view
	 */
	public void setParentSelectedLink(UILink link, int mode) {

		if (link == null) {
			oViewPane.processSelectedLink(link, mode);
		}
		else {
			UILink oLink = (UILink)oViewPane.get(link.getLink().getId());
			if (oLink != null) {

				JViewport viewport = getViewport();
				Point linkPos = oLink.getLocation();
				Point parentPos = SwingUtilities.convertPoint((Component)oViewPane, linkPos.x, linkPos.y, viewport);

				Rectangle parentBounds = new Rectangle(parentPos);
				viewport.scrollRectToVisible( new Rectangle( parentPos.x-5, parentPos.y-5, oLink.getWidth()+10, oLink.getHeight()+10 ) );

				oLink.setSelected(true);
				oViewPane.processSelectedLink(oLink, mode);
			}
		}
	}

//////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Delete the children in the given view.
	 * @param childView com.compendium.core.datamodel.View, the view to delet the children for.
	 */
	public void deleteChildren(View childView) {

		UIViewPane childUIViewPane = getViewPane();
		ViewPaneUI childViewPaneUI = childUIViewPane.getUI();
		childViewPaneUI.onSelectAll();
		for (Enumeration e = childUIViewPane.getSelectedNodes(); e.hasMoreElements();) {
			UINode deletedUINode = (UINode)e.nextElement();
			NodePosition deletedNP = deletedUINode.getNodePosition();
			childView.addDeletedNode(deletedNP);
		}
		for (Enumeration e2 = childUIViewPane.getSelectedLinks(); e2.hasMoreElements();) {
			UILink deletedUILink = (UILink)e2.nextElement();
			LinkProperties deletedLink = deletedUILink.getLinkProperties();
			childView.addDeletedLink(deletedLink);
		}
		childViewPaneUI.onDelete();
	}

	/**
	 * Adjust the scroll bars either to home or to keep the focused node on the screen.
	 */
	private void adjustScrollBars() {

		Point viewPosition = getViewPosition();
		if (viewPosition.x==0 && viewPosition.y==0)
			return;

		UIViewPane pane = getViewPane();

		int verticalScroll = getVerticalScrollBarPosition();
		int horizontalScroll = getHorizontalScrollBarPosition();

		double scale = CoreUtilities.divide(pane.currentScale, pane.previousScale);

		verticalScroll = new Double( verticalScroll * scale ).intValue();
		horizontalScroll = new Double( horizontalScroll * scale ).intValue();

		setVerticalScrollBarPosition(verticalScroll, false);
		setHorizontalScrollBarPosition(horizontalScroll, false);
	}

	/**
	 * Zoom the current map to the next level up (75/50/25/full);
	 */
	public double onZoomNextUp() {

		UIViewPane pane = getViewPane();
		double scale = pane.getZoom();

		if (scale == 0.25)
			scale = 0.50;
		else if (scale == 0.50)
			scale = 0.75;
		else if (scale == 0.75)
			scale = 1.00;
		else
			scale = 1.00;

		pane.setZoom(scale);
		pane.scale();

		adjustScrollBars();

		isFocusedNode = false;

		return scale;
	}

	/**
	 * Zoom the current map to the next level (75/50/25/full);
	 */
	public double onZoomNextDown() {

		UIViewPane pane = getViewPane();
		double scale = pane.getZoom();

		if (scale == 1.00)
			scale = 0.75;
		else if (scale == 0.75)
			scale = 0.50;
		else if (scale == 0.50)
			scale = 0.25;
		else
			scale = 0.25;

		pane.setZoom(scale);
		pane.scale();
		adjustScrollBars();

		isFocusedNode = false;
		return scale;
	}

	/**
	 * Zoom the current map to the next level (75/50/25/full);
	 */
	public void onZoomNext() {

		UIViewPane pane = getViewPane();
		double scale = pane.getZoom();

		if (scale == 0.75)
			scale = 0.50;
		else if (scale == 0.50)
			scale = 0.25;
		else if (scale == 0.25)
			scale = 1.00;
		else
			scale = 0.75;

		pane.setZoom(scale);
		pane.scale();
		adjustScrollBars();

		isFocusedNode = false;
	}

	/**
	 * Zoom the current map using the given scale.
	 */
	public void onZoomTo(double scale) {

		UIViewPane pane = getViewPane();
		pane.setZoom(scale);
		pane.scale();
		adjustScrollBars();

		isFocusedNode = false;
	}

	/**
	 * Zoom the current map to fit it all on the visible view.
	 */
	public void onZoomToFit() {

		UIViewPane pane = getViewPane();
		pane.setZoom(1.0);
		pane.scale();

 		Dimension panesize = pane.calculateSize();

		JViewport viewport = getViewport();
		Dimension viewsize = viewport.getExtentSize();

		// REMOVE EXTRA FROM ORTSIZE TO ALLOW FOR SCROLL BARS?
		double xscale = CoreUtilities.divide(viewsize.width, panesize.width);
		double yscale = CoreUtilities.divide(viewsize.height, panesize.height);

		double scale = xscale;
		if (yscale < xscale)
			scale = yscale;

		if (scale > 1.0)
			scale = 1.0;

		scrollHome(false);
		pane.setZoom(scale);
		pane.scale();

		isFocusedNode = false;
	}

	/**
	 * Zoom the current map back to normal and focus on the last selected node.
	 */
	public boolean onZoomRefocused() {

		UIViewPane pane = getViewPane();
		UINode node = pane.getSelectedNode();
		JViewport port = getViewport();

		if (node == null) {
			ProjectCompendium.APP.displayMessage(LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "UIMapViewFrame.selectNode"), LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "UIMapViewFrame.selectNodeTitle")); //$NON-NLS-1$ //$NON-NLS-2$
			return false;
		}
		else {
			Point nodePos = node.getNodePosition().getPos();

			Dimension dim = port.getExtentSize();
			int portX = (int) ((dim.width/2) - node.getWidth());
			int portY = (int) ((dim.height/2) - node.getHeight());

			Point parentPos = SwingUtilities.convertPoint((Component)pane, nodePos.x, nodePos.y, port);

			int hAdjust = parentPos.x - portX;
			int vAdjust = parentPos.y - portY;

			int currentV = getVerticalScrollBarPosition();
			int currentH = getHorizontalScrollBarPosition();
			setVerticalScrollBarPosition(currentV + vAdjust, false);
			setHorizontalScrollBarPosition(currentH + hAdjust, false);

			pane.setZoom(1.0);
			pane.scale();
			isFocusedNode = true;
			return true;
		}
	}

	/**
	 * Return the UIViewPane instance associated with this frame.
	 * @param UIViewPane, the UIViewPane instance associated with this frame.
	 */
	public UIViewPane getViewPane() {
		return oViewPane;
	}

	/**
	 * Set the view for this Frame.
	 */
	public void setView(View view) {
		oView = view;
		oViewPane.setView(view);
		repaint();
	}

	/**
	 * Remove all the component in the UIViewPane.
	 */
	public void removeViewPane() {
		oViewPane.removeAllComponents();
	}

	/**
	 * Create the UIViewPane instance with the given view.
	 * @param view com.compendium.core.datamodel.View, the view to associated with the new UIViewPane instance.
	 * @return UIViewPane, the new UIViewPOane instance created.
	 */
	public UIViewPane createViewPane(View view) {

		oView = view;

		oViewPane = new UIViewPane(view, this);
		oViewPane.setBackground(Color.white);

		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());

		//reduced size by factor of 10 since size is too big - bz
		panel.setPreferredSize(new Dimension(60000,60000));
		panel.add(oViewPane, BorderLayout.CENTER);
		scrollpane.setViewportView(panel);

		oContentPane.validate();
		oContentPane.repaint();

		return oViewPane;
	}

	/**
	 * Set the current frame selected/deselected and if selected, focus the UIViewPane.
	 * @param selected, ture if the frame should be selected, else false.
	 */
	public void setSelected(boolean selected) {
		boolean wasSelected = isSelected();
		try {
			super.setSelected(selected);
		}
		catch (Exception e) {
			System.out.println("viewframe not selected because "+e.getMessage()); //$NON-NLS-1$
		}

		if (isSelected() && !wasSelected && oViewPane != null) {
			oViewPane.requestFocus();

			// refresh edit-undo and edit-redo button for this Frame
			refreshUndoRedo();
		}
	}

	/**
	 * Null out class variables
	 */
	public void cleanUp() {

		super.cleanUp();

		oAerialDialog 	= null;
		oAerialViewPane = null;
		sBaseTitle = null;
		oViewPane = null;
	}
}
