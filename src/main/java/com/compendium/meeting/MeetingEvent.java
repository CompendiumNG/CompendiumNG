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

package com.compendium.meeting;

import com.compendium.ProjectCompendium;
import com.compendium.core.datamodel.NodeSummary;
import com.compendium.core.datamodel.View;
import com.compendium.core.datamodel.Code;
import com.compendium.core.datamodel.NodePosition;

/**
 * MeetingEvent defines the data object for storing meeting events data.
 *
 * @author	Michelle Bachler
 * @version	1.0
 */
public class MeetingEvent {

	/** The event is of unknown type. Only used to initialise the variable.*/
	public static final int UNKNOWN_EVENT			= -1;

	/** Indocates that a view was opened/brought to the front.*/
	public static final int VIEW_SELECTED_EVENT		= 0;

	/** Indicates a node was focused for more than 5 seconds.*/
	public static final int NODE_FOCUSED_EVENT		= 1;

	/** Indicates a tag was added to a node.*/
	public static final int TAG_ADDED_EVENT			= 2;

		/** Indicates a code was removed from a node.*/
	public static final int TAG_REMOVED_EVENT		= 3;

	/** Indicates a new node was added to a view.*/
	public static final int NODE_ADDED_EVENT		= 4;

	/** Indicates an existing node was added to a view.*/
	public static final int NODE_TRANSCLUDED_EVENT	= 5;

	/** Indicates anode was removed from a view.*/
	public static final int NODE_REMOVED_EVENT		= 6;

	/** Indicates a reference node was launched.*/
	public static final int REFERENCE_LAUNCHED_EVENT = 7;

	/** The id of the meeting associated with this event.*/
	protected String sMeetingID				= ""; //$NON-NLS-1$

	/** The id of the user who caused the event.*/
	protected String sUserID				= ""; //$NON-NLS-1$

	/** The time in milliseconds that the event occurred.*/
	protected long lMediaIndex				= 0;

	/** Indicates whether the event happened during a meeting replay.*/
	protected boolean bCreatingPostMeeting 	= false;

	/** The NodePosition associated with this view.*/
	protected NodePosition oNodePosition 	= null;

	/** The view associated with this event.*/
	protected View oView 					= null;

	/** The node associated with this event.*/
	protected NodeSummary oNode 			= null;

	/** The Code associated with this event. For TAG_ADDED_EVENT or TAG_REMOVED_EVENT.*/
	protected Code oCode					= null;

	/** The type of this meeting event.*/
	protected int nEventType				= UNKNOWN_EVENT;

	/**
	 * Constructor for nodes added events
	 *
	 * @param sMeetingID the id of the meeting this event belongs to.
	 * @param bCreatingPostMeeting true if the event was created during a replay, false if during a meeting recording.
	 * @param nEventType the type of this event.
	 * @param oNodePosition the node position object associated with the event.
	 */
	public MeetingEvent(String sMeetingID, boolean bCreatingPostMeeting, int nEventType, NodePosition oNodePos) {
		this.sMeetingID = sMeetingID;
		this.bCreatingPostMeeting = bCreatingPostMeeting;
		this.nEventType = nEventType;
		this.oNodePosition = oNodePos;
		this.oView = oNodePos.getView();
		this.oNode = oNodePos.getNode();
		this.sUserID = ProjectCompendium.APP.getModel().getUserProfile().getId();
	}

	/**
	 * Constructor for nodes removed/focused events
	 * and reference node lauched events and view selected events.
	 *
	 * @param sMeetingID the id of the meeting this event belongs to.
	 * @param bCreatingPostMeeting true if the event was created during a replay, false if during a meeting recording.
	 * @param nEventType the type of this event.
	 * @param oView the view associated with the event.
	 * @param oNode the node associated with the event.
	 */
	public MeetingEvent(String sMeetingID, boolean bCreatingPostMeeting, int nEventType, View oView, NodeSummary oNode) {
		this.sMeetingID = sMeetingID;
		this.bCreatingPostMeeting = bCreatingPostMeeting;
		this.nEventType = nEventType;
		this.oView = oView;
		this.oNode = oNode;
		this.sUserID = ProjectCompendium.APP.getModel().getUserProfile().getId();
	}

	/**
	 * Constructor for tag added or removed events.
	 *
	 * @param sMeetingID the id of the meeting this event belongs to.
	 * @param bCreatingPostMeeting true if the event was created during a replay, false if during a meeting recording.
	 * @param nEventType the type of this event.
	 * @param oView the view associated with the event.
	 * @param oNode the node associated with the event.
	 * @param oCode the code associated with the event.
	 */
	public MeetingEvent(String sMeetingID, boolean bCreatingPostMeeting, int nEventType, View oView, NodeSummary oNode, Code oCode) {
		this.sMeetingID = sMeetingID;
		this.bCreatingPostMeeting = bCreatingPostMeeting;
		this.nEventType = nEventType;
		this.oView = oView;
		this.oNode = oNode;
		this.oCode = oCode;
		this.sUserID = ProjectCompendium.APP.getModel().getUserProfile().getId();
	}

	/**
	 * Return the id associated with this meeting.
	 */
	public String getMeetingID() {
		return sMeetingID;
	}

	/**
	 * Return the type of event this object represents.
	 */
	public int getEventType() {
		return nEventType;
	}

	/**
	 * Return the view id associated with this event.
	 */
	public String getViewID() {
		return oView.getId();
	}

	/**
	 * Return the node id associated with this event.
	 */
	public String getNodeID() {
		return oNode.getId();
	}

	/**
	 * Return the NodePosition object associated with this event.
	 */
	public NodePosition getNodePosition() {
		return oNodePosition;
	}

	/**
	 * Return the View object associated with this event.
	 */
	public View getView() {
		return oView;
	}

	/**
	 * Return the NodeSumary object associated with this event.
	 */
	public NodeSummary getNode() {
		return oNode;
	}

	/**
	 * Set the Media Index of this event.
	 * @param lMediaIndex the media index for this meeting event.
	 */
	public void setMediaIndex(long lMediaIndex) {
		this.lMediaIndex = lMediaIndex;
	}

	/**
	 * Return the Media Index of this event.
	 */
	public long getMediaIndex() {
		return lMediaIndex;
	}

	/**
	 * Return the user id associated with this event.
	 */
	public String getUserID() {
		return sUserID;
	}

	/**
	 * Return if this event was created during a meeting replay.
	 */
	public boolean creatingPostMeeting() {
		return bCreatingPostMeeting;
	}

	/**
	 * Return the Code associated with this event if there is one, else null.
	 */
	public Code getCode() {
		return oCode;
	}
}
