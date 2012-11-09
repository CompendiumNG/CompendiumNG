package com.compendium.ui.movie;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.concurrent.TimeUnit;

import javax.media.Controller;
import javax.media.ControllerEvent;
import javax.media.ControllerListener;
import javax.media.MediaTimeSetEvent;
import javax.media.Time;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JToolTip;
import javax.swing.Popup;
import javax.swing.PopupFactory;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;

import com.compendium.ProjectCompendium;
import com.compendium.ui.UIImages;
import com.sun.media.ui.ToolTip;
import com.sun.media.util.MediaThread;

public class UITimeLineBar extends JComponent 
	implements Runnable, ControllerListener, MouseListener, MouseMotionListener {
		
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** the colour of the grabber bar element */
	private Color	GRABBER_COLOUR = Color.black;

	/** The width of the grabber bar if drawing as a line.*/	
	private int grabberWidth=3;
	
	/** The current position to draw the grabber bar at.*/
	private int grabberPosition = 0;
	
	/** The rectangle that represent the grabber bar - for mouse events to use.*/	
	private Rectangle grabberRectangle = new Rectangle(0, 0);
	
	/** Whether the mouse in on the grabber bar or not - used by mouse events.*/
    private boolean bBarGrabbed = false;
    
    /** The width of the actual timeline area that represents the master time line.*/
    private int timelineWidth = 0;

    /** The tooltip that shows the current position of the mouse in the time space of the master time line.*/
    private JToolTip    	toolTip 		= null;

    /** The actual window displaying the popup.*/
    transient Popup tipWindow;

    /** The image for the grabber bar.*/
    //Image imageMiniGrabber;
    
    /** The width of the grabber bar.*/
    //int miniGrabberWidth;

    /** The parent panel to this bar.*/
    private UITimeLinesController controlPanel;
    
    /** The MasterTimer that this bar takes it's time from.*/
    private MasterTimer player;
    
    protected boolean justSeeked = false;
    protected boolean stopTimer = false;	    
    private MediaThread timer = null;
	private Integer localLock = new Integer(0);
    private boolean resetMediaTime 	= false;
    
    
    /**
     * The constructor
     * @param cp The parent class, the controller panel.
     * @param p the MasterTimer class.
     */
	public UITimeLineBar(UITimeLinesController cp, MasterTimer p) {	
		this.controlPanel = cp;
	   	this.player = p;
    	this.player.addControllerListener ( this );
		this.setOpaque(false);
   	    addMouseListener( this );
   	   	addMouseMotionListener( this );
   	   	
 		//ImageIcon iGrabberMini = new ImageIcon(UIImages.sPATH + "red-line.png");
    	//imageMiniGrabber = iGrabberMini.getImage();
    	//miniGrabberWidth = imageMiniGrabber.getWidth(this);
    	
        addComponentListener(new ComponentAdapter() {
    	    public void componentResized ( ComponentEvent event ) {
    	        Dimension dim = getSize();
    	        if (timelineWidth != dim.width) {
    	        	timelineWidth=dim.width;
    	        	repaint();
    	        }
    	    }
    	});
	}

	/**
	 * Paints the bar.
	 */
	public void paintComponent(Graphics g) {
		g.setColor( new Color(133,12,12) );
		g.drawLine(grabberPosition, 0, grabberPosition, this.getHeight());
		g.setColor( new Color(232,20,20) );
		g.drawLine(grabberPosition+1, 0, grabberPosition+1, this.getHeight());
		g.setColor( new Color(147,13,13) );
		g.drawLine(grabberPosition+2, 0, grabberPosition+2, this.getHeight());
		grabberRectangle = new Rectangle(grabberPosition, 0, grabberWidth, this.getHeight());

		
		/*g.drawImage(imageMiniGrabber, grabberPosition, 0, miniGrabberWidth, this.getHeight(), this);
		g.setColor( GRABBER_COLOUR );
		g.drawLine(grabberPosition, 0, grabberPosition+miniGrabberWidth, 0);
		g.drawLine(grabberPosition, this.getHeight()-1, grabberPosition+miniGrabberWidth-1, this.getHeight()-1);
		grabberRectangle = new Rectangle(grabberPosition, 0, miniGrabberWidth, this.getHeight());*/
		
		//g.fillRect(grabberPosition, 0, grabberWidth, this.getHeight());    	        	    	
		//grabberRectangle = new Rectangle(grabberPosition, 0, grabberWidth, this.getHeight());
	}
	
	/**
	 * Set the MasterTimer time to reflect the current location that the bar has been dragged to.
	 * @param pixels the current location of the bar in pixels.
	 */
    private void sliderSeek(long pixels) {
 		if (player == null)
		    return;
 		
		long millisvalue = (pixels*controlPanel.pixel_time_scale);					
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

    /**
     * Relocated the bar to the given time.
     * @param time the time that the bar needs to relocate to in Nanoseconds.
     */
    public void seek(long time) {
		if (justSeeked)
		    return;
		
		long timeMillis = TimeUnit.NANOSECONDS.toMillis(time);
		long newPosition = timeMillis/controlPanel.pixel_time_scale;

		newPosition = checkBounds(newPosition);
	    if (grabberPosition != newPosition || !isEnabled()) {
	    	grabberPosition = new Long(newPosition).intValue();		
	    	scrollRectToVisible(new Rectangle(grabberPosition-controlPanel.leftBorder, grabberRectangle.y, grabberRectangle.width+(controlPanel.leftBorder*2), grabberRectangle.height));
	    	repaint();
	    }
    }

    /**
     * Check that the given location is inside the time line.
     * @param x the location to check.
     * @return the located, adjusted if required.
     */
    public long checkBounds(long x) {
    	if (x < 0)
    		x = 0;
    	if (x > timelineWidth)
		    x = timelineWidth-1;

		return x;
    }
    
    /**
     * Mouse pressed event 
     * @param e the MouseEvent for this mouse press
     */
    public void mousePressed(MouseEvent e) {
		if (!isEnabled() || player == null) { 
		    return;
		}	   
		if (player.getState() == MasterTimer.Started) {
			controlPanel.stop();
		}
				
	    bBarGrabbed = false; 
	    Point newPoint = new Point(e.getX(), e.getY());		    
		if (grabberRectangle.contains(newPoint)) {
			bBarGrabbed = true;
		    this.setCursor(new Cursor(java.awt.Cursor.HAND_CURSOR));	
		    redispatchMouseExitedEvent(e);
		} else {
	    	redispatchMouseEvent(e);
		}
    }

    /**
     * Mouse released event 
     * @param e the MouseEvent for this mouse release
     */
   public synchronized void mouseReleased(MouseEvent e) {
	   if (!isEnabled() || player == null) { 
		    return;
	   }	   
	   if (player.getState() == MasterTimer.Started) {
		   controlPanel.stop();
	   }
		
	   moveToolTipToCurrent(e.getPoint());		
		
	    this.setCursor(new Cursor(java.awt.Cursor.DEFAULT_CURSOR));										
		if (bBarGrabbed) {
			grabberPosition = new Long(checkBounds(e.getX())).intValue();
			sliderSeek(grabberPosition);
			redispatchMouseExitedEvent(e);
		} else {
	    	redispatchMouseEvent(e);				
		}
		
		bBarGrabbed = false;
		repaint();		
    }
    
   /**
    * Mouse dragged event 
    * @param e the MouseEvent for this mouse drag
    */
   public synchronized void mouseDragged(MouseEvent e) {
	   if (!isEnabled() || player == null) { 
		   return;
	   }	   
	   if (player.getState() == MasterTimer.Started) {
		   controlPanel.stop();
	   }
	   	   
		if (bBarGrabbed) {
			grabberPosition = new Long(checkBounds(e.getX())).intValue();
			sliderSeek(grabberPosition);
			redispatchMouseExitedEvent(e);
		} else {
	    	redispatchMouseEvent(e);				
		}

	    repaint();
	    
	    //having this below the repaint fixes a pain issue on the Mac.
	    moveToolTipToCurrent(e.getPoint());		    							
    }
   
   /**
    * Mouse clicked event 
    * @param e the MouseEvent for this mouse click
    */
    public void mouseClicked(MouseEvent e) {    	
		if (!isEnabled() || player == null) { 
		    return;
		}	   
		if (player.getState() == MasterTimer.Started) {
			controlPanel.stop();
		}
				
		UITimeLineHeader header = controlPanel.getHeader();
		if (header != null) {
   			Point componentPoint = SwingUtilities.convertPoint(this, e.getPoint(), header);
   			if (header.contains(componentPoint)) {
				boolean isLeftMouse = SwingUtilities.isLeftMouseButton(e);		
				if (ProjectCompendium.isMac &&
					( (e.getButton() == 3 && e.isShiftDown()) ||
					 (e.getButton() == 1 && e.isControlDown()) )) {
					isLeftMouse = false;
				}		
				if (isLeftMouse && e.getClickCount() == 1) { 
					grabberPosition = new Long(checkBounds(e.getX())).intValue();
					sliderSeek(grabberPosition);
				}
   			}
    	}
		redispatchMouseEvent(e);
    }

    /**
     * Mouse entered event 
     * @param e the MouseEvent for this mouse enter
     */
    public synchronized void mouseEntered(MouseEvent e) {
		if (!isEnabled() || player == null)
		    return;
        
        if ( toolTip == null  &&  isEnabled() && player != null) {
            moveToolTipToCurrent(e.getPoint());
        }
    	    	
    	redispatchMouseEvent(e);	    	
    }

    /**
     * Mouse exited event 
     * @param e the MouseEvent for this mouse exit
     */
    public synchronized void mouseExited(MouseEvent e) {
		if (!isEnabled() || player == null)
		    return;
		
        if ( toolTip != null) {
            toolTip.setVisible(false);
            toolTip = null;
        	tipWindow.hide();
        	tipWindow = null;            
        }	        
    	redispatchMouseEvent(e);
    }

    /**
     * Mouse moved event 
     * @param e the MouseEvent for this mouse move
     */
    public synchronized void mouseMoved(MouseEvent e) {
		if (!isEnabled() || player == null)
		    return;

	    moveToolTipToCurrent(e.getPoint());		  		    	    
	    Point newPoint = new Point(e.getX(), e.getY());
		if (grabberRectangle.contains(newPoint)) {
		    this.setCursor(new Cursor(java.awt.Cursor.HAND_CURSOR));	
		    redispatchMouseExitedEvent(e);
		} else {
		    this.setCursor(new Cursor(java.awt.Cursor.DEFAULT_CURSOR));										
	    	redispatchMouseEvent(e);
		}
    }	
    
    /**
     * Re-dispatch the mouse Exited event to lower objects.
     * @param e the event to re-dispatch as a MOUSE_EXITED event.
     */
    private void redispatchMouseExitedEvent(MouseEvent e) {
    	Point point = e.getPoint();	    	
    	Component[] comps = controlPanel.oNodeTimeLinesPanel.getComponents();
    	for (int i=0; i< comps.length; i++) {
    		Component comp = comps[i];	    		
	    	if (comp != null) {
	   			Point componentPoint = SwingUtilities.convertPoint(
	   					this,
	   					point,
	   					comp);
	    			
	   			MouseEvent newExitedEvent = new MouseEvent(comp,
	   					MouseEvent.MOUSE_EXITED,
						e.getWhen(),
						e.getModifiers(),
						componentPoint.x,
						componentPoint.y,
						e.getClickCount(),
						e.isPopupTrigger());
	   			if (comp instanceof UITimeLineForNode) {
	   				UITimeLineForNode line = (UITimeLineForNode)comp;
   		   			line.mouseExited(newExitedEvent);
	  			} else if (comp instanceof UITimeLineForMovie) {
	   				UITimeLineForMovie line = (UITimeLineForMovie)comp;
   		   			line.mouseExited(newExitedEvent);
	 	   		} else {	  			
	 	   			comp.dispatchEvent(newExitedEvent);
	 	   		}
	    	}
    	}
    }
    
    /**
     * Re-dispatch a mouse event to lower objects.
     * @param e the mouse event to re-dispatch.
     */
    private void redispatchMouseEvent(MouseEvent e) {
    	//redispatchMouseExitedEvent(e);
    	
    	Point point = e.getPoint();	    	
    	Point containerPoint = SwingUtilities.convertPoint(
    			this,
    			point,
    			controlPanel.oNodeTimeLinesPanel);
   	
    	JComponent comp = (JComponent)controlPanel.oNodeTimeLinesPanel.findComponentAt(containerPoint);
    	if (comp != null) {
   			Point componentPoint = SwingUtilities.convertPoint(
   					this,
   					point,
   					comp);
 
   			MouseEvent newEvent = new MouseEvent(comp,
					e.getID(),
					e.getWhen(),
					e.getModifiers(),
					componentPoint.x,
					componentPoint.y,
					e.getClickCount(),
					e.isPopupTrigger());
   			
   			// dispatchEvent stopped working after refreshing lines from node label change.
   			// no idea why, but this is a work around.   			
   			if (comp instanceof UITimeLineForNode) {
   				UITimeLineForNode line = (UITimeLineForNode)comp;
   				switch(e.getID()) {
   					case MouseEvent.MOUSE_ENTERED: {
   			   			line.mouseEntered(newEvent);
   						break;
   					}
	   				case MouseEvent.MOUSE_EXITED: {
	   		   			line.mouseExited(newEvent);
	   					break;
	   				}   					
	   				case MouseEvent.MOUSE_MOVED: {
	   		   			line.mouseMoved(newEvent);
	   					break;
	   				}
	   				case MouseEvent.MOUSE_PRESSED: {
	   		   			line.mousePressed(newEvent);
	   					break;
	   				}
	   				case MouseEvent.MOUSE_DRAGGED: {
	   		   			line.mouseDragged(newEvent);
	   					break;
	   				}
	   				case MouseEvent.MOUSE_RELEASED: {
	   		   			line.mouseReleased(newEvent);
	   					break;
	   				}
	   				case MouseEvent.MOUSE_CLICKED: {
	   		   			line.mouseClicked(newEvent);
	   					break;
	   				}
	   				default: comp.dispatchEvent(newEvent);
 				}
   			} else if (comp instanceof UITimeLineForMovie) {
   				UITimeLineForMovie line = (UITimeLineForMovie)comp;
   				switch(e.getID()) {
					case MouseEvent.MOUSE_ENTERED: {
			   			line.mouseEntered(newEvent);
						break;
					}
	   				case MouseEvent.MOUSE_EXITED: {
	   		   			line.mouseExited(newEvent);
	   					break;
	   				}   					
	   				case MouseEvent.MOUSE_MOVED: {
	   		   			line.mouseMoved(newEvent);
	   					break;
	   				}
	   				case MouseEvent.MOUSE_PRESSED: {
	   		   			line.mousePressed(newEvent);
	   					break;
	   				}
	   				case MouseEvent.MOUSE_DRAGGED: {
	   		   			line.mouseDragged(newEvent);
	   					break;
	   				}
	   				case MouseEvent.MOUSE_RELEASED: {
	   		   			line.mouseReleased(newEvent);
	   					break;
	   				}
	   				case MouseEvent.MOUSE_CLICKED: {
	   		   			line.mouseClicked(newEvent);
	   					break;
	   				}
	   				default: comp.dispatchEvent(newEvent);
   				}
   			} else {
   				comp.dispatchEvent(newEvent);
   			}
    	}
    }
    	    
    /**
     * Format a time string.
     * @param time the milliseconds of seconds to format;
     * @return
     */
	private String formatTime ( long time ) {
		String strTime = ""; //$NON-NLS-1$
		long    hours=0;
		long    minutes=0;
		long    seconds=0;
		long    hours10=0;
		long    minutes10=0;
		long    seconds10=0;
		long 	milliseconds=0;

    	seconds = time/1000;	    	
    	milliseconds = time%1000;
    	
		if (seconds > 59) {
			minutes = seconds/60;
			seconds = seconds%60;
			if (minutes > 59) {
				hours = minutes/60;
				minutes = minutes%60;
			}
		}
        hours10 = hours / 10;
        hours = hours % 10;
        minutes10 = minutes / 10;
        minutes = minutes % 10;
        seconds10 = seconds / 10;
        seconds = seconds % 10;

        String milli = String.valueOf(milliseconds);
        if (milli.length() == 3) {
        	milli = "0"+milli; //$NON-NLS-1$
        } else if (milli.length() == 2) {
        	milli = "00"+milli;        	 //$NON-NLS-1$
        } else if (milli.length() == 1) {
        	milli = "000"+milli; //$NON-NLS-1$
        }
        
        strTime = new String ( "" + hours10 + hours + ":" + minutes10 + minutes + ":" + seconds10 + seconds + "." + milli ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		
        return ( strTime );
    }

	/**
	 * Relocate the tooltip text to the given location.
	 * Adjust location to screen.
	 * @param currentLocation the location to relocate the tooltip to.
	 */
    public void moveToolTipToCurrent(Point currentLocation) {	    	
        if (tipWindow != null) {
        	tipWindow.hide();
        	tipWindow = null;
        }
        if (this.isShowing() && player.getState() != MasterTimer.Started) {
            if (toolTip == null) {
                toolTip = this.createToolTip();
            }
	        Dimension dim = toolTip.getSize ();
            Point pointScreen = new Point(currentLocation);
            SwingUtilities.convertPointToScreen(pointScreen, this);
            pointScreen.x = pointScreen.x - dim.width - 5;
            pointScreen.y = pointScreen.y+5;	            
            toolTip.setLocation ( pointScreen );	  
            long value = (currentLocation.x*controlPanel.pixel_time_scale);
			String strTool = formatTime(value);	        
	        toolTip.setTipText(strTool);
            PopupFactory popupFactory = PopupFactory.getSharedInstance();
            tipWindow = popupFactory.getPopup(this, toolTip, pointScreen.x, pointScreen.y);
            tipWindow.show();
        }        
    }
        
    /********** RUNNABLE STUFF ************/
    
    /**
     * Creates the MediaThread timer that the runnable uses, and starts it.
     */
    public void addNotify() {
    	super.addNotify();
   	    timer = new MediaThread(this);
   	    timer.setName("UINodeTimeLine thread"); //$NON-NLS-1$
   	    timer.useControlPriority();
   	    stopTimer = false;
   	    timer.start();
    }


    // Cannot make removeNotify synchronized.  It will deadlock
    // with other mouse event listeners.  So we'll have to create
    // another lock to synchronize removeNotify and dispose.

    Object disposeLock = new Object();
    Object syncStop = new Object();
        
    /**
     * Stops the MediaTimer and hides the tooltip.
     */
    public void removeNotify() {
    	
    	if (timer != null) {
    	    synchronized (syncStop) {
    	    	stopTimer = true;
    	    	timer = null;
    	    }
    	}
    	synchronized (disposeLock) {
            if ( toolTip != null ) {
            	toolTip.setVisible( false );
            	tipWindow.hide();
            	tipWindow = null;
             }
    	}
    
    	super.removeNotify();
    }

    /**
     * Disposes of the timer for this runnable and removes listeners and the tooltip.
     */
    public synchronized void dispose() {
    	synchronized (syncStop) {
    		if (timer != null) {
    			stopTimer = true;
    		}
    	}
    	removeMouseListener( this );
    	removeMouseMotionListener( this );         
    	player.removeControllerListener ( this );		

		synchronized (disposeLock) {
			if (toolTip != null) {
				toolTip.setVisible(false);
				toolTip = null;
            	tipWindow.hide();
            	tipWindow = null;				
			}
		}
		timer = null;
    }

    /** 
     * The run method for this runnable.
     * Checks the master timer and moves the bar along as required.
     */
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
		    		       	seek(nanoTime);
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
    
    /**
     * Receives updates from the Master Timer. 
     * Listens for MediaTimeSetEvents only.
     */
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
}	
