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

package com.compendium.ui.stencils;

import java.io.*;
import java.util.*;

import javax.help.*;
import javax.swing.*;

import org.w3c.dom.*;

import com.compendium.core.*;
import com.compendium.*;

import com.compendium.ui.*;

import com.compendium.io.xml.*;

import com.compendium.core.datamodel.*;


/**
 * This class manages all the stencils
 *
 * @author	Michelle Bachler
 * @version	1.0
 */
public class UIStencilManager implements IUIConstants, ICoreConstants {

	/**A reference to the system file path separator*/
	public final static String	sFS					= System.getProperty("file.separator"); //$NON-NLS-1$

	/**A reference to the node image directory*/
	public final static String	sPATH 				= "System"+sFS+"resources"+sFS+"Stencils"+sFS; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	
	/** A list of all stencils.*/
	private Hashtable htStencils 					= new Hashtable(10);

	/** A list of stencil currently displayed.*/
	private Hashtable htDisplayedStencils 			= new Hashtable(10);

	/** The parent frame for this class.*/
	private ProjectCompendiumFrame	oParent			= null;

	/** The HelpSet to use for toolbar help.*/
	private HelpSet 				mainHS 			= null;

	/** The HelpBroker to use for toolbar help.*/
	private HelpBroker 				mainHB			= null;

	/** Reference to the model for the current database.*/
	private IModel 					oModel			= null;

	/** The tabbedpane to hold the open stencil panels.*/
	private JTabbedPane			oTabbedPane				= null;
	
	/**
	 * Constructor. Create a new instance of UIStencilManager, with the given proerties.
	 * @param parent com.compendium.ui.ProjectCompendiumFrame, the parent frame for the application.
	 * @param hs the helpset to use.
	 * @param hb the HelpBroker to use.
	 */
	public UIStencilManager(ProjectCompendiumFrame parent, HelpSet hs, HelpBroker hb) {

		oParent = parent;
		mainHS = hs;
		mainHB = hb;

		oTabbedPane = new JTabbedPane();
		oTabbedPane.setTabPlacement(JTabbedPane.TOP);
		
	}

	/**
	 * Return the tabbed pane that holds the stencil set panels.
	 */
	public JTabbedPane getTabbedPane() {
		return oTabbedPane;
	}

	/**
	 * Update the look and feel of all the htStencils
	 */
	public void updateLAF() {
		SwingUtilities.updateComponentTreeUI(oTabbedPane);
		for(Enumeration e = htStencils.elements(); e.hasMoreElements();) {
			UIStencilSet oStencilSet = (UIStencilSet)e.nextElement();
			SwingUtilities.updateComponentTreeUI(oStencilSet);
		}
	}

	/**
	 * Return the stencil set with the given name.
	 * @param UIStencilSet The stencil set with the given name.
	 */
	public UIStencilSet getStencilSet(String sName) {

		UIStencilSet oStencilSet = null;

		for(Enumeration e = htStencils.keys();e.hasMoreElements();) {
	       	String name = (String)e.nextElement();
			if (sName.equals(name))
				oStencilSet = (UIStencilSet)htStencils.get(name);
		}
		return oStencilSet;
	}

	/**
	 * Return a list of all the stencil names currently existing.
	 * @param Vector, list of all the stencil names.
	 */
	public Vector getStencilNames() {

		Vector names = new Vector(10);

		for(Enumeration e = htStencils.keys();e.hasMoreElements();) {
	       	String name = (String)e.nextElement();
			names.addElement(name);
		}
		return names;
	}

	/**
	 * Check the passed stencil set name to see if it already exists.
	 * @param sName the name to check.
	 * @return boolean true if the name has already been used, else false;
	 */
	public boolean checkName(String sName) {
		boolean exists =  false;
		for(Enumeration e = htStencils.keys();e.hasMoreElements();) {
	       	String name = (String)e.nextElement();
			if (sName.equals(name))
				return true;
		}
		return exists;
	}

