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

package com.compendium.io.html;

import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.*;
import java.text.*;
import java.util.*;
import java.util.zip.*;

import javax.swing.*;

import com.compendium.*;
import com.compendium.ui.*;
import com.compendium.ui.dialogs.UIHTMLFormatDialog;

import com.compendium.core.CoreUtilities;
import com.compendium.core.datamodel.*;
import com.compendium.core.datamodel.services.*;
import com.compendium.core.ICoreConstants;

/**
 * The HTMLOutline Class generates a HTML Document.
 *
 * @author Cheralathan Balakrishnan / Michelle Bachler
 */
public class HTMLOutline implements IUIConstants {

	/** Indicates whether to include the node detail pages in the HTML export.*/
	private boolean				bPrintNodeDetail 		= false;

	/** Indicates whether to filter node detail pages by certain dates when exporting.*/
	private boolean				bPrintNodeDetailDate 	= false;

	/** The date to export the node details pages from.*/
	private GregorianCalendar 	fromDate 				= null;

	/** The date to export the node detail pages to.*/
	private GregorianCalendar 	toDate 					= null;

	/** Indicates whether to include the node author information in the HTML export.*/
	private boolean			bPrintNodeAuthor 			= false;

	/** Indicates the indent level to start the export at.*/
	private boolean			bStartExportAtLevel 		= false;

	/** Indicates whether to include any node image in the export.*/
	private boolean			bIncludeImage 				= false;

	/** Indicates whether to include any link labels in the export.*/
	private boolean			bIncludeLinks 				= false;

	/** Indicates whether to export each view in a separate HTML file or all in the same file.*/
	private boolean			bDisplayInDifferentPages 	= false;

	/** Indicates whether to dispaly the node detail page dates when exporting.*/
	private boolean			bDisplayDetailDates	 		= false;

	/** NOT CURRENTLY USED */
	private boolean			bHideNodeNoDates 			= false;

	/** Indicates whether to include node anchors.*/
	private boolean			bIncludeNodeAnchors			= false;

	/** Indicates whether to include anchors on each detail page exported to HTML.*/
	private boolean			bIncludeDetailAnchors		= false;

	/** Indicates whether to use anchor numbers / or images for the anchors exported.*/
	private boolean			bUseAnchorNumbers			= false;

	/** Indicates whether to export associated reference files in the export.*/
	private boolean			bIncludeReferences			= false;

	/** Indicates whether to put all the exported files in a zip file.*/
	private boolean			bZipUp						= false;

	/** Indicates whether to include a navigation bar (list of exported view) in the HTML export.*/
	private boolean			includeNavigationBar 		= true;

	/** Indicates whether to include parent views in the export.*/
	private boolean			bIncludeViews 				= false;

	/** Indicates whether to include tags in the expport.*/
	private boolean			bIncludeTags 				= false;

	/** Indicates whether to include parent view references in the body of the text.*/
	private boolean			inlineView 					= false;

	/** Indicates whether to put parent views in external files.*/
	private boolean			newView 					= false;

	/** Used to check if the main first export file is being processed.*/
	private boolean 		firstTime 					= true;

	/** Indicates whether the current view should be made the home view.*/
	private boolean 		currentViewPresent 			= false;

	/** The name of the file to export to.**/
	private String			fileName 					= "";

	/** Store a node label while processing.*/
	private String			sNodeLabel 					= "";

	/** Store a node detail page while processing.*/
	private String			sNodeDetail 				= "";

	/** Store a node author while processing.*/
	private	String			sNodeAuthor 				= "";

	/** The default title to use.*/
	private String			title 						= "Compendium";

	/** The user specified title for the mail HTML page.*/
	private String			originalTitle 				= title;

	/** The navigation table HTML data.*/
	private String			tableIndex 					= null;

	/** The directory to export to.*/
	private String			directory 					= null;

	/** The name of the current View.*/
	private String			currentViewName 			= null;

	/** The name of the anchor image to use for anchors.*/
	private String			sAnchorImage 				= "";

	/** The current indent level.*/
	private int				nLevel 						= 0;

	/** The starting export level.*/
	private int				nStartExportAtLevel 		= 0;

	/** The previous export level.*/
	private int				previousLevel 				= 0;

	/** Holds the list of nodes being exported.*/
	private Vector			nodeList;

	/** Holds a list of node export levels.*/
	private Vector			nodeLevelList;

	/** Holds a list of the Views being exported.*/
	private Vector			viewList 					= null;

	/** Contains Vector of the nodes in each view.*/
	private Vector			nodeListInViewList 			= null;

	/** Contains hastable of the node position objects with node id as key.*/
	private Hashtable			htNodePositions	 			= null;

	/** Holds a list of all reference and image files being exported against the Data Strings.*/
	private Hashtable		htExportFiles 				= new Hashtable();

	/** Holds all the newly created files against thier data Strings.*/
	private Hashtable		htCreatedFiles				= new Hashtable();

	/** The name of the backup file stub (for either .zip or .html).*/
	private String			sBackupName 				= "";

	/** The file name of the HTML file currently being created.*/
	private String 			sCurrentFileName 			= "";

	/** The platform specific file separator to use when creating new files.*/
	private String			sFS 						= System.getProperty("file.separator");

	/** Store the HTML string being created for the export.*/
	private StringBuffer	rootFile 					= new StringBuffer(1000);

	/** Used to load the stored properties.*/
	private ResourceBundle	bundle;

	/** The session object to use while processing.*/
	private	PCSession		session 					= null;

	/** The ViewService to use while processing.*/
	private	IViewService	vs 							= null;

	/** The list of nodes to process for Export.*/
	private Vector 			nodeIndexList 				= new Vector();

	/** The FileWriter used to write out the created HTML files.*/
	private	FileWriter		fileWriter 					= null;

	/** Date format to use when printint node detail page dates.*/
	private static SimpleDateFormat sdf 				= new SimpleDateFormat("d MMM, yyyy");

	/** The colour for the anchor numbers.*/
	private static String 	purple						= "#C8A8FF"; //(200, 168, 255);

	/** hold increment when using numbers for the node anchors.*/
	private int				anchorCount					= 1;

	/** The id of the current view being processed.*/
	private String			sCurrentViewID 				= "";
	
	/** Holds messages about missing reference files.*/
	private Vector 			vtMessages					= new Vector();
	
	/** The id of the first, top level view (page) for the export.*/
	private String			sMainView					= "";
	
	private Properties		oFormatProperties			= null;

	/**
	 * Constructor.
	 *
	 * @param bPrintNodeDetail inlcude the node detail in the export.
	 * @param bPrintNodeDetailDate inlcude the node detail dates for each page exported.
	 * @param bPrintAuthor include the author information in the export.
	 * @param nExportLevel the starting export level.
	 * @param sExportFile The file name of the file to export to.
	 * @param bToZip whether to export to a zip file.
	 */
	public HTMLOutline(boolean bPrintNodeDetail, boolean bPrintNodeDetailDate, 
			boolean bPrintAuthor, int nExportLevel, String sExportFile, boolean bToZip) {

		this.bPrintNodeDetail = bPrintNodeDetail;
		this.bPrintNodeDetailDate = bPrintNodeDetailDate;
		this.bPrintNodeAuthor = bPrintAuthor;
		bZipUp = bToZip;
		nStartExportAtLevel = nExportLevel;

		processExport(sExportFile);
	}

