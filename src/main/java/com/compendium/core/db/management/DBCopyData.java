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

import javax.swing.*;

import java.util.Vector;
import java.util.StringTokenizer;
import java.util.Hashtable;
import java.net.InetAddress;

import com.compendium.core.*;

/*
 * An abstract class for copyin Compendium projects from one database to another.
 * It allows external classes to register DBProgressListeners and fires appropriate progress information to them.
 * This facilitates the display of progress information in a user interface, if desired.
 *
 * @author Michelle Bachler
 */
public abstract class DBCopyData implements DBConstants, DBProgressListener {

	/** A Vector of DBProgressListeners which have been registered with this object to recieve progress events*/
  	protected Vector progressListeners = new Vector();

	/** Holds the increment number used for the progress updates.*/
	protected int				increment = 1;

	/** A StringBuffer any holding error information produced during the copying process.*/
	protected StringBuffer		errorLog		= new StringBuffer(1000);


	/**
	 * Return a String representing any error information available from the copying process.
	 */
	public String getLog() {
		return errorLog.toString();
	}

	/**
	 * Copy the given project from one database to another.
	 *
	 * @param String sFromName, the name of the database to convert.
	 * @param String sFriendlyToName, the user known name of the database to create to convert the data into.
	 * @param String sFriendlyFromName, the user known name of the database to convert the data from.
	 * @exception java.lang.ClassNotFoundException
	 * @exception java.sql.SQLException
	 * @exception DBDatabaseNameException, thrown if a database with the name given in the constructor already exists.
	 * @exception DBDatabaseTypeException, thrown if a database connection of the specific type cannot be created.
	 */
	public abstract void copyDatabase(String sFromName, String sFriendlyToName, String sFriendlyFromName)  
			throws ClassNotFoundException, SQLException, DBDatabaseException;

	/**
	 * Copy all the tables from one database to another and fire progress updates as required.
	 *
	 * @param Connection inCon, the connection object to read data from.
	 * @param Connection outCon, the connection object to write data to.
	 * @exception java.sql.SQLException
	 */
	protected void copyTables(Connection inCon, Connection outCon) throws SQLException {

		if (inCon == null || outCon == null) {
			throw new SQLException("One of the connections required to do the convertion is null.");
		}

		fireProgressUpdate(increment, "Copying System Table");
		convertSystemTable(inCon, outCon);

		// THE ORDER OF USER, NODE AND LINK IMPORT IS IMPORTANT, DON'T CHANGE IT

		fireProgressUpdate(increment, "Copying User Table");
		convertUserTable(inCon, outCon);

		fireProgressUpdate(increment, "Copying Node Table");
		convertNodeTable(inCon, outCon);

		fireProgressUpdate(increment, "Copying Link Table");
		convertLinkTable(inCon, outCon);

		/////////////////////////////////////////////////////////////////////////

		fireProgressUpdate(increment, "Copying ViewNode Table");
		convertViewNodeTable(inCon, outCon);

		fireProgressUpdate(increment, "Copying ViewLink Table");
		convertViewLinkTable(inCon, outCon);

		fireProgressUpdate(increment, "Copying Code Table");
		convertCodeTable(inCon, outCon);

		// MUST LOAD GROUPS BEFORE GROUP CODES
		fireProgressUpdate(increment, "Copying CodeGroup Table");
		convertCodeGroupTable(inCon, outCon);

		fireProgressUpdate(increment, "Copying GroupCode Table");
		convertGroupCodeTable(inCon, outCon);

		fireProgressUpdate(increment, "Copying NodeCode Table");
		convertNodeCodeTable(inCon, outCon);

		fireProgressUpdate(increment, "Copying Reference Node Table");
		convertReferenceNodeTable(inCon, outCon);

		fireProgressUpdate(increment, "Copying Shortcut Node Table");
		convertShortCutNodeTable(inCon, outCon);

		fireProgressUpdate(increment, "Copying NodeDetail Table");
		convertNodeDetailTable(inCon, outCon);

		fireProgressUpdate(increment, "Copying ViewProperty Table");
		convertViewPropertyTable(inCon, outCon);

		fireProgressUpdate(increment, "Copying Favorite Table");
		convertFavoriteTable(inCon, outCon);

		fireProgressUpdate(increment, "Copying Workspace Table");
		convertWorkspaceTable(inCon, outCon);
		convertWorkspaceViewTable(inCon, outCon);

		fireProgressUpdate(increment, "Copying NodeUserStatus Table");
		convertNodeUserStateTable(inCon, outCon);


		fireProgressUpdate(increment, "Copying Audit Table");
		convertAuditTable(inCon, outCon);

		fireProgressUpdate(increment, "Copying Permission Table");
		convertPermissionTable(inCon, outCon);

		fireProgressUpdate(increment, "Copying Clone Table");
		convertCloneTable(inCon, outCon);

		fireProgressUpdate(increment, "Copying ExtendedNode Table");
		convertExtendedNodeTable(inCon, outCon);

		fireProgressUpdate(increment, "Copying ExtendedCode Table");
		convertExtendedCodeTable(inCon, outCon);

		fireProgressUpdate(increment, "Copying UserGroup Table");
		convertUserGroupTable(inCon, outCon);

		fireProgressUpdate(increment, "Copying GroupUser Table");
		convertGroupUserTable(inCon, outCon);

		// NEW 1.3 TABLES
		fireProgressUpdate(increment, "Copying ViewLayer Table");
		convertViewLayerTable(inCon, outCon);

		fireProgressUpdate(increment, "Copying Connection Table");
		convertConnectionTable(inCon, outCon);

		fireProgressUpdate(increment, "Copying Preference Table");
		convertPreferenceTable(inCon, outCon);

		// NEW 1.4 TABLES
		fireProgressUpdate(increment, "Copying Meeting Table");
		convertMeetingTable(inCon, outCon);

		fireProgressUpdate(increment, "Copying MediaIndex Table");
		convertMediaIndexTable(inCon, outCon);
		
		// NEW 1.5.3 TABLES
		fireProgressUpdate(increment, "Copying LinkedFile Table");
		convertLinkedFileTable(inCon, outCon);	

		// NEW 2.0 TABLES
		fireProgressUpdate(increment, "Copying ViewTimeNode Table");
		convertViewTimeNodeTable(inCon, outCon);

		fireProgressUpdate(increment, "Copying Movies Table");
		convertMoviesTable(inCon, outCon);

		fireProgressUpdate(increment, "Copying MovieProperties Table");
		convertMoviePropertiesTable(inCon, outCon);

	}