	/**
	 * Add a stencil to this manager's list.
	 * @param oStencil, the stencil to add.
	 */
	public void addStencilSet(String sOldName, UIStencilSet oStencil) {

		if (!htStencils.containsKey(sOldName)) {
			htStencils.put(oStencil.getName(), oStencil);
		}
		else {
			htStencils.remove(sOldName);
			htStencils.put(oStencil.getName(), oStencil);

			if (htDisplayedStencils.containsKey(sOldName)) {
				UIStencilSet oldSet = (UIStencilSet)htDisplayedStencils.get(sOldName);
				htDisplayedStencils.remove(sOldName);
				htDisplayedStencils.put(oStencil.getName(), oStencil);

				// IF VISIBLE, UPDATE
				int index= oTabbedPane.indexOfComponent(oldSet);
				if (index > -1) {
					oTabbedPane.removeTabAt(index);
					if (!oStencil.isDrawn())
						 oStencil.draw();
					oTabbedPane.insertTab(oStencil.getTabName(), null, oStencil, oStencil.getName(), index);
					oStencil.refreshStencilSet();
					oTabbedPane.validate();
					oTabbedPane.repaint();
				}
			}
		}
	}

	/**
	 * Remove a stencil from this manager's list.
	 * @param oStencil the stencil to remove.
	 * @return boolean true if added, else false.
	 */
	public boolean removeStencilSet(UIStencilSet oStencil) {
		boolean removed = false;
		String sName = oStencil.getName();
		if (htStencils.containsKey(sName)) {
			htStencils.remove(sName);
			closeStencilSet(sName, oStencil);
			removed = true;
		}
		return removed;
	}

	/**
	 * Open the stencil with the given name to those currently displayed.
	 * @param sName, the name of the stencil set.
	 * @return boolean, true if opened, else false.
	 */
	public boolean openStencilSet(String sName) {
		boolean opened = false;
		
		if (htStencils.containsKey(sName) && !htDisplayedStencils.containsKey(sName)) {
			UIStencilSet oStencil = (UIStencilSet)htStencils.get(sName);

			htDisplayedStencils.put(sName, oStencil);
			if (!oStencil.isDrawn()) {
				oStencil.draw();
			}

			oTabbedPane.addTab(oStencil.getTabName(), null, oStencil, oStencil.getName());
			oTabbedPane.setSelectedComponent(oStencil);
			opened = true;
		} 
		
		if (htDisplayedStencils.size() == 1) {
			ProjectCompendium.APP.oTabbedPane.addTab(LanguageProperties.getString(LanguageProperties.STENCILS_BUNDLE, "UIStencilManager.stencils"), oTabbedPane); //$NON-NLS-1$
			ProjectCompendium.APP.oTabbedPane.setSelectedComponent(oTabbedPane); 
			ProjectCompendium.APP.oSplitter.resetToPreferredSizes();						
		}
		
		return opened;
	}

	/**
	 * close the UIStencil panel from those currently displayed.
	 * @param sName, the name of the stencil set.
	 * @param oStencil the stencil set.
	 * @return boolean true if closed, else false.
	 */
	public boolean closeStencilSet(String sName, Object set) {
		boolean closed = false;
		if (htDisplayedStencils.containsKey(sName)) {
			oTabbedPane.remove((UIStencilSet)htDisplayedStencils.get(sName));
			htDisplayedStencils.remove(sName);
			closed = true;
		}
		
		if (htDisplayedStencils.size() == 0) {
			ProjectCompendium.APP.oTabbedPane.remove(oTabbedPane);
			ProjectCompendium.APP.oSplitter.resetToPreferredSizes();									
		}
		return closed;
	}

	/**
	 * Return the stencil item associated with the given shortcut number.
	 * @param nShortcut the shortcut number to check for.
	 * @return DraggableStencilIcon the associated item, or null if not found.
	 */
	public DraggableStencilIcon getItemForShortcut(int nShortcut) {

		DraggableStencilIcon oIcon = null;
		UIStencilSet set = (UIStencilSet)oTabbedPane.getSelectedComponent();
		if (set != null) {
			oIcon = set.getItemForShortcut(nShortcut);
		}
		return oIcon;
	}


