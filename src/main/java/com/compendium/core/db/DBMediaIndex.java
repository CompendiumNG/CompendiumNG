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

import java.awt.*;
import java.util.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;

import com.compendium.core.datamodel.*;
import com.compendium.core.db.management.*;
import com.compendium.core.ICoreConstants;

/**
 * The DBMediaIndex class serves as the interface layer between the MediaIndex objects
 * and the MediaIndex table in the database.
 *
 * @author	Michelle Bachler
 */
public class DBMediaIndex {

// AUDITED

	/** SQL statement to insert a new MediaIndex Record into the MediaIndex table.*/
	public final static String INSERT_MEDIAINDEX_QUERY =
		"INSERT INTO MediaIndex (ViewID, NodeID, MeetingID, MediaIndex, CreationDate, ModificationDate) "+ //$NON-NLS-1$
		"VALUES (?, ?, ?, ?, ?, ?) "; //$NON-NLS-1$

	/** SQL statement to update the MediaIndex column for the MediaIndex record with the given ViewID and NodeID.*/
	public final static String SET_MEDIAINDEX_QUERY =
		"UPDATE MediaIndex " + //$NON-NLS-1$
		"SET MediaIndex = ?, ModificationDate = ? " + //$NON-NLS-1$
		"WHERE ViewID = ? AND NodeID = ? AND MeetingID = ?"; //$NON-NLS-1$

// UNAUDITED

	/** SQL statement to return the MediaIndex records with the given ViewID, NodeID.*/
	public final static String GET_MEDIAINDEXES_QUERY =
		"SELECT * " + //$NON-NLS-1$
		"FROM MediaIndex "+ //$NON-NLS-1$
		"WHERE ViewID = ? AND NodeID = ?"; //$NON-NLS-1$

	/** SQL statement to return the MediaIndex records with the given NodeID.*/
	public final static String GET_ALL_MEDIAINDEXES_QUERY =
		"SELECT * " + //$NON-NLS-1$
		"FROM MediaIndex "+ //$NON-NLS-1$
		"WHERE NodeID = ?"; //$NON-NLS-1$

	/** SQL statement to return the MediaIndex record with the given ViewID, NodeID and MeetingID.*/
	public final static String GET_MEDIAINDEX_QUERY =
		"SELECT * " + //$NON-NLS-1$
		"FROM MediaIndex "+ //$NON-NLS-1$
		"WHERE ViewID = ? AND NodeID = ? AND MeetingID = ?"; //$NON-NLS-1$


	/**
	 *  Inserts a new MediaIndex record in the database and returns a MediaIndex object representing this record.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param MediaIndex, the MediaIndex Object to insert.
	 *	@return boolean, true if it was successful, else false.
	 *	@throws java.sql.SQLException
	 */
	public static boolean insert(DBConnection dbcon, MediaIndex oMediaIndex) throws SQLException  {

		Connection con = dbcon.getConnection();
		if (con == null)
			return false;

		// CHECK IF THIS RECORD ALREADY EXISTS FIRST
		MediaIndex ind = getMediaIndex(dbcon, oMediaIndex.getViewID(), oMediaIndex.getNodeID(), oMediaIndex.getMeetingID());

		if (ind != null) {
			if (DBNode.getImporting() && DBNode.getUpdateTranscludedNodes()) {
				DBMediaIndex.setMediaIndex(dbcon, oMediaIndex);
			}
			return true;
		}

		PreparedStatement pstmt = con.prepareStatement(INSERT_MEDIAINDEX_QUERY);

		pstmt.setString(1, oMediaIndex.getViewID());
		pstmt.setString(2, oMediaIndex.getNodeID());
		pstmt.setString(3, oMediaIndex.getMeetingID());
		pstmt.setDouble(4, oMediaIndex.getMediaIndex().getTime());
		pstmt.setDouble(5, oMediaIndex.getCreationDate().getTime());
		pstmt.setDouble(6, oMediaIndex.getModificationDate().getTime());

		int nRowCount = pstmt.executeUpdate();

		pstmt.close();

		if (nRowCount > 0) {
			if (DBAudit.getAuditOn()) {
				DBAudit.auditMediaIndex(dbcon, DBAudit.ACTION_ADD, oMediaIndex);
			}
			return true;
		}

		return false;
	}

