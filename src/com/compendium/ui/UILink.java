/********************************************************************************
 *                                                                              *
 *  (c) Copyright 2009 Verizon Communications USA and The Open University UK    *
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

import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.beans.*;

import javax.swing.*;
import javax.help.*;
import javax.swing.border.*;

import com.compendium.core.datamodel.*;
import com.compendium.core.ICoreConstants;

import com.compendium.*;
import com.compendium.ui.plaf.*;
import com.compendium.ui.popups.*;
import com.compendium.ui.linkgroups.*;
import com.compendium.ui.dialogs.UILinkContentDialog;


/**
 * The main class that handles information for a map link.
 *
 * @author	Mohammed Sajid Ali / Michelle Bachler
 */
public class UILink extends UILine implements PropertyChangeListener {

	/** A reference to the label property for PropertyChangeEvents.*/
    public static final String LABEL_PROPERTY 		= "linktext";

	/** A reference to the link type property for PropertyChangeEvents.*/
    public static final String TYPE_PROPERTY 		= "linktype";

	/** The selection color to use for this link.*/
	private static final Color SELECTED_COLOR = Color.yellow; //basic yellow for white bg

	/** The associated Link datamodel object.*/
	protected Link		oLink			= null;

	/** The originating UINode for this link.*/
	protected UINode	oFromNode		= null;

	/** The destination UINode for this link.*/
	protected UINode	oToNode		= null;

	/** The value of the label.*/
	protected String	sText		="";

	/**
	 * Constructor. Creates a new instance of UILink with the given parameters.
	 * @param link com.compendium.core.datamodel.Link, the associated Link datamodel object.
	 * @param fromNode com.compendium.ui.UINode, the originating UINode for this link.
	 * @param toNode com.compendium.ui.UINode, the destination UINode for this link.
	 */
  	public UILink(Link link, UINode fromNode, UINode toNode) {
	    setFont(ProjectCompendiumFrame.labelFont);
  		
		oLink = link;
	    oLink.addPropertyChangeListener(this);

		CSH.setHelpIDString(this,"node.links");

		// line coordinates will be absolute
		setCoordinateType(UILine.ABSOLUTE);

		// arrow will be pointing to to-node
		setArrow(link.getArrow());

		// set minimum width to 12 to allow for display of rollover indicator
		setMinWidth(12);

		// set line color
		String type = link.getType();
		setForeground(getLinkColor(type));

		// set selected color;
		setSelectedColor(SELECTED_COLOR);

		// set the nodes that are linked with this link
		setFromNode(fromNode);
		setToNode(toNode);

	    //remove all returns and tabs which show up in the GUI as evil black char
	    String label = "";
	    label = oLink.getLabel();
	    if (label == null || label.equals(ICoreConstants.NOLABEL_STRING))
			label = "";

	    label = label.replace('\n',' ');
	    label = label.replace('\r',' ');
	    label = label.replace('\t',' ');
	    setText(label);

		updateUI();
		//setBorder(new LineBorder(Color.black, 1));
		
	    addFocusListener( new FocusListener() {
			public void focusGained(FocusEvent e) {
			    repaint();
			}
			public void focusLost(FocusEvent e) {
			    ((LinkUI)getUI()).resetEditing();
			    repaint();
			}
	    });
	}

