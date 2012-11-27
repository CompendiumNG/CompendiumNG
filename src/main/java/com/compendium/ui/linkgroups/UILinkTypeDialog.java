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

package com.compendium.ui.linkgroups;

import java.awt.*;
import java.awt.event.*;
import java.util.Vector;

import javax.help.CSH;
import javax.swing.*;

import com.compendium.LanguageProperties;
import com.compendium.ProjectCompendium;
import com.compendium.core.ICoreConstants;
import com.compendium.ui.*;
import com.compendium.ui.dialogs.*;

/**
 * UILinkTypeDialog defines the dialog that allows the user to create and manage link types.
 *
 * @author	Michelle Bachler
 */
public class UILinkTypeDialog extends UIDialog implements ActionListener, IUIConstants {

	/** The last director browsed to when looking for external references.*/
	private static String lastFileDialogDir = ""; //$NON-NLS-1$

	/** The choicebox with the line thickness options.*/
	private JComboBox 			cbDraw				= null;

	/** The current pane to put the dialog contents in.*/
	private Container				oContentPane 	= null;

	/** The button to close the dialog.*/
	private JButton					pbCancel 		= null;

	/** The button to save the stencil set.*/
	private JButton					pbSave 			= null;

	/** The button to open a file browser for the colour field.*/
	private JButton					pbBrowse		= null;

	/** Activates the help opeing to the appropriate section.*/
	private JButton					pbHelp		= null;

	/** The layout manager used.*/
	private	GridBagLayout 			gb 				= null;

	/** The constraints used.*/
	private	GridBagConstraints 		gc 				= null;

	/** The parent frame for this dialog.*/
	private JFrame					oParent 		= null;

	/** The stencil manager for this dialog.*/
	private UILinkGroupDialog		oManager 		= null;

	/** The counter for the gridbag layout y position.*/
	private int 					gridyStart 		= 0;

	/** The stencil set to edit / created.*/
	private UILinkType 	oType						= null;

	/** The text field to hold the link type label.*/
	private JTextField				txtName			= null;

	/** The text field to hold the link type colour.*/
	private JTextField				txtColour			= null;

	/** The text area to hold the link label.*/
	private UITextArea				txtLabel		= null;

	/** The choicebox listing the arrow head options.*/
	private JComboBox			cbArrows					= null;

	/** The choicebox with the link style options.*/
	private JComboBox 			cbLinkStyle					= null;

	/** The choicebox with the link line style options.*/
	private JComboBox 			cbLinkDashed				= null;

	private Color					oColour			= Color.black;

	private int						nThickness 		= 1;

	private int						nArrow	 		= ICoreConstants.ARROW_TO;

	private int						nStyle	 		= ICoreConstants.CURVED_LINK;

	private int						nDashed 		= ICoreConstants.PLAIN_LINE;

	/**
	 * Constructor. Initializes and sets up the dialog.
	 * Used when editing a stencils set.
	 *
	 * @param parent the frame that is the parent for this dialog.
	 * @param manager the parent managing dialog.
	 * @param oType the link type to edit / create.
	 */
	public UILinkTypeDialog(JFrame parent, UILinkGroupDialog manager, UILinkType oType) {

		super(parent, true);
		oParent = parent;
		oManager = manager;
		this.oType = oType;

		setTitle(LanguageProperties.getString(LanguageProperties.LINKGROUPS_BUNDLE, "UILinkTypeDialog.linkType")); //$NON-NLS-1$

		oContentPane = getContentPane();
		gb = new GridBagLayout();
		oContentPane.setLayout(gb);

		drawDialog();

		pack();
		setResizable(false);
		return;
	}

