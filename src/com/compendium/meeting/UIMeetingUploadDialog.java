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

package com.compendium.meeting;

import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JScrollPane;
import javax.swing.JLabel;
import javax.swing.ListSelectionModel;

import javax.swing.border.EmptyBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;

import java.awt.Container;
import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Dimension;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.Vector;
import java.util.Hashtable;

import com.compendium.LanguageProperties;
import com.compendium.ProjectCompendium;
import com.compendium.core.datamodel.Meeting;
import com.compendium.core.datamodel.IModel;
import com.compendium.ui.UIButton;
import com.compendium.ui.UIButtonPanel;
import com.compendium.ui.TableSorter;
import com.compendium.ui.UITableHeaderRenderer;
import com.compendium.ui.UIViewFrame;

import com.compendium.ui.dialogs.UIDialog;


/**
 * Dilaog to present the user with a list of meetings that have been stored to file and can be uploaded.
 *
 * @author	Michelle Bachler
 * @version	1.0
 */
public class UIMeetingUploadDialog extends UIDialog implements ActionListener {

	/** The butto to opena connction.*/
	private UIButton			pbUpload		= null;

	/** The button to close the dialog.*/
	private UIButton			pbClose			= null;

	/** The button is used to open the relevant help.*/
	private UIButton			pbHelp 			= null;

	/** A reference to the manager for this meeting.*/
	private MeetingManager		oMeetingManager = null;

	/** A list of files holding meeting data which has been saved.*/
	private Vector 				vtFiles			= new Vector();

	/** The data for the list of all known meetings.*/
	private Vector 				vtMeetings		= new Vector();

	/** The table holding the list of unrecorded meetings.*/
	private JTable				oTable 			= null;

	/** The scrollpane to put the list of meetings.*/
	private JScrollPane			oScrollpane		= new JScrollPane();

	/**
	 * Constructor.
	 */
    public UIMeetingUploadDialog(MeetingManager oMeetingManager) {

		super(ProjectCompendium.APP, true);
		setTitle(LanguageProperties.getString(LanguageProperties.MEETING_BUNDLE, "UIMeetingUploadDialog.title")); //$NON-NLS-1$

		this.oMeetingManager = oMeetingManager;

		Container oContentPane = getContentPane();
		oContentPane.setLayout(new BorderLayout());

		oContentPane.add(createDetailsPanel(), BorderLayout.CENTER);
		oContentPane.add(createButtonPanel(), BorderLayout.SOUTH);

		pack();
		setResizable(false);
    }

	/**
	 * Create and return the main panel with the fields to complete.
	 */
    private JPanel createDetailsPanel() {

		JPanel panel = new JPanel();
		panel.setBorder(new EmptyBorder(10,10,10,10));

		GridBagLayout gb = new GridBagLayout();
		GridBagConstraints gc = new GridBagConstraints();
		panel.setLayout(gb);

		gc.insets = new Insets(5,5,5,5);
		gc.anchor = GridBagConstraints.WEST;

		// GET A LIST OF ALL SAVED DATA FILES
		String sDatabaseName = ProjectCompendium.APP.getModel().getModelName();
		String sFilePath = oMeetingManager.sDirectory+ProjectCompendium.sFS+sDatabaseName+ProjectCompendium.sFS;
		File events = new File(sFilePath);
		File[] files = events.listFiles();

		File file = null;
		String sFileName = ""; //$NON-NLS-1$

		if (files != null) {
			for (int i=0; i<files.length; i++) {
				file = (File)files[i];
				sFileName = file.getName();
				if (!sFileName.endsWith(".uploaded")) { //$NON-NLS-1$
					vtFiles.addElement(file);
				}
			}
		}

		JLabel label = new JLabel(LanguageProperties.getString(LanguageProperties.MEETING_BUNDLE, "UIMeetingUploadDialog.selectData")+": "); //$NON-NLS-1$
		gc.gridy = 0;
		gc.gridx = 0;
		gc.gridwidth = 2;
		gb.setConstraints(label, gc);
		panel.add(label);

		MeetingListTableModel tablemodel = new MeetingListTableModel();
		TableSorter sorter = new TableSorter(tablemodel);
		sorter.sortByColumn(1, true);
		sorter.sortByColumn(0, true);
		oTable = new JTable(sorter);
		oTable.getColumn(LanguageProperties.getString(LanguageProperties.MEETING_BUNDLE, "UIMeetingUploadDialog.name")).setPreferredWidth(250); //$NON-NLS-1$
		oTable.getColumn(LanguageProperties.getString(LanguageProperties.MEETING_BUNDLE, "UIMeetingUploadDialog.type")).setPreferredWidth(100); //$NON-NLS-1$
		oTable.getColumn(LanguageProperties.getString(LanguageProperties.MEETING_BUNDLE, "UIMeetingUploadDialog.date")).setPreferredWidth(150); //$NON-NLS-1$
		oTable.getTableHeader().setReorderingAllowed(false);
		oTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		oTable.clearSelection();
		sorter.addMouseListenerToHeaderInTable(oTable);
		setRenderers();
		oScrollpane = new JScrollPane(oTable);
		oScrollpane.setPreferredSize(new Dimension(450,250));
		gc.gridy = 1;
		gc.gridx = 0;
		gc.gridwidth = 2;
		gb.setConstraints(oScrollpane, gc);
		panel.add(oScrollpane);

		return panel;
    }


