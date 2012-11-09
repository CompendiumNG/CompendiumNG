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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.event.PopupMenuListener;

import com.compendium.LanguageProperties;
import com.compendium.ui.DragAndDropProperties;

/**
 * Popup menu for when user drops a potential Compendium export file into the map. 
 * Simulates modal behaviour on show().
 * 
 * @author rudolf
 */
public class UIDropImportPopupMenu extends UIPopupMenu implements ActionListener, PopupMenuListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4559641290476610696L;

	/**
	 * Lists all possible actions when importing. 
	 */
	public enum ImportAction {
		/**
		 * process as normal drop
		 */
		DROP, 
		/**
		 * process as import
		 */
		IMPORT }; 
		
	/**
	 * Holds what the user selects.
	 */	
	public ImportAction selection = ImportAction.DROP; // default is normal drop
	/**
	 * The entry to process as drop.
	 */
	private JMenuItem miDrop = null;
	/**
	 * The entry to process as import.
	 */
	private JMenuItem miImport = null;
	
	/**
	 * Constructor.
	 * @param appframe the main application frame to bring to front when 
	 * 	menu is shown
	 * @param file the dropped file
	 * @param props the drag and drop properties to determine menu text from
	 */
	public UIDropImportPopupMenu( JFrame appframe, File file, DragAndDropProperties props ) {
		super(appframe);
		
		String filename = file.getName();

//		miDrop = new JMenuItem("Drop files as nodes");
//		if (!props.dndFileCopy) {
//			miDrop = new JMenuItem("Drop \"" + filename + "\" as link to original");
//		}
//		else {
//			miDrop = new JMenuItem("Drop \"" + filename + "\" as copy " 
//					+ (props.dndFileCopyDatabase?
//							"into the database" : "into the \"Linked Files\" directory"));
//		}
//		miDrop.addActionListener(this);
//		add(miDrop);
		
		miImport = new JMenuItem(LanguageProperties.getString(LanguageProperties.POPUPS_BUNDLE,"UIDropImportPopupMenu.process")+"\" " + filename + "\" "+LanguageProperties.getString(LanguageProperties.POPUPS_BUNDLE,"UIDropImportPopupMenu.compendiumImport")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		miImport.addActionListener(this);
		add(miImport);

		miDrop = new JMenuItem(LanguageProperties.getString(LanguageProperties.POPUPS_BUNDLE,"UIDropImportPopupMenu.process")+"\" " + filename + "\" "+LanguageProperties.getString(LanguageProperties.POPUPS_BUNDLE,"UIDropImportPopupMenu.normalDrop")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		miDrop.addActionListener(this);
		add(miDrop);

		this.addPopupMenuListener(this);
	}


	/**
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {

		if (e.getSource() == miImport) selection = ImportAction.IMPORT;
		else selection = ImportAction.DROP;
		
		synchronized( this ) {
			// wake up application thread - user has made his choice
			// (this is executed as a callback in the Swing thread)
			notifyAll();
		}
	}

}
