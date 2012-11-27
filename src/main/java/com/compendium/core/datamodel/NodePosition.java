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

import java.awt.Point;
import java.util.Date;
import java.util.Vector;
import java.sql.SQLException;

import com.compendium.core.datamodel.services.*;


/**
 * The INodePosition object defines the position of a node
 * in a view. The position is defined by an X and Y coordinate
 * in the view's relative coordinate system.
 * And Node in View formatting properties are now in here too.
 *
 * @author	rema and sajid / Michelle Bachler
 */
public class NodePosition extends PCObject implements INodePosition, java.io.Serializable {

	/** Position property name for use with property change events */
	public final static String POSITION_PROPERTY = "position";

	/** Position property name for use with property change events */
	public final static String FONTFACE_PROPERTY = "fontface";

	/** Position property name for use with property change events */
	public final static String FONTSTYLE_PROPERTY = "fontstyle";

	/** Position property name for use with property change events */
	public final static String FONTSIZE_PROPERTY = "fontsize";

	/** Position property name for use with property change events */
	public final static String TEXT_FOREGROUND_PROPERTY = "textforeground";

	/** Position property name for use with property change events */
	public final static String TEXT_BACKGROUND_PROPERTY = "textbackground";

	/** Position property name for use with property change events */
	public final static String WRAP_WIDTH_PROPERTY = "labelwrapwidth";

	/** Position property name for use with property change events */
	public final static String TAGS_INDICATOR_PROPERTY = "tagindicator";

	/** Position property name for use with property change events */
	public final static String TEXT_INDICATOR_PROPERTY = "textindicator";

	/** Position property name for use with property change events */
	public final static String TRANS_INDICATOR_PROPERTY = "transindicator";

	/** Position property name for use with property change events */
	public final static String WEIGHT_INDICATOR_PROPERTY = "weightindicator";

	/** Position property name for use with property change events */
	public final static String SMALL_ICON_PROPERTY = "smallicons";

	/** Position property name for use with property change events */
	public final static String HIDE_ICON_PROPERTY = "hideicons";

	/** the view the node associated with this object is in.*/
	protected View			oView 		= null;

	/** The node associated with this object*/
	protected NodeSummary	oNodeSummary 	= null;

	/** The x coordinates of the node associated with this object.*/
	protected int	 		nX			= -1;

	/** The y coordinates of the node associated with this object.*/
	protected int			nY			= -1;

	/** The date this object was created.*/
	protected Date			oCreationDate			= null;

	/** The date this object was last modified.*/
	protected Date			oModificationDate		= null;

	/** Holds the MediaIndexes for all meetings this node has been in in this view.*/
	protected Vector		vtMediaIndexes			= new Vector();
	
	//// FORMATTING PROPERTIES
	
	/** Whether to display the Tags node indicator.*/
	protected boolean 			bShowTags						= false;
	
	/** Whether to display the detail text node indicator.*/
	protected boolean 			bShowText						= false;
		
	/** Whether to show the parent view (transclusion history) node indicator.*/
	protected boolean 			bShowTrans						= false;
	
	/** Whether to display the map weight node indicator.*/
	protected boolean			bShowWeight						= false;
	
	/** Whether to show small node icons.*/
	protected boolean			bShowSmallIcon					= false;

	/** Whether to hide node icons.*/
	protected boolean			bHideIcon						= false;
	
	/** Indicates the label wrap width for this map.*/
	protected int				nLabelWrapWidth					= -1;
	
	/** Indicates the llabel fon size for this map.*/
	protected int				nFontSize						=-1;
	
	/** Indicates the label font face for nodes.*/
	protected String			sFontFace						="";
	
	/** Indicates the label font style for nodes.*/
	protected int				nFontStyle						=-1;

	/** Indicates the label font style for nodes.*/
	protected int				nForeground						=0;

	/** Indicates the label font style for nodes.*/
	protected int				nBackground						=-1;

	/**
	 * Constructor, creates a new position node,
	 * defining the position of the given node in the given view.
	 *
	 * @param View oView, The view in which the node is placed
	 * @param NodeSummary oNode, The node for which the position is defined
	 * @param int x, The X coordinate of the node's position
	 * @param int y, The Y coordinate of the node's position
	 * @param Date dCreated, the date this object was created.
	 * @param Date dModified, the date this object was last modified.
	 
	 */
	public NodePosition(View oView, NodeSummary oNode, int x, int y, Date dCreated, Date dModified) {
		this.oView = oView;
		oNodeSummary = oNode;
		nX = x;
		nY = y;
		oCreationDate = dCreated;
		oModificationDate = dModified;
	}

