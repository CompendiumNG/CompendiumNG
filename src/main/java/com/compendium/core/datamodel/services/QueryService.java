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
import java.sql.*;

import com.compendium.core.datamodel.*;
import com.compendium.core.db.*;
import com.compendium.core.db.management.*;

/**
 *	The QueryService class provides services to run general node search queries of the database.
 *
 *	@author Sajid and Rema / Michelle Bachler
 */
public class QueryService extends ClientService implements IQueryService, java.io.Serializable {

	/**
	 *	constructor
	 */
	public  QueryService() {
		super();
	}

	/**
	 * Constructor, set the name of the service.
	 *
	 * @param String sName, the name of this service.
	 */
	public  QueryService(String sName) {
		super(sName);
	}

	/**
	 * Constructor, set the name, ServiceManager and DBDatabaseManager for this service.
	 *
	 * @param String sName, the name of this service.
	 * @param ServiceManager sm, the ServiceManager used by this service.
	 * @param DBDatabaseManager dbMgr, the DBDatabaseManager used by this service.
	 */
	public  QueryService(String sName, ServiceManager sm,  DBDatabaseManager dbMgr) {
		super(sName, sm, dbMgr);
	}

	/**
	 * Returns a Vector of nodes given a keyword
	 *
	 * @param PCSession session, the session object for the database to use.
	 * @param String sContextCondition, context of the search (just the given view, all views, deleted views)
	 * @param String sViewID, the id of the curernt view.
	 * @param Vector vtSelectedNodeTypes, a vector of the selected node type to run the search against.
	 * @param Vector vtSelectedAuthors, a vector of author to run the search against.
	 * @param Vector vtSelectedCodes, a vector of codes to run the search against.
	 * @param Vector vKeywords, a vector of keywords to run the search against.
	 * @param int nMatchKeywordCondition, match all or any keywords.
	 * @param Vector attrib, match the keywords in the label, detail or both as specified.
	 * @param java.util.Date beforeCreationDate, the end creation date to run the search against.
	 * @param java.util.Date afterCreationDate, the start creation date to run the search against.
	 * @param java.util.Date beforeModificationDate, the end modification date to run the search against.
	 * @param java.util.Date afterModificationDate, the start modification date to run the search against.
	 *
	 * @return Vector, of NodeSummary objects resulting from executing the search.
	 * @exception java.sql.SQLException
	 */
	public Vector searchNode(PCSession session, String sContextCondition,
							 String sViewID, Vector vtSelectedNodeTypes, Vector vtSelectedAuthors,
							 Vector vtSelectedCodes, int sMatchCodesCondition, Vector vKeywords,
							 int nMatchKeywordCondition, Vector attrib,
							 java.util.Date dBeforeCreationDate, java.util.Date dAfterCreationDate,
							 java.util.Date dBeforeModificationDate,
							 java.util.Date dAfterModificationDate 
							 ) throws SQLException
	{
		DBConnection dbcon = getDatabaseManager().requestConnection(session.getModelName()) ;

		Vector vtNodes = new Vector(51);
		NodeSummary node = null;

	 	for(Enumeration e = DBSearch.searchAttribute(dbcon, sContextCondition,
									sViewID, vtSelectedNodeTypes, vtSelectedAuthors,
									vtSelectedCodes, sMatchCodesCondition, vKeywords,
									nMatchKeywordCondition, attrib,
									dBeforeCreationDate, dAfterCreationDate,
									dBeforeModificationDate,
									dAfterModificationDate, session.getUserID()); e.hasMoreElements();)
		{
			 node = (NodeSummary)e.nextElement();
			 vtNodes.addElement(node);
		}

		getDatabaseManager().releaseConnection(session.getModelName(), dbcon);

		return vtNodes;
	}

