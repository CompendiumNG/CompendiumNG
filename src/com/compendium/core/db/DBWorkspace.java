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

package com.compendium.core.db;

import java.sql.*;
import java.util.Date;
import java.util.Vector;

import com.compendium.core.datamodel.*;
import com.compendium.core.db.management.*;
import com.compendium.core.CoreUtilities;

/**
 * The DBWorkspace class serves as the interface layer between the Workspaces
 * and the Workspace table in the database.
 *
 * @author	Michelle Bachler
 */
public class DBWorkspace {

	// AUDITED
	/** SQL statement to insert a new Workspace Record into the Workspace table.*/
	public final static String INSERT_WORKSPACE1_QUERY =
		"INSERT INTO Workspace (WorkspaceID, UserID, Name, CreationDate, ModificationDate) "+
		"VALUES (?, ?, ?, ?, ?) ";

	/** SQL statement to insert a new WorkspaceView Record into the WorkspaceView table.*/
	public final static String INSERT_WORKSPACE2_QUERY =
		"INSERT INTO WorkspaceView ( WorkspaceID, ViewID, Width, Height, XPosition, YPosition, IsIcon, IsMaximum, HorizontalScroll, VerticalScroll) "+
		"VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ";

	/** SQL statement to delete the WorkspaceView Records for the given WorkspaceID.*/
	public final static String DELETE_FAILED_INSERT =
		"DELETE "+
		"FROM WorkspaceView "+
		"WHERE WorkspaceID = ?";

	/** SQL statement to delete the Workspace Record for the given WorkspaceID.*/
	public final static String DELETE_FAILED_INSERT2 =
		"DELETE "+
		"FROM Workspace "+
		"WHERE WorkspaceID = ?";

	// UNAUDITED
	/** SQL statement to return WorkspaceView Records for the given WorkspaceID.*/
	public final static String GET_WORKSPACEVIEW_QUERY =
		"SELECT WorkspaceID, ViewID, Width, Height, XPosition, YPosition, IsIcon, IsMaximum, HorizontalScroll, VerticalScroll " +
		"FROM WorkspaceView "+
		"WHERE WorkspaceID = ?";

	/** SQL statement to return the WorkspaceView Records for the given ViewID.*/
	public final static String GET_WORKSPACEVIEW_QUERY2 =
		"SELECT WorkspaceID, ViewID, Width, Height, XPosition, YPosition, IsIcon, IsMaximum, HorizontalScroll, VerticalScroll " +
		"FROM WorkspaceView "+
		"WHERE ViewID = ?";

	/** SQL statement to return the Workspace Records for the given UserID.*/
	public final static String GET_WORKSPACES_QUERY =
		"SELECT WorkspaceID, Name, CreationDate, ModificationDate " +
		"FROM Workspace "+
		"WHERE UserID = ?";

	/** SQL statement to return the Workspace Record for the given WorkspaceID.*/
	public final static String GET_WORKSPACE_QUERY =
		"SELECT WorkspaceID, Name, CreationDate, ModificationDate " +
		"FROM Workspace "+
		"WHERE WorkspaceID = ?";

