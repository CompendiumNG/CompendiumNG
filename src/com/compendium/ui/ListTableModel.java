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
import java.util.*;
import java.awt.Point;
import javax.swing.event.*;

import com.compendium.*;
import com.compendium.core.ICoreConstants;
import com.compendium.core.datamodel.*;

/**
 * This class is the table model for the JTable in list views.
 *
 * @author ? / Michelle Bachler / Lakshmi Prabhakaran
 */
public class ListTableModel extends AbstractTableModel {
	
	/**Serial ID*/
	private static final long serialVersionUID = 6863795268955672400L;
	
	public final static int NUMBER_COLUMN = 0;
	public final static int IMAGE_COLUMN = 1;
	public final static int TAGS_COLUMN = 2;
	public final static int VIEWS_COLUMN = 3;
	public final static int DETAIL_COLUMN = 4;
	public final static int WEIGHT_COLUMN = 5;
	public final static int LABEL_COLUMN = 6;
	public final static int CREATION_DATE_COLUMN = 7;
	public final static int MODIFICATION_DATE_COLUMN = 8;
	public final static int ID_COLUMN = 9;
	public final static int AUTHOR_COLUMN = 10;
	
	
	protected String[] columnNames = {LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "ListTableModel.no"),  //$NON-NLS-1$
									LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "ListTableModel.img"), //$NON-NLS-1$
									LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "ListTableModel.tags"), //$NON-NLS-1$
									LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "ListTableModel.views"), //$NON-NLS-1$
									LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "ListTableModel.details"),//$NON-NLS-1$
									LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "ListTableModel.weight"), //$NON-NLS-1$
									LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "ListTableModel.label"), //$NON-NLS-1$
									LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "ListTableModel.createDate"), //$NON-NLS-1$
									LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "ListTableModel.modDate"), //$NON-NLS-1$
									LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "ListTableModel.id"), //$NON-NLS-1$
									LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "ListTableModel.author")}; //$NON-NLS-1$

	protected Vector nodeData = new Vector(20);
	protected View view;
	protected TableSorter sorter;	

	public ListTableModel(View listView) {
		super();
		view = listView;
		sortNodePos();
		constructTableData();
	}

	public void setSorter(TableSorter ts) {
		sorter = ts;
	}

	/**
	 * Reorders the views nodes and sets their yPositions.
	 *
	 */
	public void sortNodePos() {
		Vector vtTemp = new Vector();
		for(Enumeration e = view.getPositions();e.hasMoreElements();) {
			vtTemp.addElement((NodePosition)e.nextElement());
		}

		for (int i = 0; i < vtTemp.size(); i++) {
			int yPos1 = ((NodePosition)vtTemp.elementAt(i)).getYPos();
			for (int j = i+1; j < vtTemp.size(); j++) {
				int yPos2 = ((NodePosition)vtTemp.elementAt(j)).getYPos();
				if (yPos1 > yPos2) {
					Object o = vtTemp.elementAt(i);
					vtTemp.setElementAt(vtTemp.elementAt(j), i);
					vtTemp.setElementAt(o, j);
					yPos1 = ((NodePosition)vtTemp.elementAt(i)).getYPos();
				}
			}
		}

		for (int i = 0; i < vtTemp.size(); i++) {
			((NodePosition)vtTemp.elementAt(i)).setYPos((i+1)*10);
		}
	}

	private void updateNodePos() {

		for (int i = 0; i < nodeData.size(); i++) {

			int oldIndex = ((Integer)sorter.getValueAt(i,0)).intValue();
			sorter.setValueAt(new Integer(i),i,0);

			if (oldIndex <= nodeData.size()) {
				NodePosition np = ((NodePosition)nodeData.elementAt(oldIndex));
				if (np != null)  {
					try {
						view.setNodePosition(np.getNode().getId(), new Point(np.getXPos(), (i + 1) * 10));
						np.setYPos((i+1)*10);
					}
					catch(Exception ex) {
						ex.printStackTrace();
						System.out.println("Error: Unable to update position "+ex.getMessage()); //$NON-NLS-1$
					}
				}
			}
		}
	}

	public void constructTableData() {

		Vector vtTemp = new Vector();
		nodeData.removeAllElements();

		for(Enumeration e = view.getPositions();e.hasMoreElements();) {
			NodePosition nodePos = (NodePosition)e.nextElement();
			vtTemp.addElement(nodePos);
			nodeData.addElement(nodePos);
		}

		for(Enumeration e = vtTemp.elements();e.hasMoreElements();) {
			NodePosition nodePos = (NodePosition)e.nextElement();
			NodeSummary node = nodePos.getNode();
			int index = nodePos.getYPos()/10;
			index--;
			if (index < nodeData.size()) {
				nodeData.removeElementAt(index);
				nodeData.insertElementAt(nodePos, index);
			}
		}
	}

	public void refreshTable() {
		updateNodePos();
		constructTableData();
		sorter.reallocateIndexes();
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

	public Object getValueAt(int row, int col) {

		if (nodeData == null) {
			return null;			
		}
		if (row >= nodeData.size()) {
			return null;			
		}
		
		NodePosition np = (NodePosition) nodeData.elementAt(row);
		if (np != null) {
			NodeSummary node = np.getNode();
			if (node != null) {
				switch (col) {
					case ListTableModel.NUMBER_COLUMN: {
						return new Integer(row);
					}				
					case ListTableModel.IMAGE_COLUMN: {
						if (node.getType() == ICoreConstants.REFERENCE) {
							return UINode.getReferenceImageSmall(node.getSource());
						} else {
							return UINode.getNodeImageSmall(node.getType());
						}
					}
					case ListTableModel.TAGS_COLUMN: {
						if (node.getCodeCount() > 0) {
							return "T"; //$NON-NLS-1$
						} else {
							return ""; //$NON-NLS-1$
						}
					}	
					case ListTableModel.VIEWS_COLUMN: {
						int count = node.getViewCount();
						if (count == 0) {
							node.updateMultipleViews();
							count = node.getViewCount();
						}
						return new Integer(count);
					}								
					case ListTableModel.DETAIL_COLUMN: {
						String sDetail = node.getDetail();
						sDetail = sDetail.trim();
						if (!sDetail.equals("") && !sDetail.equals(ICoreConstants.NODETAIL_STRING)) { //$NON-NLS-1$
							return "*"; //$NON-NLS-1$
						} else {
							return ""; //$NON-NLS-1$
						}
					}
					case ListTableModel.WEIGHT_COLUMN: {
						if (node instanceof View) {
							View view  = (View) node;
							int count = 0;
							try {count = view.getNodeCount();}
							catch(Exception e){}
							return new Integer(count);							
						} 
						return null;
					}					
					case ListTableModel.LABEL_COLUMN: {
						return node.getLabel();
					}
					case ListTableModel.CREATION_DATE_COLUMN: {
						return node.getCreationDate();
					}
					case ListTableModel.MODIFICATION_DATE_COLUMN: {
						return node.getModificationDate();
					}
					case ListTableModel.ID_COLUMN: {
						return node.getId();
					}
					case ListTableModel.AUTHOR_COLUMN: {
						return node.getAuthor();
					}
					default:
						return null;
				}
			}
		}

		return null;
	}

	public void setValueAt(Object o, int row, int col) {

		String sAuthor = ProjectCompendium.APP.getModel().getUserProfile().getUserName();
		
		NodePosition np = (NodePosition) nodeData.elementAt(row);
		NodeSummary node = np.getNode();
		switch (col) {
			case ListTableModel.NUMBER_COLUMN: {
				if (o instanceof Integer)
					np.setYPos( ( ((Integer)o).intValue() +1 )*10 );
				break;
			}		
			case ListTableModel.LABEL_COLUMN: {
				String oldLabel = node.getLabel();
				String newLabel = (String) o;
				if (!oldLabel.equals(newLabel)) {
					try {
						node.setLabel(newLabel, sAuthor);
					}
					catch(Exception ex) {
						ProjectCompendium.APP.displayError("Error: (ListTableModel.setValueAt)\n\n"+//$NON-NLS-1$
								LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "ListTableModel.errorLabel")+//$NON-NLS-1$
								": "+oldLabel+"\n\n"+ex.getMessage()); //$NON-NLS-1$ //$NON-NLS-2$ 
					}
				}
				break;
			}
		}
	}

	public Class getColumnClass(int c) {
		switch (c) {
			case ListTableModel.IMAGE_COLUMN: {
				return new ImageIcon().getClass();
			}
			case ListTableModel.TAGS_COLUMN: 
			case ListTableModel.DETAIL_COLUMN:
			case ListTableModel.LABEL_COLUMN: 
			case ListTableModel.ID_COLUMN:
			case ListTableModel.AUTHOR_COLUMN: {
				return new String().getClass();
			}	
			case ListTableModel.VIEWS_COLUMN: 
			case ListTableModel.NUMBER_COLUMN:			
			case ListTableModel.WEIGHT_COLUMN: {
				return new Integer(0).getClass();
			}					
			case ListTableModel.CREATION_DATE_COLUMN:
			case ListTableModel.MODIFICATION_DATE_COLUMN: {
				return new Date().getClass();
			}
		}
		return null;
	}

	public boolean isCellEditable(int rowIndex, int columnIndex) {
		if (columnIndex == ListTableModel.LABEL_COLUMN) {
			return true;
		} else {
			return false;
		}
	}

	public NodePosition getNodePosition(int index) {
		return (NodePosition)nodeData.elementAt(index);
	}

	public void deleteRows(int[] rowIndexes) {

		Vector tempData = new Vector(nodeData.size());

		for (int j=0; j<nodeData.size(); j++) {
			tempData.addElement(nodeData.elementAt(j));
		}

		if (rowIndexes.length <= nodeData.size()) {
			for (int i = 0; i < rowIndexes.length; i++) {
				int next = rowIndexes[i];
				NodePosition np = (NodePosition)tempData.elementAt(next);
				nodeData.remove(np);
			}
		}

		tempData.removeAllElements();
		tempData = null;
		
		sorter.reallocateIndexes();
		sorter.fireTableChanged(new TableModelEvent(this));		
	}

	public void insertNodes(NodePosition[] nps, int index) {

		if (index > nodeData.size())
			index = nodeData.size();

		for (int i=0; i< nps.length; i++) {
			if (nps[i] != null) {
				if (index > nodeData.size()) {
					nodeData.addElement(nps[i]);
				} else {
					nodeData.insertElementAt(nps[i], index+i);
				}
			}
		}
		
		sorter.reallocateIndexes();
		sorter.fireTableChanged(new TableModelEvent(this));		
	}

	public void insertNode(NodePosition np, int index) {

		if (np != null) {
			if (index > nodeData.size()) {
				nodeData.addElement(np);
			} else {
				nodeData.insertElementAt(np, index);
			}
			sorter.reallocateIndexes();
			sorter.fireTableChanged(new TableModelEvent(this));			
		}
	}
	
	public boolean contains(NodePosition np) {
		return nodeData.contains(np);
	}
}
