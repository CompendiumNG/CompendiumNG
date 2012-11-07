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
import java.awt.event.*;
import java.util.*;

import javax.help.*;
import javax.swing.*;

import com.compendium.LanguageProperties;
import com.compendium.core.*;
import com.compendium.ui.*;
import com.compendium.ui.toolbars.system.*;
import com.compendium.core.datamodel.*;


/**
 * This class manages all the toolbars
 *
 * @author	Michelle Bachler
 * @version	1.0
 */
public class UIToolBarTags implements IUIToolBar, ActionListener, IUIConstants {

	/** Indicates whether the node format toolbar is switched on or not by default.*/
	private final static boolean DEFAULT_STATE			= true;
	
	/** Indicates the default orientation for this toolbars ui object.*/
	private final static int DEFAULT_ORIENTATION		= SwingConstants.HORIZONTAL;	
		
	/** This indicates the type of the toolbar.*/
	private	int 					nType			= -1;
	
	/** The parent frame for this class.*/
	private ProjectCompendiumFrame	oParent			= null;
	
	/** The overall toolbar manager.*/
	private IUIToolBarManager 		oManager		= null;

	/** The toolbar for the tags options.*/
	private UIToolBar			tbrToolBar		= null;

	/** The active tags choice box.*/
	private UINavComboBox 		cbCodes				= null;

	/** The tags toobar button.*/
	private JButton				pbCodes				= null;

	/** The panel for the active tags chioce box.*/
	private JPanel 				comboPanel 			= null;

	/** The action listener for the active tags choice box.*/
	private ActionListener 		comboActionListener = null;	
	
	
	/**
	 * Create a new instance of UIToolBarTags, with the given properties.
	 * @param oManager the IUIToolBarManager that is managing this toolbar.
	 * @param parent the parent frame for the application.
	 * @param nType the unique identifier for this toolbar.
	 */
	public UIToolBarTags(IUIToolBarManager oManager, ProjectCompendiumFrame parent, int nType) {

		this.oParent = parent;
		this.oManager = oManager;
		this.nType = nType;
		createToolBar(DEFAULT_ORIENTATION);		
	}
	
	/**
	 * Create a new instance of UIToolBarTags, with the given properties.
	 * @param oManager the IUIToolBarManager that is managing this toolbar.
	 * @param parent the parent frame for the application.
	 * @param nType the unique identifier for this toolbar.
	 * @param orientation the orientation of this toolbars ui object. 
	 */
	public UIToolBarTags(IUIToolBarManager oManager, ProjectCompendiumFrame parent, int nType, int orientation) {

		this.oParent = parent;
		this.oManager = oManager;
		this.nType = nType;
		createToolBar(orientation);		
	}

	/**
	 * Update the look and feel of the toolbar.
	 */
	public void updateLAF() {
	    pbCodes.setIcon(UIImages.get(CODES_ICON));
		
		if (tbrToolBar != null) {
			SwingUtilities.updateComponentTreeUI(tbrToolBar);
		}
	}

	/**
	 * Create and return the toolbar with all the tag options.
	 * @return UIToolBar, the toolbar with all the tag options.
	 */
	private UIToolBar createToolBar(int orientation) {

		tbrToolBar = new UIToolBar(LanguageProperties.getString(LanguageProperties.TOOLBARS_BUNDLE, "UIToolBarTags.tagsToolbar"), UIToolBar.NORTHSOUTH); //$NON-NLS-1$
		tbrToolBar.setOrientation(orientation);

		pbCodes = tbrToolBar.createToolBarButton(LanguageProperties.getString(LanguageProperties.TOOLBARS_BUNDLE, "UIToolBarTags.tags"), UIImages.get(CODES_ICON)); //$NON-NLS-1$
		pbCodes.addActionListener(this);
		pbCodes.setEnabled(true);
		tbrToolBar.add(pbCodes);
		CSH.setHelpIDString(pbCodes,"toolbars.main"); //$NON-NLS-1$

		tbrToolBar.addSeparator();

		tbrToolBar.add( createCodeChoiceBox() );
		CSH.setHelpIDString(tbrToolBar,"toolbars.main"); //$NON-NLS-1$

		return tbrToolBar;
	}

