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
import javax.help.*;
import javax.swing.*;

import com.compendium.LanguageProperties;
import com.compendium.core.*;
import com.compendium.ui.*;
import com.compendium.ui.toolbars.system.*;

/**
 * This class manages all the toolbars
 *
 * @author	Michelle Bachler
 * @version	1.0
 */
public class UIToolBarNode implements IUIToolBar, IUIConstants {
	
	/** Indicates whether the node format toolbar is switched on or not by default.*/
	private final static boolean DEFAULT_STATE			= true;
	
	/** Indicates the default orientation for this toolbars ui object.*/
	private final static int DEFAULT_ORIENTATION		= SwingConstants.VERTICAL;	

	/** This indicates the type of the toolbar.*/
	private	int 					nType			= -1;			
	
	/** The parent frame for this class.*/
	private ProjectCompendiumFrame	oParent			= null;
	
	/** The overall toolbar manager.*/
	private IUIToolBarManager 		oManager		= null;

	/** The toolbar for the node createion buttons.*/
	private UIToolBar				tbrToolBar 		= null;

	
	/**
	 * Create a new instance of UIToolBarNode, with the given properties.
	 * @param oManager the IUIToolBarManager that is managing this toolbar.
	 * @param parent the parent frame for the application.
	 * @param nType the unique identifier for this toolbar.
	 */
	public UIToolBarNode(IUIToolBarManager oManager, ProjectCompendiumFrame parent, int nType) {

		this.oParent = parent;
		this.oManager = oManager;
		this.nType = nType;
		
		createToolBar(DEFAULT_ORIENTATION);		
	}

	/**
	 * Create a new instance of UIToolBarNode, with the given properties.
	 * @param oManager the IUIToolBarManager that is managing this toolbar.
	 * @param parent the parent frame for the application.
	 * @param nType the unique identifier for this toolbar.
	 * @param orientation the orientation of this toolbars ui object.  
	 */
	public UIToolBarNode(IUIToolBarManager oManager, ProjectCompendiumFrame parent, int nType, int orientation) {

		this.oParent = parent;
		this.oManager = oManager;
		this.nType = nType;
		
		createToolBar(orientation);		
	}

	/**
	 * Update the look and feel of all the toolbars
	 */
	public void updateLAF() {

		if (tbrToolBar != null) {
			SwingUtilities.updateComponentTreeUI(tbrToolBar);
		}
	}

	/**
	 * Creates and initializes the node creation tool bar
	 * @return UIToolBar, the toolbar with all the node type options.
	 */
	private UIToolBar createToolBar(int orientation) {

		tbrToolBar = new UIToolBar(LanguageProperties.getString(LanguageProperties.TOOLBARS_BUNDLE, "UIToolBarNode.nodeCreationToolbar")); //$NON-NLS-1$
		tbrToolBar.setOrientation(orientation);

		CSH.setHelpIDString(tbrToolBar,"toolbars.node"); //$NON-NLS-1$

		DraggableToolBarIcon pbIssue = tbrToolBar.createDraggableToolBarButton(ICoreConstants.ISSUE, UINodeTypeManager.getNodeTypeDescription(ICoreConstants.ISSUE), UIImages.getNodeIcon(IUIConstants.ISSUE_SM_ICON));
		tbrToolBar.add(pbIssue);

		DraggableToolBarIcon pbAnswer = tbrToolBar.createDraggableToolBarButton(ICoreConstants.POSITION, UINodeTypeManager.getNodeTypeDescription(ICoreConstants.POSITION), UIImages.getNodeIcon(IUIConstants.POSITION_SM_ICON));
		tbrToolBar.add(pbAnswer);

		tbrToolBar.addSeparator();

		DraggableToolBarIcon pbMap = tbrToolBar.createDraggableToolBarButton(ICoreConstants.MAPVIEW, UINodeTypeManager.getNodeTypeDescription(ICoreConstants.MAPVIEW), UIImages.getNodeIcon(IUIConstants.MAP_SM_ICON));
		tbrToolBar.add(pbMap);

		DraggableToolBarIcon pbTimeMap = tbrToolBar.createDraggableToolBarButton(ICoreConstants.MOVIEMAPVIEW, UINodeTypeManager.getNodeTypeDescription(ICoreConstants.MOVIEMAPVIEW), UIImages.getNodeIcon(IUIConstants.MOVIEMAP_SM_ICON));
		tbrToolBar.add(pbTimeMap);

		DraggableToolBarIcon pbList = tbrToolBar.createDraggableToolBarButton(ICoreConstants.LISTVIEW, UINodeTypeManager.getNodeTypeDescription(ICoreConstants.LISTVIEW), UIImages.getNodeIcon(IUIConstants.LIST_SM_ICON));
		tbrToolBar.add(pbList);

		tbrToolBar.addSeparator();

		DraggableToolBarIcon pbPro = tbrToolBar.createDraggableToolBarButton(ICoreConstants.PRO, UINodeTypeManager.getNodeTypeDescription(ICoreConstants.PRO), UIImages.getNodeIcon(IUIConstants.PRO_SM_ICON));
		tbrToolBar.add(pbPro);

		DraggableToolBarIcon pbCon = tbrToolBar.createDraggableToolBarButton(ICoreConstants.CON, UINodeTypeManager.getNodeTypeDescription(ICoreConstants.CON), UIImages.getNodeIcon(IUIConstants.CON_SM_ICON));
		tbrToolBar.add(pbCon);

		tbrToolBar.addSeparator();

		DraggableToolBarIcon pbReference = tbrToolBar.createDraggableToolBarButton(ICoreConstants.REFERENCE, UINodeTypeManager.getNodeTypeDescription(ICoreConstants.REFERENCE), UIImages.getNodeIcon(IUIConstants.REFERENCE_SM_ICON));
		tbrToolBar.add(pbReference);

		DraggableToolBarIcon pbNote = tbrToolBar.createDraggableToolBarButton(ICoreConstants.NOTE, UINodeTypeManager.getNodeTypeDescription(ICoreConstants.NOTE), UIImages.getNodeIcon(IUIConstants.NOTE_SM_ICON));
		tbrToolBar.add(pbNote);

		DraggableToolBarIcon pbDecision = tbrToolBar.createDraggableToolBarButton(ICoreConstants.DECISION, UINodeTypeManager.getNodeTypeDescription(ICoreConstants.DECISION), UIImages.getNodeIcon(IUIConstants.DECISION_SM_ICON));
		tbrToolBar.add(pbDecision);

		DraggableToolBarIcon pbArgument = tbrToolBar.createDraggableToolBarButton(ICoreConstants.ARGUMENT, UINodeTypeManager.getNodeTypeDescription(ICoreConstants.ARGUMENT), UIImages.getNodeIcon(IUIConstants.ARGUMENT_SM_ICON));
		tbrToolBar.add(pbArgument);

		return tbrToolBar;
	}

