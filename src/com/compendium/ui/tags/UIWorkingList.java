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

import java.awt.*;
import java.awt.event.*;
import java.awt.dnd.*;
import java.awt.datatransfer.*;
import java.util.*;
import java.beans.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import javax.swing.border.*;

import com.compendium.core.ICoreConstants;
import com.compendium.core.datamodel.*;

import com.compendium.LanguageProperties;
import com.compendium.ProjectCompendium;
import com.compendium.ui.*;
import com.compendium.ui.panels.UIHintNodeCodePanel;
import com.compendium.ui.panels.UIHintNodeViewsPanel;

/**
 * This class controls the tags working table area.
 *
 * @author	Michelle Bachler
 */
public class UIWorkingList implements TableModelListener, MouseListener, MouseMotionListener, PropertyChangeListener /*, 
											DragSourceListener, DragGestureListener, Transferable*/ { 

	/** Table that holds the list data for this list view.*/
	private JTable 			table;

	/** The model for the data in the list view table.*/
	private UITagsListTableModel model;

	/** The table sort for the table for this list view.*/
	private TableSorter  	sorter;

	/** The crop target reference for this list instance.*/
	//private DropTarget 		dropTarget 				= null;
		
	/** The dialog used for the rollover hints.*/
	private JDialog dialog				= null;		
	
	/** The last row the rollover hint was for.*/
	int		lastRow				= -1;
	
	/** The last column the rollover hint was for.*/
	int		lastColumn			= -1;
	
	/** The data flavors supported by this class.*/
    public static final 		DataFlavor[] supportedFlavors = { null };
	static    {
		try { supportedFlavors[0] = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType); }
		catch (Exception ex) { ex.printStackTrace(); }
	}		
	/** The DragSource object associated with this node list.*/
	private DragSource 			dragSource;
	
	
	/**
	 * Constructor. Initializes and table and options for this list.
	 */
	public UIWorkingList() {

		model = new UITagsListTableModel();
		sorter = new TableSorter(model);
		sorter.addTableModelListener(this);
		table = new JTable(sorter);
		
		//CSH.setHelpIDString(table,"node.views");
		
		table.getColumn(LanguageProperties.getString(LanguageProperties.TAGS_BUNDLE, "UIWorkingList.img")).setPreferredWidth(10); //$NON-NLS-1$
		table.getColumn(LanguageProperties.getString(LanguageProperties.TAGS_BUNDLE, "UIWorkingList.tags")).setPreferredWidth(10); //$NON-NLS-1$
		table.getColumn(LanguageProperties.getString(LanguageProperties.TAGS_BUNDLE, "UIWorkingList.views")).setPreferredWidth(10);		 //$NON-NLS-1$
		table.getColumn(LanguageProperties.getString(LanguageProperties.TAGS_BUNDLE, "UIWorkingList.label")).setPreferredWidth(300); //$NON-NLS-1$
		table.getColumn(LanguageProperties.getString(LanguageProperties.TAGS_BUNDLE, "UIWorkingList.modedate")).setPreferredWidth(150); //$NON-NLS-1$

		table.getColumn(LanguageProperties.getString(LanguageProperties.TAGS_BUNDLE, "UIWorkingList.img")).setMaxWidth(60); //$NON-NLS-1$
		table.getColumn(LanguageProperties.getString(LanguageProperties.TAGS_BUNDLE, "UIWorkingList.tags")).setMaxWidth(60); //$NON-NLS-1$
		table.getColumn(LanguageProperties.getString(LanguageProperties.TAGS_BUNDLE, "UIWorkingList.views")).setMaxWidth(60);		 //$NON-NLS-1$
		table.getColumn(LanguageProperties.getString(LanguageProperties.TAGS_BUNDLE, "UIWorkingList.modedate")).setMaxWidth(200); //$NON-NLS-1$
		
		table.getTableHeader().setReorderingAllowed(false);
		
		sorter.addMouseListenerToHeaderInTable(table);

		table.addMouseMotionListener(this);		
		table.addMouseListener(this);	
		
		table.setFont(ProjectCompendiumFrame.currentDefaultFont);
		FontMetrics metrics = table.getFontMetrics(ProjectCompendiumFrame.currentDefaultFont);
		table.setRowHeight(table.getRowMargin()+metrics.getHeight());				
		
		setRenderers();

		((TableSorter)table.getModel()).setSelectedColumn(3);

		//dragSource = new DragSource();
		//dragSource.createDefaultDragGestureRecognizer((Component)this.table, DnDConstants.ACTION_COPY, this);
	}

	/**
	 * Return the font size to its default and then appliy the passed text zoom.
	 * (To the default specificed by the user in the Project Options)
	 */
	public void onReturnTextAndZoom(int zoom) {
		Font font = table.getFont();
		Font newFont = new Font(font.getName(), font.getStyle(), font.getSize()+zoom);			
		table.setFont(newFont);
		FontMetrics metrics = table.getFontMetrics(newFont);
		table.setRowHeight(table.getRowMargin()+metrics.getHeight());				
	}
	
	/**
	 * Return the font size to its default 
	 * (To the default specificed by the user in the Project Options)
	 */
	public void onReturnTextToActual() {
		table.setFont(ProjectCompendiumFrame.currentDefaultFont);
		FontMetrics metrics = table.getFontMetrics(ProjectCompendiumFrame.currentDefaultFont);
		table.setRowHeight(table.getRowMargin()+metrics.getHeight());				
	}
	
	/**
	 * Increase the currently dislayed font size by one point.
	 */
	public void onIncreaseTextSize() {
		Font font = table.getFont();
		Font newFont = new Font(font.getName(), font.getStyle(), font.getSize()+1);			
		table.setFont(newFont);
		FontMetrics metrics = table.getFontMetrics(newFont);
		table.setRowHeight(table.getRowMargin()+metrics.getHeight());
	}
	
	/**
	 * Reduce the currently dislayed font size by one point.
	 */
	public void onReduceTextSize() {
		Font font = table.getFont();
		Font newFont = new Font(font.getName(), font.getStyle(), font.getSize()-1);			
		table.setFont(newFont);
		FontMetrics metrics = table.getFontMetrics(newFont);
		table.setRowHeight(table.getRowMargin()+metrics.getHeight());
	}
	
	public void clearPopup() {
		if (dialog != null) {
    		dialog.setVisible(false);
	    	dialog.dispose();
    		dialog = null;
	    	lastRow = -1;		
	    	lastColumn = -1;		    					
		}
	}
	
	public void mousePressed(MouseEvent e) {}
	public void mouseReleased(MouseEvent e) {}
	public void mouseEntered(MouseEvent e){}
	
	public void mouseClicked(MouseEvent e) {
    	if (lastColumn == UITagsListTableModel.VIEWS_COLUMN) {
    		clearPopup();
    	}				
	}
	
	public void mouseExited(MouseEvent e) {
    	if (lastColumn == UITagsListTableModel.TAGS_COLUMN) {
    		clearPopup();
    	}				
	}
	
	public void mouseDragged(MouseEvent e){}
	
	public void mouseMoved(MouseEvent e) {				
		int column = table.columnAtPoint( e.getPoint() );
		int row = table.rowAtPoint( e.getPoint() );		
		int ind = sorter.getRealRow(row);		
		if (ind == -1) {
			return;
		}
		
		if (ind == lastRow && column == lastColumn) {
			e.consume();
			return;
		}
		
	    if (column == UITagsListTableModel.TAGS_COLUMN) {
    		clearPopup();
			NodeSummary summary = getNodeAt(row);
			try {
				if (summary.getCodeCount() > 0 ) {							
					UIHintNodeCodePanel pop = new UIHintNodeCodePanel(summary, 0, 0);
					dialog = new JDialog(ProjectCompendium.APP);
					lastRow = ind;
					lastColumn = column;
					dialog.add(pop);
					dialog.setUndecorated(true);
					dialog.pack();	
					Point point = e.getPoint();
					SwingUtilities.convertPointToScreen(point, table);					
					//Point point = SwingUtilities.convertPoint(table, e.getPoint(), ProjectCompendium.APP);
					dialog.setLocation(point.x+5, point.y);
					dialog.setVisible(true);
				} 
			}
			catch(Exception ex) {
				System.out.println("Error: (UIWorkingList.showCodes)\n\n"+ex.getMessage()); //$NON-NLS-1$
			}			    
	    } if (column == UITagsListTableModel.VIEWS_COLUMN) {
    		clearPopup();
	    	NodeSummary summary = getNodeAt(row);
			try {
				UIHintNodeViewsPanel pop = new UIHintNodeViewsPanel(summary, 0, 0);
				dialog = new JDialog(ProjectCompendium.APP);
				lastRow = ind;
				lastColumn = column;
				dialog.add(pop);
				dialog.setUndecorated(true);
				dialog.pack();		
				Point point = e.getPoint();
				SwingUtilities.convertPointToScreen(point, table);
				//Point point = SwingUtilities.convertPoint(table, e.getPoint(), ProjectCompendium.APP);
				dialog.setLocation(point.x+5, point.y);
				dialog.setVisible(true); 
			}
			catch(Exception ex) {
				System.out.println("Error: (UIWorkingList.showViews)\n\n"+ex.getMessage()); //$NON-NLS-1$
			}
	    }			    
	}	
	
	public void hideHint() {
		if (dialog != null) {
	    	dialog.setVisible(false);
	    	dialog.dispose();
	    	dialog = null;
	    	lastRow = -1;
	    	lastColumn = -1;
		}
	}	
	
	/*private class myViewsPanel extends UIHintNodeViewsPanel {
		
		public myViewsPanel(NodeSummary node, int xPos, int yPos) {
			super(node, xPos, yPos);
		}
		
		protected MouseListener createMouseListener() {
			MouseListener mouse = new MouseAdapter() {
				public void mouseClicked(MouseEvent e) {
					int index = lstViews.locationToIndex( e.getPoint());
					String id = "";
					View view = (View)views.elementAt(index);
					id = view.getId();
					if ( !id.equals(currentView.getId())) {
						if (!htUserViews.containsKey(view.getId()) 							
							|| id.equals(ProjectCompendium.APP.getInBoxID())) {
							UIViewFrame oUIViewFrame = ProjectCompendium.APP.addViewToDesktop(view, view.getLabel());
							Vector history = new Vector();
							history.addElement(new String(sTitle));
							oUIViewFrame.setNavigationHistory(history);
							if (oUIViewFrame instanceof UIMapViewFrame) {
								UIViewPane oPane = ((UIMapViewFrame)oUIViewFrame).getViewPane();
								oPane.hideViews();
								UINode oUINode = (UINode) oPane.get(oNode.getId());
								if (oUINode != null) {
									JViewport viewport = oUIViewFrame.getViewport();
									Rectangle nodeBounds = oUINode.getBounds();
									Point parentPos = SwingUtilities.convertPoint((Component)oPane, nodeBounds.x, nodeBounds.y, viewport);
									viewport.scrollRectToVisible( new Rectangle( parentPos.x, parentPos.y, nodeBounds.width, nodeBounds.height ) );

									oUINode.setRollover(true);
									oUINode.moveToFront();
								}
							} else {
								UIListViewFrame oListViewFrame = (UIListViewFrame)oUIViewFrame;
								UIList oUIList = oListViewFrame.getUIList();
								int ind = oUIList.getIndexOf(oNode);
								if (ind > -1) {
									oUIList.getList().addRowSelectionInterval(ind, ind);
								}
							}								
						}
					}
				}
				
				public void mouseExited(MouseEvent evt) {
			    	dialog.setVisible(false);
			    	dialog.dispose();
			    	dialog = null;
			    	lastRow = -1;		
			    	lastColumn = -1;
				}			
			};
			return mouse;
		}
	}*/
	
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
		
		// REMOVE PropertyChangeListener from old data and add to new incoming data.
		NodeSummary node = null;
		Vector vtOldData = model.getData();
		int count = vtOldData.size();
		for (int i=0; i<count; i++) {
			node = (NodeSummary)vtOldData.elementAt(i);
			node.removePropertyChangeListener(this);
		}
		
		count = vtData.size();
		for (int i=0; i<count; i++) {
			node = (NodeSummary)vtData.elementAt(i);
			node.addPropertyChangeListener(this);
		}
		
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
	
	public NodeSummary getNodeAt(int row) {
		int realRow = sorter.getRealRow(row);
		if (realRow == -1) {
			return null;
		} else {
			return model.getNodeAt(realRow);
		}
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

				if(column == UITagsListTableModel.IMAGE_COLUMN){
					setIcon( (Icon) value);
					setHorizontalAlignment(CENTER);
					setVerticalAlignment(CENTER);
					value = ""; //$NON-NLS-1$
			 	} 
			}
			else {
				setBackground(table.getBackground());
				setForeground(table.getForeground());				
				if (column == UITagsListTableModel.IMAGE_COLUMN) {
					setIcon( (Icon) value);
					setHorizontalAlignment(CENTER);
					setVerticalAlignment(CENTER);
					value = ""; //$NON-NLS-1$
				}
			}
    		
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
	public JTable getList() {
		return table;
	}

	/**
	 * Return the number of currently selected node in the list.
	 * @return int, the number of currently selected node in the list.
	 */
	public int getNumberOfSelectedNodes() {
		return table.getSelectedRowCount();
	}

	/**
	 * Deselect all selected rows.
	 */
	public void deselectAll() {
		table.clearSelection();
	}

	/**
	 * Return the number of nodes currently in this list view.
	 * @return int, the number of nodes currently in this list view.
	 */
	public int getNumberOfNodes() {
		return table.getRowCount();
	}    
    
