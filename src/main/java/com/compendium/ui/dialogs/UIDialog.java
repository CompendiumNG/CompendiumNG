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

package com.compendium.ui.dialogs;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.*;

import com.compendium.ui.UIUtilities;

/**
 * This is the parent dialog class, and implements key handling for the dialog.
 *
 * @author	Michelle Bachler 25/03/03
 */
public class UIDialog extends JDialog {

	/** The main panel to hold the dialog contents.*/
	protected JPanel 	mainPanel	= null;

	/** The parent JFrame or JDialog for this dialog.*/
	protected Component parentComp = null;

	/**
	 * Constrcutor. Create a new empty dialog with the given parent and state.
	 * @param parent, the parent JFrame for this dialog.
	 * @param modal, true if this dialog is modal, else false.
	 */
	public UIDialog(JFrame parent, boolean modal) {
		super(parent, modal);
		parentComp = (Component)parent;
		init();
	}

	/**
	 * Constrcutor. Create a new empty dialog with the given parent and state.
	 * @param parent, the parent JDialog for this dialog.
	 * @param modal, true if this dialog is modal, else false.
	 */
	public UIDialog(JDialog parent, boolean modal) {
		super(parent, modal);
		parentComp = (Component)parent;
		init();
	}

	/**
	 * Initialize this dialog by setting up certain actions and a WindowListener.
	 */
	private void init() {

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent evt) {
				onCancel();
			}
		});

		mainPanel = new JPanel();
		setContentPane(mainPanel);

		Action actionEnter = new CreateAction(KeyEvent.VK_ENTER, InputEvent.CTRL_MASK);
		mainPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, InputEvent.CTRL_MASK),"enter"); //$NON-NLS-1$
		mainPanel.getActionMap().put("enter", actionEnter); //$NON-NLS-1$

		Action actionEscape = new CreateAction(KeyEvent.VK_ESCAPE, 0);
		mainPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),"escape"); //$NON-NLS-1$
		mainPanel.getActionMap().put("escape", actionEscape); //$NON-NLS-1$

		Action actionW = new CreateAction(KeyEvent.VK_W, InputEvent.CTRL_MASK);
		mainPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_W, InputEvent.CTRL_MASK),"w"); //$NON-NLS-1$
		mainPanel.getActionMap().put("w", actionW); //$NON-NLS-1$
	}

	/**
	 * Override to center the dialog on the parent.
	 */
	public void pack() {
		super.pack();
		UIUtilities.centerComponent(this, parentComp);
	}

	/**
	 * Creates an abstract action to handle enter and escape key presses.
	 */
	private class CreateAction extends AbstractAction {

		private int		nKey = 0;
		private int		nModifier = 0;

		public CreateAction(int key, int modifier) {
	    	super();
			nKey = key;
			nModifier = modifier;
		}

		public void actionPerformed(ActionEvent evt) {

			if (nKey == KeyEvent.VK_ENTER && nModifier == Event.CTRL_MASK ) {
				onEnter();
			}
			else if ( (nKey == KeyEvent.VK_ESCAPE && nModifier == 0)
						|| (nKey == KeyEvent.VK_W && nModifier == Event.CTRL_MASK) ) {
				onCancel();
			}
		}
	}

	/**
	 * Handle the Enter key event. Try and activate the default button, else call onCancel.
	 */
	public void onEnter() {
		JButton oButton = getRootPane().getDefaultButton();
		if (oButton != null) {
			oButton.doClick();
		} else {
			onCancel();
		}
	}

	/**
	 * Close the dialog.
	 */
	public void onCancel() {
		setVisible(false);
		dispose();
	}
}