	/**
	 * Convert the System table.
	 *
	 * @exception java.sql.SQLException
	 */
	private void convertSystemTable(Connection inCon, Connection outCon) throws SQLException {

		PreparedStatement pstmt1 = inCon.prepareStatement(GET_SYSTEM_QUERY);
		PreparedStatement pstmt2 = outCon.prepareStatement(INSERT_SYSTEM_QUERY);

		ResultSet rs = pstmt1.executeQuery();

		if (rs != null) {
			while (rs.next()) {
				String	sProperty		= rs.getString(1);
				String	sContents		= rs.getString(2);

				pstmt2.setString(1, sProperty);
				pstmt2.setString(2, sContents);

				try {
					pstmt2.executeUpdate();
				}
				catch(SQLException ex){
					errorLog.append(ex.getMessage());
					ex.printStackTrace();
				}
			}
		}
	}

	/**
	 * Convert the Users table.
	 *
	 * @exception java.sql.SQLException
	 */
	private void convertUserTable(Connection inCon, Connection outCon) throws SQLException {

		PreparedStatement pstmt1 = inCon.prepareStatement(GET_USER_QUERY);
		PreparedStatement pstmt2 = outCon.prepareStatement(INSERT_USER_QUERY);

		ResultSet rs = pstmt1.executeQuery();

		if (rs != null) {
			while (rs.next()) {

				String  sUserID		= rs.getString(1);
				String	sAuthor		= rs.getString(2);
				double  dbCDate		= rs.getDouble(3);
				double  dbMDate		= rs.getDouble(4);
				String  loginName	= rs.getString(5);
				String	userName	= rs.getString(6);
				String  password	= rs.getString(7);
				String	userDesc	= rs.getString(8);
				String	homeViewId	= rs.getString(9);
				String  admin 		= rs.getString(10);
				int nCurrentStatus	= rs.getInt(11);
				String	linkViewId	= rs.getString(12);
				
				if (linkViewId == null) {
					linkViewId = "";
				}

				pstmt2.setString(1, sUserID);
				pstmt2.setString(2, sAuthor);
				pstmt2.setDouble(3, dbCDate);
				pstmt2.setDouble(4, dbMDate);
				pstmt2.setString(5, loginName);
				pstmt2.setString(6, userName);
				pstmt2.setString(7, password);
				pstmt2.setString(8, userDesc);
				pstmt2.setString(9, homeViewId);
				pstmt2.setString(10, admin);
				pstmt2.setInt(11, nCurrentStatus);
				pstmt2.setString(12, linkViewId);

				try {
					pstmt2.executeUpdate();
				}
				catch(SQLException ex){
					errorLog.append(ex.getMessage());
					ex.printStackTrace();
				}
			}
		}
	}

	/**
	 * Convert the Node table.
	 *
	 * @exception java.sql.SQLException
	 */
	private void convertNodeTable(Connection inCon, Connection outCon) throws SQLException {

		PreparedStatement pstmt1 = inCon.prepareStatement(GET_NODE_QUERY);
		PreparedStatement pstmt2 = outCon.prepareStatement(INSERT_NODE_QUERY);

		ResultSet rs = pstmt1.executeQuery();

		if (rs != null) {
			while (rs.next()) {

				String	sNodeID		= rs.getString(1);
				String	sAuthor		= rs.getString(2);
				double	dbCDate		= rs.getDouble(3);
				double	dbMDate		= rs.getDouble(4);
				int		nType		= rs.getInt(5);
				String	sOriginalID	= rs.getString(6);
				String 	nXNodeType 	= rs.getString(7) ;
				String	sLabel		= rs.getString(8);
				String	sDetail 	= rs.getString(9);
				int		nCurrentStatus	= rs.getInt(10);
				String	sLastModAuthor	= rs.getString(11);
				
				if (sLastModAuthor == null) {
					sLastModAuthor = sAuthor;
				}

				pstmt2.setString(1, sNodeID);
				pstmt2.setString(2, sAuthor);
				pstmt2.setDouble(3, dbCDate);
				pstmt2.setDouble(4, dbMDate);
				pstmt2.setInt(5, nType);
				pstmt2.setString(6, sOriginalID);
				pstmt2.setString(7, nXNodeType);

				if (!sLabel.equals("")) {
					//ByteArrayInputStream bArrayLabel = new ByteArrayInputStream(sLabel.getBytes());
					//pstmt2.setAsciiStream(8, bArrayLabel, bArrayLabel.available());

					// ACCOMODATES UNICODE
					StringReader reader = new StringReader(sLabel);
					pstmt2.setCharacterStream(8, reader, sLabel.length());
				}
				else {
					pstmt2.setString(8, "");
				}

				if (!sDetail.equals("")) {
					//ByteArrayInputStream bArrayDetail = new ByteArrayInputStream(sDetail.getBytes());
					//pstmt2.setAsciiStream(9, bArrayDetail, bArrayDetail.available());

					// ACCOMODATES UNICODE
					StringReader reader = new StringReader(sDetail);
					pstmt2.setCharacterStream(9, reader, sDetail.length());
				}
				else {
					pstmt2.setString(9, "");
				}

				pstmt2.setInt(10, nCurrentStatus);
				pstmt2.setString(11, sLastModAuthor);

				try {
					pstmt2.executeUpdate();
				}
				catch(SQLException ex ){
					errorLog.append(ex.getMessage());
					ex.printStackTrace();
				}
			}
		}
	}

	/**
	 * Convert the Link table.
	 *
	 * @exception java.sql.SQLException
	 */
	private void convertLinkTable(Connection inCon, Connection outCon) throws SQLException {

		PreparedStatement pstmt1 = inCon.prepareStatement(GET_LINK_QUERY);
		PreparedStatement pstmt2 = outCon.prepareStatement(INSERT_LINK_QUERY);

		ResultSet rs = pstmt1.executeQuery();

		if (rs != null) {
			while (rs.next()) {

				String	sLinkID		= rs.getString(1);
				String	sAuthor		= rs.getString(2);
				double	dbCDate		= rs.getDouble(3);
				double	dbMDate		= rs.getDouble(4);
				String	sType		= rs.getString(5);
				String	sOriginalID	= rs.getString(6);
				String	sFrom		= rs.getString(7);
				String	sTo		 	= rs.getString(8);
				String 	sLabel	 	= rs.getString(9);
				int		nCurrentStatus	= rs.getInt(10);

				pstmt2.setString(1, sLinkID);
				pstmt2.setString(2, sAuthor);
				pstmt2.setDouble(3, dbCDate);
				pstmt2.setDouble(4, dbMDate);
				pstmt2.setString(5, sType);
				pstmt2.setString(6, sOriginalID);
				pstmt2.setString(7, sFrom);
				pstmt2.setString(8, sTo);
				pstmt2.setString(9, sLabel);
				pstmt2.setInt(10, nCurrentStatus);

				try {
					pstmt2.executeUpdate();
				}
				catch(SQLException ex) {
					errorLog.append(ex.getMessage());
					ex.printStackTrace();
				}
			}
		}
	}