	/**
	 * Constructor. Creates a new instance of UILink with the given parameters.
	 * <p>Used for creating dummy link when creating a new link in ui.
	 * <p>
	 * @param link com.compendium.core.datamodel.Link, the associated Link datamodel object.
	 * @param fromNode com.compendium.ui.UINode, the originating UINode for this link.
	 * @param toNode com.compendium.ui.UINode, the destination UINode for this link.
	 * @param type, the link type for this link.
	 */
	public UILink(UINode fromNode, UINode toNode, String type) {
		setFont(ProjectCompendiumFrame.labelFont);

		// line coordinates will be absolute
		setCoordinateType(UILine.ABSOLUTE);

		// arrow will be pointing to to-node
		setArrow(ICoreConstants.ARROW_TO);

		// set minimum width to 12 to allow for display of rollover indicator
		setMinWidth(12);

		// set line color
		if (type.equals(ICoreConstants.DEFAULT_LINK))
			setForeground(Color.gray);
		else if (type.equals(ICoreConstants.SUPPORTS_LINK))
			setForeground(Color.green);
		else if (type.equals(ICoreConstants.OBJECTS_TO_LINK))
			setForeground(Color.red);

		// set selected color;
		setSelectedColor(SELECTED_COLOR);

		// set the nodes that are linked with this link
		setFromNode(fromNode);
		setToNode(toNode);

		updateUI();

	    addFocusListener( new FocusListener() {
			public void focusGained(FocusEvent e) {
			    repaint();
			}
			public void focusLost(FocusEvent e) {
			    ((LinkUI)getUI()).resetEditing();
			    repaint();
			}
	    });
	}

	/**
	 * Increase the font size displayed by one point.
	 * This does not change the setting in the database.
	 * @return the new size.
	 */
	public int increaseFontSize() {
		Font font = getFont();
		int newSize = font.getSize()+1;
		Font newFont = new Font(font.getName(), font.getStyle(), font.getSize()+1);	
		super.setFont(newFont);
		((LinkUI)getUI()).getPreferredSize(this);
		repaint(10);		
		return newSize;
	}
	
	/**
	 * Decrease the font size displayed by one point.
	 * This does not change the setting in the database.
	 * @return the new size.
	 */
	public int decreaseFontSize() {
		Font font = getFont();
		int newSize = font.getSize()-1;
		Font newFont = new Font(font.getName(), font.getStyle(), font.getSize()-1);	
		super.setFont(newFont);
		((LinkUI)getUI()).getPreferredSize(this);
		repaint(10);
	   	return newSize;
	}
	
	/**
	 * Restore the font to the default settings.
	 *
	 */
	public void setDefaultFont() {
		setFont(ProjectCompendiumFrame.labelFont);
		((LinkUI)getUI()).getPreferredSize(this);
		repaint(10);
	}
	
	/**
	 * Returns the text string that the link displays.
	 * <p>
	 * @return String, the text string that the link displays.
	 * @see #setText
	 */
	public String getText() {
	    return sText;
	}

	/**
	 * Defines the label text this component will display.
	 * <p>
	 * @param text, the new text to display as the link label.
	 */
	public void setText(String text) {
	    String oldValue = sText;

	    try {
			if (oLink != null) {
				oLink.setLabel(text);
				sText = text;
				firePropertyChange(LABEL_PROPERTY, oldValue, sText);
				repaint();
			}
	    }
	    catch(Exception io) {
			io.printStackTrace();
			ProjectCompendium.APP.displayError("Error: (UILink.setText) Unable to update label.\n\n"+io.getMessage());
	    }
    }

	/**
	 * Returns the datamodel link object.
	 * @return com.compendium.core.datamodel.Link.
	 */
	public Link getLink() {
		return oLink;
	}

	/**
	 * Notification from the UIFactory that the L&F has changed.
	 *
	 * @see JComponent#updateUI
	 */
	public void updateUI() {
		//setUI((LinkUI)UIManager.getUI(this));
		setUI(new LinkUI());
		invalidate();
	}

  	/**
   	 * Returns a string that specifies the name of the l&f class
   	 * that renders this component.
   	 *
   	 * @return String "LinkUI"
   	 *
   	 * @see JComponent#getUIClassID
   	 * @see UIDefaults#getUI
   	 */
  	public String getUIClassID() {
		return "LinkUI";
  	}

	/**
	 * Return the originating UINode for this link.
	 * @return com.compendium.ui.UINode, the originating UINode for this link.
	 */
	public UINode getFromNode() {
		return oFromNode;
	}

