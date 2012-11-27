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
import java.io.*;
import java.util.*;
import java.sql.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;
import javax.help.*;

import com.compendium.*;
import com.compendium.ui.*;


import com.compendium.core.*;
import com.compendium.core.datamodel.*;
import com.compendium.core.db.management.*;
import com.compendium.core.CoreUtilities;

/**
 * This dialog allows the user to select a Derby database and convert it to MySQL
 *
 * @author	Michelle Bachler
 */
public class UIConvertFromDerbyDatabaseDialog extends UIDialog implements ActionListener, DBProgressListener {

	/** The pane to put this dialog's contents in.*/
	private Container		oContentPane = null;

	/** The parent frame for this node.*/
	private JFrame 			oParent = null;

	/** The list of Access database to convert.*/
	private UIProjectList	lstProjects	= null;

	/** The scrollpane to put the list of Access databases in.*/
	private JScrollPane		oScrollpane	= new JScrollPane();

	/** The button to start the conversion.*/
	private UIButton 		pbConvert	= null;

	/** The button to cancel the dialog.*/
	private UIButton		pbCancel	= null;

	/** The button to open the relevant help.*/
	private UIButton		pbHelp	= null;

	/** Holds the list of Derby projects to convert.*/
	private Vector			vtProjects	= new Vector();

	/** holds the list of existing MySQL database projects to check names against.*/
	private Vector			vtMySQLProjects	= new Vector();

	/** The progress dialog instance.*/
	private UIProgressDialog	oProgressDialog 	= null;

	/** The progress bar displayed in the progress dialog.*/
	private JProgressBar		oProgressBar 		= null;

	/** The progress thread class which runs the progress dialog.*/
	private ProgressThread		oThread 			= null;

	/** Counter used by the progress bar.*/
	private int					nCount = 0;

	/** Holds a reference to this class for use in inner threads.*/
	private UIConvertFromDerbyDatabaseDialog manager = null;

	/** The current MySQL connection profile object.*/
	private ExternalConnection oConnection = null;

	/** Database projects against their requirement to have their schemas updated.*/
	//private Hashtable		htProjectCheck		= new Hashtable(10);


