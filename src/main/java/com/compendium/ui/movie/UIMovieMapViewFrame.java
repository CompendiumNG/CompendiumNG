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

package com.compendium.ui.movie;

import java.beans.*;
import java.sql.SQLException;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.dnd.*;
import java.awt.geom.*;

import javax.swing.*;
import javax.swing.Popup;
import javax.swing.event.*;
import javax.swing.border.*;
import javax.swing.plaf.*;
import javax.help.*;
import javax.media.Player;

import com.compendium.ProjectCompendium;
import com.compendium.ui.UIImageButton;
import com.compendium.ui.UIImages;
import com.compendium.ui.UIMapViewFrame;
import com.compendium.ui.UIViewFrame;
import com.compendium.ui.UIViewPane;
import com.compendium.ui.plaf.*;
import com.compendium.core.*;
import com.compendium.core.datamodel.*;
import com.compendium.ui.dialogs.UIAerialDialog;
import com.compendium.ui.dialogs.UINodeContentDialog;
import com.sun.media.ui.ButtonComp;
import com.sun.media.ui.DefaultControlPanel;


/**
 * Has additional methods specifically for Map Frames.
 *
 * @author	Mohammed Sajid Ali / Michelle Bachler
 */
public class UIMovieMapViewFrame extends UIMapViewFrame {
    
	private UIMovieMapViewPane		oMovieMapViewPane = null;
	private MovieMapView			oMovieMapView = null;
	private JPanel					oMapPanel 	= null;
	private JSplitPane				oSplitPane 	= null;
	private UITimeLinesPanel		oTimeLinePanel = null;
	private boolean 				wasMoviePlaying = false;

	/**
	 * Constructor. Create a new instance of this class.
	 * @param view com.compendium.core.datamodel.View, the view associated with this frame.
	 */
	public UIMovieMapViewFrame (View view) {
		this(view, UIViewFrame.getViewLabel(view));
	}

	/**
	 * Constructor. Create a new instance of this class.
	 * @param view com.compendium.core.datamodel.View, the view associated with this frame.
	 * @param title, the title for this frame.
	 */
	public UIMovieMapViewFrame(View view, String title) {
		super(view, title);
		
		oMovieMapView = (MovieMapView)view;
		sBaseTitle = new String("[Movie Map]: "); //$NON-NLS-1$
		
		this.oContentPane.setLayout(new BorderLayout());
		this.oView = view;
		this.oSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);	
		this.oSplitPane.setOneTouchExpandable(true);
		this.oSplitPane.setDividerSize(10);
		this.oSplitPane.setContinuousLayout(true);
		
		addComponentListener(new ComponentAdapter() {
			 public void componentShown ( ComponentEvent event ) { 
				 int height = getHeight();
				 float position  = height * 0.75f;
				 oSplitPane.setDividerLocation(new Float(position).intValue());
			 }
		});    		
		
		// Must be done before UIMovieMapViewPane called as it needs controllerpanel.
		this.oMapPanel = new JPanel(new BorderLayout());		
		updateFrameIcon();

		// A Workaround since the scrollbar never sizes on the JLayeredPane for some reason
		// therefore created a panel and added the viewpane to it and finally added the panel
		// to the scrollpane
		// the setPreferredSize is for the scrollpane to resize. 
		// By overriding getPreferredSize in the JPanel, as the JScrollpane calls to find out how big
		// the JPanel is .
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.setPreferredSize(new Dimension(30000,30000));

		this.scrollpane = new JScrollPane(panel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
												 JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		(scrollpane.getVerticalScrollBar()).setUnitIncrement(50);
		(scrollpane.getHorizontalScrollBar()).setUnitIncrement(50);

		this.oViewport = scrollpane.getViewport();

		CSH.setHelpIDString(this,"node.planning"); //$NON-NLS-1$

		horizontalBar = scrollpane.getHorizontalScrollBar();
		verticalBar = scrollpane.getVerticalScrollBar();

		oMapPanel.add(scrollpane, BorderLayout.CENTER);					
		oSplitPane.setLeftComponent(oMapPanel);			
		oContentPane.add(oSplitPane, BorderLayout.CENTER);
		
		setTitle(title);

		this.oMovieMapViewPane = new UIMovieMapViewPane(view, this);		
		this.oViewPane = (UIViewPane)oMovieMapViewPane;
		panel.add(oMovieMapViewPane, BorderLayout.CENTER);

		oTimeLinePanel = new UITimeLinesPanel(oMovieMapViewPane);
		oSplitPane.setRightComponent(oTimeLinePanel);

		this.setVisible(true);				
	}
	