	/**
	 * Set the header renderers for the table column headers.
	 */
    public void setRenderers() {
    	int count = oTable.getModel().getColumnCount();
        for (int i = 0; i < count; i++) {
        	TableColumn aColumn = oTable.getColumnModel().getColumn(i);
        	UITableHeaderRenderer headerRenderer = new UITableHeaderRenderer();
            aColumn.setHeaderRenderer(headerRenderer);
    	}
 	}

	/**
	 * Create the panel with the buttons for this dialog.
	 * @return a new {@Link com.compendium.ui.UIButtonPanel UIButtonPanel} containing the buttons for this dialog.
	 */
    private UIButtonPanel createButtonPanel() {

		UIButtonPanel oButtonPanel = new UIButtonPanel();

		pbUpload = new UIButton(LanguageProperties.getString(LanguageProperties.MEETING_BUNDLE, "UIMeetingUploadDialog.uploadButton")); //$NON-NLS-1$
		pbUpload.setMnemonic(LanguageProperties.getString(LanguageProperties.MEETING_BUNDLE, "UIMeetingUploadDialog.uploadButtonMnemonic").charAt(0)); //$NON-NLS-1$
		pbUpload.addActionListener(this);
		getRootPane().setDefaultButton(pbUpload);
		oButtonPanel.addButton(pbUpload);

		pbClose = new UIButton(LanguageProperties.getString(LanguageProperties.MEETING_BUNDLE, "UIMeetingUploadDialog.closeButton")); //$NON-NLS-1$
		pbClose.setMnemonic(LanguageProperties.getString(LanguageProperties.MEETING_BUNDLE, "UIMeetingUploadDialog.closeButtonMnemonic").charAt(0)); //$NON-NLS-1$
		pbClose.addActionListener(this);
		oButtonPanel.addButton(pbClose);

		pbHelp = new UIButton(LanguageProperties.getString(LanguageProperties.MEETING_BUNDLE, "UIMeetingUploadDialog.helpButton")); //$NON-NLS-1$
		pbHelp.setMnemonic(LanguageProperties.getString(LanguageProperties.MEETING_BUNDLE, "UIMeetingUploadDialog.helpButtonMnemonic").charAt(0)); //$NON-NLS-1$
		ProjectCompendium.APP.mainHB.enableHelpOnButton(pbHelp, "basics.memetic-upload", ProjectCompendium.APP.mainHS); //$NON-NLS-1$
		oButtonPanel.addHelpButton(pbHelp);

		return oButtonPanel;
	}

	/**
	 * Helper class, the data model for the list of meetings.
	 */
	class MeetingListTableModel extends AbstractTableModel {

		/** String array holding the column names for this table.*/
		private String[] columnNames = {LanguageProperties.getString(LanguageProperties.MEETING_BUNDLE, "UIMeetingUploadDialog.name"), LanguageProperties.getString(LanguageProperties.MEETING_BUNDLE, "UIMeetingUploadDialog.type"), //$NON-NLS-1$ //$NON-NLS-2$
										LanguageProperties.getString(LanguageProperties.MEETING_BUNDLE, "UIMeetingUploadDialog.date")}; //$NON-NLS-1$
		/** An array of arrays holding the data for this table.*/
		private Object[][] data;