	/**
	 * Create a new choicbox for tags and return the panel it is in.
	 * @return JPanel, the panel holding the new choicebox for the tags.
	 */
	private JPanel createCodeChoiceBox() {

		comboPanel = new JPanel(new BorderLayout());
		CSH.setHelpIDString(comboPanel,"toolbars.main"); //$NON-NLS-1$

		cbCodes = new UINavComboBox();
		cbCodes.setOpaque(true);
		cbCodes.setEditable(false);
		cbCodes.setEnabled(false);
		cbCodes.setMaximumRowCount(30);
		cbCodes.setFont( new Font("Dialog", Font.PLAIN, 10 )); //$NON-NLS-1$

		DefaultListCellRenderer comboRenderer = new DefaultListCellRenderer() {
			public Component getListCellRendererComponent(
   		     	JList list,
   		        Object value,
            	int modelIndex,
            	boolean isSelected,
            	boolean cellHasFocus)
            {
			    if (list != null) {
	 		 		if (isSelected) {
						setBackground(list.getSelectionBackground());
						setForeground(list.getSelectionForeground());
					}
					else {
						setBackground(list.getBackground());
						setForeground(list.getForeground());
					}
			    }

			    if (value instanceof Code) {
					Code code = (Code)value;
					setText(code.getName());
			    }
			    else if (value instanceof String)
					setText((String) value);

			    return this;
			}
		};

		cbCodes.setRenderer(comboRenderer);

		comboActionListener = new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
				if (cbCodes.getSelectedItem() instanceof Code) {
               		Code code = (Code)cbCodes.getSelectedItem();
					if (code != null)
						oParent.addCode(code);

					cbCodes.setSelectedIndex(0);
				}
          	}
		};
        cbCodes.addActionListener(comboActionListener);

		comboPanel.add(cbCodes, BorderLayout.WEST);
		return comboPanel;
	}

	/**
	 * Update the current tags list when a change occurs.
	 */
	public void updateCodeChoiceBoxData() {
		
		// IF THIS IS CALLED BY TOOLBAR MANAGER WHEN STARTING COMP APP 
		// MODEL IS NULL.
		if (oParent == null || oParent.getModel() == null) {
			return;
		}

		Vector vtCodesSort = new Vector();

		String label = LanguageProperties.getString(LanguageProperties.TOOLBARS_BUNDLE, "UIToolBarTags.selectTag"); //$NON-NLS-1$

		String activeGroup = oParent.getActiveCodeGroup();
		if (activeGroup.equals("") || activeGroup.equals("0")) { //$NON-NLS-1$ //$NON-NLS-2$
			for(Enumeration e = oParent.getModel().getCodes(); e.hasMoreElements();) {
				Code code = (Code)e.nextElement();
				vtCodesSort.addElement(code);
			}
		}
		else {
			Hashtable group = oParent.getModel().getCodeGroup(activeGroup);
			Hashtable children = (Hashtable)group.get("children"); //$NON-NLS-1$
			for(Enumeration e = children.elements();e.hasMoreElements();) {
				Code code = (Code)e.nextElement();
				vtCodesSort.addElement(code);
			}
		}

		vtCodesSort = CoreUtilities.sortList(vtCodesSort);
		vtCodesSort.insertElementAt((Object) label, 0);

		DefaultComboBoxModel comboModel = new DefaultComboBoxModel(vtCodesSort);
		cbCodes.setModel(comboModel);

		cbCodes.setSelectedIndex(0);
	}

	/**
	 * Handles most menu and toolbar action event for this application.
	 *
	 * @param evt the genereated action event to be handled.
	 */
	public void actionPerformed(ActionEvent evt) {

		oParent.setWaitCursor();

		Object source = evt.getSource();
		if (source.equals(pbCodes)) {
			oParent.onCodes();
		}
		
		oParent.setDefaultCursor();
	}

	/**
	 * Updates the menu when a new database project is opened.
	 */
	public void onDatabaseOpen() {

		if (tbrToolBar != null) {
			tbrToolBar.setEnabled(true);
			if (pbCodes != null) {
				pbCodes.setEnabled(true);
			}
		}
	}

	/**
	 * Updates the menu when the current database project is closed.
	 */
	public void onDatabaseClose() {
		if (tbrToolBar != null) {
			if (pbCodes != null) {
				pbCodes.setEnabled(false);				
			}
			tbrToolBar.setEnabled(false);
		}
	}

	/**
 	 * Does Nothing
 	 * @param selected true to enable, false to disable.
	 */
	public void setNodeSelected(boolean selected) {
		cbCodes.setEnabled(selected);
	}
		
	/**
 	 * Does Nothing
 	 * @param selected true to enable, false to disable.
	 */
	public void setNodeOrLinkSelected(boolean selected) {}
	
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
