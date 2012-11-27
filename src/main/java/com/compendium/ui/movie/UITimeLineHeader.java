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
import javax.swing.border.LineBorder;

import java.awt.*;
import java.awt.event.*;

/**
 * This class draws the time line header with the time markings on it.
 * @author Michelle Bachler
 */
public class UITimeLineHeader extends JComponent { 
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** The number of pixels that represents a unit of screen marking.*/
	public static final int	PIXELS_PER_UNIT = 20;
	
	/** After how many unit jumps to draw the time labels */
	public static final int	UNIT_LABEL_INTERNAL = 5;
 
	/** The width of this header.*/
	private int width;
	
	/** The width of the area represented in time line itself.*/
	private int timelineWidth;
       
    /** The parent panel of this header.*/
    private UITimeLinesController controlPanel;

    /** How many hundreths of a second that a screen pixel represents. */
    private int pixel_time_scale = UITimeLinesController.DEFAULT_PIXEL_TIME_SCALE;        

    /**
     * The constructor that draws this timeline header marking the time intervals.
     * @param cp the parent controller panel.
     */
    public UITimeLineHeader(UITimeLinesController cp, int scale) {
       	this.pixel_time_scale = scale;
    	this.controlPanel = cp;
    	this.timelineWidth = this.width - UITimeLinesController.TIMELINE_LEFT_OFFSET;

    	//this.setBackground(new Color(156, 154, 206));
    	//this.setBackground(new Color(155, 174, 50));    	    	
    	
    	addComponentListener(new ComponentAdapter() {
		   public void componentResized ( ComponentEvent event ) {
		        Dimension dim = getSize();
		        if ( dim.width - UITimeLinesController.TIMELINE_LEFT_OFFSET < 1 )
		            return;

		        width = dim.width;
		        timelineWidth = width-UITimeLinesController.TIMELINE_LEFT_OFFSET;
		        repaint();
		    }
    	});
    	
    	this.setPreferredSize(new Dimension(getPreferredSize().width, 26));
    }
    
    /**
     * Set the new scale to use to draw this time line header, then  redraw it.
     * @param scale the scale to use - in hundreths of a second per pixel.
     */
    public void setScale(int scale) {
    	this.pixel_time_scale = scale;
    	setSize(controlPanel.timeline_length, getHeight());
       	setPreferredSize(new Dimension(controlPanel.timeline_length, getHeight()));
    }
    
    /**
     * Paint this component
     * @param g the graphics object to use to paint this component.
     */
    public void paintComponent(Graphics g) {
        g.setColor( getBackground() );
	    g.fillRect(0, 0, width, getHeight());	

        g.setColor( Color.DARK_GRAY );
        Font font = new Font("Dialog", Font.PLAIN, 12); //$NON-NLS-1$
        g.setFont(font);
        
        FontMetrics fontMetrics = g.getFontMetrics();
        
	    int count = timelineWidth;
	    int loop = 0;
		int     hours=0;
		int     minutes=0;
		int     seconds=0;
		int     hours10=0;
		int     minutes10=0;
		int     seconds10=0;
		//int 	milliseconds=0;
		int 	initialValue = 0;
		
		String strTime = ""; //$NON-NLS-1$
		
		//draw the timeline interval marks and time labels
	    for (int i=UITimeLinesController.TIMELINE_LEFT_OFFSET; i<count; i+=PIXELS_PER_UNIT) {
	    	if (loop%UNIT_LABEL_INTERNAL == 0) {
		    	g.drawLine(i, 0, i, 8);
		    	
		    	initialValue = (i-UITimeLinesController.TIMELINE_LEFT_OFFSET)*pixel_time_scale;		    	

		    	seconds = initialValue/1000;
		    	
		    	//should always be zero
		    	//milliseconds = initialValue%1000;
		    	
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

		        strTime = new String ( "" + hours10 + hours + ":" + minutes10 + minutes + ":" + seconds10 + seconds); //+ "." + milliseconds ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	    		g.drawString(strTime, i-(fontMetrics.stringWidth(strTime)/2), 6+(fontMetrics.getHeight()));
	    	} else {
		    	g.drawLine(i, 0, i, 4);
	    	}
	    	loop++;
	    }
    }				
}


