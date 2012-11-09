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

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;

import javax.swing.*;
import javax.swing.event.*;

import com.compendium.core.datamodel.*;
import com.compendium.*;

/**
 * This class extends JList and implments key control search of the list.
 *
 * @author	Michelle Bachler
 * @version	1.0
 */
public class UINavList extends JList {

	/** The keys entered by the user for the search.*/
	String keys = ""; //$NON-NLS-1$

    /**
     * Constructs a <code>JList</code> that displays the elements in the
     * specified, non-<code>null</code> model.
     *
     * @param dataModel, the data model for this list.
     */
    public UINavList(ListModel dataModel) {
		super(dataModel);

		addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent evt) {
				final KeyEvent e = evt;
				Thread thread = new Thread("UINavList 1") { //$NON-NLS-1$
					public void run() {
						processKeyPressed(e);
					}
				};
				thread.start();
			}
		});
    }

    /**
     * Constructs a <code>JList</code> that displays the elements in the specified array.
     *
     * @param listData, the array of Objects to be loaded into the data model.
     */
    public UINavList(final Object[] listData) {
		super(listData);

		addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent evt) {
				final KeyEvent e = evt;
				Thread thread = new Thread("UINavList 2") { //$NON-NLS-1$
					public void run() {
						processKeyPressed(e);
					}
				};
				thread.start();
			}
		});
    }

    /**
     * Constructs a <code>JList</code> that displays the elements in
     * the specified <code>Vector</code>.
     *
     * @param listData, the <code>Vector</code> to be loaded into the data model.
     */
    public UINavList(final Vector listData) {
		super(listData);

		addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent evt) {
				final KeyEvent e = evt;
				Thread thread = new Thread("UINavList 3") { //$NON-NLS-1$
					public void run() {
						processKeyPressed(e);
					}
				};
				thread.start();
			}
		});
    }


    /**
     * Constructs a <code>JList</code> with an empty model.
     */
    public UINavList() {
		super();

		addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent evt) {
				final KeyEvent e = evt;
				Thread thread = new Thread("UINavList 4") { //$NON-NLS-1$
					public void run() {
						processKeyPressed(e);
					}
				};
				thread.start();
			}
		});
    }

	/**
	 * Process the keyPressed to go down the list.
	 * @param evt, the associated KeyEvent object.
	 */
	private void processKeyPressed(KeyEvent evt) {

		char keyChar = evt.getKeyChar();
		char [] key = {keyChar};
		String sKeyPressed = new String(key);
		int keyCode = evt.getKeyCode();
		int modifiers = evt.getModifiers();

		if (keyCode == KeyEvent.VK_BACK_SPACE && modifiers == 0) {
			if (keys.length() > 0) {
				keys = keys.substring(0, keys.length()-1);
				ProjectCompendium.APP.setStatus(keys);
				searchList(keys);
			}
		}
		else if ( Character.isLetterOrDigit(keyChar) || sKeyPressed.equals(" ") ) { //$NON-NLS-1$
			keys += sKeyPressed;
			ProjectCompendium.APP.setStatus(keys);
			searchList(keys);
		}
		else if(IUIConstants.NAVKEYCHARS.indexOf(sKeyPressed) != -1) {
			keys += sKeyPressed;
			ProjectCompendium.APP.setStatus(keys);
			searchList(keys);
		}

		evt.consume();
	}


	/**
	 * Find the item that matches the current key presses and select the item.
	 * @param sKeys, the keys to find the item for.
	 */
	private void searchList( String sKeys ) {
		int index = findMatchingItem( sKeys );

		if (index <= -1)
			index = 0;

		setSelectedIndex(index);
		ensureIndexIsVisible(index);
		repaint();
	}


	/**
	 * Find the item that matches the current key presses.
	 * @param sKeys, the keys to match.
	 */
	private int findMatchingItem( String sKeys ) {

		String sKeyUpper = sKeys.toUpperCase();
		String sKeyLower = sKeys.toLowerCase();

		int lastMatchCount = 0;
		int lastMatchItem = -1;

		if (keys == null || keys.equals("")) //$NON-NLS-1$
			return lastMatchItem;

		ListModel model = getModel();
		int nCount = model.getSize();

		for(int i=0; i<nCount; i++) {

			int keyCount = 0;
			String sLegend = ""; //$NON-NLS-1$
			Object obj = model.getElementAt(i);

			if (obj instanceof NodeSummary) {
				sLegend = ((NodeSummary)obj).getLabel();
			}
			else if (obj instanceof Code) {
				sLegend = ((Code)obj).getName();
			}
			else if (obj instanceof String) {
				sLegend = (String)obj;
			}
			else if (obj instanceof JLabel) {
				sLegend = ((JLabel)obj).getText();
			}
			else if (obj instanceof Vector) { // FOR CODE GROUPS
				Vector data = (Vector)obj;
				sLegend = (String)data.elementAt(1);
			}

			if (sLegend != null && !sLegend.equals("")) { //$NON-NLS-1$
				char sNextChar = sLegend.charAt( keyCount );
				char lowerKey = sKeyLower.charAt( keyCount );
				char upperKey = sKeyUpper.charAt( keyCount );

				while(  sNextChar == lowerKey || sNextChar == upperKey ) {
					keyCount++;
					if (keyCount <= sKeys.length()-1) {
						lowerKey = sKeyLower.charAt( keyCount );
						upperKey = sKeyUpper.charAt( keyCount );
						sNextChar = sLegend.charAt( keyCount );
					}
					else {
						break;
					}
				}

				if ( keyCount > 0 ) {
					if ( lastMatchCount >= keyCount && lastMatchCount == sKeys.length() ) {
						break;
					}

					if ( keyCount > lastMatchCount ) {
						lastMatchItem = i;
						lastMatchCount = keyCount;
					}
				}
			}
		}

		return lastMatchItem;
	}
}
