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

package com.compendium.ui;

import java.awt.Font;
import java.util.*;
import java.io.*;

import com.compendium.ProjectCompendium;
import com.compendium.core.ICoreConstants;

/**
 * This class is used to store and load the format property variables.
 *
 * @author	Michelle Bachler
 */
public class FormatProperties {

	/** The properties class holding the foramt properties.*/
	private static Properties format = new Properties();

	/** The label length at which the detail box should be automatically popped up.*/
	public static int detailRolloverLength = 250;

	/** The default database to use when using the local Derby database.*/
	public static String defaultDatabase = "";

	/** The current look and feel.*/
	public static String currentLookAndFeel = "";

	/** The skin set selected.*/
	public static String skin = "default";

	/** Should audio be on.*/
	public static boolean audioOn = false;

	/** Should dnd files be copied to the Linked Files folder, and with ot without prompting (on/off/prompt)*/
	public static String dndFiles = "prompt";

	/** Is image enlargement rollover on?.*/
	public static boolean imageRollover = false;

	/** Should images be scaled on rollover to fit screen.*/
	public static boolean scaleImageRollover = false;

	/** process al text drops as plain text automatically.*/
	public static boolean dndNoTextChoice = false;

	/** Is label searching on?.*/
	public static boolean autoSearchLabel = false;

	/** Is the aerial view on.*/
	public static boolean aerialView = false;

	/** The default zoom level for maps.*/
	public static double zoomLevel = 1.0;

	/** Should the main menu be at the top of the screen on the mac os.*/
	public static boolean macMenuBar = false;

	/** Should the menu shortcut characters be underlined on the mac os.*/
	public static boolean macMenuUnderline = true;

	/** The last x position of the main application screen.*/
	public static int lastScreenX = 0;

	/** The last y position of the main application screen.*/
	public static int lastScreenY = 0;

	/** The last width of the main application screen.*/
	public static int lastScreenWidth = -1;

	/** The last height of the main application screen.*/
	public static int lastScreenHeight = -1;

	/** The database type to use */
	public static int nDatabaseType = ICoreConstants.DERBY_DATABASE;

	/** The MySQL database profile last used */
	public static String sDatabaseProfile = "";

	/** Whether to display the full path of the current datasource of not, in the application title bar.*/
	public static boolean displayFullPath = false;

	/** Whether to display the status bar.*/
	public static boolean displayStatusBar = true;

	/** Whether to display the view history bar.*/
	public static boolean displayViewHistoryBar = false;

	/** Whether to display the outline.*/
	public static String displayOutlineView = IUIConstants.DISPLAY_NONE;
	
	/** Whether to display the unread view. */
	public static boolean displayUnreadView = false;

	/** The amount the cursor should be moved when using keyboard arrow keys.*/
	public static int cursorMovementDistance = 20;

	/** The vertical gap between nodes when doing a left-to-right arrange.*/
	public static int arrangeLeftVerticalGap = 20;

	/** The horizontal gap between nodes when doing a left-to-right arrange.*/
	public static int arrangeLeftHorizontalGap = 30;

	/** The vertical gap between nodes when doing a top-down arrange.*/
	public static int arrangeTopVerticalGap = 40;

	/** The horizontal gap between nodes when doing a top-down arrange.*/
	public static int arrangeTopHorizontalGap = 20;

	/** Indicates whether the refresh timer was running.*/
	public static boolean refreshTimerRunning = false;

	/** The refresh time interval to run the timer at (in seconds).*/
	public static int refreshTime = 10;

	/** Indicates whether to start the uDig Communucations manager and related services.*/
	public static boolean startUDigCommunications = false;
	
	/** Whether to display the tag view. */
	public static boolean displayTagsView = false;

	/** Which orientation to display the tags view.*/
	public static String tagsViewOrientation = "vertical";
	
	/** open nodes with single click */
	public static boolean singleClick = false;

	/** import all subdirectories recursively */
	public static boolean dndAddDirRecursively = false;

	/** do you want to use the kfmclient to open files */
	public static boolean useKFMClient = false;
	
	/** The current outline format to use.*/
	public static String outlineFormat = "Default";

	/**
	 * Constructor. Does nothing.
	 */
	public FormatProperties() {}