	/**
	 * Convert the Code table.
	 *
	 * @exception java.sql.SQLException
	 */
	private void convertCodeTable(Connection inCon, Connection outCon) throws SQLException {

		PreparedStatement pstmt1 = inCon.prepareStatement(GET_CODE_QUERY);
		PreparedStatement pstmt2 = outCon.prepareStatement(INSERT_CODE_QUERY);

		ResultSet rs = pstmt1.executeQuery();

		if (rs != null) {
			while (rs.next()) {

				String	sCodeID		= rs.getString(1);
				String	sAuthor		= rs.getString(2);
				double	dbCDate		= rs.getDouble(3);
				double	dbMDate		= rs.getDouble(4);
				String	sName		= rs.getString(5);
				String	sDesc	 	= rs.getString(6);
				String 	sBehaviour 	= rs.getString(7);

				pstmt2.setString(1, sCodeID);
				pstmt2.setString(2, sAuthor);
				pstmt2.setDouble(3, dbCDate);
				pstmt2.setDouble(4, dbMDate);
				pstmt2.setString(5, sName);
				pstmt2.setString(6, sDesc);
				pstmt2.setString(7, sBehaviour);

				try {
					pstmt2.executeUpdate();
				}
				catch(SQLException ex) {
					errorLog.append(ex.getMessage());
					ex.printStackTrace();
				}
			}
		}
	}

	/**
	 * Convert the GroupCode table.
	 *
	 * @exception java.sql.SQLException
	 */
	private void convertGroupCodeTable(Connection inCon, Connection outCon) throws SQLException {

		PreparedStatement pstmt1 = inCon.prepareStatement(GET_GROUPCODE_QUERY);
		PreparedStatement pstmt2 = outCon.prepareStatement(INSERT_GROUPCODE_QUERY);

		ResultSet rs = pstmt1.executeQuery();

		if (rs != null) {
			while (rs.next()) {

				String	sCodeID		= rs.getString(1);
				String	sCodeGroupID= rs.getString(2);
				String	sAuthor		= rs.getString(3);
				double	dbCDate		= rs.getDouble(4);
				double	dbMDate		= rs.getDouble(5);

				pstmt2.setString(1, sCodeID);
				pstmt2.setString(2, sCodeGroupID);
				pstmt2.setString(3, sAuthor);
				pstmt2.setDouble(4, dbCDate);
				pstmt2.setDouble(5, dbMDate);

				try {
					pstmt2.executeUpdate();
				}
				catch(SQLException ex) {
					errorLog.append(ex.getMessage());
					ex.printStackTrace();
				}
			}
		}
	}

	/**
	 * Convert the CodeGroup table.
	 *
	 * @exception java.sql.SQLException
	 */
	private void convertCodeGroupTable(Connection inCon, Connection outCon) throws SQLException {

		PreparedStatement pstmt1 = inCon.prepareStatement(GET_CODEGROUP_QUERY);
		PreparedStatement pstmt2 = outCon.prepareStatement(INSERT_CODEGROUP_QUERY);

		ResultSet rs = pstmt1.executeQuery();

		if (rs != null) {
			while (rs.next()) {

				String	sCodeGroupID	= rs.getString(1);
				String	sAuthor			= rs.getString(2);
				String	sName			= rs.getString(3);
				String	sDesc			= rs.getString(4);
				double	dbCDate			= rs.getDouble(5);
				double	dbMDate			= rs.getDouble(6);

				pstmt2.setString(1, sCodeGroupID);
				pstmt2.setString(2, sAuthor);
				pstmt2.setString(3, sName);
				pstmt2.setString(4, sDesc);
				pstmt2.setDouble(5, dbCDate);
				pstmt2.setDouble(6, dbMDate);

				try {
					pstmt2.executeUpdate();
				}
				catch(SQLException ex) {
					errorLog.append(ex.getMessage());
					ex.printStackTrace();
				}
			}
		}
	}

	/**
	 * Convert the NodeCode table.
	 *
	 * @exception java.sql.SQLException
	 */
	private void convertNodeCodeTable(Connection inCon, Connection outCon) throws SQLException {

		PreparedStatement pstmt1 = inCon.prepareStatement(GET_NODECODE_QUERY);
		PreparedStatement pstmt2 = outCon.prepareStatement(INSERT_NODECODE_QUERY);

		ResultSet rs = pstmt1.executeQuery();

		if (rs != null) {
			while (rs.next()) {

				String	sNodeID		= rs.getString(1);
				String	sCodeID		= rs.getString(2);

				pstmt2.setString(1, sNodeID);
				pstmt2.setString(2, sCodeID);

				try {
					pstmt2.executeUpdate();
				}
				catch(SQLException ex) {
					errorLog.append(ex.getMessage());
					ex.printStackTrace();
				}
			}
		}
	}

	/**
	 * Convert the ReferenceNode table.
	 *
	 * @exception java.sql.SQLException
	 */
	private void convertReferenceNodeTable(Connection inCon, Connection outCon) throws SQLException {

		PreparedStatement pstmt1 = inCon.prepareStatement(GET_REFERENCE_QUERY);
		PreparedStatement pstmt2 = outCon.prepareStatement(INSERT_REFERENCE_QUERY);

		ResultSet rs = pstmt1.executeQuery();

		if (rs != null) {
			while (rs.next()) {

				String	sNodeID		= rs.getString(1);
				String	sSource		= rs.getString(2);
				String	sSourceImage= rs.getString(3);
				int		nImageWidth	= rs.getInt(4);
				int		nImageHeight= rs.getInt(5);

				pstmt2.setString(1, sNodeID);
				pstmt2.setString(2, sSource);
				pstmt2.setString(3, sSourceImage);
				pstmt2.setInt(4, nImageWidth);
				pstmt2.setInt(5, nImageHeight);

				try {
					pstmt2.executeUpdate();
				}
				catch(SQLException ex) {
					errorLog.append(ex.getMessage());
					ex.printStackTrace();
				}
			}
		}
	}

