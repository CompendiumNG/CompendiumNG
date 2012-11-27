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
 * This interface defines the global constants for the database management classes when using a MySQL database.
 * These are all String objects representing various SQL statements shared by various management classes.
 *
 * @author Michelle Bachler
 */
public interface DBConstantsMySQL extends java.io.Serializable {

//	 STATEMENTS TO DROP TABLES

	/** The SQL statement to drop a User table if it exists */
	public final static String MYSQL_DROP_USER_TABLE		= "DROP TABLE IF EXISTS Users";

	/** The SQL statement to drop a System table if it exists */
	public final static String MYSQL_DROP_SYSTEM_TABLE		= "DROP TABLE IF EXISTS System";

	/** The SQL statement to drop a Node table if it exists */
	public final static String MYSQL_DROP_NODE_TABLE 		= "DROP TABLE IF EXISTS Node";

	/** The SQL statement to drop a Link table if it exists */
	public final static String MYSQL_DROP_LINK_TABLE		= "DROP TABLE IF EXISTS Link";

	/** The SQL statement to drop a Code table if it exists */
	public final static String MYSQL_DROP_CODE_TABLE 		= "DROP TABLE IF EXISTS Code";

	/** The SQL statement to drop a GroupCode table if it exists */
	public final static String MYSQL_DROP_GROUPCODE_TABLE 	= "DROP TABLE IF EXISTS GroupCode";

	/** The SQL statement to drop a CodeGroup table if it exists */
	public final static String MYSQL_DROP_CODEGROUP_TABLE 	= "DROP TABLE IF EXISTS CodeGroup";

	/** The SQL statement to drop a NodeCode table if it exists */
	public final static String MYSQL_DROP_NODECODE_TABLE 	= "DROP TABLE IF EXISTS NodeCode";

	/** The SQL statement to drop a ReferenceNode table if it exists */
	public final static String MYSQL_DROP_REFERENCE_TABLE 	= "DROP TABLE IF EXISTS ReferenceNode";

	/** The SQL statement to drop a ViewNode table if it exists */
	public final static String MYSQL_DROP_VIEWNODE_TABLE 	= "DROP TABLE IF EXISTS ViewNode";

	/** The SQL statement to drop a ShortCutNode table if it exists */
	public final static String MYSQL_DROP_SHORTCUT_TABLE 	= "DROP TABLE IF EXISTS ShortCutNode";

	/** The SQL statement to drop a NodeDetail table if it exists */
	public final static String MYSQL_DROP_NODEDETAIL_TABLE	= "DROP TABLE IF EXISTS NodeDetail";

	/** The SQL statement to drop a ViewProperty table if it exists */
	public final static String MYSQL_DROP_VIEWPROPERTY_TABLE 	= "DROP TABLE IF EXISTS ViewProperty";

	/** The SQL statement to drop a Favorite table if it exists */
	public final static String MYSQL_DROP_FAVORITE_TABLE		= "DROP TABLE IF EXISTS Favorite";

	/** The SQL statement to drop a Workspace table if it exists */
	public final static String MYSQL_DROP_WORKSPACE_TABLE 		= "DROP TABLE IF EXISTS Workspace";

	/** The SQL statement to drop a WorkspaceView table if it exists */
	public final static String MYSQL_DROP_WORKSPACEVIEW_TABLE	= "DROP TABLE IF EXISTS WorkspaceView";

	/** The SQL statement to drop a ViewLink table if it exists */
	public final static String MYSQL_DROP_VIEWLINK_TABLE 		= "DROP TABLE IF EXISTS ViewLink";

	/** The SQL statement to drop a NodeUserState table if it exists */
	public final static String MYSQL_DROP_NODEUSERSTATE_TABLE 	= "DROP TABLE IF EXISTS NodeUserState";

	/** The SQL statement to drop a Clonetable if it exists */
	public final static String MYSQL_DROP_CLONE_TABLE			= "DROP TABLE IF EXISTS Clone";

	/** The SQL statement to drop a ExtendedNodeType table if it exists */
	public final static String MYSQL_DROP_EXTENDEDNODE_TABLE	= "DROP TABLE IF EXISTS ExtendedNodeType";

	/** The SQL statement to drop a ExtendedTypeCode table if it exists */
	public final static String MYSQL_DROP_EXTENDEDCODE_TABLE	= "DROP TABLE IF EXISTS ExtendedTypeCode";

	/** The SQL statement to drop a Permission table if it exists */
	public final static String MYSQL_DROP_PERMISSION_TABLE		= "DROP TABLE IF EXISTS Permission";

	/** The SQL statement to drop a UserGroup table if it exists */
	public final static String MYSQL_DROP_USERGROUP_TABLE		= "DROP TABLE IF EXISTS UserGroup";

	/** The SQL statement to drop a GroupUser table if it exists */
	public final static String MYSQL_DROP_GROUPUSER_TABLE		= "DROP TABLE IF EXISTS GroupUser";

	/** The SQL statement to drop a Audit table if it exists */
	public final static String MYSQL_DROP_AUDIT_TABLE			= "DROP TABLE IF EXISTS Audit";

	/** The SQL statement to drop a ViewLayer table if it exists */
	public final static String MYSQL_DROP_VIEWLAYER_TABLE		= "DROP TABLE IF EXISTS ViewLayer";

	/** The SQL statement to drop a NodeProperty table if it exists */
	public final static String MYSQL_DROP_NODEPROPERTY_TABLE	= "DROP TABLE IF EXISTS NodeProperty";

	/** The SQL statement to drop a Connection table if it exists */
	public final static String MYSQL_DROP_CONNECTION_TABLE		= "DROP TABLE IF EXISTS Connections";

