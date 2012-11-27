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
import java.util.*;
import java.io.*;

import javax.swing.JOptionPane;
import javax.swing.JFrame;

import com.compendium.core.datamodel.UserProfile;
import com.compendium.core.datamodel.services.IServiceManager;
import com.compendium.core.*;

/**
 * This class is responsible for creating and accessing the administration database
 * that Compendium uses for maintaining the list of user created database and global system properties.
 * For MYSQL Databases
 *
 * @author Michelle Bachler
 */
public class DBAdminDatabase implements DBConstants, DBConstantsMySQL {

// ON MYSQL ONLY	
	/** This variable is not being used yet.*/
	public static final String COMPENDIUM_USER = "compendiumadmin";

	/** This variable is not being used yet.*/
	public static final String COMPENDIUM_PASSWORD = "AGr81KnCu";

	/** Create Compendium User - not used yet.*/
	private static final String SET_COMPENDIUM_USER = "GRANT ALL PRIVILEDGES ON *.* TO "+
												DBAdminDatabase.COMPENDIUM_USER+"@localhost IDENTIFIED BY "+
												"PASSWORD('"+DBAdminDatabase.COMPENDIUM_PASSWORD+"')";

	/** Check if root external host has a password set*/
	private static final String SELECT_ROOT = "SELECT password from user WHERE User='root' AND Host='%'";

	/** Update the root user password for external users*/
	private static final String UPDATE_ROOT = "UPDATE user SET password=? WHERE User='root' AND Host='%'";


// GENERAL

	/** Check which database schema version a given project is using.*/
	public static final String CHECK_VERSION = "SELECT Contents FROM System WHERE Property = 'version'";

	/** Update the projects database schema version number after an update.*/
	public final static String UPDATE_VERSION = "UPDATE System SET Contents = ? WHERE Property = 'version'";


	/**The name of that administration database */
	public static final String DATABASE_NAME = "compendium";				// Original / Local
//	public static final String DATABASE_NAME = "mbegeman_c2";				// For the Michael/Jeff Bug/feature database
//	public static final String DATABASE_NAME = "jconklin_compend01";		// Jeff's Gator host db
//	public static final String DATABASE_NAME = "jconklin_compend02";		// Jeff's Gator host db
	
	/** The SQL statement to insert new database information into the projects table */
	protected static final String INSERT_PROJECT_QUERY = "INSERT INTO Project "+
														"(ProjectName, DatabaseName, CreationDate, ModificationDate) "+
														"VALUES (?, ?, ?,?)";

	/** The SQL statement to update a record in the projects table */
	protected static final String UPDATE_PROJECT_QUERY = "UPDATE Project "+
														"SET ProjectName=? WHERE ProjectName=?";

	/** The SQL statement to test for the exisitance of a specified 'user defined' project name */
	protected static final String SELECT_PROJECTNAME_QUERY = "SELECT ProjectName FROM Project "+
														"WHERE ProjectName=?";

	/** The SQL statement to select all the database and project names from the Project table */
	protected static final String SELECT_PROJECTS_QUERY = "SELECT ProjectName, DatabaseName FROM Project";

	/** The SQL statement to delete a project with the given database name from the Projects table */
	protected static final String DELETE_PROJECT = "DELETE FROM Project WHERE DatabaseName=?";

	/** The SQL statement to update the project name of a given project */
	protected static final String RENAME_PROJECT = "UPDATE Project SET ProjectName=? WHERE ProjectName=?";


	/** The SQL statement to test for the exisitance of a user's administrative status */
	protected static final String CHECK_USER_QUERY = "SELECT isAdministrator FROM Users "+
														"WHERE Login=? AND Password=?";

// OTHER VARIABLES

	/** a list of all the projects and thier database names in the Projects table */
	protected Hashtable htDatabases = null;

	/** A local reference to the main ServiceManager */
	protected IServiceManager serviceManager = null;

