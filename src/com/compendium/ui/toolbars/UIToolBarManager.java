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

package com.compendium.ui.toolbars;

import java.awt.*;
import java.io.*;
import java.util.*;

import javax.help.*;
import javax.swing.*;
import javax.swing.undo.*;

import org.w3c.dom.*;

import com.compendium.core.*;
import com.compendium.*;
import com.compendium.ui.*;
import com.compendium.ui.toolbars.system.*;
import com.compendium.io.xml.*;
import com.compendium.core.datamodel.*;
import com.compendium.ui.menus.*;


/**
 * This class manages all the toolbars
 *
 * @author	Michelle Bachler
 * @version	1.0
 */
public class UIToolBarManager implements IUIConstants, ICoreConstants, IUIToolBarManager {

	/** A reference to the main toolbar.*/
	public final static int MAIN_TOOLBAR			= 0;

	/** A reference to the node toolbar.*/
	public final static int NODE_TOOLBAR			= 1;

	/** A reference to the tags toolbar.*/
	public final static int TAGS_TOOLBAR			= 2;

	/** A reference to the zoom toolbar.*/
	public final static int ZOOM_TOOLBAR			= 3;

	/** A reference to the drawing toolbar.*/
	public final static int DRAW_TOOLBAR			= 4;

	/** A reference to the data source mode swap toolbar.*/
	public final static int DATA_TOOLBAR			= 5;

	/** A reference to the meeting toolbar.*/
	public final static int MEETING_TOOLBAR			= 6;

	/** A reference to the label formatter toolbar.*/
	public final static int FORMAT_TOOLBAR			= 7;

	/** A reference to the node formatter toolbar.*/
	public final static int NODE_FORMAT_TOOLBAR		= 8;

	/** A reference to the link formatter toolbar.*/
	public final static int LINK_FORMAT_TOOLBAR		= 9;

	
	/** The parent frame for this class.*/
	private ProjectCompendiumFrame	oParent			= null;

	/** The HelpBroker to use for toolbar help.*/
	private HelpBroker 				mainHB			= null;

	/** The top toolbar controller panel.*/
	private UIToolBarController	oTopToolBarManager	= null;

	/** The bottom toolbar controller panel.*/
	private UIToolBarController	oBottomToolBarManager= null;

	/** The left toolbar controller panel.*/
	private UIToolBarController	oLeftToolBarManager	= null;

	/** the right toolbar controller panel.*/
	private UIToolBarController	oRightToolBarManager= null;

	
	/** The main toolbar manager class.*/
	private UIToolBarMain		oMainToolBar		= null;

	/** The node toolbar manager class.*/
	private UIToolBarNode		oNodeToolBar		= null;

	/** The Toolbar manager for the Zoom toolbar.*/
	private UIToolBarZoom		oZoomToolBar		= null;

	/** The Toolbar manager for the Scrible toolbar.*/
	private UIToolBarScribble	oScribbleToolBar	= null;	

	/** The Toolbar manager for the data source toolbar.*/
	private UIToolBarData		oDataToolBar		= null;
	
	/** The label format toolbar manager.*/
	private UIToolBarFormat 	oFormatToolBar		= null;

	/** The node format toolbar manager.*/
	private UIToolBarFormatNode	oNodeFormatToolBar	= null;

	/** The link format toolbar manager.*/
	private UIToolBarFormatLink	oLinkFormatToolBar	= null;

	/** The Toolbar manager for the tags toolbar.*/
	private UIToolBarTags		oTagsToolBar		= null;

	/** The Toolbar manager for the meeting toolbar.*/
	private UIToolBarMeeting	oMeetingToolBar		= null;	

	/**Indicates whether this menu is draw as a Simple interface or a advance user inteerface.*/
	private boolean bSimpleInterface					= false;		
	
	
	/**
	 * Constructor. Create a new instance of UIToolBarManager, with the given proerties.
	 * @param parent the parent frame for the application.
	 * @param hb the HelpBroker to use.
	 * @param isSimple indicates if the toolbars should be draw for a simple user interface, false for a complex one.
	 */
	public UIToolBarManager(ProjectCompendiumFrame parent,HelpBroker hb, boolean isSimple) {

		oParent = parent;
		mainHB = hb;
		bSimpleInterface = isSimple;		
	}

	/**
	 * Update the look and feel of all the toolbars
	 */
	public void updateLAF() {
		
		if (oMainToolBar != null) {
			oMainToolBar.updateLAF();
		}	
		if (oScribbleToolBar != null) {
			oScribbleToolBar.updateLAF();
		}				
		if (oZoomToolBar != null) {
			oZoomToolBar.updateLAF();
		}						
		if (oFormatToolBar != null) {
			oFormatToolBar.updateLAF();
		}		
		if (oNodeFormatToolBar != null) {
			oNodeFormatToolBar.updateLAF();
		}		
		if (oLinkFormatToolBar != null) {
			oLinkFormatToolBar.updateLAF();
		}		
		if (oNodeToolBar != null) {
			oNodeToolBar.updateLAF();
		}				
		if (oDataToolBar != null) {
			oDataToolBar.updateLAF();
		}		
		if (oTagsToolBar != null) {
			oTagsToolBar.updateLAF();
		}		
		if (oMeetingToolBar != null) {
			oMeetingToolBar.updateLAF();
		}	
		
		SwingUtilities.updateComponentTreeUI(oTopToolBarManager);
		SwingUtilities.updateComponentTreeUI(oBottomToolBarManager);
		SwingUtilities.updateComponentTreeUI(oLeftToolBarManager);
		SwingUtilities.updateComponentTreeUI(oRightToolBarManager);
	}

