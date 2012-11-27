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

import java.awt.Point;
import java.awt.Dimension;
import java.awt.Cursor;
import java.awt.Color;

import java.net.*;
import java.io.*;

import java.util.List;
import java.util.Vector;
import java.util.Hashtable;
import java.util.Enumeration;
import java.util.Date;
import java.util.Properties;
import java.util.StringTokenizer;

import java.sql.SQLException;
import java.text.SimpleDateFormat;

import com.hp.hpl.jena.rdf.model.* ;

import com.compendium.LanguageProperties;
import com.compendium.ProjectCompendium;
import com.compendium.core.*;
import com.compendium.core.datamodel.*;
import com.compendium.core.datamodel.services.IMeetingService;
import com.compendium.core.db.management.*;

import com.compendium.ui.*;

import com.compendium.ui.dialogs.UIProgressDialog;
import com.compendium.ui.plaf.NodeUI;
import com.compendium.ui.plaf.ViewPaneUI;
import com.compendium.ui.plaf.ListUI;

import com.compendium.io.xml.XMLExport;

import com.compendium.meeting.io.ArenaConnection;
import com.compendium.meeting.io.TripleStoreConnection;
import com.compendium.meeting.io.JabberConnection;
import com.compendium.meeting.remote.RecordListener;

/**
 * This class manages a meeting recording session or a meeting replay.
 *
 * @author	Michelle Bachler
 * @version	1.0
 */
public class MeetingManager {


	/** A reference to a meeting recording session.*/
	public final static int RECORDING					= 0;

	/** A reference to a meeting replay session.*/
	public final static int REPLAY						= 1;

	/** A reference to the Jabber handler for the Meeting Replay connection.*/
	public static JabberConnection oJabberConnection 	= null;

	/** The default directory to save to.*/
	public static String 	sDirectory 		= ProjectCompendium.sHOMEPATH+ProjectCompendium.sFS+"System"+ProjectCompendium.sFS+"resources"+ProjectCompendium.sFS+"Meetings"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

	private static final String 	ORIGINALID_TAG		="TS:"; //$NON-NLS-1$

	/** The text to append to the title bar to indicate you are in recorder mode.*/
	private static final String 	RECORDER_TITLE		= ": - RECORDING MEETING"; //$NON-NLS-1$

	/** The text to append to the title bar to indicate you are in replay mode.*/
	private static final String 	REPLAY_TITLE		= ": - REPLAYING MEETING"; //$NON-NLS-1$


	/** Static text for agenda item tag.*/
	private static final String 	AGENDA_CODE_TEXT	=	"Agenda Item"; //$NON-NLS-1$

	/** Static text for attendee item tag.*/
	private static final String 	ATTENDEE_CODE_TEXT	=	"Meeting Attendee"; //$NON-NLS-1$

	/** Static text for document item tag.*/
	private static final String 	DOCUMENT_CODE_TEXT	=	"Meeting Document"; //$NON-NLS-1$

	/** Static text for notes (deleted nodes) tag.*/
	private static final String 	NOTES_CODE_TEXT		=	"Meeting Notes"; //$NON-NLS-1$


	/** The label for the map holding the attendee data.*/
	private static final String 	ATTENDEE_LABEL		=	"Attendees"; //$NON-NLS-1$

	/** The label for the node from which the agenda items are linked.*/
	private static final String 	AGENDA_LABEL		=	"Agenda Items"; //$NON-NLS-1$

	/** The label for the node froim which the documents are linked.*/
	private static final String 	DOCUMENT_LABEL		=	"Additional Documents"; //$NON-NLS-1$

	/** The label for the node froim which the documents are linked.*/
	private static final String 	NOTES_LABEL			=	"Meeting Notes"; //$NON-NLS-1$

	/** The x offset to place AgendaItem, Document and Attendee nodes at.*/
	private static final int		X_OFFSET			=	300;

	/** The spacer between nodes on the y axis.*/;
	private static final int		Y_SPACER			=	70;

	/** the format to use when putting the meeting date into the node details*/
	private static SimpleDateFormat sdf 		= new SimpleDateFormat("MMM d, yyyy HH:mm"); //$NON-NLS-1$

	/** The id of this meeting.*/
	private String 			sMeetingID			= ""; //$NON-NLS-1$

	/** The id of the meeting map associated with the current meeting.*/
	private String 			sMeetingMapID		= ""; //$NON-NLS-1$

	/** The type of this meetin, Recording or Replay.*/
	private int 			nMeetingType		= -1;

	/**
	 * Holds the a list of node added nodes against their nodeid,
	 * for checking that when additional events like tagging happen, the node has had a node added event.
	 */
	private Hashtable 		htNodesAdded		= new Hashtable();

	/** Holds the additional events to be logged during a meeting recording session.*/
	private Vector 			vtEvents 			= new Vector();

	/** Holds the additional events to be logged during a meeting recording session, when the recording is paused.*/
	private Vector 			vtPausedEvents 			= new Vector();

	/** The Object that holds all the meeting data downloaded from the triplestore.*/
	private Meeting 		oMeeting 			= null;

	/** Indicates if meeting events should be captured.*/
	private boolean 		bCaptureEvents 		= false;

	/** The timestamp to use when calculating the offset from the start of the meeting.*/
	private long 			nOffsetTime 		= 0;

	/** The timestamp to use as the current offset from the start of the meeting when replaying a meeting.*/
	private long 			nCurrentOffsetTime 	= 0;

	/** Indicates if the meeting data has been downloaded within this session.*/
	private boolean 		bMeetingDataDownloaded = false;

	/** The current extra title tag (RECORDING MEETING/ REPLAYING MEETING).*/
	private String 			sExtraTitle			=""; //$NON-NLS-1$

	/** The original title in the application title bar.*/
	private String 			sTitle				=""; //$NON-NLS-1$

	/** Indicates id recording events is currently paused.*/
	private boolean			isPaused			= false;

	/** Indicates that recording has started.*/
	private boolean			bIsRecording		= false;

	/** Holds the data to Connect to Arean and the triplestore.*/
	private AccessGridData oConnectionData		= null;

	/** The session id associated with the current MeetingID.*/
	private String 			sSessionID 			= ""; //$NON-NLS-1$

	/** The filename of the zip file to download for the map data for a replay.*/
	private String 			sMapFile			= ""; //$NON-NLS-1$

	/** An integer representing the increment to use for the progress updates */
	private int				increment = 1;

	/** A Vector of registered DBProgressListeners */
	protected Vector 		progressListeners;

	private boolean			bStopOffsetUpdate = false;

    // The connection properties for the replay
    private Properties connectionProperties = null; 
  
	/** The author name of the current user */
	private String sAuthor = ""; //$NON-NLS-1$

	/**
	 * Constructor. Create a new instance of MeetingManager.
	 */
	public MeetingManager() throws AccessGridDataException {
		oConnectionData = new AccessGridData();
		progressListeners = new Vector();
		this.sAuthor = ProjectCompendium.APP.getModel().getUserProfile().getUserName();

		if (!oConnectionData.canDoXML()) {
			throw new AccessGridDataException(LanguageProperties.getString(LanguageProperties.MEETING_BUNDLE, "MeetingManager.exception1a")+"\n\n"+
					LanguageProperties.getString(LanguageProperties.MEETING_BUNDLE, "MeetingManager.exception1b")+"\n\n"); //$NON-NLS-1$
		}
	}

	/**
	 * Constructor. Create a new instance of MeetingManager, with the given properties.
	 * @param nType the type of this meeting, Recording or Replay.
	 */
	public MeetingManager(int nType) throws AccessGridDataException {
		this.nMeetingType = nType;
		progressListeners = new Vector();

		if (nMeetingType == MeetingManager.REPLAY) {
			oJabberConnection = new JabberConnection(this);
		}
		oConnectionData = new AccessGridData();

		/*if (!oConnectionData.canDoXML()) {
			throw new AccessGridDataException("Some of the required Access Grid data has not been entered.\n\nPlease use the 'Access Grid Meeting Setup' on the Tools/memetic menu to enter this data.\n\n");
		} */
	}

	/**
	 * Download the meeting data for the given meeting id.
	 *
	 * @param sMeetingID the id to download the data for.
	 * @return if successful.
	 */
	public boolean downloadMeetingData(String sMeetingID) {

		this.sMeetingID = sMeetingID;

		try {
			TripleStoreConnection connection = new TripleStoreConnection(oConnectionData);
			oMeeting = connection.downloadMeetingData(sMeetingID);
			bMeetingDataDownloaded = true;
			oMeeting.setUser(ProjectCompendium.APP.getModel().getUserProfile());
			sSessionID = connection.getSessionID(sMeetingID);
			return true;
		} catch(AccessGridDataException agde) {
			ProjectCompendium.APP.displayError(agde.getMessage());
			return false;
		} catch(Exception ex) {
			ProjectCompendium.APP.displayError(LanguageProperties.getString(LanguageProperties.MEETING_BUNDLE, "MeetingManager.error")+":\n\n"+ex.getLocalizedMessage()); //$NON-NLS-1$
			ex.printStackTrace();
			return false;
		}
	}


//********************* GETTERS and SETTERS ****************************//

	/**
	 * Reload the Access Grid Data.
	 */
	 public void reloadAccessGridData() {
		 oConnectionData.loadProperties();
         if (!oConnectionData.getArenaURL().equals("")) { //$NON-NLS-1$
             System.setProperty("java.rmi.server.codebase", //$NON-NLS-1$
                     oConnectionData.getArenaURL() + "/stubs/"); //$NON-NLS-1$
             try {
                  ProjectCompendium.oRecordListener =
                     new RecordListener(ProjectCompendium.sCompendiumInstanceID,
                             ProjectCompendium.nRMIPort);
             }
             catch(Exception ex) {
                 ex.printStackTrace();
             }
         }
	 }

	/**
	 * Set if events should be captured.
	 * @param bCapture true if events should be captured, else false.
	 */
	public void setCaptureEvents(boolean bCapture) {
		this.bCaptureEvents = bCapture;
	}

	/**
	 * Return if events should be captured.
	 */
	public boolean captureEvents() {
		return bCaptureEvents;
	}

	/**
	 * Return the count of the Meeting event data stored.
	 */
	public int getEventCount() {
		return vtEvents.size();
	}

	/**
	 * Return the Meeting object associated with this meeting.
	 */
	public Meeting getMeeting() {
		return oMeeting;
	}

	/**
	 * Set the Meeting object associated with this meeting.
	 */
	public void setMeeting(Meeting oMeeting) {
		this. oMeeting = oMeeting;
		sMeetingID = oMeeting.getMeetingID();
		sMeetingMapID = oMeeting.getMeetingMapID();
	}

	/**
	 * Return the id associated with this meeting.
	 */
	public String getMeetingID() {
		return sMeetingID;
	}

	/**
	 * Set the id associated with this meeting.
	 */
	public void setMeetingID(String sID) {
		sMeetingID = sID;
	}

	/**
	 * Get the map id associated with this meeting.
	 */
	public String getMeetingMapID() {
		return sMeetingMapID;
	}

	/**
	 * Set the map id associated with this meeting.
	 */
	public void setMeetingMapID(String sID) {
		sMeetingMapID = sID;
	}

	/**
	 * Return the type of this meeting, RECORDING or REPLAY.
	 */
	public int getMeetingType() {
		return nMeetingType;
	}

	/**
	 * Return if recording has started.
	 */
	public boolean isRecording() {
		return bIsRecording;
	}

	/**
	 * Return if recording is currently paused.
	 */
	public boolean isPaused() {
		return isPaused;
	}

	/**
	 * Return the type of this meeting, RECORDING or REPLAY.
	 */
	public boolean isReplay() {
		if (nMeetingType == MeetingManager.REPLAY)
			return true;
		else
			return false;
	}

	/**
	 * Stop/Start the updating of the offset time.
	 */
	public void pauseCurrentOffsetUpdate(boolean bStopUpdate) {
		this.bStopOffsetUpdate = bStopUpdate;
	}

	/**
	 * Get the current offset time to synchronize with MeetingReplay.
	 */
	public long getCurrentOffset() {
		return nCurrentOffsetTime;
	}


	/**
	 * Set the current offset time to synchronize with MeetingReplay.
	 *
	 * @param nCurrentOffset, the current offset in the MeetingReplay tool.
	 */
	public void setCurrentOffset(long nCurrentOffset) {
		if (!bStopOffsetUpdate) {
			nCurrentOffsetTime = nCurrentOffset;
		}
	}

	/**
	 * Set the filename of the zip file to download containing the map data for the replay.
	 */
	public void setMapDataFile(String sFileName) {
		sMapFile = sFileName;
	}

	/**
	 * Pause Recording events for a meeting.
	 */
	 public void pauseRecording() {
		//setCaptureEvents(false);
		isPaused = true;
		ProjectCompendium.APP.setTitle(sTitle + sExtraTitle+" PAUSED"); //$NON-NLS-1$
		ProjectCompendium.APP.getStatusBar().resetColors();
		ProjectCompendium.APP.getStatusBar().setBackgroundColor(Color.black);
		ProjectCompendium.APP.getStatusBar().setForegroundColor(Color.white);
	 }

	/**
	 * Resume Recording.events for a meeting.
	 */
	 public void resumeRecording() {
		isPaused = false;
		//setCaptureEvents(true);
		ProjectCompendium.APP.setTitle(sTitle + sExtraTitle);
		ProjectCompendium.APP.getStatusBar().resetColors();
		if (nMeetingType == RECORDING) {
			ProjectCompendium.APP.getStatusBar().setBackgroundColor(Color.red);
			ProjectCompendium.APP.getStatusBar().setForegroundColor(Color.white);
		} else {
			ProjectCompendium.APP.getStatusBar().setBackgroundColor(Color.yellow);
			ProjectCompendium.APP.getStatusBar().setForegroundColor(Color.black);
		}
	 }

	 /**
	  * Add the node and view to hashtable for checking later.
	  * Returns if the pair were already in the checking list.
	  *
	  * @param sNodeID the id of the node to add.
	  * @param sViewID the id of the view to add against that node.
	  * @param sMediaIndex the media index for the node creation event.
	  * @return true if the node/view combination are already in the check list, else false;
	  */
	 public boolean addNodeView(String sNodeID, String sViewID, String sMediaIndex) {

		boolean bAlreadyThere = false;

		if (!htNodesAdded.containsKey(sNodeID)) {
			Hashtable inner = new Hashtable();
			inner.put(sViewID, sMediaIndex);
			htNodesAdded.put(sNodeID, inner);
		} else {
			Hashtable inner = (Hashtable)htNodesAdded.get(sNodeID);
			if (inner.containsKey(sViewID)) {
				String index = (String) inner.get(sViewID);
				if (index.equals(sMediaIndex)) {
					bAlreadyThere = true;
				}
			} else {
				inner.put(sViewID, sMediaIndex);
				htNodesAdded.put(sNodeID, inner);
			}
		}

		return bAlreadyThere;
	 }

