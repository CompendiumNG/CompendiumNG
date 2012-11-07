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

import java.sql.SQLException;
import java.util.*;
import java.io.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;
import javax.swing.border.*;

import com.compendium.core.*;
import com.compendium.core.datamodel.*;
import com.compendium.core.db.DBSearch;

import com.compendium.*;
import com.compendium.ui.*;

/**
 * Search Dialog
 *
 * @author	Mohammed S Ali / Michelle Bachler
 * 
 * 11/07 M.Begeman - Bug fix - Before/After fields for Date Modified fields were reversed
 */
public class UISearchDialog extends UIDialog implements ActionListener {

	/** Indicates the search in all views.*/
	private	final static int ALLVIEWS		= 1;

	/** Indicates to search on the current view only.*/
	private	final static int CURRENTVIEW	= 2;

	/** Indicates to search on all views inlcuding deleted views.*/
	private	final static int ALLVIEWSANDDEL	= 3;


	// WILL BE USED TO SAVE SEARCH SETTINGS

	/** NOT CURRENTLY USED.*/
	private static	int				iSavedContext		= 1; //ALLVIEWS;	//views

	/** NOT CURRENTLY USED.*/
	private static	String			sSavedCDateAfter	= "";	//dates //$NON-NLS-1$

	/** NOT CURRENTLY USED.*/
	private static	String			sSavedCDateBefore 	= ""; //$NON-NLS-1$

	/** NOT CURRENTLY USED.*/
	private static	String			sSavedMDateAfter	= ""; //$NON-NLS-1$

	/** NOT CURRENTLY USED.*/
	private static	String			sSavedMDateBefore	= ""; //$NON-NLS-1$

	/** NOT CURRENTLY USED.*/
	private static	Vector			vSavedNodeTypes		= new Vector(10);//nodetypes

	/** NOT CURRENTLY USED.*/
	private static	Vector			vSavedAuthors		= new Vector(10);//authors

	/** NOT CURRENTLY USED.*/
	private static	Vector			vSavedCodes			= new Vector(10);//codes

	/** NOT CURRENTLY USED.*/
	private static	int[]			iSavedNodeTypes		= null;//nodetypes

	/** NOT CURRENTLY USED.*/
	private static	int[]			iSavedAuthors		= null;//authors

	/** NOT CURRENTLY USED.*/
	private static	int[]			iSavedCodes			= null;//codes

	/** NOT CURRENTLY USED.*/
	private static	int				iSavedMatchCodes	= DBSearch.MATCH_ANY;

	/** NOT CURRENTLY USED.*/
	private	static	String			sSavedKeywords		= "";		//keywords //$NON-NLS-1$

	/** NOT CURRENTLY USED.*/
	private static	int				iSavedMatchKeywords	= DBSearch.MATCH_ALL;

	/** NOT CURRENTLY USED.*/
	private static boolean			bSavedLookInLabel	= true;

	/** NOT CURRENTLY USED.*/
	private static boolean			bSavedLookInDetail	= false;



	/** The maon content pane for the dialog.*/
	private Container				oContentPane		= null;

	/** The current view.*/
	private com.compendium.core.datamodel.View	oView	= null;

	/** The parent frame for this dialog.*/
	private JFrame			oParent 			= null;

	/** The tabbed pane holding the various search options.*/
	private JTabbedPane		TabbedPane			= null;

	/** The field for entering the creation date after which to search.*/
	private JTextField		txtCreatedAfter		= null;

	/** The field for entering the modification date after which to search.*/
	private JTextField		txtModifiedAfter	= null;

	/** The field for entering the creation date before which to search.*/
	private JTextField		txtCreatedBefore	= null;

	/** The field for entering the creation date before which to search.*/
	private JTextField		txtModifiedBefore	= null;

	/** The field to enter the keywords to search on.*/
	private JTextField		txtKeyword			= null;

	/** The list for the codes (Tags) to search on.*/
	private UINavList		lstCodes			= null;

	/** The list for the authors to search on.*/
	private UINavList		lstAuthors			= null;

	/** The list of node types to search on.*/
	private UINavList		lstNodeTypes		= null;

	/** The button to run the search.*/
	private UIButton		pbOK				= null;

	/** The button to cancel the dialog.*/
	private UIButton		pbCancel			= null;

	/** Activates the help opeing to the appropriate section.*/
	private UIButton		pbHelp				= null;

	/** Select to search on the current view only.*/
	private JRadioButton	rbCurrentView		= null;

	/** Select to search on all views.*/
	private JRadioButton	rbAllView			= null;

	/** Select to search on all view including deleted ones.*/
	private JRadioButton	rbAllViewAndDeleted = null;

	/** Select to search on all entered keywords.*/
	private JRadioButton	rbMatchAll			= null;

	/** Select to search on any entered keywords.*/
	private JRadioButton	rbMatchAny			= null;

	/** Select to search on all selected code.*/
	private JRadioButton	rbMatchAllCodes		= null;

	/** Select to search on any of the select codes.*/
	private JRadioButton	rbMatchAnyCodes		= null;

	/** Select to search for keywords in the label fiedl.*/
	private JCheckBox		cbLabel				= null;