	/**
	 * Create the toolbar controllers and load the toolbar data from the XML file where it has been save.
	 * If no data has been saved, set up the toolbars in thier default positions.
	 */
	public void createToolbars() {

		try {
			//load toolbar data
			XMLReader reader = new XMLReader();

			Document document = reader.read("System"+ProjectCompendium.sFS+"resources"+ProjectCompendium.sFS+"toolbars.xml", true);

			if (document == null)
				throw new Exception("Toolbar data could not be loaded");

			NamedNodeMap attrs = null;
			NodeList controllers = document.getElementsByTagName("controller");

			if (controllers == null || controllers.getLength() == 0)
				throw new Exception("Toolbar data could not be loaded");

			int count = controllers.getLength();

			for (int i=0; i< count; i++) {

				Node controller = controllers.item(i);
				attrs = controller.getAttributes();
				
				Attr position = (Attr)attrs.getNamedItem("position");
				int nPosition = new Integer(position.getValue()).intValue();

				Attr oClosed = (Attr)attrs.getNamedItem("isClosed");
				boolean isClosed = new Boolean(oClosed.getValue()).booleanValue();

				loadToolBars(controller, isClosed, nPosition);
			}

			// PARANOIA
			if (oTopToolBarManager == null || oBottomToolBarManager == null
						|| oLeftToolBarManager == null || oRightToolBarManager == null) {
				throw new Exception("Toolbars could not be created");
			}
			
			// ADD ANY FLOATING TOOLBARS
			NodeList floaters = document.getElementsByTagName("floater");
			createFloatingToolBars(floaters);
			
			if (oMainToolBar == null) {
				oMainToolBar = new UIToolBarMain(this, oParent, MAIN_TOOLBAR, bSimpleInterface);
				oTopToolBarManager.addToolBar(oMainToolBar.getToolBar(), MAIN_TOOLBAR, true, true, oMainToolBar.getDefaultActiveState(), 0);
				updateToolbarMenu(MAIN_TOOLBAR, oMainToolBar.getDefaultActiveState());
			}
			if (oNodeToolBar == null) {
				oNodeToolBar = new UIToolBarNode(this, oParent, NODE_TOOLBAR);
				oLeftToolBarManager.addToolBar(oNodeToolBar.getToolBar(), NODE_TOOLBAR, true, true, oNodeToolBar.getDefaultActiveState(), 0);
				updateToolbarMenu(NODE_TOOLBAR, oNodeToolBar.getDefaultActiveState());
			}
			if (oZoomToolBar == null) {
				oZoomToolBar = new UIToolBarZoom(this, oParent, ZOOM_TOOLBAR);			
				oTopToolBarManager.addToolBar(oZoomToolBar.getToolBar(), ZOOM_TOOLBAR, true, true, oZoomToolBar.getDefaultActiveState(), 0);
				updateToolbarMenu(ZOOM_TOOLBAR, oZoomToolBar.getDefaultActiveState());
			}
			if (oTagsToolBar == null) {
				oTagsToolBar = new UIToolBarTags(this, oParent, TAGS_TOOLBAR);
				oTopToolBarManager.addToolBar(oTagsToolBar.getToolBar(), TAGS_TOOLBAR, true, true, oTagsToolBar.getDefaultActiveState(), 0);
				updateToolbarMenu(TAGS_TOOLBAR, oTagsToolBar.getDefaultActiveState());
			}			
			if (oScribbleToolBar == null) {
				oScribbleToolBar = new UIToolBarScribble(this, oParent, DRAW_TOOLBAR);							
				oBottomToolBarManager.addToolBar(oScribbleToolBar.getToolBar(), DRAW_TOOLBAR, true, true, oScribbleToolBar.getDefaultActiveState(), 0);
				updateToolbarMenu(DRAW_TOOLBAR, oScribbleToolBar.getDefaultActiveState());					
			}
			if (oDataToolBar == null) {
				oDataToolBar = new UIToolBarData(this, oParent, DATA_TOOLBAR);
				oBottomToolBarManager.addToolBar(oDataToolBar.getToolBar(), DATA_TOOLBAR, true, true, oDataToolBar.getDefaultActiveState(), 0);
				updateToolbarMenu(DATA_TOOLBAR, oDataToolBar.getDefaultActiveState());									
			}
			if (oFormatToolBar == null) {
				oFormatToolBar = new UIToolBarFormat(this, oParent, FORMAT_TOOLBAR);
				oTopToolBarManager.addToolBar(oFormatToolBar.getToolBar(), FORMAT_TOOLBAR, true, true, oFormatToolBar.getDefaultActiveState(), 1);
				updateToolbarMenu(FORMAT_TOOLBAR, oFormatToolBar.getDefaultActiveState());
			}
			if (oNodeFormatToolBar == null) {
				oNodeFormatToolBar = new UIToolBarFormatNode(this, oParent, NODE_FORMAT_TOOLBAR);
				oTopToolBarManager.addToolBar(oNodeFormatToolBar.getToolBar(), NODE_FORMAT_TOOLBAR, true, true, oNodeFormatToolBar.getDefaultActiveState(), 1);
				updateToolbarMenu(NODE_FORMAT_TOOLBAR, oNodeFormatToolBar.getDefaultActiveState());
			}
			if (oLinkFormatToolBar == null) {
				oLinkFormatToolBar = new UIToolBarFormatLink(this, oParent, LINK_FORMAT_TOOLBAR);
				oTopToolBarManager.addToolBar(oLinkFormatToolBar.getToolBar(), LINK_FORMAT_TOOLBAR, true, true, oLinkFormatToolBar.getDefaultActiveState(), 1);
				updateToolbarMenu(LINK_FORMAT_TOOLBAR, oLinkFormatToolBar.getDefaultActiveState());
			}
		}
		catch(Exception ex) {
			ex.printStackTrace();
			System.out.flush();

			// IF THERE IS NO STORED DATA OR SOMETHING WENT WRONG, RESET THE TOOLBAR TO THEIR DEFAULTS
			oTopToolBarManager = new UIToolBarController(this, UIToolBarController.TOP, false);
			oParent.getMainPanel().add(oTopToolBarManager, BorderLayout.NORTH);

			oBottomToolBarManager = new UIToolBarController(this, UIToolBarController.BOTTOM, false);
			oParent.getMainPanel().add(oBottomToolBarManager, BorderLayout.SOUTH);

			oLeftToolBarManager = new UIToolBarController(this, UIToolBarController.LEFT, false);
			oParent.getMainPanel().add(oLeftToolBarManager, BorderLayout.WEST);

			oRightToolBarManager = new UIToolBarController(this, UIToolBarController.RIGHT, false);
			oParent.getMainPanel().add(oRightToolBarManager, BorderLayout.EAST);

			onResetToolBars();
		}

		if (oMainToolBar != null)
			oMainToolBar.getToolBar().addKeyListener(oParent);

		if (oNodeToolBar != null)
			oNodeToolBar.getToolBar().addKeyListener(oParent);

		if (oTagsToolBar != null)
			oTagsToolBar.getToolBar().addKeyListener(oParent);

		if (oZoomToolBar != null)
			oZoomToolBar.getToolBar().addKeyListener(oParent);

		if (oScribbleToolBar != null)
			oScribbleToolBar.getToolBar().addKeyListener(oParent);

		if (oDataToolBar != null)
			oDataToolBar.getToolBar().addKeyListener(oParent);

		//if (tbrMeetingToolBar != null)
		//	tbrMeetingToolBar.getToolBar().addKeyListener(oParent);
		
		if (oFormatToolBar != null) {
			oFormatToolBar.getToolBar().addKeyListener(oParent);		
			oFormatToolBar.getToolBar().setEnabled(false);
		}
		if (oNodeFormatToolBar != null) {
			oNodeFormatToolBar.getToolBar().addKeyListener(oParent);		
			oNodeFormatToolBar.getToolBar().setEnabled(false);
		}
		if (oLinkFormatToolBar != null) {
			oLinkFormatToolBar.getToolBar().addKeyListener(oParent);		
			oLinkFormatToolBar.getToolBar().setEnabled(false);
		}
	}

