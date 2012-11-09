/********************************************************************************
 *                                                                              *
 *  (c) Copyright 2009 Verizon Communications USA and The Open University UK    *
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
import java.io.*;

import java.awt.*;
import java.awt.image.*;
import java.awt.Container;
import java.awt.Color;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.text.Document;
import javax.swing.text.Keymap;

import com.compendium.ProjectCompendium;
import com.compendium.ui.*;
import com.compendium.core.datamodel.*;

/**
 * Creates a font dialog for changing the font used for node labels
 *
 * @author	Michelle Bachler
 */
public class UIFontDialog extends UIDialog implements ActionListener {

	/** The current map.*/
	private UIViewPane		oViewPane			= null;

	/** The current list.*/
	private UIList			oList				= null;

	/** The button to save the user setting and cloe the dialog.*/
	private UIButton			pbOK				= null;

	/** The button to apply the user setting without closing the dialog.*/
	private UIButton			pbApply			= null;

	/** The button to cancel the dialog.*/
	private UIButton			pbCancel		= null;

	/** The button to open the relevant help.*/
	private UIButton			pbHelp			= null;

	/** The scrollpane for the test label.*/
	JScrollPane 			scrollpane			= null;

	/** The font object from the font settings chosen.*/
 	protected Font oFont = new Font ("Serif", Font.PLAIN, 10);

	/** The scrollpane for the font list.*/
 	private JScrollPane fontscroll, sizescroll;

	/** The renderer for the font list.*/
	private FontCellRenderer fontListRenderer;

	/** The renderer for the fon size list.*/
	private SizeCellRenderer sizeListRenderer;

	/** The font list.*/
	private UINavList fontlist = null;

	/** The font size list.*/
	private UINavList sizelist = null;

	/** Stores the font size chosen.*/
	private int 			fontsize 			= 12;

	/** Stores the font face chosen.*/
	private String 			fontface 			= "SAN SERIF";

	/** Stores the font style chosen.*/
	private int 			fontstyle 			= Font.PLAIN;

	/** the text area to display the default label in the chosen font settings.*/
	private JTextArea 		label 				= null;

	/** Turn on bolding.*/
  	protected JCheckBox bold		= null;

	/** Turn on italics.*/
  	protected JCheckBox italic	 	= null;

	/** Activate automatic opening of details box after so many characters in the label.*/
  	protected JCheckBox detail 		= null;

	/** Stores the width for wrapping the label.*/
	private int 			labelWidth 			= 15;

	/** Stored the length of the label for automatic detail box popup.*/
	private int 			labelLength 		= 100;

	/** The label for the width field.*/
	private JLabel			lblWidth 			= null;

	/** The label for the length fiedl.*/
	private JLabel			lblLength 			= null;

	/** the field to enter the width for wrapping the label.*/
	private JTextField		labelWidthField 	= null;

	/** The field to enter the length of the label for automatic detail box popup.*/
	private JTextField		labelLengthField 	= null;

	/** Stores if the NodeContentDialog should automatically open when the label reaches a specified length.*/
	private boolean			detailPopup 		= false;

	/**
	 * Constructor.
	 * @param parent, the parent frame for this dialog.
	 * @param viewPane, the current map.
	 * @param oFont, the last font seting saved.
	 * @param width, the node label wrap width.
	 * @param length, the length of the node label before automatically opening the detail box.
	 * @param detail, whether to automatically open the contents dialog and focus the detail box.
	 */
	public UIFontDialog(JFrame parent, UIViewPane viewPane, Font oFont, int width, int length, boolean detail) {

		super(parent, false);
		oViewPane = viewPane;
		labelWidth = width;
		labelLength = length;
		detailPopup = detail;
		this.oFont = oFont;
		fontface = oFont.getName();
		fontsize = oFont.getSize();
		fontstyle = oFont.getStyle();

		initDialog();
	}


	/**
	 * Constructor.
	 * @param parent, the parent frame for this dialog.
	 * @param list, the current map.
	 * @param oFont, the last font seting saved.
	 * @param width, the node label wrap width.
	 * @param length, the length of the node label before automatically opening the detail box.
	 * @param detail, whether to automatically open the contents dialog and focus the detail box.
	 */
	public UIFontDialog(JFrame parent, UIList list, Font oFont, int width, int length, boolean detail) {

		super(parent, false);
		oList = list;
		labelWidth = width;
		labelLength = length;
		detailPopup = detail;
		this.oFont = oFont;
		fontface = oFont.getName();
		fontsize = oFont.getSize();
		fontstyle = oFont.getStyle();

		initDialog();
	}

