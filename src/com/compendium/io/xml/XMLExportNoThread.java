/********************************************************************************
 *                                                                              *
 *  (c) Copyright 2009 Verizon Communications USA and The Open University UK    *
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

package com.compendium.io.xml;

import java.util.*;
import java.lang.*;
import java.io.*;
import java.awt.*;
import java.util.zip.*;

import javax.swing.*;

import com.compendium.core.datamodel.*;
import com.compendium.core.datamodel.services.*;
import com.compendium.core.ICoreConstants;
import com.compendium.core.CoreUtilities;

import com.compendium.ProjectCompendium;

import com.compendium.ui.dialogs.*;
import com.compendium.ui.plaf.*;
import com.compendium.ui.*;

/**
 * XMLExport defines the export code, that allows the user to export Map/List Views to an XML document, but does not extend Thread.
 *
 * @author	Michelle Bachler
 */
public class XMLExportNoThread implements IUIConstants {

	/** Holds all the Links being exported.*/
	private	Vector				vtLinks 			= new Vector(51);

	/** Holds all the Codes being exported.*/
	private Vector				vtCodes 			= new Vector(51);

	/** Holds all the Nodes being exported.*/
	private Vector				vtNodes 			= new Vector(51);

	/** Holds all the Views being exported.*/
	private Vector				vtViews 			= new Vector(51);

	/** Holds a list of the meetings whose data to export.*/
	private Hashtable 			htMeetings			= new Hashtable(51);

	/** Holds all the parent views for a given node being exported - NOT USED.*/
	private Hashtable			htViews 			= new Hashtable(51);

	/** Holds processed Links for elimiating duplication on export.*/
	private Hashtable			htLinksCheck 		= new Hashtable(51);

	/** Holds processed Nodes for elimiating duplication on export.*/
	private	Hashtable			htNodesCheck 		= new Hashtable(51);

	/** Holds processed Codes for elimiating duplication on export.*/
	private Hashtable			htCodesCheck 		= new Hashtable(51);

	/** Holds processed Views for checking purposes */
	private Hashtable			htViewsCheck 		= new Hashtable(51);

	/** Holds processed Nodes for recursive full depth export check, to prevent infinite loop.*/
	private Hashtable			htCheckDepth 		= new Hashtable(51);

	/** The name of the file to export to.*/
	private String				sFilePath 			= null;

	/** Keeps the incremental total count for the progress bar.*/
	private int					nCount 				= 0;

	/** Indicates whether to export views to thier full depth (recursively).*/
	private boolean				bAllDepths 			= false;

	/** Indicates whether to export the selected nodes only.*/
	private boolean				bSelectedOnly 		= false;

	/** Indicates whether the export has been cancelled.*/
	private boolean 			bXMLExportCancelled = false;

	/** The IModel object for the current Session*/
	private IModel				oModel 				= null;

	/** The progress dialog instance.*/
	private UIProgressDialog	oProgressDialog 	= null;

	/** The progress bar displayed in the progress dialog.*/
	private JProgressBar		oProgressBar 		= null;

	/** The progress thread class which runs the progress dialog.*/
	private ProgressThread		oThread 			= null;

	/** The current View being exported.*/
	private View				oCurrentView 		= null;

	/** The UIViewFrame for the current View being exported.*/
	private UIViewFrame 		oUIViewFrame 		= null;

	/** The UIViewPane for the current View being exported - if the View is a MAP type.*/
	private UIViewPane			oUIViewPane 		= null;

	/** The UIList for the current View being exported - if the View is a LIST type.*/
	private UIList				oUIList 			= null;

	/** The zip file to store the export to.*/
	private File 		oZipfile				= null;

	/** A hashtable containing all externally referenced resources.*/
	private Hashtable 	htResources 		= new Hashtable(51);

	/** Indicates whether to export with external resources.*/
	private boolean 	bWithResources 		= false;

	/** Indicates whether link groups and stencil files shou;d also be included in and zip export.*/
	private boolean 	bWithStencilAndLinkGroups = false;

	/** Indicates whether Meeting / MediaIndex data should be included.*/
	private boolean 	bWithMeetings = false;

	/** The Name of the Backup*/
	private String 		sBackupName 		= "";

	/** The Path for the backup to be stored to.*/
	private String 		sBackupPath 		= "";

	/** The Path for the Resource files.*/
	private String 		sResourcePath 		= "";

	/** The platform specific file separator to use.*/
	private String		sFS 				= System.getProperty("file.separator");

	/** Indicated if one or more external resource files could not be found.*/
	private boolean 	bNotFound 			= false;

	/** Has this export failed or been stopped for some reason before completing.*/
	private boolean		bHasFailed			= false;

	private boolean 	bShowFinalMessage	= false;

	/** Has this export failed or been stopped for some reason before completing.*/
	private boolean		bExportComplete		= false;
		
	/**
	 * Constructor.
	 *
	 * @param UIViewFrame frame, the view being exported.
	 * @param String path, the path of the file to export to.
	 * @param boolean allDepths, indicates whether to export views to thier full depth (recursively).
	 * @param boolean selectedOnly, indicates whether to export the selected nodes only.
	 * @param boolean bWithResources, indicates whether to also export external resource files and store everything to a zip file
	 */
	public XMLExportNoThread(UIViewFrame frame, String path, boolean allDepths, boolean selectedOnly,
					boolean bWithResources, boolean bWithStencilAndLinkGroups, boolean bWithMeetings, 
					boolean bShowFinalMessage) {
		sFilePath = path;
		this.bWithResources = bWithResources;
		this.bWithStencilAndLinkGroups = bWithStencilAndLinkGroups;
		this.bWithMeetings = bWithMeetings;
		this.bShowFinalMessage = bShowFinalMessage;

		// IF EXPORTING RESOURCES, CREATE REQUIRED PATHS FOR LATER
		if (bWithResources) {
			oZipfile = new File(sFilePath);
			String sPath = oZipfile.getAbsolutePath();
			sBackupName = oZipfile.getName();

			int ind = sBackupName.lastIndexOf(".");
			if (ind != -1) {
				sBackupName = sBackupName.substring(0, ind);
			}
			
			int index = path.lastIndexOf(sFS);			
			if (index != -1) {
				sResourcePath = sPath.substring(0, index+1);
				
				String sDatabaseName = CoreUtilities.cleanFileName(ProjectCompendium.APP.sFriendlyName);						
				UserProfile oUser = ProjectCompendium.APP.getModel().getUserProfile();
				String sUserDir = CoreUtilities.cleanFileName(oUser.getUserName())+"_"+oUser.getId();
				sBackupPath = "Linked Files/"+sDatabaseName+"/"+sUserDir;			
			}
		}

		oUIViewFrame = frame;
		oCurrentView = frame.getView();

		bAllDepths = allDepths;
		bSelectedOnly = selectedOnly;

  		oProgressBar = new JProgressBar();
  		oProgressBar.setMinimum(0);
		oProgressBar.setMaximum(100);

		oModel = ProjectCompendium.APP.getModel();
		
		oThread = new ProgressThread();
		oThread.start();

		convertToXML();
		onCompletion();
		bExportComplete = true;		
	}

