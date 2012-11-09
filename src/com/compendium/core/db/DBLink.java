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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.Vector;
import java.io.*;
import javax.swing.*;

import com.compendium.core.datamodel.*;
import com.compendium.core.db.management.*;
import com.compendium.core.ICoreConstants;

/**
 * The DBLink class serves as the interface layer between the RLink objects
 * and the Link table in the database.
 *
 * @author	rema and sajid / Michelle Bachler
 */
public class DBLink {

	// AUDITED
	/** SQL statement to insert a new Link Record into the Link table.*/
	public final static String INSERT_LINK_QUERY =
		"INSERT INTO Link (LinkID, CreationDate, ModificationDate, Author, LinkType, " +
		"OriginalID, FromNode, ToNode, Label, CurrentStatus) "+
		"VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ";

	/** SQL statement to update a Link Record into the Link table.*/
	public final static String UPDATE_LINK_QUERY =
		"UPDATE Link set CreationDate=?, ModificationDate=?, Author=?, LinkType=?, " +
		"OriginalID=?, FromNode=?, ToNode=?, Label=?, CurrentStatus=? WHERE LinkID=? ";

	/** SQL statement to update a link type for a record that already exists in the table.*/
	public final static String UPDATE_LINK_TYPE_QUERY =
		"UPDATE Link " +
		"SET ModificationDate = ?,  " +
		"LinkType = ? "+
		"WHERE LinkID = ? ";

	/** SQL statement to update a link arrow type for a record that already exists in the table.*/
	public final static String UPDATE_LINK_ARROW_QUERY =
		"UPDATE Link " +
		"SET ModificationDate = ?,  " +
		"Arrow = ? "+
		"WHERE LinkID = ? ";

	/** SQL statement to set the Link status to deleted for the link with the given id.*/
	public final static String DELETE_LINK_QUERY =
		"UPDATE Link "+
		"SET CurrentStatus = "+ICoreConstants.STATUS_DELETE+
		" WHERE LinkID = ?";

	/** SQL statement to set the Link status to active for the link with the given id.*/
	public final static String RESTORE_LINK_QUERY =
		"UPDATE Link "+
		"SET CurrentStatus = "+ICoreConstants.STATUS_ACTIVE+
		" WHERE LinkID = ?";

	/** SQL statement to set the Link status to active for the links with the given FromNode, ToNode, or ViewID.*/
	public final static String RESTORE_NODE_QUERY =
		"UPDATE Link " +
		"SET CurrentStatus = " + ICoreConstants.STATUS_ACTIVE +
		" WHERE (FromNode = ? OR ToNode = ?) AND CurrentStatus="+ICoreConstants.STATUS_DELETE;

	/** SQL statement to delete a Link with the given LinkID.*/
	public final static String PURGE_LINK_QUERY =
		"DELETE FROM Link "+
		"WHERE LinkID = ?";

	/** SQL statement to delete a bunch of Links with the given LinkIDs.*/
	public final static String PURGE_ALL_LINKS_QUERY =
		"DELETE FROM Link "+
		"WHERE LinkID IN ";

	/** SQL statement to set a Link label for a link with the given id.*/
	public final static String SET_LINK_LABEL_QUERY =
		"Update Link " +
		"SET Label = ?, ModificationDate = ? " +
		"WHERE LinkID = ? ";

// UNAUDITED
	public final static String DELETED_NODE_QUERY =
		"SELECT LinkID FROM Link " +
		"WHERE (FromNode = ? OR ToNode = ?) AND CurrentStatus="+ICoreConstants.STATUS_DELETE;

	/** SQL statement to return a Link record for the given LinkID AND to or from NodeID.*/
	public final static String GET_LINKNODE_QUERY =
		"SELECT LinkID FROM Link " +
		"WHERE LinkID = ? AND (FromNode = ? OR ToNode = ?)";

	/** SQL statement to return  a Link record for the given LinkID.*/
	public final static String GET_ANYLINK_QUERY =
		"SELECT LinkID, CreationDate, ModificationDate, Author, LinkType, " +
		"OriginalID, FromNode, ToNode, Label " +
		"FROM Link "+
		"WHERE LinkID = ?";

	/** SQL statement to return all Link record for the given FromNode or ToNode ids.*/
	public final static String GET_ALLNODE_QUERY =
		"SELECT LinkID, CreationDate, ModificationDate, Author, LinkType, " +
		"OriginalID, FromNode, ToNode, Label " +
		"FROM Link "+
		"WHERE FromNode = ? OR ToNode = ?";

