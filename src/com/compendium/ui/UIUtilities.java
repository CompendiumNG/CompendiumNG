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


package com.compendium.ui;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.text.*;
import java.awt.geom.*;
import java.util.*;
import java.util.zip.*;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;

import com.compendium.*;
import com.compendium.ui.linkgroups.UILinkGroup;
import com.compendium.ui.plaf.*;
import com.compendium.core.*;
import com.compendium.core.datamodel.*;
import com.compendium.core.db.*;
import com.compendium.ui.tags.*;

/**
 * UIUtilities contains methods to help ui classes
 *
 * @author	Michelle Bachler
 */
public class UIUtilities {

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

		if (nChildWidth < nParentWidth) {
			child.setLocation(
					(nParentWidth - nChildWidth) / 2 + nParentX, (nParentHeight - nChildHeight) / 2 + nParentY);
		}
		else {
			child.setLocation(0, 0);
		}
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

		ZipFile zipFile = new ZipFile(sFilePath);
		Enumeration entries = zipFile.entries();
		ZipEntry entry = null;
		String sXMLFile = "";
		String sTemp = "";

		while(entries.hasMoreElements()) {

			entry = (ZipEntry)entries.nextElement();
			sTemp = entry.getName();
			if (sTemp.endsWith(".xml") && sTemp.startsWith("Exports")) {
				sXMLFile = sTemp;
			}

			//System.out.println("Extracting file: "+sTemp);

			// AVOID Thumbs.db files
			if (sTemp.endsWith(".db")) {
				continue;
			}

			int len = 0;
			byte[] buffer = new byte[1024];
			InputStream in = zipFile.getInputStream(entry);
            File file = new File(entry.getName());
            
            if (file.getParentFile() != null) {
            	file.getParentFile().mkdirs();
            }
            
			OutputStream out = new BufferedOutputStream(new FileOutputStream(entry.getName()));
			while((len = in.read(buffer)) >=0) {
				out.write(buffer, 0, len);
			}
			in.close();
			out.close();
		}

		zipFile.close();