	/**
	 * Convert the ViewNode table.
	 *
	 * @exception java.sql.SQLException
	 */
	private void convertViewNodeTable(Connection inCon, Connection outCon) throws SQLException {

		PreparedStatement pstmt1 = inCon.prepareStatement(GET_SPECIFIC_VIEWNODE_QUERY);
		PreparedStatement pstmt2 = outCon.prepareStatement(INSERT_VIEWNODE_QUERY);

		ResultSet rs = pstmt1.executeQuery();

		if (rs != null) {
			while (rs.next()) {

				String	sViewID		= rs.getString(1);
				String	sNodeID		= rs.getString(2);
				int		nXPos		= rs.getInt(3);
				int		nYPos		= rs.getInt(4);
				double	dbCDate		= rs.getDouble(5);
				double	dbMDate		= rs.getDouble(6);
				int		nStatus		= rs.getInt(7);
				String	sShowTags	= rs.getString(8);
				String	sShowText	= rs.getString(9);
				String	sShowTrans	= rs.getString(10);
				String	sShowWeight	= rs.getString(11);
				String	sSmallIcons	= rs.getString(12);
				String	sHideIcons	= rs.getString(13);
				int		nLabelWidth	= rs.getInt(14);
				int		nFontSize	= rs.getInt(15);
				String	sFontFace	= rs.getString(16);
				int		nFontStyle	= rs.getInt(17);
				int		nForeground	= rs.getInt(18);
				int		nBackground	= rs.getInt(19);

				pstmt2.setString(1, sViewID);
				pstmt2.setString(2, sNodeID);
				pstmt2.setInt(3, nXPos);
				pstmt2.setInt(4, nYPos);
				pstmt2.setDouble(5, dbCDate);
				pstmt2.setDouble(6, dbMDate);
				pstmt2.setInt(7, nStatus);
				pstmt2.setString(8, sShowTags);
				pstmt2.setString(9, sShowText);
				pstmt2.setString(10, sShowTrans);
				pstmt2.setString(11, sShowWeight);
				pstmt2.setString(12, sSmallIcons);
				pstmt2.setString(13, sHideIcons);
				pstmt2.setInt(14, nLabelWidth);
				pstmt2.setInt(15, nFontSize);
				pstmt2.setString(16, sFontFace);
				pstmt2.setInt(17, nFontStyle);
				pstmt2.setInt(18, nForeground);
				pstmt2.setInt(19, nBackground);			

				try {
					pstmt2.executeUpdate();
				}
				catch(SQLException ex) {
					errorLog.append(ex.getMessage());
					ex.printStackTrace();
				}
			}
		}
	}

	/**
	 * Convert the ShortCutNode table.
	 *
	 * @exception java.sql.SQLException
	 */
	private void convertShortCutNodeTable(Connection inCon, Connection outCon) throws SQLException {

		PreparedStatement pstmt1 = inCon.prepareStatement(GET_SHORTCUT_QUERY);
		PreparedStatement pstmt2 = outCon.prepareStatement(INSERT_SHORTCUT_QUERY);

		ResultSet rs = pstmt1.executeQuery();

		if (rs != null) {
			while (rs.next()) {

				String	sNodeID		= rs.getString(1);
				String	sReferenceID= rs.getString(2);

				pstmt2.setString(1, sNodeID);
				pstmt2.setString(2, sReferenceID);

				try {
					pstmt2.executeUpdate();
				}
				catch(SQLException ex) {
					errorLog.append(ex.getMessage());
					ex.printStackTrace();
				}
			}
		}
	}

	/**
	 * Convert the NodeDetail table.
	 *
	 * @exception java.sql.SQLException
	 */
	private void convertNodeDetailTable(Connection inCon, Connection outCon) throws SQLException {

		PreparedStatement pstmt1 = inCon.prepareStatement(GET_NODEDETAIL_QUERY);
		PreparedStatement pstmt2 = outCon.prepareStatement(INSERT_NODEDETAIL_QUERY);

		ResultSet rs = pstmt1.executeQuery();

		if (rs != null) {
			while (rs.next()) {

				String	sNodeID			= rs.getString(1);
				String	sAuthor			= rs.getString(2);
				int		nPage			= rs.getInt(3);
				double	dbCDate			= rs.getDouble(4);
				double	dbMDate			= rs.getDouble(5);
				String	sDetail			= rs.getString(6);

				pstmt2.setString(1, sNodeID);
				pstmt2.setString(2, sAuthor);
				pstmt2.setInt(3, nPage);
				pstmt2.setDouble(4, dbCDate);
				pstmt2.setDouble(5, dbMDate);

				if (!sDetail.equals("")) {
					//ByteArrayInputStream bArrayDetail = new ByteArrayInputStream(sDetail.getBytes());
					//pstmt2.setAsciiStream(6, bArrayDetail, bArrayDetail.available());

					// ACCOMODATES UNICODE
					StringReader reader = new StringReader(sDetail);
					pstmt2.setCharacterStream(6, reader, sDetail.length());
				}
				else {
					pstmt2.setString(6, "");
				}

				try {
					pstmt2.executeUpdate();
				}
				catch(SQLException ex) {
					errorLog.append(ex.getMessage());
					ex.printStackTrace();
				}
			}
		}
	}

	/**
	 * Convert the ViewPropery table.
	 *
	 * @exception java.sql.SQLException
	 */
	private void convertViewPropertyTable(Connection inCon, Connection outCon) throws SQLException {

		PreparedStatement pstmt1 = inCon.prepareStatement(GET_VIEWPROPERTY_QUERY);
		PreparedStatement pstmt2 = outCon.prepareStatement(INSERT_VIEWPROPERTY_QUERY);

		ResultSet rs = pstmt1.executeQuery();

		if (rs != null) {
			while (rs.next()) {

				String	sUserID				= rs.getString(1);
				String	sViewID				= rs.getString(2);
				int		nHorizontalScroll	= rs.getInt(3);
				int		nVerticalScroll		= rs.getInt(4);
				int		nWidth				= rs.getInt(5);
				int		nHeight				= rs.getInt(6);
				int		nXPos				= rs.getInt(7);
				int		nYPos				= rs.getInt(8);
				String  sIcon	 			= rs.getString(9);
				String  sMax 				= rs.getString(10);
				String	sShowTags			= rs.getString(11);
				String	sShowText			= rs.getString(12);
				String	sShowTrans			= rs.getString(13);
				String	sShowWeight			= rs.getString(14);
				String	sSmallIcons			= rs.getString(15);
				String	sHideIcons			= rs.getString(16);
				int		nLabelLength		= rs.getInt(17);
				int		nLabelWidth			= rs.getInt(18);
				int		nFontSize			= rs.getInt(19);
				String	sFontFace			= rs.getString(20);
				int		nFontStyle			= rs.getInt(21);

				pstmt2.setString(1, sUserID);
				pstmt2.setString(2, sViewID);
				pstmt2.setInt(3, nHorizontalScroll);
				pstmt2.setInt(4, nVerticalScroll);
				pstmt2.setInt(5, nWidth);
				pstmt2.setInt(6, nHeight);
				pstmt2.setInt(7, nXPos);
				pstmt2.setInt(8, nYPos);
				pstmt2.setString(9, sIcon);
				pstmt2.setString(10, sMax);
				pstmt2.setString(11, sShowTags);
				pstmt2.setString(12, sShowText);
				pstmt2.setString(13, sShowTrans);
				pstmt2.setString(14, sShowWeight);
				pstmt2.setString(15, sSmallIcons);
				pstmt2.setString(16, sHideIcons);
				pstmt2.setInt(17, nLabelLength);
				pstmt2.setInt(18, nLabelWidth);
				pstmt2.setInt(19, nFontSize);
				pstmt2.setString(20, sFontFace);
				pstmt2.setInt(21, nFontStyle);

				try {
					pstmt2.executeUpdate();
				}
				catch(SQLException ex) {
					errorLog.append(ex.getMessage());
					ex.printStackTrace();
				}
			}
		}
	}