	/** The SQL statement to drop a Preference table if it exists */
	public final static String MYSQL_DROP_PREFERENCE_TABLE		= "DROP TABLE IF EXISTS Preference";

	/** The SQL statement to drop a Meetingtable if it exists */
	public final static String MYSQL_DROP_MEETING_TABLE			= "DROP TABLE IF EXISTS Meeting";

	/** The SQL statement to drop a MediaIndex table if it exists */
	public final static String MYSQL_DROP_MEDIAINDEX_TABLE		= "DROP TABLE IF EXISTS MediaIndex";

	/** The SQL statement to drop a LinkedFile table if it exists */
	public final static String MYSQL_DROP_LINKEDFILE_TABLE		= "DROP TABLE IF EXISTS LinkedFile";

	/** The SQL statement to drop a LinkedFile table if it exists */
	public final static String MYSQL_DROP_VIEWTIMENODE_TABLE	= "DROP TABLE IF EXISTS ViewTimeNode";

	/** The SQL statement to drop a Movies table if it exists */
	public final static String MYSQL_DROP_MOVIES_TABLE			= "DROP TABLE IF EXISTS Movies";	

	/** The SQL statement to drop a Movies table if it exists */
	public final static String MYSQL_DROP_MOVIEPROPERTIES_TABLE	= "DROP TABLE IF EXISTS MovieProperties";	

	// STATEMENTS TO CREATE NEW TABLES

	/** The SQL statement to create the projects table, which holds database information */
	public static final String MYSQL_CREATE_PROJECT_TABLE = "CREATE TABLE Project ("+
														"ProjectName VARCHAR(100) NOT NULL, "+
														"DatabaseName VARCHAR(100) NOT NULL, "+
														"CreationDate DOUBLE NOT NULL, "+
														"ModificationDate DOUBLE NOT NULL, "+
														"CONSTRAINT PK_project PRIMARY KEY (ProjectName, DatabaseName) ) TYPE = InnoDB";

	/** The SQL statement to create the properties table */
	public static final String MYSQL_CREATE_PROPERTIES_TABLE = "CREATE TABLE Properties ("+
															"Property VARCHAR(100) NOT NULL, "+
															"Contents LONGTEXT NOT NULL, "+
															"CONSTRAINT PK_Properties PRIMARY KEY (Property)) TYPE = InnoDB";

	/** The SQL statement to create a new User table */
	public static final String MYSQL_CREATE_USER_TABLE = "CREATE TABLE Users ("+
															"UserID VARCHAR(50) NOT NULL, " +
															"Author VARCHAR(50) NOT NULL, " +
															"CreationDate DOUBLE NOT NULL, " +
															"ModificationDate DOUBLE NOT NULL, "+
															"Login VARCHAR(20) NOT NULL, " +
															"Name VARCHAR(50), " +
															"Password VARCHAR(50) NOT NULL, " +
															"Description VARCHAR(255), " +
															"HomeView VARCHAR(50) NOT NULL, "+
															"IsAdministrator ENUM('N','Y') NOT NULL, "+
															"CurrentStatus INTEGER NOT NULL DEFAULT '0', "+
															"LinkView VARCHAR(50), "+																														
															"CONSTRAINT PK_User PRIMARY KEY(UserID)) TYPE = InnoDB";

	/** The SQL statement to create a new Audit table */
	public static final String MYSQL_CREATE_AUDIT_TABLE = "CREATE TABLE Audit ("+
															"AuditID VARCHAR(50) NOT NULL, "+
															"Author VARCHAR(50) NOT NULL, " +
															"ItemID VARCHAR(50) NOT NULL, "+
															"AuditDate DOUBLE NOT NULL, "+
															"Category VARCHAR(50) NOT NULL, "+
															"Action INTEGER NOT NULL, "+
															"Data LONGTEXT, "+
															"CONSTRAINT PK_Audit PRIMARY KEY (AuditID)) TYPE = InnoDB";

	/** The SQL statement to create a new Code table */
	public static final String MYSQL_CREATE_CODE_TABLE = "CREATE TABLE Code ("+
															"CodeID VARCHAR(50) NOT NULL, "+
															"Author VARCHAR(50) NOT NULL, " +
															"CreationDate DOUBLE NOT NULL, " +
															"ModificationDate DOUBLE NOT NULL, "+
															"Name VARCHAR(50) NOT NULL, " +
															"Description VARCHAR(100), " +
															"Behavior VARCHAR(255), "+
															"CONSTRAINT PK_Code PRIMARY KEY (CodeID)) TYPE = InnoDB";

	/** The SQL statement to create a new CodeGroup table */
	public static final String MYSQL_CREATE_CODEGROUP_TABLE = "CREATE TABLE CodeGroup ("+
															"CodeGroupID VARCHAR(50) NOT NULL, "+
															"Author VARCHAR(50) NOT NULL, " +
															"Name VARCHAR(100) NOT NULL, "+
															"Description VARCHAR(255), "+
															"CreationDate DOUBLE NOT NULL, " +
															"ModificationDate DOUBLE NOT NULL, "+
															"CONSTRAINT PK_CodeGroup PRIMARY KEY (CodeGroupID)) TYPE = InnoDB";

	/** The SQL statement to create a new Connection table */
	public static final String MYSQL_CREATE_CONNECTION_TABLE = "CREATE TABLE Connections ("+
															"UserID VARCHAR(50) NOT NULL, " +
															"Profile VARCHAR(255) NOT NULL, "+
															"Type INTEGER NOT NULL, "+
															"Server VARCHAR(255) NOT NULL, "+
															"Login VARCHAR(255) NOT NULL, "+
															"Password VARCHAR(255) NOT NULL, "+
															"Name VARCHAR(255), "+
															"Port INTEGER, "+
															"Resource VARCHAR(255), "+
															"CONSTRAINT PK_Connection PRIMARY KEY (UserID, Profile, Type), "+
															"CONSTRAINT FK_Connection_1 FOREIGN KEY (UserID) REFERENCES Users (UserID) ON DELETE CASCADE) TYPE = InnoDB";

