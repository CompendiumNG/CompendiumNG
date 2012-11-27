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

import javax.media.Player;
import javax.media.Control;
import javax.media.Controller;
import javax.media.ControllerEvent;
import javax.media.ControllerListener;
import javax.media.MediaTimeSetEvent;
import javax.media.Time;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import com.compendium.ProjectCompendium;
import com.compendium.core.ICoreConstants;
import com.compendium.core.datamodel.Model;
import com.compendium.core.datamodel.MovieProperties;
import com.compendium.core.datamodel.NodePosition;
import com.compendium.core.datamodel.NodePositionTime;
import com.compendium.core.datamodel.NodeSummary;
import com.compendium.core.datamodel.TimeMapView;
import com.compendium.ui.UIImages;
import com.compendium.ui.UILink;
import com.compendium.ui.UINode;
import com.compendium.ui.UIUtilities;
import com.sun.media.util.MediaThread;

import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

public class UITimeLineForNode extends JComponent 
		implements MouseListener, MouseMotionListener, 
				ComponentListener, PropertyChangeListener, Runnable, ControllerListener  {

	/** the colour of the grabber bar element */
	//private static final Color	GRABBER_COLOUR = Color.darkGray;
	
	/** the colour of the time span elements.*/
	private static final Color	SPAN_COLOUR = Color.darkGray;	

	/** the colour of the time span elements.*/
	private static final Color	SELECTED_SPAN_COLOUR = Color.yellow;
	
	/** at what distance inpixels from the grabber to implement the snap.*/
	private static final int SNAP_SENSITIVITY = 20;
	
    protected boolean justSeeked = false;
    protected boolean stopTimer = false;
    private MediaThread timer = null;
	private Integer localLock = new Integer(0);
    private boolean resetMediaTime 	= false;

    int width;
    int height;
    
    
    /** The icon that is used each end of a timespan fop grabbing and expanding the span.*/
    private ImageIcon iGrabberMini = null;
    int miniGrabberWidth;
    int miniGrabberHeight;

    boolean leftGrabber = false;
    boolean rightGrabber = false;
    boolean entered;
    boolean dragging = false;
    
    int leftBorder = 0;
    int rightBorder = 0;
    int sliderWidth;
    long pressedAt = 0;
    
    private TimeSpan currentSpan = null;
    private boolean		onSpan = false;
    
    private MasterTimer player;
    private UITimeLinesController controlPanel;
  
    private UINode		oNode				= null;
    private TimeMapView oTimeMapView			= null;
    private UIMovieMapViewPane  oViewPane		= null;
    
    private UINodeTimeLinePopupMenu popup		= null;
    
    private Vector<NodePositionTime> vtTimes = null;
    private Hashtable htTimeSpans = null;
    
    /** Holds the currently selected items in this timeline */
    private Hashtable<String, NodePositionTime> selectedItems = new Hashtable<String, NodePositionTime>();

    
    /** How many milliseconds of a second that a screen pixel represents. */
   private int pixel_time_scale = UITimeLinesController.DEFAULT_PIXEL_TIME_SCALE;        
    
    public UITimeLineForNode(UINode uinode, TimeMapView view, UITimeLinesController cp, MasterTimer p) {
    	this.oNode = uinode;
		//this.oNode.addPropertyChangeListener(this);  
		//NodePosition pos = uinode.getNodePosition();
		//pos.addPropertyChangeListener(this);
		
    	this.oViewPane = (UIMovieMapViewPane)oNode.getViewPane();
    	this.oTimeMapView = view;
		
    	//this.oTimeMapView.addPropertyChangeListener(this);    	
    	this.controlPanel = cp;
       	this.player = p;
    	//this.player.addControllerListener ( this );

       	NodeSummary node = uinode.getNode();
		Hashtable times = oTimeMapView.getTimesForNode(node.getId());
		vtTimes = new Vector<NodePositionTime>(times.size());
		for (Enumeration ex = times.elements(); ex.hasMoreElements();) {
			NodePositionTime nextTime = (NodePositionTime)ex.nextElement();
			vtTimes.add(nextTime);
		}
		
		Object[] sa = new Object[vtTimes.size()];
		vtTimes.copyInto(sa);
		List l = Arrays.asList(sa);		
		Collections.sort(l, new Comparator() {
			public int compare(Object o1, Object o2) {

				NodePositionTime data1 = (NodePositionTime)o1;
				NodePositionTime data2 = (NodePositionTime)o2;
				
				long s1 = data1.getTimeToShow();
				long s2 = data2.getTimeToShow();

				return  (new Double(s1).compareTo(new Double(s2)));
			}
		});		
        
		vtTimes.removeAllElements();
		vtTimes.addAll(l);		
		
    	int countj = vtTimes.size();
    	if (vtTimes.size() > 0) {
    		NodePositionTime nextTime = (NodePositionTime)vtTimes.elementAt(0);
    		long starttime = nextTime.getTimeToShow();
    		if (starttime > 0) {
    			uinode.setVisible(false);
    		} else {
    			uinode.setLocation(nextTime.getXPos(), nextTime.getYPos());
				uinode.updateLinks();		    			
    		}
    	}       	

    	// Load the images
		iGrabberMini = new ImageIcon(UIImages.sPATH+"grabbershort.gif"); //$NON-NLS-1$
    	miniGrabberWidth = iGrabberMini.getIconWidth();
    	miniGrabberHeight = iGrabberMini.getIconHeight();
    	
    	leftBorder = UITimeLinesController.TIMELINE_LEFT_OFFSET;
    	rightBorder = controlPanel.rightBorder;
    	
    	entered = false;
    	//this.height = 22;
    	
    	sliderWidth = this.width - leftBorder - rightBorder;
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
   	    timer.setName("UITimeLineForNode thread"); //$NON-NLS-1$
   	    timer.useControlPriority();

   	    this.oNode.addPropertyChangeListener(this);  
		this.oTimeMapView.addPropertyChangeListener(this);    	
		NodePosition pos = oNode.getNodePosition();
		pos.addPropertyChangeListener(this);
    	this.player.addControllerListener ( this );
 
    	addMouseListener( this );
    	addMouseMotionListener( this );
        this.addComponentListener ( this );

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
    	
		oNode.removePropertyChangeListener(this);  
		NodePosition pos = oNode.getNodePosition();
		pos.removePropertyChangeListener(this);
		oTimeMapView.removePropertyChangeListener(this);  
    	this.player.removeControllerListener ( this );

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
		    		    	setCurrentNodeView(currentTimeMilliseconds);
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
	            Thread.yield();
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

	    String prop = evt.getPropertyName();
		Object source = evt.getSource();
	    //Object oldvalue = evt.getOldValue();
	    Object newvalue = evt.getNewValue();
	    
	    if (source instanceof TimeMapView) {
	    	if (prop.equals(TimeMapView.TIME_ADDED_PROPERTY)) {
		    	NodePositionTime newtime = (NodePositionTime)newvalue;	    		
		    	if (newtime.getNode().getId().equals(oNode.getNode().getId())) {
		    		vtTimes.add(newtime);	    		
		    	} 
		    	
		    	// redraw main if required.
		    	long newPoint = newtime.getTimeToHide()/pixel_time_scale;
		    	if (newPoint > sliderWidth) {
		    		controlPanel.recalculateRequiredTimeline();
		    	}
	    	} else if (prop.equals(TimeMapView.TIME_CHANGED_PROPERTY)) {
		    	NodePositionTime newtime = (NodePositionTime)newvalue;	    		
	    		int count = vtTimes.size();	    		
	    		for (int i=0; i< count; i++) {
	    			NodePositionTime nextTime = (NodePositionTime)vtTimes.elementAt(i);
	    			if (nextTime.getId().equals(newtime.getId())) {
	    				vtTimes.remove(nextTime);
	    				vtTimes.add(newtime);
	    		    	long newPoint = newtime.getTimeToHide()/pixel_time_scale;
	    		    	if (newPoint > sliderWidth) {
	    		    		controlPanel.recalculateRequiredTimeline();
	    		    	}
	    				break;
	    			}	    			
	    		}
	    		
		    	//If an update happens while a selection is in place
		    	// update the object in the selectedItems list.
		    	if (selectedItems.containsKey(newtime.getId())) {
		    		selectedItems.put(newtime.getId(), newtime);		    		
		    	}	    		
	    	} 
	    	
	    	if (prop.equals(TimeMapView.TIME_REMOVED_PROPERTY)) {
	    		String id = (String)newvalue;
	    		int count = vtTimes.size();
	    		for (int i=0; i< count; i++) {
	    			NodePositionTime nextTime = (NodePositionTime)vtTimes.elementAt(i);
	    			if (nextTime.getId().equals(id)) {
	    				vtTimes.remove(nextTime);
    		    		//controlPanel.recalculateRequiredTimeline();
	    				break;
	    			}	    			
	    		}
	    		
		    	//If an update happens while a selection is in place
		    	// update the object in the selectedItems list.
		    	if (selectedItems.containsKey(id)) {
		    		selectedItems.remove(id);		    		
		    	}	    		
	    	}
	    	
 	    	repaint();
 	    	
	    } else if (source instanceof NodePosition) {	    
	    	// if the user moves the node then on mouse release, update the relevant time span
	    	if (prop.equals(NodePosition.POSITION_PROPERTY)) {
	    		refreshNodeTimeDialog();
	    	}
	    }
	}
	
	/**
	 * Refresh any open node dialog time panels, if required.
	 */
	public void refreshNodeTimeDialog() {
		Long time = new Double(player.getMediaTime().getNanoseconds()).longValue();
		long millitime = TimeUnit.NANOSECONDS.toMillis(time.longValue());
	   	int count = vtTimes.size();
    	if (count > 0) {
	    	for (int i=0; i < count; i++) {
				NodePositionTime nextTime = (NodePositionTime)vtTimes.elementAt(i);
	    		long starttime = nextTime.getTimeToShow();
	    		long stoptime = nextTime.getTimeToHide();
		   		if (millitime >= starttime && millitime < stoptime || (starttime == 0 && stoptime == 0)) {
					Point loc = oNode.getLocation();
					try {
						this.oTimeMapView.updateNodeTime(nextTime.getId(), oNode.getNode().getId(), nextTime.getTimeToShow(), nextTime.getTimeToHide(), loc.x, loc.y);
	    		    	oNode.refreshTimeDialog();
					} catch(Exception ex) {
						ex.printStackTrace();
					}				
	    			break;
	    		}
			}
    	}	
	}
	
	/**
	 * Return the UINode associated with this time line.
	 * @return
	 */
	public UINode getNode() {
		return this.oNode;
	}
	
	/**
	 * Jump to the first time this node is show.
	 */
	public void showFirstTime() {
		if (vtTimes.size() > 0) {
			NodePositionTime nextTime = (NodePositionTime)vtTimes.elementAt(0);
			long starttime = nextTime.getTimeToShow();
			long newPosition = starttime/pixel_time_scale;			    
			sliderSeek(newPosition);
		}
	}	
	    
    /**
     * Show/Hide nodes for the given time (in milliseconds)
     * @param time current time line time position in milliseconds
     */
    public void setCurrentNodeView(long time) {    	
    	int count = vtTimes.size();
    	boolean inVisiblePeriod = false;
    	int x=-1;
    	int y=-1;
    	
    	if (count == 0) {
    		inVisiblePeriod = true;
     	} else {
    		NodePositionTime currentTime = null; 
	    	for (int i=0; i < count; i++) {
				NodePositionTime nextTime = (NodePositionTime)vtTimes.elementAt(i);
	    		long starttime = nextTime.getTimeToShow();
	    		long stoptime = nextTime.getTimeToHide();
	    		int xPos = nextTime.getXPos();
	    		int yPos = nextTime.getYPos();
		   		if (time >= starttime && time < stoptime || (starttime == 0 && stoptime == 0)) {
		    		//System.out.println("setCurrent for="+nextTime.getNode().getLabel());
		    		//System.out.println("x="+nextTime.getXPos());
		    		//System.out.println("y="+nextTime.getYPos());
	    			inVisiblePeriod = true;
	    			currentTime = nextTime;
	    			x = xPos;
	    			y = yPos;
	    			break;
	    		}
			}
	    	
	    	/*if (inVisiblePeriod) {
    			oNode.refreshTimeDialog(currentTime);		
	    	} else {
    			oNode.refreshTimeDialog(null);		
	    	}*/
    	}	
    	
		if (inVisiblePeriod) {
			oNode.setVisible(true);
			for (Enumeration e = oNode.getLinks(); e.hasMoreElements();) {
				UILink link = (UILink)e.nextElement();
				UINode from = link.getFromNode();
				UINode to = link.getToNode();
				if (from.isVisible() && to.isVisible()) {
					link.setVisible(true);
				}
			}
			if (x !=-1 && y !=-1) {
				oNode.setLocation(x, y);
			}
			oNode.updateLinks();
		} else {
			oNode.setVisible(false);			
			for (Enumeration e = oNode.getLinks(); e.hasMoreElements();) {
				UILink link = (UILink)e.nextElement();
				link.setVisible(false);
			}
		}		
    }
	 
    /**
     * Paint this time line
     * @param g the graphics object to use to paint this component.
     */
    public void paintComponent(Graphics g) {

        int y = (getHeight() / 2) - 2;            
        g.setColor( getBackground().darker() );
    	g.drawRect (leftBorder, y, sliderWidth, 3);
    	g.setColor(getBackground());            
        g.draw3DRect(leftBorder, y, sliderWidth, 3, false);

    	if (isEnabled()) {
            
            int count = vtTimes.size();
			if (htTimeSpans != null) {
				htTimeSpans.clear();
			} else {
				this.htTimeSpans = new Hashtable(count);
			}
			
			int spanWidth = 0;
			for (int i=0; i<count; i++) {
				NodePositionTime nextTime = (NodePositionTime)vtTimes.elementAt(i);
				long starttime = nextTime.getTimeToShow();				
				int start = new Long(starttime/pixel_time_scale).intValue();			
						    
			    // If they have switch to a short timeline for example this could happen.
			    // So just don't paint spans outside the timeline timespan. 
			    if (start > sliderWidth) 
			    	start = sliderWidth;
			    if (start < 0)
			    	start = 0;
				
				long stoptime = nextTime.getTimeToHide();
				int stop = new Long(stoptime/pixel_time_scale).intValue();			
			    			    
			    if (stop > sliderWidth)
			    	stop = sliderWidth;
			    if (stop < 0)
			    	stop = sliderWidth;
			    
			    if (start > stop) {
			    	continue;
			    }
		        spanWidth = stop-start;
			    
			    if (this.selectedItems.containsKey(nextTime.getId())) {
			    	g.setColor( getBackground().darker() );
			    	g.drawRect (start+leftBorder, y-1, spanWidth, 6);
			    	g.setColor( SELECTED_SPAN_COLOUR );
				    g.fillRect(start+leftBorder+1, y, spanWidth-1, 4);	
			    } else {
			    	g.setColor( SPAN_COLOUR );
				    g.fillRect(start+leftBorder, y-1, spanWidth, 6);	
			    }
			    			    			    
				//if (spanWidth >= 16) {
			    	iGrabberMini.paintIcon(this, g, start+leftBorder, y-2);
			    	iGrabberMini.paintIcon(this, g, start+leftBorder+(stop-start)-miniGrabberWidth, y-2);
	    	    	//g.drawImage(imageMiniGrabber, start+leftBorder, y-2, this);
	    	    	//g.drawImage(imageMiniGrabber, start+leftBorder+(stop-start)-miniGrabberWidth, y-2, this);
				//}
	    	    
    	    	TimeSpan span = new TimeSpan(nextTime);	    	    		    	    	
	    	    span.setLeftGrabber(new Rectangle(start+leftBorder, y-2, miniGrabberWidth, miniGrabberHeight));
	    	    span.setRightGrabber(new Rectangle(start+leftBorder+(stop-start)-miniGrabberWidth, y-2, miniGrabberWidth, miniGrabberHeight));
			    
	    	    htTimeSpans.put(new Rectangle(start+leftBorder, y-1, stop-start, 6), span);	    	    	
			}				
    	}
    }

    //public Dimension getPreferredSize() {
    //	return new Dimension(super.getPreferredSize().width, UITimeLinesController.ROW_HEIGHT);
    //}

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
     
   /* public float sliderToSeek(int x) {
    	float s = (float)(x) / (float)(sliderWidth);
    	return s;
    }*/

    public int mouseToSlider(int x) {
    	if (x < leftBorder)
    		x = leftBorder;
    	if (x > this.width - rightBorder)
		    x = this.width - rightBorder;

    	x -= leftBorder;
		return x;
    }
    
    private TimeSpan isInTimeSpan(Point p) {
    	if (htTimeSpans != null) {
	    	for (Enumeration e = this.htTimeSpans.keys(); e.hasMoreElements();) {
	    		Rectangle nextRec = (Rectangle)e.nextElement();
	    		if (nextRec.contains(p)) {
	    			return (TimeSpan)htTimeSpans.get(nextRec);
	    		}
	    	}
    	}
    	return null;
    }
    
    public void mousePressed(MouseEvent e) {
		if (!isEnabled() || this.player == null || this.player.getState() == Player.Started)
		    return;
	 
		onSpan = false;
	    leftGrabber = false;
	    rightGrabber = false;
	    dragging = false;
	    
		// Find out where you have clicked.
		// In a time span, on the grabber, on the bar.	

		TimeSpan span = isInTimeSpan(e.getPoint());
		if (span != null) {
			//NodePositionTime time = span.getTime();
			onSpan = true;
			controlPanel.setCursor(new Cursor(java.awt.Cursor.HAND_CURSOR));												
	    	pressedAt = e.getX();

			if (span.getLeftGrabber().contains(e.getPoint())) {
				this.leftGrabber = true;
		    } else if (span.getRightGrabber().contains(e.getPoint())) {
		    	this.rightGrabber = true;
			}			    
			
			if (leftGrabber || rightGrabber) {
				//controlPanel.clearSelection();
				controlPanel.setCursor(new Cursor(java.awt.Cursor.E_RESIZE_CURSOR));
			} 
		    currentSpan = span;
		} /*else {
			onBar = true;
			grabberPosition = mouseToSlider(e.getX());
		    controlPanel.setCursor(new Cursor(java.awt.Cursor.DEFAULT_CURSOR));								
			repaint();
		}*/
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
		    if (onSpan && isLeftMouse && dragging){
				NodePositionTime time = currentSpan.getTime();
   				Long currenttime = new Double(player.getMediaTime().getNanoseconds()).longValue();
   				long millitime = TimeUnit.NANOSECONDS.toMillis(currenttime.longValue());
				if (leftGrabber || rightGrabber) {
					long show = time.getTimeToShow();
					long hide = time.getTimeToHide();			
	   				Point loc = oNode.getLocation();
					try {
						this.oTimeMapView.updateNodeTime(time.getId(), oNode.getNode().getId(), show, hide, loc.x, loc.y);
						setCurrentNodeView(millitime);					
					} catch(Exception ex) {
						ex.printStackTrace();
	   				}
				} else if (!leftGrabber && !rightGrabber) {
					if (selectedItems.containsKey(time.getId())) {
						this.controlPanel.dragComplete(time.getId());
					} else {
						long show = time.getTimeToShow();
						long hide = time.getTimeToHide();			
						Point loc = oNode.getLocation();
						try {
							this.oTimeMapView.updateNodeTime(time.getId(), oNode.getNode().getId(), show, hide, loc.x, loc.y);
							setCurrentNodeView(millitime);					
						} catch(Exception ex) {
							ex.printStackTrace();
						}						
					}
				}
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
			isLeftMouse = false;
		}
				
		dragging = true;
		if (onSpan && isLeftMouse && (leftGrabber || rightGrabber)) {
			int newPosition = mouseToSlider(e.getX());
			NodePositionTime time = currentSpan.getTime();
			long show = time.getTimeToShow();
			long hide = time.getTimeToHide();
			
			int leftGrabberCheck = currentSpan.leftGrabber.x+currentSpan.leftGrabber.width;
			int rightGrabberCheck = currentSpan.rightGrabber.x;
			if (leftGrabber && newPosition+currentSpan.leftGrabber.width > rightGrabberCheck) {
				newPosition = rightGrabberCheck-1-currentSpan.leftGrabber.width;
			} else if (rightGrabber && newPosition < leftGrabberCheck) {
				newPosition = leftGrabberCheck+3;
			}
			
			boolean snapTo = false;
			long millisecondsNew = (newPosition*pixel_time_scale);
			long milliseconds = snapToGrabber(newPosition);
			if (milliseconds != millisecondsNew) {
				snapTo = true;
			}

			if (milliseconds >= 0) {				
			    if (leftGrabber) {
					show = milliseconds;
			    } else if (rightGrabber) {
					hide = milliseconds;
				}			    
			    
			    if (!checkForOverlap(time.getId(), show, hide)) {
					if (leftGrabber) {
						time.setTimeToShow(show);
				    } else if (rightGrabber) {
						time.setTimeToHide(hide);
						//if (hide > sliderWidth) {
							this.controlPanel.recalculateRequiredTimeline();	
						//}
					} 
			    }
			}
			
			if (snapTo) {
				Point loc = oNode.getLocation();
				try {
					this.oTimeMapView.updateNodeTime(time.getId(), oNode.getNode().getId(), show, hide, loc.x, loc.y);
	   				Long currenttime = new Double(player.getMediaTime().getNanoseconds()).longValue();		    		    	
	   				long millitime = TimeUnit.NANOSECONDS.toMillis(currenttime.longValue());
					setCurrentNodeView(millitime);					
				} catch(Exception ex) {
					ex.printStackTrace();
				}
				leftGrabber = false;
				rightGrabber = false;
				onSpan = false;
				pressedAt = 0;				
			}
		} else if (onSpan && isLeftMouse && !leftGrabber && !rightGrabber) {
			// drag whole span along bar.
			int newPosition = mouseToSlider(e.getX());
			NodePositionTime time = currentSpan.getTime();
			long show = time.getTimeToShow();
			long hide = time.getTimeToHide();			
			long duration = hide-show;
			long offset = (newPosition-pressedAt);
			long millioffset = (offset*pixel_time_scale);
			pressedAt = newPosition;
			if (selectedItems.containsKey(time.getId())) {
				this.controlPanel.dragMove(time.getId(), millioffset);
			} else {
				long newShow = millioffset+show;
				if (newShow < 0) {
					newShow = 0;
				}
				long newHide = newShow+duration;				
			    if (!checkForOverlap(time.getId(), newShow, newHide)) {
			    	time.setTimeToShow(newShow);
			    	time.setTimeToHide(newHide);
			    }				
			}
		}	
	    repaint();
    }

    /**
     * Calculate if current position is near grabber, and if so
     * set time to grabber.
     * @param newPosition
     * @param grabberPosition
     * @return the milliseconds to set the time to.
     */
    public long snapToGrabber(int newPosition) {
    	long grabberTime = player.getMediaNanoseconds();
		long grabberTimeMillis = TimeUnit.NANOSECONDS.toMillis(grabberTime);
		long grabberPosition = grabberTimeMillis/pixel_time_scale;
		long milliseconds = (newPosition*pixel_time_scale);							

		NodePositionTime time = currentSpan.getTime();
		long show = time.getTimeToShow();
		long hide = time.getTimeToHide();
		long showPixels=show/pixel_time_scale;
		long hidePixels=hide/pixel_time_scale;
		
		boolean travellingTowards = false;
		boolean travellingRight = false;
		if (newPosition > pressedAt) {
			travellingRight = true;
		}
		if ((leftGrabber && newPosition > showPixels && grabberPosition > newPosition) || 
				(leftGrabber && newPosition < showPixels && grabberPosition < newPosition) ||
					(rightGrabber && newPosition > hidePixels && grabberPosition > newPosition) ||
						(rightGrabber && newPosition < hidePixels && grabberPosition < newPosition)) {
			travellingTowards = true;
		}		
		if (travellingTowards) {
			if ( (travellingRight && newPosition+SNAP_SENSITIVITY >= grabberPosition) ||
					(!travellingRight && newPosition-SNAP_SENSITIVITY <= grabberPosition)) {
				if (leftGrabber) {
					if (!checkForOverlap(time.getId(), grabberTimeMillis, hide)) {
						milliseconds = grabberTimeMillis;						
					}
				} else if (rightGrabber) {
					if (!checkForOverlap(time.getId(), show, grabberTimeMillis)) {
						milliseconds = grabberTimeMillis;
					}
				}
			}
		}
		return milliseconds;
    }
     
 	/**
 	 * Check to see if the passed time is inside another span or another span is inside it
 	 * In which case there is a overlap.
 	 * @param newTime the time to check
 	 * @return true if there is an overlap with another time span, else false;
 	 */
 	private boolean checkForOverlap(String id, long start, long stop) {
 		int count = vtTimes.size();
 		boolean overlap = false;		
 		for (int i=0; i<count; i++) {
 			NodePositionTime time = (NodePositionTime)vtTimes.elementAt(i);
 			if (!id.equals(time.getId())) {				
 				overlap = time.checkForOverlap(start, stop);
 				if (overlap) {
 					break;
 				}
 			}
 		}	
 		
 		return overlap;
 	}

 	public void showTimeDialog(NodePositionTime span) {
    	oNode.showTimeDialog(span);
    }
    
    public void delete(String sTime, String sNode) {
   		try {
   			oTimeMapView.deleteNodeTime(sTime, sNode);
   		} catch(Exception e) {
   			e.printStackTrace();
   		}
    }
    
    /**
     * Create a new short timespan at the given x position on the timeline.	    		
     * @param x
     */
    public void createNewSpan(int x) {
		Date date = new Date();
		int position = mouseToSlider(x);
		long milliseconds = (position*pixel_time_scale);							
		String id = Model.getStaticUniqueID();					
		try {
	    	long milliDefaultSpan = this.controlPanel.getDefaultNodeTimeSpanLength();
	    	if (!checkForOverlap(id, milliseconds, milliseconds+milliDefaultSpan)) {
	    		oTimeMapView.addNodeTime(oNode.getNode().getId(), milliseconds, milliseconds+milliDefaultSpan, oNode.getLocation().x, oNode.getLocation().y);    	    		
	    		repaint();
	    	}
		} catch(Exception ex) {
			ex.printStackTrace();
			ProjectCompendium.APP.displayError(ex.getLocalizedMessage());
		}    	
    }
    
    /**
     * Set the location of the node for the given span, to be the node's current location.
     * @param span the span to update the node location for.
     */
    // NOT CURRENTLY USED - LOCATION UPDATED DYNAMICALLY
    /*public void setCurrentSpanLocationToNode(NodePositionTime span) {
		Point p = oNode.getLocation();
		if (span != null) {
			try {
				oTimeMapView.updateNodeTime(span.getId(), span.getNode().getId(), span.getTimeToShow(), span.getTimeToHide(), p.x, p.y);
			} catch(Exception ex) {
				ex.printStackTrace();
				ProjectCompendium.APP.displayError(ex.getLocalizedMessage());
			}
		}
    }*/
    
    /**
     * Can all the selected items move the required amount?
     * @param changeValue the amount to check for the move
     * @return true if all the items can move, else false.
     */
    public synchronized boolean canAllMove(long changeValue) {
    	boolean canMove = true;
    	NodePositionTime time = null;	    
		for (Enumeration<NodePositionTime> bars = selectedItems.elements(); bars.hasMoreElements();) {
			time = (NodePositionTime)bars.nextElement();
			long show = time.getTimeToShow();
			long hide = time.getTimeToHide();			
			long duration = hide-show;
			long newShow = changeValue+show;
			if (newShow < 0) {
		    	canMove = false;
		    	break;
			}
			long newHide = newShow+duration;				
		    if (checkForOverlap(time.getId(), newShow, newHide)) {
		    	canMove = false;
		    	break;
		    }
		}
		
		return canMove;
   }
    
    /**
     * Update all selected items by the amount given in milliseconds
     */
    public synchronized void dragMove(String fromID, long changeValue) {
    	NodePositionTime time = null;	    
		if (changeValue != 0) {
	    	for (Enumeration<NodePositionTime> bars = selectedItems.elements(); bars.hasMoreElements();) {
				time = (NodePositionTime)bars.nextElement();
				long show = time.getTimeToShow();
				long hide = time.getTimeToHide();			
				long duration = hide-show;
				long newShow = changeValue+show;
				if (newShow < 0) {
					newShow = 0;
				}
				long newHide = newShow+duration;				
			    if (!checkForOverlap(time.getId(), newShow, newHide)) {
			    	time.setTimeToShow(newShow);
			    	time.setTimeToHide(newHide);
			    }
			}
		}
		repaint();
    }
 
    /**
     * Save the change to all selected items
     */
    public synchronized void dragComplete(String fromID) {
       	NodePositionTime time = null;	    
		for (Enumeration<NodePositionTime> bars = selectedItems.elements(); bars.hasMoreElements();) {
			time = (NodePositionTime)bars.nextElement();
			long show = time.getTimeToShow();
			long hide = time.getTimeToHide();			
			Point loc = oNode.getLocation();
			try {
				this.oTimeMapView.updateNodeTime(time.getId(), oNode.getNode().getId(), show, hide, loc.x, loc.y);
					Long currenttime = new Double(player.getMediaTime().getNanoseconds()).longValue();		    		    	
					long millitime = TimeUnit.NANOSECONDS.toMillis(currenttime.longValue());
				setCurrentNodeView(millitime);					
			} catch(Exception ex) {
				ex.printStackTrace();
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
		if (!isEnabled() || player == null) {
		    return;
		}
   	
    	controlPanel.setCursor(new Cursor(java.awt.Cursor.DEFAULT_CURSOR));
    	
		boolean isRightMouse = SwingUtilities.isRightMouseButton(e);
		boolean isLeftMouse = SwingUtilities.isLeftMouseButton(e);		
		if (ProjectCompendium.isMac &&
			( (e.getButton() == 3 && e.isShiftDown()) ||
			  (e.getButton() == 1 && e.isControlDown()) )) {
			isRightMouse = true;
			isLeftMouse = false;
		}		
		
   		int clickCount = e.getClickCount();
   		if (!e.isShiftDown()) {
			this.controlPanel.clearSelection();
   		}
   		
	    TimeSpan span = isInTimeSpan(e.getPoint());    	   		
  		if (isLeftMouse) {		
 	    	if (clickCount >= 2) {
 	    		if (span == null) { 
 	    			createNewSpan(e.getX());
 	    		} else {
 	    			showTimeDialog(span.getTime());
 	    		}
 	    	} else if (clickCount == 1) {
 	    		if (span != null && e.isShiftDown()) { 	    	
	   				if (!selectedItems.containsKey(span.getTime().getId())) {
	   					selectedItems.put(span.getTime().getId(), span.getTime());
	   				} else {
	   					selectedItems.remove(span.getTime().getId());
	   				}
	   				repaint();
 	    		} else if (span != null && 
	    			!span.getLeftGrabber().contains(e.getPoint()) && 
					!span.getRightGrabber().contains(e.getPoint())) {
    			
					controlPanel.setCursor(new Cursor(java.awt.Cursor.DEFAULT_CURSOR));
	    			
	    			// if you are on a span, move time position to start of span.
		    		long starttime = span.getTime().getTimeToShow();
					long newPosition = starttime/pixel_time_scale;			    
		    		sliderSeek(newPosition);
		    		
					oViewPane.setSelectedNode(null,ICoreConstants.DESELECTALL);
		    		oNode.setSelected(true);
					oViewPane.setSelectedNode(oNode,ICoreConstants.SINGLESELECT);				
		    	} 	
	    		else {
					oViewPane.setSelectedNode(null,ICoreConstants.DESELECTALL);
		    		oNode.setSelected(true);
					oViewPane.setSelectedNode(oNode,ICoreConstants.SINGLESELECT);
				}   
 	    	}
     	} else if (isRightMouse) {
      		if (span != null) {
      			if (popup != null) {
      				popup.setVisible(false);
      				popup = null;
      			}
      			//Point place = e.getLocationOnScreen();
 				Point place = SwingUtilities.convertPoint((Component)e.getSource(), e.getX(), e.getY(), ProjectCompendium.APP);	
      			popup = new UINodeTimeLinePopupMenu(this, span.getTime(), oNode.getNode().getId(), e.getPoint());
      			popup.show(ProjectCompendium.APP, place.x, place.y);      			
      		} else {
      			if (popup != null) {
      				popup.setVisible(false);
      				popup = null;
      			}
      			//Point place = e.getLocationOnScreen();
				Point place = SwingUtilities.convertPoint((Component)e.getSource(), e.getX(), e.getY(), ProjectCompendium.APP);	
     			popup = new UINodeTimeLinePopupMenu(this, null, oNode.getNode().getId(), e.getPoint());
      			popup.show(ProjectCompendium.APP, place.x, place.y);      			
      		}
    	}
   		
	    currentSpan = null;
	    onSpan = false;
    }

    public synchronized void mouseEntered(MouseEvent e) {
		//if (!isEnabled() || player == null || this.player.getState() == MasterTimer.Started)
		//    return;
    }

    public synchronized void mouseExited(MouseEvent e) {
		if (!isEnabled() || player == null || this.player.getState() == MasterTimer.Started)
		    return;

		//entered = false;
		//onBar = false;
		//onSpan = false;
		//currentSpan = null;
		//this.setCursor(new Cursor(java.awt.Cursor.DEFAULT_CURSOR));

		repaint();
    }

    public synchronized void mouseMoved(MouseEvent e) {
		if (!isEnabled() || player == null) {
			return;
		}

		Dimension    dim;
        Point        pointScreen;

        controlPanel.setCursor(new Cursor(java.awt.Cursor.DEFAULT_CURSOR));
		this.leftGrabber = false;
    	this.rightGrabber = false;
       
        TimeSpan span = isInTimeSpan(e.getPoint());
        if (span != null) {
        	//NodePositionTime time = span.getTime();
    		if (span.getLeftGrabber().contains(e.getPoint())) {
    			this.leftGrabber = true;
    	    } else if (span.getRightGrabber().contains(e.getPoint())) {
    	    	this.rightGrabber = true;
    		}			    
            
			if (leftGrabber || rightGrabber) {
				controlPanel.setCursor(new Cursor(java.awt.Cursor.E_RESIZE_CURSOR));
			} else {
				controlPanel.setCursor(new Cursor(java.awt.Cursor.HAND_CURSOR));
			}
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
    
    /**
     * This class represents a time span holding the left and right grabber rectangles 
     * and the associated NodePositionTime object. Used by mouse events.
     * @author Michelle Bachler 
     */
    private class TimeSpan {
    	private Rectangle leftGrabber = null;
    	private Rectangle rightGrabber = null;
    	private NodePositionTime oTime = null;
    	
    	TimeSpan(NodePositionTime time) {
    		oTime = time;
    	}
    	
    	public void setTime(NodePositionTime time) {
    		oTime = time;
    	}
    	
    	public NodePositionTime getTime() {
    		return oTime;
    	}
    	
    	public void setLeftGrabber(Rectangle left) {
    		leftGrabber = left;
    	}
    	
    	public Rectangle getLeftGrabber() {
    		return leftGrabber;
    	}

       	public void setRightGrabber(Rectangle right) {
    		rightGrabber = right;
    	}
    	
    	public Rectangle getRightGrabber() {
    		return rightGrabber;
    	}
    }
}