	 /**
	  * Returns if the node/view pair were already in the checking list.
	  *
	  * @param sNodeID, the id of the node to check.
	  * @param sViewID, the id of the view to check
	  * @return, true if the node-view combination are already in the check list, else false;
	  */
	 public boolean hasNodeView(String sNodeID, String sViewID) {
		boolean bAlreadyThere = false;

		if (htNodesAdded.containsKey(sNodeID)) {
			Hashtable inner = (Hashtable)htNodesAdded.get(sNodeID);
			if (inner.containsKey(sViewID)) {
				bAlreadyThere = true;
			}
		}

		return bAlreadyThere;
	}

	 /**
	  * Returns if the node/view pair were already in the checking list.
	  *
	  * @param sNodeID, the id of the node to check.
	  * @param sViewID, the id of the view to check
	  * @param sMediaIndex, the media index for the node creation event.
	  * @return, true if the node-view combination are already in the check list, else false;
	  */
	 public boolean hasNodeView(String sNodeID, String sViewID, String sMediaIndex) {
		boolean bAlreadyThere = false;

		if (htNodesAdded.containsKey(sNodeID)) {
			Hashtable inner = (Hashtable)htNodesAdded.get(sNodeID);
			if (inner.containsKey(sViewID)) {
				String index = (String) inner.get(sViewID);

				if (index.equals(sMediaIndex)) {
					bAlreadyThere = true;
				}
			}
		}

		return bAlreadyThere;
	}

//******************* METHODS FOR RECORDING A MEETING *********************//

	/**
	 * Setup and initialize meeting recording.
	 * Open meeting map.
	 * Calculate Offset.
	 * Check for new Meeting data and update meeting map.
	 * Create events for all nodes on meeting map
	 */
	 public boolean startRecording() {

		IModel model = ProjectCompendium.APP.getModel();
		View view = null;
		try {
			view = (View)model.getNodeService().getView(model.getSession(), sMeetingMapID);
            view.initialize(model.getSession(), model);
		} catch (Exception ex) {
			ProjectCompendium.APP.displayError(LanguageProperties.getString(LanguageProperties.MEETING_BUNDLE, "MeetingManager.errorNoMap")); //$NON-NLS-1$
			return false;
		}

		// OPEN MEETING MAP, IF NOT ALREADY
		final UIViewFrame viewFrame = ProjectCompendium.APP.addViewToDesktop(view, view.getLabel());
		ProjectCompendium.APP.setWaitCursor();
		viewFrame.setCursor(new Cursor(java.awt.Cursor.WAIT_CURSOR));

		// CALCULATE OFFSET
		if (!calculateOffset()) {
			return false;
		}

		// CHECK FOR NEW MEETING DATA FROM TRIPLESTORE AND ADJUST MAP
		if (!bMeetingDataDownloaded) {
			if (!updateMeetingMap(viewFrame, model)) {
				return false;
			}
		}

		// CREATE EVENTS FOR ALL NODES AND TAGS ON THE MEETING MAP, INCLUDING MEETING MAP ITSELF
		vtEvents.addElement(new MeetingEvent(sMeetingID, false, MeetingEvent.NODE_ADDED_EVENT, view, (NodeSummary)view));
		addNodeView(view.getId(), view.getId(), "0"); //$NON-NLS-1$
		view.initialize(model.getSession(), model);
		try {
			NodePosition pos = null;
			NodeSummary node = null;
			Code code = null;
			NodePosition innerpos = null;
			NodeSummary innernode = null;
			Code innercode = null;

			view.initializeMembers();
			for (Enumeration e = view.getPositions(); e.hasMoreElements();) {
				pos = (NodePosition)e.nextElement();
				node = pos.getNode();
				vtEvents.addElement(new MeetingEvent(sMeetingID, false, MeetingEvent.NODE_ADDED_EVENT, view, node));
				addNodeView(node.getId(), view.getId(), "0"); //$NON-NLS-1$

				for (Enumeration codes = node.getCodes(); codes.hasMoreElements();) {
					code = (Code)codes.nextElement();
					vtEvents.addElement(new MeetingEvent(sMeetingID, false, MeetingEvent.TAG_ADDED_EVENT, view, node, code));
				}

				// CHECK FOR ATTENDEE MAP AND CREATE EVENTS FOR ITS ATTENDEES
				if (node instanceof View) {
					if (node.getLabel().startsWith(ATTENDEE_LABEL)) {
						View attendeeView = (View)node;
						attendeeView.initialize(model.getSession(), model);
						attendeeView.initializeMembers();

						for (Enumeration inner = attendeeView.getPositions(); inner.hasMoreElements();) {
							innerpos = (NodePosition)inner.nextElement();
							innernode = innerpos.getNode();
							vtEvents.addElement(new MeetingEvent(sMeetingID, false, MeetingEvent.NODE_ADDED_EVENT, attendeeView, innernode));
							addNodeView(innernode.getId(), attendeeView.getId(), "0"); //$NON-NLS-1$

							for (Enumeration innercodes = innernode.getCodes(); innercodes.hasMoreElements();) {
								innercode = (Code)innercodes.nextElement();
								vtEvents.addElement(new MeetingEvent(sMeetingID, false, MeetingEvent.TAG_ADDED_EVENT, attendeeView, innernode, innercode));
							}
						}
					}
				}
			}
		} catch(Exception ex) {
			ProjectCompendium.APP.displayError(LanguageProperties.getString(LanguageProperties.MEETING_BUNDLE, "MeetingManager.errorLoadingMap")+":\n\n"+ex.getLocalizedMessage()); //$NON-NLS-1$
		}

		setCaptureEvents(true);
		bIsRecording = true;

		ProjectCompendium.APP.getToolBarManager().setMeetingToolBarEnabled(true);
		ProjectCompendium.APP.getStatusBar().setBackgroundColor(Color.red);
		ProjectCompendium.APP.getStatusBar().setForegroundColor(Color.white);

		sTitle = ProjectCompendium.APP.getTitle();
		sExtraTitle = RECORDER_TITLE;
		ProjectCompendium.APP.setTitle(sTitle + sExtraTitle);

        Thread t = new Thread() {
            public void run() {
		        ProjectCompendium.APP.displayMessage(LanguageProperties.getString(LanguageProperties.MEETING_BUNDLE, "MeetingManager.recordingStarted"), LanguageProperties.getString(LanguageProperties.MEETING_BUNDLE, "MeetingManager.meetings")); //$NON-NLS-1$ //$NON-NLS-2$
                viewFrame.setCursor(new Cursor(java.awt.Cursor.DEFAULT_CURSOR));
                ProjectCompendium.APP.setDefaultCursor();
            }
        };
        t.start();

		return true;
	}

	/**
	 * Query Arena for the Arena clock time and store in triplestore.
	 * Record current time for use in calculating Compendium event offsets
	 */
	private boolean calculateOffset() {

		ArenaConnection arena = new ArenaConnection();
		long nArenaOffset = arena.getArenaOffset(oConnectionData);
		if (nArenaOffset == -1) {
			return false;
		}

		nOffsetTime = nArenaOffset;

		return true;
	}

