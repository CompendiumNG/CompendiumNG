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


package com.compendium.core.db.management;

import java.sql.*;
import java.util.*;

import javax.swing.JOptionPane;
import javax.swing.JFrame;

import com.compendium.core.db.management.*;
import com.compendium.core.datamodel.UserProfile;
import com.compendium.core.*;

/**
 * This class creates a new Empty Database.
 *
 * @author Michelle Bachler
 */
public class DBEmptyDatabase implements DBConstants, DBConstantsMySQL, DBConstantsDerby {

	/** A Vector of DBProgressListeners which have been registered with this object to recieve progress events*/
  	protected Vector progressListeners = new Vector();

	/** Holds the increment number used for the progress updates.*/
	private int					increment = 1;

	/**
	 * A reference to the DBAdministrationManager to use to register the newly created database
	 * when restoring a file to a new database
	 */
	private DBAdminDatabase		adminDatabase 		= null;

	/** The name to use whn accessing the MySQL database */
	private String 				databasename 			= ICoreConstants.sDEFAULT_DATABASE_USER;

	/** The password to use when accessing the MySQL database */
	private String 				databasepassword 		= ICoreConstants.sDEFAULT_DATABASE_PASSWORD;

	/** The password to use when accessing the MySQL database */
	private String 				databaseip 			= ICoreConstants.sDEFAULT_DATABASE_ADDRESS;

	/** The type of the databasse application to create an empty database for.*/
	private int nDatabaseType = -1;

	/** This array holds all the create table sql statements for the Derby database.*/
	public static final String DERBY_CREATE_TABLES[] = {
		CREATE_SYSTEM_TABLE, CREATE_USER_TABLE, CREATE_NODE_TABLE, CREATE_REFERENCE_TABLE,
		CREATE_CODE_TABLE, CREATE_LINK_TABLE, CREATE_VIEWNODE_TABLE, CREATE_NODEUSERSTATE_TABLE,
		CREATE_VIEWLINK_TABLE, CREATE_NODECODE_TABLE, CREATE_CODEGROUP_TABLE, CREATE_GROUPCODE_TABLE,
		CREATE_FAVORITE_TABLE, CREATE_WORKSPACE_TABLE, CREATE_WORKSPACEVIEW_TABLE, CREATE_AUDIT_TABLE,
		CREATE_CLONE_TABLE, CREATE_EXTENDEDNODE_TABLE, CREATE_EXTENDEDCODE_TABLE, CREATE_USERGROUP_TABLE,
		CREATE_GROUPUSER_TABLE, CREATE_PERMISSION_TABLE, CREATE_VIEWPROPERTY_TABLE, CREATE_NODEDETAIL_TABLE,
		CREATE_SHORTCUT_TABLE, CREATE_VIEWLAYER_TABLE, CREATE_CONNECTION_TABLE,
		CREATE_PREFERENCE_TABLE, CREATE_MEETING_TABLE, CREATE_MEDIAINDEX_TABLE
	};

	/** This array holds all the create table sql statements for the MySQL database.*/
	public static final String MYSQL_CREATE_TABLES[] = {
		MYSQL_CREATE_SYSTEM_TABLE, MYSQL_CREATE_USER_TABLE, MYSQL_CREATE_NODE_TABLE, MYSQL_CREATE_REFERENCE_TABLE,
		MYSQL_CREATE_CODE_TABLE, MYSQL_CREATE_LINK_TABLE, MYSQL_CREATE_VIEWNODE_TABLE, MYSQL_CREATE_NODEUSERSTATE_TABLE,
		MYSQL_CREATE_VIEWLINK_TABLE, MYSQL_CREATE_NODECODE_TABLE, MYSQL_CREATE_CODEGROUP_TABLE, MYSQL_CREATE_GROUPCODE_TABLE,
		MYSQL_CREATE_FAVORITE_TABLE, MYSQL_CREATE_WORKSPACE_TABLE, MYSQL_CREATE_WORKSPACEVIEW_TABLE, MYSQL_CREATE_AUDIT_TABLE,
		MYSQL_CREATE_CLONE_TABLE, MYSQL_CREATE_EXTENDEDNODE_TABLE, MYSQL_CREATE_EXTENDEDCODE_TABLE, MYSQL_CREATE_USERGROUP_TABLE,
		MYSQL_CREATE_GROUPUSER_TABLE, MYSQL_CREATE_PERMISSION_TABLE, MYSQL_CREATE_VIEWPROPERTY_TABLE, MYSQL_CREATE_NODEDETAIL_TABLE,
		MYSQL_CREATE_SHORTCUT_TABLE, MYSQL_CREATE_VIEWLAYER_TABLE, MYSQL_CREATE_CONNECTION_TABLE,
		MYSQL_CREATE_PREFERENCE_TABLE, MYSQL_CREATE_MEETING_TABLE, MYSQL_CREATE_MEDIAINDEX_TABLE
	};