	/** SQL statement to return the Link record for the given LinkID where the record Status is active.*/
	public final static String GET_LINK_QUERY =
		"SELECT LinkID, CreationDate, ModificationDate, Author, LinkType, " +
		"OriginalID, FromNode, ToNode, Label " +
		"FROM Link "+
		"WHERE LinkID = ? "+
		"AND CurrentStatus = "+ICoreConstants.STATUS_ACTIVE;

	/** SQL statement to return the Link record for the given OriginalID where the record Status is active.*/
	public final static String GET_IMPORTED_LINK_QUERY =
		"SELECT LinkID, CreationDate, ModificationDate, Author, LinkType, " +
		"OriginalID, FromNode, ToNode, Label "+
		"FROM Link "+
		"WHERE OriginalID = ? "+
		"AND CurrentStatus = "+ICoreConstants.STATUS_ACTIVE;

	/** SQL statement to return the Link status for the link with the given id.*/
	public final static String GET_DELETESTATUS_QUERY =
		"SELECT CurrentStatus " +
		"FROM Link "+
		"WHERE LinkID = ?";


	/**
	 * 	Inserts a new link in the database and returns it.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param linkId, the link id.
	 *	@param creationDate, the date of creation of the link.
	 *	@param modificationDate, the date of modification of the link.
	 *	@param author, the author of this link.
	 *	@param type, the type of this link.
	 *  @param sImportedID, the id of the link if importing.
	 *	@param sOriginalID, the original id of this link.
	 *	@param fromId, the source node of this link.
	 *	@param toId, the destination node of this link.
	 *	@param sLabel, the label of this link.
	 *	@return com.compendium.core.datamode.ILink, the link object.
	 *	@throws java.sql.SQLException
	 */
	public static Link insert(DBConnection dbcon, String linkId, java.util.Date creationDate,
					java.util.Date modificationDate, String author, String type, String sImportedID, String sOriginalID,
					String fromId, String toId, String sLabel)
					throws SQLException {

		Connection con = dbcon.getConnection();
		if (con == null) {
			System.out.println("Returning as con=null");
			return null;
		}

		Link link = null;

		// IF IMPORTING FROM XML
		if (DBNode.getImporting()) {
			link = DBLink.getAnyLink(dbcon, sImportedID);
			if (link != null) {
				restore(dbcon, sImportedID);
			}

			// IF IMPORTING WITH TRANSCLUSION
			if ( (link != null) && DBNode.getImportAsTranscluded()) {
				if (DBNode.getUpdateTranscludedNodes()) {
					Link updatedlink = update(dbcon, link.getId(), creationDate, modificationDate, author, type, sOriginalID, fromId, toId, sLabel);
					if (updatedlink != null)
						return updatedlink;
					else
						return link;
				}
				else {
					return link;
				}
			}

			// IF PRESERVING LINK IDS
			if (DBNode.getPreserveImportedIds()) {
				linkId = sImportedID;
			}
		}

		PreparedStatement pstmt = con.prepareStatement(INSERT_LINK_QUERY);

		pstmt.setString(1, linkId);
		pstmt.setDouble(2, new Long(creationDate.getTime()).doubleValue());
		pstmt.setDouble(3, new Long(modificationDate.getTime()).doubleValue());
		pstmt.setString(4, author);
		pstmt.setString(5, type);
		pstmt.setString(6, sOriginalID);
		pstmt.setString(7, fromId);
		pstmt.setString(8, toId);

		// THIS IS A MEMO FIELD
		if (!sLabel.equals("")) {
			//ByteArrayInputStream bArrayLabel = new ByteArrayInputStream(label.getBytes());
			//pstmt.setAsciiStream(9, bArrayLabel, bArrayLabel.available());

			// ACCOMODATES UNICODE
			StringReader reader = new StringReader(sLabel);
			pstmt.setCharacterStream(9, reader, sLabel.length());
		}
		else {
			pstmt.setString(9, "");
		}

		pstmt.setInt(10, ICoreConstants.STATUS_ACTIVE);

		int nRowCount = 0;
		try {
			nRowCount = pstmt.executeUpdate();
		} catch (Exception e){
			e.printStackTrace();
		}
		pstmt.close();

		if (nRowCount > 0) {
			link = Link.getLink(linkId, creationDate, modificationDate, author, type, sOriginalID, sLabel);
			if (DBAudit.getAuditOn()) {
				try {
					DBAudit.auditLink(dbcon, DBAudit.ACTION_ADD, link, fromId, toId);
				}
				catch(Exception ex) {
					ex.printStackTrace();
				}
			}
			return link;
		}
		else
			return null;
	}

