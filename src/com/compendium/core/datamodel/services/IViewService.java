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

package com.compendium.core.datamodel.services;

import java.util.*;
import java.util.Date;
import java.awt.*;
import java.sql.*;

import com.compendium.core.datamodel.*;
import com.compendium.core.db.*;

/**
 *	The interface for the ViewService class
 *	The ViewService class provides services to manipuate view objects in the database.
 *
 *	@author Sajid and Rema / Michelle Bachler
 */
public interface IViewService extends IService {

	/**
	 * Adds a node to this view at the given x and y coordinate without formatting data
	 * Be warned: This node will not display correctly in the Compendium UI
	 *
	 * @param session the PCSession instance for the current database Model.
	 * @param view, the View instance to add the node to.
	 * @param node the NodeSummary instance to add
	 * @param x, The X coordinate of the node in the view
	 * @param y, The Y coordinate of the node in the view
	 * @param creation, the creation date for this nodeview record.
	 * @param modification, the last modified date for this viewnode record.
	 * @return com.compendium.core.datamodel.NodePosition, the nodeposition if the node was successfully added, null otherwise
	 * @exception java.sql.SQLException
	 * @see com.compendium.core.datamodel.INodePosition
	 */
	public NodePosition addMemberNode(PCSession session, View view, NodeSummary node, int x, int y,
						java.util.Date creation, java.util.Date modification) throws SQLException;
	
	/**
	 * Adds a node to this view at the given x and y coordinate
	 *
	 * @param session com.compendium.core.datamodel.PCSession the session object for the current database Model.
	 * @param view com.compendium.core.datamodel.View the view  to add the node to.
	 * @param node com.compendium.core.datamodel.NodeSummary the node to add
	 * @param x The X coordinate of the node in the view
	 * @param y The Y coordinate of the node in the view
	 * @param creation the creation date for this nodeview record.
	 * @param modification the last modified date for this viewnode record.
	 * @param bShowTags true if this node has the tags indicator draw.
	 * @param bShowText true if this node has the text indicator drawn
	 * @param bShowTrans true if this node has the transclusion indicator drawn
	 * @param bShowWeight true if this node has the weight indicator displayed
	 * @param bSmallIcon true if this node is using a small icon
	 * @param bHideIcons true if this node is not displaying its icon
	 * @param nWrapWidth the node label wrap width used for this node in this view.
	 * @param nFontSize	the font size used for this node in this view
	 * @param sFontFace the font face used for this node in this view
	 * @param nFontStyle the font style used for this node in this view
	 * @param nForeground the foreground color used for this node in this view
	 * @param nBackground the background color used for this node in this view.
	 * @return com.compendium.core.datamodel.NodePosition the nodeposition if the node was successfully added, null otherwise
	 * @exception java.sql.SQLException
	 * @see com.compendium.core.datamodel.INodePosition
	 */
	public NodePosition addMemberNode(PCSession session, View view, NodeSummary node, int x, int y,
						java.util.Date creation, java.util.Date modification, boolean bShowTags,
						boolean bShowText, boolean bShowTrans, boolean bShowWeight, boolean bSmallIcon,
						boolean bHideIcon, int nWrapWidth, int nFontSize, String sFontFace,
						int nFontStyle, int nForeground, int nBackground) throws SQLException;
	
	/**
	 *  Update the formatting properties for the given node in all its views.
	 *
	 *  @param session com.compendium.core.datamodel.PCSession the session object for the current database Model.
	 *  @param sNodeID the node id of the node to update the formatting in all view for.
	 *  @param modification the last modified date for this viewnode record.
	 * 	@param bShowTags true if this node has the tags indicator draw.
	 *	@param bShowText true if this node has the text indicator drawn
	 * 	@param bShowTrans true if this node has the transclusion indicator drawn
	 * 	@param bShowWeight true if this node has the weight indicator displayed
	 * 	@param bSmallIcon true if this node is using a small icon
	 * 	@param bHideIcons true if this node is not displaying its icon
	 * 	@param nWrapWidth the node label wrap width used for this node in this view.
	 * 	@param nFontSize	the font size used for this node in this view
	 * 	@param sFontFace the font face used for this node in this view
	 * 	@param nFontStyle the font style used for this node in this view
	 * 	@param nForeground the foreground color used for this node in this view
	 * 	@param nBackground the background color used for this node in this view.
	 *	@return boolean, true if it was successful, else false.
	 *	@throws java.sql.SQLException
	 */
	public boolean updateTransclusionFormatting(PCSession session, String sNodeID, 
						java.util.Date modification, boolean bShowTags,	boolean bShowText, 
						boolean bShowTrans, boolean bShowWeight, boolean bSmallIcon,
						boolean bHideIcon, int nWrapWidth, int nFontSize, String sFontFace,
						int nFontStyle, int nForeground, int nBackground) throws SQLException;
	
