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

import com.compendium.core.ICoreConstants;

/**
 * This class is a wrapper class for a Connection object, and stores additional information about its state.
 *
 * @author	rema and sajid /  Michelle Bachler
 */
public class DBConnection {

	/** The default timeut used by MySQL.  It won't hurt using this on Derby as well. */
	private static long MYSQL_SESSION_TIMEOUT 	= 120000; 	// 2 minutes, as this is in milliseconds

	/** Maximum likely active period for a connection statement run*/
	private static long BUSY_TIMEOUT		= 120000;  		// 2 minutes, as this is in milliseconds

	/** A reference to the actual Connection object which this class wraps*/
	private Connection		oConnection 		= null;

	/** Indicates if the connection is busy or not*/
	private boolean		 	bIsBusy				= false;

	/** Used to monitor how long a connection has been busy for */
	private Calendar 		beginTime			= null;
	
	/** Used to monitor how long a connection has been open for */
	private Calendar		sessionStartTime 	= null;
	
	/** The type of database this is a connection for.*/
	private int				nDatabaseType		= -1;
	
	
	/**
	 * Constructor
	 *
	 * @param Connection con, the connection to use to connect to the database.
	 * @param boolean isBusy, indicates if the connection is currently busy.
	 */
	public DBConnection(Connection con, boolean isBusy, int nDatabaseType) {
		oConnection = con;
		setIsBusy(isBusy);
		sessionStartTime = Calendar.getInstance();
		this.nDatabaseType = nDatabaseType;
	}

	/**
	 * Returns the database connection object.
	 * @return Connection, the curernt database connection object.
	 */
	public Connection getConnection() {
		return oConnection;
	}

	/**
	 * Sets if the connection is busy and if it is, records the current time.
	 * This will be used by the timedOut method to calculate if the connection is now inactive.
	 *
	 * @param boolean bIsBusy, sets if this connection is busy.
	 * @see #timedOut
	 */
	public void setIsBusy(boolean bIsBusy) {
		if (bIsBusy)
			beginTime = Calendar.getInstance();
		else
			beginTime = null;

		this.bIsBusy = bIsBusy;
	}
	
	/**
	 * Sets the MYSQL_SESSION_TIMEOUT and BUSY_TIMEOUT params based on data gathered from the MySQL Server
	 * 
	 * @param lTimeout - the Timeout value to set
	 */
	public static void setTimeouts(Long lTimeout) {
		MYSQL_SESSION_TIMEOUT = lTimeout;
		BUSY_TIMEOUT = lTimeout;
	}

	/**
	 * Check to see if this Connection has been apparently active for a long time.
	 * but may infact now be inactive.
	 *
	 * @return boolean indicating if this connection has probably timed out (is inactive)
	 */
	public boolean busyTimedOut() {
		if (bIsBusy) {
			Calendar endTime = Calendar.getInstance();
			long timeTaken = (endTime.getTime().getTime() - beginTime.getTime().getTime());
			if (timeTaken > BUSY_TIMEOUT) {
				return true;
			}
		}

		return false;
	}
	
	/**
	 * Check to see if this Connection has timeded out.
	 * For MySQL only - (has it been active for more than 8 hours?)
	 *
	 * @return boolean indicating if this connection has timed out.
	 */
	public boolean sessionTimedOut() {

		if (nDatabaseType == ICoreConstants.DERBY_DATABASE) {
			return false;			
		}
		
		Calendar endTime = Calendar.getInstance();
		long timeTaken = (endTime.getTime().getTime() - sessionStartTime.getTime().getTime());
		if (timeTaken > MYSQL_SESSION_TIMEOUT) {
			return true;
		}

		// test connection with simple query.
		/*try {
			DatabaseMetaData dbmd = oConnection.getMetaData();
			//Statement statement = oConnection.createStatement();
			//statement.executeQuery("Select * From Users");
			return false;
		} catch (SQLException e) {
			System.out.println("error:"+e.getMessage());
			System.out.println("state = "+e.getSQLState());
			return true;
			//if (e.getSQLState().equalsIgnoreCase("08S01")) {
			//	return true;
			//}
		}	*/		

		return false;
	}

	/**
	 * Returns if the connection is thought to be busy.
	 */
	public boolean getIsBusy() {
		return bIsBusy;
	}
}