	/**
	 * Store the interface mode, true for simple, false for advanced 
	 * and call <code>onResetToolBars</code> to redraw the correct default toolbar layout for the mode.
	 * @param isSimple
	 */
	public void setIsSimple(boolean isSimple) {
		bSimpleInterface = isSimple;
		onResetToolBars();
	}
	
	/**
	 * Reset the toolbar layouts to their default positions.
	 */
	public void onResetToolBars() {

		// CLEAR FILE		
		try {
			FileWriter fileWriter = new FileWriter("System"+ProjectCompendium.sFS+"resources"+ProjectCompendium.sFS+"toolbars.xml");
			fileWriter.write("");
			fileWriter.close();
		}
		catch (IOException e) {
			ProjectCompendium.APP.displayError("Exception:" + e.getMessage());
		}

		disposeFloatingToolBars();
		
		oTopToolBarManager.clear();
		oBottomToolBarManager.clear();
		oLeftToolBarManager.clear();
		oRightToolBarManager.clear();
	
		oTopToolBarManager.setVisible(false);
		oBottomToolBarManager.setVisible(false);
		oLeftToolBarManager.setVisible(false);
		oRightToolBarManager.setVisible(false);
		
		oMainToolBar = new UIToolBarMain(this, oParent, MAIN_TOOLBAR, bSimpleInterface);
		oTagsToolBar = new UIToolBarTags(this, oParent, TAGS_TOOLBAR);
		oNodeToolBar = new UIToolBarNode(this, oParent, NODE_TOOLBAR);
		oZoomToolBar = new UIToolBarZoom(this, oParent, ZOOM_TOOLBAR);
		oFormatToolBar = new UIToolBarFormat(this, oParent, FORMAT_TOOLBAR);
		oNodeFormatToolBar = new UIToolBarFormatNode(this, oParent, NODE_FORMAT_TOOLBAR);
		oLinkFormatToolBar = new UIToolBarFormatLink(this, oParent, LINK_FORMAT_TOOLBAR);
		oScribbleToolBar = new UIToolBarScribble(this, oParent, DRAW_TOOLBAR);
		oDataToolBar = new UIToolBarData(this, oParent, DATA_TOOLBAR);
		//oMeetingToolBar = new UIToolBarFormat(this, oParent, MEETING_TOOLBAR);
		
		oTopToolBarManager.addToolBar( oMainToolBar.getToolBar(), MAIN_TOOLBAR, true, true, oMainToolBar.getDefaultActiveState(), 0);		
		oTopToolBarManager.addToolBar( oZoomToolBar.getToolBar(), ZOOM_TOOLBAR, true, true, oZoomToolBar.getDefaultActiveState(), 0);
		oTopToolBarManager.addToolBar( oTagsToolBar.getToolBar(), TAGS_TOOLBAR, true, true, oTagsToolBar.getDefaultActiveState(), 0);		
		oLeftToolBarManager.addToolBar( oNodeToolBar.getToolBar(), NODE_TOOLBAR, true, true, oNodeToolBar.getDefaultActiveState(), 0);

		try {
			oTopToolBarManager.addToolBar( oFormatToolBar.getToolBar(), FORMAT_TOOLBAR, true, true, oFormatToolBar.getDefaultActiveState(), 1);			
			oTopToolBarManager.addToolBar( oNodeFormatToolBar.getToolBar(), NODE_FORMAT_TOOLBAR, true, true, oNodeFormatToolBar.getDefaultActiveState(), 1);			
			oTopToolBarManager.addToolBar( oLinkFormatToolBar.getToolBar(), LINK_FORMAT_TOOLBAR, true, true, oLinkFormatToolBar.getDefaultActiveState(), 1);			
		} catch (Exception e) {
			e.printStackTrace();
			System.out.flush();
		}	
		
		oBottomToolBarManager.addToolBar( oScribbleToolBar.getToolBar(), DRAW_TOOLBAR, true, true, oScribbleToolBar.getDefaultActiveState(), 0);		
		oBottomToolBarManager.addToolBar( oDataToolBar.getToolBar(), DATA_TOOLBAR, true, true, oDataToolBar.getDefaultActiveState(), 0);
		updateToolbarMenu(DRAW_TOOLBAR, oScribbleToolBar.getDefaultActiveState());
		updateToolbarMenu(DATA_TOOLBAR, oDataToolBar.getDefaultActiveState());			
		
		updateToolbarMenu(MAIN_TOOLBAR, oMainToolBar.getDefaultActiveState());
		updateToolbarMenu(NODE_TOOLBAR, oNodeToolBar.getDefaultActiveState());
		updateToolbarMenu(TAGS_TOOLBAR, oTagsToolBar.getDefaultActiveState());
		updateToolbarMenu(ZOOM_TOOLBAR, oZoomToolBar.getDefaultActiveState());		
		updateToolbarMenu(FORMAT_TOOLBAR, oFormatToolBar.getDefaultActiveState());		
		updateToolbarMenu(NODE_FORMAT_TOOLBAR, oNodeFormatToolBar.getDefaultActiveState());		
		updateToolbarMenu(LINK_FORMAT_TOOLBAR, oLinkFormatToolBar.getDefaultActiveState());		
		//updateToolbarMenu(oMeetingToolBar.getDefaultActiveState());		
				
		oTagsToolBar.updateCodeChoiceBoxData();
		
		oParent.validate();
		oParent.repaint();
	}

