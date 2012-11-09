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

import java.sql.*;
import java.util.*;
import java.awt.Color;
import java.awt.Font;
import java.io.*;
import javax.swing.*;

import com.compendium.*;

import com.compendium.core.db.management.DBAdminDatabase;
import com.compendium.core.db.management.DBConnection;
import com.compendium.core.db.management.DBConstantsDerby;
import com.compendium.core.db.management.DBConstantsMySQL;
import com.compendium.core.datamodel.*;
import com.compendium.core.db.*;
import com.compendium.core.db.management.*;
import com.compendium.core.*;

import com.compendium.ui.dialogs.*;

/**
 * This class updates a database structure as and when required by different versions of the software.
 */
public class DatabaseUpdate implements DBConstants, DBConstantsMySQL, DBConstantsDerby{


// 1.3 Original MySQL database version - release 1.3

// 1.3.1 VERSION CHANGES MYSQL ONLY - release 1.3.04

	/** Select all the link details for the links used in views (select all links!).*/
	private static final String SELECT_ALL_LINKS = "SELECT Link.LinkID, Link.CreationDate, Link.ModificationDate, Link.Author, Link.LinkType, " + //$NON-NLS-1$
													"Link.OriginalID, Link.FromNode, Link.ToNode, Link.ViewID, Link.Arrow, Link.CurrentStatus, "+ //$NON-NLS-1$
													"ViewLink.CreationDate, ViewLink.ModificationDate, ViewLink.CurrentStatus "+ //$NON-NLS-1$
													"FROM Link, ViewLink " + //$NON-NLS-1$
													"Where Link.LinkID = ViewLink.LinkID"; //$NON-NLS-1$

	/** NOT USED AT THE MOMENT */
	private static final String DROP_VIEWID_COLUMN = "ALTER TABLE Link DROP COLUMN ViewID"; //$NON-NLS-1$

	/** Insert a property into the System table.*/
	private static final String INSERT_LINK_GROUP = "INSERT INTO System (Property, Contents) VALUES ('linkgroup', ?)"; //$NON-NLS-1$

	/** Add a Label Column to the Link table; Drop the foreign key on the ViewID field; Set the ViewID default to '0';change the LinkType field to varchar 50.*/
	private static final String UPDATE_LINK_TABLE = "ALTER TABLE Link ADD COLUMN Label TEXT, DROP FOREIGN KEY FK_Link_3, DROP Index Link_ViewID_Ind, ALTER ViewID SET DEFAULT '0', MODIFY LinkType VARCHAR(50) NOT NULL"; //$NON-NLS-1$


// 1.3.2 VERSION  CHANGES MYSQL ONLY - release 1.3.05 Beta 1

	/** Delete a constraint on the ViewLink Table.*/
	private static final String DELETE_VIEWLINK_CONSTRAINT = "ALTER TABLE ViewLink DROP FOREIGN KEY FK_ViewLink_1"; //$NON-NLS-1$

	/**
	 *	Updates a constraint on the ViewLink Table.
	 *  Foreign Key name was dictated by MySQL. My name was ignored.
	 *	Mentioned actual name allocated by MyQSL below only for reference.
	 */
	private static final String CREATE_VIEWLINK_CONSTRAINT = "ALTER TABLE ViewLink ADD CONSTRAINT viewlink_ibfk_1 FOREIGN KEY (ViewID) REFERENCES Node (NodeID) ON DELETE CASCADE"; //$NON-NLS-1$

// 1.3.3 VERSION CHANGES MYSQL ONLY - release 1.4.0 Alpha 2

	/** Update the User table name to 'Users', as 'User' is a reserved work on Derby.*/
	/** And update the Connection table name to 'Connections', as 'Connection' is a reserved work on Derby.*/
	private static final String RENAME_TABLES = "RENAME TABLE User TO Users, Connection TO Connections"; //$NON-NLS-1$

// 1.3.4 VERSION CHANGES - release 1.4.0 Alpha 3

	/** Add a MediaIndex column to the ViewNode table.*/
	private static final String UPDATE_VIEWNODE_TABLE = "ALTER TABLE ViewNode ADD COLUMN MediaIndex DOUBLE"; //$NON-NLS-1$

	/** Fill the MediaIndex column of the ViewNode table with the same data as the CreationDate field.*/
	private static final String UPDATE_MEDIAINDEX = "UPDATE ViewNode set ViewNode.MediaIndex = ViewNode.CreationDate"; //$NON-NLS-1$

// 1.3.5 VERSION CHANGE - release 1.4.0 Alpha 4

	// ONLY WORKS ON MYSQL AT THE MOMENT:1st June 2005
	/** Drop the MediaIndex column from the ViewNode table.*/
	private static final String DROP_MEDIAINDEX_COLUMN = "ALTER TABLE ViewNode DROP COLUMN MediaIndex"; //$NON-NLS-1$

// 1.3.6 VERSION CHANGE - release 1.4.0 Alpha 4.1

	/** Add a CurrentStatus field to the Meeting table.*/
	private static final String MEETING_STATUS_UPDATE = "ALTER TABLE Meeting ADD COLUMN CurrentStatus INTEGER NOT NULL DEFAULT 0"; //$NON-NLS-1$

// 1.3.7 VERSION CHANGE - release 1.4

	/** Alter the node table's OriginalID field to increase the varchar length from 50 to 255 in MySQL syntax.*/
	private static final String UPDATE_NODE_ORIGINALID = "ALTER TABLE Node MODIFY OriginalID VARCHAR(255)"; //$NON-NLS-1$

	/** Alter the link table's OriginalID field to increase the varchar length from 50 to 255 in MySQL syntax.*/
	private static final String UPDATE_LINK_ORIGINALID = "ALTER TABLE Link MODIFY OriginalID VARCHAR(255)"; //$NON-NLS-1$

	/** Alter the node table's OriginalID field to increase the varchar length from 50 to 255 in Derby database syntax.*/
	private static final String UPDATE_NODE_ORIGINALID_DERBY = "ALTER TABLE Node ALTER OriginalID SET DATA TYPE VARCHAR(255)"; //$NON-NLS-1$

	/** Alter the link table's OriginalID field to increase the varchar length from 50 to 255 in Derby database syntax.*/
	private static final String UPDATE_LINK_ORIGINALID_DERBY = "ALTER TABLE Link ALTER OriginalID SET DATA TYPE VARCHAR(255)"; //$NON-NLS-1$

// 1.3.8 VERSION CHANGE - release 1.4.2 Alpha 2
// for UDIG references
	
	/** Alter the reference node table's Source field to increase the varchar length to Text in MySQL syntax.*/
	private static final String UPDATE_REFERENCE_SOURCE = "ALTER TABLE ReferenceNode MODIFY Source TEXT"; //$NON-NLS-1$
	
	/** Alter the reference node table's Source field to increase the varchar length to long varchar in Derby database syntax.*/
	private static final String UPDATE_REFERENCE_SOURCE_DERBY = "ALTER TABLE ReferenceNode ALTER Source SET DATA TYPE VARCHAR(2500)"; //$NON-NLS-1$
	
// 1.3.9 VERSION CHANGE  -release 1.4.2 - Beta 3
// DROP NodeProperty Table


	/** Add a ShowTags field to the ViewNode table.*/
	private static final String VIEWNODE_UPDATE_SHOWTAGS = "ALTER TABLE ViewNode ADD COLUMN ShowTags VARCHAR(1) NOT NULL DEFAULT 'Y'"; //$NON-NLS-1$

	/** Add a ShowText field to the ViewNode table.*/
	private static final String VIEWNODE_UPDATE_SHOWTEXT = "ALTER TABLE ViewNode ADD COLUMN ShowText VARCHAR(1) NOT NULL DEFAULT 'Y'"; //$NON-NLS-1$
		
	/** Add a ShowTrans field to the ViewNode table.*/
	private static final String VIEWNODE_UPDATE_SHOWTRANS = "ALTER TABLE ViewNode ADD COLUMN ShowTrans VARCHAR(1) NOT NULL DEFAULT 'Y'"; //$NON-NLS-1$

	/** Add a ShowWeight field to the ViewNode table.*/
	private static final String VIEWNODE_UPDATE_SHOWWEIGHT = "ALTER TABLE ViewNode ADD COLUMN ShowWeight VARCHAR(1) NOT NULL DEFAULT 'Y'"; //$NON-NLS-1$

	/** Add a SmallIcon field to the ViewNode table.*/
	private static final String VIEWNODE_UPDATE_SMALLICON = "ALTER TABLE ViewNode ADD COLUMN SmallIcon VARCHAR(1) NOT NULL DEFAULT 'N'"; //$NON-NLS-1$

	/** Add a HideIcon field to the ViewNode table.*/
	private static final String VIEWNODE_UPDATE_HIDEICON = "ALTER TABLE ViewNode ADD COLUMN HideIcon VARCHAR(1) NOT NULL DEFAULT 'N'"; //$NON-NLS-1$

	/** Add a LabelWrapWidth field to the ViewNode table.*/
	private static final String VIEWNODE_UPDATE_WRAPWIDTH = "ALTER TABLE ViewNode ADD COLUMN LabelWrapWidth INTEGER NOT NULL DEFAULT 20"; //$NON-NLS-1$

	/** Add a FontSize field to the ViewNode table.*/
	private static final String VIEWNODE_UPDATE_FONTSIZE = "ALTER TABLE ViewNode ADD COLUMN FontSize INTEGER NOT NULL DEFAULT 12"; //$NON-NLS-1$

	/** Add a FontFace field to the ViewNode table.*/
	private static final String VIEWNODE_UPDATE_FONTFACE = "ALTER TABLE ViewNode ADD COLUMN FontFace VARCHAR(100) NOT NULL DEFAULT 'Arial'"; //$NON-NLS-1$

	/** Add a FontFace field to the ViewNode table.*/
	private static final String VIEWNODE_UPDATE_FONTSTYLE = "ALTER TABLE ViewNode ADD COLUMN FontStyle INTEGER NOT NULL DEFAULT 0"; //$NON-NLS-1$

	/** Add a Foreground field to the ViewNode table.*/
	private static final String VIEWNODE_UPDATE_FOREGROUND = "ALTER TABLE ViewNode ADD COLUMN Foreground INTEGER NOT NULL DEFAULT 0"; //$NON-NLS-1$