	/**
	 * Set the originating node for this link.  Fires a property change event.
	 * @param node com.compendium.ui.UINode, the originating UINode for this link.
	 */
	public void setFromNode(UINode node) {
		UINode oldValue = oFromNode;
		oFromNode = node;
		firePropertyChange("fromnode", oldValue, oFromNode);

		updateConnectionPoints();
	}

	/**
	 * Return the destination UINode for this link.
	 * @return com.compendium.ui.UINode, the destination UINode for this link.
	 */
	public UINode getToNode() {
		return oToNode;
	}

	/**
	 * Set the destination node for this link. Fires a property change event.
	 * @param node com.compendium.ui.UINode, the destination UINode for this link.
	 */
	public void setToNode(UINode node) {
		UINode oldValue = oToNode;
		oToNode = node;
		firePropertyChange("tonode", oldValue, oToNode);

		updateConnectionPoints();
	}

  	/**
	 * Convenience method that searchs the anscestor heirarchy for a UIViewPane instance.
	 * @return com.compendium.ui.UIViewPane, the parent pane for this link.
   	 */
  	public UIViewPane getViewPane() {
		Container p;

		// Search upward for viewpane
		p = getParent();
		while (p != null && !(p instanceof UIViewPane))
	    	p = p.getParent();

		return (UIViewPane)p;
	}

	/**
	 * Set the absolute from and to points for this link based on the position
	 * of the from and to nodes.
	 */
	public void updateConnectionPoints() {
		UINode from = getFromNode();
		UINode to		= getToNode();

		Rectangle rFrom = new Rectangle();
		Rectangle rTo	= new Rectangle();

		if (from == null || to == null)
			return;

		// get the bounds for each node
		rFrom = from.getBounds();
		rTo	= to.getBounds();

		// calculate the center for each node, used as basis for drawing line
		// between nodes.
		Point ptFromCenter = new Point(rFrom.x+(rFrom.width/2), rFrom.y+(rFrom.height/2));
		Point ptToCenter = new Point(rTo.x+(rTo.width/2), rTo.y+(rTo.height/2));

		// calculate the intersecting point between the bounds of the node and
		// the connecting line. We only want the line to draw to the boundary
		// of the node, not to the center of the node
		//System.out.println("PTS FROM " + rFrom + "," + ptFromCenter + "," + ptToCenter);
		//System.out.println("PTS TO " + rTo + "," + ptFromCenter + "," + ptToCenter);

		Point[] pts1 = UILine.intersectionWithRectangle(rFrom, ptFromCenter, ptToCenter);
		Point[] pts2 = UILine.intersectionWithRectangle(rTo, ptFromCenter, ptToCenter);

		//this is a patch.
		//if both the rectangles have the same center point, then this above
		//2 array of points has null.
		if ( (ptFromCenter.x == ptToCenter.x) &&
			 (ptFromCenter.y == ptToCenter.y) ) {
			if (rFrom.y > rTo.y) {
				pts1[0] = new Point(rFrom.x + rFrom.width, rFrom.y);
				pts1[1] = pts1[0];
				pts2[0] = new Point(rTo.x + rTo.width, rFrom.y);
				pts2[1] = pts2[0];
			} else {
				pts1[0] = new Point(rFrom.x + rFrom.width, rTo.y);
				pts1[1] = pts1[0];
				pts2[0] = new Point(rTo.x + rTo.width, rTo.y);
				pts2[1] = pts2[0];
			}
		}

		// figure out which points to use
		setFrom(getClosestPoint(pts1[0], pts1[1], ptToCenter));
		setTo(getClosestPoint(pts2[0], pts2[1], ptFromCenter));

		setBounds(getPreferredBounds()); // swing calls repaint from setBounds.
	}

