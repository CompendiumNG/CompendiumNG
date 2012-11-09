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

package com.compendium.ui.panels;

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;
import javax.swing.table.*;

import com.compendium.*;
import com.compendium.core.datamodel.*;
import com.compendium.core.datamodel.services.*;
import com.compendium.io.html.*;
import com.compendium.core.*;

import com.compendium.ui.plaf.*;
import com.compendium.ui.*;
import com.compendium.ui.dialogs.*;

/**
 * UIViewPanel holds a table of all the views in the database
 *
 * @author	Michelle Bachler
 */
public class UIViewPanel extends JPanel implements IUIConstants {

	/** The serial id of this class.*/	 	 
	private static final long serialVersionUID = 5611336567405157799L;

	/** The scrollpane for the list of Views.*/
	private JScrollPane				sp 				= null;

	/** The data for the list of Views to draw.*/
	private Vector					oViews  = new Vector(51);

	/** The table for the list of views.*/
	private JTable					table = null;

	/**
	 * Initializes and sets up the dialog.
	 * @param sHeading, the heading for this panel.
	 */
	public UIViewPanel(String sHeading, String userID) {

		setBorder(new EmptyBorder(10,10,10,10));
		setLayout(new BorderLayout());

		// Add label
		JLabel lblViews = new JLabel(sHeading);
		add(lblViews, BorderLayout.NORTH);

		ViewListTableModel model = new ViewListTableModel(userID);
		TableSorter sorter = new TableSorter(model);
		table = new JTable(sorter);
		table.getColumn(LanguageProperties.getString(LanguageProperties.PANELS_BUNDLE, "UIViewPanel.creationDate")).setPreferredWidth(25); //$NON-NLS-1$
		table.getColumn(LanguageProperties.getString(LanguageProperties.PANELS_BUNDLE, "UIViewPanel.modDate")).setPreferredWidth(25); //$NON-NLS-1$
		table.getTableHeader().setReorderingAllowed(false);
		sorter.addMouseListenerToHeaderInTable(table);
		setRenderers();
		
		sp = new JScrollPane(table);
		sp.setPreferredSize(new Dimension(400, 250));
		add(sp, BorderLayout.CENTER);
	}

	/**
	 * Set the header renderers for the table column headers.
	 */
    public void setRenderers() {
    	int count = table.getModel().getColumnCount();
        for (int i = 0; i < count; i++) {
        	TableColumn aColumn = table.getColumnModel().getColumn(i);
        	UITableHeaderRenderer headerRenderer = new UITableHeaderRenderer();
            aColumn.setHeaderRenderer(headerRenderer);
    	}
 	}
    
	/**
	 * Return the JTable associated with this panel.
	 * @return JTable, the JTable assoicated with this panel.
	 */
	public JTable getTable() {
		return table;
	}

	/**
	 * Return an array of the row indexes currently selected in the table.
	 * @return int[], the row indexes currently selected in the table.
	 */
	public int[] getSelectedRowIndexes() {
		return table.getSelectedRows();
	}

	/**
	 * Select All the views on the table.
	 */
	public void onSelectAll() {
		table.selectAll();
	}

	/**
	 * Inner class, which is the datamodel used by the JTable associated with this panel.
	 */
	class ViewListTableModel extends AbstractTableModel {

		private String[] columnNames = {LanguageProperties.getString(LanguageProperties.PANELS_BUNDLE, "UIViewPanel.label"), LanguageProperties.getString(LanguageProperties.PANELS_BUNDLE, "UIViewPanel.creationDate"), LanguageProperties.getString(LanguageProperties.PANELS_BUNDLE, "UIViewPanel.modDate")}; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		private Object[][] data;

		public ViewListTableModel(String userID) {

			Vector vtTemp = new Vector();
			try {
				Enumeration views = ProjectCompendium.APP.getModel().getNodeService().getAllActiveViews(ProjectCompendium.APP.getModel().getSession());
				Hashtable htUserViews = ProjectCompendium.APP.getModel().getUserViews();
				String id = "";				 //$NON-NLS-1$
				for(Enumeration e = views;e.hasMoreElements();) {
					View view = (View)e.nextElement();
					id = view.getId();
					if (!htUserViews.containsKey(id) 
							|| id.equals(ProjectCompendium.APP.getHomeView().getId() )
							|| id.equals(ProjectCompendium.APP.getInBoxID())) {
						vtTemp.addElement(view);							
					}					
				}

				//sort the vector
				vtTemp = CoreUtilities.sortList(vtTemp);
				
				data = new Object [vtTemp.size()][3];
				int i = 0;
				for(Enumeration e = vtTemp.elements();e.hasMoreElements();i++) {
					View view = (View)e.nextElement();
					data[i][0] = view;
					data[i][1] = view.getCreationDate();
					data[i][2] = view.getModificationDate();
					oViews.addElement(view);
				}
			}
			catch(Exception io) {
				ProjectCompendium.APP.displayError(LanguageProperties.getString(LanguageProperties.PANELS_BUNDLE, "UIViewPanel.message1")); //$NON-NLS-1$
			}
		}

		public int getColumnCount() {
			return columnNames.length;
		}

		public int getRowCount() {
			return data.length;
		}

		public String getColumnName(int col) {
			return columnNames[col];
		}

		public Object getValueAt(int row, int col) {
			return data[row][col];
		}

		public Class getColumnClass(int c) {
			return getValueAt(0, c).getClass();
		}
	}
}