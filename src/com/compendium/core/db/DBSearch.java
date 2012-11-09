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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.io.*;

import com.compendium.core.datamodel.*;
import com.compendium.core.db.management.*;
import com.compendium.core.ICoreConstants;

/**
 *  The DBSearch class serves as the interface layer to make queries and searches into the
 *	the database.
 *
 *  @author	Rema Natarajan / Michelle Bachler / Lin Yang
 */
public class DBSearch {

	/** String to represent context -- current view or home window */
	public final static String CONTEXT_SINGLE_VIEW = "contextSingleView" ;

	/** String to represent context all the views in the database */
	public final static String CONTEXT_ALLVIEWS = "contextAllViews" ;

	/** String to represent context all views AND all deleted objects in the database */
	public final static String CONTEXT_ALLVIEWS_AND_DELETEDOBJECTS = "contextAllViewsAndDeletedObjects" ;

	/** String to represent match condition 'any' */
	public final static int MATCH_ANY = 0 ;

	/** String to represent match condition 'all' */
	public final static int MATCH_ALL = 1 ;


	/**
	 * This String is an SQL stub for building a statement to return
	 * the node data from the Node table, under the later given conditions.
	 */
	public final static String SEARCH_ALLVIEWS_QUERY =
		"Select Node.NodeID, Node.NodeType, Node.ExtendedNodeType, Node.OriginalID, Node.Author, Node.CreationDate," +
		"Node.ModificationDate, Node.Label, Node.Detail, Node.LastModAuthor " +
		"FROM Node LEFT JOIN NodeDetail on Node.NodeID=NodeDetail.NodeID " +
		"WHERE ";

	/**
	 * This String is an SQL stub for building a statement to return
	 * the node data from the Node table joined with the Code table, under the later given conditions.
	 */
	public final static String SEARCH_ALLVIEWS_AND_CODES_QUERY =
		"Select Node.NodeID, Node.NodeType, Node.ExtendedNodeType, Node.OriginalID, Node.Author, Node.CreationDate," +
		"Node.ModificationDate, Node.Label, Node.Detail, Node.LastModAuthor " +
		"FROM Node LEFT JOIN NodeDetail on Node.NodeID=NodeDetail.NodeID " +
		"LEFT JOIN NodeCode ON NodeCode.NodeID = Node.NodeID " +
		"LEFT JOIN Code ON NodeCode.CodeID = Code.CodeID " +
		"WHERE ";

	/** This search query returns the node summaries in the current given view. */
	public final static String SEARCH_CURRENTVIEW_QUERY =
		"Select Node.NodeID, Node.NodeType, Node.ExtendedNodeType, Node.OriginalID, Node.Author, Node.CreationDate," +
		"Node.ModificationDate, Node.Label, Node.Detail, Node.LastModAuthor " +
		"FROM Node LEFT JOIN NodeDetail on Node.NodeID=NodeDetail.NodeID " +
		"LEFT JOIN ViewNode ON ViewNode.NodeID = Node.NodeID " +
		"WHERE ViewNode.ViewID = ";

	/**
	 * This search query returns the node summaries in the current
	 * given view, when searching on Codes as well as other criteria.
	 */
	public final static String SEARCH_CURRENTVIEW_AND_CODES_QUERY =
		"Select Node.NodeID, Node.NodeType, Node.ExtendedNodeType, Node.OriginalID, Node.Author, Node.CreationDate," +
		"Node.ModificationDate, Node.Label, Node.Detail, Node.LastModAuthor " +
		"FROM Node LEFT JOIN NodeDetail on Node.NodeID=NodeDetail.NodeID " +
		"LEFT JOIN NodeCode ON NodeCode.NodeID = Node.NodeID " +
		"LEFT JOIN Code ON NodeCode.CodeID = Code.CodeID " +
		"LEFT JOIN ViewNode ON ViewNode.NodeID = Node.NodeID " +
		"WHERE ViewNode.ViewID = ";