	/**
	 * Check if the meeting map is up-to-date with latest meeting data downloaded.
	 *
	 * @param UIViewFrame oViewFrame the frame for the Meeting Map.
	 * @param IModel oModel the current model.
	 */
	public boolean updateMeetingMap(UIViewFrame oViewFrame, IModel oModel) {

		UIMapViewFrame map = (UIMapViewFrame) oViewFrame;
		UIViewPane oUIViewPane = map.getViewPane();
		ViewPaneUI oViewPaneUI = oUIViewPane.getUI();
		View oView = oViewFrame.getView();
		String sViewID = oView.getId();

		PCSession oSession = oModel.getSession();
		String sAuthor = ProjectCompendium.APP.getModel().getUserProfile().getUserName();

		Meeting oldMeeting = oMeeting;
		try {
			if (downloadMeetingData(sMeetingID)) {

				//as the above method has overwritten the meeting object.
				//need to reset this data.
				oMeeting.setMapNode(oldMeeting.getMapNode());
				oMeeting.setMeetingMapID(oldMeeting.getMeetingMapID());

				//CHECK MEETING TITLE
				String sViewTitle = oMeeting.getName();
				if (!oView.getLabel().equals(sViewTitle)) {
					//update in the Meeting table only as user may have edited node label?
					//oView.setLabel(sViewTitle);

					oModel.getMeetingService().setMeetingName(oSession, sMeetingID, sViewTitle);
				}

				//CHECK MEETING START DATE
				Date dStartDate = oMeeting.getStartDate();
				oModel.getMeetingService().setMeetingDate(oSession, sMeetingID, dStartDate);

				// CHECK IF REQUIRED CODES EXIST IN DATABASE, ELSE CREATE
				Code oAgendaCode = CoreUtilities.checkCreateCode(AGENDA_CODE_TEXT, oModel, oSession, sAuthor);
				Code oAttendeeCode = CoreUtilities.checkCreateCode(ATTENDEE_CODE_TEXT, oModel, oSession, sAuthor);
				Code oDocumentCode = CoreUtilities.checkCreateCode(DOCUMENT_CODE_TEXT, oModel, oSession, sAuthor);

				Vector vtAttendeeNodes = new Vector();
				Vector vtAgendaNodes = new Vector();
				Vector vtDocumentNodes = new Vector();

				Vector vtAllAttendeeNodes = new Vector();
				Vector vtAllAgendaNodes = new Vector();
				Vector vtAllDocumentNodes = new Vector();

				NodePosition oAttendeeQuestionNode = null;
				NodePosition oAttendeeMapNode = null;
				NodePosition oAgendaQuestionNode = null;
				NodePosition oDocumentQuestionNode = null;

				// for repositioning nodes so they line-up
				double nWidestAttendeeNode = 0;
				double nWidestAgendaNode = 0;
				double nWidestDocumentNode = 0;

				double nAttendeeX = 0;
				double nAgendaX = 0;
				double nDocumentX = 0;

				//// CHECK MEETING ITEMS ////
				Vector triplestoreNodes = oModel.getQueryService().searchForTripleStoreNodes(oSession, sViewID);

				// Initialise the triplestore nodes as they may be used later and filter into sets by code.
				// Get the widest node fo aligning new nodes later.
				int count = triplestoreNodes.size();
				NodePosition oNodePos = null;
				NodeSummary oNode = null;
				double width = 0;
				for (int l=0; l<count; l++) {
					oNodePos = (NodePosition)triplestoreNodes.elementAt(l);
					oNode = oNodePos.getNode();
					oNode.initialize(oSession, oModel);
					UINode uinode = (UINode)oUIViewPane.get(oNode.getId());

					Dimension dim = new Dimension(0,0);
					width = 0;
					if (uinode != null) {
						dim = uinode.getPreferredSize();
					}
					width = dim.getWidth();

					if (oNode.hasCode(AGENDA_CODE_TEXT)) {
						vtAgendaNodes.addElement(uinode);
						vtAllAgendaNodes.addElement(uinode);
						if (width > nWidestAgendaNode) {
							nWidestAgendaNode = width;
							nAgendaX = uinode.getX();
						}
					} else if (oNode.hasCode(DOCUMENT_CODE_TEXT)) {
						vtDocumentNodes.addElement(uinode);
						vtAllDocumentNodes.addElement(uinode);
						if (width > nWidestDocumentNode) {
							nWidestDocumentNode = width;
							nDocumentX = uinode.getX();
						}
					}
					// Attendee info got out below, as it is in a sub-map.
				}

				int nOriginalAttendeeCount = 0;
				int nOriginalAgendaCount = vtAgendaNodes.size();
				int nOriginalDocCount = vtDocumentNodes.size();

				int nAttendeeCount = 0;
				int nAgendaCount = vtAgendaNodes.size();
				int nDocCount = vtDocumentNodes.size();

			//// CHECK ATTENDEEs
				Vector searchResults = searchNodeLabelInView(ATTENDEE_LABEL, ICoreConstants.MAPVIEW, sViewID);
				if (searchResults.size() > 0) {
					oAttendeeMapNode = (NodePosition)searchResults.elementAt(0);
					oAttendeeMapNode.initialize(oSession, oModel);
					// load contents else fails to find nodes to re-arrangeing positions later.
					try {
						((View)oAttendeeMapNode.getNode()).initializeMembers();
					} catch(Exception exc){}

					Vector searchResults2 = searchExactNodeLabelInView(ATTENDEE_LABEL, ICoreConstants.ISSUE, oAttendeeMapNode.getNode().getId());
					if (searchResults2.size() > 0) {
						oAttendeeQuestionNode = (NodePosition)searchResults2.elementAt(0);

						// Get inner attendee nodes
						Vector innerNodes = oModel.getQueryService().searchForTripleStoreNodes(oSession, oAttendeeMapNode.getNode().getId());
						int innercount = innerNodes.size();
						NodeSummary oNode2 = null;
						for (int l=0; l<innercount; l++) {
							oNodePos = (NodePosition)innerNodes.elementAt(l);
							oNode2 = oNodePos.getNode();
							oNode2.initialize(oSession, oModel);
							if (oNode2.hasCode(ATTENDEE_CODE_TEXT)) {
								UINode uinode = new UINode(oNodePos, sAuthor);
								Dimension dim = uinode.getPreferredSize();
								width = dim.getWidth();
								vtAttendeeNodes.addElement(uinode);
								vtAllAttendeeNodes.addElement(uinode);
								if (width > nWidestDocumentNode) {
									nAttendeeX = ((Point)oNodePos.getPos()).getX();
									nWidestDocumentNode = width;
								}
							}
						}
						nAttendeeCount = vtAttendeeNodes.size();
						nOriginalAttendeeCount = vtAttendeeNodes.size();

						// Loop new attendees and check
						Vector attendees = oMeeting.getAttendees();
						int counti = attendees.size();
						String id = ""; //$NON-NLS-1$
						String sOriginalID = ""; //$NON-NLS-1$
						boolean bFound = false;

						for (int i=0; i<counti; i++) {
							bFound = false;
							MeetingAttendee oAttendee = (MeetingAttendee)attendees.elementAt(i);
							id = ORIGINALID_TAG+oAttendee.getOriginalID();

							if (vtAttendeeNodes.size() > 0) {
								for (int t=0; t < nAttendeeCount; t++) {
									UINode node = (UINode) vtAttendeeNodes.elementAt(t);
									sOriginalID = node.getNode().getOriginalID();
									if ( sOriginalID.equals(id)) {
										bFound = true;
										vtAttendeeNodes.removeElement(node);
										nAttendeeCount = vtAttendeeNodes.size();
										t--;
										break;
									}
								}
							}

							if (!bFound) {
								NodePosition nodeposition = createNewAttendeeNode((View)oAttendeeMapNode.getNode(), oAttendee, oAttendeeCode, sAuthor, oAttendeeQuestionNode, Y_SPACER*nAttendeeCount, oModel);

								// ADJUST x position to line up with existing nodes.
								Dimension dim = (new UINode(nodeposition, sAuthor)).getPreferredSize();
								Point oOldPosition = nodeposition.getPos();
								double nNewX = oOldPosition.x;
								if (dim.width > nWidestAttendeeNode) {
									nNewX = nAttendeeX - ((dim.width-nWidestAttendeeNode)/2);
								} else {
									nNewX = nAttendeeX + ((nWidestAttendeeNode-dim.width)/2);
								}

								Point oNewPosition = new Point( (new Double(nNewX)).intValue(), oOldPosition.y );
								try {
									nodeposition.setPos(oNewPosition);
									oAttendeeMapNode.getView().setNodePosition(nodeposition.getNode().getId(), oNewPosition);
								} catch(Exception ex) {
									System.out.println("Unable to adjust meeting node x position due to: "+ex.getMessage()); //$NON-NLS-1$
								}

								UINode uinode = new UINode(nodeposition, sAuthor);
								uinode.getNode().initialize(oSession, oModel);
								vtAllAttendeeNodes.addElement(uinode);
								nAttendeeCount++;
							}
						}
					}
				} else {
					// NO ATTENDEES ADDED BEFORE SO ADD NODE AS IF NEW.
				}

			//// CHECK AGENDA

				searchResults = searchExactNodeLabelInView(AGENDA_LABEL, ICoreConstants.ISSUE, sViewID);
				if (searchResults.size() > 0) {
					oAgendaQuestionNode = (NodePosition)searchResults.elementAt(0);
					oAgendaQuestionNode.initialize(oSession, oModel);
					Vector items = oMeeting.getAgenda();
					String id = ""; //$NON-NLS-1$
					String sOriginalID =""; //$NON-NLS-1$
					String sOldText = ""; //$NON-NLS-1$
					boolean bFound = false;

					int countj = items.size();
					for (int j=0; j<countj; j++) {
						bFound = false;
						MeetingAgendaItem oItem = (MeetingAgendaItem)items.elementAt(j);
						id = ORIGINALID_TAG+oItem.getOriginalID();

						if (vtAgendaNodes.size() > 0) {
							for (int t=0; t<nAgendaCount; t++) {
								UINode node = (UINode) vtAgendaNodes.elementAt(t);
								sOriginalID = node.getNode().getOriginalID();
								if ( sOriginalID.equals(id)) {
									bFound = true;
									sOldText = node.getText();
									node.setText(oItem.getDisplayName()); // IN CASE MODIFIED
									//update label of inner question too.

									View innerview = (View)node.getNode();
									for (Enumeration innernodes = innerview.getPositions(); innernodes.hasMoreElements();) {
										NodePosition pos = (NodePosition)innernodes.nextElement();
										NodeSummary sum = pos.getNode();
										if (sum.getLabel().equals(sOldText) && sum.getType() == ICoreConstants.ISSUE) {
											sum.setLabel(oItem.getDisplayName(), sAuthor);
											break;
										}
									}
									vtAgendaNodes.removeElement(node);
									nAgendaCount = vtAgendaNodes.size();
									t--;
									break;
								}
							}
						}

						if (!bFound) {
							UINode uinode = createNewAgendaNode(oView, oViewPaneUI, oItem, oAgendaCode, sAuthor, oAgendaQuestionNode, Y_SPACER*nAgendaCount, oModel);
							uinode.getNode().initialize(oSession, oModel);

							// ADJUST x position to line up with existing nodes.
							Dimension dim = uinode.getPreferredSize();
							Point oOldPosition = uinode.getNodePosition().getPos();
							double nNewX = oOldPosition.x;
							if (dim.width > nWidestAgendaNode) {
								nNewX = nAgendaX - ((dim.width-nWidestAgendaNode)/2);
							} else {
								nNewX = nAgendaX + ((nWidestAgendaNode-dim.width)/2);
							}

							Point oNewPosition = new Point( (new Double(nNewX)).intValue(), oOldPosition.y );
							try {
								uinode.getNodePosition().setPos(oNewPosition);
								oView.setNodePosition(uinode.getNode().getId(), oNewPosition);
							} catch(Exception ex) {
								System.out.println("Unable to adjust meeting node x position due to: "+ex.getMessage()); //$NON-NLS-1$
							}

							vtAllAgendaNodes.insertElementAt(uinode, j);
							nAgendaCount++;
						}
					}
				} else {
					// NO AGENDA ITEMS ADDED BEFORE SO ADD NODE AS IF NEW.

				}

			//// CHECK DOCS
				searchResults = searchExactNodeLabelInView(DOCUMENT_LABEL, ICoreConstants.ISSUE, sViewID);

				if (searchResults.size() > 0) {
					oDocumentQuestionNode = (NodePosition)searchResults.elementAt(0);
					oDocumentQuestionNode.initialize(oSession, oModel);

					Vector docs = oMeeting.getDocuments();
					int countk = docs.size();
					boolean bFound = false;
					String id = ""; //$NON-NLS-1$
					String sOriginalID=""; //$NON-NLS-1$
					for (int k=0; k<countk; k++) {
						bFound = false;

						MeetingDocument oDoc = (MeetingDocument)docs.elementAt(k);
						id = ORIGINALID_TAG+oDoc.getOriginalID();

						if (vtDocumentNodes.size() > 0) {
							for (int t=0; t<nDocCount; t++) {
								UINode node = (UINode) vtDocumentNodes.elementAt(t);
								sOriginalID = node.getNode().getOriginalID();
								if ( sOriginalID.equals(id)) {
									bFound = true;
									node.setText(oDoc.getName()); // IN CASE MODIFIED
									vtDocumentNodes.removeElement(node);
									nDocCount = vtDocumentNodes.size();
									t--;
									break;
								}
							}
						}
						if (!bFound) {
							UINode uinode = createNewDocumentNode(oView, oViewPaneUI, oDoc, oDocumentCode, sAuthor, oDocumentQuestionNode, Y_SPACER*nDocCount, oModel);
							uinode.getNode().initialize(oSession, oModel);

							// ADJUST x position to line up with existing nodes.
							Dimension dim = uinode.getPreferredSize();
							Point oOldPosition = uinode.getNodePosition().getPos();
							double nNewX = oOldPosition.x;
							if (dim.width > nWidestDocumentNode) {
								nNewX = nDocumentX - ((dim.width-nWidestDocumentNode)/2);
							} else {
								nNewX = nDocumentX + ((nWidestDocumentNode-dim.width)/2);
							}

							Point oNewPosition = new Point( (new Double(nNewX)).intValue(), oOldPosition.y );
							try {
								uinode.getNodePosition().setPos(oNewPosition);
								oView.setNodePosition(uinode.getNode().getId(), oNewPosition);
							} catch(Exception ex) {
								System.out.println("Unable to adjust meeting node x position due to: "+ex.getMessage()); //$NON-NLS-1$
							}

							vtAllDocumentNodes.addElement(uinode);
							nDocCount++;
						}
					}
				} else {
					// NO DOCUMENTS ADDED BEFORE SO ADD NODE AS IF NEW.
				}

				// DEAL WITH ANY NODES THAT ARE ON THE MEETING MAP BUT HAVE BEEN DELETED FROM THE LASTED MEETING DATA
				// If they have any new nodes hanging off or are maps with child nodes, noved them to a 'Notes' map.
				// else, delete them from the map.

				if (nAttendeeCount > 0 || nAgendaCount>0 || nDocCount>0) {
					NodePosition oNotesNode = null;

					// Create 'Notes' Map, if not already there.
					searchResults = searchExactNodeLabelInView(NOTES_LABEL, ICoreConstants.ISSUE, sViewID);
					if (searchResults.size() == 0) {
						oNotesNode = oView.addMemberNode( ICoreConstants.MAPVIEW, "", "", sAuthor, NOTES_LABEL, "", 130, 10); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
						createMediaIndex(sViewID, oNotesNode.getNode().getId(), 0);
					} else {
						oNotesNode = (NodePosition)searchResults.elementAt(0);
					}

					if (oNotesNode != null) {
						View oNotesView = (View)oNotesNode.getNode();
						int nYPosition = 20;
						if (nAttendeeCount > 0) {
							nYPosition = adjustDeletedNodes(vtAttendeeNodes, oNotesView, oAttendeeQuestionNode, oModel, oSession, nYPosition);
						}
						if (nAgendaCount > 0) {
							nYPosition = adjustDeletedNodes(vtAgendaNodes, oNotesView, oAgendaQuestionNode, oModel, oSession, nYPosition);
						}
						if (nDocCount > 0) {
							nYPosition = adjustDeletedNodes(vtDocumentNodes, oNotesView, oDocumentQuestionNode, oModel, oSession, nYPosition);
						}

						//if no nodes where added to the Notes Map, delete it.
						if ( ((View)oNotesNode.getNode()).getNumberOfNodes() <= 0) {
							oView.removeMemberNode(oNotesNode.getNode());
						} else {
							// ARRANGE Notes view
							UIViewFrame frame = ProjectCompendium.APP.getViewFrame(oNotesView, oNotesView.getLabel());
							IUIArrange arrange = new UIArrangeLeftRight();
							arrange.arrangeView(oNotesView, frame);
						}
					}
				}
				// RE-POSITION THE NODES THAT ARE NOW THERE, ONLY IF ANYTHING HAS CHANGED.
				if (vtAllAttendeeNodes.size() != nOriginalAttendeeCount || nAttendeeCount > 0) {
					reorderItems(vtAllAttendeeNodes, 20, (View)oAttendeeMapNode.getNode(), oAttendeeQuestionNode);
				}

				if (vtAllAgendaNodes.size() != nOriginalAgendaCount || nAgendaCount>0 ||
						vtAllDocumentNodes.size() != nOriginalDocCount || nDocCount>0) {

					int nStartingY = reorderItems(vtAllAgendaNodes, 90, oView, oAgendaQuestionNode);
					nStartingY+=Y_SPACER;
					reorderItems(vtAllDocumentNodes, nStartingY, oView, oDocumentQuestionNode);

					// REFRESH MEETING MAP
					oView.setIsMembersInitialized(false);
					try {
						oView.initializeMembers();
					} catch(Exception io) {}

					((UIMapViewFrame)oViewFrame).createViewPane((View)oView);
				}
			}
		} catch(Exception ex) {
			ex.printStackTrace();
			ProjectCompendium.APP.displayError(LanguageProperties.getString(LanguageProperties.MEETING_BUNDLE, "MeetingManager.errorUpdatingMap")+":\b=n\n"+ex.getLocalizedMessage()); //$NON-NLS-1$
			return false;
		}

		return true;
	}

	/**
	 * Check each NodePosition in the list to see if it has child nodes or dependent nodes.
	 * If it does move it/them to the given view.
	 *
	 * @param vtNodes the nodes to check.
	 * @param oView the view to move them to if they have been used.
	 * @param oQuestionNode the question node they are all linked to.
	 * @param oModel the current model.
	 * @param oSession the current session.
	 * @param nYPosition the current y position in the deleted nodes map.
	 * @return the final y position.
	 */
	private int adjustDeletedNodes(Vector vtNodes, View oView, NodePosition oQuestionNode, IModel oModel, PCSession oSession, int nYPosition) {

		NodeSummary oFromNode = null;
		NodeSummary oToNode = null;
		UINode next = null;

		NodeSummary nextNode = null;
		Vector deleted = new Vector();

		View nextView = null;
		LinkProperties nextProps = null;
		Link nextLink = null;
		View innerView = null;
		Vector vtLinks = null;
		int nChildCount = 0;
		int nLinkCount = 0;
		int nCount = vtNodes.size();

		try {

			Vector vtRemovedElements = new Vector();

			for (int i=0; i<nCount; i++) {

				next = (UINode)vtNodes.elementAt(i);
				nextNode = next.getNode();
				nextView = next.getNodePosition().getView();
				if (nCount == 0) {
					nextView.initialize(oSession, oModel);
					nextView.initializeMembers();
				}

				// MOVE IT, IF IT HAS CHILD NODES
				if (nextNode instanceof View) {
					innerView = (View)nextNode;
					nChildCount = innerView.getNodeCount();
					if (nChildCount > 0) {
						oView.addNodeToView(nextNode, 20, nYPosition);
						nYPosition+=Y_SPACER;
					}
				}

				String sQuestionID = oQuestionNode.getNode().getId();

				// MOVE IT IF IT HAS ANY LINK, NOT TO ITS QUESTION NODE (new nodes).
				vtLinks = nextView.getLinksForNode(nextNode.getId());
				nLinkCount = vtLinks.size();

				for (int j=0; j< nLinkCount; j++) {

					nextProps = (LinkProperties)vtLinks.elementAt(j);
					nextLink = nextProps.getLink();
					oFromNode = nextLink.getFrom();
					oToNode = nextLink.getTo();

					if (!oFromNode.getId().equals(sQuestionID) && !oToNode.getId().equals(sQuestionID)) {

						// ADD
						if (!oView.containsNodeSummary(oFromNode)) {
							oView.addNodeToView(oFromNode, 20, nYPosition);
							nYPosition+=Y_SPACER;
						}
						if (!oView.containsNodeSummary(oToNode)) {
							oView.addNodeToView(oToNode, 20, nYPosition);
							nYPosition+=Y_SPACER;
						}
						if (!oView.containsLink(nextLink.getId())) {
							oView.addLinkToView(nextLink, nextProps);
							nYPosition+=Y_SPACER;
						}

						//REMOVE IF NOT A MEETING NODE THAT NEEDS TO STAY
						if (nextView.containsNodeSummary(oFromNode)) {
							vtRemovedElements.addElement(oFromNode);
						}
						if (nextView.containsNodeSummary(oToNode)) {
							vtRemovedElements.addElement(oFromNode);
						}
						if (nextView.containsLink(nextLink.getId())) {
							nextView.removeMemberLink(nextProps);
						}
					}

				} // links for loop


				// NOW DELETE THIS REDUNDANT NODE (HAVING SAFELY MOVED IT IF REQUIRED).
				if (nextView.containsNodeSummary(nextNode)) {
					nextView.removeMemberNode(nextNode);
				}

			} // main for loop

			// NOW REMOVED ANY ATTACHED NODES THAT HAVE BEEN MOVED TO THE NOTES MAP
			if (vtRemovedElements.size() >1 && nextView != null){
				NodeSummary sum = null;
				int countj = vtRemovedElements.size();
				for (int j=0; j<countj; j++) {
					sum = (NodeSummary)vtRemovedElements.elementAt(j);
					nextView.removeMemberNode(sum);
				}
			}
		} catch(Exception ex) {
			ex.printStackTrace();
			ProjectCompendium.APP.displayError(ex.getMessage());
		}

		return nYPosition;
	}