	/**
	 * Convert the Favorite table.
	 *
	 * @exception java.sql.SQLException
	 */
	private void convertFavoriteTable(Connection inCon, Connection outCon) throws SQLException {

		PreparedStatement pstmt1 = inCon.prepareStatement(GET_FAVORITE_QUERY);
		PreparedStatement pstmt2 = outCon.prepareStatement(INSERT_FAVORITE_QUERY);

		ResultSet rs = pstmt1.executeQuery();

		if (rs != null) {
			while (rs.next()) {

				String	sUserID		= rs.getString(1);
				String	sNodeID		= rs.getString(2);
				String	sLabel		= rs.getString(3);
				int		nType		= rs.getInt(4);
				double	dbCDate		= rs.getDouble(5);
				double	dbMDate		= rs.getDouble(6);

				pstmt2.setString(1, sUserID);
				pstmt2.setString(2, sNodeID);

				if (!sLabel.equals("")) {
					//ByteArrayInputStream bArrayLabel = new ByteArrayInputStream(sLabel.getBytes());
					//pstmt2.setAsciiStream(3, bArrayLabel, bArrayLabel.available());

					// ACCOMODATES UNICODE
					StringReader reader = new StringReader(sLabel);
					pstmt2.setCharacterStream(3, reader, sLabel.length());
				}
				else {
					pstmt2.setString(3, "");
				}

				pstmt2.setInt(4, nType);
				pstmt2.setDouble(5, dbCDate);
				pstmt2.setDouble(6, dbMDate);

				try {
					pstmt2.executeUpdate();
				}
				catch(SQLException ex) {
					errorLog.append(ex.getMessage());
					ex.printStackTrace();
				}
			}
		}
	}

	/**
	 * Convert the Workspace table.
	 *
	 * @exception java.sql.SQLException
	 */
	private void convertWorkspaceTable(Connection inCon, Connection outCon) throws SQLException {

		PreparedStatement pstmt1 = inCon.prepareStatement(GET_WORKSPACE_QUERY);
		PreparedStatement pstmt2 = outCon.prepareStatement(INSERT_WORKSPACE_QUERY);

		ResultSet rs = pstmt1.executeQuery();

		if (rs != null) {
			while (rs.next()) {

				String	sWorkspaceID	= rs.getString(1);
				String	sUserID			= rs.getString(2);
				String	sName			= rs.getString(3);
				double	dbCDate			= rs.getDouble(4);
				double	dbMDate			= rs.getDouble(5);

				pstmt2.setString(1, sWorkspaceID);
				pstmt2.setString(2, sUserID);
				pstmt2.setString(3, sName);
				pstmt2.setDouble(4, dbCDate);
				pstmt2.setDouble(5, dbMDate);

				try {
					pstmt2.executeUpdate();
				}
				catch(SQLException ex) {
					errorLog.append(ex.getMessage());
					ex.printStackTrace();
				}
			}
		}
	}

	/**
	 * Convert the WorkspaceView table.
	 *
	 * @exception java.sql.SQLException
	 */
	private void convertWorkspaceViewTable(Connection inCon, Connection outCon) throws SQLException {

		PreparedStatement pstmt1 = inCon.prepareStatement(GET_WORKSPACEVIEW_QUERY);
		PreparedStatement pstmt2 = outCon.prepareStatement(INSERT_WORKSPACEVIEW_QUERY);

		ResultSet rs = pstmt1.executeQuery();

		if (rs != null) {
			while (rs.next()) {

				String	sWorkspaceID		= rs.getString(1);
				String	sViewID				= rs.getString(2);
				int		nHorizontalScroll	= rs.getInt(3);
				int		nVerticalScroll		= rs.getInt(4);
				int		nWidth				= rs.getInt(5);
				int		nHeight				= rs.getInt(6);
				int		nXPos				= rs.getInt(7);
				int		nYPos				= rs.getInt(8);
				String 	sIcon		 		= rs.getString(9);
				String 	sMax 				= rs.getString(10);

				pstmt2.setString(1, sWorkspaceID);
				pstmt2.setString(2, sViewID);
				pstmt2.setInt(3, nHorizontalScroll);
				pstmt2.setInt(4, nVerticalScroll);
				pstmt2.setInt(5, nWidth);
				pstmt2.setInt(6, nHeight);
				pstmt2.setInt(7, nXPos);
				pstmt2.setInt(8, nYPos);
				pstmt2.setString(9, sIcon);
				pstmt2.setString(10, sMax);

				try {
					pstmt2.executeUpdate();
				}
				catch(SQLException ex) {
					errorLog.append(ex.getMessage());
					ex.printStackTrace();
				}
			}
		}
	}

	/**
	 * Convert the ViewLink table.
	 *
	 * @exception java.sql.SQLException
	 */
	private void convertViewLinkTable(Connection inCon, Connection outCon) throws SQLException {

		PreparedStatement pstmt1 = inCon.prepareStatement(GET_VIEWLINK_QUERY);
		PreparedStatement pstmt2 = outCon.prepareStatement(INSERT_VIEWLINK_QUERY);

		ResultSet rs = pstmt1.executeQuery();

		if (rs != null) {
			while (rs.next()) {

				String	sViewID		= rs.getString(1);
				String	sLinkID		= rs.getString(2);
				double	dbCDate		= rs.getDouble(3);
				double	dbMDate		= rs.getDouble(4);
				int		nStatus		= rs.getInt(5);
				int		nLabelWidth	= rs.getInt(6);
				int 	nArrowStyle = rs.getInt(7);
				int 	nLinkStyle 	= rs.getInt(8);
				int 	nLinkDashed = rs.getInt(9);
				int 	nLinkWeight = rs.getInt(10);
				int 	nLinkColour = rs.getInt(11);
				int		nFontSize	= rs.getInt(12);
				String	sFontFace	= rs.getString(13);
				int		nFontStyle	= rs.getInt(14);
				int		nForeground = rs.getInt(15);
				int 	nBackground = rs.getInt(16);

				pstmt2.setString(1, sViewID);
				pstmt2.setString(2, sLinkID);
				pstmt2.setDouble(3, dbCDate);
				pstmt2.setDouble(4, dbMDate);
				pstmt2.setInt(5, nStatus);
				pstmt2.setInt(6, nLabelWidth);
				pstmt2.setInt(7, nArrowStyle);
				pstmt2.setInt(8, nLinkStyle);
				pstmt2.setInt(9, nLinkDashed);
				pstmt2.setInt(10, nLinkWeight);
				pstmt2.setInt(11, nLinkColour);
				pstmt2.setInt(12, nFontSize);
				pstmt2.setString(13, sFontFace);
				pstmt2.setInt(14, nFontStyle);
				pstmt2.setInt(15, nForeground);
				pstmt2.setInt(16, nBackground);

				try {
					pstmt2.executeUpdate();
				}
				catch(SQLException ex) {
					errorLog.append(ex.getMessage());
					ex.printStackTrace();
				}
			}
		}
	}

