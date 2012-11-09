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

import java.util.*;
import java.io.*;

import com.compendium.core.ICoreConstants;

/**
 * This class is used to load and store the system initialisation variables.
 *
 * @author	Michelle Bachler
 */
public class SystemProperties {
	

	/** The properties class holding the system initialisation properties.*/
	private static Properties system = new Properties();

	/**
	 * The location to create and access Compendium's admin and local projects databases using Derby.
	 */
	public static String defaultDatabaseLocation="System/resources/Databases";

	/** 
	 * The Application Name to Display
	 */
	public static String applicationName = ICoreConstants.sAPPNAME;

	/** 
	 * The Text to place after the application name to qualify a version or flavour of the application
	 */
	public static String applicationNameAddition = "";

	/** 
	 * Splash dialog title when Compendium starts. Can tailor this to specify a version
	 * like 'Starting Compendium - OpenLearn' or 'Starting CompendiumLD'
	 */
	public static String startUpTitle = "Starting Compendium";
	
	/** 
 * Text which appears under the title on the start up screen.
 * Should be a short sentence.
	 */
	public static String startUpQualifyingText = "";
	
	/**
	 * When Compendium first run, should it create a first default project?
	 * see also 'defaultProjectName'
	 */
	public static boolean createDefaultProject = false;
	
	/**
	 * If you are letting Compendium auto-create the first default project, this is the name that project will be given.
	 * see also 'createDefaultProject'
	 */
	public static String defaultProjectName = "Default";
	//public static String defaultProjectName = "Default project for OpenLearn Knowledge Mapping";
	
	/**
	 * Specify the XML file to use to load the deafult data for a project.
	 * You can choose to leave this blank if you want no default data at all.
	 * This file must either be a Compendium xml export file or a Comendium XML zip export file - (DefaultData.zip)
	 */
	public static String projectDefaultDataFile = "";
	
	/**
	 * Specify a default background image to be used on all maps
	 * Useful when company water marks etc are required.
	 */
	public static String defaultMapBackgroundImage = "";
	
	/**
	 * Specify a default background image to be used on all home views
	 * Useful when company water marks etc are required.
	 */
	public static String defaultHomeViewBackgroundImage = "";

	/**
	 * If this is set it will open this stencil set by default.
	 * Note: the name below must match a stencil set name exactly for it to open it.
	 */
	public static String defaultStencilSetName = "";
	
	/** 
	 * The banner image file for image that sits under the menu bar when the Compendium application is running.
	 * File should be placed in System/ini/images folder. 
	 * The default image is 4500w x 18h. It will be loaded 0x,0y under the Menubar.
	 */
	public static String bannerImage = "";
	//public static String bannerImage = "System"+ProjectCompendium.sFS+"resources"+ProjectCompendium.sFS+"Images"+ProjectCompendium.sFS+"labspace-banner.jpg";

	/** 
	 * The startup/about dialog splash background image file.
	 * File should be placed in System/ini/images folder. 
	 * The image is loaded at 0x,0y. 
	 * It's default/max size is 300w x 400h.
	 */
	public static String splashImage = "System/ini/images/splash.jpg";	
	//public static String splashImage = "System"+ProjectCompendium.sFS+"resources"+ProjectCompendium.sFS+"Images"+ProjectCompendium.sFS+"knowledge-labspace_splash.jpg"
	
	/**
	 * If this image is specified it is position in the about dialog's top-lefthand corner at 10x, 10y.
	 * File should be placed in System/ini/images folder.
	 * This button will launch the companyWebsiteURL specified below.
	 * So the button image should be associated with that url (like tha Company/product logo)
	 */
	public static String aboutButtonImage = "System/ini/images/kmi.gif";
	//public static String aboutButton = 	"System"+ProjectCompendium.sFS+"resources"+ProjectCompendium.sFS+"Images"+ProjectCompendium.sFS+"knowledge-map_splash-button.jpg";		
	
	/**
	 * This url is used on the About dialog.
	 * It is launch through the aboutButtonImage specified above.
	 * It is indeded to be the Company/Product url (hence the name), but can be anything you like.
	 */
	public static String companyWebsiteURL = "http://kmi.open.ac.uk";
	//public static String companyWebsiteURL = "http://labspace.open.ac.uk";
	
	/** 
	 * The path that a Power Export should go into by default. 
	 * This was made editable as the Open Learn version had a subfolder in Exports for these specific exports.
	 * The default is just the main 'Exports' folder. If left blank, that is where the Power Export will go to.
	 */
	public static String defaultPowerExportPath = "Exports"; 
	//public static String defaultPowerExportPath = "Exports"+ProjectCompendium.sFS+"OpenLearn"; 
	
