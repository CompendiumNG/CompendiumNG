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

import java.io.File;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.JDialog;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.compendiumng.tools.Utilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.compendium.core.ICoreConstants;
import com.compendium.ui.ExecuteControl;
import com.compendium.ui.FormatProperties;
import com.compendium.ui.ProjectCompendiumFrame;
import com.compendium.ui.dialogs.UIStartUp;

/**
 * ProjectCompendium is the main class for running the Project Compendium
 * application. It initialises the main JFrame and creates a new log file
 * instance.
 * 
 * @author Michelle Bachler
 */
public class ProjectCompendium {

	/** logger for ProjectCompendium.class */
	static final Logger log = LoggerFactory.getLogger(ProjectCompendium.class);

	/** Reference to the main application frame */
	public static ProjectCompendiumFrame APP = null;

	/** The path to the current Compendium home folder. */
	public static String sHOMEPATH = (new File("")).getAbsolutePath();
	
	/** user home directory */
	public final static String USER_HOME = System.getProperty("user.home");
	public final static String USER_SETTINGS_DIR = USER_HOME + File.separator + ".compendiumng";
	private final static PropertiesConfiguration Config = new PropertiesConfiguration(); 
	private final static String COCO_FILE = USER_SETTINGS_DIR + File.separator + "main.cfg";

	/** A reference to the system file path separator */
	public final static String sFS = System.getProperty("file.separator");

	/** A reference to the system platform */
	public static String platform = System.getProperty("os.name");

	/** The indicates the current system platform is Mac */
	public static boolean isMac = false;

	/** The indicates the current system platform is Windows */
	public static boolean isWindows = false;

	/** The indicates the current system platform is Linux */
	public static boolean isLinux = false;

	/** RMI instance id for Compendium used for memetic project. */
	public static String sCompendiumInstanceID = "";

	/** RMI Port number use for memetic project. */
	public static int nRMIPort = 1099;

	/** The temporary directory of the system * */
	public static URI temporaryDirectory = null;
	
	/** is internet search allowed ? */
	public static boolean InternetSearchAllowed= false;
	public static String InternetSearchProviderUrl = null;

	/**
	 * Starts Project Compendium as an application
	 * 
	 * @param args
	 *            Application arguments, currently none are handled
	 */
	public static void main(String[] args) {
		
		String props2list[] = {"java.version",
				"java.vm.version",
				"java.runtime.version", 
				"java.vendor",
				"sun.boot.class.path", 
				"java.ext.dirs",
				"java.vm.info",
				"sun.java.command", 
				"java.class.path",
				"user.timezone", 
				"user.home",
				"os.name",
				"os.version",
				"java.library.path", 
				"java.io.tmpdir", 
				"java.vm.name"
}; 

		for (int i =0; i<props2list.length; i++) {
			log.info("java.properties(key={}) -> {}", props2list[i], System.getProperties().getProperty(props2list[i], "*** UNDEFINED ***"));
		} 
		


		String localhostname = Utilities.GetHostname();

		log.info("Starting {} platform {} on: {}", ICoreConstants.sAPPNAME,
				platform, localhostname);
		
		File config_file = new File(COCO_FILE); 
		
		if (config_file.exists()) {
			log.info("Loading CompendiumNG configuration from: {}", config_file.getAbsolutePath());
			try {
				Config.load(config_file);
			} catch (ConfigurationException e) {
				log.error("Failed to load configuration file !");
			}
		} else {
			log.warn("Configuration file for CompendiumNG missing!  [{}]", COCO_FILE);
		}

		
		InternetSearchAllowed = Config.getBoolean("internet.search.allowed", false); 
		
		if (InternetSearchAllowed) {
			InternetSearchProviderUrl = ProjectCompendium.getConfig().getString("internet.search.url", "http://www.google.com/search?hl=en&lr=&ie=UTF-8&oe=UTF-8&q=");
		}
		
		// MAKE SURE ALL EMPTY FOLDERS THAT SHOULD EXIST, DO
		log.debug("checking necessary directories...");
		checkDirectory("Exports");
		checkDirectory("Backups");
		checkDirectory("Linked Files");
		checkDirectory("Templates");
		checkDirectory("Movies");
		checkDirectory("System" + sFS + "resources" + sFS + "Logs");
		checkDirectory("System" + sFS + "resources" + sFS + "Databases");
		checkDirectory("System" + sFS + "resources" + sFS + "Meetings");

		SystemProperties.loadProperties();
		LanguageProperties.loadProperties();
		

		// NEED TO LOAD PROPERTIES FIRST TO CHECK THIS FOLDER
		checkDirectory(SystemProperties.defaultPowerExportPath);

		String sTitle = SystemProperties.startUpTitle;
		int appname = sTitle.indexOf("<appname>");
		if (appname != -1) {
			sTitle = sTitle.substring(0, appname)
					+ SystemProperties.applicationName
					+ sTitle.substring(appname + 9);
		}
		UIStartUp oStartDialog = new UIStartUp(null, sTitle);
		oStartDialog.setLocationRelativeTo(oStartDialog.getParent());
		oStartDialog.setVisible(true);

		try {
			ProjectCompendium app = new ProjectCompendium(oStartDialog, args);
		} catch (Exception ex) {
			log.error("Error while starting Compendium...", ex);
		}
	}

	/**
	 * Check if a directory with the passed path exists, and if not create it.
	 * 
	 * @param String
	 *            sDirectory, the directory to check/create.
	 */
	private static void checkDirectory(String sDirectory) {
		log.info("checking: {}", sDirectory);
		File oDirectory = new File(sDirectory);
		if (!oDirectory.isDirectory()) {
			oDirectory.mkdirs();
			log.info("creating: {}", sDirectory);
		}
	}

