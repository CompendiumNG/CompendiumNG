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

import java.util.*;
import java.lang.Thread;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.InputEvent;

import javax.swing.JTable;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.event.TableModelEvent;

import com.compendium.core.datamodel.NodePosition;

import com.compendium.ui.edits.PCEdit;
import com.compendium.ui.plaf.*;

/**
 * A sorter for TableModels. The sorter has a model (conforming to TableModel)
 * and itself implements TableModel. TableSorter does not store or copy
 * the data in the TableModel, instead it maintains an array of
 * integers which it keeps the same size as the number of rows in its
 * model. When the model changes it notifies the sorter that something
 * has changed eg. "rowsAdded" so that its internal array of integers
 * can be reallocated. As requests are made of the sorter (like
 * getValueAt(row, col) it redirects them to its model via the mapping
 * array. That way the TableSorter appears to hold another copy of the table
 * with the rows in a different order. The sorting algorthm used is stable
 * which means that it does not move around rows when its comparison
 * function returns 0 to denote that they are equivalent.
 *
 * @version 1.5 12/17/97
 * @author Philip Milne / Michelle Bachler
 */
public class TableSorter extends TableMap {

	/** The sorted index list.*/
	int             indexes[];

	/** The currently selected column.*/
	int 			selectedColumn 		= -1;

	/** Alist of the columns to sort on - currently only ever one at a time.*/
	Vector          sortingColumns 		= new Vector();

	/** True ot sort in ascending order, false to sort in descending order.*/
	boolean         ascending 			= false;

	/** Has the table been sorted.*/
    private boolean sorted 				= false;

	/** Used as a counter during during sorting.*/
	int compares;


	/**
	 * Create an instance of TableSorter.
	 */
	public TableSorter() {
		indexes = new int[0]; // for consistency
	}

	/**
	 * Create an instance of TableSorter with the given TableModel.
	 * @param model, the TableModel to associate with this sorter.
	 */
	public TableSorter(TableModel model) {
		setModel(model);
	}

	/**
	 * Set the TableModel for this sorter.
	 * @param model, the TableModel to associate with this sorter.
	 */
	public void setModel(TableModel model) {
		super.setModel(model);
		reallocateIndexes();
	}

	/**
	 * Compare the values in the two rows for the given column and by the column class type.
	 * @param row1, the first row to compare.
	 * @param row2, the second row to compare.
	 * @param column, the column hose row value to compare and whose class type to use when comparing.
	 */
	public int compareRowsByColumn(int row1, int row2, int column) {
		Class type = model.getColumnClass(column);
		TableModel data = model;

		// Check for nulls.

		Object o1 = data.getValueAt(row1, column);
		Object o2 = data.getValueAt(row2, column);

		// If both values are null, return 0.
		if (o1 == null && o2 == null) {
			return 0;
		} else if (o1 == null) { // Define null less than everything.
			return -1;
		} else if (o2 == null) {
			return 1;
		}

		/*
		 * We copy all returned values from the getValue call in case
		 * an optimised model is reusing one object to return many
		 * values.  The Number subclasses in the JDK are immutable and
		 * so will not be used in this way but other subclasses of
		 * Number might want to do this to save space and avoid
		 * unnecessary heap allocation.
		 */

		if (type.getSuperclass() == java.lang.Number.class) {
			Number n1 = (Number)data.getValueAt(row1, column);
			double d1 = n1.doubleValue();
			Number n2 = (Number)data.getValueAt(row2, column);
			double d2 = n2.doubleValue();

			if (d1 < d2) {
				return -1;
			} else if (d1 > d2) {
				return 1;
			} else {
				return 0;
			}
		}
		else if (type == java.util.Date.class) {
			Date d1 = (Date)data.getValueAt(row1, column);
			long n1 = d1.getTime();
			Date d2 = (Date)data.getValueAt(row2, column);
			long n2 = d2.getTime();

			if (n1 < n2) {
				return -1;
			} else if (n1 > n2) {
				return 1;
			} else {
				return 0;
			}
		}
		else if (type == String.class) {
			// MB: 27th April 2005: MADE NOT CASE SENSITIVE
			String s1 = (String)data.getValueAt(row1, column);
			s1 = s1.toLowerCase();
			String s2 = (String)data.getValueAt(row2, column);
			s2 = s2.toLowerCase();
			int result = s1.compareTo(s2);

			if (result < 0) {
				return -1;
			} else if (result > 0) {
				return 1;
			} else {
				return 0;
			}
		}
		else if (type == Boolean.class) {
			Boolean bool1 = (Boolean)data.getValueAt(row1, column);
			boolean b1 = bool1.booleanValue();
			Boolean bool2 = (Boolean)data.getValueAt(row2, column);
			boolean b2 = bool2.booleanValue();

			if (b1 == b2) {
				return 0;
			} else if (b1) { // Define false < true
				return 1;
			} else {
				return -1;
			}
		}
		else {
			Object v1 = data.getValueAt(row1, column);
			String s1 = v1.toString();
			Object v2 = data.getValueAt(row2, column);
			String s2 = v2.toString();
			int result = s1.compareTo(s2);

			if (result < 0) {
				return -1;
			} else if (result > 0) {
				return 1;
			} else {
			return 0;
			}
		}
	}

