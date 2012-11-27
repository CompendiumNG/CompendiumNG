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

import javax.swing.JTable;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.ListSelectionModel;
import javax.swing.JProgressBar;

import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import java.awt.Container;
import java.awt.Cursor;
import java.awt.BorderLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Dimension;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.IOException;

import java.util.Vector;
import java.util.Properties;

import com.compendium.LanguageProperties;
import com.compendium.ProjectCompendium;
import com.compendium.ui.UIButton;
import com.compendium.ui.UIButtonPanel;
import com.compendium.ui.UITableHeaderRenderer;
import com.compendium.ui.TableSorter;
import com.compendium.ui.dialogs.UIDialog;
import com.compendium.ui.dialogs.UIProgressDialog;
import com.compendium.core.datamodel.Meeting;
import com.compendium.core.datamodel.IModel;
import com.compendium.core.db.management.DBProgressListener;


/**
 * Dilaog to get the relevant data and connect to the meeting replay.
 *
 * @author	Michelle Bachler
 * @version	1.0
 */
public class UIMeetingReplayDialog extends UIDialog implements ActionListener, DBProgressListener {

	/** The name of the file holding the meeting replay jabber properties.*/
	public static final String 		PROPERTY_FILE 		= "MeetingReplay.properties"; //$NON-NLS-1$

	/** The button to open a connction.*/
	private UIButton				pbConnect			= null;

	/** The button to close the connection open.*/
	private UIButton				pbDisConnect		= null;

	/** The button to close the dialog.*/
	private UIButton				pbClose				= null;

	/** The button is used to open the relevant help.*/
	private UIButton 				pbHelp 				= null;

	/** The field for the server data.*/
	private JTextField				oServerField 		= null;

	/** The field for the user name.*/
	private JTextField				oNameField 			= null;

	/** The field for the file name of XML to download, if any.*/
	private JTextField				oFileNameField 			= null;

	/** The field for the password.*/
	private JPasswordField			oPasswordField 		= null;

	/** The field for the resource.*/
	private JTextField				oResourceField 		= null;

	/** The field for the room server details.*/
	private JTextField				oRoomServerField	= null;

	/** The server data.*/
	private String 					sServer 			= ""; //$NON-NLS-1$

	/** The user name .*/
	private String					sUsername 			= ""; //$NON-NLS-1$

	/** The password.*/
	private String 					sPassword 			= ""; //$NON-NLS-1$

	/** The resource.*/
	private String					sResource 			= "replayvideo"; //$NON-NLS-1$

	/** The room server.*/
	private String					sRoomServer 		= ""; //$NON-NLS-1$

	/** This holds the previously stored connection details, if any.*/
	private Properties				connectionProperties = null;

	/** A reference to the manager for this meeting.*/
	private MeetingManager			oMeetingManager 	= null;

	/** The data for the list of all known meetings.*/
	private Vector 					vtMeetings			= new Vector(10);

	/** The table holding the list of unrecorded meetings.*/
	private JTable					oTable 				= null;

	/** The scrollpane to put the list of meetings.*/
	private JScrollPane				oScrollpane			= new JScrollPane();

	/** The progress dialog holding the progress.*/
	private UIProgressDialog		oProgressDialog 	= null;

	/** The progress bar held in the dialog.*/
	private JProgressBar			oProgressBar 		= null;

	/** the thread that runs the progress bar.*/
	private ProgressThread			oThread 			= null;

	/** The counter used by the progress bar.*/
	private int						nCount 				= 0;


	/**
	 * Constructor. Creates and instance of this dialog.
	 * @param oMeetingManager the {@Link com.compendium.meeting.MeetingManager MeetingManager} instance controlling this replay session.
	 */
    public UIMeetingReplayDialog(MeetingManager oMeetingManager) {

		super(ProjectCompendium.APP, true);
		setTitle(LanguageProperties.getString(LanguageProperties.MEETING_BUNDLE, "UIMeetingReplayDialog.title")); //$NON-NLS-1$

		this.oMeetingManager = oMeetingManager;

		Container oContentPane = getContentPane();
		oContentPane.setLayout(new BorderLayout());

		loadProperties();

		oContentPane.add(createDetailsPanel(), BorderLayout.CENTER);
		oContentPane.add(createButtonPanel(), BorderLayout.SOUTH);

		pack();
		setResizable(false);
    }

