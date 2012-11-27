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

import com.compendium.core.CoreUtilities;

/**
 * Models a linked file which resides in the local file system.
 * @author Sebastian Ehrich, modified by Michelle Bachler
 */
public abstract class LinkedFileFileSystem extends LinkedFile {

	/**
	 * Constructor which builds a new LinkedFileFileSystem object
	 * from a path string.
	 * @param path the path of the file
	 */
	public LinkedFileFileSystem(String path) {
		super(path);	
	}
	
	/**
	 * @see com.compendium.core.datamodel.LinkedFile#exportFile(java.io.File)
	 */
	@Override
	public File exportFile(File destDir) throws IOException{
		if(!destDir.isDirectory())
			throw new IOException("Given argument is not a directory.");
		
		File inputFile, outputFile;
		inputFile = new File(filePath);
		outputFile = new File(destDir, fileName);
		
		if(!inputFile.exists())
			throw new IOException("Referenced file doest not exist.");
		if(outputFile.exists())
			throw new IOException("Output file already exists.");
		CoreUtilities.copyFile(inputFile, outputFile);
		return outputFile;
	}
	
	/**
	 * @see com.compendium.core.datamodel.LinkedFile#delete()
	 */
	@Override
	public boolean delete() {
		return (new File(filePath)).delete();
	}

}
