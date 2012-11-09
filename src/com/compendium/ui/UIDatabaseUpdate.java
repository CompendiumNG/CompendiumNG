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

import java.sql.*;
import java.util.*;
import java.io.*;
import javax.swing.*;

import com.compendium.*;

import com.compendium.core.db.management.DBConnection;
import com.compendium.core.datamodel.*;
import com.compendium.core.datamodel.services.*;
import com.compendium.core.db.*;
import com.compendium.core.db.management.*;
import com.compendium.core.*;

import com.compendium.ui.dialogs.*;

/**
 * This class updates a database structure as and when required by different versions of the software.
 */
public class UIDatabaseUpdate implements DBConstants, DBConstantsMySQL, DBConstantsDerby{


// 1.3 Original MySQL database version - release 1.3

// 1.3.1 VERSION CHANGES MYSQL ONLY - release 1.3.04

	/** Select all the link details for the links used in views (select all links!).*/
	private static final String SELECT_ALL_LINKS = "SELECT Link.LinkID, Link.CreationDate, Link.ModificationDate, Link.Author, Link.LinkType, " +
													"Link.OriginalID, Link.FromNode, Link.ToNode, Link.ViewID, Link.Arrow, Link.CurrentStatus, "+
													"ViewLink.CreationDate, ViewLink.ModificationDate, ViewLink.CurrentStatus "+
													"FROM Link, ViewLink " +
													"Where Link.LinkID = ViewLink.LinkID";

	/** NOT USED AT THE MOMENT */
	private static final String DROP_VIEWID_COLUMN = "ALTER TABLE Link DROP COLUMN ViewID";

	/** Insert a property into the System table.*/
	private static final String INSERT_LINK_GROUP = "INSERT INTO System (Property, Contents) VALUES ('linkgroup', ?)";

	/** Add a Label Column to the Link table; Drop the foreign key on the ViewID field; Set the ViewID default to '0';change the LinkType field to varchar 50.*/
	private static final String UPDATE_LINK_TABLE = "ALTER TABLE Link ADD COLUMN Label TEXT, DROP FOREIGN KEY FK_Link_3, DROP Index Link_ViewID_Ind, ALTER ViewID SET DEFAULT '0', MODIFY LinkType VARCHAR(50) NOT NULL";


// 1.3.2 VERSION  CHANGES MYSQL ONLY - release 1.3.05 Beta 1

	/** Delete a constraint on the ViewLink Table.*/
	private static final String DELETE_VIEWLINK_CONSTRAINT = "ALTER TABLE ViewLink DROP FOREIGN KEY FK_ViewLink_1";

	/**
	 *	Updates a constraint on the ViewLink Table.
	 *  Foreign Key name was dictated by MySQL. My name was ignored.
	 *	Mentioned actual name allocated by MyQSL below only for reference.
	 */
	private static final String CREATE_VIEWLINK_CONSTRAINT = "ALTER TABLE ViewLink ADD CONSTRAINT viewlink_ibfk_1 FOREIGN KEY (ViewID) REFERENCES Node (NodeID) ON DELETE CASCADE";

// 1.3.3 VERSION CHANGES MYSQL ONLY - release 1.4.0 Alpha 2

	/** Update the User table name to 'Users', as 'User' is a reserved work on Derby.*/
	/** And update the Connection table name to 'Connections', as 'Connection' is a reserved work on Derby.*/
	private static final String RENAME_TABLES = "RENAME TABLE User TO Users, Connection TO Connections";

// 1.3.4 VERSION CHANGES - release 1.4.0 Alpha 3

	/** Add a MediaIndex column to the ViewNode table.*/
	private static final String UPDATE_VIEWNODE_TABLE = "ALTER TABLE ViewNode ADD COLUMN MediaIndex DOUBLE";