	/**
	 *	Sets the MediaIndex for the given Node, Meeting and View
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param MediaIndex, the MedaiIndex Object whoise data to update.
	 *	@return boolean, true if it was successful, else false.
	 *	@throws java.sql.SQLException
	 */
	public static boolean setMediaIndex(DBConnection dbcon, MediaIndex oMediaIndex)
			throws SQLException {

		Connection con = dbcon.getConnection();
		if (con == null)
			return false;

		// IF AUDITING, STORE DATA
		MediaIndex data = null;
		if (DBAudit.getAuditOn()) {
			data = getMediaIndex(dbcon, oMediaIndex.getViewID(), oMediaIndex.getNodeID(), oMediaIndex.getMeetingID());
		}

		PreparedStatement pstmt = con.prepareStatement(SET_MEDIAINDEX_QUERY);

		pstmt.setDouble(1, oMediaIndex.getMediaIndex().getTime());
		pstmt.setDouble(2, oMediaIndex.getModificationDate().getTime());
		pstmt.setString(3, oMediaIndex.getViewID());
		pstmt.setString(4, oMediaIndex.getNodeID());
		pstmt.setString(5, oMediaIndex.getMeetingID());

		int nRowCount = pstmt.executeUpdate();

		pstmt.close();
		if (nRowCount > 0) {
			if (DBAudit.getAuditOn() && data!= null) {
				DBAudit.auditMediaIndex(dbcon, DBAudit.ACTION_EDIT, data);
			}

			return true;
		}
		return false;
	}


// GETTERS
	/**
	 *	Returns a list of all the MediaIndexes data for given node reference in the given view.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param sViewID, the id of the view the node is in.
	 *	@param sNodeID, the id of the node to return the position for.
	 *	@return a list of the MediaIndexes for this node, in this view.
	 *	@throws java.sql.SQLException
	 */
	public static Vector getMediaIndexes(DBConnection dbcon, String sViewID, String sNodeID) throws SQLException {

		Connection con = dbcon.getConnection();
		if (con == null)
			return null;

		PreparedStatement pstmt = con.prepareStatement(GET_MEDIAINDEXES_QUERY);

		pstmt.setString(1, sViewID);
		pstmt.setString(2, sNodeID);

		ResultSet rs = pstmt.executeQuery();

		Vector data = new Vector(10);
		MediaIndex oMediaIndex = null;

		if (rs != null) {
			while (rs.next()) {
				String	sViewId		= rs.getString(1);
				String	sNodeId		= rs.getString(2);
				String  sMeetingID	= rs.getString(3);
				Date	dMediaIndex	= new Date(new Double(rs.getLong(4)).longValue());
				Date	dCreated	= new Date(new Double(rs.getLong(5)).longValue());
				Date	dModified	= new Date(new Double(rs.getLong(6)).longValue());

				oMediaIndex = new MediaIndex(sViewId, sNodeId, sMeetingID, dMediaIndex, dCreated, dModified);
				data.addElement(oMediaIndex);
			}
		}

		pstmt.close();
		return data;
	}

	/**
	 *	Returns a list of all the MediaIndexes data for given node reference in the given view.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param sViewID, the id of the view the node is in.
	 *	@param sNodeID, the id of the node to return the position for.
	 *	@return a list of the MediaIndexes for this node, in this view.
	 *	@throws java.sql.SQLException
	 */
	public static Vector getAllMediaIndexes(DBConnection dbcon, String sNodeID) throws SQLException {

		Connection con = dbcon.getConnection();
		if (con == null)
			return null;

		PreparedStatement pstmt = con.prepareStatement(GET_ALL_MEDIAINDEXES_QUERY);

		pstmt.setString(1, sNodeID);

		ResultSet rs = pstmt.executeQuery();

		Vector data = new Vector(10);
		MediaIndex oMediaIndex = null;

		if (rs != null) {
			while (rs.next()) {
				String	sViewId		= rs.getString(1);
				String	sNodeId		= rs.getString(2);
				String  sMeetingID	= rs.getString(3);
				Date	dMediaIndex	= new Date(new Double(rs.getLong(4)).longValue());
				Date	dCreated	= new Date(new Double(rs.getLong(5)).longValue());
				Date	dModified	= new Date(new Double(rs.getLong(6)).longValue());

				oMediaIndex = new MediaIndex(sViewId, sNodeId, sMeetingID, dMediaIndex, dCreated, dModified);
				data.addElement(oMediaIndex);
			}
		}

		pstmt.close();
		return data;
	}

	/**
	 *	Returns the MediaIndex for given node reference in the given view and for the given meeting.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param sViewID, the id of the view the node is in.
	 *	@param sNodeID, the id of the node to return the position for.
	 *	@param sMeetingID, the id of the meeting for the medai index to return.
	 *	@return com.compendium.core.datamodel.MediaIndex, the MediaIndex of the node in the view, in the meeting.
	 *	@throws java.sql.SQLException
	 */
	public static MediaIndex getMediaIndex(DBConnection dbcon, String sViewID, String sNodeID, String sMeetingID) throws SQLException {

		Connection con = dbcon.getConnection();
		if (con == null)
			return null;

		PreparedStatement pstmt = con.prepareStatement(GET_MEDIAINDEX_QUERY);

		pstmt.setString(1, sViewID);
		pstmt.setString(2, sNodeID);
		pstmt.setString(3 , sMeetingID);

		ResultSet rs = pstmt.executeQuery();

		MediaIndex oMediaIndex = null;

		if (rs != null) {
			while (rs.next()) {
				Date	dMediaIndex	= new Date(new Double(rs.getLong(4)).longValue());
				Date	dCreated	= new Date(new Double(rs.getLong(5)).longValue());
				Date	dModified	= new Date(new Double(rs.getLong(6)).longValue());

				oMediaIndex = new MediaIndex(sViewID, sNodeID, sMeetingID, dMediaIndex, dCreated, dModified);
			}
		}

		pstmt.close();
		return oMediaIndex;
	}
}