	/** Add a Background field to the ViewNode table.*/
	private static final String VIEWNODE_UPDATE_BACKGROUND = "ALTER TABLE ViewNode ADD COLUMN Background INTEGER NOT NULL DEFAULT -1"; //$NON-NLS-1$
		
	/** Add last modification author column to the Node table.*/
	private static final String UPDATE_NODE_TABLE = "ALTER TABLE Node ADD COLUMN LastModAuthor VARCHAR(50)"; //$NON-NLS-1$

	/** Add link view id column to the Users table.*/
	private static final String UPDATE_USERS_TABLE = "ALTER TABLE Users ADD COLUMN LinkView VARCHAR(50)"; //$NON-NLS-1$

	/** Set state to READSTATE for all records.*/
	private static final String UPDATE_NODEUSERSTATE_TABLE = "UPDATE NodeUserState set State = "+ICoreConstants.READSTATE; //$NON-NLS-1$
	
// SET ALL CURRENT NODES AS READ FOR ALL CURRENT USERS IN THE USERS TABLE
	
// 1.5.0 VERSION CHANGE  -release 1.5 - Beta 1	
	
	/** Add a ViewId column to the Favorite table.*/
	private static final String FAVORITE_UPDATE = "ALTER TABLE Favorite ADD COLUMN ViewID VARCHAR(50)"; //$NON-NLS-1$

	/** Add the constraint for the Favorite table's new ViewID column*/
	private static final String FAVORITE_CONSTRAINT_UPDATE = "ALTER TABLE Favorite ADD CONSTRAINT FK_Favorite_3 FOREIGN KEY (ViewID) REFERENCES Node (NodeID) ON DELETE CASCADE";                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               //$NON-NLS-1$

// 1.5.3 VERSION CHANGE -release 1.5.3 Alpha 6
	
	private static final String VIEWLAYER_DELETE_EMPTIES = "DELETE from ViewLayer WHERE Scribble is null and Background='' and Grid='' and Shapes is null"; //$NON-NLS-1$
	
	private static final String VIEWLAYER_CHECK_DUPLICATES = "SELECT count(ViewID) as dups, ViewID from ViewLayer group by ViewID having count(ViewID) > 1"; //$NON-NLS-1$
	
	private static final String VIEWLAYER_SELECT_DUPLICATES = "SELECT Scribble, Background, Shapes, UserID from ViewLayer Where ViewID=?"; //$NON-NLS-1$
	
	/** try and merge the data if more than one entry for a view - note: grid not in use at this point.*/
	private static final String VIEWLAYER_UPDATE = "UPDATE ViewLayer set Scribble='?', Background='?', Shapes='?' WHERE UserID='?' AND ViewID='?'"; //$NON-NLS-1$
	
	/** Delete the duplicates so that the UserID field can be removed.*/
	private static final String VIEWLAYER_DELETE_DUPLICATE = "DELETE FROM ViewLayer WHERE UserID='?' AND ViewID='?'"; //$NON-NLS-1$
		
	/** Drop the USERID column from the ViewLayer table.*/
	private static final String VIEWLAYER_DROP_USERID_COLUMN = "ALTER TABLE ViewLayer DROP COLUMN UserID"; //$NON-NLS-1$
	
	/** Delete the userid foreign key constraint constraint on the ViewLayer Table.*/
	private static final String VIEWLAYER_DELETE_FOREIGNKEY = "ALTER TABLE ViewLayer DROP FOREIGN KEY FK_ViewLayer_1"; //$NON-NLS-1$
	
	private static final String VIEWLAYER_DROP_PRIMARYKEY = "ALTER TABLE ViewLayer DROP PRIMARY KEY"; //$NON-NLS-1$
	
	private static final String VIEWLAYER_ADD_PRIMARYKEY = "ALTER TABLE ViewLayer ADD PRIMARY KEY (ViewID)"; //$NON-NLS-1$
	
	/** Add a BackgroundColor column to the ViewLayer table.*/
	private static final String VIEWLAYER_UPDATE2 = "ALTER TABLE ViewLayer ADD COLUMN BackgroundColor INTEGER DEFAULT -1"; //-1 = white //$NON-NLS-1$
	
// 1.5.4 VERSION CHANGES - release 2.0 alpha 4
	// create new ViewTimeNode table 
	// create new Moves table

	/** Time base changed from seconds to milliseconds to gain precision to this adjusts existing show data.*/
	//private static final String UPDATE_SHOW_TIMES = "UPDATE ViewTimeNode set TimeToShow = TimeToShow*1000";
	
	/** Time base changed from seconds to milliseconds to gain precision to this adjusts existing hide data.*/
	//private static final String UPDATE_HIDE_TIMES = "UPDATE ViewTimeNode set TimeToHide = TimeToHide*1000";

// 1.5.5 VERSION CHANGES - release 2.0 alpha 5	
	// create new Movies table
	
	// move properties
	/** Get the movie data required to put in the new table .*/
	private static final String GET_MOVIE_DATA = "SELECT MovieID, XPos, YPos, Width, Height, Transparency, CreationDate, ModificationDate FROM Movies"; //$NON-NLS-1$
	
	//delete columns
	/** Drop the XPos Column from the Movie table.*/
	private static final String MOVIE_DROP_COLUMN_ISCONTROLLER = "ALTER TABLE Movies DROP COLUMN IsController"; //$NON-NLS-1$

	/** Drop the XPos Column from the Movie table.*/
	private static final String MOVIE_DROP_COLUMN_XPOS = "ALTER TABLE Movies DROP COLUMN XPos"; //$NON-NLS-1$

	/** Drop the YPos Column from the Movie table.*/
	private static final String MOVIE_DROP_COLUMN_YPOS = "ALTER TABLE Movies DROP Column YPos"; //$NON-NLS-1$

	/** Drop the Width Column from the Movie table.*/
	private static final String MOVIE_DROP_COLUMN_WIDTH = "ALTER TABLE Movies DROP COLUMN Width"; //$NON-NLS-1$

	/** Drop the Height Column from the Movie table.*/
	private static final String MOVIE_DROP_COLUMN_HEIGHT = "ALTER TABLE Movies DROP COLUMN Height"; //$NON-NLS-1$

	/** Drop the Transparency Column from the Movie table.*/
	private static final String MOVIE_DROP_COLUMN_TRANSPARENCY = "ALTER TABLE Movies DROP COLUMN Transparency"; //$NON-NLS-1$
	
	//add columns

	/** Add the column Names*/
	private static final String UPDATE_MOVIES_NAME = "ALTER TABLE Movies ADD COLUMN Name VARCHAR(255) DEFAULT ''"; //$NON-NLS-1$

	/** Add the column StartTime*/
	private static final String UPDATE_MOVIES_STARTTIME = "ALTER TABLE Movies ADD COLUMN StartTime DOUBLE NOT NULL DEFAULT 0"; //$NON-NLS-1$
	
// Version 2.0 - release 2.0 alpha 7
	
	/** Add a LabelWrapWidth field to the ViewLink table.*/
	private static final String VIEWLINK_UPDATE_WRAPWIDTH = "ALTER TABLE ViewLink ADD COLUMN LabelWrapWidth INTEGER NOT NULL DEFAULT 20"; //$NON-NLS-1$

	/** Add a Background field to the ViewLink table.*/
	private static final String VIEWLINK_UPDATE_ARROWSTYLE = "ALTER TABLE ViewLink ADD COLUMN ArrowType INTEGER NOT NULL DEFAULT 1"; //$NON-NLS-1$

	/** Add a Background field to the ViewLink table.*/
	private static final String VIEWLINK_UPDATE_LINESTYLE = "ALTER TABLE ViewLink ADD COLUMN LinkStyle INTEGER NOT NULL DEFAULT 0"; //$NON-NLS-1$

	/** Add a Background field to the ViewLink table.*/
	private static final String VIEWLINK_UPDATE_LINEDASHED = "ALTER TABLE ViewLink ADD COLUMN LinkDashed INTEGER NOT NULL DEFAULT 0"; //$NON-NLS-1$

	/** Add a Background field to the ViewLink table.*/
	private static final String VIEWLINK_UPDATE_LINEWEIGHT = "ALTER TABLE ViewLink ADD COLUMN LinkWeight INTEGER NOT NULL DEFAULT 1"; //$NON-NLS-1$

	/** Add a Background field to the ViewLink table.*/
	private static final String VIEWLINK_UPDATE_LINECOLOUR = "ALTER TABLE ViewLink ADD COLUMN LinkColour INTEGER NOT NULL DEFAULT "+Color.black.getRGB(); //$NON-NLS-1$

	/** Add a FontSize field to the ViewLink table.*/
	private static final String VIEWLINK_UPDATE_FONTSIZE = "ALTER TABLE ViewLink ADD COLUMN FontSize INTEGER NOT NULL DEFAULT 12"; //$NON-NLS-1$

	/** Add a FontFace field to the ViewLink table.*/
	private static final String VIEWLINK_UPDATE_FONTFACE = "ALTER TABLE ViewLink ADD COLUMN FontFace VARCHAR(100) NOT NULL DEFAULT 'Arial'"; //$NON-NLS-1$

	/** Add a FontFace field to the ViewLink table.*/
	private static final String VIEWLINK_UPDATE_FONTSTYLE = "ALTER TABLE ViewLink ADD COLUMN FontStyle INTEGER NOT NULL DEFAULT 0"; //$NON-NLS-1$

	/** Add a Foreground field to the ViewLink table.*/
	private static final String VIEWLINK_UPDATE_FOREGROUND = "ALTER TABLE ViewLink ADD COLUMN Foreground INTEGER NOT NULL DEFAULT "+Color.black.getRGB(); //$NON-NLS-1$

	/** Add a Background field to the ViewLink table.*/
	private static final String VIEWLINK_UPDATE_BACKGROUND = "ALTER TABLE ViewLink ADD COLUMN Background INTEGER NOT NULL DEFAULT "+Color.white.getRGB(); //$NON-NLS-1$
		

	/** Move the arrow data in the ViewLink table from the Link table.*/
	private static final String VIEWLINK_UPDATE_ARROWDATA = "UPDATE ViewLink set ViewLink.ArrowType = (Select Arrow from Link Where Link.LinkID = ViewLink.LinkID)"; //$NON-NLS-1$
	
