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

import java.util.*;
import java.awt.Dimension;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.compendium.core.datamodel.*;
import com.compendium.core.db.management.*;
import com.compendium.core.CoreUtilities;
import com.compendium.core.ICoreConstants;

/**
 * The DBReferenceNode class serves as the interface layer to the ReferenceNode table in
 * the database
 *
 * @author	Rema Natarajan / Michelle Bachler
 */
public class DBReferenceNode {

	// AUDITED

	/** SQL statement to insert a particular reference node reference.*/
	public final static String INSERT_NODE_QUERY =
		"INSERT INTO ReferenceNode (NodeID, Source, ImageSource) "+
		"VALUES (?, ?, ?) ";

	/** SQL statement to insert a particular reference node reference.*/
	public final static String INSERT_REFERENCE_QUERY =
		"INSERT INTO ReferenceNode (NodeID, Source, ImageSource, ImageWidth, ImageHeight) "+
		"VALUES (?, ?, ?, ?, ?) ";

	/** SQL statement to delete the reference node with the given no id.*/
	public final static String DELETE_NODE_QUERY =
		"DELETE "+
		"FROM ReferenceNode "+
		"WHERE NodeID = ? ";

	/** SQL statement to update the source and image for the given node id.*/
	public final static String UPDATE_NODE_QUERY =
		"UPDATE ReferenceNode "+
		"SET Source = ?, ImageSource = ? "+
		"WHERE NodeID = ? " ;

	/** SQL statement to update the source and image and image size for the given node id.*/
	public final static String UPDATE_REFERENCE_QUERY =
		"UPDATE ReferenceNode "+
		"SET Source = ?, ImageSource = ?, ImageWidth=?, ImageHeight=? "+
		"WHERE NodeID = ? " ;

	/** SQL statement to update the imagesize for the given node id.*/
	public final static String UPDATE_IMAGESIZE_QUERY =
		"UPDATE ReferenceNode "+
		"SET ImageWidth = ?, ImageHeight = ? "+
		"WHERE NodeID = ? " ;


	// UNAUDITED

	/** SQL statement to get the source for the given node id.*/
	public final static String GET_NODE_QUERY =
		"SELECT Source "+
		"FROM ReferenceNode "+
		"WHERE NodeID = ? " ;

	/** SQL statement to get the image for the given node id.*/
	public final static String GET_IMAGE_QUERY =
		"SELECT ImageSource "+
		"FROM ReferenceNode "+
		"WHERE NodeID = ? " ;

	/** SQL statement to get the image size for the given node id.*/
	public final static String GET_IMAGESIZE_QUERY =
		"SELECT ImageWidth, ImageHeight "+
		"FROM ReferenceNode "+
		"WHERE NodeID = ? " ;
	
	/** SQL statement to get Image, SOurce and sizes for the given node */
	public final static String GET_IRIS_QUERY =
		"SELECT ImageSource, Source, ImageWidth, ImageHeight "+
		"FROM ReferenceNode "+
		"WHERE NodeID = ? " ;


	/** SQL statement to get the all the sources and images from the database.*/
	public final static String GET_ALL_SOURCES_QUERY =
		"SELECT ReferenceNode.Source, ReferenceNode.ImageSource "+
		"FROM ReferenceNode ";

	/** SQL statement to get the all the sources and images from the database.*/
	public final static String GET_ALL_BACKGROUNDS_QUERY =
		"SELECT ViewLayer.Background "+
		"FROM ViewLayer ";

	/** SQL statement to get which nodes use the given reference or image*/
	public final static String GET_NODES_FOR_REF_QUERY =
		"SELECT NodeID "+
		"FROM ReferenceNode "+
		"WHERE Source = ? or ImageSource = ?" ;