	/** A local reference to the DatabaseManager */
	protected DBDatabaseManager databaseManager = null;

	/** The name to use when accessing the MySQL database */
	protected String mysqlname = ICoreConstants.sDEFAULT_DATABASE_USER;

	/** The password to use when accessing the MySQL database */
	protected String mysqlpassword = ICoreConstants.sDEFAULT_DATABASE_PASSWORD;

	/** The password to use when accessing the MySQL database */
	protected String mysqlip = ICoreConstants.sDEFAULT_DATABASE_ADDRESS;

	/**
	 * Constructor, which stores the ServiceManager and DatabaseManager references
	 * and asks the DatabaseManager to open a new project for the administration database.
	 *
	 * @param IServiceManager service, a reference to an instance of a ServiceManager object.
	 */
	public DBAdminDatabase(IServiceManager service) {
		serviceManager = service;
		databaseManager = serviceManager.getDatabaseManager();
		databaseManager.openProject(DATABASE_NAME);
	}

	/**
	 * Constructor, which stores the ServiceManager and DatabaseManager references
	 * and asks the DatabaseManager to open a new project for the administration database.
	 * This constructor also takes a name and password to use when accessing the MySQL database and uses 'localhost' as the address.
	 *
	 * @param IServiceManager service, a reference to an instance of a ServiceManager object.
	 * @param sDatabaseName, the name to use when creating the connection to the MySQL database
	 * @param sDatabasePassword, the password to use when connection to the MySQL database
	 */
	public DBAdminDatabase(IServiceManager service, String sDatabaseName, String sDatabasePassword) {
		serviceManager = service;
		databaseManager = serviceManager.getDatabaseManager();
		databaseManager.openProject(DATABASE_NAME);
		mysqlname = sDatabaseName;
		mysqlpassword = sDatabasePassword;
	}

	/**
	 * Constructor, which stores the ServiceManager and DatabaseManager references
	 * and asks the DatabaseManager to open a new project for the administration database.
	 * This constructor also takes a name and password to use when accessing the MySQL database,
	 * and the IP address of the MysqL server machine.
	 *
	 * @param IServiceManager service, a reference to an instance of a ServiceManager object.
	 * @param sDatabaseName, the name to use when creating the connection to the MySQL database
	 * @param sDatabasePassword, the password to use when connection to the MySQL database
	 * @param sDatabaseIP, the IP address of the MySQL server machine. The default if 'localhost'.
	 */
	public DBAdminDatabase(IServiceManager service, String sDatabaseName, String sDatabasePassword, String sDatabaseIP) {
		serviceManager = service;
		databaseManager = serviceManager.getDatabaseManager();
		databaseManager.openProject(DATABASE_NAME);

		if (sDatabaseIP != null && !sDatabaseIP.equals("")) {
			mysqlip = sDatabaseIP;
		}

		mysqlname = sDatabaseName;
		mysqlpassword = sDatabasePassword;
	}

	/**
	 * Return a reference to the database manager used by this Database Administration object.
	 */
	public DBDatabaseManager getDatabaseManager() {
		return databaseManager;
	}

	/**
	 * Check that the Compendium administration database for this MySQL database application exists.
	 *
	 * @exception java.sql.SQLException
	 * @exception java.lang.FileNotFoundException
	 */
	public boolean checkForAdminDatabase() throws SQLException, ClassNotFoundException {

	    Connection con = DBConnectionManager.getPlainConnection(ICoreConstants.MYSQL_DATABASE, "?", mysqlname, mysqlpassword, mysqlip);

		if (con != null) {
			PreparedStatement pstmt = con.prepareStatement("SHOW DATABASES");
			ResultSet rs = pstmt.executeQuery();

			boolean bExists = false;
			if (rs != null) {
				while (rs.next()) {
					String name = rs.getString(1);
					if (name.equals("compendium")) {
						pstmt.close();
						con.close();
						return true;
					}
				}
			}
			pstmt.close();

		}
		return false;
	}

