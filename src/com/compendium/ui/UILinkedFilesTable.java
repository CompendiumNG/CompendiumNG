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

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import javax.swing.border.*;

import com.compendium.core.datamodel.*;
import com.compendium.core.datamodel.services.NodeService;

import com.compendium.LanguageProperties;
import com.compendium.ProjectCompendium;
import com.compendium.ui.dialogs.UILinkedFileUsageDialog;

/**
 * This class controls the tags working table area.
 *
 * @author	Michelle Bachler
 */
public class UILinkedFilesTable implements TableModelListener { 
											 

	/** Table that holds the list data for this list view.*/
	private JTable 			table;

	/** The model for the data in the list view table.*/
	private UILinkedFilesTableModel model;

	/** The table sort for the table for this list view.*/
	private TableSorter  	sorter;
		
	private JDialog 		oParent = null;
	
	/**
	 * Constructor. Initializes and table and options for this list.
	 */
	public UILinkedFilesTable(JDialog parent) {
		oParent = parent;
		model = new UILinkedFilesTableModel();
		
		sorter = new TableSorter(model);
		sorter.addTableModelListener(this);
		
		table = new JTable(sorter);				
		table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		table.setBackground(Color.white);		
		table.setCellSelectionEnabled(false);
		table.setRowSelectionAllowed(true);
		
		Action openUsageDialog = new AbstractAction() {
		    public void actionPerformed(ActionEvent e) {
				int row = table.getSelectedRow();
				if (row != -1) {
					openUsageDialog(row);
				}
		    }
		};
		table.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER,0),"openUsageDialog"); //$NON-NLS-1$
		table.getActionMap().put("openUsageDialog",openUsageDialog); //$NON-NLS-1$
		
		//CSH.setHelpIDString(table,"node.views");		
		table.getColumn(LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "UILinkedFilesTableModel.timesused")).setPreferredWidth(100); //$NON-NLS-1$
		//table.getColumn("Tags").setPreferredWidth(10);
		//table.getColumn("Views").setPreferredWidth(10);		

		table.getColumn(LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "UILinkedFilesTableModel.timesused")).setMaxWidth(200); //$NON-NLS-1$
		//table.getColumn("Tags").setMaxWidth(60);
		//table.getColumn("Views").setMaxWidth(60);		
		
		table.getTableHeader().setReorderingAllowed(false);
		
		sorter.addMouseListenerToHeaderInTable(table);
		table.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				boolean isLeftMouse = SwingUtilities.isLeftMouseButton(e);
				if (ProjectCompendium.isMac &&
						(e.getButton() == 3 && e.isShiftDown())) {
					isLeftMouse = false;
				}				
				if (isLeftMouse && e.getClickCount() == 2) {
					int row = table.rowAtPoint( e.getPoint() );	
					openUsageDialog(row);
				}
			}
		});
		
		/*table.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				int keyCode = e.getKeyCode();
				if (keyCode == KeyEvent.VK_ENTER) {
					int row = table.getSelectedRow();
					if (row != -1) {
						openUsageDialog(row);
					}
				}
			}
		});*/

		table.setFont(ProjectCompendium.APP.currentDefaultFont);
		FontMetrics metrics = table.getFontMetrics(ProjectCompendium.APP.currentDefaultFont);
		table.setRowHeight(table.getRowMargin()+metrics.getHeight());				
		
		setRenderers();

		((TableSorter)table.getModel()).setSelectedColumn(1);
	}
	
	/**
	 * Update the table after a change.
	 */
	public void updateTable() {
		sorter.fireTableChanged(new TableModelEvent(table.getModel()));
	}
	
	/**
	 * Display the new data.
	 * @param vtData
	 */
	public void refreshTable(Vector vtData) {		
		int sort = sorter.getSelectedColumn();
		model.removeAllElements();
		model.setData(vtData);
		sorter.setModel(model);
		if (sort > -1) {
			sorter.sortByColumn(sort, sorter.getAscending());
		}
		updateTable();
		table.revalidate();
		table.repaint();		
	}
	
	/**
	 * Process a TableModelEvent, when something has changed in the data.
	 * Refresh the table and repaint.
	 * @param tme, the associated TableModelEvent.
	 */
	public void tableChanged(TableModelEvent tme) {
		table.invalidate();
		table.repaint();
	}
	
	/**
	 * Set the header renderers for the table column headers and the table cells.
	 */
    public void setRenderers() {
    	int count = table.getModel().getColumnCount();
    	
        for (int i = 0; i < count; i++) {
        	TableColumn aColumn = table.getColumnModel().getColumn(i);
        	
        	// Set the cell renderer for the column headers
        	UITableHeaderRenderer headerRenderer = new UITableHeaderRenderer();
            aColumn.setHeaderRenderer(headerRenderer);
            
            // Set the cell renderer for column cells
            CellRenderer cellRenderer = new CellRenderer();
            aColumn.setCellRenderer(cellRenderer);
    	}
 	}

 
	/**
	 * The helper class renderers the table cells.
	 */
    public class CellRenderer extends DefaultTableCellRenderer {

    	CellRenderer() {
        	super();
        	setHorizontalAlignment(SwingConstants.LEFT);
 		}
    	
    	public Component getTableCellRendererComponent(JTable table, Object value, 
    			boolean isSelected, boolean hasFocus, int row, int column) {
        		
			setFont(table.getFont());
			setBorder( isSelected ?UIManager.getBorder("List.focusCellHighlightBorder") : new EmptyBorder(1,1,1,1)); //$NON-NLS-1$
			if (isSelected) {
				setBackground(table.getSelectionBackground());
				setForeground(table.getSelectionForeground());
			}
			else {
				setBackground(table.getBackground());
				setForeground(table.getForeground());				
			}
    		
			setToolTipText(LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "UILinkedFilesTable.tip"));			 //$NON-NLS-1$
			setValue(value);			     	
        	return this;
		}

        protected void setValue(Object value) {
        	setText((value == null) ? "" : value.toString());        	 //$NON-NLS-1$
        }
	}    	
   
	/**
	 * Return the JTable that holds the view list.
	 * @return JTable, the JTable that holds the view list.
	 */
	public JTable getTable() {
		return table;
	}

	/**
	 * Deselect all selected rows.
	 */
	public void deselectAll() {
		table.clearSelection();
	}
	
	/**
	 * Triggered when the data behind the table have changed.
	 */
	public void updateFileTable() {		
		model.updateFileTable();
		table.clearSelection();
	}				
	
	/**
	 * Triggered when the delete button is pressed, if resource not in use.
	 */
	public void onDelete() {
		int[] selectedFiles = table.getSelectedRows();

		// delete selected files
		String sMessage = ""; //$NON-NLS-1$
		for(int i = 0; i < selectedFiles.length; i++)
		{	
			try {
				Vector nodes = getUsage(selectedFiles[i]);
				LinkedFile lf = (LinkedFile)model.getItemAt(sorter.getRealRow(selectedFiles[i]));
				if (nodes.size() == 0) {					
					// delete file;
					try {
						lf.delete();				
					} catch (Exception e) {
						sMessage += lf.getName()+" "+LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "UILinkedFilesTable.errorDelete")+"\n"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					}
				} else {
					sMessage += lf.getName()+" "+LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "UILinkedFilesTable.noDelete")+"\n"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				}
			} catch (Exception ex) {
				sMessage += LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "UILinkedFilesTable.errorDelete2")+"\n"; //$NON-NLS-1$ //$NON-NLS-2$
				ex.printStackTrace();
			}		

		}	
		
		if (!sMessage.equals("")) { //$NON-NLS-1$
			ProjectCompendium.APP.displayError(LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "UILinkedFilesTable.errorDeleting")+":"+"\n\n"+sMessage); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}
		updateFileTable();
	}	
	
	/**
	 * Triggered when the extract button is pressed.
	 *
	 */
	public void onExtract(File chosenDir) {
		int[] selectedFiles = table.getSelectedRows();		    	
    	for(int i = 0; i < selectedFiles.length; i++) {
			LinkedFile lf = (LinkedFile)model.getItemAt(sorter.getRealRow(selectedFiles[i]));
			IModel oModel = ProjectCompendium.APP.getModel();
			lf.initialize(oModel.getSession(), oModel);
			
			// export the file to the chosen directory
			try {
				lf.exportFile(chosenDir);
			} catch (Exception e) {
				e.printStackTrace();
			}
    	}
    }	

	/**
	 * Return the list of node for the given row of resource.
	 * @param row
	 * @return
	 */
	private Vector getUsage(int row) throws Exception {
		int ind = sorter.getRealRow(row);	
		String source = (String)model.getSourceForRow(ind);
		IModel model = ProjectCompendium.APP.getModel();
		PCSession session = model.getSession();
		NodeService ns = (NodeService)model.getNodeService();
		Vector nodes = ns.getNodesForSource(session, source, model.getUserProfile().getId());
		return nodes;
	}
	
	private void openUsageDialog(int row) {
		int ind = sorter.getRealRow(row);	
		String source = (String)model.getSourceForRow(ind);
		try {
			Vector nodes = getUsage(row);
			UILinkedFileUsageDialog dialog = new UILinkedFileUsageDialog(oParent, source, nodes);
			dialog.setVisible(true);
		} catch (Exception ex) {
			ex.printStackTrace();
		}		
	}		
}