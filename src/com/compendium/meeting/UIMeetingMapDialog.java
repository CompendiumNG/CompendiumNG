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
import javax.swing.JTextArea;
import javax.swing.JScrollPane;

import java.lang.StringBuffer;

import java.awt.Container;
import java.awt.Insets;
import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import java.util.Vector;

import com.compendium.LanguageProperties;
import com.compendium.ProjectCompendium;
import com.compendium.core.datamodel.Meeting;
import com.compendium.ui.UIButton;
import com.compendium.ui.UIButtonPanel;
import com.compendium.ui.UIUtilities;
import com.compendium.ui.dialogs.UIDialog;

/**
 * Dialog to display meeting data and request creation of the meeting Compendium map.
 *
 * @author	Michelle Bachler
 * @version	1.0
 */
public class UIMeetingMapDialog extends UIDialog implements ActionListener {

	/** The button to create the meeting map*/
	private UIButton pbMap	 		= null;

	/** The button to cancel the dialog*/
	private UIButton pbCancel	 	= null;

	/** The button is used to open the relevant help.*/
	private UIButton pbHelp 		= null;

	/** The object holding the meeting data.*/
	private Meeting	oMeeting 		= null;

	/** A reference to the parent dialog that called this one.*/
	private UIMeetingRecorderDialog oParent = null;


	/**
	 * Constructor. Creates a new meeting map dialog.
	 * @param oMeeting the {@Link com.compendium.core.datamodel.Meeting Meeting} object
	 * 					holding the meeting data to display.
	 * @param oParent the {@Link com.compendium.meeting.UIMeetingRecorderDialog UIMeetingRecorderDialog} which invoked this dialog.
	 */
    public UIMeetingMapDialog(Meeting oMeeting, UIMeetingRecorderDialog oParent) {

		super(oParent, true);

		setTitle(LanguageProperties.getString(LanguageProperties.MEETING_BUNDLE, "UIMeetingMapDialog.title")); //$NON-NLS-1$

		this.oMeeting = oMeeting;
		this.oParent = oParent;

		setResizable(true);

		Container content = getContentPane();
		content.setLayout(new BorderLayout());
		content.add(createOptions(), BorderLayout.CENTER);
		content.add(createButtonPanel(), BorderLayout.SOUTH);

		pack();
    }

	/**
	 * Create the main dialog panel with the fields to complete.
	 * @return the new JPanel created.
	 */
    private JPanel createOptions() {

		StringBuffer sDetail = new StringBuffer(500);

		sDetail.append("FOR MEETING: \n\t"+oMeeting.getMeetingID()+"\n\n"); //$NON-NLS-1$ //$NON-NLS-2$
		sDetail.append("TITLE: "+oMeeting.getName()+"\n\n"); //$NON-NLS-1$ //$NON-NLS-2$
		sDetail.append("DATE: "+(UIUtilities.getSimpleDateFormat("d MMM, yyyy h:mm a")).format(oMeeting.getStartDate()).toString()+"\n"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		sDetail.append("\nATTENDEES: \n"); //$NON-NLS-1$
		Vector vtAttendees = oMeeting.getAttendees();
		MeetingAttendee attendee = null;
		int count = vtAttendees.size();
		for (int i=0; i<count; i++) {
			attendee = (MeetingAttendee)vtAttendees.elementAt(i);
			sDetail.append("\t"+attendee.getName()+"\n"); //$NON-NLS-1$ //$NON-NLS-2$
		}

		sDetail.append("\nAGENDA: \n"); //$NON-NLS-1$
		Vector vtAgenda = oMeeting.getAgenda();
		MeetingAgendaItem agenda = null;
		int countj = vtAgenda.size();
		for (int j=0; j<countj; j++) {
			agenda = (MeetingAgendaItem)vtAgenda.elementAt(j);
			sDetail.append("\t"+agenda.getDisplayName()+"\n"); //$NON-NLS-1$ //$NON-NLS-2$
		}

		sDetail.append("\nDOCUMENTS: \n"); //$NON-NLS-1$
		Vector vtDocs = oMeeting.getDocuments();
		MeetingDocument doc = null;
		int countk = vtDocs.size();
		for (int k=0; k<countk; k++) {
			doc = (MeetingDocument)vtDocs.elementAt(k);
			sDetail.append("\t"+doc.getName()+"\n"); //$NON-NLS-1$ //$NON-NLS-2$
		}

		JPanel panel = new JPanel();

		JTextArea area = new JTextArea();
		area.setEditable(false);
		area.setMargin(new Insets(1,1,1,1));
		area.setBackground(panel.getBackground());
		area.setColumns(30);
		area.append(sDetail.toString());

		JScrollPane scrollpane = new JScrollPane(area);
		scrollpane.setPreferredSize(new Dimension(450,400));

		panel.add(scrollpane);

		return panel;
    }

	/**
	 * Create the buttom panel with the action buttons.
	 * @return the new {@Link com.comendium.ui.UIButtonPanel UIButtonPanel} created.
	 */
    private UIButtonPanel createButtonPanel() {

		UIButtonPanel oButtonPanel = new UIButtonPanel();

		pbMap = new UIButton(LanguageProperties.getString(LanguageProperties.MEETING_BUNDLE, "UIMeetingMapDialog.createMeetingMapButton")); //$NON-NLS-1$
		pbMap.setMnemonic(LanguageProperties.getString(LanguageProperties.MEETING_BUNDLE, "UIMeetingMapDialog.createMeetingMapButtonMnemonic").charAt(0)); //$NON-NLS-1$
		pbMap.addActionListener(this);
		getRootPane().setDefaultButton(pbMap);
		oButtonPanel.addButton(pbMap);

		pbCancel = new UIButton(LanguageProperties.getString(LanguageProperties.MEETING_BUNDLE, "UIMeetingMapDialog.cancelButton")); //$NON-NLS-1$
		pbCancel.setMnemonic(LanguageProperties.getString(LanguageProperties.MEETING_BUNDLE, "UIMeetingMapDialog.cancelButtonMnemonic").charAt(0)); //$NON-NLS-1$
		pbCancel.addActionListener(this);
		oButtonPanel.addButton(pbCancel);

		pbHelp = new UIButton(LanguageProperties.getString(LanguageProperties.MEETING_BUNDLE, "UIMeetingMapDialog.helpButton")); //$NON-NLS-1$
		pbHelp.setMnemonic(LanguageProperties.getString(LanguageProperties.MEETING_BUNDLE, "UIMeetingMapDialog.helpButtonMnemonic").charAt(0)); //$NON-NLS-1$
		ProjectCompendium.APP.mainHB.enableHelpOnButton(pbHelp, "basics.memetic-record", ProjectCompendium.APP.mainHS); //$NON-NLS-1$
		oButtonPanel.addHelpButton(pbHelp);

		return oButtonPanel;
	}

	/**
	 * Process the actions from a button press.
	 * @param ae the ActionEvent associated with the button push.
	 */
    public void actionPerformed(ActionEvent ae) {
		Object source = ae.getSource();

		if (source == pbMap) {
			setCursor(new Cursor(java.awt.Cursor.WAIT_CURSOR));
			oParent.createMeetingMap();
			setCursor(new Cursor(java.awt.Cursor.DEFAULT_CURSOR));
			onCancel();
		} else if (source == pbCancel) {
			onCancel();
		}
	}
}