	/** Add last modification author column to the Node table.*/
	private static final String DROP_LINK_TABLE_ARROW = "ALTER TABLE Link DROP COLUMN Arrow"; //$NON-NLS-1$
	
// Version 2.0.1 - release 2.0 Alpha 10 - check for MySQL table mistake lower case transparency column header
	private static final String RENAME_TRANSPARENCY = "ALTER TABLE MovieProperties CHANGE COLUMN transparency Transparency FLOAT NOT NULL DEFAULT 1.0";
	
////////////////////

	/** Holds the Link and ViewLink table data when transferring for table update.*/
	private static StringBuffer	data = null;

	/** An integer representing the total count of the progress updates required. */
	private static final 	int DEFAULT_COUNT = 100;

	/** An integer representing the increment to use for the progress updates */
	private static 			int	increment = 1;

	// PROGRESS BAR FOR DATABASE UPDATE CHECK
	/** The progress bar held in the dialog.*/
	private static JProgressBar		oProgressBar = null;

	/** The progress dialog holding the progress.*/
	private static UIProgressDialog	oProgressDialog = null;

	/** The counter used by the progress bar.*/
	private static int				nCount = 0;

	/** the thread that runs the progress bar.*/
	private static ProgressThread	oThread = null;

	/** The parent frame to show message dialogs in.*/
	private static JFrame oParent = null;

	/** The total count for the progress bar.*/
	private static int 	nFinalCount = DEFAULT_COUNT;

	/** The number of steps to update one database project completely.*/
	private static int 	nSteps = 10;


	/**
	 * Update the database.
	 * @param adminDatabase, the database administration object to use.
	 * @param parent, the parent frame.
	 * @param sProject, the database project to update.
	 */
	public static boolean updateDatabase(DBAdminDatabase adminDatabase, JFrame parent, String sProject) {

		oParent = parent;

		int response = JOptionPane.showConfirmDialog(parent, 				
				LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "UIDatabaseUpdate.projectNeedsUpdatingA")+"\n\n"+//$NON-NLS-1$ //$NON-NLS-2$
				LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "UIDatabaseUpdate.projectNeedsUpdatingB")+"\n\n"+//$NON-NLS-1$ //$NON-NLS-2$
				LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "UIDatabaseUpdate.projectNeedsUpdatingC")+"\n\n", //$NON-NLS-1$ //$NON-NLS-2$
				LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "UIDatabaseUpdate.updateProject"), //$NON-NLS-1$
				JOptionPane.YES_NO_OPTION); //$NON-NLS-1$

		if (response == JOptionPane.NO_OPTION || response == JOptionPane.CLOSED_OPTION) {
			return false;
		}
		else {
			DBDatabaseManager databaseManager = adminDatabase.getDatabaseManager();

			oThread = new ProgressThread(LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "UIDatabaseUpdate.updating"), LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "UIDatabaseUpdate.updatingTitle")); //$NON-NLS-1$ //$NON-NLS-2$
			oThread.start();
			progressCount(nSteps);

			DBConnection dbcon = databaseManager.requestConnection(sProject);
			if (!doUpdate(adminDatabase, dbcon, sProject)) {
				if (oProgressDialog != null)
					progressComplete();
				databaseManager.releaseConnection(sProject, dbcon);
				return false;
			}
			databaseManager.releaseConnection(sProject, dbcon);
			if (oProgressDialog != null)
				progressComplete();
			return true;
		}
	}

	/**
	 * Update the database structure for the given database without prompting.
	 * @param adminDatabase, the database administration object to use.
	 * @param dbcon, the connection object to use to connect to the database.
	 * @param parent, the parent frame.
	 * @param sDatabaseName, the name of the databases being checked/updated.
	 */
	public static boolean updateDatabase(DBAdminDatabase adminDatabase, DBConnection dbcon, JFrame parent, String sDatabaseName) {

		oParent = parent;

		oThread = new ProgressThread(LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "UIDatabaseUpdate.updating2"), LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "UIDatabaseUpdate.updatingTitle")); //$NON-NLS-1$ //$NON-NLS-2$
		oThread.start();
		progressCount(nSteps);

		boolean successful = doUpdate(adminDatabase, dbcon, sDatabaseName);

		if (oProgressDialog != null)
			progressComplete();

		return successful;
	}

	/**
	 * Update the database structure for the given database.
	 * @param adminDatabase, the database administration object to use.
	 * @param dbcon, the connection object to use to connect to the database.
	 * @param sDatabaseName, the name of the databases being checked/updated.
	 */
	private static boolean doUpdate(DBAdminDatabase adminDatabase, DBConnection dbcon, String sDatabaseName) {

		boolean successful = false;

		try {
			Connection con = dbcon.getConnection();

			if (con != null) {
				String originalVersion = adminDatabase.checkVersion(con);
				String version = new String(originalVersion);

				if (!version.equals(ICoreConstants.sDATABASEVERSION)) {
					String sFriendlyName = adminDatabase.getFriendlyName(sDatabaseName);
					progressUpdate(increment, LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "UIDatabaseUpdate.updatingScheme")+sFriendlyName); //$NON-NLS-1$

					if (version.equals("1.3")) { //$NON-NLS-1$
						progressUpdate(increment, sFriendlyName+": Updating link table.."); //$NON-NLS-1$
						successful = updateLinkTable(dbcon, sDatabaseName);
						if (successful) {
							progressUpdate(increment, sFriendlyName+": Updating linkgroup.."); //$NON-NLS-1$
							successful = insertDefaultLinkGroup(con);
							if (successful) {
								progressUpdate(increment, sFriendlyName+": Updating version.."); //$NON-NLS-1$
								successful = adminDatabase.updateVersion(con, "1.3.1"); //$NON-NLS-1$
								if (successful)
									version = "1.3.1"; //$NON-NLS-1$
							}
						}
					}
					if (version.equals("1.3.1")) { //$NON-NLS-1$
						progressUpdate(increment, sFriendlyName+": Updating LinkView constraints.."); //$NON-NLS-1$
						successful = updateViewLinkTable(dbcon, adminDatabase, sDatabaseName);
						if (successful) {
							progressUpdate(increment, sFriendlyName+": Updating version.."); //$NON-NLS-1$
							successful = adminDatabase.updateVersion(con, "1.3.2"); //$NON-NLS-1$
							if (successful)
								version = "1.3.2"; //$NON-NLS-1$
						}
					}
					if (version.equals("1.3.2")) { //$NON-NLS-1$
						progressUpdate(increment, sFriendlyName+": Renaming Tables.."); //$NON-NLS-1$
						successful = renameTables(dbcon);
						if (successful) {
							progressUpdate(increment, sFriendlyName+": Updating version.."); //$NON-NLS-1$
							successful = adminDatabase.updateVersion(con, "1.3.3"); //$NON-NLS-1$
							if (successful)
								version = "1.3.3"; //$NON-NLS-1$
						}
					}
					if (version.equals("1.3.3")) { //$NON-NLS-1$
						// DON'T WANT THIS TO HAPPEN AS ONLY TAKEN OUT AGAIN IN NEXT SECTION AND
						// CAN'T DROP COLUMN YET IN DERBY 1st JUNE 05
						//progressUpdate(increment, sFriendlyName+": Adding Media Index Column..");
						//successful = addMediaIndex(dbcon);
						successful = true;
						if (successful) {
							progressUpdate(increment, sFriendlyName+": Updating version.."); //$NON-NLS-1$
							successful = adminDatabase.updateVersion(con, "1.3.4"); //$NON-NLS-1$
							if (successful)
								version = "1.3.4"; //$NON-NLS-1$
						}
					}
					if (version.equals("1.3.4")) { //$NON-NLS-1$
						progressUpdate(increment, sFriendlyName+": Removing Media Index Column.."); //$NON-NLS-1$
						successful = removeMediaIndex(dbcon, adminDatabase, sDatabaseName);
						progressUpdate(increment, sFriendlyName+": Creating MediaIndex Table.."); //$NON-NLS-1$
						successful = createMediaIndexTable(dbcon, adminDatabase);
						if (successful) {
							progressUpdate(increment, sFriendlyName+": Updating version.."); //$NON-NLS-1$
							successful = adminDatabase.updateVersion(con, "1.3.5"); //$NON-NLS-1$
							if (successful)
								version = "1.3.5"; //$NON-NLS-1$
						}
					}
					if (version.equals("1.3.5")) { //$NON-NLS-1$
						progressUpdate(increment, sFriendlyName+": Adding MeetingStatus Column.."); //$NON-NLS-1$
						successful = addMeetingStatusColumn(dbcon);
						if (successful) {
							progressUpdate(increment, sFriendlyName+": Updating version.."); //$NON-NLS-1$
							successful = adminDatabase.updateVersion(con, "1.3.6"); //$NON-NLS-1$
							if (successful)
								version = "1.3.6"; //$NON-NLS-1$
						}
					}
					if (version.equals("1.3.6")) { //$NON-NLS-1$
						progressUpdate(increment, sFriendlyName+": Updating Original ID fields.."); //$NON-NLS-1$
						successful = updateOriginalID(dbcon, adminDatabase);
						if (successful) {
							progressUpdate(increment, sFriendlyName+": Updating version.."); //$NON-NLS-1$
							successful = adminDatabase.updateVersion(con, "1.3.7"); //$NON-NLS-1$
							if (successful)
								version = "1.3.7"; //$NON-NLS-1$
						}
					}
					if (version.equals("1.3.7")) { //$NON-NLS-1$
						progressUpdate(increment, sFriendlyName+": Updating Reference Source field length.."); //$NON-NLS-1$
						successful = updateReferenceSource(dbcon, adminDatabase);
						if (successful) {
							progressUpdate(increment, sFriendlyName+": Updating version.."); //$NON-NLS-1$
							successful = adminDatabase.updateVersion(con, "1.3.8"); //$NON-NLS-1$
							if (successful)
								version = "1.3.8"; //$NON-NLS-1$
						}
					}
					if (version.equals("1.3.8")) { //$NON-NLS-1$
						progressUpdate(increment, sFriendlyName+": Drop Node Property, Update NodeView.."); //$NON-NLS-1$
						successful = replaceNodePropertyTable(dbcon, adminDatabase);						
						if (successful) {						
							successful = addGroupwareColumns(dbcon);							
							if (successful) {
								progressUpdate(increment, sFriendlyName+": Updating version.."); //$NON-NLS-1$
								successful = adminDatabase.updateVersion(con, "1.3.9"); //$NON-NLS-1$
								if (successful) {
									version = "1.3.9"; //$NON-NLS-1$
								}
							}
						}
					}	
					if (version.equals("1.3.9")) { //$NON-NLS-1$
						progressUpdate(increment, sFriendlyName+": Add ViewID column to Favorite.."); //$NON-NLS-1$
						successful = updateFavoriteTable(dbcon);
						if (successful) {
							progressUpdate(increment, sFriendlyName+": Updating version..");						 //$NON-NLS-1$
							// Make database version match release version for 1.5
							successful = adminDatabase.updateVersion(con, "1.5.0"); //$NON-NLS-1$
							if (successful) {
								version = "1.5.0"; //$NON-NLS-1$
							}
						}
					}																					
					if (version.equals("1.5.0")) { //$NON-NLS-1$
						progressUpdate(increment, sFriendlyName+": Remove UserID column From ViewLayer.."); //$NON-NLS-1$
						removeUserIDFromLayerView(dbcon);
						progressUpdate(increment, sFriendlyName+": Add Background Color to ViewLayer.."); //$NON-NLS-1$
						updateViewLayer(dbcon, adminDatabase);							
						progressUpdate(increment, sFriendlyName+": Updating version..");	 //$NON-NLS-1$
						// Make database version match release version for 1.5.3
						successful = adminDatabase.updateVersion(con, "1.5.3"); //$NON-NLS-1$
						if (successful) {
							version = "1.5.3"; //$NON-NLS-1$
						}
					}																					
					if (version.equals("1.5.3")) {//$NON-NLS-1$
						progressUpdate(increment, sFriendlyName+": Create ViewTimeNode Table..");//$NON-NLS-1$
						createViewTimeNodeTable(dbcon, adminDatabase);
						
						progressUpdate(increment, sFriendlyName+": Create Movies Table..");//$NON-NLS-1$
						createMoviesTable(dbcon, adminDatabase);
						
						progressUpdate(increment, sFriendlyName+": Updating version..");	//$NON-NLS-1$
						successful = adminDatabase.updateVersion(con, "1.5.4");//$NON-NLS-1$
						if (successful) {
							version = "1.5.4";//$NON-NLS-1$
						}
					}	
					if (version.equals("1.5.4")) {//$NON-NLS-1$
						progressUpdate(increment, sFriendlyName+": Add MovieProperties Table..");//$NON-NLS-1$
						createMoviePropertiesTable(dbcon, adminDatabase);
						
						progressUpdate(increment, sFriendlyName+": Updating version..");	//$NON-NLS-1$
						successful = adminDatabase.updateVersion(con, "1.5.5");//$NON-NLS-1$
						if (successful) {
							version = "1.5.5";//$NON-NLS-1$
						}
					}	
					if (version.equals("1.5.5")) {//$NON-NLS-1$
						progressUpdate(increment, sFriendlyName+": Update ViewLink Table..");//$NON-NLS-1$
						updateViewLinkTable(dbcon, adminDatabase);
						
						progressUpdate(increment, sFriendlyName+": Updating version..");	//$NON-NLS-1$
						successful = adminDatabase.updateVersion(con, "2.0");//$NON-NLS-1$
						if (successful) {
							version = "2.0";//$NON-NLS-1$
						}
					}						
					if (version.equals("2.0")) {//$NON-NLS-1$
						progressUpdate(increment, sFriendlyName+": Create LinkedFiles Table..");//$NON-NLS-1$
						createLinkedFileTable(dbcon, adminDatabase);

						// fix mistake in column name on MySQL version
						if (originalVersion.equals("1.5.4") 
							|| originalVersion.equals("1.5.5")
							&& adminDatabase.getDatabaseManager().getDatabaseType() == ICoreConstants.MYSQL_DATABASE) {
							progressUpdate(increment, sFriendlyName+": Fixing Movie Properties Table..");//$NON-NLS-1$
							fixMoviePropertiesTable(dbcon);
						}
						
						progressUpdate(increment, sFriendlyName+": Updating version..");	//$NON-NLS-1$
						successful = adminDatabase.updateVersion(con, "2.0.1");//$NON-NLS-1$
						if (successful) {
							version = "2.0.1";//$NON-NLS-1$
						}
					}	
				}
				else {
					successful = true;
				}
			}
		}
		catch (Exception e) {
			System.out.println("Exception: "+e.getMessage()); //$NON-NLS-1$
  			e.printStackTrace();
  			System.out.flush();
			successful = false;
  		}

		if (!successful) {
			ProjectCompendium.APP.displayError(
					LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "UIDatabaseUpdate.unableToUpdateStructureA")+"\n"+ //$NON-NLS-1$ //$NON-NLS-2$
					LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "UIDatabaseUpdate.unableToUpdateStructureB")+"\n"); //$NON-NLS-1$ //$NON-NLS-2$
		}

		return successful;
	}