	/**
	 * Convert the NodeUserState table.
	 *
	 * @exception java.sql.SQLException
	 */
	private void convertNodeUserStateTable(Connection inCon, Connection outCon) throws SQLException {

		PreparedStatement pstmt1 = inCon.prepareStatement(GET_NODEUSERSTATE_QUERY);
		PreparedStatement pstmt2 = outCon.prepareStatement(INSERT_NODEUSERSTATE_QUERY);

		ResultSet rs = pstmt1.executeQuery();

		if (rs != null) {
			while (rs.next()) {

				String	sNodeID		= rs.getString(1);
				String	sUserID		= rs.getString(2);
				int		nState		= rs.getInt(3);

				pstmt2.setString(1, sNodeID);
				pstmt2.setString(2, sUserID);
				pstmt2.setInt(3, nState);

				try {
					pstmt2.executeUpdate();
				}
				catch(SQLException ex) {
					errorLog.append(ex.getMessage());
					ex.printStackTrace();
				}
			}
		}
	}

	/**
	 * Convert the Audit table.
	 *
	 * @exception java.sql.SQLException
	 */
	private void convertAuditTable(Connection inCon, Connection outCon) throws SQLException {

		PreparedStatement pstmt1 = inCon.prepareStatement(GET_AUDIT_QUERY);
		PreparedStatement pstmt2 = outCon.prepareStatement(INSERT_AUDIT_QUERY);

		ResultSet rs = pstmt1.executeQuery();

		if (rs != null) {
			while (rs.next()) {

				String	sAuditID	= rs.getString(1);
				String	sAuthor		= rs.getString(2);
				String	sItemID		= rs.getString(3);
				double	dbDate		= rs.getDouble(4);
				String	sCategory	= rs.getString(5);
				int		nAction		= rs.getInt(6);
				String	sData		= rs.getString(7);

				pstmt2.setString(1, sAuditID);
				pstmt2.setString(2, sAuthor);
				pstmt2.setString(3, sItemID);
				pstmt2.setDouble(4, dbDate);
				pstmt2.setString(5, sCategory);
				pstmt2.setInt(6, nAction);
				pstmt2.setString(7, sData);

				try {
					pstmt2.executeUpdate();
				}
				catch(SQLException ex) {
					errorLog.append(ex.getMessage());
					ex.printStackTrace();
				}
			}
		}
	}

	/**
	 * Convert the Permission table.
	 *
	 * @exception java.sql.SQLException
	 */
	private void convertPermissionTable(Connection inCon, Connection outCon) throws SQLException {

		PreparedStatement pstmt1 = inCon.prepareStatement(GET_PERMISSION_QUERY);
		PreparedStatement pstmt2 = outCon.prepareStatement(INSERT_PERMISSION_QUERY);

		ResultSet rs = pstmt1.executeQuery();

		if (rs != null) {
			while (rs.next()) {

				String	sItemID		= rs.getString(1);
				String	sGroupID	= rs.getString(2);
				int	nPermission		= rs.getInt(3);

				pstmt2.setString(1, sItemID);
				pstmt2.setString(2, sGroupID);
				pstmt2.setInt(3, nPermission);

				try {
					pstmt2.executeUpdate();
				}
				catch(SQLException ex) {
					errorLog.append(ex.getMessage());
					ex.printStackTrace();
				}
			}
		}
	}

	/**
	 * Convert the Clone table.
	 *
	 * @exception java.sql.SQLException
	 */
	private void convertCloneTable(Connection inCon, Connection outCon) throws SQLException {

		PreparedStatement pstmt1 = inCon.prepareStatement(GET_CLONE_QUERY);
		PreparedStatement pstmt2 = outCon.prepareStatement(INSERT_CLONE_QUERY);

		ResultSet rs = pstmt1.executeQuery();

		if (rs != null) {
			while (rs.next()) {

				String	sParentNodeID	= rs.getString(1);
				String	sChildNodeID	= rs.getString(2);

				pstmt2.setString(1, sParentNodeID);
				pstmt2.setString(2, sChildNodeID);

				try {
					pstmt2.executeUpdate();
				}
				catch(SQLException ex) {
					errorLog.append(ex.getMessage());
					ex.printStackTrace();
				}
			}
		}
	}

	/**
	 * Convert the ExtendedNode table.
	 *
	 * @exception java.sql.SQLException
	 */
	private void convertExtendedNodeTable(Connection inCon, Connection outCon) throws SQLException {

		PreparedStatement pstmt1 = inCon.prepareStatement(GET_EXTENDEDNODE_QUERY);
		PreparedStatement pstmt2 = outCon.prepareStatement(INSERT_EXTENDEDNODE_QUERY);

		ResultSet rs = pstmt1.executeQuery();

		if (rs != null) {
			while (rs.next()) {

				String	sExtendedNodeTypeID	= rs.getString(1);
				String	sAuthor			= rs.getString(2);
				double	dbCDate			= rs.getDouble(3);
				double	dbMDate			= rs.getDouble(4);
				String	sName			= rs.getString(5);
				String	sDesc			= rs.getString(6);
				int		nBaseNodeType	= rs.getInt(7);
				String	sIcon			= rs.getString(8);

				pstmt2.setString(1, sExtendedNodeTypeID);
				pstmt2.setString(2, sAuthor);
				pstmt2.setDouble(3, dbCDate);
				pstmt2.setDouble(4, dbMDate);
				pstmt2.setString(5, sName);
				pstmt2.setString(6, sDesc);
				pstmt2.setInt(7, nBaseNodeType);
				pstmt2.setString(8, sIcon);

				try {
					pstmt2.executeUpdate();
				}
				catch(SQLException ex) {
					errorLog.append(ex.getMessage());
					ex.printStackTrace();
				}
			}
		}
	}

	/**
	 * Convert the ExtendedCode table.
	 *
	 * @exception java.sql.SQLException
	 */
	private void convertExtendedCodeTable(Connection inCon, Connection outCon) throws SQLException {

		PreparedStatement pstmt1 = inCon.prepareStatement(GET_EXTENDEDCODE_QUERY);
		PreparedStatement pstmt2 = outCon.prepareStatement(INSERT_EXTENDEDCODE_QUERY);

		ResultSet rs = pstmt1.executeQuery();

		if (rs != null) {
			while (rs.next()) {

				String	sExtendedNodeTypeID	= rs.getString(1);
				String	sCodeID	= rs.getString(2);

				pstmt2.setString(1, sExtendedNodeTypeID);
				pstmt2.setString(2, sCodeID);

				try {
					pstmt2.executeUpdate();
				}
				catch(SQLException ex) {
					errorLog.append(ex.getMessage());
					ex.printStackTrace();
				}
			}
		}
	}

