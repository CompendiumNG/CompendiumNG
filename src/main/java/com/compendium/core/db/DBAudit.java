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

package com.compendium.core.db;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;
import java.util.*;
import java.awt.Dimension;

import com.compendium.core.datamodel.*;
import com.compendium.core.db.management.*;
import com.compendium.core.CoreUtilities;
import com.compendium.core.ICoreConstants;

/**
 * THIS CLASS IS CURRENTLY NOT USED AND THEREFORE ITS INTERFACE CANNOT BE GUARENTEED
 *
 * DBAudit defines the format for the saving audit information, for each record type, in the Audit table.
 * The data of each record audited is stored as an XML String.
 *
 * @author	Michelle Bachler
 */
public class DBAudit {

	// AUDIT ACTION TYPES

	/** Represents the base Audit Action type of NONE.*/
	public final static int ACTION_NONE		=	0;

	/** Represents the audit Action type for Adding a new Record.*/
	public final static int ACTION_ADD		=	1;

	/** Represents the audit Action type for Editing a Record.*/
	public final static int ACTION_EDIT		=	2;

	/** Represents the audit Action type for Marking a Record for Deletion.*/
	public final static int ACTION_DELETE	=	3;

	/** Represents the audit Action type for Deleting a Record.*/
	public final static int ACTION_PURGE	=	4;

	/** Represents the audit Action type for Marking a Record as Active.*/
	public final static int ACTION_RESTORE	=	5;

	/** Indicates if auditing is on.*/
	private static boolean AUDIT_ON			=  false;

	/** Holds the id of the current user.*/
	private static String CURRENT_USER_ID	=  "";

	/** Insert a new audit record.*/
	public final static String INSERT_AUDIT_QUERY =
		"INSERT INTO Audit (AuditID, Author, ItemID, AuditDate, Category, Action, Data) "+
		"VALUES (?, ?, ?, ?, ?, ?, ?)";


	/**
	 * Set auditing on and the current user id.
	 *
	 * @param sUserID, the id of the current user.
	 */
	public static void initalizeAudit(String sUserID) {
		AUDIT_ON = true;
		CURRENT_USER_ID = sUserID;
	}

	/**
	 * Set auditing on.
	 *
	 * @param auditOn, true to set auditing on, false to set it off.
	 */
	public static void setAuditOn(boolean auditOn) {
		AUDIT_ON = auditOn;
	}

	/**
	 * Return whether auditing is on or off.
	 *
	 * @return  boolean, true if auditing on, else false.
	 */
	public static boolean getAuditOn() {
		return AUDIT_ON;
	}

	/**
	 * Set the current user id.
	 *
	 * @param sUserID, the id of the current user.
	 */
	public static void setCurrentUserID(String sUserID) {
		CURRENT_USER_ID = sUserID;
	}

	/**
	 * Return the current user id.
	 *
	 * @return String, the current user id.
	 */
	public static String getCurrentUserID() {
		return CURRENT_USER_ID;
	}


	/**
	 *  Add a new audit record to the Audit table.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param sItemID, the id of object being audited.
	 *	@param sCategory, the table name for the record type.
	 * 	@param nAction, the action type of this audit.
	 * 	@param sData, An XML string with the data of the record being audited.
	 *	@return boolean, true if it was successful, else false.
	 *	@throws java.sql.SQLException
	 */
	public static boolean addAudit(DBConnection dbcon, String sItemID, String sCategory, int nAction, String sData) throws SQLException {

		Connection con = dbcon.getConnection();
		if (con == null)
			return false;

		String sAuditID = Model.getStaticUniqueID();
		String sUserID = CURRENT_USER_ID;

		double date = new Long(new Date().getTime()).doubleValue();

		String statement = "INSERT INTO Audit (AuditID, Author, ItemID, AuditDate, Category, Action, Data) "+
				"VALUES ('"+sAuditID+"','"+sUserID+"','"+sItemID+"',"+date+",'"+sCategory+"',"+nAction+",'"+sData+"')";

		Statement pstmt = con.createStatement();

		int nRowCount = pstmt.executeUpdate(statement);
		pstmt.close() ;

		if (nRowCount > 0) {
			return true;
		}
		else
			return false;
	}

	/**
	 *  Add a new Audit record for a Node record.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 * 	@param nAction, the action type of this audit.
	 * 	@param nodeSummary com.compendium.core.datamodel.NodeSummary, the node being audited.
	 *	@return boolean, true if it was successful, else false.
	 *	@throws java.sql.SQLException
	 */
	public static boolean auditNode(DBConnection dbcon, int nAction, NodeSummary nodeSummary) throws SQLException {

		/* DATABASE 'Node' TABLE FOR REFERENCE
			NodeID				= Text 50
			NodeType			= Number Byte
			ExtendedNodeType	= Text 50
			OriginalID			= Text 50
			Author				= Text 50
			CreationDate		= Number Double
			ModificationDate	= Number Double
			Label				= Text 100
			Detail				= Memo
			CurrentStatus		= Number
		*/

		String id = nodeSummary.getId();
		int type = nodeSummary.getType();
		String extendedType = nodeSummary.getExtendedNodeType();
		String sOriginalID = nodeSummary.getOriginalID();

		String author = nodeSummary.getAuthor();
		author = CoreUtilities.cleanXMLText(author);

		Date creationDate = nodeSummary.getCreationDate();
		long creationDateSecs = creationDate.getTime();

		Date modificationDate = nodeSummary.getModificationDate();
		long modificationDateSecs = modificationDate.getTime();

		String label = nodeSummary.getLabel();
		label = CoreUtilities.cleanXMLText(label);

		String detail = nodeSummary.getDetail();
		if (detail.equals(ICoreConstants.NODETAIL_STRING))
			detail = "";

		detail = CoreUtilities.cleanXMLText(detail);

		int state = nodeSummary.getState();

		String source = "";
		try {
			nodeSummary.getSource();
			source = CoreUtilities.cleanXMLText(source);
		}
		catch(Exception ex) {}

		//String parentID = "";
		//if (nodeSummary.getParentNode() != null)
		//	parentID = (nodeSummary.getParentNode()).getId();
		//int permission = nodeSummary.getPermission();
		//int viewCount = nodeSummary.getViewCount();
		//IModel nodel = nodeSummary.getModel();
		//PCSession session = nodeSummary.getSession();

		StringBuffer xmlNode = new StringBuffer(500);

		xmlNode.append("<node ");

		xmlNode.append("id=\""+ id +"\" ");
		xmlNode.append("type=\""+ type +"\" ");
		xmlNode.append("extendedtype=\""+ extendedType +"\" ");
		xmlNode.append("originalid=\""+ sOriginalID +"\" ");
		xmlNode.append("author=\""+ author +"\" ");
		xmlNode.append("created=\""+ creationDateSecs +"\" ");
		xmlNode.append("lastModified=\""+ modificationDateSecs +"\" ");
		xmlNode.append("label=\""+ label +"\" ");
		xmlNode.append("state=\""+ state +"\">\n");

		xmlNode.append("<detail>"+ detail +"</detail>\n");
		xmlNode.append("<source>"+ source +"</source>\n");

		xmlNode.append("</node>");

		return addAudit(dbcon, id, "Node", nAction, xmlNode.toString());
	}