	/**
	 * Given two points and a center point, return the point closest to the
	 * center point.
	 *
	 * @param p1, the first point to check.
	 * @param p2, the second point to check.
	 * @param cp, the centerpoint to check.
	 * @return Point, the point closest to the center point.
	 */
	private Point getClosestPoint(Point p1, Point p2, Point cp) {
		if (p1 == null)
			return p2;
		if (p2 == null)
			return p1;

		double hypo1 = Math.sqrt((cp.x-p1.x)*(cp.x-p1.x)+(cp.y-p1.y)*(cp.y-p1.y));
		double hypo2 = Math.sqrt((cp.x-p2.x)*(cp.x-p2.x)+(cp.y-p2.y)*(cp.y-p2.y));

		if (hypo1 <= hypo2)
			return p1;
		else
			return p2;
	}

	/**
	 * Open a popup menu  for this link.
	 * @param linkui, the ui instance to pass to the popup menu.
	 * @param x, the x position for the popup menu.
	 * @param y, the y position for this popup menu.
	 */
	public void showPopupMenu(LinkUI linkui, int x, int y) {
		String userID = ProjectCompendium.APP.getModel().getUserProfile().getId();
		UILinkPopupMenu pop = new UILinkPopupMenu("Popup menu", linkui, userID);
		pop.setCoordinates(x,y);
		pop.setViewPane(getViewPane());
		pop.show(this,x,y);
	}

	/**
	 * Open a UILinkDialog instance on the contents tab.
	 */
	public void showEditDialog() {
		UILinkContentDialog dlg = new UILinkContentDialog(ProjectCompendium.APP, this, UILinkContentDialog.CONTENTS_TAB);
		UIUtilities.centerComponent(dlg, ProjectCompendium.APP);
		dlg.setVisible(true);
	}

	/**
	 * Open a UILinkDialog instance on the properties tab.
	 */
	public void showPropertiesDialog() {
		UILinkContentDialog dlg = new UILinkContentDialog(ProjectCompendium.APP, this, UILinkContentDialog.PROPERTIES_TAB);
		UIUtilities.centerComponent(dlg, ProjectCompendium.APP);
		dlg.setVisible(true);
	}

	/**
	 * Convenience method that moves this component to position 0 if it's
	 * parent is a JLayeredPane.
   	 */
  	public void moveToFront() {
		if (getParent() != null && getParent() instanceof JLayeredPane) {
	  		JLayeredPane l =  (JLayeredPane)getParent();
	  		l.moveToFront(this);
		}
  	}

  	/**
	 * Convenience method that moves this component to position -1 if it's
   	 * parent is a JLayeredPane.
   	 */
  	public void moveToBack() {
		if (getParent() != null && getParent() instanceof JLayeredPane) {
	  		JLayeredPane l =  (JLayeredPane)getParent();
	  		l.moveToBack(this);
		}
  	}

	/**
	 * Set the arrow head style for this link.
	 * @param arrow, the arrow head style for this link.
	 */
	public void setArrow(int arrow) {

		super.setArrow(arrow);
	}

	/**
	 * Update the arrow head style for this link.
	 * @param arrow, the arrow head style for this link.
	 */
	public void updateArrow(int arrow) {

		try {
			oLink.setArrow(arrow);
			setArrow(arrow);
		}
		catch(Exception ex) {
			ProjectCompendium.APP.displayError("Error: (UILink.updateArrow) Unable to update arrow\n\n"+ex.getMessage());
		}
	}

	/**
	 * Set the link type for this link.
	 * @param type, the link type for this link.
	 */
	public void setLinkType(String type) {
		try {
			String oldValue = oLink.getType();
			oLink.setType(type);
			setForeground(getLinkColor(type));

			firePropertyChange(TYPE_PROPERTY, oldValue, type);
			repaint();
		}
		catch(Exception ex) {
			ex.printStackTrace();
			ProjectCompendium.APP.displayError("Error: (UILink.setLinkType) Unable to update link type\n\n"+ex.getMessage());
		}
	}

	/**
	 * Get the link type for this link.
	 * @return String, the link type for this link.
	 */
	public String getLinkType() {
		return oLink.getType();
	}