	/**
	 * Convert the UserGroup table.
	 *
	 * @exception java.sql.SQLException
	 */
	private void convertUserGroupTable(Connection inCon, Connection outCon) throws SQLException {

		PreparedStatement pstmt1 = inCon.prepareStatement(GET_USERGROUP_QUERY);
		PreparedStatement pstmt2 = outCon.prepareStatement(INSERT_USERGROUP_QUERY);

		ResultSet rs = pstmt1.executeQuery();

		if (rs != null) {
			while (rs.next()) {

				String	sGroupID	= rs.getString(1);
				String	sUserID		= rs.getString(2);
				double	dbCDate		= rs.getDouble(3);
				double	dbMDate		= rs.getDouble(4);
				String	sName		= rs.getString(5);
				String	sDesc		= rs.getString(6);

				pstmt2.setString(1, sGroupID);
				pstmt2.setString(2, sUserID);
				pstmt2.setDouble(3, dbCDate);
				pstmt2.setDouble(4, dbMDate);
				pstmt2.setString(5, sName);
				pstmt2.setString(6, sDesc);

				try {
					pstmt2.executeUpdate();
				}
				catch(SQLException ex) {
					errorLog.append(ex.getMessage());
					ex.printStackTrace();
				}
			}
		}
	}

	/**
	 * Convert the GroupUser table.
	 *
	 * @exception java.sql.SQLException
	 */
	private void convertGroupUserTable(Connection inCon, Connection outCon) throws SQLException {

		PreparedStatement pstmt1 = inCon.prepareStatement(GET_GROUPUSER_QUERY);
		PreparedStatement pstmt2 = outCon.prepareStatement(INSERT_GROUPUSER_QUERY);

		ResultSet rs = pstmt1.executeQuery();

		if (rs != null) {
			while (rs.next()) {

				String	sUserID		= rs.getString(1);
				String	sGroupID	= rs.getString(2);

				pstmt2.setString(1, sUserID);
				pstmt2.setString(2, sGroupID);

				try {
					pstmt2.executeUpdate();
				}
				catch(SQLException ex) {
					errorLog.append(ex.getMessage());
					ex.printStackTrace();
				}
			}
		}
	}

	/**
	 * Convert the ViewLayer table.
	 *
	 * @exception java.sql.SQLException
	 */
	private void convertViewLayerTable(Connection inCon, Connection outCon) throws SQLException {

		PreparedStatement pstmt1 = inCon.prepareStatement(GET_VIEWLAYER_QUERY);
		PreparedStatement pstmt2 = outCon.prepareStatement(INSERT_VIEWLAYER_QUERY);

		ResultSet rs = pstmt1.executeQuery();

		if (rs != null) {
			while (rs.next()) {

				String	sViewID		= rs.getString(1);
				String	sScribble	= rs.getString(2);
				String	sBackground	= rs.getString(3);
				String	sGrid		= rs.getString(4);
				String	sShapes		= rs.getString(5);
				int 	nBackgroundColor = rs.getInt(6);

				pstmt2.setString(1, sViewID);
				pstmt2.setString(2, sScribble);
				pstmt2.setString(3, sBackground);
				pstmt2.setString(4, sGrid);
				pstmt2.setString(5, sShapes);
				pstmt2.setInt(6, nBackgroundColor);

				try {
					pstmt2.executeUpdate();
				}
				catch(SQLException ex) {
					errorLog.append(ex.getMessage());
					ex.printStackTrace();
				}
			}
		}
	}

	/**
	 * Convert the Connections table.
	 *
	 * @exception java.sql.SQLException
	 */
	private void convertConnectionTable(Connection inCon, Connection outCon) throws SQLException {

		PreparedStatement pstmt1 = inCon.prepareStatement(GET_CONNECTION_QUERY);
		PreparedStatement pstmt2 = outCon.prepareStatement(INSERT_CONNECTION_QUERY);

		ResultSet rs = pstmt1.executeQuery();

		if (rs != null) {
			while (rs.next()) {

				String	sUserID		= rs.getString(1);
				String	sProfile	= rs.getString(2);
				int		nType		= rs.getInt(3);
				String	sServer		= rs.getString(4);
				String	sLogin		= rs.getString(5);
				String	sPassword	= rs.getString(6);
				String	sName		= rs.getString(7);
				int		nPort		= rs.getInt(8);
				String	sResource	= rs.getString(9);

				pstmt2.setString(1, sUserID);
				pstmt2.setString(2, sProfile);
				pstmt2.setInt(3, nType);
				pstmt2.setString(4, sServer);
				pstmt2.setString(5, sLogin);
				pstmt2.setString(6, sPassword);
				pstmt2.setString(7, sName);
				pstmt2.setInt(8, nPort);
				pstmt2.setString(9, sResource);

				try {
					pstmt2.executeUpdate();
				}
				catch(SQLException ex) {
					errorLog.append(ex.getMessage());
					ex.printStackTrace();
				}
			}
		}
	}

	/**
	 * Convert the Preference table.
	 *
	 * @exception java.sql.SQLException
	 */
	private void convertPreferenceTable(Connection inCon, Connection outCon) throws SQLException {

		PreparedStatement pstmt1 = inCon.prepareStatement(GET_PREFERENCE_QUERY);
		PreparedStatement pstmt2 = outCon.prepareStatement(INSERT_PREFERENCE_QUERY);

		ResultSet rs = pstmt1.executeQuery();

		if (rs != null) {
			while (rs.next()) {

				String	sUserID		= rs.getString(1);
				String	sProperty	= rs.getString(2);
				String	sContents	= rs.getString(3);

				pstmt2.setString(1, sUserID);
				pstmt2.setString(2, sProperty);
				pstmt2.setString(3, sContents);

				try {
					pstmt2.executeUpdate();
				}
				catch(SQLException ex) {
					errorLog.append(ex.getMessage());
					ex.printStackTrace();
				}
			}
		}
	}

	/**
	 * convert the Meeting table.
	 *
	 * @exception java.sql.SQLException
	 */
	private void convertMeetingTable(Connection inCon, Connection outCon) throws SQLException {

		PreparedStatement pstmt1 = inCon.prepareStatement(GET_MEETING_QUERY);
		PreparedStatement pstmt2 = outCon.prepareStatement(INSERT_MEETING_QUERY);

		ResultSet rs = pstmt1.executeQuery();

		if (rs != null) {
			while (rs.next()) {

				String	sMeetingID		= rs.getString(1);
				String	sMeetingMapID	= rs.getString(2);
				String 	sMeetingName	= rs.getString(3);
				double	dbMeetingDate	= rs.getDouble(4);
				int		nStatus			= rs.getInt(5);

				pstmt2.setString(1, sMeetingID);
				pstmt2.setString(2, sMeetingMapID);
				pstmt2.setString(3, sMeetingName);
				pstmt2.setDouble(4, dbMeetingDate);
				pstmt2.setInt(5, nStatus);

				try {
					pstmt2.executeUpdate();
				}
				catch(SQLException ex){
					errorLog.append(ex.getMessage());
					ex.printStackTrace();
				}
			}
		}
	}


