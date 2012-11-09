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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;

import com.compendium.core.datamodel.*;
import com.compendium.core.db.management.*;
import com.compendium.core.ICoreConstants;

/**
 * The DBMeeting class serves as the interface layer for the Meeting table in the database.
 *
 * @author	Michelle Bachler
 */
public class DBMeeting {

// AUDITED

    /** SQL statement to delete the reference node with the given no id.*/
    public final static String DELETE_MEETING_QUERY =
        "DELETE "+
        "FROM Meeting "+
        "WHERE MeetingID = ? ";
    
	/** SQL statement to insert a new Meeting Record into the Meeting table.*/
	public final static String INSERT_MEETING_QUERY =
		"INSERT INTO Meeting (MeetingID, MeetingMapID, MeetingName, MeetingDate, CurrentStatus) "+
		"VALUES (?, ?, ?, ?, ?)";

	/** SQL statement to set the status for the given meeting id.*/
	public final static String SET_MEETING_STATUS =
		"UPDATE Meeting SET CurrentStatus = ? "+
		"WHERE MeetingID = ?";

    /** SQL statement to set the status for the given meeting id.*/
    public final static String SET_MEETING_MAP_ID =
        "UPDATE Meeting SET MeetingMapID = ? "+
        "WHERE MeetingID = ?";

	/** SQL statement to set the name for the given meeting id.*/
	public final static String SET_MEETING_NAME =
		"UPDATE Meeting SET MeetingName = ? "+
		"WHERE MeetingID = ?";

	/** SQL statement to set the date for the given meeting id.*/
	public final static String SET_MEETING_DATE =
		"UPDATE Meeting SET MeetingDate = ? "+
		"WHERE MeetingID = ?";

// UNAUDITED

	/** SQL statement to return the all the Meeting records.*/
	public final static String GET_ALL_MEETINGS_QUERY =
		"SELECT * " +
		"FROM Meeting";

	/** SQL statement to return the all the Meeting records for meeting not yet recorded.*/
	public final static String GET_ALL_PREPARED_MEETINGS_QUERY =
		"SELECT * " +
		"FROM Meeting "+
		"WHERE CurrentStatus = "+ICoreConstants.STATUS_PREPARED;

	/** SQL statement to return the all the Meeting records for recorded meetings.*/
	public final static String GET_ALL_COMPLETED_MEETINGS_QUERY =
		"SELECT * " +
		"FROM Meeting "+
		"WHERE CurrentStatus = "+ICoreConstants.STATUS_RECORDED;


	/** SQL statement to return the Meeting record with the given meeting id.*/
	public final static String GET_MEETING_QUERY =
		"SELECT * " +
		"FROM Meeting " +
		"WHERE MeetingID = ?";

	/** SQL statement to return the Meeting record with the given meetingmap id.*/
	public final static String GET_MEETING_MAP_QUERY =
		"SELECT * " +
		"FROM Meeting " +
		"WHERE MeetingMapID = ?";

// SETTERS

	/**
     * Deletes a meeting
     */
    public static boolean delete(DBConnection dbcon, String sMeetingID, String userID) throws SQLException {
        Connection con = dbcon.getConnection() ;
        if (con == null)
            return false;

        Meeting meeting= null;
        if (DBAudit.getAuditOn()) {
            meeting = getMeeting(dbcon, sMeetingID, userID);
        }

        PreparedStatement pstmt = con.prepareStatement(DELETE_MEETING_QUERY);

        pstmt.setString(1, sMeetingID);

        int nRowCount = pstmt.executeUpdate();
        pstmt.close();

        if (nRowCount > 0) {
            if (DBAudit.getAuditOn() && meeting != null) {
                int nStatus = meeting.getStatus();
                String sMeetingMapID = meeting.getMeetingMapID();
                String sMeetingName = meeting.getName();
                Date dMeetingDate = meeting.getStartDate();
                DBAudit.auditMeeting(dbcon, DBAudit.ACTION_EDIT, sMeetingID, sMeetingMapID, sMeetingName, dMeetingDate, nStatus);
            }
            return true;
        }
        return false;
    }

