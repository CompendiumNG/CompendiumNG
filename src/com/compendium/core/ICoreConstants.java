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

package com.compendium.core;

/*
 * This interface defines some global constants used in the Compendium application.
 *
 * @author Ron van Hoof / Michelle Bachler
 */
public interface ICoreConstants extends java.io.Serializable {

	/** The current version number of the Questmap Parser */
	public final static String	sPARSERVERSION				= "1.03.000";

	/** The current version number of the Compendium release */
	public final static String	sAPPVERSION					= "1.5.2";

	/** Reference to the Name of this whole Application */
	public final static String	sAPPNAME 					= "Compendium";

	/** This date is used as part of the information needed to import Access databases to MySQL */
	public final static long	MYSQLDATE					= 1050401966691L;

	/** The current version number of the database schema - must be three bit number!!!*/
	public final static String 	sDATABASEVERSION			= "1.5.0";

	/** Indicates that a given database schema is correct for this version of the software. */
	public final static int CORRECT_DATABASE_SCHEMA			= 0 ;

	/** Indicates that a given database schema is too old for this version of the software. */
	public final static int OLDER_DATABASE_SCHEMA			= 1;

	/** Indicates that a given database schema is too new for this version of the software. */
	public final static int NEWER_DATABASE_SCHEMA			= 2;

	/**
	 * The stub used as part of the unique password created and added for the MySQL user root
	 * user for external hosts if none has been added already by the user. For security.
	 */
	public final static String 	sDATABASE_PASSWORD			= "KnCu";

	/** The default MySQL username */
	public final static String 	sDEFAULT_DATABASE_USER		= "root";

	/** The default MySQL password */
	public final static String 	sDEFAULT_DATABASE_PASSWORD	= null;

	/** The default MySQL hostname */
	public final static String 	sDEFAULT_DATABASE_ADDRESS	= "localhost";

	/** Identifies the database being used as Derby.*/
	public final static int	DERBY_DATABASE = 0;

	/** Identifies the database being used as MySQL.*/
	public final static int	MYSQL_DATABASE = 1;

	/** Legacy variable now only used to filter out these old node label fillers */
	public final static String	NOLABEL_STRING				= "(No Label)";

	/** Legacy variable now only used to fileter out these old node detail fillers */
	public final static String	NODETAIL_STRING				= "(No Detail)";


	/** Indicated that a node has not beed read (1) yet  */
	public final static int UNREADSTATE 					= 1 ;

	/** Indicated that a node has been read (2) */
	public final static int READSTATE 						= 2 ;

	/** Indicated that a node was modified since last read(3) */
	public final static int MODIFIEDSTATE 					= 3;


	// NODE TYPES
	/** This node type is not currently used */
	public final static int			GENERAL					= 0;

	/**
	 * This represents a list, which is a container for other nodes.
	 * This can be used to create a sortable list of nodes,
	 * which will usually be a collection of nodes that don't need to be
	 * linked with each other (associative links).
	 */
	public final static int			LISTVIEW				= 1;

	/**
	 * This represents a map, which is a container for other nodes and links.
	 * This can be used to:
	 * - create a 'picture'of the relationships between ideas;
	 * - group questions and ideas together in meaningful clusters;
	 * - create associative links between nodes.
	 */
	public final static int			MAPVIEW					= 2;

	/** This represents a Question or Issue for discussion. */
	public final static int			ISSUE					= 3;

	/** The represents an Answer or Position, often in response to a question or issue.*/
	public final static int			POSITION				= 4;

	/** This represents a general argument, usually in response to an answer or position.*/
	public final static int			ARGUMENT				= 5;

	/** This represents a response in favour of an answer or position. */
	public final static int			PRO						= 6;

	/** This represents a response against an answer or position. */
	public final static int			CON						= 7;

	/** This represents a decision reached, usually from an answer or position about a question or issue.*/
	public final static int			DECISION				= 8;

	/**
	 * This represents a link to some additional, external reference material,
	 * which can be in the form of a web link or Word document etc.
	 * These nodes can also be linked to images, which will then be scaled
	 * and used instead of the usual reference node icon.
	 */
	public final static int			REFERENCE				= 9;

	/**
	 * This represents some non-specific, additional comment or notation,
	 * often about a node or the current view.
	 */
	public final static int			NOTE					= 10;

	// SHORTCUT NODE TYPES ARE NODES THAT HAVE A REFERENCE TO A PARENT STANDARD NODE TYPE.
	// THESE ARE USED FOR LARGE MAPS WHERE A NODE MAY BE TOO FAR AWAY FOR PRACTICAL LINKING
	// SO A SHORTCUT OF IT IS CREATED TO ALLOW FOR EASE OF MAPPING
	// THERE USE IS UNDER REVIEW.

	/** This represents a shortcut to a List Node */
	public static final int			LIST_SHORTCUT			= 11;

