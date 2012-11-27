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

package com.compendium.ui.tags;

import javax.swing.*;
import javax.swing.table.*;
import javax.swing.event.*;
import javax.swing.border.*;

import java.awt.Point;
import java.util.*;

import com.compendium.*;
import com.compendium.ui.*;
import com.compendium.core.*;
import com.compendium.core.datamodel.*;

/**
 * This class is the table model for the JTable in list views.
 *
 * @author ? / Michelle Bachler / Lakshmi Prabhakaran
 */
public class UITagsListTableModel extends AbstractTableModel {

	public final static int IMAGE_COLUMN = 0;
	public final static int TAGS_COLUMN = 1;
	public final static int VIEWS_COLUMN = 2;
	public final static int LABEL_COLUMN = 3;
	public final static int MODIFICATION_DATE_COLUMN = 4;
	
	private String[] columnNames = {LanguageProperties.getString(LanguageProperties.TAGS_BUNDLE, "UITagsListTableModel.img"), //$NON-NLS-1$
									LanguageProperties.getString(LanguageProperties.TAGS_BUNDLE, "UITagsListTableModel.tags"), //$NON-NLS-1$
									LanguageProperties.getString(LanguageProperties.TAGS_BUNDLE, "UITagsListTableModel.views"), //$NON-NLS-1$
									LanguageProperties.getString(LanguageProperties.TAGS_BUNDLE, "UITagsListTableModel.label"),								 //$NON-NLS-1$
									LanguageProperties.getString(LanguageProperties.TAGS_BUNDLE, "UITagsListTableModel.modedate"), //$NON-NLS-1$
									};

	private Vector nodeData = new Vector(20);
	
	public UITagsListTableModel() {
		super();
	}

	public void removeAllElements() {
		nodeData.removeAllElements();
	}
	
	public void setData(Vector vtData) {
		this.nodeData = vtData;
	}
		
	public Vector getData() {
		return this.nodeData;
	}
	
	public int getColumnCount() {
		return columnNames.length;
	}

	public int getRowCount() {
		return nodeData.size();
	}

	public String getColumnName(int col) {
		return columnNames[col];
	}

	public NodeSummary getNodeAt(int row) {

		if (nodeData == null) {
			return null;			
		}
		if (row >= nodeData.size()) {
			return null;			
		}
		
		return (NodeSummary) nodeData.elementAt(row);
	}	
	
	public Object getValueAt(int row, int col) {

		if (nodeData == null) {
			return null;			
		}
		if (row >= nodeData.size()) {
			return null;			
		}
		
		NodeSummary node = (NodeSummary) nodeData.elementAt(row);
		if (node != null) {
			switch (col) {
				case UITagsListTableModel.IMAGE_COLUMN: {
					if (node.getType() == ICoreConstants.REFERENCE) {
						return UINode.getReferenceImageSmall(node.getSource());
					} else {
						return UINode.getNodeImageSmall(node.getType());
					}
				}
				case UITagsListTableModel.TAGS_COLUMN: {
					if (node.getCodeCount() > 0) {
						return new Integer(node.getCodeCount());
					} else {
						return ""; //$NON-NLS-1$
					}
				}	
				case UITagsListTableModel.VIEWS_COLUMN: {
					int count = node.getViewCount();
					if (count == 0) {
						node.updateMultipleViews();
						count = node.getViewCount();
					}
					return new Integer(count);
				}								
				case UITagsListTableModel.LABEL_COLUMN: {
					return node.getLabel();
				}
				case UITagsListTableModel.MODIFICATION_DATE_COLUMN: {
					return node.getModificationDate();
				}
				default:
					return null;
			}
		}

		return null;
	}

	public void setValueAt(Object o, int row, int col) {
				
	}

	public Class getColumnClass(int c) {
		return getValueAt(0, c).getClass();
	}

	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return false;
	}
}
