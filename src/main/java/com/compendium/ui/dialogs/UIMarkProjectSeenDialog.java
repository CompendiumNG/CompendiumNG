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

import com.compendium.LanguageProperties;
import com.compendium.ui.*;

/**
 * UIMarkProjectSeenDialog defines the MarkProjectSeen dialog.
 *
 * @author	M. Begeman
  */
public class UIMarkProjectSeenDialog extends UIDialog implements ActionListener, IUIConstants {

	/** The pane for the dialog's contents.*/
	private Container			oContentPane 	= null;

	/** The button to start the operation.*/
	private JButton				jbStart			= null;

	/** The button to close the dialog.*/
	private	JButton				jbCancel		= null;

	
	/**
	 * Initializes and sets up the dialog.
	 * @param parent, the parent view for this dialog.
	 */
	public UIMarkProjectSeenDialog(JFrame parent, long lNodeCount) {

	  	super(parent, true);
		setTitle(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIMarkProjectSeenDialog.title")); //$NON-NLS-1$

		oContentPane = getContentPane();
		drawDialog(lNodeCount);
	}

	/**
	 * Draw the contents of the dialog.
	 */
	private void drawDialog(long lNodeCount) {

		JPanel oCenterPanel = new JPanel();

		GridBagLayout gb = new GridBagLayout();
		GridBagConstraints gc = new GridBagConstraints();
		oCenterPanel.setLayout(gb);
		gc.insets = new Insets(5,5,5,5);
		
		gc.gridx = 0;
		gc.gridy = 1;
		String sMessage = " "+LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIMarkProjectSeenDialog.message1a") + Long.toString(lNodeCount); //$NON-NLS-1$
		sMessage = sMessage +" "+LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIMarkProjectSeenDialog.message1b"); //$NON-NLS-1$
		JLabel instructions1 = new JLabel(sMessage, null, JLabel.CENTER);
		oCenterPanel.add(instructions1, gc);

		gc.gridy = 2;
		sMessage = LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIMarkProjectSeenDialog.message2a"); //$NON-NLS-1$
		JLabel instructions2 = new JLabel(sMessage, null, JLabel.CENTER);
		oCenterPanel.add(instructions2, gc);
		
		gc.gridy = 3;
		sMessage = LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIMarkProjectSeenDialog.message2b"); //$NON-NLS-1$
		JLabel instructions3 = new JLabel(sMessage, null, JLabel.CENTER);
		oCenterPanel.add(instructions3, gc);

		JLabel spacer = new JLabel(" ");		// Add spacer //$NON-NLS-1$
		gc.gridy = 4;
		oCenterPanel.add(spacer, gc);
		
		JPanel oButtonPanel = new JPanel();		// Start building the buttons
		
		gc.gridy = 0;
		jbStart = new JButton(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIMarkProjectSeenDialog.startButton")); //$NON-NLS-1$
		jbStart.setMnemonic(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIMarkProjectSeenDialog.startButtonMnemonic").charAt(0)); //$NON-NLS-1$
		jbStart.addActionListener(this);
		
		getRootPane().setDefaultButton(jbStart);
		oButtonPanel.add(jbStart, gc);
		
		gc.gridy = 1;
		jbCancel = new JButton(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIMarkProjectSeenDialog.cancelButton")); //$NON-NLS-1$
		jbCancel.setMnemonic(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIMarkProjectSeenDialog.cancelButtonMnemonic").charAt(0)); //$NON-NLS-1$);
		jbCancel.addActionListener(this);
		oButtonPanel.add(jbCancel, gc);

		oContentPane.setLayout(new BorderLayout());
		oContentPane.add(oCenterPanel, BorderLayout.NORTH);
		oContentPane.add(new JSeparator(), BorderLayout.CENTER);
		oContentPane.add(oButtonPanel, BorderLayout.SOUTH);

		pack();
		setResizable(false);
		return;
	}

	/******* EVENT HANDLING METHODS *******/

	/**
	 * Handle the button push events.
	 * @param evt, the associated ActionEvent object.
	 */
	public void actionPerformed(ActionEvent evt) {

		Object source = evt.getSource();

		// Handle button events
		if (source instanceof JButton) {
			if (source == jbStart) {
//				Thread thread = new Thread("UIMarkProjectSeenDialog: DoIt") {
//					public void run() {
						onMarkSeenStart();
//					}
//				};
//				thread.run();
			} else
			if (source == jbCancel) {
				onCancel();
			}
		}
	}

	/**
	 * Handle the action to Mark everything as Seen.
	 */
	public void onMarkSeenStart()  {
		setVisible(false);
		try {
			MarkProjectSeen MarkProjectSeen = new MarkProjectSeen();
			MarkProjectSeen.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
		dispose();
	}

	/**
	 * Handle the Cancel action. Closes the dialog.
	 */
	public void onCancel() {
		setVisible(false);
		dispose();
	}
}