	/**
	 *  Add a new Audit record for a NodeDetail record.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 * 	@param nAction, the action type of this audit.
	 * 	@param detail com.compendium.core.datamodel.NodeDetailPage, the node detail page being audited.
	 * 	@param sAuthor, the author of the audit action.
	 *	@return boolean, true if it was successful, else false.
	 *	@throws java.sql.SQLException
	 */
	public static boolean auditNodeDetail(DBConnection dbcon, int nAction, NodeDetailPage detail, String sAuthor) throws SQLException {

		/* DATABASE 'NodeDetail' TABLE FOR REFERENCE
			NodeID					= Text 50
			Author					= Text 50
			PageNo					= Integer
			CreationDate			= Number Double
			ModificationDate		= Number Double
			NodeDetail				= Memo
		*/

		String id = detail.getNodeID();
		long creationDateSecs = detail.getCreationDate().getTime();
		long modifiedDateSecs = detail.getModificationDate().getTime();

		String detailText = (String)detail.getText();
		if (detailText.equals(ICoreConstants.NODETAIL_STRING))
			detailText = "";
		detailText = CoreUtilities.cleanXMLText(detailText);

		StringBuffer xmlNode = new StringBuffer(500);

		xmlNode.append("<node ");

		xmlNode.append("id=\""+ id +"\" ");
		xmlNode.append("author=\""+ sAuthor +"\" ");
		xmlNode.append("pageNo=\""+ detail.getPageNo() +"\" ");
		xmlNode.append("created=\""+ creationDateSecs +"\" ");
		xmlNode.append("lastModified=\""+ modifiedDateSecs +"\" ");
		xmlNode.append("<detail>"+ detailText +"</detail>\n");
		xmlNode.append("</node>");

		return addAudit(dbcon, id, "NodeDetail", nAction, xmlNode.toString());
	}

	/**
	 *  Add a new Audit record for a Link record.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 * 	@param nAction, the action type of this audit.
	 * 	@param link com.compendium.core.datamodel.Link, the link being audited.
	 *	@return boolean, true if it was successful, else false.
	 *	@throws java.sql.SQLException
	 */
	public static boolean auditLink(DBConnection dbcon, int nAction, Link link) throws SQLException {

		String sFromID = (link.getFrom()).getId();
		String sToID = (link.getTo()).getId();

		return auditLink(dbcon, nAction, link, sFromID, sToID);
	}

	/**
	 *  Add a new Audit record for a Link record.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 * 	@param nAction, the action type of this audit.
	 * 	@param link com.compendium.core.datamodel.Link, the link being audited.
	 * 	@param sFromID, the id of the link's originating node.
	 * 	@param sToID, the id of the link's destination node.
	 *	@return boolean, true if it was successful, else false.
	 *	@throws java.sql.SQLException
	 */
	public static boolean auditLink(DBConnection dbcon, int nAction, Link link, String sFromID, String sToID) throws SQLException {

		/* DATABASE 'Link' TABLE FOR REFERENCE
			LinkID				= Text 50
			CreationDate		= Number Double
			ModificationDate	= Number Double
			Author				= Text 50
			Type				= Number Byte
			OriginalID			= Text 50
			FromNode			= Text 50
			ToNode				= Text 50
			ViewID				= NOT USED
			Label				= MEMO
			arrow				= Number Double
		*/

		String id = link.getId();

		Date creationDate = link.getCreationDate();
		long creationDateSecs = creationDate.getTime();

		Date modificationDate = link.getModificationDate();
		long modificationDateSecs = modificationDate.getTime();

		String author = link.getAuthor();
		author = CoreUtilities.cleanXMLText(author);

		String linkType = link.getType();
		String sOriginalID = link.getOriginalID();
		String sLabel = link.getLabel();

		//int permission = link.getPermission();

		StringBuffer xmlLink = new StringBuffer(500);

		xmlLink.append("<link ");

		xmlLink.append("id=\""+ id +"\" ");
		xmlLink.append("created=\""+ creationDateSecs +"\" " );
		xmlLink.append("lastModified=\""+ modificationDateSecs +"\" " );
		xmlLink.append("author=\""+ author +"\" " );
		xmlLink.append("type=\""+ linkType +"\" " );
		xmlLink.append("originalid=\""+ sOriginalID +"\" ");
		xmlLink.append("from=\""+ sFromID +"\" ");
		xmlLink.append("to=\""+ sToID +"\" ");
		xmlLink.append("label=\""+ sLabel +"\">");

		xmlLink.append("</link>");

		return addAudit(dbcon, id, "Link", nAction, xmlLink.toString());
	}

	/**
	 *  Add a new Audit record for a Code record.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 * 	@param nAction, the action type of this audit.
	 * 	@param link com.compendium.core.datamodel.Code, the code being audited.
	 *	@return boolean, true if it was successful, else false.
	 *	@throws java.sql.SQLException
	 */
	public static boolean auditCode(DBConnection dbcon, int nAction, Code code) throws SQLException {

		/* DATABASE 'Code' TABLE FOR REFERENCE
			CodeID				= Text 50
			Author				= Text 50
			CreationDate		= Number Double
			ModificationDate	= Number Double
			Name				= Text 50
			Description			= Text 100
			Behaviour			= Text 255

			CodeGroupID 		= Text 50 ??? NOT ADDED YET
		*/

		Vector codeData = new Vector(3);
		String id = code.getId();

		String author = code.getAuthor();
		author = CoreUtilities.cleanXMLText(author);

		Date creationDate = code.getCreationDate();
		long creationDateSecs = creationDate.getTime();

		Date modificationDate = code.getModificationDate();
		long modificationDateSecs = modificationDate.getTime();

		String codeName = code.getName();
		codeName = CoreUtilities.cleanXMLText(codeName);
		String codeDescription = code.getDescription();
		codeDescription = CoreUtilities.cleanXMLText(codeDescription);
		String codeBehavior = code.getBehavior();
		codeBehavior = CoreUtilities.cleanXMLText(codeBehavior);

		StringBuffer xmlCode = new StringBuffer(500);

		xmlCode.append("<code ");

		xmlCode.append("id=\""+ id +"\" ");
		xmlCode.append("author=\""+ author +"\" ");
		xmlCode.append("created=\""+ creationDateSecs +"\" ");
		xmlCode.append("lastModified=\""+ modificationDateSecs +"\" " );
		xmlCode.append("name=\""+ codeName +"\" ");
		xmlCode.append("description=\""+ codeDescription +"\" ");
		xmlCode.append("behavior=\""+ codeBehavior +"\">");

		xmlCode.append("</code>");

		return addAudit(dbcon, id, "Code", nAction, xmlCode.toString());
	}