	/**
	 * Return all nodes whose labels match exactly the given text.
	 *
	 * @param PCSession session, the session object for the database to use.
	 * @param String sText, the text to match node label to.
	 * @param int nType, the node type to confine the match to.
 	 * @return Vector, of NodeSummary objects resultant from the search.
	 * @exception java.sql.SQLException
	 */
	public Vector searchExactNodeLabel(PCSession session, String sText, int nType) throws SQLException {

		DBConnection dbcon = getDatabaseManager().requestConnection(session.getModelName()) ;

		Vector vtNodes = DBSearch.searchExactNodeLabel(dbcon, sText, nType, session.getUserID());

		getDatabaseManager().releaseConnection(session.getModelName(), dbcon);

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
	public Vector searchExactNodeLabelInView(PCSession session, String sText, int nType, String sViewID) throws SQLException {

		DBConnection dbcon = getDatabaseManager().requestConnection(session.getModelName()) ;

		Vector vtNodes = DBSearch.searchExactNodeLabelInView(dbcon, sText, nType, sViewID, session.getUserID());

		getDatabaseManager().releaseConnection(session.getModelName(), dbcon);

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
	public Vector searchNodeLabelInView(PCSession session, String sText, int nType, String sViewID) throws SQLException {

		DBConnection dbcon = getDatabaseManager().requestConnection(session.getModelName()) ;

		Vector vtNodes = DBSearch.searchNodeLabelInView(dbcon, sText, nType, sViewID, session.getUserID());

		getDatabaseManager().releaseConnection(session.getModelName(), dbcon);

		return vtNodes;
	}

	/**
	 * Return all nodes whose labels start with the passed text, and are of the passed node type, but are not the given node.
	 *
	 * @param PCSession session, the session object for the database to use.
	 * @param String sText, the text to match to the start of node labels.
	 * @param String sNodeID, the node to exclude from the results.
	 * @param int nType, the node type to confine the match to.
 	 * @return Vector, of NodeSummary objects resultant from the search.
	 * @exception java.sql.SQLException
	 */
	public Vector searchTransclusions(PCSession session, String sText, String sNodeID, int nType) throws SQLException {

		DBConnection dbcon = getDatabaseManager().requestConnection(session.getModelName()) ;

		Vector vtNodes = DBSearch.searchTransclusions(dbcon, sText, sNodeID, nType, session.getUserID());

		getDatabaseManager().releaseConnection(session.getModelName(), dbcon);

		return vtNodes;
	}

	/**
	 * Return all nodes whose labels start with the passed text and are not the given node.
	 *
	 * @param PCSession session, the session object for the database to use.
	 * @param String sText, the text to match to the start of node labels.
	 * @param String sNodeID, the node to exclude from the results.
 	 * @return Vector, of NodeSummary objects resultant from the search.
	 * @exception java.sql.SQLException
	 */
	public Vector searchTransclusions(PCSession session, String sText, String sNodeID) throws SQLException {

		DBConnection dbcon = getDatabaseManager().requestConnection(session.getModelName()) ;

		Vector vtNodes = DBSearch.searchTransclusions(dbcon, sText, sNodeID, session.getUserID());

		getDatabaseManager().releaseConnection(session.getModelName(), dbcon);

		return vtNodes;
	}


	/**
	 * Return all nodes who have a triplestore original id and are in the given view.
	 *
	 * @param PCSession session, the session object for the database to use.
	 * @param String sViewID, the id of the view the node is in.
 	 * @return Vector, of NodePosition objects resultant from the search.
	 * @exception java.sql.SQLException
	 */
	public Vector searchForTripleStoreNodes(PCSession session, String sViewID) throws SQLException {

		DBConnection dbcon = getDatabaseManager().requestConnection(session.getModelName()) ;

		Vector vtNodes = DBSearch.searchForTripleStoreNodes(dbcon, sViewID, session.getUserID());

		getDatabaseManager().releaseConnection(session.getModelName(), dbcon);

		return vtNodes;
	}
}
