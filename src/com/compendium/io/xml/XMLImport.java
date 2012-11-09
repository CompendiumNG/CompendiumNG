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
import java.net.*;
import java.lang.*;
import java.awt.*;
import java.awt.event.*;
import java.applet.*;
import java.io.*;
import javax.swing.*;


import com.compendium.core.*;
import com.compendium.core.datamodel.*;
import com.compendium.core.db.*;
import com.compendium.core.datamodel.services.*;
import com.compendium.core.ICoreConstants;

import com.compendium.*;
import com.compendium.ui.*;
import com.compendium.ui.plaf.*;
import com.compendium.ui.dialogs.UIProgressDialog;

import org.w3c.dom.*;

/**
 * XMLImport imports Compendium views and nodes from an xml text file conforming to the Compendium.dtd.
 *
 * @author	Michelle Bachler
 * @version	1.0
 */
public class XMLImport extends Thread {

	// FOR PROGRESS BAR
	/** A count of the total number of nodes being imported for use with the progress bar counter.*/
	private int					nNumberOfNodes 		= 0;

	/** A count of the total number of links being imported for use with the progress bar counter.*/
	private int					nNumberOfLinks 		= 0;

	/** A count of the total number of codes being imported for use with the progress bar counter.*/
	private int 				nNumberOfCodes 		= 0;

	/** A count of the total number of shortcuts being imported for use with the progress bar counter.*/
	private int 				nNumberOfShorts 	= 0;

	/** A count of the total number of meetings being imported for use with the progress bar counter.*/
	private int 				nNumberOfMeetings 	= 0;

	/** A count of the total number of mediaindexes being imported for use with the progress bar counter.*/
	private int 				nNumberOfMediaIndexes = 0;

	/** The current count of the number of nodes processed so far.*/
	private int 				nNodeCount 			= 0;

	/** The current count of the number of links processed so far.*/
	private int					nLinkCount 			= 0;

	/** The current count of the number of codes processed so far.*/
	private int					nCodeCount 			= 0;

	/** The current count of the number of shortcuts processed so far.*/
	private int					nShortCount 		= 0;

	/** The current count of the number of meetings processed so far.*/
	private int					nMeetingCount 		= 0;

	/** The current count of the number of mediaindexes processed so far.*/
	private int					nMediaIndexCount 	= 0;

	/** The IModel object for the current database connection.*/
	private IModel				oModel 				= null;

	/** The current Session object.*/
	private PCSession 			oSession 			= null;

	/** The current parent view being imported into.*/
	private IView				oView 				= null;

	/** The current map (if there is one), being imported into.*/
	private ViewPaneUI			oViewPaneUI 		= null;

	/** The current list (if there is one), being imported into.*/
	private UIList				oUIList 			= null;

	/** The file name of the xml file to import.*/
	private String				sFileName			= "";

	/** The id of the root view in the import data.*/
	private String 				sRootView 			= "";

	/** Holds the author name of the current user.*/
	private String				sCurrentAuthor		= "";

	/** true if the import should preserve the date and author information, false if it should add todays date and author.*/
	private boolean 			bIsSmartImport 		= false;

	/** Indicates if the main view being imported into is a List View (false = map).*/
	private boolean 			bIsListImport 		= false;

	/** True if the imported date and author information should be added to the node detail text, else false.*/
	private boolean 			bIncludeInDetail 	= false;

	/** Indicates if the user has cancelled the import.*/
	private boolean 			bXMLImportCancelled = false;

	// FOR UNDOING ON CANCEL
	/** Holds a list of nodes added so far incase user cancels and we need to undo.*/
	private Vector 				vtNodeList 			= new Vector();

	/** Holds a list of links added so far incase the user cancels and we need to undo.*/
	private Vector 				vtLinkList 			= new Vector();

	// FOR DATA PROCESSING
	/** The list of nodes to process.*/
	private Hashtable 			htNodes		 		= new Hashtable(51);

	/** The list of view to process.*/
	private Hashtable 			htViews 			= new Hashtable(51);

	/** The list of codes created so far.*/
	private Hashtable 			htCodes 			= new Hashtable(51);

	/** List of all new nodes created in the database against thier id numbers, for checking.*/
	private Hashtable 			htNewNodes 			= new Hashtable(51);

	/** Th list of view/node relationship info.*/
	private Hashtable 			htNodeView 			= new Hashtable(51);

	/** The list of UINodes created so far.*/
	private Hashtable 			htUINodes 			= new Hashtable(51);

	/** The list of shortcuts to process.*/
	private Vector 				vtShortcuts 		= new Vector(51);

	/** The list of meetings to process.*/
	private Vector 				vtMeetings	 		= new Vector(51);

	// FOR PROGRESS BAR
	/** Used by the progress bar system */
	private JOptionPane			oOptionPane 		= null;

	/** The dialog which displays the progress bar.*/
	private UIProgressDialog	oProgressDialog 	= null;

	/** The progress bar.*/
	private JProgressBar		oProgressBar 		= null;

	/** The Thread class which runs the progress bar.*/
	private ProgressThread		oThread 			= null;
	
	/** The user Id of the current user */
	private String sUserID = "";

	/**
	 * Constructor.
	 *
	 * @param debug, - not curerntly used.
	 * @param fileName, the name of the xml file to import.
	 * @param model com.compendium.core.datamodel.IModel, the current model object.
	 * @param view com.compendium.core.datamodel.IView, the view we are importing into.
	 * @param isSmartImport, true if the import should preserve the date and author information,
	 * false if it should add todays date and author.
	 * @param includeDetail, true if the imported date and author information should
	 * be added to the node detail text, else false.
	 */
	public XMLImport(boolean debug, String fileName, IModel model, IView view, boolean isSmartImport,
						boolean includeInDetail) {

		sFileName = fileName;
		this.oModel = model;
		if (oModel == null)
			oModel = ProjectCompendium.APP.getModel();

		this.sCurrentAuthor = oModel.getUserProfile().getUserName();
		this.oSession = oModel.getSession();
		this.sUserID = oModel.getUserProfile().getId();
		
		this.oView = view;
		this.bIsSmartImport = isSmartImport;

		this.bIncludeInDetail = includeInDetail;

		if (oView.getType() == ICoreConstants.LISTVIEW) {
			bIsListImport = true;
		} else {
			bIsListImport = false;
		}
	}

	/**
	 * Start the import thread and progress bar, and calls <code>createDom</code>
	 * to begin the import.
	 */
	public void run() {

  		oProgressBar = new JProgressBar();
  		oProgressBar.setMinimum(0);
		oProgressBar.setMaximum(100);

		// MAKES IT STOP WORKING IF I USE?
		oThread = new ProgressThread();
		oThread.start();

		DBNode.setImporting(true);
        createDom( sFileName );
		DBNode.restoreImportSettings();

		ProjectCompendium.APP.setTrashBinIcon();

		oProgressDialog.setVisible(false);

		ProjectCompendium.APP.scaleAerialToFit();

		oProgressDialog.dispose();
	}

	/**
	 * Set the main <code>ViewPaneUI</code> object that will contain the import.
	 * @param viewpaneUI com.compendium.ui.plaf.ViewPaneUI, the map view to import into.
	 */
	public void setViewPaneUI(ViewPaneUI viewpaneUI) {
		oViewPaneUI = viewpaneUI;
	}

	/**
	 * Set the main <code>UIList</code> object that will contain the import.
	 * @param list com.compendium.ui.UIList, the list view to import into.
	 */
	public void setUIList(UIList list) {
		oUIList = list;
	}

	/**
	 * The Thread class which draws the process bar dialog.
	 */
	private class ProgressThread extends Thread {

		public ProgressThread() {
	  		oProgressDialog = new UIProgressDialog(ProjectCompendium.APP,"XML Import Progress..", "Import completed");
	  		oProgressDialog.showDialog(oProgressBar, false);
	  		oProgressDialog.setModal(true);
		}

		public void run() {
	  		oProgressDialog.setVisible(true);
		}
	}

	/**
	 * Return the count of the current number of elements processed
	 */
	private int getCurrentCount() {
		return (nNodeCount+nLinkCount+nCodeCount+nShortCount+nMeetingCount+nMediaIndexCount);
	}

	/**
	 * Load the XML from the given uri and read it.
	 * Object the loaded document and process it.
	 *
	 * @param uri, the uri of the XML document to load/process.
	 */
    private void createDom( String uri ){

        try {
			XMLReader reader = new XMLReader();
			Document document = reader.read(uri, true);			
			if (document != null) {
				processDocument( document );
			} else {
				ProjectCompendium.APP.displayError("Exception: Your document cannot be imported.\n");	
			}
			document = null;
        }
		catch ( Exception e ) {
			ProjectCompendium.APP.displayError("Exception: Your document cannot be imported.\n");
			e.printStackTrace();
        }
    }

	//*************************************************************************************************//