	/**
	 *  Update the formatting properties for the given node in the given view.
	 *
	 *  @param session com.compendium.core.datamodel.PCSession the session object for the current database Model.
	 *  @param sViewID the view of the node whose formatting properties to update.
	 *  @param sNodeID the node whose formatting properties to update.
	 *  @param modification the last modified date for this viewnode record.
	 * 	@param bShowTags true if this node has the tags indicator draw.
	 *	@param bShowText true if this node has the text indicator drawn
	 * 	@param bShowTrans true if this node has the transclusion indicator drawn
	 * 	@param bShowWeight true if this node has the weight indicator displayed
	 * 	@param bSmallIcon true if this node is using a small icon
	 * 	@param bHideIcons true if this node is not displaying its icon
	 * 	@param nWrapWidth the node label wrap width used for this node in this view.
	 * 	@param nFontSize	the font size used for this node in this view
	 * 	@param sFontFace the font face used for this node in this view
	 * 	@param nFontStyle the font style used for this node in this view
	 * 	@param nForeground the foreground color used for this node in this view
	 * 	@param nBackground the background color used for this node in this view.
	 *	@return boolean, true if it was successful, else false.
	 *	@throws java.sql.SQLException
	 */
	public boolean updateFormatting(PCSession session, String sViewID, String sNodeID, 
						java.util.Date modification, boolean bShowTags,	boolean bShowText, 
						boolean bShowTrans, boolean bShowWeight, boolean bSmallIcon,
						boolean bHideIcon, int nWrapWidth, int nFontSize, String sFontFace,
						int nFontStyle, int nForeground, int nBackground) throws SQLException;
	
	
	/**
	 *<p>
	 * Marks for deletion the node with the given id from this view with the given view id.
	 *
	 * @param session com.compendium.core.datamodel.PCSession, the session object for the current database Model.
	 * @param sViewID the id of the view to remove the node from.
	 * @param sNodeID the id of the node to be removed from this view
	 * @return if deletion was successful
	 * @exception java.util.NoSuchElementException
	 * @exception java.sql.SQLException
	 */
	public boolean removeMemberNode(PCSession session, String sViewID, String sNodeID) throws SQLException;

	/**
	 *<p>
	 * Purges from the database the record of the node with the given id from this view with the given view id.
	 *
	 * @param session com.compendium.core.datamodel.PCSession, the session object for the current database Model.
	 * @param sViewID the id of the view to remove the node from.
	 * @param sNodeID the id of the node to be removed from this view
	 * @return if purging was successful
	 * @exception java.util.NoSuchElementException
	 * @exception java.sql.SQLException
	 */
	public boolean purgeMemberNode(PCSession session, String sViewID, String sNodeID) throws SQLException;

	/**
	 * Returns the nodeposition with the given id
	 *
	 * @param session com.compendium.core.datamodel.PCSession, the session object for the current database Model.
	 * @param sViewID, the id of the view the node is in.
	 * @param sNodeID The id of the node to get the position for.
	 * @return com.compendium.core.datamodel.NodePosition, the nodeposition if it was found, else null.
	 * @exception java.util.NoSuchElementException
	 * @exception java.sql.SQLException
	 * @see com.compendium.core.datamodel.NodePosition
	 */
	public NodePosition getNodePosition(PCSession session, String sViewID, String sNodeID) throws NoSuchElementException , SQLException;

	/**
	 * Returns all the nodepositions in this view
	 *
	 * @param session com.compendium.core.datamodel.PCSession, the session object for the current database Model.
	 * @param sViewID, the id of the view to get the node position objects for.
	 * @return java.util.Vector, a Vector of all the nodepositions in the given view.
	 * @exception java.sql.SQLException
	 * @see com.compendium.core.datamodel.NodePosition
	 */
	public Vector getNodePositions(PCSession session, String sViewID) throws SQLException;

	/**
	 * Add a new time point for the given node in the given view
	 * @param sNodeID the id of the node to add the time record for
	 * @param nShow the time (in milliseconds) to show the node
	 * @param nHide the time (in milliseconds) to hide the node
	 * @param x The X coordinate of the node in the view
	 * @param y The Y coordinate of the node in the view
	 * @return the NodePositionTime object if the node was successfully added, otherwise null.
	 */
	public NodePositionTime addNodeTime(PCSession session, String sViewTimeNodeID, 
			View view, NodeSummary node, long nShow, long nHide, int x, int y) throws SQLException;