	/** SQL statement to get which nodes use the given background image*/
	public final static String GET_NODES_FOR_BACKGROUND_QUERY =
		"SELECT ViewID "+
		"FROM ViewLayer "+
		"WHERE Background = ?" ;

	
	/**
	 * 	Inserts a new reference in the table and returns true if successful.
	 *
	 *	@param dbcon, the DBConnection object to access the database with.
	 *	@param sNodeID the id of the node to insert the reference fo.
	 *	@param sSourceID the source path of the reference.
	 *	@param sImage the image path of the external image for the node.
	 *	@param dModificationDate java.util.Date, the modification date for this reference record.
	 *  @param sLastModAuthor the author name of the person who made this modification. 
	 *  @param sUserID the id of the current user logged in. 
	 *	@return boolean, true if it was successful, else false.
	 *	@throws java.sql.SQLException
	 */
	public static boolean setReference(DBConnection dbcon, String sNodeID, String sSource, 
			String sImage, java.util.Date dModificationDate, String sLastModAuthor, String sUserID)
		throws SQLException {

		Connection con = dbcon.getConnection();
		if (con == null)
			return false;

		//check if the path already exists for the node then just update the node
		boolean exist = DBReferenceNode.update(dbcon, sNodeID, sSource, sImage, 
													dModificationDate, sLastModAuthor, sUserID);
		if(exist)
			return true;
		
		PreparedStatement pstmt = con.prepareStatement(INSERT_NODE_QUERY);
 		pstmt.setString(1, sNodeID);
		pstmt.setString(2, sSource);
		pstmt.setString(3, sImage);

		int nRowCount = pstmt.executeUpdate();
		pstmt.close();

		if (nRowCount > 0) {
			NodeSummary node = DBNode.getNodeSummary(dbcon, sNodeID, sUserID);
			DBNode.setModified(dbcon, sNodeID, dModificationDate, sLastModAuthor, sUserID);

			if (DBAudit.getAuditOn())
				DBAudit.auditReferenceNode(dbcon, DBAudit.ACTION_ADD, sNodeID, sSource, sImage, new Dimension(0, 0));

			if(!DBNode.getImporting() && !DBNode.getQuestmapImporting()){
				boolean updated = DBNodeUserState.updateUsers(dbcon, sNodeID, ICoreConstants.READSTATE, ICoreConstants.MODIFIEDSTATE) ;
				
				int state = node.getState();
				if(state == ICoreConstants.UNREADSTATE){
					DBNodeUserState.updateUser(dbcon, sNodeID, sUserID, ICoreConstants.UNREADSTATE, ICoreConstants.READSTATE);
				} else  {
					DBNodeUserState.updateUser(dbcon, sNodeID, sUserID, ICoreConstants.MODIFIEDSTATE, ICoreConstants.READSTATE);
				}
				node.setStateLocal(ICoreConstants.READSTATE);
			}

			return true;
		} else
			return false;
	}