	/** The SQL statement to create a new ExtendedNodeType table */
	public static final String MYSQL_CREATE_EXTENDEDNODE_TABLE = "CREATE TABLE ExtendedNodeType ("+
															"ExtendedNodeTypeID VARCHAR(50) NOT NULL, " +
															"Author VARCHAR(50) NOT NULL, " +
															"CreationDate DOUBLE NOT NULL, " +
															"ModificationDate DOUBLE NOT NULL, "+
															"Name VARCHAR(50), "+
															"Description VARCHAR(100), "+
															"BaseNodeType INTEGER NOT NULL, " +
															"Icon VARCHAR(200), "+
															"CONSTRAINT PK_ExtendedNode PRIMARY KEY (ExtendedNodeTypeID)) TYPE = InnoDB";

	/** The SQL statement to create a new ExtendedTypeCode table */
	public static final String MYSQL_CREATE_EXTENDEDCODE_TABLE = "CREATE TABLE ExtendedTypeCode ("+
															"ExtendedNodeTypeID VARCHAR(50) NOT NULL, " +
															"CodeID VARCHAR(50) NOT NULL, " +
															"INDEX ExtendedCode_CodeID_Ind (CodeID), "+
															"CONSTRAINT PK_ExtendedCode PRIMARY KEY (ExtendedNodeTypeID, CodeID), "+
															"CONSTRAINT FK_ExtendedCode_1 FOREIGN KEY (ExtendedNodeTypeID) REFERENCES ExtendedNodeType (ExtendedNodeTypeID) ON DELETE CASCADE, "+
															"CONSTRAINT FK_ExtendedCode_2 FOREIGN KEY (CodeID) REFERENCES Code (CodeID) ON DELETE CASCADE) TYPE = InnoDB";

	/** The SQL statement to create a new Favorite table */
	public static final String MYSQL_CREATE_FAVORITE_TABLE = "CREATE TABLE Favorite (" +
															"UserID VARCHAR(50) NOT NULL, " +
															"NodeID VARCHAR(50) NOT NULL, " +
															"Label LONGTEXT NOT NULL, " +
															"NodeType INTEGER NOT NULL, " +
															"CreationDate DOUBLE NOT NULL, " +
															"ModificationDate DOUBLE NOT NULL, "+
															"ViewID VARCHAR(50), " +	
															"INDEX Favorite_NodeID_Ind (NodeID), "+	
															//"INDEX Favorite_ViewID_Ind (ViewID), "+																														
															"CONSTRAINT PK_Favorite PRIMARY KEY (UserID, NodeID), "+
															"CONSTRAINT FK_Favorite_1 FOREIGN KEY (UserID) REFERENCES Users (UserID) ON DELETE CASCADE, "+
															"CONSTRAINT FK_Favorite_2 FOREIGN KEY (NodeID) REFERENCES Node (NodeID) ON DELETE CASCADE, "+
															"CONSTRAINT FK_Favorite_3 FOREIGN KEY (ViewID) REFERENCES Node (NodeID) ON DELETE CASCADE) TYPE = InnoDB";
	

	/** The SQL statement to create a new GroupCode table */
	public static final String MYSQL_CREATE_GROUPCODE_TABLE = "CREATE TABLE GroupCode ("+
															"CodeID VARCHAR(50) NOT NULL, "+
															"CodeGroupID VARCHAR(50) NOT NULL, " +
															"Author VARCHAR(50) NOT NULL, " +
															"CreationDate DOUBLE NOT NULL, " +
															"ModificationDate DOUBLE NOT NULL, "+
															"INDEX GroupCode_CodeGroupID_Ind (CodeGroupID), "+
															"CONSTRAINT PK_GroupCode PRIMARY KEY (CodeID, CodeGroupID), "+
															"CONSTRAINT FK_GroupCode_1 FOREIGN KEY (CodeID) REFERENCES Code (CodeID) ON DELETE CASCADE, "+
															"CONSTRAINT FK_GroupCode_2 FOREIGN KEY (CodeGroupID) REFERENCES CodeGroup (CodeGroupID) ON DELETE CASCADE) TYPE = InnoDB";

	/** The SQL statement to create a new Link table */
	public static final String MYSQL_CREATE_LINK_TABLE = "CREATE TABLE Link ("+
															"LinkID VARCHAR(50) NOT NULL, "+
															"Author VARCHAR(50) NOT NULL, " +
															"CreationDate DOUBLE NOT NULL, " +
															"ModificationDate DOUBLE NOT NULL, "+
															"LinkType VARCHAR(50) NOT NULL, " +
															"OriginalID VARCHAR(50), "+
															"FromNode VARCHAR(50) NOT NULL, " +
															"ToNode VARCHAR(50) NOT NULL, " +
															"ViewID VARCHAR(50) NOT NULL DEFAULT '0', " + // LEAVE FOR BACKWARDS COMPATIBILITY
															"Label TEXT, "+
															"CurrentStatus INTEGER NOT NULL DEFAULT '0', "+
															"INDEX Link_FromNode_Ind (FromNode), "+
															"INDEX Link_ToNode_Ind (ToNode), "+
															"CONSTRAINT PK_Link PRIMARY KEY (LinkID), "+
															"CONSTRAINT FK_Link_1 FOREIGN KEY (FromNode) REFERENCES Node (NodeID) ON DELETE CASCADE, "+
															"CONSTRAINT FK_Link_2 FOREIGN KEY (ToNode) REFERENCES Node (NodeID) ON DELETE CASCADE) type = InnoDB";

