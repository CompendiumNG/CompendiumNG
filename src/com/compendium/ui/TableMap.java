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

import javax.swing.table.*; 
import javax.swing.event.TableModelListener; 
import javax.swing.event.TableModelEvent; 

/** 
 * In a chain of data manipulators some behaviour is common. TableMap
 * provides most of this behavour and can be subclassed by filters
 * that only need to override a handful of specific methods. TableMap 
 * implements TableModel by routing all requests to its model, and
 * TableModelListener by routing all events to its listeners. Inserting 
 * a TableMap which has not been subclassed into a chain of table filters 
 * should have no effect.
 *
 * @version 1.4 12/17/97
 * @author Philip Milne / Michelle Bachler - JavaDocs only.
 */
public class TableMap extends AbstractTableModel implements TableModelListener {

	/** The <code>TableModel</code> associated with this <code>TableMap</code> instance.*/
	protected TableModel model; 

	/**
	 * Return the <code>TableModel</code> associated with this <code>TableMap</code> instance.
	 * @return TableModel, the <code>TableModel</code> associated with this <code>TableMap</code> instance.
	 */
	public TableModel getModel() {
		return model;
	}

	/**
	 * Set the <code>TableModel</code> associated with this <code>TableMap</code> instance.
	 * @param model, the <code>TableModel</code> associated with this <code>TableMap</code> instance.
	 */
	public void setModel(TableModel model) {
		this.model = model; 
		model.addTableModelListener(this); 
	}

	/**
	 * Return the value for the cell at the given row and column.
	 * @param row, the row of the cell to return the value for.
	 * @param column, the column of the row to return the value for.
	 * @return Object, the value of the cell at the given row and column.
	 */
	public Object getValueAt(int aRow, int aColumn) {
		return model.getValueAt(aRow, aColumn); 
	}
		
	/**
	 * Set the value of the cell ath the given row and column.
	 * @param Object, the value to set for the cell.
	 * @param row, the row of the cell to set the value for.
	 * @param column, the column of the row to set the value for.
	 */
	public void setValueAt(Object aValue, int aRow, int aColumn) {
		model.setValueAt(aValue, aRow, aColumn); 
	}

	/**
	 * Return the total number of rows in the table.
	 * @return int, the total number of rows in the column.
	 */
	public int getRowCount() {
		return (model == null) ? 0 : model.getRowCount(); 
	}

	/**
	 * Return a count of all columns in the table.
	 * @return int, the total number of columns in the table.
	 */
	public int getColumnCount() {
		return (model == null) ? 0 : model.getColumnCount(); 
	}
		
	/**
	 * Return the name of the given column?
	 * @param column, the column to return the name for.
	 * @return String, the name of the given column.
	 */
	public String getColumnName(int aColumn) {
		return model.getColumnName(aColumn); 
	}

	/**
	 * Return the class of item in the given column?
	 * @param column, the column to return the class of items for.
	 * @return Class, the class of items in the given column.
	 */
	public Class getColumnClass(int aColumn) {
		return model.getColumnClass(aColumn); 
	}

	/**
	 * Is the cell with the given row and column numbers editable?
	 * @param row, the row of the cell to check.
	 * @param column, the column of the row to check.
	 * @return boolean, true if the cell with the given row and column numbers editable, else false.
	 */
	public boolean isCellEditable(int row, int column) { 
		 return model.isCellEditable(row, column); 
	}

	/**
	 * Implementation of the TableModelListener interface, 
	 * By default forward all events to all the listeners. 
	 *
	 * @param e, the associated TableModelEvent object.
	 */
	public void tableChanged(TableModelEvent e) {
		fireTableChanged(e);
	}
}
