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
 *	The MeetingService class for interacting with the Database layer.
 *	The MeetingService class provides services to manipuate MediaIndex objects in the MediaIndex table and the Meeting table.
 *
 *	@author Michelle Bachler
 */
public class MeetingService extends ClientService implements IMeetingService, java.io.Serializable {

	/**
	 *	Constructor.
	 */
	public  MeetingService() {
		super();
	}

	/**
	 * Constructor, set the name of the service.
	 *
	 * @param String sName, the name of this service.
	 */
	public MeetingService(String sName) {
		super(sName);
	}

	/**
	 * Constructor.
	 *
	 * @param String name, the unique name of this service
 	 * @param ServiceManager sm, the current ServiceManager
	 * @param DBDtabaseManager dbMgr, the current DBDatabaseManager
	 */
	public  MeetingService(String name, ServiceManager sm, DBDatabaseManager dbMgr) {
		super(name, sm, dbMgr) ;
	}

	/**
	 * Adds a new meeting record to the database and returns it if successful.
	 *
	 * @param PCSession session, the PCSession object for the database to use.
	 * @param String sMeetingID, the identifier for the meeting.
	 * @param String sMeetingMapID, the identifier for the meetings main map.
	 * @param String sMeetingName, the name the user assigned for this meeting.
	 * @param Date dMeetingDate, the date of the meeting.
	 * @param int nStatus, the status of the meeting.
	 * @return boolean, true if the creation was successful, else false.
	 * @exception java.sql.SQLException
	 */
	public boolean createMeeting(PCSession session, String sMeetingID, String sMeetingMapID, String sMeetingName, java.util.Date dMeetingDate, int nStatus) throws SQLException {

		DBConnection dbcon = getDatabaseManager().requestConnection(session.getModelName());

		boolean isSuccessful = DBMeeting.insert(dbcon, sMeetingID, sMeetingMapID, sMeetingName, dMeetingDate, nStatus, session.getUserID());

		getDatabaseManager().releaseConnection(session.getModelName(),dbcon);

		return isSuccessful;
	}

	/**
	 * Returns the meeting data for the given meeting id.
	 *
	 * @param session com.compendium.core.datamodel.PCSession, the session object for the current database Model.
	 * @param sMeetingID, the id of the meeting whose data to return
	 * @return java.util.Vector, a vector of the Meeting data.
	 * @exception java.sql.SQLException
	 */
	public Meeting getMeeting(PCSession session, String sMeetingID) throws SQLException  {

		DBConnection dbcon = getDatabaseManager().requestConnection(session.getModelName()) ;

		Meeting oMeeting = DBMeeting.getMeeting(dbcon, sMeetingID, session.getUserID());

		getDatabaseManager().releaseConnection(session.getModelName(),dbcon);

		return oMeeting;
	}

	/**
	 * Returns the meeting data for the given meeting map id.
	 *
	 * @param session com.compendium.core.datamodel.PCSession, the session object for the current database Model.
	 * @param sMeetingID, the id of the map whose meeting whose data to return
	 * @return java.util.Vector, a vector of the Meeting data.
	 * @exception java.sql.SQLException
	 */
	public Meeting getMeetingForMap(PCSession session, String sMeetingMapID) throws SQLException  {

		DBConnection dbcon = getDatabaseManager().requestConnection(session.getModelName()) ;

		Meeting oMeeting = DBMeeting.getMeetingForMap(dbcon, sMeetingMapID, session.getUserID());

		getDatabaseManager().releaseConnection(session.getModelName(),dbcon);

		return oMeeting;
	}

	/**
	 * Returns all the meeting data.
	 *
	 * @param session com.compendium.core.datamodel.PCSession, the session object for the current database Model.
	 * @return java.util.Vector, a vector of all the Meeting data.
	 * @exception java.sql.SQLException
	 */
	public Vector getMeetings(PCSession session) throws SQLException  {

		DBConnection dbcon = getDatabaseManager().requestConnection(session.getModelName()) ;

		Vector vtMeetings = DBMeeting.getAllMeetings(dbcon, session.getUserID());

		getDatabaseManager().releaseConnection(session.getModelName(),dbcon);

		return vtMeetings;
	}