	/**
	 * Reorder Y position of Nodes in the given list after update.
	 * @param vtNodes the nodes whose y positions to reorder.
	 * @param nStartingY the starting position for reordering along the Y axis.
	 * @param oView the view the node is in.
	 * @param oQuestionNode the question node they are all linked to.
	 * @return int the final Y poisition.
	 */
	private int reorderItems(Vector vtNodes, int nStartingY, View oView, NodePosition oQuestionNode) {

		Point oNewPosition = null;
		int y = nStartingY;
		int nStartY = nStartingY;
		int nStopY = nStartingY;
		int nWidest = 0;
		int width = 0;

		int count = vtNodes.size();
		if (count > 0) {
			for (int i=0; i<count; i++) {
				UINode uinode = (UINode)vtNodes.elementAt(i);
				width = (uinode.getPreferredSize()).width;
				if (width > nWidest) {
					nWidest = width;
				}
				Point oOldPosition = uinode.getNodePosition().getPos();
				oNewPosition = new Point(X_OFFSET, y );
				try {
					uinode.getNodePosition().setPos(oNewPosition);
					oView.setNodePosition(uinode.getNode().getId(), oNewPosition);
					y += Y_SPACER;
				} catch(Exception ex) {
					System.out.println("unable to adjust node y position due to: "+ex.getMessage()); //$NON-NLS-1$
				}
			}
		}

		// Center question node.
		nStopY = y;
		int height = nStopY-nStartY;
		UINode  oUINode = new UINode(oQuestionNode, sAuthor);
		Dimension oDimension = oUINode.getPreferredSize();

		Point oNewPosition2 = new Point( 20, nStopY-( (height/2)+(oDimension.height/2)+7 ) ); // why + 7?, cause needs it.
		try {
			oUINode.getNodePosition().setPos(oNewPosition);
			oView.setNodePosition(oQuestionNode.getNode().getId(), oNewPosition2);
		} catch(Exception ex) {
			System.out.println("unable to adjust node y position due to: "+ex.getMessage()); //$NON-NLS-1$
		}

		recalcuateNodeXPositions(vtNodes, oView, nWidest);

		return y;
	}

	/**
	 * Create a new attendee node in the given view.
	 *
	 * @param oView the view to create the new nodes in.
	 * @param oAttendeeItem the object holding the new attendee data.
	 * @param oCode the code to add to the new node.
	 * @param sAuthor the author of this new node.
	 * @param oParentNodePos the node to link this new node too.
	 * @param oQuestionNode the question node they are all linked to.
	 * @param nY the y position to place the node at.
	 * @param oModel the current model
	 */
	private NodePosition createNewAttendeeNode(View oAttendeeView, MeetingAttendee oAttendeeItem, Code oCode, String sAuthor, NodePosition oParentNodePos, int nY, IModel oModel) throws Exception {

		NodePosition nodePos = null;
		String sName = oAttendeeItem.getName();
		String sViewID = oAttendeeView.getId();

		Vector searchResults = searchExactNodeLabel(sName, ICoreConstants.REFERENCE);
		if (searchResults.size() >0) {
			nodePos = oAttendeeView.addNodeToView((NodeSummary)searchResults.elementAt(0), X_OFFSET, nY);
		} else {
			nodePos = oAttendeeView.addMemberNode( ICoreConstants.REFERENCE, "", ORIGINALID_TAG+oAttendeeItem.getOriginalID(), sAuthor, sName, oAttendeeItem.getOriginalID(), X_OFFSET, nY); //$NON-NLS-1$
		}

		if (nodePos != null) {
			NodeSummary nodeSum = nodePos.getNode();
			nodeSum.initialize(oModel.getSession(), oModel);
			String email = oAttendeeItem.getEmail();
			if (!email.equals("")) { //$NON-NLS-1$
				nodeSum.setSource(email, "", sAuthor); //$NON-NLS-1$
			}

			if (oCode != null) {
				nodeSum.addCode(oCode);
			}
			createMediaIndex(sViewID, nodeSum.getId(), 0);
			LinkProperties props = UIUtilities.getLinkProperties(ICoreConstants.DEFAULT_LINK);
			props.setArrowType(ICoreConstants.ARROW_TO);			
			oAttendeeView.addMemberLink(ICoreConstants.DEFAULT_LINK, "0", sAuthor, nodeSum, oParentNodePos.getNode(), props); //$NON-NLS-1$
		}

		return nodePos;
	}

	/**
	 * Create a new document node in the given view.
	 *
	 * @param oView the current view.
	 * @param oViewPaneUI the frame of the meeting map to place the node in.
	 * @param oDocumentItem the object holding the new attendee data.
	 * @param oCode the code to add to the new node.
	 * @param sAuthor the author of this new node.
	 * @param oParentNodePos the node to link this new node too.
	 * @param nY the y position to place the node at.
	 * @param oModel the current model
	 */
	private UINode createNewDocumentNode(View oView, ViewPaneUI oViewPaneUI, MeetingDocument oDocumentItem, Code oCode, String sAuthor, NodePosition oParentNodePos, int nY, IModel oModel) throws Exception {

		String sName = oDocumentItem.getName();

		UINode uinode = oViewPaneUI.createNode(ICoreConstants.REFERENCE, ORIGINALID_TAG+oDocumentItem.getOriginalID(), sAuthor, sName, "", X_OFFSET, nY); //$NON-NLS-1$
		NodeSummary nodeSum = uinode.getNode();
		nodeSum.initialize(oModel.getSession(), oModel);

		createMediaIndex(oView.getId(), nodeSum.getId(), 0);

		String url = oDocumentItem.getURL();
		if (!url.equals("")) { //$NON-NLS-1$
			nodeSum.setSource(url, "", sAuthor); //$NON-NLS-1$
		}

		if (oCode != null) {
			nodeSum.addCode(oCode);
		}

		LinkProperties props = UIUtilities.getLinkProperties(ICoreConstants.DEFAULT_LINK);
		props.setArrowType(ICoreConstants.ARROW_TO);		
		oView.addMemberLink(ICoreConstants.DEFAULT_LINK, "0", sAuthor, nodeSum, oParentNodePos.getNode(), props); //$NON-NLS-1$

		return uinode;
	}

	/**
	 * Create a new agenda node in the given view.
	 *
	 * @param oView the current view.
	 * @param oViewPaneUI the frame of the meeting map to place the node in.
	 * @param oAgendaItem the object holding the new attendee data.
	 * @param oCode the code to add to the new node.
	 * @param sAuthor the author of this new node.
	 * @param oParentNodePos the node to link this new node too.
	 * @param nY the y position to place the node at.
	 * @param oModel the current model
	 */
	private UINode createNewAgendaNode(View oView, ViewPaneUI oViewPaneUI, MeetingAgendaItem oAgendaItem, Code oCode, String sAuthor, NodePosition oParentNodePos, int nY, IModel oModel) throws Exception {

		String sName = oAgendaItem.getDisplayName();

		UINode uinode = oViewPaneUI.createNode(ICoreConstants.MAPVIEW, ORIGINALID_TAG+oAgendaItem.getOriginalID(), sAuthor, sName, "", X_OFFSET, nY); //$NON-NLS-1$
		NodeSummary nodeSum = uinode.getNode();
		nodeSum.initialize(oModel.getSession(), oModel);

		createMediaIndex(oView.getId(), nodeSum.getId(), 0);

		if (oCode != null) {
			nodeSum.addCode(oCode);
		}
		LinkProperties props = UIUtilities.getLinkProperties(ICoreConstants.DEFAULT_LINK);
		props.setArrowType(ICoreConstants.ARROW_TO);
		oView.addMemberLink(ICoreConstants.DEFAULT_LINK, "0", sAuthor, nodeSum, oParentNodePos.getNode(), props); //$NON-NLS-1$

		// add inner question with same name as agenda item
		View newView = (View)uinode.getNode();
		if (newView != null) {
			newView.initialize(oModel.getSession(), oModel);
			newView.addMemberNode( ICoreConstants.ISSUE, "", "" ,sAuthor, sName, "", 10, 250); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}

		return uinode;
	}

	/**
	 * Stop Recording events for a meeting and upload data.
	 */
	 public boolean stopRecording() {

		if (!bIsRecording) {
			return true;			
		}
		 
		setCaptureEvents(false);
		bIsRecording = false;

		ProjectCompendium.APP.getToolBarManager().setMeetingToolBarEnabled(false);
		ProjectCompendium.APP.setTitle(sTitle);
		ProjectCompendium.APP.getStatusBar().resetColors();
		sTitle=""; //$NON-NLS-1$

		try {
			// SET MEETING STATUS AS RECORDED
			IModel model = ProjectCompendium.APP.getModel();
			model.getMeetingService().setMeetingStatus(model.getSession(), sMeetingID, ICoreConstants.STATUS_RECORDED);
		} catch(Exception ex) {
			ProjectCompendium.APP.displayError(LanguageProperties.getString(LanguageProperties.MEETING_BUNDLE, "MeetingManager.errorSettingStatus")+"\n\n"+ex.getLocalizedMessage()); //$NON-NLS-1$
			return false;
		}

        final MeetingManager manager = this;
        Thread t = new Thread() {
            public void run() {
        		// ASK USER WHAT TO DO WITH DATA, IF THERE IS ANY.
        		if (getEventCount() > 0) {
        			UIMeetingUploadChoiceDialog dlg = new UIMeetingUploadChoiceDialog(manager);
                    dlg.onUpload();
        		}
            }
        };
        t.start();

		return true;
	 }

	/**
	 * Upload meeting events from a saved file.
	 */
	 public void uploadRecording() {
		UIMeetingUploadDialog dlg = new UIMeetingUploadDialog(this);
		dlg.setVisible(true);
	 }

	/**
	 * Upload file of n3 data with the given name to the triplestore.
	 * And the zip with the xml and references to the appropriate place.
	 * If upload appears successful, tag the files as uploaded by renaming file with '.uploaded' at the end.
	 * If renaming the files is not successful, delete the n3 file.
	 * We do not want to upload the same file twice as this will over-populate
	 * the elements of triplestore data for this meeting.
	 *
	 * @param sFilePath the file of data to upload..
	 * @param sMeetingID the id of the meeting to upload all files for.
	 * @throws Exception if something goes wrong with either upload.
	 */
	public void uploadAllFiles(String sFilePath, String sMeetingID) throws Exception {
		uploadEventFile(sFilePath, sMeetingID);
		ArenaConnection arena = new ArenaConnection();
		arena.uploadXMLFile(oConnectionData, sSessionID, sFilePath, false);
	}

	/**
	 * Upload file of n3 data with the given name to the triplestore.
	 * If upload appears successful, tag the file as uploaded by renameing file with '.uploaded' at the end.
	 * If renaming the file is not successful. Delete the file.
	 * We do not want to upload the same file twice as this will over-populate
	 * the elements of triplestore data for this meeting.
	 *
	 * @param sFilePath the file of data to upload.
	 * @param sMeetingID the id of the meeting to upload the event file for.
	 * @throws Exception if something goes wrong with the upload.
	 */
	public void uploadEventFile(String sFilePath, String sMeeting) throws Exception {
		if (!sFilePath.equals("")) { //$NON-NLS-1$
			TripleStoreConnection connection = new TripleStoreConnection(oConnectionData);
			connection.uploadFile(sFilePath, sMeeting);

			File oSourceFile = new File(sFilePath);
			File oDestinationFile = new File(sFilePath+".uploaded"); //$NON-NLS-1$

			boolean bSuccessful = oSourceFile.renameTo(oDestinationFile);

			// IF YOU CAN'T RENAME TRY AND COPY IT. EITHER WAY DELETE ORIGINAL AS n3 MUST NOT BE UPLOADED TWICE
			if (!bSuccessful) {
				try {
					CoreUtilities.copyFile(oSourceFile, oDestinationFile);
				} catch (IOException io) {io.printStackTrace();}
				CoreUtilities.deleteFile(oSourceFile);
			}
		}
	}

	/**
	 * Create a media index record for the given view and node id in the current meeting
	 * with the given index
	 *
	 * @param sViewID the view the node is in.
	 * @param sNodeID the node to set the medai index for
	 * @param index the media index to set.
	 */
	private MediaIndex createMediaIndex(String sViewID, String sNodeID, long index) {

		MediaIndex oMediaIndex = null;
		try {
			Date dIndex = new Date(index);
			oMediaIndex = new MediaIndex(sViewID, sNodeID, sMeetingID, dIndex, new Date(), new Date());
			IModel model  = ProjectCompendium.APP.getModel();
			model.getMeetingService().createMediaIndex(model.getSession(), oMediaIndex);
			oMediaIndex.initialize(model.getSession(), model);
		} catch(Exception ex) {
			ex.printStackTrace();
			ProjectCompendium.APP.displayError(LanguageProperties.getString(LanguageProperties.MEETING_BUNDLE, "MeetingManager.errorWritingRecord")+":\n\n"+ex.getLocalizedMessage()); //$NON-NLS-1$
		}

		return oMediaIndex;
	}

	/**
	 * Calculate and return the mediaIndex offset for the current time.
	 * Save the data to the database and store the MediaIndex in the relevant NodePosition object.
	 */
	private long getMediaIndex(MeetingEvent oEvent) {

		long offset = 0;

		if (nMeetingType == MeetingManager.RECORDING) {
			long currentTime = (new Date()).getTime();
			offset = new Long((currentTime + nOffsetTime)).longValue();
			if (offset < 0)
				offset = 0;
		} else {
			offset = new Double(nCurrentOffsetTime).longValue();
		}

		// WRITE TO DATABASE IF NODE ADDED TO VIEW  EVENT
		if (oEvent.getEventType() == MeetingEvent.NODE_ADDED_EVENT
					|| oEvent.getEventType() == MeetingEvent.NODE_TRANSCLUDED_EVENT) {

			MediaIndex oMediaIndex = createMediaIndex(oEvent.getViewID(), oEvent.getNodeID(), offset);

			//ADD TO NODEPOSITION OBJECT
			NodePosition pos = oEvent.getView().getNodePosition(oEvent.getNodeID());
			if (pos != null) {
				pos.setMediaIndex(sMeetingID, oMediaIndex);
			} else {
				System.out.println("NodePosition object was null for "+oEvent.getNode().getLabel()); //$NON-NLS-1$
			}
		}

		return offset;
	}

