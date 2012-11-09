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

package com.compendium.core.db.management;

import java.awt.Color;

/*
 * This interface defines the global constants for the database management classes.
 * These are all String objects representing various SQL statements shared by various management classes.
 *
 * @author Michelle Bachler
 */
public interface DBConstants extends java.io.Serializable {

	// KEEP FOR REFERENCE - Insert into viewlink (ViewID, LinkID) Select Link.ViewID, Link.LinkID from Link

// STATEMENTS TO GET ALL DATA OUT OF THE DATABASE

	/** The SQL statement to select specific data from the User table of an Access database */
	public final static String GET_USER_QUERY_ACCESS	= "SELECT UserID, Author, CreationDate, ModificationDate, Login, Name, Password, Description, HomeView, IsAdministrator FROM User";

	/** The SQL statement to select all the data from the User table */
	public final static String GET_USER_QUERY			= "SELECT * FROM Users";

	/** The SQL statement to select all the data from the System table */
	public final static String GET_SYSTEM_QUERY		 	= "SELECT * FROM System";

	/** The SQL statement to select all the data from the Node table */
	public final static String GET_NODE_QUERY 			= "SELECT * FROM Node";

	/** The SQL statement to select all the data from the Link table */
	public final static String GET_LINK_QUERY			= "SELECT LinkID, Author, CreationDate,	ModificationDate, LinkType, "+
															"OriginalID, FromNode, ToNode, Label, CurrentStatus FROM Link";

	/** The SQL statement to select all the data from the Link table */
	public final static String GET_ACCESS_LINK_QUERY		= "SELECT LinkID, Author, CreationDate,	ModificationDate, LinkType, "+
															"OriginalID, FromNode, ToNode, ViewID, Arrow, CurrentStatus FROM Link";

	/** The SQL statement to select all the data from the Code table */
	public final static String GET_CODE_QUERY 			= "SELECT * FROM Code";

	/** The SQL statement to select all the data from the GroupCode table */
	public final static String GET_GROUPCODE_QUERY 		= "SELECT * FROM GroupCode";

	/** The SQL statement to select all the data from the CodeGroup table */
	public final static String GET_CODEGROUP_QUERY 		= "SELECT * FROM CodeGroup";

	/** The SQL statement to select all the data from the NodeCode table */
	public final static String GET_NODECODE_QUERY 		= "SELECT * FROM NodeCode";

	/** The SQL statement to select all the data from the ReferenceNode table */
	public final static String GET_REFERENCE_QUERY 		= "SELECT * FROM ReferenceNode";

	/** The SQL statement to select all the data from the ViewNode table */
	public final static String GET_VIEWNODE_QUERY 		= "SELECT * FROM ViewNode";

	/** Due to a beta release bug, some users had this table's columns in a different order.
	 *  So this specifes the exact columns
	 */
	public final static String GET_SPECIFIC_VIEWNODE_QUERY = "SELECT ViewID, NodeID, XPos, YPos, CreationDate, ModificationDate, CurrentStatus, " +
		"ShowTags, ShowText, ShowTrans, ShowWeight, SmallIcon, HideIcon, LabelWrapWidth, " +
		"FontSize, FontFace, FontStyle, Foreground, Background FROM ViewNode";		

	/** The SQL statement to select all the data from the ShortCutNode table */
	public final static String GET_SHORTCUT_QUERY 		= "SELECT * FROM ShortCutNode";

	/** The SQL statement to select all the data from the NodeDetail table */
	public final static String GET_NODEDETAIL_QUERY		= "SELECT * FROM NodeDetail";

	/** The SQL statement to select all the data from the ViewProperty table */
	public final static String GET_VIEWPROPERTY_QUERY 	= "SELECT * FROM ViewProperty";

	/** The SQL statement to select all the data from the Favorite table */
	public final static String GET_FAVORITE_QUERY	 	= "SELECT * FROM Favorite";

	/** The SQL statement to select all the data from the Workspace table */
	public final static String GET_WORKSPACE_QUERY 		= "SELECT * FROM Workspace";

	/** The SQL statement to select all the data from the WorkspaceView table */
	public final static String GET_WORKSPACEVIEW_QUERY	= "SELECT * FROM WorkspaceView";

	/** The SQL statement to select all the data from the ViewLink table */ 
															// Added due to an error in 2.0 Alpha releases DatabaseUpdate order.
															// A few testers will have the fields in a different order, 
															// So they are added here in full to avoid errors in Backup.
	public final static String GET_VIEWLINK_QUERY 		= "SELECT ViewID,LinkID,CreationDate,ModificationDate,CurrentStatus,"+
														  "LabelWrapWidth,ArrowType,LinkStyle,LinkDashed,LinkWeight,LinkColour,"+
														  "FontSize,FontFace,FontStyle,Foreground,Background FROM ViewLink";																														

	/** The SQL statement to select all the data from the NodeUserState table */
	public final static String GET_NODEUSERSTATE_QUERY 	= "SELECT * FROM NodeUserState";

