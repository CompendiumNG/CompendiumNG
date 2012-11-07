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
import java.util.*;
import java.sql.SQLException;

import com.compendium.core.datamodel.services.ILinkService;
import com.compendium.core.datamodel.services.IViewService;

/**
 * The IView is an interface object that represents a collection of nodes and links.
 * The visual representation of the nodes and links depends on the type of the view.
 *
 * @author	Rema Natarajan / Michelle Bachler
 */
public interface IView extends INodeSummary {

	/**
	 * Loads all the nodes and links into this view from the DATABASE.
	 *
	 * @exception java.sql.SQLException
	 * @exception ModelSessionException
	 */
	public void initializeMembers() throws SQLException, ModelSessionException;

	/**
	 * Return the number of nodes in this view.
	 */
	public int getNumberOfNodes();

	/**
	 * Return the number of links in this view.
	 */
	public int getNumberOfLinks();

	/**
	 * Adds a new node with the given properties to this view at
	 * the given x and y coordinate, both locally and in the DATABASE.
	 *
	 * @param int nType, the type of this node.
	 * @param String sXNodeType, the extended node type id of the node - NOT CURRENTLY USED.
	 * @param String sOriginalID, the original id of the node if it was imported.
	 * @param String sAuthor, the author of the node.
	 * @param String sLabel, the label of this node.
	 * @param String sDetail, the first page of detail for this node.
	 * @param x, The X coordinate of the node in the view
	 * @param y, The Y coordinate of the node in the view
	 * @return the NodePosition object if the node was successfully added, otherwise null.
	 * @exception java.sql.SQLException
	 * @exception ModelSessionException
	 */
	public NodePosition addMemberNode(  int nType,
										String sXNodeType,
										String sOriginalID,
										String sAuthor,
										String sLabel,
										String sDetail,
										int x,
										int y
										) throws SQLException, ModelSessionException;

	/**
	 * Adds a new node with the given properties to this view at
	 * the given x and y coordinate, both locally and in the DATABASE.
	 *
	 * @param int nType, the type of this node.
	 * @param String sXNodeType, the extended node type id of the node - NOT CURRENTLY USED.
	 * @param String sImportedID, the imported id,
	 * @param String sOriginalID, the original id of the node.
	 * @param String sAuthor, the author of the node.
	 * @param String sLabel, the label of this node.
	 * @param String sDetail, the first page of detail for this node.
	 * @param x, The X coordinate of the node in the view.
	 * @param y, The Y coordinate of the node in the view.
	 * @exception java.sql.SQLException
	 * @exception ModelSessionException
	 */
	public NodePosition addMemberNode(  int nType,
										String sXNodeType,
										String sImportedId,
										String sOriginalID,
										String sAuthor,
										String sLabel,
										String sDetail,
										int x,
										int y
										) throws SQLException, ModelSessionException;

	/**
	 * Adds a new node with the given properties to this view at
	 * the given x and y coordinate, both locally and in the DATABASE.
	 *
	 * @param int nType, the type of this node.
	 * @param String sXNodeType, the extended node type id of the node - NOT CURRENTLY USED.
	 * @param String sImportedID, the imported id,
	 * @param String sOriginalID, the original id of the node.
	 * @param String sAuthor, the author of the node.
	 * @param creationDate, the date the node was created.
	 * @param modDate, the date the node was last modified.
	 * @param String sLabel, the label of this node.
	 * @param String sDetail, the first page of detail for this node.
	 * @param x, The X coordinate of the node in the view.
	 * @param y, The Y coordinate of the node in the view.
	 * @param transCreationDate, the date the node was put into the view.
	 * @param transModDate, the date the view-node data was last modified.
	 * @exception java.sql.SQLException
	 * @exception ModelSessionException
	 */
	public NodePosition addMemberNode(  int nType,
										String sXNodeType,
										String sImportedId,
										String sOriginalID,
										String sAuthor,
										Date creationDate,
										Date modDate,
										String sLabel,
										String sDetail,
										int x,
										int y,
										Date transCreationDate,
										Date transModDate) throws SQLException, ModelSessionException;