	/** The SQL statement to create a new Node table */
	public static final String MYSQL_CREATE_NODE_TABLE = "CREATE TABLE Node ("+
															"NodeID VARCHAR(50) NOT NULL, " +
															"Author VARCHAR(50) NOT NULL, " +
															"CreationDate DOUBLE NOT NULL, " +
															"ModificationDate DOUBLE NOT NULL, "+
															"NodeType INTEGER NOT NULL, " +
															"OriginalID VARCHAR(255), "+
															"ExtendedNodeType VARCHAR(50), " +
															"Label LONGTEXT, "+
															"Detail LONGTEXT, "+
															"CurrentStatus INTEGER NOT NULL DEFAULT '0', "+
															"LastModAuthor VARCHAR(50), " +															
															"CONSTRAINT PK_Node PRIMARY KEY (NodeID)) TYPE = InnoDB";

	/** The SQL statement to create a new NodeDetail table */
	public static final String MYSQL_CREATE_NODEDETAIL_TABLE = "CREATE TABLE NodeDetail ("+
															"NodeID VARCHAR(50) NOT NULL, "+
															"Author VARCHAR(50) NOT NULL, " +
															"PageNo INTEGER NOT NULL, "+
															"CreationDate DOUBLE NOT NULL, " +
															"ModificationDate DOUBLE NOT NULL, "+
															"Detail LONGTEXT, "+
															"CONSTRAINT PK_NodeDetail PRIMARY KEY (NodeID, PageNo), "+
															"CONSTRAINT FK_NodeDetail_1 FOREIGN KEY (NodeID) REFERENCES Node (NodeID) ON DELETE CASCADE) TYPE = InnoDB";

	/** The SQL statement to create a new NodeUserState table */
	public static final String MYSQL_CREATE_NODEUSERSTATE_TABLE = "CREATE TABLE NodeUserState ("+
															"NodeID VARCHAR(50) NOT NULL, " +
															"UserID VARCHAR(50) NOT NULL, " +
															"State INTEGER NOT NULL, "+
															"INDEX NodeUserState_UserID_Ind (UserID), "+
															"CONSTRAINT PK_NodeUserState PRIMARY KEY (NodeID, UserID), "+
															"CONSTRAINT FK_NodeUserState_1 FOREIGN KEY (NodeID) REFERENCES Node (NodeID) ON DELETE CASCADE, "+
															"CONSTRAINT FK_NodeUaerState_2 FOREIGN KEY (UserID) REFERENCES Users (UserID) ON DELETE CASCADE) TYPE = InnoDB";

	/** The SQL statement to create a new System table */
	public static final String MYSQL_CREATE_SYSTEM_TABLE = "CREATE TABLE System (Property VARCHAR(100) NOT NULL, "+
															"Contents VARCHAR(255) NOT NULL, "+
															"CONSTRAINT PK_System PRIMARY KEY (Property)) TYPE = InnoDB";

	/** The SQL statement to create a new ReferenceNode table */
	public static final String MYSQL_CREATE_REFERENCE_TABLE = "CREATE TABLE ReferenceNode ("+
															"NodeID VARCHAR(50) NOT NULL, "+
															"Source TEXT, "+
															"ImageSource VARCHAR(255), "+
															"ImageWidth INTEGER, "+
															"ImageHeight INTEGER, "+
															"CONSTRAINT PK_ReferenceNode PRIMARY KEY (NodeID), "+
															"CONSTRAINT FK_ReferenceNode_1 FOREIGN KEY (NodeID) REFERENCES Node (NodeID) ON DELETE CASCADE) TYPE = InnoDB";

	/** The SQL statement to create a new NodeCode table */
	public static final String MYSQL_CREATE_NODECODE_TABLE = "CREATE TABLE NodeCode ("+
															"NodeID VARCHAR(50) NOT NULL, " +
															"CodeID VARCHAR(50) NOT NULL, "+
															"INDEX NodeCode_CodeID_Ind (CodeID), "+
															"CONSTRAINT PK_NodeCode PRIMARY KEY (NodeID, CodeID), "+
															"CONSTRAINT FK_NodeCode_1 FOREIGN KEY (NodeID) REFERENCES Node (NodeID) ON DELETE CASCADE, "+
															"CONSTRAINT FK_NodeCode_2 FOREIGN KEY (CodeID) REFERENCES Code (CodeID) ON DELETE CASCADE) TYPE = InnoDB";

	/** The SQL statement to create a new ViewLink table */
	public static final String MYSQL_CREATE_VIEWLINK_TABLE = "CREATE TABLE ViewLink ("+
															"ViewID VARCHAR(50) NOT NULL, " +
															"LinkID VARCHAR(50) NOT NULL, " +
															"CreationDate DOUBLE NOT NULL, " +
															"ModificationDate DOUBLE NOT NULL, "+
															"CurrentStatus INTEGER NOT NULL DEFAULT '0', "+
															"LabelWrapWidth INTEGER NOT NULL DEFAULT 25, "+
															"ArrowType INTEGER NOT NULL DEFAULT 1, "+
															"LinkStyle INTEGER NOT NULL DEFAULT 0,"+
															"LinkDashed INTEGER NOT NULL DEFAULT 0,"+
															"LinkWeight INTEGER NOT NULL DEFAULT 1,"+
															"LinkColour INTEGER NOT NULL DEFAULT "+Color.black.getRGB()+","+
															"FontSize INTEGER NOT NULL DEFAULT 12, "+
															"FontFace VARCHAR(100) NOT NULL DEFAULT 'Arial', "+
															"FontStyle INTEGER NOT NULL DEFAULT 0, " +
															"Foreground INTEGER NOT NULL DEFAULT "+Color.black.getRGB()+", "+ 
															"Background INTEGER NOT NULL DEFAULT "+Color.white.getRGB()+", "+																														
															"INDEX ViewLink_LinkID_Ind (LinkID), "+
															"CONSTRAINT PK_ViewLink PRIMARY KEY (ViewID, LinkID), "+
															"CONSTRAINT viewlink_ibfk_1 FOREIGN KEY (ViewID) REFERENCES Node (NodeID) ON DELETE CASCADE, "+
															"CONSTRAINT FK_ViewLink_2 FOREIGN KEY (LinkID) REFERENCES Link (LinkID) ON DELETE CASCADE) TYPE = InnoDB";

