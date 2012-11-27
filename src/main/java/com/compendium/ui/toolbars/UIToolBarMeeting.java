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

package com.compendium.ui.toolbars;

import java.awt.event.*;
import java.io.*;
import java.util.*;

import javax.help.*;
import javax.swing.*;

import com.compendium.core.*;
import com.compendium.*;
import com.compendium.ui.*;
import com.compendium.ui.toolbars.system.*;
import com.compendium.meeting.*;
import com.compendium.core.datamodel.*;


/**
 * This class manages all the toolbars
 *
 * @author	Michelle Bachler
 * @version	1.0
 */
public class UIToolBarMeeting implements IUIToolBar, ActionListener, IUIConstants {
	
	/** Indicates whether the node format toolbar is switched on or not by default.*/
	private final static boolean DEFAULT_STATE			= false;
	
	/** Indicates the default orientation for this toolbars ui object.*/
	private final static int DEFAULT_ORIENTATION		= SwingConstants.HORIZONTAL;	
	
	/** This indicates the type of the toolbar.*/
	private	int 					nType			= -1;	
	
	/** The parent frame for this class.*/
	private ProjectCompendiumFrame	oParent			= null;
	
	/** The overall toolbar manager.*/
	private IUIToolBarManager 		oManager		= null;

	/** The meeting toolbar.*/
	private UIToolBar			tbrToolBar 		= null;

	/** The button to start the meeting session.*/
	private JButton				pbStartMeeting		= null;

	/** The button to pause the meeting recording.*/
	private JButton				pbPauseMeeting		= null;

	/** The button to stop the meeting session.*/
	private JButton				pbStopMeeting		= null;

	/** The button to reset the meeting recordong.*/
	private JButton				pbResetMeeting		= null;

	/** The button to upload meeting data.*/
	private JButton				pbUploadMeeting		= null;		
	
	/**
	 * Create a new instance of UIToolBarMeeting, with the given properties.
	 * @param oManager the IUIToolBarManager that is managing this toolbar.
	 * @param parent the parent frame for the application.
	 * @param orientation the orientation of this toolbars ui object.   
	 */
	public UIToolBarMeeting(IUIToolBarManager oManager, ProjectCompendiumFrame parent, int nType) {

		this.oParent = parent;
		this.oManager = oManager;
		this.nType = nType;
		createToolBar(DEFAULT_ORIENTATION);
	}	
	
	/**
	 * Create a new instance of UIToolBarMeeting, with the given properties.
	 * @param oManager the IUIToolBarManager that is managing this toolbar.
	 * @param parent the parent frame for the application.
	 * @param nType the unique identifier for this toolbar.
	 * @param orientation the orientation of this toolbars ui object.   
	 */
	public UIToolBarMeeting(IUIToolBarManager oManager, ProjectCompendiumFrame parent, int nType, int orientation) {

		this.oParent = parent;
		this.oManager = oManager;
		this.nType = nType;
		createToolBar(orientation);
	}

	/**
	 * Update the look and feel of all the toolbars
	 */
	public void updateLAF() {
		if (tbrToolBar != null) {
			SwingUtilities.updateComponentTreeUI(tbrToolBar);
		}
	}

	/**
	 * Create and return the toolbar with all the meeting recorder options.
	 * @return UIToolBar, the toolbar with all the meeting recorder options.
	 */
	private UIToolBar createToolBar(int orientation) {

		tbrToolBar = new UIToolBar(LanguageProperties.getString(LanguageProperties.TOOLBARS_BUNDLE, "UIToolBarMeeting.meetingToolbar")); //$NON-NLS-1$
		tbrToolBar.setOrientation(orientation);

		pbStartMeeting = tbrToolBar.createToolBarButton(LanguageProperties.getString(LanguageProperties.TOOLBARS_BUNDLE, "UIToolBarMeeting.startRecording"), UIImages.get(RECORD_ICON)); //$NON-NLS-1$
		pbStartMeeting.addActionListener(this);
		pbStartMeeting.setEnabled(true);
		tbrToolBar.add(pbStartMeeting);
		CSH.setHelpIDString(pbStartMeeting,"toolbars.meeting"); //$NON-NLS-1$

		pbPauseMeeting = tbrToolBar.createToolBarButton(LanguageProperties.getString(LanguageProperties.TOOLBARS_BUNDLE, "UIToolBarMeeting.pauseRecording"), UIImages.get(PAUSE_ICON)); //$NON-NLS-1$
		pbPauseMeeting.addActionListener(this);
		pbPauseMeeting.setEnabled(false);
		pbPauseMeeting.setActionCommand("pause"); //$NON-NLS-1$
		tbrToolBar.add(pbPauseMeeting);
		CSH.setHelpIDString(pbPauseMeeting,"toolbars.meeting"); //$NON-NLS-1$

		pbStopMeeting = tbrToolBar.createToolBarButton(LanguageProperties.getString(LanguageProperties.TOOLBARS_BUNDLE, "UIToolBarMeeting.stopRecording"), UIImages.get(STOP_ICON)); //$NON-NLS-1$
		pbStopMeeting.addActionListener(this);
		pbStopMeeting.setEnabled(false);
		tbrToolBar.add(pbStopMeeting);
		CSH.setHelpIDString(pbStopMeeting,"toolbars.meeting"); //$NON-NLS-1$

		pbResetMeeting = tbrToolBar.createToolBarButton(LanguageProperties.getString(LanguageProperties.TOOLBARS_BUNDLE, "UIToolBarMeeting.resetRecording"), UIImages.get(RESET_ICON)); //$NON-NLS-1$
		pbResetMeeting.addActionListener(this);
		pbResetMeeting.setEnabled(false);
		tbrToolBar.add(pbResetMeeting);
		CSH.setHelpIDString(pbResetMeeting, "toolbars.meeting"); //$NON-NLS-1$

		pbUploadMeeting = tbrToolBar.createToolBarButton(LanguageProperties.getString(LanguageProperties.TOOLBARS_BUNDLE, "UIToolBarMeeting.uploadData"), UIImages.get(UPLOAD_ICON)); //$NON-NLS-1$
		pbUploadMeeting.addActionListener(this);
		pbUploadMeeting.setEnabled(true);
		tbrToolBar.add(pbUploadMeeting);
		CSH.setHelpIDString(pbUploadMeeting,"toolbars.meeting"); //$NON-NLS-1$

		return tbrToolBar;
	}