	/**
	 * Adds a new node with the given properties to this view at
	 * the given x and y coordinate, both locally and in the DATABASE.
	 *
	 * @param sNodeID, The node id of the node to be added to the view
	 * @param int nType, the type of this node.
	 * @param String sXNodeType, the extended node type id of the node - NOT CURRENTLY USED.
	 * @param String sImportedID, the imported id,
	 * @param String sOriginalID, the original id of the node.
	 * @param String sAuthor, the author of the node.
	 * @param creationDate, the date the node was created.
	 * @param modDate, the date the node was last modified.
	 * @param String sLabel, the label of this node.
	 * @param String sDetail, the first page of detail for this node.
	 * @param x, The X coordinate of the node in the view.
	 * @param y, The Y coordinate of the node in the view.
	 * @param transCreationDate, the date the node was put into the view.
	 * @param transModDate, the date the view-node data was last modified.
	 * @exception java.sql.SQLException
	 * @exception ModelSessionException
	 */
	public NodePosition addMemberNode(  String sNodeID,
										int type,
										String sXNodeType,
										String sImportedId,
										String sOriginalID,
										String sAuthor,
										String sLabel,
										String sDetail,
										int x,
										int y
										) throws SQLException, ModelSessionException;

	/**
	 * Adds a new node with the given properties to this view at
	 * the given x and y coordinate, both locally and in the DATABASE.
	 *
	 * @param sNodeID, The node id of the node to be added to the view
	 * @param int nType, the type of this node.
	 * @param String sXNodeType, the extended node type id of the node - NOT CURRENTLY USED.
	 * @param String sImportedID, the imported id,
	 * @param String sOriginalID, the original id of the node.
	 * @param String sAuthor, the author of the node.
	 * @param creationDate, the date the node was created.
	 * @param modDate, the date the node was last modified.
	 * @param String sLabel, the label of this node.
	 * @param String sDetail, the first page of detail for this node.
	 * @param x, The X coordinate of the node in the view.
	 * @param y, The Y coordinate of the node in the view.
	 * @param transCreationDate, the date the node was put into the view.
	 * @param transModDate, the date the view-node data was last modified.
	 * @exception java.sql.SQLException
	 * @exception ModelSessionException
	 */
	public NodePosition addMemberNode(  String sNodeID,
										int type,
										String sXNodeType,
										String sImportedId,
										String sOriginalID,
										String sAuthor,
										Date creationDate,
										Date modDate,
										String sLabel,
										String sDetail,
										int x,
										int y,
										Date transCreationDate,
										Date transModDate
										) throws SQLException, ModelSessionException;

	/**
	 * Adds a new node with the given properties to this view at
	 * the given x and y coordinate, both locally and in the DATABASE.
	 *
	 * @param nType the type of this node.
	 * @param sXNodeType the extended node type id of the node - NOT CURRENTLY USED.
	 * @param sImportedID the imported id,
	 * @param sOriginalID the original id of the node.
	 * @param sAuthor the author of the node.
	 * @param creationDate the date the node was created.
	 * @param modDate the date the node was last modified.
	 * @param sLabel the label of this node.
	 * @param sDetail the first page of detail for this node.
	 * @param x The X coordinate of the node in the view.
	 * @param y The Y coordinate of the node in the view.
	 * @param transCreationDate the date the node was put into the view.
	 * @param transModDate the date the view-node data was last modified.
	 * @param sLastModAuthor the author name of the current user.
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
	 * 
	 * @exception java.sql.SQLException
	 * @exception ModelSessionException
	 */
	public NodePosition addMemberNode(  int type,
										String sXNodeType,
										String sImportedId,
										String sOriginalID,
										String sAuthor,
										Date creationDate,
										Date modDate,
										String sLabel,
										String sDetail,
										int x,
										int y,
										Date transCreationDate,
										Date transModDate,
										String sLastModAuthor,
										boolean bShowTags, 
										boolean bShowText, 
										boolean bShowTrans, 
										boolean bShowWeight, 
										boolean bSmallIcon, 
										boolean bHideIcon, 
										int 	nWrapWidth, 
										int 	nFontSize, 
										String 	sFontFace, 
										int 	nFontStyle, 
										int 	nForeground, 
										int 	nBackground) throws SQLException, ModelSessionException;
	
