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

import java.sql.Connection;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.Vector;
import java.io.*;

import com.compendium.core.ICoreConstants;
import com.compendium.core.datamodel.*;
import com.compendium.core.db.management.*;

/**
 * The DBFavorite class serves as the interface layer between the Favorites
 * and the Favorite table in the database.
 *
 * @author	Michelle Bachler
 * @version	1.0
 */
public class DBFavorite {

	// AUDITED

	/** SQL statement to insert a new favorite record into the database.*/
	public final static String INSERT_FAVORITE_QUERY =
		"INSERT INTO Favorite (UserID, NodeID, ViewID, Label, NodeType, CreationDate, ModificationDate) "+
		"VALUES (?, ?, ?, ?, ?, ?, ?) ";

	/** SQL statement to delete all favorite records for a given user.*/
	public final static String DELETE_ALL_FAVORITE_QUERY =
		"DELETE "+
		"FROM Favorite "+
		"WHERE UserID = ?";

	/** SQL statement to delete the favorite record for a given user, node and view ids.*/
	public final static String DELETE_FAVORITE_QUERY =
		"DELETE "+
		"FROM Favorite "+
		"WHERE UserID = ? AND NodeID = ? AND ViewID = ?";

	
	// UNAUDITED

	/** SQL statement to return all favorite records for a given user.*/
	public final static String GET_FAVORITES_QUERY =
		"SELECT NodeID, ViewID, Label, NodeType, CreationDate, ModificationDate " +
		"FROM Favorite "+
		"WHERE UserID = ?";


	/**
	 * 	Inserts a new Favorite in the database and if successful.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param sUserID the user id whose favorite it is.
	 *	@param sNodeID the id of the node to add.
	 *	@param sViewID the id of the view for this favorite
	 *	@param sLabel the label of the node.
	 * 	@param nType the node type of the favorite being added.
	 *	@return Favorite the new favorite object, else null if something ewnt wrong.
	 *	@throws java.sql.SQLException
	 */
	public static Favorite insert(DBConnection dbcon, String sUserID, String sNodeID, String sViewID, String sLabel, int nType)
					throws SQLException {

		Connection con = dbcon.getConnection();
		if (con == null)
			return null;

		PreparedStatement pstmt = con.prepareStatement(INSERT_FAVORITE_QUERY);

		Date oDate = new Date();
		double time = oDate.getTime();

		pstmt.setString(1, sUserID);
		pstmt.setString(2, sNodeID);
		pstmt.setString(3, sViewID);

		// THIS IS NOW A MEMO FIELD SO SAME PROBLEM WITH setString cutoff at 256 chars AS FOR BDNode.setDetail etc. - mb
		if (sLabel != "") {
			//ByteArrayInputStream bArrayLabel = new ByteArrayInputStream(sLabel.getBytes());
			//pstmt.setAsciiStream(3, bArrayLabel, bArrayLabel.available());

			// ACCOMODATES UNICODE
			StringReader reader = new StringReader(sLabel);
			pstmt.setCharacterStream(4, reader, sLabel.length());
		}
		else {
			pstmt.setString(4, "");
		}
		//pstmt.setString(4, sLabel);

		pstmt.setInt(5, nType);
		pstmt.setDouble(6, time);
		pstmt.setDouble(7, time);

		int nRowCount = pstmt.executeUpdate();
		pstmt.close();

		if (nRowCount > 0) {
			Favorite fav = new Favorite(sUserID, sNodeID, sViewID, sLabel, nType, oDate, oDate);
			if (DBAudit.getAuditOn())
				DBAudit.auditFavorite(dbcon, DBAudit.ACTION_ADD, fav);

			return fav;
		}

		return null;
	}

	/**
	 *	Deletes the favorites with the given user id and node ids from the database
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param sUserID, the user id whose favorites it is.
	 *	@param sNodeID, the node ids of the favorite nodes to delete (comma separated string).
	 *	@throws java.sql.SQLException
	 */
	public static void delete(DBConnection dbcon, String sUserID, Vector vtFavorites) throws SQLException {

		Connection con = dbcon.getConnection();
		if (con == null) {
			throw new SQLException("Conneciotn null)");
		}

		int count = vtFavorites.size();
		Favorite fav = null;
		PreparedStatement pstmt = null;
		for (int i=0; i<count; i++) {
			fav = (Favorite)vtFavorites.elementAt(i);
		
			pstmt = con.prepareStatement(DELETE_FAVORITE_QUERY);
			pstmt.setString(1, sUserID);
			pstmt.setString(2, fav.getNodeID());
			pstmt.setString(3, fav.getViewID());

			int nRowCount = pstmt.executeUpdate();
			pstmt.close();

			if (nRowCount > 0) {
				if (DBAudit.getAuditOn()) {
					DBAudit.auditFavorite(dbcon, DBAudit.ACTION_DELETE, fav);
				}
			}			
		}
	}

	/**
	 *	Deletes all the favorites for the given user from the database.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param sUserID, the user id whose favorites to delete.
	 *	@return boolean, true if it was successful, else false.
	 *	@throws java.sql.SQLException
	 */
	public static boolean deleteAll(DBConnection dbcon, String sUserID) throws SQLException {

		Connection con = dbcon.getConnection();
		if (con == null)
			return false;

		Vector data = null;
		if (DBAudit.getAuditOn()) {
			data = DBFavorite.getFavorites(dbcon, sUserID);
		}

		PreparedStatement pstmt = con.prepareStatement(DELETE_ALL_FAVORITE_QUERY);
		pstmt.setString(1, sUserID);

		int nRowCount = pstmt.executeUpdate();
		pstmt.close();

		if (nRowCount > 0) {
			if (DBAudit.getAuditOn()) {
				if (data != null) {
					int jcount = data.size();
					Favorite fav = null;
					for (int j=0; j<jcount; j++) {
						fav= (Favorite)data.elementAt(j);
						DBAudit.auditFavorite(dbcon, DBAudit.ACTION_DELETE, fav);
					}
				}
			}

			return true;
		}
		else
			return false;
	}

	/**
	 *	Returns a Vector of Favorites for the given user
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param sUserID, the user id whose favorites to return.
	 *	@return a Vector of Vectors of Favorite Objects.
	 *	@throws java.sql.SQLException
	 */
	public static Vector getFavorites(DBConnection dbcon, String sUserID) throws SQLException {

		Connection con = dbcon.getConnection();
		if (con == null)
			return null;

		PreparedStatement pstmt = con.prepareStatement(GET_FAVORITES_QUERY);

		pstmt.setString(1, sUserID) ;
		ResultSet rs = pstmt.executeQuery();

		Vector vtFavorites = new Vector(51);
		Favorite fav = null;
		if (rs != null) {
			while (rs.next()) {
				String sNodeID = rs.getString(1);
				String sViewID = rs.getString(2);
				String sLabel = rs.getString(3);
				int nType = rs.getInt(4);
				Date creation = new Date( (new Double(rs.getDouble(5))).longValue() );
				Date modification = new Date( (new Double(rs.getDouble(6))).longValue() );
				fav = new Favorite(sUserID, sNodeID, sViewID, sLabel, nType, creation,modification);

				vtFavorites.addElement(fav);
			}
		}
		pstmt.close();
		return vtFavorites;
	}
}