	/** The SQL statement to create a new Shortcut table */
	public static final String MYSQL_CREATE_SHORTCUT_TABLE = "CREATE TABLE ShortCutNode ("+
															"NodeID VARCHAR(50) NOT NULL, " +
															"ReferenceID VARCHAR(50) NOT NULL, "+
															"CONSTRAINT PK_ShortcutNode PRIMARY KEY (NodeID, ReferenceID), "+
															"CONSTRAINT FK_ShortcutNode_1 FOREIGN KEY (NodeID) REFERENCES Node (NodeID) ON DELETE CASCADE) TYPE = InnoDB";

	/** The SQL statement to create a new ViewNode table */
	public static final String MYSQL_CREATE_VIEWNODE_TABLE = "CREATE TABLE ViewNode ("+
															"ViewID VARCHAR(50) NOT NULL, " +
															"NodeID VARCHAR(50) NOT NULL, " +
															"XPos INTEGER NOT NULL DEFAULT '0', "+
															"YPos INTEGER NOT NULL DEFAULT '0', "+
															"CreationDate DOUBLE, " +
															"ModificationDate DOUBLE, "+
															"CurrentStatus INTEGER NOT NULL DEFAULT '0', "+
															"ShowTags VARCHAR(1) NOT NULL DEFAULT 'Y', "+
															"ShowText VARCHAR(1) NOT NULL DEFAULT 'Y', "+
															"ShowTrans VARCHAR(1) NOT NULL DEFAULT 'Y', "+
															"ShowWeight VARCHAR(1) NOT NULL DEFAULT 'Y', "+
															"SmallIcon VARCHAR(1) NOT NULL DEFAULT 'N', "+
															"HideIcon VARCHAR(1) NOT NULL DEFAULT 'N', "+															
															"LabelWrapWidth INTEGER NOT NULL DEFAULT '25', "+
															"FontSize INTEGER NOT NULL DEFAULT '12', "+
															"FontFace VARCHAR(100) NOT NULL DEFAULT 'Arial', "+
															"FontStyle INTEGER NOT NULL DEFAULT '0', " +
															"Foreground INTEGER NOT NULL DEFAULT "+Color.black.getRGB()+", "+ 
															"Background INTEGER NOT NULL DEFAULT "+Color.white.getRGB()+", "+																														
															"INDEX ViewNode_NodeID_Ind (NodeID), "+
															"CONSTRAINT PK_ViewNode PRIMARY KEY (ViewID, NodeID), "+
															"CONSTRAINT FK_ViewNode_1 FOREIGN KEY (ViewID) REFERENCES Node (NodeID) ON DELETE CASCADE, "+
															"CONSTRAINT FK_ViewNode_2 FOREIGN KEY (NodeID) REFERENCES Node (NodeID) ON DELETE CASCADE) TYPE = InnoDB";

	/** The SQL statement to create a new UserGroup table */
	public static final String MYSQL_CREATE_USERGROUP_TABLE = "CREATE TABLE UserGroup ("+
															"GroupID VARCHAR(50) NOT NULL, "+
															"UserID VARCHAR(50) NOT NULL, " +
															"CreationDate DOUBLE NOT NULL, " +
															"ModificationDate DOUBLE NOT NULL, "+
															"Name VARCHAR(100) NOT NULL, " +
															"Description VARCHAR(255), "+
															"CONSTRAINT PK_UserGroup PRIMARY KEY (GroupID)) TYPE = InnoDB";

	/** The SQL statement to create a new GroupUser table */
	public static final String MYSQL_CREATE_GROUPUSER_TABLE = "CREATE TABLE GroupUser ("+
															"UserID VARCHAR(50) NOT NULL, " +
															"GroupID VARCHAR(50) NOT NULL, " +
															"INDEX GroupUser_GroupID_Ind (GroupID), "+
															"INDEX UserGroup_UserID_Ind (UserID), "+
															"CONSTRAINT PK_GroupUser PRIMARY KEY (UserID, GroupID), "+
															"CONSTRAINT FK_GroupUser_1 FOREIGN KEY (UserID) REFERENCES Users (UserID) ON DELETE CASCADE, "+
															"CONSTRAINT FK_GroupUser_2 FOREIGN KEY (GroupID) REFERENCES UserGroup (GroupID) ON DELETE CASCADE) TYPE = InnoDB";

	/** The SQL statement to create a new Permision table */
	public static final String MYSQL_CREATE_PERMISSION_TABLE = "CREATE TABLE Permission ("+
															"ItemID VARCHAR(50) NOT NULL, " +
															"GroupID VARCHAR(50) NOT NULL, " +
															"Permission INTEGER NOT NULL, "+
															"INDEX Permission_GroupID_Ind (GroupID), "+
															"CONSTRAINT PK_Permission PRIMARY KEY (ItemID, GroupID), "+
															"CONSTRAINT FK_Permission_1 FOREIGN KEY (GroupID) REFERENCES UserGroup (GroupID) ON DELETE CASCADE) TYPE = InnoDB";

