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

import java.beans.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import java.sql.SQLException;
import java.util.Vector;

import javax.swing.*;
import javax.swing.undo.*;
import javax.swing.event.*;

import com.compendium.core.datamodel.*;
import com.compendium.core.ICoreConstants;

import com.compendium.ProjectCompendium;
import com.compendium.ui.dialogs.UINodeContentDialog;
import com.compendium.ui.movie.UIMovieMapViewFrame;
import com.compendium.ui.movie.UIMovieMapViewPane;

/**
 * This class if the base class for map and list frames.
 *
 * @author	Mohammed Sajid Ali / Michelle Bachler
 */
public class UIViewFrame extends JInternalFrame implements InternalFrameListener, PropertyChangeListener {

	/** The generated serial version ID */
	private static final long 		serialVersionUID 	= 5070162939415250356L;

	/** The view associated with this view frame.*/
	protected View					oView 				= null;

	/** the main content pane for this internal frame.*/
	protected Container				oContentPane		= null;

	/** A Reference to the scrollpane for the contents of this frame.*/
	protected JScrollPane			scrollpane			= null;

	/** A reference to the horizontal scrollbar for this frame.*/
	protected JScrollBar 			horizontalBar 		= null;

	/** A reference to the vertical scrollbar for this frame.*/
	protected JScrollBar			verticalBar			= null;

	/** A reference to the viewport of the scrollpane for this frame.*/
	protected JViewport				oViewport			= null;

	/** The undo manager used by this frame.*/
	protected UndoManager			oUndoManager		= null;

	/** The undo support instance used by this frame.*/
	protected UndoableEditSupport	oUndoSupport		= null;

	/** A list of views that where opened to get to this view.*/
	protected Vector				vtViewNavigationHistory	= null;

	/** The <code>UINodeContentDialog</code> instance for the view associated with this frame.*/
	protected UINodeContentDialog	contentDialog			= null;

	/** The user author name of the current user */
	protected String sAuthor = ""; //$NON-NLS-1$
	
	/**
	 * Constructor. Create a new instance of this class.
	 * @param title, the title for this frame.
	 */
	public UIViewFrame (String title) {
		this(null, title);
	}
	
	/**
	 * Constructor. Create a new instance of this class.
	 * @param view com.compendium.core.datamodel.View, the view associated with this frame.
	 */
	public UIViewFrame (View view) {
		this(view, ""); //$NON-NLS-1$
	}

	/**
	 * Constructor. Create a new instance of this class.
	 * @param view com.compendium.core.datamodel.View, the view associated with this frame.
	 * @param title, the title for this frame.
	 */
	public UIViewFrame (View view, String title) {

		super(title, true, true, true, true);
		
		if (view != null) {
			this.oView = view;		
		}
		this.sAuthor = ProjectCompendium.APP.getModel().getUserProfile().getUserName();
		
		// Stop a user dragging a window too far left, 
		// or up and loosing access to the frame title bar
		final UIViewFrame frame = this;
		addComponentListener(new ComponentAdapter() {
			public void componentMoved(ComponentEvent e) {
				if (frame.getX() < 0) {
					frame.setLocation(0, frame.getY());
				}
				if (frame.getY() < 0) {
					frame.setLocation(frame.getX(), 0);
				}
			}
		});
		
		view.addPropertyChangeListener(this);
		vtViewNavigationHistory = new Vector(10);

		setBackground(Color.white);
		this.addInternalFrameListener(this);

		oContentPane = getContentPane();
		oContentPane.setBackground(Color.white);

		// initialize the undo.redo system
		oUndoManager = new UndoManager();
		oUndoSupport = new UndoableEditSupport();
		oUndoSupport.addUndoableEditListener(new UndoAdaptor());
	}

