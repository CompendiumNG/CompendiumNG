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
import java.io.*;

import javax.swing.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;
import javax.swing.event.*;
import javax.swing.border.*;
import javax.swing.table.*;

import com.compendium.LanguageProperties;
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

	public static int   TYPE_COUNT	= 7;
	
	public static int 	LEVEL_COUNT = 11; // needs to be 1 more than number of required levels as includes header
	
	/** The number of rows in the table.*/
	public static int 	ROW_COUNT	=	TYPE_COUNT*LEVEL_COUNT;
	
	/** The path to the formats.*/
	public static String DEFAULT_FILE_PATH = "System"+ProjectCompendium.sFS+"resources"+ProjectCompendium.sFS+"OutlineStyles"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	
	/** The name of the default file.*/
	public static String DEFAULT_FILE_NAME = "Default.properties"; //$NON-NLS-1$
	
	/** The name of the default format style.*/
	private static String DEFAULT_FORMAT = "Default"; //$NON-NLS-1$
	
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

	 	this.setTitle(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIHTMLFormatDialog.htmlFormattingOptions")); //$NON-NLS-1$
	  	oParent = parent;

		oContentPane = getContentPane();

		String[] indent = {"0", "0.25", "0.5", "0.75","1.0","1.25","1.5", "1.75", "2.0", "2.25", "2.5", "3.0", "3.25", "3.5", "3.75", "4.0"}; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$ //$NON-NLS-10$ //$NON-NLS-11$ //$NON-NLS-12$ //$NON-NLS-13$ //$NON-NLS-14$ //$NON-NLS-15$ //$NON-NLS-16$
		String[] topmargin = {"-0.1", "-0.05", "0", "0.05", "0.1", "0.15", "0.2","0.25","0.3", "0.35", "0.4", "0.45", "0.5", "0.55", "0.6", "0.65", "0.7", "0.75", "0.8", "0.85", "0.9", "0.95", "1.0"};		 //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$ //$NON-NLS-10$ //$NON-NLS-11$ //$NON-NLS-12$ //$NON-NLS-13$ //$NON-NLS-14$ //$NON-NLS-15$ //$NON-NLS-16$ //$NON-NLS-17$ //$NON-NLS-18$ //$NON-NLS-19$ //$NON-NLS-20$ //$NON-NLS-21$ //$NON-NLS-22$ //$NON-NLS-23$
   	 	String[] fonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
		String[] sizes = {"6","7","8","9","10","11","12","13","14","15","16","17","18","19","20","21","22","23","24","25","26","27","28","29","30","31","32","33","34","35","36","37","38","39","40"}; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$ //$NON-NLS-10$ //$NON-NLS-11$ //$NON-NLS-12$ //$NON-NLS-13$ //$NON-NLS-14$ //$NON-NLS-15$ //$NON-NLS-16$ //$NON-NLS-17$ //$NON-NLS-18$ //$NON-NLS-19$ //$NON-NLS-20$ //$NON-NLS-21$ //$NON-NLS-22$ //$NON-NLS-23$ //$NON-NLS-24$ //$NON-NLS-25$ //$NON-NLS-26$ //$NON-NLS-27$ //$NON-NLS-28$ //$NON-NLS-29$ //$NON-NLS-30$ //$NON-NLS-31$ //$NON-NLS-32$ //$NON-NLS-33$ //$NON-NLS-34$ //$NON-NLS-35$
		String[] styles = {LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIHTMLFormatDialog.normal"),LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIHTMLFormatDialog.bold"),LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIHTMLFormatDialog.italic"), LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIHTMLFormatDialog.boldItalic")}; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$

		UIButtonPanel oButtonPanel = new UIButtonPanel();

		pbSave = new UIButton(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIHTMLFormatDialog.saveButton")); //$NON-NLS-1$
		pbSave.setMnemonic(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIHTMLFormatDialog.saveButtonMnemonic").charAt(0)); //$NON-NLS-1$
		pbSave.setEnabled(false);
		pbSave.addActionListener(this);
		oButtonPanel.addButton(pbSave);

		pbSaveAs = new UIButton(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIHTMLFormatDialog.saveAsButton")); //$NON-NLS-1$
		pbSaveAs.setMnemonic(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIHTMLFormatDialog.saveAsButtonMnemonic").charAt(0)); //$NON-NLS-1$
		pbSaveAs.setEnabled(false);
		pbSaveAs.addActionListener(this);
		oButtonPanel.addButton(pbSaveAs);

		pbDelete = new UIButton(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIHTMLFormatDialog.deleteButton")); //$NON-NLS-1$
		pbDelete.setMnemonic(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIHTMLFormatDialog.deleteButtonMnemonic").charAt(0)); //$NON-NLS-1$
		pbDelete.setEnabled(false);
		pbDelete.addActionListener(this);
		oButtonPanel.addButton(pbDelete);

		pbCancel = new UIButton(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIHTMLFormatDialog.cancelButton")); //$NON-NLS-1$
		pbCancel.setMnemonic(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIHTMLFormatDialog.cancelButtonMnemonic").charAt(0));//$NON-NLS-1$
		pbCancel.addActionListener(this);
		oButtonPanel.addButton(pbCancel);

		pbHelp = new UIButton(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIHTMLFormatDialog.helpButton")); //$NON-NLS-1$
		pbHelp.setMnemonic(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIHTMLFormatDialog.helpButtonMnemonic").charAt(0)); //$NON-NLS-1$);
		ProjectCompendium.APP.mainHB.enableHelpOnButton(pbHelp, "io.exportFormat", ProjectCompendium.APP.mainHS); //$NON-NLS-1$
		oButtonPanel.addHelpButton(pbHelp);
		
		JPanel mainpanel = new JPanel(new BorderLayout());
		mainpanel.setBorder(new EmptyBorder(10,10,10,10));

		lblID = new JLabel(" "); //$NON-NLS-1$
		lblID.setHorizontalAlignment(SwingConstants.LEFT);
		lblID.setFont(new Font("Dialog", Font.PLAIN, 12)); //$NON-NLS-1$
				
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

		JLabel lblMenu = new JLabel(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIHTMLFormatDialog.formatMenuAndDivider")); //$NON-NLS-1$
		lblMenu.setFont(new Font("Dialog", Font.BOLD, 12)); //$NON-NLS-1$
		gc.gridy = y;
		gc.gridwidth = 4;
		gc.weightx = 100;
		y++;
		grid.addLayoutComponent(lblMenu, gc);
		menupanel.add(lblMenu);
		
		JLabel lblFontFamily = new JLabel(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIHTMLFormatDialog.font")+": "); //$NON-NLS-1$
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
		
		JLabel lblTextColor = new JLabel(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIHTMLFormatDialog.textColor")+": "); //$NON-NLS-1$
		gc.gridy = y;
		gc.gridwidth = 1;
		grid.addLayoutComponent(lblTextColor, gc);
		menupanel.add(lblTextColor);

		lblMenuFontColor = new JLabel("                              "); //$NON-NLS-1$
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
		
		JLabel lblMenuBackground = new JLabel(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIHTMLFormatDialog.menuBackground")+": "); //$NON-NLS-1$
		gc.gridy = y;
		gc.gridwidth = 1;
		grid.addLayoutComponent(lblMenuBackground, gc);
		menupanel.add(lblMenuBackground);

		lblMenuBackgroundColor = new JLabel("                              "); //$NON-NLS-1$
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

		JLabel lblMenuBorder = new JLabel(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIHTMLFormatDialog.menuBorder")+": "); //$NON-NLS-1$
		gc.gridy = y;
		gc.gridwidth = 1;
		grid.addLayoutComponent(lblMenuBorder, gc);
		menupanel.add(lblMenuBorder);

		lblMenuBorderColor = new JLabel("                              "); //$NON-NLS-1$
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

		JLabel lblDivider = new JLabel(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIHTMLFormatDialog.viewDivider")+": "); //$NON-NLS-1$
		gc.gridy = y;
		gc.gridwidth = 1;
		grid.addLayoutComponent(lblDivider, gc);
		menupanel.add(lblDivider);

		lblDividerColor = new JLabel("                              "); //$NON-NLS-1$
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
		JLabel lblTable = new JLabel(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIHTMLFormatDialog.formatNodeData")); //$NON-NLS-1$
		lblTable.setFont(new Font("Dialog", Font.BOLD, 12)); //$NON-NLS-1$
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
		oTable.getColumn(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIHTMLFormatDialog.itemColumn")).setPreferredWidth(100); //$NON-NLS-1$
		oTable.getColumn(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIHTMLFormatDialog.leftMarginColumn")).setPreferredWidth(35); //$NON-NLS-1$
		oTable.getColumn(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIHTMLFormatDialog.topMarginColumn")).setPreferredWidth(35); //$NON-NLS-1$
		oTable.getColumn(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIHTMLFormatDialog.fontColumn")).setPreferredWidth(50); //$NON-NLS-1$
		oTable.getColumn(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIHTMLFormatDialog.sizeColumn")).setPreferredWidth(20); //$NON-NLS-1$
		oTable.getColumn(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIHTMLFormatDialog.styleColumn")).setPreferredWidth(50); //$NON-NLS-1$
		oTable.getColumn(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIHTMLFormatDialog.backgroundColumn")).setPreferredWidth(50); //$NON-NLS-1$
		oTable.getColumn(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIHTMLFormatDialog.textColorColumn")).setPreferredWidth(50); //$NON-NLS-1$
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

		pbHead = new UIButton(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIHTMLFormatDialog.cascadeHeadingSettings")); //$NON-NLS-1$
		pbHead.setToolTipText(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIHTMLFormatDialog.cascadeHeadingSettingsTip")); //$NON-NLS-1$
		pbHead.addActionListener(this);
		panel.add(pbHead);

		pbRow = new UIButton(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIHTMLFormatDialog.copyToLikeRows")); //$NON-NLS-1$
		pbRow.setToolTipText(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIHTMLFormatDialog.copyToLikeRowsTip")); //$NON-NLS-1$
		pbRow.addActionListener(this);
		panel.add(pbRow);

		pbColumn = new UIButton(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIHTMLFormatDialog.copyWhileColumn")); //$NON-NLS-1$
		pbColumn.setToolTipText(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIHTMLFormatDialog.copyWhileColumnTip")); //$NON-NLS-1$
		pbColumn.addActionListener(this);
		panel.add(pbColumn);

		centerpanel.add(panel, BorderLayout.SOUTH);
		
		mainpanel.add(centerpanel, BorderLayout.CENTER);

		// MAINPANEL - NORTH. NOTE: needs to be below MAINPANEL - CENTER for variable referencing.
		JLabel lblStyle = new JLabel(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIHTMLFormatDialog.formatStyle")); //$NON-NLS-1$
		lblStyle.setFont(new Font("Dialog", Font.PLAIN, 12)); //$NON-NLS-1$

		JLabel lblFormatID = new JLabel(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIHTMLFormatDialog.formatID")); //$NON-NLS-1$
		lblFormatID.setFont(new Font("Dialog", Font.PLAIN, 12)); //$NON-NLS-1$

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
		choicebox.setFont( new Font("Dialog", Font.PLAIN, 12 )); //$NON-NLS-1$
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
		oStyles.setFont( new Font("Dialog", Font.PLAIN, 12 )); //$NON-NLS-1$

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
            	Thread choiceThread = new Thread("UIHTMLFormatDialog.createStylesChoiceBox") { //$NON-NLS-1$
                	public void run() {
						if (oStyles != null) {
							String selected = (String)oStyles.getSelectedItem();
							if (htStyles.containsKey(selected)) {
								if (!selected.equals("Default")) { //$NON-NLS-1$
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
			String sName = ""; //$NON-NLS-1$
			String value = ""; //$NON-NLS-1$
			String sFileName = ""; //$NON-NLS-1$
			int index = 0;
			int j = 0;
			if (styles.length > 0) {			
				for (int i=0; i<styles.length; i++) {
					file = styles[i];
					sFileName = file.getName();
					if (!sFileName.startsWith(".") && sFileName.endsWith(".properties")) { //$NON-NLS-1$ //$NON-NLS-2$
						Properties styleProp = new Properties();
						styleProp.load(new FileInputStream(file));
						value = styleProp.getProperty("status"); //$NON-NLS-1$
						if (value.equals("active")) { //$NON-NLS-1$
							value = styleProp.getProperty("name"); //$NON-NLS-1$
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
				vtStyles.insertElementAt("< Select An Outline Format >", 0); //$NON-NLS-1$
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
			ProjectCompendium.APP.displayError("Exception: (UIHTMLFormatDialog.reloadData) " + ex.getMessage()); //$NON-NLS-1$
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
					ProjectCompendium.APP.displayMessage(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIHTMLFormatDialog.selectCell"), LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIHTMLFormatDialog.formatOptions")); //$NON-NLS-1$ //$NON-NLS-2$
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
					ProjectCompendium.APP.displayMessage(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIHTMLFormatDialog.selectCell"), LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIHTMLFormatDialog.formatOptions")); //$NON-NLS-1$ //$NON-NLS-2$
				}
			}
			else {
				if (source == pbSave) {
					formatTableModel.storeData();
					String sName = (String)oStyles.getSelectedItem();
					FormatProperties.outlineFormat = sName;
					FormatProperties.setFormatProp("outlineFormat", sName); //$NON-NLS-1$
					FormatProperties.saveFormatProps();
					onCancel();
				} else if (source == pbSaveAs) {
					boolean bNameExists = true;
					while(bNameExists) {
				   		String sNewName = JOptionPane.showInputDialog(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIHTMLFormatDialog.newName")); //$NON-NLS-1$
						sNewName = sNewName.trim();

						bNameExists = false;

						if (sNewName.equals("")) { //$NON-NLS-1$
						   	JOptionPane.showMessageDialog(this, LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIHTMLFormatDialog.noName")+"\n"); //$NON-NLS-1$
						}
						else if (vtStyles.contains(sNewName)) {
							JOptionPane.showMessageDialog(this, LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIHTMLFormatDialog.nameExists")+"\n"); //$NON-NLS-1$
							bNameExists = true;
						} else {
							formatTableModel.storeDataAsNew(sNewName);
							FormatProperties.outlineFormat = sNewName;
							FormatProperties.setFormatProp("outlineFormat", sNewName); //$NON-NLS-1$
							FormatProperties.saveFormatProps();
							reloadData();
						}
					}
				} else if (source == pbDelete) {
					String sName = (String)oStyles.getSelectedItem();
			  		int answer = JOptionPane.showConfirmDialog(this, LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIHTMLFormatDialog.deleteCheck")+": "+sName, LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIHTMLFormatDialog.deleteStyle"), //$NON-NLS-1$ //$NON-NLS-2$
							JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

					if (answer == JOptionPane.YES_OPTION) {	
						try {
							File file = (File)htStyles.get(sName);
							Properties prop = new Properties();
							prop.load(new FileInputStream(file));
							prop.setProperty( "status", "deleted" );										 //$NON-NLS-1$ //$NON-NLS-2$
							prop.store(new FileOutputStream(file), "Outline Format Data"); //$NON-NLS-1$
	
							CoreUtilities.deleteFile(file);
							FormatProperties.outlineFormat = DEFAULT_FORMAT;
							FormatProperties.setFormatProp("outlineFormat", DEFAULT_FORMAT); //$NON-NLS-1$
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
	private class FormatTableModel extends AbstractTableModel {

		private String[] columnNames = {LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIHTMLFormatDialog.itemColumn"),	LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIHTMLFormatDialog.leftMarginColumn"), LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIHTMLFormatDialog.topMarginColumn"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIHTMLFormatDialog.fontColumn"),	LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIHTMLFormatDialog.sizeColumn"), //$NON-NLS-1$ //$NON-NLS-2$
				LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIHTMLFormatDialog.styleColumn"), LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIHTMLFormatDialog.backgroundColumn"), LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIHTMLFormatDialog.textColorColumn")}; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		
		private Object[][] data;
		private int columnCount = 8;
		private File file = null;
		private Properties styleProp = null;

		public FormatTableModel() {

			data = new Object [ROW_COUNT][columnCount];

			for (int i=0; i<ROW_COUNT; i++) {	

				int level = new Double(Math.floor(i/TYPE_COUNT)).intValue();
				int type = i%TYPE_COUNT;

				if (level == 0) {				
					switch (i) {
					case 0: data[i][0] = LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIHTMLFormatDialog.headingLabel"); break; //$NON-NLS-1$
					case 1: data[i][0] = LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIHTMLFormatDialog.headingDetail"); break; //$NON-NLS-1$
					case 2: data[i][0] = LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIHTMLFormatDialog.headingDetailDate"); break; //$NON-NLS-1$
					case 3: data[i][0] = LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIHTMLFormatDialog.headingReference"); break; //$NON-NLS-1$
					case 4: data[i][0] = LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIHTMLFormatDialog.headingAuthor"); break; //$NON-NLS-1$
					case 5: data[i][0] = LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIHTMLFormatDialog.headingTags"); break; //$NON-NLS-1$
					case 6: data[i][0] = LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIHTMLFormatDialog.headingViews"); break; //$NON-NLS-1$
					}
				} else {
					switch (type) {
					case 0: data[i][0] = LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIHTMLFormatDialog.level")+" "+level+" "+LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIHTMLFormatDialog.labelTitle"); break; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
					case 1: data[i][0] = LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIHTMLFormatDialog.level")+" "+level+" "+LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIHTMLFormatDialog.detailTitle"); break; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
					case 2: data[i][0] = LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIHTMLFormatDialog.level")+" "+level+" "+LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIHTMLFormatDialog.detailDateTitle"); break; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
					case 3: data[i][0] = LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIHTMLFormatDialog.level")+" "+level+" "+LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIHTMLFormatDialog.referenceTitle"); break; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
					case 4: data[i][0] = LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIHTMLFormatDialog.level")+" "+level+" "+LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIHTMLFormatDialog.authorTitle"); break; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
					case 5: data[i][0] = LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIHTMLFormatDialog.level")+" "+level+" "+LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIHTMLFormatDialog.tagsTitle"); break; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
					case 6: data[i][0] = LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIHTMLFormatDialog.level")+" "+level+" "+LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIHTMLFormatDialog.viewsTitle"); break; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
					}
				}

				data[i][1] = ""; //$NON-NLS-1$
				data[i][2] = ""; //$NON-NLS-1$
				data[i][3] = ""; //$NON-NLS-1$
				data[i][4] = ""; //$NON-NLS-1$
				data[i][5] = ""; //$NON-NLS-1$
				data[i][6] = "-1"; //$NON-NLS-1$
				data[i][7] = "-1"; //$NON-NLS-1$
			}
		}

		public void clearData() {
			for (int i=0; i<ROW_COUNT; i++) {	
				data[i][1] = ""; //$NON-NLS-1$
				data[i][2] = ""; //$NON-NLS-1$
				data[i][3] = ""; //$NON-NLS-1$
				data[i][4] = ""; //$NON-NLS-1$
				data[i][5] = ""; //$NON-NLS-1$
				data[i][6] = "-1"; //$NON-NLS-1$
				data[i][7] = "-1"; //$NON-NLS-1$
			}

			lblID.setText(" ");			 //$NON-NLS-1$
		}

		public void loadData(File file) {						
			this.file = file;
			try{
				styleProp = new Properties();
				styleProp.load(new FileInputStream(file));

				int level=0;
				int typeNumber = -1;
				String type = ""; //$NON-NLS-1$

				for (int i=0; i<ROW_COUNT; i++) {
					level = new Double(Math.floor(i/TYPE_COUNT)).intValue();
					type = getType(i);	
					
					String indent = styleProp.getProperty( type+level+"indent" );
					if (indent == null) {
						indent = "0"; //$NON-NLS-1$
					}					
					data[i][1] = indent; //$NON-NLS-1$					
					
					String top = ""; //$NON-NLS-1$
					top = styleProp.getProperty( type+level+"top" ); //$NON-NLS-1$
					if (top == null) {
						top = "0.05"; 	//$NON-NLS-1$
					}
					data[i][2] = top;
					
					String font = styleProp.getProperty( type+level+"font" );
					if (font == null) {
						font = "Verdana";//$NON-NLS-1$
					}
					data[i][3] = font; //$NON-NLS-1$
					
					String size = styleProp.getProperty( type+level+"size" );
					if (size == null) {
						size = "8";		//$NON-NLS-1$
					}
					data[i][4] = size; //$NON-NLS-1$
					
					String style = styleProp.getProperty( type+level+"style" );
					if (style == null) {
						style = LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIHTMLFormatDialog.normal");	//$NON-NLS-1$
					}
					data[i][5] = style; //$NON-NLS-1$
					
					String back = styleProp.getProperty( type+level+"back" );
					if (back == null) {
						back = "-1";	//$NON-NLS-1$
					}
					data[i][6] = back; 	//$NON-NLS-1$
					
					String textcolor = styleProp.getProperty( type+level+"color" );
					if (textcolor == null) {
						textcolor = "-1";	//$NON-NLS-1$
					}
					data[i][7] = textcolor; //$NON-NLS-1$

					// LOAD OTHER DATA
					String id = styleProp.getProperty( "id" ); //$NON-NLS-1$
					lblID.setText(id);

					String menutextcol = styleProp.getProperty( "menutextcolor" ); //$NON-NLS-1$
					try {
						int color = (new Integer(menutextcol)).intValue();
						lblMenuFontColor.setBackground(new Color(color));	
					} catch(NumberFormatException ex) {
						lblMenuFontColor.setBackground(Color.white);
					}	

					String menubackcol = styleProp.getProperty( "menubackcolor" ); //$NON-NLS-1$
					try {
						int color = (new Integer(menubackcol)).intValue();
						lblMenuBackgroundColor.setBackground(new Color(color));	
					} catch(NumberFormatException ex) {
						lblMenuBackgroundColor.setBackground(Color.white);
					}	

					String menubordercol = styleProp.getProperty( "menubordercolor" ); //$NON-NLS-1$
					try {
						int color = (new Integer(menubordercol)).intValue();
						lblMenuBorderColor.setBackground(new Color(color));	
					} catch(NumberFormatException ex) {
						lblMenuBorderColor.setBackground(Color.white);
					}	

					String dividercol = styleProp.getProperty( "dividercolor" ); //$NON-NLS-1$
					try {
						int color = (new Integer(dividercol)).intValue();
						lblDividerColor.setBackground(new Color(color));	
					} catch(NumberFormatException ex) {
						lblDividerColor.setBackground(Color.white);
					}	

					cbMenuFontFamily.setSelectedItem(styleProp.getProperty( "menufontfamily" )); //$NON-NLS-1$
					cbMenuFontSize.setSelectedItem(styleProp.getProperty( "menufontsize" )); //$NON-NLS-1$
					cbMenuFontStyle.setSelectedItem(styleProp.getProperty( "menufontstyle" )); //$NON-NLS-1$

				}
			} catch (Exception e) {
				System.out.println(e.getLocalizedMessage());
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
			for (int i=TYPE_COUNT; i<ROW_COUNT; i++) {
				int typeNumber = i%TYPE_COUNT;
				for (int j=1; j<columnCount; j++) {
					value = getValueAt(typeNumber, j);
					if (value != null)
						setValueAt(value, i, j);
				}
			}
		}

		public void cascadeRow(int row, int col) {
			String value = (String)getValueAt(row, col);
			int typeNumber = row%TYPE_COUNT;
			for (int i=typeNumber; i<ROW_COUNT; i+=TYPE_COUNT) {
				setValueAt((Object) value, i, col);
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
				String type = ""; //$NON-NLS-1$
				int level = -1;
				int typeNumber = -1;
				for (int i=0; i<ROW_COUNT; i++) {
					level = new Double(Math.floor(i/TYPE_COUNT)).intValue();
					type = getType(i);						
					styleProp.setProperty( type+level+"indent", (String)data[i][1] ); //$NON-NLS-1$
					styleProp.setProperty( type+level+"top", (String)data[i][2] );					 //$NON-NLS-1$
					styleProp.setProperty( type+level+"font", (String)data[i][3] ); //$NON-NLS-1$
					styleProp.setProperty( type+level+"size", (String)data[i][4] ); //$NON-NLS-1$
					styleProp.setProperty( type+level+"style", (String)data[i][5] ); //$NON-NLS-1$
	
					String backcolor = (String)data[i][6];
					if (backcolor == null) {
						backcolor = "-1"; //$NON-NLS-1$
					}
	
					String color = (String)data[i][7];
					if (color == null) {
						color = "-1"; //$NON-NLS-1$
					}					
					styleProp.setProperty( type+level+"back",  backcolor); //$NON-NLS-1$
					styleProp.setProperty( type+level+"color", color );			 //$NON-NLS-1$
	
					// MENU SETTINGS
					Color colour = lblMenuFontColor.getBackground();
					styleProp.setProperty( "menutextcolor", String.valueOf(colour.getRGB()) );	 //$NON-NLS-1$
	
					Color back = lblMenuBackgroundColor.getBackground();
					styleProp.setProperty( "menubackcolor", String.valueOf(back.getRGB()) ); //$NON-NLS-1$
	
					Color border = lblMenuBorderColor.getBackground();
					styleProp.setProperty( "menubordercolor", String.valueOf(border.getRGB()) ); //$NON-NLS-1$
	
					Color divider = lblDividerColor.getBackground();
					styleProp.setProperty( "dividercolor", String.valueOf(divider.getRGB()) ); //$NON-NLS-1$
	
					styleProp.setProperty("menufontfamily", (String)cbMenuFontFamily.getSelectedItem()); //$NON-NLS-1$
					styleProp.setProperty("menufontsize", (String)cbMenuFontSize.getSelectedItem()); //$NON-NLS-1$
					styleProp.setProperty("menufontstyle", (String)cbMenuFontStyle.getSelectedItem()); //$NON-NLS-1$
				}
				styleProp.store(new FileOutputStream(file), LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIHTMLFormatDialog.outlineFormatData")); //$NON-NLS-1$
	
			} catch (Exception e) {
				e.printStackTrace();
				ProjectCompendium.APP.displayError(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIHTMLFormatDialog.errorSavingFormat")+":\n\n"+e.getMessage()); //$NON-NLS-1$
			}
		}
	
		public void storeDataAsNew(String sName) {
	
			String sUniqueID = ProjectCompendium.APP.getModel().getUniqueID();
			try{
				Properties newProp = new Properties();
	
				int level = -1;
				String type = ""; //$NON-NLS-1$	
				for (int i=0; i<ROW_COUNT; i++) {
					level = new Double(Math.floor(i/TYPE_COUNT)).intValue();
					type = getType(i);	
					
					System.out.println("level="+level);				 //$NON-NLS-1$
					System.out.println("type="+type);				 //$NON-NLS-1$
					System.out.println("(String)data[i][1]="+(String)data[i][1]);
					newProp.setProperty( type+level+"indent", (String)data[i][1] ); //$NON-NLS-1$
					newProp.setProperty( type+level+"top", (String)data[i][2] );					 //$NON-NLS-1$
					newProp.setProperty( type+level+"font", (String)data[i][3] ); //$NON-NLS-1$
					newProp.setProperty( type+level+"size", (String)data[i][4] ); //$NON-NLS-1$
					newProp.setProperty( type+level+"style", (String)data[i][5] ); //$NON-NLS-1$
	
					String backcolor = (String)data[i][6];
					if (backcolor == null) {
						backcolor = "-1"; //$NON-NLS-1$
					}
	
					String color = (String)data[i][7];
					if (color == null) {
						color = "-1"; //$NON-NLS-1$
					}
	
					newProp.setProperty( type+level+"back",  backcolor); //$NON-NLS-1$
					newProp.setProperty( type+level+"color", color );				 //$NON-NLS-1$
				}
	
				newProp.setProperty( "name",  sName); //$NON-NLS-1$
				newProp.setProperty( "id", sUniqueID );				 //$NON-NLS-1$
				newProp.setProperty( "status", "active" );	 //$NON-NLS-1$ //$NON-NLS-2$
	
				// MENU SETTINGS
				Color colour = lblMenuFontColor.getBackground();
				newProp.setProperty( "menutextcolor", String.valueOf(colour.getRGB()) ); //$NON-NLS-1$
	
				Color back = lblMenuBackgroundColor.getBackground();
				newProp.setProperty( "menubackcolor", String.valueOf(back.getRGB()) ); //$NON-NLS-1$
	
				Color border = lblMenuBorderColor.getBackground();
				newProp.setProperty( "menubordercolor", String.valueOf(border.getRGB()) ); //$NON-NLS-1$
	
				Color divider = lblDividerColor.getBackground();
				newProp.setProperty( "dividercolor", String.valueOf(divider.getRGB()) ); //$NON-NLS-1$
	
				newProp.setProperty("menufontfamily", (String)cbMenuFontFamily.getSelectedItem()); //$NON-NLS-1$
				newProp.setProperty("menufontsize", (String)cbMenuFontSize.getSelectedItem()); //$NON-NLS-1$
				newProp.setProperty("menufontstyle", (String)cbMenuFontStyle.getSelectedItem()); //$NON-NLS-1$
	
				newProp.store(new FileOutputStream(DEFAULT_FILE_PATH+ProjectCompendium.sFS+sUniqueID+".properties"), LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIHTMLFormatDialog.outlineFormatData")); //$NON-NLS-1$ //$NON-NLS-2$
			} catch (Exception e) {
				e.printStackTrace();
				ProjectCompendium.APP.displayError(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIHTMLFormatDialog.errorSavingFormat")+e.getMessage());				 //$NON-NLS-1$
			}
		}		
	
		/**
		 * Return the data type for the given row.
		 * @param row the row to find the data type for.
		 * @return the data type for the given row.
		 */
		private String getType(int row) {
			String sType = ""; //$NON-NLS-1$
			int typeNumber = row%TYPE_COUNT;
			switch (typeNumber) {
				case 0: sType = "level";  break; //$NON-NLS-1$							
				case 1: sType = "detail"; break; //$NON-NLS-1$
				case 2: sType = "detaildate"; break; //$NON-NLS-1$
				case 3: sType = "reference"; break; //$NON-NLS-1$
				case 4: sType = "author"; break; //$NON-NLS-1$
				case 5: sType = "codes"; break; //$NON-NLS-1$
				case 6: sType = "views"; break; //$NON-NLS-1$
			}
			return sType;
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
					int level = new Double(Math.floor(row/TYPE_COUNT)).intValue();
					if (level%2 != 0) {
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
					} else {
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
            	text += " "; //$NON-NLS-1$
      		}

			setBorder((cellHasFocus) ? UIManager.getBorder("List.focusCellHighlightBorder") : noFocusBorder); //$NON-NLS-1$
			setText(text);
			return this;
		}
	}
}