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

import java.util.*;
import java.util.Date;
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
		"INSERT INTO ViewLink (ViewID, LinkID, CreationDate, ModificationDate, CurrentStatus, " +
		"LabelWrapWidth, ArrowType, "+
		"LinkStyle, LinkDashed, LinkWeight, LinkColour, FontSize, FontFace, FontStyle, "+ 
		"Foreground, Background) "+
		"VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ";

	/** SQL statement to update the node formatting for just the given node to the given formatting.*/
	public final static String UPDATE_FORMATTING_QUERY =
		"UPDATE ViewLink "+
		"SET ModificationDate=?, LabelWrapWidth=?, ArrowType=?, LinkStyle=?, "+
		"LinkDashed=?, LinkWeight=?, LinkColour=?, "+
		"FontSize=?, FontFace=?, FontStyle=?, Foreground=?, Background=? " +
		"WHERE ViewID=? AND LinkID=? AND CurrentStatus="+ICoreConstants.STATUS_ACTIVE;
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
		"SELECT ViewID, LinkID, CreationDate, ModificationDate, " +
		"LabelWrapWidth, ArrowType, "+
		"LinkStyle, LinkDashed, LinkWeight, LinkColour, FontSize, FontFace, FontStyle, "+ 
		"Foreground, Background " +
		"FROM ViewLink "+
		"WHERE ViewID = ? AND LinkID = ? "+
		"AND CurrentStatus = "+ICoreConstants.STATUS_ACTIVE;

	// SQL statement to return all the records for a given ViewID, if the record status is active.*/
	public final static String GET_VIEWLINKS_QUERY =
		"SELECT ViewID, LinkID, CreationDate, ModificationDate, " +
		"LabelWrapWidth, ArrowType, "+
		"LinkStyle, LinkDashed, LinkWeight, LinkColour, FontSize, FontFace, FontStyle, "+ 
		"Foreground, Background " +
		"FROM ViewLink " +
		"WHERE ViewID = ? "+
		"AND CurrentStatus = "+ICoreConstants.STATUS_ACTIVE;
	
	// SQL statement to return all the records for a given ViewID & NodeID, if the record status is active.*/
	public final static String GET_VIEWLINKSFORNODE_QUERY =
		"SELECT ViewLink.ViewID, ViewLink.LinkID, ViewLink.CreationDate, ViewLink.ModificationDate, " +
		"ViewLink.LabelWrapWidth, ViewLink.ArrowType, "+
		"ViewLink.LinkStyle, ViewLink.LinkDashed, ViewLink.LinkWeight, ViewLink.LinkColour, ViewLink.FontSize, " +
		"ViewLink.FontFace, ViewLink.FontStyle, "+ 
		"ViewLink.Foreground, ViewLink.Background " +
		"FROM ViewLink, ViewNode " +
		"WHERE ViewLink.ViewID = ViewNode.ViewID " +
		"AND ViewLink.ViewID = ? " +
		"AND ViewNode.NodeID = ? " +
		"AND ViewLink.CurrentStatus = "+ICoreConstants.STATUS_ACTIVE;

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
	 *	@param sViewID the id of the view the link is in.
	 *	@param sLinkID the id of the link in the given view.
	 *  @param nLabelWrapWidth The wrap width for the link label
	 *  @param nArrowType The arrow head type to use
	 *  @param nLinkStyle The style of the link, straight, square, curved
	 *  @param LinkDashed The style of the line fill, plain, dashed etc.
	 *  @param LinkWeight The thickness of the line
	 *  @param LinkColour The colour of the line
	 *  @param nFontSize The font size for the link label
	 *  @param sFontFace The font face for the link label
	 *  @param nFontStyle The font style for the link label
	 *  @param nForeground The foreground colour for the link label
	 *  @param nBackground The background colour for the link label	 
	 *	@return boolean, true if it was successful, else false.
	 *	@throws java.sql.SQLException
	 */
	public static ILinkProperties insert(DBConnection dbcon, String sViewID, String sLinkID,
			int nLabelWrapWidth, int nArrowType, int nLinkStyle, 
			int nLinkDashed, int nLinkWeight, int nLinkColour,
			int nFontSize, String sFontFace, int nFontStyle, 
			int nForeground, int nBackground) throws SQLException {

		LinkProperties props = null;

		Connection con = dbcon.getConnection();
		if (con == null)
			return props;

		// CHECK IF THE LINK IS IN THE VIEW, AND ACTIVE.
		// IF IT IS, THEN WE WANT TO RETURN TRUE HERE.
		props = DBViewLink.getLink(dbcon, sViewID, sLinkID);
		if ( props != null) {
			return props;
		}

		// CHECK IF IT IS THERE BUT MARKED FOR DELETION
		// IF IT IS, THEN WE WANT TO RESTORE.
		if (restore(dbcon, sViewID, sLinkID)) {
			props = DBViewLink.getLink(dbcon, sViewID, sLinkID);
			return props;
		}

		java.util.Date dDate  = new java.util.Date();

		PreparedStatement pstmt = con.prepareStatement(INSERT_VIEWLINK_QUERY);
		pstmt.setString(1, sViewID);
		pstmt.setString(2, sLinkID);
		pstmt.setDouble(3, new Long(dDate.getTime()).doubleValue());
		pstmt.setDouble(4, new Long(dDate.getTime()).doubleValue());
		pstmt.setInt(5, ICoreConstants.STATUS_ACTIVE);
		pstmt.setInt(6, nLabelWrapWidth);
		pstmt.setInt(7, nArrowType);
		pstmt.setInt(8, nLinkStyle);
		pstmt.setInt(9, nLinkDashed);
		pstmt.setInt(10, nLinkWeight);
		pstmt.setInt(11, nLinkColour);
		pstmt.setInt(12, nFontSize);		
		pstmt.setString(13, sFontFace);
		pstmt.setInt(14, nFontStyle);
		pstmt.setInt(15, nForeground);
		pstmt.setInt(16, nBackground);

		int nRowCount = pstmt.executeUpdate();
		pstmt.close();
		if (nRowCount > 0) {
			props = DBViewLink.getLink(dbcon, sViewID, sLinkID);
			if (DBAudit.getAuditOn())
				DBAudit.auditViewLink(dbcon, DBAudit.ACTION_ADD, props);
		}
		return props;
	}

	/**
	 *  Update the viewlink record with the given viewID and LinkID in the database.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param sViewID the id of the view the link is in.
	 *	@param sLinkID the id of the link in the given view.
	 *  @param nLabelWrapWidth The wrap width for the link label
	 *  @param nArrowType The arrow head type to use
	 *  @param nLinkStyle The style of the link, straight, square, curved
	 *  @param LinkDashed The style of the line fill, plain, dashed etc.
	 *  @param LinkWeight The thickness of the line
	 *  @param LinkColour The colour of the line
	 *  @param nFontSize The font size for the link label
	 *  @param sFontFace The font face for the link label
	 *  @param nFontStyle The font style for the link label
	 *  @param nForeground The foreground colour for the link label
	 *  @param nBackground The background colour for the link label	 
	 *	@return boolean, true if it was successful, else false.
	 *	@throws java.sql.SQLException
	 */
	public static ILinkProperties updateFormatting(DBConnection dbcon, String sViewID, String sLinkID,
			int nLabelWrapWidth, int nArrowType, int nLinkStyle, 
			int nLinkDashed, int nLinkWeight, int nLinkColour,
			int nFontSize, String sFontFace, int nFontStyle, 
			int nForeground, int nBackground) throws SQLException {

		LinkProperties props = null;

		Connection con = dbcon.getConnection();
		if (con == null)
			return props;

		java.util.Date dDate  = new java.util.Date();

		PreparedStatement pstmt = con.prepareStatement(UPDATE_FORMATTING_QUERY);
		pstmt.setDouble(1, new Long(dDate.getTime()).doubleValue());
		pstmt.setInt(2, nLabelWrapWidth);
		pstmt.setInt(3, nArrowType);
		pstmt.setInt(4, nLinkStyle);
		pstmt.setInt(5, nLinkDashed);
		pstmt.setInt(6, nLinkWeight);
		pstmt.setInt(7, nLinkColour);
		pstmt.setInt(8, nFontSize);		
		pstmt.setString(9, sFontFace);
		pstmt.setInt(10, nFontStyle);
		pstmt.setInt(11, nForeground);
		pstmt.setInt(12, nBackground);
		
		pstmt.setString(13, sViewID);
		pstmt.setString(14, sLinkID);

		int nRowCount = pstmt.executeUpdate();
		pstmt.close();
		if (nRowCount > 0) {
			props = DBViewLink.getLink(dbcon, sViewID, sLinkID);
			if (DBAudit.getAuditOn())
				DBAudit.auditViewLink(dbcon, DBAudit.ACTION_EDIT, props);
		}
		return props;
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
	 *  @param nLabelWrapWidth The wrap width for the link label
	 *  @param nArrowType The arrow head type to use
	 *  @param nLinkStyle The style of the link, straight, square, curved
	 *  @param LinkDashed The style of the line fill, plain, dashed etc.
	 *  @param LinkWeight The thickness of the line
	 *  @param LinkColour The colour of the line
	 *  @param nFontSize The font size for the link label
	 *  @param sFontFace The font face for the link label
	 *  @param nFontStyle The font style for the link label
	 *  @param nForeground The foreground colour for the link label
	 *  @param nBackground The background colour for the link label	 
	 *	@return boolean, true if it was successful, else false.
	 *	@throws java.sql.SQLException
	 */
	public static ILinkProperties recreate(DBConnection dbcon, String sViewID, String sLinkID, 
			java.util.Date dCreationDate, java.util.Date dModificationDate, int nCurrentStatus,
			int nLabelWrapWidth, int nArrowType, int nLinkStyle, 
			int nLinkDashed, int nLinkWeight, int nLinkColour,
			int nFontSize, String sFontFace, int nFontStyle, 
			int nForeground, int nBackground) throws SQLException {

		LinkProperties props = null;
		Connection con = dbcon.getConnection();
		if (con == null)
			return props;

		PreparedStatement pstmt = con.prepareStatement(INSERT_VIEWLINK_QUERY);
		pstmt.setString(1, sViewID);
		pstmt.setString(2, sLinkID);
		pstmt.setDouble(3, new Long(dCreationDate.getTime()).doubleValue());
		pstmt.setDouble(4, new Long(dModificationDate.getTime()).doubleValue());
		pstmt.setInt(5, nCurrentStatus);
		pstmt.setInt(6, nLabelWrapWidth);
		pstmt.setInt(7, nArrowType);
		pstmt.setInt(8, nLinkStyle);
		pstmt.setInt(9, nLinkDashed);
		pstmt.setInt(10, nLinkWeight);
		pstmt.setInt(11, nLinkColour);
		pstmt.setInt(12, nFontSize);		
		pstmt.setString(13, sFontFace);
		pstmt.setInt(14, nFontStyle);
		pstmt.setInt(15, nForeground);
		pstmt.setInt(16, nBackground);

		int nRowCount = pstmt.executeUpdate();
		pstmt.close();
		if (nRowCount > 0) {
			props = DBViewLink.getLink(dbcon, sViewID, sLinkID);
			if (DBAudit.getAuditOn()) {
				DBAudit.auditViewLink(dbcon, DBAudit.ACTION_ADD, props);
			}
		}
		return props;
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

		// IF AUDITING, STORE DATA
		LinkProperties props = null;
		if (DBAudit.getAuditOn()) {
			props = DBViewLink.getLink(dbcon, sViewID, sLinkID);
		}

		PreparedStatement pstmt = con.prepareStatement(DELETE_VIEWLINK_QUERY);

		pstmt.setString(1, sViewID);
		pstmt.setString(2, sLinkID);

		int nRowCount = 0;
		try {
			nRowCount = pstmt.executeUpdate();
		} catch (Exception e){
			e.printStackTrace();
		}
		pstmt.close();
		if (nRowCount > 0) {
			if (DBAudit.getAuditOn())
				DBAudit.auditViewLink(dbcon, DBAudit.ACTION_DELETE, props);

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

		// IF AUDITING, STORE DATA
		Vector data = null;
		if (DBAudit.getAuditOn()) {
			data = getLinks(dbcon, sViewID);
		}
		
		PreparedStatement pstmt = con.prepareStatement(DELETE_VIEW_QUERY);
		pstmt.setString(1, sViewID) ;

		int nRowCount = 0;
		try {
			nRowCount = pstmt.executeUpdate();
		} catch (Exception e){
			e.printStackTrace();
		}
		pstmt.close();
		if (nRowCount > 0) {
			if (DBAudit.getAuditOn()) {
				int count = data.size();
				for (int i=0; i< count; i++) {
					DBAudit.auditViewLink(dbcon, DBAudit.ACTION_DELETE, (LinkProperties)data.elementAt(i));
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

		// IF AUDITING, STORE DATA
		LinkProperties props = null;
		if (DBAudit.getAuditOn()) {
			props = DBViewLink.getLink(dbcon, sViewID, sLinkID);
		}

		PreparedStatement pstmt = con.prepareStatement(RESTORE_VIEWLINK_QUERY);

		pstmt.setString(1, sViewID) ;
		pstmt.setString(2, sLinkID) ;

		int nRowCount = 0;
		try {
			nRowCount = pstmt.executeUpdate();
		} catch (Exception e){
			e.printStackTrace();
		}
		pstmt.close();
		if (nRowCount > 0) {
			if (DBAudit.getAuditOn())
				DBAudit.auditViewLink(dbcon, DBAudit.ACTION_RESTORE, props);

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

		PreparedStatement pstmt = con.prepareStatement(RESTORE_VIEW_QUERY);
		pstmt.setString(1, sViewID) ;

		int nRowCount = 0;
		try {
			nRowCount = pstmt.executeUpdate();
		} catch (Exception e){
			e.printStackTrace();
		}
		pstmt.close();

		if (nRowCount > 0) {
			// RESTORE LINKS
			LinkProperties props = null;
			Vector data = getLinks(dbcon, sViewID);

			int count = data.size();
			for (int i=0; i< count; i++) {
				props = (LinkProperties)data.elementAt(i);
				DBLink.restore(dbcon, props.getLink().getId());
				if (DBAudit.getAuditOn()) {
					DBAudit.auditViewLink(dbcon, DBAudit.ACTION_RESTORE, props);
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

		// IF AUDITING, STORE DATA
		LinkProperties props = null;
		if (DBAudit.getAuditOn()) {
			props = DBViewLink.getLink(dbcon, sViewID, sLinkID);
		}

		PreparedStatement pstmt = con.prepareStatement(PURGE_VIEWLINK_QUERY) ;

		pstmt.setString(1, sViewID) ;
		pstmt.setString(2, sLinkID) ;

		int nRowCount = 0;
		try {
			nRowCount = pstmt.executeUpdate();
		} catch (Exception e){
			e.printStackTrace();
		}
		pstmt.close();
		if (nRowCount > 0) {
			if (DBAudit.getAuditOn())
				DBAudit.auditViewLink(dbcon, DBAudit.ACTION_PURGE, props);

			return true;
		}

		return false;
	}


	/**
	 * Set the arrow type for the given LinkProperpties objects
	 * @param dbcon the database connection
	 * @param sViewID the relevant view
	 * @param vtProperties the LinkProperties objects to set the arrow type for
	 * @param nArrowType the arrow type to set
	 * @return if the property was set.
	 * @throws SQLException
	 */
	public static boolean setArrowType(DBConnection dbcon, String sViewID, Vector vtProperties, int nArrowType) throws SQLException {

		Connection con = dbcon.getConnection();
		if (con == null)
			return false;
		
		String sLinkList = extractLinkIDs(vtProperties);
		
		Statement stmt = con.createStatement();
		String sQuery = "UPDATE ViewLink " +
		"SET ArrowType="+nArrowType+
		" WHERE ViewID='"+sViewID+
		"' AND ViewLink.CurrentStatus = "+ICoreConstants.STATUS_ACTIVE+
		" AND LinkID IN ("+sLinkList+")";

		int nRowCount = stmt.executeUpdate(sQuery);

		stmt.close();
		if (nRowCount > 0) {			
			if (DBAudit.getAuditOn()) {
				auditLinkPositions(dbcon, vtProperties, DBAudit.ACTION_EDIT);
			}
			return true;
		}
		return false;
	}	
	
	/**
	 * Set the link style width for the given LinkProperpties objects
	 * @param dbcon the database connection
	 * @param sViewID the relevant view
	 * @param vtProperties the LinkProperties objects to set the link style for
	 * @param nStyle the link style to set
	 * @return if the property was set.
	 * @throws SQLException
	 */
	public static boolean setLinkStyle(DBConnection dbcon, String sViewID, Vector vtProperties, int nStyle) throws SQLException {

		Connection con = dbcon.getConnection();
		if (con == null)
			return false;
		
		String sLinkList = extractLinkIDs(vtProperties);
		
		Statement stmt = con.createStatement();
		String sQuery = "UPDATE ViewLink " +
		"SET LinkStyle="+nStyle+
		" WHERE ViewID='"+sViewID+
		"' AND ViewLink.CurrentStatus = "+ICoreConstants.STATUS_ACTIVE+
		" AND LinkID IN ("+sLinkList+")";

		int nRowCount = stmt.executeUpdate(sQuery);

		stmt.close();
		if (nRowCount > 0) {			
			if (DBAudit.getAuditOn()) {
				auditLinkPositions(dbcon, vtProperties, DBAudit.ACTION_EDIT);
			}
			return true;
		}
		return false;
	}

	/**
	 * Set the link dashed (line style) for the given LinkProperpties objects
	 * @param dbcon the database connection
	 * @param sViewID the relevant view
	 * @param vtProperties the LinkProperties objects to set the wrap width for
	 * @param nDashed the link line style to set
	 * @return if the property was set.
	 * @throws SQLException
	 */
	public static boolean setLinkDashed(DBConnection dbcon, String sViewID, Vector vtProperties, int nDashed) throws SQLException {

		Connection con = dbcon.getConnection();
		if (con == null)
			return false;
		
		String sLinkList = extractLinkIDs(vtProperties);
		
		Statement stmt = con.createStatement();
		String sQuery = "UPDATE ViewLink " +
		"SET LinkDashed="+nDashed+
		" WHERE ViewID='"+sViewID+
		"' AND ViewLink.CurrentStatus = "+ICoreConstants.STATUS_ACTIVE+
		" AND LinkID IN ("+sLinkList+")";

		int nRowCount = stmt.executeUpdate(sQuery);

		stmt.close();
		if (nRowCount > 0) {			
			if (DBAudit.getAuditOn()) {
				auditLinkPositions(dbcon, vtProperties, DBAudit.ACTION_EDIT);
			}
			return true;
		}
		return false;
	}

	/**
	 * Set the link weight for the given LinkProperpties objects
	 * @param dbcon the database connection
	 * @param sViewID the relevant view
	 * @param vtProperties the LinkProperties objects to set the link weight for
	 * @param nWeight the link weight to set
	 * @return if the property was set.
	 * @throws SQLException
	 */
	public static boolean setLinkWeight(DBConnection dbcon, String sViewID, Vector vtProperties, int nWeight) throws SQLException {

		Connection con = dbcon.getConnection();
		if (con == null)
			return false;
		
		String sLinkList = extractLinkIDs(vtProperties);
		
		Statement stmt = con.createStatement();
		String sQuery = "UPDATE ViewLink " +
		"SET LinkWeight="+nWeight+
		" WHERE ViewID='"+sViewID+
		"' AND ViewLink.CurrentStatus = "+ICoreConstants.STATUS_ACTIVE+
		" AND LinkID IN ("+sLinkList+")";

		int nRowCount = stmt.executeUpdate(sQuery);

		stmt.close();
		if (nRowCount > 0) {			
			if (DBAudit.getAuditOn()) {
				auditLinkPositions(dbcon, vtProperties, DBAudit.ACTION_EDIT);
			}
			return true;
		}
		return false;
	}

	/**
	 * Set the link colour for the given LinkProperpties objects
	 * @param dbcon the database connection
	 * @param sViewID the relevant view
	 * @param vtProperties the LinkProperties objects to set the colour for
	 * @param nColour the link colour to set
	 * @return if the property was set.
	 * @throws SQLException
	 */
	public static boolean setLinkColour(DBConnection dbcon, String sViewID, Vector vtProperties, int nColour) throws SQLException {

		Connection con = dbcon.getConnection();
		if (con == null)
			return false;
		
		String sLinkList = extractLinkIDs(vtProperties);
		
		Statement stmt = con.createStatement();
		String sQuery = "UPDATE ViewLink " +
		"SET LinkColour="+nColour+
		" WHERE ViewID='"+sViewID+
		"' AND ViewLink.CurrentStatus = "+ICoreConstants.STATUS_ACTIVE+
		" AND LinkID IN ("+sLinkList+")";

		int nRowCount = stmt.executeUpdate(sQuery);

		stmt.close();
		if (nRowCount > 0) {			
			if (DBAudit.getAuditOn()) {
				auditLinkPositions(dbcon, vtProperties, DBAudit.ACTION_EDIT);
			}
			return true;
		}
		return false;
	}

	
	
	/**
	 * Set the font size for the given LinkProperpties objects
	 * @param dbcon the database connection
	 * @param sViewID the relevant view
	 * @param vtProperties the LinkProperties objects to set the font size for
	 * @param nFontSize the font size to set
	 * @return if the font size was set.
	 * @throws SQLException
	 */
	public static boolean setFontSize(DBConnection dbcon, String sViewID, Vector vtProperties, int nFontSize) throws SQLException {

		Connection con = dbcon.getConnection();
		if (con == null)
			return false;
		
		String sLinkList = extractLinkIDs(vtProperties);
		 
		Statement stmt = con.createStatement();
		String sQuery = "UPDATE ViewLink " +
		"SET FontSize="+nFontSize+
		" WHERE ViewID='"+sViewID+
		"' AND ViewLink.CurrentStatus = "+ICoreConstants.STATUS_ACTIVE+
		" AND LinkID IN ("+sLinkList+")";
		
		int nRowCount = stmt.executeUpdate(sQuery);
		stmt.close();
		if (nRowCount > 0) {			
			if (DBAudit.getAuditOn()) {
				auditLinkPositions(dbcon, vtProperties, DBAudit.ACTION_EDIT);
			}
			return true;
		}
		return false;
	}
	
	/**
	 * Set the font face for the given LinkProperpties objects
	 * @param dbcon the database connection
	 * @param sViewID the relevant view
	 * @param vtProperties the LinkProperties objects to set the font face for
	 * @param nFontFace the font face to set
	 * @return if the font face was set.
	 * @throws SQLException
	 */
	public static boolean setFontFace(DBConnection dbcon, String sViewID, Vector vtProperties, String nFontFace) throws SQLException {

		Connection con = dbcon.getConnection();
		if (con == null)
			return false;
		
		String sLinkList = extractLinkIDs(vtProperties);

		Statement stmt = con.createStatement();
		String sQuery = "UPDATE ViewLink " +
		"SET FontFace='"+nFontFace+
		"' WHERE ViewID='"+sViewID+
		"' AND ViewLink.CurrentStatus = "+ICoreConstants.STATUS_ACTIVE+
		" AND LinkID IN ("+sLinkList+")";
		
		int nRowCount = stmt.executeUpdate(sQuery);
		stmt.close();
		if (nRowCount > 0) {			
			if (DBAudit.getAuditOn()) {
				auditLinkPositions(dbcon, vtProperties, DBAudit.ACTION_EDIT);
			}
			return true;
		}
		return false;
	}	
	
	/**
	 * Set the font style for the given LinkProperpties objects
	 * @param dbcon the database connection
	 * @param sViewID the relevant view
	 * @param vtProperties the LinkProperties objects to set the font style for
	 * @param nFontStyle the font style to set
	 * @return if the font style was set.
	 * @throws SQLException
	 */
	public static boolean setFontStyle(DBConnection dbcon, String sViewID, Vector vtProperties, int nFontStyle) throws SQLException {

		Connection con = dbcon.getConnection();
		if (con == null)
			return false;
		
		String sLinkList = extractLinkIDs(vtProperties);

		Statement stmt = con.createStatement();
		String sQuery = "UPDATE ViewLink " +
		"SET FontStyle="+nFontStyle+
		" WHERE ViewID='"+sViewID+
		"' AND ViewLink.CurrentStatus = "+ICoreConstants.STATUS_ACTIVE+
		" AND LinkID IN ("+sLinkList+")";
		
		int nRowCount = stmt.executeUpdate(sQuery);
		stmt.close();
		if (nRowCount > 0) {			
			if (DBAudit.getAuditOn()) {
				auditLinkPositions(dbcon, vtProperties, DBAudit.ACTION_EDIT);
			}
			return true;
		}
		return false;
	}
	
	/**
	 * Set the wrap width for the given LinkProperpties objects
	 * @param dbcon the database connection
	 * @param sViewID the relevant view
	 * @param vtProperties the LinkProperties objects to set the wrap width for
	 * @param nWidth the wrap width to set
	 * @return if the property was set.
	 * @throws SQLException
	 */
	public static boolean setWrapWidth(DBConnection dbcon, String sViewID, Vector vtProperties, int nWidth) throws SQLException {

		Connection con = dbcon.getConnection();
		if (con == null)
			return false;
		
		String sLinkList = extractLinkIDs(vtProperties);
		
		Statement stmt = con.createStatement();
		String sQuery = "UPDATE ViewLink " +
		"SET LabelWrapWidth="+nWidth+
		" WHERE ViewID='"+sViewID+
		"' AND ViewLink.CurrentStatus = "+ICoreConstants.STATUS_ACTIVE+
		" AND LinkID IN ("+sLinkList+")";

		int nRowCount = stmt.executeUpdate(sQuery);

		stmt.close();
		if (nRowCount > 0) {			
			if (DBAudit.getAuditOn()) {
				auditLinkPositions(dbcon, vtProperties, DBAudit.ACTION_EDIT);
			}
			return true;
		}
		return false;
	}
	
	/**
	 * Set the text foreground colour width for the given LinkProperpties objects
	 * @param dbcon the database connection
	 * @param sViewID the relevant view
	 * @param vtProperties the LinkProperties objects to set the text foreground colour for
	 * @param nColor the colour to set the text foreground to
	 * @return if the property was set.
	 * @throws SQLException
	 */
	public static boolean setTextForeground(DBConnection dbcon, String sViewID, Vector vtProperties, int nColour) throws SQLException {

		Connection con = dbcon.getConnection();
		if (con == null)
			return false;
		
		String sLinkList = extractLinkIDs(vtProperties);
		
		Statement stmt = con.createStatement();
		String sQuery = "UPDATE ViewLink " +
		"SET Foreground="+nColour+
		" WHERE ViewID='"+sViewID+
		"' AND ViewLink.CurrentStatus = "+ICoreConstants.STATUS_ACTIVE+
		" AND LinkID IN ("+sLinkList+")";

		int nRowCount = stmt.executeUpdate(sQuery);

		stmt.close();
		if (nRowCount > 0) {			
			if (DBAudit.getAuditOn()) {
				auditLinkPositions(dbcon, vtProperties, DBAudit.ACTION_EDIT);
			}
			return true;
		}
		return false;
	}
	
	/**
	 * Set the text background colour width for the given LinkProperpties objects
	 * @param dbcon the database connection
	 * @param sViewID the relevant view
	 * @param vtProperties the LinkProperpties objects to set the text background colour for
	 * @param nColor the colour to set the text background to
	 * @return if the property was set.
	 * @throws SQLException
	 */
	public static boolean setTextBackground(DBConnection dbcon, String sViewID, Vector vtProperties, int nColour) throws SQLException {

		Connection con = dbcon.getConnection();
		if (con == null)
			return false;
		
		String sLinkList = extractLinkIDs(vtProperties);
		
		Statement stmt = con.createStatement();
		String sQuery = "UPDATE ViewLink " +
		"SET Background="+nColour+
		" WHERE ViewID='"+sViewID+
		"' AND ViewLink.CurrentStatus = "+ICoreConstants.STATUS_ACTIVE+
		" AND LinkID IN ("+sLinkList+")";

		int nRowCount = stmt.executeUpdate(sQuery);

		stmt.close();
		if (nRowCount > 0) {			
			if (DBAudit.getAuditOn()) {
				auditLinkPositions(dbcon, vtProperties, DBAudit.ACTION_EDIT);
			}
			return true;
		}
		return false;
	}	
	
	/**
	 * Process the given list of LinkProperties object for audit
	 * @param dbcon the database connection
	 * @param vtProperties the list of LinkProperties objects to process
	 * @param type the audit type (e.g. DBAudit.EDIT etc)
	 * @throws SQLException
	 */
	private static void auditLinkPositions(DBConnection dbcon, Vector vtProperties, int type) throws SQLException {
		int count = vtProperties.size();
		LinkProperties props = null;				
		for (int j=0; j<count; j++) {					
			props = (LinkProperties)vtProperties.elementAt(j);
			DBAudit.auditViewLink(dbcon, type, props);
		}		
	}
	
	/**
	 * Processes the list of LinkProperties and produce a comma separated list of the link ids.
	 * @param vtProperties the list of LinkProperties to process.
	 * @return the comma separates string of link ids.
	 */
	private static String extractLinkIDs(Vector vtProperties) {
		String sLinkList = "";		
		int count = vtProperties.size();
		LinkProperties props = null;
		for (int i=0; i<count; i++) {
			props = (LinkProperties)vtProperties.elementAt(i);
			if (i == (count-1)) {
				sLinkList += "'"+props.getLink().getId()+"'";
			} else {
				sLinkList += "'"+props.getLink().getId()+"',";
			}
		}
		return sLinkList;
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

		ResultSet rs = null;
		try {
			rs = pstmt.executeQuery();
		} catch (Exception e){
			e.printStackTrace();
		}
		if (rs != null) {
			while (rs.next()) {
				views.addElement(rs.getString(1));
			}
		}
		pstmt.close();
		return views;
	}

	/**
	 *	Returns the LinkProperties in the given view if Link is there, else null.
	 *
	 *	@param dbcon the DBConnection object to access the database with.
	 *	@param sViewID the id of the view the link to return is in.
	 *	@param sLinkID the id of the link.
	 *	@return the matching LinkProperties else null.
	 *	@throws java.sql.SQLException
	 */
	public static LinkProperties getLink(DBConnection dbcon, String sViewID, String sLinkID) throws SQLException {

		Connection con = dbcon.getConnection();
		if (con == null)
			return null;

		PreparedStatement pstmt = con.prepareStatement(GET_VIEWLINK_QUERY);

		pstmt.setString(1, sViewID);
		pstmt.setString(2, sLinkID);

		ResultSet rs = null;
		try {
			rs = pstmt.executeQuery();
		} catch (Exception e){
			e.printStackTrace();
		}

		LinkProperties linkProps = null;
		Link link = null;
		View view = null;
		if (rs != null) {
			while (rs.next()) {
				String	sViewId	= rs.getString(1);
				String	sLinkId	= rs.getString(2);
				Date	created		= new Date(new Double(rs.getLong(3)).longValue());
				Date	modified	= new Date(new Double(rs.getLong(4)).longValue());
				int	nLabelWrapWidth	= rs.getInt(5);
				int nArrowType		= rs.getInt(6);
				int nLinkStyle		= rs.getInt(7);
				int nLinkDashed		= rs.getInt(8);
				int nLinkWeight		= rs.getInt(9);
				int nLinkColour		= rs.getInt(10);
				int	nFontSize		= rs.getInt(11);
				String sFontFace	= rs.getString(12);
				int	nFontStyle		= rs.getInt(13);
				int	nForeground		= rs.getInt(14);
				int	nBackground		= rs.getInt(15);
				
				link = DBLink.getLink(dbcon, sLinkId);
				view = (View)NodeSummary.getNodeSummary(sViewID);
				linkProps = new LinkProperties(view, link, created, modified, nLabelWrapWidth, 
						nArrowType, nLinkStyle, nLinkDashed, nLinkWeight, nLinkColour, nFontSize, sFontFace,
						nFontStyle, nForeground, nBackground);
				
			}
		}

		pstmt.close();
		return linkProps;
	}

	/**
	 *	Returns the array of View objects for the given LinkID.
	 *
	 *	@param dbcon the DBConnection object to access the database with.
	 *	@param sLinkID the id of the link whose Views to return.
	 *	@return Vector of <code>View</code> objects that the link with the given id appears in.
	 *	@throws java.sql.SQLException
	 */
	public static Vector getViews(DBConnection dbcon, String sLinkID) throws SQLException {

		Connection con = dbcon.getConnection();
		if (con == null)
			return null;

		PreparedStatement pstmt = con.prepareStatement(GET_VIEWS_QUERY);
		pstmt.setString(1, sLinkID);

		ResultSet rs = null;
		try {
			rs = pstmt.executeQuery();
		} catch (Exception e){
			e.printStackTrace();
		}

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
	 *	Returns the array of LinkProperties objects in the given view.
	 *
	 *	@param dbcon the DBConnection object to access the database with.
	 *	@param sViewID the id of the view whose Links to return.
	 *	@return Vector of <code>LinkProperties</code> objects that the View with the given id contains.
	 *	@throws java.sql.SQLException
	 */
	public static Vector<LinkProperties> getLinks(DBConnection dbcon, String sViewID) throws SQLException {

		Connection con = dbcon.getConnection();
		if (con == null)
			return null;

		PreparedStatement pstmt = con.prepareStatement(GET_VIEWLINKS_QUERY);

		pstmt.setString(1, sViewID);

		ResultSet rs = null;
		
		try {
			rs = pstmt.executeQuery();
		} catch (Exception e){
			e.printStackTrace();
		}

		Vector<LinkProperties> vtLinks = new Vector<LinkProperties>(51);
		LinkProperties linkProps = null;
		Link link = null;
		View view = null;
		if (rs != null) {
			while (rs.next()) {
				//String	sViewId	= rs.getString(1);
				String	sLinkId	= rs.getString(2);
				Date	created		= new Date(new Double(rs.getLong(3)).longValue());
				Date	modified	= new Date(new Double(rs.getLong(4)).longValue());
				int	nLabelWrapWidth	= rs.getInt(5);
				int nArrowType		= rs.getInt(6);
				int nLinkStyle		= rs.getInt(7);
				int nLinkDashed		= rs.getInt(8);
				int nLinkWeight		= rs.getInt(9);
				int nLinkColour		= rs.getInt(10);
				int	nFontSize		= rs.getInt(11);
				String sFontFace	= rs.getString(12);
				int	nFontStyle		= rs.getInt(13);
				int	nForeground		= rs.getInt(14);
				int	nBackground		= rs.getInt(15);
				
				link = DBLink.getLink(dbcon, sLinkId);
				view = (View)NodeSummary.getNodeSummary(sViewID);
				linkProps = new LinkProperties(view, link, created, modified, nLabelWrapWidth, 
						nArrowType, nLinkStyle, nLinkDashed, nLinkWeight, nLinkColour, nFontSize, sFontFace,
						nFontStyle, nForeground, nBackground);

				vtLinks.addElement(linkProps);
			}
		}
		pstmt.close();
		return vtLinks;
	}
	
	/**
	 *	Returns the array of LinkProperties objects in the given view that involve the given node.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param sViewID, the id of the view whose Links to return.
	 *	@param sNodeID, the id of the node in sViewID whose links we're looking for
	 *	@return Vector of <code>LinkProperties</code> objects that the View with the given id contains.
	 *	@throws java.sql.SQLException
	 */
	public static Vector<LinkProperties> getLinks(DBConnection dbcon, String sViewID, String sNodeID) throws SQLException {

		Connection con = dbcon.getConnection();
		if (con == null)
			return null;

		PreparedStatement pstmt = con.prepareStatement(GET_VIEWLINKSFORNODE_QUERY);

		pstmt.setString(1, sViewID);
		pstmt.setString(2, sNodeID);

		ResultSet rs = null;
		
		try {
			rs = pstmt.executeQuery();
		} catch (Exception e){
			e.printStackTrace();
		}

		Vector<LinkProperties> vtLinks = new Vector<LinkProperties>(51);
		LinkProperties linkProps = null;
		Link link = null;
		View view = null;
		if (rs != null) {
			while (rs.next()) {
				//String	sViewId	= rs.getString(1);
				String	sLinkId	= rs.getString(2);
				Date	created		= new Date(new Double(rs.getLong(3)).longValue());
				Date	modified	= new Date(new Double(rs.getLong(4)).longValue());
				int	nLabelWrapWidth	= rs.getInt(5);
				int nArrowType		= rs.getInt(6);
				int nLinkStyle		= rs.getInt(7);
				int nLinkDashed		= rs.getInt(8);
				int nLinkWeight		= rs.getInt(9);
				int nLinkColour		= rs.getInt(10);
				int	nFontSize		= rs.getInt(11);
				String sFontFace	= rs.getString(12);
				int	nFontStyle		= rs.getInt(13);
				int	nForeground		= rs.getInt(14);
				int	nBackground		= rs.getInt(15);
				
				link = DBLink.getLink(dbcon, sLinkId);
				view = (View)NodeSummary.getNodeSummary(sViewID);
				linkProps = new LinkProperties(view, link, created, modified, nLabelWrapWidth, 
						nArrowType, nLinkStyle, nLinkDashed, nLinkWeight, nLinkColour, nFontSize, sFontFace,
						nFontStyle, nForeground, nBackground);

				vtLinks.addElement(linkProps);
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
		ResultSet rs = null;
		try {
			rs = pstmt.executeQuery();
		} catch (Exception e){
			e.printStackTrace();
		}

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
