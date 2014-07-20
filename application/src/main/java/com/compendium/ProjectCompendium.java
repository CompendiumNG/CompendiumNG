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

import com.compendium.core.ICoreConstants;
import com.compendium.ui.FormatProperties;
import com.compendium.ui.ProjectCompendiumFrame;
import com.compendium.ui.dialogs.UIStartUp;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.derby.tools.sysinfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.io.*;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.*;
import java.util.UUID;

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
	private static final Logger log = LoggerFactory.getLogger(ProjectCompendium.class);

	/** Reference to the main application frame */
	public static ProjectCompendiumFrame APP = null;

	/** The path to the current Compendium home folder. */
//	public static String sHOMEPATH = (new File("")).getAbsolutePath();
	
	public static String DIR_USER_SETTINGS = null;
	public final static PropertiesConfiguration Config = new PropertiesConfiguration(); 
	private static String MAIN_CONFIG = null;
	public static String DIR_DATA=null;
	public static String DIR_EXPORT=null;
	private static String DIR_BACKUP=null;
	public static String DIR_PROJECT_TEMPLATES=null;
	private static String DIR_LINKED_FILES=null;
	public static String DIR_IMAGES=null;
	public static String DIR_REFERENCE_NODE_ICONS = null;
	private static String DIR_TEMPLATES=null;
	public static String DIR_HELP=null;
	public static String DIR_SKINS = null;
	public static String DIR_BASE= null;
	public static String DIR_LOCALE= null;
	public static String DIR_STENCILS= null;
	public static String DIR_IMAGES_TOOLBARS= null;
	public static String DIR_DOC= null;
	private final static String DIR_USER_HOME = System.getProperty("user.home") + File.separator;
	
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

		String dir_base_override = System.getProperty("compendiumng.base.dir");
		
		// dir_base is the directory where is the CompendiumNG jar located
		// usually it is auto-calculated however this is the way how to override it i.e. for debugging purposes
		if (dir_base_override!=null) {
			DIR_BASE = dir_base_override;
		} else {
					
			URI uri = null;
			try {
				uri = ProjectCompendium.class.getProtectionDomain().getCodeSource().getLocation().toURI();
			} catch (URISyntaxException e) {
				log.error("Unable to determine base directory during startup! you must set it manually via configuration file!");
				System.exit(9);
			}

			Path path = Paths.get(uri);
			if (Files.isRegularFile(path, LinkOption.NOFOLLOW_LINKS)) { // 
				DIR_BASE = path.normalize().getParent().toAbsolutePath().toString();
			} else if (Files.isDirectory(path, LinkOption.NOFOLLOW_LINKS)){
				DIR_BASE = path.normalize().toAbsolutePath().toString();
			} else {
				log.error("Unable to determine base directory during startup! you must set it manually via configuration file!");
				System.exit(8);
			}
		}
		
		DIR_BASE += File.separator;
		log.info("application base directory: " + DIR_BASE);
		
		if (passed_config_dir==null) {
			// no configuration directory passed so first we try to find it next to the binary in case it is portable installation
			// then we should expect one in users' home directory
			// if none is found then we set it to user home directory and eventually create it
			
			Path p1 = Paths.get(DIR_BASE + "compendiumng_config");
			Path p2 = Paths.get(DIR_USER_HOME + "compendiumng_config");
			
			if (Files.exists(p1, LinkOption.NOFOLLOW_LINKS) && Files.isDirectory(p1, LinkOption.NOFOLLOW_LINKS)) {
				log.info("found configuration directory {} ", p1);
				DIR_USER_SETTINGS = p1.toString();
			} else if (Files.exists(p2, LinkOption.NOFOLLOW_LINKS) && Files.isDirectory(p2, LinkOption.NOFOLLOW_LINKS)) {
				log.info("found configuration directory {} ", p2);
				DIR_USER_SETTINGS = p2.toString();
			} else {
				DIR_USER_SETTINGS = p2.toString();
			}
		} else {
			DIR_USER_SETTINGS = passed_config_dir;
		}
		
		if (!DIR_USER_SETTINGS.endsWith(File.separator)) {
			DIR_USER_SETTINGS += File.separator;
		}
		
		
		if (!Paths.get(DIR_USER_SETTINGS).toFile().exists()) {
			try {
				log.info("Creating user settings directory: {}",
						DIR_USER_SETTINGS);
				Files.createDirectory(Paths.get(DIR_USER_SETTINGS));
			} catch (IOException e) {
				// no config
				log.error(
						"It is not possible to create configuration directory: {}. Can't continue !",
						DIR_USER_SETTINGS, e);
				JOptionPane
						.showMessageDialog(
								null,
								"Can't continue ! It is not possible to create configuration directory: " + DIR_USER_SETTINGS,
								"Fatal error", JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			
			String files_to_copy[] = {"main.properties", "toolbars.xml", "logback.xml"};
					
			for (int i = 0; i < files_to_copy.length; i++) {
				try {
					Files.copy(
							Paths.get(DIR_BASE + files_to_copy[i]+".default"),
							Paths.get(DIR_USER_SETTINGS + files_to_copy[i]), 
							StandardCopyOption.REPLACE_EXISTING
					);
				} catch (IOException e) {
					log.error("Failed to restore default settings: {}", files_to_copy[i]);
				}
			}
		}		
		log.info("user settings directory: " + DIR_USER_SETTINGS);
		DIR_LOCALE = DIR_BASE + "Languages";
		
		if (System.getProperty("dir.data")==null) {
			DIR_DATA =  DIR_BASE + "data"+ File.separator;
			log.info("dir.data is not explicitly set from command line... deriving own value...");
		} else {
			DIR_DATA = System.getProperty("dir.data");
		}
		
		if (!DIR_DATA.endsWith(File.separator)) {
			DIR_DATA += File.separator;
		}
		
		log.info("user data directory: " + DIR_DATA);

		DIR_DOC = DIR_BASE + "doc" + File.separator; 
		
		MAIN_CONFIG = DIR_USER_SETTINGS +  "main.properties";
		
		File config_file = new File(MAIN_CONFIG); 

		try {
			if (config_file.exists()) {
				log.debug("configuration file {} already exists... loading", config_file.toPath());
				Config.load(config_file);
			} else {
				log.info("Configuration file doesn't exist... creating one");
				
				InputStream in = null;
				String default_configuration_name = DIR_BASE + "main.properties.default";
				File default_config_file  = new File (default_configuration_name);
				
				if (default_config_file.exists()) {
					
					try {
						in = new FileInputStream(default_config_file);
						Config.load(in, "UTF-8");
					} catch (FileNotFoundException e) {
						log.error("Can't load default configuration file from: {}", default_config_file.getAbsolutePath());
						JOptionPane.showMessageDialog(
								null,
								"The default configuration file can't be loaded. can't continue. Please reinstall application! ",
								"Fatal error", JOptionPane.ERROR_MESSAGE);
					}
					
				} else {
					JOptionPane.showMessageDialog(
							null,
							"The default configuration file is missing. can't continue. Please reinstall application! ",
							"Fatal error", JOptionPane.ERROR_MESSAGE);
				}
			}
			
			Config.setFile(config_file);
			log.info("saving configuration: {}", config_file.toString());
			Config.save();
			Config.setAutoSave(true);

		} catch (ConfigurationException e) {
			log.error("Failed to load configuration file from {}",	config_file.getAbsolutePath(), e);
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

		// user resources
		DIR_EXPORT = Config.getString("dir.export", DIR_DATA + "Exports" + File.separator);
		DIR_BACKUP = Config.getString("dir.backup", DIR_DATA + "Backups" + File.separator);

		//FIXME: linked file are owned by project so they must be store in somewhere in database directory
		DIR_LINKED_FILES= Config.getString("dir.linked.files", DIR_DATA + "LinkedFiles"+ File.separator);
		
		// application resources
		DIR_SKINS= Config.getString("dir.skins", DIR_BASE + "Skins"+ File.separator);
		DIR_IMAGES = DIR_BASE + File.separator + "images" + File.separator + (isMac?"Mac"+File.separator:"");
		DIR_STENCILS = DIR_BASE + "Stencils" + File.separator + (isMac?"Mac"+File.separator:"");
		DIR_REFERENCE_NODE_ICONS = DIR_IMAGES +  "ReferenceNodeIcons" + File.separator + (isMac?"Mac"+File.separator:"");
		DIR_PROJECT_TEMPLATES = Config.getString("dir.project.templates", DIR_BASE + "ProjectTemplates"+ File.separator);
		
		DIR_TEMPLATES = Config.getString("dir.templates", DIR_BASE + "Templates"+ File.separator);
		DIR_HELP = DIR_BASE + "Help" + File.separator;
		
		
		checkDirectory(DIR_DATA);
		checkDirectory(DIR_BACKUP);
		checkDirectory(DIR_EXPORT);
		checkDirectory(DIR_PROJECT_TEMPLATES);
		checkDirectory(DIR_LINKED_FILES);
		checkDirectory(DIR_TEMPLATES);

        log.info("dir review: DIR_SKINS={}",DIR_SKINS);
        log.info("dir review: DIR_IMAGES={}",DIR_IMAGES);
        log.info("dir review: DIR_IMAGES_TOOLBARS={}",DIR_IMAGES_TOOLBARS);
        log.info("dir review: DIR_STENCILS={}",DIR_STENCILS);
        log.info("dir review: DIR_REFERENCE_NODE_ICONS={}",DIR_REFERENCE_NODE_ICONS);
        log.info("dir review: DIR_DATA={}",DIR_DATA);
        log.info("dir review: DIR_BACKUP={}",DIR_BACKUP);
        log.info("dir review: DIR_EXPORT={}",DIR_EXPORT);
        log.info("dir review: DIR_PROJECT_TEMPLATES={}",DIR_PROJECT_TEMPLATES);
        log.info("dir review: DIR_LINKED_FILES={}",DIR_LINKED_FILES);
        log.info("dir review: DIR_TEMPLATES={}",DIR_TEMPLATES);

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
	 * @param sDirectory
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
            log.warn("Exception...", e);
		}

		String sIP = "";
		try {
			sIP = (InetAddress.getLocalHost()).getHostAddress();
		} catch (java.net.UnknownHostException e) {
			log.error("Exception...", e);
		}

        // inspiration taken from here: http://alvinalexander.com/blog/post/jfc-swing/how-put-java-application-name-mac-menu-bar-menubar


        if (isMac) {
            // take the menu bar off the jframe
            System.setProperty("apple.laf.useScreenMenuBar", "true");

            // set the name of the application menu item
            System.setProperty("com.apple.mrj.application.apple.menu.about.name", ProjectCompendium.Config.getString("base.appname", "Compendium NG"));

            String systemlookandfeel = "undefined";

            try {
                // set the look and feel
                systemlookandfeel = UIManager. getSystemLookAndFeelClassName();
                UIManager.setLookAndFeel(systemlookandfeel);
            } catch (Throwable t) {
                log.warn("Failed to set L&F to \"%s\" on Mac... ignoring", systemlookandfeel);
            }
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
