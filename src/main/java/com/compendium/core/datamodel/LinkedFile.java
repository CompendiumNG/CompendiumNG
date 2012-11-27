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

/** 
 * Abstract base class for a Compendium object representing a file. LinkedFile objects 
 * come in three flavours:
 * 
 * 1) Reference to the original file in the file system ("file://" URI)
 * 2) Reference to a copy of the original file, residing in the dedicated "Linked Files"
 * 	folder ("file://[Linked File Folder]" URI )
 * 3) Pointer to a database copy of the original file ("linkedFile://" URI). 
 * 
 * @author Sebastian Ehrich
 * @author rudolf
 */
public abstract class LinkedFile extends IdObject {
	
	/**
	 * This enum denotes the different kinds of linked files.
	 */
	public enum LFType {
		/**
		 * A link to the original file
		 */
		LINK,
		/**
		 * A file in the linked files folder
		 */
		COPY, 
		/**
		 * A file in the database
		 */
		DATABASE };

	/**
	 * the URI of the file
	 */
	protected String filePath = null;
	
	/**
	 * the file name
	 */
	protected String fileName = null;

	/**
	 * Constructor
	 */
	protected LinkedFile() {}

	/**
	 * Constructor which builds a new object from a String.
	 * @param file
	 */
	protected LinkedFile(String file) {
		this.filePath = file;
		this.fileName =  new File(file).getName();
	}

	/**
	 * Returns the name of the linked file.
	 * @return file name of the linked file.
	 */
	public String getName() {
		return fileName;
	}
	
	public String getSourcePath() {
		return filePath;
	}
	
	/**
	 * Returns the type of the linked file.
 	 * @see LFType
	 * @return the type of the linked file
	 */
	public abstract LFType getLFType(); 
	
	/**
	 * @param destDir The directory in which to copy the file to.
	 * @return Returns a reference to the newly created file, oder null 
	 * 			if the file could not be created.
	 * @throws IOException If the given reference does not point to a directory.
	 * @throws ModelSessionException 
	 */
	public abstract File exportFile(File destDir) throws IOException, ModelSessionException;
	
	/**
	 * Returns a <code>File</code> reference to the linked file
	 * @param tempDir the temporary directory to put the database file in.
	 * @return reference to the linked file
	 * @throws ModelSessionException 
	 * @throws SQLException 
	 * @throws IOException 
	 */
	public File getFile(URI tempDir) throws SQLException, ModelSessionException, IOException {
		return new File(filePath);
	}
	
	/**
	 * Deletes the linked file.
	 * @return <code>true</code> if and only if the file or 
	 * directory is successfully deleted; <code>false</code> otherwise
	 * @throws SQLException 
	 * @throws ModelSessionException 
	 */
	public abstract boolean delete() throws SQLException, ModelSessionException;
}
