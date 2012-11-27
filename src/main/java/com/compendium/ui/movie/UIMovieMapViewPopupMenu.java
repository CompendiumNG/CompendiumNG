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

import java.awt.Point;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.Transferable;
import java.awt.event.*;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.swing.*;

import com.compendium.core.ICoreConstants;
import com.compendium.core.datamodel.*;
import com.compendium.core.datamodel.services.ILinkService;
import com.compendium.core.datamodel.services.INodeService;
import com.compendium.ui.UILink;
import com.compendium.ui.UIListViewFrame;
import com.compendium.ui.UIMapViewFrame;
import com.compendium.ui.UINode;
import com.compendium.ui.UIViewFrame;
import com.compendium.ui.edits.ClipboardTransferables;
import com.compendium.ui.edits.PasteEdit;
import com.compendium.ui.movie.UIMovieMapViewFrame;
import com.compendium.ui.movie.UIMoviePanel;
import com.compendium.ui.plaf.*;
import com.compendium.ui.popups.UIViewPopupMenu;
import com.compendium.LanguageProperties;
import com.compendium.ProjectCompendium;

/**
 * This class draws the right-click menu for movie map views.
 * It extends UIViewPopupMenu and adds additional movie related options to the standard right-click menu.
 *
 * @author	Michelle Bachler
 */
public class UIMovieMapViewPopupMenu extends UIViewPopupMenu implements ActionListener, ClipboardOwner {
		
	/** The JMenuItem to open the Movies tab of the map contents dialog.*/
	private JMenuItem		miMovies			= null;

	/** The JMenuItem to add a new set or properties to this movie.*/
	private JMenuItem		miMovieProperties	= null;

	/** the movie that the mouse was over when this menu was opened.*/
	private UIMoviePanel	oMovie				= null;

	/**
	 * Constructor. Create the menus and items and draws the popup menu.
	 * @param title the title for this popup menu.
	 * @param viewpaneUI  the associated map for this popup menu.
	 */
	public UIMovieMapViewPopupMenu(String title, ViewPaneUI viewpaneUI) {
		super(title);
		setViewPaneUI(viewpaneUI) ;
		init();
	}

	/**
	 * Constructor. Create the menus and items and draws the popup menu.
	 * @param title the title for this popup menu.
	 * @param viewpaneUI  the associated map for this popup menu.
	 * @param movie the movie the was in the background when this menu opened
	 */
	public UIMovieMapViewPopupMenu(String title, ViewPaneUI viewpaneUI, UIMoviePanel movie) {
		super(title);
		this.oMovie = movie;
		setViewPaneUI(viewpaneUI) ;
		init();
	}

	protected void init() {
		View view = oViewPane.getView();
		
		if (view instanceof MovieMapView) {
			miMovies = new JMenuItem(LanguageProperties.getString(LanguageProperties.MOVIE_BUNDLE, "UIMovieMapViewPopupMenu.movies")); //$NON-NLS-1$
			miMovies.setToolTipText(LanguageProperties.getString(LanguageProperties.MOVIE_BUNDLE, "UIMovieMapViewPopupMenu.moviesTip")); //$NON-NLS-1$
			miMovies.addActionListener(this);
			add(miMovies);
			
			if (oMovie != null) {
				miMovieProperties = new JMenuItem(LanguageProperties.getString(LanguageProperties.MOVIE_BUNDLE, "UIMovieMapViewPopupMenu.transitionPoint")); //$NON-NLS-1$
				miMovieProperties.setToolTipText(LanguageProperties.getString(LanguageProperties.MOVIE_BUNDLE, "UIMovieMapViewPopupMenu.transitionPointTip")); //$NON-NLS-1$
				miMovieProperties.addActionListener(this);
				add(miMovieProperties);
				addSeparator();
				addCutCopyPaste(shortcutKey);
				addDelete(shortcutKey);
			} else {
				addSeparator();
				super.init();	
			}
		}	
	}

	/**
	 * Handles the event of an option being selected.
	 * @param evt, the event associated with the option being selected.
	 */
	public void actionPerformed(ActionEvent evt) {
		ProjectCompendium.APP.setWaitCursor();

		Object source = evt.getSource();

		if (source.equals(miMovies)) {
			UIMovieMapViewFrame frame = (UIMovieMapViewFrame)oViewPaneUI.getViewPane().getViewFrame();
			if (oMovie != null) {
				frame.showMovieDialog(oMovie.getMovieData());
			} else {
				frame.showMovieDialog();
			}
			ProjectCompendium.APP.setDefaultCursor();
		}
		else if (source.equals(miMovieProperties)) {						
			UIMovieMapViewFrame frame = (UIMovieMapViewFrame)oViewPaneUI.getViewPane().getViewFrame();
			long time = 10;
			if (frame != null) {
				UITimeLinesController controller = frame.getController();
				time = controller.getCurrentTime();
			}									
	    	try {
	    		MovieProperties props = ((MovieMapView)oViewPane.getView()).addMovieProperties(oMovie.getMovieData().getId(), oMovie.getX(), oMovie.getY(), oMovie.getWidth(), oMovie.getHeight(), 1.0f, time);
				frame.showMovieDialog(props);			
			} catch(Exception e) {
				System.out.println(e.getLocalizedMessage());
	    	}			
			ProjectCompendium.APP.setDefaultCursor();
		} else {
			super.actionPerformed(evt);
		}
	}
	
	/**
	 * Required for the ClipboardOwner implementation.
   	 */
	public void lostOwnership(Clipboard clip, Transferable trans) {}
	
	/**
	 * Cut the selected node(s)
	 */
	protected void cut() {
		ClipboardTransferables clips = new ClipboardTransferables();
		clips.addTransferables(oMovie.getMovieData());
		if (clips.getTransferables().hasMoreElements()) {	  	
			ProjectCompendium.APP.getClipboard().setContents(clips, oViewPaneUI);
			ProjectCompendium.APP.setPasteEnabled(true);
		}
		if (oMovie != null) {
			oMovie.deleteMovie();
		}
	}
	
 	/**
	 * Copy the selected node(s)
	 */
	protected void copy() {
		ClipboardTransferables clips = new ClipboardTransferables();
		clips.addTransferables(oMovie.getMovieData());
		if (clips.getTransferables().hasMoreElements()) {	  	
			ProjectCompendium.APP.getClipboard().setContents(clips, oViewPaneUI);
			ProjectCompendium.APP.setPasteEnabled(true);
		}
	}

	/**
	 * Delete the currently selected nodes.
	 * Subclasses should implement this method.
	 */
	protected void delete() {
		if (oMovie != null) {
			oMovie.deleteMovie();
		}
	}
}