	/**
	 * 	Inserts a new Workspace into the database and returns if successful.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param String sWorkspaceID, the id of the new workspace.
	 *	@param String sUserID, the id of the user whose workspace it is.
	 *	@param String sName, the name of the workspace.
	 *	@param String vtViews, a Vector of the WorkspaceView objects in this workspace.
	 *	@return boolean, true if it was successful, else false.
	 *	@throws java.sql.SQLException
	 */
	public static boolean insert(DBConnection dbcon, String sWorkspaceID, String sUserID, String sName, Vector vtViews)	throws SQLException {

		Connection con = dbcon.getConnection();
		if (con == null)
			return false;

		PreparedStatement pstmt = con.prepareStatement(INSERT_WORKSPACE1_QUERY);

		Date oDate = new Date();
		double time = oDate.getTime();

		pstmt.setString(1, sWorkspaceID);
		pstmt.setString(2, sUserID);
		pstmt.setString(3, sName);
		pstmt.setDouble(4, time);
		pstmt.setDouble(5, time);

		int nRowCount = pstmt.executeUpdate();

		if (nRowCount > 0) {
			try {
				Vector views = new Vector(51);
				int count = vtViews.size();
				for (int i=0; i<count; i++) {
					WorkspaceView view = (WorkspaceView)vtViews.elementAt(i);

					PreparedStatement pstmt2 = con.prepareStatement(INSERT_WORKSPACE2_QUERY);

					String isMax = "N";
					if (view.getIsMaximum())
						isMax = "Y";

					String isIcon = "N";
					if (view.getIsIcon())
						isIcon = "Y";

					pstmt2.setString(1, sWorkspaceID);
					pstmt2.setString(2, view.getViewID());
					pstmt2.setInt(3, view.getWidth());
					pstmt2.setInt(4, view.getHeight());
					pstmt2.setInt(5, view.getXPosition());
					pstmt2.setInt(6, view.getYPosition());
					pstmt2.setString(7, isIcon);
					pstmt2.setString(8, isMax);
					pstmt2.setInt(9, view.getHorizontalScrollBarPosition());
					pstmt2.setInt(10, view.getVerticalScrollBarPosition());

					int nRowCount2 = pstmt2.executeUpdate();
					pstmt2.close();

					if (nRowCount2 <= 0) {
						// TRY AND CLEAN UP
						PreparedStatement pstmt3 = con.prepareStatement(DELETE_FAILED_INSERT);
						pstmt3.setString(1, sWorkspaceID);
						pstmt3.executeUpdate();
						pstmt3.close();

						PreparedStatement pstmt4 = con.prepareStatement(DELETE_FAILED_INSERT2);
						pstmt4.setString(1, sWorkspaceID);
						pstmt4.executeUpdate();
						pstmt4.close();

						// HOW TO CLEAN UP AUDIT??
						pstmt.close();
						return false;
					}
					else {
						views.addElement(view);
					}
				}
				if (DBAudit.getAuditOn())
					DBAudit.auditWorkspace(dbcon, DBAudit.ACTION_ADD, sWorkspaceID, sUserID, sName, time, time, views);
			}
			catch(SQLException sql) {
				// TRY AND CLEAN UP
				PreparedStatement pstmt5 = con.prepareStatement(DELETE_FAILED_INSERT);
				pstmt5.setString(1, sWorkspaceID);
				pstmt5.executeUpdate();
				pstmt5.close();
				pstmt.close();
				throw sql;
			}
		}

		pstmt.close();

		return true;
	}