	/**
	 * Process the given XML document and create all the view, nodes, links, codes etc required.
	 * @param document, the XML Document object to process.
	 */
	private void processDocument( Document document ) {

		vtNodeList.removeAllElements();
		vtLinkList.removeAllElements();
		vtShortcuts.removeAllElements();
		vtMeetings.removeAllElements();

		htNewNodes.clear();
		htNodes.clear();
		htViews.clear();
		htCodes.clear();
		htNodeView.clear();
		htUINodes.clear();

 		ProjectCompendium.APP.setWaitCursor();

		Node node = document.getDocumentElement();

		NamedNodeMap attrs = node.getAttributes();
		Attr oRootView = (Attr)attrs.getNamedItem("rootview");
		sRootView = oRootView.getValue();

		// PRE-PROCESS VIEWS
		NodeList views = document.getElementsByTagName("view");
		int countj = views.getLength();
		for (int j=0; j< countj; j++) {

			Node view = views.item(j);
			attrs = view.getAttributes();

			Attr oViewID = (Attr)attrs.getNamedItem("viewref");
			String viewid = oViewID.getValue();
			Attr oNodeID = (Attr)attrs.getNamedItem("noderef");
			String nodeid = oNodeID.getValue();
			Attr oXPos = (Attr)attrs.getNamedItem("XPosition");
			String xPos = oXPos.getValue();
			Attr oYPos = (Attr)attrs.getNamedItem("YPosition");
			String yPos = oYPos.getValue();

			Attr oCreated = (Attr)attrs.getNamedItem("created");
			Long created = new Long(0);
			if (oCreated != null) {
				created = new Long(oCreated.getValue());
			}

			Attr oLastModified = (Attr)attrs.getNamedItem("lastModified");
			Long lastModified = new Long(0);
			if (oLastModified != null) {
				lastModified = new Long(oLastModified.getValue());
			}
			
			Model model = (Model)oModel;
						
			Attr oShowTags = (Attr)attrs.getNamedItem("showTags");
			Boolean bShowTags = null;
			if (oShowTags != null) {
				bShowTags = new Boolean(oShowTags.getValue());
			} else {
				bShowTags = new Boolean(model.showTagsNodeIndicator);
			}

			Attr oShowText = (Attr)attrs.getNamedItem("showText");
			Boolean bShowText = null;
			if (oShowText != null) {
				bShowText = new Boolean(oShowText.getValue());
			} else {
				bShowText = new Boolean(model.showTextNodeIndicator);
			}

			Attr oShowTrans = (Attr)attrs.getNamedItem("showTrans");
			Boolean bShowTrans = null;
			if (oShowTrans != null) {
				bShowTrans = new Boolean(oShowTrans.getValue());
			} else {
				bShowTrans = new Boolean(model.showTransNodeIndicator);
			}

			Attr oShowWeight = (Attr)attrs.getNamedItem("showWeight");
			Boolean bShowWeight = null;
			if (oShowWeight != null) {
				bShowWeight = new Boolean(oShowWeight.getValue());
			} else {
				bShowWeight = new Boolean(model.showWeightNodeIndicator);
			}

			Attr oSmallIcon = (Attr)attrs.getNamedItem("smallIcon");
			Boolean bSmallIcon = null;
			if (oSmallIcon != null) {
				bSmallIcon = new Boolean(oSmallIcon.getValue());
			} else {
				bSmallIcon = new Boolean(model.smallIcons);
			}

			Attr oHideIcon = (Attr)attrs.getNamedItem("hideIcon");
			Boolean bHideIcon = null;
			if (oHideIcon != null) {
				bHideIcon = new Boolean(oHideIcon.getValue());
			} else {
				bHideIcon = new Boolean(model.hideIcons);
			}
			
			Attr oWrapWidth = (Attr)attrs.getNamedItem("labelWrapWidth");
			Integer nWrapWidth = null;
			if (oWrapWidth != null) {
				nWrapWidth = new Integer(oWrapWidth.getValue());
			} else {
				nWrapWidth = new Integer(model.labelWrapWidth);
			}
			
			Attr oFontSize = (Attr)attrs.getNamedItem("fontsize");
			Integer nFontSize = null;
			if (oFontSize != null) {
				nFontSize = new Integer(oFontSize.getValue());
			} else {
				nFontSize = new Integer(model.fontsize);
			}

			Attr oFontFace = (Attr)attrs.getNamedItem("fontface");
			String sFontFace = "";			
			if (oFontFace != null) {
				sFontFace = oFontFace.getValue();	
			} else {
				sFontFace = model.fontface;
			}
			
			Attr oFontStyle = (Attr)attrs.getNamedItem("fontstyle");
			Integer nFontStyle = null;
			if (oFontStyle != null) {
				nFontStyle = new Integer(oFontStyle.getValue());
			} else {
				nFontStyle = new Integer(model.fontstyle);
			}
			
			Attr oForeground = (Attr)attrs.getNamedItem("foreground");
			Integer nForeground = null;
			if (oForeground != null) {
				nForeground = new Integer(oForeground.getValue());
			} else {
				nForeground = new Integer(Model.FOREGROUND_DEFAULT.getRGB());
			}
			
			Attr oBackground = (Attr)attrs.getNamedItem("background");
			Integer nBackground = null;
			if (oBackground != null) {
				nBackground = new Integer(oBackground.getValue());
			} else {
				nBackground = new Integer(Model.BACKGROUND_DEFAULT.getRGB());
			}				
			
			Vector nodePos = new Vector(18);
			nodePos.add(viewid);
			nodePos.add(nodeid);
			nodePos.add(xPos);
			nodePos.add(yPos);
			nodePos.add(created);
			nodePos.add(lastModified);

			nodePos.add(bShowTags);
			nodePos.add(bShowText);
			nodePos.add(bShowTrans);
			nodePos.add(bShowWeight);
			nodePos.add(bSmallIcon);								
			nodePos.add(bHideIcon);
			nodePos.add(nWrapWidth);
			nodePos.add(nFontSize);
			nodePos.add(sFontFace);
			nodePos.add(nFontStyle);
			nodePos.add(nForeground);
			nodePos.add(nBackground);
			
			if (!htViews.containsKey((Object) viewid))
				htViews.put((Object) viewid, (Object) new Vector(51));

			Vector nextView = (Vector)htViews.get((Object) viewid);
			nextView.add( (Object) nodePos );
			htViews.put((Object) viewid, (Object) nextView);
		}

		// PRE-PROCESS NODES
		NodeList nodes = document.getElementsByTagName("node");
		int counti = nodes.getLength();
		for (int i=0; i< counti; i++) {
			Node innernode = nodes.item(i);
			attrs = innernode.getAttributes();
			Attr oID = (Attr)attrs.getNamedItem("id");
			String nodeid = oID.getValue();
			htNodes.put((Object) nodeid, (Object) innernode);
		}

		NodeList codes = document.getElementsByTagName("code");
		NodeList links = document.getElementsByTagName("link");
		NodeList mediaindexes = document.getElementsByTagName("mediaindex");

		// FOR PROGRESS BAR ONLY
		NodeList shorts = document.getElementsByTagName("shortcutref");
		NodeList meetings = document.getElementsByTagName("meeting");

		// INITIALISE THE PROGRESS BARS MAXIMUM
		nNumberOfNodes = counti;
		nNumberOfCodes = codes.getLength();
		nNumberOfLinks = links.getLength();
		nNumberOfShorts = shorts.getLength();
		nNumberOfMeetings = meetings.getLength();
		nNumberOfMediaIndexes = meetings.getLength();
  		oProgressBar.setMaximum(nNumberOfNodes+nNumberOfLinks+nNumberOfCodes+nNumberOfShorts+nNumberOfMeetings+nNumberOfMediaIndexes);

		// NEED TO DO THIS BEFORE VIEWS ARE PROCESSED SO CODE OBJECTS HAVE BEEN CREATED
		processCodes( codes );

		processView( sRootView, this.oView, this.oModel );

		// NEED TO DO THIS AFTER VIEWS HAVE BEEN PROCESSED SO NODE OBJECTS HAVE BEEN CREATED
		processLinks( links );

		// NEED TO DO THIS AFTER VIEWS HAVE BEEN PROCESSED SO NODE OBJECTS HAVE BEEN CREATED
		processShortcuts();

		// NEED TO DO THIS BEFORE MEDIAINDEXES ARE PROCESSED AS THEY NEED TO BE REFERENCED BY THEM
		processMeetings(meetings);

		// NEED TO DO THIS AFTER NODES ARE PROCESSED AS THEY NEED TO REFERENCE THEM
		processMediaIndexes(mediaindexes);

		// INITIALIZE THE NEW VIEW NODES SO THEIR NODE WEIGHT INICATION NUMBERS REFRESH CORRECTLY LATER
		int countk = htUINodes.size();
		int nType = 0;
		UINode oCheckNode = null;
		for(Enumeration e = htUINodes.elements();e.hasMoreElements();) {
			Object obj = e.nextElement();
			if (obj instanceof UINode) {
				oCheckNode = (UINode)obj;
				nType = oCheckNode.getType();
				if(nType == ICoreConstants.MAPVIEW || nType == ICoreConstants.MAP_SHORTCUT ||
						nType == ICoreConstants.LISTVIEW || nType == ICoreConstants.LIST_SHORTCUT) {
					try {
						((View)oCheckNode.getNode()).initializeMembers();
					}
					catch(Exception io) {}
				}
			}
		}

		//?WHY - FIX FOR MAP WEIGHT PROBLEM? IF NOT NOT NEEDED NOW AND IT DELETE HEIGHTLIGHTING SO REMOVE.
		// NEED TO THINK ABOUT IF THIS WAS FOR SOMETHING ELSE AS WELL?
		/*JInternalFrame[] frames = ProjectCompendium.APP.getDesktop().getAllFrames();
		int i=0;
		String trashbinID = ProjectCompendium.APP.getTrashBinID();
		while(i<frames.length) {
			UIViewFrame viewFrame = (UIViewFrame)frames[i++];
			View innerview = viewFrame.getView();

			if (innerview != null) {

				if (viewFrame instanceof UIMapViewFrame) {

					UIViewPane pane = ((UIMapViewFrame)viewFrame).getViewPane();
					UINode trashbin = (UINode)pane.get(trashbinID);

					innerview.setIsMembersInitialized(false);
					try {
						innerview.initializeMembers();
					}
					catch(Exception io) {}

					if (trashbin != null)
						innerview.addMemberNode(trashbin.getNodePosition());

					((UIMapViewFrame)viewFrame).createViewPane((View)innerview);
				}
				else {
					innerview.setIsMembersInitialized(false);
					try {
						innerview.initializeMembers();
					}
					catch(Exception io) {}
					((UIListViewFrame)viewFrame).getUIList().updateTable();
				}
			}
		}*/

		ProjectCompendium.APP.refreshIconIndicators();
		ProjectCompendium.APP.setDefaultCursor();
	}