// PROPERTY CHANGE EVENT METHOD
	
	/**
	 * Handle a PropertyChangeEvent.
	 * @param evt, the associated PropertyChangeEvent to handle.
	 */
	public void propertyChange(PropertyChangeEvent evt) {

	    String prop = evt.getPropertyName();
   		Object source = evt.getSource();
	    //Object oldvalue = evt.getOldValue();
	    //Object newvalue = evt.getNewValue();
	    
		if (source instanceof NodeSummary) {
		    if (prop.equals(NodeSummary.LABEL_PROPERTY) || prop.equals(View.CHILDREN_PROPERTY)) {
				table.revalidate();
				table.repaint();		
		    } 
		    
		    /*if (prop.equals(NodeSummary.TAG_PROPERTY)) {
		    	firePropertyChange(NodeSummary.TAG_PROPERTY, oldvalue, newvalue);
		    }
		    else if (prop.equals(NodeSummary.NODE_TYPE_PROPERTY)) {
			}
		    else if (prop.equals(NodeSummary.VIEW_NUM_PROPERTY)) {
				firePropertyChange(NodeSummary.VIEW_NUM_PROPERTY, oldvalue, newvalue);
		    }
		    else if (prop.equals(NodeSummary.STATE_PROPERTY)) {
		    	firePropertyChange(NodeSummary.STATE_PROPERTY, oldvalue, newvalue);
		    }
	    	else if (prop.equals(View.CHILDREN_PROPERTY)) {
				firePropertyChange(CHILDREN_PROPERTY, oldvalue, newvalue);
		    }*/
		}
	}	
	