	/**
	 * Create any individual floating toolbars.
	 * @param floaters, the XML node with the saved information for which toolbars were floating.
	 */
	private void createFloatingToolBars(NodeList floaters) {

		// SIMPLE INTERFACE DOES NOT HAVE FLOATING TOOLBARS.
		if (floaters != null)  {
			
			int jcount = floaters.getLength();
			NamedNodeMap attrs = null;

			for (int j=0; j<jcount; j++) {

				Node controller = floaters.item(j);
				attrs = controller.getAttributes();

				Attr oType = (Attr)attrs.getNamedItem("type");
				int nType = new Integer(oType.getValue()).intValue();

				//Attr oVisible = (Attr)attrs.getNamedItem("isVisible");
				//boolean isVisible = new Boolean(oVisible.getValue()).booleanValue();

				//Attr oWasVisible = (Attr)attrs.getNamedItem("wasVisible");
				//boolean wasVisible = new Boolean(oWasVisible.getValue()).booleanValue();

				Attr oXPos = (Attr)attrs.getNamedItem("xPos");
				int nXPos = new Integer(oXPos.getValue()).intValue();

				Attr oYPos = (Attr)attrs.getNamedItem("yPos");
				int nYPos = new Integer(oYPos.getValue()).intValue();

				Attr oRow = (Attr)attrs.getNamedItem("row");
				int row = 0;
				if (oRow != null) {
					row = new Integer(oRow.getValue()).intValue();
				}
				
				UIToolBar bar = createToolBar(nType);				
				updateToolbarMenu(nType, true);			
				if (bar != null) {
					UIToolBarFloater oFloater = new UIToolBarFloater(this, bar, nType, row);
					oFloater.setLocation(nXPos, nYPos);
				}
			}
		}
	}

	/**
	 * Create a toolbar controller, and load any toolbars that it should be displaying.
	 */
	private void loadToolBars(Node node, boolean isClosed, int nPosition) {

		switch(nPosition) {
			case (UIToolBarController.TOP): {
				oTopToolBarManager = new UIToolBarController(this, nPosition, isClosed);		
				oParent.getMainPanel().add(oTopToolBarManager, BorderLayout.NORTH);
				break;
			}
			case (UIToolBarController.BOTTOM): {
				oBottomToolBarManager = new UIToolBarController(this, nPosition, isClosed);		
				oParent.getMainPanel().add(oBottomToolBarManager, BorderLayout.SOUTH);
				break;
			}
			case (UIToolBarController.LEFT): {
				oLeftToolBarManager = new UIToolBarController(this, nPosition, isClosed);		
				oParent.getMainPanel().add(oLeftToolBarManager, BorderLayout.WEST);
				break;
			}
			case (UIToolBarController.RIGHT): {
				oRightToolBarManager = new UIToolBarController(this, nPosition, isClosed);		
				oParent.getMainPanel().add(oRightToolBarManager, BorderLayout.EAST);
				break;
			}
		}
		
		if (node.hasChildNodes()) {

			Node top = XMLReader.getFirstChildWithTagName(node, "toolbars");
			if (top != null) {
				Vector toolbars = XMLReader.getChildrenWithTagName(top, "toolbar");
				NamedNodeMap attrs = null;

				int count = toolbars.size();
				for (int i=0; i< count; i++) {
					Node toolbar = (Node)toolbars.elementAt(i);
					attrs = toolbar.getAttributes();

					Attr oType = (Attr)attrs.getNamedItem("type");
					int nType = new Integer(oType.getValue()).intValue();

					Attr oRow = (Attr)attrs.getNamedItem("row");
					int row = 0;
					if (oRow != null) {
						row = new Integer(oRow.getValue()).intValue();
					}
					
					Attr oVisible = (Attr)attrs.getNamedItem("isVisible");
					boolean isVisible = new Boolean(oVisible.getValue()).booleanValue();

					Attr oWasVisible = (Attr)attrs.getNamedItem("wasVisible");
					boolean wasVisible = new Boolean(oWasVisible.getValue()).booleanValue();

					Attr oSwitchedOn = (Attr)attrs.getNamedItem("isOn");
					boolean bIsOn = true;
					if (oSwitchedOn != null) {
						bIsOn = new Boolean(oSwitchedOn.getValue()).booleanValue();
					}

					switch(nPosition) {
						case (UIToolBarController.TOP): {					
							UIToolBar oToolbar = createToolBar(nType, SwingConstants.HORIZONTAL);							
							if (oToolbar != null) {
								oTopToolBarManager.addToolBar(oToolbar, nType, isVisible, wasVisible, bIsOn, row);
								updateToolbarMenu(nType, bIsOn);
							}
							break;
						}
						case (UIToolBarController.BOTTOM): {
							UIToolBar oToolbar = createToolBar(nType, SwingConstants.HORIZONTAL);
							if (oToolbar != null) {
								oBottomToolBarManager.addToolBar(oToolbar, nType, isVisible, wasVisible, bIsOn, row);
								updateToolbarMenu(nType, bIsOn);
							}
							break;
						}
						case (UIToolBarController.LEFT): {
							UIToolBar oToolbar = createToolBar(nType, SwingConstants.VERTICAL);
							if (oToolbar != null) {
								oLeftToolBarManager.addToolBar(oToolbar, nType, isVisible, wasVisible, bIsOn, row);
								updateToolbarMenu(nType, bIsOn);
							}							
							break;
						}
						case (UIToolBarController.RIGHT): {
							UIToolBar oToolBar = createToolBar(nType, SwingConstants.VERTICAL);
							if (oToolBar != null) {
								oRightToolBarManager.addToolBar(oToolBar, nType, isVisible, wasVisible, bIsOn, row);
								updateToolbarMenu(nType, bIsOn);
							}
							break;
						}							
					}
				}
			}
		}
	}

	/**
	 * Return the help broker 
	 * @return the help broker
	 */
	public HelpBroker getHelpBroker() {
		return mainHB;
	}

	/**
	 * Creates and return the toolbar for the given type for its default orientation.
	 *
	 * @param nType the type of the toolbar to create.
	 * @return UIToolBar the toolbar for the given type, or null if type unknown or the bar already exists
	 */
	private UIToolBar createToolBar(int nType) {
		return createToolBar(nType, -1);
	}
	