	/**
	 *	Return all nodes whose labels EQUAL the passed text.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param text, the text to match.
	 * 	@param type, the type of the node to filter the search on.
	 * 	@return Vector, of <code>NodeSummary</code> objects whose label matches the given text.
	 *	@throws java.sql.SQLException
	 */
	public static Vector searchExactNodeLabel(DBConnection dbcon, String sText, int nType, String userID) throws SQLException {

		Connection con = dbcon.getConnection();
		if (con == null)
			return null;

		PreparedStatement pstmt = con.prepareStatement("Select NodeID, NodeType, ExtendedNodeType, OriginalID, Author, CreationDate," +
										"ModificationDate, Label, Detail, LastModAuthor  " +
										"FROM Node WHERE Label LIKE ('"+sText+"')"+
										"AND NodeType = "+nType+
										" AND Node.CurrentStatus = "+ICoreConstants.STATUS_ACTIVE);

		ResultSet rs = pstmt.executeQuery();
		Vector vtNodes = new Vector(51);
		NodeSummary node = null;

		if (rs != null) {

			while (rs.next()) {
				node = DBNode.processNode(dbcon, rs, userID);
				vtNodes.addElement(node);
			}
		}
		pstmt.close();
		return vtNodes;
	}

	/**
	 *	Return all nodes whose labels starts with the passed text and are in the given view.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param sText, the text to match.
	 * 	@param nType, the type of the node to filter the search on.
	 * 	@param sViewID, the id of the view to search in.
	 * 	@return Vector, of <code>NodePosition</code> objects whose label matches the given text.
	 *	@throws java.sql.SQLException
	 */
	public static Vector searchNodeLabelInView(DBConnection dbcon, String sText, int nType, String sViewID, String userID) throws SQLException {

		Connection con = dbcon.getConnection();
		if (con == null)
			return null;

		PreparedStatement pstmt = con.prepareStatement("Select Node.NodeID, Node.NodeType, Node.ExtendedNodeType, Node.OriginalID, Node.Author, Node.CreationDate," +
										"Node.ModificationDate, Node.Label, Node.Detail, Node.LastModAuthor, " +
										"ViewNode.XPos, ViewNode.YPos, ViewNode.CreationDate, ViewNode.ModificationDate " +
										"FROM Node LEFT JOIN ViewNode ON Node.NodeID = ViewNode.NodeID " +
										"WHERE Node.Label LIKE ('"+sText+"%') AND Node.NodeType = "+nType+
										" AND ViewNode.ViewID LIKE ('"+sViewID+"')"+
										" AND Node.CurrentStatus = "+ICoreConstants.STATUS_ACTIVE+											
										" AND ViewNode.CurrentStatus = "+ICoreConstants.STATUS_ACTIVE);

		ResultSet rs = pstmt.executeQuery();
		Vector vtNodes = new Vector(51);
		NodeSummary node = null;
		NodePosition nodePos = null;

		if (rs != null) {

			while (rs.next()) {
				node = DBNode.processNode(dbcon, rs, userID);
				int 	nX			= rs.getInt(11);
				int 	nY			= rs.getInt(12);
				Date	oCreated	= new Date(new Double(rs.getString(13)).longValue());
				Date	oModified	= new Date(new Double(rs.getString(14)).longValue());
				// now that the node summary object is generated, create the node position object
				View view = View.getView(sViewID) ;
				nodePos = new NodePosition(view, node, nX, nY, oCreated, oModified);
				vtNodes.addElement(nodePos);
			}
		}
		pstmt.close();
		return vtNodes;
	}

