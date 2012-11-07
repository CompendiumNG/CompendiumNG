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

import javax.swing.*;

import java.util.Vector;
import java.util.StringTokenizer;
import java.util.Hashtable;
import java.net.InetAddress;

import com.compendium.core.*;

/*
 * Handles Converting/Importing a Derby Compendium database project to a MySQL database project.
 * It allows external classes to register DBProgressListeners and fires appropriate progress information to them.
 * This facilitates the display of progress information in a user interface, if desired.
 *
 * @author Michelle Bachler
 */
public class DBConvertDerbyToMySQLDatabase  extends DBCopyData {


	/** The name of the Access database we are converting from.*/
	private String 				sFromName = "";

	/** The actual name of the MySQL database we are converting the data into.*/
	private String 				sToName = "";

	/** The user friendly name of the MySQL database we are converting the data into.*/
	private String 				sFriendlyName = "";

	/** A reference to the instance of DBAdminDatabase that this class will use*/
	private DBAdminDatabase		adminDatabase = null;

	/** The name to use whn accessing the database */
	private String sDatabaseUserName = ICoreConstants.sDEFAULT_DATABASE_USER;

	/** The password to use when accessing the database */
	private String sDatabasePassword = ICoreConstants.sDEFAULT_DATABASE_PASSWORD;

	/** The password to use when accessing the database */
	private String sDatabaseIP = ICoreConstants.sDEFAULT_DATABASE_ADDRESS;


	/**
	 * This constructor takes a name and password to use when accessing the database,
	 * and the IP address of the server machine.
	 *
	 * @param admin, the DBAdminDatabase instance to use to register the new database created.
	 * @param sDatabaseUserName, the name to use when creating the connection to the database
	 * @param sDatabasePassword, the password to use when connection to the database
	 * @param sDatabaseIP, the IP address of the database server machine.
	 */
	public DBConvertDerbyToMySQLDatabase(DBAdminDatabase admin, String sDatabaseUserName, String sDatabasePassword, String sDatabaseIP) {

		this.sFromName = sFromName;
		this.sToName = sToName;
		this.sFriendlyName = sFriendlyName;

		this.adminDatabase = admin;

		this.sDatabaseUserName = sDatabaseUserName;
		this.sDatabasePassword = sDatabasePassword;

		if (sDatabaseIP != null && !sDatabaseIP.equals(""))
			this.sDatabaseIP = sDatabaseIP;
	}

	/**
	 * Convert the given Derby database to MySQL.
	 *
	 * @param String sFromName, the name of the database to convert.
	 * @param String sFriendlyToName, the name of the database to create to convert the data into.
	 * @param String sFriendlyName, the user known name of the database to convert the data from.
	 * @exception java.lang.ClassNotFoundException
	 * @exception java.sql.SQLException
	 * @exception DBDatabaseNameException, thrown if a database with the name given in the constructor already exists.
	 * @exception DBDatabaseTypeException, thrown if a database connection of the specific type cannot be created.
	 * @exception DBProjectListException, thrown if the list of projects could not be loaded from the database.
	 */
	public void copyDatabase(String sFromName, String sFriendlyToName, String sFriendlyFromName) throws ClassNotFoundException, SQLException, DBDatabaseNameException, DBDatabaseTypeException, DBProjectListException {

		this.sFromName = sFromName;
		this.sFriendlyName = sFriendlyToName;

		String sCleanName = CoreUtilities.cleanDatabaseName(sFriendlyToName);
		this.sToName = sCleanName.toLowerCase();

 		Connection inCon = DBConnectionManager.getDerbyConnection(sFromName);
		if (inCon == null) {
			throw new DBDatabaseTypeException("Derby connection not established");
		}

		DBEmptyDatabase empty = new DBEmptyDatabase(ICoreConstants.MYSQL_DATABASE, adminDatabase, sDatabaseUserName, sDatabasePassword, sDatabaseIP);
		empty.addProgressListener(this);
		empty.createEmptyMySQLDatabase(sToName);

		Connection outCon = DBConnectionManager.getMySQLConnection(sToName, sDatabaseUserName, sDatabasePassword, sDatabaseIP);
		if (outCon == null) {
			throw new DBDatabaseTypeException("MySQL connection not established");
		}

		fireProgressCount(30);

		copyTables(inCon, outCon);

		try {
			inCon.close();
			outCon.close();
		}
		catch(ConcurrentModificationException io) {
			System.out.println("closing connections exception in Convert Derby To My SQL database: \n\n"+io.getMessage());
		}

		adminDatabase.addNewDatabase(this.sFriendlyName, this.sToName);

		fireProgressUpdate(increment, "Finished");
		fireProgressComplete();
	}
}