	/**
	 * Creates and return the toolbar for the given type.
	 *
	 * @param nType the type of the toolbar to create.
	 * @param nOrientation the orientation to create the toolbar for. -1 means use the bar's default orientation.
	 * @return UIToolBar the toolbar for the given type, or null if type unknown or the bar already exists
	 */
	private UIToolBar createToolBar(int nType, int nOrientation) {

		if (nType == MAIN_TOOLBAR) {
			if (oMainToolBar == null) {
				if (nOrientation == -1) {
					oMainToolBar = new UIToolBarMain(this, oParent, nType, bSimpleInterface);
				} else {
					oMainToolBar = new UIToolBarMain(this, oParent, nType, nOrientation, bSimpleInterface);
				}
				return oMainToolBar.getToolBar();
			} else {
				return null;
			}
		}
		else if (nType == TAGS_TOOLBAR) {
			if (oTagsToolBar == null) {
				if (nOrientation == -1) {
					oTagsToolBar = new UIToolBarTags(this, oParent, nType);
				} else {
					oTagsToolBar = new UIToolBarTags(this, oParent, nType, nOrientation);
				}
				return oTagsToolBar.getToolBar();
			} else {
				return null;
			}
		}
		else if (nType == NODE_TOOLBAR) {
			if (oNodeToolBar == null) {
				if (nOrientation == -1) {
					oNodeToolBar = new UIToolBarNode(this, oParent, nType);
				} else {
					oNodeToolBar = new UIToolBarNode(this, oParent, nType, nOrientation);
				}
				return oNodeToolBar.getToolBar();
			} else {
				return null;
			}
		}
		else if (nType == ZOOM_TOOLBAR) {
			if (oZoomToolBar == null) {
				if (nOrientation == -1) {
					oZoomToolBar = new UIToolBarZoom(this, oParent, nType);					
				} else {
					oZoomToolBar = new UIToolBarZoom(this, oParent, nType, nOrientation);
				}
				return oZoomToolBar.getToolBar();
			} else {
				return null;
			}
		}
		else if (nType == DRAW_TOOLBAR) {
			if (oScribbleToolBar == null) {
				if (nOrientation == -1) {
					oScribbleToolBar = new UIToolBarScribble(this, oParent, nType);					
				} else {
					oScribbleToolBar = new UIToolBarScribble(this, oParent, nType, nOrientation);
				}
				return oScribbleToolBar.getToolBar();
			} else {
				return null;			
			}
		}
		else if (nType == DATA_TOOLBAR) {
			if (oDataToolBar == null) {
				if (nOrientation == -1) {
					oDataToolBar = new UIToolBarData(this, oParent, nType);					
				} else {
					oDataToolBar = new UIToolBarData(this, oParent, nType, nOrientation);
				}
				return oDataToolBar.getToolBar();
			} else {
				return null;
			}
		}
		else if (nType == FORMAT_TOOLBAR) {
			if (oFormatToolBar == null) {
				oFormatToolBar = new UIToolBarFormat(this, oParent, nType);				
				return oFormatToolBar.getToolBar();
			} else {
				return null;
			}
		}
		else if (nType == NODE_FORMAT_TOOLBAR) {
			if (oNodeFormatToolBar == null) {
				oNodeFormatToolBar = new UIToolBarFormatNode(this, oParent, nType);				
				return oNodeFormatToolBar.getToolBar();
			} else {
				return null;
			}
		}
		else if (nType == LINK_FORMAT_TOOLBAR) {
			if (oLinkFormatToolBar == null) {
				oLinkFormatToolBar = new UIToolBarFormatLink(this, oParent, nType);				
				return oLinkFormatToolBar.getToolBar();
			} else {
				return null;
			}
		}
		else if (nType == MEETING_TOOLBAR) {
			if (oDataToolBar == null) {
				if (nOrientation == -1) {
					oMeetingToolBar = new UIToolBarMeeting(this, oParent, nType);					
				} else {
					oMeetingToolBar = new UIToolBarMeeting(this, oParent, nType, nOrientation);
				}
				return oDataToolBar.getToolBar();
			} else {
				return null;
			}
		}		

		return null;
	}

	/**
	 * Update the toolbar menu as to whether a given toolbar is on or not.
	 *
	 * @param nType, the type of the toolbar.
	 * @param bIsOn, if the toolbar is switched on or not.
	 */
	private void updateToolbarMenu(int nType, boolean bIsOn) {
		UIMenuManager oMenuManager = oParent.getMenuManager();
		oMenuManager.setToolbar(nType, bIsOn);
	}

	/**
	 * Hide/Show the given toolbar depending on the given selected state.
	 *
	 * @param boolean switchOn, whether to switch the given toolbar type on or off.
	 * @param int, the toolbar to switch On/Off.
	 */
	public void toggleToolBar(boolean switchOn, int nToolbarType) {

		UIToolBar oToolBar = null;

		switch(nToolbarType) {
			case(MAIN_TOOLBAR) :
				if (oMainToolBar != null) {
					oToolBar = oMainToolBar.getToolBar();
				}
				break;
			case(NODE_TOOLBAR) :
				if (oNodeToolBar != null) {
					oToolBar = oNodeToolBar.getToolBar();
				}
				break;
			case(TAGS_TOOLBAR) :
				if (oTagsToolBar != null ) {
					oToolBar = oTagsToolBar.getToolBar();
				}
				break;
			case(ZOOM_TOOLBAR) :
				if (oZoomToolBar != null) {
					oToolBar = oZoomToolBar.getToolBar();
				}
				break;
			case(DRAW_TOOLBAR) :
				if (oScribbleToolBar != null) {
					oToolBar = oScribbleToolBar.getToolBar();
				}
				break;
			case(DATA_TOOLBAR) :
				if (oDataToolBar != null) {
					oToolBar = oDataToolBar.getToolBar();
				}
				break;
			case(FORMAT_TOOLBAR) :
				if (oFormatToolBar != null) {
					oToolBar = oFormatToolBar.getToolBar();
				}
				break;
			case(NODE_FORMAT_TOOLBAR) :
				if (oNodeFormatToolBar != null) {
					oToolBar = oNodeFormatToolBar.getToolBar();
				}
				break;
			case(LINK_FORMAT_TOOLBAR) :
				if (oLinkFormatToolBar != null) {
					oToolBar = oLinkFormatToolBar.getToolBar();
				}
				break;
			case(MEETING_TOOLBAR) :
				if (oMeetingToolBar != null) {
					oToolBar = oMeetingToolBar.getToolBar();
				}
				break;
		}

		if (oToolBar == null) {
			oToolBar = createToolBar(nToolbarType, SwingConstants.HORIZONTAL);
		}

		if (oToolBar != null) {
			if (!oTopToolBarManager.toggleToolBar(oToolBar, switchOn)) {
				if (!oBottomToolBarManager.toggleToolBar(oToolBar, switchOn)) {
					if (!oLeftToolBarManager.toggleToolBar(oToolBar, switchOn)) {
						if (!oRightToolBarManager.toggleToolBar(oToolBar, switchOn)) {

							// SHOULD NEVER GET HERE!
							if (nToolbarType == MAIN_TOOLBAR || nToolbarType == NODE_TOOLBAR ||
									nToolbarType == TAGS_TOOLBAR || nToolbarType == ZOOM_TOOLBAR) {

								oTopToolBarManager.addToolBar(oToolBar, nToolbarType, true, true, switchOn, 0);
							} else if (nToolbarType == FORMAT_TOOLBAR) {
								oTopToolBarManager.addToolBar(oToolBar, nToolbarType, true, true, switchOn, 1);								
							} else {
								oTopToolBarManager.addToolBar(oToolBar, nToolbarType, true, true, switchOn, 0);
							}
						}
					}
				}
			}
		}
	}

