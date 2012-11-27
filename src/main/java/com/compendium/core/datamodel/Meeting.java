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

package com.compendium.core.datamodel;


import java.util.Date;
import java.util.Vector;

import com.compendium.core.ICoreConstants;

/**
 * Meeting defines the data object for storing meeting data, such as name, date, attendees, agenda etc.
 *
 * @author	Michelle Bachler
 * @version	1.0
 */
public class Meeting {

	/** The id for this meeting.*/
	protected String sMeetingID		= "";

	/** The node id of the meetin's Compendium map.*/
	protected String sMapID	= "";

	/** The name given for this meeting.*/
	protected String sName	= "";

	/** The start date/time for this meeting.*/
	protected Date dStartDate	= null;


	/** The status of this meeting. Either ICoreConstants.STATUS_RECORDED or ICoreConstants.STATUS_PREPARED.*/
	protected int nStatus = ICoreConstants.STATUS_PREPARED;

	/** The node of the meeting map.*/
	protected NodeSummary oMapNode 	= null;

	/** The UserProfile of the user who created this meeting map.*/
	protected UserProfile oUser = null;


	/** The list of 'MeetingAttendee's for this meeting.*/
	protected Vector vtAttendees	= new Vector();

	/** The list of 'MeetingAgendaItem's for this meeting.*/
	protected Vector vtAgenda		= new Vector();

	/** The list of 'MeetingDocument's for this meeting.*/
	protected Vector vtDocuments	= new Vector();



	/**
	 * Constructor for a meeting data object.
	 *
	 * @param sMeetingID, the id of the meeting.
	 */
	public Meeting(String sMeetingID) {
		this.sMeetingID = sMeetingID;
	}

	/**
	 * Constructor for a meeting data object.
	 *
	 * @param sMeetingID, the id of the meeting map for this meeting.
	 * @param sMeetingMapID, the id of the meeting map.
	 * @param sName, the name of this meeting.
	 */
	public Meeting(String sMeetingID, String sMeetingMapID, String sName, Date dStartDate, int nStatus) {
		this.sMeetingID = sMeetingID;
		this.sMapID = sMeetingMapID;
		this.sName = sName;
		this.dStartDate = dStartDate;
		this.nStatus = nStatus;
	}

	/**
	 * Return the id of this meeting.
	 */
	public String getMeetingID() {
		return this.sMeetingID;
	}

	/**
	 * return the user associated with creating this meeting and associated map.
	 */
	public UserProfile getUser() {
		return oUser;
	}

	/**
	 * Set the user associated with creating this meeting and associated map.
	 */
	public  void setUser(UserProfile oUser) {
		this.oUser = oUser;
	}

	/**
	 * Return the Node associated with the meeting Map.
	 */
	public NodeSummary getMapNode() {
		return oMapNode;
	}

	/**
	 * Set the Node associated with the meeting Map.
	 */
	public void setMapNode(NodeSummary oNode) {
		oMapNode = oNode;
	}

	/**
	 * Set the name of this meeting.
	 *
	 * @param String sName, the name of this meeting.
	 */
	public void setName(String sName) {
		this.sName = sName;
	}

	/**
	 * Return the name of this meeting.
	 */
	public String getName() {
		return this.sName;
	}

	/**
	 * Set the start date/time of this meeting.
	 *
	 * @param Date dDate, the start date/time of this meeting.
	 */
	public void setStartDate(Date dDate) {
		this.dStartDate = dDate;
	}

	/**
	 * Return the start date/time of this meeting.
	 */
	public Date getStartDate() {
		return this.dStartDate;
	}

	/**
	 * Set the status of this meeting.
	 * Either <code>ICoreConstants.STATUS_PREPARED</code> or <code>ICoreConstants.STAUTS_RECORDED</code>
	 */
	public void setStatus(int nStatus) {
		this.nStatus = nStatus;
	}

	/**
	 * Get the status of this meeting.
	 * Either <code>ICoreConstants.STATUS_PREPARED</code> or <code>ICoreConstants.STAUTS_RECORDED</code>
	 */
	public int getStatus() {
		return this.nStatus;
	}

	/**
	 * Set the parent map id for this meeting.
	 *
	 * @param String sMapID, the parent map id for this meeting.
	 */
	public void setMeetingMapID(String sMapID) {
		this.sMapID = sMapID;
	}

	/**
	 * Return the parent map id for this meeting.
	 */
	public String getMeetingMapID() {
		return this.sMapID;
	}

	/**
	 * Set the list of <code>MeetingAtendee</code>s.
	 *
	 * @param Vector vtAttendees, the list of <code>MeetingAtendee</code>s.
	 */
	public void setAttendees(Vector vtAttendees) {
		this.vtAttendees = vtAttendees;
	}

	/**
	 * Return the list of <code>MeetingAtendee</code>s.
	 */
	public Vector getAttendees() {
		return this.vtAttendees;
	}

	/**
	 * Set the list of <code>MeetingAgendaItem</code>s.
	 *
	 * @param Vector vtAgenda, the list of <code>MeetingAgendaItem</code>s.
	 */
	public void setAgenda(Vector vtAgenda) {
		this.vtAgenda = vtAgenda;
	}

	/**
	 * Return the list of <code>MeetingAgendaItem</code>s.
	 */
	public Vector getAgenda() {
		return this.vtAgenda;
	}

	/**
	 * Set the list of <code>MeetingDocument</code>s.
	 * This is additional online documentation that attendees should read for the meeting etc.
	 *
	 * @param Vector vtDocuments, the list of <code>MeetingDocument</code>s.
	 */
	public void setDocuments(Vector vtDocuments) {
		this.vtDocuments = vtDocuments;
	}

	/**
	 * Return the list of <code>MeetingDocument</code>s.
	 */
	public Vector getDocuments() {
		return this.vtDocuments;
	}
}