	/**
	 * Process the XML NodeList containing code data.
	 *
	 * @param codes, the XML NodeList of code data to process.
	 */
	private void processCodes( NodeList codes ) {

		int count = codes.getLength();
       	for ( int i = 0; i < count; i++ ) {
			Node code = codes.item(i);

   	  		NamedNodeMap attrs = code.getAttributes();

			String id = ((Attr)attrs.getNamedItem("id")).getValue();
			String author = ((Attr)attrs.getNamedItem("author")).getValue();
			long created = new Long( ((Attr)attrs.getNamedItem("created")).getValue() ).longValue();
			long lastModified = new Long( ((Attr)attrs.getNamedItem("lastModified")).getValue() ).longValue();
			String name = ((Attr)attrs.getNamedItem("name")).getValue();
			String description = ((Attr)attrs.getNamedItem("description")).getValue();
			String behavior = ((Attr)attrs.getNamedItem("behavior")).getValue();

			Date modificationDate = null;
			Date creationDate = null;

			if (bIsSmartImport) {
				creationDate = new Date();
				creationDate.setTime(created);
				modificationDate = new Date();
				modificationDate.setTime(lastModified);
			}
			else {
				author = oModel.getUserProfile().getUserName();
				creationDate = new Date();
				modificationDate = creationDate;
			}

			// ADD TO DATABASE "Code" TABLE
			try {
				// DO I NEED TO KEEP ORIGINAL ID IF TRANCLUSION SET ??
				String codeId = oModel.getUniqueID();

				Code codeObj = null;

				// CHECK IF ALREADY IN DATABASE
				Vector existingCodesForName = (oModel.getCodeService()).getCodeIDs(oSession, name);

				if (existingCodesForName.size() == 0) {
					codeObj = oModel.getCodeService().createCode(oSession, codeId, author, creationDate,
														 modificationDate, name, description, behavior);

					oModel.addCode(codeObj);
					htCodes.put((Object)id, (Object)codeObj);
				}
				else {
					String existingCodeID = (String)existingCodesForName.elementAt(0);
					codeObj = oModel.getCodeService().getCode(oSession, existingCodeID);
					htCodes.put((Object)id, (Object)codeObj);
				}

				nCodeCount++;
				oProgressBar.setValue(getCurrentCount());
				oProgressDialog.setStatus(getCurrentCount());
			}
			catch(Exception ex) {
				ProjectCompendium.APP.displayError("Exception: (XMLImport.processCodes) " + ex.getMessage());
			}
		}
	}

	/**
	 * Process the XML NodeList containing meeting data.
	 *
	 * @param codes, the XML NodeList of meeting data to process.
	 */
	private void processMeetings( NodeList meetings ) {

		int count = meetings.getLength();

       	for ( int i = 0; i < count; i++ ) {

			Node meeting = meetings.item(i);
   	  		NamedNodeMap attrs = meeting.getAttributes();

			String sMeetingID = ((Attr)attrs.getNamedItem("meetingref")).getValue();
			String sMeetingMapID = ((Attr)attrs.getNamedItem("meetingmapref")).getValue();
			String sMeetingName = ((Attr)attrs.getNamedItem("meetingname")).getValue();
			long meetingdate = new Long( ((Attr)attrs.getNamedItem("meetingdate")).getValue() ).longValue();
			int nStatus = new Integer( ((Attr)attrs.getNamedItem("currentstatus")).getValue() ).intValue();

			Date dMeetingDate = new Date(meetingdate);

			// ADD TO DATABASE "Meeting" TABLE
			try {
				oModel.getMeetingService().createMeeting(oSession, sMeetingID, sMeetingMapID, sMeetingName, dMeetingDate, nStatus);

				nMeetingCount++;
				oProgressBar.setValue(getCurrentCount());
				oProgressDialog.setStatus(getCurrentCount());
			}
			catch(Exception ex) {
				ProjectCompendium.APP.displayError("Exception: (XMLImport.processMeetings) " + ex.getMessage());
			}
		}
	}

	/**
	 * Process the view with the given view id.
	 *
	 * @param viewid, the id of the view to process.
	 * @param view com.compendium.datamodel.IView, the parent view.
	 * @param oModel com.compendium.datamodel.IModel, - NOT CURRENTLY USED.
	 */
	private void processView( String viewid, IView view, IModel model ) {

		// DO NOT CONITUNE IF EMPTY VIEW
		if (!htViews.containsKey((Object) viewid))
			return;

		Vector innerviews = new Vector(51);
		Vector nodes = (Vector)htViews.get( (Object) viewid );
		int counti = nodes.size();
		Date creationDate = null;
		Date modificationDate = null;

		for (int i=0; i<counti; i++) {

			Vector node = (Vector)nodes.elementAt(i);
			Object nodeid = node.elementAt(1);
			
			int xPos = new Integer((String)node.elementAt(2)).intValue();
			int yPos = new Integer((String)node.elementAt(3)).intValue();
			long lCreationDate = ((Long)node.elementAt(4)).longValue();
			long lModificationDate = ((Long)node.elementAt(5)).longValue();

			creationDate = new Date();
			creationDate.setTime(lCreationDate);
			modificationDate = new Date();
			modificationDate.setTime(lModificationDate);

			boolean bShowTags = ((Boolean)node.elementAt(6)).booleanValue();
			boolean bShowText = ((Boolean)node.elementAt(7)).booleanValue();
			boolean bShowTrans = ((Boolean)node.elementAt(8)).booleanValue();
			boolean bShowWeight = ((Boolean)node.elementAt(9)).booleanValue();
			boolean bSmallIcon = ((Boolean)node.elementAt(10)).booleanValue();
			boolean bHideIcon = ((Boolean)node.elementAt(11)).booleanValue();
			int nWrapWidth = ((Integer)node.elementAt(12)).intValue();
			int nFontSize = ((Integer)node.elementAt(13)).intValue();
			String sFontFace = (String)node.elementAt(14);
			int nFontStyle = ((Integer)node.elementAt(15)).intValue();
			int nForeground = ((Integer)node.elementAt(16)).intValue();
			int nBackground = ((Integer)node.elementAt(17)).intValue();			
			
			//ProjectCompendium.APP.displayError("Processing node = "+nodeid);

			// IF THIS NODE HAS ALREADY BEEN ADDED OR IT IS AN INNER REFERENCE TO THE ROOT VIEW
			if ( (!htNewNodes.containsKey(nodeid)) && !nodeid.equals(sRootView) ) {

				Node nextnode = (Node)htNodes.get(nodeid);
				if (nextnode != null) {
					
					processNode( model, view, nextnode, xPos, yPos, viewid, creationDate, modificationDate, 
							bShowTags, bShowText, bShowTrans, bShowWeight, bSmallIcon, bHideIcon,
							nWrapWidth, nFontSize, sFontFace, nFontStyle, nForeground, nBackground);

					NodeSummary newNode = (NodeSummary)htNewNodes.get((Object)nodeid);
					int nodeType = newNode.getType();

					if (nodeType == ICoreConstants.MAPVIEW
								|| nodeType == ICoreConstants.LISTVIEW ) {

						//|| nodeType == ICoreConstants.MAP_SHORTCUT
						//|| nodeType == ICoreConstants.LIST_SHORTCUT)

						innerviews.add((Object)nodeid);
					}
				}

				//set the node count for progress bar
				nNodeCount++;
				oProgressBar.setValue(getCurrentCount());
				oProgressDialog.setStatus(getCurrentCount());
			}
			else {
				processNodeView( viewid, (String)nodeid, xPos, yPos, creationDate, modificationDate, 
						bShowTags, bShowText, bShowTrans, bShowWeight, bSmallIcon, bHideIcon,
						nWrapWidth, nFontSize, sFontFace, nFontStyle, nForeground, nBackground);
			}
		}

		int countj = innerviews.size();
		for (int j=0; j< countj; j++) {

			String nextviewid = (String)innerviews.elementAt(j);
			View nextView = (View) htNewNodes.get((Object)nextviewid);

			// ONLY PROCESS VIEWS CONTENTS IF IT IS NOT A TRANSCLUSION
			// IF THE NEXTVIEW ID MATCHES THE VIEW NODE ID, THEN ITS BEEN TRANSCLUDED
			// BUT CHECK YOUR NOT PRESERVING IDS
			if ( !nextviewid.equals(nextView.getId()) || DBNode.getPreserveImportedIds() ) {
				processView (nextviewid, nextView, model );
			}
		}
	}