	/**
	 * Updates the menu when a new database project is opened.
	 */
	public void onDatabaseOpen() {

		if (oMainToolBar != null) {
			oMainToolBar.onDatabaseOpen();
		}
		if (oNodeToolBar != null) {
			oNodeToolBar.onDatabaseOpen();
		}
		if (oZoomToolBar != null) {
			oZoomToolBar.onDatabaseOpen();
		}
		if (oFormatToolBar != null) {
			oFormatToolBar.onDatabaseOpen();
		}		
		if (oNodeFormatToolBar != null) {
			oNodeFormatToolBar.onDatabaseOpen();
		}		
		if (oLinkFormatToolBar != null) {
			oLinkFormatToolBar.onDatabaseOpen();
		}		
		if (oDataToolBar != null) {
			oDataToolBar.onDatabaseOpen();
		}		
		if (oTagsToolBar != null) {
			oTagsToolBar.onDatabaseOpen();
		}
		if (oMeetingToolBar != null) {
			oMeetingToolBar.onDatabaseOpen();
		}

		//Note: DRAW TOOLBAR TURNED ON BY CODE ELSEWHERE
	}

	/**
	 * Updates the menu when the current database project is closed.
	 */
	public void onDatabaseClose() {

		if (oMainToolBar != null) {
			oMainToolBar.onDatabaseClose();
		}		
		if (oFormatToolBar != null) {
			oFormatToolBar.onDatabaseClose();
		}		
		if (oNodeFormatToolBar != null) {
			oNodeFormatToolBar.onDatabaseClose();
		}		
		if (oNodeFormatToolBar != null) {
			oNodeFormatToolBar.onDatabaseClose();
		}		
		if (oNodeToolBar != null) {
			oNodeToolBar.onDatabaseClose();
		}
		if (oZoomToolBar != null) {
			oZoomToolBar.onDatabaseClose();
		}
		if (oScribbleToolBar != null) {
			oScribbleToolBar.onDatabaseClose();
		}				
		if (oDataToolBar != null) {
			oDataToolBar.onDatabaseClose();
		}		
		if (oTagsToolBar != null) {
			oTagsToolBar.onDatabaseClose();
		}		
		if (oMeetingToolBar != null) {
		 	oMeetingToolBar.onDatabaseClose();
		}
	}

	/**
 	 * Enable the codes toobar icon.
 	 * @param selected, true to enalbe, false to disable.
	 */
	public void setNodeSelected(boolean selected) {
		if (oMainToolBar != null) {
			oMainToolBar.setNodeSelected(selected);
		}
		if (oFormatToolBar != null) {
			oFormatToolBar.setNodeSelected(selected);
		}		
		if (oNodeFormatToolBar != null) {
			oNodeFormatToolBar.setNodeSelected(selected);
		}		
		if (oLinkFormatToolBar != null) {
			oLinkFormatToolBar.setNodeSelected(selected);
		}		
		if (oNodeToolBar != null) {
			oNodeToolBar.setNodeSelected(selected);
		}
		if (oZoomToolBar != null) {
			oZoomToolBar.setNodeSelected(selected);
		}
		if (oScribbleToolBar != null) {
			oScribbleToolBar.setNodeSelected(selected);
		}		
		if (oDataToolBar != null) {
			oDataToolBar.setNodeSelected(selected);
		}	
		if (oTagsToolBar != null) {
			oTagsToolBar.setNodeSelected(selected);
		}										
		if (oMeetingToolBar != null) {
			oMeetingToolBar.setNodeSelected(selected);
		}										
	}

	/**
 	 * Set the enabled status of cut cop and delete.
 	 * @param selected, true to enable, false to disable.
	 */
	public void setNodeOrLinkSelected(boolean selected) {

		if (oMainToolBar != null) {
			oMainToolBar.setNodeOrLinkSelected(selected);
		}
		if (oFormatToolBar != null) {
			oFormatToolBar.setNodeOrLinkSelected(selected);
		}		
		if (oNodeFormatToolBar != null) {
			oNodeFormatToolBar.setNodeOrLinkSelected(selected);
		}		
		if (oLinkFormatToolBar != null) {
			oLinkFormatToolBar.setNodeOrLinkSelected(selected);
		}		
		if (oNodeToolBar != null) {
			oNodeToolBar.setNodeOrLinkSelected(selected);
		}
		if (oZoomToolBar != null) {
			oZoomToolBar.setNodeOrLinkSelected(selected);
		}
		if (oScribbleToolBar != null) {
			oScribbleToolBar.setNodeOrLinkSelected(selected);
		}	
		if (oDataToolBar != null) {
			oDataToolBar.setNodeOrLinkSelected(selected);
		}		
		if (oTagsToolBar != null) {
			oTagsToolBar.setNodeOrLinkSelected(selected);
		}										
		if (oMeetingToolBar != null) {
			oMeetingToolBar.setNodeOrLinkSelected(selected);
		}								
	}	
	
////Formatting Toolbar redirects
	/**
	 * Update the node label font style on the currently selected nodes by 
	 * adding / removing the given style depending on existing style.
	 */	
	public void addFontStyle(int nStyle) {
		if (oFormatToolBar != null) {
			oFormatToolBar.addFontStyle(nStyle);
		}
	}
	
//// Meeting toolbar redirects.
	