// DRAG AND DROP METHODS

	   /**
     * Returns an array of DataFlavor objects indicating the flavors the data
     * can be provided in.
     * @return an array of data flavors in which this data can be transferred
     */
	/*public DataFlavor[] getTransferDataFlavors() {
		return supportedFlavors;
	}*/

    /**
     * Returns whether or not the specified data flavor is supported for
     * this object.
     * @param flavor the requested flavor for the data
     * @return boolean indicating whether or not the data flavor is supported
     */
	/*public boolean isDataFlavorSupported(DataFlavor flavor) {
		return flavor.isMimeTypeEqual(DataFlavor.javaJVMLocalObjectMimeType);
	}*/

    /**
     * Returns an object which represents the data to be transferred.  The class
     * of the object returned is defined by the representation class of the flavor.
     *
     * @param flavor the requested flavor for the data
     * @see DataFlavor#getRepresentationClass
     * @exception IOException                if the data is no longer available in the requested flavor.
     * @exception UnsupportedFlavorException if the requested data flavor is not supported.
     */
	/*public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
		if (flavor.isMimeTypeEqual(DataFlavor.javaJVMLocalObjectMimeType))
			return this;
		else return null;
	}*/

    /**
     * A <code>DragGestureRecognizer</code> has detected
     * a platform-dependent drag initiating gesture and
     * is notifying this listener
     * in order for it to initiate the action for the user.
     * <P>
     * @param e the <code>DragGestureEvent</code> describing
     * the gesture that has just occurred
     */
	/*public void dragGestureRecognized(DragGestureEvent e) {
	    InputEvent in = e.getTriggerEvent();
	    if (in instanceof MouseEvent) {
			MouseEvent evt = (MouseEvent)in;
			boolean isLeftMouse = SwingUtilities.isLeftMouseButton(evt);

		    if (isLeftMouse && !evt.isAltDown()) {
				dragSource.startDrag(e, DragSource.DefaultCopyDrop, this, this);
			}
		}
	}*/

    /**
     * This method is invoked to signify that the Drag and Drop
     * operation is complete. The getDropSuccess() method of
     * the <code>DragSourceDropEvent</code> can be used to
     * determine the termination state. The getDropAction() method
     * returns the operation that the drop site selected
     * to apply to the Drop operation. Once this method is complete, the
     * current <code>DragSourceContext</code> and
     * associated resources become invalid.
	 * HERE THE METHOD DOES NOTHING.
     *
     * @param e the <code>DragSourceDropEvent</code>
     */
	//public void dragDropEnd(DragSourceDropEvent e) {}

    /**
     * Called as the cursor's hotspot enters a platform-dependent drop site.
     * This method is invoked when all the following conditions are true:
     * <UL>
     * <LI>The cursor's hotspot enters the operable part of a platform-
     * dependent drop site.
     * <LI>The drop site is active.
     * <LI>The drop site accepts the drag.
     * </UL>
	 * HERE THE METHOD DOES NOTHING.
     *
     * @param e the <code>DragSourceDragEvent</code>
     */
	//public void dragEnter(DragSourceDragEvent e) {}

    /**
     * Called as the cursor's hotspot exits a platform-dependent drop site.
     * This method is invoked when any of the following conditions are true:
     * <UL>
     * <LI>The cursor's hotspot no longer intersects the operable part
     * of the drop site associated with the previous dragEnter() invocation.
     * </UL>
     * OR
     * <UL>
     * <LI>The drop site associated with the previous dragEnter() invocation
     * is no longer active.
     * </UL>
     * OR
     * <UL>
     * <LI> The current drop site has rejected the drag.
     * </UL>
	 * HERE THE METHOD DOES NOTHING.
     *
     * @param e the <code>DragSourceEvent</code>
     */
	//public void dragExit(DragSourceEvent e) {}

    /**
     * Called as the cursor's hotspot moves over a platform-dependent drop site.
     * This method is invoked when all the following conditions are true:
     * <UL>
     * <LI>The cursor's hotspot has moved, but still intersects the
     * operable part of the drop site associated with the previous
     * dragEnter() invocation.
     * <LI>The drop site is still active.
     * <LI>The drop site accepts the drag.
     * </UL>
	 * HERE THE METHOD DOES NOTHING.
     *
     * @param e the <code>DragSourceDragEvent</code>
     */
	//public void dragOver(DragSourceDragEvent e) {}

    /**
     * Called when the user has modified the drop gesture.
     * This method is invoked when the state of the input
     * device(s) that the user is interacting with changes.
     * Such devices are typically the mouse buttons or keyboard
     * modifiers that the user is interacting with.
	 * HERE THE METHOD DOES NOTHING.
     *
     * @param e the <code>DragSourceDragEvent</code>
     */
	//public void dropActionChanged(DragSourceDragEvent e) {}
}