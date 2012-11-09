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

import java.awt.Container;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import java.io.IOException;

import javax.swing.JProgressBar;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JOptionPane;

import java.net.MalformedURLException;

import javax.swing.border.EmptyBorder;

import com.compendium.LanguageProperties;
import com.compendium.ProjectCompendium;
import com.compendium.ui.UIButton;
import com.compendium.ui.UIButtonPanel;
import com.compendium.ui.dialogs.UIDialog;
import com.compendium.ui.dialogs.UIProgressDialog;
import com.compendium.core.db.management.DBProgressListener;


/**
 * Displays the options for uploading Meeting data.
 *
 * @author	Michelle Bachler
 */
public class UIMeetingUploadChoiceDialog extends UIDialog implements ActionListener, DBProgressListener{

	/** The button to cancel this dialog.*/
	private UIButton			pbCancel 	= null;

	/** The button to copy selected contents to the clipboard.*/
	private UIButton			pbUploadNow = null;

	/** The button to select all the elements in the table.*/
	private UIButton			pbSave 		= null;

	/** The button is used to open the relevant help.*/
	private UIButton			pbHelp 		= null;

	/** The MeetingManager object which this class needs to reference.*/
	private MeetingManager		oMeetingManager = null;

	/** The progress dialog holding the progress.*/
	private UIProgressDialog	oProgressDialog = null;

	/** The progress bar held in the dialog.*/
	private JProgressBar		oProgressBar = null;

	/** the thread that runs the progress bar.*/
	private ProgressThread		oThread = null;

	/** The counter used by the progress bar.*/
	private int					nCount = 0;


	/**
	 * Constructor. Initializes and sets up the dialog.
	 * @param oManager the {@Link com.compendium.meeting.MeetingManager MeetingManager} object that recorded the events to upload.
	 */
	public UIMeetingUploadChoiceDialog(MeetingManager oManager) {

		super(ProjectCompendium.APP, true);

		setTitle(LanguageProperties.getString(LanguageProperties.MEETING_BUNDLE, "UIMeetingUploadChoiceDialog.uploadOptionsTitle")); //$NON-NLS-1$
		setResizable(false);

		oProgressBar = new JProgressBar();
		oProgressBar.setMinimum(0);
		oProgressBar.setMaximum(100);

		oMeetingManager = oManager;

		Container oContentPane = getContentPane();
		oContentPane.setLayout(new BorderLayout());

		JPanel oMessagePanel = new JPanel();
		oMessagePanel.setBorder(new EmptyBorder(10,10,10,10));

		JTextArea label = new JTextArea(LanguageProperties.getString(LanguageProperties.MEETING_BUNDLE, "UIMeetingUploadChoiceDialog.chooseLabel")); //$NON-NLS-1$
		label.setLineWrap(true);
		label.setWrapStyleWord(true);
		label.setFont(new Font("Dialog", Font.PLAIN, 12)); //$NON-NLS-1$
		label.setSize(new  Dimension(300, 200));
		label.setBackground(oMessagePanel.getBackground());
		label.setEditable(false);
		oMessagePanel.add(label);

		oContentPane.add(oMessagePanel, BorderLayout.CENTER);
		oContentPane.add(createButtonPanel(), BorderLayout.SOUTH);

		pack();
	}

	/**
	 * Create the panel with the buttons for this dialog.
	 * @return a JPanel containing the button for this dialog.
	 */
    private UIButtonPanel createButtonPanel() {

		UIButtonPanel oButtonPanel = new UIButtonPanel();

		pbUploadNow = new UIButton(LanguageProperties.getString(LanguageProperties.MEETING_BUNDLE, "UIMeetingUploadChoiceDialog.uploadButton")); //$NON-NLS-1$
		pbUploadNow.setToolTipText(LanguageProperties.getString(LanguageProperties.MEETING_BUNDLE, "UIMeetingUploadChoiceDialog.uploadButtonTip")); //$NON-NLS-1$
		pbUploadNow.setMnemonic(LanguageProperties.getString(LanguageProperties.MEETING_BUNDLE, "UIMeetingUploadChoiceDialog.uploadButtonMnemonic").charAt(0)); //$NON-NLS-1$
		pbUploadNow.addActionListener(this);
		getRootPane().setDefaultButton(pbUploadNow);
		oButtonPanel.addButton(pbUploadNow);

		/*pbSave = new UIButton("Upload Later");
		pbSave.setToolTipText("Save the Compendium meeting event data to upload to the triplestore later");
		pbSave.setMnemonic(KeyEvent.VK_S);
		pbSave.addActionListener(this);
		oButtonPanel.addButton(pbSave);*/

		pbCancel = new UIButton(LanguageProperties.getString(LanguageProperties.MEETING_BUNDLE, "UIMeetingUploadChoiceDialog.discardButton")); //$NON-NLS-1$
		pbCancel.setToolTipText(LanguageProperties.getString(LanguageProperties.MEETING_BUNDLE, "UIMeetingUploadChoiceDialog.discardButtonTip")); //$NON-NLS-1$
		pbCancel.setMnemonic(LanguageProperties.getString(LanguageProperties.MEETING_BUNDLE, "UIMeetingUploadChoiceDialog.discardButtonMnemonic").charAt(0)); //$NON-NLS-1$
		pbCancel.addActionListener(this);
		oButtonPanel.addButton(pbCancel);

		pbHelp = new UIButton(LanguageProperties.getString(LanguageProperties.MEETING_BUNDLE, "UIMeetingUploadChoiceDialog.helpButton")); //$NON-NLS-1$
		pbHelp.setMnemonic(LanguageProperties.getString(LanguageProperties.MEETING_BUNDLE, "UIMeetingUploadChoiceDialog.helpButtonMnemonic").charAt(0)); //$NON-NLS-1$
		ProjectCompendium.APP.mainHB.enableHelpOnButton(pbHelp, "basics.memetic", ProjectCompendium.APP.mainHS); //$NON-NLS-1$
		oButtonPanel.addHelpButton(pbHelp);

		return oButtonPanel;
	}