	/**
	 *	Return all nodes whose labels EQUAL the passed text and are in the given view.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param sText, the text to match.
	 * 	@param nType, the type of the node to filter the search on.
	 * 	@param sViewID, the id of the view to search in.
	 * 	@return Vector, of <code>NodePosition</code> objects whose label matches the given text.
	 *	@throws java.sql.SQLException
	 */
	public static Vector searchExactNodeLabelInView(DBConnection dbcon, String sText, int nType, String sViewID, String userID) throws SQLException {

		Connection con = dbcon.getConnection();
		if (con == null)
			return null;

		PreparedStatement pstmt = con.prepareStatement("Select Node.NodeID, Node.NodeType, Node.ExtendedNodeType, Node.OriginalID, Node.Author, Node.CreationDate," +
										"Node.ModificationDate, Node.Label, Node.Detail, Node.LastModAuthor, " +
										"ViewNode.XPos, ViewNode.YPos, ViewNode.CreationDate, ViewNode.ModificationDate " +
										"FROM Node LEFT JOIN ViewNode ON Node.NodeID = ViewNode.NodeID " +
										"WHERE Node.Label LIKE ('"+sText+"') AND Node.NodeType = "+nType+
										" AND ViewNode.ViewID LIKE ('"+sViewID+"')"+
										" AND Node.CurrentStatus = "+ICoreConstants.STATUS_ACTIVE+										
										" AND ViewNode.CurrentStatus = "+ICoreConstants.STATUS_ACTIVE);

		ResultSet rs = pstmt.executeQuery();
		Vector vtNodes = new Vector(51);
		NodeSummary node = null;
		NodePosition nodePos = null;

		if (rs != null) {

			while (rs.next()) {
				node = DBNode.processNode(dbcon, rs, userID);
				int 	nX			= rs.getInt(11);
				int 	nY			= rs.getInt(12);
				Date	oCreated	= new Date(new Double(rs.getString(13)).longValue());
				Date	oModified	= new Date(new Double(rs.getString(14)).longValue());

				// now that the node summary object is generated, create the node position object
				View view = View.getView(sViewID) ;
				nodePos = new NodePosition(view, node, nX, nY, oCreated, oModified);
				vtNodes.addElement(nodePos);
			}
		}
		pstmt.close();
		return vtNodes;
	}

	/**
	 * Return all nodes with a triplestore original id in the given view.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 * 	@param sViewID, the id of the view to search in.
	 * 	@return Vector, of <code>NodePosition</code> objects whose label start with the given text.
	 *	@throws java.sql.SQLException
	 */
	public static Vector searchForTripleStoreNodes(DBConnection dbcon, String sViewID, String userID) throws SQLException {

		Connection con = dbcon.getConnection();
		if (con == null)
			return null;

		PreparedStatement pstmt = con.prepareStatement("Select Node.NodeID, Node.NodeType, Node.ExtendedNodeType, Node.OriginalID, Node.Author, Node.CreationDate," +
										"Node.ModificationDate, Node.Label, Node.Detail, Node.LastModAuthor, " +
										"ViewNode.XPos, ViewNode.YPos, ViewNode.CreationDate, ViewNode.ModificationDate " +
										"FROM Node LEFT JOIN ViewNode ON Node.NodeID = ViewNode.NodeID " +
										"WHERE ViewNode.ViewID='"+sViewID+"' "+
										"AND Node.OriginalID LIKE ('TS:%') "+
										"AND Node.CurrentStatus = "+ICoreConstants.STATUS_ACTIVE+
										" AND ViewNode.CurrentStatus = "+ICoreConstants.STATUS_ACTIVE);

		ResultSet rs = pstmt.executeQuery();
		Vector vtNodes = new Vector(51);
		NodeSummary node = null;
		NodePosition nodePos = null;

		if (rs != null) {

			while (rs.next()) {
				node = DBNode.processNode(dbcon, rs, userID);
				int 	nX			= rs.getInt(11);
				int 	nY			= rs.getInt(12);
				Date	oCreated	= new Date(new Double(rs.getString(13)).longValue());
				Date	oModified	= new Date(new Double(rs.getString(14)).longValue());
				// now that the node summary object is generated, create the node position object
				View view = View.getView(sViewID) ;
				nodePos = new NodePosition(view, node, nX, nY, oCreated, oModified);
				vtNodes.addElement(nodePos);
			}
		}
		pstmt.close();
		return vtNodes;
	}

	/**
	 *	Return all nodes whose labels start with the passed text, and have the passed node type.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param text, the text to match.
	 * 	@param sNodeID, the id of the node being matched that therefore needs to be excluded from the results.
	 * 	@param type, the type of the node to filter the search on.
	 * 	@return Vector, of <code>NodeSummary</code> objects whose label start with the given text.
	 *	@throws java.sql.SQLException
	 */
	public static Vector searchTransclusions(DBConnection dbcon, String text, String sNodeID, int type, String userID) throws SQLException {

		Connection con = dbcon.getConnection();
		if (con == null)
			return null;

		PreparedStatement pstmt = con.prepareStatement("Select NodeID, NodeType, ExtendedNodeType, OriginalID, Author, CreationDate," +
										"ModificationDate, Label, Detail, LastModAuthor " +
										"FROM Node " +
										"WHERE Label LIKE('"+text+"%') AND NodeID NOT IN ('"+sNodeID+"') "+
										"AND NodeType = "+type+
										" AND Node.CurrentStatus = "+ICoreConstants.STATUS_ACTIVE+
										" AND ViewNode.CurrentStatus = "+ICoreConstants.STATUS_ACTIVE);

		ResultSet rs = pstmt.executeQuery();
		Vector vtNodes = new Vector(51);
		NodeSummary node = null;

		if (rs != null) {

			while (rs.next()) {
				node = DBNode.processNode(dbcon, rs, userID);
				vtNodes.addElement(node);
			}
		}
		pstmt.close();
		return vtNodes;
	}