	/**
	 *  Inserts a new Meeting record in the database and returns a MediaIndex object representing this record.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param sMeetingID, the id of this meeting.
	 *	@param sMeetingMapID, the id of the main parent meeting map
	 *	@param sMeetingName, the name the meeting is known as in Compendium.
	 *	@param dMeetingDate, the date of the meeting.
	 * 	@param nStatus, the status of this meeting. Either ICoreConstants.STATUS_PREPARED or ICoreConstants.STATUS_RECORDED
	 *	@return boolean, true if it was successful, else false.
	 *	@throws java.sql.SQLException
	 */
	public static boolean insert(DBConnection dbcon, String sMeetingID, String sMeetingMapID, String sMeetingName, java.util.Date dMeetingDate, int nStatus, String userID) throws SQLException  {

		Connection con = dbcon.getConnection();
		if (con == null)
			return false;

		// CHECK IF ALREADY IN DATABASE
		Meeting meeting = getMeeting(dbcon, sMeetingID, userID);
		if (meeting != null) {
			if (DBNode.getImporting() && DBNode.getUpdateTranscludedNodes()) {
				setStatus(dbcon, sMeetingID, nStatus, userID);
			}
            setMeetingMapID(dbcon, sMeetingID, sMeetingMapID, userID);
			return true;
		}

		PreparedStatement pstmt = con.prepareStatement(INSERT_MEETING_QUERY);

		pstmt.setString(1, sMeetingID);
		pstmt.setString(2, sMeetingMapID);
		pstmt.setString(3, sMeetingName);
		pstmt.setDouble(4, dMeetingDate.getTime());
		pstmt.setInt(5, nStatus);

		int nRowCount = pstmt.executeUpdate();

		pstmt.close();

		if (nRowCount > 0) {
			if (DBAudit.getAuditOn()) {
				DBAudit.auditMeeting(dbcon, DBAudit.ACTION_ADD, sMeetingID, sMeetingMapID, sMeetingName, dMeetingDate, nStatus);
			}
			return true;
		}
		return false;
	}
    
    /**
     *  Update the meeting map id in the database and return if successful.
     *
     *  @param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
     *  @param sMeetingID, the id of the meeting whose status to update.
     *  @param sMeetingMapID, the map id of this meeting
     *  @return boolean, true if it was successful, else false.
     *  @throws java.sql.SQLException
     */
    public static boolean setMeetingMapID(DBConnection dbcon, String sMeetingID, String sMeetingMapID, String userID)  throws SQLException {

        Connection con = dbcon.getConnection();
        if (con == null)
            return false;

        Meeting meeting= null;
        if (DBAudit.getAuditOn()) {
            meeting = getMeeting(dbcon, sMeetingID, userID);
        }

        PreparedStatement pstmt = con.prepareStatement(SET_MEETING_MAP_ID);

        pstmt.setString(1, sMeetingMapID);
        pstmt.setString(2, sMeetingID);

        int nRowCount = pstmt.executeUpdate();

        pstmt.close();

        if (nRowCount > 0) {
            if (DBAudit.getAuditOn() && meeting != null) {
                int nStatus = meeting.getStatus();
                String sMeetingName = meeting.getName();
                Date dMeetingDate = meeting.getStartDate();
                DBAudit.auditMeeting(dbcon, DBAudit.ACTION_EDIT, sMeetingID, sMeetingMapID, sMeetingName, dMeetingDate, nStatus);
            }
            return true;
        }
        return false;
    }

	/**
	 *  Update the meeting status in the database and return if successful.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param sMeetingID, the id of the meeting whose status to update.
	 *  @param nStatus, the staus of this meeting, (ICoreConstants.PREPARED or ICoreConstants.RECORDED).
	 *	@return boolean, true if it was successful, else false.
	 *	@throws java.sql.SQLException
	 */
	public static boolean setStatus(DBConnection dbcon, String sMeetingID, int nStatus, String userID)  throws SQLException {

		Connection con = dbcon.getConnection();
		if (con == null)
			return false;

		Meeting meeting= null;
		if (DBAudit.getAuditOn()) {
			meeting = getMeeting(dbcon, sMeetingID, userID);
		}

		PreparedStatement pstmt = con.prepareStatement(SET_MEETING_STATUS);

		pstmt.setInt(1, nStatus);
		pstmt.setString(2, sMeetingID);

		int nRowCount = pstmt.executeUpdate();

		pstmt.close();

		if (nRowCount > 0) {
			if (DBAudit.getAuditOn() && meeting != null) {
				String sMeetingMapID = meeting.getMeetingMapID();
				String sMeetingName = meeting.getName();
				Date dMeetingDate = meeting.getStartDate();
				DBAudit.auditMeeting(dbcon, DBAudit.ACTION_EDIT, sMeetingID, sMeetingMapID, sMeetingName, dMeetingDate, nStatus);
			}
			return true;
		}
		return false;
	}

