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
	private String				sFileName			= ""; //$NON-NLS-1$

	/** The id of the root view in the import data.*/
	private String 				sRootView 			= ""; //$NON-NLS-1$

	/** Holds the author name of the current user.*/
	private String				sCurrentAuthor		= ""; //$NON-NLS-1$

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

	/** The list of view/node relationship info.*/
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
	private String sUserID = ""; //$NON-NLS-1$

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

		if (View.isListType(oView.getType())) {
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
	  		oProgressDialog = new UIProgressDialog(ProjectCompendium.APP,LanguageProperties.getString(LanguageProperties.IO_BUNDLE, "XMLImport.progressMessage"), LanguageProperties.getString(LanguageProperties.IO_BUNDLE, "XMLImport.progressTitle")); //$NON-NLS-1$ //$NON-NLS-2$
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
				ProjectCompendium.APP.displayError(LanguageProperties.getString(LanguageProperties.IO_BUNDLE, "XMLImport.errorImporting")+"\n");	 //$NON-NLS-1$
			}
			document = null;
        }
		catch ( Exception e ) {
			ProjectCompendium.APP.displayError(LanguageProperties.getString(LanguageProperties.IO_BUNDLE, "XMLImport.errorImporting")); //$NON-NLS-1$
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
		Attr oRootView = (Attr)attrs.getNamedItem("rootview"); //$NON-NLS-1$
		sRootView = oRootView.getValue();

		// PRE-PROCESS VIEWS
		NodeList views = document.getElementsByTagName("view"); //$NON-NLS-1$
		int counti = views.getLength();
		for (int i=0; i< counti; i++) {

			Node view = views.item(i);
			attrs = view.getAttributes();

			Attr oViewID = (Attr)attrs.getNamedItem("viewref"); //$NON-NLS-1$
			String viewid = oViewID.getValue();
			Attr oNodeID = (Attr)attrs.getNamedItem("noderef"); //$NON-NLS-1$
			String nodeid = oNodeID.getValue();
			Attr oXPos = (Attr)attrs.getNamedItem("XPosition"); //$NON-NLS-1$
			String xPos = oXPos.getValue();
			Attr oYPos = (Attr)attrs.getNamedItem("YPosition"); //$NON-NLS-1$
			String yPos = oYPos.getValue();

			Attr oCreated = (Attr)attrs.getNamedItem("created"); //$NON-NLS-1$
			Long created = new Long(0);
			if (oCreated != null) {
				created = new Long(oCreated.getValue());
			}

			Attr oLastModified = (Attr)attrs.getNamedItem("lastModified"); //$NON-NLS-1$
			Long lastModified = new Long(0);
			if (oLastModified != null) {
				lastModified = new Long(oLastModified.getValue());
			}
			
			Model model = (Model)oModel;
						
			Attr oShowTags = (Attr)attrs.getNamedItem("showTags"); //$NON-NLS-1$
			Boolean bShowTags = null;
			if (oShowTags != null) {
				bShowTags = new Boolean(oShowTags.getValue());
			} else {
				bShowTags = new Boolean(model.showTagsNodeIndicator);
			}

			Attr oShowText = (Attr)attrs.getNamedItem("showText"); //$NON-NLS-1$
			Boolean bShowText = null;
			if (oShowText != null) {
				bShowText = new Boolean(oShowText.getValue());
			} else {
				bShowText = new Boolean(model.showTextNodeIndicator);
			}

			Attr oShowTrans = (Attr)attrs.getNamedItem("showTrans"); //$NON-NLS-1$
			Boolean bShowTrans = null;
			if (oShowTrans != null) {
				bShowTrans = new Boolean(oShowTrans.getValue());
			} else {
				bShowTrans = new Boolean(model.showTransNodeIndicator);
			}

			Attr oShowWeight = (Attr)attrs.getNamedItem("showWeight"); //$NON-NLS-1$
			Boolean bShowWeight = null;
			if (oShowWeight != null) {
				bShowWeight = new Boolean(oShowWeight.getValue());
			} else {
				bShowWeight = new Boolean(model.showWeightNodeIndicator);
			}

			Attr oSmallIcon = (Attr)attrs.getNamedItem("smallIcon"); //$NON-NLS-1$
			Boolean bSmallIcon = null;
			if (oSmallIcon != null) {
				bSmallIcon = new Boolean(oSmallIcon.getValue());
			} else {
				bSmallIcon = new Boolean(model.smallIcons);
			}

			Attr oHideIcon = (Attr)attrs.getNamedItem("hideIcon"); //$NON-NLS-1$
			Boolean bHideIcon = null;
			if (oHideIcon != null) {
				bHideIcon = new Boolean(oHideIcon.getValue());
			} else {
				bHideIcon = new Boolean(model.hideIcons);
			}
			
			Attr oWrapWidth = (Attr)attrs.getNamedItem("labelWrapWidth"); //$NON-NLS-1$
			Integer nWrapWidth = null;
			if (oWrapWidth != null) {
				nWrapWidth = new Integer(oWrapWidth.getValue());
			} else {
				nWrapWidth = new Integer(model.labelWrapWidth);
			}
			
			Attr oFontSize = (Attr)attrs.getNamedItem("fontsize"); //$NON-NLS-1$
			Integer nFontSize = null;
			if (oFontSize != null) {
				nFontSize = new Integer(oFontSize.getValue());
			} else {
				nFontSize = new Integer(model.fontsize);
			}

			Attr oFontFace = (Attr)attrs.getNamedItem("fontface"); //$NON-NLS-1$
			String sFontFace = "";			 //$NON-NLS-1$
			if (oFontFace != null) {
				sFontFace = oFontFace.getValue();	
			} else {
				sFontFace = model.fontface;
			}
			
			Attr oFontStyle = (Attr)attrs.getNamedItem("fontstyle"); //$NON-NLS-1$
			Integer nFontStyle = null;
			if (oFontStyle != null) {
				nFontStyle = new Integer(oFontStyle.getValue());
			} else {
				nFontStyle = new Integer(model.fontstyle);
			}
			
			Attr oForeground = (Attr)attrs.getNamedItem("foreground"); //$NON-NLS-1$
			Integer nForeground = null;
			if (oForeground != null) {
				nForeground = new Integer(oForeground.getValue());
			} else {
				nForeground = new Integer(Model.FOREGROUND_DEFAULT.getRGB());
			}
			
			Attr oBackground = (Attr)attrs.getNamedItem("background"); //$NON-NLS-1$
			Integer nBackground = null;
			if (oBackground != null) {
				nBackground = new Integer(oBackground.getValue());
			} else {
				nBackground = new Integer(Model.BACKGROUND_DEFAULT.getRGB());
			}				
			
			Vector<Object> nodePos = new Vector<Object>(19);
			nodePos.addElement(viewid);
			nodePos.addElement(nodeid);
			nodePos.addElement(xPos);
			nodePos.addElement(yPos);
			nodePos.addElement(created);
			nodePos.addElement(lastModified);

			nodePos.addElement(bShowTags);
			nodePos.addElement(bShowText);
			nodePos.addElement(bShowTrans);
			nodePos.addElement(bShowWeight);
			nodePos.addElement(bSmallIcon);								
			nodePos.addElement(bHideIcon);
			nodePos.addElement(nWrapWidth);
			nodePos.addElement(nFontSize);
			nodePos.addElement(sFontFace);
			nodePos.addElement(nFontStyle);
			nodePos.addElement(nForeground);
			nodePos.addElement(nBackground);
			
			NodeList mychildren = view.getChildNodes();
			Node times = null;
			int mycount = mychildren.getLength();
	      	for ( int j = 0; j < mycount; j++ ) {
				Node mychild = mychildren.item(j);
				String myname = mychild.getNodeName();
				if ( myname.equals("times") ) {
					times = mychild;
				}
			}
	      	
	      	if (times != null) {
	      		nodePos.add(times);
	      	}
			
			if (!htViews.containsKey((Object) viewid))
				htViews.put((Object) viewid, (Object) new Vector(51));

			Vector nextView = (Vector)htViews.get((Object) viewid);
			nextView.add( (Object) nodePos );
			htViews.put((Object) viewid, (Object) nextView);
		}

		// PRE-PROCESS NODES
		NodeList nodes = document.getElementsByTagName("node"); //$NON-NLS-1$
		counti = nodes.getLength();
		for (int i=0; i< counti; i++) {
			Node innernode = nodes.item(i);
			attrs = innernode.getAttributes();
			Attr oID = (Attr)attrs.getNamedItem("id"); //$NON-NLS-1$
			String nodeid = oID.getValue();
			htNodes.put((Object) nodeid, (Object) innernode);
		}

		NodeList codes = document.getElementsByTagName("code"); //$NON-NLS-1$
		NodeList links = document.getElementsByTagName("link"); //$NON-NLS-1$
		NodeList mediaindexes = document.getElementsByTagName("mediaindex"); //$NON-NLS-1$

		// FOR PROGRESS BAR ONLY
		NodeList shorts = document.getElementsByTagName("shortcutref");
		NodeList meetings = document.getElementsByTagName("meeting");
		NodeList movies = document.getElementsByTagName("movie");

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

		// NEED TO DO THIS AFTER NODES ARE PROCESSED AS THEY NEED TO REFERENCE THEM
		processMovies(movies);

		// INITIALIZE THE NEW VIEW NODES IF THERE ARE ANY
		// SO THEIR NODE WEIGHT INICATION NUMBERS REFRESH CORRECTLY LATER
		int countk = htUINodes.size();
		int nType = 0;
		UINode oCheckNode = null;
		for(Enumeration e = htUINodes.elements();e.hasMoreElements();) {
			Object obj = e.nextElement();
			if (obj instanceof UINode) {
				oCheckNode = (UINode)obj;
				nType = oCheckNode.getType();
				if(View.isViewType(nType)) {
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

			String id = ((Attr)attrs.getNamedItem("id")).getValue(); //$NON-NLS-1$
			String author = ((Attr)attrs.getNamedItem("author")).getValue(); //$NON-NLS-1$
			long created = new Long( ((Attr)attrs.getNamedItem("created")).getValue() ).longValue(); //$NON-NLS-1$
			long lastModified = new Long( ((Attr)attrs.getNamedItem("lastModified")).getValue() ).longValue(); //$NON-NLS-1$
			String name = ((Attr)attrs.getNamedItem("name")).getValue(); //$NON-NLS-1$
			String description = ((Attr)attrs.getNamedItem("description")).getValue(); //$NON-NLS-1$
			String behavior = ((Attr)attrs.getNamedItem("behavior")).getValue(); //$NON-NLS-1$

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
				ProjectCompendium.APP.displayError("Exception: (XMLImport.processCodes) " + ex.getMessage()); //$NON-NLS-1$
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

			String sMeetingID = ((Attr)attrs.getNamedItem("meetingref")).getValue(); //$NON-NLS-1$
			String sMeetingMapID = ((Attr)attrs.getNamedItem("meetingmapref")).getValue(); //$NON-NLS-1$
			String sMeetingName = ((Attr)attrs.getNamedItem("meetingname")).getValue(); //$NON-NLS-1$
			long meetingdate = new Long( ((Attr)attrs.getNamedItem("meetingdate")).getValue() ).longValue(); //$NON-NLS-1$
			int nStatus = new Integer( ((Attr)attrs.getNamedItem("currentstatus")).getValue() ).intValue(); //$NON-NLS-1$

			Date dMeetingDate = new Date(meetingdate);

			// ADD TO DATABASE "Meeting" TABLE
			try {
				oModel.getMeetingService().createMeeting(oSession, sMeetingID, sMeetingMapID, sMeetingName, dMeetingDate, nStatus);

				nMeetingCount++;
				oProgressBar.setValue(getCurrentCount());
				oProgressDialog.setStatus(getCurrentCount());
			}
			catch(Exception ex) {
				ProjectCompendium.APP.displayError("Exception: (XMLImport.processMeetings) " + ex.getMessage()); //$NON-NLS-1$
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

		// DO NOT CONTINUE IF EMPTY VIEW
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

					if (View.isViewType(nodeType)) {
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
			
			if (node.size() == 19) {				
				processTimes((Node)node.elementAt(18), view, (String)nodeid);
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
	 * Process the XML Node containing a list of time data.
	 *
	 * @param node the XML Node containing the list of time data to process.
	 * @param viewid the id of the view they are in
	 * @param nodeid the id of the node the time data relates to.
	 */
	private void processTimes(Node node, IView view, String nodeid) {

		if (view != null) {
			if (view instanceof TimeMapView) {
				if (htNewNodes.containsKey(nodeid)) {
					NodeSummary nextnode = (NodeSummary)htNewNodes.get(nodeid);
					String sNodeID = nextnode.getId();
					TimeMapView mapview = (TimeMapView)view;
					NodeList children = node.getChildNodes();
					int count = children.getLength();
					for ( int i = 0; i < count; i++ ) {
						Node child = children.item(i);
						String name = child.getNodeName();
						if ( name.equals("time") ) {
					 		NamedNodeMap attrs = child.getAttributes();
					 		
							long show = new Long( ((Attr)attrs.getNamedItem("show")).getValue() ).longValue();
							long hide = new Long( ((Attr)attrs.getNamedItem("hide")).getValue() ).longValue();
							
							int x = new Integer( ((Attr)attrs.getNamedItem("atX")).getValue() ).intValue();
							int y = new Integer( ((Attr)attrs.getNamedItem("atY")).getValue() ).intValue();
							
							try {
								mapview.addNodeTime(sNodeID, show, hide, x, y);
							} catch(Exception e) {
								e.printStackTrace();
							}
						}
					}
				}
			}
		}
	}
	
	/**
	 * Process the given view and node data to create the view/node relationship.
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
	 * @param nForeground the foreground colour used for this node in this view
	 * @param nBackground the background colour used for this node in this view. 
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
			ProjectCompendium.APP.displayError("Exception: (XMLImport.processNodeView) " + ex.getMessage()); //$NON-NLS-1$
		}

		// STORE NODEPOSITION FOR USE WITH LINKS
		if (!htNodeView.containsKey((Object) viewid))
			htNodeView.put((Object) viewid, (Object) new Hashtable(51));

		Hashtable nextView = (Hashtable)htNodeView.get((Object) viewid);
		nextView.put( (Object) nodeid, (Object) nodePos );
		htNodeView.put( (Object) viewid, (Object) nextView);
	}

	/** 
	 * Check whether the given path denotes a database file from an export zip file.
	 * @param sourcepath the path to check
	 * @return true iff sourcepath denotes a database file
	 */
	private static boolean isDatabaseFile(String sourcepath) {
		if (sourcepath == "") return false; //$NON-NLS-1$
		File file = new File(sourcepath);
		String parent = file.getParent();
		if (null == parent) return false;
		return (parent.endsWith(XMLExport.EXPORT_DB_PATH));
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
		Attr oType = (Attr)attrs.getNamedItem("type"); //$NON-NLS-1$

		int type = new Integer(oType.getValue()).intValue();
		String id = ((Attr)attrs.getNamedItem("id")).getValue(); //$NON-NLS-1$
		String extendedtype = ((Attr)attrs.getNamedItem("extendedtype")).getValue(); //$NON-NLS-1$

		String sOriginalID = ""; //$NON-NLS-1$
		if ( (Attr)attrs.getNamedItem("questmapid") != null) { //$NON-NLS-1$
		 	sOriginalID	= "QM"+((Attr)attrs.getNamedItem("questmapid")).getValue(); //$NON-NLS-1$ //$NON-NLS-2$
		}
		else {
			sOriginalID = ((Attr)attrs.getNamedItem("originalid")).getValue(); //$NON-NLS-1$
		}

		String author = ((Attr)attrs.getNamedItem("author")).getValue(); //$NON-NLS-1$
		long created = new Long( ((Attr)attrs.getNamedItem("created")).getValue() ).longValue(); //$NON-NLS-1$
		long lastModified = new Long( ((Attr)attrs.getNamedItem("lastModified")).getValue() ).longValue(); //$NON-NLS-1$
		String label = ((Attr)attrs.getNamedItem("label")).getValue(); //$NON-NLS-1$
		String state = ((Attr)attrs.getNamedItem("state")).getValue(); //$NON-NLS-1$
		Point position = new Point( xPos, yPos );
		
		String sLastModAuthor = "";		 //$NON-NLS-1$
		if ((Attr)attrs.getNamedItem("lastModAuthor") != null) { //$NON-NLS-1$
			sLastModAuthor = ((Attr)attrs.getNamedItem("lastModAuthor")).getValue(); //$NON-NLS-1$
		}
		if (sLastModAuthor == null || sLastModAuthor.equals("")) { //$NON-NLS-1$
			sLastModAuthor = author;
		}

		String detail = ""; //$NON-NLS-1$
		String source = ""; //$NON-NLS-1$
		String image = ""; //$NON-NLS-1$
  		int imagewidth = 0;
		int imageheight = 0;
		String background = ""; //$NON-NLS-1$

		Node codes = null;
		Node shortcuts = null;
		Node details = null;
		Node mediaindexes = null;

		NodeList mychildren = node.getChildNodes();
		int mycount = mychildren.getLength();
      	for ( int j = 0; j < mycount; j++ ) {
			Node mychild = mychildren.item(j);
			String myname = mychild.getNodeName();

			if ( myname.equals("detail") ) { //$NON-NLS-1$
				Node first = mychild.getFirstChild();
				if (first != null)
					detail = first.getNodeValue();
			}
			else if ( myname.equals("details") ) { //$NON-NLS-1$
				details = mychild;
			}
			else if ( myname.equals("source") ) { //$NON-NLS-1$
				Node first = mychild.getFirstChild();
				if (first != null) {
					source = first.getNodeValue();
					if (CoreUtilities.isFile(source))
						source = CoreUtilities.cleanPath(source, ProjectCompendium.isWindows);
				}
			}
			else if ( myname.equals("image") ) { //$NON-NLS-1$
				Node first = mychild.getFirstChild();
				if (first != null) {
					image = first.getNodeValue();
					image = CoreUtilities.cleanPath( image, ProjectCompendium.isWindows);
			  		NamedNodeMap imageattrs = mychild.getAttributes();
					Attr oWidth = (Attr)imageattrs.getNamedItem("width"); //$NON-NLS-1$
					if (oWidth != null) {
						imagewidth = new Integer(oWidth.getValue()).intValue();
					}
					Attr oHeight = (Attr)imageattrs.getNamedItem("height"); //$NON-NLS-1$
					if (oHeight != null) {
						imageheight = new Integer(oHeight.getValue()).intValue();
					}					
				}
			}
			else if ( myname.equals("background" )) { //$NON-NLS-1$
				Node first = mychild.getFirstChild();
				if (first != null) {
					background = first.getNodeValue();
					background = CoreUtilities.cleanPath( background, ProjectCompendium.isWindows );
				}
			}
			else if ( myname.equals("coderefs") ) //$NON-NLS-1$
				codes = mychild;
			else if ( myname.equals("shortcutrefs") ) //$NON-NLS-1$
				shortcuts = mychild;
		}

		boolean didExist = false;
		try {
			if (sOriginalID.startsWith("QM") && DBNode.getImportAsTranscluded()) { //$NON-NLS-1$
				didExist = nodeService.doesNodeExist(oSession, sOriginalID);
			}
			else if (!id.equals("0") && !id.equals("-1") && !id.equals("") && DBNode.getImportAsTranscluded()) { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				didExist = nodeService.doesNodeExist(oSession, id);
			}
		}
		catch(Exception ex) {
			System.out.println("Exception: trying to find out if nodes exist already in XMLImport"); //$NON-NLS-1$
		}

		//System.out.println("Did exist = "+didExist);
		if (isDatabaseFile(source)) {
			File file = new File(source);
			try {
				LinkedFile lf = model.getLinkedFileService().addFile(oSession, id, file);
				source = lf.getSourcePath();
			} catch (IOException e) {
				ProjectCompendium.APP
						.displayError("Exception: (XMLImport.processNode)\n\n" //$NON-NLS-1$
								+ e.getMessage());
				e.printStackTrace();
			}
			finally {
				if (file.exists()) {
					boolean deleted = file.delete();
				}
			}
		}

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
			ProjectCompendium.APP.displayError("Exception: (XMLImport.processNode) ("+id+") "+e.getMessage()); //$NON-NLS-1$ //$NON-NLS-2$
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

			if ( name.equals("page") ) { //$NON-NLS-1$

		 		NamedNodeMap attrs = child.getAttributes();

				String nodeID = newNode.getId();

				String author = ""; //$NON-NLS-1$
				Attr oAuthor = (Attr)attrs.getNamedItem("author"); //$NON-NLS-1$
				if (oAuthor != null)
					author = oAuthor.getValue();

				long created = new Long( ((Attr)attrs.getNamedItem("created")).getValue() ).longValue(); //$NON-NLS-1$
				long lastModified = new Long( ((Attr)attrs.getNamedItem("lastModified")).getValue() ).longValue(); //$NON-NLS-1$
				int pageNo = new Integer(((Attr)attrs.getNamedItem("pageno")).getValue()).intValue(); //$NON-NLS-1$

				Date cDate = new Date(created);
				Date mDate = new Date(lastModified);

				Node first = child.getFirstChild();
				String text = ""; //$NON-NLS-1$
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
				System.out.println("Error: (XMLImport.processDetailPages) \n\n"+ex.getMessage()); //$NON-NLS-1$
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
				if ( name.equals("coderef") ) { //$NON-NLS-1$

			 		NamedNodeMap attrs = child.getAttributes();
					String id = ((Attr)attrs.getNamedItem("coderef")).getValue(); //$NON-NLS-1$

					if (htCodes.containsKey((Object)id)) {
						Code code = (Code)htCodes.get((Object)id);
						if (!didExist || (didExist && DBNode.getUpdateTranscludedNodes()))
							newNode.addCode(code);
					}
				}
			}
		}
		catch(Exception ex) {
			System.out.println("Error: (XMLImport.processCodeRefs) \n\n"+ex.getMessage()); //$NON-NLS-1$
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
				if ( name.equals("shortcutref") ) { //$NON-NLS-1$

			 		NamedNodeMap attrs = child.getAttributes();
					String id = ((Attr)attrs.getNamedItem("shortcutref")).getValue(); //$NON-NLS-1$

					Hashtable shortcut = new Hashtable(2);
					shortcut.put((Object)"parentid", (Object)parentid); //$NON-NLS-1$
					shortcut.put((Object)"shortid", (Object)id); //$NON-NLS-1$

					vtShortcuts.add((Object)shortcut);
				}
			}
		}
		catch(Exception ex) {
			System.out.println("Error: (XMLImport.processShortcutRefs) \n\n"+ex.getMessage()); //$NON-NLS-1$
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
				String parentid = (String)next.get((Object)"parentid"); //$NON-NLS-1$
				String shortid = (String)next.get((Object)"shortid"); //$NON-NLS-1$

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
			System.out.println("Error: (XMLImport.processShortcuts) \n\n"+ex.getMessage()); //$NON-NLS-1$
		}
	}

	/**
	 * Process the given XML Node which contains a list of all MediaIndex data to add.
	 *
	 * @param oMediaIndexes the list of media indexes to process.
	 */
	private void processMediaIndexes(NodeList oMediaIndexes) {

		int count = oMediaIndexes.getLength();
		MediaIndex ind = null;
		try {
			NodeSummary node = null;
	      	for ( int i = 0; i < count; i++ ) {
				Node oMediaIndex = oMediaIndexes.item(i);
				NamedNodeMap attrs = oMediaIndex.getAttributes();
				long index = new Long( ((Attr)attrs.getNamedItem("mediaindex")).getValue() ).longValue(); //$NON-NLS-1$
				
				String noderef = ((Attr)attrs.getNamedItem("noderef")).getValue(); //$NON-NLS-1$
				if (htNewNodes.containsKey((Object)noderef)) {
					node = (NodeSummary)htNewNodes.get((Object)noderef);
					noderef = node.getId();
				}
				String viewref = ((Attr)attrs.getNamedItem("viewref")).getValue(); //$NON-NLS-1$
				if (htNewNodes.containsKey((Object)viewref)) {
					node = (NodeSummary)htNewNodes.get((Object)viewref);
					viewref = node.getId();
				}
				String meetingref = ((Attr)attrs.getNamedItem("meetingref")).getValue(); //$NON-NLS-1$
				long created = new Long( ((Attr)attrs.getNamedItem("created")).getValue() ).longValue(); //$NON-NLS-1$
				long lastModified = new Long( ((Attr)attrs.getNamedItem("lastModified")).getValue() ).longValue(); //$NON-NLS-1$				
				
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
	 * Process the given XML Node which contains a list of all Movie data to add.
	 *
	 * @param oMovies the list of movies to process.
	 */
	private void processMovies(NodeList oMovies) {
		
		int count = oMovies.getLength();
		Movie ind = null;
		try {
			String id = "";
	      	for ( int i = 0; i < count; i++ ) {
				Node oMovie = oMovies.item(i);
				NamedNodeMap attrs = oMovie.getAttributes();
				String originalID = ((Attr)attrs.getNamedItem("id")).getValue();
				id = originalID;
				if (!DBNode.getPreserveImportedIds()) {
					id = oModel.getUniqueID();
				}
				
				NodeList children = oMovie.getChildNodes();
				int countj = children.getLength();			
				Vector<MovieProperties> properties = new Vector<MovieProperties>(countj);
				MovieProperties nextProp = null;
				for ( int j = 0; j < countj; j++ ) {
					Node child = children.item(j);
					String name = child.getNodeName();
					if ( name.equals("movieproperties") ) {
				 		NamedNodeMap attrs2 = child.getAttributes();
						String pid = ((Attr)attrs2.getNamedItem("id")).getValue();
						if (!DBNode.getPreserveImportedIds()) {
							pid = oModel.getUniqueID();
						}
						String movieid = id;
						int xPos = new Integer( ((Attr)attrs2.getNamedItem("xPosition")).getValue() ).intValue();
						int yPos = new Integer( ((Attr)attrs2.getNamedItem("yPosition")).getValue() ).intValue();
						int width = new Integer( ((Attr)attrs2.getNamedItem("width")).getValue() ).intValue();
						int height = new Integer( ((Attr)attrs2.getNamedItem("height")).getValue() ).intValue();
						float transparency = new Float( ((Attr)attrs2.getNamedItem("transparency")).getValue() ).floatValue();
						long time = new Long( ((Attr)attrs2.getNamedItem("time")).getValue() ).longValue();
						long created = new Long( ((Attr)attrs2.getNamedItem("created")).getValue() ).longValue();
						long lastModified = new Long( ((Attr)attrs2.getNamedItem("lastModified")).getValue() ).longValue();

						nextProp = new MovieProperties(pid, movieid, xPos, yPos, width, height, transparency, time, new Date(created), new Date(lastModified));
						properties.add(nextProp);
					}
				}
				
				String viewref = ((Attr)attrs.getNamedItem("viewref")).getValue();				
				if (htNewNodes.containsKey((Object)viewref)) {
					NodeSummary node = (NodeSummary)htNewNodes.get((Object)viewref);
					System.out.println("view found = "+node.getId());
					viewref = node.getId();
				} else if (viewref.equals(this.sRootView)) { // map exported from inside moviemap
					viewref = this.oView.getId();
				}
				
				String link = ((Attr)attrs.getNamedItem("link")).getValue();
				String name = ((Attr)attrs.getNamedItem("name")).getValue();
				long startTime = new Long( ((Attr)attrs.getNamedItem("startTime")).getValue() ).longValue();
				long created = new Long( ((Attr)attrs.getNamedItem("created")).getValue() ).longValue();
				long lastModified = new Long( ((Attr)attrs.getNamedItem("lastModified")).getValue() ).longValue();
				ind = new Movie(id, viewref,  link, name, startTime, new Date(created), new Date(lastModified), properties);				
		 		oModel.getMovieService().addMovie(oSession, ind);
			}
		}
		catch(Exception ex) {
			ex.printStackTrace();
			System.out.println("Error: (XMLImport.processMovies) \n\n"+ex.getMessage()); //$NON-NLS-1$
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

			if (name.equals("link")) { //$NON-NLS-1$

    	  		NamedNodeMap attrs = link.getAttributes();

				String id = ((Attr)attrs.getNamedItem("id")).getValue(); //$NON-NLS-1$
				String type = ((Attr)attrs.getNamedItem("type")).getValue(); //$NON-NLS-1$
				String to = ((Attr)attrs.getNamedItem("to")).getValue(); //$NON-NLS-1$
				String from = ((Attr)attrs.getNamedItem("from")).getValue(); //$NON-NLS-1$

				if (to.equals(from))
					continue;

				String sOriginalID = ""; //$NON-NLS-1$
				if ( (Attr)attrs.getNamedItem("questmapid") != null) { //$NON-NLS-1$
		 			sOriginalID	= "QM"+((Attr)attrs.getNamedItem("questmapid")).getValue(); //$NON-NLS-1$ //$NON-NLS-2$
				}
				else {
					sOriginalID = ((Attr)attrs.getNamedItem("originalid")).getValue(); //$NON-NLS-1$
				}

				String author = ((Attr)attrs.getNamedItem("author")).getValue(); //$NON-NLS-1$
				long created = new Long( ((Attr)attrs.getNamedItem("created")).getValue() ).longValue(); //$NON-NLS-1$
				long lastModified = new Long( ((Attr)attrs.getNamedItem("lastModified")).getValue() ).longValue(); //$NON-NLS-1$

				int arrow = -1;
				if (((Attr)attrs.getNamedItem("arrow")) == null) //$NON-NLS-1$
					arrow = ICoreConstants.ARROW_TO;
				else
					arrow = new Integer( ((Attr)attrs.getNamedItem("arrow")).getValue() ).intValue(); //$NON-NLS-1$

				String sLabel = ""; //$NON-NLS-1$
				Attr oLabel = (Attr)attrs.getNamedItem("label"); //$NON-NLS-1$
				if (oLabel != null)
					sLabel = oLabel.getValue();

				LinkProperties props = UIUtilities.getLinkProperties(type);
				props.setArrowType(arrow);
				
				// CHECK IF IMPORTING OLD FORMAT - PRE 1.3.04
				Attr oView = (Attr)attrs.getNamedItem("view"); //$NON-NLS-1$
				if (oView != null) {
					String viewid = oView.getValue();
					View thisview = null;
					if (htNewNodes.containsKey((Object)viewid))
						thisview = (View)htNewNodes.get(viewid);
					else if (viewid.equals(sRootView)) {
						thisview = (View)this.oView;
					} else {
						System.out.println("New node not found in processLinks for:"+viewid);
					}
					
					IModel thismodel = thisview.getModel();

					if (!bIsListImport) {
						if (viewid.equals(sRootView)) {
							createLink(thismodel, thisview, viewid, type, id, sOriginalID, from, to, sLabel, props);
						} else {
							addLink(thismodel, thisview, viewid, type, id, sOriginalID, from, to, sLabel, props);
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

						if ( myname.equals("linkviews") ) //$NON-NLS-1$
							views = mychild;
					}

					NodeList children = views.getChildNodes();
					int kcount = children.getLength();
					try {
				      	for ( int k = 0; k < kcount; k++ ) {

							Node child2 = children.item(k);
							String name2 = child2.getNodeName();
							if ( name2.equals("linkview") ) { //$NON-NLS-1$

								props = UIUtilities.getLinkProperties(type);

						 		NamedNodeMap attrs2 = child2.getAttributes();
								String viewid = ((Attr)attrs2.getNamedItem("id")).getValue(); //$NON-NLS-1$

								Attr oWrapWidth = (Attr)attrs2.getNamedItem("labelWrapWidth"); //$NON-NLS-1$
								if (oWrapWidth != null) {
									props.setLabelWrapWidth(new Integer(oWrapWidth.getValue()).intValue());
								}
								
								Attr oFontSize = (Attr)attrs2.getNamedItem("fontsize"); //$NON-NLS-1$
								if (oFontSize != null) {
									props.setFontSize(new Integer(oFontSize.getValue()).intValue());
								}
								
								Attr oFontFace = (Attr)attrs2.getNamedItem("fontface"); //$NON-NLS-1$
								if (oFontFace != null) {
									props.setFontFace(oFontFace.getValue());	
								}
								
								Attr oFontStyle = (Attr)attrs2.getNamedItem("fontstyle"); //$NON-NLS-1$
								if (oFontStyle != null) {
									props.setFontStyle(new Integer(oFontStyle.getValue()).intValue());
								}
								
								Attr oForeground = (Attr)attrs2.getNamedItem("foreground"); //$NON-NLS-1$
								if (oForeground != null) {
									props.setForeground(new Integer(oForeground.getValue()).intValue());
								} 
								
								Attr oBackground = (Attr)attrs2.getNamedItem("background"); //$NON-NLS-1$
								if (oBackground != null) {
									props.setBackground(new Integer(oBackground.getValue()).intValue());
								}				

								Attr oNext = (Attr)attrs2.getNamedItem("arrowtype"); //$NON-NLS-1$
								if (oNext != null) {
									props.setArrowType(new Integer(oNext.getValue()).intValue());
								} else {
									props.setArrowType(arrow);
								}
								
								Attr oCreated = (Attr)attrs2.getNamedItem("created"); //$NON-NLS-1$
								if (oCreated != null) {
									props.setCreationDate(new Date(new Long(oCreated.getValue()).longValue())); //$NON-NLS-1$
								}
								
								Attr oMod = (Attr)attrs2.getNamedItem("lastModified"); //$NON-NLS-1$
								if (oMod != null) {
									props.setModificationDate(new Date(new Long(oMod.getValue()).longValue())); //$NON-NLS-1$
								}
								
								oNext = (Attr)attrs2.getNamedItem("linkstyle"); //$NON-NLS-1$
								if (oNext != null) {
									props.setLinkStyle(new Integer(oNext.getValue()).intValue());
								}				
								oNext = (Attr)attrs2.getNamedItem("linkdashed"); //$NON-NLS-1$
								if (oNext != null) {
									props.setLinkDashed(new Integer(oNext.getValue()).intValue());
								}				
								oNext = (Attr)attrs2.getNamedItem("linkweight"); //$NON-NLS-1$
								if (oNext != null) {
									props.setLinkWeight(new Integer(oNext.getValue()).intValue());
								}				
								oNext = (Attr)attrs2.getNamedItem("linkcolour"); //$NON-NLS-1$
								if (oNext != null) {
									props.setLinkColour(new Integer(oNext.getValue()).intValue());
								}				

								View thisview = null;
								if (htNewNodes.containsKey((Object)viewid)) {
									thisview = (View)htNewNodes.get(viewid);
								} else if (sRootView.equals(viewid)){
									thisview = (View)this.oView;
								} else {
									System.out.println("not found node="+viewid);
								}

								IModel thismodel = thisview.getModel();
								if (!bIsListImport) {								
									if (viewid.equals(sRootView)) {
										createLink(thismodel, thisview, viewid, type, id, sOriginalID, from, to, sLabel, props);
									} else {
										addLink(thismodel, thisview, viewid, type, id, sOriginalID, from, to, sLabel, props);
									}
								}
							}
						}
					}
					catch(Exception ex) {
						ex.printStackTrace();
						System.out.println("Error: (XMLImport.processLinks) \n\n"+ex.getMessage()); //$NON-NLS-1$
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

		detail += "\n\n(Imported Author: " + author + ")\n"; //$NON-NLS-1$ //$NON-NLS-2$
		detail += "\n(Creation Date: " + (new Date(lCreationDate)).toString() + ")\n"; //$NON-NLS-1$ //$NON-NLS-2$
		detail += "\n(Modification Date: " + (new Date(lModeDate)).toString() + ")\n"; //$NON-NLS-1$ //$NON-NLS-2$
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

		if (sOriginalID.equals("-1")) //$NON-NLS-1$
			sOriginalID = ""; //$NON-NLS-1$

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
			uinode.getNode().setSource(sSource, sImage, new Dimension(imagewidth, imageheight), author);
			if (sImage == null || sImage.equals(""))
				uinode.setReferenceIcon(sSource);
			else
				uinode.setReferenceIcon(sImage);
		}
		else if(View.isViewType(nType)) {

			uinode.getNode().setSource("", sImage, author); //$NON-NLS-1$
			uinode.getNode().setImageSize(new Dimension(imagewidth, imageheight), author);			
			if (sImage != null && !sImage.equals("")) //$NON-NLS-1$
				uinode.setReferenceIcon(sImage);
		}

		if (!background.equals("") && (View.isMapType(nType))) { //$NON-NLS-1$
			View newview = (View)uinode.getNode();
			newview.setBackgroundImage(background);
			try { newview.updateViewLayer(); }
			catch(Exception ex) { System.out.println("Unable to update database with background image "+background);} //$NON-NLS-1$
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

		if (sOriginalID.equals("-1")) //$NON-NLS-1$
			sOriginalID = ""; //$NON-NLS-1$
		else if (!sOriginalID.equals("") && lCreationDate < ICoreConstants.MYSQLDATE) //$NON-NLS-1$
			sOriginalID = "QM"+sOriginalID; //$NON-NLS-1$

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
			node.setSource(sSource, sImage, new Dimension(imagewidth, imageheight), author);
		}
		else if(View.isViewType(nType)) {
			node.setSource("", sImage, new Dimension(imagewidth, imageheight), author); //$NON-NLS-1$
		}

		if (!background.equals("") && (View.isMapType(nType))) { //$NON-NLS-1$
			((View)node).setBackgroundImage(background);
			try { ((View)node).updateViewLayer(); }
			catch(Exception ex) { System.out.println("Unable to update database with background image "+background);} //$NON-NLS-1$
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

		if (sOriginalID.equals("-1")) //$NON-NLS-1$
			sOriginalID = ""; //$NON-NLS-1$
		else if (!sOriginalID.equals("") && lCreationDate < ICoreConstants.MYSQLDATE) //$NON-NLS-1$
			sOriginalID = "QM"+sOriginalID; //$NON-NLS-1$

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

		NodePosition nodePos = view.addMemberNode(nType, "", importedId, sOriginalID, author, oCreationDate,  //$NON-NLS-1$
				oModfificationDate, label, detail, ptPos.x, ptPos.y, transCreationDate, transModDate, 
				sLastModAuthor, bShowTags, bShowText, bShowTrans, bShowWeight, bSmallIcon, bHideIcon,
				nWrapWidth, nFontSize, sFontFace, nFontStyle, nForeground, nBackground);

		if(nType == ICoreConstants.REFERENCE || nType == ICoreConstants.REFERENCE_SHORTCUT) {
			nodePos.getNode().setSource(source, image, new Dimension(imagewidth, imageheight), author);
		}
		else if(View.isViewType(nType)) {
			nodePos.getNode().setSource("", image,  new Dimension(imagewidth, imageheight),author); //$NON-NLS-1$
		}

		if (!background.equals("") && (View.isMapType(nType))) { //$NON-NLS-1$
			((View)nodePos.getNode()).setBackgroundImage(background);
			try { ((View)nodePos.getNode()).updateViewLayer(); }
			catch(Exception ex) { System.out.println("Unable to update database with background image "+background);} //$NON-NLS-1$
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
													LinkProperties 	props ) {

		if(View.isMapType(view.getType())) {

			UINode fromUINode = (UINode)htUINodes.get((Object)sFromId);
			UINode toUINode = (UINode)htUINodes.get((Object)sToId);

			//int type = UILink.getLinkType(sType);

			NodeUI nodeui = toUINode.getUI();

			UILink uiLink = nodeui.createLink(sImportedId, fromUINode, toUINode, sType, sLabel, props);
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
													LinkProperties 	props) {

		if(View.isMapType(view.getType())) {

			Hashtable viewNodePositions = (Hashtable)htNodeView.get((Object) currentViewId);
			if (viewNodePositions == null)
				return;

			NodePosition fromNode = (NodePosition) viewNodePositions.get((Object) sFromId);
			NodePosition toNode = (NodePosition) viewNodePositions.get((Object) sToId);

			if (sOriginalID.equals("-1")) //$NON-NLS-1$
				sOriginalID = ""; //$NON-NLS-1$

			try {
				LinkProperties linkprops = (LinkProperties)view.addMemberLink(sType,
												sImportedId,
												sOriginalID,
												sCurrentAuthor,
												fromNode.getNode(),
												toNode.getNode(),
												sLabel,
												props);

				// FOR UNDO ON CANCEL
				vtLinkList.addElement(linkprops.getLink());
			}
			catch(Exception ex) {
				ex.printStackTrace();
				System.out.println("Error: (XMLImport.addLink) \n\n"+ex.getMessage()); //$NON-NLS-1$
			}
		}
	}
}