	/**
	 * Set various variables and load the export properties ready to run the export.
	 *
	 * @param exportFile the name of the file to export to.
	 */
	public void processExport(String exportFile) {

		fileName = exportFile;

		sBackupName = "";
		String name = new File(fileName).getName();
		int ind = name.lastIndexOf(".");
		if (ind != -1) {
			sBackupName = name.substring(0, ind);
		}

	 	int directoryIndex = fileName.lastIndexOf(File.separator);
		if (directoryIndex == -1) {
			directoryIndex = fileName.lastIndexOf('/');
		}

		//If string exists....take the substring that starts at
		//the beginning up to the directoryIndex, represented by the separator
		//character (usu / for unix and \ for WIN)
		if (directoryIndex != -1) {
			directory = fileName.substring(0, directoryIndex);
		} else {
			directory = "";
		}

		if (bZipUp) {
			sCurrentFileName = sBackupName+"_Outline.html";
		} else {		
			sCurrentFileName = fileName;
		}

		nodeList = new Vector(51);
		nodeLevelList = new Vector(51);
		viewList = new Vector(51);
		nodeListInViewList = new Vector(51);
		htNodePositions = new Hashtable(51);
		session = ProjectCompendium.APP.getModel().getSession();
		vs = ProjectCompendium.APP.getModel().getViewService();

		try {
			File oFile = new File(UIHTMLFormatDialog.DEFAULT_FILE_PATH+ProjectCompendium.sFS+UIHTMLFormatDialog.DEFAULT_FILE_NAME);

			File main = new File(UIHTMLFormatDialog.DEFAULT_FILE_PATH);
			File styles[] = main.listFiles();
			File file = null;
			String sName = "";
			if (styles.length > 0) {			
				for (int i=0; i<styles.length; i++) {
					file = styles[i];
					Properties styleProp = new Properties();
					styleProp.load(new FileInputStream(file));
					String value = styleProp.getProperty("name");
					if (value != null) {
						sName = value;
						if (sName.equals(FormatProperties.outlineFormat)) {
							oFile = file;
							break;
						}
					}
				}		
			}
			oFormatProperties = new Properties();
			oFormatProperties.load(new FileInputStream(oFile));

		} catch (Exception e) {
			ProjectCompendium.APP.displayError("Unable to load formatting style for export");
		}		
	}

	// GETTERS AND SETTER

	/**
	 * Set whether to include external reference file in the export.
	 *
	 * @param boolean includeRefs, true if you want to include external reference file in the export, else false.
	 */
	public void setIncludeFiles(boolean includeRefs) {
		bIncludeReferences = includeRefs;
	}

	/**
	 * Set whether to export to a zip file.
	 *
	 * @param boolean zipUp, true if you want to export to a zip file, else false.
	 */
	public void setZipUp(boolean zipUp) {
		bZipUp = zipUp;
	}

	/**
	 * Set whether to include node anchors in the export.
	 *
	 * @param includeNodeAnchors true if you want to include node anchors in the export, else false.
	 */
	public void setIncludeNodeAnchors(boolean includeNodeAnchors) {
		bIncludeNodeAnchors = includeNodeAnchors;
	}

	/**
	 * Set whether to include detila anchors in the export.
	 *
	 * @param boolean includeDetailAnchors, true if you want to include detail anchors in the export, else false.
	 */
	public void setIncludeDetailAnchors(boolean includeDetailAnchors) {
		bIncludeDetailAnchors = includeDetailAnchors;
	}

	/**
	 * Set whether to use anchor numbers or images in the export.
	 *
	 * @param boolean useAnchorNumbers, true if you want to use anchor numbers, false if you want to use images.
	 */
	public void setUseAnchorNumbers(boolean useAnchorNumbers) {
		bUseAnchorNumbers = useAnchorNumbers;
	}

	/**
	 * Set path of the anchor image to use.
	 *
	 * @param String image, tha path of the image file to use for the anchors.
	 */
	public void setAnchorImage(String image) {
		sAnchorImage = image;
	}

	/**
	 * Set whether to include node images in the export.
	 *
	 * @param boolean includeImage, true if you want to include node images in the export, else false.
	 */
	public void setIncludeImage(boolean includeImage) {
		bIncludeImage = includeImage;
	}

	/**
	 * Set whether to include link labels in the export.
	 *
	 * @param boolean includeLinks, true if you want to include link labels in the export, else false.
	 */
	public void setIncludeLinks(boolean includeLinks) {
		bIncludeLinks = includeLinks;
	}

	/**
	 * Set the title of the HTML main export file.
	 *
	 * @param String htmltitle, the title to put in the main html file.
	 */
	public void setTitle(String htmltitle) {
		title = htmltitle;
		originalTitle = title;
	}

	/**
	 * Set whether to display detail dates in the export.
	 *
	 * @param boolean display, true if you want to display detail dates in the export, else false.
	 */
	public void setDisplayDetailDates(boolean display) {
		bDisplayDetailDates = display;
	}

	/**
	 * NOT CURRENT USED.
	 *
	 * @param boolean hide
	 */
	public void setHideNodeNoDates(boolean hide) {
		bHideNodeNoDates = hide;
	}

	/**
	 * Set whether to display each view in a separate file.
	 *
	 * @param boolean display, true if you want to export each view in a different file, false if you want them all in one file.
	 */
	public void setDisplayInDifferentPages(boolean display) {
		bDisplayInDifferentPages = display;
	}

	/**
	 * Set whether to make the current view the home page of the export.
	 *
	 * @param viewHomePage true if you want to make the current view the home page view.
	 */
	public void setCurrentViewAsHomePage(boolean viewHomePage) {
		currentViewPresent = viewHomePage;
	}

	/**
	 * Set whether to include a navigation bar at the side if the export listing all view.
	 *
	 * @param navigationBar true if you want to include a navigation bar, else false.
	 */
	public void setIncludeNavigationBar(boolean navigationBar) {
		includeNavigationBar = navigationBar;
	}

	/**
	 * Set whether to include parent view references in the export.
	 *
	 * @param includeViews true if you do not want to include parent view references in the export.
	 */
	public void setIncludeViews(boolean includeViews) {
		bIncludeViews = includeViews;
	}

	/**
	 * Set whether to include tags in the export.
	 *
	 * @param inlucdeTags true if you do not want to include tags in the export.
	 */
	public void setIncludeTags(boolean includeTags) {
		bIncludeTags = includeTags;
	}

	/**
	 * Set whether to put parent view references in the main export text body or in a separate file.
	 *
	 * @param doInlineView true if you want to put parent view references in the main export text body.
	 */
	public void setInlineView(boolean doInlineView) {
		inlineView = doInlineView;
	}

	/**
	 * Set whether to include parent views in a new file.
	 *
	 * @param doNewView true if you want to include parent views in a new file.
	 */
	public void setNewView(boolean doNewView) {
		newView = doNewView;
	}

	/**
	 * Set whether toDate for node detail pages.
	 *
	 * @param toDate the date to filter node detail pages to.
	 */
	public void setToDate(GregorianCalendar toDate) {
		this.toDate = toDate;
	}

	/**
	 * Set whether fromDate for node detail pages.
	 *
	 * @param fromDate the date to filter node detail pages from.
	 */
	public void setFromDate(GregorianCalendar fromDate) {
		this.fromDate = fromDate;
	}

	// OTHER METHODS

	/**
	 * This function creates various necessary Vector Lists.
	 *
	 * @param node the node of the current view.
	 * @param level the indent starting level.
	 * @param index -1.
	 */
	public void runGenerator(NodeSummary oNode, int level, int index) {

		//Add node and its attributes to respective Vectors
		nodeList.addElement(oNode);
		nodeIndexList.addElement(new Integer(index));
		nodeLevelList.addElement(new Integer(level));

		try {
			if((oNode instanceof View) && (level == 0)) {

				viewList.addElement(oNode);

				Vector npList = vs.getNodePositions(session, oNode.getId());

				//In this vector, each element is another vector...
				nodeListInViewList.addElement(new Vector());

				//for the size of npList, add into the last position within the nodeListInViewList vector
				//a vector of elements:  nodes at element i in npList(which are node Positions)
				NodePosition npos = null;
				for (int i = 0; i < npList.size(); i++) {
					npos = (NodePosition)npList.elementAt(i);
					((Vector)nodeListInViewList.lastElement()).addElement(npos.getNode());
					htNodePositions.put(npos.getNode().getId(), npos);
				}
			}
		}
		catch (Exception e) {
			ProjectCompendium.APP.displayError("Exception: (HTMLOutline.runGenerator) " + e.getMessage());
		}
	}

	/**
	 * Format the given String, by replacing all '\n' with the current HTML 'newline' character.
	 *
	 * @param oldString the String to format.
	 * @return String the formatted String.
	 */
	public String formatString(String oldString) {

		String newLine = "<br>";
		oldString = oldString.trim();
		StringBuffer sb = new StringBuffer(oldString);

		for (int i = 0; i < sb.length(); i++) {
			if (sb.charAt(i) == '\n') {
				sb.setCharAt(i, newLine.charAt(newLine.length()-1));
				sb.insert(i, newLine.substring(0, newLine.length()-1));
			}
		}
		return sb.toString();
	}	