	/**
	 * Handles most menu and toolbar action event for this application.
	 *
	 * @param evt the genereated action event to be handled.
	 */
	public void actionPerformed(ActionEvent evt) {

		oParent.setWaitCursor();

		Object source = evt.getSource();

		if (source.equals(pbStartMeeting)) {
			startMeeting();
		}
		else if (source.equals(pbPauseMeeting)) {
			if (oParent.oMeetingManager != null) {
				if (pbPauseMeeting.getActionCommand().equals("pause")) { //$NON-NLS-1$
					pbPauseMeeting.setIcon(UIImages.get(RESUME_ICON));
					pbPauseMeeting.setToolTipText(LanguageProperties.getString(LanguageProperties.TOOLBARS_BUNDLE, "UIToolBarMeeting.resumeRecording")); //$NON-NLS-1$
					pbPauseMeeting.setActionCommand("resume"); //$NON-NLS-1$
					pbStopMeeting.setEnabled(false);
					oParent.oMeetingManager.pauseRecording();
				}
				else {
					oParent.oMeetingManager.resumeRecording();
					pbPauseMeeting.setIcon(UIImages.get(PAUSE_ICON));
					pbPauseMeeting.setToolTipText(LanguageProperties.getString(LanguageProperties.TOOLBARS_BUNDLE, "UIToolBarMeeting.pauseRecording")); //$NON-NLS-1$
					pbStopMeeting.setEnabled(true);
					pbPauseMeeting.setActionCommand("pause"); //$NON-NLS-1$
				}
			}
			else {
				oParent.displayError(LanguageProperties.getString(LanguageProperties.TOOLBARS_BUNDLE, "UIToolBarMeeting.recordingNotStarted")); //$NON-NLS-1$
			}
		}
		else if (source.equals(pbStopMeeting)) {
			if (oParent.oMeetingManager != null) {
				Thread thread = new Thread("UIMeetingReplayDialog-1") { //$NON-NLS-1$
					public void run() {
						ProjectCompendium.APP.setWaitCursor();

						if (oParent.oMeetingManager.isReplay()) {
							oParent.oMeetingManager.stopReplayRecording();
						}
						else {
							oParent.oMeetingManager.stopRecording();
						}
						ProjectCompendium.APP.setDefaultCursor();
					}
				};
				thread.start();
			}
			else {
				oParent.displayError(LanguageProperties.getString(LanguageProperties.TOOLBARS_BUNDLE, "UIToolBarMeeting.recordingNotStarted")); //$NON-NLS-1$
			}
		}
		else if (source.equals(pbUploadMeeting)) {
			try {
				MeetingManager oMeetingManager = oParent.oMeetingManager;
				if (oParent.oMeetingManager == null) {
					oMeetingManager = new MeetingManager();
				}
				oMeetingManager.uploadRecording();
			} catch (AccessGridDataException ex) {
				oParent.displayError(ex.getMessage());
			}
		}

		oParent.setDefaultCursor();
	}

	/**
	 * Updates the menu when a new database project is opened.
	 */
	public void onDatabaseOpen() {

		if (tbrToolBar != null) {
			tbrToolBar.setEnabled(true);
			pbStartMeeting.setEnabled(true);
			pbPauseMeeting.setEnabled(false);
			pbStopMeeting.setEnabled(false);
			pbUploadMeeting.setEnabled(true);
		}
	}