	/**
	 * Update a time point data for the given id
	 * @param sViewTimeNodeID the id of the time point to update
	 * @param sNodeID the id of the node to update the time record for
	 * @param nShow the time (in milliseconds) to show the node
	 * @param nHide the time (in milliseconds) to hide the node
	 * @param x The X coordinate of the node in the view
	 * @param y The Y coordinate of the node in the view
	 * @return the NodePositionTime object if the node was successfully update, otherwise null.
	 */
	public NodePositionTime updateNodeTime(PCSession session, String sViewTimeNodeID, 
			View view, NodeSummary node, long nShow, long nHide, int x, int y) throws SQLException;	
	
	/**
	 * Add a new time point for the given node in the given view
	 * @param sViewTimeNodeID the id of the time point to delete
	 * @param sNodeID the id of the node to delete the time record for
	 */
	public void deleteNodeTime(PCSession session, String sViewTimeNodeID) throws SQLException;
	
	/**
	 * Returns all the NodePositionTimess in this view
	 *
	 * @param session com.compendium.core.datamodel.PCSession, the session object for the current database Model.
	 * @param sViewID, the id of the view to get the node position objects for.
	 * @return java.util.Vector, a Vector of all the NodePositionTimess in the given view.
	 * @exception java.sql.SQLException
	 * @see com.compendium.core.datamodel.NodePosition
	 */
	public Vector getNodeTimes(PCSession session, String sViewID) throws SQLException;

	/**
	 * Returns all the nodepositionsummary objects in this view
	 *
	 * @param session com.compendium.core.datamodel.PCSession, the session object for the current database Model.
	 * @param sViewID, the id of the view to get the node position objects for.
	 * @return java.util.Vector, a Vector of all the nodepositions in the given view.
	 * @exception java.sql.SQLException
	 * @see com.compendium.core.datamodel.NodePosition
	 */
	public Vector getNodePositionsSummary(PCSession session, String sViewID) throws SQLException;

	/**
	 * Returns nodepositions count for this view
	 *
	 * @param session com.compendium.core.datamodel.PCSession, the session object for the current database Model.
	 * @param sViewID, the id of the view to get the node position count for.
	 * @return int, a int of the nodeposition count for the given view.
	 * @exception java.sql.SQLException
	 * @see com.compendium.core.datamodel.NodePosition
	 */
	public int getNodeCount(PCSession session, String sViewID) throws SQLException;

	/**
	 * Sets the position of the node with the given id in this view
	 *
	 * @param session com.compendium.core.datamodel.PCSession, the session object for the current database Model.
	 * @param sViewID, the id of the view to add the node position to.
	 * @param sNodeID, the id of the node whose position to add to the given view.
	 * @param p, The new position of the node.
	 * @return boolean, true if the node position was successfulyy added, else false.
	 * @exception java.util.NoSuchElementException
	 * @exception java.sql.SQLException
	 */
	public boolean setNodePosition(PCSession session, String sViewID, String sNodeID, Point p) throws NoSuchElementException , SQLException;

	/**
	 * Adds a link to the View with the given view id.
	 *
	 * @param session com.compendium.core.datamodel.PCSession, the session object for the current database Model.
	 * @param sViewID, the id of the view to add the link to.
	 * @param sLinkID, the linkid of the link to be added to the view.
	 * @param nLabelWrapWidth The wrap width for the link label
	 * @param nArrowType The arrow head type to use
	 * @param nLinkStyle The style of the link, straight, square, curved
	 * @param LinkDashed The style of the line fill, plain, dashed etc.
	 * @param LinkWeight The thickness of the line
	 * @param LinkColour The colour of the line
	 * @param nFontSize The font size for the link label
	 * @param sFontFace The font face for the link label
	 * @param nFontStyle The font style for the link label
	 * @param nForeground The foreground colour for the link label
	 * @param nBackground The background colour for the link label	 
	 * @return true if the link was successfully added, else false.
	 * @exception java.sql.SQLException
	 * @see com.compendium.core.datamodel.ILink
	 */
	public ILinkProperties addMemberLink(PCSession session, String sViewID, String sLinkID,
			int nLabelWrapWidth, int nArrowType, int nLinkStyle, 
			int nLinkDashed, int nLinkWeight, int nLinkColour,
			int nFontSize, String sFontFace, int nFontStyle, 
			int nForeground, int nBackground) throws SQLException;
	