	/**
	 * Constructor. Initializes and draws the contents of this dialog.
	 *
	 * @param parent the parent frame for this dialog.
	 * @param projects the list of current MySQL databse projects.
	 * @param sMySQLName the MySQL database username used to access MySQL.
	 * @param sMySQLPassword the MySQL database password used to access MySQL.
	 * @param sMySQLIP the MySQL database ip address or host name used to access MySQL.
	 */
	public UIConvertFromDerbyDatabaseDialog(JFrame parent, ExternalConnection connection, Vector projects) {
		//public UIConvertFromDerbyDatabaseDialog(JFrame parent, Hashtable htProjectCheck, ExternalConnection connection, Vector projects) {

		super(parent, true);

		oParent = parent;
		manager = this;
		this.oConnection = connection;
		//this.htProjectCheck = htProjectCheck;

		setTitle(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIConvertFromDerbyDatabaseDialog.convertDerby")); //$NON-NLS-1$

	    CSH.setHelpIDString(this,"basic.databases"); //$NON-NLS-1$

		oContentPane = getContentPane();
		oContentPane.setLayout(new BorderLayout());

		vtProjects = projects;
		
		vtMySQLProjects = ProjectCompendium.APP.getProjects();
			
		// create label and text box for model name
		JLabel label = new JLabel(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIConvertFromDerbyDatabaseDialog.chooseProjectToConvert")); //$NON-NLS-1$
		JPanel labelpanel = new JPanel();
		labelpanel.setBorder(new EmptyBorder(5,5,5,5));
		labelpanel.add(label);
		oContentPane.add(labelpanel, BorderLayout.NORTH);

		//lstProjects = new UIProjectList(htProjectCheck, vtProjects);
		lstProjects = new UIProjectList(vtProjects);
		oScrollpane = new JScrollPane(lstProjects);
		oScrollpane.setPreferredSize(new Dimension(300,200));

		JPanel listpanel = new JPanel();
		listpanel.setBorder(new EmptyBorder(5,5,5,5));
		listpanel.add(oScrollpane);

		oContentPane.add(listpanel, BorderLayout.CENTER);

		UIButtonPanel oButtonPanel = new UIButtonPanel();

		pbConvert = new UIButton(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIConvertFromDerbyDatabaseDialog.convertButton")); //$NON-NLS-1$
		pbConvert.setMnemonic(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIConvertFromDerbyDatabaseDialog.convertButtonMnemonic").charAt(0));
		pbConvert.addActionListener(this);
		getRootPane().setDefaultButton(pbConvert);
		oButtonPanel.addButton(pbConvert);

		pbCancel = new UIButton(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIConvertFromDerbyDatabaseDialog.cancelButton")); //$NON-NLS-1$
		pbCancel.setMnemonic(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIConvertFromDerbyDatabaseDialog.cancelButtonMnemonic").charAt(0));
		pbCancel.addActionListener(this);
		oButtonPanel.addButton(pbCancel);

		// Add help button
		pbHelp = new UIButton(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIConvertFromDerbyDatabaseDialog.helpButton")); //$NON-NLS-1$
		pbHelp.setMnemonic(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIConvertFromDerbyDatabaseDialog.helpButtonMnemonic").charAt(0));
		ProjectCompendium.APP.mainHB.enableHelpOnButton(pbHelp, "basics.databases-mysql", ProjectCompendium.APP.mainHS); //$NON-NLS-1$
		oButtonPanel.addHelpButton(pbHelp);

		oContentPane.add(oButtonPanel, BorderLayout.SOUTH);

		oProgressBar = new JProgressBar();
		oProgressBar.setMinimum(0);
		oProgressBar.setMaximum(100);

		setResizable(false);
		pack();
	}

	/**
	 * Handles a button push event.
	 * @param evt, the associated ACtionEvent.
	 */
	public void actionPerformed(ActionEvent evt) {

		Object source = evt.getSource();

		if ((source instanceof JButton)) {

			if (source.equals(pbConvert)) {
				onConvert();
			}
			else if (source.equals(pbCancel)) {
				onCancel();
			}
		}
	}

	/**
	 * Processes a request to convert the Derby database selected from the list.
	 */
	private void onConvert() {
		int index = lstProjects.getSelectedIndex();
		if (vtProjects.size() > 0) {
			String sName = (String)vtProjects.elementAt(index);
			processConversion(sName);
		}
		else {
			ProjectCompendium.APP.displayMessage(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIConvertFromDerbyDatabaseDialog.noDerbyProjectToConvert"), LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIConvertFromDerbyDatabaseDialog.noDerbyProjectToConvertTitle")); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	/**
	 * Processes a request to convert the Derby database with the given name.
	 *
	 * @param sName, the name of the Derby database to convert.
	 */
	private void processConversion(String sName) {

		boolean bNameExists = true;

		while(bNameExists) {
	   		String sNewName = JOptionPane.showInputDialog(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIConvertFromDerbyDatabaseDialog.enterName"), sName); //$NON-NLS-1$
			sNewName = sNewName.trim();

			bNameExists = false;
			if (!sNewName.equals("")) { //$NON-NLS-1$

				int count = vtMySQLProjects.size();
				for (int i=0; i<count; i++) {
					String next = (String)vtMySQLProjects.elementAt(i);
					if (next.equals(sNewName)) {
						bNameExists = true;
						break;
					}
				}

				if (!bNameExists) {
					final String sfFriendlyName = sName;
					final String sfToName = sNewName;
					Thread thread = new Thread("UIConvertDatabaseDialog") { //$NON-NLS-1$
						public void run() {
							setVisible(false);

							try {
								String sFromName = ProjectCompendium.APP.adminDerbyDatabase.getDatabaseName(sfFriendlyName);
								int status = ProjectCompendium.APP.adminDerbyDatabase.getSchemaStatusForDatabase(sFromName);

								/*
								int status = -1;
								if (htProjectCheck.containsKey(sfFriendlyName)) {
									status = ((Integer)htProjectCheck.get(sfFriendlyName)).intValue();
								}*/

								if (status == ICoreConstants.OLDER_DATABASE_SCHEMA) {
									if (!DatabaseUpdate.updateDatabase(ProjectCompendium.APP.adminDerbyDatabase, ProjectCompendium.APP, sFromName)) {
										return;
									}
								}
								else if (status == ICoreConstants.NEWER_DATABASE_SCHEMA) {
									ProjectCompendium.APP.displayMessage(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIConvertFromDerbyDatabaseDialog.newerVersionRequired")+"\n\n", LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIConvertFromDerbyDatabaseDialog.convertProjectTitle")); //$NON-NLS-1$ //$NON-NLS-2$
									return;
								}
								else if (status == -1) {
									ProjectCompendium.APP.displayMessage(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIConvertFromDerbyDatabaseDialog.message1")+"\n\n", LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIConvertFromDerbyDatabaseDialog.convertProjectTitle")); //$NON-NLS-1$ //$NON-NLS-2$
									return;
								}

								DBConvertDerbyToMySQLDatabase converter = new DBConvertDerbyToMySQLDatabase(ProjectCompendium.APP.adminDatabase, oConnection.getLogin(), oConnection.getPassword(), oConnection.getServer());
								
								converter.addProgressListener((DBProgressListener)manager);
								oThread = new ProgressThread(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIConvertFromDerbyDatabaseDialog.progressThreadTitle"), LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIConvertFromDerbyDatabaseDialog.progressThreadMessage")); //$NON-NLS-1$ //$NON-NLS-2$
								oThread.start();
								converter.copyDatabase(sFromName, sfToName, sfFriendlyName);
								ProjectCompendium.APP.updateProjects();
								ProjectCompendium.APP.onFileOpen();
								converter.removeProgressListener((DBProgressListener)manager);
							}
							catch (SQLException ex) {
								ex.printStackTrace();
								ProjectCompendium.APP.displayError(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIConvertFromDerbyDatabaseDialog.error1")+":\n\n"+ex.getMessage()); //$NON-NLS-1$
								ex.printStackTrace();
								progressComplete();
								return;
	  						}
							catch(ClassNotFoundException ex) {
								//ex.printStackTrace();
								ProjectCompendium.APP.displayError(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIConvertFromDerbyDatabaseDialog.error2")+":\n\n"+ex.getMessage()); //$NON-NLS-1$
								progressComplete();
								return;
							}
							catch(DBDatabaseNameException ex) {
								//ex.printStackTrace();
								ProjectCompendium.APP.displayError(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIConvertFromDerbyDatabaseDialog.error1")+":\n\n"+ex.getMessage()); //$NON-NLS-1$
								progressComplete();
								return;
							}
							catch(DBDatabaseTypeException ex) {
								//ex.printStackTrace();
								ProjectCompendium.APP.displayError(ex.getMessage());
								progressComplete();
								return;
							}
							catch(DBProjectListException ex) {
								ProjectCompendium.APP.displayError(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIConvertFromDerbyDatabaseDialog.error1")+":\n\n"+ex.getMessage()); //$NON-NLS-1$
								progressComplete();
								return;
							}
						}
					};
					thread.start();
				}
				else {
			   		JOptionPane.showMessageDialog(this, LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIConvertFromDerbyDatabaseDialog.projectNamedA")
			   				+" '"+sNewName+"' "
			   				+LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIConvertFromDerbyDatabaseDialog.projectNamedB")+"\n"
			   				+LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIConvertFromDerbyDatabaseDialog.projectNamedC")+"\n", //$NON-NLS-1$ //$NON-NLS-2$
										 LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIConvertFromDerbyDatabaseDialog.warning"),JOptionPane.WARNING_MESSAGE); //$NON-NLS-1$
				}
			}
		}
	}

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
	 * Set the amount of progress items being counted.
	 *
	 * @param int nCount, the amount of progress items being counted.
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
	 * @param int nIncrement, the current position of the progress in relation to the inital count
	 * @param String sMessage, the message to display to the user
	 */
    public void progressUpdate(int nIncrement, String sMessage) {
		nCount += nIncrement;
		oProgressBar.setValue(nCount);
		oProgressDialog.setMessage(sMessage);
		oProgressDialog.setStatus(nCount);
	}

	/**
	 * Indicate that progress has complete.
	 */
    public void progressComplete() {
		this.nCount = 0;
		oProgressDialog.setVisible(false);
		oProgressDialog.dispose();
	}

	/**
	 * Indicate that progress has had a problem.
	 *
	 * @param String sMessage, the message to display to the user.
	 */
    public void progressAlert(String sMessage) {
		progressComplete();
		ProjectCompendium.APP.displayError(sMessage);
	}
}