	/** Fill the MediaIndex column of the ViewNode table with the same data as the CreationDate field.*/
	private static final String UPDATE_MEDIAINDEX = "UPDATE ViewNode set ViewNode.MediaIndex = ViewNode.CreationDate";

// 1.3.5 VERSION CHANGE - release 1.4.0 Alpha 4

	// ONLY WORKS ON MYSQL AT THE MOMENT:1st June 2005
	/** Drop the MediaIndex column from the ViewNode table.*/
	private static final String DROP_MEDIAINDEX_COLUMN = "ALTER TABLE ViewNode DROP COLUMN MediaIndex";

// 1.3.6 VERSION CHANGE - release 1.4.0 Alpha 4.1

	/** Add a CurrentStatus field to the Meeting table.*/
	private static final String MEETING_STATUS_UPDATE = "ALTER TABLE Meeting ADD COLUMN CurrentStatus INTEGER NOT NULL DEFAULT 0";

// 1.3.7 VERSION CHANGE - release 1.4

	/** Alter the node table's OriginalID field to increase the varchar length from 50 to 255 in MySQL syntax.*/
	private static final String UPDATE_NODE_ORIGINALID = "ALTER TABLE Node MODIFY OriginalID VARCHAR(255)";

	/** Alter the link table's OriginalID field to increase the varchar length from 50 to 255 in MySQL syntax.*/
	private static final String UPDATE_LINK_ORIGINALID = "ALTER TABLE Link MODIFY OriginalID VARCHAR(255)";

	/** Alter the node table's OriginalID field to increase the varchar length from 50 to 255 in Derby database syntax.*/
	private static final String UPDATE_NODE_ORIGINALID_DERBY = "ALTER TABLE Node ALTER OriginalID SET DATA TYPE VARCHAR(255)";

	/** Alter the link table's OriginalID field to increase the varchar length from 50 to 255 in Derby database syntax.*/
	private static final String UPDATE_LINK_ORIGINALID_DERBY = "ALTER TABLE Link ALTER OriginalID SET DATA TYPE VARCHAR(255)";

// 1.3.8 VERSION CHANGE - release 1.4.2 Alpha 2
// for UDIG references
	
	/** Alter the reference node table's Source field to increase the varchar length to Text in MySQL syntax.*/
	private static final String UPDATE_REFERENCE_SOURCE = "ALTER TABLE ReferenceNode MODIFY Source TEXT";
	
	/** Alter the reference node table's Source field to increase the varchar length to long varchar in Derby database syntax.*/
	private static final String UPDATE_REFERENCE_SOURCE_DERBY = "ALTER TABLE ReferenceNode ALTER Source SET DATA TYPE VARCHAR(2500)";
	
// 1.3.9 VERSION CHANGE  -release 1.4.2 - Beta 3
// DROP NodeProperty Table


	/** Add a ShowTags field to the ViewNode table.*/
	private static final String VIEWNODE_UPDATE_SHOWTAGS = "ALTER TABLE ViewNode ADD COLUMN ShowTags VARCHAR(1) NOT NULL DEFAULT 'Y'";

	/** Add a ShowText field to the ViewNode table.*/
	private static final String VIEWNODE_UPDATE_SHOWTEXT = "ALTER TABLE ViewNode ADD COLUMN ShowText VARCHAR(1) NOT NULL DEFAULT 'Y'";
		
	/** Add a ShowTrans field to the ViewNode table.*/
	private static final String VIEWNODE_UPDATE_SHOWTRANS = "ALTER TABLE ViewNode ADD COLUMN ShowTrans VARCHAR(1) NOT NULL DEFAULT 'Y'";

	/** Add a ShowWeight field to the ViewNode table.*/
	private static final String VIEWNODE_UPDATE_SHOWWEIGHT = "ALTER TABLE ViewNode ADD COLUMN ShowWeight VARCHAR(1) NOT NULL DEFAULT 'Y'";

