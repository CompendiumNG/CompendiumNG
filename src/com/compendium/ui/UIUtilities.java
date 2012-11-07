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

import java.io.*;
import java.net.URI;
import java.awt.*;
import java.awt.event.*;
import java.awt.font.FontRenderContext;
import java.text.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.zip.*;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;

import com.compendium.*;
import com.compendium.core.datamodel.IModel;
//import com.compendium.core.datamodel.LinkedFile;
//import com.compendium.core.datamodel.LinkedFileCopy;
//import com.compendium.core.datamodel.LinkedFileDatabase;
import com.compendium.core.datamodel.LinkedFile;
import com.compendium.core.datamodel.LinkedFileDatabase;
import com.compendium.core.datamodel.Model;
import com.compendium.core.datamodel.NodePosition;
import com.compendium.core.datamodel.PCSession;
import com.compendium.core.datamodel.UserProfile;
import com.compendium.ui.linkgroups.UILinkGroup;
import com.compendium.ui.linkgroups.UILinkType;
import com.compendium.ui.movie.UIMovieMapViewFrame;
import com.compendium.ui.movie.UIMovieMapViewPane;
import com.compendium.ui.plaf.*;
import com.compendium.core.*;
import com.compendium.core.datamodel.*;
import com.compendium.core.datamodel.services.ILinkedFileService;
import com.compendium.core.db.*;
import com.compendium.ui.tags.*;




/**
 * UIUtilities contains methods to help ui classes
 *
 * @author	Michelle Bachler
 */
public class UIUtilities {
	
	/** Create a new node to the Right of the given one.*/
	public static final int	DIRECTION_RIGHT = 0;

	/** Create a new node to the Left of the given one.*/
	public static final int	DIRECTION_LEFT = 1;

	/** Create a new node to the Up from the given one.*/
	public static final int	DIRECTION_UP = 2;

	/** Create a new node to the Down from the given one.*/
	public static final int	DIRECTION_DOWN = 3;

	/** A FontRenderContext that can be used to measure Strings */
	private static FontRenderContext frc=null;
		 
	/**
	 * Get the current GraphicsConfiguration for this machine.
	 * @author DrLaszloJamf (Sun Developer Forum)
	 * @return GraphicsConfiguration the current graphics configuration.
	 */
    public static GraphicsConfiguration getDefaultConfiguration() {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();
        return gd.getDefaultConfiguration();
    }
	
	/**
	 * This method returns the FontRenderContext
	 * for the default Screen-device.
	 * @author FahleE (Sun Developer Forum)
	 * @return FontRenderContext the FontRenderContext that is used for the screen-device
	 */
	private static void createFontRenderContext() {
	  GraphicsConfiguration gc=getDefaultConfiguration();
	  BufferedImage bi=gc.createCompatibleImage(1,1); //we need at least one pixel
	  Graphics2D g2=bi.createGraphics();
	  frc=g2.getFontRenderContext();
	}
		 
	/**
	 * Gets the FontRenderContext for the default screen-device.
	 * The FontRenderContext returned here, can be used to measure
	 * for instance the visual size of Strings that are to be painted by a component
	 * that is not realized yet. 
	 * The FontRenderContext is mapped to the defaultScreenDevice 
	 * of the JVM. The returned here FontRenderContext can only to be used for 
	 * components that are painted onto the screen if you need a FontRenderContex
	 * for printing this will probably not work. 
	 * For performance reasons we return only a reference
	 * to a static FontRenderContext-object that is created in this class.
	 * You should take care NOT to set the object returned here to null.
	 * If you do this method will have to create a new one next time it is called.  
	 * So just use the returned FontRenderContext for obtaining the 
	 * metrics of a font and than leave it alone.
	 * @author FahleE (Sun Developer Forum)
	 * @return FontRenderContext a FontRenderContext for the 
	 * default screen.
	 */
	public static synchronized FontRenderContext getDefaultFontRenderContext() {
	  if(frc==null)
	    createFontRenderContext();
	  return frc;
	}

	/**
	 * Center the given child component on the given parent component.
 	 * @param child, the component to center.
	 * @param parent, the component to center the child component on.
	 */
	public static void centerComponent(Component child, Component parent) {

		int nChildWidth = child.getSize().width;
		int nChildHeight = child.getSize().height;

		int nParentWidth = parent.getWidth();
		int nParentHeight = parent.getHeight();
		int nParentX = parent.getX();
		int nParentY = parent.getY();

		int x = (nParentWidth - nChildWidth) / 2 + nParentX;
		int y = (nParentHeight - nChildHeight) / 2 + nParentY;
		if (x < 0) x = 0;
		if (y < 0) y = 0;

		child.setLocation(x, y);
	}
	
	/**
	 * Determine the file location of the current given path and relocate as appropriate.
	 * If in the database move to linkedFiles.
	 * If in LinkedFiles move to database.
	 * @param path the path of the file to move
	 * @return the new path of the file once moved.
	 */
	public static String processLocation(String path) {
		ProjectCompendium.APP.setWaitCursor();
		String sNewPath = path;
		try {
			if (LinkedFileDatabase.isDatabaseURI(path)) {
				Model oModel = (Model)ProjectCompendium.APP.getModel();
				PCSession oSession = oModel.getSession();				
				LinkedFile linked = new LinkedFileDatabase(new URI(path));
				linked.initialize(oSession, oModel);
				File file = linked.getFile(ProjectCompendium.temporaryDirectory);
				LinkedFile lFile = UIUtilities.copyDnDFileToFolder(file);
				sNewPath = lFile.getSourcePath();	
			} else {
				if (CoreUtilities.isFile(path)) {
					LinkedFile lFile = UIUtilities.copyDnDFileToDB(new File(path));
					sNewPath = lFile.getSourcePath();					
				}
			}		
		} catch(Exception io) {
			System.out.println("Failed to extract file from database: "+io.getLocalizedMessage()); //$NON-NLS-1$
		}
		ProjectCompendium.APP.setDefaultCursor();
		return sNewPath;
	}	
	