	/**
	 * Updates the link formatting of the link with the given LinkID, in the view with the given ViewID.
	 *
	 * @param session com.compendium.core.datamodel.PCSession, the session object for the current database Model.
	 * @param sViewID, the id of the view to add the link to.
	 * @param sLinkID, the linkid of the link to be added to the view.
	 * @param nLabelWrapWidth The wrap width for the link label
	 * @param nArrowType The arrow head type to use
	 * @param nLinkStyle The style of the link, straight, square, curved
	 * @param LinkDashed The style of the line fill, plain, dashed etc.
	 * @param LinkWeight The thickness of the line
	 * @param LinkColour The colour of the line
	 * @param nFontSize The font size for the link label
	 * @param sFontFace The font face for the link label
	 * @param nFontStyle The font style for the link label
	 * @param nForeground The foreground colour for the link label
	 * @param nBackground The background colour for the link label	 
	 * @return true if the link was successfully added, else false.
	 * @exception java.sql.SQLException
	 * @see com.compendium.core.datamodel.ILink
	 */
	public ILinkProperties updateLinkFormatting(PCSession session, String sViewID, String sLinkID,
			int nLabelWrapWidth, int nArrowType, int nLinkStyle, 
			int nLinkDashed, int nLinkWeight, int nLinkColour,
			int nFontSize, String sFontFace, int nFontStyle, 
			int nForeground, int nBackground) throws SQLException;

	/**
	 * Restores all links to this view
	 *
	 * @param session com.compendium.core.datamodel.PCSession, the session object for the current database Model.
	 * @param sViewID, the view id of the View whose links to restore.
	 * @return boolean, true if the links were successfully restored, else false.
	 * @exception java.sql.SQLException
	 * @see com.compendium.core.datamodel.ILink
	 */
	public boolean restoreViewLinks(PCSession session, String sViewID) throws SQLException;

	/**
	 * Restores the given link in this view
	 *
	 * @param session com.compendium.core.datamodel.PCSession, the session object for the current database Model.
	 * @param sViewID, the view id of the View to restore the link in.
	 * @param sLinkID, the link id of the Link to restore.
	 * @return boolean, true if the link was successfully restored, else false.
	 * @exception java.sql.SQLException
	 * @see com.compendium.core.datamodel.ILink
	 */
	public boolean restoreLink(PCSession session, String sViewID, String sLinkID) throws SQLException;

	/**
	 * Returns the link with the given id
	 *
	 * @param session com.compendium.core.datamodel.PCSession, the session object for the current database Model.
	 * @param sViewID, the view id of the View the link to return is in.
	 * @param sLinkID, the link id of the Link to return.
	 * @return the LinkProperties if it was found, else null.
	 * @exception java.util.NoSuchElementException
	 * @exception java.sql.SQLException
	 * @see com.compendium.core.datamodel.LinkProperties
	 */
	public LinkProperties getLink(PCSession session,String sViewID, String sLinkID) throws NoSuchElementException, SQLException;

	/**
	 * Returns all the links in the View with the given id.
	 *
	 * @param session com.compendium.core.datamodel.PCSession, the session object for the current database Model.
	 * @param sViewID, the view id of the View whose links to return.
	 * @return java.util.Vector, a vector of all the links in this view.
	 * @exception java.sql.SQLException
	 * @see com.compendium.core.datamodel.LinkProperties
	 */
	public Vector getLinks(PCSession session, String sViewID) throws SQLException;

	/**
	 * Returns all the links ID's in the View with the given id.
	 *
	 * @param session com.compendium.core.datamodel.PCSession, the session object for the current database Model.
	 * @param sViewID, the view id of the View whose links to return.
	 * @return java.util.Vector, a vector of all the links in this view.
	 * @exception java.sql.SQLException
	 * @see com.compendium.core.datamodel.LinkProperties
	 */
	public Vector getLinkIDs(PCSession session, String sViewID) throws SQLException;

	/**
	 * Deletes a link from the ViewLink table for the given view returns true if successful
	 *
	 * @param PCSession session, the session object for the database to use.
	 * @param String sViewID, the view id of the view in which the link to mark for deletion is.
	 * @param String sLinkID, the link id of the link to mark for deletion in the ViewLink table.
	 * @return boolean, true if the link was successfully marked for deletion in the ViewLink, else false.
	 * @exception java.sql.SQLException
	 */
	public boolean deleteLinkFromView(PCSession session, String sViewID, String sLinkID) throws SQLException;
	
	/**
	 * Does the View with the given id contain itself?
	 *
	 * @param session com.compendium.core.datamodel.PCSession, the session object for the current database Model.
	 * @param sViewID, the view id of the View to check.
	 * @return boolean, true if the View with the given id contains itself, else false.
	 * @exception java.sql.SQLException
 	 */
	public boolean isViewContainsItself(PCSession session, String sViewID) throws SQLException;
	
	/**
	 *	Returns TRUE if the given view has been modified since it was last loaded.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param sViewID, the id of the View to check.
	 *	@param sUserName the current users's name
	 *	@param dLastViewModDate - date od last modification by another user that we're aware of
	 *	@return Boolean, TRUE if the view is 'dirty'.
	 *	@throws java.sql.SQLException
	 */
	public Boolean bIsViewDirty(PCSession session, String sViewID, String sUserName, Date dLastViewModDate) throws SQLException;
}
