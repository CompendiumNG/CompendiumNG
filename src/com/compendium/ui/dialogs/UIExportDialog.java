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
import java.sql.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;
import javax.swing.table.*;

import com.compendium.core.datamodel.*;
import com.compendium.core.datamodel.services.*;
import com.compendium.core.ICoreConstants;
import com.compendium.core.CoreUtilities;

import com.compendium.*;
import com.compendium.io.html.*;

import com.compendium.ui.plaf.*;
import com.compendium.ui.*;
import com.compendium.ui.panels.*;

/**
 * UIExportDialog defines the export dialog, that allows
 * the user to export PC Map/List Views to a MS-Word format document
 *
 * @author	Mohammed Sajid Ali / Michelle Bachler
 */
public class UIExportDialog extends UIDialog implements ActionListener, ItemListener, IUIConstants {

	/** The name of the property file holding the suers export settings.*/
	public static final String	EXPORT_OPTIONS_FILE_NAME = "System"+ProjectCompendium.sFS+"resources"+ProjectCompendium.sFS+"ExportOptions.properties"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

	public static String sBaseAnchorPath = "System"+ProjectCompendium.sFS+"resources"+ProjectCompendium.sFS+"Images"+ProjectCompendium.sFS; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

	/** The default directory to export to.*/
	private static String		exportDirectory = ProjectCompendium.sHOMEPATH+ProjectCompendium.sFS+"Exports"; //$NON-NLS-1$

	/** The pane for the dialog's content to be placed in.*/
	private Container contentPane = null;

	/** The button to start the export.*/
	private UIButton			pbExport	= null;

	/** The button to close the dialog.*/
	private UIButton			pbClose		= null;

	/** The button to open the help.*/
	private UIButton			pbHelp 		= null;

	/** The button to open the HTML export formatting dialog.*/
	private UIButton			pbFormatOutput = null;

	/** The button to open the view dialog.*/
	private UIButton			pbViews 	= null;

	/** The button to browse for a image to use for anchors in the export.*/
	private UIButton			pbBrowse 	= null;

	/** Indicates whether to include the author details in the export.*/
	private JCheckBox 		includeNodeAuthor = null;

	/** Indicates whether to inlucde the images in the export.*/
	private JCheckBox 		includeImage = null;

	/** Indicates whether to inlucde the links in the export.*/
	private JCheckBox 		includeLinks = null;

	/** Indicates whether to inlcude a user assigned title for the main export file.*/
	private JCheckBox 		includeTitle = null;

	/** Indicates whether the inlcude a navigation bar with the export.*/
	private JCheckBox 		includeNavigationBar = null;

	/** Indicates whether to diapl the node detail page dates on export.*/
	private JCheckBox 		displayDetailDates = null;

	/** Indicates that the node detail page dates should not be displayed.*/
	private JCheckBox 		hideNodeNoDates = null;

	/** Indicates that each view should be exported in a separate HTML file.*/
	private JCheckBox 		displayInDifferentPages = null;

	/** Indicates whether to include node anchors.*/
	private JCheckBox 		includeNodeAnchor = null;

	/** Indicates whether to include anchors on node detail pages.*/
	private JCheckBox 		includeDetailAnchor = null;

	/** Indicates whether to export all files to a zip file.*/
	private JCheckBox       cbToZip				= null;

	/** Indicates whether to include external local reference files in the export.*/
	private JCheckBox       cbWithRefs			= null;

	/** Lets the user indicate whether to open the export file after completion (only if not zipped).*/
	private JCheckBox		cbOpenAfter			= null;

	/** Indicates whether to inlcude the heading tags in the export (good for accessibility, bad for Word).*/
	private JCheckBox 		optimizeForWord = null;

	/** Holds the user assigned title for the main export file.*/
	private JTextField		titlefield = null;

	/** Holds the name of the anchor image file to use.*/
	private JTextField		anchorImage = null;

	/** Holds choice boxes to enter the from date for filtering node detail pages.*/
	private UIDatePanel 	fromPanel = null;

	/** Holds choice boxes to enter the to date for filtering node detail pages.*/
	private UIDatePanel 	toPanel = null;

	/** Should parent view data be placed in line in the main text body?*/
	private JRadioButton 	inlineView = null;

	/** Indicates whether to include tags in the export.*/
	private JCheckBox       cbIncludeTags			= null;

	/** Indicates whether to include parent views in the export.*/
	private JCheckBox		cbIncludeViews			= null;

	/** Should node parent view data be inlcuded in the export?*/
	private JRadioButton 	noView = null;

	/** should parent view data be placed in separate files?*/
	private JRadioButton 	newView = null;

	/** Should node detail detail pages be included in the export?*/
	private JRadioButton 	noNodeDetail = null;

	/** Should node detail pages should be filtered in given dates?*/
	private JRadioButton 	includeNodeDetail = null;

	/** Should node detail pages dates be included in the export?*/
	private JRadioButton 	includeNodeDetailDate = null;

	/** Should images be used for anchors?*/
	private JRadioButton 	useAnchorImages = null;

	/** Should purple numbers be used for anchors.*/
	private JRadioButton 	useAnchorNumbers = null;

	/** Should the views being exported be exported to thier full depth?*/
	private JRadioButton	fullDepth = null;

	/** Should view being exported only export themselves and not thier child nodes?*/
	private	JRadioButton	currentDepth = null;

	/** Should views being export be export to a sinlge level of depth only?*/
	private	JRadioButton	oneDepth = null;

	/** Should all nodes in the current view be export?*/
	private JRadioButton	allNodes = null;

	/** Should only the selected views in the current view be exported.*/
	private	JRadioButton	selectedViews = null;

	/** Should only views selected through the views dialog be exported.*/
	private	JRadioButton	otherViews = null;

	/** The label for the title field.*/
	private JLabel			titleLabel = null;
	
	/** The text area to list the views selected for export.*/
	private JTextArea 		oTextArea  = null;

	// EXPORT SETTINGS
	/** Stores if node detail pages should be included in the export.*/
	private boolean			bIncludeNodeDetail 		= true;

	/** Stores if node detail pages should be filterd on certain dates.*/
	private boolean			bIncludeNodeDetailDate 	= false;

	/** Stores if node author information should be included in the export.*/
	private boolean			bIncludeNodeAuthor 		= false;

	/** Stores if link label information should be included in the export.*/
	private boolean			bIncludeLinks 			= false;

	/** Stores if node images should be included in the export.*/
	private boolean			bIncludeImage 			= true;

	/** Stores if node detail page dates should be diaplyed in the export.*/
	private boolean			bDisplayDetailDates 	= false;

	/** Stores if exported views should be exported to separate pages.*/
	private boolean			bDisplayInDifferentPages = true;

	/** No node detail dates should be included.*/
	private boolean			bHideNodeNoDates 		= false;

	/** Stores if the export should include a navigation bar.*/
	private boolean			bIncludeNavigationBar 	= true;

	/** Stores if the parent view information should be diaplyed in the main text body.*/
	private boolean			bInlineView 			= false;

	/** Stores if no parent view data should be included in the export.*/
	//private boolean			bNoView 				= false;

	/** Stores if parent views should be included in the export.*/
	private boolean			bIncludeViews 			= true;

	/** Stores if tags should be included in the export.*/
	private boolean			bIncludeTags 			= true;
	
	/** Stores if parent view data should be exported to separate pages.*/
	private boolean			bNewView 				= false;

	/** Stores if only the selected views should be exported.*/
	private boolean			bSelectedViewsOnly 		= false;

	/** Stores if views selected from the views dialog should be exported.*/
	private boolean			bOtherViews 			= false;

	/** Stores if node anchors should be inlucded in the export.*/
	private boolean			bIncludeNodeAnchors 	= false;

	/** Stores if node detail anchors should be inlcuded in the export.*/
	private boolean			bIncludeDetailAnchors	= false;

	/** Stores if purple numbers hsoul be used for the anchors.*/
	private boolean			bUseAnchorNumbers		= false;

	/** Stores if images should be used for the acnhors.*/
	private boolean			bUseAnchorImages		= true;

	/** Stores if the exported files should be exported to a zip file.*/
	private boolean 		bToZip 					= false;

	/** Stores if external local reference files should be included in the export.*/
	private boolean			bIncludeReferences 		= false;

	/** Stores if the export should include the heading tags or not (not means it is optimised for Word).*/
	private boolean			bOptimizeForWord		= false;

	/** Indicates whether to open the export file after completion (only if not zipped). */
	private boolean			bOpenAfter			= false;

	/** Used to hold the depth chosen to export views to.*/
	private int				depth = 0;

	/** Stores the to date for filtering node detail pages.*/
	private long			toDate = 0;

	/** Stores the from date for filtering node detail pages.*/
	private long			fromDate = 0;

	/** Used while processing nodes for export.*/
	private Vector			nodeLevelList = null;

	/** Used while processing nodes for export.*/
	private Hashtable		htNodesLevel = new Hashtable(51);

	/** Holds nodes being processed for export.*/
	private	Hashtable		htNodes = new Hashtable(51);

	/** Used wile processing nodes for export.*/
	private Hashtable		htNodesBelow = new Hashtable(51);

	/** Used while processing nodes for export.*/
	private Hashtable		htCheckDepth = new Hashtable(51);

	/** Used while processing nodes for export.*/
	private Hashtable		htChildrenAdded = new Hashtable(51);

	/** The level to start the export at.*/
	private int				nStartExportAtLevel 	= 0;

	/** Used while processing nodes for export.*/
	private int				nodeIndex 				= -1;

	/** The file name for the main export file.*/
	private String			fileName 		= ""; //$NON-NLS-1$

	/** The anchor image to use.*/
	private String	sAnchorImage	= sBaseAnchorPath+"anchor0.gif"; //$NON-NLS-1$

	/** the parent frame for this dialog.*/
	private JFrame			oParent	= null;

