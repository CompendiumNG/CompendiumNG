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

import javax.media.Time;
import javax.media.Player;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import com.compendium.LanguageProperties;
import com.compendium.ProjectCompendium;
import com.compendium.core.ICoreConstants;
import com.compendium.core.datamodel.Movie;
import com.compendium.core.datamodel.MovieMapView;
import com.compendium.core.datamodel.MovieProperties;
import com.compendium.core.datamodel.NodePositionTime;
import com.compendium.core.datamodel.NodeSummary;
import com.compendium.ui.IUIConstants;
import com.compendium.ui.UIImageButton;
import com.compendium.ui.UIImages;
import com.compendium.ui.UILink;
import com.compendium.ui.UINode;
import com.compendium.ui.UIUtilities;
import com.compendium.ui.UIViewPane;
import com.compendium.ui.plaf.NodeUI;

import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

/**
 * This class loads all the time lines and header and bar and creates the master timer. 
 * @author Michelle Bachler
 *
 */
public class UITimeLinesController extends JLayeredPane implements PropertyChangeListener, AdjustmentListener {
	
	/** The default length for a new node time span, in pixels */
	public static final int DEFAULT_PIXEL_SPAN_LENGTH = 30;

	/** The left hand offset for each timelines internal layout .*/
	public static final int	TIMELINE_LEFT_OFFSET = 3;  

	/** the default time, in hundreths of a second, that a pixel of timeline represents*/
	public static final int DEFAULT_PIXEL_TIME_SCALE = 320;
	
	/** the height of a row of the timeline or timeline label panel*/
	public static final int ROW_HEIGHT = 26;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** The border colour for the timeline header area.*/
	private static final Color	HEADER_BORDER_COLOUR = Color.darkGray.darker();
	
	/** The background colour for the Node timelines.*/
	private static final Color	NODE_TIMELINE_BACKGROUND_COLOUR = new Color(233, 234, 253);

	/** The border colour for the Node timelines.*/
	private static final Color	NODE_TIMELINE_BORDER_COLOUR = NODE_TIMELINE_BACKGROUND_COLOUR.darker();

	/** The background colour when the node is selected**/
	private static final Color	NODE_TIMELINE_SELECTED_BACKGROUND_COLOUR = new Color(255,255,192); //128	

	/** The background colour for the MovieTimelines.*/
	private static final Color	MOVIE_TIMELINE_BACKGROUND_COLOUR = new Color(193, 193, 193);	

	/** The background colour when the movie is selected**/
	private static final Color	MOVIE_TIMELINE_SELECTED_BACKGROUND_COLOUR = new Color(128,255,255);	//192

	/** The border colour for the Movie timelines.*/
	private static final Color	MOVIE_TIMELINE_BORDER_COLOUR = MOVIE_TIMELINE_BACKGROUND_COLOUR.darker();

	/** A reference to the layer to hold timelines.*/
	private static final Integer	TIMELINE_LAYER		= new Integer(100);

	/** A reference to the layer to hold timeline header bar.*/
	private static final Integer	HEADER_LAYER		= new Integer(120);

	/** A reference to the layer holding the timeline bar.*/
	private static final Integer	BAR_LAYER			= new Integer(130);

	/** A reference to the layer to hold timeline header bar.*/
	private static final Integer	LABEL_LAYER		= new Integer(140);

	/** A reference to the layer to hold player toolbar*/
	private static final Integer	TOOLBAR_LAYER		= new Integer(150);

	/** the position in the zoom options to start at - must match DEFAULT_PIXEL_TIME_SCALE position */
	private static final int DEFAULT_PIXEL_TIME_SCALE_POSITION = 5;
	
	/** The amount in milliseconds to add and remove when buttons pushed.*/
	private static final long EXTRA_TIME = 300000; //5 minutes? 
		

	/** the time, in hundreths of a second, that a pixel of timeline represents*/
	public int pixel_time_scale = DEFAULT_PIXEL_TIME_SCALE;

	/** the length of the timelines in pixels*/
	public int timeline_length = 0;
	
	/** The main panel with all the timelines.*/
	public JPanel oNodeTimeLinesPanel = null;	

	/** The main panel with all the timeline labels.*/
	public JPanel oNodeTimeLinesLabelPanel = null;	

	/** The header panel*/
	private JPanel oHeaderPanel = null;
	
	/** The left toolbar panel*/
	private JPanel oToolBarPanel = null;
	
	/** the length of the current master timeline in milliseconds .*/
	private long currentTimelineDuration = 0;
	
	/** the minimum length required to display know time elements.*/
	private long requiredTimelineDuration = 0;
	
	/** The numbers represent number of millseconds of a second that a pixel represents.*/
	private int zoomScales[] = {10,20,40,80,160,320,600,1200,2400,4800,9600,19200};
	
	/** 
	 * The actual width required for the left Border to accomodate the top toolbar.
	 * Also used (-1) to set the with of the left pane holding labels etc..
	 */
	public int leftBorder = 101; 

	/** This is the width of the right hand toobar.
	 * It determines the right hand offset for each timelines internal layout .
	 */
	public int rightBorder = 70; 

	private int position = DEFAULT_PIXEL_TIME_SCALE_POSITION;
	
	/** The button to zoom in on the current view.*/
	private JButton				pbZoomIn			= null;

	/** The button to zoom in on the current view.*/
	private JButton				pbZoomOut			= null;
	
	/** the button for taking off 5 minutes of time.*/
    private UIImageButton lessButton = null;

	/** the button for adding 5 minutes of time.*/
    private UIImageButton moreButton = null;
	          
    /** The movie pane that this is the Controller for.*/
    private UIMovieMapViewPane		oMovieMapViewPane = null;
    
    /** The MovieMapView data object associated with this controller's map.*/
	private MovieMapView 			oMovieMapView = null;
	
	/** Stores a list of all the UITimeLineForNode objects this controller is manageing.*/
	private Hashtable<String, UITimeLineForNode> htNodeSliders = null;	

	/** Stores a list of all the UITimeLineForMovie objects this controller is manageing.*/
	private Hashtable<String, UITimeLineForMovie> htMovieSliders = null;	
	
	/** Stores a list of the JPanels that hold the node timelines.
	 * Used for changing background colour when a node selected.*/
	private Hashtable<String, JPanel>	htNodeTimeLinePanels = null;

	/** Stores a list of the JPanels that hold the node timeline labels.
	 * Used for changing background colour when a node selected.*/
	private Hashtable<String, JPanel>	htNodeTimeLineLabelPanels = null;

	/** Stores a list of the JPanels that hold the movie timelines.
	 * Used for changing background colour when a movie rolled over.*/
	private Hashtable<String, JPanel>	htMovieTimeLinePanels = null;

	/** Stores a list of the JPanels that hold the movie timeline labels.
	 * Used for changing background colour when a movie rolled over.*/
	private Hashtable<String, JPanel>	htMovieTimeLineLabelPanels = null;

	/** Layout manager used by oNodeTimeLinesPanel.*/
	private GridBagLayout			layout		= null;
	
	/** Layout constraints by used by the oNodeTimeLinesPanel layout manager.*/
	private GridBagConstraints		cons		= null;
    	
	/** Layout manager used by oNodeTimeLinesLabelPanel.*/
	private GridBagLayout			layoutLabel		= null;
	
	/** Layout constraints by used by the oNodeTimeLinesLabelPanel layout manager.*/
	private GridBagConstraints		consLabel		= null;

	/** The width of this panel **/
    int width = 0;
    
    /** The height of this panel **/
    int height = 0;
         
    private int rowCount = 0;
    
    /** The reference to the timeline header */
    private UITimeLineHeader timelineHeader = null;
    
    /** The parent panel to this panel.*/
    private UITimeLinesPanel parentPanel;   
    
    /** The main master timeline grabber bar.*/
    private UITimeLineBar bar = null;
    
    /** The time at which the movie was paused at.*/
    private Time					lastTime 			= new Time(0);
    
    /** The time at which the master time line needs to stop playing.*/
    private long					stopTime			= 0;
    
    /** the master play/pause button.*/
	private UIImageButton 		masterPlayButton	= null;
	
