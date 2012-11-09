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
import java.io.*;
import java.util.*;
import java.net.URL;

import javax.swing.JOptionPane;
import javax.swing.JFrame;
import javax.swing.JProgressBar;

import com.compendium.core.datamodel.*;
import com.compendium.core.db.DBSystem;
import com.compendium.core.db.DBNodeUserState;
import com.compendium.core.*;

/**
 * This class handles creating a new database, adds the default data to it and if required, a new user record.
 * It allows external classes to register DBProgressListeners and fires appropriate progress information to them.
 * This facilitates the display of progress information in a user interface, if desired.
 *
 * @author Michelle Bachler
 */
public class DBNewDatabase implements DBProgressListener {

	/** SQL statement to insert the default database version number into the System table of the database*/
	public static final String INSERT_SYSTEM_QUERY = "UPDATE System set Contents = '"+ICoreConstants.sDATABASEVERSION+"' WHERE Property='version'";

	/** NOT CURRENTLY USED */
	public static final String INSERT_PREFERENCE_QUERY1 = "INSERT INTO Preference (UserID, Property, Contents) VALUES (?, 'codegroup', '')";

	/** NOT CURRENTLY USED */
	public static final String INSERT_PREFERENCE_QUERY2 = "INSERT INTO Preference (UserID, Property, Contents) VALUES (?, 'LAF', '')";

	/** NOT CURRENTLY USED */
	public static final String INSERT_PREFERENCE_QUERY3 = "INSERT INTO Preference (UserID, Property, Contents) VALUES (?, 'Skin', '')";

	/** NOT CURRENTLY USED */
	public static final String INSERT_PREFERENCE_QUERY4 = "INSERT INTO Preference (UserID, Property, Contents) VALUES (?, 'DnDFiles', 'N')";

	/** NOT CURRENTLY USED */
	public static final String INSERT_PREFERENCE_QUERY5 = "INSERT INTO Preference (UserID, Property, Contents) VALUES (?, 'AudioOn', 'N')";

	/** SQL statement to check the default view node exits before adding to the desktop of the newly created user*/
	//public static final String SELECT_DEFAULT_NODE =
	//	"SELECT NodeID FROM Node WHERE NodeID = '13710825351068804681614'";

	/** SQL statement to add the default view node to the desktop of the newly created user, if there is one*/
	//public static final String INSERT_DEFAULT_NODE =
	//	"INSERT INTO ViewNode (ViewID, NodeID, XPos, YPos, CreationDate, ModificationDate, CurrentStatus)"+
	//	"VALUES (? , '13710825351068804681614', 99 , 7, ?, ?, 0)"; 

	/** SQL statement to check the default view node exits before adding to the desktop of the newly created user*/
	public static final String SELECT_DEFAULT_NODE =
		"SELECT NodeID FROM Node WHERE NodeID = '137108251921165929909344'";

	/** SQL statement to add the default view node to the desktop of the newly created user, if there is one*/
	public static final String INSERT_DEFAULT_NODE =
		"INSERT INTO ViewNode (ViewID, NodeID, XPos, YPos, CreationDate, ModificationDate, CurrentStatus)"+
		"VALUES (? , '137108251921165929909344', 200 , 7, ?, ?, 0)"; 

	/** SQL statement to add the default view node to the desktop of the newly created user, if there is one*/
	public static final String INSERT_DEFAULT_NODE2 =
		"INSERT INTO ViewNode (ViewID, NodeID, XPos, YPos, CreationDate, ModificationDate, CurrentStatus)"+
		"VALUES (? , '137108251921158578648470', 99 , 7, ?, ?, 0)"; 

	/** SQL statement to insert the homeview node for the newly created user*/
	public final static String INSERT_NODE_QUERY =
		"INSERT INTO Node (NodeID, NodeType, ExtendedNodeType, OriginalID, Author, " +
		"CreationDate, ModificationDate, Label, Detail, CurrentStatus) "+
		"VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ";

	/** SQL statement to insert a new user into the new database*/
	public final static String INSERT_USER_QUERY =
		"INSERT INTO Users (UserID, Author, CreationDate, ModificationDate, " +
		"Login, Name, Password, Description, " +
		"HomeView, IsAdministrator) "+
		"VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ";

	/** SQL statement to install a code to represent the newly created user name*/
	public final static String INSERT_CODE_QUERY =
		"INSERT INTO Code (CodeID, Author, CreationDate, ModificationDate, Name, Description, Behavior) "+
		"VALUES (?, ?, ?, ?, ?, ?, ?) ";