	/**
	 * Returns all the meeting data for meetings not yet recorded.
	 *
	 * @param session com.compendium.core.datamodel.PCSession, the session object for the current database Model.
	 * @return java.util.Vector, a vector of all the Meeting data.
	 * @exception java.sql.SQLException
	 */
	public Vector getPreparedMeetings(PCSession session) throws SQLException  {

		DBConnection dbcon = getDatabaseManager().requestConnection(session.getModelName()) ;

		Vector vtMeetings = DBMeeting.getAllPreparedMeetings(dbcon, session.getUserID());

		getDatabaseManager().releaseConnection(session.getModelName(),dbcon);

		return vtMeetings;
	}

	/**
	 * Returns all the meeting data for meetings already recorded.
	 *
	 * @param session com.compendium.core.datamodel.PCSession, the session object for the current database Model.
	 * @return java.util.Vector, a vector of all the Meeting data.
	 * @exception java.sql.SQLException
	 */
	public Vector getRecordedMeetings(PCSession session) throws SQLException  {

		DBConnection dbcon = getDatabaseManager().requestConnection(session.getModelName()) ;

		Vector vtMeetings = DBMeeting.getAllCompletedMeetings(dbcon, session.getUserID());

		getDatabaseManager().releaseConnection(session.getModelName(),dbcon);

		return vtMeetings;
	}

	/**
	 * Sets the Status for a Meeting.
	 *
	 * @param session com.compendium.core.datamodel.PCSession, the session object for the current database Model.
	 * @param sMeetingID, the id of the meeting whose status to update.
	 * @param nStatus, the staus of this meeting, (ICoreConstants.PREPARED or ICoreConstants.RECORDED).
	 * @return boolean, true if the MediaIndex was successfully updated, else false.
	 * @exception java.util.NoSuchElementException
	 * @exception java.sql.SQLException
	 */
	public boolean setMeetingStatus(PCSession session, String sMeetingID, int nStatus) throws SQLException {

		DBConnection dbcon = getDatabaseManager().requestConnection(session.getModelName()) ;

		boolean updated = DBMeeting.setStatus(dbcon, sMeetingID, nStatus, session.getUserID());

		getDatabaseManager().releaseConnection(session.getModelName(),dbcon);

		return updated;
	}


	/**
	 * Sets the Name for a Meeting.
	 *
	 * @param session com.compendium.core.datamodel.PCSession, the session object for the current database Model.
	 * @param sMeetingID, the id of the meeting whose name to update.
	 * @param sName, the name of this meeting.
	 * @return boolean, true if the MediaIndex was successfully updated, else false.
	 * @exception java.util.NoSuchElementException
	 * @exception java.sql.SQLException
	 */
	public boolean setMeetingName(PCSession session, String sMeetingID, String sName) throws SQLException {

		DBConnection dbcon = getDatabaseManager().requestConnection(session.getModelName()) ;

		boolean updated = DBMeeting.setName(dbcon, sMeetingID, sName, session.getUserID());

		getDatabaseManager().releaseConnection(session.getModelName(),dbcon);

		return updated;
	}


	/**
	 * Sets the Status for a Meeting.
	 *
	 * @param session com.compendium.core.datamodel.PCSession, the session object for the current database Model.
	 * @param sMeetingID, the id of the meeting whose date to update.
	 * @param dDate, the date of this meeting, (ICoreConstants.PREPARED or ICoreConstants.RECORDED).
	 * @return boolean, true if the MediaIndex was successfully updated, else false.
	 * @exception java.util.NoSuchElementException
	 * @exception java.sql.SQLException
	 */
	public boolean setMeetingDate(PCSession session, String sMeetingID, java.util.Date dMeetingDate) throws SQLException {

		DBConnection dbcon = getDatabaseManager().requestConnection(session.getModelName()) ;

		boolean updated = DBMeeting.setDate(dbcon, sMeetingID, dMeetingDate, session.getUserID());

		getDatabaseManager().releaseConnection(session.getModelName(),dbcon);

		return updated;
	}


	/**
	 * Create a Media Index record for the given MediaIndex object data.
	 *
	 * @param session com.compendium.core.datamodel.PCSession, the session object for the current database Model.
	 * @param MediaIndex, the object holding the MediaIndex data to create.
	 * @return boolean, true if the MediaIndex was successfully created, else false.
	 * @exception java.util.NoSuchElementException
	 * @exception java.sql.SQLException
	 */
	public boolean createMediaIndex(PCSession session, MediaIndex oMediaIndex) throws SQLException {

		DBConnection dbcon = getDatabaseManager().requestConnection(session.getModelName()) ;

		boolean isSuccessful = DBMediaIndex.insert(dbcon, oMediaIndex);

		getDatabaseManager().releaseConnection(session.getModelName(),dbcon);

		return isSuccessful;
	}