	/**
	 * Compare the values in the two rows.
	 * @param row1, the first row to compare.
	 * @param row2, the second row to compare.
	 */
	public int compare(int row1, int row2) {
		compares++;
		for (int level = 0; level < sortingColumns.size(); level++) {
			Integer column = (Integer)sortingColumns.elementAt(level);
			int result = compareRowsByColumn(row1, row2, column.intValue());
			if (result != 0) {
				return ascending ? result : -result;
			}
		}
		return 0;
	}

	/**
	 * Unsort the table, and clear the index.
	 */
	public void reallocateIndexes() {
		int rowCount = model.getRowCount();
        this.sorted = false;

		// Set up a new array of indexes with the right number of elements
		// for the new data model.
		indexes = new int[rowCount];

		// Initialise with the identity mapping.
		for (int row = 0; row < rowCount; row++) {
			indexes[row] = row;
		}
	}

	/**
	 * Handle a tableChanged event.
	 * @param e, the associated TableModelEvent object.
	 */
	public void tableChanged(TableModelEvent e) {
		reallocateIndexes();
		super.tableChanged(e);
	}

	/**
	 * Check the table row length matches the sorted indexes size.
	 * If they do not match, write out a system error message.
	 */
	public void checkModel() {
		if (indexes.length != model.getRowCount()) {
			System.err.println("Sorter not informed of a change in model."); //$NON-NLS-1$
		}
	}

	/**
	 * Sort the table.
	 * @param sender, the class using this method.
	 */
	public void sort(Object sender) {
		checkModel();
		compares = 0;
		shuttlesort((int[])indexes.clone(), indexes, 0, indexes.length);
	}

	/**
	 * Sort the table.
	 */
	public void n2sort() {
		for (int i = 0; i < getRowCount(); i++) {
			for (int j = i+1; j < getRowCount(); j++) {
				if (compare(indexes[i], indexes[j]) == -1) {
					swap(i, j);
				}
			}
		}
	}

	// This is a home-grown implementation which we have not had time
	// to research - it may perform poorly in some circumstances. It
	// requires twice the space of an in-place algorithm and makes
	// NlogN assigments shuttling the values between the two
	// arrays. The number of compares appears to vary between N-1 and
	// NlogN depending on the initial order but the main reason for
	// using it here is that, unlike qsort, it is stable.
	/**
	 * Sorts. For more detail read the source code.
	 */
	public void shuttlesort(int from[], int to[], int low, int high) {
		if (high - low < 2) {
			return;
		}
		int middle = (low + high)/2;
		shuttlesort(to, from, low, middle);
		shuttlesort(to, from, middle, high);

		int p = low;
		int q = middle;

		/* This is an optional short-cut; at each recursive call,
		check to see if the elements in this subset are already
		ordered.  If so, no further comparisons are needed; the
		sub-array can just be copied.  The array must be copied rather
		than assigned otherwise sister calls in the recursion might
		get out of sinc.  When the number of elements is three they
		are partitioned so that the first set, [low, mid), has one
		element and and the second, [mid, high), has two. We skip the
		optimisation when the number of elements is three or less as
		the first compare in the normal merge will produce the same
		sequence of steps. This optimisation seems to be worthwhile
		for partially ordered lists but some analysis is needed to
		find out how the performance drops to Nlog(N) as the initial
		order diminishes - it may drop very quickly.  */

		if (high - low >= 4 && compare(from[middle-1], from[middle]) <= 0) {
			for (int i = low; i < high; i++) {
				to[i] = from[i];
			}
			return;
		}

		// A normal merge.

		for (int i = low; i < high; i++) {
			if (q >= high || (p < middle && compare(from[p], from[q]) <= 0)) {
				to[i] = from[p++];
			}
			else {
				to[i] = from[q++];
			}
		}
	}