	/** Set state to READSTATE for all records.*/
	private static final String UPDATE_NODEUSERSTATE_TABLE = "UPDATE NodeUserState set State = "+ICoreConstants.READSTATE;
		
	/** the name of the file containing the default data for a new database in Derby*/
	public static final String DERBY_DEFAULT_DATA_FILE 	= "DefaultDataDerby.sql";

	/** the name of the file containing the default data for a new database in MySQL*/
	public static final String MYSQL_DEFAULT_DATA_FILE 	= "DefaultDataMySQL.sql";

	/**A reference to the system file path separator*/
	private final static String	sFS					= System.getProperty("file.separator");

	/**
	 * An integer representing the total count of the progress updates required,
	 * There is one for each SQL statement in the data file.
	 */
	public static final int DEFAULT_DATA_COUNT 		= 375;

	/** An integer representing the increment to use for the progress updates */
	private int					increment 		= 1;

	/** A Vector of registered DBProgressListeners */
   	protected Vector progressListeners;

	/** The UserProfile for a new user to be added to the newly created database*/
	private UserProfile			userProfile 	= null;

	/** A local reference for the DBAdminDatabase object which is required to register the new database*/
	private DBAdminDatabase		adminDatabase 	= null;

	/** The name to use whn accessing the MySQL database */
	private String sDatabaseUserName = ICoreConstants.sDEFAULT_DATABASE_USER;

	/** The password to use when accessing the MySQL database */
	private String sDatabasePassword = ICoreConstants.sDEFAULT_DATABASE_PASSWORD;

	/** The password to use when accessing the MySQL database */
	private String sDatabaseIP = ICoreConstants.sDEFAULT_DATABASE_ADDRESS;

	/** Indicates if one or more external resources being backed up could not be found*/
	private boolean bNotFound = false;

	/** The given user is the default user for this new database*/
	private boolean isDefaultUser = false;

	/** The type of the databasse application to create an empty database for.*/
	private int nDatabaseType = -1;


	/**
	 * This constructor takes a name and password to use when accessing the database,
	 * and the IP address of the server machine.
	 *
	 * @param nDatabaseType, the type of the database being used (e.g, MySQL, Derby).
	 * @param DBAdminDatabase admin, a reference to the administration database so that the new database can be registered.
	 * @param sDatabaseUserName, the name to use when creating the connection to the database
	 * @param sDatabasePassword, the password to use when connection to the database
	 * @param sDatabaseIP, the IP address of the server machine. The default if 'localhost'.
	 */
	public DBNewDatabase(int nDatabaseType, DBAdminDatabase admin, String sDatabaseUserName, String sDatabasePassword, String sDatabaseIP) {

		progressListeners = new Vector();
		this.adminDatabase = admin;
		this.sDatabaseUserName = sDatabaseUserName;
		this.sDatabasePassword = sDatabasePassword;
		this.nDatabaseType = nDatabaseType;

		if (sDatabaseIP != null && !sDatabaseIP.equals("")) {
			this.sDatabaseIP = sDatabaseIP;
		}
	}

	/**
	 * Constructor, takes the user profile to add to the newly created database.
	 * This constructor takes a name and password to use when accessing the database,
	 * and the IP address of the server machine.
	 *
	 * @param nDatabaseType, the type of the database being used (e.g, MySQL, Derby).
	 * @param DBAdminDatabase admin, a reference to the administration database so that the nwe database can be registered.
	 * @param UserProile up, the UserProfile of a new user to be added to the newly created database.
	 * @param sDatabaseUserName, the name to use when creating the connection to the database
	 * @param sDatabasePassword, the password to use when connection to the database
	 * @param sDatabaseIP, the IP address of the server machine. The default if 'localhost'.
	 */
	public DBNewDatabase(int nDatabaseType, DBAdminDatabase admin, UserProfile up, boolean isDefaultUser, String sDatabaseUserName, String sDatabasePassword, String sDatabaseIP) {

		progressListeners = new Vector();
		userProfile = up;
		this.isDefaultUser = isDefaultUser;
		this.adminDatabase = admin;
		this.sDatabaseUserName = sDatabaseUserName;
		this.sDatabasePassword = sDatabasePassword;
		this.nDatabaseType = nDatabaseType;

		if (sDatabaseIP != null && !sDatabaseIP.equals("")) {
			this.sDatabaseIP = sDatabaseIP;
		}
	}