	/**
	 * Create the main dialog panel with the fields to complete.
	 * @return and instance of JPanel.
	 */
    private JPanel createDetailsPanel() {

		JPanel panel = new JPanel();
		panel.setBorder(new EmptyBorder(10,10,10,10));

		GridBagLayout gb = new GridBagLayout();
		GridBagConstraints gc = new GridBagConstraints();
		panel.setLayout(gb);

		gc.insets = new Insets(5,5,5,5);
		gc.anchor = GridBagConstraints.WEST;

		JLabel label = new JLabel(LanguageProperties.getString(LanguageProperties.MEETING_BUNDLE, "UIMeetingReplayDialog.selectMeeting")); //$NON-NLS-1$
		gc.gridy = 0;
		gc.gridx = 0;
		gc.gridwidth = 2;
		gb.setConstraints(label, gc);
		panel.add(label);

		MeetingListTableModel tablemodel = new MeetingListTableModel();
		TableSorter sorter = new TableSorter(tablemodel);
		oTable = new JTable(sorter);
		oTable.getColumn(LanguageProperties.getString(LanguageProperties.MEETING_BUNDLE, "UIMeetingReplayDialog.name")).setPreferredWidth(250); //$NON-NLS-1$
		oTable.getColumn(LanguageProperties.getString(LanguageProperties.MEETING_BUNDLE, "UIMeetingReplayDialog.date")).setPreferredWidth(100); //$NON-NLS-1$
		oTable.getTableHeader().setReorderingAllowed(false);
		oTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		oTable.clearSelection();
		sorter.addMouseListenerToHeaderInTable(oTable);
		setRenderers();
		
		oScrollpane = new JScrollPane(oTable);
		oScrollpane.setPreferredSize(new Dimension(350,150));
		gc.gridy = 1;
		gc.gridx = 0;
		gc.gridwidth = 2;
		gb.setConstraints(oScrollpane, gc);
		panel.add(oScrollpane);

		JPanel innerpanel = new JPanel();
		innerpanel.setBorder(new TitledBorder(LanguageProperties.getString(LanguageProperties.MEETING_BUNDLE, "UIMeetingReplayDialog.enterJabberDetails"))); //$NON-NLS-1$

		GridBagLayout gb2 = new GridBagLayout();
		GridBagConstraints gc2 = new GridBagConstraints();
		innerpanel.setLayout(gb2);

		gc2.insets = new Insets(5,5,5,5);
		gc2.anchor = GridBagConstraints.WEST;
		gc2.gridy = 0;
		gc2.gridx = 0;

		JLabel oServerLabel = new JLabel(LanguageProperties.getString(LanguageProperties.MEETING_BUNDLE, "UIMeetingReplayDialog.server")+": "); //$NON-NLS-1$
		gb2.setConstraints(oServerLabel, gc2);
		innerpanel.add(oServerLabel);

		gc2.gridy = 0;
		gc2.gridx = 1;

		oServerField = new JTextField(sServer);
		oServerField.setColumns(30);
		gb2.setConstraints(oServerField, gc2);
		innerpanel.add(oServerField);

		gc2.gridy = 1;
		gc2.gridx = 0;

		JLabel oNameLabel = new JLabel(LanguageProperties.getString(LanguageProperties.MEETING_BUNDLE, "UIMeetingReplayDialog.username")+": "); //$NON-NLS-1$
		gb2.setConstraints(oNameLabel, gc2);
		innerpanel.add(oNameLabel);

		gc2.gridy = 1;
		gc2.gridx = 1;

		oNameField = new JTextField(sUsername);
		oNameField.setColumns(20);
		gb2.setConstraints(oNameField, gc2);
		innerpanel.add(oNameField);

		gc2.gridy = 2;
		gc2.gridx = 0;

		JLabel oPasswordLabel = new JLabel(LanguageProperties.getString(LanguageProperties.MEETING_BUNDLE, "UIMeetingReplayDialog.password")+": "); //$NON-NLS-1$
		gb2.setConstraints(oPasswordLabel, gc2);
		oServerLabel.setText(LanguageProperties.getString(LanguageProperties.MEETING_BUNDLE, "UIMeetingReplayDialog.server")); //$NON-NLS-1$
		innerpanel.add(oPasswordLabel);

		gc2.gridy = 2;
		gc2.gridx = 1;

		oPasswordField = new JPasswordField(sPassword);
		oPasswordField.setColumns(20);
		gb2.setConstraints(oPasswordField, gc2);
		innerpanel.add(oPasswordField);

		gc2.gridy = 3;
		gc2.gridx = 0;

		oNameLabel.setText(LanguageProperties.getString(LanguageProperties.MEETING_BUNDLE, "UIMeetingReplayDialog.username")+": "); //$NON-NLS-1$
		oPasswordLabel.setText(LanguageProperties.getString(LanguageProperties.MEETING_BUNDLE, "UIMeetingReplayDialog.password")+": "); //$NON-NLS-1$

		JLabel oResourceLabel = new JLabel(LanguageProperties.getString(LanguageProperties.MEETING_BUNDLE, "UIMeetingReplayDialog.resource")+": "); //$NON-NLS-1$
		gc.gridwidth = 1;
		gb2.setConstraints(oResourceLabel, gc2);
		innerpanel.add(oResourceLabel);

		gc2.gridy = 3;
		gc2.gridx = 1;

		oResourceField = new JTextField(sResource);
		oResourceField.setColumns(30);
		gb2.setConstraints(oResourceField, gc2);
		innerpanel.add(oResourceField);

		gc2.gridy = 4;
		gc2.gridx = 0;
		gc2.gridwidth = 2;

		JLabel oRoomServerLabel = new JLabel(LanguageProperties.getString(LanguageProperties.MEETING_BUNDLE, "UIMeetingReplayDialog.conferenceID")+": "); //$NON-NLS-1$
		gb2.setConstraints(oRoomServerLabel, gc2);
		innerpanel.add(oRoomServerLabel);

		gc2.gridy = 5;
		gc2.gridx = 0;

		oRoomServerField = new JTextField(sRoomServer);
		oRoomServerField.setColumns(40);
		gc2.gridwidth = GridBagConstraints.REMAINDER;
		gb2.setConstraints(oRoomServerField, gc2);
		innerpanel.add(oRoomServerField);

		gc2.gridy = 6;
		gc2.gridx = 0;

		JLabel oFileNameLabel = new JLabel(LanguageProperties.getString(LanguageProperties.MEETING_BUNDLE, "UIMeetingReplayDialog.dataFile")+": "); //$NON-NLS-1$
		gb2.setConstraints(oFileNameLabel, gc2);
		innerpanel.add(oFileNameLabel);

		gc2.gridx = 1;

		oFileNameField = new JTextField();
		oFileNameField.setColumns(30);
		gb2.setConstraints(oFileNameField, gc2);
		innerpanel.add(oFileNameField);

		gc.gridy = 3;
		gc.gridx = 0;
		gc.gridwidth = 2;
		gb.setConstraints(innerpanel, gc);
		panel.add(innerpanel);

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
	 * @return the {@Link com.compendium.ui.UIButtonPanel UIButtonPanel} instance for this dialog.
	 */
    private UIButtonPanel createButtonPanel() {

		UIButtonPanel oButtonPanel = new UIButtonPanel();

		pbConnect = new UIButton(LanguageProperties.getString(LanguageProperties.MEETING_BUNDLE, "UIMeetingReplayDialog.connectButton")); //$NON-NLS-1$
		pbConnect.setMnemonic(LanguageProperties.getString(LanguageProperties.MEETING_BUNDLE, "UIMeetingReplayDialog.connectButtonMnemonic").charAt(0)); //$NON-NLS-1$
		pbConnect.addActionListener(this);
		oButtonPanel.addButton(pbConnect);

		pbDisConnect = new UIButton(LanguageProperties.getString(LanguageProperties.MEETING_BUNDLE, "UIMeetingReplayDialog.disconnectButton")); //$NON-NLS-1$
		pbDisConnect.setMnemonic(LanguageProperties.getString(LanguageProperties.MEETING_BUNDLE, "UIMeetingReplayDialog.disconnectButtonMnemonic").charAt(0)); //$NON-NLS-1$
		pbDisConnect.addActionListener(this);
		oButtonPanel.addButton(pbDisConnect);

		if (oMeetingManager.isMeetingReplayConnected()) {
			pbConnect.setEnabled(false);
			getRootPane().setDefaultButton(pbDisConnect);
		} else {
			pbDisConnect.setEnabled(false);
			getRootPane().setDefaultButton(pbConnect);
		}

		pbClose = new UIButton(LanguageProperties.getString(LanguageProperties.MEETING_BUNDLE, "UIMeetingReplayDialog.closeButton")); //$NON-NLS-1$
		pbClose.setMnemonic(LanguageProperties.getString(LanguageProperties.MEETING_BUNDLE, "UIMeetingReplayDialog.closeButtonMnemonic").charAt(0)); //$NON-NLS-1$
		pbClose.addActionListener(this);
		oButtonPanel.addButton(pbClose);

		pbHelp = new UIButton(LanguageProperties.getString(LanguageProperties.MEETING_BUNDLE, "UIMeetingReplayDialog.helpButton")); //$NON-NLS-1$
		pbHelp.setMnemonic(LanguageProperties.getString(LanguageProperties.MEETING_BUNDLE, "UIMeetingReplayDialog.helpButtonMnemonic").charAt(0)); //$NON-NLS-1$
		ProjectCompendium.APP.mainHB.enableHelpOnButton(pbHelp, "basics.memetic-replay", ProjectCompendium.APP.mainHS); //$NON-NLS-1$
		oButtonPanel.addHelpButton(pbHelp);

		return oButtonPanel;
	}

	/**
	 * Helper class, the data model for the list of meetings.
	 */
	class MeetingListTableModel extends AbstractTableModel {

		/** String array holding the column names for this table.*/
		private String[] columnNames = {LanguageProperties.getString(LanguageProperties.MEETING_BUNDLE, "UIMeetingReplayDialog.name"), //$NON-NLS-1$
										LanguageProperties.getString(LanguageProperties.MEETING_BUNDLE, "UIMeetingReplayDialog.date")}; //$NON-NLS-1$
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
				ProjectCompendium.APP.displayError(LanguageProperties.getString(LanguageProperties.MEETING_BUNDLE, "UIMeetingReplayDialog.meetingNotLoaded")+":\n\n"+ex.getLocalizedMessage()); //$NON-NLS-1$
			}

			data = new Object [vtMeetings.size()][2];
			Meeting meeting = null;

			int count = vtMeetings.size();
			for(int i = 0; i<count; i++) {
				meeting = (Meeting)vtMeetings.elementAt(i);
				String text = meeting.getName();
				java.util.Date date = meeting.getStartDate();
				data[i][0] = text;
				data[i][1] = date;
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
		 * @return a Class fo the data in the column at the given position.
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

		if (source == pbConnect) {
			onConnect();
		} else if (source == pbDisConnect) {
			onDisconnect();
		} else if (source == pbClose) {
			onCancel();
		}
	}

	/**
	 * Establish a Jabber connection with the entered information.
	 */
	public void onConnect() {

		String filename = oFileNameField.getText();
		filename = filename.trim();

		int sel = oTable.getSelectedRow();
		if (sel == -1 && filename.equals("")) { //$NON-NLS-1$
			ProjectCompendium.APP.displayError(LanguageProperties.getString(LanguageProperties.MEETING_BUNDLE, "UIMeetingReplayDialog.selectMeetingMessageA")+"\n\n "+
					LanguageProperties.getString(LanguageProperties.MEETING_BUNDLE, "UIMeetingReplayDialog.selectMeetingMessageB")); //$NON-NLS-1$
			return;
		}

		String server = oServerField.getText();
		String username = oNameField.getText();
		String password = new String(oPasswordField.getPassword());

		if ( (!server.equals("") && !username.equals("") && !password.equals("")) ) { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			String roomServer = oRoomServerField.getText();
			String resource = oResourceField.getText();

			if (sel > -1) {
				Meeting meeting = (Meeting)vtMeetings.elementAt(sel);
				oMeetingManager.setMeeting(meeting);
			}

			oMeetingManager.setMapDataFile(filename);
            oMeetingManager.setupMeetingForReplay();
			oMeetingManager.openMeetingReplayConnection(server, username, password, resource, roomServer);
			onCancel();
		} else {
			ProjectCompendium.APP.displayError(LanguageProperties.getString(LanguageProperties.MEETING_BUNDLE, "UIMeetingReplayDialog.completeDetails")); //$NON-NLS-1$
		}
	}

	/**
	 * Disconnect from the current Jabber connection.
	 */
	public void onDisconnect() {

		final UIMeetingReplayDialog dlg = this;

		ProjectCompendium.APP.setWaitCursor();
		this.setCursor(new Cursor(Cursor.WAIT_CURSOR));

		Thread thread = new Thread("UIMeetingReplayDialog-1") { //$NON-NLS-1$
			public void run() {

				oMeetingManager.stopReplayRecording();

				dlg.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
				ProjectCompendium.APP.setDefaultCursor();

				onCancel();
			}
		};
		thread.start();
	}

	/**
	 * Load the previously stored medai replay connection data.
	 */
	private void loadProperties() {

		File optionsFile = new File("System"+ProjectCompendium.sFS+"resources"+ProjectCompendium.sFS+PROPERTY_FILE); //$NON-NLS-1$ //$NON-NLS-2$
		connectionProperties = new Properties();

		if (optionsFile.exists()) {
			try {
				connectionProperties.load(new FileInputStream("System"+ProjectCompendium.sFS+"resources"+ProjectCompendium.sFS+PROPERTY_FILE)); //$NON-NLS-1$ //$NON-NLS-2$

				String value = connectionProperties.getProperty("mediacompserver"); //$NON-NLS-1$
				if (value != null)
					sServer = value;

				value = connectionProperties.getProperty("mediacompusername"); //$NON-NLS-1$
				if (value != null)
					sUsername = value;

				value = connectionProperties.getProperty("mediacomppassword"); //$NON-NLS-1$
				if (value != null)
					sPassword = value;

				value = connectionProperties.getProperty("mediacompresource"); //$NON-NLS-1$
				if (value != null)
					sResource = value;

				value = connectionProperties.getProperty("mediaroomserver"); //$NON-NLS-1$
				if (value != null)
					sRoomServer = value;

			} catch (IOException e) {
				System.out.println("Unable to load MeetingReplay.properties file"); //$NON-NLS-1$
			}
		}
	}

	/**
	 * Handle the enter key action. Override superclass to do nothing
	 */
	public void onEnter() {}

	/**
	 * Handle the close action. Closes the Meeting Replay dialog and saves the properties
	 */
	public void onCancel() {

		setVisible(false);

		try {
			if (!(oServerField.getText()).equals("")) //$NON-NLS-1$
				connectionProperties.put("mediacompserver", oServerField.getText()); //$NON-NLS-1$
			if (!(oNameField.getText()).equals("")) //$NON-NLS-1$
				connectionProperties.put("mediacompusername", oNameField.getText()); //$NON-NLS-1$
			if (!(new String(oPasswordField.getPassword())).equals("")) //$NON-NLS-1$
				connectionProperties.put("mediacomppassword", new String(oPasswordField.getPassword())); //$NON-NLS-1$
			if (!(oResourceField.getText()).equals("")) //$NON-NLS-1$
				connectionProperties.put("mediacompresource", oResourceField.getText()); //$NON-NLS-1$
			if (!(new String(oRoomServerField.getText())).equals("")) //$NON-NLS-1$
				connectionProperties.put("mediaroomserver", oRoomServerField.getText()); //$NON-NLS-1$

			connectionProperties.store(new FileOutputStream("System"+ProjectCompendium.sFS+"resources"+ProjectCompendium.sFS+PROPERTY_FILE), "Media Replay Details"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		} catch (IOException e) {
			ProjectCompendium.APP.displayError(LanguageProperties.getString(LanguageProperties.MEETING_BUNDLE, "UIMeetingReplayDialog.ioError")); //$NON-NLS-1$
		}

		dispose();
	}

// PROGRESS LISTENER EVENTS

	/**
	 * Draws the progress dialog.
	 */
	private class ProgressThread extends Thread {

		public ProgressThread(String sTitle, String sFinal) {
	  		oProgressDialog = new UIProgressDialog(ProjectCompendium.APP, sTitle, sFinal);
	  		oProgressDialog.showDialog(oProgressBar, false);
	  		oProgressDialog.setModal(true);
		}

		public void run() {
	  		oProgressDialog.setVisible(true);
			while(oProgressDialog.isVisible());
		}
	}

	/**
	 * Set the amount of progress items being counted and sets the progress bar at zero.
	 *
	 * @param int nCount the amount of progress items being counted.
	 */
    public void progressCount(int nCount) {
		oProgressBar.setMaximum(nCount);
		this.nCount = 0;
		oProgressBar.setValue(0);
		oProgressDialog.setStatus(0);
	}

	/**
	 * Indicate that progress has been updated. Increment the progress bar count.
	 *
	 * @param int nCount the current position of the progress in relation to the inital count.
	 * @param String sMessage the message to display to the user.
	 */
    public void progressUpdate(int nIncrement, String sMessage) {
		nCount += nIncrement;
		oProgressBar.setValue(nCount);
		oProgressDialog.setMessage(sMessage);
		oProgressDialog.setStatus(nCount);
	}

	/**
	 * Indicate that progress has completed.
	 * Hide the progress dialog.
	 */
    public void progressComplete() {
		this.nCount = 0;
		oProgressDialog.setVisible(false);
		oProgressDialog.dispose();
	}

	/**
	 * Indicate that progress has had a problem.
	 * Display the error message to the user.
	 *
	 * @param String sMessage the message to display to the user.
	 */
    public void progressAlert(String sMessage) {
		progressComplete();
		ProjectCompendium.APP.displayError(sMessage);
	}
}