	/**
	 * Constructor, creates a new position node,
	 * defining the position of the given node in the given view.
	 *
	 * @param View oView, The view in which the node is placed
	 * @param NodeSummary oNode, The node for which the position is defined
	 * @param int x, The X coordinate of the node's position
	 * @param int y, The Y coordinate of the node's position
	 * @param Date dCreated, the date this object was created.
	 * @param Date dModified, the date this object was last modified.
	 
	 */
	public NodePosition(View oView, NodeSummary oNode, int x, int y, Date dCreated, Date dModified,
			boolean bShowTags, boolean bShowText, boolean bShowTrans, boolean bShowWeight, 
			boolean bShowSmallIcon, boolean bHideIcon, int nLabelWrapWidth, 
			int nFontSize, String sFontFace, int nFontStyle, int nForeground, int nBackground) {
		this.oView = oView;
		this.oNodeSummary = oNode;
		this.nX = x;
		this.nY = y;
		this.oCreationDate = dCreated;
		this.oModificationDate = dModified;
		this.bShowTags = bShowTags;
		this.bShowText = bShowText;
		this.bShowTrans = bShowTrans;
		this.bShowWeight = bShowWeight;		
		this.bShowSmallIcon = bShowSmallIcon;
		this.bHideIcon = bHideIcon;
		this.nLabelWrapWidth = nLabelWrapWidth;
		this.nFontSize = nFontSize;
		this.sFontFace = sFontFace;
		this.nFontStyle = nFontStyle;
		this.nForeground = nForeground;
		this.nBackground = nBackground;
	}
	
	/**
	 * The initialize method is called by the Model before adding the object to the cache.
	 *
	 * @param PCSession session, the session associated with this object.
	 * @param IMode model, the model this object belongs to.
	 */
	public void initialize(PCSession session, IModel model) {
		super.initialize(session, model);

		// Load and initialize the related MediaIndex objects.
		try {
			loadMediaIndexes();
			if (vtMediaIndexes.size() > 0) {
				int count = vtMediaIndexes.size();
				for (int i=0; i<count; i++) {
					MediaIndex index = (MediaIndex) vtMediaIndexes.elementAt(i);
					index.initialize(session, model);
				}
			}
			
			if (sFontFace.equals("")) {
				sFontFace = ((Model)model).fontface;
			}
			if (nFontStyle == -1) {
				nFontStyle = ((Model)model).fontstyle;
			}
			if (nFontSize == -1) {
				nFontSize = ((Model)model).fontsize;
			}
			if (nLabelWrapWidth == -1) {
				nLabelWrapWidth = ((Model)model).labelWrapWidth;
			}
		}
		catch(Exception ex) {
			System.out.println("Unable to load media indexes for node "+oNodeSummary.getId()+" : "+ex.getMessage());
		}
	}

	/**
	 *	This method needs to be called on this object before the Model removes it from the cache.
	 */
	public void cleanUp() {
		super.cleanUp() ;
	}

	/**
	 *  Load all the MediaIndexes referenced by this node, from the DATABASE.
	 *
	 *	@exception java.sql.SQLException
	 *	@exception java.sql.ModelSessionException
	 */
	public void loadMediaIndexes() throws SQLException, ModelSessionException {
		if (oModel == null)
			throw new ModelSessionException("Model is null in NodePosition.loadMediaIndexes");
		if (oSession == null) {
			oSession = oModel.getSession();
			if (oSession == null)
				throw new ModelSessionException("Session is null in NodePosition.loadMediaIndexes");
		}

		IMeetingService ms = oModel.getMeetingService() ;
		vtMediaIndexes = ms.getMediaIndexes(oSession, oView.getId(), oNodeSummary.getId());
	}

	/**
	 * Return the media index for the given meeting.
	 *
	 * @param sMeetingID, the meeting to get the MediaIndex for.
	 * @return MediaIndex, for the given meeting
	 */
	public MediaIndex getMediaIndex(String sMeetingID) {

		MediaIndex match = null;
		MediaIndex ind = null;
		int count = vtMediaIndexes.size();
		for (int i=0; i<count; i++) {
			ind = (MediaIndex)vtMediaIndexes.elementAt(i);
			if (ind.getMeetingID().equals(sMeetingID)) {
				match = ind;
				break;
			}
		}

		return match;
	}

