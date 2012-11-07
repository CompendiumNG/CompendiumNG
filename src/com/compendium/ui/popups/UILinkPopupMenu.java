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

package com.compendium.ui.popups;


import java.awt.event.*;
import java.sql.SQLException;
import java.util.*;

import javax.swing.*;

import com.compendium.LanguageProperties;
import com.compendium.ProjectCompendium;
import com.compendium.core.*;
import com.compendium.core.datamodel.LinkProperties;
import com.compendium.core.datamodel.Model;
import com.compendium.core.datamodel.services.ViewService;
import com.compendium.ui.*;
import com.compendium.ui.edits.*;
import com.compendium.ui.plaf.*;
import com.compendium.ui.linkgroups.*;

/**
 * This class draws and handles events for right-click popup menu on Links.
 *
 * @author	Mohammed Sajid Ali / Michelle Bachler
 */
public class UILinkPopupMenu extends JPopupMenu implements ActionListener{

	/** The width for this popup.*/
	private static final int WIDTH		= 100;

	/** The height for this popup.*/
	private static final int HEIGHT		= 300;

	/** The LinkUI associated with this popup.*/
	private LinkUI			oLink					= null;

	/** The view pane of the map this link is in.*/
	private UIViewPane		oViewPane				= null;

	/** The JMenuItem for the delete option.*/
	private JMenuItem		miMenuItemDelete		= null;

	/** The JMenuItem to show the properties tab of the link dialog.*/
	private JMenuItem		miMenuItemProperties	= null;

	/** The JMenuItem to show the contents tab of the link dialog.*/

	private JMenuItem 		miMenuItemContents		= null;

	/** The JMenu for changing the link type.*/
	private JMenu			mnuLinkTypes			= null;

	/** The JMenu for the link arrows option.*/
	private JMenu			mnuArrows			= null;

	/** The JMenuItem for the no arrows option.*/
	private JMenuItem		miMenuItemNoArrows	= null;

	/** The JMenuItem for painting the arrow at the To end of the link.*/
	private JMenuItem		miMenuItemToArrow	= null;

	/** The JMenuItem for painting the arrow at the From end of the link.*/
	private JMenuItem		miMenuItemFromArrow	= null;

	/** The JMenuItem for painting arrows at both ends.*/
	private JMenuItem		miMenuItemBothArrows= null;

	/** The JMenu for the link type label option.*/
	private JMenuItem		miTypes			= null;

	/** The height if this popup.*/
	private int				nHeight				= HEIGHT;

	/** The width of this popup.*/
	private int				nWidth				= WIDTH;

	/** The user Id of the current user */
	private String userID = ""; //$NON-NLS-1$
	
