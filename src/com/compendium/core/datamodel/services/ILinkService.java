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

import java.util.Vector;
import java.sql.SQLException;

import com.compendium.core.datamodel.*;
import com.compendium.core.db.*;

/**
 * The interface for the LinkService class
 * The LinkService class provides services to manipuate Link record data in the database.
 *
 * @author Sajid and Rema / Michelle Bachler
 */
public interface ILinkService extends IService {

	/**
	 * Add a new link in the database and returns it.
	 *
	 * @param PCSession session, the session object for the database to use.
	 * @param String sLinkID, the link id of the new link.
	 * @param java.util.Date dCreationDate, the date of creation of the link.
	 * @param java.util.Date dModificationDate, the date of modification of the link.
	 * @param String sAuthor, the author of this link.
	 * @param String sType, the type of this link.
	 * @param String sImportedID, the id of the link if importing.
	 * @param String sOriginalID, the original imported of this link.
	 * @param String sFromID, the source node of this link.
	 * @param String sToID, the destination node of this link.
	 * @param String sLabel, the label for this node.
	 * @return ILink, the new link object created.
	 * @exception java.sql.SQLException
	 */
	public Link createLink( PCSession session,
							String sLinkID,	java.util.Date dCreationDate,java.util.Date dModificationDate,
							String sAuthor,	String sType, String sImportedID, String sOriginalID,
							String sFromID,	String sToID, String sLabel)
		throws SQLException;

	/**
	 * Returns a link given its ID, if the node is fiund and not marked for deletion.
	 * @param PCSession session, the session object for the database to use.
	 * @param String sLinkID, the link id of the link to mark for deletion.
	 * @return Link, the link for the given id.
	 */
	public Link getLink(PCSession session, String sLinkID) throws SQLException;

	/**
	 * Returns a link given its ID no matter what the current status.
	 * @param PCSession session, the session object for the database to use.
	 * @param String sLinkID, the link id of the link to mark for deletion.
	 * @return Link, the link for the given id.
	 */
	public Link getAnyLink(PCSession session, String sLinkID) throws SQLException;

	/**
	 * Deletes a link from the database and returns true if successful
	 *
	 * @param PCSession session, the session object for the database to use.
	 * @param sLinkID, the link id of the link to mark for deletion.
	 * @param sViewID, the id of the view it is in.
	 * @return boolean, true if the link was successfully marked for deletion, else false.
	 * @exception java.sql.SQLException
	 */
	public boolean deleteLink(PCSession session, String sLinkID, String sViewID) throws SQLException;

	/**
	 * Purges a link from the database and returns true if successful
	 *
	 * @param PCSession session, the session object for the database to use.
	 * @param String sLinkID, the link id of the link to purge from the database.
	 * @param sViewID, the id of the view it is in.
	 * @return boolean, true if the link was successfully purged from the database.
	 * @exception java.sql.SQLException
	 */
	public boolean purgeLink(PCSession session, String sLinkID, String sViewID) throws SQLException;

	/**
	 * Purges all link for the given node in the given view returns true if successful
	 *
	 * @param PCSession session, the session object for the database to use.
	 * @param String sViewID, the view id of the view in which the link for the node are to be purged.
	 * @param String sNodeID, the node id of the node whose links to purge in the given view.
	 * @return boolean, true if the links were successfully purged for the given node and view, else false.
	 * @exception java.sql.SQLException
	 */
	public boolean purgeViewNode(PCSession session, String sViewID, String sNodeID) throws SQLException;

	/**
	 * Purges all links in the given view from the database and returns true if successful
	 *
	 * @param PCSession session, the session object for the database to use.
	 * @param String sViewID, the view id of the view whose links to purge.
	 * @return boolean, true if the links were successfully purged for the given view, else false.
	 * @exception java.sql.SQLException
	 */
	public boolean purgeAllLinks(PCSession session, String sViewID) throws SQLException;

	/**
	 * Restores the links for a given node in a given view.
	 *
	 * @param PCSession session, the session object for the database to use.
	 * @param String sNodeID, the node id of the node whose links to restore.
	 * @param String sViewID, the view id of the view for which to restore the nodes link's in.
	 * @return Vector, of the restored links.
	 * @exception java.sql.SQLException
	 */
	public Vector restoreNode(PCSession session, String sNodeID, String sViewID) throws SQLException;

	/**
	 * Restores a link in the database and returns true if successful
	 *
	 * @param PCSession session, the session object for the database to use.
	 * @param String sLinkID, the link id of the link to restore.
	 * @return boolean, true if the links were successfully restored, else false.
	 * @exception java.sql.SQLException
	 */
	public boolean restoreLink(PCSession session, String sLinkID) throws SQLException;

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
	public boolean setType(PCSession session, String sLinkID, String oldValue, String newValue) throws SQLException;

	/**
	 * Sets the label of this link
	 *
	 * @param PCSession session, the current session object.
	 * @param String sLinkID, the id of the link whose label to change
	 * @param String sLabel, the label of this link.
	 * @param java.util.Date dModificationDate, the modification date for the label change.
	 * @exception java.sql.SQLException
	 */
	public void setLabel(PCSession session, String sLinkID, String sLabel, java.util.Date dModificationDate) throws SQLException;


	/**
	 * Returns if the link with the given link id has been marked for deletion.
	 * @param PCSession session, the current session object.
	 * @param sLinkID, the id if the link to check the status for.
	 * @return boolean, true if the link is marked for deletion else false.
	 * @exception java.sql.SQLException
	 */
	public boolean isMarkedForDeletion(PCSession session, String sLinkID) throws SQLException;
}