	/**
	 * Set the media index for the given meeting.
	 *
	 * @param sMeetingID, the meeting to set the MediaIndex for.
	 * @param MediaIndex, for the given meeting
	 */
	public void setMediaIndex(String sMeetingID, MediaIndex oMediaIndex) {

		int count = vtMediaIndexes.size();
		boolean bFound = false;
		MediaIndex ind = null;

		for (int i=0; i<count; i++) {
			ind = (MediaIndex)vtMediaIndexes.elementAt(i);
			if (ind.getMeetingID().equals(sMeetingID)) {
				// Try to Update?
				try {
					ind.setMediaIndex(oMediaIndex.getMediaIndex());
				}
				catch(Exception ex) {}
				bFound = true;
			}
		}

		if (!bFound) {
			vtMediaIndexes.addElement(oMediaIndex);
		}
	}

	/**
	 * Returns all the medai indexes referenced by this node, in this view.
	 *
	 * @return Vector, of all the MediaIndex objects referenced by this node, in this view
	 */
	public Vector getMediaIndexes() {
		return vtMediaIndexes;
	}

	/**
	 * Return a new NodePosition object with the properties of this node.
	 */
	public NodePosition getClone() {
		return new NodePosition(oView, oNodeSummary, nX, nY, oCreationDate, oModificationDate, 
				bShowTags, bShowText, bShowWeight, bShowTrans,
				bShowSmallIcon, bHideIcon, nLabelWrapWidth, 
				nFontSize, sFontFace, nFontStyle, nForeground, nBackground);
	}

	/**
	 * Returns the node for which this object defines its position
	 *
	 * @return NodeSummary, the node summary for which the this position is defined.
	 */
	public NodeSummary getNode() {
		return oNodeSummary ;
	}

	/**
	 * Sets the node for which this object defines its position, in the local data ONLY.
	 *
	 * @param NodeSummary oNode, the node for which this object defines its position
	 */
	public void setNode(NodeSummary oNode) {
		oNodeSummary = oNode;
	}

	/**
	 * Returns the view in which the node is placed at the defined position
	 *
	 * @return View, the view in which the node is placed at the defined position
	 */
	public View getView() {
		return oView ;
	}

	/**
	 * Set the view in which the node is placed at the defined position.
	 * @param View oView, the view in which this node is placed.
	 */
	public void setView(View oView) {
		this.oView = oView;
	}

	/**
	 * Returns the X coordinate of the nodes position in the defined view.
	 *
	 * @return int, the X coordinate of the nodes position.
	 */
	public int getXPos() {
		 return nX ;
	}

	/**
	 * Sets the X coordinate of the nodes position in the defined view, in the local data ONLY.
	 * and fires a PropertyChangeEvent.
	 *
	 * @param int x, the X coordinate of the nodes position.
	 */
	public void setXPos(int x) {
		Point oldPoint = new Point(nX, nY);
		nX = x;
		firePropertyChange(POSITION_PROPERTY, oldPoint, new Point(nX, nY));
	}

	/**
	 * Returns the Y coordinate of the nodes position in the defined view.
	 *
	 * @return int, the Y coordinate of the nodes position.
	 */
	public int getYPos() {
		return nY ;
	}

	/**
	 * Sets the Y coordinate of the nodes position in the defined view, in the local data ONLY.
	 * and fires a PropertyChangeEvent.
	 *
	 * @param int y, the Y coordinate of the nodes position.
	 */
	public void setYPos(int y) {
		Point oldPoint = new Point(nX, nY);
		nY = y;
		firePropertyChange(POSITION_PROPERTY, oldPoint, new Point(nX, nY));
	}

	/**
	 * Returns the nodes position in the defined view.
	 *
	 * @return Point, a point object representing the node's position.
	 */
	public Point getPos() {
		return new Point(nX,nY);
	}

	/**
	 * Sets the nodes position in the defined view, in the local data ONLY.
	 * and fires a PropertyChangeEvent.
	 *
	 * @param int x, the X coordinate of the node's position.
	 * @param int y, the Y coordinate of the node's position.
	 */
	public void setPos(int x, int y) {

		Point oldPoint = new Point(nX, nY);
		nX = x;
		nY = y;
		firePropertyChange(POSITION_PROPERTY, oldPoint, new Point(x, y));
	}