	/**
	 * Enable/disable the meeting toolbar.
	 * @param enabled, true to enable, false to disable.
	 */
	public void setMeetingToolBarEnabled(boolean enabled) {
		if (oMeetingToolBar != null) {
			oMeetingToolBar.setEnabled(enabled);
		}
	}

//// Tags toolbar redirect
	/**
	 * Update the current tags list when a change occurs.
	 */
	public void updateCodeChoiceBoxData() {
		if (oTagsToolBar != null) {
			oTagsToolBar.updateCodeChoiceBoxData();
		}

	}	
	
//// Data toolbar redirects

	/**
	 * Update the data in the profiles choicebox.
	 */
	public synchronized void updateProfilesChoiceBoxData(int selectedIndex) {
		if (oDataToolBar != null) {
			oDataToolBar.updateProfilesChoiceBoxData(selectedIndex);
		}
	}
	/** 
	 * Disable the Refresh button
	 */
	public void  disableDataRefresh() {
		oDataToolBar.disableRefresh();
	}
	
	/** 
	 * Enable the Refresh button
	 */	
	public void enableDataRefresh() {
		oDataToolBar.enableRefresh();
	}

	/**
	 * Select the indicated database profile.
	 * An empty string means, select the Derby default option.
	 */
	public synchronized void selectProfile(String sName) {
		if (oDataToolBar != null) {
			oDataToolBar.selectProfile(sName);
		}
	}	
	
//// Node toolbar redirects
	
	/**
	 * Used to change the Node toolbar icons when a different skin has been chosen.
	 */
	public void swapToobarSkin() {

		if (oNodeToolBar != null) {
			oNodeToolBar.swapToobarSkin();
		}
	}	
	
//// Scribble toolbar redirects

	/**
	 * Is scribble pad on.
	 * @return boolean, true if on, else false.
 	 */
	public boolean isScribblePadOn() {
		if (oScribbleToolBar != null) {
			return oScribbleToolBar.isScribblePadOn();
		}		
		return false;
	}	

	/**
	 * Enable/disable the draw toolbar.
	 * @param enabled, true to enable, false to disable.
	 */
	public void setDrawToolBarEnabled(boolean enabled) {
		if (oScribbleToolBar != null) {
			oScribbleToolBar.setEnabled(enabled);
		}		
	}

	/**
	 * Enable/disable the drawing toolbar items.
	 * @param enabled, true to enable, false to disable.
	 */
	public void setDrawToolBarPaintEnabled(boolean enabled) {
		if (oScribbleToolBar != null) {
			oScribbleToolBar.setDrawToolBarPaintEnabled(enabled);
		}
	}
	
	/**
	 * Toggle scribble pad on and off.
 	 */
	public void onToggleScribble() {
		if (oScribbleToolBar != null) {
			oScribbleToolBar.onToggleScribble();
		}
	}

	/**
	 * Position scribble pad at the bacl or front of node layer.
 	 */
	public void onPositionScribble() {
		if (oScribbleToolBar != null) {
			oScribbleToolBar.onPositionScribble();
		}
	}
	
	
//// Zoom toolbar redirects	
	
	public int getTextZoom() {
		int zoom = 0;
		if (oZoomToolBar != null) {
			zoom = oZoomToolBar.getTextZoom();
		} 		
		return zoom;
	}

	/**
	 * Enable/disable the zoom toolbar.
	 * @param enabled, true to enable, false to disable.
	 */
	public void setZoomToolBarEnabled(boolean enabled) {
		if (oZoomToolBar != null) {
			oZoomToolBar.setEnabled(enabled);
		}
	}
	
	/**
	 * Zoom the current map to the next level up(75/50/25/full).
	 */
	public void onZoomOut() {
		if (oZoomToolBar != null) {
			oZoomToolBar.onZoomOut();
		}
	}

	/**
	 * Zoom the current map to the next level down(75/50/25/full).
	 */
	public void onZoomIn() {
		if (oZoomToolBar != null) {
			oZoomToolBar.onZoomIn();
		}
	}

	/**
	 * Reset the zoom choicebox to reflect the current views settings.
	 */
	public void resetZoom() {
		
		if (oZoomToolBar != null) {
			oZoomToolBar.resetZoom();
		}
	}

	/**
	 * Zoom the current map using the given scale.
	 * @param scale, the scale to zoom to.
	 */
	public void onZoomTo(double scale) {
		if (oZoomToolBar != null) {
			oZoomToolBar.onZoomTo(scale);
		}
	}

	/**
	 * Zoom the current map to fit it all on the visible view.
	 */
	public void onZoomToFit() {
		if (oZoomToolBar != null) {
			oZoomToolBar.onZoomToFit();
		}
	}

	/**
	 * Zoom the current map back to normal and focus on the last selected node.
	 */
	public boolean onZoomRefocused() {
		if (oZoomToolBar != null) {
			return oZoomToolBar.onZoomRefocused();
		}
		return false;
	}	
	
//// Main toolbar redirects
	
	/**
	 * Refreshes the undo/redo icons with the last action performed.
	 * @param oUndoManager, the manager to use to check for undo/redo possibilities.
	 */
	public void refreshUndoRedo(UndoManager oUndoManager) {
		if (oMainToolBar != null) {
			oMainToolBar.refreshUndoRedo(oUndoManager);
		}
	}
	
	/**
	 * Enable all the view history related toolbar icons.
	 */
	public void enableHistoryButtons() {
		if (oMainToolBar != null) {
			oMainToolBar.enableHistoryButtons();
		}
	}

	/**
	 * Clear the view history.
	 */
	public void clearHistory() {
		if (oMainToolBar != null) {
			oMainToolBar.clearHistory();
		}				
	}

	/**
	 * Try to remove a View object from the history.
	 * @param view com.compendium.core.datamodel.View, the view to add to the history.
	 */
	public void removeFromHistory(View view) {
		if (oMainToolBar != null) {
			oMainToolBar.removeFromHistory(view);
		}				
	}

	/**
	 * Try to add a View object to the history.
	 * @param view com.compendium.core.datamodel.View, the view to add to the history.
	 */
	public void addToHistory(View view) {
		if (oMainToolBar != null) {
			oMainToolBar.addToHistory(view);
		}						
	}
	
	/**
	 * Enable/disable paste button.
	 * @param enabled, true to enalbe, false to disable.
	 */
	public void setPasteEnabled(boolean enabled) {
		if (oMainToolBar != null) {
			oMainToolBar.setPasteEnabled(enabled);
		}
	}