	/** Select to search for keywords on the details field.*/
	private JCheckBox		cbDetail 			= null;

	/** The list of selected node types to search on.*/
	private int []			typeSelected		=	new int[15];

	/** Holds the indicator to which views to search on.*/
	private String			sContextCondition 	= DBSearch.CONTEXT_ALLVIEWS;

	/** Holds whether to search on all or any of the selected codes.*/
	private int				iMatchCodesCondition = DBSearch.MATCH_ANY;

	/** Indicates whether to search on all or any entered keywords.*/
	private int				iMatchKeywordCondition = DBSearch.MATCH_ANY;

	/** Holds whether to search on the node label and or detail.*/
	private Vector			vtAttrib				= new Vector(2);

	/** Holds the list of selected codes(Tags) to search on.*/
	private Vector			vtCodes				= new Vector();

	/** Holds the list of selected author to search on.*/
	private Vector			vtAuthors			= new Vector();

	/** Holds the list of selected not types to search on.*/
	private Vector			vtNodeTypes			= new Vector(20);

	/** Is it the first time that the tag/node tab has been selected.*/
	private boolean 		isFirstTime			= true;


	/**
	 * Constructor. Initializes and draws this dialog.
	 * @param parent, the parent view for this dialog.
	 * @param view com.compendium.core.datamodel.View, the current view.
	 */
	public UISearchDialog(JFrame parent, com.compendium.core.datamodel.View view) {

		super(parent, true);

		oParent = parent;
		oView = view;

		setTitle(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UISearchDialog.searchTitle")); //$NON-NLS-1$

		oContentPane = getContentPane();
		oContentPane.setLayout(new BorderLayout());

		TabbedPane = new JTabbedPane();
		TabbedPane.add(createKeywordPanel(), LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UISearchDialog.mainTab")); //$NON-NLS-1$
		TabbedPane.add(createTypeTagsPanel(), LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UISearchDialog.typeTab")); //$NON-NLS-1$
		TabbedPane.add(createDateAuthorPanel(), LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UISearchDialog.dateTab")); //$NON-NLS-1$

		TabbedPane.addFocusListener( new FocusListener() {
        	public void focusGained(FocusEvent e) {
				txtKeyword.requestFocus();
			}
            public void focusLost(FocusEvent e) {

			}
		});

		TabbedPane.addChangeListener( new ChangeListener() {
        	public void stateChanged(ChangeEvent e) {
				int nIndex = TabbedPane.getSelectedIndex();
				if (nIndex == 0)
					txtKeyword.requestFocus();

				else if (nIndex == 1) {
					if (isFirstTime) {
						//This currently does not include Shortcut nodes.
						// If you want it to search all node types, select nothing.
						/*lstNodeTypes.requestFocus();

						int[] sels = {0,1,2,3,4,5,6,7,8,9};
						lstNodeTypes.setSelectedIndices(sels);

						//int size = lstCodes.getModel().getSize();
						//int[] sels2 = new int[size];
						//for (int i=0; i<size; i++) {
						//	sels2[i] = i;
						//}
						//lstCodes.setSelectedIndices(sels2);
						 
						isFirstTime = false;*/
					}
				}
				else if (nIndex == 2)
					txtCreatedAfter.requestFocus();
			}
		});

		JPanel buttonpanel = createButtonPanel();

		oContentPane.add(TabbedPane, BorderLayout.CENTER);
		oContentPane.add(buttonpanel, BorderLayout.SOUTH);

		pack();
		setResizable(false);

		initNodeTypesList();
		initCodesList();
		initAuthorsList();

		if (vSavedAuthors.isEmpty())
			lstAuthors.clearSelection();

		if (vSavedCodes.isEmpty())
			lstCodes.clearSelection();

		if (vSavedNodeTypes.isEmpty())
			lstNodeTypes.clearSelection();
	}

	/**
	 * Create the panel of buttons.
	 */
	private UIButtonPanel createButtonPanel() {

		UIButtonPanel panel = new UIButtonPanel();

		pbOK = new UIButton(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UISearchDialog.okButton")); //$NON-NLS-1$
		pbOK.setMnemonic(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UISearchDialog.okButtonMnemonic").charAt(0)); //$NON-NLS-1$
		pbOK.addActionListener(this);
		getRootPane().setDefaultButton(pbOK);
		panel.addButton(pbOK);

		pbCancel = new UIButton(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UISearchDialog.cancelButton")); //$NON-NLS-1$
		pbCancel.setMnemonic(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UISearchDialog.cancelButtonMnemonic").charAt(0)); //$NON-NLS-1$
		pbCancel.addActionListener(this);
		panel.addButton(pbCancel);

		pbHelp = new UIButton(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UISearchDialog.helpButton")); //$NON-NLS-1$
		ProjectCompendium.APP.mainHB.enableHelpOnButton(pbHelp, "basics.search", ProjectCompendium.APP.mainHS); //$NON-NLS-1$
		pbHelp.setMnemonic(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UISearchDialog.helpButtonMnemonic").charAt(0)); //$NON-NLS-1$
		panel.addHelpButton(pbHelp);

		return panel;
	}

	/**
	 * Create the main panle with keyword and view options.
	 */
	public JPanel createKeywordPanel() {

		JPanel panel = new JPanel();
		GridBagLayout gb = new GridBagLayout();
		panel.setLayout(gb);

		GridBagConstraints gc = new GridBagConstraints();
		gc.insets = new Insets(5,5,5,5);
		gc.anchor = GridBagConstraints.WEST;
		gc.gridwidth=2;

		int y=0;

		// Keywords Text field
		JLabel lblLabel = new JLabel(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UISearchDialog.keyWords")+":"); //$NON-NLS-1$
		lblLabel.setFont(new Font("ARIAL", Font.BOLD, 12)); //$NON-NLS-1$
		gc.gridy=y;
		y++;
		gb.setConstraints(lblLabel, gc);
		panel.add(lblLabel);

		txtKeyword = new JTextField(sSavedKeywords);
		txtKeyword.setColumns(30);
		txtKeyword.setMargin(new Insets(2,2,2,2));
		gc.gridy=y;
		y++;
		gb.setConstraints(txtKeyword, gc);
		panel.add(txtKeyword);

		//Radio button for Matches
		rbMatchAny = new JRadioButton(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UISearchDialog.matchAny")); //$NON-NLS-1$
		gc.gridy=y;
		gc.gridx=0;
		gc.gridwidth=1;
		gb.setConstraints(rbMatchAny, gc);
		panel.add(rbMatchAny);
		if (iSavedMatchKeywords == DBSearch.MATCH_ANY)
			rbMatchAny.setSelected(true);

		rbMatchAll = new JRadioButton(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UISearchDialog.matchAll")); //$NON-NLS-1$
		gc.gridy=y;
		gc.gridx=1;
		y++;
		gb.setConstraints(rbMatchAll, gc);
		panel.add(rbMatchAll);
		if (iSavedMatchKeywords == DBSearch.MATCH_ALL)
			rbMatchAll.setSelected(true);

		ButtonGroup rgGroup = new ButtonGroup();
		rgGroup.add(rbMatchAny);
		rgGroup.add(rbMatchAll);

		//Check boxes for label and detail search
		lblLabel = new JLabel(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UISearchDialog.lookIn")+":"); //$NON-NLS-1$
		gc.gridwidth=2;
		gc.gridx=0;
		gc.gridy=y;
		y++;
		gb.setConstraints(lblLabel, gc);
		panel.add(lblLabel);

		cbLabel = new JCheckBox(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UISearchDialog.label")); //$NON-NLS-1$
		gc.gridy=y;
		gc.gridx=0;
		gc.gridwidth=1;
		gb.setConstraints(cbLabel, gc);
		panel.add(cbLabel);

		if (bSavedLookInLabel)
			cbLabel.setSelected(true);

		cbDetail = new JCheckBox(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UISearchDialog.detail")); //$NON-NLS-1$
		gc.gridy=y;
		gc.gridx=1;
		y++;
		gb.setConstraints(cbDetail, gc);
		panel.add(cbDetail);
		if (bSavedLookInDetail)
			cbDetail.setSelected(true);

		JSeparator sep = new JSeparator();
		gc.gridy=y;
		gc.gridx=0;
		gc.gridwidth=2;
		gc.fill = GridBagConstraints.BOTH;
		y++;
		gb.setConstraints(sep, gc);
		panel.add(sep);

		//Add Context Option
		lblLabel = new JLabel(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UISearchDialog.context")+":"); //$NON-NLS-1$
		lblLabel.setFont(new Font("ARIAL", Font.BOLD, 12)); //$NON-NLS-1$
		gc.gridy=y;
		gc.gridx=0;
		y++;
		gb.setConstraints(lblLabel, gc);
		panel.add(lblLabel);

		rbCurrentView = new JRadioButton(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UISearchDialog.currentView") + oView.getLabel()); //$NON-NLS-1$
		gc.gridy=y;
		y++;
		gb.setConstraints(rbCurrentView, gc);
		panel.add(rbCurrentView);
		if (iSavedContext == CURRENTVIEW)
			rbCurrentView.setSelected(true);

		rbAllView = new JRadioButton(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UISearchDialog.allViews")); //$NON-NLS-1$
		gc.gridy=y;
		y++;
		gb.setConstraints(rbAllView, gc);
		panel.add(rbAllView);
		if (iSavedContext == ALLVIEWS)
			rbAllView.setSelected(true);

		rbAllViewAndDeleted = new JRadioButton(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UISearchDialog.allViewsDeleted")); //$NON-NLS-1$
		gc.gridy=y;
		gb.setConstraints(rbAllViewAndDeleted, gc);
		panel.add(rbAllViewAndDeleted);
		if (iSavedContext == ALLVIEWSANDDEL)
			rbAllViewAndDeleted.setSelected(true);

		rgGroup = new ButtonGroup();
		rgGroup.add(rbAllView);
		rgGroup.add(rbCurrentView);
		rgGroup.add(rbAllViewAndDeleted);

		return panel;
	}

	/**
	 * Create the options for date and author filtering for the search.
	 */
	public JPanel createDateAuthorPanel() {

		JPanel panel = new JPanel();
		panel.setBorder(new EmptyBorder(3,3,3,3));
		GridBagLayout gb = new GridBagLayout();
		panel.setLayout(gb);

		GridBagConstraints gc = new GridBagConstraints();
		gc.insets = new Insets(5,5,5,5);
		gc.anchor = GridBagConstraints.WEST;
		gc.gridwidth=2;

		int y=0;

		// Search dates
		JLabel lblLabel = new JLabel(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UISearchDialog.dateCreated")+":"); //$NON-NLS-1$
		lblLabel.setFont(new Font("ARIAL", Font.BOLD, 12)); //$NON-NLS-1$
		gc.gridy=y;
		y++;
		gb.setConstraints(lblLabel, gc);
		panel.add(lblLabel);

		lblLabel = new JLabel(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UISearchDialog.after")+":"); //$NON-NLS-1$
		lblLabel.setFont(new Font("ARIAL", Font.ITALIC, 10)); //$NON-NLS-1$
		gc.gridwidth=1;
		gc.gridy=y;
		gc.gridx=0;
		gb.setConstraints(lblLabel, gc);
		panel.add(lblLabel);

		lblLabel = new JLabel(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UISearchDialog.before")+":"); //$NON-NLS-1$
		lblLabel.setFont(new Font("ARIAL", Font.ITALIC, 10)); //$NON-NLS-1$
		gc.gridy=y;
		gc.gridx=1;
		y++;
		gb.setConstraints(lblLabel, gc);
		panel.add(lblLabel);

		txtCreatedAfter = new JTextField(sSavedCDateAfter);
		txtCreatedAfter.setColumns(15);
		txtCreatedAfter.setMargin(new Insets(2,2,2,2));
		gc.gridy=y;
		gc.gridx=0;
		gb.setConstraints(txtCreatedAfter, gc);
		panel.add(txtCreatedAfter);

		//remove binding of the enter key - so the enter key defaults to the OK button
		// this will do it for all the text fields in this dialog box.
		KeyStroke enter = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);
		Keymap map = txtCreatedAfter.getKeymap();
		map.removeKeyStrokeBinding(enter);

		txtCreatedBefore = new JTextField(sSavedCDateBefore);
		txtCreatedBefore.setColumns(15);
		txtCreatedBefore.setMargin(new Insets(2,2,2,2));
		gc.gridy=y;
		gc.gridx=1;
		y++;
		gb.setConstraints(txtCreatedBefore, gc);
		panel.add(txtCreatedBefore);

		lblLabel = new JLabel(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UISearchDialog.dateModified")+":"); //$NON-NLS-1$
		lblLabel.setFont(new Font("ARIAL", Font.BOLD, 12)); //$NON-NLS-1$
		gc.gridwidth=2;
		gc.gridy=y;
		gc.gridx=0;
		y++;
		gb.setConstraints(lblLabel, gc);
		panel.add(lblLabel);

		lblLabel = new JLabel(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UISearchDialog.after")); //$NON-NLS-1$
		lblLabel.setFont(new Font("ARIAL", Font.ITALIC, 10)); //$NON-NLS-1$
		gc.gridwidth=1;
		gc.gridy=y;
		gc.gridx=0;
		gb.setConstraints(lblLabel, gc);
		panel.add(lblLabel);

		lblLabel = new JLabel(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UISearchDialog.before")); //$NON-NLS-1$
		lblLabel.setFont(new Font("ARIAL", Font.ITALIC, 10)); //$NON-NLS-1$
		gc.gridy=y;
		gc.gridx=1;
		y++;
		gb.setConstraints(lblLabel, gc);
		panel.add(lblLabel);

		txtModifiedAfter = new JTextField(sSavedMDateAfter);
		txtModifiedAfter.setColumns(15);
		txtModifiedAfter.setMargin(new Insets(2,2,2,2));
		gc.gridy=y;
		gc.gridx=0;
		gb.setConstraints(txtModifiedAfter, gc);
		panel.add(txtModifiedAfter);

		txtModifiedBefore = new JTextField(sSavedMDateBefore);
		txtModifiedBefore.setColumns(15);
		txtModifiedBefore.setMargin(new Insets(2,2,2,2));
		gc.gridy=y;
		gc.gridx=1;
		y++;
		gb.setConstraints(txtModifiedBefore, gc);
		panel.add(txtModifiedBefore);

		// Author List
		lblLabel = new JLabel(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UISearchDialog.author")+":"); //$NON-NLS-1$
		lblLabel.setFont(new Font("ARIAL", Font.BOLD, 12)); //$NON-NLS-1$
		gc.gridwidth=2;
		gc.gridy=y;
		gc.gridx=0;
		y++;
		gb.setConstraints(lblLabel, gc);
		panel.add(lblLabel);

		// Create the list
		lstAuthors = new UINavList(new DefaultListModel());
		lstAuthors.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		lstAuthors.setSelectionModel(new ToggleSelectionModel());
		lstAuthors.setCellRenderer(new LabelListCellRenderer());
		lstAuthors.setBackground(Color.white);

		JScrollPane sp2 = new JScrollPane(lstAuthors);
		sp2.setPreferredSize(new Dimension(250,120));
		gc.gridy=y;
		gb.setConstraints(sp2, gc);
		panel.add(sp2);

		return panel;
	}

	/**
	 * Create the panel with node type and tag filtering options.
	 */
	public JPanel createTypeTagsPanel() {

		JPanel panel = new JPanel();
		panel.setBorder(new EmptyBorder(3,3,3,3));
		GridBagLayout gb = new GridBagLayout();
		panel.setLayout(gb);

		GridBagConstraints gc = new GridBagConstraints();
		gc.insets = new Insets(5,5,5,5);
		gc.anchor = GridBagConstraints.WEST;
		gc.gridwidth=2;

		int y=0;

		//Node Type List
		JLabel lblLabel = new JLabel(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UISearchDialog.nodeType")); //$NON-NLS-1$
		lblLabel.setFont(new Font("ARIAL", Font.BOLD, 12)); //$NON-NLS-1$
		gc.gridy=y;
		y++;
		gb.setConstraints(lblLabel, gc);
		panel.add(lblLabel);

		// Create the list
		lstNodeTypes = new UINavList(new DefaultListModel());
		lstNodeTypes.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		lstNodeTypes.setSelectionModel(new ToggleSelectionModel());
		lstNodeTypes.setCellRenderer(new LabelListCellRenderer());
		lstNodeTypes.setBackground(Color.white);

		JScrollPane sp1 = new JScrollPane(lstNodeTypes);
		sp1.setPreferredSize(new Dimension(245,100));
		gc.gridy=y;
		y++;
		gb.setConstraints(sp1, gc);
		panel.add(sp1);

		// Codes List
		lblLabel = new JLabel(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UISearchDialog.tags")+":"); //$NON-NLS-1$
		lblLabel.setFont(new Font("ARIAL", Font.BOLD, 12)); //$NON-NLS-1$
		gc.gridy=y;
		y++;
		gb.setConstraints(lblLabel, gc);
		panel.add(lblLabel);

		lstCodes = new UINavList(new DefaultListModel());
		lstCodes.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION );
		lstCodes.setSelectionModel(new ToggleSelectionModel());
		lstCodes.setCellRenderer(new LabelListCellRenderer());
		lstCodes.setBackground(Color.white);

		JScrollPane sp3 = new JScrollPane(lstCodes);
		sp3.setPreferredSize(new Dimension(245,100));
		gc.gridy=y;
		y++;
		gb.setConstraints(sp3, gc);
		panel.add(sp3);

		//Radio button for Matches
		rbMatchAnyCodes = new JRadioButton(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UISearchDialog.matchAnyTags")); //$NON-NLS-1$
		gc.gridy=y;
		gc.gridx = 0;
		gc.gridwidth=1;
		gb.setConstraints(rbMatchAnyCodes, gc);
		panel.add(rbMatchAnyCodes);
		if (iSavedMatchCodes == DBSearch.MATCH_ANY)
			rbMatchAnyCodes.setSelected(true);

		rbMatchAllCodes = new JRadioButton(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UISearchDialog.matchAllTags")); //$NON-NLS-1$
		gc.gridy=y;
		gc.gridx=1;
		y++;
		gb.setConstraints(rbMatchAllCodes, gc);
		panel.add(rbMatchAllCodes);
		if (iSavedMatchCodes == DBSearch.MATCH_ALL)
			rbMatchAllCodes.setSelected(true);

		ButtonGroup rgGroup = new ButtonGroup();
		rgGroup.add(rbMatchAnyCodes);
		rgGroup.add(rbMatchAllCodes);

		return panel;
	}

	/**
	 * Check the given keyword String and clean for SQL use.
	 * @param keywords, the words to check and clean.
	 */
	private static Vector parseKeywords(String keywords) {

		Vector vKeywords = new Vector(10);

		StringTokenizer st = new StringTokenizer(keywords, ", ;\t\n\r\f"); //$NON-NLS-1$

		while (st.hasMoreTokens()) {
			String token = (String)st.nextToken();

			// CLEAN TO ESCAPE SPECHMARKS ETC..
			token = CoreUtilities.cleanSQLText(token, FormatProperties.nDatabaseType);

			if(!vKeywords.contains(token))
				vKeywords.addElement(token);

		}
		return (vKeywords);
	}

	/**
	 * Convert the given date string into a Date object.
	 * @param sDate, the date string to convert.
	 */
	private Date convertDate(String sDate) throws IOException {

		if (sDate.equals("")) //$NON-NLS-1$
			return (null);

		Calendar cal = Calendar.getInstance();
		StringTokenizer st = null;

		if (sDate.indexOf(".") != -1) { //$NON-NLS-1$
			st = new StringTokenizer(sDate, ".-"); //$NON-NLS-1$
		}
		else {
			st = new StringTokenizer(sDate, "/-"); //$NON-NLS-1$
		}

		String year = "", month = "", day = ""; int count = 0; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		while(st.hasMoreTokens()) {
			String token = (String)st.nextToken();
			if(count == 0)
				if ((token.length() == 2) || (token.length() == 1))
					month = token;
				else {
					IOException e = new IOException(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UISearchDialog.error1")+": " + sDate); //$NON-NLS-1$
					throw (e);
				}
			else
				if(count == 1) {
					if ((token.length() == 2) || (token.length() == 1))
				  		day = token;
				else {
					IOException e = new IOException(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UISearchDialog.error2")+": "+ sDate); //$NON-NLS-1$
					throw (e);
				}
			}
			else {
				if(count == 2) {
					if (token.length() == 4)
						year = token;
					else {
						IOException e = new IOException(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UISearchDialog.error3")+": "+ sDate); //$NON-NLS-1$
						throw (e);
					}
				}
			}
			count++;
		}

		if (count != 3) {
			IOException e = new IOException(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UISearchDialog.error4")+": "+ sDate); //$NON-NLS-1$
			throw (e);
		}

		// remember that months are zero based so need to subtract one
		int imonth = new Integer(month).intValue()-1;
		int iday = new Integer(day).intValue();
		int iyear = new Integer(year).intValue();
		if ((imonth+1 <1) || (imonth+1>12)) {
			IOException e = new IOException(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UISearchDialog.error5")+": "+ sDate); //$NON-NLS-1$
			throw (e);
		}
		if ((iday < 1) || (iday>31)) {
			IOException e = new IOException(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UISearchDialog.error6")+": "+ sDate); //$NON-NLS-1$
			throw (e);
		}
		cal.set(iyear, imonth, iday, 0, 0, 0);
		java.util.Date d = cal.getTime();
		return (d);
	}

	/**
	 * Handle the button push events.
	 * @param evt, the assoicated ActionEvent object.
	 */
	public void actionPerformed(ActionEvent evt) {

		Object source = evt.getSource();
		if (source == pbCancel)
			onCancel();
		else if (source == pbOK)
			onOK();
	}

	/**
	 * Run the search based on the entered data.
	 */
	public void onOK() {

		UIViewFrame activeFrame = ProjectCompendium.APP.getCurrentFrame();
		ProjectCompendium.APP.setWaitCursor();
		activeFrame.setCursor(new Cursor(java.awt.Cursor.WAIT_CURSOR));
		this.setCursor(new Cursor(java.awt.Cursor.WAIT_CURSOR));
		Boolean bDoQuery = false;

		Vector vtSelectedCodes = new Vector(10);
		Vector vtSelectedAuthors = new Vector(10);
		Vector vtSelectedNodeTypes = new Vector(10);
		java.util.Date beforeCreationDate, afterCreationDate, beforeModificationDate, afterModificationDate;

		String sViewId = ""; //$NON-NLS-1$

		//Get view to Search - sContextCondition
		if(rbCurrentView.isSelected()) {
			sContextCondition = DBSearch.CONTEXT_SINGLE_VIEW;
			sViewId = oView.getId();
		}
		else {
			if(rbAllView.isSelected()) {
				sContextCondition = DBSearch.CONTEXT_ALLVIEWS;
			}
			else {
				if(rbAllViewAndDeleted.isSelected())
					sContextCondition = DBSearch.CONTEXT_ALLVIEWS_AND_DELETEDOBJECTS;
			}
		}

		//dates
		String sDateField = ""; //$NON-NLS-1$
		try {
			sDateField = LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UISearchDialog.dateCreatedBefore"); //$NON-NLS-1$
			beforeCreationDate = convertDate(txtCreatedBefore.getText());
			sDateField = LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UISearchDialog.dateCreatedAfter"); //$NON-NLS-1$
			afterCreationDate = convertDate(txtCreatedAfter.getText());
			sDateField = LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UISearchDialog.dateModifiedBefore"); //$NON-NLS-1$
			beforeModificationDate = convertDate(txtModifiedBefore.getText());
			sDateField = LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UISearchDialog.dateModifiedAfter"); //$NON-NLS-1$
			afterModificationDate = convertDate(txtModifiedAfter.getText());

		}
		catch(IOException ex) {
			//popup the error message
			String message = ex.toString();
			StringTokenizer st = new StringTokenizer(message, ":"); //$NON-NLS-1$
			st.nextElement();//skip the "java.io.Exception msg"
			message = (String)st.nextElement();
			message = message + LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UISearchDialog.inField") + sDateField; //$NON-NLS-1$
			JOptionPane oOptionPane = new JOptionPane(message);
			JDialog oDialog = oOptionPane.createDialog(oContentPane,LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UISearchDialog.searchErrorTitle")); //$NON-NLS-1$
			oDialog.setModal(true);
			oDialog.setVisible(true);

			this.setCursor(Cursor.getDefaultCursor());
			activeFrame.setCursor(Cursor.getDefaultCursor());
			ProjectCompendium.APP.setDefaultCursor();

			return;
		}

		// get selected Node Types - vtSelectedNodeTypes
		int [] selected = lstNodeTypes.getSelectedIndices();

		for(int i=0;i<selected.length;i++) {
			int type = UINodeTypeManager.convertStringToNodeType((String)vtNodeTypes.elementAt(selected[i]));
			String sType = (new Integer(type)).toString();
			vtSelectedNodeTypes.addElement((String) sType);
		}

		// get selected Authors - vtSelectedAuthors
		selected = lstAuthors.getSelectedIndices();

		for(int i=0;i<selected.length;i++) {
			vtSelectedAuthors.addElement((UserProfile)vtAuthors.elementAt(selected[i]));
		}

		// get selected Codes - vtSelectedCodes
		selected = lstCodes.getSelectedIndices();

		for(int i=0;i<selected.length;i++) {
			vtSelectedCodes.addElement((Code)vtCodes.elementAt(selected[i]));
		}

		//do this requirement later - bz
		//vSavedCodes= vtSelectedCodes;

		//iMatchCodesCondition
		if(rbMatchAnyCodes.isSelected())
			iMatchCodesCondition = DBSearch.MATCH_ANY;
		else if(rbMatchAllCodes.isSelected())
			iMatchCodesCondition = DBSearch.MATCH_ALL;

		//iMatchKeywordCondition
		if(rbMatchAny.isSelected())
			iMatchKeywordCondition = DBSearch.MATCH_ANY;
		else if(rbMatchAll.isSelected())
			iMatchKeywordCondition = DBSearch.MATCH_ALL;

		vtAttrib.removeAllElements();
		if(cbLabel.isSelected())
			vtAttrib.addElement("Node.Label"); //$NON-NLS-1$

		if(cbDetail.isSelected()) {
			vtAttrib.addElement("Node.Detail"); //$NON-NLS-1$
			vtAttrib.addElement("NodeDetail.Detail"); //$NON-NLS-1$
		}
		
		IModel model = ProjectCompendium.APP.getModel();
		PCSession session = model.getSession();
		String author = ""; //$NON-NLS-1$
		
		Vector vKeywords = parseKeywords(txtKeyword.getText());
		Vector vtNodes = new Vector(51);
		
		bDoQuery = ((sContextCondition != "contextAllViews") ||		// Check to prevent accidental whole-database search //$NON-NLS-1$
				(sViewId != "") || //$NON-NLS-1$
				(vtSelectedNodeTypes.size() > 0) ||
				(vtSelectedAuthors.size() > 0) ||
				(vtSelectedCodes.size() > 0) ||
				(vKeywords.size() > 0) ||
				(iMatchCodesCondition != 0) ||
				(iMatchKeywordCondition != 0) ||
				(beforeCreationDate != null) ||
				(afterCreationDate != null) ||
				(beforeModificationDate != null) ||
				(afterModificationDate != null));
		if (!bDoQuery) {
			int answer = JOptionPane.showConfirmDialog(this, LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UISearchDialog.noParameters")+"\n", LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UISearchDialog.noParametersTitle"), //$NON-NLS-1$ //$NON-NLS-2$
						JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
			if (answer == JOptionPane.YES_OPTION) {
				bDoQuery = true;
			}
		}
		
		if (bDoQuery) {
			try {
				vtNodes = model.getQueryService().searchNode(session,
															   sContextCondition,
															   sViewId,
															   vtSelectedNodeTypes,
															   vtSelectedAuthors,
															   vtSelectedCodes,
															   iMatchCodesCondition,
															   vKeywords,
															   iMatchKeywordCondition,
															   vtAttrib,
															   beforeCreationDate, afterCreationDate,
															   beforeModificationDate,
															   afterModificationDate
															   );
	
				//close this window only when the search results in nodes
				if(vtNodes.size() > 0) {
					setVisible(false);
	
					this.setCursor(Cursor.getDefaultCursor());
					activeFrame.setCursor(Cursor.getDefaultCursor());
					ProjectCompendium.APP.setDefaultCursor();
	
					UISearchResultDialog dlgResult = new UISearchResultDialog(oParent, this, vtNodes);
					UIUtilities.centerComponent(dlgResult, ProjectCompendium.APP);
					dlgResult.setVisible(true);
				}
				else {
					ProjectCompendium.APP.displayMessage(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UISearchDialog.noResultsA")+"\n\n"+
							LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UISearchDialog.noResultsB")+"\n", LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UISearchDialog.noResultsTitle")); //$NON-NLS-1$ //$NON-NLS-2$
				}
			}
			catch(SQLException ex) {
				ex.printStackTrace();
				ProjectCompendium.APP.displayError("Exception:" + ex.getMessage()); //$NON-NLS-1$
			}
		}
		this.setCursor(Cursor.getDefaultCursor());
		activeFrame.setCursor(Cursor.getDefaultCursor());
		ProjectCompendium.APP.setDefaultCursor();
	}

	/**
	 * Initalize the data for the list of authors.
	 */
	private void initAuthorsList() {

		((DefaultListModel)lstAuthors.getModel()).removeAllElements();
		vtAuthors.removeAllElements();

		IModel model = ProjectCompendium.APP.getModel();
		String modelName = model.getModelName();
		String userID = model.getUserProfile().getId() ;
//
// Following code changed to get user list from cache instead of the database
//		try {
//			for(Enumeration e = (model.getUserService().getUsers(modelName, userID)).elements();e.hasMoreElements();) {
			for(Enumeration e = (ProjectCompendium.APP.getModel().getUsers()).elements();e.hasMoreElements();) {
				UserProfile up = (UserProfile)e.nextElement();
				ImageIcon img = null;
				if (up.isActive()) {
					img = UIImages.get(IUIConstants.NEW_ICON);
				} else {
					img = UIImages.get(IUIConstants.INACTIVE_USER_ICON);
				}

				String authorName = up.getUserName();
				String displayText = authorName;
				if (authorName.equals("")) { //$NON-NLS-1$
					continue;
				}

				if(displayText.length() > 40) {
					displayText = displayText.substring(0,39);
					displayText += "...."; //$NON-NLS-1$
				}

				JLabel lblAuthorsList = new JLabel(displayText,img,SwingConstants.LEFT);
				lblAuthorsList.setToolTipText(authorName);
				((DefaultListModel)lstAuthors.getModel()).addElement(lblAuthorsList);
				vtAuthors.addElement(up);
			}
//		}
//		catch(SQLException ex) {
//			ProjectCompendium.APP.displayError("Exception:" + ex.getMessage());
//		}

		lstAuthors.setSelectedIndex(0);
	}

	/**
	 * Initialize the list of node types.
	 */
	private void initNodeTypesList() {

		((DefaultListModel)lstNodeTypes.getModel()).removeAllElements();
		vtNodeTypes.removeAllElements();

		/*
		public static final int			PARENT_SHORTCUT_DISPLACEMENT = 10;
		*/

		for(int i=0;i<UINodeTypeManager.nodeTypeStrings.length;i++)
		{
			String sType = UINodeTypeManager.nodeTypeStrings[i];
			ImageIcon img = UIImages.getNodeIcon(UINodeTypeManager.imgIndex[i]);
			//trim text to fit the label for the timebeing since the label comes out of the scrollbar window

			if(sType.length() > 40)
			{
				sType = sType.substring(0,39);
				sType += "...."; //$NON-NLS-1$
			}

			JLabel lblNodeTypesList = new JLabel(sType,img,SwingConstants.LEFT);
			lblNodeTypesList.setToolTipText(sType);
			//lblNodeTypesList.addMouseListener(this);
			((DefaultListModel)lstNodeTypes.getModel()).addElement(lblNodeTypesList);
			vtNodeTypes.addElement(sType);
		}

		lstNodeTypes.setSelectedIndex(0);
	}

	/**
	 * Initialize the list of codes (tags) to select.
	 */
	private void initCodesList() {

		((DefaultListModel)lstCodes.getModel()).removeAllElements();
		vtCodes.removeAllElements();
		Vector vtCodesSort = new Vector();

		for(Enumeration e = ProjectCompendium.APP.getModel().getCodes();e.hasMoreElements();)
		{
			Code code = (Code)e.nextElement();
			vtCodesSort.addElement(code);
		}
		//sort the vector
		vtCodesSort = CoreUtilities.sortList(vtCodesSort);

		for(Enumeration e = vtCodesSort.elements();e.hasMoreElements();)
		{
			Code code = (Code)e.nextElement();
			ImageIcon img = null;
				img = UIImages.get(IUIConstants.NEW_ICON);

			//trim text to fit the label for the timebeing since the label comes out of the scrollbar window
			String text = code.getName();

			if(text.length() > 40)
			{
				text = text.substring(0,39);
				text += "...."; //$NON-NLS-1$
			}
			JLabel lblCodesList = new JLabel(text,img,SwingConstants.LEFT);
			lblCodesList.setToolTipText(text);
			//lblCodesList.addMouseListener(this);
			((DefaultListModel)lstCodes.getModel()).addElement(lblCodesList);
			vtCodes.addElement(code);
		}

		lstCodes.setSelectedIndex(0);
	}

	/**
	 * NOT CURRENTLY USED.
	 */
	private void resetDefaultSelections() {

		iSavedContext = ALLVIEWS;	//views
		sSavedCDateAfter = "";	//dates //$NON-NLS-1$
		sSavedCDateBefore = ""; //$NON-NLS-1$
		sSavedMDateAfter = ""; //$NON-NLS-1$
		sSavedMDateBefore = ""; //$NON-NLS-1$
		iSavedNodeTypes = null;	//nodetypes
		iSavedAuthors = null;		//authors
		iSavedCodes = null;		//codes
		iSavedMatchCodes = DBSearch.MATCH_ANY;
		sSavedKeywords = "";		//keywords //$NON-NLS-1$
		iSavedMatchKeywords = DBSearch.MATCH_ALL;
		bSavedLookInLabel = true;
		bSavedLookInDetail = false;
	}

	/**
	 * Give the lists used in this dialog a toggle model - LY 12/02/2003.
	 */
	class ToggleSelectionModel extends DefaultListSelectionModel{
  		public void setSelectionInterval(int index0, int index1) {
			if (isSelectedIndex(index0)){
		  		super.removeSelectionInterval(index0, index1);
			}
			else {
	  			super.setSelectionInterval(index0, index1);
			}
  		}
	}
}
