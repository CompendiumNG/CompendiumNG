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


package com.compendium.core.db;

import java.util.*;
import java.sql.*;

import com.compendium.core.datamodel.*;
import com.compendium.core.db.management.*;
import com.compendium.core.ICoreConstants;

/**
 * The DBViewLink class serves as the interface layer to the ViewLink table in
 * the database for the relationship between Views and Links in the datamodel.
 *
 * @author	Rema and Sajid / Michelle Bachler
 */
public class DBViewLink {

	// AUDITED
	/** SQL statement to insert a new View/Link relationship into the ViewLink table.*/
	public final static String INSERT_VIEWLINK_QUERY =
		"INSERT INTO ViewLink (ViewID, LinkID, CreationDate, ModificationDate, CurrentStatus) "+
		"VALUES (?, ?, ?, ?, ?) ";

/*
	public final static String DELETE_VIEWLINK_QUERY =
		"DELETE "+
		"FROM ViewLink "+
		" WHERE ViewID = ?";

	public final static String DELETE_VIEW_QUERY =
		"DELETE "+
		"FROM ViewLink "+
		"WHERE ViewID = ?";
*/

	/** SQL statement to set the status as deleted on a record in the ViewLink table for the given ViewID and LinkID.*/
	public final static String DELETE_VIEWLINK_QUERY =
		"UPDATE ViewLink "+
		"SET CurrentStatus = "+ICoreConstants.STATUS_DELETE+
		" WHERE ViewID = ? AND LinkID = ?";

	/** SQL statement to set the status as deleted on records in the ViewLink table for the given ViewID.*/
	public final static String DELETE_VIEW_QUERY =
		"UPDATE ViewLink "+
		"SET CurrentStatus = "+ICoreConstants.STATUS_DELETE+
		" WHERE ViewID = ?";

	/** SQL statement to set the status as active on a record in the ViewLink table for the given ViewID and LinkID.*/
	public final static String RESTORE_VIEWLINK_QUERY =
		"UPDATE ViewLink "+
		"SET CurrentStatus = "+ICoreConstants.STATUS_ACTIVE+
		" WHERE ViewID = ? AND LinkID = ?";

	/** SQL statement to set the status as active on records in the ViewLink table for the given ViewID.*/
	public final static String RESTORE_VIEW_QUERY =
		"UPDATE ViewLink "+
		"SET CurrentStatus = "+ICoreConstants.STATUS_ACTIVE+
		" WHERE ViewID = ?";

	/** SQL statement to set the purge the records in the ViewLink table for the given ViewID and LinkID.*/
	public final static String PURGE_VIEWLINK_QUERY =
		"DELETE "+
		"FROM ViewLink "+
		"WHERE ViewID = ? AND LinkID = ?";

	// UNAUDITED
	/** SQL statement to return the record for the given ViewID and LinkID if the Status is active.*/
	public final static String GET_VIEWLINK_QUERY =
		"SELECT ViewID, LinkID " +
		"FROM ViewLink "+
		"WHERE ViewID = ? AND LinkID = ? "+
		"AND CurrentStatus = "+ICoreConstants.STATUS_ACTIVE;

	// SQL statement to return all the records for a given ViewID, if the record status is active.*/
	public final static String GET_VIEWLINKS_QUERY =
		"SELECT ViewID, LinkID " +
		"FROM ViewLink " +
		"WHERE ViewID = ? "+
		"AND CurrentStatus = "+ICoreConstants.STATUS_ACTIVE;

	// SQL statement to return all the records for a given LinkID, if the record status is active.*/
	public final static String GET_VIEWS_QUERY =
		"SELECT ViewID, LinkID " +
		"FROM ViewLink " +
		"WHERE LinkID = ? "+
		"AND CurrentStatus = "+ICoreConstants.STATUS_ACTIVE;

	/** SQL statement to return a count of the ViewIDs for the given LinkID, if active.*/
	public final static String GET_ACTIVEVIEWSCOUNT_QUERY =
		"SELECT ViewID " +
		"FROM ViewLink " +
		"WHERE LinkID = ? "+
		"AND CurrentStatus = "+ICoreConstants.STATUS_ACTIVE;