	/**
	 * Adds a new node with the given properties to this view at
	 * the given x and y coordinate, both locally and in the DATABASE.
	 *
	 * @param sNodeID The node id of the node to be added to the view
	 * @param nType the type of this node.
	 * @param sXNodeType the extended node type id of the node - NOT CURRENTLY USED.
	 * @param sImportedID the imported id,
	 * @param sOriginalID the original id of the node.
	 * @param sAuthor the author of the node.
	 * @param creationDate the date the node was created.
	 * @param modDate the date the node was last modified.
	 * @param sLabel the label of this node.
	 * @param sDetail the first page of detail for this node.
	 * @param x The X coordinate of the node in the view.
	 * @param y The Y coordinate of the node in the view.
	 * @param transCreationDate the date the node was put into the view.
	 * @param transModDate the date the view-node data was last modified.
	 * @param sLastModAuthor the author name of the current user.
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
	 * 
	 * @exception java.sql.SQLException
	 * @exception ModelSessionException
	 */
	public NodePosition addMemberNode(  String sNodeID,	int type,
										String sXNodeType,
										String sImportedId,
										String sOriginalID,
										String sAuthor,
										Date creationDate,
										Date modDate,
										String sLabel,
										String sDetail,
										int x,
										int y,
										Date transCreationDate,
										Date transModDate,
										String sLastModAuthor,
										boolean bShowTags, 
										boolean bShowText, 
										boolean bShowTrans, 
										boolean bShowWeight, 
										boolean bSmallIcon, 
										boolean bHideIcon, 
										int 	nWrapWidth, 
										int 	nFontSize, 
										String 	sFontFace, 
										int 	nFontStyle, 
										int 	nForeground, 
										int 	nBackground) throws SQLException, ModelSessionException;

	
	/**
	 * Replace a node in this view.
	 *
	 * @param NodePosition oPos, the node to be replaced in the view, in the local data ONLY.
	 * @return NodePosition, the passed object.
	 * @see com.compendium.core.datamodel.INodeSummary
	 */
	public NodePosition replaceMemberNode(NodePosition oPos);

	/**
	 * Adds a node to this view.
	 *
	 * @param NodePosition nodePos, the node to be added to the view, in the local data ONLY.
	 * @return NodePosition, the nodePosition if the node was successfully added.
	 */
	public NodePosition addMemberNode(NodePosition nodePos);

	/**
	 * Adds a node with the given properties to this view at
	 * the given x and y coordinate, both locally and in the DATABASE.
	 *
	 * @param NodeSumary node, The node to be added to the view.
	 * @param int x, The X coordinate of the node in the view.
	 * @param int y, The Y coordinate of the node in the view.
	 * @return NodePosition, the node if the node was successfully added, null otherwise.
	 * @exception java.sql.SQLException
	 * @exception ModelSessionException
	 * @see INodeSummary
	 */
	public NodePosition addNodeToView(NodeSummary node,
										int x,
										int y) throws SQLException, ModelSessionException;

	/**
	 * Removes the given node from this view, both locally and from the DATABASE.
	 *
	 * @param NodeSummary node, The node to be removed from this view.
	 * @return boolean, true if the node was removed, else false.
	 * @exception java.util.NoSuchElementException
	 * @exception java.sql.SQLException
	 * @exception ModelSessionException
	 */
	public boolean removeMemberNode(NodeSummary node) throws NoSuchElementException, SQLException, ModelSessionException;

	/**
	 * Returns all relations between this view and its nodes.
	 * @param Enumeration, a list of all relations between this view and its nodes.
	 */
	public Enumeration getPositions();

	/**
	 * Sets the position of the given node in this view, both locally and in the DATABASE.
	 *
	 * @param String sNodeID, the id of the node to set the new position for.
	 * @param Point oPoint, the new position of the node.
	 * @return boolean, true if database was updated successfully, else false.
	 * @exception java.util.NoSuchElementException
	 * @exception java.sql.SQLException
	 * @exception ModelSessionException
	 */
	public boolean setNodePosition(String sNodeID, Point oPoint) throws NoSuchElementException, SQLException, ModelSessionException;


