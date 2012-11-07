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

import java.awt.*;
import java.awt.dnd.*;
import java.beans.*;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.TimeUnit;

import javax.media.Player;
import javax.swing.*;

import com.compendium.LanguageProperties;
import com.compendium.ProjectCompendium;
import com.compendium.ui.UIImages;
import com.compendium.ui.UINode;
import com.compendium.ui.UIViewFrame;
import com.compendium.ui.UIViewPane;
import com.compendium.ui.plaf.*;
import com.compendium.ui.popups.UIViewPopupMenu;
import com.compendium.core.datamodel.*;


/**
 * This class is the main class that draws and handles Compendium planner maps and their events.
 *
 * @author	Michelle Bachler
 */
public class UIMovieMapViewPane extends UIViewPane implements PropertyChangeListener {

	/** uinode added property for use with property change events */
	public	final static String		UINODE_ADDED	 	= "uinodeadded"; //$NON-NLS-1$

	/** uinode added property for use with property change events */
	public	final static String		UINODE_REMOVED 		= "uinoderemoved"; //$NON-NLS-1$

	/** uinode added property for use with property change events */
	public	final static String		UIMOVIE_ADDED	 	= "uimovieadded"; //$NON-NLS-1$

	/** uinode added property for use with property change events */
	public	final static String		UIMOVIE_REMOVED 	= "uimovieremoved"; //$NON-NLS-1$

	/** A reference to the layer to hold a movie. */
	public final static Integer MOVIE_LAYER 			= new Integer(240);	
	
	/** Holds the list of all movies in this pane.*/
	private Vector<UIMoviePanel>			vtMovies 	= new Vector<UIMoviePanel>(51);
	
	/** The hint label displayed when there are no movies added.*/
	private JLabel							oHint		= null;						

	/**
	 * Constructor. Creates and initializes a new instance of UIViewPane.
	 * @param view the view holding the data for this pane to display.
	 * @param viewFrame the parent frame containing this view pane
	 */
	public UIMovieMapViewPane(View view, UIViewFrame viewframe) {
		super(view, viewframe);
		
		oHint  = new JLabel(LanguageProperties.getString(LanguageProperties.MOVIE_BUNDLE, "UIMovieMapViewPane.movieHint")); //$NON-NLS-1$
		oHint.setFont(new Font("Dialog", Font.BOLD, 20)); //$NON-NLS-1$
		oHint.setForeground(Color.gray);
		oHint.setLocation(30,30);
		oHint.setSize(oHint.getPreferredSize());
		oHint.setVisible(false);
		add(oHint, MOVIE_LAYER);	
		
		checkToolTip();
	}		
		
	/**
	 * Check if the passed movie has a path and then process according to type.
	 * @param movie the Movie data object to process.
	 * @return A movie panel
	 */
	public UIMoviePanel processMovie(Movie movie) {
		String sLink = movie.getLink();
		UIMoviePanel panel = null;
		if (!sLink.equals("")) { //$NON-NLS-1$
			if (sLink.indexOf(".") != -1) { //UIImages.isMovie(sLink)) { //$NON-NLS-1$
				panel = addMovie(movie);
			} else if (sLink.indexOf(".") == -1) {  //$NON-NLS-1$
				//treat it as a stream
				panel = addGridMovie(movie);
			}
		} else {
			ProjectCompendium.APP.displayError(LanguageProperties.getString(LanguageProperties.MOVIE_BUNDLE, "UIMovieMapViewPane.errorEmptyPath")); //$NON-NLS-1$
		}
		if (panel != null) {
			panel.setName(sLink);
		}
		return panel;					
	}
	
