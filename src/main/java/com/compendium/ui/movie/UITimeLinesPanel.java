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
import java.awt.event.AdjustmentListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

 
/**
 * This class holds the class that draws all the sliders for each node in the passed view.
 * It also holds the scrollpane.
 * @author Michelle Bachler
 */
public class UITimeLinesPanel extends JPanel {
	
	private static final long serialVersionUID = 1L;
	
	public JScrollPane				scrollpane 	= null;	
	private UITimeLinesController controllerPane = null;

	public UITimeLinesPanel(UIMovieMapViewPane oView) {
		this.scrollpane = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		controllerPane = new UITimeLinesController(this, oView);
        setLayout ( new BorderLayout() );
        scrollpane.setViewportView(controllerPane);
        add(scrollpane, BorderLayout.CENTER);
        
        addComponentListener(new ComponentAdapter() {
    	    public void componentResized ( ComponentEvent event ) {
    	    	controllerPane.scaleWidth();
    	    }
        });
    }
	
	/**
	 * Add an adjustment listener to the vertical and horizontal scroll bars
	 * @param adj the adjustment listener to add.
	 */
	public void addScrollAdjustmentListener(AdjustmentListener adj) {
		if (scrollpane != null) {
			scrollpane.getVerticalScrollBar().addAdjustmentListener(adj);
			scrollpane.getHorizontalScrollBar().addAdjustmentListener(adj);
		}
	}
	
	/**
	 * Return the time lines controller object
	 * @return
	 */
	public UITimeLinesController getController() {
		return controllerPane;		
	}

	/**
	 * Jump to the time point when the node with the given id is first shown.
	 * @param sNodeID the id of the node to jump to.
	 */
	public void jumpToNode(String sNodeID) {
		controllerPane.jumpToNode(sNodeID);
	}
	
	/**
	 * Stop the controller timeline.
	 */
	public void start() {
		controllerPane.start();
	}
	
	/**
	 * Stop the controller timeline.
	 */
	public void stop() {
		controllerPane.stop();
	}
	
	public void scrollToY(int y) {
		final int fy = y;
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				scrollpane.getViewport().scrollRectToVisible(new Rectangle(0, fy, 30, 30));	
			}
		});
	}
	
	public void scrollToRectangle(int x, int y, int width, int height) {
		Rectangle visi = scrollpane.getVisibleRect();
		width = visi.width-x-5;
		height = visi.height-y-5;
       	Point parentPos = SwingUtilities.convertPoint((Component)controllerPane, x, y, scrollpane);
       	scrollpane.scrollRectToVisible(new Rectangle(parentPos.x, parentPos.y, width, height)); 	    	       			
	}
}
 