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

package com.compendium.ui.tags;

import java.awt.event.*;
import java.sql.SQLException;
import java.util.Date;

import javax.swing.*;

import com.compendium.LanguageProperties;
import com.compendium.ProjectCompendium;
import com.compendium.core.datamodel.Code;
import com.compendium.core.datamodel.IModel;
import com.compendium.core.datamodel.PCSession;
import com.compendium.meeting.MeetingEvent;
import com.compendium.ui.*;

/**
 * This draws a small popup menu for right-clicks on a UITextArea.
 *
 * @author	Michelle Bachler
 */
public class UITagTreeLeafPopupMenu extends JPopupMenu implements ActionListener {

	/** The menu option to show code usage.*/
	private JMenuItem		miMenuItemShow		= null;

	/** The menu option to edit code label.*/
	private JMenuItem		miMenuItemEdit		= null;

	/** The menu option to delete the passed node.*/
	private JMenuItem		miMenuItemDelete		= null;

	/** The menu option to remove the code from its current group.*/
	private JMenuItem		miMenuItemRemove		= null;

	/** The menu option to remove the code from selected nodes.*/
	private JMenuItem		miMenuItemRemoveTag		= null;

	/** The height of this popup.*/
	private int				nHeight				= 100;

	/** The width of this popup.*/
	private int				nWidth				= 100;

	/** The UITextArea associated with this popup.*/
	private UITagTreePanel oParent				= null;
	
	private Code			code				= null;
	
	private String			sGroupID			= ""; //$NON-NLS-1$

	/**
	 * Constructor. Draws the popupmenu.
	 * @param panel the parent panel for this popup.
	 */
	public UITagTreeLeafPopupMenu(UITagTreePanel panel, Code code, String sGroupID) {
		super(LanguageProperties.getString(LanguageProperties.TAGS_BUNDLE, "UITagTreeLeafPopupMenu.detailsoptions")); //$NON-NLS-1$

		this.sGroupID = sGroupID;
		
		this.oParent = panel;
		this.code = code;

		/*miMenuItemShow = new JMenuItem("Show Tag Usage");
		miMenuItemShow.setToolTipText("Show a list of nodes which have this tag assigned");
		miMenuItemShow.setMnemonic('S');
		miMenuItemShow.addActionListener(this);
		add(miMenuItemShow);*/

		miMenuItemEdit = new JMenuItem(LanguageProperties.getString(LanguageProperties.TAGS_BUNDLE, "UITagTreeLeafPopupMenu.edittagname")); //$NON-NLS-1$
		miMenuItemEdit.setToolTipText(LanguageProperties.getString(LanguageProperties.TAGS_BUNDLE, "UITagTreeLeafPopupMenu.edittagnameTip")); //$NON-NLS-1$
		miMenuItemEdit.setMnemonic((LanguageProperties.getString(LanguageProperties.TAGS_BUNDLE, "UITagTreeLeafPopupMenu.edittagnameMnemonic")).charAt(0)); //$NON-NLS-1$
		miMenuItemEdit.addActionListener(this);
		add(miMenuItemEdit);

		/*miMenuItemRemoveTag = new JMenuItem("Remove From Selected Nodes");
		miMenuItemRemoveTag.setToolTipText("Remove this tag from the selected node on the current view");
		miMenuItemRemoveTag.setMnemonic('R');
		miMenuItemRemoveTag.addActionListener(this);
		add(miMenuItemRemoveTag);*/
		
		if (!sGroupID.equals("")) { //$NON-NLS-1$
			miMenuItemRemove = new JMenuItem(LanguageProperties.getString(LanguageProperties.TAGS_BUNDLE, "UITagTreeLeafPopupMenu.removefromgroup")); //$NON-NLS-1$
			miMenuItemRemove.setToolTipText(LanguageProperties.getString(LanguageProperties.TAGS_BUNDLE, "UITagTreeLeafPopupMenu.removefromgroupTip")); //$NON-NLS-1$
			miMenuItemRemove.setMnemonic((LanguageProperties.getString(LanguageProperties.TAGS_BUNDLE, "UITagTreeLeafPopupMenu.removefromgrouptipMnemonic")).charAt(0)); //$NON-NLS-1$
			miMenuItemRemove.addActionListener(this);
			add(miMenuItemRemove);
		}		
		
		miMenuItemDelete = new JMenuItem(LanguageProperties.getString(LanguageProperties.TAGS_BUNDLE, "UITagTreeLeafPopupMenu.deletetag")); //$NON-NLS-1$
		miMenuItemDelete.setToolTipText(LanguageProperties.getString(LanguageProperties.TAGS_BUNDLE, "UITagTreeLeafPopupMenu.deletetagTip")); //$NON-NLS-1$
		miMenuItemDelete.setMnemonic((LanguageProperties.getString(LanguageProperties.TAGS_BUNDLE, "UITagTreeLeafPopupMenu.deletetagMnemonic")).charAt(0)); //$NON-NLS-1$
		miMenuItemDelete.addActionListener(this);
		add(miMenuItemDelete);

		pack();
		setSize(nWidth,nHeight);
	}
	
	/**
	 * Handles the event of an option being selected.
	 * @param evt, the event associated with the option being selected.
	 */
	public void actionPerformed(ActionEvent evt) {

		Object source = evt.getSource();

		if(source.equals(miMenuItemShow)) {
			UITagUsageDialog dlg = new UITagUsageDialog(code, oParent);
 			dlg.setVisible(true);	   		          			
		}
		else if(source.equals(miMenuItemEdit)) {
			IModel model = ProjectCompendium.APP.getModel();
			
			String sOldName = code.getName();
	   		String sNewName = JOptionPane.showInputDialog(LanguageProperties.getString(LanguageProperties.TAGS_BUNDLE, "UITagTreeLeafPopupMenu.edittagname"), sOldName); //$NON-NLS-1$
			sNewName = sNewName.trim();
			if (!sNewName.equals("")) {				 //$NON-NLS-1$
				try {				
					String sCodeID = code.getId();

					//CHECK NAME DOES NOT ALREADY EXIST
					if (model.codeNameExists(sCodeID, sNewName)) {
						ProjectCompendium.APP.displayMessage(LanguageProperties.getString(LanguageProperties.TAGS_BUNDLE, "UITagTreeLeafPopupMenu.tagexists")+"\n\n"+sNewName+LanguageProperties.getString(LanguageProperties.TAGS_BUNDLE, "UITagTreeLeafPopupMenu.pleasetryagain")+"\n\n", LanguageProperties.getString(LanguageProperties.TAGS_BUNDLE, "UITagTreeLeafPopupMenu.tagmaintenance")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					}
					else {
						code.initialize(model.getSession(), model);
						code.setName(sNewName); // Updates Database and model as model holds same object.
						oParent.updateTreeData();
					}
					
				} catch( Exception ex) {
					ProjectCompendium.APP.displayError("UITagTreeLeafPopupMenu.editTagName\n\n"+ex.getMessage()); //$NON-NLS-1$
				}	
			}			
		} else if (source.equals(miMenuItemDelete)) {
			oParent.onDeleteCode(code);
		} else if (source.equals(miMenuItemRemove)) {
			oParent.onRemoveCodeFromGroup(code, sGroupID);
		} else if (source.equals(miMenuItemRemoveTag)) {
			oParent.onRemoveCodeFromNodes(code);
		}
	}
		
	/**
	 * Handle the cancelling of this popup. Set is to invisible.
	 */
	public void onCancel() {
		setVisible(false);
	}
}