	/**
	 * convert the MediaIndex table.
	 *
	 * @exception java.sql.SQLException
	 */
	private void convertMediaIndexTable(Connection inCon, Connection outCon) throws SQLException {

		PreparedStatement pstmt1 = inCon.prepareStatement(GET_MEDIAINDEX_QUERY);
		PreparedStatement pstmt2 = outCon.prepareStatement(INSERT_MEDIAINDEX_QUERY);

		ResultSet rs = pstmt1.executeQuery();

		if (rs != null) {
			while (rs.next()) {

				String	sViewID		= rs.getString(1);
				String	sNodeID		= rs.getString(2);
				String 	sMeetingID	= rs.getString(3);
				double	dbMediaIndex= rs.getDouble(4);
				double	dbCreationDate= rs.getDouble(5);
				double	dbModificationDate= rs.getDouble(6);

				pstmt2.setString(1, sViewID);
				pstmt2.setString(2, sNodeID);
				pstmt2.setString(3, sMeetingID);
				pstmt2.setDouble(4, dbMediaIndex);
				pstmt2.setDouble(5, dbCreationDate);
				pstmt2.setDouble(6, dbModificationDate);

				try {
					pstmt2.executeUpdate();
				}
				catch(SQLException ex){
					errorLog.append(ex.getMessage());
					ex.printStackTrace();
				}
			}
		}
	}

	/**
	 * convert the LinkedFile table;
	 * @param inCon the input connection
	 * @param outCon the output connection
	 * @throws SQLException thrown if an error occurs while copying the data
	 */
	private void convertLinkedFileTable(Connection inCon, Connection outCon) throws SQLException {
		PreparedStatement pstmt1 = inCon.prepareStatement(GET_LINKEDFILE_QUERY);
		PreparedStatement pstmt2 = outCon.prepareStatement(INSERT_LINKEDFILE_QUERY);

		ResultSet rs = pstmt1.executeQuery();

		if (rs != null) {
			while (rs.next()) {

				String	sFileID		= rs.getString(1);
				String	sFileName	= rs.getString(2);
				int 	iFileSize	= rs.getInt(3);
				byte[]  aFileData   = rs.getBytes(4);

				pstmt2.setString(1, sFileID);
				pstmt2.setString(2, sFileName);
				pstmt2.setInt(3, iFileSize);
				pstmt2.setBytes(4, aFileData);

				try {
					pstmt2.executeUpdate();
				}
				catch(SQLException ex){
					errorLog.append(ex.getMessage());
					ex.printStackTrace();
				}
			}
		}
	}

	/**
	 * Convert the ViewNode table.
	 *
	 * @exception java.sql.SQLException
	 */
	private void convertViewTimeNodeTable(Connection inCon, Connection outCon) throws SQLException {

		PreparedStatement pstmt1 = inCon.prepareStatement(GET_VIEWTIMENODE_QUERY);
		PreparedStatement pstmt2 = outCon.prepareStatement(INSERT_VIEWTIMENODE_QUERY);

		ResultSet rs = pstmt1.executeQuery();

		if (rs != null) {
			while (rs.next()) {

				String	sViewTimeNodeID = rs.getString(1);
				String	sViewID		= rs.getString(2);
				String	sNodeID		= rs.getString(3);
				double	nShow		= rs.getDouble(4);
				double	nHide		= rs.getDouble(5);
				int		nXPos		= rs.getInt(6);
				int		nYPos		= rs.getInt(7);
				double	dbCDate		= rs.getDouble(8);
				double	dbMDate		= rs.getDouble(9);
				int		nStatus		= rs.getInt(10);

				pstmt2.setString(1, sViewTimeNodeID);
				pstmt2.setString(2, sViewID);
				pstmt2.setString(3, sNodeID);
				pstmt2.setDouble(4, nShow);
				pstmt2.setDouble(5, nHide);
				pstmt2.setInt(6, nXPos);
				pstmt2.setInt(7, nYPos);
				pstmt2.setDouble(8, dbCDate);
				pstmt2.setDouble(9, dbMDate);
				pstmt2.setInt(10, nStatus);

				try {
					pstmt2.executeUpdate();
				}
				catch(SQLException ex) {
					errorLog.append(ex.getMessage());
					ex.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * Convert the Movies table.
	 *
	 * @exception java.sql.SQLException
	 */
	private void convertMoviesTable(Connection inCon, Connection outCon) throws SQLException {

		PreparedStatement pstmt1 = inCon.prepareStatement(GET_MOVIES_QUERY);
		PreparedStatement pstmt2 = outCon.prepareStatement(INSERT_MOVIES_QUERY);

		ResultSet rs = pstmt1.executeQuery();

		if (rs != null) {
			while (rs.next()) {

				String	sMovieID = rs.getString(1);
				String	sViewID		= rs.getString(2);
				String	sLink		= rs.getString(3);
				double	dbCDate		= rs.getDouble(4);
				double	dbMDate		= rs.getDouble(5);
				String	sName		= rs.getString(6);
				double	dbStartTime	= rs.getDouble(7);

				pstmt2.setString(1, sMovieID);
				pstmt2.setString(2, sViewID);
				pstmt2.setString(3, sLink);
				pstmt2.setDouble(4, dbCDate);
				pstmt2.setDouble(5, dbMDate);
				pstmt2.setString(6, sName);
				pstmt2.setDouble(7, dbStartTime);

				try {
					pstmt2.executeUpdate();
				}
				catch(SQLException ex) {
					errorLog.append(ex.getMessage());
					ex.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * Convert the MovieProperties table.
	 *
	 * @exception java.sql.SQLException
	 */
	private void convertMoviePropertiesTable(Connection inCon, Connection outCon) throws SQLException {

		PreparedStatement pstmt1 = inCon.prepareStatement(GET_MOVIEPROPERTIES_QUERY);
		PreparedStatement pstmt2 = outCon.prepareStatement(INSERT_MOVIEPROPERTIES_QUERY);

		ResultSet rs = pstmt1.executeQuery();

		if (rs != null) {
			while (rs.next()) {

				String	sMoviePropertyID = rs.getString(1);
				String	sMovieID = rs.getString(2);
				int		nXPos		= rs.getInt(3);
				int		nYPos		= rs.getInt(4);
				int		width		= rs.getInt(5);
				int		height		= rs.getInt(6);
				float 	fTransparency = rs.getFloat(7);
				double	time		= rs.getDouble(8);
				double	dbCDate		= rs.getDouble(9);
				double	dbMDate		= rs.getDouble(10);

				pstmt2.setString(1, sMoviePropertyID);
				pstmt2.setString(2, sMovieID);
				pstmt2.setInt(3, nXPos);
				pstmt2.setInt(4, nYPos);
				pstmt2.setInt(5, width);
				pstmt2.setInt(6, height);
				pstmt2.setFloat(7, fTransparency);
				pstmt2.setDouble(8, time);
				pstmt2.setDouble(9, dbCDate);
				pstmt2.setDouble(10, dbMDate);

				try {
					pstmt2.executeUpdate();
				}
				catch(SQLException ex) {
					errorLog.append(ex.getMessage());
					ex.printStackTrace();
				}
			}
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
