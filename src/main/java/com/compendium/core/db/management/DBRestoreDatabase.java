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

import javax.swing.JOptionPane;
import javax.swing.JFrame;
import javax.swing.JProgressBar;

import com.compendium.core.datamodel.*;
import com.compendium.core.*;

/**
 * This class restores the data in a given sql file into the named database.
 * It allows external classes to register DBProgressListeners and fires appropriate progress information to them.
 * This facilitates the display of progress information in a user interface, if desired.
 *
 * @author Michelle Bachler
 */
public class DBRestoreDatabase implements DBConstants, DBProgressListener, DBConstantsMySQL, DBConstantsDerby {

	/** A Vector of registerd progress listeners, to recieve progress updates*/
   	protected Vector progressListeners;

	private Model				model = null;

	/** The connection to use to load the data.*/
	private Connection 			connection;

	/** A count for the progress events to use*/
	private int					nCount = 5;

	/** The increment for the progress counters to use*/
	private int					increment = 1;

	/**
	 * A reference to the DBAdministrationManager to use to register the newly created database
	 * when restoring a file to a new database
	 */
	private DBAdminDatabase		adminDatabase = null;

	/** The name to use whn accessing the database */
	private String databasename = ICoreConstants.sDEFAULT_DATABASE_USER;

	/** The password to use when accessing the database */
	private String databasepassword = ICoreConstants.sDEFAULT_DATABASE_PASSWORD;

	/** The password to use when accessing the database */
	private String databaseip = ICoreConstants.sDEFAULT_DATABASE_ADDRESS;

	/** The type of the databasse application to create an empty database for.*/
	private int nDatabaseType = -1;
	
	/**
	 * This constructor takes a name and password to use when accessing the database,
	 * and the IP address of the server machine.
	 *
	 * @param nDatabaseType, the type of the database being used (e.g, MySQL, Derby).
	 * @param admin, the DBAdminDatabase instance to use to register the new database created.
	 * @param sDatabaseName, the name to use when creating the connection to the database.
	 * @param sDatabasePassword, the password to use when connection to the database.
	 * @param sDatabaseIP, the IP address of the server machine. The default if 'localhost'.
	 */
	public DBRestoreDatabase(int nDatabaseType, DBAdminDatabase admin, String sDatabaseName, String sDatabasePassword, String sDatabaseIP) {
		progressListeners = new Vector();
		this.adminDatabase = admin;
		this.databasename = sDatabaseName;
		this.databasepassword = sDatabasePassword;
		this.nDatabaseType = nDatabaseType;
		if (sDatabaseIP != null && !sDatabaseIP.equals(""))
			this.databaseip = sDatabaseIP;
	}

	/**
	 * Restore the data in the passed file to the database with the given name.
	 *
	 * @param String sName, the name of the database to restore to.
	 * @param File file, the file holding the sql statements to restore from.
	 * @param boolean fullRecreation, idicates whether to drop an recreate all tables - not currently used.
	 */
	public boolean restoreDatabase(String sName, File file, boolean fullRecreation) {

		fireProgressCount(100);
		fireProgressUpdate(increment, "Opening Connection..");

		boolean dataRestored = false;

		try {
  		    connection = DBConnectionManager.getPlainConnection(nDatabaseType, sName, databasename, databasepassword, databaseip);
 		    if (connection == null)
				throw new DBDatabaseTypeException("Database type "+nDatabaseType+" not found");
			
			deleteAllData(connection);		// Purge all tables of existing data before reloading

			if (dataRestored = loadData(connection, file)) {
				checkReferencePaths(connection);
			}
			fireProgressComplete();
			try {
				connection.close();
			}
			catch(ConcurrentModificationException io) {
				System.out.println("Exception closing connection for restore database:\n\n"+io.getMessage());
			}
	  	}
		catch (Exception ex) {
			ex.printStackTrace();
			fireProgressComplete();
			return false;
	  	}

		return dataRestored;
	}

