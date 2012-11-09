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

import java.util.*;
import java.lang.*;
import java.io.*;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;
import javax.swing.table.*;

import com.compendium.core.*;
import com.compendium.core.datamodel.*;
import com.compendium.core.datamodel.services.*;

import com.compendium.LanguageProperties;
import com.compendium.ProjectCompendium;
import com.compendium.ui.plaf.*;
import com.compendium.ui.dialogs.UIProgressDialog;
import com.compendium.ui.*;

/**
 * UIExportRDFDialog defines the export dialog, that allows
 * the user to export PC Map/List Views to an XML document formatted for RDF.
 * <p>
 * THIS CLASS IS NOT CURRENTLY USED.
 *
 * @author	Michelle Bachler
 */
public class UIExportRDFDialog extends UIDialog implements ActionListener, IUIConstants {

	/** The default directory to export to.*/
	private static String		exportDirectory = ProjectCompendium.sHOMEPATH+ProjectCompendium.sFS+LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIExportRDFDialog.title"); //$NON-NLS-1$

	/** The parent frame of this dialog.*/
	private JFrame				oParent	= null;

	/** The button to start the export.*/
	private JButton				pbExport	= null;

	/** The button to close the dialog.*/
	private JButton				pbClose	= null;

	/** Set whether to export view to thier full depth.*/
	private JRadioButton		rbAllDepths = null;

	/** Set whether to export view to thier current depth only.*/
	private	JRadioButton		rbCurrentDepth = null;

	/** The file browing dialog to enter the export filename.*/
	private	FileDialog			fdgExport = null;

	/** Nodes to export.*/
	private Vector				vtNodes = new Vector(51);

	/** View to export.*/
	private Vector				vtViews = new Vector(51);

	/** Links to export.*/
	private Vector				vtLinks = new Vector(51);

	/** Tripple link data.*/
	private Vector				vtTrippleLinks = new Vector(51);

	/** Tripple node data.*/
	private Vector				vtTrippleNodes = new Vector(51);

	/** Tripple list data.*/
	private Vector				vtTrippleLists = new Vector(51);

	/** Used when processing views to export.*/
	private Hashtable			htViews = new Hashtable(51);

	/** Used when processing originating nodes to export.*/
	private Hashtable			htFromLinks = new Hashtable(51);

	/** Used when processing destination nodes to export.*/
	private Hashtable			htToLinks = new Hashtable(51);

	/** Used when processing nodes to export.*/
	private	Hashtable			htNodesCheck = new Hashtable(51);

	/** Used when processing view to export.*/
	private Hashtable			htViewsCheck = new Hashtable(51);

	/** USed when processing depth checking.*/
	private Hashtable			htCheckDepth = new Hashtable(51);

	/** Holds a list of RDF link types.*/
	private Hashtable			htRDFCheckLinks = new Hashtable(51);

	/** The file to export to.*/
	private String				fileName = null;

	/** The directory to export to.*/
	private String				directory = null;

	/** The current view to export.*/
	private View				currentView = null;

	/** The dialog that holds the progress bar.*/
	private UIProgressDialog	oProgressDialog = null;

	/** The progress bar.*/
	private JProgressBar		oProgressBar = null;

	/** The thread that runs the progress bar.*/
	private ProgressThread		oThread = null;

	/** Has the export been cancelled.*/
	private boolean XMLExportCancelled = false;

	/** The counter used by the progres bar.*/
	private int					nCount = 0;

	/** The thread that runs the export process.*/
	private Thread exportThread = null;


