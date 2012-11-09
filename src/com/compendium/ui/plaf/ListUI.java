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

package com.compendium.ui.plaf;

import java.io.File;
import java.sql.*;
import java.util.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.datatransfer.*;

import javax.swing.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;

import com.compendium.io.xml.*;
import com.compendium.io.questmap.*;
import com.compendium.meeting.MeetingEvent;
import com.compendium.meeting.MeetingManager;

import com.compendium.ui.*;
import com.compendium.ui.dialogs.UIHintDialog;
import com.compendium.ui.edits.*;

import com.compendium.LanguageProperties;
import com.compendium.ProjectCompendium;
import com.compendium.core.datamodel.*;
import com.compendium.core.datamodel.services.*;
import com.compendium.core.ICoreConstants;


/**
 * The UI class for the UIList Component
 *
 * @author	Mohammed Sajid Ali /  Michelle Bachler
 */
public	class ListUI
				extends BasicTableUI
				implements MouseListener, MouseMotionListener, KeyListener,
							ICoreConstants, IUIConstants, ClipboardOwner {


	/** The minimum size for the list view.*/
	private static Dimension minSize	= new Dimension(0,0);

	/** The maximum size for the list view.*/
	private static Dimension maxSize	= new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);

	/** Component that we're going to be drawing into. */
  	protected JTable							list;

  	/** Set to true while keyPressed is active. */
  	protected boolean								bIsKeyDown;

  	/** The MouseListener registered for this list.*/
	private		MouseListener						oMouseListener;

  	/** The MouseMotionListener registered for this list.*/
	private		MouseMotionListener					oMouseMotionListener;

  	/** The KeyListener registered for this list.*/
	private		KeyListener							oKeyListener;

	/** _x & _y are the mousePressed location in absolute coordinate system.*/
	private		int											_x, _y;

	/** Location of the mouse at the time a key was pressed.*/
	public		Point								ptLocationKeyPress;

	/** Location of the mouse when it was clicked.*/
	private		Point								ptLocationMouseClicked = new Point(0,0);

	/** Start point in view pane's coordinate system.*/
	private		Point								ptStart;

	/** Previous end point in dragging.*/
	private		Point								ptPrev;

	/** Defines whether dragging is started with right mouse button.*/
	private		boolean								bDragging = false;

	/** Defines whether mouse clicked (used when objects are copied to the clicked place).*/
	private		boolean								bClicked = false;

	/** Defines whether the view paen is being scrolled by the user by mouse dragging.*/
	private		boolean								bScrolling = false;

	/** Defines whether the view pane has been exited by the mouse.*/
	private		boolean								bMouseExited = false;

	/** Is a copy in progress?*/
	private		boolean								bCopyToClipboard = false;

	/** Is a cut in progress?*/
	private		boolean								bCutToClipboard = false;

	/** The UIList object that this is the ui for.*/
	private UIList uiList;

	/** Stores the row information for the selected rows during a drag operation.*/
	private int[] selectedRowsWhileDragging;

	/** Used during Questmap and XML imports.*/
	private boolean isSmartImport = false;

	/** The shortcut key for the current platfrom.*/
	private int shortcutKey;


	/**
	 * Constructor. Just calls super.
	 */
  	public ListUI() {
		super();
	}

	/**
	 * Constructor. Installs the default and listeners.
	 * @param c, the component this is the ui for.
	 * @param listView com.compendium.ui.UIList, the UIList object this is the ui for.
	 */
	public ListUI(JComponent c, UIList listView) {
		super();

		uiList = listView;
		list = (JTable)c;
		shortcutKey = ProjectCompendium.APP.shortcutKey;

		installDefaults(c);
		installListeners(c);
  	}

	/**
	 * Create a new ListUI instance.
	 * @param c, the component this is the ui for - NOT REALLY USED AT PRESENT HERE.
	 */
	public static ComponentUI createUI(JComponent c) {
		return new ListUI();
  	}

	/***** USER INTERFACE INITIALIZATION METHODS *****/

	/**
	 * Run any install instructions for installing this UI.
	 * @param c, the component this is the ui for.
	 */
 	 public void installUI(JComponent c)   {
		super.installUI(c);
		list = (JTable)c;
		installDefaults(c);
		installListeners(c);
  	}

	/**
	 * Install any default - Just sets the background color at present.
	 * @param c, the component to uninstall the listeners for.
	 */
  	protected void installDefaults(JComponent c) {
		if (c.getBackground() == null || c.getBackground() instanceof UIResource) {
	      	c.setBackground(Color.white);
		}
  	}

	/**
	 * Install any Listener classes required by this UI.
	 * @param c, the component to install the listeners for.
	 */
	protected void installListeners(JComponent c) {
		if ( (oMouseListener = createMouseListener( c )) != null ) {
	   		c.addMouseListener( oMouseListener );
		}
		if ( (oMouseMotionListener = createMouseMotionListener( c )) != null ) {
	    	c.addMouseMotionListener( oMouseMotionListener );
		}
		if ( (oKeyListener = createKeyListener( c )) != null ) {
			c.addKeyListener( oKeyListener );
		}
	}


	/**
	 * Just returns this class as the MouseListener.
	 * @param c, the component to create the MouseLisener for.
	 * @return MouseListener, the listener to use.
	 */
  	protected MouseListener createMouseListener( JComponent c ) {
		return this;
  	}

	/**
	 * Just returns this class as the MouseMotionListener.
	 * @param c, the component to create the MouseMotionLisener for.
	 * @return MouseMotionListener, the listener to use.
	 */
  	protected MouseMotionListener createMouseMotionListener( JComponent c ) {
		return this;
  	}

	/**
	 * Just returns this class as the KeyListener.
	 * @param c, the component to create the KeyLisener for.
	 * @return KeyListener, the listener to use.
	 */
  	protected KeyListener createKeyListener(JComponent c) {
		return this;
  	}

	/**
	 * Run any uninstall instructions for uninstalling this UI.
	 * @param c, the component this is the ui to uninstall for.
	 */
  	public void uninstallUI(JComponent c) {
		uninstallListeners(c);
	    uninstallDefaults(c);

		oMouseListener = null;
		oKeyListener = null;
		oMouseMotionListener = null;
  	}

	/**
	 * Uninstall any default - CURRENTLY DOES NOTHING.
	 * @param c, the component to uninstall the listeners for.
	 */
  	protected void uninstallDefaults(JComponent c) {} // uninstallDefaults

	/**
	 * Uninstall any Listener classes used by this UI.
	 * @param c, the component to uninstall the listeners for.
	 */
	protected void uninstallListeners( JComponent c ) {
		if ( oKeyListener!= null ) {
	    	c.removeKeyListener( oKeyListener );
		}
		if ( oMouseMotionListener!= null ) {
	    	c.removeMouseMotionListener( oMouseMotionListener );
		}
		if ( oMouseListener!= null ) {
	    	c.removeMouseListener( oMouseListener );
		}
	}

	/***** PAINT METHODS *****/

	/**
	 * Just calls super.paint
	 *
	 * @param g, the Graphics object for this pain method to use.
	 * @param c, the component to paint.
	 */
  	public void paint(Graphics g, JComponent c) {
		super.paint(g, c);
	}

	/**
	 * Return the preferred size of this component - currently returns null.
	 * @param c, the component to return the preferred size for.
	 * @return Rectangle, the preferred size of this component - currently null.
	 */
  	public Dimension getPreferredSize(JComponent c) {
		return null;
	}

	/**
	 * Return the minimum size of this component.
	 * @param c, the component to return the minimum size for.
	 * @return Rectangle, the minimum size of this component.
	 */
  	public Dimension getMinimumSize(JComponent c) {
		return minSize;
	}

	/**
	 * Return the maximum size of this component.
	 * @param c, the component to return the maximum size for.
	 * @return Rectangle, the maximum size of this component.
	 */
	public Dimension getMaximumSize(JComponent c){
		return maxSize;
  	}

	/**
	 * Return the UIList object that this is the ui for.
	 * @return com.compendium.ui.UIList, the UIList object that this is the ui for.
	 */
	public UIList getUIList() {
		return uiList;
	}

	/***** EVENT HANDLING METHODS *****/

	/**
	 * Update the local and global mouse position information.
	 * @param Point p, the mouse position.
	 */
	private void updateMousePosition( Point p ) {
		_x = p.x;
		_y = p.y;

		Point point = new Point(p.x, p.y);
		SwingUtilities.convertPointToScreen(point, (Component)list);
		ProjectCompendium.APP._x = point.x;
		ProjectCompendium.APP._y = point.y;
	}

  	/**
   	 * Invoked when the mouse has been clicked on a component.
	 * @param e, the associated MouseEvent.
   	 */
  	public void mouseClicked(MouseEvent e) {

	  	bClicked = true;
	  	Point p = SwingUtilities.convertPoint((Component)e.getSource(), e.getX(), e.getY(), (Component)list);
	  	ptLocationMouseClicked.x = p.x;
	 	ptLocationMouseClicked.y = p.y;

		boolean isLeftMouse = SwingUtilities.isLeftMouseButton(e);
		boolean isRightMouse = SwingUtilities.isRightMouseButton(e);		
		int clickCount = e.getClickCount();
		if(isRightMouse || (isLeftMouse && ProjectCompendium.isMac && (e.getModifiers() & MouseEvent.CTRL_MASK) != 0)) {
			if(clickCount == 1) {
				int rowIndex = list.rowAtPoint(ptLocationMouseClicked);
				if (rowIndex != -1) {
					if ((e.getModifiers() & MouseEvent.SHIFT_MASK) != 0) {
						uiList.selectNode(rowIndex, ICoreConstants.MULTISELECT);
					} else {
						uiList.selectNode(rowIndex, ICoreConstants.SINGLESELECT);
					}
					uiList.showPopupMenu(this, rowIndex, e.getX(), e.getY());
				}
			}
		} else if (isLeftMouse) {
			int rowIndex = list.rowAtPoint(ptLocationMouseClicked);
			int colIndex = list.columnAtPoint(ptLocationMouseClicked);
			if (clickCount == 1) {
				if (rowIndex != -1) {
					NodePosition pos =uiList.getNodePosition(rowIndex);
					NodeSummary node = pos.getNode();
					if ((node.getDetail().length() > 0) && (colIndex == ListTableModel.DETAIL_COLUMN)) {		// Single click over node detail indicator
						uiList.showEditDialog(pos);
					} else {
						uiList.selectNode(rowIndex, ICoreConstants.MULTISELECT);
					}
				} else {
					uiList.hideHint();
				}
			} else if(clickCount == 2) {
				if (rowIndex != -1) {
					NodePosition pos =uiList.getNodePosition(rowIndex);
					NodeSummary node = pos.getNode();
					if ((node.getDetail().length() > 0) && (colIndex == ListTableModel.DETAIL_COLUMN)) {
						return;																					// Eat it since the single click handled this
					} else {
						openNode(node);
					}
				}
				else {
					uiList.getViewFrame().showEditDialog();
				}
			}
		}
  	}
  	
	/**
	 * Open this node depending on type.
	 * If a map/list node, open the view.
	 * If a reference node, open any associated reference in an external application.
	 * If any other node, open the ContentDialog for this node.
	 * @param oNode the node to open.
	 */
	private void openNode(NodeSummary oNode) {

		int type = oNode.getType();
		String sNodeID = oNode.getId();		
		if ( View.isViewType(type) ||
			View.isShortcutViewType(type))
		{
			ProjectCompendium.APP.getAudioPlayer().playAudio(UIAudio.ABOUT_ACTION);

			View view = null;
			if( View.isShortcutViewType(type)) {
				view = (View)(((ShortCutNodeSummary)oNode).getReferredNode());
			}
			else {
				view = (View)oNode;
			}

			UIViewFrame frame = ProjectCompendium.APP.addViewToDesktop(view, oNode.getLabel());			
			frame.setNavigationHistory(uiList.getViewFrame().getChildNavigationHistory());
		}
		else if (type == ICoreConstants.REFERENCE || type == ICoreConstants.REFERENCE_SHORTCUT) {
			try {
				oNode.setState(ICoreConstants.READSTATE);		// Mark the node read (doesn't happen elsewhere)
			}
			catch (SQLException ex) {}
			catch (ModelSessionException ex) {};
			String path = oNode.getSource();
			if (path == null || path.equals("")) { //$NON-NLS-1$
				uiList.showEditDialog(uiList.getNode(sNodeID));
			} else if (path.startsWith(ICoreConstants.sINTERNAL_REFERENCE)) {
				path = path.substring(ICoreConstants.sINTERNAL_REFERENCE.length());
				int ind = path.indexOf("/"); //$NON-NLS-1$
				if (ind != -1) {
					String sGoToViewID = path.substring(0, ind);
					String sGoToNodeID = path.substring(ind+1);			
					UIUtilities.jumpToNode(sGoToViewID, sGoToNodeID, 
							uiList.getViewFrame().getChildNavigationHistory());
				}
			} else if (path.startsWith("http:") || path.startsWith("https:") || path.startsWith("www.")) { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				if (ExecuteControl.launch( path ) == null) {
					uiList.showEditDialog(uiList.getNode(sNodeID));
				}
				else {
					// IF WE ARE RECORDING A MEETING, RECORD A REFERENCE LAUNCHED EVENT.
					if (ProjectCompendium.APP.oMeetingManager != null && ProjectCompendium.APP.oMeetingManager.captureEvents()
							&& (ProjectCompendium.APP.oMeetingManager.getMeetingType() == MeetingManager.RECORDING)) {

						ProjectCompendium.APP.oMeetingManager.addEvent(
							new MeetingEvent(ProjectCompendium.APP.oMeetingManager.getMeetingID(),
											 ProjectCompendium.APP.oMeetingManager.isReplay(),
											 MeetingEvent.REFERENCE_LAUNCHED_EVENT,
											 uiList.getViewFrame().getView(),
											 oNode));
					}
				}
			}
			else {
				File file = new File(path);
				String sPath = path;
				if (file.exists()) {
					sPath = file.getAbsolutePath();
				}
				// If the reference is not a file, just pass the path as is, as it is probably a special type of url.
				if (ExecuteControl.launch( sPath ) == null)
					uiList.showEditDialog(uiList.getNode(sNodeID));
				else {
					// IF WE ARE RECORDING A MEETING, RECORD A REFERENCE LAUNCHED EVENT.
					if (ProjectCompendium.APP.oMeetingManager != null && ProjectCompendium.APP.oMeetingManager.captureEvents()
							&& (ProjectCompendium.APP.oMeetingManager.getMeetingType() == MeetingManager.RECORDING)) {

						ProjectCompendium.APP.oMeetingManager.addEvent(
							new MeetingEvent(ProjectCompendium.APP.oMeetingManager.getMeetingID(),
											 ProjectCompendium.APP.oMeetingManager.isReplay(),
											 MeetingEvent.REFERENCE_LAUNCHED_EVENT,
											 uiList.getViewFrame().getView(),
											 oNode));
					}
				}
			}
		}
		else {
			uiList.showEditDialog(uiList.getNode(sNodeID));
		}
	}

  	/**
   	 * Invoked when a mouse button has been pressed on a component.
	 * @param e, the associated MouseEvent.
   	 */
  	public void mousePressed(MouseEvent e) {
	  	Point p = SwingUtilities.convertPoint((Component)e.getSource(), e.getX(), e.getY(), (Component)list);
		updateMousePosition(p);

	 	bClicked = false;
		boolean isLeftMouse = SwingUtilities.isLeftMouseButton(e);
		if (isLeftMouse) {
			//int sortColumn = ((TableSorter)(list.getModel())).getSelectedColumn();
			//if (sortColumn == -1) {
			//	selectedRowsWhileDragging = list.getSelectedRows();
			//}
		}
	}

	/**
	 * Invoked when a mouse is dragged in a component.
	 * @param e, the associated MouseEvent.
	 */
	public void mouseDragged(MouseEvent e) {
		/*boolean isLeftMouse = SwingUtilities.isLeftMouseButton(e);
		if (isLeftMouse) {
			//int sortColumn = ((TableSorter)(list.getModel())).getSelectedColumn();
			//if (sortColumn == -1) {
				bDragging = true;
	
				uiList.getList().setCursor(new Cursor(Cursor.MOVE_CURSOR));
				uiList.deselectAll();
	
				for (int i= 0; i < selectedRowsWhileDragging.length; i++) {
					list.addRowSelectionInterval(selectedRowsWhileDragging[i], selectedRowsWhileDragging[i]);
			//	}
			//}
		}*/
	}
		
  	/**
   	 * Invoked when a mouse button has been released on a component.
	 * @param e, the associated MouseEvent.
   	 */
  	public void mouseReleased(MouseEvent e) {

		boolean isLeftMouse = SwingUtilities.isLeftMouseButton(e);
		if(bDragging && isLeftMouse) {

			uiList.getList().setCursor(Cursor.getDefaultCursor());
			
			int index = list.rowAtPoint(e.getPoint());

			if (index != -1) {

				NodePosition np = uiList.getNodePosition(index);
				NodePosition[] npList = new NodePosition[selectedRowsWhileDragging.length];

				for (int i = 0; i < selectedRowsWhileDragging.length; i++) {

					npList[i] = uiList.getNodePosition(selectedRowsWhileDragging[i]);

					if (index == selectedRowsWhileDragging[i]) {
						ProjectCompendium.APP.setStatus(""); //$NON-NLS-1$
						bDragging = false;
						//bScrolling = false;
						ptStart = null;
						ptPrev = null;
						return;
					}
				}

				// DELETE NODES TO MOVE
				uiList.deleteSelectedNodes(null);
				uiList.deselectAll();

				NodePosition pos = null;
				String id = null;

				for (int i = 0; i < npList.length; i++) {

					pos = npList[i];
					id = pos.getNode().getId();

					try {
						boolean restored = ProjectCompendium.APP.getModel().getNodeService().restoreNode(ProjectCompendium.APP.getModel().getSession(), id);
						if (restored) {
							NodePosition oriPos = ProjectCompendium.APP.getModel().getNodeService().restoreNodeView( ProjectCompendium.APP.getModel().getSession(), id, (uiList.getView()).getId() );
						}

						// INSERT NODES MOVED
						uiList.getView().addMemberNode(pos);
						uiList.getView().setNodePosition(id, new Point(pos.getXPos(),(index + i + 1) * 10));

						uiList.insertNode(pos, index + i);
						uiList.selectNode(index + i,ICoreConstants.MULTISELECT);
					}
					catch (Exception ex) {
						ProjectCompendium.APP.displayError("Error: (ListUI.mouseReleased) \n\n" + ex.getLocalizedMessage()); //$NON-NLS-1$
					}
				}
			}
		}

		ProjectCompendium.APP.setStatus(""); //$NON-NLS-1$

		//reset the flags and points
		bDragging = false;
		//bScrolling = false;
		ptStart = null;
		ptPrev = null;
	  }


  	/**
   	 * Invoked when the mouse enters a component.
	 * @param e, the associated MouseEvent.
   	 */
 	public void mouseEntered(MouseEvent e) {
	  	Point p = SwingUtilities.convertPoint((Component)e.getSource(), e.getX(), e.getY(), (Component)list);
		updateMousePosition(p);
	}

  	/**
   	 * Invoked when the mouse exits a component.
	 * @param e, the associated MouseEvent.
  	 */
  	public void mouseExited(MouseEvent e) {
	  	Point p = SwingUtilities.convertPoint((Component)e.getSource(), e.getX(), e.getY(), (Component)list);
		updateMousePosition(p);
	  	bMouseExited = true;
  	}

	/**
	 * Invoked when a mouse is moved in a component.
	 * @param e, the associated MouseEvent.
	 */
	public void mouseMoved(MouseEvent e) {
	  	Point p = SwingUtilities.convertPoint((Component)e.getSource(), e.getX(), e.getY(), (Component)list);
		updateMousePosition(p);
	}