	/**
	 * Create the filename for the given view.
	 * @param view
	 * @return
	 */
	private String createFileName(View view) {
		String sViewFileName = "";
		if (view.getId().equals(sMainView)) {
			if (bZipUp) {
				sViewFileName = sBackupName+"_Outline.html";
			} else {		
				sViewFileName = fileName;
			}
		} else {
			String sLabel = view.getLabel();
			if (sLabel.length()> 20) {
				sLabel = sLabel.substring(0, 20);
			}
			sViewFileName = CoreUtilities.cleanFileName(sLabel)+"_"+view.getId()+"_Outline.html";
		}
		return sViewFileName;
	}
	
	/**
	 * Get the Alt property string to use for the node icon area based on the node type passed. 
	 * @param nType
	 * @return
	 */
	private String getNodeTypeDescription(int nType) {
		String label = "Unknown Node Type Icon";
	    switch (nType) {
		case ICoreConstants.ISSUE:
			label="Question Node";
		    break;

		case ICoreConstants.POSITION:
			label="Answer Node";
		    break;

		case ICoreConstants.ARGUMENT:
			label="Argument Node";
		    break;

		case ICoreConstants.REFERENCE:
			label="Reference Node";
		    break;

		case ICoreConstants.DECISION:
			label="Decision Node";
		    break;

		case ICoreConstants.NOTE:
			label="Note Node";
		    break;

		case ICoreConstants.MAPVIEW:
			label="Map Node";
		    break;

		case ICoreConstants.LISTVIEW:
			label="List Node";
		    break;

		case ICoreConstants.PRO:
			label="Pro Node";
		    break;

		case ICoreConstants.CON:
			label="Con Node";
		    break;

		case ICoreConstants.ISSUE_SHORTCUT:
			label="Question Shortcut Node";
		    break;

		case ICoreConstants.POSITION_SHORTCUT:
			label="Answer Shortcut Node";
		    break;

		case ICoreConstants.ARGUMENT_SHORTCUT:
			label="Argument Shortcut Node";
		    break;

		case ICoreConstants.REFERENCE_SHORTCUT:
			label="Reference Shortcut Node";
		    break;

		case ICoreConstants.DECISION_SHORTCUT:
			label="Decision Shortcut Node";
		    break;

		case ICoreConstants.NOTE_SHORTCUT:
			label="Note Shortcut Node";
		    break;

		case ICoreConstants.MAP_SHORTCUT:
			label="Map Shortcut Node";
		    break;

		case ICoreConstants.LIST_SHORTCUT:
			label="List Shortcut Node";
		    break;

		case ICoreConstants.PRO_SHORTCUT:
			label="Pro Shortcut Node";
		    break;

		case ICoreConstants.CON_SHORTCUT:
			label="Con Shortcut Node";
		    break;

		case ICoreConstants.TRASHBIN:
			label="Trashbin Node";
		    break;
	    }		
		return label;
	}
		
/************************************************************************************************************************************/
	
	
	/**
	 * Create the HTML text for the beginning tags for the given level and node.
	 * This method is called at the beginning of each new View.
	 *
	 * @param node the node to create the HTML for.
	 * @param level the indent level we are currently at.
	 * @return String the HTML for the begining tags for this level and node.
	 */
	private String getBeginTags(NodeSummary node, int level) throws IOException {

		String tags = "";

		if (level == 0) {
			sCurrentViewID = node.getId();

			while (previousLevel > 0) {
				tags += "\r\n";
				previousLevel--;
			}

			if (bDisplayInDifferentPages) {

				rootFile.append(tags);

				tags = "";
				String newFileName = null;

				if (!firstTime) {
					writeEndTags();

					if (directory.equals("")) {
						newFileName = this.createFileName((View)node);
					}
					else {
						//NOTE: we need to remove the directory from the top link
						//in each view when the export uses a different file for each view
						//commenting this directory + File.separator below does not do this
						//When this is removed, all other files except for the first view
						//are not produced... ?  7-3-01 RD
						newFileName = this.createFileName((View)node);
						newFileName = directory + File.separator + newFileName;
					}
					sCurrentFileName = newFileName;
				}

				//so if it is the "firstTime" use the new file's title provided by the user
				//o/w, use the name of the node itself (ie C:\Windows\Desktop\bug_test)
				//and then start writing to the file (the source of the file from link)
				if (firstTime) {
					title = originalTitle;
				}
				else {
					title = node.getLabel();
					writeBeginTags();
					anchorCount = 1;
				}
			}
			else { // (!bDisplayInDifferentPages)

				if (bIncludeNodeAnchors) {
					tags += "<a name='nid"+node.getId()+"_"+sCurrentViewID+"'></a>\r\n";
				}

				if (!firstTime) {
					rootFile.append("<span class=\"top\"><a href=\"#top\">Top</a></span>\r\n");
					rootFile.append("<div class=\"unit-divider\"></div>\r\n\r\n");
				}
			}
			firstTime = false;
		}
		else { // (level != 0)
			if (bIncludeNodeAnchors) {
				tags += "\r\n<a name='nid"+node.getId()+"_"+sCurrentViewID+"'></a>\r\n";
			} else {
				tags += "\r\n";
			}
		}

		tags += "<div class=\"level"+level+"\">\r\n";

		switch(level) {
			case 0: {
				tags += "<h1>\r\n";	
				break;
			}
			case 1: {
				tags += "<h2>\r\n";	
				break;
			}
			case 2: {
				tags += "<h3>\r\n";		
				break;
			}
			case 3: {
				tags += "<h4>\r\n";	
				break;
			}
			case 4: {
				tags += "<h5>\r\n";	
				break;
			}
			case 5: {
				tags += "<h6>\r\n";
				break;
			}
			case 6:	{
				tags += "<h6>\r\n";	
				break;
			}
			default: {
				tags += "<h6>\r\n";		
			}
		}			

		previousLevel = level;
		return tags;
	}

	/**
	 * Create the HTML text for the inner end tags for the given level and node.
	 *
	 * @param NodeSummary node, the node to create the HTML for.
	 * @param int level, the indent level we are currently at.
	 * @return String, the HTML for the inner end tags for this level and node.
	 */
	private String getInnerEndTags(NodeSummary node, int level) {

		String tags = "";

		if (level == 0) {
			tags += "</a>\r\n";
		}
		else {
			//add a link if available in the list with level 0
			for (int i = 0; i < nodeList.size(); i++) {
				if (node.getId().equals(((NodeSummary)nodeList.elementAt(i)).getId())) {
					tags += "</a>\r\n";
					break;
				}
			}
		}

		if (bIncludeNodeAnchors) {
			if (bUseAnchorNumbers) {
				tags += "<sup alt=\"url anchor\"><a href=\"#nid"+node.getId()+"_"+sCurrentViewID+"\">"+anchorCount+"</a></sup>\r\n";
			}
			else {
				tags += "<a href='#nid"+node.getId()+"_"+sCurrentViewID+"'>&nbsp;<img alt='url anchor' border='0' src='"+sAnchorImage+">'</a>\r\n";
			}
			anchorCount++;
		} else {
			tags += "\r\n";
		}
		
		switch(level) {
			case 0: {
				tags = "</h1>\r\n";		
				break;
			}
			case 1: {
				tags = "</h2>\r\n";
				break;
			}
			case 2: {
				tags = "</h3>\r\n";
				break;
			}
			case 3: {
				tags = "</h4>\r\n";
				break;
			}
			case 4: {
				tags = "</h5>\r\n";
				break;
			}
			case 5: {
				tags = "</h6>\r\n";
				break;
			}
			case 6:	{
				tags = "</h6>\r\n";
				break;
			}
			default: {
				tags = "</h6>\r\n";
			}
		}
		
		tags += "</div>\r\n";

		return tags;
	}