	/**
	 * 	Inserts a new link in the database with the given status and returns it.
	 * 	Used to recreate a record from another record.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param linkId, the link id.
	 *	@param creationDate, the date of creation of the link.
	 *	@param modificationDate, the date of modification of the link.
	 *	@param author, the author of this link.
	 *	@param type, the type of this link.
	 *  @param sImportedID, the id of the link if importing.
	 *	@param sOriginalID, the original id of this link.
	 *	@param fromId, the source node of this link.
	 *	@param toId, the destination node of this link.
	 *	@param sLabel, the label of this link.
	 *	@param nStatus, the status of this link.
	 *	@return com.compendium.core.datamode.ILink, the link object.
	 *	@throws java.sql.SQLException
	 */
	public static Link recreate(DBConnection dbcon, String linkId, java.util.Date creationDate,
					java.util.Date modificationDate, String author, String type, String sImportedID, String sOriginalID,
					String fromId, String toId, String sLabel, int nStatus)
					throws SQLException {

		Connection con = dbcon.getConnection();
		if (con == null)
			return null;

		Link link = null;
		PreparedStatement pstmt = con.prepareStatement(INSERT_LINK_QUERY);

		pstmt.setString(1, linkId);
		pstmt.setDouble(2, new Long(creationDate.getTime()).doubleValue());
		pstmt.setDouble(3, new Long(modificationDate.getTime()).doubleValue());
		pstmt.setString(4, author);
		pstmt.setString(5, type);
		pstmt.setString(6, sOriginalID);
		pstmt.setString(7, fromId);
		pstmt.setString(8, toId);

		// THIS IS A MEMO FIELD
		if (!sLabel.equals("")) {
			//ByteArrayInputStream bArrayLabel = new ByteArrayInputStream(label.getBytes());
			//pstmt.setAsciiStream(9, bArrayLabel, bArrayLabel.available());

			// ACCOMODATES UNICODE
			StringReader reader = new StringReader(sLabel);
			pstmt.setCharacterStream(9, reader, sLabel.length());
		}
		else {
			pstmt.setString(9, "");
		}

		pstmt.setInt(10, nStatus);
		
		int nRowCount = 0;
		try {
			nRowCount = pstmt.executeUpdate();
		} catch (Exception e){
			e.printStackTrace();
		}

		pstmt.close();

		if (nRowCount > 0) {
			link = Link.getLink(linkId, creationDate, modificationDate, author, type, sOriginalID, sLabel);
			return link;
		}
		else
			return null;
	}

	/**
	 * 	Update the link with the given id in the database and returns it.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param linkId, the link id.
	 *	@param creationDate, the date of creation of the link.
	 *	@param modificationDate, the date of modification of the link.
	 *	@param author, the author of this link.
	 *	@param type, the type of this link.
	 *	@param sOriginalID, the original imported of this link.
	 *	@param fromId, the source node of this link.
	 *	@param toId, the destination node of this link.
	 *	@param containingViewId, the view which contains this link.
	 *	@return com.compendium.core.datamode.ILink, the link object.
	 *	@throws java.sql.SQLException
	 */
	public static Link update(DBConnection dbcon, String linkId, java.util.Date creationDate,
					java.util.Date modificationDate, String author, String type, String sOriginalID,
					String fromId, String toId, String sLabel)
					throws SQLException {

		Connection con = dbcon.getConnection();
		if (con == null)
			return null;

		PreparedStatement pstmt = con.prepareStatement(UPDATE_LINK_QUERY);
		pstmt.setDouble(1, new Long(creationDate.getTime()).doubleValue());
		pstmt.setDouble(2, new Long(modificationDate.getTime()).doubleValue());
		pstmt.setString(3, author);
		pstmt.setString(4, type);
		pstmt.setString(5, sOriginalID);
		pstmt.setString(6, fromId);
		pstmt.setString(7, toId);

		// THIS IS A MEMO FIELD
		if (!sLabel.equals("")) {
			//ByteArrayInputStream bArrayLabel = new ByteArrayInputStream(label.getBytes());
			//pstmt.setAsciiStream(8, bArrayLabel, bArrayLabel.available());

			// ACCOMODATES UNICODE
			StringReader reader = new StringReader(sLabel);
			pstmt.setCharacterStream(8, reader, sLabel.length());
		}
		else {
			pstmt.setString(8, "");
		}
		pstmt.setInt(9, ICoreConstants.STATUS_ACTIVE);
		pstmt.setString(10, linkId);

		int nRowCount = 0;
		try {
			nRowCount = pstmt.executeUpdate();
		} catch (Exception e){
			e.printStackTrace();
		}

		pstmt.close();

		Link link = null;
		if (nRowCount > 0) {
			link = 	DBLink.getAnyLink(dbcon, linkId);
			if (DBAudit.getAuditOn()) {
				try {
					DBAudit.auditLink(dbcon, DBAudit.ACTION_ADD, link, fromId, toId);
				}
				catch(Exception ex) {
					ex.printStackTrace();
				}
			}
			return link;
		}
		else
			return null;
	}

