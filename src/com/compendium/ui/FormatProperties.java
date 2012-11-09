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
	public static String defaultDatabase = ""; //$NON-NLS-1$

	/** The current look and feel.*/
	public static String currentLookAndFeel = ""; //$NON-NLS-1$

	/** The current Timed Refresh setting.*/
	public static String currentTimedRefresh = ""; //$NON-NLS-1$

	/** The skin set selected.*/
	public static String skin = "default"; //$NON-NLS-1$

	/** Should audio be on.*/
	public static boolean audioOn = false;

	/** Is image enlargement rollover on?.*/
	public static boolean imageRollover = false;

	/** Should images be scaled on rollover to fit screen.*/
	public static boolean scaleImageRollover = false;

	/** Properties for dropping files and directories. */
	public static DragAndDropProperties dndProperties = new DragAndDropProperties();

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
	public static String sDatabaseProfile = ""; //$NON-NLS-1$

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

	/** True if the user wants to view the simple interface, false for the complex one.*/
	public static boolean simpleInterface = true;
			
	/** Whether to display the tag view. */
	public static boolean displayTagsView = false;

	/** Which orientation to display the tags view.*/
	public static String tagsViewOrientation = "vertical"; //$NON-NLS-1$
	
	/** open nodes with single click */
	public static boolean singleClick = false;

	/** do you want to use the kfmclient to open files */
	public static boolean useKFMClient = false;

	/** do you want to be emailed when an item goes in the inbox. */
	public static boolean emailInbox = false;

	/** The current outline format to use.*/
	public static String outlineFormat = "Default"; //$NON-NLS-1$
	
	/** Whether to display or hide the paste hint message.*/
	public static boolean showPasteHint = true;
	
	/** Whether to run the version checker dialog */
	public static boolean autoUpdateCheckerOn = true;
	
	/**
	 * Constructor. Does nothing.
	 */
	public FormatProperties() {}

	/**
	 * Load the format properties into the class variables.
	 */
	public static void loadProperties() {

		loadFormatProps();

		String sDefaultDatabase = getFormatProp("defaultdatabase"); //$NON-NLS-1$
		if (sDefaultDatabase != null && !sDefaultDatabase.equals("")) //$NON-NLS-1$
			defaultDatabase = sDefaultDatabase;
		else
			defaultDatabase = ""; //$NON-NLS-1$

		String sProfile = getFormatProp("databaseprofile"); //$NON-NLS-1$
		if (sProfile != null && !sProfile.equals("")) //$NON-NLS-1$
			sDatabaseProfile = sProfile;

		String sDatabaseType = getFormatProp("database"); //$NON-NLS-1$
		if (sDatabaseType != null && sDatabaseType.equals("mysql")) //$NON-NLS-1$
			nDatabaseType = ICoreConstants.MYSQL_DATABASE;
		else
			nDatabaseType = ICoreConstants.DERBY_DATABASE;

		String sCurrentLookAndFeel = getFormatProp("LAF"); //$NON-NLS-1$
		if (sCurrentLookAndFeel != null && !sCurrentLookAndFeel.equals("")) { //$NON-NLS-1$
			currentLookAndFeel = sCurrentLookAndFeel;
		}
		
		String sCurrentTimedRefresh = getFormatProp("TimedRefresh"); //$NON-NLS-1$
		if (sCurrentTimedRefresh != null && !sCurrentTimedRefresh.equals("")) { //$NON-NLS-1$
			currentTimedRefresh = sCurrentTimedRefresh;
		}

		String sSkin = getFormatProp("skin"); //$NON-NLS-1$
		if (sSkin != null && !sSkin.equals("")) { //$NON-NLS-1$
			skin = sSkin;
		}

		String audio = getFormatProp("audioOn"); //$NON-NLS-1$
		if (audio != null && !audio.equals("")) //$NON-NLS-1$
			audioOn = new Boolean(audio).booleanValue();

		String imgroll = getFormatProp("imageRollover"); //$NON-NLS-1$
		if (imgroll != null && !imgroll.equals("")) //$NON-NLS-1$
			imageRollover = new Boolean(imgroll).booleanValue();

		String simgroll = getFormatProp("scaleImageRollover"); //$NON-NLS-1$
		if (simgroll != null && !simgroll.equals("")) //$NON-NLS-1$
			scaleImageRollover = new Boolean(simgroll).booleanValue();

		String searchLabel = getFormatProp("autoSearchLabel"); //$NON-NLS-1$
		if (searchLabel != null && !searchLabel.equals("")) //$NON-NLS-1$
			autoSearchLabel = new Boolean(searchLabel).booleanValue();

		String aerial = getFormatProp("aerialView"); //$NON-NLS-1$
		if (aerial != null && !aerial.equals("")) //$NON-NLS-1$
			aerialView = new Boolean(aerial).booleanValue();

		String oZoom = getFormatProp("zoom"); //$NON-NLS-1$
		if (oZoom != null && !oZoom.equals("")) //$NON-NLS-1$
			zoomLevel = new Double(oZoom).doubleValue();
		else
			zoomLevel = 1.0;

		String detLen = getFormatProp("detailrolloverlength"); //$NON-NLS-1$
		if (detLen != null && !detLen.equals("")) //$NON-NLS-1$
			detailRolloverLength = Integer.valueOf(detLen).intValue();
		else
			detailRolloverLength = 250;

		String macmenu = getFormatProp("macmenubar"); //$NON-NLS-1$
		if (macmenu != null && !macmenu.equals("")) //$NON-NLS-1$
			macMenuBar = new Boolean(macmenu).booleanValue();

		String macmenuund = getFormatProp("macmenuunderline"); //$NON-NLS-1$
		if (macmenuund != null && !macmenuund.equals("")) //$NON-NLS-1$
			macMenuUnderline = new Boolean(macmenuund).booleanValue();

		String swidth = getFormatProp("lastScreenWidth"); //$NON-NLS-1$
		if (swidth != null && !swidth.equals("")) { //$NON-NLS-1$
			try { lastScreenWidth = Integer.valueOf(swidth).intValue(); }
			catch(NumberFormatException nfe) {}
		}
		String sheight = getFormatProp("lastScreenHeight"); //$NON-NLS-1$
		if (sheight != null && !sheight.equals("")) { //$NON-NLS-1$
			try { lastScreenHeight = Integer.valueOf(sheight).intValue(); }
			catch(NumberFormatException nfe) {}
		}
		String sXPos = getFormatProp("lastScreenX"); //$NON-NLS-1$
		if (sXPos != null && !sXPos.equals("")) { //$NON-NLS-1$
			try { lastScreenX = Integer.valueOf(sXPos).intValue(); }
			catch(NumberFormatException nfe) {}
		}
		String sYPos = getFormatProp("lastScreenY"); //$NON-NLS-1$
		if (sYPos != null && !sYPos.equals("")) { //$NON-NLS-1$
			try { lastScreenY = Integer.valueOf(sYPos).intValue(); }
			catch(NumberFormatException nfe) {}
		}

		String path = getFormatProp("displayFullPath"); //$NON-NLS-1$
		if (path != null && !path.equals("")) //$NON-NLS-1$
			displayFullPath = new Boolean(path).booleanValue();

		String statusBar = getFormatProp("displayStatusBar"); //$NON-NLS-1$
		if (statusBar != null && !statusBar.equals("")) //$NON-NLS-1$
			displayStatusBar = new Boolean(statusBar).booleanValue();

		String viewHistory = getFormatProp("displayViewHistoryBar"); //$NON-NLS-1$
		if (viewHistory != null && !viewHistory.equals("")) //$NON-NLS-1$
			displayViewHistoryBar= new Boolean(viewHistory).booleanValue();

		// Lakshmi (4/3/06)
		String outlineView = getFormatProp("displayOutlineView"); //$NON-NLS-1$
		if (outlineView != null && !outlineView.equals("")) //$NON-NLS-1$
			displayOutlineView = outlineView;
		
		String unreadView = getFormatProp("displayUnreadView"); //$NON-NLS-1$
		if(unreadView != null && !unreadView.equals("")) //$NON-NLS-1$
			displayUnreadView = new Boolean(unreadView).booleanValue();
		
		String cursorMove = getFormatProp("cursorMovementDistance"); //$NON-NLS-1$
		if (cursorMove != null && !cursorMove.equals("")) { //$NON-NLS-1$
			try { cursorMovementDistance = Integer.valueOf(cursorMove).intValue(); }
			catch(NumberFormatException nfe) {}
		}

		String arrangeleftH = getFormatProp("arrangeLeftHorizontalGap"); //$NON-NLS-1$
		if (arrangeleftH != null && !arrangeleftH.equals("")) { //$NON-NLS-1$
			try { arrangeLeftHorizontalGap = Integer.valueOf(arrangeleftH).intValue(); }
			catch(NumberFormatException nfe) {}
		}

		String arrangeleftV = getFormatProp("arrangeLeftVerticalGap"); //$NON-NLS-1$
		if (arrangeleftV != null && !arrangeleftV.equals("")) { //$NON-NLS-1$
			try { arrangeLeftVerticalGap = Integer.valueOf(arrangeleftV).intValue(); }
			catch(NumberFormatException nfe) {}
		}

		String arrangeTopH = getFormatProp("arrangeTopHorizontalGap"); //$NON-NLS-1$
		if (arrangeTopH != null && !arrangeTopH.equals("")) { //$NON-NLS-1$
			try { arrangeTopHorizontalGap = Integer.valueOf(arrangeTopH).intValue(); }
			catch(NumberFormatException nfe) {}
		}

		String arrangeTopV = getFormatProp("arrangeTopVerticalGap"); //$NON-NLS-1$
		if (arrangeTopV != null && !arrangeTopV.equals("")) { //$NON-NLS-1$
			try { arrangeTopVerticalGap = Integer.valueOf(arrangeTopV).intValue(); }
			catch(NumberFormatException nfe) {}
		}

		String timerRunning = getFormatProp("timerRunning"); //$NON-NLS-1$
		if (timerRunning != null && !timerRunning.equals("")) { //$NON-NLS-1$
			refreshTimerRunning = new Boolean(timerRunning).booleanValue();
		}

		String refreshIndex = getFormatProp("refreshTime"); //$NON-NLS-1$
		if (refreshIndex != null && !refreshIndex.equals("")) { //$NON-NLS-1$
			try { refreshTime = Integer.valueOf(refreshIndex).intValue(); }
			catch(NumberFormatException nfe) {}
		}
		
		String simple = getFormatProp("simpleInterface"); //$NON-NLS-1$
		if (simple != null && !simple.equals("")) { //$NON-NLS-1$
			simpleInterface = new Boolean(simple).booleanValue();
		}		
		
		String tagsView = getFormatProp("displayTagsView"); //$NON-NLS-1$
		if (tagsView != null && !tagsView.equals("")) { //$NON-NLS-1$
			displayTagsView = new Boolean(tagsView).booleanValue();
		}		
		
		String tagsOri = getFormatProp("tagsViewOrientation"); //$NON-NLS-1$
		if (tagsOri != null && !tagsOri.equals("")) { //$NON-NLS-1$
			tagsViewOrientation = tagsOri;
		}		

		String bClick = getFormatProp("singleClick"); //$NON-NLS-1$
		if (bClick != null && !bClick.equals("")) //$NON-NLS-1$
			singleClick = new Boolean(bClick).booleanValue();
	
		String email = getFormatProp("emailInbox"); //$NON-NLS-1$
		if (email != null && !email.equals("")) //$NON-NLS-1$
			emailInbox = new Boolean(email).booleanValue();

		String ukfm = getFormatProp("kfmclient"); //$NON-NLS-1$
		if (ukfm != null && !ukfm.equals("")) //$NON-NLS-1$
			useKFMClient = new Boolean(ukfm).booleanValue();

		String outline = getFormatProp("outlineFormat"); //$NON-NLS-1$
		if (outline != null && !outline.equals("")) //$NON-NLS-1$
			outlineFormat = outline;

		String pastehint = getFormatProp("showPasteHint"); //$NON-NLS-1$
		if (pastehint != null && !pastehint.equals("")) //$NON-NLS-1$
			showPasteHint = new Boolean(pastehint).booleanValue();

		String bAutoUpdateCheckerOn = getFormatProp("autoUpdateCheckerOn"); //$NON-NLS-1$
		if (bAutoUpdateCheckerOn != null && !bAutoUpdateCheckerOn.equals("")) //$NON-NLS-1$
			autoUpdateCheckerOn = new Boolean(bAutoUpdateCheckerOn).booleanValue();

		loadDragAndDropProperties();
	}

	/**
	 * Loads the properties concerning drag and drop
	 */
	private static void loadDragAndDropProperties() {
		String dndFileCopy = getFormatProp("dndFileCopy"); //$NON-NLS-1$
		if (dndFileCopy != null && !dndFileCopy.equals("")) //$NON-NLS-1$
			dndProperties.dndFileCopy = new Boolean(dndFileCopy).booleanValue();

		String dndFileCopyDatabase = getFormatProp("dndFileCopyDatabase"); //$NON-NLS-1$
		if (dndFileCopyDatabase != null && !dndFileCopyDatabase.equals("")) //$NON-NLS-1$
			dndProperties.dndFileCopyDatabase = new Boolean(dndFileCopyDatabase).booleanValue();

		String dndFilePrompt = getFormatProp("dndFilePrompt"); //$NON-NLS-1$
		if (dndFilePrompt != null && !dndFilePrompt.equals("")) //$NON-NLS-1$
			dndProperties.dndFilePrompt = new Boolean(dndFilePrompt).booleanValue();

		String dndFolderMap = getFormatProp("dndFolderMap"); //$NON-NLS-1$
		if (dndFolderMap != null && !dndFolderMap.equals("")) //$NON-NLS-1$
			dndProperties.dndFolderMap = new Boolean(dndFolderMap).booleanValue();

		String dndFolderMapRecursively = getFormatProp("dndFolderMapRecursively"); //$NON-NLS-1$
		if (dndFolderMapRecursively != null && !dndFolderMapRecursively.equals("")) //$NON-NLS-1$
			dndProperties.dndFolderMapRecursively = new Boolean(dndFolderMapRecursively).booleanValue();

		String dndFolderPrompt = getFormatProp("dndFolderPrompt"); //$NON-NLS-1$
		if (dndFolderPrompt != null && !dndFolderPrompt.equals("")) //$NON-NLS-1$
			dndProperties.dndFolderPrompt = new Boolean(dndFolderPrompt).booleanValue();

		String dndNoTextChoice = getFormatProp("dndNoTextChoice"); //$NON-NLS-1$
		if (dndNoTextChoice != null && !dndNoTextChoice.equals("")) //$NON-NLS-1$
			dndProperties.dndNoTextChoice = new Boolean(dndNoTextChoice).booleanValue();
	}

	/**
	 * Load the format properties for the appropriat file.
	 */
	public static void loadFormatProps() {
		FileInputStream fin = null;
		try {
			fin = new FileInputStream("System"+ProjectCompendium.sFS+"resources"+ProjectCompendium.sFS+"Format.properties"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
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
			fout = new FileOutputStream("System"+ProjectCompendium.sFS+"resources"+ProjectCompendium.sFS+"Format.properties"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}
		catch(FileNotFoundException e) {}

		try {
			if( fout != null ) {
				format.store( (OutputStream)fout, "Format properties" ); //$NON-NLS-1$
				fout.close();
			}
		}
		catch(IOException e ) {}
	}

	/**
	 * Return the value against the given key if found, else an empty String.
	 * @param key, the key to set.
	 * @return String, the associated value.
	 */
	public static String getFormatProp( String key ) {
		String value = ""; //$NON-NLS-1$

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
