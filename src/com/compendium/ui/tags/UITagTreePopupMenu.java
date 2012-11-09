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
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.*;

import com.compendium.ProjectCompendium;
import com.compendium.core.datamodel.*;
import com.compendium.ui.*;

/**
 * This draws a small popup menu for right-clicks on a UITextArea.
 *
 * @author	Michelle Bachler
 */
public class UITagTreePopupMenu extends JPopupMenu implements ActionListener {

	/** The menu option to show code usage.*/
	private JMenuItem		miMenuItemNewGroup		= null;

	/** The menu option to edit code label.*/
	private JMenuItem		miMenuItemNewTag		= null;

	/** The height of this popup.*/
	private int				nHeight				= 100;

	/** The width of this popup.*/
	private int				nWidth				= 100;

	/** The UITextArea associated with this popup.*/
	private UITagTreePanel oParent				= null;

	/**
	 * Constructor. Draws the popupmenu.
	 * @param panel the parent panel for this popup.
	 */
	public UITagTreePopupMenu(UITagTreePanel panel) {

		super("Tag Tree Main Menu");

		this.oParent = panel;

		miMenuItemNewGroup = new JMenuItem("Create New Group");
		miMenuItemNewGroup.setToolTipText("Create a new tag group");
		miMenuItemNewGroup.setMnemonic('G');
		miMenuItemNewGroup.addActionListener(this);
		add(miMenuItemNewGroup);

		miMenuItemNewTag = new JMenuItem("create New Tag");
		miMenuItemNewTag.setToolTipText("Create a new tag");
		miMenuItemNewTag.setMnemonic('T');
		miMenuItemNewTag.addActionListener(this);
		add(miMenuItemNewTag);

		pack();
		
		setSize(nWidth,nHeight);
	}

	/**
	 * Handles the event of an option being selected.
	 * @param evt, the event associated with the option being selected.
	 */
	public void actionPerformed(ActionEvent evt) {

		Object source = evt.getSource();

		if(source.equals(miMenuItemNewGroup)) {
			onAddGroup();
		}
		else if(source.equals(miMenuItemNewTag)) {
			onAddTag();
		}
	}
	
	/**
	 * Process the adding of a new code group.
	 */
	private void onAddTag() {	
 		
		IModel model = ProjectCompendium.APP.getModel();		
		PCSession session = ProjectCompendium.APP.getModel().getSession();
		String author = model.getUserProfile().getUserName();
		Date creationDate = new Date();
		Date modificationDate = creationDate;
		String description = "No Description";
		String behavior = "No Behavior";

		try {
			String sCodeID = model.getUniqueID();

	 		String sNewName = JOptionPane.showInputDialog("Enter the new tag name", "");
			sNewName = sNewName.trim();
			
			if (ProjectCompendium.APP.getModel().codeNameExists(sCodeID, sNewName)) {
				ProjectCompendium.APP.displayMessage("You already have a tag called "+sNewName+"\n\n", "Tag Maintenance");
			}
			else {
				//UPDATE DATABASE
				Code code = model.getCodeService().createCode(session, sCodeID, author, creationDate,
												 modificationDate, sNewName, description, behavior);
				// UPDATE MODEL
				model.addCode(code);
				oParent.updateTreeData();					
			}
		}
		catch(SQLException ex) {
			ProjectCompendium.APP.displayError("Exception: (UITagTreePopupMenu.onAddTag) " + ex.getMessage());
		}
	}	
	
	/**
	 * Process the adding of a new code group.
	 */
	private void onAddGroup() {	
   		String sNewName = JOptionPane.showInputDialog("Enter the new tag group name", "");
		sNewName = sNewName.trim();
		
		IModel model = ProjectCompendium.APP.getModel();
		try {
			String sCodeGroupID = model.getUniqueID();
			Date date = new Date();
			String sAuthor = model.getUserProfile().getUserName();

			//ADD NEW CODE TO DATABASE
			(model.getCodeGroupService()).createCodeGroup(model.getSession(), sCodeGroupID, sAuthor, sNewName, date, date);

			// UPDATE MODEL
			Vector group = new Vector(2);
			group.addElement(sCodeGroupID);
			group.addElement(sNewName);
			model.addCodeGroup(sCodeGroupID, group);
			
			oParent.updateTreeData();			
		}
		catch(Exception ex) {
			ProjectCompendium.APP.displayError("Exception: (UITagTreePopupMenu.onAddGroup) " + ex.getMessage());
		}
	}	
	
	/**
	 * Handle the cancelleing of this popup. Set is to invisible.
	 */
	public void onCancel() {
		setVisible(false);
	}
}