	/**
	 * Add a background movie for this view from a grid stream.
	 *
	 * @param movie the Movie to add.
	 */
	public UIMoviePanel addGridMovie(Movie movie) {		
		String sMoviePath = movie.getLink();
		UIMoviePanel oMoviePanel = null;
		try {	        
			oMoviePanel = new UIMoviePanel(movie, (MovieMapView)oView, this);	
			Player player = oMoviePanel.createGridStreamPlayer(sMoviePath, 0, 1.0);				
			if (player != null) {
				vtMovies.addElement(oMoviePanel);				
				Dimension pref = oMoviePanel.getPreferredSize();
				int width = pref.width;
				int height = pref.height;		
				MovieMapView oMovieMapView = (MovieMapView)oView;
				MovieProperties props = movie.getStartingProperties();
				if (props != null) {
					width = props.getWidth();
					height = props.getHeight();
					if (width <= 0 && height <= 0) {
						oMovieMapView.updateMovieProperties(props.getId(), props.getMovieID(), props.getXPos(), props.getYPos(), pref.width, pref.height, props.getTransparency(), props.getTime());
						width = pref.width;
						height = pref.height;
					}
				}
				
				oMoviePanel.setSize(new Dimension(width, height));
				oMoviePanel.setLocation(props.getXPos(), props.getYPos());					
				oMoviePanel.setVisible(true);
				
				add(oMoviePanel, MOVIE_LAYER);	
				this.setPosition(oMoviePanel, 0);
				firePropertyChange(UIMOVIE_ADDED, null, oMoviePanel);	
			} else {
				oMoviePanel = null;
				// If the movie failed to load, delete the movie from the database.
				MovieMapView oMovieMapView = (MovieMapView)oView;
				oMovieMapView.deleteMovie(movie.getId());
			}
		} catch (ModelSessionException se) {
		    ProjectCompendium.APP.displayError(LanguageProperties.getString(LanguageProperties.MOVIE_BUNDLE, "UIMovieMapViewPane.errorUpdatingProperties")+"\n\n"+ se.getLocalizedMessage());			 //$NON-NLS-1$
		} catch (SQLException sql) {
		    ProjectCompendium.APP.displayError(LanguageProperties.getString(LanguageProperties.MOVIE_BUNDLE, "UIMovieMapViewPane.errorUpdatingProperties")+"\n\n"+ sql.getLocalizedMessage());			 //$NON-NLS-1$
		} catch(Exception e) {
			e.printStackTrace();
			System.out.flush();
		}
		
		checkToolTip();		
		return oMoviePanel;
	}
	
	/**
	 * Add a background movie for this view.
	 *
	 * @param movie the Movie to add.
	 */
	public UIMoviePanel addMovie(Movie movie) {
		String sMoviePath = movie.getLink();
		UIMoviePanel oMoviePanel = null;
		try {
			oMoviePanel = new UIMoviePanel(movie,(MovieMapView)oView, this);	
			Player player = oMoviePanel.createMoviePlayer(sMoviePath);
			if (player != null) {
				vtMovies.addElement(oMoviePanel);			
				Dimension pref = oMoviePanel.getPreferredSize();
				int width = pref.width;
				int height = pref.height;		
				MovieMapView oMovieMapView = (MovieMapView)oView;
				MovieProperties props = movie.getStartingProperties();
				int x=0;
				int y=0;
				if (props != null) {
					x = props.getXPos();
					y = props.getYPos();
					width = props.getWidth();
					height = props.getHeight();
					if (width <= 0 && height <= 0) {
						oMovieMapView.updateMovieProperties(props.getId(), props.getMovieID(), props.getXPos(), props.getYPos(), pref.width, pref.height, props.getTransparency(), props.getTime());
						width = pref.width;
						height = pref.height;
					}
				}
				
				oMoviePanel.setSize(new Dimension(width, height));
				oMoviePanel.setLocation(x, y);
				oMoviePanel.setVisible(true);
				
				add(oMoviePanel, MOVIE_LAYER);		
				this.setPosition(oMoviePanel, 0);
				firePropertyChange(UIMOVIE_ADDED, null, oMoviePanel);	
			} else {
				oMoviePanel = null;
				// If the movie failed to load, delete the movie from the database.
				MovieMapView oMovieMapView = (MovieMapView)oView;
				oMovieMapView.deleteMovie(movie.getId());
			}
		} catch (ModelSessionException se) {
		    ProjectCompendium.APP.displayError(LanguageProperties.getString(LanguageProperties.MOVIE_BUNDLE, "UIMovieMapViewPane.errorUpdatingProperties")+"\n\n"+ se.getLocalizedMessage());			 //$NON-NLS-1$
		} catch (SQLException sql) {
		    ProjectCompendium.APP.displayError(LanguageProperties.getString(LanguageProperties.MOVIE_BUNDLE, "UIMovieMapViewPane.errorUpdatingProperties")+"\n\n"+ sql.getLocalizedMessage());			 //$NON-NLS-1$
		} catch(Exception e) {
			e.printStackTrace();
			System.out.flush();
		}
		
		checkToolTip();		
		return oMoviePanel;
	}
	