	/**
	 * Initialize and draw this frame.
	 * @param view the View associated with this frame.
	 */
	protected void init(View view) {
		// Moved all code to the constructor to get around a bug.
		// So just overriding method to do nothing.
	}
	
	public UIMovieMapViewFrame getFrame() {
		return this;
	}
		
	/**
	 * Return the time lines controller object
	 * @return
	 */
	public UITimeLinesController getController() {
		return oTimeLinePanel.getController();		
	}
	
	/**
	 * Is the move maps playing at present.
	 */
	public boolean isPlaying() {
		if (oTimeLinePanel.getController().getPlayerState() == Player.Started) {
			return true;
		}
		return false;
	}

	/**
	 * Stop the timeline if it is running.
	 */
	public void stopTimeLine() {
		stopTimeLine(false);
	}

	/**
	 * Stop the timeline if it is running.
	 * @param recordState true is you want this object to record the previous play state
	 */
	public void stopTimeLine(boolean recordState) {
		if (recordState) {
			wasMoviePlaying = true;
		}
		oTimeLinePanel.stop();
	}
		
	/**
	 * Start the timeline if it is running.
	 */
	public void startTimeLine() {
		startTimeLine(false);;
	}

	/**
	 * Start the timeline if it is running.
	 * @param recordState true is you want this object to reset the previous play state
	 */
	public void startTimeLine(boolean recordState) {
		if (recordState) {
			wasMoviePlaying = false;
		}
		oTimeLinePanel.start();
	}
		
	/**
	 * Jump to the time point when the node with the given id is first shown.
	 * @param sNodeID the id of the node to jump to.
	 */
	public void jumpToNode(String sNodeID) {
		oTimeLinePanel.jumpToNode(sNodeID);
	}
	
	/**
	 * Return the variable which says whether the movie was playing when the node was interacted with
	 * @return whether the movie was playing when the node was interacted with
	 */
	public boolean wasMoviePlaying() {
		return wasMoviePlaying;
	}
	
	
    /**
     * Invoked when a internal frame has been opened.
	 * Loads Movies.
     * @see javax.swing.JInternalFrame#show
     */
	public void internalFrameOpened(InternalFrameEvent evt) {
		for (Enumeration e = ((MovieMapView)oView).getMovies(); e.hasMoreElements();) {
			Movie movie = (Movie)e.nextElement();
			if (movie != null) {
				oMovieMapViewPane.processMovie(movie);
			}
		}
	}
		
    /**
     * Override to close/stop any movies playing.
     * @see javax.swing.JInternalFrame#setDefaultCloseOperation
     */
	public void internalFrameClosing(InternalFrameEvent e) {
		oMovieMapViewPane.closeMovies();
		super.internalFrameClosing(e);
	}	
	
	/**
	 * Open the content dialog and select the Movie tab.
	 * @return UINodeContentDialog the current reference to the content dialog for this view.
	 */
	public UINodeContentDialog showMovieDialog() {
		return showContentDialog(UINodeContentDialog.MOVIE_TAB);
	}
	
	/**
	 * Open the content dialog and select the given tab.
	 *
	 * @param props the MovieProperties that called this dialog.
	 * @return UINodeContentDialog, the current reference to the content dialog for this view.
	 */
	public UINodeContentDialog showMovieDialog(MovieProperties props) {
		View view = getView();
		contentDialog  = new UINodeContentDialog(ProjectCompendium.APP, view, view, UINodeContentDialog.MOVIE_TAB, props);
		contentDialog.setVisible(true);
		return contentDialog;
	}

	/**
	 * Open the content dialog and select the given tab.
	 *
	 * @param props the MovieProperties that called this dialog.
	 * @return UINodeContentDialog, the current reference to the content dialog for this view.
	 */
	public UINodeContentDialog showMovieDialog(Movie movie) {
		View view = getView();
		contentDialog  = new UINodeContentDialog(ProjectCompendium.APP, view, view, UINodeContentDialog.MOVIE_TAB, movie);
		contentDialog.setVisible(true);
		return contentDialog;
	}

	/**
	 * Override to open the content dialog and select the Movie tab.
	 * @return UINodeContentDialog the current reference to the content dialog for this view.
	 */
	public UINodeContentDialog showEditDialog() {
		return showContentDialog(UINodeContentDialog.MOVIE_TAB);
	}
	
	/**
	 * Null out class variables
	 */
	public void cleanUp() {
		super.cleanUp();
	}
}