	/**
	 *	Updates a link type in the table and returns boolean value true/false depending on success state.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param sLinkID, the id of the link whose type to set.
	 *	@param nType, the new type of this link.
	 *	@return boolean, true if the update was successful, else false.
	 *	@throws java.sql.SQLException
	 */
	public static boolean setType(DBConnection dbcon, String sLinkID, String sType) throws SQLException {

		Connection con = dbcon.getConnection();
		if (con == null)
			return false;

		double date = new Long((new Date()).getTime()).doubleValue();

		PreparedStatement pstmt = con.prepareStatement(UPDATE_LINK_TYPE_QUERY);

		pstmt.setDouble(1, date);
		pstmt.setString(2, sType);
		pstmt.setString(3, sLinkID);

		int nRowCount = 0;
		try {
			nRowCount = pstmt.executeUpdate();
		} catch (Exception e){
			e.printStackTrace();
		}
		pstmt.close();

		if (nRowCount >0) {
			if (DBAudit.getAuditOn()) {
				Link link = DBLink.getAnyLink(dbcon, sLinkID);
				DBAudit.auditLink(dbcon, DBAudit.ACTION_EDIT, link);
			}

			return true;
		}
		else
			return false;
	}

	/**
	 *  Sets the label of the link in the database and returns if successful.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param sLinkID, the id of the link to set the label for.
	 *	@param sLabel, the new label of the link.
	 *	@param dModificationDate java.util.Date, the date for this modification to the link record.
	 *	@return boolean value, the success or failure of the operation.
	 *	@exception java.sql.SQLException
	 */
	public static boolean setLabel(DBConnection dbcon, String sLinkID, String sLabel, java.util.Date dModificationDate) throws SQLException {

		Connection con = dbcon.getConnection();
		if (con == null)
			return false;

		double date = new Long(dModificationDate.getTime()).doubleValue();
		PreparedStatement pstmt = con.prepareStatement(SET_LINK_LABEL_QUERY);

		// THIS IS A MEMO FIELD
		if (!sLabel.equals("")) {
			//ByteArrayInputStream bArrayLabel = new ByteArrayInputStream(label.getBytes());
			//pstmt.setAsciiStream(1, bArrayLabel, bArrayLabel.available());

			// ACCOMODATES UNICODE
			StringReader reader = new StringReader(sLabel);
			pstmt.setCharacterStream(1, reader, sLabel.length());
		}
		else {
			pstmt.setString(1, "");
		}

		pstmt.setDouble(2, date);
		pstmt.setString(3, sLinkID);

		int nRowCount = 0;
		try {
			nRowCount = pstmt.executeUpdate();
		} catch (Exception e){
			e.printStackTrace();
		}
		pstmt.close();

		if (nRowCount > 0) {
			if (DBAudit.getAuditOn()) {
				Link link = DBLink.getLink(dbcon, sLinkID);
				DBAudit.auditLink(dbcon, DBAudit.ACTION_EDIT, link);
			}
			return true;
		}
		else
			return false ;
	}