	/**
	 * Handle button push events.
	 * @param event the associated ActionEvent for the button push.
	 */
	public void actionPerformed(ActionEvent event) {
		Object source = event.getSource();
		if (source == pbCancel) {
			onCancel();
		} else if (source == pbUploadNow) {
			onUpload();
		}
		/*else if (source == pbSave) {
			Thread thread = new Thread("UIMeetingUploadChoiceDialog.actionPerformed-2") {
				public void run() {
					try {
						oMeetingManager.addProgressListener(dlg);
						oThread = new ProgressThread("Saving to file..", "Data Saved");
						oThread.start();
						oMeetingManager.saveMeetingEventToFile();
					} catch (Exception e) {
						ProjectCompendium.APP.displayError("There was the following problem\ntrying to save meeting events to a file:\n\n" + e.getMessage());
					}
					oMeetingManager.removeProgressListener(dlg);
					onClose();
				}
			};
			thread.start();
		}*/
		}
    
    /**
     * Handles the upload dialog action
     */
    public void onUpload() {
        final UIMeetingUploadChoiceDialog dlg = this;
        Thread thread = new Thread("UIMeetingUploadChoiceDialog.actionPerformed-1") { //$NON-NLS-1$
				public void run() {
					try {
                    dlg.setVisible(false);
						oMeetingManager.addProgressListener(dlg);
                    oThread = new ProgressThread(LanguageProperties.getString(LanguageProperties.MEETING_BUNDLE, "UIMeetingUploadChoiceDialog.progressMessage"), LanguageProperties.getString(LanguageProperties.MEETING_BUNDLE, "UIMeetingUploadChoiceDialog.progressTitle")); //$NON-NLS-1$ //$NON-NLS-2$
						oThread.start();
                    oMeetingManager.saveAndUploadMeetingData();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    System.out.flush();
                    progressComplete();
                    ProjectCompendium.APP.displayError(LanguageProperties.getString(LanguageProperties.MEETING_BUNDLE, "UIMeetingUploadChoiceDialog.error1")+":\n\n"+ex.getLocalizedMessage()+"\n\n"); //$NON-NLS-1$ //$NON-NLS-2$
					}
					oMeetingManager.removeProgressListener(dlg);
					onClose();
				}
			};
			thread.start();
	}

	/**
	 * Handle the cancel dialog action.
	 * Override superclass to check that the user really wants to discard the data. If yes call {@Link #onClose() onClose()}.
	 */
	public void onCancel() {
		int answer = JOptionPane.showConfirmDialog(this, LanguageProperties.getString(LanguageProperties.MEETING_BUNDLE, "UIMeetingUploadChoiceDialog.discardEventsCheck")+"\n", LanguageProperties.getString(LanguageProperties.MEETING_BUNDLE, "UIMeetingUploadChoiceDialog.warning"), //$NON-NLS-1$ //$NON-NLS-2$
		JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
		if (answer == JOptionPane.YES_OPTION) {
			onClose();
		}
	}

	/**
	 * Dispose of this dialog.
	 */
	public void onClose() {
		setVisible(false);
		dispose();
	}


// PROGRESS LISTENER EVENTS

	/**
	 * Exntends Thread and creates the progress dialog.
	 */
	private class ProgressThread extends Thread {

		public ProgressThread(String sTitle, String sFinal) {
	  		oProgressDialog = new UIProgressDialog(ProjectCompendium.APP, sTitle, sFinal);
	  		oProgressDialog.showDialog(oProgressBar, false);
	  		oProgressDialog.setModal(true);
		}

		public void run() {
	  		oProgressDialog.setVisible(true);

			while (oProgressDialog.isVisible());
		}
	}

	/**
	 * Set the amount of progress items being counted.
	 *
	 * @param nCount the amount of progress items being counted.
	 */
    public void progressCount(int nCount) {
		oProgressBar.setMaximum(nCount);
		this.nCount = 0;
		oProgressBar.setValue(0);
		oProgressDialog.setStatus(0);
	}

	/**
	 * Indicate that progress has been updated.
	 *
	 * @param nCount the current position of the progress in relation to the inital count.
	 * @param sMessage the message to display to the user.
	 */
    public void progressUpdate(int nIncrement, String sMessage) {
		nCount += nIncrement;
		oProgressBar.setValue(nCount);
		oProgressDialog.setMessage(sMessage);
		oProgressDialog.setStatus(nCount);
	}

	/**
	 * Indicate that progress has completed.
	 */
    public void progressComplete() {
		this.nCount = 0;
		oProgressDialog.setVisible(false);
		oProgressDialog.dispose();
	}

	/**
	 * Indicate that progress has had a problem.
	 *
	 * @param sMessage the message to display to the user.
	 */
    public void progressAlert(String sMessage) {
		progressComplete();
		ProjectCompendium.APP.displayError(sMessage);
	}
}
