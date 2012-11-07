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
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.table.*;

import com.compendium.core.datamodel.*;
import com.compendium.core.db.*;

import com.compendium.LanguageProperties;
import com.compendium.ProjectCompendium;
import com.compendium.ui.plaf.*;
import com.compendium.ui.*;


/**
 * UIImportDialog defines the import dialog, that allows
 * the user to import a Questmap export file into a Compendium view.
 *
 * @author	Mohammed Sajid Ali / Michelle Bachler
 */
public class UIImportDialog extends UIDialog implements ActionListener, IUIConstants {

	/** The last directory the user selected to import from.*/
	public static String lastFileDialogDir = ProjectCompendium.sHOMEPATH+ProjectCompendium.sFS+"Exports"; //$NON-NLS-1$

	/** The main content pane for this dialog.*/
	private Container				oContentPane = null;

	/** The scrollpane for the list of views.*/
	private JScrollPane				sp		= null;

	/** The counter for the y grid positions for ui elements in the layout.*/
	private int 					gridyStart	= 1;

	/** The button to start the import.*/
	private UIButton				pbImport	= null;

	/** The button to close the dialog without importing.*/
	private UIButton				pbClose	= null;

	/** The button to open the help.*/
	private UIButton				pbHelp	= null;

	/** Select to set the date for imported nodes as today, and the user as the current user.*/
	private JRadioButton			rbNormal	= null;

	/** Select to prserve importing date and user information.*/
	private JRadioButton			rbSmart	= null;

	/** Incluse original dateas and author information in the node detail fields.*/
	private JCheckBox				cbInclude = null;

	/** Select to preserve transclusion when importing.*/
	private JCheckBox				cbTransclude = null;
	
	/** Select to mark all nodes seen /unseen  on import.*/
	private JCheckBox			cbMarkSeen 	= null;

	/** The layout manager used in this dialog.*/
	private	GridBagLayout 			gb = null;

	/** The layout constraint instance used in this dialog.*/
	private	GridBagConstraints 		gc = null;

	/** The View data for the list of views.*/
	private Vector					oViews  = new Vector(51);

	/** The view to import into when it is a map.*/
	private ViewPaneUI				oViewPaneUI = null;

	/** Whether importing into multiple views.*/
	private boolean					showViewList = true;

	/** The table holding the list of views.*/
	private JTable					table = null;

	/** The view to import into when it is a list.*/
	private UIList					uiList = null;

	/** The parent frame for this dialog.*/
	private JFrame					oParent = null;

	/** The main panel*/
	private JPanel 					oCenterPanel	=	null;

	/** The title of this dialog.*/
	private String					sTitle	= LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIImportDialog.importFromQuestmap"); //$NON-NLS-1$

	/**
	 * Initializes and sets up the dialog.
	 * @param parent, the parent frame for this dialog.
	 * @param showViewList, true if importing into multiple views, else false.
	 */
	public UIImportDialog(JFrame parent, boolean showViewList) {

		super(parent, true);
		oParent = parent;

	  	this.showViewList = showViewList;

		setTitle(sTitle);

		oContentPane = getContentPane();
		oContentPane.setLayout(new BorderLayout());
		gb = new GridBagLayout();

		oCenterPanel = new JPanel(gb);

		oContentPane.add(oCenterPanel, BorderLayout.CENTER);

		if (showViewList == false) {
			constructDialogWithoutViewList();
		}
		else {
			gridyStart = 2;
			constructDialogWithViewList();
		}

		// other initializations
		pack();
		setResizable(false);
		return;
	}

