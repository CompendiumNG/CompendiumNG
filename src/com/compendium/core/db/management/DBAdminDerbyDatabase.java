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
import java.util.Enumeration;
import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;
import java.util.Enumeration;
import java.io.*;


import javax.swing.JOptionPane;
import javax.swing.JFrame;

import com.compendium.core.datamodel.*;
import com.compendium.core.datamodel.services.IServiceManager;
import com.compendium.core.*;

/**
 * This class is responsible for creating and accessing the Derby administration database
 * that Compendium uses for maintaining the list of user created database and global system properties.
 * For DERBY Databases
 *
 * @author Michelle Bachler
 */
public class DBAdminDerbyDatabase extends DBAdminDatabase implements DBConstants, DBConstantsDerby {

	/** A reference to the system file path separator*/
	private final static String	sFS		= System.getProperty("file.separator");

	/** SQL statement to add a new Connection record into the Connections   table.*/
	private final static String INSERT_CONNECTION_QUERY =
		"INSERT INTO Connections " +
		"(Profile, Type, Server, Login, Password, Port, DefaultDatabase) " +
		"VALUES (?, ?, ? ,? ,?, ?, ?)";

	/** SQL statement to update a Connection record in the Connections table.*/
	private final static String UPDATE_CONNECTION_QUERY =
		"UPDATE Connections " +
		"SET Profile=?, Type=?, Server=?, Login=?, Password=?, Port=?, DefaultDatabase=? " +
		"WHERE Profile=? AND Type=?";

	/** SQL statement to delete the Connection in the Connections table.*/
	private final static String DELETE_CONNECTION_QUERY =
		"DELETE From Connections " +
		"WHERE Profile=? AND Type=?";

	/** SQL statement to update the DefaultDatabase field for a Connection in the Connections table.*/
	private final static String UPDATE_DEFAULT_DATABASE_NAME_QUERY =
		"UPDATE Connections " +
		"SET DefaultDatabase=? " +
		"WHERE Profile=? AND Type=?";

	/** SQL statement to return all the connections for a given type.*/
	private final static String SELECT_CONNECTIONS_QUERY =
		"SELECT * " +
		"FROM Connections "+
		"WHERE Type=? Order By Profile";

	/** SQL statement to return the the connection with the given name and type.*/
	private final static String SELECT_NAMED_CONNECTION_QUERY =
		"SELECT * " +
		"FROM Connections "+
		"WHERE Profile=? AND Type=?";

