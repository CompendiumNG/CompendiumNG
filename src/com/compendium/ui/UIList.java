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
import java.awt.dnd.*;
import java.awt.datatransfer.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.sql.SQLException;
import java.text.*;
import java.util.*;
import java.io.*;
import java.beans.*;

import javax.help.*;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import javax.swing.border.*;

import com.compendium.core.datamodel.*;
import com.compendium.core.CoreUtilities;
import com.compendium.core.ICoreConstants;

import com.compendium.meeting.*;

import com.compendium.LanguageProperties;
import com.compendium.ProjectCompendium;
import com.compendium.ui.panels.UIHintNodeCodePanel;
import com.compendium.ui.plaf.*;
import com.compendium.ui.popups.*;
import com.compendium.ui.edits.*;
import com.compendium.ui.dialogs.*;
import com.compendium.ui.panels.*;

/**
 * This class is the controlling class for Compendium Lists.
 *
 * @author	Cheralathan Balakrishnan / Michelle Bachler / Lakshmi Prabhakaran
 */
public class UIList implements PropertyChangeListener, TableModelListener, ListSelectionListener,
											DropTargetListener,	MouseListener, MouseMotionListener {
	
	/** The data flavors supported by this class.*/
    //public static final 		DataFlavor[] supportedFlavors = { null };
	//static    {
	//	try { supportedFlavors[0] = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType+"; class=com.compendium.ui.UIList", null); }
	//	catch (Exception ex) { ex.printStackTrace(); }
	//}
	
	/** The view associated with this list view.*/
	protected View			oView			= null;

	/** The parent frame for this list view.*/
	protected UIViewFrame	oViewFrame	= null;

	/** The ListUI object associated with this list view.*/
	private ListUI			listUI			= null;

	/** Table that holds the list data for this list view.*/
	private JTable 			table;

	/** The table sort for the table for this list view.*/
	private TableSorter 	sorter;

	/** The model for this list view.*/
	private ListTableModel 	model;

	/** Holds a list of the node content dialogs opened in this view.*/
	private Hashtable 		contentDialogs 			= new Hashtable();

	/** A reference to the right-click popup menu last activated in this view.*/
	private UINodePopupMenuForList viewPopup		= null;

	/** Are the table columns current contracted or expanded to show all availbale columns?*/
	private boolean			isSmall 				= true;

	/** The crop target reference for this list instance.*/
	private DropTarget 		dropTarget 				= null;
	
	/** The NodeSummary of the deleted node*/
	private NodeSummary 		deletedNode 		= null;
	
	/** The author name of the current user.*/
	private String sAuthor = ""; //$NON-NLS-1$
	
	/** The dialog used for the rollover hints.*/
	private JDialog dialog				= null;		
	
	/** The last row the rollover hint was for.*/
	int		lastRow				= -1;
	
	/** The last column the rollover hint was for.*/
	int		lastColumn			= -1;
	
	/** The DragSource object associated with this draggable item.*/
	//private DragSource 			dragSource;
	
	/**
	 * Constructor. Initializes and table and options for this list view.
	 * @param view com.compendium.core.datamodel.View, the view associated with this list view.
	 * @param viewFrame com.compendium.ui.UIViewFrame, the parent frame for this list view.
	 */
	public UIList(View view, UIViewFrame viewframe) {

		oViewFrame = viewframe;

		view.addPropertyChangeListener(this);
		setView(view);

		model = new ListTableModel(view);
		sorter = new TableSorter(model);
		sorter.addTableModelListener(this);
		model.setSorter(sorter);
		table = new JTable(sorter);
		table.getSelectionModel().addListSelectionListener(this);
		
		this.sAuthor = ProjectCompendium.APP.getModel().getUserProfile().getUserName() ;
		
		CSH.setHelpIDString(table,"node.views"); //$NON-NLS-1$

		table.getColumn(LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE,"ListTableModel.no")).setMinWidth(0); //$NON-NLS-1$
		table.getColumn(LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE,"ListTableModel.id")).setMinWidth(0); //$NON-NLS-1$
		table.getColumn(LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE,"ListTableModel.createDate")).setMinWidth(0); //$NON-NLS-1$
		table.getColumn(LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE,"ListTableModel.modDate")).setMinWidth(0); //$NON-NLS-1$

		table.getColumn(LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE,"ListTableModel.no")).setMaxWidth(0); //$NON-NLS-1$
		table.getColumn(LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE,"ListTableModel.id")).setMaxWidth(0); //$NON-NLS-1$
		table.getColumn(LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE,"ListTableModel.createDate")).setMaxWidth(200); //$NON-NLS-1$
		table.getColumn(LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE,"ListTableModel.modDate")).setMaxWidth(0); //$NON-NLS-1$

		table.getColumn(LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE,"ListTableModel.no")).setPreferredWidth(0); //$NON-NLS-1$
		table.getColumn(LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE,"ListTableModel.id")).setPreferredWidth(0); //$NON-NLS-1$
		table.getColumn(LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE,"ListTableModel.createDate")).setPreferredWidth(200); //$NON-NLS-1$
		table.getColumn(LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE,"ListTableModel.modDate")).setPreferredWidth(0); //$NON-NLS-1$
		
		table.getColumn(LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE,"ListTableModel.img")).setPreferredWidth(15); //$NON-NLS-1$
		table.getColumn(LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE,"ListTableModel.tags")).setPreferredWidth(15); //$NON-NLS-1$
		table.getColumn(LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE,"ListTableModel.views")).setPreferredWidth(15); //$NON-NLS-1$
		table.getColumn(LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE,"ListTableModel.details")).setPreferredWidth(15); //$NON-NLS-1$
		table.getColumn(LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE,"ListTableModel.weight")).setPreferredWidth(15);		 //$NON-NLS-1$
		table.getColumn(LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE,"ListTableModel.label")).setPreferredWidth(700); //$NON-NLS-1$
		table.getColumn(LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE,"ListTableModel.author")).setPreferredWidth(150); //$NON-NLS-1$

		table.getColumn(LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE,"ListTableModel.img")).setMaxWidth(60); //$NON-NLS-1$
		table.getColumn(LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE,"ListTableModel.tags")).setMaxWidth(60); //$NON-NLS-1$
		table.getColumn(LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE,"ListTableModel.views")).setMaxWidth(60); //$NON-NLS-1$
		table.getColumn(LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE,"ListTableModel.details")).setMaxWidth(60); //$NON-NLS-1$
		table.getColumn(LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE,"ListTableModel.weight")).setMaxWidth(60);		 //$NON-NLS-1$
		
		table.getTableHeader().setReorderingAllowed(false);
		table.setFont(ProjectCompendiumFrame.currentDefaultFont);
		FontMetrics metrics = table.getFontMetrics(ProjectCompendiumFrame.currentDefaultFont);
		table.setRowHeight(table.getRowMargin()+metrics.getHeight());
		
		sorter.addMouseListenerToHeaderInTable(table);
		setRenderers();
 		listUI = new ListUI(table, this);
		table.addMouseMotionListener(this);		
		table.addMouseListener(this);	
		
		sorter.setSelectedColumn(ListTableModel.LABEL_COLUMN);

		dropTarget = new DropTarget((Component)table, this);
		DropTarget dropTarget2 = new DropTarget((Component)table.getTableHeader(), this);
		
		//dragSource = new DragSource();
		//dragSource.createDefaultDragGestureRecognizer((JComponent)table, DnDConstants.ACTION_MOVE, this);   			
	}

	/**
	 * Return the font size to its default and then appliy the passed text zoom.
	 * (To the default specificed by the user in the Project Options)
	 */
	public void onReturnTextAndZoom(int zoom) {
		Font font = ProjectCompendiumFrame.currentDefaultFont;
		Font newFont = new Font(font.getName(), font.getStyle(), font.getSize()+zoom);			
		table.setFont(newFont);
		FontMetrics metrics = table.getFontMetrics(newFont);
		table.setRowHeight(table.getRowMargin()+metrics.getHeight());				
	}

	/**
	 * Checks to see if there are any open content dialogs and repaints them if there are.
	 */
	private void repaintDialogs() {		
		UINodeContentDialog dlg = null;				
		for (Enumeration e = contentDialogs.elements(); e.hasMoreElements();) {
			dlg = (UINodeContentDialog)e.nextElement();
			dlg.refreshFont();
		}
	}
	
	/**
	 * Return the font size to its default 
	 * (To the default specificed by the user in the Project Options)
	 */
	public void onReturnTextToActual() {
		table.setFont(ProjectCompendiumFrame.currentDefaultFont);
		FontMetrics metrics = table.getFontMetrics(ProjectCompendiumFrame.currentDefaultFont);
		table.setRowHeight(table.getRowMargin()+metrics.getHeight());
		
		repaintDialogs();
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
		
		repaintDialogs();
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
		
		repaintDialogs();
	}
	
	/** Unsort the table*/
	/*public void unsort() {
		sorter.reallocateIndexes();
		table.invalidate();
		table.repaint();
	}*/
	
	public void mousePressed(MouseEvent e) {}
	public void mouseReleased(MouseEvent e) {}
	public void mouseEntered(MouseEvent e){}
	
	public void mouseClicked(MouseEvent e) {
    	if (lastColumn == ListTableModel.VIEWS_COLUMN && dialog != null) {
    		dialog.setVisible(false);
	    	dialog.dispose();
    		dialog = null;
	    	lastRow = -1;		
	    	lastColumn = -1;		    		
    	}				
	}
	
	public void mouseExited(MouseEvent e) {
    	if ((lastColumn == ListTableModel.TAGS_COLUMN || lastColumn == ListTableModel.DETAIL_COLUMN) && dialog != null) {
    		dialog.setVisible(false);
	    	dialog.dispose();
    		dialog = null;
	    	lastRow = -1;		
	    	lastColumn = -1;
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
		
	    if (column == ListTableModel.TAGS_COLUMN) {
			if (dialog != null) {
		    	dialog.setVisible(false);
		    	dialog.dispose();
		    	dialog = null;
		    	lastRow = -1;		
		    	lastColumn = -1;
			}					
			NodePosition pos = getNodePosition(ind);
			NodeSummary summary = pos.getNode();
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
				System.out.println("Error: (UIList.showCodes)\n\n"+ex.getMessage()); //$NON-NLS-1$
			}			    
	    } if (column == ListTableModel.VIEWS_COLUMN) {
			if (dialog != null) {
		    	dialog.setVisible(false);
		    	dialog.dispose();
		    	dialog = null;
		    	lastRow = -1;		
		    	lastColumn = -1;
			}											 
			NodePosition pos = getNodePosition(ind);
			NodeSummary summary = pos.getNode();
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
				System.out.println("Error: (UIList.showViews)\n\n"+ex.getMessage()); //$NON-NLS-1$
			}
	    } else if (column == ListTableModel.DETAIL_COLUMN) {
			if (dialog != null) {
		    	dialog.setVisible(false);
		    	dialog.dispose();
		    	dialog = null;
		    	lastRow = -1;		
		    	lastColumn = -1;
			}					
			NodePosition pos = getNodePosition(ind);
			NodeSummary summary = pos.getNode();
			try {
				String sDetail = summary.getDetail();
				sDetail = sDetail.trim();
				if (!sDetail.equals("") && !sDetail.equals(ICoreConstants.NODETAIL_STRING)) { //$NON-NLS-1$
					UIHintNodeDetailPanel pop = new UIHintNodeDetailPanel(summary, 0, 0);
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
				System.out.println("Error: (UIList.showDetail)\n\n"+ex.getMessage()); //$NON-NLS-1$
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
	
	/**
	 * Set the header renderers for the table column headers and the table cells.
	 */
    public void setRenderers() {
    	int count = table.getColumnCount();
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
        		
    		NodePosition node = model.getNodePosition(row);
    		if (node != null) {
	    		NodeSummary oNodeSummary = node.getNode();
	    		
				// Important do the user's font choice is applied 
	    		// Make weight column italic to show it is not active.
	    		if (column == ListTableModel.WEIGHT_COLUMN) {
	    			Font font = table.getFont();
	    			setFont(new Font(font.getFontName(), Font.ITALIC, font.getSize()));
	    		} else {
	    			setFont(table.getFont());
	    		}
				setBorder( isSelected ?UIManager.getBorder("List.focusCellHighlightBorder") : new EmptyBorder(1,1,1,1)); //$NON-NLS-1$
				if (isSelected) {
					setBackground(table.getSelectionBackground());
					setForeground(table.getSelectionForeground());
				}
				else {
					setBackground(table.getBackground());
					setForeground(table.getForeground());					
				}
				
				if (column == ListTableModel.IMAGE_COLUMN) { 
					setIcon( (Icon) value);
					setHorizontalAlignment(CENTER);
					setVerticalAlignment(CENTER);
					setBorder(new NodeBorder(oNodeSummary));						
					value = ""; //$NON-NLS-1$
				}	
				
				if (column == ListTableModel.CREATION_DATE_COLUMN) {
					SimpleDateFormat dateFormat = new SimpleDateFormat("EEE dd-MMM-yy h:mm a"); //$NON-NLS-1$
					value = dateFormat.format(value);
				}
    		}
    		    		
			setValue(value);			     	
        	return this;
		}

        protected void setValue(Object value) {
        	setText((value == null) ? "" : value.toString()); //$NON-NLS-1$
        }
	}    	

	/**
	 * This border class paints the border for this node.
	 */
	private class NodeBorder extends AbstractBorder {
		
		int state		 = 0;
		NodeSummary node = null;
		
		public NodeBorder(NodeSummary node){
			this.node  = node;
			this.state = node.getState();
		}

		public void paintBorder (Component c, Graphics g, int x, int y, int width, int height) {

			if (state == ICoreConstants.UNREADSTATE) {
				Color oldColor = g.getColor();
				g.setColor(IUIConstants.UNREAD_BORDER_COLOR);
				g.draw3DRect(x, y, width - 1, height - 1, true);
				g.setColor(oldColor);
				
			}
			else if (state == ICoreConstants.MODIFIEDSTATE) {
				Color oldColor = g.getColor();
				g.setColor(IUIConstants.MODIFIED_BORDER_COLOR);
				g.draw3DRect(x, y, width - 1, height - 1, true);
				g.setColor(oldColor);
			} else {
				Color oldColor = g.getColor();
				g.setColor(oldColor);
			}
			
		}
	}

	/**
	 * Create a new node with the given proerties.
 	 * @param nType, the type of the new node.
	 * @param nX, the x position for the new node.
	 * @param nY, the y position for the new node.
	 */
	private NodePosition createNode(int nType, int nX, int nY) {

		NodePosition node = listUI.createNode(nType,
							 "", //$NON-NLS-1$
							 ProjectCompendium.APP.getModel().getUserProfile().getUserName(),
							 "", //$NON-NLS-1$
							 "", //$NON-NLS-1$
							 nX,
							 nY
							 );

		updateTable();
		selectNode(getNumberOfNodes() - 1, ICoreConstants.MULTISELECT);

		return node;
	}

	/**
	 * Return if the the list table is currently contracted or exapanded.
	 * @return boolean, true if the list table is currently contracted, else false.
	 */
	public boolean isSmall() {
		return isSmall;
	}

	/**
	 * Set the size of the list table.
	 * @param size, indicates is a small or large table layout should be drawn.
	 * i.e. with extra columns of information.
	 */
	public void setSize(String size) {
		if (size.equals("small")) //$NON-NLS-1$
			isSmall = true;
		else
			isSmall = false;

		if (isSmall) {
			table.getColumn(LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE,"ListTableModel.id")).setMaxWidth(0); //$NON-NLS-1$
			table.getColumn(LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE,"ListTableModel.createDate")).setMaxWidth(200);	//mlb add Cr Date & Author to default list view //$NON-NLS-1$
			table.getColumn(LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE,"ListTableModel.modDate")).setMaxWidth(0); //$NON-NLS-1$

			table.getColumn(LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE,"ListTableModel.id")).setPreferredWidth(0); //$NON-NLS-1$
			table.getColumn(LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE,"ListTableModel.createDate")).setPreferredWidth(200);	 //$NON-NLS-1$
			table.getColumn(LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE,"ListTableModel.modDate")).setPreferredWidth(0); //$NON-NLS-1$
			
			table.getColumn(LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE,"ListTableModel.label")).setPreferredWidth(700); //$NON-NLS-1$
			//((TableSorter)table.getModel()).setSelectedColumn(ListTableModel.LABEL_COLUMN);
			table.getColumn(LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE,"ListTableModel.author")).setPreferredWidth(150); //$NON-NLS-1$
		}
		else  {
			table.getColumn(LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE,"ListTableModel.id")).setMaxWidth(200); //$NON-NLS-1$
			table.getColumn(LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE,"ListTableModel.createDate")).setMaxWidth(200); //$NON-NLS-1$
			table.getColumn(LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE,"ListTableModel.modDate")).setMaxWidth(200); //$NON-NLS-1$
			
			table.getColumn(LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE,"ListTableModel.id")).setPreferredWidth(180); //$NON-NLS-1$
			table.getColumn(LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE,"ListTableModel.createDate")).setPreferredWidth(100); //$NON-NLS-1$
			table.getColumn(LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE,"ListTableModel.modDate")).setPreferredWidth(100); //$NON-NLS-1$
			
			table.getColumn(LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE,"ListTableModel.label")).setPreferredWidth(400);		 //$NON-NLS-1$
			//((TableSorter)table.getModel()).setSelectedColumn(ListTableModel.LABEL_COLUMN);
			table.getColumn(LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE,"ListTableModel.author")).setPreferredWidth(150); //$NON-NLS-1$
		}

		updateTable();
	}

	/**
	 * Convenience Method to get Containing ViewFrame.
	 * @return com.compendium.ui.UIViewFrame, the parent frame for this list.
	 */
	public UIViewFrame getViewFrame() {
		return oViewFrame;
	}

	/**
	 * Return the JTable that holds the view list.
	 * @return JTable, the JTable that holds the view list.
	 */
	public JTable getList() {
		return table;
	}

	/**
	 * Return the ListUI for this view list
	 * @return ListUI, the ListUI for this list.
	 */
	public ListUI getListUI() {
		return listUI;
	}

	/**
	 * Override to always return true.
	 * @return boolean, true.
	 */
  	public boolean isOpaque() {
		return true;
	}

   /**
	 * Set the view that this view pane represents.
	 *
	 * @param view com.compendium.core.datamodel.View, the view represented by this view pane.
	 * @see com.compendium.core.datamodel.IView
	 */
	public void setView(View view) {
		NodePosition pos = null;
		
		if (oView != null) {
			for(Enumeration e = oView.getPositions();e.hasMoreElements();) {
				pos = ((NodePosition)e.nextElement());
				pos.getNode().removePropertyChangeListener(this);
			}
		}
				
		oView = view;
		for(Enumeration e = view.getPositions();e.hasMoreElements();) {
			pos = ((NodePosition)e.nextElement());
			pos.getNode().addPropertyChangeListener(this);
		}		
	}

	/**
	 * Returns the view that this view pane represents.
	 *
	 * @return com.compendium.core.datamodel.View, the represented view.
	 * @see com.compendium.core.datamodel.IView
	 */
	public View getView() {
		return oView;
	}

	/**
	 * Return the content dialog for the given node.
	 *
	 * @param NodeSummary node, the node whose content dialog to return.
	 * @return com.compendium.ui.dialogs.UINodecontentDialog, the content dialog for the given node.
	 */
	public UINodeContentDialog getContentDialog(String sNodeID) {
		if (contentDialogs.containsKey(sNodeID))
			return (UINodeContentDialog)contentDialogs.get((Object)sNodeID);
		return null;
	}

	/**
	 * Open the content dialog for the given node and select the Edit/Contents tab.
	 *
	 * @param NodePosition node, the node to open the content dialog for.
	 * @return com.compendium.ui.dialogs.UINodeContentDialog, the content dialog for the given node.
	 */
	public UINodeContentDialog showEditDialog(NodePosition node) {
		return showContentDialog(node, UINodeContentDialog.CONTENTS_TAB);
	}

	/**
	 * Open the content dialog for the given node and select the Properties tab.
	 *
	 * @param NodePosition node, the node to open the content dialog for.
	 * @return com.compendium.ui.dialogs.UINodeContentDialog, the content dialog for the given node.
	 */
	public UINodeContentDialog showPropertiesDialog(NodePosition node) {
		return showContentDialog(node, UINodeContentDialog.PROPERTIES_TAB);
	}

	/**
	 * Open the content dialog for the given node and select the View tab.
	 *
	 * @param NodePosition node, the node to open the content dialog for.
	 * @return com.compendium.ui.dialogs.UINodeContentDialog, the content dialog for the given node.
	 */
	public UINodeContentDialog showViewsDialog(NodePosition node) {
		return showContentDialog(node, UINodeContentDialog.VIEW_TAB);
	}

	/**
	 * Open the content dialog for the given node and select the given tab.
	 *
	 * @param NodePosition nodePos, the node to open the content dialog for.
	 * @param int tab, the tab on the dialog to select.
	 * @return com.compendium.ui.dialogs.UINodeContentDialog, the content dialog for the given node and tab.
	 */
	private UINodeContentDialog showContentDialog(NodePosition nodePos, int tab) {
		
		String sNodeID = nodePos.getNode().getId();
		if (contentDialogs.containsKey(sNodeID)) {
			UINodeContentDialog contentDialog = (UINodeContentDialog)contentDialogs.get((Object)sNodeID);
			if (contentDialog != null) {
				contentDialog.setVisible(true);
				contentDialog.requestFocus();
				return contentDialog;
			}
		}
				
		UINodeContentDialog contentDialog  = new UINodeContentDialog(ProjectCompendium.APP, getView(), nodePos, tab);
		contentDialogs.put(nodePos.getNode().getId(), contentDialog);
		contentDialog.setVisible(true);
		contentDialog.requestFocus();
		
		//Lakshmi (4/24/06) - if the contents dialog is opened set state as read in NodeUserState DB
   		int state = nodePos.getNode().getState();
   		if(state != ICoreConstants.READSTATE){
   			try {
   				nodePos.getNode().setState(ICoreConstants.READSTATE);
			} catch (SQLException e) {
				e.printStackTrace();
				System.out.println("Error: (UIList.showContentDialog) \n\n"+e.getMessage()); //$NON-NLS-1$
			} catch (ModelSessionException e) {
				e.printStackTrace();
				System.out.println("Error: (UIList.showContentDialog) \n\n"+e.getMessage()); //$NON-NLS-1$
			}
   		} 
		return contentDialog;
	}

	/**
	 * Return the current right-click popup menu else create one for the first node in the list and return that.
	 * @return com.compendium.popups.UINodePopupMenuForList, the appropriate popup menu.
	 */
	public UINodePopupMenuForList getPopupMenu() {

		// IF NO POPUP IS CREATED, CREATE A DEFAULT ONE - CURRENTLY ONLY USED FOR P2P
		if (viewPopup == null) {
			viewPopup = new UINodePopupMenuForList("Popup menu",listUI,	model.getNodePosition(0)); //$NON-NLS-1$
		}
		return viewPopup;
	}

	/**
	 * Return the current right-click popup menu else create one for the first node in the list and return that.
	 * @param listUI com.compendium.ui.plaf.ListUI, the associated list object.
	 * @param rowIndex, the row for the node to create the popup for.
	 * @param x, the x position of the activating mouse event.
	 * @param y, the y position for the activating mouse event.
	 * @return com.compendium.popups.UINodePopupMenuForList, the created popup menu.
	 */
	public UINodePopupMenuForList showPopupMenu(ListUI listUI, int rowIndex, int x, int y) {

		viewPopup = new UINodePopupMenuForList("Popup menu",listUI, model.getNodePosition(rowIndex)); //$NON-NLS-1$

		//get the active frame which will give the view to be searched
		UIViewFrame viewFrame = ProjectCompendium.APP.getCurrentFrame();

		Dimension dim = ProjectCompendium.APP.getScreenSize();
		int screenWidth = dim.width - 50; //to accomodate for the scrollbar
		int screenHeight = dim.height-100; //to accomodate for the menubar...

		int dtopX = ((Component)ProjectCompendium.APP).getLocationOnScreen().x;
		int dtopY = ((Component)ProjectCompendium.APP).getLocationOnScreen().y;

		Point point = viewFrame.getViewPosition();
		int realX = Math.abs(point.x - x);
		int realY = Math.abs(point.y - y);

		int endXCoordForPopUpMenu = realX + viewPopup.getWidth();
		int endYCoordForPopUpMenu = realY + viewPopup.getHeight();

		int offsetX = (screenWidth) - endXCoordForPopUpMenu;
		int offsetY = (screenHeight) - endYCoordForPopUpMenu;
		if(offsetX > 0)
			offsetX = 0;
		if(offsetY > 0)
			offsetY = 0;

		viewPopup.setCoordinates(realX+offsetX, realY+offsetY);
		viewPopup.show(viewFrame,realX+offsetX,realY+offsetY);

		return viewPopup;
	}


	/**
	 * Create the right-click popup menu for the list.
	 * @param x, the x position of the activating mouse event.
	 * @param y, the y position for the activating mouse event.
	 */
	public void showPopupMenuForList(int x, int y) {

		UIViewPopupMenuForList pop = new UIViewPopupMenuForList("View Popup menu", listUI); //$NON-NLS-1$

		JInternalFrame[] frames = ProjectCompendium.APP.getDesktop().getAllFrames();
		//get the active frame which will give the view to be searched
		UIViewFrame viewFrame = null;
		boolean frameFound = false; int i=0;
		while(!frameFound && i<frames.length) {
			viewFrame = (UIViewFrame)frames[i++];
			if (viewFrame.isSelected()) {
				frameFound = true;
			}
		}

		Dimension dim = ProjectCompendium.APP.getScreenSize();
		int screenWidth = dim.width - 50; //to accomodate for the scrollbar
		int screenHeight = dim.height-100; //to accomodate for the menubar...

		int dtopX = ((Component)ProjectCompendium.APP).getLocationOnScreen().x;
		int dtopY = ((Component)ProjectCompendium.APP).getLocationOnScreen().y;

		int realX = Math.abs(x);
		int realY = Math.abs(y);

		int endXCoordForPopUpMenu = realX + pop.getWidth();
		int endYCoordForPopUpMenu = realY + pop.getHeight();

		int offsetX = (screenWidth) - endXCoordForPopUpMenu;
		int offsetY = (screenHeight) - endYCoordForPopUpMenu;
		if(offsetX > 0)
			offsetX = 0;
		if(offsetY > 0)
			offsetY = 0;

		pop.setCoordinates(realX, realY);
		pop.show(viewFrame,realX+offsetX,realY+offsetY);
	}

	/**
	 * Process a TableModelEvent, when something has changed in the data.
	 * Refresh the table and repaint.
	 * @param tme, the associated TableModelEvent.
	 */
	public void tableChanged(TableModelEvent tme) {
		Object src = tme.getSource();
		if (src instanceof ListTableModel) {
			((ListTableModel)src).refreshTable();
			table.invalidate();
			table.repaint();
		}
		else if (src instanceof TableSorter) {
			((ListTableModel)((TableSorter)src).getModel()).refreshTable();
			table.invalidate();
			table.repaint();
		}
		oViewFrame.repaint();
	}
	
	/**
	 * Update the table after a change.
	 */
	public void updateTable() {
		table.setFont(ProjectCompendium.APP.currentDefaultFont);
		sorter.fireTableChanged(new TableModelEvent(table.getModel()));
		((UIListViewFrame)oViewFrame).updateCountLabel();
	}

	/**
	 * Validate the component in the table. Currently just calls <code>updateTable</code>
	 * @see #updateTable
	 */
	public void validateComponents() {
		updateTable();
	}

	/**
	 * Return the number of currently selected node in the list.
	 * @return int, the number of currently selected node in the list.
	 */
	public int getNumberOfSelectedNodes() {
		return table.getSelectedRowCount();
	}

	/**
	 * Return a list of all the currently selected nodes in the table.
	 *
	 * @return Enumeration, a list of all the currently selected nodes in the table.
	 */
	public Enumeration getSelectedNodes() {
		Vector selectedNodes = new Vector();
		int[] selectedIndexes = table.getSelectedRows();
		for (int i = 0; i < selectedIndexes.length; i++) {
			NodePosition np = model.getNodePosition(selectedIndexes[i]);
			selectedNodes.addElement(np);
		}
		return selectedNodes.elements();
	}

	/**
	 * Deselect all selected rows.
	 */
	public void deselectAll() {
		table.clearSelection();
	}

	/**
	 * Return if the given node is contain in the list table.
	 * @param np com.compendium.core.datamodel.NodePosition, the node to check.
	 * @return boolean, true if the list table contains the given node, else false.
	 */
	public boolean contains(NodePosition np) {
		return model.contains(np);
	}
	
	/**
	 * Return the number of nodes currently in this list view.
	 * @return int, the number of nodes currently in this list view.
	 */
	public int getNumberOfNodes() {
		return table.getRowCount();
	}
  	
	/**
	 * Return the node at the given index.
	 * @param index, the index of the node to return.
 	 * @return com.compendium.core.datamodel.NodePosition, the node at the given index, else null.
	 */
	public NodePosition getNodePosition(int index) {
		return model.getNodePosition(index);
	}			
 	
	/**
	 * Return the node for the given node id.
	 * @param sNodeID the id of the node to return.
 	 * @return com.compendium.core.datamodel.NodePosition, the node with the given id, else null if not found.
	 */
  	public NodePosition getNode(String sNodeID) {
		int rows = sorter.getRowCount();

		for (int i=0; i<rows; i++) {
		  	NodePosition nodePos = model.getNodePosition(i);
		  	if (sNodeID.equals(nodePos.getNode().getId())) {
				return nodePos;
		  	}
		}

	  	return null;
  	}
  	
  	/**
	 * Return the index of the given node. 
	 * @param node com.compendium.core.datamodel.NodeSummary, the node to return the index for.
	 * @return int, the index of the row position for the given node, else -1 if not found.
	 */
 	public int getIndexOf(NodeSummary node) {
 		return getIndexOf(node.getId());
  	}

 	/**
	 * Return the sorter level index of the node with the given node id.
	 * @param sNodeID, the id of the node to return the index for.
	 * @return int, the index of the row position for the given node, else -1 if not found.
	 */
  	public int getIndexOf(String sNodeID) {
		int rows = sorter.getRowCount();

		for (int i=0; i<rows; i++) {
		  	NodePosition nodePos = model.getNodePosition(i);
		  	if (sNodeID.equals(nodePos.getNode().getId())) {
				return i;
		  	}
		}

	  	return -1;
  	}

	/**
	 * Creates shortcut nodes for the nodes at the given indexes.
	 * @param indexList, the list of indexes for the nodes to create shortcuts for.
	 */
	public void createShortCutNodes(int[] indexList) {

 		try {

			for (int i = 0; i < indexList.length; i++) {
				NodePosition np = model.getNodePosition(indexList[i]);
				NodeSummary parentnode = np.getNode();
				//create a node to be added to view
				String author = ProjectCompendium.APP.getModel().getUserProfile().getUserName();
				String userID = ProjectCompendium.APP.getModel().getUserProfile().getId();
				String label = parentnode.getLabel();
				String detail = ""; //$NON-NLS-1$
				int nodeType = parentnode.getType() + ICoreConstants.PARENT_SHORTCUT_DISPLACEMENT;

				NodePosition nodePos = oView.addMemberNode(nodeType,		//int type
														  "",								//String xNodeType, //$NON-NLS-1$
														  "",								//String importedID, //$NON-NLS-1$
														  author,							//String author,
														  label,							//String label
														  detail,							//String detail
														  np.getXPos(),						//int x
														  (table.getRowCount() + i + 1)* 10 //int y														  
														  );
				NodeSummary node = nodePos.getNode();
				node.initialize(oView.getModel().getSession(), oView.getModel());
				node.setSource(parentnode.getSource(), parentnode.getImage(), sAuthor);

				//add the shortcut to the parent node list
				//node.addShortCutNode(node);							// MLB: Replaced these w/the following 2 lines
				// ((ShortCutNodeSummary)node).setReferredNode(node);	// as it was creating a shortcut that pointed to itself
				parentnode.addShortCutNode(node);
				((ShortCutNodeSummary)node).setReferredNode(parentnode);
			}
			((AbstractTableModel)table.getModel()).fireTableChanged(new TableModelEvent(table.getModel()));
		}
		catch(Exception e) {
			ProjectCompendium.APP.displayError("Exception:" + e.getMessage()); //$NON-NLS-1$
		}
		updateTable();
		return ;
	}
  	
	/**
	 * Set the selection mode of the node at the given index.
	 * @param index, the index of the node row to select.
	 * @param selectMode, the mode of selection for the node (DESELECTALL/MULTISELECT/SINGLESELECT).
	 */
  	public void selectNode(int index, int selectMode) {

		if (selectMode == ICoreConstants.DESELECTALL) {
		  	deselectAll();
		  	//Done through the selection event listener now 
			//ProjectCompendium.APP.setNodeOrLinkSelected(false);		  	
	  	}
		else if (selectMode == ICoreConstants.MULTISELECT) {
		  	table.addRowSelectionInterval(index, index);
		  	//Done through the selection event listener now 
			//ProjectCompendium.APP.setNodeOrLinkSelected(true);		  	
	  	}
		else if (selectMode == ICoreConstants.SINGLESELECT) {
		  	table.setRowSelectionInterval(index, index);
		  	//Done through the selection event listener now 
			//ProjectCompendium.APP.setNodeOrLinkSelected(true);		  	
	  	}
  	}  	
	
	/**
	 * Insert the given set of nodes into the list at the given index position.
	 * @param nps the array of com.compendium.core,datamodel.NodePosition to insert.
	 * @param index, the index position to insert the nodes at.
	 */
	public void insertNodes(NodePosition[] nps, int index) {
		model.insertNodes(nps, index);
		((UIListViewFrame)oViewFrame).updateCountLabel();
	}

	/**
	 * Insert the given node into the list at the given index position.
	 * @param np com.compendium.core,datamodel.NodePosition, the node to insert.
	 * @param index, the index position to insert the node at.
	 */
	public void insertNode(NodePosition np, int index) {
		model.insertNode(np, index);
		((UIListViewFrame)oViewFrame).updateCountLabel();
	}
		
	/**
	 * Delete all the currently selected nodes.
	 * @param edit, the PCEdit object to use to store changes for later undo/redo.
	 */
	public void deleteSelectedNodes(PCEdit edit) {
		int[] selectedRows = table.getSelectedRows();
		IModel imodel = ProjectCompendium.APP.getModel();

		for (int i = 0; i < selectedRows.length; i++) {

			String nodeId = (String)table.getValueAt(selectedRows[i], ListTableModel.ID_COLUMN);
			// IF NODE ALREADY DELETED, DON'T TRY AND DELETE CHILDREN AGAIN
			// NEED TO CATCH NEVERENDING LOOP WHEN NODE CONTAINS ITSELF SOMEWHERE IN CHILDREN TREE
			boolean wasDeleted = false;
			try {
				if (imodel.getNodeService().isMarkedForDeletion(imodel.getSession(), nodeId)) {
					wasDeleted = true;
				}
			}
			catch (SQLException ex) {
				ex.printStackTrace();
			}

			try {
				NodeSummary oNode = NodeSummary.getNodeSummary(nodeId);		// This section a bug fix from Michelle - patch to 1.5.2
                oView.removeMemberNode(oNode);
                boolean lastInstance = false;
                if(oNode.getViewCount() == 0 ) {
                      lastInstance = true;
           }

				
				// IF NODE IS A VIEW AND IF NODE WAS ACTUALLY LAST INSTANCE AND WAS DELETED, DELETE CHILDREN
				if (NodeSummary.getNodeSummary(nodeId) instanceof View && lastInstance && !wasDeleted) {
					View childView = (View)NodeSummary.getNodeSummary(nodeId);
					UIViewFrame childViewFrame = ProjectCompendium.APP.getViewFrame(childView, childView.getLabel());
					if (childViewFrame instanceof UIMapViewFrame)
						((UIMapViewFrame)childViewFrame).deleteChildren(childView);
					else
						((UIListViewFrame)childViewFrame).deleteChildren(childView);
					
					// delete from ProjectCompendium.APP opened frame list.
					ProjectCompendium.APP.removeViewFromHistory(childView);										
				}
			}
			catch(Exception ex) {
				ex.printStackTrace();
				System.out.println("Error: (UIList.deleteSelectedNodes) \n\n"+ex.getMessage()); //$NON-NLS-1$
			}

			if (edit != null) {
				edit.AddNodeToEdit(	 getNodePosition(selectedRows[i]), selectedRows[i]);
			}
		}

		model.deleteRows(selectedRows);
		selectNode(0, ICoreConstants.DESELECTALL);
		sorter.fireTableChanged(new TableModelEvent(table.getModel()));
		((UIListViewFrame)oViewFrame).updateCountLabel();
	}

	/**
	 * Delete the node at the given index position.
	 * @param index, the index position of the node to delete.
	 */
	public void deleteNode(int index) {
		int[] selectedRows = {sorter.getRealRow(index)};
		String nodeId = nodeId = (String)table.getValueAt(index, ListTableModel.ID_COLUMN);
		try {
			oView.removeMemberNode(NodeSummary.getNodeSummary(nodeId));
			//updateTable();			
		}
		catch(Exception ex) {
			ProjectCompendium.APP.displayError("Error: (UIList.deleteNode) "+LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE,"UIList.unableToDeleteNode")+"\n\n"+ex.getMessage()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}
	}
	
	/** Print the current table.*/
	public void print(PrintRequestAttributeSet aset) {
		try {
			table.print(JTable.PrintMode.NORMAL, null, null, true, aset, true);
		}
		catch(Exception ex) {
			System.out.println("printable exception: "+ex.getMessage()); //$NON-NLS-1$
		}
	}
	
	/** Set the view to be sorted by the Date field.  Used when opening the Inbox */
	public void sortByCreationDate() {
		sorter.sortByColumn(ListTableModel.CREATION_DATE_COLUMN, false);
	}

	/**
	 * Handles property change events.
	 * @param evt, the associated PropertyChangeEvent object.
	 */
	public void propertyChange(PropertyChangeEvent evt) {

	    String prop = evt.getPropertyName();

		Object source = evt.getSource();
	    Object newvalue = evt.getNewValue();

	    if (source instanceof NodeSummary) {
		    if (prop.equals(NodeSummary.LABEL_PROPERTY) || prop.equals(View.CHILDREN_PROPERTY)) {
				table.revalidate();
				table.repaint();		
		    }
		}
	    
	    if (source instanceof View) {
		    if (prop.equals(View.NODE_ADDED)) {
				// IF RECODRING or REPLAYING A MEETING, SENT A NODE ADDED EVENT
				if (ProjectCompendium.APP.oMeetingManager != null && ProjectCompendium.APP.oMeetingManager.captureEvents()) {
					NodePosition oNodePos = (NodePosition)newvalue;

					// IF NODE NOT ALREADY THERE, SEND EVENT
					NodePosition oNode = (NodePosition)getNode(oNodePos.getNode().getId());
					if (oNode == null) {
						ProjectCompendium.APP.oMeetingManager.addEvent(
							new MeetingEvent(ProjectCompendium.APP.oMeetingManager.getMeetingID(),
											 ProjectCompendium.APP.oMeetingManager.isReplay(),
											 MeetingEvent.NODE_ADDED_EVENT,
											 oNodePos));
					}
				}
			}
		    else if (prop.equals(View.NODE_TRANSCLUDED)) {
				// IF RECODRING or REPLAYING A MEETING, SENT A NODE TRANSCLUDED EVENT
				if (ProjectCompendium.APP.oMeetingManager != null && ProjectCompendium.APP.oMeetingManager.captureEvents())  {
					NodePosition oNodePos = (NodePosition)newvalue;

					// IF NODE NOT ALREADY THERE, SEND EVENT
					NodePosition oNode = (NodePosition)getNode(oNodePos.getNode().getId());
					if (oNode == null) {
						ProjectCompendium.APP.oMeetingManager.addEvent(
							new MeetingEvent(ProjectCompendium.APP.oMeetingManager.getMeetingID(),
											 ProjectCompendium.APP.oMeetingManager.isReplay(),
											 MeetingEvent.NODE_TRANSCLUDED_EVENT,
											 oNodePos));
					}
				}
			}
		    else if (prop.equals(View.NODE_REMOVED)) {
				// IF RECODRING or REPLAYING A MEETING, SENT A NODE REMOVED EVENT
				if (ProjectCompendium.APP.oMeetingManager != null && ProjectCompendium.APP.oMeetingManager.captureEvents()) {
					NodeSummary node = (NodeSummary)newvalue;						
					ProjectCompendium.APP.oMeetingManager.addEvent(
						new MeetingEvent(ProjectCompendium.APP.oMeetingManager.getMeetingID(),
										 ProjectCompendium.APP.oMeetingManager.isReplay(),
										 MeetingEvent.NODE_REMOVED_EVENT,
										 oView,
										 node));						
				}
			}
		}
	}	
		
	// LIST SELECTION LISTENER
	/**
	 * Need this to detect selection changes made with the keyboard arrows.
	 */
	public void valueChanged(ListSelectionEvent e) {		
		if (table.getSelectedRowCount() > 0) {
			ProjectCompendium.APP.setNodeOrLinkSelected(true);	
		} else {
			ProjectCompendium.APP.setNodeOrLinkSelected(false);			
		}
	}
	
  	//  TRANSFERABLE  	
   /**
     * Returns an array of DataFlavor objects indicating the flavors the data
     * can be provided in.
     * @return an array of data flavors in which this data can be transferred
     */
	//public DataFlavor[] getTransferDataFlavors() {
	//	return supportedFlavors;
	//}

    /**
     * Returns whether or not the specified data flavor is supported for
     * this object.
     * @param flavor the requested flavor for the data
     * @return boolean indicating whether or not the data flavor is supported
     */
	//public boolean isDataFlavorSupported(DataFlavor flavor) {
	//	return flavor.isMimeTypeEqual(DataFlavor.javaJVMLocalObjectMimeType+"; class=com.compendium.ui.UIList");
	//}

    /**
     * Returns an object which represents the data to be transferred.  The class
     * of the object returned is defined by the representation class of the flavor.
     *
     * @param flavor the requested flavor for the data
     * @see DataFlavor#getRepresentationClass
     * @exception IOException                if the data is no longer available in the requested flavor.
     * @exception UnsupportedFlavorException if the requested data flavor is not supported.
     */
	//public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
	//	if (flavor.isMimeTypeEqual(DataFlavor.javaJVMLocalObjectMimeType+"; class=com.compendium.ui.UIList"))
	//		return this;
	//	else return null;
	//}
	
	//	SOURCE
	
	//private int[] dragRows = null;
	
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
	    dragRows = null;
	    int action = e.getDragAction();	    
	    if (in instanceof MouseEvent) {
	    	dragRows = table.getSelectedRows();
 	    	if (action == DnDConstants.ACTION_MOVE) {
 				int sortColumn = ((TableSorter)(table.getModel())).getSelectedColumn();
 				if (sortColumn == -1) {
 					e.startDrag(DragSource.DefaultMoveDrop, this, this);
 				}
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
	/*public void dragOver(DragSourceDragEvent e) {
		int action = e.getUserAction();
		DragSourceContext context = e.getDragSourceContext();
		Point loc = e.getLocation();
		SwingUtilities.convertPointFromScreen(loc, table);		
		table.scrollRectToVisible(new Rectangle(loc.x-20, loc.y-20, 40, 40));								
	}*/

    /**
     * Called when the user has modified the drop gesture.
     * This method is invoked when the state of the input
     * device(s) that the user is interacting with changes.
     * Such devices are typically the mouse buttons or keyboard
     * modifiers that the user is interacting with.
    *
     * @param e the <code>DragSourceDragEvent</code>
     */
	//public void dropActionChanged(DragSourceDragEvent e) {}	
	
//	 TARGET DROP METHODS

    /**
     * Called if the user has modified
     * the current drop gesture.
     * <P>
	 * THIS METHOD DOES NOTHING HERE.
     * @param e the <code>DropTargetDragEvent</code>
     */
	public void dropActionChanged(DropTargetDragEvent e) {}

    /**
     * Called while a drag operation is ongoing, when the mouse pointer enters
     * the operable part of the drop site for the <code>DropTarget</code>
     * registered with this listener. Accepts COPY_ACTION and COPY_OR_MOVE drags.
     *
     * @param e the <code>DropTargetDragEvent</code>
     */
	public void dragEnter(DropTargetDragEvent e) {
		e.acceptDrag(DnDConstants.ACTION_COPY_OR_MOVE);
	}

    /**
     * Called while a drag operation is ongoing, when the mouse pointer has
     * exited the operable part of the drop site for the
     * <code>DropTarget</code> registered with this listener.
	 * THIS METHOD DOES NOTHING HERE.
     *
     * @param e the <code>DropTargetEvent</code>
     */
	public void dragExit(DropTargetEvent e) {}

    /**
     * Called when a drag operation is ongoing, while the mouse pointer is still
     * over the operable part of the drop site for the <code>DropTarget</code>
     * registered with this listener.
	 * THIS METHOD DOES NOTHING HERE.
     *
     * @param e the <code>DropTargetDragEvent</code>
     */
	public void dragOver(DropTargetDragEvent e) {}

    /**
     * Called when the drag operation has terminated with a drop on
     * the operable part of the drop site for the <code>DropTarget</code>
     * registered with this listener.
     * <p>
     * This method is responsible for undertaking
     * the transfer of the data associated with the
     * gesture. The <code>DropTargetDropEvent</code>
     * provides a means to obtain a <code>Transferable</code>
     * object that represents the data object(s) to
     * be transfered.<P>
     * From this method, the <code>DropTargetListener</code>
     * shall accept or reject the drop via the
     * acceptDrop(int dropAction) or rejectDrop() methods of the
     * <code>DropTargetDropEvent</code> parameter.
     * <P>
     * Subsequent to acceptDrop(), but not before,
     * <code>DropTargetDropEvent</code>'s getTransferable()
     * method may be invoked, and data transfer may be
     * performed via the returned <code>Transferable</code>'s
     * getTransferData() method.
     * <P>
     * At the completion of a drop, an implementation
     * of this method is required to signal the success/failure
     * of the drop by passing an appropriate
     * <code>boolean</code> to the <code>DropTargetDropEvent</code>'s
     * dropComplete(boolean success) method.
     * <P>
	 * This method accept or declines the drop of an external file, directory or text block.
     * <P>
     * @param e the <code>DropTargetDropEvent</code>
     */
	public void drop(DropTargetDropEvent e) {
		try {
       		final Transferable tr = e.getTransferable();
			final UIList list = this;
			final DropTargetDropEvent evt = e;

		    /*if (tr.isDataFlavorSupported(new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType+"; class=com.compendium.ui.UIList", null))) {
				// MOVE ROWS AROUND THE TABLE
				JTable table = getList();
				table.setCursor(Cursor.getDefaultCursor());
				int index = table.rowAtPoint(e.getLocation());
				
				if (index != -1) {
					NodePosition np = getNodePosition(index);
					NodePosition[] npList = new NodePosition[dragRows.length];
					for (int i = 0; i < dragRows.length; i++) {
						npList[i] = getNodePosition(dragRows[i]);
						if (index == dragRows[i]) {
							ProjectCompendium.APP.setStatus("");
							return;
						}
					}

					// DELETE NODES TO MOVE
					deleteSelectedNodes(null);
					//for (int i = 0; i < dragRows.length; i++) {
					//	deleteNode(dragRows[i]);
					//}					
					deselectAll();

					// Need to find the new index of the insert point after deleting the nodes to move.
					//index = getIndexOf(np.getNode());
					
					NodePosition pos = null;
					String id = null;
					for (int i = 0; i < npList.length; i++) {
						pos = npList[i];
						id = pos.getNode().getId();
						try {
							boolean restored = ProjectCompendium.APP.getModel().getNodeService().restoreNode(ProjectCompendium.APP.getModel().getSession(), id);
							if (restored) {
								NodePosition oriPos = ProjectCompendium.APP.getModel().getNodeService().restoreNodeView( ProjectCompendium.APP.getModel().getSession(), id, (getView()).getId() );
							}

							// INSERT NODES MOVED
							getView().addMemberNode(pos);
							getView().setNodePosition(id, new Point(pos.getXPos(),(index + i + 1) * 10));

							//insertNode(pos, index + i);
							//selectNode(index + i,ICoreConstants.MULTISELECT);
						}
						catch (Exception ex) {
							ProjectCompendium.APP.displayError("Exception: (ListUI.mouseReleased) \n" + ex.getMessage());
						}
					}
					
					//updateTable();
					//unsort();
				}		    	
			} else */if (tr.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {

                e.acceptDrop (DnDConstants.ACTION_COPY_OR_MOVE);
				final java.util.List fileList = (java.util.List)tr.getTransferData(DataFlavor.javaFileListFlavor);

				// new Thread required for Mac bug caused when code calls UIUtilities.checkCopyLinkedFile
				// and tries to open a JOptionPane popup.
				Thread thread = new Thread("UIList-FileListFlvor") { //$NON-NLS-1$
					public void run() {

						Iterator iterator = fileList.iterator();

						int nY = (listUI.getUIList().getNumberOfNodes() + 1) * 10;
						int nX = 0;

						int nType = ICoreConstants.REFERENCE;
						String fileName = ""; //$NON-NLS-1$

						while (iterator.hasNext()) {

							File file = (File)iterator.next();

							NodePosition pos = null;
							NodePosition nodePos = null;

							// IF IS A DIRECTORY - CREATE A MAP AND FILL IT WITH REFERENCE NODES
							if (file.isDirectory()) {
								pos = listUI.createNode(ICoreConstants.MAPVIEW,
														"", //$NON-NLS-1$
														sAuthor,
														file.getName(),
														"", //$NON-NLS-1$
														nX,
														nY
														);

								updateTable();
								selectNode(getNumberOfNodes() - 1, ICoreConstants.MULTISELECT);

								int x = 10;
								int y = 10;

								File[] files = file.listFiles();
								for (int i=0; i<files.length; i++) {
									try {
										File innerFile = files[i];
										if (! (innerFile.getName()).startsWith(".") || ProjectCompendium.isWindows) { //$NON-NLS-1$
											LinkedFile lf = UIUtilities.copyDnDFile(innerFile,null);
											String name = innerFile.getName();
											String path = innerFile.getPath();
											String source = path;
											if (lf != null)
											{
												name = lf.getName();
												source = lf.getSourcePath();
											}

											//File oldFile = innerFile;
											//file = UIUtilities.checkCopyLinkedFile(innerFile);
											//if (innerFile == null)
											//	innerFile = oldFile;

											nodePos = ((View)pos.getNode()).addMemberNode(nType, "", "", sAuthor, innerFile.getName(), "", x, y); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
											if (UIImages.isImage(innerFile.getPath()))
												nodePos.getNode().setSource("", source, sAuthor); //$NON-NLS-1$
											else
												nodePos.getNode().setSource(source, "", sAuthor); //$NON-NLS-1$
											y += 50;
										}
									}
									catch(Exception ex) {
										System.out.println("Error: (UIList.drop)\n\n"+ex.getMessage()); //$NON-NLS-1$
									}
								}

								nY = (listUI.getUIList().getNumberOfNodes() + 1) * 10;
							}
							else {
								fileName = file.getName();
								if (!fileName.startsWith(".") || ProjectCompendium.isWindows) { //$NON-NLS-1$

									fileName = fileName.toLowerCase();

									if ((fileName.endsWith(".xml") || fileName.endsWith(".zip")) && file.exists()) { //$NON-NLS-1$ //$NON-NLS-2$
										UIDropFileDialog dropDialog = new UIDropFileDialog(ProjectCompendium.APP, list, file, nX, nY);
										dropDialog.setVisible(true);
										nY = (listUI.getUIList().getNumberOfNodes() + 1) * 10;
									}
									else {
										LinkedFile lf = UIUtilities.copyDnDFile(file,null);
										String name = file.getName();
										String path = file.getPath();
										String source = path;
										if (lf != null)
										{
											name = lf.getName();
											source = lf.getSourcePath();
										}
										
										//file = UIUtilities.checkCopyLinkedFile(file);
										//File oldfile = file;
										//if (file == null)
										//	file = oldfile;

										pos = listUI.createNode(ICoreConstants.REFERENCE,
																"", //$NON-NLS-1$
																sAuthor,
																name,
																"", //$NON-NLS-1$
																nX,
																nY
																);

										try {
											if (UIImages.isImage(path))
												pos.getNode().setSource("", source, sAuthor); //$NON-NLS-1$
											else {
												pos.getNode().setSource(source, "", sAuthor); //$NON-NLS-1$
											}
										}
										catch(Exception ex) {
											System.out.println("Error: (UIList.drop-2)\n\n"+ex.getMessage()); //$NON-NLS-1$
										}

										updateTable();
										selectNode(getNumberOfNodes() - 1, ICoreConstants.MULTISELECT);

										nY = (listUI.getUIList().getNumberOfNodes() + 1) * 10;
									}
								}
							}
						}

						evt.getDropTargetContext().dropComplete(true);
					}
				};
				thread.start();
        	}
  			else if (tr.isDataFlavorSupported(DataFlavor.stringFlavor)) {

				e.acceptDrop(DnDConstants.ACTION_COPY);
				final String dropString = (String)tr.getTransferData(DataFlavor.stringFlavor);

				// new Thread required for Mac bug caused when code calls UIUtilities.checkCopyLinkedFile
				// and tries to open a JOptionPane popup.
				Thread thread = new Thread("UIViewPane.drop-StringFlavor") { //$NON-NLS-1$
					public void run() {

						String s = dropString;
						int nY = (listUI.getUIList().getNumberOfNodes() + 1) * 10;
						int nX = 0;

						/*if (s.startsWith("memetic-replay")) {
							ProjectCompendium.APP.oMeetingManager = new MeetingManager(MeetingManager.REPLAY);
							ProjectCompendium.APP.oMeetingManager.processAsMeetingReplay(s);
						}
						else if (s.startsWith("memetic-index")) {
							if (ProjectCompendium.APP.oMeetingManager == null) {
								ProjectCompendium.APP.displayError("You are not currently replaying a Meeting");
								return;
							}
							else {
								ProjectCompendium.APP.oMeetingManager.processAsMeetingReplayIndex(s, nX, nY);
							}
						}*/

						try {
							int type = new Integer(s).intValue();
							createNode(type, nX, nY);
							evt.getDropTargetContext().dropComplete(true);
						}
						catch(NumberFormatException io) {

							if (UINode.isReferenceNode(s)) {

								File newFile = new File(s);
								String fileName = newFile.getName();
								fileName = fileName.toLowerCase();
								String sFullPath = UIUtilities.sGetLinkedFilesLocation();								
								String sFilePath = sFullPath;
								File directory = new File(sFilePath);
								if (ProjectCompendium.isMac)
									sFilePath = directory.getAbsolutePath()+ProjectCompendium.sFS;

								String sActualFilePath = ""; //$NON-NLS-1$
								try {
									sActualFilePath = UIImages.loadWebImageToLinkedFiles(s, fileName, sFilePath);
								}
								catch(Exception exp) {}

								if (!sActualFilePath.equals("")) { //$NON-NLS-1$
									File temp = new File(sActualFilePath);
									NodePosition nodePos = listUI.createNode(ICoreConstants.REFERENCE,
																			 "", //$NON-NLS-1$
																			 ProjectCompendium.APP.getModel().getUserProfile().getUserName(),
																			 temp.getName(),
																			 "", //$NON-NLS-1$
																			 nX,
																			 nY
																			 );

									try {
										nodePos.getNode().setSource("", sActualFilePath, sAuthor); //$NON-NLS-1$
									}
									catch(Exception ex) {
										System.out.println("error in UIList.drop-3b) \n\n"+ex.getMessage()); //$NON-NLS-1$
									}

									updateTable();
									selectNode(getNumberOfNodes() - 1, ICoreConstants.MULTISELECT);
								}
								else {
								    LinkedFile lf = UIUtilities.copyDnDFile(newFile, null);
								    String name = newFile.getName();
								    String path = newFile.getPath();
								    String source = path;
									if (lf != null)
									{
										name = lf.getName();
										source = lf.getSourcePath();
									}

									NodePosition nodePos = listUI.createNode(ICoreConstants.REFERENCE,
																			 "", //$NON-NLS-1$
																			 sAuthor,
																			 source,
																			 "", //$NON-NLS-1$
																			 nX,
																			 nY
																			 );

									try {
										if (UIImages.isImage(path))
											nodePos.getNode().setSource("", source, sAuthor); //$NON-NLS-1$
										else {
											nodePos.getNode().setSource(source, "", sAuthor); //$NON-NLS-1$
										}
									}
									catch(Exception ex) {
										System.out.println("Error: (UIList.drop-3)\n\n"+ex.getMessage()); //$NON-NLS-1$
									}

									updateTable();																												
									selectNode(getNumberOfNodes() - 1, ICoreConstants.MULTISELECT);
								}

								evt.getDropTargetContext().dropComplete(true);
							}
							else {
								UIDropSelectionDialog dropDialog = new UIDropSelectionDialog(ProjectCompendium.APP, list, s, nX, nY);
								dropDialog.setVisible(true);
								evt.getDropTargetContext().dropComplete(true);
							}
						}
					}
				};
				thread.start();
			}
			else {
				e.rejectDrop();
			}
		}
		catch(IOException io) {
			e.rejectDrop();
		}
		catch(UnsupportedFlavorException ufe) {
			e.rejectDrop();
		}
	}	
}