	/**
	 * Sets the nodes position in the defined view, in the local data ONLY.
	 *
	 * @param Point oPoint, The node's position.
	 */
	public void setPos(Point oPoint) {
		Point oldPoint = new Point(nX, nY);

		nX = oPoint.x;
		nY = oPoint.y;

		firePropertyChange(POSITION_PROPERTY, oldPoint, oPoint);
	}

	/**
	 *	Sets the date when this node was created, in the local data ONLY.
	 *
	 *	@param Date date, the creation date of this object.
	 */
	public void setCreationDate(Date date) {
		oCreationDate = date ;
	}

	/**
	 *	Returns the creation date of this object.
	 *
	 *	@return Date, the date when this object was created.
	 */
	public Date getCreationDate() {
		return oCreationDate;
	}

	/**
	 * Sets the ModificationDate date of this object, in the local data ONLY.
	 *
	 * @param Date date, the date this object was last modified.
	 */
	public void setModificationDate(Date date) {
		oModificationDate = date;
	}

	/**
	 *	Returns the modification date of this node.
	 *
	 *	@return Date, the date when this node was last modified.
	 */
	public Date getModificationDate() {
		return oModificationDate;
	}
	
	
	/**
	 * Returns whether this View should show the tags node indicators.
	 *
	 * @return a boolean representing whether this View should show the tags node indicators.
	 */
	public boolean getShowTags() {
		return bShowTags;
	}

	/**
	 * Sets the boolean representing whether this View should show the tags node indicators, in the local data ONLY.
	 *
	 * @param bShowTags the boolean representing whether this View should show the tags node indicators.
	 */
	public void setShowTags(boolean bShowTags) {
		boolean bOldShowTags = this.bShowTags;
		this.bShowTags = bShowTags;
		firePropertyChange(TAGS_INDICATOR_PROPERTY, bOldShowTags, bShowTags);							
	}	
	
	/**
	 * Returns whether this View should show the detail text node indicators.
	 *
	 * @return a boolean representing whether this View should show the detail text node indicators.
	 */
	public boolean getShowText() {
		return bShowText;
	}

	/**
	 * Sets the boolean representing whether this View should show the detail text node indicators, in the local data ONLY.
	 *
	 * @param bShowText the boolean representing whether this View should show the detail text node indicators.
	 */
	public void setShowText(boolean bShowText) {
		boolean bOldShowText = this.bShowText;
		this.bShowText = bShowText;
		firePropertyChange(TEXT_INDICATOR_PROPERTY, bOldShowText, bShowText);					
	}	

	/**
	 * Returns whether this View should show the transclusion history node indicators.
	 *
	 * @return a boolean representing whether this View should show the transclusion history node indicators.
	 */
	public boolean getShowTrans() {
		return bShowTrans;
	}

	/**
	 * Sets the boolean representing whether this View should show the transclusion history node indicators, in the local data ONLY.
	 *
	 * @param bShowTrans the boolean representing whether this View should show the transclusion history node indicators.
	 */
	public void setShowTrans(boolean bShowTrans) {
		boolean bOldShowTrans = this.bShowTrans;
		this.bShowTrans = bShowTrans;
		firePropertyChange(TRANS_INDICATOR_PROPERTY, bOldShowTrans, bShowTrans);			
	}	
	
	/**
	 * Returns whether this View should show the map weight node indicators.
	 *
	 * @return a boolean representing whether this View should show the map weight node indicators.
	 */
	public boolean getShowWeight() {
		return bShowWeight;
	}

	/**
	 * Sets the boolean representing whether this View should show the map weight node indicators, in the local data ONLY.
	 *
	 * @param bShowWeight the boolean representing whether this View should show the map weight node indicators.
	 */
	public void setShowWeight(boolean bShowWeight) {
		boolean bOldShowWeight = this.bShowWeight;
		this.bShowWeight = bShowWeight;
		firePropertyChange(WEIGHT_INDICATOR_PROPERTY, bOldShowWeight, bShowWeight);																		
	}	
	
	/**
	 * Returns whether this View should show the small node icon.
	 *
	 * @return a boolean representing whether this View should show the small node icon.
	 */
	public boolean getShowSmallIcon() {
		return bShowSmallIcon;
	}

