/********************************************************************************
 *                                                                              *
 *  (c) Copyright 2009 Verizon Communications USA and The Open University UK    *
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

import java.awt.event.*;

import javax.help.*;
import javax.swing.*;

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
public class UIMenuView implements IUIMenu, ActionListener, IUIConstants, ICoreConstants {

	/** The map menu.*/
	private JMenu				mnuMainMenu					= null;

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
	private JMenuItem    		miViewUnread				= null;

	/** The JMenuItem to open tags view.  */
	private JMenuItem    		miViewTags				= null;

	/** Idicates is a toolbar checkbox was checked/unchecked externally. Prevents loop.*/
	private boolean bExternalActivation					= false;

	/** The UIViewOutline to display for outline view      */
	private UIViewOutline 		outlineView 			= null;

	/** To display unread View */
	private UIViewUnread 		unreadView				= null	;
	
	/** The tags tree view.*/
	private UITagTreePanel 		tagsTree				= null;
	
	//private boolean				externalActivation		= false;
	
	/**Indicates whether this menu is draw as a Simple interface or a advance user inteerface.*/
	private boolean bSimpleInterface					= false;	
	
	/**
	 * Constructor.
	 * @param bSimple true if the simple interface should be draw, false if the advanced. 	 * 
	 */
	public UIMenuView(boolean bSimple) {
		
		this.bSimpleInterface = bSimple;		
		
		mnuMainMenu	= new JMenu("View");  //$NON-NLS-1$
		CSH.setHelpIDString(mnuMainMenu,"menus.map"); //$NON-NLS-1$
		mnuMainMenu.setMnemonic(KeyEvent.VK_V);
		
		createMenuItems();
	}

	/**
	 * If true, redraw the simple form of this menu, else redraw the complex form.
	 * @param isSimple true for the simple menu, false for the advanced.
	 */public void setIsSimple(boolean isSimple) {
		bSimpleInterface = isSimple;
		
		// SET THE VIEW HISTORY BAR ACTIVE - BUT IT WILL NOT BE ON THIS MENU
		if (bSimpleInterface) {
            FormatProperties.displayViewHistoryBar = true;
			FormatProperties.setFormatProp( "displayViewHistoryBar", "true" );  
			FormatProperties.saveFormatProps();
			
		}
		recreateMenu();
	}

	/**
	 * Redraw the menu items
	 */
	private void recreateMenu() {
		mnuMainMenu.removeAll();
		createMenuItems();
		onDatabaseOpen();				
	}
	
	/**
	 * Create and return the View menu.
	 * @return JMenu the Map menu.
	 */
	private JMenu createMenuItems() {

		// Lakshmi 4/3/05 - Add the outline view options to View menu.
		mnuViewOutline = new JMenu("Outline View"); 
		mnuViewOutline.setMnemonic(KeyEvent.VK_O);
		mnuViewOutline.addActionListener(this);
		
		miViewsOnly = new JRadioButtonMenuItem(IUIConstants.DISPLAY_VIEWS_ONLY);
		miViewsOnly.setMnemonic(KeyEvent.VK_V);
		miViewsOnly.addActionListener(this);
		mnuViewOutline.add(miViewsOnly);
		
		miViewsAndNodes = new JRadioButtonMenuItem(IUIConstants.DISPLAY_VIEWS_AND_NODES);
		miViewsAndNodes.setMnemonic(KeyEvent.VK_N);
		miViewsAndNodes.addActionListener(this);
		mnuViewOutline.add(miViewsAndNodes);
		
		miNone = new JRadioButtonMenuItem(IUIConstants.DISPLAY_NONE);
		miNone.setMnemonic(KeyEvent.VK_D);
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
		
		miViewUnread = new JCheckBoxMenuItem("Unread View"); 
		miViewUnread.setMnemonic(KeyEvent.VK_U);
		miViewUnread.addActionListener(this);
		
		if(FormatProperties.displayUnreadView){
			miViewUnread.setSelected(true);
		} else {
			miViewUnread.setSelected(false);
		}
		
		mnuMainMenu.add(miViewUnread);
		mnuMainMenu.addSeparator();
		
		miViewTags = new JCheckBoxMenuItem("Tag View"); 
		miViewTags.setMnemonic(KeyEvent.VK_G);
		miViewTags.addActionListener(this);		
		
		mnuMainMenu.add(miViewTags);
		mnuMainMenu.addSeparator();

		miViewMap = new JMenuItem("Find a Map/List...");  //$NON-NLS-1$
		miViewMap.setMnemonic(KeyEvent.VK_F);
		miViewMap.addActionListener(this);
		mnuMainMenu.add(miViewMap);

		mnuMainMenu.addSeparator();

		// TOOLBAR MENU
		mnuToolbars	= new JMenu("Toolbars");  //$NON-NLS-1$
		CSH.setHelpIDString(mnuToolbars,"menus.map"); //$NON-NLS-1$
		mnuToolbars.setMnemonic(KeyEvent.VK_T);
		
		miToolbarMain = new JCheckBoxMenuItem("Main Toolbar");  //$NON-NLS-1$
		miToolbarMain.setMnemonic(KeyEvent.VK_M);
		miToolbarMain.addActionListener(this);
		mnuToolbars.add(miToolbarMain);

		miToolbarData = new JCheckBoxMenuItem("Data Source Toolbar");  //$NON-NLS-1$
		miToolbarData.setMnemonic(KeyEvent.VK_D);
		miToolbarData.addActionListener(this);
		mnuToolbars.add(miToolbarData);

		miToolbarNode = new JCheckBoxMenuItem("Node Creation Toolbar");  //$NON-NLS-1$
		miToolbarNode.setMnemonic(KeyEvent.VK_N);
		miToolbarNode.addActionListener(this);
		mnuToolbars.add(miToolbarNode);

		miToolbarFormat = new JCheckBoxMenuItem("Node Format Toolbar"); 
		miToolbarFormat.setMnemonic(KeyEvent.VK_F);
		miToolbarFormat.addActionListener(this);
		mnuToolbars.add(miToolbarFormat);
				
		miToolbarDraw = new JCheckBoxMenuItem("Scribble Toolbar");  //$NON-NLS-1$
		miToolbarDraw.setMnemonic(KeyEvent.VK_S);
		miToolbarDraw.addActionListener(this);
		mnuToolbars.add(miToolbarDraw);
				
		miToolbarTags = new JCheckBoxMenuItem("Tags Toolbar");  //$NON-NLS-1$
		miToolbarTags.setMnemonic(KeyEvent.VK_T);
		miToolbarTags.addActionListener(this);
		mnuToolbars.add(miToolbarTags);

		miToolbarZoom = new JCheckBoxMenuItem("Zoom Toolbar");  //$NON-NLS-1$
		miToolbarZoom.setMnemonic(KeyEvent.VK_Z);
		miToolbarZoom.addActionListener(this);
		mnuToolbars.add(miToolbarZoom);
		
		//miToolbarMeeting = new JCheckBoxMenuItem("Meeting Toolbar");
		//miToolbarMeeting.setMnemonic(KeyEvent.VK_E);
		//miToolbarMeeting.addActionListener(this);
		//mnuToolbars.add(miToolbarMeeting);

		mnuToolbars.addSeparator();
		
		miResetToolbars = new JMenuItem("Reset ToolBars to Default");  //$NON-NLS-1$
		miResetToolbars.setMnemonic(KeyEvent.VK_R);
 		miResetToolbars.addActionListener(this);
 		mnuToolbars.add(miResetToolbars);
		
		mnuMainMenu.add(mnuToolbars);

		miStatusBar = new JCheckBoxMenuItem("Status Bar");  //$NON-NLS-1$
		miStatusBar.setMnemonic(KeyEvent.VK_S);
		miStatusBar.addActionListener(this);
		mnuMainMenu.add(miStatusBar);
		if (FormatProperties.displayStatusBar)
       		miStatusBar.setSelected(true);
		else
			miStatusBar.setSelected(false);
	
		miViewHistoryBar = new JCheckBoxMenuItem("View History Bar");  //$NON-NLS-1$
		miViewHistoryBar.setMnemonic(KeyEvent.VK_B);
		miViewHistoryBar.addActionListener(this);
		mnuMainMenu.add(miViewHistoryBar);
		if (FormatProperties.displayViewHistoryBar)
       		miViewHistoryBar.setSelected(true);
		else
			miViewHistoryBar.setSelected(false);

		mnuMainMenu.addSeparator();

		// TICK BOX FOR ACTIVATING AERIAL VIEW
		miAerialView = new JCheckBoxMenuItem("Aerial View");  //$NON-NLS-1$
		miAerialView.setMnemonic(KeyEvent.VK_V);

		if (FormatProperties.aerialView)
        	miAerialView.setSelected(true);
		else
			miAerialView.setSelected(false);

		miAerialView.addActionListener(this);
		mnuMainMenu.add(miAerialView);

		mnuMainMenu.addSeparator();

		// ZOOM MENU
		mnuZoom	= new JMenu("Zoom");  //$NON-NLS-1$
		CSH.setHelpIDString(mnuZoom,"menus.map"); //$NON-NLS-1$
		mnuZoom.setMnemonic(KeyEvent.VK_Z);

		miZoomNormal = new JMenuItem("Zoom 100%");  //$NON-NLS-1$
		miZoomNormal.setMnemonic(KeyEvent.VK_Z);
		miZoomNormal.addActionListener(this);
		mnuZoom.add(miZoomNormal);

		miZoom75 = new JMenuItem("Zoom 75%");  //$NON-NLS-1$
		miZoom75.setMnemonic(KeyEvent.VK_7);
		miZoom75.addActionListener(this);
		mnuZoom.add(miZoom75);

		miZoom50 = new JMenuItem("Zoom 50%");  //$NON-NLS-1$
		miZoom50.setMnemonic(KeyEvent.VK_5);
		miZoom50.addActionListener(this);
		mnuZoom.add(miZoom50);

		miZoom25 = new JMenuItem("Zoom 25%");  //$NON-NLS-1$
		miZoom25.setMnemonic(KeyEvent.VK_2);
		miZoom25.addActionListener(this);
		mnuZoom.add(miZoom25);

		miZoomFit = new JMenuItem("Fit to Page");  //$NON-NLS-1$
		miZoomFit.setMnemonic(KeyEvent.VK_F);
		miZoomFit.addActionListener(this);
		mnuZoom.add(miZoomFit);

		miZoomFocus = new JMenuItem("Focus Node");  //$NON-NLS-1$
		miZoomFocus.setMnemonic(KeyEvent.VK_N);
		miZoomFocus.addActionListener(this);
		mnuZoom.add(miZoomFocus);

		mnuMainMenu.add(mnuZoom);

		// TICK BOX FOR ACTIVATING IMAGE ROLLOVER
		miImageRollover = new JCheckBoxMenuItem("Image Rollover");  //$NON-NLS-1$
		miImageRollover.setMnemonic(KeyEvent.VK_I);

		if (FormatProperties.imageRollover)
        	miImageRollover.setSelected(true);
		else
			miImageRollover.setSelected(false);

		miImageRollover.addActionListener(this);
		mnuMainMenu.add(miImageRollover);

		mnuMainMenu.addSeparator();

		miSearchLabel = new JCheckBoxMenuItem("Auto Label Searching");  //$NON-NLS-1$
		miSearchLabel.setMnemonic(KeyEvent.VK_L);
	
		if (FormatProperties.autoSearchLabel)
       		miSearchLabel.setSelected(true);
		else
			miSearchLabel.setSelected(false);

		miSearchLabel.addActionListener(this);
		mnuMainMenu.add(miSearchLabel);
		
		mnuMainMenu.addSeparator();

		//Begin edit, Lakshmi (11/3/05)
		//include Top - Down and Left - Right Option in Arrange Menu.
		mnuViewArrange = new JMenu("Arrange");  //$NON-NLS-1$
		mnuViewArrange.setMnemonic(KeyEvent.VK_R);
		mnuViewArrange.addActionListener(this);

		miMenuItemLeftRightArrange = new JMenuItem("Left to Right");  //$NON-NLS-1$
		miMenuItemLeftRightArrange.addActionListener(this);
		miMenuItemLeftRightArrange.setMnemonic(KeyEvent.VK_R);
		mnuViewArrange.add(miMenuItemLeftRightArrange);

		mnuViewArrange.addSeparator();

		miMenuItemTopDownArrange = new JMenuItem("Top-Down");  //$NON-NLS-1$
		miMenuItemTopDownArrange.addActionListener(this);
		miMenuItemTopDownArrange.setMnemonic(KeyEvent.VK_W);
		mnuViewArrange.add(miMenuItemTopDownArrange);

		mnuMainMenu.add(mnuViewArrange);

		mnuViewAlign = new JMenu("Align");  //$NON-NLS-1$
		mnuViewAlign.setMnemonic(KeyEvent.VK_A);
		mnuViewAlign.setEnabled(false);

		miMenuItemAlignLeft = new JMenuItem("Left");  //$NON-NLS-1$
		miMenuItemAlignLeft.addActionListener(this);
		miMenuItemAlignLeft.setMnemonic(KeyEvent.VK_L);
		mnuViewAlign.add(miMenuItemAlignLeft);

		miMenuItemAlignCenter = new JMenuItem("Center");  //$NON-NLS-1$
		miMenuItemAlignCenter.addActionListener(this);
		miMenuItemAlignCenter.setMnemonic(KeyEvent.VK_C);
		mnuViewAlign.add(miMenuItemAlignCenter);

		miMenuItemAlignRight = new JMenuItem("Right");  //$NON-NLS-1$
		miMenuItemAlignRight.addActionListener(this);
		miMenuItemAlignRight.setMnemonic(KeyEvent.VK_R);
		mnuViewAlign.add(miMenuItemAlignRight);

		mnuViewAlign.addSeparator();

		miMenuItemAlignTop = new JMenuItem("Top");  //$NON-NLS-1$
		miMenuItemAlignTop.addActionListener(this);
		miMenuItemAlignTop.setMnemonic(KeyEvent.VK_T);
		mnuViewAlign.add(miMenuItemAlignTop);

		miMenuItemAlignMiddle = new JMenuItem("Middle");  //$NON-NLS-1$
		miMenuItemAlignMiddle.addActionListener(this);
		miMenuItemAlignMiddle.setMnemonic(KeyEvent.VK_M);
		mnuViewAlign.add(miMenuItemAlignMiddle);

		miMenuItemAlignBottom = new JMenuItem("Bottom");  //$NON-NLS-1$
		miMenuItemAlignBottom.addActionListener(this);
		miMenuItemAlignBottom.setMnemonic(KeyEvent.VK_B);
		mnuViewAlign.add(miMenuItemAlignBottom);

		mnuMainMenu.add(mnuViewAlign);

		return mnuMainMenu;
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
		else if (source.equals(miViewMap))
			ProjectCompendium.APP.onViewMap();
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
				addUnreadView(true);
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
			Thread thread = new Thread("Format Font") { //$NON-NLS-1$
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
			Thread thread = new Thread("UIMenuManager.miToolbarMain") { //$NON-NLS-1$
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
			Thread thread = new Thread("UIMenuManager.miToolbarNode") { //$NON-NLS-1$
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
			Thread thread = new Thread("UIMenuManager.miToolbarTags") { //$NON-NLS-1$
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
			Thread thread = new Thread("UIMenuManager.miToolbarZoom") { //$NON-NLS-1$
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
			Thread thread = new Thread("UIMenuManager.miToolbarDraw") { //$NON-NLS-1$
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
			Thread thread = new Thread("UIMenuManager.miToolbarData") { //$NON-NLS-1$
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
			Thread thread = new Thread("UIMenuManager.miToolbarFormat") { //$NON-NLS-1$
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
				FormatProperties.setFormatProp( "displayStatusBar", "true" ); //$NON-NLS-1$ //$NON-NLS-2$
			}
			else {
				FormatProperties.displayStatusBar = false;
				FormatProperties.setFormatProp( "displayStatusBar", "false" ); //$NON-NLS-1$ //$NON-NLS-2$
			}
			FormatProperties.saveFormatProps();
			ProjectCompendium.APP.displayStatusBar(miStatusBar.isSelected());
		}
		else if (source.equals(miViewHistoryBar)) {
			if (((JCheckBoxMenuItem)miViewHistoryBar).isSelected()) {
				FormatProperties.displayViewHistoryBar = true;
				FormatProperties.setFormatProp( "displayViewHistoryBar", "true" ); //$NON-NLS-1$ //$NON-NLS-2$
			}
			else {
				FormatProperties.displayViewHistoryBar = false;
				FormatProperties.setFormatProp( "displayViewHistoryBar", "false" ); //$NON-NLS-1$ //$NON-NLS-2$
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
				FormatProperties.setFormatProp( "displayOutlineView", DISPLAY_NONE );					 
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
				tabPane.setTitleAt(index,IUIConstants.OUTLINE_VIEW+sType);
				tabPane.setToolTipTextAt(index,IUIConstants.OUTLINE_VIEW+sType);				
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
				FormatProperties.setFormatProp( "displayOutlineView", sType );				 
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
				tabPane.setTitleAt(index,IUIConstants.OUTLINE_VIEW+sType);
				tabPane.setToolTipTextAt(index,IUIConstants.OUTLINE_VIEW+sType);
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
				FormatProperties.setFormatProp( "displayOutlineView", sType );			 
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
		
		if (miViewUnread.isSelected()) {
			miViewUnread.setSelected(false);
		}
		if(unreadView != null){
			unreadView.cleanUp();
			
			ProjectCompendium.APP.oTabbedPane.remove(unreadView);
			unreadView = null;	
			ProjectCompendium.APP.oSplitter.resetToPreferredSizes();
			if (store) {			
				FormatProperties.displayUnreadView = false;
				FormatProperties.setFormatProp( "displayUnreadView", "false" );  
				FormatProperties.saveFormatProps();
			}
		}
	}
	
	/**
	 * open the unread view.
	 * @param store indicates whether to store the change to the properties file.
	 */
	public void addUnreadView(boolean store){
		
		if (miViewUnread == null) {
			return;
		}
		
		String sProjectName = ProjectCompendium.APP.getProjectName();
		unreadView = new UIViewUnread(sProjectName);					
		if (!unreadView.isDrawn())
			unreadView.draw();
				
		ProjectCompendium.APP.oTabbedPane.addTab(UNREAD_VIEW, null, unreadView, UNREAD_VIEW);
		ProjectCompendium.APP.oTabbedPane.setSelectedComponent(unreadView);
		
		if(store){
			FormatProperties.displayUnreadView = true;
			FormatProperties.setFormatProp( "displayUnreadView", "true" );  
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
		
		if (miViewTags.isSelected()) {
			miViewTags.setSelected(false);
		}		
		
		if(tagsTree != null){
			ProjectCompendium.APP.oTabbedPane.remove(tagsTree);
			tagsTree = null;	
			ProjectCompendium.APP.oSplitter.resetToPreferredSizes();
			miViewTags.setSelected(false);
		}
		
		if (store) {			
			FormatProperties.displayUnreadView = false;
			FormatProperties.setFormatProp( "displayTagsView", "false" );  
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
		
		if(store){
			FormatProperties.displayUnreadView = true;
			FormatProperties.setFormatProp( "displayTagsView", "true" );  
			FormatProperties.saveFormatProps();
		}
		
		int textZoom = ProjectCompendium.APP.getToolBarManager().getTextZoom();			
		ProjectCompendium.APP.getMenuManager().onReturnTextAndZoom(textZoom);			
		ProjectCompendium.APP.oSplitter.resetToPreferredSizes();			
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
			ProjectCompendium.APP.displayError("" + ex.getMessage());  //$NON-NLS-1$
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

		Thread th = new Thread("APP.onImageRollover") { //$NON-NLS-1$
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
				FormatProperties.setFormatProp( "autoSearchLabel", "true" ); //$NON-NLS-1$ //$NON-NLS-2$
			}
	    	else {
	           	FormatProperties.autoSearchLabel = false;
				FormatProperties.setFormatProp( "autoSearchLabel", "false" ); //$NON-NLS-1$ //$NON-NLS-2$
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
	
	/**
	 * Return a reference to the main menu.
	 * @return JMenu a reference to the main menu.
	 */
	public JMenu getMenu() {
		return mnuMainMenu;
	}		
}