	/** Holds the anchor options.*/
	private JPanel			innerAnchorPanel = null;

	/** Holds the saved export options.*/
	private Properties		optionsProperties = null;

	/** The main pane for the dialog's contents.*/
	private Container		oContentPane = null;

	/** The file browser dialog instance to select the export file name.*/
	private	FileDialog		fdgExport = null;

	/** The class that will process the export and create the HTML files etc. for the export.*/
	private HTMLOutline		oHTMLExport = null;

	/** The current view being exported.*/
	private View			currentView = null;

	/** The frame of the current view being exported.*/
	private UIViewFrame		currentFrame = null;

	/** Used to order the nodes being exported.*/
	private IUIArrange		arrange = null;

	/** The model of the currently open database.*/
	private IModel 			model 	= null;

	/** The session for the current user in the current model*/
	private PCSession 		session = null;

	/** The IViewService instance to access the database.*/
	private IViewService 	vs = null;

	/** The font to use for labels.*/
	private Font 			font = null;

	/** The tabbedpane holding all the various option panels.*/
	private JTabbedPane		tabbedPane = null;

	/** The scrollpane holding the list of default anhor images.*/
	private JScrollPane 	imagescroll = null;

	/** The renderer used to render the list of default anchor imags.*/
	private AnchorImageCellRenderer anchorImageListRenderer = null;

	/** The list of default anchor images.*/
	private UINavList 		lstAnchorImages		= null;

	/** The dialog diaplying all views avilable to export.*/
	private UIExportMultipleViewDialog viewsDialog = null;

	/** The label which tells the user which format the export will use.*/
	private JLabel			lblFormatUsed = null;
		
	/** List of style names to be displayed in the choice box.*/
	private Vector 					vtStyles = new Vector();	

	/** Holds a list of existing styles.*/
	private JComboBox				oStyles	= null;
	
	/**
	 * Initializes and sets up the dialog.
	 * @param frame, the view frame being exported.
	 */
	public UIExportDialog(UIViewFrame frame) {		
		super(ProjectCompendium.APP, true);
		this.currentFrame = frame;
		this.currentView = frame.getView();
	}
	
