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

import java.awt.Container;
import java.awt.Color;
import java.awt.Point;
import java.awt.event.*;

import javax.swing.*;

import com.compendium.*;
import com.compendium.core.ICoreConstants;
import com.compendium.core.datamodel.Movie;
import com.compendium.core.datamodel.MovieProperties;
import com.compendium.core.datamodel.NodePositionTime;
import com.compendium.core.datamodel.ShortCutNodeSummary;
import com.compendium.core.datamodel.View;
import com.compendium.ui.*;

/**
 * This draws a small popup menu for right-clicks on a Movie timeline.
 *
 * @author	Michelle Bachler
 */
public class UIMovieTimeLinePopupMenu extends JPopupMenu implements ActionListener {

	/** The menu option to add a new timespan for the current node.*/
	private JMenuItem		miMenuItemAdd		= null;
 	
	/** The menu option to delete the given timespan from the node.*/
	private JMenuItem		miMenuItemCut		= null;

	/** The menu option to open the view (if it is a View.*/
	private JMenuItem		miMenuItemOpen		= null;

	/** The menu option to set the node location for this the current timespan 
	 * to the nodes current location.*/
	private JMenuItem		miMenuItemLocation		= null;

	/** The menu option to perform a copy operation.*/
	private JMenuItem		miMenuItemProperties		= null;

	/** The height of this popup.*/
	private int				nHeight				= 100;

	/** The width of this popup.*/
	private int				nWidth				= 300;

	/** The UINodeTimeLine associated with this popup.*/
	private UITimeLineForMovie 	oLine			= null;
	
	private MovieProperties oMovieProperties = null;
	private boolean			onMovie				= false;
	private String 			sMovieID 			= ""; //$NON-NLS-1$
	private Point			oLocation			= null;
		
	/**
	 * Constructor. Draws the popupmenu.
	 * @param line the UINodeTimeLinePopupMenu associated with this popup menu.
	 */
	public UIMovieTimeLinePopupMenu(UITimeLineForMovie line, MovieProperties timespan, String movieID, Point location, boolean onMovie) {
		super(LanguageProperties.getString(LanguageProperties.MOVIE_BUNDLE, "UIMovieTimeLinePopupMenu.movieTimelineMenu")); //$NON-NLS-1$
		
		this.oLine = line;
		UIMoviePanel moviepanel = oLine.getMovie();
		this.sMovieID = movieID;
		this.oLocation = location;
		this.oMovieProperties = timespan;
		this.onMovie = onMovie;
		
		miMenuItemProperties = new JMenuItem(LanguageProperties.getString(LanguageProperties.MOVIE_BUNDLE, "UIMovieTimeLinePopupMenu.viewProperties")); //$NON-NLS-1$
		miMenuItemProperties.setToolTipText(LanguageProperties.getString(LanguageProperties.MOVIE_BUNDLE, "UIMovieTimeLinePopupMenu.viewPropertiesTip")); //$NON-NLS-1$
		miMenuItemProperties.setMnemonic('V');
		miMenuItemProperties.addActionListener(this);
		add(miMenuItemProperties);

		if  (oMovieProperties == null) { // IF NOT ON TIMESPAN
			miMenuItemAdd = new JMenuItem(LanguageProperties.getString(LanguageProperties.MOVIE_BUNDLE, "UIMovieTimeLinePopupMenu.addTransitionPoint")); //$NON-NLS-1$
			miMenuItemAdd.setToolTipText(LanguageProperties.getString(LanguageProperties.MOVIE_BUNDLE, "UIMovieTimeLinePopupMenu.addTransitionPointTip")); //$NON-NLS-1$
			miMenuItemAdd.setMnemonic('A');
			miMenuItemAdd.addActionListener(this);
			add(miMenuItemAdd);
		} else { 
			miMenuItemCut = new JMenuItem(LanguageProperties.getString(LanguageProperties.MOVIE_BUNDLE, "UIMovieTimeLinePopupMenu.deleteTransitionPoint"), UIImages.get(IUIConstants.DELETE_ICON)); //$NON-NLS-1$
			miMenuItemCut.setToolTipText(LanguageProperties.getString(LanguageProperties.MOVIE_BUNDLE, "UIMovieTimeLinePopupMenu.deleteTransitionPointTip")); //$NON-NLS-1$
			miMenuItemCut.setMnemonic('D');
			miMenuItemCut.addActionListener(this);
			add(miMenuItemCut);
		}
		pack();
		setSize(nWidth,nHeight);
	}

	/**
	 * Handles the event of an option being selected.
	 * @param evt, the event associated with the option being selected.
	 */
	public void actionPerformed(ActionEvent evt) {

		Object source = evt.getSource();

		if(source.equals(miMenuItemCut)) {
			oLine.delete(this.oMovieProperties.getId());
		} else if (source.equals(miMenuItemAdd)) {
			oLine.createProps(oLocation.x);
		} else if(source.equals(miMenuItemProperties)) {
			oLine.showMovieDialog(this.oMovieProperties, onMovie);
		} else if (source.equals(miMenuItemLocation)) {
			//oLine.setCurrentSpanLocationToNode(oNodePositionTime);
		} 
		
		onCancel();
	}

	/**
	 * Handle the cancelleing of this popup. Set is to invisible.
	 */
	public void onCancel() {
		setVisible(false);
	}
}