	/**
	 * Swap the elements at the given indexes.
	 * @param i, the first index to swap.
	 * @param j, the second index to swap.
	 */
	public void swap(int i, int j) {
		int tmp = indexes[i];
		indexes[i] = indexes[j];
		indexes[j] = tmp;
	}

	/**
	 * Get the NodePosition at the given row.
	 * @param aRow, the row for the cell address.
	 * @return Object, the NodePosition object for the given Row.
	 */
	public NodePosition getNodePosition(int aRow) {
		checkModel();
		return ((ListTableModel)model).getNodePosition(indexes[aRow]);
	}

	/**
	 * Get the real model row for the given row.
	 * @param aRow the row to get the real row for
	 * @return int the real row for the given row.
	 */
	public int getRealRow(int aRow) {		
		checkModel();
		int ind = -1;
		try {
			ind = indexes[aRow];
		} catch (ArrayIndexOutOfBoundsException e) {
			//e.printStackTrace();
		} 

		return ind;
	}	
	
	/**
	 * Get the value at the given row and column cell.
	 * @param aRow, the row for the cell address.
	 * @param aColumn, the column for the cell address.
	 * @return Object, tha value at the given cell address.
	 */
	public Object getValueAt(int aRow, int aColumn) {
		checkModel();
		return model.getValueAt(indexes[aRow], aColumn);
	}

	/**
	 * Set the value at the given row and column cell.
	 * @param aValue, the vluae to set for the given cell address.
	 * @param aRow, the row for the cell address.
	 * @param aColumn, the column for the cell address.
	 */
	public void setValueAt(Object aValue, int aRow, int aColumn) {
		checkModel();
		model.setValueAt(aValue, indexes[aRow], aColumn);
	}

	/**
	 * Sort the table by the given column, in the given order.
	 * @param column, the column to sort by.
	 * @param ascending, true to sort in ascending order, false to sort in descending order.
	 */
	public void sortByColumn(int column, boolean ascending) {
		this.ascending = ascending;
        this.sorted = true;
        selectedColumn = column;
		sortingColumns.removeAllElements();
		sortingColumns.addElement(new Integer(column));
		sort(this);
		super.tableChanged(new TableModelEvent(this));
	}

	/**
	 * Return the currently selected column.
	 * @return int, the currently selected column.
	 */
	public int getSelectedColumn() {
		return selectedColumn;
	}

	/**
	 * Set the given column as selected.
	 * @param column, the column to select.
	 */
	public void setSelectedColumn(int column) {
		selectedColumn = column;
		sortByColumn(column, true);
	}
	
	public boolean getAscending() {
		return ascending;
	}

	/**
	 * Create and add a mouse listener to the Table header of the given table
	 * to trigger a table sort when a column heading is clicked in the JTable.
	 * @param table, the table whose table header to add the MouseListener to.
	 */
	public void addMouseListenerToHeaderInTable(JTable table) {

		final TableSorter sorter = this;
		final JTable tableView = table;
		tableView.setColumnSelectionAllowed(false);

		MouseAdapter listMouseListener = new MouseAdapter() {
			public void mouseClicked(MouseEvent event) {

				final MouseEvent e = event;

				TableColumnModel columnModel = tableView.getColumnModel();
				int viewColumn = columnModel.getColumnIndexAtX(e.getX());
				int column = tableView.convertColumnIndexToModel(viewColumn);
				if (e.getClickCount() == 1 && column != -1) {
					selectedColumn = column;
					//int shiftPressed = e.getModifiers()&InputEvent.SHIFT_MASK;
					//boolean ascending = (shiftPressed == 0);
					sorter.sortByColumn(column, !ascending);

					tableView.getTableHeader().validate();
					tableView.getTableHeader().repaint();
				}
			}
		};


		JTableHeader th = tableView.getTableHeader();
		th.addMouseListener(listMouseListener);
	}
}
