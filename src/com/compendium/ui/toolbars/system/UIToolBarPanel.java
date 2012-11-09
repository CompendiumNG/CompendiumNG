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

package com.compendium.ui.toolbars.system;

import java.awt.*;
import java.awt.event.*;
import java.awt.dnd.*;
import java.awt.datatransfer.*;

import java.util.*;
import java.io.*;

import javax.swing.*;
import javax.swing.border.*;

/**
 * This class manages the toolbars placed in it.
 *
 * @author	Michelle Bachler
 */
public class UIToolBarPanel extends JPanel implements Transferable, DropTargetListener, DragSourceListener, 
																		DragGestureListener {

	/** The toolbar held in this panel.*/
	private UIToolBar 	bar 				= null;

	/** The type of the toolbar held in this panel.*/
	private int			type				= -1;
	
	/** The alignment of the toolbar panel.*/
	private int			nAlignment			= 0;

	/** The controller button for this toolbar panel.*/
	private JButton		button				= null;

	/** The rollover hint for this panel.*/
	private String		hint				= "";

	/** The current size of this toolbar panel.*/
	private Dimension	fullSize			= null;

	/** Is this panel currently visible?.*/
	private boolean		isVisible 			= false;

	/** Was this panel visible.*/
	private boolean		wasVisible 			= false;

	/** Has this panel been closed up due to the controller panel being closed?.*/
	private boolean 	isClosed			= false;

	/** Has this panel been put in a flaoting toolbar window?*/
	private boolean		isFloated			= false;

	/** Used by the code when hiding and showing this toolbar panel.*/
	private boolean		visibilityToggled 	= false;

	/** Used by the code when opening and closing this toolbar panel.*/
	private boolean		positionToggled 	= false;

	/** Indocates if this toolbar panel has just been newly created and not yet resized.*/
	private boolean		isNew				= false;

	/** The toolbar Manager managing this toolbar.*/
	private IUIToolBarManager	oManager 	= null;

	/** The toolbar controller currently controlling this toolbar.*/
	private UIToolBarControllerRow oController = null;

	/** If the toolbar is in a floating window, the floating window object.*/
	private UIToolBarFloater    oFloater	= null;

	/** The grid bag layout used to layout this toolbar panel.*/
	private GridBagLayout 		gb 			= null;

	/** The gridbag constraints object used in when laying out this panel.*/
	private GridBagConstraints 	gc 			= null;

	/** Used to hold the y position to open the floating window at.*/
	private int					yPos		= 0;

	/** Used to hold the x position to open the floating window at.*/
	private int 				xPos		= 0;

	/** The drag source object for this panel.*/
	private DragSource 			dragSource 	= null;

	/** The drop target object for this panel.*/
	private DropTarget 			dropTarget 	= null;

	/** This is used to hold the panels position in the controller panel when it is switched on and off.*/
	private int					nPosition	= -1;

	/** The row in the toolbar controller panel that this toolbar panel sits on.*/
	private int					nRow			= 0;
	
	
	/** Holds the variable showing the value when the x pos at which the drag entered this object.*/
	private double	nDragEnterX		= 0;
	
	/** Holds the variable showing the value when the y pos at which the drag entered this object.*/
	private double nDragEnterY		= 0;	

	/** Holds the variable showing the value when the x pos of the last drag move.*/
	private double nDragExitX = 0;	
	
	/** Holds the variable showing the value when the y pos of the last drag move.*/
	private double nDragExitY = 0;
	
	private UIToolBarPanel		dragPanel = null;
	
	/** The data flavors supported by this panel.*/
    public static final 		DataFlavor[] supportedFlavors = { null };
	static    {
		try { supportedFlavors[0] = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType); }
		catch (Exception ex) { ex.printStackTrace(); }
	}

	/**
	 * Constructor.
	 *
	 * @param manager The toolbar Manager managing this toolbar.
	 * @param controller the toolbar controller currently controlling this toolbar.
	 * @param bar the toolbar held in this panel.
	 * @param nType the type of this toolbar.
	 * @param visible is this toolbar panel currently visible.
	 * @param wasvisible was this toolbar panel previously visible.
	 * @param nRow the row this toolbar sits on (stacking)
	 */
	public UIToolBarPanel(IUIToolBarManager manager, UIToolBarControllerRow controller, UIToolBar bar, int nType,
									boolean visible, boolean wasvisible, int row) {

		isNew = true;

		this.oManager = manager;
		this.oController = controller;
		this.nAlignment = controller.getAlignment();
		this.bar = bar;
		this.type = nType;
		this.hint = bar.getName();
		this.nRow = row;

		button = new JButton();
		setButtonIcon();
		//button.setToolTipText(hint);
		button.setToolTipText(bar.getName()+": Right-Click to float or reposition. Left-Click to minimize");		
		button.setMargin(new Insets(0,0,0,0));
		
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				toggleVisibility();
			}			
		});
		
		final UIToolBarPanel me = this;
		button.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent evt) {

				boolean isRightMouse = SwingUtilities.isRightMouseButton(evt);
				if (isRightMouse) {
					if (!isFloated) {

						if (nRow == 0 && oController.getVisibleCount() == 1
								&& oController.getController().getRowCount() > 1 ) {
							return;
						}
						
						isFloated = true;
						//incase turned off
						me.bar.setVisible(true);

						oFloater = new UIToolBarFloater(oManager, me.bar, type, nRow);

						JFrame parent = oManager.getToolBarFloatFrame();
						Point p = new Point(0, 0);
						p = SwingUtilities.convertPoint((Component)evt.getSource(), evt.getX(), evt.getY(), parent);

						Dimension dim = parent.getSize();
						int screenWidth = dim.width;
						int screenHeight = dim.height;

						Dimension size = oFloater.getSize();
						int xPos = p.x+10;
						int yPos = p.y+10;

						int endXCoord = xPos + oFloater.getWidth();
						int endYCoord = yPos + oFloater.getHeight();

						int offsetX = screenWidth - endXCoord;
						int offsetY = screenHeight - endYCoord;

						if(offsetX > 0)
							offsetX = 0;
						if(offsetY > 0)
							offsetY = 0;

						oFloater.setLocation(xPos+offsetX, yPos+offsetY);
						oFloater.setVisible(true);
						oController.removePanel(me);
					}
				}
			}			
		});		
		
		button.setFocusPainted(false);
		//button.setBorder(null);

		dragSource = new DragSource();
		dragSource.createDefaultDragGestureRecognizer((Component)button, DnDConstants.ACTION_MOVE, this);
		dropTarget = new DropTarget(this, this);

		addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent e) {
				// ONLY VALIDATE RESIZE IF EXTERNAL EVENT CAUSED SIZE CHANGE
				if (visibilityToggled)
					visibilityToggled = false;
				else if (positionToggled)
					positionToggled = false;
				else if (isNew)
					isNew = false;
				else {
					oController.validateResize(me);
				}
			}			
		});
		
		createLayoutManager();
		addToLayout();

		this.isVisible = true;
		this.wasVisible = wasvisible;
		if (!visible)
			hide();
		else
			bar.setVisible(true);
	}

	/**
	 * Create the layout manager used by this panel.
	 */
	private void createLayoutManager() {

		gb = new GridBagLayout();
		setLayout(gb);

		gc = new GridBagConstraints();
		gc.insets = new Insets(0,0,0,0);
		gc.weightx=0;
		gc.weighty=0;
		gc.fill = GridBagConstraints.BOTH;

		if (nAlignment == UIToolBarController.HORIZONTAL_ALIGNMENT) {
			gc.anchor = GridBagConstraints.SOUTHWEST;
		}
		else {
			gc.anchor = GridBagConstraints.NORTHWEST;
		}
	}
	
	/**
	 * Adds the toolbar and panel controller button to this panel.
	 */
	private void addToLayout() {

		if (nAlignment == UIToolBarController.HORIZONTAL_ALIGNMENT) {
			gc.gridx = 0;
			gc.gridy = 0;
			gb.setConstraints(button, gc);
			gc.gridx = 1;
			gc.gridy = 0;
			gb.setConstraints(bar, gc);
		}
		else {
			gc.gridx = 0;
			gc.gridy = 0;
			gb.setConstraints(button, gc);
			gc.gridx = 0;
			gc.gridy = 1;
			gb.setConstraints(bar, gc);
		}

		add(button);
		add(bar);
		validate();
		fullSize = getPreferredSize();
	}
	
	/**
	 * Set the icon used for the toolbat panel controller button
	 * depending on the orientation and status of this panel.
	 */
	private void setButtonIcon() {
		if (!isClosed) {
			if (nAlignment == UIToolBarController.HORIZONTAL_ALIGNMENT)
				button.setIcon(UIToolBarImages.get(UIToolBarImages.TOOLBAR_VERTICAL_ICON));
			else
				button.setIcon(UIToolBarImages.get(UIToolBarImages.TOOLBAR_HORIZONTAL_ICON));
		}
		else {
			if (nAlignment == UIToolBarController.HORIZONTAL_ALIGNMENT)
				button.setIcon(UIToolBarImages.get(UIToolBarImages.TOOLBAR_HORIZONTAL_ICON));
			else
				button.setIcon(UIToolBarImages.get(UIToolBarImages.TOOLBAR_VERTICAL_ICON));
		}
	}		
		