	/**
	 * Load the format properties into the class variables.
	 */
	public static void loadProperties() {

		loadFormatProps();

		String sDefaultDatabase = getFormatProp("defaultdatabase");
		if (sDefaultDatabase != null && !sDefaultDatabase.equals(""))
			defaultDatabase = sDefaultDatabase;
		else
			defaultDatabase = "";

		String sProfile = getFormatProp("databaseprofile");
		if (sProfile != null && !sProfile.equals(""))
			sDatabaseProfile = sProfile;

		String sDatabaseType = getFormatProp("database");
		if (sDatabaseType != null && sDatabaseType.equals("mysql"))
			nDatabaseType = ICoreConstants.MYSQL_DATABASE;
		else
			nDatabaseType = ICoreConstants.DERBY_DATABASE;

		String sCurrentLookAndFeel = getFormatProp("LAF");
		if (sCurrentLookAndFeel != null && !sCurrentLookAndFeel.equals("")) {
			currentLookAndFeel = sCurrentLookAndFeel;
		}

		String sSkin = getFormatProp("skin");
		if (sSkin != null && !sSkin.equals("")) {
			skin = sSkin;
		}

		String sDndFile = getFormatProp("dndFiles");
		if (sDndFile != null && !sDndFile.equals("")) {
			dndFiles = sDndFile;
		}

		String audio = getFormatProp("audioOn");
		if (audio != null && !audio.equals(""))
			audioOn = new Boolean(audio).booleanValue();

		String imgroll = getFormatProp("imageRollover");
		if (imgroll != null && !imgroll.equals(""))
			imageRollover = new Boolean(imgroll).booleanValue();

		String simgroll = getFormatProp("scaleImageRollover");
		if (simgroll != null && !simgroll.equals(""))
			scaleImageRollover = new Boolean(simgroll).booleanValue();

		String noChoice = getFormatProp("dndNoTextChoice");
		if (noChoice != null && !noChoice.equals(""))
			dndNoTextChoice = new Boolean(noChoice).booleanValue();

		String searchLabel = getFormatProp("autoSearchLabel");
		if (searchLabel != null && !searchLabel.equals(""))
			autoSearchLabel = new Boolean(searchLabel).booleanValue();

		String aerial = getFormatProp("aerialView");
		if (aerial != null && !aerial.equals(""))
			aerialView = new Boolean(aerial).booleanValue();

		String oZoom = getFormatProp("zoom");
		if (oZoom != null && !oZoom.equals(""))
			zoomLevel = new Double(oZoom).doubleValue();
		else
			zoomLevel = 1.0;

		String detLen = getFormatProp("detailrolloverlength");
		if (detLen != null && !detLen.equals(""))
			detailRolloverLength = Integer.valueOf(detLen).intValue();
		else
			detailRolloverLength = 250;

		String macmenu = getFormatProp("macmenubar");
		if (macmenu != null && !macmenu.equals(""))
			macMenuBar = new Boolean(macmenu).booleanValue();

		String macmenuund = getFormatProp("macmenuunderline");
		if (macmenuund != null && !macmenuund.equals(""))
			macMenuUnderline = new Boolean(macmenuund).booleanValue();

		String swidth = getFormatProp("lastScreenWidth");
		if (swidth != null && !swidth.equals("")) {
			try { lastScreenWidth = Integer.valueOf(swidth).intValue(); }
			catch(NumberFormatException nfe) {}
		}
		String sheight = getFormatProp("lastScreenHeight");
		if (sheight != null && !sheight.equals("")) {
			try { lastScreenHeight = Integer.valueOf(sheight).intValue(); }
			catch(NumberFormatException nfe) {}
		}
		String sXPos = getFormatProp("lastScreenX");
		if (sXPos != null && !sXPos.equals("")) {
			try { lastScreenX = Integer.valueOf(sXPos).intValue(); }
			catch(NumberFormatException nfe) {}
		}
		String sYPos = getFormatProp("lastScreenY");
		if (sYPos != null && !sYPos.equals("")) {
			try { lastScreenY = Integer.valueOf(sYPos).intValue(); }
			catch(NumberFormatException nfe) {}
		}

		String path = getFormatProp("displayFullPath");
		if (path != null && !path.equals(""))
			displayFullPath = new Boolean(path).booleanValue();

		String statusBar = getFormatProp("displayStatusBar");
		if (statusBar != null && !statusBar.equals(""))
			displayStatusBar = new Boolean(statusBar).booleanValue();

		String viewHistory = getFormatProp("displayViewHistoryBar");
		if (viewHistory != null && !viewHistory.equals(""))
			displayViewHistoryBar= new Boolean(viewHistory).booleanValue();

		// Lakshmi (4/3/06)
		String outlineView = getFormatProp("displayOutlineView");
		if (outlineView != null && !outlineView.equals(""))
			displayOutlineView = outlineView;
		
		String unreadView = getFormatProp("displayUnreadView");
		if(unreadView != null && !unreadView.equals(""))
			displayUnreadView = new Boolean(unreadView).booleanValue();
		
		String cursorMove = getFormatProp("cursorMovementDistance");
		if (cursorMove != null && !cursorMove.equals("")) {
			try { cursorMovementDistance = Integer.valueOf(cursorMove).intValue(); }
			catch(NumberFormatException nfe) {}
		}

		String arrangeleftH = getFormatProp("arrangeLeftHorizontalGap");
		if (arrangeleftH != null && !arrangeleftH.equals("")) {
			try { arrangeLeftHorizontalGap = Integer.valueOf(arrangeleftH).intValue(); }
			catch(NumberFormatException nfe) {}
		}

		String arrangeleftV = getFormatProp("arrangeLeftVerticalGap");
		if (arrangeleftV != null && !arrangeleftV.equals("")) {
			try { arrangeLeftVerticalGap = Integer.valueOf(arrangeleftV).intValue(); }
			catch(NumberFormatException nfe) {}
		}

		String arrangeTopH = getFormatProp("arrangeTopHorizontalGap");
		if (arrangeTopH != null && !arrangeTopH.equals("")) {
			try { arrangeTopHorizontalGap = Integer.valueOf(arrangeTopH).intValue(); }
			catch(NumberFormatException nfe) {}
		}

		String arrangeTopV = getFormatProp("arrangeTopVerticalGap");
		if (arrangeTopV != null && !arrangeTopV.equals("")) {
			try { arrangeTopVerticalGap = Integer.valueOf(arrangeTopV).intValue(); }
			catch(NumberFormatException nfe) {}
		}

		String timerRunning = getFormatProp("timerRunning");
		if (timerRunning != null && !timerRunning.equals("")) {
			refreshTimerRunning = new Boolean(timerRunning).booleanValue();
		}

		String refreshIndex = getFormatProp("refreshTime");
		if (refreshIndex != null && !refreshIndex.equals("")) {
			try { refreshTime = Integer.valueOf(refreshIndex).intValue(); }
			catch(NumberFormatException nfe) {}
		}
		
		String uDig = getFormatProp("udig");
		if (uDig != null && !uDig.equals("")) {
			startUDigCommunications = new Boolean(uDig).booleanValue();
		}
		
		String tagsView = getFormatProp("displayTagsView");
		if (tagsView != null && !tagsView.equals("")) {
			displayTagsView = new Boolean(tagsView).booleanValue();
		}		
		
		String tagsOri = getFormatProp("tagsViewOrientation");
		if (tagsOri != null && !tagsOri.equals("")) {
			tagsViewOrientation = tagsOri;
		}		
				
		String dirRecursively = getFormatProp("dndAddDirRecursively");
		if (dirRecursively != null && !dirRecursively.equals(""))
			dndAddDirRecursively = new Boolean(dirRecursively).booleanValue();

		String bClick = getFormatProp("singleClick");
		if (bClick != null && !bClick.equals(""))
			singleClick = new Boolean(bClick).booleanValue();
	
		String bkfm = getFormatProp("kfmclient");
		if (bkfm != null && !bkfm.equals(""))
			useKFMClient = new Boolean(bkfm).booleanValue();
		
		String outline = getFormatProp("outlineFormat");
		if (outline != null && !outline.equals(""))
			outlineFormat = outline;

	}