	/**
	 * Used to change the Node toolbar icons when a different skin has been chosen.
	 */
	public void swapToobarSkin() {

		if (tbrToolBar == null)
			return;

		int count = tbrToolBar.getComponentCount();

		for (int i=0; i<count; i++) {

			Component comp =  tbrToolBar.getComponentAtIndex(i);
			if (comp instanceof DraggableToolBarIcon) {
				DraggableToolBarIcon node = (DraggableToolBarIcon) comp;

				int type = -1;
				try { type = new Integer(node.getIdentifier()).intValue(); }
				catch(Exception ex){}

				switch(type) {
					case ICoreConstants.ISSUE:
						node.setIcon( UIImages.getNodeIcon(IUIConstants.ISSUE_SM_ICON) );
					break;
					case ICoreConstants.POSITION:
						node.setIcon( UIImages.getNodeIcon(IUIConstants.POSITION_SM_ICON) );
					break;
					case ICoreConstants.MAPVIEW:
						node.setIcon( UIImages.getNodeIcon(IUIConstants.MAP_SM_ICON) );
					break;
					case ICoreConstants.LISTVIEW:
						node.setIcon( UIImages.getNodeIcon(IUIConstants.LIST_SM_ICON) );
					break;
					case ICoreConstants.PRO:
						node.setIcon( UIImages.getNodeIcon(IUIConstants.PRO_SM_ICON) );
					break;
					case ICoreConstants.CON:
						node.setIcon( UIImages.getNodeIcon(IUIConstants.CON_SM_ICON) );
					break;
					case ICoreConstants.REFERENCE:
						node.setIcon( UIImages.getNodeIcon(IUIConstants.REFERENCE_SM_ICON) );
					break;
					case ICoreConstants.NOTE:
						node.setIcon( UIImages.getNodeIcon(IUIConstants.NOTE_SM_ICON) );
					break;
					case ICoreConstants.DECISION:
						node.setIcon( UIImages.getNodeIcon(IUIConstants.DECISION_SM_ICON) );
					break;
					case ICoreConstants.ARGUMENT:
						node.setIcon( UIImages.getNodeIcon(IUIConstants.ARGUMENT_SM_ICON) );
					break;
					default:
					break;
				}
			}
		}
	}

	/**
	 * Updates the menu when a new database project is opened.
	 */
	public void onDatabaseOpen() {
		if (tbrToolBar != null) {
			tbrToolBar.setEnabled(true);
		}
	}

	/**
	 * Updates the menu when the current database project is closed.
	 */
	public void onDatabaseClose() {
		if (tbrToolBar != null) {
			tbrToolBar.setEnabled(false);
		}
	}
	
	/**
 	 * Does nothing
 	 * @param selected true to enable, false to disable.
	 */
	public void setNodeSelected(boolean selected) {}

	/**
 	 * Does nothing
 	 * @param selected true to enable, false to disable.
	 */
	public void setNodeOrLinkSelected(boolean selected) {}	
	
	/**
	 * Return the ui toolbar object.
	 */
	public UIToolBar getToolBar() {
		return tbrToolBar;
	}
	
	/**
	 * Enable/disable the toolbar.
	 * @param enabled true to enable, false to disable.
	 */
	public void setEnabled(boolean enabled) {
		tbrToolBar.setEnabled(enabled);
	}	
	
	/**
	 * Return true if this toolbar is active by default, or false if it must be switched on by the user.
	 * @return true if the toolbar is active by default, else false.
	 */
	public boolean getDefaultActiveState() {
		return DEFAULT_STATE;
	}	
	
	/**
	 * Return a unique integer identifier for this toolbar.
	 * @return a unique integer identifier for this toolbar.
	 */
	public int getType() {
		return nType;
	}	
}