	/**
	 * Process the given view and node id data to create the view/node relationship.
	 *
	 * @param viewid, the id of the view the node with the given node id is in.
	 * @param nodeidm, the id of the node to add to the view with the given view id.
	 * @param xPos, the x position to add this node at in the view.
	 * @param yPos, the y position to add this node at in the view.
	 * @param transCreationDate, the date the node was put into the view.
	 * @param transModDate, the date the view-node data was last modified.
	 * @param bShowTags true if this node has the tags indicator draw.
	 * @param bShowText true if this node has the text indicator drawn
	 * @param bShowTrans true if this node has the transclusion indicator drawn
	 * @param bShowWeight true if this node has the weight indicator displayed
	 * @param bSmallIcon true if this node is using a small icon
	 * @param bHideIcons true if this node is not displaying its icon
	 * @param nWrapWidth the node label wrap width used for this node in this view.
	 * @param nFontSize	the font size used for this node in this view
	 * @param sFontFace the font face used for this node in this view
	 * @param nFontStyle the font style used for this node in this view
	 * @param nForeground the foreground color used for this node in this view
	 * @param nBackground the background color used for this node in this view. 
	 */
	private void processNodeView( String viewid, String nodeid, int xPos, int yPos, Date transCreationDate, 
			Date transModDate, boolean bShowTags, boolean bShowText, boolean bShowTrans, boolean bShowWeight, 
			boolean bSmallIcon, boolean bHideIcon, int nWrapWidth, int nFontSize, String sFontFace, 
			int nFontStyle, int nForeground, int nBackground) {

		NodeSummary thisnode = null;
		if (nodeid.equals(sRootView))
			thisnode = (NodeSummary)this.oView;
		else if (htNewNodes.containsKey((Object)nodeid))
			thisnode = (NodeSummary)htNewNodes.get((Object)nodeid);
		else
			return;

		View thisview = null;
		if (htNewNodes.containsKey((Object)viewid))
			thisview = (View)htNewNodes.get(viewid);
		else
			thisview = (View)this.oView; // SHOULD NEVER HAPPEN

		IViewService vs = null;
		IModel thismodel = thisview.getModel();
		if (thismodel != null)
  			vs = thisview.getModel().getViewService();
				
		NodePosition nodePos = null;
		try {
			nodePos = vs.addMemberNode(thismodel.getSession(), thisview, thisnode, xPos, 
					yPos, transCreationDate, transModDate, bShowTags, bShowText, bShowTrans, 
					bShowWeight, bSmallIcon, bHideIcon,	nWrapWidth, nFontSize, sFontFace, 
					nFontStyle, nForeground, nBackground);
			nodePos.initialize(oSession, oModel);
		}
		catch (Exception ex) {
			ProjectCompendium.APP.displayError("Exception: (XMLImport.processNodeView) " + ex.getMessage());
		}

		// STORE NODEPOSITION FOR USE WITH LINKS
		if (!htNodeView.containsKey((Object) viewid))
			htNodeView.put((Object) viewid, (Object) new Hashtable(51));

		Hashtable nextView = (Hashtable)htNodeView.get((Object) viewid);
		nextView.put( (Object) nodeid, (Object) nodePos );
		htNodeView.put( (Object) viewid, (Object) nextView);
	}

	/**
	 * Process the given node data and create, as required, the associated NodeSummary objects and additional data.
	 *
	 * @param oModel com.compendium.datamodel.IModel, - NOT CURRENTLY USED.
	 * @param view com.compendium.datamodel.IView, the view to add the node to.
	 * @param node, the XML Node of Node data to process.
	 * @param xPos, the x position to add this node at.
	 * @param yPos, the y position to add this node at.
	 * @param currentViewId, the id of the current view being imported into.
	 * @param transCreationDate, the date the node was put into the view.
	 * @param transModDate, the date the view-node data was last modified.
	 * @param bShowTags true if this node has the tags indicator draw.
	 * @param bShowText true if this node has the text indicator drawn
	 * @param bShowTrans true if this node has the transclusion indicator drawn
	 * @param bShowWeight true if this node has the weight indicator displayed
	 * @param bSmallIcon true if this node is using a small icon
	 * @param bHideIcons true if this node is not displaying its icon
	 * @param nWrapWidth the node label wrap width used for this node in this view.
	 * @param nFontSize	the font size used for this node in this view
	 * @param sFontFace the font face used for this node in this view
	 * @param nFontStyle the font style used for this node in this view
	 * @param nForeground the foreground color used for this node in this view
	 * @param nBackground the background color used for this node in this view.
	 */
	private void processNode( IModel model, IView view, Node node, int xPos, int yPos, String currentViewId, 
			Date transCreationDate, Date transModDate, boolean bShowTags, boolean bShowText, boolean bShowTrans, boolean bShowWeight, 
			boolean bSmallIcon, boolean bHideIcon, int nWrapWidth, int nFontSize, String sFontFace, 
			int nFontStyle, int nForeground, int nBackground) {

		NodeService nodeService = (NodeService)model.getNodeService();

  		NamedNodeMap attrs = node.getAttributes();
		Attr oType = (Attr)attrs.getNamedItem("type");

		int type = new Integer(oType.getValue()).intValue();
		String id = ((Attr)attrs.getNamedItem("id")).getValue();
		String extendedtype = ((Attr)attrs.getNamedItem("extendedtype")).getValue();

		String sOriginalID = "";
		if ( (Attr)attrs.getNamedItem("questmapid") != null) {
		 	sOriginalID	= "QM"+((Attr)attrs.getNamedItem("questmapid")).getValue();
		}
		else {
			sOriginalID = ((Attr)attrs.getNamedItem("originalid")).getValue();
		}

		String author = ((Attr)attrs.getNamedItem("author")).getValue();
		long created = new Long( ((Attr)attrs.getNamedItem("created")).getValue() ).longValue();
		long lastModified = new Long( ((Attr)attrs.getNamedItem("lastModified")).getValue() ).longValue();
		String label = ((Attr)attrs.getNamedItem("label")).getValue();
		String state = ((Attr)attrs.getNamedItem("state")).getValue();
		Point position = new Point( xPos, yPos );
		
		String sLastModAuthor = "";		
		if ((Attr)attrs.getNamedItem("lastModAuthor") != null) {
			sLastModAuthor = ((Attr)attrs.getNamedItem("lastModAuthor")).getValue();
		}
		if (sLastModAuthor == null || sLastModAuthor.equals("")) {
			sLastModAuthor = author;
		}

		String detail = "";
		String source = "";
		String image = "";
  		int imagewidth = 0;
		int imageheight = 0;
		String background = "";

		Node codes = null;
		Node shortcuts = null;
		Node details = null;
		Node mediaindexes = null;

		NodeList mychildren = node.getChildNodes();
		int mycount = mychildren.getLength();
      	for ( int j = 0; j < mycount; j++ ) {
			Node mychild = mychildren.item(j);
			String myname = mychild.getNodeName();

			if ( myname.equals("detail") ) {
				Node first = mychild.getFirstChild();
				if (first != null)
					detail = first.getNodeValue();
			}
			else if ( myname.equals("details") ) {
				details = mychild;
			}
			else if ( myname.equals("source") ) {
				Node first = mychild.getFirstChild();
				if (first != null) {
					source = first.getNodeValue();
					if (CoreUtilities.isFile(source))
						source = CoreUtilities.cleanPath(source);
				}
			}
			else if ( myname.equals("image") ) {
				Node first = mychild.getFirstChild();
				if (first != null) {
					image = first.getNodeValue();
					image = CoreUtilities.cleanPath( image );
			  		NamedNodeMap imageattrs = mychild.getAttributes();
					Attr oWidth = (Attr)imageattrs.getNamedItem("width");
					if (oWidth != null) {
						imagewidth = new Integer(oWidth.getValue()).intValue();
					}
					Attr oHeight = (Attr)imageattrs.getNamedItem("height");
					if (oHeight != null) {
						imageheight = new Integer(oHeight.getValue()).intValue();
					}					
				}
			}
			else if ( myname.equals("background" )) {
				Node first = mychild.getFirstChild();
				if (first != null) {
					background = first.getNodeValue();
					background = CoreUtilities.cleanPath( background );
				}
			}
			else if ( myname.equals("coderefs") )
				codes = mychild;
			else if ( myname.equals("shortcutrefs") )
				shortcuts = mychild;
		}

		boolean didExist = false;
		try {
			if (sOriginalID.startsWith("QM") && DBNode.getImportAsTranscluded()) {
				didExist = nodeService.doesNodeExist(oSession, sOriginalID);
			}
			else if (!id.equals("0") && !id.equals("-1") && !id.equals("") && DBNode.getImportAsTranscluded()) {
				didExist = nodeService.doesNodeExist(oSession, id);
			}
		}
		catch(Exception ex) {
			System.out.println("Exception: trying to find out if nodes exist already in XMLImport");
		}

		//System.out.println("Did exist = "+didExist);

		// CREATE NEW NODE OBJECT

		NodeSummary newNode = null;
		try	{
			// CHECK TO SEE IF WANT TO DRAW NODE OR JUST ADD TO DATA STRUCTURE
			if (currentViewId.equals(sRootView)) {
				if (bIsListImport) {
					newNode = (NodeSummary) createListNode(model, view, currentViewId, type, id, sOriginalID, 
							author, created, lastModified, label, detail, source, image, imagewidth, imageheight, background, position, 
							transCreationDate, transModDate, sLastModAuthor,
							bShowTags, bShowText, bShowTrans, bShowWeight, bSmallIcon, bHideIcon,
							nWrapWidth, nFontSize, sFontFace, nFontStyle, nForeground, nBackground);
				}
				else {
					newNode = (NodeSummary) createNode(model, view, currentViewId, type, id, sOriginalID, 
							author, created, lastModified, label, detail, source, image, imagewidth, imageheight, background, 
							position, transCreationDate, transModDate, sLastModAuthor,
							bShowTags, bShowText, bShowTrans, bShowWeight, bSmallIcon, bHideIcon,
							nWrapWidth, nFontSize, sFontFace, nFontStyle, nForeground, nBackground);
				}
			}
			else {
				newNode = (NodeSummary) addNode(model, view, currentViewId, type, id, sOriginalID,
						author, created, lastModified, label, detail, source, image, imagewidth, imageheight, background, 
						position, transCreationDate, transModDate, sLastModAuthor,
						bShowTags, bShowText, bShowTrans, bShowWeight, bSmallIcon, bHideIcon,
						nWrapWidth, nFontSize, sFontFace, nFontStyle, nForeground, nBackground);
			}
		}
		catch	(Exception e)	{
			e.printStackTrace();
			ProjectCompendium.APP.displayError("Exception: (XMLImport.processNode) ("+id+") "+e.getMessage());
		}

		if (newNode == null)
			return;

		if ( codes != null )
			processCodeRefs( codes, newNode, didExist );

		if ( details != null )
			processDetailPages( details, newNode, didExist, created, lastModified );

		// CHECK DONE IN process Shortcuts AS Might be a transcluded node to new shortcut
		if (shortcuts != null)
			processShortcutRefs( shortcuts, id );
	}

