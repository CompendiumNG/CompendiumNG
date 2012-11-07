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
package com.compendium.core.datamodel;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.SQLException;

import com.compendium.core.ICoreConstants;
import com.compendium.core.datamodel.services.ILinkedFileService;

/**
 * Pointer to a database copy of the original file ("linkedFile://" URI).
 *  
 * @author rudolf
 * @author Sebastian Ehrich
 */
public class LinkedFileDatabase extends LinkedFile {

	private static final long serialVersionUID = 4400222583236118695L;
	
	/**
	 * Determines whether a given URI points to a file in the database.
	 * @param fileUri the URI to check
	 * @return <code>True</code>if and only if the given URI point to
	 * a linked file in the database, <code>false</code> otherwise.
	 */
	public static boolean isDatabaseURI(URI fileUri) {
		return isDatabaseURI(fileUri.toString());
	}

	/**
	 * Determines whether an URI given as a string points to a file in the database.
	 * @param path the string to check 
	 * @return <code>True</code>if and only if the given URI point to
	 * a linked file in the database, <code>false</code> otherwise.
	 */	
	public static boolean isDatabaseURI(String path) {
		return path.startsWith(ICoreConstants.sDATABASE_REFERENCE);
	}
	
	/**
	 * Constructor
	 * @param fileID the ID for this IdObject
	 * @param fileName the name of the source file (without path)
	 */
	public LinkedFileDatabase(String fileID, String fileName) {
		this.fileName = fileName;
		setId(fileID);
		try {
			URI  fileUri = new URI("linkedFile", fileID, "/" + fileName, null);
			this.filePath = fileUri.toString();
		} 
		catch (URISyntaxException e) {
			System.err.println("LinkedFile(): Error in URI");
			e.printStackTrace();
		}
	}

	/**
	 * Constructor
	 * @param fileUri
	 * @precondition isDatabaseURI(fileUri)
	 */
	public LinkedFileDatabase(URI fileUri) {
		this( fileUri.getHost(), // ID is encoded into the host part of the URI
				fileUri.getPath().substring(fileUri.getPath().lastIndexOf('/') + 1)
		);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public LFType getLFType() {
		return LFType.DATABASE;
	}
	
	/**
	 * Returns a <code>File</code> reference to the linked file
	 * @param tempDir the temporary directory to put the database file in.
	 * @return reference to the linked file
	 * @throws ModelSessionException 
	 * @throws SQLException 
	 */
	@Override
	public File getFile(URI tempDir) throws IOException, SQLException, ModelSessionException {
		if (oModel == null)
			throw new ModelSessionException("Model is null in NodeSummary.setOriginalID");
		if (oSession == null) {
			oSession = oModel.getSession();
			if (oSession == null)
				throw new ModelSessionException("Session is null in NodeSummary.setOriginalID");
		}
		
		ILinkedFileService lfs = oModel.getLinkedFileService();
		File file = null;
		File temp = createUniqueTempDirectory(this, tempDir);
		if (temp != null) {
			file = lfs.getFile(oSession, this, temp);
			// delete file if Compendium / the JVM exits
			file.deleteOnExit();
			temp.deleteOnExit();
		}
		return file;
	}

	/**
	 * Create a unique temporary directory for the given LinkedFile. We need generated unique names
	 * because there could be multiple files with the same name. We generate a dedicated temporary
	 * directory for each file because we want the file itself to retain 
	 * its genuine name so external viewing/editing apps will display the proper name which is 
	 * known to the user.
	 * 
	 * @param lf the database object to create a temporary directory for. This is only needed to 
	 * 	give the directory a suffix that denotes the file it holds.
	 * @param tempDir the temporary directory to use.
	 * @return the File object representing the temporary directory or null if the directory could
	 * 	not be created.
	 */
	private static File createUniqueTempDirectory(LinkedFile lf, URI tempDir) {
		File compTempDir = new File(tempDir);
			
			// the global Compendium temp dir		
			File temp = new File(compTempDir,"compendium-"+lf.getId().toString()+System.getProperty("file.separator"));
			if(temp.exists())
				return temp;
			if (!temp.mkdir()) {
				return null;
			}
			else {
				temp.deleteOnExit();
				return temp;
			}
	}

		
	/**
	 * @see com.compendium.core.datamodel.LinkedFile#exportFile(java.io.File) 
	 */
	@Override	
	public File exportFile(File destDir) throws IOException, ModelSessionException{
		if (oModel == null)
			throw new ModelSessionException("Model is null in LinkedFileDatabase.exportFile");
		if (oSession == null) {
			oSession = oModel.getSession();
			if (oSession == null)
				throw new ModelSessionException("Session is null in LinkedFileDatabase.exportFile");
		}

		if(!destDir.isDirectory())
			throw new IOException("Given argument is not a directory.");
		ILinkedFileService lfs = oModel.getLinkedFileService();
		return lfs.getFile(oSession, this, destDir);
	}
	
	/**
	 * @see com.compendium.core.datamodel.LinkedFile#delete()
	 */
	@Override
	public boolean delete() throws SQLException, ModelSessionException {
		if (oModel == null)
			throw new ModelSessionException("Model is null in LinkedFileDatabase.delete");
		if (oSession == null) {
			oSession = oModel.getSession();
			if (oSession == null)
				throw new ModelSessionException("Session is null in LinkedFileDatabase.delete");
		}

		ILinkedFileService lfs = oModel.getLinkedFileService();
		return lfs.deleteFile(oSession, this);
	}	
}
