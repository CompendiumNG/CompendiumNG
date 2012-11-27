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

import java.sql.SQLException;
import java.util.Date;
import java.util.Vector;

import javax.swing.JOptionPane;

import com.compendium.LanguageProperties;
import com.compendium.ProjectCompendium;
import com.compendium.core.datamodel.Code;
import com.compendium.core.datamodel.IModel;

/**
 * this class hoolds data for the tag tree nodes.
 * @author Michelle Bachler
 *
 */
public class CheckNode {
	
	Vector vtGroup = null;
	private Code code = null;
	private boolean isGroup = false;
	
	
	private boolean checked = false;
	
	/** Is the associated tag found on all selected nodes?.*/
	private boolean universal = false;
	
	public CheckNode(Vector data) {
		isGroup = true;
		this.vtGroup = data;
	}
	
	public CheckNode(Code code) {
		this.code = code;
	}
	
	public Object getData() {
		if (isGroup) {
			return vtGroup;
		} else {
			return code;
		}
	}
	
	public boolean isGroup () {
		return isGroup;
	}
	
	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}
	
	public boolean isUniversal() {
		return universal;
	}

	public void setUniversal(boolean uni) {
		this.universal = uni;
	}
	
	public void setText(String sText) {
		IModel model = ProjectCompendium.APP.getModel();
		if (sText.length() > 50) {
			ProjectCompendium.APP.displayError(LanguageProperties.getString(LanguageProperties.TAGS_BUNDLE, "CheckNode.error50chars")+".\n"); //$NON-NLS-1$
			return;
		}		
		
		if (isGroup) {
			String sOldName = (String)vtGroup.elementAt(1);
			if (!sText.equals("") && !sOldName.equals(sText)) {				 //$NON-NLS-1$
				try {	
					vtGroup.setElementAt(sText, 1);
					String sUserID = model.getUserProfile().getId();
					String sCodeGroupID = (String)vtGroup.elementAt(0);
					// UPDATE DATABASE
					(model.getCodeGroupService()).setName(model.getSession(), sCodeGroupID, sText, new Date(), sUserID);
	
					// UPDATE MODEL
					model.replaceCodeGroupName(sCodeGroupID, sText);
					
				} catch( SQLException ex) {
					ProjectCompendium.APP.displayError("UITagTreeGroupPopupMenu.editGroupName\n\n"+ex.getMessage()); //$NON-NLS-1$
				}	
			}
		} else {
			String sOldName = code.getName();
	   		String sNewName = sText;
			sNewName = sNewName.trim();
			if (!sNewName.equals("") && !sOldName.equals(sText)) {				 //$NON-NLS-1$
				try {				
					String sCodeID = code.getId();

					//CHECK NAME DOES NOT ALREADY EXIST
					if (model.codeNameExists(sCodeID, sNewName)) {
						ProjectCompendium.APP.displayMessage("\n\n"+LanguageProperties.getString(LanguageProperties.TAGS_BUNDLE, "CheckNode.errortagexists")+sNewName+LanguageProperties.getString(LanguageProperties.TAGS_BUNDLE, "CheckNode.pleasetryagain")+"\n\n", LanguageProperties.getString(LanguageProperties.TAGS_BUNDLE, "CheckNode.tagmaintenance")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					}
					else {
						code.initialize(model.getSession(), model);
						code.setName(sNewName); // Updates Database and model as model holds same object.
					}
					
				} catch( Exception ex) {
					ProjectCompendium.APP.displayError("UITagTreeLeafPopupMenu.editTagName\n\n"+ex.getMessage()); //$NON-NLS-1$
				}	
			}
		}
	}
	
	public String getText() {
		String sText = ""; //$NON-NLS-1$
		if (isGroup) {
			sText = (String)vtGroup.elementAt(1);
		} else {
			sText = code.getName();
		}
		return sText;
	}
}