	/** Add a SmallIcon field to the ViewNode table.*/
	private static final String VIEWNODE_UPDATE_SMALLICON = "ALTER TABLE ViewNode ADD COLUMN SmallIcon VARCHAR(1) NOT NULL DEFAULT 'N'";

	/** Add a HideIcon field to the ViewNode table.*/
	private static final String VIEWNODE_UPDATE_HIDEICON = "ALTER TABLE ViewNode ADD COLUMN HideIcon VARCHAR(1) NOT NULL DEFAULT 'N'";

	/** Add a LabelWrapWidth field to the ViewNode table.*/
	private static final String VIEWNODE_UPDATE_WRAPWIDTH = "ALTER TABLE ViewNode ADD COLUMN LabelWrapWidth INTEGER NOT NULL DEFAULT 20";

	/** Add a FontSize field to the ViewNode table.*/
	private static final String VIEWNODE_UPDATE_FONTSIZE = "ALTER TABLE ViewNode ADD COLUMN FontSize INTEGER NOT NULL DEFAULT 12";

	/** Add a FontFace field to the ViewNode table.*/
	private static final String VIEWNODE_UPDATE_FONTFACE = "ALTER TABLE ViewNode ADD COLUMN FontFace VARCHAR(100) NOT NULL DEFAULT 'Arial'";

	/** Add a FontFace field to the ViewNode table.*/
	private static final String VIEWNODE_UPDATE_FONTSTYLE = "ALTER TABLE ViewNode ADD COLUMN FontStyle INTEGER NOT NULL DEFAULT 0";

	/** Add a Foreground field to the ViewNode table.*/
	private static final String VIEWNODE_UPDATE_FOREGROUND = "ALTER TABLE ViewNode ADD COLUMN Foreground INTEGER NOT NULL DEFAULT 0";

	/** Add a Background field to the ViewNode table.*/
	private static final String VIEWNODE_UPDATE_BACKGROUND = "ALTER TABLE ViewNode ADD COLUMN Background INTEGER NOT NULL DEFAULT -1";
		
	/** Add last modification author column to the Node table.*/
	private static final String UPDATE_NODE_TABLE = "ALTER TABLE Node ADD COLUMN LastModAuthor VARCHAR(50)";

	/** Add link view id column to the Users table.*/
	private static final String UPDATE_USERS_TABLE = "ALTER TABLE Users ADD COLUMN LinkView VARCHAR(50)";

	/** Set state to READSTATE for all records.*/
	private static final String UPDATE_NODEUSERSTATE_TABLE = "UPDATE NodeUserState set State = "+ICoreConstants.READSTATE;
	
// SET ALL CURRENT NODES AS READ FOR ALL CURRENT USERS IN THE USERS TABLE
	
//	 1.5.0 VERSION CHANGE  -release 1.5 - Beta 1	
	
	/** Add a ViewId column to the Favorite table.*/
	private static final String FAVORITE_UPDATE = "ALTER TABLE Favorite ADD COLUMN ViewID VARCHAR(50)";

	/** Add the constraint for the Favorite table's new ViewID column*/
	private static final String FAVORITE_CONSTRAINT_UPDATE = "ALTER TABLE Favorite ADD CONSTRAINT FK_Favorite_3 FOREIGN KEY (ViewID) REFERENCES Node (NodeID) ON DELETE CASCADE";                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                              

	
////////////////////

	/** Holds the Link and ViewLink table data when transfering for table update.*/
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

		int response = JOptionPane.showConfirmDialog(parent, "Due to software changes, your project data structure needs updating before you can continue.\n\nDo you wish to update it now?\n\nPress 'No' to quit\n\n",
												"Update project", JOptionPane.YES_NO_OPTION);