	/**
	 * Does this view contain this node?
	 *
	 * @param NodeSummary oNode, the node to be checked in the view.
	 * @return boolean, true if this view contains this node, else false.
	 */
	public boolean containsNodeSummary(NodeSummary oNode);

	/**
	 * Does this view contain this node?
	 *
	 * @param NodePosition oPos, the node to be checked in the view.
	 * @return boolean true if this view contains this node, else false.
	 */
	public boolean containsNode(NodePosition oPos);

	/**
	 * Adds a new link with the given properties to this view, both locally and in the DATABASE.
	 *
	 * @param type The link type of the link to be added to the view
	 * @param sOriginalID, The original imported id
	 * @param String author, the author of the link.
	 * @param from The link's originating node
	 * @param to The link's destination node
	 * @param props the LinkProperties to apply to this link.
	 * @return the link if the link was successfully added, null otherwise
	 * @exception java.sql.SQLException
	 * @exception ModelSessionException
	 * @see com.compendium.core.datamodel.Link
	 * @see com.compendium.core.datamodel.INodeSummary
	 */
	public ILinkProperties addMemberLink(String type, String sOriginalID, String author, 
					INodeSummary from, INodeSummary to, LinkProperties props) 
						throws SQLException, ModelSessionException;
	
	/**
	 * Adds a new link with the given properties to this view, both locally and in the DATABASE.
	 *
	 * @param type The link type of the link to be added to the view
	 * @param sOriginalID The original imported id
	 * @param String author the author of the link.
	 * @param from The link's originating node
	 * @param to The link's destination node
	 * @param sLabel the label for this link.
	 * @param props the LinkProperties to apply to this link.
	 * @return the link if the link was successfully added, null otherwise
	 * @exception java.sql.SQLException
	 * @exception ModelSessionException
	 * @see com.compendium.core.datamodel.Link
	 * @see com.compendium.core.datamodel.INodeSummary
	 */
	public ILinkProperties addMemberLink(String type, String sOriginalID, String author, 
					INodeSummary from, INodeSummary to, String sLabel, LinkProperties props) 
						throws SQLException, ModelSessionException;
	
	/**
	 * Adds a new link with the given properties to this view, both locally and in the DATABASE.
	 *
	 * @param type The link type of the link to be added to the view
	 * @param sImportedID the imported id.
	 * @param sOriginalID The original imported id
	 * @param String author the author of the link.
	 * @param from The link's originating node
	 * @param to The link's destination node
	 * @param sLabel the label for this link.
	 * @param props the LinkProperties to apply to this link.
	 * @return the link if the link was successfully added, null otherwise
	 * @exception java.sql.SQLException
	 * @exception ModelSessionException
	 * @see com.compendium.core.datamodel.Link
	 * @see com.compendium.core.datamodel.INodeSummary
	 */
	public ILinkProperties addMemberLink(String type, String sImportedID, String sOriginalID, String author, 
					INodeSummary from, INodeSummary to, String sLabel, LinkProperties props) 
						throws SQLException, ModelSessionException;	
	
	/**
	 * Adds a new link with the given properties to this view, both locally and in the DATABASE.
	 *
	 * @param type The link type of the link to be added to the view
	 * @param sOriginalID The original imported id
	 * @param String author the author of the link.
	 * @param from The link's originating node
	 * @param to The link's destination node
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
	 * @return the link if the link was successfully added, null otherwise
	 * @exception java.sql.SQLException
	 * @exception ModelSessionException
	 * @see com.compendium.core.datamodel.Link
	 * @see com.compendium.core.datamodel.INodeSummary
	 */
	public ILinkProperties addMemberLink(String type, String sOriginalID, String author, INodeSummary from, INodeSummary to,
			int nLabelWrapWidth, int nArrowType, int nLinkStyle, int LinkDashed, int LinkWeight, int LinkColour,
				int nFontSize, String sFontFace, int nFontStyle, int nForeground, int nBackground) 
						throws SQLException, ModelSessionException;
	