	/**
	 * Updates the menu when the current database project is closed.
	 */
	public void onDatabaseClose() {
		if (tbrToolBar != null) {
			tbrToolBar.setEnabled(false);
		}
	}

	/**
	 * Determine if the current map is a meeting map, and
	 * determine which sort pre-recorded/recorded.
	 * Start recording/replaying if possible else open appropriate dialog.
	 */
	private void startMeeting() {

		UIViewFrame frame = ProjectCompendium.APP.getCurrentFrame();
		View view = frame.getView();
		boolean bOpenDialog = true;

		if (frame instanceof UIMapViewFrame) {
			IModel model = oParent.getModel();
			
			Meeting meeting = null;
			try {
				meeting = model.getMeetingService().getMeetingForMap(model.getSession(), view.getId());
			}
			catch(Exception ex) {}

			if (meeting != null) {
				int nStatus = meeting.getStatus();
				if (nStatus == ICoreConstants.STATUS_PREPARED) {
					bOpenDialog = false;
					try {
						oParent.oMeetingManager = new MeetingManager(MeetingManager.RECORDING);
						oParent.oMeetingManager.setMeeting(meeting);
						oParent.oMeetingManager.startRecording();
					} catch (AccessGridDataException ex) {
						oParent.displayError(ex.getMessage());
						return;
					}
				}
				else {
					bOpenDialog = false;

					String sPath = "System"+ProjectCompendium.sFS+"resources"+ProjectCompendium.sFS+UIMeetingReplayDialog.PROPERTY_FILE; //$NON-NLS-1$ //$NON-NLS-2$
					File optionsFile = new File(sPath);
					Properties connectionProperties = new Properties();
					boolean bOpenReplayDialog = true;
					if (optionsFile.exists()) {
						try {
							connectionProperties.load(new FileInputStream(sPath));
							String server = connectionProperties.getProperty("mediacompserver"); //$NON-NLS-1$
							String username = connectionProperties.getProperty("mediacompusername"); //$NON-NLS-1$
							String password = connectionProperties.getProperty("mediacomppassword"); //$NON-NLS-1$
							String roomServer = connectionProperties.getProperty("mediaroomserver"); //$NON-NLS-1$
							String resource = connectionProperties.getProperty("mediacompresource"); //$NON-NLS-1$

							if (server != null && username != null && password != null && roomServer != null) {
								try {
									oParent.oMeetingManager = new MeetingManager(MeetingManager.REPLAY);
									oParent.oMeetingManager.setMeeting(meeting);
									bOpenReplayDialog = false;
									oParent.oMeetingManager.openMeetingReplayConnection(server, username, password, resource, roomServer);
								} catch (AccessGridDataException ex) {
									oParent.displayError(ex.getMessage());
									return;
								}
							}
							else {
								bOpenReplayDialog = true;
							}
						}
						catch (IOException e) {
							e.printStackTrace();
							bOpenReplayDialog = true;
						}
					}

					if (bOpenReplayDialog) {
						try {
							oParent.oMeetingManager = new MeetingManager(MeetingManager.REPLAY);
							UIMeetingReplayDialog dlg = new UIMeetingReplayDialog(oParent.oMeetingManager);
							dlg.setVisible(true);
						} catch (AccessGridDataException ex) {
							oParent.displayError(ex.getMessage());
							return;
						}
					}
				}
			}
		}

		if (bOpenDialog) {
			try {
				oParent.oMeetingManager = new MeetingManager(MeetingManager.RECORDING);
				UIMeetingRecorderDialog dlg = new UIMeetingRecorderDialog(oParent.oMeetingManager);
				dlg.setVisible(true);
			} catch (AccessGridDataException ex) {
				oParent.displayError(ex.getMessage());
				return;
			}
		}
	}

	/**
 	 * Does Nothing
 	 * @param selected true to enable, false to disable.
	 */
	public void setNodeSelected(boolean selected) {}
		
	/**
 	 * Does Nothing
 	 * @param selected true to enable, false to disable.
	 */
	public void setNodeOrLinkSelected(boolean selected) {}
	
	public UIToolBar getToolBar() {
		return tbrToolBar;
	}
	
	/**
	 * Enable/disable the toolbar.
	 * @param enabled true to enable, false to disable.
	 */
	public void setEnabled(boolean enabled) {
		tbrToolBar.setEnabled(enabled);
		//pbStartMeeting.setEnabled(!enabled);
		//pbPauseMeeting.setEnabled(enabled);
		//pbStopMeeting.setEnabled(enabled);		
	}	
	
	/**
	 * Return true if this toolbar is active by default, or false if it must be switched on by the user.
	 * @return true if the toolbar is active by default, else false.
	 */
	public boolean getDefaultActiveState() {
		return DEFAULT_STATE;
	}	
	
	/**
	 * Return a unique integer identifier for this toolbar.
	 * @return a unique integer identifier for this toolbar.
	 */
	public int getType() {
		return nType;
	}		
}