	/**
	 * Load the format properties for the appropriat file.
	 */
	public static void loadFormatProps() {
		FileInputStream fin = null;
		try {
			fin = new FileInputStream("System"+ProjectCompendium.sFS+"resources"+ProjectCompendium.sFS+"Format.properties");
		}
		catch(FileNotFoundException e) {}

		try {
			if( fin != null ) {
				format.load( fin );
				fin.close();
			}
		}
		catch(IOException e ) {}
	}

	/**
	 * Save all the properties out to the relevant file.
	 */
	public static void saveFormatProps() {

		FileOutputStream fout = null;
		try {
			fout = new FileOutputStream("System"+ProjectCompendium.sFS+"resources"+ProjectCompendium.sFS+"Format.properties");
		}
		catch(FileNotFoundException e) {}

		try {
			if( fout != null ) {
				format.store( (OutputStream)fout, "Format properties" );
				fout.close();
			}
		}
		catch(IOException e ) {}
	}

	/**
	 * Return the valu against the given key if found, else an empty String.
	 * @param key, the key to set.
	 * @return String, the associated value.
	 */
	public static String getFormatProp( String key ) {
		String value = "";

		try { value = format.getProperty( key ); }
		catch(Exception e) {}

		return value;
	}

	/**
	 * Set the given key, value pair in the property list.
	 * @param key, the key to set.
	 * @param value, the value against the key.
	 */
	public static void setFormatProp( String key, String value ) {
		format.setProperty( key, value );
	}
}