	/**
     * Update frame top-upper icon when the skin has changed.
     */
    public void updateFrameIcon(){

   		if(oView.getId().equals(ProjectCompendium.APP.getInBoxID())) {
   			setFrameIcon(UIImages.get(IUIConstants.INBOX_SM));
    	}  	
   		else if (View.isListType(getView().getType())) {
  			setFrameIcon(UIImages.getNodeIcon(IUIConstants.LIST_SM_ICON));
		}
		else{
			setFrameIcon(UIImages.getNodeIcon(IUIConstants.MAP_SM_ICON));
      	}
 	}

	/**
	 * Set the navigation history list for this view.
	 * @param vtHistory the list of views opened to get to this view.
	 */
	public void setNavigationHistory(Vector vtHistory) {
		vtViewNavigationHistory = vtHistory;
		ProjectCompendium.APP.setViewHistory(vtViewNavigationHistory);
	}

	/**
	 * Get the navigation history list for this view.
	 * @return Vector the list of views opened to get to this view.
	 */
	public Vector getNavigationHistory() {
		return vtViewNavigationHistory;
	}

	/**
	 * Get the navigation history list for this view, including this view.
	 * Used for child views' navigation history lists.
	 * @return Vector the list of views opened to get to this view.
	 */
	public Vector getChildNavigationHistory() {
		Vector clone = (Vector)vtViewNavigationHistory.clone();
		clone.addElement(this);
		return clone;
	}

	/**
	 * Return the current reference to the content dialog for this view.
	 * @return UINodeContentDialog, the current reference to the content dialog for this view.
	 */
	public UINodeContentDialog getContentDialog() {
		return contentDialog;
	}

	/**
	 * Open the content dialog and select the Edit/Contents tab.
	 * @return UINodeContentDialog, the current reference to the content dialog for this view.
	 */
	public UINodeContentDialog showEditDialog() {
		return showContentDialog(UINodeContentDialog.CONTENTS_TAB);
	}

	/**
	 * Open the content dialog and select the Properties tab.
	 * @return UINodeContentDialog, the current reference to the content dialog for this view.
	 */
	public UINodeContentDialog showPropertiesDialog() {
		return showContentDialog(UINodeContentDialog.PROPERTIES_TAB);
	}

	/**
	 * Open the content dialog and select the View tab.
	 * @return UINodeContentDialog, the current reference to the content dialog for this view.
	 */
	public UINodeContentDialog showViewsDialog() {
		return showContentDialog(UINodeContentDialog.VIEW_TAB);
	}

	/**
	 * Open the content dialog and select the given tab.
	 *
	 * @param int tab, the tab on the dialog to select.
	 * @return UINodeContentDialog, the current reference to the content dialog for this view.
	 */
	protected UINodeContentDialog showContentDialog(int tab) {
		View view = getView();
		contentDialog  = new UINodeContentDialog(ProjectCompendium.APP, view, view, tab);
		contentDialog.setVisible(true);
		return contentDialog;
	}

	/**
	 * Return the scrollpane used in this frame.
	 * @return JScrollPane, the scrollpane used in this frame.
	 */
	public JScrollPane getScrollPane() {
		return scrollpane;
	}

	/**
	 * Scroll the pange to the top-left corner.
	 *
	 * @param repaint, whether to repaint the view after scrolling or not.
	 */
	public void scrollHome(boolean repaint) {
		setHorizontalScrollBarPosition(0, repaint);
		setVerticalScrollBarPosition(0, repaint);
	}

	/**
	 * Return the horizontal scrollbar position.
	 * @return int, the horizontal scrollbar position.
	 */
	public int getHorizontalScrollBarPosition() {
		return horizontalBar.getValue();
	}

	/**
	 * Set the horizontal scrollbar position.
	 * @param  pos, the horizontal scrollbar position.
	 * @param repaint, whether to repaint the view afterwards or not.
	 */
	public void setHorizontalScrollBarPosition(int pos, boolean repaint) {
		horizontalBar.setValue(pos);

		if (repaint)
			horizontalBar.repaint();
	}

	/**
	 * Return the vertical scrollbar position.
	 * @return int, the vertical scrollbar position.
	 */
	public int getVerticalScrollBarPosition() {
		return verticalBar.getValue();
	}