	/**
	 * Remove a background movie for this view.
	 *
	 * @param movie the Movie to remove.
	 */
	public void removeMovie(String id) {
		if (vtMovies.size() > 0) {
			int count = vtMovies.size();
			for (int i =0; i<count; i++) {
				UIMoviePanel next = vtMovies.elementAt(i);
				if (next != null && next.getMovieData().getId().equals(id)) {
					next.getMediaPlayer().close();
					vtMovies.remove(next);
					remove(next);
					firePropertyChange(UIMOVIE_REMOVED, null, next);			
					break;
				}
			}
		}
		
		checkToolTip();		
	}

	/**
	 * Stop any running movies and close them
	 */
	public void closeMovies() {
		if (vtMovies.size() > 0) {
			int count = vtMovies.size();
			for (int i =0; i<count; i++) {
				UIMoviePanel oMoviePanel = vtMovies.elementAt(i);
				if (oMoviePanel != null) {
					oMoviePanel.stop();
					oMoviePanel.getMediaPlayer().close();
					oMoviePanel.clean();
				}
			}
		}
	}	
	
	/**
	 * Stop any running movies
	 */
	public void stopMovies() {
		if (vtMovies.size() > 0) {
			int count = vtMovies.size();
			for (int i =0; i<count; i++) {
				UIMoviePanel oMoviePanel = vtMovies.elementAt(i);
				if (oMoviePanel != null) {
					oMoviePanel.stop();
				}
			}
		}
	}

	/**
	 * Start any running movies
	 */
	public void startMovies() {
		if (vtMovies.size() > 0) {
			int count = vtMovies.size();
			for (int i =0; i<count; i++) {
				UIMoviePanel oMoviePanel = vtMovies.elementAt(i);
				if (oMoviePanel != null) {
					oMoviePanel.start();
				}
			}
		}
	}

	/**
	 * Reset movies to start
	 */
	public void resetMovies() {
		if (vtMovies.size() > 0) {
			int count = vtMovies.size();
			for (int i =0; i<count; i++) {
				UIMoviePanel oMoviePanel = vtMovies.elementAt(i);
				if (oMoviePanel != null) {
					oMoviePanel.reset();
				}
			}
		}
	}

	/**
	 * Return all the movie panels.
	 * @return all the movie panels.
	 */
	public Vector<UIMoviePanel> getMovies() {
		return vtMovies;
	}
	
	/**
	 * Return the movie panel for the given id.
	 * @param id the id of the movie whose panel to return
	 * @return the movie panel for the given id or null if not found.
	 */
	public UIMoviePanel getMovie(String id) {
		UIMoviePanel oMoviePanel = null;
		if (vtMovies.size() > 0) {
			int count = vtMovies.size();
			for (int i =0; i<count; i++) {
				UIMoviePanel next = vtMovies.elementAt(i);
				if (next != null && next.getMovieData().getId().equals(id)) {
					oMoviePanel = next;
					break;
				}
			}
		}
		
		return oMoviePanel;
	}
	
	/**
	 * Return the movie duration in milliseconds for the given id.
	 * @param id the id of the movie whose duration to return
	 * @return the movie duration in milliseconds for the given id or 0 if not found.
	 */
	public long getDuration(String id) {
		long duration = 0;
		UIMoviePanel oMoviePanel = null;
		if (vtMovies.size() > 0) {
			int count = vtMovies.size();
			for (int i =0; i<count; i++) {
				UIMoviePanel next = vtMovies.elementAt(i);
				if (next != null && next.getMovieData().getId().equals(id)) {
					duration = next.getDuration().getNanoseconds();
					duration = TimeUnit.NANOSECONDS.toMillis(duration);
					break;
				}
			}
		}
		
		return duration;
	}
	
