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
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import java.util.Properties;
import java.util.UUID;

import javax.swing.JDialog;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.compendiumng.tools.Utilities;
import org.eclipse.jetty.util.log.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.compendium.core.ICoreConstants;
import com.compendium.ui.ExecuteControl;
import com.compendium.ui.FormatProperties;
import com.compendium.ui.ProjectCompendiumFrame;
import com.compendium.ui.dialogs.UIStartUp;

/**
 * ProjectCompendium is the main class for running the Project Compendium
 * application instance. Its' responsibilities are:
 *  - initializes the main JFrame
 *  - find configuration directory
 *  - load & merge or create configuration file
 *  - save merged configuration and delete new configuration that came with upgrade
 */
public class ProjectCompendium {
	
	/** logger for ProjectCompendium.class */
	static final Logger log = LoggerFactory.getLogger(ProjectCompendium.class);

	/** Reference to the main application frame */
	public static ProjectCompendiumFrame APP = null;

	/** The path to the current Compendium home folder. */
	public static String sHOMEPATH = (new File("")).getAbsolutePath();
	
	/** user home directory */
	public final static String USER_HOME = null; 
			// System.getProperty("user.home");
	public static String DIR_USER_SETTINGS = null;
	public final static PropertiesConfiguration Config = new PropertiesConfiguration(); 
	private static String MAIN_CONFIG = null;
			// DIR_USER_SETTINGS + File.separator + "main.properties";
	public static String DIR_BASE=null;
	public static String DIR_DATA=null;
	public static String DIR_EXPORT=null;
	public static String DIR_BACKUP=null;
	public static String DIR_PROJECT_TEMPLATES=null;
	public static String DIR_LINKED_FILES=null;
	public static String DIR_IMAGES=null;
	public static String DIR_REFERENCE_NODE_ICONS = null;
	public static String DIR_TEMPLATES=null;
	public static String DIR_HELP=null;
	public static String DIR_SKINS = null;
	
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
	 *            
	 * you can override application default configuration directory with -Dcompendiumng.config.dir=yourdir
	 * i.e.: -Dcompendiumng.config.dir="/home/michal/cng_configuration"
	 * CNG then looks for configuration files in that directory  
	 * @throws UnsupportedEncodingException 
	 */
	public static void main(String[] args) throws UnsupportedEncodingException {
		log.info("Starting {} version {}", ICoreConstants.sAPPNAME, ICoreConstants.sAPPVERSION);

		
		String props2list[] = {
				"compendiumng.config.dir",
				"java.version",
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
			log.info("java.properties(key={}) -> {}", props2list[i], System.getProperty(props2list[i], "*** UNDEFINED ***"));
		} 

		
		// config dir was passed from command line
		String passed_config_dir = (System.getProperty("compendiumng.config.dir"));
		
		if (passed_config_dir==null) {
			// no configuration directory passed derive so we need to calculate it
			//TODO: implement configuration directory auto calculation
		} else {
			DIR_USER_SETTINGS = passed_config_dir;
		}
		
		MAIN_CONFIG = DIR_USER_SETTINGS + File.separator + "main.properties";
		
		File config_file = new File(MAIN_CONFIG); 
		File config_file_new = new File(MAIN_CONFIG + ".new");

		log.info("Loading configuration from new configuration file: {}",
				config_file_new.getAbsolutePath());

		try {
			if (config_file_new.exists()) { // there is updated version of the main configuration file so we
											// have to load it first
				log.debug("new configuration file is present...loading");
				Config.load(config_file_new);
			}

			log.debug("old configuration file already exists... loading");
			if (config_file.exists()) { // there is also old version of the main configuration file that needs to be
										// updated
				Config.load(config_file);
			}

			// resulting merged version must be saved
			Config.setFile(config_file);
			Config.save();

			// new (already merged-in) configuration will be deleted on exit
			config_file_new.getAbsoluteFile().deleteOnExit();

		} catch (ConfigurationException e) {
			log.error("Failed to load configuration file from {}",
					config_file.getAbsolutePath());
			log.error("Exception", e);
		}

		InternetSearchAllowed = Config.getBoolean("internet.search.allowed",
				false);
		final String InternetSearchUrl =Config.getString("internet.search.url", "http://www.google.com/search?hl=en&lr=&ie=UTF-8&oe=UTF-8&q="); 
		log.info("Internet search allowed due to configuration option. URL = {}", InternetSearchUrl);
		
		if (InternetSearchAllowed) {
			InternetSearchProviderUrl = InternetSearchUrl;
		}
		
		// MAKE SURE ALL EMPTY FOLDERS THAT SHOULD EXIST, DO
		log.info("checking necessary directories...");

		// should be jar location
		String appdir = URLDecoder.decode(ProjectCompendium.class.getProtectionDomain().getCodeSource().getLocation().getPath(), "UTF-8");
		
		log.info("basedir pre-detected as: " + appdir);
		DIR_BASE = Config.getString("dir.base",  appdir);
		DIR_EXPORT = Config.getString("dir.export", DIR_BASE + File.separator + "Exports" + File.separator);
		DIR_BACKUP = Config.getString("dir.backup", DIR_BASE + File.separator + "Backups" + File.separator);
		DIR_DATA = Config.getString("dir.data", DIR_BASE + File.separator + "CompendiumNG-Data"+ File.separator);
		DIR_PROJECT_TEMPLATES = Config.getString("dir.project.templates", DIR_BASE + File.separator + "ProjectTemplates"+ File.separator);
		DIR_LINKED_FILES= Config.getString("dir.linked.files", DIR_DATA + File.separator + "LinkedFiles"+ File.separator);
		DIR_SKINS= Config.getString("dir.skins", DIR_BASE + File.separator + "Skins"+ File.separator);
		DIR_IMAGES = DIR_BASE + File.separator + "images" + File.separator;
		DIR_REFERENCE_NODE_ICONS = DIR_IMAGES + "ReferenceNodeIcons" + File.separator;
		
		
		if (isMac) {
			DIR_IMAGES = DIR_IMAGES + "Mac" + File.separator;
			DIR_REFERENCE_NODE_ICONS = DIR_REFERENCE_NODE_ICONS + "Mac" + File.separator;
		}
		
		DIR_TEMPLATES = Config.getString("dir.templates", DIR_BASE + File.separator + "Templates"+ File.separator);
		DIR_HELP = DIR_BASE + File.separator + "Help";
		
		
		checkDirectory(DIR_DATA);
		checkDirectory(DIR_BACKUP);
		checkDirectory(DIR_EXPORT);
		checkDirectory(DIR_PROJECT_TEMPLATES);
		checkDirectory(DIR_LINKED_FILES);
		checkDirectory(DIR_TEMPLATES);

		LanguageProperties.loadProperties();

		String sTitle = ICoreConstants.sAPPNAME;
		
		UIStartUp oStartDialog = new UIStartUp(null, sTitle);
		oStartDialog.setLocationRelativeTo(oStartDialog.getParent());
		oStartDialog.setVisible(true);

		try {
			ProjectCompendium app = new ProjectCompendium(oStartDialog, args);
		} catch (Throwable t) {
			log.error("Error while starting CompendiumNG...", t);
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

		FormatProperties.loadProperties();

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
		APP = new ProjectCompendiumFrame(this,ICoreConstants.sAPPNAME, sServer, sIP, oStartDialog);

		// Fill all variables and draw the frame contents
		if (!APP.initialiseFrame()) {
			return;
		}

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
			}  else if (APP.projectsExist()) {
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
	 */
	private void establishTempDirectory() {
		String random_unique_filename = "CNG-"+ UUID.randomUUID().toString();
		Path tmpPath = null;
		
		try {
			tmpPath = Files.createTempDirectory(random_unique_filename);
		} catch (IOException e) {
			log.error("Failed to create temporary directory: " + random_unique_filename);
		}

		temporaryDirectory  = tmpPath.toUri();
		log.debug("created temporary directory: " + temporaryDirectory);
		
		tmpPath.toFile().deleteOnExit();
	} 
	
	public static final Configuration getConfig() {
		return Config;
	}
}