	/**
	 *  Add a new Audit record for a ViewNode record.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 * 	@param nAction the action type of this audit.
	 * 	@param nodePos the NodePosition being audited.
	 *	@return boolean true if it was successful, else false.
	 *	@throws java.sql.SQLException
	 */
	public static boolean auditViewNode(DBConnection dbcon, int nAction, NodePosition nodePos) throws SQLException {

		/* DATBASE 'ViewNode' TABLE FOR REFERENCE
			ViewID	= Text 50
			NodeID	= Text 50
			XPos	= Integer
			YPos	= Integer
			CreationDate = DOUBLE
			ModificationDate = DOUBLE
			CurrentStatus = INTEGER
			ShowTags ENUM('N','Y')
			ShowText ENUM('N','Y')
			ShowTrans ENUM('N','Y')
			ShowWeight ENUM('N','Y')
			SmallIcon ENUM('N','Y')
			HideIcon ENUM('N','Y')
			LabelWrapWidth INTEGER
			FontSize INTEGER
			FontFace VARCHAR(100)
			FontStyle INTEGER
			Foreground INTEGER
			Background INTEGER																																	
		*/

		NodeSummary node = nodePos.getNode();
		View nodeView = nodePos.getView();

		StringBuffer xmlView = new StringBuffer(500);

		xmlView.append("<viewnode ");

		String viewid = nodeView.getId();

		xmlView.append("viewref=\""+ viewid +"\" ");
		xmlView.append("noderef=\""+ node.getId() +"\" ");
		xmlView.append("XPosition=\""+ nodePos.getXPos() +"\" ");
		xmlView.append("YPosition=\""+ nodePos.getYPos() +"\"" );
		xmlView.append("created=\""+ (nodePos.getCreationDate()).getTime() +"\" ");
		xmlView.append("lastModified=\""+ (nodePos.getModificationDate()).getTime() +"\"");
		xmlView.append("showTags=\""+ nodePos.getShowTags() +"\"");
		xmlView.append("showText=\""+ nodePos.getShowText() +"\"");
		xmlView.append("showTrans=\""+ nodePos.getShowTrans() +"\"");
		xmlView.append("showWeight=\""+ nodePos.getShowWeight() +"\"");
		xmlView.append("smallIcons=\""+ nodePos.getShowSmallIcon() +"\"");
		xmlView.append("hideIcons=\""+ nodePos.getHideIcon() +"\"");
		xmlView.append("labelWrapWidth=\""+ nodePos.getLabelWrapWidth() +"\"");
		xmlView.append("fontSize=\""+ nodePos.getFontSize() +"\"");
		xmlView.append("fontFace=\""+ nodePos.getFontFace() +"\"");
		xmlView.append("fontStyle=\""+ nodePos.getFontStyle() +"\"");
		xmlView.append("foreground=\""+ nodePos.getForeground() +"\"");
		xmlView.append("background=\""+ nodePos.getBackground() +"\" >");

		xmlView.append("</viewnode>");

		return addAudit(dbcon, viewid, "ViewNode", nAction, xmlView.toString());
	}

	/**
	 *  Add a new Audit record for a Link record.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 * 	@param nAction, the action type of this audit.
	 * 	@param sNodeID, the id of the node the code is being added to.
	 * 	@param sCodeID, the id of the code being added.
	 *	@return boolean, true if it was successful, else false.
	 *	@throws java.sql.SQLException
	 */
	public static boolean auditNodeCode(DBConnection dbcon, int nAction, String sNodeID, String sCodeID) throws SQLException {
		/*
		  DATABASE 'NodeCode' TABLE
			NodeID			= Text 50
			CodeID			= Text 50
		*/

		StringBuffer xmlNodeCode = new StringBuffer(200);

		xmlNodeCode.append("<nodecode ");
		xmlNodeCode.append("noderef=\""+ sNodeID +"\" ");
		xmlNodeCode.append("coderef=\""+ sCodeID +"\">");
		xmlNodeCode.append("</nodecode>");

		return addAudit(dbcon, sNodeID, "NodeCode", nAction, xmlNodeCode.toString());
	}

	/**
	 *  Add a new Audit record for a ReferenceNode record.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 * 	@param nAction, the action type of this audit.
	 * 	@param sNodeID, the id of the reference node.
	 * 	@param sSource, the reference source path for the node.
	 * 	@param sImage, the image for the node.
	 *	@return boolean, true if it was successful, else false.
	 *	@throws java.sql.SQLException
	 */
	public static boolean auditReferenceNode(DBConnection dbcon, int nAction, String sNodeID, String sSource, String sImage, Dimension oImageSize) throws SQLException {
		/*
		  DATABASE 'ReferenceNode' TABLE
			NodeID			= Text 50
			Source			= Text 250
			ImageSource		= Text 250
			ImageWidth		= INT
			ImageHeight		= INT
		*/

		StringBuffer xmlReferenceNode = new StringBuffer(200);

		xmlReferenceNode.append("<referencenode ");
		xmlReferenceNode.append("noderef=\""+ sNodeID +"\" ");
		xmlReferenceNode.append("source=\""+ sSource +"\" ");
		xmlReferenceNode.append("image=\""+ sImage +"\" ");
		xmlReferenceNode.append("imagewidth=\"0\" ");
		xmlReferenceNode.append("imageheight=\"0\">");				
		xmlReferenceNode.append("</referencenode>");

		return addAudit(dbcon, sNodeID, "ReferenceNode", nAction, xmlReferenceNode.toString());
	}

	/**
	 *  Add a new Audit record for a ShortCutNode record.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 * 	@param nAction, the action type of this audit.
	 * 	@param sNodeID, the id of the shortcut node.
	 * 	@param sReferenceID, the id of shortcut node it references.
	 *	@return boolean, true if it was successful, else false.
	 *	@throws java.sql.SQLException
	 */
	public static boolean auditShortcutNode(DBConnection dbcon, int nAction,
												String sNodeID, String sReferenceID) throws SQLException {
		/*
		  DATABASE 'ShortcutNode' TABLE
			NodeID			= Text 50
			ReferenceID		= Text 50
		*/


		StringBuffer xmlShortcutNode = new StringBuffer(200);

		xmlShortcutNode.append("<referencenode ");
		xmlShortcutNode.append("noderef=\""+ sNodeID +"\" ");
		xmlShortcutNode.append("referenceref=\""+ sReferenceID +"\">");
		xmlShortcutNode.append("</referencenode>");

		return addAudit(dbcon, sNodeID, "ShortcutNode", nAction, xmlShortcutNode.toString());
	}