	/**
	 * Restore the data in the passed File, to a new database with given database name.
	 *
	 * @param String sFriendlyName, the name of the new database to create and restore to.
	 * @param File file, the file holding the sql statements to restore from.
	 * @param boolean fullRecreation, indicates whether to drop an recreate all tables - not currently used.
	 * @exception java.lang.ClassNotFoundException
	 * @exception java.sql.SQLException
	 * @exception DBDatabaseNameException, thrown if a database with the name given in the constructor already exists.
	 * @exception DBDatabaseTypeException, thrown if a database connection of the specific type cannot be created.
	 * @exception DBProjectListException, thrown if the list of projects could not be loaded from the database.
	 */
	public boolean restoreDatabaseAsNew(String sFriendlyName, File file, boolean fullRecreation) throws ClassNotFoundException, SQLException, DBDatabaseNameException, DBDatabaseTypeException, DBProjectListException {

		String sToName = CoreUtilities.cleanDatabaseName(sFriendlyName);
		sToName = sToName.toLowerCase();

		boolean dataRestored = false;

		// ARE WE TRYING TO LOAD DATA FROM PRE THE TABLE NAME CHANGES OF VERSION 1.3.3
		// IF SO, DO NOT CREATE NEW EMPTY DATABASE FIRST
		String version = checkVersion(file);

		if (version.equals("")) {
			return false;
		}
		else if (version.equals("1.3") || version.equals("1.3.1") || version.equals("1.3.2")) {
			connection = DBConnectionManager.getCreationConnection(nDatabaseType, sToName, databasename, databasepassword, databaseip);
		}
		else {
			DBEmptyDatabase empty = new DBEmptyDatabase(nDatabaseType, adminDatabase, databasename, databasepassword, databaseip);
			empty.addProgressListener(this);

			fireProgressUpdate(increment, "Creating new database..");

			empty.createEmptyDatabase(sToName);
			connection = DBConnectionManager.getPlainConnection(nDatabaseType, sToName, databasename, databasepassword, databaseip);
			if (connection == null) {
				throw new DBDatabaseTypeException("Database type "+nDatabaseType+" not found");
			}
		}

		nCount = 5;
		fireProgressCount(100);

		dataRestored = loadData(connection, file);

		if (dataRestored) {
			adminDatabase.addNewDatabase(sFriendlyName, sToName);
			checkReferencePaths(connection);
			fireProgressComplete();
		}

		try {
			connection.close();
		}
		catch(ConcurrentModificationException io) {
			System.out.println("Exception closing connection for restore As New database:\n\n"+io.getMessage());
		}

		return dataRestored;
	}

	/**
	 * Check that all the reference and image path just loaded have the correct file separators for the current platform.
	 * Update the data where required.
	 *
	 * @param Connection con, the connection to the database to use to restore the data.
	 * @exception java.sql.SQLException
	 */
	private void checkReferencePaths(Connection con) throws SQLException {

		if (con == null)
			return;

		String platform = System.getProperty("os.name");
		String os = platform.toLowerCase();
		boolean isWindows = false;
		if (os.indexOf("windows") != -1) {
		    isWindows = true;
		}

		PreparedStatement pstmt = con.prepareStatement("SELECT NodeID, Source, ImageSource from ReferenceNode");
		ResultSet rs = pstmt.executeQuery();

		fireProgressUpdate(increment, "Checking reference paths correct..");

		if (rs != null) {

			String cleanSource = "";
			String cleanImage = "";

			while (rs.next()) {

				String nodeid		= rs.getString(1);
				String source		= rs.getString(2);
				String	image		= rs.getString(3);

				cleanSource = "";
				cleanImage = "";

				if (source != null && !source.equals("") && CoreUtilities.isFile(source)) {
					cleanSource = CoreUtilities.cleanPath(source, isWindows);
					if (!cleanSource.equals(source)) {
						String statement = "UPDATE ReferenceNode SET Source=? WHERE NodeID='"+nodeid+"'";
						PreparedStatement pstmt2 = con.prepareStatement(statement);
						pstmt2.setString(1, cleanSource);
						int nRowCount = pstmt2.executeUpdate();
						pstmt2.close();
						if (nRowCount == 0) {
							System.out.println("Unable to update source = "+cleanSource);
						}
					}
				}

				if (image != null && !image.equals("")) {
					cleanImage = CoreUtilities.cleanPath( image, isWindows );
					if (!cleanImage.equals(image)) {
						String statement = "UPDATE ReferenceNode SET ImageSource=? WHERE NodeID='"+nodeid+"'";
						PreparedStatement pstmt3 = con.prepareStatement(statement);
						pstmt3.setString(1, cleanImage);
						int nRowCount = pstmt3.executeUpdate();
						pstmt3.close();
						if (nRowCount == 0) {
							System.out.println("Unable to update image = "+cleanImage);
						}
					}
				}
			}
		}
	}

