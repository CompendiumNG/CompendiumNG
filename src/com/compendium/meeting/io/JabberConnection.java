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

package com.compendium.meeting.io;

import java.util.Vector;
import java.util.Date;

import com.compendium.LanguageProperties;
import com.compendium.ProjectCompendium;

import com.compendium.core.ICoreConstants;
import com.compendium.core.datamodel.MediaIndex;
import com.compendium.core.datamodel.IModel;
import com.compendium.core.datamodel.View;
import com.compendium.core.datamodel.NodePosition;
import com.compendium.core.datamodel.NodeSummary;

import com.compendium.ui.*;

import com.compendium.ui.plaf.NodeUI;
import com.compendium.ui.plaf.ViewPaneUI;
import com.compendium.ui.plaf.ListUI;

import com.compendium.io.jabber.MeetingReplay;
import com.compendium.meeting.MeetingManager;

/**
 * This class manages a meeting recording session or a meeting replay.
 *
 * @author	Michelle Bachler
 * @version	1.0
 */
public class JabberConnection {

	/** A reference to the Jabber client for the Meeting Replay connection.*/
	private MeetingReplay oMeetingReplayClient = null;

	/** The MeetingManager in change of the Replay session.*/
	private MeetingManager oMeetingManager = null;

	/**
	 * Constructor. Create a new instance of JabberConnection.
	 */
	public JabberConnection(MeetingManager oMeetingManager)  {
		this.oMeetingManager = oMeetingManager;
	}

	/**
	 * Process the incoming message from Jabber (MeetingReplay) and act accordingly.
	 */
	public void processJabberMessage(String sMessage) {
		try {
			if (sMessage.startsWith("ViewingJabberNode=")) { //$NON-NLS-1$
				jumpToNode(sMessage);
			}
			if (sMessage.startsWith("CreateJabberNode=")) { //$NON-NLS-1$
				processAsMeetingReplayIndex(sMessage, oMeetingManager.getMeetingID());
			}
			else if (sMessage.startsWith("ForceUpdate=") || sMessage.startsWith("InformationalUpdate=")) { //$NON-NLS-1$ //$NON-NLS-2$
				String time = sMessage.substring(sMessage.indexOf("=")+1); //$NON-NLS-1$
				time = time.trim();
				long last_update_value = (new Long(time)).longValue();

				//System.out.println("New timestamp recieved = "+last_update_value);

				if (last_update_value > -1) {
					//System.out.println("About to store new timestamp from Meeting Replay");
					oMeetingManager.setCurrentOffset(last_update_value);
				}
			}
		}
		catch (NumberFormatException nfe) {
			System.out.println("Bad update value received.\n\n" + nfe.getMessage()); //$NON-NLS-1$
		}
	}

	/**
	 * Process the passed string as a Meeting Replay index string.
	 * Create a new answer node with the given video index timestamp.
	 *
	 * @param s, the string to process.
	 */
	public void processAsMeetingReplayIndex(String s, String sMeetingID) {

		UIViewFrame oUIViewFrame = ProjectCompendium.APP.getCurrentFrame();

		int nX = 0;
		int nY = 0;

		if (oUIViewFrame instanceof UIMapViewFrame) {
			nX = (oUIViewFrame.getWidth()/2)-60;
			nY = (oUIViewFrame.getHeight()/2)-60;

			// GET CURRENT SCROLL POSITION AND ADD THIS TO POSITIONING INFO
			int hPos = oUIViewFrame.getHorizontalScrollBarPosition();
			int vPos = oUIViewFrame.getVerticalScrollBarPosition();

			nX = nX + hPos;
			nY = nY + vPos;
		} else {
			UIListViewFrame oListViewFrame = (UIListViewFrame)oUIViewFrame;
			UIList oUIList = oListViewFrame.getUIList();

			nY = (oUIList.getNumberOfNodes() + 1) * 10;
			nX = 0;
		}

		processAsMeetingReplayIndex(s, nX, nY, sMeetingID);
	}