	/**
	 *  Update a Workspace in the database and returns if successful.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param String sWorkspaceID, the id of the workspace to update.
	 *	@param String sUserID, the id of the user whose workspave to update.
	 *	@param String sName, the name of the workspace.
	 *	@param String vtViews, a Vector of the WorkspaceView objects in this updated Workspace.
	 *	@return boolean, true if it was successful, else false.
	 *	@throws java.sql.SQLException
	 */
	public static boolean update(DBConnection dbcon, String sWorkspaceID, String sUserID, String sName, Vector vtViews)	throws SQLException {

		Connection con = dbcon.getConnection();
		if (con == null)
			return false;

		Date oDate = new Date();
		double time = oDate.getTime();

		PreparedStatement pstmt = con.prepareStatement(DELETE_FAILED_INSERT);
		pstmt.setString(1, sWorkspaceID);

		int nRowCount = pstmt.executeUpdate();
		pstmt.close();

		if (nRowCount > 0) {
			if (DBAudit.getAuditOn()) {
				//DBAudit.auditWorkspace(dbcon, DBAudit.ACTION_DELETE, sUserID, next, views);
			}

			try {
				Vector views = new Vector(51);
				int count = vtViews.size();
				for (int i=0; i<count; i++) {
					WorkspaceView view = (WorkspaceView)vtViews.elementAt(i);

					PreparedStatement pstmt2 = con.prepareStatement(INSERT_WORKSPACE2_QUERY);

					String isMax = "N";
					if (view.getIsMaximum())
						isMax = "Y";

					String isIcon = "N";
					if (view.getIsIcon())
						isIcon = "Y";

					pstmt2.setString(1, sWorkspaceID);
					pstmt2.setString(2, view.getViewID());
					pstmt2.setInt(3, view.getWidth());
					pstmt2.setInt(4, view.getHeight());
					pstmt2.setInt(5, view.getXPosition());
					pstmt2.setInt(6, view.getYPosition());
					pstmt2.setString(7, isIcon);
					pstmt2.setString(8, isMax);
					pstmt2.setInt(9, view.getHorizontalScrollBarPosition());
					pstmt2.setInt(10, view.getVerticalScrollBarPosition());

					int nRowCount2 = pstmt2.executeUpdate();
					pstmt2.close();

					if (nRowCount2 <= 0) {
						PreparedStatement pstmt4 = con.prepareStatement(DELETE_FAILED_INSERT);
						pstmt4.setString(1, sWorkspaceID);
						pstmt4.executeUpdate();
						pstmt4.close();
						// HOW TO CLEAN UP AUDIT??

						return false;
					}
					else {
						views.addElement(view);
					}
				}
				if (DBAudit.getAuditOn())
					DBAudit.auditWorkspace(dbcon, DBAudit.ACTION_ADD, sWorkspaceID, sUserID, sName, time, time, views);
			}
			catch(SQLException sql) {
				// TRY AND CLEAN UP
				PreparedStatement pstmt5 = con.prepareStatement(DELETE_FAILED_INSERT);
				pstmt5.setString(1, sWorkspaceID);
				pstmt5.executeUpdate();
				pstmt5.close();
				throw sql;
			}
		}
		else {
			return false;
		}

		return true;
	}

	/**
	 *  Deletes the Workspaces with the given user and WorkspaceIDs from the database.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param String sUserID, the id of the user whose Workspace records to delete.
	 *	@param String sWorkspaceIDs, a String of comma separated workspace id to delete.
	 *	@return boolean, true if it was successful, else false.
	 *	@throws java.sql.SQLException
	 */
	public static boolean delete(DBConnection dbcon, String sUserID, String sWorkspaceIDs) throws SQLException {

		Connection con = dbcon.getConnection();
		if (con == null || sWorkspaceIDs.equals(""))
			return false;

		// IF AUDITING, save workspace data
		Vector data = new Vector(10);
		if (DBAudit.getAuditOn()) {
			Vector ids = CoreUtilities.splitString(sWorkspaceIDs, ",");
			int count = ids.size();
			for (int i=0; i < count; i++)
				data.addElement(DBWorkspace.getWorkspace(dbcon, (String)ids.elementAt(i)));
		}

		String DELETE_WORKSPACE_QUERY2 = "DELETE FROM Workspace WHERE WorkspaceID IN ("+sWorkspaceIDs+")";
		PreparedStatement pstmt2 = con.prepareStatement(DELETE_WORKSPACE_QUERY2);
		int nRowCount2 = pstmt2.executeUpdate();
		pstmt2.close();

		if (nRowCount2 > 0) {
			if (DBAudit.getAuditOn() && data.size() > 0) {
				int jcount = data.size();
				for (int j=0; j<jcount; j++) {
					Vector next = (Vector)data.elementAt(j);
					Vector views = DBWorkspace.getWorkspaceViews(dbcon, (String)next.elementAt(0));

					DBAudit.auditWorkspace(dbcon, DBAudit.ACTION_DELETE, sUserID, next, views);
				}
			}
			return true;
		}
		else
			return false;
	}