	/**
	 * Listen for the MovieMapView having movie change events add/delete/change
	 */
	public void propertyChange(PropertyChangeEvent evt) {
	    String prop = evt.getPropertyName();
		Object source = evt.getSource();
	    Object newvalue = evt.getNewValue();
	    
	    if (source instanceof MovieMapView) {
	    	if (prop.equals(MovieMapView.MOVIEPROPERTIES_CHANGED_PROPERTY)) {
	    		MovieProperties props = (MovieProperties)newvalue;
	    		UIMoviePanel moviepanel = getMovie(props.getMovieID());
	    		if (moviepanel != null) {
	    			MovieProperties currentProps = moviepanel.getCurrentProperties();
	    			if (currentProps.getId().equals(props.getId())) {
	    				moviepanel.setCurrentProperties(props);
	    			}
	    		}
	    	}
	    	if (prop.equals(MovieMapView.MOVIE_CHANGED_PROPERTY)) {	    			    		
	    		Movie movie = (Movie)newvalue;
	    		UIMoviePanel moviepanel = getMovie(movie.getId());
	    		if (moviepanel != null) {
		    		Movie currentMovie = moviepanel.getMovieData();
		    		moviepanel.setMovieData(movie);		    		
	
		    		// If a movie link has changed, update the panel completely.
		    		if (!currentMovie.getLink().equals(movie.getLink())) {
	    				removeMovie(movie.getId());
	    				processMovie(movie);
		    		}	
	    		}
	    	}
	    	else if (prop.equals(MovieMapView.MOVIE_ADDED_PROPERTY)) {
	    		Movie movie = (Movie)newvalue;
	    		processMovie(movie);
	    	}
	    	else if (prop.equals(MovieMapView.MOVIE_REMOVED_PROPERTY)) {
	    		String sMovieID = (String)newvalue;
    			removeMovie(sMovieID);
	    	}
	    } else {
			super.propertyChange(evt);	    	
	    }
	}
	
	/**
	 * If there are no movie and no nodes add a tooltip to the viewpane to instruct user. 
	 */
	private void checkToolTip() {
		Component[] nodes = getComponentsInLayer((NODE_LAYER).intValue());
		if (vtMovies != null && vtMovies.size() == 0 && nodes.length == 0) {			
			showHint();
		} else {
			hideHint();
		}
	}
	
	public void showHint() {
		if (oHint != null) {
			oHint.setVisible(true);
		}
	}
	
	public void hideHint() {
		if (oHint != null) {
			oHint.setVisible(false);
		}
	}
	
	/**
	 * Adds a component to this view. Override to fire an event
	 *
	 * @param c the component to be added.
	 * @param constraints an object expressing layout constraints for this component.
	 * @see java.awt.LayoutManager
	 */
	public void add(Component c, Object constraints) {
		UINode oldnode = null;
		UINode node = null;
		if (c instanceof UINode) {
			node = (UINode)c;
			oldnode = (UINode)get(node.getNode().getId());
		}
		super.add(c, constraints);
		if (c instanceof UINode && oldnode == null) {
			checkToolTip();		
			firePropertyChange(UINODE_ADDED, oldnode, node);
		}		
	}

	/**
	 * Removes a component from this view. Override to fire an event
	 *
	 * @param c the component to be removed
	 * @see java.awt.LayoutManager
	 */
	public void remove(Component c) {
		super.remove(c);
		if (c instanceof UINode) {
			checkToolTip();		
			firePropertyChange(UINODE_REMOVED, null, (UINode)c);
		}
	}
	
	/**
	 * Create and display an instance of the right-click popup menu for this view.
	 * @param viewpaneui the ui object for this view required as a parameter for the popup.
	 * @param x the x position of the trigger event for this request. Used to calculate the popup x position.
	 * @param y the y position of the trigger event for this request. Used to calculate the popup y position.
	 */
	public void showPopupMenu(ViewPaneUI viewpaneui, int x, int y) {

		viewPopup = new UIMovieMapViewPopupMenu("View Popup menu", viewpaneui); //$NON-NLS-1$
		UIViewFrame viewFrame = oViewFrame;

		Dimension dim = ProjectCompendium.APP.getScreenSize();
		int screenWidth = dim.width - 70; //to accomodate for the scrollbar
		int screenHeight = dim.height-120; //to accomodate for the menubar...

		Point point = getViewFrame().getViewPosition();
		int realX = Math.abs(point.x - x)+20;
		int realY = Math.abs(point.y - y)+20;

		int endXCoordForPopUpMenu = realX + viewPopup.getWidth();
		int endYCoordForPopUpMenu = realY + viewPopup.getHeight();

		int offsetX = (screenWidth) - endXCoordForPopUpMenu;
		int offsetY = (screenHeight) - endYCoordForPopUpMenu;

		if(offsetX > 0)
			offsetX = 0;
		if(offsetY > 0)
			offsetY = 0;

		viewPopup.setCoordinates(realX+offsetX, realY+offsetY);
		viewPopup.setViewPane(this);
		viewPopup.show(viewFrame,realX+offsetX, realY+offsetY);
	}
		