	/** Returns if the export has failed.*/
	public boolean exportCompleted() {
		return bExportComplete;
	}


	/** Returns if the export has failed.*/
	public boolean hasFailed() {
		return bHasFailed;
	}

	/**
	 * This class extends Thread and creates and shows the progress dialog.
	 */
	private class ProgressThread extends Thread {

		public ProgressThread() {
	  		oProgressDialog = new UIProgressDialog(ProjectCompendium.APP,"XML Export Progress..", "Export completed");
	  		oProgressDialog.showDialog(oProgressBar);
	  		oProgressDialog.setModal(true);
		}

		public void run() {
	  		oProgressDialog.setVisible(true);
		}
	}

	/**
	 * Checks the state of the progress dialog.
	 *
	 * @return boolean, true if the progress dialog has been cancelled, else false.
	 */
	private boolean checkProgress() {

	  	if (!bXMLExportCancelled && oProgressDialog.isCancelled()) {

			int result = JOptionPane.showConfirmDialog(oProgressDialog,
							"Do you want to Cancel the export?",
							"Cancel XML Export",
							JOptionPane.YES_NO_OPTION);

			if (result == JOptionPane.YES_OPTION) {
				bXMLExportCancelled = true;
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
	 * Clean up vector and hashtables when export completed.
	 */
	private void onCompletion() {

		htNodesCheck.clear();
		htCodesCheck.clear();
		htLinksCheck.clear();
		htMeetings.clear();
		vtNodes.removeAllElements();
		vtCodes.removeAllElements();
		vtLinks.removeAllElements();

		htNodesCheck = null;
		htCodesCheck = null;
		htLinksCheck = null;
		htMeetings = null;
		vtNodes = null;
		vtCodes = null;
		vtLinks = null;

		ProjectCompendium.APP.setDefaultCursor();
	}

	/**
	 * Convert Compendium node/s into xml output
	 */
	public void convertToXML() {

		StringBuffer root = new StringBuffer(1000);
		root.append("<?xml version=\"1.0\" encoding=\"UTF-16\"?>\n");

		//root.append("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n");
		root.append("<!DOCTYPE model SYSTEM \"Compendium.dtd\">\n");

		root.append("<model ");

		ProjectCompendium.APP.setWaitCursor();

		htNodesCheck.clear();
		htCodesCheck.clear();
		htLinksCheck.clear();
		htMeetings.clear();
		vtNodes.removeAllElements();
		vtCodes.removeAllElements();
		vtLinks.removeAllElements();

		try {
			if (oCurrentView != null) {

				root.append( "rootview=\""+oCurrentView.getId()+"\">\n");

				// PROCESS SELECTED NODES AND LINKS ONLY
				if (bSelectedOnly) {
					processSelectedNodesForExport();
				}
				else {	// PROCESS ALL NODES AND LINKS
					int count = 0;

					if (!bAllDepths)
						count = oCurrentView.getNumberOfNodes();
					else {
						nCount += 2;
						oProgressBar.setValue(nCount);
						oProgressDialog.setStatus(nCount);
						count = countDepth(oCurrentView);
					}

			  		oProgressBar.setMaximum(count+12);

					processNodeForExport(oCurrentView, oCurrentView.getParentNode());
				}
			}
		}
		catch(Exception ex) {
			ex.printStackTrace();
			ProjectCompendium.APP.displayError("Exception: (XMLExport.convertToXML) " + ex.getMessage());
			oProgressDialog.setVisible(false);
			oProgressDialog.dispose();
			ProjectCompendium.APP.setStatus("");
			bHasFailed = true;
			return;
		}

		if (bXMLExportCancelled || checkProgress()) {
			root = null;
			bHasFailed = true;
			return;
		}
		nCount += 3;
		oProgressBar.setValue(nCount);
		oProgressDialog.setStatus(nCount);

		root.append( processDataToXML() );

		if (bXMLExportCancelled || checkProgress()) {
			root = null;
			bHasFailed = true;
			return;
		}
		nCount +=3;
		oProgressBar.setValue(nCount);
		oProgressDialog.setStatus(nCount);

		root.append("</model>");

		// SAVE TO FILE
		if (bWithResources) {

			if (bWithStencilAndLinkGroups) {
				addLinkGroupsToResources();
				addStencilsToResources();
			}

			nCount += 3;
			oProgressBar.setValue(nCount);
			oProgressDialog.setStatus(nCount);

			nCount = 0;
			oProgressBar.setValue(0);
	  		oProgressBar.setMinimum(0);
			oProgressBar.setMaximum(htResources.size()+1);
			oProgressDialog.setMessage("Writing to zip..");
			oProgressDialog.setStatus(0);

			// ZIP ALL TOGETHER
			try {
				int BUFFER = 2048;
				BufferedInputStream origin = null;
				FileInputStream fi = null;

				FileOutputStream dest = new FileOutputStream(oZipfile.getAbsolutePath());
				ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(dest));
				out.setMethod(ZipOutputStream.DEFLATED);
				byte data2[] = new byte[BUFFER];

				ZipEntry entry = null;
				
				//ADD SQL FILE
				String sXMLFilePath = "Exports/"+sBackupName+".xml";
				String sqlFile = root.toString();
				// NEED TO WRITE MAIN XML FILE OUT TO FILE FIRST AS NEED TO ENCODE IT TO UTF16
				try {
					FileOutputStream fos = new FileOutputStream(sXMLFilePath);
					Writer out2 = new OutputStreamWriter(fos, "UTF16");
					out2.write(sqlFile);
					out2.close();
					
					fi = new FileInputStream(sXMLFilePath);
					origin = new BufferedInputStream(fi, BUFFER);
					entry = new ZipEntry(sXMLFilePath);
					out.putNextEntry(entry);

					int count = 0;
					while((count = origin.read(data2, 0, BUFFER)) != -1) {
						out.write(data2, 0, count);
					}
					origin.close();

					CoreUtilities.deleteFile(new File(sXMLFilePath));
					
					nCount +=1;
					oProgressBar.setValue(nCount);
					oProgressDialog.setStatus(nCount);					
				}
				catch (IOException e) {
					ProjectCompendium.APP.displayError("Exception:" + e.getMessage());
				}

				// ADD RESOURCES
				int count = 0;
				for (Enumeration e = htResources.keys(); e.hasMoreElements() ;) {
					String sOldFilePath = (String)e.nextElement();
					String sNewFilePath = (String)htResources.get(sOldFilePath);
					try {
						fi = new FileInputStream(sOldFilePath);
						origin = new BufferedInputStream(fi, BUFFER);

						entry = new ZipEntry(sNewFilePath);
						out.putNextEntry(entry);

						while((count = origin.read(data2, 0, BUFFER)) != -1) {
							out.write(data2, 0, count);
						}
						origin.close();

						nCount +=1;
						oProgressBar.setValue(nCount);
						oProgressDialog.setStatus(nCount);
					}
					catch (Exception ex) {
						System.out.println("Unable to backup database resource: \n\n"+sOldFilePath+"\n\n"+ex.getMessage());
					}
				}
				out.close();
			}
			catch(Exception e) {
				e.printStackTrace();
			}

			oProgressDialog.setVisible(false);
			oProgressDialog.dispose();

			if (sFilePath != null && bShowFinalMessage) {
				Thread thread = new Thread("XMLExport.convertToXML") {
					public void run() {
						String sMessage = "Finished exporting into " + sFilePath;
						if (bNotFound)
							sMessage += "\n\nOne or more reference files could not be found.\n\nPlease check the log file for details.";

						ProjectCompendium.APP.displayMessage(sMessage, "Export Finished");
					}
				};
				thread.start();
			}
		}

		// SAVE TO XML file
		else {
			try {
				FileOutputStream fos = new FileOutputStream(sFilePath);
				Writer out = new OutputStreamWriter(fos, "UTF16");
				out.write(root.toString());
				out.close();

				//FileWriter fileWriter = new FileWriter(sFilePath);
				//fileWriter.write(root.toString());
				//fileWriter.close();

				nCount += 3;
				oProgressBar.setValue(nCount);
				oProgressDialog.setStatus(nCount);

			}
			catch (IOException e) {
				ProjectCompendium.APP.displayError("Exception:" + e.getMessage());
			}

			oProgressDialog.setVisible(false);
			oProgressDialog.dispose();

			if (sFilePath != null && bShowFinalMessage) {
				Thread thread = new Thread("XMLExport.convertToXML") {
					public void run() {
						ProjectCompendium.APP.displayMessage("Finished exporting into " + sFilePath, "Export Finished");
					}
				};
				thread.start();
			}
		}

		ProjectCompendium.APP.setStatus("");
	}

	/**
	 * Load the link group file names into the htResources table.
	 */
	public void addLinkGroupsToResources() {

		File main = new File("System"+sFS+"resources"+sFS+"LinkGroups");
		File oLinkGroups[] = main.listFiles();
		String sOldLinkGroupPath = "";
		String sNewLinkGroupPath = "";
		File nextLinkGroup = null;

		for (int i=0; i< oLinkGroups.length; i++) {
			nextLinkGroup = oLinkGroups[i];
			sOldLinkGroupPath = nextLinkGroup.getAbsolutePath();
			if (!htResources.containsKey(sOldLinkGroupPath)) {
				sNewLinkGroupPath = "System"+sFS+"resources"+sFS+"LinkGroups"+sFS+ nextLinkGroup.getName();
				htResources.put(sOldLinkGroupPath, sNewLinkGroupPath);
			}
		}
	}

	/**
	 * Load the stencil files into the htResources table.
	 */
	public void addStencilsToResources() {

		String sStencilPath = "System"+sFS+"resources"+sFS+"Stencils/";
		File main = new File("System"+sFS+"resources"+sFS+"Stencils");
		File oStencils[] = main.listFiles();

		String sOldStencilName = "";
		String sStencilName = "";
		String sOldStencilImageName = "";
		String sStencilImageName = "";

		for (int i=0; i<oStencils.length; i++) {
			File nextStencil = oStencils[i];

			// EACH SEPARATE STENIL SET IS IN A SUBFOLDER
			if (nextStencil.isDirectory()) {

				String sSubStencilPath = sStencilPath+nextStencil.getName()+"/";
				File oStencilsSub[] = nextStencil.listFiles();

				for (int j=0; j<oStencilsSub.length; j++) {
					File nextSubStencil = oStencilsSub[j];

					// EACH STENCIL SET CONSTITS OF ONE XML FILE AND TWO DIRECTORIES OF IMAGES
					if (nextSubStencil.isDirectory()) {

						String sStencilImagePath = sSubStencilPath+nextSubStencil.getName()+"/";
						File oStencilImages[] = nextSubStencil.listFiles();

						for (int k=0; k<oStencilImages.length; k++) {
							File nextStencilImage = oStencilImages[k];

							sStencilImageName = nextStencilImage.getName();
							sOldStencilImageName = nextStencilImage.getAbsolutePath();
							if (!htResources.containsKey(sOldStencilImageName)) {
								sStencilImageName = sStencilImagePath + sStencilImageName;
								htResources.put(sOldStencilImageName, sStencilImageName);
							}
						}
					}
					else {
						sStencilName = nextSubStencil.getName();
						sOldStencilName = nextSubStencil.getAbsolutePath();
						if (!htResources.containsKey(sOldStencilName)) {
							sStencilName = sSubStencilPath + sStencilName;
							htResources.put(sOldStencilName, sStencilName);
						}
					}
				}
			}
		}
	}

	/**
 	 * Calculate the node depth level of the given view, that is, how may maps within maps within maps.
	 *
	 * @param View view, the view to check the depth of.
	 * @return int, an int representing the depth count for this view.
	 */
	public int countDepth(View view) {

		int count = 0;
		try {
			Vector nodePositions = oModel.getViewService().getNodePositions(oModel.getSession(), view.getId());
			count = nodePositions.size();

			for(Enumeration en = nodePositions.elements(); en.hasMoreElements();) {
				NodeSummary node = (NodeSummary)((NodePosition)en.nextElement()).getNode();

				if ( (node.getType() == ICoreConstants.MAPVIEW)
							|| (node.getType() == ICoreConstants.LISTVIEW) ) {

					if (!htCheckDepth.containsKey((Object)node.getId())) {
						htCheckDepth.put(node.getId(), node);
						count += countDepth((View) node);
					}
				}
			}
		}
		catch(Exception io) {}
		return count;
	}

	/**
	 * Process the currently selected nodes for export.
	 */
	private void processSelectedNodesForExport() {
		int count = 0;

		Enumeration nodes = null;
		Enumeration nodesForCount = null;
		Enumeration links = null;
		int numberOfNodes = 0;

		if (oUIViewFrame instanceof UIMapViewFrame) {
			oUIViewPane = ((UIMapViewFrame)oUIViewFrame).getViewPane();

			nodes = oUIViewPane.getSelectedNodes();
			nodesForCount = oUIViewPane.getSelectedNodes();

			links = oUIViewPane.getSelectedLinks();
			Vector selectedLinks = new Vector(51);
			for(Enumeration e = links; e.hasMoreElements();) {
				Link link = (Link)((UILink)e.nextElement()).getLink();
				selectedLinks.add(link);
			}
			links = selectedLinks.elements();

			numberOfNodes = oUIViewPane.getNumberOfSelectedNodes();
		}
		else {
			oUIList = ((UIListViewFrame)oUIViewFrame).getUIList();
			nodes = oUIList.getSelectedNodes();
			nodesForCount = oUIList.getSelectedNodes();
			numberOfNodes = oUIList.getNumberOfSelectedNodes();
		}

		// GET COUNT FOR PROGRESS BAR
		if (!bAllDepths)
			count += numberOfNodes;
		else {
			count += numberOfNodes;
			for(Enumeration e = nodesForCount; e.hasMoreElements();) {
				NodeSummary node = null;
				if (oUIViewFrame instanceof UIMapViewFrame)
					node = (NodeSummary)((UINode)e.nextElement()).getNode();
				else {
					NodePosition nodePos = (NodePosition)e.nextElement();
					node = nodePos.getNode();
				}

				if ( (node.getType() == ICoreConstants.MAPVIEW)
					|| (node.getType() == ICoreConstants.LISTVIEW) ) {
						htCheckDepth.put(node.getId(), node);
					count += countDepth((View) node);
				}
			}
		}
		oProgressBar.setMaximum(count+12);

		// IF THIS IS A MAP PROCESS LINKS
		if (links != null)
			processLinks( links, oCurrentView );

		processNodeSummary(oCurrentView);

		String sViewID = "";
		for(Enumeration e = nodes; e.hasMoreElements();) {

			NodePosition nodePos = null;
			if (oUIViewPane != null)
				nodePos = (NodePosition)((UINode)e.nextElement()).getNodePosition();
			else
				nodePos = (NodePosition)e.nextElement();

			NodeSummary node = nodePos.getNode();
			
			View nodeView = nodePos.getView();
			Date creationDate = nodePos.getCreationDate();
			long creationDateSecs = creationDate.getTime();

			Date modificationDate = nodePos.getModificationDate();
			long modificationDateSecs = modificationDate.getTime();

			Vector viewData = new Vector(18);
			sViewID = oCurrentView.getId();
			viewData.add((Object) sViewID);
			viewData.add((Object) node.getId());
			viewData.add((Object) new Integer(nodePos.getXPos()));
			viewData.add((Object) new Integer(nodePos.getYPos()));
			viewData.add((Object) new Long(creationDateSecs) );
			viewData.add((Object) new Long(modificationDateSecs) );			

			viewData.add((Object) new Boolean(nodePos.getShowTags()));
			viewData.add((Object) new Boolean(nodePos.getShowText()) );
			viewData.add((Object) new Boolean(nodePos.getShowTrans()) );
			viewData.add((Object) new Boolean(nodePos.getShowWeight()) );
			viewData.add((Object) new Boolean(nodePos.getShowSmallIcon()) );
			viewData.add((Object) new Boolean(nodePos.getHideIcon()) );		
			viewData.add((Object) new Integer(nodePos.getLabelWrapWidth()) );
			viewData.add((Object) new Integer(nodePos.getFontSize()) );
			viewData.add((Object) nodePos.getFontFace());
			viewData.add((Object) new Integer(nodePos.getFontStyle()) );
			viewData.add((Object) new Integer(nodePos.getForeground()) );
			viewData.add((Object) new Integer(nodePos.getBackground()) );
			
			vtViews.add((Object) viewData);
			htViewsCheck.put(sViewID, sViewID);

			processNodeForExport(node, oCurrentView);
		}
	}

	/**
	 * Process the given node for export
	 *
	 * @param NodeSummary nodeToExport, the top level node to export (usually a map or list).
	 * @param NodeSummary parentNode, the parent node to the node to exprt.
	 */
	public void processNodeForExport(NodeSummary nodeToExport, NodeSummary parentNode) {

		if (bXMLExportCancelled || checkProgress()) {
			bHasFailed = true;
			return;
		}

		nCount++;
		oProgressBar.setValue(nCount);
		oProgressDialog.setStatus(nCount);

		int sType = nodeToExport.getType();
		if ( (sType == ICoreConstants.MAPVIEW)
			|| (sType == ICoreConstants.LISTVIEW) ) {

			// HAVE I ALREADY ADDED THIS VIEW?
			if (!htNodesCheck.containsKey((Object)nodeToExport.getId())) {

				View view = (View)nodeToExport;
				try {
					if (!view.isMembersInitialized())
						view.initializeMembers();
				}
				catch(Exception ex) {
					System.out.println("Error: (XMLExport.processNodeForExport) \n\n"+ex.getMessage());
				}

				processNodeSummary(nodeToExport);

				// IF YOUR ABOUT TO PROCESS CHILD MAPS AND USER HAS SAID NO, DON'T
				if ( !nodeToExport.getId().equals(oCurrentView.getId()) && !bAllDepths)
					return;

				Enumeration links = view.getLinks();
				processLinks( links, view );

				String sViewID = "";
				Enumeration nodePositions = view.getPositions();
				for(Enumeration en = nodePositions; en.hasMoreElements();) {

					NodePosition nodePos = (NodePosition)en.nextElement();
					NodeSummary nodeSummary = nodePos.getNode();
					View nodeView = nodePos.getView();

					Date creationDate = nodePos.getCreationDate();
					long creationDateSecs = creationDate.getTime();

					Date modificationDate = nodePos.getModificationDate();
					long modificationDateSecs = modificationDate.getTime();

					Vector viewData = new Vector(18);
					sViewID = nodeView.getId();
					viewData.add((Object) sViewID);
					viewData.add((Object) nodeSummary.getId());
					viewData.add((Object) new Integer(nodePos.getXPos()));
					viewData.add((Object) new Integer(nodePos.getYPos()));
					viewData.add((Object) new Long(creationDateSecs) );
					viewData.add((Object) new Long(modificationDateSecs) );

					viewData.add((Object) new Boolean(nodePos.getShowTags()));
					viewData.add((Object) new Boolean(nodePos.getShowText()) );
					viewData.add((Object) new Boolean(nodePos.getShowTrans()) );
					viewData.add((Object) new Boolean(nodePos.getShowWeight()) );
					viewData.add((Object) new Boolean(nodePos.getShowSmallIcon()) );
					viewData.add((Object) new Boolean(nodePos.getHideIcon()) );		
					viewData.add((Object) new Integer(nodePos.getLabelWrapWidth()) );
					viewData.add((Object) new Integer(nodePos.getFontSize()) );
					viewData.add((Object) nodePos.getFontFace());
					viewData.add((Object) new Integer(nodePos.getFontStyle()) );
					viewData.add((Object) new Integer(nodePos.getForeground()) );
					
					viewData.add((Object) new Integer(nodePos.getBackground()) );
					
					vtViews.add((Object) viewData);
					htViewsCheck.put(sViewID, sViewID);
					
					processNodeForExport(nodeSummary, nodeView);
				}
			}
		}
		else if ( (sType != ICoreConstants.TRASHBIN)) {
			if (!htNodesCheck.containsKey((Object)nodeToExport.getId())) {
				processNodeSummary(nodeToExport);
			}
		}
	}

	/**
	 * Process the node given to extract the information required for export
	 *
	 * @param NodeSummary node, the node to process for export.
	 */
	private void processNodeSummary(NodeSummary nodeSummary) {

		// PROCESS LABEL AND DETAILS AND SOURCE THROUGH CHECK XML CHARS
		Vector nodeData = new Vector(20);

		String id = nodeSummary.getId();
		int type = nodeSummary.getType();
		String extendedType = nodeSummary.getExtendedNodeType();
		String sOriginalID = nodeSummary.getOriginalID();
		if (sOriginalID.equals("-1"))
			sOriginalID = "";

		String author = nodeSummary.getAuthor();
		author = CoreUtilities.cleanXMLText(author);

		Date creationDate = nodeSummary.getCreationDate();
		long creationDateSecs = creationDate.getTime();

		Date modificationDate = nodeSummary.getModificationDate();
		long modificationDateSecs = modificationDate.getTime();

		String label = nodeSummary.getLabel();
		label = CoreUtilities.cleanXMLText(label);
		
		String sLastModAuthor = nodeSummary.getLastModificationAuthor();
		sLastModAuthor = CoreUtilities.cleanXMLText(sLastModAuthor);
				
		Vector details = null;
		try {
			details = nodeSummary.getDetailPages(author);
			int state = nodeSummary.getState();

			String sSource = nodeSummary.getSource();
			String sSourceImage = nodeSummary.getImage();
			Dimension oImageSize = nodeSummary.getImageSize();						
			int nImageWidth = oImageSize.width;
			int nImageHeight = oImageSize.height;						
			String sBackground = "";
			if (nodeSummary instanceof View) {
				ViewLayer layer  = ((View)nodeSummary).getViewLayer();
				if (layer == null) {
					try { ((View)nodeSummary).initializeMembers();
						sBackground = layer.getBackground();
					}
					catch(Exception ex) {
						sBackground = "";
					}
				}
				else {
					sBackground = layer.getBackground();
				}
			}

			if (bWithResources) {
				if (!sBackground.equals("")) {
					File file3 = new File(sBackground);
					if (file3.exists()) {
						String sOldBackground = sBackground;
						if (!htResources.containsKey(sOldBackground)) {
							sBackground = sBackupPath + "/" + file3.getName();
							htResources.put(sOldBackground, sBackground);
						}
					}
					else if (sBackground != null && !sBackground.equals("")) {
						bNotFound = true;
						System.out.println("NOT FOUND ON EXPORT: "+sBackground);
					}
				}

				if (!sSource.equals("") && CoreUtilities.isFile(sSource)) {
					File file = new File(sSource);
					if (file.exists()) {
						String sOldSource = sSource;
						if (!htResources.containsKey(sOldSource)) {
							sSource = sBackupPath + "/" + file.getName();
							htResources.put(sOldSource, sSource);
						}
					}
					else if (sSource != null && !sSource.equals("")) {
						bNotFound = true;
						System.out.println("NOT FOUND ON EXPORT: "+sSource);
					}
				}
				if (!sSourceImage.equals("") && CoreUtilities.isFile(sSourceImage)) {
					File file2 = new File(sSourceImage);
					if (file2.exists()) {
						String sOldSourceImage = sSourceImage;
						if (!htResources.containsKey(sOldSourceImage)) {
							sSourceImage = sBackupPath + "/" + file2.getName();
							htResources.put(sOldSourceImage, sSourceImage);
						}
					}
					else if (sSourceImage != null && !sSourceImage.equals("")) {
						bNotFound = true;
						System.out.println("NOT FOUND ON EXPORT: "+sSourceImage);
					}
				}
			}

			sSource = CoreUtilities.cleanXMLText(sSource);
			sSourceImage = CoreUtilities.cleanXMLText(sSourceImage);
			sBackground = CoreUtilities.cleanXMLText(sBackground);

			//String parentID = "";
			//if (nodeSummary.getParentNode() != null)
			//	parentID = (nodeSummary.getParentNode()).getId();

			//int permission = nodeSummary.getPermission();

			Vector codes = processCodes( (Enumeration)nodeSummary.getCodes() );
			Vector shortcuts = nodeSummary.getShortCutNodes();
			if (shortcuts == null)
				shortcuts = new Vector(1);

			Vector vtMeetings = new Vector(1);
			try {
				vtMeetings = (oModel.getMeetingService()).getAllMediaIndexes(oModel.getSession(), id);
			}
			catch(Exception ex) {
				System.out.println("Unable to get media index data for node = "+id+"\nDue to:\n\n"+ex.getMessage());
			}

			//int viewCount = nodeSummary.getViewCount();

			nodeData.add((Object) id );
			nodeData.add((Object) new Integer(type) );
			nodeData.add((Object) extendedType );
			nodeData.add((Object) sOriginalID );
			nodeData.add((Object) author );
			nodeData.add((Object) new Long(creationDateSecs) );
			nodeData.add((Object) new Long(modificationDateSecs) );
			nodeData.add((Object) label );
			nodeData.add((Object) details );
			nodeData.add((Object) new Integer(state) );
			//nodeData.add((Object) parentID );
			//nodeData.add((Object) new Integer(permission) );

			nodeData.add((Object) sSource );
			nodeData.add((Object) sSourceImage );
			nodeData.add((Object) new Integer(nImageWidth) );
			nodeData.add((Object) new Integer(nImageHeight) );									
			nodeData.add((Object) sBackground );
			nodeData.add((Object) sLastModAuthor );

			nodeData.add((Object) codes );
			nodeData.add((Object) shortcuts );
			nodeData.add((Object) vtMeetings );
		}
		catch(Exception ex) {
			System.out.println("Error: (XMLExport.processNodeSummary) \n\n"+ex.getMessage());
		}

		if ( !htNodesCheck.containsKey((Object) id)) {
			htNodesCheck.put((Object) id, (Object) id);
			vtNodes.add((Object) nodeData);
		}
	}

	/**
	 * Process the links given to extract the information required for export
	 *
	 * @param Enumeration links, the list of links to process for export.
	 * @return Vector, containing the link data extracted
	 */
	private void processLinks(Enumeration links, View view) {

		String linkViewID = view.getId();

		for(Enumeration en = links; en.hasMoreElements();) {

			Link link = (Link)en.nextElement();
			Vector linkData = new Vector(10);

			String id = link.getId();
			String sLabel = CoreUtilities.cleanXMLText(link.getLabel());

			Date creationDate = link.getCreationDate();
			long creationDateSecs = creationDate.getTime();

			Date modificationDate = link.getModificationDate();
			long modificationDateSecs = modificationDate.getTime();

			String author = link.getAuthor();
			author = CoreUtilities.cleanXMLText(author);

			String linkType = link.getType();
			String sOriginalID = link.getOriginalID();
			if (sOriginalID.equals("-1"))
				sOriginalID = "";

			String linkFromID = (link.getFrom()).getId();
			String linkToID = (link.getTo()).getId();
			int arrow = link.getArrow();

			//int permission = link.getPermission();
			//String sOriginalID = link.getOriginalID() ??

			linkData.add((Object) id );
			linkData.add((Object) new Long(creationDateSecs) );
			linkData.add((Object) new Long(modificationDateSecs) );
			linkData.add((Object) author );
			linkData.add((Object) linkType );
			linkData.add((Object) sOriginalID );
			linkData.add((Object) linkFromID );
			linkData.add((Object) linkToID );
			linkData.add((Object) linkViewID );
			linkData.add((Object) sLabel );
			linkData.add((Object) new Integer(arrow) );

			//linkData.add((Object) new Integer(permission) );

			if ( !htLinksCheck.containsKey( id ) ) {
				Hashtable table = new Hashtable();
				table.put((Object)linkViewID, (Object)linkViewID);
				htLinksCheck.put(id, table);
				vtLinks.add((Object) linkData);
			}
			else{
				Hashtable table = (Hashtable)htLinksCheck.get(id);
				table.put((Object)linkViewID, (Object)linkViewID);
				htLinksCheck.put(id, table);
			}
		}
	}

	/**
	 * Process the codes given to extract the information required for export
	 *
	 * @param Enumeration codes, the list of code to process for export.
	 * @return Vector, containing the code data extracted
	 */
	private Vector processCodes(Enumeration codes) {

		// PROCESS NAME AND DESCRIPTION  THROUGH CHECK XML CHARS

		Vector codeIds = new Vector(10);

		for(Enumeration en = codes; en.hasMoreElements();) {

			Code code = (Code)en.nextElement();

			Vector codeData = new Vector(3);

			String id = code.getId();

			String author = code.getAuthor();
			author = CoreUtilities.cleanXMLText(author);

			Date creationDate = code.getCreationDate();
			long creationDateSecs = creationDate.getTime();

			Date modificationDate = code.getModificationDate();
			long modificationDateSecs = modificationDate.getTime();

			String codeName = code.getName();
			codeName = CoreUtilities.cleanXMLText(codeName);
			String codeDescription = code.getDescription();
			codeDescription = CoreUtilities.cleanXMLText(codeDescription);
			String codeBehavior = code.getBehavior();
			codeBehavior = CoreUtilities.cleanXMLText(codeBehavior);

			//int permission = code.getPermission();

			codeData.add((Object) id);
			codeData.add((Object) author );
			codeData.add((Object) new Long(creationDateSecs) );
			codeData.add((Object) new Long(modificationDateSecs) );
			codeData.add((Object) codeName );
			codeData.add((Object) codeDescription );
			codeData.add((Object) codeBehavior );
			//codeData.add((Object) new Integer(permission) );

			if ( !htCodesCheck.containsKey( id ) ) {
				htCodesCheck.put((Object)id, (Object)id);
				vtCodes.add((Object) codeData);
			}

			codeIds.add((Object) id);
		}
		return codeIds;
	}

	/**
	 * Process the data gathered into XML output
	 *
	 * @return String, the xml formatted string representing a Compendium map/list or group of nodes/links
	 */
	public String processDataToXML() {
		StringBuffer xml = new StringBuffer(1000);

		xml.append( processViewsToXML() );
		xml.append( processNodesToXML() );
		xml.append( processLinksToXML() );
		xml.append( processCodesToXML() );

		if (bWithMeetings) {
			xml.append( processMeetingsToXML() );
		}

		return xml.toString();
	}

	/**
	 * Process view information into XML output
	 *
	 * @return String, the xml formatted string representing views
	 */
	public String processViewsToXML() {

		StringBuffer xmlViews = new StringBuffer(500);

		/* VECTOR FOR REFERENCE
			0 = viewid
			1 = nodeid
			2 = xPos (Integer)
			3 = yPos (Integer)
			4 = created (Long)
			5 = lastModified (Long)
			6 = showTags
			7 = showText
			8 = showTrans
			9 = showWeight
			10 = smallNode
			11 = hideNode
			12 = wrapWidth
			13 = fontsize
			14 = fontface
			15 = fonstyle
			16 = foreground
			17 = background
		*/
		/* DATBASE 'ViewNode' TABLE FOR REFERENCE
			ViewID	= Text 50
			NodeID	= Text 50
			XPos	= Number Long Integer
			YPos	= Number Long Integer
			CreationDate		= Number Double
			ModificationDate	= Number Double
		*/

		xmlViews.append("\t<views>\n");

		Vector nextView= null;
		int count = vtViews.size();
		
		for (int i = 0; i < count; i++) {
	
			nextView = (Vector)vtViews.elementAt(i);
				
			xmlViews.append("\t\t<view ");
			
			xmlViews.append("viewref=\""+ (String)nextView.elementAt(0) +"\" ");
			xmlViews.append("noderef=\""+ (String)nextView.elementAt(1) +"\" ");
			xmlViews.append("XPosition=\""+ ((Integer)nextView.elementAt(2)).toString() +"\" ");
			xmlViews.append("YPosition=\""+ ((Integer)nextView.elementAt(3)).toString() +"\" " );
			xmlViews.append("created=\""+ ((Long)nextView.elementAt(4)).toString() +"\" ");
			xmlViews.append("lastModified=\""+ ((Long)nextView.elementAt(5)).toString() +"\" ");
			xmlViews.append("showTags=\""+ ((Boolean)nextView.elementAt(6)).toString() +"\" ");
			xmlViews.append("showText=\""+ ((Boolean)nextView.elementAt(7)).toString() +"\" ");
			xmlViews.append("showTrans=\""+ ((Boolean)nextView.elementAt(8)).toString() +"\" ");
			xmlViews.append("showWeight=\""+ ((Boolean)nextView.elementAt(9)).toString() +"\" ");
			xmlViews.append("smallIcon=\""+ ((Boolean)nextView.elementAt(10)).toString() +"\" ");
			xmlViews.append("hideIcon=\""+ ((Boolean)nextView.elementAt(11)).toString() +"\" ");
			xmlViews.append("labelWrapWidth=\""+ ((Integer)nextView.elementAt(12)).toString() +"\" ");	
			xmlViews.append("fontsize=\""+ ((Integer)nextView.elementAt(13)).toString() +"\" ");
			xmlViews.append("fontface=\""+ ((String)nextView.elementAt(14)) +"\" ");
			xmlViews.append("fontstyle=\""+ ((Integer)nextView.elementAt(15)).toString() +"\" ");
			xmlViews.append("foreground=\""+ ((Integer)nextView.elementAt(16)).toString() +"\" ");
			xmlViews.append("background=\""+ ((Integer)nextView.elementAt(17)).toString() +"\"");
			xmlViews.append(">\n\t\t</view>\n");
		}

		xmlViews.append("\t</views>\n");
		return xmlViews.toString();
	}

	/**
	 * Process node information into XML output
	 *
	 * @return String, the xml formatted string representing nodes
	 */
	public String processNodesToXML() {

		StringBuffer xmlNodes = new StringBuffer(500);

		/* VECTOR FOR REFERENCE
			0 = id
			1 = type (Integer)
			2 = extendedType
			3 = sOriginalID
			4 = author
			5 = creationDate (Long)
			6 = modificationDate (Long)
			7 = label
			8 = details (Vector)
			9 = state (Integer)
			10 = source
			11 = image
			12 = background image
			13 = sLastModAuthor
			14 = codes (Vector)
			15 = shortcuts (Vector)
			16 = meetings(Vector)
		*/
		/* DATABASE 'Node' TABLE FOR REFERENCE
			NodeID					= Text 50
			NodeType				= Number Byte
			ExtendedNodeType		= Text 50
			ImportedI	D			= Number Long Integer
			Author					= Text 50
			CreationDate			= Number Double
			ModificationDate		= Number Double
			Label					= Text 100
			Detail					= Memo
			CurrentStauts			= Integer
			sLastModAuthor			= Text 50

		  DATABASE 'ReferenceNode' TABLE
			NodeID			= Text 50
			Source			= Text 250
			ImageSource		= VARCHAR 255
			ImageWidth		= INT 11
			ImageHeight		= INT 11			

		  DATABASE 'ShortutNode' TABLE
			NodeID			= Text 50
			ReferenceID		= Text 50

		  DATABASE 'NodeCode' TABLE
			NodeID			= Text 50
			CodeID			= Text 50

		  DATABASE 'NodeDetail'
			NodeID				= Text 50
			UserID				= Text 50
			PageNo				= Integer
			CreationDate		= Number Double
			ModificationDate	= Number Double

		  DATABASE 'MediaIndex'
			ViewID 					= Text 50
			NodeID 					= Text 50
			MeetingID 				= Text 255
			MediaIndex 				= Number Double
			CreationDate			= Number Double
			ModificationDate		= Number Double

		*/

		xmlNodes.append("\t<nodes>\n");

		Vector nextNode = null;
		int counti = vtNodes.size();

		for (int i = 0; i < counti; i++) {
			nextNode = (Vector)vtNodes.elementAt(i);

			xmlNodes.append("\t\t<node ");

			xmlNodes.append("id=\""+ (String)nextNode.elementAt(0) +"\" ");
			xmlNodes.append("type=\""+ ((Integer)nextNode.elementAt(1)).toString() +"\" ");
			xmlNodes.append("extendedtype=\""+ (String)nextNode.elementAt(2) +"\" ");
			xmlNodes.append("originalid=\""+ (String)nextNode.elementAt(3) +"\" ");
			xmlNodes.append("author=\""+ (String)nextNode.elementAt(4) +"\" ");
			xmlNodes.append("created=\""+ ((Long)nextNode.elementAt(5)).toString() +"\" ");
			xmlNodes.append("lastModified=\""+ ((Long)nextNode.elementAt(6)).toString() +"\" ");
			xmlNodes.append("label=\"");			
			String label = (String)nextNode.elementAt(7);
			xmlNodes.append(label+"\" ");			
			xmlNodes.append("state=\""+ ((Integer)nextNode.elementAt(9)).toString() +"\" ");
			xmlNodes.append("lastModificationAuthor=\""+ ((String)nextNode.elementAt(15)) +"\"");			
			xmlNodes.append(">\n");

			xmlNodes.append("\t\t\t<details>\n");

			Vector details = (Vector)nextNode.elementAt(8);
			int count = details.size();
			String detail = "";
			for (int j=0; j<count; j++) {
				NodeDetailPage page = (NodeDetailPage)details.elementAt(j);
				detail = page.getText();

				if (detail.equals(ICoreConstants.NODETAIL_STRING) )
					detail = "";

				detail = CoreUtilities.cleanXMLText(detail);
				xmlNodes.append("\t\t\t\t<page ");
				xmlNodes.append("nodeid=\""+ page.getNodeID() +"\" ");
				xmlNodes.append("author=\""+ page.getAuthor() +"\" ");
				xmlNodes.append("created=\""+ new Long( (page.getCreationDate()).getTime() ).toString() +"\" ");
				xmlNodes.append("lastModified=\""+ new Long( (page.getModificationDate()).getTime() ).toString() +"\" ");
				xmlNodes.append("pageno=\""+ new Integer(page.getPageNo()).toString() +"\"");

				xmlNodes.append(">"+detail+"</page>\n");
			}
			xmlNodes.append("\t\t\t</details>\n");

			xmlNodes.append("\t\t\t<source>"+ (String)nextNode.elementAt(10) +"</source>\n");
			xmlNodes.append("\t\t\t<image width=\""+((Integer)nextNode.elementAt(12)).toString()+"\" height=\""+((Integer)nextNode.elementAt(13)).toString()+"\">"+ (String)nextNode.elementAt(11) +"</image>\n");
			xmlNodes.append("\t\t\t<background>"+ (String)nextNode.elementAt(14) +"</background>\n");

			xmlNodes.append("\t\t\t<coderefs>");

			Vector codes = (Vector)nextNode.elementAt(16);
			int countj = codes.size();
			for (int j=0; j<countj; j++) {
				if (codes.elementAt(j) instanceof String) {
					xmlNodes.append("\n\t\t\t\t<coderef coderef=\""+ (String)codes.elementAt(j) +"\" />");
				}
			}
			xmlNodes.append("\n\t\t\t</coderefs>\n");

			xmlNodes.append("\t\t\t<shortcutrefs>");
			Vector shorts = (Vector)nextNode.elementAt(17);
			int countk = shorts.size();
			for (int k=0; k<countk; k++) {
				if (shorts.elementAt(k) instanceof NodeSummary) {
					xmlNodes.append("\n\t\t\t\t<shortcutref shortcutref=\""+ ((NodeSummary)shorts.elementAt(k)).getId() +"\" />");
				}
			}
			xmlNodes.append("\n\t\t\t</shortcutrefs>\n");

			if (bWithMeetings) {
				xmlNodes.append("\t\t\t<mediaindexes>");
				Vector meetings = (Vector)nextNode.elementAt(18);
				int countl = meetings.size();

				String sMeetingID = "";
				String sMeetingMapID = "";
				for (int l=0; l<countl; l++) {
					if (meetings.elementAt(l) instanceof MediaIndex) {
						MediaIndex mediaIndex = (MediaIndex)meetings.elementAt(l);
						sMeetingID = mediaIndex.getMeetingID();
						sMeetingMapID = mediaIndex.getViewID();
						if (htViewsCheck.containsKey(sMeetingMapID)) {
							xmlNodes.append("\n\t\t\t\t<mediaindex mediaindex=\""+mediaIndex.getMediaIndex().getTime()+"\" ");
							xmlNodes.append("noderef=\""+(String)nextNode.elementAt(0)+"\" ");
							xmlNodes.append("viewref=\""+sMeetingMapID+"\" ");
							xmlNodes.append("meetingref=\""+sMeetingID+"\" ");
							xmlNodes.append("created=\""+mediaIndex.getCreationDate().getTime()+"\" ");
							xmlNodes.append("lastModified=\""+mediaIndex.getModificationDate().getTime()+"\" />");
							htMeetings.put(sMeetingID, sMeetingID);
						}
					}
				}
				xmlNodes.append("\n\t\t\t</mediaindexes>\n");
			}

			/*
			xmlNodes.append("\t\t\t<views>\n");
			Vector views = htViews.get((Object)(String)nextNode.elementAt(0));
			int countm = views.size();
			for (int m=0; m<countm; m++) {
				Vector next = views.elementAt(m);
				xmlNodes.append("\t\t\t\t<view noderef=\""+next.elementAt(0)+"\" XPosition=\""+next.elementAt(2)+"\" YPosition=\""+next.elementAt(3)+"\" />\n");
			}
			xmlNodes.append("\t\t\t</views>\n");
			*/

			xmlNodes.append("\t\t</node>\n");
		}

		xmlNodes.append("\t</nodes>\n");

		return xmlNodes.toString();		
	}
	
	/**
	 * Process link information into XML output
	 *
	 * @return String, the xml formatted string representing links
	 */
	public String processLinksToXML() {

		StringBuffer xmlLinks = new StringBuffer(500);

		/* VECTOR FOR REFERENCE
			0 = id
			1 = creationDate (Long)
			2 = modificationDate (Long)
			3 = author
			4 = linkType (Integer)
			5 = sOriginalID
			6 = linkFromID
			7 = linkToID
			8 = linkViewID
			9 = sLabel
			10 = arrow
		*/
		/* DATABASE 'Link' TABLE FOR REFERENCE
			LinkID				= VarChar 50
			CreationDate		= Number Double
			ModificationDate	= Number Double
			Author				= VarChar 50
			Type				= VarChar 50
			OriginalID			= VarChar 50
			FromNode			= VarChar 50
			ToNode				= VarChar 50
			Label				= Memo
			arrow				= Number Double
		*/

		xmlLinks.append("\t<links>");

		Vector nextLink = null;
		int count = vtLinks.size();

		for (int i = 0; i < count; i++) {
			nextLink = (Vector)vtLinks.elementAt(i);
			String fromID = (String)nextLink.elementAt(6);
			String toID = (String)nextLink.elementAt(7);
			String sID = (String)nextLink.elementAt(0);

			// ONLY ADD THE LINK IF BOTH NODES HAVE BEEN SELECTED/ADDED
			if (htNodesCheck.containsKey((Object)fromID)
					&& htNodesCheck.containsKey((Object)toID) ) {

				xmlLinks.append("\n\t\t<link ");

				xmlLinks.append("id=\""+ sID +"\" ");
				xmlLinks.append("created=\""+ ((Long)nextLink.elementAt(1)).toString() +"\" " );
				xmlLinks.append("lastModified=\""+ ((Long)nextLink.elementAt(2)).toString() +"\" " );
				xmlLinks.append("author=\""+ (String)nextLink.elementAt(3) +"\" " );
				xmlLinks.append("type=\""+ (String)nextLink.elementAt(4) +"\" " );
				xmlLinks.append("originalid=\""+ (String)nextLink.elementAt(5) +"\" ");
				xmlLinks.append("from=\""+ fromID +"\" ");
				xmlLinks.append("to=\""+ toID +"\" ");
				xmlLinks.append("label=\""+ (String)nextLink.elementAt(9) +"\" " );
				xmlLinks.append("arrow=\""+ ((Integer)nextLink.elementAt(10)).toString() +"\">" );
				xmlLinks.append("\n\t\t\t<linkviews>");

				if ( htLinksCheck.containsKey( sID ) ) {
					Hashtable table = (Hashtable)htLinksCheck.get(sID);
					for (Enumeration e = table.keys(); e.hasMoreElements();) {
						String viewid = (String)e.nextElement();
						xmlLinks.append("\n\t\t\t\t<linkview id=\""+viewid+"\"/>");
					}
				}

				xmlLinks.append("\n\t\t\t</linkviews>");
				xmlLinks.append("\n\t\t</link>\n");
			}
		}

		xmlLinks.append("\t</links>\n");
		return xmlLinks.toString();
	}

	/**
	 * Process code information into XML output
	 *
	 * @return String, the xml formatted string representing codes
	 */
	public String processCodesToXML() {

		StringBuffer xmlCodes = new StringBuffer(500);

		/* VECTOR FOR REFERENCE
			0 = id
			1 = author
			2 = creationDate (Long)
			3 = modificationDate (Long)
			4 = codeName
			5 = codeDescription
			6 = codeBehavior
		*/
		/* DATBASE 'Code' TABLE FOR REFERENCE
			CodeID				= Text 50
			Author				= Text 50
			CreationDate		= Number Double
			ModificationDate	= Number Double
			CodeName			= Text 50
			CodeDescription		= Text 100
			CodeBehaviour		= Text 255
		*/

		xmlCodes.append("\t<codes>\n");

		Vector nextCode= null;
		int count = vtCodes.size();

		for (int i = 0; i < count; i++) {
			nextCode = (Vector)vtCodes.elementAt(i);

			xmlCodes.append("\t\t<code ");

			xmlCodes.append("id=\""+ (String)nextCode.elementAt(0) +"\" ");
			xmlCodes.append("author=\""+ (String)nextCode.elementAt(1) +"\" ");
			xmlCodes.append("created=\""+ ((Long)nextCode.elementAt(2)).toString() +"\" ");
			xmlCodes.append("lastModified=\""+ ((Long)nextCode.elementAt(3)).toString() +"\" " );
			xmlCodes.append("name=\""+ (String)nextCode.elementAt(4) +"\" ");
			xmlCodes.append("description=\""+ (String)nextCode.elementAt(5) +"\" ");
			xmlCodes.append("behavior=\""+ (String)nextCode.elementAt(6) +"\" />\n");
		}

		xmlCodes.append("\t</codes>\n");
		return xmlCodes.toString();
	}

	/**
	 * Process meeting information into XML output
	 *
	 * @return String, the xml formatted string representing meetings
	 */
	public String processMeetingsToXML() {

		StringBuffer xmlMeetings = new StringBuffer(500);

		/* VECTOR FOR REFERENCE
			0 = id
			1 = mapid
			2 = meetingname
			3 = meeting date (Long)
			4 = status (Integer)
		*/
		/* DATBASE 'Meeting' TABLE FOR REFERENCE
			MeetingID			= Text 255
			MeetingMapID		= Text 50
			MeetingName			= Text 255
			MeetingDate			= Number Double
			CurrentStatus		= Integer
		*/

		xmlMeetings.append("\t<meetings>\n");

		Vector vtAllMeetings = null;
		try {
			vtAllMeetings = (oModel.getMeetingService()).getMeetings(oModel.getSession());
		}
		catch(Exception ex) {
			ex.printStackTrace();
			System.out.flush();
			ProjectCompendium.APP.displayError("Meeting data could not be loaded due to:\n\n"+ex.getMessage());
			return new String("");
		}

		int count = vtAllMeetings.size();

		Meeting nextMeeting= null;

		for (int i = 0; i < count; i++) {

			nextMeeting = (Meeting)vtAllMeetings.elementAt(i);

			String meetingid = (String)nextMeeting.getMeetingID();

			if (htMeetings.containsKey(meetingid)) {
				xmlMeetings.append("\t\t<meeting ");

				xmlMeetings.append("meetingref=\""+ meetingid +"\" ");
				xmlMeetings.append("meetingmapref=\""+ (String)nextMeeting.getMeetingMapID() +"\" ");
				xmlMeetings.append("meetingname=\""+ (String)CoreUtilities.cleanXMLText(nextMeeting.getName()) +"\" ");
				xmlMeetings.append("meetingdate=\""+ ((Date)nextMeeting.getStartDate()).getTime() +"\" ");
				xmlMeetings.append("currentstatus=\""+ new Integer(nextMeeting.getStatus()).toString() +"\" />\n" );
			}
		}

		xmlMeetings.append("\t</meetings>\n");
		return xmlMeetings.toString();
	}
}
