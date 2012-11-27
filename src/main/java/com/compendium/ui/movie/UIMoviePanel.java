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

import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;
import javax.swing.border.AbstractBorder;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Date;
import java.util.Vector;

import javax.media.CannotRealizeException;
import javax.media.Effect;
import javax.media.Manager;
import javax.media.MediaLocator;
import javax.media.NoDataSourceException;
import javax.media.NoPlayerException;
import javax.media.PackageManager;
import javax.media.Player;
import javax.media.PlugInManager;
import javax.media.Time;
import javax.media.control.FramePositioningControl;
import javax.media.protocol.DataSource;
import javax.media.renderer.video.RGBRenderer;

import net.crew_vre.media.Misc;

import com.compendium.LanguageProperties;
import com.compendium.ProjectCompendium;
import com.compendium.core.CoreUtilities;
import com.compendium.core.datamodel.Model;
import com.compendium.core.datamodel.ModelSessionException;
import com.compendium.core.datamodel.Movie;
import com.compendium.core.datamodel.MovieMapView;
import com.compendium.core.datamodel.MovieProperties;

public class UIMoviePanel extends JComponent implements MouseListener, MouseMotionListener, 
										KeyListener, FocusListener {

	/** A reference to the selected property for PropertyChangeEvents.*/
    public static final String SELECTED_PROPERTY 	= "movieselected"; //$NON-NLS-1$
    
	/** A reference to the rollover property for PropertyChangeEvents.*/
    public final static String ROLLOVER_PROPERTY	= "moviesrollover"; //$NON-NLS-1$
    
    public static final int		BORDER_WIDTH		= 6;
	    
    // The multiplier for the large size
    private static final int LARGE_SIZE_MULTIPLIER = 2;

    // The divider for the small size
    private static final int SMALL_SIZE_DIVIDER = 2;

    // The default width of the video
    private static final int DEFAULT_WIDTH = 352;

    // The offset of the key pressed
    private static final int KEY_OFFSET = 5;

   // The width to remove for key '4'
    private static final int NEGATIVE_MULTIPLIER_WIDTH = 59;

    // The width to add for key '6'
    private static final int POSITIVE_MULTIPLIER_WIDTH = 118;

    // The height to remove for key '4'
    private static final int NEGATIVE_MULTIPLIER_HEIGHT = 48;

    // The width to add for key '6'
    private static final int POSITIVE_MULTIPLIER_HEIGHT = 96;
    
	/** Is this movie currently selected?*/
	private boolean			bSelected					= false;
	
	/** Has the mouse rolled over this movie*/
	private boolean			bRollover					= false;
	
	private Component oMovieControls = null;
	
	private Component oVideo = null;

	private FramePositioningControl fpc = null;
	
	private Player mediaPlayer = null;
	
	/** The duration of this movie.*/
	private Time duration = null;
	
	/** The data object holding the map info on this Movie*/
	private Movie	oMovie = null;
	
	/** The map data object this movie is in.*/
	private MovieMapView oMovieMapView = null;
	
	/** The map ui object this movie is in.*/
	private UIMovieMapViewPane oMovieMapViewPane = null;
	
	private boolean		inTopLeftSquare = false;
	private boolean		inTopRightSquare = false;
	private boolean		inBottomLeftSquare = false;
	private boolean		inBottomRightSquare = false;
	    
	private Dimension movieSize = null;    
    private int videoWidth = 0;
    private int videoHeight = 0;
    
    private int 					lastMousePosX		= 0;
    private int						lastMousePosY		= 0;
            
    //private RGBRenderer renderer = new RGBRenderer(new Effect[]{});

    private DataSource dataSource = null;
    
    private MovieProperties currentProps = null;
        
	public UIMoviePanel(Movie movie, MovieMapView view, UIMovieMapViewPane pane) {
		
		Vector<String> prefixes = new Vector<String>(); 
		prefixes.add("com.omnividea");  //$NON-NLS-1$
		prefixes.addAll(PackageManager.getProtocolPrefixList());
		PackageManager.setProtocolPrefixList(prefixes);

		//You can then add the fobs codecs and demultiplexer:
		try {
			Misc.addDemultiplexer(com.omnividea.media.parser.video.Parser.class);
			Misc.addCodec(com.omnividea.media.codec.video.NativeDecoder.class);
			Misc.addCodec(com.omnividea.media.codec.audio.NativeDecoder.class);
			Misc.addCodec(com.omnividea.media.codec.video.JavaDecoder.class);
		} catch(Exception e) {
			
		}		
		
		oMovie = movie;
		currentProps = movie.getStartingProperties();
		oMovieMapView = view;
		oMovieMapViewPane = pane;
		addMouseListener(this);
		addMouseMotionListener(this);
		addKeyListener(this);
		addFocusListener(this);
		
		setLayout( new BorderLayout() );
		setBorder(new MovieBorder(BORDER_WIDTH, this));
		
		// Use lightweight components for Swing compatibility
		Manager.setHint( Manager.LIGHTWEIGHT_RENDERER, true );
	}
			
	/**
	 * Create a new Player and load the Movie.
	 * @param movieFile the path to the movie to load.
	 * @return the new Player object, or null if there was an error.
	 */
	public Player createMoviePlayer(String movieFile) {
		URL url = null;
		try {
			if (CoreUtilities.isFile(movieFile)) {
				File file = new File(movieFile);
				url = file.toURL();
			} else {
				url = new URL(movieFile);
		    }

			// create a player to play the media specified in the URL
			mediaPlayer = Manager.createRealizedPlayer( url );			
			duration = mediaPlayer.getDuration();
			
			//So shows first frame when map opened.Else if just gray square.
			mediaPlayer.prefetch();
			
			// get the components for the video and the playback controls
			oVideo = mediaPlayer.getVisualComponent();
			if (oVideo != null) {
				movieSize = oVideo.getPreferredSize();
				videoWidth = movieSize.width;
				videoHeight = movieSize.height;	
				
				if (currentProps == null) {
					currentProps = oMovie.getStartingProperties();
				}

				oMovie.setDefaultWidth(videoWidth);
				oMovie.setDefaultHeight(videoHeight);
				oMovieControls = mediaPlayer.getControlPanelComponent();
				add( oVideo, BorderLayout.CENTER ); // add video component
			} else {
				System.out.println("Video = null");
				ProjectCompendium.APP.displayError(LanguageProperties.getString(LanguageProperties.MOVIE_BUNDLE, "UIMoviePanel.errorSorry")+"\n\n"+LanguageProperties.getString(LanguageProperties.MOVIE_BUNDLE, "UIMoviePanel.errorSorry2")); //$NON-NLS-1$
				mediaPlayer = null;
			}
		} catch ( NoPlayerException noPlayerException ) { 
		    ProjectCompendium.APP.displayError(LanguageProperties.getString(LanguageProperties.MOVIE_BUNDLE, "UIMoviePanel.errorSorry")+"\n\n"+LanguageProperties.getString(LanguageProperties.MOVIE_BUNDLE, "UIMoviePanel.errorSorry2")); //$NON-NLS-1$
		    System.out.println("No player found:"+noPlayerException.getLocalizedMessage()); //$NON-NLS-1$
			mediaPlayer = null;
		} catch ( CannotRealizeException cannotRealizeException ) {
		    ProjectCompendium.APP.displayError(LanguageProperties.getString(LanguageProperties.MOVIE_BUNDLE, "UIMoviePanel.errorSorry")+"\n\n"+LanguageProperties.getString(LanguageProperties.MOVIE_BUNDLE, "UIMoviePanel.errorSorry2")); //$NON-NLS-1$
		    System.out.println("Could not realize media player:"+cannotRealizeException.getLocalizedMessage()); //$NON-NLS-1$
			mediaPlayer = null;
		} catch (MalformedURLException e) {
		    ProjectCompendium.APP.displayError(LanguageProperties.getString(LanguageProperties.MOVIE_BUNDLE, "UIMoviePanel.errorSorry")+"\n\n"+LanguageProperties.getString(LanguageProperties.MOVIE_BUNDLE, "UIMoviePanel.errorSorry2")); //$NON-NLS-1$
		    System.out.println("MalformedURL:"+e.getLocalizedMessage()); //$NON-NLS-1$
			mediaPlayer = null;
		} catch ( IOException iOException ) {
		    ProjectCompendium.APP.displayError(LanguageProperties.getString(LanguageProperties.MOVIE_BUNDLE, "UIMoviePanel.errorSorry")+"\n\n"+LanguageProperties.getString(LanguageProperties.MOVIE_BUNDLE, "UIMoviePanel.errorSorry2")); //$NON-NLS-1$
		    System.out.println("IOException:"+iOException.getLocalizedMessage());			 //$NON-NLS-1$
			mediaPlayer = null;
		}
		
		return mediaPlayer;
	}
	
	/**
	 * Create a new Player and load the Grid Movie.
	 * @param sFile the path to the grid file for the grid movie
	 * @return the new Player object, or null if there was an error.
	 */
	public Player createGridStreamPlayer(String sFile, long seek, double scale) {
		try {			
			Misc.addCodec("net.crew_vre.codec.h261.H261Decoder"); //$NON-NLS-1$
			Misc.addCodec("net.crew_vre.codec.h261.H261ASDecoder"); //$NON-NLS-1$
			Misc.addCodec("net.crew_vre.codec.colourspace.YUV420RGB32Converter"); //$NON-NLS-1$

		    PlugInManager.removePlugIn("com.sun.media.codec.video.h261.NativeDecoder", PlugInManager.CODEC); //$NON-NLS-1$
			
	        MediaLocator locator = new MediaLocator("recorded://" + sFile //$NON-NLS-1$
	                + "?seek=" + seek + "&scale=" + scale); //$NON-NLS-1$ //$NON-NLS-2$

	        dataSource = (DataSource) Manager.createDataSource(locator);
			mediaPlayer = Manager.createRealizedPlayer( dataSource );			
			duration = mediaPlayer.getDuration();
								
			//So shows first frame when map opened.Else if just gray square.
			mediaPlayer.prefetch();
			
			// get the components for the video and the playback controls
			oVideo = mediaPlayer.getVisualComponent();
			if (oVideo != null) {
				movieSize = oVideo.getPreferredSize();
				videoWidth = movieSize.width;
				videoHeight = movieSize.height;		
				
				if (currentProps == null) {
					currentProps = oMovie.getStartingProperties();
				}
				
				oMovie.setDefaultWidth(videoWidth);
				oMovie.setDefaultHeight(videoHeight);
				oMovieControls = mediaPlayer.getControlPanelComponent();
				add( oVideo, BorderLayout.CENTER ); // add video component
			} else {
				System.out.println("Video = null");
				ProjectCompendium.APP.displayError(LanguageProperties.getString(LanguageProperties.MOVIE_BUNDLE, "UIMoviePanel.errorSorry")+"\n\n"+LanguageProperties.getString(LanguageProperties.MOVIE_BUNDLE, "UIMoviePanel.errorSorry2")); //$NON-NLS-1$
				mediaPlayer = null;
			}
		} catch ( NoPlayerException noPlayerException ) { 
		    ProjectCompendium.APP.displayError(LanguageProperties.getString(LanguageProperties.MOVIE_BUNDLE, "UIMoviePanel.errorSorry")+"\n\n"+LanguageProperties.getString(LanguageProperties.MOVIE_BUNDLE, "UIMoviePanel.errorSorry2")); //$NON-NLS-1$
		    System.out.println("No player found:"+noPlayerException.getLocalizedMessage()); //$NON-NLS-1$
			mediaPlayer = null;
		} catch ( CannotRealizeException cannotRealizeException ) {
		    ProjectCompendium.APP.displayError(LanguageProperties.getString(LanguageProperties.MOVIE_BUNDLE, "UIMoviePanel.errorSorry")+"\n\n"+LanguageProperties.getString(LanguageProperties.MOVIE_BUNDLE, "UIMoviePanel.errorSorry2")); //$NON-NLS-1$
		    System.out.println("Could not realize media player:"+cannotRealizeException.getLocalizedMessage()); //$NON-NLS-1$
			mediaPlayer = null;
		} catch (NoDataSourceException e) {
	    	ProjectCompendium.APP.displayError(LanguageProperties.getString(LanguageProperties.MOVIE_BUNDLE, "UIMoviePanel.errorSorry")+"\n\n"+LanguageProperties.getString(LanguageProperties.MOVIE_BUNDLE, "UIMoviePanel.errorSorry2")); //$NON-NLS-1$
		    System.out.println("NoDataSourceException:"+e.getLocalizedMessage()); //$NON-NLS-1$
			mediaPlayer = null;
	    } catch (MalformedURLException e) {
		    ProjectCompendium.APP.displayError(LanguageProperties.getString(LanguageProperties.MOVIE_BUNDLE, "UIMoviePanel.errorSorry")+"\n\n"+LanguageProperties.getString(LanguageProperties.MOVIE_BUNDLE, "UIMoviePanel.errorSorry2")); //$NON-NLS-1$
		    System.out.println("MalformedURL:"+e.getLocalizedMessage()); //$NON-NLS-1$
			mediaPlayer = null;
		} catch ( IOException iOException ) {
		    ProjectCompendium.APP.displayError(LanguageProperties.getString(LanguageProperties.MOVIE_BUNDLE, "UIMoviePanel.errorSorry")+"\n\n"+LanguageProperties.getString(LanguageProperties.MOVIE_BUNDLE, "UIMoviePanel.errorSorry2")); //$NON-NLS-1$
		    System.out.println("IOException:"+iOException.getLocalizedMessage());			 //$NON-NLS-1$
			mediaPlayer = null;
		} catch ( IllegalAccessException iaException ) {
		    ProjectCompendium.APP.displayError(LanguageProperties.getString(LanguageProperties.MOVIE_BUNDLE, "UIMoviePanel.errorSorry")+"\n\n"+LanguageProperties.getString(LanguageProperties.MOVIE_BUNDLE, "UIMoviePanel.errorSorry2")); //$NON-NLS-1$
			System.out.println("IllegalAccessException:"+iaException.getLocalizedMessage());			
			mediaPlayer = null;
		} catch ( ClassNotFoundException cnfException ) {
		    ProjectCompendium.APP.displayError(LanguageProperties.getString(LanguageProperties.MOVIE_BUNDLE, "UIMoviePanel.errorSorry")+"\n\n"+LanguageProperties.getString(LanguageProperties.MOVIE_BUNDLE, "UIMoviePanel.errorSorry2")); //$NON-NLS-1$
			System.out.println("ClassNotFoundException:"+cnfException.getLocalizedMessage());			
			mediaPlayer = null;
		} catch ( InstantiationException instException ) {
		    ProjectCompendium.APP.displayError(LanguageProperties.getString(LanguageProperties.MOVIE_BUNDLE, "UIMoviePanel.errorSorry")+"\n\n"+LanguageProperties.getString(LanguageProperties.MOVIE_BUNDLE, "UIMoviePanel.errorSorry2")); //$NON-NLS-1$
			System.out.println("InstantiationException:"+instException.getLocalizedMessage());			
			mediaPlayer = null;
		}
		
		return mediaPlayer;
	}	
	
	/**
	 * Returns the selected state of the node.
	 *
	 * @return boolean, the selected state of the node
	 */
	public boolean isSelected() {
	    return bSelected;
	}

	/**
	 * Sets the selected state of the node.
	 * <p>
	 * @param selected, the selected state
	 */
	public void setSelected(boolean selected) {
	    boolean oldValue = bSelected;
	    bSelected = selected;
	    firePropertyChange(SELECTED_PROPERTY, oldValue, bSelected);
	    repaint();
	}
	
	/**
	 * Returns the roll-over state of the node.
	 *
	 * @return boolean, true if the node is currently rolled over, else false.
	 */
	/*public boolean isRollover() {
	    return bRollover;
	}*/

	/**
	* Sets the roll over state of the movie.
	* <p>
	* @param rollover, the roll over state of the movie.
	*/
	/*public void setRollover(boolean rollover) {

	    boolean oldValue = bRollover;
	    bRollover = rollover;

	    firePropertyChange(ROLLOVER_PROPERTY, new Boolean(oldValue), new Boolean(bRollover));

	    repaint();
	}*/
	
	/**
	 * Using a cached duration variable as Grid Streams where sending occasional spurious 
	 * data which was messing up the progress bar.
	 * @return
	 */
	public Time getDuration() {
		return duration;
	}
	
	public Component getControls() {
		return oMovieControls;
	}
	
	public void start() {
		mediaPlayer.start();
	}
	
	public void stop() {
		mediaPlayer.stop();
	}
	
	public void reset() {
		mediaPlayer.setMediaTime(new Time(0));
	}

	public Player getMediaPlayer() {
		return mediaPlayer;
	}
	
	public Movie getMovieData() {
		return oMovie;
	}

	public void setMovieData(Movie movie) {
		oMovie = movie;
		oMovie.setDefaultWidth(this.videoWidth);
		oMovie.setDefaultHeight(this.videoHeight);		
	}

	/**
	 * Set the current movie properties that apply at this time point.
	 * @param props
	 */
	public void setCurrentProperties(MovieProperties props) {
		this.currentProps = props;
		setLocation(props.getXPos(), props.getYPos());
		setSize(props.getWidth(), props.getHeight());
		setPreferredSize(new Dimension(props.getWidth(), props.getHeight()));			
		repaint();
	}
		
	/**
	 * Return the MovieProperties that this movie is currently using.
	 * @return the MovieProperties that this movie is currently using.
	 */
	public MovieProperties getCurrentProperties() {
		return currentProps;
	}
	
	/**
	 * This method was overridden to apply transparency percentages to the movie panel.
	 */
	@Override public void paintComponent(Graphics graphics) {		
        Graphics2D g2d = (Graphics2D) graphics;
        float trans = 0;
        if (currentProps != null) {
        	trans = currentProps.getTransparency();
        }
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, trans));
        super.paintComponent(graphics);  
    }