	/**
	 * Create the table that acts as an index of the exported views.
	 *
	 * @return String, the HTML for the inde table of views.
	 */
	private String getTableIndex() {

		if (tableIndex != null) {
			return tableIndex;
		}

		StringBuffer sb = new StringBuffer();
		Vector viewList = new Vector();
		NodeSummary node = null;
		for (int j = 0; j < nodeList.size(); j++) {
			node = (NodeSummary)nodeList.elementAt(j);
			if (node instanceof View) {
				if (viewList.indexOf(node.getId()) != -1) {
					continue;
				}

				viewList.addElement(node.getId());

				//add a link if available in the list with level 0
				for (int i = 0; i < nodeList.size(); i++) {
					if (node.getId().equals(((NodeSummary)nodeList.elementAt(i)).getId()) &&
							(((Integer)nodeLevelList.elementAt(i)).intValue() == 0)) {
						
						if (j==0) {
							sMainView = node.getId();
						}
						String fileNameForView = this.createFileName((View)node);
						if (bDisplayInDifferentPages) {
							sb.append("<a href=\"" +
									fileNameForView+"\">");
						}
						else {
							sb.append("<a href=\"#" +
									node.getLabel().replace(' ','_') + ":" + node.getId() + "\">");
						}

						String label = node.getLabel();
						if(label == null) {
							label = "";
						}

						sb.append(label);
						sb.append("</a>\r\n");
						break;
					}
				}
			}
		}
		tableIndex = sb.toString();

		return tableIndex;
	}
	
	/**
	 * Create the HTML text for the parent views associated with the given node.
	 *
	 * @param node the node whose parent views to create the HTML for.
	 * @param level the indent level we are currently at.
	 * @return boolean true if the HTML was created successfully, else false.
	 */
	private boolean createLinksFile(NodeSummary node, int level) throws IOException {

		Vector nodeAvailableInViews = new Vector();
		Vector nodes = null;
		int count = viewList.size();
		for (int i = 0; i < count; i++) {
			nodes = (Vector)nodeListInViewList.elementAt(i);

			for (int j = 0; j < nodes.size(); j++) {
				if (((NodeSummary)nodes.elementAt(j)).getId().equals(node.getId())) {
					nodeAvailableInViews.addElement(viewList.elementAt(i));
					{break;}
				}
			}
		}

		if (nodeAvailableInViews.size() > 1) {

			StringBuffer data = new StringBuffer(1000);
			String newFileName = null;

			if (!inlineView) {
				if (directory.equals("")) {
					newFileName = node.getId() + ".html";
				}
				else {
					newFileName = directory + File.separator + node.getId() + ".html";
				}

				data.append("<html><head>");
				data.append("<title>");
				data.append(node.getLabel());
				data.append("</title></head>\r\n");
				data.append("<body>\r\n");
				if (level <= 6) {
					data.append("<div class=\"views"+level+"\">");
				} else {
					data.append("<div class=\"views6\">");					
				}
				data.append("</div>");
				data.append("<script> function view(url) { parent.window.opener.location = url; } </script>");
			}
			else {
				if (level <= 6) {
					data.append("<div class=\"views"+level+"\">");
				} else {
					data.append("<div class=\"views6\">");					
				}				
				data.append("<b>Views:</b>&nbsp;");
			}

			if (bDisplayInDifferentPages) {
				View view = null;
				int countNodes = nodeAvailableInViews.size();
				
				for (int i = 0; i < countNodes; i++) {
					view = (View)nodeAvailableInViews.elementAt(i);
					String fileName = this.createFileName(view);										
					//on regular export, does not enter this "if"
					if (!inlineView) {
						if (bIncludeNodeAnchors) {
							data.append("<a href=\"" + "javascript:view('"+
									fileName + "#nid"+node.getId()+"_"+view.getId()+"');"+"\">");
						}
						else {
							data.append("<a href=\"" + "javascript:view('" +
									fileName+"');\">");
						}

						data.append(((View)nodeAvailableInViews.elementAt(i)).getLabel());
						data.append("</a>");
					}
					else {
						if (bIncludeNodeAnchors) {
							data.append("<a href=\""+fileName+"#nid"+node.getId()+"_"+view.getId()+"\">");
						}
						else {
							data.append("<a href=\""+fileName+"\">");
						}
						data.append(((View)nodeAvailableInViews.elementAt(i)).getLabel());
						data.append("</a>");
						
						if (i < countNodes-1) {
							data.append(", ");
						}
					}
				}
			}
			else { // !bDisplayInDifferentPages

				String fileNameWithoutDirectory = null;
				int slashIndex = fileName.lastIndexOf('/');

				if (slashIndex != -1) {
					//if it is not a directory location -> -1, adding 1 to it gets to after the '/'
					//and the substring is then the file name only.
					fileNameWithoutDirectory = fileName.substring(slashIndex+1);
				}
				else { //it's just the file name anyway
					fileNameWithoutDirectory = fileName;
				}
				View view = null;
				int countNodes = nodeAvailableInViews.size();
				for (int i = 0; i < countNodes; i++) {
					view = (View)nodeAvailableInViews.elementAt(i);
					String label = view.getLabel(); //.replace(' ','_');
					if (!inlineView) {
						data.append("<a href=\"");
						data.append("javascript:view('");
						data.append(fileNameWithoutDirectory + "#nid"+node.getId()+"_"+view.getId()+"');\">");
						data.append(label);
						data.append("</a>\r\n");
					}
					else {
						data.append("<a href=\"");
						data.append(fileNameWithoutDirectory + "#nid"+node.getId()+"_"+view.getId()+"\">");
						data.append(label);
						data.append("</a>\r\n");
						
						if (i < countNodes-1) {
							data.append(", ");
						}
					}
				}
			}

			if (!inlineView) {
				data.append("</html>");

				if (!bZipUp) {
					FileWriter fw = new FileWriter(newFileName);
					fw.write(data.toString());
					fw.close();
				}
				else {
					File file = new File(newFileName);
					htCreatedFiles.put(file.getName(), data.toString());
				}
			}
			else {
				data.append("</div>\r\n");
				rootFile.append(data.toString());
			}

			return true;
		}
		return false;
	}


	/**
	 * Create the HTML text for the codes associated with the given node.
	 *
	 * @param node the node whose codes to create the HTML for.
	 * @param level the indent level we are currently at.
	 * @return boolean true if the HTML was created successfully, else false.
	 */
	private boolean createCodesFile(NodeSummary node, int level) {

		boolean codeExists = false;

		String codeList = "";
		if (level <=6) {
			codeList = "<div class=\"codes"+level+"\">";
		} else {
			codeList = "<div class=\"codes6\">";			
		}
		codeList += "<b>Tags: </b>&nbsp;";

		if (!inlineView) {
			codeList += "<br>";
		}

		try {
			Enumeration codes = node.getCodes();
			StringBuffer data = new StringBuffer(1000);
			String newFileName = null;

			if (codes.hasMoreElements()) {
				codeExists = true;

				if (!inlineView) {
					if (directory.equals("")) {
						newFileName = node.getId() + "_tags.html";
					} else {
						newFileName = directory + File.separator +
									  node.getId() + "_tags.html";
					}

					data.append("<html><body>\r\n");
					data.append("<title>tags for " + node.getLabel() +"</title>\r\n");
				}

				while(codes.hasMoreElements()) {
					Code code = (Code)codes.nextElement();
					codeList += code.getName();
					if (inlineView && codes.hasMoreElements()) {
						codeList += ", ";
					} else {
						codeList += "<br>";
					}
				}

				codeList += "</div>\r\n";
				data.append(codeList);

				if (!inlineView) {
					data.append("</html>");

					if (!bZipUp) {
						FileWriter fw = new FileWriter(newFileName);
						fw.write(data.toString());
						fw.close();
					}
					else {
						File file = new File(newFileName);
						htCreatedFiles.put(file.getName(), data.toString());
					}
				}
				else {
					rootFile.append(data.toString());
				}
			}
		}
		catch(Exception ex) {
			String sMessage = new String("Error: (HTMLOutline.createCodesFile) \n\n"+ex.getMessage());
			if (!vtMessages.contains(sMessage)) {
				vtMessages.addElement(sMessage);
			}
		}

		return codeExists;
	}