	/**
	 * 	Inserts a new reference in the table and returns true if successful.
	 *
	 *	@param dbcon, the DBConnection object to access the database with.
	 *	@param sNodeID the id of the node to insert the reference fo.
	 *	@param sSourceID the source path of the reference.
	 *	@param sImage the image path of the external image for the node.
	 *  @param oImageSize the size to draw the image. *
	 *	@param dModificationDate java.util.Date, the modification date for this reference record.
	 *  @param sLastModAuthor the author name of the person who made this modification. 
	 *  @param sUserID the id of the current user logged in. 
	 *	@return boolean, true if it was successful, else false.
	 *	@throws java.sql.SQLException
	 */
	public static boolean setReference(DBConnection dbcon, String sNodeID, String sSource, 
			String sImage, Dimension oImageSize, java.util.Date dModificationDate, String sLastModAuthor, String sUserID)
		throws SQLException {

		Connection con = dbcon.getConnection();
		if (con == null)
			return false;

		//check if the path already exists for the node then just update the node
		boolean exist = DBReferenceNode.update(dbcon, sNodeID, sSource, sImage, oImageSize,
													dModificationDate, sLastModAuthor, sUserID);
		if(exist)
			return true;
		
		PreparedStatement pstmt = con.prepareStatement(INSERT_REFERENCE_QUERY);
 		pstmt.setString(1, sNodeID);
		pstmt.setString(2, sSource);
		pstmt.setString(3, sImage);
		pstmt.setInt(4, oImageSize.width);
		pstmt.setInt(5, oImageSize.height);

		int nRowCount = pstmt.executeUpdate();
		pstmt.close();

		if (nRowCount > 0) {
			NodeSummary node = DBNode.getNodeSummary(dbcon, sNodeID, sUserID);
			DBNode.setModified(dbcon, sNodeID, dModificationDate, sLastModAuthor, sUserID);

			if (DBAudit.getAuditOn())
				DBAudit.auditReferenceNode(dbcon, DBAudit.ACTION_ADD, sNodeID, sSource, sImage, oImageSize);

			if(!DBNode.getImporting() && !DBNode.getQuestmapImporting()){
				boolean updated = DBNodeUserState.updateUsers(dbcon, sNodeID, ICoreConstants.READSTATE, ICoreConstants.MODIFIEDSTATE) ;
				
				int state = node.getState();
				if(state == ICoreConstants.UNREADSTATE){
					DBNodeUserState.updateUser(dbcon, sNodeID, sUserID, ICoreConstants.UNREADSTATE, ICoreConstants.READSTATE);
				} else  {
					DBNodeUserState.updateUser(dbcon, sNodeID, sUserID, ICoreConstants.MODIFIEDSTATE, ICoreConstants.READSTATE);
				}
				node.setStateLocal(ICoreConstants.READSTATE);
			}

			return true;
		} else
			return false;
	}
	
	/**
	 * 	Updates the source / image for the given reference node and returns if successful.
	 *
	 *	@param dbcon, the DBConnection object to access the database with.
	 *	@param sNodeID the id of the node to update the reference for.
	 *	@param sSourceID the source path of the reference.
	 *	@param sImage the image path of the external image.
	 *	@param dModificationDate java.util.Date, the modification date for this reference record.
	 *  @param sLastModAuthor the author name of the person who made this modification. 
	 *  @param sUserID the id of the current user logged in. *
	 *	@return boolean, true if it was successful, else false.
	 *	@throws java.sql.SQLException
	 */
	private static boolean update(DBConnection dbcon, String sNodeID, String sSource, 
			String sImage, java.util.Date dModificationDate, String sLastModAuthor, String sUserID) throws SQLException {
		
		Connection con = dbcon.getConnection() ;
		if (con == null)
			return false;

		PreparedStatement pstmt = con.prepareStatement(UPDATE_NODE_QUERY) ;

		pstmt.setString(1, sSource);
		pstmt.setString(2, sImage);
		pstmt.setString(3, sNodeID);

		int nRowCount = pstmt.executeUpdate();
		pstmt.close();

		if (nRowCount > 0) {
			NodeSummary node = DBNode.getNodeSummary(dbcon, sNodeID, sUserID);
			DBNode.setModified(dbcon, sNodeID, dModificationDate, sLastModAuthor, sUserID);
			
			if (DBAudit.getAuditOn())
				DBAudit.auditReferenceNode(dbcon, DBAudit.ACTION_EDIT, sNodeID, sSource, sImage, new Dimension(0, 0));

			if(!DBNode.getImporting() && !DBNode.getQuestmapImporting()){
				boolean updated = DBNodeUserState.updateUsers(dbcon, sNodeID, ICoreConstants.READSTATE, ICoreConstants.MODIFIEDSTATE) ;
				
				int state = node.getState();
				if(state == ICoreConstants.UNREADSTATE){
					DBNodeUserState.updateUser(dbcon, sNodeID, sUserID, ICoreConstants.UNREADSTATE, ICoreConstants.READSTATE);
				} else  {
					DBNodeUserState.updateUser(dbcon, sNodeID, sUserID, ICoreConstants.MODIFIEDSTATE, ICoreConstants.READSTATE);
				}
				node.setStateLocal(ICoreConstants.READSTATE);
			}
			return true;
		}
		else
			return false;
	}