// TRANSFERABLE METHODS
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

//	 DRAG GESTURE LISTENER METHODS

    /**
     * A <code>DragGestureRecognizer</code> has detected
     * a platform-dependent drag initiating gesture and
     * is notifying this listener
     * in order for it to initiate the action for the user.
     * <P>
     * @param e the <code>DragGestureEvent</code> describing
     * the gesture that has just occurred.
     */
	public void dragGestureRecognized(DragGestureEvent e) {

		InputEvent in = e.getTriggerEvent();
		if (in instanceof MouseEvent) {
			MouseEvent evt = (MouseEvent)in;

			if (evt.getID() == MouseEvent.MOUSE_PRESSED) {

				boolean isLeftMouse = SwingUtilities.isLeftMouseButton(evt);
				//boolean isRightMouse = SwingUtilities.isRightMouseButton(evt);

				if (isLeftMouse ) {
					DragSource source = (DragSource)e.getDragSource();
					source.startDrag(e, DragSource.DefaultLinkDrop, this, this);
				}
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
     *
     * @param e the <code>DragSourceDropEvent</code>
     */
	public void dragDropEnd(DragSourceDropEvent e) {}

// DRAG SOURCE LISTENER METHODS
	
    /**
     * Called as the cursor's hotspot enters a platform-dependent drop site.
     * This method is invoked when all the following conditions are true:
     * <UL>
     * <LI>The cursor's hotspot enters the operable part of a platform-
     * dependent drop site.
     * <LI>The drop site is active.
     * <LI>The drop site accepts the drag.
     * </UL>
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
     *
     * @param e the <code>DragSourceDragEvent</code>
     */
	public void dropActionChanged(DragSourceDragEvent e) {}

// DROP TARGET

    /**
     * Called if the user has modified
     * the current drop gesture.
     * <P>
     * @param e the <code>DropTargetDragEvent</code>
     */
	public void dropActionChanged(DropTargetDragEvent e) {}

    /**
     * Called when a drag operation is ongoing, while the mouse pointer is still
     * over the operable part of the drop site for the <code>DropTarget</code>
     * registered with this listener.
     *
     * @param e the <code>DropTargetDragEvent</code>
     */
	public void dragOver(DropTargetDragEvent e) {
		/*nDragExitY = e.getLocation().getY();
		nDragExitX = e.getLocation().getX();	
		System.out.println("in drag exit x="+nDragExitX);		
		System.out.println("in drag exit y="+nDragExitY);		
		
		if (dragPanel == null) {
		 	try{ 
		 		Object source = e.getTransferable().getTransferData(UIToolBarPanel.supportedFlavors[0]);
		 	
		 		if (source instanceof UIToolBarPanel) {
		 			dragPanel = (UIToolBarPanel)source;
		 		}
		 	} catch(Exception ex) {
		 		ex.printStackTrace();
		 	}
		}*/
	}

    /**
     * Called while a drag operation is ongoing, when the mouse pointer has
     * exited the operable part of the drop site for the
     * <code>DropTarget</code> registered with this listener.
     *
     * @param e the <code>DropTargetEvent</code>
     */
	public void dragExit(DropTargetEvent e) {
		//lastDragOver = e.getLocation();
		//System.out.println("source = "+e.getSource());
		/*System.out.println("in mouse exit row="+nRow);		
		
		// Has just been created and has not had row added yet.
		if (oController.getTotalCount() == 0) {
			return;
		}
		
		try {
			if (dragPanel != null) {		
				// Don't allow drag of last item in a row to create a new row with.
				int sourceRow = dragPanel.getRow();
				int items = dragPanel.getController().getVisibleCount();
				if ((sourceRow == 0 && items <= 2) || (sourceRow > 0 && items <= 1)) {
					return;
				}
				
				int rowCount = oController.getController().getRowCount();								
				System.out.println("rowCount = "+rowCount);
				// we are the last row
				if (rowCount == nRow+1) {
					int pos = oController.getController().getPosition();
					System.out.println("pos="+pos);
					System.out.println("TOP="+UIToolBarController.TOP);
					System.out.println("drag enter y = "+nDragEnterY);
					System.out.println("drag exit y = "+nDragExitY);
					if (pos == UIToolBarController.TOP && nDragExitY > nDragEnterY) {
						UIToolBarControllerRow row = oController.getController().createNewRow(nRow+1);						
						row.movePanel(dragPanel, false);
						row.setVisible(true);
						dragPanel.setVisible(true);
						validate();
						repaint();
						row.validate();
						row.repaint();		
						row.getParent().validate();
						row.getParent().repaint();																		
					} else if (pos == UIToolBarController.LEFT && nDragExitX > nDragEnterX){
						UIToolBarControllerRow row = oController.getController().createNewRow(nRow+1);
						row.movePanel(dragPanel, false);
						validate();
						repaint();
						getParent().validate();
						getParent().repaint();												
					} else if (pos == UIToolBarController.RIGHT && nDragExitX < nDragEnterX ){
						UIToolBarControllerRow row = oController.getController().createNewRow(nRow+1);
						row.movePanel(dragPanel, false);
						validate();
						repaint();
						getParent().validate();
						getParent().repaint();												
					} else if (pos == UIToolBarController.BOTTOM && nDragExitY < nDragEnterY ){
						UIToolBarControllerRow row = oController.getController().createNewRow(nRow+1);
						row.movePanel(dragPanel, false);
						validate();
						repaint();
						getParent().validate();
						getParent().repaint();												
					}										
				}			
			}	
		} catch(Exception ex) {
			ex.printStackTrace();		
		}	
		
		dragPanel = null;
		*/		
	}

    /**
     * Called while a drag operation is ongoing, when the mouse pointer enters
     * the operable part of the drop site for the <code>DropTarget</code>
     * registered with this listener.
     *
     * @param e the <code>DropTargetDragEvent</code>
     */
	public void dragEnter(DropTargetDragEvent e) {
		e.acceptDrag(DnDConstants.ACTION_MOVE);
		/*nDragEnterX	 = e.getLocation().getX();
		nDragEnterY	 = e.getLocation().getY();
		System.out.println("in drag enter row="+nRow);		
		System.out.println("in drag enter x="+nDragEnterX);		
		System.out.println("in drag enter y="+nDragEnterY);	
		*/				
	}

    /**
     * Called when the drag operation has terminated with a drop on
     * the operable part of the drop site for the <code>DropTarget</code>
     * registered with this listener.
     * <p>
	 * If another toolbar panel is dropped over this panel, swap positions with it.
     * <P>
     * @param e the <code>DropTargetDropEvent</code>
     */
	public void drop(DropTargetDropEvent e) {

		try {
	       	Object target = e.getSource();
		 	Object source = e.getTransferable().getTransferData(UIToolBarPanel.supportedFlavors[0]);
			if (source instanceof UIToolBarPanel) {

				UIToolBarPanel panel = (UIToolBarPanel)source;

				// IF THE BAR IN THE PANEL DROPPED CANNOT BE AT THIS ORIENTATION, DON'T ALLOW THE DROP
				if (!oController.canDocToolbar(panel))
					return;

			    e.acceptDrop(DnDConstants.ACTION_MOVE);

				if (panel != null) {
					if (panel.getRow() < nRow) {
						oController.movePanel(panel, false);
					} else if (panel.getRow() > nRow) {
						oController.movePanel(panel, false);						
					} else {
						oController.swapPanels(this, panel);
						e.dropComplete(true);
					}
				}
    		}
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
	}

/////////////////////////////////////////////
		
	/**
	 * Collapse this toolbar panel is open.
	 */
	public void hide() {
		if (isVisible)
			toggleVisibility();
	}
	
	/**
	 * if the toolbarpanel is currently collapsed, expand it, if there is enough space.
	 * If the toolbar panel is currently expanded, collapse it.
	 */
	protected void toggleVisibility() {

		visibilityToggled = true;

		if (isVisible) {
			isVisible = false;
			bar.setVisible(false);
		}
		else {
			if (isClosed) {
				oController.togglePosition();
			}

			if (!oController.isEnoughSpace(this)) {
				oController.hideToolBars(this);
			}

			isVisible = true;
			bar.setVisible(true);
		}
	}

	/**
	 * Called by the toolbar controller when it has been opened/closed, to open/close this toolbar panel.
	 * @param boolean closed, true if the toolbar panel should be set to invisible, else false to make it visible.
	 */
	protected void setClosed(boolean closed) {

		if (closed && !isClosed) {
			positionToggled = true;
			isClosed = true;
			setButtonIcon();
			if (isVisible) {
				wasVisible = isVisible;
				isVisible = false;
				bar.setVisible(false);
			}
		}
		else if (!closed && isClosed) {
			positionToggled = true;
			isClosed = false;
			setButtonIcon();
			if (wasVisible) {
				wasVisible = false;
				isVisible = true;
				bar.setVisible(true);
			}
		}
	}	
	
// GETTER AND SETTER

	/**
	 * Set the toolbar controller row for this panel.
	 *
	 * @param UIToolBarControllerRow controller, the current controller  of this panel.
	 */
	public void setController(UIToolBarControllerRow controller) {
		oController = controller;
	}

	/**
	 * Return the <code>UIToolBarControllerRow</code>, currently associated with this toolbar panel.
	 * @return UIToolBarControllerRow currently associated with this toolbar panel.
	 */
	public UIToolBarControllerRow getController() {
		return oController;
	}

	/**
	 * Return the current alignment of this toolbar panel.
	 * @return int, the current alignment of this toolbar panel.
	 */
	public int getAlignment() {
		return nAlignment;
	}

	/**
	 * Set the current alignment of this toolbar panel.
	 * @param int alignment, the current alignment of this toolbar panel.
	 */
	public void setAlignment(int alignment) {
		if (alignment != nAlignment) {
			nAlignment = alignment;
			setButtonIcon();
			if (nAlignment == UIToolBarController.HORIZONTAL_ALIGNMENT) {
				if (bar.getOrientation() == SwingConstants.VERTICAL) {
					bar.setOrientation(SwingConstants.HORIZONTAL);
				}
			}
			else {
				if (bar.getOrientation() == SwingConstants.HORIZONTAL) {
					bar.setOrientation(SwingConstants.VERTICAL);
				}
			}
			removeAll();
			addToLayout();
		}
	}

	/**
	 * Return the full size of this toolbar panel.
	 * @return Dimension, the full size of this toolbar panel.
	 */
	public Dimension getActualSize() {
		return fullSize;
	}

	/**
	 * Return if this toolbar panel is currently in a floating window.
	 * @return boolean, true if this toolbar panel is currently in a floating window, else false.
	 */
	public boolean getIsFloated() {
		return isFloated;
	}

	/**
	 * Return if this toolbar panel is currently closed.
	 * @return boolean, true if this toolbar panel is currently closed, else false.
	 */
	public boolean getIsClosed() {
		return isClosed;
	}

	/**
	 * Return if this toolbar panel is currently visible.
	 * @return boolean, true if this toolbar panel is currently visible, else false.
	 */
	public boolean getIsVisible() {
		return isVisible;
	}

	/**
	 * Return if this toolbar panel was visible.
	 * @return boolean, true if this toolbar panel was visible.
	 */
	public boolean getWasVisible() {
		return wasVisible;
	}

	/**
	 * Return the size of the button on this toolbarpanel.
	 * @return Dimension, the size of the button on this toolbarpanel.
	 */
	public Dimension getButtonSize() {
		return button.getSize();
	}

	/**
	 * Return the <code>UIToolBar</code>, currently associated with this toolbar panel.
	 * @return UIToolBar, currently associated with this toolbar panel.
	 */
	public UIToolBar getToolBar() {
		return bar;
	}

	/**
	 * Set the type of this toolbar panel. This is an identifier, and can be anything the user desires.
	 * In Compendium code see the UIToolBarManager class for toolbar panel types.
	 * @param in type, the type of this toolbar panel.
	 */
	public void setToolBarType(int type) {
		this.type = type;
	}

	/**
	 * Return the type of this toolbar panel.
	 * @return int, the type of this toolbar panel.
	 */
	public int getToolBarType() {
		return type;
	}

	/**
	 * Return the position the toolbar panel is in, in the controller panel.
	 * @return int, the position the toolbar panel is in, in the controller panel.
	 */
	public int getPosition() {
		return nPosition;
	}

	/**
	 * Set the position the toolbar panel is in, in the controller panel.
	 * @param int, the position the toolbar panel is in, in the controller panel.
	 */
	public void setPosition(int nPos) {
		nPosition = nPos;
	}

	/**
	 * Return the row the toolbar panel is in, in the controller panel.
	 * @return int, the row the toolbar panel is in, in the controller panel.
	 */
	public int getRow() {
		return nRow;
	}

	/**
	 * Set the row the toolbar panel is in, in the controller panel.
	 * @param int, the row the toolbar panel is in, in the controller panel.
	 */
	public void setRow(int nRow) {
		this.nRow = nRow;
	}
}