	/**
	 * Write out the inital HTML tags for the current HTML file being created.
	 * This includes dynamically creating the style entries as per the users requirements.
	 */
	private void writeBeginTags() {

		rootFile.append("<html>\r\n");
		rootFile.append("<head>\r\n");

		//ADD STYLES
		rootFile.append("<style>\r\n");

		if (oFormatProperties != null && !oFormatProperties.isEmpty()) {
			int j=-1;
			String type = "";
			String currentIndent = "";
			currentIndent = oFormatProperties.getProperty( type+j+"indent" );
			String style = "";
	
			for (int i=0; i<UIHTMLFormatDialog.ROW_COUNT; i++) {
	
				// SET TYPE
				switch (i) {
					case 0: case 7: case 14: case 21: case 28: case 35: case 42: //LEVEL
						type = "level"; j++;
					break;
					case 1: case 8: case 15: case 22: case 29: case 36: case 43: // DETAIL					
						type = "detail";					
					break;
					case 2: case 9: case 16: case 23: case 30: case 37: case 44: // DETAIL DATE
						type = "detaildate";					
					break;
					case 3: case 10: case 17: case 24: case 31: case 38: case 45: // REFERENCE
						type = "reference";					
					break;
					case 4: case 11: case 18: case 25: case 32: case 39: case 46: // AUTHOR
						type = "author";
					break;
					case 5: case 12: case 19: case 26: case 33: case 40: case 47: // CODES
						type = "codes";
					break;
					case 6: case 13: case 20: case 27: case 34: case 41: case 48: // VIEWS
						type = "views";
					break;
				}
						
				// FOREGROUND COLOUR
				try {
					String color = oFormatProperties.getProperty( type+j+"color" );
					Color backgroundColor = new Color((new Integer(color).intValue())); 
					rootFile.append("\t."+type+j+" { color: rgb(");
					rootFile.append(backgroundColor.getRed()+","+backgroundColor.getGreen()+","+backgroundColor.getBlue()+");");
				} catch(Exception e) {
					rootFile.append("\t."+type+j+" { color: ");
					rootFile.append(oFormatProperties.getProperty( type+j+"color" )+";");
				}				
				
				// BACKGROUND COLOUR
				try {
					String color = oFormatProperties.getProperty( type+j+"back" );
					Color backgroundColor = new Color((new Integer(color).intValue())); 
					rootFile.append(" background: rgb(");
					rootFile.append(backgroundColor.getRed()+","+backgroundColor.getGreen()+","+backgroundColor.getBlue()+");");
				} catch(Exception e) {
					rootFile.append(" background: ");
					rootFile.append(oFormatProperties.getProperty( type+j+"back" ) +";");
				}
				
				// FONT FAMILY/SIZE AND MARGINS
				rootFile.append(" margin: ");
				rootFile.append(oFormatProperties.getProperty( type+j+"top" ));
				rootFile.append("in 0in 0in ");	
				currentIndent = oFormatProperties.getProperty( type+j+"indent" );
				rootFile.append(currentIndent);
				rootFile.append("in; font-family: \"");
				rootFile.append(oFormatProperties.getProperty( type+j+"font" ));
				rootFile.append("\"; font-size: ");
				rootFile.append(oFormatProperties.getProperty( type+j+"size" ));
						
				if (j==0) {
					rootFile.append("pt; text-align: left; text-decoration: none;");												
				} else {											
					rootFile.append("pt; vertical-align: top; text-align: left; text-decoration: none;");
				}
				
				// FONT STYLE
				style = oFormatProperties.getProperty( type+j+"style" );								
				if (style.equals("bold") || style.equals("bold-italic")) {
					rootFile.append(" font-weight: bold;");
				} else {
					rootFile.append(" font-weight: normal;");
				}				
				if (style.equals("italic") || style.equals("bold-italic")) {
					rootFile.append(" font-style: italic;");
				} else {
					rootFile.append(" font-style: normal;");
				}
					
				// LAST BITS
				switch (i) {
					case 0: case 7: case 14: case 21: case 28: case 35: case 42: //LEVEL
						rootFile.append(" padding: 3px;}\r\n");
						rootFile.append("\t.level"+j+" a { color: #000000; text-decoration: none;}\r\n");				
						rootFile.append("\t.level"+j+" a:hover {text-decoration: underline;}\r\n");
						rootFile.append("\t.level"+j+" img { padding: 3px 0px 3px 0px; margin-right: 10px; vertical-align: text-bottom;}\r\n");								
					break;
					case 1: case 8: case 15: case 22: case 29: case 36: case 43: // DETAIL
						rootFile.append(" padding: 3px; display: block;");
						rootFile.append("}\r\n");					
						rootFile.append("\t.detail"+j+" a {color: #000000; text-decoration: none;}\r\n");
						rootFile.append("\t.detail"+j+" a:hover { color: #000000; text-decoration: underline;}\r\n");
					break;
					case 2: case 9: case 16: case 23: case 30: case 37: case 44: // DETAIL DATE
						rootFile.append(" padding: 3px; display: block;");
						rootFile.append("}\r\n");					
					break;
					case 3: case 10: case 17: case 24: case 31: case 38: case 45: // REFERENCE
						rootFile.append(" padding: 3px; display: block;");
						rootFile.append("}\r\n");				
						rootFile.append("\t.reference"+j+" a {color: #000; text-decoration: none;}\r\n");
						rootFile.append("\t.reference"+j+" a:hover {text-decoration: underline;}\r\n");
					break;
					case 4: case 11: case 18: case 25: case 32: case 39: case 46: // AUTHOR
						rootFile.append(" padding: 3px; display: block;");
						rootFile.append("}\r\n");					
					break;
					case 5: case 12: case 19: case 26: case 33: case 40: case 47: // CODES
						rootFile.append(" padding: 3px; vertical-align: middle;");
						rootFile.append("}\r\n");					
					break;
					case 6: case 13: case 20: case 27: case 34: case 41: case 48: // VIEWS
						rootFile.append(" padding: 3px; vertical-align: middle;");
						rootFile.append("}\r\n");										
						rootFile.append("\t.views"+j+" a {color: #000; text-decoration: none;}\r\n");
						rootFile.append("\t.views"+j+" a:hover {text-decoration: underline;}\r\n");
					break;
				}						
			}		
		}

		rootFile.append("\r\n\tbody { margin: 15px; width: 810px;}\r\n");
		rootFile.append("\ta {text-decoration: none;}\r\n");
		
		// MENU BACKGROUND COLOUR
		rootFile.append("\t.left-col {width: 200px; background-color:");
		try {
			String color = oFormatProperties.getProperty( "menubackcolor" );
			Color oColor = new Color((new Integer(color).intValue())); 
			rootFile.append(" rgb(");
			rootFile.append(oColor.getRed()+","+oColor.getGreen()+","+oColor.getBlue()+");");
		} catch(Exception e) {
			rootFile.append(" white;");
		}		
		rootFile.append(" float: left; border: 1px solid");
		
		//MENU BORDER COLOUR
		try {
			String color = oFormatProperties.getProperty( "menubordercolor" );
			Color oColor = new Color((new Integer(color).intValue())); 
			rootFile.append(" rgb(");
			rootFile.append(oColor.getRed()+","+oColor.getGreen()+","+oColor.getBlue()+");");
		} catch(Exception e) {
			rootFile.append(" white;");
		}		
		rootFile.append("}\r\n");
		
		rootFile.append("\t.left-col-content {padding: 5px; ");
		rootFile.append("font-family: \"");
		rootFile.append(oFormatProperties.getProperty( "menufontfamily" ));
		rootFile.append("\"; font-size: ");
		rootFile.append(oFormatProperties.getProperty( "menufontsize" ));
		rootFile.append("pt; color: ");
		try {
			String color = oFormatProperties.getProperty( "menutextcolor" );
			Color oColor = new Color((new Integer(color).intValue())); 
			rootFile.append(" rgb(");
			rootFile.append(oColor.getRed()+","+oColor.getGreen()+","+oColor.getBlue()+");");
		} catch(Exception e) {
			rootFile.append(" white;");
		}				
		String style = oFormatProperties.getProperty( "menufontstyle" );								
		if (style.equals("bold") || style.equals("bold-italic")) {
			rootFile.append(" font-weight: bold;");
		} else {
			rootFile.append(" font-weight: normal;");
		}				
		if (style.equals("italic") || style.equals("bold-italic")) {
			rootFile.append(" font-style: italic;");
		} else {
			rootFile.append(" font-style: normal;");
		}
		rootFile.append("}\r\n");
		
		rootFile.append("\t.left-col a {display: block; padding-bottom: 10px; text-decoration: none;  color: #000;}\r\n");
		rootFile.append("\t.left-col a:hover {text-decoration: underline; color: #000;}\r\n");
		rootFile.append("\t.left-col a:visited {text-decoration: underline; color: #352e85; text-decoration: none;}\r\n");

		rootFile.append("\t.right-col {width: 570px; background-color: #fff; float: left; margin-left:15px;}\r\n");
		rootFile.append("\t.right-col-content h1, h2, h3, h4, h5, h6 {padding-top: 0px; margin-top:0px; margin-bottom: 0px; vertical-align: top;}\r\n");
		rootFile.append("\t.ref a	{font-family: \"Verdana\"; font-size: 7pt; color: #352e85; font-weight: normal; text-decoration: none; vertical-align:super; line-height:1px; display: inline;}\r\n");
		rootFile.append("\t.ref a:hover {text-decoration: underline; color: #000;}\r\n");
		rootFile.append("\tsup a {font-family: \"Verdana\"; font-size: 7pt; color: #352e85; font-weight: normal; text-decoration: none; line-height:-1em; display: inline;}\r\n");
		rootFile.append("\tsup a:hover {text-decoration: underline; color: #000;}\r\n");

		rootFile.append("\t.top a {color: #000; background: #fff; display:block; font-family: \"Verdana\"; font-size: 8pt; vertical-align: middle; text-align: left; font-weight: normal; font-style: normal;}\r\n");
		rootFile.append("\t.top a:hover {text-decoration: underline;}\r\n");		
				
		// VIEW DIVIDER BAR
		rootFile.append("\t.unit-divider {width: 570px; height: 5px; border-top: 2px dotted");
		try {
			String divider = oFormatProperties.getProperty( "dividercolor" );
			Color DividerColor = new Color((new Integer(divider).intValue())); 
			rootFile.append(" rgb(");
			rootFile.append(DividerColor.getRed()+","+DividerColor.getGreen()+","+DividerColor.getBlue()+");");
		} catch(Exception e) {
			rootFile.append(" white;");
		}
		rootFile.append(" margin: 10px 0px 4px 0px;}\r\n");
		
		rootFile.append("</style>\r\n");

		rootFile.append("<title>");

		if (currentViewPresent) {
			rootFile.append(originalTitle);
		}
		else {
			rootFile.append(title);
		}

		rootFile.append("</title>\r\n");
		rootFile.append("</head>\r\n\r\n");

		rootFile.append("<body>\r\n");

		if (newView == true) {
			rootFile.append("<script> function opennewwindow(url, name, features) { popBox = window.open(url,name,features); popBox.focus(); } </script>\r\n");
		}

		if (includeNavigationBar) {
			rootFile.append("<div class=\"left-col left-col-content\">\r\n");
			rootFile.append(getTableIndex());
			rootFile.append("</div>\r\n\r\n");
		}
		else if (bDisplayInDifferentPages) {
			getTableIndex();
		}
		
		rootFile.append("<div class=\"right-col right-col-content\">\r\n");
		rootFile.append("<a Name=\"top\"></a>\r\n");
	}