	/**
	 * Initializes and draw the dialog
	 */
	public UIExportRDFDialog(JFrame parent) {

		super(parent, true);

	  	oParent = parent;

		// SET UP CHECK LIST FOR ALLOWED LINK LABELS
		htRDFCheckLinks.put("isAbout", "isAbout"); //$NON-NLS-1$ //$NON-NLS-2$
		htRDFCheckLinks.put("uses-applies-isEnabledBy", "uses-applies-isEnabledBy"); //$NON-NLS-1$ //$NON-NLS-2$
		htRDFCheckLinks.put("improvesOn", "improvesOn"); //$NON-NLS-1$ //$NON-NLS-2$
		htRDFCheckLinks.put("impairs", "impairs"); //$NON-NLS-1$ //$NON-NLS-2$
		htRDFCheckLinks.put("otherLink", "otherLink"); //$NON-NLS-1$ //$NON-NLS-2$
		htRDFCheckLinks.put("addresses", "addresses"); //$NON-NLS-1$ //$NON-NLS-2$
		htRDFCheckLinks.put("solves", "solves"); //$NON-NLS-1$ //$NON-NLS-2$
		htRDFCheckLinks.put("proves", "proves"); //$NON-NLS-1$ //$NON-NLS-2$
		htRDFCheckLinks.put("refutes", "refutes"); //$NON-NLS-1$ //$NON-NLS-2$
		htRDFCheckLinks.put("isEvidenceFor", "isEvidenceFor"); //$NON-NLS-1$ //$NON-NLS-2$
		htRDFCheckLinks.put("isEvidenceAgainst", "isEvidenceAgainst"); //$NON-NLS-1$ //$NON-NLS-2$
		htRDFCheckLinks.put("agreesWith", "agreesWith"); //$NON-NLS-1$ //$NON-NLS-2$
		htRDFCheckLinks.put("disagreesWith", "disagreesWith"); //$NON-NLS-1$ //$NON-NLS-2$
		htRDFCheckLinks.put("isConsistentWith", "isConsistentWith"); //$NON-NLS-1$ //$NON-NLS-2$
		htRDFCheckLinks.put("isInconsistentWith", "isInconsistentWith"); //$NON-NLS-1$ //$NON-NLS-2$
		htRDFCheckLinks.put("partOf", "partOf"); //$NON-NLS-1$ //$NON-NLS-2$
		htRDFCheckLinks.put("exampleOf", "exampleOf"); //$NON-NLS-1$ //$NON-NLS-2$
		htRDFCheckLinks.put("subclassOf", "subclassOf"); //$NON-NLS-1$ //$NON-NLS-2$
		htRDFCheckLinks.put("notPartOf", "notPartOf"); //$NON-NLS-1$ //$NON-NLS-2$
		htRDFCheckLinks.put("notExampleOf", "notExampleOf"); //$NON-NLS-1$ //$NON-NLS-2$
		htRDFCheckLinks.put("notSubclassOf", "notSubclassOf"); //$NON-NLS-1$ //$NON-NLS-2$
		htRDFCheckLinks.put("isIdenticalTo", "isIdenticalTo"); //$NON-NLS-1$ //$NON-NLS-2$
		htRDFCheckLinks.put("isSimilarTo", "isSimilarTo"); //$NON-NLS-1$ //$NON-NLS-2$
		htRDFCheckLinks.put("isDifferentTo", "isDifferentTo"); //$NON-NLS-1$ //$NON-NLS-2$
		htRDFCheckLinks.put("isTheOppositeOf", "isTheOppositeOf"); //$NON-NLS-1$ //$NON-NLS-2$
		htRDFCheckLinks.put("sharesIssuesWith", "sharesIssuesWith"); //$NON-NLS-1$ //$NON-NLS-2$
		htRDFCheckLinks.put("hasNothingToDoWith", "hasNothingToDoWith"); //$NON-NLS-1$ //$NON-NLS-2$
		htRDFCheckLinks.put("isAnalogousTo", "isAnalogousTo"); //$NON-NLS-1$ //$NON-NLS-2$
		htRDFCheckLinks.put("isNotAnalogousTo", "isNotAnalogousTo"); //$NON-NLS-1$ //$NON-NLS-2$
		htRDFCheckLinks.put("causes", "causes"); //$NON-NLS-1$ //$NON-NLS-2$
		htRDFCheckLinks.put("isCapableOfCausing", "isCapableOfCausing"); //$NON-NLS-1$ //$NON-NLS-2$
		htRDFCheckLinks.put("isPrerequisiteFor", "isPrerequisiteFor"); //$NON-NLS-1$ //$NON-NLS-2$
		htRDFCheckLinks.put("isUnlikelyToAffect", "isUnlikelyToAffect"); //$NON-NLS-1$ //$NON-NLS-2$
		htRDFCheckLinks.put("prevents", "prevents"); //$NON-NLS-1$ //$NON-NLS-2$

	  	setTitle("Export To RDF"); //$NON-NLS-1$

		Container oContentPane = getContentPane();

		GridBagLayout gb = new GridBagLayout();
		GridBagConstraints gc = new GridBagConstraints();
		oContentPane.setLayout(gb);
		gc.insets = new Insets(5,5,5,5);
		gc.anchor = GridBagConstraints.WEST;

		rbCurrentDepth = new JRadioButton(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIExportRDFDialog.mapDepth")); //$NON-NLS-1$
		rbCurrentDepth.setSelected(true);
		rbCurrentDepth.addActionListener(this);
		gc.gridy = 1;
		gb.setConstraints(rbCurrentDepth, gc);
		oContentPane.add(rbCurrentDepth);

		rbAllDepths = new JRadioButton(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIExportRDFDialog.withLists")); //$NON-NLS-1$
		rbAllDepths.setSelected(false);
		rbAllDepths.addActionListener(this);
		gc.gridy = 2;
		gb.setConstraints(rbAllDepths, gc);
		oContentPane.add(rbAllDepths);

		ButtonGroup rgGroup = new ButtonGroup();
		rgGroup.add(rbAllDepths);
		rgGroup.add(rbCurrentDepth);

		// Add spacer label
		JLabel spacer = new JLabel(" "); //$NON-NLS-1$
		gc.gridy = 3;
		gb.setConstraints(spacer, gc);
		oContentPane.add(spacer);

		// Add export button
		pbExport = new UIButton(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIExportRDFDialog.exportButton")); //$NON-NLS-1$
		pbExport.setMnemonic(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIExportRDFDialog.exportButtonMnemonic").charAt(0)); //$NON-NLS-1$
		pbExport.addActionListener(this);
		gc.gridy = 4;
		gc.anchor = GridBagConstraints.WEST;
		gb.setConstraints(pbExport, gc);
		oContentPane.add(pbExport);

		// Add close button
		pbClose = new UIButton(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIExportRDFDialog.closeButton")); //$NON-NLS-1$
		pbClose.setMnemonic(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIExportRDFDialog.closeButtonMnemonic").charAt(0)); //$NON-NLS-1$
		pbClose.addActionListener(this);
		gc.gridy = 4;
		gb.setConstraints(pbClose, gc);
		oContentPane.add(pbClose);

  		oProgressBar = new JProgressBar();
  		oProgressBar.setMinimum(0);
		oProgressBar.setMaximum(100);

		pack();
		setResizable(false);
		return;
	}

	/**
	 * This thread runs the progress bar.
	 */
	private class ProgressThread extends Thread {

		public ProgressThread() {
	  		oProgressDialog = new UIProgressDialog(ProjectCompendium.APP,LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIExportRDFDialog.exportProgress"), LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIExportRDFDialog.exportCompleted")); //$NON-NLS-1$ //$NON-NLS-2$
	  		oProgressDialog.showDialog(oProgressBar);
	  		oProgressDialog.setModal(true);
		}

		public void run() {
	  		oProgressDialog.setVisible(true);
		}
	}

	/**
	 * Check if the user has cancelled the export.
	 */
	private boolean checkProgress() {

	  	if (!XMLExportCancelled && oProgressDialog.isCancelled()) {

			int result = JOptionPane.showConfirmDialog(oProgressDialog,
							LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIExportRDFDialog.cancelOption"), //$NON-NLS-1$
							LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIExportRDFDialog.cancelExport"), //$NON-NLS-1$
							JOptionPane.YES_NO_OPTION);

			if (result == JOptionPane.YES_OPTION) {
				XMLExportCancelled = true;
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
	 * Handle action events coming from the buttons.
	 * @param evt, the associated ActionEvent object.
	 */
	public void actionPerformed(ActionEvent evt) {

		Object source = evt.getSource();
		if (source instanceof JButton) {
			if (source == pbExport) {

				exportThread = new Thread("UIExportRDFDialog: Export") { //$NON-NLS-1$
					public void run() {
						onExport();
					}
				};
				exportThread.start();

			} else
			if (source == pbClose) {
				onCancel();
			}
		}
	}

	/**
	 * Set the current view.
	 * @param view com.compendium.core.datamodel.View, the current view.
	 */
	public void setCurrentView(View view) {
		currentView = view;
	}

	/**
	 * Handle the export action.
	 */
	private void onExport() {

		UIFileFilter filter = new UIFileFilter(new String[] {"rdf"}, "RDF Files"); //$NON-NLS-1$ //$NON-NLS-2$
		UIFileChooser fileDialog = new UIFileChooser();
		fileDialog.setDialogTitle(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIExportRDFDialog.chooseFile")); //$NON-NLS-1$
		fileDialog.setFileFilter(filter);
		fileDialog.setApproveButtonText(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIExportRDFDialog.saveButton")); //$NON-NLS-1$
		fileDialog.setRequiredExtension(".rdf"); //$NON-NLS-1$

		// FIX FOR MAC - NEEDS '/' ON END TO DENOTE A FOLDER
		File file = new File(exportDirectory+ProjectCompendium.sFS);
		if (file.exists()) {
			fileDialog.setCurrentDirectory(file);
		}

		UIUtilities.centerComponent(fileDialog, ProjectCompendium.APP);
		int retval = fileDialog.showSaveDialog(ProjectCompendium.APP);
		if (retval == JFileChooser.APPROVE_OPTION) {
			if ((fileDialog.getSelectedFile()) != null) {
				this.requestFocus();

				setCursor(new Cursor(Cursor.WAIT_CURSOR));

            	fileName = fileDialog.getSelectedFile().getName();
				File fileDir = fileDialog.getCurrentDirectory();
				exportDirectory = fileDir.getPath();

				if (fileName != null) {
					setVisible(false);

					oThread = new ProgressThread();
					oThread.start();

					convertToXML();

					onCancel();
				}

				setCursor(Cursor.getDefaultCursor());
			}
		}
	}

	/**
	 * process the current view to export.
	 */
	private void convertToXML() {

		ProjectCompendium.APP.setWaitCursor();

		StringBuffer root = new StringBuffer(1000);
		root.append("<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:sch=\"http://kmi.open.ac.uk/people/victoria/Scholonto1#\">\n\n"); //$NON-NLS-1$

		htNodesCheck.clear();
		htFromLinks.clear();
		htToLinks.clear();
		vtNodes.removeAllElements();
		vtTrippleNodes.removeAllElements();
		vtTrippleLinks.removeAllElements();
		vtTrippleLists.removeAllElements();

		try {
			if (currentView != null) {
				if (!currentView.isMembersInitialized())
					currentView.initializeMembers();

				int count = 0;
				if (rbCurrentDepth.isSelected())
					count = currentView.getNumberOfNodes();
				else {
					nCount += 2;
					oProgressBar.setValue(nCount);
					oProgressDialog.setStatus(nCount);
					count = countDepth(currentView);
				}
		  		oProgressBar.setMaximum(count+12);

				printNode(currentView, currentView.getParentNode());
			}
		}
		catch(Exception ex) {
			ProjectCompendium.APP.displayError("Exception (UIExportRDFDialog.convertToXML) :" + ex.getMessage()); //$NON-NLS-1$
		}

		if (XMLExportCancelled || checkProgress()) { root = null; return; }
		nCount += 3;
		oProgressBar.setValue(nCount);
		oProgressDialog.setStatus(nCount);

		root.append( processDataToXML() );

		if (XMLExportCancelled || checkProgress()) { root = null; return; }
		nCount +=3;
		oProgressBar.setValue(nCount);
		oProgressDialog.setStatus(nCount);

		root.append("</rdf:RDF>"); //$NON-NLS-1$

		// SAVE TO FILE
		try {
			FileWriter fileWriter = new FileWriter(directory+fileName);
			fileWriter.write(root.toString());
			fileWriter.close();
			nCount += 3;
			oProgressBar.setValue(nCount);
			oProgressDialog.setStatus(nCount);

		} catch (IOException e) {
			ProjectCompendium.APP.displayError("Exception:" + e.getMessage()); //$NON-NLS-1$
		}

		oProgressDialog.setVisible(false);
		oProgressDialog.dispose();

		if (fileName != null) {
			ProjectCompendium.APP.displayMessage(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIExportRDFDialog.finishExport") + directory + fileName, LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIExportRDFDialog.finishExportTitle")); //$NON-NLS-1$ //$NON-NLS-2$
		}
		ProjectCompendium.APP.setStatus(""); //$NON-NLS-1$
	}

	/**
	 * Calculate the depth for the current view.
	 * @param view com.compendium.core.datamodel.View, the view to count the export depth for.
	 */
	public int countDepth(View view) {
		try {
			if (!view.isMembersInitialized()) {
				view.initializeMembers();
			}
		}
		catch(Exception ex) {
			System.out.println("Error: (UIExportRDFDialog.countDepth) \n\n"+ex.getMessage()); //$NON-NLS-1$
		}

		int count = view.getNumberOfNodes();

		Enumeration nodePositions = view.getPositions();
		for(Enumeration en = nodePositions; en.hasMoreElements();) {

			NodeSummary node = (NodeSummary)((NodePosition)en.nextElement()).getNode();

			if ( node.getType() == ICoreConstants.LISTVIEW ) {

				if (!htCheckDepth.containsKey((Object)node.getId())) {
					htCheckDepth.put(node.getId(), node);
					count += countDepth((View) node);
				}
			}
		}
		return count;
	}

	/**
	 * Extract the data to export for the given node
	 * @param nodeToPrint com.compendum.core.datamodel.NodeSummary, the node to process.
	 * @param parentNode com.compendum.core.datamodel.NodeSummary, the parent of the node to process.
	 */
	private void printNode(NodeSummary nodeToPrint, NodeSummary parentNode) {

		if (XMLExportCancelled || checkProgress()) { return; }
		nCount++;
		oProgressBar.setValue(nCount);
		oProgressDialog.setStatus(nCount);

		StringBuffer nodeString = new StringBuffer(200);

		int nType = nodeToPrint.getType();
		if (View.isViewType(nType)) {

			// HAVE I ALREADY ADDED THIS VIEW?
			if (!htNodesCheck.containsKey((Object)nodeToPrint.getId())) {

				if (!nodeToPrint.getId().equals(currentView.getId()))
					processNodeSummary(nodeToPrint);

				// IF YOUR ABOUT TO PROCESS CHILD MAPS AND USER HAS SAID NO, DON'T
				if (!nodeToPrint.getId().equals(currentView.getId()) && rbCurrentDepth.isSelected()) {
					return;
				}

				View view = (View)nodeToPrint;

				try {
					if (!view.isMembersInitialized()) {
						view.initializeMembers();
					}
				}
				catch(Exception ex) {
					System.out.println("Error: (UIExportRDFDialog.countDepth) \n\n"+ex.getMessage()); //$NON-NLS-1$
				}

				Enumeration links = view.getLinks();
				processLinks( links, view );

				Enumeration nodePositions = view.getPositions();
				for(Enumeration en = nodePositions; en.hasMoreElements();) {

					NodePosition nodePos = (NodePosition)en.nextElement();

					NodeSummary nodeSummary = nodePos.getNode();

					View nodeView = nodePos.getView();
					String viewid = nodeView.getId();
					String nodeid = nodeSummary.getId();

					Vector viewData = new Vector(2);
					viewData.add((Object) viewid);
					viewData.add((Object) nodeid);

					vtViews.add((Object) viewData);

					printNode(nodeSummary, nodeView);
				}
			}
		}
		else if ( nType != ICoreConstants.TRASHBIN) {
			if ( nType == ICoreConstants.ISSUE || nType == ICoreConstants.POSITION ) {
				if (!htNodesCheck.containsKey((Object)nodeToPrint.getId())) {
					processNodeSummary(nodeToPrint);
				}
			}
		}
	}

	/**
	 * Process the given node for export.
	 * @param nodeSummary com.compendum.core.datamodel.NodeSummary, the node to process.
	 */
	private void processNodeSummary(NodeSummary nodeSummary) {

		// PROCESS LABEL AND DETAILS AND SOURCE THROUGH CHECK XML CHARS

		Vector nodeData = new Vector(20);

		String id = nodeSummary.getId();
		int type = nodeSummary.getType();

		String label = nodeSummary.getLabel();
		label = CoreUtilities.cleanXMLText(label);

		String detail = nodeSummary.getDetail();
		if (detail.equals(ICoreConstants.NODETAIL_STRING) )
			detail = ""; //$NON-NLS-1$
		detail = CoreUtilities.cleanXMLText(detail);

		nodeData.add((Object) id );
		nodeData.add((Object) new Integer(type) );
		nodeData.add((Object) label );
		nodeData.add((Object) detail );

		if ( !htNodesCheck.containsKey((Object) id)) {
			htNodesCheck.put((Object) id, (Object) id);
			vtNodes.add((Object) nodeData);
		}
	}

	/**
	 * Process the array of links in the given view.
	 * @param link, the list of links to process.
	 * @param view com.compendum.core.datamodel.View, the view the links are in.
	 */
	private void processLinks(Enumeration links, View view) {
		String linkViewID = view.getId();

		for(Enumeration en = links; en.hasMoreElements();) {

			Link link = (Link)en.nextElement();
			Vector linkData = new Vector(10);

			String id = link.getId();
			String linkFromID = (link.getFrom()).getId();
			String linkToID = (link.getTo()).getId();

			linkData.add((Object) id );
			linkData.add((Object) linkFromID );
			linkData.add((Object) linkToID );
			linkData.add((Object) linkViewID );

			if ( !htFromLinks.containsKey( id ) ) {
				htFromLinks.put((Object)id, (Object)id);
				vtLinks.add((Object) linkData);
			}
		}
	}


	/**
	 * Process the current view to export.
	 */
	public String processDataToXML() {
		StringBuffer xml = new StringBuffer(1000);

		calculateTripples();
		xml.append( processTrippleNodesToXML() );
		xml.append( processTrippleLinksToXML() );
		xml.append( processTrippleListsToXML() );

		return xml.toString();
	}

	/**
	 * Calculate nodes to export.
	 */
	private void calculateTripples() {

		int counti = vtNodes.size();
		Vector nextNode = null;

		for (int i = 0; i < counti; i++) {
			nextNode = (Vector)vtNodes.elementAt(i);
			String nodeid = (String)nextNode.elementAt(0);
			int sType = ((Integer)nextNode.elementAt(1)).intValue();
			if ( sType == ICoreConstants.ISSUE ) {
				Vector myLinks = new Vector();
				String fromLink = ""; //$NON-NLS-1$
				String toLink = ""; //$NON-NLS-1$
				Vector nextLink = null;

				String fromid = ""; //$NON-NLS-1$
				String toid = ""; //$NON-NLS-1$
				// DO I NEED TO THINK ABOUT VIEW ???
				for (int j=0; j<vtLinks.size(); j++) {
					nextLink = (Vector)vtLinks.elementAt(j);
					fromid = (String)nextLink.elementAt(1);
					toid = (String)nextLink.elementAt(2);
					if (fromid.equals(nodeid)) {
						toLink = toid;
						myLinks.add(nextLink);
					}
					if (toid.equals(nodeid))  {
						fromLink = fromid;
						myLinks.add(nextLink);
					}
				}

				if (myLinks.size() == 2 && !fromLink.equals("") && !toLink.equals("") ) { //$NON-NLS-1$ //$NON-NLS-2$
					String linkLabel = (String)nextNode.elementAt(2);
					if (!htRDFCheckLinks.containsKey(linkLabel))
						linkLabel = "otherLink"; //$NON-NLS-1$

					Vector trippleLink = new Vector(3);
					trippleLink.add(fromLink);
					trippleLink.add(linkLabel);
					trippleLink.add(toLink);
					vtTrippleLinks.add(trippleLink);
				}
			}
			else if ( sType == ICoreConstants.LISTVIEW ) {
				vtTrippleLists.add(nextNode);
			}
			else {
				vtTrippleNodes.add(nextNode);
			}
		}
	}

	/**
	 * Process nodes to RDF.
	 */
	private String processTrippleNodesToXML() {

		StringBuffer xmlNodes = new StringBuffer(500);

		Vector nextNode = null;
		int counti = vtTrippleNodes.size();

		for (int i = 0; i < counti; i++) {

			nextNode = (Vector)vtTrippleNodes.elementAt(i);

			xmlNodes.append("<sch:SchConceptID rdf:about=\""+(String)nextNode.elementAt(0)+"\">\n"); //$NON-NLS-1$ //$NON-NLS-2$

			String label = (String)nextNode.elementAt(2);
			String detail = (String)nextNode.elementAt(3);

			if (label != null && !label.equals("")) //$NON-NLS-1$
				xmlNodes.append("\t<sch:conceptLabel>\""+label+"\"</sch:conceptLabel>\n"); //$NON-NLS-1$ //$NON-NLS-2$

			if (detail != null && !detail.equals("")) //$NON-NLS-1$
				xmlNodes.append("\t<sch:conceptDescription>\""+detail+"\"</sch:conceptDescription>\n"); //$NON-NLS-1$ //$NON-NLS-2$

			xmlNodes.append("</sch:SchConceptID>\n\n"); //$NON-NLS-1$
		}

		return xmlNodes.toString();
	}

	/**
	 * Process data to RDF tripples.
	 */
	private String processTrippleLinksToXML() {

		StringBuffer xmlLinks = new StringBuffer(500);

		Vector nextLink = null;
		int count = vtTrippleLinks.size();

		for (int i = 0; i < count; i++) {
			nextLink = (Vector)vtTrippleLinks.elementAt(i);

			xmlLinks.append("<sch:SchConceptID rdf:about=\""+(String)nextLink.elementAt(0)+"\">\n"); //$NON-NLS-1$ //$NON-NLS-2$
			xmlLinks.append("\t<sch:"+(String)nextLink.elementAt(1)+" rdf:resource=\""+(String)nextLink.elementAt(2)+"\"/>\n"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			xmlLinks.append("</sch:SchConceptID>\n\n"); //$NON-NLS-1$
		}

		return xmlLinks.toString();
	}

	/**
	 * Process node lists to RDF.
	 */
	private String processTrippleListsToXML() {

		StringBuffer xmlNodes = new StringBuffer(500);

		Vector nextNode = null;
		int counti = vtTrippleLists.size();

		for (int i = 0; i < counti; i++) {

			nextNode = (Vector)vtTrippleLists.elementAt(i);
			String nodeid = (String)nextNode.elementAt(0);

			xmlNodes.append("<sch:SchSetID rdf:about=\""+nodeid+"\">\n"); //$NON-NLS-1$ //$NON-NLS-2$

			String label = (String)nextNode.elementAt(2);
			String detail = (String)nextNode.elementAt(3);

			if (label != null && !label.equals("")) //$NON-NLS-1$
				xmlNodes.append("\t<sch:conceptLabel>\""+label+"\"</sch:conceptLabel>\n"); //$NON-NLS-1$ //$NON-NLS-2$
			if (detail != null && !detail.equals("")) //$NON-NLS-1$
				xmlNodes.append("\t<sch:conceptDescription>\""+detail+"\"</sch:conceptDescription>\n"); //$NON-NLS-1$ //$NON-NLS-2$

			Vector nextView=null;
			int countj = vtViews.size();
			for (int j = 0; j < countj; j++) {
				nextView = (Vector)vtViews.elementAt(j);
				String viewid = (String)nextView.elementAt(0);

				if (viewid.equals(nodeid)) {
					xmlNodes.append("\t<sch:setElement rdf:resource=\""+(String)nextView.elementAt(1)+"\" />\n"); //$NON-NLS-1$ //$NON-NLS-2$
				}
			}

			xmlNodes.append("</sch:SchSetID>\n\n"); //$NON-NLS-1$
		}

		return xmlNodes.toString();
	}

	/**
	 * Handle the enter key action. Override superclass to do nothing.
	 */
	public void onEnter() {}

	/**
	 * Handle the close action. Clear all the class variables.
	 */
	public void onCancel() {

		htNodesCheck.clear();
		htFromLinks.clear();
		htToLinks.clear();
		vtNodes.removeAllElements();
		vtLinks.removeAllElements();
		vtTrippleLinks.removeAllElements();
		vtTrippleNodes.removeAllElements();
		vtTrippleLists.removeAllElements();

		htNodesCheck = null;
		htFromLinks = null;
		htToLinks = null;
		vtNodes = null;
		vtLinks = null;
		vtTrippleLinks = null;
		vtTrippleNodes = null;
		vtTrippleLists = null;

		ProjectCompendium.APP.setDefaultCursor();

		setVisible(false);
		dispose();
	}
}
