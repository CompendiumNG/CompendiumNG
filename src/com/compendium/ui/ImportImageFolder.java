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
import java.sql.SQLException;

import javax.swing.*;

import com.compendium.core.datamodel.*;
import com.compendium.core.datamodel.services.*;
import com.compendium.core.ICoreConstants;

import com.compendium.*;
import com.compendium.ui.*;
import com.compendium.ui.plaf.*;
import com.compendium.ui.dialogs.UIProgressDialog;

/**
 * The ImportImageFolder class imports a folder of images and creates reference nodes for them.
 *
 * @author	Michelle Bachler
 * @version	1.0
 */
public class ImportImageFolder extends Thread {

	/** the view to import into if it is a map.*/
	private ViewPaneUI			oViewPaneUI = null;

	/** The view to import into if it is an image.*/
	private UIList				uiList = null;

	/** The dialog that holds the progress bar.*/
	private UIProgressDialog	oProgressDialog = null;

	/** The progress bar.*/
	private JProgressBar		oProgressBar = null;

	/** The thread that runs the progress bar.*/
	private ProgressThread		oThread = null;

	/**
	 * Constrcutor. Does nothing.
	 */
	public ImportImageFolder() {}

	/**
	 * Creates the progress bar and starts the import process.
	 * @see #importImages
	 */
	public void run() {

  		oProgressBar = new JProgressBar();
  		oProgressBar.setMinimum(0);

		ProjectCompendium.APP.setWaitCursor();
        importImages();
		ProjectCompendium.APP.setDefaultCursor();

		oProgressDialog.setVisible(false);
		oProgressDialog.dispose();

 	}

	/**
	 * Set the ViewPaneUI when the view to import into is a map.
	 * @param viewpaneUI com.compendium.ui.plaf.ViewPaneUI, the view to import into.
	 */
	public void setViewPaneUI(ViewPaneUI viewpaneUI) {
		oViewPaneUI = viewpaneUI;
	}

	/**
	 * Set the UIList when the view to import into is a list.
	 * @param viewpaneUI com.compendium.ui.UIList, the view to import into.
	 */
	public void setUIList(UIList list) {
		uiList = list;
	}

	/**
	 * The thread class that runs the progress dialog.
	 */
	private class ProgressThread extends Thread {

		public ProgressThread() {
	  		oProgressDialog = new UIProgressDialog(ProjectCompendium.APP,LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "ImportImageFolder.importProgress"), LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "ImportImageFolder.importProgressTitle")); //$NON-NLS-1$ //$NON-NLS-2$
	  		oProgressDialog.showDialog(oProgressBar);
	  		oProgressDialog.setModal(true);
		}

		public void run() {
	  		oProgressDialog.setVisible(true);
		}
	}

	/**
	 * Check if the user has cancelled the import.
	 * @return boolean, true if the user has cancelled the import.
	 */
	private boolean checkProgress() {

	  	if (oProgressDialog.isCancelled()) {

			int result = JOptionPane.showConfirmDialog(oProgressDialog,
							LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "ImportImageFolder.cancelImportMessage"), //$NON-NLS-1$
							LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "ImportImageFolder.cancelImportTitle"), //$NON-NLS-1$
							JOptionPane.YES_NO_OPTION);

			if (result == JOptionPane.YES_OPTION) {
				oProgressDialog.setVisible(false);
				return true;
			}
	  		else {
				oProgressDialog.setCancelled(false);
			  return false;
	  		}
		}
		return false;
	}

	/**
	 * Import the images from the folder the suer selects.
	 */
	private void importImages() {

		String sAuthor = ProjectCompendium.APP.getModel().getUserProfile().getUserName();
		
		JFileChooser fileDialog = new JFileChooser();
		fileDialog.setDialogTitle(LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "ImportImageFolder.selectImage")); //$NON-NLS-1$
		fileDialog.setApproveButtonText(LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "ImportImageFolder.loadButton")); //$NON-NLS-1$

		fileDialog.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		fileDialog.setAcceptAllFileFilterUsed(false);		
		
		UIUtilities.centerComponent(fileDialog, ProjectCompendium.APP);
		int retval = fileDialog.showOpenDialog(ProjectCompendium.APP);
		if (retval == JFileChooser.APPROVE_OPTION) {
			if ((fileDialog.getSelectedFile()) != null) {

				/*String file = fileDialog.getSelectedFile().getName();
				if (file == null) {
					return;
				}*/

		    	File chosenDir = fileDialog.getSelectedFile();		    

				//File dir = fileDialog.getCurrentDirectory();
				if (chosenDir == null)
					return;

				UIViewFrame viewFrame = ProjectCompendium.APP.getCurrentFrame();
				boolean isMap = false;
				ViewPaneUI oViewPaneUI = null;
				ListUI listUI = null;
				UIList uiList = null;
				if (viewFrame instanceof UIMapViewFrame) {
					oViewPaneUI = ((UIMapViewFrame)viewFrame).getViewPane().getUI();
					isMap = true;
				}
				else {
					uiList = ((UIListViewFrame)viewFrame).getUIList();
					listUI = uiList.getListUI();
				}

				int xPos = 30;
				int yPos = 20;
				int nX = 0;

				File images[] = chosenDir.listFiles();

				oThread = new ProgressThread();
				oThread.start();
				int count = images.length;
				oProgressBar.setMaximum(count);
				int ImageCount = 0;

				for (int i=0; i< images.length; i++) {

					if (checkProgress())
						return;

					File nextImage = images[i];
					String imageName = nextImage.getName();

					if ( UIImages.isImage(imageName) ) {
						if (isMap) {
							UINode uinode = oViewPaneUI.createNode(ICoreConstants.REFERENCE,
													 "", //$NON-NLS-1$
													 sAuthor,
													 imageName,
													 "", //$NON-NLS-1$
													 xPos,
													 yPos
													 );
							yPos = yPos + 120;

							try {
								uinode.getNode().setSource("", nextImage.getPath(), sAuthor); //$NON-NLS-1$
								uinode.setReferenceIcon(nextImage.getPath());
							}
							catch(Exception ex) {}
						}
						else {
							int nY = (listUI.getUIList().getNumberOfNodes() + 1) * 10;

							NodePosition np = listUI.createNode(ICoreConstants.REFERENCE,
											 "", //$NON-NLS-1$
											 sAuthor,
											 imageName,
											 "", //$NON-NLS-1$
											 nX,
											 nY
											 );

							try { np.getNode().setSource("", nextImage.getPath(), sAuthor); } //$NON-NLS-1$
							catch(Exception ex) {}

							uiList.updateTable();
							uiList.selectNode(uiList.getNumberOfNodes() - 1, ICoreConstants.MULTISELECT);
						}
					}

					ImageCount = ImageCount+1;
					oProgressBar.setValue(ImageCount);
					oProgressDialog.setStatus(ImageCount);
				}
			}
		}
	}
}