	/**
	 * This is the link on the Help menu for the release notes for the current version of Compendium.
	 */
	public static String releaseNotesURL = "http://compendium.open.ac.uk/institute/download/release-notes-2.0.htm";

	/**
	 * This is the link on the Help menu for the Quick start Movie.
	 */
	public static String quickStartMovie = "System/resources/Help/Movies/welcome.html";
	
	/**
	 * This is the url for the About dialog which will be accessed by the 'Help And Support' button on that dialog.
	 */
	public static String helpAndSupportURL = "http://compendium.open.ac.uk/support/";
	//public static String helpAndSupportURL = "http://labspace.open.ac.uk/mod/forum/view.php?id=179";

	/**
	 * The interface mode to start Compendium with when first installed.
	 * 'true' = simplified interface; 'false' = full interface.
	 * The user can override this and their choice is stored in Format.properties
	 */
	public static boolean simpleInterface = false;
	
	/**
	 * Should the admin database name be displayed in the startup screen?
	 * 'true' = yes; 'false' = no.
	 */
	public static boolean showAdminDatabase = false;
	
	/***** WELCOME PAGE OPTIONS *****/
	
	/**
	 * This is the option to set the background image used on the welcome page.
	 * The default one supplied is 3000 x 3000, to allow for large screens across to monitors.
	 * The file should be placed in System/ini/images folder.  
	 * Compendium default for this options is: System/ini/images/background.png
	 */
	public static String welcomeBackgroundImage="System/ini/images/background.png";

	/**
	 * This is the option to specify the Welcome page title message.
	 * If you want the Compendium application version included in the message
	 * add <version> where you want it to appear.
	 * Compendium default for this options is: Welcome to Compendium <version>
	 */
	public static String welcomeMessage="Welcome to Compendium <version>";	
	
	/**
	 * This is the option to specify the button image to use for the button to open the New Project Dialog.
	 * The file should be placed in System/ini/images folder. 
	 * Compendium default for this options is: System/ini/images/new-project.png
	 */
	public static String welcomeNewProjectButtonImage="System/ini/images/new-project.png";

	/**
	 * This is the option to specify the button image for the 1st user defined button.
	 * The file should be placed in System/ini/images folder. 
	 * When pressed it will activate the path/url specified by welcomeButton1Link.
	 * Compendium default for this options is: System/ini/images/pdf.png
	 */
	public static String welcomeButton1Image="System/ini/images/pdf.png";

	/**
	 * This is the option to specify the link activated by welcomeButton1Image.
	 * This can be a url (please specify fully) or a local file. 
	 * If the link is the path to a local file, the path must be specified relative to the Compendium folder.
	 * for example System/resources/Help/Docs/CompendiumQuickRef.pdf
	 * Compendium default for this options is: System/resources/Help/Docs/CompendiumQuickRef.pdf
	 */
	public static String welcomeButton1Link="System/resources/Help/Docs/CompendiumQuickRef.pdf";

	/**
	 * This is the option to specify the button rollover hint to tell the user what button 1 is for.
	 */
	public static String welcomeButton1Hint="View quick overview pdf (opens in your pdf viewer)";

	/**
	 * This is the option to specify the button image for the 2nd user defined button.
	 * The file should be placed in System/ini/images folder. 
	 * When pressed it will activate the path/url specified by welcomeButton2Link.
	 * Compendium default for this options is: System/ini/images/movie.png
	  */
	public static String welcomeButton2Image="System/ini/images/movie.png";

	/**
	 * This is the option to specify the link activated by welcomeButton2Image.
	 * This can be a url (please specify fully) or a local file. 
	 * If the link is the path to a local file, the path must be specified relative to the Compendium folder.
	 * for example System/resources/Help/Docs/CompendiumQuickRef.pdf
	 * Compendium default for this options is: System/resources/Help/Movies/welcome.html
	 */
	public static String welcomeButton2Link="System/resources/Help/Movies/welcome.html";

	/**
	 * This is the option to specify the button rollover hint to tell the user what button 2 is for.
	 */
	public static String welcomeButton2Hint="Watch Quick Start Moive (opens browser)";

	/**
	 * This is the option to specify the button image for the 3rd user defined button.
	 * The file should be placed in System/ini/images folder 
	 * When pressed it will activate the path/url specified by welcomeButton3Link. 
	 * Compendium default for this options is: System/ini/images/help.png
	 */
	public static String welcomeButton3Image="System/ini/images/help.png";

	/**
	 * This is the option to specify the link activated by welcomeButton3Image.
	 * This can be a url (please specify fully) or a local file. 
	 * If the link is the path to a local file, the path must be specified relative to the Compendium folder.
	 * for example System/resources/Help/Docs/CompendiumQuickRef.pdf
	 * Compendium default for this options is: http://www.compendiuminstitute.org/training/videos/
	 */
	public static String welcomeButton3Link="http://compendium.open.ac.uk/intitute/training/videos/";