	/**
	 * Return all nodes whose labels start with the passed text.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param text, the text to match.
	 * 	@param sNodeID, the id of the node being matched that therefore needs to be excluded from the results.
	 * 	@return Vector, of <code>NodeSummary</code> objects whose label start with the given text.
	 *	@throws java.sql.SQLException
	 */
	public static Vector searchTransclusions(DBConnection dbcon, String text, String sNodeID, String userID) throws SQLException {

		Connection con = dbcon.getConnection();
		if (con == null)
			return null;

		PreparedStatement pstmt = con.prepareStatement("Select NodeID, NodeType, ExtendedNodeType, OriginalID, Author, CreationDate," +
										"ModificationDate, Label, Detail, LastModAuthor  " +
										"FROM Node " +
										"WHERE Label LIKE('"+text+"%') AND NodeID NOT IN ('"+sNodeID+"') "+
										" AND Node.CurrentStatus = "+ICoreConstants.STATUS_ACTIVE);

		ResultSet rs = pstmt.executeQuery();
		Vector vtNodes = new Vector(51);
		NodeSummary node = null;

		if (rs != null) {

			while (rs.next()) {
				node = DBNode.processNode(dbcon, rs, userID);
				vtNodes.addElement(node);
			}
		}
		pstmt.close();
		return vtNodes;
	}


