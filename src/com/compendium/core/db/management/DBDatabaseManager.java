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
import java.sql.Connection;
import java.util.*;
import java.io.*;

import com.compendium.core.datamodel.*;
import com.compendium.core.datamodel.services.*;
import com.compendium.core.CoreUtilities;
import com.compendium.core.*;

/**
 * This class, with the help of the <code>DBConnectionManager</code>, opens connections for databases.
 * These connection objects can be requested by the services, and the services objects can
 * use these connection objects to call database operations.
 *
 * @author ? / Michelle Bachler
 */

public class DBDatabaseManager {

	/** A hashtable of database names and references to the connection manager objects for a database.*/
	private Hashtable htDatabases = new Hashtable() ;

	/** The name of the file in which to find the list of any old Compendium Access database*/
	private final String file = "database.ini";

	/** The name to use whn accessing the database */
	private String sDatabaseUserName = ICoreConstants.sDEFAULT_DATABASE_USER;

	/** The password to use when accessing the database */
	private String sDatabasePassword = ICoreConstants.sDEFAULT_DATABASE_PASSWORD;

	/** The password to use when accessing the database */
	private String sDatabaseIP = ICoreConstants.sDEFAULT_DATABASE_ADDRESS;

	/** The type of database being used */
	private int nDatabaseType = ICoreConstants.DERBY_DATABASE;

	/**
	 * This constructor is for use with Derby database.
	 */
	public DBDatabaseManager(int nDatabaseType) {
		this.nDatabaseType = nDatabaseType;
		this.sDatabaseUserName = "";
		this.sDatabasePassword = "";
		this.sDatabaseIP = "";
	}

	/**
	 * This constructor takes a name and password to use when accessing the database.
	 *
	 * @param sDatabaseUserName, the name to use when creating the connection to the database
	 * @param sDatabasePassword, the password to use when connection to the database.
	 */
	public DBDatabaseManager(int nDatabaseType, String sDatabaseUserName, String sDatabasePassword) {
		this.nDatabaseType = nDatabaseType;
		this.sDatabaseUserName = sDatabaseUserName;
		this.sDatabasePassword = sDatabasePassword;
	}

	/**
	 * This constructor takes a name and password to use when accessing the database,
	 * and the IP address of the MysqL server machine.
	 *
	 * @param sDatabaseUserName, the name to use when creating the connection to the database
	 * @param sDatabasePassword, the password to use when connection to the database.
	 * @param sDatabaseIP, the IP address of the server machine. The default if 'localhost'.
	 */
	public DBDatabaseManager(int nDatabaseType, String sDatabaseUserName, String sDatabasePassword, String sDatabaseIP) {
		this.nDatabaseType = nDatabaseType;
		this.sDatabaseUserName = sDatabaseUserName;
		this.sDatabasePassword = sDatabasePassword;
		this.sDatabaseIP = sDatabaseIP;
	}


	/**
	 * Return the type of database this object is Managing.
	 */
	public int getDatabaseType() {
		return nDatabaseType;
	}

	/**
	 * Check to see if there is a Database.ini file referencing Access Databases.
	 * This is needed for crossover from Access to MySQL.
	 *
	 * @return true if the file exists, false if it does not.
	 */
	// MB: 7th April 2005 - NOT USED ANYMORE. LEFT FOR A WHILE IN CASE NEED TO RETURN IT.
	public boolean hasAccessDatabases() {

		try {
			File databaseFile = new File(file);
			if (databaseFile.exists())
				return true;
		}
		catch (Exception e) {
			System.out.println("Exception: (DBDatabaseManager.hasAccessDatabases) \n\n"+e.getMessage());
		}

		return false;
	}