	/**
	 *  Inserts a new viewlink record in the database.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param sViewID, the id of the view the link is in.
	 *	@param sLinkID, the id of the link in the given view.
	 *	@return boolean, true if it was successful, else false.
	 *	@throws java.sql.SQLException
	 */
	public static boolean insert(DBConnection dbcon, String sViewID, String sLinkID) throws SQLException {

		Connection con = dbcon.getConnection();
		if (con == null)
			return false;

		// CHECK IF THE LINK IS IN THE VIEW, AND ACTIVE.
		// IF IT IS, THEN WE WANT TO RETURN TRUE HERE.
		if ( (getLink(dbcon, sViewID, sLinkID)) != null) {
			return true;
		}

		// CHECK IF IT IS THERE BUT MARKED FOR DELETION
		// IF IT IS, THEN WE WANT TO RETURN TRUE HERE.
		if (restore(dbcon, sViewID, sLinkID)) {
			return true;
		}

		java.util.Date dDate  = new java.util.Date();

		PreparedStatement pstmt = con.prepareStatement(INSERT_VIEWLINK_QUERY);
		pstmt.setString(1, sViewID);
		pstmt.setString(2, sLinkID);
		pstmt.setDouble(3, new Long(dDate.getTime()).doubleValue());
		pstmt.setDouble(4, new Long(dDate.getTime()).doubleValue());
		pstmt.setInt(5, ICoreConstants.STATUS_ACTIVE);

		int nRowCount = pstmt.executeUpdate();
		pstmt.close();

		if (nRowCount > 0) {
			if (DBAudit.getAuditOn())
				DBAudit.auditViewLink(dbcon, DBAudit.ACTION_ADD, sViewID, sLinkID);

			return true;
		}
		return false;
	}

	/**
	 *  Inserts a new viewlink record in the database.
	 *  Used specifically by the DBUpdateDatabase class to restore links it has deleted.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param sViewID, the id of the view the link is in.
	 *	@param sLinkID, the id of the link in the given view.
	 *	@param dCreationDate, the creation date for this ViewLink record.
	 *	@param dModificationDate, the modificationDate for this ViewLink record.
	 *	@param nStatus, the status of this ViewLink.
	 *	@return boolean, true if it was successful, else false.
	 *	@throws java.sql.SQLException
	 */
	public static boolean recreate(DBConnection dbcon, String sViewID, String sLinkID, java.util.Date dCreationDate, java.util.Date dModificationDate, int nCurrentStatus) throws SQLException {

		Connection con = dbcon.getConnection();
		if (con == null)
			return false;

		PreparedStatement pstmt = con.prepareStatement(INSERT_VIEWLINK_QUERY);
		pstmt.setString(1, sViewID);
		pstmt.setString(2, sLinkID);
		pstmt.setDouble(3, new Long(dCreationDate.getTime()).doubleValue());
		pstmt.setDouble(4, new Long(dModificationDate.getTime()).doubleValue());
		pstmt.setInt(5, nCurrentStatus);

		int nRowCount = pstmt.executeUpdate();
		pstmt.close();

		if (nRowCount > 0) {
			if (DBAudit.getAuditOn())
				DBAudit.auditViewLink(dbcon, DBAudit.ACTION_ADD, sViewID, sLinkID);

			return true;
		}
		return false;
	}

	/**
	 *	Mark a ViewLink record Status as deleted, for the given ViewId and LinkID and return true if successful;
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param sViewID, the id of the view.
	 *	@param sLinkID, the id of the link.
	 *	@return boolean, true if it was successful, else false.
	 *	@throws java.sql.SQLException
	 */
	public static boolean delete(DBConnection dbcon, String sViewID, String sLinkID) throws SQLException {

		Connection con = dbcon.getConnection();
		if (con == null)
			return false;

		PreparedStatement pstmt = con.prepareStatement(DELETE_VIEWLINK_QUERY);

		pstmt.setString(1, sViewID);
		pstmt.setString(2, sLinkID);

		int nRowCount = pstmt.executeUpdate();
		pstmt.close();
		if (nRowCount > 0) {
			if (DBAudit.getAuditOn())
				DBAudit.auditViewLink(dbcon, DBAudit.ACTION_DELETE, sViewID, sLinkID);

			return true;
		}

		return false;
	}