	/** The SQL statement to select all the data from the Clone table */
	public final static String GET_CLONE_QUERY			= "SELECT * FROM Clone";

	/** The SQL statement to select all the data from the ExtendedNodeType table */
	public final static String GET_EXTENDEDNODE_QUERY	= "SELECT * FROM ExtendedNodeType";

	/** The SQL statement to select all the data from the ExtendedTypeCode table */
	public final static String GET_EXTENDEDCODE_QUERY	= "SELECT * FROM ExtendedTypeCode";

	/** The SQL statement to select all the data from the Permission table */
	public final static String GET_PERMISSION_QUERY		= "SELECT * FROM Permission";

	/** The SQL statement to select all the data from the UserGroup table */
	public final static String GET_USERGROUP_QUERY		= "SELECT * FROM UserGroup";

	/** The SQL statement to select all the data from the GroupUser table */
	public final static String GET_GROUPUSER_QUERY		= "SELECT * FROM GroupUser";

	/** The SQL statement to select all the data from the Audit table */
	public final static String GET_AUDIT_QUERY			= "SELECT * FROM Audit";

	/** The SQL statement to select all the data from the ViewLayer table */
	public final static String GET_VIEWLAYER_QUERY		= "SELECT * FROM ViewLayer";

	/** The SQL statement to select all the data from the NodeProperty table */
	public final static String GET_NODEPROPERTY_QUERY	= "SELECT * FROM NodeProperty";

	/** The SQL statement to select all the data from the Connection table */
	public final static String GET_CONNECTION_QUERY		= "SELECT * FROM Connections";

	/** The SQL statement to select all the data from the Preference table */
	public final static String GET_PREFERENCE_QUERY		= "SELECT * FROM Preference";

	/** The SQL statement to select all the data from the Meeting table */
	public final static String GET_MEETING_QUERY		= "SELECT * FROM Meeting";

	/** The SQL statement to select all the data from the MediaIndex table */
	public final static String GET_MEDIAINDEX_QUERY		= "SELECT * FROM MediaIndex";

	/** The SQL statement to select all the data from the LinkedFile table */
	public final static String GET_LINKEDFILE_QUERY		= "SELECT * FROM LinkedFile";

	/** The SQL statement to select all the data from the ViewTimeNode table */
	public final static String GET_VIEWTIMENODE_QUERY	= "SELECT * FROM ViewTimeNode";

	/** The SQL statement to select all the data from the Movie table */
	public final static String GET_MOVIES_QUERY		= "SELECT * FROM Movies";

	/** The SQL statement to select all the data from the MovieProperties table */
	public final static String GET_MOVIEPROPERTIES_QUERY = "SELECT * FROM MovieProperties";

// UNPREPARED STATEMENTS TO PUT THE DATA INTO THE DATABASE

	/**
	 * A partial SQL statement to put a record into the System table, ends with 'VALUES '.
	 * For use with unprepared Statement objects. Values need to be appended for:
	 * (Property, Contents)
	 */
	public final static String INSERT_SYSTEM_QUERY_BASE =
		"INSERT INTO System (Property, Contents) "+
		"VALUES ";

	/**
	 * A partial SQL statement to put a record into the User table, ends with 'VALUES '.
	 * For use with unprepared Statement objects. Values need to be appended for:
	 * (UserID, Author, CreationDate, ModificationDate, Login, Name, Password, Description, HomeView, IsAdministrator, CurrentStatus)
	 */
	public final static String INSERT_USER_QUERY_BASE =
		"INSERT INTO Users (UserID, Author, CreationDate, ModificationDate, " +
		"Login, Name, Password, Description, HomeView, IsAdministrator, CurrentStatus, LinkView) "+
		"VALUES ";

	/**
	 * A partial SQL statement to put a record into the Node table, ends with 'VALUES '.
	 * For use with unprepared Statement objects. Values need to be appended for:
	 * (NodeID, Author, CreationDate, ModificationDate, NodeType, OriginalID, ExtendedNodeType, Label, Detail, CurrentStatus)
	 */
	public final static String INSERT_NODE_QUERY_BASE =
		"INSERT INTO Node (NodeID, Author, CreationDate, ModificationDate, NodeType, " +
		"OriginalID, ExtendedNodeType, Label, Detail, CurrentStatus, LastModAuthor) "+
		"VALUES ";

	/**
	 * A partial SQL statement to put a record into the Link table, ends with 'VALUES '.
	 * For use with unprepared Statement objects. Values need to be appended for:
	 * (LinkID, Author, CreationDate, ModificationDate, LinkType, OriginalID, FromNode, ToNode, CurrentStatus)
	 */
	public final static String INSERT_LINK_QUERY_BASE =
		"INSERT INTO Link (LinkID, Author, CreationDate, ModificationDate, LinkType, " +
		"OriginalID, FromNode, ToNode, Label, CurrentStatus) "+
		"VALUES ";

