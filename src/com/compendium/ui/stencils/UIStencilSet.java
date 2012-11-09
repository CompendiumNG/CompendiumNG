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

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;

import javax.swing.*;

import com.compendium.*;
import com.compendium.core.*;
import com.compendium.ui.*;


/**
 * This class manages a stencil set.
 *
 * @author	Michelle Bachler
 * @version	1.0
 */
public class UIStencilSet extends JPanel {

	// Need to implement this everywhere in an organized fashion.
	//** The required field for serializable objects*/
	//private final static long serialVersionUID = 1001; 
	
	/** The node image directory*/
	public final static String	sNODEIMAGEDIR 	= "nodeimages"; //$NON-NLS-1$

	/** The palette image directory*/
	public final static String	sPALETTEIMAGEDIR = "paletteimages"; //$NON-NLS-1$

	/** The background image directory*/
	public final static String	sBACKGROUNDIMAGEDIR = "backgroundimages"; //$NON-NLS-1$

	/** The template image directory*/
	public final static String	sTEMPLATEDIR = "templates"; //$NON-NLS-1$

	/** the parent stencil manager for this stencil panel.*/
	private UIStencilManager 	oParent 		= null;

	/** The name of this stencil.*/
	private String 				sName 			= ""; //$NON-NLS-1$

	/** The tab name of this stencil.*/
	private String 				sTabName 		= ""; //$NON-NLS-1$

	/** The filename of this stencil.*/
	private String 				sFileName 		= ""; //$NON-NLS-1$

	/** The filename of this stencil.*/
	private String 				sFolderName 	= ""; //$NON-NLS-1$

	/** A list of the DraggableStencilIcon object in this stencil.*/
	private Vector 				vtItems 			= new Vector(10);

	/** A list of the DraggableStencilIcon object that have been deleted.*/
	private Vector 				vtDeletedItems 			= new Vector(10);
	
	/** The scrollpane for the stencil set.*/
	private JScrollPane			oScrollPane 	= null;

	/** Has this panel been drawn yet?*/
	private boolean 			drawn 			= false;

	/** The button to close the dialog.*/
	private JButton				pbClose 		= null;

	/** The panel that holds the draggable icons.*/
	private JPanel				oIconPanel		= null;

	/** The current pointer for the grid layout.*/
	private int					y				= 0;

	/** The layout manager for the icon panel.*/
	private GridBagLayout 		grid 			= null;

	/** A reference to self for event listener to use.*/
	private UIStencilSet		me				= null;


	/**
	 * Constructor. Create a new instance of UIStencil, for a new Stencil.
	 * @param oParent, the parent UIStencilManager object for this stencil panel.
	 */
	public UIStencilSet(UIStencilManager oParent) {
		this.oParent = oParent;
		me = this;
	}

	/**
	 * Constructor. Create a new instance of UIStencil, for a new Stencil.
	 * @param oParent the parent UIStencilManager object for this stencil panel.
	 * @param sName the name of this panel.
	 */
	public UIStencilSet(UIStencilManager oParent, String sName) {
		this.sName = sName;
		this.sFolderName = (CoreUtilities.cleanFileName(sName));
		this.sFileName = (CoreUtilities.cleanFileName(sName))+".xml"; //$NON-NLS-1$
		this.oParent = oParent;
		me = this;
	}

	/**
	 * Constructor. Create a new instance of UIStencil for an exiting stencil.
	 *
	 * @param oParent the parent UIStencilManager object for this stencil panel.
	 * @param sFileName the file name of this setcil set.
	 * @param sFolderName the parent folder name of this stencil set.
	 * @param sName the name of this panel.
	 * @param sTabName the tab name when displaying this panel.
	 */
	public UIStencilSet(UIStencilManager oParent, String sFileName, String sFolderName, String sName, String sTabName) {
		this.sName = sName;

		if (sTabName == null || sTabName.equals("")) //$NON-NLS-1$
			this.sTabName = sName;
		else
			this.sTabName = sTabName;

		this.sFileName = sFileName;
		this.sFolderName = sFolderName;
		this.oParent = oParent;
		me = this;
	}

