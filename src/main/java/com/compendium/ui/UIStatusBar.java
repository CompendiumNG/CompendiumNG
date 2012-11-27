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
import javax.swing.*;
import javax.swing.border.*;

/**
 * The status bar is a generic status bar for use in application
 * to display the status when an application is processing or
 * to display informational messages.
 *
 * @author	Ron van Hoof / Michelle Bachler
 */
public class UIStatusBar extends JPanel {

	/** Default top inset */
	public final static int TOP			= 3;

	/** Default left inset */
	public final static int LEFT		= 5;

	/** Default bottom inset */
	public final static int BOTTOM		= 3;

	/** Default right inset */
	public final static int RIGHT		= 5;

	/** The status label displaying the actual message */
	private	JLabel lblStatus = new JLabel();

	/** Insets for the status bar */
	private	Insets oInsets = new Insets(TOP, LEFT, BOTTOM, RIGHT);

	/** The background color of the panel.*/
	private Color panelBack	= null;

	/** The background color of the label.*/
	private Color labelBack = null;

	/** The foreground color of the label.*/
	private Color labelFore = null;

	/**
	 * Constuctor, creates a new status bar with an empty message.
	 */
	public UIStatusBar() {
		this(""); //$NON-NLS-1$
	}

	/**
	 * Constructor, creates a new status bar with the given initial message.
	 *
	 * @param text, the initial message in the status bar.
	 */
	public UIStatusBar(String text) {
		super();
		lblStatus.setText(text);
		init();
	}

	/**
	 * Initializes the status bar.
	 */
	public void init() {
		setBorder(new SoftBevelBorder(BevelBorder.LOWERED));
		setLayout(new BorderLayout());
		add(lblStatus, BorderLayout.CENTER);
	}

	/**
	 * Sets the status message in the status bar.
	 *
	 * @param text, the message to be displayed.
	 */
	public void setStatus(String text) {
		// a quick fix to keep the status bar from disappearing when the text
		// is null - set it to one blank space and it stays - bz
		if (text.equals("")) //$NON-NLS-1$
			text = " "; //$NON-NLS-1$
		lblStatus.setText(text);
		repaint();
	}

	/**
	 * Returns the message currently displayed in the status bar.
	 *
	 * @return String, the status message.
	 */
	public String getStatus() {
		return lblStatus.getText();
	}

	/**
	 * Returns the insets of the status bar.
	 *
	 * @return Insets, the insets of the status bar.
	 */
	public Insets getInsets() {

		if (oInsets == null)
			oInsets = new Insets(TOP,LEFT,BOTTOM,RIGHT);
		return oInsets;
	}

	/**
	 * Sets the Background colour of the status bar to the given colour
	 */
	public void setBackgroundColor(Color color) {
		panelBack=getBackground();
		setBackground(color);
		labelBack=lblStatus.getBackground();
		lblStatus.setBackground(color);
	}

	/**
	 * Sets the Foreground colour of the status bar to the given colour
	 */
	public void setForegroundColor(Color color) {
		labelFore=lblStatus.getForeground();
		lblStatus.setForeground(color);
	}

	/**
	 * resets the status bar background and foreground to the default
	 */
	public void resetColors() {
		setBackground(panelBack);
		lblStatus.setBackground(labelBack);
		lblStatus.setForeground(labelFore);
	}
}