	/** This represents a shortcut to a Map Node */
	public static final int			MAP_SHORTCUT			= 12;

	/** This represents a shortcut to a Issue Node */
	public static final int			ISSUE_SHORTCUT			= 13;

	/** This represents a shortcut to a Position Node */
	public static final int			POSITION_SHORTCUT		= 14;

	/** This represents a shortcut to a Argument Node */
	public static final int			ARGUMENT_SHORTCUT		= 15;

	/** This represents a shortcut to a Pro Node */
	public static final int			PRO_SHORTCUT			= 16;

	/** This represents a shortcut to a Con Node */
	public static final int			CON_SHORTCUT			= 17;

	/** This represents a shortcut to a Decision Node */
	public static final int			DECISION_SHORTCUT		= 18;

	/** This represents a shortcut to a Reference Node */
	public static final int			REFERENCE_SHORTCUT		= 19;

	/** This represents a shortcut to a Note Node */
	public static final int			NOTE_SHORTCUT			= 20;

	/**
	 * The integer difference between the parent and its shortcut
	 * is used when creating shortcut icons for a particular node type
	 */
	public static final int			PARENT_SHORTCUT_DISPLACEMENT = 10;

	
	/** This node type is used for the Trashbin */
	public static final int			TRASHBIN					= 51;
	
	/** This node type is used for the inbox*/
	//public static final int			INBOX					= 52;

	
	// NODE NAMES
	/** Holds a string representation of the ISSUE Node type*/
	public final static String		sISSUE					= "IssueNode";

	/** Holds a string representation of the POSITION Node type*/
	public final static String		sPOSITION				= "PositionNode";

	/** Holds a string representation of the ARGUMENT Node type*/
	public final static String		sARGUMENT				= "ArgumentNode";

	/** Holds a string representation of the PRO Node type*/
	public final static String		sPRO					= "ArgumentProNode";

	/** Holds a string representation of the CON Node type*/
	public final static String		sCON					= "ArgumentConNode";

	/** Holds a string representation of the REFERENCE Node type*/
	public final static String		sREFERENCE				= "ReferenceNode";

	/** Holds a string representation of the DECISION Node type*/
	public final static String		sDECISION				= "DecisionNode";

	/** Holds a string representation of the NOTE Node type*/
	public final static String		sNOTE					= "NoteNode";

	/** Holds a string representation of the MAP Node type*/
	public final static String		sMAPVIEW				= "MapView";

	/** Holds a string representation of the LIST Node type*/
	public final static String		sLISTVIEW				= "ListView";

	/** Holds a string representation of a shortcut Node type*/
	public final static String		sSHORTCUT				= "ShortCutNode";

	// REFERRENCE NODE PROCESSING CONSTANTS
	/** The Internal reference string starter.*/
	public final static String		sINTERNAL_REFERENCE		= "comp://"; 

	// SELECTION STATES
	/** Indicates a single node / link should be selected */
	public final static int			SINGLESELECT			= 100;

	/** Indicated that one or more nodes / links can be selected */
	public final static int			MULTISELECT				= 101;

	/** Indicated that all nodes / links should be deselected */
	public final static int			DESELECTALL				= 102;


	// PERMISSION TYPES - Currently these are used but are not active
	// as the permission system has not been fully implemented

	/**
	 * No access to the object
	 * Code - cannot read code details, but can use the code
	 * NodeSummary - can see node summary on view only
	 * View - can only see view as node summary on another view
	 */
	public final static int NOACCESS						= 30 ;

	/**
	 * Code - can read code behavior
	 * View - can see view detail
	 * NodeSummary - can see node detail
	 */
	public final static int READ							= 31 ;

	/**
	 * Code - can change code detail
	 * NodeSummary - can change node detail
	 */
	public final static int WRITE							= 32 ;

	/** View - can see view expansion, and read the view detail */
	public final static int	READVIEWNODE					= 33 ;

	/** View - can modify view expansion	only, not the detail */
	public final static int WRITEVIEW						= 34 ;

	/** view can modify both view expansion and the view node detail */
	public final static int WRITEVIEWNODE					= 35 ;


	//LINK TYPES
	/** Indicates the Node at the FROM end of the link responds to the Node at the TO end of the link */
	public final static String		RESPONDS_TO_LINK		= "39";

	/** Indicates the Node at the FROM end of the link supports the Node at the TO end of the link */
	public final static String		SUPPORTS_LINK			= "40";

	/** Indicates the Node at the FROM end of the link objects to the Node at the TO end of the link */
	public final static String		OBJECTS_TO_LINK			= "41";

	/** Indicates the Node at the FROM end of the link challenges the Node at the TO end of the link */
	public final static String		CHALLENGES_LINK			= "42";

	/** Indicates the Node at the FROM end of the link is a specialization of the Node at the TO end of the link */
	public final static String		SPECIALIZES_LINK		= "43";