	/** The master reset button to reset the master time line.*/
	private UIImageButton 		masterResetButton	= null;  
	
	/** The master timer that runs all the time lines that this controller holds.*/
	private MasterTimer 			controller			= null;
	    
    /**
     * Constructor creates/loads all the various timelines and panels that make up this controller area.
     * @param cp the parent panel to this panel.
     * @param oView The main pane that holds the movie map that this is the controller for.
     */
	public UITimeLinesController(UITimeLinesPanel cp, UIMovieMapViewPane oView) {
        this.oMovieMapViewPane = oView;
        
		this.oMovieMapViewPane.addPropertyChangeListener(this);    	
		this.oMovieMapView = (MovieMapView)oView.getView();
		this.oMovieMapView.addPropertyChangeListener(this);    	
		this.parentPanel = cp;
		cp.addScrollAdjustmentListener(this);
		
        htNodeSliders = new Hashtable<String, UITimeLineForNode>();
        htNodeTimeLinePanels = new Hashtable<String, JPanel>();
        htNodeTimeLineLabelPanels = new Hashtable<String, JPanel>();
        htMovieSliders = new Hashtable<String, UITimeLineForMovie>();
        htMovieTimeLinePanels = new Hashtable<String, JPanel>();
        htMovieTimeLineLabelPanels = new Hashtable<String, JPanel>();
        
        oNodeTimeLinesLabelPanel = new JPanel();	
        oNodeTimeLinesLabelPanel.setName("oNodeTimeLinesLabelPanel"); //$NON-NLS-1$
        oNodeTimeLinesPanel = new JPanel();	
        oNodeTimeLinesPanel.setName("oNodeTimeLinesPanel"); //$NON-NLS-1$
       /*oNodeTimeLinesPanel.addComponentListener(new ComponentAdapter() {
    	    public void componentResized ( ComponentEvent event ) { 
    	    	Thread thread = new Thread("UITimeLinesController.1") {
    	    		public void run() {
    	    			setPreferredSize(new Dimension(getWidth(), oNodeTimeLinesPanel.getHeight()));
    	    			setSize(new Dimension(getWidth(), oNodeTimeLinesPanel.getHeight()));
    	    		}
    	    	};
    	    	thread.start();
    	    }
    	});*/        
  
		controller = new MasterTimer();
		controller.prefetch();
 
        refreshTimeLines();  
        
        oNodeTimeLinesPanel.setLocation(leftBorder, oHeaderPanel.getPreferredSize().height);
        add(oNodeTimeLinesPanel, TIMELINE_LAYER);
        oNodeTimeLinesPanel.setVisible(true);
 
        oNodeTimeLinesLabelPanel.setLocation(0, oHeaderPanel.getPreferredSize().height);
        add(oNodeTimeLinesLabelPanel, LABEL_LAYER);
        oNodeTimeLinesLabelPanel.setVisible(true);
        
   	   	bar = new UITimeLineBar(this, controller);
   	   	//bar.setBorder(new LineBorder(Color.red, 1));
   	   	bar.setLocation(leftBorder-1+TIMELINE_LEFT_OFFSET,0);
	   	bar.setSize(getPreferredSize().width-leftBorder-TIMELINE_LEFT_OFFSET-rightBorder, getPreferredSize().height);
	   	add(bar, BAR_LAYER);
	   	bar.setVisible(true);    	
	   	
        /*addComponentListener(new ComponentAdapter() {
    	    public void componentResized ( ComponentEvent event ) {
     	    	if (bar != null) {
       		   		bar.setPreferredSize(new Dimension(getPreferredSize().width-TIMELINES_START_OFFSET-TIMELINE_LEFT_OFFSET-TIMELINE_RIGHT_OFFSET, getPreferredSize().height));
       		   		bar.setSize(getPreferredSize().width-TIMELINES_START_OFFSET-TIMELINE_LEFT_OFFSET-TIMELINE_RIGHT_OFFSET, getPreferredSize().height);
    	    	}
     	    	parentPanel.resized();
     			revalidate();
     		   	repaint();
     	    }
    	});*/
	   	
		Component [] array1 = oMovieMapViewPane.getComponentsInLayer((UIViewPane.LINK_LAYER).intValue());
		for(int i=0;i<array1.length;i++) {
			JComponent object = (JComponent)array1[i];
			UILink link = (UILink)object;
			UINode fromNode = link.getFromNode();
			UINode toNode = link.getToNode();
			if (!fromNode.isVisible() || !toNode.isVisible()) {
				link.setVisible(false);
			}
		}
         
		
	   	setVisible(true); 
    }
	
	public void addSelectedItem() {
		
	}
	
    /**
     * Calculate the required timeline for all know time elements.
     * Sets the stopTime, the requiredTimelineDuration and the currentTimelineDuration.
     */
    public synchronized void recalculateRequiredTimeline() {
        requiredTimelineDuration = calculateStopTime();
		stopTime = TimeUnit.MILLISECONDS.toNanos(requiredTimelineDuration);	
		if (controller != null) {
			controller.setStopTime(new Time(stopTime));
		}
		if (requiredTimelineDuration >= currentTimelineDuration) {
			currentTimelineDuration = requiredTimelineDuration;	
		} 
        scaleWidth();
    }

    /**
     * Take the current total timeline duration and apply the current scale and reset sizes accordingly.
     */
    public synchronized void scaleWidth() {
		int pixelWidth = new Long(currentTimelineDuration/pixel_time_scale).intValue();
		width=pixelWidth+leftBorder+TIMELINE_LEFT_OFFSET+rightBorder;	

		// MAKE IT ALWAYS FILL THE AVAILABLE SCREEN SPACE AS A MINIMUM
		int parentWidth = parentPanel.getSize().width-(new JToolBar().getPreferredSize().width);
		if (parentWidth > width) {
        	width = parentWidth;
        	pixelWidth = width-leftBorder-TIMELINE_LEFT_OFFSET-rightBorder;
        } 		
		int requiredHundrethsLongest = new Long(requiredTimelineDuration).intValue();
		int requiredPixelWidth = requiredHundrethsLongest/pixel_time_scale;
		int requiredWidth=requiredPixelWidth+leftBorder+TIMELINE_LEFT_OFFSET+rightBorder;	
		if (width > requiredWidth && width > parentWidth) {
	    	lessButton.setEnabled(true);
		} else {
     		lessButton.setEnabled(false);
		}

		timeline_length = pixelWidth+TIMELINE_LEFT_OFFSET+rightBorder;
		//int height = rowCount*ROW_HEIGHT; 
		int height = layout.minimumLayoutSize(oNodeTimeLinesPanel).height;
		
		// reset all the sizes of the components and layout main panel again.
        oNodeTimeLinesPanel.setPreferredSize(new Dimension(width-TIMELINE_LEFT_OFFSET, height));
        oNodeTimeLinesPanel.setSize(new Dimension(width-TIMELINE_LEFT_OFFSET, height));

        oNodeTimeLinesLabelPanel.setPreferredSize(new Dimension(leftBorder, height));
        oNodeTimeLinesLabelPanel.setSize(new Dimension(leftBorder, height));

        oHeaderPanel.setPreferredSize(new Dimension(width-leftBorder, oHeaderPanel.getPreferredSize().height));
        oHeaderPanel.setSize(new Dimension(width-leftBorder, oHeaderPanel.getPreferredSize().height));

        oToolBarPanel.setPreferredSize(new Dimension(leftBorder, oHeaderPanel.getPreferredSize().height));
        oToolBarPanel.setSize(new Dimension(leftBorder, oHeaderPanel.getPreferredSize().height));
                
    	layout.invalidateLayout(oNodeTimeLinesPanel);
    	layout.layoutContainer(oNodeTimeLinesPanel);

    	layoutLabel.invalidateLayout(oNodeTimeLinesLabelPanel);
    	layoutLabel.layoutContainer(oNodeTimeLinesLabelPanel);

		height = height+oHeaderPanel.getPreferredSize().height;

    	setSize(new Dimension(width, height));
		setPreferredSize(new Dimension(width, height));
    	if (bar != null) {
	   		bar.setPreferredSize(new Dimension(width-leftBorder-TIMELINE_LEFT_OFFSET-rightBorder, height));
	   		bar.setSize(width-leftBorder-TIMELINE_LEFT_OFFSET-rightBorder, height);
	   		bar.seek(controller.getMediaNanoseconds()); // otherwise it is not in the correct location relative to scale
    	}
    	
		revalidate();
		repaint();
    }
    