		if (response == JOptionPane.NO_OPTION || response == JOptionPane.CLOSED_OPTION) {
			return false;
		}
		else {
			DBDatabaseManager databaseManager = adminDatabase.getDatabaseManager();

			oThread = new ProgressThread("Updating Project Database...", "Project Update Completed");
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

		oThread = new ProgressThread("Updating Project Database..", "Project Update Completed");
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
					progressUpdate(increment, "Updating scheme for "+sFriendlyName);

					if (version.equals("1.3")) {
						progressUpdate(increment, sFriendlyName+": Updating link table..");
						successful = updateLinkTable(dbcon, sDatabaseName);
						if (successful) {
							progressUpdate(increment, sFriendlyName+": Updating linkgroup..");
							successful = insertDefaultLinkGroup(con);
							if (successful) {
								progressUpdate(increment, sFriendlyName+": Updating version..");
								successful = adminDatabase.updateVersion(con, "1.3.1");
								if (successful)
									version = "1.3.1";
							}
						}
					}
					if (version.equals("1.3.1")) {
						progressUpdate(increment, sFriendlyName+": Updating LinkView constraints..");
						successful = updateViewLinkTable(dbcon, adminDatabase, sDatabaseName);
						if (successful) {
							progressUpdate(increment, sFriendlyName+": Updating version..");
							successful = adminDatabase.updateVersion(con, "1.3.2");
							if (successful)
								version = "1.3.2";
						}
					}
					if (version.equals("1.3.2")) {
						progressUpdate(increment, sFriendlyName+": Renaming Tables..");
						successful = renameTables(dbcon);
						if (successful) {
							progressUpdate(increment, sFriendlyName+": Updating version..");
							successful = adminDatabase.updateVersion(con, "1.3.3");
							if (successful)
								version = "1.3.3";
						}
					}
					if (version.equals("1.3.3")) {
						// DON'T WANT THIS TO HAPPEN AS ONLY TAKEN OUT AGAIN IN NEXT SECTION AND
						// CAN'T DROP COLUMN YET IN DERBY 1st JUNE 05
						//progressUpdate(increment, sFriendlyName+": Adding Media Index Column..");
						//successful = addMediaIndex(dbcon);
						successful = true;
						if (successful) {
							progressUpdate(increment, sFriendlyName+": Updating version..");
							successful = adminDatabase.updateVersion(con, "1.3.4");
							if (successful)
								version = "1.3.4";
						}
					}
					if (version.equals("1.3.4")) {
						progressUpdate(increment, sFriendlyName+": Removing Media Index Column..");
						successful = removeMediaIndex(dbcon, adminDatabase, sDatabaseName);
						progressUpdate(increment, sFriendlyName+": Creating MediaIndex Table..");
						successful = createMediaIndexTable(dbcon, adminDatabase);
						if (successful) {
							progressUpdate(increment, sFriendlyName+": Updating version..");
							successful = adminDatabase.updateVersion(con, "1.3.5");
							if (successful)
								version = "1.3.5";
						}
					}
					if (version.equals("1.3.5")) {
						progressUpdate(increment, sFriendlyName+": Adding MeetingStatus Column..");
						successful = addMeetingStatusColumn(dbcon);
						if (successful) {
							progressUpdate(increment, sFriendlyName+": Updating version..");
							successful = adminDatabase.updateVersion(con, "1.3.6");
							if (successful)
								version = "1.3.6";
						}
					}
					if (version.equals("1.3.6")) {
						progressUpdate(increment, sFriendlyName+": Updating Original ID fields..");
						successful = updateOriginalID(dbcon, adminDatabase);
						if (successful) {
							progressUpdate(increment, sFriendlyName+": Updating version..");
							successful = adminDatabase.updateVersion(con, "1.3.7");
							if (successful)
								version = "1.3.7";
						}
					}
					if (version.equals("1.3.7")) {
						progressUpdate(increment, sFriendlyName+": Updating Reference Source field length..");
						successful = updateReferenceSource(dbcon, adminDatabase);
						if (successful) {
							progressUpdate(increment, sFriendlyName+": Updating version..");
							successful = adminDatabase.updateVersion(con, "1.3.8");
							if (successful)
								version = "1.3.8";
						}
					}
					if (version.equals("1.3.8")) {
						progressUpdate(increment, sFriendlyName+": Drop Node Property, Update NodeView..");
						successful = replaceNodePropertyTable(dbcon, adminDatabase);						
						if (successful) {						
							successful = addGroupwareColumns(dbcon);							
							if (successful) {
								progressUpdate(increment, sFriendlyName+": Updating version..");
								successful = adminDatabase.updateVersion(con, "1.3.9");
								if (successful) {
									version = "1.3.9";
								}
							}
						}
					}	
					if (version.equals("1.3.9")) {
						progressUpdate(increment, sFriendlyName+": Add ViewID column to Favorite..");
						successful = updateFavoriteTable(dbcon);
						if (successful) {
							progressUpdate(increment, sFriendlyName+": Updating version..");						
							// Make database version mathc release version for 1.5
							successful = adminDatabase.updateVersion(con, "1.5.0");
							if (successful) {
								version = "1.5.0";
							}
						}
					}																					
				}
				else {
					successful = true;
				}
			}
		}
		catch (Exception e) {
			System.out.println("Exception: "+e.getMessage());
  			e.printStackTrace();
  			System.out.flush();
			successful = false;
  		}

		if (!successful) {
			ProjectCompendium.APP.displayError("Your database structure was unable to be updated.\nPlease contact Compendium support staff.\n");
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
		ResultSet rs = dbmd.getIndexInfo(null, null, "LINK", false, false);

		//DOES NOT WORK IN DERBY
		//PreparedStatement pstmt = con.prepareStatement("SHOW INDEX FROM Link");
		//ResultSet rs = pstmt.executeQuery();
		boolean proceed = false;

		if (rs != null) {
			while (rs.next()) {
				String	sName = rs.getString(6);
				if (sName.equalsIgnoreCase("Link_ViewID_Ind")) {
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
				FileWriter fileWriter = new FileWriter("Backups"+System.getProperty("file.separator")+"LinkData_"+sDatabaseName+".sql");
				fileWriter.write(data.toString());
				fileWriter.close();
		        data = null;
			}
			catch(IOException io) {
				System.out.println("unable to backup link data");
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

			String sLinkID = "";
			java.util.Date dCreationDate = null;
			java.util.Date dModificationDate = null;
			java.util.Date dCreationDate2 = null;
			java.util.Date dModificationDate2 = null;

			String sAuthor = "";
			String sType = "";
			String sOriginalID = "";
			String sFrom = "";
			String sTo = "";
			String sViewID = "";
			int nArrow = 0;
			int nCurrentStatus = 0;
			int nStatus = 0;

			int count = links.size();

			progressCount(count+2);

			for (int i=0; i<count; i++) {

				progressUpdate(increment, "Updating link table..");

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
				nArrow = ((Integer)link.elementAt(9)).intValue();
				nCurrentStatus = ((Integer)link.elementAt(10)).intValue();

				dCreationDate2 = (java.util.Date)link.elementAt(11);
				dModificationDate2 = (java.util.Date)link.elementAt(12);
				nStatus = ((Integer)link.elementAt(13)).intValue();

				DBLink.recreate(dbcon, sLinkID, dCreationDate, dModificationDate, sAuthor, sType, "", sOriginalID, sFrom, sTo, "", nArrow, nCurrentStatus);
				DBViewLink.recreate(dbcon, sViewID, sLinkID, dCreationDate2, dModificationDate2, nStatus);
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

		data.append(MYSQL_DROP_VIEWLINK_TABLE+";\n\n");
		data.append(MYSQL_DROP_LINK_TABLE+";\n\n");
		data.append(MYSQL_CREATE_LINK_TABLE+";\n\n");
		data.append(MYSQL_CREATE_VIEWLINK_TABLE+";\n\n");

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
				data.append("(");
				data.append("\'"+sLinkID+"\',");
				data.append("\'"+CoreUtilities.cleanSQLText(sAuthor, FormatProperties.nDatabaseType)+"\',");
				data.append(dbCDate+",");
				data.append(dbMDate+",");
				data.append("\'"+sType+"\',");
				data.append("\'"+sOriginalID+"\',");
				data.append("\'"+sFrom+"\',");
				data.append("\'"+sTo+"\',");
				data.append("\'\',");
				data.append(nArrow.toString()+",");
				data.append(nCurrentStatus.toString());
				data.append(");\n");

				data.append(DBConstants.INSERT_VIEWLINK_QUERY_BASE);
				data.append("(");
				data.append("\'"+sViewID+"\',");
				data.append("\'"+sLinkID+"\',");
				data.append(dbCDate2+",");
				data.append(dbMDate2+",");
				data.append(nStatus.toString());
				data.append(");\n");

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
		pstmt.setString(1, "1");
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

		progressUpdate(increment, "Delete constraint..");

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
			ResultSet rs = dbmd.getExportedKeys(null, null, "VIEWLINK");

			if (rs != null) {
				while (rs.next()) {
					String	sName = rs.getString(12);
					if (sName.equalsIgnoreCase("FK_ViewLink_1")) {
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

		progressUpdate(increment, "Create constraint...");

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
			ResultSet rs = dbmd.getExportedKeys(null, null, "VIEWLINK");
			if (rs != null) {
				while (rs.next()) {
					String	sName = rs.getString(12);
					if (sName.equalsIgnoreCase("viewlink_ibfk_1")) {
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
			ResultSet rs = dbmd.getColumns(sDatabaseName, null, "VIEWNODE", "MEDIAINDEX");
			if (rs != null) {
				PreparedStatement pstmt2 = con.prepareStatement(DROP_MEDIAINDEX_COLUMN);
				pstmt2.executeUpdate();
				pstmt2.close();
			}
		}

		return true;
	}

	/**
	 * Remove the MediaIndex column from the ViewNode table.
	 * @param dbcon, the DBConnection to use to run the sql.
	 * @param adminDatabase the database admin object required to check database type.
	 */
	private static boolean createMediaIndexTable(DBConnection dbcon, DBAdminDatabase adminDatabase) throws SQLException {

		Connection con = dbcon.getConnection();

		// IF CALLED AFTER A RESTORE, TABLE MAY BE ADDED
		// CHECK IF MEETING TABLE EXISTS
		DatabaseMetaData dbmd = con.getMetaData();
		ResultSet rs = dbmd.getTables(null, null, "MEETING", null);
		boolean proceed = true;
		if (rs != null) {
			while (rs.next()) {
				String	sName = rs.getString(3);
				if (sName.equalsIgnoreCase("Meeting")) {
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
		ResultSet rs = dbmd.getColumns(null, null, "MEETING", "CURRENTSTATUS");
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
		ResultSet rs = dbmd.getTables(null, null, "NODEPROPERTY", null);

		boolean proceed = false;
		if (rs != null) {
			while (rs.next()) {
				String	sName = rs.getString(3);
				if (sName.equalsIgnoreCase("NodeProperty")) {
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
		rs = dbmd.getColumns(null, null, "VIEWNODE", "BACKGROUND");		
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
		ResultSet rs = dbmd.getColumns(null, null, "NODE", "LASTMODAUTHOR");
		if (rs == null || !rs.next()) {
			PreparedStatement pstmt = con.prepareStatement(UPDATE_NODE_TABLE);
			pstmt.executeUpdate();
			pstmt.close();
		}
		
		// IF CALLED AFTER A RESTORE, TABLE WILL HAVE NEW COLUMNS
		// CHECK IF COLUMN EXISTS
		rs = dbmd.getColumns(null, null, "USERS", "LINKVIEW");
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
		ResultSet rs = dbmd.getColumns(null, null, "FAVORITE", "VIEWID");
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
