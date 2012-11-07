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
import javax.swing.event.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.text.*;

import com.compendium.core.ICoreConstants;
import com.compendium.core.datamodel.*;
import com.compendium.core.datamodel.services.*;
import com.compendium.core.CoreUtilities;

import com.compendium.LanguageProperties;
import com.compendium.ProjectCompendium;
import com.compendium.io.html.*;
import com.compendium.ui.plaf.*;
import com.compendium.ui.*;

/**
 * This class processes a view into HTML, and uses image maps for map views.
 *
 * @author ? / Michelle Bachler
 */
public class UIExportViewDialog extends UIDialog implements IUIConstants, ActionListener, ItemListener {

	/** The file holding the saved export properties.*/
	public static final String	EXPORT_OPTIONS_FILE_NAME = "System"+ProjectCompendium.sFS+"resources"+ProjectCompendium.sFS+"ExportOptions.properties"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

	/** The default export directory.*/
	public static String		exportDirectory = ProjectCompendium.sHOMEPATH+ProjectCompendium.sFS+"Exports"; //$NON-NLS-1$

	/** Loaded export option properties.*/
	private Properties		optionsProperties = null;

	/** The list of views to export.*/
	private Vector			oViews  			= new Vector(51);

	/** Holds information for the depth check.*/
	private Hashtable		htCheckDepth 		= new Hashtable(51);

	/** Holds child data when calculating export views data.*/
	private Hashtable		htChildrenAdded 	= new Hashtable(51);

	/** The parent view for this dialog.*/
	private JFrame 			oParent 			= null;

	/** The pane for the dialog's contents.*/
	private Container 		oContentPane 		= null;

	/** The class that will create the HTML ans other expot files.*/
	private HTMLViews 		htmlViews 			= null;

	/** The current view frame.*/
	private UIViewFrame		currentFrame 		= null;

	/** The current view to export.*/
	private View 			currentView 		= null;

	/** The button to start the export.*/
	private UIButton 		pbExport 			= null;

	/** The button to close the dialog.*/
	private UIButton 		pbClose 			= null;

	/** The button to open the help.*/
	private UIButton 		pbHelp	 			= null;

	/** The button to open the views dialog.*/
	private UIButton		pbViews 			= null;

	/** The depth to export to.*/
	private int				depth 				= 0;

	/** Has the user cancelled the export.*/
	private boolean 		exportCancelled 	= false;

	/** Holds whether to include local reference file in the export.*/
	private boolean			bIncludeReferences	= false;

	/** Holds whether to export to a zip file.*/
	private boolean 		bToZip 				= false;

	/** Indicates whether to include a HTML title.*/
	private boolean			bContentsTitle 		= false;

	/** Indicates whether to export selected views only.*/
	private boolean			bSelectedViewsOnly 	= false;

	/** Indicates whether to export views selected from the view dialog.*/
	private boolean			bOtherViews 		= false;

	/** Indicates whether to sort the side menu item alphabetically or not.*/
	private boolean			bSortMenu			= false;

	/** Indicates whether to open the export file after completion (only if not zipped). */
	private boolean			bOpenAfter			= false;
	
	/** open external refs and images in new window.*/
	private boolean 		bOpenNew			= true;
	
	/** Holds whether to exclude detial popups for node with only anchor id info in them.*/
	private boolean			bNoDetailPopup		= false; 

	/** Holds whether to exclude detial popups for all nodes.*/
	private boolean			bNoDetailPopupAtAll	= false; 

	/** Store if the user wants heading on maps.*/
	private boolean 		bAddTitle 			= false;	

	/** Used to enter the label the user want as the title for the main html file.*/
	private JTextField		titleField 			= null;

	/** The label for the title field.*/
	private JLabel			titleLabel 			= null;

	/** Select to export to a zip file.*/
	private JCheckBox       cbToZip				= null;

	/** Select to export local reference files.*/
	private JCheckBox       cbWithRefs			= null;

	/** The title for the main HTML export file.*/
	private JCheckBox 		cbContentsTitle 	= null;

	/** Lets the user indicate whether to sort the Menu list alphabetically or not.*/
	private JCheckBox		cbSortMenu 			= null;

	/** Lets the user specify if they want the title on a map.*/
	private JCheckBox		cbMapTitle			= null;

	/** Lets the user specify if they want node links to be opened in a new window or not.*/
	private JCheckBox		cbOpenNew			= null;
	
	/** Whether to exclude detail popups when they only contain the anchor id*/
	private JCheckBox 		cbNoDetailPopup		= null;

	/** Whether to exclude detail popups*/
	private JCheckBox 		cbNoDetailPopupAtAll= null;

	/** Lets the user indicate whether to open the export file after completion (only if not zipped).*/
	private JCheckBox		cbOpenAfter			= null;

	/** Export views to thier full depth.*/
	private JRadioButton	fullDepth 			= null;

	/** Export views to the current depth only.*/
	private	JRadioButton	currentDepth 		= null;

	/** Export view to one depth down only.*/
	private	JRadioButton	oneDepth 			= null;

	/** Select to export all node in the current view.*/
	private JRadioButton	allNodes 			= null;

	/** Select to export only the selected views in the current view.*/
	private	JRadioButton	selectedViews 		= null;

	/** Select to export views selected from the views list to export.*/
	private	JRadioButton	otherViews 			= null;

	/** The font to use for the interface elements.*/
	private Font 			font 				= null;

	/** The file name to export to.*/
	private String			fileName 			= ""; //$NON-NLS-1$

	/** The model for the currently open database.*/
	private IModel 			model 				= null;

	/** The session for the current user in the current model.*/
	private PCSession 		session 			= null;

	/** The IViewService to use to access the database.*/
	private IViewService 	vs 					= null;

	/** The view dialog to select view to export.*/
	private UIExportMultipleViewDialog viewsDialog = null;

	/** The text area to list the views selected for export.*/
	private JTextArea 		oTextArea  	= null;
		

