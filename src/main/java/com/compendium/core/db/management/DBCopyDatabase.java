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
import java.util.Vector;

import com.compendium.core.*;

/*
 * Handles Copying a database.
 * It allows external classes to register DBProgressListeners and fires appropriate progress information to them.
 * This facilitates the display of progress information in a user interface, if desired.
 *
 * @author Michelle Bachler
 */
public class DBCopyDatabase extends DBCopyData implements DBConstants, DBProgressListener {


	/** The name of the database to copy from.*/
	private String 				sFromName 		= "";

	/** The name of the database to copy to.*/
	private String 				sToName 		= "";

	/** A reference to the instance of DBAdminDatabase that this class will use*/
	private DBAdminDatabase		adminDatabase	= null;

	/** The name to use whn accessing the database */
	private String sDatabaseUserName = ICoreConstants.sDEFAULT_DATABASE_USER;

	/** The password to use when accessing the database */
	private String sDatabasePassword = ICoreConstants.sDEFAULT_DATABASE_PASSWORD;

	/** The password to use when accessing the database */
	private String sDatabaseIP = ICoreConstants.sDEFAULT_DATABASE_ADDRESS;

	/** The type of the databasse application to create an empty database for.*/
	private int nDatabaseType = -1;


	/**
	 * Constructor, creates an empty Vector to hold the DBProgressListeners which may be registered.
	 * This constructor also takes a name and password to use when accessing the MySQL database,
	 * and the IP address of the MysqL server machine.
	 *
	 * @param nDatabaseType, the type of the database being used (e.g, MySQL, Derby).
	 * @param admin, a reference to the DBAdminDatabase object to use to register the new database.
	 * @param sDatabaseUserName, the name to use when creating the connection to the database
	 * @param sDatabasePassword, the password to use when connection to the database. Both databases are assumed to be in the same database application instance.
	 * @param sDatabaseIP, the IP address of the server machine. The default if 'localhost'.
	 */
	public DBCopyDatabase(int nDatabaseType, DBAdminDatabase admin, String sDatabaseUserName, String sDatabasePassword, String sDatabaseIP) {
		progressListeners = new Vector();
		adminDatabase = admin;
		this.sDatabaseUserName = sDatabaseUserName;
		this.sDatabasePassword = sDatabasePassword;
		this.nDatabaseType = nDatabaseType;
		if (sDatabaseIP != null && !sDatabaseIP.equals(""))
			this.sDatabaseIP = sDatabaseIP;
	}

	/**
	 * Copy the given database to the new given database name.
	 *
	 * @param String sFromName, the name of the database to convert.
	 * @param String sFriendlyToName, the name of the database to create to convert the data into.
	 * @param String sFriendlyFromName, the user known name of the database to convert the data from.
	 * @exception java.lang.ClassNotFoundException
	 * @exception java.sql.SQLException
	 * @exception DBDatabaseNameException, thrown if a database with the name given in the constructor already exists.
	 * @exception DBDatabaseTypeException, thrown if a database connection of the specific type cannot be created.
	 * @exception DBProjectListException, thrown if the list of projects could not be loaded from the database.
	 */
	public void copyDatabase(String sFromName, String sFriendlyToName, String sFriendlyFromName) 
			throws ClassNotFoundException, SQLException, DBDatabaseNameException, DBDatabaseTypeException, DBProjectListException {

		this.sFromName = sFromName;
		String sCleanName = CoreUtilities.cleanDatabaseName(sFriendlyToName);
		this.sToName = sCleanName.toLowerCase();

		Connection inCon = DBConnectionManager.getPlainConnection(nDatabaseType, sFromName, sDatabaseUserName, sDatabasePassword, sDatabaseIP);
		if (inCon == null) {
			throw new DBDatabaseTypeException("Database type "+nDatabaseType+" not found");
		}

		DBEmptyDatabase empty = new DBEmptyDatabase(nDatabaseType, adminDatabase, sDatabaseUserName, sDatabasePassword, sDatabaseIP);
		empty.addProgressListener(this);
		empty.createEmptyDatabase(sToName);

		Connection outCon = DBConnectionManager.getPlainConnection(nDatabaseType, sToName, sDatabaseUserName, sDatabasePassword, sDatabaseIP);
		if (outCon == null) {
			throw new DBDatabaseTypeException("Database type "+nDatabaseType+" not found");
		}

		fireProgressCount(29);

		copyTables(inCon, outCon);
		adminDatabase.addNewDatabase(sFriendlyToName, this.sToName);

		fireProgressUpdate(increment, "Finished");
		fireProgressComplete();

		try {
			inCon.close();
			outCon.close();
		}
		catch(ConcurrentModificationException io) {
			System.out.println("closing connections exception in copy database: \n\n"+io.getMessage());
		}
	}
}