	/**
	 * Unzip the passed zip file and extract the XML.
	 *
	 * @param sFilePath the path to the zip file to import.
	 * @param bGiveUserXMLDialog true means give the user the XML Import dialog, else use defaults.
	 *							Defaults are: import with translcusion, preserve node ids,
	 *							overwrite node contents and preserve author and date in detail.
	 * @return true if completely successful.
	 * @exception IOException if there is an IO or Zip error.
	 */
    public static boolean unzipXMLZipFile(String sFilePath, boolean bGiveUserXMLDialog) throws IOException {

    	ProjectCompendium.APP.setWaitCursor();
    	
		ZipFile zipFile = new ZipFile(sFilePath);
		Enumeration entries = zipFile.entries();

		// Progress bar zipFile.size();
		
		ZipEntry entry = null;
		String sXMLFile = ""; //$NON-NLS-1$
		String sTemp = ""; //$NON-NLS-1$

		while(entries.hasMoreElements()) {
			entry = (ZipEntry)entries.nextElement();		
			sTemp = entry.getName();
			if (sTemp.endsWith(".xml") && sTemp.startsWith("Exports")) { //$NON-NLS-1$ //$NON-NLS-2$
				sXMLFile = sTemp;
			}

			// AVOID Thumbs.db files
			if (sTemp.endsWith(".db")) { //$NON-NLS-1$
				continue;
			}

			int len = 0;
			byte[] buffer = new byte[1024];
			InputStream in = zipFile.getInputStream(entry);
						
			String sFileName = entry.getName();
			File file = new File(sFileName);            
			if (file.getParentFile() != null) {
            	file.getParentFile().mkdirs();
            }
            
			OutputStream out = new BufferedOutputStream(new FileOutputStream(sFileName));
			while((len = in.read(buffer)) >=0) {
				out.write(buffer, 0, len);
			}
			in.close();
			out.close();
		}

		zipFile.close();

		// IMPORT THE XML
		if (!sXMLFile.equals("")) { //$NON-NLS-1$
			File oXMLFile = new File(sXMLFile);
			if (oXMLFile.exists()) {
				if (bGiveUserXMLDialog) {
					ProjectCompendium.APP.onFileXMLImport(oXMLFile);
					return true;
				} else {
					boolean importAuthorAndDate = true;
					boolean includeOriginalAuthorDate = true;
					boolean preserveIDs = true;
					boolean transclude = true;
					boolean updateTranscludedNodes = true;
					boolean markSeen = true;

					File oXMLFile2 = new File(sXMLFile);
					if (oXMLFile2.exists()) {
						DBNode.setImportAsTranscluded(transclude);
						DBNode.setPreserveImportedIds(preserveIDs);
						DBNode.setUpdateTranscludedNodes(updateTranscludedNodes);
						DBNode.setNodesMarkedSeen(markSeen);

						UIViewFrame frame = ProjectCompendium.APP.getCurrentFrame();
						if (frame instanceof UIMapViewFrame) {
							UIMapViewFrame mapFrame = (UIMapViewFrame)frame;
							UIViewPane oViewPane = mapFrame.getViewPane();
							ViewPaneUI oViewPaneUI = oViewPane.getUI();
							if (oViewPaneUI != null) {
								oViewPaneUI.setSmartImport(importAuthorAndDate);
								oViewPaneUI.onImportXMLFile(sXMLFile, includeOriginalAuthorDate);
							}
						} else if (frame instanceof UIListViewFrame){
							UIListViewFrame listFrame = (UIListViewFrame)frame;
							UIList uiList = listFrame.getUIList();
							if (uiList != null) {
								uiList.getListUI().setSmartImport(importAuthorAndDate);
								uiList.getListUI().onImportXMLFile(sXMLFile, includeOriginalAuthorDate);
							}
						}
						return true;
					}
				}
			}
		} else {
			ProjectCompendium.APP.displayError(LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "UIUtilities.noXMLFileFound")); //$NON-NLS-1$
		}
		
    	ProjectCompendium.APP.setDefaultCursor();