	/**
	 *  Mark the link with the given id for deletion, and return if sucessful.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param sLinkID, the id of the link whose Status to update to deleted.
	 *	@param sViewID, the id of the view the link to be deleted is in.
	 *	@return boolean, true if the update was successful, else false.
	 *	@throws java.sql.SQLException
	 */
	public static boolean delete(DBConnection dbcon, String sLinkID, String sViewID) throws SQLException {

		Connection con = dbcon.getConnection();
		if (con == null)
			return false;

		// ONLY MARK FOR DELETION IF IT IS THE LAST ACTIVE INSTANCE - OTHERWISE DELETE FROM VIEWLINK
		Vector occurence = DBViewLink.getActiveViewCount(dbcon, sLinkID);
		if ( occurence.size() == 1 && ((String)occurence.elementAt(0)).equals(sViewID) ) {
			PreparedStatement pstmt = con.prepareStatement(DELETE_LINK_QUERY);
			pstmt.setString(1, sLinkID);
			int nRowCount = 0;
			try {
				nRowCount = pstmt.executeUpdate();
			} catch (Exception e){
				e.printStackTrace();
			}
			pstmt.close();

			if (nRowCount > 0) {
				if (DBAudit.getAuditOn()) {
					Link link = DBLink.getAnyLink(dbcon, sLinkID);
					DBAudit.auditLink(dbcon, DBAudit.ACTION_DELETE, link);
				}

				return true;
			}
			else
				return false;
		}
		else {
			DBViewLink.delete(dbcon, sViewID, sLinkID);
			return true;
		}
	}

	/**
	 *  Marks the links with the given to/from id in the given view, as deleted, and return  if sucessful.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param sNodeID, the id of the ToNode, FromNode fields of the link whose Status to mark as deleted.
	 *	@param sViewID, the id of the view field of the link whose Status to mark as deleted.
	 *	@return boolean, true if the update was successful, else false.
	 *	@throws java.sql.SQLException
	 */
	public static boolean deleteNode(DBConnection dbcon, String sNodeID, String sViewID) throws SQLException {

		Connection con = dbcon.getConnection();
		if (con == null)
			return false;

		// GET ALL THE VIEWLINKS FOR THE GIVEN VIEW
		Vector links = DBViewLink.getLinks(dbcon, sViewID);
		int count = links.size();

		// FOR EACH LINK IN THE GIVEN VIEW, CHECK IF THE TO/FROM NODE = THE GIVEN NODEID
		// IF IT DOES, MARK IT AS DELETED
		for (int i=0; i<count; i++) {
			LinkProperties viewlink = (LinkProperties)links.elementAt(i);
			String sLinkID = viewlink.getLink().getId();

			PreparedStatement pstmt = con.prepareStatement(GET_LINKNODE_QUERY);
			pstmt.setString(1, sLinkID);
			pstmt.setString(2, sNodeID);
			pstmt.setString(3, sNodeID);

			ResultSet rs = null;
			try {
				rs = pstmt.executeQuery();
			} catch (Exception e){
				e.printStackTrace();
			}

			Link link = null;
			if (rs != null) {
				while (rs.next()) {
					String lId = rs.getString(1);
					DBLink.delete(dbcon, sLinkID, sViewID);
					DBViewLink.delete(dbcon, sViewID, sLinkID);
				}
				pstmt.close();
			}
			else {
				pstmt.close();
				return false;
			}
		}
		return true;
	}

	/**
	 *  Restore the link with the given id, and return  if sucessful.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param sLinkID, the id of the link whose Status to update to active.
	 *	@return boolean, true if the update was successful, else false.
	 *	@throws java.sql.SQLException
	 */
	public static boolean restore(DBConnection dbcon, String sLinkID) throws SQLException {

		Connection con = dbcon.getConnection();
		if (con == null)
			return false;

		PreparedStatement pstmt = con.prepareStatement(RESTORE_LINK_QUERY);
		pstmt.setString(1, sLinkID);
		int nRowCount = 0;
		try {
			nRowCount = pstmt.executeUpdate();
		} catch (Exception e){
			e.printStackTrace();
		}
		pstmt.close();

		if (nRowCount > 0) {
			if (DBAudit.getAuditOn()) {
				Link link = DBLink.getAnyLink(dbcon, sLinkID);
				DBAudit.auditLink(dbcon, DBAudit.ACTION_RESTORE, link);
			}

			return true;
		}
		else
			return false;
	}

