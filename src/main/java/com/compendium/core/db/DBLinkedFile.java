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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Vector;


import com.compendium.ProjectCompendium;
import com.compendium.core.ICoreConstants;
import com.compendium.core.datamodel.LinkedFile;
import com.compendium.core.datamodel.LinkedFileDatabase;
import com.compendium.core.datamodel.LinkedFile.LFType;
import com.compendium.core.db.management.DBConnection;

/**
 * The DBLinkedFiles class is the interface which is used to access the
 * LinkedFiles table in the database
 * @author Sebastian Ehrich
 */
public class DBLinkedFile {
	private final static String INSERT_FILE_QUERY = "INSERT INTO LinkedFile (fileID, fileName, fileSize, fileData)"
			+ "VALUES (? , ?, ?, ?) ";

	private final static String GET_FILE_QUERY = "SELECT * FROM LinkedFile WHERE FileId = ?";

	private final static String GET_FILES_QUERY = "SELECT FileID, FileName FROM LinkedFile";
	private final static String UPDATE_FILE_QUERY = "UPDATE LinkedFile SET FileName= ?, FileSize= ?, FileData = ? WHERE FileID = ?";

	private final static String DELETE_FILE_QUERY = "DELETE FROM LinkedFile WHERE fileID = ?";

	private final static String REFERENCE_COUNT_QUERY = "SELECT rn.NodeID FROM ReferenceNode rn JOIN Node n ON rn.NodeID = n.NodeID"
			+"  WHERE (rn.Source = ? OR rn.ImageSource = ? AND n.CurrentStatus != ?";
	
	/**
	 * Inserts the given file into the linked file table of the database
	 * @param dbcon the database connection
	 * @param fileID a unique id of the file
	 * @param file the file to insert into the database
	 * @return a LinkedFile object for the inserted file
	 * @throws IOException Occurs if there are errors while reading the input file.
	 */
	public static LinkedFile insert(DBConnection dbcon, String fileID, File file) throws IOException {
		Connection conn = dbcon.getConnection();
		if (conn == null)
			return null;
		PreparedStatement pstmt = null;
		try {
			pstmt = conn.prepareStatement(INSERT_FILE_QUERY);
		} catch (SQLException e) {
			System.err.println("DBLinkedFiles.insert(): Could not prepare insert query.");
			e.printStackTrace();
			throw new IOException("DBLinkedFiles.insert(): Could not prepare insert query.");
		}

		// read the file
		FileInputStream fileIn;
		fileIn = new FileInputStream(file);
		byte[] fileData = new byte[fileIn.available()];
		int bytesRead = fileIn.read(fileData);
		fileIn.close();
		
		// set the parameters
		try {
			pstmt.setString(1, fileID);
			pstmt.setString(2, file.getName());
			pstmt.setInt(3, bytesRead);
			pstmt.setBytes(4, fileData);
		} catch (SQLException e) {
			System.err
					.println("DBLinkedFiles.insert(): Could not set parameters.");
			e.printStackTrace();
			throw new IOException("DBLinkedFiles.insert(): Could not set parameters.");
		}
		int rowCount = 0;
		try {
			rowCount = pstmt.executeUpdate();
		} catch (SQLException e) {
			System.err
					.println("DBLinkedFiles.insert(): Could not execute update.");
			e.printStackTrace();
			throw new IOException("DBLinkedFiles.insert(): Could not execute update.");
		}
		try {
			pstmt.close();
		} catch (SQLException e) {
			System.err
					.println("DBLinkedFiles.insert(): Could not close statement.");
			e.printStackTrace();
		}
		
		LinkedFile lf = null;
		if (rowCount > 0) {
			lf = new LinkedFileDatabase(fileID, file.getName());
		}

		return (lf);
	}