/** MOUSE EVENTS**/
  	
	public void mousePressed(MouseEvent e) {
		Point p = SwingUtilities.convertPoint((Component)e.getSource(), e.getX(), e.getY(), null);		
		lastMousePosX = p.x;
		lastMousePosY = p.y;
		
		MovieBorder border = (MovieBorder)this.getBorder();
		if (border.isInTopLeft(e.getPoint())) {
			inTopLeftSquare = true;
		} else if (border.isInTopRight(e.getPoint())) {
			inTopRightSquare = true;
		} else if (border.isInBottomLeft(e.getPoint())) {
			inBottomLeftSquare = true;
		} else if (border.isInBottomRight(e.getPoint())) {
			inBottomRightSquare = true;			
		}
	}
	
	public void mouseReleased(MouseEvent e) {
		lastMousePosX = 0;
		lastMousePosY = 0;
		
		/*if ((currentProps.getXPos() != this.getX()) 
				|| (currentProps.getYPos() != this.getY())
				|| (currentProps.getWidth() != this.getWidth())
				|| (currentProps.getHeight() != this.getHeight())) {
			try{
				// Need to update the local movie at this point so later events get the new data.
				currentProps = oMovieMapView.updateMovieProperties(currentProps.getId(), currentProps.getMovieID(), this.getX(), this.getY(), this.getWidth(), this.getHeight(), currentProps.getTransparency(), currentProps.getTime());
			} catch (Exception ex) {
				ProjectCompendium.APP.displayError("Unable to update movie location due to:\n\n"+ex.getLocalizedMessage());
				this.setLocation(currentProps.getXPos(), currentProps.getYPos());
			}
		}*/
				
		inTopLeftSquare = false;
		inTopRightSquare = false;
		inBottomLeftSquare = false;
		inBottomRightSquare = false;									
		this.repaint();
	}
	
	public void mouseClicked(MouseEvent e){	
		this.stop();

		boolean isRightMouse = SwingUtilities.isRightMouseButton(e);
		boolean isLeftMouse = SwingUtilities.isLeftMouseButton(e);		
		if (ProjectCompendium.isMac &&
			( (e.getButton() == 3 && e.isShiftDown()) ||
			 (e.getButton() == 1 && e.isControlDown()) )) {
			isRightMouse = true;
			isLeftMouse = false;
		}		
		
		oMovieMapViewPane.setLayer(this, oMovieMapViewPane.MOVIE_LAYER, 0);	
	    if (bSelected) {
	    	this.requestFocusInWindow();
	    }		

	    if (e.getClickCount() == 2 && isLeftMouse) {
	    	((UIMovieMapViewFrame)oMovieMapViewPane.getViewFrame()).showMovieDialog(oMovie);
		} else if(e.getClickCount() == 1 && isRightMouse) {
	    	Point p = SwingUtilities.convertPoint(this, e.getX(), e.getY(), oMovieMapViewPane);
			oMovieMapViewPane.showPopupMenu(oMovieMapViewPane.getUI(),p.x, p.y, this);
		} else {
	    	// Change it to the viewpane coordinate space so it draws the right-click menu etc correctly.			
	    	Point p = SwingUtilities.convertPoint(this, e.getX(), e.getY(), oMovieMapViewPane);
	 		MouseEvent mouse = new MouseEvent(oMovieMapViewPane, e.getID(), e.getWhen(), e.getModifiers(), p.x, p.y, e.getClickCount(), e.isPopupTrigger(), e.getButton());		
			oMovieMapViewPane.getUI().mouseClicked(mouse);
		}
	    
	    repaint();
	}
	
	public void mouseEntered(MouseEvent e) {					
		setSelected(true);					
	}
	public void mouseExited(MouseEvent e){
		setSelected(false);	
	}
 	
	public void mouseDragged(MouseEvent e) {
		UIMoviePanel panel = (UIMoviePanel)e.getSource();
		
		Point p = SwingUtilities.convertPoint((Component)e.getSource(), e.getX(), e.getY(), null);
		Dimension s = panel.getParent().getSize();
		int pWidth = s.width;
		int pHeight = s.height;
		int diffX = p.x - lastMousePosX;
		int diffY = p.y - lastMousePosY;
		       
		int minX, minY, maxX, maxY;
		Dimension panelSize = getSize();							
		Point currentLocation = getLocation();
		 
		boolean bKeepProportional = false;
		boolean bWidthRatio = true;		
		/*if ( (this.inBottomRightSquare || this.inTopRightSquare || this.inTopLeftSquare || this.inBottomLeftSquare) &&				
				e.isShiftDown()) {
			bKeepProportional = true;	

			int calcY = diffY;
			int calcX = diffX;
			if (diffX < 0) calcX = diffX*-1;
			if (diffY < 0) calcY = diffY*-1;
			if (calcX > calcY) {
				diffY = Math.round((diffX  * videoHeight ) / videoWidth);
			} else {
				diffX = Math.round((diffY  * videoWidth ) / videoHeight);
				bWidthRatio = false;
			}
			
			if (diffY == 0 || diffX == 0) {
				return;
			}
		}*/
		
		minX = currentLocation.x + diffX;
		minY = currentLocation.y + diffY;
		maxX = minX + panelSize.width;
		maxY = minY + panelSize.height;

		//CHECK BOUNDARIES
		if (maxX > pWidth) diffX = diffX - (maxX - pWidth);		
		if (maxY > pHeight)	diffY = diffY - (maxY - pWidth);		

		if (bKeepProportional) {
			if (bWidthRatio) {
				diffY = Math.round((diffX  * videoHeight ) / videoWidth);
			} else {
				diffX = Math.round((diffY  * videoWidth ) / videoHeight);
			}
		}

		if (inTopLeftSquare) {
			// CHECK BOUNDARIES
			if (minX < 0) diffX = diffX - minX;
			if (minY < 0) diffY = diffY - minY;
			if (bKeepProportional) {
				if (bWidthRatio) {
					diffY = Math.round((diffX  * videoHeight ) / videoWidth);
				} else {
					diffX = Math.round((diffY  * videoWidth ) / videoHeight);
				}
			}

			//CALCULATE LOCATION
			int newX = currentLocation.x+diffX;
			int newY = currentLocation.y+diffY;
			//CALCULATE SIZE
			int newdiffX = diffX*-1; 
			int newdiffY = diffY*-1;
			int newWidth = panelSize.width+newdiffX;
			if (newWidth > pWidth) {
				newWidth = pWidth;
			}
			int newHeight = panelSize.height+newdiffY;
			if (newHeight > pHeight) {
				newHeight = pHeight;
			}			
			//Double check aspect ratio
			if (bKeepProportional) {
				double ratio = videoHeight/videoWidth;			
				if (newHeight/newWidth != ratio) {
					newHeight = new Long( Math.round(ratio * newWidth)).intValue(); 
				}
			}
			if (newHeight < 10 || newWidth < 10) return;
				
			setLocation(newX, newY);						
			setSize(newWidth, newHeight);
			setPreferredSize(new Dimension(newWidth, newHeight));	
		} else if (this.inTopRightSquare) {
			// CHECK BOUNDARIES
			if (minX+panelSize.width < 0) diffX = diffX - minX;
			if (minY < 0) diffY = diffY - minY;
			if (bKeepProportional) {
				if (bWidthRatio) {
					diffY = Math.round((diffX  * videoHeight ) / videoWidth);
				} else {
					diffX = Math.round((diffY  * videoWidth ) / videoHeight);
				}
			}

			//CALCULATE LOCATION
			int newY = currentLocation.y+diffY;

			//CALCULATE SIZE
			int newWidth = panelSize.width+diffX;
			if (newWidth > pWidth) {
				newWidth = pWidth;
			}
			int newdiffY = diffY*-1; 
			int newHeight = panelSize.height+newdiffY;
			if (newHeight > pHeight) {
				newHeight = pHeight;
			}
			
			//Double check aspect ratio
			if (bKeepProportional) {
				double ratio = videoHeight/videoWidth;			
				if (newHeight/newWidth != ratio) {
					newHeight = new Long( Math.round(ratio * newWidth)).intValue(); 
				}
			}
			if (newHeight < 10 || newWidth < 10) return;

			setLocation(currentLocation.x, newY);			
			setSize(newWidth, newHeight);
			setPreferredSize(new Dimension(newWidth, newHeight));	
		} else if (this.inBottomLeftSquare) {
			// CHECK BOUNDARIES
			if (minX < 0) diffX = diffX - minX;
			if (minY+panelSize.height < 0) {
				diffY = diffY - minY;
			}
			if (bKeepProportional) {
				if (bWidthRatio) {
					diffY = Math.round((diffX  * videoHeight ) / videoWidth);
				} else {
					diffX = Math.round((diffY  * videoWidth ) / videoHeight);
				}
			}

			//CALCULATE LOCATION
			int newX = currentLocation.x+diffX;
			
			//CALCULATE SIZE
			int newDiffY = diffY*-1; 
			int newWidth = panelSize.width-diffX;
			int newHeight = panelSize.height-newDiffY;

			//Double check aspect ratio
			if (bKeepProportional) {
				double ratio = videoHeight/videoWidth;			
				if (newHeight/newWidth != ratio) {
					newHeight = new Long( Math.round(ratio * newWidth)).intValue(); 
				}
			}
			if (newHeight < 10 || newWidth < 10) return;

			setLocation(newX, currentLocation.y);
			setSize(newWidth, newHeight);
			setPreferredSize(new Dimension(newWidth, newHeight));	
		} else if (this.inBottomRightSquare) {			
			// CHECK BOUNDARIES
			if (minX+panelSize.width < 0) {
				diffX = diffX - minX;
			}	
			if (minY+panelSize.height < 0) {
				diffY = diffY - minY;
			}
			if (bKeepProportional) {
				if (bWidthRatio) {
					diffY = Math.round((diffX  * videoHeight ) / videoWidth);
				} else {
					diffX = Math.round((diffY  * videoWidth ) / videoHeight);
				}
			}
						
			//CALCULATE LOCATION
			//remains the same
			
			//CALCULATE SIZE
			int newWidth = panelSize.width+diffX;
			int newHeight = panelSize.height+diffY;
			//Double check aspect ratio
			if (bKeepProportional) {
				double ratio = videoHeight/videoWidth;			
				if (newHeight/newWidth != ratio) {
					newHeight = new Long( Math.round(ratio * newWidth)).intValue(); 
				}
			}
			if (newHeight < 10 || newWidth < 10) return;
			
			setSize(newWidth, newHeight);
			setPreferredSize(new Dimension(newWidth, newHeight));	
		} else {			
			// CHECK BOUNDARIES
			if (minX < 0) diffX = diffX - minX;
			if (minY < 0) diffY = diffY - minY;
			
			int newX = currentLocation.x + diffX;
			int newY = currentLocation.y + diffY;
	
			Rectangle nodeBounds = panel.getBounds();
			
			// NEED THIS SILLY DANCE TO FORCE BORDER TO RECALC INSETS AND SO DRAW CORRECTLY
			this.setSize(nodeBounds.width+1, nodeBounds.height);
			this.setSize(nodeBounds.width, nodeBounds.height);
			
			setLocation(newX, newY);
			
			// MAKE SURE MOVIE IS VISIBLE, SO IF DRAGGED OFF SCREEN - AUTO SCROLL
			JViewport viewport = oMovieMapViewPane.getViewFrame().getViewport();
			Point parentPos = SwingUtilities.convertPoint((Component)oMovieMapViewPane, newX, newY, viewport);
			viewport.scrollRectToVisible( new Rectangle( parentPos.x, parentPos.y, nodeBounds.width, nodeBounds.height ) );
		}	
		lastMousePosX = p.x;
		lastMousePosY = p.y;

		validate();
		repaint();		
	}
	
	public void mouseMoved(MouseEvent e) {		
		MovieBorder border = (MovieBorder)this.getBorder();
		
		if (border.isInTopLeft(e.getPoint())) {
			this.setCursor(new Cursor(java.awt.Cursor.NW_RESIZE_CURSOR));
		} else if (border.isInTopRight(e.getPoint())) {
			this.setCursor(new Cursor(java.awt.Cursor.NE_RESIZE_CURSOR));
		} else if (border.isInBottomLeft(e.getPoint())) {
			this.setCursor(new Cursor(java.awt.Cursor.SW_RESIZE_CURSOR));
		} else if (border.isInBottomRight(e.getPoint())) {
			this.setCursor(new Cursor(java.awt.Cursor.SE_RESIZE_CURSOR));
		} else {
			this.setCursor(new Cursor(java.awt.Cursor.DEFAULT_CURSOR));				
		}
		
		//e.setSource(this);				
		this.oMovieMapViewPane.getUI().mouseMoved(e);		
	}

	/**
	 * Reset the movie to its default size; 
	 */
	public void resetMovieToDefaultSize() {
		try{
			MovieProperties newProps = oMovieMapView.updateMovieProperties(currentProps.getId(), currentProps.getMovieID(), currentProps.getXPos(), currentProps.getYPos(), videoWidth, videoHeight, currentProps.getTransparency(), currentProps.getTime());
			oMovie.setProperties(newProps);
		} catch (Exception ex) {
			ProjectCompendium.APP.displayError(LanguageProperties.getString(LanguageProperties.MOVIE_BUNDLE, "UIMoviePanel.errorUpdateLocation")+":\n\n"+ex.getLocalizedMessage()); //$NON-NLS-1$
		}				
	}
	
	/*public MovieProperties getCurrentMovieProperties() {
		return currentProps;
	}*/
	
	public void keyPressed(KeyEvent e) {
		if (bSelected) {
			int key = e.getKeyCode();	
			int modifiers = e.getModifiers();
			Dimension size = new Dimension(movieSize);

			// Calculate the default height and width
			int width = DEFAULT_WIDTH;
			int height = (int) ((DEFAULT_WIDTH * size.getHeight()) / size.getWidth());
			
			if (currentProps != null ) {
				if (key == '0') {
					resetMovieToDefaultSize();
				}
				else if ((key >= '1') && (key <= '9') && modifiers == 0) {
					int value = (key - '0') - KEY_OFFSET;
					int wdiff = value < 0 ? NEGATIVE_MULTIPLIER_WIDTH : POSITIVE_MULTIPLIER_WIDTH;
					int hdiff = value < 0 ? NEGATIVE_MULTIPLIER_HEIGHT : POSITIVE_MULTIPLIER_HEIGHT;
					width = (width + (wdiff * value));
					height = (height + (hdiff * value));
					try{
						oMovieMapView.updateMovieProperties(currentProps.getId(), currentProps.getMovieID(), currentProps.getXPos(), currentProps.getYPos(), width, height, currentProps.getTransparency(), currentProps.getTime());
					} catch (Exception ex) {
						ProjectCompendium.APP.displayError(LanguageProperties.getString(LanguageProperties.MOVIE_BUNDLE, "UIMoviePanel.errorUpdateLocation")+":\n\n"+ex.getLocalizedMessage()); //$NON-NLS-1$
					}				
				} else if ( (key == KeyEvent.VK_DELETE && modifiers == 0) || 
						   key == KeyEvent.VK_BACK_SPACE && modifiers == 0) {
					deleteMovie();
				} else {
					//e.setSource(this);				
					this.oMovieMapViewPane.getUI().keyPressed(e);
				}	
			} else {
				ProjectCompendium.APP.displayError(LanguageProperties.getString(LanguageProperties.MOVIE_BUNDLE, "UIMoviePanel.errorUpdateProperties")); //$NON-NLS-1$
			}
		}		
	}
	
	/**
	 * Delete this movie from the map
	 */
	public void deleteMovie() {
		File file = new File(oMovie.getLink());
		int response = JOptionPane.showConfirmDialog(ProjectCompendium.APP, LanguageProperties.getString(LanguageProperties.MOVIE_BUNDLE, "UIMoviePanel.confirmDelete")+":\n\n"+file.getName()+"\n\n"+LanguageProperties.getString(LanguageProperties.MOVIE_BUNDLE, "UIMoviePanel.confirmDelete2")+"\n",LanguageProperties.getString(LanguageProperties.MOVIE_BUNDLE, "UIMoviePanel.confirmDeleteTitle"), JOptionPane.YES_NO_OPTION); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		if (response == JOptionPane.NO_OPTION || response == JOptionPane.CLOSED_OPTION) {
			return;
		} else {								
			try {					
				String sMovieID = oMovie.getId();
				oMovieMapView.deleteMovie(sMovieID);										
			} catch (Exception ex) {
				System.out.println(LanguageProperties.getString(LanguageProperties.MOVIE_BUNDLE, "UIMoviePanel.error")+":"+ex.getLocalizedMessage()); //$NON-NLS-1$
				ex.printStackTrace();
			}
		}
	}
											
	public void keyReleased(KeyEvent e) {
		//this.oMovieMapViewPane.getUI().keyReleased(e);
	}
	
	public void keyTyped(KeyEvent e) {		
		//e.setSource(this);			
		this.oMovieMapViewPane.getUI().keyTyped(e);
	}
	
	public void focusGained(FocusEvent e) {
		
	}
	
	public void focusLost(FocusEvent e){
		inTopLeftSquare = false;
		inTopRightSquare = false;
		inBottomLeftSquare = false;
		inBottomRightSquare = false;
		repaint();
	}
	
	/**
	 * Setting the size also sets the Video size.
	 */
	public void setSize() {
		//oVideo.setSize(new Dimension(oMovie.getWidth(),oMovie.getHeight()));
		//super.setSize(new Dimension(oMovie.getWidth(),oMovie.getHeight()));		
	}
	
	private class MovieBorder extends AbstractBorder {
				
		/** The colour to use for the border around the node if it has the focus.*/
		private final Color 	FOCUSED_COLOR 		= Color.blue;

		/** The colour to use for the movie border when the node is rolled over with the mouse.*/
		private final Color 	BORDER_COLOR		= Color.cyan; 

		/** The colour to use for the movie border when the node is rolled over with the mouse.*/
		//private final Color 	SELECTED_COLOR		= Color.yellow;

	    private Rectangle rTopLeft = null;
	    private Rectangle rTopRight = null;
	    private Rectangle rBottomLeft = null;
	    private Rectangle rBottomRight = null;
	    
		int left = 0;
		int right = 0;
		int top = 0;
		int bottom = 0;
	    		
	    private int	borderWidth = 0;
	    
	    private UIMoviePanel oPanel = null;
	    		
		public MovieBorder(int width, UIMoviePanel panel) {
			this.borderWidth = width;
			this.oPanel = panel;			
			this.left = borderWidth;
			this.right = borderWidth;
			this.top = borderWidth;
			this.bottom = borderWidth;
		}
		
	    /**
	     * Paints the border for the specified component with the 
	     * specified position and size.
	     * @param c the component for which this border is being painted
	     * @param g the paint graphics
	     * @param x the x position of the painted border
	     * @param y the y position of the painted border
	     * @param width the width of the painted border
	     * @param height the height of the painted border
	     */
	    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
	    	
		    if (bSelected || c.hasFocus()) {
		        Color oldColor = g.getColor();
	
		        g.setColor(BORDER_COLOR);
		        if (c.hasFocus()) {
		        	g.setColor(this.FOCUSED_COLOR);
		        }
		        		        
		        int borderX = x+left-1;
		        int borderY = y+top-1;		        
		        int borderWidth = width-(left+right)+1;
		        int borderHeight = height-(top+bottom)+1;
		        
		        g.drawRect(borderX, borderY, borderWidth, borderHeight);
	        	
	        	//Graphics2D g2d = (Graphics2D)g;
	    		//g2d.setStroke(new BasicStroke(2));
	    		
		        borderX = x+left-2;		        
		        borderY = y+top-2;		        
		        borderWidth = width-(left+right)+3;
		        borderHeight = height-(top+bottom)+3;
	        	g.drawRect(borderX, borderY, borderWidth, borderHeight);

        		rTopLeft = new Rectangle(x, y, left, top);
        		g.fillRect(x, y, left, top);
	        	
		        rTopRight = new Rectangle(x + width - right, y, right, top);
		        g.fillRect(x + width - right, y, right, top);
	
		        rBottomLeft = new Rectangle(x, y+height-bottom, left, bottom);
		        g.fillRect(x, y+height-bottom, left, bottom);
	
		        rBottomRight = new Rectangle(x+width-right, y+height-bottom, right, bottom);
		        g.fillRect(x+width-right, y+height-bottom, right, bottom);
		        
	        	g.setColor(oldColor);		        
		    }
	    }
	    
	    public boolean isInTopLeft(Point mouse) {
	    	if (rTopLeft != null && rTopLeft.contains(mouse)) {
	    		return true;
	    	}
	    	return false;
	    }

	    public boolean isInTopRight(Point mouse) {
	    	if (rTopRight != null && rTopRight.contains(mouse)) {
	    		return true;
	    	}
	    	return false;
	    }

	    public boolean isInBottomLeft(Point mouse) {
	    	if (rBottomLeft != null && rBottomLeft.contains(mouse)) {
	    		return true;
	    	}
	    	return false;
	    }

	    public boolean isInBottomRight(Point mouse) {
	    	if (rBottomRight != null && rBottomRight.contains(mouse)) {
	    		return true;
	    	}
	    	return false;
	    }

	    public Insets calculateInsets() {
			Point location = oPanel.getLocation();
			Dimension dim = oVideo.getPreferredSize();
						
			left = borderWidth;
			right = borderWidth;
			top = borderWidth;
			bottom = borderWidth;
			
			if (location.x < borderWidth) {
				left = location.x;
			}
			if (location.y < borderWidth) {
				top = location.y;
			}

			return new Insets(top, left, bottom, right);
	    }
	    
		public Insets getBorderInsets() {
			return calculateInsets();
		}
		
		public Insets getBorderInsets(Component c) {
			return calculateInsets();
		}

		public Insets getBorderInsets(Component c, Insets insets) {
			return calculateInsets();
		}
	}	
	
	public void clean() {
		if (mediaPlayer != null) {
			this.removeAll();
			this.validate();
			this.setVisible(false);
		}
	}
}