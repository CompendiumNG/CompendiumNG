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

package com.compendium.ui.panels;

import java.util.*;
import java.io.*;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.Document;
import javax.swing.border.*;

import com.compendium.core.*;
import com.compendium.core.datamodel.Link;

import com.compendium.*;
import com.compendium.ui.*;
import com.compendium.ui.linkgroups.*;
import com.compendium.ui.dialogs.UILinkContentDialog;

/**
 * This panel displays a link's static properties.
 *
 * @author	Michelle Bachler
 */
public class UILinkPropertiesPanel extends JPanel implements ActionListener {

	/** The UILink reference for the link to change setting for.*/
	public UILink			oUILink				= null;

	/** The Link object to chagen setting for.*/
	public Link				oLink				= null;

	/** Holds the link author.*/
	public JLabel			lblAuthor2			= null;

	/** Holds the link creation date.*/
	public JLabel			lblCreated2			= null;

	/** Holds the link last modification date.*/
	public JLabel			lblModified2		= null;

	/** Holds the link id number.*/
	public JLabel			lblId2				= null;

	/** Holds the originating node for this link..*/
	public JLabel			tfFromNode			= null;

	/** Holds the destination node for this link.*/
	public JLabel			tfToNode			= null;

	/** The button to cancel this dialog.*/
	public UIButton			pbCancel			= null;

	/** The button to open the relevant help.*/
	public UIButton			pbHelp				= null;

	/** The layout manager used for this dialog.*/
	private GridBagLayout 		gb 					= null;

	/** The constaints used with the layout manager.*/
	private GridBagConstraints 	gc 					= null;

	/** The parent dialog opf this panel.*/
	private UILinkContentDialog	oParentDialog = null;


	/**
	 * Constructor.
	 * @param uilink com.compendium.ui.UILink, the link to assign settings to.
	 * @param oParent, the parent dialog for this panel.
	 */
	public UILinkPropertiesPanel(UILink uilink, UILinkContentDialog oParent) {

		oUILink = uilink;
		oLink = uilink.getLink();
		this.oParentDialog = oParent;

		draw();
	}

	/**
	 * Draw the dialog's contents.
	 */
	public void draw() {

		JPanel centerpanel = new JPanel();
		centerpanel.setBorder(new EmptyBorder(5,5,5,5));

		gb = new GridBagLayout();
		centerpanel.setLayout(gb);

		gc = new GridBagConstraints();
		gc.insets = new Insets(3,3,3,3);
		gc.anchor = GridBagConstraints.WEST;
		gc.weightx=1;
		gc.weighty=1;

		int y=0;

		JLabel lFromNode = new JLabel(LanguageProperties.getString(LanguageProperties.PANELS_BUNDLE, "UILinkPropertiesPanel.from")+":"); //$NON-NLS-1$
		gc.gridy = y;
		gc.gridx = 0;
		gc.gridwidth=1;
		gb.setConstraints(lFromNode, gc);
		centerpanel.add(lFromNode);

		tfFromNode = new JLabel(""); //$NON-NLS-1$
		gc.gridy = y;
		y++;
		gc.gridx = 1;
		gc.gridwidth=1;
		gb.setConstraints(tfFromNode, gc);
		centerpanel.add(tfFromNode);

		JLabel lblToNode = new JLabel(LanguageProperties.getString(LanguageProperties.PANELS_BUNDLE, "UILinkPropertiesPanel.to")+":"); //$NON-NLS-1$
		gc.gridy = y;
		gc.gridx = 0;
		gc.gridwidth=1;
		gb.setConstraints(lblToNode, gc);
		centerpanel.add(lblToNode);

		tfToNode = new JLabel(""); //$NON-NLS-1$
		gc.gridy = y;
		y++;
		gc.gridx = 1;
		gc.gridwidth=1;
		gb.setConstraints(tfToNode, gc);
		centerpanel.add(tfToNode);

		//Author
		JLabel lblAuthor = new JLabel(LanguageProperties.getString(LanguageProperties.PANELS_BUNDLE, "UILinkPropertiesPanel.author")+":"); //$NON-NLS-1$
		gc.gridy = y;
		gc.gridx = 0;
		gc.gridwidth=1;
		gb.setConstraints(lblAuthor, gc);
		centerpanel.add(lblAuthor);

		lblAuthor2 = new JLabel(""); //$NON-NLS-1$
		gc.gridy = y;
		y++;
		gc.gridx = 1;
		gc.gridwidth=1;
		gb.setConstraints(lblAuthor2, gc);
		centerpanel.add(lblAuthor2);

		//Created
		JLabel lblCreated = new JLabel(LanguageProperties.getString(LanguageProperties.PANELS_BUNDLE, "UILinkPropertiesPanel.created")+":"); //$NON-NLS-1$
		gc.gridy = y;
		gc.gridx = 0;
		gc.gridwidth=1;
		gb.setConstraints(lblCreated, gc);
		centerpanel.add(lblCreated);

		lblCreated2 = new JLabel(""); //$NON-NLS-1$
		gc.gridy = y;
		y++;
		gc.gridx = 1;
		gc.gridwidth=1;
		gb.setConstraints(lblCreated2, gc);
		centerpanel.add(lblCreated2);

		//Modified
		JLabel lblModified = new JLabel(LanguageProperties.getString(LanguageProperties.PANELS_BUNDLE, "UILinkPropertiesPanel.lastMod")+":"); //$NON-NLS-1$
		gc.gridy = y;
		gc.gridx = 0;
		gc.gridwidth=1;
		gb.setConstraints(lblModified, gc);
		centerpanel.add(lblModified);

		lblModified2 = new JLabel(""); //$NON-NLS-1$
		gc.gridy = y;
		y++;
		gc.gridx = 1;
		gc.gridwidth=1;
		gb.setConstraints(lblModified2, gc);
		centerpanel.add(lblModified2);

		//Link ID
		JLabel lblId = new JLabel(LanguageProperties.getString(LanguageProperties.PANELS_BUNDLE, "UILinkPropertiesPanel.linkId")+":"); //$NON-NLS-1$
		gc.gridy = y;
		gc.gridx = 0;
		gc.gridwidth=1;
		gb.setConstraints(lblId, gc);
		centerpanel.add(lblId);

		lblId2 = new JLabel(""); //$NON-NLS-1$
		gc.gridy = y;
		y++;
		gc.gridx = 1;
		gc.gridwidth=1;
		gb.setConstraints(lblId2, gc);
		centerpanel.add(lblId2);

		JLabel lab = new JLabel(""); //$NON-NLS-1$
		gc.gridy = y;
		gc.gridx = 0;
		gc.gridwidth=2;
		gc.weighty = 100;
		gb.setConstraints(lab, gc);
		centerpanel.add(lab);

		setLayout(new BorderLayout());
		add(centerpanel, BorderLayout.CENTER);
		add(createButtonPanel(), BorderLayout.SOUTH);

		setLink(oLink);
	}