	/**
	 *  Add a new Audit record for a ViewLink record.
	 *
	 *	@param dbcon the DBConnection object to access the database with.
	 * 	@param nAction the action type of this audit.
	 * 	@param props the link view properties to store.
	 *	@return boolean true if it was successful, else false.
	 *	@throws java.sql.SQLException
	 */
	public static boolean auditViewLink(DBConnection dbcon, int nAction, LinkProperties props) throws SQLException {
		/*
		  DATABASE 'ViewLink' TABLE
			ViewID			= Text 50
			LinkID			= Text 50

			ArrowType INTEGER
			LinkStyle INTEGER
			LinkDashed INTEGER
		 	LinkWeight INTEGER
			LinkColour INTEGER

			LabelWrapWidth INTEGER
			FontSize INTEGER
			FontFace VARCHAR(100)
			FontStyle INTEGER
			Foreground INTEGER
			Background INTEGER																																	
		*/

		StringBuffer xmlViewLink = new StringBuffer(200);
		xmlViewLink.append("<viewlink ");
		xmlViewLink.append("viewref=\""+ props.getView().getId() +"\" ");
		xmlViewLink.append("linkref=\""+ props.getLink().getId() +"\"");
		xmlViewLink.append("labelWrapWidth=\""+ props.getLabelWrapWidth() +"\"");
		
		xmlViewLink.append("arrowType=\""+ props.getArrowType() +"\"");
		xmlViewLink.append("linkStyle=\""+ props.getLinkStyle() +"\"");
		xmlViewLink.append("linkDashed=\""+ props.getLinkDashed() +"\"");
		xmlViewLink.append("linkWeight=\""+ props.getLinkWeight() +"\"");
		xmlViewLink.append("linkColour=\""+ props.getLinkColour() +"\"");
		
		xmlViewLink.append("fontSize=\""+ props.getFontSize() +"\"");
		xmlViewLink.append("fontFace=\""+ props.getFontFace() +"\"");
		xmlViewLink.append("fontStyle=\""+ props.getFontStyle() +"\"");
		xmlViewLink.append("foreground=\""+ props.getForeground() +"\"");
		xmlViewLink.append("background=\""+ props.getBackground() +"\">");
		
		xmlViewLink.append("</viewlink>");

		return addAudit(dbcon, props.getView().getId(), "ViewLink", nAction, xmlViewLink.toString());
	}

	/**
	 *  Add a new Audit record for a MediaIndex record.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 * 	@param nAction, the action type of this audit.
	 * 	@param oMediaIndex, the MediaIndex whose data to audit.
	 *	@return boolean, true if it was successful, else false.
	 *	@throws java.sql.SQLException
	 */
	public static boolean auditMediaIndex(DBConnection dbcon, int nAction, MediaIndex oMediaIndex) throws SQLException {

		/* DATABASE 'MediaIndex' TABLE FOR REFERENCE
			ViewID					= Text 50
			NodeID					= Text 50
			MeetingID				= Text 255
			MediaIndex				= Number Double
			CreationDate			= Number Double
			ModificationDate		= Number Double
		*/

		StringBuffer xmlNode = new StringBuffer(500);

		xmlNode.append("<mediaindex ");

		xmlNode.append("viewref=\""+ oMediaIndex.getViewID() +"\" ");
		xmlNode.append("noderef=\""+ oMediaIndex.getNodeID() +"\" ");
		xmlNode.append("meetingref=\""+ oMediaIndex.getMeetingID() +"\" ");
		xmlNode.append("mediaindex=\""+ oMediaIndex.getMediaIndex().getTime() +"\" ");
		xmlNode.append("created=\""+ oMediaIndex.getCreationDate().getTime() +"\" ");
		xmlNode.append("lastModified=\""+ oMediaIndex.getModificationDate().getTime() +"\"");
		xmlNode.append("</mediaindex>");

		return addAudit(dbcon, oMediaIndex.getNodeID(), "MediaIndex", nAction, xmlNode.toString());
	}

	/**
	 *  Add a new Audit record for a Meeting record.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 * 	@param nAction, the action type of this audit.
	 *	@param sMeetingID, the Meeting Id if the meeting.
	 *	@param sMeetingMapID, the id of the main parent meeting map
	 *	@param sMeetingName, the name the meeting is known as in Compendium.
	 *	@param dMeetingDate, the date of the meeting.
	 *	@return boolean, true if it was successful, else false.
	 *	@throws java.sql.SQLException
	 */
	public static boolean auditMeeting(DBConnection dbcon, int nAction, String sMeetingID, String sMeetingMapID, String sMeetingName, Date dMeetingDate, int nStatus) throws SQLException {

		/* DATABASE 'Meeting' TABLE FOR REFERENCE
			MeetingID				= Text 255
			NeetingMapID			= Text 50
			MeetingName				= Text 255
			MeetingDate				= Number Double
			CurrentStatus			= Integer
		*/

		StringBuffer xmlNode = new StringBuffer(500);

		xmlNode.append("<meeting ");

		xmlNode.append("meetingref=\""+ sMeetingID +"\" ");
		xmlNode.append("meetingmapref=\""+ sMeetingMapID +"\" ");
		xmlNode.append("meetingname=\""+ sMeetingName +"\" ");
		xmlNode.append("meetingdate=\""+ dMeetingDate.getTime() +"\" ");
		xmlNode.append("meetingstatus=\""+String.valueOf(nStatus)+"\"");
		xmlNode.append("</meeting>");

		return addAudit(dbcon, sMeetingMapID, "Meeting", nAction, xmlNode.toString());
	}

	/**
	 *  Add a new Audit record for a CodeGroup record.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 * 	@param nAction, the action type of this audit.
	 * 	@param sCodeGroupID, the id of the code group.
	 * 	@param sAuthor, the author of the CodeGroup.
	 *	@param sName, the name of the CodeGroup.
	 * 	@param sDescription, the description of the CodeGroup.
	 *	@param created, the creation date of this record in milliseconds.
	 * 	@param modified, the last mnodification date of this record in milliseconds.
	 *	@return boolean, true if it was successful, else false.
	 *	@throws java.sql.SQLException
	 */
	public static boolean auditCodeGroup(DBConnection dbcon, int nAction, String sCodeGroupID, String sAuthor, String sName,
											String sDescription, double created, double modified) throws SQLException {

		/* DATBASE 'CodeGroup' TABLE FOR REFERENCE
			CodeGroupID			= Text 50
			UserID				= Text 50
			Name				= Text 100
			Description			= Text 255
			CreationDate		= Number Double
			ModificationDate	= Number Double
		*/

		StringBuffer xmlCodeGroup = new StringBuffer(300);

		xmlCodeGroup.append("<codegroup ");

		xmlCodeGroup.append("codegroupref=\""+ sCodeGroupID +"\" ");
		xmlCodeGroup.append("author=\""+ sAuthor +"\" ");
		xmlCodeGroup.append("name=\""+ sName +"\" ");
		xmlCodeGroup.append("description=\""+ sDescription +"\" ");
		xmlCodeGroup.append("created=\""+ created +"\"" );
		xmlCodeGroup.append("lastModified=\""+ modified +"\">");

		xmlCodeGroup.append("</codegroup>");

		return addAudit(dbcon, sCodeGroupID, "CodeGroup", nAction, xmlCodeGroup.toString());
	}