	/** The SQL statement to create a new Clone table */
	public static final String MYSQL_CREATE_CLONE_TABLE = "CREATE TABLE Clone ("+
															"ParentNodeID VARCHAR(50) NOT NULL, "+
															"ChildNodeID VARCHAR(50) NOT NULL, " +
															"INDEX Clone_ChildNodeID_Ind (ChildNodeID), "+
															"CONSTRAINT PK_Clone PRIMARY KEY (ParentNodeID, ChildNodeID), "+
															"CONSTRAINT FK_Clone_1 FOREIGN KEY (ChildNodeID) REFERENCES Node (NodeID) ON DELETE CASCADE) TYPE = InnoDB";

	/** The SQL statement to create a new Preference table */
	public static final String MYSQL_CREATE_PREFERENCE_TABLE = "CREATE TABLE Preference ("+
															"UserID VARCHAR(50) NOT NULL, " +
															"Property VARCHAR(100) NOT NULL, "+
															"Contents VARCHAR(255) NOT NULL, "+
															"CONSTRAINT PK_Preference PRIMARY KEY (UserID, Property), "+
															"CONSTRAINT FK_Preference_1 FOREIGN KEY (UserID) REFERENCES Users (UserID) ON DELETE CASCADE) TYPE = InnoDB";

	/** The SQL statement to create a new ViewLayer table */
	public static final String MYSQL_CREATE_VIEWLAYER_TABLE = "CREATE TABLE ViewLayer ("+
															"ViewID VARCHAR(50) NOT NULL, " +
															"Scribble LONGTEXT, " +
															"Background VARCHAR(255), "+
															"Grid VARCHAR(255), "+
															"Shapes LONGTEXT, " +
															"BackgroundColor INTEGER DEFAULT '-1', "+															
															"INDEX ViewLayer_ViewID_Ind (ViewID), "+
															"CONSTRAINT PK_ViewLayer PRIMARY KEY (ViewID), "+
															"CONSTRAINT FK_ViewLayer_1 FOREIGN KEY (ViewID) REFERENCES Node (NodeID) ON DELETE CASCADE) TYPE = InnoDB";

	/** The SQL statement to create a new ViewProperty table */
	public static final String MYSQL_CREATE_VIEWPROPERTY_TABLE = "CREATE TABLE ViewProperty ("+
															"UserID VARCHAR(50) NOT NULL, " +
															"ViewID VARCHAR(50) NOT NULL, " +
															"HorizontalScroll INTEGER NOT NULL, " +
															"VerticalScroll INTEGER NOT NULL, " +
															"Width INTEGER NOT NULL, "+
															"Height INTEGER NOT NULL, "+
															"XPosition INTEGER NOT NULL, "+
															"YPosition INTEGER NOT NULL, "+
															"IsIcon ENUM('N','Y') NOT NULL DEFAULT 'N', "+
															"IsMaximum ENUM('N','Y') NOT NULL DEFAULT 'N', "+
															"ShowTags ENUM('N','Y') NOT NULL DEFAULT 'N', "+
															"ShowText ENUM('N','Y') NOT NULL DEFAULT 'N', "+
															"ShowTrans ENUM('N','Y') NOT NULL DEFAULT 'N', "+
															"ShowWeight ENUM('N','Y') NOT NULL DEFAULT 'N', "+
															"SmallIcons ENUM('N','Y') NOT NULL DEFAULT 'N', "+
															"HideIcons ENUM('N','Y') NOT NULL DEFAULT 'N', "+
															"LabelLength INTEGER NOT NULL DEFAULT '100', "+
															"LabelWidth INTEGER NOT NULL DEFAULT '15', "+
															"FontSize INTEGER NOT NULL DEFAULT '12', "+
															"FontFace VARCHAR(100) NOT NULL DEFAULT 'Arial', "+
															"FontStyle INTEGER NOT NULL DEFAULT '0', "+
															"INDEX ViewProperty_ViewID_Ind (ViewID), "+
															"CONSTRAINT PK_ViewProperty PRIMARY KEY (UserID, ViewID), "+
															"CONSTRAINT FK_ViewProperty_1 FOREIGN KEY (UserID) REFERENCES Users (UserID) ON DELETE CASCADE, "+
															"CONSTRAINT FK_ViewProperty_2 FOREIGN KEY (ViewID) REFERENCES Node (NodeID) ON DELETE CASCADE) TYPE = InnoDB";

	/** The SQL statement to create a new Workspace table */
	public static final String MYSQL_CREATE_WORKSPACE_TABLE = "CREATE TABLE Workspace (" +
															"WorkspaceID VARCHAR(50) NOT NULL, "+
															"UserID VARCHAR(50) NOT NULL, " +
															"Name VARCHAR(100) NOT NULL, " +
															"CreationDate DOUBLE NOT NULL, " +
															"ModificationDate DOUBLE NOT NULL, "+
															"INDEX Workspace_UserID_Ind (UserID), "+
															"CONSTRAINT PK_Workspace PRIMARY KEY (WorkspaceID), "+
															"CONSTRAINT FK_Workspace_1 FOREIGN KEY (UserID) REFERENCES Users (UserID) ON DELETE CASCADE) TYPE = InnoDB";

