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

import java.util.*;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.*;

import com.compendium.core.ICoreConstants;
import com.compendium.LanguageProperties;
import com.compendium.ProjectCompendium;
import com.compendium.ui.plaf.*;
import com.compendium.ui.*;


/**
 * UIHintDialog defines the dialog to display a hint message which can be switched off by the user.
 *
 * @author	Michelle Bachler
 */
public class UIHintDialog extends UIDialog implements ActionListener, IUIConstants {

	public static final int		PASTE_HINT = 0;
	
	private static final String PASTE_HINT_KEY = "showPasteHint";
		
	/** The button to close the dialog.*/
	private UIButton				pbClose	= null;

	/** The pane for the dialog's contents.*/
	private Container				oContentPane = null;

	/** The central panel with the message details.*/
	private JPanel					oDetailsPanel = null;

	/** The panel with the buttons on.*/
	private UIButtonPanel			oButtonPanel = null;

	/** The label for the image.*/
	private JLabel					oImageLabel = null;

	/** The image for the node to create.*/
	private ImageIcon				oImage = null;

	/** Holds the jabber message text.*/
	private JTextArea				oTextArea = null;

	/** The jabber message.*/
	private String 					sMessage = ""; //$NON-NLS-1$
	
	/** The type of hint - see e.g. PASTE_HINT*/
	private int						nType = 0;
	
	/** Check box to let the user turn off this hint.*/
	private JCheckBox				cbShowPasteHint = null;

	/**
	 * Initializes and draws the dialog.
	 * @param parent the parent frame for this dialog.
	 * @param sKey the property string associated with this message - used to set FormatProperties when user ticks box.
	 */
	public UIHintDialog(JFrame parent, int nType) {

		super(parent, true);
		setTitle("Hint");

		this.nType = nType;
		
		if (nType == PASTE_HINT) {
			this.sMessage = LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIHintDialog.pasteHint1")+"\n\n"+
			LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIHintDialog.pasteHint2")+"\n\n"+
			LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIHintDialog.pasteHint3");
		}
		
		oContentPane = getContentPane();
		oContentPane.setLayout(new BorderLayout());

		oDetailsPanel = new JPanel(new BorderLayout());

		JPanel imagePanel = new JPanel();
		imagePanel.setBorder(new EmptyBorder(10,10,10,0));

		oImage = UIImages.getNodeImage(ICoreConstants.POSITION);
		oImageLabel = new JLabel( oImage );
		oImageLabel.setVerticalAlignment(SwingConstants.TOP);
		imagePanel.add(oImageLabel);

		oDetailsPanel.add(imagePanel, BorderLayout.WEST);

		oTextArea = new JTextArea(sMessage);
		oTextArea.setEditable(false);
		oTextArea.setFont(new Font("Dialog", Font.PLAIN, 12)); //$NON-NLS-1$
		oTextArea.setBackground(oDetailsPanel.getBackground());
		oTextArea.setColumns(35);
		oTextArea.setLineWrap(true);
		oTextArea.setWrapStyleWord(true);
		oTextArea.setSize(oTextArea.getPreferredSize());

		JPanel textPanel = new JPanel();
		textPanel.setBorder(new EmptyBorder(10,10,20,10));
		textPanel.setBorder(new EmptyBorder(10,10,20,10));
		textPanel.add(oTextArea);

		JPanel oCheckBoxPanel = new JPanel();
		cbShowPasteHint = new JCheckBox();
		cbShowPasteHint.setText(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIHintDialog.hideHint"));
		cbShowPasteHint.setSelected(false);
		cbShowPasteHint.setHorizontalAlignment(SwingConstants.LEFT);
		oCheckBoxPanel.add(cbShowPasteHint);

		oDetailsPanel.add(textPanel, BorderLayout.CENTER);
		oDetailsPanel.add(oCheckBoxPanel, BorderLayout.SOUTH);
		
		oButtonPanel = new UIButtonPanel();
		pbClose = new UIButton(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIHintDialog.closeButton")); //$NON-NLS-1$
		pbClose.setMnemonic(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIHintDialog.closeButtonMnemonic").charAt(0));
		pbClose.addActionListener(this);
		oButtonPanel.addButton(pbClose);

		oContentPane.add(oDetailsPanel, BorderLayout.CENTER);
		oContentPane.add(oButtonPanel, BorderLayout.SOUTH);

		pack();
		setResizable(false);
	}


	/**
	 * Handle action events coming from the buttons.
	 * @param evt, the associated ActionEvent object.
	 */
	public void actionPerformed(ActionEvent evt) {
		Object source = evt.getSource();
		if (source instanceof JButton) {
			if (source == pbClose) {
				if (nType == PASTE_HINT) {
					if (cbShowPasteHint.isSelected()) {
						FormatProperties.showPasteHint = false;
						FormatProperties.setFormatProp( PASTE_HINT_KEY, "false" ); //$NON-NLS-1$ //$NON-NLS-2$
						FormatProperties.saveFormatProps();
					}
				}
				onCancel();
			}
		}
	}
}
