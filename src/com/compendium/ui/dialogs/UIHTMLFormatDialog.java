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
import java.awt.*;
import java.awt.event.*;
import java.io.*;

import javax.swing.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;
import javax.swing.event.*;
import javax.swing.border.*;
import javax.swing.table.*;

import com.compendium.ProjectCompendium;
import com.compendium.io.html.*;
import com.compendium.ui.*;
import com.compendium.ui.plaf.*;

import com.compendium.core.CoreUtilities;
import com.compendium.core.datamodel.*;
import com.compendium.core.datamodel.services.*;

/**
 * UIHTMLFormatDialog defines the format dialog, that allows
 * the user to chosen certain format options for the exporting HTML document
 *
 * @author	Michelle Bachler
 */
public class UIHTMLFormatDialog extends UIDialog implements ActionListener, IUIConstants {

	/** The number of rows in the table.*/
	public static int 	ROW_COUNT	=	49;
	
	/** The path to the formats.*/
	public static String DEFAULT_FILE_PATH = "System"+ProjectCompendium.sFS+"resources"+ProjectCompendium.sFS+"OutlineStyles";
	
	/** The name of the default file.*/
	public static String DEFAULT_FILE_NAME = "Default.properties";
	
	/** The name of the default format style.*/
	private static String DEFAULT_FORMAT = "Default";
	
	/** The pane to hold the dialogs contents.*/
	private Container				oContentPane = null;

	/** The parent frame for this dialog.*/
	private JFrame					oParent	= null;

	/** Model for the table holding the format element.*/
	private FormatTableModel		formatTableModel = null;

	/** The button to save the format options.*/
	private UIButton				pbSave	= null;

	/** The button to save the format options as a new file.*/
	private UIButton				pbSaveAs = null;

	/** The button to delete the format options.*/
	private UIButton				pbDelete	= null;

	/** Holds a list of existing styles.*/
	private JComboBox				oStyles	= null;

	/** The button to cancel the format dialog without saving.*/
	private UIButton				pbCancel	= null;

	/** The button to cascade the heading settings.*/
	private UIButton				pbHead	= null;

	/** The button to copy row data to like rows.*/
	private UIButton				pbRow		= null;

	/** The button to copy column data to whole column. */
	private UIButton				pbColumn 	= null;

	/** The button to launch the help dialog for this topic.*/
	private UIButton				pbHelp 	= null;
	
	/** The label displaying the style id number which ties into the filename.*/
	private JLabel					lblID = null;

	private Vector					oViews  = new Vector(51);
	private ViewPaneUI				oViewPaneUI = null;

	/** The current view.*/
	private View					currentView = null;

	/** The table holding the view options.*/
	private JTable					oTable = null;

	/** The scrollpane holding the format options table.*/
	private JScrollPane				oScrollPane = null;
	
	/** The referrence to the colour chooser dialog. */
	private UIColorChooserDialog oColorChooserDialog = null;
	
	/** List of style snames mapped to the file name.*/
	private Hashtable				htStyles = new Hashtable();
	
	/** List of style names to be displayed in the choice box.*/
	private Vector 					vtStyles = new Vector();
	
	private JComboBox				cbMenuFontFamily = null;
	private JComboBox				cbMenuFontSize = null;
	private JComboBox				cbMenuFontStyle = null;
	private JLabel					lblMenuFontColor = null;
	private JLabel					lblMenuBackgroundColor = null;
	private JLabel					lblMenuBorderColor = null;
	private JLabel					lblDividerColor = null;
	
	/** The row height for table rows.*/
    final int INITIAL_ROWHEIGHT = 20;


