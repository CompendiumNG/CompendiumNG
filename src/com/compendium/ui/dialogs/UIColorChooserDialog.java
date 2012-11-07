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
import javax.swing.border.*;

import com.compendium.LanguageProperties;
import com.compendium.ui.UIButton;

/**
 * Produces a colour chooser dialog.
 * <p>
 * THIS CLASS IS NOT CURRENTLY USED.
 *
 * @author	Michelle Bachler
 */
public class UIColorChooserDialog extends UIDialog implements ActionListener {

	/** The main pane to add content to.*/
	private Container	oContentPane = null;

	/** The current chosen color.*/
	private Color 		chosen = null;

	/** The button to save the chosen color.*/
	private JButton		pbSave	= null;

	/** The button to cancel the dialog without saving.*/
	private JButton		pbCancel	= null;

	/** The JColorChooser panel this dialog uses.*/
	private JColorChooser tcc		= null;

	/**
	 * Constrcutor. Draws the dialog.
	 * @param parent the parent frame for this dialog.
	 * @param oColour the starting colour to select;
	 */
    public UIColorChooserDialog(JFrame parent, Color oColour) {
		super(parent, true);

		setTitle(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIColorChooserDialog.title")); //$NON-NLS-1$
		
        tcc = new JColorChooser(oColour);
		oContentPane = getContentPane();
		JPanel mainpanel = new JPanel(new BorderLayout());
		mainpanel.setBorder(new EmptyBorder(10,10,10,10));

		mainpanel.add(tcc, BorderLayout.CENTER);

		JPanel buttonpanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

		// Add export button
		pbSave = new UIButton(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIColorChooserDialog.saveButton")); //$NON-NLS-1$
		pbSave.setMnemonic(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIColorChooserDialog.saveButtonMnemonic").charAt(0)); //$NON-NLS-1$
		pbSave.addActionListener(this);
		getRootPane().setDefaultButton(pbSave);
		buttonpanel.add(pbSave);

		// Add close button
		pbCancel = new UIButton(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIColorChooserDialog.cancelButton")); //$NON-NLS-1$
		pbCancel.setMnemonic(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIColorChooserDialog.cancelButtonMnemonic").charAt(0)); //$NON-NLS-1$
		pbCancel.addActionListener(this);
		buttonpanel.add(pbCancel);

		mainpanel.add(buttonpanel, BorderLayout.SOUTH);
		oContentPane.add(mainpanel);

		pack();
    }

	/**
	 * Handle action events coming from the buttons.
	 * @param evt, the associated ActionEvent.
	 */
	public void actionPerformed(ActionEvent evt) {
		Object source = evt.getSource();

		if (source instanceof JButton) {
			if (source == pbSave) {
				chosen = tcc.getColor();
				onCancel();
			}
			else {
				onCancel();
			}
		}
	}

	/**
	 * Set the colour the user has chosen.
	 */
	public void setColour(Color color) {
		tcc.setColor(color);
	}
	
	/**
	 * Return the colour the user has chosen.
	 */
	public Color getColour() {
		return chosen;
	}

	/**
	 * hide the dialog.
	 */
	public void onCancel() {
		setVisible(false);
	}
}
