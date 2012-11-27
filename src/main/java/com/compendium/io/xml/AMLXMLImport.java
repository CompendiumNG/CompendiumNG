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
import com.compendium.ui.edits.AlignEdit;

import org.w3c.dom.*;

/**
 * Flashmeeting XMLImport imports Flashmeeting xml text file conforming to the Flashmeeting dtd.
 *
 * @author	Michelle Bachler
 * @version	1.0
 */
public class AMLXMLImport extends Thread {

	private static String sPath = "Templates"+ProjectCompendium.sFS; //$NON-NLS-1$

	/** Static text for agenda item tag.*/
	private static final String 	CONCLUSION_TAG_TEXT	=	"Conclusion"; //$NON-NLS-1$

	/** Static text for attendee item tag.*/
	private static final String 	QUESTION_TAG_TEXT	=	"Critical Question"; //$NON-NLS-1$

	/** Static text for document item tag.*/
	private static final String 	PREMISE_TAG_TEXT	=	"Premise"; //$NON-NLS-1$
		
	/** The x offset to place AgendaItem, Document and Attendee nodes at.*/
	private static final int		X_OFFSET			=	200;

	/** The spacer between nodes on the y axis.*/;
	private static final int		Y_SPACER			=	65;

	/** the tag for the main grouping.*/
	private Code oFolderCode = null;
	
	/** the tag for Conclusion items.*/
	private Code oConclusionCode = null;

	/** the tag for Question items.*/
	private Code oQuestionCode = null;

	/** the tag for Premise items.*/
	private Code oPremiseCode = null;

	/** The IModel object for the current database connection.*/
	private IModel				oModel 				= null;

	/** The current Session object.*/
	private PCSession 			oSession 			= null;

	/** The file name of the xml file to import.*/
	private String				sFileName			= ""; //$NON-NLS-1$

	/** Holds the author name of the current user.*/
	private String				sAuthor		= ""; //$NON-NLS-1$

	// FOR PROGRESS BAR
	/** The progress bar counter.*/
	private int nCount					= 0;

	/** The dialog which displays the progress bar.*/
	private UIProgressDialog	oProgressDialog 	= null;

	/** The progress bar.*/
	private JProgressBar		oProgressBar 		= null;

	/** The Thread class which runs the progress bar.*/
	private ProgressThread		oThread 			= null;

	/** The x position of the main map.*/
	private int nMainMapX				= 200;
	
	/** The y position of the main map.*/
	private int nMainMapY				= 200;

	
	/** The main name for the set of imports.*/
	private String sFolderName			= ""; //$NON-NLS-1$
	
	/**
	 * Constructor.
	 *
	 * @param fileName, the name of the xml file to import.
	 * @param model com.compendium.core.datamodel.IModel, the current model object.
	 * be added to the node detail text, else false.
	 */
	public AMLXMLImport(String fileName, IModel model) {
		
		this.sFileName = fileName;
		File file = new File(sFileName);
		String sName = file.getName();
		this.sFolderName = sName.substring(0, sName.indexOf(".")); //$NON-NLS-1$
		this.oModel = model;
		if (oModel == null)
			oModel = ProjectCompendium.APP.getModel();

		this.sAuthor = oModel.getUserProfile().getUserName();
		this.oSession = oModel.getSession();
	}