	/**
	 *  Add a new Audit record for a GroupCode record.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 * 	@param nAction, the action type of this audit.
	 * 	@param sCodeID, the id of the code GroupCode record.
	 * 	@param sCodeGroupID, the id of the code group of the GroupCode record.
	 * 	@param sAuthor, the author of the GroupCode.
	 *	@param created, the creation date of this record in milliseconds.
	 * 	@param modified, the last mnodification date of this record in milliseconds.
	 *	@return boolean, true if it was successful, else false.
	 *	@throws java.sql.SQLException
	 */
	public static boolean auditGroupCode(DBConnection dbcon, int nAction, String sCodeID, String sCodeGroupID,
											String sAuthor, double created, double modified) throws SQLException {

		/* DATBASE 'GroupCode' TABLE FOR REFERENCE
			CodeID				= Text 50
			CodeGroupID			= Text 50
			UserID				= Text 50
			CreationDate		= Number Double
			ModificationDate	= Number Double
		*/

		StringBuffer xmlGroupCode = new StringBuffer(300);

		xmlGroupCode.append("<groupcode ");

		xmlGroupCode.append("coderef=\""+ sCodeID +"\" ");
		xmlGroupCode.append("codegroupref=\""+ sCodeGroupID +"\" ");
		xmlGroupCode.append("author=\""+ sAuthor +"\" ");
		xmlGroupCode.append("created=\""+ created +"\"" );
		xmlGroupCode.append("lastModified=\""+ modified +"\">" );

		xmlGroupCode.append("</groupcode>\n");

		return addAudit(dbcon, sCodeID, "GroupCode", nAction, xmlGroupCode.toString());
	}

	/**
	 *  Add a new Audit record for a System record.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 * 	@param nAction, the action type of this audit.
	 * 	@param sProperty, the property value for the System record.
	 * 	@param sContents, the contents value for the System record.
	 *	@return boolean, true if it was successful, else false.
	 *	@throws java.sql.SQLException
	 */
	public static boolean auditSystem(DBConnection dbcon, int nAction,
										String sProperty, String sContents) throws SQLException {

		/* DATBASE 'System' TABLE FOR REFERENCE
			Property		= Text 100
			Contents		= Text 255
		*/

		StringBuffer xmlSystem = new StringBuffer(300);

		xmlSystem.append("<system ");
		xmlSystem.append("property=\""+ sProperty +"\" ");
		xmlSystem.append("contents=\""+ sContents +"\">");
		xmlSystem.append("</system>\n");

		return addAudit(dbcon, "", "System", nAction, xmlSystem.toString());
	}

	/**
	 *  Add a new Audit record for a Favorite record.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 * 	@param nAction the action type of this audit.
	 *  @param fav the favorite object to audit.
	 *	@return boolean true if it was successful, else false.
	 *	@throws java.sql.SQLException
	 */
	public static boolean auditFavorite(DBConnection dbcon, int nAction, Favorite fav) throws SQLException {

		/* DATBASE 'Favorite' TABLE FOR REFERENCE
			UserID					= Text 50
			NodeID					= Text 50
			ViewID					= Text 50
			Label					= Text 250
			Type					= Integer
			CreationDate			= Number Double
			ModificationDate		= Number Double
		*/

		String sID = "";
		StringBuffer xmlFavorite = new StringBuffer(300);

		xmlFavorite.append("<favorite ");

		xmlFavorite.append("userref=\""+ fav.getUserID() +"\" ");
		xmlFavorite.append("noderef=\""+ fav.getNodeID() +"\" ");
		xmlFavorite.append("viewref=\""+ fav.getViewID() +"\" ");		
		xmlFavorite.append("label=\""+ fav.getLabel() +"\" ");
		xmlFavorite.append("type=\""+ fav.getType() +"\" ");
		xmlFavorite.append("created=\""+ (fav.getCreationDate()).getTime() +"\" ");
		xmlFavorite.append("last Modified=\""+ (fav.getModificationDate()).getTime() +"\">");

		xmlFavorite.append("</favorite>");

		return addAudit(dbcon, sID, "Favorite", nAction, xmlFavorite.toString());
	}
	
	/**
	 *  Add a new Audit record for a ViewProperty record.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 * 	@param nAction, the action type of this audit.
	 * 	@param sUserID, the id of the user whose ViewProperty record this is.
	 * 	@param com.compendium.core.datamodel.ViewProperty, the ViewProperty record being audited.
	 *	@return boolean, true if it was successful, else false.
	 *	@throws java.sql.SQLException
	 */
	public static boolean auditViewProperty(DBConnection dbcon, int nAction, String sUserID, ViewProperty view) throws SQLException {

		/* DATBASE 'ViewProperty' TABLE FOR REFERENCE
			UserID					= Text 50
			ViewID					= Text 50
			HorizontalScroll		= Integer
			VerticalScroll			= Integer
			Width					= Integer
			Height					= Integer
			XPosition				= Integer
			YPosition				= Integer
			IsIcon					= YesNo
			IsMaximum				= YesNo
		*/

		StringBuffer xmlViewProperty = new StringBuffer(300);

		xmlViewProperty.append("<viewproperty ");

		xmlViewProperty.append("userref=\""+ sUserID +"\" ");
		xmlViewProperty.append("viewref=\""+ view.getViewID() +"\" ");
		xmlViewProperty.append("vscroll=\""+ view.getVerticalScrollBarPosition() +"\" ");
		xmlViewProperty.append("hscroll=\""+ view.getHorizontalScrollBarPosition() +"\" ");
		xmlViewProperty.append("width=\""+ view.getWidth() +"\" ");
		xmlViewProperty.append("height=\""+ view.getHeight() +"\" ");
		xmlViewProperty.append("xpos=\""+ view.getXPosition() +"\" ");
		xmlViewProperty.append("ypos=\""+ view.getYPosition() +"\" ");
		xmlViewProperty.append("isicon=\""+ view.getIsIcon() +"\" ");
		xmlViewProperty.append("ismaximum=\""+ view.getIsMaximum() +"\">");

		xmlViewProperty.append("</viewproperty>");

		return addAudit(dbcon, sUserID, "ViewProperty", nAction, xmlViewProperty.toString());
	}

	/**
	 *  Add a new Audit record for a ViewLayer record.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 * 	@param nAction, the action type of this audit.
	 * 	@param sUserID, the id of the user whose ViewLayer record this is.
	 * 	@param view, the ViewLayer record being audited.
	 *	@return boolean, true if it was successful, else false.
	 *	@throws java.sql.SQLException
	 */
	public static boolean auditViewLayer(DBConnection dbcon, int nAction, ViewLayer view) throws SQLException {

		/* DATBASE 'ViewProperty' TABLE FOR REFERENCE
			ViewID					= Text 50
			Scribble    			= longtext
			Background  			= varchar(255)
			Grid        			= varchar(255)
			Shapes      			= longtext
			Backgroundcolor			= Int
		*/

		StringBuffer xmlViewLayer = new StringBuffer(300);

		xmlViewLayer.append("<viewlayer ");

		xmlViewLayer.append("viewref=\""+ view.getViewID() +"\" ");
		xmlViewLayer.append("scribble=\""+ view.getScribble() +"\" ");
		xmlViewLayer.append("background=\""+ view.getBackgroundImage() +"\" ");
		xmlViewLayer.append("grid=\""+ view.getGrid() +"\" ");
		xmlViewLayer.append("shapes=\""+ view.getShapes() +"\" ");
		xmlViewLayer.append("backgroundcolor=\""+ view.getBackgroundColor() +"\">");

		xmlViewLayer.append("</viewlayer>");

		return addAudit(dbcon, view.getViewID(), "ViewLayer", nAction, xmlViewLayer.toString());
	}

