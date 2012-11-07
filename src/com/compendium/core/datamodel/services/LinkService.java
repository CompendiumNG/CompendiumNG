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

import java.sql.*;
import java.util.*;

import com.compendium.core.datamodel.*;
import com.compendium.core.db.*;
import com.compendium.core.db.management.*;

/**
 * The LinkService class provides services to manipuate Link record data in the database.
 *
 * @author Sajid and Rema / Michelle Bachler
 */
public class LinkService extends ClientService implements ILinkService, java.io.Serializable {

	/**
	 * Constructor
	 */
	public  LinkService() {
		super();
	}

	/**
	 * Constructor, set the name of the service.
	 *
	 * @param String sName, the name of this service.
	 */
	public LinkService(String sName) {
		super(sName);
	}

	/**
	 *	Constructor, set the name, ServiceManager and DBDatabaseManager for this service.
	 *
	 * @param String sName, the name of this service.
	 * @param ServiceManager sm, the ServiceManager used by this service.
	 * @param DBDatabaseManager dbMgr, the DBDatabaseManager used by this service.
	 */
	public  LinkService(String sName, ServiceManager sm,  DBDatabaseManager dbMgr) {
		super(sName, sm, dbMgr) ;
	}

	/**
	 * Add a new link in the database and returns it.
	 *
	 * @param session the session object for the database to use.
	 * @param String sViewID the view this link is being created in.
	 * @param String sLinkID the link id of the new link.
	 * @param dCreationDate the date of creation of the link.
	 * @param dModificationDate the date of modification of the link.
	 * @param sAuthor the author of this link.
	 * @param sType the type of this link.
	 * @param sImportedID the id of the link if importing.
	 * @param sOriginalID the original imported of this link.
	 * @param sFromID the source node of this link.
	 * @param sToID the destination node of this link.
	 * @param sLabel the label for this node.
	 * @return ILink, the new link object created.
	 * @exception java.sql.SQLException
	 */
	public Link createLink( PCSession session,
							String sLinkID,	java.util.Date dCreationDate,java.util.Date dModificationDate,
							String sAuthor,	String sType, String sImportedID, String sOriginalID,
							String sFromID,	String sToID, String sLabel)
		throws SQLException
	{
		// get connection object with db manager
		DBConnection dbcon = getDatabaseManager().requestConnection(session.getModelName()) ;
		
		Link link = DBLink.insert(dbcon, sLinkID, dCreationDate, dModificationDate,
								sAuthor, sType, sImportedID, sOriginalID,
								sFromID, sToID, sLabel);

		getDatabaseManager().releaseConnection(session.getModelName(),dbcon);

		return link;
	}

	/**
	 * Returns a link given its ID, if the node is fiund and not marked for deletion.
	 * @param PCSession session, the session object for the database to use.
	 * @param String sLinkID, the link id of the link to mark for deletion.
	 * @return Link, the link for the given id.
	 */
	public Link getLink(PCSession session, String sLinkID) throws SQLException {

		DBConnection dbcon = getDatabaseManager().requestConnection(session.getModelName()) ;

		Link link = DBLink.getLink(dbcon, sLinkID);

		getDatabaseManager().releaseConnection(session.getModelName(),dbcon);

		return link;
	}

	/**
	 * Returns a link given its ID no matter what the current status.
	 * @param PCSession session, the session object for the database to use.
	 * @param String sLinkID, the link id of the link to mark for deletion.
	 * @return Link, the link for the given id.
	 */
	public Link getAnyLink(PCSession session, String sLinkID) throws SQLException {

		DBConnection dbcon = getDatabaseManager().requestConnection(session.getModelName()) ;

		Link link = DBLink.getAnyLink(dbcon, sLinkID);

		getDatabaseManager().releaseConnection(session.getModelName(),dbcon);

		return link;
	}

	/**
	 * Deletes a link from the database and returns true if successful
	 *
	 * @param PCSession session, the session object for the database to use.
	 * @param sLinkID, the link id of the link to mark for deletion.
	 * @param sViewID, the id of the view it is in.
	 * @return boolean, true if the link was successfully marked for deletion, else false.
	 * @exception java.sql.SQLException
	 */
	public boolean deleteLink(PCSession session, String sLinkID, String sViewID) throws SQLException {

		DBConnection dbcon = getDatabaseManager().requestConnection(session.getModelName()) ;

		boolean deleted = DBLink.delete(dbcon, sLinkID, sViewID);

		getDatabaseManager().releaseConnection(session.getModelName(),dbcon);

		return deleted;
	}