	/**
	 * Load the stencil data from the XML files where it has been save.
	 */
	public void loadStencils() {
		oModel = ProjectCompendium.APP.getModel();

		htStencils.clear();
		
		String errorMessage = "";
		
		try {
			File main = new File("System"+sFS+"resources"+sFS+"Stencils"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			File oStencils[] = main.listFiles();
			String stencilName = "";
			File nextStencil = null;
			String nextMessage = "";
			for (int i=0; i< oStencils.length; i++) {
				stencilName = "";
				nextStencil = null;
				try {
					nextStencil = oStencils[i];
					if (nextStencil.isDirectory()) {
						String folderName = nextStencil.getName();
	
						File oChildren[] = nextStencil.listFiles();
	
						for (int j=0; j< oChildren.length; j++) {
							File next = oChildren[j];
	
							stencilName = next.getName();
							if (stencilName.endsWith(".xml")) { //$NON-NLS-1$
									loadFile(next.getAbsolutePath(), stencilName, folderName);
							}
						}
					}
				} catch (Exception e) {					
					System.out.println("Exception: Stencil - "+nextStencil.getName()+" could not be loaded due to: "+e.getLocalizedMessage());
					nextMessage = "";
					if (!stencilName.equals("")) {
						nextMessage = stencilName.substring(0, stencilName.length()-4);
					} else if (nextStencil != null) {
						nextMessage = nextStencil.getName();
					}
					errorMessage += nextMessage+"\n";
				}
			}
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
		
		if (!errorMessage.equals("")) {
			errorMessage = LanguageProperties.getString(LanguageProperties.STENCILS_BUNDLE, "UIStencilManager.loadError1")+"\n"+LanguageProperties.getString(LanguageProperties.STENCILS_BUNDLE, "UIStencilManager.loadError2")+"\n\n"+errorMessage;//$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			ProjectCompendium.APP.displayError(errorMessage);
		}
	}

	/**
	 * Load the stencil data for the given filename.
	 * @param sFullPath the path of the file to load.
	 * @param sFileName the filename for this stencil.
	 * @param sFolderName the folder name for this stencil.
	 */
	public void loadFile(String sFullPath, String sFileName, String sFolderName) throws Exception {
		XMLReader reader = new XMLReader();

		Document document = reader.read(sFullPath, true);
		if (document == null)
			System.out.println(LanguageProperties.getString(LanguageProperties.STENCILS_BUNDLE, "UIStencilManager.notFoundStencilData")+sFolderName); //$NON-NLS-1$

		Node data = document.getDocumentElement();
		if (data == null)
			throw new Exception(LanguageProperties.getString(LanguageProperties.STENCILS_BUNDLE, "UIStencilManager.stencil")+sFileName+" "+LanguageProperties.getString(LanguageProperties.STENCILS_BUNDLE, "UIStencilManager.notLoaded")); //$NON-NLS-1$ //$NON-NLS-2$

		storeStencil(data, sFileName, sFolderName);
	}

	/**
	 * Store stendil data against name.
	 * @param data the Node XML data object for the stencil to create.
	 * @param sFileName the filename for this stencil.
	 * @param sFolderName the folder name for this stencil.
	 */
	private void storeStencil(Node node, String sFileName, String sFolderName) {
		NamedNodeMap attrs = node.getAttributes();
		Attr name = (Attr)attrs.getNamedItem("name"); //$NON-NLS-1$
		String sName = new String(name.getValue());

		Attr oTabName = (Attr)attrs.getNamedItem("tabname"); //$NON-NLS-1$
		String sTabName = ""; //$NON-NLS-1$
		if (oTabName != null)
			sTabName = new String(oTabName.getValue());

		if (sTabName.equals("")) //$NON-NLS-1$
			sTabName = sName;

		UIStencilSet oSet = createStencil(node, sName, sTabName, sFileName, sFolderName);
		addStencilSet(sName, oSet);
	}

	/**
	 * Create a stencil panel from the given data.
	 * @param node the Node XML data object for the stencil to create
	 * @param sName the name of the stencil.
	 * @param sFileName the filename of the stencil.
	 */
	private UIStencilSet createStencil(Node node, String sName, String sTabName, String sFileName, String sFolderName) {

		UIStencilSet oStencil = null;

		if (node.hasChildNodes()) {

			oStencil = new UIStencilSet(this, sFileName, sFolderName, sName, sTabName);
			Node items = XMLReader.getFirstChildWithTagName(node, "items"); //$NON-NLS-1$

			Vector vtItems = XMLReader.getChildrenWithTagName(items, "item"); //$NON-NLS-1$
			if (vtItems.size() > 0) {
				int count = vtItems.size();
				for (int i=0; i<count; i++) {
					Node child = (Node)vtItems.elementAt(i);
					NamedNodeMap attrs = child.getAttributes();

					Attr oLabel = (Attr)attrs.getNamedItem("label"); //$NON-NLS-1$
					String sLabel = new String(oLabel.getValue());

					Attr oTip = (Attr)attrs.getNamedItem("tooltip"); //$NON-NLS-1$
					String sTip = new String(oTip.getValue());

					Attr oImage = (Attr)attrs.getNamedItem("image"); //$NON-NLS-1$
					String sImage = new String(oImage.getValue());
					sImage = sPATH+sFolderName+sFS+UIStencilSet.sNODEIMAGEDIR+sFS+sImage;

					Attr oPaletteImage = (Attr)attrs.getNamedItem("paletteimage"); //$NON-NLS-1$
					String sPaletteImage = ""; //$NON-NLS-1$
					if (oPaletteImage != null)
						sPaletteImage = new String(oPaletteImage.getValue());

					if (!sPaletteImage.equals("")) //$NON-NLS-1$
						sPaletteImage = sPATH+sFolderName+sFS+UIStencilSet.sPALETTEIMAGEDIR+sFS+sPaletteImage;

					Attr oBackgroundImage = (Attr)attrs.getNamedItem("backgroundimage"); //$NON-NLS-1$
					String sBackgroundImage = ""; //$NON-NLS-1$
					if (oBackgroundImage != null)
						sBackgroundImage = new String(oBackgroundImage.getValue());

					if (!sBackgroundImage.equals("")) //$NON-NLS-1$
						sBackgroundImage = sPATH+sFolderName+sFS+UIStencilSet.sBACKGROUNDIMAGEDIR+sFS+sBackgroundImage;

					Attr oTemplate = (Attr)attrs.getNamedItem("template"); //$NON-NLS-1$
					String sTemplate = ""; //$NON-NLS-1$
					if (oTemplate != null)
						sTemplate = new String(oTemplate.getValue());

					if (!sTemplate.equals("")) //$NON-NLS-1$
						sTemplate = sPATH+sFolderName+sFS+UIStencilSet.sTEMPLATEDIR+sFS+sTemplate;
					
					Attr oType = (Attr)attrs.getNamedItem("type"); //$NON-NLS-1$
					int nType = new Integer(oType.getValue()).intValue();

					Attr oShortcut = (Attr)attrs.getNamedItem("shortcut"); //$NON-NLS-1$
					int nShortcut = new Integer(oShortcut.getValue()).intValue();

					Vector vtTags = loadTags(XMLReader.getFirstChildWithTagName(child, "tags")); //$NON-NLS-1$

					ImageIcon oIcon = null;
					if (sPaletteImage.equals("")) //$NON-NLS-1$
						oIcon = UIImages.thumbnailIcon(sImage);
					else
						oIcon = UIImages.thumbnailIcon(sPaletteImage);

					DraggableStencilIcon item = new DraggableStencilIcon(sImage, sPaletteImage, sBackgroundImage, sTemplate, sLabel, sTip, nType, nShortcut, vtTags, oIcon);

					oStencil.loadStencilItem(item);
				}
			}
		}

		return oStencil;
	}

	/**
	 * Extract the tags list from the given data.
	 * @param node the Node XML data object for the tags to create
	 */
	private Vector loadTags(Node node) {
		Vector vtTags = new Vector(10);

		if (node != null && node.hasChildNodes()) {

			Vector vtTagItems = XMLReader.getChildrenWithTagName(node, "tag"); //$NON-NLS-1$
			if (vtTagItems.size() > 0) {
				int count = vtTagItems.size();
				for (int i=0; i<count; i++) {
					Node child = (Node)vtTagItems.elementAt(i);
					NamedNodeMap attrs = child.getAttributes();

					String sID = ((Attr)attrs.getNamedItem("id")).getValue(); //$NON-NLS-1$
					String sName = ((Attr)attrs.getNamedItem("name")).getValue(); //$NON-NLS-1$

					String sAuthor = ((Attr)attrs.getNamedItem("author")).getValue(); //$NON-NLS-1$
					String sDescription = ((Attr)attrs.getNamedItem("description")).getValue(); //$NON-NLS-1$
					String sBehavior = ((Attr)attrs.getNamedItem("behavior")).getValue(); //$NON-NLS-1$

					long created = new Long( ((Attr)attrs.getNamedItem("created")).getValue() ).longValue(); //$NON-NLS-1$
					long lastModified = new Long( ((Attr)attrs.getNamedItem("lastModified")).getValue() ).longValue(); //$NON-NLS-1$
					Date dCreationDate = new Date(created);
					Date dModificationDate = new Date(lastModified);

					Vector code = new Vector(7);
					code.addElement(sID);
					code.addElement(sName);
					code.addElement(sAuthor);
					code.addElement(sDescription);
					code.addElement(sBehavior);
					code.addElement(dCreationDate);
					code.addElement(dModificationDate);

					vtTags.add(code);
				}
			}
		}
		return vtTags;
	}
	
	/**
	 * @return Returns the htDisplayedStencils.
	 */
	public Hashtable getHtDisplayedStencils() {
		return htDisplayedStencils;
	}
	
}
