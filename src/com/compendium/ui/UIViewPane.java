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

import java.awt.print.*;

import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.dnd.*;
import java.awt.image.*;
import java.awt.geom.*;

import java.beans.*;
import java.sql.SQLException;
import java.util.*;
import java.io.*;

import javax.swing.*;
import javax.help.*;
import javax.imageio.*;

import com.compendium.ProjectCompendium;
import com.compendium.ui.plaf.*;
import com.compendium.ui.popups.*;
import com.compendium.ui.edits.*;
import com.compendium.ui.dialogs.*;
import com.compendium.ui.panels.*;
import com.compendium.ui.stencils.*;

import com.compendium.meeting.*;

import com.compendium.core.datamodel.*;
import com.compendium.core.CoreUtilities;
import com.compendium.core.ICoreConstants;

/**
 * This class is the main class that draws and handles Compendium maps and thier events.
 *
 * @author	Mohammed Sajid Ali / Michelle Bachler
 */
public class UIViewPane extends JLayeredPane implements PropertyChangeListener, DropTargetListener, Printable {

	/** The generated serial version id */
	private static final long serialVersionUID 		= -6997855860445477967L;

	/** view property name for use with property change events */
	public	final static String		VIEW_PROPERTY 	= "view";

	//place nodes on higher layers so that when mouse goes over it, rollover is enabled
	//by placing the link on the lower layer, its bounds doesnt override the node bounds
	//and the node can be selected even if it is inside link preferred bound range

	/** A reference to the layer to hold background images. */
	public final static Integer BACKGROUND_LAYER 	= new Integer(200);

	/** A reference to the layer to hold grid layout stuff NOT IMPLEMENTED YET.*/
	public final static	Integer	GRID_LAYER			= new Integer(250);

	/** A reference to the layer holding the scribble notes when moved to the back of the nodes.*/
	public final static Integer	SCRIBBLE_LAYER_BACK	= new Integer(260);

	/** A reference to the layer to hold links.*/
	public final static Integer	LINK_LAYER			= new Integer(350);

	/** A reference to the layer to hold nodes.*/
	public final static Integer	NODE_LAYER			= new Integer(400);

	/** A reference to the layer to hold the rollover hint popups.*/
	public final static Integer HINT_LAYER			= new Integer(450);

	/** A reference to the layer holding the scribble notes, when sitting infront of the nodes.*/
	public final static Integer	SCRIBBLE_LAYER		= new Integer(480);

	/** The current scaling resolution for this view.*/
	public double				currentScale		= 1.0;

	/** The previous scaling resolution for this view.*/
	public double				previousScale		= 1.0;

	/** The View object tha holds the data for this view.*/
	protected View			oView					= null;

	/** The parent view frame that holds this view.*/
	protected UIViewFrame	oViewFrame				= null;

	/** The UI object that paints this view.*/
	protected ViewPaneUI	oViewPaneUI				= null;

	/** The node last selected in this view.*/
	protected UINode		oNode					= null;

	/** A list off all currently selected nodes in this view.*/
	protected Vector		vtNodeSelected			= new Vector();

	/** The link last selected in this view.*/
	protected UILink		oLink					= null;

	/** A list of all links selected in this view.*/
	protected Vector		vtLinkSelected			= new Vector();

	/** The instance of the scribble pad class used in this view - CURRENTLY NOT IMPLEMENTED.*/
	protected UIScribblePad	oScribblePad			= null;

	/** Holds a list of currently displayed tag rollover popups  -CURRENTLY SHOULD BE ONLY ONE.*/
	protected Hashtable 	tagPopups 				= new Hashtable(51);

	/** Holds a list of currently displayed detail rollover popups  -CURRENTLY SHOULD BE ONLY ONE.*/
	protected Hashtable 	detailPopups 			= new Hashtable(51);

	/** Holds a list of currently displayed parent view rollover popups  -CURRENTLY SHOULD BE ONLY ONE.*/
	protected Hashtable 	viewsPopups 			= new Hashtable(51);

	/** Holds a list of currently displayed image rollover popups  -CURRENTLY SHOULD BE ONLY ONE.*/
	protected Hashtable 	imagePopups 			= new Hashtable(51);

	/** Holds a list of currently displayed label search rollover popups  -CURRENTLY SHOULD BE ONLY ONE.*/
	protected Hashtable 	labelPopups 			= new Hashtable(51);

	/** Holds a list of currently displayed node focus rollover popups  -CURRENTLY SHOULD BE ONLY ONE.*/
	protected Hashtable 	nodeFocusPopups			= new Hashtable(51);

	/** The offset to use when displaying rollover popups.*/
	protected int 			hintOffset 				= 20;

	/** The drop target instance associated with this vie.*/
	protected DropTarget 	dropTarget 				= null;

	/** The instance of the view right-click popup menu last accessed for this view.*/
	protected UIViewPopupMenu 		viewPopup 		= null;

	/** The label holding the background layer image.*/
	private JLabel 			lblBackgroundLabel		= null;

	/** The original title of the map.*/
	private String 			sTitle 					= "";

	/** The user name of the current user */
	private String 			sAuthor 				= "";
	
	/**
	 * Constructor. Creates and initializes a new instance of UIViewPane.
	 * @param view the view holding the data for this pane to dispaly.
	 * @param viewFrame the parent frame containing this view pane
	 */
	public UIViewPane(View view, UIViewFrame viewframe) {

		oViewFrame = viewframe;
		this.sAuthor = viewframe.getCurrentAuthor();
		
		if (oViewFrame != null) {
			sTitle = oViewFrame.getTitle();
		}

		view.addPropertyChangeListener(this);
		setView(view);

		ViewLayer oViewLayer = view.getViewLayer();
		if (oViewLayer != null) {
			String sBackground = oViewLayer.getBackground();
			if (!sBackground.equals(""))
				addBackground(sBackground);
		}

		CSH.setHelpIDString(this,"node.views");

		dropTarget = new DropTarget(this, this);

		updateUI();
	}
	
	/**
	 * Return the current user's author name.
	 * @return the current user's author name.
	 */
	public String getCurrentAuthor() {
		return sAuthor;
	}
	
	/**
     * Set the current zoom scale for this view pane.
 	 * @param zoom, the current zoom scale for this view pane.
     */
	public void setZoom(double zoom) {
		previousScale = currentScale;
		currentScale = zoom;
	}

	/**
     * Get the current zoom scale for this view pane.
	 * @return double, the current zoom scale for this view pane.
     */
	public double getZoom() {
		return currentScale;
	}

// DRAG AND DROP STUFF

    /**
     * Called if the user has modified
     * the current drop gesture.
     * <P>
	 * THIS METHOD DOES NOTHING HERE.
     * @param e the <code>DropTargetDragEvent</code>
     */
	public void dropActionChanged (DropTargetDragEvent dropTargetDragEvent){}

    /**
     * Called while a drag operation is ongoing, when the mouse pointer has
     * exited the operable part of the drop site for the
     * <code>DropTarget</code> registered with this listener.
	 * THIS METHOD DOES NOTHING HERE.
     *
     * @param e the <code>DropTargetEvent</code>
     */
	public void dragExit(DropTargetEvent e) {}

    /**
     * Called when a drag operation is ongoing, while the mouse pointer is still
     * over the operable part of the drop site for the <code>DropTarget</code>
     * registered with this listener.
	 * THIS METHOD DOES NOTHING HERE.
     *
     * @param e the <code>DropTargetDragEvent</code>
     */
	public void dragOver(DropTargetDragEvent e) {}

    /**
     * Called while a drag operation is ongoing, when the mouse pointer enters
     * the operable part of the drop site for the <code>DropTarget</code>
     * registered with this listener. Accepts COPY_ACTION drags.
     *
     * @param e the <code>DropTargetDragEvent</code>
     */
	public void dragEnter(DropTargetDragEvent e) {
		e.acceptDrag(DnDConstants.ACTION_COPY);
	}