	/**
	 * Draw and initialize the dialog interface elements.
	 */
	public void initDialog() {

		// set title and background
		setResizable(false);
		setTitle("Node/List Label Preferences");

		JPanel main = new JPanel();
		main.setLayout(new BorderLayout());

		GridLayout grid = new GridLayout(1,2);
		grid.setHgap(5);
		JPanel choices = new JPanel(grid);
		choices.setBorder( new EmptyBorder( 5,5,5,5 ) );

		createFontList();
		createSizeList();

		choices.add(fontscroll);
 		choices.add(sizescroll);

		//main.add(choices, BorderLayout.NORTH);

		JPanel checks = new JPanel();

		ItemListener fontItemListener = new ItemListener() {

			public void itemStateChanged(ItemEvent event) {

				if (bold.isSelected() && italic.isSelected())
            		fontstyle = Font.BOLD+Font.ITALIC;
				else if (bold.isSelected() && !italic.isSelected())
					fontstyle = Font.BOLD;
				else if (!bold.isSelected() && italic.isSelected())
					fontstyle = Font.ITALIC;
				else if (!bold.isSelected() && !italic.isSelected())
					fontstyle = Font.PLAIN;

				setLabelFont();
			}
		};

	    bold = new JCheckBox ("bold");
    	bold.setSelected(oFont.isBold());
    	bold.setFont(new Font ("TimesRoman", Font.BOLD, 12));
    	bold.addItemListener(fontItemListener);
		checks.add(bold);

    	italic = new JCheckBox ("italic");
    	italic.setSelected(oFont.isItalic());
    	italic.setFont (new Font ("TimesRoman", Font.ITALIC, 12));
    	italic.addItemListener(fontItemListener);
		checks.add(italic);

		JPanel labelpanel = new JPanel(new BorderLayout());
		labelpanel.setBorder( new EmptyBorder( 5,5,5,5 ) );

		label = new JTextArea("Test Script");
		label.setPreferredSize(new Dimension(300, 50));
		scrollpane = new JScrollPane( label );
		label.setEditable(false);
		label.setFont( oFont );
		labelpanel.add( scrollpane, BorderLayout.CENTER );

		JPanel center = new JPanel( new BorderLayout() );
		center.setBorder(new TitledBorder(new EtchedBorder(),
                    "Label Font",
                    TitledBorder.LEFT,
                    TitledBorder.TOP,
					new Font("Dialog", Font.BOLD, 12) ));

		center.add(choices, BorderLayout.NORTH);
		center.add(checks, BorderLayout.CENTER);
		center.add(labelpanel, BorderLayout.SOUTH);
		main.add(center, BorderLayout.NORTH);

		// LABEL WIDTH
		JPanel widthPanel = new JPanel();
		widthPanel.setBorder(new TitledBorder(new EtchedBorder(),
                    "View Node Properties",
                    TitledBorder.LEFT,
                    TitledBorder.TOP,
					new Font("Dialog", Font.BOLD, 12) ));

		GridBagLayout gb = new GridBagLayout();
		widthPanel.setLayout(gb);

		GridBagConstraints gc = new GridBagConstraints();
		gc.insets = new Insets(5,5,5,5);

		lblWidth = new JLabel("Node Label wrap width ");
		lblWidth.setToolTipText("The label width at which you wish the label word wrapping to apply");
		gc.gridy = 0;
		gc.gridx = 0;
		gc.anchor = GridBagConstraints.WEST;
		gb.setConstraints(lblWidth, gc);
		widthPanel.add(lblWidth);

		labelWidthField = new JTextField(new Integer(labelWidth).toString());
		labelWidthField.setColumns(5);
		gc.gridy = 0;
		gc.gridx = 1;
		gc.anchor = GridBagConstraints.WEST;
		gb.setConstraints(labelWidthField, gc);
		widthPanel.add(labelWidthField);

    	detail = new JCheckBox ("Enable node detail popup");
		detail.setToolTipText("Enable the auto-popup of the details window after the specified number of characters");
 		gc.gridy = 1;
		gc.gridx = 0;
		gc.gridwidth=2;
		gc.anchor = GridBagConstraints.WEST;
		gb.setConstraints(detail, gc);
		widthPanel.add(detail);

		gc.gridwidth=1;

		// LABEL LENGTH
		lblLength = new JLabel("Label length for details popup");
		lblLength.setToolTipText("The label length at which you wish to automatically popup the details window when typing");
		gc.gridy = 2;
		gc.gridx = 0;
		gb.setConstraints(lblLength, gc);
		widthPanel.add(lblLength);

		labelLengthField = new JTextField(new Integer(labelLength).toString());
		labelLengthField.setColumns(5);
		gc.gridy = 2;
		gc.gridx = 1;
		gb.setConstraints(labelLengthField, gc);
		widthPanel.add(labelLengthField);

		lblLength.setEnabled(false);
		labelLengthField.setEditable(false);
		labelLengthField.setEnabled(false);

		main.add(widthPanel, BorderLayout.CENTER);

		// ADD BUTTONS
		UIButtonPanel oButtonPanel = new UIButtonPanel();

		pbOK = new UIButton("OK");
		pbOK.setMnemonic(KeyEvent.VK_O);
		pbOK.addActionListener(this);
		getRootPane().setDefaultButton(pbOK);
		oButtonPanel.addButton(pbOK);

		pbCancel = new UIButton("Cancel");
		pbCancel.setMnemonic(KeyEvent.VK_C);
		pbCancel.addActionListener(this);
		oButtonPanel.addButton(pbCancel);

		pbApply = new UIButton("Apply");
		pbApply.setMnemonic(KeyEvent.VK_A);
		pbApply.addActionListener(this);
		oButtonPanel.addButton(pbApply);

		pbHelp = new UIButton("Help");
		pbHelp.setMnemonic(KeyEvent.VK_H);
		ProjectCompendium.APP.mainHB.enableHelpOnButton(pbHelp, "node.preferences", ProjectCompendium.APP.mainHS);
		oButtonPanel.addHelpButton(pbHelp);

		main.add(oButtonPanel, BorderLayout.SOUTH);

		Container oContentPane = getContentPane();
		oContentPane.add(main, BorderLayout.CENTER);
		//oContentPane.add(oButtonPanel, BorderLayout.SOUTH);

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent evt) {
				onCancel();
			}
		});

    	detail.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent event) {

				if (detail.isSelected()) {
            		lblLength.setEnabled(true);
					labelLengthField.setEnabled(true);
					labelLengthField.setEditable(true);
				}
				else {
					lblLength.setEnabled(false);
					labelLengthField.setEditable(false);
					labelLengthField.setEnabled(false);
				}
			}
		});

		detail.setSelected(detailPopup);

		pack();
		setResizable(false);
	}


	/**
	 * This class draws the elements of the font list.
	 */
	public class FontCellRenderer extends JLabel implements ListCellRenderer {

	  	protected Border noFocusBorder;

		FontCellRenderer() {
        	super();
			noFocusBorder = new EmptyBorder(1, 1, 1, 1);
			setOpaque(true);
			setBorder(noFocusBorder);
		}

		public Component getListCellRendererComponent(
        	JList list,
            Object value,
            int modelIndex,
            boolean isSelected,
            boolean cellHasFocus)
            {

            //int index = modelIndex;

			//System.out.println("value = "+value);
			String text = (String) value;

			String font = (String)value;
			/*if (font.equals("Estrangelo Edessa") || font.equals("Latha")
					|| font.equals("Mangal") || font.equals("Marlett")
					|| font.equals("MS Outlook") || font.equals("Symbol")
					|| font.equals("Webdings") || font.equals("Wingdings")
					|| font.equals("Wingdings 2") || font.equals("Wingdings 3") ) {
	   			setFont( new Font("Sans Serif", Font.PLAIN, 12) );
			}
			else*/
	   			setFont( new Font((String)value, Font.PLAIN, 12) );

 	 		if (isSelected) {
				setBackground(list.getSelectionBackground());
				setForeground(list.getSelectionForeground());
			}
			else {
				setBackground(list.getBackground());
				setForeground(list.getForeground());
			}

			for(int i = 0 ; i < 5; i++) {
            	text += " ";
      		}

			setBorder((cellHasFocus) ? UIManager.getBorder("List.focusCellHighlightBorder") : noFocusBorder);
			setText(text);
			return this;
		}
	}

	/**
	 * Create the list of available fonts.
	 */
	private void createFontList() {

   	 	String[] fonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();

		fontlist = new UINavList(fonts);

        fontListRenderer = new FontCellRenderer();
		fontlist.setCellRenderer(fontListRenderer);
		fontscroll = new JScrollPane(fontlist);
		fontscroll.setPreferredSize(new Dimension(200, 150));

	   	fontlist.setSelectedValue((Object)oFont.getName(), true);

		MouseListener fontmouse = new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 1) {
					fontface = (String)fontlist.getSelectedValue();
					setLabelFont();
				}
			}
		};
		KeyListener fontkey = new KeyAdapter() {
           	public void keyPressed(KeyEvent e) {
				if ((e.getKeyCode() == KeyEvent.VK_ENTER) && (e.getModifiers() == 0)) {
					fontface = (String)fontlist.getSelectedValue();
					setLabelFont();
				}
			}
		};

		fontlist.addKeyListener(fontkey);
		fontlist.addMouseListener(fontmouse);
	}

	/**
	 * This class is used to draw the size list elements.
	 */
	public class SizeCellRenderer extends JLabel implements ListCellRenderer {

	  	protected Border noFocusBorder;

		SizeCellRenderer() {
        	super();
			noFocusBorder = new EmptyBorder(1, 1, 1, 1);
			setOpaque(true);
			setBorder(noFocusBorder);
		}

		public Component getListCellRendererComponent(
        	JList list,
            Object value,
            int modelIndex,
            boolean isSelected,
            boolean cellHasFocus)
            {

            //int index = modelIndex;

   			//setFont( new Font((String)value, Font.PLAIN, 12) );

	 		if (isSelected) {
				setBackground(list.getSelectionBackground());
				setForeground(list.getSelectionForeground());
			}
			else {
				setBackground(list.getBackground());
				setForeground(list.getForeground());
			}

			setBorder((cellHasFocus) ? UIManager.getBorder("List.focusCellHighlightBorder") : noFocusBorder);
			setText(" "+(String)value+"  ");
			return this;
		}
	}

	/**
	 * Create the font size list.
	 */
	private void createSizeList() {

  	 	String[] sizes = {"8","10","12","14","16","18","20","22","24","26","28","30"};

 		sizelist = new UINavList(sizes);
        sizeListRenderer = new SizeCellRenderer();
		sizelist.setCellRenderer(sizeListRenderer);
		sizescroll = new JScrollPane(sizelist);
		sizescroll.setPreferredSize(new Dimension(100, 150));

  		sizelist.setSelectedValue((Object) new Integer(oFont.getSize()).toString(), true);

		MouseListener sizemouse = new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 1) {
					fontsize = new Integer((String)sizelist.getSelectedValue()).intValue();
					setLabelFont();
				}
			}
		};
		KeyListener sizekey = new KeyAdapter() {
           	public void keyPressed(KeyEvent e) {
				if ((e.getKeyCode() == KeyEvent.VK_ENTER) && (e.getModifiers() == 0)) {
					fontsize = new Integer((String)sizelist.getSelectedValue()).intValue();
					setLabelFont();
				}
			}
		};

		sizelist.addKeyListener(sizekey);
		sizelist.addMouseListener(sizemouse);
	}

	/**
	 * Set the font to be used for the label showing the example text for a given font.
	 */
	private void setLabelFont() {

		label.setFont(new Font(fontface, fontstyle, fontsize));
		label.repaint();
		label.validate();

		scrollpane.repaint();
	}

	/**
	 * Handle button push events.
	 * @param evt, the assoicated ActionEvent object.
	 */
	public void actionPerformed(ActionEvent evt) {

		Object source = evt.getSource();
		if (source == pbOK) {
			onApply();
			onCancel();
		}
		else if (source == pbApply) {
			onApply();
		}
		else if (source == pbCancel) {
			onCancel();
		}
	}

	/**
	 * Save the changes made to the properties.
	 */
	private void onApply() {
		try {
			String width = labelWidthField.getText();
			int lWidth = (new Integer(width)).intValue();

			detailPopup = detail.isSelected();

			String length = labelLengthField.getText();
			int lLength = (new Integer(length)).intValue();

			//ProjectCompendium.APP.setLabelProperties( new Font(fontface, fontstyle, fontsize), lWidth, lLength, detailPopup );
			
			// Begin edit - Lakshmi - 5/22/06
			//refresh outline view label
			//if(ProjectCompendium.APP.oStencilManager.getOutlineView() != null){
			//	UIViewOutline.me.refreshTree();
			//}
			//end edit
		}
		catch(NumberFormatException e) {
			return;
		}
	}
}