	/**
	 * Process the XML Node containing a list of detail page data.
	 *
	 * @param node, the XML Node containing the list of detail page data to process.
	 * @param node com.compendium.datamodel.NodeSummary, the Node the detail pages should be added to.
	 * @param didExist, indicates if a node with the same id as the node passed already existed in this database.
	 * @param lCreationDate, the creation date of the given node.
	 * @param lModDate, the date the passed node was last modified.
	 */
	private void processDetailPages(Node node, NodeSummary newNode, boolean didExist, long lCreationDate, long lModDate) {

		Vector details = new Vector();
		NodeDetailPage page = null;

		NodeList children = node.getChildNodes();
		int count = children.getLength();

      	for ( int i = 0; i < count; i++ ) {
			Node child = children.item(i);
			String name = child.getNodeName();

			if ( name.equals("page") ) {

		 		NamedNodeMap attrs = child.getAttributes();

				String nodeID = newNode.getId();

				String author = "";
				Attr oAuthor = (Attr)attrs.getNamedItem("author");
				if (oAuthor != null)
					author = oAuthor.getValue();

				long created = new Long( ((Attr)attrs.getNamedItem("created")).getValue() ).longValue();
				long lastModified = new Long( ((Attr)attrs.getNamedItem("lastModified")).getValue() ).longValue();
				int pageNo = new Integer(((Attr)attrs.getNamedItem("pageno")).getValue()).intValue();

				Date cDate = new Date(created);
				Date mDate = new Date(lastModified);

				Node first = child.getFirstChild();
				String text = "";
				if (first != null)
					text = first.getNodeValue();
				text = text.trim();

				if(pageNo == 1 && bIncludeInDetail) {
					text += includeInDetails(text, author, lCreationDate, lModDate);
				}

				page  = new NodeDetailPage(nodeID, author, text, pageNo, cDate, mDate);
				details.addElement(page);
			}
		}

		if (!details.isEmpty()) {
			try {
				if (!didExist || (didExist && DBNode.getUpdateTranscludedNodes()) ) 	{
					newNode.setDetailPages(details, sCurrentAuthor, sCurrentAuthor);
				}
			}
			catch(Exception ex) {
				ex.printStackTrace();
				System.out.println("Error: (XMLImport.processDetailPages) \n\n"+ex.getMessage());
			}
		}
	}

	/**
	 * Process the given XML Node which contains a list of all Code data to add.
	 *
	 * @param node com.compendium.datamodel.NodeSummary, the Node the codes should be added to..
	 * @param didExist, indicates if a node with the same id as the node passed already existed in this database.
	 */
	private void processCodeRefs(Node node, NodeSummary newNode, boolean didExist) {

		NodeList children = node.getChildNodes();
		int count = children.getLength();
		try {
	      	for ( int i = 0; i < count; i++ ) {

				Node child = children.item(i);
				String name = child.getNodeName();
				if ( name.equals("coderef") ) {

			 		NamedNodeMap attrs = child.getAttributes();
					String id = ((Attr)attrs.getNamedItem("coderef")).getValue();

					if (htCodes.containsKey((Object)id)) {
						Code code = (Code)htCodes.get((Object)id);
						if (!didExist || (didExist && DBNode.getUpdateTranscludedNodes()))
							newNode.addCode(code);
					}
				}
			}
		}
		catch(Exception ex) {
			System.out.println("Error: (XMLImport.processCodeRefs) \n\n"+ex.getMessage());
		}
	}

	/**
	 * Process the given list of XML shortcut.
	 *
	 * @param node, the XML Node containing the list of shortcut node data to process.
	 * @param parentid, the parent node these shortcuts point to.
	 */
	private void processShortcutRefs(Node node, String parentid) {

		NodeList children = node.getChildNodes();
		int count = children.getLength();

		try {
	      	for ( int i = 0; i < count; i++ ) {

				Node child = children.item(i);
				String name = child.getNodeName();
				if ( name.equals("shortcutref") ) {

			 		NamedNodeMap attrs = child.getAttributes();
					String id = ((Attr)attrs.getNamedItem("shortcutref")).getValue();

					Hashtable shortcut = new Hashtable(2);
					shortcut.put((Object)"parentid", (Object)parentid);
					shortcut.put((Object)"shortid", (Object)id);

					vtShortcuts.add((Object)shortcut);
				}
			}
		}
		catch(Exception ex) {
			System.out.println("Error: (XMLImport.processShortcutRefs) \n\n"+ex.getMessage());
		}
	}

	/**
	 * Process the Vector of shortcut relationship data held in the class.
	 * Make sure each shortcut is registered with it's parent.
	 */
	private void processShortcuts() {

		int count  = vtShortcuts.size();

		try {
			for (int i=0; i<count; i++) {
				Hashtable next = (Hashtable)vtShortcuts.elementAt(i);
				String parentid = (String)next.get((Object)"parentid");
				String shortid = (String)next.get((Object)"shortid");

				if (htNewNodes.containsKey((Object)parentid)) {
					NodeSummary parent = (NodeSummary)htNewNodes.get((Object)parentid);

					if (htNewNodes.containsKey((Object)shortid)) {
						NodeSummary shortcut = (NodeSummary)htNewNodes.get((Object)shortid);

						// IF THE PARENT AND THE NODE ARE BOTH TRANSCLUTIONS DON'T ADD THIS RELATIONSHIP
						// I THINK addShortCutNode CHECKS, BUT JUST TO BE ON THE SAFE SIDE
						if ( (!shortcut.getId().equals(shortid)) || (!parent.getId().equals(parentid)) ) {
							//System.out.println("parent = "+parent);
							//System.out.println("shortcut = "+shortcut);
							parent.addShortCutNode(shortcut);
						}
					}
				}
				nShortCount++;
				oProgressBar.setValue(getCurrentCount());
				oProgressDialog.setStatus(getCurrentCount());
			}
		}
		catch(Exception ex) {
			System.out.println("Error: (XMLImport.processShortcuts) \n\n"+ex.getMessage());
		}
	}