	/**
	 * Enable/disable file open button.
	 * @param enabled, true to enable, false to disable.
	 */
	public void setFileOpenEnablement(boolean enabled) {
		if (oMainToolBar != null) {
			oMainToolBar.setFileOpenEnablement(enabled);
		}
	}

	/**
	 * Reset the image rollover icon as enabled/disabled.
	 * @param enabled, the status to draw the image rollover icon for.
	 */
	public void updateImageRollover(boolean enabled) {
		if (oMainToolBar != null) {
			oMainToolBar.updateImageRollover(enabled);
		}
	}
	

//// IUIToolBarManager Methods

	/**
	 * Returns the JFrame in which is to be the parent of the floating toolbars.
	 *
	 * @return JFrame which represents the parent of the floating toolbars.
	 */
	public JFrame getToolBarFloatFrame() {
		return oParent;
	}

	/**
	 * Returns the toolbar controller for the top edge of the container.
	 *
	 * @return com.compendium.ui.toolbars.UIToolBarController object for the top edge of the container.
	 */
	public UIToolBarController getTopToolBarController() {
		return oTopToolBarManager;
	}

	/**
	 * Returns the toolbar controller for the bottom edge of the container.
	 *
	 * @return com.compendium.ui.toolbars.UIToolBarController object for the bottom edge of the container.
	 */
	public UIToolBarController getBottomToolBarController() {
		return oBottomToolBarManager;
	}

	/**
	 * Returns the toolbar controller for the left edge of the container.
	 *
	 * @return com.compendium.ui.toolbars.UIToolBarController object for the left hand side of the container.
	 */
	public UIToolBarController getLeftToolBarController() {
		return oLeftToolBarManager;
	}

	/**
	 * Returns the toolbar controller for the right edge of the container.
	 *
	 * @return com.compendium.ui.toolbars.UIToolBarController object for the right hand side of the container.
	 */
	public UIToolBarController getRightToolBarController() {
		return oRightToolBarManager;
	}

	private Vector floaters = new Vector(10);

	/**
	 * Tell the manager that a toolbar is now floating by passing the UIToolBarFloater object to it.
	 * @param floater com.compendium.ui.toolbars.UIToolBarFloater, the floating toolbar to add.
	 */
	public void addFloatingToolBar(UIToolBarFloater floater) {
		floaters.addElement(floater);
	}

	/**
	 * Tell the manager that a toolbar is now docked by removing the UIToolBarFloater object from it.
	 * @param floater com.compendium.ui.toolbars.UIToolBarFloater, the floating toolbar to remove.
	 */
	public void removeFloatingToolBar(UIToolBarFloater floater) {
		floaters.removeElement(floater);
	}

	/**
	 * Dispose of all toolbars currently floating.
	 */
	public void disposeFloatingToolBars() {
		int count = floaters.size();
		for (int i=0; i< count; i++) {
			UIToolBarFloater floater = (UIToolBarFloater)floaters.elementAt(i);
			floater.setVisible(false);
			floater.dispose();
		}
		floaters.removeAllElements();
	}

	/**
	 * Create instances of UIToolBarFloater, for all toolbars tha should be floating.
	 */
	public void showFloatingToolBars() {
		int count = floaters.size();
		for (int i=0; i< count; i++) {
			UIToolBarFloater floater = (UIToolBarFloater)floaters.elementAt(i);
			floater.setVisible(true);
		}
	}

	/**
	 * Returns a xml string containing the toolbar data from the toolbar controllers
	 * The <code>UIToolBarController</code> class has a <code>toXML</code> method that can be used to get this information.
	 *
	 * @return a String object containing formatted xml representation of the toolbar data
	 */
	public String getToolbarXML() {

		StringBuffer data = new StringBuffer(600);

		data.append("<?xml version=\"1.0\"?>\n");
		data.append("<!DOCTYPE toolbarcontrollers [\n");
		data.append("<!ELEMENT toolbarcontrollers (#PCDATA | controller | floaters)*>\n");
		data.append("<!ELEMENT controller (#PCDATA | toolbars)*>\n");
		data.append("<!ATTLIST controller\n");
		data.append("position CDATA #REQUIRED\n");
		data.append("isClosed CDATA #REQUIRED\n>\n");
		data.append("<!ELEMENT toolbars (#PCDATA | toolbar)*>\n");
		data.append("<!ELEMENT toolbar (#PCDATA)>\n");
		data.append("<!ATTLIST toolbar\n");
		data.append("type CDATA #REQUIRED\n");
		data.append("name CDATA #REQUIRED\n");
		data.append("isVisible CDATA #REQUIRED\n");
		data.append("wasVisible CDATA #REQUIRED\n");
		data.append("isOn CDATA #REQUIRED\n");
		data.append("row CDATA #REQUIRED\n");		
		data.append(">\n");
		data.append("<!ELEMENT floaters (#PCDATA | floater)*>\n");
		data.append("<!ELEMENT floater (#PCDATA)>\n");
		data.append("<!ATTLIST floater\n");
		data.append("name CDATA #REQUIRED\n");
		data.append("isVisible CDATA #REQUIRED\n");
		data.append("wasVisible CDATA #REQUIRED\n");
		data.append("xPos CDATA #REQUIRED\n");
		data.append("yPos CDATA #REQUIRED\n");
		data.append("row CDATA #REQUIRED\n");				
		data.append(">\n]>\n\n");

		data.append("<toolbarcontrollers>\n");
		data.append(getRightToolBarController().toXML());
		data.append(getLeftToolBarController().toXML());
		data.append(getTopToolBarController().toXML());
		data.append(getBottomToolBarController().toXML());

		// ADD FLOATERS
		data.append("<floaters>");
		int count = floaters.size();
		if (count > 0)
			data.append("\n");

		for (int i=0; i< count; i++) {
			UIToolBarFloater floater = (UIToolBarFloater)floaters.elementAt(i);
			data.append(floater.toXML());
		}
		data.append("</floaters>\n");

		data.append("</toolbarcontrollers>\n");

		return data.toString();
	}

	/**
	 * Save all the current toolbar setting to an xml file.
	 */
	public void saveToolBarData() {
		String toolbardata = getToolbarXML();
		try {
			FileWriter fileWriter = new FileWriter("System"+ProjectCompendium.sFS+"resources"+ProjectCompendium.sFS+"toolbars.xml");
			fileWriter.write(toolbardata);
			fileWriter.close();
		}
		catch (IOException e) {
			oParent.displayError("Exception:" + e.getMessage());
		}
	}
}