	/**
	 * Purges a link from the database and returns true if successful
	 *
	 * @param PCSession session, the session object for the database to use.
	 * @param String sLinkID, the link id of the link to purge from the database.
	 * @param sViewID, the id of the view it is in.
	 * @return boolean, true if the link was successfully purged from the database.
	 * @exception java.sql.SQLException
	 */
	public boolean purgeLink(PCSession session, String sLinkID, String sViewID) throws SQLException {

		DBConnection dbcon = getDatabaseManager().requestConnection(session.getModelName()) ;

		boolean deleted = DBLink.purge(dbcon, sLinkID, sViewID);

		getDatabaseManager().releaseConnection(session.getModelName(),dbcon);

		return deleted;
	}

	/**
	 * Restores a link in the database and returns true if successful
	 *
	 * @param PCSession session, the session object for the database to use.
	 * @param String sLinkID, the link id of the link to restore.
	 * @return boolean, true if the links were successfully restored, else false.
	 * @exception java.sql.SQLException
	 */
	public boolean restoreLink(PCSession session, String sLinkID) throws SQLException {

		DBConnection dbcon = getDatabaseManager().requestConnection(session.getModelName()) ;

		boolean restored = DBLink.restore(dbcon, sLinkID);

		getDatabaseManager().releaseConnection(session.getModelName(),dbcon);

		return restored;
	}

	/**
	 * Restores the links for a given node in a given view.
	 *
	 * @param PCSession session, the session object for the database to use.
	 * @param String sNodeID, the node id of the node whose links to restore.
	 * @param String sViewID, the view id of the view for which to restore the nodes link's in.
	 * @return Vector, of the restored links.
	 * @exception java.sql.SQLException
	 */
	public Vector restoreNode(PCSession session, String sNodeID, String sViewID) throws SQLException {

		DBConnection dbcon = getDatabaseManager().requestConnection(session.getModelName()) ;

		Vector links = DBLink.restoreNode(dbcon, sNodeID, sViewID);

		getDatabaseManager().releaseConnection(session.getModelName(),dbcon);

		return links;
	}

	/**
	 * Purges all link for the given node in the given view returns true if successful
	 *
	 * @param PCSession session, the session object for the database to use.
	 * @param String sViewID, the view id of the view in which the link for the node are to be purged.
	 * @param String sNodeID, the node id of the node whose links to purge in the given view.
	 * @return boolean, true if the links were successfully purged for the given node and view, else false.
	 * @exception java.sql.SQLException
	 */
	public boolean purgeViewNode(PCSession session, String sViewID, String sNodeID) throws SQLException {

		DBConnection dbcon = getDatabaseManager().requestConnection(session.getModelName()) ;

		boolean deleted = DBLink.purgeViewNode(dbcon, sViewID, sNodeID);

		getDatabaseManager().releaseConnection(session.getModelName(),dbcon);

		return deleted;
	}

	/**
	 * Purges all links in the given view from the database and returns true if successful
	 *
	 * @param PCSession session, the session object for the database to use.
	 * @param String sViewID, the view id of the view whose links to purge.
	 * @return boolean, true if the links were successfully purged for the given view, else false.
	 * @exception java.sql.SQLException
	 */
	public boolean purgeAllLinks(PCSession session, String sViewID) throws SQLException {

		DBConnection dbcon = getDatabaseManager().requestConnection(session.getModelName());

		boolean deleted = DBLink.purgeAll(dbcon, sViewID);

		getDatabaseManager().releaseConnection(session.getModelName(),dbcon);

		return deleted;
	}

	/**
	 * CURRENTLY NOT IMPLEMENTED.<br>
	 * Gets the type of this link
	 *
	 * @param PCSession session, the session object for the database to use.
	 * @param String sLinkID, the link id of the link whose type to return.
	 * @return int, the link type for the link.
	 * @exception java.sql.SQLException
	 */
	public int getType(PCSession session, String sLinkID) throws SQLException {
		//String modelName = session.getModelName() ;
		// get from db
		return -1;
	}

	/**
	 * Sets the type of the link
	 *
	 * @param PCSession session, the session object for the database to use.
	 * @param String sLinkID, the link id of the link whose type to set.
	 * @param String oldValue, the original link type for this link.
	 * @param String newValue, the new link type for this link.
	 * @return boolean, true if the link type was successfully changed, else false.
	 * @exception java.sql.SQLException
	 */
	public boolean setType(PCSession session, String id, String oldValue, String newValue) throws SQLException {

		String modelName = session.getModelName();

		DBConnection dbcon = getDatabaseManager().requestConnection(modelName);

		boolean typeChanged = DBLink.setType(dbcon, id, newValue);

		getDatabaseManager().releaseConnection(modelName,dbcon);

		return typeChanged;
	}

