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

package com.compendium.ui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JOptionPane;

import com.compendium.ProjectCompendium;
import com.compendium.core.datamodel.IModel;
import com.compendium.core.datamodel.LinkedFile;
import com.compendium.core.datamodel.PCSession;
import com.compendium.core.datamodel.services.ILinkedFileService;

/**
 * This listener is registered with the dialog to update files in the database. 
 * It is to update the database from the temporary file system copy and to delete
 * the copy when the user has finished editing.  
 * 
 * @author rudolf
 *
 */
public class DBUpdateListener implements PropertyChangeListener {

	/** Compendium object representing a file in the database */
	private LinkedFile oLinkedFile;
	
	/** the temporary file system copy of <code>oLinkedFile</code> */
	private File oTmpCopy;
	
	/** the dialog we are listening on */
	private JOptionPane oDialog;
	
	/**
	 * Constructor. 
	 * @param linkedFile representation of the database object to update 
	 * @param tmpCopy the temporary file to update from
	 * @param dialog the dialog containing the current and possible options 
	 */
	public DBUpdateListener( LinkedFile linkedFile, File tmpCopy, JOptionPane dialog ) {
		oLinkedFile = linkedFile;
		oTmpCopy = tmpCopy;
		oDialog = dialog;
	}
	
	/**
	 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent evt) {
        if (JOptionPane.VALUE_PROPERTY.equals(evt.getPropertyName())) {
            // System.out.println("user selected " + oDialog.getValue());
        	if (oDialog.getValue() == oDialog.getOptions()[0]) {
        		if ( updateLinkedFile(oLinkedFile, oTmpCopy) ) {
                	oTmpCopy.delete();
        		}        			
        	}
        	else {
        		// user chose not to update, but to discard tmpCopy anyway
            	oTmpCopy.delete();
        	}
        }
	}

	/**
	 * Update database with the contents of the temporary filesystem copy.
	 * 
	 * @param linkedFile representation of the database object to update 
	 * @param tmpCopy the temporary file to update from
	 * @return true iff the update failed
	 */
	private static boolean updateLinkedFile(LinkedFile linkedFile, File tmpCopy ) {
		IModel model = ProjectCompendium.APP.getModel();
		PCSession session = model.getSession();
		ILinkedFileService lfs = model.getLinkedFileService();

		try {
			lfs.updateFile(session, linkedFile, tmpCopy);
		} catch (IOException e) {
			ProjectCompendium.APP
			.displayError("Exception: (ExecuteControl.updateLinkedFile): Update of LinkedFile failed:\n\n" //$NON-NLS-1$
					+ e.getMessage());
			e.printStackTrace();
			return false;
		}
		return true;
	}

}