// KEY EVENTS

	/**
	 * Invoked when a key is pressed in a component.
	 * @param evt, the associated KeyEvent.
	 */
	public void keyPressed(KeyEvent evt) {

		ptLocationKeyPress = new Point(_x, _y);

		// NOTE: IF THIS IS NOT THE SELECTED VIEW, IT SHOULD NOT HANDLE THE EVENT,
		// SO GO THROUGH THE FRAME EVENT TO FIND THE SELECTED VIEW
		// - THIS IS REQUIRED BECAUSE OF PROBLEMS PASSING THE FOCUS WHEN AN INTERNAL FRAME IS OPENED
		if (!uiList.getViewFrame().isSelected()) {
			ProjectCompendium.APP.keyPressed(evt);
			return;
		}

		int keyCode = evt.getKeyCode();
		int modifiers = evt.getModifiers();

		if (modifiers == shortcutKey) {
			switch(keyCode) {
				case KeyEvent.VK_F: { // OPEN SEARCH
					ProjectCompendium.APP.onSearch();
					evt.consume();
					break;
				}
				case KeyEvent.VK_O: { // OPEN PROJECT DIALOG
					ProjectCompendium.APP.onFileOpen();
					evt.consume();
					break;
				}
				case KeyEvent.VK_N: { // NEW PROJECT DIALOG
					ProjectCompendium.APP.onFileNew();
					evt.consume();
					break;
				}
				case KeyEvent.VK_X: { // CUT
					ProjectCompendium.APP.setWaitCursor();
					cutToClipboard();
					ProjectCompendium.APP.setDefaultCursor();
					evt.consume();
					break;
				}
				case KeyEvent.VK_C: { // COPY
					ProjectCompendium.APP.setWaitCursor();
					copyToClipboard();
					ProjectCompendium.APP.setDefaultCursor();
					evt.consume();
					break;
				}
				case KeyEvent.VK_V: { // PASTE
					ProjectCompendium.APP.setWaitCursor();
					pasteFromClipboard();
					ProjectCompendium.APP.setDefaultCursor();
					evt.consume();
					break;
				}
				case KeyEvent.VK_A: { // SELECT ALL
					onSelectAll();
					evt.consume();
					break;
				}
				case KeyEvent.VK_Z: { // UNDO
					ProjectCompendium.APP.setWaitCursor();
					ProjectCompendium.APP.onEditUndo();
					ProjectCompendium.APP.setDefaultCursor();
					evt.consume();
					break;
				}
				case KeyEvent.VK_Y: { // REDO
					ProjectCompendium.APP.setWaitCursor();
					ProjectCompendium.APP.onEditRedo();
					ProjectCompendium.APP.setDefaultCursor();
					evt.consume();
					break;
				}
				case KeyEvent.VK_W: { // CLOSE WINDOW
					try {
						if (uiList.getView() != ProjectCompendium.APP.getHomeView() ) {
							uiList.getViewFrame().setClosed(true);
						
							JDesktopPane pane = ProjectCompendium.APP.getDesktop();
							JInternalFrame frame = pane.getSelectedFrame();
							if (frame instanceof UIMapViewFrame) {
								UIMapViewFrame mapframe = (UIMapViewFrame)frame;
								mapframe.getViewPane().requestFocus();
							} else if (frame instanceof UIListViewFrame) {
								UIListViewFrame listframe = (UIListViewFrame)frame;
								listframe.getUIList().getList().requestFocus();
							}
						}
					}
					catch(Exception e) {}

					evt.consume();
					break;
				}				
			}
		}		
		if (modifiers == java.awt.Event.CTRL_MASK) {
			switch(keyCode) {
				case KeyEvent.VK_T: { // OPEN TAG WINDOW
					ProjectCompendium.APP.onCodes();
					evt.consume();
					break;
				}
				case KeyEvent.VK_W: // CLOSE WINDOW
				case KeyEvent.VK_ENTER: {
					try {
						if (uiList.getView() != ProjectCompendium.APP.getHomeView() ) {
							uiList.getViewFrame().setClosed(true);
						
							JDesktopPane pane = ProjectCompendium.APP.getDesktop();
							JInternalFrame frame = pane.getSelectedFrame();
							if (frame instanceof UIMapViewFrame) {
								UIMapViewFrame mapframe = (UIMapViewFrame)frame;
								mapframe.getViewPane().requestFocus();
							} else if (frame instanceof UIListViewFrame) {
								UIListViewFrame listframe = (UIListViewFrame)frame;
								listframe.getUIList().getList().requestFocus();
							}
						}
					}
					catch(Exception e) {}

					evt.consume();
					break;
				}

			}
		}
		else if (keyCode == KeyEvent.VK_DELETE && modifiers == 0) {
			ProjectCompendium.APP.setWaitCursor();
			onDelete();
			ProjectCompendium.APP.setDefaultCursor();
			evt.consume();
		}
		else if (keyCode == KeyEvent.VK_ESCAPE && modifiers == 0) {
			if (uiList.getNumberOfNodes() != 0) {
				uiList.getList().clearSelection();
			}
			evt.consume();
		}
		else if (keyCode == KeyEvent.VK_BACK_SPACE && modifiers == 0) {
			ProjectCompendium.APP.setWaitCursor();
			onDelete();
			ProjectCompendium.APP.setDefaultCursor();
			evt.consume();
		}
		else if (keyCode == KeyEvent.VK_SPACE && modifiers == 0) {
			int rowIndex = uiList.getList().getSelectedRow();
			if (rowIndex != -1) {
				uiList.getList().editCellAt(rowIndex, ListTableModel.LABEL_COLUMN);
			}
			evt.consume();
		}		
		else if (keyCode == KeyEvent.VK_ENTER && modifiers == 0) {
			int rowIndex = uiList.getList().getSelectedRow();
			if (rowIndex != -1) {
				NodePosition pos =uiList.getNodePosition(rowIndex);
				NodeSummary node = pos.getNode();
				openNode(node);						
			}
			else {
				uiList.getViewFrame().showEditDialog();
			}
			evt.consume();
		}	
		else if (keyCode == KeyEvent.VK_INSERT && modifiers == 0) {
			int rowIndex = uiList.getList().getSelectedRow();
			if (rowIndex != -1) {
				NodePosition pos =uiList.getNodePosition(rowIndex);
				uiList.hideHint();
				uiList.showEditDialog(pos);
			}
			evt.consume();
		}		
		else if (keyCode == KeyEvent.VK_F12 && modifiers == 0) {
			onMarkSelectionSeen();				// Mark all selected nodes Seen - mlb
			evt.consume();
		}
		else if ((keyCode == KeyEvent.VK_F12) && (modifiers == java.awt.Event.SHIFT_MASK)) {
			onMarkSelectionUnseen();			// Mark all selected nodes Unseen - mlb
			evt.consume();
		}

		bClicked = false;
 	}

	/**
	 * Invoked when a key is released in a component.
	 * @param evt, the associated KeyEvent.
	 */
  	public void keyReleased(KeyEvent e) {
		bIsKeyDown = false;
 	}

	/**
	 * Invoked when a key is typed in a component.
	 * @param evt, the associated KeyEvent.
	 */
  	public void keyTyped(KeyEvent evt) {
		
  		char [] key = {evt.getKeyChar()};
		String sKeyPressed = new String(key);
		int keyCode = evt.getKeyCode();
		int modifiers = evt.getModifiers();

		if (modifiers == 0) {
			int nType = UINodeTypeManager.getTypeForKeyPress(sKeyPressed);
			if (nType > -1) {
				if (!uiList.getList().isEditing()) {
					createNode( nType, "", ProjectCompendium.APP.getModel().getUserProfile().getUserName(), //$NON-NLS-1$
								"", "", ptLocationKeyPress.x, (uiList.getNumberOfNodes() + 1) * 10); //$NON-NLS-1$ //$NON-NLS-2$
					uiList.updateTable();
				}

				evt.consume();
			}
		}
 		
  	}


	/**
	 * Required for the ClipboardOwner implementation.
   	 */
	public void lostOwnership(Clipboard clip, Transferable trans) {}


	/**
	 * Deletes the selected objects from the list.
	 */
	public void onDelete() {
		// record the effect of the deletion
		// need to pass to this method the info you need to recreate the nodes/links
		DeleteEdit edit = new DeleteEdit(uiList.getViewFrame());
		uiList.deleteSelectedNodes(edit);

		// notify the listeners
		uiList.getViewFrame().getUndoListener().postEdit(edit);

		ProjectCompendium.APP.setTrashBinIcon();
	}
	
	/**
	 * Mark all nodes in the selection as read/seen (F12 handler)		MLB: Feb. '08
	 */
	public void onMarkSelectionSeen() {
		try {
			Enumeration e = getUIList().getSelectedNodes();
			for(;e.hasMoreElements();){
				NodePosition np = (NodePosition) e.nextElement();
				NodeSummary oNode = np.getNode();
				oNode.setState(ICoreConstants.READSTATE);	
			}
			uiList.updateTable();
		}
		catch(Exception io) {
			System.out.println("Unable to mark as read: "+io.getMessage()); //$NON-NLS-1$
		}
	}
	
	/**
	 * Mark all nodes in the selection as unread/unseen (Shift-F12 handler)		MLB: Feb. '08
	 */
	public void onMarkSelectionUnseen() {
		try {
			Enumeration e = getUIList().getSelectedNodes();
			for(;e.hasMoreElements();){
				NodePosition np = (NodePosition) e.nextElement();
				NodeSummary oNode = np.getNode();
				oNode.setState(ICoreConstants.UNREADSTATE);	
			}
			uiList.updateTable();
		}
		catch(Exception io) {
			System.out.println("Unable to mark as unread: "+io.getMessage()); //$NON-NLS-1$
		}
	}	

	/**
	 * Selects all the objects on the list.
	 */
	public void onSelectAll() {
		if (uiList.getNumberOfNodes() != 0) {
			uiList.getList().selectAll();
		}
	}

  	/**
   	 * Return if a copy to the Clipbaord is currently underway.
	 * @return boolean, true is a copy is underway, else false.
   	 */
	public boolean isCopyToClipboard() {
		return bCopyToClipboard;
	}

  	/**
   	 * Return if a cut to the Clipbaord is currently underway.
	 * @return boolean, true is a cut is underway, else false.
   	 */
	public boolean isCutToClipboard() {
		return bCutToClipboard;
	}

	/**
	 * This method copies the selected nodes to the clipboard and also
	 * stores all view children for potential subsequent pasting into another database.
	 */
	public void externalCopyToClipboard() {

		bCopyToClipboard = true;
		ClipboardTransferables clips = new ClipboardTransferables();

		for(Enumeration e = uiList.getSelectedNodes();e.hasMoreElements();) {
			NodePosition nodePosition = (NodePosition)e.nextElement();
			clips.addTransferables(nodePosition);
			if (nodePosition.getNode() instanceof View) {
				View view = (View)nodePosition.getNode();
				view.storeChildren(new Hashtable(51));
			}
		}
		ProjectCompendium.APP.getClipboard().setContents(clips,this);
	}

	/**
	 * This method copies the selected nodes to the clipboard.
	 */
	public void copyToClipboard() {

		bCopyToClipboard = true;
		ClipboardTransferables clips = new ClipboardTransferables();

		for(Enumeration e = uiList.getSelectedNodes();e.hasMoreElements();) {
			NodePosition nodePosition = (NodePosition)e.nextElement();
			clips.addTransferables(nodePosition);
		}
		ProjectCompendium.APP.getClipboard().setContents(clips,this);
		ProjectCompendium.APP.setPasteEnabled(true);
	}

	/**
	 * This method cuts the selected nodes to the clipboard, and deletes them from the list.
	 */
	public void cutToClipboard() {

		bCutToClipboard = true;

		ClipboardTransferables clips = new ClipboardTransferables();

		// record the effect of the deletion
		// need to pass to this method the info you need to recreate the nodes/links
		CutEdit edit = new CutEdit(uiList.getViewFrame());

		// copy the objects selected
		for(Enumeration e = uiList.getSelectedNodes();e.hasMoreElements();) {
			NodePosition nodePosition = (NodePosition)e.nextElement();
			clips.addTransferables(nodePosition);
			//uinode.setCut(true);
		}

		uiList.deleteSelectedNodes(edit);

		// notify the listeners
		uiList.getViewFrame().getUndoListener().postEdit(edit);

		ProjectCompendium.APP.getClipboard().setContents(clips,this);
		ProjectCompendium.APP.setPasteEnabled(true);

		// update node inidicators		
		Thread thread = new Thread() {
			public void run() {
				ProjectCompendium.APP.setTrashBinIcon();
				ProjectCompendium.APP.refreshIconIndicators();		
			}
		};
		thread.start();		
	}

	/**
	 * This routine gets the clipboard contents, and paste it into this list.
	 */
	public void pasteFromClipboard() {

		ClipboardTransferables clipui = null;

		if((clipui = (ClipboardTransferables)(ProjectCompendium.APP.getClipboard().getContents(this))) != null) {
			Class clipclass = null;
			try {
				clipclass = Class.forName("com.compendium.ui.edits.ClipboardTransferables"); //$NON-NLS-1$
			}
			catch(ClassNotFoundException ex) {
				ProjectCompendium.APP.displayError("Error: (ListUI.pasteFromClipboard-1)\n\n" + ex.getLocalizedMessage()); //$NON-NLS-1$
			}

			String mimetype = "application/x-java-serialized-object"; //$NON-NLS-1$
			DataFlavor dataflavor = new DataFlavor(clipclass,mimetype);

			IModel model = ProjectCompendium.APP.getModel();
			PCSession session = model.getSession();
			INodeService nodeService = model.getNodeService();
			String sViewID = uiList.getView().getId();

			try {
				PasteEdit edit = new PasteEdit(uiList.getViewFrame());

				uiList.deselectAll();
				for(Enumeration e = clipui.getTransferables();e.hasMoreElements();) {

					Object o = e.nextElement();
					NodePosition np = null;
					NodeSummary pasteNodeSummary = null;

					if(o instanceof NodeUI) {
						NodeUI nodeui = (NodeUI)o;
						np = nodeui.getUINode().getNodePosition();
						pasteNodeSummary = np.getNode();
					}
					else if (o instanceof NodePosition) {
						np = (NodePosition)o;
						pasteNodeSummary = np.getNode();
					}
					else {
						continue;
					}

					// IF NODE WAS DELETED, RESTORE NODE
					String sNodeID = pasteNodeSummary.getId();
					if (nodeService.isMarkedForDeletion(session, sNodeID)) {
						nodeService.restoreNode(session, sNodeID);
					}

					// PASTE CREATES A NEW OBJECT, SO PURGE ANY OLD VIEWNODE RECORDS FOR THIS VIEW/NODE COMBO
					nodeService.purgeViewNode(session, sViewID, sNodeID);

					NodeSummary newPasteNodeSummary = nodeService.getNodeSummary(session, sNodeID);

					int nodeInView = getUIList().getIndexOf(pasteNodeSummary);
					if (nodeInView == -1) {
						np = uiList.getView().addNodeToView(pasteNodeSummary, np.getXPos(), (uiList.getNumberOfNodes() + 1) * 10);
						edit.AddNodeToEdit (np, (uiList.getNumberOfNodes() + 1) * 10);
						uiList.insertNode(np, uiList.getNumberOfNodes());
						uiList.selectNode(uiList.getNumberOfNodes() - 1, ICoreConstants.MULTISELECT);
					}
					else {
						//select node that was found in view
						uiList.selectNode(nodeInView, ICoreConstants.MULTISELECT);
					}

					if (pasteNodeSummary instanceof View) {
						ProjectCompendium.APP.ht_pasteCheck.put(sNodeID, np.getNode().getId());

						View deletedView = (View)pasteNodeSummary;
						View newView = (View)np.getNode();
						UIViewFrame deletedUIViewFrame = ProjectCompendium.APP.getViewFrame(newView, newView.getLabel());
						if (View.isListType(deletedView.getType())) {
							((UIListViewFrame)deletedUIViewFrame).getUIList(). getListUI().restoreDeletedNodes(deletedView);
						}
						else {
							((UIMapViewFrame)deletedUIViewFrame).getViewPane().getUI().restoreDeletedNodesAndLinks(deletedView);
						}
					}
				}
				// notify the listeners of the Paste (for undo/redo)
				uiList.getViewFrame().getUndoListener().postEdit(edit);
		  	}
			catch(Exception ex) {
				ex.printStackTrace();
			  	ProjectCompendium.APP.displayError("Error: (ListUI.pasteFromClipbard-2)\n\n" + ex.getLocalizedMessage()); //$NON-NLS-1$
			}
		}
		
		if (FormatProperties.showPasteHint) {
			UIHintDialog hint = new UIHintDialog(ProjectCompendium.APP, UIHintDialog.PASTE_HINT);
			UIUtilities.centerComponent(hint, ProjectCompendium.APP);
			hint.setVisible(true);		
		}
		
		bCopyToClipboard = false;
		bCutToClipboard = false;
		
		ProjectCompendium.APP.refreshIconIndicators();
	}

	/**
	 * This routine gets the clipboard contents and paste them into the list as an external paste.
	 */
	public void externalPasteFromClipboard() {

		ClipboardTransferables clipui = null;

		if((clipui = (ClipboardTransferables)(ProjectCompendium.APP.getClipboard().getContents(this))) != null) {
			Class clipclass = null;
			try {
				clipclass = Class.forName("com.compendium.ui.edits.ClipboardTransferables"); //$NON-NLS-1$
			}
			catch(ClassNotFoundException ex) {
				ProjectCompendium.APP.displayError("Error: (ListUI.externalPasteFromClipboard-1)\n\n" + ex.getLocalizedMessage()); //$NON-NLS-1$
			}

			String mimetype = "application/x-java-serialized-object"; //$NON-NLS-1$
			DataFlavor dataflavor = new DataFlavor(clipclass,mimetype);

			IModel model = ProjectCompendium.APP.getModel();
			PCSession session = model.getSession();
			INodeService nodeService = model.getNodeService();

			String sAuthor = uiList.getViewFrame().getCurrentAuthor();
			try {
				// record the effect of the paste
				// need to pass to this method the info you need to delete/recreate the nodes&links
				PasteEdit edit = new PasteEdit(uiList.getViewFrame());

				uiList.deselectAll();

				for(Enumeration e = clipui.getTransferables();e.hasMoreElements();) {
					Object o = e.nextElement();
					NodePosition np = null;
					NodeSummary pasteNodeSummary = null;
					if(o instanceof NodeUI) {
						NodeUI nodeui = (NodeUI)o;
						np = nodeui.getUINode().getNodePosition();
						pasteNodeSummary = np.getNode();
					}
					else if (o instanceof NodePosition) {
						np = (NodePosition)o;
						pasteNodeSummary = np.getNode();
					}
					else {
						continue;
					}

					int nodeType = np.getNode().getType();
					String label = np.getNode().getLabel();
					String detail = np.getNode().getDetail();
					String sNodeID = pasteNodeSummary.getId();

					//check if node is already present(if paste is from another DB)
					NodeSummary newPasteNodeSummary = nodeService.getNodeSummary(session, sNodeID);

					int nodeInView = getUIList().getIndexOf(pasteNodeSummary);
					NodePosition newNode = null;

					if (newPasteNodeSummary == null) {
						newNode = createNode(sNodeID,
								pasteNodeSummary.getType(),
								pasteNodeSummary.getOriginalID(),
								pasteNodeSummary.getAuthor(),
								pasteNodeSummary.getLabel(),
								pasteNodeSummary.getDetail(),
								np.getXPos(), (uiList.getNumberOfNodes() + 1) * 10
								);

						NodeSummary newNodeSummary = newNode.getNode();
						int nodeType2 = pasteNodeSummary.getType();
						if (nodeType2 == ICoreConstants.REFERENCE || nodeType2 == ICoreConstants.REFERENCE_SHORTCUT) {
							newNodeSummary.setSource(pasteNodeSummary.getSource(), pasteNodeSummary.getImage(), sAuthor);
						}
						else if(View.isViewType(nodeType2)) {
							newNodeSummary.setSource("", pasteNodeSummary.getImage(), sAuthor); //$NON-NLS-1$
						}

						//newNode = uiList.getView().addNodeToView(newNodeSummary, np.getXPos(), (uiList.getNumberOfNodes() + 1) * 10);
						uiList.insertNode(newNode, uiList.getNumberOfNodes());
						PasteEdit.nodeList.put(pasteNodeSummary.getId(), newNode.getNode().getId());
						edit.AddNodeToEdit (newNode, (uiList.getNumberOfNodes() + 1) * 10);
						uiList.selectNode(uiList.getNumberOfNodes() - 1, ICoreConstants.MULTISELECT);
					}
					else if (nodeInView == -1) {
						newNode = uiList.getView().addNodeToView(pasteNodeSummary, np.getXPos(), (uiList.getNumberOfNodes() + 1) * 10);
						edit.AddNodeToEdit (newNode, (uiList.getNumberOfNodes() + 1) * 10);
						uiList.insertNode(newNode, uiList.getNumberOfNodes());
						uiList.selectNode(uiList.getNumberOfNodes() - 1, ICoreConstants.MULTISELECT);
					}
					else {
						//select node that was found in view
						uiList.selectNode(uiList.getIndexOf(np.getNode()), ICoreConstants.MULTISELECT);
					}

					if (pasteNodeSummary instanceof View) {
						ProjectCompendium.APP.ht_pasteCheck.put(sNodeID, newNode.getNode().getId());

						View deletedView = (View)pasteNodeSummary;
						View newView = (View)newNode.getNode();
						UIViewFrame deletedUIViewFrame = ProjectCompendium.APP.getViewFrame(newView, newView.getLabel());
						if (View.isListType(deletedView.getType())) {
							((UIListViewFrame)deletedUIViewFrame).getUIList(). getListUI().restoreDeletedNodes(deletedView);
						}
						else {
							((UIMapViewFrame)deletedUIViewFrame).getViewPane().getUI().restoreDeletedNodesAndLinks(deletedView);
						}
					}
				}
				// notify the listeners of the Paste (for undo/redo)
				uiList.getViewFrame().getUndoListener().postEdit(edit);
		  	}
			catch(Exception ex) {
				ex.printStackTrace();
			  	ProjectCompendium.APP.displayError("Error: (ListUI.externalPasteFromClipbard-2)\n\n" + ex.getLocalizedMessage()); //$NON-NLS-1$
			}
		}

		bCopyToClipboard = false;
		bCutToClipboard = false;
		ProjectCompendium.APP.refreshIconIndicators();		
	}


	// IMPORTING METHODS

	/**
	 * Setting for Questmap or XML imports to use.
	 * @param doSmartImport, setting for Questmap or XML imports to use.
	 */
  	public void setSmartImport(boolean doSmartImport) {
		isSmartImport = doSmartImport;
  	}

	/**
	 * For Importing files from Questmap. Instantiates and starts the parser.
	 * @param filename, the name of the file to import.
	 */
	public void onImportFile(String filename) {

		// parse the file
		Parser parser = new Parser(false, filename, ProjectCompendium.APP.getModel(), uiList.getView());
		parser.setUIList(uiList);
		parser.setSmartImport(isSmartImport);

		//since the parser is on a new thread ...
		parser.start();
	}

	/**
	 * For Importing XML files.
	 * @param filename, the name of the file to import.
	 * @param includeInDetail, whether to include the original author and dates in the node detail.
	 */
	public void onImportXMLFile(String filename, boolean includeInDetail) {
		
		XMLImport xmlImport = new XMLImport(false, filename, ProjectCompendium.APP.getModel(), uiList.getView(), isSmartImport, includeInDetail);
		xmlImport.setUIList(uiList);
		xmlImport.start();
	}


  	/**
     * Creates a clone node to the given node.
	 * @param node com.compendium.core.datamodel.NodeSummary, the node to clone.
	 * @return com.compendium.core.datamodel.NodePosition, the cloned node.
     */
	public NodePosition createCloneNode(NodeSummary node) {

	  	NodePosition np = null;
	  	NodeSummary cloneNode = null;
	  	NodePosition cloneNodePos = null;

 		try {
			//create a node to be added to view
			String id = uiList.getView().getModel().getUniqueID();
			String author = node.getAuthor();
			String label = node.getLabel();
			String detail = node.getDetail();
			
			if(detail == null)
					detail = ""; //$NON-NLS-1$
			int nodeType = node.getType();

			if(!(node instanceof ShortCutNodeSummary)) {
				cloneNodePos = (NodePosition)uiList.getView().addMemberNode(
													  nodeType,		//int type
													  "",			//String xNodeType, //$NON-NLS-1$
													  "",			//String sOriginalID, //$NON-NLS-1$
													  author,		//String author,
													  label,		//String label
													  detail,		//String detail
													  100,			//int x
													  (uiList.getNumberOfNodes() + 1) * 10
													  );			//int y
				cloneNode = cloneNodePos.getNode();
				if(cloneNode == null) {
					throw new Exception("Node null"); //$NON-NLS-1$
				}
				cloneNode.initialize(uiList.getView().getModel().getSession(), uiList.getView().getModel());
				
				String source = node.getSource();
				String image = node.getImage();
				if ((source != null && !source.equals("")) || (image != null && !image.equals(""))) {					 //$NON-NLS-1$ //$NON-NLS-2$
					cloneNode.setSource(source, image, node.getImageSize(), author);
				}			

				if(View.isMapType(nodeType)) {
					String sBackground = ""; //$NON-NLS-1$
					ViewLayer layer  = ((View)node).getViewLayer();
					if (layer == null) {
						try { ((View)node).initializeMembers();
							sBackground = layer.getBackgroundImage();
						}
						catch(Exception ex) {}
					}
					else {
						sBackground = layer.getBackgroundImage();
					}
					if (!sBackground.equals("")) { //$NON-NLS-1$
						((View)cloneNode).setBackgroundImage( sBackground );
						((View)cloneNode).updateViewLayer();
					}
				}

				//get the detailpages for the parent node. The cloned node has the same details			
				Vector details = node.getDetailPages(author);
				Vector newDetails = new Vector();
				int count = details.size();
				for (int i=0; i<count; i++) {
					NodeDetailPage page = (NodeDetailPage)details.elementAt(i);
					NodeDetailPage newPage = new NodeDetailPage(cloneNode.getId(), page.getAuthor(), page.getText(), page.getPageNo(), page.getCreationDate(), page.getModificationDate());
					newDetails.addElement(newPage);
				}

				if (!newDetails.isEmpty()) {
					String sAuthor = ProjectCompendium.APP.getModel().getUserProfile().getAuthor();
					cloneNode.setDetailPages(newDetails, sAuthor, sAuthor);
				}

				//get the codes for the parent nodes.. even the cloned node has the same codes
				for(Enumeration e = node.getCodes();e.hasMoreElements();) {
					Code code = (Code)e.nextElement();
					if (cloneNode.addCode(code)) {	//means the code is added
						System.out.println("Cannot add code "+ code.getName() + " to "  + label); //$NON-NLS-1$ //$NON-NLS-2$
					}
				}

				//add the clone to parent node list
				uiList.updateTable();
			}
		}
		catch(Exception e) {
			ProjectCompendium.APP.displayError("Error: (ListUI.createCloneNode) \n\n" + e.getLocalizedMessage()); //$NON-NLS-1$
		}
 		
		return (cloneNodePos);
  	}

  	/**
     * Creates a new node from the passed parameters.
	 * @param sNodeID, the id to give this node.
	 * @param nodeType, the type of the new node.
	 * @param sOriginalID, the original id of this node.
	 * @param author, the author of this node.
	 * @param label, the label for this node.
	 * @param detail, the main detail page for this node.
	 * @param x, the x position to place this node at in this view.
	 * @param y, the y position, or row to place this node at in this view.
	 * @return com.compendium.core.datamodel.NodePosition, the newly create node.
    */
	public NodePosition createNode(String sNodeID, int nodeType, String sOriginalID,
							String author, String label,
							String detail, int x, int y) {

		NodeSummary node = null;
	  	View view = null;
	  	NodePosition nodePos = null;

	  	if(!View.isViewType(nodeType)) {
			try {
				nodePos = uiList.getView().addMemberNode(sNodeID,
													nodeType,				//int type
													"",						//String xNodeType, //$NON-NLS-1$
													"",						//String xml imported id //$NON-NLS-1$
													sOriginalID,			//String original id,
													author,					//String author,
													label,					//String label
													detail,					//String detail
													x,						//int x
													y						//int y
													);
				node = nodePos.getNode();
				node.initialize(ProjectCompendium.APP.getModel().getSession(), ProjectCompendium.APP.getModel());
			}
			catch (Exception e) {
				e.printStackTrace();
				ProjectCompendium.APP.displayError("Error: (ListUI.createNode - with ID)\n\n "+e.getLocalizedMessage()); //$NON-NLS-1$
			}
	 	}
	  	else {
			try {
				nodePos = uiList.getView().addMemberNode(sNodeID,
													nodeType,				//int type
													"",						//String xNodeType, //$NON-NLS-1$
													""		,				//String xml imported id //$NON-NLS-1$
													sOriginalID,			//String originalId,
													author,					//String author,
													label,					//String label
													detail,					//String detail
													x,						//int x
													y
													);

				view = (View) nodePos.getNode();
				view.initialize(ProjectCompendium.APP.getModel().getSession(), ProjectCompendium.APP.getModel());
			}
			catch (Exception e) {
				System.out.println("Error in (ListUI.createNode - with ID)\n\n"+e.getMessage()); //$NON-NLS-1$
			}
		}

		return nodePos;
  	}
	
  	/**
     * Creates a new node from the passed parameters.
	 * @param nodeType, the type of the new node.
	 * @param sOriginalID, the original id of this node is it has been imported.
	 * @param author, the author of this node.
	 * @param label, the label for this node.
	 * @param detail, the main detail page for this node.
	 * @param x, the x position to place this node at - not really used here - Always zero.
	 * @param y, the y position, or row to place this node at in the list.
	 * @return com.compendium.core.datamodel.NodePosition, the newly create node.
     */
  	public NodePosition createNode(int nodeType, String sOriginalID,
							String author, String label,
							String detail, int x, int y)
  	{
		NodeSummary node = null;
	  	View view = null;
	  	NodePosition nodePos = null;

	  	if(!View.isViewType(nodeType)) {
			try {
				nodePos = uiList.getView().addMemberNode(nodeType,		//int type
													  "",								//String xNodeType, //$NON-NLS-1$
													  sOriginalID,						//String original id,
													  author,							//String author,
													  label,							//String label
													  detail,							//String detail
													  x,								//int x
													  y
													  );								//int y
				node = nodePos.getNode();
				node.initialize(ProjectCompendium.APP.getModel().getSession(), ProjectCompendium.APP.getModel());
			}
			catch (Exception e) {
				ProjectCompendium.APP.displayError("Error: (ListUI.createNode)\n\n"+e.getLocalizedMessage()); //$NON-NLS-1$
			}
	  	}
	  	else {
			try {
				nodePos = uiList.getView().addMemberNode(nodeType,			//int type
													  "",								//String xNodeType, //$NON-NLS-1$
													  sOriginalID,						//String original id,
													  author,							//String author,
													  label,							//String label
													  detail,							//String detail
													  x,
													  y
													  );
													  //(list.getRowCount() + 1) * 10);
				view = (View) nodePos.getNode();
				view.initialize(ProjectCompendium.APP.getModel().getSession(), ProjectCompendium.APP.getModel());
			}
			catch (Exception e) {
				ProjectCompendium.APP.displayError("Error: (ListUI.createNode)\n\n"+e.getLocalizedMessage()); //$NON-NLS-1$
			}
	  	}
	  	return nodePos;
  	}

  	/**
     * Creates a new node from the passed parameters.
	 * @param nodeType, the type of the new node.
	 * @param importedId, the id the node had when imported.
	 * @param sOriginalID, the original id of this node.
	 * @param author, the author of this node.
	 * @param label, the label for this node.
	 * @param detail, the main detail page for this node.
	 * @param x, the x position to place this node at - not really used here - Always zero.
	 * @param y, the y position, or row to place this node at in the list.
	 * @return com.compendium.core.datamodel.NodePosition, the newly create node.
     */
  	public NodePosition createNode(int nodeType, String importedId, String sOriginalID,
							String author, String label,
							String detail, int x, int y) {

		java.util.Date date = new java.util.Date();

		return createNode(nodeType, importedId, sOriginalID, author, date, date, 
				label, detail, x, y, date, date);
  	}

  	/**
     * Creates a new node from the passed parameters.
	 * @param nodeType, the type of the new node.
	 * @param importedId, the id the node had when imported.
	 * @param sOriginalID, the original id of this node.
	 * @param author, the author of this node.
	 * @param creationDate, the date the node was created.
	 * @param modDate, the date the node was last modified.
	 * @param label, the label for this node.
	 * @param detail, the main detail page for this node.
	 * @param x, the x position to place this node at - not really used here - Always zero.
	 * @param y, the y position, or row to place this node at in the list.
	 * @param transCreationDate, the date the node was put into the view.
	 * @param transModDate, the date the view-node data was last modified.
	 * @param sLastModAuthor the author who last modified this node.
	 * @return com.compendium.core.datamodel.NodePosition, the newly create node.
     */
  	public NodePosition createNode(int nodeType, String importedId, String sOriginalID,
							String author, java.util.Date creationDate, java.util.Date modDate, String label,
							String detail, int x, int y, java.util.Date transCreationDate,
							java.util.Date transModDate) {

		NodeSummary node = null;
	  	View view = null;
	  	NodePosition nodePos = null;

	  	if(!View.isViewType(nodeType)) {
			try {
				nodePos = uiList.getView().addMemberNode(
													nodeType,				//int type
													"",						//String xNodeType, //$NON-NLS-1$
													importedId,				//String xml imported id
													sOriginalID,			//String original id,
													author,					//String author,
													creationDate,			//Node creation date
													modDate,				//Node modification date
													label,					//String label
													detail,					//String detail
													x,						//int x
													y,						//int y
													transCreationDate,		//Node in View creation date
													transModDate			//Node inView last modification date													
													);
				//System.out.println("NodeType in if:" + nodeType);
				node = nodePos.getNode();
				node.initialize(ProjectCompendium.APP.getModel().getSession(), ProjectCompendium.APP.getModel());

			}
			catch (Exception e) {
				ProjectCompendium.APP.displayError("Error: (ListUI.createNode) \n\n"+e.getLocalizedMessage()); //$NON-NLS-1$
			}
	  	}
	  	else {
			try {
				nodePos = uiList.getView().addMemberNode(
													nodeType,				//int type
													"",						//String xNodeType, //$NON-NLS-1$
													importedId,				//String xml imported id
													sOriginalID,			//String original id,
													author,					//String author,
													creationDate,			//Node creation date
													modDate,				//Node modification date
													label,					//String label
													detail,					//String detail
													x,						//int x
													y,						//int y
													transCreationDate,		//Node in View creation date
													transModDate			//Node inView last modification date													
													);
				view = (View) nodePos.getNode();
				view.initialize(ProjectCompendium.APP.getModel().getSession(), ProjectCompendium.APP.getModel());


				//cast it to NodeSummary can be returned by this function
				node = (NodeSummary)view;
			}
			catch (Exception e) {
				ProjectCompendium.APP.displayError("Error: (ListUI.createNode)\n\n"+e.getLocalizedMessage()); //$NON-NLS-1$
			}
	  	}
	  	return nodePos;
  	}

 	/**
     * Creates a new node from the passed parameters.
	 * @param nodeType the type of the new node.
	 * @param importedId the id the node had when imported.
	 * @param sOriginalID the original id of this node.
	 * @param author the author of this node.
	 * @param creationDate the date the node was created.
	 * @param modDate the date the node was last modified.
	 * @param label the label for this node.
	 * @param detail the main detail page for this node.
	 * @param x the x position to place this node at - not really used here - Always zero.
	 * @param y the y position or row to place this node at in the list.
	 * @param transCreationDate the date the node was put into the view.
	 * @param transModDate the date the view-node data was last modified.
	 * @param sLastModAuthor the author who last modified this node.
	 * @param bShowTags true if this node has the tags indicator draw.
	 * @param bShowText true if this node has the text indicator drawn
	 * @param bShowTrans true if this node has the transclusion indicator drawn
	 * @param bShowWeight true if this node has the weight indicator displayed
	 * @param bSmallIcon true if this node is using a small icon
	 * @param bHideIcons true if this node is not displaying its icon
	 * @param nWrapWidth the node label wrap width used for this node in this view.
	 * @param nFontSize	the font size used for this node in this view
	 * @param sFontFace the font face used for this node in this view
	 * @param nFontStyle the font style used for this node in this view
	 * @param nForeground the foreground color used for this node in this view
	 * @param nBackground the background color used for this node in this view.
	 * 
	 * @return com.compendium.core.datamodel.NodePosition, the newly create node.
     */
  	public NodePosition createNode(int nodeType, String importedId, String sOriginalID,
							String author, java.util.Date creationDate, java.util.Date modDate, String label,
							String detail, int x, int y, java.util.Date transCreationDate,
							java.util.Date transModDate, String sLastModAuthor,	
							boolean bShowTags, boolean bShowText, boolean bShowTrans, 
							boolean bShowWeight, boolean bSmallIcon, boolean bHideIcon, 
							int nWrapWidth, int nFontSize, String sFontFace, 
							int nFontStyle, int nForeground, int nBackground) {

		NodeSummary node = null;
	  	View view = null;
	  	NodePosition nodePos = null;

	  	if(!View.isViewType(nodeType)) {
			try {
				nodePos = uiList.getView().addMemberNode(
													nodeType,				//int type
													"",						//String xNodeType, //$NON-NLS-1$
													importedId,				//String xml imported id
													sOriginalID,			//String original id,
													author,					//String author,
													creationDate,			//Node creation date
													modDate,				//Node modification date
													label,					//String label
													detail,					//String detail
													x,						//int x
													y,						//int y
													transCreationDate,	transModDate,
													sLastModAuthor,bShowTags, bShowText,
													bShowTrans,	bShowWeight, bSmallIcon,	bHideIcon, nWrapWidth,
													nFontSize, sFontFace, nFontStyle, nForeground, nBackground
													);
				//System.out.println("NodeType in if:" + nodeType);
				node = nodePos.getNode();
				node.initialize(ProjectCompendium.APP.getModel().getSession(), ProjectCompendium.APP.getModel());

			}
			catch (Exception e) {
				ProjectCompendium.APP.displayError("Error: (ListUI.createNode)\n\n"+e.getLocalizedMessage()); //$NON-NLS-1$
			}
	  	}
	  	else {
			try {
				nodePos = uiList.getView().addMemberNode(nodeType,	"",	importedId,	sOriginalID, //$NON-NLS-1$
													author,	creationDate, modDate,
													label, detail,	x, y,	
													transCreationDate, transModDate,
													sLastModAuthor, bShowTags, bShowText,
													bShowTrans,	bShowWeight, bSmallIcon,	bHideIcon, nWrapWidth,
													nFontSize, sFontFace, nFontStyle, nForeground, nBackground
													);
				view = (View) nodePos.getNode();
				view.initialize(ProjectCompendium.APP.getModel().getSession(), ProjectCompendium.APP.getModel());


				//cast it to NodeSummary can be returned by this function
				node = (NodeSummary)view;
			}
			catch (Exception e) {
				ProjectCompendium.APP.displayError("Error: (ListUI.createNode2)\n\n"+e.getLocalizedMessage()); //$NON-NLS-1$
			}
	  	}
	  	return nodePos;
  	}
  	
	/**
	 * Used by view paste operations to restore child nodes.
	 * @param deletedView com.compendium.core.datamodel.View
	 */
	public void restoreDeletedNodes(View deletedView) {

		try {
			IModel model = ProjectCompendium.APP.getModel();
			
			PCSession session = model.getSession();
			
			INodeService nodeService = model.getNodeService();

			String sViewID = deletedView.getId();

			Vector deletedNodes = deletedView.getDeletedNodes();
			for (int i = 0; i < deletedNodes.size(); i++) {

				NodePosition np = (NodePosition)deletedNodes.elementAt(i);
				NodeSummary pasteNodeSummary = np.getNode();
				String sNodeID = pasteNodeSummary.getId();

				NodePosition newNode = null;

				//Restore, incase deleted
				boolean restored = model.getNodeService().restoreNode(session, sNodeID);
				if (restored) {
					np = model.getNodeService().restoreNodeView(session, sNodeID, sViewID);
				}

				if (np != null) {
					newNode = deletedView.addMemberNode(np);
				}
				else {
					newNode = getUIList().getView().addNodeToView(np.getNode(), np.getXPos(), np.getYPos());
				}


				if (newNode.getNode() instanceof View) {

					// IF NODE ALREADY RESTORED, DON'T TRY AND RESTORE IT AGAIN
					if (!ProjectCompendium.APP.ht_pasteCheck.containsKey(sNodeID)) {
						ProjectCompendium.APP.ht_pasteCheck.put(sNodeID, newNode.getNode().getId());

						View view = (View)newNode.getNode();
						UIViewFrame deletedUIViewFrame = ProjectCompendium.APP.getViewFrame(view, view.getLabel());
						if (View.isListType(view.getType()))
							((UIListViewFrame)deletedUIViewFrame).getUIList().getListUI().restoreDeletedNodes((View)pasteNodeSummary);
						else
							((UIMapViewFrame)deletedUIViewFrame).getViewPane().getUI().restoreDeletedNodesAndLinks((View)pasteNodeSummary);
					}
				}

				// DON'T ADD THE SAME NODE TWICE TO THE SAME LIST
				if (getUIList().getIndexOf(newNode.getNode()) == -1)
					getUIList().insertNode(newNode, (newNode.getYPos()/10) - 1);
			}
			getUIList().updateTable();
			deletedNodes.removeAllElements();
		}
		catch(Exception e) {
			e.printStackTrace();
			ProjectCompendium.APP.displayError("Error: (ListUI.restoreDeletedNodes)\n\n" + e.getLocalizedMessage()); //$NON-NLS-1$
		}
	}

	/**
	 * Used by view external paste operations to restore child nodes.
	 * @param deletedView com.compendium.core.datamodel.View
	 */
	public void externalRestoreDeletedNodes(View deletedView) {

		try {
			IModel model = ProjectCompendium.APP.getModel();
			PCSession session = model.getSession();
			INodeService nodeService = model.getNodeService();
			
			String sAuthor = getUIList().getViewFrame().getCurrentAuthor();

			View thisView = getUIList().getView();
			if (thisView.getModel() == null) {
				thisView.initialize( session, model );
			}
			thisView.initializeMembers();
			
			String sViewID = thisView.getId();

			Vector deletedNodes = deletedView.getDeletedNodes();
			for (int i = 0; i < deletedNodes.size(); i++) {

				NodePosition np = (NodePosition)deletedNodes.elementAt(i);
				NodeSummary pasteNodeSummary = np.getNode();
				String sNodeID = pasteNodeSummary.getId();
				NodePosition newNode = null;

				String existingNode = null;

				// IF NODE ALREADY IN DATABASE
				if (nodeService.doesNodeExist(session, sNodeID)) {
					//Restore, incase deleted
					boolean restored = model.getNodeService().restoreNode(session, sNodeID);
					NodePosition oPos = null;
					if (restored) {
						oPos = model.getNodeService().restoreNodeView(session, sNodeID, sViewID);
					}
					if (oPos != null) {
						newNode = thisView.addMemberNode(oPos);
					}
					else {
						newNode = thisView.addNodeToView(np.getNode(), np.getXPos(), np.getYPos());
					}
				}
				else  { // CREATE A NEW ONE

					NodeSummary newPasteNodeSummary = null;

					//check if this node has been created in this paste operation
					existingNode = (String)PasteEdit.nodeList.get(sNodeID);
					if (existingNode != null) {
						newPasteNodeSummary = nodeService.getNodeSummary(session, existingNode);
						if (newPasteNodeSummary != null) {
							String sNewView = (String)PasteEdit.nodeList.get(sViewID);
							if (sNewView == null || sNewView.equals("")) //$NON-NLS-1$
								sNewView = sViewID;

							NodePosition oPos = model.getViewService().getNodePosition(session, sNewView, existingNode);
							if (oPos != null) {
								// ADD THE NODE IF NOT ALREADY THERE
								newNode = getUIList().getNode(existingNode);
								if (newNode == null) {
									newNode = thisView.addMemberNode(oPos);
								}
							}
							else {
								newNode = getUIList().getView().addNodeToView(newPasteNodeSummary, np.getXPos(), np.getYPos());
							}
						}
					}
					if (existingNode == null || (existingNode != null && newPasteNodeSummary == null)) {
						newNode = createNode(sNodeID,
								pasteNodeSummary.getType(),
								pasteNodeSummary.getOriginalID(),
								pasteNodeSummary.getAuthor(),
								pasteNodeSummary.getLabel(),
								pasteNodeSummary.getDetail(),
								np.getXPos(), np.getYPos());

						newPasteNodeSummary = newNode.getNode();
						int nodeType = pasteNodeSummary.getType();
						if ( nodeType == ICoreConstants.REFERENCE || nodeType == ICoreConstants.REFERENCE_SHORTCUT) {
							newPasteNodeSummary.setSource(pasteNodeSummary.getSource(), pasteNodeSummary.getImage(), sAuthor);
						}
						else if(View.isViewType(nodeType) ||
								View.isShortcutViewType(nodeType)) {

							newPasteNodeSummary.setSource("", pasteNodeSummary.getImage(), sAuthor); //$NON-NLS-1$
						}
						PasteEdit.nodeList.put(sNodeID,	newNode.getNode().getId());
					}
				}

				// IF NODE ALREADY RESTORED, DON'T TRY AND RESTORE IT AGAIN
				if (!ProjectCompendium.APP.ht_pasteCheck.containsKey(sNodeID)) {
					ProjectCompendium.APP.ht_pasteCheck.put(sNodeID, newNode.getNode().getId());

					if (newNode.getNode() instanceof View) {

						View deletedView2 = (View)pasteNodeSummary;
						View view = (View)newNode.getNode();
						UIViewFrame deletedUIViewFrame = ProjectCompendium.APP.getViewFrame(view, view.getLabel());
						if (View.isListType(view.getType()))
							((UIListViewFrame)deletedUIViewFrame).getUIList().getListUI().externalRestoreDeletedNodes(deletedView2);
						else {
							((UIMapViewFrame)deletedUIViewFrame).getViewPane().getUI().externalRestoreDeletedNodesAndLinks(deletedView2);
						}
					}
				}

				// DON'T ADD THE SAME NODE TWICE TO THE SAME LIST
				if (getUIList().getIndexOf(newNode.getNode()) == -1)
					getUIList().insertNode(newNode, (newNode.getYPos()/10) - 1);
			}
			getUIList().updateTable();
			deletedNodes.removeAllElements();
		}
		catch(Exception e) {
			e.printStackTrace();
			ProjectCompendium.APP.displayError("Error: (ListUI.restoreDeletedNodes)\n\n" + e.getLocalizedMessage()); //$NON-NLS-1$
		}
	}
}