	/**
	 * Set the vertical scrollbar position.
	 * @param  pos, the vertical scrollbar position.
	 * @param repaint, whether to repaint the view afterwards or not.
	 */
	public void setVerticalScrollBarPosition(int pos, boolean repaint) {
		verticalBar.setValue(pos);

		if (repaint)
			verticalBar.repaint();
	}

	/**
	 * Return the scrollbar viewport.
	 * @return JViewPort, the scrollbar viewport.
	 */
	public JViewport getViewport() {
		return oViewport;
	}

	/**
	 * Set the view for this Frame.
	 * @param view com.compendium.core.datamodel.View, the view for this frame.
	 */
	public void setView(View view) {
		oView = view;
		oView.addPropertyChangeListener(this);

		repaint();
	}

	/**
	 * Return the view that is presented in this frame.
	 * @return View, the view that is presented in this frame.
	 */
	public View getView() {
		return oView;
	}

	/**
	 * Set the viewport view position.
	 * @param  p, the viewport view position.
	 */
	public void setViewPosition(Point p) {
		oViewport.setViewPosition(p);
	}

	/**
	 * Return the viewport view position.
	 * @return Point, the viewport view position.
	 */
	public Point getViewPosition() {
		return oViewport.getViewPosition();
	}
	
	/**
	 * Return the current user's author name.
	 * @return the current user's author name.
	 */
	public String getCurrentAuthor() {
		return sAuthor;
	}
	
	/**
	 * Subclasses must override this method to delete as required.
	 */
	public void deleteChildren(View childView) {
		
	}	
	
// UNDO/REDO METHODS

	/**
	 * Return the UndoableEditSupport instance associated with this frame.
	 * @return UndoableEditSupport, the UndoableEditSupport instance associated with this frame.
	 */
	public UndoableEditSupport getUndoListener() {
		return oUndoSupport;
	}

	/**
	 * Refreshes the undo/redo buttons with the last action performed.
	 * @see com.compendium.ui.ProjectCompendiumFrame#refreshUndoRedo
	 */
	public void refreshUndoRedo() {
		ProjectCompendium.APP.refreshUndoRedo(oUndoManager);
	}

	/**
	 * Helper class. Create so that the local <code>refreshUndoRedo</code> method will get invoked.
	 * @see #refreshUndoRedo
	 */
	private class UndoAdaptor implements UndoableEditListener {
		public void undoableEditHappened (UndoableEditEvent evt) {
			UndoableEdit edit = evt.getEdit();
			oUndoManager.addEdit(edit);
			refreshUndoRedo();
	    }
	}

	/**
	 * Undo the last edit operation and refresh.
	 * @see #refreshUndoRedo
	 */
	public void onUndo() {
		try {
			oUndoManager.undo();
			refreshUndoRedo();
		}
		catch(CannotUndoException ex) {}
	}

	/**
	 * Redo the last edit operation and refresh.
	 * @see #refreshUndoRedo
	 */
	public void onRedo() {
		try {
			oUndoManager.redo();
			refreshUndoRedo();
		}
		catch(CannotRedoException ex) {}
	}

	/**
	 * This is a helper method to handle the possible remote
	 * exception
	 */
	public static String getViewLabel(IView view) {
		String label = null;
		label = view.getLabel();

		if (label == null)
			label = ""; //$NON-NLS-1$
		return label;
	}

// INTERNAL FRAME EVENTS

    /**
     * Invoked when an internal frame is activated. Calls the parent frame activate window method.
	 * @see com.compendium.ui.ProjectCompendiumFrame#activateWindow
     * @see javax.swing.JInternalFrame#setSelected
     */
	public void internalFrameActivated(InternalFrameEvent e) {
		if (ProjectCompendium.APP != null) {
			ProjectCompendium.APP.activateWindow(this);

			if (vtViewNavigationHistory.size() > 0 || oView.getLabel().equals("Home Window")) { //$NON-NLS-1$
				ProjectCompendium.APP.setViewHistory(vtViewNavigationHistory);
			}
		}
	}