	/**
	 * A partial SQL statement to put a record into the Code table, ends with 'VALUES '.
	 * For use with unprepared Statement objects. Values need to be appended for:
	 * (CodeID, Author, CreationDate, ModificationDate, Name, Description, Behavior)
	 */
	public final static String INSERT_CODE_QUERY_BASE =
		"INSERT INTO Code (CodeID, Author, CreationDate, ModificationDate, Name, Description, Behavior) "+
		"VALUES ";

	/**
	 * A partial SQL statement to put a record into the CodeGroup table, ends with 'VALUES '.
	 * For use with unprepared Statement objects. Values need to be appended for:
	 * (CodeID, CodeGroupID, Author, CreationDate, ModificationDate)
	 */
	public final static String INSERT_GROUPCODE_QUERY_BASE =
		"INSERT INTO GroupCode (CodeID, CodeGroupID, Author, CreationDate, ModificationDate) "+
		"VALUES ";

	/**
	 * A partial SQL statement to put a record into the CodeGroup table, ends with 'VALUES '.
	 * For use with unprepared Statement objects. Values need to be appended for:
	 * (CodeGroupID, Author, Name, Description, CreationDate, ModificationDate)
	 */
	public final static String INSERT_CODEGROUP_QUERY_BASE =
		"INSERT INTO CodeGroup (CodeGroupID, Author, Name, Description, CreationDate, ModificationDate) "+
		"VALUES ";

	/**
	 * A partial SQL statement to put a record into the NodeCode table, ends with 'VALUES '.
	 * For use with unprepared Statement objects. Values need to be appended for:
	 * (NodeID, CodeID)
	 */
	public final static String INSERT_NODECODE_QUERY_BASE =
		"INSERT INTO NodeCode (NodeID, CodeID) "+
		"VALUES ";

	/**
	 * A partial SQL statement to put a record into the Reference table, ends with 'VALUES '.
	 * For use with unprepared Statement objects. Values need to be appended for:
	 * (NodeID, Source, ImageSource, ImageWidth, ImageHeight)
	 */
	public final static String INSERT_REFERENCE_QUERY_BASE =
		"INSERT INTO ReferenceNode (NodeID, Source, ImageSource, ImageWidth, ImageHeight) "+
		"VALUES ";

	/**
	 * A partial SQL statement to put a record into the ViewNode table, ends with 'VALUES '.
	 * For use with unprepared Statement objects. Values need to be appended for:
	 * (ViewID, NodeID, XPos, YPos, CreationDate, ModificationDate, CurrentStatus,
	 *  ShowTags, ShowText, ShowTrans, ShowWeight, SmallIcons, HideIcons, LabelWrapWidth, 
	 *  FontSize, FontFace, FontStyle, Foreground, Background)
	 */
	public final static String INSERT_VIEWNODE_QUERY_BASE =
		"INSERT INTO ViewNode (ViewID, NodeID, XPos, YPos, CreationDate, ModificationDate, CurrentStatus, " +
		"ShowTags, ShowText, ShowTrans, ShowWeight, SmallIcon, HideIcon, LabelWrapWidth, " +
		"FontSize, FontFace, FontStyle, Foreground, Background) "+
		"VALUES ";

	/**
	 * A partial SQL statement to put a record into the ViewLink table, ends with 'VALUES '.
	 * For use with unprepared Statement objects. Values need to be appended for:
	 * (ViewID, LinkID, CreationDate, ModificationDate, CurrentStatus)
	 */	
	public final static String INSERT_VIEWLINK_QUERY_BASE =
		"INSERT INTO ViewLink (ViewID, LinkID, CreationDate, ModificationDate, CurrentStatus, " +
		"LabelWrapWidth, ArrowType, LinkStyle, LinkDashed, LinkWeight, LinkColour, FontSize, " +
		"FontFace, FontStyle, Foreground, Background) "+
		"VALUES ";

	/**
	 * A partial SQL statement to put a record into the ShortCutNode table, ends with 'VALUES '.
	 * For use with unprepared Statement objects. Values need to be appended for:
	 * (NodeID, ReferenceID)
	 */
	public final static String INSERT_SHORTCUT_QUERY_BASE =
		"INSERT INTO ShortCutNode (NodeID, ReferenceID) "+
		"VALUES ";

	/**
	 * A partial SQL statement to put a record into the NodeDetail table, ends with 'VALUES '.
	 * For use with unprepared Statement objects. Values need to be appended for:
	 * (NodeID, Author, PageNo, CreationDate, ModificationDate, Detail)
	 */
	public final static String INSERT_NODEDETAIL_QUERY_BASE =
		"INSERT INTO NodeDetail (NodeID, Author, PageNo, CreationDate, ModificationDate, Detail) "+
		"VALUES ";