	/**
	 * Create and display an instance of the right-click popup menu for this view.
	 * @param viewpaneui the ui object for this view required as a parameter for the popup.
	 * @param x the x position of the trigger event for this request. Used to calculate the popup x position.
	 * @param y the y position of the trigger event for this request. Used to calculate the popup y position.
	 * @param UIMoviePanel the movie the user was over when they opened the menu.
	 */
	public void showPopupMenu(ViewPaneUI viewpaneui, int x, int y, UIMoviePanel movie) {

		viewPopup = new UIMovieMapViewPopupMenu("View Popup menu", viewpaneui, movie); //$NON-NLS-1$
		UIViewFrame viewFrame = oViewFrame;

		Dimension dim = ProjectCompendium.APP.getScreenSize();
		int screenWidth = dim.width - 70; //to accomodate for the scrollbar
		int screenHeight = dim.height-120; //to accomodate for the menubar...

		Point point = getViewFrame().getViewPosition();
		int realX = Math.abs(point.x - x)+20;
		int realY = Math.abs(point.y - y)+20;

		int endXCoordForPopUpMenu = realX + viewPopup.getWidth();
		int endYCoordForPopUpMenu = realY + viewPopup.getHeight();

		int offsetX = (screenWidth) - endXCoordForPopUpMenu;
		int offsetY = (screenHeight) - endYCoordForPopUpMenu;

		if(offsetX > 0)
			offsetX = 0;
		if(offsetY > 0)
			offsetY = 0;

		viewPopup.setCoordinates(realX+offsetX, realY+offsetY);
		viewPopup.setViewPane(this);
		viewPopup.show(viewFrame,realX+offsetX, realY+offsetY);
	}
	
// DRAG AND DROP STUFF

    /**
	 * Override to stop movie before processing drop.
     * @param e the <code>DropTargetDropEvent</code>
     */
	public void drop(DropTargetDropEvent e) {
		((UIMovieMapViewFrame)oViewFrame).stopTimeLine();
		super.drop(e);
	}
	
// UI METHODS
	
  	/**
     * Returns the L&F object that renders this component.
     *
     * @return ViewPaneUI, the object that renders this component.
     */
  	public MovieMapViewPaneUI getUI() {
		return (MovieMapViewPaneUI)oViewPaneUI;
  	}

  	/**
     * Sets the L&F object that renders this component.
     * <p>CURRENTLY DOES NOTHING</p>
     * @param ui,  the MovieMapViewPaneUI L&F object.
     */
  	public void setUI(ViewPaneUI ui) {
		//    if ((ViewPaneUI)this.ui != ui) {
		//      super.setUI(ui);
		//      repaint();
		//    }
  	}

  	/**
     * Notification from the UIManager that the L&F has changed.
     * Replaces the current UI object with the latest version from the
     * UIManager.
     *
     * @see JComponent#updateUI
     */
  	public void updateUI() {
		oViewPaneUI = new MovieMapViewPaneUI(this);
		invalidate();
 	}

	/**
     * Returns the name of the L&F class that renders this component.
     *
     * @return "MovieMapViewPaneUI"
     * @see JComponent#getUIClassID
     * @see UIDefaults#getUI
     */
  	public String getUIClassID() {
		return "MovieMapViewPaneUI"; //$NON-NLS-1$
  	}
	
	/**
	 * Clean up the components and variables used by this class to help with garbage collection.
	 */
	public void cleanUp() {
		super.cleanUp();	
		((UIMovieMapViewFrame)oViewFrame).stopTimeLine();
	}
}