	/**
	 * Check that the Compendium administration database for this MySQL database application exists.
	 * If not, it must be the first time Compendium is being used with this MySQL database application, so create it.
	 *
	 * @exception java.sql.SQLException
	 * @exception java.lang.ClassNotFoundException
	 */
	public boolean checkAdminDatabase() throws SQLException, ClassNotFoundException {

		Connection con = null;
		PreparedStatement pstmt = null;

	    con = DBConnectionManager.getPlainConnection(ICoreConstants.MYSQL_DATABASE, "?", mysqlname, mysqlpassword, mysqlip);

		if (con != null) {
			// CHECK IF COMPENDIUM DATABASE ALREADY EXISITS
			pstmt = con.prepareStatement("SHOW DATABASES");
			ResultSet rs = pstmt.executeQuery();

			boolean bExists = false;
			if (rs != null) {
				while (rs.next()) {
					String name = rs.getString(1);
					if (name.equals(DATABASE_NAME)) {
						// SHOULD CHECK FOR PROPERTIES TABLE HERE, AND ADD IF NECESSARY ?
						// CHECK FOR COMPENDIUMADMIN USER AND STORE
				  		//Connection con2 = DBConnectionManager.getPlainConnection("mysql", mysqlname, mysqlpassword, mysqlip);
						//if (con2 != null && mysqlip.equals(ICoreConstants.sDEFAULT_DATABASE_ADDRESS)) {

						//}
						pstmt.close();
						con.close();
						return true;
					}
				}
			}
			pstmt.close();

	  		con = DBConnectionManager.getMySQLConnection("mysql", mysqlname, mysqlpassword, mysqlip);
			if (con != null && mysqlip.equals(ICoreConstants.sDEFAULT_DATABASE_ADDRESS)) {

				// ADD COMPENDIUM USER
				//pstmt = con.prepareStatement(SET_COMPENDIUM_USER);
				//int nRowCount2 = pstmt.executeUpdate();
				//if (nRowCount2 > 0) {}
				//pstmt.close();

				// UPDATE ROOT USER PASSWORD FOR EXTERNAL ACCESS ONLY
				pstmt = con.prepareStatement(SELECT_ROOT);
				rs = pstmt.executeQuery();
				boolean hasPassword = false;
				if (rs != null) {
					while (rs.next()) {
						String  password = rs.getString(1);
						if (password != null && !password.equals(""))
							hasPassword = true;
					}
				}
				pstmt.close();

				if (!hasPassword) {
					pstmt = con.prepareStatement(UPDATE_ROOT);
					java.util.Date date = new java.util.Date();
					String time = (new Integer( (new Double(date.getTime())).intValue() )).toString();
					pstmt.setString(1, ICoreConstants.sDATABASE_PASSWORD+time);
					int nRowCount = pstmt.executeUpdate();
					pstmt.close();
					if (nRowCount < 0) {
						System.out.println("failed to update root password for external host");
					}
					else {
						pstmt = con.prepareStatement("FLUSH PRIVILEGES");
						pstmt.executeUpdate();
						pstmt.close();
					}
				}
				con.close();
			}

			// IF THIS FAR, THEN COMPENDIUM DATABASE DOES NOT EXISTS, SO CREATE
			//con = DBConnectionManager.getCreationConnection(DATABASE_NAME, mysqlname, mysqlpassword, mysqlip);
			con = DBConnectionManager.getPlainConnection(ICoreConstants.MYSQL_DATABASE, "?", mysqlname, mysqlpassword, mysqlip);

			//pstmt = con.statement("CREATE DATABASE "+DATABASE_NAME);
			//int nRowCount = pstmt.executeUpdate();

			Statement stmt = con.createStatement();
			int nRowCount = stmt.executeUpdate("CREATE DATABASE "+DATABASE_NAME);
			pstmt.close();
			con.close();

			if (nRowCount > 0) {
	  			con = DBConnectionManager.getPlainConnection(ICoreConstants.MYSQL_DATABASE, DATABASE_NAME, mysqlname, mysqlpassword, mysqlip);
				if (con != null) {
					createTables(con);
					con.close();
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * Call the methods to create the Project and Properties tables.
	 *
	 * @param Connection con, the connection used to creaate the new tables.
	 * @see #createProjectTable
	 * @see #createPropertiesTable
	 */
	private static void createTables(Connection con) throws SQLException {

		createMySQLProjectTable(con);
		createMySQLPropertiesTable(con);
	}

	/**
	 * Create the Projects table in the administration database on a MySQL database.
	 *
	 * @param Connection con, the connection used to creaate the new table.
	 * @see #createTables
	 * @see #createPropertiesTable
	 */
	private static void createMySQLProjectTable(Connection con) throws SQLException {

		PreparedStatement pstmt = con.prepareStatement(MYSQL_CREATE_PROJECT_TABLE);
		int nRowCount = pstmt.executeUpdate() ;
		pstmt.close();
	}

	/**
	 * Create the Properties table in the administration database on a MySQL database.
	 *
	 * @param Connection con, the connection used to creaate the new table.
	 * @see #createTables
	 * @see #createProjectTable
	 */
	private static void createMySQLPropertiesTable(Connection con) throws SQLException {

		PreparedStatement pstmt = con.prepareStatement(MYSQL_CREATE_PROPERTIES_TABLE);
		int nRowCount = pstmt.executeUpdate() ;
		pstmt.close();
	}

	/**
	 * Update the database version number in the database.
	 */
	public boolean updateVersion(Connection con, String version) throws SQLException {

		PreparedStatement pstmt = con.prepareStatement(UPDATE_VERSION);
		pstmt.setString(1, version);
		int nRowCount = pstmt.executeUpdate() ;
		pstmt.close();

		if (nRowCount > 0) {
			return true;
		}
		return false;
	}

	/**
	 * Extract the database schema version number from the database.
	 *
	 * @param Connection con, the connection to the database.
	 * @return a String representing the schema version number of the database.
	 */
	public String checkVersion(Connection con) {

		String version = "";

		try {
			PreparedStatement pstmt = con.prepareStatement(CHECK_VERSION);
			ResultSet rs = pstmt.executeQuery();

			if (rs != null) {
				while (rs.next()) {
					version	= rs.getString(1);
				}
			}

			pstmt.close();
		}
		catch(SQLException ie) {}

		return version;
	}

	/**
	 * Check to see if a given database scheme version requires updating.
	 * @param String sProject, the name of the project whose schema status to check.
	 * @return int, indicating the schema status;
	 */
	public int getSchemaStatusForDatabase(String sProject) {

		int status = -1;

		try {
			DBConnection dbcon = databaseManager.requestConnection(sProject);
			Connection con = dbcon.getConnection();
			if (con != null) {
				String version = checkVersion(con);
				if (version.equals(ICoreConstants.sDATABASEVERSION)) {
					status = ICoreConstants.CORRECT_DATABASE_SCHEMA;
				}
				else if (CoreUtilities.isNewerSchema(version)) {
					status = ICoreConstants.NEWER_DATABASE_SCHEMA;
				}
				else if (CoreUtilities.isOlderSchema(version)) {
					status = ICoreConstants.OLDER_DATABASE_SCHEMA;
				}
			}
			databaseManager.releaseConnection(sProject, dbcon);
		}
		catch(Exception ex) {
			System.out.println("Exception: (DBAdminDatabase.getSchemaStatusForDatabase)\n\n"+ex.getMessage());
		}

		return status;
	}

	/**
	 * Check to see if any of the database schemes versions requires updating.
	 * @return Hashtable, list of projects and if they need updating (true/false);
	 * @exception DBProjectListException, thrown if the list of projects could not be loaded from the database.
	 * 
	 */
	public Hashtable getProjectSchemaStatus() throws DBProjectListException {

		Vector vtProjects = getDatabaseProjects();
		Hashtable htProjects = new Hashtable();

		int count = vtProjects.size();
		for (int i=0; i<count; i++) {
			String project = (String)vtProjects.elementAt(i);
			try {
				String nextModel = getDatabaseName(project);
				DBConnection dbcon = databaseManager.requestConnection(nextModel);
				Connection con = dbcon.getConnection();

				if (con != null) {
					String version = checkVersion(con);
					if (version.equals(ICoreConstants.sDATABASEVERSION)) {
						htProjects.put(project, new Integer(ICoreConstants.CORRECT_DATABASE_SCHEMA));
					}
					else if (CoreUtilities.isNewerSchema(version)) {
						htProjects.put(project, new Integer(ICoreConstants.NEWER_DATABASE_SCHEMA));
					}
					else if (CoreUtilities.isOlderSchema(version)) {
						htProjects.put(project, new Integer(ICoreConstants.OLDER_DATABASE_SCHEMA));
					}
					else {
						htProjects.put(project, new Integer(-1));
					}
				}
				databaseManager.releaseConnection(nextModel, dbcon);
			}
			catch(Exception ex) {
				htProjects.put(project, new Integer(-1));
				System.out.println("Exception: (DBAdminDatabase.getProjectSchemaStatus)\n\n"+ex.getMessage());
			}
		}

		return htProjects;
	}


	/**
	 * Load the list of available Compendium databases
	 *
	 * @exception DBProjectListException, thrown if the list of projects could not be loaded from the database.
	 */
	public boolean loadDatabaseProjects() throws DBProjectListException {

		try {
			DBConnection dbcon = null;
        	dbcon = databaseManager.requestConnection(DATABASE_NAME);
        	if (dbcon == null) {
				return false;
			}
			Connection con = dbcon.getConnection();
			if (con == null) {
				return false;
			}

			htDatabases = new Hashtable();
			PreparedStatement pstmt = con.prepareStatement(SELECT_PROJECTS_QUERY);
			ResultSet rs = pstmt.executeQuery();

			if (rs != null) {
				while (rs.next()) {
					String  name			= rs.getString(1);
					String	database		= rs.getString(2);
					htDatabases.put(name, database);
				}
			}
			databaseManager.releaseConnection(DATABASE_NAME, dbcon);

			return true;
		}
		catch (SQLException ex) {
			System.out.println("SQLException: (DBAdminDatabase.loadDatabaseProjects): "+ex.getLocalizedMessage());
			ex.printStackTrace();
			System.out.flush();
			throw new DBProjectListException("Problem fetching list of database connections: " + ex.getLocalizedMessage());
		}
	}

	/**
	 * Sort the project names alphabetically and return a comma separated string of the names.
	 *
	 * @return String, a comma separated string of the project name.
	 * @exception DBProjectListException, thrown if the list of projects could not be loaded from the database. 
	 */
	public Vector getDatabaseProjects() throws DBProjectListException {

		if (htDatabases == null) {
			loadDatabaseProjects();
		}

		// SORT THE DATABASES NAME ALPHABETICALLY FIRST
		Vector databases = new Vector();
		for(Enumeration e = htDatabases.keys();e.hasMoreElements();) {
			databases.addElement((String)e.nextElement());
		}
		databases = CoreUtilities.sortList(databases);

		/*String projects = "";
		int count = databases.size();
		for (int i=0; i<count; i++) {
			if (i < count-1)
				projects = projects + (String)databases.elementAt(i)+",";
			else
				projects = projects + (String)databases.elementAt(i);
		}*/

		return databases;
	}

	/**
	 * Return true if a database with the given name already exists.
	 *
	 * @param String sDatabase, the name of the database to check.
	 * @return boolean.
	 * @exception DBProjectListException, thrown if the list of projects could not be loaded from the database.
	 */
	public boolean hasDatabase(String sDatabase) throws DBProjectListException {

		if (htDatabases == null) {
			loadDatabaseProjects();
		}

		for(Enumeration e = htDatabases.elements();e.hasMoreElements();) {
			String sName = (String)e.nextElement();
			if (sName.equals(sDatabase))
				return true;
		}

		return false;
	}

	/**
	 * Return the number of Compendium projects.
	 * @exception DBProjectListException, thrown if the list of projects could not be loaded from the database.
	 */
	public int getDatabaseCount() throws DBProjectListException {
		if (htDatabases == null) {
			loadDatabaseProjects();
		}

		return htDatabases.size();
	}

	/**
	 * For the given database name, find and return its friendly name.
	 *
	 * @param String name, the database name to get the 'user friendly' for.
	 * @return String, the 'user friendly' name for the given database name.
	 * @exception DBProjectListException, thrown if the list of projects could not be loaded from the database.
	 */
	public String getFriendlyName(String sName) throws DBProjectListException {

		if (htDatabases == null) {
			loadDatabaseProjects();
		}

		String name = "Unknown";
		if (htDatabases.containsValue(sName)) {
			for (Enumeration e = htDatabases.keys(); e.hasMoreElements();) {
				String key = (String)e.nextElement();
				String value = (String)htDatabases.get(key);
				if (value.equals(sName))
					name = key;
			}
		}
		return name;
	}

	/**
	 * For the given project name, find and return its database name.
	 *
	 * @param String name, the 'user friendly' project name of a database.
	 * @return String, the database name for the given project name.
	 * @exception DBProjectListException, thrown if the list of projects could not be loaded from the database.
	 */
	public String getDatabaseName(String sName) throws DBProjectListException {

		if (htDatabases == null) {
			loadDatabaseProjects();
		}

		String name = null;
		if (htDatabases.containsKey(sName)) {
			name = (String)htDatabases.get(sName);
		}
		return name;
	}

	/**
	 * For the given old project name, update the database with its new name.
	 *
	 * @param String sOldName, the current 'user friendly' project name of a database.
	 * @param String sNewName, the new project name for the given project.
	 * @exception DBProjectListException, thrown if the list of projects could not be loaded from the database.
	 */
	public boolean editFriendlyName(String sOldName, String sNewName) throws DBProjectListException {

		try {
			DBConnection dbcon = null;
        	dbcon = databaseManager.requestConnection(DATABASE_NAME);

			Connection con = dbcon.getConnection();
			if (con == null) {
				return false;
			}
			else {
				PreparedStatement pstmt = con.prepareStatement(RENAME_PROJECT);
				pstmt.setString(1, sNewName);
				pstmt.setString(2, sOldName);

				int nRowCount = pstmt.executeUpdate();
				pstmt.close();

				if (nRowCount > 0) {
					loadDatabaseProjects();
					return true;
				}
			}
			databaseManager.releaseConnection(DATABASE_NAME, dbcon);
		}
		catch (SQLException ex) {
		    System.out.println("SQLException: (DBAdminDatabase.editFriendlyName)\n\n"+ex.getMessage());
		}

		return false;
	}

	/**
	 * Delete the database with the given database name
	 *
	 * @param String sFriendlyName, the 'user friendly' name for a database project.
	 * @param String sDatabaseName, the actual database name as it is known in MySQL.
	 * @return if the database was successfully deleted.
	 * @exception java.sql.SQLException
	 * @exception DBProjectListException, thrown if the list of projects could not be loaded from the database.
	 */
	public boolean deleteDatabase(String sProjectName, String sDatabaseName) throws SQLException, DBProjectListException{

       	DBConnection dbcon = databaseManager.requestConnection(DATABASE_NAME);
		Connection con = dbcon.getConnection();

		if (con == null) {
			System.out.println("Connection = false");
			return false;
		}
		else {
	      	DBConnection dbcon2 = databaseManager.requestConnection(sDatabaseName);
			Connection con2 = dbcon2.getConnection();			
			dropTables(con2, MYSQL_DROP_TABLES);
			databaseManager.releaseConnection(sDatabaseName, dbcon2);

			Statement stmt = con.createStatement();
			String statement = "DROP DATABASE IF EXISTS "+sDatabaseName;
			stmt.executeUpdate(statement);
			stmt.close();

			PreparedStatement pstmt = con.prepareStatement(DELETE_PROJECT);
			pstmt.setString(1, sDatabaseName);
			pstmt.executeUpdate() ;
			pstmt.close();
			loadDatabaseProjects();
		}
		databaseManager.releaseConnection(DATABASE_NAME, dbcon);

		return false;
	}
	
	/**
	 * Drop all the tables for the current database.
	 *
	 * @param Connection con, the connection to use to access the database.
	 * @param String[], the SQL strings to drop the tables with.
	 * @exception java.sql.SQLException
	 */
	protected void dropTables(Connection con, String[] tables) throws SQLException {
		PreparedStatement pstmt = null;
		for (int i= 0; i < tables.length; i++) {
			pstmt = con.prepareStatement(tables[i]);
			pstmt.executeUpdate() ;
			pstmt.close();
		}
	}

	/**
	 * Add a new database with the given project and database name.
	 *
	 * @param String sProjectName, the 'user friendly' name for a database project.
	 * @param String sDatabaseName, the actual database name as it is known in MySQL.
	 * @exception java.sql.SQLException
	 */
	public void addNewDatabase(String sProjectName, String sDatabaseName) throws SQLException {

		DBConnection dbcon = null;
       	dbcon = databaseManager.requestConnection(DATABASE_NAME);

		Connection con = dbcon.getConnection();
		if (con == null) {
			throw new SQLException("Unable to get connection to database");
		}
		else {
			java.util.Date oDate = new java.util.Date();
			double dbDate = oDate.getTime();

			PreparedStatement pstmt = con.prepareStatement(INSERT_PROJECT_QUERY);
			pstmt.setString(1, sProjectName);
			pstmt.setString(2, sDatabaseName);
			pstmt.setDouble(3, dbDate);
			pstmt.setDouble(4, dbDate);

			int nRowCount = pstmt.executeUpdate() ;
			pstmt.close();
			databaseManager.releaseConnection(DATABASE_NAME,dbcon);

			if (nRowCount > 0) {
				htDatabases.put(sProjectName, sDatabaseName);
			}
			else {
				throw new SQLException("New database details could not be added to Administration database list");
			}
		}
	}

	/**
	 * Add a new database with the given project and database name.
	 *
	 * @param String sDatabaseName, the actual database name as it is known in MySQL.
	 * @param String slogin, the users login name.
	 * @param String sPassword, the users password.
	 * @return if the the user is a valid user for the given database and is an administrator of it.
	 */
	public boolean isAdministrator(String sDatabaseName, String sLogin, String sPassword) {

		try {
			DBConnection dbcon = null;
        	dbcon = databaseManager.requestConnection(sDatabaseName);

			Connection con = dbcon.getConnection();
			if (con == null) {
				System.out.println("Connection = false for DBAdminDatabase.isAdministrator");
				return false;
			}
			else {

				PreparedStatement pstmt = con.prepareStatement(CHECK_USER_QUERY);
				pstmt.setString(1, sLogin);
				pstmt.setString(2, sPassword);

				ResultSet rs = pstmt.executeQuery();

				if (rs != null) {
					while (rs.next()) {
						String admin = rs.getString(1);
						if (admin.equals("Y")) {
							pstmt.close();
							return true;
						}
					}
				}
				pstmt.close();
			}
			databaseManager.releaseConnection(DATABASE_NAME,dbcon);
		}
		catch (SQLException ex) {
		    System.out.println("SQLException: (DBAdminDatabase.isAdministrator)\n\n"+ex.getMessage());
		}

		return false;
	}
}
