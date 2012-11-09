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


package com.compendium;

import java.net.*;
import java.util.*;
import java.io.File;

import com.compendium.core.*;
import com.compendium.ui.dialogs.UIStartUp;
import com.compendium.ui.ProjectCompendiumFrame;
import com.compendium.meeting.MeetingManager;
import com.compendium.meeting.remote.RecordListener;


/**
 * ProjectCompendium is the main class for running the Project Compendium application.
 * It initalises the main JFrame and creates a new logfile instance.
 *
 * @author	Michelle Bachler
  */
public class ProjectCompendium {

	/** Reference to the main application frame */
	public static ProjectCompendiumFrame APP = null;

	/** The path to the current Compendium home folder.*/
	public static String 		sHOMEPATH	= (new File("")).getAbsolutePath();

	/** A reference to the system file path separator*/
	public final static String	sFS		= System.getProperty("file.separator");

	/** A reference to the system platform */
	public static String platform = System.getProperty("os.name");

	/** The indicates the current system platform is Mac*/
	public static boolean isMac = false;

	/** The indicates the current system platform is Windows*/
	public static boolean isWindows = false;

	/** The indicates the current system platform is Linux*/
	public static boolean isLinux = false;

	/** RMI instance id for Compendium used for memetic project.*/
	public static String sCompendiumInstanceID = "";

	/** RMI Port number use for memetic project.*/
	public static int nRMIPort = 1099;

	/** Instance of the RMI listener for memetic web start stuff.*/
	public static RecordListener oRecordListener = null;

	/**
	 * Starts Project Compendium as an application
	 *
	 * @param args Application arguments, currently none are handled
	 */
	public static void main(String [] args) {

		UIStartUp oStartDialog = new UIStartUp(null);
        oStartDialog.setLocationRelativeTo(oStartDialog.getParent());
		oStartDialog.setVisible(true);

		// MAKE SURE ALL EMPTY FOLDERS THAT SHOULD EXIST, DO
		checkDirectory("Exports");
		checkDirectory("Backups");
		checkDirectory("Linked Files");
		checkDirectory("Templates");
		checkDirectory("System"+sFS+"resources"+sFS+"Logs");
		checkDirectory("System"+sFS+"resources"+sFS+"Databases");
		checkDirectory("System"+sFS+"resources"+sFS+"Meetings");

		try {
			Date date = new Date();
			sCompendiumInstanceID = (new Long(date.getTime()).toString());
			SaveOutput.start("System"+sFS+"resources"+sFS+"Logs"+sFS+"log_"+CoreCalendar.getCurrentDateStringFull()+".txt");
			ProjectCompendium app = new ProjectCompendium(oStartDialog, args);
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Check if a directory with the passed path exists, and if not create it.
	 * @param String sDirectory, the directory to check/create.
	 */
	private static void checkDirectory(String sDirectory) {
		File oDirectory = new File(sDirectory);
		if (!oDirectory.isDirectory()) {
			oDirectory.mkdirs();
		}
	}

	/**
	 * Constructor, creates a new project compendium application instance.
	 */
	public ProjectCompendium(UIStartUp oStartDialog, String [] args) {

		String os = platform.toLowerCase();
		if (os.indexOf("windows") != -1) {
		    isWindows = true;
		}
		else if (os.indexOf("mac") != -1) {
		    isMac = true;
		}
		else if (os.indexOf("linux") != -1) {
		    isLinux = true;
		}

		// Get the hostname and ip address of the current machine.
		String sServer = "";
		try {
			sServer = (InetAddress.getLocalHost()).getHostName();
		}
		catch(java.net.UnknownHostException e) {}

		String sIP = "";
		try {
			sIP = (InetAddress.getLocalHost()).getHostAddress();
		}
		catch(java.net.UnknownHostException e) {
			System.out.println("Exception: UnknownHost\n\n"+e.getMessage());
		}

		// Create main frame for the application
		APP = new ProjectCompendiumFrame(this, ICoreConstants.sAPPNAME, sServer, sIP, oStartDialog);

		// Fill all variables and draw the frame contents
		if (!APP.initialiseFrame()) {
			return;
		}

		String sReplayData = "";
		String sSetupData = "";
        boolean startRecording = false;

		int count = args.length;

		if (count > 0) {

			int nPort = 0;
			String sID = "";
			String next = "";
			int index = 0;
			for (int i=0; i<count; i++) {
				next = args[i];
				if (next.startsWith("memetic-compendiuminstance")) {
					index = next.indexOf(":");
					if (index> -1) {
						sID = next.substring(index+1);
					}
				}
				else if (next.startsWith("memetic-rmiport")) {
					index = next.indexOf(":");
					if (index> -1) {
						try {
							nPort = new Integer(next.substring(index+1)).intValue();
						}
						catch(Exception e) {
							System.out.println("failed to load memetic rmi port from string = "+next);
						}
					}
				}
				else if (next.startsWith("memetic-setup")) {
					sSetupData = next;
				}
				else if (next.startsWith("memetic-replay")) {
					sReplayData = next;
				}
                else if (next.startsWith("memetic-startrecording")) {
                    startRecording = true;
                }
			}

			if (nPort > 0) {
				nRMIPort = nPort;
			}
			if (!sID.equals("")) {
				sCompendiumInstanceID = sID;
			}
		}
		
		oStartDialog.setVisible(false);
		oStartDialog.dispose();

		// create the project compendium panel
		APP.setVisible(true);

		APP.showFloatingToolBars();
		if (APP.isFirstTime())
			APP.onFileNew();
		else if (APP.shouldOpenFile()) {
			APP.onFileOpen();
		}

		if (!sSetupData.equals("")) {
			if (!sReplayData.equals("")) {
				APP.setupForReplay(sSetupData, sReplayData);
                if (startRecording) {
                    APP.oMeetingManager.startReplayRecording();
                }
			}
			else {
				APP.setupForRecording(sSetupData);
                if (startRecording) {
                    APP.oMeetingManager.startRecording();
                }
			}
		} else {
            try {
                APP.oMeetingManager = new MeetingManager(MeetingManager.RECORDING);
                APP.oMeetingManager.reloadAccessGridData();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
	}
}