	/**
	 * 	Deletes all the Workspaces for the given user from the database
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param String sUserID, the id of the user whose workspaces to delete.
	 *	@return boolean, if it was successful, else false.
	 *	@throws java.sql.SQLException
	 */
	public static boolean deleteAll(DBConnection dbcon, String sUserID) throws SQLException {

		Connection con = dbcon.getConnection();
		if (con == null)
			return false;

		// GET ALL THE WORKSPACES FOR THE GIVEN USER
		Vector workspaces = DBWorkspace.getWorkspaces(dbcon, sUserID);
		int count = workspaces.size();

		String workspaceIDs = "";
		for (int i=0; i<count; i++) {
			Vector nextItem = (Vector)workspaces.elementAt(i);
			String sWorkspaceID = (String)nextItem.elementAt(0);
			workspaceIDs += "'"+sWorkspaceID+"'";
			if (i < count-1)
				workspaceIDs += ",";
		}

		// DELETE ALL THEIR WORKSPACE VIEWS
		if (!workspaceIDs.equals("")) {

			String DELETE_WORKSPACE_QUERY = "DELETE FROM WorkspaceView WHERE WorkspaceID IN ("+workspaceIDs+")";
			PreparedStatement pstmt = con.prepareStatement(DELETE_WORKSPACE_QUERY);
			int nRowCount = pstmt.executeUpdate();
			pstmt.close();
		}

		// DELETE ALL THEIR WORKSPACES
		String DELETE_WORKSPACE_QUERY2 = "DELETE FROM Workspace WHERE UserID = '"+sUserID+"'";
		PreparedStatement pstmt2 = con.prepareStatement(DELETE_WORKSPACE_QUERY2);
		int nRowCount2 = pstmt2.executeUpdate();
		pstmt2.close();

		if (nRowCount2 > 0) {
			if (DBAudit.getAuditOn() && workspaces != null) {
				for (int i=0; i<count; i++) {
					Vector nextItem = (Vector)workspaces.elementAt(i);
					String sWorkspaceID = (String)nextItem.elementAt(0);
					Vector view = DBWorkspace.getWorkspaceViews(dbcon, sWorkspaceID);
					DBAudit.auditWorkspace(dbcon, DBAudit.ACTION_DELETE, sUserID, nextItem, view);
				}
			}
			return true;
		}
		else
			return false;
	}

	/**
	 *	Returns a Vector of WorkspaceView objects for the given view id.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param String sViewID, the view id of the view whose WorkspaceView to return.
	 *	@return Vector, a list of WorkspaceView objects for the given view id.
	 *	@throws java.sql.SQLException
	 */
	public static Vector getViews(DBConnection dbcon, String sViewID) throws SQLException {

		Connection con = dbcon.getConnection();
		if (con == null)
			return null;

		PreparedStatement pstmt = con.prepareStatement(GET_WORKSPACEVIEW_QUERY2);
		pstmt.setString(1, sViewID) ;
		ResultSet rs = pstmt.executeQuery();

		Vector vtWorkspaces = new Vector(51);
		if (rs != null) {
			while (rs.next()) {
				WorkspaceView workspace = new WorkspaceView();
				workspace.setWorkspaceID(rs.getString(1));
				workspace.setViewID(rs.getString(2));
				workspace.setWidth(rs.getInt(3));
				workspace.setHeight(rs.getInt(4));
				workspace.setXPosition(rs.getInt(5));
				workspace.setYPosition(rs.getInt(6));

				String  isIcon = rs.getString(7);
				if (isIcon.equals("Y"))
					workspace.setIsIcon(true);
				else
					workspace.setIsIcon(false);

				String  isMax = rs.getString(8);
				if (isMax.equals("Y"))
					workspace.setIsMaximum(true);
				else
					workspace.setIsMaximum(false);

				workspace.setHorizontalScrollBarPosition(rs.getInt(9));
				workspace.setVerticalScrollBarPosition(rs.getInt(10));
				vtWorkspaces.addElement(workspace);
			}
		}
		pstmt.close();
		return vtWorkspaces;
	}