	/**
	 * Add the given event to store.
	 *
	 * @param oEvent the event to add.
	 */
	public synchronized void addEvent(MeetingEvent oEvent) {

		if (isPaused) {
			return;
		}

		// Record the media index offset for this event.
		oEvent.setMediaIndex(getMediaIndex(oEvent));

		// Make sure there is a node added events for nodes generating any other sort of event.
		// This covers, if events happen on nodes created before the meeting, or inside maps
		// transcluded in during the meeting. Treated as transcluded events.
		int type = oEvent.getEventType();
		Code code = null;
		Code eventCode = oEvent.getCode();
		String sCodeID = ""; //$NON-NLS-1$
		if (eventCode != null)
			eventCode.getId();

		NodeSummary node = oEvent.getNode();
		View view = oEvent.getView();
		String sIndex = String.valueOf(oEvent.getMediaIndex());

		if (type != MeetingEvent.NODE_ADDED_EVENT && type != MeetingEvent.NODE_TRANSCLUDED_EVENT) {

			// NEEDS A DIFFERENT CHECK AS NODE AND VIEW IDS ARE THE SAME AND WILL ALWAYS FAIL STANDARD CHECK.
			// IF WE KNOW ABOUT THE NODE, STORE THE EVENT, ELSE IGNORE IT
			// THEY COULD BE OPENING A VIEW FROM A VIEWS DIALOG/PANEL ETC, SO IT IS NOT PART OF THE MEETING MAP.
			if (type == MeetingEvent.VIEW_SELECTED_EVENT) {
				if (!htNodesAdded.containsKey(node.getId())) {
					return;
				}
			} else if (!hasNodeView(node.getId(), view.getId())) {

				MeetingEvent oNodeEvent = new MeetingEvent(sMeetingID, false, MeetingEvent.NODE_TRANSCLUDED_EVENT, view, node);
				vtEvents.addElement(oNodeEvent);
				addNodeView(node.getId(), view.getId(), sIndex);

				// ADD THIER EXISTING TAGS?
				for (Enumeration codes = node.getCodes(); codes.hasMoreElements();) {
					code = (Code)codes.nextElement();

					boolean bInsertCode = true;
					if (type == MeetingEvent.TAG_ADDED_EVENT && !sCodeID.equals("")) { //$NON-NLS-1$
						if (code.getId().equals(sCodeID)) {
							bInsertCode = false;
						}
					}
					if (bInsertCode) {
						vtEvents.addElement(new MeetingEvent(sMeetingID, false, MeetingEvent.TAG_ADDED_EVENT, oEvent.getView(), node, code));
					}
				}
			}
			vtEvents.addElement(oEvent);

		} else if (!hasNodeView(node.getId(), view.getId(), sIndex)) {
			addNodeView(node.getId(), view.getId(), sIndex);
			vtEvents.addElement(oEvent);
		}
	}

	/**
	 * Upload all the meeting events currently logged to the triplstore.
	 * @throws Exception
	 */
	public void uploadMeetingEvents() throws Exception {

		try {
			int count = vtEvents.size();

			//fireProgressCount(count+1);

			TripleStoreConnection connection = new TripleStoreConnection(oConnectionData);

			com.hp.hpl.jena.rdf.model.Model model = com.hp.hpl.jena.rdf.model.ModelFactory.createDefaultModel();

			if (nMeetingType == RECORDING) {
				connection.addMeetingData(oMeeting, model);
			}

			MeetingEvent oEvent = null;
			for (int i=0; i<count; i++) {
				oEvent = (MeetingEvent)vtEvents.elementAt(i);
				//fireProgressUpdate(increment, "Creating data for triplestore...");
				connection.addEvent(oEvent, model);
			}

			//fireProgressUpdate(increment, "Uploading data to triplestore...");

			connection.uploadModel(model, sMeetingID);
	   		model.close();
			//fireProgressComplete();
		} catch(AccessGridDataException agde) {
			ProjectCompendium.APP.displayError(agde.getMessage());
			//fireProgressComplete();
		} catch(Exception ex) {
			//fireProgressComplete();
			throw ex;
		}
	}

	/**
	 * Save the meeting data to a file. And Save the map data to a zip.
	 * Then upload both n3 data and zip file.
	 * @throws Exception if something goes wrong with file create or upload
	 */
	public void saveAndUploadMeetingData() throws Exception {

		String sDate = CoreCalendar.getCurrentDateStringFullForFile();
		String sEventsFile = saveMeetingEventsToFile(sDate);
		fireProgressUpdate(increment, LanguageProperties.getString(LanguageProperties.MEETING_BUNDLE, "MeetingManager.progressMessage1")); //$NON-NLS-1$
		uploadEventFile(sEventsFile, sMeetingID);
		fireProgressComplete();

		String sXMLFile = saveMapDataToFile(sDate); // OWN PROGRESS BAR
		(new ArenaConnection()).uploadXMLFile(oConnectionData, sSessionID, sXMLFile, true);
        //IModel model = ProjectCompendium.APP.getModel();
        //(model.getMeetingService()).deleteMeeting(model.getSession(), oMeeting.getMeetingID());
        //(model.getMeetingService()).createMeeting(model.getSession(), sMeetingID + "_" + getXMLExportFileName(sDate), sMeetingMapID, oMeeting.getName(), oMeeting.getStartDate(), ICoreConstants.STATUS_PREPARED);
	}

	/**
	 * Save all the current meeting event data to an rdf file.
	 * @param sDate, the date string to used as part of the filename.
	 * @return the file path saved to.
	 * @throws Exception if something goes wrong.
	 */
	public String saveMeetingEventsToFile(String sDate) throws Exception {

		String sFilePath = ""; //$NON-NLS-1$
		sFilePath = sDirectory+ProjectCompendium.sFS;

		String sDatabaseName = ProjectCompendium.APP.getModel().getModelName();
		File directory = new File(sFilePath+sDatabaseName);
		if (!directory.isDirectory()) {
			directory.mkdirs();
		}

		sFilePath += sDatabaseName+ProjectCompendium.sFS;

		Date date = new Date();
		if (isReplay()) {
			sFilePath += sMeetingMapID+"_Replay_"+sDate+".n3"; //$NON-NLS-1$ //$NON-NLS-2$
		} else {
			sFilePath += sMeetingMapID+"_Record_"+sDate+".n3"; //$NON-NLS-1$ //$NON-NLS-2$
		}

		int count = vtEvents.size();

		fireProgressCount(count+3);

		TripleStoreConnection connection = new TripleStoreConnection(oConnectionData);
		com.hp.hpl.jena.rdf.model.Model model = com.hp.hpl.jena.rdf.model.ModelFactory.createDefaultModel();

		if (nMeetingType == RECORDING) {
			connection.addMeetingData(oMeeting, model);
		}

		MeetingEvent oEvent = null;
		for (int i=0; i<count; i++) {
			oEvent = (MeetingEvent)vtEvents.elementAt(i);
			fireProgressUpdate(increment, LanguageProperties.getString(LanguageProperties.MEETING_BUNDLE, "MeetingManager.progressMessage2")); //$NON-NLS-1$
			connection.addEvent(oEvent, model);
		}

		fireProgressUpdate(increment, LanguageProperties.getString(LanguageProperties.MEETING_BUNDLE, "MeetingManager.progressMessage3")); //$NON-NLS-1$

		connection.writeFile(model, sFilePath);

		return sFilePath;
	}

    private String getXMLExportFileName(String sDate) {
        String sFilePath = ""; //$NON-NLS-1$
        if (isReplay()) {
            sFilePath += sMeetingMapID+"_Replay_"+sDate+".zip"; //$NON-NLS-1$ //$NON-NLS-2$
        } else {
            sFilePath += sMeetingMapID+"_Record_"+sDate+".zip"; //$NON-NLS-1$ //$NON-NLS-2$
        }
        return sFilePath.toLowerCase();
    }

	/**
	 * Try and save the map data to XML and into a zip file.
	 * @param sDate, the date string to used as part of the filename.
	 * @return the file path saved to.
	 * @throws Exception if anything goes wrong.
	 */
	public String saveMapDataToFile(String sDate) throws Exception {
        String sFilePath = sDirectory+ProjectCompendium.sFS;

        String sDatabaseName = ProjectCompendium.APP.getModel().getModelName();
        File directory = new File(sFilePath+sDatabaseName);
        if (!directory.isDirectory()) {
            directory.mkdirs();
        }

        sFilePath += sDatabaseName+ProjectCompendium.sFS;
		sFilePath += getXMLExportFileName(sDate);

		if (!sFilePath.equals("")) { //$NON-NLS-1$
			String sViewID = oMeeting.getMeetingMapID();
			View oHome = ProjectCompendium.APP.getHomeView();
            UIViewFrame oHomeFrame = ProjectCompendium.APP.getViewFrame(oHome, oHome.getLabel());
            NodeSummary oHomeNode = oHome.getNode(sViewID);
            UIUtilities.focusNodeAndScroll(oHomeNode, oHomeFrame);

			XMLExport export = new XMLExport(oHomeFrame, sFilePath, true, true, true, true, false, true, false);
			export.start();

			while (!export.hasFailed()) {
				if (export.exportCompleted()) {
					break;
				}
			}
		}

		return sFilePath;
	}

//********************** MAP CREATION METHODS **************************//

