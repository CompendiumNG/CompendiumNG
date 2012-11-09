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

package com.compendium.core.db.management;

import java.sql.*;
import java.io.*;
import java.util.*;
import java.util.zip.*;

import com.compendium.core.datamodel.*;
import com.compendium.core.*;
import com.compendium.ui.UIImages;

/**
 * This class is responsible for backing up user databases. Information is stored as SQL statements in a text file.
 * It allows external classes to register DBProgressListeners and fires appropriate progress information to them.
 * This facilitates the display of progress information in a user interface, if desired.
 *
 * @author Michelle Bachler
 */
public class DBBackupDatabase implements DBConstants, DBConstantsMySQL {

	/** The header for the backup file for Derby backups.*/
	public static String DERBY_DATABASE_HEADER_CHECK = "DATABASE TYPE = DERBY";

	/** The header for the backup file for MySQL backups.*/
	public static String MYSQL_DATABASE_HEADER_CHECK = "DATABASE TYPE = MYSQL";

	/** The header for the backup file for Derby backups.*/
	private static String DERBY_DATABASE_HEADER = "DATABASE TYPE = DERBY :"+ICoreConstants.sDATABASEVERSION;

	/** The header for the backup file for MySQL backups.*/
	private static String MYSQL_DATABASE_HEADER = "DATABASE TYPE = MYSQL :"+ICoreConstants.sDATABASEVERSION;

	/** A Vector of registered DBProgressListeners */
	protected Vector progressListeners;

	/** The length a StringBuffer can reach. */
	private static final double BUFFER_LIMIT = 25000000;

	/**
	 * An integer representing the total count of the progress updates required,
	 * There is one for each table backedup plus an additional one when opening the database connection.
	 */
	private static final int DEFAULT_COUNT = 35; //33 tables + 1 extra comment

	/** An integer representing the increment to use for the progress updates */
	private int					increment = 1;
	
	private FileWriter					dumpfile = null;

	/** The name to use when accessing the MySQL database */
	private String sDatabaseUserName = ICoreConstants.sDEFAULT_DATABASE_USER;

	/** The password to use when accessing the MySQL database */
	private String sDatabasePassword = ICoreConstants.sDEFAULT_DATABASE_PASSWORD;

	/** The password to use when accessing the MySQL database */
	private String sDatabaseIP = ICoreConstants.sDEFAULT_DATABASE_ADDRESS;

	/** The type of the database application to create an empty database for.*/
	private int nDatabaseType = -1;

	/** This Hashtable will hold a list of all associated references when backing up */
	private Hashtable htResources = new Hashtable(51);

	/** This boolean says whether to backup the database with all associated resources*/
	private boolean bWithResources = false;

	/** This boolean says whether to preserve reference file paths in the zip file*/
	private boolean bKeepPaths = false;

	/** This boolean says whether to backup movie files */
	private boolean bIncludeMovies = false;

	/** This boolean says whether to backup the Templates folder */
	private boolean bIncludeTemplates = false;

	/** This boolean says whether to backup deleted items*/
	private boolean bIncludeTrash = false;

	/** This is the name of the backup file / zip / resource subfolder */
	private String sBackupName = "";

	/** This is the new path to change resource paths to */
	private String sBackupPath = "";

	/** This is the path where the backup is being put*/
	private String sResourcePath = "";

	/** The system file separator */
	private String sFS = System.getProperty("file.separator");

	/** Indicates if one or more external resources being backed up could not be found*/
	private boolean bNotFound = false;


	/**
	 * Constructor, creates an empty Vector to hold the DBProgressListeners which may be registered.
	 * This constructor also takes a name and password to use when accessing the database
	 * and the IP address of the server machine.
	 *
	 * @param nDatabaseType, the type of the database being used (e.g, MySQL, Derby).
	 * @param sDatabaseUserName, the name to use when creating the connection to the database
	 * @param sDatabasePassword, the password to use when connection to the database
	 * @param sDatabaseIP, the IP address of the server machine. The default if 'localhost'.
	 */
	public DBBackupDatabase(int nDatabaseType, String sDatabaseUserName, String sDatabasePassword, String sDatabaseIP) {
		progressListeners = new Vector();
		this.sDatabaseUserName = sDatabaseUserName;
		this.sDatabasePassword = sDatabasePassword;
		this.nDatabaseType = nDatabaseType;
		if (sDatabaseIP != null && !sDatabaseIP.equals(""))
			this.sDatabaseIP = sDatabaseIP;
	}

	/**
	 * Store the data in the database with the given name, into the passed File, as SQL statments
	 *
	 * @param String sName, the database name of the database to backup.
	 * @param File file, a file object representing the file to write the backedup data to.
	 * @param boolean fullRecreation, indicates whether data only, or delete and create statment should be sotred to file.
	 * @exception java.io.IOException
	 * @exception java.lang.ClassNotFoundException
	 * @exception java.sql.SQLException
	 */
	public void backupDatabase(String sName, File file, boolean fullRecreation) throws DBDatabaseTypeException, IOException, SQLException, ClassNotFoundException {

		fireProgressCount(DEFAULT_COUNT);
		fireProgressUpdate(increment, "Opening Connection..");

		Connection connection = DBConnectionManager.getPlainConnection(nDatabaseType, sName, sDatabaseUserName, sDatabasePassword, sDatabaseIP);
		if (connection == null)
			throw new DBDatabaseTypeException("Database type "+nDatabaseType+" not found");

		dumpfile = new FileWriter(file);		// open the dump file
		
		backupTables(connection, fullRecreation);
		
		try {
			connection.close();
		}
		catch(ConcurrentModificationException io) {
            io.printStackTrace();
			System.out.println("Exception closing connection for backup database:\n\n"+io.getMessage());
		}

		dumpfile.flush();
		dumpfile.close();

		fireProgressComplete();
	}

	/**
	 * Store the data in the database with the given name, into the passed zip File, as SQL statments with external resource files.
	 *
	 * @param sName the database name of the database to backup.
	 * @param sFriendlyName the name of the project that the user sees.
	 * @param file a file object representing the file to write the backedup data to.
	 * @param fullRecreation indicates whether data only, or delete and create statment should be sotred to file.
	 * @param bWithResources indicates whether associated resource should also be backed up.
	 * @param bToZip indicates whether backup and resource should be zipped.
	 * @param oUser the current user backing up - required for creating the correct backup path for the Linked Files area.
	 * @exception java.io.IOException
	 * @exception java.lang.ClassNotFoundException
	 * @exception java.sql.SQLException
	 */
	public void backupDatabaseToZip(String sName, String sFriendlyName, File file, 
			boolean fullRecreation, boolean bKeepPaths, UserProfile oUser) throws DBDatabaseTypeException, IOException, SQLException, ClassNotFoundException {
	
		backupDatabaseToZip(sName, sFriendlyName, file, fullRecreation, bKeepPaths, true, true, true, oUser);
	}

	/**
	 * Store the data in the database with the given name, into the passed zip File, as SQL statments with external resource files.
	 *
	 * @param sName the database name of the database to backup.
	 * @param sFriendlyName the name of the project that the user sees.
	 * @param file a file object representing the file to write the backedup data to.
	 * @param fullRecreation indicates whether data only, or delete and create statment should be sotred to file.
	 * @param bWithResources indicates whether associated resource should also be backed up.
	 * @param bToZip indicates whether backup and resource should be zipped.
	 * @param bIncludeMovies indicates whether backup Movie files.
	 * @param bIncludeTemplates indicates whether backup templates folder.
	 * @param bIncludeTrash indicates whether backup deleted items.
	 * @param oUser the current user backing up - required for creating the correct backup path for the Linked Files area.
	 * @exception java.io.IOException
	 * @exception java.lang.ClassNotFoundException
	 * @exception java.sql.SQLException
	 */
	public void backupDatabaseToZip(String sName, String sFriendlyName, File file, 
			boolean fullRecreation, boolean bKeepPaths, boolean bIncludeMovies, 
			boolean bIncludeTemplates, boolean bIncludeTrash, UserProfile oUser) throws DBDatabaseTypeException, IOException, SQLException, ClassNotFoundException {

		this.bWithResources = true;
		this.bKeepPaths = bKeepPaths;
		this.bIncludeMovies = bIncludeMovies;
		this.bIncludeTemplates = bIncludeTemplates;
		this.bIncludeTrash = bIncludeTrash;
		
		fireProgressCount(DEFAULT_COUNT + 3);
		fireProgressUpdate(increment, "Opening Connection..");

	    Connection connection = DBConnectionManager.getPlainConnection(nDatabaseType, sName, sDatabaseUserName, sDatabasePassword, sDatabaseIP);
		if (connection == null)
			throw new DBDatabaseTypeException("Database type "+nDatabaseType+" not found");
		
		//CREATE PATHS
		String path = file.getAbsolutePath();
		sBackupName = file.getName();

		int ind = sBackupName.lastIndexOf(".");
		if (ind != -1) {
			sBackupName = sBackupName.substring(0, ind);
		}
		
		int index = path.lastIndexOf(sFS);
		if (index != -1) {
			sResourcePath = path.substring(0, index+1);
			// ALWAYS USE UNIX SLASH AS IT WORKS ON ALL THREE PLATFFORMS
			
			String sDatabaseName = CoreUtilities.cleanFileName(sFriendlyName);						
			String sUserDir = CoreUtilities.cleanFileName(oUser.getUserName())+"_"+oUser.getId();
			sBackupPath = "Linked Files/"+sDatabaseName+"/"+sUserDir;			
		}

		dumpfile = new FileWriter(sResourcePath+sBackupName+".sql");	// open the dump file
				
		backupTables(connection, fullRecreation);

		dumpfile.flush();
		dumpfile.close();			
		
		try {
			connection.close();
		}
		catch(ConcurrentModificationException io) {
            io.printStackTrace();
			System.out.println("Exception closing connection for backup database:\n\n"+io.getMessage());
		}

		// ADD LINK GROUPS
		addLinkGroupsToResources();

		// ADD STENCILS
		addStencilsToResources();

		// ADD TEMPLATES FOLDER AND SUBFOLDERS RECURSIVELY
		// MB: need to handle this being too much for zip file
		if (bIncludeTemplates) {
			fireProgressUpdate(increment, "Backing up Templates");
			File main = new File("Templates");
			addFilesToResources(main);
		}
		
		// ZIP ALL TOGETHER
		try {
			int BUFFER = 2048;
			BufferedInputStream origin = null;
			FileInputStream fi = null;
			int count = 0;

			FileOutputStream dest = new FileOutputStream(file.getAbsolutePath());
			ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(dest));
			out.setMethod(ZipOutputStream.DEFLATED);
			byte data2[] = new byte[BUFFER];

			//ADD SQL FILE
			fireProgressCount(htResources.size()+2);
			fireProgressUpdate(increment, "Writing sql file to zip..");

			fi = new FileInputStream(sResourcePath+sBackupName+".sql");
			origin = new BufferedInputStream(fi, BUFFER);
			ZipEntry entry = new ZipEntry("Backups"+sFS+sBackupName+".sql");
			out.putNextEntry(entry);
			while((count = origin.read(data2, 0, BUFFER)) != -1) {
				out.write(data2, 0, count);
			}
			origin.close();
			File sqlFile = new File(sResourcePath+sBackupName+".sql");
			sqlFile.delete();

			// ADD RESOURCES
			fireProgressUpdate(increment, "Writing files to zip..");
			for (Enumeration e = htResources.keys(); e.hasMoreElements() ;) {
				
				String sOldFilePath = (String)e.nextElement();
				String sNewFilePath = (String)htResources.get(sOldFilePath);
				
				try {
					fi = new FileInputStream(sOldFilePath);
					origin = new BufferedInputStream(fi, BUFFER);

					entry = new ZipEntry(sNewFilePath);
					out.putNextEntry(entry);

					while((count = origin.read(data2, 0, BUFFER)) != -1) {
						out.write(data2, 0, count);
					}
					origin.close();

					fireProgressUpdate(increment, "Writing to zip..");
				}
				catch (Exception ex) {
					System.out.println("Unable to backup database resource: \n\n"+sOldFilePath+"\n\n"+ex.getMessage());
				}
			}
			out.close();
		}
		catch(Exception e) {
			e.printStackTrace();
		}