	/**
	 * Write out the final closing HTML tags for the current HTML file being created.
	 *
	 * @exception java.io.IOException, if we are not zipping but trying to write the file out, and it fails.
	 */
	private void writeEndTags() throws IOException {

		rootFile.append("<span class=\"top\"><a href=\"#top\">Top</a></span>\r\n");
		if (includeNavigationBar) {
			rootFile.append("</div>\r\n");
		}
		rootFile.append("</body>\r\n");
		rootFile.append("</html>");

		if (bZipUp) {
			File file = new File(sCurrentFileName);
			htCreatedFiles.put(file.getName(), rootFile.toString());
			rootFile = new StringBuffer(1000);
		}
		else {
			fileWriter = new FileWriter(sCurrentFileName);
			fileWriter.write(rootFile.toString());
			fileWriter.close();
			rootFile = new StringBuffer(1000);
		}
	}

   /**
	*	Process the export data and create the HTML output.
	*/
	public void print() {
		
		// DETEMINE THE MAIN PATH FOR EXPORTING TO
		String pathForHTMLFile = null;

		int indexOfSlashInHTMLPath = fileName.lastIndexOf("/");
		int indexOfSlashInFile = fileName.lastIndexOf(File.separator);

		if (indexOfSlashInHTMLPath != -1) { //multiple pages
			pathForHTMLFile = fileName.substring(0, indexOfSlashInHTMLPath + 1);
		}
		else if(indexOfSlashInFile != -1) {
			pathForHTMLFile = fileName.substring(0, indexOfSlashInFile + 1);
		}
		else {
			pathForHTMLFile = "";
		}
		
		// if adding node anchors, copy selected anchor image into images dir of export
		if (bIncludeNodeAnchors && !bUseAnchorNumbers) {

			File anchorFile = new File(sAnchorImage);
			String anchorFileName = anchorFile.getName();

			// ONLY COPY FILE TO IMAGE DIR IF NOT ZIPPING UP EXPORT
			if (bZipUp) {
				htExportFiles.put(sAnchorImage, "images/"+anchorFileName);
			}
			else {
				File directory = new File(pathForHTMLFile + "images");
				if (!directory.isDirectory()) {
					directory.mkdirs();
				}

				try {
					FileInputStream fis = new FileInputStream(sAnchorImage);
					FileOutputStream fos = new FileOutputStream(pathForHTMLFile+"images"+ProjectCompendium.sFS+anchorFileName);

					byte[] data = new byte[fis.available()];
					fis.read(data);
					fos.write(data);
				}
				catch (Exception e) {
					String sMessage = new String("Unable to copy anchor image: " + e.getMessage());
					if (!vtMessages.contains(sMessage)) {
						vtMessages.addElement(sMessage);
					}
					//ProjectCompendium.APP.displayError("Unable to copy anchor image: " + e.getMessage());
				}
			}

			// FOR THE PURPOSES OF THE HTML FILE
			sAnchorImage = "images/"+anchorFileName;
		}

		tableIndex = null;
		try {

			writeBeginTags();
			NodeSummary node = null;
			String sNodeID = "";
			for (int i = 0; i < nodeList.size(); i++) {
				
				node = (NodeSummary) nodeList.elementAt(i);
				IModel model = ProjectCompendium.APP.getModel();
				node.initialize(model.getSession(), model);
				sNodeID = node.getId();

				int nodeIndex = ((Integer)nodeIndexList.elementAt(i)).intValue();
				int level = ((Integer)nodeLevelList.elementAt(i)).intValue();

				sNodeLabel = node.getLabel();
				if(sNodeLabel == null)
					sNodeLabel = "";

				sNodeAuthor = "(" + node.getAuthor() + ")";

				// get the size of the label
				int nLabelLength = sNodeLabel.length();

				// get the size of the author string
				int nAuthorLength = sNodeAuthor.length();

				// Subtract the level at which the views are exported
				// This information is used to reset the export level in HTML Document
				// so when a user exports a view/node at a different level the HTML Style
				// begins from Heading 1 rather than the level the node is found.

				if(nStartExportAtLevel > 1 && !bStartExportAtLevel) {
					nStartExportAtLevel = nStartExportAtLevel - 1;
					bStartExportAtLevel = true;
				}

				if(level > 6) {
					nLevel = 6 - nStartExportAtLevel;
					level=6; 
				}
				else {
					nLevel = level - nStartExportAtLevel;
				}

				String beginTags = getBeginTags(node, level);
				rootFile.append(beginTags);

				if (nodeIndex != -1) {
					rootFile.append((nodeIndex+1) + ".");
				}

				String image = node.getImage();
				String source = node.getSource();
				int nodeType = node.getType();

				boolean hasExternalFile = false;

				boolean bViewNav = false;
				String sViewNav = "";
				if (node instanceof View && level!=0) {
					//add a link if available in the list with level 0
					String 	newFileName = this.createFileName((View)node);
					for (int k = 0; k < nodeList.size(); k++) {
						if (sNodeID.equals(((NodeSummary)nodeList.elementAt(k)).getId()) &&
							(((Integer)nodeLevelList.elementAt(k)).intValue() == 0)) {
							bViewNav = true;
							if (bDisplayInDifferentPages) {
								sViewNav = "<a href=\"" + newFileName + "\">\r\n";
							}
							else { //o/w just use the label of the node + ':' + id (number) of node
								sViewNav="<a href=\"#"+node.getLabel().replace(' ','_') + ":" + node.getId() + "\">\r\n";
							}
							break;
						}
					}
				}

				if (bIncludeImage || bIncludeReferences) {
					boolean isReference = false;
					boolean hasExternalImage = false;

					int imageWidth = 25;
					int imageHeight = 25;

					String path = "";
					if (htNodePositions.containsKey(node.getId())) {
						NodePosition npos = (NodePosition)htNodePositions.get(node.getId());
						path = UIImages.getPath(nodeType, npos.getShowSmallIcon());
					} else {
						path = UIImages.getPath(nodeType, false);
					}
					String newPath = null;

					//check to see if node is a Reference Node
					if (nodeType == ICoreConstants.REFERENCE || nodeType == ICoreConstants.REFERENCE_SHORTCUT) {
						if (image != null && !image.equals("")) {
							if (image.startsWith("www.")) {
								image = "http://"+image;
							}																																
							path = image;
							isReference = true;
							hasExternalImage = true;

							if(source != null && !source.equals("")) {
								hasExternalFile = true;
							}
						}
						else if(source != null && source.equals("")) { //if no ref, leave ref icon (path)
							path = path;
						}
						else {
							if (source != null) {
								if (source.startsWith("www.")) {
									source = "http://"+source;
								}																
								if ( UIImages.isImage(source) ) {
									isReference = true;
									hasExternalImage = true;
									hasExternalFile = true;
									path = source;
								}
								else {
									if (CoreUtilities.isFile(source)) {
										hasExternalFile = true;
										path = UIImages.getReferencePath(source, path, false);
									}
									else
										path = UIImages.getReferencePath(source, path, false);
								}
							}
						}
					}
					else if(nodeType == ICoreConstants.MAPVIEW || nodeType == ICoreConstants.MAP_SHORTCUT ||
							nodeType == ICoreConstants.LISTVIEW || nodeType == ICoreConstants.LIST_SHORTCUT) {

						image = node.getImage();
						if (image != null && !image.equals("")) {
							if (image.startsWith("www.")) {
								image = "http://"+image;
							}																								
							path = image;
							isReference = true;
							hasExternalImage = true;
						}
					}
		
					// ADD IMAGE
					if (path != null && bIncludeImage) {

						// GET THE SCALED WIDTH AND HEIGHT OR ACTUAL SIZE IF SMALLER
						Dimension refDim = UIImages.thumbnailImage(path, imageWidth, imageHeight);
						imageWidth = refDim.width;
						imageHeight = refDim.height;

						String htmlPath = path;
						File imageFile = new File(path);
						String imageName = "";
						if (imageFile.exists()) {
							imageName = imageFile.getName();
							newPath = pathForHTMLFile + "images" + File.separator + imageName;

							// ONLY COPY FILE TO EXPORT DIR IF NOT ZIPPING UP EXPORT
							if (bZipUp) {
								File newFile = new File(newPath);
								htExportFiles.put(path, "images/"+newFile.getName());
							}
							else {
								File newImageFile = new File(newPath);
								if (!newImageFile.exists()) {
									//then create a directory instance, and see if the directory exists.
									File directory = new File(pathForHTMLFile + "images");
									if (!directory.isDirectory()) {
										directory.mkdirs();
									}
									try {
										FileInputStream fis = new FileInputStream(path);
										FileOutputStream fos = new FileOutputStream(newPath);
										byte[] data = new byte[fis.available()];
										fis.read(data);
										fos.write(data);
									}
									catch (Exception e) {
										String sMessage = new String("Unable to create image: " + e.getMessage());
										if (!vtMessages.contains(sMessage)) {
											vtMessages.addElement(sMessage);
										}
										//ProjectCompendium.APP.displayError("Unable to create image:" + e.getMessage());
									}
								}
							}

							htmlPath = "images/"+imageName;
						}

						if (hasExternalImage) {
							rootFile.append("<a href=\""+htmlPath+"\" target=\"_blank\"><img alt=\""+getNodeTypeDescription(node.getType())+" Icon: "+imageName+"\" border=\"0\" src=\"" + htmlPath);
							rootFile.append("\" width=\""+imageWidth+"\" Height=\""+imageHeight+"\"></a>\r\n");
						}
						else
							if (bViewNav) {
								rootFile.append(sViewNav);
								rootFile.append("<img alt=\""+getNodeTypeDescription(node.getType())+": "+sNodeLabel+"\" border=\"0\" src=\"" + htmlPath + "\" width=\""+imageWidth+"\" Height=\""+imageHeight+"\">\r\n");
								rootFile.append("</a>\r\n");
							} else {
								rootFile.append("<img alt=\""+getNodeTypeDescription(node.getType())+": "+sNodeLabel+"\" border=\"0\" src=\"" + htmlPath + "\" width=\""+imageWidth+"\" Height=\""+imageHeight+"\">\r\n");
							}
					}
				}

				// NODE LABEL
				if (level == 0) {
					currentViewName = node.getLabel().replace(' ','_');
					rootFile.append("<a valign=\"bottom\" name=\""+currentViewName + ":" + node.getId() + "\"></a>\r\n");
				}
				else if (bViewNav) {
					rootFile.append("<a valign=\"bottom\" name=\""+currentViewName + ":" + node.getId() + "\"></a>\r\n");
					rootFile.append(sViewNav);
				}
				else {
					rootFile.append("<a name=\""+currentViewName + ":" + node.getId() + "\"></a>\r\n");
				}
				
				rootFile.append(sNodeLabel);
				rootFile.append(getInnerEndTags(node, level));

				// NODE AUTHOR
				if(bPrintNodeAuthor) {
					if(nAuthorLength > 1) {
						String authorstart = "";
						if (level <= 6) {
							authorstart = "<div class=\"author"+level+"\">";
						} else {
							authorstart = "<div class=\"author6\">";								
						}
						rootFile.append(authorstart+sNodeAuthor+"</div>\r\n");
					}
				}

				// NODE DETAIL
				if(!bPrintNodeDetail && !bPrintNodeDetailDate && level > 0) {
					//rootFile.append("<br>\r\n");
				}
				else if(bPrintNodeDetail || bPrintNodeDetailDate) {

					String sAuthor = ProjectCompendium.APP.getModel().getUserProfile().getUserName();
					Vector details = node.getDetailPages(sAuthor);

					int countDetails = details.size();

					for (int det=0; det<countDetails; det++) {

						NodeDetailPage page = (NodeDetailPage)details.elementAt(det);
						sNodeDetail = page.getText();
						if (sNodeDetail == null || (sNodeDetail != null && sNodeDetail.equals(ICoreConstants.NODETAIL_STRING) ))
							sNodeDetail = "";

						// get the size of the detail
						int nDetailLength = sNodeDetail.length();

						if(nDetailLength > 0) {

							Date creation = page.getCreationDate();
							Date modified = page.getModificationDate();
							long creationTime = new Long(creation.getTime()).longValue();

							long fromTime = 0;
							long toTime = 0;
							if (bPrintNodeDetailDate ) {
								fromTime = fromDate.getTime().getTime();
								toTime = toDate.getTime().getTime();
							}

							if (bPrintNodeDetail || (bPrintNodeDetailDate && (creationTime >= fromTime && creationTime <= toTime) ) ) {

								if (bDisplayDetailDates) {
									if (level <= 6) {
										rootFile.append("<div class=\"detaildate"+level+"\">\r\n");
									} else {
										rootFile.append("<div class=\"detaildate6\">\r\n");										
									}
									rootFile.append("<strong>Entered:</strong> "+sdf.format(creation).toString()+"&nbsp;&nbsp;<strong>Modified:</strong> "+sdf.format(modified).toString());
									rootFile.append("</div>\r\n");
								}

								if (bIncludeDetailAnchors) {
									rootFile.append("<a name='detail"+node.getId()+"'></a>\r\n");
								}

								//	Node Detail work here
								if (level <= 6) {
									rootFile.append("<div class=\"detail"+level+"\">\r\n");
								} else {
									rootFile.append("<div class=\"detail6\">\r\n");										
								}

								sNodeDetail = formatString(sNodeDetail);
								sNodeDetail = sNodeDetail.trim();
								
								rootFile.append(sNodeDetail);

								if (bIncludeDetailAnchors) {
									if (bUseAnchorNumbers)
										rootFile.append("<sup alt=\"url anchor\"><a href=\"#detail"+node.getId()+"\">"+anchorCount+"</a></sup>");
									else
										rootFile.append("<a href=\"#detail"+node.getId()+"\">&nbsp;<img alt=\"url anchor\" border=\"0\" src=\""+sAnchorImage+"\"></a>");
									anchorCount++;
								}

								rootFile.append("</div>\r\n");

								if (countDetails > 1)
									rootFile.append("<br>\r\n");
							}
						}
					}
				}
				
				// REFERENCE
				String refName = "";
				File refFile = new File(source);
				if (refFile.exists() && !refFile.isDirectory()) {
					refName = refFile.getName();
					if (hasExternalFile && bIncludeReferences && bZipUp) {
						htExportFiles.put(refFile.getAbsolutePath(), "references/"+refFile.getName());
						source = "references/"+refName;
					}
					else if (hasExternalFile && bIncludeReferences && !bZipUp) {
						File newFile = new File(pathForHTMLFile + "references" + File.separator + refName);
						source = "references/"+refName;
						if (!newFile.exists()) {
							File directory = new File(pathForHTMLFile + "references");
							if (!directory.isDirectory()) {
								directory.mkdirs();
							}
							try {
								FileInputStream fis = new FileInputStream(refFile.getAbsolutePath());
								FileOutputStream fos = new FileOutputStream(newFile.getAbsolutePath());
								byte[] data = new byte[fis.available()];
								fis.read(data);
								fos.write(data);
							}
							catch (Exception e) {
								String sMessage = new String("Unable to create reference:" + e.getMessage());
								if (!vtMessages.contains(sMessage)) {
									vtMessages.addElement(sMessage);
								}
								//ProjectCompendium.APP.displayError("Unable to create reference:" + e.getMessage());
							}
						}
					}
				}

				if ( (nodeType == ICoreConstants.REFERENCE || nodeType == ICoreConstants.REFERENCE_SHORTCUT)
					&& source != null && !source.equals("")) {

					String lowerCaseSource = source.toLowerCase();
					if (source.startsWith("www.") || source.startsWith("http:") || source.startsWith("https:")) {
						refName = source;
					}

					if (!refName.equals("")) {
						if (level <= 6) {
							rootFile.append("<div class=\"reference" + level + "\">\r\n");
						} else {
							rootFile.append("<div class=\"reference6\">\r\n");								
						}
						rootFile.append("<strong>Reference:</strong>&nbsp;");
						rootFile.append("<a href=\"" + source + "\">" + refName + "</a>\r\n");
						rootFile.append("</div>\r\n");
					}
				}

				// TAGS
				String tags = "";
				boolean codesPresent = false;
				if (bIncludeTags) {
					codesPresent = createCodesFile(node, level);
				}

				if ( (codesPresent) && (newView)) {
					if (level <= 6) {
						tags += "<div class=\"codes"+level+"\">\r\n";
					} else {
						tags += "<div class=\"codes6\">\r\n";							
					}
					tags += "<a href=\"";
					tags += "javascript:opennewwindow('";
					tags += node.getId() + "_tags.html" + "','" + node.getId() + "','width=200,height=300";
					tags += "');\">";
					tags += "tags";
					tags += "</a>\r\n";
					tags += "</div>\r\n";
				}
				rootFile.append(tags);					
				
				// VIEWS
				tags = "";
				boolean presentInMoreThanOneView = false;
				try {
					if (bIncludeViews) {
						presentInMoreThanOneView = createLinksFile(node, level);
					}
				}
				catch (IOException e) {
					String sMessage = new String("Exception: (HTMLOutline.print - presentInMoreThanOneView) " + e.getMessage());
					if (!vtMessages.contains(sMessage)) {
						vtMessages.addElement(sMessage);
					}
					//ProjectCompendium.APP.displayError("Exception: (HTMLOutline.print - presentInMoreThanOneView) " + e.getMessage());
				}

				if ((presentInMoreThanOneView) && (newView)) {
					if (level <= 6) {
						tags += "<div class=\"views"+level+"\">";
					} else {
						tags += "<div class=\"views6\">";							
					}
					tags += "<a href=\"";
					tags += "javascript:opennewwindow('";
					tags += node.getId() + ".html" + "','" + node.getId() +
							"','width=200,height=300";
					tags += "');\">";
					tags += "views";
					tags += "</a>\r\n";
					tags += "</div>\r\n";
				}

				rootFile.append(tags);					
			}

			writeEndTags();

			if (bZipUp) {
				zipUpExport(fileName);
			}
			
			// Display Messages
			if (vtMessages.size() > 0) {
				UITextArea txtLabel = new UITextArea(800, 800);
				int count = vtMessages.size();
				for (int i=0; i<count; i++) {
					txtLabel.append((String)vtMessages.elementAt(i));
					txtLabel.append("\n\n");					
				}
				//txtLabel.setAutoscrolls(true);
				txtLabel.setEditable(false);
				JScrollPane scrollpane = new JScrollPane(txtLabel);
				scrollpane.setPreferredSize(new Dimension(600,300));					
				JOptionPane.showMessageDialog(ProjectCompendium.APP,
						scrollpane,
                        "Export Problems Encountered",
                        JOptionPane.WARNING_MESSAGE);				
			}
		}
		catch(Exception ex)	{
			ex.printStackTrace();
			ProjectCompendium.APP.displayError("Exception: (HTMLOutline.print) " + ex.getMessage() );
		}
	}