	/**
	 * Copy the contents of a database object to a file.
	 * @param dbcon the database connection to use
	 * @param lFile the object representing the database object
	 * @param file the file or directory to copy the database object to
	 * @return the file the database object was copied to or null if copying failed
	 * @throws IOException
	 */
	public static File get(DBConnection dbcon, LinkedFile lFile, File file)
			throws IOException {
		assert( lFile.getLFType() == LFType.DATABASE );
		Connection conn = dbcon.getConnection();
		if (conn == null)
			return null;
		PreparedStatement pstmt = null;
		try {
			pstmt = conn.prepareStatement(GET_FILE_QUERY);
		} catch (SQLException e) {
			System.err
					.println("DBLinkedFiles.get(): Could not prepare select query.");
			e.printStackTrace();
			return null;
		}
		try {
			pstmt.setString(1, lFile.getId());
		} catch (SQLException e) {
			System.err
					.println("DBLinkedFiles.get(): Could not set parameters.");
			e.printStackTrace();
			return null;
		}
		ResultSet rs = null;
		try {
			rs = pstmt.executeQuery();
		} catch (SQLException e) {
			System.err.println("DBLinkedFile.get(): Could not execute query.");
			e.printStackTrace();
			return null;
		}
		FileOutputStream fileOut = null;
		try {
			if (file.isDirectory()) {
				file = new File( file.getPath() + File.separator 
						+ lFile.getName().replaceAll(" ", "%20") );
			}
			if (rs.next()) {
				fileOut = new FileOutputStream(file);
				fileOut.write(rs.getBytes(4));
				fileOut.close();
			}
		} catch (SQLException e) {
			System.err
					.println("DBLinkedFile.get(): Could not read result set.");
			e.printStackTrace();
			return null;
		}
		return file;
	}

	/**
	 * Delete the given linkedFile from the database
	 * @param dbcon the database connection
	 * @param linkedFile the LinkedFile object which should be deleted
	 * @return <code>True</code>if and only if the linked file is deleted, <code>false</code>
	 * otherwise.
	 */
	public static boolean del(DBConnection dbcon, LinkedFile linkedFile) throws SQLException {
		assert( linkedFile.getLFType() == LFType.DATABASE );
		Connection conn = dbcon.getConnection();
		if (conn == null)
			return false;
		PreparedStatement pstmt = null;
		try {
			pstmt = conn.prepareStatement(DELETE_FILE_QUERY);
		} catch (SQLException e) {
			System.err
					.println("DBLinkedFiles.del(): Could not prepare delete query.");
			e.printStackTrace();
			throw e;
		}

		try {
			pstmt.setString(1, linkedFile.getId());
		} catch (SQLException e) {
			System.err.println("DBLinkedFiles.del(): Could not set parameter.");
			e.printStackTrace();
			throw e;
		}

		try {
			pstmt.executeUpdate();
		} catch (SQLException e) {
			System.err
					.println("DBLinkedFiles.del(): Could not execute update.");
			e.printStackTrace();
			throw e;
		}
		return true;
	}

	/**
	 * @see ILinkedFileService#updateFile(com.compendium.core.datamodel.PCSession, LinkedFile, File)updateFile
	 */
	public static void update(DBConnection dbcon, LinkedFile linkedFile,
			File file) throws IOException {
		
		assert( linkedFile.getLFType() == LFType.DATABASE );
		Connection conn = dbcon.getConnection();
		if (conn == null) {
			throw new IOException("Could not get database connection.");
		}
		PreparedStatement pstmt = null;
		try {
			pstmt = conn.prepareStatement(UPDATE_FILE_QUERY);
		} 
		catch (SQLException e) {
			System.err
					.println("DBLinkedFile.update(): Could not prepare update query.");
			e.printStackTrace();
			throw new IOException(e.getMessage());
		}

		FileInputStream fileIn;
		fileIn = new FileInputStream(file);
		byte[] fileData = new byte[fileIn.available()];
		int bytesRead = fileIn.read(fileData);
		try {
			pstmt.setString(1, file.getName());
			pstmt.setInt(2, bytesRead);
			pstmt.setBytes(3, fileData);
			pstmt.setString(4, linkedFile.getId());
		} 
		catch (SQLException e) {
			System.err
					.println("DBLinkedFiles.update(): Could not set parameters.");
			e.printStackTrace();
			throw new IOException(e.getMessage());
		}
		int rowCount = 0;
		try {
			rowCount = pstmt.executeUpdate();
		} catch (SQLException e) {
			System.err
					.println("DBLinkedFiles.update(): Could not execute update.");
			e.printStackTrace();
			throw new IOException(e.getMessage());
		}
		try {
			pstmt.close();
		} catch (SQLException e) {
			System.err
					.println("DBLinkedFiles.update(): Could not close statement.");
			e.printStackTrace();
		}
		if (rowCount <= 0) {
			throw new IOException("DBLinkedFiles.update(): no matching row found.");
		}	
	}
	