	/**
	 *  Restore the links with the given nodeid in the given view, and return  if sucessful.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param sNodeID, the id of the ToNode, FromNode fields of the link whose Status to mark as active.
	 *	@param sViewID, the id of the view field of the link whose Status to mark as active.
	 *	@return boolean, true if the update was successful, else false.
	 *	@throws java.sql.SQLException
	 */
	public static Vector restoreNode(DBConnection dbcon, String sNodeID, String sViewID) throws SQLException {

		Vector returnLinks = new Vector();

		Connection con = dbcon.getConnection();
		if (con == null) {
			return returnLinks;
		}

		PreparedStatement pstmt = con.prepareStatement(RESTORE_NODE_QUERY);
		pstmt.setString(1, sNodeID);
		pstmt.setString(2, sNodeID);

		int nRowCount = 0;
		try {
			nRowCount = pstmt.executeUpdate();
		} catch (Exception e){
			e.printStackTrace();
		}
		pstmt.close();

		if (nRowCount > 0) {
			Vector linkids = DBLink.getDeletedNodeLinks(dbcon, sNodeID);
			int count = linkids.size();

			// RESTORE THE ASSOCIATED VIEWLINKS FOR THE RESTORED LINKS
			for (int i = 0; i < count; i++) {
				String link = (String) linkids.elementAt(i);
				DBViewLink.restore(dbcon, sViewID, link);
				LinkProperties props = DBViewLink.getLink(dbcon, sViewID, link);
				returnLinks.addElement(props);
				
				if (DBAudit.getAuditOn()) {
					DBAudit.auditLink(dbcon, DBAudit.ACTION_RESTORE, props.getLink());
				}
			}
		}
		return returnLinks;
	}

	/**
	 *  Purge the link with the given id from the database, and return  if sucessful.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param sLinkID, the id of the link to delete.
	 *	@param sViewID, the id of the view it is in.
	 *	@return boolean, true if the deletion was successful, else false.
	 *	@throws java.sql.SQLException
	 */
	public static boolean purge(DBConnection dbcon, String sLinkID, String sViewID) throws SQLException {

		Connection con = dbcon.getConnection();
		if (con == null)
			return false;

		// ONLY PURGE IF IT IS NOT ACTIVE IN ANY VIEWS - OR THIS IS THE LAST VIEW IT IS ACTIVE IN
		// OTHERWISE JUST MARK FOR DELETION
		Vector occurence = DBViewLink.getActiveViewCount(dbcon, sLinkID);
		if ( occurence.size() == 0 || ( occurence.size() == 1 && ((String)occurence.elementAt(0)).equals(sViewID) ) ) {

			// IF AUDITING, STORE LINK DATA
			Link link = null;
			if (DBAudit.getAuditOn()) {
				link = DBLink.getAnyLink(dbcon, sLinkID);
			}

			PreparedStatement pstmt = con.prepareStatement(PURGE_LINK_QUERY);
			pstmt.setString(1, sLinkID);
			int nRowCount = 0;
			try {
				nRowCount = pstmt.executeUpdate();
			} catch (Exception e){
				e.printStackTrace();
			}
			pstmt.close();

			if (nRowCount > 0) {
				if (DBAudit.getAuditOn() && link != null)
					DBAudit.auditLink(dbcon, DBAudit.ACTION_PURGE, link);

				return true;
			}
			else
				return false;
		}
		else {
			DBViewLink.delete(dbcon, sViewID, sLinkID);
			return true;
		}
	}

	/**
	 * Purge the links with the given fromid or toid in the given view from the database, and return if successful.
	 */
	public static boolean purgeViewNode(DBConnection dbcon, String sViewID, String sNodeID) throws SQLException {

		Connection con = dbcon.getConnection();
		if (con == null) {
			return false;
		}

		// GET ALL THE VIEWLINKS FOR THE GIVEN VIEW
//		Vector links = DBViewLink.getLinks(dbcon, sViewID);
		
		// MLB: Replaced the above method with the one below that filters the returned link list
		// with only those with a matching to or from ID.  This saves a lot of unnecessary link
		// fetching from the database.  That said, I do not see how it's possible to have an active
		// link in the view for a node that's just now being pasted into a view, and based on this
		// thinking, this code would NEVER return anything, making this purgeViewNode() method
		// unnecessary.  Makes me wonder if I'm missing something.............
		
		Vector links = DBViewLink.getLinks(dbcon, sViewID, sNodeID);  
		int count = links.size();

		// FOR EACH LINK IN  THE GIVEN VIEW, CHECK IF THE TO/FROM NODE = THE GIVEN NODEID
		// IF IT DOES, PURGE THE VIEWLINK AND IF THE LAST VIEW LINK IS REMOVED
		// PURGE THE LINK ITSELF
		for (int i=0; i<count; i++) {
			LinkProperties viewlink = (LinkProperties)links.elementAt(i);
			Link next = viewlink.getLink();
			String sLinkID = next.getId();

			PreparedStatement pstmt = con.prepareStatement(GET_LINKNODE_QUERY);
			pstmt.setString(1, sLinkID);
			pstmt.setString(2, sNodeID);
			pstmt.setString(3, sNodeID);

			ResultSet rs = null;
			try {
				rs = pstmt.executeQuery();
			} catch (Exception e){
				e.printStackTrace();
			}

			Link link = null;
			if (rs != null) {
				while (rs.next()) {
					String lId = rs.getString(1);
					DBViewLink.purge(dbcon, sViewID, sLinkID);

					// IF THIS LINK NOW NO LONGER APPEARS IN ANY VIEW, PURGE IT
					if ((DBViewLink.getViews(dbcon, sLinkID)).size() == 0) {
						purge(dbcon, sLinkID, sViewID);
					}
				}
				pstmt.close();
			}
			else {
				pstmt.close();
				return false;
			}
		}
		return true;
	}