    /**
     * Look at the node time spans and the movies durations
     * and determine the last time requirement which determines the stop time or duration required..
     * @return the largest time requirements either from a time span of a movie duration in milliseconds
     */
    private synchronized long calculateStopTime() {
    	long longest = 0;
    	
    	// Check node time spans
		Component[] array = oMovieMapViewPane.getComponentsInLayer((UIViewPane.NODE_LAYER).intValue());
		for(int i=0;i<array.length;i++) {
			UINode uinode = (UINode)array[i];	  	
			Hashtable<String, NodePositionTime> times = oMovieMapView.getTimesForNode(uinode.getNode().getId());
			for (Enumeration<NodePositionTime> ex = times.elements(); ex.hasMoreElements();) {
				NodePositionTime nextTime = (NodePositionTime)ex.nextElement();
	    		long stoptime = nextTime.getTimeToHide();
	    		if (stoptime > longest) {
	    			longest = stoptime;
	    		}
			}
		}    
				
		// Check the movie duration and property times
		Vector<UIMoviePanel> movies  = oMovieMapViewPane.getMovies();
		int count = movies.size();
		UIMoviePanel panel = null;
		long nextStart = 0;
		long nextDuration = 0;
		long nextDurationMillis = 0;
		Vector<MovieProperties> props = null;
		MovieProperties prop = null;
		for (int i=0; i<count; i++) {
			panel = (UIMoviePanel)movies.elementAt(i);
			nextDuration = panel.getDuration().getNanoseconds();
			nextDurationMillis = TimeUnit.NANOSECONDS.toMillis(nextDuration);
			nextDurationMillis = nextDurationMillis+panel.getMovieData().getStartTime();
			if (nextDurationMillis > longest) {
				longest = nextDurationMillis;
			}
			props = panel.getMovieData().getProperties();
			int countj = props.size();
			for (int j=0; j<countj; j++) {
				prop = props.elementAt(j);
				nextStart = prop.getTime();
				if (nextStart > longest) {
					longest = nextStart;
				}
			}			
		}
		
		return longest;
    }
    
    /**
     * Create all the time lines and header. 
     */
	private synchronized void refreshTimeLines() {
		this.htNodeTimeLinePanels.clear();
		this.htNodeTimeLineLabelPanels.clear();
		this.htMovieTimeLinePanels.clear();
		this.htMovieTimeLineLabelPanels.clear();
		this.htNodeSliders.clear(); // take this out if I fix why reuse of line was broken
		this.htMovieSliders.clear();
		
		if (oHeaderPanel != null) {
			remove(oHeaderPanel);
			oHeaderPanel = null;
		}
		if (oToolBarPanel != null) {
			remove(oToolBarPanel);
			oToolBarPanel = null;
		}

	    layout = new GridBagLayout();
	    cons = new GridBagConstraints();
	    cons.fill = GridBagConstraints.HORIZONTAL;
	    cons.anchor = GridBagConstraints.NORTHWEST;
	    cons.gridwidth = GridBagConstraints.REMAINDER;
		cons.weighty = 0;
	    cons.weightx = 2;
	    cons.ipady = 4;	    
		oNodeTimeLinesPanel.removeAll();		
		oNodeTimeLinesPanel.setLayout(layout);

	    layoutLabel = new GridBagLayout();
	    consLabel = new GridBagConstraints();
	    consLabel.fill = GridBagConstraints.HORIZONTAL;
	    consLabel.anchor = GridBagConstraints.NORTHWEST;
	    consLabel.gridwidth = GridBagConstraints.REMAINDER;
		consLabel.weighty = 0;
	    consLabel.weightx = 2;
	    consLabel.ipady = 4;
		oNodeTimeLinesLabelPanel.removeAll();
		oNodeTimeLinesLabelPanel.setLayout(layoutLabel);
	    
	    // ADD HEADER
		createTimeLineHeader();
		oToolBarPanel.setLocation(0,0);
	    add(oToolBarPanel, TOOLBAR_LAYER);
	    oToolBarPanel.setVisible(true);
	    
		oHeaderPanel.setLocation(leftBorder,0);
	    add(oHeaderPanel, HEADER_LAYER);
	    oHeaderPanel.setVisible(true);

	    // GET NODES
		Component[] array = oMovieMapViewPane.getComponentsInLayer((UIViewPane.NODE_LAYER).intValue());
		Vector<UINode> nodes = new Vector<UINode>(array.length);
		for(int i=0;i<array.length;i++) {
			nodes.addElement((UINode)array[i]);
		}				
		nodes = UIUtilities.sortList(nodes); 		
		int count = nodes.size();	    
	    
	    // ADD MOVIE TIME LINES	- sort alphabetically by movie label  
		Vector<UIMoviePanel> movies = oMovieMapViewPane.getMovies();
		
		/*Object[] sa = new Object[movies.size()];
		movies.copyInto(sa);
		List l = Arrays.asList(sa);		
		Collections.sort(l, new Comparator() {
			public int compare(Object o1, Object o2) {
				UIMoviePanel data1 = (UIMoviePanel)o1;
				UIMoviePanel data2 = (UIMoviePanel)o2;				
				String s1 = data1.getMovieData().getMovieName();
				String s2 = data2.getMovieData().getMovieName();
				return  s1.compareTo(s2);
			}
		});		
		movies.removeAllElements();
		movies.addAll(l);*/

		int countj = movies.size();		
		Vector<UIMoviePanel> sortedmovies = new Vector<UIMoviePanel>(countj); 
		for (int j=0; j<countj; j++) {
			sortedmovies.add(movies.elementAt(j));			
		}
		sortedmovies = UIMovieUtilities.sortList(sortedmovies);	
		
		countj = sortedmovies.size();
		rowCount = count+countj;

		for (int j=0; j<countj; j++) {
			UIMoviePanel panel = (UIMoviePanel)sortedmovies.elementAt(j);
			if (j == countj-1 && count == 0) {
	        	cons.weighty = 2;
	        	consLabel.weighty = 2;
			} else {
	        	cons.weighty = 0;
	        	consLabel.weighty = 2;
			}

			createMovieTimeLine(panel);
		}
	    
	    // ADD NODE TIME LINES	    		
		for(int i=0;i<count;i++) {
			UINode uinode = (UINode)nodes.elementAt(i);	  	
	        if (i == count-1) {
	        	cons.weighty = 2;
	        	consLabel.weighty = 2;
	        } else {
	        	cons.weighty = 0;
	        	consLabel.weighty = 0;
	        }
	        
        	createNodeTimeLine(uinode);	            
 	    }
				
		recalculateRequiredTimeline();
	}
	
	
	