	/**
	 * Zip up the export and its associated directories.
	 *
	 * @param sFileName the name of the zip file to export to.
	 */
	private void zipUpExport(String sFileName) {

		try {
			int BUFFER = 2048;
			BufferedInputStream origin = null;
			FileInputStream fi = null;

			File exportFile = new File(sFileName);
			FileOutputStream dest = new FileOutputStream(exportFile.getAbsolutePath());
			ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(dest));
			out.setMethod(ZipOutputStream.DEFLATED);
			byte data2[] = new byte[BUFFER];

			ZipEntry entry = null;
			int count = 0;

			// ADD CREATED FILES
			for (Enumeration e = htCreatedFiles.keys(); e.hasMoreElements() ;) {
				String sFilePath = (String)e.nextElement();
				String sData = (String)htCreatedFiles.get(sFilePath);

				try {
					entry = new ZipEntry(sFilePath);
					out.putNextEntry(entry);
					int len = sData.length();
					byte data3[] = sData.getBytes();
					out.write(data3, 0, len);
				}
				catch (Exception ex) {
					System.out.println("Unable to zip up html export: \n\n"+sFilePath+"\n\n"+ex.getMessage());
				}
			}

			// ADD RESOURCES
			count = 0;
			for (Enumeration e = htExportFiles.keys(); e.hasMoreElements() ;) {
				String sOldFilePath = (String)e.nextElement();
				String sNewFilePath = (String)htExportFiles.get(sOldFilePath);

				try {
					fi = new FileInputStream(sOldFilePath);
					origin = new BufferedInputStream(fi, BUFFER);

					entry = new ZipEntry(sNewFilePath);
					out.putNextEntry(entry);

					while((count = origin.read(data2, 0, BUFFER)) != -1) {
						out.write(data2, 0, count);
					}
					origin.close();
				}
				catch (Exception ex) {
					System.out.println("Unable to zip up html export: \n\n"+sOldFilePath+"\n\n"+ex.getMessage());
				}
			}

			out.close();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
}