    /**
     * Called when the drag operation has terminated with a drop on
     * the operable part of the drop site for the <code>DropTarget</code>
     * registered with this listener.
     * <p>
     * This method is responsible for undertaking
     * the transfer of the data associated with the
     * gesture. The <code>DropTargetDropEvent</code>
     * provides a means to obtain a <code>Transferable</code>
     * object that represents the data object(s) to
     * be transfered.<P>
     * From this method, the <code>DropTargetListener</code>
     * shall accept or reject the drop via the
     * acceptDrop(int dropAction) or rejectDrop() methods of the
     * <code>DropTargetDropEvent</code> parameter.
     * <P>
     * Subsequent to acceptDrop(), but not before,
     * <code>DropTargetDropEvent</code>'s getTransferable()
     * method may be invoked, and data transfer may be
     * performed via the returned <code>Transferable</code>'s
     * getTransferData() method.
     * <P>
     * At the completion of a drop, an implementation
     * of this method is required to signal the success/failure
     * of the drop by passing an appropriate
     * <code>boolean</code> to the <code>DropTargetDropEvent</code>'s
     * dropComplete(boolean success) method.
     * <P>
	 * This method accept or declines the drop of an external file, directory or text block.
     * <P>
     * @param e the <code>DropTargetDropEvent</code>
     */
	public void drop(DropTargetDropEvent e) {

		// IF THE SCRIBBLE LAYER IS ON AND ON THE TOP LAYER, REJECT ALL DROPS
		UIScribblePad pad = getScribblePad();
		if (pad != null && pad.isVisible() && getLayer(oScribblePad) == SCRIBBLE_LAYER) {
			return;
		}

		try {
       		final Transferable tr = e.getTransferable();
			final UIViewPane pane = this;
			final DropTargetDropEvent evt = e;

			Point dropPoint = e.getLocation();

			int nX = dropPoint.x;
			int nY = dropPoint.y;
			if (nX >= 20 && nY >= 10) {
				nX -= 20;
				nY -= 10;
			}

		 	if (tr.isDataFlavorSupported(DraggableStencilIcon.supportedFlavors[0])) {
				Object source = tr.getTransferData(DraggableStencilIcon.supportedFlavors[0]);
				if (source instanceof DraggableStencilIcon) {
					DraggableStencilIcon stencil = (DraggableStencilIcon)source;
					createNodeFromStencil(stencil, nX, nY);
				} 
			}
            else if (tr.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {

				e.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
				final java.util.List fileList = (java.util.List) tr.getTransferData(DataFlavor.javaFileListFlavor);

				// new Thread required for Mac bug caused when code calls UIUtilities.checkCopyLinkedFile
				// and tries to open a JOptionPane popup.
				final int xPos = nX;
				final int yPos = nY;
				Thread thread = new Thread("UIViewPane.drop-FileListFlavor") {
					public void run() {

						int nX = xPos;
						int nY = yPos;

						Iterator iterator = fileList.iterator();
						while (iterator.hasNext()) {
							createNodes(pane, (File) iterator.next(), nX, nY);
							nY = +80;
						}
												
						/*
						int nType = ICoreConstants.REFERENCE;
						String fileName = "";
					 	while (iterator.hasNext()) {

							File file = (File)iterator.next();

							UINode node = null;

							// IF IS A DIRECTORY - CREATE A MAP AND FILL IT WITH REFERENCE NODES
							if (file.isDirectory()) {

								node = oViewPaneUI.addNewNode(ICoreConstants.MAPVIEW, nX, nY );
								node.setText(file.getName());
								node.getUI().refreshBounds();

								int x = 10;
								int y = 10;

								File[] files = file.listFiles();
								for (int i=0; i<files.length; i++) {
									try {
										File innerFile = files[i];
										File oldFile = innerFile;
										if (! (innerFile.getName()).startsWith(".") || ProjectCompendium.isWindows) {

											innerFile = UIUtilities.checkCopyLinkedFile(innerFile);
											if (innerFile == null)
												innerFile = oldFile;

											NodePosition nodePos = ((View)node.getNode()).addMemberNode(nType, "", "", sAuthor, innerFile.getName(), "", x, y);
											if (UIImages.isImage(innerFile.getPath()))
												nodePos.getNode().setSource("", innerFile.getPath(), sAuthor);
											else
												nodePos.getNode().setSource(innerFile.getPath(), "" , sAuthor);

											y += 50;
										}
									}
									catch(Exception ex) {
										System.out.println("error in UIViewPane.drop) \n\n"+ex.getMessage());
									}
								}
								nY += 50;
							}
							else {
								fileName = file.getName();

								if (!fileName.startsWith(".") || ProjectCompendium.isWindows) {

									fileName = fileName.toLowerCase();

									if ((fileName.endsWith(".xml") || fileName.endsWith(".zip")) && file.exists()) {
										UIDropFileDialog dropDialog = new UIDropFileDialog(ProjectCompendium.APP, pane, file, nX, nY);
										dropDialog.setVisible(true);
										nY += 50;
									}
									else {
										file = UIUtilities.checkCopyLinkedFile(file);

										File oldfile = file;
										if (file == null)
											file = oldfile;

										node = oViewPaneUI.addNewNode(nType, nX, nY);
										node.setReferenceIcon(file.getPath());

										try {
											if (UIImages.isImage(file.getPath()))
												node.getNode().setSource("", file.getPath(), sAuthor);
											else
												node.getNode().setSource(file.getPath(), "" , sAuthor);
										}
										catch(Exception ex) {
											System.out.println("error in UIViewPane.drop-2) \n\n"+ex.getMessage());
										}

										node.setText(file.getName());
										node.getUI().refreshBounds();

										nY += 50;
									}
								}
							}
						}*/
						
						evt.getDropTargetContext().dropComplete(true);
					}
				};
				thread.start();
        	}
  			else if (tr.isDataFlavorSupported(DataFlavor.stringFlavor)) {

				e.acceptDrop(DnDConstants.ACTION_COPY);
				final String dropString = (String)tr.getTransferData(DataFlavor.stringFlavor);

				// new Thread required for Mac bug caused when code calls UIUtilities.checkCopyLinkedFile
				// and tries to open a JOptionPane popup.
				final int xPos = nX;
				final int yPos = nY;
				Thread thread = new Thread("UIViewPane.drop-StringFlavor") {
					public void run() {

						int nX = xPos;
						int nY = yPos;
						String s = dropString;

						/*if (s.startsWith("memetic-replay")) {
							ProjectCompendium.APP.oMeetingManager = new MeetingManager(MeetingManager.REPLAY);
							ProjectCompendium.APP.oMeetingManager.processAsMeetingReplay(s);
						}
						else if (s.startsWith("memetic-index")) {
							if (ProjectCompendium.APP.oMeetingManager == null) {
								ProjectCompendium.APP.displayError("You are not currently replaying a Meeting");
								return;
							}
							else {
								ProjectCompendium.APP.oMeetingManager.processAsMeetingReplayIndex(s, nX, nY);
							}
						}*/


						boolean bdragdropKDE = false;
						if (ProjectCompendium.isLinux) { 
							if (s.startsWith("www.") || s.startsWith("http://")
									|| s.startsWith("https://")) {
								UINode node = oViewPaneUI.addNewNode(
										ICoreConstants.REFERENCE, nX, nY);
								node.setText(s);
								try {
									node.getNode().setSource(s, "", sAuthor);
									node.setReferenceIcon(s);
								} catch (Exception ex) {
									System.out
											.println("error in UIViewPane.drop-2) \n\n"
													+ ex.getMessage());
								}
								node.getUI().refreshBounds();
							} else {
								final java.util.List fileList = new LinkedList();
								if (s.startsWith("file://")) {
									// remove 'file://' from file path								
									String[] liste = s.split("file://");

									for (int i = 1; i < liste.length; i++) {
										// remove 'file://' from file path
										String filename = new String(liste[i]
												.replaceFirst("\n", "")); 
										File file = new File(filename);
										fileList.add(file);
									}
									Iterator iterator = fileList.iterator();

									nX = xPos;
									nY = yPos;
									while (iterator.hasNext()) {
										createNodes(pane, (File) iterator
												.next(), nX, nY);
										nY = +80;
									}
									// drop object is not a file but e.g. text									
									bdragdropKDE = true; 
								} else {
									bdragdropKDE = false;
								}
							}
						}
						
						try {
							int nType = new Integer(s).intValue();
							oViewPaneUI.addNewNode(nType, nX, nY);
							evt.getDropTargetContext().dropComplete(true);
						}
						catch(NumberFormatException io) {

							if (UINode.isReferenceNode(s)) {

								File newFile = new File(s);
								String fileName = newFile.getName();
								fileName = fileName.toLowerCase();

								String sDatabaseName = CoreUtilities.cleanFileName(ProjectCompendium.APP.sFriendlyName);						
								UserProfile oUser = ProjectCompendium.APP.getModel().getUserProfile();
								String sUserDir = CoreUtilities.cleanFileName(oUser.getUserName())+"_"+oUser.getId();
								String sFullPath = "Linked Files"+ProjectCompendium.sFS+sDatabaseName+ProjectCompendium.sFS+sUserDir;			

								File directory = new File(sFullPath);
								if (!directory.isDirectory()) {
									directory.mkdirs();
								}
								String sFilePath = sFullPath+ProjectCompendium.sFS;
								directory = new File(sFilePath);
								if (ProjectCompendium.isMac)
									sFilePath = directory.getAbsolutePath()+ProjectCompendium.sFS;

								String sActualFilePath = "";
								try {
									sActualFilePath = UIImages.loadWebImageToLinkedFiles(s, fileName, sFilePath);
								}
								catch(Exception exp) {}

								if (!sActualFilePath.equals("")) {
									UINode node = oViewPaneUI.addNewNode(ICoreConstants.REFERENCE, nX, nY);
									node.setReferenceIcon(sActualFilePath);

									try {
										node.getNode().setSource("", sActualFilePath, sAuthor);
									}
									catch(Exception ex) {
										System.out.println("error in UIViewPane.drop-3b) \n\n"+ex.getMessage());
									}

									File temp = new File(sActualFilePath);
									node.setText(temp.getName());
									node.getUI().refreshBounds();
								}
								else {
									newFile = UIUtilities.checkCopyLinkedFile(newFile);
									if (newFile != null)
										s = newFile.getPath();

									UINode node = oViewPaneUI.addNewNode(ICoreConstants.REFERENCE, nX, nY);
									node.setReferenceIcon(s);

									try {
										if (UIImages.isImage(s))
											node.getNode().setSource("", s, sAuthor);
										else {
											node.getNode().setSource(s, "", sAuthor);
										}
									}
									catch(Exception ex) {
										System.out.println("error in UIViewPane.drop-3) \n\n"+ex.getMessage());
									}

									node.setText(s);
									node.getUI().refreshBounds();
								}
								evt.getDropTargetContext().dropComplete(true);
							}
							else {
								if (!bdragdropKDE) { 
									UIDropSelectionDialog dropDialog = new UIDropSelectionDialog(ProjectCompendium.APP, pane, s, nX, nY);
									if (FormatProperties.dndNoTextChoice) {
										dropDialog.processAsPlain();
										dropDialog.onCancel();
									}
									else {
										dropDialog.setVisible(true);
									}
									evt.getDropTargetContext().dropComplete(true);
								}
							}
						}
					}
				};
				thread.start();
			}
			else if (tr.isDataFlavorSupported(DataFlavor.imageFlavor)) {
				e.acceptDrop(DnDConstants.ACTION_COPY);

				Image img = (Image)tr.getTransferData(DataFlavor.imageFlavor);
				if (img instanceof BufferedImage) {
					try {

						File newFile = new File("Linked Files"+ProjectCompendium.sFS+"External_Image_"+(new Date()).getTime()+".jpg");

						ImageIO.write((RenderedImage)img, "jpeg", newFile);

						if (newFile.exists()) {
							String s = "";
							if (newFile != null)
								s = newFile.getPath();

							UINode node = oViewPaneUI.addNewNode(ICoreConstants.REFERENCE, nX, nY);
							node.setReferenceIcon(s);
							try {
								if (UIImages.isImage(s))
									node.getNode().setSource("", s, sAuthor);
								else {
									node.getNode().setSource(s, "", sAuthor);
								}
							}
							catch(Exception ex) {
								System.out.println("error in UIViewPane.drop-4) \n\n"+ex.getMessage());
							}

							node.setText(s);
							node.getUI().refreshBounds();
						}
					}
					catch(IOException io) {
						System.out.println("io exception "+io.getMessage());
					}
				}
			}
			else {
				e.rejectDrop();
			}
		}
  		catch (IOException io) {
            io.printStackTrace();
			System.out.flush();
			e.rejectDrop();
        }
		catch (UnsupportedFlavorException ufe) {
            ufe.printStackTrace();
			System.out.flush();
 			e.rejectDrop();
		}
	}

	/**
	 * Create a new node from a stencil item.
	 * @param stencil, the stencil item to create the node from.
	 * @param nX, the x position for the new node.
	 * @param nY, the y position for the new node.
	 */
	public void createNodeFromStencil( DraggableStencilIcon stencil, int nX, int nY) {

		String sImage = stencil.getImage();
		String sBackgroundImage = stencil.getBackgroundImage();
		String sTemplate = stencil.getTemplate();
		
		String sLabel = stencil.getLabel();
		int nType = stencil.getNodeType();
		Vector vtTags = stencil.getTags();

		UINode node = oViewPaneUI.addNewNode(nType, nX, nY);
		node.setReferenceIcon(sImage);
		try {
			node.getNode().setSource("", sImage, sAuthor);
		}
		catch(Exception ex) {
			System.out.println("error in UIViewPane.createNodeFromStencil) \n\n"+ex.getMessage());
		}
		node.setText(sLabel);
		node.getUI().setEditing();

		// ADD THE TAGS
		IModel oModel = ProjectCompendium.APP.getModel();
		PCSession oSession = oModel.getSession();

		NodeSummary nodeSum = node.getNode();
		int count = vtTags.size();
		for(int i=0; i<count;i++) {
			Vector data = (Vector)vtTags.elementAt(i);
			String sID = (String)data.elementAt(0);
			String sName = (String)data.elementAt(1);
			String sAuthor = (String)data.elementAt(2);
			String sDescription = (String)data.elementAt(3);
			String sBehavior = (String)data.elementAt(4);
			Date dCreated = (Date)data.elementAt(5);
			Date dLastModified = (Date)data.elementAt(6);

			Code codeObj = null;

			try {
				// CHECK IF ALREADY IN DATABASE
				Vector existingCodesForName = (oModel.getCodeService()).getCodeIDs(oSession, sName);
				if (existingCodesForName.size() == 0) {
					codeObj = oModel.getCodeService().createCode(oSession, sID, sAuthor, dCreated,
															 dLastModified, sName, sDescription, sBehavior);
					oModel.addCode(codeObj);
					// SHOULD I UPDATE THE XML STENCIL DATA?
				}
				else {
					String existingCodeID = (String)existingCodesForName.elementAt(0);
					codeObj = oModel.getCodeService().getCode(oSession, existingCodeID);
				}
				nodeSum.addCode(codeObj);
			}
			catch(Exception ex) { System.out.println("Unable to add tag = "+codeObj.getName()+"\n\ndue to:\n\n"+ex.getMessage()); }
		}

		if (node.getNode() instanceof View) {
			View view  = (View)node.getNode();			
			if (sBackgroundImage != null && !sBackgroundImage.equals("")) {
				try {
					view.setBackground(sBackgroundImage);
					view.updateViewLayer();
				}
				catch(Exception ex) {
					System.out.println("error in UIViewPane.createNodeFromStencil) \n\n"+ex.getMessage());
				}
			} 
			if (sTemplate != null && !sTemplate.equals("")) {				
				UIMapViewFrame mapFrame = null;
				try {
					view.initializeMembers();					
					mapFrame = new UIMapViewFrame(view, view.getLabel());
				}
				catch(Exception ex) {
					ex.printStackTrace();
				}				
				if (mapFrame != null) {
					ProjectCompendium.APP.onTemplateImport(sTemplate, mapFrame.getViewPane());
				}
			}		
		}	
		
		node.getUI().refreshBounds();
	}

	/**
	 * Create node(d) for the given file/directory of files
	 * @param pane The view to create the top node in.
	 * @param file the File object to process
	 * @param nX the x location of the top node.
	 * @param nY the y location of the top node.
	 */
	public void createNodes(UIViewPane pane, File file, int nX, int nY) {

		UINode node;
		if (file.isDirectory()) {			
			int oneLevelChoice = JOptionPane.showConfirmDialog(
					ProjectCompendium.APP,
					"Create a Reference to the directory?\nSelect 'No' to create a Map " +
					"with the directory contents.",
					"Create a Reference to directory?",
					JOptionPane.YES_NO_CANCEL_OPTION);
			if (oneLevelChoice == JOptionPane.YES_OPTION){
				node = oViewPaneUI.addNewNode(ICoreConstants.REFERENCE, nX, nY);
				node.setText(file.getName());
				try { node.getNode().setSource(file.getPath(), "", sAuthor);}
				catch (Exception ex) {return;}
				node.setReferenceIcon(file.getPath());
				node.getUI().refreshBounds();
				return;
			} else if (oneLevelChoice == JOptionPane.CANCEL_OPTION) return; //do nothing
						

			node = oViewPaneUI.addNewNode(ICoreConstants.MAPVIEW, nX, nY);
			node.setText(file.getName());
			node.setReferenceIcon(file.getPath());
			node.getUI().refreshBounds();

			File[] files = file.listFiles(); // list files in directory
			nX = 10;
			nY = 10;
			View view = (View) node.getNode();
			boolean add_recursively = false;
			boolean alreadyAsked = false;
			if (FormatProperties.dndAddDirRecursively) {
				add_recursively = true;
				alreadyAsked = true;				
			}
			for (int i = 0; i < files.length; i++) {
				if (files[i].isDirectory() && !alreadyAsked) {
					int recursiveChoice = JOptionPane.showConfirmDialog(
							ProjectCompendium.APP,
							"Do you want to add subdirectories recursively as map nodes?",
							"Add subdirectories recursively?",
							JOptionPane.YES_NO_OPTION);
					if (recursiveChoice == JOptionPane.YES_OPTION)
						add_recursively = true;
					alreadyAsked = true;
				}
				createSingleNode(view, files[i], nX, nY, add_recursively);
				nY += 80;
			}
		} else { // create a REFERENCE NODE
			String fileName = file.getName();
			if (!fileName.startsWith(".") || ProjectCompendium.isWindows) {
				fileName = fileName.toLowerCase();

				if ((fileName.endsWith(".xml") || fileName.endsWith(".zip"))
						&& file.exists()) {
					UIDropFileDialog dropDialog = new UIDropFileDialog(
							ProjectCompendium.APP, pane, file, nX, nY);
					dropDialog.setVisible(true);
				} else {
					file = UIUtilities.checkCopyLinkedFile(file);

					node = oViewPaneUI.addNewNode(ICoreConstants.REFERENCE, nX,
							nY);
					node.setReferenceIcon(file.getPath());
					try {
						if (UIImages.isImage(file.getPath()))
							node.getNode().setSource("", file.getPath(),
									sAuthor);
						else
							node.getNode().setSource(file.getPath(), "",
									sAuthor);
					} catch (Exception ex) {
						System.out.println("error in UIViewPane.drop-2) \n\n"
								+ ex.getMessage());
					}
					node.setText(file.getName());
					node.getUI().refreshBounds();
				}
			}
		}
	}

	/**
	 * Create node(d) for the given file/directory of files
	 * @param view the view to create the nodes in.
	 * @param file the file/directory to create the node(s) from
	 * @param nX the x location of the top node
	 * @param nY the y location of the top node
	 * @param recursive whether to traverse the directory structure recrusvely creating maps.
	 */
	protected void createSingleNode(View view, File file, int nX, int nY, boolean recursive) {
		 
		if (file.isDirectory()) {
			try {
				int nType;
				if (recursive) {
					nType=ICoreConstants.MAPVIEW;
				} else { 
					nType=ICoreConstants.REFERENCE;
				}
				
				NodePosition nodePos = view.addMemberNode(nType, "", "", sAuthor, file.getName(), "", nX, nY);
				nodePos.getNode().setSource(file.getPath(), "", sAuthor);
				nY += 80;
				view = (View)nodePos.getNode();
			} catch (Exception ex) {
				System.out.println("error in UIViewPane.drop-2) \n\n"
						+ ex.getMessage());
			}
			if (recursive) {
				File[] files = file.listFiles(); // all files in directory
				nX = 10;
				nY = 10;
				for (int i = 0; i < files.length; i++) {
					createSingleNode(view, files[i], nX, nY, recursive);
					nY += 80;
				}				
			}

		} else {
			try {
				file = UIUtilities.checkCopyLinkedFile(file);
				NodePosition nodePos = view.addMemberNode(
						ICoreConstants.REFERENCE, "", "", sAuthor, file
								.getName(), "", nX, nY);
				nodePos.getNode().setSource(file.getPath(), "", sAuthor);
			} catch (Exception ex) {
				ex.printStackTrace();
				System.out.println("error in UIViewPane.drop-2) \n\n"
						+ ex.getMessage());
			}
		}
	}
	
	/////////////////////////////////////////////

	/**
	 *	Convenience Method to get Containing ViewFrame.
	 * @return UIViewFrame, the parent frame for this view.
	 */
	public UIViewFrame getViewFrame() {
		return oViewFrame;
	}

  	/**
     * Returns the L&F object that renders this component.
     *
     * @return ViewPaneUI, the object that renders this component.
     */
  	public ViewPaneUI getUI() {
		return oViewPaneUI;
  	}

  	/**
     * Sets the L&F object that renders this component.
     * <p>CURRENTLY DOES NOTHING</p>
     * @param ui,  the ViewPaneUI L&F object.
     */
  	public void setUI(ViewPaneUI ui) {
		//    if ((ViewPaneUI)this.ui != ui) {
		//      super.setUI(ui);
		//      repaint();
		//    }
  	}

  	/**
     * Notification from the UIManager that the L&F has changed.
     * Replaces the current UI object with the latest version from the
     * UIManager.
     *
     * @see JComponent#updateUI
     */
  	public void updateUI() {

		oViewPaneUI = new ViewPaneUI(this);
	  	//setUI(oViewPaneUI);
		invalidate();
 	}

	/**
     * Returns the name of the L&F class that renders this component.
     *
     * @return "ViewPaneUI"
     * @see JComponent#getUIClassID
     * @see UIDefaults#getUI
     */
  	public String getUIClassID() {
		return "ViewPaneUI";
  	}

   	/**
     * Returns the L&F object that renders this component.
     *
     * @return ViewPaneUI, the object that renders this component.
     */
  	public ViewPaneUI getViewPaneUI() {
		return oViewPaneUI;
  	}

	/**
	 * Overridden to always return true.
	 * @return boolean, always returns true.
	 */
  	public boolean isOpaque() {
		return true;
  	}

	/**
	 * Set the view that this view pane represents
	 *
	 * @param view com.compendium.core.datamodel.View, the view represented by this view pane.
	 * @see com.compendium.core.datamodel.IView
	 */
	public void setView(View view) {
		View oldValue = oView;
		oView = view;
		firePropertyChange(VIEW_PROPERTY, oldValue, view);
		repaint();
	}

	/**
	 * Returns the view that this view pane represents.
	 *
	 * @return com.compendium.core.datamodel.View, the represented view
	 * @see com.compendium.core.datamodel.IView
	 */
	public View getView() {
		return oView;
	}

	/**
	 * Display popups listing the codes added to all node in this view.
	 * <p>NOT CURRENTLY USED</p>
	 */
	public void showCodes() {
		for(Enumeration e = oView.getPositions();e.hasMoreElements();) {
			NodePosition nodepos = (NodePosition)e.nextElement();
			NodeSummary summary = nodepos.getNode();
			try {
				if (summary.getCodeCount() > 0 ) {
					int x = nodepos.getXPos();
					int y = nodepos.getYPos();

					UIHintNodeCodePanel pop = new UIHintNodeCodePanel(summary, x, y);
					add(pop, HINT_LAYER);
					pop.setVisible(true);
					tagPopups.put(summary.getId(), pop);
				}
			}
			catch(Exception ex) {
				System.out.println("Error: (UIViewPane.showCodes)\n\n"+ex.getMessage());
			}
		}
	}

	/**
	 * Calculate the position for the popup panel passed
	 * @param pop the popup panel
	 * @param realX the starting x
	 * @param realY the starting y
	 * @return the final location for the panel
	 */private Point calculateLocation(JPanel pop, int realX, int realY) {
		
		Rectangle rect = getViewFrame().getViewport().getViewRect();
		int screenWidth = rect.width;
		int screenHeight = rect.height;
		screenWidth = rect.x + screenWidth;
		screenHeight = rect.y + screenHeight;

		int endXCoordForPopUpMenu = realX + pop.getWidth();
		int endYCoordForPopUpMenu = realY + pop.getHeight();

		int offsetX = (screenWidth) - endXCoordForPopUpMenu;
		int offsetY = (screenHeight) - endYCoordForPopUpMenu;

		if(offsetX > 0)
			offsetX = 0;
		if(offsetY > 0)
			offsetY = 0;

		int finalX = realX+offsetX;
		if (realX+offsetX < rect.x) {
			finalX = rect.x;
		}
		
		return new Point(finalX, realY+offsetY);
	}
	
	/**
	 * Show tags (codes) rollover popup for the given node.
	 * @param node com.compendium.core.datamodel.NodeSummary, the node to show the tags for.
	 * @param nX, the x position of the trigger event.
	 * @param nY, the y position of the trigger event.
	 */
	public void showCodes(NodeSummary node, int nX, int nY) {

		if (!tagPopups.containsKey(node.getId())) {

			int realX = nX+hintOffset;
			int realY = nY+hintOffset;

			UIHintNodeCodePanel pop = new UIHintNodeCodePanel(node, realX, realY);
			Point newLoc= calculateLocation(pop, realX, realY);				
			pop.setLocation(newLoc.x, newLoc.y);

			add(pop, HINT_LAYER);
			pop.setVisible(true);
			tagPopups.put(node.getId(), pop);
		}
	}

	/**
	 * Hide all open tag rollover popups.
	 */
	public void hideCodes() {

		for(Enumeration e = tagPopups.elements() ;e.hasMoreElements();) {
			UIHintNodeCodePanel pop = (UIHintNodeCodePanel)e.nextElement();
			remove(getIndexOf(pop));
			pop.setVisible(false);
			pop = null;
		}

		tagPopups.clear();
		invalidate();
		repaint();
	}

	/**
	 * Show parent views rollover popup for the given node.
	 * @param node com.compendium.core.datamodel.NodeSummary, the node to show the parent views for.
	 * @param nX, the x position of the trigger event.
	 * @param nY, the y position of the trigger event.
	 */
	public void showViews(NodeSummary node, int nX, int nY) {

		if (!viewsPopups.containsKey(node.getId())) {

			int realX = nX+hintOffset;
			int realY = nY+hintOffset;

			UIHintNodeViewsPanel pop = new UIHintNodeViewsPanel(node, realX, realY);

			Point newLoc= calculateLocation(pop, realX, realY);				
			pop.setLocation(newLoc.x, newLoc.y);

			add(pop, HINT_LAYER);
			pop.setVisible(true);
			viewsPopups.put(node.getId(), pop);
		}
	}

	/**
	 * Hide all parent views rollover popups.
	 */
	public void hideViews() {

		for(Enumeration e = viewsPopups.elements(); e.hasMoreElements();) {
			UIHintNodeViewsPanel pop = (UIHintNodeViewsPanel)e.nextElement();
			remove(getIndexOf(pop));
			pop.setVisible(false);
			pop = null;
		}

		viewsPopups.clear();
		invalidate();
		repaint();
	}

	/**
	 * Show node detail rollover popup for the given node.
	 * @param node com.compendium.core.datamodel.NodeSummary, the node to show the detail for.
	 * @param nX, the x position of the trigger event.
	 * @param nY, the y position of the trigger event.
	 */
	public void showDetail(NodeSummary node, int nX, int nY) {

		if (!detailPopups.containsKey(node.getId())) {
			
			int realX = nX+hintOffset;
			int realY = nY+hintOffset;
				
			UIHintNodeDetailPanel pop = new UIHintNodeDetailPanel(node, realX, realY);

			Point newLoc= calculateLocation(pop, realX, realY);				
			pop.setLocation(newLoc.x, newLoc.y);

			add(pop, HINT_LAYER);
			pop.setVisible(true);

			detailPopups.put(node.getId(), pop);
		}
	}

	/**
	 * Hide all node detail rollover popups.
	 */
	public void hideDetail() {

		for(Enumeration e = detailPopups.elements(); e.hasMoreElements();) {
			UIHintNodeDetailPanel pop = (UIHintNodeDetailPanel)e.nextElement();
			remove(getIndexOf(pop));
			pop.setVisible(false);
			pop = null;
		}

		detailPopups.clear();
		invalidate();
		repaint();
	}

	/**
	 * Show node image rollover popup for the given node.
	 * @param node com.compendium.core.datamodel.NodeSummary, the node to show the image for.
	 * @param nX, the x position of the trigger event.
	 * @param nY, the y position of the trigger event.
	 */
	public void showImage(NodeSummary node, int nX, int nY) {

		if (!imagePopups.containsKey(node.getId())) {

			UIViewFrame frame = getViewFrame();
			UIHintNodeImagePanel pop = new UIHintNodeImagePanel(node, 0, 0, frame);

			int nChildWidth = pop.getSize().width;
			int nChildHeight = pop.getSize().height;

			Dimension dim = frame.getViewport().getExtentSize();

			int nParentWidth = dim.width;
			int nParentHeight = dim.height;

			if (nChildWidth < nParentWidth) {
				pop.setLocation(
					 ( (nParentWidth - nChildWidth) / 2 )+ frame.getHorizontalScrollBarPosition(),
					 ( (nParentHeight - nChildHeight) / 2 ) + frame.getVerticalScrollBarPosition());
			}
			else {
				pop.setLocation(0, 0);
			}

			add(pop, HINT_LAYER);

			pop.setVisible(true);

			imagePopups.put(node.getId(), pop);
		}
	}

	/**
	 * Hide all image rollover poppups.
	 */
	public void hideImages() {

		for(Enumeration e = imagePopups.elements(); e.hasMoreElements();) {
			UIHintNodeImagePanel pop = (UIHintNodeImagePanel)e.nextElement();
			remove(getIndexOf(pop));
			pop.setVisible(false);
			pop = null;
		}

		imagePopups.clear();
		invalidate();
		repaint();
	}

	/**
	 * Show list of nodes to move to.
	 * @param NodeSummary the node this list applies to.
	 * @param Vector list list of nodes to move to.
	 * @param nX the x position of the trigger event.
	 * @param nY the y position of the trigger event.
	 */
	/*public void showNodeFocusList(UINode node, Vector list, int nX, int nY) {

		if (!nodeFocusPopups.containsKey(node.getNode().getId())) {
			UIHintNodeFocusPanel pop = new UIHintNodeFocusPanel(this, node, list, nX+hintOffset, nY+hintOffset);

			int realX = nX+hintOffset;
			int realY = nY+hintOffset;

			Rectangle rect = getViewFrame().getViewport().getViewRect();
			int screenWidth = rect.width;
			int screenHeight = rect.height;
			screenWidth = rect.x + screenWidth;
			screenHeight = rect.y + screenHeight;

			int endXCoordForPopUpMenu = realX + pop.getWidth();
			int endYCoordForPopUpMenu = realY + pop.getHeight();

			int offsetX = (screenWidth) - endXCoordForPopUpMenu;
			int offsetY = (screenHeight) - endYCoordForPopUpMenu;

			if(offsetX > 0)
				offsetX = 0;
			if(offsetY > 0)
				offsetY = 0;

			//pop.setLocation(realX+offsetX, realY+offsetY);
			pop.setLocation(0, 0);

			add(pop, HINT_LAYER);
			pop.setVisible(true);
			nodeFocusPopups.put(node.getNode().getId(), pop);
			pop.focusList();
		}
	}*/

	/**
	 * Hide all node focus list rollover popups.
	 */
	/*public void hideNodeFocusList() {

		for(Enumeration e = nodeFocusPopups.elements(); e.hasMoreElements();) {
			UIHintNodeFocusPanel pop = (UIHintNodeFocusPanel)e.nextElement();
			remove(getIndexOf(pop));
			pop.setVisible(false);
			pop = null;
		}

		nodeFocusPopups.clear();
		invalidate();
		repaint();
	}*/

	/**
	 * Show the label search popup for the given node.
	 * @param node com.compendium.core.datamodel.NodeSummary, the node to show the label search popup for.
	 * @param text, the text to match in the search.
	 */
	public void showLabels(UINode node, String text) {

		if (text == null || text.equals("")) {
			if (labelPopups.containsKey(node.getNode().getId()))
				hideLabels();

			return;
		}

		if (!labelPopups.containsKey(node.getNode().getId())) {

			Point p = node.getLocation();
			Dimension d = node.getSize();
			int nX = p.x;
			int nY = p.y;

			int realX = nX;
			int realY = nY+d.height;

			try {
				UIHintNodeLabelPanel pop = new UIHintNodeLabelPanel(this, node, realX, realY, text);
				add(pop, HINT_LAYER);
				pop.setVisible(true);
				labelPopups.put(node.getNode().getId(), pop);
			}
			catch(Exception ex) {}
		}
		else {
			UIHintNodeLabelPanel pop = (UIHintNodeLabelPanel)labelPopups.get(node.getNode().getId());
			if (pop != null) {
				if (!pop.searchLabel(text, node.getNode().getId()))
					hideLabels();
			}
		}
	}

	/**
	 * Return the node search label panel for the given node id.
	 * @param id, the node id of the node to return the search label panel for.
	 * @return com.compendium.ui.panels.UIHintNodeLabelPanel
	 */
	public UIHintNodeLabelPanel getLabelPanel(String id) {
		if (labelPopups.containsKey(id))
			return (UIHintNodeLabelPanel)labelPopups.get(id);

		return null;
	}

	/**
	 * Hide all search label popups open - CURRENTLY ONLY ONE AT A TIME.
	 */
	public void hideLabels() {

		for(Enumeration e = labelPopups.elements() ;e.hasMoreElements();) {
			UIHintNodeLabelPanel pop = (UIHintNodeLabelPanel)e.nextElement();
			remove(getIndexOf(pop));
			pop.setVisible(false);
			pop = null;
		}

		labelPopups.clear();
		invalidate();
		repaint();
	}
	
	/**
	 * Refresh search label to redraw fonts.
	 */
	public void refreshLabels() {

		for(Enumeration e = labelPopups.elements() ;e.hasMoreElements();) {
			UIHintNodeLabelPanel pop = (UIHintNodeLabelPanel)e.nextElement();
			pop.refresh();
		}
	}	

	/**
	 * Return the scribble pad.
	 */
	public UIScribblePad getScribblePad() {
		return oScribblePad;
	}

	/**
	 * Activate the scribble pad layer.
	 */
	public void showScribblePad() {
		if (oScribblePad == null) {
			oScribblePad = new UIScribblePad(this);
			oScribblePad.setPreferredSize(getSize());
			oScribblePad.setSize(getSize());

			ViewLayer viewlayer = oView.getViewLayer();
			oScribblePad.processPencilData(viewlayer.getScribble());
			oScribblePad.processShapesData(viewlayer.getShapes());
		}

		add(oScribblePad, SCRIBBLE_LAYER);

		oViewFrame.setTitle(sTitle+ " - Scribble Pad");
		oScribblePad.setVisible(true);
	}

	/**
	 * Move scribble pad to back.
	 */
	public void moveBackScribblePad() {
		if (oScribblePad == null) {
			return;
		}

		setLayer(oScribblePad, SCRIBBLE_LAYER_BACK.intValue());
	}


	/**
	 * Move scribble pad to front.
	 */
	public void moveForwardScribblePad() {
		if (oScribblePad == null) {
			return;
		}

		setLayer(oScribblePad, SCRIBBLE_LAYER.intValue());
	}

	/**
	 * Is the scribble pad layer at the back of the node and links layers.
	 * @return true if it is, else false;
	 */
	public boolean isScribblePadBack() {
		if (getLayer(oScribblePad) == SCRIBBLE_LAYER_BACK.intValue()) {
			return true;
		}

		return false;
	}


	/**
	 * Remove the scribble pad layer.
	 */
	public void hideScribblePad() {
		if (oScribblePad != null) {
			oScribblePad.setVisible(false);
			oViewFrame.setTitle(sTitle);
			remove(getIndexOf(oScribblePad));
		}
	}

	/**
	 * Clear the scribble pad layer contents.
	 */
	public void clearScribblePad() {
		if (oScribblePad != null)
			oScribblePad.clearPad();
	}

	/**
	 * Save the scribble pad layer contents.
	 */
	public void saveScribblePad() {
		if (oScribblePad != null) {

			ProjectCompendium.APP.setWaitCursor();
			ProjectCompendium.APP.setWaitCursor(oViewFrame);

			ViewLayer viewlayer = oView.getViewLayer();

			String sScribble = oScribblePad.reversePencilData();
			String sShapes = oScribblePad.reverseShapesData();
			viewlayer.setShapes(sShapes);
			viewlayer.setScribble(sScribble);
			try {
				oView.updateViewLayer();
			}
			catch(Exception ex) {
				ProjectCompendium.APP.displayError("Scribble layer data could not be saved due to: \n\n"+ex.getMessage());
			}

			ProjectCompendium.APP.setDefaultCursor(oViewFrame);
			ProjectCompendium.APP.setDefaultCursor();
		}
	}

	/**
	 * Set the background image for this view.
	 *
	 * @param node com.compendium.core.datamodel.NodeSummary, the node to show the detail for.
	 */
	public void addBackground(String sImagePath) {
		if (lblBackgroundLabel != null)
			removeBackground();

		lblBackgroundLabel = new JLabel();
		ImageIcon oIcon	= UIImages.createImageIcon(sImagePath);
		lblBackgroundLabel.setIcon(oIcon);
		lblBackgroundLabel.setLocation(0,0);
		lblBackgroundLabel.setSize(lblBackgroundLabel.getPreferredSize());
		add(lblBackgroundLabel, BACKGROUND_LAYER);
	}

	/**
	 * Set the background image for this view.
	 *
	 * @param node com.compendium.core.datamodel.NodeSummary, the node to show the detail for.
	 */
	public void removeBackground() {

		int index = getIndexOf(lblBackgroundLabel);
		if (index > -1)
			remove(index);
	}

// NODE TRAVERSAL

  	/**
   	 * Move to the next node upward.
   	 */
 	public void moveUp(UINode oUINode) {
		Component nodearray[] = getComponentsInLayer((UIViewPane.NODE_LAYER).intValue());
		int i=0;

		int nParentX = oUINode.getX();
		int nParentY = oUINode.getY();

		UINode uinode = null;
		int nChildX = 0;
		int nChildY = 0;
		while(i<nodearray.length) {
			uinode = (UINode)nodearray[i++];
			nChildX = uinode.getX();
			nChildY = uinode.getY();


		}
  	}

  	/**
   	 * Move to the next node upward.
   	 */
 	public void moveDown(UINode uinode) {

  	}

  	/**
   	 * Move to the next node upward.
   	 */
 	public void moveLeft(UINode uinode) {

  	}

  	/**
   	 * Move to the next node upward.
   	 */
 	public void moveRight(UINode uinode) {

  	}

  	/**
   	 * Count the number of child node for the given node.
   	 * If one, move to that node.
   	 * If more than one, display a list to choose from.
   	 */
 	/*public void moveChild(UINode uinode) {

		String parentID = uinode.getNode().getId();

		Vector children = new Vector();

		for (Enumeration e = uinode.getLinks(); e.hasMoreElements();) {

			UILink uilink = (UILink)e.nextElement();
			UINode to = uilink.getToNode();
			String toID = to.getNode().getId();

			if (toID.equals(parentID)) {
				children.addElement(to);
			}
		}

		int count = children.size();
		if (count == 1) {
			UINode node = (UINode)children.elementAt(0);
			uinode.requestFocus();
		}
		else if (count > 1) {
			showNodeFocusList(uinode, children, uinode.getX(), uinode.getY());
		}
  	}*/

  	/**
   	 * Count the number of ancestor node for the given node.
   	 * If one, move to that node.
   	 * If more than one, display a list to choose from.
   	 */
 	/*public void moveAncestor(UINode uinode) {

		String parentID = uinode.getNode().getId();

		Vector ancestors = new Vector();

		for (Enumeration e = uinode.getLinks(); e.hasMoreElements();) {

			UILink uilink = (UILink)e.nextElement();
			UINode from = uilink.getFromNode();
			String fromID = from.getNode().getId();

			if (fromID.equals(parentID)) {
				ancestors.addElement(from);
			}
		}

		int count = ancestors.size();
		if (count == 1) {
			UINode node = (UINode)ancestors.elementAt(0);
			uinode.requestFocus();
		}
		else if (count > 1) {
			showNodeFocusList(uinode, ancestors, uinode.getX(), uinode.getY());
		}
  	}*/

// NODE SELECTION AND DESELECTION ROUTINES

 	/**
   	 * Selects all the child links and nodes for the given node.
	 * @param node com.compendium.ui.UINode, the node to select the children for.
   	 */
  	public void selectAllChildren(UINode node) {

		setSelectedNode(null, ICoreConstants.DESELECTALL);
		setSelectedLink(null, ICoreConstants.DESELECTALL);
		selectChildren(node);
  	}

  	/**
   	 * Selects all the child nodes for the given node.
	 * @param node com.compendium.ui.UINode, the node to select the child nodes for.
   	 */
 	private void selectChildren(UINode uinode) {

		uinode.setSelected(true);
		setSelectedNode(uinode,ICoreConstants.MULTISELECT);
		String parentID = uinode.getNode().getId();

		for (Enumeration e = uinode.getLinks(); e.hasMoreElements();) {

			UILink uilink = (UILink)e.nextElement();

			UINode from = uilink.getFromNode();
			UINode to = uilink.getToNode();

			String toID = to.getNode().getId();
			if (toID.equals(parentID)) {

				uilink.setSelected(true);
				setSelectedLink(uilink,ICoreConstants.MULTISELECT);
				selectChildren(from);
			}
		}
  	}

 	/**
   	 * Delselect all links and nodes then select all the ancestor links and nodes for the given node.
	 * i.e. up the tree, as opposed to down.
	 * @param node com.compendium.ui.UINode, the node to select the ancestor links and nodes for.
	 * @see #selectAllChildren
   	 */
  	public void selectAllAncestors(UINode node) {

		setSelectedNode(null, ICoreConstants.DESELECTALL);
		setSelectedLink(null, ICoreConstants.DESELECTALL);
		selectAncestors(node);
  	}

 	/**
   	 * Selects all the ancestor links and nodes the given node.
	 * i.e. up the tree, as opposed to down.
	 * @param node com.compendium.ui.UINode, the node to select the ancestor links and nodes for.
	 * @see #selectAllAncestors
	 * @see #selectAllChildren
   	 */
  	private void selectAncestors(UINode uinode) {

		uinode.setSelected(true);
		setSelectedNode(uinode,ICoreConstants.MULTISELECT);
		String parentID = uinode.getNode().getId();

		for (Enumeration e = uinode.getLinks(); e.hasMoreElements();) {

			UILink uilink = (UILink)e.nextElement();

			UINode from = uilink.getFromNode();
			UINode to = uilink.getToNode();

			String fromID = from.getNode().getId();
			if (fromID.equals(parentID)) {

				uilink.setSelected(true);
				setSelectedLink(uilink,ICoreConstants.MULTISELECT);
				selectAncestors(to);
			}
		}
  	}

 	/**
   	 * Selects all the links and nodes in the view.
   	 */
  	public void selectAll() {

		setSelectedNode(null, ICoreConstants.DESELECTALL);
		setSelectedLink(null, ICoreConstants.DESELECTALL);

		// get the uinode components in the viewpane layer
		Component nodearray[] = getComponentsInLayer((UIViewPane.NODE_LAYER).intValue());
		int i=0; UINode uinode = null;
		while(i<nodearray.length) {
			uinode = (UINode)nodearray[i++];
			uinode.setSelected(true);
			setSelectedNode(uinode,ICoreConstants.MULTISELECT);
		}

		// get the uilink components in the viewpane layer
		Component linkarray[] = getComponentsInLayer((UIViewPane.LINK_LAYER).intValue());
		i=0; UILink uilink = null;
		while(i<linkarray.length) {
			uilink = (UILink)linkarray[i++];
			uilink.setSelected(true);
			setSelectedLink(uilink,ICoreConstants.MULTISELECT);
		}
  	}

	/**
	 * Sets the given node selection state in this view and signals the aerial view of the event.
	 *
	 * @param node com.compendium.ui.UINode, the node to set the selection state for.
	 * @param mode, the selection state for the given node.
	 */
	public void setSelectedNode(UINode node, int mode) {

		((UIMapViewFrame)oViewFrame).setAerialSelectedNode(node, mode);
		processSelectedNode(node, mode);
	}

	/**
	 * Sets the given node selection state in this view without updating the aerial view
	 *
	 * @param node com.compendium.ui.UINode, the node to set the selection state for.
	 * @param mode, the selection state for the given node.
	 */
	public void processSelectedNode(UINode node, int mode) {

		if(mode == ICoreConstants.SINGLESELECT) {
			oNode = node;

			// and deselect all the previous selected nodes
			for(Enumeration e = vtNodeSelected.elements();e.hasMoreElements();) {

				UINode uinode = (UINode)e.nextElement();
				uinode.setSelected(false);
				uinode.setCut(false);
			}

			vtNodeSelected.removeAllElements();
			addNode(node);
			
			ProjectCompendium.APP.setNodeOrLinkSelected(true);
			ProjectCompendium.APP.setNodeSelected(true);			
		}

		if(mode == ICoreConstants.MULTISELECT) {
			oNode = node;
			addNode(node);			

			ProjectCompendium.APP.setNodeOrLinkSelected(true);
			ProjectCompendium.APP.setNodeSelected(true);			
		}

		if(mode == ICoreConstants.DESELECTALL) {
			if (getNumberOfSelectedLinks() == 0)
				ProjectCompendium.APP.setNodeOrLinkSelected(false);

			if(oNode != null) {
				if(oNode.equals(node))
					return;
				oNode.setSelected(false);
				oNode.setCut(false);
				oNode = null;
			}

			// and all the previous selected nodes
			for(Enumeration e = vtNodeSelected.elements();e.hasMoreElements();) {
				UINode uinode = (UINode)e.nextElement();
				if (uinode != null) {
					uinode.setSelected(false);
					uinode.setCut(false);
				}
			}
			vtNodeSelected.removeAllElements();
			ProjectCompendium.APP.setNodeSelected(false);			
		}
	}

	/**
	 * Return the last node selected, if one is selected.
	 * @return com.compendium.ui.UINode, the last currently selected node, else null.
	 */
	public UINode getSelectedNode() {
		return oNode;
	}

	/**
	 * Return all the currently selected nodes.
	 * @return Enumeration, all the currently selected nodes.
	 */
	public Enumeration getSelectedNodes() {
		return vtNodeSelected.elements();
	}

	/**
	 * Return a count of the number of nodes currently selected.
	 * @return int, a count of the number of nodes currently selected.
	 */
	public int getNumberOfSelectedNodes() {
		int count = 0;
		count = vtNodeSelected.size();
		return count;
	}

	/**
	 * Adds a particular node to the already selected group.
	 * @param node com.compendium.ui.UINode, the node to add the the group of selected nodes.
	 */
	public void addNode(UINode node) {
		if(!vtNodeSelected.contains(node))
			vtNodeSelected.addElement(node);
  	}

	/**
	 * Remove a particular node to from selected group of nodes.
	 * @param node com.compendium.ui.UINode, the node to remove from the group of selected nodes.
	 */
	public void removeNode(UINode node) {
		if(vtNodeSelected.contains(node))
			vtNodeSelected.removeElement(node);
  	}

	/**
	 * Clear all selected nodes from the group list.
	 */
	public void emptySelectedNodes() {
		vtNodeSelected.removeAllElements();
  	}


 /////////////////////////// LINK SELECTION AND DESELECTION ROUTINES ///////////////////////////

	/**
	 * Sets the given link selection state in this view and signals the aerial view of the event.
	 *
	 * @param link com.compendium.ui.UILink, the link to set the selection state for.
	 * @param mode, the selection state for the given link.
	 */
	public void setSelectedLink(UILink link, int mode) {

		((UIMapViewFrame)oViewFrame).setAerialSelectedLink(link, mode);
		processSelectedLink(link, mode);
	}

	/**
	 * Sets the given link selection state in this view without updating the aerial view
	 *
	 * @param link com.compendium.ui.UILink, the link to set the selection state for.
	 * @param mode, the selection state for the given link.
	 */
	public void processSelectedLink(UILink link, int mode) {

		if(mode == ICoreConstants.SINGLESELECT) {
			ProjectCompendium.APP.setNodeOrLinkSelected(true);

			if(oLink != null) {
				// bz - not sure what this code is here for ...
		  		if(oLink.equals(link))
					return;

				//deselect the previous link
				oLink.setSelected(false);
				oLink = link;

				// deselect all the previous selected links
				for(Enumeration e = vtLinkSelected.elements();e.hasMoreElements();) {
					UILink uilink = (UILink)e.nextElement();
					uilink.setSelected(false);
				}
				vtLinkSelected.removeAllElements();
				addLink(link);
	  		}
	  		else {
				// bz - not sure what oLink is used here for ...
				oLink = link;
				// deselect all the previous selected nodes
				for(Enumeration e = vtLinkSelected.elements();e.hasMoreElements();) {
					UILink uilink = (UILink)e.nextElement();
					uilink.setSelected(false);
				}

				// add this node
				vtLinkSelected.removeAllElements();
				addLink(link);
	  		}
		}

		if(mode == ICoreConstants.MULTISELECT) {
			ProjectCompendium.APP.setNodeOrLinkSelected(true);

			addLink(link);
		}

		if(mode == ICoreConstants.DESELECTALL) {

			if (getNumberOfSelectedNodes() == 0)
				ProjectCompendium.APP.setNodeOrLinkSelected(false);

			if(oLink != null) {
				oLink.setSelected(false);
				oLink = null;
			}

			// deselect all the previous selected links
			for(Enumeration e = vtLinkSelected.elements();e.hasMoreElements();) {
				UILink uilink = (UILink)e.nextElement();
				uilink.setSelected(false);
			}
			vtLinkSelected.removeAllElements();
		}
	}

	/**
	 * Return all the currently selected links.
	 * @return Enumeration, all the currently selected links.
	 */
	public Enumeration getSelectedLinks() {
		return vtLinkSelected.elements();
	}

	/**
	 * Return a count of the number of links currently selected.
	 * @return int, a count of the number of links currently selected.
	 */
	public int getNumberOfSelectedLinks() {
		return vtLinkSelected.size();
	}

	/**
	 * Adds a particular link to the already selected group.
	 * @param link com.compendium.ui.UILink, the link to add the the group of selected links.
	 */
	public void addLink(UILink link) {
		if(!vtLinkSelected.contains(link))
			vtLinkSelected.addElement(link);
  	}

	/**
	 * Remove a particular link to from selected group of links.
	 * @param link com.compendium.ui.UILink, the link to remove from the group of selected links.
	 */
	public void removeLink(UILink link) {
		if(vtLinkSelected.contains(link))
			vtLinkSelected.removeElement(link);
  	}

	/**
	 * Clear all selected links from the group list.
	 */
	public void emptySelectedLinks() {
		vtLinkSelected.removeAllElements();
	}

	/**
	 * Delete selected nodes and links in the view.
	 * @param edit, the PCEdit object to add the deleted object to for undo/redo purposes.
	 */
	public void deleteSelectedNodesAndLinks(PCEdit edit) {
		Hashtable links = new Hashtable(51);

		// delete the NODES selected
	  	int i = 0;

		String sHomeViewID = ProjectCompendium.APP.getHomeView().getId();
		String sInBoxID = ProjectCompendium.APP.getInBoxID();
		IModel model = ProjectCompendium.APP.getModel();
		for(Enumeration e = getSelectedNodes(); e.hasMoreElements(); i++) {

			UINode uinode = (UINode)e.nextElement();
			String sNodeID = uinode.getNode().getId();
			if (uinode.getType() != ICoreConstants.TRASHBIN 
					&& !sNodeID.equals(sInBoxID)) {

				NodeUI nodeui = uinode.getUI();

				// IF YOU SOMEHOW GET YOUR HOMEVIEW AS A NODE AND TRY AND DELETE IT,
				// JUST REMOVE FROM VIEW, DO NOT ACTUALLY DELETE IT!!!
				if (sNodeID.equals(sHomeViewID)) {
					// StoreLinks being deleted
					for(Enumeration es = uinode.getLinks();es.hasMoreElements();) {
						UILink uilink = (UILink)es.nextElement();
						links.put(uilink.getLink().getId(), uilink);
					}

					nodeui.deleteLinksforNode(uinode, edit);
					try {
						model.getViewService().removeMemberNode(model.getSession(), oView.getId() ,sNodeID);
						model.getViewService().purgeMemberNode(model.getSession(), oView.getId() ,sNodeID);
					}
					catch(Exception ex) {
						System.out.println("Unable to remove home view node from view = "+oView.getLabel()+" due to:\n"+ex.getMessage());
					}
					nodeui.removeFromUI(uinode);
				} else {
					
					// IF NODE ALREADY DELETED, DON'T TRY AND DELETE CHILDREN AGAIN
					// NEED TO CATCH NEVERENDING LOOP WHEN NODE CONTAINS ITSELF SOMEWHERE IN CHILDREN TREE
					boolean wasDeleted = false;
					try {
						if (model.getNodeService().isMarkedForDeletion(model.getSession(), uinode.getNode().getId())) {
							wasDeleted = true;
						}
					}
					catch (SQLException ex) {
						// WHAT TO DO?
					}
					boolean lastInstance = nodeui.removeFromDatamodel(uinode);
					//if (lastInstance || wasDeleted) {
						// StoreLinks being deleted					
						for(Enumeration es = uinode.getLinks();es.hasMoreElements();) {
							UILink uilink = (UILink)es.nextElement();
							links.put(uilink.getLink().getId(), uilink);
						}
						nodeui.deleteLinksforNode(uinode, edit);	
						edit.AddNodeToEdit(uinode);

						// IF NODE IS A VIEW AND IF NODE WAS ACTUALLY LAST INSTANCE AND HAS NOT ALREADY BEEN DELETED, DELETE CHILDREN
						if (uinode.getNode() instanceof View && lastInstance && !wasDeleted) {
							View childView = (View)uinode.getNode();
							UIViewFrame childViewFrame = ProjectCompendium.APP.getViewFrame(childView, childView.getLabel());
							if (childViewFrame instanceof UIMapViewFrame) {
								((UIMapViewFrame)childViewFrame).deleteChildren(childView);
							} else {
								((UIListViewFrame)childViewFrame).deleteChildren(childView);
							}
							// delete from ProjectCompendium.APP opened frame list.
							ProjectCompendium.APP.removeViewFromHistory(childView);		
						}	
						// NEED TO CALL THIS TO REMOVE NODE
						nodeui.removeFromUI(uinode);
					//}
				}
			}
		}

		// PURGE ALL LINKS NOT ASSOCIATED WITH A NODE (OTHERS WILL ALREADY BE MARK FOR DELETION ABOVE)
		for(Enumeration et = getSelectedLinks();et.hasMoreElements();) {

			UILink uilink = (UILink)et.nextElement();
			if (!links.containsKey(uilink.getLink().getId())) {
				LinkUI linkui = (LinkUI)uilink.getUI();
				linkui.purgeLink(uilink);

				// save link in case operation needs to be undone.
				edit.AddLinkToEdit (uilink);
			}
		}

		setSelectedNode(null, ICoreConstants.DESELECTALL);
		setSelectedLink(null, ICoreConstants.DESELECTALL);

		hideImages();
		repaint();
 	}

	
//////////////////////////////////////////////////////////////////////////////

	/**
	 * Adds a component to this view.
	 *
	 * @param c the component to be added.
	 * @param constraints an object expressing layout contraints for this component.
	 * @see java.awt.LayoutManager
	 */
	public void add(Component c, Object constraints) {

		if (c instanceof UINode) {
			UINode node = (UINode)c;
			UINode oldnode = (UINode)get(node.getNode().getId());
			if (oldnode == null)
				super.add(c, constraints);
		}
		else if (c instanceof UILink) {
			UILink link = (UILink)c;
			if (link.getLink() != null) {
				UILink oldlink = (UILink)get(link.getLink().getId());
				if (oldlink == null)
					super.add(c, constraints);
			}
			else {
				// DUMMY LINKS
				super.add(c, constraints);
			}
		}
		else {
			super.add(c, constraints);
		}

		// add drag and drop capabilities for nodes
		if (c instanceof UINode) {
			UINode node = (UINode)c;
			checkScale(node);
			checkFont(node);			
			node.addPropertyChangeListener(this);
			moveToFront(c);
		}
	}

	/**
	 * Removes a component from this view.
	 *
	 * @param c the component to be removed
	 * @see java.awt.LayoutManager
	 */
	public void remove(Component c) {

		super.remove(c);
		repaint();
	}

	/**
	 * Removes all components from this view,
	 * @see java.awt.LayoutManager
	 */
	public void removeAllComponents() {

		Component [] array = getComponentsInLayer((UIViewPane.NODE_LAYER).intValue());
		for(int i=0;i<array.length;i++) {
			JComponent object = (JComponent)array[i];
			remove(object);
		}

		Component [] array1 = getComponentsInLayer((UIViewPane.LINK_LAYER).intValue());
		for(int i=0;i<array1.length;i++) {
			JComponent object = (JComponent)array1[i];
			remove(object);
		}
		repaint();
	}

	/**
	 * Retrieves a component from this view based on it Id.
	 *
	 * @param id of the component to remove.
	 * @see java.awt.LayoutManager
	 */
	public void removeObject(String id) {

		Component [] array = getComponentsInLayer((UIViewPane.NODE_LAYER).intValue());
		for(int i=0;i<array.length;i++) {
			JComponent object = (JComponent)array[i];
			UINode uinode = (UINode)object;
			if(uinode.getNode().getId().equals(id))
				remove(uinode);
		}

		Component [] array1 = getComponentsInLayer((UIViewPane.LINK_LAYER).intValue());
		for(int i=0;i<array1.length;i++) {
			JComponent object = (JComponent)array1[i];
			UILink uilink = (UILink)object;
			if(uilink.getLink().getId().equals(id))
				remove(uilink);
		}
	}

	/**
	 * Retrieves a component from this view based on it Id.
	 *
	 * @param id of the component to return.
	 * @return Object, the object with the given id.
	 * @see java.awt.LayoutManager
	 */
	public Object get(String id) {

		Component [] array = getComponentsInLayer((UIViewPane.NODE_LAYER).intValue());
		for(int i=0;i<array.length;i++) {
			JComponent object = (JComponent)array[i];
			UINode uinode = (UINode)object;
			if(uinode != null && uinode.getNode() != null && uinode.getNode().getId().equals(id))
				return uinode;
		}

		Component [] array1 = getComponentsInLayer((UIViewPane.LINK_LAYER).intValue());
		for(int i=0;i<array1.length;i++) {
			JComponent object = (JComponent)array1[i];
			UILink uilink = (UILink)object;
			if(uilink != null && uilink.getLink() != null && uilink.getLink().getId().equals(id))
				return uilink;
		}
		return null;
	}

	/**
	 * Update the node icon indicators of the nodes in this view.
	 */
	public void refreshIconIndicators() {
		Component array[] = getComponentsInLayer((UIViewPane.NODE_LAYER).intValue());

		UINode uinode = null;
		for (int j = 0; j < array.length; j++) {
			uinode = (UINode)array[j];
			uinode.getUI().refreshBounds();
		}
	}

	/**
	 * Update the icon indicator of the node with the specific sNodeID.
	 */
	public void refreshNodeIconIndicators(String sNodeID) {
		Component array[] = getComponentsInLayer((UIViewPane.NODE_LAYER).intValue());

		UINode uinode = null;
		for (int j = 0; j < array.length; j++) {
			uinode = (UINode)array[j];
			if ( (uinode.getNode().getId()).equals(sNodeID) )
				uinode.getUI().refreshBounds();
		}
	}

	/**
	 * Go through all layers in this view and repaint their objects.
	 */
	public void validateComponents() {

		repaint();
		Component[] array0 = getComponentsInLayer((UIViewPane.BACKGROUND_LAYER).intValue());
		for(int i=0;i<array0.length;i++) {
			JComponent object = (JComponent)array0[i];
			object.revalidate();
			object.repaint();
		}

		Component [] array = getComponentsInLayer((UIViewPane.NODE_LAYER).intValue());
		for(int i=0;i<array.length;i++) {
			JComponent object = (JComponent)array[i];
			object.revalidate();
			object.repaint();
		}

		Component [] array1 = getComponentsInLayer((UIViewPane.LINK_LAYER).intValue());
		for(int i=0;i<array1.length;i++) {
			JComponent object = (JComponent)array1[i];
			object.revalidate();
			object.repaint();
		}
	}

	/**
	 * Return an instance of the right-click popup menu for this view.
	 * @return com.compendium.ui.UIViewPopupMenu, the popup menu for this view.
	 */
	public UIViewPopupMenu getPopupMenu() {
		if (viewPopup == null)
			viewPopup = new UIViewPopupMenu("View Popup menu", getUI());

		return viewPopup;
	}

	/**
	 * Create and display an instance of the right-click popup menu for this view.
	 * @param com.compendium.ui.plaf.ViewPaneUI, the ui object for this view required as a parameter for the popup.
	 * @param x, the x position of the trigger event for this request. Used to calculate the popup x position.
	 * @param y, the y position of the trigger event for this request. Used to calculate the popup y position.
	 */
	public void showPopupMenu(ViewPaneUI viewpaneui, int x, int y) {

		viewPopup = new UIViewPopupMenu("View Popup menu", viewpaneui);
		UIViewFrame viewFrame = oViewFrame;

		Dimension dim = ProjectCompendium.APP.getScreenSize();
		int screenWidth = dim.width - 70; //to accomodate for the scrollbar
		int screenHeight = dim.height-120; //to accomodate for the menubar...

		Point point = getViewFrame().getViewPosition();
		int realX = Math.abs(point.x - x)+20;
		int realY = Math.abs(point.y - y)+20;

		int endXCoordForPopUpMenu = realX + viewPopup.getWidth();
		int endYCoordForPopUpMenu = realY + viewPopup.getHeight();

		int offsetX = (screenWidth) - endXCoordForPopUpMenu;
		int offsetY = (screenHeight) - endYCoordForPopUpMenu;

		if(offsetX > 0)
			offsetX = 0;
		if(offsetY > 0)
			offsetY = 0;

		viewPopup.setCoordinates(realX+offsetX, realY+offsetY);
		viewPopup.setViewPane(this);
		viewPopup.show(viewFrame,realX+offsetX, realY+offsetY);
	}


// PRINT METHODS

	/**
	 * Caculate the size of the used map area and return this as a Dimension.
	 * @return Dimension, the size of the used map area
	 */
	public Dimension calculateSize() {

        //get all the nodes this UIViewPane contains
        Component nodes[] = getComponentsInLayer(UIViewPane.NODE_LAYER.intValue());

        int maxWidth = 0;
        int maxHeight = 0;
		int index = 0;

        //calculate the area which contains all the nodes in this UIViewPane for subsquent calculating
        //this should be implemented for only calculating once for a printing job.
        //at present, every call to this print() will excute this chunk of code again for
        //every page to be printed in the same printing job. Quite stupid codes.

        while(index < nodes.length) {

            UINode node = (UINode) nodes[index];

            //System.out.println("node x coord is: " + node.getX() + "\nnode y coord is: " + node.getY());

            int width = node.getX() + node.getWidth();
            int height = node.getY() + node.getHeight();
            if( width > maxWidth){
                maxWidth = width;
            }
            if( height > maxHeight){
                maxHeight = height;
            }
            index ++;
		}

        Dimension viewPaneSize = new Dimension( maxWidth, maxHeight);


		// IF HAS BACKGROUND IMAGE, CHECK IF SIZE GREATER THAN ABOVE AND IF SO, CHANGE.
		String image = oView.getImage();
		if (lblBackgroundLabel != null) {
			Dimension labelSize = lblBackgroundLabel.getPreferredSize();
			if (labelSize.width > viewPaneSize.width)
				viewPaneSize.width = labelSize.width;
			if(labelSize.height > viewPaneSize.height)
				viewPaneSize.height = labelSize.height;
		}

		return viewPaneSize;
	}


	/**
	 * Print the contents of this maps used area.
	 * @param g, the Graphics object to use for the paint.
	 * @param pageFormat, the format to use for the print.
	 * @param pageIndex, the index of the page being printed.
	 */
	public int print(Graphics g, PageFormat pageFormat, int pageIndex) throws PrinterException{

		Graphics2D g2d = (Graphics2D) g;

		int columnNum = 0;//the number of columns to be printed of current UIViewPane
		int rowNum = 0;//the number of rows to be printed

		int totalPageNum = 0;//the number of total pages on current UIViewPane

		double paperWidth = 0.0;
		double paperHeight = 0.0;

        Dimension viewPaneSize = calculateSize();

		//the width within which the printer is going to print
        paperWidth = pageFormat.getImageableWidth();

		//the height within which the printer will print
        paperHeight = pageFormat.getImageableHeight();

        columnNum = (int) Math.ceil(viewPaneSize.getWidth()/paperWidth);
        rowNum = (int) Math.ceil(viewPaneSize.getHeight()/paperHeight);

        totalPageNum = columnNum * rowNum;
        //above several line codes for calculating numbers of pages to be printed
        //they should be excuted once as well, I think

		//make sure no empty output
		if(columnNum == 0 || rowNum == 0 || pageIndex >= totalPageNum){
			return Printable.NO_SUCH_PAGE;
		}

		RepaintManager currentManager = RepaintManager.currentManager(this);
		currentManager.setDoubleBufferingEnabled(false);
		
		//paint the page to be printed
        paint(preparePage(g2d, pageFormat, pageIndex, columnNum, viewPaneSize));

		currentManager.setDoubleBufferingEnabled(true);
       
		return Printable.PAGE_EXISTS;
	}
	
	/**
	 * Prepare page to be printed.
     * It is assumed that the printing area begins at (0, 0) of current UIViewPane.
	 *
	 * @param g, the Graphics object to use for the paint.
	 * @param pageFormat, the format to use for the print.
	 * @param pageIndex, the index of the page being printed.
	 * @param columnNum, the number of the current column being printed.
	 * @param viewPaneSize, the size of the area being printed.
     */
	private Graphics2D preparePage(Graphics2D g2d, PageFormat pf, int pageIndex, int columnNum, Dimension viewPaneSize){

        int imageableX = (int) pf.getImageableX();
		int imageableY = (int) pf.getImageableY();
        int paperWidth = (int) pf.getImageableWidth();
        int paperHeight = (int) pf.getImageableHeight();

        //move the coordinates to printing starting coordinates
        g2d.translate(imageableX, imageableY);

        //caculate which page to render
        if(columnNum == 1){
            //move the coordinates vertically to the page to be printed
            g2d.translate(0, -pageIndex * paperHeight);
            
            //double scale = pf.getImageableWidth() / viewPaneSize.getWidth();                       
            //g2d.scale(scale, scale); //scale to the paper size, could be optional
        }
        else{
            //calculate which page should be current one
            int currentRow = pageIndex/columnNum;
            int currentColumn = pageIndex - currentRow * columnNum;

            //move the coordinates to the page to be printed
            g2d.translate( -currentColumn * paperWidth, -currentRow * paperHeight);
        }

        return g2d;
	}


// ZOOM METHODS

	/**
	 * Return the current view scale.
	 */
	public double getScale() {
		return currentScale;
	}
	
	/**
	 * Scale the nodes and links and their locations in this view.
	 */
	public void scale() {
		Component [] array = getComponentsInLayer((UIViewPane.NODE_LAYER).intValue());
		for(int i=0;i<array.length;i++) {
			UINode node = (UINode)array[i];
			scaleNode(node, currentScale);
		}
	}

	/**
	 * Scale the given node to the given scale.
	 *
	 * @param node, the node to scale.
	 * @param scale, the scale to use.
	 */
	public void scaleNode(UINode node, double scale) {

		NodePosition pos = node.getNodePosition();
		
		if (scale != 0.0 && scale != 1.0) {
			node.setScale(scale);
			
			// SCALE FONT - setFont scales font.
			node.setFont(node.getFont());

			// SCALE ICON - calls setIcon which scales to scale set in node.
			node.restoreIcon();

			// SCALE LOCATION - setLocation scales location	
			node.setLocation(pos.getPos());

			// SCALE LINK ARROW HEAD AND TEXT
			AffineTransform trans=new AffineTransform();
			trans.setToScale(scale, scale);
			node.scaleLinks(trans);
		}
		else {
			node.setScale(1.0);

			// RESET FONT
			node.setFont(new Font(pos.getFontFace(), pos.getFontStyle(), pos.getFontSize()));

			// RESET ICON
			node.restoreIcon();

			// RESTORE LOCATION TO 100%
			node.setLocation(pos.getPos());

			// RESTORE THE LINKS
			node.scaleLinks(null);
		}
	}

	/**
	 * Used when new node is added to view to check if it needs scaling, and if it does, do it.
	 * @param node com.compendium.ui.UINode, the node to scale.
	 */
	public void checkScale(UINode node) {

		double currentNodeScale = node.getScale();

		// ONLY RESCALE IF NECESSARY
		if (currentNodeScale != currentScale) {

			node.setScale(currentScale);

			// SCALE FONT - setFont scales font.
			node.setFont(node.getFont());

			// SCALE ICON - calls setIcon which scales to scale set in node.			
			node.restoreIcon();
			//node.setIcon(icon);

			// SCALE LOCATION - setLocation scales location
			node.setLocation(node.getNodePosition().getPos());
		}
	}

	/**
	 * Used when new node is added to view to check if it needs font adjustment, and if it does, do it.
	 * @param node com.compendium.ui.UINode, the node to scale.
	 */
	public void checkFont(UINode node) {
		int fontZoom = ProjectCompendium.APP.getToolBarManager().getTextZoom();
		if (fontZoom != 0) {
			Font font = node.getFont();
			node.setFontSize(font.getSize()+fontZoom);
		}
	}

	/**
	 * Return the font size to its default 
	 * (To what is stored in the database with current map zoom applied)
	 */
	public void onReturnTextToActual() {
		Component[] nodes = getComponentsInLayer(UIViewPane.NODE_LAYER.intValue());
		int count = nodes.length;
		UINode node = null;
		UINodeContentDialog dlg = null;
		for (int i=0; i<count;i++) {
			node = (UINode)nodes[i];
			node.setDefaultFont();
			dlg = node.getCurrentContentDialog();
			if (dlg != null) {
				dlg.refreshFont();
			}
		}
		
		Component[] links = getComponentsInLayer(UIViewPane.LINK_LAYER.intValue());
		count = links.length;
		UILink link = null;
		for (int i=0; i<count;i++) {
			link = (UILink)links[i];
			link.setDefaultFont();
		}		
		
		refreshLabels();
	}
	
	/**
	 * Increase the currently dislayed font size by one point.
	 * (This does not change the stored value in the database)
	 */
	public void onIncreaseTextSize() {
		Component[] nodes = getComponentsInLayer(UIViewPane.NODE_LAYER.intValue());
		int count = nodes.length;
		UINode node = null;
		UINodeContentDialog dlg = null;		
		for (int i=0; i<count;i++) {
			node = (UINode)nodes[i];
			node.increaseFontSize();
			dlg = node.getCurrentContentDialog();
			if (dlg != null) {
				dlg.refreshFont();
			}			
		}
		
		Component[] links = getComponentsInLayer(UIViewPane.LINK_LAYER.intValue());
		count = links.length;
		UILink link = null;
		for (int i=0; i<count;i++) {
			link = (UILink)links[i];
			link.increaseFontSize();
		}	
		
		refreshLabels();
	}
	
	/**
	 * Reduce the currently dislayed font size by one point.
	 * (This does not change the stored value in the database)
	 */
	public void onReduceTextSize() {
		
		Component[] nodes = getComponentsInLayer(UIViewPane.NODE_LAYER.intValue());
		int count = nodes.length;
		UINode node = null;
		UINodeContentDialog dlg = null;		
		for (int i=0; i<count;i++) {
			node = (UINode)nodes[i];
			node.decreaseFontSize();
			dlg = node.getCurrentContentDialog();
			if (dlg != null) {
				dlg.refreshFont();
			}			
		}
		
		Component[] links = getComponentsInLayer(UIViewPane.LINK_LAYER.intValue());
		count = links.length;
		UILink link = null;
		for (int i=0; i<count;i++) {
			link = (UILink)links[i];
			link.decreaseFontSize();
		}		
		
		refreshLabels();
	}
	
//*********************** PROPERTY CHANGE LISTENER *************************/

	/**
	 * Handles property change events.
	 * @param evt, the associated PropertyChangeEvent object.
	 */
	public void propertyChange(PropertyChangeEvent evt) {

	    String prop = evt.getPropertyName();

		Object source = evt.getSource();
	    Object oldvalue = evt.getOldValue();
	    Object newvalue = evt.getNewValue();
	    
	    if (source instanceof View) {
		    if (prop.equals(View.LINK_ADDED)) {
				Link link = (Link)newvalue;
				((UIMapViewFrame)oViewFrame).addAerialLink(link);
			}
		    else if (prop.equals(View.LINK_REMOVED)) {
				Link link = (Link)newvalue;
				((UIMapViewFrame)oViewFrame).removeAerialLink(link);
			}
		    else if (prop.equals(View.NODE_ADDED)) {
				NodePosition oNodePos = (NodePosition)newvalue;
				((UIMapViewFrame)oViewFrame).addAerialNode(oNodePos);

				// IF RECODRING or REPLAYING A MEETING, SEND A NODE ADDED EVENT
				if (ProjectCompendium.APP.oMeetingManager != null && ProjectCompendium.APP.oMeetingManager.captureEvents()) {

					// IF NODE NOT ALREADY THERE, SEND EVENT
					UINode oNode = (UINode)get(oNodePos.getNode().getId());
					if (oNode == null) {
						ProjectCompendium.APP.oMeetingManager.addEvent(
							new MeetingEvent(ProjectCompendium.APP.oMeetingManager.getMeetingID(),
											 ProjectCompendium.APP.oMeetingManager.isReplay(),
											 MeetingEvent.NODE_ADDED_EVENT,
											 oNodePos));
					}
				}
			}
		    else if (prop.equals(View.NODE_TRANSCLUDED)) {
				NodePosition oNodePos = (NodePosition)newvalue;
				((UIMapViewFrame)oViewFrame).addAerialNode(oNodePos);

				// IF RECODRING or REPLAYING A MEETING, SEND A NODE TRANSCLUDED EVENT
				if (ProjectCompendium.APP.oMeetingManager != null && ProjectCompendium.APP.oMeetingManager.captureEvents()) {

					// IF NODE NOT ALREADY THERE, SEND EVENT
					UINode oNode = (UINode)get(oNodePos.getNode().getId());
					if (oNode == null) {
						ProjectCompendium.APP.oMeetingManager.addEvent(
							new MeetingEvent(ProjectCompendium.APP.oMeetingManager.getMeetingID(),
											 ProjectCompendium.APP.oMeetingManager.isReplay(),
											 MeetingEvent.NODE_TRANSCLUDED_EVENT,
											 oNodePos));
					}
				}
			}
		    else if (prop.equals(View.NODE_REMOVED)) {

				NodeSummary node = (NodeSummary)newvalue;
				((UIMapViewFrame)oViewFrame).removeAerialNode(node);

				// IF RECODRING or REPLAYING A MEETING, SEND A NODE REMOVED EVENT
				if (ProjectCompendium.APP.oMeetingManager != null && ProjectCompendium.APP.oMeetingManager.captureEvents()) {

					ProjectCompendium.APP.oMeetingManager.addEvent(
						new MeetingEvent(ProjectCompendium.APP.oMeetingManager.getMeetingID(),
										 ProjectCompendium.APP.oMeetingManager.isReplay(),
										 MeetingEvent.NODE_REMOVED_EVENT,
										 oView,
										 node));
				}
			}
		}   
		else if (source instanceof UINode) {
		    if (prop.equals(UINode.ROLLOVER_PROPERTY)) {
				UINode node = (UINode)source;
				((UIMapViewFrame)oViewFrame).setAerialRolloverNode(node, ((Boolean)newvalue).booleanValue());
			}
		    //else if (prop.equals(UINode.TYPE_PROPERTY)) {
			//	UINode node = (UINode)source;
			//	((UIMapViewFrame)oViewFrame).setAerialNodeType(node, ((Integer)newvalue).intValue());
			//}
			else if (prop.equals(NodePosition.POSITION_PROPERTY)) {
				UINode uinode = (UINode)source;
				Point oPoint = (Point)newvalue;
				Point transPoint = UIUtilities.transformPoint(oPoint.x, oPoint.y, currentScale);

				// CHECK THAT THIS NODE WAS NOT THE ONE ORIGINATING THE EVENT
				Point location = uinode.getLocation();
				if (location.x != transPoint.x && location.y != transPoint.y) {
					uinode.setBounds(transPoint.x, transPoint.y, uinode.getWidth(), uinode.getHeight());
					uinode.updateLinks();
				}
			}
		}
	}

	/**
	 * Clean up the components and variables used by this class to help with garbarge collection.
	 */
	public void cleanUp() {

		Component[] array0 = getComponentsInLayer((UIViewPane.BACKGROUND_LAYER).intValue());
		for(int i=0;i<array0.length;i++) {
			JComponent object = (JComponent)array0[i];
			object = null;
		}

		Component [] array = getComponentsInLayer((UIViewPane.NODE_LAYER).intValue());
		for(int i=0;i<array.length;i++) {
			JComponent object = (JComponent)array[i];
			UINode uinode = (UINode)object;
			uinode.cleanUp();
			uinode = null;
		}

		Component [] array1 = getComponentsInLayer((UIViewPane.LINK_LAYER).intValue());
		for(int i=0;i<array1.length;i++) {
			JComponent object = (JComponent)array1[i];
			object = null;
		}

		if (this.getUI() != null)
			this.getUI().uninstallUI(this);

		oView					= null;
		oViewFrame			= null;
		oViewPaneUI			= null;
		oNode					= null;
		oLink					= null;
		dropTarget = null;
		oScribblePad			= null;

		if (vtNodeSelected != null)
			vtNodeSelected.removeAllElements();
		vtNodeSelected = null;

		if (vtLinkSelected != null)
			vtLinkSelected.removeAllElements();
		vtLinkSelected = null;

		if (tagPopups != null)
			tagPopups.clear();
		tagPopups = null;

		if (detailPopups != null)
			detailPopups.clear();
		detailPopups = null;

		if (viewsPopups != null)
			viewsPopups.clear();
		viewsPopups = null;
	}
}
