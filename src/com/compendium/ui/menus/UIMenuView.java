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

package com.compendium.ui.menus;

import java.awt.BorderLayout;
import java.awt.event.*;
import java.sql.SQLException;

import javax.help.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import com.compendium.core.*;

import com.compendium.*;
import com.compendium.ui.*;
import com.compendium.ui.toolbars.*;
import com.compendium.ui.tags.*;

/**
 * This class creates and manages the View menu.
 *
 * @author	Michelle Bachler
 */
public class UIMenuView extends UIMenu implements ActionListener, IUIConstants, ICoreConstants {

	/** The label for the Outlint View Tab*/
	private String 				OUTLINE_VIEW			= "Outline View - ";
	
	/** The label for the Unread Views tab */
	private String 				UNREAD_VIEW				= "Unread View";
	
	/** The label for the Tags tab*/
	private String 				TAGS_VIEW				= "Tags View";

	/** The toolbars menu*/
	private JMenu				mnuToolbars				= null;

	/** The menuitem to switch on and off the Status bar.*/
	private JMenuItem			miStatusBar				= null;

	/** The menuitem to switch on and off the ViewHistory bar.*/
	private JMenuItem			miViewHistoryBar		= null;

	/** The menu item to hide/show the main toolbar.*/
	private JMenuItem			miToolbarMain			= null;

	/** The menu item to hide/show the tags toolbar.*/
	private JMenuItem			miToolbarTags			= null;

	/** The menu item to hide/show the node creation toolbar.*/
	private JMenuItem			miToolbarNode			= null;

	/** The menu item to hide/show the zoom toolbar.*/
	private JMenuItem			miToolbarZoom			= null;

	/** The menu item to hide/show the scribble toolbar.*/
	private JMenuItem			miToolbarDraw			= null;

	/** The menu item to hide/show the data toolbar.*/
	private JMenuItem			miToolbarData			= null;

	/** The menu item to hide/show the meeting toolbar.*/
	private JMenuItem			miToolbarMeeting		= null;

	/** The menu item to reset the toolbars to thier default position.*/
	private JMenuItem			miResetToolbars			= null;
	
	/** The menu item to hide/show the format toolbar.*/
	private JMenuItem			miToolbarFormat		= null;

	/** The zoom menu*/
	private JMenu				mnuZoom					= null;

	/** The menu item to zoom to view to 25%.*/
	private JMenuItem			miZoom25				= null;

	/** The menu item to zoom the view to 50%.*/
	private JMenuItem			miZoom50				= null;

	/** The menu item to zoom the view to 75%.*/
	private JMenuItem			miZoom75				= null;

	/** The menu item to zoom the view to fit the visible area.*/
	private JMenuItem			miZoomFit				= null;

	/** The menu item to zoom the view to 100%.*/
	private JMenuItem			miZoomNormal			= null;

	/** The menu item to zoom the view to 100% and center on the selected node.*/
	private JMenuItem			miZoomFocus				= null;

	/** The menu item to open the Views dialog.*/
	private JMenuItem			miViewMap				= null;

	/** The menu item to open a list of nodes not in a view - NOT CURRENTLY USED.*/
	private JMenuItem			miLimboNode				= null;

	/** The menu item to open an aerial view window for this view.*/
	private JMenuItem			miAerialView			= null;

	/** The menu item to activate/deactivate image rollover.*/
	private JMenuItem			miImageRollover			= null;

		/** The map menu.*/
	private JMenu				mnuNodeIndicators			= null;

	/** The menu item to activate/deactivate node label searching.*/
	private JMenuItem			miSearchLabel			= null;

	/** The JMenu to perform a arrange operation.*/
	private JMenu			mnuViewArrange				= null;

	/** The JMenuItem to perform a arrange operation.*/
	private JMenuItem	miMenuItemLeftRightArrange		= null;

	/** The JMenuItem to perform a arrange operation.*/
	private JMenuItem    miMenuItemTopDownArrange		= null;

	/** The JMenu to perform a arrange operation.*/
	private JMenu			mnuViewAlign				= null;

	/** The JMenuItem to perform a align top operation.*/
	private JMenuItem	miMenuItemAlignTop				= null;

	/** The JMenuItem to perform a bottom align operation.*/
	private JMenuItem    miMenuItemAlignBottom			= null;

	/** The JMenuItem to perform a middle align operation.*/
	private JMenuItem	miMenuItemAlignMiddle			= null;

	/** The JMenuItem to perform a center align operation.*/
	private JMenuItem    miMenuItemAlignCenter			= null;

	/** The JMenuItem to perform a right align operation.*/
	private JMenuItem	miMenuItemAlignRight			= null;

	/** The JMenuItem to perform a left align operation.*/
	private JMenuItem    miMenuItemAlignLeft			= null;

	/** The JMenu to open outline view of views.*/
	private JMenu    		mnuViewOutline				= null;
	
	/** The JMenuItem to perform a right align operation.*/
	private JMenuItem		miViewsOnly					= null;

	/** The JMenuItem to perform a left align operation.*/
	private JMenuItem    	miViewsAndNodes				= null;
	
	/** The JMenuItem to perform a left align operation.*/
	private JMenuItem    		miNone					= null;
	
	/** The JMenuItem to open unread view.  */
	private JMenuItem    		miViewUnread			= null;

	/** The JMenuItem to open tags view.  */
	private JMenuItem    		miViewTags				= null;
	
	/** Idicates is a toolbar checkbox was checked/unchecked externally. Prevents loop.*/
	private boolean bExternalActivation					= false;

	/** The UIViewOutline to display for outline view      */
	private UIViewOutline 			outlineView 		= null;

	/** To display unread View */
	private UIViewUnread 			unreadView			= null;
	
	/** The tags tree view.*/
	private static UITagTreePanel 	tagsTree			= null;

	//private boolean				externalActivation		= false;
		
