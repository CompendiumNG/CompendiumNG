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
import java.awt.*;
import java.awt.event.*;
import java.awt.datatransfer.*;
import java.io.*;

import com.compendium.*;
import com.compendium.ui.popups.*;

/**
 * This class extends textarea to add a right-click menu for cut/copy/paste.
 *
 * @author Michelle Bachler
 */
public class UITextArea extends JTextArea {

	/** NOT CURRENTLY USED.*/
	private int length			=0;

	/** Reference to this class for use in inner thread.*/
	private UITextArea area = null;

    /**
     * Constructs a new UITextArea.
     */
	public UITextArea() {
		super();
		initialiseArea();
	}

    /**
     * Constructs a new TextArea with the specified text displayed.
     *
     * @param text the text to be displayed, or null
     */
	public UITextArea(String text) {
		super(text);
		initialiseArea();
	}

    /**
     * Constructs a new empty TextArea with the specified number of
     * rows and columns.  A default model is created, and the initial
     * string is null.
     *
     * @param rows, the number of rows >= 0
     * @param columns, the number of columns >= 0
     */
	public UITextArea(int rows, int columns) {
		super(rows, columns);

		area = this;

		addMouseListener( new MouseAdapter() {
			public void mouseClicked(MouseEvent evt) {
				boolean isRightMouse = SwingUtilities.isRightMouseButton(evt);
				if(isRightMouse) {
					if(evt.getClickCount() == 1) {
						UITextAreaPopupMenu pop = new UITextAreaPopupMenu(area);
						pop.show(area, evt.getX(), evt.getY());
					}
				}
			}
		});

		initialiseArea();
	}

	/**
	 * Set line wrap and word wrap to true.
	 */
	private void initialiseArea() {
		setLineWrap(true);
		setWrapStyleWord(true);
	}

	/**
	 * Process a cut operation.
	 */
	public void processCut() {

		if (getSelectedText() != null) {
			cut();
		}
		else {
			ProjectCompendium.APP.displayMessage(LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "UITextArea.tryAgain"), LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "UITextArea.title1")); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	/**
	 * Process a copy operation.
	 */
	public void processCopy() {

		if (getSelectedText() != null) {
			copy();
		}
		else {
			ProjectCompendium.APP.displayMessage(LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "UITextArea.tryAgain"), LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "UITextArea.title2")); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	/**
	 * Process a paste operation.
	 */
	public void processPaste() {
		paste();
	}

	/**
	 * Process a print operation - NOT CURRENTLY IMPLEMENTED.
	 */
	public void processPrint() {

		/*
	   	Toolkit tk = this.getToolkit();
		String title = "Details print by "+ProjectCompendium.APP.getModel().getUserProfile().getUserName();
	   	PrintJob pj = tk.getPrintJob((Component)this, title, null);
	   	if(pj != null) {
			Graphics pg = pj.getGraphics();
			this.printAll(pg);
			pg.dispose();
			pj.end();
	   	}
		*/

		/*
		String s = getText();
		String tmp = getText();
		String nextline = "";

		StringBuffer finaltext = new StringBuffer(1000);
		StringBuffer line = new StringBuffer(150);

		StringReader tr = new StringReader (tmp);
		LineNumberReader tlnr = new LineNumberReader (tr);

		// GET LENGTH OF LONGEST STRING //
		int len=0;
		boolean verylong = false;
		String orientation = "Portrait";
		try {
			do {
				nextline = tlnr.readLine();
				len = nextline.length();
				if (len > 134) {
					verylong = true;
					orientation = "Landscape";
					break;
				}
				else if (len > 80) {
					orientation = "Landscape";
					break;
				}
			} while (nextline != null);
		}
		catch (EOFException eof) {}
		catch(Throwable t) {}

		StringReader sr = new StringReader (s);
		LineNumberReader lnr = new LineNumberReader (sr);

		try {
			do {
				nextline = (lnr.readLine());

				//System.out.println("nextline = "+nextline);

				if (nextline != null) {
					length = nextline.length();
					if (length == 0) {
						finaltext.append("\n");
					}
					else {
						finaltext.append(nextline).append("\n");
						length=-1;
					}
				}
			} while (nextline != null);
		}
		catch (EOFException eof) {}
		catch(Throwable t) {}

		//String message = "Set the printer orientation to "+orientation+"\n";
		//ProjectCompendium.APP.displayMessage.warn(message, "Print Opertaion");

		PrintManager pf = new PrintManager(this, CGconstants.STRING_TYPE);
		pf.putText(""); // CLEAR BUFFER
		pf.setJobTitle("Details print by "+ProjectCompendium.APP.getModel().getUserProfile().getUserName());
		pf.addText(finaltext.toString());
		pf.printAll();
		*/
	}
}