	/**
	 * Load and restore the data in the given File, using the given Connection.
	 *
	 * @param Connection con, the connection to the database to use to restore the data.
	 * @param File file, the file object to get the sql statements from to restore the data.
	 * @return boolean, true if the restoration was successful, else false.
	 */
	private boolean loadData(Connection con, File file) {

		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(file));
		}
		catch (FileNotFoundException e) {
			fireProgressAlert("The sql data file could not be found. Restoration cancelled.");
			return false;
		}

		Vector statements = new Vector(51);
		String version = "";
		String line = "";
		String header = "";
		int		nLines = 0;

// Make a first pass through the file, counting the number of lines in it
// and picking out the header and version for later use
		
		try {
			fireProgressUpdate(increment, "Scanning data..");
			int ind = 0;

			while (reader.ready()) {
				line = reader.readLine();
				nLines++;
				if (line.startsWith(DBBackupDatabase.MYSQL_DATABASE_HEADER_CHECK) 
						|| line.startsWith(DBBackupDatabase.DERBY_DATABASE_HEADER_CHECK)) {
					header = line;
							
					// Compendium 1.4.2 and later only this was introduced
					ind = line.indexOf(":");
					if (ind != -1) {
						version = line.substring(ind+1);
					}
				}				
			}
// Close and reopen the file (do it this way since you can't 'seek' or 'rewind' a BufferedReader
// Error checking not necessary since we've already successfully opened it once

			reader.close();
			reader = new BufferedReader(new FileReader(file));
			
			if (nDatabaseType == ICoreConstants.DERBY_DATABASE && header.startsWith(DBBackupDatabase.MYSQL_DATABASE_HEADER_CHECK)) {
				fireProgressAlert("This backup file is for a MySQL database project");
				return false;
			}
			else if (nDatabaseType == ICoreConstants.MYSQL_DATABASE && header.startsWith(DBBackupDatabase.DERBY_DATABASE_HEADER_CHECK)) {
				fireProgressAlert("This backup file is for a Derby database project");
				return false;
			}
			fireProgressCount(nLines);
			
			Statement stmt = con.createStatement();
			int nRowCount = 0;
			int	Lines = 0;
			
			while (reader.ready()) {
				line = reader.readLine();
				
				//Don't process the header line
				if (line.startsWith(DBBackupDatabase.MYSQL_DATABASE_HEADER_CHECK) 
						|| line.startsWith(DBBackupDatabase.DERBY_DATABASE_HEADER_CHECK)) {
					continue;
				}
				Lines++;
				fireProgressUpdate(increment, "Adding database records...");
				if (line != null) {
					line = line.trim();
					if (!line.equals("")) {
						if (!line.startsWith("DROP") && !line.startsWith("CREATE")) {		// Skip over troublesome statements
							line = line.replace("\\n", "\n");
							line = line.replace("\\\\n", "\n");
							line = line.replace("\\r", "\r");
							line = line.replace("\\\\r", "\r");
							nRowCount = 0;
							try {
								nRowCount = stmt.executeUpdate(line);
							}
							catch(Exception ex) {
								System.out.println("Problem with restoring = "+ex.getMessage());
								ex.printStackTrace();
							}
						}
					}
				}
			}
			reader.close();

			/*if (version.equals("1.3.9")) { // == 1.4.2
				JOptionPane.showMessageDialog(null, "This backup file is from a previous version\nof the Compendium database.\n\nIt cannot be used to restore from.", "Restoration error", JOptionPane.INFORMATION_MESSAGE);
				return false;
			}*/
		

			stmt.close();
			return true;
		}
		catch (IOException e) {
			fireProgressAlert("There has been a problem loading the data:\n\n"+e.getMessage());
  			e.printStackTrace();
			return false;
		}
		catch (SQLException e) {
			fireProgressAlert("There has been a problem loading the data:\n\n"+e.getMessage());
  			e.printStackTrace();
			return false;
  		}
	}
	
	private void deleteAllData(Connection con) throws SQLException {				
		if (nDatabaseType == ICoreConstants.DERBY_DATABASE) {
			dropTables(con, DERBY_DROP_TABLES);
		} else if (nDatabaseType == ICoreConstants.MYSQL_DATABASE) {
			dropTables(con, MYSQL_DROP_TABLES);
		}
	}
	
	/**
	 * Drop all the tables for the current database.
	 *
	 * @param Connection con, the connection to use to access the database.
	 * @param String[], the SQL strings to drop the tables with.
	 * @exception java.sql.SQLException
	 */
	private void dropTables(Connection con, String[] tables) throws SQLException {
		PreparedStatement pstmt = null;
		for (int i= 0; i < tables.length; i++) {
			pstmt = con.prepareStatement(tables[i]);
			pstmt.executeUpdate() ;
			pstmt.close();
		}
	}

	/**
	 * Check which version of the database schema the data to load is from.
	 *
	 * @param File file, the file object to get the sql statements from to restore the data.
	 * @return boolean, true if the restoration was successful, else false.
	 */
	private String checkVersion(File file) {

		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(file));
		}
		catch (FileNotFoundException e) {
			fireProgressAlert("The sql data file could not be found. Restoration cancelled.");
			return new String("");
		}

		String version = "";
		String line = "";

		try {
			fireProgressUpdate(increment, "Checking data version..");

			while (reader.ready()) {
				line = reader.readLine();
				if (line != null) {
					line = line.trim();
					if (!line.equals("")) {
						if (line.startsWith("INSERT INTO System")) {
							int cut = line.indexOf("VALUES ('version'");
							if (cut != -1) {
								int bracket = line.indexOf(")", cut);
								version = line.substring(cut+19, bracket-1);
								return version;
							}
						}
					}
				}
			}
			reader.close();

			return new String("");
		}
		catch (IOException e) {
			fireProgressAlert("There has been a problem loading the data:\n\n"+e.getMessage());
  			e.printStackTrace();
			return new String("");
		}
	}

// IMPLEMENT PROGRESS LISTENER

	/**
	 * Set the amount of progress items being counted.
	 *
	 * @param int nCount, the amount of progress items being counted.
	 */
    public void progressCount(int nCount) {
		fireProgressCount(nCount);
	}

	/**
	 * Indicate that progress has been updated.
	 *
	 * @param int nIncrement, the current position of the progress in relation to the inital count
	 * @param String sMessage, the message to display to the user
	 */
    public void progressUpdate(int nIncrement, String sMessage) {
		fireProgressUpdate(nIncrement, sMessage);
	}

	/**
	 * Indicate that progress has complete.
	 */
    public void progressComplete() {
		fireProgressComplete();
	}

	/**
	 * Indicate that progress has had a problem.
	 *
	 * @param String sMessage, the message to display to the user.
	 */
    public void progressAlert(String sMessage) {
		fireProgressAlert(sMessage);
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
