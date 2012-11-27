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

package com.compendium.io.html;

import java.awt.*;
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
	
	/** Indicates if this export should be optimized for Word by excluding the heading tags.*/
	private boolean			bOptimizeForWord			= false;

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
	private String			fileName 					= ""; //$NON-NLS-1$

	/** Store a node label while processing.*/
	private String			sNodeLabel 					= ""; //$NON-NLS-1$

	/** Store a node detail page while processing.*/
	private String			sNodeDetail 				= ""; //$NON-NLS-1$

	/** Store a node author while processing.*/
	private	String			sNodeAuthor 				= ""; //$NON-NLS-1$

	/** The default title to use.*/
	private String			title 						= "Compendium"; //$NON-NLS-1$

	/** The user specified title for the mail HTML page.*/
	private String			originalTitle 				= title;

	/** The navigation table HTML data.*/
	private String			tableIndex 					= null;

	/** The directory to export to.*/
	private String			directory 					= null;

	/** The name of the current View.*/
	private String			currentViewName 			= null;

	/** The name of the anchor image to use for anchors.*/
	private String			sAnchorImage 				= ""; //$NON-NLS-1$

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
	private String			sBackupName 				= ""; //$NON-NLS-1$

	/** The file name of the HTML file currently being created.*/
	private String 			sCurrentFileName 			= ""; //$NON-NLS-1$

	/** The platform specific file separator to use when creating new files.*/
	private String			sFS 						= System.getProperty("file.separator"); //$NON-NLS-1$

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
	private static SimpleDateFormat sdf 				= new SimpleDateFormat("d MMM, yyyy"); //$NON-NLS-1$

	/** The colour for the anchor numbers.*/
	private static String 	purple						= "#C8A8FF"; //(200, 168, 255); //$NON-NLS-1$

	/** hold increment when using numbers for the node anchors.*/
	private int				anchorCount					= 1;

	/** The id of the current view being processed.*/
	private String			sCurrentViewID 				= ""; //$NON-NLS-1$
	
	/** Holds messages about missing reference files.*/
	private Vector 			vtMessages					= new Vector();
	
	/** The id of the first, top level view (page) for the export.*/
	private String			sMainView					= ""; //$NON-NLS-1$
	
	private Properties		oFormatProperties			= null;
	
	/** The depth of the view being processed.*/
	private int				nDepth						= 0;

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

		sBackupName = ""; //$NON-NLS-1$
		String name = new File(fileName).getName();
		int ind = name.lastIndexOf("."); //$NON-NLS-1$
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
			directory = ""; //$NON-NLS-1$
		}

		if (bZipUp) {
			sCurrentFileName = sBackupName+"_Outline.html"; //$NON-NLS-1$
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
			String sName = ""; //$NON-NLS-1$
			if (styles.length > 0) {			
				for (int i=0; i<styles.length; i++) {
					file = styles[i];
					Properties styleProp = new Properties();
					styleProp.load(new FileInputStream(file));
					String value = styleProp.getProperty("name"); //$NON-NLS-1$
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
			ProjectCompendium.APP.displayError(LanguageProperties.getString(LanguageProperties.IO_BUNDLE, "HTMLOutline.erroLoadingStyle")); //$NON-NLS-1$
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
	
	public void setOptimizeForWord(boolean opt) {
		bOptimizeForWord = opt;
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

	private NodePosition getPosition(String sNodeID) {
		NodePosition oPos = null;
		try {
			Vector views = ProjectCompendium.APP.getModel().getNodeService().getViews(session, sNodeID);
			if (views.size() == 1) {
				View view  = (View)views.elementAt(0);		
				oPos = view.getNodePosition(sNodeID);
			} 			
		}
		catch(Exception ex) {}
		
		return oPos;
	}
	
	/**
	 * This function creates various necessary Vector Lists.
	 *
	 * @param oNode the node of the current view.
	 * @param level the indent starting level.
	 * @param index -1.
	 */
	public void runGenerator(NodeSummary oNode, int level, int index) {
		
		// Try and determine the NodePosition for the given view, 
		// if it has only one parent view or is on the currently open view
		// So that later we can try and draw the correct icon size.
		String sNodeID = oNode.getId();
		NodePosition oPos = getPosition(sNodeID);
		if (oPos == null) {
			UIViewFrame frame = ProjectCompendium.APP.getCurrentFrame();
			View view = frame.getView();
			oPos = view.getNodePosition(oNode.getId());
		}
		if (oPos != null) {
			htNodePositions.put(sNodeID, oPos);
		}
		
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
			ProjectCompendium.APP.displayError("Exception: (HTMLOutline.runGenerator) " + e.getMessage()); //$NON-NLS-1$
		}
	}

	/**
	 * Format the given String, by replacing all '\n' with the current HTML 'newline' character.
	 *
	 * @param oldString the String to format.
	 * @return String the formatted String.
	 */
	public String formatString(String oldString) {

		String newLine = "<br>"; //$NON-NLS-1$
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
		String sViewFileName = ""; //$NON-NLS-1$
		if (view.getId().equals(sMainView)) {
			if (bZipUp) {
				sViewFileName = sBackupName+"_Outline.html"; //$NON-NLS-1$
			} else {		
				sViewFileName = fileName;
			}
		} else {
			String sLabel = view.getLabel();
			if (sLabel.length()> 20) {
				sLabel = sLabel.substring(0, 20);
			}
			sViewFileName = CoreUtilities.cleanFileName(sLabel)+"_"+view.getId()+"_Outline.html"; //$NON-NLS-1$ //$NON-NLS-2$
		}
		return sViewFileName;
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
	private String getBeginTags(NodeSummary node, int level, int nType, int nDepth) throws IOException {

		String tags = ""; //$NON-NLS-1$

		if (level == 0) {
			sCurrentViewID = node.getId();

			while (previousLevel > 0) {
				tags += "\r\n"; //$NON-NLS-1$
				previousLevel--;
			}

			if (bDisplayInDifferentPages) {

				rootFile.append(tags);

				tags = ""; //$NON-NLS-1$
				String newFileName = null;

				if (!firstTime) {
					writeEndTags();

					if (directory.equals("")) { //$NON-NLS-1$
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
					tags += "<a name='nid"+node.getId()+"_"+sCurrentViewID+"'></a>\r\n"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				}

				if (!firstTime) {
					if (!bOptimizeForWord) {
						rootFile.append("<span class=\"top\"><a href=\"#top\">Top</a></span>\r\n"); //$NON-NLS-1$
					} else {
						rootFile.append("<br>\r\n");						 //$NON-NLS-1$
					}
					
					rootFile.append("<div class=\"unit-divider\"></div>\r\n\r\n"); //$NON-NLS-1$
				}
			}
			firstTime = false;
		}
		else { // (level != 0)
			if (bIncludeNodeAnchors) {
				tags += "\r\n<a name='nid"+node.getId()+"_"+sCurrentViewID+"'></a>\r\n"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			} else {
				tags += "\r\n"; //$NON-NLS-1$
			}
		}

		if (bOptimizeForWord) {
			tags += "<p class=\"level"+level+"\">\r\n"; //$NON-NLS-1$ //$NON-NLS-2$
		} else {
			tags += "<div class=\"level"+level+"\">\r\n"; //$NON-NLS-1$ //$NON-NLS-2$
		}

		if (!bOptimizeForWord) {
			switch(level) {
				case 0: {
					tags += "<h1>\r\n";	 //$NON-NLS-1$
					break;
				}
				case 1: {
					tags += "<h2>\r\n";	 //$NON-NLS-1$
					break;
				}
				case 2: {
					tags += "<h3>\r\n";		 //$NON-NLS-1$
					break;
				}
				case 3: {
					tags += "<h4>\r\n";	 //$NON-NLS-1$
					break;
				}
				case 4: {
					tags += "<h5>\r\n";	 //$NON-NLS-1$
					break;
				}
				case 5: {
					tags += "<h6>\r\n"; //$NON-NLS-1$
					break;
				}
				case 6:	{
					tags += "<h6>\r\n";	 //$NON-NLS-1$
					break;
				}
				default: {
					tags += "<h6>\r\n";		 //$NON-NLS-1$
				}
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
	private String getInnerEndTags(NodeSummary node, int level, int nType, int nDepth) {

		String tags = ""; //$NON-NLS-1$

		if (bIncludeNodeAnchors) {
			if (bUseAnchorNumbers) {
				tags += "<sup alt=\"url anchor\"><a href=\"#nid"+node.getId()+"_"+sCurrentViewID+"\">"+anchorCount+"</a></sup>\r\n"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			}
			else {
				tags += "<a href='#nid"+node.getId()+"_"+sCurrentViewID+"'>&nbsp;<img alt='url anchor' border='0' src='"+sAnchorImage+"'></a>\r\n"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			}
			anchorCount++;
		} else {
			tags += "\r\n"; //$NON-NLS-1$
		}
		
		if (!bOptimizeForWord) {
			switch(level) {
				case 0: {
					tags += "</h1>\r\n";		 //$NON-NLS-1$
					break;
				}
				case 1: {
					tags += "</h2>\r\n"; //$NON-NLS-1$
					break;
				}
				case 2: {
					tags += "</h3>\r\n"; //$NON-NLS-1$
					break;
				}
				case 3: {
					tags += "</h4>\r\n"; //$NON-NLS-1$
					break;
				}
				case 4: {
					tags += "</h5>\r\n"; //$NON-NLS-1$
					break;
				}
				case 5: {
					tags += "</h6>\r\n"; //$NON-NLS-1$
					break;
				}
				case 6:	{
					tags += "</h6>\r\n"; //$NON-NLS-1$
					break;
				}
				default: {
					tags += "</h6>\r\n"; //$NON-NLS-1$
				}
			}
		} 
		
		if (bOptimizeForWord) {
			tags += "</p>\r\n";			 //$NON-NLS-1$
		} else {
			tags += "</div>\r\n"; //$NON-NLS-1$
		}

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
							sb.append("<a href=\"" + //$NON-NLS-1$
									fileNameForView+"\">"); //$NON-NLS-1$
						}
						else {
							sb.append("<a href=\"#" + //$NON-NLS-1$
									node.getLabel().replace(' ','_') + ":" + node.getId() + "\">"); //$NON-NLS-1$ //$NON-NLS-2$
						}

						String label = node.getLabel();
						if(label == null) {
							label = ""; //$NON-NLS-1$
						}

						sb.append(label);
						sb.append("</a>\r\n"); //$NON-NLS-1$
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
				if (directory.equals("")) { //$NON-NLS-1$
					newFileName = node.getId() + ".html"; //$NON-NLS-1$
				}
				else {
					newFileName = directory + File.separator + node.getId() + ".html"; //$NON-NLS-1$
				}

				data.append("<html><head>\r\n"); //$NON-NLS-1$
				//data.append("<META http-equiv=\"content-type\" content=\"text/html; charset=UTF-16\">\r\n");
				data.append("<title>"); //$NON-NLS-1$
				data.append(node.getLabel());
				data.append("</title></head>\r\n"); //$NON-NLS-1$
				data.append("<body>\r\n"); //$NON-NLS-1$
				if (level < UIHTMLFormatDialog.LEVEL_COUNT) {
					if (bOptimizeForWord) {
						data.append("<p class=\"views"+level+"\">"); //$NON-NLS-1$ //$NON-NLS-2$
					} else {
						data.append("<div class=\"views"+level+"\">"); //$NON-NLS-1$ //$NON-NLS-2$
					}
				} else {
					if (bOptimizeForWord) {
						data.append("<p class=\"views"+(UIHTMLFormatDialog.LEVEL_COUNT-1)+"\">"); //$NON-NLS-1$
					} else {
						data.append("<div class=\"views"+(UIHTMLFormatDialog.LEVEL_COUNT-1)+"\">"); //$NON-NLS-1$
					}
				}
				
				if (bOptimizeForWord) {				
					data.append("</p>"); //$NON-NLS-1$
				} else {
					data.append("</div>");					 //$NON-NLS-1$
				}
				data.append("<script> function view(url) { parent.window.opener.location = url; } </script>"); //$NON-NLS-1$
			}
			else {
				if (level < UIHTMLFormatDialog.LEVEL_COUNT) {
					if (bOptimizeForWord) {	
						data.append("<p class=\"views"+level+"\">"); //$NON-NLS-1$ //$NON-NLS-2$
					} else {
						data.append("<div class=\"views"+level+"\">"); //$NON-NLS-1$ //$NON-NLS-2$
					}
				} else {
					if (bOptimizeForWord) {
						data.append("<p class=\"views"+(UIHTMLFormatDialog.LEVEL_COUNT-1)+"\">");						 //$NON-NLS-1$
					} else {
						data.append("<div class=\"views"+(UIHTMLFormatDialog.LEVEL_COUNT-1)+"\">"); //$NON-NLS-1$
					}
				}				
				data.append("<b>Views:</b>&nbsp;"); //$NON-NLS-1$
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
							data.append("<a href=\"" + "javascript:view('"+ //$NON-NLS-1$ //$NON-NLS-2$
									fileName + "#nid"+node.getId()+"_"+view.getId()+"');"+"\">"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
						}
						else {
							data.append("<a href=\"" + "javascript:view('" + //$NON-NLS-1$ //$NON-NLS-2$
									fileName+"');\">"); //$NON-NLS-1$
						}

						data.append(((View)nodeAvailableInViews.elementAt(i)).getLabel());
						data.append("</a>"); //$NON-NLS-1$
					}
					else {
						if (bIncludeNodeAnchors) {
							data.append("<a href=\""+fileName+"#nid"+node.getId()+"_"+view.getId()+"\">"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
						}
						else {
							data.append("<a href=\""+fileName+"\">"); //$NON-NLS-1$ //$NON-NLS-2$
						}
						data.append(((View)nodeAvailableInViews.elementAt(i)).getLabel());
						data.append("</a>"); //$NON-NLS-1$
						
						if (i < countNodes-1) {
							data.append(", "); //$NON-NLS-1$
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
						data.append("<a href=\""); //$NON-NLS-1$
						data.append("javascript:view('"); //$NON-NLS-1$
						data.append(fileNameWithoutDirectory + "#nid"+node.getId()+"_"+view.getId()+"');\">"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
						data.append(label);
						data.append("</a>\r\n"); //$NON-NLS-1$
					}
					else {
						data.append("<a href=\""); //$NON-NLS-1$
						data.append(fileNameWithoutDirectory + "#nid"+node.getId()+"_"+view.getId()+"\">"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
						data.append(label);
						data.append("</a>\r\n"); //$NON-NLS-1$
						
						if (i < countNodes-1) {
							data.append(", "); //$NON-NLS-1$
						}
					}
				}
			}

			if (!inlineView) {
				data.append("</html>"); //$NON-NLS-1$

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
				if (bOptimizeForWord) {
					data.append("</p>\r\n"); //$NON-NLS-1$
				} else {
					data.append("</div>\r\n"); //$NON-NLS-1$
				}
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

		String codeList = ""; //$NON-NLS-1$
		if (level < UIHTMLFormatDialog.LEVEL_COUNT) {
			if (bOptimizeForWord) {
				codeList = "<p class=\"codes"+level+"\">";	 //$NON-NLS-1$ //$NON-NLS-2$
			} else {
				codeList = "<div class=\"codes"+level+"\">";	 //$NON-NLS-1$ //$NON-NLS-2$
			}
		} else {
			if (bOptimizeForWord) {
				codeList = "<p class=\"codes"+(UIHTMLFormatDialog.LEVEL_COUNT-1)+"\">";			 //$NON-NLS-1$
			} else {
				codeList = "<div class=\"codes"+(UIHTMLFormatDialog.LEVEL_COUNT-1)+"\">"; //$NON-NLS-1$
			}
		}
		codeList += "<b>Tags: </b>&nbsp;"; //$NON-NLS-1$

		if (!inlineView) {
			codeList += "<br>"; //$NON-NLS-1$
		}

		try {
			Enumeration codes = node.getCodes();
			StringBuffer data = new StringBuffer(1000);
			String newFileName = null;

			if (codes.hasMoreElements()) {
				codeExists = true;

				if (!inlineView) {
					if (directory.equals("")) { //$NON-NLS-1$
						newFileName = node.getId() + "_tags.html"; //$NON-NLS-1$
					} else {
						newFileName = directory + File.separator +
									  node.getId() + "_tags.html"; //$NON-NLS-1$
					}

					data.append("<html><body>\r\n"); //$NON-NLS-1$
					data.append("<title>tags for " + node.getLabel() +"</title>\r\n"); //$NON-NLS-1$ //$NON-NLS-2$
				}

				while(codes.hasMoreElements()) {
					Code code = (Code)codes.nextElement();
					codeList += code.getName();
					if (inlineView && codes.hasMoreElements()) {
						codeList += ", "; //$NON-NLS-1$
					} else {
						codeList += "<br>"; //$NON-NLS-1$
					}
				}

				if (bOptimizeForWord) {
					codeList += "</p>\r\n";					 //$NON-NLS-1$
				} else {
					codeList += "</div>\r\n";					 //$NON-NLS-1$
				}
				data.append(codeList);

				if (!inlineView) {
					data.append("</html>"); //$NON-NLS-1$

					if (!bZipUp) {
						//FileOutputStream fos = new FileOutputStream(newFileName);
						//Writer out = new OutputStreamWriter(fos, "UTF16");
						//out.write(data.toString());
						//out.close();
						
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
			String sMessage = new String("Error: (HTMLOutline.createCodesFile) \n\n"+ex.getMessage()); //$NON-NLS-1$
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

		rootFile.append("<html>\r\n"); //$NON-NLS-1$
		rootFile.append("<head>\r\n"); //$NON-NLS-1$
		//rootFile.append("<META http-equiv=\"content-type\" content=\"text/html; charset=UTF-16\">\r\n");

		//ADD STYLES
		rootFile.append("<style>\r\n"); //$NON-NLS-1$

		if (oFormatProperties != null && !oFormatProperties.isEmpty()) {
			String sType = ""; //$NON-NLS-1$
			String currentIndent = ""; //$NON-NLS-1$
			String style = ""; //$NON-NLS-1$
			int level=0;
			int typeNumber=0;
			for (int i=0; i<UIHTMLFormatDialog.ROW_COUNT; i++) {
				typeNumber = i%UIHTMLFormatDialog.TYPE_COUNT;
				level = new Double(Math.floor(i/UIHTMLFormatDialog.TYPE_COUNT)).intValue();
				switch (typeNumber) {
					case 0: sType = "level";  break; //$NON-NLS-1$							
					case 1: sType = "detail"; break; //$NON-NLS-1$
					case 2: sType = "detaildate"; break; //$NON-NLS-1$
					case 3: sType = "reference"; break; //$NON-NLS-1$
					case 4: sType = "author"; break; //$NON-NLS-1$
					case 5: sType = "codes"; break; //$NON-NLS-1$
					case 6: sType = "views"; break; //$NON-NLS-1$
				}
						
				// FOREGROUND COLOUR
				try {
					String color = oFormatProperties.getProperty( sType+level+"color" ); //$NON-NLS-1$
					Color backgroundColor = new Color((new Integer(color).intValue())); 
					String extra = ""; //$NON-NLS-1$
					if (sType.equals("level")) { //$NON-NLS-1$
						switch(level) {
							case 0:
								extra = " h1";	 //$NON-NLS-1$
							break;
							case 1: 
								extra = " h2";	 //$NON-NLS-1$
							break;
							
							case 2: 
								extra = " h3";	 //$NON-NLS-1$
							break;
							case 3: 
								extra = " h4";	 //$NON-NLS-1$
							break;
							case 4: 
								extra = " h5";	 //$NON-NLS-1$
							break;							
							case 5:
								extra = " h6";	 //$NON-NLS-1$
							break;							
							default: 
								extra = " h6";							 //$NON-NLS-1$
						}
						
						if (bOptimizeForWord) {
							rootFile.append("\tp."+sType+level+" { color: rgb("); //$NON-NLS-1$ //$NON-NLS-2$
						} else {
							rootFile.append("\t."+sType+level+extra+" { color: rgb("); //$NON-NLS-1$ //$NON-NLS-2$
						}
					} else {						
						if (bOptimizeForWord) {
							rootFile.append("\tp."+sType+level+" { color: rgb("); //$NON-NLS-1$ //$NON-NLS-2$
						} else {
							rootFile.append("\t."+sType+level+" { color: rgb("); //$NON-NLS-1$ //$NON-NLS-2$
						}
					}
					
					rootFile.append(backgroundColor.getRed()+","+backgroundColor.getGreen()+","+backgroundColor.getBlue()+");"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				} catch(Exception e) {
					rootFile.append("\t."+sType+level+" { color: "); //$NON-NLS-1$ //$NON-NLS-2$
					rootFile.append(oFormatProperties.getProperty( sType+level+"color" )+";"); //$NON-NLS-1$ //$NON-NLS-2$
				}				
				
				// BACKGROUND COLOUR
				try {
					String color = oFormatProperties.getProperty( sType+level+"back" ); //$NON-NLS-1$
					Color backgroundColor = new Color((new Integer(color).intValue())); 
					rootFile.append(" background: rgb("); //$NON-NLS-1$
					rootFile.append(backgroundColor.getRed()+","+backgroundColor.getGreen()+","+backgroundColor.getBlue()+");"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				} catch(Exception e) {
					rootFile.append(" background: "); //$NON-NLS-1$
					rootFile.append(oFormatProperties.getProperty( sType+level+"back" ) +";"); //$NON-NLS-1$ //$NON-NLS-2$
				}
				
				// FONT FAMILY/SIZE AND MARGINS
				rootFile.append(" margin: "); //$NON-NLS-1$
				rootFile.append(oFormatProperties.getProperty( sType+level+"top" )); //$NON-NLS-1$
				rootFile.append("in 0in 0in ");	 //$NON-NLS-1$
				currentIndent = oFormatProperties.getProperty( sType+level+"indent" ); //$NON-NLS-1$
				rootFile.append(currentIndent);
				rootFile.append("in; font-family: \""); //$NON-NLS-1$
				rootFile.append(oFormatProperties.getProperty( sType+level+"font" )); //$NON-NLS-1$
				rootFile.append("\"; font-size: "); //$NON-NLS-1$
				rootFile.append(oFormatProperties.getProperty( sType+level+"size" )); //$NON-NLS-1$
						
				if (level==0) {
					rootFile.append("pt; text-align: left; text-decoration: none;");												 //$NON-NLS-1$
				} else {											
					rootFile.append("pt; vertical-align: top; text-align: left; text-decoration: none;"); //$NON-NLS-1$
				}
				
				// FONT STYLE
				style = oFormatProperties.getProperty( sType+level+"style" );			 //$NON-NLS-1$
				if (style != null) {
					if (style.equals("bold") || style.equals("bold-italic")) { //$NON-NLS-1$ //$NON-NLS-2$
						rootFile.append(" font-weight: bold;"); //$NON-NLS-1$
					} else {
						rootFile.append(" font-weight: normal;"); //$NON-NLS-1$
					}				
					if (style.equals("italic") || style.equals("bold-italic")) { //$NON-NLS-1$ //$NON-NLS-2$
						rootFile.append(" font-style: italic;"); //$NON-NLS-1$
					} else {
						rootFile.append(" font-style: normal;"); //$NON-NLS-1$
					}
				}
					
				// LAST BITS
				switch (typeNumber) {
					case 0: //LEVEL
						rootFile.append(" padding: 3px;}\r\n"); //$NON-NLS-1$
						if (bOptimizeForWord) {
							rootFile.append("\tp.level"+level+" a { color: #000000; text-decoration: none;}\r\n");				 //$NON-NLS-1$ //$NON-NLS-2$
							rootFile.append("\tp.level"+level+" a:hover {text-decoration: underline;}\r\n"); //$NON-NLS-1$ //$NON-NLS-2$
							rootFile.append("\tp.level"+level+" img { padding: 3px 0px 3px 0px; margin-right: 10px; vertical-align: text-bottom;}\r\n");							 //$NON-NLS-1$ //$NON-NLS-2$
						} else {
							rootFile.append("\t.level"+level+" a { color: #000000; text-decoration: none;}\r\n");				 //$NON-NLS-1$ //$NON-NLS-2$
							rootFile.append("\t.level"+level+" a:hover {text-decoration: underline;}\r\n"); //$NON-NLS-1$ //$NON-NLS-2$
							rootFile.append("\t.level"+level+" img { padding: 3px 0px 3px 0px; margin-right: 10px; vertical-align: text-bottom;}\r\n"); //$NON-NLS-1$ //$NON-NLS-2$
						}
					break;
					case 1: // DETAIL
						rootFile.append(" padding: 3px; display: block;"); //$NON-NLS-1$
						rootFile.append("}\r\n");	 //$NON-NLS-1$
						if (bOptimizeForWord) {
							rootFile.append("\tp.detail"+level+" a {color: #000000; text-decoration: none;}\r\n"); //$NON-NLS-1$ //$NON-NLS-2$
							rootFile.append("\tp.detail"+level+" a:hover { color: #000000; text-decoration: underline;}\r\n");							 //$NON-NLS-1$ //$NON-NLS-2$
						} else {
							rootFile.append("\t.detail"+level+" a {color: #000000; text-decoration: none;}\r\n"); //$NON-NLS-1$ //$NON-NLS-2$
							rootFile.append("\t.detail"+level+" a:hover { color: #000000; text-decoration: underline;}\r\n"); //$NON-NLS-1$ //$NON-NLS-2$
						}
					break;
					case 2: // DETAIL DATE
						rootFile.append(" padding: 3px; display: block;"); //$NON-NLS-1$
						rootFile.append("}\r\n");					 //$NON-NLS-1$
					break;
					case 3: // REFERENCE
						rootFile.append(" padding: 3px; display: block;"); //$NON-NLS-1$
						rootFile.append("}\r\n"); //$NON-NLS-1$
						if (bOptimizeForWord) {	
							rootFile.append("\tp.reference"+level+" a {color: #000; text-decoration: none;}\r\n"); //$NON-NLS-1$ //$NON-NLS-2$
							rootFile.append("\tp.reference"+level+" a:hover {text-decoration: underline;}\r\n");						 //$NON-NLS-1$ //$NON-NLS-2$
						} else {
							rootFile.append("\t.reference"+level+" a {color: #000; text-decoration: none;}\r\n"); //$NON-NLS-1$ //$NON-NLS-2$
							rootFile.append("\t.reference"+level+" a:hover {text-decoration: underline;}\r\n"); //$NON-NLS-1$ //$NON-NLS-2$
						}
					break;
					case 4: // AUTHOR
						rootFile.append(" padding: 3px; display: block;"); //$NON-NLS-1$
						rootFile.append("}\r\n");					 //$NON-NLS-1$
					break;
					case 5: // CODES
						rootFile.append(" padding: 3px; vertical-align: middle;"); //$NON-NLS-1$
						rootFile.append("}\r\n");					 //$NON-NLS-1$
					break;
					case 6: // VIEWS
						rootFile.append(" padding: 3px; vertical-align: middle;"); //$NON-NLS-1$
						rootFile.append("}\r\n");	 //$NON-NLS-1$
						if (bOptimizeForWord) {
							rootFile.append("\tp.views"+level+" a {color: #000; text-decoration: none;}\r\n"); //$NON-NLS-1$ //$NON-NLS-2$
							rootFile.append("\tp.views"+level+" a:hover {text-decoration: underline;}\r\n");							 //$NON-NLS-1$ //$NON-NLS-2$
						} else {
							rootFile.append("\t.views"+level+" a {color: #000; text-decoration: none;}\r\n"); //$NON-NLS-1$ //$NON-NLS-2$
							rootFile.append("\t.views"+level+" a:hover {text-decoration: underline;}\r\n"); //$NON-NLS-1$ //$NON-NLS-2$
						}
					break;
				}						
			}		
		}

		rootFile.append("\r\n\tbody { margin: 15px; width: 810px;}\r\n"); //$NON-NLS-1$
		rootFile.append("\ta {text-decoration: none;}\r\n"); //$NON-NLS-1$
		
		// MENU BACKGROUND COLOUR
		rootFile.append("\t.left-col {width: 200px; background-color:"); //$NON-NLS-1$
		try {
			String color = oFormatProperties.getProperty( "menubackcolor" ); //$NON-NLS-1$
			Color oColor = new Color((new Integer(color).intValue())); 
			rootFile.append(" rgb("); //$NON-NLS-1$
			rootFile.append(oColor.getRed()+","+oColor.getGreen()+","+oColor.getBlue()+");"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		} catch(Exception e) {
			rootFile.append(" white;"); //$NON-NLS-1$
		}		
		rootFile.append(" float: left; border: 1px solid"); //$NON-NLS-1$
		
		//MENU BORDER COLOUR
		try {
			String color = oFormatProperties.getProperty( "menubordercolor" ); //$NON-NLS-1$
			Color oColor = new Color((new Integer(color).intValue())); 
			rootFile.append(" rgb("); //$NON-NLS-1$
			rootFile.append(oColor.getRed()+","+oColor.getGreen()+","+oColor.getBlue()+");"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		} catch(Exception e) {
			rootFile.append(" white;"); //$NON-NLS-1$
		}		
		rootFile.append("}\r\n"); //$NON-NLS-1$
		
		rootFile.append("\t.left-col-content {padding: 5px; "); //$NON-NLS-1$
		rootFile.append("font-family: \""); //$NON-NLS-1$
		rootFile.append(oFormatProperties.getProperty( "menufontfamily" )); //$NON-NLS-1$
		rootFile.append("\"; font-size: "); //$NON-NLS-1$
		rootFile.append(oFormatProperties.getProperty( "menufontsize" )); //$NON-NLS-1$
		rootFile.append("pt; color: "); //$NON-NLS-1$
		try {
			String color = oFormatProperties.getProperty( "menutextcolor" ); //$NON-NLS-1$
			Color oColor = new Color((new Integer(color).intValue())); 
			rootFile.append(" rgb("); //$NON-NLS-1$
			rootFile.append(oColor.getRed()+","+oColor.getGreen()+","+oColor.getBlue()+");"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		} catch(Exception e) {
			rootFile.append(" white;"); //$NON-NLS-1$
		}				
		String style = oFormatProperties.getProperty( "menufontstyle" );								 //$NON-NLS-1$
		if (style.equals("bold") || style.equals("bold-italic")) { //$NON-NLS-1$ //$NON-NLS-2$
			rootFile.append(" font-weight: bold;"); //$NON-NLS-1$
		} else {
			rootFile.append(" font-weight: normal;"); //$NON-NLS-1$
		}				
		if (style.equals("italic") || style.equals("bold-italic")) { //$NON-NLS-1$ //$NON-NLS-2$
			rootFile.append(" font-style: italic;"); //$NON-NLS-1$
		} else {
			rootFile.append(" font-style: normal;"); //$NON-NLS-1$
		}
		rootFile.append("}\r\n"); //$NON-NLS-1$
		
		rootFile.append("\t.left-col a {display: block; padding-bottom: 10px; text-decoration: none;  color: #000;}\r\n"); //$NON-NLS-1$
		rootFile.append("\t.left-col a:hover {text-decoration: underline; color: #000;}\r\n"); //$NON-NLS-1$
		rootFile.append("\t.left-col a:visited {text-decoration: underline; color: #352e85; text-decoration: none;}\r\n"); //$NON-NLS-1$

		rootFile.append("\t.right-col {width: 570px; background-color: #fff; float: left; margin-left:15px;}\r\n"); //$NON-NLS-1$
		rootFile.append("\t.right-col-content h1, h2, h3, h4, h5, h6 {padding-top: 0px; margin-top:0px; margin-bottom: 0px; vertical-align: top;}\r\n"); //$NON-NLS-1$
		rootFile.append("\t.ref a	{font-family: \"Verdana\"; font-size: 7pt; color: #352e85; font-weight: normal; text-decoration: none; vertical-align:super; line-height:1px; display: inline;}\r\n"); //$NON-NLS-1$
		rootFile.append("\t.ref a:hover {text-decoration: underline; color: #000;}\r\n"); //$NON-NLS-1$
		rootFile.append("\tsup a {font-family: \"Verdana\"; font-size: 7pt; color: #352e85; font-weight: normal; text-decoration: none; line-height:-1em; display: inline;}\r\n"); //$NON-NLS-1$
		rootFile.append("\tsup a:hover {text-decoration: underline; color: #000;}\r\n"); //$NON-NLS-1$

		rootFile.append("\t.top a {color: #000; background: #fff; display:block; font-family: \"Verdana\"; font-size: 8pt; vertical-align: middle; text-align: left; font-weight: normal; font-style: normal;}\r\n"); //$NON-NLS-1$
		rootFile.append("\t.top a:hover {text-decoration: underline;}\r\n");		 //$NON-NLS-1$
				
		// VIEW DIVIDER BAR
		rootFile.append("\t.unit-divider {width: 570px; height: 5px; border-top: 2px dotted"); //$NON-NLS-1$
		try {
			String divider = oFormatProperties.getProperty( "dividercolor" ); //$NON-NLS-1$
			Color DividerColor = new Color((new Integer(divider).intValue())); 
			rootFile.append(" rgb("); //$NON-NLS-1$
			rootFile.append(DividerColor.getRed()+","+DividerColor.getGreen()+","+DividerColor.getBlue()+");"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		} catch(Exception e) {
			rootFile.append(" white;"); //$NON-NLS-1$
		}
		rootFile.append(" margin: 10px 0px 4px 0px;}\r\n"); //$NON-NLS-1$
		
		rootFile.append("</style>\r\n"); //$NON-NLS-1$

		rootFile.append("<title>"); //$NON-NLS-1$

		if (currentViewPresent) {
			rootFile.append(originalTitle);
		}
		else {
			rootFile.append(title);
		}

		rootFile.append("</title>\r\n"); //$NON-NLS-1$
		rootFile.append("</head>\r\n\r\n"); //$NON-NLS-1$

		rootFile.append("<body>\r\n"); //$NON-NLS-1$

		if (newView == true) {
			rootFile.append("<script> function opennewwindow(url, name, features) { popBox = window.open(url,name,features); popBox.focus(); } </script>\r\n"); //$NON-NLS-1$
		}

		if (includeNavigationBar) {
			rootFile.append("<div class=\"left-col left-col-content\">\r\n"); //$NON-NLS-1$
			rootFile.append(getTableIndex());
			rootFile.append("</div>\r\n\r\n"); //$NON-NLS-1$
		}
		else if (bDisplayInDifferentPages) {
			getTableIndex();
		}
		
		if (includeNavigationBar) {
			rootFile.append("<div class=\"right-col right-col-content\">\r\n"); //$NON-NLS-1$
		}
		if (!bOptimizeForWord) {
			rootFile.append("<a Name=\"top\"></a>\r\n"); //$NON-NLS-1$
		}
	}

	/**
	 * Write out the final closing HTML tags for the current HTML file being created.
	 *
	 * @exception java.io.IOException, if we are not zipping but trying to write the file out, and it fails.
	 */
	private void writeEndTags() throws IOException {

		if (!bOptimizeForWord) {
			rootFile.append("<span class=\"top\"><a href=\"#top\">Top</a></span>\r\n"); //$NON-NLS-1$
		}
		
		if (includeNavigationBar) {
			rootFile.append("</div>\r\n"); //$NON-NLS-1$
		}
		rootFile.append("</body>\r\n"); //$NON-NLS-1$
		rootFile.append("</html>"); //$NON-NLS-1$

		if (bZipUp) {
			File file = new File(sCurrentFileName);
			htCreatedFiles.put(file.getName(), rootFile.toString());
			rootFile = new StringBuffer(1000);
		}
		else {			
			//FileOutputStream fos = new FileOutputStream(sCurrentFileName);
			//Writer out = new OutputStreamWriter(fos, "UTF16");
			//out.write(rootFile.toString());
			//out.close();
						
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

		int indexOfSlashInHTMLPath = fileName.lastIndexOf("/"); //$NON-NLS-1$
		int indexOfSlashInFile = fileName.lastIndexOf(File.separator);

		if (indexOfSlashInHTMLPath != -1) { //multiple pages
			pathForHTMLFile = fileName.substring(0, indexOfSlashInHTMLPath + 1);
		}
		else if(indexOfSlashInFile != -1) {
			pathForHTMLFile = fileName.substring(0, indexOfSlashInFile + 1);
		}
		else {
			pathForHTMLFile = ""; //$NON-NLS-1$
		}
		
		// if adding node anchors, copy selected anchor image into images dir of export
		if (bIncludeNodeAnchors && !bUseAnchorNumbers) {

			File anchorFile = new File(sAnchorImage);
			String anchorFileName = anchorFile.getName();

			// ONLY COPY FILE TO IMAGE DIR IF NOT ZIPPING UP EXPORT
			if (bZipUp) {
				htExportFiles.put(sAnchorImage, "images/"+anchorFileName); //$NON-NLS-1$
			}
			else {
				File directory = new File(pathForHTMLFile + "images"); //$NON-NLS-1$
				if (!directory.isDirectory()) {
					directory.mkdirs();
				}

				try {
					FileInputStream fis = new FileInputStream(sAnchorImage);
					FileOutputStream fos = new FileOutputStream(pathForHTMLFile+"images"+ProjectCompendium.sFS+anchorFileName); //$NON-NLS-1$

					byte[] data = new byte[fis.available()];
					fis.read(data);
					fos.write(data);
				}
				catch (Exception e) {
					String sMessage = new String(LanguageProperties.getString(LanguageProperties.IO_BUNDLE, "HTMLOutline.errorCopyingAnchorImage") +":\n\n"+ e.getLocalizedMessage()); //$NON-NLS-1$
					if (!vtMessages.contains(sMessage)) {
						vtMessages.addElement(sMessage);
					}
					//ProjectCompendium.APP.displayError("Unable to copy anchor image: " + e.getMessage());
				}
			}

			// FOR THE PURPOSES OF THE HTML FILE
			sAnchorImage = "images/"+anchorFileName; //$NON-NLS-1$
		}

		tableIndex = null;
		try {

			writeBeginTags();
			NodeSummary node = null;
			String sNodeID = ""; //$NON-NLS-1$
			int nType = 0;
			for (int i = 0; i < nodeList.size(); i++) {
				
				node = (NodeSummary) nodeList.elementAt(i);
				IModel model = ProjectCompendium.APP.getModel();
				node.initialize(model.getSession(), model);
				sNodeID = node.getId();
				nType = node.getType();
				int nodeIndex = ((Integer)nodeIndexList.elementAt(i)).intValue();
				int level = ((Integer)nodeLevelList.elementAt(i)).intValue();

				sNodeLabel = CoreUtilities.cleanHTMLText(node.getLabel());
				if(sNodeLabel == null)
					sNodeLabel = ""; //$NON-NLS-1$

				sNodeAuthor = "(" + node.getAuthor() + ")"; //$NON-NLS-1$ //$NON-NLS-2$

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

				int maxLevel = UIHTMLFormatDialog.LEVEL_COUNT-1;
				if(level > maxLevel) {
					nLevel = maxLevel - nStartExportAtLevel;
					level=maxLevel; 
				}
				else {
					nLevel = level - nStartExportAtLevel;
				}
				
				if (level == 0) {
					nDepth = 0;
				} else {
					if (View.isViewType(nType)) {
						nDepth++;
					}
				}

				String beginTags = getBeginTags(node, level, nType, nDepth);
				rootFile.append(beginTags);

				if (nodeIndex != -1) {
					rootFile.append((nodeIndex+1) + "."); //$NON-NLS-1$
				}

				String image = node.getImage();
				String source = node.getSource();
				int nodeType = node.getType();

				boolean hasExternalFile = false;

				boolean bViewNav = false;
				String sViewNav = ""; //$NON-NLS-1$
				if (node instanceof View && level!=0) {
					//add a link if available in the list with level 0
					String 	newFileName = this.createFileName((View)node);
					for (int k = 0; k < nodeList.size(); k++) {
						if (sNodeID.equals(((NodeSummary)nodeList.elementAt(k)).getId()) &&
							(((Integer)nodeLevelList.elementAt(k)).intValue() == 0)) {
							bViewNav = true;
							if (bDisplayInDifferentPages) {
								sViewNav = "<a href=\"" + newFileName + "\">\r\n"; //$NON-NLS-1$ //$NON-NLS-2$
							}
							else { //o/w just use the label of the node + ':' + id (number) of node
								sViewNav="<a href=\"#"+node.getLabel().replace(' ','_') + ":" + node.getId() + "\">\r\n"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
							}
							break;
						}
					}
				}

				if (bIncludeImage || bIncludeReferences) {
					boolean hasExternalImage = false;

					int imageWidth = 25;
					int imageHeight = 25;

					String path = ""; //$NON-NLS-1$
					
					boolean bUseSmallImage = false;
					if (htNodePositions.containsKey(sNodeID)) {
						NodePosition npos = (NodePosition)htNodePositions.get(sNodeID);
						bUseSmallImage = true;
						path = UIImages.getPath(nodeType, npos.getShowSmallIcon());
					} else {
						path = UIImages.getPath(nodeType, false);
					}
					String newPath = null;

					//check to see if node is a Reference Node
					if (nodeType == ICoreConstants.REFERENCE || nodeType == ICoreConstants.REFERENCE_SHORTCUT) {
						if (image != null && !image.equals("")) { //$NON-NLS-1$
							if (image.startsWith("www.")) { //$NON-NLS-1$
								image = "http://"+image; //$NON-NLS-1$
							}																																
							path = image;
							hasExternalImage = true;

							if(source != null && !source.equals("")) { //$NON-NLS-1$
								hasExternalFile = true;
							}
						}
						else if(source != null && source.equals("")) { //if no ref, leave ref icon (path) //$NON-NLS-1$
							path = path;
						}
						else {
							if (source != null) {
								if (source.startsWith("www.")) { //$NON-NLS-1$
									source = "http://"+source; //$NON-NLS-1$
								}																
								if ( UIImages.isImage(source) ) {
									hasExternalImage = true;
									hasExternalFile = true;
									path = source;
								}
								else {
									if (CoreUtilities.isFile(source)) {
										hasExternalFile = true;
										path = UIImages.getReferencePath(source, path, bUseSmallImage);
									}
									else
										path = UIImages.getReferencePath(source, path, bUseSmallImage);
								}
							}
						}
					}
					else if(View.isViewType(nodeType) || View.isShortcutViewType(nodeType)) {

						image = node.getImage();
						if (image != null && !image.equals("")) { //$NON-NLS-1$
							if (image.startsWith("www.")) { //$NON-NLS-1$
								image = "http://"+image; //$NON-NLS-1$
							}																								
							path = image;
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
						String imageName = ""; //$NON-NLS-1$
						if (imageFile.exists()) {
							imageName = imageFile.getName();
							newPath = pathForHTMLFile + "images" + File.separator + imageName; //$NON-NLS-1$

							// ONLY COPY FILE TO EXPORT DIR IF NOT ZIPPING UP EXPORT
							if (bZipUp) {
								File newFile = new File(newPath);
								htExportFiles.put(path, "images/"+newFile.getName()); //$NON-NLS-1$
							}
							else {
								File newImageFile = new File(newPath);
								if (!newImageFile.exists()) {
									//then create a directory instance, and see if the directory exists.
									File directory = new File(pathForHTMLFile + "images"); //$NON-NLS-1$
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
										String sMessage = new String(LanguageProperties.getString(LanguageProperties.IO_BUNDLE, "HTMLOutline.errorCreatingImage") +":\n\n"+ e.getLocalizedMessage()); //$NON-NLS-1$
										if (!vtMessages.contains(sMessage)) {
											vtMessages.addElement(sMessage);
										}
										//ProjectCompendium.APP.displayError("Unable to create image:" + e.getMessage());
									}
								}
							}

							htmlPath = "images/"+imageName; //$NON-NLS-1$
						}

						if (hasExternalImage) {
							rootFile.append("<a href=\""+htmlPath+"\" target=\"_blank\"><img alt=\""+UINodeTypeManager.getNodeTypeDescription(node.getType())+" Icon: "+imageName+"\" border=\"0\" src=\"" + htmlPath); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							rootFile.append("\" width=\""+imageWidth+"\" Height=\""+imageHeight+"\"></a>\r\n"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
						}
						else
							if (bViewNav) {
								rootFile.append(sViewNav);
								rootFile.append("<img alt=\""+UINodeTypeManager.getNodeTypeDescription(node.getType())+"\" border=\"0\" src=\"" + htmlPath + "\" width=\""+imageWidth+"\" Height=\""+imageHeight+"\">\r\n"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
								rootFile.append("</a>\r\n"); //$NON-NLS-1$
							} else {
								rootFile.append("<img alt=\""+UINodeTypeManager.getNodeTypeDescription(node.getType())+"\" border=\"0\" src=\"" + htmlPath + "\" width=\""+imageWidth+"\" Height=\""+imageHeight+"\">\r\n"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
							}
					}
				}

				// NODE LABEL
				if (level == 0) {
					currentViewName = node.getLabel().replace(' ','_');
					rootFile.append("<a valign=\"bottom\" name=\""+currentViewName + ":" + node.getId() + "\"></a>\r\n"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				}
				else if (bViewNav) {
					rootFile.append("<a valign=\"bottom\" name=\""+currentViewName + ":" + node.getId() + "\"></a>\r\n"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					rootFile.append(sViewNav);
				}
				else {
					rootFile.append("<a name=\""+currentViewName + ":" + node.getId() + "\"></a>\r\n"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				}
				
				if (bIncludeImage) rootFile.append("&nbsp;");		// mlb for Jeff //$NON-NLS-1$
				
				rootFile.append(sNodeLabel);
				if (bViewNav) {
					rootFile.append("</a>");					 //$NON-NLS-1$
				}
				rootFile.append(getInnerEndTags(node, level, nType, nDepth));

				// NODE AUTHOR
				if(bPrintNodeAuthor) {
					if(nAuthorLength > 1) {
						String authorstart = ""; //$NON-NLS-1$
						if (level <= maxLevel) {
							if (bOptimizeForWord) {
								authorstart = "<p class=\"author"+level+"\">";								 //$NON-NLS-1$ //$NON-NLS-2$
							} else {
								authorstart = "<div class=\"author"+level+"\">"; //$NON-NLS-1$ //$NON-NLS-2$
							}
						} else {
							if (bOptimizeForWord) {							
								authorstart = "<p class=\"author"+maxLevel+"\">"; //$NON-NLS-1$
							} else {
								authorstart = "<div class=\"author"+maxLevel+"\">";								 //$NON-NLS-1$
							}
						}
						
						if (bOptimizeForWord) {						
							rootFile.append(authorstart+sNodeAuthor+"</p>\r\n"); //$NON-NLS-1$
						} else {
							rootFile.append(authorstart+sNodeAuthor+"</div>\r\n");							 //$NON-NLS-1$
						}
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
							sNodeDetail = ""; //$NON-NLS-1$

						sNodeDetail = CoreUtilities.cleanHTMLText(sNodeDetail);

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
									if (level <= maxLevel) {
										if (bOptimizeForWord) {
											rootFile.append("<p class=\"detaildate"+level+"\">\r\n");											 //$NON-NLS-1$ //$NON-NLS-2$
										} else {
											rootFile.append("<div class=\"detaildate"+level+"\">\r\n"); //$NON-NLS-1$ //$NON-NLS-2$
										}
									} else {
										if (bOptimizeForWord) {
											rootFile.append("<p class=\"detaildate"+maxLevel+"\">\r\n");											 //$NON-NLS-1$
										} else {
											rootFile.append("<div class=\"detaildate"+maxLevel+"\">\r\n"); //$NON-NLS-1$
										}
									}
									rootFile.append("<strong>Entered:</strong> "+sdf.format(creation).toString()+"&nbsp;&nbsp;<strong>Modified:</strong> "+sdf.format(modified).toString()); //$NON-NLS-1$ //$NON-NLS-2$
									if (bOptimizeForWord) {
										rootFile.append("</p>\r\n");										 //$NON-NLS-1$
									} else {
										rootFile.append("</div>\r\n"); //$NON-NLS-1$
									}
								}

								if (bIncludeDetailAnchors) {
									rootFile.append("<a name='detail"+node.getId()+"'></a>\r\n"); //$NON-NLS-1$ //$NON-NLS-2$
								}

								//	Node Detail work here
								if (level <= maxLevel) {
									if (bOptimizeForWord) {
										rootFile.append("<p class=\"detail"+level+"\">\r\n");										 //$NON-NLS-1$ //$NON-NLS-2$
									} else {
										rootFile.append("<div class=\"detail"+level+"\">\r\n"); //$NON-NLS-1$ //$NON-NLS-2$
									}
								} else {
									if (bOptimizeForWord) {
										rootFile.append("<p class=\"detail"+maxLevel+"\">\r\n");										 //$NON-NLS-1$
									} else {
										rootFile.append("<div class=\"detail"+maxLevel+"\">\r\n"); //$NON-NLS-1$
									}
								}

								sNodeDetail = formatString(sNodeDetail);
								sNodeDetail = sNodeDetail.trim();
								
								rootFile.append(sNodeDetail);

								if (bIncludeDetailAnchors) {
									if (bUseAnchorNumbers)
										rootFile.append("<sup alt=\"url anchor\"><a href=\"#detail"+node.getId()+"\">"+anchorCount+"</a></sup>"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
									else
										rootFile.append("<a href=\"#detail"+node.getId()+"\">&nbsp;<img alt=\"url anchor\" border=\"0\" src=\""+sAnchorImage+"\"></a>"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
									anchorCount++;
								}

								if (bOptimizeForWord) {
									rootFile.append("</p>\r\n");									 //$NON-NLS-1$
								} else {
									rootFile.append("</div>\r\n"); //$NON-NLS-1$
								}

								if (countDetails > 1)
									rootFile.append("<br>\r\n"); //$NON-NLS-1$
							}
						}
					}
				}
				
				// REFERENCE
				String refName = ""; //$NON-NLS-1$
				File refFile = new File(source);
				if (refFile.exists() && !refFile.isDirectory()) {
					refName = refFile.getName();
					if (hasExternalFile && bIncludeReferences && bZipUp) {
						htExportFiles.put(refFile.getAbsolutePath(), "references/"+refFile.getName()); //$NON-NLS-1$
						source = "references/"+refName; //$NON-NLS-1$
					}
					else if (hasExternalFile && bIncludeReferences && !bZipUp) {
						File newFile = new File(pathForHTMLFile + "references" + File.separator + refName); //$NON-NLS-1$
						source = "references/"+refName; //$NON-NLS-1$
						if (!newFile.exists()) {
							File directory = new File(pathForHTMLFile + "references"); //$NON-NLS-1$
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
								String sMessage = new String(LanguageProperties.getString(LanguageProperties.IO_BUNDLE, "HTMLOutline.unableToCreateReference")+":\n\n" + e.getLocalizedMessage()); //$NON-NLS-1$
								if (!vtMessages.contains(sMessage)) {
									vtMessages.addElement(sMessage);
								}
								//ProjectCompendium.APP.displayError("Unable to create reference:" + e.getMessage());
							}
						}
					}
				}

				if ( (nodeType == ICoreConstants.REFERENCE || nodeType == ICoreConstants.REFERENCE_SHORTCUT)
					&& source != null && !source.equals("")) { //$NON-NLS-1$

					String lowerCaseSource = source.toLowerCase();
					if (source.startsWith("www.") || source.startsWith("http:") || source.startsWith("https:")) { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
						refName = source;
					}

					if (!refName.equals("")) { //$NON-NLS-1$
						if (level <= maxLevel) {
							if (bOptimizeForWord) {	
								rootFile.append("<p class=\"reference" + level + "\">\r\n");								 //$NON-NLS-1$ //$NON-NLS-2$
							} else {
								rootFile.append("<div class=\"reference" + level + "\">\r\n"); //$NON-NLS-1$ //$NON-NLS-2$
							}
						} else {
							if (bOptimizeForWord) {								
								rootFile.append("<p class=\"reference"+maxLevel+"\">\r\n"); //$NON-NLS-1$
							} else {
								rootFile.append("<div class=\"reference"+maxLevel+"\">\r\n"); //$NON-NLS-1$
							}
						}
						rootFile.append("<strong>Reference:</strong>&nbsp;"); //$NON-NLS-1$
						rootFile.append("<a href=\"" + source + "\">" + refName + "</a>\r\n"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
						
						if (bOptimizeForWord) {
							rootFile.append("</p>\r\n"); //$NON-NLS-1$
						} else {
							rootFile.append("</div>\r\n"); //$NON-NLS-1$
						}
					}
				}

				// TAGS
				String tags = ""; //$NON-NLS-1$
				boolean codesPresent = false;
				if (bIncludeTags) {
					codesPresent = createCodesFile(node, level);
				}

				if ( (codesPresent) && (newView)) {
					if (level <= maxLevel) {
						if (bOptimizeForWord) {
							tags += "<p class=\"codes"+level+"\">\r\n"; //$NON-NLS-1$ //$NON-NLS-2$
						} else {
							tags += "<div class=\"codes"+level+"\">\r\n"; //$NON-NLS-1$ //$NON-NLS-2$
						}
					} else {
						if (bOptimizeForWord) {
							tags += "<p class=\"codes"+maxLevel+"\">\r\n"; //$NON-NLS-1$
						} else {
							tags += "<div class=\"codes"+maxLevel+"\">\r\n"; //$NON-NLS-1$
						}
					}
					tags += "<a href=\""; //$NON-NLS-1$
					tags += "javascript:opennewwindow('"; //$NON-NLS-1$
					tags += node.getId() + "_tags.html" + "','" + node.getId() + "','width=200,height=300"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					tags += "');\">"; //$NON-NLS-1$
					tags += "tags"; //$NON-NLS-1$
					tags += "</a>\r\n"; //$NON-NLS-1$
					
					if (bOptimizeForWord) {
						tags += "</p>\r\n"; //$NON-NLS-1$
					} else {
						tags += "</div>\r\n"; //$NON-NLS-1$
					}
				}
				rootFile.append(tags);					
				
				// VIEWS
				tags = ""; //$NON-NLS-1$
				boolean presentInMoreThanOneView = false;
				try {
					if (bIncludeViews) {
						presentInMoreThanOneView = createLinksFile(node, level);
					}
				}
				catch (IOException e) {
					String sMessage = new String("Exception: (HTMLOutline.print - presentInMoreThanOneView) " + e.getMessage()); //$NON-NLS-1$
					if (!vtMessages.contains(sMessage)) {
						vtMessages.addElement(sMessage);
					}
					//ProjectCompendium.APP.displayError("Exception: (HTMLOutline.print - presentInMoreThanOneView) " + e.getMessage());
				}

				if ((presentInMoreThanOneView) && (newView)) {
					if (level <= maxLevel) {
						if (bOptimizeForWord) {
							tags += "<p class=\"views"+level+"\">"; //$NON-NLS-1$ //$NON-NLS-2$
						} else {
							tags += "<div class=\"views"+level+"\">"; //$NON-NLS-1$ //$NON-NLS-2$
						}
					} else {
						if (bOptimizeForWord) {
							tags += "<p class=\"views"+maxLevel+"\">"; //$NON-NLS-1$
						} else {
							tags += "<div class=\"views"+maxLevel+"\">"; //$NON-NLS-1$
						}
					}
					tags += "<a href=\""; //$NON-NLS-1$
					tags += "javascript:opennewwindow('"; //$NON-NLS-1$
					tags += node.getId() + ".html" + "','" + node.getId() + //$NON-NLS-1$ //$NON-NLS-2$
							"','width=200,height=300"; //$NON-NLS-1$
					tags += "');\">"; //$NON-NLS-1$
					tags += "views"; //$NON-NLS-1$
					tags += "</a>\r\n"; //$NON-NLS-1$
					
					if (bOptimizeForWord) {
						tags += "</p>\r\n"; //$NON-NLS-1$
					} else {
						tags += "</div>\r\n"; //$NON-NLS-1$
					}
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
					txtLabel.append("\n\n");					 //$NON-NLS-1$
				}
				//txtLabel.setAutoscrolls(true);
				txtLabel.setEditable(false);
				JScrollPane scrollpane = new JScrollPane(txtLabel);
				scrollpane.setPreferredSize(new Dimension(600,300));					
				JOptionPane.showMessageDialog(ProjectCompendium.APP,
						scrollpane,
                        LanguageProperties.getString(LanguageProperties.IO_BUNDLE, "HTMLOutline.exportProblems"), //$NON-NLS-1$
                        JOptionPane.WARNING_MESSAGE);				
			}
		}
		catch(Exception ex)	{
			ex.printStackTrace();
			ProjectCompendium.APP.displayError("Exception: (HTMLOutline.print) " + ex.getMessage() ); //$NON-NLS-1$
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
					/*FileOutputStream fos = new FileOutputStream(sFilePath);
					Writer out2 = new OutputStreamWriter(fos, "UTF16");
					out2.write(sData);
					out2.close();
					
					fi = new FileInputStream(sFilePath);
					origin = new BufferedInputStream(fi, BUFFER);
					entry = new ZipEntry(sFilePath);
					out.putNextEntry(entry);

					while((count = origin.read(data2, 0, BUFFER)) != -1) {
						out.write(data2, 0, count);
					}
					origin.close();

					CoreUtilities.deleteFile(new File(sFilePath));*/
					
					entry = new ZipEntry(sFilePath);
					out.putNextEntry(entry);
					int len = sData.length();
					byte data3[] = sData.getBytes();
					out.write(data3, 0, len);
				}
				catch (Exception ex) {
					System.out.println("Unable to zip up html export: \n\n"+sFilePath+"\n\n"+ex.getMessage()); //$NON-NLS-1$ //$NON-NLS-2$
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
					System.out.println("Unable to zip up html export: \n\n"+sOldFilePath+"\n\n"+ex.getMessage()); //$NON-NLS-1$ //$NON-NLS-2$
				}
			}

			out.close();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
}