	/**
	 *  Update the meeting name in the database and return if successful.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param sMeetingID, the id of the meeting whose status to update.
	 *  @param sName, the new name of this meeting.
	 *	@return boolean, true if it was successful, else false.
	 *	@throws java.sql.SQLException
	 */
	public static boolean setName(DBConnection dbcon, String sMeetingID, String sMeetingName, String userID)  throws SQLException {

		Connection con = dbcon.getConnection();
		if (con == null)
			return false;

		Meeting meeting= null;
		if (DBAudit.getAuditOn()) {
			meeting = getMeeting(dbcon, sMeetingID, userID);
		}

		PreparedStatement pstmt = con.prepareStatement(SET_MEETING_NAME);

		pstmt.setString(1, sMeetingName);
		pstmt.setString(2, sMeetingID);

		int nRowCount = pstmt.executeUpdate();

		pstmt.close();

		if (nRowCount > 0) {
			if (DBAudit.getAuditOn() && meeting != null) {
				String sMeetingMapID = meeting.getMeetingMapID();
				int nStatus = meeting.getStatus();
				Date dMeetingDate = meeting.getStartDate();
				DBAudit.auditMeeting(dbcon, DBAudit.ACTION_EDIT, sMeetingID, sMeetingMapID, sMeetingName, dMeetingDate, nStatus);
			}
			return true;
		}
		return false;
	}

	/**
	 *  Update the meeting date in the database and return if successful.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param sMeetingID, the id of the meeting whose status to update.
	 *  @param dMeetingDate, the new date of this meeting.
	 *	@return boolean, true if it was successful, else false.
	 *	@throws java.sql.SQLException
	 */
	public static boolean setDate(DBConnection dbcon, String sMeetingID, java.util.Date dMeetingDate, String userID)  throws SQLException {

		Connection con = dbcon.getConnection();
		if (con == null)
			return false;

		Meeting meeting= null;
		if (DBAudit.getAuditOn()) {
			meeting = getMeeting(dbcon, sMeetingID, userID);
		}

		PreparedStatement pstmt = con.prepareStatement(SET_MEETING_DATE);

		pstmt.setDouble(1, dMeetingDate.getTime());
		pstmt.setString(2, sMeetingID);

		int nRowCount = pstmt.executeUpdate();

		pstmt.close();

		if (nRowCount > 0) {
			if (DBAudit.getAuditOn() && meeting != null) {
				String sMeetingMapID = meeting.getMeetingMapID();
				String sMeetingName = meeting.getName();
				int nStatus = meeting.getStatus();
				DBAudit.auditMeeting(dbcon, DBAudit.ACTION_EDIT, sMeetingID, sMeetingMapID, sMeetingName, dMeetingDate, nStatus);
			}
			return true;
		}
		return false;
	}

// GETTERS

	/**
	 *	Returns the Meeting data for the given meeting id in a Meeting object.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param sMeetingID, the id of the meeting whose data to return.
	 *	@return the Meeting data if found, else null.
	 *	@throws java.sql.SQLException
	 */
	public static Meeting getMeeting(DBConnection dbcon, String sMeetingID, String userID) throws SQLException {

		Connection con = dbcon.getConnection();
		if (con == null)
			return null;

		PreparedStatement pstmt = con.prepareStatement(GET_MEETING_QUERY);
		pstmt.setString(1, sMeetingID);
		ResultSet rs = pstmt.executeQuery();

		Meeting meeting = null;

		if (rs != null) {
			if (rs.next()) {

				String	sMeetingMapID	= rs.getString(2);
				String  sMeetingName	= rs.getString(3);
				Date	dMeetingDate	= new Date(new Double(rs.getLong(4)).longValue());
				int		nStatus			= rs.getInt(5);

				meeting = new Meeting(sMeetingID, sMeetingMapID, sMeetingName, dMeetingDate, nStatus);
				IView view = DBNode.getView(dbcon, sMeetingMapID, userID);
				if (view != null)
					meeting.setMapNode((NodeSummary)view);
			}
		}

		pstmt.close();

		return meeting;
	}