	/**
	 * Create and return the button panel.
	 */
	private UIButtonPanel createButtonPanel() {

		UIButtonPanel oButtonPanel = new UIButtonPanel();

		pbCancel = new UIButton(LanguageProperties.getString(LanguageProperties.PANELS_BUNDLE, "UILinkPropertiesPanel.closeButton")); //$NON-NLS-1$
		pbCancel.setMnemonic(LanguageProperties.getString(LanguageProperties.PANELS_BUNDLE, "UILinkPropertiesPanel.closeButtonMnemonic").charAt(0));
		pbCancel.addActionListener(this);
		oButtonPanel.addButton(pbCancel);

		pbHelp = new UIButton(LanguageProperties.getString(LanguageProperties.PANELS_BUNDLE, "UILinkPropertiesPanel.helpButton")); //$NON-NLS-1$
		pbHelp.setMnemonic(LanguageProperties.getString(LanguageProperties.PANELS_BUNDLE, "UILinkPropertiesPanel.helpButtonMnemonic").charAt(0));
		ProjectCompendium.APP.mainHB.enableHelpOnButton(pbHelp, "node.links-properties", ProjectCompendium.APP.mainHS); //$NON-NLS-1$
		oButtonPanel.addHelpButton(pbHelp);

		return oButtonPanel;
	}

	/**
	 * Set the default button for the parent dialog to be this panel's default button.
	 */
	public void setDefaultButton() {
		oParentDialog.getRootPane().setDefaultButton(pbCancel);
	}

	/**
	 * Initalize the dialog based on the given link settings.
	 * @param link com.compendium.core.datamodel.Link, the link whose setting to initialize for.
	 */
	public void setLink(Link link) {

		oLink = link;
		if (link != null) {
			try {
				String label = link.getFrom().getLabel();
				if (label.length() > 40){
					label = label.substring(0,35);
					label = label + "..."; //$NON-NLS-1$
				}
				tfFromNode.setText(label);
				label = link.getTo().getLabel();
				if (label.length() > 40){
					label = label.substring(0,35);
					label = label + "..."; //$NON-NLS-1$
				}

				tfToNode.setText(label);
				lblId2.setText(link.getId());
				lblAuthor2.setText(link.getAuthor());
				lblCreated2.setText(UIUtilities.getSimpleDateFormat("dd, MMMM, yyyy h:mm a").format(link.getCreationDate())); //$NON-NLS-1$
				lblModified2.setText(UIUtilities.getSimpleDateFormat("dd, MMMM, yyyy h:mm a").format(link.getModificationDate())); //$NON-NLS-1$
			}
			catch (Exception e) {
				ProjectCompendium.APP.displayError("Exception in 'UILinkPropertiesPanel.setLink'"+e.getMessage()); //$NON-NLS-1$
			}
		}
	}

	/**
	 * Process button pushes.
	 * @param evt, the associated ActionEvent object.
	 */
	public void actionPerformed(ActionEvent evt) {

		Object source = evt.getSource();
		if (source == pbCancel) {
			oParentDialog.onCancel();
		}
	}
}