   /**
	* Returns the Link type for the given link type description
	* @param type, the link type description to return the link type for.
	* @param int, the link type assoicated with the given description.
	*/
	public static String getLinkType(String type) {

		String linkType = "";

		if(type.equals(ICoreConstants.sRESPONDSTOLINK)) {
			linkType = ICoreConstants.RESPONDS_TO_LINK;
		}
		else if(type.equals(ICoreConstants.sSUPPORTSLINK)) {
			linkType = ICoreConstants.SUPPORTS_LINK;
		}
		else if(type.equals(ICoreConstants.sOBJECTSTOLINK)) {
			linkType = ICoreConstants.OBJECTS_TO_LINK;
		}
		else if(type.equals(ICoreConstants.sCHALLENGESLINK)) {
			linkType = ICoreConstants.CHALLENGES_LINK;
		}
		else if(type.equals(ICoreConstants.sSPECIALIZESLINK)) {
			linkType = ICoreConstants.SPECIALIZES_LINK;
		}
		else if(type.equals(ICoreConstants.sEXPANDSONLINK)) {
			linkType = ICoreConstants.EXPANDS_ON_LINK;
		}
		else if(type.equals(ICoreConstants.sRELATEDTOLINK)) {
			linkType = ICoreConstants.RELATED_TO_LINK;
		}
		else if(type.equals(ICoreConstants.sABOUTLINK)) {
			linkType = ICoreConstants.ABOUT_LINK;
		}
		else if(type.equals(ICoreConstants.sRESOLVESLINK)) {
			linkType = ICoreConstants.RESOLVES_LINK;
		}
		else {
			linkType = ICoreConstants.DEFAULT_LINK;
		}

		return linkType;
	}

	/**
	 * Get the link type description for the given link type.
	 * @param type, the link type to return the description for.
	 * @return String, the description for the given link type.
	 */
/*	public static String getLinkTypeName(String type) {

		String linkType = "";
		Color linkColor = null;
		UILinkType oLinktype = ProjectCompendium.APP.oLinkGroupManager.getLinkType(type);

		if (oLinktype == null) {
			int nType = 0;
			try {
				nType = new Integer(type).intValue();
			catch(NumberFormatException ex) {
				nType = -1;
			}

			switch (nType) {
				case ICoreConstants.RESPONDS_TO_LINK: {
					linkType = ICoreConstants.sRESPONDSTOLINK;
					break;
				}
				case ICoreConstants.SUPPORTS_LINK: {
					linkType = ICoreConstants.sSUPPORTSLINK;
					break;
				}
				case ICoreConstants.OBJECTS_TO_LINK: {
					linkType = ICoreConstants.sOBJECTSTOLINK;
					break;
				}
				case ICoreConstants.CHALLENGES_LINK: {
					linkType = ICoreConstants.sCHALLENGESLINK;
					break;
				}
				case ICoreConstants.SPECIALIZES_LINK: {
					linkType = ICoreConstants.sSPECIALIZESLINK;
					break;
				}
				case ICoreConstants.EXPANDS_ON_LINK: {
					linkType = ICoreConstants.sEXPANDSONLINK;
					break;
				}
				case ICoreConstants.RELATED_TO_LINK: {
					linkType = ICoreConstants.sRELATEDTOLINK;
					break;
				}
				case ICoreConstants.ABOUT_LINK: {
					linkType = ICoreConstants.sABOUTLINK;
					break;
				}
				case ICoreConstants.RESOLVES_LINK: {
					linkType = ICoreConstants.sRESOLVESLINK;
					break;
				}
				default : {
					linkType = ICoreConstants.sDEFAULTLINK;
					break;
				}
			}
		}
		else {
			return oLinktype.getName();
		}

		return linkType;
	}
*/