	/**
	 * 	Updates the source / image for the given reference node and returns if successful.
	 *
	 *	@param dbcon, the DBConnection object to access the database with.
	 *	@param sNodeID the id of the node to update the reference for.
	 *	@param sSourceID the source path of the reference.
	 *	@param sImage the image path of the external image.
	 *  @param oImageSize the size to draw the image. **
	 *	@param dModificationDate java.util.Date, the modification date for this reference record.
	 *  @param sLastModAuthor the author name of the person who made this modification. 
	 *  @param sUserID the id of the current user logged in. *
	 *	@return boolean, true if it was successful, else false.
	 *	@throws java.sql.SQLException
	 */
	private static boolean update(DBConnection dbcon, String sNodeID, String sSource, String sImage, 
				Dimension oImageSize, java.util.Date dModificationDate, String sLastModAuthor, String sUserID) throws SQLException {
		
		Connection con = dbcon.getConnection() ;
		if (con == null)
			return false;

		PreparedStatement pstmt = con.prepareStatement(UPDATE_REFERENCE_QUERY) ;

		pstmt.setString(1, sSource);
		pstmt.setString(2, sImage);
		pstmt.setInt(3, oImageSize.width);
		pstmt.setInt(4, oImageSize.height);				
		pstmt.setString(5, sNodeID);

		int nRowCount = pstmt.executeUpdate();
		pstmt.close();

		if (nRowCount > 0) {
			NodeSummary node = DBNode.getNodeSummary(dbcon, sNodeID, sUserID);
			DBNode.setModified(dbcon, sNodeID, dModificationDate, sLastModAuthor, sUserID);
			
			if (DBAudit.getAuditOn())
				DBAudit.auditReferenceNode(dbcon, DBAudit.ACTION_EDIT, sNodeID, sSource, sImage, oImageSize);

			if(!DBNode.getImporting() && !DBNode.getQuestmapImporting()){
				boolean updated = DBNodeUserState.updateUsers(dbcon, sNodeID, ICoreConstants.READSTATE, ICoreConstants.MODIFIEDSTATE) ;
				
				int state = node.getState();
				if(state == ICoreConstants.UNREADSTATE){
					DBNodeUserState.updateUser(dbcon, sNodeID, sUserID, ICoreConstants.UNREADSTATE, ICoreConstants.READSTATE);
				} else  {
					DBNodeUserState.updateUser(dbcon, sNodeID, sUserID, ICoreConstants.MODIFIEDSTATE, ICoreConstants.READSTATE);
				}
				node.setStateLocal(ICoreConstants.READSTATE);
			}
			return true;
		}
		else
			return false;
	}
	
