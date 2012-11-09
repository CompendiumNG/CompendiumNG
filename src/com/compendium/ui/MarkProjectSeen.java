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

package com.compendium.ui;

import java.util.*;
import java.net.*;
import java.lang.*;
import java.awt.*;
import java.awt.event.*;
import java.applet.*;
import java.io.*;
import javax.swing.*;

import java.sql.SQLException;

import com.compendium.core.*;
import com.compendium.core.datamodel.*;
import com.compendium.core.db.*;
import com.compendium.core.db.management.DBConstants;
import com.compendium.core.db.management.DBProgressListener;
import com.compendium.core.datamodel.services.*;
import com.compendium.core.ICoreConstants;

import com.compendium.*;
import com.compendium.ui.*;
import com.compendium.ui.plaf.*;
import com.compendium.ui.dialogs.UIProgressDialog;

import org.w3c.dom.*;

/**
 * Creates a thread and progress bar for marking the project as seen.
 *
 * @author	M. Begeman
 * @version	1.0
 */
public class MarkProjectSeen extends Thread implements DBProgressListener{

	/** The IModel object for the current database connection.*/
	private IModel				oModel 				= null;

	/** The dialog which displays the progress bar.*/
	public static UIProgressDialog	oProgressDialog 	= null;

	/** The progress bar.*/
	public static JProgressBar		oProgressBar 		= null;

	/** The Thread class which runs the progress bar.*/
	private  ProgressThread	oThread 			= null;
	
	/** The counter used by the progress bar.*/
	private int					nCount = 0;
	

	/**
	 * Constructor.
	 *
	 */
	public MarkProjectSeen() {

		oModel = ProjectCompendium.APP.getModel();
	}

	/**
	 * Start the marking-seen thread and progress bar.
	 */
	public void run() {

		oThread = new ProgressThread();
		oThread.start();

		ProjectCompendium.APP.setStatus(LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "MarkProjectSeen.statusMessage")); //$NON-NLS-1$
		
		// Note to other developers: The approach taken here violates object encapsulation and will not work
		// if Compendium is ever split into a real client/server architecture.  For reasons of efficiency,
		// all the node-marking is handled as a low-level loop in DBNodeUserState.  However, the Progress Bar
		// is implemented/scoped here, and DBNodeUserState updates the Progress Bar by referencing the
		// oProgressBar and oProgressDialog objects declared here.  (Note, I tried declaring/managing the
		// progress bar stuff from DBNodeUserState, but it didn't work and I gave up trying to figure out why.)

 		try {
 			DBNodeUserState.addProgressListener(this);
 			oModel.getNodeService().vMarkProjectSeen(oModel.getSession());		// Does all the marking-seen work
 			DBNodeUserState.removeProgressListener(this);
 		}																		// (see DBNodeUserState.java) 		
 		catch (SQLException ex) {
 			DBNodeUserState.removeProgressListener(this);
 			progressComplete();
 		};
  		
		ProjectCompendium.APP.setStatus(""); //$NON-NLS-1$
		JOptionPane.showMessageDialog(null, LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "MarkProjectSeen.message1a")+"\n\n"+ //$NON-NLS-1$ //$NON-NLS-2$
				LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "MarkProjectSeen.message1b"), //$NON-NLS-1$
					LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "MarkProjectSeen.message1c"), JOptionPane.INFORMATION_MESSAGE); //$NON-NLS-1$
	
		ProjectCompendium.APP.onFileClose();
		ProjectCompendium.APP.onFileOpen();
	}


	/**
	 * The Thread class which draws the process bar dialog.
	 */
	private class ProgressThread extends Thread {

		public ProgressThread() {
	  		oProgressBar = new JProgressBar();
	  		oProgressBar.setMinimum(0);
			oProgressBar.setMaximum(100);
			
	  		oProgressDialog = new UIProgressDialog(ProjectCompendium.APP,LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "MarkProjectSeen.progressMessage"), LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "MarkProjectSeen.progressTitle")); //$NON-NLS-1$ //$NON-NLS-2$
	  		oProgressDialog.showDialog(oProgressBar, false);
		}

		public void run() {
	  		oProgressDialog.setVisible(true);
		}
	}
	
// IMPLEMENT PROGRESS LISTENER

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
