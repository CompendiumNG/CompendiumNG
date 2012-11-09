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

package com.compendium.ui;

import javax.swing.*;
import java.io.*;

import com.compendium.*;

/**
 * This class extends JFileChooser to allow the user to chooser whether to over write an existing file.
 * It also add am image preview pane.
 *
 * @author Michelle Bachler
 */
public class UIFileChooser extends JFileChooser {

	/** The required extension for this file chooser dialog.*/
	String sRequiredExtension = ""; //$NON-NLS-1$

	/**
	 * Constructor.
	 * @param extension, the required file extension to enforce for this file chooser.
	 */
	public void setRequiredExtension(String extension) {
		sRequiredExtension = extension;
	}

	/**
	 * Approve the file selection against the required extension.
	 */
  	public void approveSelection() {

    	File file = getSelectedFile();
    	if (file == null) {
    		// no file selected ... -> cancelSelection()
    	}
		else {
    		if (getDialogType() == JFileChooser.SAVE_DIALOG) {

		       	String fileName = file.getAbsolutePath();

				File newfile = file;
				if (fileName != null && !sRequiredExtension.equals("")) { //$NON-NLS-1$
					if ( !fileName.toLowerCase().endsWith(sRequiredExtension) ) {
						fileName = fileName+sRequiredExtension;
						newfile = new File(fileName);
						setSelectedFile(newfile);
					}
				}

				if (newfile.exists()) {
	        		int answer = JOptionPane.showConfirmDialog(this, LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "UIFileChooser.fileExists"), LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "UIFileChooser.warning"), //$NON-NLS-1$ //$NON-NLS-2$
								JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);

	    	    	if (answer == JOptionPane.OK_OPTION) {
	       				super.approveSelection();
	        		}
					else {
	           			// a user cancelled over write.
	        		}
				}
				else {
					super.approveSelection();
				}
			}
			else if (getDialogType() == JFileChooser.OPEN_DIALOG) {

		       	String fileName = file.getAbsolutePath();
				if (fileName != null) {
					if (!sRequiredExtension.equals("")) { //$NON-NLS-1$

						if ( !fileName.toLowerCase().endsWith(sRequiredExtension) ) {
			        		JOptionPane.showMessageDialog(this, LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "UIFileChooser.selectFile")+" '"+sRequiredExtension+"'"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
						}
						else {
		       				super.approveSelection();
						}
					}
					else {
						super.approveSelection();
					}
				}
    		}
			else {
				super.approveSelection();
			}
    	}
  	}
}