	/**
	 * Get the link type label for the given link type.
	 * @param type, the link type to return the description for.
	 * @return String, the description for the given link type.
	 */
	public static String getLinkTypeLabel(String type) {

		UILinkType oLinktype = ProjectCompendium.APP.oLinkGroupManager.getLinkType(type);
		String linkType = "";

		if (oLinktype == null) {
			if (type.equals(ICoreConstants.RESPONDS_TO_LINK))
				linkType = ICoreConstants.sRESPONDSTOLINKLABEL;
			else if (type.equals(ICoreConstants.SUPPORTS_LINK))
				linkType = ICoreConstants.sSUPPORTSLINKLABEL;
			else if (type.equals(ICoreConstants.OBJECTS_TO_LINK))
				linkType = ICoreConstants.sOBJECTSTOLINKLABEL;
			else if (type.equals(ICoreConstants.CHALLENGES_LINK))
				linkType = ICoreConstants.sCHALLENGESLINKLABEL;
			else if (type.equals(ICoreConstants.SPECIALIZES_LINK))
				linkType = ICoreConstants.sSPECIALIZESLINKLABEL;
			else if (type.equals(ICoreConstants.EXPANDS_ON_LINK))
				linkType = ICoreConstants.sEXPANDSONLINKLABEL;
			else if (type.equals(ICoreConstants.RELATED_TO_LINK))
				linkType = ICoreConstants.sRELATEDTOLINKLABEL;
			else if (type.equals(ICoreConstants.ABOUT_LINK))
				linkType = ICoreConstants.sABOUTLINKLABEL;
			else if (type.equals(ICoreConstants.RESOLVES_LINK))
				linkType = ICoreConstants.sRESOLVESLINKLABEL;
			else
				linkType = "Unknown";
		}
		else {
			return oLinktype.getName();
		}

		return linkType;
	}

	/**
	 * Get the link color based on its type.
	 * @param type, the link type to return the color for.
	 * @return Color, the color for the given link type.
	 */
	public static Color getLinkColor(String type) {

		Color linkColor = null;
		UILinkType oLinktype = ProjectCompendium.APP.oLinkGroupManager.getLinkType(type);

		if (oLinktype == null) {
			if (type.equals(ICoreConstants.RESPONDS_TO_LINK))
				linkColor = Color.magenta;
			else if (type.equals(ICoreConstants.SUPPORTS_LINK))
				linkColor = Color.green;
			else if (type.equals(ICoreConstants.OBJECTS_TO_LINK))
				linkColor = Color.red;
			else if (type.equals(ICoreConstants.CHALLENGES_LINK))
				linkColor = Color.pink;
			else if (type.equals(ICoreConstants.SPECIALIZES_LINK))
				linkColor = Color.blue;
			else if (type.equals(ICoreConstants.EXPANDS_ON_LINK))
				linkColor = Color.orange;
			else if (type.equals(ICoreConstants.RELATED_TO_LINK))
				linkColor = Color.black;
			else if (type.equals(ICoreConstants.ABOUT_LINK))
				linkColor = Color.cyan;
			else if (type.equals(ICoreConstants.RESOLVES_LINK))
				linkColor = Color.gray;
			else
				linkColor = Color.black;
		}
		else
			linkColor = oLinktype.getColour();

		return linkColor;
	}

	/**
	 * Toggle selection.
	 */
	public void controlClick() {

		if(this.isSelected()) {
			//deselect from the group
			setSelected(false);
			getViewPane().removeLink(this);
		}
		else {
			//select and add to the group
			setSelected(true);
			getViewPane().setSelectedLink(this,ICoreConstants.MULTISELECT);
			moveToFront();
		}
  	}

	/**
	 * Handle a PropertyChangeEvent.
	 * @param evt, the associated PropertyChangeEvent to handle.
	 */
	public void propertyChange(PropertyChangeEvent evt) {

	    String prop = evt.getPropertyName();
	    Object newvalue = evt.getNewValue();

	    if (prop.equals(Link.LABEL_PROPERTY)) {
			setText((String)evt.getNewValue());
	    }
	    else if (prop.equals(Link.TYPE_PROPERTY)) {
			setLinkType( (String)evt.getNewValue() );
	    }
	    else if (prop.equals(Link.ARROW_PROPERTY)) {
			updateArrow( ((Integer)evt.getNewValue()).intValue() );
	    }

	    repaint();
	}
}