	/**
	 * 	Updates the image size for the given reference node and returns if successful.
	 *
	 *	@param dbcon, the DBConnection object to access the database with.
	 *	@param sNodeID the id of the node to update the reference for.
	 *  @param oImageSize the size to draw the image. **
	 *	@param dModificationDate java.util.Date, the modification date for this reference record.
	 *  @param sLastModAuthor the author name of the person who made this modification. 
	 *  @param sUserID the id of the current user logged in. *
	 *	@return boolean, true if it was successful, else false.
	 *	@throws java.sql.SQLException
	 */
	public static boolean setImageSize(DBConnection dbcon, String sNodeID, Dimension oImageSize, 
					java.util.Date dModificationDate, String sLastModAuthor, String sUserID) throws SQLException {
		
		Connection con = dbcon.getConnection() ;
		if (con == null)
			return false;

		PreparedStatement pstmt = con.prepareStatement(UPDATE_IMAGESIZE_QUERY) ;

		pstmt.setInt(1, oImageSize.width);
		pstmt.setInt(2, oImageSize.height);				
		pstmt.setString(3, sNodeID);

		int nRowCount = pstmt.executeUpdate();
		pstmt.close();

		if (nRowCount > 0) {
			NodeSummary node = DBNode.getNodeSummary(dbcon, sNodeID, sUserID);
			DBNode.setModified(dbcon, sNodeID, dModificationDate, sLastModAuthor, sUserID);
			
			if (DBAudit.getAuditOn())
				DBAudit.auditReferenceNode(dbcon, DBAudit.ACTION_EDIT, sNodeID, "", "", oImageSize);

			if(!DBNode.getImporting() && !DBNode.getQuestmapImporting()){
				boolean updated = DBNodeUserState.updateUsers(dbcon, sNodeID, ICoreConstants.READSTATE, ICoreConstants.MODIFIEDSTATE) ;
				
				int state = node.getState();
				if(state == ICoreConstants.UNREADSTATE){
					DBNodeUserState.updateUser(dbcon, sNodeID, sUserID, ICoreConstants.UNREADSTATE, ICoreConstants.READSTATE);
				} else  {
					DBNodeUserState.updateUser(dbcon, sNodeID, sUserID, ICoreConstants.MODIFIEDSTATE, ICoreConstants.READSTATE);
				}
				node.setStateLocal(ICoreConstants.READSTATE);
			}
			return true;
		}
		else
			return false;
	}	
	
	/**
	 *	Deletes the reference node with the given sNodeID from the table and returns true if successful.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param sNodeID, the id of the node to delete the reference for.
	 *	@return boolean, true if it was successful, else false.
	 *	@throws java.sql.SQLException
	 */
	public static boolean delete(DBConnection dbcon, String sNodeID) throws SQLException {
		Connection con = dbcon.getConnection() ;
		if (con == null)
			return false;

		// If auditing, save the data first.
		String sSource = null;
		String sImage = null;
		int nWidth = -1;
		int nHeight = -1;		
		if (DBAudit.getAuditOn()) {
			sSource = DBReferenceNode.getReference(dbcon, sNodeID);
			sImage = DBReferenceNode.getImage(dbcon, sNodeID);
			Dimension dim = DBReferenceNode.getImageSize(dbcon, sNodeID);
			nWidth = dim.width;
			nHeight = dim.height;
		}

		PreparedStatement pstmt = con.prepareStatement(DELETE_NODE_QUERY);

		pstmt.setString(1, sNodeID);

		int nRowCount = pstmt.executeUpdate();
		pstmt.close();

		if (nRowCount > 0) {
			if (DBAudit.getAuditOn() && sSource != null && sImage != null)
				DBAudit.auditReferenceNode(dbcon, DBAudit.ACTION_DELETE, sNodeID, sSource, sImage, new Dimension(nWidth, nHeight));

			boolean updated = DBNodeUserState.updateUsers(dbcon, sNodeID, ICoreConstants.READSTATE, ICoreConstants.MODIFIEDSTATE) ;

			return true;
		}
		else
			return false;
	}


// UNAUDITED

	/**
	 *	Gets the source for the given reference node and returns the String value if successful.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param sNodeID, the id of the node to update the reference for.
	 *	@return String, the reference path for the given NodeID, else an empty String.
	 *	@throws java.sql.SQLException
	 */
	public static String getReference(DBConnection dbcon, String sNodeID) throws SQLException {
		Connection con = dbcon.getConnection();
		if (con == null)
			return null;

		PreparedStatement pstmt = con.prepareStatement(GET_NODE_QUERY) ;

		pstmt.setString(1, sNodeID);

		ResultSet rs = pstmt.executeQuery();

		String source = "";
		if (rs != null) {
			while (rs.next()) {
				source	= rs.getString(1);
			}
		}
		pstmt.close();
		return source;
	}

