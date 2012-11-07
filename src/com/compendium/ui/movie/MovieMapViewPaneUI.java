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
import java.awt.geom.*;
import java.awt.event.*;
import java.awt.datatransfer.*;

import java.util.*;

import javax.swing.*;
import javax.swing.plaf.*;

import com.compendium.core.CoreUtilities;
import com.compendium.core.ICoreConstants;
import com.compendium.core.datamodel.*;
import com.compendium.core.datamodel.services.*;

import com.compendium.io.xml.*;
import com.compendium.io.questmap.*;
import com.compendium.ui.*;
import com.compendium.ui.edits.*;
import com.compendium.ui.plaf.NodeUI;
import com.compendium.ui.plaf.ViewPaneUI;
import com.compendium.ProjectCompendium;

/**
 * The UI class for the UIViewPane Component
 *
 * @author	Mohammed Sajid Ali / Michelle Bachler
 */
public	class MovieMapViewPaneUI extends ViewPaneUI
				implements MouseListener, MouseMotionListener, KeyListener {


  	/** The MouseListener registered for this list.*/
	private		MouseListener						oMouseListener;

  	/** The MouseMotionListener registered for this list.*/
	private		MouseMotionListener					oMouseMotionListener;

  	/** The KeyListener registered for this list.*/
	private		KeyListener							oKeyListener;

	/**
	 * Constructor. Just calls super.
	 */
 	public MovieMapViewPaneUI() {
		super();
	}

	/**
	 * Constructor. Installs the default and listeners.
	 * @param c, the component this is the ui for.
	 */
	public MovieMapViewPaneUI(JComponent c) {
		super(c);		
	}

	/**
	 * Create a new MovieMapViewPaneUI instance.
	 * @param c, the component this is the ui for - NOT REALLY USED AT PRESENT HERE.
	 */
	public static ComponentUI createUI(JComponent c) {
		return new MovieMapViewPaneUI(c);
  	}

	/***** USER INTERFACE INITIALIZATION METHODS *****/

	/**
	 * Run any install instructions for installing this UI.
	 * @param c, the component this is the ui for.
	 */
  	public void installUI(JComponent c)   {
		super.installUI(c);
		installListeners(c);
		initializeView();
  	}

	/**
	 * Install any Listener classes required by this UI.
	 * @param c, the component to install the listeners for.
	 */
	protected void installListeners(JComponent c) {
		if ( (oMouseListener = createMouseListener( c )) != null ) {
		    c.addMouseListener( oMouseListener );
		}
		if ( (oMouseMotionListener = createMouseMotionListener( c )) != null ) {
		    c.addMouseMotionListener( oMouseMotionListener );
		}
		if ( (oKeyListener = createKeyListener( c )) != null ) {
				c.addKeyListener( oKeyListener );
		}
	}

	/**
	 * Just returns this class as the MouseListener.
	 * @param c, the component to create the MouseLisener for.
	 * @return MouseListener, the listener to use.
	 */
  	protected MouseListener createMouseListener( JComponent c ) {
		return this;
  	}

	/**
	 * Just returns this class as the MouseMotionListener.
	 * @param c, the component to create the MouseMotionLisener for.
	 * @return MouseMotionListener, the listener to use.
	 */
  	protected MouseMotionListener createMouseMotionListener( JComponent c ) {
		return this;
  	}

	/**
	 * Just returns this class as the KeyListener.
	 * @param c, the component to create the KeyLisener for.
	 * @return KeyListener, the listener to use.
	 */
  	protected KeyListener createKeyListener(JComponent c) {
		return this;
  	}

	/**
	 * Run any uninstall instructions for uninstalling this UI.
	 * @param c, the component this is the ui to uninstall for.
	 */
	public void uninstallUI(JComponent c) {
		uninstallListeners(c);

		oMouseListener = null;
		oKeyListener = null;
		oMouseMotionListener = null;
  	}

	/**
	 * Uninstall any Listener classes used by this UI.
	 * @param c, the component to uninstall the listeners for.
	 */
	protected void uninstallListeners( JComponent c ) {
		if ( oKeyListener!= null ) {
	    	c.removeKeyListener( oKeyListener );
		}
		if ( oMouseMotionListener!= null ) {
	    	c.removeMouseMotionListener( oMouseMotionListener );
		}
		if ( oMouseListener!= null ) {
	    	c.removeMouseListener( oMouseListener );
		}
	}


	/**
	 * This routine gets the clipboard contents, and paste it into this view.
	 */
	public void pasteFromClipboard() {
		ProjectCompendium.APP.setWaitCursor();
		ClipboardTransferables clipui = null;
		if((clipui = (ClipboardTransferables)(ProjectCompendium.APP.getClipboard().getContents(this))) != null) {
			for(Enumeration e = clipui.getTransferables();e.hasMoreElements();) {
				Object o = e.nextElement();
				if((o instanceof Movie)) {
					Movie movie = (Movie)o;
					try {
						Vector<MovieProperties> props = new Vector<MovieProperties>();
						((MovieMapView)oViewPane.getView()).addMovie(movie.getLink(), movie.getMovieName(), movie.getStartTime(), props);
					} catch (Exception ex) {
						System.out.println("error:"+ex.getLocalizedMessage()); //$NON-NLS-1$
						ex.printStackTrace();
					}			

				} else {
					super.pasteFromClipboard();
					break;
				}
			}
		}
		ProjectCompendium.APP.setDefaultCursor();
	}
	
// Mouse events
	/**
	 * Invoked when the mouse has been clicked on a component.
	 * @param e, the associated MouseEvent.
	 */
  	public void mouseClicked(MouseEvent e) {
		((UIMovieMapViewFrame)oViewPane.getViewFrame()).stopTimeLine();
   		super.mouseClicked(e);
  	}

	/**
	 * Invoked when a mouse button has been pressed on a component.
	 * @param e, the associated MouseEvent.
	 */
	public void mousePressed(MouseEvent e) {	
		((UIMovieMapViewFrame)oViewPane.getViewFrame()).stopTimeLine();
  		super.mousePressed(e);
	}

	/**
	 * Invoked when a mouse button has been released on a component.
	 * @param e, the associated MouseEvent.
	 */
	public void mouseReleased(MouseEvent e) {
  		super.mouseReleased(e);
  	} 

  	/**
 	 * Invoked when the mouse enters a component.
	 * @param e, the associated MouseEvent.
	 */
	public void mouseEntered(MouseEvent e) {
  		super.mouseEntered(e);
	}

	/**
	 * Invoked when the mouse exits a component.
	 * @param e, the associated MouseEvent.
   	 */
	public void mouseExited(MouseEvent e) {
  		super.mouseExited(e);
  	}

	/**
	 * Invoked when a mouse is dragged in a component.
	 * @param e, the associated MouseEvent.
	 */
	public void mouseDragged(MouseEvent e) {
  		super.mouseDragged(e);
	}


	/**
	 * Invoked when a mouse is moved in a component.
	 * @param e, the associated MouseEvent.
	 */
	public void mouseMoved(MouseEvent e) {
  		super.mouseMoved(e);
	}


// KEY EVENTS

	/**
	 * Invoked when a key is pressed in a component.
	 * @param evt, the associated KeyEvent.
	 */
	public void keyPressed(KeyEvent e) {
		((UIMovieMapViewFrame)oViewPane.getViewFrame()).stopTimeLine();		
  		super.keyPressed(e);
	}

	/**
	 * Invoked when a key is released in a component.
	 * @param evt, the associated KeyEvent.
	 */
	public void keyReleased(KeyEvent e) {
  		super.keyReleased(e);
 	}


	/**
	 * Invoked when a key is typed in a component.
	 * @param evt, the associated KeyEvent.
	 */
	public void keyTyped(KeyEvent e) {
  		super.keyTyped(e);
	}
}