// VERSION 1.3.1

	/**
	 * Add a field called 'Label' to the Link table, and drop the foreign key on ViewID
	 * @param dbcon, the DBConnection to use to run the sql.
	 * @param sDatabaseName, the name of the databases being updated.
	 */
	private static boolean updateLinkTable(DBConnection dbcon, String sDatabaseName) throws SQLException {

		Connection con = dbcon.getConnection();

		// IF CALLED AFTER A RESTORE, TABLE MAY BE CORRECT ALREADY
		// CHECK INDEX TO DROP. IF EXISTS PROCEED TO TABLE UPDATE
		// IF DOES NOT EXIST, TABLE ALREADY UPDATED
		DatabaseMetaData dbmd = con.getMetaData();
		ResultSet rs = dbmd.getIndexInfo(null, null, "LINK", false, false); //$NON-NLS-1$

		//DOES NOT WORK IN DERBY
		//PreparedStatement pstmt = con.prepareStatement("SHOW INDEX FROM Link");
		//ResultSet rs = pstmt.executeQuery();
		boolean proceed = false;

		if (rs != null) {
			while (rs.next()) {
				String	sName = rs.getString(6);
				if (sName.equalsIgnoreCase("Link_ViewID_Ind")) { //$NON-NLS-1$
					proceed = true;
					break;
				}
			}
		}

		if (proceed) {

			data = new StringBuffer(1000);

			Vector links = getAllLinks(con);

			// INCASE OF CRASH - BACKUP
			try {
				FileWriter fileWriter = new FileWriter("Backups"+System.getProperty("file.separator")+"LinkData_"+sDatabaseName+".sql"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
				fileWriter.write(data.toString());
				fileWriter.close();
		        data = null;
			}
			catch(IOException io) {
				System.out.println("unable to backup link data"); //$NON-NLS-1$
			}

			PreparedStatement pstmt2 = con.prepareStatement(MYSQL_DROP_VIEWLINK_TABLE);
			pstmt2.executeUpdate() ;
			pstmt2.close();

			PreparedStatement pstmt3 = con.prepareStatement(MYSQL_DROP_LINK_TABLE);
			pstmt3.executeUpdate() ;
			pstmt3.close();

			PreparedStatement pstmt4 = con.prepareStatement(MYSQL_CREATE_LINK_TABLE);
			pstmt4.executeUpdate() ;
			pstmt4.close();

			PreparedStatement pstmt5 = con.prepareStatement(MYSQL_CREATE_VIEWLINK_TABLE);
			pstmt5.executeUpdate() ;
			pstmt5.close();

			String sLinkID = ""; //$NON-NLS-1$
			java.util.Date dCreationDate = null;
			java.util.Date dModificationDate = null;
			java.util.Date dCreationDate2 = null;
			java.util.Date dModificationDate2 = null;

			String sAuthor = ""; //$NON-NLS-1$
			String sType = ""; //$NON-NLS-1$
			String sOriginalID = ""; //$NON-NLS-1$
			String sFrom = ""; //$NON-NLS-1$
			String sTo = ""; //$NON-NLS-1$
			String sViewID = ""; //$NON-NLS-1$
			int nCurrentStatus = 0;
			int nStatus = 0;

			int count = links.size();

			progressCount(count+2);

			for (int i=0; i<count; i++) {

				progressUpdate(increment, "Updating link table.."); //$NON-NLS-1$

				Vector link = (Vector)links.elementAt(i);

				sLinkID = (String)link.elementAt(0);
				dCreationDate = (java.util.Date)link.elementAt(1);
				dModificationDate = (java.util.Date)link.elementAt(2);
				sAuthor = (String)link.elementAt(3);
				sType = (String)link.elementAt(4);
				sOriginalID = (String)link.elementAt(5);
				sFrom = (String)link.elementAt(6);
				sTo = (String)link.elementAt(7);
				sViewID = (String)link.elementAt(8);
				nCurrentStatus = ((Integer)link.elementAt(9)).intValue();
				dCreationDate2 = (java.util.Date)link.elementAt(10);
				dModificationDate2 = (java.util.Date)link.elementAt(11);
				nStatus = ((Integer)link.elementAt(12)).intValue();

				DBLink.recreate(dbcon, sLinkID, dCreationDate, dModificationDate, sAuthor, sType, "", sOriginalID, sFrom, sTo, "", nCurrentStatus); //$NON-NLS-1$ //$NON-NLS-2$
				DBViewLink.recreate(dbcon, sViewID, sLinkID, dCreationDate2, dModificationDate2, nStatus, 25, 1,0,0,1,0, 12, "Arial", Font.PLAIN, 0, -1); //$NON-NLS-1$
			}

			// THIS ALTER TABLE STATEMENT ONLY WORKED ON MYSQL 4.0.18 AND LATER
			// EVEN IF THE STATEMENT WAS BROKEN UP. MYSQL THREW A TABLE RENAME EXCEPTION
			//PreparedStatement pstmt2 = con.prepareStatement(UPDATE_LINK_TABLE);
			//int nRowCount = pstmt2.executeUpdate() ;
			//pstmt2.close();
			//if (nRowCount > 0) {
			//	return true;
			//}

			return true;
		}
		return true;
	}

	/**
	 *  Retrieves all the links.
	 *
	 *	@param con, the Connection object to access the database with.
	 *	@return Vector, a list of all Link objects.
	 */
	private static Vector getAllLinks(Connection con) throws SQLException {

		data.append(MYSQL_DROP_VIEWLINK_TABLE+";\n\n"); //$NON-NLS-1$
		data.append(MYSQL_DROP_LINK_TABLE+";\n\n"); //$NON-NLS-1$
		data.append(MYSQL_CREATE_LINK_TABLE+";\n\n"); //$NON-NLS-1$
		data.append(MYSQL_CREATE_VIEWLINK_TABLE+";\n\n"); //$NON-NLS-1$

		Vector links = new Vector(51);

		PreparedStatement pstmt = con.prepareStatement(SELECT_ALL_LINKS);

		ResultSet rs = pstmt.executeQuery();
		Vector link = null;
		if (rs != null) {
			while (rs.next()) {
				String		sLinkID		= rs.getString(1);
				long		dbCDate		= new Double(rs.getLong(2)).longValue();
				long		dbMDate 	= new Double(rs.getLong(3)).longValue();
				java.util.Date		oCDate		= new java.util.Date(dbCDate);
				java.util.Date		oMDate		= new java.util.Date(dbMDate);
				String		sAuthor		= rs.getString(4);
				String		sType		= rs.getString(5);
				String		sOriginalID	= rs.getString(6);
				String		sFrom		= rs.getString(7);
				String		sTo			= rs.getString(8);
				String 		sViewID		= rs.getString(9);
				Integer		nArrow		= new Integer(rs.getInt(10));
				Integer		nCurrentStatus	= new Integer(rs.getInt(11));

				long		dbCDate2	= new Double(rs.getLong(12)).longValue();
				long		dbMDate2	= new Double(rs.getLong(13)).longValue();
				java.util.Date		oCDate2		= new java.util.Date(dbCDate);
				java.util.Date		oMDate2		= new java.util.Date(dbMDate);
				Integer		nStatus	= new Integer(rs.getInt(14));

				link = new Vector(10);
				link.addElement(sLinkID);
				link.addElement(oCDate);
				link.addElement(oMDate);
				link.addElement(sAuthor);
				link.addElement(sType);
				link.addElement(sOriginalID);
				link.addElement(sFrom);
				link.addElement(sTo);
				link.addElement(sViewID);
				link.addElement(nArrow);
				link.addElement(nCurrentStatus);
				link.addElement(oCDate2);
				link.addElement(oMDate2);
				link.addElement(nStatus);

				data.append(DBConstants.INSERT_LINK_QUERY_BASE);
				data.append("("); //$NON-NLS-1$
				data.append("\'"+sLinkID+"\',"); //$NON-NLS-1$ //$NON-NLS-2$
				data.append("\'"+CoreUtilities.cleanSQLText(sAuthor, FormatProperties.nDatabaseType)+"\',"); //$NON-NLS-1$ //$NON-NLS-2$
				data.append(dbCDate+","); //$NON-NLS-1$
				data.append(dbMDate+","); //$NON-NLS-1$
				data.append("\'"+sType+"\',"); //$NON-NLS-1$ //$NON-NLS-2$
				data.append("\'"+sOriginalID+"\',"); //$NON-NLS-1$ //$NON-NLS-2$
				data.append("\'"+sFrom+"\',"); //$NON-NLS-1$ //$NON-NLS-2$
				data.append("\'"+sTo+"\',"); //$NON-NLS-1$ //$NON-NLS-2$
				data.append("\'\',"); //$NON-NLS-1$
				data.append(nArrow.toString()+","); //$NON-NLS-1$
				data.append(nCurrentStatus.toString());
				data.append(");\n"); //$NON-NLS-1$

				data.append(DBConstants.INSERT_VIEWLINK_QUERY_BASE);
				data.append("("); //$NON-NLS-1$
				data.append("\'"+sViewID+"\',"); //$NON-NLS-1$ //$NON-NLS-2$
				data.append("\'"+sLinkID+"\',"); //$NON-NLS-1$ //$NON-NLS-2$
				data.append(dbCDate2+","); //$NON-NLS-1$
				data.append(dbMDate2+","); //$NON-NLS-1$
				data.append(nStatus.toString());
				data.append(");\n"); //$NON-NLS-1$

				links.addElement(link);
			}
		}
		pstmt.close();
		return links;
	}

	/**
	 * Drop the 'ViewID' column, its index and constraint from the Link table.
	 * @param con, the connection to use to run the sql.
	 */
	 // CURRENTLY DOES NOT WORK - DOES NOT ALLOW A DROP COLUMN - Rename table error 150.
	private static boolean dropViewIDColumn(Connection con) throws SQLException {

		PreparedStatement pstmt = con.prepareStatement(DROP_VIEWID_COLUMN);
		int nRowCount = pstmt.executeUpdate() ;
		pstmt.close();
		if (nRowCount > 0) {
			return true;
		}

		return false;
	}

	/**
	 * Insert the default link group into the System table.
	 */
	private static boolean insertDefaultLinkGroup(Connection con) throws SQLException {

		PreparedStatement pstmt = con.prepareStatement(INSERT_LINK_GROUP);
		pstmt.setString(1, "1"); //$NON-NLS-1$
		int nRowCount = pstmt.executeUpdate() ;
		pstmt.close();
		if (nRowCount > 0)
			return true;

		return false;
	}

// 1.3.2

	/**
	 * Delete old constraint and call 'updateViewLinkTableMore' to
	 * Add ON CASCADE DELETE to the LinkView table NodeID foreign key.
	 * @param dbcon, the DBConnection to use to run the sql.
	 * @param adminDatabase, the database administration object to use.
	 * @param sDatabaseName, the name of the databases being updated.
	 */
	private static boolean updateViewLinkTable(DBConnection dbcon, DBAdminDatabase adminDatabase, String sDatabaseName) throws SQLException {

		Connection con = dbcon.getConnection();

		progressUpdate(increment, "Delete constraint.."); //$NON-NLS-1$

		boolean proceed = false;
		boolean success = false;
		try {
			PreparedStatement pstmt = con.prepareStatement(DELETE_VIEWLINK_CONSTRAINT);
			int nRowCount = pstmt.executeUpdate();
			pstmt.close();
			if (nRowCount > 0) {
				success = updateViewLinkTableMore(dbcon, adminDatabase, sDatabaseName);
			}
		}
		catch(SQLException ex) {
			ex.printStackTrace();

			//System.out.println("exception on updateViewLinkTable");

			// CHECK IF IT HAS REALLY DELETED THE CONSTRAINT AND JUST SPAZZED OUT RENAMING TEMPORARY TABLE
			// IF SO, CONTINUE.
			boolean bFound = false;

			// FIRST GET NEW CONNECTION, OR CHECK DOES NOT WORK
			DBConnection dbcon2 = adminDatabase.getDatabaseManager().requestConnection(sDatabaseName);
			Connection con2 = dbcon2.getConnection();

			DatabaseMetaData dbmd = con2.getMetaData();
			ResultSet rs = dbmd.getExportedKeys(null, null, "VIEWLINK"); //$NON-NLS-1$

			if (rs != null) {
				while (rs.next()) {
					String	sName = rs.getString(12);
					if (sName.equalsIgnoreCase("FK_ViewLink_1")) { //$NON-NLS-1$
						bFound = true;
					}
				}
			}

			rs.close();

			if (!bFound) {
				success = updateViewLinkTableMore(dbcon2, adminDatabase, sDatabaseName);
			}
			else {
				adminDatabase.getDatabaseManager().releaseConnection(sDatabaseName, dbcon2);
				throw ex;
			}

			adminDatabase.getDatabaseManager().releaseConnection(sDatabaseName, dbcon2);
		}

		return success;
	}

	/**
	 * Add ON CASCADE DELETE to the LinkView table NodeID foreign key
	 * @param dbcon, the DBConnection to use to run the sql.
	 * @param adminDatabase, the database administration object to use.
	 * @param sDatabaseName, the name of the databases being updated.
	 */
	private static boolean updateViewLinkTableMore(DBConnection dbcon, DBAdminDatabase adminDatabase, String sDatabaseName) throws SQLException {

		Connection con = dbcon.getConnection();

		progressUpdate(increment, "Create constraint..."); //$NON-NLS-1$

		try {
			PreparedStatement pstmt = con.prepareStatement(CREATE_VIEWLINK_CONSTRAINT);
			int nRowCount2 = pstmt.executeUpdate() ;
			pstmt.close();
			if (nRowCount2 > 0)
				return true;
		}
		catch(SQLException ex) {
			//System.out.println("in SQLException");
			ex.printStackTrace();

			// CHECK IF IT HAS REALLY CREATED THE NEW CONSTRAINT AND JUST SPAZZED OUT RENAMING TEMPORARY TABLE
			// IF SO, RETURN TRUE.

			// FIRST GET NEW CONNECTION, OR CHECK DOES NOT WORK
			DBConnection dbcon2 = adminDatabase.getDatabaseManager().requestConnection(sDatabaseName);
			Connection con2 = dbcon2.getConnection();

			DatabaseMetaData dbmd = con2.getMetaData();
			ResultSet rs = dbmd.getExportedKeys(null, null, "VIEWLINK"); //$NON-NLS-1$
			if (rs != null) {
				while (rs.next()) {
					String	sName = rs.getString(12);
					if (sName.equalsIgnoreCase("viewlink_ibfk_1")) { //$NON-NLS-1$
						rs.close();
						adminDatabase.getDatabaseManager().releaseConnection(sDatabaseName, dbcon2);
						return true;
					}
				}
			}

			adminDatabase.getDatabaseManager().releaseConnection(sDatabaseName, dbcon2);
		}
		return false;
	}

// 1.3.3

	/**
	 * Change the User and Connection table names as they are reserved words on Derby
	 * @param dbcon, the DBConnection to use to run the sql.
	 */
	private static boolean renameTables(DBConnection dbcon) throws SQLException {

		Connection con = dbcon.getConnection();
		Statement stmt = con.createStatement();
		stmt.executeUpdate(RENAME_TABLES);
		stmt.close();

		return true;
	}

// 1.3.4

	/**
	 * Add a MediaIndex column to the ViewNode table.
	 * @param dbcon, the DBConnection to use to run the sql.
	 */
	private static boolean addMediaIndex(DBConnection dbcon) throws SQLException {

		Connection con = dbcon.getConnection();

		PreparedStatement pstmt = con.prepareStatement(UPDATE_VIEWNODE_TABLE);

		//DERBY RETURNED INT 0, so no point in checking.
		pstmt.executeUpdate();
		pstmt.close();

		pstmt = con.prepareStatement(UPDATE_MEDIAINDEX);
		int nReturn = pstmt.executeUpdate();

		pstmt.close();
		if (nReturn > 0)
			return true;
		return false;
	}

// 1.3.5

	/**
	 * Remove the MediaIndex column from the ViewNode table.
	 * @param dbcon the DBConnection to use to run the sql.
	 * @param adminDatabase the database admin object required to check database type.
	 * @param sDatabaseName the name of the databases being updated.
	 */
	private static boolean removeMediaIndex(DBConnection dbcon, DBAdminDatabase adminDatabase, String sDatabaseName) throws SQLException {

		Connection con = dbcon.getConnection();
		// 1st June 05: CURRENTLY DERBY DOES NOT SUPPORT DROP COLUMN
		if (adminDatabase.getDatabaseManager().getDatabaseType() == ICoreConstants.MYSQL_DATABASE) {
			DatabaseMetaData dbmd = con.getMetaData();
			ResultSet rs = dbmd.getColumns(sDatabaseName, null, "VIEWNODE", "MEDIAINDEX"); //$NON-NLS-1$ //$NON-NLS-2$
			if (rs != null) {
				PreparedStatement pstmt2 = con.prepareStatement(DROP_MEDIAINDEX_COLUMN);
				pstmt2.executeUpdate();
				pstmt2.close();
			}
		}

		return true;
	}

	/**
	 * Create the Meeting and Media Index Tables.
	 * @param dbcon, the DBConnection to use to run the sql.
	 * @param adminDatabase the database admin object required to check database type.
	 */
	private static boolean createMediaIndexTable(DBConnection dbcon, DBAdminDatabase adminDatabase) throws SQLException {

		Connection con = dbcon.getConnection();

		// IF CALLED AFTER A RESTORE, TABLE MAY BE ADDED
		// CHECK IF MEETING TABLE EXISTS
		DatabaseMetaData dbmd = con.getMetaData();
		ResultSet rs = dbmd.getTables(null, null, "MEETING", null); //$NON-NLS-1$
		boolean proceed = true;
		if (rs != null) {
			while (rs.next()) {
				String	sName = rs.getString(3);
				if (sName.equalsIgnoreCase("Meeting")) { //$NON-NLS-1$
					proceed = false;
					break;
				}
			}
		}
		if (proceed) {
			String sSQL = DBConstantsDerby.CREATE_MEETING_TABLE;
			String sSQL2 = DBConstantsDerby.CREATE_MEDIAINDEX_TABLE;

			if (adminDatabase.getDatabaseManager().getDatabaseType() == ICoreConstants.MYSQL_DATABASE) {
				sSQL = DBConstantsMySQL.MYSQL_CREATE_MEETING_TABLE;
				sSQL2 = DBConstantsMySQL.MYSQL_CREATE_MEDIAINDEX_TABLE;
			}

			PreparedStatement pstmt = con.prepareStatement(sSQL);
			pstmt.executeUpdate();
			pstmt.close();

			PreparedStatement pstmt2 = con.prepareStatement(sSQL2);
			pstmt2.executeUpdate();
			pstmt2.close();
		}

		return true;
	}

// 1.3.6

	/**
	 * Add a CurrentStatus column to the Meeting table.
	 * @param dbcon the DBConnection to use to run the sql.
	 * @param sDatabaseName the name of the databases being updated. 
	 */
	private static boolean addMeetingStatusColumn(DBConnection dbcon) throws SQLException {

		Connection con = dbcon.getConnection();

		// IF CALLED AFTER A RESTORE, TABLE WILL HAVE COLUMN ALREADY
		// CHECK IF CURRENTSTATUS COLUMN EXISTS
		DatabaseMetaData dbmd = con.getMetaData();
		ResultSet rs = dbmd.getColumns(null, null, "MEETING", "CURRENTSTATUS"); //$NON-NLS-1$ //$NON-NLS-2$
		if (rs == null) {
			PreparedStatement pstmt = con.prepareStatement(MEETING_STATUS_UPDATE);
			pstmt.executeUpdate();
			pstmt.close();
		}
		return true;
	}

// 1.3.7 VERSION CHANGE

	/**
	 * Update the Node  and Link tables's OriginalID fields to be 255 chars long (instead of 50).
	 * @param dbcon the DBConnection to use to run the sql.
	 * @param adminDatabase the database admin object required to check database type.
	 */
	private static boolean updateOriginalID(DBConnection dbcon, DBAdminDatabase adminDatabase) throws SQLException {

		Connection con = dbcon.getConnection();

		String sUpdateNodeTable = UPDATE_NODE_ORIGINALID;
		String sUpdateLinkTable = UPDATE_LINK_ORIGINALID;
		if (adminDatabase.getDatabaseManager().getDatabaseType() == ICoreConstants.DERBY_DATABASE) {
			sUpdateNodeTable = UPDATE_NODE_ORIGINALID_DERBY;
			sUpdateLinkTable = UPDATE_LINK_ORIGINALID_DERBY;
		}

		PreparedStatement pstmt = con.prepareStatement(sUpdateNodeTable);
		int nReturn = pstmt.executeUpdate();
		pstmt.close();

		pstmt = con.prepareStatement(sUpdateLinkTable);
		nReturn = pstmt.executeUpdate();
		pstmt.close();

		return true;
	}

//	 1.3.8 VERSION CHANGE - for Compendium 1.4.2 - Alpha 2

	/**
	 * Update the Node  and Link tables's OriginalID fields to be 255 chars long (instead of 50).
	 * @param dbcon the DBConnection to use to run the sql.
	 * @param adminDatabase the database admin object required to check database type.
	 */
	private static boolean updateReferenceSource(DBConnection dbcon, DBAdminDatabase adminDatabase) throws SQLException {

		Connection con = dbcon.getConnection();

		String sUpdateReferenceNodeTable = UPDATE_REFERENCE_SOURCE;
		if (adminDatabase.getDatabaseManager().getDatabaseType() == ICoreConstants.DERBY_DATABASE) {
			sUpdateReferenceNodeTable = UPDATE_REFERENCE_SOURCE_DERBY;
		}

		PreparedStatement pstmt = con.prepareStatement(sUpdateReferenceNodeTable);
		int nReturn = pstmt.executeUpdate();
		pstmt.close();

		return true;
	}
	
// 1.3.9 VERSION CHANGE = for Compendium 1.4.2 - Beta 3
	
	/**
	 * Delete the NodeProperty table and add new fields to ViewNode.
	 * @param dbcon the DBConnection to use to run the sql.
	 */
	private static boolean replaceNodePropertyTable(DBConnection dbcon, DBAdminDatabase adminDatabase) throws SQLException {
	
		Connection con = dbcon.getConnection();

		// IF CALLED AFTER A RESTORE, TABLE MAY NOT EXIST
		// CHECK IF NodeProperty TABLE EXISTS
		DatabaseMetaData dbmd = con.getMetaData();
		ResultSet rs = dbmd.getTables(null, null, "NODEPROPERTY", null); //$NON-NLS-1$

		boolean proceed = false;
		if (rs != null) {
			while (rs.next()) {
				String	sName = rs.getString(3);
				if (sName.equalsIgnoreCase("NodeProperty")) { //$NON-NLS-1$
					proceed = true;
					break;
				}
			}
		}

		if (proceed) {		
			String sDropNodePropertyTable = DROP_NODEPROPERTY_TABLE;			
			if (adminDatabase.getDatabaseManager().getDatabaseType() == ICoreConstants.MYSQL_DATABASE) {
				sDropNodePropertyTable = MYSQL_DROP_NODEPROPERTY_TABLE;
			}
			
			PreparedStatement pstmt = con.prepareStatement(sDropNodePropertyTable);
			pstmt.executeUpdate() ;
			pstmt.close();
		}
			
		// IF CALLED AFTER A RESTORE, TABLE WILL HAVE NEW COLUMNS
		// CHECK IF Background COLUMN EXISTS
		rs = dbmd.getColumns(null, null, "VIEWNODE", "BACKGROUND");		 //$NON-NLS-1$ //$NON-NLS-2$
		if (rs == null || !rs.next()) {
			
			PreparedStatement pstmt2 = con.prepareStatement(VIEWNODE_UPDATE_SHOWTAGS);
			pstmt2.executeUpdate();
			pstmt2.close();

			pstmt2 = con.prepareStatement(VIEWNODE_UPDATE_SHOWTEXT);
			pstmt2.executeUpdate();
			pstmt2.close();
				
			pstmt2 = con.prepareStatement(VIEWNODE_UPDATE_SHOWTRANS);
			pstmt2.executeUpdate();
			pstmt2.close();
	
			pstmt2 = con.prepareStatement(VIEWNODE_UPDATE_SHOWWEIGHT);
			pstmt2.executeUpdate();
			pstmt2.close();

			pstmt2 = con.prepareStatement(VIEWNODE_UPDATE_SMALLICON);
			pstmt2.executeUpdate();
			pstmt2.close();

			pstmt2 = con.prepareStatement(VIEWNODE_UPDATE_HIDEICON);
			pstmt2.executeUpdate();
			pstmt2.close();
			
			pstmt2 = con.prepareStatement(VIEWNODE_UPDATE_WRAPWIDTH);
			pstmt2.executeUpdate();
			pstmt2.close();
	
			pstmt2 = con.prepareStatement(VIEWNODE_UPDATE_FONTSIZE);
			pstmt2.executeUpdate();
			pstmt2.close();

			pstmt2 = con.prepareStatement(VIEWNODE_UPDATE_FONTFACE);
			pstmt2.executeUpdate();
			pstmt2.close();
		
			pstmt2 = con.prepareStatement(VIEWNODE_UPDATE_FONTSTYLE);
			pstmt2.executeUpdate();
			pstmt2.close();

			pstmt2 = con.prepareStatement(VIEWNODE_UPDATE_FOREGROUND);
			pstmt2.executeUpdate();
			pstmt2.close();

			pstmt2 = con.prepareStatement(VIEWNODE_UPDATE_BACKGROUND);
			pstmt2.executeUpdate();
			pstmt2.close();
			
			return true;			
		}				
		
		return true;
	}
		
	/**
	 * Add new columns to help groupware.
	 * One to the node table to hold the author who last modified a node.
	 * One to the user table to hold the user's inbox or LinkView id
	 * Update the NodeUserState table and set all to READSTATE.
	 * Add an inbox to the home view of each user.
	 * @param dbcon the DBConnection to use to run the sql.
	 */
	private static boolean addGroupwareColumns(DBConnection dbcon) throws SQLException {
	
		Connection con = dbcon.getConnection();		
		DatabaseMetaData dbmd = con.getMetaData();
		
		// IF CALLED AFTER A RESTORE, TABLE WILL HAVE NEW COLUMNS
		// CHECK IF COLUMN EXISTS
		ResultSet rs = dbmd.getColumns(null, null, "NODE", "LASTMODAUTHOR"); //$NON-NLS-1$ //$NON-NLS-2$
		if (rs == null || !rs.next()) {
			PreparedStatement pstmt = con.prepareStatement(UPDATE_NODE_TABLE);
			pstmt.executeUpdate();
			pstmt.close();
		}
		
		// IF CALLED AFTER A RESTORE, TABLE WILL HAVE NEW COLUMNS
		// CHECK IF COLUMN EXISTS
		rs = dbmd.getColumns(null, null, "USERS", "LINKVIEW"); //$NON-NLS-1$ //$NON-NLS-2$
		if (rs == null || !rs.next()) {
			PreparedStatement pstmt2 = con.prepareStatement(UPDATE_USERS_TABLE);
			pstmt2.executeUpdate();
			pstmt2.close();
		}

		PreparedStatement pstmt3 = con.prepareStatement(UPDATE_NODEUSERSTATE_TABLE);
		pstmt3.executeUpdate() ;
		pstmt3.close();
		
		return true;
	}	
	
	/**
	 * Add new column for ViewID and its constraint.
	 * @param dbcon the DBConnection to use to run the sql.
	 */
	private static boolean updateFavoriteTable(DBConnection dbcon) throws SQLException {
	
		Connection con = dbcon.getConnection();		
		DatabaseMetaData dbmd = con.getMetaData();
		
		// IF CALLED AFTER A RESTORE, TABLE WILL HAVE NEW COLUMN
		// CHECK IF COLUMN EXISTS
		ResultSet rs = dbmd.getColumns(null, null, "FAVORITE", "VIEWID"); //$NON-NLS-1$ //$NON-NLS-2$
		if (rs == null || !rs.next()) {
			PreparedStatement pstmt = con.prepareStatement(FAVORITE_UPDATE);
			pstmt.executeUpdate();
			pstmt.close();
			
			PreparedStatement pstmt2 = con.prepareStatement(FAVORITE_CONSTRAINT_UPDATE);
			pstmt2.executeUpdate();
			pstmt2.close();
		}

		return true;
	}	

// 1.5.3 change for 1.5.3 Alpha 6 (2.0 Alpha 1)
	
	/**
	 * Remove the UserID column from the VIewLayer table, 
	 * so only one set of data per view.
	 * This means merging data first if there are multiple records.
	 * @param dbcon the DBConnection to use to run the sql.
	 */
	private static void removeUserIDFromLayerView(DBConnection dbcon) throws SQLException {
	
		Connection con = dbcon.getConnection();			
		DatabaseMetaData dbmd = con.getMetaData();
		
		// IF CALLED AFTER A RESTORE, TABLE WILL NOT HAVE USERID COLUMN
		// CHECK IF COLUMN EXISTS
		ResultSet rs = dbmd.getColumns(null, null, "VIEWLAYER", "USERID"); //$NON-NLS-1$ //$NON-NLS-2$

		if (rs != null) {
			PreparedStatement pstmt = con.prepareStatement(VIEWLAYER_DELETE_EMPTIES);
			pstmt.executeUpdate();
			pstmt.close();
			
			//select all view id for views with more than one ViewLayer entry.
			PreparedStatement pstmt2 = con.prepareStatement(VIEWLAYER_CHECK_DUPLICATES);
			ResultSet rs2 = pstmt2.executeQuery();

			if (rs2 != null) {
				//int iCount = 0;
				String sViewID = ""; //$NON-NLS-1$
				Vector<String> vtViews = new Vector<String>();
				while (rs2.next()) {
					//iCount	= rs2.getInt(1);
					sViewID = rs2.getString(2);
					vtViews.addElement(sViewID);
				}
				rs2.close();				
				pstmt2.close();

				int count = vtViews.size();
				for (int j=0; j<count;j++) {
					sViewID = vtViews.elementAt(j);
					PreparedStatement pstmt3 = con.prepareStatement(VIEWLAYER_SELECT_DUPLICATES);
					pstmt3.setString(1, sViewID);
					ResultSet rs3 = pstmt3.executeQuery();
					
					// Build up arrays of the multiple data possibilities.
					if (rs3 != null) {
						Vector<String> vtBackgrounds = new Vector<String>();
						Vector<String> vtScribbles = new Vector<String>();
						Vector<String> vtShapes = new Vector<String>();
						Vector<String> vtUsers = new Vector<String>();
						String sScribble = ""; //$NON-NLS-1$
						String sBackground = ""; //$NON-NLS-1$
						String sShapes = ""; //$NON-NLS-1$
						String sUserID=""; //$NON-NLS-1$
						while (rs.next()) {
							sScribble = rs3.getString(1);
							sBackground = rs3.getString(2);
							sShapes = rs3.getString(3);
							sUserID = rs3.getString(4);
							
							vtBackgrounds.addElement(sBackground);
							vtScribbles.addElement(sScribble);							
							vtShapes.addElement(sShapes);
							vtUsers.addElement(sUserID);
						}
						rs3.close();
						pstmt3.close();
						
						// Get the first instance of each type and save to the first record.
						String sMergedBackground = ""; //$NON-NLS-1$
						String sMergedScribble = ""; //$NON-NLS-1$
						String sMergedShapes = "";	//$NON-NLS-1$
						if (!vtScribbles.isEmpty()) {
							sMergedScribble = vtScribbles.elementAt(0);
						}
						if (!vtShapes.isEmpty()) {
							sMergedShapes = vtShapes.elementAt(0);
						}
						if (!vtBackgrounds.isEmpty()) {
							sMergedBackground = vtBackgrounds.elementAt(0);
						}
						
						//update the first record for that view - this will be the only record.
						PreparedStatement pstmt4 = con.prepareStatement(VIEWLAYER_UPDATE);
						pstmt4.setString(1, sMergedScribble);
						pstmt4.setString(2, sMergedBackground);
						pstmt4.setString(3, sMergedShapes);
						pstmt4.setString(4, vtUsers.elementAt(0));
						pstmt4.setString(5, sViewID);
						pstmt4.executeUpdate();
						pstmt4.close();
						
						//delete other records for this view
						PreparedStatement pstmt5 = null;
						for (int i=1; i<vtUsers.size(); i++) {
							pstmt5 = con.prepareStatement(VIEWLAYER_DELETE_DUPLICATE);
							pstmt5.setString(1, vtUsers.elementAt(i));
							pstmt5.setString(2, sViewID);
							pstmt5.executeUpdate();
						}						
						pstmt5.close();
					}
				}
			}
			
			// remove foreign key
			PreparedStatement pstmt6 = con.prepareStatement(VIEWLAYER_DELETE_FOREIGNKEY);						
			pstmt6.executeUpdate();

			//drop old primary key
			PreparedStatement pstmt7 = con.prepareStatement(VIEWLAYER_DROP_PRIMARYKEY);
			pstmt7.executeUpdate();
				
			//add new primary key
			PreparedStatement pstmt8 = con.prepareStatement(VIEWLAYER_ADD_PRIMARYKEY);
			pstmt8.executeUpdate();
			
			//drop userid column, finally!!
			PreparedStatement pstmt9 = con.prepareStatement(VIEWLAYER_DROP_USERID_COLUMN);
			pstmt9.executeUpdate();
		}
	}	
	
	//update the ViewLayer table to add the Background Color field
	/**
	 * Add BackgroundColor field to ViewLayer Table.
	 * @param dbcon, the DBConnection to use to run the sql.
	 * @param adminDatabase the database admin object required to check database type.
	 */
	private static void updateViewLayer(DBConnection dbcon, DBAdminDatabase adminDatabase) throws SQLException {

		Connection con = dbcon.getConnection();		
		DatabaseMetaData dbmd = con.getMetaData();
		
		// IF CALLED AFTER A RESTORE, TABLE WILL HAVE NEW COLUMN
		// CHECK IF COLUMN EXISTS
		ResultSet rs = dbmd.getColumns(null, null, "VIEWLAYER", "BACKGROUNDCOLOR"); //$NON-NLS-1$ //$NON-NLS-2$
		if (rs == null || !rs.next()) {
			PreparedStatement pstmt = con.prepareStatement(VIEWLAYER_UPDATE2);
			pstmt.executeUpdate();
			pstmt.close();
		}
	}

// 1.5.4 VERSION CHANGES - release 2.0 alpha 4

	//add new ViewTimeNode table
	/**
	 * Create the ViewTimeNodeTable Table.
	 * @param dbcon, the DBConnection to use to run the sql.
	 * @param adminDatabase the database admin object required to check database type.
	 * @return true if the table needed to be created else false;
	 */
	private static void createViewTimeNodeTable(DBConnection dbcon, DBAdminDatabase adminDatabase) throws SQLException {

		Connection con = dbcon.getConnection();

		// IF CALLED AFTER A RESTORE, TABLE MAY BE ADDED
		// CHECK IF TABLE EXISTS
		DatabaseMetaData dbmd = con.getMetaData();
		ResultSet rs = dbmd.getTables(null, null, "VIEWTIMENODE", null);  //$NON-NLS-1$
		boolean proceed = true;
		if (rs != null) {
			while (rs.next()) {
				String	sName = rs.getString(3);
				if (sName.equalsIgnoreCase("ViewTimeNode")) {  //$NON-NLS-1$
					proceed = false;
					break;
				}
			}
		}
		if (proceed) {
			String sSQL = DBConstantsDerby.CREATE_VIEWTIMENODE_TABLE;
			if (adminDatabase.getDatabaseManager().getDatabaseType() == ICoreConstants.MYSQL_DATABASE) {
				sSQL = DBConstantsMySQL.MYSQL_CREATE_VIEWTIMENODE_TABLE;
			}

			PreparedStatement pstmt = con.prepareStatement(sSQL);
			pstmt.executeUpdate();
			pstmt.close();
		}
	}	

	
	//add new Movies table
	/**
	 * Create the Movies Table.
	 * @param dbcon, the DBConnection to use to run the sql.
	 * @param adminDatabase the database admin object required to check database type.
	 * @return true if the table needed to be created else false;
	 */
	private static void createMoviesTable(DBConnection dbcon, DBAdminDatabase adminDatabase) throws SQLException {

		Connection con = dbcon.getConnection();

		// IF CALLED AFTER A RESTORE, TABLE MAY BE ADDED
		// CHECK IF TABLE EXISTS
		DatabaseMetaData dbmd = con.getMetaData();
		ResultSet rs = dbmd.getTables(null, null, "MOVIES", null);  //$NON-NLS-1$
		boolean proceed = true;
		if (rs != null) {
			while (rs.next()) {
				String	sName = rs.getString(3);
				if (sName.equalsIgnoreCase("Movies")) {  //$NON-NLS-1$
					proceed = false;
					break;
				}
			}
		}
		if (proceed) {
			String sSQL = DBConstantsDerby.CREATE_MOVIES_TABLE;
			if (adminDatabase.getDatabaseManager().getDatabaseType() == ICoreConstants.MYSQL_DATABASE) {
				sSQL = DBConstantsMySQL.MYSQL_CREATE_MOVIES_TABLE;
			}

			PreparedStatement pstmt = con.prepareStatement(sSQL);
			pstmt.executeUpdate();
			pstmt.close();
		}
	}	

// 1.5.5 VERSION CHANGES - release 2.0 alpha 5
	
	//add new MovieProperties table
	/**
	 * Create the MovieProperties Table.
	 * @param dbcon, the DBConnection to use to run the sql.
	 * @param adminDatabase the database admin object required to check database type.
	 * @return true if the table needed to be created else false;
	 */
	private static void createMoviePropertiesTable(DBConnection dbcon, DBAdminDatabase adminDatabase) throws SQLException {

		Connection con = dbcon.getConnection();

		// IF CALLED AFTER A RESTORE, TABLE MAY BE ADDED
		// CHECK IF TABLE EXISTS
		DatabaseMetaData dbmd = con.getMetaData();
		ResultSet rs = dbmd.getTables(null, null, "MOVIEPROPERTIES", null);  //$NON-NLS-1$
		boolean proceed = true;
		if (rs != null) {
			while (rs.next()) {
				String	sName = rs.getString(3);
				if (sName.equalsIgnoreCase("MovieProperties")) {  //$NON-NLS-1$
					proceed = false;
					break;
				}
			}
		}
		if (proceed) {
			String sSQL = DBConstantsDerby.CREATE_MOVIEPROPERTIES_TABLE;
			if (adminDatabase.getDatabaseManager().getDatabaseType() == ICoreConstants.MYSQL_DATABASE) {
				sSQL = DBConstantsMySQL.MYSQL_CREATE_MOVIEPROPERTIES_TABLE;
			}

			PreparedStatement pstmt = con.prepareStatement(sSQL);
			pstmt.executeUpdate();
			pstmt.close();
		}
	}		
	
	
// 2.0 VERSION CHANGES for release 2.0 Alpha 7.	
	
	/**
	 * Add the link formatting fields to the ViewLink table
	 * @param dbcon the DBConnection to use to run the sql.
	 */
	private static boolean updateViewLinkTable(DBConnection dbcon, DBAdminDatabase adminDatabase) throws SQLException {
	
		Connection con = dbcon.getConnection();

		// IF CALLED AFTER A RESTORE, TABLE WILL HAVE NEW COLUMNS
		// CHECK IF Background COLUMN EXISTS
		DatabaseMetaData dbmd = con.getMetaData();
		ResultSet rs = dbmd.getColumns(null, null, "VIEWLINK", "BACKGROUND");		 //$NON-NLS-1$ //$NON-NLS-2$
		if (rs == null || !rs.next()) {
			
			PreparedStatement pstmt2 = con.prepareStatement(VIEWLINK_UPDATE_WRAPWIDTH);
			pstmt2.executeUpdate();
			pstmt2.close();			
			
			pstmt2 = con.prepareStatement(VIEWLINK_UPDATE_ARROWSTYLE);
			pstmt2.executeUpdate();
			pstmt2.close();

			pstmt2 = con.prepareStatement(VIEWLINK_UPDATE_LINESTYLE);
			pstmt2.executeUpdate();
			pstmt2.close();

			pstmt2 = con.prepareStatement(VIEWLINK_UPDATE_LINEDASHED);
			pstmt2.executeUpdate();
			pstmt2.close();
		
			pstmt2 = con.prepareStatement(VIEWLINK_UPDATE_LINEWEIGHT);
			pstmt2.executeUpdate();
			pstmt2.close();

			pstmt2 = con.prepareStatement(VIEWLINK_UPDATE_LINECOLOUR);
			pstmt2.executeUpdate();
			pstmt2.close();

			pstmt2 = con.prepareStatement(VIEWLINK_UPDATE_FONTSIZE);
			pstmt2.executeUpdate();
			pstmt2.close();
				
			pstmt2 = con.prepareStatement(VIEWLINK_UPDATE_FONTFACE);
			pstmt2.executeUpdate();
			pstmt2.close();
	
			pstmt2 = con.prepareStatement(VIEWLINK_UPDATE_FONTSTYLE);
			pstmt2.executeUpdate();
			pstmt2.close();

			pstmt2 = con.prepareStatement(VIEWLINK_UPDATE_FOREGROUND);
			pstmt2.executeUpdate();
			pstmt2.close();

			pstmt2 = con.prepareStatement(VIEWLINK_UPDATE_BACKGROUND);
			pstmt2.executeUpdate();
			pstmt2.close();

			pstmt2 = con.prepareStatement(VIEWLINK_UPDATE_ARROWDATA);
			pstmt2.executeUpdate();
			pstmt2.close();
			
			pstmt2 = con.prepareStatement(DROP_LINK_TABLE_ARROW);
			pstmt2.executeUpdate();
			pstmt2.close();
			
			return true;			
		}				
		
		return true;
	}
		
// 2.0.1 VERSION CHANGES - release 2.0 alpha 10

	// Add new LinkedFile table - forgot to add this when I merged with Sebastian's code!
	/**
	 * Create the LinkedFile Table.
	 * @param dbcon the DBConnection to use to run the sql.
	 * @param adminDatabase the database admin object required to check database type.
	 * @return true if the table needed to be created else false;
	 */
	private static void createLinkedFileTable(DBConnection dbcon, DBAdminDatabase adminDatabase) throws SQLException {

		Connection con = dbcon.getConnection();

		// IF CALLED AFTER A RESTORE, TABLE MAY BE ADDED
		// CHECK IF TABLE EXISTS
		DatabaseMetaData dbmd = con.getMetaData();
		ResultSet rs = dbmd.getTables(null, null, "LINKEDFILE", null);  //$NON-NLS-1$
		boolean proceed = true;
		if (rs != null) {
			while (rs.next()) {
				String	sName = rs.getString(3);
				if (sName.equalsIgnoreCase("LinkedFile")) {  //$NON-NLS-1$
					proceed = false;
					break;
				}
			}
		}
		if (proceed) {
			String sSQL = DBConstantsDerby.CREATE_LINKEDFILE_TABLE;
			if (adminDatabase.getDatabaseManager().getDatabaseType() == ICoreConstants.MYSQL_DATABASE) {
				sSQL = DBConstantsMySQL.MYSQL_CREATE_LINKEDFILE_TABLE;
			}

			PreparedStatement pstmt = con.prepareStatement(sSQL);
			pstmt.executeUpdate();
			pstmt.close();
		}
	}	
	
	/**
	 * Fix the column name of the transparency column on the MovieProperties table.
	 * @param dbcon the DBConnection to use to run the sql.
	 */
	private static void fixMoviePropertiesTable(DBConnection dbcon)  throws SQLException {
		Connection con = dbcon.getConnection();

		// IF CALLED AFTER A RESTORE, TABLE MAY BE ADDED CORRECTLY
		// CHECK IF COLUMN NAME CORRECT
		DatabaseMetaData dbmd = con.getMetaData();
		ResultSet rs = dbmd.getColumns(null, null, "MOVIEPROPERTIES", "TRANSPARENCY");		 //$NON-NLS-1$ //$NON-NLS-2$
		boolean proceed = false;
		if (rs != null) {
			while (rs.next()) {
				String	sName = rs.getString(3);
				if (sName.equalsIgnoreCase("transparency")) {  //$NON-NLS-1$
					proceed = true;
					break;
				}
			}
		}
		if (proceed) {
			PreparedStatement pstmt = con.prepareStatement(RENAME_TRANSPARENCY);
			pstmt.executeUpdate();
			pstmt.close();
		}
	}	
	
// PROGRESS BAR METHODS
	/**
	 * Draws the progress dialog.
	 */
	private static class ProgressThread extends Thread {

		public boolean keep = false;

		public ProgressThread(String sTitle, String sFinal) {
			oProgressBar = new JProgressBar();
			oProgressBar.setMinimum(0);
			oProgressBar.setMaximum(100);

	  		oProgressDialog = new UIProgressDialog(oParent, sTitle, sFinal);
	  		oProgressDialog.showDialog(oProgressBar, false);
	  		oProgressDialog.setModal(true);
		}

		public void run() {
			keep = true;
	  		oProgressDialog.setVisible(true);
			while(keep);
			oProgressDialog.setVisible(false);
		}
	}

	/**
	 * Set the amount of progress items being counted.
	 *
	 * @param int nCount, the amount of progress items being counted.
	 */
    public static void progressCount(int count) {
		nCount = 0;
		oProgressBar.setValue(0);
		oProgressBar.setMaximum(count);
		oProgressDialog.setStatus(0);
	}

	/**
	 * Indicate that progress has been updated.
	 *
	 * @param int nCount, the current position of the progress in relation to the inital count.
	 * @param String sMessage, the message to display to the user.
	 */
    public static void progressUpdate(int nIncrement, String sMessage) {
		nCount += nIncrement;
		oProgressBar.setValue(nCount);
		oProgressDialog.setMessage(sMessage);
		oProgressDialog.setStatus(nCount);
	}

	/**
	 * Indicate that progress has complete.
	 *
	 * @param int nCount, the final position of the progress in relation to the intial count.
	 * @param String sMessage, the message to display to the user.
	 */
    public static void progressComplete() {
		nCount = -1;
		oThread.keep = false;
		oProgressDialog.setVisible(false);
		oProgressDialog.dispose();
	}
}