	/**
	 * Adds a new link with the given properties to this view, both locally and in the DATABASE.
	 *
	 * @param id, the link id of this link.
	 * @param type The link type of the link to be added to the view.
	 * @param sOriginalID The original imported id.
	 * @param String author the author of the link.
	 * @param from The link's originating node.
	 * @param to The link's destination node.
	 * @param nLabelWrapWidth The wrap width for the link label
	 * @param nArrowType The arrow head type to use
	 * @param nLinkStyle The style of the link, straight, square, curved
	 * @param nLinkDashed The style of the line fill, plain, dashed etc.
	 * @param nLinkWeight The thickness of the line
	 * @param nLinkColour The colour of the line
	 * @param nFontSize The font size for the link label
	 * @param sFontFace The font face for the link label
	 * @param nFontStyle The font style for the link label
	 * @param nForeground The foreground colour for the link label
	 * @param nBackground The background colour for the link label	 
	 * @return the link if the link was successfully added, null otherwise
	 * @exception java.sql.SQLException
	 * @exception ModelSessionException
	 * @see com.compendium.core.datamodel.Link
	 * @see com.compendium.core.datamodel.INodeSummary
	 */
	public ILinkProperties addMemberLink(String linkId, String type, String sImportedID, String sOriginalID, String author,
									INodeSummary from, INodeSummary to,
									int nLabelWrapWidth, int nArrowType, int nLinkStyle, int nLinkDashed, int nLinkWeight, int nLinkColour,
									int nFontSize, String sFontFace, int nFontStyle, int nForeground, int nBackground) 
										throws SQLException, ModelSessionException;
	
	/**
	 * Adds a new link with the given properties to this view, both locally and in the DATABASE.
	 *
	 * @param type The link type of the link to be added to the view
	 * @param sOriginalID, The original imported id
	 * @param String author, the author of the link.
	 * @param from The link's originating node
	 * @param to The link's destination node
	 * @param sLabel, the label for this link.
	 * @param nLabelWrapWidth The wrap width for the link label
	 * @param nArrowType The arrow head type to use
	 * @param nLinkStyle The style of the link, straight, square, curved
	 * @param nLinkDashed The style of the line fill, plain, dashed etc.
	 * @param nLinkWeight The thickness of the line
	 * @param nLinkColour The colour of the line
	 * @param nFontSize The font size for the link label
	 * @param sFontFace The font face for the link label
	 * @param nFontStyle The font style for the link label
	 * @param nForeground The foreground colour for the link label
	 * @param nBackground The background colour for the link label	 
	 * @return the link if the link was successfully added, null otherwise
	 * @exception java.sql.SQLException
	 * @exception ModelSessionException
	 * @see com.compendium.core.datamodel.Link
	 * @see com.compendium.core.datamodel.INodeSummary
	 */
	public ILinkProperties addMemberLink(String type, String sOriginalID, String author, 
				INodeSummary from, INodeSummary to, String sLabel,
				int nLabelWrapWidth, int nArrowType, int nLinkStyle, int nLinkDashed, int nLinkWeight, int nLinkColour,
				int nFontSize, String sFontFace, int nFontStyle, int nForeground, int nBackground) 
						throws SQLException, ModelSessionException;
	
	/**
	 * Adds a new link with the given properties to this view, both locally and in the DATABASE.
	 *
	 * @param type The link type of the link to be added to the view.
	 * @param sImportedID, the imported id.
	 * @param sOriginalID, The original imported id
	 * @param String author, the author of the link.
	 * @param from The link's originating node.
	 * @param to The link's destination node.
	 * @param sLabel, the label for this link.
	 * @param nLabelWrapWidth The wrap width for the link label
	 * @param nArrowType The arrow head type to use
	 * @param nLinkStyle The style of the link, straight, square, curved
	 * @param nLinkDashed The style of the line fill, plain, dashed etc.
	 * @param nLinkWeight The thickness of the line
	 * @param nLinkColour The colour of the line
	 * @param nFontSize The font size for the link label
	 * @param sFontFace The font face for the link label
	 * @param nFontStyle The font style for the link label
	 * @param nForeground The foreground colour for the link label
	 * @param nBackground The background colour for the link label	 
	 * @return the link if the link was successfully added, null otherwise
	 * @exception java.sql.SQLException
	 * @exception ModelSessionException
	 * @see com.compendium.core.datamodel.Link
	 * @see com.compendium.core.datamodel.INodeSummary
	 */
	public ILinkProperties addMemberLink(String type, String sImportedID, String sOriginalID, String author, INodeSummary from, INodeSummary to, String sLabel,
			int nLabelWrapWidth, int nArrowType, int nLinkStyle, int nLinkDashed, int nLinkWeight, int nLinkColour,
			int nFontSize, String sFontFace, int nFontStyle, int nForeground, int nBackground) 
						throws SQLException, ModelSessionException;
	