	/** Indicates the Node at the FROM end of the link expans on the Node at the TO end of the link */
	public final static String		EXPANDS_ON_LINK			= "44";

	/** Indicates the Node at the FROM end of the link is related to the Node at the TO end of the link */
	public final static String		RELATED_TO_LINK			= "45";

	/** Indicates the Node at the FROM end of the link is about the Node at the TO end of the link */
	public final static String		ABOUT_LINK				= "46";

	/** Indicates the Node at the FROM end of the link resolves the Node at the TO end of the link */
	public final static String		RESOLVES_LINK			= "47";

	/** Indicates which link type is the default link type - Currently the 'Related To Link' */
	public final static String		DEFAULT_LINK			= "45";


	// LINK NAMES
	/** Holds a string representation of the RESPONDS_TO_LINK type*/
	public final static String	sRESPONDSTOLINK			= "Responds To Link";

	/** Holds a string representation of the SUPPORTS_LINK type*/
	public final static String	sSUPPORTSLINK			= "Supports Link";

	/** Holds a string representation of the OBJECTS_TO_LINK type*/
	public final static String	sOBJECTSTOLINK			= "Objects To Link";

	/** Holds a string representation of the CHALLENGES_LINK type*/
	public final static String	sCHALLENGESLINK			= "Challenges Link";

	/** Holds a string representation of the SPECIALIZES_LINK type*/
	public final static String	sSPECIALIZESLINK		= "Specializes Link";

	/** Holds a string representation of the EXPANDS_ON_LINK type*/
	public final static String	sEXPANDSONLINK			= "Expands On Link";

	/** Holds a string representation of the RELATED_TO_LINK type*/
	public final static String	sRELATEDTOLINK			= "Related To Link";

	/** Holds a string representation of the ABOUT_LINK type*/
	public final static String	sABOUTLINK				= "About Link";

	/** Holds a string representation of the RESOLVES_LINK type*/
	public final static String	sRESOLVESLINK			= "Resolves Link";

	/** Holds a string representation of the default link type - currently the RELATED_TO_LINK.*/
	public final static String	sDEFAULTLINK			= "Related To Link";

	// LINK LABELS
	/** Holds a string representation of the RESPONDS_TO_LINK type*/
	public final static String	sRESPONDSTOLINKLABEL	= "Responds To";

	/** Holds a string representation of the SUPPORTS_LINK type*/
	public final static String	sSUPPORTSLINKLABEL		= "Supports";

	/** Holds a string representation of the OBJECTS_TO_LINK type*/
	public final static String	sOBJECTSTOLINKLABEL		= "Objects To";

	/** Holds a string representation of the CHALLENGES_LINK type*/
	public final static String	sCHALLENGESLINKLABEL	= "Challenges";

	/** Holds a string representation of the SPECIALIZES_LINK type*/
	public final static String	sSPECIALIZESLINKLABEL	= "Specializes";

	/** Holds a string representation of the EXPANDS_ON_LINK type*/
	public final static String	sEXPANDSONLINKLABEL			= "Expands On";

	/** Holds a string representation of the RELATED_TO_LINK type*/
	public final static String	sRELATEDTOLINKLABEL			= "Related To";

	/** Holds a string representation of the ABOUT_LINK type*/
	public final static String	sABOUTLINKLABEL				= "About";

	/** Holds a string representation of the RESOLVES_LINK type*/
	public final static String	sRESOLVESLINKLABEL			= "Resolves";


	// ARROW TYPES
	/** Link with no arrow heads */
	public final static int 	NO_ARROW				= 0;

	/** Link with arrow head at the to Node end */
	public final static int 	ARROW_TO				= 1;

	/** Link with arrow head at the from Node end */
	public final static int 	ARROW_FROM				= 2;

	/** Link with arrow heads at both ends of the link */
	public final static int 	ARROW_TO_AND_FROM		= 3;


	// DATABASE RECORD STATUS TYPES FOR NODES/LINKS/VIEWS AND THEIR RELATED RECORDS
	/** Node / Link / View are active */
	public final static int 	STATUS_ACTIVE			=	0;

	/** Node / Link / View have been marked for deletion */
	public final static int 	STATUS_DELETE			=	3;

	// DATABASE RECORD STATUS TYPES FOR MEETING RECORDS
	/** Meetings have had map created */
	public final static int 	STATUS_PREPARED			=	0;

	/** Meetings have been recorded */
	public final static int 	STATUS_RECORDED			=	1;

	// CONNECTION TYPES
	/** Indicates a MySQL connection type.*/
	public final static int		MYSQL_CONNECITON		=	0;

	/** Indicates a Jabber connection type.*/
	public final static int 	JABBER_CONNECTION		=	1;

	/** Indicates an IX Panel Connection type.*/
	public final static int		IX_CONNECTION			= 	2;
}