	/**
	 *  Add a new Audit record for a Workspace record.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 * 	@param nAction, the action type of this audit.
	 * 	@param sWorkspaceID, the id of the Workspace record.
	 * 	@param sUserID, the id of the user whose Workspace this is.
	 *	@param sName, the name of this Workspace.
	 * 	@param sAuthor, the author of the Workspace.
	 *	@param created, the creation date of this record in milliseconds.
	 * 	@param modified, the last mnodification date of this record in milliseconds.
	 *	@param views, a list of the WorkspaceView objects in this Workspace.
	 *	@return boolean, true if it was successful, else false.
	 *	@throws java.sql.SQLException
	 */
	public static boolean auditWorkspace(DBConnection dbcon, int nAction, String sWorkspaceID, String sUserID,
											String sName, double created, double modified, Vector views) throws SQLException {

		/* DATBASE 'Workspace' TABLE FOR REFERENCE
			WorkspaceID				= Text 50
			UserID					= Text 50
			Name					= Text 100
			CreationDate		= Number Double
			ModificationDate	= Number Double
		*/

		String sID = "";
		StringBuffer xmlWorkspace = new StringBuffer(300);

		xmlWorkspace.append("<workspace ");

		xmlWorkspace.append("workspaceref=\""+ sWorkspaceID +"\" ");
		xmlWorkspace.append("userref=\""+ sUserID +"\" ");
		xmlWorkspace.append("name=\""+ sName +"\" ");
		xmlWorkspace.append("created=\""+ created +"\" ");
		xmlWorkspace.append("last Modified=\""+ modified +"\">\n");

		int count = views.size();
		for (int i=0; i<count; i++) {
			WorkspaceView view = (WorkspaceView)views.elementAt(i);

			xmlWorkspace.append("<workspaceview ");

			xmlWorkspace.append("workspaceref=\""+ sWorkspaceID +"\" ");
			xmlWorkspace.append("viewref=\""+ view.getViewID() +"\" ");
			xmlWorkspace.append("width=\""+ view.getWidth() +"\" ");
			xmlWorkspace.append("height=\""+ view.getHeight() +"\" ");
			xmlWorkspace.append("xpos=\""+ view.getXPosition() +"\" ");
			xmlWorkspace.append("ypos=\""+ view.getYPosition() +"\" ");
			xmlWorkspace.append("isicon=\""+ view.getIsIcon() +"\" ");
			xmlWorkspace.append("ismaximum=\""+ view.getIsMaximum() +"\"");
			xmlWorkspace.append("vscroll=\""+ view.getVerticalScrollBarPosition() +"\" ");
			xmlWorkspace.append("hscroll=\""+ view.getHorizontalScrollBarPosition() +"\">\n");

			xmlWorkspace.append("</workspaceview>");
		}

		xmlWorkspace.append("></workspace>");

		return addAudit(dbcon, sID, "Workspace", nAction, xmlWorkspace.toString());
	}

	/**
	 *  Add a new Audit record for deleting a Workspace record.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 * 	@param nAction, the action type of this audit.
	 * 	@param sUserID, the id of the user whose WorkspaceViews to delete.
	 *	@param data, the Workspace record data.
	 *	@param views, a list of the WorkspaceView objects to delete.
	 *	@return boolean, true if it was successful, else false.
	 *	@throws java.sql.SQLException
	 */
	public static boolean auditWorkspace(DBConnection dbcon, int nAction, String sUserID, Vector data, Vector views) throws SQLException {

		/* DATBASE 'Workspace' TABLE FOR REFERENCE
			WorkspaceID				= Text 50
			UserID					= Text 50
			Name					= Text 100
			CreationDate		= Number Double
			ModificationDate	= Number Double
		*/

		String sWorkspaceID = (String)data.elementAt(0);
		StringBuffer xmlWorkspace = new StringBuffer(300);

		xmlWorkspace.append("<workspace ");

		xmlWorkspace.append("workspaceref=\""+ sWorkspaceID +"\" ");
		xmlWorkspace.append("userref=\""+ sUserID +"\" ");
		xmlWorkspace.append("name=\""+ (String)data.elementAt(1) +"\" ");
		xmlWorkspace.append("created=\""+ ((Double)data.elementAt(2)).doubleValue() +"\" ");
		xmlWorkspace.append("last Modified=\""+ ((Double)data.elementAt(3)).doubleValue() +"\">\n");

		int count = views.size();
		for (int i=0; i<count; i++) {
			WorkspaceView view = (WorkspaceView)views.elementAt(i);

			xmlWorkspace.append("<workspaceview ");

			xmlWorkspace.append("workspaceref=\""+ sWorkspaceID +"\" ");
			xmlWorkspace.append("viewref=\""+ view.getViewID() +"\" ");
			xmlWorkspace.append("width=\""+ view.getWidth() +"\" ");
			xmlWorkspace.append("height=\""+ view.getHeight() +"\" ");
			xmlWorkspace.append("xpos=\""+ view.getXPosition() +"\" ");
			xmlWorkspace.append("ypos=\""+ view.getYPosition() +"\" ");
			xmlWorkspace.append("isicon=\""+ view.getIsIcon() +"\" ");
			xmlWorkspace.append("ismaximum=\""+ view.getIsMaximum() +"\"");
			xmlWorkspace.append("vscroll=\""+ view.getVerticalScrollBarPosition() +"\" ");
			xmlWorkspace.append("hscroll=\""+ view.getHorizontalScrollBarPosition() +"\">\n");

			xmlWorkspace.append("</workspaceview>\n");
		}

		xmlWorkspace.append("</workspace>");

		return addAudit(dbcon, sWorkspaceID, "Workspace", nAction, xmlWorkspace.toString());
	}

	/**
	 *  Add a new Audit record for a NodeUserState record.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 * 	@param nAction, the action type of this audit.
	 * 	@param sNodeID, the id of the node whose state record to set.
	 * 	@param sUserID, the id of the user whose node state it is.
	 *	@param state, the state of the node NodeUserState record.
	 *	@return boolean, true if it was successful, else false.
	 *	@throws java.sql.SQLException
	 */
	public static boolean auditNodeUserState(DBConnection dbcon, int nAction, String sNodeID, String sUserID, int state) throws SQLException {

		/* DATBASE 'NodeUserState' TABLE FOR REFERENCE
			NodeID					= Text 50
			UserID					= Text 50
			State					= Long Integer
		*/

		StringBuffer xmlNodeUserState = new StringBuffer(100);

		xmlNodeUserState.append("<nodeuserstate ");
		xmlNodeUserState.append("noderef=\""+ sNodeID +"\" ");
		xmlNodeUserState.append("userref=\""+ sUserID +"\" ");
		xmlNodeUserState.append("state=\""+ state +"\">");
		xmlNodeUserState.append("</nodeuserstate>");

		return addAudit(dbcon, sNodeID, "NodeUserState", nAction, xmlNodeUserState.toString());
	}