	/**
	 * Reads all linked files from the database
	 * @param dbcon the database connection
	 * @return A Vector containing all the linked files in the database.
	 */
	public static Vector<LinkedFile> readAllLinkedFiles(DBConnection dbcon) throws SQLException {
		Connection conn = dbcon.getConnection();
		Vector<LinkedFile> vtLinkedFiles = new Vector<LinkedFile>();
		
		if (conn == null)
			return vtLinkedFiles;
		
		PreparedStatement pstmt = null;
		try {
			pstmt = conn.prepareStatement(GET_FILES_QUERY);
		} catch (SQLException e) {
			System.err
					.println("DBLinkedFiles.readAllLinkedFiles(): Could not prepare select query.");
			e.printStackTrace();
			throw e;
		}
		
		ResultSet rs = null;
		try {
			rs = pstmt.executeQuery();
		} catch (SQLException e) {
			System.err.println("DBLinkedFile.readAllLinkedFiles(): Could not execute query.");
			e.printStackTrace();
			throw e;
		}
		LinkedFile lf;

		try {
			while(rs.next())
			{
				try {
					lf = new LinkedFileDatabase(rs.getString(1), rs.getString(2));
					vtLinkedFiles.add(lf);
				} catch (SQLException e) {
					System.err.println("DBLinkedFile.readAllLinkedFiles(): Could not read row.");
					e.printStackTrace();
				}
			}
		} catch (SQLException e) {
			System.err.println("DBLinkedFile.readAllLinkedFiles(): Could not advance result set.");
			e.printStackTrace();
			throw e;
		}

		return vtLinkedFiles;		
	}

	/**
	 * Check for a given linked file, whether there are references to this file.
	 * @param dbcon the database connection to use
	 * @param linkedFileUri the complete uri of the linked file which 
	 * 	  	  to check for existing references
	 * @return <code>True</code>if and only if there are referenced to the 
	 * 		   linked file, <code>false</code> otherwise.
	 */
	
	public static int referenceCount(DBConnection dbcon, URI linkedFileUri) throws SQLException	{
		
		Connection conn = dbcon.getConnection();
				
		if (conn == null)
			throw new SQLException("No database conncetion available.");
		PreparedStatement pstmt = null;
		try {
			pstmt = conn.prepareStatement(REFERENCE_COUNT_QUERY);
		} catch (SQLException e) {
			System.err
					.println("DBLinkedFiles.referenceCount(): Could not prepare select query.");
			throw e;
		}
		try {
			pstmt.setString(1, linkedFileUri.toString());
			pstmt.setString(2, linkedFileUri.toString());
			pstmt.setInt(3, ICoreConstants.STATUS_DELETE);
		} catch (SQLException e) {
			System.err
					.println("DBLinkedFiles.referenceCount(): Could not set parameters.");
			throw e;
		}
		ResultSet rs = null;
		try {
			rs = pstmt.executeQuery();
		} catch (SQLException e) {
			System.err.println("DBLinkedFile.referenceCount(): Could not execute query.");
			throw e;
		}
		int rowCount = -1;
		try {		
			while(rs.next())
				rowCount++;
			// as we started at -1 add one to gain the correct result
			rowCount++;
		} catch (SQLException e) {
			System.err.println("DBLinkedFile.referenceCount(): Could not retrieve number of rows.");
			throw e;
		}
		return rowCount;
	}

}