	/**
	 * Constructor, which stores the ServiceManager and DatabaseManager references
	 * and asks the DatabaseManager to open a new project for the administration database.
	 *
	 * @param IServiceManager service, a reference to an instance of a ServiceManager object.
	 */
	public DBAdminDerbyDatabase(IServiceManager service) {
		super(service);
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
	public DBAdminDerbyDatabase(IServiceManager service, String sDatabaseName, String sDatabasePassword) {
		super(service, sDatabaseName, sDatabasePassword);
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
	public DBAdminDerbyDatabase(IServiceManager service, String sDatabaseName, String sDatabasePassword, String sDatabaseIP) {
		super(service, sDatabaseName, sDatabasePassword, sDatabaseIP);
	}

	/**
	 * Check if this is the first time compendium is being opened, by
	 * checking if the Derby Compendium administration database exists.
	 * If it does return false return false, else true.
	 *
	 * @exception java.sql.SQLException
	 * @exception java.lang.ClassNotFoundException
	 */
	public boolean firstTime() throws SQLException, ClassNotFoundException {

		File file = new File("System"+sFS+"resources"+sFS+"Databases"+sFS+DATABASE_NAME);
		if (!file.exists())
			return true;

		return false;
	}

	/**
	 * Check that the Derby Compendium administration database exists.
	 * If not, it must be the first time Compendium is being used, so create it.
	 *
	 * @exception java.sql.SQLException
	 * @exception java.lang.ClassNotFoundException
	 */
	public boolean checkAdminDatabase() throws SQLException, ClassNotFoundException {

		File file = new File("System"+sFS+"resources"+sFS+"Databases"+sFS+DATABASE_NAME);
		if (!file.exists()) {
			Connection con = DBConnectionManager.getDerbyCreationConnection(DATABASE_NAME);
			if (con != null) {
				createTables(con);
				con.close();
				return true;
			}
		}
		else {
			return true;
		}

		return false;
	}

	/**
	 * Call the methods to create the Project, Properties and external Connections tables.
	 *
	 * @param Connection con, the connection used to creaate the new tables.
	 * @see #createProjectTable
	 * @see #createPropertiesTable
	 */
	private static void createTables(Connection con) throws SQLException {

		createProjectTable(con);
		createPropertiesTable(con);
		createConnectionsTable(con);
	}

	/**
	 * Create the Projects table in the administration database.
	 *
	 * @param Connection con, the connection used to creaate the new table.
	 * @see #createTables
	 * @see #createPropertiesTable
	 */
	private static void createProjectTable(Connection con) throws SQLException {

		PreparedStatement pstmt = con.prepareStatement(CREATE_PROJECT_TABLE);
		int nRowCount = pstmt.executeUpdate() ;
		pstmt.close();
	}

	/**
	 * Create the Properties table in the administration database.
	 *
	 * @param Connection con, the connection used to creaate the new table.
	 * @see #createTables
	 * @see #createProjectTable
	 */
	private static void createPropertiesTable(Connection con) throws SQLException {

		PreparedStatement pstmt = con.prepareStatement(CREATE_PROPERTIES_TABLE);
		int nRowCount = pstmt.executeUpdate() ;
		pstmt.close();
	}

	/**
	 * Create the Connections table in the Derby administration database.
	 *
	 * @param Connection con, the connection used to creaate the new table.
	 * @see #createTables
	 * @see #createProjectTable
	 */
	private static void createConnectionsTable(Connection con) throws SQLException {

		PreparedStatement pstmt = con.prepareStatement(CREATE_ADMIN_CONNECTIONS_TABLE);
		int nRowCount = pstmt.executeUpdate() ;
		pstmt.close();
	}

	/**
	 * Delete the database with the given database name.
	 * For Derby this involves deleting all the files and folders associated with the database
	 * and then the record from the project table.
	 *
	 * @param String sFriendlyName, the 'user friendly' name for a database project.
	 * @param String sDatabaseName, the actual database name as it is known in MySQL.
	 * @return if the database was successfully deleted.
	 * @exception java.sql.SQLException
	 * @exception DBProjectListException, thrown if the list of projects could not be loaded from the database.
	 */
	public boolean deleteDatabase(String sProjectName, String sDatabaseName) throws SecurityException, SQLException, DBProjectListException {

		File file = new File("System"+sFS+"resources"+sFS+"Databases"+sFS+sDatabaseName);

		
		boolean successful = CoreUtilities.deleteDirectory(file);

		databaseManager.removeAllConnections(sProjectName);

       	DBConnection dbcon = databaseManager.requestConnection(DATABASE_NAME);
		Connection con = dbcon.getConnection();
		PreparedStatement pstmt = con.prepareStatement(DELETE_PROJECT);
		pstmt.setString(1, sDatabaseName);
		pstmt.executeUpdate() ;
		pstmt.close();

		loadDatabaseProjects();

		return successful;
	}

	/**
	 * Return a list of all the MySQL Conections held in the database.
	 *
	 * @return a list of all the MySQL Conections held in the database.
	 * @exception java.sql.SQLException
	 */
	public Vector getMySQLConnections() throws SQLException {

		Vector connections  = new Vector();

		DBConnection dbcon = null;
       	dbcon = databaseManager.requestConnection(DATABASE_NAME);

		Connection con = dbcon.getConnection();
		if (con == null)
			return connections;

		PreparedStatement pstmt = con.prepareStatement(SELECT_CONNECTIONS_QUERY);
		pstmt.setInt(1, ICoreConstants.MYSQL_DATABASE);
		ResultSet rs = pstmt.executeQuery();

		if (rs != null) {
			while (rs.next()) {
				String sProfile = rs.getString(1);
				String sServer = rs.getString(3);
				String sLogin = rs.getString(4);
				String sPassword = rs.getString(5);
				int nPort = rs.getInt(6);
				String sDefaultDatabase = rs.getString(7);

				ExternalConnection connection = new ExternalConnection(sProfile, ICoreConstants.MYSQL_DATABASE, sServer, sLogin, sPassword, sDefaultDatabase, nPort);

				connections.addElement(connection);
			}
		}
		pstmt.close() ;
		databaseManager.releaseConnection(DATABASE_NAME,dbcon);

		return connections;
	}

	/**
	 * Return the connection with the given name and type.
	 *
	 * @param String sName, the name of the connection profile to return.
	 * @param int nType, the type fo the connection to search for.
	 * @return the connection with the given name and type.
	 * @exception java.sql.SQLException
	 */
	public ExternalConnection getConnectionByName(String sName, int nType) throws SQLException {

		ExternalConnection connection  = null;

		DBConnection dbcon = null;
       	dbcon = databaseManager.requestConnection(DATABASE_NAME);

		Connection con = dbcon.getConnection();
		if (con == null)
			return connection;

		PreparedStatement pstmt = con.prepareStatement(SELECT_NAMED_CONNECTION_QUERY);
		pstmt.setString(1, sName);
		pstmt.setInt(2, nType);
		ResultSet rs = pstmt.executeQuery();

		if (rs != null) {
			while (rs.next()) {
				String sProfile = rs.getString(1);
				String sServer = rs.getString(3);
				String sLogin = rs.getString(4);
				String sPassword = rs.getString(5);
				int nPort = rs.getInt(6);
				String sDefaultDatabase = rs.getString(7);

				connection = new ExternalConnection(sProfile, nType, sServer, sLogin, sPassword, sDefaultDatabase, nPort);
				return connection;
			}
		}
		pstmt.close() ;
		databaseManager.releaseConnection(DATABASE_NAME,dbcon);

		return connection;
	}

	/**
	 *  Insert a new connection record and return if successful.
	 *
	 *	@param DBExternalConnection connection, the connection to insert.
	 *	@return boolean, true if it was successful, else false.
	 *	@throws java.sql.SQLException
	 */
	public boolean insertConnection(ExternalConnection connection) throws SQLException {

		DBConnection dbcon = null;
       	dbcon = databaseManager.requestConnection(DATABASE_NAME);
		Connection con = dbcon.getConnection();
		if (con == null)
			return false;

		PreparedStatement pstmt = con.prepareStatement(INSERT_CONNECTION_QUERY);
		pstmt.setString(1, connection.getProfile());
		pstmt.setInt(2, connection.getType());
		pstmt.setString(3, connection.getServer());
		pstmt.setString(4, connection.getLogin());
		pstmt.setString(5, connection.getPassword());
		pstmt.setInt(6, connection.getPort());
		pstmt.setString(7, connection.getName());

		int nRowCount = pstmt.executeUpdate();
		pstmt.close() ;
		databaseManager.releaseConnection(DATABASE_NAME,dbcon);

		if (nRowCount > 0) {
			return true;
		}
		return false;
	}

	/**
	 *  Update the connection record and return if successful.
	 *
	 *	@param DBExternalConnection connection, the connection to update.
	 *	@param String sProfile, the olf profile name for the record to be updated.
	 *	@param int nType, the old connection type for the record being updated.
	 *	@return boolean, true if it was successful, else false.
	 *	@throws java.sql.SQLException
	 */
	public boolean updateConnection(ExternalConnection connection, String sProfile, int nType) throws SQLException {

		DBConnection dbcon = null;
       	dbcon = databaseManager.requestConnection(DATABASE_NAME);
		Connection con = dbcon.getConnection();
		if (con == null)
			return false;

		PreparedStatement pstmt = con.prepareStatement(UPDATE_CONNECTION_QUERY);
		pstmt.setString(1, connection.getProfile());
		pstmt.setInt(2, connection.getType());
		pstmt.setString(3, connection.getServer());
		pstmt.setString(4, connection.getLogin());
		pstmt.setString(5, connection.getPassword());
		pstmt.setInt(6, connection.getPort());
		pstmt.setString(7, connection.getName());

		// Primary Key Fields
		pstmt.setString(8, sProfile);
		pstmt.setInt(9, nType);

		int nRowCount = pstmt.executeUpdate();

		pstmt.close();
		databaseManager.releaseConnection(DATABASE_NAME,dbcon);

		if (nRowCount > 0) {
			return true;
		}
		return false;
	}

	/**
	 *  Update the default database name for the given database profile and type and return if successful.
	 *
	 *	@param String sDefaultDatabase, the name of the default database to be updated.
	 *	@param String sProfile, the olf profile name for the record to be updated.
	 *	@param int nType, the old connection type for the record being updated.
	 *	@return boolean, true if it was successful, else false.
	 *	@throws java.sql.SQLException
	 */
	public boolean setDefaultDatabase(String sDefaultDatabase, String sProfile, int nType) throws SQLException {

		DBConnection dbcon = null;
       	dbcon = databaseManager.requestConnection(DATABASE_NAME);
		Connection con = dbcon.getConnection();

		if (con == null)
			return false;

		PreparedStatement pstmt = con.prepareStatement(UPDATE_DEFAULT_DATABASE_NAME_QUERY);
		pstmt.setString(1, sDefaultDatabase);
		pstmt.setString(2, sProfile);
		pstmt.setInt(3, nType);

		int nRowCount = pstmt.executeUpdate();

		pstmt.close() ;
		databaseManager.releaseConnection(DATABASE_NAME,dbcon);

		if (nRowCount > 0) {
			return true;
		}
		return false;
	}

	/**
	 *  Delete the given connection.
	 *
	 *	@param DBExternalConnection connection, the connection to delete.
	 *	@return boolean, true if it was successful, else false.
	 *	@throws java.sql.SQLException
	 */
	public boolean deleteConnection(ExternalConnection connection) throws SQLException {

		DBConnection dbcon = null;
       	dbcon = databaseManager.requestConnection(DATABASE_NAME);
		Connection con = dbcon.getConnection();
		if (con == null)
			return false;

		PreparedStatement pstmt = con.prepareStatement(DELETE_CONNECTION_QUERY);
		pstmt.setString(1, connection.getProfile());
		pstmt.setInt(2, connection.getType());

		int nRowCount = pstmt.executeUpdate();
		pstmt.close() ;
		databaseManager.releaseConnection(DATABASE_NAME,dbcon);

		if (nRowCount > 0) {
			return true;
		}
		return false;
	}
}
