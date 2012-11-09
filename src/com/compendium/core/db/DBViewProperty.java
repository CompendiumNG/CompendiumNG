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

import java.awt.*;

import java.sql.*;
import java.util.Date;
import java.util.Vector;

import com.compendium.core.datamodel.*;
import com.compendium.core.db.management.*;

/**
 * The DBViewProperty class serves as the interface layer between the View Properties
 * and the ViewProperty table in the database.
 *
 * @author	Michelle Bachler
 */
public class DBViewProperty {

	// AUDITED
	/** SQL statement to insert a new ViewProperty record into the ViewProperty table.*/
	public final static String INSERT_VIEWPROPERTY_QUERY =
		"INSERT INTO ViewProperty (UserID, ViewID, HorizontalScroll, VerticalScroll, Width, Height, XPosition, YPosition, IsIcon, IsMaximum) "+
		"VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

	/** SQL statement to update a ViewProperty recrod for the given UserID and ViewID.*/
	public final static String UPDATE_VIEWPROPERTY_QUERY =
		"UPDATE ViewProperty set HorizontalScroll = ?, VerticalScroll = ?, Width = ?, Height = ?, XPosition = ?, YPosition = ?, IsIcon = ?, IsMaximum = ? "+
		"WHERE UserID = ? AND ViewID = ?";

	/** SQL statement to delete a ViewProperty record for the given UserID and ViewID.*/
	public final static String DELETE_VIEWPROPERTY_QUERY =
		"DELETE "+
		"FROM ViewProperty "+
		"WHERE UserID = ? AND ViewID = ?";

	// UNAUDITED
	/** SQL statement to return the ViewProperty record for the given UserID and ViewID.*/
	public final static String GET_VIEWPROPERTY =
		"SELECT HorizontalScroll, VerticalScroll, Width, Height, XPosition, YPosition, IsIcon, IsMaximum " +
		"FROM ViewProperty "+
		"WHERE UserID = ? AND ViewID = ?";

	/** SQL statement to return the ViewProperty records for the given UserID.*/
	public final static String GET_ALL_VIEWPROPERTY =
		"SELECT ViewID, HorizontalScroll, VerticalScroll, Width, Height, XPosition, YPosition, IsIcon, IsMaximum" +
		"FROM ViewProperty "+
		"WHERE UserID = ?";

	/** SQL statement to return the ViewProperty records for the given ViewID.*/
	public final static String GET_ALL_VIEWPROPERTY_VIEWS =
		"SELECT UserID, HorizontalScroll, VerticalScroll, Width, Height, XPosition, YPosition, IsIcon, IsMaximum" +
		"FROM ViewProperty "+
		"WHERE ViewID = ?";

	/**
	 * 	Inserts a new ViewProperty in the database and if successful.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param String sUserID, the current user's id.
	 *	@param UIViewFrame viewFrame, the frame whose properties to insert into a new ViewProperty record.
	 *	@return boolean, true if it was successful, else false.
	 *	@throws java.sql.SQLException
	 */
	public static boolean insert(DBConnection dbcon, String sUserID, ViewProperty view)
					throws SQLException {

		Connection con = dbcon.getConnection();
		if (con == null)
			return false;

		PreparedStatement pstmt = con.prepareStatement(INSERT_VIEWPROPERTY_QUERY);

		pstmt.setString(1, sUserID);
		pstmt.setString(2, view.getViewID());
		pstmt.setInt(3, view.getHorizontalScrollBarPosition());
		pstmt.setInt(4, view.getVerticalScrollBarPosition());
		pstmt.setInt(5, view.getWidth());
		pstmt.setInt(6, view.getHeight());
		pstmt.setInt(7, view.getXPosition());
		pstmt.setInt(8, view.getYPosition());
		pstmt.setString(9, view.getIsIcon() ? "Y" : "N");
		pstmt.setString(10, view.getIsMaximum() ? "Y" : "N");

		int nRowCount = pstmt.executeUpdate();
		pstmt.close();

		if (nRowCount > 0) {
			if (DBAudit.getAuditOn())
				DBAudit.auditViewProperty(dbcon, DBAudit.ACTION_ADD, sUserID, view);

			return true;
		}
		else
			return false;
	}

