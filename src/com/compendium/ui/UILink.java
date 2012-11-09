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

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.sql.SQLException;
import java.beans.*;

import javax.swing.*;
import javax.help.*;
import javax.swing.border.*;

import com.compendium.core.datamodel.*;
import com.compendium.core.datamodel.services.ViewService;
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

	// LINK NAMES
	/** Holds a string representation of the RESPONDS_TO_LINK type*/
	public final static String	sRESPONDSTOLINK			= "Responds To Link"; //$NON-NLS-1$

	/** Holds a string representation of the SUPPORTS_LINK type*/
	public final static String	sSUPPORTSLINK			= "Supports Link"; //$NON-NLS-1$

	/** Holds a string representation of the OBJECTS_TO_LINK type*/
	public final static String	sOBJECTSTOLINK			= "Objects To Link"; //$NON-NLS-1$

	/** Holds a string representation of the CHALLENGES_LINK type*/
	public final static String	sCHALLENGESLINK			= "Challenges Link"; //$NON-NLS-1$

	/** Holds a string representation of the SPECIALIZES_LINK type*/
	public final static String	sSPECIALIZESLINK		= "Specializes Link"; //$NON-NLS-1$

	/** Holds a string representation of the EXPANDS_ON_LINK type*/
	public final static String	sEXPANDSONLINK			= "Expands On Link"; //$NON-NLS-1$

	/** Holds a string representation of the RELATED_TO_LINK type*/
	public final static String	sRELATEDTOLINK			= "Related To Link"; //$NON-NLS-1$

	/** Holds a string representation of the ABOUT_LINK type*/
	public final static String	sABOUTLINK				= "About Link"; //$NON-NLS-1$

	/** Holds a string representation of the RESOLVES_LINK type*/
	public final static String	sRESOLVESLINK			= "Resolves Link"; //$NON-NLS-1$

	/** Holds a string representation of the default link type - currently the RELATED_TO_LINK.*/
	public final static String	sDEFAULTLINK			= "Responds To Link"; //$NON-NLS-1$

	// LINK LABELS
	/** Holds a string representation of the RESPONDS_TO_LINK type*/
	public final static String	sRESPONDSTOLINKLABEL	= "Responds To"; //$NON-NLS-1$

	/** Holds a string representation of the SUPPORTS_LINK type*/
	public final static String	sSUPPORTSLINKLABEL		= "Supports"; //$NON-NLS-1$

	/** Holds a string representation of the OBJECTS_TO_LINK type*/
	public final static String	sOBJECTSTOLINKLABEL		= "Objects To"; //$NON-NLS-1$

	/** Holds a string representation of the CHALLENGES_LINK type*/
	public final static String	sCHALLENGESLINKLABEL	= "Challenges"; //$NON-NLS-1$

	/** Holds a string representation of the SPECIALIZES_LINK type*/
	public final static String	sSPECIALIZESLINKLABEL	= "Specializes"; //$NON-NLS-1$

	/** Holds a string representation of the EXPANDS_ON_LINK type*/
	public final static String	sEXPANDSONLINKLABEL			= "Expands On"; //$NON-NLS-1$

	/** Holds a string representation of the RELATED_TO_LINK type*/
	public final static String	sRELATEDTOLINKLABEL			= "Related To"; //$NON-NLS-1$

	/** Holds a string representation of the ABOUT_LINK type*/
	public final static String	sABOUTLINKLABEL				= "About"; //$NON-NLS-1$

	/** Holds a string representation of the RESOLVES_LINK type*/
	public final static String	sRESOLVESLINKLABEL			= "Resolves"; //$NON-NLS-1$
	
	/** A reference to the label property for PropertyChangeEvents.*/
    public static final String LABEL_PROPERTY 		= "linktext"; //$NON-NLS-1$

	/** A reference to the link type property for PropertyChangeEvents.*/
    public static final String TYPE_PROPERTY 		= "linktype"; //$NON-NLS-1$

	/** The selection color to use for this link.*/
	private static final Color SELECTED_COLOR = Color.yellow; //basic yellow for white bg

	/** The associated Link datamodel object.*/
	protected Link				oLink			= null;
	
	/** The associated LinkProperties object that holds the formatting data.*/
	protected LinkProperties	oLinkProperties = null;

	/** The originating UINode for this link.*/
	protected UINode			oFromNode		= null;

	/** The destination UINode for this link.*/
	protected UINode			oToNode			= null;

	/** The value of the label.*/
	protected String			sText			=""; //$NON-NLS-1$

	/**
	 * Constructor. Creates a new instance of UILink with the given parameters.
	 * @param link the associated Link datamodel object.
	 * @param props the associated LinkProperties object that holds the link formatting data.
	 * @param fromNode the originating UINode for this link.
	 * @param toNode the destination UINode for this link.
	 */
  	public UILink(Link link, LinkProperties props, UINode fromNode, UINode toNode) {
		oLink = link;
	    oLink.addPropertyChangeListener(this);
		setLinkProps(props);

		CSH.setHelpIDString(this,"node.links"); //$NON-NLS-1$
 		
		// line coordinates will be absolute
		setCoordinateType(UILine.ABSOLUTE);
		
		// set minimum width to 12 to allow for display of rollover indicator
		setMinWidth(12);
		
		// set selected color;
		setSelectedColor(SELECTED_COLOR);

		// set the nodes that are linked with this link
		setFromNode(fromNode);
		setToNode(toNode);

		//setBorder(new LineBorder(Color.red, 1));
		
	    //remove all returns and tabs which show up in the GUI as evil black char
	    String label = ""; //$NON-NLS-1$
	    label = oLink.getLabel();
	    if (label == null || label.equals(ICoreConstants.NOLABEL_STRING))
			label = ""; //$NON-NLS-1$

	    label = label.replace('\n',' ');
	    label = label.replace('\r',' ');
	    label = label.replace('\t',' ');
	    setText(label);

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
  	 * Set the local link properties based on the passed properties.
  	 * @param props
  	 */
  	private void setLinkProps(LinkProperties props) {
  		if (oLinkProperties != null) {
  			oLinkProperties.removePropertyChangeListener(this);
  		}
		oLinkProperties = props;
	    props.addPropertyChangeListener(this);

		setArrow(props.getArrowType());

		// set line color
		//It will be zero only on update from version 1.5.2, so needs updating to the correct Colour.
		//the RGB for black actually comes out as a minus number and not zero as expected.
		//So zero is safe to represent an unset colour.
		if (props.getLinkColour() == 0) {
			Color col = this.getLinkColor(oLink.getType());
			props.setLinkColour(col.getRGB());
		}
		
		setForeground(new Color(props.getLinkColour()));
		setLineThickness(props.getLinkWeight());	
		
		// set the font based on the link properties
		setDefaultFont();
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
		setFont(ProjectCompendiumFrame.currentDefaultFont);

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
		Font labelFont = new Font(oLinkProperties.getFontFace(), oLinkProperties.getFontStyle(), oLinkProperties.getFontSize());
		setFont(labelFont);
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
			ProjectCompendium.APP.displayError("Error: (UILink.setText) "+LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "UILink.unableToUpdateLabel")+"\n\n"+io.getMessage()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	    }
    }

	/**
	 * Returns the Link object.
	 * @return Link.
	 */
	public Link getLink() {
		return oLink;
	}

	/**
	 * Returns LinkProperties object associated with this link.
	 * @return LinkProperties object associated with this link.
	 */
	public LinkProperties getLinkProperties() {
		return oLinkProperties;
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
		return "LinkUI"; //$NON-NLS-1$
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
		firePropertyChange("fromnode", oldValue, oFromNode); //$NON-NLS-1$

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
		firePropertyChange("tonode", oldValue, oToNode); //$NON-NLS-1$

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
		UINode to = getToNode();

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
		UILinkPopupMenu pop = new UILinkPopupMenu("Popup menu", linkui, userID); //$NON-NLS-1$
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
	 * @param arrow the arrow head style for this link.
	 */
	private void updateArrow(int arrow) {
		try {
			setArrow(arrow);
			double currentScale = this.getViewPane().getZoom();
			AffineTransform trans=new AffineTransform();
			trans.setToScale(currentScale, currentScale);
			scaleLink(trans);			
		}
		catch(Exception ex) {
			ProjectCompendium.APP.displayError("Error: (UILink.updateArrow) "+LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "UILink.unableToUpdateArrow")+"\n\n"+ex.getMessage()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}
	}

	/**
	 * Set the link type for this link.
	 * @param type, the link type for this link.
	 */
	public void setLinkType(String type) {
		String oldValue = oLink.getType();
		if (oldValue == type) {
			return;
		}
		
		try {
			oLink.setType(type);
			
			//update all LinkProperties to the ones for the chosen link type
			LinkProperties props = UIUtilities.getLinkProperties(type);
			UIViewPane pane = getViewPane();
			try {
				View view  = pane.getView();
				if (view != null) {
					LinkProperties newProps = (LinkProperties)view.updateLinkFormatting(oLink.getId(), props);
					if (newProps != null) {
						setLinkProps(newProps);
					} else {
						System.out.println("Failed to update LinkProperties after set type due to newProps being null"); //$NON-NLS-1$
					}
				}
			} catch (SQLException ex) {
				System.out.println("Failed to update LinkProperties after set type due to:"+ex.getMessage()); //$NON-NLS-1$
			}

			firePropertyChange(TYPE_PROPERTY, oldValue, type);
			repaint();
		}
		catch(Exception ex) {
			ex.printStackTrace();
			ProjectCompendium.APP.displayError("Error: (UILink.setLinkType) "+LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "UILink.unableToUpdateLink")+"\n\n"+ex.getMessage()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
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

		String linkType = ""; //$NON-NLS-1$

		if(type.equals(UILink.sRESPONDSTOLINK)) {
			linkType = ICoreConstants.RESPONDS_TO_LINK;
		}
		else if(type.equals(UILink.sSUPPORTSLINK)) {
			linkType = ICoreConstants.SUPPORTS_LINK;
		}
		else if(type.equals(UILink.sOBJECTSTOLINK)) {
			linkType = ICoreConstants.OBJECTS_TO_LINK;
		}
		else if(type.equals(UILink.sCHALLENGESLINK)) {
			linkType = ICoreConstants.CHALLENGES_LINK;
		}
		else if(type.equals(UILink.sSPECIALIZESLINK)) {
			linkType = ICoreConstants.SPECIALIZES_LINK;
		}
		else if(type.equals(UILink.sEXPANDSONLINK)) {
			linkType = ICoreConstants.EXPANDS_ON_LINK;
		}
		else if(type.equals(UILink.sRELATEDTOLINK)) {
			linkType = ICoreConstants.RELATED_TO_LINK;
		}
		else if(type.equals(UILink.sABOUTLINK)) {
			linkType = ICoreConstants.ABOUT_LINK;
		}
		else if(type.equals(UILink.sRESOLVESLINK)) {
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
		String linkType = ""; //$NON-NLS-1$

		if (oLinktype == null) {
			if (type.equals(ICoreConstants.RESPONDS_TO_LINK))
				linkType = UILink.sRESPONDSTOLINKLABEL;
			else if (type.equals(ICoreConstants.SUPPORTS_LINK))
				linkType = UILink.sSUPPORTSLINKLABEL;
			else if (type.equals(ICoreConstants.OBJECTS_TO_LINK))
				linkType = UILink.sOBJECTSTOLINKLABEL;
			else if (type.equals(ICoreConstants.CHALLENGES_LINK))
				linkType = UILink.sCHALLENGESLINKLABEL;
			else if (type.equals(ICoreConstants.SPECIALIZES_LINK))
				linkType = UILink.sSPECIALIZESLINKLABEL;
			else if (type.equals(ICoreConstants.EXPANDS_ON_LINK))
				linkType = UILink.sEXPANDSONLINKLABEL;
			else if (type.equals(ICoreConstants.RELATED_TO_LINK))
				linkType = UILink.sRELATEDTOLINKLABEL;
			else if (type.equals(ICoreConstants.ABOUT_LINK))
				linkType = UILink.sABOUTLINKLABEL;
			else if (type.equals(ICoreConstants.RESOLVES_LINK))
				linkType = UILink.sRESOLVESLINKLABEL;
			else
				linkType = LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "UILink.unknown"); //$NON-NLS-1$
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
	 * Scale the arrow and line thickness with with the given transform.
	 * @param trans, the transform to use when scaling.
	 */
	public void scaleLink(AffineTransform trans) {
		if (trans == null) {
			nCurrentArrowWidth = nArrowWidth;
			nCurrentThickness = nThickness;
		} else {
			Point p1 = new Point(nArrowWidth, nArrowWidth);
			try {
				p1 = (Point)trans.transform(p1, new Point(0, 0));
			}
			catch(Exception e) {
				System.out.println("can't convert arrow width\n\n"+e.getMessage()); //$NON-NLS-1$
			}
			if (p1.x < 7)
				p1.x = p1.x+1;

			nCurrentArrowWidth = p1.x;
			
			Point p2 = new Point(nThickness, nThickness);
			try {
				p2 = (Point)trans.transform(p2, new Point(0, 0));
			}
			catch(Exception e) {
				System.out.println("can't convert line thickness\n\n"+e.getMessage()); //$NON-NLS-1$
			}
			if (p2.x < 1)
				p2.x = 1;

			nCurrentThickness = p2.x;
		}
	}

	/**
	 * Sets the font used to display the link's text.
	 *
	 * @param font The font to use.
	 */
	public void setFont(Font font) {		
		super.setFont(font);				
    	((LinkUI)getUI()).refreshBounds();
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
	    else if (prop.equals(LinkProperties.FONTFACE_PROPERTY)) {
			Font font = getFont();
			Font newFont = new Font((String)newvalue, font.getStyle(), font.getSize());
			setFont(newFont);		
		}	
	    else if (prop.equals(LinkProperties.FONTSTYLE_PROPERTY)) {
			Font font = getFont();
			Font newFont = new Font(font.getName(), ((Integer)newvalue).intValue(), font.getSize());	
			setFont(newFont);	
		}		    
	    else if (prop.equals(LinkProperties.FONTSIZE_PROPERTY)) {
			Font font = getFont();
			int newsize = ((Integer)newvalue).intValue();
			Font newFont = new Font(font.getName(), font.getStyle(), newsize);				
			setFont(newFont);	//scales	
			
			int adjustment = ProjectCompendium.APP.getToolBarManager().getTextZoom();
			font = getFont();
			Font adjustedFont = new Font(font.getName(), font.getStyle(), font.getSize()+adjustment);	
			super.setFont(adjustedFont);
		}	
	    else if (prop.equals(LinkProperties.TEXT_FOREGROUND_PROPERTY)) {
	    	((LinkUI)getUI()).refreshBounds();
		}		    
	    else if (prop.equals(LinkProperties.TEXT_BACKGROUND_PROPERTY)) {
	    	((LinkUI)getUI()).refreshBounds();
		}		    		    
	    else if (prop.equals(LinkProperties.WRAP_WIDTH_PROPERTY)) {
	    	((LinkUI)getUI()).refreshBounds();
		}		    
	    else if (prop.equals(LinkProperties.ARROWTYPE_PROPERTY)) {
			updateArrow( ((Integer)evt.getNewValue()).intValue() );
	    	((LinkUI)getUI()).refreshBounds();
	    }
	    else if (prop.equals(LinkProperties.LINKDASHED_PROPERTY)) {
	    	((LinkUI)getUI()).refreshBounds();
		}		    
	    else if (prop.equals(LinkProperties.LINKSTYLE_PROPERTY)) {
	    	((LinkUI)getUI()).refreshBounds();
		}		    
	    else if (prop.equals(LinkProperties.LINKCOLOUR_PROPERTY)) {
			setForeground(new Color(oLinkProperties.getLinkColour()));
			if ((LinkUI)getUI() != null) {
				((LinkUI)getUI()).refreshBounds();
			}
	    }
	    else if (prop.equals(LinkProperties.LINKWEIGHT_PROPERTY)) {
			setLineThickness(oLinkProperties.getLinkWeight());	
			double currentScale = this.getViewPane().getZoom();
			AffineTransform trans=new AffineTransform();
			trans.setToScale(currentScale, currentScale);
			scaleLink(trans);
	    	((LinkUI)getUI()).refreshBounds();
	    }

	    repaint();
	}
}