	/**
	 * Create a new database, and then load the default data.
	 * If a user was specified in the constructor, add the new user details to the new database.
	 *
	 * @param String sFriendlyName, the name of the database as seen by a Compendium user.
	 * This name is 'cleaned' to remove illegal characters and a time/date stamp is addded.
	 * @exception java.sql.SQLException
	 * @exception java.io.IOException
	 * @exception java.io.FileNotFoundLException
	 * @exception java.lang.ClassNotFoundException
	 * @see com.compendium.core.CoreUtilities#cleanDatabaseName
	 */
	public void createNewDatabase(String sFriendlyName)
			throws DBDatabaseNameException, DBDatabaseTypeException, ClassNotFoundException, IOException, SQLException, FileNotFoundException  {

		String sCleanName = CoreUtilities.cleanDatabaseName(sFriendlyName);

		DBEmptyDatabase empty = new DBEmptyDatabase(nDatabaseType, adminDatabase, sDatabaseUserName, sDatabasePassword, sDatabaseIP);
		empty.addProgressListener(this);

		Connection 	connection = null;

		empty.createEmptyDatabase(sCleanName);
		connection = DBConnectionManager.getPlainConnection(nDatabaseType, sCleanName, sDatabaseUserName, sDatabasePassword, sDatabaseIP);
		if (connection == null) {
			throw new DBDatabaseTypeException("Database type "+nDatabaseType+" not found");
		}

		fireProgressCount(DEFAULT_DATA_COUNT);

		insertDefaultData(connection);
		
		fireProgressUpdate(increment, "Finished");
		fireProgressComplete();

		adminDatabase.addNewDatabase(sFriendlyName, sCleanName);
		if (userProfile != null) {
			insertNewUser(connection);
			if (isDefaultUser) {
				//System.out.println("About to add default user as"+userProfile.getId());
				DBSystem.setDefaultUser(new DBConnection(connection, true, nDatabaseType), userProfile.getId());
			}
		}

		try {
			connection.close();
		}
		catch(ConcurrentModificationException io) {
			System.out.println("Exception closing connection for new database:\n\n"+io.getMessage());
		}
	}

