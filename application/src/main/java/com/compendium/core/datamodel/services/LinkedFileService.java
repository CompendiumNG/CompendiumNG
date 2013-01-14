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
import java.io.Serializable;
import java.sql.SQLException;
import java.util.Vector;

import com.compendium.core.datamodel.LinkedFile;
import com.compendium.core.datamodel.PCSession;
import com.compendium.core.db.DBLinkedFile;
import com.compendium.core.db.management.DBConnection;
import com.compendium.core.db.management.DBDatabaseManager;

/**
 * A service which provides access to the data from the linked files
 * table of the database
 * 
 * @author Sebastian Ehrich
 */
public class LinkedFileService extends ClientService implements
		ILinkedFileService, Serializable {

	private static final long serialVersionUID = 3940423033969857336L;

	/**
	 *	Constructor.
	 */
	public  LinkedFileService() {
		super();
	}

	/**
	 * Constructor, set the name of the service.
	 *
	 * @param sName the name of this service.
	 */
	public LinkedFileService(String sName) {
		super(sName);
	}

	/**
	 * Constructor.
	 *
	 * @param name the unique name of this service
 	 * @param sm the current ServiceManager
	 * @param dbMgr the current DBDatabaseManager
	 */
	public  LinkedFileService(String name, ServiceManager sm, DBDatabaseManager dbMgr) {
		super(name, sm, dbMgr) ;
	}	
	
	/**
	 * add the given file to the database
	 * @param session the current sesssion
	 * @param fileID the fileID to add
	 * @param file the file to add
	 * @return LinkedFile the object representing the LinkedFile in the database
	 * @throws IOException Occurs if there is an error reading the file. 
	 */
	public LinkedFile addFile(PCSession session, String fileID, File file) throws IOException {
		
		DBConnection dbcon = getDatabaseManager().requestConnection(session.getModelName()) ;
		LinkedFile linkedfile = DBLinkedFile.insert(dbcon, fileID, file);		
		getDatabaseManager().releaseConnection(session.getModelName(),dbcon);
		return linkedfile;
	}

	/**
	 * get the LinkedFile from the database and save it in the file system
	 * @param session the current sesssion
	 * @param linkedFile the LinkedFile object of the file to get 
	 * @param file the filename of the destination
	 * @return File file the file from the database
	 * @throws IOException Occurs if there is an error writing the file.
	 */
	public File getFile(PCSession session, LinkedFile linkedFile, File file) throws IOException {
		
		DBConnection dbcon = getDatabaseManager().requestConnection(session.getModelName()) ;
		File thefile = DBLinkedFile.get(dbcon, linkedFile, file);
		getDatabaseManager().releaseConnection(session.getModelName(),dbcon);
		return thefile;
	}

	/**
	 * @see com.compendium.core.datamodel.services.ILinkedFileService#deleteFile(com.compendium.core.datamodel.PCSession, com.compendium.core.datamodel.LinkedFile)
	 */
	public boolean deleteFile(PCSession session, LinkedFile linkedFile) throws SQLException {
		
		DBConnection dbcon = getDatabaseManager().requestConnection(session.getModelName()) ;
		boolean hasDeleted = DBLinkedFile.del(dbcon, linkedFile);
		getDatabaseManager().releaseConnection(session.getModelName(),dbcon);
		return hasDeleted;
	}

	/** 
	 * {@inheritDoc} 
	 */
	public void updateFile(PCSession session, LinkedFile linkedFile, File tmpcopy) throws IOException {
		
		DBConnection dbcon = getDatabaseManager().requestConnection(session.getModelName());
		DBLinkedFile.update(dbcon, linkedFile, tmpcopy);
		getDatabaseManager().releaseConnection(session.getModelName(),dbcon);
	}
	
	/**
	 * {@inheritDoc}
	 */
	public Vector<LinkedFile> readAllLinkedFiles(PCSession session) throws SQLException {
		
		DBConnection dbcon = getDatabaseManager().requestConnection(session.getModelName()) ;
		Vector links = DBLinkedFile.readAllLinkedFiles(dbcon);
		getDatabaseManager().releaseConnection(session.getModelName(),dbcon);
		return links;
	}	
}