	/**
	 *  Purge all the links from the given view from the database, and return if sucessful.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param sViewID, the id of the view field of the links to delete.
	 *	@return boolean, true if the deletions were successful, else false.
	 *	@throws java.sql.SQLException
	 */
	public static boolean purgeAll(DBConnection dbcon, String sViewID) throws SQLException {

		Connection con = dbcon.getConnection();
		if (con == null)
			return false;

		Vector data = DBViewLink.getLinks(dbcon, sViewID);
		int count = data.size();
		if (count == 0)
			return true;

		String ids = "";
		for (int i=0; i<count; i++) {
			LinkProperties link = (LinkProperties)data.elementAt(i);
			if (i<count-1)
				ids += "'"+link.getLink().getId()+"',";
			else
				ids += "'"+link.getLink().getId()+"'";
		}

		PreparedStatement pstmt = con.prepareStatement(PURGE_ALL_LINKS_QUERY+"("+ids+")");
		int nRowCount = 0;
		try {
			nRowCount = pstmt.executeUpdate();
		} catch (Exception e){
			e.printStackTrace();
		}
		pstmt.close();

		if (nRowCount > 0) {
			if (DBAudit.getAuditOn()) {
				for (int i=0; i<count; i++) {
					DBAudit.auditLink(dbcon, DBAudit.ACTION_PURGE, (Link)data.elementAt(i));
				}
			}
			return true;
		}
		else
			return false;
	 }

// GETTERS
	/**
	 * Retrieves the link ids with the given fromid/toid from the database and returns it.
	 */
	private static Vector getDeletedNodeLinks(DBConnection dbcon, String sNodeID) throws
		SQLException {

		Vector links = new Vector(51);

		Connection con = dbcon.getConnection();
		if (con == null) {
			return links;
		}

		PreparedStatement pstmt = con.prepareStatement(DELETED_NODE_QUERY);
		pstmt.setString(1, sNodeID);
		pstmt.setString(2, sNodeID);

		ResultSet rs = null;
		try {
			rs = pstmt.executeQuery();
		} catch (Exception e){
			e.printStackTrace();
		}

		Link link = null;
		if (rs != null) {
			while (rs.next()) {
				String sLinkID  = rs.getString(1);
				links.addElement(sLinkID);
			}
		}
		pstmt.close();
		return links;
	}

	/**
	 *  Retrieves the links with the given fromid/toid from the database and returns it.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param sNodeID, the id of the FromNode or ToNode whose Links to return.
	 *	@return Vector, a list of all Link objects for the given NodeID (FromNode / ToNode fields).
	 */
	public static Vector getAllNodes(DBConnection dbcon, String sNodeID) throws SQLException {

		Vector links = new Vector(51);

		Connection con = dbcon.getConnection();
		if (con == null)
			return links;

		PreparedStatement pstmt = con.prepareStatement(GET_ALLNODE_QUERY);
		pstmt.setString(1, sNodeID);
		pstmt.setString(2, sNodeID);

		ResultSet rs = null;
		try {
			rs = pstmt.executeQuery();
		} catch (Exception e){
			e.printStackTrace();
		}

		Link link = null;
		if (rs != null) {
			while (rs.next()) {
				link = processLink(dbcon, rs);
				if (link != null) links.addElement(link);
			}
		}
		pstmt.close();
		return links;
	}

	/**
	 *  Retrieves a link with the given id if it is active from the database and returns it.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param id, the id of the Link to return.
	 *	@return com.compendium.core.datamodel.Link, the Link object if the record was found, else null.
	 */
	public static Link getLink(DBConnection dbcon, String id) throws SQLException {
		Connection con = dbcon.getConnection();
		if (con == null)
			return null;

		PreparedStatement pstmt = con.prepareStatement(GET_LINK_QUERY);
		pstmt.setString(1, id);
		ResultSet rs = null;
		try {
			rs = pstmt.executeQuery();
		} catch (Exception e){
			e.printStackTrace();
		}

		Link link = null;
		if (rs != null) {
			while (rs.next()) {
				link = processLink(dbcon, rs);
			}
		}
		pstmt.close();
		return link;
	}