	/**
	 *	Mark all ViewLink records for deletion for the given ViewID, and returns if successful.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param sViewID, the id of the view whose ViewLink records to mark for deletion.
	 *	@return boolean, true if it was successful, else false.
	 *	@throws java.sql.SQLException
	 */
	public static boolean deleteViewLinks(DBConnection dbcon, String sViewID) throws SQLException {

		Connection con = dbcon.getConnection();
		if (con == null)
			return false ;

		PreparedStatement pstmt = con.prepareStatement(DELETE_VIEW_QUERY);
		pstmt.setString(1, sViewID) ;

		int nRowCount = pstmt.executeUpdate();
		pstmt.close();
		if (nRowCount > 0) {
			if (DBAudit.getAuditOn()) {
				Vector data = DBViewLink.getLinkIDs(dbcon, sViewID);
				int count = data.size();
				for (int i=0; i< count; i++) {
					DBAudit.auditViewLink(dbcon, DBAudit.ACTION_DELETE, sViewID, (String)data.elementAt(i));
				}
			}

			return true;
		}
		return false;
	}

	/**
	 *	Mark a ViewLink record Status as active, for the given ViewId and LinkID and return true if successful;
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param sViewID, the id of the view.
	 *	@param sLinkID, the id of the link.
	 *	@return boolean, true if it was successful, else false.
	 *	@throws java.sql.SQLException
	 */
	public static boolean restore(DBConnection dbcon, String sViewID, String sLinkID) throws SQLException {

		Connection con = dbcon.getConnection() ;
		if (con == null)
			return false ;

		PreparedStatement pstmt = con.prepareStatement(RESTORE_VIEWLINK_QUERY);

		pstmt.setString(1, sViewID) ;
		pstmt.setString(2, sLinkID) ;

		int nRowCount = pstmt.executeUpdate();
		pstmt.close();
		if (nRowCount > 0) {
			if (DBAudit.getAuditOn())
				DBAudit.auditViewLink(dbcon, DBAudit.ACTION_RESTORE, sViewID, sLinkID);

			return true;
		}

		return false;
	}

	/**
	 *	Mark all ViewLink records as active for the given ViewID, and returns if successful.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param sViewID, the id of the view whose ViewLink records to mark as active.
	 *	@return boolean, true if it was successful, else false.
	 *	@throws java.sql.SQLException
	 */
	public static boolean restoreViewLinks(DBConnection dbcon, String sViewID) throws SQLException {

		Connection con = dbcon.getConnection() ;
		if (con == null)
			return false ;

		PreparedStatement pstmt = con.prepareStatement(RESTORE_VIEW_QUERY) ;
		pstmt.setString(1, sViewID) ;

		int nRowCount = pstmt.executeUpdate();
		pstmt.close();

		if (nRowCount > 0) {

			// RESTORE LINKS
			Vector data = DBViewLink.getLinkIDs(dbcon, sViewID);
			int count = data.size();
			for (int i=0; i< count; i++) {
				String sLinkID = (String)data.elementAt(i);
				DBLink.restore(dbcon, sLinkID);
			}

			if (DBAudit.getAuditOn()) {

				for (int j=0; j < count; j++) {
					DBAudit.auditViewLink(dbcon, DBAudit.ACTION_RESTORE, sViewID, (String)data.elementAt(j));
				}
			}

			return true;
		}
		return false;
	}

	/**
	 *	purge the given viewlink from the database and return true if successful
	 */
	public static boolean purge(DBConnection dbcon, String sViewID, String sLinkID) throws SQLException {

		Connection con = dbcon.getConnection() ;
		if (con == null)
			return false ;

		PreparedStatement pstmt = con.prepareStatement(PURGE_VIEWLINK_QUERY) ;

		pstmt.setString(1, sViewID) ;
		pstmt.setString(2, sLinkID) ;

		int nRowCount = pstmt.executeUpdate();
		pstmt.close();
		if (nRowCount > 0) {
			if (DBAudit.getAuditOn())
				DBAudit.auditViewLink(dbcon, DBAudit.ACTION_PURGE, sViewID, sLinkID);

			return true;
		}

		return false;
	}

// GETTERS