	/**
	 * Draws the contents of this dialog.
	 */
	private void drawDialog() {

		GridBagConstraints gc = new GridBagConstraints();
		gc.insets = new Insets(5,5,5,5);
		gc.anchor = GridBagConstraints.WEST;

		JLabel lblLabel = new JLabel(LanguageProperties.getString(LanguageProperties.LINKGROUPS_BUNDLE, "UILinkTypeDialog.name")); //$NON-NLS-1$
		lblLabel.setFont(new Font("Dialog", Font.PLAIN, 12)); //$NON-NLS-1$
		gc.gridy = gridyStart;
		gc.gridx = 0;
		gb.setConstraints(lblLabel, gc);
		oContentPane.add(lblLabel);

		txtName = new JTextField(oType.getName());
		txtName.setFont(new Font("Dialog", Font.PLAIN, 12)); //$NON-NLS-1$
		txtName.setColumns(20);
		txtName.setMargin(new Insets(2,2,2,2));
		txtName.setSize(txtName.getPreferredSize());
		gc.gridy = gridyStart;
		gridyStart++;
		gc.gridx = 1;
		gc.gridwidth = 2;
		gc.fill=GridBagConstraints.HORIZONTAL;
		gb.setConstraints(txtName, gc);
		oContentPane.add(txtName);

		gc.fill=GridBagConstraints.NONE;

		
		JLabel lblAuto = new JLabel(LanguageProperties.getString(LanguageProperties.LINKGROUPS_BUNDLE, "UILinkTypeDialog.label")); //$NON-NLS-1$
		lblAuto.setFont(new Font("Dialog", Font.PLAIN, 12)); //$NON-NLS-1$
		gc.gridy = gridyStart;
		gc.gridx = 0;
		gc.gridwidth = 1;
		gb.setConstraints(lblAuto, gc);
		oContentPane.add(lblAuto);

		txtLabel = new UITextArea(30, 20);
		txtLabel.setFont(new Font("Dialog", Font.PLAIN, 12)); //$NON-NLS-1$
		txtLabel.setText(oType.getLabel());
		txtLabel.setAutoscrolls(true);
		txtLabel.addFocusListener( new FocusListener() {
        	public void focusGained(FocusEvent e) {
 				txtLabel.setCaretPosition(txtLabel.getCaretPosition());
			}
            public void focusLost(FocusEvent e) {}
		});
		JScrollPane scrollpane2 = new JScrollPane(txtLabel);
		scrollpane2.setPreferredSize(new Dimension(100,50));
		gc.gridy = gridyStart;
		gridyStart++;
		gc.gridx = 1;
		gc.gridwidth=2;
		gc.fill=GridBagConstraints.HORIZONTAL;
		gb.setConstraints(scrollpane2, gc);
		oContentPane.add(scrollpane2);

		gc.fill=GridBagConstraints.NONE;

		JLabel lblColour = new JLabel(LanguageProperties.getString(LanguageProperties.LINKGROUPS_BUNDLE, "UILinkTypeDialog.linkColour")); //$NON-NLS-1$
		lblColour.setFont(new Font("Dialog", Font.PLAIN, 12)); //$NON-NLS-1$
		gc.gridy = gridyStart;
		gc.gridx = 0;
		gc.gridwidth = 1;
		gb.setConstraints(lblColour, gc);
		oContentPane.add(lblColour);

		oColour = oType.getColour();

		txtColour = new JTextField();
		txtColour.setBackground(oColour);
		txtColour.setFont(new Font("Dialog", Font.PLAIN, 12)); //$NON-NLS-1$
		txtColour.setColumns(4);
		txtColour.setEditable(false);
		txtColour.setMargin(new Insets(2,2,2,2));
		txtColour.setSize(txtColour.getPreferredSize());
		txtColour.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				int clickCount = e.getClickCount();
				if (clickCount == 2) {
					pbBrowse.doClick();
				}
			}
		});
		gc.gridx = 1;
		gc.gridwidth = 1;
		gc.fill=GridBagConstraints.HORIZONTAL;
		gb.setConstraints(txtColour, gc);
		oContentPane.add(txtColour);

		pbBrowse = new UIButton(LanguageProperties.getString(LanguageProperties.LINKGROUPS_BUNDLE, "UILinkTypeDialog.choose")); //$NON-NLS-1$
		pbBrowse.setToolTipText(LanguageProperties.getString(LanguageProperties.LINKGROUPS_BUNDLE, "UILinkTypeDialog.chooseTip")); //$NON-NLS-1$
		pbBrowse.addActionListener(this);
		gc.gridy = gridyStart;
		gridyStart++;
		gc.gridx = 2;
		gc.gridwidth = 1;
		gc.fill=GridBagConstraints.NONE;
		gb.setConstraints(pbBrowse, gc);
		oContentPane.add(pbBrowse);

		JLabel label = new JLabel(LanguageProperties.getString(LanguageProperties.LINKGROUPS_BUNDLE, "UILinkTypeDialog.linkThickness")); //$NON-NLS-1$
		label.setFont(new Font("Dialog", Font.PLAIN, 12)); //$NON-NLS-1$
		gc.gridy = gridyStart;
		gc.gridx = 0;
		gc.gridwidth = 1;
		gb.setConstraints(label, gc);
		oContentPane.add(label);

		gc.gridy = gridyStart;
		gridyStart++;
		gc.gridx = 1;
		gc.gridwidth = 2;
		gc.fill=GridBagConstraints.NONE;
		JPanel panel = createDrawChoiceBox();
		gb.setConstraints(panel, gc);
		oContentPane.add(panel);
	
		label = new JLabel(LanguageProperties.getString(LanguageProperties.LINKGROUPS_BUNDLE, "UILinkTypeDialog.linkArrow")); //$NON-NLS-1$
		label.setFont(new Font("Dialog", Font.PLAIN, 12)); //$NON-NLS-1$
		gc.gridy = gridyStart;
		gc.gridx = 0;
		gc.gridwidth = 1;
		gb.setConstraints(label, gc);
		oContentPane.add(label);

		gc.gridy = gridyStart;
		gridyStart++;
		gc.gridx = 1;
		gc.gridwidth = 2;
		gc.fill=GridBagConstraints.NONE;
		JPanel panel2 = createArrowChoiceBox();
		gb.setConstraints(panel2, gc);
		oContentPane.add(panel2);
		
		label = new JLabel(LanguageProperties.getString(LanguageProperties.LINKGROUPS_BUNDLE, "UILinkTypeDialog.linkStyle")); //$NON-NLS-1$
		label.setFont(new Font("Dialog", Font.PLAIN, 12)); //$NON-NLS-1$
		gc.gridy = gridyStart;
		gc.gridx = 0;
		gc.gridwidth = 1;
		gb.setConstraints(label, gc);
		oContentPane.add(label);

		gc.gridy = gridyStart;
		gridyStart++;
		gc.gridx = 1;
		gc.gridwidth = 2;
		gc.fill=GridBagConstraints.NONE;
		JPanel panel3 = createLinkStyleChoiceBox();
		gb.setConstraints(panel3, gc);
		oContentPane.add(panel3);

		label = new JLabel(LanguageProperties.getString(LanguageProperties.LINKGROUPS_BUNDLE, "UILinkTypeDialog.linkDashed")); //$NON-NLS-1$
		label.setFont(new Font("Dialog", Font.PLAIN, 12)); //$NON-NLS-1$
		gc.gridy = gridyStart;
		gc.gridx = 0;
		gc.gridwidth = 1;
		gb.setConstraints(label, gc);
		oContentPane.add(label);

		gc.gridy = gridyStart;
		gridyStart++;
		gc.gridx = 1;
		gc.gridwidth = 2;
		gc.fill=GridBagConstraints.NONE;
		JPanel panel4 = createLinkDashedChoiceBox();
		gb.setConstraints(panel4, gc);
		oContentPane.add(panel4);

		JSeparator sep = new JSeparator();
		gc.gridy = gridyStart;
		gridyStart++;
		gc.gridx = 0;
		gc.gridwidth = 3;
		gc.anchor = GridBagConstraints.WEST;
		gc.fill = GridBagConstraints.BOTH;
		gb.setConstraints(sep, gc);
		oContentPane.add(sep);

		gc.gridwidth=1;
		gc.fill = GridBagConstraints.NONE;

		pbSave = new UIButton(LanguageProperties.getString(LanguageProperties.LINKGROUPS_BUNDLE, "UILinkTypeDialog.save")); //$NON-NLS-1$
		pbSave.addActionListener(this);
		gc.gridy = gridyStart;
		gc.gridx = 0;
		gc.gridwidth = 1;
		gc.weightx=1.0;
		gc.anchor = GridBagConstraints.WEST;
		gb.setConstraints(pbSave, gc);
		oContentPane.add(pbSave);

		pbCancel = new UIButton(LanguageProperties.getString(LanguageProperties.LINKGROUPS_BUNDLE, "UILinkTypeDialog.canel")); //$NON-NLS-1$
		pbCancel.addActionListener(this);
		gc.gridy = gridyStart;
		gc.gridx = 1;
		gc.gridwidth = 1;
		gc.weightx=2.0;
		gc.anchor = GridBagConstraints.EAST;
		gb.setConstraints(pbCancel, gc);
		oContentPane.add(pbCancel);

		pbHelp = new UIButton(LanguageProperties.getString(LanguageProperties.LINKGROUPS_BUNDLE, "UILinkTypeDialog.help")); //$NON-NLS-1$
		ProjectCompendium.APP.mainHB.enableHelpOnButton(pbHelp, "node.linkgroups", ProjectCompendium.APP.mainHS); //$NON-NLS-1$
		gc.gridx = 2;
		gc.gridwidth=1;
		gc.weightx=1.0;
		gc.anchor = GridBagConstraints.EAST;
		gb.setConstraints(pbHelp, gc);
		oContentPane.add(pbHelp);
	}

	/**
	 * Create a choicebox for line thickness options and return the panel it is in.
	 * @return JPanel, the panel holding the new choicebox for the line thickness options.
	 */
	private JPanel createDrawChoiceBox() {

		JPanel drawPanel = new JPanel(new BorderLayout());
		CSH.setHelpIDString(drawPanel,"node.linkgroups"); //$NON-NLS-1$

		cbDraw = new JComboBox();
        cbDraw.setOpaque(true);
		cbDraw.setEditable(false);
		cbDraw.setEnabled(true);
		cbDraw.setMaximumRowCount(10);
		cbDraw.setFont( new Font("Dialog", Font.PLAIN, 10 )); //$NON-NLS-1$

		cbDraw.addItem(new String("1 px")); //$NON-NLS-1$
		cbDraw.addItem(new String("2 px")); //$NON-NLS-1$
		cbDraw.addItem(new String("3 px")); //$NON-NLS-1$
		cbDraw.addItem(new String("4 px")); //$NON-NLS-1$
		cbDraw.addItem(new String("5 px")); //$NON-NLS-1$
		cbDraw.addItem(new String("6 px")); //$NON-NLS-1$
		cbDraw.addItem(new String("7 px")); //$NON-NLS-1$
		cbDraw.addItem(new String("8 px")); //$NON-NLS-1$
		cbDraw.addItem(new String("9 px")); //$NON-NLS-1$
		cbDraw.addItem(new String("10 px")); //$NON-NLS-1$

		cbDraw.validate();

		cbDraw.setSelectedIndex(oType.getLinkWeight()-1);

		DefaultListCellRenderer drawRenderer = new DefaultListCellRenderer() {
			public Component getListCellRendererComponent(
   		     	JList list,
   		        Object value,
            	int modelIndex,
            	boolean isSelected,
            	boolean cellHasFocus)
            {
				if (list != null) {
	 		 		if (isSelected) {
						setBackground(list.getSelectionBackground());
						setForeground(list.getSelectionForeground());
					}
					else {
						setBackground(list.getBackground());
						setForeground(list.getForeground());
					}
				}

				setText((String) value);
				return this;
			}
		};

		cbDraw.setRenderer(drawRenderer);

		ActionListener drawActionListener = new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
 				int ind = cbDraw.getSelectedIndex();

				if (ind == 0)
					setScribbleThickness(1);
				else if (ind == 1)
					setScribbleThickness(2);
				else if (ind == 2)
					setScribbleThickness(3);
				else if (ind == 3)
					setScribbleThickness(4);
				else if (ind == 4)
					setScribbleThickness(5);
				else if (ind == 5)
					setScribbleThickness(6);
				else if (ind == 6)
					setScribbleThickness(7);
				else if (ind == 7)
					setScribbleThickness(8);
				else if (ind == 8)
					setScribbleThickness(9);
				else if (ind == 9)
					setScribbleThickness(10);
         	}
		};
        cbDraw.addActionListener(drawActionListener);

		drawPanel.add(new JLabel(" "), BorderLayout.WEST); //$NON-NLS-1$
		drawPanel.add(cbDraw, BorderLayout.CENTER);
		return drawPanel;
	}	
	
 	/**
	 * Set the scribble pad line thickness to the given integer.
	 * @param nThickness, the line thickness for the scribble layer.
	 */
	private void setScribbleThickness(int nThickness) {
		this.nThickness = nThickness;
	}
	
	
	/**
	 * Create the arrow head choicebox.
	 */
	private JPanel createArrowChoiceBox() {
		
		JPanel drawPanel = new JPanel(new BorderLayout());
		CSH.setHelpIDString(drawPanel,"toolbars.formatlink"); //$NON-NLS-1$

		cbArrows = new JComboBox();
		cbArrows.setToolTipText(LanguageProperties.getString(LanguageProperties.TOOLBARS_BUNDLE, "UIToolBarFormatLink.selectArrow")); //$NON-NLS-1$
		cbArrows.setOpaque(true);
		cbArrows.setEditable(false);
		cbArrows.setEnabled(true);
		cbArrows.setMaximumRowCount(4);
		cbArrows.setFont( new Font("Dialog", Font.PLAIN, 10 )); //$NON-NLS-1$

		Vector arrows = new Vector(4);
		arrows.insertElementAt(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UILinkEditDialog.noArrows"), 0); //$NON-NLS-1$
		arrows.insertElementAt(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UILinkEditDialog.fromTo"), 1); //$NON-NLS-1$
		arrows.insertElementAt(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UILinkEditDialog.toFfrom"), 2); //$NON-NLS-1$
		arrows.insertElementAt(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UILinkEditDialog.bothWays"),3); //$NON-NLS-1$
		DefaultComboBoxModel comboModel = new DefaultComboBoxModel(arrows);
		cbArrows.setModel(comboModel);
		//cbArrows.setSelectedIndex(1);
		cbArrows.setSelectedIndex(oType.getArrowType());
		
		DefaultListCellRenderer comboRenderer = new DefaultListCellRenderer() {
			public Component getListCellRendererComponent(
   		     	JList list,
   		        Object value,
            	int modelIndex,
            	boolean isSelected,
            	boolean cellHasFocus)
            {
 		 		if (isSelected) {
					setBackground(list.getSelectionBackground());
					setForeground(list.getSelectionForeground());
				}
				else {
					setBackground(list.getBackground());
					setForeground(list.getForeground());
				}

				setText((String) value);

				return this;
			}
		};
		cbArrows.setRenderer(comboRenderer);

		cbArrows.addActionListener(new ActionListener() {
	       	public void actionPerformed(ActionEvent e) {	       	 
	       		nArrow = cbArrows.getSelectedIndex();
			}	
		});
		
		drawPanel.add(new JLabel(" "), BorderLayout.WEST); //$NON-NLS-1$
		drawPanel.add(cbArrows, BorderLayout.CENTER);
		return drawPanel;
	}
	
	
	/**
	 * Create a choicebox for link style options and return the panel it is in.
	 * @return JPanel the panel holding the new choicebox for the link style options.
	 */
	private JPanel createLinkStyleChoiceBox() {

		JPanel drawPanel = new JPanel(new BorderLayout());
		CSH.setHelpIDString(drawPanel,"toolbars.formatlink"); //$NON-NLS-1$

		cbLinkStyle = new JComboBox();
		cbLinkStyle.setToolTipText(LanguageProperties.getString(LanguageProperties.TOOLBARS_BUNDLE, "UIToolBarFormatLink.selectStlye")); //$NON-NLS-1$
		cbLinkStyle.setOpaque(true);
		cbLinkStyle.setEditable(false);
		cbLinkStyle.setEnabled(true);
		cbLinkStyle.setMaximumRowCount(10);
		cbLinkStyle.setFont( new Font("Dialog", Font.PLAIN, 10 )); //$NON-NLS-1$


		cbLinkStyle.addItem(new String(LanguageProperties.getString(LanguageProperties.TOOLBARS_BUNDLE, "UIToolBarFormatLink.straightLink"))); //$NON-NLS-1$
		cbLinkStyle.addItem(new String(LanguageProperties.getString(LanguageProperties.TOOLBARS_BUNDLE, "UIToolBarFormatLink.curvedLink"))); //$NON-NLS-1$
		cbLinkStyle.addItem(new String(LanguageProperties.getString(LanguageProperties.TOOLBARS_BUNDLE, "UIToolBarFormatLink.squaredLink"))); //$NON-NLS-1$

		cbLinkStyle.validate();

		//cbLinkStyle.setSelectedIndex(0);
		cbLinkStyle.setSelectedIndex(oType.getLinkStyle());

		DefaultListCellRenderer drawRenderer = new DefaultListCellRenderer() {
			public Component getListCellRendererComponent(
   		     	JList list,
   		        Object value,
            	int modelIndex,
            	boolean isSelected,
            	boolean cellHasFocus)
            {
				if (list != null) {
	 		 		if (isSelected) {
						setBackground(list.getSelectionBackground());
						setForeground(list.getSelectionForeground());
					}
					else {
						setBackground(list.getBackground());
						setForeground(list.getForeground());
					}
				}

				setText((String) value);
				return this;
			}
		};

		cbLinkStyle.setRenderer(drawRenderer);

		ActionListener drawActionListener = new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
 				nStyle = cbLinkStyle.getSelectedIndex();
         	}
		};
		cbLinkStyle.addActionListener(drawActionListener);

		drawPanel.add(new JLabel(" "), BorderLayout.WEST); //$NON-NLS-1$
		drawPanel.add(cbLinkStyle, BorderLayout.CENTER);
		return drawPanel;
	}	
	
	/**
	 * Create a choicebox for link line style options and return the panel it is in.
	 * @return JPanel the panel holding the new choicebox for the link style options.
	 */
	private JPanel createLinkDashedChoiceBox() {

		JPanel drawPanel = new JPanel(new BorderLayout());
		CSH.setHelpIDString(drawPanel,"toolbars.formatlink"); //$NON-NLS-1$

		cbLinkDashed = new JComboBox();
		cbLinkDashed.setToolTipText(LanguageProperties.getString(LanguageProperties.TOOLBARS_BUNDLE, "UIToolBarFormatLink.selectDashed")); //$NON-NLS-1$
		cbLinkDashed.setOpaque(true);
		cbLinkDashed.setEditable(false);
		cbLinkDashed.setEnabled(true);
		cbLinkDashed.setMaximumRowCount(10);
		cbLinkDashed.setFont( new Font("Dialog", Font.PLAIN, 10 )); //$NON-NLS-1$

		cbLinkDashed.addItem(new String(LanguageProperties.getString(LanguageProperties.TOOLBARS_BUNDLE, "UIToolBarFormatLink.plainLine"))); //$NON-NLS-1$
		cbLinkDashed.addItem(new String(LanguageProperties.getString(LanguageProperties.TOOLBARS_BUNDLE, "UIToolBarFormatLink.largeDashes"))); //$NON-NLS-1$
		cbLinkDashed.addItem(new String(LanguageProperties.getString(LanguageProperties.TOOLBARS_BUNDLE, "UIToolBarFormatLink.smallDashes"))); //$NON-NLS-1$

		cbLinkDashed.validate();

		//cbLinkDashed.setSelectedIndex(0);
		cbLinkDashed.setSelectedIndex(oType.getLinkDashed());

		DefaultListCellRenderer drawRenderer = new DefaultListCellRenderer() {
			public Component getListCellRendererComponent(
   		     	JList list,
   		        Object value,
            	int modelIndex,
            	boolean isSelected,
            	boolean cellHasFocus)
            {
				if (list != null) {
	 		 		if (isSelected) {
						setBackground(list.getSelectionBackground());
						setForeground(list.getSelectionForeground());
					}
					else {
						setBackground(list.getBackground());
						setForeground(list.getForeground());
					}
				}

				setText((String) value);
				return this;
			}
		};

		cbLinkDashed.setRenderer(drawRenderer);

		ActionListener drawActionListener = new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
				nDashed = cbLinkDashed.getSelectedIndex();
         	}
		};
		cbLinkDashed.addActionListener(drawActionListener);

		drawPanel.add(new JLabel(" "), BorderLayout.WEST); //$NON-NLS-1$
		drawPanel.add(cbLinkDashed, BorderLayout.CENTER);
		return drawPanel;
	}	
			
	/**
	 * Handle action events coming from the buttons.
	 * @param evt the associated ActionEvent.
	 */
	public void actionPerformed(ActionEvent evt) {
		Object source = evt.getSource();

		// Handle button events
		if (source instanceof JButton) {
			if (source == pbBrowse) {
				onBrowse();
			}
			else if (source == pbSave) {
				onSave();
			}
			else if (source == pbCancel) {
				onCancel();
			}
		}
	}

	/**
	 * Open the color chooser dialog for the user to select a link colour.
	 */
	private void onBrowse() {

		UIColorChooserDialog dlg = new UIColorChooserDialog(ProjectCompendium.APP, oColour);
		dlg.setVisible(true);
		Color colour = dlg.getColour();
		dlg.dispose();
		if (colour != null) {
			txtColour.setBackground(colour);
			oColour = colour;
		}
	}

	/**
	 * Open the dialog to create a new stencil set.
	 */
	public void onSave()  {
		String oldName = oType.getName();
		String sName = txtName.getText();
		if (sName.equals("")) { //$NON-NLS-1$
			ProjectCompendium.APP.displayMessage(LanguageProperties.getString(LanguageProperties.LINKGROUPS_BUNDLE, "UILinkTypeDialog.message1"), LanguageProperties.getString(LanguageProperties.LINKGROUPS_BUNDLE, "UILinkTypeDialog.message1Title")); //$NON-NLS-1$ //$NON-NLS-2$
			txtName.requestFocus();
			return;
		}
		else if (!sName.equals(oldName) && oManager.checkName(sName)) {
			ProjectCompendium.APP.displayMessage(LanguageProperties.getString(LanguageProperties.LINKGROUPS_BUNDLE, "UILinkTypeDialog.message2"), LanguageProperties.getString(LanguageProperties.LINKGROUPS_BUNDLE, "UILinkTypeDialog.message2Title")); //$NON-NLS-1$ //$NON-NLS-2$
			txtName.requestFocus();
			return;
		}
		String sLabel = txtLabel.getText();

		oType.setName(sName);
		oType.setLabel(sLabel);
		oType.setColour(oColour);
		oType.setLinkWeight(this.nThickness);
		oType.setLinkStyle(nStyle);
		oType.setLinkDashed(nDashed);
		oType.setArrowType(nArrow);

		oManager.addLinkType(oType);
		onCancel();
	}

	/**
	 * Handle the enter key action. Override superclass to do nothing.
	 */
	public void onEnter() {}
}