	/**
	 *	Returns the Meeting data for the given meeting map id in a Meeting object.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param sMeetingMapID, the id of the map whose meeting whose data to return.
	 *	@return the Meeting data if found, else null.
	 *	@throws java.sql.SQLException
	 */
	public static Meeting getMeetingForMap(DBConnection dbcon, String sMeetingMapID, String userID) throws SQLException {

		Connection con = dbcon.getConnection();
		if (con == null)
			return null;

		PreparedStatement pstmt = con.prepareStatement(GET_MEETING_MAP_QUERY);
		pstmt.setString(1, sMeetingMapID);
		ResultSet rs = pstmt.executeQuery();

		Meeting meeting = null;

		if (rs != null) {
			if (rs.next()) {
				String	sMeetingID		= rs.getString(1);
				String  sMeetingName	= rs.getString(3);
				Date	dMeetingDate	= new Date(new Double(rs.getLong(4)).longValue());
				int		nStatus			= rs.getInt(5);

				meeting = new Meeting(sMeetingID, sMeetingMapID, sMeetingName, dMeetingDate, nStatus);
				IView view = DBNode.getView(dbcon, sMeetingMapID, userID);
				if (view != null)
					meeting.setMapNode((NodeSummary)view);
			}
		}

		pstmt.close();

		return meeting;
	}

	/**
	 *	Returns a list of all the Meeting data for meetings not yet recorded.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@return a list of the Meeting data.
	 *	@throws java.sql.SQLException
	 */
	public static Vector getAllMeetings(DBConnection dbcon, String userID) throws SQLException {

		Connection con = dbcon.getConnection();
		if (con == null)
			return null;

		PreparedStatement pstmt = con.prepareStatement(GET_ALL_MEETINGS_QUERY);

		ResultSet rs = pstmt.executeQuery();

		Vector data = new Vector(10);
		Meeting meeting = null;

		if (rs != null) {
			while (rs.next()) {

				String	sMeetingID		= rs.getString(1);
				String	sMeetingMapID	= rs.getString(2);
				String  sMeetingName	= rs.getString(3);
				Date	dMeetingDate	= new Date(new Double(rs.getLong(4)).longValue());
				int		nStatus			= rs.getInt(5);

				meeting = new Meeting(sMeetingID, sMeetingMapID, sMeetingName, dMeetingDate, nStatus);
				IView view = DBNode.getView(dbcon, sMeetingMapID, userID);
				if (view != null)
					meeting.setMapNode((NodeSummary)view);

				data.addElement(meeting);
			}
		}

		pstmt.close();
		return data;
	}

	/**
	 *	Returns a list of all the Meeting data for meetings not yet recorded.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@return a list of the Meeting data.
	 *	@throws java.sql.SQLException
	 */
	public static Vector getAllPreparedMeetings(DBConnection dbcon, String userID) throws SQLException {

		Connection con = dbcon.getConnection();
		if (con == null)
			return null;

		PreparedStatement pstmt = con.prepareStatement(GET_ALL_PREPARED_MEETINGS_QUERY);

		ResultSet rs = pstmt.executeQuery();

		Vector data = new Vector(10);
		Meeting meeting = null;

		if (rs != null) {
			while (rs.next()) {

				String	sMeetingID		= rs.getString(1);
				String	sMeetingMapID	= rs.getString(2);
				String  sMeetingName	= rs.getString(3);
				Date	dMeetingDate	= new Date(new Double(rs.getLong(4)).longValue());
				int		nStatus			= rs.getInt(5);

				meeting = new Meeting(sMeetingID, sMeetingMapID, sMeetingName, dMeetingDate, nStatus);
				IView view = DBNode.getView(dbcon, sMeetingMapID, userID);
				if (view != null)
					meeting.setMapNode((NodeSummary)view);

				data.addElement(meeting);
			}
		}

		pstmt.close();
		return data;
	}

	/**
	 *	Returns a list of all the Meeting data for meetings recorded.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@return a list of the Meeting data.
	 *	@throws java.sql.SQLException
	 */
	public static Vector getAllCompletedMeetings(DBConnection dbcon, String userID) throws SQLException {

		Connection con = dbcon.getConnection();
		if (con == null)
			return null;

		PreparedStatement pstmt = con.prepareStatement(GET_ALL_COMPLETED_MEETINGS_QUERY);

		ResultSet rs = pstmt.executeQuery();

		Vector data = new Vector(10);
		Meeting meeting = null;

		if (rs != null) {
			while (rs.next()) {

				String	sMeetingID		= rs.getString(1);
				String	sMeetingMapID	= rs.getString(2);
				String  sMeetingName	= rs.getString(3);
				Date	dMeetingDate	= new Date(new Double(rs.getLong(4)).longValue());
				int		nStatus			= rs.getInt(5);

				meeting = new Meeting(sMeetingID, sMeetingMapID, sMeetingName, dMeetingDate, nStatus);
				IView view = DBNode.getView(dbcon, sMeetingMapID, userID);
				if (view != null)
					meeting.setMapNode((NodeSummary)view);

				data.addElement(meeting);
			}
		}

		pstmt.close();
		return data;
	}
}