	/**
	 * A partial SQL statement to put a record into the ViewProperty table, ends with 'VALUES '.
	 * For use with unprepared Statement objects. Values need to be appended for:
	 * (UserID, ViewID, HorizontalScroll, VerticalScroll, Width, Height, XPosition, YPosition, IsIcon, IsMaximum,
     *	ShowTags, ShowText, ShowTrans, ShowWeight, SmallIcons, HideIcons, LabelLength, LabelWidth, FontSize, FontFace, FontStyle)
	 */
	public final static String INSERT_VIEWPROPERTY_QUERY_BASE =
		"INSERT INTO ViewProperty (UserID, ViewID, HorizontalScroll, VerticalScroll, Width, Height, XPosition, YPosition, IsIcon, IsMaximum, "+
		"ShowTags, ShowText, ShowTrans, ShowWeight, "+
		"SmallIcons, HideIcons, LabelLength, LabelWidth, FontSize, FontFace, FontStyle) "+
		"VALUES ";

	/**
	 * A partial SQL statement to put a record into the Favorite table, ends with 'VALUES '.
	 * For use with unprepared Statement objects. Values need to be appended for:
	 * (UserID, NodeID, Label, NodeType, CreationDate, ModificationDate)
	 */
	public final static String INSERT_FAVORITE_QUERY_BASE =
		"INSERT INTO Favorite (UserID, NodeID, Label, NodeType, CreationDate, ModificationDate, ViewID) "+
		"VALUES ";

	/**
	 * A partial SQL statement to put a record into the Workspace table, ends with 'VALUES '.
	 * For use with unprepared Statement objects. Values need to be appended for:
	 * (WorkspaceID, UserID, Name, CreationDate, ModificationDate)
	 */
	public final static String INSERT_WORKSPACE_QUERY_BASE =
		"INSERT INTO Workspace (WorkspaceID, UserID, Name, CreationDate, ModificationDate) "+
		"VALUES ";

	/**
	 * A partial SQL statement to put a record into the WorkspaceView table, ends with 'VALUES '.
	 * For use with unprepared Statement objects. Values need to be appended for:
	 * (WorkspaceID, ViewID,  HorizontalScroll, VerticalScroll, Width, Height, XPosition, YPosition, IsIcon, IsMaximum)
	 */
	public final static String INSERT_WORKSPACEVIEW_QUERY_BASE =
		"INSERT INTO WorkspaceView (WorkspaceID, ViewID,  HorizontalScroll, VerticalScroll, Width, Height, XPosition, YPosition, IsIcon, IsMaximum) "+
		"VALUES ";

	/**
	 * A partial SQL statement to put a record into the NodeUserState table, ends with 'VALUES '.
	 * For use with unprepared Statement objects. Values need to be appended for:
	 * (NodeID, UserID, State)
	 */
	public final static String INSERT_NODEUSERSTATE_QUERY_BASE =
		"INSERT INTO NodeUserState (NodeID, UserID, State) "+
		"VALUES ";

	/**
	 * A partial SQL statement to put a record into the Clone table, ends with 'VALUES '.
	 * For use with unprepared Statement objects. Values need to be appended for:
	 * (ParentNodeID, ChildNodeID)
	 */
	public final static String INSERT_CLONE_QUERY_BASE =
		"INSERT INTO Clone (ParentNodeID, ChildNodeID) "+
		"VALUES ";

	/**
	 * A partial SQL statement to put a record into the ExtendedNodeType table, ends with 'VALUES '.
	 * For use with unprepared Statement objects. Values need to be appended for:
	 * (ExtendedNodeTypeID, Author, CreationDate, ModificationDate, Name, Description, BaseNodeType, Icon)
	 */
	public final static String INSERT_EXTENDEDNODE_QUERY_BASE =
		"INSERT INTO ExtendedNodeType (ExtendedNodeTypeID, Author, CreationDate, ModificationDate, Name, Description, BaseNodeType, Icon) "+
		"VALUES ";

	/**
	 * A partial SQL statement to put a record into the ExtendedTypeCode table, ends with 'VALUES '.
	 * For use with unprepared Statement objects. Values need to be appended for:
	 * (ExtendedNodeTypeID, CodeID)
	 */
	public final static String INSERT_EXTENDEDCODE_QUERY_BASE =
		"INSERT INTO ExtendedTypeCode (ExtendedNodeTypeID, CodeID) "+
		"VALUES ";

	/**
	 * A partial SQL statement to put a record into the Permission table, ends with 'VALUES '.
	 * For use with unprepared Statement objects. Values need to be appended for:
	 * (ItemID, GroupID, Permission)
	 */
	public final static String INSERT_PERMISSION_QUERY_BASE =
		"INSERT INTO Permission (ItemID, GroupID, Permission) "+
		"VALUES ";

	/**
	 * A partial SQL statement to put a record into the UserGroup table, ends with 'VALUES '.
	 * For use with unprepared Statement objects. Values need to be appended for:
	 * (GroupID, UserID, CreationDate, ModificationDate, Name, Description)
	 */
	public final static String INSERT_USERGROUP_QUERY_BASE =
		"INSERT INTO UserGroup (GroupID, UserID, CreationDate, ModificationDate, Name, Description) "+
		"VALUES ";