	/**
	 * Constructor.
	 * @param bSimple true if the simple interface should be draw, false if the advanced. 	 * 
	 */
	public UIMenuView(boolean bSimple) {
		
		this.OUTLINE_VIEW = LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuView.outlineViewTabLabel");
		this.UNREAD_VIEW = LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuView.unreadViewTabLabel");
		this.TAGS_VIEW = LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuView.tagsViewTabLabel");
		
		this.bSimpleInterface = bSimple;		
		
		mnuMainMenu	= new JMenu(LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuView.view"));   //$NON-NLS-1$
		mnuMainMenu.setMnemonic((LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuView.viewMnemonic")).charAt(0)); //$NON-NLS-1$
		CSH.setHelpIDString(mnuMainMenu,"menus.map");  //$NON-NLS-1$
				
		createMenuItems(bSimple);
	}

	/**
	 * Create and return the View menu.
	 * @return JMenu the Map menu.
	 */
	private JMenu createMenuItems(boolean bSimple) {

		// Lakshmi 4/3/05 - Add the outline view options to View menu.
		mnuViewOutline = new JMenu(LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuView.outlineView"));  //$NON-NLS-1$
		mnuViewOutline.setMnemonic((LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuView.outlineViewMnemonic")).charAt(0)); //$NON-NLS-1$
		mnuViewOutline.addActionListener(this);
		
		miViewsOnly = new JRadioButtonMenuItem(LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuView.outlineViewViewsOnly"));
		miViewsOnly.setMnemonic((LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuView.outlineViewViewsOnlyMnemonic")).charAt(0)); //$NON-NLS-1$
		miViewsOnly.addActionListener(this);
		mnuViewOutline.add(miViewsOnly);
		
		miViewsAndNodes = new JRadioButtonMenuItem(LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuView.outlineViewViewsAndNodes"));
		miViewsAndNodes.setMnemonic((LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuView.outlineViewViewsAndNodesMnemonic")).charAt(0)); //$NON-NLS-1$
		miViewsAndNodes.addActionListener(this);
		mnuViewOutline.add(miViewsAndNodes);
		
		miNone = new JRadioButtonMenuItem(LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuView.outlineViewNone"));
		miNone.setMnemonic((LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuView.outlineViewNoneMnemonic")).charAt(0)); //$NON-NLS-1$
		miNone.addActionListener(this);
		mnuViewOutline.add(miNone);
		
		if (FormatProperties.displayOutlineView.equals(DISPLAY_VIEWS_ONLY)){
			miViewsOnly.setSelected(true);
			miViewsAndNodes.setSelected(false);
			miNone.setSelected(false);
			
			miViewsOnly.setEnabled(false);
			miViewsAndNodes.setEnabled(true);
			miNone.setEnabled(true);
		
		} else if(FormatProperties.displayOutlineView.equals(DISPLAY_VIEWS_AND_NODES)) {
			miViewsOnly.setSelected(false);
			miViewsAndNodes.setSelected(true);
			miNone.setSelected(false);	
			
			miViewsOnly.setEnabled(true);
			miViewsAndNodes.setEnabled(false);
			miNone.setEnabled(true);
		} else { 
			miViewsOnly.setSelected(false);
			miViewsAndNodes.setSelected(false);
			miNone.setSelected(true);
			
			miViewsOnly.setEnabled(true);
			miViewsAndNodes.setEnabled(true);
			miNone.setEnabled(false);
		}
		mnuMainMenu.add(mnuViewOutline);

		mnuMainMenu.addSeparator();
		
		miViewUnread = new JCheckBoxMenuItem(LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuView.unreadView"));  //$NON-NLS-1$
		miViewUnread.setMnemonic((LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuView.unreadViewMnemonic")).charAt(0)); //$NON-NLS-1$
		miViewUnread.addActionListener(this);
		
		if(FormatProperties.displayUnreadView){
			miViewUnread.setSelected(true);
		} else {
			miViewUnread.setSelected(false);
		}
		
		mnuMainMenu.add(miViewUnread);
			
		separator1 = new JPopupMenu.Separator();
		mnuMainMenu.add(separator1);
		
		miViewTags = new JCheckBoxMenuItem(LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuView.tagView"));  //$NON-NLS-1$
		miViewTags.setMnemonic((LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuView.tagViewMnemonic")).charAt(0)); //$NON-NLS-1$
		miViewTags.addActionListener(this);		
		
		mnuMainMenu.add(miViewTags);
		mnuMainMenu.addSeparator();

		miViewMap = new JMenuItem(LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuView.findViews"));   //$NON-NLS-1$
		miViewMap.setMnemonic((LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuView.findViewsMnemonic")).charAt(0)); //$NON-NLS-1$
		miViewMap.addActionListener(this);
		mnuMainMenu.add(miViewMap);

		mnuMainMenu.addSeparator();

		// TOOLBAR MENU
		mnuToolbars	= new JMenu(LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuView.toolbars"));   //$NON-NLS-1$
		mnuToolbars.setMnemonic((LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuView.toolbarsMnemonic")).charAt(0)); //$NON-NLS-1$
		CSH.setHelpIDString(mnuToolbars,"menus.map");  //$NON-NLS-1$
		
		miToolbarMain = new JCheckBoxMenuItem(LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuView.toolbarMain"));   //$NON-NLS-1$
		miToolbarMain.setMnemonic((LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuView.toolbarMainMnemonic")).charAt(0)); //$NON-NLS-1$
		miToolbarMain.addActionListener(this);
		mnuToolbars.add(miToolbarMain);

		miToolbarData = new JCheckBoxMenuItem(LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuView.toolbarDataSource"));   //$NON-NLS-1$
		miToolbarData.setMnemonic((LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuView.toolbarDataSourceMnemonic")).charAt(0)); //$NON-NLS-1$
		miToolbarData.addActionListener(this);
		mnuToolbars.add(miToolbarData);

		miToolbarNode = new JCheckBoxMenuItem(LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuView.toolbarNodeCreation"));   //$NON-NLS-1$
		miToolbarNode.setMnemonic((LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuView.toolbarNodeCreationMnemonic")).charAt(0)); //$NON-NLS-1$
		miToolbarNode.addActionListener(this);
		mnuToolbars.add(miToolbarNode);

		miToolbarFormat = new JCheckBoxMenuItem(LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuView.toolbarNodeFormat"));  //$NON-NLS-1$
		miToolbarFormat.setMnemonic((LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuView.toolbarNodeFormatMnemonic")).charAt(0)); //$NON-NLS-1$
		miToolbarFormat.addActionListener(this);
		mnuToolbars.add(miToolbarFormat);
				
		miToolbarDraw = new JCheckBoxMenuItem(LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuView.toolbarScribble"));   //$NON-NLS-1$
		miToolbarDraw.setMnemonic((LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuView.toolbarScribbleMnemonic")).charAt(0)); //$NON-NLS-1$
		miToolbarDraw.addActionListener(this);
		mnuToolbars.add(miToolbarDraw);
				
		miToolbarTags = new JCheckBoxMenuItem(LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuView.toolbarTags"));   //$NON-NLS-1$
		miToolbarTags.setMnemonic((LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuView.toolbarTagsMnemonic")).charAt(0)); //$NON-NLS-1$
		miToolbarTags.addActionListener(this);
		mnuToolbars.add(miToolbarTags);

		miToolbarZoom = new JCheckBoxMenuItem(LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuView.toolbarZoom"));   //$NON-NLS-1$
		miToolbarZoom.setMnemonic((LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuView.toolbarZoomMnemonic")).charAt(0)); //$NON-NLS-1$
		miToolbarZoom.addActionListener(this);
		mnuToolbars.add(miToolbarZoom);
		
		//miToolbarMeeting = new JCheckBoxMenuItem("Meeting Toolbar");
		//miToolbarMeeting.setMnemonic(KeyEvent.VK_E);
		//miToolbarMeeting.addActionListener(this);
		//mnuToolbars.add(miToolbarMeeting);

		mnuToolbars.addSeparator();
		
		miResetToolbars = new JMenuItem(LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuView.toolbarReset"));   //$NON-NLS-1$
		miResetToolbars.setMnemonic((LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuView.toolbarResetMnemonic")).charAt(0)); //$NON-NLS-1$
 		miResetToolbars.addActionListener(this);
 		mnuToolbars.add(miResetToolbars);
		
		mnuMainMenu.add(mnuToolbars);

		miStatusBar = new JCheckBoxMenuItem(LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuView.statusBar"));   //$NON-NLS-1$
		miStatusBar.setMnemonic((LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuView.statusBarMnemonic")).charAt(0)); //$NON-NLS-1$
			miStatusBar.addActionListener(this);
		mnuMainMenu.add(miStatusBar);
		if (FormatProperties.displayStatusBar)
       		miStatusBar.setSelected(true);
		else
			miStatusBar.setSelected(false);

		miViewHistoryBar = new JCheckBoxMenuItem(LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuView.viewHistoryBar"));   //$NON-NLS-1$
		miViewHistoryBar.setMnemonic((LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuView.viewHistoryBarMnemonic")).charAt(0)); //$NON-NLS-1$
		miViewHistoryBar.addActionListener(this);
		mnuMainMenu.add(miViewHistoryBar);
		if (FormatProperties.displayViewHistoryBar)
      		miViewHistoryBar.setSelected(true);
		else
			miViewHistoryBar.setSelected(false);

		mnuMainMenu.addSeparator();

		// TICK BOX FOR ACTIVATING AERIAL VIEW
		miAerialView = new JCheckBoxMenuItem(LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuView.aerialView"));   //$NON-NLS-1$
		miAerialView.setMnemonic((LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuView.aerialViewMnemonic")).charAt(0)); //$NON-NLS-1$

		if (FormatProperties.aerialView)
        	miAerialView.setSelected(true);
		else
			miAerialView.setSelected(false);

		miAerialView.addActionListener(this);
		mnuMainMenu.add(miAerialView);

		mnuMainMenu.addSeparator();

		// ZOOM MENU
		mnuZoom	= new JMenu(LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuView.zoom"));   //$NON-NLS-1$
		mnuZoom.setMnemonic((LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuView.zoomMnemonic")).charAt(0)); //$NON-NLS-1$
		CSH.setHelpIDString(mnuZoom,"menus.map");  //$NON-NLS-1$

		miZoomNormal = new JMenuItem(LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuView.zoom100"));   //$NON-NLS-1$
		miZoomNormal.setMnemonic((LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuView.zoom100Mnemonic")).charAt(0)); //$NON-NLS-1$
		miZoomNormal.addActionListener(this);
		mnuZoom.add(miZoomNormal);

		miZoom75 = new JMenuItem(LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuView.zoom75"));   //$NON-NLS-1$
		miZoom75.setMnemonic((LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuView.zoom75Mnemonic")).charAt(0)); //$NON-NLS-1$
		miZoom75.addActionListener(this);
		mnuZoom.add(miZoom75);

		miZoom50 = new JMenuItem(LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuView.zoom50"));   //$NON-NLS-1$
		miZoom50.setMnemonic((LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuView.zoom50Mnemonic")).charAt(0)); //$NON-NLS-1$
		miZoom50.addActionListener(this);
		mnuZoom.add(miZoom50);

		miZoom25 = new JMenuItem(LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuView.zoom25"));   //$NON-NLS-1$
		miZoom25.setMnemonic((LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuView.zoom25Mnemonic")).charAt(0)); //$NON-NLS-1$
		miZoom25.addActionListener(this);
		mnuZoom.add(miZoom25);

		miZoomFit = new JMenuItem(LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuView.zoomFitPage"));   //$NON-NLS-1$
		miZoomFit.setMnemonic((LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuView.zoomFitPageMnemonic")).charAt(0)); //$NON-NLS-1$
		miZoomFit.addActionListener(this);
		mnuZoom.add(miZoomFit);

		miZoomFocus = new JMenuItem(LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuView.zoomFocusNode"));   //$NON-NLS-1$
		miZoomFocus.setMnemonic((LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuView.zoomFocusNodeMnemonic")).charAt(0)); //$NON-NLS-1$
		miZoomFocus.addActionListener(this);
		mnuZoom.add(miZoomFocus);

		mnuMainMenu.add(mnuZoom);

		// TICK BOX FOR ACTIVATING IMAGE ROLLOVER
		miImageRollover = new JCheckBoxMenuItem(LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuView.imageRollover"));   //$NON-NLS-1$
		miImageRollover.setMnemonic((LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuView.imageRolloverMnemonic")).charAt(0)); //$NON-NLS-1$

		if (FormatProperties.imageRollover)
        	miImageRollover.setSelected(true);
		else
			miImageRollover.setSelected(false);

		miImageRollover.addActionListener(this);
		mnuMainMenu.add(miImageRollover);

		mnuMainMenu.addSeparator();

		miSearchLabel = new JCheckBoxMenuItem(LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuView.autoSearchLabel"));   //$NON-NLS-1$
		miSearchLabel.setMnemonic((LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuView.autoSearchLabelMnemonic")).charAt(0)); //$NON-NLS-1$
		
		if (FormatProperties.autoSearchLabel)
   			miSearchLabel.setSelected(true);
		else
			miSearchLabel.setSelected(false);

		miSearchLabel.addActionListener(this);
		mnuMainMenu.add(miSearchLabel);
		
		separator2 = new JPopupMenu.Separator();
		mnuMainMenu.add(separator2);

		//Begin edit, Lakshmi (11/3/05)
		//include Top - Down and Left - Right Option in Arrange Menu.
		mnuViewArrange = new JMenu(LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuView.arrange"));   //$NON-NLS-1$
		mnuViewArrange.setMnemonic((LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuView.arrangeMnemonic")).charAt(0)); //$NON-NLS-1$
		mnuViewArrange.addActionListener(this);

		miMenuItemLeftRightArrange = new JMenuItem(LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuView.arrangeLeftRight"));   //$NON-NLS-1$
		miMenuItemLeftRightArrange.setMnemonic((LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuView.arrangeLeftRightMnemonic")).charAt(0)); //$NON-NLS-1$
		miMenuItemLeftRightArrange.addActionListener(this);
		mnuViewArrange.add(miMenuItemLeftRightArrange);

		mnuViewArrange.addSeparator();

		miMenuItemTopDownArrange = new JMenuItem(LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuView.arrangeTopDown"));   //$NON-NLS-1$
		miMenuItemTopDownArrange.setMnemonic((LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuView.arrangeTopDownMnemonic")).charAt(0)); //$NON-NLS-1$
		miMenuItemTopDownArrange.addActionListener(this);
		mnuViewArrange.add(miMenuItemTopDownArrange);

		mnuMainMenu.add(mnuViewArrange);

		mnuViewAlign = new JMenu(LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuView.align"));   //$NON-NLS-1$
		mnuViewAlign.setMnemonic((LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuView.alignMnemonic")).charAt(0)); //$NON-NLS-1$
		mnuViewAlign.setEnabled(false);

		miMenuItemAlignLeft = new JMenuItem(LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuView.alignLeft"));   //$NON-NLS-1$
		miMenuItemAlignLeft.setMnemonic((LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuView.alignLeftMnemonic")).charAt(0)); //$NON-NLS-1$
		miMenuItemAlignLeft.addActionListener(this);
		mnuViewAlign.add(miMenuItemAlignLeft);

		miMenuItemAlignCenter = new JMenuItem(LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuView.alignCenter"));   //$NON-NLS-1$
		miMenuItemAlignCenter.setMnemonic((LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuView.alignCenterMnemonic")).charAt(0)); //$NON-NLS-1$
		miMenuItemAlignCenter.addActionListener(this);
		mnuViewAlign.add(miMenuItemAlignCenter);

		miMenuItemAlignRight = new JMenuItem(LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuView.alignRight"));   //$NON-NLS-1$
		miMenuItemAlignRight.setMnemonic((LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuView.alignRightMnemonic")).charAt(0)); //$NON-NLS-1$
		miMenuItemAlignRight.addActionListener(this);
		mnuViewAlign.add(miMenuItemAlignRight);

		separator3 = new JPopupMenu.Separator();
		mnuViewAlign.add(separator3);

		miMenuItemAlignTop = new JMenuItem(LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuView.alignTop"));   //$NON-NLS-1$
		miMenuItemAlignTop.setMnemonic((LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuView.alignTopMnemonic")).charAt(0)); //$NON-NLS-1$
		miMenuItemAlignTop.addActionListener(this);
		mnuViewAlign.add(miMenuItemAlignTop);

		miMenuItemAlignMiddle = new JMenuItem(LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuView.alignMiddle"));   //$NON-NLS-1$
		miMenuItemAlignMiddle.setMnemonic((LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuView.alignMiddleMnemonic")).charAt(0)); //$NON-NLS-1$
		miMenuItemAlignMiddle.addActionListener(this);
		mnuViewAlign.add(miMenuItemAlignMiddle);

		miMenuItemAlignBottom = new JMenuItem(LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuView.alignBottom"));   //$NON-NLS-1$
		miMenuItemAlignBottom.setMnemonic((LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuView.alignBottomMnemonic")).charAt(0)); //$NON-NLS-1$
		miMenuItemAlignBottom.addActionListener(this);
		mnuViewAlign.add(miMenuItemAlignBottom);

		mnuMainMenu.add(mnuViewAlign);
		
		if (bSimple) {
			addExtenderButton();
			setDisplay(bSimple);
		}
		
		return mnuMainMenu;
	}

	/**
	 * Hide/show items depending on whether the user wants the simple view or simple.
	 * @param bSimple
	 */
	protected void setDisplay(boolean bSimple) {
		if (bSimple) {
			miViewUnread.setVisible(false);
			separator1.setVisible(false);

			miStatusBar.setVisible(false);
			miViewHistoryBar.setVisible(false);

			miSearchLabel.setVisible(false);
			separator2.setVisible(false);

			mnuViewAlign.setVisible(false);
			miMenuItemAlignLeft.setVisible(false);
			miMenuItemAlignCenter.setVisible(false);
			miMenuItemAlignRight.setVisible(false);
			separator3.setVisible(false);
			miMenuItemAlignTop.setVisible(false);
			miMenuItemAlignMiddle.setVisible(false);
			miMenuItemAlignBottom.setVisible(false);				
		} else {
			miViewUnread.setVisible(true);
			separator1.setVisible(true);

			miStatusBar.setVisible(true);
			miViewHistoryBar.setVisible(true);

			miSearchLabel.setVisible(true);
			separator2.setVisible(true);

			mnuViewAlign.setVisible(true);
			miMenuItemAlignLeft.setVisible(true);
			miMenuItemAlignCenter.setVisible(true);
			miMenuItemAlignRight.setVisible(true);
			separator3.setVisible(true);
			miMenuItemAlignTop.setVisible(true);
			miMenuItemAlignMiddle.setVisible(true);
			miMenuItemAlignBottom.setVisible(true);				
		}
		
		setControlItemStatus(bSimple);
		
		JPopupMenu pop = mnuMainMenu.getPopupMenu();
		if (pop.isVisible()) {
			pop.setVisible(false);
			pop.setVisible(true);
			pop.requestFocus();
		}
	}
	
	/**
	 * Handles most menu action event for this application.
	 *
	 * @param evt the generated action event to be handled.
	 */
	public void actionPerformed(ActionEvent evt) {

		ProjectCompendium.APP.setWaitCursor();

		Object source = evt.getSource();

		if (source.equals(miImageRollover))
			onImageRollover();
		else if (source.equals(miSearchLabel))
			onSearchLabel();
		else if (source.equals(miViewMap)) {
			long lViewCount = 0;
			try {
				lViewCount = ProjectCompendium.APP.getModel().getNodeService().lGetViewCount(ProjectCompendium.APP.getModel().getSession());
			} catch (SQLException e) {
				e.printStackTrace();
			}
	        if (lViewCount > 250) {		// Note that 250 is a completely arbitrary threshold to set
	        	if (JOptionPane.showConfirmDialog(null,
	                LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuView.message1a")+ " \n" + //$NON-NLS-1$
	                LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuView.message1b")+Long.toString(lViewCount) +
	                LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuView.message1c")+ "\n"  + //$NON-NLS-1$ //$NON-NLS-2$
	                LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuView.message1d") + "\n"+ //$NON-NLS-1$
	                LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuView.message1e") + "\n\n" + //$NON-NLS-1$
	                LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuView.message1f"), //$NON-NLS-1$
	                LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuView.message1title"), //$NON-NLS-1$
	                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
	        			ProjectCompendium.APP.onViewMap();

	        	}
	        } else {
	        	ProjectCompendium.APP.onViewMap();
	        }
		}
		else if (source.equals(miLimboNode))
			ProjectCompendium.APP.onLimboNode();
//		 begin edit, Lakshmi (30/1/06)
		else if (source.equals (miViewsAndNodes)) {
			addOutlineView(DISPLAY_VIEWS_AND_NODES, true);			
		}
		else if (source.equals (miViewsOnly)) {	
			addOutlineView(DISPLAY_VIEWS_ONLY, true);
		}
		else if (source.equals (miNone)){
			removeOutlineView(true);	
		}
		else if(source.equals(miViewUnread)){
			if(miViewUnread.isSelected()){
				try {
					addUnreadView(true);
				} catch (SQLException e) {
					e.printStackTrace();
				}
			} else {
				removeUnreadView(true);
			}
		}
// begin edit, Lakshmi (11/3/05)

		else if (source.equals(miViewTags)) {
			if(miViewTags.isSelected()){
				addTagsView(true);
			} else {
				removeTagsView(true);
			}			
		}
		else if (source.equals(miResetToolbars))
			ProjectCompendium.APP.onResetToolBars();		
		else if(source.equals(miMenuItemTopDownArrange))
			ProjectCompendium.APP.onViewArrange(IUIArrange.TOPDOWN);
		else if(source.equals(miMenuItemLeftRightArrange))
			ProjectCompendium.APP.onViewArrange(IUIArrange.LEFTRIGHT);

		else if(source.equals(miMenuItemAlignTop))
			ProjectCompendium.APP.onViewAlign(UIAlign.TOP);
		else if(source.equals(miMenuItemAlignCenter))
			ProjectCompendium.APP.onViewAlign(UIAlign.CENTER);
		else if(source.equals(miMenuItemAlignBottom))
			ProjectCompendium.APP.onViewAlign(UIAlign.BOTTOM);
		else if(source.equals(miMenuItemAlignRight))
			ProjectCompendium.APP.onViewAlign(UIAlign.RIGHT);
		else if(source.equals(miMenuItemAlignMiddle))
			ProjectCompendium.APP.onViewAlign(UIAlign.MIDDLE);
		else if(source.equals(miMenuItemAlignLeft))
			ProjectCompendium.APP.onViewAlign(UIAlign.LEFT);
//end edit
		/*else if (source.equals(miFormatFont)) {
			Thread thread = new Thread("Format Font") { 
				public void run() {
					oParent.onFormatFont();
				}
			};
			thread.start();
		}*/
		else if (source.equals(miZoomFocus))
			onZoomRefocused();
		else if (source.equals(miZoomFit))
			onZoomToFit();
		else if (source.equals(miZoom25))
			onZoomTo(0.25);
		else if (source.equals(miZoom50))
			onZoomTo(0.50);
		else if (source.equals(miZoom75))
			onZoomTo(0.75);
		else if (source.equals(miZoomNormal))
			onZoomTo(1.0);

		else if (source.equals(miAerialView))
			ProjectCompendium.APP.onAerialView( ((JCheckBoxMenuItem)miAerialView).isSelected());

		else if (source.equals(miToolbarMain)) {
			Thread thread = new Thread("UIMenuManager.miToolbarMain") {  //$NON-NLS-1$
				public void run() {
					if (ProjectCompendium.APP.getToolBarManager() != null && !bExternalActivation) {
						ProjectCompendium.APP.getToolBarManager().toggleToolBar(miToolbarMain.isSelected(), UIToolBarManager.MAIN_TOOLBAR);
					}
				}
			};
			thread.start();
			bExternalActivation = false;
		}
		else if (source.equals(miToolbarNode)) {
			Thread thread = new Thread("UIMenuManager.miToolbarNode") {  //$NON-NLS-1$
				public void run() {
					if (ProjectCompendium.APP.getToolBarManager() != null && !bExternalActivation) {
						ProjectCompendium.APP.getToolBarManager().toggleToolBar(miToolbarNode.isSelected(), UIToolBarManager.NODE_TOOLBAR);
					}
				}
			};
			thread.start();
			bExternalActivation = false;
		}
		else if (source.equals(miToolbarTags)) {
			Thread thread = new Thread("UIMenuManager.miToolbarTags") {  //$NON-NLS-1$
				public void run() {
					if (ProjectCompendium.APP.getToolBarManager() != null && !bExternalActivation) {
						ProjectCompendium.APP.getToolBarManager().toggleToolBar(miToolbarTags.isSelected(), UIToolBarManager.TAGS_TOOLBAR);
					}
				}
			};
			thread.start();
			bExternalActivation = false;
		}
		else if (source.equals(miToolbarZoom)) {
			Thread thread = new Thread("UIMenuManager.miToolbarZoom") {  //$NON-NLS-1$
				public void run() {
					if (ProjectCompendium.APP.getToolBarManager() != null && !bExternalActivation) {
						ProjectCompendium.APP.getToolBarManager().toggleToolBar(miToolbarZoom.isSelected(), com.compendium.ui.toolbars.UIToolBarManager.ZOOM_TOOLBAR);
					}
				}
			};
			thread.start();
			bExternalActivation = false;
		}
		else if (source.equals(miToolbarDraw)) {
			Thread thread = new Thread("UIMenuManager.miToolbarDraw") {  //$NON-NLS-1$
				public void run() {

					if (ProjectCompendium.APP.getToolBarManager() != null && !bExternalActivation) {
						ProjectCompendium.APP.getToolBarManager().toggleToolBar(miToolbarDraw.isSelected(), UIToolBarManager.DRAW_TOOLBAR);
					}
				}
			};
			thread.start();
			bExternalActivation = false;
		}
		else if (source.equals(miToolbarData)) {
			Thread thread = new Thread("UIMenuManager.miToolbarData") {  //$NON-NLS-1$
				public void run() {
					if (ProjectCompendium.APP.getToolBarManager() != null && !bExternalActivation) {
						ProjectCompendium.APP.getToolBarManager().toggleToolBar(miToolbarData.isSelected(), UIToolBarManager.DATA_TOOLBAR);
					}
				}
			};
			thread.start();
			bExternalActivation = false;
		}
		/*else if (source.equals(miToolbarMeeting)) {
			Thread thread = new Thread("UIMenuManager.miToolbarMeeting") {
				public void run() {
					if (ProjectCompendium.APP() != null && !bExternalActivation) {
						ProjectCompendium.APP().toggleToolBar(miToolbarMeeting.isSelected(), UIToolBarManager.MEETING_TOOLBAR);
					}
				}
			};
			thread.start();
			bExternalActivation = false;
		}*/
		else if (source.equals(miToolbarFormat)) {
			Thread thread = new Thread("UIMenuManager.miToolbarFormat") {  //$NON-NLS-1$
				public void run() {
					if (ProjectCompendium.APP.getToolBarManager() != null && !bExternalActivation) {
						ProjectCompendium.APP.getToolBarManager().toggleToolBar(miToolbarFormat.isSelected(), UIToolBarManager.FORMAT_TOOLBAR);
					}
				}
			};
			thread.start();
			bExternalActivation = false;
		}	
		else if (source.equals(miStatusBar)) {
			if (((JCheckBoxMenuItem)miStatusBar).isSelected()) {
				FormatProperties.displayStatusBar = true;
				FormatProperties.setFormatProp( "displayStatusBar", "true" );   //$NON-NLS-1$//$NON-NLS-2$
			}
			else {
				FormatProperties.displayStatusBar = false;
				FormatProperties.setFormatProp( "displayStatusBar", "false" );   //$NON-NLS-1$//$NON-NLS-2$
			}
			FormatProperties.saveFormatProps();
			ProjectCompendium.APP.displayStatusBar(miStatusBar.isSelected());
		}
		else if (source.equals(miViewHistoryBar)) {
			if (((JCheckBoxMenuItem)miViewHistoryBar).isSelected()) {
				FormatProperties.displayViewHistoryBar = true;
				FormatProperties.setFormatProp( "displayViewHistoryBar", "true" );   //$NON-NLS-1$//$NON-NLS-2$
			}
			else {
				FormatProperties.displayViewHistoryBar = false;
				FormatProperties.setFormatProp( "displayViewHistoryBar", "false" );   //$NON-NLS-1$//$NON-NLS-2$
			}
			FormatProperties.saveFormatProps();

			ProjectCompendium.APP.displayViewHistoryBar(miViewHistoryBar.isSelected());
		}

		ProjectCompendium.APP.setDefaultCursor();
	}

	/**
	 * Remove the Outline View from the tabbed pane.
	 * @param store indicates whether to store the change to the properties file.
	 */
	public void removeOutlineView(boolean store) {

		if (outlineView != null) {
			outlineView.cleanUp();
			
			ProjectCompendium.APP.oTabbedPane.remove(outlineView);
			outlineView = null;				
		
			miViewsOnly.setSelected(false);
			miViewsAndNodes.setSelected(false);
			miNone.setSelected(true);
				
			miViewsOnly.setEnabled(true);
			miViewsAndNodes.setEnabled(true);
			miNone.setEnabled(false);
    				
			if (store) {			
				FormatProperties.displayOutlineView = DISPLAY_NONE;
				FormatProperties.setFormatProp( "displayOutlineView", DISPLAY_NONE );					  //$NON-NLS-1$
				FormatProperties.saveFormatProps();
			}
		
			ProjectCompendium.APP.oSplitter.resetToPreferredSizes();
		}
	}
	
	/**
	 * open the outline view of the type specified.
	 * @param sType the type of outline view to open
	 * @param store indicates whether to store the change to the properties file.
	 */
	public void addOutlineView(String sType, boolean store) {
		
		if (sType.equals(DISPLAY_VIEWS_AND_NODES)) {
			miViewsOnly.setSelected(false);
			miViewsAndNodes.setSelected(true);
			miNone.setSelected(false);
			
			if(outlineView != null) {
				JTabbedPane tabPane = ProjectCompendium.APP.oTabbedPane;
				int index = tabPane.indexOfComponent(outlineView);
				outlineView.setObjectName(sType);
				outlineView.addNodesToTree();
				tabPane.setTitleAt(index,OUTLINE_VIEW+sType);
				tabPane.setToolTipTextAt(index,OUTLINE_VIEW+sType);				
			} else {
				String sProjectName = ProjectCompendium.APP.getProjectName();
				outlineView = new UIViewOutline(sProjectName, sType);					
				if (!outlineView.isDrawn()) {
					outlineView.draw();
				}
				ProjectCompendium.APP.oTabbedPane.addTab(OUTLINE_VIEW+ sType, null, outlineView, OUTLINE_VIEW+sType);
				ProjectCompendium.APP.oTabbedPane.setSelectedComponent(outlineView); 
			}
			miViewsOnly.setEnabled(true);
			miViewsAndNodes.setEnabled(false);
			miNone.setEnabled(true);
				
			if (store) {				
				FormatProperties.displayOutlineView = sType;
				FormatProperties.setFormatProp( "displayOutlineView", sType );				  //$NON-NLS-1$
				FormatProperties.saveFormatProps();
			}
			
			int textZoom = ProjectCompendium.APP.getToolBarManager().getTextZoom();			
			ProjectCompendium.APP.getMenuManager().onReturnTextAndZoom(textZoom);			
			ProjectCompendium.APP.oSplitter.resetToPreferredSizes();
			
		} else if (sType.equals(DISPLAY_VIEWS_ONLY)) {
			miViewsOnly.setSelected(true);
			miViewsAndNodes.setSelected(false);
			miNone.setSelected(false);
						
			if(outlineView != null) {
				JTabbedPane tabPane = ProjectCompendium.APP.oTabbedPane;				
				int index = tabPane.indexOfComponent(outlineView);
				outlineView.setObjectName(sType);
				outlineView.addNodesToTree();
				tabPane.setTitleAt(index,OUTLINE_VIEW+sType);
				tabPane.setToolTipTextAt(index,OUTLINE_VIEW+sType);
			} else {
				String sProjectName = ProjectCompendium.APP.getProjectName();
				outlineView = new UIViewOutline(sProjectName, sType);					
				if (!outlineView.isDrawn())
					outlineView.draw();
						
				ProjectCompendium.APP.oTabbedPane.addTab(OUTLINE_VIEW+ sType, null, outlineView, OUTLINE_VIEW+sType);
				ProjectCompendium.APP.oTabbedPane.setSelectedComponent(outlineView); 					
			}
			miViewsOnly.setEnabled(false);
			miViewsAndNodes.setEnabled(true);
			miNone.setEnabled(true);
		
			if (store) {
				FormatProperties.displayOutlineView = sType;
				FormatProperties.setFormatProp( "displayOutlineView", sType );			  //$NON-NLS-1$
				FormatProperties.saveFormatProps();
			}
			
			int textZoom = ProjectCompendium.APP.getToolBarManager().getTextZoom();			
			ProjectCompendium.APP.getMenuManager().onReturnTextAndZoom(textZoom);						
			ProjectCompendium.APP.oSplitter.resetToPreferredSizes();
		}
	}

	/**
	 * Remove the unread View from the tabbed pane.
	 * @param store indicates whether to store the change to the properties file.
	 */
	public void removeUnreadView(boolean store){
		
		if (miViewUnread != null && miViewUnread.isSelected()) {
			miViewUnread.setSelected(false);
		}
		if(unreadView != null){
			unreadView.cleanUp();
			
			ProjectCompendium.APP.oTabbedPane.remove(unreadView);
			unreadView = null;	
			ProjectCompendium.APP.oSplitter.resetToPreferredSizes();
			if (store) {			
				FormatProperties.displayUnreadView = false;
				FormatProperties.setFormatProp( "displayUnreadView", "false" );   //$NON-NLS-1$ //$NON-NLS-2$
				FormatProperties.saveFormatProps();
			}
		}
	}
	
	/**
	 * open the unread view.
	 * @param store indicates whether to store the change to the properties file.
	 */
	public void addUnreadView(boolean store) throws SQLException {
		
		if (miViewUnread == null) {
			return;
		}
		
		long lNodeCount = ProjectCompendium.APP.getModel().getNodeService().lGetNodeCount(ProjectCompendium.APP.getModel().getSession());
		long lReadCount = ProjectCompendium.APP.getModel().getNodeService().lGetStateCount(ProjectCompendium.APP.getModel().getSession());
		long lUnreadCount = lNodeCount - lReadCount;
		
        if (lUnreadCount > 250) {		// Note that 250 is a completely arbitrary threshold to set
        	if (JOptionPane.showConfirmDialog(null,
                LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuView.message2a") + "\n" + //$NON-NLS-1$
                LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuView.message2b")+Long.toString(lUnreadCount)+
                LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuView.message2c") + "\n " + //$NON-NLS-1$ //$NON-NLS-2$
                LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuView.message2d") + "\n" + //$NON-NLS-1$
                LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuView.message2e") + "\n\n" +//$NON-NLS-1$
                LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuView.message2f"), //$NON-NLS-1$
                LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuView.message2title"), //$NON-NLS-1$
                JOptionPane.YES_NO_OPTION)
                != JOptionPane.YES_OPTION) {
        		if (miViewUnread.isSelected()) {		// They said 'no'.  Make sure menu item appears
        			miViewUnread.setSelected(false);	// unselected, and then return
        		}
        		return;
        	}
        }
        
        // Go ahead and build the Unread View
		
		String sProjectName = ProjectCompendium.APP.getProjectName();
		unreadView = new UIViewUnread(sProjectName);					
		if (!unreadView.isDrawn())
			unreadView.draw();
				
		ProjectCompendium.APP.oTabbedPane.addTab(UNREAD_VIEW, null, unreadView, UNREAD_VIEW);
		ProjectCompendium.APP.oTabbedPane.setSelectedComponent(unreadView);
		
		if(store){
			FormatProperties.displayUnreadView = true;
			FormatProperties.setFormatProp( "displayUnreadView", "true" );   //$NON-NLS-1$ //$NON-NLS-2$
			FormatProperties.saveFormatProps();
		}
		
		int textZoom = ProjectCompendium.APP.getToolBarManager().getTextZoom();			
		ProjectCompendium.APP.getMenuManager().onReturnTextAndZoom(textZoom);	
		ProjectCompendium.APP.oSplitter.resetToPreferredSizes();		
	}
	
	/**
	 * Remove the tags View from the tabbed pane.
	 * @param store indicates whether to store the change to the properties file.
	 */
	public void removeTagsView(boolean store){
		
		if (miViewTags != null && miViewTags.isSelected()) {
			miViewTags.setSelected(false);
		}		
		
		if(tagsTree != null){
			ProjectCompendium.APP.oTabbedPane.remove(tagsTree);
			tagsTree = null;	
			ProjectCompendium.APP.oSplitter.resetToPreferredSizes();
			miViewTags.setSelected(false);
		}
		
		if (store) {			
			FormatProperties.displayTagsView = false;
			FormatProperties.setFormatProp( "displayTagsView", "false" );   //$NON-NLS-1$ //$NON-NLS-2$
			FormatProperties.saveFormatProps();
		}		
	}
	
	/**
	 * open the tags view.
	 * @param store indicates whether to store the change to the properties file.
	 */
	public void addTagsView(boolean store){		
		if (!miViewTags.isSelected()) {
			miViewTags.setSelected(true);
		}
		
		if (tagsTree != null) {
			//Check it is visible. Not hidden by split pane.
			ProjectCompendium.APP.oTabbedPane.setSelectedComponent(tagsTree);
			ProjectCompendium.APP.oSplitter.resetToPreferredSizes();
			return;
		}
		
		tagsTree = new UITagTreePanel();					
		ProjectCompendium.APP.oTabbedPane.addTab(TAGS_VIEW, null, tagsTree, TAGS_VIEW);
		ProjectCompendium.APP.oTabbedPane.setSelectedComponent(tagsTree);
		tagsTree.setNodeSelected(true); // select any nodes selected by default
		
		if(store){
			FormatProperties.displayTagsView = true;
			FormatProperties.setFormatProp( "displayTagsView", "true" );   //$NON-NLS-1$ //$NON-NLS-2$
			FormatProperties.saveFormatProps();
		}
		
		int textZoom = ProjectCompendium.APP.getToolBarManager().getTextZoom();			
		ProjectCompendium.APP.getMenuManager().onReturnTextAndZoom(textZoom);			
		ProjectCompendium.APP.oSplitter.resetToPreferredSizes();			
	}
		
	/**
	 * Returns the handle for the Tags Tree Panel, if it exists
	 * @return tagsTree - the handle for the Tags window
	 */
	public static UITagTreePanel getTagTreePanel() {
		return tagsTree;
	}	
	
// ZOOM METHODS

	/**
	 * Zoom the current map to the next level (75/50/25/full);
	 */
	public void onZoomNext() {
		UIViewFrame frame = ProjectCompendium.APP.getCurrentFrame();
		if (frame != null) {
			if (frame instanceof UIMapViewFrame) {
				UIMapViewFrame mapframe = (UIMapViewFrame)frame;
				mapframe.onZoomNext();
				ProjectCompendium.APP.resetZoom();
			}
		}
	}

	/**
	 * Zoom the current map using the given scale.
	 * @param scale the zoom scaling factor.
	 */
	public void onZoomTo(double scale) {
		UIViewFrame frame = ProjectCompendium.APP.getCurrentFrame();
		if (frame != null) {
			if (frame instanceof UIMapViewFrame) {
				UIMapViewFrame mapframe = (UIMapViewFrame)frame;
				mapframe.onZoomTo(scale);
				ProjectCompendium.APP.resetZoom();
			}
		}
	}

	/**
	 * Zoom the current map to fit it all on the visible view.
	 */
	public void onZoomToFit() {
		UIViewFrame frame = ProjectCompendium.APP.getCurrentFrame();
		if (frame != null) {
			if (frame instanceof UIMapViewFrame) {
				UIMapViewFrame mapframe = (UIMapViewFrame)frame;
				mapframe.onZoomToFit();
				ProjectCompendium.APP.resetZoom();
			}
		}
	}

	/**
	 * Zoom the current map back to normal and focus on the last selected node.
	 */
	public void onZoomRefocused() {
		UIViewFrame frame = ProjectCompendium.APP.getCurrentFrame();
		if (frame != null) {
			if (frame instanceof UIMapViewFrame) {
				UIMapViewFrame mapframe = (UIMapViewFrame)frame;
				mapframe.onZoomRefocused();
				ProjectCompendium.APP.resetZoom();
			}
		}
	}

	/**
	 * Return the font size to its default 
	 * (To the default specificed by the user in the Project Options)
	 */
	public void onReturnTextToActual() {
		if (this.tagsTree != null) {
			this.tagsTree.onReturnTextToActual();
		}
		if (this.outlineView != null) {
			this.outlineView.onReturnTextToActual();
		}
		if (this.unreadView != null) {
			this.unreadView.onReturnTextToActual();			
		}
	}
	
	/**
	 * Return the font size to its default and then appliy the passed text zoom.
	 * (To the default specificed by the user in the Project Options)
	 */
	public void onReturnTextAndZoom(int zoom) {
		if (this.tagsTree != null) {
			this.tagsTree.onReturnTextAndZoom(zoom);
		}
		if (this.outlineView != null) {
			this.outlineView.onReturnTextAndZoom(zoom);
		}
		if (this.unreadView != null) {
			this.unreadView.onReturnTextAndZoom(zoom);			
		}
	}
	
	/**
	 * Increase the currently dislayed font size by one point.
	 */
	public void onIncreaseTextSize() {
		if (this.tagsTree != null) {
			this.tagsTree.onIncreaseTextSize();
		}
		if (this.outlineView != null) {
			this.outlineView.onIncreaseTextSize();
		}
		if (this.unreadView != null) {
			this.unreadView.onIncreaseTextSize();			
		}
	}
	
	/**
	 * Reduce the currently dislayed font size by one point.
	 */
	public void onReduceTextSize() {
		if (this.tagsTree != null) {
			this.tagsTree.onReduceTextSize();
		}
		if (this.outlineView != null) {
			this.outlineView.onReduceTextSize();
		}
		if (this.unreadView != null) {
			this.unreadView.onReduceTextSize();			
		}
	}
	
	/**
	 * Updates the menus when a database project is closed.
	 */
	public void onDatabaseClose() {

		try {
			mnuMainMenu.setEnabled(false);
		}
		catch (Exception ex) {
			ex.printStackTrace();
			ProjectCompendium.APP.displayError("" + ex.getMessage());   //$NON-NLS-1$
		}

		removeTagsView(false);
		removeOutlineView(false);
		removeUnreadView(false);
	}

	/**
	 * Updates the menus when a database projects is opened.
	 */
	public void onDatabaseOpen() {
		if (ProjectCompendium.APP.getModel() != null) {
			mnuMainMenu.setEnabled(true);
		}
	}
	
	/**
 	 * Indicates when nodes and link are selected and deselected
  	 * @param selected true for selected false for deselected.
	 */
	public void setNodeOrLinkSelected(boolean selected) {
		if (mnuViewAlign != null) {
			mnuViewAlign.setEnabled(selected);
		}
		if (tagsTree != null) {
			tagsTree.setNodeOrLinkSelected(selected);
		}
		if (outlineView != null) {
			outlineView.setNodeSelected(selected);
		}
	}

	/**
 	 * Indicates when nodes on a view are selected and deselected.
  	 * @param selected true for selected false for deselected.
	 */
	public void setNodeSelected(boolean selected) {
		if (mnuViewAlign != null) {
			mnuViewAlign.setEnabled(selected);
		}
		if (tagsTree != null) {
			tagsTree.setNodeSelected(selected);
		}	
		if (outlineView != null) {
			outlineView.setNodeSelected(selected);
		}
	}
	
	
	/**
	 * Record the state of the image rollover option.
	 */
	public void onImageRollover() {

		Thread th = new Thread("APP.onImageRollover") {  //$NON-NLS-1$
	    	public void run() {
				JCheckBoxMenuItem cb = (JCheckBoxMenuItem)miImageRollover;

	            if(cb.isSelected()) {
	            	ProjectCompendium.APP.onImageRollover(true);
				}
	    	    else {
	    	    	ProjectCompendium.APP.onImageRollover(false);
				}
		     }
		};
		th.start();
	}

	/**
	 * Set the auto Search Label setting on and off.
	 */
	public void onSearchLabel() {
		if (miSearchLabel != null) {
			JCheckBoxMenuItem cb = (JCheckBoxMenuItem)miSearchLabel;
	
	        if (cb.isSelected()) {
	            FormatProperties.autoSearchLabel = true;
				FormatProperties.setFormatProp( "autoSearchLabel", "true" );   //$NON-NLS-1$//$NON-NLS-2$
			}
	    	else {
	           	FormatProperties.autoSearchLabel = false;
				FormatProperties.setFormatProp( "autoSearchLabel", "false" );   //$NON-NLS-1$//$NON-NLS-2$
			}
			FormatProperties.saveFormatProps();
		}
	}

	/**
	 * Enable/disable the map menu and its components.
	 * @param enabled true to enable, false to disable.
	 */
	public void setMapMenuEnabled(boolean enabled) {
		if (miAerialView != null) {
			miAerialView.setEnabled(enabled);
		}
		if (mnuZoom != null) {
			mnuZoom.setEnabled(enabled);
		}
		if (miImageRollover != null) {
			miImageRollover.setEnabled(enabled);
		}
		if (miSearchLabel != null) {
			miSearchLabel.setEnabled(enabled);
		}
		if (mnuViewArrange != null) {
			mnuViewArrange.setEnabled(enabled);
		}
	}

	/**
	 * Select/unselect the aerial view.
	 * @param enabled true to select, false to unselect.
	 */
	public void setAerialView(boolean selected) {
		if (miAerialView != null) {
			miAerialView.setSelected(selected);
		}
	}

	/**
	 * Select/unselect the image rollover .
	 * @param enabled true to enable, false to disable.
	 */
	public void updateImageRollover(boolean enabled) {
		if (miImageRollover != null) {
			JCheckBoxMenuItem cb = (JCheckBoxMenuItem)miImageRollover;
			cb.setSelected(enabled);
		}
	}

	/**
	 * Select/unselect the given toolbar.
	 * @param enabled true to select, false to unselect.
	 */
	public void setToolbar(int nToolbar, boolean selected) {
		bExternalActivation = true;
		
		switch (nToolbar) {
		case UIToolBarManager.MAIN_TOOLBAR: {
			if (miToolbarMain != null)
				miToolbarMain.setSelected(selected);
			break;
		}
		case UIToolBarManager.DRAW_TOOLBAR: {
			if (miToolbarDraw != null)
				miToolbarDraw.setSelected(selected);			
			break;
		}
		case UIToolBarManager.NODE_TOOLBAR: {
			if (miToolbarNode != null)
				miToolbarNode.setSelected(selected);			
			break;
		}
		case UIToolBarManager.DATA_TOOLBAR: {
			if (miToolbarData != null)
				miToolbarData.setSelected(selected);			
			break;
		}
		case UIToolBarManager.TAGS_TOOLBAR: {
			if (miToolbarTags != null)
				miToolbarTags.setSelected(selected);			
			break;
		}
		case UIToolBarManager.ZOOM_TOOLBAR: {
			if (miToolbarZoom != null)
				miToolbarZoom.setSelected(selected);			
			break;
		}
		case UIToolBarManager.FORMAT_TOOLBAR: {
			if (miToolbarFormat != null)
				miToolbarFormat.setSelected(selected);			
			break;
		}	
		
		//case UIToolBarManager.MEETING_TOOLBAR: {
		//	if (miToolbarMeeting != null)
		//		miToolbarMeeting.setSelected(selected);
		//	break;
		//}
		}
	}
	
	/**
	 * Update the look and feel of the menu.
	 */
	public void updateLAF() {
		if (mnuMainMenu != null) {
			SwingUtilities.updateComponentTreeUI(mnuMainMenu);
		}		
		if (outlineView != null) {
			SwingUtilities.updateComponentTreeUI(outlineView);
		}
		if (unreadView != null) {
			SwingUtilities.updateComponentTreeUI(unreadView);
		}		
		if (tagsTree != null) {
			SwingUtilities.updateComponentTreeUI(tagsTree);
		}
	}
	
	/**
	 * Gets the outline view object
	 * @return UIViewOutline the UIViewOutline object
	 */
	public UIViewOutline getOutlineView() {
		return outlineView;
	}

	/**
	 * @return Returns the unreadView.
	 */
	public UIViewUnread getUnreadView() {
		return unreadView;
	}

	/**
	 * @param outlineView The outlineView to set.
	 */
	public void setOutlineView(UIViewOutline outlineView) {
		this.outlineView = outlineView;
	}

	/**
	 * @param unreadView The unreadView to set.
	 */
	public void setUnreadView(UIViewUnread unreadView) {
		this.unreadView = unreadView;
	}		
}
