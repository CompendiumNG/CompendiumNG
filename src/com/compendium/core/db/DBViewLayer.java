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

import java.io.*;
import java.awt.*;

import java.sql.*;
import java.util.Date;
import java.util.Vector;

import com.compendium.core.datamodel.*;
import com.compendium.core.db.management.*;

/**
 * The DBViewLayer class serves as the interface layer between the ViewLayer 
 * class and the ViewLayer table in the database.
 *
 * @author	Michelle Bachler
 */
public class DBViewLayer {

	// AUDITED
	/** SQL statement to insert a new ViewLayer record into the ViewPeoerty table.*/
	public final static String INSERT_VIEWLAYER_QUERY = DBConstants.INSERT_VIEWLAYER_QUERY;

	/** SQL statement to update a ViewLayer record for the given ViewID.*/
	public final static String UPDATE_VIEWLAYER_QUERY =
		"UPDATE ViewLayer set Scribble = ?, Background = ?, Grid = ?, Shapes = ? , BackgroundColor = ? "+
		"WHERE ViewID = ?";

	/** SQL statement to delete a ViewLayer record for the given ViewID.*/
	public final static String DELETE_VIEWLAYER_QUERY =
		"DELETE "+
		"FROM ViewLayer "+
		"WHERE ViewID = ?";

	// UNAUDITED
	/** SQL statement to return the ViewLayer record for the given ViewID.*/
	public final static String GET_VIEWLAYER =
		"SELECT Scribble, Background, Grid, Shapes, BackgroundColor " +
		"FROM ViewLayer "+
		"WHERE ViewID = ?";

	/**
	 * 	Inserts a new DBViewLayer in the database and if successful.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param DBViewLayer view, the class holding the layer details.
	 *	@return boolean, true if it was successful, else false.
	 *	@throws java.sql.SQLException
	 */
	public static boolean insert(DBConnection dbcon, ViewLayer view)
					throws SQLException {

		Connection con = dbcon.getConnection();
		if (con == null)
			return false;

		PreparedStatement pstmt = con.prepareStatement(INSERT_VIEWLAYER_QUERY);

		pstmt.setString(1, view.getViewID());

		String sScribble = view.getScribble();
		if (!sScribble.equals("")) {
			StringReader reader = new StringReader(sScribble);
			pstmt.setCharacterStream(2, reader, sScribble.length());

		}
		else {
			pstmt.setString(2, "");
		}

		pstmt.setString(3, view.getBackgroundImage());
		pstmt.setString(4, view.getGrid());

		String sShapes = view.getShapes();
		if (!sShapes.equals("")) {
			StringReader reader = new StringReader(sShapes);
			pstmt.setCharacterStream(5, reader, sShapes.length());

		}
		else {
			pstmt.setString(5, "");
		}

		pstmt.setInt(6, view.getBackgroundColor());

		int nRowCount = pstmt.executeUpdate();
		pstmt.close();

		if (nRowCount > 0) {
			if (DBAudit.getAuditOn())
				DBAudit.auditViewLayer(dbcon, DBAudit.ACTION_ADD, view);

			return true;
		}
		else
			return false;
	}

	/**
	 * 	Update a ViewProperty in the database and if successful.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param view, the ViewLayer holding the view properties to update.
	 *	@return boolean, true if it was successful, else false.
	 *	@throws java.sql.SQLException
	 */
	public static boolean update(DBConnection dbcon, ViewLayer view)
					throws SQLException {

		Connection con = dbcon.getConnection();
		if (con == null)
			return false;


		PreparedStatement pstmt = con.prepareStatement(UPDATE_VIEWLAYER_QUERY);

		String sScribble = view.getScribble();
		if (!sScribble.equals("")) {
			StringReader reader = new StringReader(sScribble);
			pstmt.setCharacterStream(1, reader, sScribble.length());

		}
		else {
			pstmt.setString(1, "");
		}

		pstmt.setString(2, view.getBackgroundImage());
		pstmt.setString(3, view.getGrid());

		String sShapes = view.getShapes();
		if (!sShapes.equals("")) {
			StringReader reader = new StringReader(sShapes);
			pstmt.setCharacterStream(4, reader, sShapes.length());

		}
		else {
			pstmt.setString(4, "");
		}
		
		pstmt.setInt(5, view.getBackgroundColor());
		
		pstmt.setString(6, view.getViewID());


		int nRowCount = pstmt.executeUpdate();
		pstmt.close();

		if (nRowCount > 0) {
			if (DBAudit.getAuditOn())
				DBAudit.auditViewLayer(dbcon, DBAudit.ACTION_EDIT, view);

			return true;
		}
		else
			return false;
	}


	/**
	 *  Deletes the view properties with the given user and view id from the database
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param String sViewID, the id of the View whose layer properties to delete.
	 *	@return boolean, true if it was successful, else false.
	 *	@throws java.sql.SQLException
	 */
	public static boolean delete(DBConnection dbcon, String sViewID) throws SQLException {

		Connection con = dbcon.getConnection();
		if (con == null)
			return false;

		ViewLayer view = null;
		if (DBAudit.getAuditOn())  {
			view = DBViewLayer.getViewLayer(dbcon, sViewID);
		}

		PreparedStatement pstmt = con.prepareStatement(DELETE_VIEWLAYER_QUERY);
		pstmt.setString(1, sViewID);

		int nRowCount = pstmt.executeUpdate();
		pstmt.close();

		if (nRowCount > 0) {
			if (DBAudit.getAuditOn())
				DBAudit.auditViewLayer(dbcon, DBAudit.ACTION_DELETE, view);

			return true;
		}
		else
			return false;
	}

	/**
	 *	Returns the view layer properties for the given and view id
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param String sViewID, the id of the view whose layer properties to get.
	 *	@return com.compendium.core.datamodel.ViewLayer, the view's layer properties, else null.
	 *	@throws java.sql.SQLException
	 */
	public static ViewLayer getViewLayer(DBConnection dbcon, String sViewID) throws SQLException {

		Connection con = dbcon.getConnection();
		if (con == null)
			return null;

		PreparedStatement pstmt = con.prepareStatement(GET_VIEWLAYER);
		pstmt.setString(1, sViewID);

		ResultSet rs = pstmt.executeQuery();

		ViewLayer view = null;
		if (rs != null) {
			while (rs.next()) {
				view = new ViewLayer();
				view.setViewID(sViewID);
				view.setScribble(rs.getString(1));
				view.setBackgroundImage(rs.getString(2));
				view.setGrid(rs.getString(3));
				view.setShapes(rs.getString(4));
				view.setBackgroundColor(rs.getInt(5));

				break;
			}
		} else {
			view = new ViewLayer();
			view.setViewID(sViewID);
			if (!DBViewLayer.insert(dbcon, view)) {
				view = null;
			}
		}
		pstmt.close();
		return view;
	}
}