	/**
	 * A partial SQL statement to put a record into the GroupUser table, ends with 'VALUES '.
	 * For use with unprepared Statement objects. Values need to be appended for:
	 * (GroupID, UserID)
	 */
	public final static String INSERT_GROUPUSER_QUERY_BASE =
		"INSERT INTO GroupUser (GroupID, UserID) "+
		"VALUES ";

	/**
	 * A partial SQL statement to put a record into the Audit table, ends with 'VALUES '.
	 * For use with unprepared Statement objects. Values need to be appended for:
	 * (AuditID, Author, ItemID, AuditDate, Category, Action, Data)
	 */
	public final static String INSERT_AUDIT_QUERY_BASE =
		"INSERT INTO Audit (AuditID, Author, ItemID, AuditDate, Category, Action, Data) "+
		"VALUES ";

	/**
	 * A partial SQL statement to put a record into the ViewLayer table, ends with 'VALUES '.
	 * For use with unprepared Statement objects. Values need to be appended for:
	 * (UserID, ViewID, Scribble, Background, Grid, Shapes)
	 */
	public final static String INSERT_VIEWLAYER_QUERY_BASE =
		"INSERT INTO ViewLayer (ViewID, Scribble, Background, Grid, Shapes, BackgroundColor) "+
		"VALUES ";

	/**
	 * A partial SQL statement to put a record into the Connection table, ends with 'VALUES '.
	 * For use with unprepared Statement objects. Values need to be appended for:
	 * (UserID, Profile, Type, Server, Login, Password, Name, Port, Resource)
	 */
	public final static String INSERT_CONNECTION_QUERY_BASE =
		"INSERT INTO Connections (UserID, Profile, Type, Server, Login, Password, Name, Port, Resource) "+
		"VALUES ";

	/**
	 * A partial SQL statement to put a record into the Preference table, ends with 'VALUES '.
	 * For use with unprepared Statement objects. Values need to be appended for:
	 * (UserID, Property, Contents)
	 */
	public final static String INSERT_PREFERENCE_QUERY_BASE =
		"INSERT INTO Preference (UserID, Property, Contents) "+
		"VALUES ";

	/**
	 * A partial SQL statement to put a record into the Meeting table, ends with 'VALUES '.
	 * For use with unprepared Statement objects. Values need to be appended for:
	 * (MeetingID, MeetingMapID, MeetingName, MeetingDate)
	 */
	public final static String INSERT_MEETING_QUERY_BASE =
		"INSERT INTO Meeting (MeetingID, MeetingMapID, MeetingName, MeetingDate, CurrentStatus) "+
		"VALUES ";

	/**
	 * A partial SQL statement to put a record into the MediaIndex table, ends with 'VALUES '.
	 * For use with unprepared Statement objects. Values need to be appended for:
	 * (ViewID, NodeID, MeetingID, MediaIndex, CreationDate, ModificationDate)
	 */
	public final static String INSERT_MEDIAINDEX_QUERY_BASE =
		"INSERT INTO MediaIndex (ViewID, NodeID, MeetingID, MediaIndex, CreationDate, ModificationDate) "+
		"VALUES ";

	/**
	 * A partial SQL statement to put a record into the LinkedFile table, ends with 'VALUES '.
	 * For use with unprepared Statement objects. Values need to be appended for:
	 * (FileID, FileName, FileSize, FileData)
	 */
	public final static String INSERT_LINKEDFILE_QUERY_BASE =
		"INSERT INTO LinkedFile (FileID, FileName, FileSize, FileData) "+
		"VALUES ";
	
	/**
	 * A partial SQL statement to put a record into the ViewTimeNode table, ends with 'VALUES '.
	 * For use with unprepared Statement objects. Values need to be appended for:
	 * (ViewTimeNodeID, ViewID, NodeID, TimeToShow, TimeToHide, XPos, YPos, CreationDate, ModificationDate, CurrentStatus)
	 */
	public final static String INSERT_VIEWTIMENODE_QUERY_BASE =
		"INSERT INTO ViewTimeNode (ViewTimeNodeID, ViewID, NodeID, TimeToShow, TimeToHide, XPos, YPos, CreationDate, ModificationDate, CurrentStatus) "+
		"VALUES ";

	/**
	 * A partial SQL statement to put a record into the Movie table, ends with 'VALUES '.
	 * For use with unprepared Statement objects. Values need to be appended for:
	 * (ViewID, ViewID, Link, CreationDate, ModificationDate, Name, StartTime)
	 */
	public final static String INSERT_MOVIES_QUERY_BASE =
		"INSERT INTO Movies (MovieID, ViewID, Link, CreationDate, ModificationDate, Name, StartTime) "+
		"VALUES ";