	/**
	 * Constructor.
	 *
	 * @param fileName, the name of the xml file to import.
	 * @param model com.compendium.core.datamodel.IModel, the current model object.
	 * @param x the x location to put the main map (if current map not a list).
	 * @param y the y location to put the main map (if current map not a list).
	 * be added to the node detail text, else false.
	 */
	public AMLXMLImport(String fileName, IModel model, int x, int y) {
		
		this.nMainMapX = x;
		this.nMainMapY = y;
		this.sFileName = fileName;
		File file = new File(sFileName);
		String sName = file.getName();
		this.sFolderName = sName.substring(0, sName.indexOf(".")); //$NON-NLS-1$
		this.oModel = model;
		if (oModel == null)
			oModel = ProjectCompendium.APP.getModel();

		this.sAuthor = oModel.getUserProfile().getUserName();
		this.oSession = oModel.getSession();
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
	 * The Thread class which draws the process bar dialog.
	 */
	private class ProgressThread extends Thread {

		public ProgressThread() {
	  		oProgressDialog = new UIProgressDialog(ProjectCompendium.APP,LanguageProperties.getString(LanguageProperties.IO_BUNDLE, "AMLXMLImport.progressMessage"), LanguageProperties.getString(LanguageProperties.IO_BUNDLE, "AMLXMLImport.progressTitle")); //$NON-NLS-1$ //$NON-NLS-2$
	  		oProgressDialog.showDialog(oProgressBar, false);
	  		oProgressDialog.setModal(true);
		}

		public void run() {
	  		oProgressDialog.setVisible(true);
		}
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
			Document document = reader.read(uri, false);			
			if (document != null) {
				processDocument( document );
			} else {
				ProjectCompendium.APP.displayError(LanguageProperties.getString(LanguageProperties.IO_BUNDLE, "AMLXMLImport.errorImporting")+"\n");	 //$NON-NLS-1$
			}
			document = null;
        }
		catch ( Exception e ) {
			ProjectCompendium.APP.displayError(LanguageProperties.getString(LanguageProperties.IO_BUNDLE, "AMLXMLImport.errorImporting2")+":\n\n"+e.getLocalizedMessage()); //$NON-NLS-1$
			e.printStackTrace();
        }
    }

	//*************************************************************************************************//

