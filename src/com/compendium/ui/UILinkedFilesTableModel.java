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

import javax.swing.*;
import javax.swing.table.*;
import javax.swing.event.*;
import javax.swing.border.*;

import java.awt.Point;
import java.io.File;
import java.sql.SQLException;
import java.util.*;

import com.compendium.*;
import com.compendium.ui.*;
import com.compendium.core.*;
import com.compendium.core.datamodel.*;
import com.compendium.core.datamodel.LinkedFile.LFType;
import com.compendium.core.datamodel.services.ILinkedFileService;
import com.compendium.core.datamodel.services.NodeService;

/**
 * This class is the table model for the JTable in list views.
 *
 * @author ? / Michelle Bachler / Lakshmi Prabhakaran
 */
public class UILinkedFilesTableModel extends AbstractTableModel {

	public final static int LOCATION_COLUMN = 0;
	public final static int NAME_COLUMN = 1;
	public final static int USED_COLUMN = 2;
		
	private String[] columnNames = {LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "UILinkedFilesTableModel.location"), //$NON-NLS-1$
									LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "UILinkedFilesTableModel.filename"), //$NON-NLS-1$
									LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "UILinkedFilesTableModel.timesused"),									 //$NON-NLS-1$
									};

	private Vector<LinkedFile> vtData = new Vector<LinkedFile>(51);
	
	/** A mapping of the used references and their use count. */
	private Hashtable<String,Integer> 		sources				 = null;

	/** The service to access the linked files in the database */
	private ILinkedFileService lfs				= null;
	
	/** the current session */
	private PCSession 		session				= null;
	

	public UILinkedFilesTableModel() {
		super();
		
		IModel model = ProjectCompendium.APP.getModel();
		session = model.getSession();
		
		//Only gets node image and reference
		//What about background images and movies?
		lfs = model.getLinkedFileService();			
		
		NodeService ns = (NodeService)model.getNodeService();
		try {
			sources = ns.getAllSources(session);
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}

	/**
	 * Triggered when the data behind the table have changed.
	 */
	public void updateFileTable() {
		
		// read linked files from database
		try {
			vtData = lfs.readAllLinkedFiles(session);		
		} catch (SQLException e) {
			ProjectCompendium.APP.displayError(e.getLocalizedMessage());
		}
		
		// read linked files from file system
		/*File lfDir = new File(UIUtilities.sGetLinkedFilesLocation());
		
		// add linked files from file system to vector of files 
		File[] linkedFiles = lfDir.listFiles();
		if (linkedFiles != null) {
			for(int i = 0; i < linkedFiles.length; i++) {
				File file = linkedFiles[i];
				if (!file.isDirectory()) {
					vtFiles.add(new LinkedFileCopy(file.toURI()));
				}
			}
		}*/
	
		for(String key: sources.keySet()) {
			File file = new File(key);
			if (!file.isDirectory() && CoreUtilities.isFile(key)) { // don't add websites etc.
				LinkedFileCopy copy = new LinkedFileCopy(key);
				vtData.addElement(copy);
			}
		}
		
		fireTableDataChanged();		
	}	
	
	public void removeAllElements() {
		vtData.removeAllElements();
	}
	
	public void setData(Vector vtData) {
		this.vtData = vtData;
	}
		
	public Vector getData() {
		return this.vtData;
	}
	
	public int getColumnCount() {
		return columnNames.length;
	}

	public int getRowCount() {
		return vtData.size();
	}

	public String getColumnName(int col) {
		return columnNames[col];
	}

	public LinkedFile getItemAt(int row) {

		if (vtData == null) {
			return null;			
		}
		if (row >= vtData.size()) {
			return null;			
		}
		
		return (LinkedFile) vtData.elementAt(row);
	}	
	
	public Object getSourceForRow(int rowIndex) {
		LinkedFile lf = (LinkedFile)vtData.elementAt(rowIndex);
		String path = lf.getSourcePath();
		return path;		
	}
	
	public Object getValueAt(int rowIndex, int columnIndex) {
		LinkedFile lf = (LinkedFile)vtData.elementAt(rowIndex);
		switch(columnIndex) {
			case LOCATION_COLUMN: {
				switch(lf.getLFType())
				{
					case COPY: 
					{
						String path = lf.getSourcePath();
						File file = new File(path);
						if (file.exists()) {								
							path = file.getParent();
						} else {
							path = LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "UILinkedFilesTableModel.lost")+": "+file.getParent(); //$NON-NLS-1$ //$NON-NLS-2$
						}
						return path;
					}
					case DATABASE: 
					{
						return LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "UILinkedFilesTableModel.database"); //$NON-NLS-1$
					}
					
				}
			}
			case NAME_COLUMN:	return lf.getName();
			case USED_COLUMN:
				{						
					String path = lf.getSourcePath();
					int count = 0;
					if (sources.containsKey(path)) {
						count = ((Integer)sources.get(path)).intValue();
					}
					return count;
				}
			default: return null;	
		}
	}	

	public void setValueAt(Object o, int row, int col) {}

	public Class getColumnClass(int c) {
		return getValueAt(0, c).getClass();
	}
	
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return false;
	}
}