	/**
	 * A partial SQL statement to put a record into the MovieProperties table, ends with 'VALUES '.
	 * For use with unprepared Statement objects. Values need to be appended for:
	 * (MoviePropertyID, MovieID, XPos, YPos, Width, Height, Transparency, Time, CreationDate, ModificationDate)
	 */
	public final static String INSERT_MOVIEPROPERTIES_QUERY_BASE =
		"INSERT INTO MovieProperties (MoviePropertyID, MovieID, XPos, YPos, Width, Height, Transparency, Time, CreationDate, ModificationDate) "+
		"VALUES ";

// PREPARETD STATEMENTS TO PUT THE DATA INTO THE DATABASE

	/**
	 * An SQL statement to put a record into the System table.
	 * For use with PreparedStatement objects. Values need to be added to a PreparedStatement instance for:
	 * (Property, Contents)
	 */
	public final static String INSERT_SYSTEM_QUERY = INSERT_SYSTEM_QUERY_BASE +
		"(?, ?)";

	/**
	 * An SQL statement to put a record into the User table.
	 * For use with PreparedStatement objects. Values need to be added to a PreparedStatement instance for:
	 * (UserID, Author, CreationDate, ModificationDate, Login, Name, Password, Description, HomeView, 
	 * IsAdministrator, CurrentStatus, LinkView)
	 */
	public final static String INSERT_USER_QUERY = INSERT_USER_QUERY_BASE +
		"(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

	/**
	 * An SQL statement to put a record into the Node table.
	 * For use with PreparedStatement objects. Values need to be added to a PreparedStatement instance for:
	 * (NodeID, Author, CreationDate, ModificationDate, NodeType, OriginalID, ExtendedNodeType, Label, Detail, 
	 * CurrentStatus, LastModUserID)
	 */
	public final static String INSERT_NODE_QUERY = INSERT_NODE_QUERY_BASE +
		"(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

	/**
	 * An SQL statement to put a record into the Link table.
	 * For use with Prepared Statement objects. Values need to be added to a PreparedStatement instance for:
	 * (LinkID, Author, CreationDate, ModificationDate, LinkType, OriginalID, FromNode, ToNode, ViewID, Arrow, CurrentStatus)
	 */
	public final static String INSERT_LINK_QUERY = INSERT_LINK_QUERY_BASE +
		"(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

	/**
	 * An SQL statement to put a record into the Code table.
	 * For use with PreparedStatement objects. Values need to be added to a PreparedStatement instance for:
	 * (CodeID, Author, CreationDate, ModificationDate, Name, Description, Behavior)
	 */
	public final static String INSERT_CODE_QUERY = INSERT_CODE_QUERY_BASE +
		"(?, ?, ?, ?, ?, ?, ?)";

	/**
	 * An SQL statement to put a record into the CodeGroup table.
	 * For use with PreparedStatement objects. Values need to be added to a PreparedStatement instance for:
	 * (CodeID, CodeGroupID, Author, CreationDate, ModificationDate)
	 */
	public final static String INSERT_GROUPCODE_QUERY = INSERT_GROUPCODE_QUERY_BASE +
		"(?, ?, ?, ?, ?)";

	/**
	 * An SQL statement to put a record into the CodeGroup table.
	 * For use with PreparedStatement objects. Values need to be added to a PreparedStatement instanace for:
	 * (CodeGroupID, Author, Name, Description, CreationDate, ModificationDate)
	 */
	public final static String INSERT_CODEGROUP_QUERY = INSERT_CODEGROUP_QUERY_BASE +
		"(?, ?, ?, ?, ?, ?)";

	/**
	 * An SQL statement to put a record into the NodeCode table.
	 * For use with PreparedStatement objects. Values need to be added to a PreparedStatement instance for:
	 * (NodeID, CodeID)
	 */
	public final static String INSERT_NODECODE_QUERY = INSERT_NODECODE_QUERY_BASE +
		"(?, ?)";

	/**
	 * An SQL statement to put a record into the Reference table.
	 * For use with PreparedStatement objects. Values need to be aded to a PreparedStatement instance for:
	 * (NodeID, Source, ImageSource, ImageWidth, ImageHeight)
	 */
	public final static String INSERT_REFERENCE_QUERY = INSERT_REFERENCE_QUERY_BASE +
		"(?, ?, ?, ?, ?)";

	/**
	 * An SQL statement to put a record into the ViewNode table.
	 * For use with PreparedStatement objects. Values need to be added to a PreparedStatement instance for:
	 * (ViewID, NodeID, XPos, YPos, CreationDate, ModificationDate, CurrentStatus,
	 *  ShowTags, ShowText, ShowTrans, ShowWeight, SmallIcons, HideIcons, LabelWrapWidth, 
	 *  FontSize, FontFace, FontStyle, Foreground, Background)
	 */
	public final static String INSERT_VIEWNODE_QUERY = INSERT_VIEWNODE_QUERY_BASE +
		"(?, ?, ?, ?, ?, ?, ?, ?, ? ,? ,? ,? ,? ,? ,?, ?, ? ,? ,?)";

