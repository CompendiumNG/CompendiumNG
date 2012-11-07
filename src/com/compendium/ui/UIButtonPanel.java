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
 * This class extends JPanel and was implemented so all the button panels would have consistent look and layout.
 *
 * @author Michelle Bachler
 */
public class UIButtonPanel extends JPanel {

	/** The layout manager used by this button panel.*/
	private GridBagLayout oGridBagLayout 	= null;

	private UIButton	pbHelpButton = null;

	private int oXPos = 1;

    /**
     * Creates a buttonpanel.
     */
	public UIButtonPanel() {
		super();
		oGridBagLayout = new GridBagLayout();
		setLayout(oGridBagLayout);

		GridBagConstraints gc = new GridBagConstraints();
		gc.fill = GridBagConstraints.BOTH;
		gc.weightx = 1;
		gc.gridwidth=GridBagConstraints.REMAINDER;
		gc.anchor = GridBagConstraints.NORTH;

		JSeparator sep = new JSeparator();
		super.add(sep);
		oGridBagLayout.setConstraints(sep, gc);
	}

	/**
	 * Add the given button on the left of the panel.
	 *
	 * @param oButton - the button to be added;
	 */
	public void addButton(UIButton oButton) {
		GridBagConstraints gc = new GridBagConstraints();
		gc.fill = GridBagConstraints.NONE;
		gc.insets = new Insets(10,10,10,10);
		gc.weightx = 1;
		gc.gridwidth=1;
		//gc.gridx = oXPos;
		//oXPos++;
		//gc.anchor = GridBagConstraints.EAST;
		gc.anchor = GridBagConstraints.WEST;
		oGridBagLayout.setConstraints(oButton, gc);
		super.add(oButton);
	}

	/**
	 * Add the given help button on the right of the panel.
	 *
	 * @param oButton - the button to be added;
	 */
	public void addHelpButton(UIButton oButton) {
		GridBagConstraints gc = new GridBagConstraints();
		gc.fill = GridBagConstraints.NONE;
		gc.insets = new Insets(10,10,10,10);
		gc.weightx = 30;
		gc.gridwidth=1;
		//gc.gridx = 0;
		//gc.anchor = GridBagConstraints.WEST;
		gc.anchor = GridBagConstraints.EAST;
		oGridBagLayout.setConstraints(oButton, gc);
		super.add(oButton);
	}


	/**
	 * Overridden to do nothing. Returns null.
	 *
	 * @param comp - the component to be added
	 */
	public Component add(Component comp) {
		return null;
	}

	/**
	 * Overridden to do nothing. Returns null.
	 *
	 * @param
	 * @param comp - the component to be added
	 */
	public Component add(String name, Component comp) {
		return null;
	}

	/**
	 * Overridden to do nothing. Returns null.
	 *
	 * @param comp - the component to be added
	 * @param index - the position at which to insert the component, or -1 to append the component to the end.
	 */
	public Component add(Component comp, int index) {
		return null;
	}

	/**
	 * Overridden to do nothing.
	 *
	 * @param comp - the component to be added
	 * @param constraints - an object expressing layout contraints for this
	 * @param index - the position in the container's list at which to insert the component; -1 means insert at the end component
	 */
	public void add(Component comp, Object constraints, int index) {}
}
