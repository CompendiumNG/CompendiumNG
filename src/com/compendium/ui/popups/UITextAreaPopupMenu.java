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

package com.compendium.ui.popups;

import java.awt.Container;
import java.awt.Color;
import java.awt.event.*;

import javax.swing.*;

import com.compendium.*;
import com.compendium.ui.*;

/**
 * This draws a small popup menu for right-clicks on a UITextArea.
 *
 * @author	Michelle Bachler
 */
public class UITextAreaPopupMenu extends JPopupMenu implements ActionListener {

	/** The menu option to perform a cut operation.*/
	private JMenuItem		miMenuItemCut		= null;

	/** The menu option to perform a copy operation.*/
	private JMenuItem		miMenuItemCopy		= null;

	/** The menu option to perform a paste operation.*/
	private JMenuItem		miMenuItemPaste		= null;

	/** The menu option to perform a print operation.*/
	private JMenuItem		miMenuItemPrint		= null;

	/** The height of this popup.*/
	private int				nHeight				= 100;

	/** The width of this popup.*/
	private int				nWidth				= 300;

	/** The UITextArea associated with this popup.*/
	private UITextArea 		area				= null;

	/** The shortcut key associated with the current platform.*/
	private int 			shortcutKey;

	/**
	 * Constructor. Draws the popupmenu.
	 * @param area com.compendium.ui.UITextArea, the text area associated with this popup menu.
	 */
	public UITextAreaPopupMenu(UITextArea area) {
		super("Details options");

		shortcutKey = ProjectCompendium.APP.shortcutKey;

		this.area = area;

		miMenuItemCut = new JMenuItem("Cut", UIImages.get(IUIConstants.CUT_ICON));
		miMenuItemCut.setToolTipText("Cut selected text to system clipbard");
		miMenuItemCut.setAccelerator(KeyStroke.getKeyStroke( KeyEvent.VK_X, shortcutKey));
		miMenuItemCut.setMnemonic('U');
		miMenuItemCut.addActionListener(this);
		add(miMenuItemCut);

		miMenuItemCopy = new JMenuItem("Copy", UIImages.get(IUIConstants.COPY_ICON));
		miMenuItemCopy.setToolTipText("Copy selected text to system clipbard");
		miMenuItemCopy.setAccelerator(KeyStroke.getKeyStroke( KeyEvent.VK_C, shortcutKey));
		miMenuItemCopy.setMnemonic('C');
		miMenuItemCopy.addActionListener(this);
		add(miMenuItemCopy);

		miMenuItemPaste = new JMenuItem("Paste", UIImages.get(IUIConstants.PASTE_ICON));
		miMenuItemPaste.setToolTipText("Paste text from system clipbard");
		miMenuItemPaste.setAccelerator(KeyStroke.getKeyStroke( KeyEvent.VK_V, shortcutKey));
		miMenuItemPaste.setMnemonic('A');
		miMenuItemPaste.addActionListener(this);
		add(miMenuItemPaste);

		//miMenuItemPrint = new JMenuItem("Print");
		//miMenuItemPrint.setToolTipText("Print the current page");
		//miMenuItemPrint.setMnemonic('P');
		//miMenuItemPrint.addActionListener(this);
		//add(miMenuItemPrint);

		// If on the Mac OS and the Menu bar is at the top of the OS screen, remove the menu shortcut Mnemonics.
		if (ProjectCompendium.isMac && (FormatProperties.macMenuBar || (!FormatProperties.macMenuBar && !FormatProperties.macMenuUnderline)) )
			UIUtilities.removeMenuMnemonics(getSubElements());

		pack();
		setSize(nWidth,nHeight);
	}

	/**
	 * Handles the event of an option being selected.
	 * @param evt, the event associated with the option being selected.
	 */
	public void actionPerformed(ActionEvent evt) {

		Object source = evt.getSource();

		if(source.equals(miMenuItemCut)) {
			area.processCut();
		}
		else if(source.equals(miMenuItemCopy)) {
			area.processCopy();
		}
		else if(source.equals(miMenuItemPaste)) {
			area.processPaste();
		}
		//else if(source.equals(miMenuItemPrint)) {
		//	area.processPrint();
		//}
	}

	/**
	 * Handle the cancelleing of this popup. Set is to invisible.
	 */
	public void onCancel() {
		setVisible(false);
	}
}