    /**
     * Invoked when a internal frame has been opened.
	 * HERE DOES NOTHING.
     * @see javax.swing.JInternalFrame#show
     */
	public void internalFrameOpened(InternalFrameEvent e) {}

    /**
     * Invoked when an internal frame has been closed.
	 * HERE DOES NOTHING.
     * @see javax.swing.JInternalFrame#setClosed
     */
	public void internalFrameClosed(InternalFrameEvent e) {}

    /**
     * Invoked when an internal frame is in the process of being closed.
     * Saves the view properties and remove the view from the desktop.
     * @see javax.swing.JInternalFrame#setDefaultCloseOperation
     */
	public void internalFrameClosing(InternalFrameEvent e) {

		if (ProjectCompendium.APP != null) {
			ProjectCompendium.APP.saveViewProperties(this);

			// Should not be necessary but, BUG_FIX
			ProjectCompendium.APP.removeView(getView());
			
			// To set the viewnode state.
			boolean read = false;
			boolean unread = false;
			int i = 0;
			if (getView() == ProjectCompendium.APP.getHomeView()){
				read = true;
			} else {
				Vector nodes = getView().getMemberNodes();
				for(; i < nodes.size(); i ++){
					NodeSummary node = (NodeSummary) nodes.get(i);
					int state = node.getState();
					if(state == ICoreConstants.UNREADSTATE){
						unread = true;
					} else if(state == ICoreConstants.READSTATE){
						read = true;
					}
				}
			}
			try {
				if((read && !unread) ||(i == 0)){
					getView().setState(ICoreConstants.READSTATE);
				} else {
					getView().setState(ICoreConstants.MODIFIEDSTATE);
				}
				
			} catch (SQLException e1) {
				e1.printStackTrace();
			} catch (ModelSessionException e1) {
				e1.printStackTrace();
			}
			
			// If this view was opened from a MovieMap that was playing.
			// restart the playing when this view is closed.
			Vector vtHistory = getNavigationHistory();
			if (vtHistory.size() > 0) {
				Object obj = vtHistory.elementAt(vtHistory.size()-1);
				if (obj instanceof UIMovieMapViewFrame) {
					UIMovieMapViewFrame frame = (UIMovieMapViewFrame)obj;
					if (frame.wasMoviePlaying()) {
						frame.startTimeLine(true);
					}
				}					
			}
		}
	}

    /**
     * Invoked when an internal frame is de-activated.
	 * HERE DOES NOTHING.
     * @see javax.swing.JInternalFrame#setSelected
     */
	public void internalFrameDeactivated(InternalFrameEvent e) {}

    /**
     * Invoked when an internal frame is de-iconified.
	 * HERE DOES NOTHING.
     * @see javax.swing.JInternalFrame#setIcon
     */
	public void internalFrameDeiconified(InternalFrameEvent e) {}

    /**
     * Invoked when an internal frame is iconified.
	 * HERE DOES NOTHING.
     * @see javax.swing.JInternalFrame#setIcon
     */
	public void internalFrameIconified(InternalFrameEvent e) {}

	/**
	 * Handle a PropertyChangeEvent.
	 * @param evt, the asspciated PropertyChangeEvent to handle.
	 */
	public void propertyChange(PropertyChangeEvent evt) {

	    String prop = evt.getPropertyName();
   		Object source = evt.getSource();
	    Object newvalue = evt.getNewValue();

		if (source instanceof NodeSummary) {

		    if (prop.equals(NodeSummary.LABEL_PROPERTY)) {
				setTitle((String)newvalue);
		    }
    	}

	    repaint();
	}

	/**
	 * Null class variables.
	 */
	public void cleanUp() {

		oContentPane	= null;
		scrollpane		= null;
		oViewport		= null;
		oUndoManager	= null;
		oUndoSupport	= null;
	}
}