	/**
	 * Initializes and sets up the dialog.
	 * @param parent, the parent frame for this dialog.
	 * @param frame, the view frame being exported.
	 */
	public UIExportDialog(JFrame parent, UIViewFrame frame) {

		super(parent, true);

		this.currentFrame = frame;
		this.currentView = frame.getView();
	  	this.setTitle(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIExportDialog.webOutlineExport")); //$NON-NLS-1$

		font = new Font("Dialog", Font.PLAIN, 12); //$NON-NLS-1$
	  	oParent = parent;

		JPanel mainPanel = new JPanel(new BorderLayout());

		oContentPane = getContentPane();
		oContentPane.setLayout(new BorderLayout());

		tabbedPane = new JTabbedPane();
		tabbedPane.setFont(new Font("Dialog", Font.BOLD, 12)); //$NON-NLS-1$

		JPanel contentPanel = createContentPanel();
		JPanel optionsPanel = createOptionsPanel();
		//JPanel detailPanel = createDetailPanel();
		JPanel tagPanel = createAnchorPanel();

		JPanel outer = new JPanel(new FlowLayout(FlowLayout.LEFT));
		outer.add(contentPanel);
		tabbedPane.add(outer, LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIExportDialog.NodeSelection")); //$NON-NLS-1$

		outer = new JPanel(new FlowLayout(FlowLayout.LEFT));
		outer.add(optionsPanel);
		tabbedPane.add(outer, LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIExportDialog.formatAndContents")); //$NON-NLS-1$

		//outer = new JPanel(new FlowLayout(FlowLayout.LEFT));
		//outer.add(detailPanel);
		//tabbedPane.add(outer, "Node Detail Pages");

		outer = new JPanel(new FlowLayout(FlowLayout.LEFT));
		outer.add(tagPanel);
		tabbedPane.add(outer, LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIExportDialog.NodeAnchors")); //$NON-NLS-1$

		mainPanel.add(tabbedPane, BorderLayout.CENTER);

		JPanel buttonpanel = createButtonPanel();

		oContentPane.add(mainPanel, BorderLayout.CENTER);
		oContentPane.add(buttonpanel, BorderLayout.SOUTH);

		loadProperties();
		applyLoadedProperties();

		pack();
		setResizable(false);
	}

	/**
	 * Draw the button panel for the bottom of the dialog.
	 */
	private JPanel createButtonPanel() {

		UIButtonPanel oButtonPanel = new UIButtonPanel();

		pbExport = new UIButton(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIExportDialog.exportButton")); //$NON-NLS-1$
		pbExport.setMnemonic(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIExportDialog.exportButtonMnemonic").charAt(0)); //$NON-NLS-1$
		pbExport.addActionListener(this);
		getRootPane().setDefaultButton(pbExport);
		oButtonPanel.addButton(pbExport);

		pbClose = new UIButton(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIExportDialog.cancelButton")); //$NON-NLS-1$
		pbClose.setMnemonic(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIExportDialog.cancelButtonMnemonic").charAt(0)); //$NON-NLS-1$
		pbClose.addActionListener(this);
		oButtonPanel.addButton(pbClose);

		pbHelp = new UIButton(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIExportDialog.helpButton")); //$NON-NLS-1$
		pbHelp.setMnemonic(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIExportDialog.helpButtonMnemonic").charAt(0)); //$NON-NLS-1$
		ProjectCompendium.APP.mainHB.enableHelpOnButton(pbHelp, "io.export_html_outline", ProjectCompendium.APP.mainHS); //$NON-NLS-1$
		oButtonPanel.addHelpButton(pbHelp);

		return oButtonPanel;
	}

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
		
		JLabel lbltitle1 = new JLabel(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIExportDialog.viewsToExport")); //$NON-NLS-1$
		lbltitle1.setFont(font);
		lbltitle1.setForeground(Color.blue);
		gc1.gridy = y;
		gc1.gridwidth=1;
		y++;
		gb1.setConstraints(lbltitle1, gc1);
		innerpanel.add(lbltitle1);
		
		allNodes = new JRadioButton(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIExportDialog.currentViewOnly")); //$NON-NLS-1$
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

		selectedViews = new JRadioButton(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIExportDialog.selectedViews")); //$NON-NLS-1$
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

		otherViews = new JRadioButton(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIExportDialog.otherViews")+":"); //$NON-NLS-1$ //$NON-NLS-2$
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

		pbViews = new UIButton(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIExportDialog.chooseViewsButton")); //$NON-NLS-1$
		pbViews.setMnemonic(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIExportDialog.chooseViewsButtonMnemonic").charAt(0)); //$NON-NLS-1$
		pbViews.setEnabled(false);
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
		
		JLabel label = new JLabel(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIExportDialog.chosenViews")+":"); //$NON-NLS-1$ //$NON-NLS-2$
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
		
		JLabel lbltitle2 = new JLabel(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIExportDialog.depth")); //$NON-NLS-1$
		lbltitle2.setFont(font);
		lbltitle2.setForeground(Color.blue);
		gc2.gridy = y;
		gc2.gridwidth=2;
		y++;
		gb2.setConstraints(lbltitle2, gc2);
		innerpanel2.add(lbltitle2);
		
		currentDepth = new JRadioButton(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIExportDialog.nodesOnly")); //$NON-NLS-1$
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
		
		oneDepth = new JRadioButton(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIExportDialog.oneLevel"));  //$NON-NLS-1$
		oneDepth.setSelected(true);
		oneDepth.addItemListener(this);
		oneDepth.setFont(font);
		gc2.gridy = y;
		gc2.gridwidth=1;
		gb2.setConstraints(oneDepth, gc2);
		innerpanel2.add(oneDepth);

		JLabel lbl1 = new JLabel(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIExportDialog.oneLevelTip")); //$NON-NLS-1$
		lbl1.setFont(font);
		gc2.gridy = y;
		gc2.gridwidth=1;
		y++;
		gb2.setConstraints(lbl1, gc2);
		innerpanel2.add(lbl1);
		
		fullDepth = new JRadioButton(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIExportDialog.fullDepth")); //$NON-NLS-1$
		fullDepth.setSelected(false);
		fullDepth.addItemListener(this);
		fullDepth.setFont(font);
		gc2.gridwidth=1;
		gc2.gridy = y;
		gb2.setConstraints(fullDepth, gc2);
		innerpanel2.add(fullDepth);

		JLabel lbl2 = new JLabel(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIExportDialog.fullDepthTip")); //$NON-NLS-1$
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

		displayInDifferentPages = new JCheckBox(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIExportDialog.separateHTMLFile")); //$NON-NLS-1$
		displayInDifferentPages.addItemListener(this);
		displayInDifferentPages.setFont(font);
		gc.gridy = y;
		gc.gridwidth=2;
		y++;
		gb.setConstraints(displayInDifferentPages, gc);
		contentPanel.add(displayInDifferentPages);

		titleLabel = new JLabel(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIExportDialog.htmlTitle")+": "); //$NON-NLS-1$ //$NON-NLS-2$
		titleLabel.setFont(font);
		titleLabel.setEnabled(false);
		gc.gridy = y;
		gc.gridwidth=1;
		gb.setConstraints(titleLabel, gc);
		contentPanel.add(titleLabel);

		titlefield = new JTextField(""); //$NON-NLS-1$
		titlefield.setEditable(false);
		titlefield.setColumns(20);
		titlefield.setMargin(new Insets(2,2,2,2));
		titlefield.setEnabled(true);
		gc.gridy = y;
		gc.gridwidth=1;
		y++;
		gb.setConstraints(titlefield, gc);
		contentPanel.add(titlefield);

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

      	cbWithRefs = new JCheckBox(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIExportDialog.oncludeReferencedFiles")); //$NON-NLS-1$
      	cbWithRefs.setSelected(false);
		cbWithRefs.addItemListener(this);
		cbWithRefs.setFont(font);
		gc.gridy = y;
		y++;
		gb.setConstraints(cbWithRefs, gc);
      	contentPanel.add(cbWithRefs);

      	cbToZip = new JCheckBox(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIExportDialog.exportTozip")); //$NON-NLS-1$
      	cbToZip.setSelected(false);
		cbToZip.addItemListener(this);
		cbToZip.setFont(font);
		gc.gridy = y;
		y++;
		gb.setConstraints(cbToZip, gc);
      	contentPanel.add(cbToZip);

      	cbOpenAfter = new JCheckBox(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIExportDialog.openAfterExport")); //$NON-NLS-1$
      	cbOpenAfter.setSelected(false);
		cbOpenAfter.addItemListener(this);
		cbOpenAfter.setFont(font);
		gc.gridy = y;
		gb.setConstraints(cbOpenAfter, gc);
      	contentPanel.add(cbOpenAfter);

		return contentPanel;
	}

	/**
	 *	Create a panel holding the node detail page export options.
	 */
	//private JPanel createDetailPanel() {

		/*JPanel detailPanel = new JPanel();
		detailPanel.setBorder(new EmptyBorder(10,10,10,10));
		detailPanel.setFont(new Font("Dialog", Font.PLAIN, 12));

		GridBagLayout gb = new GridBagLayout();
		GridBagConstraints gc = new GridBagConstraints();
		detailPanel.setLayout(gb);
		gc.anchor = GridBagConstraints.WEST;

		int y=0;

		// CREATE DATE PANEL FIRST FOR REFERENCE REASONS
		JPanel datePanel = createDatePanel();*/

		//JLabel label = new JLabel("Node Details");
		//label.setFont(new Font("Arial", Font.BOLD, 12));
		//gc.gridy = y;
		//y++;
		//gb.setConstraints(label, gc);
		//detailPanel.add(label);

		/*noNodeDetail = new JRadioButton("No node detail pages");
		noNodeDetail.addItemListener(this);
		noNodeDetail.setFont(font);
		gc.gridy = y;
		y++;
		gc.gridwidth=1;
		gb.setConstraints(noNodeDetail, gc);
		detailPanel.add(noNodeDetail);

		includeNodeDetail = new JRadioButton("Include all node detail pages");
		includeNodeDetail.addItemListener(this);
		includeNodeDetail.setFont(font);
		gc.gridy = y;
		y++;
		gc.gridwidth=1;
		gb.setConstraints(includeNodeDetail, gc);
		detailPanel.add(includeNodeDetail);

		includeNodeDetailDate = new JRadioButton("Include node detail pages for Dates: ");
		includeNodeDetailDate.addItemListener(this);
		includeNodeDetailDate.setFont(font);
		gc.gridy = y;
		y++;
		gc.gridwidth=2;
		gb.setConstraints(includeNodeDetailDate, gc);
		detailPanel.add(includeNodeDetailDate);

		ButtonGroup detailGroup = new ButtonGroup();
		detailGroup.add(noNodeDetail);
		detailGroup.add(includeNodeDetail);
		detailGroup.add(includeNodeDetailDate);

		// ADD DATE PANEL
		gc.gridy = y;
		y++;
		gc.gridwidth=2;
		gb.setConstraints(datePanel, gc);
		detailPanel.add(datePanel);

		JLabel other = new JLabel(" ");
		gc.gridy = y;
		y++;
		gb.setConstraints(other, gc);
		detailPanel.add(other);

		displayDetailDates = new JCheckBox("Display detail page dates");
		displayDetailDates.addItemListener(this);
		displayDetailDates.setSelected(false);
		displayDetailDates.setFont(font);
		gc.gridy = y;
		gc.gridwidth=1;
		gb.setConstraints(displayDetailDates, gc);
		detailPanel.add(displayDetailDates);*/

		/*
		hideNodeNoDates = new JCheckBox("Hide nodes outside of dates");
		gc.gridy = y;
		y++;
		gc.gridwidth=1;
		gb.setConstraints(hideNodeNoDates, gc);
		hideNodeNoDates.addItemListener(this);
		hideNodeNoDates.setSelected(false);
		detailPanel.add(hideNodeNoDates);
		*/

		//return detailPanel;
	//}

	/**
	 *	Create a panel holding the anchor export options (i.e. purple numbers stuff).
	 */
	private JPanel createAnchorPanel() {

		JPanel anchorPanel = new JPanel();
		anchorPanel.setLayout(new BorderLayout());
		anchorPanel.setBorder(new EmptyBorder(10,10,10,10));
		anchorPanel.setFont(new Font("Dialog", Font.PLAIN, 12)); //$NON-NLS-1$

		JPanel innerAnchorPanelTop = new JPanel();
		GridBagLayout gb = new GridBagLayout();
		GridBagConstraints gc = new GridBagConstraints();
		innerAnchorPanelTop.setLayout(gb);
		gc.anchor = GridBagConstraints.WEST;
		gc.insets = new Insets(5,5,5,5);

		int y=0;

		includeNodeAnchor = new JCheckBox(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIExportDialog.anchorsOnLabels")); //$NON-NLS-1$
		includeNodeAnchor.addItemListener(this);
		includeNodeAnchor.setFont(font);
		gc.gridy = y;
		gc.gridx = 0;
		gc.gridwidth=1;
		gb.setConstraints(includeNodeAnchor, gc);
		innerAnchorPanelTop.add(includeNodeAnchor);

		includeDetailAnchor = new JCheckBox(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIExportDialog.anchorsOnDetails")); //$NON-NLS-1$
		includeDetailAnchor.addItemListener(this);
		includeDetailAnchor.setFont(font);
		gc.gridy = y;
		gc.gridx = 1;
		y++;
		gc.gridwidth=1;
		gb.setConstraints(includeDetailAnchor, gc);
		innerAnchorPanelTop.add(includeDetailAnchor);

		useAnchorImages = new JRadioButton(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIExportDialog.imageAsAnchor")); //$NON-NLS-1$
		useAnchorImages.addItemListener(this);
		useAnchorImages.setFont(font);
		gc.gridy = y;
		gc.gridx = 0;
		gc.gridwidth=1;
		gb.setConstraints(useAnchorImages, gc);
		innerAnchorPanelTop.add(useAnchorImages);

		useAnchorNumbers = new JRadioButton(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIExportDialog.usePurpleNumbers")); //$NON-NLS-1$
		useAnchorNumbers.addItemListener(this);
		useAnchorNumbers.setFont(font);
		gc.gridy = y;
		gc.gridx = 1;
		y++;
		gc.gridwidth=1;
		gb.setConstraints(useAnchorNumbers, gc);
		innerAnchorPanelTop.add(useAnchorNumbers);

		ButtonGroup anchorGroup = new ButtonGroup();
		anchorGroup.add(useAnchorImages);
		anchorGroup.add(useAnchorNumbers);

		innerAnchorPanel = new JPanel();
		innerAnchorPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
		gb = new GridBagLayout();
		gc = new GridBagConstraints();
		innerAnchorPanel.setLayout(gb);
		gc.anchor = GridBagConstraints.WEST;
		gc.insets = new Insets(5,5,5,5);

		y=0;

		createAnchorImageList();
		gc.gridy = y;
		//y++;
		gc.gridwidth=2;
		gb.setConstraints(lstAnchorImages, gc);
		innerAnchorPanel.add(lstAnchorImages);

		JTextArea area = new JTextArea(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIExportDialog.selectDefaultAnchorImage")); //$NON-NLS-1$
		area.setBackground(innerAnchorPanel.getBackground());
		area.setColumns(20);
		area.setRows(7);
		area.setLineWrap(true);
		area.setWrapStyleWord(true);
		area.setEnabled(false);
		gc.gridy = y;
		y++;
		gc.gridwidth=1;
		gb.setConstraints(area, gc);
		innerAnchorPanel.add(area);

		JLabel label = new JLabel(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIExportDialog.anchorImage")+": "); //$NON-NLS-1$ //$NON-NLS-2$
		label.setFont(font);
		gc.gridy = y;
		gc.gridwidth=1;
		gb.setConstraints(label, gc);
		innerAnchorPanel.add(label);

		anchorImage = new JTextField(""); //$NON-NLS-1$
		anchorImage.setEditable(false);
		anchorImage.setColumns(25);
		anchorImage.setMargin(new Insets(2,2,2,2));
		anchorImage.setEnabled(true);
		gc.gridy = y;
		gc.gridwidth=1;
		gb.setConstraints(anchorImage, gc);
		innerAnchorPanel.add(anchorImage);

		pbBrowse = new UIButton(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIExportDialog.browseButton")); //$NON-NLS-1$
		pbBrowse.setMnemonic(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIExportDialog.browseButtonMnemonic").charAt(0)); //$NON-NLS-1$
		pbBrowse.addActionListener(this);
		pbBrowse.setEnabled(false);
		gc.gridy = y;
		gc.gridwidth=1;
		gb.setConstraints(pbBrowse, gc);
		innerAnchorPanel.add(pbBrowse);

		anchorPanel.add(innerAnchorPanelTop, BorderLayout.NORTH);
		anchorPanel.add(innerAnchorPanel, BorderLayout.CENTER);

		return anchorPanel;
	}

	/**
	 *	Create a panel holding other export options.
	 */
	private JPanel createOptionsPanel() {

		JPanel optionsPanel = new JPanel();
		optionsPanel.setBorder(new EmptyBorder(10,10,10,10));
		optionsPanel.setFont(new Font("Dialog", Font.PLAIN, 12)); //$NON-NLS-1$

		GridBagLayout gb = new GridBagLayout();
		GridBagConstraints gc = new GridBagConstraints();
		optionsPanel.setLayout(gb);
		gc.anchor = GridBagConstraints.WEST;
		gc.gridwidth=1;

		int y=0;

		lblFormatUsed = new JLabel(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIExportDialog.outlineFormat")); //$NON-NLS-1$
		lblFormatUsed.setFont(font);
		gc.gridy = y;
		gc.gridwidth = 1;
		gb.setConstraints(lblFormatUsed, gc);
		optionsPanel.add(lblFormatUsed);
	
		this.createStylesChoiceBox();
		gc.gridy = y;
		gc.gridwidth = 1;
		gb.setConstraints(oStyles, gc);
		optionsPanel.add(oStyles);

		pbFormatOutput = new UIButton(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIExportDialog.editFormatButton")); //$NON-NLS-1$
		pbFormatOutput.setMnemonic(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIExportDialog.editFormatButtonMnemonic").charAt(0)); //$NON-NLS-1$
		pbFormatOutput.addActionListener(this);		
		gc.gridy = y;
		//gc.weightx = 10;
		gb.setConstraints(pbFormatOutput, gc);
		optionsPanel.add(pbFormatOutput);
		y++;
		
		gc.gridwidth = 3;		
		
		optimizeForWord = new JCheckBox(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIExportDialog.optimiseForWord")); //$NON-NLS-1$
		optimizeForWord.addItemListener(this);
		optimizeForWord.setSelected(false);
		optimizeForWord.setFont(font);
		gc.gridy = y;
		y++;
		gb.setConstraints(optimizeForWord, gc);
		optionsPanel.add(optimizeForWord);

		JSeparator sep = new JSeparator();
		gc.gridy = y;
		gc.insets = new Insets(3,0,5,0);
		y++;
		gc.fill = GridBagConstraints.HORIZONTAL;
		gb.setConstraints(sep, gc);
		optionsPanel.add(sep);
		gc.insets = new Insets(0,0,0,0);

		includeNavigationBar = new JCheckBox(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIExportDialog.includeNavMenu")); //$NON-NLS-1$
		includeNavigationBar.addItemListener(this);
		includeNavigationBar.setSelected(false);
		includeNavigationBar.setFont(font);
		gc.gridy = y;
		y++;
		gb.setConstraints(includeNavigationBar, gc);
		optionsPanel.add(includeNavigationBar);

		JLabel label = new JLabel(" "); //$NON-NLS-1$
		gc.gridy = y;
		y++;
		gb.setConstraints(label, gc);
		optionsPanel.add(label);

		includeLinks = new JCheckBox(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIExportDialog.inlcudeLinkLabels")); //$NON-NLS-1$
		includeLinks.addItemListener(this);
		includeLinks.setSelected(false);
		includeLinks.setFont(font);
		gc.gridy = y;
		y++;
		//gb.setConstraints(includeLinks, gc);
		//optionsPanel.add(includeLinks);

		includeNodeAuthor = new JCheckBox(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIExportDialog.inlcudeNodeAuthor")); //$NON-NLS-1$
		includeNodeAuthor.addItemListener(this);
		includeNodeAuthor.setSelected(false);
		includeNodeAuthor.setFont(font);
		gc.gridy = y;
		y++;
		gb.setConstraints(includeNodeAuthor, gc);
		optionsPanel.add(includeNodeAuthor);

		includeImage = new JCheckBox(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIExportDialog.inlcudeImages")); //$NON-NLS-1$
		includeImage.addItemListener(this);
		includeImage.setSelected(true);
		includeImage.setFont(font);
		gc.gridy = y;
		y++;
		gb.setConstraints(includeImage, gc);
		optionsPanel.add(includeImage);

		cbIncludeTags = new JCheckBox(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIExportDialog.includeTags")); //$NON-NLS-1$
		cbIncludeTags.addItemListener(this);
		cbIncludeTags.setFont(font);
		gc.gridy = y;
		y++;
		gb.setConstraints(cbIncludeTags, gc);
		optionsPanel.add(cbIncludeTags);
		
		cbIncludeViews = new JCheckBox(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIExportDialog.inlcudeViews")); //$NON-NLS-1$
		cbIncludeViews.addItemListener(this);
		cbIncludeViews.setFont(font);
		gc.gridy = y;
		y++;
		gb.setConstraints(cbIncludeViews, gc);
		optionsPanel.add(cbIncludeViews);
		
		ButtonGroup bg = new ButtonGroup();

		inlineView = new JRadioButton("\t"+LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIExportDialog.tagsInline")); //$NON-NLS-1$ //$NON-NLS-2$
		inlineView.addItemListener(this);
		inlineView.setSelected(false);
		inlineView.setFont(font);
		gc.gridy = y;
		y++;
		gb.setConstraints(inlineView, gc);
		bg.add(inlineView);
		optionsPanel.add(inlineView);

		newView = new JRadioButton("\t"+LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIExportDialog.tagsNewWindow")); //$NON-NLS-1$ //$NON-NLS-2$
		newView.addItemListener(this);
		newView.setSelected(false);
		newView.setFont(font);
		gc.gridy = y;
		y++;
		gb.setConstraints(newView, gc);
		bg.add(newView);
		optionsPanel.add(newView);

		sep = new JSeparator();
		gc.gridy = y;
		gc.insets = new Insets(3,0,5,0);
		y++;
		gc.fill = GridBagConstraints.HORIZONTAL;
		gb.setConstraints(sep, gc);
		optionsPanel.add(sep);
		gc.insets = new Insets(0,0,0,0);

		// DETAIL PAGES SECTION

		JPanel detailPanel = new JPanel();
		detailPanel.setBorder(new EmptyBorder(10,10,10,10));
		detailPanel.setFont(new Font("Dialog", Font.PLAIN, 12)); //$NON-NLS-1$

		// CREATE DATE PANEL FIRST FOR REFERENCE REASONS
		JPanel datePanel = createDatePanel();

		noNodeDetail = new JRadioButton(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIExportDialog.noDetails")); //$NON-NLS-1$
		noNodeDetail.addItemListener(this);
		noNodeDetail.setFont(font);
		gc.gridy = y;
		y++;
		gb.setConstraints(noNodeDetail, gc);
		optionsPanel.add(noNodeDetail);

		includeNodeDetail = new JRadioButton(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIExportDialog.allDetails")); //$NON-NLS-1$
		includeNodeDetail.addItemListener(this);
		includeNodeDetail.setFont(font);
		gc.gridy = y;
		y++;
		gb.setConstraints(includeNodeDetail, gc);
		optionsPanel.add(includeNodeDetail);

		includeNodeDetailDate = new JRadioButton(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIExportDialog.selectedDetails")+": "); //$NON-NLS-1$ //$NON-NLS-2$
		includeNodeDetailDate.addItemListener(this);
		includeNodeDetailDate.setFont(font);
		gc.gridy = y;
		y++;
		gb.setConstraints(includeNodeDetailDate, gc);
		optionsPanel.add(includeNodeDetailDate);

		ButtonGroup detailGroup = new ButtonGroup();
		detailGroup.add(noNodeDetail);
		detailGroup.add(includeNodeDetail);
		detailGroup.add(includeNodeDetailDate);

		// ADD DATE PANEL
		gc.gridy = y;
		y++;
		gb.setConstraints(datePanel, gc);
		optionsPanel.add(datePanel);

		JLabel other = new JLabel(" "); //$NON-NLS-1$
		gc.gridy = y;
		y++;
		gb.setConstraints(other, gc);
		optionsPanel.add(other);

		displayDetailDates = new JCheckBox(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIExportDialog.deisplayDetailDates")); //$NON-NLS-1$
		displayDetailDates.addItemListener(this);
		displayDetailDates.setSelected(false);
		displayDetailDates.setFont(font);
		gc.gridy = y;
		gb.setConstraints(displayDetailDates, gc);
		optionsPanel.add(displayDetailDates);

		/*
		hideNodeNoDates = new JCheckBox("Hide nodes outside of dates");
		gc.gridy = y;
		y++;
		gc.gridwidth=1;
		gb.setConstraints(hideNodeNoDates, gc);
		hideNodeNoDates.addItemListener(this);
		hideNodeNoDates.setSelected(false);
		detailPanel.add(hideNodeNoDates);
		*/

		return optionsPanel;
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
							FormatProperties.outlineFormat = selected;
							FormatProperties.setFormatProp("outlineFormat", selected); //$NON-NLS-1$
							FormatProperties.saveFormatProps();
						}
                	}
               	};
	            choiceThread.start();
        	}
		};
		oStyles.addActionListener(choiceaction);

		return oStyles;
	}
	
	private void reloadData() {
		try {
			vtStyles.clear();
			File main = new File(UIHTMLFormatDialog.DEFAULT_FILE_PATH);
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
			}
		}
		catch(Exception ex) {
			ex.printStackTrace();
			ProjectCompendium.APP.displayError("Exception: (UIExportDialog.reloadData) " + ex.getMessage()); //$NON-NLS-1$
		}		
	}
	
	/**
	 * Crate the panel hold the node detail pages date filter options.
	 */
	private JPanel createDatePanel() {

		JPanel panel = new JPanel(new BorderLayout());

		fromPanel = new UIDatePanel(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIExportDialog.from")+": "); //$NON-NLS-1$ //$NON-NLS-2$
		panel.add(fromPanel, BorderLayout.WEST);

		toPanel = new UIDatePanel(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIExportDialog.to")+": "); //$NON-NLS-1$ //$NON-NLS-2$
		panel.add(toPanel, BorderLayout.EAST);

		return panel;
	}

	/**
	 * Create the list to display anchor images.
	 */
	private void createAnchorImageList() {

   	 	String[] images = {sBaseAnchorPath+"anchor0.gif", sBaseAnchorPath+"anchor1.gif", sBaseAnchorPath+"anchor2.gif", sBaseAnchorPath+"anchor3.gif", sBaseAnchorPath+"anchor4.gif", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
							sBaseAnchorPath+"anchor5.gif", sBaseAnchorPath+"anchor6.gif", sBaseAnchorPath+"anchor7.gif"}; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		lstAnchorImages = new UINavList(images);
		lstAnchorImages.setEnabled(false);
		lstAnchorImages.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        anchorImageListRenderer = new AnchorImageCellRenderer();
		lstAnchorImages.setCellRenderer(anchorImageListRenderer);
		lstAnchorImages.setBorder(new CompoundBorder(new LineBorder(Color.gray ,1), new EmptyBorder(5,5,5,5)));
		imagescroll = new JScrollPane(lstAnchorImages);
		imagescroll.setPreferredSize(new Dimension(150, 60));

		MouseListener fontmouse = new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 1) {
					String image = (String)lstAnchorImages.getSelectedValue();
					setAnchorImage(image);
				}
			}
		};
		KeyListener fontkey = new KeyAdapter() {
           	public void keyPressed(KeyEvent e) {
				if ((e.getKeyCode() == KeyEvent.VK_ENTER) && (e.getModifiers() == 0)) {
					String image = (String)lstAnchorImages.getSelectedValue();
					setAnchorImage(image);
				}
			}
		};

		lstAnchorImages.addKeyListener(fontkey);
		lstAnchorImages.addMouseListener(fontmouse);
	}

	/**
	 * Helper class to render the anchor image list.
	 */
	public class AnchorImageCellRenderer extends JLabel implements ListCellRenderer {

	  	protected Border noFocusBorder;

		public AnchorImageCellRenderer() {
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

 	 		if (isSelected) {
				setBackground(list.getSelectionBackground());
				setForeground(list.getSelectionForeground());
			}
			else {
				setBackground(list.getBackground());
				setForeground(list.getForeground());
			}

			setText((String)value);
			setHorizontalTextPosition(SwingConstants.TRAILING);
			setIconTextGap(6);
			setBorder((cellHasFocus) ? UIManager.getBorder("List.focusCellHighlightBorder") : noFocusBorder); //$NON-NLS-1$

			ImageIcon image = new ImageIcon((String)value);
   			setIcon(image);

			return this;
		}
	}

	/******* EVENT HANDLING METHODS *******/

	/**
	 * Handle action events coming from the buttons.
 	 * @param evt, the associated ActionEvent object.
	 */
	public void actionPerformed(ActionEvent evt) {

		Object source = evt.getSource();

		// Handle button events
		if (source instanceof JButton) {
			if (source == pbExport) {
				onExport();
				saveProperties();
			}
			else if (source == pbViews) {
				onViews();
			}
			else if (source == pbFormatOutput) {
				UIHTMLFormatDialog dialog2 = new UIHTMLFormatDialog(ProjectCompendium.APP);
				dialog2.setVisible(true);
				while (dialog2.isVisible()) {}
				reloadData();
			}
			else if (source == pbBrowse) {
				onBrowse();
			}
			else if (source == pbClose) {
				onCancel(false);
			}
		}
	}

	/**
	 * Open the file browser dialog for the user to select an anchor image.
	 */
	private void onBrowse() {

		UIFileChooser fileDialog = new UIFileChooser();
		fileDialog.setDialogTitle(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIExportDialog.selectImage")); //$NON-NLS-1$
		fileDialog.setFileFilter(UIImages.IMAGE_FILTER);
		fileDialog.setDialogType(JFileChooser.OPEN_DIALOG);

		String fileName = ""; //$NON-NLS-1$
		UIUtilities.centerComponent(fileDialog, this);
		int retval = fileDialog.showDialog(this, null);

		if (retval == JFileChooser.APPROVE_OPTION) {
        	if ((fileDialog.getSelectedFile()) != null) {

            	fileName = fileDialog.getSelectedFile().getAbsolutePath();

				if (fileName != null) {
					setAnchorImage(fileName);
				}
			}
		}
	}

	/**
	 * Open the views dialog for the user to select views to export.
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
	 * Apply the export options previously saved, to the various ui elements.
	 */
	private void applyLoadedProperties() {

		displayInDifferentPages.setSelected(bDisplayInDifferentPages);

		if (depth == 2) {
			fullDepth.setSelected(true);
		}
		else if (depth == 1) {
			oneDepth.setSelected(true);
		}
		else {
			currentDepth.setSelected(true);
		}

		anchorImage.setText(sAnchorImage);
		includeNodeAnchor.setSelected(bIncludeNodeAnchors);
		includeDetailAnchor.setSelected(bIncludeDetailAnchors);
		if (bUseAnchorNumbers)
			useAnchorNumbers.setSelected(true);
		else
			useAnchorImages.setSelected(true);

		//toPanel.setDate(toDate);
		//fromPanel.setDate(fromDate);

		includeNodeDetail.setSelected(bIncludeNodeDetail);
		includeNodeDetailDate.setSelected(bIncludeNodeDetailDate);
		if (!bIncludeNodeDetail && !bIncludeNodeDetailDate)
			noNodeDetail.setSelected(true);

		displayDetailDates.setSelected(bDisplayDetailDates);
		includeNodeAuthor.setSelected(bIncludeNodeAuthor);
		includeImage.setSelected(bIncludeImage);
		includeLinks.setSelected(bIncludeLinks);
		optimizeForWord.setSelected(bOptimizeForWord);

		//hideNodeNoDates.setSelected(bHideNodeNoDates);
		
		cbIncludeViews.setSelected(bIncludeViews);		
		cbIncludeTags.setSelected(bIncludeTags);		
		
		includeNavigationBar.setSelected(bIncludeNavigationBar);
		inlineView.setSelected(bInlineView);
		newView.setSelected(bNewView);

		cbOpenAfter.setSelected(bOpenAfter);
		cbToZip.setSelected(bToZip);
		cbWithRefs.setSelected(bIncludeReferences);

		if (!hasSelectedViews()) {
			bSelectedViewsOnly = false;
			selectedViews.setEnabled(false);
		} 
				
		selectedViews.setSelected(bSelectedViewsOnly);		
		otherViews.setSelected(bOtherViews);

		if (!bSelectedViewsOnly && !bOtherViews)
			allNodes.setSelected(true);

	   	lstAnchorImages.setSelectedValue((Object)sAnchorImage, true);
	}


	/**
	 * Return the to date for filtering node detail pages.
	 * @return GregorianCalendar, the to date for filtering node detail pages.
	 */
	public GregorianCalendar getToDate() {
		return toPanel.getDateEnd();
	}

	/**
	 * Return the from date for filtering node detail pages.
	 * @return GregorianCalendar, the from date for filtering node detail pages.
	 */
	public GregorianCalendar getFromDate() {
		return fromPanel.getDate();
	}

	/**
	 * Set the anchor image to use.
 	 * @param sImage, the path of the anchor image to use.
	 */
	public void setAnchorImage(String sImage) {
		if (sImage != null && !sImage.equals("")) { //$NON-NLS-1$
			sAnchorImage = sImage;
			anchorImage.setText(sImage);
		}
	}

	/**
	 * Set the current view to being exported.
 	 * @param view com.compendium.core.datamodel.View, the current view being exported.
	 */
	public void setCurrentView(View view) {
		currentView = view;
	}

	/**
	 * Check that the dates for filtering node detail pages have been entered correctly.
	 */
	public boolean checkDates() {
		if (fromPanel.checkDate() && toPanel.checkDate())
			return true;

		return false;
	}

	/**
	 * Records the fact that a checkbox / radio button state has been changed and stores the new data.
	 * @param e, the associated ItemEvent.
	 */
	public void itemStateChanged(ItemEvent e) {

		Object source = e.getItemSelectable();


		if (source == displayInDifferentPages) {
			bDisplayInDifferentPages = displayInDifferentPages.isSelected();
			if (bDisplayInDifferentPages) {
				if (titlefield != null) {
					titlefield.setEditable(true);
					titleLabel.setEnabled(true);
					titlefield.repaint();
				}
			}
			else {
				if (titlefield != null) {
					titlefield.setText(""); //$NON-NLS-1$
					titlefield.setEditable(false);
					titleLabel.setEnabled(false);
					titlefield.repaint();
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
		else if (source == includeDetailAnchor) {
			bIncludeDetailAnchors = includeDetailAnchor.isSelected();
		}
		else if (source == includeNodeAnchor) {
			bIncludeNodeAnchors = includeNodeAnchor.isSelected();
		}
		else if (source == useAnchorNumbers) {
			bUseAnchorNumbers = useAnchorNumbers.isSelected();
			if (useAnchorNumbers.isSelected()) {
				pbBrowse.setEnabled(false);
				lstAnchorImages.setEnabled(false);
			}
			else if (!useAnchorNumbers.isSelected() && !useAnchorNumbers.isSelected()) {
				pbBrowse.setEnabled(true);
				lstAnchorImages.setEnabled(true);
			}
		}
		else if (source == useAnchorImages) {
			bUseAnchorImages = useAnchorImages.isSelected();
			if (useAnchorImages.isSelected()) {
				pbBrowse.setEnabled(true);
				lstAnchorImages.setEnabled(true);
			}
			else if (!useAnchorImages.isSelected() && !useAnchorImages.isSelected()) {
				pbBrowse.setEnabled(false);
				lstAnchorImages.setEnabled(false);
			}
		}
		else if (source == fullDepth && fullDepth.isSelected()) {
			depth = 2;

			displayInDifferentPages.setEnabled(true);
			displayInDifferentPages.repaint();
		}
		else if (source == oneDepth && oneDepth.isSelected()) {
			depth = 1;

			displayInDifferentPages.setEnabled(true);
			displayInDifferentPages.repaint();
		}
		else if (source == currentDepth && currentDepth.isSelected()) {
			depth = 0;

			if (allNodes.isSelected()) {
				displayInDifferentPages.setSelected(false);
				displayInDifferentPages.setEnabled(false);
				titlefield.setEditable(false);
				titleLabel.setEnabled(false);
			}
			else {
				displayInDifferentPages.setEnabled(true);
				displayInDifferentPages.repaint();
			}
		}

		else if (source == selectedViews && selectedViews.isSelected()) {
			bOtherViews = false;
			bSelectedViewsOnly = true;

			pbViews.setEnabled(false);
			displayInDifferentPages.setEnabled(true);
			displayInDifferentPages.repaint();
			updateViewsList();
		}
		else if (source == allNodes && allNodes.isSelected()) {
			bOtherViews = false;
			bSelectedViewsOnly = false;

			pbViews.setEnabled(false);

			if (currentDepth.isSelected()) {
				displayInDifferentPages.setSelected(false);
				displayInDifferentPages.setEnabled(false);
				titlefield.setEditable(false);
				titleLabel.setEnabled(false);
			}
			else {
				displayInDifferentPages.setEnabled(true);
				displayInDifferentPages.repaint();
			}
			updateViewsList();
		}
		else if (source == otherViews && otherViews.isSelected()) {
			bOtherViews = true;
			bSelectedViewsOnly = false;

			pbViews.setEnabled(true);
			displayInDifferentPages.setEnabled(true);
			displayInDifferentPages.repaint();
			updateViewsList();
		}
		else if (source == includeNodeAuthor) {
			bIncludeNodeAuthor = includeNodeAuthor.isSelected();
		}
		else if (source == displayDetailDates) {
			bDisplayDetailDates = displayDetailDates.isSelected();
		}
		else if (source == hideNodeNoDates) {
			bHideNodeNoDates = hideNodeNoDates.isSelected();
		}
		else if (source == noNodeDetail && noNodeDetail.isSelected()) {
			bIncludeNodeDetail = false;
			bIncludeNodeDetailDate = false;

			toPanel.setDateEnabled(false);
			fromPanel.setDateEnabled(false);
		}
		else if (source == includeNodeDetail && includeNodeDetail.isSelected()) {
			bIncludeNodeDetail= true;
			bIncludeNodeDetailDate = false;

			toPanel.setDateEnabled(false);
			fromPanel.setDateEnabled(false);
		}
		else if (source == includeNodeDetailDate && includeNodeDetailDate.isSelected()) {
			bIncludeNodeDetail = false;
			bIncludeNodeDetailDate = true;

			toPanel.setDateEnabled(true);
			fromPanel.setDateEnabled(true);
		}
		else if (source == includeImage) {
			bIncludeImage = includeImage.isSelected();
		}
		else if (source == optimizeForWord) {
			bOptimizeForWord = optimizeForWord.isSelected();
		}
		
		else if (source == includeLinks) {
			bIncludeLinks = includeLinks.isSelected();
		}
		else if (source == includeNavigationBar) {
			bIncludeNavigationBar = includeNavigationBar.isSelected();
		}
		else if (source == cbIncludeTags) {
			bIncludeTags = cbIncludeTags.isSelected();
			if ((cbIncludeViews != null && !cbIncludeViews.isSelected()) && !cbIncludeTags.isSelected()) {
				inlineView.setEnabled(false);
				newView.setEnabled(false);
			} else {
				inlineView.setEnabled(true);
				newView.setEnabled(true);				
			}			
		}
		else if (source == cbIncludeViews) {
			bIncludeViews = cbIncludeViews.isSelected();
			if (!cbIncludeViews.isSelected() && (cbIncludeTags != null && !cbIncludeTags.isSelected())) {
				inlineView.setEnabled(false);
				newView.setEnabled(false);
			} else {
				inlineView.setEnabled(true);
				newView.setEnabled(true);				
			}
		}
		else if (source == inlineView) {
			bInlineView = inlineView.isSelected();
		}
		else if (source == newView) {
			bNewView = newView.isSelected();
		}
	}

	/******* EXPORT *******************************************************/

	/**
	 * Handle the export action. Rquest the export file be selected.
	 * @see #processExport
	 */
	public void onExport() {
		
		// CHECK ALL DATE INFORMATION ENTERED, IF REQUIRED
		if (bIncludeNodeDetailDate) {
			if (!checkDates()) {
				ProjectCompendium.APP.displayMessage(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIExportDialog.errorDateInfo"), LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIExportDialog.dateError")); //$NON-NLS-1$ //$NON-NLS-2$
				return;
			}
		}

		if (otherViews.isSelected()) {
			if(viewsDialog == null || (viewsDialog.getTable().getSelectedRows()).length <= 0) {
				ProjectCompendium.APP.displayMessage(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIExportDialog.selectView"), LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIExportDialog.webOutlineExport")); //$NON-NLS-1$ //$NON-NLS-2$
				return;
			}
		}

		boolean toZip = cbToZip.isSelected();
		if (toZip) {
			UIFileFilter filter = new UIFileFilter(new String[] {"zip"}, "ZIP Files"); //$NON-NLS-1$ //$NON-NLS-2$

			UIFileChooser fileDialog = new UIFileChooser();
			fileDialog.setDialogTitle(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIExportDialog.enterFileName")); //$NON-NLS-1$
			fileDialog.setFileFilter(filter);
			fileDialog.setApproveButtonText(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIExportDialog.saveButton")); //$NON-NLS-1$
			fileDialog.setRequiredExtension(".zip"); //$NON-NLS-1$

		    // FIX FOR MAC - NEEDS '/' ON END TO DENOTE A FOLDER
		    File file = new File(exportDirectory+ProjectCompendium.sFS);
		    if (file.exists()) {
				fileDialog.setCurrentDirectory(file);
			}

			int retval = fileDialog.showSaveDialog(ProjectCompendium.APP);
			if (retval == JFileChooser.APPROVE_OPTION) {
	        	if ((fileDialog.getSelectedFile()) != null) {

	            	fileName = fileDialog.getSelectedFile().getAbsolutePath();
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
			fileDialog.setDialogTitle(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIExportDialog.enterFileName")); //$NON-NLS-1$
			fileDialog.setFileFilter(filter);
			fileDialog.setApproveButtonText(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIExportDialog.saveButton")); //$NON-NLS-1$
			fileDialog.setRequiredExtension(".html"); //$NON-NLS-1$

		    // FIX FOR MAC - NEEDS '/' ON END TO DENOTE A FOLDER
		    File file = new File(exportDirectory+ProjectCompendium.sFS);
		    if (file.exists()) {
				fileDialog.setCurrentDirectory(file);
			}

			int retval = fileDialog.showSaveDialog(ProjectCompendium.APP);
			if (retval == JFileChooser.APPROVE_OPTION) {
	        	if ((fileDialog.getSelectedFile()) != null) {

	            	fileName = fileDialog.getSelectedFile().getAbsolutePath();
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

		setCursor(new Cursor(Cursor.WAIT_CURSOR));
		if (fileName != null && !fileName.equals("")) { //$NON-NLS-1$
			if (!processExport())
				onCancel(false);
			else {
				if (bOpenAfter) {
					ExecuteControl.launch(fileName);
				}
				onCancel(true);
			}
		}
		setCursor(Cursor.getDefaultCursor());
	}


	/**
	 *	Process the export.
	 */
	public boolean processExport() {
		oHTMLExport = new HTMLOutline(bIncludeNodeDetail,
										bIncludeNodeDetailDate,
									  	bIncludeNodeAuthor,
										nStartExportAtLevel,
										fileName, bToZip);

		if (bIncludeNodeDetailDate) {
			GregorianCalendar fDate = getFromDate();
			GregorianCalendar tDate = getToDate();
			fromDate = fDate.getTime().getTime();
			toDate = tDate.getTime().getTime();
			if (tDate != null && fDate != null) {
				oHTMLExport.setFromDate(fDate);
				oHTMLExport.setToDate(tDate);
			}
		}

		oHTMLExport.setIncludeLinks(bIncludeLinks);
		oHTMLExport.setIncludeImage(bIncludeImage);
		oHTMLExport.setIncludeNodeAnchors(bIncludeNodeAnchors);
		oHTMLExport.setIncludeDetailAnchors(bIncludeDetailAnchors);
		oHTMLExport.setUseAnchorNumbers(bUseAnchorNumbers);
		if (!bUseAnchorNumbers)
			oHTMLExport.setAnchorImage(sAnchorImage);

		oHTMLExport.setTitle(titlefield.getText());
		oHTMLExport.setDisplayInDifferentPages(bDisplayInDifferentPages);
		oHTMLExport.setDisplayDetailDates(bDisplayDetailDates);
		oHTMLExport.setHideNodeNoDates(bHideNodeNoDates);
		oHTMLExport.setIncludeNavigationBar(bIncludeNavigationBar);
		oHTMLExport.setInlineView(bInlineView);
		oHTMLExport.setNewView(bNewView);
		oHTMLExport.setIncludeViews(bIncludeViews);
		oHTMLExport.setIncludeTags(bIncludeTags);
		oHTMLExport.setOptimizeForWord(bOptimizeForWord);

		oHTMLExport.setIncludeFiles(bIncludeReferences);

		boolean sucessful = false;
		
		if (printExport(oHTMLExport, otherViews.isSelected(), bSelectedViewsOnly, depth)) {
			oHTMLExport.print();
			sucessful = true;
		}

		return sucessful;
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
	 * Get the views to export depending on user options.
	 */
	private Vector getSelectedViews(HTMLOutline oHTMLExport, boolean otherViews, boolean bSelectedViewsOnly, int depth) {
		model = ProjectCompendium.APP.getModel();
		session = model.getSession();
		vs = model.getViewService();

		Vector selectedViews = new Vector();

		// IF MULTIPLE VIEWS
		if (otherViews) {
			oHTMLExport.setCurrentViewAsHomePage(false);

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

			oHTMLExport.setCurrentViewAsHomePage(false);
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

			//ADD THE CHILD VIEWS TO THE childViews VECTOR
			if (depth == 1) {
				for (int i = 0; i < vtTemp.size(); i++) {
					NodePosition nodePos = (NodePosition)vtTemp.elementAt(i);
					View view = (View)nodePos.getNode();
					htCheckDepth.put((Object)view.getId(), view);
					selectedViews = getChildViews(view, selectedViews, false);
				}
			} else if (depth == 2) {
				for (int i = 0; i < vtTemp.size(); i++) {
					NodePosition nodePos = (NodePosition)vtTemp.elementAt(i);
					View view = (View)nodePos.getNode();
					htCheckDepth.put((Object)view.getId(), view);
					selectedViews = getChildViews(view, selectedViews, true);
				}
			}
		}
		else {
			// IF JUST CURRENT VIEW
			oHTMLExport.setCurrentViewAsHomePage(true);

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
	 * Helper method when getting view to export.
	 * @param view com.compendium.core.datamodel.View, the view to get the child nodes for.
	 * @param childViews, the list of views aquired.
	 * @param fullDepth, are we searching to full depth?
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
	 * Create the HTML files.
	 */
	public boolean printExport(HTMLOutline oHTMLExport, boolean bOtherViews, boolean bSelectedViewsOnly, int depth) {
		ProjectCompendium.APP.setWaitCursor();
		Vector selectedViews = getSelectedViews(oHTMLExport, bOtherViews, bSelectedViewsOnly, depth);
		if (selectedViews.size() == 0)
			return true;

		arrange = new UIArrangeLeftRight();

   		// CYCLE THROUGH selectedViews VECTOR
		try {
			int count = selectedViews.size();
			for(int i=0; i < count; i++) {

				//clear the hashtables and vectors for a new export
				htNodesLevel.clear();
				htNodes.clear();
				htNodesBelow.clear();

				View view = (View)selectedViews.elementAt(i);
				if (view == null)
					continue;
				
				if (!view.isMembersInitialized()) {
					view.initializeMembers();
				}

				oHTMLExport.runGenerator((NodeSummary)view, 0, -1);
				ProjectCompendium.APP.setStatus(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIExportDialog.calculatingData")); //$NON-NLS-1$

				if (!arrange.processView(view)) {
					return false;
				}

				htNodes = arrange.getNodes();
				htNodesLevel = arrange.getNodesLevel();
				htNodesBelow = arrange.getNodesBelow();

				nodeLevelList = arrange.getNodeLevelList();

				//now print the nodes
				ProjectCompendium.APP.setStatus(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIExportDialog.gerenatingFile")); //$NON-NLS-1$

				if (nodeLevelList.size() > 0) {
					// CYCLE THROUGH NODES SORTED BY YPOS AND PRINT THEM AND THIER CHILDREN
					for(Enumeration f = ((Vector)nodeLevelList.elementAt(0)).elements();f.hasMoreElements();) {

						String nodeToPrintId = (String)f.nextElement();
						NodeSummary nodeToPrint = (NodeSummary)htNodes.get(nodeToPrintId);						
						if (View.isListType(view.getType())) {
							printNode(nodeToPrintId, true, oHTMLExport);
						}
						else {
							printNode(nodeToPrintId, false, oHTMLExport);
						}
					}
				}
				ProjectCompendium.APP.setStatus(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIExportDialog.finishedExporting") + view.getLabel() +" "+LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIExportDialog.toHTML")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			}
		}
		catch(Exception ex) {
			ex.printStackTrace();
			ProjectCompendium.APP.displayError("Exception: (UIExportDialog.printExport) \n\n" + ex.getMessage()); //$NON-NLS-1$
		}

		ProjectCompendium.APP.setDefaultCursor();
		ProjectCompendium.APP.setStatus(""); //$NON-NLS-1$

		return true;
	}

	/** holds the information about nodes recursed when processing the outline data*/
	private Hashtable nodesRecursed = new Hashtable(51);
	
	/** max number a node can recurse */ 
	private int recursionCount  	= 3; 
	
	/**
	 * Create the HTML files for each given node.
 	 * @param nodeToPrintId, the id of the node to process.
	 * @param printingList, is the current view a list.
	 */
	private void printNode(String nodeToPrintId, boolean printingList, HTMLOutline oHTMLExport) {
		
		Integer count = new Integer(1);
		if(nodesRecursed.containsKey(nodeToPrintId)){
			count = (Integer) nodesRecursed.get(nodeToPrintId);
			if(count.intValue() > recursionCount) {
				return;
			} else {
				count = new Integer(count.intValue() + 1);
				nodesRecursed.put(nodeToPrintId, count);
			}
		} else {
			nodesRecursed.put(nodeToPrintId, count);
		}
		
		if (!printingList) {
			nodeIndex = -1;
		} else {
			nodeIndex++;
		}

		NodeSummary nodeToPrint = (NodeSummary)htNodes.get(nodeToPrintId);

		int lev = ((Integer)htNodesLevel.get(nodeToPrint.getId())).intValue();

		oHTMLExport.runGenerator(nodeToPrint, lev, nodeIndex);

		Vector nodeChildren = (Vector)htNodesBelow.get(nodeToPrintId);
		if (nodeChildren != null) {
			//System.out.println("printing children for "+nodeToPrint.getLabel());

			for (int i = 0; i < nodeChildren.size(); i++) {
				printNode((String)nodeChildren.elementAt(i), printingList, oHTMLExport);
			}
		}
	}

	/**
	 * Load the user saved options for exporting.
	 */
	private void loadProperties() {

		File optionsFile = new File(EXPORT_OPTIONS_FILE_NAME);
		optionsProperties = new Properties();
		if (optionsFile.exists()) {
			try {
				optionsProperties.load(new FileInputStream(EXPORT_OPTIONS_FILE_NAME));

				String value = optionsProperties.getProperty("anchorimage"); //$NON-NLS-1$
				if (value != null) {
					setAnchorImage(value);
				}

				value = optionsProperties.getProperty("includerefs"); //$NON-NLS-1$
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

				value = optionsProperties.getProperty("includenodeanchors"); //$NON-NLS-1$
				if (value != null) {
					if (value.toLowerCase().equals("yes")) { //$NON-NLS-1$
						bIncludeNodeAnchors = true;
					} else {
						bIncludeNodeAnchors = false;
					}
				}

				value = optionsProperties.getProperty("includedetailanchors"); //$NON-NLS-1$
				if (value != null) {
					if (value.toLowerCase().equals("yes")) { //$NON-NLS-1$
						bIncludeDetailAnchors = true;
					} else {
						bIncludeDetailAnchors = false;
					}
				}

				value = optionsProperties.getProperty("useanchornumbers"); //$NON-NLS-1$
				if (value != null) {
					if (value.toLowerCase().equals("yes")) { //$NON-NLS-1$
						bUseAnchorNumbers = true;
						bUseAnchorImages = false;
					} else {
						bUseAnchorNumbers = false;
						bUseAnchorImages = true;
					}
				}

				value = optionsProperties.getProperty("depth"); //$NON-NLS-1$
				if (value != null) {
					if (value.equals("1")) //$NON-NLS-1$
						depth = 1;
					else if (value.equals("2")) //$NON-NLS-1$
						depth = 2;
					else
						depth = 0;
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

				value = optionsProperties.getProperty("nodedetail"); //$NON-NLS-1$
				if (value != null) {
					if (value.toLowerCase().equals("yes")) { //$NON-NLS-1$
						bIncludeNodeDetail = true;
					} else {
						bIncludeNodeDetail = false;
					}
				}

				value = optionsProperties.getProperty("nodedetaildate"); //$NON-NLS-1$
				if (value != null) {
					if (value.toLowerCase().equals("yes")) //$NON-NLS-1$
						bIncludeNodeDetailDate = true;
					else
						bIncludeNodeDetailDate = false;
				}

				value = optionsProperties.getProperty("hidenodenodate"); //$NON-NLS-1$
				if (value != null) {
					if (value.toLowerCase().equals("yes")) //$NON-NLS-1$
						bHideNodeNoDates = true;
					else
						bHideNodeNoDates = false;
				}

				value = optionsProperties.getProperty("todate"); //$NON-NLS-1$
				if (value != null) {
					try  {
						toDate = new Long(value).longValue();
					}
					catch(Exception io){
						System.out.println("cannot convert todate = "+value); //$NON-NLS-1$
					}
				}

				value = optionsProperties.getProperty("fromdate"); //$NON-NLS-1$
				if (value != null) {
					try  {
						fromDate = new Long(value).longValue();
					}
					catch(Exception io){
						System.out.println("cannot convert fromdate = "+value); //$NON-NLS-1$
					}
				}

				value = optionsProperties.getProperty("nodeauthor"); //$NON-NLS-1$
				if (value != null) {
					if (value.toLowerCase().equals("yes")) { //$NON-NLS-1$
						bIncludeNodeAuthor = true;
					} else {
						bIncludeNodeAuthor = false;
					}
				}

				value = optionsProperties.getProperty("nodeimage"); //$NON-NLS-1$
				if (value != null) {
					if (value.toLowerCase().equals("yes")) { //$NON-NLS-1$
						bIncludeImage = true;
					} else {
						bIncludeImage = false;
					}
				}

				value = optionsProperties.getProperty("includelinks"); //$NON-NLS-1$
				if (value != null) {
					if (value.toLowerCase().equals("yes")) { //$NON-NLS-1$
						bIncludeLinks = true;
					} else {
						bIncludeLinks = false;
					}
				}

				value = optionsProperties.getProperty("displaydetaildates"); //$NON-NLS-1$
				if (value != null) {
					if (value.toLowerCase().equals("yes")) { //$NON-NLS-1$
						bDisplayDetailDates = true;
					} else {
						bDisplayDetailDates = false;
					}
				}

				value = optionsProperties.getProperty("displayindifferentpages"); //$NON-NLS-1$
				if (value != null) {
					if (value.toLowerCase().equals("yes")) { //$NON-NLS-1$
						bDisplayInDifferentPages = true;
					} else {
						bDisplayInDifferentPages = false;
					}
				}

				value = optionsProperties.getProperty("includenavigationbar"); //$NON-NLS-1$
				if (value != null) {
					if (value.toLowerCase().equals("yes")) { //$NON-NLS-1$
						bIncludeNavigationBar = true;
					} else {
						bIncludeNavigationBar = false;
					}
				}

				value = optionsProperties.getProperty("inlineview"); //$NON-NLS-1$
				if (value != null) {
					if (value.toLowerCase().equals("yes")) { //$NON-NLS-1$
						bInlineView = true;
					} else {
						bInlineView = false;
					}
				}

				value = optionsProperties.getProperty("includeviews"); //$NON-NLS-1$
				if (value != null) {
					if (value.toLowerCase().equals("yes")) { //$NON-NLS-1$
						bIncludeViews = true;
					} else {
						bIncludeViews = false;
					}
				}

				value = optionsProperties.getProperty("includetags"); //$NON-NLS-1$
				if (value != null) {
					if (value.toLowerCase().equals("yes")) { //$NON-NLS-1$
						bIncludeTags = true;
					} else {
						bIncludeTags = false;
					}
				}

				value = optionsProperties.getProperty("newview"); //$NON-NLS-1$
				if (value != null) {
					if (value.toLowerCase().equals("yes")) { //$NON-NLS-1$
						bNewView = true;
					} else {
						bNewView = false;
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

				value = optionsProperties.getProperty("optimizeforword"); //$NON-NLS-1$
				if (value != null) {
					if (value.toLowerCase().equals("yes")) { //$NON-NLS-1$
						bOptimizeForWord = true;
					} else {
						bOptimizeForWord = false;
					}
				}				

			} catch (IOException e) {
				ProjectCompendium.APP.displayError(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIExportDialog.errorReadingProperties")); //$NON-NLS-1$
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
			}
			else {
				optionsProperties.put("includerefs", "no"); //$NON-NLS-1$ //$NON-NLS-2$
			}

			if (bToZip == true) {
				optionsProperties.put("zip", "yes"); //$NON-NLS-1$ //$NON-NLS-2$
			}
			else {
				optionsProperties.put("zip", "no"); //$NON-NLS-1$ //$NON-NLS-2$
			}

			if (bIncludeNodeAnchors == true) {
				optionsProperties.put("includenodeanchors", "yes"); //$NON-NLS-1$ //$NON-NLS-2$
			}
			else {
				optionsProperties.put("includenodeanchors", "no"); //$NON-NLS-1$ //$NON-NLS-2$
			}

			if (bIncludeDetailAnchors == true) {
				optionsProperties.put("includedetailanchors", "yes"); //$NON-NLS-1$ //$NON-NLS-2$
			}
			else {
				optionsProperties.put("includedetailanchors", "no"); //$NON-NLS-1$ //$NON-NLS-2$
			}

			optionsProperties.put("anchorimage", sAnchorImage); //$NON-NLS-1$

			if (bUseAnchorNumbers == true) {
				optionsProperties.put("useanchornumbers", "yes"); //$NON-NLS-1$ //$NON-NLS-2$
			}
			else {
				optionsProperties.put("useanchornumbers", "no"); //$NON-NLS-1$ //$NON-NLS-2$
			}

			if (depth == 2) {
				optionsProperties.put("depth", "2"); //$NON-NLS-1$ //$NON-NLS-2$
			}
			else if (depth == 1) {
				optionsProperties.put("depth", "1"); //$NON-NLS-1$ //$NON-NLS-2$
			}
			else {
				optionsProperties.put("depth", "0"); //$NON-NLS-1$ //$NON-NLS-2$
			}

			if (bSelectedViewsOnly == true) {
				optionsProperties.put("selectedviewsonly", "yes"); //$NON-NLS-1$ //$NON-NLS-2$
			}
			else {
				optionsProperties.put("selectedviewsonly", "no"); //$NON-NLS-1$ //$NON-NLS-2$
			}

			if (bOtherViews == true) {
				optionsProperties.put("otherviews", "yes"); //$NON-NLS-1$ //$NON-NLS-2$
			}
			else {
				optionsProperties.put("otherviews", "no"); //$NON-NLS-1$ //$NON-NLS-2$
			}

			if (bIncludeNodeAuthor == true) {
				optionsProperties.put("nodeauthor", "yes"); //$NON-NLS-1$ //$NON-NLS-2$
			}
			else {
				optionsProperties.put("nodeauthor", "no"); //$NON-NLS-1$ //$NON-NLS-2$
			}

			if (bIncludeNodeDetail == true) {
				optionsProperties.put("nodedetail", "yes"); //$NON-NLS-1$ //$NON-NLS-2$
			}
			else {
				optionsProperties.put("nodedetail", "no"); //$NON-NLS-1$ //$NON-NLS-2$
			}

			if (bIncludeNodeDetailDate == true) {
				optionsProperties.put("nodedetaildate", "yes"); //$NON-NLS-1$ //$NON-NLS-2$
			}
			else {
				optionsProperties.put("nodedetaildate", "no"); //$NON-NLS-1$ //$NON-NLS-2$
			}

			if (bHideNodeNoDates == true) {
				optionsProperties.put("hidenodenodate", "yes"); //$NON-NLS-1$ //$NON-NLS-2$
			}
			else {
				optionsProperties.put("hidenodenodate", "no"); //$NON-NLS-1$ //$NON-NLS-2$
			}

			optionsProperties.put("todate", new Long(toDate).toString()); //$NON-NLS-1$
			optionsProperties.put("fromdate", new Long(fromDate).toString()); //$NON-NLS-1$

			if (bIncludeImage == true) {
				optionsProperties.put("nodeimage", "yes"); //$NON-NLS-1$ //$NON-NLS-2$
			}
			else {
				optionsProperties.put("nodeimage", "no"); //$NON-NLS-1$ //$NON-NLS-2$
			}

			if (bIncludeLinks == true) {
				optionsProperties.put("includelinks", "yes"); //$NON-NLS-1$ //$NON-NLS-2$
			}
			else {
				optionsProperties.put("includelinks", "no"); //$NON-NLS-1$ //$NON-NLS-2$
			}

			if (bDisplayDetailDates == true) {
				optionsProperties.put("displaydetaildates", "yes"); //$NON-NLS-1$ //$NON-NLS-2$
			}
			else {
				optionsProperties.put("displaydetaildates", "no"); //$NON-NLS-1$ //$NON-NLS-2$
			}

			if (bDisplayInDifferentPages == true) {
				optionsProperties.put("displayindifferentpages", "yes"); //$NON-NLS-1$ //$NON-NLS-2$
			}
			else {
				optionsProperties.put("displayindifferentpages", "no"); //$NON-NLS-1$ //$NON-NLS-2$
			}

			if (bIncludeNavigationBar == true) {
				optionsProperties.put("includenavigationbar", "yes"); //$NON-NLS-1$ //$NON-NLS-2$
			}
			else {
				optionsProperties.put("includenavigationbar", "no"); //$NON-NLS-1$ //$NON-NLS-2$
			}

			if (bIncludeViews == true) {
				optionsProperties.put("includeviews", "yes"); //$NON-NLS-1$ //$NON-NLS-2$
			}
			else {
				optionsProperties.put("includeviews", "no"); //$NON-NLS-1$ //$NON-NLS-2$
			}

			if (bIncludeTags == true) {
				optionsProperties.put("includetags", "yes"); //$NON-NLS-1$ //$NON-NLS-2$
			}
			else {
				optionsProperties.put("includetags", "no"); //$NON-NLS-1$ //$NON-NLS-2$
			}

			if (bInlineView == true) {
				optionsProperties.put("inlineview", "yes"); //$NON-NLS-1$ //$NON-NLS-2$
			}
			else {
				optionsProperties.put("inlineview", "no"); //$NON-NLS-1$ //$NON-NLS-2$
			}

			if (bNewView == true) {
				optionsProperties.put("newview", "yes"); //$NON-NLS-1$ //$NON-NLS-2$
			}
			else {
				optionsProperties.put("newview", "no"); //$NON-NLS-1$ //$NON-NLS-2$
			}

			if (bOpenAfter == true) {
				optionsProperties.put("openafter", "yes"); //$NON-NLS-1$ //$NON-NLS-2$
			}
			else {
				optionsProperties.put("openafter", "no"); //$NON-NLS-1$ //$NON-NLS-2$
			}

			if (bOptimizeForWord == true) {
				optionsProperties.put("optimizeforword", "yes"); //$NON-NLS-1$ //$NON-NLS-2$
			}
			else {
				optionsProperties.put("optimizeforword", "no"); //$NON-NLS-1$ //$NON-NLS-2$
			}

			optionsProperties.store(new FileOutputStream(EXPORT_OPTIONS_FILE_NAME), "Export Options"); //$NON-NLS-1$
		}
		catch (IOException e) {
			ProjectCompendium.APP.displayError(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIExportDialog.ioError")); //$NON-NLS-1$
		}
	}
	
	/**
	 * Handle the close action. Closes the export dialog.
	 */
	public void onCancel() {
		onCancel(false);
	}

	/**
	 * Handle the close action. Saves the current setting and closes the export dialog.
	 */
	public void onCancel(boolean successful) {

		if (viewsDialog != null)
			viewsDialog.dispose();

		setVisible(false);

		dispose();

		if (fileName != null && successful && !bOpenAfter) {
			ProjectCompendium.APP.displayMessage(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIExportDialog.finishedExportingInto") + fileName, LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIExportDialog.exportFinished")); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}
}