	/**
	 * 	Update a ViewProperty in the database and if successful.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param String sUserID, the id of the user whose ViewProprty record to update.
	 *	@param UIViewFrame viewFrame, the frame whose properties to update.
	 *	@return boolean, true if it was successful, else false.
	 *	@throws java.sql.SQLException
	 */
	public static boolean update(DBConnection dbcon, String sUserID, ViewProperty view)
					throws SQLException {

		Connection con = dbcon.getConnection();
		if (con == null)
			return false;

		PreparedStatement pstmt = con.prepareStatement(UPDATE_VIEWPROPERTY_QUERY);
		pstmt.setInt(1, view.getHorizontalScrollBarPosition());
		pstmt.setInt(2, view.getVerticalScrollBarPosition());
		pstmt.setInt(3, view.getWidth());
		pstmt.setInt(4, view.getHeight());
		pstmt.setInt(5, view.getXPosition());
		pstmt.setInt(6, view.getYPosition());
		pstmt.setString(7, view.getIsIcon() ? "Y" : "N");
		pstmt.setString(8, view.getIsMaximum() ? "Y" : "N");
		pstmt.setString(9, sUserID);
		pstmt.setString(10, view.getViewID());

		int nRowCount = pstmt.executeUpdate();
		pstmt.close();

		if (nRowCount > 0) {
			if (DBAudit.getAuditOn())
				DBAudit.auditViewProperty(dbcon, DBAudit.ACTION_EDIT, sUserID, view);

			return true;
		}
		else
			return false;
	}


	/**
	 *  Deletes the view properties with the given user and view id from the database
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param String sUserID, the id of the user whose ViewProperty record to delete.
	 *	@param String sViewID, the id of the View whose properties to delete.
	 *	@return boolean, tru if it was successful, else false.
	 *	@throws java.sql.SQLException
	 */
	public static boolean delete(DBConnection dbcon, String sUserID, String sViewID) throws SQLException {

		Connection con = dbcon.getConnection();
		if (con == null)
			return false;

		ViewProperty view = null;
		if (DBAudit.getAuditOn())  {
			view = DBViewProperty.getViewPosition(dbcon, sUserID, sViewID);
		}

		PreparedStatement pstmt = con.prepareStatement(DELETE_VIEWPROPERTY_QUERY);
		pstmt.setString(1, sUserID);
		pstmt.setString(2, sViewID);

		int nRowCount = pstmt.executeUpdate();
		pstmt.close();

		if (nRowCount > 0) {
			if (DBAudit.getAuditOn())
				DBAudit.auditViewProperty(dbcon, DBAudit.ACTION_DELETE, sUserID, view);

			return true;
		}
		else
			return false;
	}

	/**
	 *	Returns a Vector of View Properties for the given user and view id
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param String sUserID, the id of the user whose ViewProperty record to return.
	 *	@param String sViewID, the id of the view whose properties to get.
	 *	@return com.compendium.core.datamodel.ViewProperty, the view's properties, else null.
	 *	@throws java.sql.SQLException
	 */
	public static ViewProperty getViewPosition(DBConnection dbcon, String sUserID, String sViewID) throws SQLException {

		Connection con = dbcon.getConnection();
		if (con == null)
			return null;

		PreparedStatement pstmt = con.prepareStatement(GET_VIEWPROPERTY);
		pstmt.setString(1, sUserID);
		pstmt.setString(2, sViewID);

		ResultSet rs = pstmt.executeQuery();

		ViewProperty view = null;
		if (rs != null) {
			while (rs.next()) {
				view = new ViewProperty();
				view.setViewID(sViewID);
				view.setUserID(sUserID);
				view.setHorizontalScrollBarPosition(rs.getInt(1));
				view.setVerticalScrollBarPosition(rs.getInt(2));
				view.setWidth(rs.getInt(3));
				view.setHeight(rs.getInt(4));
				view.setXPosition(rs.getInt(5));
				view.setYPosition(rs.getInt(6));

				String  isIcon = rs.getString(7);
				if (isIcon.equals("Y"))
					view.setIsIcon(true);
				else
					view.setIsIcon(false);

				String  isMax = rs.getString(8);
				if (isMax.equals("Y"))
					view.setIsMaximum(true);
				else
					view.setIsMaximum(false);

				break;
			}
		}
		pstmt.close();
		return view;
	}