	/**
	 * 	Gets the image for the given reference node and returns the String value if successful.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param sNodeID, the id of the node to update the image for.
	 *	@return String, the image path for the given NodeID, else an empty String.
	 *	@throws java.sql.SQLException
	 */
	public static String getImage(DBConnection dbcon, String sNodeID) throws SQLException {
		
		Connection con = dbcon.getConnection();
		if (con == null)
			return null;

		PreparedStatement pstmt = con.prepareStatement(GET_IMAGE_QUERY) ;

		pstmt.setString(1, sNodeID);

		ResultSet rs = pstmt.executeQuery();

		String image = "";
		if (rs != null) {
			while (rs.next()) {
				image = rs.getString(1);
			}
		}
		pstmt.close();
		return image;
	}


	/**
	 * 	Gets the image size for the given reference node.
	 *
	 *	@param dbcon the DBConnection object to access the database with.
	 *	@param sNodeID the id of the node to update the image for.
	 *	@return Dimension the image size for the given NodeID.
	 *	@throws java.sql.SQLException
	 */
	public static Dimension getImageSize(DBConnection dbcon, String sNodeID) throws SQLException {

		Connection con = dbcon.getConnection();
		if (con == null)
			return null;

		PreparedStatement pstmt = con.prepareStatement(GET_IMAGESIZE_QUERY);

		pstmt.setString(1, sNodeID);

		ResultSet rs = pstmt.executeQuery();

		Dimension oSize = new Dimension(0, 0);
		if (rs != null) {
			int width = -1;
			int height = -1;
			while (rs.next()) {
				width = rs.getInt(1);
				height = rs.getInt(2);
				oSize.width = width;
				oSize.height = height;
			}
		}
		pstmt.close();
		return oSize;
	}
	
	/**
	 *  Populates the Image, Reference, and ImageSize fields of the given Node object.
	 *  This is done simply as a performance optimization to do this operation using
	 *  one MySQL call instead of three.  This operation is done very frequently as maps
	 *  are being built (opened), and hence this optimization speeds map opening considerably.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param sNodeID the id of the node to get the information for.
	 *	@param NodeSummary node - the node object into which to store the results
	 *	@throws java.sql.SQLException
	 */
	public static void getIRIS(DBConnection dbcon, String sNodeID, NodeSummary node) throws SQLException {
		
		Connection con = dbcon.getConnection();
		if (con == null)
			return;
		
		String image = "";
		String source = "";
		Dimension oSize = new Dimension(0, 0);
		
		PreparedStatement pstmt = con.prepareStatement(GET_IRIS_QUERY);
		pstmt.setString(1, sNodeID);
		ResultSet rs = pstmt.executeQuery();
		
		if (rs != null) {
			while (rs.next()) {
				image = rs.getString(1);
				source	= rs.getString(2);
				oSize.width = rs.getInt(3);
				oSize.height = rs.getInt(4);
			}
		}
		pstmt.close();
		
		node.setLocalImage(image);
		node.setLocalSource(source);
		node.setLocalImageSize(oSize);	
		return;
	}
	
	/**
	 *  Gets a unique list of all the references (files only) and images and background images 
	 *  in the database and a count of their use.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@return a unique list of all the reference paths and image paths as Strings (Hashtable - File->use count).
	 *	@throws java.sql.SQLException
	 */
	public static Hashtable<String,Integer> getAllSources(DBConnection dbcon) throws SQLException {

		Connection con = dbcon.getConnection();
		if (con == null)
			return null;

		Hashtable<String,Integer> sources = new Hashtable<String,Integer>(51);

		PreparedStatement pstmt = con.prepareStatement(GET_ALL_SOURCES_QUERY);
		ResultSet rs = pstmt.executeQuery();		
		if (rs != null) {
			String ref = "";
			String image = "";
			while (rs.next()) {
				ref	= rs.getString(1);
				image = rs.getString(2);
				
				if (ref != null && !ref.equals("")) {
					if (sources.containsKey(ref)) {
						Integer count = (Integer)sources.get(ref);
						count++;
						sources.put(ref, count);
					} else {
						sources.put(ref, new Integer(1));
					}
				}
				if (image != null && !image.equals("")) {
					if (sources.containsKey(image)) {
						Integer count = (Integer)sources.get(image);
						count++;
						sources.put(image, count);
					} else {
						sources.put(image, new Integer(1));
					}
				}
			}
		}
		pstmt.close();
		
		pstmt = con.prepareStatement(GET_ALL_BACKGROUNDS_QUERY);
		rs = pstmt.executeQuery();
		if (rs != null) {
			String background = "";
			while (rs.next()) {
				background	= rs.getString(1);
				if (background != null && !background.equals("")) {
					if (sources.containsKey(background)) {
						Integer count = (Integer)sources.get(background);
						count++;
						sources.put(background, count);
					} else {
						sources.put(background, new Integer(1));
					}
				}
			}
		}
		
		return sources;
	}
	