	/**
	 *	Searches the node table for nodes that satisfy user query conditions.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param contextCondition three choices -- search only in current view or home window
	 *	/all views/all views and deleted objects -- refer const string definitions above.
	 *	@param viewID -- only applicable in case of context choice 'current view or home window' contains the viewID
	 *	@param vtSelectedNodeTypes, a Vector of the node types to search on.
	 *	@param vtSelectedAuthors, a Vector of the author names to search on.
	 *	@param vtSelectedCodes, a Vector of the code id to search on.
	 *	@param matchCodesCondition -- (1) match any (2) match all codes listed
	 *	@param vKeywords -- a Vector of the keywords to search on
	 *	@param matchKeywordCondition -- (1) match any (2) match all keywords listed
	 *  @param attrib, lists whether to match the keyword on the label, detail, or label and detail fields.
	 *	@param beforeCreationDate java.util.Date , the creation date to search up to.
	 *	@param afterCreationDate java.util.Date , the creation date to search from.
	 *	@param beforeModificationDate java.util.Date , the modification date to search up to.
	 *	@param afterModificationDate java.util.Date , the modification date to search from.
	 *	@return an Enumeration of <code>NodeSummary</code> objects that match the search query.
	 *	@throws java.sql.SQLException
	 */
	public static Enumeration searchAttribute(DBConnection dbcon,
											  String contextCondition,
							 String viewID, Vector vtSelectedNodeTypes, Vector vtSelectedAuthors,
							 Vector vtSelectedCodes, int matchCodesCondition, Vector vKeywords,
							 int matchKeywordCondition, Vector attrib,
							 java.util.Date beforeCreationDate, java.util.Date afterCreationDate,
							 java.util.Date beforeModificationDate,
							 java.util.Date afterModificationDate, 
							 String userID) throws SQLException
	{

		Vector vAttribFields = new Vector();
		Connection con = dbcon.getConnection();
		if (con == null)
			return null;

		String query = "" ;
		boolean prevCond = false;

		//-----------------------------------------------------------------------
		//View
		if (contextCondition.equals(CONTEXT_SINGLE_VIEW)) {

			if (viewID == "") {
				throw new SQLException("invalid view id") ;
			}
			else {
				if (vtSelectedCodes.isEmpty()) {
					query = SEARCH_CURRENTVIEW_QUERY + "'" + viewID + "'";
				}
				else {
					query = SEARCH_CURRENTVIEW_AND_CODES_QUERY + "'" + viewID + "'";
				}
				prevCond = true;
			}
		}
		else if (contextCondition.equals(CONTEXT_ALLVIEWS)) {
			if (vtSelectedCodes.isEmpty()) {
				query = SEARCH_ALLVIEWS_QUERY;
			}
			else {
				query = SEARCH_ALLVIEWS_AND_CODES_QUERY;
				//prevCond = true;
			}
		}
		else if (contextCondition.equals(CONTEXT_ALLVIEWS_AND_DELETEDOBJECTS)) {
			if (vtSelectedCodes.isEmpty()) {
				query = SEARCH_ALLVIEWS_QUERY;
			}
			else {
				query = SEARCH_ALLVIEWS_AND_CODES_QUERY;
				//prevCond = true;
			}
		}
		else if (!contextCondition.equals(CONTEXT_ALLVIEWS_AND_DELETEDOBJECTS)) {
			throw new SQLException("unknown context condition in search query: searchNodeDetail, for keyword " +
				" in view " + viewID + "for match condition " + contextCondition ) ;
		}

		//------------------------------------------------------------------------
		// Creation and Modification dates
		if (beforeCreationDate != null) {
			Double dDate = new Double(new Long(beforeCreationDate.getTime()).doubleValue());
			if (prevCond)
				query = query + " AND ";
			query = query + "Node.CreationDate < " + dDate.toString();
			prevCond = true;
		}
		if (afterCreationDate != null) {
			Double dDate = new Double(new Long(afterCreationDate.getTime()).doubleValue());
			if (prevCond)
				query = query + " AND ";
			query = query + "Node.CreationDate > " + dDate.toString();
			prevCond = true;
		}
		if (beforeModificationDate != null) {
			Double dDate = new Double(new Long(beforeModificationDate.getTime()).doubleValue());
			if (prevCond)
				query = query + " AND ";
			query = query + "Node.ModificationDate < " + dDate.toString();
			prevCond = true;
		}
		if (afterModificationDate != null) {
			Double dDate = new Double(new Long(afterModificationDate.getTime()).doubleValue());
			if (prevCond)
				query = query + " AND ";
			query = query + "Node.ModificationDate > " + dDate.toString();
			prevCond = true;
		}

		//------------------------------------------------------------------------
		// Keywords
		// generate partial query for match condition on attrib
		if (!vKeywords.isEmpty()) {
			if (prevCond)
				query = query + " AND ";
			if (matchKeywordCondition == MATCH_ALL) {
				query = query + matchAttrib(attrib, vKeywords, "AND");
			}
			else if (matchKeywordCondition == MATCH_ANY) {
				query = query + matchAttrib(attrib, vKeywords, "OR");
			}
			else {
				throw new SQLException("unknown match condition for Keywords") ;
			}
			prevCond = true;
		}
		//------------------------------------------------------------------------
		// NodeTypes
		if (!vtSelectedNodeTypes.isEmpty()) {
			if (prevCond)
				query = query + " AND ";

			/* comment out by Lin Yang to correct the Node Type match error.
			Delete after testing.
			vAttribFields.removeAllElements();
			vAttribFields.addElement("Node.NodeType");
			query = query + matchAttrib(vAttribFields, vtSelectedNodeTypes, "OR");*/

			query = query + matchAttrib(vtSelectedNodeTypes);
			prevCond = true;
		}
		//------------------------------------------------------------------------
		// Authors
		if (!vtSelectedAuthors.isEmpty()) {
			if (prevCond)
				query = query + " AND ";

			vAttribFields.removeAllElements();
			vAttribFields.addElement("Node.Author");
			query = query + matchAttrib(vAttribFields, vtSelectedAuthors, "OR");
			prevCond = true;
		}

		//------------------------------------------------------------------------
		// Codes
		if (!vtSelectedCodes.isEmpty()) {
			if (prevCond)
				query = query + " AND ";

			vAttribFields.removeAllElements();
			vAttribFields.addElement("Code.Name");

			// THE MATCH ALL CODE DID NOT WORK, THIS IS NOW DEALT WITH LOWER DOWN
			// AFTER THE FIRST RESULTS SET HAS RETURNED.

			//if (matchCodesCondition == MATCH_ALL) {
			//	query = query + matchAttrib(vAttribFields, vtSelectedCodes, "AND");
			//}
			//else if (matchCodesCondition == MATCH_ANY) {
				query = query + matchAttrib(vAttribFields, vtSelectedCodes, "OR");
			//}
			//else {
			//	throw new SQLException("unknown match condition for Codes") ;
			//}
			prevCond = true;
		}

		// now cover the 'delete' flag consideration
		// if context does not cover the deleted objects, exclude them from
		// the test results
		if (!(contextCondition.equals(CONTEXT_ALLVIEWS_AND_DELETEDOBJECTS))) {
			if (prevCond)
				query = query + " AND ";

			if (contextCondition.equals(CONTEXT_SINGLE_VIEW)) {
				query = query + "( Node.CurrentStatus = "+ICoreConstants.STATUS_ACTIVE+
				" AND ViewNode.CurrentStatus = "+ICoreConstants.STATUS_ACTIVE+ ") ";				
			} else {
				query = query + "( Node.CurrentStatus = "+ICoreConstants.STATUS_ACTIVE+") ";
			}
		}

		System.out.println("query = "+query);

		Statement statement = con.createStatement() ;
		ResultSet rs = statement.executeQuery(query) ;

		Vector vtNodes = new Vector(51);
		NodeSummary node = null;

		if (rs != null) {
			Vector vtTempNodes = new Vector(51);
			Hashtable htTempNodes = new Hashtable(51);

			while (rs.next()) {				
				String	sId			= rs.getString(1) ;
				if (!htTempNodes.containsKey(sId)) {
					htTempNodes.put(sId, sId);
					node = DBNode.processNode(dbcon, rs, userID);
					vtTempNodes.addElement(node);
				}
			}
			statement.close() ;

			if (matchCodesCondition == MATCH_ALL) {

				String query2 = "SELECT Count(NodeCode.NodeID) FROM Code, NodeCode WHERE NodeCode.CodeID=Code.CodeID and Code.Name IN (";
				int codecount = vtSelectedCodes.size();
				for (int i=0 ;i<codecount;i++) {
					Code code = (Code)vtSelectedCodes.elementAt(i);
	 				if (i==0)
						query2 = query2 + " '"+code.getName()+"'";
					else
						query2 = query2 + ", '"+code.getName()+"'";
				}
				query2 += ")";

				String finalQuery = "";
				NodeSummary node2 = null;
				int jcount = vtTempNodes.size();
				for (int j=0; j< jcount; j++) {
					node2 = (NodeSummary)vtTempNodes.elementAt(j);
					finalQuery = query2 + " AND NodeCode.NodeID='"+node2.getId()+"'";
					//System.out.println("finalQuery = "+finalQuery);
					Statement statement2 = con.createStatement();
					ResultSet rs2 = statement2.executeQuery(finalQuery);
					if (rs2 != null) {
						while (rs2.next()) {
							int idcount = rs2.getInt(1);
							//System.out.println("idcount = "+idcount);

							if (idcount == codecount)
								vtNodes.addElement(node2);
						}
					}
					statement2.close();
				}
			}
			else {
				vtNodes = vtTempNodes;
			}
		}

		return (Enumeration)vtNodes.elements();
	}