	/**
	 *	Returns a Vector of all the View Properties for the given user id
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param String sUserID, the id of the user whose ViewProperty record to return.
	 *	@return Vector, a list of all the <code>ViewProperty</code> objects for the given user id.
	 *	@throws java.sql.SQLException
	 */
	public static Vector getAllViewProperty(DBConnection dbcon, String sUserID) throws SQLException {

		Connection con = dbcon.getConnection();
		if (con == null)
			return null;

		PreparedStatement pstmt = con.prepareStatement(GET_ALL_VIEWPROPERTY);
		pstmt.setString(1, sUserID);

		ResultSet rs = pstmt.executeQuery();

		Vector data = new Vector(8);
		if (rs != null) {
			while (rs.next()) {
				ViewProperty view = new ViewProperty();
				view.setViewID(rs.getString(1));
				view.setUserID(sUserID);
				view.setHorizontalScrollBarPosition(rs.getInt(2));
				view.setVerticalScrollBarPosition(rs.getInt(3));
				view.setWidth(rs.getInt(4));
				view.setHeight(rs.getInt(5));
				view.setXPosition(rs.getInt(6));
				view.setYPosition(rs.getInt(7));

				String  isIcon = rs.getString(8);
				if (isIcon.equals("Y"))
					view.setIsIcon(true);
				else
					view.setIsIcon(false);

				String  isMax = rs.getString(9);
				if (isMax.equals("Y"))
					view.setIsMaximum(true);
				else
					view.setIsMaximum(false);

				data.addElement(view);
			}
		}
		pstmt.close();
		return data;
	}

	/**
	 *	Returns a Vector of all the View Properties for the given view id
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param String sViewID, the view id of the view whose ViewProperty records to return.
	 *	@return Vector, a list of all the <code>ViewProperty</code> objects for the given view id.
	 *	@throws java.sql.SQLException
	 */
	public static Vector getAllViewPropertyViews(DBConnection dbcon, String sViewID) throws SQLException {

		Connection con = dbcon.getConnection();
		if (con == null)
			return null;

		PreparedStatement pstmt = con.prepareStatement(GET_ALL_VIEWPROPERTY_VIEWS);
		pstmt.setString(1, sViewID);

		ResultSet rs = pstmt.executeQuery();

		Vector data = new Vector(8);
		if (rs != null) {
			while (rs.next()) {
				ViewProperty view = new ViewProperty();
				view.setUserID(rs.getString(1));
				view.setViewID(sViewID);
				view.setHorizontalScrollBarPosition(rs.getInt(2));
				view.setVerticalScrollBarPosition(rs.getInt(3));
				view.setWidth(rs.getInt(4));
				view.setHeight(rs.getInt(5));
				view.setXPosition(rs.getInt(6));
				view.setYPosition(rs.getInt(7));

				String  isIcon = rs.getString(8);
				if (isIcon.equals("Y"))
					view.setIsIcon(true);
				else
					view.setIsIcon(false);

				String  isMax = rs.getString(9);
				if (isMax.equals("Y"))
					view.setIsMaximum(true);
				else
					view.setIsMaximum(false);

				data.addElement(view);
			}
		}
		pstmt.close();
		return data;
	}
}