	/**
	 *  Gets a unique list of all the references, images and background images in the database.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@return Vector, a unique list of all the reference paths and image paths as Strings.
	 *	@throws java.sql.SQLException
	 */
	public static Vector getAllSourcesUnique(DBConnection dbcon) throws SQLException {

		Connection con = dbcon.getConnection();
		if (con == null)
			return null;

		Hashtable check = new Hashtable(51);
		Vector sources = new Vector();

		PreparedStatement pstmt = con.prepareStatement(GET_ALL_SOURCES_QUERY);
		ResultSet rs = pstmt.executeQuery();
		if (rs != null) {
			String ref = "";
			String image = "";
			while (rs.next()) {
				ref	= rs.getString(1);
				image = rs.getString(2);
				
				if (!check.containsKey(ref)) {
					sources.addElement(ref);
					check.put(ref, ref);
				}
				if (!check.containsKey(image)) {
					sources.addElement(image);
					check.put(image, image);
				}
			}
		}
		
		pstmt = con.prepareStatement(GET_ALL_BACKGROUNDS_QUERY);
		rs = pstmt.executeQuery();
		if (rs != null) {
			String image = "";
			while (rs.next()) {
				image	= rs.getString(1);
				if (!check.containsKey(image)) {
					sources.addElement(image);
					check.put(image, image);
				}
			}
		}
		
		pstmt.close();
		return sources;
	}
	
	/**
	 *  Gets a list of all nodes that use the given source for a ref, image or background.
	 *
	 *	@param dbcon the DBConnection object to access the database with.
	 *	@param source the source to return the nodes for.
	 *	@return a unique list of the node using the given source.
	 *	@throws java.sql.SQLException
	 */
	public static Vector<NodeSummary> getNodesForSource(DBConnection dbcon, String source, String sUserID) throws SQLException {

		Connection con = dbcon.getConnection();
		if (con == null)
			return null;

		Vector<NodeSummary>nodes = new Vector<NodeSummary>(51);

		PreparedStatement pstmt = con.prepareStatement(GET_NODES_FOR_REF_QUERY);
		pstmt.setString(1, source);
		pstmt.setString(2, source);
		ResultSet rs = pstmt.executeQuery();
		if (rs != null) {
			String sNodeID = "";
			while (rs.next()) {
				sNodeID	= rs.getString(1);
				NodeSummary node = DBNode.getAnyNodeSummary(dbcon, sNodeID, sUserID);
				nodes.addElement(node);
			}
		}

		pstmt = con.prepareStatement(GET_NODES_FOR_BACKGROUND_QUERY);
		pstmt.setString(1, source);
		rs = pstmt.executeQuery();
		if (rs != null) {
			String sNodeID = "";
			while (rs.next()) {
				sNodeID	= rs.getString(1);
				NodeSummary node = DBNode.getAnyNodeSummary(dbcon, sNodeID, sUserID);
				nodes.addElement(node);
			}
		}

		pstmt.close();
		
		return nodes;
	}	
}