	/**
	 * Sets the Media Index for the given MedaiIndex object data.
	 *
	 * @param session com.compendium.core.datamodel.PCSession, the session object for the current database Model.
	 * @param MediaIndex, the object holding the MediaIndex data to update.
	 * @return boolean, true if the MediaIndex was successfully updated, else false.
	 * @exception java.util.NoSuchElementException
	 * @exception java.sql.SQLException
	 */
	public boolean setMediaIndex(PCSession session, MediaIndex oMediaIndex) throws SQLException {

		DBConnection dbcon = getDatabaseManager().requestConnection(session.getModelName()) ;

		boolean updated = DBMediaIndex.setMediaIndex(dbcon, oMediaIndex);

		getDatabaseManager().releaseConnection(session.getModelName(),dbcon);

		return updated;
	}

	/**
	 * Returns the MediaIndex for given node reference in the given view and for the given meeting.
	 *
	 * @param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 * @param sViewID, the id of the view the node is in.
	 * @param sNodeID, the id of the node to return the position for.
	 * @param sMeetingID, the id of the meeting for the medai index to return.
	 * @return com.compendium.core.datamodel.MediaIndex, the MediaIndex of the node in the view, in the meeting.
	 * @throws java.sql.SQLException
	 * @see com.compendium.core.datamodel.MediaIndex
	 */
	public MediaIndex getMediaIndex(PCSession session, String sViewID, String sNodeID, String sMeetingID) throws SQLException {

		DBConnection dbcon = getDatabaseManager().requestConnection(session.getModelName()) ;

		MediaIndex oMediaIndex = DBMediaIndex.getMediaIndex(dbcon, sViewID, sNodeID, sMeetingID);

		getDatabaseManager().releaseConnection(session.getModelName(),dbcon);

		return oMediaIndex;
	}

	/**
	 * Returns all the media indexes in the View for the given node.
	 *
	 * @param session com.compendium.core.datamodel.PCSession, the session object for the current database Model.
	 * @param sViewID, the view id of the View the node is in.
	 * @param sNodeID, the node id of the Node whose Media Indexes to return.
	 * @return java.util.Vector, a vector of all the Media indexes in this view for the node in the view.
	 * @exception java.sql.SQLException
	 * @see com.compendium.core.datamodel.MediaIndex
	 */
	public Vector getMediaIndexes(PCSession session, String sViewID, String sNodeID) throws SQLException  {

		DBConnection dbcon = getDatabaseManager().requestConnection(session.getModelName()) ;

		Vector vtMediaIndexes = DBMediaIndex.getMediaIndexes(dbcon, sViewID, sNodeID);

		getDatabaseManager().releaseConnection(session.getModelName(),dbcon);

		return vtMediaIndexes;
	}

	/**
	 * Returns all the media indexes for the given node.
	 *
	 * @param session com.compendium.core.datamodel.PCSession, the session object for the current database Model.
	 * @param sViewID, the view id of the View the node is in.
	 * @param sNodeID, the node id of the Node whose Media Indexes to return.
	 * @return java.util.Vector, a vector of all the Media indexes in this view for the node in the view.
	 * @exception java.sql.SQLException
	 * @see com.compendium.core.datamodel.MediaIndex
	 */
	public Vector getAllMediaIndexes(PCSession session, String sNodeID) throws SQLException  {

		DBConnection dbcon = getDatabaseManager().requestConnection(session.getModelName()) ;

		Vector vtMediaIndexes = DBMediaIndex.getAllMediaIndexes(dbcon, sNodeID);

		getDatabaseManager().releaseConnection(session.getModelName(),dbcon);

		return vtMediaIndexes;
	}

    public boolean deleteMeeting(PCSession session, String sMeetingID) throws SQLException {
        DBConnection dbcon = getDatabaseManager().requestConnection(session.getModelName());

        boolean isSuccessful = DBMeeting.delete(dbcon, sMeetingID, session.getUserID());

        getDatabaseManager().releaseConnection(session.getModelName(),dbcon);

        return isSuccessful;
    }
}