	/**
	 * Initializes and draws the dialog
	 * @param parent, the parent view tp this dialog.
	 */
	public UIHTMLFormatDialog(JFrame parent) {

		super(parent, true);

	 	this.setTitle("HTML Formatting Options");
	  	oParent = parent;

		oContentPane = getContentPane();

		String[] indent = {"0", "0.25", "0.5", "0.75","1.0","1.25","1.5", "1.75", "2.0", "2.25", "2.5", "3.0", "3.25", "3.5", "3.75", "4.0"};
		String[] topmargin = {"-0.1", "-0.05", "0", "0.05", "0.1", "0.15", "0.2","0.25","0.3", "0.35", "0.4", "0.45", "0.5", "0.55", "0.6", "0.65", "0.7", "0.75", "0.8", "0.85", "0.9", "0.95", "1.0"};		
   	 	String[] fonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
		String[] sizes = {"6","7","8","9","10","11","12","13","14","15","16","17","18","19","20","21","22","23","24","25","26","27","28","29","30","31","32","33","34","35","36","37","38","39","40"};
		String[] styles = {"normal","bold","italic", "bold-italic"};

		UIButtonPanel oButtonPanel = new UIButtonPanel();

		pbSave = new UIButton("Save");
		pbSave.setMnemonic(KeyEvent.VK_S);
		pbSave.setEnabled(false);
		pbSave.addActionListener(this);
		oButtonPanel.addButton(pbSave);

		pbSaveAs = new UIButton("Save As");
		pbSaveAs.setMnemonic(KeyEvent.VK_A);
		pbSaveAs.setEnabled(false);
		pbSaveAs.addActionListener(this);
		oButtonPanel.addButton(pbSaveAs);

		pbDelete = new UIButton("Delete");
		pbDelete.setMnemonic(KeyEvent.VK_D);
		pbDelete.setEnabled(false);
		pbDelete.addActionListener(this);
		oButtonPanel.addButton(pbDelete);

		pbCancel = new UIButton("Cancel");
		pbCancel.setMnemonic(KeyEvent.VK_C);
		pbCancel.addActionListener(this);
		oButtonPanel.addButton(pbCancel);

		pbHelp = new UIButton("Help");
		pbHelp.setMnemonic(KeyEvent.VK_H);
		ProjectCompendium.APP.mainHB.enableHelpOnButton(pbHelp, "io.exportFormat", ProjectCompendium.APP.mainHS);
		oButtonPanel.addHelpButton(pbHelp);
		
		JPanel mainpanel = new JPanel(new BorderLayout());
		mainpanel.setBorder(new EmptyBorder(10,10,10,10));

		lblID = new JLabel(" ");
		lblID.setHorizontalAlignment(SwingConstants.LEFT);
		lblID.setFont(new Font("Dialog", Font.PLAIN, 12));
				
		// CENTERPANEL = MAINPANEL - CENTER
		JPanel centerpanel = new JPanel(new BorderLayout());

		// MAINPANEL = SOUTH
		// MENU FORMATTING
		GridBagLayout grid = new GridBagLayout();
		GridBagConstraints gc = new GridBagConstraints();
		gc.insets = new Insets(3,3,3,3);
		int y=0;
		gc.anchor = GridBagConstraints.WEST;
		JPanel menupanel = new JPanel(grid);

		JLabel lblMenu = new JLabel("Format Navigation Menu & View Divider");
		lblMenu.setFont(new Font("Dialog", Font.BOLD, 12));
		gc.gridy = y;
		gc.gridwidth = 4;
		gc.weightx = 100;
		y++;
		grid.addLayoutComponent(lblMenu, gc);
		menupanel.add(lblMenu);
		
		JLabel lblFontFamily = new JLabel("Font: ");
		gc.gridy = y;
		gc.gridwidth = 1;
		grid.addLayoutComponent(lblFontFamily, gc);
		menupanel.add(lblFontFamily);
		
		cbMenuFontFamily = createFontChoicebox(fonts);
		gc.gridy = y;
		grid.addLayoutComponent(cbMenuFontFamily, gc);
		menupanel.add(cbMenuFontFamily);
		//y++;
		
		//JLabel lblFontSize = new JLabel("Size: ");
		//gc.gridy = y;
		//gc.gridwidth = 1;
		//grid.addLayoutComponent(lblFontSize, gc);
		//menupanel.add(lblFontSize);
				
		cbMenuFontSize = createChoicebox(sizes);
		gc.gridy = y;
		grid.addLayoutComponent(cbMenuFontSize, gc);
		menupanel.add(cbMenuFontSize);
		//y++;

		//JLabel lblFontStyle = new JLabel("Style: ");
		//gc.gridy = y;
		//gc.gridwidth = 1;
		//grid.addLayoutComponent(lblFontStyle, gc);
		//menupanel.add(lblFontStyle);
		
		cbMenuFontStyle = createChoicebox(styles);
		gc.gridy = y;
		grid.addLayoutComponent(cbMenuFontStyle, gc);
		menupanel.add(cbMenuFontStyle);
		y++;
		
		JLabel lblTextColor = new JLabel("Text Color: ");
		gc.gridy = y;
		gc.gridwidth = 1;
		grid.addLayoutComponent(lblTextColor, gc);
		menupanel.add(lblTextColor);

		lblMenuFontColor = new JLabel("                              ");
		lblMenuFontColor.setOpaque(true);
		lblMenuFontColor.setBorder(new BevelBorder(BevelBorder.RAISED));
		lblMenuFontColor.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
	            Color colour = lblMenuFontColor.getBackground();
				if (oColorChooserDialog != null) {
					oColorChooserDialog.setColour(colour);
				} else {
					oColorChooserDialog = new UIColorChooserDialog(ProjectCompendium.APP, colour);
				}
				oColorChooserDialog.setVisible(true);
				Color oColour = oColorChooserDialog.getColour();
				oColorChooserDialog.setVisible(false);
				if (oColour != null) {
					lblMenuFontColor.setBackground(oColour);
				}
			}			
		});		
		gc.gridy = y;
		gc.gridwidth = 3;
		y++;
		grid.addLayoutComponent(lblMenuFontColor, gc);
		menupanel.add(lblMenuFontColor);
		
		JLabel lblMenuBackground = new JLabel("Menu Background: ");
		gc.gridy = y;
		gc.gridwidth = 1;
		grid.addLayoutComponent(lblMenuBackground, gc);
		menupanel.add(lblMenuBackground);

		lblMenuBackgroundColor = new JLabel("                              ");
		lblMenuBackgroundColor.setOpaque(true);
		lblMenuBackgroundColor.setBorder(new BevelBorder(BevelBorder.RAISED));
		lblMenuBackgroundColor.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
	            Color colour = lblMenuBackgroundColor.getBackground();
				if (oColorChooserDialog != null) {
					oColorChooserDialog.setColour(colour);
				} else {
					oColorChooserDialog = new UIColorChooserDialog(ProjectCompendium.APP, colour);
				}
				oColorChooserDialog.setVisible(true);
				Color oColour = oColorChooserDialog.getColour();
				oColorChooserDialog.setVisible(false);
				if (oColour != null) {
					lblMenuBackgroundColor.setBackground(oColour);
				}
			}			
		});		
		gc.gridy = y;
		gc.gridwidth = 3;
		y++;
		grid.addLayoutComponent(lblMenuBackgroundColor, gc);
		menupanel.add(lblMenuBackgroundColor);

		JLabel lblMenuBorder = new JLabel("Menu Border: ");
		gc.gridy = y;
		gc.gridwidth = 1;
		grid.addLayoutComponent(lblMenuBorder, gc);
		menupanel.add(lblMenuBorder);

		lblMenuBorderColor = new JLabel("                              ");
		lblMenuBorderColor.setOpaque(true);
		lblMenuBorderColor.setBorder(new BevelBorder(BevelBorder.RAISED));
		lblMenuBorderColor.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
	            Color colour = lblMenuBorderColor.getBackground();
				if (oColorChooserDialog != null) {
					oColorChooserDialog.setColour(colour);
				} else {
					oColorChooserDialog = new UIColorChooserDialog(ProjectCompendium.APP, colour);
				}
				oColorChooserDialog.setVisible(true);
				Color oColour = oColorChooserDialog.getColour();
				oColorChooserDialog.setVisible(false);
				if (oColour != null) {
					lblMenuBorderColor.setBackground(oColour);
				}
			}			
		});		
		gc.gridy = y;
		gc.gridwidth = 3;		
		y++;
		grid.addLayoutComponent(lblMenuBorderColor, gc);
		menupanel.add(lblMenuBorderColor);

		JLabel lblDivider = new JLabel("View Divider: ");
		gc.gridy = y;
		gc.gridwidth = 1;
		grid.addLayoutComponent(lblDivider, gc);
		menupanel.add(lblDivider);

		lblDividerColor = new JLabel("                              ");
		lblDividerColor.setOpaque(true);
		lblDividerColor.setBorder(new BevelBorder(BevelBorder.RAISED));
		lblDividerColor.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
	            Color colour = lblDividerColor.getBackground();
				if (oColorChooserDialog != null) {
					oColorChooserDialog.setColour(colour);
				} else {
					oColorChooserDialog = new UIColorChooserDialog(ProjectCompendium.APP, colour);
				}
				oColorChooserDialog.setVisible(true);
				Color oColour = oColorChooserDialog.getColour();
				oColorChooserDialog.setVisible(false);
				if (oColour != null) {
					lblDividerColor.setBackground(oColour);
				}
			}			
		});		
		gc.gridy = y;
		gc.gridwidth = 3;
		y++;
		grid.addLayoutComponent(lblDividerColor, gc);
		menupanel.add(lblDividerColor);
		
		mainpanel.add(menupanel, BorderLayout.SOUTH);
		
		// CENTERPANEL - NORTH
		JLabel lblTable = new JLabel("Format Node Data");
		lblTable.setFont(new Font("Dialog", Font.BOLD, 12));
		centerpanel.add(lblTable, BorderLayout.NORTH);
		
		// CENTEPANL - CENTER
		formatTableModel = new FormatTableModel();
		oTable = new JTable(formatTableModel);

      	TableColumn comboColumn = oTable.getColumnModel().getColumn(0);
       	comboColumn.setCellRenderer(createCellRenderer());
 
		TableColumn comboColumn1 = oTable.getColumnModel().getColumn(1);
		comboColumn1.setCellRenderer(createCellRenderer());
		comboColumn1.setCellEditor(new DefaultCellEditor(createChoicebox(indent)));

		TableColumn comboColumn2 = oTable.getColumnModel().getColumn(2);
		comboColumn2.setCellRenderer(createCellRenderer());
		comboColumn2.setCellEditor(new DefaultCellEditor(createChoicebox(topmargin)));

		TableColumn comboColumn3 = oTable.getColumnModel().getColumn(3);
		comboColumn3.setCellRenderer(createCellRenderer());
		comboColumn3.setCellEditor(new DefaultCellEditor(createFontChoicebox(fonts)));

		TableColumn comboColumn4 = oTable.getColumnModel().getColumn(4);
		comboColumn4.setCellRenderer(createCellRenderer());
		comboColumn4.setCellEditor(new DefaultCellEditor(createChoicebox(sizes)));

		TableColumn comboColumn5 = oTable.getColumnModel().getColumn(5);
		comboColumn5.setCellRenderer(createCellRenderer());
		comboColumn5.setCellEditor(new DefaultCellEditor(createChoicebox(styles)));
		
        comboColumn = oTable.getColumnModel().getColumn(6);
        comboColumn.setCellRenderer(createCellRenderer());
        
        comboColumn = oTable.getColumnModel().getColumn(7);
        comboColumn.setCellRenderer(createCellRenderer());
		
		oTable.getTableHeader().setReorderingAllowed(false);
		oTable.setRowHeight(INITIAL_ROWHEIGHT);
		oTable.setCellSelectionEnabled(true);
		oTable.getColumn("Item").setPreferredWidth(100);
		oTable.getColumn("Left Margin").setPreferredWidth(35);
		oTable.getColumn("Top Margin").setPreferredWidth(35);
		oTable.getColumn("Font").setPreferredWidth(50);
		oTable.getColumn("Size").setPreferredWidth(20);
		oTable.getColumn("Style").setPreferredWidth(50);
		oTable.getColumn("Background").setPreferredWidth(50);
		oTable.getColumn("Text Color").setPreferredWidth(50);
		oTable.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
	            Point click = new Point(e.getX(), e.getY());
	            int col = oTable.columnAtPoint(click);
	            if (col == 6 || col == 7) {
		            int row = oTable.rowAtPoint(click);
		            String value = (String)oTable.getValueAt(row, col);
		            Color colour = null;
					try {
						int color = (new Integer((String)value)).intValue();
						colour = new Color(color);
					} catch(NumberFormatException ex) {}
		            
					if (oColorChooserDialog != null) {
						if (colour != null) {
							oColorChooserDialog.setColour(colour);
						} 
					} else {
						if (colour != null) {
							oColorChooserDialog = new UIColorChooserDialog(ProjectCompendium.APP, colour);
						} else {
							oColorChooserDialog = new UIColorChooserDialog(ProjectCompendium.APP, Color.white);
						}
					}
					oColorChooserDialog.setVisible(true);
					Color oColour = oColorChooserDialog.getColour();
					oColorChooserDialog.setVisible(false);
					if (oColour != null) {
						oTable.setValueAt(String.valueOf(oColour.getRGB()), row, col);
					}
		            
					oTable.repaint();
	            }
			}
		});
		oScrollPane = new JScrollPane(oTable);
		oScrollPane.setPreferredSize(new Dimension(500,300));
		
		centerpanel.add(oScrollPane, BorderLayout.CENTER);
		
		// CENTERPANL - SOUTH / MAINPANEL - CENTER		
		JPanel panel = new JPanel();
		panel.setBorder(new EtchedBorder());

		pbHead = new UIButton("Cascade Heading Settings");
		pbHead.setToolTipText("Copy heading row setting to all other sections below");
		pbHead.addActionListener(this);
		panel.add(pbHead);

		pbRow = new UIButton("Copy to 'Like' Rows");
		pbRow.setToolTipText("Copy the selected cell to other rows of the same type");
		pbRow.addActionListener(this);
		panel.add(pbRow);

		pbColumn = new UIButton("Copy to Whole Column");
		pbColumn.setToolTipText("Copy the selected cell to all other cells in its column");
		pbColumn.addActionListener(this);
		panel.add(pbColumn);

		centerpanel.add(panel, BorderLayout.SOUTH);
		
		mainpanel.add(centerpanel, BorderLayout.CENTER);

		// MAINPANEL - NORTH. NOTE: needs to be below MAINPANEL - CENTER for variable referencing.
		JLabel lblStyle = new JLabel("Format Style");
		lblStyle.setFont(new Font("Dialog", Font.PLAIN, 12));

		JLabel lblFormatID = new JLabel("Format ID");
		lblFormatID.setFont(new Font("Dialog", Font.PLAIN, 12));

		GridBagLayout grid2 = new GridBagLayout();
		GridBagConstraints gc2 = new GridBagConstraints();
		gc2.anchor = GridBagConstraints.WEST;
		
		JPanel labelpanel = new JPanel(grid2);
		labelpanel.setBorder(new EmptyBorder(0,0,5,0));

		gc2.insets = new Insets(5,0,5,5);
		gc2.gridy = 0;
		gc2.gridx = 0;
		grid2.setConstraints(lblStyle, gc2);
		labelpanel.add(lblStyle);
		
		gc2.insets = new Insets(5,5,5,5);
		gc2.gridy = 0;
		gc2.gridx = 1;
		JComboBox box = this.createStylesChoiceBox();
		grid2.setConstraints(box, gc2);
		labelpanel.add(box);
		
		gc2.insets = new Insets(5,0,5,5);
		gc2.gridy = 1;
		gc2.gridx = 0;
		grid2.setConstraints(lblFormatID, gc2);
		labelpanel.add(lblFormatID);
		
		gc2.insets = new Insets(5,5,5,5);
		gc2.gridy = 1;
		gc2.gridx = 1;
		gc2.weightx = 100;
		grid2.setConstraints(lblID, gc2);
		labelpanel.add(lblID);
		
		mainpanel.add(labelpanel, BorderLayout.NORTH);
				
		oContentPane.setLayout(new BorderLayout());
		oContentPane.add(mainpanel, BorderLayout.CENTER);
		oContentPane.add(oButtonPanel, BorderLayout.SOUTH);

		pack();

		setResizable(false);
	}

	/**
	 * Create a choicebox with the given data for the given indentifier.
	 * @param data, the data for the choicebox.
	 */
	private JComboBox createChoicebox(String[] data) {

		JComboBox choicebox = new JComboBox(data);
   	   	choicebox.setOpaque(true);
		choicebox.setEditable(false);
		choicebox.setEnabled(true);
		choicebox.setMaximumRowCount(10);
		choicebox.setFont( new Font("Dialog", Font.PLAIN, 12 ));
		return choicebox;
	}

	/**
	 * Create a choicebox for font options with the given data for the given indentifier.
	 * @param data, the data for the choicebox.
	 */
	private JComboBox createFontChoicebox(String[] data) {

		JComboBox choicebox = new JComboBox(data);
   	   	choicebox.setOpaque(true);
		choicebox.setEditable(false);
		choicebox.setEnabled(true);
		choicebox.setMaximumRowCount(10);
		choicebox.setRenderer(new FontCellRenderer());
		return choicebox;
	}
	
	/**
	 * Create the styles choicebox.
	 */
	private JComboBox createStylesChoiceBox() {

		oStyles = new JComboBox();
		oStyles.setOpaque(true);
		oStyles.setEditable(false);
		oStyles.setEnabled(true);
		oStyles.setMaximumRowCount(30);
		oStyles.setFont( new Font("Dialog", Font.PLAIN, 12 ));

		reloadData();
 
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

				setText((String)value);
				return this;
			}
		};
		oStyles.setRenderer(comboRenderer);
		
		ActionListener choiceaction = new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
            	Thread choiceThread = new Thread("UIHTMLFormatDialog.createStylesChoiceBox") {
                	public void run() {
						if (oStyles != null) {
							String selected = (String)oStyles.getSelectedItem();
							if (htStyles.containsKey(selected)) {
								if (!selected.equals("Default")) {
									pbSave.setEnabled(true);
									pbSaveAs.setEnabled(true);
									pbDelete.setEnabled(true);	
									getRootPane().setDefaultButton(pbSave);
								} else {
									pbSave.setEnabled(false);
									pbSaveAs.setEnabled(true);
									pbDelete.setEnabled(false);	
									getRootPane().setDefaultButton(pbSaveAs);
								}
								File file = (File)htStyles.get(selected);
								formatTableModel.loadData(file);
								oTable.setModel(formatTableModel);
							} else {
								pbSave.setEnabled(false);
								pbSaveAs.setEnabled(false);
								pbDelete.setEnabled(false);	
								getRootPane().setDefaultButton(pbCancel);
								formatTableModel.clearData();								
							}
							oTable.repaint();
						}
                	}
               	};
	            choiceThread.start();
        	}
		};
		oStyles.addActionListener(choiceaction);

		return oStyles;
	}
	
	/**
	 * Load the styles data.
	 *
	 */
	private void reloadData() {
		try {
			vtStyles.clear();
			File main = new File(DEFAULT_FILE_PATH);
			File styles[] = main.listFiles();
			File file = null;
			String sName = "";
			String value = "";
			String sFileName = "";
			int index = 0;
			int j = 0;
			if (styles.length > 0) {			
				for (int i=0; i<styles.length; i++) {
					file = styles[i];
					sFileName = file.getName();
					if (!sFileName.startsWith(".") && sFileName.endsWith(".properties")) {
						Properties styleProp = new Properties();
						styleProp.load(new FileInputStream(file));
						value = styleProp.getProperty("status");
						if (value.equals("active")) {
							value = styleProp.getProperty("name");
							if (value != null) {
								sName = value;
								if (sName.equals(FormatProperties.outlineFormat)) {
									index = j+1;
								}
								vtStyles.add(sName);
								htStyles.put(sName, file);
							}
							j++;
						}
					}
				}
				vtStyles = UIUtilities.sortList(vtStyles);				
				vtStyles.insertElementAt("< Select An Outline Format >", 0);
				DefaultComboBoxModel comboModel = new DefaultComboBoxModel(vtStyles);
				oStyles.setModel(comboModel);
				oStyles.setSelectedIndex(index);
				
				if (index > 0) {
					if (htStyles.containsKey(FormatProperties.outlineFormat)) {
						if (!FormatProperties.outlineFormat.equals(DEFAULT_FORMAT)) {
							pbSave.setEnabled(true);
							getRootPane().setDefaultButton(pbSave);
							pbSaveAs.setEnabled(true);
							pbDelete.setEnabled(true);	
						} else {
							pbSave.setEnabled(false);
							pbSaveAs.setEnabled(true);
							getRootPane().setDefaultButton(pbSaveAs);
							pbDelete.setEnabled(false);										
						}
						File file2 = (File)htStyles.get(FormatProperties.outlineFormat);
						formatTableModel.loadData(file2);
						oTable.setModel(formatTableModel);
						oTable.repaint();
					} else {
						pbSave.setEnabled(false);
						pbSaveAs.setEnabled(false);
						getRootPane().setDefaultButton(pbCancel);
						pbDelete.setEnabled(false);																		
					}
				}
			}
		}
		catch(Exception ex) {
			ex.printStackTrace();
			ProjectCompendium.APP.displayError("Exception: (UIHTMLFormatDialog.reloadData) " + ex.getMessage());
		}		
	}
	
	/**
	 * Handle action events coming from the buttons.
	 * @param evt, the associated ActionEvent object.
	 */
	public void actionPerformed(ActionEvent evt) {
		Object source = evt.getSource();

		if (source instanceof JButton) {

			if (source == pbHead) {
				FormatTableModel model = (FormatTableModel)oTable.getModel();
				model.applyHeadingSettings();
				oTable.invalidate();
				oTable.repaint();
			}
			else if (source == pbRow) {
				FormatTableModel model = (FormatTableModel)oTable.getModel();
				int row = oTable.getSelectedRow();
				int column = oTable.getSelectedColumn();
				if (row != -1 && column != -1) {
					model.cascadeRow(row, column);
					oTable.invalidate();
					oTable.repaint();
				}
				else {
					ProjectCompendium.APP.displayMessage("Please select a cell first", "Format options");
				}
			}
			if (source == pbColumn) {
				FormatTableModel model = (FormatTableModel)oTable.getModel();
				int row = oTable.getSelectedRow();
				int column = oTable.getSelectedColumn();
				if (row != -1 && column != -1) {
					model.cascadeColumn(row, column);
					oTable.invalidate();
					oTable.repaint();
				}
				else {
					ProjectCompendium.APP.displayMessage("Please select a cell first", "Format options");
				}
			}
			else {
				if (source == pbSave) {
					formatTableModel.storeData();
					String sName = (String)oStyles.getSelectedItem();
					FormatProperties.outlineFormat = sName;
					FormatProperties.setFormatProp("outlineFormat", sName);
					FormatProperties.saveFormatProps();
					onCancel();
				} else if (source == pbSaveAs) {
					boolean bNameExists = true;
					while(bNameExists) {
				   		String sNewName = JOptionPane.showInputDialog("Enter the name for the new outline format");
						sNewName = sNewName.trim();

						bNameExists = false;

						if (sNewName.equals("")) {
						   	JOptionPane.showMessageDialog(this, "You did not enter a name, so the save has not been performed\n");
						}
						else if (vtStyles.contains(sNewName)) {
							JOptionPane.showMessageDialog(this, "An outline format with that name already exists. Please try again.\n");
							bNameExists = true;
						} else {
							formatTableModel.storeDataAsNew(sNewName);
							FormatProperties.outlineFormat = sNewName;
							FormatProperties.setFormatProp("outlineFormat", sNewName);
							FormatProperties.saveFormatProps();
							reloadData();
						}
					}
				} else if (source == pbDelete) {
					String sName = (String)oStyles.getSelectedItem();
			  		int answer = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete the file: "+sName, "Delete Style",
							JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

					if (answer == JOptionPane.YES_OPTION) {	
						try {
							File file = (File)htStyles.get(sName);
							Properties prop = new Properties();
							prop.load(new FileInputStream(file));
							prop.setProperty( "status", "deleted" );										
							prop.store(new FileOutputStream(file), "Outline Format Data");
	
							CoreUtilities.deleteFile(file);
							FormatProperties.outlineFormat = DEFAULT_FORMAT;
							FormatProperties.setFormatProp("outlineFormat", DEFAULT_FORMAT);
							FormatProperties.saveFormatProps();
							reloadData();
						} catch (Exception e) {}
					}
				} else if (source == pbCancel) {	
					onCancel();
				}
			}
		}
	}

	/**
	 * The model for the table of format options.
	 */
	class FormatTableModel extends AbstractTableModel {

		private String[] columnNames = {"Item",	"Left Margin", "Top Margin",
										"Font",	"Size",
										"Style", "Background", "Text Color"};
		private Object[][] data;
		private int columnCount = 8;
		private File file = null;
		private Properties styleProp = null;

		public FormatTableModel() {
			data = new Object [ROW_COUNT][columnCount];
			for (int i=0; i<ROW_COUNT; i++) {	
				switch (i) {
					case 0: data[i][0] = "Heading Label"; break;
					case 1: data[i][0] = "Heading Detail"; break;
					case 2: data[i][0] = "Heading Detail Date"; break;
					case 3: data[i][0] = "Heading Reference"; break;
					case 4: data[i][0] = "Heading Author"; break;
					case 5: data[i][0] = "Heading Tags"; break;
					case 6: data[i][0] = "Heading Views"; break;

					case 7: data[i][0] = "Level 1 Label"; break;
					case 8: data[i][0] = "Level 1 Detail"; break;
					case 9: data[i][0] = "Level 1 Detail Date"; break;
					case 10: data[i][0] = "Level 1 Reference"; break;					
					case 11: data[i][0] = "Level 1 Author"; break;
					case 12: data[i][0] = "Level 1 Tags"; break;
					case 13: data[i][0] = "Level 1 Views"; break;

					case 14: data[i][0] = "Level 2 Label"; break;
					case 15: data[i][0] = "Level 2 Detail"; break;
					case 16: data[i][0] = "Level 2 Detail Date"; break;
					case 17: data[i][0] = "Level 2 Reference"; break;					
					case 18: data[i][0] = "Level 2 Author"; break;
					case 19: data[i][0] = "Level 2 Tags"; break;
					case 20: data[i][0] = "Level 2 Views"; break;
					
					case 21: data[i][0] = "Level 3 Label"; break;
					case 22: data[i][0] = "Level 3 Detail"; break;
					case 23: data[i][0] = "Level 3 Detail Date"; break;	
					case 24: data[i][0] = "Level 3 Reference"; break;										
					case 25: data[i][0] = "Level 3 Author"; break;
					case 26: data[i][0] = "Level 3 Tags"; break;
					case 27: data[i][0] = "Level 3 Views"; break;

					case 28: data[i][0] = "Level 4 Label"; break;
					case 29: data[i][0] = "Level 4 Detail"; break;
					case 30: data[i][0] = "Level 4 Detail Date"; break;
					case 31: data[i][0] = "Level 4 Reference"; break;					
					case 32: data[i][0] = "Level 4 Author"; break;
					case 33: data[i][0] = "Level 4 Tags"; break;
					case 34: data[i][0] = "Level 4 Views"; break;

					case 35: data[i][0] = "Level 5 Label"; break;
					case 36: data[i][0] = "Level 5 Detail"; break;
					case 37: data[i][0] = "Level 5 Detail Date"; break;
					case 38: data[i][0] = "Level 5 Reference"; break;					
					case 39: data[i][0] = "Level 5 Author"; break;
					case 40: data[i][0] = "Level 5 Tags"; break;
					case 41: data[i][0] = "Level 5 Views"; break;

					case 42: data[i][0] = "Level 6 Label"; break;
					case 43: data[i][0] = "Level 6 Detail"; break;
					case 44: data[i][0] = "Level 6 Detail Date"; break;	
					case 45: data[i][0] = "Level 6 Reference"; break;										
					case 46: data[i][0] = "Level 6 Author"; break;
					case 47: data[i][0] = "Level 6 Tags"; break;
					case 48: data[i][0] = "Level 6 Views"; break;
				}
				
				data[i][1] = "";
				data[i][2] = "";
				data[i][3] = "";
				data[i][4] = "";
				data[i][5] = "";
				data[i][6] = "-1";
				data[i][7] = "-1";
			}
		}
		
		public void clearData() {
			for (int i=0; i<ROW_COUNT; i++) {	
				data[i][1] = "";
				data[i][2] = "";
				data[i][3] = "";
				data[i][4] = "";
				data[i][5] = "";
				data[i][6] = "-1";
				data[i][7] = "-1";
			}
			
			lblID.setText(" ");			
		}
		
		public void loadData(File file) {						
			this.file = file;
			try{
				styleProp = new Properties();
				styleProp.load(new FileInputStream(file));

				int j=0;
				String type = "";
	
				for (int i=0; i<ROW_COUNT; i++) {
	
					switch (i) {
						case 0: 
							j=0;
							type = "level"; 
						break;
						case 7: 
							j=1;
							type = "level"; 
						break;
						case 14: 
							j=2;
							type = "level"; 
						break;
						case 21: 
							j=3;
							type = "level"; 
						break;
						case 28: 
							j=4;
							type = "level"; 
						break;
						case 35: 
							j=5;
							type = "level"; 
						break;
						case 42: 
							j=6;
							type = "level"; 
						break;
							
						case 1: case 8: case 15: case 22: case 29: case 36: case 43: type = "detail"; break;
						case 2: case 9: case 16: case 23: case 30: case 37: case 44: type = "detaildate"; break;
						case 3: case 10: case 17: case 24: case 31: case 38: case 45: type = "reference"; break;
						case 4: case 11: case 18: case 25: case 32: case 39: case 46: type = "author"; break;
						case 5: case 12: case 19: case 26: case 33: case 40: case 47: type = "codes"; break;
						case 6: case 13: case 20: case 27: case 34: case 41: case 48: type = "views"; break;
					}
					
					data[i][1] = styleProp.getProperty( type+j+"indent" );
					
					String top = "";
					top = styleProp.getProperty( type+j+"top" );
					if (top == null) {
						top = "0.05";
					}
					data[i][2] = top;
					data[i][3] = styleProp.getProperty( type+j+"font" );
					data[i][4] = styleProp.getProperty( type+j+"size" );					
					data[i][5] = styleProp.getProperty( type+j+"style" );
					data[i][6] = styleProp.getProperty( type+j+"back" );
					data[i][7] = styleProp.getProperty( type+j+"color" );

					// LOAD OTHER DATA
					String id = styleProp.getProperty( "id" );
					lblID.setText(id);
					
					String menutextcol = styleProp.getProperty( "menutextcolor" );
					try {
						int color = (new Integer(menutextcol)).intValue();
						lblMenuFontColor.setBackground(new Color(color));	
					} catch(NumberFormatException ex) {
						lblMenuFontColor.setBackground(Color.white);
					}	

					String menubackcol = styleProp.getProperty( "menubackcolor" );
					try {
						int color = (new Integer(menubackcol)).intValue();
						lblMenuBackgroundColor.setBackground(new Color(color));	
					} catch(NumberFormatException ex) {
						lblMenuBackgroundColor.setBackground(Color.white);
					}	

					String menubordercol = styleProp.getProperty( "menubordercolor" );
					try {
						int color = (new Integer(menubordercol)).intValue();
						lblMenuBorderColor.setBackground(new Color(color));	
					} catch(NumberFormatException ex) {
						lblMenuBorderColor.setBackground(Color.white);
					}	

					String dividercol = styleProp.getProperty( "dividercolor" );
					try {
						int color = (new Integer(dividercol)).intValue();
						lblDividerColor.setBackground(new Color(color));	
					} catch(NumberFormatException ex) {
						lblDividerColor.setBackground(Color.white);
					}	

					cbMenuFontFamily.setSelectedItem(styleProp.getProperty( "menufontfamily" ));
					cbMenuFontSize.setSelectedItem(styleProp.getProperty( "menufontsize" ));
					cbMenuFontStyle.setSelectedItem(styleProp.getProperty( "menufontstyle" ));
					
				}
			} catch (Exception e) {
				//
			}				
		}

		public int getColumnCount() {
			return columnNames.length;
		}

		public int getRowCount() {
			return data.length;
		}

		public String getColumnName(int col) {
			return columnNames[col];
		}

		public Object getValueAt(int row, int col) {
			return data[row][col];
		}

		public Class getColumnClass(int c) {
			return getValueAt(0, c).getClass();
		}

	    public boolean isCellEditable(int row, int col) {
			if (col > 0)
				return true;
			return false;
		}

		public void applyHeadingSettings() {

			Object value = null;

			for (int i=6; i<ROW_COUNT; i++) {
				switch (i) {
					case 7: case 14: case 21: case 28: case 35: case 42: {
						for (int j=1; j<columnCount; j++) {
							value = getValueAt(0, j);
							if (value != null)
								setValueAt(value, i, j);
						}
					}
					break;

					case 8: case 15: case 22: case 29: case 36: case 43: {
						for (int j=1; j<columnCount; j++) {
							value = getValueAt(1, j);
							if (value != null)
								setValueAt(value, i, j);
						}
					}
					break;
					
					case 9: case 16: case 23: case 30: case 37: case 44: {
						for (int j=1; j<columnCount; j++) {
							value = getValueAt(2, j);
							if (value != null)
								setValueAt(value, i, j);
						}
					}
					break;

					case 10: case 17: case 24: case 31: case 38: case 45: {
						for (int j=1; j<columnCount; j++) {
							value = getValueAt(3, j);
							if (value != null)
								setValueAt(value, i, j);
						}
					}
					break;

					case 11: case 18: case 25: case 32: case 39: case 46: {
						for (int j=1; j<columnCount; j++) {
							value = getValueAt(4, j);
							if (value != null)
								setValueAt(value, i, j);
						}
					}
					break;

					case 12: case 19: case 26: case 33: case 40: case 47: {
						for (int j=1; j<columnCount; j++) {
							value = getValueAt(5, j);
							if (value != null)
								setValueAt(value, i, j);
						}
					}
					break;
					
					case 13: case 20: case 27: case 34: case 41: case 48: {
						for (int j=1; j<columnCount; j++) {
							value = getValueAt(6, j);
							if (value != null)
								setValueAt(value, i, j);
						}
					}
					break;					
				}
			}
		}

		public void cascadeRow(int row, int col) {
			String value = (String)getValueAt(row, col);

			switch (row) {
				case 0: case 7: case 14: case 21: case 28: case 35: case 42: {
					setValueAt((Object) value, 0, col);
					setValueAt((Object) value, 7, col);
					setValueAt((Object) value, 14, col);
					setValueAt((Object) value, 21, col);
					setValueAt((Object) value, 28, col);
					setValueAt((Object) value, 35, col);
					setValueAt((Object) value, 42, col);
				}
				break;

				case 1: case 8: case 15: case 22: case 29: case 36: case 43: {
					setValueAt((Object) value, 1, col);
					setValueAt((Object) value, 8, col);
					setValueAt((Object) value, 15, col);
					setValueAt((Object) value, 22, col);
					setValueAt((Object) value, 29, col);
					setValueAt((Object) value, 36, col);
					setValueAt((Object) value, 43, col);
				}
				break;

				case 2: case 9: case 16: case 23: case 30: case 37: case 44: {
					setValueAt((Object) value, 2, col);
					setValueAt((Object) value, 9, col);
					setValueAt((Object) value, 16, col);
					setValueAt((Object) value, 23, col);
					setValueAt((Object) value, 30, col);
					setValueAt((Object) value, 37, col);
					setValueAt((Object) value, 44, col);
				}
				break;

				case 3: case 10: case 17: case 24: case 31: case 38: case 45: {
					setValueAt((Object) value, 3, col);
					setValueAt((Object) value, 10, col);
					setValueAt((Object) value, 17, col);
					setValueAt((Object) value, 24, col);
					setValueAt((Object) value, 31, col);
					setValueAt((Object) value, 38, col);
					setValueAt((Object) value, 45, col);
				}
				break;

				case 4: case 11: case 18: case 25: case 32: case 39: case 46: {
					setValueAt((Object) value, 4, col);
					setValueAt((Object) value, 11, col);
					setValueAt((Object) value, 18, col);
					setValueAt((Object) value, 25, col);
					setValueAt((Object) value, 33, col);
					setValueAt((Object) value, 39, col);
					setValueAt((Object) value, 46, col);
				}
				break;
				
				case 5: case 12: case 19: case 26: case 33: case 40: case 47: {
					setValueAt((Object) value, 5, col);
					setValueAt((Object) value, 12, col);
					setValueAt((Object) value, 19, col);
					setValueAt((Object) value, 26, col);
					setValueAt((Object) value, 33, col);
					setValueAt((Object) value, 40, col);
					setValueAt((Object) value, 47, col);
				}
				break;		
				
				case 6: case 13: case 20: case 27: case 34: case 41: case 48: {
					setValueAt((Object) value, 6, col);
					setValueAt((Object) value, 13, col);
					setValueAt((Object) value, 20, col);
					setValueAt((Object) value, 27, col);
					setValueAt((Object) value, 34, col);
					setValueAt((Object) value, 41, col);
					setValueAt((Object) value, 48, col);
				}
				break;								
			}
		}

		public void cascadeColumn(int row, int col) {
			String value = (String)getValueAt(row, col);

			for (int i=0; i<ROW_COUNT; i++) {
				setValueAt((Object)value, i, col);
			}
		}

        public void setValueAt(Object aValue, int row, int column) {
			data[row][column] = aValue;
		}

		public void storeData() {
			try {
				int j=-1;
				String type = "";
	
				for (int i=0; i<ROW_COUNT; i++) {
	
					switch (i) {
						case 0: case 7: case 14: case 21: case 28: case 35: case 42: 
							type = "level"; j++;
						break;
						
						case 1: case 8: case 15: case 22: case 29: case 36: case 43: 
							type = "detail";
						break;
						
						case 2: case 9: case 16: case 23: case 30: case 37: case 44: 
							type = "detaildate";
						break;
	
						case 3: case 10: case 17: case 24: case 31: case 38: case 45: 
							type = "reference";
						break;
	
						case 4: case 11: case 18: case 25: case 32: case 39: case 46: 
							type = "author";
						break;
	
						case 5: case 12: case 19: case 26: case 33: case 40: case 47: 
							type = "codes";
						break;
	
						case 6: case 13: case 20: case 27: case 34: case 41: case 48: 
							type = "views";
						break;
					}
					styleProp.setProperty( type+j+"indent", (String)data[i][1] );
					styleProp.setProperty( type+j+"top", (String)data[i][2] );					
					styleProp.setProperty( type+j+"font", (String)data[i][3] );
					styleProp.setProperty( type+j+"size", (String)data[i][4] );
					styleProp.setProperty( type+j+"style", (String)data[i][5] );
	
					String backcolor = (String)data[i][6];
					if (backcolor == null) {
						backcolor = "-1";
					}
					
					String color = (String)data[i][7];
					if (color == null) {
						color = "-1";
					}					
					styleProp.setProperty( type+j+"back",  backcolor);
					styleProp.setProperty( type+j+"color", color );			
					
					// MENU SETTINGS
		            Color colour = lblMenuFontColor.getBackground();
					styleProp.setProperty( "menutextcolor", String.valueOf(colour.getRGB()) );	
					
					Color back = lblMenuBackgroundColor.getBackground();
					styleProp.setProperty( "menubackcolor", String.valueOf(back.getRGB()) );

					Color border = lblMenuBorderColor.getBackground();
					styleProp.setProperty( "menubordercolor", String.valueOf(border.getRGB()) );

					Color divider = lblDividerColor.getBackground();
					styleProp.setProperty( "dividercolor", String.valueOf(divider.getRGB()) );
					
					styleProp.setProperty("menufontfamily", (String)cbMenuFontFamily.getSelectedItem());
					styleProp.setProperty("menufontsize", (String)cbMenuFontSize.getSelectedItem());
					styleProp.setProperty("menufontstyle", (String)cbMenuFontStyle.getSelectedItem());
				}
				styleProp.store(new FileOutputStream(file), "Outline Format Data");
				
			} catch (Exception e) {
				ProjectCompendium.APP.displayError("Unable to save format due to:\n\n"+e.getMessage());
			}
		}
		
		public void storeDataAsNew(String sName) {

			String sUniqueID = ProjectCompendium.APP.getModel().getUniqueID();
			try{
				Properties newProp = new Properties();
			
				int j=-1;
				String type = "";
	
				for (int i=0; i<ROW_COUNT; i++) {
	
					switch (i) {
						case 0: case 7: case 14: case 21: case 28: case 35: case 42: 
							type = "level"; j++;
						break;
						
						case 1: case 8: case 15: case 22: case 29: case 36: case 43: 
							type = "detail";
						break;
						
						case 2: case 9: case 16: case 23: case 30: case 37: case 44: 
							type = "detaildate";
						break;
	
						case 3: case 10: case 17: case 24: case 31: case 38: case 45: 
							type = "reference";
						break;
	
						case 4: case 11: case 18: case 25: case 32: case 39: case 46: 
							type = "author";
						break;
	
						case 5: case 12: case 19: case 26: case 33: case 40: case 47: 
							type = "codes";
						break;
	
						case 6: case 13: case 20: case 27: case 34: case 41: case 48: 
							type = "views";
						break;
					}
	
					newProp.setProperty( type+j+"indent", (String)data[i][1] );
					newProp.setProperty( type+j+"top", (String)data[i][2] );					
					newProp.setProperty( type+j+"font", (String)data[i][3] );
					newProp.setProperty( type+j+"size", (String)data[i][4] );
					newProp.setProperty( type+j+"style", (String)data[i][5] );
	
					String backcolor = (String)data[i][6];
					if (backcolor == null) {
						backcolor = "-1";
					}
					
					String color = (String)data[i][7];
					if (color == null) {
						color = "-1";
					}
					
					newProp.setProperty( type+j+"back",  backcolor);
					newProp.setProperty( type+j+"color", color );				
				}
				
				newProp.setProperty( "name",  sName);
				newProp.setProperty( "id", sUniqueID );				
				newProp.setProperty( "status", "active" );	
				
				// MENU SETTINGS
	            Color colour = lblMenuFontColor.getBackground();
				newProp.setProperty( "menutextcolor", String.valueOf(colour.getRGB()) );
				
				Color back = lblMenuBackgroundColor.getBackground();
				newProp.setProperty( "menubackcolor", String.valueOf(back.getRGB()) );

				Color border = lblMenuBorderColor.getBackground();
				newProp.setProperty( "menubordercolor", String.valueOf(border.getRGB()) );

				Color divider = lblDividerColor.getBackground();
				newProp.setProperty( "dividercolor", String.valueOf(divider.getRGB()) );

				newProp.setProperty("menufontfamily", (String)cbMenuFontFamily.getSelectedItem());
				newProp.setProperty("menufontsize", (String)cbMenuFontSize.getSelectedItem());
				newProp.setProperty("menufontstyle", (String)cbMenuFontStyle.getSelectedItem());

				newProp.store(new FileOutputStream(DEFAULT_FILE_PATH+ProjectCompendium.sFS+sUniqueID+".properties"), "Outline Format Data");
			} catch (Exception e) {
				ProjectCompendium.APP.displayError("Unable to save format due to:\n\n"+e.getMessage());				
			}
		}		
	}

	/**
	 * Renderer for the cells in the table.
	 */
	private DefaultTableCellRenderer createCellRenderer() {

     	DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
			public Component getTableCellRendererComponent(JTable table,
                                               Object value,
                                               boolean isSelected,
                                               boolean hasFocus,
                                               int row,
                                               int column) {
				setValue(value);
		 		if (isSelected) {
					if (column == 6 || column == 7) {
						try {
							int color = (new Integer((String)value)).intValue();
							setBackground(new Color(color));
							setForeground(new Color(color));									
						} catch(NumberFormatException ex) {
							setBackground(table.getSelectionBackground());
							setForeground(table.getSelectionBackground());
						}
					} else {
						setBackground(table.getSelectionBackground());
						setForeground(table.getSelectionForeground());
					}
				}
				else {
					switch( row ) {
						case 7: case 8: case 9: case 10: case 11: case 12: case 13:
						case 21: case 22: case 23: case 24: case 25: case 26: case 27:
						case 35: case 36: case 37: case 38: case 39: case 40: case 41:							
							if (column == 6 || column == 7) {
								try {
									int color = (new Integer((String)value)).intValue();
									setBackground(new Color(color));
									setForeground(new Color(color));									
								} catch(NumberFormatException ex) {
									setBackground(new Color(220,220,255));
									setForeground(new Color(220,220,255));
								}
							} else {
								setBackground(new Color(220,220,255));
								setForeground(Color.black);
							}
						break;
						default:
							if (column == 6 || column == 7) {
								try {
									int color = (new Integer((String)value)).intValue();
									setBackground(new Color(color));	
									setForeground(new Color(color));
								} catch(NumberFormatException ex) {
									setBackground(Color.white);
									setForeground(Color.white);									
								}
							} else {
								setBackground(table.getBackground());
								setForeground(table.getForeground());								
							}							
						break;
					}
				}
		 		
				if (column == 6 || column == 7) {
					setBorder(new BevelBorder(BevelBorder.RAISED));
				}
                return this;
			}
        };
		return renderer;
	}

	/**
	 * Draws the elements of the font list
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
			String text = (String)value;
			String font = (String)value;
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
}
