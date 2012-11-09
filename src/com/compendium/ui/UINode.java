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

import java.awt.*;
import java.awt.image.*;
import java.awt.geom.*;
import java.awt.event.*;
import java.awt.dnd.*;
import java.awt.datatransfer.*;
import java.sql.SQLException;

import java.beans.*;
import java.util.*;
import java.io.*;
import java.net.URI;

import javax.swing.*;
import javax.help.*;

import com.compendium.LanguageProperties;
import com.compendium.ProjectCompendium;
import com.compendium.ui.plaf.NodeUI;
import com.compendium.ui.popups.*;
import com.compendium.ui.linkgroups.*;
import com.compendium.ui.dialogs.UINodeContentDialog;
import com.compendium.core.datamodel.*;
import com.compendium.core.ICoreConstants;
import com.compendium.meeting.*;

/**
 * Holds the data for and handles the events of a node in a map.
 *
 * @author	Mohammed Sajid Ali / Michelle Bachler
 */
public class UINode extends JComponent implements PropertyChangeListener, SwingConstants,
		Transferable, DropTargetListener, DragSourceListener, DragGestureListener {

	/** A reference to the text property for PropertyChangeEvents.*/
    public static final String TEXT_PROPERTY 		= "text"; //$NON-NLS-1$

	/** A reference to the icon property for PropertyChangeEvents.*/
    public static final String ICON_PROPERTY 		= "icon"; //$NON-NLS-1$

	/** A reference to the children property for PropertyChangeEvents.*/
    public final static String CHILDREN_PROPERTY 	= "children"; //$NON-NLS-1$

	/** A reference to the rollover property for PropertyChangeEvents.*/
    public final static String ROLLOVER_PROPERTY	= "rollover"; //$NON-NLS-1$

	/** A reference to the node type property for PropertyChangeEvents.*/
    public final static String TYPE_PROPERTY		= "nodetype"; //$NON-NLS-1$

	/** A reference to the selected property for PropertyChangeEvents.*/
    public static final String SELECTED_PROPERTY 	= "selected"; //$NON-NLS-1$

	/** The default font to use for node labels.*/
    private static final Font  NODE_FONT = new Font("Sans Serif", Font.PLAIN, 12); //$NON-NLS-1$

 	/** The DataFlavour for external string based drag and drop operations.*/
 	public static final DataFlavor plainTextFlavor = DataFlavor.plainTextFlavor;

	/** The DataFlavour for internal string based drag and drop operations.*/
  	public static final DataFlavor localStringFlavor = DataFlavor.stringFlavor;

  	/** The DataFlavour for external drag and drop of file reference nodes **/
  	public static final DataFlavor fileListFlavor = DataFlavor.javaFileListFlavor;

  	/** Use a separate DataFlavor for Linux to work around a bug in the linux java vm
  	 * see bugs.sun.com/bugdatabase/view_bug.do?bug_id=4899516 
  	 * This is not important for dropping files. But when dragging a file in i.e. GNOME when
  	 * we'd use the stringFlavour GNOME would asks for a file name, wheres it creates a file
  	 * with the correct file name when using the uri-list flavour */  	
  	public static DataFlavor uriListFlavor = null;

	/** The DataFlavour for internal object based drag and drop operations.*/
	public static 		DataFlavor nodeFlavor 			= null;

	/** The deafult node icon for this node.*/
	private ImageIcon		oDefaultIcon				= null;

	/** The current node icon for this node.*/
	private ImageIcon		oCurrentIcon				= null;

	/** Is this node currently selected?*/
	private boolean			bSelected					= false;

	/** Has this node been cut?*/
	private boolean			bCut						= false;

	/** Has this node been rolloved over?*/
	private boolean			bRollover					= false;

	/** Indicates if the image has been scaled.*/
	private boolean			bIsImageScaled				= false;

	/** The label text for this node.*/
	private String			sText						= ""; //$NON-NLS-1$

	/** The distance for the gap between the node icon and its text.*/
	private int			nIconTextGap					= 4;

	/** The current font to use for the node label text.*/
	protected Font		oFont							= null;

	/** A List of the Link objects associated with this node.*/
	private Hashtable	htLinks							= new Hashtable();

	/** The drag source object associated with this node.*/
	private DragSource dragSource = null;

	/** The drop target object associated with this node.*/
	private DropTarget dropTarget = null;

	/** The node right-click popup menu associated with this node - null if one has not been opened yet.*/
	private UINodePopupMenu			nodePopup			= null;

	/** The node contents dialog associated with this node - null if one has not been opened yet.*/
	private UINodeContentDialog 	contentDialog		= null;

	/** The node data object associated with this UINode.*/
	private NodeSummary		oNode						= null;

	/** The NodePosition object associated with this node.*/
	private NodePosition  	oPos						= null;

	/** The node type of this node.*/
	private int				oNodeType					= -1;

	/** The current scale factor for this node in its parent view.*/
	private double scale 								= 1.0;

	/** A local reference to the name of the current computer platform.*/
	//private String os 									= "";

	private Date			focusGainedDate				= null;
	
	private String 			originalLabel				= null;
	
	/** The user author name of the current user */
	private String 			sAuthor = ""; //$NON-NLS-1$

	/**
	 * Create a new UINode instance with the given NodePosition object for data.
	 * @param nodePos the object with the node data for this UINode.
	 * @param sAuthor the author name of the current user.
	 * @param sUserID the id of the current user.
	 */
	public UINode(NodePosition nodePos, String sAuthor) {
	    //os = ProjectCompendium.platform.toLowerCase();
	    /* set the uriListFlavor */
	    try {
			uriListFlavor = new DataFlavor("text/uri-list;class=java.lang.String"); //$NON-NLS-1$
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		}		
	    
	    dragSource = new DragSource();
	    dragSource.createDefaultDragGestureRecognizer((Component)this, DnDConstants.ACTION_COPY, this);

	    dropTarget = new DropTarget(this, this);
	    nodeFlavor = new DataFlavor(this.getClass(), "UINode"); //$NON-NLS-1$

	    oPos = nodePos;

	    setDefaultFont();

	    this.setCursor(new Cursor(java.awt.Cursor.HAND_CURSOR));
	    this.sAuthor = sAuthor;
	    
	    addFocusListener( new FocusListener() {
			public void focusGained(FocusEvent e) {
				focusGainedDate = new Date();
				originalLabel = oPos.getNode().getLabel();
			    repaint();
			}
			public void focusLost(FocusEvent e) {

				if (ProjectCompendium.APP.oMeetingManager != null && ProjectCompendium.APP.oMeetingManager.captureEvents()
						&& (ProjectCompendium.APP.oMeetingManager.getMeetingType() == MeetingManager.RECORDING)) {

					Date focusLostDate = new Date();
					// If the node had the focus for more than 5 seconds, record the event.
					if ( (focusLostDate.getTime()) - (focusGainedDate.getTime()) > 5000) {
						ProjectCompendium.APP.oMeetingManager.addEvent(
								new MeetingEvent(ProjectCompendium.APP.oMeetingManager.getMeetingID(),
												 ProjectCompendium.APP.oMeetingManager.isReplay(),
												 MeetingEvent.NODE_FOCUSED_EVENT,
												 oPos.getView(),
												 oPos.getNode()));
					}
				}
				
				if (oNode == null) {
					oNode = oPos.getNode();
				}
				
				String sUserName="Unknown";
				Model oModel = (Model)ProjectCompendium.APP.getModel();
				if (oModel != null) {
					sUserName = ProjectCompendium.APP.getModel().getUserProfile().getUserName();
				}
				try {
					if (oNode.flushLabel(sUserName)) {
						NodeUI nodeui = getUI();	// Label length changed, so update node's view position
						nodeui.flushPosition();
					} 
				} catch (SQLException e1) {
					e1.printStackTrace();
					System.out.println("Error: (UINode.showContentDialog) \n\n"+e1.getMessage()); //$NON-NLS-1$
				} catch (ModelSessionException e2) {
					e2.printStackTrace();
					System.out.println("Error: (UINode.showContentDialog) \n\n"+e2.getMessage()); //$NON-NLS-1$
				}

			    getUI().resetEditing();
			    repaint();
			}
	    });

	    NodeSummary node = nodePos.getNode();
    	setNode(node);
	    updateUI();
	}


/*** DND EVENTS ***/

// TRANSFERABLE

    /**
     * Returns an array of DataFlavor objects indicating the flavors the data
     * can be provided in.
     * @return an array of data flavors in which this data can be transferred
     */
	public DataFlavor[] getTransferDataFlavors() {
		DataFlavor[] flavs = null;
		
		if (getType() == ICoreConstants.REFERENCE) {
		    flavs = new DataFlavor[] {	DataFlavor.javaFileListFlavor, uriListFlavor };			
		}
		else {
		    flavs = new DataFlavor[] {	UINode.nodeFlavor,
		    						UINode.plainTextFlavor,
	    							UINode.localStringFlavor};
		}

	    return flavs;
	}

    /**
     * Returns whether or not the specified data flavor is supported for
     * this object.
     * @param flavor the requested flavor for the data
     * @return boolean indicating whether or not the data flavor is supported
     */
	public boolean isDataFlavorSupported(DataFlavor flavor) {
		if ((flavor == UINode.fileListFlavor) 
				&& (getType() == ICoreConstants.REFERENCE))			
			return true;
		// for Linux uriListFlavor is supported for drag and drop for reference nodes
		if  ((flavor == uriListFlavor) 
				&& (getType() == ICoreConstants.REFERENCE))
			return true;
		if (flavor.getHumanPresentableName().equals("UINode") || //$NON-NLS-1$
	        flavor == UINode.plainTextFlavor ||
	        flavor == UINode.localStringFlavor) {
			return true;
		}

	    return false;
	}

    /**
     * Returns an object which represents the data to be transferred.  The class
     * of the object returned is defined by the representation class of the flavor.
     *
     * @param flavor the requested flavor for the data
     * @see DataFlavor#getRepresentationClass
     * @exception IOException                if the data is no longer available in the requested flavor.
     * @exception UnsupportedFlavorException if the requested data flavor is not supported.
     */
	public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {

	    //System.out.println("in getTransferData flavour = "+flavor.getHumanPresentableName());

		if (flavor.equals(UINode.plainTextFlavor)) {
	    	String charset = flavor.getParameter("charset").trim(); //$NON-NLS-1$
      		if(charset.equalsIgnoreCase("unicode")) { //$NON-NLS-1$
				return new ByteArrayInputStream(oNode.getId().getBytes("Unicode")); //$NON-NLS-1$
			}
			else {
				return new ByteArrayInputStream(oNode.getId().getBytes("iso8859-1")); //$NON-NLS-1$
			}
    	}
		else if(uriListFlavor.equals(flavor))
		{			
			// Linux dnd uses URI as file://, http://, ...
			try {
				
				URI nodeSource = new URI(getNode().getSource());
    			
				if(LinkedFileDatabase.isDatabaseURI(nodeSource))
				{  
					LinkedFile lf = new LinkedFileDatabase(nodeSource);
					// create temporary file to dnd
					File tempFile = lf.getFile(ProjectCompendium.temporaryDirectory);   				
					// delete the temporary file when the jvm exits
					// any Drag and Drop operations should be completed by then
					tempFile.deleteOnExit();
					return tempFile.toURI().toString();
				}
				else
				{
					// if it is a file:// URI return a string representation of it
					return nodeSource.toString();
				}
			}
			catch (Exception e) {
				e.printStackTrace();
				throw new UnsupportedFlavorException(flavor);
			}				
		}
    	
		else if (UINode.localStringFlavor.equals(flavor)) {
			return oNode.getId();
    	}		
		
    	else if (DataFlavor.javaFileListFlavor.equals(flavor)) {
    		try {
    			URI nodeSource = new URI(getNode().getSource());
    			if(LinkedFileDatabase.isDatabaseURI(nodeSource))
    			{  
    				LinkedFile lf = new LinkedFileDatabase(nodeSource);
					// create temporary file to dnd
    				File tempFile = lf.getFile(ProjectCompendium.temporaryDirectory);   				
    				// delete the temporary file when the jvm exits
    				// any Drag and Drop operations should be completed by then
    				tempFile.deleteOnExit();
    				return Collections.singletonList(tempFile);
    			}
    			else
    			{
    				File f = new File(nodeSource);
    				return Collections.singletonList(f);
    			}
    		}
    		catch (Exception e) {
    			e.printStackTrace();
    			throw new UnsupportedFlavorException(flavor);
    		}
    	}		
	    else if (flavor.getHumanPresentableName().equals("UINode")) { //$NON-NLS-1$
			return (Object)new String(oPos.getView().getId()+"/"+oNode.getId()); //$NON-NLS-1$
	    }
	    else
			throw new UnsupportedFlavorException(flavor);
	}

//SOURCE

	/**
	 * A <code>DragGestureRecognizer</code> has detected
	 * a platform-dependent drag initiating gesture and
	 * is notifying this listener
	 * in order for it to initiate the action for the user.
	 * <P>Currently only used to create links on the Mac platform.</p>
	 * @param e the <code>DragGestureEvent</code> describing the gesture that has just occurred.
	 */
	public void dragGestureRecognized(DragGestureEvent e) {

		InputEvent in = e.getTriggerEvent();

		if (in instanceof MouseEvent) {
			MouseEvent evt = (MouseEvent)in;
			boolean isLeftMouse = SwingUtilities.isLeftMouseButton(evt);

			if ((isLeftMouse && evt.getID() == MouseEvent.MOUSE_PRESSED) && (
					(ProjectCompendium.isWindows && evt.isAltDown()) 
					|| (ProjectCompendium.isLinux && evt.isControlDown())
			)) {
				try {
					DragSource source = (DragSource)e.getDragSource();
					source.startDrag(e, DragSource.DefaultCopyDrop, getViewPane(), getViewPane());
				}
				catch(Exception io) {
					io.printStackTrace();
				}
			}
		}


		/*InputEvent in = e.getTriggerEvent();

	    if (in instanceof MouseEvent) {
			MouseEvent evt = (MouseEvent)in;
			boolean isLeftMouse = SwingUtilities.isLeftMouseButton(evt);
			//boolean isRightMouse = SwingUtilities.isRightMouseButton(evt);

			if (isLeftMouse && evt.getID() == MouseEvent.MOUSE_PRESSED && evt.isAltDown()) {
				try {
					DragSource source = (DragSource)e.getDragSource();
				    source.startDrag(e, DragSource.DefaultCopyDrop, this, this);
				}
				catch(Exception io) {
				    io.printStackTrace();
				}
			}
		}

			/*if (os.indexOf("windows") != -1) {
			    if (isRightMouse || (isLeftMouse && isAltDown)) { // creating links
				System.out.println("In dragGestureRecognized = right mouse click recognised");
				DragSource source = (DragSource)e.getDragSource();
				source.addDragSourceListener(this);

				System.out.println("source = "+source);
				try {
				    System.out.println("DragSource.DefaultLinkDrop = "+DragSource.DefaultLinkDrop);
				    source.startDrag(e, DragSource.DefaultLinkDrop, this, this);
				    System.out.println("After source.startDrag");
				}
				catch(Exception io) {
				    System.out.println("IN CATCH "+io.getMessage());
				    io.printStackTrace();
				}
			    }
			}
			else {
		 */

		/*if (ProjectCompendium.isMac) {
				boolean isLeftMouse = SwingUtilities.isLeftMouseButton(evt);
				//boolean isRightMouse = SwingUtilities.isRightMouseButton(evt);
				boolean isAltDown = evt.isAltDown();

				//boolean isMiddleMouse = SwingUtilities.isMiddleMouseButton(evt);

			    if (isLeftMouse && isAltDown) { // creating links
				//if (isRightMouse) {
					DragSource source = (DragSource)e.getDragSource();

					/*DragGestureRecognizer dgr = e.getSourceAsDragGestureRecognizer();
					int act = e.getDragAction();
					Point ori = e.getDragOrigin();
					ArrayList evs = new ArrayList();

					for (Iterator it=e.iterator(); it.hasNext();) {
					    Object obj = it.next();
					    if (obj.equals(evt)) {
						MouseEvent me = new MouseEvent((Component)evt.getSource(), evt.getID(), evt.getWhen(),
							0, evt.getX(), evt.getY(), evt.getClickCount(), false, evt.getButton());
						System.out.println("AFTER CHANGE mouse event "+me.toString());

						evs.add(me);
					    }
					    else {
						evs.add(obj);
					    }
					}

					java.util.List evsList = (java.util.List)evs;
					DragGestureEvent newE = new DragGestureEvent(dgr, act, ori, evsList);
		 */

		//System.out.println("source = "+source);
		/*try {
					    source.startDrag(e, DragSource.DefaultLinkDrop, this, this);
					}
					catch(Exception io) {
					    io.printStackTrace();
					}
				}
			}*/
		//}
	}

    /**
     * This method is invoked to signify that the Drag and Drop
     * operation is complete. The getDropSuccess() method of
     * the <code>DragSourceDropEvent</code> can be used to
     * determine the termination state. The getDropAction() method
     * returns the operation that the drop site selected
     * to apply to the Drop operation. Once this method is complete, the
     * current <code>DragSourceContext</code> and
     * associated resources become invalid.
	 * <p>Here, clears dummy links draw while creating the new link. </p>
     *
     * @param e the <code>DragSourceDropEvent</code>
     */
	public void dragDropEnd(DragSourceDropEvent e) {
	    //getUI().clearDummyLinks();
	}

    /**
     * Called as the cursor's hotspot enters a platform-dependent drop site.
     * This method is invoked when all the following conditions are true:
     * <UL>
     * <LI>The cursor's hotspot enters the operable part of a platform-
     * dependent drop site.
     * <LI>The drop site is active.
     * <LI>The drop site accepts the drag.
     * </UL>
	 * HERE THE METHOD DOES NOTHING.
     *
     * @param e the <code>DragSourceDragEvent</code>
     */
	public void dragEnter(DragSourceDragEvent e) {
	    //System.out.println("IN drag Enter on Source");
	}

    /**
     * Called as the cursor's hotspot exits a platform-dependent drop site.
     * This method is invoked when any of the following conditions are true:
     * <UL>
     * <LI>The cursor's hotspot no longer intersects the operable part
     * of the drop site associated with the previous dragEnter() invocation.
     * </UL>
     * OR
     * <UL>
     * <LI>The drop site associated with the previous dragEnter() invocation
     * is no longer active.
     * </UL>
     * OR
     * <UL>
     * <LI> The current drop site has rejected the drag.
     * </UL>
	 * HERE THE METHOD DOES NOTHING.
     *
     * @param e the <code>DragSourceEvent</code>
     */
	public void dragExit(DragSourceEvent e) {
	    //System.out.println("IN drag Exit of Source");
	}

    /**
     * Called as the cursor's hotspot moves over a platform-dependent drop site.
     * This method is invoked when all the following conditions are true:
     * <UL>
     * <LI>The cursor's hotspot has moved, but still intersects the
     * operable part of the drop site associated with the previous
     * dragEnter() invocation.
     * <LI>The drop site is still active.
     * <LI>The drop site accepts the drag.
     * </UL>
	 * <p>Draws the dummy links, while link crateion is in progress.</p>
     *
     * @param dsde the <code>DragSourceDragEvent</code>
     */
	public void dragOver(DragSourceDragEvent e) {
	    //System.out.println("draw dummy links and dragsourcedrag event at "+e.getLocation());
	    //getUI().drawDummyLinks(e.getLocation());
	}

    /**
     * Called when the user has modified the drop gesture.
     * This method is invoked when the state of the input
     * device(s) that the user is interacting with changes.
     * Such devices are typically the mouse buttons or keyboard
     * modifiers that the user is interacting with.
	 * HERE THE METHOD DOES NOTHING.
     *
     * @param e the <code>DragSourceDragEvent</code>
     */
	public void dropActionChanged(DragSourceDragEvent e) {
	    //System.out.println("IN dropActionChanged of Source");
	}

// TARGET

    /**
     * Called if the user has modified
     * the current drop gesture.
     * <P>HERE DOES NOTHING</P>
     * @param e the <code>DropTargetDragEvent</code>
     */
	public void dropActionChanged(DropTargetDragEvent e) {
	    //System.out.println("IN dropActionChanged of Target");
	}

    /**
     * Called when a drag operation is ongoing, while the mouse pointer is still
     * over the operable part of the drop site for the <code>DropTarget</code>
     * registered with this listener.
     * <P>HERE DOES NOTHING</P>
     * @param e the <code>DropTargetDragEvent</code>
     */
	public void dragOver(DropTargetDragEvent e) {
	     //System.out.println("dragtargetdrag event at "+e.getLocation());
	}

    /**
     * Called while a drag operation is ongoing, when the mouse pointer has
     * exited the operable part of the drop site for the
     * <code>DropTarget</code> registered with this listener.
     * <P>HERE DOES NOTHING</P>
     * @param e the <code>DropTargetEvent</code>
     */
	public void dragExit(DropTargetEvent e) {
	    //System.out.println("In drag exit of Target");
	}

    /**
     * Called while a drag operation is ongoing, when the mouse pointer enters
     * the operable part of the drop site for the <code>DropTarget</code>
     * registered with this listener.
     * <P>HERE DOES NOTHING</P>
     * @param e the <code>DropTargetDragEvent</code>
     */
	public void dragEnter(DropTargetDragEvent e) {
	    //System.out.println("dragEnter - about to accept DnDConstants.ACTION_LINK");
	    //e.acceptDrag(DnDConstants.ACTION_LINK);
	    //e.acceptDrag(DnDConstants.ACTION_MOVE);
	}

    /**
     * Called when the drag operation has terminated with a drop on
     * the operable part of the drop site for the <code>DropTarget</code>
     * registered with this listener.
     * <p>
	 * Process a drop for createing links - CURRENTLY ONLY ON THE MAC (WINDOW/LINUX use mouse events).
     * <P>
     * @param e the <code>DropTargetDropEvent</code>
     */
	public void drop(DropTargetDropEvent e) {
	    /*DropTarget drop = (DropTarget)e.getSource();

	    if (drop.getComponent() instanceof UINode) {

			UINode uinode = null;
			UIViewFrame oViewFrame = null;
			UIViewPane oViewPane = null;
			String sNodeID = "";
			try {
				Transferable trans = e.getTransferable();
				Object obj = trans.getTransferData(UINode.nodeFlavor);

				if (obj != null && (obj instanceof String)) {
				    String path = (String)obj;
				    int index = path.indexOf("/");
				    String sViewID = path.substring(0, index);
				    sNodeID = path.substring(index+1);

					View view  = oNode.getModel().getView(sViewID);
					if (view != null) {
						oViewFrame = ProjectCompendium.APP.getViewFrame(view, view.getLabel());
						oViewPane = ((UIMapViewFrame)oViewFrame).getViewPane();
				    	if (oViewPane != null) {
							Object obj2 = oViewPane.get(sNodeID);
							if (obj2 instanceof UINode) {
					    		uinode = (UINode)obj2;
							}
						}
			    	}
				}
			}
			catch(IOException io) {
				io.printStackTrace();
			}
			catch(UnsupportedFlavorException io) {
				io.printStackTrace();
			}

			final UINode fuinode = uinode;
			final UIViewFrame foViewFrame = oViewFrame;
			final UIViewPane foViewPane = oViewPane;
			final String fsNodeID = sNodeID;
			final DropTargetDropEvent fe = e;

			Thread thread = new Thread() {
				public void run() {

			if (foViewPane != null && fuinode != null && getType() == ICoreConstants.TRASHBIN) {
				fe.acceptDrop(DnDConstants.ACTION_MOVE);

				DeleteEdit edit = new DeleteEdit(foViewFrame);
				NodeUI nodeui = fuinode.getUI();
				nodeui.deleteNodeAndLinks(fuinode, edit);
				//oViewPane.repaint();
				foViewFrame.getUndoListener().postEdit(edit);

				fe.getDropTargetContext().dropComplete(true);
			}

				}
			};
			thread.start();
	    }*/
	}

	/**
	 * Set the help context for this node depending on node type.
	 * @param type, the node type to set the help string for.
	 */
	private void setHelp(int type) {
		UINodeTypeManager.setHelp(this, type);
	}

	/**
	 * Return the standard size icon for the given node type.
	 * @param type, the node type to return the icon for.
	 * @return ImageIcon, the icon for the given node type.
	 */
	public static ImageIcon getNodeImage(int type, boolean isSmall) {
		return UINodeTypeManager.getNodeImage(type, isSmall);
	}

	/**
	 * Return the small size icon for the given node type.
	 * @param type, the node type to return the icon for.
	 * @return ImageIcon, the icon for the given node type.
	 */
	public static ImageIcon getNodeImageSmall(int type) {
		return UINodeTypeManager.getNodeImageSmall(type);
	}

	/**
	 * Return the small size icon for the given reference string for file types (not images).
	 * @param sRefString the reference string to get an icon for.
	 * @return ImageIcon the icon for the given node type.
	 */
	public static ImageIcon getReferenceImageSmall(String sRefString) {
		return UIReferenceNodeManager.getSmallReferenceIcon(sRefString);
	}
	
	/**
	 * Return the small size icon for the given reference string for file types (not images).
	 * @param sRefString the reference string to get an icon for.
	 * @return ImageIcon the icon for the given node type.
	 */
	public static ImageIcon getReferenceImage(String sRefString) {
		return UIReferenceNodeManager.getReferenceIcon(sRefString);		
	}
	
	/**
	* Returns the L&F object that renders this component.
	*
	* @return NodeUI object.
	*/
	public NodeUI getUI() {
	    return (NodeUI)ui;
	}

	/**
	* Sets the L&F object that renders this component.
	*
	* @param ui  the NodeUI L&F object
	* @see UIDefaults#getUI
	*/
	public void setUI(NodeUI ui) {
	    super.setUI(ui);
	}

	/**
	* Notification from the UIFactory that the L&F has changed.
	*
	* @see JComponent#updateUI
	*/
	public void updateUI() {
	  	NodeUI newNodeUI = (NodeUI)NodeUI.createUI(this);
	  	setUI(newNodeUI);
		invalidate();
	}

	/**
	* Returns a string that specifies the name of the l&f class
	* that renders this component.
	*
	* @return String "NodeUI"
	*
	* @see JComponent#getUIClassID
	* @see UIDefaults#getUI
	*/
	public String getUIClassID() {
	  	return "NodeUI"; //$NON-NLS-1$
	}


	/**
	 * Returns the type of this node.
	 *
	 * @return int, the type of this node.
	 * @see #setType
	 */
	public int getType() {
	    return oNode.getType();
	}

	/**
 	 * Change the type of this node to the given type.
	 * Return if type changed.
	 *
	 * @param nNewType, the new type for this node.
	 * @return boolean, true of the type was changed, else false.
	 * @see #getType
	 */
	public boolean setType( int nNewType ) {
		return setType(nNewType, false, -1);
	}

	/**
 	 * Change the type of this node to the given type.
	 * Return if type changed.
	 *
	 * @param newtype, the new type for this node.
	 * @param focus, indicates whether to focus the node after type change.
	 * @param position, indicaes to focus node label for editing after type change at the given point. -1 indicates not to focus label.
	 * @return boolean, true of the type was changed, else false.
	 * @see #getType
	 */
	public boolean setType( int nNewType, boolean focus, int position ) {

	    int nOldType = oNode.getType();
		if (nOldType == nNewType)
			return false;

	    boolean changeType = true;

	    // IF NODE WAS A VIEW AND IS CHANGING TO NOT BE A VIEW
	    // WARN USER CONTENTS WILL  BE LOST
	    if ( View.isViewType(nOldType) && !View.isViewType(nNewType) ) {

			int response = JOptionPane.showConfirmDialog(ProjectCompendium.APP, 					
					LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "UINode.warningMessage1a")+"\n"+ //$NON-NLS-1$ //$NON-NLS-2$
						LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "UINode.warningMessage1b")+"\n\n"+//$NON-NLS-1$ //$NON-NLS-2$
							LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "UINode.warningMessage1c")+"\n", //$NON-NLS-1$ //$NON-NLS-2$
						      LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "UINode.changeType")+oNode.getLabel(), JOptionPane.YES_NO_OPTION); //$NON-NLS-1$
			if (response == JOptionPane.NO_OPTION || response == JOptionPane.CLOSED_OPTION) {
		    	return false;
			}
			else {
		    	try {
					View view = (View)oNode;
					view.clearViewForTypeChange();
					ProjectCompendium.APP.setTrashBinIcon();
					ProjectCompendium.APP.removeView((View)view);
					oNode.setType(nNewType, sAuthor);
				}
		    	catch(Exception io) {
					io.printStackTrace();
					return false;
				}
			}
		}
	    // IF NODE IS CHANING FROM A VIEW TO ANOTHER VIEW, CHECK TYPES AND WARN AS APPROPRIATE
	    else if ( View.isViewType(nNewType) ) {
			if (View.isViewType(nOldType)) {
				try {
					boolean proceed = false;
					boolean purgeLinks = false;
					if (nOldType == ICoreConstants.MOVIEMAPVIEW && View.isListType(nNewType)) {
						int response = JOptionPane.showConfirmDialog(ProjectCompendium.APP, 
								LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "UINode.warningMessage4a") +"\n\n"+ //$NON-NLS-1$ //$NON-NLS-2$
								LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "UINode.warningMessage1c") +"\n", //$NON-NLS-1$ //$NON-NLS-2$
							      LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "UINode.changeType")+oNode.getLabel(), JOptionPane.YES_NO_OPTION); //$NON-NLS-1$
						if (response == JOptionPane.NO_OPTION || response == JOptionPane.CLOSED_OPTION) {
							return false;
						}
						else {
							proceed = true;
							purgeLinks = true;
						}					
					} else if (nOldType == ICoreConstants.MOVIEMAPVIEW && View.isMapType(nNewType)) {
						int response = JOptionPane.showConfirmDialog(ProjectCompendium.APP, 
								LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "UINode.warningMessage5a") +"\n\n"+ //$NON-NLS-1$ //$NON-NLS-2$
								LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "UINode.warningMessage1c") +"\n", //$NON-NLS-1$ //$NON-NLS-2$
							      LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "UINode.changeType")+oNode.getLabel(), JOptionPane.YES_NO_OPTION); //$NON-NLS-1$
						if (response == JOptionPane.NO_OPTION || response == JOptionPane.CLOSED_OPTION) {
							return false;
						}
						else {
							proceed  = true;
							purgeLinks = false;
						}					
					} else if (View.isMapType(nOldType) && View.isListType(nNewType)) {
						
						int response = JOptionPane.showConfirmDialog(ProjectCompendium.APP, 
								LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "UINode.warningMessage2a") +"\n\n"+ //$NON-NLS-1$ //$NON-NLS-2$
								LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "UINode.warningMessage1c") +"\n", //$NON-NLS-1$ //$NON-NLS-2$
							      LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "UINode.changeType")+oNode.getLabel(), JOptionPane.YES_NO_OPTION); //$NON-NLS-1$
						if (response == JOptionPane.NO_OPTION || response == JOptionPane.CLOSED_OPTION) {
							return false;
						}
						else {		
							proceed  = true;
							purgeLinks = true;
						}
					} else {
						proceed  = true;
						purgeLinks = false;
					}
					
					if (proceed) {
						if (purgeLinks) {
							View view = (View)oNode;
							view.purgeAllLinks();
						}
						if (oNode instanceof TimeMapView) {
							TimeMapView timeview = (TimeMapView)oNode;
							timeview.clearTimes();
						}
						if (oNode instanceof MovieMapView) {
							MovieMapView movieview = (MovieMapView)oNode;
							movieview.clearMovies();
						}
						
						ProjectCompendium.APP.removeView((View)oNode);
						oNode.setType(nNewType, sAuthor);							
					}
				}
				catch(Exception io){
					io.printStackTrace();
					return false;
				}
			}
			else {
				try {
	   				oNode.setType(nNewType, sAuthor);
				}
				catch(Exception io) {
					return false;
				}
			}
		}
	    // IF NODE IS CHANING FROM A REFERENCE NODE TO ANOTHER TYPE
	    // WARN USER REFERENCE WILL BE LOST.
	    else if (nOldType == ICoreConstants.REFERENCE && nNewType != ICoreConstants.REFERENCE) {

			String source = oNode.getSource();
			String image = oNode.getImage();

			if (!image.equals("") || !source.equals("")) { //$NON-NLS-1$ //$NON-NLS-2$
				int response = JOptionPane.showConfirmDialog(ProjectCompendium.APP, 
						LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "UINode.warningMessage3b")+"\n"+ //$NON-NLS-1$ //$NON-NLS-2$
						LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "UINode.warningMessage3b")+"\n\n"+ //$NON-NLS-1$ //$NON-NLS-2$
						LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "UINode.warningMessage1c")+"\n", //$NON-NLS-1$ //$NON-NLS-2$
							      LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "UINode.changeType")+" - "+oNode.getLabel(), JOptionPane.YES_NO_OPTION); //$NON-NLS-1$ //$NON-NLS-2$

				if (response == JOptionPane.NO_OPTION || response == JOptionPane.CLOSED_OPTION)
				    changeType = false;
				else
					changeType = true;
			}

			if (changeType) {
		    	try {
		    		// ONLY CLEAR THE REFERENCE AND IMAGE IF NOT CHANING TO A VIEW AS VIEW'S CAN HAVE IMAGES.
					if (!View.isViewType(nNewType))
						oNode.setSource("", "", sAuthor); //$NON-NLS-1$ //$NON-NLS-2$
					else {
						// if there is a reference to an image and no existing image, 
						// move to image field so node image draws when a view.
						if (UIImages.isImage(source) && image.equals("")) {
							image = source;
						}					
						oNode.setSource("", image, sAuthor);
					}
					oNode.setType(nNewType, sAuthor);
				}
		    	catch(Exception io) {
					return false;
				}
			}
	    }
	    else {
			try {
			    oNode.setType(nNewType, sAuthor);
			}
			catch(Exception ex) {
				return false;
			}
	    }

	    return changeType;
	}

	/**
	 * Update the link colours as appropriate after a node type change.
	 * @param type, the type of the node to update the link color for.
	 * @param oldType, the previous node type of this node.
	 */
	private void changeLinkColour(int type, int oldType) {

		UILinkGroup group = ProjectCompendium.APP.oLinkGroupManager.getLinkGroup(ProjectCompendium.APP.getActiveLinkGroup());

		if (group == null || (group.getID()).equals("1") ) { //$NON-NLS-1$
		    if ( type == ICoreConstants.PRO ) {

				for(Enumeration e = htLinks.elements();e.hasMoreElements();) {
				    UILink link = (UILink)e.nextElement();
				    if ( (link.getFromNode().getNode().getId()).equals(getNode().getId()) )
						link.setLinkType(new Integer(ICoreConstants.SUPPORTS_LINK).toString());
				}
			}
		    else if ( type == ICoreConstants.CON ) {

				for(Enumeration e = htLinks.elements();e.hasMoreElements();) {
				    UILink link = (UILink)e.nextElement();
				    if ( (link.getFromNode().getNode().getId()).equals(getNode().getId()) )
						link.setLinkType(new Integer(ICoreConstants.OBJECTS_TO_LINK).toString());
				}
		    }
		    else if (oldType == ICoreConstants.PRO || oldType == ICoreConstants.CON) {

				for(Enumeration e = htLinks.elements();e.hasMoreElements();) {
				    UILink link = (UILink)e.nextElement();
				    if ( (link.getFromNode().getNode().getId()).equals(getNode().getId()) ) {
						link.setLinkType(new Integer(ICoreConstants.DEFAULT_LINK).toString());
					}
				}
		    }
		}
	}

	/**
	 * Move first page of detail text into label.
	 */
	public void onMoveDetails() {

	    String details = oNode.getDetail();
	    if (details.equals(ICoreConstants.NODETAIL_STRING))
			details = ""; //$NON-NLS-1$

	    String label = getText();
	    if (label.equals(ICoreConstants.NOLABEL_STRING))
			label = ""; //$NON-NLS-1$

	    if (!details.equals("") && !label.equals("")) //$NON-NLS-1$ //$NON-NLS-2$
			label = label+" "+details; //$NON-NLS-1$
	    else if (label.equals("") && !details.equals("")) //$NON-NLS-1$ //$NON-NLS-2$
			label = details;

	    label = label.replace('\n',' ');
	    label = label.replace('\r',' ');
	    label = label.replace('\t',' ');

	    setText(label);
	    try {
			// shuffle detail pages.
			Vector pages = oNode.getDetailPages(sAuthor);
			if (pages.size() > 1) {
			    pages.removeElementAt(0);
			    NodeDetailPage page = null;
			    int count = pages.size();
			    for (int i=0; i<count; i++) {
					page = (NodeDetailPage)pages.elementAt(i);
					page.setPageNo(i+1);
			    }
			}
			else {
			    NodeDetailPage page = (NodeDetailPage)pages.elementAt(0);
			    page.setText(""); //$NON-NLS-1$
				pages.setElementAt(page, 0);
			}
			oNode.setDetailPages(pages, sAuthor, sAuthor);
		}
	    catch(Exception ex) {
			System.out.println("Error: (UINode.onMoveDetails) \n\n"+ex.getMessage()); //$NON-NLS-1$
	    }

	    ProjectCompendium.APP.refreshNodeIconIndicators(oNode.getId());
	}

	/**
	 * Move label text into the first page of detail.
	 */
	public void onMoveLabel() {

	    String label = getText();
	    if (label.equals(ICoreConstants.NOLABEL_STRING))
			label = ""; //$NON-NLS-1$

	    String details = oNode.getDetail();
	    if (details.equals(ICoreConstants.NODETAIL_STRING))
			details = ""; //$NON-NLS-1$

	    if (!label.equals("") && !details.equals("")) //$NON-NLS-1$ //$NON-NLS-2$
			details = label+" "+details; //$NON-NLS-1$
	    else if (details.equals("") && !label.equals("")) //$NON-NLS-1$ //$NON-NLS-2$
			details = label;

	    try {
			oNode.setDetail(details, sAuthor, sAuthor);
		    setText(""); //$NON-NLS-1$
	    }
	    catch(Exception ex) {
			ex.printStackTrace();
			System.out.println("Error: (UINode.onMoveLabel) \n\n"+ex.getMessage()); //$NON-NLS-1$
	    }

	    ProjectCompendium.APP.refreshNodeIconIndicators(oNode.getId());
	}

	/**
	 * Increase the font size displayed by one point.
	 * This does not change the setting in the database.
	 * @return the new size.
	 */
	public int increaseFontSize() {
		Font font = getFont();
		int newSize = font.getSize()+1;
		Font newFont = new Font(font.getName(), font.getStyle(), font.getSize()+1);	
		super.setFont(newFont);
		getUI().refreshBounds();		
		return newSize;
	}
	
	/**
	 * Decrease the font size displayed by one point.
	 * This does not change the setting in the database.
	 * @return the new size.
	 */
	public int decreaseFontSize() {
		Font font = getFont();
		int newSize = font.getSize()-1;
		Font newFont = new Font(font.getName(), font.getStyle(), font.getSize()-1);	
		super.setFont(newFont);
	   	getUI().refreshBounds();
	   	return newSize;
	}
	
	/**
	 * Sets the font used to display the node's text to the given font without scaling
 	 *
	 * @param size The size to set the font.
	 */
	public void setFontSize(int size) {		
		Font font = getFont();
		Font newFont = new Font(font.getName(), font.getStyle(), size);	
		super.setFont(newFont);
	   	getUI().refreshBounds();		
	}
		
	/**
	 * Restore the font to the default settings.
	 *
	 */
	public void setDefaultFont() {
		Font labelFont = new Font(oPos.getFontFace(), oPos.getFontStyle(), oPos.getFontSize());
		setFont(labelFont);
		
		if (getUI() != null) {					
			getUI().refreshBounds();
		}
	}
	
	/**
	 * Sets the font used to display the node's text.
	 * Scales if required.
 	 *
	 * @param font The font to use.
	 */
	public void setFont(Font font) {
		
		if (scale != 0.0 && scale != 1.0) {	
			if (oPos != null) {
				String sFontFace = oPos.getFontFace();
				int nFontSize = oPos.getFontSize();
				int nFontStyle = oPos.getFontStyle();
				Font font2 = new Font(sFontFace, nFontStyle, nFontSize);
				Point p1 = UIUtilities.transformPoint(font2.getSize(), font2.getSize(), scale);
				font = new Font(font2.getName() , font2.getStyle(), p1.x);
			}
		}
		
		super.setFont(font);				
	    repaint(10);
	}
		
	/**
	 * Returns the text string that the node displays.
	 *
	 * @return String, the text string that the node displays.
	 * @see #setText
	 */
	public String getText() {
	    return sText;
	}

	/**
	 * Defines the single line of text this component will display.
	 * <p>
	 * @param text, the new text to diaply as the node label.
	 * @see #setIcon
	 */
	public void setText(String text) {
	    String oldValue = sText;

	    //set thte label of the model nodesummary object
	    try {
			if (oNode != null) {
				oNode.setLabel(text, sAuthor);
				sText = text;
				firePropertyChange(TEXT_PROPERTY, oldValue, sText);
				repaint();
			}
	    }
	    catch(Exception io) {
			io.printStackTrace();
			ProjectCompendium.APP.displayError("Error: (UINode.setText) "+LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "UINode.errorUpdateLabel")+"\n\n"+io.getMessage()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	    }
	}

	/**
	 * Defines the single line of text this component will display.  This method (with the bDefer param)
	 * is used only by NodeUI.addChartoLabel() - this is a performance mod to speed up typing when 
	 * entering node labels (otherwise, each character typed generated 6 DB interactions). Going this
	 * route, the DB update for the label is deferred until the node loses focus.
	 * 
	 * @param text, the new text to display as the node label.
	 * @param bDefer, 
	 */
	public void setText(String text, Boolean bDefer) {
	    String oldValue = sText;
	    try {
			if (oNode != null) {
				oNode.setLabelLocal(text);
				sText = text;
				firePropertyChange(TEXT_PROPERTY, oldValue, sText);
				repaint();
			}
	    }
	    catch(Exception io) {
			io.printStackTrace();
			ProjectCompendium.APP.displayError(LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "UINode.errorUpdateLabel")+io.getMessage()); //$NON-NLS-1$
	    }
	}
	
	/**
	 * Sets the current scaling value for this node.
	 * @param scale, the scaling value to apply to this node.
	 */
	public void setScale(double scale) {
	    this.scale = scale;
	}

	/**
	 * Gets the current scaling value for this node.
	 * @return double, the current scaling value for this node.
	 */
	public double getScale() {
	    return scale;
	}

	/**
	 * Returns the graphic image (glyph, icon) that the node displays.
	 *
	 * @return ImageIocn, the icon displayed by this node.
	 * @see #setIcon
	 */
	public ImageIcon getIcon() {
		if (oDefaultIcon == null || oDefaultIcon.getImageLoadStatus() == MediaTracker.ERRORED) {
			return null;
		}
	    return oDefaultIcon;
	}

	/**
	 * Defines the icon this component will display. Fires a PropertyChangeEvent.
	 * <p>
	 * @param icon, the icon for this node to display.
	 * @see #getIcon
	 */
	public void setIcon(ImageIcon icon) {

	    if (scale != 1.0) {
			icon = scaleIcon(icon);
	    }

	    ImageIcon oldValue = oDefaultIcon;
	    oDefaultIcon = icon;

	    firePropertyChange(ICON_PROPERTY, oldValue, oDefaultIcon);
	    repaint();
	}

	/**
	 * Refreshes the icon this component will display. Forces the firing of a PropertyChangeEvent.
	 * <p>
	 * @param icon, the icon for this node to display.
	 * @see #getIcon
	 */
	public void refreshIcon(ImageIcon icon) {

	    if (scale != 1.0)
			icon = scaleIcon(icon);

	    ImageIcon oldValue = oDefaultIcon;
	    oDefaultIcon = icon;

	    // FORCE A PROPERTY CHANGE
	    oldValue = null;

	    firePropertyChange(ICON_PROPERTY, oldValue, oDefaultIcon);
	    repaint();
	}

	/**
	 * Scale the given icon to the current scaling factor and return.
	 * @param icon, the icon to scale.
	 * @return ImageIcon, the scaled version of the icon, or the original if something went wrong.
	 */
	public ImageIcon scaleIcon(ImageIcon icon) {

		if (icon == null)
			return icon;

	    int imgWidth = icon.getIconWidth();
	    int imgHeight = icon.getIconHeight();

		if (imgWidth == 0 || imgHeight == 0)
			return icon;

		//System.out.println("original height = "+imgHeight);
		//System.out.println("original Width = "+imgWidth);

		//System.out.println("scale = "+scale);

	    int scaledW = (int)(scale*imgWidth);
	    int scaledH = (int)(scale*imgHeight);

		//System.out.println("scaled height = "+scaledH);
		//System.out.println("scaled Width = "+scaledW);

		if (scaledW == 0 || scaledH == 0)
			return null;

	    ImageFilter filter = new AreaAveragingScaleFilter(scaledW, scaledH);
	    FilteredImageSource filteredSource = new FilteredImageSource((ImageProducer)icon.getImage().getSource(), filter);
	    JLabel comp = new JLabel();
	    Image img = comp.createImage(filteredSource);
	    icon = new ImageIcon(img);

	    return icon;
	}

	/**
	 * Restore the icon on this node to its original type specific icon.
	 */
	public ImageIcon restoreIcon() {

	    int type = oNode.getType();

	    if (type == ICoreConstants.TRASHBIN) {
			ImageIcon icon = ProjectCompendium.APP.setTrashBinIcon();
			if (icon != null) {
				setIcon(icon);
			}
	    }
	    else if (type == ICoreConstants.REFERENCE || type == ICoreConstants.REFERENCE_SHORTCUT) {
			String refString = oNode.getImage();
			if (refString == null || refString.equals("")) //$NON-NLS-1$
		    	refString = oNode.getSource();
			setReferenceIcon(refString);
	    }
		else if(View.isViewType(type) || View.isShortcutViewType(type)) {

			String refString = oNode.getImage();
			if (refString != null && !refString.equals("")) //$NON-NLS-1$
				setReferenceIcon(refString);
			else
				setIcon(getNodeImage(type, oPos.getShowSmallIcon()));
		}
	    else {
			setIcon(getNodeImage(type, oPos.getShowSmallIcon()));
	    }

	    return oDefaultIcon;
	}

	/**
	 * return if the image on this node has been scaled,
	 * and therefore can be enlarge on rollover.
	 */
	public boolean hasImageBeenScaled() {
		return bIsImageScaled;
	}

	/**
	 * Set the correct reference icon for the given file path or url.
	 *
	 * @param refString, the string for the file path or url for this reference node.
	 */
	public void setReferenceIcon(String refString) {
	    final String imageRef = refString;

		//Thread thread = new Thread("UINode.setReferenceIcon") {
		//	public void run() {
			    ImageIcon icon = null;
		
			    if (imageRef != null) {
					if ( UIImages.isImage(imageRef) ) {
						ImageIcon originalSizeImage = UIImages.createImageIcon(imageRef); 		
						if (originalSizeImage == null) {
							setIcon(UIImages.get(IUIConstants.BROKEN_IMAGE_ICON));
							return;
						}
						
						Image originalIcon = originalSizeImage.getImage();
						int originalWidth = originalIcon.getWidth(null);
						int originalHeight = originalIcon.getHeight(null);
		
						Dimension specifiedSize = getNode().getImageSize();
						if (specifiedSize.width == 0 && specifiedSize.height == 0) {
				    		icon = UIImages.thumbnailIcon(originalSizeImage);
							Image newIcon = icon.getImage();
							int newWidth = newIcon.getWidth(null);
							int newHeight = newIcon.getHeight(null);
				    		if (newWidth < originalWidth || newHeight < originalHeight) {
								bIsImageScaled = true;
							}
						} else if (specifiedSize.width == originalWidth && specifiedSize.height == originalHeight) {
							icon = originalSizeImage;
							bIsImageScaled = false;
						} else {
				    		icon = UIImages.scaleIcon(originalSizeImage, specifiedSize);
							Image newIcon = icon.getImage();
							int newWidth = newIcon.getWidth(null);
							int newHeight = newIcon.getHeight(null);
				    		if (newWidth < originalWidth || newHeight < originalHeight) {
								bIsImageScaled = true;
							}					
						}
					}
					else {
					    //FileSystemView fsv = FileSystemView.getFileSystemView();
				     	//File file = new File(refString);
				     	//icon = (ImageIcon)fsv.getSystemIcon(file);
		
					    // IF USING SMALL ICON MODE, LOAD SMALL VERSION
					    if (oPos.getShowSmallIcon()) {
					    	icon = getReferenceImageSmall(imageRef);
					    }
					    else {
					    	icon = getReferenceImage(imageRef);			    	
					    }
					}
			    }
			    setIcon(icon);
			//}
		//};
		//thread.start();
	}

	/**
	 * Check if the file path or url given is a recognised type for reference nodes.
	 * @param sRefString the string to check.
	 * @return boolean true if the file path or url given is a recognised type, else false.
	 */
	public static boolean isReferenceNode(String sRefString) {
		return UIReferenceNodeManager.isReferenceNode(sRefString);
	}

	/**
	 * Returns the nodedata object that this UINode represents.
	 *
	 * @return com.compendium.core.datamodel.NodeSummary, the associated node.
	 * @see #setNode
	 */
	public NodeSummary getNode() {
	    return oNode;
	}

	/**
	 * Returns the node pos that the node represents.
	 *
	 * @return com.compendium.core.datamodel.NodePosition, the associated node position object.
	 */
	public NodePosition getNodePosition() {
	    return oPos;
	}

	/**
 	 * Set the NodePosition of this node
	 * @param x, the x position of this node in the map.
	 * @param y, the y position of this node in the map.
	 */
	public void setNodePosition(int x, int y) {
	    oPos.setPos(x, y);
	}

	/**
 	 * Set the NodeSummary data object for this node.
	 * <p>
	 * @param node com.compendium.core.datamodel.NodeSummary, the node data object for this UINode.
	 */
	public void setNode(NodeSummary node) {
	    oNode = node;
		oNodeType = node.getType();

	    oNode.addPropertyChangeListener(this);
	    oPos.addPropertyChangeListener(this);

	    setHelp(oNode.getType());

	    //remove all returns and tabs which show up in the GUI as evil black char
	    String label = ""; //$NON-NLS-1$
	    label = oNode.getLabel();
	    if (label.equals(ICoreConstants.NOLABEL_STRING))
			label = ""; //$NON-NLS-1$

	    label = label.replace('\n',' ');
	    label = label.replace('\r',' ');
	    label = label.replace('\t',' ');
	    setText(label);

	    int type = oNode.getType();

	    //if the default icon has not already been set, then set it here.
	    //otherwise leave the icon image alone since it may have been changed.
	    ImageIcon icon = null;

	    if (oDefaultIcon == null) {
			if (type == ICoreConstants.REFERENCE || type == ICoreConstants.REFERENCE_SHORTCUT) {
			    String refString = oNode.getImage();
			    if (refString == null || refString.equals("")) //$NON-NLS-1$
					refString = oNode.getSource();
			    
			    if (refString == null || refString.equals("")) { //$NON-NLS-1$
			    	setIcon(getNodeImage(type, oPos.getShowSmallIcon()));
			    } else {
			    	setReferenceIcon(refString);
			    }
			}
			else if(View.isViewType(type) || View.isShortcutViewType(type)) {
			    String refString = oNode.getImage();
			    if (refString == null || refString.equals("") || refString.endsWith("meeting_big.gif")) //$NON-NLS-1$ //$NON-NLS-2$
				    setIcon(getNodeImage(type, oPos.getShowSmallIcon()));
				else
				    setReferenceIcon(refString);
			}
			else {
			    setIcon(getNodeImage(type, oPos.getShowSmallIcon()));
			}
	    }

	    oNode.updateMultipleViews();
	}

	/**
	 * Returns the selected state of the node.
	 *
	 * @return boolean, the selected state of the node
	 */
	public boolean isSelected() {
	    return bSelected;
	}

	/**
	 * Sets the selected state of the node.
	 * <p>
	 * @param selected, the selected state
	 */
	public void setSelected(boolean selected) {
	    boolean oldValue = bSelected;
	    bSelected = selected;
	    firePropertyChange(SELECTED_PROPERTY, oldValue, bSelected);

	    repaint();
	}

	/**
	 * Returns the state of the cut node.
	 *
	 * @return boolean, true if the node has been cut, else false.
	 */
	public boolean isCut() {
	    return bCut;
	}

	/**
	 * Sets the state of the node to cut.
	 *
	 * @param cut, has this node been cut.
	 */
	public void setCut(boolean cut) {
	    boolean oldValue = bCut;
	    bCut = cut;
	    firePropertyChange("selected", oldValue, bSelected); //$NON-NLS-1$

	    repaint();
	}

	/**
	 * Returns the roll-over state of the node.
	 *
	 * @return boolean, true if the node is currently rolled over, else false.
	 */
	public boolean isRollover() {
	    return bRollover;
	}

	/**
	* Sets the roll over state of the node.
	* <p>
	* @param rollover, the roll over state of the node.
	*/
	public void setRollover(boolean rollover) {

	    boolean oldValue = bRollover;
	    bRollover = rollover;

	    firePropertyChange(ROLLOVER_PROPERTY, new Boolean(oldValue), new Boolean(bRollover));

	    repaint();
	}

	/**
	 * Adds a reference to a link this node is linked to.
	 *
	 * @param link com.compendium.ui.UILink, the link this node is linked to.
	 */
	public void addLink(UILink link) {
	    if(!htLinks.containsKey((link.getLink()).getId()))
			htLinks.put((link.getLink()).getId(),link);
	}

	/**
	 * Removes a reference to a link this node is linked to.
	 *
	 * @param link com.compenduim.ui.UILink, the link to be removed.
	 */
	public void removeLink(UILink link) {
	    if(htLinks.containsKey((link.getLink()).getId()))
			htLinks.remove((link.getLink()).getId());
	}

	/**
	 * Removes references to all links this node is linked to.
	 */
	public void removeAllLinks() {

	    for(Enumeration keys = htLinks.keys();keys.hasMoreElements();) {
			String key = (String)keys.nextElement();
			htLinks.remove(key);;
	    }
	}

	/**
	 * Updates the connection points of the links.
	 */
	public void updateLinks() {
		if (htLinks != null && htLinks.size() > 0) {
		    for(Enumeration e = htLinks.elements();e.hasMoreElements();) {
				UILink link = (UILink)e.nextElement();
				if (link != null)
					link.updateConnectionPoints();
	   	 	}
		}
	}

	/**
	 * Scale links and update the connection points of the links.
	 */
	public void scaleLinks(AffineTransform trans) {

	    for(Enumeration e = htLinks.elements();e.hasMoreElements();) {
			UILink link = (UILink)e.nextElement();
			link.scaleLink(trans);
			link.updateConnectionPoints();
	    }
	}

	/**
	 * Returns the enumeration of UILinks to this node.
	 * @return Enumeration, the enumeration of UILinks to this node.
	 */
	public Enumeration getLinks() {
	    return htLinks.elements();
	}

	/**
	 * Checks whether this node is linked to the given node.
	 *
	 * @param to com.compendium.ui.UINode, the node to test for.
	 * @return boolean, true if this node is linked to the given node, false otherwise.
	 */
	public boolean containsLink(UINode to) {
	    for(Enumeration e = htLinks.elements();e.hasMoreElements();) {
			UILink link = (UILink)e.nextElement();
			if (link.getFromNode() == to || link.getToNode() == to)
			    return true;
	    }
	    return false;
	}

	/**
	 * Return the link associated with the given node or null.
	 *
	 * @param to com.compendium.ui.UINode, the node to return the link for.
	 * @return com.compendium.ui.UILink, the link of found else null.
	 */
	public UILink getLink(UINode to) {

	    for(Enumeration e = htLinks.elements();e.hasMoreElements();) {
			UILink link = (UILink)e.nextElement();
			if (link.getFromNode() == to || link.getToNode() == to)
			    return link;
	    }
	    return null;
	}

	/**
	 * Convenience method that moves this component to position 0 if it's
	 * parent is a JLayeredPane.
	 */
	public void moveToFront() {
	    if (getParent() != null && getParent() instanceof JLayeredPane) {
			JLayeredPane l =  (JLayeredPane)getParent();
			l.moveToFront(this);
	    }
	}

	/**
	 * Convenience method that moves this component to position -1 if it's
	 * parent is a JLayeredPane.
	 */
	public void moveToBack() {
	    if (getParent() != null && getParent() instanceof JLayeredPane) {
			JLayeredPane l =  (JLayeredPane)getParent();
			l.moveToBack(this);
	    }
	}

	/**
	 * Returns the amount of space between the text and the icon
	 * displayed in this node.
	 *
	 * @return int, an int equal to the number of pixels between the text and the icon.
	 * @see #setIconTextGap
	 */
	public int getIconTextGap() {
	    return nIconTextGap;
	}

	/**
	 * If both the icon and text properties are set, this property
	 * defines the space between them.
	 * <p>
	 * The default value of this property is 4 pixels.
	 * <p>
	 *
	 * @see #getIconTextGap
	 */
	public void setIconTextGap(int iconTextGap) {
	    int oldValue = nIconTextGap;
	    nIconTextGap = iconTextGap;
	    firePropertyChange("iconTextGap", oldValue, nIconTextGap); //$NON-NLS-1$
	    invalidate();
	    repaint(10);
	}

	/**
	 * Set the location of this node, scaling if required.
	 * Location must be passed in at the original 100% scale value.
	 */
	public void setLocation(Point loc) {
		
		if (scale != 0.0 && scale != 1.0) {		
			loc = UIUtilities.transformPoint(loc.x, loc.y, scale);
		}
		
		super.setLocation(loc);		
	}
	
	/**
	 * Sets the font used to display the node's text. Fires a PropertyChangeEvent.
 	 *
	 * @param font,  The font to use.
	 */
	public void setLabelFont(Font font) {

	    super.setFont(font);
	    firePropertyChange(TEXT_PROPERTY, "", sText); //$NON-NLS-1$
	    repaint();
	}

	/**
	 * Return the current reference to the content dialog for this node or null.
	 * @return com.compendium.ui.dialogs.UINodeContentDialog, the content dialog for this node.
	 */
	public UINodeContentDialog getCurrentContentDialog() {
		return contentDialog;
	}

	/**
	 * Return the current reference to the content dialog for this node.
	 * If the content dialog is null, create a new one, but don't show it.
	 * @return com.compendium.ui.dialogs.UINodeContentDialog, the content dialog for this node.
	 */
	public UINodeContentDialog getContentDialog() {
	    if(getNode().getType() == ICoreConstants.TRASHBIN || 
	    		getNode().getId().equals(ProjectCompendium.APP.getInBoxID())) {
	    	return null;
	    }
		
	    if (contentDialog == null)
			contentDialog = new UINodeContentDialog(ProjectCompendium.APP, oPos.getView(), this, UINodeContentDialog.CONTENTS_TAB);

	    return contentDialog;
	}

	/**
	 * Open and return the content dialog and select the Edit/Contents tab.
	 * @return com.compendium.ui.dialogs.UINodeContentDialog, the content dialog for this node.
	 */
	public UINodeContentDialog showEditDialog() {
		return showContentDialog(UINodeContentDialog.CONTENTS_TAB);
	}

	/**
	 * Open and return the content dialog and select the Properties tab.
	 * @return com.compendium.ui.dialogs.UINodeContentDialog, the content dialog for this node.
	 */
	public UINodeContentDialog showPropertiesDialog() {
		return showContentDialog(UINodeContentDialog.PROPERTIES_TAB);
	}

	/**
	 * Open and return the content dialog and select the View tab.
	 * @return com.compendium.ui.dialogs.UINodeContentDialog, the content dialog for this node.
	 */
	public UINodeContentDialog showViewsDialog() {
		return showContentDialog(UINodeContentDialog.VIEW_TAB);
	}

	/**
	 * Open and return the content dialog and select the Time tab.
	 * @return com.compendium.ui.dialogs.UINodeContentDialog, the content dialog for this node.
	 */
	public UINodeContentDialog showTimeDialog() {
		return showContentDialog(UINodeContentDialog.TIME_TAB);
	}

	/**
	 * If the dialog is open, refresh the time tab.
	 */
	public void refreshTimeDialog() {
		if (contentDialog != null) {
			contentDialog.refreshTimes();
		}
	}
	
	/**
	 * Open and return the content dialog and select the Time tab.
	 * @param span the timespan the user was on when calling this dialog.
	 * @return com.compendium.ui.dialogs.UINodeContentDialog, the content dialog for this node.
	 */
	public UINodeContentDialog showTimeDialog(NodePositionTime span) {
	    if(getNode().getType() == ICoreConstants.TRASHBIN || 
	    		getNode().getId().equals(ProjectCompendium.APP.getInBoxID())) {
	    	return null;
	    }
	    	
		if (contentDialog != null && contentDialog.isVisible())
			return contentDialog;

		contentDialog = new UINodeContentDialog(ProjectCompendium.APP, oPos.getView(), this, UINodeContentDialog.TIME_TAB, span);
   		contentDialog.setVisible(true);
   		int state = this.getNode().getState();
   		if(state != ICoreConstants.READSTATE){
   			try {
				this.getNode().setState(ICoreConstants.READSTATE);
			} catch (SQLException e) {
				e.printStackTrace();
				System.out.println("Error: (UINode.showContentDialog) \n\n"+e.getMessage());//$NON-NLS-1$
			} catch (ModelSessionException e) {
				e.printStackTrace();
				System.out.println("Error: (UINode.showContentDialog) \n\n"+e.getMessage());//$NON-NLS-1$
			}
   		}
	    return contentDialog;
	}

	/**
	 * Open and return the content dialog and select the given tab.
	 *
	 * @param int tab, the tab on the dialog to select.
	 * @return com.compendium.ui.dialogs.UINodeContentDialog, the content dialog for this node.
	 */
	private UINodeContentDialog showContentDialog(int tab) {
	    if(getNode().getType() == ICoreConstants.TRASHBIN || 
	    		getNode().getId().equals(ProjectCompendium.APP.getInBoxID())) {
	    	return null;
	    }
	    	
		if (contentDialog != null && contentDialog.isVisible())
			return contentDialog;

		contentDialog = new UINodeContentDialog(ProjectCompendium.APP, oPos.getView(), this, tab);
   		contentDialog.setVisible(true);
   		//Lakshmi (4/19/06) - if the contents dialog is opened set state as read in NodeUserState DB
   		int state = this.getNode().getState();
   		if(state != ICoreConstants.READSTATE){
   			try {
				this.getNode().setState(ICoreConstants.READSTATE);
			} catch (SQLException e) {
				e.printStackTrace();
				System.out.println("Error: (UINode.showContentDialog) \n\n"+e.getMessage()); //$NON-NLS-1$
			} catch (ModelSessionException e) {
				e.printStackTrace();
				System.out.println("Error: (UINode.showContentDialog) \n\n"+e.getMessage()); //$NON-NLS-1$
			}
   		}
	    return contentDialog;
	}

	/**
	 * Return the right-click node menu for this node.
	 * @return com.compendium.ui.popups.UINodePopupMenu, the right-click node menu for this node.
	 */
	public UINodePopupMenu getPopupMenu() {
	    if(getNode().getType() == ICoreConstants.TRASHBIN || 
	    		getNode().getId().equals(ProjectCompendium.APP.getInBoxID())) {
			return null;
	    }

	    if (nodePopup == null)
			nodePopup = new UINodePopupMenu("Popup menu", getUI()); //$NON-NLS-1$

	    return nodePopup;
	}

	/**
	 * Create and hosw the right-click node popup menu for the given nodeui.
	 * @param nodeui com.compendium.ui.plad.NodeUI, the node to create the popup for.
	 * @param x, the x position of the mouse event that triggered this request.
	 * @param y, the y position of the mouse event that triggered this request.
	 */
	public UINodePopupMenu showPopupMenu(NodeUI nodeui,  int x, int y) {

	    if(getNode().getType() == ICoreConstants.TRASHBIN || 
	    		getNode().getId().equals(ProjectCompendium.APP.getInBoxID())) {
			return null;
	    }

	    nodePopup = new UINodePopupMenu("Popup menu", nodeui); //$NON-NLS-1$
	    UIViewFrame viewFrame = getViewPane().getViewFrame();

	    Dimension dim = ProjectCompendium.APP.getScreenSize();
	    int screenWidth = dim.width - 50; //to accomodate for the scrollbar
	    int screenHeight = dim.height -200; //to accomodate for the menubar...

	    Point point = viewFrame.getViewPosition();
	    int realX = Math.abs(point.x - getX())+50;
	    int realY = Math.abs(point.y - getY())+50;

	    int endXCoordForPopUpMenu = realX + nodePopup.getWidth();
	    int endYCoordForPopUpMenu = realY + nodePopup.getHeight();

	    int offsetX = (screenWidth) - endXCoordForPopUpMenu;
	    int offsetY = (screenHeight) - endYCoordForPopUpMenu;

	    if(offsetX > 0)
		offsetX = 0;
	    if(offsetY > 0)
		offsetY = 0;

	    nodePopup.setCoordinates(realX+offsetX, realY+offsetY);
	    nodePopup.setViewPane(getViewPane());
	    nodePopup.show(viewFrame, realX+offsetX, realY+offsetY);

	    return nodePopup;
	}

	/**
	 * Convenience method that searchs the anscestor heirarchy for a UIViewPane instance.
	 * @return com.compendium.ui.UIViewPane, the parent pane for this node.
	 */
	public UIViewPane getViewPane() {
	    Container p;

	    // Search upward for viewpane
	    p = getParent();
	    while (p != null && !(p instanceof UIViewPane)) {
	    	p = p.getParent();
	    }

	    return (UIViewPane)p;
	}

	/**
	 * Handle a PropertyChangeEvent.
	 * @param evt, the associated PropertyChangeEvent to handle.
	 */
	public void propertyChange(PropertyChangeEvent evt) {

	    String prop = evt.getPropertyName();
   		Object source = evt.getSource();
	    Object oldvalue = evt.getOldValue();
	    Object newvalue = evt.getNewValue();
	    
		if (source instanceof NodePosition) {
		    if (prop.equals(NodePosition.POSITION_PROPERTY)) {
				firePropertyChange(NodePosition.POSITION_PROPERTY, oldvalue, newvalue);
			    repaint();
			}
		    else if (prop.equals(NodePosition.FONTFACE_PROPERTY)) {
				Font font = getFont();
				Font newFont = new Font((String)newvalue, font.getStyle(), font.getSize());
				setFont(newFont);		
		    	getUI().refreshBounds();
			    repaint();
			}	
		    else if (prop.equals(NodePosition.FONTSTYLE_PROPERTY)) {
				Font font = getFont();
				Font newFont = new Font(font.getName(), ((Integer)newvalue).intValue(), font.getSize());	
				setFont(newFont);	
		    	getUI().refreshBounds();
			    repaint();
			}		    
		    else if (prop.equals(NodePosition.FONTSIZE_PROPERTY)) {
				Font font = getFont();
				int newsize = ((Integer)newvalue).intValue();
				Font newFont = new Font(font.getName(), font.getStyle(), newsize);				
				setFont(newFont);	//scales	
				
				int adjustment = ProjectCompendium.APP.getToolBarManager().getTextZoom();
				font = getFont();
				Font adjustedFont = new Font(font.getName(), font.getStyle(), font.getSize()+adjustment);	
				super.setFont(adjustedFont);
				
				getUI().refreshBounds();		
			    repaint();
			}	
		    else if (prop.equals(NodePosition.TEXT_FOREGROUND_PROPERTY)) {
		    	//setForeground(new Color( ((Integer)newvalue).intValue() ));
		    	getUI().refreshBounds();
			    repaint();
			}		    
		    else if (prop.equals(NodePosition.TEXT_BACKGROUND_PROPERTY)) {
		    	//setBackground(new Color( ((Integer)newvalue).intValue() ));
		    	getUI().refreshBounds();
			    repaint();
			}		    		    
		    else if (prop.equals(NodePosition.TAGS_INDICATOR_PROPERTY)) {
		    	getUI().refreshBounds();
			    repaint();
			}		    
		    else if (prop.equals(NodePosition.TEXT_INDICATOR_PROPERTY)) {
		    	getUI().refreshBounds();
			    repaint();
			}		    
		    else if (prop.equals(NodePosition.TRANS_INDICATOR_PROPERTY)) {
		    	getUI().refreshBounds();
			    repaint();
			}		    
		    else if (prop.equals(NodePosition.WEIGHT_INDICATOR_PROPERTY)) {
		    	getUI().refreshBounds();
			    repaint();
			}		    
		    else if (prop.equals(NodePosition.HIDE_ICON_PROPERTY)) {
		    	getUI().refreshBounds();
			    repaint();
			}		    
		    else if (prop.equals(NodePosition.SMALL_ICON_PROPERTY)) {
		    	int nType = oNode.getType();
		    	ImageIcon icon = null;
				if (nType == ICoreConstants.REFERENCE || nType == ICoreConstants.REFERENCE_SHORTCUT) {
					String image  = oNode.getImage();
					if ( image != null && !image.equals("")) //$NON-NLS-1$
						setReferenceIcon( image );
					else {
						setReferenceIcon( oNode.getSource() );
					}
				}
				else if(View.isViewType(nType) || View.isShortcutViewType(nType)) {
					String image  = oNode.getImage();
					if ( image != null && !image.equals("")) //$NON-NLS-1$
						setReferenceIcon( image );
					else {
						icon = getNodeImage(oNode.getType(), oPos.getShowSmallIcon());
						refreshIcon( icon );
					}
				}
				else {
					icon = getNodeImage(oNode.getType(), oPos.getShowSmallIcon());
					refreshIcon( icon );
				}
				updateLinks();
			    repaint();
			}	
		    else if (prop.equals(NodePosition.WRAP_WIDTH_PROPERTY)) {
		    	getUI().refreshBounds();
			    repaint();
			}		    		    
		}
		else if (source instanceof NodeSummary) {
		    if (prop.equals(NodeSummary.LABEL_PROPERTY)) {
		    	//Update as could have come from another uinode instance, 
		    	//like transclusion in another open map or outline view or aerial view 
				setText((String)newvalue); 
				updateLinks();
			    repaint();
		    }
		    else if (prop.equals(NodeSummary.TAG_PROPERTY)) {
		    	firePropertyChange(NodeSummary.TAG_PROPERTY, oldvalue, newvalue);
			    repaint();
		    }
		    else if (prop.equals(NodeSummary.DETAIL_PROPERTY)) {
		    	firePropertyChange(NodeSummary.DETAIL_PROPERTY, oldvalue, newvalue);
			    repaint();
		    }
		    else if (prop.equals(NodeSummary.NODE_TYPE_PROPERTY)) {

				NodeSummary oldnode = oNode;
				NodeSummary newnode = NodeSummary.getNodeSummary(oldnode.getId());

				int nNewType = ((Integer)newvalue).intValue();
				int nOldType = ((Integer)oldvalue).intValue();

				// IF THE NODE SHOULD CHANGE CLASS AND HAS NOT YET, CHANGE IT.
				// ONLY WANT THE DATABASE READ TO HAPPEN ONCE.
				// AFTER THAT, THE NEW OBJECT CAN BE RETRIEVED FROM CACHE
				String oldClassName = oldnode.getClass().getName();
				String newClassName = newnode.getClass().getName();
				IModel model = oNode.getModel();

			   	if ( (nOldType > ICoreConstants.PARENT_SHORTCUT_DISPLACEMENT && nNewType <=
	    					ICoreConstants.PARENT_SHORTCUT_DISPLACEMENT)
	    			|| ( View.isViewType(nOldType) && !View.isViewType(nNewType))
		 			|| ( !View.isViewType(nOldType) && View.isViewType(nNewType)) ) {

					// IF NOT BEEN RECREATED YET, DO IT.
					if (oldClassName.equals(newClassName)) {
						try {
							newnode = model.getNodeService().getNodeSummary(model.getSession(), oNode.getId());
						}
						catch(Exception ex) {
							ex.printStackTrace();
							System.out.println("Exception (UINode.propertyChange)\n\n"+ex.getMessage()); //$NON-NLS-1$
						}
					}
				}

	    		if (View.isViewType(nOldType)) {
	    			if (oldnode instanceof View) {
	    				ProjectCompendium.APP.removeViewFromHistory((View) oldnode);
	    			}
				}

				// IF THE NODE OBJECT HAS BEEN CHANGED e.g to/from View, ShortcutNodeSummary, ReferenceNode
				if (!oNode.equals(newnode)) {
					//oNode.removePropertyChangeListener(this); // BREAKS LOOP SENDING CHANGE EVENTS
					newnode.addPropertyChangeListener(this);
				}

				newnode.initialize(model.getSession(), model);
	
				oPos.setNode(newnode);
    			oNode = newnode;
				oNodeType = newnode.getType();

				setHelp(newnode.getType());

				restoreIcon();

				changeLinkColour(nNewType, nOldType);
				UIViewPane pane = getViewPane();

				if (pane != null) {
					pane.validateComponents();
					pane.repaint();
				}
			    repaint();
			}

		    else if (prop.equals(NodeSummary.VIEW_NUM_PROPERTY)) {
				firePropertyChange(NodeSummary.VIEW_NUM_PROPERTY, oldvalue, newvalue);
			    repaint();
		    }
		    else if (prop.equals(NodeSummary.STATE_PROPERTY)) {
		    	firePropertyChange(NodeSummary.STATE_PROPERTY, oldvalue, newvalue);
			    repaint();
		    }

		    else if (prop.equals(NodeSummary.IMAGE_PROPERTY)) {
		    	// THIS DOES NOT WORK - THE EVENT DOES NOT GET CALLED AS EXPECTED
		    	// USE ProjectCOmpendiumFrame.refreshIcons(String sNodeID)
				// String image = (String)newvalue;
				// if (image != null && !image.equals("")) {
				//	setReferenceIcon(image);
				//}
		    }
		    else if (prop.equals(NodeSummary.SOURCE_PROPERTY)) {
		    	// THIS DOES NOT WORK - THE EVENT DOES NOT GET CALLED AS EXPECTED
		    	// USE ProjectCOmpendiumFrame.refreshIcons(String sNodeID)
				//String sReference = (String)newvalue;
				//if (oNode.getImage().equals("") ) {
				//	setReferenceIcon( sReference );
		    	//}
		    }
		    else if (prop.equals(View.CHILDREN_PROPERTY)) {
				firePropertyChange(CHILDREN_PROPERTY, oldvalue, newvalue);
			    //this.paintImmediately(this.getBounds());				
		    }
		}
	}

	/**
	 * Clean up class variables to help with garbage collection.
	 */
	public void cleanUp() {

	    NodeUI nodeui = getUI();
	    nodeui.uninstallUI(this);

	    sText						= null;
	    oDefaultIcon				= null;
	    oNode						= null;
	    oPos						= null;

	    dragSource = null;
	    dropTarget = null;

	    if (htLinks != null)
		htLinks.clear();
	    htLinks = null;
	}
}
