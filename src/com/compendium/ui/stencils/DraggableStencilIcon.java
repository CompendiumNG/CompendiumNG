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
import java.awt.image.*;
import java.awt.dnd.*;
import java.awt.datatransfer.*;
import java.util.*;
import java.io.*;

import javax.swing.*;

import com.compendium.ui.UIImages;

/**
 * This class create a stencil icon which can be dragged and creat a node.
 *
 * @author	Michelle Bachler
 */
public class DraggableStencilIcon extends JLabel implements DragSourceListener, DragGestureListener, Transferable {

	/** The DragSource object associated with this draggable toolbar icon.*/
	private DragSource 			dragSource;

	/** The path of the node image file for this stencil node.*/
	private String 				sImage 				= ""; //$NON-NLS-1$

	/** The path of the palette image file for this stencil node.*/
	private String 				sPaletteImage 		= ""; //$NON-NLS-1$

	/** The path of the backgrounds image file for this stencil node (only if map).*/
	private String 				sBackgroundImage 	= ""; //$NON-NLS-1$

	/** The path of the xml file tha will act as the template for the contents of the node (only if map).*/
	private String 				sTemplate		 	= ""; //$NON-NLS-1$
	
	/** The node type to create (map or reference only).*/
	private int 				nNodeType 		= 0;

	/** The shortcut number that, when combined with the ALT key, will create a new node.*/
	private int 				nShortcut 		= -1;

	/** The node label of the node to create.*/
	private String 				sLabel 			= ""; //$NON-NLS-1$

	/** The tooltip text.*/
	private String 				sTip 			= ""; //$NON-NLS-1$

	/** The list of associated tag for node created.*/
	private Vector 				vtTags 			= new Vector(10);