		fireProgressComplete();
	}

	/**
	 * Return true if some external reference resources could not be found.
	 */
	public boolean getNotFound() {
		return bNotFound;
	}

	/**
	 * Load the link group file names into the htResources table.
	 */
	public void addLinkGroupsToResources() {

		fireProgressUpdate(increment, "Backing up Link Groups");

		File main = new File("System"+sFS+"resources"+sFS+"LinkGroups");
		File oLinkGroups[] = main.listFiles();
		String sOldLinkGroupPath = "";
		String sNewLinkGroupPath = "";
		File nextLinkGroup = null;

		for (int i=0; i< oLinkGroups.length; i++) {
			nextLinkGroup = oLinkGroups[i];
			if (nextLinkGroup.getName().startsWith(".")) {
				continue;
			}
			sOldLinkGroupPath = nextLinkGroup.getAbsolutePath();
			if (!htResources.containsKey(sOldLinkGroupPath)) {
				sNewLinkGroupPath = "System"+sFS+"resources"+sFS+"LinkGroups"+sFS+ nextLinkGroup.getName();
				htResources.put(sOldLinkGroupPath, sNewLinkGroupPath);
			}
		}
	}

	/**
	 * Load the stencil files into the htResources table.
	 */
	public void addStencilsToResources() {

		fireProgressUpdate(increment, "Backing up Stencils");

		String sStencilPath = "System"+sFS+"resources"+sFS+"Stencils"+sFS;
		File main = new File("System"+sFS+"resources"+sFS+"Stencils");
		File oStencils[] = main.listFiles();

		String sOldStencilName = "";
		String sStencilName = "";
		String sOldStencilImageName = "";
		String sStencilImageName = "";

		for (int i=0; i<oStencils.length; i++) {
			File nextStencil = oStencils[i];
			if (nextStencil.getName().startsWith(".")) {
				continue;
			}

			// EACH SEPARATE STENCIL SET IS IN A SUBFOLDER
			if (nextStencil.isDirectory()) {

				String sSubStencilPath = sStencilPath+nextStencil.getName()+"/";
				File oStencilsSub[] = nextStencil.listFiles();

				for (int j=0; j<oStencilsSub.length; j++) {
					File nextSubStencil = oStencilsSub[j];
					if (nextSubStencil.getName().startsWith(".")) {
						continue;
					}

					// EACH STENCIL SET CONSTITS OF ONE XML FILE AND TWO DIRECTORIES OF IMAGES
					if (nextSubStencil.isDirectory()) {

						String sStencilImagePath = sSubStencilPath+nextSubStencil.getName()+"/";
						File oStencilImages[] = nextSubStencil.listFiles();

						for (int k=0; k<oStencilImages.length; k++) {
							File nextStencilImage = oStencilImages[k];
							if (nextStencilImage.getName().startsWith(".")) {
								continue;
							}

							sStencilImageName = nextStencilImage.getName();
							sOldStencilImageName = nextStencilImage.getAbsolutePath();
							if (!htResources.containsKey(sOldStencilImageName)) {
								sStencilImageName = sStencilImagePath + sStencilImageName;
								htResources.put(sOldStencilImageName, sStencilImageName);
							}
						}
					}
					else {
						sStencilName = nextSubStencil.getName();
						sOldStencilName = nextSubStencil.getAbsolutePath();
						if (!htResources.containsKey(sOldStencilName)) {
							sStencilName = sSubStencilPath + sStencilName;
							htResources.put(sOldStencilName, sStencilName);
						}
					}
				}
			}
		}
	}

	/**
	 * Load the template files into the htResources table.
	 */
	public void addFilesToResources(File folder) {

		File oFiles[] = folder.listFiles();

		String sFullPath = "";
		String sRelativePath = "";

		for (int i=0; i<oFiles.length; i++) {
			File next = oFiles[i];
			if (next.getName().startsWith(".")) {
				continue;
			}

			if (next.isDirectory()) {
				addFilesToResources(next);
			} else {
				sFullPath = next.getAbsolutePath();
				try {
					sRelativePath =  getRelativePath(next);
					if (!htResources.containsKey(sFullPath)) {
						htResources.put(sFullPath, sRelativePath);
					}
				} catch(Exception e) {
					System.out.println("Unable to add template resource: "+sFullPath);
				}
			}

		}
	}

	/**
	 * Work out and return the string of the path to the template file relative to the Compendium folder.
	 * @param nextfile the File to work out the relative path for
	 * @return the string of the path to the template file relative to the Compendium folder
	 * @throws Exception if something goes wrong.
	 */
	public String getRelativePath(File nextfile) throws Exception {
		String path = "";
		File file = nextfile.getCanonicalFile();
			
		int count = 0;
		while (file != null && !file.getName().equals("Templates")) {
			if (path.equals("")) {
				path = file.getName();
			} else {
				path = file.getName()+sFS+path;				
			}
			file = file.getParentFile();
		}
		
		path = "Templates"+sFS+path;
		
		return path.toString();
	} 
	
	/**
	 * Backup the all the tables in the database opened on the given connection.
	 *
	 * @param Connection con, the connection object to use to read the data from the database.
	 * @param boolean fullRecreation, indicates if the table drop and create statement should be stored to file.
	 */
	private void backupTables(Connection con, boolean fullRecreation) throws SQLException, IOException {		
		if (nDatabaseType == ICoreConstants.DERBY_DATABASE)
			 dumpfile.write(DERBY_DATABASE_HEADER+"\n");
		else
			 dumpfile.write(MYSQL_DATABASE_HEADER+"\n");

		fireProgressUpdate(increment, "Backing up System Table");
		backupSystemTable(con, fullRecreation);

        //System.out.println("data length after System = "+data.length());
		//System.out.flush();

		fireProgressUpdate(increment, "Backing up User Table");
		backupUserTable(con, fullRecreation);

        //System.out.println("data length after User= "+data.length());
		//System.out.flush();

		fireProgressUpdate(increment, "Backing up Node Table");
		backupNodeTable(con, fullRecreation);

        //System.out.println("data length after Node= "+data.length());
		//System.out.flush();

		fireProgressUpdate(increment, "Backing up Reference Node Table");
		backupReferenceNodeTable(con, fullRecreation);

        //System.out.println("data length after reference= "+data.length());
		//System.out.flush();

		fireProgressUpdate(increment, "Backing up Code Table");
		backupCodeTable(con, fullRecreation);

        //System.out.println("data length after code= "+data.length());
		//System.out.flush();

		fireProgressUpdate(increment, "Backing up Link Table");
		backupLinkTable(con, fullRecreation);

        //System.out.println("data length after link= "+data.length());
		//System.out.flush();

		fireProgressUpdate(increment, "Backing up ViewNode Table");
		backupViewNodeTable(con, fullRecreation);

        //System.out.println("data length after viewnode = "+data.length());
		//System.out.flush();

		fireProgressUpdate(increment, "Backing up UserState Table");
		backupNodeUserStateTable(con, fullRecreation);

        //System.out.println("data length after userstate= "+data.length());
		//System.out.flush();

		fireProgressUpdate(increment, "Backing up ViewLink Table");
		backupViewLinkTable(con, fullRecreation);

        //System.out.println("data length after viewlink= "+data.length());
		//System.out.flush();

		fireProgressUpdate(increment, "Backing up NodeCode Table");
		backupNodeCodeTable(con, fullRecreation);

        //System.out.println("data length after nodecode= "+data.length());
		//System.out.flush();

		fireProgressUpdate(increment, "Backing up CodeGroup Table");
		backupCodeGroupTable(con, fullRecreation);

        //System.out.println("data length after codegroup= "+data.length());
		//System.out.flush();

		fireProgressUpdate(increment, "Backing up GroupCodes Table");
		backupGroupCodeTable(con, fullRecreation);

        //System.out.println("data length after groupcode= "+data.length());
		//System.out.flush();

		fireProgressUpdate(increment, "Backing up Favorites Table");
		backupFavoriteTable(con, fullRecreation);

        //System.out.println("data length after favorites = "+data.length());
		//System.out.flush();

		fireProgressUpdate(increment, "Backing up Workspace Table");
		backupWorkspaceTable(con, fullRecreation);

        //System.out.println("data length after workspace = "+data.length());
		//System.out.flush();

		fireProgressUpdate(increment, "Backing up ViewProperty Table");
		backupViewPropertyTable(con, fullRecreation);

        //System.out.println("data length after viewproperty"+data.length());
		//System.out.flush();

		fireProgressUpdate(increment, "Backing up Node Details Table");
		backupNodeDetailTable(con, fullRecreation);

        //System.out.println("data length after nodedetail= "+data.length());
		//System.out.flush();

		fireProgressUpdate(increment, "Backing up ShortcutNode Table");
		backupShortCutNodeTable(con, fullRecreation);

        //System.out.println("data length after shortcut= "+data.length());
		//System.out.flush();

		fireProgressUpdate(increment, "Backing up Audit Table");
		backupAuditTable(con, fullRecreation);

        //System.out.println("data length after audit= "+data.length());
		//System.out.flush();

		fireProgressUpdate(increment, "Backing up Clone Table");
		backupCloneTable(con, fullRecreation);

        //System.out.println("data length after clone= "+data.length());
		//System.out.flush();

		fireProgressUpdate(increment, "Backing up ExtendedNode Table");
		backupExtendedNodeTable(con, fullRecreation);

        //System.out.println("data length after extendednode= "+data.length());
		//System.out.flush();

		fireProgressUpdate(increment, "Backing up ExtendedCode Table");
		backupExtendedCodeTable(con, fullRecreation);

        //System.out.println("data length after extended code= "+data.length());
		//System.out.flush();

		fireProgressUpdate(increment, "Backing up UserGroup Table");
		backupUserGroupTable(con, fullRecreation);

        //System.out.println("data length after usergroup= "+data.length());
		//System.out.flush();

		fireProgressUpdate(increment, "Backing up GroupUser Table");
		backupGroupUserTable(con, fullRecreation);

        //System.out.println("data length after groupuser = "+data.length());
		//System.out.flush();

		fireProgressUpdate(increment, "Backing up Permission Table");
		backupPermissionTable(con, fullRecreation);

        //System.out.println("data length after permission = "+data.length());
		//System.out.flush();

		// NEW 1.3 TABLES
		fireProgressUpdate(increment, "Backing up ViewLayer Table");
		backupViewLayerTable(con, fullRecreation);

        //System.out.println("data length after viewlayer = "+data.length());
		//System.out.flush();

		fireProgressUpdate(increment, "Backing up Connection Table");
		backupConnectionTable(con, fullRecreation);

        //System.out.println("data length after connection = "+data.length());
		//System.out.flush();

		fireProgressUpdate(increment, "Backing up Preference Table");
		backupPreferenceTable(con, fullRecreation);

        //System.out.println("data length after preference = "+data.length());
		//System.out.flush();

		fireProgressUpdate(increment, "Backing up Meeting Table");
		backupMeetingTable(con, fullRecreation);

        //System.out.println("data length after meeting = "+data.length());
		//System.out.flush();

		fireProgressUpdate(increment, "Backing up MediaIndex Table");
		backupMediaIndexTable(con, fullRecreation);

        //System.out.println("data length after media index = "+data.length());
		//System.out.flush();

		fireProgressUpdate(increment, "Backing up LinkedFile Table");
		backupLinkedFileTable(con, fullRecreation);		
		
		fireProgressUpdate(increment, "Backing up ViewTimeNode Table");
		backupViewTimeNodeTable(con, fullRecreation);		

		fireProgressUpdate(increment, "Backing up Movie Table");
		backupMoviesTable(con, fullRecreation);		

		fireProgressUpdate(increment, "Backing up MovieProperties Table");
		backupMoviePropertiesTable(con, fullRecreation);		
}

	/**
	 * Backup the System table.
	 *
	 * @param Connection con, the connection object to use to read the data from the database.
	 * @param boolean fullRecreation, indicates if the table drop and create statement should be stored to file.
	 */
	private void backupSystemTable(Connection con, boolean fullRecreation) throws SQLException, IOException {

		if (fullRecreation) {
			if (nDatabaseType == ICoreConstants.DERBY_DATABASE) {
				dumpfile.write(DBConstantsDerby.DROP_SYSTEM_TABLE+"\n\n");
				dumpfile.write(DBConstantsDerby.CREATE_SYSTEM_TABLE+"\n\n");
			} else {
				dumpfile.write(DBConstantsMySQL.MYSQL_DROP_SYSTEM_TABLE+";\n\n");				
				dumpfile.write(DBConstantsMySQL.MYSQL_CREATE_SYSTEM_TABLE+";\n\n");
			}
		}

		PreparedStatement pstmt1 = con.prepareStatement(GET_SYSTEM_QUERY);
		ResultSet rs = pstmt1.executeQuery();

		if (rs != null) {

			while (rs.next()) {
				String	sProperty		= rs.getString(1);
				String	sContents		= rs.getString(2);
				
				dumpfile.write(INSERT_SYSTEM_QUERY_BASE);
				dumpfile.write("(");
				dumpfile.write("\'"+CoreUtilities.cleanSQLText(sProperty, nDatabaseType)+"\',");
				dumpfile.write("\'"+CoreUtilities.cleanSQLText(sContents, nDatabaseType)+"\'");
				dumpfile.write(")\n");
			}
			dumpfile.write("\n");
		}
        pstmt1.close();
        rs.close();
	}

	/**
	 * Backup the User table.
	 *
	 * @param Connection con, the connection object to use to read the data from the database.
	 * @param boolean fullRecreation, indicates if the table drop and create statement should be stored to file.
	 */
	private void backupUserTable(Connection con, boolean fullRecreation) throws SQLException, IOException {

		if (fullRecreation) {
			if (nDatabaseType == ICoreConstants.DERBY_DATABASE) {
				dumpfile.write(DBConstantsDerby.DROP_USER_TABLE+"\n\n");				
				dumpfile.write(DBConstantsDerby.CREATE_USER_TABLE+"\n\n");
			} else {
				dumpfile.write(DBConstantsMySQL.MYSQL_DROP_USER_TABLE+";\n\n");								
				dumpfile.write(DBConstantsMySQL.MYSQL_CREATE_USER_TABLE+";\n\n");
			}
		}

		PreparedStatement pstmt1 = con.prepareStatement(GET_USER_QUERY);
		ResultSet rs = pstmt1.executeQuery();

		if (rs != null) {

			while (rs.next()) {

				String  sUserID		= rs.getString(1);
				String	sAuthor		= rs.getString(2);
				long  dbCDate		= rs.getLong(3);
				long  dbMDate		= rs.getLong(4);
				String  loginName	= rs.getString(5);
				String	userName	= rs.getString(6);
				String  password	= rs.getString(7);
				String	userDesc	= rs.getString(8);
				String	homeViewId	= rs.getString(9);
				String  admin 		= rs.getString(10);
				int nCurrentStatus	= rs.getInt(11);
				String	linkViewId	= rs.getString(12);				

				if (linkViewId == null) {
					linkViewId="";
				}
				
				dumpfile.write(INSERT_USER_QUERY_BASE);
				dumpfile.write("(");
				dumpfile.write("\'"+sUserID+"\',");
				dumpfile.write("\'"+CoreUtilities.cleanSQLText(sAuthor, nDatabaseType)+"\',");
				dumpfile.write(String.valueOf(dbCDate)+",");
				dumpfile.write(String.valueOf(dbMDate)+",");
				dumpfile.write("\'"+CoreUtilities.cleanSQLText(loginName, nDatabaseType)+"\',");
				dumpfile.write("\'"+CoreUtilities.cleanSQLText(userName, nDatabaseType)+"\',");
				dumpfile.write("\'"+CoreUtilities.cleanSQLText(password, nDatabaseType)+"\',");
				dumpfile.write("\'"+CoreUtilities.cleanSQLText(userDesc, nDatabaseType)+"\',");
				dumpfile.write("\'"+homeViewId+"\',");
				dumpfile.write("\'"+admin+"\',");
				dumpfile.write(String.valueOf(nCurrentStatus)+",");
				dumpfile.write("\'"+linkViewId+"\'");				
				dumpfile.write(")\n");
			}
			dumpfile.write("\n");
		}
        pstmt1.close();
        rs.close();
	}

	/**
	 * Backup the Node table.
	 *
	 * @param Connection con, the connection object to use to read the data from the database.
	 * @param boolean fullRecreation, indicates if the table drop and create statement should be stored to file.
	 */
	private void backupNodeTable(Connection con, boolean fullRecreation) throws SQLException, IOException {

		if (fullRecreation) {
			if (nDatabaseType == ICoreConstants.DERBY_DATABASE) {
				dumpfile.write(DBConstantsDerby.DROP_NODE_TABLE+"\n\n");				
				dumpfile.write(DBConstantsDerby.CREATE_NODE_TABLE+"\n\n");
			} else {
				dumpfile.write(DBConstantsMySQL.MYSQL_DROP_NODE_TABLE+";\n\n");								
				dumpfile.write(DBConstantsMySQL.MYSQL_CREATE_NODE_TABLE+";\n\n");
			}
		}		

		String query  = GET_NODE_QUERY;
		if (!bIncludeTrash) {
			query += " Where CurrentStatus != 3";
		}
		
 		PreparedStatement pstmt1 = con.prepareStatement(query);
 		 		
		ResultSet rs = pstmt1.executeQuery();
		
		if (rs != null) {
			
			
			while (rs.next()) {
				
				String	sNodeID		= rs.getString(1);
				String	sAuthor		= rs.getString(2);
				long	dbCDate		= rs.getLong(3);
				long	dbMDate		= rs.getLong(4);
				int		nType		= rs.getInt(5);
				String	sOriginalID	= rs.getString(6);
				String	nXNodeType 	= rs.getString(7) ;
				String	sLabel		= rs.getString(8);
				String	sDetail 	= rs.getString(9);
				int		nCurrentStatus	= rs.getInt(10);
				String	sLastModAuthor	= rs.getString(11);
				
				if (sLastModAuthor == null) {
					sLastModAuthor=sAuthor;
				}

				dumpfile.write(INSERT_NODE_QUERY_BASE);
				dumpfile.write("(");
				dumpfile.write("\'"+sNodeID+"\',");
				dumpfile.write("\'"+CoreUtilities.cleanSQLText(sAuthor, nDatabaseType)+"\',");
				dumpfile.write(String.valueOf(dbCDate)+",");
				dumpfile.write(String.valueOf(dbMDate)+",");
				dumpfile.write(String.valueOf(nType)+",");
				dumpfile.write("\'"+sOriginalID+"\',");
				dumpfile.write("\'"+nXNodeType+"\',");
				dumpfile.write("\'"+CoreUtilities.cleanSQLText(sLabel, nDatabaseType)+"\',");
				dumpfile.write("\'"+CoreUtilities.cleanSQLText(sDetail, nDatabaseType)+"\',");
				dumpfile.write(String.valueOf(nCurrentStatus)+",");
				dumpfile.write("\'"+CoreUtilities.cleanSQLText(sLastModAuthor, nDatabaseType)+"\'");				
				dumpfile.write(")\n");
			}
			dumpfile.write("\n");
		}
        pstmt1.close();
        rs.close();
	}

	/**
	 * Backup the Link table.
	 *
	 * @param Connection con, the connection object to use to read the data from the database.
	 * @param boolean fullRecreation, indicates if the table drop and create statement should be stored to file.
	 */
	private void backupLinkTable(Connection con, boolean fullRecreation) throws SQLException, IOException {

		if (fullRecreation) {
			if (nDatabaseType == ICoreConstants.DERBY_DATABASE) {
				dumpfile.write(DBConstantsDerby.DROP_LINK_TABLE+"\n\n");				
				dumpfile.write(DBConstantsDerby.CREATE_LINK_TABLE+"\n\n");
			} else {
				dumpfile.write(DBConstantsMySQL.MYSQL_DROP_LINK_TABLE+";\n\n");								
				dumpfile.write(DBConstantsMySQL.MYSQL_CREATE_LINK_TABLE+";\n\n");
			}
		}

		String query  = GET_LINK_QUERY;
		if (!bIncludeTrash) {
			query += " Where CurrentStatus != 3";
		}

		PreparedStatement pstmt1 = con.prepareStatement(query);
		ResultSet rs = pstmt1.executeQuery();

		if (rs != null) {

			while (rs.next()) {

				String	sLinkID		= rs.getString(1);
				String	sAuthor		= rs.getString(2);
				long	dbCDate		= rs.getLong(3);
				long	dbMDate		= rs.getLong(4);
				String	sType		= rs.getString(5);
				String	sOriginalID	= rs.getString(6);
				String	sFrom		= rs.getString(7);
				String	sTo		 	= rs.getString(8);
				String 	sLabel 		= rs.getString(9);
				int		nCurrentStatus	= rs.getInt(10);

				dumpfile.write(INSERT_LINK_QUERY_BASE);
				dumpfile.write("(");
				dumpfile.write("\'"+sLinkID+"\',");
				dumpfile.write("\'"+CoreUtilities.cleanSQLText(sAuthor, nDatabaseType)+"\',");
				dumpfile.write(String.valueOf(dbCDate)+",");
				dumpfile.write(String.valueOf(dbMDate)+",");
				dumpfile.write("\'"+sType+"\',");
				dumpfile.write("\'"+sOriginalID+"\',");
				dumpfile.write("\'"+sFrom+"\',");
				dumpfile.write("\'"+sTo+"\',");
				dumpfile.write("\'"+CoreUtilities.cleanSQLText(sLabel, nDatabaseType)+"\',");
				dumpfile.write(String.valueOf(nCurrentStatus));
				dumpfile.write(")\n");
			}
			dumpfile.write("\n");
		}
        pstmt1.close();
        rs.close();
	}

	/**
	 * Backup the Code table.
	 *
	 * @param Connection con, the connection object to use to read the data from the database.
	 * @param boolean fullRecreation, indicates if the table drop and create statement should be stored to file.
	 */
	private void backupCodeTable(Connection con, boolean fullRecreation) throws SQLException, IOException {

		if (fullRecreation) {
			if (nDatabaseType == ICoreConstants.DERBY_DATABASE) {
				dumpfile.write(DBConstantsDerby.DROP_CODE_TABLE+"\n\n");				
				dumpfile.write(DBConstantsDerby.CREATE_CODE_TABLE+"\n\n");
			} else {
				dumpfile.write(DBConstantsMySQL.MYSQL_DROP_CODE_TABLE+";\n\n");				
				dumpfile.write(DBConstantsMySQL.MYSQL_CREATE_CODE_TABLE+";\n\n");
			}
		}

		PreparedStatement pstmt1 = con.prepareStatement(GET_CODE_QUERY);
		ResultSet rs = pstmt1.executeQuery();

		if (rs != null) {

			while (rs.next()) {

				String	sCodeID		= rs.getString(1);
				String	sAuthor		= rs.getString(2);
				long	dbCDate		= rs.getLong(3);
				long	dbMDate		= rs.getLong(4);
				String	sName		= rs.getString(5);
				String	sDesc	 	= rs.getString(6);
				String 	sBehaviour 	= rs.getString(7);

				dumpfile.write(INSERT_CODE_QUERY_BASE);
				dumpfile.write("(");
				dumpfile.write("\'"+sCodeID+"\',");
				dumpfile.write("\'"+CoreUtilities.cleanSQLText(sAuthor, nDatabaseType)+"\',");
				dumpfile.write(String.valueOf(dbCDate)+",");
				dumpfile.write(String.valueOf(dbMDate)+",");
				dumpfile.write("\'"+CoreUtilities.cleanSQLText(sName, nDatabaseType)+"\',");
				dumpfile.write("\'"+CoreUtilities.cleanSQLText(sDesc, nDatabaseType)+"\',");
				dumpfile.write("\'"+CoreUtilities.cleanSQLText(sBehaviour, nDatabaseType)+"\'");
				dumpfile.write(")\n");
			}
			dumpfile.write("\n");
		}
        pstmt1.close();
        rs.close();
	}

	/**
	 * Backup the GroupCode table.
	 *
	 * @param Connection con, the connection object to use to read the data from the database.
	 * @param boolean fullRecreation, indicates if the table drop and create statement should be stored to file.
	 */
	private void backupGroupCodeTable(Connection con, boolean fullRecreation) throws SQLException, IOException {

		if (fullRecreation) {
			if (nDatabaseType == ICoreConstants.DERBY_DATABASE) {
				dumpfile.write(DBConstantsDerby.DROP_GROUPCODE_TABLE+"\n\n");
				dumpfile.write(DBConstantsDerby.CREATE_GROUPCODE_TABLE+"\n\n");
			} else {
				dumpfile.write(DBConstantsMySQL.MYSQL_DROP_GROUPCODE_TABLE+";\n\n");				
				dumpfile.write(DBConstantsMySQL.MYSQL_CREATE_GROUPCODE_TABLE+";\n\n");
			}
		}

		PreparedStatement pstmt1 = con.prepareStatement(GET_GROUPCODE_QUERY);
		ResultSet rs = pstmt1.executeQuery();

		if (rs != null) {

			while (rs.next()) {

				String	sCodeID		= rs.getString(1);
				String	sCodeGroupID= rs.getString(2);
				String	sAuthor		= rs.getString(3);
				long	dbCDate		= rs.getLong(4);
				long	dbMDate		= rs.getLong(5);

				dumpfile.write(INSERT_GROUPCODE_QUERY_BASE);
				dumpfile.write("(");
				dumpfile.write("\'"+sCodeID+"\',");
				dumpfile.write("\'"+sCodeGroupID+"\',");
				dumpfile.write("\'"+CoreUtilities.cleanSQLText(sAuthor, nDatabaseType)+"\',");
				dumpfile.write(String.valueOf(dbCDate)+",");
				dumpfile.write(String.valueOf(dbMDate)+")\n");				
			}
			dumpfile.write("\n");
		}
        pstmt1.close();
        rs.close();
	}

	/**
	 * Backup the CodeGroup table.
	 *
	 * @param Connection con, the connection object to use to read the data from the database.
	 * @param boolean fullRecreation, indicates if the table drop and create statement should be stored to file.
	 */
	private void backupCodeGroupTable(Connection con, boolean fullRecreation) throws SQLException, IOException {

		if (fullRecreation) {
			if (nDatabaseType == ICoreConstants.DERBY_DATABASE) {
				dumpfile.write(DBConstantsDerby.DROP_CODEGROUP_TABLE+"\n\n");				
				dumpfile.write(DBConstantsDerby.CREATE_CODEGROUP_TABLE+"\n\n");
			} else {
				dumpfile.write(DBConstantsMySQL.MYSQL_DROP_CODEGROUP_TABLE+";\n\n");								
				dumpfile.write(DBConstantsMySQL.MYSQL_CREATE_CODEGROUP_TABLE+";\n\n");
			}
		}

		PreparedStatement pstmt1 = con.prepareStatement(GET_CODEGROUP_QUERY);
		ResultSet rs = pstmt1.executeQuery();

		if (rs != null) {

			while (rs.next()) {

				String	sCodeGroupID	= rs.getString(1);
				String	sAuthor			= rs.getString(2);
				String	sName			= rs.getString(3);
				String	sDesc			= rs.getString(4);
				long	dbCDate			= rs.getLong(5);
				long	dbMDate			= rs.getLong(6);

				dumpfile.write(INSERT_CODEGROUP_QUERY_BASE);
				dumpfile.write("(");
				dumpfile.write("\'"+sCodeGroupID+"\',");
				dumpfile.write("\'"+CoreUtilities.cleanSQLText(sAuthor, nDatabaseType)+"\',");
				dumpfile.write("\'"+CoreUtilities.cleanSQLText(sName, nDatabaseType)+"\',");
				dumpfile.write("\'"+CoreUtilities.cleanSQLText(sDesc, nDatabaseType)+"\',");
				dumpfile.write(String.valueOf(dbCDate)+",");
				dumpfile.write(String.valueOf(dbMDate)+")\n");				
			}
			dumpfile.write("\n");
		}
        pstmt1.close();
        rs.close();
	}

	/**
	 * Backup the NodeCode table.
	 *
	 * @param Connection con, the connection object to use to read the data from the database.
	 * @param boolean fullRecreation, indicates if the table drop and create statement should be stored to file.
	 */
	private void backupNodeCodeTable(Connection con, boolean fullRecreation) throws SQLException, IOException {

		if (fullRecreation) {
			if (nDatabaseType == ICoreConstants.DERBY_DATABASE) {
				dumpfile.write(DBConstantsDerby.DROP_NODECODE_TABLE+"\n\n");				
				dumpfile.write(DBConstantsDerby.CREATE_NODECODE_TABLE+"\n\n");
			} else {
				dumpfile.write(DBConstantsMySQL.MYSQL_DROP_NODECODE_TABLE+";\n\n");								
				dumpfile.write(DBConstantsMySQL.MYSQL_CREATE_NODECODE_TABLE+";\n\n");
			}
		}

		PreparedStatement pstmt1 = con.prepareStatement(GET_NODECODE_QUERY);
		ResultSet rs = pstmt1.executeQuery();

		if (rs != null) {

			while (rs.next()) {

				String	sNodeID		= rs.getString(1);
				String	sCodeID		= rs.getString(2);

				dumpfile.write(INSERT_NODECODE_QUERY_BASE);
				dumpfile.write("(");
				dumpfile.write("\'"+sNodeID+"\',");
				dumpfile.write("\'"+sCodeID+"\'");
				dumpfile.write(")\n");
			}
			dumpfile.write("\n");
		}
        pstmt1.close();
        rs.close();
	}

	/**
	 * Backup the ReferenceNode table.
	 *
	 * @param Connection con, the connection object to use to read the data from the database.
	 * @param boolean fullRecreation, indicates if the table drop and create statement should be stored to file.
	 */
	private void backupReferenceNodeTable(Connection con, boolean fullRecreation) throws SQLException, IOException {

		if (fullRecreation) {
			if (nDatabaseType == ICoreConstants.DERBY_DATABASE) {
				dumpfile.write(DBConstantsDerby.DROP_REFERENCE_TABLE+"\n\n");				
				dumpfile.write(DBConstantsDerby.CREATE_REFERENCE_TABLE+"\n\n");
			} else {
				dumpfile.write(DBConstantsMySQL.MYSQL_DROP_REFERENCE_TABLE+";\n\n");								
				dumpfile.write(DBConstantsMySQL.MYSQL_CREATE_REFERENCE_TABLE+";\n\n");
			}
		}

		PreparedStatement pstmt1 = con.prepareStatement(GET_REFERENCE_QUERY);
		ResultSet rs = pstmt1.executeQuery();

		if (rs != null) {

			while (rs.next()) {

				String	sNodeID		= rs.getString(1);
				String	sSource		= rs.getString(2);
				String	sSourceImage= rs.getString(3);
				int		nImageWidth	= rs.getInt(4);
				int		nImageHeight= rs.getInt(5);

				if (bWithResources) {
					if (sSource != null && !sSource.equals("") && CoreUtilities.isFile(sSource)) {
						File file = new File(sSource);
						if (file.exists()) {
							String sOldSource = sSource;
							if (!bKeepPaths)
								sSource = sBackupPath + "/" + file.getName();
							else {
								sSource = CoreUtilities.unixPath(sSource);
							}

							if (!htResources.containsKey(sOldSource)) {
								htResources.put(sOldSource, sSource);
							}
						}
						else if (sSource != null && !sSource.equals("")) {
							bNotFound = true;
							System.out.println("NOT FOUND ON EXPORT: "+sSource);
						}
					}
					if (sSourceImage != null && !sSourceImage.equals("") && CoreUtilities.isFile(sSourceImage)) {
						File file2 = new File(sSourceImage);
						if (file2.exists()) {
							String sOldSourceImage = sSourceImage;
							if (!bKeepPaths)
								sSourceImage = sBackupPath + "/" + file2.getName();
							else {
								sSourceImage = CoreUtilities.unixPath(sSourceImage);
							}
							
							if (!htResources.containsKey(sOldSourceImage)) {
								htResources.put(sOldSourceImage, sSourceImage);
							}
						}
						else if (sSourceImage != null && !sSourceImage.equals("")) {
							bNotFound = true;
							System.out.println("NOT FOUND ON EXPORT: "+sSourceImage);
						}
					}
				}

				dumpfile.write(INSERT_REFERENCE_QUERY_BASE);
				dumpfile.write("(");
				dumpfile.write("\'"+sNodeID+"\',");
				dumpfile.write("\'"+CoreUtilities.cleanSQLText(sSource, nDatabaseType)+"\',");
				dumpfile.write("\'"+CoreUtilities.cleanSQLText(sSourceImage, nDatabaseType)+"\',");
				dumpfile.write(String.valueOf(nImageWidth)+",");
				dumpfile.write(String.valueOf(nImageHeight));
				dumpfile.write(")\n");
			}
			dumpfile.write("\n");
		}
        pstmt1.close();
        rs.close();
	}

	/**
	 * Backup the ViewNode table.
	 *
	 * @param Connection con, the connection object to use to read the data from the database.
	 * @param boolean fullRecreation, indicates if the table drop and create statement should be stored to file.
	 */
	private void backupViewNodeTable(Connection con, boolean fullRecreation) throws SQLException, IOException {

		if (fullRecreation) {
			if (nDatabaseType == ICoreConstants.DERBY_DATABASE) {
				dumpfile.write(DBConstantsDerby.DROP_VIEWNODE_TABLE+"\n\n");				
				dumpfile.write(DBConstantsDerby.CREATE_VIEWNODE_TABLE+"\n\n");
			} else {
				dumpfile.write(DBConstantsMySQL.MYSQL_DROP_VIEWNODE_TABLE+";\n\n");								
				dumpfile.write(DBConstantsMySQL.MYSQL_CREATE_VIEWNODE_TABLE+";\n\n");
			}
		}

		String query  = GET_SPECIFIC_VIEWNODE_QUERY;
		if (!bIncludeTrash) {
			query += " Where CurrentStatus != 3";
		}

		PreparedStatement pstmt1 = con.prepareStatement(query);
		ResultSet rs = pstmt1.executeQuery();

		if (rs != null) {

			while (rs.next()) {

				String	sViewID		= rs.getString(1);
				String	sNodeID		= rs.getString(2);
				int		nXPos		= rs.getInt(3);
				int		nYPos		= rs.getInt(4);
				long	dbCDate		= rs.getLong(5);
				long	dbMDate		= rs.getLong(6);
				int		nStatus		= rs.getInt(7);
				String	sShowTags	= rs.getString(8);
				String	sShowText	= rs.getString(9);
				String	sShowTrans	= rs.getString(10);
				String	sShowWeight	= rs.getString(11);
				String	sSmallIcons	= rs.getString(12);
				String	sHideIcons	= rs.getString(13);
				int		nLabelWidth	= rs.getInt(14);
				int		nFontSize	= rs.getInt(15);
				String	sFontFace	= rs.getString(16);
				int		nFontStyle	= rs.getInt(17);
				int		nForeground = rs.getInt(18);
				int 	nBackground = rs.getInt(19);

				dumpfile.write(INSERT_VIEWNODE_QUERY_BASE);
				dumpfile.write("(");
				dumpfile.write("\'"+sViewID+"\',");
				dumpfile.write("\'"+sNodeID+"\',");
				dumpfile.write(String.valueOf(nXPos)+",");
				dumpfile.write(String.valueOf(nYPos)+",");
				dumpfile.write(String.valueOf(dbCDate)+",");
				dumpfile.write(String.valueOf(dbMDate)+",");
				dumpfile.write(String.valueOf(nStatus)+",");
				dumpfile.write("\'"+sShowTags+"\',");
				dumpfile.write("\'"+sShowText+"\',");
				dumpfile.write("\'"+sShowTrans+"\',");
				dumpfile.write("\'"+sShowWeight+"\',");
				dumpfile.write("\'"+sSmallIcons+"\',");
				dumpfile.write("\'"+sHideIcons+"\',");
				dumpfile.write(String.valueOf(nLabelWidth)+",");
				dumpfile.write(String.valueOf(nFontSize)+",");
				dumpfile.write("\'"+CoreUtilities.cleanSQLText(sFontFace, nDatabaseType)+"\',");
				dumpfile.write(String.valueOf(nFontStyle)+",");
				dumpfile.write(String.valueOf(nForeground)+",");
				dumpfile.write(String.valueOf(nBackground));
								
				dumpfile.write(")\n");
			}
			dumpfile.write("\n");
		}
        pstmt1.close();
        rs.close();
	}

	/**
	 * Backup the ShortCutNode table.
	 *
	 * @param Connection con, the connection object to use to read the data from the database.
	 * @param boolean fullRecreation, indicates if the table drop and create statement should be stored to file.
	 */
	private void backupShortCutNodeTable(Connection con, boolean fullRecreation) throws SQLException, IOException {

		if (fullRecreation) {
			if (nDatabaseType == ICoreConstants.DERBY_DATABASE) {
				dumpfile.write(DBConstantsDerby.DROP_SHORTCUT_TABLE+"\n\n");				
				dumpfile.write(DBConstantsDerby.CREATE_SHORTCUT_TABLE+"\n\n");
			} else {
				dumpfile.write(DBConstantsMySQL.MYSQL_DROP_SHORTCUT_TABLE+";\n\n");								
				dumpfile.write(DBConstantsMySQL.MYSQL_CREATE_SHORTCUT_TABLE+";\n\n");
			}
		}

		PreparedStatement pstmt1 = con.prepareStatement(GET_SHORTCUT_QUERY);
		ResultSet rs = pstmt1.executeQuery();

		if (rs != null) {

			while (rs.next()) {

				String	sNodeID		= rs.getString(1);
				String	sReferenceID= rs.getString(2);

				dumpfile.write(INSERT_SHORTCUT_QUERY_BASE);
				dumpfile.write("(");
				dumpfile.write("\'"+sNodeID+"\',");
				dumpfile.write("\'"+sReferenceID+"\'");
				dumpfile.write(")\n");
			}
			dumpfile.write("\n");
		}
        pstmt1.close();
        rs.close();
	}

	/**
	 * Backup the NodeDetail table.
	 *
	 * @param Connection con, the connection object to use to read the data from the database.
	 * @param boolean fullRecreation, indicates if the table drop and create statement should be stored to file.
	 */
	private void backupNodeDetailTable(Connection con, boolean fullRecreation) throws SQLException, IOException {

		if (fullRecreation) {
			if (nDatabaseType == ICoreConstants.DERBY_DATABASE) {
				dumpfile.write(DBConstantsDerby.DROP_NODEDETAIL_TABLE+"\n\n");				
				dumpfile.write(DBConstantsDerby.CREATE_NODEDETAIL_TABLE+"\n\n");
			} else {
				dumpfile.write(DBConstantsMySQL.MYSQL_DROP_NODEDETAIL_TABLE+";\n\n");								
				dumpfile.write(DBConstantsMySQL.MYSQL_CREATE_NODEDETAIL_TABLE+";\n\n");
			}
		}

		PreparedStatement pstmt1 = con.prepareStatement(GET_NODEDETAIL_QUERY);
		ResultSet rs = pstmt1.executeQuery();

		if (rs != null) {

			while (rs.next()) {

				String	sNodeID			= rs.getString(1);
				String	sAuthor			= rs.getString(2);
				int		nPage			= rs.getInt(3);
				long	dbCDate			= rs.getLong(4);
				long	dbMDate			= rs.getLong(5);
				String	sDetail			= rs.getString(6);

				dumpfile.write(INSERT_NODEDETAIL_QUERY_BASE);
				dumpfile.write("(");
				dumpfile.write("\'"+sNodeID+"\',");
				dumpfile.write("\'"+CoreUtilities.cleanSQLText(sAuthor, nDatabaseType)+"\',");
				dumpfile.write(String.valueOf(nPage)+",");
				dumpfile.write(String.valueOf(dbCDate)+",");
				dumpfile.write(String.valueOf(dbMDate)+",");
				dumpfile.write("\'"+CoreUtilities.cleanSQLText(sDetail, nDatabaseType)+"\'");
				dumpfile.write(")\n");
			}
			dumpfile.write("\n");
		}
        pstmt1.close();
        rs.close();
	}

	/**
	 * Backup the ViewProperty table.
	 *
	 * @param Connection con, the connection object to use to read the data from the database.
	 * @param boolean fullRecreation, indicates if the table drop and create statement should be stored to file.
	 */
	private void backupViewPropertyTable(Connection con, boolean fullRecreation) throws SQLException, IOException {

		if (fullRecreation) {
			if (nDatabaseType == ICoreConstants.DERBY_DATABASE) {
				dumpfile.write(DBConstantsDerby.DROP_VIEWPROPERTY_TABLE+"\n\n");				
				dumpfile.write(DBConstantsDerby.CREATE_VIEWPROPERTY_TABLE+"\n\n");
			} else {
				dumpfile.write(DBConstantsMySQL.MYSQL_DROP_VIEWPROPERTY_TABLE+";\n\n");								
				dumpfile.write(DBConstantsMySQL.MYSQL_CREATE_VIEWPROPERTY_TABLE+";\n\n");
			}
		}

		PreparedStatement pstmt1 = con.prepareStatement(GET_VIEWPROPERTY_QUERY);
		ResultSet rs = pstmt1.executeQuery();

		if (rs != null) {

			while (rs.next()) {

				String	sUserID				= rs.getString(1);
				String	sViewID				= rs.getString(2);
				int		nHorizontalScroll	= rs.getInt(3);
				int		nVerticalScroll		= rs.getInt(4);
				int		nWidth				= rs.getInt(5);
				int		nHeight				= rs.getInt(6);
				int		nXPos				= rs.getInt(7);
				int		nYPos				= rs.getInt(8);
				String  sIcon	 			= rs.getString(9);
				String  sMax 				= rs.getString(10);
				String	sShowTags			= rs.getString(11);
				String	sShowText			= rs.getString(12);
				String	sShowTrans			= rs.getString(13);
				String	sShowWeight			= rs.getString(14);
				String	sSmallIcons			= rs.getString(15);
				String	sHideIcons			= rs.getString(16);
				int		nLabelLength		= rs.getInt(17);
				int		nLabelWidth			= rs.getInt(18);
				int		nFontSize			= rs.getInt(19);
				String	sFontFace			= rs.getString(20);
				int		nFontStyle			= rs.getInt(21);

				dumpfile.write(INSERT_VIEWPROPERTY_QUERY_BASE);
				dumpfile.write("(");
				dumpfile.write("\'"+sUserID+"\',");
				dumpfile.write("\'"+sViewID+"\',");
				dumpfile.write(String.valueOf(nHorizontalScroll)+",");
				dumpfile.write(String.valueOf(nVerticalScroll)+",");
				dumpfile.write(String.valueOf(nWidth)+",");
				dumpfile.write(String.valueOf(nHeight)+",");
				dumpfile.write(String.valueOf(nXPos)+",");
				dumpfile.write(String.valueOf(nYPos)+",");
				dumpfile.write("\'"+sIcon+"\',");
				dumpfile.write("\'"+sMax+"\',");
				dumpfile.write("\'"+sShowTags+"\',");
				dumpfile.write("\'"+sShowText+"\',");
				dumpfile.write("\'"+sShowTrans+"\',");
				dumpfile.write("\'"+sShowWeight+"\',");
				dumpfile.write("\'"+sSmallIcons+"\',");
				dumpfile.write("\'"+sHideIcons+"\',");
				dumpfile.write(String.valueOf(nLabelLength)+",");
				dumpfile.write(String.valueOf(nLabelWidth)+",");
				dumpfile.write(String.valueOf(nFontSize)+",");
				dumpfile.write("\'"+CoreUtilities.cleanSQLText(sFontFace, nDatabaseType)+"\',");
				dumpfile.write(String.valueOf(nFontStyle));
				dumpfile.write(")\n");
			}
			dumpfile.write("\n");
		}
        pstmt1.close();
        rs.close();
	}

	/**
	 * Backup the Favorite table.
	 *
	 * @param Connection con, the connection object to use to read the data from the database.
	 * @param boolean fullRecreation, indicates if the table drop and create statement should be stored to file.
	 */
	private void backupFavoriteTable(Connection con, boolean fullRecreation) throws SQLException, IOException {

		if (fullRecreation) {
			if (nDatabaseType == ICoreConstants.DERBY_DATABASE) {
				dumpfile.write(DBConstantsDerby.DROP_FAVORITE_TABLE+"\n\n");				
				dumpfile.write(DBConstantsDerby.CREATE_FAVORITE_TABLE+"\n\n");
			} else {
				dumpfile.write(DBConstantsMySQL.MYSQL_DROP_FAVORITE_TABLE+";\n\n");								
				dumpfile.write(DBConstantsMySQL.MYSQL_CREATE_FAVORITE_TABLE+";\n\n");
			}
		}

		PreparedStatement pstmt1 = con.prepareStatement(GET_FAVORITE_QUERY);
		ResultSet rs = pstmt1.executeQuery();

		if (rs != null) {
			while (rs.next()) {

				String	sUserID		= rs.getString(1);
				String	sNodeID		= rs.getString(2);
				String	sLabel		= rs.getString(3);
				int		nType		= rs.getInt(4);
				long	dbCDate		= rs.getLong(5);
				long	dbMDate		= rs.getLong(6);
				String	sViewID		= rs.getString(7);				

				dumpfile.write(INSERT_FAVORITE_QUERY_BASE);
				dumpfile.write("(");
				dumpfile.write("\'"+sUserID+"\',");
				dumpfile.write("\'"+sNodeID+"\',");
				dumpfile.write("\'"+CoreUtilities.cleanSQLText(sLabel, nDatabaseType)+"\',");
				dumpfile.write(String.valueOf(nType)+",");
				dumpfile.write(String.valueOf(dbCDate)+",");
				dumpfile.write(String.valueOf(dbMDate)+",");
				dumpfile.write("\'"+sViewID+"\'");				
				dumpfile.write(")\n");
			}
			dumpfile.write("\n");
		}
        pstmt1.close();
        rs.close();
	}

	/**
	 * Backup the Workspace table.
	 *
	 * @param Connection con, the connection object to use to read the data from the database.
	 * @param boolean fullRecreation, indicates if the table drop and create statement should be stored to file.
	 */
	private void backupWorkspaceTable(Connection con, boolean fullRecreation) throws SQLException, IOException {

		if (fullRecreation) {
			if (nDatabaseType == ICoreConstants.DERBY_DATABASE) {
				dumpfile.write(DBConstantsDerby.DROP_WORKSPACE_TABLE+"\n\n");				
				dumpfile.write(DBConstantsDerby.CREATE_WORKSPACE_TABLE+"\n\n");
			} else {
				dumpfile.write(DBConstantsMySQL.MYSQL_DROP_WORKSPACE_TABLE+";\n\n");								
				dumpfile.write(DBConstantsMySQL.MYSQL_CREATE_WORKSPACE_TABLE+";\n\n");
			}
		}

		PreparedStatement pstmt1 = con.prepareStatement(GET_WORKSPACE_QUERY);
		ResultSet rs = pstmt1.executeQuery();

		if (rs != null) {
			while (rs.next()) {

				String	sWorkspaceID	= rs.getString(1);
				String	sUserID			= rs.getString(2);
				String	sName			= rs.getString(3);
				long	dbCDate			= rs.getLong(4);
				long	dbMDate			= rs.getLong(5);

				dumpfile.write(INSERT_WORKSPACE_QUERY_BASE);
				dumpfile.write("(");
				dumpfile.write("\'"+sWorkspaceID+"\',");
				dumpfile.write("\'"+sUserID+"\',");
				dumpfile.write("\'"+CoreUtilities.cleanSQLText(sName, nDatabaseType)+"\',");
				dumpfile.write(String.valueOf(dbCDate)+",");
				dumpfile.write(String.valueOf(dbMDate)+")\n");				
			}
			dumpfile.write("\n");
		}
        pstmt1.close();
        rs.close();
	}

	/**
	 * Backup the WorkspaceView table.
	 *
	 * @param Connection con, the connection object to use to read the data from the database.
	 * @param boolean fullRecreation, indicates if the table drop and create statement should be stored to file.
	 */
	private void backupWorkspaceViewTable(Connection con, boolean fullRecreation) throws SQLException, IOException {

		if (fullRecreation) {
			if (nDatabaseType == ICoreConstants.DERBY_DATABASE) {
				dumpfile.write(DBConstantsDerby.DROP_WORKSPACEVIEW_TABLE+"\n\n");				
				dumpfile.write(DBConstantsDerby.CREATE_WORKSPACEVIEW_TABLE+"\n\n");
			} else {
				dumpfile.write(DBConstantsMySQL.MYSQL_DROP_WORKSPACEVIEW_TABLE+";\n\n");								
				dumpfile.write(DBConstantsMySQL.MYSQL_CREATE_WORKSPACEVIEW_TABLE+";\n\n");
			}
		}

		PreparedStatement pstmt1 = con.prepareStatement(GET_WORKSPACEVIEW_QUERY);
		ResultSet rs = pstmt1.executeQuery();

		if (rs != null) {
			while (rs.next()) {

				String	sWorkspaceID		= rs.getString(1);
				String	sViewID				= rs.getString(2);
				int		nHorizontalScroll	= rs.getInt(3);
				int		nVerticalScroll		= rs.getInt(4);
				int		nWidth				= rs.getInt(5);
				int		nHeight				= rs.getInt(6);
				int		nXPos				= rs.getInt(7);
				int		nYPos				= rs.getInt(8);
				String 	sIcon		 		= rs.getString(9);
				String 	sMax 				= rs.getString(10);

				dumpfile.write(INSERT_WORKSPACEVIEW_QUERY_BASE);
				dumpfile.write("(");
				dumpfile.write("\'"+sWorkspaceID+"\',");
				dumpfile.write("\'"+sViewID+"\',");
				dumpfile.write(String.valueOf(nHorizontalScroll)+",");
				dumpfile.write(String.valueOf(nVerticalScroll)+",");
				dumpfile.write(String.valueOf(nWidth)+",");
				dumpfile.write(String.valueOf(nHeight)+",");
				dumpfile.write(String.valueOf(nXPos)+",");
				dumpfile.write(String.valueOf(nYPos)+",");
				dumpfile.write("\'"+sIcon+"\',");
				dumpfile.write("\'"+sMax+"\'");
				dumpfile.write(")\n");
			}
			dumpfile.write("\n");
		}
        pstmt1.close();
        rs.close();
	}

	/**
	 * Backup the ViewLink table.
	 *
	 * @param Connection con, the connection object to use to read the data from the database.
	 * @param boolean fullRecreation, indicates if the table drop and create statement should be stored to file.
	 */
	private void backupViewLinkTable(Connection con, boolean fullRecreation) throws SQLException, IOException {

		if (fullRecreation) {
			if (nDatabaseType == ICoreConstants.DERBY_DATABASE) {
				dumpfile.write(DBConstantsDerby.DROP_VIEWLINK_TABLE+"\n\n");				
				dumpfile.write(DBConstantsDerby.CREATE_VIEWLINK_TABLE+"\n\n");
			} else {
				dumpfile.write(DBConstantsMySQL.MYSQL_DROP_VIEWLINK_TABLE+";\n\n");								
				dumpfile.write(DBConstantsMySQL.MYSQL_CREATE_VIEWLINK_TABLE+";\n\n");
			}
		}

		String query  = GET_VIEWLINK_QUERY;
		if (!bIncludeTrash) {
			query += " Where CurrentStatus != 3";
		}

		PreparedStatement pstmt1 = con.prepareStatement(query);
		ResultSet rs = pstmt1.executeQuery();

		if (rs != null) {
			while (rs.next()) {
				
				String	sViewID		= rs.getString(1);
				String	sLinkID		= rs.getString(2);
				long	dbCDate		= rs.getLong(3);
				long	dbMDate		= rs.getLong(4);
				int		nStatus		= rs.getInt(5);
				int		nLabelWidth	= rs.getInt(6);
				int 	nArrowStyle = rs.getInt(7);
				int 	nLinkStyle 	= rs.getInt(8);
				int 	nLinkDashed = rs.getInt(9);
				int 	nLinkWeight = rs.getInt(10);
				int 	nLinkColour = rs.getInt(11);
				int		nFontSize	= rs.getInt(12);
				String	sFontFace	= rs.getString(13);
				int		nFontStyle	= rs.getInt(14);
				int		nForeground = rs.getInt(15);
				int 	nBackground = rs.getInt(16);

				dumpfile.write(INSERT_VIEWLINK_QUERY_BASE);
				dumpfile.write("(");
				dumpfile.write("\'"+sViewID+"\',");
				dumpfile.write("\'"+sLinkID+"\',");
				dumpfile.write(String.valueOf(dbCDate)+",");
				dumpfile.write(String.valueOf(dbMDate)+",");
				dumpfile.write(String.valueOf(nStatus)+",");
				dumpfile.write(String.valueOf(nLabelWidth)+",");
				dumpfile.write(String.valueOf(nArrowStyle)+",");
				dumpfile.write(String.valueOf(nLinkStyle)+",");
				dumpfile.write(String.valueOf(nLinkDashed)+",");
				dumpfile.write(String.valueOf(nLinkWeight)+",");
				dumpfile.write(String.valueOf(nLinkColour)+",");
				dumpfile.write(String.valueOf(nFontSize)+",");
				dumpfile.write("\'"+CoreUtilities.cleanSQLText(sFontFace, nDatabaseType)+"\',");
				dumpfile.write(String.valueOf(nFontStyle)+",");
				dumpfile.write(String.valueOf(nForeground)+",");
				dumpfile.write(String.valueOf(nBackground));
				dumpfile.write(")\n");
			}
			dumpfile.write("\n");
		}
        pstmt1.close();
        rs.close();
	}

	/**
	 * Backup the NodeUserState table.
	 *
	 * @param Connection con, the connection object to use to read the data from the database.
	 * @param boolean fullRecreation, indicates if the table drop and create statement should be stored to file.
	 */
	private void backupNodeUserStateTable(Connection con, boolean fullRecreation) throws SQLException, IOException {

		if (fullRecreation) {
			if (nDatabaseType == ICoreConstants.DERBY_DATABASE) {
				dumpfile.write(DBConstantsDerby.DROP_NODEUSERSTATE_TABLE+"\n\n");				
				dumpfile.write(DBConstantsDerby.CREATE_NODEUSERSTATE_TABLE+"\n\n");
			} else {
				dumpfile.write(DBConstantsMySQL.MYSQL_DROP_NODEUSERSTATE_TABLE+";\n\n");								
				dumpfile.write(DBConstantsMySQL.MYSQL_CREATE_NODEUSERSTATE_TABLE+";\n\n");
			}
		}

		PreparedStatement pstmt1 = con.prepareStatement(GET_NODEUSERSTATE_QUERY);
		ResultSet rs = pstmt1.executeQuery();

		
		if (rs != null) {
			while (rs.next()) {

				String	sNodeID		= rs.getString(1);
				String	sUserID		= rs.getString(2);
				int		nState		= rs.getInt(3);

				dumpfile.write(INSERT_NODEUSERSTATE_QUERY_BASE);
				dumpfile.write("(");
				dumpfile.write("\'"+sNodeID+"\',");
				dumpfile.write("\'"+sUserID+"\',");
				dumpfile.write(String.valueOf(nState));
				dumpfile.write(")\n");
			}
			dumpfile.write("\n");
		}
        pstmt1.close();
        rs.close();
	}

	/**
	 * Backup the Audit table.
	 *
	 * @param Connection con, the connection object to use to read the data from the database.
	 * @param boolean fullRecreation, indicates if the table drop and create statement should be stored to file.
	 */
	private void backupAuditTable(Connection con, boolean fullRecreation) throws SQLException, IOException {

		if (fullRecreation) {
			if (nDatabaseType == ICoreConstants.DERBY_DATABASE) {
				dumpfile.write(DBConstantsDerby.DROP_AUDIT_TABLE+"\n\n");				
				dumpfile.write(DBConstantsDerby.CREATE_AUDIT_TABLE+"\n\n");
			} else {
				dumpfile.write(DBConstantsMySQL.MYSQL_DROP_AUDIT_TABLE+";\n\n");								
				dumpfile.write(DBConstantsMySQL.MYSQL_CREATE_AUDIT_TABLE+";\n\n");
			}
		}

		PreparedStatement pstmt1 = con.prepareStatement(GET_AUDIT_QUERY);
		ResultSet rs = pstmt1.executeQuery();

		if (rs != null) {
			while (rs.next()) {

				String	sAuditID	= rs.getString(1);
				String	sAuthor		= rs.getString(2);
				String	sItemID		= rs.getString(3);
				long	dbDate		= rs.getLong(4);
				String	sCategory	= rs.getString(5);
				int		nAction		= rs.getInt(6);
				String	sData		= rs.getString(7);

				dumpfile.write(INSERT_AUDIT_QUERY_BASE);
				dumpfile.write("(");
				dumpfile.write("\'"+sAuditID+"\',");
				dumpfile.write("\'"+CoreUtilities.cleanSQLText(sAuthor, nDatabaseType)+"\',");
				dumpfile.write("\'"+sItemID+"\',");
				dumpfile.write(String.valueOf(dbDate)+",");
				dumpfile.write("\'"+sCategory+"\',");
				dumpfile.write(String.valueOf(nAction)+",");
				dumpfile.write("\'"+CoreUtilities.cleanSQLText(sData, nDatabaseType)+"\'");
				dumpfile.write(")\n");
			}
			dumpfile.write("\n");
		}
        pstmt1.close();
        rs.close();
	}

	/**
	 * Backup the Permission table.
	 *
	 * @param Connection con, the connection object to use to read the data from the database.
	 * @param boolean fullRecreation, indicates if the table drop and create statement should be stored to file.
	 */
	private void backupPermissionTable(Connection con, boolean fullRecreation) throws SQLException, IOException {

		if (fullRecreation) {
			if (nDatabaseType == ICoreConstants.DERBY_DATABASE) {
				dumpfile.write(DBConstantsDerby.DROP_PERMISSION_TABLE+"\n\n");				
				dumpfile.write(DBConstantsDerby.CREATE_PERMISSION_TABLE+"\n\n");
			} else {
				dumpfile.write(DBConstantsMySQL.MYSQL_DROP_PERMISSION_TABLE+";\n\n");								
				dumpfile.write(DBConstantsMySQL.MYSQL_CREATE_PERMISSION_TABLE+";\n\n");
			}
		}

		PreparedStatement pstmt1 = con.prepareStatement(GET_PERMISSION_QUERY);
		ResultSet rs = pstmt1.executeQuery();

		if (rs != null) {
			while (rs.next()) {

				String	sItemID		= rs.getString(1);
				String	sGroupID	= rs.getString(2);
				int	nPermission		= rs.getInt(3);

				dumpfile.write(INSERT_PERMISSION_QUERY_BASE);
				dumpfile.write("(");
				dumpfile.write("\'"+sItemID+"\',");
				dumpfile.write("\'"+sGroupID+"\',");
				dumpfile.write(String.valueOf(nPermission));
				dumpfile.write(")\n");
			}
			dumpfile.write("\n");
		}
        pstmt1.close();
        rs.close();
	}

	/**
	 * Backup the Clone table.
	 *
	 * @param Connection con, the connection object to use to read the data from the database.
	 * @param boolean fullRecreation, indicates if the table drop and create statement should be stored to file.
	 */
	private void backupCloneTable(Connection con, boolean fullRecreation) throws SQLException, IOException {

		if (fullRecreation) {
			if (nDatabaseType == ICoreConstants.DERBY_DATABASE) {
				dumpfile.write(DBConstantsDerby.DROP_CLONE_TABLE+"\n\n");				
				dumpfile.write(DBConstantsDerby.CREATE_CLONE_TABLE+"\n\n");
			} else {
				dumpfile.write(DBConstantsMySQL.MYSQL_DROP_CLONE_TABLE+";\n\n");								
				dumpfile.write(DBConstantsMySQL.MYSQL_CREATE_CLONE_TABLE+";\n\n");
			}
		}

		PreparedStatement pstmt1 = con.prepareStatement(GET_CLONE_QUERY);
		ResultSet rs = pstmt1.executeQuery();

		if (rs != null) {
			while (rs.next()) {

				String	sParentNodeID	= rs.getString(1);
				String	sChildNodeID	= rs.getString(2);

				dumpfile.write(INSERT_CLONE_QUERY_BASE);
				dumpfile.write("(");
				dumpfile.write("\'"+sParentNodeID+"\',");
				dumpfile.write("\'"+sChildNodeID+"\'");
				dumpfile.write(")\n");
			}
			dumpfile.write("\n");
		}
        pstmt1.close();
        rs.close();
	}

	/**
	 * Backup the ExtendedNode table.
	 *
	 * @param Connection con, the connection object to use to read the data from the database.
	 * @param boolean fullRecreation, indicates if the table drop and create statement should be stored to file.
	 */
	private void backupExtendedNodeTable(Connection con, boolean fullRecreation) throws SQLException, IOException {

		if (fullRecreation) {
			if (nDatabaseType == ICoreConstants.DERBY_DATABASE) {
				dumpfile.write(DBConstantsDerby.DROP_EXTENDEDNODE_TABLE+"\n\n");				
				dumpfile.write(DBConstantsDerby.CREATE_EXTENDEDNODE_TABLE+"\n\n");
			} else {
				dumpfile.write(DBConstantsMySQL.MYSQL_DROP_EXTENDEDNODE_TABLE+";\n\n");								
				dumpfile.write(DBConstantsMySQL.MYSQL_CREATE_EXTENDEDNODE_TABLE+";\n\n");
			}
		}

		PreparedStatement pstmt1 = con.prepareStatement(GET_EXTENDEDNODE_QUERY);
		ResultSet rs = pstmt1.executeQuery();

		if (rs != null) {
			while (rs.next()) {

				String	sExtendedNodeTypeID	= rs.getString(1);
				String	sAuthor			= rs.getString(2);
				long	dbCDate			= rs.getLong(3);
				long	dbMDate			= rs.getLong(4);
				String	sName			= rs.getString(5);
				String	sDesc			= rs.getString(6);
				int		nBaseNodeType	= rs.getInt(7);
				String	sIcon			= rs.getString(8);

				dumpfile.write(INSERT_EXTENDEDNODE_QUERY_BASE);
				dumpfile.write("(");
				dumpfile.write("\'"+sExtendedNodeTypeID+"\',");
				dumpfile.write("\'"+CoreUtilities.cleanSQLText(sAuthor, nDatabaseType)+"\',");
				dumpfile.write(String.valueOf(dbCDate)+",");
				dumpfile.write(String.valueOf(dbMDate)+",");
				dumpfile.write("\'"+CoreUtilities.cleanSQLText(sName, nDatabaseType)+"\',");
				dumpfile.write("\'"+CoreUtilities.cleanSQLText(sDesc, nDatabaseType)+"\',");
				dumpfile.write(String.valueOf(nBaseNodeType)+",");
				dumpfile.write("\'"+CoreUtilities.cleanSQLText(sIcon, nDatabaseType)+"\'");
				dumpfile.write(")\n");
			}
			dumpfile.write("\n");
		}
        pstmt1.close();
        rs.close();
	}

	/**
	 * Backup the ExtendedCode table.
	 *
	 * @param Connection con, the connection object to use to read the data from the database.
	 * @param boolean fullRecreation, indicates if the table drop and create statement should be stored to file.
	 */
	private void backupExtendedCodeTable(Connection con, boolean fullRecreation) throws SQLException, IOException {

		if (fullRecreation) {
			if (nDatabaseType == ICoreConstants.DERBY_DATABASE) {
				dumpfile.write(DBConstantsDerby.DROP_EXTENDEDCODE_TABLE+"\n\n");				
				dumpfile.write(DBConstantsDerby.CREATE_EXTENDEDCODE_TABLE+"\n\n");
			} else {
				dumpfile.write(DBConstantsMySQL.MYSQL_DROP_EXTENDEDCODE_TABLE+";\n\n");								
				dumpfile.write(DBConstantsMySQL.MYSQL_CREATE_EXTENDEDCODE_TABLE+";\n\n");
			}
		}

		PreparedStatement pstmt1 = con.prepareStatement(GET_EXTENDEDCODE_QUERY);
		ResultSet rs = pstmt1.executeQuery();

		if (rs != null) {
			while (rs.next()) {

				String	sExtendedNodeTypeID	= rs.getString(1);
				String	sCodeID	= rs.getString(2);

				dumpfile.write(INSERT_EXTENDEDCODE_QUERY_BASE);
				dumpfile.write("(");
				dumpfile.write("\'"+sExtendedNodeTypeID+"\',");
				dumpfile.write("\'"+sCodeID+"\'");
				dumpfile.write(")\n");
			}
			dumpfile.write("\n");
		}
        pstmt1.close();
        rs.close();
	}

	/**
	 * Backup the UserGroup table.
	 *
	 * @param Connection con, the connection object to use to read the data from the database.
	 * @param boolean fullRecreation, indicates if the table drop and create statement should be stored to file.
	 */
	private void backupUserGroupTable(Connection con, boolean fullRecreation) throws SQLException, IOException {

		if (fullRecreation) {
			if (nDatabaseType == ICoreConstants.DERBY_DATABASE) {
				dumpfile.write(DBConstantsDerby.DROP_USERGROUP_TABLE+"\n\n");				
				dumpfile.write(DBConstantsDerby.CREATE_USERGROUP_TABLE+"\n\n");
			} else {
				dumpfile.write(DBConstantsMySQL.MYSQL_DROP_USERGROUP_TABLE+";\n\n");								
				dumpfile.write(DBConstantsMySQL.MYSQL_CREATE_USERGROUP_TABLE+";\n\n");
			}
		}

		PreparedStatement pstmt1 = con.prepareStatement(GET_USERGROUP_QUERY);
		ResultSet rs = pstmt1.executeQuery();

		if (rs != null) {
			while (rs.next()) {

				String	sGroupID	= rs.getString(1);
				String	sUserID		= rs.getString(2);
				long	dbCDate		= rs.getLong(3);
				long	dbMDate		= rs.getLong(4);
				String	sName		= rs.getString(5);
				String	sDesc		= rs.getString(6);

				dumpfile.write(INSERT_USERGROUP_QUERY_BASE);
				dumpfile.write("(");
				dumpfile.write("\'"+sGroupID+"\',");
				dumpfile.write("\'"+sUserID+"\',");
				dumpfile.write(String.valueOf(dbCDate)+",");
				dumpfile.write(String.valueOf(dbMDate)+",");
				dumpfile.write("\'"+CoreUtilities.cleanSQLText(sName, nDatabaseType)+"\',");
				dumpfile.write("\'"+CoreUtilities.cleanSQLText(sDesc, nDatabaseType)+"\'");
				dumpfile.write(")\n");
			}
			dumpfile.write("\n");
		}
        pstmt1.close();
        rs.close();
	}

	/**
	 * Backup the GroupUser table.
	 *
	 * @param Connection con, the connection object to use to read the data from the database.
	 * @param boolean fullRecreation, indicates if the table drop and create statement should be stored to file.
	 */
	private void backupGroupUserTable(Connection con, boolean fullRecreation) throws SQLException, IOException {

		if (fullRecreation) {
			if (nDatabaseType == ICoreConstants.DERBY_DATABASE) {
				dumpfile.write(DBConstantsDerby.DROP_GROUPUSER_TABLE+"\n\n");				
				dumpfile.write(DBConstantsDerby.CREATE_GROUPUSER_TABLE+"\n\n");
			} else {
				dumpfile.write(DBConstantsMySQL.MYSQL_DROP_GROUPUSER_TABLE+";\n\n");								
				dumpfile.write(DBConstantsMySQL.MYSQL_CREATE_GROUPUSER_TABLE+";\n\n");
			}
		}

		PreparedStatement pstmt1 = con.prepareStatement(GET_GROUPUSER_QUERY);
		ResultSet rs = pstmt1.executeQuery();

		if (rs != null) {
			while (rs.next()) {

				String	sUserID		= rs.getString(1);
				String	sGroupID	= rs.getString(2);

				dumpfile.write(INSERT_GROUPUSER_QUERY_BASE);
				dumpfile.write("(");
				dumpfile.write("\'"+sUserID+"\',");
				dumpfile.write("\'"+sGroupID+"\'");
				dumpfile.write(")\n");
			}
			dumpfile.write("\n");
		}
        pstmt1.close();
        rs.close();
	}

	/**
	 * Backup the ViewLayer table.
	 *
	 * @param Connection con, the connection object to use to read the data from the database.
	 * @param boolean fullRecreation, indicates if the table drop and create statement should be stored to file.
	 */
	private void backupViewLayerTable(Connection con, boolean fullRecreation) throws SQLException, IOException {

		if (fullRecreation) {
			if (nDatabaseType == ICoreConstants.DERBY_DATABASE) {
				dumpfile.write(DBConstantsDerby.DROP_VIEWLAYER_TABLE+"\n\n");				
				dumpfile.write(DBConstantsDerby.CREATE_VIEWLAYER_TABLE+"\n\n");
			} else {
				dumpfile.write(DBConstantsMySQL.MYSQL_DROP_VIEWLAYER_TABLE+";\n\n");								
				dumpfile.write(DBConstantsMySQL.MYSQL_CREATE_VIEWLAYER_TABLE+";\n\n");
			}
		}

		PreparedStatement pstmt1 = con.prepareStatement(GET_VIEWLAYER_QUERY);
		ResultSet rs = pstmt1.executeQuery();

		if (rs != null) {
			while (rs.next()) {

				String	sViewID		= rs.getString(1);
				String	sScribble	= rs.getString(2);
				String	sBackground	= rs.getString(3);
				String	sGrid		= rs.getString(4);
				String	sShapes		= rs.getString(5);
				int	nBackgroundColor = rs.getInt(6);

				if (bWithResources) {
					if (sBackground != null && !sBackground.equals("") && CoreUtilities.isFile(sBackground)) {
						File file = new File(sBackground);
						if (file.exists()) {
							String sOldBackground = sBackground;
							if (!bKeepPaths)
								sBackground = sBackupPath + "/" + file.getName();
							else {
								sBackground = CoreUtilities.unixPath(sBackground);
							}

							if (!htResources.containsKey(sOldBackground)) {
								htResources.put(sOldBackground, sBackground);
							}
						}
						else if (sBackground != null && !sBackground.equals("")) {
							bNotFound = true;
							System.out.println("NOT FOUND ON BACKUP: "+sBackground);
						}
					}
				}

				dumpfile.write(INSERT_VIEWLAYER_QUERY_BASE);
				dumpfile.write("(");
				dumpfile.write("\'"+sViewID+"\',");
				dumpfile.write("\'"+CoreUtilities.cleanSQLText(sScribble, nDatabaseType)+"\',");
				dumpfile.write("\'"+CoreUtilities.cleanSQLText(sBackground, nDatabaseType)+"\',");
				dumpfile.write("\'"+CoreUtilities.cleanSQLText(sGrid, nDatabaseType)+"\',");
				dumpfile.write("\'"+CoreUtilities.cleanSQLText(sShapes, nDatabaseType)+"\',");
				dumpfile.write("\'"+String.valueOf(nBackgroundColor)+"\'");
				dumpfile.write(")\n");
			}
			dumpfile.write("\n");
		}
        pstmt1.close();
        rs.close();
	}

	/**
	 * Backup the Connection table.
	 *
	 * @param Connection con, the connection object to use to read the data from the database.
	 * @param boolean fullRecreation, indicates if the table drop and create statement should be stored to file.
	 */
	private void backupConnectionTable(Connection con, boolean fullRecreation) throws SQLException, IOException {

		if (fullRecreation) {
			if (nDatabaseType == ICoreConstants.DERBY_DATABASE) {
				dumpfile.write(DBConstantsDerby.DROP_CONNECTION_TABLE+"\n\n");				
				dumpfile.write(DBConstantsDerby.CREATE_CONNECTION_TABLE+"\n\n");
			} else {
				dumpfile.write(DBConstantsMySQL.MYSQL_DROP_CONNECTION_TABLE+";\n\n");								
				dumpfile.write(DBConstantsMySQL.MYSQL_CREATE_CONNECTION_TABLE+";\n\n");
			}
		}

		PreparedStatement pstmt1 = con.prepareStatement(GET_CONNECTION_QUERY);
		ResultSet rs = pstmt1.executeQuery();

		if (rs != null) {
			while (rs.next()) {

				String	sUserID		= rs.getString(1);
				String	sProfile	= rs.getString(2);
				int		nType		= rs.getInt(3);
				String	sServer		= rs.getString(4);
				String	sLogin		= rs.getString(5);
				String	sPassword	= rs.getString(6);
				String	sName		= rs.getString(7);
				int		nPort		= rs.getInt(8);
				String	sResource	= rs.getString(9);

				dumpfile.write(INSERT_CONNECTION_QUERY_BASE);
				dumpfile.write("(");
				dumpfile.write("\'"+sUserID+"\',");
				dumpfile.write("\'"+CoreUtilities.cleanSQLText(sProfile, nDatabaseType)+"\',");
				dumpfile.write(String.valueOf(nType)+",");
				dumpfile.write("\'"+CoreUtilities.cleanSQLText(sServer, nDatabaseType)+"\',");
				dumpfile.write("\'"+CoreUtilities.cleanSQLText(sLogin, nDatabaseType)+"\',");
				dumpfile.write("\'"+CoreUtilities.cleanSQLText(sPassword, nDatabaseType)+"\',");
				dumpfile.write("\'"+CoreUtilities.cleanSQLText(sName, nDatabaseType)+"\',");
				dumpfile.write(String.valueOf(nPort)+",");
				dumpfile.write("\'"+CoreUtilities.cleanSQLText(sResource, nDatabaseType)+"\'");
				dumpfile.write(")\n");
			}
			dumpfile.write("\n");
		}
        pstmt1.close();
        rs.close();
	}

	/**
	 * Backup the Preference table.
	 *
	 * @param Connection con, the connection object to use to read the data from the database.
	 * @param boolean fullRecreation, indicates if the table drop and create statement should be stored to file.
	 */
	private void backupPreferenceTable(Connection con, boolean fullRecreation) throws SQLException, IOException {

		if (fullRecreation) {
			if (nDatabaseType == ICoreConstants.DERBY_DATABASE) {
				dumpfile.write(DBConstantsDerby.DROP_PREFERENCE_TABLE+"\n\n");				
				dumpfile.write(DBConstantsDerby.CREATE_PREFERENCE_TABLE+"\n\n");
			} else {
				dumpfile.write(DBConstantsMySQL.MYSQL_DROP_PREFERENCE_TABLE+";\n\n");								
				dumpfile.write(DBConstantsMySQL.MYSQL_CREATE_PREFERENCE_TABLE+";\n\n");
			}
		}

		PreparedStatement pstmt1 = con.prepareStatement(GET_PREFERENCE_QUERY);
		ResultSet rs = pstmt1.executeQuery();

		if (rs != null) {
			while (rs.next()) {

				String	sUserID		= rs.getString(1);
				String	sProperty	= rs.getString(2);
				String	sContents	= rs.getString(3);

				dumpfile.write(INSERT_PREFERENCE_QUERY_BASE);
				dumpfile.write("(");
				dumpfile.write("\'"+sUserID+"\',");
				dumpfile.write("\'"+CoreUtilities.cleanSQLText(sProperty, nDatabaseType)+"\',");
				dumpfile.write("\'"+CoreUtilities.cleanSQLText(sContents, nDatabaseType)+"\'");
				dumpfile.write(")\n");
			}
			dumpfile.write("\n");
		}
        pstmt1.close();
        rs.close();
	}

	/**
	 * Backup the Meeting table.
	 *
	 * @param Connection con, the connection object to use to read the data from the database.
	 * @param boolean fullRecreation, indicates if the table drop and create statement should be stored to file.
	 */
	private void backupMeetingTable(Connection con, boolean fullRecreation) throws SQLException, IOException {

		if (fullRecreation) {
			if (nDatabaseType == ICoreConstants.DERBY_DATABASE) {
				dumpfile.write(DBConstantsDerby.DROP_MEETING_TABLE+"\n\n");				
				dumpfile.write(DBConstantsDerby.CREATE_MEETING_TABLE+"\n\n");
			} else {
				dumpfile.write(DBConstantsMySQL.MYSQL_DROP_MEETING_TABLE+";\n\n");								
				dumpfile.write(DBConstantsMySQL.MYSQL_CREATE_MEETING_TABLE+";\n\n");
			}
		}

		PreparedStatement pstmt1 = con.prepareStatement(GET_MEETING_QUERY);
		ResultSet rs = pstmt1.executeQuery();

		if (rs != null) {
			while (rs.next()) {

				String	sMeetingID		= rs.getString(1);
				String	sMeetingMapID	= rs.getString(2);
				String	sMeetingName	= rs.getString(3);
				long	dbMeetingDate	= rs.getLong(4);
				int		nStatus			= rs.getInt(5);

				dumpfile.write(INSERT_MEETING_QUERY_BASE);
				dumpfile.write("(");
				dumpfile.write("\'"+sMeetingID+"\',");
				dumpfile.write("\'"+sMeetingMapID+"\',");
				dumpfile.write("\'"+CoreUtilities.cleanSQLText(sMeetingName, nDatabaseType)+"\',");
				dumpfile.write(String.valueOf(dbMeetingDate)+",");
				dumpfile.write(String.valueOf(nStatus));
				dumpfile.write(")\n");
			}
			dumpfile.write("\n");
		}
        pstmt1.close();
        rs.close();
	}

	/**
	 * Backup the MediaIndex table.
	 *
	 * @param Connection con, the connection object to use to read the data from the database.
	 * @param boolean fullRecreation, indicates if the table drop and create statement should be stored to file.
	 */
	private void backupMediaIndexTable(Connection con, boolean fullRecreation) throws SQLException, IOException {

		if (fullRecreation) {
			if (nDatabaseType == ICoreConstants.DERBY_DATABASE) {
				dumpfile.write(DBConstantsDerby.DROP_MEDIAINDEX_TABLE+"\n\n");				
				dumpfile.write(DBConstantsDerby.CREATE_MEDIAINDEX_TABLE+"\n\n");
			} else {
				dumpfile.write(DBConstantsMySQL.MYSQL_DROP_MEDIAINDEX_TABLE+";\n\n");								
				dumpfile.write(DBConstantsMySQL.MYSQL_CREATE_MEDIAINDEX_TABLE+";\n\n");
			}
		}

		PreparedStatement pstmt1 = con.prepareStatement(GET_MEDIAINDEX_QUERY);
		ResultSet rs = pstmt1.executeQuery();

		if (rs != null) {
			while (rs.next()) {

				String	sViewID		= rs.getString(1);
				String	sNodeID		= rs.getString(2);
				String	sMeetingID	= rs.getString(3);
				double	dbMediaIndex= rs.getDouble(4);
				long  	dbCreationDate = rs.getLong(5);
				long	dbModificationDate = rs.getLong(6);

				dumpfile.write(INSERT_MEDIAINDEX_QUERY_BASE);
				dumpfile.write("(");
				dumpfile.write("\'"+sViewID+"\',");
				dumpfile.write("\'"+sNodeID+"\',");
				dumpfile.write("\'"+sMeetingID+"\',");
				dumpfile.write(new Double(dbMediaIndex).longValue()+",");
				dumpfile.write(String.valueOf(dbCreationDate)+",");
				dumpfile.write(String.valueOf(dbModificationDate)+")\n");
			}
			dumpfile.write("\n");
		}
        pstmt1.close();
        rs.close();
	}
	
	/*
	 * Backup the LinkedFile table.
	 *
	 * @param Connection con, the connection object to use to read the data from the database.
	 * @param boolean fullRecreation, indicates if the table drop and create statement should be stored to file.
	 */
	private void backupLinkedFileTable(Connection con, boolean fullRecreation) throws SQLException, IOException {

		if (fullRecreation) {
			if (nDatabaseType == ICoreConstants.DERBY_DATABASE) {
				dumpfile.write(DBConstantsDerby.DROP_LINKEDFILE_TABLE+"\n\n");				
				dumpfile.write(DBConstantsDerby.CREATE_LINKEDFILE_TABLE+"\n\n");
			} else {
				dumpfile.write(DBConstantsMySQL.MYSQL_DROP_LINKEDFILE_TABLE+";\n\n");								
				dumpfile.write(DBConstantsMySQL.MYSQL_CREATE_LINKEDFILE_TABLE+";\n\n");
			}
		}

		PreparedStatement pstmt1 = con.prepareStatement(GET_LINKEDFILE_QUERY);
		ResultSet rs = pstmt1.executeQuery();

		if (rs != null) {
			while (rs.next()) {
				
				String	sFileID		= rs.getString(1);
				String	sFileName	= rs.getString(2);
				int		iFileSize	= rs.getInt(3);
				Blob    fileData    = rs.getBlob(4);
		
				InputStream blobStream = fileData.getBinaryStream(); 
				byte[] b;
				String sFileData = null;
				try {
					b = new byte[blobStream.available()];
					blobStream.read(b, 0, b.length);
					sFileData = new String(b);
				} catch (IOException e) {
					System.err.println("DBBackupDatabase.backupLinkedFileTable: Could not load file data.");
					e.printStackTrace();
				}
				
				dumpfile.write(INSERT_LINKEDFILE_QUERY_BASE);
				dumpfile.write("(");
				dumpfile.write("\'"+sFileID+"\',");
				dumpfile.write("\'"+CoreUtilities.cleanSQLText(sFileName, nDatabaseType)+"\',");
				dumpfile.write("\'"+iFileSize+"\',");
				dumpfile.write("\'"+sFileData+"\')\n");
			}
			dumpfile.write("\n");
		}
        pstmt1.close();
        rs.close();
	}	


	/**
	 * Backup the ViewtimeNode table.
	 *
	 * @param Connection con, the connection object to use to read the data from the database.
	 * @param boolean fullRecreation, indicates if the table drop and create statement should be stored to file.
	 */
	private void backupViewTimeNodeTable(Connection con, boolean fullRecreation) throws SQLException, IOException {

		if (fullRecreation) {
			if (nDatabaseType == ICoreConstants.DERBY_DATABASE) {
				dumpfile.write(DBConstantsDerby.DROP_VIEWTIMENODE_TABLE+"\n\n");				
				dumpfile.write(DBConstantsDerby.CREATE_VIEWTIMENODE_TABLE+"\n\n");
			} else {
				dumpfile.write(DBConstantsMySQL.MYSQL_DROP_VIEWTIMENODE_TABLE+";\n\n");								
				dumpfile.write(DBConstantsMySQL.MYSQL_CREATE_VIEWTIMENODE_TABLE+";\n\n");
			}
		}

		PreparedStatement pstmt1 = con.prepareStatement(GET_VIEWTIMENODE_QUERY);
		ResultSet rs = pstmt1.executeQuery();

		if (rs != null) {

			while (rs.next()) {

				String	sViewTimeNodeID = rs.getString(1);
				String	sViewID		= rs.getString(2);
				String	sNodeID		= rs.getString(3);
				double	nShow		= rs.getDouble(4);
				double	nHide		= rs.getDouble(5);
				int		nXPos		= rs.getInt(6);
				int		nYPos		= rs.getInt(7);
				long	dbCDate		= rs.getLong(8);
				long	dbMDate		= rs.getLong(9);
				int		nStatus		= rs.getInt(10);

				dumpfile.write(INSERT_VIEWTIMENODE_QUERY_BASE);
				dumpfile.write("(");
				dumpfile.write("\'"+sViewTimeNodeID+"\',");
				dumpfile.write("\'"+sViewID+"\',");
				dumpfile.write("\'"+sNodeID+"\',");
				dumpfile.write(new Double(nShow).longValue()+",");
				dumpfile.write(new Double(nHide).longValue()+",");
				dumpfile.write(String.valueOf(nXPos)+",");
				dumpfile.write(String.valueOf(nYPos)+",");
				dumpfile.write(String.valueOf(dbCDate)+",");
				dumpfile.write(String.valueOf(dbMDate)+",");
				dumpfile.write(String.valueOf(nStatus));
				dumpfile.write(")\n");
			}
			dumpfile.write("\n");
		}
        pstmt1.close();
        rs.close();
	}
	
	/**
	 * Backup the Movies table.
	 *
	 * @param Connection con, the connection object to use to read the data from the database.
	 * @param boolean fullRecreation, indicates if the table drop and create statement should be stored to file.
	 */
	private void backupMoviesTable(Connection con, boolean fullRecreation) throws SQLException, IOException {

		if (fullRecreation) {
			if (nDatabaseType == ICoreConstants.DERBY_DATABASE) {
				dumpfile.write(DBConstantsDerby.DROP_MOVIES_TABLE+"\n\n");				
				dumpfile.write(DBConstantsDerby.CREATE_MOVIES_TABLE+"\n\n");
			} else {
				dumpfile.write(DBConstantsMySQL.MYSQL_DROP_MOVIES_TABLE+";\n\n");								
				dumpfile.write(DBConstantsMySQL.MYSQL_CREATE_MOVIES_TABLE+";\n\n");
			}
		}

		PreparedStatement pstmt1 = con.prepareStatement(GET_MOVIES_QUERY);
		ResultSet rs = pstmt1.executeQuery();
		String sBackupPath = "Movies";
		if (rs != null) {

			while (rs.next()) {

				String	sMovieID = rs.getString(1);
				String	sViewID		= rs.getString(2);
				String	sLink		= rs.getString(3);
				long	dbCDate		= rs.getLong(4);
				long	dbMDate		= rs.getLong(5);
				String	sName		= rs.getString(6);
				long	startTime	= rs.getLong(7);

				if (bWithResources && bIncludeMovies) {
					if (sLink != null && !sLink.equals("")) {
						File file = new File(sLink);
						if (file.exists()) {
							String sOldMovie = sLink;
							if (!bKeepPaths)
								sLink = sBackupPath + sFS + file.getName();
							else {
								sLink = CoreUtilities.unixPath(sLink);
							}							
							if (!htResources.containsKey(sOldMovie)) {
								htResources.put(sOldMovie, sLink);
							}
							
							// If it is not a movie files, check if it is a grid stream
							// see if there is an _index and _info file that need copying too
							if (sLink.indexOf(".") == -1) {
								File fileindex = new File(file.getAbsolutePath()+"_index");
								if (fileindex.exists()) {
									String sOldIndex = file.getAbsolutePath()+"_index";
									String sIndex = sBackupPath + sFS + file.getName()+"_index";
									if (bKeepPaths) {
										sIndex = CoreUtilities.unixPath(sOldIndex);
									}							
									if (!htResources.containsKey(sOldIndex)) {
										htResources.put(sOldIndex, sIndex);
									}
								}
								File fileinfo = new File(file.getAbsolutePath()+"_info");
								if (fileinfo.exists()) {
									String sOldInfo = file.getAbsolutePath()+"_info";
									String sInfo = sBackupPath + sFS + file.getName()+"_info";
									if (bKeepPaths) {
										sInfo = CoreUtilities.unixPath(sOldInfo);
									}							
									if (!htResources.containsKey(sOldInfo)) {
										htResources.put(sOldInfo, sInfo);
									}
								}
							}
						} else if (sLink != null && !sLink.equals("")) {
							bNotFound = true;
							System.out.println("NOT FOUND ON EXPORT: "+sLink);
						}
					}
				}		
				
				dumpfile.write(INSERT_MOVIES_QUERY_BASE);
				dumpfile.write("(");
				dumpfile.write("\'"+sMovieID+"\',");
				dumpfile.write("\'"+sViewID+"\',");
				dumpfile.write("\'"+CoreUtilities.cleanSQLText(sLink, nDatabaseType)+"\',");
				dumpfile.write(String.valueOf(dbCDate)+",");
				dumpfile.write(String.valueOf(dbMDate)+",");
				dumpfile.write("\'"+CoreUtilities.cleanSQLText(sName, nDatabaseType)+"\',");
				dumpfile.write("\'"+String.valueOf(startTime)+"\'");
				dumpfile.write(")\n");
			}

			dumpfile.write("\n");
		}
        pstmt1.close();
        rs.close();
	}
	
	/**
	 * Backup the MovieProperties table.
	 *
	 * @param Connection con, the connection object to use to read the data from the database.
	 * @param boolean fullRecreation, indicates if the table drop and create statement should be stored to file.
	 */
	private void backupMoviePropertiesTable(Connection con, boolean fullRecreation) throws SQLException, IOException {

		if (fullRecreation) {
			if (nDatabaseType == ICoreConstants.DERBY_DATABASE) {
				dumpfile.write(DBConstantsDerby.DROP_MOVIEPROPERTIES_TABLE+"\n\n");				
				dumpfile.write(DBConstantsDerby.CREATE_MOVIEPROPERTIES_TABLE+"\n\n");
			} else {
				dumpfile.write(DBConstantsMySQL.MYSQL_DROP_MOVIEPROPERTIES_TABLE+";\n\n");								
				dumpfile.write(DBConstantsMySQL.MYSQL_CREATE_MOVIEPROPERTIES_TABLE+";\n\n");
			}
		}

		PreparedStatement pstmt1 = con.prepareStatement(GET_MOVIEPROPERTIES_QUERY);
		ResultSet rs = pstmt1.executeQuery();

		if (rs != null) {

			while (rs.next()) {

				String	sMoviePropertyID = rs.getString(1);
				String	sMovieID = rs.getString(2);
				int		nXPos		= rs.getInt(3);
				int		nYPos		= rs.getInt(4);
				int		width		= rs.getInt(5);
				int		height		= rs.getInt(6);
				float 	fTransparency = rs.getFloat(7);
				double	time		= rs.getDouble(8);
				long	dbCDate		= rs.getLong(9);
				long	dbMDate		= rs.getLong(10);

				dumpfile.write(INSERT_MOVIEPROPERTIES_QUERY_BASE);
				dumpfile.write("(");
				dumpfile.write("\'"+sMoviePropertyID+"\',");
				dumpfile.write("\'"+sMovieID+"\',");
				dumpfile.write(String.valueOf(nXPos)+",");
				dumpfile.write(String.valueOf(nYPos)+",");
				dumpfile.write(String.valueOf(width)+",");
				dumpfile.write(String.valueOf(height)+",");
				dumpfile.write(fTransparency+",");
				dumpfile.write(String.valueOf(time)+",");
				dumpfile.write(String.valueOf(dbCDate)+",");
				dumpfile.write(String.valueOf(dbMDate));
				dumpfile.write(")\n");
			}

			dumpfile.write("\n");
		}
        pstmt1.close();
        rs.close();
	}
	
    /**
     * Adds <code>DBProgressListener</code> to listeners notified when progress events happen.
     *
     * @see #removeProgressListener
     * @see #removeAllProgressListeners
     * @see #fireProgressCount
     * @see #fireProgressUpdate
     * @see #fireProgressComplete
     * @see #fireProgressAlert
     */
    public void addProgressListener(DBProgressListener listener) {
        if (listener == null) return;
        if (!progressListeners.contains(listener)) {
            progressListeners.addElement(listener);
        }
    }

    /**
     * Removes <code>DBProgressListener</code> from listeners notified of progress events.
     *
     * @see #addProgressListener
     * @see #removeAllProgressListeners
     * @see #fireProgressCount
     * @see #fireProgressUpdate
     * @see #fireProgressComplete
     * @see #fireProgressAlert
     */
    public void removeProgressListener(DBProgressListener listener) {
        if (listener == null) return;
        progressListeners.removeElement(listener);
    }

    /**
     * Removes all listeners notified about progress events.
     *
     * @see #addProgressListener
     * @see #removeProgressListener
     * @see #fireProgressCount
     * @see #fireProgressUpdate
     * @see #fireProgressComplete
     * @see #fireProgressAlert
     */
    public void removeAllProgressListeners() {
        progressListeners.clear();
    }

    /**
     * Notifies progress listeners of the total count of progress events.
     *
     * @see #fireProgressUpdate
     * @see #fireProgressComplete
     * @see #fireProgressAlert
     * @see #addProgressListener
     * @see #removeProgressListener
     * @see #removeAllProgressListeners
     */
    protected void fireProgressCount(int nCount) {
        for (Enumeration e = progressListeners.elements(); e.hasMoreElements(); ) {
            DBProgressListener listener = (DBProgressListener) e.nextElement();
            listener.progressCount(nCount);
        }
    }

    /**
     * Notifies progress listeners about progress change.
     *
     * @see #fireProgressCount
     * @see #fireProgressComplete
     * @see #fireProgressAlert
     * @see #addProgressListener
     * @see #removeProgressListener
     * @see #removeAllProgressListeners
     */
    protected void fireProgressUpdate(int nIncrement, String sMessage) {
        for (Enumeration e = progressListeners.elements(); e.hasMoreElements(); ) {
            DBProgressListener listener = (DBProgressListener) e.nextElement();
            listener.progressUpdate(nIncrement, sMessage);
        }
    }

    /**
     * Notifies progress listeners about progress completion.
     *
     * @see #fireProgressCount
     * @see #fireProgressUpdate
     * @see #fireProgressAlert
     * @see #addProgressListener
     * @see #removeProgressListener
     * @see #removeAllProgressListeners
     */
    protected void fireProgressComplete() {
        for (Enumeration e = progressListeners.elements(); e.hasMoreElements(); ) {
            DBProgressListener listener = (DBProgressListener) e.nextElement();
            listener.progressComplete();
        }
    }

    /**
     * Notifies progress listeners about progress alert.
     *
     * @see #fireProgressCount
     * @see #fireProgressUpdate
     * @see #fireProgressComplete
     * @see #addProgressListener
     * @see #removeProgressListener
     * @see #removeAllProgressListeners
     * @see #removeAllProgressListeners
     */
    protected void fireProgressAlert(String sMessage) {
        for (Enumeration e = progressListeners.elements(); e.hasMoreElements(); ) {
            DBProgressListener listener = (DBProgressListener) e.nextElement();
            listener.progressAlert(sMessage);
        }
    }
}
