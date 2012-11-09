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

package com.compendium;

import java.net.*;
import java.util.*;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import com.compendium.core.*;
import com.compendium.ui.dialogs.UIStartUp;
import com.compendium.ui.ExecuteControl;
import com.compendium.ui.FormatProperties;
import com.compendium.ui.IUIConstants;
import com.compendium.ui.ProjectCompendiumFrame;
import com.compendium.ui.UIUtilities;
import com.compendium.ui.UIImages;
import com.compendium.io.http.HttpFileDownloadInputStream;
import com.compendium.meeting.MeetingManager;
import com.compendium.meeting.remote.RecordListener;


/**
 * ProjectCompendium is the main class for running the Project Compendium application.
 * It initialises the main JFrame and creates a new log file instance.
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

	/** The temporary directory of the system * */
	public static URI temporaryDirectory = null;

	/**
	 * Starts Project Compendium as an application
	 * 
	 * @param args Application arguments, currently none are handled
	 */
	public static void main(String [] args) {

		// MAKE SURE ALL EMPTY FOLDERS THAT SHOULD EXIST, DO
		checkDirectory("Exports");
		checkDirectory("Backups");
		checkDirectory("Linked Files");
		checkDirectory("Templates");
		checkDirectory("Movies");
		checkDirectory("System"+sFS+"resources"+sFS+"Logs");
		checkDirectory("System"+sFS+"resources"+sFS+"Databases");
		checkDirectory("System"+sFS+"resources"+sFS+"Meetings");

		try {
			Date date = new Date();
			sCompendiumInstanceID = (new Long(date.getTime()).toString());
			SaveOutput.start("System"+sFS+"resources"+sFS+"Logs"+sFS+"log_"+CoreCalendar.getCurrentDateStringFull()+".txt");
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}

		SystemProperties.loadProperties();
		LanguageProperties.loadProperties();

		// NEED TO LOAD PROPERTIES FIRST TO CHECK THIS FOLDER
		checkDirectory(SystemProperties.defaultPowerExportPath);

		String sTitle = SystemProperties.startUpTitle;
		int appname = sTitle.indexOf("<appname>");
		if (appname != -1) {
			sTitle = sTitle.substring(0, appname)+SystemProperties.applicationName+sTitle.substring(appname+9);
		}		
		UIStartUp oStartDialog = new UIStartUp(null, sTitle);
        oStartDialog.setLocationRelativeTo(oStartDialog.getParent());
		oStartDialog.setVisible(true);
		
		try {
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
		
		FormatProperties.loadProperties();
		if (FormatProperties.autoUpdateCheckerOn) {
			ProjectCompendium.checkForUpdates((JDialog)oStartDialog);
		}

		establishTempDirectory();
		
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
		APP = new ProjectCompendiumFrame(this, SystemProperties.applicationName, sServer, sIP, oStartDialog);
				
		// Fill all variables and draw the frame contents
		if (!APP.initialiseFrame()) {
			return;
		}
		
		// If there are any arguments passed, then setup memetic bits.
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
		
		//oStartDialog.setMessage(LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "ProjectCompendiumFrame.checkAutoLogin")); //$NON-NLS-1$		
		oStartDialog.setVisible(false);
		oStartDialog.dispose();

		// create the project compendium panel
		APP.setVisible(true);

		APP.showFloatingToolBars();
		
		// IF A DEFAULT DATABASE HAS BEEN SET, AND YOU ARE CONNECTING LOCALLY
		// TRY AND LOGIN AUTOMATICALLY
		// ELSE CHECK FOR VARIOUS SETTING AND DISPLAY THE APPROPRIATE INITIAL DIALOG OR PAGE
		if (FormatProperties.nDatabaseType == ICoreConstants.DERBY_DATABASE) {
			if (FormatProperties.defaultDatabase != null
					&& !FormatProperties.defaultDatabase.equals("") //$NON-NLS-1$
						&& APP.projectsExist()) {
				APP.autoFileOpen(FormatProperties.defaultDatabase);
			} else if (!APP.projectsExist() && SystemProperties.createDefaultProject) {
				APP.onFileNew();
			} else if (APP.projectsExist()) {
				APP.onFileOpen();
			} else {
				APP.showWelcome();
			}
		} else {
			if (APP.oCurrentMySQLConnection != null) {
				APP.getToolBarManager().selectProfile(APP.oCurrentMySQLConnection.getProfile());
				try {
					String sDefaultDatabase = APP.oCurrentMySQLConnection.getName();
					if (APP.oCurrentMySQLConnection.getServer().equals(ICoreConstants.sDEFAULT_DATABASE_ADDRESS)
								&& sDefaultDatabase != null
									&& !sDefaultDatabase.equals("")) { //$NON-NLS-1$

						APP.autoFileOpen(sDefaultDatabase);
					}
					else {
						APP.onFileOpen();
					}
				}
				catch(Exception ex) {
					APP.displayError(LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "ProjectCompendiumFrame.error1a")+" "+
							FormatProperties.sDatabaseProfile+LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "ProjectCompendiumFrame.error1b")+":\n\n"
							+ex.getMessage()+"\n\n"+
							LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "ProjectCompendiumFrame.error1c")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					APP.setDerbyDatabaseProfile();
				}
			}
			else {
				APP.setDerbyDatabaseProfile();
			}
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
	
	/**
	 * Check if the current version of Compendium being run here is out-of-date.
	 * Tell the user if it is and offer to link to download.
	 */
	public static void checkForUpdates(JDialog oStartDialog) {
		// check for software version
		try {
			// GET VERSION
			HttpFileDownloadInputStream stream = new HttpFileDownloadInputStream(new URL("http://compendium.open.ac.uk/institute/download/version.txt"));
			String version = stream.downloadToString();
			stream.close();
			if (CoreUtilities.isNewerVersion(version)) {
				// GET ADDITIONAL TEXT
				HttpFileDownloadInputStream stream2 = new HttpFileDownloadInputStream(new URL("http://compendium.open.ac.uk/institute/download/version-text.txt"));
				String blurb = stream2.downloadToString();
				stream2.close();
				
				JLabel label = new JLabel(UIImages.get(IUIConstants.COMPENDIUM_ICON_32));
				label.setHorizontalAlignment(SwingConstants.LEFT);
				
				final JCheckBox noShow = new JCheckBox(LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "ProjectCompendium.hideChecker")); //$NON-NLS-1$)
     			Object[] fields = {label, "\n"+LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "ProjectCompendium.checkVersionMessage1")+"\n\n"+
     					blurb+"\n", noShow}; //$NON-NLS-1$

     			final String okButton = LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "ProjectCompendium.downloadButton"); //$NON-NLS-1$
     			final String closeButton = LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "ProjectCompendium.closeButton"); //$NON-NLS-1$
     			Object[] options = {okButton, closeButton};

      			final JOptionPane optionPane = new JOptionPane(fields,
                                  JOptionPane.PLAIN_MESSAGE,
                                  JOptionPane.OK_CANCEL_OPTION,
                                  null,
                                  options,
                                  options[0]);
 
 				final JDialog dlg = new JDialog(oStartDialog, true);
		        optionPane.addPropertyChangeListener(new PropertyChangeListener() {
		        	
		        	public void propertyChange(PropertyChangeEvent e) {
		            	String prop = e.getPropertyName();
		            	boolean hideChecker = false;
		            	if ((e.getSource() == optionPane)
		                    && (prop.equals(JOptionPane.VALUE_PROPERTY) ||
		                       prop.equals(JOptionPane.INPUT_VALUE_PROPERTY))) {
		                    Object value = optionPane.getValue();
		                    
		                    if (value == JOptionPane.UNINITIALIZED_VALUE) {
		                        return;
		                    }
		                    
		                    if (value.equals(okButton)) {
		                    	try {
			                    	if (ExecuteControl.launchFile("http://compendium.open.ac.uk/institute/download/download.htm")) {
			                           	System.gc();
				                		System.exit(0);
				                   	} else {
				                   		System.out.println("Failed to launch");
				                   	}
		                    	} catch(Exception ex) {
		                    		System.out.println(ex.getLocalizedMessage());
		                    	}
		                    	hideChecker =  noShow.isSelected();
		                    } else if (value.equals(closeButton)) {
		                    	hideChecker = noShow.isSelected();
		                    }
							dlg.setVisible(false);
							dlg.dispose();
							
							if (hideChecker) {
								FormatProperties.autoUpdateCheckerOn = false;
								FormatProperties.setFormatProp( "autoUpdateCheckerOn", "false" ); //$NON-NLS-1$ //$NON-NLS-2$
								FormatProperties.saveFormatProps();
							}
		            	}
		        	}
		        });
				
				dlg.getContentPane().add(optionPane);
				dlg.pack();
				dlg.setSize(dlg.getPreferredSize());
				UIUtilities.centerComponent(dlg, oStartDialog);
				dlg.setVisible(true);
			} 
		} catch(Exception ex) {
			System.out.println(ex.getLocalizedMessage());
			ex.printStackTrace();
			System.out.flush();
		}	
	}

	/**
	 * Method to create a temporary directory for Compendium to use
	 * @author Sebastian Ehrich
	 */
	private void establishTempDirectory() {
		try {
			String tmp = System.getProperty("java.io.tmpdir");
			if (tmp == null)
				System.out.println("ProjectCompendium(): Could not determine system's default temporary directory, using internal defaults.");
			else
				// replace FS by '/' to create a valid URI
				// only Windows violates this by using '\' as FS 
				temporaryDirectory = new URI("file:///" + tmp.replaceAll("\\"+sFS, "/"));
		} 
		catch (URISyntaxException e1) {
			e1.printStackTrace();
			System.err.println("ProjectCompendium(): Could not create URI for default temporary directory.");
		}
		if (temporaryDirectory == null) {
			// if none exists use defaults
			try {
				if (ProjectCompendium.isWindows) {
					temporaryDirectory = new URI("file:///C:/WINDOWS/TEMP/");
				} 
				else {
					// MacOS && Linux
					temporaryDirectory = new URI("file:///var/tmp/");
				}
			} 
			catch (URISyntaxException e) {
				System.err.println("ProjectCompendium(): Could not create URI for internal temporary directory defaults.");
				e.printStackTrace();
			}
		}
	}
}