	/**
	 * Process the given XML Node which contains a list of all MediaIndex data to add.
	 *
	 * @param node com.compendium.datamodel.NodeSummary, the Node the mediaindex should be added to..
	 * @param didExist, indicates if a node with the same id as the node passed already existed in this database.
	 */
	private void processMediaIndexes(NodeList oMediaIndexes) {

		int count = oMediaIndexes.getLength();
		MediaIndex ind = null;
		try {
	      	for ( int i = 0; i < count; i++ ) {
				Node oMediaIndex = oMediaIndexes.item(i);
				NamedNodeMap attrs = oMediaIndex.getAttributes();
				long index = new Long( ((Attr)attrs.getNamedItem("mediaindex")).getValue() ).longValue();
				String noderef = ((Attr)attrs.getNamedItem("noderef")).getValue();
				String viewref = ((Attr)attrs.getNamedItem("viewref")).getValue();
				String meetingref = ((Attr)attrs.getNamedItem("meetingref")).getValue();
				long created = new Long( ((Attr)attrs.getNamedItem("created")).getValue() ).longValue();
				long lastModified = new Long( ((Attr)attrs.getNamedItem("lastModified")).getValue() ).longValue();

				ind = new MediaIndex(viewref, noderef, meetingref, new Date(index), new Date(created), new Date(lastModified));
				oModel.getMeetingService().createMediaIndex(oSession, ind);
			}
		}
		catch(Exception ex) {
			ex.printStackTrace();
			System.out.println("Error: (XMLImport.processMediaIndexes) \n\n"+ex.getMessage());
		}
	}

	/**
	 * Process the given list of XML link node data and create the compendium links as required.
	 *
	 * @param links, the XML NodeList of link information to process.
	 */
	private void processLinks( NodeList links ) {

		int count = links.getLength();
       	for ( int i = 0; i < count; i++ ) {

			Node link = links.item(i);
			String name = link.getNodeName();

			if (name.equals("link")) {

    	  		NamedNodeMap attrs = link.getAttributes();

				String id = ((Attr)attrs.getNamedItem("id")).getValue();
				String type = ((Attr)attrs.getNamedItem("type")).getValue();
				String to = ((Attr)attrs.getNamedItem("to")).getValue();
				String from = ((Attr)attrs.getNamedItem("from")).getValue();

				if (to.equals(from))
					continue;

				String sOriginalID = "";
				if ( (Attr)attrs.getNamedItem("questmapid") != null) {
		 			sOriginalID	= "QM"+((Attr)attrs.getNamedItem("questmapid")).getValue();
				}
				else {
					sOriginalID = ((Attr)attrs.getNamedItem("originalid")).getValue();
				}

				String author = ((Attr)attrs.getNamedItem("author")).getValue();
				long created = new Long( ((Attr)attrs.getNamedItem("created")).getValue() ).longValue();
				long lastModified = new Long( ((Attr)attrs.getNamedItem("lastModified")).getValue() ).longValue();

				int arrow = -1;
				if (((Attr)attrs.getNamedItem("arrow")) == null)
					arrow = ICoreConstants.ARROW_TO;
				else
					arrow = new Integer( ((Attr)attrs.getNamedItem("arrow")).getValue() ).intValue();

				String sLabel = "";
				Attr oLabel = (Attr)attrs.getNamedItem("label");
				if (oLabel != null)
					sLabel = oLabel.getValue();

				// CHECK IF IMPORTING OLD FORMAT - PRE 1.3.04
				Attr oView = (Attr)attrs.getNamedItem("view");
				if (oView != null) {
					String viewid = oView.getValue();
					View thisview = null;
					if (htNewNodes.containsKey((Object)viewid))
						thisview = (View)htNewNodes.get(viewid);
					else
						thisview = (View)this.oView;

					IModel thismodel = thisview.getModel();

					if (!bIsListImport) {
						if (viewid.equals(sRootView)) {
							createLink(thismodel, thisview, viewid, type, id, sOriginalID, from, to, sLabel, arrow);
						} else {
							addLink(thismodel, thisview, viewid, type, id, sOriginalID, from, to, sLabel, arrow);
						}
					}
				}
				else {
					Node views = null;
					NodeList mychildren = link.getChildNodes();
					int mycount = mychildren.getLength();
			      	for ( int j = 0; j < mycount; j++ ) {
						Node mychild = mychildren.item(j);
						String myname = mychild.getNodeName();

						if ( myname.equals("linkviews") )
							views = mychild;
					}

					NodeList children = views.getChildNodes();
					int kcount = children.getLength();
					try {
				      	for ( int k = 0; k < kcount; k++ ) {

							Node child2 = children.item(k);
							String name2 = child2.getNodeName();
							if ( name2.equals("linkview") ) {

						 		NamedNodeMap attrs2 = child2.getAttributes();
								String viewid = ((Attr)attrs2.getNamedItem("id")).getValue();

								View thisview = null;
								if (htNewNodes.containsKey((Object)viewid))
									thisview = (View)htNewNodes.get(viewid);
								else
									thisview = (View)this.oView;

								IModel thismodel = thisview.getModel();

								if (!bIsListImport) {								
									if (viewid.equals(sRootView)) {
										createLink(thismodel, thisview, viewid, type, id, sOriginalID, from, to, sLabel, arrow);
									} else {
										addLink(thismodel, thisview, viewid, type, id, sOriginalID, from, to, sLabel, arrow);
									}
								}
							}
						}
					}
					catch(Exception ex) {
						System.out.println("Error: (XMLImport.processCodeRefs) \n\n"+ex.getMessage());
					}
				}

				//set the node count for progress bar
				nLinkCount++;
				oProgressBar.setValue(getCurrentCount());
				oProgressDialog.setStatus(getCurrentCount());
			}
		}
	}


	// METHODS TO CREATE NODES AND LINKS
	/**
	 * Adjust the x and y coordinates so node close to the border can be fully seen.
	 * <p>CURRENTLY DOES NOTHING, JUST RETURNS THE POSITION GIVEN.
	 *
	 * @param Point ptPos, the position to adjust.
	 * @return Point, the adjusted position.
	 */
	private Point adjustPosition( Point ptPos ) {

		/*
		if(ptPos.x < 0){
			ptPos.x = -ptPos.x;
		}
		else if(ptPos.x < 25) {
			ptPos.x = ptPos.x + 50;
		}

		if(ptPos.y < 0) {
			ptPos.y = -ptPos.y;
		}
		else if(ptPos.y < 25) {
			ptPos.y = ptPos.y + 50;
		}

		*/
		return ptPos;
	}

	/**
	 * If the user has chosen to change the date and author but request the original information be preserved,
	 * create a string with the original date/author information appended to the current node detail.
	 *
	 * @param detail, the current first page of node detail text.
	 * @param author, the author for this node.
	 * @param lCreationDate, the date in milliseconds when this node was created.
	 * @param lModDate, the date in milliseconds when this node was last modified.
	 * @return java.lang.String, the detail text with author/date info appended.
	 */
	private String includeInDetails( String detail, String author, long lCreationDate, long lModeDate ) {

		detail += "\n\n(Imported Author: " + author + ")\n";
		detail += "\n(Creation Date: " + (new Date(lCreationDate)).toString() + ")\n";
		detail += "\n(Modification Date: " + (new Date(lModeDate)).toString() + ")\n";
		return detail;
	}