	/**
	 * Sets the label of this link
	 *
	 * @param PCSession session, the current session object.
	 * @param String sLinkID, the id of the link whose label to change
	 * @param String sLabel, the label of this link.
	 * @param java.util.Date dModificationDate, the modification date for the label change.
	 * @exception java.sql.SQLException
	 */
	public void setLabel(PCSession session, String sLinkID, String sLabel, java.util.Date dModificationDate) throws SQLException {

		String modelName = session.getModelName();

		DBConnection dbcon = getDatabaseManager().requestConnection(session.getModelName());

		boolean labelChanged = DBLink.setLabel(dbcon, sLinkID, sLabel, dModificationDate);

		getDatabaseManager().releaseConnection(session.getModelName(),dbcon);
	}

	/**
	 * Returns if the link with the given link id has been marked for deletion.
	 * @param PCSession session, the current session object.
	 * @param sLinkID, the id if the link to check the status for.
	 * @return boolean, true if the link is marked for deletion else false.
	 * @exception java.sql.SQLException
	 */
	public boolean isMarkedForDeletion(PCSession session, String sLinkID) throws SQLException {

		DBConnection dbcon = getDatabaseManager().requestConnection(session.getModelName()) ;

		boolean deleted = DBLink.isMarkedForDeletion(dbcon, sLinkID);

		getDatabaseManager().releaseConnection(session.getModelName(),dbcon);

		return deleted;
	}

	/**
	 * CURRENTLY NOT IMPLEMENTED.<br>
	 * Gets the original imported id of this link
	 *
	 * @param PCSession session, the session object for the database to use.
	 * @param String sLinkID, the link id of the link whose original id to return.
	 * @return String, the original id for the link.
	 * @exception java.sql.SQLException
	 */
	public String getOriginalID(PCSession session, String sLinkID) throws SQLException {
		//String modelName = session.getModelName() ;
		// get from db
		return ""; //$NON-NLS-1$
	}

	/**
	 * CURRENTLY NOT IMPLEMENTED.<br>
	 * Sets the original imported id of the link
	 *
	 * @param PCSession session, the session object for the database to use.
	 * @param String sLinkID, the link id of the link whose original id to set.
	 * @param String oldValue, the original original id for this link.
	 * @param String newValue, the new original id for this link.
	 * @exception java.sql.SQLException
	 */
	public void setOriginalID(PCSession session, String sLinkID, String oldValue, String newValue) throws SQLException {
		String modelName = session.getModelName() ;
		// update db
	}

	/**
	 * CURRENTLY NOT IMPLEMENTED.<br>
	 * Returns the node from which this link originates.
	 *
	 * @param PCSession session, the session object for the database to use.
	 * @param String sLinkID, the link id of the link whose originating node to return.
	 * @return the node from which this link originates
	 * @exception java.sql.SQLException
	 */
	public INodeSummary getFrom(PCSession session, String sLinkID) throws SQLException {
		//String modelName = session.getModelName() ;
		// get from db
		return null;
	}

	/**
	 * CURRENTLY NOT IMPLEMENTED.<br>
	 * Sets this link's originating node
	 *
	 * @param PCSession session, the session object for the database to use.
	 * @param String sLinkID, the link id of the link whose originating node to set.
	 * @param INodeSummary oldValue, the original originating node for this link.
	 * @param INodeSummary newValue, the new originating node for this link.
	 * @exception java.sql.SQLException
	 */
	public void setFrom(PCSession session, String sLinkID, INodeSummary oldValue, INodeSummary newValue)
		throws SQLException  {
		//String modelName = session.getModelName() ;
	}

	/**
	 * CURRENTLY NOT IMPLEMENTED.<br>
	 * Returns the destination node of this link.
	 *
	 * @param PCSession session, the session object for the database to use.
	 * @param String sLinkID, the link id of the link whose destination node to return.
	 * @return the destination node of this link.
	 * @exception java.sql.SQLException
	 */
	public INodeSummary getTo(PCSession session, String sLinkID) throws SQLException {
		//String modelName = session.getModelName() ;
		return null;
	}

	/**
	 * CURRENTLY NOT IMPLEMENTED.<br>
	 * Sets this link's destination node
	 *
	 * @param PCSession session, the session object for the database to use.
	 * @param String sLinkID, the link id of the link whose destination node to set.
	 * @param INodeSummary oldValue, the original destination node for this link.
	 * @param INodeSummary newValue, the new destination node for this link.
	 * @exception java.sql.SQLException
	 */
	public void setTo(PCSession session, String sLinkID, INodeSummary oldValue, INodeSummary newValue) throws SQLException {
		//String modelName = session.getModelName() ;
		// update db
	}
}