	/**
	 * Constructor. Creates the menuitems for this popup.
	 * @param title the title for this popup.
	 * @param linkui com.compendium.ui.plaf.LinkUI, the link associated with this popup.
	 */
	public UILinkPopupMenu(String title, LinkUI linkui, String userID) {
		super(title);
		
		this.userID = userID;
		miMenuItemContents = new JMenuItem(LanguageProperties.getString(LanguageProperties.POPUPS_BUNDLE, "UILinkPopupMenu.contents")); //$NON-NLS-1$
		miMenuItemContents.addActionListener(this);
		add(miMenuItemContents);

		addSeparator();

		mnuLinkTypes = new JMenu(LanguageProperties.getString(LanguageProperties.POPUPS_BUNDLE, "UILinkPopupMenu.changeTypeTo")); //$NON-NLS-1$
		add(mnuLinkTypes);

		String sActiveGroupID = ProjectCompendium.APP.getActiveLinkGroup();
		UILinkGroup group = ProjectCompendium.APP.oLinkGroupManager.getLinkGroup(sActiveGroupID);
		if (group == null) {
			JMenuItem item = new JMenuItem(UILink.sRESPONDSTOLINK);
			item.setForeground(UILink.getLinkColor(new Integer(ICoreConstants.RESPONDS_TO_LINK).toString()));
			item.addActionListener(this);
			mnuLinkTypes.add(item);

			item = new JMenuItem(UILink.sSUPPORTSLINK);
			item.setForeground(UILink.getLinkColor(new Integer(ICoreConstants.SUPPORTS_LINK).toString()));
			item.addActionListener(this);
			mnuLinkTypes.add(item);

			item = new JMenuItem(UILink.sOBJECTSTOLINK);
			item.setForeground(UILink.getLinkColor(new Integer(ICoreConstants.OBJECTS_TO_LINK).toString()));
			item.addActionListener(this);
			mnuLinkTypes.add(item);

			item = new JMenuItem(UILink.sCHALLENGESLINK);
			item.setForeground(UILink.getLinkColor(new Integer(ICoreConstants.CHALLENGES_LINK).toString()));
			item.addActionListener(this);
			mnuLinkTypes.add(item);

			item = new JMenuItem(UILink.sSPECIALIZESLINK);
			item.setForeground(UILink.getLinkColor(new Integer(ICoreConstants.SPECIALIZES_LINK).toString()));
			item.addActionListener(this);
			mnuLinkTypes.add(item);

			item = new JMenuItem(UILink.sEXPANDSONLINK);
			item.setForeground(UILink.getLinkColor(new Integer(ICoreConstants.EXPANDS_ON_LINK).toString()));
			item.addActionListener(this);
			mnuLinkTypes.add(item);

			item = new JMenuItem(UILink.sRELATEDTOLINK);
			item.setForeground(UILink.getLinkColor(new Integer(ICoreConstants.RELATED_TO_LINK).toString()));
			item.addActionListener(this);
			mnuLinkTypes.add(item);

			item = new JMenuItem(UILink.sABOUTLINK);
			item.setForeground(UILink.getLinkColor(new Integer(ICoreConstants.ABOUT_LINK).toString()));
			item.addActionListener(this);
			mnuLinkTypes.add(item);

			item = new JMenuItem(UILink.sRESOLVESLINK);
			item.setForeground(UILink.getLinkColor(new Integer(ICoreConstants.RESOLVES_LINK).toString()));
			item.addActionListener(this);
			mnuLinkTypes.add(item);
		}
		else {
			Vector items = group.getItems();
			int count = items.size();
			for (int i=0; i<count; i++) {
				final UILinkType type = (UILinkType)items.elementAt(i);

				JMenuItem item = new JMenuItem(type.getName());
				item.setForeground(type.getColour());
				item.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent evt) {
						setLinkType(type.getID(), true, type.getLabel());
					}
				});
				mnuLinkTypes.add(item);
			}
		}

		mnuArrows = new JMenu(LanguageProperties.getString(LanguageProperties.POPUPS_BUNDLE, "UILinkPopupMenu.changeArrowsTo")); //$NON-NLS-1$
		add(mnuArrows);

		miMenuItemToArrow = new JMenuItem(LanguageProperties.getString(LanguageProperties.POPUPS_BUNDLE, "UILinkPopupMenu.fromTo")); //$NON-NLS-1$
		miMenuItemToArrow.addActionListener(this);
		mnuArrows.add(miMenuItemToArrow);

		miMenuItemFromArrow = new JMenuItem(LanguageProperties.getString(LanguageProperties.POPUPS_BUNDLE, "UILinkPopupMenu.toFrom")); //$NON-NLS-1$
		miMenuItemFromArrow.addActionListener(this);
		mnuArrows.add(miMenuItemFromArrow);

		miMenuItemBothArrows = new JMenuItem(LanguageProperties.getString(LanguageProperties.POPUPS_BUNDLE, "UILinkPopupMenu.bothWays")); //$NON-NLS-1$
		miMenuItemBothArrows.addActionListener(this);
		mnuArrows.add(miMenuItemBothArrows);

		miMenuItemNoArrows = new JMenuItem(LanguageProperties.getString(LanguageProperties.POPUPS_BUNDLE, "UILinkPopupMenu.noArrows")); //$NON-NLS-1$
		miMenuItemNoArrows.addActionListener(this);
		mnuArrows.add(miMenuItemNoArrows);

		addSeparator();

		miTypes = new JMenuItem(LanguageProperties.getString(LanguageProperties.POPUPS_BUNDLE, "UILinkPopupMenu.labelWithType")); //$NON-NLS-1$
		miTypes.addActionListener(this);
		add(miTypes);

		addSeparator();

		miMenuItemDelete = new JMenuItem(LanguageProperties.getString(LanguageProperties.POPUPS_BUNDLE, "UILinkPopupMenu.delete")); //$NON-NLS-1$
		miMenuItemDelete.setAccelerator(KeyStroke.getKeyStroke( KeyEvent.VK_DELETE, 0));
		miMenuItemDelete.addActionListener(this);
		add(miMenuItemDelete);
		addSeparator();

		miMenuItemProperties = new JMenuItem(LanguageProperties.getString(LanguageProperties.POPUPS_BUNDLE, "UILinkPopupMenu.properties")); //$NON-NLS-1$
		miMenuItemProperties.addActionListener(this);
		add(miMenuItemProperties);

		setLink(linkui);

		/**
		 * If on the Mac OS and the Menu bar is at the top of the OS screen, remove the menu shortcut Mnemonics.
		 */
		if (ProjectCompendium.isMac && (FormatProperties.macMenuBar || (!FormatProperties.macMenuBar && !FormatProperties.macMenuUnderline)) )
			UIUtilities.removeMenuMnemonics(getSubElements());

		pack();
		setSize(nWidth,nHeight);
	}

	/**
	 * Set the UIViewPane of the link associated with this popup menu.
	 * @param viewPane com.compendium.ui.UIViewPane, the pane of the link associated with this popup menu.
	 */
	public void setViewPane(UIViewPane viewPane) {
		oViewPane = viewPane;
	}

	/**
	 * Sets the associated link for this popup menu.
	 * @param link com.compendium.ui.plaf.LinkUI, the link associated with this popup menu.
	 */
	public void setLink(LinkUI link) {
		oLink = link;
	}

	/**
	 * Handles the event of an option being selected.
	 * @param evt, the event associated with the option being selected.
	 */
	public void actionPerformed(ActionEvent evt) {
		Object source = evt.getSource();

		if(source.equals(miMenuItemDelete)) {

			// CHECK IF LINK SELECTED ELSE WILL NEED TO DELETE SEPARATELY BELOW.
			if(oViewPane.getNumberOfSelectedLinks() > 1) {
				DeleteEdit edit = new DeleteEdit(oViewPane.getViewFrame());
				oViewPane.deleteSelectedNodesAndLinks(edit);
				oViewPane.getViewFrame().getUndoListener().postEdit(edit);
				ProjectCompendium.APP.refreshNodeIconIndicators(oViewPane.getView().getId());
			}
			else {
				DeleteEdit edit = new DeleteEdit(oViewPane.getViewFrame());
				UILink uilink = oLink.getUILink();
				oLink.deleteLink(uilink);
				edit.AddLinkToEdit (uilink);
				oViewPane.getViewFrame().getUndoListener().postEdit(edit);
				ProjectCompendium.APP.refreshNodeIconIndicators(oViewPane.getView().getId());
			}

			oViewPane.setSelectedLink(null, ICoreConstants.DESELECTALL);
			//Thread thread = new Thread() {
			//	public void run() {
					ProjectCompendium.APP.setTrashBinIcon();
			//	}
			//};
			//thread.start();
		}
		else if(source.equals(miMenuItemProperties)) {
			(oLink.getUILink()).showPropertiesDialog();
		}
		else if(source.equals(miMenuItemContents)) {
			(oLink.getUILink()).showEditDialog();
		}
		else if(source.equals(miTypes)) {
			if(oViewPane.getNumberOfSelectedLinks() > 1) {
				UILink link = null;
				for (Enumeration links = oViewPane.getSelectedLinks(); links.hasMoreElements(); ) {
					link = (UILink)links.nextElement();
					link.setText( UILink.getLinkTypeLabel(link.getLinkType()) );
				}
			}
			else {
				(oLink.getUILink()).setText( UILink.getLinkTypeLabel(oLink.getUILink().getLinkType()) );
			}

			oViewPane.setSelectedLink(null, ICoreConstants.DESELECTALL);
		}
		else if (source.equals(miMenuItemToArrow)) {
			setArrow(ICoreConstants.ARROW_TO);
		}
		else if (source.equals(miMenuItemFromArrow)) {
			setArrow(ICoreConstants.ARROW_FROM);
		}
		else if (source.equals(miMenuItemBothArrows)) {
			setArrow(ICoreConstants.ARROW_TO_AND_FROM);
		}
		else if (source.equals(miMenuItemNoArrows)) {
			setArrow(ICoreConstants.NO_ARROW);
		}
		else {
			JMenuItem item = (JMenuItem)source;
			String sText = item.getText();

			if (sText.equals(UILink.sRESPONDSTOLINK) || sText.equals(UILink.sRESOLVESLINK)
				|| sText.equals(UILink.sSUPPORTSLINK) || sText.equals(UILink.sOBJECTSTOLINK)
				|| sText.equals(UILink.sCHALLENGESLINK) || sText.equals(UILink.sSPECIALIZESLINK)
				|| sText.equals(UILink.sEXPANDSONLINK) || sText.equals(UILink.sRELATEDTOLINK)
				|| sText.equals(UILink.sABOUTLINK) ) {

				String sLinkType = UILink.getLinkType(sText);
				setLinkType(sLinkType, false, null);
			}
		}
	}

	/**
	 * Set the arrow head of the selected links to the given type.
	 *
	 * @param nArrowType the arrow head type to set the links to.
	 */
	private void setArrow(int nArrowType) {
		Vector vtUpdateLinks = new Vector();								
		Model oModel = (Model)ProjectCompendium.APP.getModel();
		UILink link = null;
		LinkProperties props = null;
		for (Enumeration e = oViewPane.getSelectedLinks(); e.hasMoreElements();) {
			link = (UILink)e.nextElement();					
			props = link.getLinkProperties();
			if (nArrowType != props.getArrowType()) {
				vtUpdateLinks.addElement(props);
			}
		}				
		if (vtUpdateLinks.size() > 0) {				
			try {
				((ViewService)oModel.getViewService()).setArrowType(oModel.getSession(), oViewPane.getView().getId(), vtUpdateLinks, nArrowType);
				int count = vtUpdateLinks.size();
				for (int i=0; i<count;i++) {
					props = (LinkProperties)vtUpdateLinks.elementAt(i);
					props.setArrowType(nArrowType);
				}
			} catch (SQLException ex) {
				ProjectCompendium.APP.displayError(LanguageProperties.getString(LanguageProperties.TOOLBARS_BUNDLE, "UIToolBarFormatLink.unableUpdateArrowType")+":\n\n"+ex.getMessage()); //$NON-NLS-1$
			}
		}
	}					

	/**
	 * Set the type of the selected links to the given type.
	 *
	 * @param sLinkType, the type to set the links to.
	 * @param bWithLabel, whether to set the link label too.
	 * @param sLabel, the label to add, or null.
	 */
	private void setLinkType(String sLinkType, boolean bWithLabel, String sLabel) {
		if(oViewPane.getNumberOfSelectedLinks() > 1) {
			UILink link = null;
			for (Enumeration links = oViewPane.getSelectedLinks(); links.hasMoreElements(); ) {
				link = (UILink)links.nextElement();

				if (bWithLabel) {
					UILinkType oldType = ProjectCompendium.APP.oLinkGroupManager.getLinkType(link.getLinkType());
					if (oldType != null && (oldType.getLabel()).equals(link.getText()) )
						link.setText(sLabel);
					else if (oldType == null)
						link.setText(sLabel);
				}

				link.setLinkType(sLinkType);
			}
		}
		else {
			UILink link = oLink.getUILink();
			if (bWithLabel) {
				UILinkType oldType = ProjectCompendium.APP.oLinkGroupManager.getLinkType(link.getLinkType());
				if (oldType != null && (oldType.getLabel()).equals(link.getText()) )
					link.setText(sLabel);
				else if (oldType == null)
					link.setText(sLabel);
			}
			oLink.getUILink().setLinkType(sLinkType);
		}

		oViewPane.setSelectedLink(null, ICoreConstants.DESELECTALL);
	}

	/**
	 * Handle the cancelling of this popup. Set is to invisible.
	 */
	public void onCancel() {
		setVisible(false);
	}
}
