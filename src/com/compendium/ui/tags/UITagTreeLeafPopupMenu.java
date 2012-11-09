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

package com.compendium.ui.tags;

import java.awt.event.*;
import java.sql.SQLException;
import java.util.Date;

import javax.swing.*;

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
	
	private String			sGroupID			= "";

	/**
	 * Constructor. Draws the popupmenu.
	 * @param panel the parent panel for this popup.
	 */
	public UITagTreeLeafPopupMenu(UITagTreePanel panel, Code code, String sGroupID) {
		super("Details options");

		this.sGroupID = sGroupID;
		
		this.oParent = panel;
		this.code = code;

		/*miMenuItemShow = new JMenuItem("Show Tag Usage");
		miMenuItemShow.setToolTipText("Show a list of nodes which have this tag assigned");
		miMenuItemShow.setMnemonic('S');
		miMenuItemShow.addActionListener(this);
		add(miMenuItemShow);*/

		miMenuItemEdit = new JMenuItem("Edit Tag Name");
		miMenuItemEdit.setToolTipText("Edit the tag name");
		miMenuItemEdit.setMnemonic('E');
		miMenuItemEdit.addActionListener(this);
		add(miMenuItemEdit);

		/*miMenuItemRemoveTag = new JMenuItem("Remove From Selected Nodes");
		miMenuItemRemoveTag.setToolTipText("Remove this tag from the selected node on the current view");
		miMenuItemRemoveTag.setMnemonic('R');
		miMenuItemRemoveTag.addActionListener(this);
		add(miMenuItemRemoveTag);*/
		
		if (!sGroupID.equals("")) {
			miMenuItemRemove = new JMenuItem("Remove Tag From Group");
			miMenuItemRemove.setToolTipText("Temove this tag from this group");
			miMenuItemRemove.setMnemonic('G');
			miMenuItemRemove.addActionListener(this);
			add(miMenuItemRemove);
		}		
		
		miMenuItemDelete = new JMenuItem("Delete Tag");
		miMenuItemDelete.setToolTipText("Delete this tag from the project");
		miMenuItemDelete.setMnemonic('D');
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
	   		String sNewName = JOptionPane.showInputDialog("Edit tag name", sOldName);
			sNewName = sNewName.trim();
			if (!sNewName.equals("")) {				
				try {				
					String sCodeID = code.getId();

					//CHECK NAME DOES NOT ALREADY EXIST
					if (model.codeNameExists(sCodeID, sNewName)) {
						ProjectCompendium.APP.displayMessage("You already have a tag called "+sNewName+"\n\nPlease try again\n\n", "Tag Maintenance");
					}
					else {
						code.initialize(model.getSession(), model);
						code.setName(sNewName); // Updates Database and model as model holds same object.
						oParent.updateTreeData();
					}
					
				} catch( Exception ex) {
					ProjectCompendium.APP.displayError("UITagTreeLeafPopupMenu.editTagName\n\n"+ex.getMessage());
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
	 * Handle the cancelleing of this popup. Set is to invisible.
	 */
	public void onCancel() {
		setVisible(false);
	}
}