	/**
	 * Sets the boolean representing whether this View should show the small node icon, in the local data ONLY.
	 *
	 * @param bShowSmallIcon the boolean representing whether this View should show the small node icon.
	 */
	public void setShowSmallIcon(boolean bShowSmallIcon) {
		boolean bOldShowSmallIcon = this.bShowSmallIcon;
		this.bShowSmallIcon = bShowSmallIcon;
		firePropertyChange(SMALL_ICON_PROPERTY, bOldShowSmallIcon, bShowSmallIcon);																
	}	
	/**
	 * Returns whether this View should hide the node icon.
	 *
	 * @return a boolean representing whether this View should hide the node icon.
	 */
	public boolean getHideIcon() {
		return bHideIcon;
	}

	/**
	 * Sets the boolean representing whether this View should hide the node icon, in the local data ONLY.
	 *
	 * @param bHideIcon the boolean representing whether this View should hide the node icon.
	 */
	public void setHideIcon(boolean bHideIcon) {
		boolean bOldHideIcon = this.bHideIcon;
		this.bHideIcon = bHideIcon;
		firePropertyChange(HIDE_ICON_PROPERTY, bOldHideIcon, bHideIcon);														
	}	
	
	/**
	 * Returns the label wrap width of this View
	 *
	 * @return the int of the label wrap width of this View
	 */
	public int getLabelWrapWidth() {
		return nLabelWrapWidth;
	}

	/**
	 * Sets the label wrap width of this View, in the local data ONLY.
	 *
	 * @param nWidth the label wrap width of this View
	 */
	public void setLabelWrapWidth(int nWidth) {
		int oldWrapWidth = this.nLabelWrapWidth;
		this.nLabelWrapWidth = nWidth;
		firePropertyChange(WRAP_WIDTH_PROPERTY, oldWrapWidth, nWidth);												
	}
	/**
	 * Returns the font size for this View
	 *
	 * @return the int of the font size for this View
	 */
	public int getFontSize() {
		return nFontSize;
	}

	/**
	 * Sets the font size for this View, in the local data ONLY.
	 *
	 * @param nWidth the font size for this View
	 */
	public void setFontSize(int nFontSize) {			
		int oldFontSize = this.nFontSize;
		this.nFontSize = nFontSize;
		firePropertyChange(FONTSIZE_PROPERTY, oldFontSize, nFontSize);										
	}
	
	/**
	 * Returns the the font face for node labels in this View
	 *
	 * @return String the the font face for node labels in this View
	 */
	public String getFontFace() {
		return sFontFace;
	}

	/**
	 * Sets the the font face for node labels in this View, in the local data ONLY.
	 *
	 * @param sFontFace the font face for node labels in this View
	 */
	public void setFontFace(String sFontFace) {
		String oldFontFace = this.sFontFace;
		this.sFontFace = sFontFace;
		firePropertyChange(FONTFACE_PROPERTY, oldFontFace, sFontFace);								
	}	
	
	/**
	 * Returns the font style for this View
	 *
	 * @return int font style for this View
	 */
	public int getFontStyle() {
		return nFontStyle;
	}

	/**
	 * Sets the font style for this View, in the local data ONLY.
	 *
	 * @param nStyle the font style for this View
	 */
	public void setFontStyle(int nStyle) {
		int oldStyle = this.nFontStyle;
		this.nFontStyle = nStyle;
		firePropertyChange(FONTSTYLE_PROPERTY, oldStyle, nStyle);						
	}	
	
	/**
	 * Returns the text foreground for this Node in this View
	 *
	 * @return int text foreground for this node in this view
	 */
	public int getForeground() {
		return this.nForeground;
	}

	/**
	 * Sets the text foreground for this Node in this View, in the local data ONLY.
	 *
	 * @param nForeground the text foreground for this Node in this View
	 */
	public void setForeground(int nFore) {
		int oldForeground = this.nForeground;
		this.nForeground = nFore;
		firePropertyChange(TEXT_FOREGROUND_PROPERTY, oldForeground, nFore);				
	}	
	
	/**
	 * Returns the text background for this Node in this View
	 *
	 * @return int text background for this node in this view
	 */
	public int getBackground() {
		return this.nBackground;
	}

	/**
	 * Sets the text background for this Node in this View, in the local data ONLY.
	 *
	 * @param nBackground the text background for this Node in this View
	 */
	public void setBackground(int nBackground) {	
		int oldBackground = this.nBackground;
		this.nBackground = nBackground;
		firePropertyChange(TEXT_BACKGROUND_PROPERTY, oldBackground, nBackground);		
	}			
}