	/** The data flavors supported by this class.*/
    public static final 		DataFlavor[] supportedFlavors = { null };
	static    {
		try { supportedFlavors[0] = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType); }
		catch (Exception ex) { ex.printStackTrace(); }
	}

	/**
	 * The Constructor.
	 */
  	public DraggableStencilIcon() {
		dragSource = new DragSource();
		dragSource.createDefaultDragGestureRecognizer((Component)this, DnDConstants.ACTION_COPY, this);
	}

	/**
	 * The Constructor.
	 *
	 * @param sImage the node image for the created node.
	 * @param sPaletteImage the palette image for the created node.
	 * @param sLabel the label of this stencil item.
	 * @param sTip the tool tip text of this stencil item.
	 * @param nNodeType map or reference node type.
	 * @param vtTags any tags to be assigned to this node type.
	 * @param oIcon the image to draw for this toolbar icon button.
	 */
  	public DraggableStencilIcon(String sImage, String sPaletteImage, String sBackgroundImage, String sLabel, String sTip, int nNodeType, Vector vtTags, ImageIcon oIcon) {

		super(oIcon);

		this.sImage = sImage;
		this.sPaletteImage = sPaletteImage;
		this.sBackgroundImage = sBackgroundImage;
		this.sLabel = sLabel;
		this.nNodeType = nNodeType;
		this.vtTags = vtTags;
		this.sTip = sTip;
		setToolTipText(sTip);

		dragSource = new DragSource();
		dragSource.createDefaultDragGestureRecognizer((Component)this, DnDConstants.ACTION_COPY, this);
  	}


	/**
	 * The Constructor.
	 *
	 * @param sImage the node image for the created node.
	 * @param sPaletteImage the palette image for the created node.
	 * @param sBackgroundImage the background image if this is a map node.
	 * @param sTemplate the template file if this is a map node.
	 * @param sLabel the label of this stencil item.
	 * @param sTip the tool tip text of this stencil item.
	 * @param nNodeType map or reference node type.
	 * @param nShortcut the shortcut number associated with this item.
	 * @param vtTags any tags to be assigned to this node type.
	 * @param oIcon the image to draw for this toolbar icon button.
	 */
  	public DraggableStencilIcon(String sImage, String sPaletteImage, String sBackgroundImage, 
  					String sTemplate, String sLabel, String sTip, int nNodeType, 
  					int nShortcut, Vector vtTags, ImageIcon oIcon) {

		super(oIcon);

		this.sImage = sImage;
		this.sPaletteImage = sPaletteImage;
		this.sBackgroundImage = sBackgroundImage;
		this.sTemplate = sTemplate;
		this.sLabel = sLabel;
		this.nNodeType = nNodeType;
		this.nShortcut = nShortcut;
		this.vtTags = vtTags;
		this.sTip = sTip;

		if (nShortcut > -1)
			setToolTipText(sTip+" - ALT + "+(new Integer(nShortcut)).toString()); //$NON-NLS-1$
		else
			setToolTipText(sTip);

		dragSource = new DragSource();
		dragSource.createDefaultDragGestureRecognizer((Component)this, DnDConstants.ACTION_COPY, this);
  	}

	/**
	 * Set the node image associated with this draggable stencil icon.
	 * @param sImage the node image associated with this draggable stencil icon.
	 */
	public void setImage(String sImage) {
		this.sImage = sImage;
	}

	/**
	 * Return the node image associated with this draggable stencil icon.
	 * @return String the node image associated with this draggable stencil icon.
	 */
	public String getImage() {
		return sImage;
	}

	/**
	 * Set the palette image associated with this draggable stencil icon.
	 * @param sPaletteImage the palette image associated with this draggable stencil icon.
	 */
	public void setPaletteImage(String sPaletteImage) {
		this.sPaletteImage = sPaletteImage;
	}

	/**
	 * Return the palette image associated with this draggable stencil icon.
	 * @return String the palette image associated with this draggable stencil icon.
	 */
	public String getPaletteImage() {
		return sPaletteImage;
	}

	/**
	 * Set the background image associated with this draggable stencil icon.
	 * @param sBackgroundImage the background image associated with this draggable stencil icon.
	 */
	public void setBackgroundImage(String sBackgroundImage) {
		this.sBackgroundImage = sBackgroundImage;
	}

	/**
	 * Return the background image associated with this draggable stencil icon.
	 * @return String the background image associated with this draggable stencil icon.
	 */
	public String getBackgroundImage() {
		return sBackgroundImage;
	}

	/**
	 * Set the template associated with this draggable stencil icon.
	 * @param sTemplate the template associated with this draggable stencil icon.
	 */
	public void setTemplate(String sTemplate) {
		this.sTemplate = sTemplate;
	}

	/**
	 * Return the template associated with this draggable stencil icon.
	 * @return String the template associated with this draggable stencil icon.
	 */
	public String getTemplate() {
		return sTemplate;
	}

	/**
	 * Return the name associated with this draggable stencil icon.
	 * This is for the sort function to use. If there is a ToolTip use that,
	 * else use the node label.
	 * @return String the name of this draggable stencil icon.
	 */
	public String getName() {
		if (sTip != null && !sTip.equals("")) { //$NON-NLS-1$
			return sTip;
		}

		if (sLabel == null)
			sLabel = ""; //$NON-NLS-1$

		return sLabel;
	}

	/**
	 * Set the node label associated with this draggable stencil icon.
	 * @param sLabel the node label associated with this draggable stencil icon.
	 */
	public void setLabel(String sLabel) {
		this.sLabel = sLabel;
	}

	/**
	 * Return the node label associated with this draggable stencil icon.
	 * @return String the node label of this draggable stencil icon.
	 */
	public String getLabel() {
		return sLabel;
	}

	/**
	 * Set the tool tip text associated with this draggable stencil icon.
	 * @param sTip the tool tip text associated with this draggable stencil icon.
	 */
	public void setToolTip(String sTip) {
		this.sTip = sTip;
		if (nShortcut > -1)
			setToolTipText(sTip+" - ALT + "+(new Integer(nShortcut)).toString()); //$NON-NLS-1$
		else
			setToolTipText(sTip);
	}

	/**
	 * Return the tool tip text associated with this draggable stencil icon.
	 * @return String the tool tip text of this draggable stencil icon.
	 */
	public String getToolTip() {
		return sTip;
	}

	/**
	 * Return the node type associated with this draggable stencil icon.
	 * @return int the node type associated with this draggable stencil icon.
	 */
	public int getNodeType() {
		return nNodeType;
	}

	/**
	 * Set the node type associated with this draggable stencil icon.
	 * @param nType the node type associated with this draggable stencil icon.
	 */
	public void setNodeType(int nType) {
		this.nNodeType = nType;
	}

	/**
	 * Return the shortcut number associated with this draggable stencil icon.
	 * @return int the shortcut number associated with this draggable stencil icon.
	 */
	public int getShortcut() {
		return nShortcut;
	}

	/**
	 * Set the shortcut number associated with this draggable stencil icon.
	 * @param nShortcut the shortcut number associated with this draggable stencil icon.
	 */
	public void setShortcut(int nShortcut) {
		this.nShortcut = nShortcut;
		if (nShortcut > -1)
			setToolTipText(sTip+" - ALT + "+(new Integer(nShortcut)).toString()); //$NON-NLS-1$
	}

	/**
	 * Return the tags associated with this draggable stencil icon.
	 * @return Vector the tags associated with this draggable stencil icon.
	 */
	public Vector getTags() {
		return vtTags;
	}

	/**
	 * Set the tags associated with this draggable stencil icon.
	 * @param vtTags the tags associated with this draggable stencil icon.
	 */
	public void setTags(Vector vtTags) {
		this.vtTags = vtTags;
	}

	/**
	 * Make a duplicate of this object but with a new id.
	 */
	public DraggableStencilIcon duplicate() {
		
		ImageIcon oIcon = null;
		if (sPaletteImage.equals("")) { //$NON-NLS-1$
			oIcon = UIImages.thumbnailIcon(sImage);
		} else {
			oIcon = UIImages.thumbnailIcon(sPaletteImage);
		}
		DraggableStencilIcon oItem = new DraggableStencilIcon(sImage, sPaletteImage, sBackgroundImage, sTemplate, sLabel, sTip, nNodeType, nShortcut, (Vector)vtTags.clone(), oIcon);
		return oItem;
	}

   /**
     * Returns an array of DataFlavor objects indicating the flavors the data
     * can be provided in.
     * @return an array of data flavors in which this data can be transferred
     */
	public DataFlavor[] getTransferDataFlavors() {
		return supportedFlavors;
	}

    /**
     * Returns whether or not the specified data flavor is supported for
     * this object.
     * @param flavor the requested flavor for the data
     * @return boolean indicating whether or not the data flavor is supported
     */
	public boolean isDataFlavorSupported(DataFlavor flavor) {
		return flavor.isMimeTypeEqual(DataFlavor.javaJVMLocalObjectMimeType);
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
		if (flavor.isMimeTypeEqual(DataFlavor.javaJVMLocalObjectMimeType))
			return this;
		else return null;
	}

    /**
     * A <code>DragGestureRecognizer</code> has detected
     * a platform-dependent drag initiating gesture and
     * is notifying this listener
     * in order for it to initiate the action for the user.
     * <P>
     * @param e the <code>DragGestureEvent</code> describing
     * the gesture that has just occurred
     */
	public void dragGestureRecognized(DragGestureEvent e) {
	    InputEvent in = e.getTriggerEvent();
	    if (in instanceof MouseEvent) {
			MouseEvent evt = (MouseEvent)in;
			boolean isLeftMouse = SwingUtilities.isLeftMouseButton(evt);

		    if (isLeftMouse && !evt.isAltDown()) {
		    	this.requestFocus();
				dragSource.startDrag(e, DragSource.DefaultCopyDrop, this, this);
			}
		}
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
	 * HERE THE METHOD DOES NOTHING.
     *
     * @param e the <code>DragSourceDropEvent</code>
     */
	public void dragDropEnd(DragSourceDropEvent e) {}

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
	public void dragEnter(DragSourceDragEvent e) {}

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
	public void dragExit(DragSourceEvent e) {}

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
	 * HERE THE METHOD DOES NOTHING.
     *
     * @param e the <code>DragSourceDragEvent</code>
     */
	public void dragOver(DragSourceDragEvent e) {}

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
	public void dropActionChanged(DragSourceDragEvent e) {}
}
