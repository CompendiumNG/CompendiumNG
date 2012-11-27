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

import javax.media.Controller;
import javax.media.ControllerEvent;
import javax.media.ControllerListener;
import javax.media.MediaTimeSetEvent;
import javax.media.Player;
import javax.media.Time;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;

import com.compendium.ProjectCompendium;
import com.compendium.core.ICoreConstants;
import com.compendium.core.datamodel.Movie;
import com.compendium.core.datamodel.MovieMapView;
import com.compendium.core.datamodel.MovieProperties;
import com.compendium.core.datamodel.NodePositionTime;
import com.compendium.ui.UIImages;
import com.sun.media.util.MediaThread;

import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

public class UITimeLineForMovie extends JComponent 
		implements MouseListener, MouseMotionListener, 
				ComponentListener, PropertyChangeListener, Runnable, ControllerListener {
	
	/** the colour of the time span elements.*/
	private static final Color	SPAN_COLOUR = Color.darkGray;

	/** the colour of the time span elements.*/
	private static final Color	SELECTED_SPAN_COLOUR = Color.yellow;

    int width;        
    int leftBorder = 0;
    int rightBorder = 0;
    int sliderWidth;
    long pressedAt = 0;
    
    protected boolean justSeeked = false;
    protected boolean stopTimer = false;
    private MediaThread timer = null;
 	private Integer localLock = new Integer(0);
    private boolean resetMediaTime 	= false;

    private MovieProperties currentProps = null;
    //private boolean		onBar = false;
    private boolean		onProps = false;    
    private boolean 	onMovie = false;
    private boolean 	dragging = false;
    private Rectangle	movieRectangle = null;
    
    ImageIcon iGrabberMiniYellow = null;
	ImageIcon iGrabberMini = null;
	ImageIcon iMiniGrabberMain = null;	
    int miniGrabberWidth;
    int miniGrabberHeight;

    private UITimeLinesController controlPanel;
    private MasterTimer player;

    private MovieMapView oMovieMapView	= null;
    //private UIMovieMapViewPane  oViewPane 		= null;
    private UIMoviePanel oMoviePanel = null;
    private Movie oMovie = null;
    private UIMovieMapViewPane		oMovieMapViewPane = null;

    private UIMovieTimeLinePopupMenu popup = null;
    
    private Hashtable<Rectangle, MovieProperties> htTimeProps = null;
    
    /** Holds the currently selected items in this timeline */
    private Hashtable<String, Object> selectedItems = new Hashtable<String, Object>();
    
    /** How many Milliseconds of a second that a screen pixel represents. */
    private int pixel_time_scale = UITimeLinesController.DEFAULT_PIXEL_TIME_SCALE;        
    
    public UITimeLineForMovie(UIMoviePanel m, UIMovieMapViewPane pane, MovieMapView view, UITimeLinesController cp, MasterTimer p) {
    	this.oMovieMapViewPane = pane;
    	this.oMoviePanel = m;
    	this.oMovie = oMoviePanel.getMovieData();
    	this.controlPanel = cp;
       	this.player = p;
 
     	this.oMovieMapView = view;
     	
		//this.oMovieMapView.addPropertyChangeListener(this);    	
    	//this.addMouseListener( this );
    	//this.addMouseMotionListener( this );
        //this.addComponentListener ( this );
       	//this.player.addControllerListener ( this );

		this.iGrabberMini = new ImageIcon(UIImages.sPATH + "video-key-marker2.png"); //$NON-NLS-1$
 		this.iMiniGrabberMain = new ImageIcon(UIImages.sPATH + "grabbershortmain.gif");	 //$NON-NLS-1$
       	this.miniGrabberWidth = iGrabberMini.getIconWidth();
    	this.miniGrabberHeight = iGrabberMini.getIconHeight();
		this.iGrabberMiniYellow = new ImageIcon(UIImages.sPATH + "video-key-marker-yellow.png"); //$NON-NLS-1$
 
    	this.leftBorder = UITimeLinesController.TIMELINE_LEFT_OFFSET;
    	this.rightBorder = controlPanel.rightBorder;    	
    	this.sliderWidth = this.width - leftBorder - rightBorder;
    }
   
    /**
     * Set the new scale to use to draw this time line header, then  redraw it.
     * @param scale the scale to use - in hundreths of a second per pixel.
     */
    public void setScale(int scale) {
    	this.pixel_time_scale = scale;
    	setSize(controlPanel.timeline_length, getHeight());
      	setPreferredSize(new Dimension(controlPanel.timeline_length, getHeight()));
      	repaint();
    }
    
    //*********************** RUNNABLE RELATED STUFF ***************************/
    public void addNotify() {
    	super.addNotify();
   	    timer = new MediaThread(this);
   	    timer.setName("UITimeLineForMovie thread"); //$NON-NLS-1$
   	    timer.useControlPriority();
  	   	
		this.oMovieMapView.addPropertyChangeListener(this);    	
    	this.addMouseListener( this );
    	this.addMouseMotionListener( this );
        this.addComponentListener ( this );
       	this.player.addControllerListener ( this );

   	    stopTimer = false;
   	    timer.start();
    }

    // Cannot make removeNotify synchronized.  It will deadlock
    // with other mouse event listeners.  So we'll have to create
    // another lock to synchronize removeNotify and dispose.

    Object disposeLock = new Object();
    Object syncStop = new Object();
    
    public void removeNotify() {    	
    	if (timer != null) {
    	    synchronized (syncStop) {
    	    	stopTimer = true;
    	    	timer = null;
    	    }
    	}
    	
		oMovieMapView.removePropertyChangeListener(this);  
        removeComponentListener ( this );
    	removeMouseListener( this );
    	removeMouseMotionListener( this );         
    	player.removeControllerListener ( this );		    
    	super.removeNotify();
    }

    public synchronized void dispose() {
    	synchronized (syncStop) {
    		if (timer != null) {
    			stopTimer = true;
    		}
    	}
		timer = null;
    }

    public void run() {
    	int counter = 0;
    	int pausecnt = -1;
    	int sleepTime;
    	boolean doUpdate = true;

    	while (!stopTimer) {
    		try {
	    	    if ( player != null && player.getState() == Controller.Started) {
	    	    	doUpdate = true;
	    			pausecnt = -1;
	    	    } else if (player != null && pausecnt < 5) {
	    	    	pausecnt ++;
	    	    	doUpdate = true;
	    	    } else if ( resetMediaTime ) {
	    	    	doUpdate = true;
	    	    	resetMediaTime = false;
	    	    } else {
	    	    	doUpdate = false;
	    	    }
	    	  
	    	    try {
		    		if (doUpdate) {
	    		    	long nanoDuration = player.getDuration().getNanoseconds();	
		    		    if (nanoDuration >= 0) {
		    		    	long nanoTime = player.getMediaTime().getNanoseconds();
		    		    	long currentTimeMilliseconds = TimeUnit.NANOSECONDS.toMillis(nanoTime);
		    		    	setCurrentMovieProperties(currentTimeMilliseconds);
		    			}
		    		}
	    	    } catch (Exception e) { }
	
	    	    sleepTime = (isEnabled() ? 200 : 1000);
	
	    	    try { Thread.sleep(sleepTime); } catch (Exception e) {}
	    	  
	    	    counter++;
	    	    if (counter == 1000/sleepTime) {
	    	    	counter = 0;
	    	    }
	    	  
	    	    if (justSeeked) {
	    	    	justSeeked = false;
	    	    	try { Thread.sleep(1000); } catch (Exception e) {}
	    	    }
    		} catch (Exception e) {}
    	}
    }    
        
    public synchronized void controllerUpdate ( ControllerEvent event ) {
        
	    synchronized (localLock) {
	        if (player == null)
	        	return;
	            
	        if (event instanceof MediaTimeSetEvent) {
	            Thread.yield ();
				resetMediaTime = true;
	        } 
	    }        
    }
    
    //*********************** PROPERTY CHANGE LISTENER *************************/

	/**
	 * Handles property change events for the TimeMapView else calls super.
	 * @param evt, the associated PropertyChangeEvent object.
	 */
	public void propertyChange(PropertyChangeEvent evt) {

	    final String prop = evt.getPropertyName();
		final Object source = evt.getSource();
	    //Object oldvalue = evt.getOldValue();
	    final Object newvalue = evt.getNewValue();
	    
	    //Tried to add this thread to help repaint glitch when transition point removed.
	    Thread thread = new Thread("UITimeLineForMovie") { //$NON-NLS-1$
	    	public void run() {
			    if (source instanceof MovieMapView) {
			    	if (prop.equals(MovieMapView.MOVIEPROPERTIES_ADDED_PROPERTY) ||
			    			prop.equals(MovieMapView.MOVIEPROPERTIES_CHANGED_PROPERTY)) {
			    		
			    		MovieProperties props = (MovieProperties)newvalue;
				    	long newPoint = props.getTime()/pixel_time_scale;
				    	if (newPoint > sliderWidth) {
				    		controlPanel.recalculateRequiredTimeline();
				    	}					    	
				    	//If an update happens while a selection is in place
				    	// update the object in the selectedItems list.
				    	if (selectedItems.containsKey(props.getId())) {
				    		selectedItems.put(props.getId(), props);		    		
				    	}
			    		checkMovieProperties(TimeUnit.NANOSECONDS.toMillis(player.getMediaNanoseconds()));	
			    	} 	    	
			    	if (prop.equals(MovieMapView.MOVIEPROPERTIES_REMOVED_PROPERTY)) {
			    		String id = (String)newvalue;
			    		
				    	//If an update happens while a selection is in place
				    	// update the object in the selectedItems list.
				    	if (selectedItems.containsKey(id)) {
				    		selectedItems.remove(id);	
				    	}
			    	}
			    	
			    	if (prop.equals(MovieMapView.MOVIE_CHANGED_PROPERTY)) {
			    		Movie newMovie = (Movie)newvalue;
			    		if (newMovie.getId().equals(oMovie.getId())) {
			    			oMovie = newMovie;	   
			    			
					    	//If an update happens while a selection is in place
					    	// update the object in the selectedItems list.
					    	if (selectedItems.containsKey(oMovie.getId())) {
					    		selectedItems.put(oMovie.getId(), oMovie);		    		
					    	}
			    		}
			    	}
			    	
		 	    	repaint();
			    }
	    	}
	    };
	    thread.start();
	}
	
	public void refreshMovieDialog() {
		/*Long time = new Double(player.getMediaTime().getSeconds()).longValue();
	   	int count = vtTimes.size();
    	boolean inVisiblePeriod = false;
    	int x=-1;
    	int y=-1;
    	
    	if (count > 0) {
	    	for (int i=0; i < count; i++) {
				NodePositionTime nextTime = (NodePositionTime)vtTimes.elementAt(i);
	    		long starttime = nextTime.getTimeToShow();
	    		long stoptime = nextTime.getTimeToHide();
	    		int xPos = nextTime.getXPos();
	    		int yPos = nextTime.getYPos();
		   		if (time >= starttime && time < stoptime || (starttime == 0 && stoptime == 0)) {
					Point loc = oNode.getLocation();
					try {
						this.oMovieMapView.updateNodeTime(nextTime.getId(), oNode.getNode().getId(), nextTime.getTimeToShow(), nextTime.getTimeToHide(), loc.x, loc.y);
	    		    	//setCurrentNodeView(new Double(player.getMediaTime().getSeconds()).longValue());
	    		    	oNode.refreshTimeDialog(nextTime);
					} catch(Exception ex) {
						ex.printStackTrace();
					}				
	    			break;
	    		}
			}
    	}*/
	}
	
	/**
	 * Return the UINode associated with this timeline.
	 * @return
	 */
	public UIMoviePanel getMovie() {
		return this.oMoviePanel;
	}
 
    /**
     * Set the current properties that the movie should use and 
     * start or stop the movie if required.
     * @param time the current time to check against in milliseconds.
     */
    public void setCurrentMovieProperties(long time) {    	
       	// CHECK IF PROPERTIES NEED APPLYING
    	checkMovieProperties(time);
    	
    	//CHECK IF MOVIE NEEDS STARTING OR STOPING OR RESETTING
    	long startTime = oMovie.getStartTime();
    	long duration = TimeUnit.NANOSECONDS.toMillis(oMoviePanel.getDuration().getNanoseconds());
    	long stopTime = oMovie.getStartTime()+duration;
    	if (this.player.getState() == MasterTimer.Started) {
	    	if (time>= startTime && time < stopTime) {
	    		if (oMoviePanel.getMediaPlayer().getState() != Player.Started) {
   	    			oMoviePanel.start();  					
	    		}
	    	} else if (time >= stopTime) {
	       		if (oMoviePanel.getMediaPlayer().getState() == Player.Started) {
	    			oMoviePanel.stop();
	    		}
	    	} else if (time < startTime) {
	    		oMoviePanel.getMediaPlayer().setMediaTime(new Time(0));
	    	}
    	} 
    }
    
    /**
     * Set the current properties that the movie should use.
     * @param time the current time to check against in milliseconds.
     */
    public void checkMovieProperties(long millis) {
    	Vector<MovieProperties> vtProperties = oMovie.getProperties();
    	int count = vtProperties.size();
    	if (count == 1) {
    		return;
    		
    	}
    	long setTime = -1;
    	MovieProperties setProps = null;
   		MovieProperties currentProps = null; 
   		
    	for (int i=0; i < count; i++) {
    		currentProps = (MovieProperties)vtProperties.elementAt(i);
    		long nexttime = currentProps.getTime();
       		if(nexttime == millis) {    			
    			setProps = currentProps;
     			break;
    		} else if (nexttime < millis && nexttime > setTime) {
           		setTime = nexttime;
     			setProps = currentProps;
    		}
		}
    	
    	if (setProps != null) {
    		oMoviePanel.setCurrentProperties(setProps);
    	}
    }
    
    /**
     * Set the current media time of the associated movie.
     * @param millis the time in milliseconds to set.
     */
    public void setMovieTime(long millis) {
   		checkMovieProperties(millis);    		
	    
       	long startTime = oMovie.getStartTime();
    	long duration = TimeUnit.NANOSECONDS.toMillis(oMoviePanel.getDuration().getNanoseconds());
   		long stopTime = oMovie.getStartTime()+duration;

		if (oMoviePanel.getMediaPlayer().getState() != Player.Started) {
			long movieTime = millis-startTime;
	   		if (movieTime < 0) {
				movieTime = 0;
			} else if (movieTime > duration) {
				movieTime = duration;
			}
	    	long time = TimeUnit.MILLISECONDS.toNanos(movieTime);    	    	    	    	
	   		try {
	   			oMoviePanel.getMediaPlayer().setMediaTime(new Time(time));
	   		} catch(Exception e) {}
		}
    }
    
    /**
     * If the user has dragged the movie bar along.
     * Reset the movie play position to the current bar position if required.
     * Or to the start or end depending on bar.
     */
    private void checkMovieState() {
   		try {
	    	long currentPosition = player.getMediaNanoseconds();
	    	long currentPositionMillis = TimeUnit.NANOSECONDS.toMillis(currentPosition);
	    	long duration = TimeUnit.NANOSECONDS.toMillis(oMoviePanel.getDuration().getNanoseconds());
	    	if (currentPositionMillis > oMovie.getStartTime() && currentPositionMillis < oMovie.getStartTime()+duration) {
	    		long newPosition = currentPositionMillis-oMovie.getStartTime();
				oMoviePanel.getMediaPlayer().setMediaTime(new Time(TimeUnit.MILLISECONDS.toNanos(newPosition)));
			} else if (currentPositionMillis < oMovie.getStartTime()) {
				oMoviePanel.getMediaPlayer().setMediaTime(new Time(0));
			} else if (currentPositionMillis > oMovie.getStartTime()+duration) {
				oMoviePanel.getMediaPlayer().setMediaTime(new Time(oMoviePanel.getDuration().getNanoseconds()));
			}
   		} catch(Exception e) {}
   }
    
    /**
     * Start the movie if it should be playing.
     */
    public void startMovie(long time) {
    	long startTime = oMovie.getStartTime();
    	long duration = TimeUnit.NANOSECONDS.toMillis(oMoviePanel.getDuration().getNanoseconds());
    	long stopTime = oMovie.getStartTime()+duration;
    	if (time>= startTime && time < stopTime) {
    		if (oMoviePanel.getMediaPlayer().getState() != Player.Started) {
    			oMoviePanel.start();  					
    		}
    	}
    }
    
    /**
     * Stop the movie if it is playing.
     */
    public void stopMovie() {
   		if (oMoviePanel.getMediaPlayer().getState() == Player.Started) {
   			oMoviePanel.stop();
   		}
    }
	    
    public void paintComponent(Graphics g) {

		int            y;
        y = (getHeight() / 2) - 2;   
 
        // Draw main bar
        g.setColor( getBackground() );            
    	g.drawRect (leftBorder, y, sliderWidth, 3);            
        g.draw3DRect(leftBorder, y, sliderWidth, 3, false);

    	if (isEnabled()) {
             // draw movie bar
 			long duration = oMoviePanel.getDuration().getNanoseconds();			
			long millisecondsduration = (int) TimeUnit.NANOSECONDS.toMillis(duration);
			int spanWidth = new Long(millisecondsduration/pixel_time_scale).intValue();			
			long startTime = oMovie.getStartTime();
			int start = new Long(startTime/pixel_time_scale).intValue();			
			if (start < 0) {
				start = 0;
			} 

			if (this.selectedItems.containsKey(oMovie.getId())) {
				g.setColor( SELECTED_SPAN_COLOUR );	
			} else {
				g.setColor( SPAN_COLOUR );
			}
		    g.fillRect(start+leftBorder, y-1, spanWidth, 6);	
		    movieRectangle = new Rectangle(start+leftBorder, y-1, spanWidth, 6);
		    
		    Vector<MovieProperties> vtProperties = oMovie.getProperties();
 			int count = vtProperties.size();
			
			if (htTimeProps != null) {
				htTimeProps.clear();
			} else {
				this.htTimeProps = new Hashtable<Rectangle, MovieProperties>(count);
			}						
			
			for (int i=0; i<count; i++) {
				MovieProperties nextTime = (MovieProperties)vtProperties.elementAt(i);
				long starttime = nextTime.getTime();
				if (starttime < 0) {
					starttime = 0;
				} 
				int startProperties = new Long(starttime/pixel_time_scale).intValue();			

				if (starttime == 0) {
					iMiniGrabberMain.paintIcon(this, g, startProperties, y-2);
				} else {
					if (this.selectedItems.containsKey(nextTime.getId())) {
						iGrabberMiniYellow.paintIcon(this, g, startProperties, y-2);
					} else {
						iGrabberMini.paintIcon(this, g, startProperties, y-2);
					}
				}
				//leftBorder-(miniGrabberWidth/2)- minigrabberWidth = 8 so this led to painting -1 outside the canvas				
				htTimeProps.put(new Rectangle(startProperties, y-2, this.miniGrabberWidth, this.miniGrabberHeight), nextTime);
			}									
     	} 
    }

   // public Dimension getPreferredSize() {
   // 	return new Dimension(super.getPreferredSize().width, UITimeLinesController.ROW_HEIGHT);
   // }
    
	/**
	 * Set the MasterTimer time to reflect the current location that the bar has been dragged to.
	 * @param pixels the current location of the bar in pixels.
	 */
    private void sliderSeek(long pixels) {
 		if (player == null)
		    return;
 		
		long millisvalue = (pixels*pixel_time_scale);					
		long locationNano = TimeUnit.MILLISECONDS.toNanos(millisvalue);	
		justSeeked = true;
		if (locationNano >= 0) {
			long duration = player.getDuration().getNanoseconds();
			if (locationNano > duration) {
				locationNano = duration;
			}
			player.setMediaTime(new Time(locationNano));
			controlPanel.setMovieTimes(millisvalue);
		}
    }
    
    public int mouseToSlider(int x) {
    	if (x < leftBorder)
    		x = leftBorder;
    	if (x > this.width - rightBorder)
		    x = this.width - rightBorder;

    	x -= leftBorder;
		return x;
    }
    
    /**
     * Return true if the given point is in a property time point
     * @param p the point to check.
     * @return true if the given point is in a property time point, else false.
     */
    private MovieProperties isInTimeProps(Point p) {
    	MovieProperties props = null;
    	//long time = (p.x*pixel_time_scale);

    	if (htTimeProps != null) {
	    	for (Enumeration e = this.htTimeProps.keys(); e.hasMoreElements();) {
	    		Rectangle nextRec = (Rectangle)e.nextElement();
	    		if (nextRec.contains(p)) {
	    			props =  (MovieProperties)htTimeProps.get(nextRec);
	    		}
	    	}
    	}
    	return props;
    }
    
    public void mousePressed(MouseEvent e) {
		if (!isEnabled() || this.player == null || this.player.getState() == Player.Started)
		    return;
		
	    //onBar = false;
	    onProps = false;
	    onMovie = false;
	    dragging = false;
	    
    	MovieProperties props = isInTimeProps(e.getPoint());
		if (props != null) {
			if (props.getTime() != 0) {		
				onProps = true;
				controlPanel.setCursor(new Cursor(java.awt.Cursor.HAND_CURSOR));								
				currentProps = props;
			}
		} else if (movieRectangle.contains(e.getPoint())) {
			controlPanel.setCursor(new Cursor(java.awt.Cursor.HAND_CURSOR));								
	    	onMovie = true;
	    	pressedAt = e.getX();
		} else {
			//onBar = true;
			controlPanel.setCursor(new Cursor(java.awt.Cursor.DEFAULT_CURSOR));								
		}
   }

    public synchronized void mouseReleased(MouseEvent e) {
		if (!isEnabled() || player == null)
		    return;
		
		//boolean isRightMouse = SwingUtilities.isRightMouseButton(e);
		boolean isLeftMouse = SwingUtilities.isLeftMouseButton(e);		
		if (ProjectCompendium.isMac &&
			(e.getButton() == 3 && e.isShiftDown())) {
			//isRightMouse = true;
			isLeftMouse = false;
		}
		
		controlPanel.setCursor(new Cursor(java.awt.Cursor.DEFAULT_CURSOR));
		if (dragging) {
		    if (onMovie) {
		    	if (selectedItems.containsKey(oMovie.getId())) {
		    		this.controlPanel.dragComplete(oMovie.getId());
		    	} else {
			    	try {
			    		oMovieMapView.updateMovie(oMovie.getId(), oMovie.getLink(), oMovie.getMovieName(), oMovie.getStartTime());
			    	} catch(Exception ex) {
			    		System.out.println("Unable to update movie start time dues to\n\n:"+ex.getLocalizedMessage()); //$NON-NLS-1$
			    	}		    		
		    	}
		    }
			if (onProps && isLeftMouse) {
				if (selectedItems.containsKey(currentProps.getId())) {
					this.controlPanel.dragComplete(currentProps.getId());
				} else {
					try {
						this.oMovieMapView.updateMovieProperties(currentProps.getId(), currentProps.getMovieID(), currentProps.getXPos(), currentProps.getYPos(), currentProps.getWidth(), currentProps.getHeight(), currentProps.getTransparency(), currentProps.getTime());
					} catch(Exception ex) {
						ex.printStackTrace();
					}					
				}
		    	
			    currentProps = null;
			}
		}
		
		dragging = false;
		pressedAt = 0;
		repaint();		
    }
    
    public synchronized void mouseDragged(MouseEvent e) {
		if (!isEnabled() || player == null)
		    return;
		
		//boolean isRightMouse = SwingUtilities.isRightMouseButton(e);
		boolean isLeftMouse = SwingUtilities.isLeftMouseButton(e);		
		if (ProjectCompendium.isMac &&
			(e.getButton() == 3 && e.isShiftDown())) {
			//isRightMouse = true;
			isLeftMouse = false;
		}
		
		dragging = true;
		if (onMovie && isLeftMouse) {			
			int newPosition = mouseToSlider(e.getX());
			long oldStart = oMovie.getStartTime();
			long offset = (newPosition-pressedAt);
			long millioffset = (offset*pixel_time_scale);
			long newStart = millioffset+oldStart;
			if (newStart < 0) {
				newStart = 0;
				millioffset = 0;
			}
			pressedAt = newPosition;
			if (selectedItems.containsKey(oMovie.getId())) {
				this.controlPanel.dragMove(oMovie.getId(), millioffset);
			} else {
				oMovie.setStartTime(newStart);	
				checkMovieState();
				repaint();
			}
		} else if (onProps && isLeftMouse) {
			int newPosition = mouseToSlider(e.getX());
			long oldPosition = currentProps.getTime();
			long newPositionMillis = (newPosition*pixel_time_scale);
		    if (!checkForExisting(currentProps.getId(), newPositionMillis)) {								
			    long millioffset = newPositionMillis-oldPosition;
			    if (selectedItems.containsKey(currentProps.getId())) {
			    	this.controlPanel.dragMove(currentProps.getId(), millioffset);
			    } else {
			    	currentProps.setTime(newPositionMillis);
			    }
		    }
		}		
    }
     
 	/**
 	 * Check to see if the passed time is the same as another time.
 	 * @param time the time to check
 	 * @return true if it is the same as an existing span, else false;
 	 */
 	private boolean checkForExisting(String id, long time) {
 		int count = oMovie.getPropertiesCount();
 		Vector<MovieProperties> vtProperties = oMovie.getProperties();
 		boolean same = false;		
 		for (int i=0; i<count; i++) {
 			MovieProperties props = (MovieProperties)vtProperties.elementAt(i);
 			if (!id.equals(props.getId())) {				
 				if (props.getTime() == time) {
 					same = true;
 					break;
 				}
 			}
 		}	
 		
 		return same;
 	}

 	/**
 	 * Open the MovieProperties dialog.
 	 * @param props
 	 */
 	public void showMovieDialog(MovieProperties props, boolean onMovie) {
 		if (onMovie) {
 			((UIMovieMapViewFrame)oMovieMapViewPane.getViewFrame()).showMovieDialog(oMovie);
 		} else {
 			((UIMovieMapViewFrame)oMovieMapViewPane.getViewFrame()).showMovieDialog(props);
 		}
    }
    
    /**
     * delete the properties with the given id.
     * @param sMoviePropertiesID the id of the properties to delete.
     */
 	public void delete(String sMoviePropertiesID) {
   		try {
   			oMovieMapView.deleteMovieProperties(sMoviePropertiesID, oMovie.getId());
   		} catch(Exception e) {
   			e.printStackTrace();
   		}
    }   
    
    /**
     * Create a new MovieProperties object for this movie at the location given.
     * @param location
     */
    public MovieProperties createProps(long location) {
    	MovieProperties props = null;
    	long time = (location*pixel_time_scale);
    	try {
			props = oMovieMapView.addMovieProperties(oMovie.getId(), oMoviePanel.getX(), oMoviePanel.getY(), oMoviePanel.getWidth(), oMoviePanel.getHeight(), 1.0f, time);
    		showMovieDialog(props, onMovie);			
		} catch(Exception e) {
			System.out.println(e.getLocalizedMessage());
    	}
		return props;
    }    
 	
    /**
     * Can all the selected items move the required amount?
     * @param changeValue the amount to check for the move
     * @return true if all the items can move, else false.
     */
    public synchronized boolean canAllMove(long changeValue) {
    	boolean canMove = true;
    	
    	Object next = null;	    
		for (Enumeration<Object> bars = selectedItems.elements(); bars.hasMoreElements();) {
			next = (Object)bars.nextElement();
			if (next instanceof Movie) {
				Movie nextMovie = (Movie)next;
				long newStart = nextMovie.getStartTime()+changeValue;
				if (newStart < 0) {
					canMove = false;
					break;
				}
			} else if (next instanceof MovieProperties) {
				MovieProperties nextProp = (MovieProperties) next;
				long newValue = nextProp.getTime()+changeValue;
				if (checkForExisting(nextProp.getId(), newValue) || newValue < 0) {								
					canMove = false;
					break;
				}	
			}
		}
   	
    	return canMove;
    }
    
    /**
     * Update all selected items by the amount given in milliseconds
     */
    public synchronized void dragMove(String fromID, long changeValue) {
    	Object next = null;	    
		if (changeValue != 0) {
			for (Enumeration<Object> bars = selectedItems.elements(); bars.hasMoreElements();) {
				next = (Object)bars.nextElement();
				if (next instanceof Movie) {
					Movie nextMovie = (Movie)next;
					long newStart = nextMovie.getStartTime()+changeValue;
					if (newStart < 0) {
						changeValue = 0;
						break;
					} else {
						nextMovie.setStartTime(newStart);
					}
				}
			}
		}		
		if (changeValue != 0) {
			for (Enumeration<Object> bars = selectedItems.elements(); bars.hasMoreElements();) {
				next = (Object)bars.nextElement();
				if (next instanceof MovieProperties) {
					MovieProperties nextProp = (MovieProperties) next;
					long newValue = nextProp.getTime()+changeValue;
					if (!checkForExisting(nextProp.getId(), newValue)) {								
						nextProp.setTime(newValue);
					}
				}
			}
		}
		checkMovieState();
		repaint();
    }
 
    /**
     * Save the change to all selected items
     */
    public synchronized void dragComplete(String fromID) {
    	Object next = null;	    
		for (Enumeration<Object> bars = selectedItems.elements(); bars.hasMoreElements();) {
			next = (Object)bars.nextElement();
			if (next instanceof Movie) {
				Movie nextMovie = (Movie)next;
		    	try {
		    		oMovieMapView.updateMovie(nextMovie.getId(), nextMovie.getLink(), nextMovie.getMovieName(), nextMovie.getStartTime());
		    	} catch(Exception ex) {
		    		System.out.println("Unable to update movie start time dues to\n\n:"+ex.getLocalizedMessage()); //$NON-NLS-1$
		    	}
			} else if (next instanceof MovieProperties) {
				MovieProperties nextProp = (MovieProperties) next;
				try {
					this.oMovieMapView.updateMovieProperties(nextProp.getId(), nextProp.getMovieID(), nextProp.getXPos(), nextProp.getYPos(), nextProp.getWidth(), nextProp.getHeight(), nextProp.getTransparency(), nextProp.getTime());
				} catch(Exception ex) {
					ex.printStackTrace();
				}
			}
		}
		repaint();
    }
    	
    /** 
     * Clear all selected items
     */
    public void clearSelection() {
    	selectedItems.clear();
    }
    
    /**
     * Handle mouseClicked events. 
     * @param e the MouseEvent for this mouse click.
     */
    public void mouseClicked(MouseEvent e) {    	
		if (!isEnabled() || player == null)
		    return;
    	
    	controlPanel.setCursor(new Cursor(java.awt.Cursor.DEFAULT_CURSOR));
    	
		boolean isRightMouse = SwingUtilities.isRightMouseButton(e);
		boolean isLeftMouse = SwingUtilities.isLeftMouseButton(e);		
		if (ProjectCompendium.isMac &&
			( (e.getButton() == 3 && e.isShiftDown()) ||
			 (e.getButton() == 1 && e.isControlDown()) )) {
			isRightMouse = true;
			isLeftMouse = false;
		}		
		
		MovieProperties props = isInTimeProps(e.getPoint());    	
   		if (!e.isShiftDown()) {
			this.controlPanel.clearSelection();
   		}
		
   		int clickCount = e.getClickCount();
   		if (isLeftMouse) {		
 	    	if (clickCount >= 2) {
 	    		if (props != null) { 
 	    			showMovieDialog(props, onMovie);
 	    		} else {
	    			createProps(mouseToSlider(e.getX())); 	
 	    		}
 	    	} else if (clickCount == 1) {
 	    		if (e.isShiftDown()) {
 	    			if (props != null) {
						if (props.getTime() != 0) {		
		 					if (!selectedItems.containsKey(props.getId())) {
		 						selectedItems.put(props.getId(), props);
		 					} else {
		 						selectedItems.remove(props.getId());
		 					}
		 				}
	 	    		} else if (onMovie) {
		  				if (!selectedItems.containsKey(oMovie.getId())) {
		   					selectedItems.put(oMovie.getId(), oMovie);
		   				} else {
		   					selectedItems.remove(oMovie.getId());
		   				}
	 	    		}
 	    		} else {
 	    			if (props != null) {    			
		    			// if you are on a span, move time position to start of span.
			    		long starttime = props.getTime();
						long newPosition = starttime/pixel_time_scale;			    
			    		sliderSeek(newPosition);		    			
 	    			} else if (onMovie) {
		    			// if you are on the movie span, move time position to start of movie.
			    		long starttime = oMovie.getStartTime();
						long newPosition = starttime/pixel_time_scale;			    
			    		sliderSeek(newPosition);
 	    			}
 	    		} 
	    	} 	
      	} else if (isRightMouse) { // || (ProjectCompendium.isMac && (e.getModifiers() & MouseEvent.CTRL_MASK) != 0)) {
			if (popup != null) {
				popup.setVisible(false);
				popup = null;
			}
			//Point place = e.getLocationOnScreen();
			Point place = SwingUtilities.convertPoint((Component)e.getSource(), e.getX(), e.getY(), ProjectCompendium.APP);
			popup = new UIMovieTimeLinePopupMenu(this, props, oMovie.getId(), e.getPoint(), onMovie);
			popup.show(ProjectCompendium.APP, place.x, place.y);						
    	}

   		repaint();
  		
		onMovie = false;
	    currentProps = null;
	    onProps = false;
    }
 
    public synchronized void mouseEntered(MouseEvent e) {
		//if (!isEnabled() || player == null || this.player.getState() == MasterTimer.Started)
		//    return;
		/*entered = true;
  		repaint();*/
    }

    public synchronized void mouseExited(MouseEvent e) {
		//if (!isEnabled() || player == null || this.player.getState() == MasterTimer.Started)
		//    return;

		/*entered = false;		
		//onBar = false;
		onProps = false;
		currentProps = null;		
		this.setCursor(new Cursor(java.awt.Cursor.DEFAULT_CURSOR));
		*/
		//repaint();
    }

    public synchronized void mouseMoved(MouseEvent e) {
		if (!isEnabled() || player == null || this.player.getState() == MasterTimer.Started)
		    return;

		controlPanel.setCursor(new Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        MovieProperties props = isInTimeProps(e.getPoint());
		if (props != null) {
			if (props.getTime() != 0) {		
				controlPanel.setCursor(new Cursor(java.awt.Cursor.HAND_CURSOR));
			}
		} else if (movieRectangle.contains(e.getPoint())) {
			controlPanel.setCursor(new Cursor(java.awt.Cursor.HAND_CURSOR));								
	    } 
    }

    public void componentResized ( ComponentEvent event ) {
        Dimension dim = this.getSize();
        if ( dim.width - leftBorder - rightBorder < 1 )
            return;

        this.width = dim.width;
        sliderWidth = this.width - leftBorder - rightBorder;
        repaint();
    }

    public void componentMoved ( ComponentEvent event ) {}
    public void componentShown ( ComponentEvent event ) {}
    public void componentHidden ( ComponentEvent event ) {}
}