	/**
	 *  Method to help generate a partial query for searching a set of attributes (columns) for any of the given keywords.
	 *  for eg. for the attribute "Label", for keywords "test1" and "test2", and the
	 *  conjunction "OR" the return value will be of the format
	 *  "Label Like 'test1' OR Label Like 'test2'"
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *  @param attrib, a list of fields to search on.
	 *	@param vKeywords, a Vector of the keywords to search on.
	 *  @param sConjunction, whether to perfom and 'AND' or 'OR' search on those fields.
	 * 	@param String, the partial SQL string to search on for the given parameters.
	 */
	private static String matchAttrib(Vector vAttrib, Vector vKeywords, String sConjunction) {

		boolean firstAttrib = true;
		boolean firstKeyword = true;
		Enumeration attrib = vAttrib.elements();

		String partialQuery = " ( ";

		if ((attrib.hasMoreElements() == false)||((vKeywords.elements()).hasMoreElements() == false)) {
			return null ;
		}

		do {
			String nextAttrib = (String) attrib.nextElement();
			if (firstAttrib) {
				partialQuery = partialQuery + " ( " + nextAttrib + " Like " ;
				firstAttrib = false;
			}
			else {
				partialQuery = partialQuery + " " + "OR" + " ( " + nextAttrib + " Like " ;
			}

			firstKeyword = true;
			Enumeration keywords = vKeywords.elements();
			do {
				Object nextKeyword = (Object)keywords.nextElement() ;
				String sNextKeyword = "";
				boolean isCode = false;
				boolean isAuthor = false;

				//get string for UserProfiles
				if (nextKeyword.getClass() == UserProfile.class) {
					isAuthor = true;
					sNextKeyword = ((UserProfile) nextKeyword).getUserName();
				}
				else if (nextKeyword.getClass() == Code.class) {
					isCode = true;
					sNextKeyword = ((Code) nextKeyword).getName();
				}
				else {
					sNextKeyword = (String) nextKeyword;
				}

				if (firstKeyword) {
					if (isCode || isAuthor) {
						partialQuery = partialQuery + "'" + sNextKeyword + "'";
					}
					else {
						partialQuery = partialQuery + "'%" + sNextKeyword + "%'";
					}
					firstKeyword = false;
				}
				else {
					if (isCode || isAuthor) {
						partialQuery = partialQuery + " " + sConjunction + " " + nextAttrib + " Like " + "'" + sNextKeyword + "'" ;
					}
					else {
						partialQuery = partialQuery + " " + sConjunction + " " + nextAttrib + " Like " + "'%" + sNextKeyword + "%'" ;
					}
				}
			}

			while (keywords.hasMoreElements());
			partialQuery = partialQuery + " ) ";
		}
		while (attrib.hasMoreElements());

		partialQuery = partialQuery + " ) ";
		return partialQuery;
	}