	/**
	 *	Returns the count Views this link is still active in.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param sLinkID, the id of the link to return the View count for.
	 *	@return int, a count of the Views the given link is in.
	 *	@throws java.sql.SQLException
	 */
	public static Vector getActiveViewCount(DBConnection dbcon, String sLinkID) throws SQLException {

		Vector views = new Vector();

		Connection con = dbcon.getConnection();
		if (con == null) {
			return views;
		}

		PreparedStatement pstmt = con.prepareStatement(GET_ACTIVEVIEWSCOUNT_QUERY);
		pstmt.setString(1, sLinkID);

		ResultSet rs = pstmt.executeQuery();
		if (rs != null) {
			while (rs.next()) {
				views.addElement(rs.getString(1));
			}
		}
		pstmt.close();
		return views;
	}

	/**
	 *	Returns the Link in the given view if it is there, else null.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param sViewID, the id of the view the link to return is in.
	 *	@param sLinkID, the id of the link.
	 *	@return com.compendium.core.datamodel.Link, the matching link else null.
	 *	@throws java.sql.SQLException
	 */
	public static Link getLink(DBConnection dbcon, String sViewID, String sLinkID) throws SQLException {

		Connection con = dbcon.getConnection();
		if (con == null)
			return null;

		PreparedStatement pstmt = con.prepareStatement(GET_VIEWLINK_QUERY);

		pstmt.setString(1, sViewID);
		pstmt.setString(2, sLinkID);

		ResultSet rs = pstmt.executeQuery();

		Link link = null;
		if (rs != null) {
			while (rs.next()) {
				//String	sViewId	= rs.getString(1);
				String	sLinkId	= rs.getString(2);

				link = DBLink.getLink(dbcon, sLinkId);
			}
		}

		pstmt.close();
		return link;
	}

	/**
	 *	Returns the array of View objects for the given LinkID.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param sLinkID, the id of the link whose Views to return.
	 *	@return Vector, of <code>View</code> objects that the link with the given id appears in.
	 *	@throws java.sql.SQLException
	 */
	public static Vector getViews(DBConnection dbcon, String sLinkID) throws SQLException {

		Connection con = dbcon.getConnection();
		if (con == null)
			return null;

		PreparedStatement pstmt = con.prepareStatement(GET_VIEWS_QUERY);
		pstmt.setString(1, sLinkID);

		ResultSet rs = pstmt.executeQuery();

		Vector vtViews = new Vector(51);
		if (rs != null) {
			while (rs.next()) {
				String	sViewId	= rs.getString(1);
				String	sLinkId = rs.getString(2);

				View view = View.getView(sViewId);
				vtViews.addElement(view);
			}
		}

		pstmt.close();
		return vtViews;
	}

	/**
	 *	Returns the array of Links objects in the given view.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param sViewID, the id of the view whose Links to return.
	 *	@return Enumeration, of <code>Link</code> objects that the View with the given id contains.
	 *	@throws java.sql.SQLException
	 */
	public static Vector getLinks(DBConnection dbcon, String sViewID) throws SQLException {

		Connection con = dbcon.getConnection();
		if (con == null)
			return null;

		PreparedStatement pstmt = con.prepareStatement(GET_VIEWLINKS_QUERY);

		pstmt.setString(1, sViewID);

		ResultSet rs = pstmt.executeQuery();

		Vector vtLinks = new Vector(51);
		if (rs != null) {
			while (rs.next()) {

				String	sViewId	= rs.getString(1);
				String	sLinkId	= rs.getString(2);

				//View view = View.getView(sViewId) ;
				Link link = DBLink.getLink(dbcon, sLinkId);

				vtLinks.addElement(link);
			}
		}
		pstmt.close();
		return vtLinks;
	}


	/**
	 *	Returns the array of Link ids for the given view
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param sViewID, the id of the view whose Link ids to return.
	 *	@return Vector, of link ids that the View with the given view id contains.
	 *	@throws java.sql.SQLException
	 */
	public static Vector getLinkIDs(DBConnection dbcon, String sViewID) throws SQLException {

		Connection con = dbcon.getConnection();
		if (con == null)
			return null;

		PreparedStatement pstmt = con.prepareStatement(GET_VIEWLINKS_QUERY);
		pstmt.setString(1, sViewID) ;
		ResultSet rs = pstmt.executeQuery();

		Vector vtLinks = new Vector(51);
		if (rs != null) {
			while (rs.next()) {
				String	sLinkID	= rs.getString(2) ;
				vtLinks.addElement(sLinkID);
			}
		}
		pstmt.close();
		return vtLinks;
	}
}