	/**
	 * Draw the dialog contents when importing into mutiple views.
	 */
	private void constructDialogWithViewList() {

		GridBagConstraints gc = new GridBagConstraints();
		gc.insets = new Insets(5,5,5,5);

		// Add label
		JLabel lblViews = new JLabel(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIImportDialog.selectViews")+":"); //$NON-NLS-1$
		gc.anchor = GridBagConstraints.WEST;
		gc.gridy = 0;
		gc.gridwidth=GridBagConstraints.REMAINDER;
		gb.setConstraints(lblViews, gc);
		oCenterPanel.add(lblViews);

		ViewListTableModel model = new ViewListTableModel();
		TableSorter sorter = new TableSorter(model);
		table = new JTable(sorter);
		table.getColumn(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIImportDialog.createDate")).setPreferredWidth(25); //$NON-NLS-1$
		table.getColumn(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIImportDialog.modDate")).setPreferredWidth(25); //$NON-NLS-1$
		table.getTableHeader().setReorderingAllowed(false);
		setRenderers();
		sorter.addMouseListenerToHeaderInTable(table);

		JScrollPane sp = new JScrollPane(table);
		sp.setPreferredSize(new Dimension(400,250));
		gc.gridy = 1;
		gc.gridwidth=GridBagConstraints.REMAINDER;
		gb.setConstraints(sp, gc);
		oCenterPanel.add(sp);

		constructDialogWithoutViewList();
	}

	/**
	 * Set the header renderers for the table column headers.
	 */
    public void setRenderers() {
    	int count = table.getModel().getColumnCount();
        for (int i = 0; i < count; i++) {
        	TableColumn aColumn = table.getColumnModel().getColumn(i);
        	UITableHeaderRenderer headerRenderer = new UITableHeaderRenderer();
            aColumn.setHeaderRenderer(headerRenderer);
    	}
 	}

	/**
	 * Draw the dialog conents when importing into a single view.
	 */
	private void constructDialogWithoutViewList() {

		GridBagConstraints gc = new GridBagConstraints();
		gc.insets = new Insets(5,5,5,5);
		//gc.gridwidth=2;

		//get the import profile form the main frame
		Vector profile = ProjectCompendium.APP.getImportProfile();
		boolean normalImport = ((Boolean)profile.elementAt(0)).booleanValue();
		boolean includeInDetail = ((Boolean)profile.elementAt(1)).booleanValue();

		// Add radio button for import profiles (Normal and Smart)

		rbSmart = new JRadioButton(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIImportDialog.importQuestmapAuthorDate")); //$NON-NLS-1$
		rbSmart.setSelected(true);
		rbSmart.addActionListener(this);
		gc.gridy = gridyStart;
		gridyStart ++;
		gc.anchor = GridBagConstraints.WEST;
		gc.gridwidth=GridBagConstraints.REMAINDER;
		gb.setConstraints(rbSmart, gc);
		oCenterPanel.add(rbSmart);

		rbNormal = new JRadioButton(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIImportDialog.setAuthor")+ProjectCompendium.APP.getModel().getUserProfile().getUserName()+" "+LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIImportDialog.allDatesToday")); //$NON-NLS-1$ //$NON-NLS-2$
		rbNormal.setSelected(false);
		rbNormal.addActionListener(this);
		gc.insets = new Insets(5,5,0,5);
		gc.gridy = gridyStart;
		gridyStart ++;
		gb.setConstraints(rbNormal, gc);
		oCenterPanel.add(rbNormal);

		ButtonGroup rgGroup = new ButtonGroup();
		rgGroup.add(rbNormal);
		rgGroup.add(rbSmart);

		cbInclude = new JCheckBox(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIImportDialog.includeAuthorDate")); //$NON-NLS-1$
		cbInclude.setSelected(includeInDetail);
		cbInclude.addActionListener(this);
		cbInclude.setEnabled(false);
		gc.gridy = gridyStart;
		gridyStart++;
		gc.insets = new Insets(0,30,5,5);
		gb.setConstraints(cbInclude, gc);
		oCenterPanel.add(cbInclude);

		cbTransclude = new JCheckBox(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIImportDialog.preserveEmbeds")); //$NON-NLS-1$
		cbTransclude.setSelected(true);
		gc.insets = new Insets(5,5,5,5);
		gc.gridy = gridyStart;
		gridyStart++;
		gb.setConstraints(cbTransclude, gc);
		oCenterPanel.add(cbTransclude);
		
		// flag to mark seen/unseen on import
		cbMarkSeen = new JCheckBox(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIImportDialog.markSeen")); //$NON-NLS-1$
		cbMarkSeen.setSelected(false);
		cbMarkSeen.addActionListener(this);
		
		gc.insets = new Insets(5,5,5,5);
		gc.gridy = gridyStart;
		gridyStart++;
		gb.setConstraints(cbMarkSeen, gc);
		oCenterPanel.add(cbMarkSeen);
		
		// Add spacer label
		JLabel spacer = new JLabel(" "); //$NON-NLS-1$
		gc.gridy = gridyStart;
		gridyStart++;
		gb.setConstraints(spacer, gc);
		oCenterPanel.add(spacer);


		UIButtonPanel oButtonPanel = new UIButtonPanel();

		pbImport = new UIButton(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIImportDialog.importButton")); //$NON-NLS-1$
		pbImport.setMnemonic(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIImportDialog.importButtonMnemonic").charAt(0)); //$NON-NLS-1$
		pbImport.addActionListener(this);
		getRootPane().setDefaultButton(pbImport);
		oButtonPanel.addButton(pbImport);

		pbClose = new UIButton(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIImportDialog.cancelButton")); //$NON-NLS-1$
		pbClose.setMnemonic(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIImportDialog.cancelButtonMnemonic").charAt(0)); //$NON-NLS-1$
		pbClose.addActionListener(this);
		oButtonPanel.addButton(pbClose);

		pbHelp = new UIButton(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIImportDialog.helpButton")); //$NON-NLS-1$
		pbHelp.setMnemonic(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIImportDialog.helpButtonMnemonic").charAt(0)); //$NON-NLS-1$
		ProjectCompendium.APP.mainHB.enableHelpOnButton(pbHelp, "io.import_qm", ProjectCompendium.APP.mainHS); //$NON-NLS-1$
		oButtonPanel .addHelpButton(pbHelp);

		oContentPane.add(oButtonPanel, BorderLayout.SOUTH);

	}

	/**
	 * Handle the button push events.
	 * @param evt, the assoicated ActionEvent object.
	 */
	public void actionPerformed(ActionEvent evt) {
		Object source = evt.getSource();

		// Handle button events
		if (source instanceof JButton) {
			if (source == pbImport) {
				onImport();
			}
			else if (source == pbClose) {
				onCancel();
			}
		}

		if (source instanceof JRadioButton) {
			if (source == rbNormal && rbNormal.isSelected()) {
				cbInclude.setEnabled(true);
				cbInclude.setSelected(true);
			}
			else if (source == rbSmart && rbSmart.isSelected()) {
				cbInclude.setSelected(false);
				cbInclude.setEnabled(false);
			}
		}
	}


	/**
	 * Handle the import action, check if the selected file exists and if so import the file.
	 * If the file does not exist cancel the action.
	 */
	public void onImport()  {

		//set the import profile
		boolean normalProfile 	= rbNormal.isSelected();
		boolean includeInDetail = cbInclude.isSelected();
		boolean markseen 		= cbMarkSeen.isSelected();

		ProjectCompendium.APP.setImportProfile(normalProfile,includeInDetail, false, false);

		boolean oldValue = DBNode.getImportAsTranscluded();
		DBNode.setQuestmapImporting(true);
		DBNode.setImportAsTranscluded(cbTransclude.isSelected());
		DBNode.setNodesMarkedSeen(cbMarkSeen.isSelected());

		UIFileChooser fileDialog = new UIFileChooser();
		UIFileFilter filter = new UIFileFilter(new String[] {"txt"}, "Text Files"); //$NON-NLS-1$ //$NON-NLS-2$
		fileDialog.setFileFilter(filter);
		fileDialog.setRequiredExtension(".txt"); //$NON-NLS-1$
		fileDialog.setDialogTitle(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIImportDialog.chooseFile")); //$NON-NLS-1$
		fileDialog.setApproveButtonText(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIImportDialog.ImportButton")); //$NON-NLS-1$

		if (!UIImportDialog.lastFileDialogDir.equals("")) { //$NON-NLS-1$
			// FIX FOR MAC - NEEDS '/' ON END TO DENOTE A FOLDER
			File file = new File(UIImportDialog.lastFileDialogDir+ProjectCompendium.sFS);
			if (file.exists()) {
				fileDialog.setCurrentDirectory(file);
			}
		}

		UIUtilities.centerComponent(fileDialog, ProjectCompendium.APP);
		int retval = fileDialog.showOpenDialog(ProjectCompendium.APP);
		if (retval == JFileChooser.APPROVE_OPTION) {
			if ((fileDialog.getSelectedFile()) != null) {

				String fileName = fileDialog.getSelectedFile().getAbsolutePath();
				File fileDir = fileDialog.getCurrentDirectory();
				String dir = fileDir.getPath();

				if (fileName != null) {
					// save this so the next time we can point the user to this directory again.
					UIImportDialog.lastFileDialogDir = dir;

					// if the user selected doc/rtf file then a text file was genereted by word reader
					// to be imported into the project
					//String lowerCaseFileTitle=fileName.toLowerCase();
					//if (lowerCaseFileTitle.endsWith("doc") || lowerCaseFileTitle.endsWith("rtf")) {
					//	fileName = dir + "pc.txt";
					//}

					if ((new File(fileName)).exists()) {
						ProjectCompendium.APP.setStatus(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIImportDialog.importing")+fileDialog.getSelectedFile().getName()+"..."); //$NON-NLS-1$ //$NON-NLS-2$

						if (showViewList == false) {
							if (oViewPaneUI != null) {
								oViewPaneUI.setSmartImport(rbSmart.isSelected());
								oViewPaneUI.onImportFile(fileName);
							}
							else if (uiList != null) {
								uiList.getListUI().setSmartImport(rbSmart.isSelected());
								uiList.getListUI().onImportFile(fileName);
							}
						}
						else {
							int [] selection = table.getSelectedRows();

							for(int i=0;i<selection.length;i++) {

								View view = (View)table.getModel().getValueAt(selection[i],0);
								UIViewFrame oUIViewFrame = ProjectCompendium.APP.addViewToDesktop(view,view.getLabel());
								Vector history = new Vector();
								history.addElement(new String(sTitle));
								oUIViewFrame.setNavigationHistory(history);

								if (oUIViewFrame instanceof UIListViewFrame) {
									UIList list = ((UIListViewFrame)oUIViewFrame).getUIList();
									ListUI listUI = list.getListUI();
									listUI.setSmartImport(rbSmart.isSelected());
									listUI.onImportFile(fileName);
								}
								else {
									UIViewPane oUIViewPane = ((UIMapViewFrame)oUIViewFrame).getViewPane();
									oViewPaneUI = oUIViewPane.getUI();

									//pass the file name to the viewpaneUI importfile routine
									oViewPaneUI.setSmartImport(rbSmart.isSelected());
									oViewPaneUI.onImportFile(fileName);
								}
							}
						}
						dispose();
						ProjectCompendium.APP.setStatus(""); //$NON-NLS-1$
					}
				}
			}
		}
	}

	/**
	 * Set the current view when it is a map.
	 * @param list com.compendium.ui.plaf.ViewPaneUI, the current view.
	 */
	public void setViewPaneUI(ViewPaneUI vpUI) {
		oViewPaneUI = vpUI;
	}

	/**
	 * Set the current view when it is a list.
	 * @param list com.compendium.ui.UIList, the current view.
	 */
	public void setUIList(UIList list) {
		uiList = list;
	}

	/**
	 * Helper class, the data model for the list of views to import into.
	 */
	class ViewListTableModel extends AbstractTableModel {
		private String[] columnNames = {LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIImportDialog.label"), //$NON-NLS-1$
										LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIImportDialog.creationDate"), //$NON-NLS-1$
										LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIImportDialog.modDate")}; //$NON-NLS-1$
		private Object[][] data;

		public ViewListTableModel() {
			Vector vtTemp = new Vector();
			IModel model = ProjectCompendium.APP.getModel();
			Enumeration views = null;
			try {
				views = model.getNodeService().getAllActiveViews(model.getSession());
			} catch (Exception ex){}

			if (views != null) {
				for(Enumeration e = views;e.hasMoreElements();)
				{
					vtTemp.addElement((View)e.nextElement());
				}
				data = new Object [vtTemp.size()][3];
				int i = 0;
				for(Enumeration e = vtTemp.elements();e.hasMoreElements();i++)
				{
					View view = (View)e.nextElement();

					//trim text to fit the label for the timebeing since the label comes out of the scrollbar window
					String text = view.getLabel();
					data[i][0] = view;
					data[i][1] = view.getCreationDate();
					data[i][2] = view.getModificationDate();
					oViews.addElement(view);
				}
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
	}

	/**
	 * Handle the close action. Restores import settings in the core and closes the import dialog.
	 */
	public void onCancel() {
		DBNode.restoreImportSettings();
		setVisible(false);
		dispose();
	}
}
