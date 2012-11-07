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

import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.event.PopupMenuListener;

import com.compendium.LanguageProperties;
import com.compendium.ui.DragAndDropProperties;

/**
 * Popup menu for when user drops a file into the map. Simulates modal behaviour 
 * on show().
 * 
 * @author rudolf
 */
public class UIDropFilePopupMenu extends UIPopupMenu implements ActionListener, PopupMenuListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 197001013873377817L;

	/** 
	 * Specifies which action can be done when dropping a file
	 */
	public enum FileDropAction {
		/**
		 * Copy to the Linked Filed folder
		 */
		COPY,
		/**
		 * Create a reference
		 */
		LINK,
		/**
		 * Abort
		 */
		CANCEL }; 
	
	/**
	 * Saves what the user selects.
	 */
	public FileDropAction selection = FileDropAction.CANCEL; // default is cancel

	/**
	 * The entry for copying.
	 */
	private JMenuItem miCopy = null;
	/**
	 * The entry for creating a reference.
	 */
	private JMenuItem miLink = null;
	/**
	 * The entry to abort.
	 */
	private JMenuItem miCancel = null;

	/**
	 * Constructor.
	 * @param appframe the main application frame to bring to front when 
	 * 	menu is shown
	 * @param props the drag and drop properties to determine menu text from
	 */
	public UIDropFilePopupMenu( JFrame appframe, DragAndDropProperties props ) {
		super(appframe);
		
		miLink = new JMenuItem(LanguageProperties.getString(LanguageProperties.POPUPS_BUNDLE, "UIDropFilePopupMenu.dropAsLink")); //$NON-NLS-1$
		miLink.addActionListener(this);
		add(miLink);

		miCopy = new JMenuItem(LanguageProperties.getString(LanguageProperties.POPUPS_BUNDLE, "UIDropFilePopupMenu.dropAsCopy")  //$NON-NLS-1$
				+ (props.dndFileCopyDatabase?
						LanguageProperties.getString(LanguageProperties.POPUPS_BUNDLE, "UIDropFilePopupMenu.dropIntoDatabase") : LanguageProperties.getString(LanguageProperties.POPUPS_BUNDLE, "UIDropFilePopupMenu.dropIntoLinkedFiles"))); //$NON-NLS-1$ //$NON-NLS-2$
		miCopy.addActionListener(this);
		add(miCopy);
		
		miCancel = new JMenuItem(LanguageProperties.getString(LanguageProperties.POPUPS_BUNDLE, "UIDropFilePopupMenu.cancel")); //$NON-NLS-1$
		miCancel.addActionListener(this);
		add(miCancel);
		
		this.addPopupMenuListener(this);
	}


	/**
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {

		if (e.getSource() == miLink) selection = FileDropAction.LINK;
		else if (e.getSource() == miCopy) selection = FileDropAction.COPY;
		else selection = FileDropAction.CANCEL;
		
		synchronized( this ) {
			// wake up application thread - user has made his choice
			// (this is executed as a callback in the Swing thread)
			notifyAll();
		}
	}
}