	/** The SQL statement to create a new WorkspaceView table */
	public static final String MYSQL_CREATE_WORKSPACEVIEW_TABLE = "CREATE TABLE WorkspaceView (" +
															"WorkspaceID VARCHAR(50) NOT NULL, " +
															"ViewID VARCHAR(50) NOT NULL, " +
															"HorizontalScroll INTEGER NOT NULL, "+
															"VerticalScroll INTEGER NOT NULL, "+
															"Width INTEGER NOT NULL, "  +
															"Height INTEGER NOT NULL, " +
															"XPosition INTEGER NOT NULL, " +
															"YPosition INTEGER NOT NULL, " +
															"IsIcon ENUM('N','Y') NOT NULL, " +
															"IsMaximum ENUM('N','Y') NOT NULL, "+
															"INDEX WorkspaceView_ViewID_Ind (ViewID), "+
															"CONSTRAINT PK_WorkspaceView PRIMARY KEY (WorkspaceID, ViewID), "+
															"CONSTRAINT FK_WorkspaceView_1 FOREIGN KEY (WorkspaceID) REFERENCES Workspace (WorkspaceID) ON DELETE CASCADE, "+
															"CONSTRAINT FK_WorkspaceView_2 FOREIGN KEY (ViewID) REFERENCES Node (NodeID) ON DELETE CASCADE) TYPE = InnoDB";

	/** The SQL statement to create a new Meeting table */
	public static final String MYSQL_CREATE_MEETING_TABLE = "CREATE TABLE Meeting (" +
															"MeetingID VARCHAR(255) NOT NULL, " +
															"MeetingMapID VARCHAR(50) NOT NULL, "+
															"MeetingName VARCHAR (255), "+
															"MeetingDate DOUBLE, "+
															"CurrentStatus INTEGER NOT NULL DEFAULT 0, "+
															"INDEX Meeting_MeetingMapID_Ind (MeetingMapID), "+
															"CONSTRAINT PK_Meeting PRIMARY KEY (MeetingID), "+
															"CONSTRAINT FK_Meeting_1 FOREIGN KEY (MeetingMapID) REFERENCES Node (NodeID) ON DELETE CASCADE) TYPE = InnoDB";

	/** The SQL statement to create a new MediaIndex table */
	public static final String MYSQL_CREATE_MEDIAINDEX_TABLE = "CREATE TABLE MediaIndex (" +
															"ViewID VARCHAR(50) NOT NULL, "+
															"NodeID VARCHAR(50) NOT NULL, " +
															"MeetingID VARCHAR (255) NOT NULL, " +
															"MediaIndex DOUBLE NOT NULL, " +
															"CreationDate DOUBLE NOT NULL, " +
															"ModificationDate DOUBLE NOT NULL, "+
															"INDEX MediaIndex_NodeID_Ind (NodeID), "+
															"INDEX MediaIndex_MeetingID_Ind (MeetingID), "+
															"CONSTRAINT PK_MediaIndex PRIMARY KEY (ViewID, NodeID, MeetingID), "+
															"CONSTRAINT FK_MediaIndex_1 FOREIGN KEY (ViewID) REFERENCES Node (NodeID) ON DELETE CASCADE, "+
															"CONSTRAINT FK_MediaIndex_2 FOREIGN KEY (NodeID) REFERENCES Node (NodeID) ON DELETE CASCADE, "+
															"CONSTRAINT FK_MediaIndex_3 FOREIGN KEY (MeetingID) REFERENCES Meeting (MeetingID) ON DELETE CASCADE) TYPE = InnoDB";

	/** The SQL statement to create a new LinkedFile table */
	public static final String MYSQL_CREATE_LINKEDFILE_TABLE = "CREATE TABLE LinkedFile (" +
															"FileID VARCHAR(50) NOT NULL, "+
															"FileName VARCHAR(255) NOT NULL, "+
															"FileSize INT NOT NULL, "+
															"FileData LONGBLOB, "+
															"INDEX LinkedFile_FileID_Ind (FileID), "+
															"INDEX LinkedFile_FileName_Ind (FileName)) TYPE=InnoDB";

	/** The SQL statement to create a new ViewNode table */
	public static final String MYSQL_CREATE_VIEWTIMENODE_TABLE = "CREATE TABLE ViewTimeNode ("+
															"ViewTimeNodeID VARCHAR(50) NOT NULL, " +
															"ViewID VARCHAR(50) NOT NULL, " +
															"NodeID VARCHAR(50) NOT NULL, " +
															"TimeToShow DOUBLE NOT NULL DEFAULT 0, "+
															"TimeToHide DOUBLE NOT NULL DEFAULT -1, "+
															"XPos INTEGER NOT NULL DEFAULT '0', "+
															"YPos INTEGER NOT NULL DEFAULT '0', "+
															"CreationDate DOUBLE, " +
															"ModificationDate DOUBLE, "+
															"CurrentStatus INTEGER NOT NULL DEFAULT '0', "+
															"INDEX ViewTimeNode_NodeID_Ind (NodeID), "+
															"CONSTRAINT PK_ViewTimeNode PRIMARY KEY (ViewTimeNodeID), "+
															"CONSTRAINT FK_ViewTimeNode_1 FOREIGN KEY (ViewID) REFERENCES Node (NodeID) ON DELETE CASCADE, "+
															"CONSTRAINT FK_ViewTimeNode_2 FOREIGN KEY (NodeID) REFERENCES Node (NodeID) ON DELETE CASCADE) TYPE = InnoDB";

	/** The SQL statement to create a new Movies table */
	public static final String MYSQL_CREATE_MOVIES_TABLE = "CREATE TABLE Movies ("+
															"MovieID VARCHAR(50) NOT NULL, " +
															"ViewID VARCHAR(50) NOT NULL, "+
															"Link TEXT NOT NULL, "+
															"CreationDate DOUBLE, " +
															"ModificationDate DOUBLE, "+
															"Name VARCHAR(255) DEFAULT '', "+
															"StartTime DOUBLE NOT NULL DEFAULT 0, " +
															"CONSTRAINT PK_Movies PRIMARY KEY (MovieID), "+
															"CONSTRAINT FK_Movies_1 FOREIGN KEY (ViewID) REFERENCES Node (NodeID) ON DELETE CASCADE) TYPE = InnoDB";