	/**
	 * An SQL statement to put a record into the ViewLink table.
	 * For use with PreparedStatement objects. Values need to be added to a PreparedStatement instance for:
	 * (ViewID, LinkID, CreationDate, ModificationDate, CurrentStatus)
	 */
	public final static String INSERT_VIEWLINK_QUERY = INSERT_VIEWLINK_QUERY_BASE +
		"(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

	/**
	 * An SQL statement to put a record into the ShortCutNode table.
	 * For use with PreparedStatement objects. Values need to be added to a PreparedStatement instance for:
	 * (NodeID, ReferenceID)
	 */
	public final static String INSERT_SHORTCUT_QUERY = INSERT_SHORTCUT_QUERY_BASE +
		"(?, ?)";

	/**
	 * An SQL statement to put a record into the NodeDetail table.
	 * For use with PreparedStatement objects. Values need to be added to a PreparedStatement instance for:
	 * (NodeID, Author, PageNo, CreationDate, ModificationDate, Detail)
	 */
	public final static String INSERT_NODEDETAIL_QUERY = INSERT_NODEDETAIL_QUERY_BASE +
		"(?, ?, ?, ?, ?, ?)";

	/**
	 * An SQL statement to put a record into the ViewProperty table.
	 * For use with PreparedStatement objects. Values need to be added to a PreparedStatement instance for:
	 * (UserID, ViewID, HorizontalScroll, VerticalScroll, Width, Height, XPosition, YPosition, IsIcon, IsMaximum,
     *	ShowTags, ShowText, ShowTrans, ShowWeight, SmallIcons, HideIcons, LabelLength, LabelWidth, FontSize, FontFace, FontStyle)
	 */
	public final static String INSERT_VIEWPROPERTY_QUERY = INSERT_VIEWPROPERTY_QUERY_BASE +
		"(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

	/**
	 * An SQL statement to put a record into the Favorite table.
	 * For use with PreparedStatement objects. Values need to be added to a PreparedStatement instance for:
	 * (UserID, NodeID, Label, NodeType, CreationDate, ModificationDate)
	 */
	public final static String INSERT_FAVORITE_QUERY = INSERT_FAVORITE_QUERY_BASE +
		"(?, ?, ?, ?, ?, ?, ?)";

	/**
	 * An SQL statement to put a record into the Workspace table.
	 * For use with PreparedStatement objects. Values need to be added to a PreparedStatement instance for:
	 * (WorkspaceID, UserID, Name, CreationDate, ModificationDate)
	 */
	public final static String INSERT_WORKSPACE_QUERY = INSERT_WORKSPACE_QUERY_BASE +
		"(?, ?, ?, ?, ?)";

	/**
	 * An SQL statement to put a record into the WorkspaceView table.
	 * For use with PreparedStatement objects. Values need to be added to a PreparedStatement instance for:
	 * (WorkspaceID, ViewID,  HorizontalScroll, VerticalScroll, Width, Height, XPosition, YPosition, IsIcon, IsMaximum)
	 */
	public final static String INSERT_WORKSPACEVIEW_QUERY = INSERT_WORKSPACEVIEW_QUERY_BASE +
		"(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

	/**
	 * An SQL statement to put a record into the NodeUserState table.
	 * For use with PreparedStatement objects. Values need to be aded to a PreparedStatement instance for:
	 * (NodeID, UserID, State)
	 */
	public final static String INSERT_NODEUSERSTATE_QUERY = INSERT_NODEUSERSTATE_QUERY_BASE +
		"(?, ?, ?)";

	/**
	 * An SQL statement to put a record into the Clone table.
	 * For use with PreparedStatement objects. Values need to be added to a PreparedStatement instance for:
	 * (ParentNodeID, ChildNodeID)
	 */
	public final static String INSERT_CLONE_QUERY = INSERT_CLONE_QUERY_BASE +
		"(?, ?)";

	/**
	 * An SQL statement to put a record into the ExtendedNodeType table.
	 * For use with PreparedStatement objects. Values need to be added to a PreparedStatement instance for:
	 * (ExtendedNodeTypeID, Author, CreationDate, ModificationDate, Name, Description, BaseNodeType, Icon)
	 */
	public final static String INSERT_EXTENDEDNODE_QUERY = INSERT_EXTENDEDNODE_QUERY_BASE +
		"(?, ?, ?, ?, ?, ?, ?, ?)";

	/**
	 * An SQL statement to put a record into the ExtendedTypeCode table.
	 * For use with PreparedStatement objects. Values need to be added to a PreparedStatement instance for:
	 * (ExtendedNodeTypeID, CodeID)
	 */
	public final static String INSERT_EXTENDEDCODE_QUERY = INSERT_EXTENDEDCODE_QUERY_BASE +
		"(?, ?)";

	/**
	 * An SQL statement to put a record into the Permission table.
	 * For use with PreparedStatement objects. Values need to be added to a PreparedStatement instance for:
	 * (ItemID, GroupID, Permission)
	 */
	public final static String INSERT_PERMISSION_QUERY = INSERT_PERMISSION_QUERY_BASE +
		"(?, ?, ?)";

