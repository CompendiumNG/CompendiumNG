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
import com.compendium.core.datamodel.NodePositionTime;
import com.compendium.core.datamodel.ShortCutNodeSummary;
import com.compendium.core.datamodel.View;
import com.compendium.ui.*;

/**
 * This draws a small popup menu for right-clicks on a Node time line.
 *
 * @author	Michelle Bachler
 */
public class UINodeTimeLinePopupMenu extends JPopupMenu implements ActionListener {

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
	private UITimeLineForNode 	oLine				= null;
	
	private NodePositionTime	oNodePositionTime = null;
	private String 			sNodePositionTimeID = ""; //$NON-NLS-1$
	private String 			sNodeID 			= ""; //$NON-NLS-1$
	private Point			oLocation			= null;

	/**
	 * Constructor. Draws the popupmenu.
	 * @param line the UINodeTimeLinePopupMenu associated with this popup menu.
	 */
	public UINodeTimeLinePopupMenu(UITimeLineForNode line, NodePositionTime timespan, String nodeid, Point location) {
		super(LanguageProperties.getString(LanguageProperties.MOVIE_BUNDLE, "UINodeTimeLinePopupMenu.nodeTimelineMenu")); //$NON-NLS-1$
		
		this.oLine = line;
		this.sNodeID = nodeid;
		this.oNodePositionTime = timespan;
		if (timespan != null) {
			this.sNodePositionTimeID = timespan.getId();
		}
		this.oLocation = location;

		int type = oLine.getNode().getType();
		if (View.isViewType(type)) {
			String label = LanguageProperties.getString(LanguageProperties.MOVIE_BUNDLE, "UINodeTimeLinePopupMenu.openMap"); //$NON-NLS-1$
			String tooltip = LanguageProperties.getString(LanguageProperties.MOVIE_BUNDLE, "UINodeTimeLinePopupMenu.openMapTip"); //$NON-NLS-1$
			if (View.isListType(type)) {
				label = LanguageProperties.getString(LanguageProperties.MOVIE_BUNDLE, "UINodeTimeLinePopupMenu.openList"); //$NON-NLS-1$
				tooltip = LanguageProperties.getString(LanguageProperties.MOVIE_BUNDLE, "UINodeTimeLinePopupMenu.openListTip"); //$NON-NLS-1$
			}
			miMenuItemOpen = new JMenuItem(label);
			miMenuItemOpen.setToolTipText(tooltip);
			miMenuItemOpen.setMnemonic('O');
			miMenuItemOpen.addActionListener(this);
			add(miMenuItemOpen);
		}
		
		miMenuItemProperties = new JMenuItem(LanguageProperties.getString(LanguageProperties.MOVIE_BUNDLE, "UINodeTimeLinePopupMenu.viewTimes")); //$NON-NLS-1$
		miMenuItemProperties.setToolTipText(LanguageProperties.getString(LanguageProperties.MOVIE_BUNDLE, "UINodeTimeLinePopupMenu.viewTimesTip")); //$NON-NLS-1$
		miMenuItemProperties.setMnemonic('V');
		miMenuItemProperties.addActionListener(this);
		add(miMenuItemProperties);

		if  (oNodePositionTime == null) { // IF NOT ON TIMESPAN
			miMenuItemAdd = new JMenuItem(LanguageProperties.getString(LanguageProperties.MOVIE_BUNDLE, "UINodeTimeLinePopupMenu.addSpan")); //$NON-NLS-1$
			miMenuItemAdd.setToolTipText(LanguageProperties.getString(LanguageProperties.MOVIE_BUNDLE, "UINodeTimeLinePopupMenu.addSpanTip")); //$NON-NLS-1$
			miMenuItemAdd.setMnemonic('A');
			miMenuItemAdd.addActionListener(this);
			add(miMenuItemAdd);
		} else { // IF ON TIMESPAN
			//Not needed - reset dynamically as you move the node.
			//miMenuItemLocation = new JMenuItem("Set Location");
			//miMenuItemLocation.setToolTipText("Set the node location for this timespan to the node's current location");
			//miMenuItemLocation.setMnemonic('L');
			//miMenuItemLocation.addActionListener(this);
			//add(miMenuItemLocation);
								
			miMenuItemCut = new JMenuItem(LanguageProperties.getString(LanguageProperties.MOVIE_BUNDLE, "UINodeTimeLinePopupMenu.deleteSpan"), UIImages.get(IUIConstants.DELETE_ICON)); //$NON-NLS-1$
			miMenuItemCut.setToolTipText(LanguageProperties.getString(LanguageProperties.MOVIE_BUNDLE, "UINodeTimeLinePopupMenu.deleteSpanTip")); //$NON-NLS-1$
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
			oLine.delete(sNodePositionTimeID, sNodeID);
		} else if (source.equals(miMenuItemAdd)) {
			oLine.createNewSpan(oLocation.x);
		} else if(source.equals(miMenuItemProperties)) {
			oLine.showTimeDialog(oNodePositionTime);
		} /*else if (source.equals(miMenuItemLocation)) {
			oLine.setCurrentSpanLocationToNode(oNodePositionTime);
		}*/ else if (source.equals(miMenuItemOpen)){
			
			// stop movies
			UINode oNode = oLine.getNode();
 			UIMovieMapViewPane pane = (UIMovieMapViewPane)oNode.getViewPane();
  			((UIMovieMapViewFrame)pane.getViewFrame()).stopTimeLine();
			
  			// Open View
			ProjectCompendium.APP.getAudioPlayer().playAudio(UIAudio.ABOUT_ACTION);
			int type = oNode.getType();
			View view = null;
			if( oNode.getNode() instanceof ShortCutNodeSummary ) {
				view = (View)(((ShortCutNodeSummary)oNode.getNode()).getReferredNode());
			}
			else {
				view = (View)oNode.getNode();
			}

			UIViewFrame frame = ProjectCompendium.APP.addViewToDesktop(view, oNode.getText());
			frame.setNavigationHistory(oNode.getViewPane().getViewFrame().getChildNavigationHistory());			
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