	/**
	 * Constructor.
	 *
	 * @param JFrame parent, the parent of this dialog
	 * @param boolean views, are we exporting multiple views of not (just current)
	 */
  	public UIExportViewDialog(JFrame parent, UIViewFrame frame) {

		super(parent, true);

      	this.setTitle(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIExportViewDialog.webMapExportTitle")); //$NON-NLS-1$
		this.currentFrame = frame;
		this.currentView = frame.getView();

     	oParent = parent;
		font = new Font("Dialog", Font.PLAIN, 12); //$NON-NLS-1$

      	oContentPane = getContentPane();
      	oContentPane.setLayout(new BorderLayout());

		oContentPane.add(createContentPanel(), BorderLayout.CENTER);
		oContentPane.add(createButtonPanel(), BorderLayout.SOUTH);

		loadProperties();
		applyLoadedProperties();

		pack();
		setResizable(false);
	}

	/**
	 * Create the button panel.
	 */
	private JPanel createButtonPanel() {

		UIButtonPanel oButtonPanel = new UIButtonPanel();

		pbExport = new UIButton(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIExportViewDialog.exportButton")); //$NON-NLS-1$
		pbExport.setMnemonic(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIExportViewDialog.exportButtonMnemonic").charAt(0));
		pbExport.addActionListener(this);
		getRootPane().setDefaultButton(pbExport);
		oButtonPanel.addButton(pbExport);

		pbClose = new UIButton(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIExportViewDialog.cancelButton")); //$NON-NLS-1$
		pbClose.setMnemonic(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIExportViewDialog.cancelButtonMnemonic").charAt(0));
		pbClose.addActionListener(this);
		oButtonPanel.addButton(pbClose);

		pbHelp = new UIButton(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIExportViewDialog.helpButton")); //$NON-NLS-1$
		pbHelp.setMnemonic(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIExportViewDialog.helpButtonMnemonic").charAt(0));
		ProjectCompendium.APP.mainHB.enableHelpOnButton(pbHelp, "io.export_html_views", ProjectCompendium.APP.mainHS); //$NON-NLS-1$
		oButtonPanel.addHelpButton(pbHelp);

		return oButtonPanel;
	}

	/**
	 * Draws the dialogs contents.
	 */
	/*private JPanel createContentPanel() {

		JPanel contentPanel = new JPanel();
		contentPanel.setBorder(new EmptyBorder(10,10,10,10));

		GridBagLayout gb = new GridBagLayout();
		GridBagConstraints gc = new GridBagConstraints();
		contentPanel.setLayout(gb);

		gc.anchor = GridBagConstraints.WEST;

		int y=0;
		Insets old = gc.insets;

		allNodes = new JRadioButton("Export all nodes on current map");
		allNodes.setSelected(false);
		allNodes.addItemListener(this);
		allNodes.setFont(font);
		gc.gridy = y;
		gc.gridwidth=2;
		y++;
		gb.setConstraints(allNodes, gc);
		contentPanel.add(allNodes);

		selectedViews = new JRadioButton("Export selected views on current view only");
		selectedViews.setSelected(false);
		selectedViews.addItemListener(this);
		selectedViews.setFont(font);
		gc.gridy = y;
		gc.gridwidth=2;
		y++;
		gb.setConstraints(selectedViews, gc);
		contentPanel.add(selectedViews);

		otherViews = new JRadioButton("Export other views instead of current: ");
		otherViews.setSelected(false);
		otherViews.addItemListener(this);
		otherViews.setFont(font);
		gc.gridy = y;
		gc.gridwidth=1;
		gb.setConstraints(otherViews, gc);
		contentPanel.add(otherViews);

		pbViews = new UIButton("Select Other Views to Export");
		pbViews.setEnabled(false);
		pbViews.addActionListener(this);
		pbViews.setFont(font);
		gc.gridy = y;
		gc.gridwidth=1;
		y++;
		gb.setConstraints(pbViews, gc);
		contentPanel.add(pbViews);

		ButtonGroup group1 = new ButtonGroup();
		group1.add(allNodes);
		group1.add(selectedViews);
		group1.add(otherViews);

		JSeparator sep = new JSeparator();
		gc.gridy = y;
		gc.gridwidth=2;
		gc.insets = new Insets(7,0,0,0);
		y++;
		gc.fill = GridBagConstraints.HORIZONTAL;
		gb.setConstraints(sep, gc);
		contentPanel.add(sep);
		gc.fill = GridBagConstraints.NONE;

		gc.insets = old;

		currentDepth = new JRadioButton("Export immediate depth only");
		currentDepth.setSelected(true);
		currentDepth.addItemListener(this);
		currentDepth.setFont(font);
		gc.gridy = y;
		gc.gridwidth=1;
		gb.setConstraints(currentDepth, gc);
		contentPanel.add(currentDepth);

		oneDepth = new JRadioButton("Export nodes to one depth level");
		oneDepth.setSelected(true);
		oneDepth.addItemListener(this);
		oneDepth.setFont(font);
		gc.gridy = y;
		gc.gridwidth=1;
		y++;
		gb.setConstraints(oneDepth, gc);
		contentPanel.add(oneDepth);

		fullDepth = new JRadioButton("Export nodes to full depth");
		fullDepth.setSelected(false);
		fullDepth.addItemListener(this);
		fullDepth.setFont(font);
		gc.gridwidth=2;
		gc.gridy = y;
		y++;
		gb.setConstraints(fullDepth, gc);
		contentPanel.add(fullDepth);

		ButtonGroup rgGroup = new ButtonGroup();
		rgGroup.add(currentDepth);
		rgGroup.add(oneDepth);
		rgGroup.add(fullDepth);

		sep = new JSeparator();
		gc.gridy = y;
		gc.gridwidth=2;
		gc.insets = new Insets(0,0,7,0);
		y++;
		gc.fill = GridBagConstraints.HORIZONTAL;
		gb.setConstraints(sep, gc);
		contentPanel.add(sep);
		gc.fill = GridBagConstraints.NONE;

		gc.insets = old;

		cbContentsTitle = new JCheckBox("Set Table of Contents Title");
		cbContentsTitle.addItemListener(this);
		cbContentsTitle.setFont(font);
		gc.gridy = y;
		gc.gridwidth=2;
		y++;
		gb.setConstraints(cbContentsTitle, gc);
		contentPanel.add(cbContentsTitle);

		titleLabel = new JLabel("HTML title for the base web page: ");
		titleLabel.setFont(font);
		titleLabel.setEnabled(false);
		gc.gridy = y;
		gc.gridwidth=1;
		gb.setConstraints(titleLabel, gc);
		contentPanel.add(titleLabel);

		titleField = new JTextField("");
		titleField.setEditable(false);
		titleField.setColumns(20);
		titleField.setMargin(new Insets(2,2,2,2));
		titleField.setEnabled(true);
		gc.gridy = y;
		gc.gridwidth=1;
		y++;
		gb.setConstraints(titleField, gc);
		contentPanel.add(titleField);

		sep = new JSeparator();
		gc.gridy = y;
		gc.gridwidth=2;
		gc.insets = new Insets(3,0,7,0);
		y++;
		gc.fill = GridBagConstraints.HORIZONTAL;
		gb.setConstraints(sep, gc);
		contentPanel.add(sep);
		gc.fill = GridBagConstraints.NONE;

		gc.insets = old;

      	cbSortMenu = new JCheckBox("List Views Alphabetically on Menu?");
      	cbSortMenu.setSelected(false);
		cbSortMenu.addItemListener(this);
		cbSortMenu.setFont(font);
		gc.gridy = y;
		y++;
		gb.setConstraints(cbSortMenu, gc);
      	contentPanel.add(cbSortMenu);

      	cbWithRefs = new JCheckBox("Include referenced files?");
      	cbWithRefs.setSelected(false);
		cbWithRefs.addItemListener(this);
		cbWithRefs.setFont(font);
		gc.gridy = y;
		y++;
		gb.setConstraints(cbWithRefs, gc);
      	contentPanel.add(cbWithRefs);

      	cbToZip = new JCheckBox("Export to Zip Archive?");
      	cbToZip.setSelected(false);
		cbToZip.addItemListener(this);
		cbToZip.setFont(font);
		gc.gridy = y;
		y++;
		gb.setConstraints(cbToZip, gc);
      	contentPanel.add(cbToZip);

      	cbOpenAfter = new JCheckBox("Open Export after completion?");
      	cbOpenAfter.setSelected(false);
		cbOpenAfter.addItemListener(this);
		cbOpenAfter.setFont(font);
		gc.gridy = y;
		gb.setConstraints(cbOpenAfter, gc);
      	contentPanel.add(cbOpenAfter);

		return contentPanel;
	}*/

	/**
	 * Draw the first tabbed panel with the primary export options.
	 */
	private JPanel createContentPanel() {

		JPanel contentPanel = new JPanel();
		contentPanel.setBorder(new EmptyBorder(10,10,10,10));

		//STAGE ONE
		
		GridBagLayout gb1 = new GridBagLayout();
		GridBagConstraints gc1 = new GridBagConstraints();
		contentPanel.setLayout(gb1);
		int y=0;
		gc1.anchor = GridBagConstraints.WEST;

		JPanel innerpanel = new JPanel(gb1);
		//innerpanel.setBorder(new TitledBorder("Views to Export"));
		
		JLabel lbltitle1 = new JLabel(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIExportViewDialog.viewToExport")); //$NON-NLS-1$
		lbltitle1.setFont(font);
		lbltitle1.setForeground(Color.blue);
		gc1.gridy = y;
		gc1.gridwidth=1;
		y++;
		gb1.setConstraints(lbltitle1, gc1);
		innerpanel.add(lbltitle1);
		
		allNodes = new JRadioButton(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIExportViewDialog.currentView")); //$NON-NLS-1$
		allNodes.setSelected(false);
		allNodes.addItemListener(this);
		allNodes.setFont(font);
		gc1.gridy = y;
		gc1.gridx = 0;
		gc1.gridheight = 1;		
		gc1.gridwidth=2;
		y++;
		gb1.setConstraints(allNodes, gc1);
		innerpanel.add(allNodes);

		selectedViews = new JRadioButton(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIExportViewDialog.selectedViews")); //$NON-NLS-1$
		selectedViews.setSelected(false);
		selectedViews.addItemListener(this);
		selectedViews.setFont(font);
		gc1.gridy = y;
		gc1.gridx = 0;		
		gc1.gridheight = 1;
		gc1.gridwidth=2;
		y++;
		gb1.setConstraints(selectedViews, gc1);
		innerpanel.add(selectedViews);

		otherViews = new JRadioButton(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIExportViewDialog.otherViews")+": "); //$NON-NLS-1$
		otherViews.setSelected(false);
		otherViews.addItemListener(this);
		otherViews.setFont(font);
		gc1.gridy = y;
		gc1.gridx = 0;		
		gc1.gridheight = 1;		
		gc1.gridwidth=1;
		//y++;
		gb1.setConstraints(otherViews, gc1);
		innerpanel.add(otherViews);

		pbViews = new UIButton(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIExportViewDialog.chooseViews")); //$NON-NLS-1$
		pbViews.setEnabled(false);
		pbViews.addActionListener(this);
		pbViews.setFont(font);
		gc1.gridy = y;
		gc1.gridx = 1;
		gc1.gridwidth=1;
		gc1.gridheight = 1;
		y++;
		gb1.setConstraints(pbViews, gc1);
		innerpanel.add(pbViews);

		JPanel textpanel = new JPanel(new BorderLayout());
		textpanel.setBorder(new EmptyBorder(0,10,0,0));
		
		JLabel label = new JLabel(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIExportViewDialog.chosenViews")+":"); //$NON-NLS-1$
		label.setFont(font);
		label.setAlignmentX(SwingConstants.LEFT);
		textpanel.add(label, BorderLayout.NORTH);
					
		oTextArea = new JTextArea(""); //$NON-NLS-1$
		oTextArea.setEditable(false);
		JScrollPane scrollpane = new JScrollPane(oTextArea);
		scrollpane.setPreferredSize(new Dimension(220,120));
		textpanel.add(scrollpane, BorderLayout.CENTER);
		
		gc1.gridy = 0;
		gc1.gridx = 2;		
		gc1.gridwidth=1;
		gc1.gridheight = 4;
		gb1.setConstraints(textpanel, gc1);		
		innerpanel.add(textpanel);
		
		ButtonGroup group1 = new ButtonGroup();
		group1.add(allNodes);
		group1.add(selectedViews);
		group1.add(otherViews);

		//STAGE TWO		
		GridBagLayout gb2 = new GridBagLayout();
		GridBagConstraints gc2 = new GridBagConstraints();
		contentPanel.setLayout(gb2);
		y=0;
		gc2.anchor = GridBagConstraints.WEST;
		JPanel innerpanel2 = new JPanel(gb2);
		
		//innerpanel2.setBorder(new TitledBorder("Depth to Export Views at"));
		
		JSeparator sep2 = new JSeparator();
		gc2.gridy = y;
		gc2.gridwidth=2;
		gc2.insets = new Insets(5,0,2,0);
		y++;
		gc2.fill = GridBagConstraints.HORIZONTAL;
		gb2.setConstraints(sep2, gc2);
		innerpanel2.add(sep2);
		gc2.fill = GridBagConstraints.NONE;
		
		gc2.insets = new Insets(0,0,0,0);
		
		JLabel lbltitle2 = new JLabel(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIExportViewDialog.exportDepth")); //$NON-NLS-1$
		lbltitle2.setFont(font);
		lbltitle2.setForeground(Color.blue);
		gc2.gridy = y;
		gc2.gridwidth=2;
		y++;
		gb2.setConstraints(lbltitle2, gc2);
		innerpanel2.add(lbltitle2);
		
		currentDepth = new JRadioButton(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIExportViewDialog.nodesOnly")); //$NON-NLS-1$
		currentDepth.setSelected(true);
		currentDepth.addItemListener(this);
		currentDepth.setFont(font);
		gc2.gridy = y;
		gc2.gridwidth=1;
		gb2.setConstraints(currentDepth, gc2);
		innerpanel2.add(currentDepth);

		JLabel lbl = new JLabel(""); //$NON-NLS-1$
		lbl.setFont(font);
		gc2.gridy = y;
		gc2.gridwidth=1;
		y++;
		gb2.setConstraints(lbl, gc2);
		innerpanel2.add(lbl);
		
		oneDepth = new JRadioButton(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIExportViewDialog.oneLevel"));  //$NON-NLS-1$
		oneDepth.setSelected(true);
		oneDepth.addItemListener(this);
		oneDepth.setFont(font);
		gc2.gridy = y;
		gc2.gridwidth=1;
		gb2.setConstraints(oneDepth, gc2);
		innerpanel2.add(oneDepth);

		JLabel lbl1 = new JLabel(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIExportViewDialog.oneLevelTip")); //$NON-NLS-1$
		lbl1.setFont(font);
		gc2.gridy = y;
		gc2.gridwidth=1;
		y++;
		gb2.setConstraints(lbl1, gc2);
		innerpanel2.add(lbl1);
		
		fullDepth = new JRadioButton(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIExportViewDialog.fullDepth")); //$NON-NLS-1$
		fullDepth.setSelected(false);
		fullDepth.addItemListener(this);
		fullDepth.setFont(font);
		gc2.gridwidth=1;
		gc2.gridy = y;
		gb2.setConstraints(fullDepth, gc2);
		innerpanel2.add(fullDepth);

		JLabel lbl2 = new JLabel(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIExportViewDialog.fullDepthTip")); //$NON-NLS-1$
		lbl2.setFont(font);
		gc2.gridy = y;
		gc2.gridwidth=1;
		y++;
		gb2.setConstraints(lbl2, gc2);
		innerpanel2.add(lbl2);

		ButtonGroup rgGroup = new ButtonGroup();
		rgGroup.add(currentDepth);
		rgGroup.add(oneDepth);
		rgGroup.add(fullDepth);

		// MAIN PANEL
		GridBagLayout gb = new GridBagLayout();
		contentPanel.setLayout(gb);
		GridBagConstraints gc = new GridBagConstraints();
		gc.anchor = GridBagConstraints.WEST;
		y=0;
						
		gc.gridy = y;
		gc.gridwidth=2;
		y++;
		gb.setConstraints(innerpanel, gc);
		contentPanel.add(innerpanel);

		gc.gridy = y;
		gc.gridwidth=2;
		y++;
		gb.setConstraints(innerpanel2, gc);
		contentPanel.add(innerpanel2);
		
		JSeparator sep = new JSeparator();
		gc.gridy = y;
		gc.gridwidth=2;
		gc.insets = new Insets(5,0,2,0);
		y++;
		gc.fill = GridBagConstraints.HORIZONTAL;
		gb.setConstraints(sep, gc);
		contentPanel.add(sep);
		gc.fill = GridBagConstraints.NONE;

		cbContentsTitle = new JCheckBox(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIExportViewDialog.setTableContents")); //$NON-NLS-1$
		cbContentsTitle.addItemListener(this);
		cbContentsTitle.setFont(font);
		gc.gridy = y;
		gc.gridwidth=2;
		y++;
		gb.setConstraints(cbContentsTitle, gc);
		contentPanel.add(cbContentsTitle);
		
		titleLabel = new JLabel(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIExportViewDialog.hmlTitle")+": "); //$NON-NLS-1$
		titleLabel.setFont(font);
		titleLabel.setEnabled(false);
		gc.gridy = y;
		gc.gridwidth=1;
		gb.setConstraints(titleLabel, gc);
		contentPanel.add(titleLabel);

		titleField = new JTextField(""); //$NON-NLS-1$
		titleField.setEditable(false);
		titleField.setColumns(20);
		titleField.setMargin(new Insets(2,2,2,2));
		titleField.setEnabled(true);
		gc.gridy = y;
		gc.gridwidth=1;
		y++;
		gb.setConstraints(titleField, gc);
		contentPanel.add(titleField);

		sep = new JSeparator();
		gc.gridy = y;
		gc.gridwidth=2;
		gc.insets = new Insets(3,0,5,0);
		y++;
		gc.fill = GridBagConstraints.HORIZONTAL;
		gb.setConstraints(sep, gc);
		contentPanel.add(sep);
		
		gc.fill = GridBagConstraints.NONE;
		gc.insets = new Insets(0,0,0,0);

     	cbNoDetailPopup = new JCheckBox(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIExportViewDialog.excludesDetailsPopupsIf")); //$NON-NLS-1$
     	cbNoDetailPopup.setToolTipText(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIExportViewDialog.excludesDetailsPopupsIfTip")); //$NON-NLS-1$
     	cbNoDetailPopup.setSelected(false);
     	cbNoDetailPopup.addItemListener(this);
     	cbNoDetailPopup.setFont(font);
		gc.gridy = y;
		y++;
		gb.setConstraints(cbNoDetailPopup, gc);
      	contentPanel.add(cbNoDetailPopup);

     	cbNoDetailPopupAtAll = new JCheckBox(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIExportViewDialog.excludesDetailsPopupsAll")); //$NON-NLS-1$
     	cbNoDetailPopupAtAll.setToolTipText(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIExportViewDialog.excludesDetailsPopupsAllTip")); //$NON-NLS-1$
     	cbNoDetailPopupAtAll.setSelected(false);
     	cbNoDetailPopupAtAll.addItemListener(this);
     	cbNoDetailPopupAtAll.setFont(font);
		gc.gridy = y;
		y++;
		gb.setConstraints(cbNoDetailPopupAtAll, gc);
      	contentPanel.add(cbNoDetailPopupAtAll);

     	cbOpenNew = new JCheckBox(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIExportViewDialog.newWindow")); //$NON-NLS-1$
     	cbOpenNew.setToolTipText(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIExportViewDialog.newWindowTip")); //$NON-NLS-1$
     	cbOpenNew.setSelected(false);
     	cbOpenNew.addItemListener(this);
     	cbOpenNew.setFont(font);
		gc.gridy = y;
		y++;
		gb.setConstraints(cbOpenNew, gc);
      	contentPanel.add(cbOpenNew);

     	cbMapTitle = new JCheckBox(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIExportViewDialog.addTitles")); //$NON-NLS-1$
     	cbMapTitle.setToolTipText(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIExportViewDialog.addTitlesTip")); //$NON-NLS-1$
      	cbMapTitle.setSelected(false);
     	cbMapTitle.addItemListener(this);
     	cbMapTitle.setFont(font);
		gc.gridy = y;
		y++;
		gb.setConstraints(cbMapTitle, gc);
      	contentPanel.add(cbMapTitle);

      	cbSortMenu = new JCheckBox(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIExportViewDialog.alphabeticalList")); //$NON-NLS-1$
      	cbSortMenu.setSelected(false);
		cbSortMenu.addItemListener(this);
		cbSortMenu.setFont(font);
		gc.gridy = y;
		y++;
		gb.setConstraints(cbSortMenu, gc);
      	contentPanel.add(cbSortMenu);
		
      	cbWithRefs = new JCheckBox(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIExportViewDialog.includeReferences")); //$NON-NLS-1$
      	cbWithRefs.setSelected(false);
		cbWithRefs.addItemListener(this);
		cbWithRefs.setFont(font);
		gc.gridy = y;
		y++;
		gb.setConstraints(cbWithRefs, gc);
      	contentPanel.add(cbWithRefs);

      	cbToZip = new JCheckBox(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIExportViewDialog.exportToZip")); //$NON-NLS-1$
      	cbToZip.setSelected(false);
		cbToZip.addItemListener(this);
		cbToZip.setFont(font);
		gc.gridy = y;
		y++;
		gb.setConstraints(cbToZip, gc);
      	contentPanel.add(cbToZip);

      	cbOpenAfter = new JCheckBox(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIExportViewDialog.openAfter")); //$NON-NLS-1$
      	cbOpenAfter.setSelected(false);
		cbOpenAfter.addItemListener(this);
		cbOpenAfter.setFont(font);
		gc.gridy = y;
		gb.setConstraints(cbOpenAfter, gc);
      	contentPanel.add(cbOpenAfter);

		return contentPanel;
	}
	
	/**
	 * Open the views dialog.
	 */
	private void onViews() {

		if (viewsDialog == null) {
			viewsDialog = new UIExportMultipleViewDialog(this);
			viewsDialog.setVisible(true);
		}
		else {
			viewsDialog.setVisible(true);
		}
	}

	/**
	 * Invoked when a button push event happens.
	 * @param e, the associated ActionEvent object.
	 */
	public void actionPerformed(java.awt.event.ActionEvent e) {

		Object source = e.getSource();

		// Handle button events
		if (source instanceof JButton) {
  			if (source == pbExport) {
    			onExport();
    			saveProperties();
  			}
			else if (source == pbViews) {
				onViews();
			}
			else if (source == pbClose) {
    			onCancel();
  			}
		}
	}

	/**
	 * Note when an options state has changed.
	 * @param e, the associated ItemEvent object.
	 */
	public void itemStateChanged(java.awt.event.ItemEvent e) {

    	Object source = e.getItemSelectable();

		if (source == cbContentsTitle) {
			bContentsTitle = cbContentsTitle.isSelected();
			if (bContentsTitle) {
				if (titleField != null) {
					titleField.setEditable(true);
					titleLabel.setEnabled(true);
					titleField.repaint();
				}
			}
			else {
				if (titleField != null) {
					titleField.setText(""); //$NON-NLS-1$
					titleField.setEditable(false);
					titleLabel.setEnabled(false);
					titleField.repaint();
				}
			}
		}
		else if (source == cbWithRefs) {
			bIncludeReferences = cbWithRefs.isSelected();
		}
		else if (source == cbToZip) {
			bToZip = cbToZip.isSelected();
			if (bToZip) {
				cbOpenAfter.setSelected(false);
				cbOpenAfter.setEnabled(false);
				bOpenAfter = false;
			}
			else {
				cbOpenAfter.setEnabled(true);
			}
		}
		else if (source == cbOpenAfter) {
			bOpenAfter = cbOpenAfter.isSelected();
		}
		else if (source == fullDepth && fullDepth.isSelected()) {
			depth = 2;
		}
		else if (source == oneDepth && oneDepth.isSelected()) {
			depth = 1;
		}
		else if (source == currentDepth && currentDepth.isSelected()) {
			depth = 0;
		}
		else if (source == selectedViews && selectedViews.isSelected()) {
			bOtherViews = false;
			bSelectedViewsOnly = true;

			pbViews.setEnabled(false);
			updateViewsList();			
		}
		else if (source == allNodes && allNodes.isSelected()) {
			bOtherViews = false;
			bSelectedViewsOnly = false;

			pbViews.setEnabled(false);
			updateViewsList();			
		}
		else if (source == otherViews && otherViews.isSelected()) {
			bOtherViews = true;
			bSelectedViewsOnly = false;

			pbViews.setEnabled(true);
			updateViewsList();			
		}
		else if (source == cbSortMenu) {
			bSortMenu = cbSortMenu.isSelected();
		}
		else if (source == cbMapTitle) {
			bAddTitle = cbMapTitle.isSelected();
		}	
		else if (source == cbOpenNew) {
			bOpenNew = cbOpenNew.isSelected();
		}
		else if (source == cbNoDetailPopup) {
			bNoDetailPopup = cbNoDetailPopup.isSelected();
			if (bNoDetailPopup) {
				bNoDetailPopupAtAll = false;
				cbNoDetailPopupAtAll.setSelected(false);
			}
		} else if (source == cbNoDetailPopupAtAll) {			
			bNoDetailPopupAtAll = cbNoDetailPopupAtAll.isSelected();
			if (bNoDetailPopupAtAll) {
				bNoDetailPopup = false;
				cbNoDetailPopup.setSelected(false);
			}
		}		
  	}

	/**
	 * Update the list of view to export;
	 */
	public void updateViewsList() {
		String sViews = ""; //$NON-NLS-1$
		Vector views = checkSelectedViews();
		int count = views.size();
		for (int i = 0; i < count; i++) {
			View view = (View)views.elementAt(i);
			sViews += view.getLabel()+"\n"; //$NON-NLS-1$
		}
		oTextArea.setText(sViews);											
	}
	
	/** Return true if any views are selected, else false;*/
	private boolean hasSelectedViews() {

		Enumeration nodes = null;

		if (currentFrame instanceof UIMapViewFrame) {
			UIViewPane uiViewPane = ((UIMapViewFrame)currentFrame).getViewPane();
			nodes = uiViewPane.getSelectedNodes();
			for(Enumeration en = nodes; en.hasMoreElements();) {
				UINode uinode = (UINode)en.nextElement();
				if (uinode.getNode() instanceof View) {
					return true;
				}
			}
		}
		else {
			UIList uiList = ((UIListViewFrame)currentFrame).getUIList();
			nodes = uiList.getSelectedNodes();
			for(Enumeration en = nodes; en.hasMoreElements();) {
				NodePosition nodepos = (NodePosition)en.nextElement();
				if (nodepos.getNode() instanceof View) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Get the views to export depending on user options to display
	 */
	private Vector checkSelectedViews() {

		model = ProjectCompendium.APP.getModel();
		session = model.getSession();
		vs = model.getViewService();

		Vector selectedViews = new Vector();

		// IF MULTIPLE VIEWS
		if (otherViews.isSelected()) {
			if (viewsDialog != null) {
				JTable table = viewsDialog.getTable();
				int [] selection = table.getSelectedRows();
				for (int i = 0; i < selection.length; i++) {
					View view = (View)table.getModel().getValueAt(selection[i],0);
					selectedViews.addElement(view);
				}
			}
		}
		else if (bSelectedViewsOnly) {
			Enumeration nodes = null;
			Vector vtTemp = new Vector();
			if (currentFrame instanceof UIMapViewFrame) {
				UIViewPane uiViewPane = ((UIMapViewFrame)currentFrame).getViewPane();
				nodes = uiViewPane.getSelectedNodes();
				for(Enumeration en = nodes; en.hasMoreElements();) {
					UINode uinode = (UINode)en.nextElement();
					if (uinode.getNode() instanceof View) {
						vtTemp.addElement(uinode.getNodePosition());
					}
				}
			}
			else {
				UIList uiList = ((UIListViewFrame)currentFrame).getUIList();
				nodes = uiList.getSelectedNodes();
				for(Enumeration en = nodes; en.hasMoreElements();) {
					NodePosition nodepos = (NodePosition)en.nextElement();
					if (nodepos.getNode() instanceof View) {
						vtTemp.addElement(nodepos);
					}
				}
			}
			
			//SORT VIEWS VECTOR BY DECENDING Y POSITION
			for (int i = 0; i < vtTemp.size(); i++) {
				int yPosition = ((NodePosition)vtTemp.elementAt(i)).getYPos();
				for (int j = i+1; j < vtTemp.size(); j++) {
					int secondYPosition = ((NodePosition)vtTemp.elementAt(j)).getYPos();

					if (secondYPosition < yPosition) {
						NodePosition np = (NodePosition)vtTemp.elementAt(i);
						vtTemp.setElementAt(vtTemp.elementAt(j), i);
						vtTemp.setElementAt(np, j);
						yPosition = ((NodePosition)vtTemp.elementAt(i)).getYPos();
					}
				}
			}

			for(int j=0; j < vtTemp.size(); j++) {
				NodePosition nodePos = (NodePosition)vtTemp.elementAt(j);
				View innerview = (View)nodePos.getNode();
				selectedViews.addElement(innerview);
			}			
		}
		else {
			selectedViews.addElement(currentView);
		}
		
		return selectedViews;
	}	
	
	/**
	 * Process an export request.
	 */
	public void onExport() {

		if (otherViews.isSelected()) {
			if(viewsDialog == null || (viewsDialog.getTable().getSelectedRows()).length <= 0) {
				ProjectCompendium.APP.displayMessage(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIExportViewDialog.selectView"), LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIExportViewDialog.exportWebMaps")); //$NON-NLS-1$ //$NON-NLS-2$
				return;
			}
		}

		boolean toZip = cbToZip.isSelected();
		if (toZip) {
			UIFileFilter filter = new UIFileFilter(new String[] {"zip"}, "ZIP Files"); //$NON-NLS-1$ //$NON-NLS-2$

			UIFileChooser fileDialog = new UIFileChooser();
			fileDialog.setDialogTitle(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIExportViewDialog.enterFileName")); //$NON-NLS-1$
			fileDialog.setFileFilter(filter);
			fileDialog.setApproveButtonText(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIExportViewDialog.saveButton")); //$NON-NLS-1$
			fileDialog.setRequiredExtension(".zip"); //$NON-NLS-1$

		    // FIX FOR MAC - NEEDS '/' ON END TO DENOTE A FOLDER
		    File file = new File(exportDirectory+ProjectCompendium.sFS);
		    if (file.exists()) {
				fileDialog.setCurrentDirectory(file);
			}

			int retval = fileDialog.showSaveDialog(ProjectCompendium.APP);
			if (retval == JFileChooser.APPROVE_OPTION) {
	        	if ((fileDialog.getSelectedFile()) != null) {

	            	fileName = fileDialog.getSelectedFile().getName();
					File fileDir = fileDialog.getCurrentDirectory();
					exportDirectory = fileDir.getPath();

					if (fileName != null) {
						if ( !fileName.toLowerCase().endsWith(".zip") ) { //$NON-NLS-1$
							fileName = fileName+".zip"; //$NON-NLS-1$
						}
					}
				}
			}
		}
		else {
			UIFileFilter filter = new UIFileFilter(new String[] {"html"}, "HTML Files"); //$NON-NLS-1$ //$NON-NLS-2$

			UIFileChooser fileDialog = new UIFileChooser();
			fileDialog.setDialogTitle(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIExportViewDialog.enterFileName")); //$NON-NLS-1$
			fileDialog.setFileFilter(filter);
			fileDialog.setApproveButtonText(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIExportViewDialog.saveButton")); //$NON-NLS-1$
			fileDialog.setRequiredExtension(".html"); //$NON-NLS-1$

		    // FIX FOR MAC - NEEDS '/' ON END TO DENOTE A FOLDER
		    File file = new File(exportDirectory+ProjectCompendium.sFS);
		    if (file.exists()) {
				fileDialog.setCurrentDirectory(file);
			}

			int retval = fileDialog.showSaveDialog(ProjectCompendium.APP);
			if (retval == JFileChooser.APPROVE_OPTION) {
	        	if ((fileDialog.getSelectedFile()) != null) {

	            	fileName = fileDialog.getSelectedFile().getName();
					File fileDir = fileDialog.getCurrentDirectory();
					exportDirectory = fileDir.getPath();

					if (fileName != null) {
						if ( !fileName.toLowerCase().endsWith(".html") ) { //$NON-NLS-1$
							fileName = fileName+".html"; //$NON-NLS-1$
						}
					}
				}
			}
		}

		ProjectCompendium.APP.setWaitCursor();
		if (fileName != null && !fileName.equals("")) { //$NON-NLS-1$
			setVisible(false);

			String sUserTitle = ""; //$NON-NLS-1$
			if (cbContentsTitle.isSelected())
				sUserTitle = titleField.getText();

			final Vector selectedViews = getSelectedViews();
			if (selectedViews.size() == 0)
				return;

			final String fFileName = fileName;
			final String fsUserTitle = sUserTitle;
			Thread thread = new Thread("UIExportViewDialog.onExport") { //$NON-NLS-1$
				public void run() {
					htmlViews = new HTMLViews(exportDirectory, fFileName, fsUserTitle, bIncludeReferences, bToZip, bSortMenu, bAddTitle, bOpenNew, bNoDetailPopup, bNoDetailPopupAtAll);
					htmlViews.processViews(selectedViews, bOpenAfter);
					if (bOpenAfter) {
						ExecuteControl.launch(exportDirectory +ProjectCompendium.sFS+fFileName);
					}
					onCancel();
				}
			};
			thread.start();
		}
	}

	/**
	 * Get the views to export depending on user options.
	 * Vector, the list of view to export.
	 */
	private Vector getSelectedViews() {

		model = ProjectCompendium.APP.getModel();
		session = model.getSession();
		vs = model.getViewService();

		Vector selectedViews = new Vector();

		// IF MULTIPLE VIEWS
		if (otherViews.isSelected()) {

			JTable table = viewsDialog.getTable();
			int [] selection = table.getSelectedRows();
			for (int i = 0; i < selection.length; i++) {
				View view = (View)table.getModel().getValueAt(selection[i],0);
				selectedViews.addElement(view);
			}

			if (depth == 1) {
				for (int i = 0; i < selection.length; i++) {
					View view = (View)table.getModel().getValueAt(selection[i],0);
					htCheckDepth.put((Object)view.getId(), view);
					selectedViews = getChildViews(view, selectedViews, false);
				}
			}
			else if (depth == 2) {
				for (int i = 0; i < selection.length; i++) {
					View view = (View)table.getModel().getValueAt(selection[i],0);
					htCheckDepth.put((Object)view.getId(), view);
					selectedViews = getChildViews(view, selectedViews, true);
				}
			}
		}
		else if (bSelectedViewsOnly) {

			Enumeration nodes = null;

			Vector vtTemp = new Vector();

			if (currentFrame instanceof UIMapViewFrame) {
				UIViewPane uiViewPane = ((UIMapViewFrame)currentFrame).getViewPane();
				nodes = uiViewPane.getSelectedNodes();
				for(Enumeration en = nodes; en.hasMoreElements();) {
					UINode uinode = (UINode)en.nextElement();
					if (uinode.getNode() instanceof View) {
						vtTemp.addElement(uinode.getNodePosition());
					}
				}
			}
			else {
				UIList uiList = ((UIListViewFrame)currentFrame).getUIList();
				nodes = uiList.getSelectedNodes();
				for(Enumeration en = nodes; en.hasMoreElements();) {
					NodePosition nodepos = (NodePosition)en.nextElement();
					if (nodepos.getNode() instanceof View) {
						vtTemp.addElement(nodepos);
					}
				}
			}

			//SORT VIEWS VECTOR BY DECENDING Y POSITION
			for (int i = 0; i < vtTemp.size(); i++) {
				int yPosition = ((NodePosition)vtTemp.elementAt(i)).getYPos();
				for (int j = i+1; j < vtTemp.size(); j++) {
					int secondYPosition = ((NodePosition)vtTemp.elementAt(j)).getYPos();

					if (secondYPosition < yPosition) {
						NodePosition np = (NodePosition)vtTemp.elementAt(i);
						vtTemp.setElementAt(vtTemp.elementAt(j), i);
						vtTemp.setElementAt(np, j);
						yPosition = ((NodePosition)vtTemp.elementAt(i)).getYPos();
					}
				}
			}

			//ADD THE CHILD VIEWS TO THE childViews VECTOR
			for(int j=0; j < vtTemp.size(); j++) {
				NodePosition nodePos = (NodePosition)vtTemp.elementAt(j);
				View innerview = (View)nodePos.getNode();
				selectedViews.addElement(innerview);
			}

			if (depth == 1) {
				for (int i = 0; i < vtTemp.size(); i++) {
					NodePosition nodePos = (NodePosition)vtTemp.elementAt(i);
					View view = (View)nodePos.getNode();
					htCheckDepth.put((Object)view.getId(), view);
					selectedViews = getChildViews(view, selectedViews, false);
				}
			}
			if (depth == 2) {
				for (int i = 0; i < vtTemp.size(); i++) {
					NodePosition nodePos = (NodePosition)vtTemp.elementAt(i);
					View view = (View)nodePos.getNode();
					htCheckDepth.put((Object)view.getId(), view);
					selectedViews = getChildViews(view, selectedViews, true);
				}
			}
		}
		else {
			selectedViews.addElement(currentView);

			if (depth == 1) {
				htCheckDepth.put((Object)currentView.getId(), currentView);
				selectedViews = getChildViews(currentView, selectedViews, false);
			}
			else if (depth == 2) {
				htCheckDepth.put((Object)currentView.getId(), currentView);
				selectedViews = getChildViews(currentView, selectedViews, true);
			}
		}

		return selectedViews;
	}

	/**
	 * Return the child views for the given view.
	 * @param view com.compendium.code.datamodel.View, the view to return the child nodes to.
	 * @param childViews, the child views found.
	 * @param fullDepth, whether to get child views to full depth.
	 */
	private Vector getChildViews(View view, Vector childViews, boolean fullDepth) {
		try {
			Vector vtTemp = vs.getNodePositions(session, view.getId());
			Vector nodePositionList = new Vector();

			//EXTRACT THE VIEWS AND ADD TO nodePositionList VECTOR
			for(Enumeration en = vtTemp.elements();en.hasMoreElements();) {
				NodePosition nodePos = (NodePosition)en.nextElement();
				NodeSummary node = nodePos.getNode();
				if (node instanceof View) {
					nodePositionList.addElement(nodePos);
				}
			}

			//SORT VIEWS VECTOR BY DECENDING Y POSITION
			for (int i = 0; i < nodePositionList.size(); i++) {
				int yPosition = ((NodePosition)nodePositionList.elementAt(i)).getYPos();
				for (int j = i+1; j < nodePositionList.size(); j++) {
					int secondYPosition = ((NodePosition)nodePositionList.elementAt(j)).getYPos();

					if (secondYPosition < yPosition) {
						NodePosition np = (NodePosition)nodePositionList.elementAt(i);
						nodePositionList.setElementAt(nodePositionList.elementAt(j), i);
						nodePositionList.setElementAt(np, j);
						yPosition = ((NodePosition)nodePositionList.elementAt(i)).getYPos();
					}
				}
			}

			//ADD THE CHILD VIEWS TO THE childViews VECTOR
			for (int k = 0; k < nodePositionList.size(); k++) {
				NodePosition np = (NodePosition)nodePositionList.elementAt(k);
				View innerview = (View)np.getNode();

				if (!htCheckDepth.containsKey((Object)innerview.getId())) {
					htCheckDepth.put((Object)innerview.getId(), innerview);
					childViews.addElement(np.getNode());
				}
			}

			if (fullDepth) {
				//GET CHILD VIEWS CHILDREN
				for (int j = 0; j < nodePositionList.size(); j++) {
					NodePosition np = (NodePosition)nodePositionList.elementAt(j);
					View innerview = (View)np.getNode();

					if (!htChildrenAdded.containsKey((Object)innerview.getId())) {
						htChildrenAdded.put((Object)innerview.getId(), innerview);
						childViews = getChildViews(innerview, childViews, fullDepth);
					}
				}
			}
		}
		catch (Exception e) {
			ProjectCompendium.APP.displayError("Exception: (UIExportDialog.getChildViews) \n\n" + e.getMessage()); //$NON-NLS-1$
		}

		return childViews;
	}


	/**
	 * Set the current view to export.
	 * @param view com.compendium.core.datamodel.View, the current view.
	 */
	public void setCurrentView(View view) {
		currentView = view;
	}

	/**
	 * Get the currently active view on the desktop.
	 * @return com.compendium.core.datamodel.View, the current view.
	 */
	private View getCurrentActiveView() {
		UIViewFrame viewFrame = ProjectCompendium.APP.getCurrentFrame();
		return viewFrame.getView();
	}

	/**
	 * Returns current view.  If null, returns current active View.
	 * @return com.compendium.core.datamodel.View
	 */
	public View getCurrentView() {
	  	if(currentView != null){
	    	return currentView;
	  	}
		else {
	    	return getCurrentActiveView();
	  	}
	}

	/**
	 * Apply the loaded export properties to the interface elements.
	 */
	private void applyLoadedProperties() {

		cbContentsTitle.setSelected(bContentsTitle);

		if (depth == 2) {
			fullDepth.setSelected(true);
		}
		else if (depth == 1) {
			oneDepth.setSelected(true);
		}
		else {
			currentDepth.setSelected(true);
		}

		cbOpenAfter.setSelected(bOpenAfter);
		cbToZip.setSelected(bToZip);
		cbSortMenu.setSelected(bSortMenu);
		cbMapTitle.setSelected(bAddTitle);
		cbOpenNew.setSelected(bOpenNew);
		cbNoDetailPopup.setSelected(bNoDetailPopup);		
		cbNoDetailPopupAtAll.setSelected(bNoDetailPopupAtAll);
		cbWithRefs.setSelected(bIncludeReferences);

		if (!hasSelectedViews()) {
			bSelectedViewsOnly = false;
			selectedViews.setEnabled(false);
		} 
		selectedViews.setSelected(bSelectedViewsOnly);		
		otherViews.setSelected(bOtherViews);

		if (!bSelectedViewsOnly && !bOtherViews)
			allNodes.setSelected(true);		
	}

	/**
	 * Load the saved properties for exporting.
	 */
	private void loadProperties() {

		File optionsFile = new File(EXPORT_OPTIONS_FILE_NAME);
		optionsProperties = new Properties();
		if (optionsFile.exists()) {
			try {
				optionsProperties.load(new FileInputStream(EXPORT_OPTIONS_FILE_NAME));

				String value = optionsProperties.getProperty("includerefs"); //$NON-NLS-1$
				if (value != null) {
					if (value.toLowerCase().equals("yes")) { //$NON-NLS-1$
						bIncludeReferences = true;
					} else {
						bIncludeReferences = false;
					}
				}

				value = optionsProperties.getProperty("zip"); //$NON-NLS-1$
				if (value != null) {
					if (value.toLowerCase().equals("yes")) { //$NON-NLS-1$
						bToZip = true;
					} else {
						bToZip = false;
					}
				}

				value = optionsProperties.getProperty("depth"); //$NON-NLS-1$
				if (value != null) {
					if (value.equals("1")) { //$NON-NLS-1$
						depth = 1;
					} else if (value.equals("2")) { //$NON-NLS-1$
						depth = 2;
					} else {
						depth = 0;
					}
				}

				value = optionsProperties.getProperty("selectedviewsonly"); //$NON-NLS-1$
				if (value != null) {
					if (value.toLowerCase().equals("yes")) { //$NON-NLS-1$
						bSelectedViewsOnly = true;
					}
					else {
						bSelectedViewsOnly = false;
					}
				}

				value = optionsProperties.getProperty("otherviews"); //$NON-NLS-1$
				if (value != null) {
					if (value.toLowerCase().equals("yes")) { //$NON-NLS-1$
						bOtherViews = true;
					}
					else {
						bOtherViews = false;
					}
				}

				value = optionsProperties.getProperty("contentstitle"); //$NON-NLS-1$
				if (value != null) {
					if (value.toLowerCase().equals("yes")) { //$NON-NLS-1$
						bContentsTitle = true;
					} else {
						bContentsTitle = false;
					}
				}

				value = optionsProperties.getProperty("sortmenu"); //$NON-NLS-1$
				if (value != null) {
					if (value.toLowerCase().equals("yes")) { //$NON-NLS-1$
						bSortMenu = true;
					} else {
						bSortMenu = false;
					}
				}

				value = optionsProperties.getProperty("openafter"); //$NON-NLS-1$
				if (value != null) {
					if (value.toLowerCase().equals("yes")) { //$NON-NLS-1$
						bOpenAfter = true;
					} else {
						bOpenAfter = false;
					}
				}

				value = optionsProperties.getProperty("addmaptitles"); //$NON-NLS-1$
				if (value != null) {
					if (value.toLowerCase().equals("yes")) { //$NON-NLS-1$
						bAddTitle = true;
					} else {
						bAddTitle = false;
					}
				}

				value = optionsProperties.getProperty("openinnew"); //$NON-NLS-1$
				if (value != null) {
					if (value.toLowerCase().equals("yes")) { //$NON-NLS-1$
						bOpenNew = true;
					} else {
						bOpenNew = false;
					}
				}

				value = optionsProperties.getProperty("nodetailpopup"); //$NON-NLS-1$
				if (value != null) {
					if (value.toLowerCase().equals("yes")) { //$NON-NLS-1$
						bNoDetailPopup = true;
					} else {
						bNoDetailPopup = false;
					}
				}

				value = optionsProperties.getProperty("nodetailpopupatall"); //$NON-NLS-1$
				if (value != null) {
					if (value.toLowerCase().equals("yes")) { //$NON-NLS-1$
						bNoDetailPopupAtAll = true;
					} else {
						bNoDetailPopupAtAll = false;
					}
				}
				
			} catch (IOException e) {
				ProjectCompendium.APP.displayError("Error reading export options properties. Default values will be used"); //$NON-NLS-1$
			}
		}
	}
	
	/**
	 * Save Properties.
	 */
	private void saveProperties() {
		try {
			if (bIncludeReferences == true) {
				optionsProperties.put("includerefs", "yes"); //$NON-NLS-1$ //$NON-NLS-2$
			} else {
				optionsProperties.put("includerefs", "no"); //$NON-NLS-1$ //$NON-NLS-2$
			}

			if (bToZip == true) {
				optionsProperties.put("zip", "yes"); //$NON-NLS-1$ //$NON-NLS-2$
			} else {
				optionsProperties.put("zip", "no"); //$NON-NLS-1$ //$NON-NLS-2$
			}

			if (depth == 2) {
				optionsProperties.put("depth", "2"); //$NON-NLS-1$ //$NON-NLS-2$
			} else if (depth == 1) {
				optionsProperties.put("depth", "1"); //$NON-NLS-1$ //$NON-NLS-2$
			} else {
				optionsProperties.put("depth", "0"); //$NON-NLS-1$ //$NON-NLS-2$
			}

			if (bSelectedViewsOnly == true) {
				optionsProperties.put("selectedviewsonly", "yes"); //$NON-NLS-1$ //$NON-NLS-2$
			} else {
				optionsProperties.put("selectedviewsonly", "no"); //$NON-NLS-1$ //$NON-NLS-2$
			}

			if (bOtherViews == true) {
				optionsProperties.put("otherviews", "yes"); //$NON-NLS-1$ //$NON-NLS-2$
			} else {
				optionsProperties.put("otherviews", "no"); //$NON-NLS-1$ //$NON-NLS-2$
			}

			if (bContentsTitle == true) {
				optionsProperties.put("contentstitle", "yes"); //$NON-NLS-1$ //$NON-NLS-2$
			} else {
				optionsProperties.put("contentstitle", "no"); //$NON-NLS-1$ //$NON-NLS-2$
			}

			if (bSortMenu == true) {
				optionsProperties.put("sortmenu", "yes"); //$NON-NLS-1$ //$NON-NLS-2$
			} else {
				optionsProperties.put("sortmenu", "no"); //$NON-NLS-1$ //$NON-NLS-2$
			}

			if (bOpenAfter == true) {
				optionsProperties.put("openafter", "yes"); //$NON-NLS-1$ //$NON-NLS-2$
			} else {
				optionsProperties.put("openafter", "no"); //$NON-NLS-1$ //$NON-NLS-2$
			}

			if (bAddTitle == true) {
				optionsProperties.put("addmaptitles", "yes"); //$NON-NLS-1$ //$NON-NLS-2$
			} else {
				optionsProperties.put("addmaptitles", "no"); //$NON-NLS-1$ //$NON-NLS-2$
			}

			if (bOpenNew == true) {
				optionsProperties.put("openinnew", "yes"); //$NON-NLS-1$ //$NON-NLS-2$
			} else {
				optionsProperties.put("openinnew", "no"); //$NON-NLS-1$ //$NON-NLS-2$
			}
	
			if (bNoDetailPopup == true) {
				optionsProperties.put("nodetailpopup", "yes"); //$NON-NLS-1$ //$NON-NLS-2$
			} else {
				optionsProperties.put("nodetailpopup", "no"); //$NON-NLS-1$ //$NON-NLS-2$
			}

			if (bNoDetailPopupAtAll == true) {
				optionsProperties.put("nodetailpopupatall", "yes"); //$NON-NLS-1$ //$NON-NLS-2$
			} else {
				optionsProperties.put("nodetailpopupatall", "no"); //$NON-NLS-1$ //$NON-NLS-2$
			}

			optionsProperties.store(new FileOutputStream(EXPORT_OPTIONS_FILE_NAME), LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIExportViewDialog.exportOptions")); //$NON-NLS-1$
		}
		catch (IOException e) {
			ProjectCompendium.APP.displayError(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIExportViewDialog.ioError")); //$NON-NLS-1$
		}		
	}

	/**
	 * Handle the close action. Save settings and close the export dialog.
	 */
	public void onCancel() {

		if (viewsDialog != null)
			viewsDialog.dispose();

		setVisible(false);

		ProjectCompendium.APP.setDefaultCursor();

		dispose();
	}
}