	/**
	 * Draws the contents of this panel.
	 */
	public void draw() {

		setLayout(new BorderLayout());

		vtItems = CoreUtilities.sortList(vtItems);

		createIconPanel();

		Dimension size = oIconPanel.getPreferredSize();

		oScrollPane = new JScrollPane(oIconPanel);
		oScrollPane.setBackground(Color.white);
		oScrollPane.setPreferredSize(new Dimension(size.width+25, size.height));
		add(oScrollPane, BorderLayout.CENTER);

		JPanel panel = new JPanel();
		pbClose = new UIButton(LanguageProperties.getString(LanguageProperties.STENCILS_BUNDLE, "UIStencilSet.close")); //$NON-NLS-1$
		pbClose.setMargin(new Insets(1,1,1,1));
		final UIStencilSet me = this;
		pbClose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				oParent.closeStencilSet(me.getName(), me);
			}
		});
		panel.add(pbClose);
		add(panel, BorderLayout.SOUTH);

		drawn = true;
	}

	/**
	 * Create the icon panel.
	 */
	private void createIconPanel() {

		int count = vtItems.size();
		
		GridBagConstraints gc = new GridBagConstraints();
		gc.insets = new Insets(2,2,2,2);
		gc.anchor = GridBagConstraints.NORTH;
		gc.fill = GridBagConstraints.NONE;

		grid = new GridBagLayout();
		oIconPanel = new JPanel(grid);
		oIconPanel.setBackground(Color.white);
		for (int i=0; i<count; i++) {
			DraggableStencilIcon icon = (DraggableStencilIcon)vtItems.elementAt(i);			
			icon.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent evt) {
					if (evt.getClickCount() == 2) {
						if (evt.getSource() instanceof DraggableStencilIcon) {
							DraggableStencilIcon oIcon = (DraggableStencilIcon)evt.getSource();
							UIStencilDialog dlg = new UIStencilDialog(ProjectCompendium.APP, ProjectCompendium.APP.oStencilManager);
							UIUtilities.centerComponent(dlg, ProjectCompendium.APP);
							dlg.onAutoEdit(me, oIcon);
							dlg.setVisible(true);
							evt.consume();
						}
						else {
							System.out.println("comp = "+evt.getSource().getClass().getName()); //$NON-NLS-1$
						}
					}
				}
			});

			gc.gridy = y;
			y++;
			if (i == count-1)
				gc.weighty = 100.00;
			grid.setConstraints(icon, gc);
			oIconPanel.add(icon);
		}
	}

	/**
	 * Redraw the icon panel.
	 */
	public void refreshStencilSet() {
		vtItems = CoreUtilities.sortList(vtItems);

		if (drawn) {
			int count = vtItems.size();
			oIconPanel.removeAll();
			y=0;
			GridBagConstraints gc = new GridBagConstraints();
			gc.insets = new Insets(2,2,2,2);
			gc.anchor = GridBagConstraints.NORTH;
			gc.fill = GridBagConstraints.NONE;

			for (int i=0; i<count; i++) {
				DraggableStencilIcon icon = (DraggableStencilIcon)vtItems.elementAt(i);
				gc.gridy = y;
				y++;
				if (i == count-1)
					gc.weighty = 100.00;
				grid.setConstraints(icon, gc);
				oIconPanel.add(icon);
			}
		}
	}

	/**
	 * Return the stencil item associated with the given shortcut number.
	 * @param nShortcut the shortcut number to check for.
	 * @return DraggableStencilIcon the associated item, or null if not found.
	 */
	public DraggableStencilIcon getItemForShortcut(int nShortcut) {

		int count = vtItems.size();
		DraggableStencilIcon oIcon = null;

		for (int i=0; i<count; i++) {
			DraggableStencilIcon icon = (DraggableStencilIcon)vtItems.elementAt(i);
			int nShort = icon.getShortcut();
			if (nShort != -1 && nShort == nShortcut) {
				oIcon = icon;
				break;
			}
		}

		return oIcon;
	}

	/**
	 * Has this panel been drawn yet?
	 * @return boolean, true if the panel has been drawn, else false.
	 */
	public boolean isDrawn() {
		return drawn;
	}

	/**
	 * Set the name for this stencil set.
	 * @param sName the name of this stencil set.
	 */
	public void setName(String sName) {
		this.sName = sName;
		if (this.sFolderName.equals("")) //$NON-NLS-1$
			this.sFolderName = (CoreUtilities.cleanFileName(sName));
		if (this.sFileName.equals("")) //$NON-NLS-1$
			this.sFileName = (CoreUtilities.cleanFileName(sName))+".xml"; //$NON-NLS-1$
	}

	/**
	 * Get the name for this stencil set.
	 * @return String, the name of this stencil set.
	 */
	public String getName() {
		return sName;
	}

	/**
	 * Set the tab  for this stencil set panel.
	 * @param sTabName the name of this stencil set.
	 */
	public void setTabName(String sTabName) {
		this.sTabName = sTabName;
	}

	/**
	 * Get the tab name for this stencil set panel.
	 * @return String, the tab name of this stencil set.
	 */
	public String getTabName() {
		return sTabName;
	}

	/**
	 * Get the file name for this stencil set panel.
	 * @return String, the file name of this stencil set.
	 */
	public String getFileName() {
		return sFileName;
	}

	/**
	 * Get the folder name for this stencil set panel.
	 * @return String, the folder name of this stencil set.
	 */
	public String getFolderName() {
		return sFolderName;
	}

	/**
	 * Get the current item list for this stencil set.
	 * @return Vector, the current item list for this stencil set.
	 */
	public Vector getItems() {
		return (Vector)vtItems.clone();
	}

	/**
	 * Add the given item to the list.
	 * @param item the DraggableStencilIcon to add.
	 */
	public void loadStencilItem(DraggableStencilIcon item) {
		vtItems.addElement(item);
	}

	/**
	 * Add the given item to the list.
	 * @param item the DraggableStencilIcon to add.
	 */
	public void addStencilItem(DraggableStencilIcon item) {
		if (!vtItems.contains(item)) {
			vtItems.addElement(item);
		}
		else {
			vtItems.remove(vtItems.elementAt(vtItems.indexOf(item)));
			vtItems.addElement(item);
		}
	}

	/**
	 * Remove the given item from the list
	 * @param item the DraggableStencilIcon to remove.
	 */
	public void removeStencilItem(DraggableStencilIcon item) {
		if (vtItems.contains(item)) {
			vtItems.remove(item);
			vtDeletedItems.addElement(item);
		}
	}
	
	/**
	 * Remove any associated files not being used by another item for deleted items.
	 */
	private void processDeletedItems() {
		
		String sImagePath = ""; //$NON-NLS-1$
		String sPaletteImagePath = ""; //$NON-NLS-1$
		String sBackground = ""; //$NON-NLS-1$
		String sTemplate = ""; //$NON-NLS-1$
		
		String sNextImagePath = ""; //$NON-NLS-1$
		String sNextPaletteImagePath = ""; //$NON-NLS-1$
		String sNextBackgroundImagePath = ""; //$NON-NLS-1$
		String sNextTemplatePath = ""; //$NON-NLS-1$

		boolean bKeep = false;
		boolean bKeepPalette = false;			
		boolean bKeepBackground = false;
		boolean bKeepTemplate = false;	
		
		int count = vtDeletedItems.size();
		for (int i=0; i<count; i++) {
		
			DraggableStencilIcon item = (DraggableStencilIcon)vtDeletedItems.elementAt(i);
			
			// Delete associated files if no other item in this group is using them.
			sImagePath = item.getImage();
			sPaletteImagePath = item.getPaletteImage();
			sBackground = item.getBackgroundImage();
			sTemplate = item.getTemplate();
			
			sNextImagePath = ""; //$NON-NLS-1$
			sNextPaletteImagePath = ""; //$NON-NLS-1$
			sNextBackgroundImagePath = ""; //$NON-NLS-1$
			sNextTemplatePath = ""; //$NON-NLS-1$

			bKeep = false;
			bKeepPalette = false;			
			bKeepBackground = false;
			bKeepTemplate = false;			
			
			DraggableStencilIcon oNext = null;
			
			int countj = vtItems.size();
			for (int j=0; j<countj; j++) {
				oNext = (DraggableStencilIcon)vtItems.elementAt(j);
				sNextImagePath = oNext.getImage();
				sNextPaletteImagePath = oNext.getPaletteImage();
				sNextBackgroundImagePath = oNext.getBackgroundImage();
				sNextTemplatePath = oNext.getTemplate();
				
				if (sImagePath.equals(sNextImagePath)) {
					bKeep = true;
				}
				if (sImagePath.equals(sNextPaletteImagePath)) {
					bKeep = true;
				}								
				if (sPaletteImagePath.equals(sNextImagePath)) {
					bKeepPalette = true;
				}				
				if (sPaletteImagePath.equals(sNextPaletteImagePath)) {
					bKeepPalette = true;
				}				
				if (sTemplate.equals(sNextTemplatePath)) {
					bKeepTemplate = true;
				}
				if (sBackground.equals(sNextBackgroundImagePath)) {
					bKeepBackground = true;
				}				
			}
			
			try {
				if (!bKeep) {
					CoreUtilities.deleteFile(new File(sImagePath));
				}	
				if (!bKeepPalette) {
					CoreUtilities.deleteFile(new File(sPaletteImagePath));
				}	
				if (!bKeepBackground) {
					CoreUtilities.deleteFile(new File(sBackground));
				}
				if (!bKeepTemplate) {
					CoreUtilities.deleteFile(new File(sTemplate));
				}	
			} catch (SecurityException ex) {
				System.out.println("Exception deleting directory due to:\n"+ex.getMessage()); //$NON-NLS-1$
			}			
		}
		
		vtDeletedItems.clear();		
	}

	/**
	 * Returns a xml string containing the stencil data.
	 *
	 * @return a String object containing formatted xml representation of the stencil data
	 */
	private String getStencilXML() {

		StringBuffer data = new StringBuffer(600);

		data.append("<?xml version=\"1.0\"?>\n"); //$NON-NLS-1$
		data.append("<!DOCTYPE stencil [\n"); //$NON-NLS-1$
		data.append("<!ELEMENT stencil (#PCDATA | items)*>\n"); //$NON-NLS-1$
		data.append("<!ATTLIST stencil\n"); //$NON-NLS-1$
		data.append("name CDATA #REQUIRED\n"); //$NON-NLS-1$
		data.append("tabname CDATA #REQUIRED\n"); //$NON-NLS-1$
		data.append(">\n"); //$NON-NLS-1$
		data.append("<!ELEMENT items (#PCDATA | item)*>\n"); //$NON-NLS-1$
		data.append("<!ELEMENT item (#PCDATA | tags)*>\n"); //$NON-NLS-1$
		data.append("<!ATTLIST item\n"); //$NON-NLS-1$
		data.append("type CDATA #REQUIRED\n"); //$NON-NLS-1$
		data.append("shortcut CDATA #REQUIRED\n"); //$NON-NLS-1$
		data.append("label CDATA #REQUIRED\n"); //$NON-NLS-1$
		data.append("tooltip CDATA #REQUIRED\n"); //$NON-NLS-1$
		data.append("image CDATA #REQUIRED\n"); //$NON-NLS-1$
		data.append("paletteimage CDATA #REQUIRED\n"); //$NON-NLS-1$
		data.append("backgroundimage CDATA #IMPLIED\n"); //$NON-NLS-1$
		data.append("template CDATA #IMPLIED\n");		 //$NON-NLS-1$
		data.append(">\n"); //$NON-NLS-1$
		data.append("<!ELEMENT tags (#PCDATA | tag)*>\n"); //$NON-NLS-1$
		data.append("<!ELEMENT tag (#PCDATA)>\n"); //$NON-NLS-1$
		data.append("<!ATTLIST tag\n"); //$NON-NLS-1$
		data.append("id CDATA #REQUIRED\n"); //$NON-NLS-1$
		data.append("author CDATA #REQUIRED\n"); //$NON-NLS-1$
		data.append("created CDATA #REQUIRED\n"); //$NON-NLS-1$
		data.append("lastModified CDATA #REQUIRED\n"); //$NON-NLS-1$
		data.append("name CDATA #REQUIRED\n"); //$NON-NLS-1$
		data.append("description CDATA #REQUIRED\n"); //$NON-NLS-1$
		data.append("behavior CDATA #REQUIRED\n"); //$NON-NLS-1$
		data.append(">\n"); //$NON-NLS-1$
		data.append("]>\n\n"); //$NON-NLS-1$

		data.append("<stencil name=\""+sName+"\" tabname=\""+sTabName+"\">\n"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		data.append("\t<items>\n"); //$NON-NLS-1$

		int count = vtItems.size();
		for (int i=0; i<count; i++) {
			DraggableStencilIcon item = (DraggableStencilIcon)vtItems.elementAt(i);

			String sFS = UIStencilManager.sFS;
			String sImage = item.getImage();
			if (sImage.startsWith(UIStencilManager.sPATH+sFolderName+sFS+sNODEIMAGEDIR)) {
				File file = new File(sImage);
				sImage = file.getName();
			}

			String sPaletteImage = item.getPaletteImage();
			if (sPaletteImage.startsWith(UIStencilManager.sPATH+sFolderName+sFS+sPALETTEIMAGEDIR)) {
				File pfile = new File(sPaletteImage);
				sPaletteImage = pfile.getName();
			}

			String sBackgroundImage = item.getBackgroundImage();
			if (sBackgroundImage.startsWith(UIStencilManager.sPATH+sFolderName+sFS+sBACKGROUNDIMAGEDIR)) {
				File bfile = new File(sBackgroundImage);
				sBackgroundImage = bfile.getName();
			}

			String sTemplate = item.getTemplate();
			if (sTemplate.startsWith(UIStencilManager.sPATH+sFolderName+sFS+sTEMPLATEDIR)) {
				File bfile = new File(sTemplate);
				sTemplate = bfile.getName();
			}

			String sLabel = item.getLabel();
			String sTip = item.getToolTip();
			int nType = item.getNodeType();
			int nShortcut = item.getShortcut();
			Vector vtTags = item.getTags();

			data.append("\t\t<item label=\""+CoreUtilities.cleanXMLText(sLabel)+"\" "); //$NON-NLS-1$ //$NON-NLS-2$
			data.append("tooltip=\""+CoreUtilities.cleanXMLText(sTip)+"\" "); //$NON-NLS-1$ //$NON-NLS-2$
			data.append("image=\""+sImage+"\" "); //$NON-NLS-1$ //$NON-NLS-2$
			data.append("paletteimage=\""+sPaletteImage+"\" "); //$NON-NLS-1$ //$NON-NLS-2$
			data.append("backgroundimage=\""+sBackgroundImage+"\" "); //$NON-NLS-1$ //$NON-NLS-2$
			data.append("template=\""+sTemplate+"\" ");			 //$NON-NLS-1$ //$NON-NLS-2$
			data.append("shortcut=\""+nShortcut+"\" "); //$NON-NLS-1$ //$NON-NLS-2$
			data.append("type=\""+nType+"\">\n"); //$NON-NLS-1$ //$NON-NLS-2$

			if (vtTags.size() > 0) {
				data.append("\t\t\t<tags>\n"); //$NON-NLS-1$
				int jcount = vtTags.size();
				for(int j=0; j<jcount;j++) {
					Vector nextCode = (Vector)vtTags.elementAt(j);
					data.append("\t\t\t\t<tag "); //$NON-NLS-1$

					data.append("id=\""+ (String)nextCode.elementAt(0) +"\" "); //$NON-NLS-1$ //$NON-NLS-2$
					data.append("author=\""+ (String)nextCode.elementAt(2) +"\" "); //$NON-NLS-1$ //$NON-NLS-2$
					data.append("created=\""+ ((Date)nextCode.elementAt(5)).getTime() +"\" "); //$NON-NLS-1$ //$NON-NLS-2$
					data.append("lastModified=\""+ ((Date)nextCode.elementAt(6)).getTime() +"\" " ); //$NON-NLS-1$ //$NON-NLS-2$
					data.append("name=\""+ CoreUtilities.cleanXMLText((String)nextCode.elementAt(1)) +"\" "); //$NON-NLS-1$ //$NON-NLS-2$
					data.append("description=\""+ CoreUtilities.cleanXMLText((String)nextCode.elementAt(3)) +"\" "); //$NON-NLS-1$ //$NON-NLS-2$
					data.append("behavior=\""+ CoreUtilities.cleanXMLText((String)nextCode.elementAt(4)) +"\""); //$NON-NLS-1$ //$NON-NLS-2$

					data.append("/>\n"); //$NON-NLS-1$
				}
				data.append("\t\t\t</tags>\n"); //$NON-NLS-1$
			}

			data.append("\t\t</item>\n"); //$NON-NLS-1$
		}

		data.append("\t</items>\n"); //$NON-NLS-1$
		data.append("</stencil>\n"); //$NON-NLS-1$

		return data.toString();
	}

	/**
	 * Delete this stencil set.
	 */
	public void delete() {
		File directory = new File(UIStencilManager.sPATH+sFolderName);
		try {
			boolean deleted = CoreUtilities.deleteDirectory(directory);		
			if (!deleted) {
				ProjectCompendium.APP.displayError(LanguageProperties.getString(LanguageProperties.STENCILS_BUNDLE, "UIStencilSet.unableToFindFolder")+"\n\n"); //$NON-NLS-1$ //$NON-NLS-2$
			}
		} catch (SecurityException ex) {
			System.out.println("Exception deleting directory due to:\n"+ex.getMessage()); //$NON-NLS-1$
		}		
	}

	/**
	 * Rename the stencil folder and xml file after a name change and save data.
	 * @param sNewName The new name for this stencil set.
	 */
	public void saveToNew(String sNewName) {

		File olddir = new File(UIStencilManager.sPATH+sFolderName);
		File oldfile = new File(UIStencilManager.sPATH+sFolderName+ProjectCompendium.sFS+sFileName);
		String newFolderName = (CoreUtilities.cleanFileName(sNewName));
		File newdir = new File(UIStencilManager.sPATH+newFolderName);

		boolean dirrenamed = olddir.renameTo(newdir);
		boolean filedeleted = false;
		if (!dirrenamed) {
			ProjectCompendium.APP.displayError(LanguageProperties.getString(LanguageProperties.STENCILS_BUNDLE, "UIStencilSet.unableToRename")); //$NON-NLS-1$
			saveStencilData();
			return;
		}
		else
			filedeleted = oldfile.delete();

		if (!filedeleted) {
			ProjectCompendium.APP.displayError(LanguageProperties.getString(LanguageProperties.STENCILS_BUNDLE, "UIStencilSet.unableToDeleteA")+"\n\n"+LanguageProperties.getString(LanguageProperties.STENCILS_BUNDLE, "UIStencilSet.unableToDeleteB")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}

		if (dirrenamed) {

			// CORRECT THE FILE PATHS.
			int count = vtItems.size();
			for (int i=0; i<count; i++) {
				DraggableStencilIcon item = (DraggableStencilIcon)vtItems.elementAt(i);

				String sImage = item.getImage();
				String oldPath = UIStencilManager.sPATH+sFolderName+ProjectCompendium.sFS+sNODEIMAGEDIR+ProjectCompendium.sFS;
				String newPath = UIStencilManager.sPATH+newFolderName+ProjectCompendium.sFS+sNODEIMAGEDIR+ProjectCompendium.sFS;
				if (sImage.startsWith(oldPath)) {
					File imageFile = new File(sImage);
					newPath = newPath+imageFile.getName();
					item.setImage(newPath);
				}

				String sPaletteImage = item.getPaletteImage();
				String oldPath2 = UIStencilManager.sPATH+sFolderName+ProjectCompendium.sFS+sPALETTEIMAGEDIR+ProjectCompendium.sFS;
				String newPath2 = UIStencilManager.sPATH+newFolderName+ProjectCompendium.sFS+sPALETTEIMAGEDIR+ProjectCompendium.sFS;
				if (sPaletteImage.startsWith(oldPath2)) {
					File imageFile2 = new File(sPaletteImage);
					newPath2 = newPath2+imageFile2.getName();
					item.setPaletteImage(newPath2);
					item.setIcon(new ImageIcon(newPath2));
				}

				String sBackgroundImage = item.getBackgroundImage();
				String oldPath3 = UIStencilManager.sPATH+sFolderName+ProjectCompendium.sFS+sBACKGROUNDIMAGEDIR+ProjectCompendium.sFS;
				String newPath3 = UIStencilManager.sPATH+newFolderName+ProjectCompendium.sFS+sBACKGROUNDIMAGEDIR+ProjectCompendium.sFS;
				if (sPaletteImage.startsWith(oldPath3)) {
					File imageFile3 = new File(sBackgroundImage);
					newPath3 = newPath3+imageFile3.getName();
					item.setBackgroundImage(newPath3);
				}
				
				String sTemplate = item.getTemplate();
				String oldPath4 = UIStencilManager.sPATH+sFolderName+ProjectCompendium.sFS+sTEMPLATEDIR+ProjectCompendium.sFS;
				String newPath4 = UIStencilManager.sPATH+newFolderName+ProjectCompendium.sFS+sTEMPLATEDIR+ProjectCompendium.sFS;
				if (sTemplate.startsWith(oldPath4)) {
					File file4 = new File(sTemplate);
					newPath4 = newPath4+file4.getName();
					item.setTemplate(newPath4);
				}				
			}

			sFolderName = newFolderName;
			sFileName = (CoreUtilities.cleanFileName(sNewName))+".xml"; //$NON-NLS-1$
		}

		// SAVE THE XML FILE
		String data = getStencilXML();
		try {
			FileWriter fileWriter = new FileWriter(UIStencilManager.sPATH+sFolderName+ProjectCompendium.sFS+sFileName);
			fileWriter.write(data);
			fileWriter.close();
		}
		catch (IOException e) {
			ProjectCompendium.APP.displayError("Exception: (UIStencil.saveStencilData) \n\n" + e.getMessage()); //$NON-NLS-1$
		}
	}

	/**
	 * Save this stencil to an xml file.
	 */
	public void saveStencilData() {
		
		// MAKE ANY REQUIRED DIRECTORIES
		File directory = new File(UIStencilManager.sPATH+sFolderName);
		if (!directory.isDirectory()) {
			directory.mkdirs();
		}

		File imagedirectory = new File(UIStencilManager.sPATH+sFolderName+ProjectCompendium.sFS+sNODEIMAGEDIR);
		if (!imagedirectory.isDirectory()) {
			imagedirectory.mkdirs();
		}

		File pimagedirectory = new File(UIStencilManager.sPATH+sFolderName+ProjectCompendium.sFS+sPALETTEIMAGEDIR);
		if (!pimagedirectory.isDirectory()) {
			pimagedirectory.mkdirs();
		}

		File bimagedirectory = new File(UIStencilManager.sPATH+sFolderName+ProjectCompendium.sFS+sBACKGROUNDIMAGEDIR);
		if (!bimagedirectory.isDirectory()) {
			bimagedirectory.mkdirs();
		}

		File templatedirectory = new File(UIStencilManager.sPATH+sFolderName+ProjectCompendium.sFS+sTEMPLATEDIR);
		if (!templatedirectory.isDirectory()) {
			templatedirectory.mkdirs();
		}

		// MOVE ASSOCIATED NODE, BACKGROUND, PALLETTE AND TEMPLATE FILES IF NOT IN RIGHT PLACE.
		int count = vtItems.size();
		for (int i=0; i<count; i++) {
			DraggableStencilIcon item = (DraggableStencilIcon)vtItems.elementAt(i);

			String sImage = item.getImage();
			String newPath = UIStencilManager.sPATH+sFolderName+ProjectCompendium.sFS+sNODEIMAGEDIR+ProjectCompendium.sFS;
			if (!sFolderName.equals("") && //$NON-NLS-1$
					!sImage.startsWith(newPath)) {
				File imageFile = new File(sImage);
				if (imageFile.exists()) {
					try {
						newPath = newPath+imageFile.getName();
						FileInputStream fis = new FileInputStream(imageFile.getAbsolutePath());
					    FileOutputStream fos = new FileOutputStream(newPath);
				    	byte[] dataBytes = new byte[fis.available()];
				      	fis.read(dataBytes);
				      	fos.write(dataBytes);
						fis.close();
						fos.close();
						item.setImage(newPath);
					}
					catch(Exception ex) {
						System.out.println(LanguageProperties.getString(LanguageProperties.STENCILS_BUNDLE, "UIStencilSet.unableToMoveImage")+imageFile.getName()+"\n\n"+LanguageProperties.getString(LanguageProperties.STENCILS_BUNDLE, "UIStencilSet.dueTo")+"\n\n"+ex.getMessage()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
					}
				}
				else {
					System.out.println(LanguageProperties.getString(LanguageProperties.STENCILS_BUNDLE, "UIStencilSet.notFoundMove")+imageFile.getName()); //$NON-NLS-1$
				}
			}

			String sPaletteImage = item.getPaletteImage();
			if (!sPaletteImage.equals("") && !sPaletteImage.equals(sImage)) { //$NON-NLS-1$

				String newPath2 = UIStencilManager.sPATH+sFolderName+ProjectCompendium.sFS+sPALETTEIMAGEDIR+ProjectCompendium.sFS;
				if (!sFolderName.equals("") && //$NON-NLS-1$
						!sPaletteImage.startsWith(newPath2)) {
					File imageFile2 = new File(sPaletteImage);
					if (imageFile2.exists()) {
						try {
							newPath2 = newPath2+imageFile2.getName();
							FileInputStream fis = new FileInputStream(imageFile2.getAbsolutePath());
						    FileOutputStream fos = new FileOutputStream(newPath2);
					    	byte[] dataBytes = new byte[fis.available()];
					      	fis.read(dataBytes);
					      	fos.write(dataBytes);
							fis.close();
							fos.close();
							item.setPaletteImage(newPath2);
							item.setIcon(new ImageIcon(newPath2));
						}
						catch(Exception ex) {
							System.out.println(LanguageProperties.getString(LanguageProperties.STENCILS_BUNDLE, "UIStencilSet.unableToMovePalette")+imageFile2.getName()+"\n\n"+LanguageProperties.getString(LanguageProperties.STENCILS_BUNDLE, "UIStencilSet.dueTo")+"\n\n"+ex.getMessage()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
						}
					}
					else {
						System.out.println(LanguageProperties.getString(LanguageProperties.STENCILS_BUNDLE, "UIStencilSet.notFoundMovePalette")+imageFile2.getName()); //$NON-NLS-1$
					}
				}
			}

			String sBackgroundImage = item.getBackgroundImage();
			if (!sBackgroundImage.equals("")) { //$NON-NLS-1$

				String newPath3 = UIStencilManager.sPATH+sFolderName+ProjectCompendium.sFS+sBACKGROUNDIMAGEDIR+ProjectCompendium.sFS;
				if (!sFolderName.equals("") && //$NON-NLS-1$
						!sBackgroundImage.startsWith(newPath3)) {
					File imageFile3 = new File(sBackgroundImage);
					if (imageFile3.exists()) {
						try {
							newPath3 = newPath3+imageFile3.getName();
							FileInputStream fis = new FileInputStream(imageFile3.getAbsolutePath());
						    FileOutputStream fos = new FileOutputStream(newPath3);
					    	byte[] dataBytes = new byte[fis.available()];
					      	fis.read(dataBytes);
					      	fos.write(dataBytes);
							fis.close();
							fos.close();
							item.setBackgroundImage(newPath3);
						}
						catch(Exception ex) {
							System.out.println(LanguageProperties.getString(LanguageProperties.STENCILS_BUNDLE, "UIStencilSet.unableToMoveBackground")+imageFile3.getName()+"\n\n"+LanguageProperties.getString(LanguageProperties.STENCILS_BUNDLE, "UIStencilSet.dueTo")+"\n\n"+ex.getMessage()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
						}
					}
					else {
						System.out.println(LanguageProperties.getString(LanguageProperties.STENCILS_BUNDLE, "UIStencilSet.notFoundMoveBackground")+imageFile3.getName()); //$NON-NLS-1$
					}
				}
			}
			
			String sTemplate = item.getTemplate();
			if (!sTemplate.equals("")) { //$NON-NLS-1$

				String newPath4 = UIStencilManager.sPATH+sFolderName+ProjectCompendium.sFS+sTEMPLATEDIR+ProjectCompendium.sFS;
				if (!sFolderName.equals("") && //$NON-NLS-1$
						!sTemplate.startsWith(newPath4)) {
					File file4 = new File(sTemplate);
					if (file4.exists()) {
						try {
							newPath4 = newPath4+file4.getName();
							FileInputStream fis = new FileInputStream(file4.getAbsolutePath());
						    FileOutputStream fos = new FileOutputStream(newPath4);
					    	byte[] dataBytes = new byte[fis.available()];
					      	fis.read(dataBytes);
					      	fos.write(dataBytes);
							fis.close();
							fos.close();
							item.setTemplate(newPath4);
						}
						catch(Exception ex) {
							System.out.println(LanguageProperties.getString(LanguageProperties.STENCILS_BUNDLE, "UIStencilSet.unableToMoveTemplate")+file4.getName()+"\n\n"+LanguageProperties.getString(LanguageProperties.STENCILS_BUNDLE, "UIStencilSet.dueTo")+"\n\n"+ex.getMessage()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
						}
					}
					else {
						System.out.println(LanguageProperties.getString(LanguageProperties.STENCILS_BUNDLE, "UIStencilSet.notFoundMoveTemplate")+file4.getName()); //$NON-NLS-1$
					}
				}
			}			
		}
		
		// SAVE THE XML FILE
		String data = getStencilXML();
		try {
			FileWriter fileWriter = new FileWriter(UIStencilManager.sPATH+sFolderName+ProjectCompendium.sFS+sFileName);
			fileWriter.write(data);
			fileWriter.close();
			
			// REMOAVE ANY UNREQUIRED FILES FOR ITEMS THAT HAVE BEEN DELETED
			processDeletedItems();			
		}
		catch (IOException e) {
			ProjectCompendium.APP.displayError("Exception: (UIStencil.saveStencilData) \n\n" + e.getMessage()); //$NON-NLS-1$
		}
	}

	/**
	 * Make a duplicate of this object but with a new id.
	 */
	public UIStencilSet duplicate(String sNewName) {

		UIStencilSet oStencilSet = new UIStencilSet(oParent);
		oStencilSet.setName(sNewName);
		oStencilSet.setTabName(sNewName);

		Vector items = getItems();
		int count = items.size();
		for (int i=0; i<count; i++) {
			DraggableStencilIcon item = (DraggableStencilIcon)items.elementAt(i);
			DraggableStencilIcon dup = item.duplicate();
			oStencilSet.loadStencilItem(dup);
		}

		return oStencilSet;
	}
}