	/**
	 *  Retrieves the link with the given id from the database, whatever its status, and returns it.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param id, the id of the Link to return.
	 *	@return com.compendium.core.datamodel.Link, the Link object if the record was found, else null.
	 */
	public static Link getAnyLink(DBConnection dbcon, String id) throws SQLException {
		Connection con = dbcon.getConnection();
		if (con == null)
			return null;

		PreparedStatement pstmt = con.prepareStatement(GET_ANYLINK_QUERY);
		pstmt.setString(1, id);
		ResultSet rs = null;
		try {
			rs = pstmt.executeQuery();
		} catch (Exception e){
			e.printStackTrace();
		}

		Link link = null;
		if (rs != null) {
			while (rs.next()) {
				link = processLink(dbcon, rs);
			}
		}
		pstmt.close();
		return link;
	}

	/**
	 * 	Retrieves the link with the given original id from the database and returns it.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param sOriginalID, the original id of the Link to return.
	 *	@return com.compendium.core.datamodel.Link, the Link object if the record was found, else null.
	 */
	public static Link getImportedLink(DBConnection dbcon, String sOriginalID) throws SQLException {

		Connection con = dbcon.getConnection();
		if (con == null)
			return null;

		PreparedStatement pstmt = con.prepareStatement(GET_IMPORTED_LINK_QUERY);
		pstmt.setString(1, sOriginalID);
		ResultSet rs = null;
		try {
			rs = pstmt.executeQuery();
		} catch (Exception e){
			e.printStackTrace();
		}

		Link link = null;
		if (rs != null) {
			while (rs.next()) {
				link = processLink(dbcon, rs);
			}
		}
		pstmt.close();
		return link;
	}

	/**
	 * 	Helper method to extract and build a link object from a result set item.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *  @param rs, the ResultsSet to process to create the link from a query.
	 *	@return com.compendium.core.datamodel.Link, the Link object created from the passed data.
	 */
	private static Link processLink(DBConnection dbcon, ResultSet rs) throws SQLException {

		Link link = null;

		String		lId			= rs.getString(1);
		Date		oCDate		= new Date(new Double(rs.getLong(2)).longValue());
		Date		oMDate		= new Date(new Double(rs.getLong(3)).longValue());
		String		sAuthor		= rs.getString(4);
		String		sType		= rs.getString(5);
		String		sOriginalID	= rs.getString(6);
		String		sFrom		= rs.getString(7);
		String		sTo			= rs.getString(8);
		String		sLabel		= rs.getString(9);

		if (!NodeSummary.bIsInCache(sFrom)) {
			System.out.println("Warning: Link (ID "+lId+") referencing missing \"FROM\" node (ID "+sFrom+") ignored.\n");
			return link;
		}
		if (!NodeSummary.bIsInCache(sTo)) {
			System.out.println("Warning: Link (ID "+lId+") referencing missing \"TO\" node (ID "+sTo+") ignored.\n");
			return link;
		}
		NodeSummary		oFrom	= NodeSummary.getNodeSummary(sFrom);
		NodeSummary		oTo		= NodeSummary.getNodeSummary(sTo);
		//View oView = View.getView(sView) ;

		link = Link.getLink(lId, oCDate, oMDate, sAuthor, sType, sOriginalID, oFrom, oTo, sLabel);

		return link;
	}

	/**
	 *  Return if the given link has been marked for deletion.
	 *
	 *	@param dbcon, the DBConnection object to use.
	 *	@param sLinkID, the id if the link to check the status for.
	 *	@return boolean, whether node been marked for deletion.
	 *	@exception java.sql.SQLException.
	 */
	public static boolean isMarkedForDeletion(DBConnection dbcon, String sLinkID) throws SQLException {

		Connection con = dbcon.getConnection();
		if (con == null)
			return false;

		PreparedStatement pstmt = con.prepareStatement(GET_DELETESTATUS_QUERY);
		pstmt.setString(1, sLinkID);

		ResultSet rs = null;
		try {
			rs = pstmt.executeQuery();
		} catch (Exception e){
			e.printStackTrace();
		}
		if (rs != null) {
			if (rs.next()) {
				int status = rs.getInt(1);
				if (status == ICoreConstants.STATUS_DELETE)
					return true;
				else
					return false;
			}
		}
		pstmt.close();
		return false;
	}
}