	/**
	 * Load the Access database references from the database.ini file.
	 */
	// MB: 7th April 2005 - NOT USED ANYMORE. LEFT FOR A WHILE IN CASE NEED TO RETURN IT.
	private void loadAccessDatabases() {

		htDatabases.clear();

		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(file));
		}
		catch (FileNotFoundException e) {
			return;
		}

		try {
			while (reader.ready()) {
				String line = reader.readLine();
				if (line == null) {
					break;
				}
				if (line.startsWith("DB")) {
					String dbinfo = line.substring(line.indexOf((int)'=')+1);

					int index = dbinfo.lastIndexOf((int)'\\');
					String odbcname = dbinfo.substring(index+1,dbinfo.indexOf((int)','));
					String display = (dbinfo.substring(line.indexOf((int)',')-1)).trim();

					htDatabases.put(display, odbcname);
				}
			}
			reader.close();
		}
		catch (IOException e) {}
	}

	/**
	 *	This method returns a list of all Compendium Access databases available.
	 *
	 *	@return String of project database names.
	 */
	// MB: 7th April 2005 - NOT USED ANYMORE. LEFT FOR A WHILE IN CASE NEED TO RETURN IT.
	public String getAccessProjects() {

		if (htDatabases.isEmpty())
			loadAccessDatabases();

		// SORT THE DATABASES NAME ALPHABETICALLY FIRST
		Vector databases = new Vector();
		for(Enumeration e = htDatabases.keys();e.hasMoreElements();) {
			databases.addElement((String)e.nextElement());
		}
		databases = CoreUtilities.sortList(databases);

		String projects = "";
		int count = databases.size();
		for (int i=0; i<count; i++) {
			if (i < count-1)
				projects = projects + (String)databases.elementAt(i)+",";
			else
				projects = projects + (String)databases.elementAt(i);
		}

		return projects;
	}

	/**
	 * This method opens a project by setting up a DBConnectionManager for the project.
	 *
	 * @param String sDatabaseName, the name of the database project to open a connection for.
	 * @return true, if connection opened, false if there was an error.
	 */
	public boolean openProject(String sDatabaseName) {

		if (htDatabases.containsKey(sDatabaseName)) {
			return true;
		}
		else {
			DBConnectionManager con = null;
			try {
				if (nDatabaseType == ICoreConstants.DERBY_DATABASE)
					con = new DBConnectionManager(nDatabaseType, sDatabaseName);
				else
					con = new DBConnectionManager(nDatabaseType, sDatabaseName, sDatabaseUserName, sDatabasePassword, sDatabaseIP);
			}
			catch(Exception ex) {
				System.out.println("Exception (DBDatabaseManager.openProject) \n\n"+ex.getMessage());
				return false;
			}
			htDatabases.put(sDatabaseName, con) ;
			return true ;
		}
	}

	/**
	 * This method is called by a service requesting a database connection and
	 * returns null if connection could not be obtained.
	 *
	 * @param String sDatabaseName, the name of the database to create a connection to.
	 * @return DBConnection, a connection to the spcified database, or null if the connection could not be made.
	 */
	public DBConnection requestConnection(String sDatabaseName) {

		// If the database has not been opened with connections, open it
		if (!htDatabases.containsKey(sDatabaseName)) {
			if(!openProject(sDatabaseName))
				return null ;	//could not get connection
		}

		// returns the ste of connections
		DBConnectionManager conSet = (DBConnectionManager)htDatabases.get(sDatabaseName);

		// returns the free connection
		return conSet.getConnection();
	}

	/**
	 * This method is called by a service to release a connection that it has been using.
	 *
	 * @param String sDatabaseName, the name of the database to release the connection for.
	 * @param DBConnection dbcon, the connection to release.
	 * @return boolean, true if the database was found, else false.
	 */
	public boolean releaseConnection(String sDatabaseName, DBConnection dbcon) {

		if (htDatabases.containsKey(sDatabaseName)) {
			DBConnectionManager conSet = (DBConnectionManager)htDatabases.get(sDatabaseName) ;
			conSet.releaseConnection(dbcon) ;
			return true;
		}
		else
			return false;
	}

	/**
	 * Removes all the connection relating to a database project,
	 * if the service manager has no active clients connected to the project.
	 *
	 * @param String sDatabaseName, the name of the database to release all connection for.
	 * @return boolean, true if the database was found, else false.
	 */
	public boolean removeAllConnections(String sDatabaseName) {

		if (htDatabases.containsKey(sDatabaseName)) {
			DBConnectionManager conSet = (DBConnectionManager)htDatabases.get(sDatabaseName);
			conSet.removeAllConnections();
			htDatabases.remove(sDatabaseName);
			return true;
		}
		else
			return false;
	}
}
