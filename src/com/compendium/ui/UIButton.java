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

/**
 * This class extends JButton and was implemented so all the buttons used in Compendium have the same font.
 * Will be use for more in the future.
 * @author Michelle Bachler
 */
public class UIButton extends JButton {

	private Font oFont			= new Font("Dialog", Font.PLAIN, 12); //$NON-NLS-1$

    /**
     * Creates a button with no set sText or icon.
     */
	public UIButton() {
		super();
		initialise();
	}

    /**
     * Creates a button with sText.
     *
	 * @param sText - the text of the button.
     */
	public UIButton(String sText) {
		super(sText);
		initialise();
	}

	/**
	 * Creates a button with an icon.
	 *
	 * @param icon - the Icon image to display on the button.
	 */
	public UIButton(Icon icon) {
		super(icon);
		initialise();
	}


	/**
	 * Creates a button where properties are taken from the Action supplied.
	 *
	 * @param a - the Action used to specify the new button.
	 */
	public UIButton(Action a) {
		super(a);
		initialise();
	}

	/**
	 * Creates a button with initial sText and an icon.
	 *
	 * @param sText - the sText of the button
	 * @param icon - the Icon image to display on the button.
	 */
	public UIButton(String sText, Icon icon) {
		super(sText, icon);
		initialise();
	}

	/**
	 * Set font.
	 */
	private void initialise() {
		setFont(oFont);
	}
}