	/**
	 * Constructor, creates a new project compendium application instance.
	 */
	public ProjectCompendium(UIStartUp oStartDialog, String[] args) {
		String os = platform.toLowerCase();
		if (os.indexOf("windows") != -1) {
			isWindows = true;
		} else if (os.indexOf("mac") != -1) {
			isMac = true;
		} else if (os.indexOf("linux") != -1) {
			isLinux = true;
		}

		FormatProperties.loadProperties();
		if (FormatProperties.autoUpdateCheckerOn) {
			ProjectCompendium.checkForUpdates((JDialog) oStartDialog);
		}

		establishTempDirectory();

		// Get the hostname and ip address of the current machine.
		String sServer = "";
		try {
			sServer = (InetAddress.getLocalHost()).getHostName();
		} catch (java.net.UnknownHostException e) {
		}

		String sIP = "";
		try {
			sIP = (InetAddress.getLocalHost()).getHostAddress();
		} catch (java.net.UnknownHostException e) {
			log.error("Exception...", e);
		}

		// Create main frame for the application
		APP = new ProjectCompendiumFrame(this,
				SystemProperties.applicationName, sServer, sIP, oStartDialog);

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
			for (int i = 0; i < count; i++) {
				next = args[i];
				if (next.startsWith("memetic-compendiuminstance")) {
					index = next.indexOf(":");
					if (index > -1) {
						sID = next.substring(index + 1);
					}
				} else if (next.startsWith("memetic-rmiport")) {
					index = next.indexOf(":");
					if (index > -1) {
						try {
							nPort = new Integer(next.substring(index + 1))
									.intValue();
						} catch (Exception e) {
							log.info("failed to load memetic rmi port from string = "
									+ next);
						}
					}
				} else if (next.startsWith("memetic-setup")) {
					sSetupData = next;
				} else if (next.startsWith("memetic-replay")) {
					sReplayData = next;
				} else if (next.startsWith("memetic-startrecording")) {
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
		// ELSE CHECK FOR VARIOUS SETTING AND DISPLAY THE APPROPRIATE INITIAL
		// DIALOG OR PAGE
		if (FormatProperties.nDatabaseType == ICoreConstants.DERBY_DATABASE) {
			if (FormatProperties.defaultDatabase != null
					&& !FormatProperties.defaultDatabase.equals("") //$NON-NLS-1$
					&& APP.projectsExist()) {
				APP.autoFileOpen(FormatProperties.defaultDatabase);
			} else if (!APP.projectsExist()
					&& SystemProperties.createDefaultProject) {
				APP.onFileNew();
			} else if (APP.projectsExist()) {
				APP.onFileOpen();
			} else {
				APP.showWelcome();
			}
		} else {
			if (APP.oCurrentMySQLConnection != null) {
				APP.getToolBarManager().selectProfile(
						APP.oCurrentMySQLConnection.getProfile());
				try {
					String sDefaultDatabase = APP.oCurrentMySQLConnection
							.getName();
					if (APP.oCurrentMySQLConnection.getServer().equals(
							ICoreConstants.sDEFAULT_DATABASE_ADDRESS)
							&& sDefaultDatabase != null
							&& !sDefaultDatabase.equals("")) { //$NON-NLS-1$

						APP.autoFileOpen(sDefaultDatabase);
					} else {
						APP.onFileOpen();
					}
				} catch (Exception ex) {
					log.error("Exception...", ex);
					APP.displayError(LanguageProperties.getString(
							LanguageProperties.UI_GENERAL_BUNDLE,
							"ProjectCompendiumFrame.error1a")
							+ " "
							+ FormatProperties.sDatabaseProfile
							+ LanguageProperties.getString(
									LanguageProperties.UI_GENERAL_BUNDLE,
									"ProjectCompendiumFrame.error1b")
							+ ":\n\n"
							+ ex.getMessage()
							+ "\n\n"
							+ LanguageProperties.getString(
									LanguageProperties.UI_GENERAL_BUNDLE,
									"ProjectCompendiumFrame.error1c")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					APP.setDerbyDatabaseProfile();
				}
			} else {
				APP.setDerbyDatabaseProfile();
			}
		}
	}

	/**
	 * Check if the current version of Compendium being run here is out-of-date.
	 * Tell the user if it is and offer to link to download.
	 */
	public static void checkForUpdates(JDialog oStartDialog) {
		//TODO: update checker disabled since it is not clear where we should check for new version for CompendiumNG
		//FIXME: replace update checker with something else (possibly p2 ?)
	}

	/**
	 * Method to create a temporary directory for Compendium to use
	 * 
	 * @author Sebastian Ehrich
	 */
	private void establishTempDirectory() {
		try {
			String tmp = System.getProperty("java.io.tmpdir");
			if (tmp == null)
				log.info("ProjectCompendium(): Could not determine system's default temporary directory, using internal defaults.");
			else
				// replace FS by '/' to create a valid URI
				// only Windows violates this by using '\' as FS
				temporaryDirectory = new URI("file:///"
						+ tmp.replaceAll("\\" + sFS, "/"));
		} catch (URISyntaxException e1) {
			log.error("ProjectCompendium(): Could not create URI for default temporary directory.", e1);
		}
		if (temporaryDirectory == null) {
			// if none exists use defaults
			try {
				if (ProjectCompendium.isWindows) {
					temporaryDirectory = new URI("file:///C:/WINDOWS/TEMP/");
				} else {
					// MacOS && Linux
					temporaryDirectory = new URI("file:///var/tmp/");
				}
			} catch (URISyntaxException e) {
				log.error("ProjectCompendium(): Could not create URI for internal temporary directory defaults.", e);
			}
		}
	}
	
	public static final Configuration getConfig() {
		return Config;
	}
}