	/**
	 * An SQL statement to put a record into the UserGroup table.
	 * For use with PreparedStatement objects. Values need to be added to a PreparedStatment instance for:
	 * (GroupID, UserID, CreationDate, ModificationDate, Name, Description)
	 */
	public final static String INSERT_USERGROUP_QUERY = INSERT_USERGROUP_QUERY_BASE +
		"(?, ?, ?, ?, ?, ?)";

	/**
	 * An SQL statement to put a record into the GroupUser table.
	 * For use with PreparedStatement objects. Values need to be added to a PreparedStatement instance for:
	 * (GroupID, UserID)
	 */
	public final static String INSERT_GROUPUSER_QUERY = INSERT_GROUPUSER_QUERY_BASE +
		"(?, ?)";

	/**
	 * An SQL statement to put a record into the Audit table.
	 * For use with PreparedStatement objects. Values need to be added to a PreparedStatement instance for:
	 * (AuditID, Author, ItemID, AuditDate, Category, Action, Data)
	 */
	public final static String INSERT_AUDIT_QUERY = INSERT_AUDIT_QUERY_BASE +
		"(?, ?, ?, ?, ?, ?, ?)";

	/**
	 * An SQL statement to put a record into the ViewLayer table.
	 * For use with PreparedStatement objects. Values need to be added to a PreparedStatement instance for:
	 * (UserID, ViewID, Scribble, Background, Grid, Shapes)
	 */
	public final static String INSERT_VIEWLAYER_QUERY = INSERT_VIEWLAYER_QUERY_BASE +
		"(?, ?, ?, ?, ?, ?)";

	/**
	 * An SQL statement to put a record into the Connection table.
	 * For use with PreparedStatement objects. Values need to be edded to a PreparedStatement instance for:
	 * (UserID, Profile, Type, Server, Login, Password, Name, Port, Resource)
	 */
	public final static String INSERT_CONNECTION_QUERY = INSERT_CONNECTION_QUERY_BASE +
		"(?, ?, ?, ?, ?, ?, ?, ?, ?)";

	/**
	 * An SQL statement to put a record into the Preference table.
	 * For use with PreparedStatement objects. Values need to be added to a PreparedStatement instance for:
	 * (UserID, Property, Contents)
	 */
	public final static String INSERT_PREFERENCE_QUERY = INSERT_PREFERENCE_QUERY_BASE +
		"(?, ?, ?)";

	/**
	 * An SQL statement to put a record into the Meeting table.
	 * For use with PreparedStatement objects. Values need to be added to a PreparedStatement instance for:
	 * (MeetingID, MeetingMapID, MeetingName, MeetingDate)
	 */
	public final static String INSERT_MEETING_QUERY = INSERT_MEETING_QUERY_BASE +
		"(?, ?, ?, ?, ?)";

	/**
	 * An SQL statement to put a record into the MediaIndex table.
	 * For use with PreparedStatement objects. Values need to be added to a PreparedStatement instance for:
	 * (ViewID, NodeID, MeetingID, MediaIndex)
	 */
	public final static String INSERT_MEDIAINDEX_QUERY = INSERT_MEDIAINDEX_QUERY_BASE +
		"(?, ?, ?, ?, ?, ?)";

	/**
	 * An SQL statement to put a record into the LinkedFile table.
	 * For use with PreparedStatement objects. Values need to be added to a PreparedStatement instance for:
	 * (FileID, FileName, FileSize, FileData)
	 */	
	public final static String INSERT_LINKEDFILE_QUERY = INSERT_LINKEDFILE_QUERY_BASE + 
		"(?, ?, ?, ?)";	
	
	/**
	 * An SQL statement to put a record into the ViewTimeNode table.
	 * For use with PreparedStatement objects. Values need to be added to a PreparedStatement instance for:
	 * (ViewTimeNodeID, ViewID, NodeID, TimeToShow, TimeToHide, XPos, YPos, CreationDate, ModificationDate, CurrentStatus)
	 */
	public final static String INSERT_VIEWTIMENODE_QUERY = INSERT_VIEWTIMENODE_QUERY_BASE +
		"(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
	
	/**
	 * An SQL statement to put a record into the Movies table.
	 * For use with PreparedStatement objects. Values need to be added to a PreparedStatement instance for:
	 * (MovieID, ViewID, Link, CreationDate, ModificationDate, Name, StartTime)
	 */
	public final static String INSERT_MOVIES_QUERY = INSERT_MOVIES_QUERY_BASE +
		"(?, ?, ?, ?, ?, ?, ?)";

	/**
	 * An SQL statement to put a record into the Movies table.
	 * For use with PreparedStatement objects. Values need to be added to a PreparedStatement instance for:
	 * (ViewID, XPos, YPos, Width, Height, Transparency, Time, CreationDate, ModificationDate)
	 */
	public final static String INSERT_MOVIEPROPERTIES_QUERY = INSERT_MOVIEPROPERTIES_QUERY_BASE +
		"(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

}
