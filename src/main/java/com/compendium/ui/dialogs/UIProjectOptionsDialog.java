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
import java.io.*;
import java.sql.SQLException;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;

import com.compendium.LanguageProperties;
import com.compendium.ProjectCompendium;
import com.compendium.core.datamodel.IModel;
import com.compendium.core.datamodel.Model;
import com.compendium.ui.*;

/**
 * This class draws the options dialog and handles storing/setting the user's chosen options.
 *
 * @author	Michelle Bachler
 */
public class UIProjectOptionsDialog extends UIDialog implements ActionListener {

	/** The parent frame for this dialog.*/
	private Container		oParent					= null;

	/** The button to cancel this dialog.*/
	public	UIButton		pbCancel				= null;

	/** The button to update the user settings.*/
	public	UIButton		pbUpdate				= null;
	
	/** The field to hold the Linked Files folder path.*/
	public JTextField		txtLinkedFilesPath	= null;

	/** Activates the help opening to the appropriate section.*/
	private UIButton		pbHelp					= null;

	/** Holds the various panels with options.*/
	private JTabbedPane		TabbedPane				= null;

	/** The scrollpane for the test label.*/
	JScrollPane 			scrollpane				= null;
	
	/** Holds a temporary piece of info for the preview pane.*/	
	private String fontface		= "Serif"; //$NON-NLS-1$

	/** Holds a temporary piece of info for the preview pane.*/	
	private int fontstyle		= Font.PLAIN;

	/** Holds a temporary piece of info for the preview pane.*/	
	private int fontsize		= 12;

	/** The font object from the font settings chosen.*/
	private Font oFont = new Font ("Serif", Font.PLAIN, 10); //$NON-NLS-1$

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

	/** the text area to display the default label in the chosen font settings.*/
	private JTextArea 		label 				= null;

	/** Turn on bolding.*/
	private JCheckBox 		cbBold				= null;

	/** Turn on italics.*/
  	private JCheckBox 		cbItalic	 		= null;

	/** Activate automatic opening of details box after so many characters in the label.*/
  	private JCheckBox 		cbDetail 			= null;

	/** Should maps with images show a border?*/
	private JCheckBox		cbMapBorder			= null;
	
	/** Turn on tag node indicators.*/
  	private JCheckBox 		cbShowTags			= null;

	/** Turn on test node indicators.*/
  	private JCheckBox 		cbShowText			= null;

	/** Turn on transclusion node indicators.*/
  	private JCheckBox 		cbShowTrans			= null;

	/** Turn on weight node indicators.*/
  	private JCheckBox 		cbShowWeight			= null;

	/** Turn on small icons.*/
  	private JCheckBox 		cbSmallIcon			= null;

	/** Turn on hide icons.*/
  	private JCheckBox 		cbHideIcon			= null;

	/** The label for the width field.*/
	private JLabel			lblWidth 			= null;

	/** The label for the length field.*/
	private JLabel			lblLength 			= null;

	/** The label for the length field.*/
	private JLabel			lblLength2 			= null;

	/** the field to enter the width for wrapping the label.*/
	private JTextField		labelWidthField 	= null;

	/** The field to enter the length of the label for automatic detail box popup.*/
	private JTextField		labelLengthField 	= null;
	
	private JRadioButton rbSubfolderYes 		= null;
	
	private JRadioButton rbSubfolderNo 			= null;
	
	
	/** The model object that holds the project preference properties.*/
	private Model			oModel				= null;

		
	/** The iniital ShowWeight preference setting.*/
	private boolean 	bShowWeight = false;
	
	/** The iniital ShowTrans preference setting.*/
	private boolean 	bShowTrans 	= false;
	
	/** The iniital ShowText preference setting.*/
	private boolean		bShowText	= false;

	/** The iniital ShowTags preference setting.*/
	private boolean		bShowTags	= false;

	/** The iniital HideIcons preference setting.*/
	private boolean 	bHideIcon	= false;

	/** The iniital SmallIcons preference setting.*/
	private boolean		bSmallIcon	= false;

	/** The iniital MapBorder preference setting.*/
	private boolean 	bMapBorder	= false;
	
	/** The iniital font face preference setting.*/
	private String 		sFontFace		= "Serif"; //$NON-NLS-1$