	/**
	 * Process the given XML document and create all the view, nodes, links, codes etc required.
	 * @param document, the XML Document object to process.
	 */
	private void processDocument( Document document ) {
		try {
	 		ProjectCompendium.APP.setWaitCursor();
	 		UIViewFrame oViewFrame =ProjectCompendium.APP.getCurrentFrame();	 		
			NodeList schemes = document.getElementsByTagName("SCHEME"); //$NON-NLS-1$
	
			View view = null;
			UIViewPane oUIViewPane = null;
			IModel oModel = ProjectCompendium.APP.getModel();
			PCSession oSession = (PCSession)oModel.getSession();
			UINode newMap = null;
	
			oFolderCode = CoreUtilities.checkCreateCode(sFolderName, oModel, oSession, sAuthor);
			oConclusionCode = CoreUtilities.checkCreateCode(CONCLUSION_TAG_TEXT, oModel, oSession, sAuthor);
			oPremiseCode = CoreUtilities.checkCreateCode(PREMISE_TAG_TEXT, oModel, oSession, sAuthor);
			oQuestionCode = CoreUtilities.checkCreateCode(QUESTION_TAG_TEXT, oModel, oSession, sAuthor);

			if (oViewFrame instanceof UIMapViewFrame) {
				UIMapViewFrame map = (UIMapViewFrame) oViewFrame;
				oUIViewPane = map.getViewPane();
				ViewPaneUI oViewPaneUI = oUIViewPane.getUI();
	
				String sDetails = ""; //$NON-NLS-1$
				newMap = oViewPaneUI.createNode(ICoreConstants.LISTVIEW,
											 "", //$NON-NLS-1$
											 sAuthor,
											 sFolderName,
											 "", //$NON-NLS-1$
											 nMainMapX,
											 nMainMapY);
	
				// GIVE IT THE SPECIAL FLASHMEETING MAP IMAGE
				try {
					NodeSummary nodeSum = newMap.getNode();
					nodeSum.initialize(oSession, oModel);
					if (oFolderCode != null) {
						nodeSum.addCode(oFolderCode);
					}
				} catch(Exception e) {}
	
				view = ((View)newMap.getNode());
			}
			else {
				UIListViewFrame list = (UIListViewFrame) oViewFrame;
				UIList oUIList = list.getUIList();
				ListUI oListUI = oUIList.getListUI();
	
				NodePosition newMap2 = oListUI.createNode(ICoreConstants.LISTVIEW,
											 "", //$NON-NLS-1$
											 sAuthor,
											 sFolderName,
											 "", //$NON-NLS-1$
											 0,
											 ((oUIList.getNumberOfNodes() + 1) * 10)										 
											 );
	
				// GIVE IT THE SPECIAL MEETING MAP IMAGE
				try {
					NodeSummary nodeSum = newMap.getNode();
					nodeSum.initialize(oSession, oModel);
					if (oFolderCode != null) {
						nodeSum.addCode(oFolderCode);
					}
				} catch(Exception e) {}
	
				view = ((View)newMap2.getNode());
			}
			
			if (view != null) {
		  		oProgressBar.setMaximum(schemes.getLength());
		  		
				processSchemes(schemes, view);

				// set size of main map window based on extent of node positions.
				view.initializeMembers();
			}

			ProjectCompendium.APP.refreshIconIndicators();
			ProjectCompendium.APP.setDefaultCursor();
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}	

	/**
	 * Process scheme data and create nodes.
	 * @param items the playlist xml items
	 * @param view the view to put the main node in.
	 */
	private void processSchemes(NodeList items, View view) {

		int count = items.getLength();
		NamedNodeMap attrs = null;
		Node item = null;
		Node form = null;
		Node next = null;
		Node conclusion = null;
		Node name = null;
		String sName = ""; //$NON-NLS-1$
		String sLabel = ""; //$NON-NLS-1$
		Node first = null;
		Vector premises = null;
		Vector questions = null;
		Vector nodes = null;
		NodeSummary nodeSum = null;
		NodeSummary nodeSum2 = null;
		
		File sMainFilePath = null;
		if (count > 0) {
			sMainFilePath = new File(sPath+"Argumentation Schemes"+ProjectCompendium.sFS+sFolderName); //$NON-NLS-1$
			if (!sMainFilePath.exists()) {
				sMainFilePath.mkdirs();
			}
		}

		for (int i=0; i<count; i++) {
			item = items.item(i);
			name = XMLReader.getFirstChildWithTagName(item, "NAME"); //$NON-NLS-1$
			first = name.getFirstChild();
			if (first != null) {
				sName = first.getNodeValue();
				sName = sName.trim();
			}

			try {
				NodePosition nodePos = null;
				NodePosition nodePos2 = null;

				NodePosition oMap = view.addMemberNode(ICoreConstants.MAPVIEW, "", "", sAuthor, sName, "", 0, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
						 ((view.getNumberOfNodes() + 1) * 10));
				View oView = (View)oMap.getNode();
				oView.initialize(oSession, oModel);
				if (oFolderCode != null) {
					oView.addCode(oFolderCode);
				}

				form = XMLReader.getFirstChildWithTagName(item, "FORM"); //$NON-NLS-1$
				conclusion = XMLReader.getFirstChildWithTagName(form, "CONCLUSION"); //$NON-NLS-1$
				
				premises = XMLReader.getChildrenWithTagName(form, "PREMISE"); //$NON-NLS-1$
				questions = XMLReader.getChildrenWithTagName(item, "CQ"); //$NON-NLS-1$
				
				first = conclusion.getFirstChild();
				if (first != null) {
					sLabel = first.getNodeValue();
				}
				
				nodePos = oView.addMemberNode( ICoreConstants.POSITION, "", "", sAuthor, sLabel, "", 10, 50); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				nodeSum = nodePos.getNode();
				nodeSum.initialize(oSession, oModel);
				if (oFolderCode != null) {
					nodeSum.addCode(oFolderCode);
				}
				if (oConclusionCode != null) {
					nodeSum.addCode(oConclusionCode);
				}

				int y=5;
				int countj = premises.size();
				for (int j=0; j<countj; j++) {
					next = (Node)premises.elementAt(j);
					first = next.getFirstChild();
					if (first != null) {
						sLabel = first.getNodeValue();
					}
					nodePos2 = oView.addMemberNode( ICoreConstants.PRO, "", "", sAuthor, sLabel, "", X_OFFSET, y); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					nodeSum2 = nodePos2.getNode();
					nodeSum2.initialize(oSession, oModel);
					if (oFolderCode != null) {
						nodeSum2.addCode(oFolderCode);
					}
					if (oPremiseCode != null) {
						nodeSum2.addCode(oPremiseCode);
					}
					
					LinkProperties props = UIUtilities.getLinkProperties(ICoreConstants.SUPPORTS_LINK);
					props.setArrowType(ICoreConstants.ARROW_TO);
					oView.addMemberLink(ICoreConstants.SUPPORTS_LINK, "0", sAuthor, nodeSum2, nodeSum, props); //$NON-NLS-1$

					y += Y_SPACER;
				}
				
				oView.initializeMembers();			
				UIArrangeLeftRight arrange = new UIArrangeLeftRight();
				arrange.arrangeView(oView, new UIMapViewFrame(oView));
				UIMapViewFrame frame = new UIMapViewFrame(oView);
				//Dimension size = frame.getViewPane().calculateSize();
				
				y=20;
				countj = questions.size();
				Dimension dim = null;
				nodes = new Vector(countj);
				for (int j=0; j<countj; j++) {
					next = (Node)questions.elementAt(j);
					first = next.getFirstChild();
					if (first != null) {
						sLabel = first.getNodeValue();
					}
					nodePos2 = oView.addMemberNode( ICoreConstants.ISSUE, "", "", sAuthor, sLabel, "", 250, y); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					nodes.addElement(nodePos2);
					nodeSum2 = nodePos2.getNode();
					nodeSum2.initialize(oSession, oModel);
					if (oFolderCode != null) {
						nodeSum2.addCode(oFolderCode);
					}
					if (oQuestionCode != null) {
						nodeSum2.addCode(oQuestionCode);
					}
					
					dim = (new UINode(nodePos2, sAuthor)).getPreferredSize();												
					y += dim.height+20;
				}
				
				alignCenter(nodes);
											
				boolean selectedOnly = false;
				boolean allDepths = true;
				boolean withStencilsAndLinkGroups = false;
				boolean withMeetings = false;		
				boolean withMovies = false;
				boolean toZip = false;
				
				
				String sFileName = CoreUtilities.cleanFileName(sName)+".xml";	 //$NON-NLS-1$
				File xmlFile = new File(sMainFilePath.getAbsolutePath()+ProjectCompendium.sFS+sFileName);
				XMLExportNoThread export = new XMLExportNoThread(frame, xmlFile.getAbsolutePath(), allDepths, selectedOnly, toZip, withStencilsAndLinkGroups, withMovies, withMeetings, false);
				
				nCount++;
				oProgressBar.setValue(nCount);
				oProgressDialog.setStatus(nCount);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * To perform center align
	 */
	private Dimension alignCenter(Vector nodes) {
		
		Dimension maindim = new Dimension(0,0);
		
		double sXPos = 0;
		double lXPos = 0;
		double center = 0;
		double xPos = 0;
		double yPos = 0;
		double width = 0;
		double height = 0;
        double maxWidth = 0;
        double maxHeight = 0;

		Dimension dim = null;
		NodePosition nodePos = null;
		int count = nodes.size();
		for(int i=0; i<count; i++){
			nodePos = (NodePosition)nodes.elementAt(i);
			xPos = nodePos.getXPos();
			yPos = nodePos.getYPos();
			
			dim = (new UINode(nodePos, sAuthor)).getPreferredSize();			
			width = xPos+dim.getWidth();
			height = yPos+dim.getHeight();
            if( width > maxWidth){
                maxWidth = width;
            }
            if( height > maxHeight){
                maxHeight = height;
            }
 
			if(i == 0 || sXPos > xPos){
				sXPos = xPos;
			}
			if(i == 0 || lXPos < (xPos +width) ) {
				lXPos = xPos + width;
			}
		}
		
		maindim = new Dimension( new Double(maxWidth).intValue(), new Double(maxHeight).intValue());

		center = (sXPos + lXPos) /2 ;
		for(int i=0; i<count; i++){
			nodePos = (NodePosition)nodes.elementAt(i);
			dim = (new UINode(nodePos, sAuthor)).getPreferredSize();			
			width = dim.getWidth();
			Point p = nodePos.getPos();
			Point pt = new Point();
			pt.setLocation(center - ( width /2), p.y);
			nodePos.setPos(pt);
		}
		
		return maindim;
	}	
}