	/**
	 * Create a new map for the meeting and return if successful.
	 *
	 * @exception SQLException if problem writing meeting record to database.
	 */
	public boolean createMeetingMap() throws Exception {

		if (oMeeting != null) {

			UIViewFrame viewFrame = ProjectCompendium.APP.getCurrentFrame();
			String sAuthor = ProjectCompendium.APP.getModel().getUserProfile().getAuthor();
			NodeSummary node = createMeetingMap(viewFrame, sAuthor);
			if (node != null) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Create a new map node for the meeting and place in it any related nodes
	 * for attendees, agenda, related documents etc.
	 * @param oViewFrame, the view to create the new map in.
	 * @param sAuthor, the author for the map.
	 * @exception SQLException if problem writing meeting record to database.
	 */
	private NodeSummary createMeetingMap(UIViewFrame oViewFrame, String sAuthor) throws Exception {

		/// **** WRITE ALL NEW NODES TO MEDIAINDEX WITH ZERO

		View view = null;
		UIViewPane oUIViewPane = null;
		IModel oModel = ProjectCompendium.APP.getModel();
		PCSession oSession = (PCSession)oModel.getSession();
		UINode newMap = null;

		Date dMeetingStartDate = oMeeting.getStartDate();
		String sMeetingName = oMeeting.getName();

		if (oViewFrame instanceof UIMapViewFrame) {
			UIMapViewFrame map = (UIMapViewFrame) oViewFrame;
			oUIViewPane = map.getViewPane();
			ViewPaneUI oViewPaneUI = oUIViewPane.getUI();

			String sDetails = ""; //$NON-NLS-1$
			if (dMeetingStartDate != null) {
				sDetails = sMeetingID+"\n\nMeeting was scheduled for: "+(UIUtilities.getSimpleDateFormat("d MMM, yyyy HH:mm")).format(dMeetingStartDate).toString(); //$NON-NLS-1$ //$NON-NLS-2$
			}

			newMap = oViewPaneUI.createNode(ICoreConstants.MAPVIEW,
										 ORIGINALID_TAG+oMeeting.getMeetingID(),
										 sAuthor,
										 sMeetingName,
										 sDetails,
										 200,
								 		 200);

			// GIVE IT THE SPECIAL> MEETING MAP IMAGE
			try {
				newMap.getNode().initialize(oSession, oModel);
				newMap.getNode().setSource("", UIImages.getPathString(IUIConstants.MEETING_BIG), sAuthor); //$NON-NLS-1$
			} catch(Exception e) {}
			newMap.setIcon(UIImages.get(IUIConstants.MEETING_BIG));

			view = ((View)newMap.getNode());
		}
		else {
			UIListViewFrame list = (UIListViewFrame) oViewFrame;
			UIList oUIList = list.getUIList();
			ListUI oListUI = oUIList.getListUI();

			NodePosition newMap2 = oListUI.createNode(ICoreConstants.MAPVIEW,
										 ORIGINALID_TAG+oMeeting.getMeetingID(),
										 sAuthor,
										 sMeetingName,
										 sMeetingMapID+"\n\nMeeting was scheduled for: "+UIUtilities.getSimpleDateFormat("d MMM, yyyy HH:mm").format(dMeetingStartDate).toString(), //$NON-NLS-1$ //$NON-NLS-2$
										 0,
										 ((oUIList.getNumberOfNodes() + 1) * 10)
										 
										 );

			// GIVE IT THE SPECIAL MEETING MAP IMAGE
			try {
				newMap.getNode().initialize(oSession, oModel);
				newMap.getNode().setSource("", UIImages.getPathString(IUIConstants.MEETING_BIG), sAuthor); //$NON-NLS-1$
			} catch(Exception e) {}

			view = ((View)newMap2.getNode());
		}

		if (view != null) {
			String sViewID = view.getId();
			setMeetingMapID(sViewID);
			oMeeting.setMapNode((NodeSummary)view);
			oMeeting.setMeetingMapID(sViewID);

			// CREATE THE MEETING RECORD BEFORE CREATING THE MEETING MAP ELSE FOREIGN KEY REFERENCES BROKEN
			IModel model = ProjectCompendium.APP.getModel();
			(model.getMeetingService()).createMeeting(model.getSession(), sMeetingID, sViewID, oMeeting.getName(), oMeeting.getStartDate(), ICoreConstants.STATUS_PREPARED);

			// CHECK IF REQUIRED CODE EXIST IN DATABASE, ELSE CREATE
			Code oAgendaCode = CoreUtilities.checkCreateCode(AGENDA_CODE_TEXT, oModel, oSession, sAuthor);
			Code oAttendeeCode = CoreUtilities.checkCreateCode(ATTENDEE_CODE_TEXT, oModel, oSession, sAuthor);
			Code oDocumentCode = CoreUtilities.checkCreateCode(DOCUMENT_CODE_TEXT, oModel, oSession, sAuthor);

			int y = 20;

			try {
				createAttendeeNodes(oAttendeeCode, view, sAuthor, y, oModel, oSession);
				y = createAgendaNodes(oAgendaCode, view, sAuthor, y, oModel, oSession);
				y = createDocumentNodes(oDocumentCode, view, sAuthor, y, oModel, oSession);
			} catch(Exception ex) {
				ex.printStackTrace();
				System.out.println("Error: (Meeting.createMeetingMap)\n\n"+ex.getMessage()); //$NON-NLS-1$
			}
		}

		if (newMap != null && oUIViewPane != null) {
			try {
				view.initializeMembers();
			}
			catch(Exception io) {}

			oUIViewPane.refreshIconIndicators();
			oUIViewPane.repaint();
			oUIViewPane.validate();
		}

		return (NodeSummary)view;
	}

    /**
     * Create a new map for the meeting and return if successful.
     *
     * @exception SQLException if problem writing meeting record to database.
     */
    public boolean createMeetingMapForReplay() throws Exception {
        if (oMeeting != null) {
            ArenaConnection arena = new ArenaConnection();
            arena.downloadXMLFile(oConnectionData, sSessionID, sMapFile, sDirectory);
            /*UIViewFrame viewFrame = ProjectCompendium.APP.getCurrentFrame();
            String sAuthor = ProjectCompendium.APP.getModel().getUserProfile().getAuthor();
            NodeSummary node = createMeetingMapForReplay(viewFrame, sAuthor);
            if (node != null) {
                return true;
            } */
        }
        return false;
    }

    /**
     * Create a new map node for the meeting and place in it any related nodes
     * for attendees, agenda, related documents etc.
     * @param oViewFrame, the view to create the new map in.
     * @param sAuthor, the author for the map.
     * @exception SQLException if problem writing meeting record to database.
     */
    private NodeSummary createMeetingMapForReplay(UIViewFrame oViewFrame, String sAuthor) throws Exception {

        /// **** WRITE ALL NEW NODES TO MEDIAINDEX WITH ZERO

        View view = null;
        UIViewPane oUIViewPane = null;
        IModel oModel = ProjectCompendium.APP.getModel();
        PCSession oSession = (PCSession)oModel.getSession();
        UINode newMap = null;

        Date dMeetingStartDate = oMeeting.getStartDate();
        String sMeetingName = oMeeting.getName();

        if (oViewFrame instanceof UIMapViewFrame) {
            UIMapViewFrame map = (UIMapViewFrame) oViewFrame;
            oUIViewPane = map.getViewPane();
            ViewPaneUI oViewPaneUI = oUIViewPane.getUI();

            String sDetails = ""; //$NON-NLS-1$
            if (dMeetingStartDate != null) {
                sDetails = sMeetingID+"\n\nMeeting was scheduled for: "+(UIUtilities.getSimpleDateFormat("d MMM, yyyy HH:mm")).format(dMeetingStartDate).toString(); //$NON-NLS-1$ //$NON-NLS-2$
            }

            newMap = oViewPaneUI.createNode(ICoreConstants.MAPVIEW,
                                         ORIGINALID_TAG+oMeeting.getMeetingID(),
                                         sAuthor,
                                         sMeetingName,
                                         sDetails,
                                         200,
                                         200);

            // GIVE IT THE SPECIAL> MEETING MAP IMAGE
            try {
                newMap.getNode().initialize(oSession, oModel);
                newMap.getNode().setSource("", UIImages.getPathString(IUIConstants.MEETING_BIG), sAuthor); //$NON-NLS-1$
            } catch(Exception e) {}
            newMap.setIcon(UIImages.get(IUIConstants.MEETING_BIG));

            view = ((View)newMap.getNode());
        }
        else {
            UIListViewFrame list = (UIListViewFrame) oViewFrame;
            UIList oUIList = list.getUIList();
            ListUI oListUI = oUIList.getListUI();

            NodePosition newMap2 = oListUI.createNode(ICoreConstants.MAPVIEW,
                                         ORIGINALID_TAG+oMeeting.getMeetingID(),
                                         sAuthor,
                                         sMeetingName,
                                         sMeetingMapID+"\n\nMeeting was scheduled for: "+UIUtilities.getSimpleDateFormat("d MMM, yyyy HH:mm").format(dMeetingStartDate).toString(), //$NON-NLS-1$ //$NON-NLS-2$
                                         0,
                                         ((oUIList.getNumberOfNodes() + 1) * 10)                                    
                                         );

            // GIVE IT THE SPECIAL MEETING MAP IMAGE
            try {
                newMap.getNode().initialize(oSession, oModel);
                newMap.getNode().setSource("", UIImages.getPathString(IUIConstants.MEETING_BIG), sAuthor); //$NON-NLS-1$
            } catch(Exception e) {}

            view = ((View)newMap2.getNode());
        }

        if (view != null) {
            String sViewID = view.getId();
            setMeetingMapID(sViewID);
            oMeeting.setMapNode((NodeSummary)view);
            oMeeting.setMeetingMapID(sViewID);

            // CREATE THE MEETING RECORD BEFORE CREATING THE MEETING MAP ELSE FOREIGN KEY REFERENCES BROKEN
            IModel model = ProjectCompendium.APP.getModel();
            (model.getMeetingService()).createMeeting(model.getSession(), sMeetingID + "_" + sMapFile, sViewID, oMeeting.getName(), oMeeting.getStartDate(), ICoreConstants.STATUS_RECORDED); //$NON-NLS-1$

            view.initialize(model.getSession(), model);
            UIViewFrame viewFrame = ProjectCompendium.APP.addViewToDesktop(view, view.getLabel());
            ArenaConnection arena = new ArenaConnection();
            arena.downloadXMLFile(oConnectionData, sSessionID, sMapFile, sDirectory);
        }

        if (newMap != null && oUIViewPane != null) {
            try {
                view.initializeMembers();
            }
            catch(Exception io) {}

            oUIViewPane.refreshIconIndicators();
            oUIViewPane.repaint();
            oUIViewPane.validate();
        }

        return (NodeSummary)view;
    }


	/**
	 * Create the nodes for the Attendees.
	 *
	 * @param oAttendeeCode the code to add to nodes.
	 * @param view the meeting map view.
	 * @param sAuthor the current author.
	 * @param y the current y position.
	 * @param oModel the current model.
	 * @param oSession the current session.
	 */
	private int createAttendeeNodes(Code oAttendeeCode, View view, String sAuthor, int y, IModel oModel, PCSession oSession) throws Exception {

		int x = X_OFFSET;
		int ySpacer = Y_SPACER;
		int startY = y;
		int stopY = y;
		int i = 0;
		int count = 0;
		int widest = 0;
		int width = 0;

		String sViewID = view.getId();
		NodePosition nodePos = null;

		String sLabel = ATTENDEE_LABEL;

		// Need to solve re-finding it first. What if label changed.
		//if (oMeeting != null) {
		//	sLabel = oMeeting.getName()+" "+ATTENDEE_LABEL;
		//}

		Date date = oMeeting.getStartDate();
		sLabel+= " - "+CoreCalendar.getDateString(date, "yyyy.MM.dd")+": "+oMeeting.getName(); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		NodePosition oAttendeeMap = view.addMemberNode( ICoreConstants.MAPVIEW, "", "", sAuthor, sLabel, "", 20, 20); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		createMediaIndex(sViewID, oAttendeeMap.getNode().getId(), 0);

		View oAttendeeView = (View)oAttendeeMap.getNode();

		Vector vtAttendees = oMeeting.getAttendees();
		count = vtAttendees.size();
		Vector nodes = new Vector(count);

		// attendee nodes
		String sName = ""; //$NON-NLS-1$
		String sOriginalID = ""; //$NON-NLS-1$
		for (i=0; i<count; i++) {
			MeetingAttendee attendee = (MeetingAttendee)vtAttendees.elementAt(i);

			sName = attendee.getName();
			NodeSummary nodeSum = null;

			// check if already have node for this person, if so, transclude.
			
			String sCleanName = CoreUtilities.replace(sName, '\'', "\\'"); //$NON-NLS-1$
			Vector searchResults = searchExactNodeLabel(sCleanName, ICoreConstants.REFERENCE);

			// Java 1.5 code
			//Vector searchResults = searchExactNodeLabel(sName.replace("'", "\\'"), ICoreConstants.REFERENCE);
						
			if (searchResults.size() >0) {
				nodePos = oAttendeeView.addNodeToView((NodeSummary)searchResults.elementAt(0), x, y);
				nodeSum = nodePos.getNode();
				nodeSum.initialize(oSession, oModel);
				sOriginalID = ORIGINALID_TAG+attendee.getOriginalID();
				if (!(nodeSum.getOriginalID()).equals(sOriginalID)) {
					nodeSum.setOriginalID(sOriginalID, sAuthor);
				}
			}
			else {
				nodePos = oAttendeeView.addMemberNode( ICoreConstants.REFERENCE, "", ORIGINALID_TAG+attendee.getOriginalID(), sAuthor, sName, attendee.getOriginalID(), x, y); //$NON-NLS-1$
				nodeSum = nodePos.getNode();
				nodeSum.initialize(oSession, oModel);
			}


			String email = attendee.getEmail();
			if (!email.equals("") && nodePos != null) { //$NON-NLS-1$
				nodeSum.setSource(email, "", sAuthor); //$NON-NLS-1$
			}

			// tag as attendee
			if (oAttendeeCode != null) {
				nodeSum.addCode(oAttendeeCode);
			}

			createMediaIndex(sViewID, nodePos.getNode().getId(), 0);

			// Record width for later adjustments
			Dimension dim = (new UINode(nodePos, sAuthor)).getPreferredSize();
			width = dim.width;
			if (width > widest) {
				widest = width;
			}

			nodes.addElement(nodePos);

			y += ySpacer;
		}

		recalcuateNodeXPositions(nodes, oAttendeeView, widest);

		stopY = y;

		// question node
		int nHeight = stopY-startY;
		NodePosition questionNode = oAttendeeView.addMemberNode( ICoreConstants.ISSUE, "", "", sAuthor, ATTENDEE_LABEL, "", 20, startY+(nHeight/2)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		createMediaIndex(sViewID, questionNode.getNode().getId(), 0);
		recalcuateNodeYPosition(questionNode, view, nHeight);

		// links
		NodePosition tempNode = null;
		for (i=0; i<count; i++) {
			tempNode = (NodePosition)nodes.elementAt(i);
			LinkProperties props = UIUtilities.getLinkProperties(ICoreConstants.DEFAULT_LINK);
			props.setArrowType(ICoreConstants.ARROW_TO);
			oAttendeeView.addMemberLink(ICoreConstants.DEFAULT_LINK, "0", sAuthor, tempNode.getNode(), questionNode.getNode(), props); //$NON-NLS-1$
		}

		return y;
	}


	/**

	 * Create the nodes for the Agenda Items.
	 *
	 * @param oAgendaCode the code to add to nodes.
	 * @param view the meeting map view.
	 * @param sAuthor the current author.
	 * @param y the current y position.
	 * @param oModel the current model.
	 * @param oSession the current session.
	 */
	private int createAgendaNodes(Code oAgendaCode, View view, String sAuthor, int y, IModel oModel, PCSession oSession) throws Exception {

		int x = X_OFFSET;
		int ySpacer = Y_SPACER;
		int startY = y;
		int stopY = y;
		int i = 0;
		int count = 0;
		int widest = 0;
		int width = 0;
		int innerX = 10;
		int innerY = 250;

		String sViewID = view.getId();
		NodePosition nodePos = null;

		y = ySpacer + 30;

		Vector vtAgenda = oMeeting.getAgenda();
		count = vtAgenda.size();
		Vector nodes = new Vector(count);

		startY = y;
		for (i=0; i<count; i++) {
			MeetingAgendaItem item = (MeetingAgendaItem)vtAgenda.elementAt(i);
			nodePos = view.addMemberNode( ICoreConstants.MAPVIEW, "", ORIGINALID_TAG+item.getOriginalID(),sAuthor, item.getDisplayName(), item.getOriginalID(), x, y); //$NON-NLS-1$

			createMediaIndex(sViewID, nodePos.getNode().getId(), 0);

			// Record width for later adjustments
			Dimension dim = (new UINode(nodePos, sAuthor)).getPreferredSize();
			width = dim.width;
			if (width > widest) {
				widest = width;
			}

			// tag as agenda item
			if (oAgendaCode != null) {
				NodeSummary nodeSum = nodePos.getNode();
				nodeSum.initialize(oSession, oModel);
				nodeSum.addCode(oAgendaCode);
			}

			nodes.addElement(nodePos);
			y += ySpacer;

			// add inner question with same name as agenda item
			View newView = (View)nodePos.getNode();
			if (newView != null) {
				newView.initialize(oSession, oModel);
				newView.addMemberNode( ICoreConstants.ISSUE, "", "" ,sAuthor, item.getDisplayName(), "", innerX, innerY); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			}
		}

		// adjust node x positions for final actual widths so they line up right.
		recalcuateNodeXPositions(nodes, view, widest);

		stopY = y;

		// question node
		int nHeight = stopY-startY;
		NodePosition questionNode = view.addMemberNode( ICoreConstants.ISSUE, "", "", sAuthor, AGENDA_LABEL, "", 20, startY+(nHeight/2)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		createMediaIndex(sViewID, questionNode.getNode().getId(), 0);
		recalcuateNodeYPosition(questionNode, view, nHeight);

		// links
		NodePosition tempNode = null;
		for (i=0; i<count; i++) {
			tempNode = (NodePosition)nodes.elementAt(i);
			LinkProperties props = UIUtilities.getLinkProperties(ICoreConstants.DEFAULT_LINK);
			props.setArrowType(ICoreConstants.ARROW_TO);			
			view.addMemberLink(ICoreConstants.DEFAULT_LINK, "0", sAuthor, tempNode.getNode(), questionNode.getNode(), props); //$NON-NLS-1$
		}

		return y;
	}

	/**
	 * Create the nodes for the documents.
	 *
	 * @param oDocumentCode the code to add to nodes.
	 * @param view the meeting map view.
	 * @param sAuthor the current author.
	 * @param y the current y position.
	 * @param oModel the current model.
	 * @param oSession the current session.
	 */
	private int createDocumentNodes(Code oDocumentCode, View view, String sAuthor, int y, IModel oModel, PCSession oSession) throws Exception {

		int x = X_OFFSET;
		int ySpacer = Y_SPACER;
		int startY = y;
		int stopY = y;
		int i = 0;
		int count = 0;
		int widest = 0;
		int width = 0;

		String sViewID = view.getId();
		NodePosition nodePos = null;

		y+= Y_SPACER;

		Vector vtDocuments = oMeeting.getDocuments();
		count = vtDocuments.size();
		Vector nodes = new Vector(count);

		startY = y;

		for (i=0; i<count; i++) {
			MeetingDocument doc = (MeetingDocument)vtDocuments.elementAt(i);
			nodePos = view.addMemberNode( ICoreConstants.REFERENCE, "", ORIGINALID_TAG+doc.getOriginalID(), sAuthor, doc.getName(), "", x, y); //$NON-NLS-1$ //$NON-NLS-2$

			createMediaIndex(sViewID, nodePos.getNode().getId(), 0);

			NodeSummary nodeSum = nodePos.getNode();
			nodeSum.initialize(oSession, oModel);

			// Record width for later adjustments
			Dimension dim = (new UINode(nodePos, sAuthor)).getPreferredSize();
			width = dim.width;
			if (width > widest) {
				widest = width;
			}

			String url = doc.getURL();
			if (!url.equals("")) { //$NON-NLS-1$
				nodeSum.setSource(url, "", sAuthor); //$NON-NLS-1$
			}

			// tag as meeting document
			if (oDocumentCode != null) {
				nodeSum.addCode(oDocumentCode);
			}

			nodes.addElement(nodePos);
			y += ySpacer;
		}

		// adjust node positions for final actual widths
		recalcuateNodeXPositions(nodes, view, widest);

		stopY = y;

		// question node
		int nHeight = stopY-startY;
		NodePosition questionNode = view.addMemberNode( ICoreConstants.ISSUE, "", "", sAuthor, DOCUMENT_LABEL, "", 20, startY+(nHeight/2)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		createMediaIndex(sViewID, questionNode.getNode().getId(), 0);

		recalcuateNodeYPosition(questionNode, view, nHeight);

		// links
		NodePosition tempNode = null;
		for (i=0; i<count; i++) {
			tempNode = (NodePosition)nodes.elementAt(i);
			LinkProperties props = UIUtilities.getLinkProperties(ICoreConstants.DEFAULT_LINK);
			props.setArrowType(ICoreConstants.ARROW_TO);
			view.addMemberLink(ICoreConstants.DEFAULT_LINK, "0", sAuthor, tempNode.getNode(), questionNode.getNode(), props); //$NON-NLS-1$
		}

		return y;
	}

	/**
	 * Adjust node x positions for final actual widths, so all nodes line up.
	 *
	 * @param vtNodes the nodes whose positions to adjust.
	 * @param oView the view in which ti adjust the nodes position.
	 * @param nWidest the width to adjust against.
	 *
	 */
	private void recalcuateNodeXPositions(Vector vtNodes, View oView, int nWidest) {

		NodePosition oNodePosition = null;
		UINode oUINode = null;
		Dimension oDimension = null;
		Point oNewPosition = null;
		Point oOldPosition = null;

		int count = vtNodes.size();

		for (int i=0; i<count; i++) {
			Object obj = vtNodes.elementAt(i);
			if (obj instanceof UINode) {
				oUINode = (UINode) obj;
				oNodePosition = oUINode.getNodePosition();
			} else {
				oNodePosition = (NodePosition)obj;
				oUINode = new UINode(oNodePosition, sAuthor);
			}
			oDimension = oUINode.getPreferredSize();
			oOldPosition = oNodePosition.getPos();
			oNewPosition = new Point( oOldPosition.x+((nWidest-oDimension.width)/2), oOldPosition.y );

			try {
				oNodePosition.setPos(oNewPosition);
				oView.setNodePosition(oNodePosition.getNode().getId(), oNewPosition);
			} catch(Exception ex) {
				System.out.println("unable to adjust meeting node x position due to: "+ex.getMessage()); //$NON-NLS-1$
			}
		}
	}

	/**
	 * Adjust node x positions for final actual widths, so all nodes line up.
	 *
	 * @param vtNodes, the nodes whose positions to adjust.
	 * @param oView, the view in which ti adjust the nodes position.
	 * @param nHeight, the height of the linked node spread to center this on.
	 *
	 */
	private void recalcuateNodeYPosition(NodePosition oNodePosition, View oView, int nHeight) {

		UINode  oUINode = new UINode(oNodePosition, sAuthor);
		Dimension oDimension = oUINode.getPreferredSize();
		Point oOldPosition = oNodePosition.getPos();

		if (nHeight > oDimension.height) {
			Point oNewPosition = new Point( oOldPosition.x, oOldPosition.y-((oDimension.height)/2)-7 ); // why - 7?, cause needs it.

			try {
				oNodePosition.setPos(oNewPosition);
				oView.setNodePosition(oNodePosition.getNode().getId(), oNewPosition);
			} catch(Exception ex) {
				System.out.println("unable to adjust meeting node y position due to: "+ex.getMessage()); //$NON-NLS-1$
			}
		}
	}

	/**
	 * Search the nodes for labels exactly matching the given string.
	 * Load the results into the displayed list.
	 *
	 * @param String text, the text to match against the start of node labels.
	 * @param int type, the type of the node to search the label on.
	 * @return Vector, return the list of any matches found.
	 */
	private Vector searchExactNodeLabel(String sText, int nType) {
		Vector vtNodes = new Vector();
		try {
			//String cleantext = CoreUtilities.cleanSQLText(text, FormatProperties.nDatabaseType);
			IModel model = ProjectCompendium.APP.getModel();
			vtNodes = model.getQueryService().searchExactNodeLabel(model.getSession(), sText, nType);
		} catch(SQLException ex) {
			ProjectCompendium.APP.displayError("Exception:" + ex.getMessage()); //$NON-NLS-1$
		}

		return vtNodes;
	}

	/**
	 * Search the nodes for labels exactly matching the given string.
	 * Load the results into the displayed list.
	 *
	 * @param String text, the text to match against the start of node labels.
	 * @param int type, the type of the node to search the label on.
	 * @param String sViewID, the id of the view to search in.
	 * @return Vector, return the list of any matches found.
	 */
	private Vector searchExactNodeLabelInView(String sText, int nType, String sViewID) {
		Vector vtNodes = new Vector();
		try {
			IModel model = ProjectCompendium.APP.getModel();
			vtNodes = model.getQueryService().searchExactNodeLabelInView(model.getSession(), sText, nType, sViewID);
		} catch(SQLException ex) {
			ex.printStackTrace();
			ProjectCompendium.APP.displayError("Exception:" + ex.getMessage()); //$NON-NLS-1$
		}

		return vtNodes;
	}

	/**
	 * Search the nodes for labels starting with the given string.
	 * Load the results into the displayed list.
	 *
	 * @param String text, the text to match against the start of node labels.
	 * @param int type, the type of the node to search the label on.
	 * @param String sViewID, the id of the view to search in.
	 * @return Vector, return the list of any matches found.
	 */
	private Vector searchNodeLabelInView(String sText, int nType, String sViewID) {
		Vector vtNodes = new Vector();
		try {
			IModel model = ProjectCompendium.APP.getModel();
			vtNodes = model.getQueryService().searchNodeLabelInView(model.getSession(), sText, nType, sViewID);
		} catch(SQLException ex) {
			ex.printStackTrace();
			ProjectCompendium.APP.displayError("Exception:" + ex.getMessage()); //$NON-NLS-1$
		}

		return vtNodes;
	}

	/**
	 * Process the passed string as a Meeting Record setup string.
	 * Store the memetic setup data and then
	 * download the passed meeting data, if not found and start recording
	 *
	 * @param s the string to process.
	 */
	public boolean processSetupData(String sSetupData) {

		sSetupData = sSetupData.trim();
        //System.out.println("Setup Data:" + sSetupData);

		String sMeetingURI = null;
		String sArenaURL = null;
		//String sArenaPort = "";
		String sTriplestoreURL = null;
		//String sTriplestorePort = "";
		String sUserName = null;
		String sPassword = null;
		//String sFileURL = "";
		String sFileName = null;
		String sProxyHost = ""; //$NON-NLS-1$
		String sProxyPort = ""; //$NON-NLS-1$

		// Meeting URI
		int ind = sSetupData.indexOf(":"); //$NON-NLS-1$
		int ind2 = sSetupData.indexOf("?"); //$NON-NLS-1$
		if (ind > -1 && ind2 > 1) {
			sMeetingURI = sSetupData.substring(ind+1, ind2);
			sSetupData = sSetupData.substring(ind2+1);
            //System.out.println(sMeetingURI);

			StringTokenizer oTokenizer = new StringTokenizer(sSetupData, "&"); //$NON-NLS-1$
			StringTokenizer oInnerTokenizer = null;
			String sKey = ""; //$NON-NLS-1$
			String sValue = ""; //$NON-NLS-1$
			while (oTokenizer.hasMoreTokens()) {
				String token = (String)oTokenizer.nextToken();
				oInnerTokenizer = new StringTokenizer(token, "="); //$NON-NLS-1$

				if (oInnerTokenizer.hasMoreTokens()) {
					sKey = oInnerTokenizer.nextToken();

                    sValue = ""; //$NON-NLS-1$
					if (oInnerTokenizer.hasMoreTokens()) {
						sValue = oInnerTokenizer.nextToken();
                    }

					if (sKey.equals("arenahost")) { //$NON-NLS-1$
                        //System.out.println(sKey + "=" + sValue);
						sArenaURL = sValue;
					} else if (sKey.equals("triplestoreurl")) { //$NON-NLS-1$
                        //System.out.println(sKey + "=" + sValue);
						sTriplestoreURL = sValue;
					} else if (sKey.equals("username")) { //$NON-NLS-1$
                        //System.out.println(sKey + "=" + sValue);
						sUserName = sValue;
					} else if (sKey.equals("password")) { //$NON-NLS-1$
                        //System.out.println(sKey + "=" + sValue);
						sPassword = sValue;
					} else if (sKey.equals("filename")) { //$NON-NLS-1$
                        //System.out.println(sKey + "=" + sValue);
						sFileName = sValue;
					} else if (sKey.equals("proxyhost")) { //$NON-NLS-1$
                        //System.out.println(sKey + "=" + sValue);
						sProxyHost = sValue;
					} else if (sKey.equals("proxyport")) { //$NON-NLS-1$
                        //System.out.println(sKey + "=" + sValue);
						sProxyPort = sValue;
					}
				}
			}
		}

		if (!sProxyHost.equals("") && !sProxyPort.equals("")) { //$NON-NLS-1$ //$NON-NLS-2$
			System.setProperty("proxySet", "true"); //$NON-NLS-1$ //$NON-NLS-2$
			System.setProperty("http.proxyHost", sProxyHost); //$NON-NLS-1$
			System.setProperty("http.proxyPort", sProxyPort); //$NON-NLS-1$
		}

		if ((sMeetingURI != null) && (sArenaURL != null) &&
					(sTriplestoreURL != null) &&
					(sUserName != null) && (sPassword != null) &&
					(sFileName != null)) {

			sMeetingID = sMeetingURI;

			Properties connectionProperties = new Properties();

			try {
				File optionsFile = new File(AccessGridData.FILE_NAME);
				if (optionsFile.exists()) {
					connectionProperties.load(new FileInputStream(AccessGridData.FILE_NAME));
				}

				connectionProperties.put("arenaurl", sArenaURL); //$NON-NLS-1$
				//connectionProperties.put("arenaport", sArenaPort);
				connectionProperties.put("triplestoreurl", sTriplestoreURL); //$NON-NLS-1$
				//connectionProperties.put("triplestoreport", sTriplestorePort);
				connectionProperties.put("username", sUserName); //$NON-NLS-1$
				connectionProperties.put("password", sPassword); //$NON-NLS-1$
				//connectionProperties.put("fileurl", sFileURL);

				sMapFile = sFileName;

				if (!sProxyHost.equals("") && !sProxyPort.equals("")) { //$NON-NLS-1$ //$NON-NLS-2$
					connectionProperties.put("localproxyhost", sProxyHost); //$NON-NLS-1$
					connectionProperties.put("localproxyport", sProxyPort); //$NON-NLS-1$
				}

				connectionProperties.store(new FileOutputStream(AccessGridData.FILE_NAME), "Access Grid Details"); //$NON-NLS-1$
				reloadAccessGridData();

				return true;
			} catch(Exception io) {
				System.out.println("The following error occurred trying to store the setup data:\n\n"+io.getMessage()); //$NON-NLS-1$
			}
		} else {
			ProjectCompendium.APP.displayError(LanguageProperties.getString(LanguageProperties.MEETING_BUNDLE, "MeetingManager.setupDataMissing")); //$NON-NLS-1$
			//LAUNCH SETUP SCREEN
			return false;
		}

		return false;
	}

	/**
	 * Download the meeting data.
	 */
	public void setupMeetingForRecording() {
        nMeetingType = RECORDING;
		// CHECK THAT THE MEETING HAS NOT ALREADY BEEN ENTERED AND A MAP CREATED
		try {
			IModel model = ProjectCompendium.APP.getModel();
            while (model == null) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    // Do Nothing
                }
                model = ProjectCompendium.APP.getModel();
            }
            IMeetingService service = model.getMeetingService();
            PCSession session = model.getSession();
            View view = null;

            oMeeting = service.getMeeting(session, sMeetingID);
            if (oMeeting != null) {
                setMeetingMapID(oMeeting.getMeetingMapID());
                //System.out.println("Finding " + sMeetingMapID);
                view = (View)model.getNodeService().getView(session, sMeetingMapID);
            }

			if ((oMeeting == null) || (view == null)) {
				if (downloadMeetingData(sMeetingID)) {
					try {
						createMeetingMap();
					} catch(Exception ex) {
						ex.printStackTrace();
						ProjectCompendium.APP.displayError(LanguageProperties.getString(LanguageProperties.MEETING_BUNDLE, "MeetingManager.errorEncountered")+":\n\n"+ex.getLocalizedMessage()); //$NON-NLS-1$
					}
				} else {
					ProjectCompendium.APP.displayError(LanguageProperties.getString(LanguageProperties.MEETING_BUNDLE, "MeetingManager.dataDownloadFilaure")); //$NON-NLS-1$
				}
			}
		} catch(SQLException ex) {
			ex.printStackTrace();
			System.out.flush();

			ProjectCompendium.APP.displayError(LanguageProperties.getString(LanguageProperties.MEETING_BUNDLE, "MeetingManager.errorCheck")+":\n\n"+ex.getLocalizedMessage()); //$NON-NLS-1$
		}
	}

// REPLAY METHODS

	/**
	 * Process the passed string as a Meeting Replay setup string.
	 * Open the passed meeting map if found,
	 * and connect to the jabber meeting with the given info.
	 *
	 * @param s the string to process.
	 */
	public void processReplayData(String sSetupData) {
		sSetupData = sSetupData.trim();
        //System.out.println("ReplayData:" + sSetupData);

		String sMeetingURI = ""; //$NON-NLS-1$
		String sUserName = ""; //$NON-NLS-1$
		String sPassword = ""; //$NON-NLS-1$
		String sServer = ""; //$NON-NLS-1$
		String sRoom = ""; //$NON-NLS-1$
		String sResource = "replayvideo"; //$NON-NLS-1$

		// Map ID
		int ind = sSetupData.indexOf(":"); //$NON-NLS-1$
		int ind2 = sSetupData.indexOf("?"); //$NON-NLS-1$
		if (ind > -1 && ind2 > 1) {
			sMeetingURI = sSetupData.substring(ind+1, ind2);
			sSetupData = sSetupData.substring(ind2+1);
            //System.out.println(sMeetingURI);

			StringTokenizer oTokenizer = new StringTokenizer(sSetupData, "&"); //$NON-NLS-1$
			StringTokenizer oInnerTokenizer = null;
			String sKey = ""; //$NON-NLS-1$
			String sValue = ""; //$NON-NLS-1$
			while (oTokenizer.hasMoreTokens()) {
				String token = (String)oTokenizer.nextToken();
				oInnerTokenizer = new StringTokenizer(token, "="); //$NON-NLS-1$

				if (oInnerTokenizer.hasMoreTokens()) {
					sKey = oInnerTokenizer.nextToken();
					if (oInnerTokenizer.hasMoreTokens()) {
						sValue = oInnerTokenizer.nextToken();

						if (sKey.equals("jid")) { //$NON-NLS-1$
							sUserName = sValue;
                            //System.err.println(sKey + "=" + sValue);
						} else if (sKey.equals("pswd")) { //$NON-NLS-1$
							sPassword = sValue;
                            //System.err.println(sKey + "=" + sValue);
						} else if (sKey.equals("srv")) { //$NON-NLS-1$
							sServer = sValue;
                            //System.err.println(sKey + "=" + sValue);
						} else if (sKey.equals("grp")) { //$NON-NLS-1$
							sRoom = sValue;
                            //System.err.println(sKey + "=" + sValue);
						}
					}
				}
			}
		}

		if (!sMeetingURI.equals("") && !sUserName.equals("") && !sPassword.equals("") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				&& !sServer.equals("") && !sRoom.equals("")) { //$NON-NLS-1$ //$NON-NLS-2$
            sMeetingID = sMeetingURI;

            Properties connectionProperties = new Properties();

            try {
                File optionsFile = new File("System"+ProjectCompendium.sFS+"resources"+ProjectCompendium.sFS+UIMeetingReplayDialog.PROPERTY_FILE); //$NON-NLS-1$ //$NON-NLS-2$
                if (optionsFile.exists()) {
                    connectionProperties.load(new FileInputStream(optionsFile));
                }

                connectionProperties.put("mediacompserver", sServer); //$NON-NLS-1$
                connectionProperties.put("mediacompusername", sUserName); //$NON-NLS-1$
                connectionProperties.put("mediacomppassword", sPassword); //$NON-NLS-1$
                connectionProperties.put("mediacompresource", sResource); //$NON-NLS-1$
                connectionProperties.put("mediaroomserver", sRoom); //$NON-NLS-1$

                connectionProperties.store(new FileOutputStream(optionsFile), "Media Replay Details"); //$NON-NLS-1$
            } catch(Exception io) {
                System.out.println("The following error occurred trying to store the setup data:\n\n"+io.getMessage()); //$NON-NLS-1$
            }
		} else {
			ProjectCompendium.APP.displayError(LanguageProperties.getString(LanguageProperties.MEETING_BUNDLE, "MeetingManager.errorMissingDataA")+"\n\n"+
					LanguageProperties.getString(LanguageProperties.MEETING_BUNDLE, "MeetingManager.errorMissingDataB")); //$NON-NLS-1$
		}
	}

    /**
     * Download the meeting data.
     */
    public void setupMeetingForReplay() {
        nMeetingType = REPLAY;
        

        // CHECK THAT THE MEETING HAS NOT ALREADY BEEN ENTERED AND A MAP CREATED
        try {
            TripleStoreConnection connection = new TripleStoreConnection(oConnectionData);
            connection.loadNodes(this, sMeetingID);
            sSessionID = connection.getSessionID(sMeetingID);
            
            IModel model = ProjectCompendium.APP.getModel();
            while (model == null) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    // Do Nothing
                }
                model = ProjectCompendium.APP.getModel();
            }
            IMeetingService service = model.getMeetingService();
            PCSession session = model.getSession();
            View view = null;
                oMeeting = service.getMeeting(session, sMeetingID);

            if (oMeeting != null) {
                setMeetingMapID(oMeeting.getMeetingMapID());
                //System.out.println("Finding " + sMeetingMapID);
                view = (View)model.getNodeService().getView(session, sMeetingMapID);
            }

            if ((oMeeting == null) || (view == null)) {
                //System.out.println("Downloading Meeting data (oMeeting = " + oMeeting + ", view = " + view + ")");
                if (downloadMeetingData(sMeetingID)) {
                    //System.out.println("Downloading Meeting Maps");
                    try {
                        if ((sMapFile != null) && !sMapFile.equals("")) { //$NON-NLS-1$
                            createMeetingMapForReplay();
                        } else {
                            createMeetingMap();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    ProjectCompendium.APP.displayError(LanguageProperties.getString(LanguageProperties.MEETING_BUNDLE, "MeetingManager.downloadFailure")); //$NON-NLS-1$
                }
            } else if ((sMapFile != null) && !sMapFile.equals("")) { //$NON-NLS-1$
                createMeetingMapForReplay();
            }


        } catch(Exception ex) {
            ex.printStackTrace();
            System.out.flush();
            ProjectCompendium.APP.displayError(LanguageProperties.getString(LanguageProperties.MEETING_BUNDLE, "MeetingManager.errorChecking")+":\n\n" + ex.getLocalizedMessage()); //$NON-NLS-1$
        }
    }


	/**
	 * Stop Recording events for a meeting and upload data.
	 */
	 public boolean stopReplayRecording() {
		if (!bIsRecording) {
			return true;
		}
		 
		bIsRecording = false;		 
		oJabberConnection.closeMeetingReplayConnection();
		setCaptureEvents(false);
		ProjectCompendium.APP.getToolBarManager().setMeetingToolBarEnabled(false);
		ProjectCompendium.APP.getStatusBar().resetColors();
		ProjectCompendium.APP.refreshIconIndicators(); // to get it to clear the 'M' node indicators

		// TAKE OFF THE REPLAY TITLE PART
		ProjectCompendium.APP.setTitle(sTitle);
		sTitle = ""; //$NON-NLS-1$

		// ASK USER WHAT TO DO WITH DATA, IF THERE IS ANY.
		if (getEventCount() > 0) {
			UIMeetingUploadChoiceDialog dlg = new UIMeetingUploadChoiceDialog(this);
			dlg.setVisible(true);
		}

		return true;
	}


    /**
     * Load the previously stored medai replay connection data.
     */
    private void openReplay() {

        File optionsFile = new File("System"+ProjectCompendium.sFS+"resources"+ProjectCompendium.sFS+UIMeetingReplayDialog.PROPERTY_FILE); //$NON-NLS-1$ //$NON-NLS-2$
        connectionProperties = new Properties();

        if (optionsFile.exists()) {
            try {
                connectionProperties.load(new FileInputStream("System"+ProjectCompendium.sFS+"resources"+ProjectCompendium.sFS+UIMeetingReplayDialog.PROPERTY_FILE)); //$NON-NLS-1$ //$NON-NLS-2$
                String sServer = null;
                String sUsername = null;
                String sPassword = null;
                String sResource = null;
                String sRoomServer = null;
                String value = connectionProperties.getProperty("mediacompserver"); //$NON-NLS-1$
                if (value != null)
                    sServer = value;

                value = connectionProperties.getProperty("mediacompusername"); //$NON-NLS-1$
                if (value != null)
                    sUsername = value;

                value = connectionProperties.getProperty("mediacomppassword"); //$NON-NLS-1$
                if (value != null)
                    sPassword = value;

                value = connectionProperties.getProperty("mediacompresource"); //$NON-NLS-1$
                if (value != null)
                    sResource = value;

                value = connectionProperties.getProperty("mediaroomserver"); //$NON-NLS-1$
                if (value != null)
                    sRoomServer = value;

                if (oJabberConnection == null) {
                    oJabberConnection = new JabberConnection(this);
                }
                openMeetingReplayConnection(sServer, sUsername, sPassword, sResource, sRoomServer);

            } catch (IOException e) {
                System.out.println("Unable to load MeetingReplay.properties file"); //$NON-NLS-1$
            }
        }
    }

    private void startActualReplay() {
    	bIsRecording = true;
        UIViewFrame viewFrame = null;
        try {
            // OPEN MEETING MAP, IF NOT ALREADY
            IModel model = ProjectCompendium.APP.getModel();
            View view = null;
            try {
                view = (View)model.getNodeService().getView(model.getSession(), sMeetingMapID);
            } catch (Exception ex) {
                ProjectCompendium.APP.displayError(LanguageProperties.getString(LanguageProperties.MEETING_BUNDLE, "MeetingManager.meetingMapNotFound")); //$NON-NLS-1$
            }

            view.initialize(model.getSession(), model);
            viewFrame = ProjectCompendium.APP.addViewToDesktop(view, view.getLabel());
            Vector history = new Vector();
            history.addElement(new String(LanguageProperties.getString(LanguageProperties.MEETING_BUNDLE, "MeetingManager.meetingRecorder"))); //$NON-NLS-1$
            viewFrame.setNavigationHistory(history);

            ProjectCompendium.APP.setWaitCursor();
            viewFrame.setCursor(new Cursor(java.awt.Cursor.WAIT_CURSOR));

            setCaptureEvents(true);
            ProjectCompendium.APP.getToolBarManager().setMeetingToolBarEnabled(true);

            sTitle = ProjectCompendium.APP.getTitle();
            sExtraTitle = REPLAY_TITLE;
            ProjectCompendium.APP.setTitle(sTitle + sExtraTitle);
            ProjectCompendium.APP.getStatusBar().setBackgroundColor(Color.yellow);
            ProjectCompendium.APP.getStatusBar().setForegroundColor(Color.black);
            ProjectCompendium.APP.refreshIconIndicators(); // to get it to draw the 'M' node indicators

            ProjectCompendium.APP.displayMessage(LanguageProperties.getString(LanguageProperties.MEETING_BUNDLE, "MeetingManager.connectionAndRecordingStartedA")+"\n"+
            		LanguageProperties.getString(LanguageProperties.MEETING_BUNDLE, "MeetingManager.connectionAndRecordingStartedB"), LanguageProperties.getString(LanguageProperties.MEETING_BUNDLE, "MeetingManager.meetingReplay")); //$NON-NLS-1$ //$NON-NLS-2$
        } catch(Exception ex) {
            ProjectCompendium.APP.displayError(LanguageProperties.getString(LanguageProperties.MEETING_BUNDLE, "MeetingManager.errorOccurred")+":\n\n"+ex.getLocalizedMessage()); //$NON-NLS-1$
            ex.printStackTrace();
        }

        ProjectCompendium.APP.setDefaultCursor();

        if (viewFrame != null) {
            viewFrame.setCursor(new Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        }

    }

	/**
	 * Start Recording events for a meeting replay.
	 */
	 public boolean startReplayRecording() {
		boolean bSuccess = false;
		openReplay();
		return bSuccess;
	}

// JABBER CONNECTION METHODS

	public void processJabberMessage(String sMessage){
		oJabberConnection.processJabberMessage(sMessage);
	}

	/**
	 * Process the passed string as a Meeting Replay index string.
	 * Create a new answer node with the given video index timestamp.
	 *
	 * @param s the string to process.
	 * @param nX the x position on the current view to create the new node.
	 * @param nY the y position on the current view to create the new node.
	 */
	public void processAsMeetingReplayIndex(String s, int nX, int nY) {
		oJabberConnection.processAsMeetingReplayIndex(s, nX, nY, sMeetingID);
	}

	/**
	 * Open a Jabber Meeting Replay connection for the given details.
	 *
	 * @param server the jabber server to connect to.
	 * @param username the username of the account to connect to.
	 * @param password the password to use to connect.
	 * @param sResource the resource to use to connect.
	 * @param roomJIDString the conecference room identifier to use.
	 */
	public void openMeetingReplayConnection(String sServer, String sUsername, String sPassword, String sResource,
												String roomJIDString	) {
		oJabberConnection.openMeetingReplayConnection(sServer, sUsername, sPassword, sResource, roomJIDString);
	}

	/**
	 * Close the Jabber Meeting Replay connection if open.
	 */
	public void closeMeetingReplayConnection() {
		oJabberConnection.closeMeetingReplayConnection();
		setCaptureEvents(false);
	}

	/**
	 * Return if an Jabber Media connection is open.
	 * @return boolean, true if the connection if open, else false.
	 */
	public void meetingReplayConnectionOpened() {
		startActualReplay();
	}

	/**
	 * Return if an Jabber Meeting Replay connection is open.
	 * @return true if the connection if open, else false.
	 */
	public boolean isMeetingReplayConnected() {
		return oJabberConnection.isMeetingReplayConnected();
	}

	/**
	 * Send the given node in the current view to the Meeting Replay Jabber account.
	 *
	 * @param jid the jabber id of the Jabber account to send the nodes to.
	 */
	public void sendMeetingReplay(NodeUI nodeui) {
		oJabberConnection.sendMeetingReplay(nodeui, sMeetingID);
	}

// PROGRESS LISTENER METHODS

    /**
     * Adds <code>DBProgressListener</code> to listeners notified when progress events happen.
     *
     * @see #removeProgressListener
     * @see #removeAllProgressListeners
     * @see #fireProgressCount
     * @see #fireProgressUpdate
     * @see #fireProgressComplete
     * @see #fireProgressAlert
     */
    public void addProgressListener(DBProgressListener listener) {
        if (listener == null) return;
        if (!progressListeners.contains(listener)) {
            progressListeners.addElement(listener);
        }
    }

    /**
     * Removes <code>DBProgressListener</code> from listeners notified of progress events.
     *
     * @see #addProgressListener
     * @see #removeAllProgressListeners
     * @see #fireProgressCount
     * @see #fireProgressUpdate
     * @see #fireProgressComplete
     * @see #fireProgressAlert
     */
    public void removeProgressListener(DBProgressListener listener) {
        if (listener == null) return;
        progressListeners.removeElement(listener);
    }

    /**
     * Removes all listeners notified about progress events.
     *
     * @see #addProgressListener
     * @see #removeProgressListener
     * @see #fireProgressCount
     * @see #fireProgressUpdate
     * @see #fireProgressComplete
     * @see #fireProgressAlert
     */
    public void removeAllProgressListeners() {
        progressListeners.clear();
    }

    /**
     * Notifies progress listeners of the total count of progress events.
     *
     * @see #fireProgressUpdate
     * @see #fireProgressComplete
     * @see #fireProgressAlert
     * @see #addProgressListener
     * @see #removeProgressListener
     * @see #removeAllProgressListeners
     */
    protected void fireProgressCount(int nCount) {
        for (Enumeration e = progressListeners.elements(); e.hasMoreElements(); ) {
            DBProgressListener listener = (DBProgressListener) e.nextElement();
            listener.progressCount(nCount);
        }
    }

    /**
     * Notifies progress listeners about progress change.
     *
     * @see #fireProgressCount
     * @see #fireProgressComplete
     * @see #fireProgressAlert
     * @see #addProgressListener
     * @see #removeProgressListener
     * @see #removeAllProgressListeners
     */
    protected void fireProgressUpdate(int nIncrement, String sMessage) {
        for (Enumeration e = progressListeners.elements(); e.hasMoreElements(); ) {
            DBProgressListener listener = (DBProgressListener) e.nextElement();
            listener.progressUpdate(nIncrement, sMessage);
        }
    }

    /**
     * Notifies progress listeners about progress completion.
     *
     * @see #fireProgressCount
     * @see #fireProgressUpdate
     * @see #fireProgressAlert
     * @see #addProgressListener
     * @see #removeProgressListener
     * @see #removeAllProgressListeners
     */
    protected void fireProgressComplete() {
        for (Enumeration e = progressListeners.elements(); e.hasMoreElements(); ) {
            DBProgressListener listener = (DBProgressListener) e.nextElement();
            listener.progressComplete();
        }
    }

    /**
     * Notifies progress listeners about progress alert.
     *
     * @see #fireProgressCount
     * @see #fireProgressUpdate
     * @see #fireProgressComplete
     * @see #addProgressListener
     * @see #removeProgressListener
     * @see #removeAllProgressListeners
     * @see #removeAllProgressListeners
     */
    protected void fireProgressAlert(String sMessage) {
        for (Enumeration e = progressListeners.elements(); e.hasMoreElements(); ) {
            DBProgressListener listener = (DBProgressListener) e.nextElement();
            listener.progressAlert(sMessage);
        }
    }
}