		/**
		 * Constructor to create a new MeetingListTableModel instance.
		 */
		public MeetingListTableModel() {

			try {
				IModel model = ProjectCompendium.APP.getModel();
				vtMeetings = (model.getMeetingService()).getMeetings(model.getSession());
			} catch (Exception ex) {
				ProjectCompendium.APP.displayError(LanguageProperties.getString(LanguageProperties.MEETING_BUNDLE, "UIMeetingUploadDialog.errorUploading")+":\n\n"+ex.getLocalizedMessage()); //$NON-NLS-1$
			}

			int count = vtFiles.size();
			int countj = vtMeetings.size();
			String sFileName = ""; //$NON-NLS-1$
			String sText = ""; //$NON-NLS-1$
			String sType = "Main Meeting"; //$NON-NLS-1$
			String sDate = ""; //$NON-NLS-1$
			String sMeetingID = ""; //$NON-NLS-1$

			data = new Object [count][4];
			Meeting meeting = null;
			File file = null;

			Hashtable htFiles = new Hashtable();

			for (int j = 0; j<count; j++) {
				file = (File)vtFiles.elementAt(j);
				sFileName = file.getName();
				htFiles.put(sFileName, sFileName);
			}

			for (int i = 0; i<count; i++) {
				file = (File)vtFiles.elementAt(i);
				sFileName = file.getName();

				// IF THE FILE HAS BEEN UPLOAD, DON'T ADD TO LIST
				if (sFileName.endsWith(".uploaded")) { //$NON-NLS-1$
					vtFiles.removeElementAt(i);
					count--;
					i--;
					continue;
				}
				else {
					// IF THE FILE WAS UPLOAD AND COPIED, BUT THE ORIGINAL NOT YET DELETED
					// DON'T INLCUDE ON LIST
					String sTempFile = sFileName+".uploaded"; //$NON-NLS-1$
					if (htFiles.containsKey(sTempFile)) {
						vtFiles.removeElementAt(i);
						count--;
						i--;
						continue;
					}
				}

				for (int j=0; j<countj; j++) {
					meeting = (Meeting)vtMeetings.elementAt(j);
					if (sFileName.startsWith(meeting.getMeetingMapID())) {
						sText = meeting.getName();
						sMeetingID = meeting.getMeetingID();
						break;
					}
				}

				int index = sFileName.indexOf("_Replay_"); //$NON-NLS-1$
				if (index != -1) {
					sType = "Replay"; //$NON-NLS-1$
				}

				if (sFileName.endsWith(".zip")) { //$NON-NLS-1$
					sType += " - Maps"; //$NON-NLS-1$
				} else {
					sType += " - Events"; //$NON-NLS-1$
				}

				//DOES NOT WORK ON THE MAC as is 1.5
				//if (sFileName.contains("_Replay_")) {
				//	sType = "Replay";
				//}

				int ind = sFileName.lastIndexOf("_"); //$NON-NLS-1$
				int ind2 = sFileName.lastIndexOf("."); //$NON-NLS-1$
				sDate = sFileName.substring(ind+1, ind2);

				data[i][0] = sText;
				data[i][1] = sType;
				data[i][2] = sDate;
				data[i][3] = sMeetingID;
			}
		}

		/**
		 * Return a count of the columns in this table.
		 * @return an int representing the number of columns in this table.
		 */
		public int getColumnCount() {
			return columnNames.length;
		}

		/**
		 * Return a count of the rows in this table.
		 * @return an int representing the number of rows in this table.
		 */
		public int getRowCount() {
			return data.length;
		}

		/**
		 * Return the name of the column for the given column number.
		 * @param col the column whose name to return.
		 * @return a the appropriate column name.
		 */
		public String getColumnName(int col) {
			return columnNames[col];
		}

		/**
		 * Return the object at the given row and column position.
		 * @param row the row the object to return is in.
		 * @param col the column the object to return is in.
		 * @return the object at the given row and column.
		 */
		public Object getValueAt(int row, int col) {
			return data[row][col];
		}

		/**
		 * Return the class of the data in the column at the given column position.
		 * @param c the column whose data class to return.
		 * @return a Class for the data in the column at the given position.
		 */
		public Class getColumnClass(int c) {
			Object obj = getValueAt(0, c);
			if (obj != null)
				return obj.getClass();

			return null;
		}
	}

	/**
	 * Handle action events coming from the buttons.
	 * @param evt the associated ActionEvent.
	 */
    public void actionPerformed(ActionEvent ae) {
		Object source = ae.getSource();

		if (source == pbUpload) {
			onUpload();
		} else if (source == pbClose) {
			onCancel();
		}
	}

	/**
	 * Upload the data from the selected meeting/replay file.
	 */
	public void onUpload() {

		UIViewFrame activeFrame = ProjectCompendium.APP.getCurrentFrame();
		ProjectCompendium.APP.setWaitCursor();
		activeFrame.setCursor(new Cursor(java.awt.Cursor.WAIT_CURSOR));
		this.setCursor(new Cursor(java.awt.Cursor.WAIT_CURSOR));

		int sel = oTable.getSelectedRow();
		if (sel == -1) {
			ProjectCompendium.APP.displayError(LanguageProperties.getString(LanguageProperties.MEETING_BUNDLE, "UIMeetingUploadDialog.selectMeeting")); //$NON-NLS-1$
			return;
		}

		File file = (File)vtFiles.elementAt(sel);
		try {
			oMeetingManager.uploadAllFiles(file.getAbsolutePath(), (String)oTable.getModel().getValueAt(sel, 3));
		} catch (Exception ex) {
			ProjectCompendium.APP.displayError(LanguageProperties.getString(LanguageProperties.MEETING_BUNDLE, "UIMeetingUploadDialog.errorUploadingFiles")+":\n\n"+ex.getLocalizedMessage()); //$NON-NLS-1$
		}

		this.setCursor(Cursor.getDefaultCursor());
		activeFrame.setCursor(Cursor.getDefaultCursor());
		ProjectCompendium.APP.setDefaultCursor();

		onCancel();
	}
}