	/**
	 *  Method to help generate a partial query for searching NodeType
	 *  Added to correct the NodeType mismatch.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param vKeywords, a Vector of the keywords to search on.
	 * 	@param String, the partial SQL string to search on for the given parameters.
	 */
	private static String matchAttrib(Vector vKeywords) {

		boolean firstKeyword = true;

		if ((vKeywords.elements()).hasMoreElements() == false) {
			return null ;
		}

		String partialQuery = " ( NodeType =  ";
		String operator = "OR";

		firstKeyword = true;
		Enumeration keywords = vKeywords.elements();
		do {
			Object nextKeyword = (Object)keywords.nextElement() ;
			String sNextKeyword = "";

			//get string for UserProfiles
			if (nextKeyword.getClass() == UserProfile.class) {
				sNextKeyword = ((UserProfile) nextKeyword).getLoginName();
			}
			else if (nextKeyword.getClass() == Code.class) {
				sNextKeyword = ((Code) nextKeyword).getName();
			}
			else {
				sNextKeyword = (String) nextKeyword;
			}
			if (firstKeyword) {
				partialQuery = partialQuery + sNextKeyword + " ";
				firstKeyword = false;
			}
			else {
				partialQuery = partialQuery + operator + " NodeType = " + sNextKeyword + " " ;
			}
		}
		while (keywords.hasMoreElements());

		partialQuery = partialQuery + ")";
		return partialQuery;
	}

// FOR FUTURE USE
	/*private static String matchTags(Vector vKeywords) {

		String partialQuery = " CodeNode.CodeID = ALL ( SELECT CodeID FROM Code WHERE (";

		if (vKeywords.elements().hasMoreElements() == false) {
			return null;
		}

		boolean firstKeyword = true;
		Enumeration keywords = vKeywords.elements();
		do {
			Object nextKeyword = (Object)keywords.nextElement() ;
			String sNextKeyword = "";
			sNextKeyword = ((Code) nextKeyword).getName();
			if (firstKeyword) {
				partialQuery = partialQuery + "Code.CodeName LIKE '" + sNextKeyword + "'";
				firstKeyword = false;
			}
			else {
				partialQuery = partialQuery + " AND Code.CodeName LIKE '" + sNextKeyword + "'" ;
			}
		}
		while (keywords.hasMoreElements());

		partialQuery = partialQuery + " ))";

		return partialQuery;
	}*/
}