	/**
	 * Process the passed string as a Meeting Replay index string.
	 * Create a new answer node with the given video index timestamp.
	 *
	 * @param s the string to process.
	 * @param nX the x position on the current view to create the new node.
	 * @param nY the y position on the current view to create the new node.
	 */
	public void processAsMeetingReplayIndex(String s, int nX, int nY, String sMeetingID) {
		int ind = s.indexOf("="); //$NON-NLS-1$
		String sVideoIndex = "0"; //$NON-NLS-1$
		NodeSummary oNode = null;
		NodePosition oPos = null;
		View oView = null;
		IModel model = ProjectCompendium.APP.getModel();

		if (ind > -1) {
			sVideoIndex = s.substring(ind+1);
			Date dIndex = new Date((new Double(sVideoIndex).longValue()));
			long lIndex = dIndex.getTime();

			UIViewFrame oUIViewFrame = ProjectCompendium.APP.getCurrentFrame();
			oView = oUIViewFrame.getView();

			// NEED THE EVENTS TO BE CREATED WITH THE CORRECT MEDIA INDEX WHEN NODES CREATED IN A MOMENT.
			long lCurrentOffsetTime = ProjectCompendium.APP.oMeetingManager.getCurrentOffset();
			ProjectCompendium.APP.oMeetingManager.setCurrentOffset(lIndex);

			if (oUIViewFrame instanceof UIMapViewFrame) {
				UIMapViewFrame oMapViewFrame = (UIMapViewFrame)oUIViewFrame;
				UIViewPane oUIViewPane = oMapViewFrame.getViewPane();
				ViewPaneUI oViewPaneUI = oUIViewPane.getUI();

				UINode node = oViewPaneUI.addNewNode(ICoreConstants.POSITION, nX, nY);
				oNode = node.getNode();
				oPos = node.getNodePosition();
			} else {
				UIListViewFrame oListViewFrame = (UIListViewFrame)oUIViewFrame;
				UIList oUIList = oListViewFrame.getUIList();
				ListUI oListUI = oUIList.getListUI();
				oPos = oListUI.createNode(ICoreConstants.POSITION, "", //$NON-NLS-1$
												ProjectCompendium.APP.getModel().getUserProfile().getUserName(),
													"", "", nX, nY); //$NON-NLS-1$ //$NON-NLS-2$
				oNode = oPos.getNode();
			}

			// RESET THE MEDIA INDEX
			ProjectCompendium.APP.oMeetingManager.pauseCurrentOffsetUpdate(false);
			ProjectCompendium.APP.oMeetingManager.setCurrentOffset(lCurrentOffsetTime);

			try {
				MediaIndex oMediaIndex = new MediaIndex(oView.getId(), oNode.getId(), sMeetingID, dIndex, new Date(), new Date());
				oMediaIndex.initialize(model.getSession(), model);
				model.getMeetingService().createMediaIndex(model.getSession(), oMediaIndex);
				oPos.setMediaIndex(sMeetingID, oMediaIndex);
			} catch(Exception ex) {
				ex.printStackTrace();
				ProjectCompendium.APP.displayError(LanguageProperties.getString(LanguageProperties.MEETING_BUNDLE, "JabberConnection.error1")+":\n\n"+ex.getLocalizedMessage()); //$NON-NLS-1$
			}
		}
	}

	/**
	 * Process the passed message and jump the the view and node given.
	 *
	 * @param ssMessage the jabber message to process.
	 */
	public void jumpToNode(String sMessage) {
		
		int index1 = sMessage.indexOf("="); //$NON-NLS-1$
		sMessage = sMessage.substring(index1+1);

		int index = sMessage.indexOf("/"); //$NON-NLS-1$
		if (index != -1) {

			String sViewID = sMessage.substring(0, index);
			String sNodeID = sMessage.substring(index+1);

			UIUtilities.jumpToNode(sViewID, sNodeID, "Meeting Replay");			 //$NON-NLS-1$
		}
		else {
			System.out.println("Incorrect syntax for MeetingReplay request to jump to node: "+sMessage); //$NON-NLS-1$
		}
	}

	/**
	 * Open a Jabber Meeting Replay connection for the given details.
	 *
	 * @param server the jabber server to connect to.
	 * @param username the username of the account to connect to.
	 * @param password the password to use to connect.
	 */
	public void openMeetingReplayConnection(String sServer, String sUsername, String sPassword, String sResource,
									/*String sMediaServer, String sMediaUsername, String sMediaPassword,
									String sMediaResource,*/ String roomJIDString	) {
		if (oMeetingReplayClient == null) {
			oMeetingReplayClient = new MeetingReplay(roomJIDString);
			oMeetingReplayClient.connect(sServer, sUsername, sPassword, sResource);
		} else {
			oMeetingReplayClient.disconnect();
			closeMeetingReplayConnection();
		}
	}

	/**
	 * Send the given node in the current view to the Meeting Replay Jabber account.
	 *
	 * @param jid the jabber id of the Jabber account to send the nodes to.
	 */
	public void sendMeetingReplay(NodeUI nodeui, String sMeetingID) {
		if (oMeetingReplayClient != null) {
			NodePosition nodePos = nodeui.getUINode().getNodePosition();

			// FIX FOR SITUATION WHERE NODEPOSITION BEING RECREATED.
			IModel model = ProjectCompendium.APP.getModel();
			nodePos.initialize(model.getSession(), model);
			MediaIndex index = nodePos.getMediaIndex(sMeetingID);

			if (index != null) {
				Date timestamp = index.getMediaIndex();
				oMeetingReplayClient.sendMessageToRoom( "CompendiumUpdate="+timestamp.getTime() ); //$NON-NLS-1$
			} else {
				ProjectCompendium.APP.displayMessage(LanguageProperties.getString(LanguageProperties.MEETING_BUNDLE, "JabberConnection.errorNoIndex"), LanguageProperties.getString(LanguageProperties.MEETING_BUNDLE, "JabberConnection.erroNoIndexTitle")); //$NON-NLS-1$ //$NON-NLS-2$
			}
		}
	}

	/**
	 * Close the Jabber Meeting Replay connection if open.
	 */
	public void closeMeetingReplayConnection() {
		if (oMeetingReplayClient != null) {
			oMeetingReplayClient.destroy();
			oMeetingReplayClient = null;
		}
	}

	/**
	 * Return if an Jabber Meeting Replay connection is open.
	 * @return true if the connection if open, else false.
	 */
	public boolean isMeetingReplayConnected() {
		if (oMeetingReplayClient != null) {
			return true;
		}
		return false;
	}
}