		// IMPORT THE XML
		if (!sXMLFile.equals("")) {
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

					File oXMLFile2 = new File(sXMLFile);
					if (oXMLFile2.exists()) {
						DBNode.setImportAsTranscluded(transclude);
						DBNode.setPreserveImportedIds(preserveIDs);
						DBNode.setUpdateTranscludedNodes(updateTranscludedNodes);

						UIViewFrame frame = ProjectCompendium.APP.getCurrentFrame();
						if (frame instanceof UIMapViewFrame) {
							UIMapViewFrame mapFrame = (UIMapViewFrame)frame;
							UIViewPane oViewPane = mapFrame.getViewPane();
							ViewPaneUI oViewPaneUI = oViewPane.getUI();
							if (oViewPaneUI != null) {
								oViewPaneUI.setSmartImport(importAuthorAndDate);
								oViewPaneUI.onImportXMLFile(sXMLFile, includeOriginalAuthorDate);
							}
						} else {
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
		}
		return false;
	}

	/**
	 * Check whether the user has asked to copy DnD files to the Linked Files folder or not.
	 * If they have, copy the file.
	 *
	 * @param File file, the file to copy to the Linked Files folder.
	 */
	public static File checkCopyLinkedFile(File file) {

		String path = file.getPath();

		if (path.startsWith("www.") || path.startsWith("http:") || path.startsWith("https:"))
			return null;

		if (FormatProperties.dndFiles.equals("on")) {
			return copyDnDFile(file);
		}
		else if (FormatProperties.dndFiles.equals("prompt")) {

			int response = JOptionPane.showConfirmDialog(ProjectCompendium.APP, "Do you want to copy\n\n"+file.getAbsolutePath()+"\n\ninto the 'Linked Files' directory?\n\n",
														"External Drag and Drop", JOptionPane.YES_NO_OPTION);

			if (response == JOptionPane.YES_OPTION)
				return copyDnDFile(file);
		}

		return file;
	}

	/**
	 * Copy the given file to the Linked Files folder, into a subfolder with the current database name.
	 *
	 * @param File file, the file to copy to the Linked Files folder.
	 */
	private static File copyDnDFile(File file) {

		File newFile = null;

		String sDatabaseName = ProjectCompendium.APP.sFriendlyName;		
		UserProfile oUser = ProjectCompendium.APP.getModel().getUserProfile();
		String sUserDir = CoreUtilities.cleanFileName(oUser.getUserName())+"_"+oUser.getId();
		
		try {
			String sFullPath = "Linked Files"+ProjectCompendium.sFS+CoreUtilities.cleanFileName(sDatabaseName)+ProjectCompendium.sFS+sUserDir;			
			File directory = new File(sFullPath);
			if (!directory.isDirectory()) {
				directory.mkdirs();
			}
			
			String sPATH = sFullPath+ProjectCompendium.sFS;

			FileInputStream stream = new FileInputStream(file);
			byte b[] = new byte[stream.available()];
			stream.read(b);
			stream.close();

			newFile = new File(sPATH+file.getName());
			if (newFile.exists()) {

				int response = JOptionPane.showConfirmDialog(ProjectCompendium.APP, "A file with this name already exists in the 'Linked Files' directory.\nDo you wish to rename it before saving?\n\n(if you do not rename it, the existing file will be replaced)\n\n",
														"External Drag and Drop", JOptionPane.YES_NO_OPTION);

				if (response == JOptionPane.YES_OPTION) {
					FileDialog fileDialog = new FileDialog(ProjectCompendium.APP, "Change the file name to...",
											   FileDialog.SAVE);

					fileDialog.setFile(sPATH+file.getName());
					UIUtilities.centerComponent(fileDialog, ProjectCompendium.APP);
					fileDialog.setVisible(true);

					String fileName = fileDialog.getFile();

					if (fileName != null) {
						newFile = new File(sPATH+fileName);
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
			ProjectCompendium.APP.displayError("Exception: (UIUtilities.copyDnDFile)\n\nUnable to create Linked Files subfolders for this database and user\n\n" + e.getMessage());
		}
		catch (IOException e) {
		    e.printStackTrace();
			ProjectCompendium.APP.displayError("Exception: (UIUtilities.copyDnDFile)\n\n" + e.getMessage());
		}

		return newFile;
	}

	/**
	 * Sort the given Vector of objects, depending on the object type.
	 * Types currently accepted are: UINode,
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
					s1 = "";

				String s2 = (String)o2;
				if (s2 == null)
					s2 = "";

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

				String text = "";
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

			if (source != null && !source.equals("")) {
				String label = source;
				if (CoreUtilities.isFile(source)) {
					File file = new File(source);
					label = file.getName();
				}

				final String path = source;
				JMenuItem miMenuItem = new JMenuItem(label, UIImages.getSmallReferenceIcon(source));
					miMenuItem.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent evt) {
							if (path.startsWith("http:") || path.startsWith("https:") || path.startsWith("www.")) {
								ExecuteControl.launch( path );
							} else if (path.startsWith(ICoreConstants.sINTERNAL_REFERENCE)) {
								String sPath = path.substring(ICoreConstants.sINTERNAL_REFERENCE.length());
								int ind = sPath.indexOf("/");
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

				String sType = "Unknown";
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
		String sKey = "";
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
		return createNodeAndLinkRight(uinode, nType, offSet, sText, sAuthor, getLinkType(uinode));
	}
		
	/**
	 * Create a new node of the given type, offset to the right of the given node.
	 *
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
	public static UINode createNodeAndLinkRight(UINode uinode, int nType, int offSet,
				String sText, String sAuthor, String sLinkType) {

		double scale = uinode.getScale();

		UINode newNode = null;

		UIViewPane oViewPane = uinode.getViewPane();

		// Do all calculations at 100% scale and then scale back down if required.
		if (oViewPane != null) {
			if (scale != 1.0) {
				oViewPane.scaleNode(uinode, 1.0);
			}

			ViewPaneUI oViewPaneUI = oViewPane.getViewPaneUI();
			if (oViewPaneUI != null) {

				int parentHeight = uinode.getHeight();
				int parentWidth = uinode.getWidth();

				Point loc = uinode.getNodePosition().getPos();
				loc.x += parentWidth;
				loc.x += offSet;

				// CREATE NEW NODE RIGHT OF THE GIVEN NODE WITH THE GIVEN LABEL
				newNode = oViewPaneUI.createNode(nType,
								 "",
								 sAuthor,
								 sText,
								 "",
								 loc.x,
								 loc.y
								 );

				if (scale != 1.0) {
					oViewPane.scaleNode(newNode, 1.0);
				}

				//Adjust y location for height variation so new node centered.
				int childHeight = newNode.getHeight();

				int locy = 0;
				if (parentHeight > childHeight) {
					locy = loc.y + ((parentHeight-childHeight)/2);
				}
				else if (childHeight > parentHeight) {
					locy = loc.y - ((childHeight-parentHeight)/2);
				}

				if (locy > 0 && locy != loc.y) {
					loc.y = locy;
					(newNode.getNodePosition()).setPos(loc);
					try {
						oViewPane.getView().setNodePosition(newNode.getNode().getId(), loc);
					}
					catch(Exception ex) {
						System.out.println(ex.getMessage());
					}
				}

				if (scale != 1.0) {
					oViewPane.scaleNode(newNode, scale);
				}

				oViewPaneUI.createLink(newNode, uinode, sLinkType, ICoreConstants.ARROW_TO);
			}			

			if (scale != 1.0) {
				oViewPane.scaleNode(uinode, scale);
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

		if (group == null || group.getID().equals("1")) {
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
						if (!sDefaultLinkType.equals("")) {
							return sDefaultLinkType;
						}
					} 
				}
			} else {
				if (group != null) {
					String sDefaultLinkType = group.getDefaultLinkTypeID();
					if (!sDefaultLinkType.equals("")) {
						return sDefaultLinkType;
					}
				} 		
			}
		}
		else {
			String sDefaultLinkType = group.getDefaultLinkTypeID();
			if (!sDefaultLinkType.equals("")) {
				return sDefaultLinkType;
			}
		}

		return ICoreConstants.DEFAULT_LINK;
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
			System.out.println("UIUtilites.transformPoint:failed to transform");
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
			System.out.println("UIUtilites.scaleCoordinates:failed to inverse transform");
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
			ProjectCompendium.APP.displayError("Could not find requested View in this project.");
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
				ProjectCompendium.APP.displayError("Could not find the requested node in this view.\nIt may have been moved or deleted from the view since the reference was created.\n\nSearch may find the node in its new location.\n");				
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
				ProjectCompendium.APP.displayError("Could not find the requested Node in this view.\nIt may have been moved or deleted from the view since the reference was created.\n\nSearch may find the node in its new location.\n");
			}			
		}						
	}
	
	/**
	 * Focus the given node in the given view and scroll so it is visible
	 * @param oNode the node to focus in the view.
	 * @param oViewFrame the view to focus the node in.
	 */
	public static void focusNodeAndScroll(NodeSummary oNode, UIViewFrame oViewFrame) {
		if (oViewFrame instanceof UIMapViewFrame) {
			UIViewPane oPane = ((UIMapViewFrame)oViewFrame).getViewPane();
			UINode oUINode = (UINode) oPane.get(oNode.getId());
			if (oUINode != null) {
				JViewport viewport = oViewFrame.getViewport();
				Rectangle nodeBounds = oUINode.getBounds();
				Point parentPos = SwingUtilities.convertPoint((Component)oPane, nodeBounds.x, nodeBounds.y, viewport);
				viewport.scrollRectToVisible( new Rectangle( parentPos.x, parentPos.y, nodeBounds.width, nodeBounds.height ) );

				oUINode.setRollover(false);
				oUINode.setSelected(true);
				oPane.setSelectedNode(null, ICoreConstants.DESELECTALL);
				oPane.setSelectedNode(oUINode,ICoreConstants.SINGLESELECT);
				oUINode.moveToFront();
			}
		} else {
			UIListViewFrame oListViewFrame = (UIListViewFrame)oViewFrame;
			UIList oUIList = oListViewFrame.getUIList();
			oUIList.selectNode(oUIList.getIndexOf(oNode), ICoreConstants.SINGLESELECT);
		}
	}
}