	/**
	 * Creates an INodeSummary object and adds it to the model and view
	 *
	 * @param oModel NOT CURRENTLY USED.
	 * @param view the view to add the node to.
	 * @param currentViewId the id of the current view being imported into.
	 * @param nType the type of the node to add.
	 * @param importedid the id of the node in the importation information.
	 * @param sOriginalID the first orignal id of this node.
	 * @param author the author of this node.
	 * @param lCreationDate the date in milliseconds when this node was created.
	 * @param lModDate the date in milliseconds when this node was last modified.
	 * @param sLabel the label of the node to add.
	 * @param sDeail the first/main page of detail text for the node to add.
	 * @param sSource the path for external reference / url this node point to.
	 * @param sImage a path to any image file this node references.
	 * @param imagewidth the width to draw the node image.
	 * @param imageheight the height to draw the node image.
	 * @param ptPos the position in the given view to add to node at.
	 * @param transCreationDate the date the node was put into the view.
	 * @param transModDate the date the view-node data was last modified.
	 * @param sLastModAuthor the author who last modified this node.
	 * @param bShowTags true if this node has the tags indicator draw.
	 * @param bShowText true if this node has the text indicator drawn
	 * @param bShowTrans true if this node has the transclusion indicator drawn
	 * @param bShowWeight true if this node has the weight indicator displayed
	 * @param bSmallIcon true if this node is using a small icon
	 * @param bHideIcons true if this node is not displaying its icon
	 * @param nWrapWidth the node label wrap width used for this node in this view.
	 * @param nFontSize	the font size used for this node in this view
	 * @param sFontFace the font face used for this node in this view
	 * @param nFontStyle the font style used for this node in this view
	 * @param nForeground the foreground color used for this node in this view
	 * @param nBackground the background color used for this node in this view.
	 * 
	 * @exception Exception, if something goes wrong.
	 */
	private INodeSummary createNode( IModel oModel, IView  view, String currentViewId,
													 int		nType,
													 String		importedId,
													 String		sOriginalID,
													 String 	author,
													 long		lCreationDate,
													 long		lModDate,
													 String 	label,
													 String 	detail,
													 String		sSource,
													 String		sImage,
													 int		imagewidth,
													 int		imageheight,
													 String		background,
													 Point		ptPos,
													 Date 		transCreationDate,
													 Date 		transModDate,
													 String 	sLastModAuthor,
													 boolean 	bShowTags, 
													 boolean 	bShowText, 
													 boolean 	bShowTrans, 
													 boolean 	bShowWeight, 
													 boolean 	bSmallIcon, 
													 boolean 	bHideIcon, 
													 int 		nWrapWidth, 
													 int 		nFontSize, 
													 String 	sFontFace, 
													 int 		nFontStyle, 
													 int 		nForeground, 
													 int 		nBackground) throws Exception {


		//Adjust the x and y coordinates so node close to the border can be fully seen
		//ptPos = adjustPosition( ptPos );
		
		//include the details only if import profile says so..
		if(bIncludeInDetail) {
			detail += includeInDetails(detail, author, lCreationDate, lModDate);
		}

		if (sOriginalID.equals("-1"))
			sOriginalID = "";

		Date oCreationDate = new Date(lCreationDate);
		Date oModfificationDate = new Date(lModDate);
		if (!bIsSmartImport) {
			Date date = new Date();
			oCreationDate = date;
			oModfificationDate = date;
			transCreationDate = date;
			transModDate = date;
			author = sCurrentAuthor;
			sLastModAuthor = sCurrentAuthor;
		}

		UINode uinode = oViewPaneUI.createNode(nType, importedId, sOriginalID, author, oCreationDate, 
				oModfificationDate, label, detail, ptPos.x, ptPos.y, transCreationDate, 
				transModDate, sLastModAuthor, bShowTags, bShowText, bShowTrans, bShowWeight, 
				bSmallIcon, bHideIcon, nWrapWidth, nFontSize, sFontFace, nFontStyle, nForeground, nBackground);

		if(nType == ICoreConstants.REFERENCE || nType == ICoreConstants.REFERENCE_SHORTCUT) {
			uinode.getNode().setSource(sSource, sImage, author);
			uinode.getNode().setImageSize(new Dimension(imagewidth, imageheight), author);
			if (sImage == null || sImage.equals(""))
				uinode.setReferenceIcon(sSource);
			else
				uinode.setReferenceIcon(sImage);
		}
		else if(nType == ICoreConstants.MAPVIEW || nType == ICoreConstants.MAP_SHORTCUT ||
				nType == ICoreConstants.LISTVIEW || nType == ICoreConstants.LIST_SHORTCUT) {

			uinode.getNode().setSource("", sImage, author);
			uinode.getNode().setImageSize(new Dimension(imagewidth, imageheight), author);			
			if (sImage != null && !sImage.equals(""))
				uinode.setReferenceIcon(sImage);
		}

		if (!background.equals("") && ( nType == ICoreConstants.MAPVIEW || nType == ICoreConstants.MAP_SHORTCUT)) {
			View newview = (View)uinode.getNode();
			newview.setBackground(background);
			try { newview.updateViewLayer(); }
			catch(Exception ex) { System.out.println("Unable to update database with background image "+background);}
		}

		uinode.setRollover(false);
		uinode.setSelected(true);
		oViewPaneUI.getViewPane().setSelectedNode(uinode,ICoreConstants.MULTISELECT);

		// UNDO LIST
		vtNodeList.addElement(uinode);

		// FOR LINKS
		htUINodes.put((Object)importedId, (Object) uinode);

		// FOR CHECKING IN LOOP
		htNewNodes.put((Object) importedId, (Object) uinode.getNode());

		return uinode.getNode();
	}


	/**
	 * Creates an INodeSummary object and adds it to the model and view
	 *
	 * @param oModel com.compendium.datamodel.IModel, - NOT CURRENTLY USED.
	 * @param view com.compendium.datamodel.View, the view to add the node to.
	 * @param currentViewId, the id of the current view being imported into.
	 * @param nType, the type of the node to add.
	 * @param importedid, the id of the node in the importation information.
	 * @param sOriginalID, the first orignal id of this node.
	 * @param author, the author of this node.
	 * @param lCreationDate, the date in milliseconds when this node was created.
	 * @param lModDate, the date in milliseconds when this node was last modified.
	 * @param sLabel, the label of the node to add.
	 * @param sDeail, the first/main page of detail text for the node to add.
	 * @param sSource, the path for external reference / url this node point to.
	 * @param sImage, a path to any image file this node references.
	 * @param imagewidth the width to draw the node image.
	 * @param imageheight the height to draw the node image.
	 * @param ptPos, the position in the given view to add to node at.
	 * @param transCreationDate, the date the node was put into the view.
	 * @param transModDate, the date the view-node data was last modified.
	 * @param sLastModAuthor the author who last modified this node.
	 * @param bShowTags true if this node has the tags indicator draw.
	 * @param bShowText true if this node has the text indicator drawn
	 * @param bShowTrans true if this node has the transclusion indicator drawn
	 * @param bShowWeight true if this node has the weight indicator displayed
	 * @param bSmallIcon true if this node is using a small icon
	 * @param bHideIcons true if this node is not displaying its icon
	 * @param nWrapWidth the node label wrap width used for this node in this view.
	 * @param nFontSize	the font size used for this node in this view
	 * @param sFontFace the font face used for this node in this view
	 * @param nFontStyle the font style used for this node in this view
	 * @param nForeground the foreground color used for this node in this view
	 * @param nBackground the background color used for this node in this view. 
	 * @exception Exception, if something goes wrong.
	 */
	private INodeSummary createListNode( IModel oModel, IView  view, String currentViewId,
													 int		nType,
													 String		importedId,
													 String		sOriginalID,
													 String 	author,
													 long		lCreationDate,
													 long		lModDate,
													 String 	label,
													 String 	detail,
													 String		sSource,
													 String 	sImage,
													 int		imagewidth,
													 int		imageheight,													 
													 String		background,
													 Point		ptPos,
													 Date 		transCreationDate,
													 Date 		transModDate,
													 String 	sLastModAuthor,
													 boolean 	bShowTags, 
													 boolean 	bShowText, 
													 boolean 	bShowTrans, 
													 boolean 	bShowWeight, 
													 boolean 	bSmallIcon, 
													 boolean 	bHideIcon, 
													 int 		nWrapWidth, 
													 int 		nFontSize, 
													 String 	sFontFace, 
													 int 		nFontStyle, 
													 int 		nForeground, 
													 int 		nBackground) throws Exception {

		//ptPos = adjustPosition( ptPos );

		if(bIncludeInDetail)
			detail += includeInDetails(detail, author, lCreationDate, lModDate);

		if (sOriginalID.equals("-1"))
			sOriginalID = "";
		else if (!sOriginalID.equals("") && lCreationDate < ICoreConstants.MYSQLDATE)
			sOriginalID = "QM"+sOriginalID;

		Date oCreationDate = new Date(lCreationDate);
		Date oModfificationDate = new Date(lModDate);
		if (!bIsSmartImport) {
			Date date = new Date();
			oCreationDate = date;
			oModfificationDate = date;
			transCreationDate = date;
			transModDate = date;
			author = sCurrentAuthor;
			sLastModAuthor = sCurrentAuthor;
		}

		NodePosition npTemp = oUIList.getListUI().createNode (nType, importedId, sOriginalID, author, oCreationDate, 
											oModfificationDate, label, detail, ptPos.x,
											(oUIList.getNumberOfNodes() + vtNodeList.size() + 1) * 10,
											transCreationDate, transModDate, sLastModAuthor, bShowTags, 
											bShowText, bShowTrans, bShowWeight, bSmallIcon, bHideIcon, 
											nWrapWidth, nFontSize, sFontFace, nFontStyle, nForeground, nBackground);

		INodeSummary node = npTemp.getNode();

		if(nType == ICoreConstants.REFERENCE || nType == ICoreConstants.REFERENCE_SHORTCUT) {
			node.setSource(sSource, sImage, author);
			node.setImageSize(new Dimension(imagewidth, imageheight), author);			
		}
		else if(nType == ICoreConstants.MAPVIEW || nType == ICoreConstants.MAP_SHORTCUT ||
				nType == ICoreConstants.LISTVIEW || nType == ICoreConstants.LIST_SHORTCUT) {
			node.setSource("", sImage, author);
			node.setImageSize(new Dimension(imagewidth, imageheight), author);			
		}

		if (!background.equals("") && (nType == ICoreConstants.MAPVIEW || nType == ICoreConstants.MAP_SHORTCUT)) {
			((View)node).setBackground(background);
			try { ((View)node).updateViewLayer(); }
			catch(Exception ex) { System.out.println("Unable to update database with background image "+background);}
		}

		//setAuthorDate( (NodeSummary) node, author, lCreationDate, lModDate );

		// FOR UNDO
		vtNodeList.addElement(node);

		// FOR CHECKING IN LOOP
		htNewNodes.put((Object) importedId, (Object) node);

		return node;
	}