	/**
	 * If new user details where passed into the contructor, set up the new user and their home page.
	 *
	 * @param Connection con, the connection to use to write the new user information to the database.
	 * @exception java.sql.SQLException
	 */
	private void insertNewUser(Connection con) throws SQLException {

		if (con == null)
			throw new SQLException("A database connection could not be established to create the new user.");

		String name = userProfile.getUserName();
		java.util.Date date = new java.util.Date();
		String homeViewId = Model.getStaticUniqueID();

		PreparedStatement pstmt = con.prepareStatement(INSERT_NODE_QUERY);

		pstmt.setString(1, homeViewId) ;
		pstmt.setInt(2, ICoreConstants.MAPVIEW);
		pstmt.setString(3, "") ;
		pstmt.setString(4, "");
		pstmt.setString(5, name);
		pstmt.setDouble(6, new Long(date.getTime()).doubleValue());
		pstmt.setDouble(7, new Long(date.getTime()).doubleValue());

		//ByteArrayInputStream bArrayLabel = new ByteArrayInputStream(new String("Home Window").getBytes());
		//pstmt.setAsciiStream(8, bArrayLabel, bArrayLabel.available());

		//ByteArrayInputStream bArrayDetail = new ByteArrayInputStream(new String("Home Window of " + name).getBytes());
		//pstmt.setAsciiStream(9, bArrayDetail, bArrayDetail.available());

		// ACCOMODATES UNICODE
		String sLabel = new String("Home Window");
		StringReader reader = new StringReader(sLabel);
		pstmt.setCharacterStream(8, reader, sLabel.length());

		// ACCOMODATES UNICODE
		sLabel = new String("Home Window of " + name);
		reader = new StringReader(sLabel);
		pstmt.setCharacterStream(9, reader, sLabel.length());

		pstmt.setInt(10, ICoreConstants.STATUS_ACTIVE);

		int nRowCount = pstmt.executeUpdate();

		pstmt.close();

		if (nRowCount >0) {

			pstmt = con.prepareStatement(INSERT_CODE_QUERY);

	 		pstmt.setString(1, Model.getStaticUniqueID());
			pstmt.setString(2, name) ;
			pstmt.setDouble(3, new Long(date.getTime()).doubleValue());
			pstmt.setDouble(4, new Long(date.getTime()).doubleValue());
			pstmt.setString(5, name) ;
			pstmt.setString(6, "No Description");
			pstmt.setString(7, "No Behavior") ;

			nRowCount = pstmt.executeUpdate();

			// close pstmt to save resources
			pstmt.close() ;

			if (nRowCount > 0) {
				pstmt = con.prepareStatement(INSERT_USER_QUERY);

				String id = Model.getStaticUniqueID();
				userProfile.setId(id);
				String login = userProfile.getLoginName();
				String desc = userProfile.getUserDescription();
				String password = userProfile.getPassword();

				String admin = "N";
				if (userProfile.isAdministrator())
					admin = "Y";

				pstmt.setString(1, id);
				pstmt.setString(2, name);
				pstmt.setDouble(3, new Long(date.getTime()).doubleValue());
				pstmt.setDouble(4, new Long(date.getTime()).doubleValue());
				pstmt.setString(5, login);
				pstmt.setString(6, name);
				pstmt.setString(7, password);
				pstmt.setString(8, desc);
				pstmt.setString(9, homeViewId);
				pstmt.setString(10, admin);

				nRowCount = pstmt.executeUpdate();
				pstmt.close();

				if (nRowCount >0) {					
					
					// CHECK DEFAULT NODE EXISTS AND IF IT DOES, INSERT ON USERS DESKTOP
					pstmt = con.prepareStatement(SELECT_DEFAULT_NODE);
					ResultSet rs = pstmt.executeQuery();
					if (rs != null) {
						String sId = "";
						while (rs.next()) {
							sId	= rs.getString(1) ;
						}

						if (!sId.equals("")) {
							// INSERT DEFAULT NODE ON DESKTOP - QUICK START
							pstmt = con.prepareStatement(INSERT_DEFAULT_NODE);
					 		pstmt.setString(1, homeViewId);
							pstmt.setDouble(2, new Long(date.getTime()).doubleValue());
							pstmt.setDouble(3, new Long(date.getTime()).doubleValue());
							nRowCount = pstmt.executeUpdate();
							pstmt.close();
							
							// INSERT DEFAULT NODE ON DESKTOP - FUNDERS
							pstmt = con.prepareStatement(INSERT_DEFAULT_NODE2);
					 		pstmt.setString(1, homeViewId);
							pstmt.setDouble(2, new Long(date.getTime()).doubleValue());
							pstmt.setDouble(3, new Long(date.getTime()).doubleValue());
							nRowCount = pstmt.executeUpdate();
							pstmt.close();							
						}
					}
					
					// SET THE STATE FOR ALL NODES FOR THIS USER AS READ.
					pstmt = con.prepareStatement("SELECT NodeID FROM Node");
					rs = pstmt.executeQuery();

					String sNodeID = "";				
					while (rs.next()) {
						sNodeID	= rs.getString(1);
						
						pstmt = con.prepareStatement(DBNodeUserState.INSERT_STATE_QUERY);
				 		pstmt.setString(1, sNodeID);
						pstmt.setString(2, id) ;
						pstmt.setInt(3, ICoreConstants.READSTATE) ;
						pstmt.executeUpdate();
					}																	
				}
			}
		}
	}

	/**
	 * Add the default data to the newly created database.
	 *
	 * @param Connection con, the connection to use to write the default data to the database.
	 * @exception java.sql.SQLException
	 * @exception java.io.IOException
	 * @exception java.io.FileNotFoundLException
	 */
	private void insertDefaultData(Connection con) throws IOException, SQLException, FileNotFoundException {

		String defaultFile = DERBY_DEFAULT_DATA_FILE;
		if (nDatabaseType == ICoreConstants.MYSQL_DATABASE)
			defaultFile = MYSQL_DEFAULT_DATA_FILE;

		InputStream stream = ClassLoader.getSystemResourceAsStream("com/compendium/core/db/management/"+defaultFile);

		BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
		String line = reader.readLine();

		while (line != null) {
			line = line.trim();
			if (!line.equals("")) {

				//System.out.println("line = "+line);

				Statement stmt = con.createStatement();
				int nRowCount = stmt.executeUpdate(line) ;
				stmt.close();

				if (nRowCount <= 0) {
					System.out.println("failed to run sql = "+line);
				}
			}
			fireProgressUpdate(increment, "Loading default data..");

			line = reader.readLine();
		}
		stream.close();
		reader.close();

		// UPDATE THE DATABASE VERSION
		PreparedStatement pstmt = con.prepareStatement(INSERT_SYSTEM_QUERY);
		pstmt.executeUpdate();
		pstmt.close();
		
		// SET ALL DEFAULT NODES TO READ.
		PreparedStatement pstmt2 = con.prepareStatement(UPDATE_NODEUSERSTATE_TABLE);
		pstmt2.executeUpdate() ;
		pstmt2.close();						
		
		//System.out.println("system updated");
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