	/**
	 * Create the header bar with the times marked on.
	 * @return the JPanel holding the header bar.
	 */
	private void createTimeLineHeader() {
		JPanel main = new JPanel(new BorderLayout());
		main.setBackground(NODE_TIMELINE_BACKGROUND_COLOUR);

		oToolBarPanel = new JPanel(new BorderLayout());
		JToolBar leftToolBar = new JToolBar();
		oToolBarPanel.add(leftToolBar, BorderLayout.CENTER);
		leftToolBar.setFloatable(false);
		leftToolBar.setMargin(new Insets(5,5,5,5));
		leftToolBar.setBorder(new LineBorder(HEADER_BORDER_COLOUR, 1));						
		
	    timelineHeader = new UITimeLineHeader(this, pixel_time_scale);		   
	    timelineHeader.setBorder(new LineBorder(HEADER_BORDER_COLOUR, 1));
	    
		masterPlayButton = new UIImageButton(new ImageIcon(UIImages.sPATH + "play.gif")); //$NON-NLS-1$
		masterPlayButton.setActionCommand("Play"); //$NON-NLS-1$
		masterPlayButton.setBorder(new EmptyBorder(0,0,0,5));
		masterPlayButton.setToolTipText(LanguageProperties.getString(LanguageProperties.MOVIE_BUNDLE, "UITimeLinesController.playButtonTip")); //$NON-NLS-1$
		masterPlayButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (masterPlayButton.getActionCommand().equals("Play")) { //$NON-NLS-1$
					start();
				} else {
					stop();
				}
			}
		});

		masterResetButton = new UIImageButton(new ImageIcon(UIImages.sPATH + "restore-start.gif")); //$NON-NLS-1$
		masterResetButton.setActionCommand("Reset"); //$NON-NLS-1$
		masterResetButton.setBorder(null);
		masterResetButton.setToolTipText(LanguageProperties.getString(LanguageProperties.MOVIE_BUNDLE, "UITimeLinesController.restButtonTip")); //$NON-NLS-1$
		masterResetButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				lastTime = new Time(0);
				controller.setMediaTime(lastTime);
				setMovieTimes(0);
			}
		});
		
		leftToolBar.add(masterPlayButton, BorderLayout.WEST);
		leftToolBar.add(masterResetButton, BorderLayout.EAST);
		
		// ZOOM IN AND OUT
		pbZoomIn = new UIImageButton(UIImages.get(IUIConstants.ZOOM_IN_ICON));
		pbZoomIn.setBorder(new EmptyBorder(0,0,0,5));
		pbZoomIn.setToolTipText(LanguageProperties.getString(LanguageProperties.TOOLBARS_BUNDLE, "UIToolBarZoom.zoomOut")); //$NON-NLS-1$
		pbZoomIn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (position < zoomScales.length-1) {
					position++;
				}
				pixel_time_scale = zoomScales[position];
				scaleWidth();												
				timelineHeader.setScale(pixel_time_scale);

				UITimeLineForMovie movieSlider = null;	    
				for (Enumeration<UITimeLineForMovie> bars = htMovieSliders.elements(); bars.hasMoreElements();) {
					movieSlider = (UITimeLineForMovie)bars.nextElement();
					movieSlider.setScale(pixel_time_scale);
				}
			    
				UITimeLineForNode nodeSlider = null;	    
				for (Enumeration<UITimeLineForNode> bars = htNodeSliders.elements(); bars.hasMoreElements();) {
					nodeSlider = (UITimeLineForNode)bars.nextElement();
					nodeSlider.setScale(pixel_time_scale);
				}
			}
		});
		pbZoomIn.setEnabled(true);		
		
		pbZoomOut = new UIImageButton(UIImages.get(IUIConstants.ZOOM_OUT_ICON));
		pbZoomOut.setBorder(new EmptyBorder(0,0,0,5));
		pbZoomOut.setToolTipText(LanguageProperties.getString(LanguageProperties.TOOLBARS_BUNDLE, "UIToolBarZoom.zoomIn")); //$NON-NLS-1$
		pbZoomOut.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (position > 0) {
					position--;
				}
				pixel_time_scale = zoomScales[position];
				scaleWidth();																
				timelineHeader.setScale(pixel_time_scale);
				
				UITimeLineForMovie movieSlider = null;	    
				for (Enumeration<UITimeLineForMovie> bars = htMovieSliders.elements(); bars.hasMoreElements();) {
					movieSlider = (UITimeLineForMovie)bars.nextElement();
					movieSlider.setScale(pixel_time_scale);
				}
			    
				UITimeLineForNode nodeSlider = null;	    
				for (Enumeration<UITimeLineForNode> bars = htNodeSliders.elements(); bars.hasMoreElements();) {
					nodeSlider = (UITimeLineForNode)bars.nextElement();
					nodeSlider.setScale(pixel_time_scale);
				}
			}
		});
		pbZoomOut.setEnabled(true);
				
		leftToolBar.addSeparator();
		
		leftToolBar.add(pbZoomIn, BorderLayout.WEST);
		leftToolBar.add(pbZoomOut, BorderLayout.EAST);
		
		leftBorder = leftToolBar.getPreferredSize().width+1;
		//if (leftBorder < TIMELINES_START_OFFSET) {
		//	leftBorder = TIMELINES_START_OFFSET;
		//}
		
		leftToolBar.setPreferredSize(new Dimension(leftBorder-1, leftToolBar.getPreferredSize().height));
		leftToolBar.setSize(new Dimension(leftBorder-1, leftToolBar.getPreferredSize().height));
		leftToolBar.setMaximumSize(new Dimension(leftBorder-1, leftToolBar.getPreferredSize().height));
		leftToolBar.setMinimumSize(new Dimension(leftBorder-1, leftToolBar.getPreferredSize().height));

	    JPanel headerPanel = new JPanel(new BorderLayout());
	    headerPanel.add(timelineHeader, BorderLayout.CENTER);
	    
		JToolBar rightToolBar = new JToolBar();
		rightToolBar.setFloatable(false);
		rightToolBar.setMargin(new Insets(5,5,5,5));
		rightToolBar.setBorder(new LineBorder(HEADER_BORDER_COLOUR, 1));						
	    
	    lessButton = new UIImageButton(new ImageIcon(UIImages.sPATH + "delete2.png")); //$NON-NLS-1$
	    lessButton.setBorder(new EmptyBorder(0,5,0,5));
	    lessButton.setToolTipText(LanguageProperties.getString(LanguageProperties.MOVIE_BUNDLE, "UITimeLinesController.offTimeTip")); //$NON-NLS-1$
	    lessButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
		    	currentTimelineDuration -= EXTRA_TIME;
		    	if (currentTimelineDuration <= requiredTimelineDuration) {
		    		currentTimelineDuration = requiredTimelineDuration;
		    		lessButton.setEnabled(false);
		    	} else {
			    	lessButton.setEnabled(true);
		    	}
		    	scaleWidth();	
			}
		});
    	if (currentTimelineDuration <= requiredTimelineDuration) {
	    	lessButton.setEnabled(false);
	    } else {
	    	lessButton.setEnabled(true);
	    }
    	rightToolBar.add(lessButton);
	    
	    moreButton = new UIImageButton(new ImageIcon(UIImages.sPATH + "add-green.png"));	     //$NON-NLS-1$
	    moreButton.setBorder(new EmptyBorder(0,0,0,5));
	    moreButton.setToolTipText(LanguageProperties.getString(LanguageProperties.MOVIE_BUNDLE, "UITimeLinesController.addTimeTip")); //$NON-NLS-1$
	    moreButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				currentTimelineDuration += EXTRA_TIME;
				scaleWidth();								
		    	if (currentTimelineDuration > requiredTimelineDuration) {
			    	lessButton.setEnabled(true);
			    } 
			}
		});
	    moreButton.setEnabled(true);
	    rightToolBar.add(moreButton);
	    
	    rightBorder = rightToolBar.getPreferredSize().width;
	    
	    rightToolBar.setSize(rightToolBar.getPreferredSize().width, rightToolBar.getPreferredSize().height);
	    rightToolBar.setPreferredSize(new Dimension(rightBorder, rightToolBar.getPreferredSize().height));
	    
	    //main.add(leftToolBar, BorderLayout.WEST);
	    main.add(headerPanel, BorderLayout.CENTER);	  
	    main.add(rightToolBar, BorderLayout.EAST);
	    
	    oHeaderPanel = main;
	}
	
	/**
	 * Return the timeline Header panel;
	 */
	public UITimeLineHeader getHeader() {
		return timelineHeader;
	}
	
	/**
	 * Create a movie time line.
	 * @param movie the Movie to create a timeline for.
	 * @return a JPanel holding the movie time line.
	 */
	private void createMovieTimeLine(UIMoviePanel movie) {
		// Remove previously added one if there, else they pile up!
		movie.removePropertyChangeListener(this); 
		movie.addPropertyChangeListener(this);    	

		//JPanel main = new JPanel(new BorderLayout());
		//main.setBorder(new LineBorder(Color.black, 1));						
		//main.setBackground(MOVIE_TIMELINE_BACKGROUND_COLOUR);
		
	    BorderLayout border = new BorderLayout();
	    border.setVgap(5);
	    border.setHgap(5);
	    final JPanel leftPanel = new JPanel(border);
	    leftPanel.setBackground(MOVIE_TIMELINE_BACKGROUND_COLOUR);
		leftPanel.setBorder(new CompoundBorder(new LineBorder(MOVIE_TIMELINE_BORDER_COLOUR, 1), new EmptyBorder(3,3,3,3)));
		
	    final JLabel movieicon = new JLabel(new ImageIcon(UIImages.sPATH + "movie.gif")); //$NON-NLS-1$
	    movieicon.setBackground(MOVIE_TIMELINE_BACKGROUND_COLOUR);
	    movieicon.setAlignmentY(JLabel.CENTER_ALIGNMENT);
	    movieicon.setAlignmentX(JLabel.WEST);
	    /*movieicon.setToolTipText("Double-click to open: "+text);	
	    movieicon.addMouseListener(new MouseAdapter() {
	    	public void mouseClicked(MouseEvent e) {
	     		boolean isLeftMouse = SwingUtilities.isLeftMouseButton(e);		
	    		if (ProjectCompendium.isMac &&
	    			(e.getButton() == 3 && e.isShiftDown())) {
	    			isLeftMouse = false;
	    		}        		
	       		int clickCount = e.getClickCount();
	       		if (isLeftMouse && clickCount == 1) {		
	       			oMovieMapViewPane.setSelectedNode(null,ICoreConstants.DESELECTALL);
	       			fuinode.setSelected(true);
		    		oMovieMapViewPane.setSelectedNode(fuinode,ICoreConstants.SINGLESELECT);
	       		} else if (isLeftMouse && clickCount == 2) {		
	      			((NodeUI)fuinode.getUI()).openNode();
	       		}
	    	}
	    });*/
	    
	    leftPanel.add(movieicon, BorderLayout.WEST);		
		
		
		final Movie fMovie = movie.getMovieData();	    
		final String name = fMovie.getMovieName();
		
	    final JLabel flabel = new JLabel(name);
		final JTextField field = new JTextField(name);
		field.setEditable(true);
		field.setVisible(false);
		field.addFocusListener(new FocusAdapter() {
			public void focusLost(FocusEvent e) {
				String newName = field.getText();
				try {
					if (!fMovie.getMovieName().equals(newName)) {
						oMovieMapView.updateMovie(fMovie.getId(), fMovie.getLink(), newName, fMovie.getStartTime());
						refreshTimeLines();
					} else {
		       			field.setVisible(false);
		       			leftPanel.remove(field);
		       			leftPanel.add(flabel, BorderLayout.CENTER);
		       			flabel.setVisible(true);
		       			leftPanel.repaint();
					}
				} catch(Exception ex) {
					System.out.println("Exception: "+ex.getLocalizedMessage()); //$NON-NLS-1$
				}
			}
		});
		field.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String newName = field.getText();
				try {
					if (!fMovie.getMovieName().equals(newName)) {
						oMovieMapView.updateMovie(fMovie.getId(), fMovie.getLink(), newName, fMovie.getStartTime());
						refreshTimeLines();
					} else {
		       			field.setVisible(false);
		       			leftPanel.remove(field);
		       			leftPanel.add(flabel, BorderLayout.CENTER);
		       			flabel.setVisible(true);
		       			leftPanel.repaint();
					}
				} catch(Exception ex) {
					System.out.println("Exception: "+ex.getLocalizedMessage()); //$NON-NLS-1$
				}
			}
		});
		
	    flabel.setBackground(MOVIE_TIMELINE_BACKGROUND_COLOUR);
	    flabel.setAlignmentY(JLabel.CENTER_ALIGNMENT);
	    flabel.setAlignmentX(JLabel.WEST);
	    flabel.setToolTipText(LanguageProperties.getString(LanguageProperties.MOVIE_BUNDLE, "UITimeLinesController.editMovieTip")); //$NON-NLS-1$
	    flabel.addMouseListener(new MouseAdapter() {
	    	public void mouseClicked(MouseEvent e) {
	    		boolean isRightMouse = SwingUtilities.isRightMouseButton(e);
	    		boolean isLeftMouse = SwingUtilities.isLeftMouseButton(e);		
	    		if (ProjectCompendium.isMac &&
	    			( (e.getButton() == 3 && e.isShiftDown()) ||
	    			 (e.getButton() == 1 && e.isControlDown()) )) {
	    			isRightMouse = true;
	    			isLeftMouse = false;
	    		}		
	       		int clickCount = e.getClickCount();
	       		if (isRightMouse && clickCount == 1) {	
	       			Rectangle bounds = flabel.getBounds();
	       			field.setBounds(bounds);
	       			field.setText(name);
	       			leftPanel.remove(flabel);
	       			leftPanel.add(field, BorderLayout.CENTER);
	       			field.setVisible(true);
	       			field.requestFocus();
	       			field.setCaretPosition(0);
	       		} else if (isLeftMouse && clickCount == 1) {		
	       			flabel.requestFocus();
	       		}
	    	}
	    });	
		
	    leftPanel.add(flabel, BorderLayout.CENTER);
	    
	    UITimeLineForMovie movieline = null;
	    String sMovieID = movie.getMovieData().getId();
	    
	    //NEED TO FIND OUT WHY REUSING OBJECTS WAS BROKEN
		//if (htMovieSliders.containsKey(sMovieID)) {
		//	movieline = (UITimeLineForMovie)htMovieSliders.get(sMovieID);
		//} else {
			movieline = new UITimeLineForMovie(movie, oMovieMapViewPane ,oMovieMapView, this, controller);
			movieline.setBackground(MOVIE_TIMELINE_BACKGROUND_COLOUR);			
			movieline.setScale(pixel_time_scale);
	        htMovieSliders.put(sMovieID, movieline);
		//}
		
		movieline.setBorder(new LineBorder(MOVIE_TIMELINE_BORDER_COLOUR, 1));
		
	    JPanel moviePanel = new JPanel(new BorderLayout());
	    if (movie.isSelected()) {
	    	moviePanel.setBackground(MOVIE_TIMELINE_SELECTED_BACKGROUND_COLOUR);
	    } else {
	    	moviePanel.setBackground(MOVIE_TIMELINE_BACKGROUND_COLOUR);
	    }
	    moviePanel.add(movieline, BorderLayout.CENTER);
	    
	    //main.add(leftPanel, BorderLayout.WEST);
	    //main.add(moviePanel, BorderLayout.CENTER);	    

		leftPanel.setPreferredSize(new Dimension(leftBorder-1, ROW_HEIGHT));
		leftPanel.setSize(new Dimension(leftBorder-1, ROW_HEIGHT));
		leftPanel.setMaximumSize(new Dimension(leftBorder-1, ROW_HEIGHT));
		leftPanel.setMinimumSize(new Dimension(leftBorder-1, ROW_HEIGHT));

		moviePanel.setPreferredSize(new Dimension(moviePanel.getPreferredSize().width, ROW_HEIGHT));
		moviePanel.setSize(new Dimension(moviePanel.getPreferredSize().width, ROW_HEIGHT));
		moviePanel.setMaximumSize(new Dimension(moviePanel.getPreferredSize().width, ROW_HEIGHT));
		moviePanel.setMinimumSize(new Dimension(moviePanel.getPreferredSize().width, ROW_HEIGHT));

		layoutLabel.setConstraints(leftPanel, consLabel);		        
	    oNodeTimeLinesLabelPanel.add(leftPanel);

		layout.setConstraints(moviePanel, cons);		        
	    oNodeTimeLinesPanel.add(moviePanel);
	    	    
	    htMovieTimeLinePanels.put(movie.getMovieData().getId(), moviePanel);
	    htMovieTimeLineLabelPanels.put(movie.getMovieData().getId(), leftPanel);
	}	
	
	/**
	 * Create a Node timeline
	 * @param uinode the node to create a timeline for.
	 * @return a JPanel holding the node timeline.
	 */
	private void createNodeTimeLine(UINode uinode) {
		// Remove previously added one if there, else they pile up!
		uinode.removePropertyChangeListener(this); 
		uinode.addPropertyChangeListener(this);    			
		
		final UINode fuinode = uinode;
		final String text = uinode.getText();
		NodeSummary node = uinode.getNode();
		
		//final JPanel main = new JPanel(new BorderLayout());
		//main.setBackground(NODE_TIMELINE_BACKGROUND_COLOUR);
		
	    ImageIcon icon = UINode.getNodeImageSmall(node.getType());	
	    
	    BorderLayout border = new BorderLayout();
	    border.setVgap(5);
	    border.setHgap(5);
	    JPanel leftPanel = new JPanel(border);
		leftPanel.setBorder(new CompoundBorder(new LineBorder(NODE_TIMELINE_BORDER_COLOUR, 1), new EmptyBorder(3,3,3,3)));						
	    leftPanel.setBackground(NODE_TIMELINE_BACKGROUND_COLOUR);
	    
	    final JLabel nodelabel = new JLabel(icon);
	    nodelabel.setBackground(NODE_TIMELINE_BACKGROUND_COLOUR);
	    nodelabel.setAlignmentY(JLabel.CENTER_ALIGNMENT);
	    nodelabel.setAlignmentX(JLabel.WEST);
	    nodelabel.setToolTipText(LanguageProperties.getString(LanguageProperties.MOVIE_BUNDLE, "UITimeLinesController.openTip")+": "+text);	 //$NON-NLS-1$
	    nodelabel.addMouseListener(new MouseAdapter() {
	    	public void mouseClicked(MouseEvent e) {
	     		boolean isLeftMouse = SwingUtilities.isLeftMouseButton(e);		
	    		if (ProjectCompendium.isMac &&
	    			(e.getButton() == 3 && e.isShiftDown())) {
	    			isLeftMouse = false;
	    		}        		
	       		int clickCount = e.getClickCount();
	       		if (isLeftMouse && clickCount == 1) {		
	       			oMovieMapViewPane.setSelectedNode(null,ICoreConstants.DESELECTALL);
	       			fuinode.setSelected(true);
		    		oMovieMapViewPane.setSelectedNode(fuinode,ICoreConstants.SINGLESELECT);
	       		} else if (isLeftMouse && clickCount == 2) {		
	      			((NodeUI)fuinode.getUI()).openNode();
	       		}
	    	}
	    });
	    
	    leftPanel.add(nodelabel, BorderLayout.WEST);
	    
	    final JLabel flabel = new JLabel(text);
	    flabel.setBackground(NODE_TIMELINE_BACKGROUND_COLOUR);
	    flabel.setAlignmentY(JLabel.CENTER_ALIGNMENT);
	    flabel.setAlignmentX(JLabel.WEST);
	    flabel.setToolTipText(LanguageProperties.getString(LanguageProperties.MOVIE_BUNDLE, "UITimeLinesController.openTip")+text); //$NON-NLS-1$
	    flabel.addMouseListener(new MouseAdapter() {
	    	public void mouseClicked(MouseEvent e) {
	    		boolean isLeftMouse = SwingUtilities.isLeftMouseButton(e);		
	    		if (ProjectCompendium.isMac &&
	    			(e.getButton() == 3 && e.isShiftDown())) {
	    			isLeftMouse = false;
	    		}        		
	       		int clickCount = e.getClickCount();
	       		if (isLeftMouse && clickCount == 1) {		
	       			oMovieMapViewPane.setSelectedNode(null,ICoreConstants.DESELECTALL);
	       			fuinode.setSelected(true);
		    		oMovieMapViewPane.setSelectedNode(fuinode,ICoreConstants.SINGLESELECT);
	       		} else if (isLeftMouse && clickCount == 2) {		
	       			((NodeUI)fuinode.getUI()).openNode();
	       		}
	   	}
	    });
	    
	     // If the node label has changed when the node looses focus
	     // repaint the timelines so the order is correct.
	    // TO DO: need to remove previous listener? or they will pile up
	    uinode.addFocusListener(new FocusAdapter() {
	    	public void focusLost(FocusEvent e) {
	    		String text = flabel.getText();
	    		if (!text.equals(fuinode.getText())) {
	    	        refreshTimeLines();
	    		}
	    	}
	    });
	   
	    leftPanel.add(flabel, BorderLayout.CENTER);	    

		//main.add(leftPanel, BorderLayout.WEST);
	    		    	    
	    UITimeLineForNode progressSlider = null;	
	    
	    //NEED TO FIND OUT WHY REUSING OBJECTS WAS BROKEN
		//if (htNodeSliders.containsKey(node.getId())) {
		//	progressSlider = (UITimeLineForNode)htNodeSliders.get(node.getId());
		//} else {
	        progressSlider = new UITimeLineForNode(uinode, this.oMovieMapView, this, controller);
	        progressSlider.setBackground(NODE_TIMELINE_BACKGROUND_COLOUR);			
	        progressSlider.setScale(pixel_time_scale);
	        htNodeSliders.put(node.getId(), progressSlider);
		//}
			
	    JPanel panelProgress = new JPanel ( new BorderLayout() );
	    panelProgress.setBorder(new LineBorder(NODE_TIMELINE_BORDER_COLOUR, 1));						
	    if (uinode.isSelected()) {
	    	panelProgress.setBackground(NODE_TIMELINE_SELECTED_BACKGROUND_COLOUR);
	    } else {
	       	panelProgress.setBackground(NODE_TIMELINE_BACKGROUND_COLOUR);
	    }
	    panelProgress.add(progressSlider, BorderLayout.CENTER);	    
	    //main.add(panelProgress, BorderLayout.CENTER);
	   
	    panelProgress.setPreferredSize(panelProgress.getPreferredSize());
	    
		
	    leftPanel.setPreferredSize(new Dimension(leftBorder-1, ROW_HEIGHT));
		leftPanel.setSize(new Dimension(leftBorder-1, ROW_HEIGHT));
		leftPanel.setMaximumSize(new Dimension(leftBorder-1, ROW_HEIGHT));
		leftPanel.setMinimumSize(new Dimension(leftBorder-1, ROW_HEIGHT));

		panelProgress.setPreferredSize(new Dimension(panelProgress.getPreferredSize().width, ROW_HEIGHT));
		panelProgress.setSize(new Dimension(panelProgress.getPreferredSize().width, ROW_HEIGHT));
		panelProgress.setMaximumSize(new Dimension(panelProgress.getPreferredSize().width, ROW_HEIGHT));
		panelProgress.setMinimumSize(new Dimension(panelProgress.getPreferredSize().width, ROW_HEIGHT));

	    layoutLabel.setConstraints(leftPanel, consLabel);		        
	    oNodeTimeLinesLabelPanel.add(leftPanel);	 	 
	    
	    layout.setConstraints(panelProgress, cons);		        
	    oNodeTimeLinesPanel.add(panelProgress);	
	    
       	htNodeTimeLinePanels.put(uinode.getNode().getId(), panelProgress);	    
       	htNodeTimeLineLabelPanels.put(uinode.getNode().getId(), leftPanel);	    
	}
    

    /**
     * Update the movies current positions.
     * @param millis
     */
    public void setMovieTimes(long millis) {
	    for (Enumeration<UITimeLineForMovie> e = htMovieSliders.elements(); e.hasMoreElements();) {
	    	UITimeLineForMovie progressSlider = (UITimeLineForMovie)e.nextElement();
	    	progressSlider.setMovieTime(millis);
	    }
    }
	
	/**
     * Start the master timeline controller
     */
    public void start() {
		if (controller.getState() == Player.Started) {
			return;
		}
		masterPlayButton.setActionCommand("Pause"); //$NON-NLS-1$
		masterPlayButton.setIcon(new ImageIcon(UIImages.sPATH + "pause.gif")); //$NON-NLS-1$
		masterPlayButton.setToolTipText(LanguageProperties.getString(LanguageProperties.MOVIE_BUNDLE, "UITimeLinesController.pasueButtonTip")); //$NON-NLS-1$
		
		controller.syncStart(lastTime);
		startMovies();		
	}
	
    /**
     * Stop the master timeline controller
     */
    public void stop() {
		masterPlayButton.setActionCommand("Play"); //$NON-NLS-1$
		masterPlayButton.setIcon(new ImageIcon(UIImages.sPATH + "play.gif")); //$NON-NLS-1$
		masterPlayButton.setToolTipText(LanguageProperties.getString(LanguageProperties.MOVIE_BUNDLE, "UITimeLinesController.playButtonTip")); //$NON-NLS-1$

		lastTime = controller.getMediaTime();
		controller.stop();
		stopMovies();
    }

	/**
	 * Return the current state of the player.
	 */
	public int getPlayerState() {
		return controller.getState();
	}
	
    /**
     * Return the current time set in the controller in milliseconds.
     * @return the current time set in the controller in milliseconds
     */
    public long getCurrentTime() {
    	return TimeUnit.NANOSECONDS.toMillis(controller.getMediaNanoseconds());
    }
    
    /**
     * Let the movie time lines know that the controller has been stopped.
     */
    private void stopMovies() {
		UITimeLineForMovie movieSlider = null;	    
		for (Enumeration<UITimeLineForMovie> bars = htMovieSliders.elements(); bars.hasMoreElements();) {
			movieSlider = (UITimeLineForMovie)bars.nextElement();
			movieSlider.stopMovie();
		}    	
    }
  
    /**
     * Let the movie time lines know that the controller has been started.
     */
    private void startMovies() {
		UITimeLineForMovie movieSlider = null;	    
		for (Enumeration<UITimeLineForMovie> bars = htMovieSliders.elements(); bars.hasMoreElements();) {
			movieSlider = (UITimeLineForMovie)bars.nextElement();
			movieSlider.startMovie(TimeUnit.NANOSECONDS.toMillis(controller.getMediaNanoseconds()));
		}    	
    }
 
    /** 
     * Clear all selected items
     */
    public void clearSelection() {
		UITimeLineForMovie movieSlider = null;	    
		for (Enumeration<UITimeLineForMovie> bars = htMovieSliders.elements(); bars.hasMoreElements();) {
			movieSlider = (UITimeLineForMovie)bars.nextElement();
			movieSlider.clearSelection();
		}    	
		
		UITimeLineForNode nodeSlider = null;	    
		for (Enumeration<UITimeLineForNode> bars = htNodeSliders.elements(); bars.hasMoreElements();) {
			nodeSlider = (UITimeLineForNode)bars.nextElement();
			nodeSlider.clearSelection();
		}    	
    }
    
    /**
     * Update all time lines that a move had occurred in order to update selected items
     */
    public void dragMove(String fromID, long changeValue) {
    	//check that all items can move first.
    	boolean canMove = true;
		UITimeLineForMovie movieSlider = null;	    
		for (Enumeration<UITimeLineForMovie> bars = htMovieSliders.elements(); bars.hasMoreElements();) {
			movieSlider = (UITimeLineForMovie)bars.nextElement();
			if (!movieSlider.canAllMove(changeValue)) {
				canMove = false;
				break;
			}
		}    	
		
		if (canMove) {
			UITimeLineForNode nodeSlider = null;	    
			for (Enumeration<UITimeLineForNode> bars = htNodeSliders.elements(); bars.hasMoreElements();) {
				nodeSlider = (UITimeLineForNode)bars.nextElement();
				if (!nodeSlider.canAllMove(changeValue)) {
					canMove = false;
					break;
				}
			}  
		}
    	
    	if (canMove) {
			movieSlider = null;	    
			for (Enumeration<UITimeLineForMovie> bars = htMovieSliders.elements(); bars.hasMoreElements();) {
				movieSlider = (UITimeLineForMovie)bars.nextElement();
				movieSlider.dragMove(fromID, changeValue);
			}    	
			
			UITimeLineForNode nodeSlider = null;	    
			for (Enumeration<UITimeLineForNode> bars = htNodeSliders.elements(); bars.hasMoreElements();) {
				nodeSlider = (UITimeLineForNode)bars.nextElement();
				nodeSlider.dragMove(fromID, changeValue);
			} 
    	}
		recalculateRequiredTimeline();	
    }
 
    /**
     * Save the change to all selected items
     */
    public void dragComplete(String fromID) {
		UITimeLineForMovie movieSlider = null;	    
		for (Enumeration<UITimeLineForMovie> bars = htMovieSliders.elements(); bars.hasMoreElements();) {
			movieSlider = (UITimeLineForMovie)bars.nextElement();
			movieSlider.dragComplete(fromID);
		}    	
		
		UITimeLineForNode nodeSlider = null;	    
		for (Enumeration<UITimeLineForNode> bars = htNodeSliders.elements(); bars.hasMoreElements();) {
			nodeSlider = (UITimeLineForNode)bars.nextElement();
			nodeSlider.dragComplete(fromID);
		}    	
		recalculateRequiredTimeline();	
    }
    
    /**
     * If the vertical scrollbar moved, relocate screen elements as required.
     */
    public void adjustmentValueChanged(AdjustmentEvent evt) {
        if (evt.getID() == AdjustmentEvent.ADJUSTMENT_VALUE_CHANGED) {
        	JScrollBar bar = (JScrollBar)evt.getSource();
            if (bar.getOrientation() == Adjustable.HORIZONTAL) {
             	if (oNodeTimeLinesLabelPanel != null) {
            		Point currentLoc = oNodeTimeLinesLabelPanel.getLocation();
            		oNodeTimeLinesLabelPanel.setLocation(bar.getValue(), currentLoc.y);
            	}
            	if (oToolBarPanel != null) {
            		Point currentLoc = oToolBarPanel.getLocation();
            		oToolBarPanel.setLocation(bar.getValue(), currentLoc.y);
            	}
            } else {
            	if (oHeaderPanel != null) {
                	Point currentLoc = oHeaderPanel.getLocation();
            		oHeaderPanel.setLocation(currentLoc.x, bar.getValue());
            	}
            	if (oToolBarPanel != null) {
                	Point currentLoc = oToolBarPanel.getLocation();
                	oToolBarPanel.setLocation(currentLoc.x, bar.getValue());
            	}
            }
            parentPanel.validate();
        } 
    }
     
	/**
	 * Handles property change events.
	 * @param evt, the associated PropertyChangeEvent object.
	 */
	public void propertyChange(PropertyChangeEvent evt) {
	    String prop = evt.getPropertyName();
		Object source = evt.getSource();
		Object oldvalue = evt.getOldValue();
	    Object newvalue = evt.getNewValue();
	    
	    if (source instanceof UIMovieMapViewPane) {
	    	if (prop.equals(UIMovieMapViewPane.UINODE_ADDED)) {
		    	UINode uinode = (UINode)newvalue;
		    	addNode(uinode);
	    	} else if (prop.equals(UIMovieMapViewPane.UINODE_REMOVED)) {
		    	UINode uinode = (UINode)newvalue;
				uinode.removePropertyChangeListener(this);    			    	
	    		String sNodeID = uinode.getNode().getId();	    		
	    		if (htNodeSliders.containsKey(sNodeID)) {
	    			htNodeSliders.remove(sNodeID);
	    		}
	    		if (htNodeTimeLinePanels.containsKey(sNodeID)) {
	    			htNodeTimeLinePanels.remove(sNodeID);
	    		}
	    		if (htNodeTimeLineLabelPanels.containsKey(sNodeID)) {
	    			htNodeTimeLineLabelPanels.remove(sNodeID);
	    		}
	    		refreshTimeLines();
	    	} else if (prop.equals(UIMovieMapViewPane.UIMOVIE_ADDED)) {
	    		refreshTimeLines();
	    	} else if (prop.equals(UIMovieMapViewPane.UIMOVIE_REMOVED)) {
		    	UIMoviePanel uimovie = (UIMoviePanel)newvalue;
	    		String sMovieID = uimovie.getMovieData().getId();	    		
	    		if (htMovieSliders.containsKey(sMovieID)) {
	    			htMovieSliders.remove(sMovieID);
	    		}
	    		if (htMovieTimeLinePanels.containsKey(sMovieID)) {
	    			htMovieTimeLinePanels.remove(sMovieID);
	    		}
	    		if (htMovieTimeLineLabelPanels.containsKey(sMovieID)) {
	    			htMovieTimeLineLabelPanels.remove(sMovieID);
	    		}
	    			    		
	    		refreshTimeLines();
	    	}
	    } else if (source instanceof UINode) {
	    	if (prop.equals(UINode.SELECTED_PROPERTY)) {	 
	    		UINode node = (UINode)source;
	    		String sNodeID = node.getNode().getId();

	    		if (htNodeTimeLineLabelPanels.containsKey(sNodeID)) {
	    			JPanel panel  = (JPanel)htNodeTimeLineLabelPanels.get(sNodeID);
		        	if ((Boolean)newvalue) {
		        		panel.setBackground(NODE_TIMELINE_SELECTED_BACKGROUND_COLOUR);
		        	} else {
		        		panel.setBackground(NODE_TIMELINE_BACKGROUND_COLOUR);
		        	}
	    			
	    	       	int countj = panel.getComponentCount();
	    	       	for (int j=0; j<countj; j++) {
	    	        	Component innernext = panel.getComponent(j);	    	        	
	    	        	if ((Boolean)newvalue) {
	    	        		innernext.setBackground(NODE_TIMELINE_SELECTED_BACKGROUND_COLOUR);
	    	        	} else {
	    	        		innernext.setBackground(NODE_TIMELINE_BACKGROUND_COLOUR);
	    	        	}
	    	       	}
	    		}

	    		if (htNodeTimeLinePanels.containsKey(sNodeID)) {
	    			JPanel panel  = (JPanel)htNodeTimeLinePanels.get(sNodeID);
		        	if ((Boolean)newvalue) {
		        		panel.setBackground(NODE_TIMELINE_SELECTED_BACKGROUND_COLOUR);
		        	} else {
		        		panel.setBackground(NODE_TIMELINE_BACKGROUND_COLOUR);
		        	}
	    			
	    	       	int countj = panel.getComponentCount();
	    	       	for (int j=0; j<countj; j++) {
	    	        	Component innernext = panel.getComponent(j);	    	        	
	    	        	if ((Boolean)newvalue) {
	    	        		innernext.setBackground(NODE_TIMELINE_SELECTED_BACKGROUND_COLOUR);
	    	        	} else {
	    	        		innernext.setBackground(NODE_TIMELINE_BACKGROUND_COLOUR);
	    	        	}
	    	       	}
	    	       	
	    	       	//works a bit. scrolls down, but not up?
	    	       	final JPanel fpanel = panel;
   					SwingUtilities.invokeLater(new Runnable() {
   						public void run() {
   							fpanel.scrollRectToVisible(fpanel.getBounds());
   						}
   					});
	    		}

	    		if (htNodeSliders.containsKey(sNodeID)) {
	    			UITimeLineForNode line = (UITimeLineForNode)htNodeSliders.get(sNodeID);
	    			line.refreshNodeTimeDialog();
	    		}
	    	}
	    } else if (source instanceof MovieMapView) {
	    	if (prop.equals(MovieMapView.MOVIE_CHANGED_PROPERTY)) {
	    		Movie oldmovie = (Movie)oldvalue;
	    		Movie movie = (Movie)newvalue;
	    		if ((!oldmovie.getMovieName().equals(movie.getMovieName())) || 
	    				(oldmovie.getStartTime() != movie.getStartTime())) {
	    			
		    		if (htMovieSliders.containsKey(movie.getId())) {
		    			UITimeLineForMovie panel  = htMovieSliders.get(movie.getId());
		    			panel.getMovie().setMovieData(movie);
		    		}
		    		refreshTimeLines();
	    		}
	    	}
	    } else if (source instanceof UIMoviePanel) {
	    	UIMoviePanel moviepanel = (UIMoviePanel)source;
	    	if (prop.equals(UIMoviePanel.SELECTED_PROPERTY)) {
	    		String sMovieID = moviepanel.getMovieData().getId();
	    		if (htMovieTimeLineLabelPanels.containsKey(sMovieID)) {
	    			JPanel panel  = (JPanel)htMovieTimeLineLabelPanels.get(sMovieID);
		        	if ((Boolean)newvalue) {
		        		panel.setBackground(MOVIE_TIMELINE_SELECTED_BACKGROUND_COLOUR);
		        	} else {
		        		panel.setBackground(MOVIE_TIMELINE_BACKGROUND_COLOUR);
		        	}
	    			
	    	       	int countj = panel.getComponentCount();
	    	       	for (int j=0; j<countj; j++) {
			        	Component innernext = panel.getComponent(j);	    	        	
			        	if ((Boolean)newvalue) {
			        		innernext.setBackground(MOVIE_TIMELINE_SELECTED_BACKGROUND_COLOUR);
			        	} else {
			        		innernext.setBackground(MOVIE_TIMELINE_BACKGROUND_COLOUR);
			        	}
	    	       	}
	    		}
	    		if (htMovieTimeLinePanels.containsKey(sMovieID)) {
	    			JPanel panel  = (JPanel)htMovieTimeLinePanels.get(sMovieID);
		        	if ((Boolean)newvalue) {
		        		panel.setBackground(MOVIE_TIMELINE_SELECTED_BACKGROUND_COLOUR);
		        	} else {
		        		panel.setBackground(MOVIE_TIMELINE_BACKGROUND_COLOUR);
		        	}
	    			
	    	       	int countj = panel.getComponentCount();
	    	       	for (int j=0; j<countj; j++) {
			        	Component innernext = panel.getComponent(j);	    	        	
			        	if ((Boolean)newvalue) {
			        		innernext.setBackground(MOVIE_TIMELINE_SELECTED_BACKGROUND_COLOUR);
			        	} else {
			        		innernext.setBackground(MOVIE_TIMELINE_BACKGROUND_COLOUR);
			        	}
	    	       	}
	
	    	       	//scroll to visible
			    	//parentPanel.scrollRectToVisible(new Rectangle(oNodeTimeLinesPanel.getX(), oNodeTimeLinesPanel.getY(), oNodeTimeLinesPanel.getWidth(), oNodeTimeLinesPanel.getHeight())); 	    	       	
			    	parentPanel.scrollToRectangle(oNodeTimeLinesPanel.getX(), oNodeTimeLinesPanel.getY(), oNodeTimeLinesPanel.getWidth(), oNodeTimeLinesPanel.getHeight()); 	    	       	
	    		}
	    		
	    		/*if (htMovieSliders.containsKey(sMovieID)) {
	    			UITimeLineForMovie line = (UITimeLineForMovie)htMovieSliders.get(sMovieID);
	    			//line.refreshNodeTimeDialog();
	    		}*/
	    	}
	    }
	}    
		
	/**
	 * Jump to the time point when the node with the given id is first shown.
	 * @param sNodeID the id of the node to jump to.
	 */
	public void jumpToNode(String sNodeID) {
		UITimeLineForNode nodeSlider = null;	
		if (htNodeSliders.containsKey(sNodeID)) {
			nodeSlider = (UITimeLineForNode)htNodeSliders.get(sNodeID);
			nodeSlider.showFirstTime();
		}
	}	
	
	/**
	 * Return the time length, in milliseconds, for a new time span.
	 * @return
	 */
	public long getDefaultNodeTimeSpanLength() {
    	return DEFAULT_PIXEL_SPAN_LENGTH*pixel_time_scale;
	}
	
	/**
	 * There was a bug which meant many first spans where sometimes added.
	 * This was an attempt to stop that.
	 * @param uinode
	 */
	private synchronized void addNode(UINode uinode) {
		if (uinode != null) {
			// add a first span, if it does not have one.
			String sNodeID = uinode.getNode().getId();
	    	Hashtable<String, NodePositionTime> times = oMovieMapView.getTimesForNode(sNodeID);
	    	if (times.size() == 0) {
				try {
			    	long currentTime = new Double(controller.getMediaTime().getNanoseconds()).longValue();
			    	long milliCurrentTime = TimeUnit.NANOSECONDS.toMillis(currentTime);
			    	long milliDefaultSpan = getDefaultNodeTimeSpanLength();
					oMovieMapView.addNodeTime(sNodeID, milliCurrentTime, milliCurrentTime+milliDefaultSpan, uinode.getLocation().x, uinode.getLocation().y);    	    		
				} catch(Exception ex) {
					ex.printStackTrace();
					ProjectCompendium.APP.displayError(ex.getLocalizedMessage());
				}
	    	}			
			refreshTimeLines();
		}		
	}   
	
    public void setCursor(Cursor cursor) {
    	bar.setCursor(cursor);
    }
}
