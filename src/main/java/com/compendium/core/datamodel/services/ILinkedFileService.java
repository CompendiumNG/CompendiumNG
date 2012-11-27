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

package com.compendium.core.datamodel.services;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Vector;

import com.compendium.core.datamodel.LinkedFile;
import com.compendium.core.datamodel.PCSession;


/**
 * The interface for the LinkedFileService class
 * The LinkedFileService class provides services to store files in the database.
 *
 * @author Sebastian Ehrich
 */
public interface ILinkedFileService extends IService {
	
	/**
	 * @param session the session object for the database to use.
	 * @param fileID the ID of the new linked file
	 * @param file the file to add
	 * @return LinkedFile, the new LinkedFile object created
	 * @throws IOException Occurs when there is an error reading the file 
	 */
	public LinkedFile addFile(PCSession session, String fileID, File file) throws IOException;

	/**
	 * get the LinkedFile from the database and save it in the file system
	 * @param session the current sesssion
	 * @param linkedFile the LinkedFile object of the file to get 
	 * @param file the destination file or directory
	 * @return file the file from the database
	 * @throws IOException Occurs if there is an error writing the file.
	 */
	public File getFile(PCSession session, LinkedFile linkedFile, File file) throws IOException;

	/**
	 * Update database with the contents of the temporary filesystem copy.
	 * 
	 * @param session the current sesssion
	 * @param linkedFile representation of the database object to update 
	 * @param tmpcopy the temporary file to update from
	 * @throws IOException when the update failed
	 */
	public void updateFile(PCSession session, LinkedFile linkedFile, File tmpcopy) throws IOException;
	
	/**
	 * @param session the current sesssion
	 * @param linkedFile the LinkedFile object of the file to delete 
	 * @return boolean true if deletion was successful, false otherwise
	 */
	public boolean deleteFile(PCSession session, LinkedFile linkedFile) throws SQLException;

	/**
	 * Reads all (the ids and file names of the ) linked files in the database
	 * @param session session the current sesssion
	 * @return Array with all linked files
	 */
	public Vector<LinkedFile> readAllLinkedFiles(PCSession session) throws SQLException;	
}