	/** The iniital font size preference setting.*/
	private int 		nFontSize		= 12;

	/** The iniital font style preference setting.*/
	private int 		nFontStyle		= Font.PLAIN;
	  	
	/** Stores the width for wrapping the label.*/
	private int 		labelWidth 		= 15;

	/** Stored the length of the label for automatic detail box popup.*/
	private int 		labelLength 	= 100;
	
	/** Stores if the NodeContentDialog should automatically open when the label reaches a specified length.*/
	private boolean		detailPopup 	= false;	
	

	/**
	 * Constructor.
	 * @param parent, the parent frame for this dialog.
	 */
	public UIProjectOptionsDialog(JFrame parent, IModel oModel) {

		super(parent, true);
		this.oParent = parent;
		this.oModel = (Model)oModel;
		
		Container oContentPane = getContentPane();
		oContentPane.setLayout(new BorderLayout());

		TabbedPane = new JTabbedPane();

		if (ProjectCompendium.isMac) {
			setTitle(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIProjectOptionsDialog.projectPreferencesTitle")); //$NON-NLS-1$
		}
		else {
			setTitle(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIProjectOptionsDialog.projectOptionsTitle")); //$NON-NLS-1$
		}
		
		TabbedPane.add(createFontPanel(), LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIProjectOptionsDialog.nodeText")); //$NON-NLS-1$
		TabbedPane.add(createNodePanel(), LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIProjectOptionsDialog.nodeExtras")); //$NON-NLS-1$
		TabbedPane.add(createLinkedFilesPanel(), LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIProjectOptionsDialog.linkedFiles")); //$NON-NLS-1$

		loadProperties();		
		
		JPanel buttonpanel = createButtonPanel();

		oContentPane.add(TabbedPane, BorderLayout.CENTER);
		oContentPane.add(buttonpanel, BorderLayout.SOUTH);

		pack();
		setResizable(false);
	}  	
	
	public JPanel createNodePanel() {
		JPanel panel = new JPanel();
		panel.setBorder(new EmptyBorder(5,5,5,5));
		
		GridBagLayout gb = new GridBagLayout();
		panel.setLayout(gb);

		GridBagConstraints gc = new GridBagConstraints();
		gc.insets = new Insets(5,5,5,5);
		gc.anchor = GridBagConstraints.WEST;
		gc.gridwidth = GridBagConstraints.REMAINDER;

		JLabel label = new JLabel(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIProjectOptionsDialog.defaultSettings")); //$NON-NLS-1$
		gb.setConstraints(label, gc);		
    	panel.add(label);

		cbShowTags = new JCheckBox (LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIProjectOptionsDialog.showTagIndicator")); //$NON-NLS-1$
		gb.setConstraints(cbShowTags, gc);		
    	panel.add(cbShowTags);

    	cbShowText = new JCheckBox (LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIProjectOptionsDialog.showTextIndicator")); //$NON-NLS-1$
 		gb.setConstraints(cbShowText, gc);		    	
    	panel.add(cbShowText);

    	cbShowTrans = new JCheckBox (LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIProjectOptionsDialog.showTransIndicator")); //$NON-NLS-1$
  		gb.setConstraints(cbShowTrans, gc);		    	
    	panel.add(cbShowTrans);

    	cbShowWeight = new JCheckBox (LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIProjectOptionsDialog.showWeightIndicator")); //$NON-NLS-1$
  		gb.setConstraints(cbShowWeight, gc);		    	
    	panel.add(cbShowWeight);

    	cbSmallIcon = new JCheckBox (LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIProjectOptionsDialog.useSmallIcons")); //$NON-NLS-1$
 		gb.setConstraints(cbSmallIcon, gc);		    	
    	panel.add(cbSmallIcon);

    	cbHideIcon = new JCheckBox (LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIProjectOptionsDialog.hideIcons")); //$NON-NLS-1$
 		gb.setConstraints(cbHideIcon, gc);		    	
    	panel.add(cbHideIcon);

    	JSeparator sep = new JSeparator();
    	gc.fill = GridBagConstraints.HORIZONTAL;
		gb.setConstraints(sep, gc);
		panel.add(sep);
   	   
	   	gc.fill = GridBagConstraints.NONE;		
		cbMapBorder = new JCheckBox(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIProjectOptionsDialog.showMapBorder")); //$NON-NLS-1$
		gb.setConstraints(cbMapBorder, gc);
		panel.add(cbMapBorder);
    	
		return panel;
	}

	/**
	 * Create the panel with the audio and zoom options.
	 */
	public JPanel createFontPanel() {

		JPanel panel = new JPanel(new BorderLayout());

		GridLayout grid = new GridLayout(1,2);
		grid.setHgap(5);
		JPanel choices = new JPanel(grid);
		choices.setBorder( new EmptyBorder( 5,5,5,5 ) );

		createFontList();
		createSizeList();

		choices.add(fontscroll);
 		choices.add(sizescroll);

		JPanel checks = new JPanel();
		ItemListener fontItemListener = new ItemListener() {

			public void itemStateChanged(ItemEvent event) {
				if (cbBold.isSelected() && cbItalic.isSelected())
            		fontstyle = Font.BOLD+Font.ITALIC;
				else if (cbBold.isSelected() && !cbItalic.isSelected())
					fontstyle = Font.BOLD;
				else if (!cbBold.isSelected() && cbItalic.isSelected())
					fontstyle = Font.ITALIC;
				else if (!cbBold.isSelected() && !cbItalic.isSelected())
					fontstyle = Font.PLAIN;

				setLabelFont();
			}
		};

	    cbBold = new JCheckBox (LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIProjectOptionsDialog.bold")); //$NON-NLS-1$
    	cbBold.setSelected(oFont.isBold());
    	cbBold.setFont(new Font ("TimesRoman", Font.BOLD, 12)); //$NON-NLS-1$
    	cbBold.addItemListener(fontItemListener);
		checks.add(cbBold);

    	cbItalic = new JCheckBox (LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIProjectOptionsDialog.italic")); //$NON-NLS-1$
    	cbItalic.setSelected(oFont.isItalic());
    	cbItalic.setFont (new Font ("TimesRoman", Font.ITALIC, 12)); //$NON-NLS-1$
    	cbItalic.addItemListener(fontItemListener);
		checks.add(cbItalic);

		JPanel labelpanel = new JPanel(new BorderLayout());
		labelpanel.setBorder( new EmptyBorder( 5,5,5,5 ) );

		label = new JTextArea(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIProjectOptionsDialog.testScript")); //$NON-NLS-1$
		label.setPreferredSize(new Dimension(300, 50));
		scrollpane = new JScrollPane( label );
		label.setEditable(false);
		label.setFont( oFont );
		labelpanel.add( scrollpane, BorderLayout.CENTER );

		JPanel center = new JPanel( new BorderLayout() );
		center.setBorder(new TitledBorder(new EtchedBorder(),
                    LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIProjectOptionsDialog.defaultBorderTitle"), //$NON-NLS-1$
                    TitledBorder.LEFT,
                    TitledBorder.TOP,
					new Font("Dialog", Font.BOLD, 12) )); //$NON-NLS-1$

		center.add(choices, BorderLayout.NORTH);
		center.add(checks, BorderLayout.CENTER);
		center.add(labelpanel, BorderLayout.SOUTH);
		
		panel.add(center, BorderLayout.NORTH);

		// LABEL WIDTH
		JPanel widthPanel = new JPanel();
		/*widthPanel.setBorder(new TitledBorder(new EtchedBorder(),
                    "View Node Properties",
                    TitledBorder.LEFT,
                    TitledBorder.TOP,
					new Font("Dialog", Font.BOLD, 12) ));*/

		GridBagLayout gb = new GridBagLayout();
		widthPanel.setLayout(gb);

		GridBagConstraints gc = new GridBagConstraints();
		gc.insets = new Insets(5,5,5,5);

		lblWidth = new JLabel(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIProjectOptionsDialog.defaultWrapWidth")); //$NON-NLS-1$
		gc.gridy = 0;
		gc.gridx = 0;
		gc.gridwidth=3;		
		gc.anchor = GridBagConstraints.WEST;
		gb.setConstraints(lblWidth, gc);
		widthPanel.add(lblWidth);

		labelWidthField = new JTextField(new Integer(labelWidth).toString());
		labelWidthField.setColumns(5);
		gc.gridwidth=1;		
		gc.gridy = 0;
		gc.gridx = 3;
		gc.anchor = GridBagConstraints.WEST;
		gb.setConstraints(labelWidthField, gc);
		widthPanel.add(labelWidthField);

	   	cbDetail = new JCheckBox (LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIProjectOptionsDialog.autoOpen")); //$NON-NLS-1$
		cbDetail.setToolTipText(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIProjectOptionsDialog.enalbeAutoOpen")); //$NON-NLS-1$
 		gc.gridy = 1;
		gc.gridx = 0;
		gc.gridwidth=4;
		gc.anchor = GridBagConstraints.WEST;
		gb.setConstraints(cbDetail, gc);
		widthPanel.add(cbDetail);

		gc.gridwidth=1;

		// LABEL LENGTH
		lblLength = new JLabel(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIProjectOptionsDialog.ofMoreThan")); //$NON-NLS-1$
		lblLength.setToolTipText(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIProjectOptionsDialog.ofMoreThanTip")); //$NON-NLS-1$
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

		lblLength2 = new JLabel(" "+LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIProjectOptionsDialog.characters")); //$NON-NLS-1$
		lblLength2.setToolTipText(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIProjectOptionsDialog.charactersTip")); //$NON-NLS-1$
		gc.gridy = 2;
		gc.gridx = 2;
		gc.gridwidth=2;		
		gb.setConstraints(lblLength2, gc);
		widthPanel.add(lblLength2);
		
		lblLength.setEnabled(false);
		lblLength2.setEnabled(false);		
		labelLengthField.setEditable(false);
		labelLengthField.setEnabled(false);

	   	cbDetail.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent event) {

				if (cbDetail.isSelected()) {
            		lblLength.setEnabled(true);
               		lblLength2.setEnabled(true);            		
					labelLengthField.setEnabled(true);
					labelLengthField.setEditable(true);
				}
				else {
					lblLength.setEnabled(false);
					lblLength2.setEnabled(false);					
					labelLengthField.setEditable(false);
					labelLengthField.setEnabled(false);
				}
			}
		});

		cbDetail.setSelected(detailPopup);
		
		panel.add(widthPanel, BorderLayout.CENTER);
		return panel;
	}
	
	public JPanel createLinkedFilesPanel() {  
		JPanel panel = new JPanel();

		panel.setBorder(new TitledBorder(new EtchedBorder(),
                LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIProjectOptionsDialog.databaseDefaultLocation"), //$NON-NLS-1$
                TitledBorder.LEFT,
                TitledBorder.TOP,
				new Font("Dialog", Font.BOLD, 12) )); //$NON-NLS-1$
		
		GridBagLayout gb = new GridBagLayout();
		panel.setLayout(gb);

		int y = 0;
		GridBagConstraints gc = new GridBagConstraints();
		gc.insets = new Insets(5,5,5,5);
		gc.anchor = GridBagConstraints.NORTHWEST;
		gc.gridwidth = GridBagConstraints.REMAINDER;
		gc.gridx = 0;
		gc.gridy = y;
  	
		txtLinkedFilesPath = new JTextField(""); //$NON-NLS-1$
		
		JLabel lblLinkedFiles = new JLabel(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIProjectOptionsDialog.enterPath")+":"); //$NON-NLS-1$
		lblLinkedFiles.setLabelFor(txtLinkedFilesPath);
		gc.gridy = y;
		gb.setConstraints(lblLinkedFiles, gc);
		panel.add(lblLinkedFiles);
		y++;
		
		txtLinkedFilesPath.setColumns(50);
		gc.gridy = y;
		gb.setConstraints(txtLinkedFilesPath, gc);
		panel.add(txtLinkedFilesPath);
		y++;
		
		JLabel lblLinkedFiles2 = new JLabel(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIProjectOptionsDialog.message1")); //$NON-NLS-1$
		gc.gridy = y;
		gb.setConstraints(lblLinkedFiles2, gc);
		panel.add(lblLinkedFiles2);
		y++;
		
		
		JSeparator sep = new JSeparator();
    	gc.fill = GridBagConstraints.HORIZONTAL;
    	gc.gridy = y;
    	gb.setConstraints(sep, gc);
		panel.add(sep);
		y++;
		
		JLabel lblFlatLinkedFiles = new JLabel(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIProjectOptionsDialog.messageLinkFilesStoragePath")); //$NON-NLS-1$
		gc.gridy = y;
		gb.setConstraints(lblFlatLinkedFiles, gc);
		panel.add(lblFlatLinkedFiles);
		y++;
		
		JPanel panel2 = new JPanel();
		rbSubfolderNo = new JRadioButton(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIProjectOptionsDialog.no")); //$NON-NLS-1$
		panel2.add(rbSubfolderNo);
		rbSubfolderYes = new JRadioButton(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIProjectOptionsDialog.yes")); //$NON-NLS-1$
		panel2.add(rbSubfolderYes);
		
		ButtonGroup rgGroup = new ButtonGroup();
		rgGroup.add(rbSubfolderNo);
		rgGroup.add(rbSubfolderYes);
		
    	gc.gridy = y;
    	gb.setConstraints(panel2, gc);
    	panel.add(panel2);
    	y++;
		
		JLabel lblFlat2 = new JLabel(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIProjectOptionsDialog.noTip")); //$NON-NLS-1$
		gc.gridy = y;
		gb.setConstraints(lblFlat2, gc);
		panel.add(lblFlat2);
		y++;
		JLabel lblFlat3 = new JLabel(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIProjectOptionsDialog.yesTip")); //$NON-NLS-1$
		gc.gridy = y;
		gb.setConstraints(lblFlat3, gc);
		panel.add(lblFlat3);
		y++;
		
		
    	
		return panel;
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
            	text += " "; //$NON-NLS-1$
      		}

			setBorder((cellHasFocus) ? UIManager.getBorder("List.focusCellHighlightBorder") : noFocusBorder); //$NON-NLS-1$
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

			setBorder((cellHasFocus) ? UIManager.getBorder("List.focusCellHighlightBorder") : noFocusBorder); //$NON-NLS-1$
			setText(" "+(String)value+"  "); //$NON-NLS-1$ //$NON-NLS-2$
			return this;
		}
	}

	/**
	 * Create the font size list.
	 */
	private void createSizeList() {

  	 	String[] sizes = {"8","10","12","14","16","18","20","22","24","26","28","30"}; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$ //$NON-NLS-10$ //$NON-NLS-11$ //$NON-NLS-12$

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
	 * Create the button panel.
	 */
	private UIButtonPanel createButtonPanel() {

		UIButtonPanel panel = new UIButtonPanel();

		pbUpdate = new UIButton(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIProjectOptionsDialog.updateButton")); //$NON-NLS-1$
		pbUpdate.setMnemonic(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIProjectOptionsDialog.updateButtonMnemonic").charAt(0));
		pbUpdate.addActionListener(this);
		getRootPane().setDefaultButton(pbUpdate);
		panel.addButton(pbUpdate);

		pbCancel = new UIButton(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIProjectOptionsDialog.cancelButton")); //$NON-NLS-1$
		pbCancel.setMnemonic(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIProjectOptionsDialog.cancelButtonMnemonic").charAt(0));
		pbCancel.addActionListener(this);
		panel.addButton(pbCancel);

		// Add help button
		pbHelp = new UIButton(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIProjectOptionsDialog.helpButton")); //$NON-NLS-1$
		pbHelp.setMnemonic(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIProjectOptionsDialog.helpButtonMnemonic").charAt(0));
		ProjectCompendium.APP.mainHB.enableHelpOnButton(pbHelp, "basics.options", ProjectCompendium.APP.mainHS); //$NON-NLS-1$
		panel.addHelpButton(pbHelp);

		return panel;
	}
	
	private void loadProperties() {

		bShowWeight = oModel.showWeightNodeIndicator;
		cbShowWeight.setSelected(bShowWeight);

		bShowTrans = oModel.showTransNodeIndicator;
		cbShowTrans.setSelected(bShowTrans);

		bShowText = oModel.showTextNodeIndicator;
		cbShowText.setSelected(bShowText);

		bShowTags = oModel.showTagsNodeIndicator;
		cbShowTags.setSelected(bShowTags);

		bSmallIcon = oModel.smallIcons;
		cbSmallIcon.setSelected(bSmallIcon);
	
		bHideIcon = oModel.hideIcons;
		cbHideIcon.setSelected(bHideIcon);

		bMapBorder = oModel.mapBorder;
		cbMapBorder.setSelected(bMapBorder);

		detailPopup = oModel.detailPopup;
		cbDetail.setSelected(detailPopup);
		
		labelWidth = oModel.labelWrapWidth;
		labelWidthField.setText(String.valueOf(labelWidth));
		
		labelLength = oModel.labelPopupLength;
		labelLengthField.setText(String.valueOf(labelLength));
		
		sFontFace = oModel.fontface;
		fontface = sFontFace;
	   	fontlist.setSelectedValue(sFontFace, true);

		nFontSize = oModel.fontsize;
		fontsize = nFontSize;
	   	sizelist.setSelectedValue(String.valueOf(nFontSize), true);
		
		nFontStyle = oModel.fontstyle;
		fontstyle = nFontStyle;
		
		oFont = new Font(sFontFace, nFontStyle, nFontSize);
		
		cbItalic.setSelected(oFont.isItalic());
		cbBold.setSelected(oFont.isBold());
		
		txtLinkedFilesPath.setText(oModel.linkedFilesPath);
		
		if (oModel.linkedFilesFlat) {
			rbSubfolderYes.setSelected(false);
			rbSubfolderNo.setSelected(true);
		} else {
			rbSubfolderYes.setSelected(true);
			rbSubfolderNo.setSelected(false);
		}
		
		setLabelFont();
	}
	
	/**
	 * Process button pushes.
	 * @param evt, the associated ActionEvent object.
	 */
	public void actionPerformed(ActionEvent evt) {
		Object source = evt.getSource();

		if (source == pbCancel)
			onCancel();
		else
		if (source == pbUpdate)
			onUpdate();
	}
	
	/**
	 * Save the users options and update where necessary.
	 */
	public void onUpdate() {

		try {	
			if (bShowTags != cbShowTags.isSelected()) {
				if (cbShowTags.isSelected()) {
					oModel.setProjectPreference(Model.SHOW_TAGS_PROPERTY, "true"); //$NON-NLS-1$
				} else {
					oModel.setProjectPreference(Model.SHOW_TAGS_PROPERTY, "false"); //$NON-NLS-1$
				}				
			}

			if (bShowText != cbShowText.isSelected()) {
				if (cbShowText.isSelected()) {
					oModel.setProjectPreference(Model.SHOW_TEXT_PROPERTY, "true"); //$NON-NLS-1$
				} else {
					oModel.setProjectPreference(Model.SHOW_TEXT_PROPERTY, "false"); //$NON-NLS-1$
				}				
			}

			if (bShowTrans != cbShowTrans.isSelected()) {
				if (cbShowTrans.isSelected()) {
					oModel.setProjectPreference(Model.SHOW_TRANS_PROPERTY, "true"); //$NON-NLS-1$
				} else {
					oModel.setProjectPreference(Model.SHOW_TRANS_PROPERTY, "false"); //$NON-NLS-1$
				}				
			}

			if (bShowWeight != cbShowWeight.isSelected()) {
				if (cbShowWeight.isSelected()) {
					oModel.setProjectPreference(Model.SHOW_WEIGHT_PROPERTY, "true"); //$NON-NLS-1$
				} else {
					oModel.setProjectPreference(Model.SHOW_WEIGHT_PROPERTY, "false"); //$NON-NLS-1$
				}				
			}

			if (bSmallIcon != cbSmallIcon.isSelected()) {
				if (cbSmallIcon.isSelected()) {
					oModel.setProjectPreference(Model.SMALL_ICONS_PROPERTY, "true"); //$NON-NLS-1$
				} else {
					oModel.setProjectPreference(Model.SMALL_ICONS_PROPERTY, "false"); //$NON-NLS-1$
				}				
			}

			if (bHideIcon != cbHideIcon.isSelected()) {
				if (cbHideIcon.isSelected()) {
					oModel.setProjectPreference(Model.HIDE_ICONS_PROPERTY, "true"); //$NON-NLS-1$
				} else {
					oModel.setProjectPreference(Model.HIDE_ICONS_PROPERTY, "false"); //$NON-NLS-1$
				}				
			}

			if (bMapBorder != cbMapBorder.isSelected()) {
				if (cbMapBorder.isSelected()) {
					oModel.setProjectPreference(Model.MAP_BORDER_PROPERTY, "true"); //$NON-NLS-1$
				} else {
					oModel.setProjectPreference(Model.MAP_BORDER_PROPERTY, "false"); //$NON-NLS-1$
				}				
			}

			if (detailPopup != cbDetail.isSelected()) {
				if (cbDetail.isSelected()) {
					oModel.setProjectPreference(Model.DETAIL_POPUP_PROPERTY, "true"); //$NON-NLS-1$
				} else {
					oModel.setProjectPreference(Model.DETAIL_POPUP_PROPERTY, "false"); //$NON-NLS-1$
				}				
			}
			
			if (rbSubfolderYes.isSelected()) {
				oModel.setProjectPreference(Model.LINKED_FILES_FLAT_PROPERTY, "false"); //$NON-NLS-1$
			} else {
				oModel.setProjectPreference(Model.LINKED_FILES_FLAT_PROPERTY, "true"); //$NON-NLS-1$
			}
			
			if (txtLinkedFilesPath.getText().equals("")) { //$NON-NLS-1$
				oModel.setProjectPreference(Model.LINKED_FILES_PATH_PROPERTY, "Linked Files"); //$NON-NLS-1$
			} else {
				oModel.setProjectPreference(Model.LINKED_FILES_PATH_PROPERTY, txtLinkedFilesPath.getText());
			}
			
			String sFace = (String)fontlist.getSelectedValue();
			if (!sFace.equals(sFontFace)) {
				oModel.setProjectPreference(Model.FONTFACE_PROPERTY, sFace);				
			}
			if (nFontStyle != fontstyle) {
				oModel.setProjectPreference(Model.FONTSTYLE_PROPERTY, String.valueOf(fontstyle));
			}
			if (nFontSize != fontsize) {
				oModel.setProjectPreference(Model.FONTSIZE_PROPERTY, String.valueOf(fontsize));
			}			
			
			String width = labelWidthField.getText();
			int nWidth = -1;			
			try {
				nWidth = (new Integer(width)).intValue();
				if (nWidth != labelWidth) {
					oModel.setProjectPreference(Model.LABEL_WRAP_WIDTH_PROPERTY, String.valueOf(nWidth));
				}
			}
			catch(NumberFormatException e) {
				ProjectCompendium.APP.displayError(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIProjectOptionsDialog.errorWrapWidth")); //$NON-NLS-1$
				labelWidthField.requestFocus();
				return;
			}

			String length = labelLengthField.getText();
			int nLength = -1;
			try {
				nLength = (new Integer(length)).intValue();
				if (nLength != labelLength) {
					oModel.setProjectPreference(Model.LABEL_POPUP_LENGTH_PROPERTY, String.valueOf(nLength));
				}
			}
			catch(NumberFormatException e) {
				ProjectCompendium.APP.displayError(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIProjectOptionsDialog.errorPopupLength")); //$NON-NLS-1$
				labelLengthField.requestFocus();
				return;
			}
			
			oFont = ( new Font(fontface, fontstyle, fontsize));
			ProjectCompendium.APP.setDefaultFont( oFont );
			
			// Now Re-apply the new font plus zoom to the list view and side views.
			int textZoom = ProjectCompendium.APP.getToolBarManager().getTextZoom();
			Vector views = ProjectCompendium.APP.getAllFrames();
			int count = views.size();
			UIViewFrame viewFrame  = null;
			UIViewPane view = null;
			UIList list = null;
			for (int i=0; i<count; i++) {
				viewFrame = (UIViewFrame)views.elementAt(i);
				if (viewFrame instanceof UIListViewFrame) {
					list = ((UIListViewFrame)viewFrame).getUIList();
					list.onReturnTextAndZoom(textZoom);
				}
			}
			ProjectCompendium.APP.getMenuManager().onReturnTextAndZoom(textZoom);
						
			oModel.saveProjectPreferences();
			ProjectCompendium.APP.refreshIconIndicators();
			
		} catch (SQLException ex) {
			ProjectCompendium.APP.displayError(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIProjectOptionsDialog.errorSaving")+":\n\n"+ex.getMessage()); //$NON-NLS-1$
			return;
		}

		onCancel();
	}
}