	/**
	 * Adds a new link with the given properties to this view, both locally and in the DATABASE.
	 *
	 * @param id, the link id of this link.
	 * @param type The link type of the link to be added to the view.
	 * @param String sImportedID, the imported id,
	 * @param sOriginalID, The original imported id.
	 * @param String author, the author of the link.
	 * @param from The link's originating node.
	 * @param to The link's destination node.
	 * @param sLabel, the label for this link.
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
	 * @return the link if the link was successfully added, null otherwise
	 * @exception java.sql.SQLException
	 * @exception ModelSessionException
	 * @see com.compendium.core.datamodel.Link
	 * @see com.compendium.core.datamodel.INodeSummary
	 */
	public ILinkProperties addMemberLink(String linkId, String type, String sImportedID, String sOriginalID, String author,
									INodeSummary from, INodeSummary to, String sLabel,
									int nLabelWrapWidth, int nArrowType, int nLinkStyle,
									int nLinkDashed, int nLinkWeight, int nLinkColour,
									int nFontSize, String sFontFace, int nFontStyle, int nForeground, int nBackground) 
										throws SQLException, ModelSessionException;
	/**
	 * Adds a link to this view, in the local data ONLY.
	 *
	 * @param link the link to be added to the view.
	 * @return LinkProperties the link if the link was successfully added, null otherwise.
	 * @see ILink
	 */
	public LinkProperties addMemberLink(LinkProperties link);

	/**
	 * Removes a link from this view, both locally and from the DATABASE.
	 *
	 * @param linkprops the link to be removed from this view.
	 * @return boolean, true if the link was successfully removed, else false.
	 * @exception java.util.NoSuchElementException
	 * @exception java.sql.SQLException
	 * @exception ModelSessionException
	 * @see ILink
	 */
	public boolean removeMemberLink(LinkProperties linkprops) throws NoSuchElementException, SQLException, ModelSessionException;

	/**
	 * Purges a link from this view, both locally and in the DATABASE, (permenantly removes the record from the database).
	 *
	 * @param linkprops the link to be removed from this view.
	 * @return boolean, true if the purge was successful, else false.
	 * @exception java.util.NoSuchElementException
	 * @exception java.sql.SQLException
	 * @exception ModelSessionException
	 * @see ILink
	 */
	public boolean purgeMemberLink(LinkProperties linkprops) throws NoSuchElementException, SQLException, ModelSessionException;
	
	/**
	 * Purges all links from this view, both locally and in the DATABASE, (permenantly removes the records from the database).
	 *
	 * @return boolean, true if the purge was successful, else false.
	 * @exception java.util.NoSuchElementException
	 * @exception java.sql.SQLException
	 * @exception ModelSessionException
	 * @see ILink
	 */
	public boolean purgeAllLinks() throws NoSuchElementException, SQLException, ModelSessionException;

	/**
	 * Returns all the links in this view.
	 *
	 * @return Enumeration, an array of all the links in this view.
	 * @see ILink
	 */
	public Enumeration getLinks();
	
	/**
	 * Updates the LastModifiedByOther property.  This is used by XML import to prevent the user from being
	 * prompted to refresh the view.
	 * 
	 * @param NodeSumary node, The node being added to the view.
	 */
	public void updateLastModifiedByOther(NodeSummary node);
}