	/**
	 * Creates an INodeSummary object and adds it to the database
	 *
	 * @param oModel - NOT CURRENTLY USED.
	 * @param view the view to add the node to.
	 * @param currentViewId the id of the current view being imported into.
	 * @param nType the type of the node to add.
	 * @param importedid the id of the node in the importation information.
	 * @param sOriginalID the first orignal id of this node.
	 * @param author the author of this node.
	 * @param lCreationDate the date in milliseconds when this node was created.
	 * @param lModDate the date in milliseconds when this node was last modified.
	 * @param sLabel the label of the node to add.
	 * @param sDeail the first/main page of detail text for the node to add.
	 * @param sSource the path for external reference / url this node point to.
	 * @param sImage a path to any image file this node references.
	 * @param imagewidth the width to draw the node image.
	 * @param imageheight the height to draw the node image. 
	 * @param ptPos the position in the given view to add to node at.
	 * @param transCreationDate the date the node was put into the view.
	 * @param transModDate the date the view-node data was last modified.
	 * @param sLastModAuthor the author who last modified this imported node.
	 * @param bShowTags true if this node has the tags indicator draw.
	 * @param bShowText true if this node has the text indicator drawn
	 * @param bShowTrans true if this node has the transclusion indicator drawn
	 * @param bShowWeight true if this node has the weight indicator displayed
	 * @param bSmallIcon true if this node is using a small icon
	 * @param bHideIcons true if this node is not displaying its icon
	 * @param nWrapWidth the node label wrap width used for this node in this view.
	 * @param nFontSize	the font size used for this node in this view
	 * @param sFontFace the font face used for this node in this view
	 * @param nFontStyle the font style used for this node in this view
	 * @param nForeground the foreground color used for this node in this view
	 * @param nBackground the background color used for this node in this view.
	 * 
	 * @exception Exception, if something goes wrong.
	 */
	private INodeSummary addNode( IModel oModel, IView  view, String currentViewId,
													 int		nType,
													 String		importedId,
													 String		sOriginalID,
													 String 	author,
													 long		lCreationDate,
													 long		lModDate,
													 String 	label,
													 String 	detail,
													 String		source,
													 String 	image,
													 int		imagewidth,
													 int		imageheight,													 
													 String		background,
													 Point		ptPos,
													 Date 		transCreationDate,
													 Date 		transModDate,
													 String 	sLastModAuthor,
													 boolean 	bShowTags, 
													 boolean 	bShowText, 
													 boolean 	bShowTrans, 
													 boolean 	bShowWeight, 
													 boolean 	bSmallIcon, 
													 boolean 	bHideIcon, 
													 int 		nWrapWidth, 
													 int 		nFontSize, 
													 String 	sFontFace, 
													 int 		nFontStyle, 
													 int 		nForeground, 
													 int 		nBackground) throws Exception {

		//System.out.println("ADDING NODE "+importedId);

		//Adjust the x and y coordinates so node close to the border can be fully seen
		//ptPos = adjustPosition( ptPos );

		//include the details only if import profile says so..
		if(bIncludeInDetail) {
			detail += includeInDetails(detail, author, lCreationDate, lModDate);
		}

		if (sOriginalID.equals("-1"))
			sOriginalID = "";
		else if (!sOriginalID.equals("") && lCreationDate < ICoreConstants.MYSQLDATE)
			sOriginalID = "QM"+sOriginalID;

		Date oCreationDate = new Date(lCreationDate);
		Date oModfificationDate = new Date(lModDate);
		if (!bIsSmartImport) {
			Date date = new Date();
			oCreationDate = date;
			oModfificationDate = date;
			transCreationDate = date;
			transModDate = date;
			author = sCurrentAuthor;
			sLastModAuthor = sCurrentAuthor;
		}

		NodePosition nodePos = view.addMemberNode(nType, "", importedId, sOriginalID, author, oCreationDate, 
				oModfificationDate, label, detail, ptPos.x, ptPos.y, transCreationDate, transModDate, 
				sLastModAuthor, bShowTags, bShowText, bShowTrans, bShowWeight, bSmallIcon, bHideIcon,
				nWrapWidth, nFontSize, sFontFace, nFontStyle, nForeground, nBackground);

		if(nType == ICoreConstants.REFERENCE || nType == ICoreConstants.REFERENCE_SHORTCUT) {
			nodePos.getNode().setSource(source, image, author);
			nodePos.getNode().setImageSize(new Dimension(imagewidth, imageheight), author);						
		}
		else if(nType == ICoreConstants.MAPVIEW || nType == ICoreConstants.MAP_SHORTCUT ||
				nType == ICoreConstants.LISTVIEW || nType == ICoreConstants.LIST_SHORTCUT) {
			nodePos.getNode().setSource("", image, author);
			nodePos.getNode().setImageSize(new Dimension(imagewidth, imageheight), author);			
		}

		if (!background.equals("") && (nType == ICoreConstants.MAPVIEW || nType == ICoreConstants.MAP_SHORTCUT)) {
			((View)nodePos.getNode()).setBackground(background);
			try { ((View)nodePos.getNode()).updateViewLayer(); }
			catch(Exception ex) { System.out.println("Unable to update database with background image "+background);}
		}

		// UNDO LIST
		vtNodeList.addElement(nodePos);

		// FOR CHECKING IN LOOP
		htNewNodes.put((Object) importedId, (Object) nodePos.getNode());

		// FOR LINKS
		htUINodes.put((Object)importedId, (Object) nodePos);

		// STORE NODEPOSITION FOR USE WITH INNERLINKS
		if (!htNodeView.containsKey((Object) currentViewId))
			htNodeView.put((Object) currentViewId, (Object) new Hashtable(51));

		Hashtable nextView = (Hashtable)htNodeView.get((Object) currentViewId);
		nextView.put( (Object) importedId, (Object) nodePos );
		htNodeView.put( (Object) currentViewId, (Object) nextView);

		return nodePos.getNode();
	}

	/**
	 * Creates a ILink object and adds it to the model and view
	 *
	 * @param oModel com.compendium.datamodel.IModel, - NOT CURRENTLY USED.
	 * @param view com.compendium.datamodel.View, the view to add the link to.
	 * @param currentViewId, the id of the current view being imported into.
	 * @param sType, the type of the link to add.
	 * @param importedid, the id of the link in the importation information.
	 * @param sOriginalID, the first orignal id of this link.
	 * @param sFromId, the id number of the node the link comes from.
	 * @param sToId, the id number of the node the link goes to.
	 * @param nArrow, the type of arrow heads to draw.
	 */
	private void createLink( IModel oModel, IView  view, String currentViewId,
													String 	sType,
													String	sImportedId,
													String 	sOriginalID,
													String	sFromId,
													String	sToId,
													String  sLabel,
													int 	nArrow ) {

		if(view.getType() == ICoreConstants.MAPVIEW) {

			UINode fromUINode = (UINode)htUINodes.get((Object)sFromId);
			UINode toUINode = (UINode)htUINodes.get((Object)sToId);

			//int type = UILink.getLinkType(sType);

			NodeUI nodeui = toUINode.getUI();

			UILink uiLink = nodeui.createLink(sImportedId, fromUINode, toUINode, sType, sLabel, nArrow);

			if (oViewPaneUI != null) {
				uiLink.setRollover(false);
				uiLink.setSelected(true);
				oViewPaneUI.getViewPane().setSelectedLink(uiLink,ICoreConstants.MULTISELECT);
			}

			// FOR UNDO ON CANCEL
			vtLinkList.addElement(uiLink);
		}
	}


	/**
	 * Add a link to the datamodel view only.
	 *
	 * @param oModel com.compendium.datamodel.IModel, - NOT CURRENTLY USED.
	 * @param view com.compendium.datamodel.View, the view to add the link to.
	 * @param currentViewId, the id of the current view being imported into.
	 * @param sType, the type of the link to add.
	 * @param importedid, the id of the link in the importation information.
	 * @param sOriginalID, the first orignal id of this link.
	 * @param sFromId, the id number of the node the link comes from.
	 * @param sToId, the id number of the node the link goes to.
	 * @param nArrow, the type of arrow heads to draw.
	 */
	private void addLink( IModel oModel, IView  view, String currentViewId,
													String	 	sType,
													String		sImportedId,
													String 		sOriginalID,
													String		sFromId,
													String		sToId,
													String 		sLabel,
													int nArrow) {

		if(view.getType() == ICoreConstants.MAPVIEW) {

			Hashtable viewNodePositions = (Hashtable)htNodeView.get((Object) currentViewId);
			if (viewNodePositions == null)
				return;

			NodePosition fromNode = (NodePosition) viewNodePositions.get((Object) sFromId);
			NodePosition toNode = (NodePosition) viewNodePositions.get((Object) sToId);

			if (sOriginalID.equals("-1"))
				sOriginalID = "";

			try {
				Link link = (Link)view.addMemberLink(sType,
												sImportedId,
												sOriginalID,
												sCurrentAuthor,
												fromNode.getNode(),
												toNode.getNode(),
												sLabel,
												nArrow);

				// FOR UNDO ON CANCEL
				vtLinkList.addElement(link);
			}
			catch(Exception ex) {
				ex.printStackTrace();
				System.out.println("Error: (XMLImport.addLink) \n\n"+ex.getMessage());
			}
		}
	}
}