	/**
	 * This constructor also takes a name and password to use when accessing the database,
	 * and the IP address of the server machine.
	 *
	 * @param nDatabaseType, the type of database application being used.
	 * @param DBAdminDatabase admin, a reference to the administration database so that the new database can be registered.
	 * @param sDatabaseName, the name to use when creating the connection to the database
	 * @param sDatabasePassword, the password to use when connection to the database.
	 * @param sDatabaseIP, the IP address of the MySQL server machine. The default if 'localhost'.
	 */
	public DBEmptyDatabase(int nDatabaseType, DBAdminDatabase admin, String sDatabaseName, String sDatabasePassword, String sDatabaseIP) {
		adminDatabase = admin;

		this.databasename = sDatabaseName;
		this.databasepassword = sDatabasePassword;
		this.nDatabaseType = nDatabaseType;
		if (sDatabaseIP != null && !sDatabaseIP.equals(""))
			this.databaseip = sDatabaseIP;

		progressListeners = new Vector();
	}

	/**
	 * Create all the empty tables for new database.
	 *
	 * @param String sName, the name requested for the new database.
	 * @return boolean true if the database was successful created else false.
	 * @exception com.compendium.core.db.management.DBDatabaseNameException, if the requested database name already exists.
	 * @exception java.lang.ClassNotFoundException
	 * @exception java.sql.SQLException
	 */
	public void createEmptyDatabase(String sName) throws DBDatabaseTypeException, DBDatabaseNameException, SQLException, ClassNotFoundException {

		// CHECK IF A DATABASE WITH THE GIVEN NAME ALREADY EXISTS.
		// THE NAME SHOULD BE CREATED UNIQUELY, BUT JUST IN CASE
		if (adminDatabase.hasDatabase(sName)) {
			throw new DBDatabaseNameException(sName);
		}
		else {

			Connection con = DBConnectionManager.getCreationConnection(nDatabaseType, sName, databasename, databasepassword, databaseip);
			if (con == null)
				throw new DBDatabaseTypeException("Database type "+nDatabaseType+" not found");

			createDatabaseTables(con);
			con.close();
		}
	}

	/**
	 * Create all the tables for the new database. Check type in Connection Manager.
	 *
	 * @param Connection con, the connection to use to create the tables.
	 * @exception java.sql.SQLException
	 */
	private void createDatabaseTables(Connection con) throws SQLException {

		if (nDatabaseType == ICoreConstants.DERBY_DATABASE) {
			createDerbyDatabaseTables(con);
		}
		else {
			createMySQLDatabaseTables(con);
		}
	}

	/**
	 * Create all the empty tables for new database on a MySQL server.
	 *
	 * @param String sName, the name requested for the new database.
	 * @exception com.compendium.core.db.management.DBDatabaseNameException, if the requested database name already exists.
	 * @exception java.lang.ClassNotFoundException
	 * @exception java.sql.SQLException
	 */
	public void createEmptyMySQLDatabase(String sName) throws DBDatabaseNameException, SQLException, ClassNotFoundException {

		if (adminDatabase.hasDatabase(sName)) {
			throw new DBDatabaseNameException(sName);
		}
		else {
			Connection con = DBConnectionManager.getMySQLCreationConnection(sName, databasename, databasepassword, databaseip);
			if (con == null) {
				throw new SQLException("A database connection could not be established");
			}
			else {
				createMySQLDatabaseTables(con);
				con.close();
			}
		}
	}

	/**
	 * Create all the empty tables for new database on a Derby server.
	 *
	 * @param String sName, the name requested for the new database.
	 * @exception com.compendium.core.db.management.DBDatabaseNameException, if the requested database name already exists.
	 * @exception java.lang.ClassNotFoundException
	 * @exception java.sql.SQLException
	 */
	public void createEmptyDerbyDatabase(String sName) throws DBDatabaseNameException, SQLException, ClassNotFoundException {

		if (adminDatabase.hasDatabase(sName)) {
			throw new SQLException("A database connection could not be established");
		}
		else {
			Connection con = DBConnectionManager.getDerbyCreationConnection(sName);
			if (con == null) {
				throw new SQLException("A database connection could not be established");
			}
			else {
				createDerbyDatabaseTables(con);
				con.close();
			}
		}
	}

	/**
	 * Create all the tables for the new MySQL database.
	 *
	 * @param Connection con, the connection to use to create the tables.
	 */
	private void createMySQLDatabaseTables(Connection con) throws SQLException {
		createTables(con, MYSQL_CREATE_TABLES);
	}

	/**
	 * Create all the tables for the new Derby database.
	 *
	 * @param Connection con, the connection to use to create the tables.
	 */
	private void createDerbyDatabaseTables(Connection con) throws SQLException {
		createTables(con, DERBY_CREATE_TABLES);
	}

	/**
	 * Create all the tables for a new database.
	 *
	 * @param Connection con, the connection to use to access the database.
	 * @param String[], the SQL string to create the tables from.
	 * @exception java.sql.SQLException
	 */
	private void createTables(Connection con, String[] tables) throws SQLException {

		PreparedStatement pstmt = null;

		fireProgressCount(tables.length);

		for (int i= 0; i < tables.length; i++) {

			fireProgressUpdate(increment, "Creating empty project");

			pstmt = con.prepareStatement(tables[i]);
			pstmt.executeUpdate() ;
			pstmt.close();
		}
	}

// PROGRESS LISTENER EVENTS

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