	/** The SQL statement to create a new Movies table */
	public static final String MYSQL_CREATE_MOVIEPROPERTIES_TABLE = "CREATE TABLE MovieProperties ("+
															"MoviePropertyID VARCHAR(50) NOT NULL, " +
															"MovieID VARCHAR(50) NOT NULL, " +
															"XPos INTEGER NOT NULL DEFAULT 0, "+
															"YPos INTEGER NOT NULL DEFAULT 0, "+
															"Width INTEGER NOT NULL DEFAULT 0, "+
															"Height INTEGER NOT NULL DEFAULT 0, "+
															"Transparency FLOAT NOT NULL DEFAULT 1.0, "+															
															"Time DOUBLE NOT NULL DEFAULT 0, "+
															"CreationDate DOUBLE, " +
															"ModificationDate DOUBLE, "+
															"CONSTRAINT PK_MovieProperties PRIMARY KEY (MoviePropertyID), "+
															"CONSTRAINT FK_MovieProperties_1 FOREIGN KEY (MovieID) REFERENCES Movies (MovieID) ON DELETE CASCADE) TYPE = InnoDB";


	/** 
	 * This array holds all the create table sql statements for the MySQL database.
	 * Used by DBEmptyDatabase to create a new database.
	 */
	public static final String MYSQL_CREATE_TABLES[] = {
		MYSQL_CREATE_SYSTEM_TABLE, MYSQL_CREATE_USER_TABLE, MYSQL_CREATE_NODE_TABLE, MYSQL_CREATE_REFERENCE_TABLE,
		MYSQL_CREATE_CODE_TABLE, MYSQL_CREATE_LINK_TABLE, MYSQL_CREATE_VIEWNODE_TABLE, MYSQL_CREATE_NODEUSERSTATE_TABLE,
		MYSQL_CREATE_VIEWLINK_TABLE, MYSQL_CREATE_NODECODE_TABLE, MYSQL_CREATE_CODEGROUP_TABLE, MYSQL_CREATE_GROUPCODE_TABLE,
		MYSQL_CREATE_FAVORITE_TABLE, MYSQL_CREATE_WORKSPACE_TABLE, MYSQL_CREATE_WORKSPACEVIEW_TABLE, MYSQL_CREATE_AUDIT_TABLE,
		MYSQL_CREATE_CLONE_TABLE, MYSQL_CREATE_EXTENDEDNODE_TABLE, MYSQL_CREATE_EXTENDEDCODE_TABLE, MYSQL_CREATE_USERGROUP_TABLE,
		MYSQL_CREATE_GROUPUSER_TABLE, MYSQL_CREATE_PERMISSION_TABLE, MYSQL_CREATE_VIEWPROPERTY_TABLE, MYSQL_CREATE_NODEDETAIL_TABLE,
		MYSQL_CREATE_SHORTCUT_TABLE, MYSQL_CREATE_VIEWLAYER_TABLE, MYSQL_CREATE_CONNECTION_TABLE,
		MYSQL_CREATE_PREFERENCE_TABLE, MYSQL_CREATE_MEETING_TABLE, MYSQL_CREATE_MEDIAINDEX_TABLE, MYSQL_CREATE_LINKEDFILE_TABLE,
		MYSQL_CREATE_VIEWTIMENODE_TABLE, MYSQL_CREATE_MOVIES_TABLE, MYSQL_CREATE_MOVIEPROPERTIES_TABLE
	};
		
	/** 
	 * This array holds all the drop table sql statements for the MySQL database.
	 * Used by DBRestoreDatabase to drop all the tables before restoring.
	 */
	public static final String MYSQL_DROP_TABLES[] = {
		MYSQL_DROP_SYSTEM_TABLE, MYSQL_DROP_REFERENCE_TABLE, MYSQL_DROP_VIEWNODE_TABLE, MYSQL_DROP_NODEUSERSTATE_TABLE,
		MYSQL_DROP_VIEWLINK_TABLE, MYSQL_DROP_NODECODE_TABLE, MYSQL_DROP_GROUPCODE_TABLE, MYSQL_DROP_CODEGROUP_TABLE, 
		MYSQL_DROP_FAVORITE_TABLE, MYSQL_DROP_WORKSPACEVIEW_TABLE, MYSQL_DROP_WORKSPACE_TABLE, MYSQL_DROP_AUDIT_TABLE,
		MYSQL_DROP_CLONE_TABLE, MYSQL_DROP_EXTENDEDCODE_TABLE, MYSQL_DROP_EXTENDEDNODE_TABLE, MYSQL_DROP_GROUPUSER_TABLE, 
		MYSQL_DROP_PERMISSION_TABLE, MYSQL_DROP_VIEWPROPERTY_TABLE, MYSQL_DROP_NODEDETAIL_TABLE,
		MYSQL_DROP_SHORTCUT_TABLE, MYSQL_DROP_VIEWLAYER_TABLE, MYSQL_DROP_USERGROUP_TABLE, MYSQL_DROP_CONNECTION_TABLE,
		MYSQL_DROP_PREFERENCE_TABLE, MYSQL_DROP_MEDIAINDEX_TABLE, MYSQL_DROP_MEETING_TABLE, MYSQL_DROP_LINKEDFILE_TABLE,
		MYSQL_DROP_VIEWTIMENODE_TABLE, MYSQL_DROP_MOVIEPROPERTIES_TABLE, MYSQL_DROP_MOVIES_TABLE, 
		MYSQL_DROP_CODE_TABLE, MYSQL_DROP_LINK_TABLE, MYSQL_DROP_NODE_TABLE, MYSQL_DROP_USER_TABLE 
	};
}