	/**
	 *  Add a new Audit record for a User record.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 * 	@param nAction, the action type of this audit.
	 * 	@param com.compendium.core.datamodel.UserProfile, the UserProfile record to audit.
	 *	@return boolean, true if it was successful, else false.
	 *	@throws java.sql.SQLException
	 */
	public static boolean auditUser(DBConnection dbcon, int nAction, UserProfile profile) throws SQLException {

		/* DATBASE 'User' TABLE FOR REFERENCE
			UserID					= Text 50
			Author					= Text 50
			CreationDate			= Number Double
			ModificationDate		= Number Double
			Login					= Text 20
			Name					= Text 50
			Password				= Text 50
			Description				= Text 255
			HomeView				= Text 50
			IsAdministrator			= YesNo
		*/

		String sID = profile.getId();
		StringBuffer xmlUser = new StringBuffer(300);

		xmlUser.append("<user ");
		xmlUser.append("userref=\""+ sID +"\" ");
		xmlUser.append("author=\""+ profile.getAuthor() +"\" ");
		xmlUser.append("name=\""+ profile.getUserName() +"\" ");
		xmlUser.append("login=\""+ profile.getLoginName() +"\" ");
		xmlUser.append("password=\""+ profile.getPassword() +"\" ");
		xmlUser.append("description=\""+ profile.getUserDescription() +"\" ");
		xmlUser.append("isadministrator=\""+ profile.isAdministrator() +"\" ");
		xmlUser.append("homeview=\""+ (profile.getHomeView()).getId() +"\" ");
		xmlUser.append("created=\""+ profile.getCreationDate() +"\" ");
		xmlUser.append("modified=\""+ profile.getModificationDate() +"\">");

		xmlUser.append("</user>");

		return addAudit(dbcon, sID, "Users", nAction, xmlUser.toString());
	}


	/**
	 *  Add a new Audit entry for a Connection record
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 * 	@param DBExternalConnection, the connection object for this audit.
	 *	@return boolean, true if it was successful, else false.
	 *	@throws java.sql.SQLException
	 */
	public static boolean auditExternalConnection(DBConnection dbcon, int nAction, ExternalConnection connection) throws SQLException {

		/* DATBASE 'UserClones' TABLE FOR REFERENCE
			UserID			= VarChar 50
			Profile			= VarChar 255
			Type			= Integer 11
			Server			= VarChar 255
			Login			= VarChar 255
			Password		= VarChar 255
			Name			= VarChar 255
			Port			= Integer 11
			Resource		= VarChar 255
		*/

		StringBuffer xmlConnections = new StringBuffer(300);
		xmlConnections.append("<connections ");
		xmlConnections.append("userref=\""+ connection.getUserID() +"\" ");
		xmlConnections.append("profile=\""+ connection.getProfile() +"\" ");
		xmlConnections.append("type=\""+ connection.getType() +"\" ");
		xmlConnections.append("server=\""+ connection.getServer() +"\" ");
		xmlConnections.append("login=\""+ connection.getLogin() +"\" ");
		xmlConnections.append("password=\""+ connection.getPassword() +"\" ");
		xmlConnections.append("name=\""+ connection.getName() +"\" ");
		xmlConnections.append("port=\""+ connection.getPort() +"\" ");
		xmlConnections.append("resource=\""+ connection.getResource() +"\"");
		xmlConnections.append("</connections>");

		return addAudit(dbcon, connection.getUserID(), "Connections", nAction, xmlConnections.toString());
	}

	/**
	 *  Add a new audit entry for a LinkedFile record
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 * 	@param DBExternalConnection, the connection object for this audit.
	 *	@return boolean, true if it was successful, else false.
	 *	@throws java.sql.SQLException
	 */
	public static boolean auditLinkedFile(DBConnection dbcon, int nAction, String sFileID, String sFileName, String sFileSize) throws SQLException {

		/* DATBASE 'LinkedFile' TABLE FOR REFERENCE
			"FileID VARCHAR(50) NOT NULL, "+
			"FileName VARCHAR(255) NOT NULL, "+
			"FileSize INT NOT NULL, "+
			"FileData BLOB(2G)) //can't audit this as it requires a BLOB field";
		*/

		StringBuffer xmlLinkedFile = new StringBuffer(300);
		xmlLinkedFile.append("<linkedfile ");
		xmlLinkedFile.append("fileid=\""+ sFileID +"\" ");
		xmlLinkedFile.append("filename=\""+ sFileName +"\" ");
		xmlLinkedFile.append("filesize=\""+ sFileSize +"\"");
		//xmlLinkedFile.append("filedata=\""+ connection.getServer() +"\"");
		xmlLinkedFile.append("</linkedfile>");

		return addAudit(dbcon, sFileID, "LinkedFile", nAction, xmlLinkedFile.toString());
	}

	/**
	 *  Add a new Audit record for a ViewTimeNode record.
	 *
	 *	@param dbcon the DBConnection object to access the database with.
	 * 	@param nAction the action type of this audit.
	 * 	@param nodePos the NodePositionTime being audited.
	 *	@return boolean true if it was successful, else false.
	 *	@throws java.sql.SQLException
	 */
	public static boolean auditViewTimeNode(DBConnection dbcon, int nAction, NodePositionTime nodePos) throws SQLException {

		/* DATBASE 'ViewTimeNode' TABLE FOR REFERENCE
			ViewID	= Text 50
			NodeID	= Text 50
			TimeToShow = DOUBLE
			TimeToHide = DOUBLE
			XPos	= Integer
			YPos	= Integer
			CreationDate = DOUBLE
			ModificationDate = DOUBLE
			CurrentStatus = INTEGER
		*/

		NodeSummary node = nodePos.getNode();
		View nodeView = nodePos.getView();

		StringBuffer xmlView = new StringBuffer(500);

		xmlView.append("<viewtimenode ");

		String viewid = nodeView.getId();

		xmlView.append("ViewID=\""+ viewid +"\" ");
		xmlView.append("NodeID=\""+ node.getId() +"\" ");
		xmlView.append("TimeToShow=\""+ nodePos.getTimeToShow() +"\" ");
		xmlView.append("TimeToHide=\""+ nodePos.getTimeToHide() +"\" ");
		xmlView.append("XPos=\""+ nodePos.getXPos() +"\" ");
		xmlView.append("YPos=\""+ nodePos.getYPos() +"\"" );
		xmlView.append("CreationDate=\""+ (nodePos.getCreationDate()).getTime() +"\" ");
		xmlView.append("ModificationDate=\""+ (nodePos.getModificationDate()).getTime() +"\"");

		xmlView.append("</viewtimenode>");

		return addAudit(dbcon, viewid, "ViewTimeNode", nAction, xmlView.toString());
	}