		return false;
	}

	/**
	 * Return the location to store DnD files.  Create the directory if necessary.
	 * 
	 *  @return string - The PATH
	 */
	public static String sGetLinkedFilesLocation() {
		
		String sDatabaseName = ProjectCompendium.APP.sFriendlyName;		
		UserProfile oUser = ProjectCompendium.APP.getModel().getUserProfile();
		String sUserDir = CoreUtilities.cleanFileName(oUser.getUserName())+"_"+oUser.getId(); //$NON-NLS-1$
		String sLinkedFilesPath = ProjectCompendium.APP.getModel().getlinkedFilesPath();
		Boolean bLinkedFilesFlat = ProjectCompendium.APP.getModel().getlinkedFilesFlat();
		
		if (sLinkedFilesPath == "") { //$NON-NLS-1$
			sLinkedFilesPath = "Linked Files"; //$NON-NLS-1$
		}
		String sFullPath = sLinkedFilesPath;
		
		if (!bLinkedFilesFlat) {
			sFullPath = sFullPath + ProjectCompendium.sFS+CoreUtilities.cleanFileName(sDatabaseName)+ProjectCompendium.sFS+sUserDir;
		}
		
		File directory = new File(sFullPath);
		if (!directory.isDirectory()) {
			directory.mkdirs();
		}
					
		int len = sFullPath.length();
		if (!sFullPath.substring(len-1).equals(ProjectCompendium.sFS)) {
			sFullPath = sFullPath + ProjectCompendium.sFS;
		}
		return sFullPath;
	}	

	/**
	 * Check whether the user has asked to copy DnD files to the Linked Files folder or database or not.
	 * If they have, copy the file.
	 *
	 * @param File file, the file to copy to the Linked Files folder/database.
	 */
	/*public static File checkCopyLinkedFile(File file) {

		String path = file.getPath();

		if (path.startsWith("www.") || path.startsWith("http:") || path.startsWith("https:"))
			return null;

		
		if (DragAndDropProperties.dndFilePrompt) {
			String sMessage = "";
			if (DragAndDropProperties.dndFileCopy) {
				sMessage = "Do you want Compendium to copy\n\n"+file.getAbsolutePath()+"\n\ninto the 'Linked Files' directory?\n\n"
			} else if (DragAndDropProperties.dndFileCopyDatabase) {
				sMessage = "Do you want Compendium to copy\n\n"+file.getAbsolutePath()+"\n\ninto the 'Linked Files' directory?\n\n"
			}

			int response = JOptionPane.showConfirmDialog(ProjectCompendium.APP, sMessage,
														"External Drag and Drop", JOptionPane.YES_NO_OPTION);

			if (response == JOptionPane.YES_OPTION) {
				if (DragAndDropProperties.dndFileCopy) {
					return copyDnDFile(file);
				} else if (DragAndDropProperties.dndFileCopyDatabase) {
					
				}
			}
		}

		return file;
	}*/
	
	/**
	 *  Copies a dropped file either to linked Files folder or the the database depending on prefs.
	 * @param File file, the file to copy.
	 * @param props the drag and drop properties for this file
	 * @return the newly created LinkedFile object, or <code>null</code>
	 * 	if the copy could not be created
	 */
	public static LinkedFile copyDnDFile(File file, DragAndDropProperties props) {
		if (props == null) {
			props = FormatProperties.dndProperties.clone(); // do not alter default settings
		}
		assert(props.dndFileCopy);
		String path = file.getPath();

		// should have been checked before if necessary:
		assert(!(path.startsWith("www.") || path.startsWith("http:") || path.startsWith("https:"))); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		if (props.dndFileCopyDatabase) {
			return copyDnDFileToDB(file);
		}
		else {
			return copyDnDFileToFolder(file);
		} 		
	}

	/**
	 * Copy the given file into the database.
	 * 
	 * @param file the file to copy into the database.
	 */
	public static LinkedFile copyDnDFileToDB(File file) {
		IModel model = ProjectCompendium.APP.getModel();
		PCSession session = model.getSession();
		ILinkedFileService lfs = model.getLinkedFileService();
		LinkedFile lf = null;
		try {
			lf = lfs.addFile(session, model.getUniqueID(), file);
		} catch (IOException e) {
			ProjectCompendium.APP
					.displayError("Exception: (UIUtilities.copyDnDFileToDB)\n\n" //$NON-NLS-1$
							+ e.getMessage());
			e.printStackTrace();
		}
		return lf;
	}

	/**
	 * Copy the given file to the Linked Files folder, into a subfolder with the current database name.
	 *
	 * @param File file, the file to copy to the Linked Files folder.
	 */
	public static LinkedFile copyDnDFileToFolder(File file) {

		File newFile = null;

		String sPATH = sGetLinkedFilesLocation();
		
		// Fix for Linux sent in by: Matthieu Nué
        if (ProjectCompendium.isLinux){
            String sFile=file.getAbsolutePath();
            sFile = sFile.substring(0, sFile.length() -1);
            file = new File(sFile);
        }
        //////////////////////////////////////////
        
		try {
			FileInputStream stream = new FileInputStream(file);
			byte b[] = new byte[stream.available()];
			stream.read(b);
			stream.close();

			String sTargetName = file.getName();
			sTargetName = sTargetName.replace("#", "");			// Windows ref node launch fails of target filename //$NON-NLS-1$ //$NON-NLS-2$
			sTargetName = sTargetName.replaceAll("  *", " ");		// has double spaces or a pound sign //$NON-NLS-1$ //$NON-NLS-2$
			newFile = new File(sPATH+sTargetName);
			if (newFile.exists()) {
				int response = JOptionPane.showConfirmDialog(ProjectCompendium.APP, 
														LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "UIUtilities.fileExistsA")+"\n"+ //$NON-NLS-1$ //$NON-NLS-2$
														LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "UIUtilities.fileExistsB")+"\n\n"+ //$NON-NLS-1$ //$NON-NLS-2$
														LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "UIUtilities.fileExistsC")+"\n\n", //$NON-NLS-1$ //$NON-NLS-2$
														LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "UIUtilities.externalDnd"), //$NON-NLS-1$
														JOptionPane.YES_NO_OPTION); //$NON-NLS-1$

				if (response == JOptionPane.YES_OPTION) {
					UIFileChooser fileDialog = new UIFileChooser();
					fileDialog.setDialogTitle(LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "UIUtilities.changeName")); //$NON-NLS-1$
					fileDialog.setApproveButtonText(LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "UIUtilities.saveButton")); //$NON-NLS-1$
					fileDialog.setApproveButtonMnemonic(LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "UIUtilities.saveButtonMnemonic").charAt(0)); //$NON-NLS-1$

				    File file2 = new File(sPATH+file.getName());
				    if (file2.exists()) {
						fileDialog.setCurrentDirectory(new File(file2.getParent()+ProjectCompendium.sFS));
						fileDialog.setSelectedFile(file2);
					}

					UIUtilities.centerComponent(fileDialog, ProjectCompendium.APP);
					int retval = fileDialog.showDialog(ProjectCompendium.APP, LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "UIUtilities.saveButton"));
					if (retval == JFileChooser.APPROVE_OPTION) {
			        	if ((fileDialog.getSelectedFile()) != null) {
			        			fileDialog.setVisible(true);

							String fileName = fileDialog.getSelectedFile().getAbsolutePath();
							if (fileName != null) {
								newFile = new File(fileName);
							}
			        	}
					}
				}
			}

			FileOutputStream output = new FileOutputStream(newFile);
			output.write(b);
			output.flush();
			output.close();
		}
		catch (SecurityException e) {
		    e.printStackTrace();
			ProjectCompendium.APP.displayError("Error: (UIUtilities.copyDnDFileToFolder)\n\n"+LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "UIUtilities.error1")+"\n\n" + e.getMessage()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}
		catch (IOException e) {
		    e.printStackTrace();
			ProjectCompendium.APP.displayError("Error: (UIUtilities.copyDnDFileToFolder)\n\n" + e.getMessage()); //$NON-NLS-1$
		}

		LinkedFile lf = new LinkedFileCopy(newFile.getPath());

		return lf;
	}
	
	/**
	 * Sort the given Vector of objects, depending on the object type.
	 * Types currently accepted are: UINode, DefaultMutlableTreeNode - CheckNode.
	 *
	 * @param Vector unsortedVector, the vector of Objects to sort.
	 * @return Vector, or sorted objects.
	 */
	public static Vector sortList(Vector unsortedVector) {

		Vector sortedVector = new Vector();
		Vector unsortedVector2 = new Vector();

		if(unsortedVector.size() > 0) {

			if(unsortedVector.elementAt(0) instanceof UINode) {
				for(Enumeration e = unsortedVector.elements();e.hasMoreElements();) {
					Object obj = (Object)e.nextElement();
					if (obj != null && obj instanceof UINode) {
						String text = ((UINode)obj).getText();
						unsortedVector2.addElement(text);
					}
				}
			} else if(unsortedVector.elementAt(0) instanceof DefaultMutableTreeNode) {	// FOR CODE GROUP TREE
				for(Enumeration e = unsortedVector.elements();e.hasMoreElements();) {
					DefaultMutableTreeNode node = (DefaultMutableTreeNode)e.nextElement();
					Object obj = node.getUserObject();
					if (obj instanceof CheckNode) {
						CheckNode check = (CheckNode)obj;					
						Object data = check.getData();
						if (data instanceof Code)
							unsortedVector2.addElement( ((Code)data).getName() );
						else if (data instanceof Vector) {
							Vector checkdata = (Vector)data;
							unsortedVector2.addElement((String)checkdata.elementAt(1));						
						}
					} 
				}
			}
			else {
				return unsortedVector;
			}
		}
		else {
			return unsortedVector;
		}

		Object[] sa = new Object[unsortedVector2.size()];
		unsortedVector2.copyInto(sa);
		java.util.List l = Arrays.asList(sa);

		Collections.sort(l, new Comparator() {
			public int compare(Object o1, Object o2) {

				String s1 = (String)o1;
				if (s1 == null)
					s1 = ""; //$NON-NLS-1$

				String s2 = (String)o2;
				if (s2 == null)
					s2 = ""; //$NON-NLS-1$

				String s1L = s1.toLowerCase();
		        String s2L = s2.toLowerCase();
				return  (s1L.compareTo(s2L));
			}
		});

	 	// add sorted elements from list to vector
	 	for (Iterator it = l.iterator(); it.hasNext(); ) {
			String sortedElement = (String) it.next();

			// add it to vector rearranged with the objects
			for(Enumeration e = unsortedVector.elements();e.hasMoreElements();) {

				Object pcobject = (Object)e.nextElement();

				String text = ""; //$NON-NLS-1$
				if (pcobject instanceof UINode) {
					text = ((UINode)pcobject).getText();
				} else if (pcobject instanceof DefaultMutableTreeNode) { // FOR CODE GROUP TREE
					DefaultMutableTreeNode node = (DefaultMutableTreeNode)pcobject;
					Object obj = node.getUserObject();
					if (obj instanceof CheckNode) {
						CheckNode check = (CheckNode)obj;
						Object data = check.getData();
						if (data instanceof Code)
							text =  ((Code)data).getName();
						else if (data instanceof Vector) {
							Vector checkdata = (Vector)data;
							text = (String)checkdata.elementAt(1);						
						}												
					}
				}
				if (text.equals(sortedElement)) {
					sortedVector.addElement(pcobject);

					//remove this element so it can't be found again in case there
					//is more than one object with the same text.
					unsortedVector.removeElement(pcobject);
					break;
				}
			}
		 }

		return sortedVector;
	}

	/**
	 * Sort the passed Vector of references Nodes into type order and
	 * create a JMenuItem for each node which will launch the given external reference.
	 *
	 * @param Vector refNode, a Vector of reference NodeSummary objects.
	 * @return a Vector of JMenuItems sorted by reference type.
	 */
	public static Vector sortReferences(Vector refNodes) {

		Vector sortedRefs = new Vector(51);
		Vector group = null;
		Hashtable groups = new Hashtable(51);
		
		int count = refNodes.size();
		int i=0;
		for (i=0; i<count; i++) {
			NodeSummary node = (NodeSummary)refNodes.elementAt(i);
			String source = node.getSource();

			if (source != null && !source.equals("")) { //$NON-NLS-1$
				String label = source;
				if (CoreUtilities.isFile(source)) {
					File file = new File(source);
					label = file.getName();
				}

				final String path = source;
				JMenuItem miMenuItem = new JMenuItem(label, UIImages.getSmallReferenceIcon(source));
					miMenuItem.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent evt) {
							if (path.startsWith("http:") || path.startsWith("https:") || path.startsWith("www.")) { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
								ExecuteControl.launch( path );
							} else if (path.startsWith(ICoreConstants.sINTERNAL_REFERENCE)) {
								String sPath = path.substring(ICoreConstants.sINTERNAL_REFERENCE.length());
								int ind = sPath.indexOf("/"); //$NON-NLS-1$
								if (ind != -1) {
									String sGoToViewID = sPath.substring(0, ind);
									String sGoToNodeID = sPath.substring(ind+1);	
									UIViewFrame frame = ProjectCompendium.APP.getCurrentFrame();
									Vector history = frame.getNavigationHistory();
									UIUtilities.jumpToNode(sGoToViewID, sGoToNodeID, history);
								}
							} else {
								File file = new File(path);
								String sPath = path;
								if (file.exists()) {
									sPath = file.getAbsolutePath();
								}
								// It the reference is not a file, just pass the path as is, as it is probably a special type of url.
								ExecuteControl.launch( sPath );
							}
						}
				});

				String sType = LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "UIUtilities.unknown"); //$NON-NLS-1$
			    if (source != null) {
			    	sType = UIReferenceNodeManager.getReferenceTypeName(source);
					if (groups.containsKey(sType)) {
						group = (Vector)groups.get(sType);
						group.addElement(miMenuItem);
						groups.put(sType, group);
					} else {
						group = new Vector();
						group.addElement(miMenuItem);
						groups.put(sType, group);
					}
			    }
			}
		}

		int counti = 0;
		String sKey = ""; //$NON-NLS-1$
		for (Enumeration e = groups.elements(); e.hasMoreElements();) {
			group = (Vector)e.nextElement();
			counti = group.size();
			for (i=0; i<counti;i++) {
				sortedRefs.addElement(group.elementAt(i));
			}
		}
	    return sortedRefs;
	}

	/**
	 * Remove the menu shortcut Mnemonics from the given MenuElement and its sub MenuElements.
	 */
	public static void removeMenuMnemonics(MenuElement[] elements) {

		for (int i=0; i<elements.length; i++) {
			MenuElement element = elements[i];

			if (element instanceof JMenuItem) {
				JMenuItem item = (JMenuItem)element;
				item.setMnemonic(-1);
			}
			removeMenuMnemonics(element.getSubElements());
		}
	}

	/**
	 * Return a SimpleDateFormat instance for the given pattern
	 * @param pattern, the pattern to return the SimpleDateFormat for.
	 * @return SimpleDateFormat.
	 */
	public static SimpleDateFormat getSimpleDateFormat(String pattern) {

		Calendar cal = Calendar.getInstance();
		java.util.Date date = cal.getTime();

		SimpleDateFormat sdf =
					  (SimpleDateFormat)DateFormat.getDateTimeInstance(DateFormat.LONG,
						   DateFormat.LONG);

		sdf.applyPattern(pattern);
		String result = sdf.format(date);

		return sdf;
   	}

	/**
	 * Create a new node of the given type, offset to the right of the given node.
	 *
	 * @param UINode uinode, the node to create the new node to the right of.
	 *			This node must be in a view so that a call to getViewPane works.
	 * @param int nType, the type of the new node to create.
	 * @param int offSet, the offset the new node should be to the right of the current node.
	 * @param String sText, the text to put in the label of the new node.
	 * @param String sAuthor, the author of this node.
	 *
	 * @return the new uinode object, or null if something failed.
	 */
	public static UINode createNodeAndLinkRight(UINode uinode, int nType, int offSet, String sText, String sAuthor) {
		return createNodeAndLink(DIRECTION_RIGHT, uinode, nType, offSet, sText, sAuthor, getLinkType(nType));
	}
		
	/**
	 * Create a new node of the given type, offset to the left of the given node.
	 *
	 * @param uinode the node to create the new node to the right of.
	 *			This node must be in a view so that a call to getViewPane works.
	 * @param nType the type of the new node to create.
	 * @param offSet the offset the new node should be to the right of the current node.
	 * @param sText the text to put in the label of the new node.
	 * @param sAuthor the author of this node.
	 *
	 * @return the new uinode object, or null if something failed.
	 */
	public static UINode createNodeAndLinkLeft(UINode uinode, int nType, int offSet,
				String sText, String sAuthor) {
		
		return createNodeAndLink(DIRECTION_LEFT, uinode, nType, offSet, sText, sAuthor, getLinkType(nType));
	}

	/**
	 * Create a new node of the given type, offset up from the given node.
	 *
	 * @param uinode the node to create the new node to the right of.
	 *			This node must be in a view so that a call to getViewPane works.
	 * @param nType the type of the new node to create.
	 * @param offSet the offset the new node should be to the right of the current node.
	 * @param sText the text to put in the label of the new node.
	 * @param sAuthor the author of this node.
	 *
	 * @return the new uinode object, or null if something failed.
	 */
	public static UINode createNodeAndLinkUp(UINode uinode, int nType, int offSet,
				String sText, String sAuthor) {
		
		return createNodeAndLink(DIRECTION_UP, uinode, nType, offSet, sText, sAuthor, getLinkType(nType));
	}

	/**
	 * Create a new node of the given type, offset down from the given node.
	 *
	 * @param uinode the node to create the new node to the right of.
	 *			This node must be in a view so that a call to getViewPane works.
	 * @param nType the type of the new node to create.
	 * @param offSet the offset the new node should be to the right of the current node.
	 * @param sText the text to put in the label of the new node.
	 * @param sAuthor the author of this node.
	 *
	 * @return the new uinode object, or null if something failed.
	 */
	public static UINode createNodeAndLinkDown(UINode uinode, int nType, int offSet,
				String sText, String sAuthor) {
		
		return createNodeAndLink(DIRECTION_DOWN, uinode, nType, offSet, sText, sAuthor, getLinkType(nType));
	}

	/**
	 * Create a new node of the given type, offset in the direction specified, 
	 * by the amount specified, from the given node.
	 *
	 * @param iDirection the direction to create the node and link 
	 * (DIRECTION_RIGHT,DIRECITON_LEFT, DIRECTION_UP, DIRECTION_DOWN) 
	 * @param uinode the node to create the new node to the right of.
	 *			This node must be in a view so that a call to getViewPane works.
	 * @param nType the type of the new node to create.
	 * @param offSet the offset the new node should be to the right of the current node.
	 * @param sText the text to put in the label of the new node.
	 * @param sAuthor the author of this node.
	 * @param sLinkType the link type to use for the link.
	 *
	 * @return the new uinode object, or null if something failed.
	 */
	public static UINode createNodeAndLink(int iDirection, UINode uinode, int nType, int offSet,
					String sText, String sAuthor, String sLinkType) {
		
		UINode newNode = null;
		UIViewPane oViewPane = uinode.getViewPane();

		if (oViewPane != null) {
			ViewPaneUI oViewPaneUI = oViewPane.getUI();
			if (oViewPaneUI != null) {

				Point loc = uinode.getNodePosition().getPos();
				
				// CREATE NEW NODE RIGHT OF THE GIVEN NODE WITH THE GIVEN LABEL
				newNode = oViewPaneUI.createNode(nType,
								 "", //$NON-NLS-1$
								 sAuthor,
								 sText,
								 "", //$NON-NLS-1$
								 loc.x,
								 loc.y
								 );				
				
				newNode.requestFocus();
				
				int parentHeight = uinode.getBounds().height;
				int parentWidth = uinode.getBounds().width;				
				int newWidth = newNode.getBounds().width;
				int newHeight = newNode.getBounds().height;
				int newX = loc.x;
				int newY = loc.y;
				if (parentWidth > newWidth) {
					newX = loc.x+((parentWidth-newWidth)/2);
				} else if (parentWidth < newWidth) {
					newX = loc.x-((newWidth-parentWidth)/2);
				}
				if (parentHeight > newHeight) {
					newY = loc.y+((parentHeight-newHeight)/2);
				} else if (parentWidth < newWidth) {
					newY = loc.y-((newHeight-parentHeight)/2);
				}
				
				if (iDirection == DIRECTION_RIGHT) {
					loc.x = loc.x + parentWidth + offSet;
					loc.y = newY;
				} else if (iDirection == DIRECTION_LEFT) {
					if (loc.x < (offSet+newWidth)) {
						loc.x=0;
					} else {
						loc.x -= (offSet+newWidth);
					}
					loc.y = newY;
				} else if (iDirection == DIRECTION_UP) {
					if (loc.y < (offSet+newHeight)) {
						loc.y=0;
					} else {
						loc.y = loc.y-(offSet+newHeight);
					}
					loc.x = newX;
				} else if (iDirection == DIRECTION_DOWN) {
					loc.y = loc.y+parentHeight+offSet;
					loc.x = newX;
				}
								
				(newNode.getNodePosition()).setPos(loc);
				try {
					oViewPane.getView().setNodePosition(newNode.getNode().getId(), loc);
				}
				catch(Exception ex) {
					System.out.println(ex.getMessage());
				}

				newNode.setLocation(loc);
				
				LinkProperties props = getLinkProperties(sLinkType);
				props.setArrowType(ICoreConstants.ARROW_TO);
				oViewPaneUI.createLink(newNode, uinode, sLinkType, props);
			}			
		}

		return newNode;
	}	
	
	/**
	 * Work out the link type from the node type (and for Argument nodes the drag start position->type).
	 * @param uinode com.compendium.ui.UINode, the node to work out the link type for.
	 */
	public static String getLinkType(UINode uinode) {
		int nodeType = uinode.getType();

		UILinkGroup group = ProjectCompendium.APP.oLinkGroupManager.getLinkGroup(ProjectCompendium.APP.getActiveLinkGroup());

		if (group == null || group.getID().equals("1")) { //$NON-NLS-1$
			if ( nodeType == ICoreConstants.CON || nodeType == ICoreConstants.CON_SHORTCUT) {
				return ICoreConstants.OBJECTS_TO_LINK;
			} else if (nodeType == ICoreConstants.PRO || nodeType == ICoreConstants.PRO) {
				return ICoreConstants.SUPPORTS_LINK;
			} else if (nodeType == ICoreConstants.ARGUMENT || nodeType == ICoreConstants.ARGUMENT_SHORTCUT) {
				if (uinode.getUI().isPlus)
					return ICoreConstants.SUPPORTS_LINK;
				else if (uinode.getUI().isMinus)
					return ICoreConstants.OBJECTS_TO_LINK;
				else {
					if (group != null) {
						String sDefaultLinkType = group.getDefaultLinkTypeID();
						if (!sDefaultLinkType.equals("")) { //$NON-NLS-1$
							return sDefaultLinkType;
						}
					} 
				}
			} else {
				if (group != null) {
					String sDefaultLinkType = group.getDefaultLinkTypeID();
					if (!sDefaultLinkType.equals("")) { //$NON-NLS-1$
						return sDefaultLinkType;
					}
				} 		
			}
		}
		else {
			String sDefaultLinkType = group.getDefaultLinkTypeID();
			if (!sDefaultLinkType.equals("")) { //$NON-NLS-1$
				return sDefaultLinkType;
			}
		}

		return ICoreConstants.DEFAULT_LINK;
	}
		
	/**
	 * Work out the link type from the node type.
	 * @param uinode com.compendium.ui.UINode, the node to work out the link type for.
	 */
	public static String getLinkType(int nodeType) {

		UILinkGroup group = ProjectCompendium.APP.oLinkGroupManager.getLinkGroup(ProjectCompendium.APP.getActiveLinkGroup());

		if (group == null || group.getID().equals("1")) { //$NON-NLS-1$
			if ( nodeType == ICoreConstants.CON || nodeType == ICoreConstants.CON_SHORTCUT) {
				return ICoreConstants.OBJECTS_TO_LINK;
			} else if (nodeType == ICoreConstants.PRO || nodeType == ICoreConstants.PRO) {
				return ICoreConstants.SUPPORTS_LINK;
			} else {
				if (group != null) {
					String sDefaultLinkType = group.getDefaultLinkTypeID();
					if (!sDefaultLinkType.equals("")) { //$NON-NLS-1$
						return sDefaultLinkType;
					}
				} 		
			}
		}
		else {
			String sDefaultLinkType = group.getDefaultLinkTypeID();
			if (!sDefaultLinkType.equals("")) { //$NON-NLS-1$
				return sDefaultLinkType;
			}
		}

		return ICoreConstants.DEFAULT_LINK;
	}		
	
	/**
	 * Work out the default link formatting properties from the passed link type.
	 * @param LinkProperties the default link properties for the given.
	 */
	public static LinkProperties getLinkProperties(String sLinkType) {
		LinkProperties props = new LinkProperties();
		
		UILinkType oLinkType = ProjectCompendium.APP.oLinkGroupManager.getLinkType(sLinkType);
		// If no default link set, use model defaults.
		if (oLinkType == null) {
			props.setArrowType(ICoreConstants.ARROW_TO);
			props.setLinkStyle(ICoreConstants.STRAIGHT_LINK);
			props.setLinkDashed(ICoreConstants.PLAIN_LINE);
			props.setLinkColour((UILink.getLinkColor(sLinkType)).getRGB());
			props.setLinkWeight(1); 
			props.setBackground((Model.BACKGROUND_DEFAULT).getRGB());
			props.setForeground((Model.FOREGROUND_DEFAULT).getRGB());
			props.setFontFace(Model.FONTFACE_DEFAULT);
			props.setFontStyle(Model.FONTSTYLE_DEFAULT);
			props.setFontSize(Model.FONTSIZE_DEFAULT);
			props.setLabelWrapWidth(Model.LABEL_WRAP_WIDTH_DEFAULT);
		} else {
			props.setArrowType(oLinkType.getArrowType());
			props.setLinkStyle(oLinkType.getLinkStyle());
			props.setLinkDashed(oLinkType.getLinkDashed());
			props.setLinkColour(oLinkType.getColour().getRGB());
			props.setLinkWeight(oLinkType.getLinkWeight()); 
			props.setBackground(oLinkType.getLinkLabelBackground());
			props.setForeground(oLinkType.getLinkLabelForeground());
			props.setFontFace(oLinkType.getFontFace());
			props.setFontStyle(oLinkType.getFontStyle());
			props.setFontSize(oLinkType.getFontSize());
			props.setLabelWrapWidth(oLinkType.getLabelWrapWidth());
		}
		
		return props;
	}
	
	/**
	 * Transform the given x and y positions to the given scale and return them as a Point.
	 * @param x the x position to scale.
	 * @param y the y position to scale.
	 * @param scale the scale to use.
	 * @return Point the scaled version of the given x and y positions.
	 */
	public static Point transformPoint(int x, int y, double scale) {

		Point p1=new Point(x, y);
		if ( (scale == 0.0 && scale == 1.0) )
			return p1;

		Point p2=new Point(0, 0);
		AffineTransform af=new AffineTransform();
		af.setToScale(scale, scale);

		try {
			p2=(Point)af.transform(p1, p2);
		}
		catch (Exception excp) {
			System.out.println("UIUtilites.transformPoint:failed to transform"); //$NON-NLS-1$
		}

		return p2;
	}

	/**
	 * Scale (inverse transform), the given x and y positions and return them as a Point.
	 * @param x the x position to scale.
	 * @param y the y position to scale.
	 * @param scale the scale to use.
	 * @return Point the scaled version of the given x and y positions.
	 */
	public static Point scalePoint(int x, int y, double scale) {

		Point p1=new Point(x, y);
		if ( (scale == 0.0 && scale == 1.0) )
			return p1;

		Point p2=new Point(0, 0);

		AffineTransform af=new AffineTransform();
		af.setToScale(scale, scale);

		try {
			p2=(Point)af.inverseTransform(p1, p2);
		}
		catch (Exception excp) {
			System.out.println("UIUtilites.scaleCoordinates:failed to inverse transform"); //$NON-NLS-1$
		}

		return p2;
	}

	/**
	 * Jump to the Given Node in the Given View and focus it.
	 *
	 * @param sViewID the id of the view to opne.
	 * @param sNodeID the id of the node to focus on.
	 */
	public static void jumpToNode(String sViewID, String sNodeID, String sNavigationHistory) {
		Vector history = new Vector();
		history.addElement(sNavigationHistory);
		jumpToNode(sViewID, sNodeID, history);
	}
	
	/**
	 * Jump to the Given Node in the Given View and focus it.
	 *
	 * @param sViewID the id of the view to opne.
	 * @param sNodeID the id of the node to focus on.
	 */
	public static void jumpToNode(String sViewID, String sNodeID, Vector vtNavigationHistory) {
		
		IModel model = ProjectCompendium.APP.getModel();
		View view = null;
		try {
			view = (View)model.getNodeService().getView(model.getSession(), sViewID);
		} catch (Exception ex) {}
		if (view == null) {
			ProjectCompendium.APP.displayError(LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "UIUtilities.viewNotFound")); //$NON-NLS-1$
			return;
		}

		view.initialize(model.getSession(), model);

		UIViewFrame oViewFrame = ProjectCompendium.APP.addViewToDesktop(view, view.getLabel());
		oViewFrame.setNavigationHistory(vtNavigationHistory);

		NodeSummary node = null;
		if (oViewFrame instanceof UIMapViewFrame) {
			UINode uinode = (UINode)((UIMapViewFrame)oViewFrame).getViewPane().get(sNodeID);
			if (uinode != null) {
				node = uinode.getNode();
				focusNodeAndScroll(node, oViewFrame);
				//if(UIViewOutline.me != null){
					//UIViewOutline.me.expandPath(view, node);
				//}											
			} else {
				ProjectCompendium.APP.displayError(LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "UIUtilities.errorMessage1a")+"\n"+ //$NON-NLS-1$ //$NON-NLS-2$
						LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "UIUtilities.errorMessage1b")+"\n\n" + //$NON-NLS-1$ //$NON-NLS-2$
						LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "UIUtilities.errorMessage1c")+"\n"); //$NON-NLS-1$ //$NON-NLS-2$
			}
		} else {
			UIListViewFrame oListViewFrame = (UIListViewFrame)oViewFrame;
			UIList oUIList = oListViewFrame.getUIList();
			NodePosition pos = oUIList.getNode(sNodeID);
			if (pos != null) {
				node = pos.getNode();
				focusNodeAndScroll(node, oViewFrame);
				//if(UIViewOutline.me != null) {
					//UIViewOutline.me.expandPath(view, node);
				//}											
			} else {
				ProjectCompendium.APP.displayError(LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "UIUtilities.errorMessage1a")+"\n"+ //$NON-NLS-1$ //$NON-NLS-2$
						LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "UIUtilities.errorMessage1b")+"\n\n" + //$NON-NLS-1$ //$NON-NLS-2$
						LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "UIUtilities.errorMessage1c")+"\n"); //$NON-NLS-1$ //$NON-NLS-2$
			}			
		}						
	}
	
	/**
	 * Focus the given node in the given view and scroll so it is visible
	 * @param oNode the node to focus in the view.
	 * @param oViewFrame the view to focus the node in.
	 */
	public static void focusNodeAndScroll(NodeSummary oNode, UIViewFrame oViewFrame) {
		if (oViewFrame instanceof UIMovieMapViewFrame) {
			UIMovieMapViewFrame frame = (UIMovieMapViewFrame)oViewFrame;
			frame.jumpToNode(oNode.getId());
			
			UIMovieMapViewPane oPane = (UIMovieMapViewPane)frame.getViewPane();			
			UINode oUINode = (UINode) oPane.get(oNode.getId());
			if (oUINode != null) {
				Rectangle rect = frame.getVisibleRect();
				//pane has height of 4 at this point so using frame.
				//divider at 75% of pane - so guessing 70% of frame is about the same
				int newheight  = new Float(rect.height * 0.70f).intValue(); 
				rect.height = newheight;
				Rectangle noderect = oUINode.getBounds();
				if (!rect.contains(noderect)) {
					oUINode.scrollRectToVisible(new Rectangle(0,0, noderect.width, noderect.height));
				}
				oPane.setSelectedNode(null, ICoreConstants.DESELECTALL);
				oUINode.setRollover(false);
				oUINode.setSelected(true);
				oPane.setSelectedNode(oUINode,ICoreConstants.SINGLESELECT);
				oUINode.moveToFront();
			}
		} else if (oViewFrame instanceof UIMapViewFrame) {
			UIViewPane oPane = ((UIMapViewFrame)oViewFrame).getViewPane();
			UINode oUINode = (UINode) oPane.get(oNode.getId());
			if (oUINode != null) {
				oPane.scrollRectToVisible( oUINode.getBounds() );
				oPane.setSelectedNode(null, ICoreConstants.DESELECTALL);
				oUINode.setRollover(false);
				oUINode.setSelected(true);
				oPane.setSelectedNode(oUINode,ICoreConstants.SINGLESELECT);
				oUINode.moveToFront();
			}
		} else {
			UIListViewFrame oListViewFrame = (UIListViewFrame)oViewFrame;
			UIList oUIList = oListViewFrame.getUIList();
			oUIList.selectNode(oUIList.getIndexOf(oNode), ICoreConstants.SINGLESELECT);
		}
	}
	
	/**
	 * Modify a given path so that file:// and linkedFile:// URIs are
	 * handled correctly
	 * @author Sebastian Ehrich
	 * @param path
	 * @return the modified path
	 */
	public static String modifyPath(String path)
	{
		if (!ProjectCompendium.isWindows) return path;
		if (LinkedFileDatabase.isDatabaseURI(path))
		{
			return path;
		}
		else if (path.startsWith("file:")) //$NON-NLS-1$
		{
//			Windows does not execute file:// ... and filenames with %20 in it
//			via 'start' - so replace that			
			String newPath = path.replaceFirst("file:/*", ""); //$NON-NLS-1$ //$NON-NLS-2$
			newPath = newPath.replace("/", ProjectCompendium.sFS); //$NON-NLS-1$
			newPath = newPath.replace("%20", " "); //$NON-NLS-1$ //$NON-NLS-2$
			return newPath;
		}
		else
			return path;
	}
	
	
	/*private void applyOptionPaneBackground(JOptionPane optionPane, Color color) {
	      optionPane.setBackground(color);
	      for (Iterator i = getComponents(optionPane).iterator(); i.hasNext(); ) {
	        Component comp = (Component)i.next();
	        if (comp instanceof JPanel) {
	          comp.setBackground(color);
	        }
	      }
	}

	private Collection getComponents(Container container) {
	      Collection components = new Vector();
	      Component[] comp = container.getComponents();
	      for (int i = 0, n = comp.length; i < n; i++) {
	        components.add(comp[i]);
	        if (comp[i] instanceof Container) {
	          components.addAll(getComponents((Container) comp[i]));
	        }
	      }
	      return components;
	}*/	
}