	/**
	 * This is the option to specify the button rollover hint to tell the user what button 3 is for.
	 */
	public static String welcomeButton3Hint="View online help movies (opens browser)";
	
	/**
	 * This is the option to specify the button image to use for the button to close the Welcome page.
	 * The file should be placed in System/ini/images folder. 
	 * Compendium default for this options is: System/ini/images/enter.png
	 */
	public static String welcomeEnterButtonImage="System/ini/images/enter.png";
	
	

	/**
	 * Constructor. Does nothing.
	 */
	public SystemProperties() {}

	/**
	 * Load the format properties into the class variables.
	 */
	public static void loadProperties() {

		loadSystemIni();

		String sDefaultDatabaseLocation = getProperty("defaultDatabaseLocation");
		if (sDefaultDatabaseLocation != null && !sDefaultDatabaseLocation.equals("")) {
			defaultDatabaseLocation = sDefaultDatabaseLocation;
		}

		String sApplicationNameAddition = getProperty("applicationNameAddition");
		if (sApplicationNameAddition != null && !sApplicationNameAddition.equals("")) {
			applicationNameAddition = sApplicationNameAddition;
			applicationName += " "+applicationNameAddition;
		}

		String sStartUpTitle = getProperty("startUpTitle");
		if (sStartUpTitle != null && !sStartUpTitle.equals(""))
			startUpTitle = sStartUpTitle;

		String sStartUpQualifyingText = getProperty("startUpQualifyingText");
		if (sStartUpQualifyingText != null && !sStartUpQualifyingText.equals(""))
			startUpQualifyingText = sStartUpQualifyingText;

		String sCreateDefaultProject = getProperty("createDefaultProject");
		if (sCreateDefaultProject != null && !sCreateDefaultProject.equals(""))
			createDefaultProject = new Boolean(sCreateDefaultProject).booleanValue();

		String sDefaultProjectName = getProperty("defaultProjectName");
		if (sDefaultProjectName != null && !sDefaultProjectName.equals(""))
			defaultProjectName = sDefaultProjectName;

		String sProjectDefaultDataFile = getProperty("projectDefaultDataFile");
		if (sProjectDefaultDataFile != null && !sProjectDefaultDataFile.equals(""))
			projectDefaultDataFile = sProjectDefaultDataFile;
		
		String sDefaultMapBackgroundImage = getProperty("defaultMapBackgroundImage");
		if (sDefaultMapBackgroundImage != null && !sDefaultMapBackgroundImage.equals(""))
			defaultMapBackgroundImage = sDefaultMapBackgroundImage;				
		
		String sDefaultHomeViewBackgroundImage = getProperty("defaultHomeViewBackgroundImage");
		if (sDefaultHomeViewBackgroundImage != null && !sDefaultHomeViewBackgroundImage.equals(""))
			defaultHomeViewBackgroundImage = sDefaultHomeViewBackgroundImage;				

		String sDefaultStencilSetName = getProperty("defaultStencilSetName");
		if (sDefaultStencilSetName != null && !sDefaultStencilSetName.equals(""))
			defaultStencilSetName = sDefaultStencilSetName;
		
		String sBannerImage = getProperty("bannerImage");
		if (sBannerImage != null && !sBannerImage.equals("")) {
			bannerImage = sBannerImage;
		}
		
		String sSplashImage = getProperty("splashImage");
		if (sSplashImage != null && !sSplashImage.equals(""))
			splashImage = sSplashImage;

		String sAboutButtonImage = getProperty("aboutButtonImage");
		if (sAboutButtonImage != null && !sAboutButtonImage.equals("")) {
			aboutButtonImage = sAboutButtonImage;
		}
		
		String sCompanyWebsiteURL = getProperty("companyWebsiteURL");
		if (sCompanyWebsiteURL != null && !sCompanyWebsiteURL.equals(""))
			companyWebsiteURL = sCompanyWebsiteURL;
					
		String sDefaultPowerExportPath = getProperty("defaultPowerExportPath");
		if (sDefaultPowerExportPath != null && !sDefaultPowerExportPath.equals(""))
			defaultPowerExportPath = sDefaultPowerExportPath;

		String sReleaseNotesURL = getProperty("releaseNotesURL");
		if (sReleaseNotesURL != null && !sReleaseNotesURL.equals(""))
			releaseNotesURL = sReleaseNotesURL;

		String sQuickStartMovie = getProperty("quickStartMovie");
		if (sQuickStartMovie != null && !sQuickStartMovie.equals(""))
			quickStartMovie = sQuickStartMovie;
		
		String sHelpAndSupportURL = getProperty("helpAndSupportURL");
		if (sHelpAndSupportURL != null && !sHelpAndSupportURL.equals(""))
			helpAndSupportURL = sHelpAndSupportURL;
		
		String simple = getProperty("simpleInterface");
		if (simple != null && !simple.equals("")) {
			simpleInterface = new Boolean(simple).booleanValue();
		}			

		String sShowAdminDatabase = getProperty("showAdminDatabase");
		if (sShowAdminDatabase != null && !sShowAdminDatabase.equals("")) {
			showAdminDatabase = new Boolean(sShowAdminDatabase).booleanValue();
		}			
		
		String sWelcomeBackgroundImage = getProperty("welcomeBackgroundImage");
		if (sWelcomeBackgroundImage != null && !sWelcomeBackgroundImage.equals("")) {
			welcomeBackgroundImage = sWelcomeBackgroundImage;
		}			

		String sWelcomeMessage = getProperty("welcomeMessage");
		if (sWelcomeMessage != null && !sWelcomeMessage.equals("")) {
			welcomeMessage = sWelcomeMessage;
		}			
		
		String sWelcomeNewProjectButtonImage = getProperty("welcomeNewProjectButtonImage");
		if (sWelcomeNewProjectButtonImage != null && !sWelcomeNewProjectButtonImage.equals("")) {
			welcomeNewProjectButtonImage = sWelcomeNewProjectButtonImage;
		}			

		String sWelcomeButton1Image = getProperty("welcomeButton1Image");
		if (sWelcomeButton1Image != null && !sWelcomeButton1Image.equals("")) {
			welcomeButton1Image = sWelcomeButton1Image;
		}			

		String sWelcomeButton1Hint = getProperty("welcomeButton1Hint");
		if (sWelcomeButton1Hint != null && !sWelcomeButton1Hint.equals("")) {
			welcomeButton1Hint = sWelcomeButton1Hint;
		}			

		String sWelcomeButton1Link = getProperty("welcomeButton1Link");
		if (sWelcomeButton1Link != null && !sWelcomeButton1Link.equals("")) {
			welcomeButton1Link = sWelcomeButton1Link;
		}			

		String sWelcomeButton2Image = getProperty("welcomeButton2Image");
		if (sWelcomeButton2Image != null && !sWelcomeButton2Image.equals("")) {
			welcomeButton2Image = sWelcomeButton2Image;
		}			

		String sWelcomeButton2Link = getProperty("welcomeButton2Link");
		if (sWelcomeButton2Link != null && !sWelcomeButton2Link.equals("")) {
			welcomeButton2Link = sWelcomeButton2Link;
		}			

		String sWelcomeButton2Hint = getProperty("welcomeButton2Hint");
		if (sWelcomeButton2Hint != null && !sWelcomeButton2Hint.equals("")) {
			welcomeButton2Hint = sWelcomeButton2Hint;
		}			

		String sWelcomeButton3Image = getProperty("welcomeButton3Image");
		if (sWelcomeButton3Image != null && !sWelcomeButton3Image.equals("")) {
			welcomeButton3Image = sWelcomeButton3Image;
		}			

		String sWelcomeButton3Link = getProperty("welcomeButton3Link");
		if (sWelcomeButton3Link != null && !sWelcomeButton3Link.equals("")) {
			welcomeButton3Link = sWelcomeButton3Link;
		}			

		String sWelcomeButton3Hint = getProperty("welcomeButton3Hint");
		if (sWelcomeButton3Hint != null && !sWelcomeButton3Hint.equals("")) {
			welcomeButton3Hint = sWelcomeButton3Hint;
		}			
		
		String sWelcomeEnterButtonImage = getProperty("welcomeEnterButtonImage");
		if (sWelcomeEnterButtonImage != null && !sWelcomeEnterButtonImage.equals("")) {
			welcomeEnterButtonImage = sWelcomeEnterButtonImage;
		}			
	}

	/**
	 * Load the format properties for the appropriate file.
	 */
	public static void loadSystemIni() {
		FileInputStream fin = null;
		try {
			fin = new FileInputStream("System/ini/system.ini");
		}
		catch(FileNotFoundException e) {
			e.printStackTrace();			
		}

		try {
			if( fin != null ) {
				system.load( fin );
				fin.close();
			}
		}
		catch(IOException e ) {
			e.printStackTrace();
		}
	}

	/**
	 * Return the value against the given key if found, else an empty String.
	 * @param key, the key to set.
	 * @return String, the associated value.
	 */
	public static String getProperty( String key ) {
		String value = "";

		try { value = system.getProperty( key ); }
		catch(Exception e) {}

		return value;
	}
}