	/**
	 *  Add a new Audit record for a Movie record.
	 *
	 *	@param dbcon the DBConnection object to access the database with.
	 * 	@param nAction the action type of this audit.
	 * 	@param movie the Movie being audited.
	 *	@return boolean true if it was successful, else false.
	 *	@throws java.sql.SQLException
	 */
	public static boolean auditMovie(DBConnection dbcon, int nAction, Movie movie) throws SQLException {

		/* DATBASE 'Movie' TABLE FOR REFERENCE
		ViewTimeNodeID	= Text 50
		ViewID	= Text 50
		Link	= Text
		Name 	= VarChar 255
		StartTime = Double
		CreationDate = DOUBLE
		ModificationDate = DOUBLE
		*/

		StringBuffer xmlView = new StringBuffer(500);

		xmlView.append("<movie ");

		String id = movie.getId();

		xmlView.append("MovieID=\""+ movie.getId() +"\" ");
		xmlView.append("ViewID=\""+ movie.getViewID() +"\" ");
		xmlView.append("Link=\""+ movie.getLink() +"\" ");
		xmlView.append("Name=\""+ movie.getMovieName() +"\" " );
		xmlView.append("StartTime=\""+ String.valueOf(movie.getStartTime()) +"\" " );
		xmlView.append("CreationDate=\""+ (movie.getCreationDate()).getTime() +"\" ");
		xmlView.append("ModificationDate=\""+ (movie.getModificationDate()).getTime() +"\"");

		xmlView.append("</movie>");

		return addAudit(dbcon, id, "Movie", nAction, xmlView.toString());
	}
	

	/**
	 *  Add a new Audit record for a MovieProperties record.
	 *
	 *	@param dbcon the DBConnection object to access the database with.
	 * 	@param nAction the action type of this audit.
	 * 	@param movie the Movie being audited.
	 *	@return boolean true if it was successful, else false.
	 *	@throws java.sql.SQLException
	 */
	public static boolean auditMovieProperties(DBConnection dbcon, int nAction, MovieProperties movie) throws SQLException {

		/* DATBASE 'Movie' TABLE FOR REFERENCE
		MoviePropertyID	= Text 50
		MovieID	= Text 50
		XPos	= Integer
		YPos	= Integer
		Width	= Integer
		Height	= Integer
		Transparency = Float
		Time = Double
		CreationDate = DOUBLE
		ModificationDate = DOUBLE
		*/

		StringBuffer xmlView = new StringBuffer(500);

		xmlView.append("<movie ");

		String id = movie.getId();

		xmlView.append("MoviePropertyID=\""+ movie.getId() +"\" ");
		xmlView.append("MovieID=\""+ movie.getMovieID() +"\" ");
		xmlView.append("XPos=\""+ movie.getXPos() +"\" ");
		xmlView.append("YPos=\""+ movie.getYPos() +"\"" );
		xmlView.append("Width=\""+ movie.getWidth() +"\" ");
		xmlView.append("Height=\""+ movie.getHeight() +"\" " );
		xmlView.append("Transparency=\""+ String.valueOf(movie.getHeight()) +"\" " );
		xmlView.append("Time=\""+ movie.getTime() +"\" " );
		xmlView.append("CreationDate=\""+ (movie.getCreationDate()).getTime() +"\" ");
		xmlView.append("ModificationDate=\""+ (movie.getModificationDate()).getTime() +"\"");

		xmlView.append("</movie>");

		return addAudit(dbcon, id, "Movie", nAction, xmlView.toString());
	}
	

// NOT CURRENTLY USED

	/**
	 *  NOT USED - NOT COMPLETE
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 * 	@param nAction, the action type of this audit.
	 *	@return boolean, true if it was successful, else false.
	 *	@throws java.sql.SQLException
	 */
	public static boolean auditExtendedTypeCode(DBConnection dbcon, int nAction) throws SQLException {

		/* DATBASE 'ExtendedTypeCode' TABLE FOR REFERENCE
			ExtendedNodeTypeID		= Text 50
			CodeID					= Text 50
		*/

		String sID = "";
		StringBuffer xmlExtendedTypeCode = new StringBuffer(300);

		return addAudit(dbcon, sID, "ExtendedTypeCode", nAction, xmlExtendedTypeCode.toString());
	}

	/**
	 *  NOT USED - NOT COMPLETE
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 * 	@param nAction, the action type of this audit.
	 *	@return boolean, true if it was successful, else false.
	 *	@throws java.sql.SQLException
	 */
	public static boolean auditExtendedNodeType(DBConnection dbcon, int nAction) throws SQLException {

		/* DATBASE 'ExtendedNodeType' TABLE FOR REFERENCE
			ExtendedNodeTypeID		= Text 50
			Author					= Text 50
			CreationDate			= Number Double
			ModificationDate		= Number Double
			Name					= Text 50
			Description				= Text 50
			BaseNodeType			= Integer
			Icon					= Text 200
		*/

		String sID = "";
		StringBuffer xmlExtendedNodeType = new StringBuffer(300);

		return addAudit(dbcon, sID, "ExtendedNodeType", nAction, xmlExtendedNodeType.toString());
	}

	/**
	 *  NOT USED - NOT COMPLETE
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 * 	@param nAction, the action type of this audit.
	 *	@return boolean, true if it was successful, else false.
	 *	@throws java.sql.SQLException
	 */
	public static boolean auditPermission(DBConnection dbcon, int nAction) throws SQLException {

		/* DATBASE 'Permission' TABLE FOR REFERENCE
			ObjectID				= Text 50
			GroupID					= Text 50
			Permission				= Integer
		*/

		String sID = "";
		StringBuffer xmlPermission = new StringBuffer(300);

		return addAudit(dbcon, sID, "Permission", nAction, xmlPermission.toString());
	}

	/**
	 *  NOT USED - NOT COMPLETE
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 * 	@param nAction, the action type of this audit.
	 *	@return boolean, true if it was successful, else false.
	 *	@throws java.sql.SQLException
	 */
	public static boolean auditGroup(DBConnection dbcon, int nAction) throws SQLException {

		/* DATBASE 'Group' TABLE FOR REFERENCE
			GroupID					= Text 20
			Author					= Text 50
			CreationDate			= Number Double
			ModificationDate		= Number Double
			GroupName				= Text 50
			GroupDescription		= Text 255
		*/

		String sID = "";
		StringBuffer xmlGroup = new StringBuffer(300);

		return addAudit(dbcon, sID, "Group", nAction, xmlGroup.toString());
	}

	/**
	 *  NOT USED - NOT COMPLETE
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 * 	@param nAction, the action type of this audit.
	 *	@return boolean, true if it was successful, else false.
	 *	@throws java.sql.SQLException
	 */
	public static boolean auditUserGroup(DBConnection dbcon, int nAction) throws SQLException {

		/* DATBASE 'UserGroup' TABLE FOR REFERENCE
			UserID					= Text 20	// NEEDS TO BE 50
			GroupID					= Text 20
		*/

		String sID = "";
		StringBuffer xmlUserGroup = new StringBuffer(300);

		return addAudit(dbcon, sID, "UserGroup", nAction, xmlUserGroup.toString());
	}

	/**
	 *  NOT USED - NOT COMPLETE
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 * 	@param nAction, the action type of this audit.
	 *	@return boolean, true if it was successful, else false.
	 *	@throws java.sql.SQLException
	 */
	public static boolean auditClones(DBConnection dbcon, int nAction) throws SQLException {

		/* DATBASE 'UserClones' TABLE FOR REFERENCE
			ParentNodeID			= Text 50
			ChildNodeID				= Text 50

			// VIEW ID ???
		*/

		String sID = "";
		StringBuffer xmlClones = new StringBuffer(300);

		return addAudit(dbcon, sID, "Clones", nAction, xmlClones.toString());
	}
}