	/**
	 *	Return all the WorkspaceViews for the given WorkspaceID.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param String sWorkspaceID, the id of the Workspace whose WorkspaceViews to return.
	 *	@return Vector, a list of WorkspaceView objects for the given WorkspaceID.
	 *	@throws java.sql.SQLException
	 */
	public static Vector getWorkspaceViews(DBConnection dbcon, String sWorkspaceID) throws SQLException {

		Connection con = dbcon.getConnection();
		if (con == null)
			return null;

		PreparedStatement pstmt = con.prepareStatement(GET_WORKSPACEVIEW_QUERY);

		pstmt.setString(1, sWorkspaceID) ;
		ResultSet rs = pstmt.executeQuery();

		Vector vtWorkspaces = new Vector(51);
		if (rs != null) {
			while (rs.next()) {
				WorkspaceView workspace = new WorkspaceView();
				workspace.setWorkspaceID(rs.getString(1));
				workspace.setViewID(rs.getString(2));
				workspace.setWidth(rs.getInt(3));
				workspace.setHeight(rs.getInt(4));
				workspace.setXPosition(rs.getInt(5));
				workspace.setYPosition(rs.getInt(6));

				String  isIcon = rs.getString(7);
				if (isIcon.equals("Y"))
					workspace.setIsIcon(true);
				else
					workspace.setIsIcon(false);

				String  isMax = rs.getString(8);
				if (isMax.equals("Y"))
					workspace.setIsMaximum(true);
				else
					workspace.setIsMaximum(false);

				workspace.setHorizontalScrollBarPosition(rs.getInt(9));
				workspace.setVerticalScrollBarPosition(rs.getInt(10));
				vtWorkspaces.addElement(workspace);
			}
		}
		pstmt.close();
		return vtWorkspaces;
	}

	/**
	 *	Returns a Vector of all Workspace data for the given UserID.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param String sUserID, the id of the user whose workspaces to return.
	 *	@return Vector, a list of Vectors with the Workspace data for the given user. Each inner Vector contains:
	 *  <li>WorkspaceID - String
	 * 	<li>Name - String
	 *  <li>CreationDate - Double (milliseconds)
	 * 	<li>ModificationDate - Double (milliseconds)
	 *	@throws java.sql.SQLException
	 */
	public static Vector getWorkspaces(DBConnection dbcon, String sUserID) throws SQLException {

		Connection con = dbcon.getConnection();
		if (con == null)
			return null;

		PreparedStatement pstmt = con.prepareStatement(GET_WORKSPACES_QUERY);

		pstmt.setString(1, sUserID) ;
		ResultSet rs = pstmt.executeQuery();

		Vector vtWorkspaces = new Vector(51);
		if (rs != null) {
			while (rs.next()) {
				Vector workspace = new Vector(4);
				workspace.addElement(rs.getString(1));
				workspace.addElement(rs.getString(2));
				Double oCDate = new Double(rs.getLong(3));
				Double oMDate = new Double(rs.getLong(4));
				workspace.addElement(oCDate);
				workspace.addElement(oMDate);

				vtWorkspaces.addElement(workspace);
			}
		}
		pstmt.close();
		return vtWorkspaces;
	}

	/**
	 *	Returns a Vector of the Workspace data for the given WorkspaceID
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param String sWorkspaceID, the id of the workspace whose data to return.
	 *	@return Vector, which contains:
	 *  <li>WorkspaceID - String
	 * 	<li>Name - String
	 *  <li>CreationDate - Double (milliseconds)
	 * 	<li>ModificationDate - Double (milliseconds)
	 *	@throws java.sql.SQLException
	 */
	public static Vector getWorkspace(DBConnection dbcon, String sWorkspaceID) throws SQLException {

		Connection con = dbcon.getConnection();
		if (con == null)
			return null;

		PreparedStatement pstmt = con.prepareStatement(GET_WORKSPACE_QUERY);

		pstmt.setString(1, sWorkspaceID) ;
		ResultSet rs = pstmt.executeQuery();

		Vector vtWorkspace = new Vector(51);
		if (rs != null) {
			if (rs.next()) {
				vtWorkspace.addElement(rs.getString(1));
				vtWorkspace.addElement(rs.getString(2));
				Double oCDate = new Double(rs.getLong(3));
				Double oMDate = new Double(rs.getLong(4));
				vtWorkspace.addElement(oCDate);
				vtWorkspace.addElement(oMDate);
			}
		}
		pstmt.close();
		return vtWorkspace;
	}
}
