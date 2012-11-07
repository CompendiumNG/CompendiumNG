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
 * This class controls a collection of toolbar panels which can be added and removed from it.
 *
 * @author	Michelle Bachler
 */
public class UIToolBarControllerRow extends JPanel implements SwingConstants, DropTargetListener {

	/** The toolbar manager managing this toolbar controller instance.*/
	private UIToolBarController	oManager 				= null;

	/** Holds a list of the toolbars currently docked with this toolbar controller panel.*/
	private Hashtable 			htToolBarPanels			= null;

	/** Holds a list of the toolbars currently docked but not switched on with this toolbar controller panel.*/
	private Hashtable 			htOffToolBarPanels		= null;

	/** Holds the current alignment of this toolbar controller panel.*/
	private int					nAlignment				= 0;

	/** Row.*/
	private int					nRow					= 0;

	/** Holds the gap to paint between toolbars held in this toolbar controller.*/
	private int					nGap					= 4;

	/** The layout used by this panel*/
	private GridBagLayout 		gb 						= null;

	/** The constraint instance used by this panel for the layout.*/
	private GridBagConstraints 	gc 						= null;

	/** The DropTarget instance associated with this toolbar controller panel.*/
	private DropTarget 			dropTarget 				= null;

	/** The filler label for this row*/
	private JLabel filler = null;
	
	/** The current row position count.*/
	private int nPositionCount = 0;	

	
	/**
	 * Constructor.
	 *
	 * @param IUIToolBarManager manager, the manager responsible for this tollbar controller panel.
	 */
	public UIToolBarControllerRow(UIToolBarController manager) {
		this(manager, UIToolBarController.NORTH);
	}

	/**
	 * Constructor.
	 *
	 * @param IUIToolBarManager manager, the manager responsible for this tollbar controller panel.
	 * @param int pos, the position this toolbar will be drawn in (TOP / BOTTOM / LEFT / RIGHT).
	 * @param boolean isClosed, indicates whether to draw this toolbar conroller panel collapsed or expanded.
	 */
	public UIToolBarControllerRow(UIToolBarController manager, int pos) {

		oManager = manager;

		//setBorder(new LineBorder(Color.red, 1));
		
		htToolBarPanels = new Hashtable(10);
		htOffToolBarPanels = new Hashtable(10);

		nRow = pos;
		nAlignment = oManager.getAlignment();
		
		dropTarget = new DropTarget(this, this);

		createLayoutManager();
		
		addFiller();

		setVisible(false);
	}

// DRAG AND DROP TARGET

    /**
     * Called if the user has modified
     * the current drop gesture.
     * <P>
	 * THIS METHOD DOES NOTHING HERE.
     * @param e the <code>DropTargetDragEvent</code>
     */
	public void dropActionChanged(DropTargetDragEvent e) {}

    /**
     * Called when a drag operation is ongoing, while the mouse pointer is still
     * over the operable part of the drop site for the <code>DropTarget</code>
     * registered with this listener.
	 * THIS METHOD DOES NOTHING HERE.
     *
     * @param e the <code>DropTargetDragEvent</code>
     */
	public void dragOver(DropTargetDragEvent e) {
	}

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
     * Called while a drag operation is ongoing, when the mouse pointer enters
     * the operable part of the drop site for the <code>DropTarget</code>
     * registered with this listener.
     *
     * @param e the <code>DropTargetDragEvent</code>
     */
	public void dragEnter(DropTargetDragEvent e) {
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
	 * This method accepts or declines the drop of a toolbar panel.
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
				if (!canDocToolbar(panel))
					return;

		 	    e.acceptDrop(DnDConstants.ACTION_MOVE);

				if (panel != null) {
					// Drop point is inside the filler component so need to convert.
					Point dropPoint = e.getLocation();
					dropPoint = SwingUtilities.convertPoint(filler, dropPoint, this);
					
					Point sourcePoint = panel.getLocation();
					int sourceRow = panel.getRow();
					if (nRow != sourceRow) {
						movePanel(panel, false);
					} else { 
						if (nAlignment == UIToolBarController.HORIZONTAL_ALIGNMENT) {
							if (dropPoint.x > sourcePoint.x)
								movePanel(panel, false);
							else {
								System.out.println("Move Panel true");
								movePanel(panel, true);
							}
						}
						else {
							if (dropPoint.y > sourcePoint.y)
								movePanel(panel, false);
							else
								movePanel(panel, true);
						}
					}
					e.dropComplete(true);
				}
    		}
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Does this controller row contain the given panel.
	 *
	 * @param panel the panel to validate.
	 * @return true if the panel passed is in this row
	 */
	public boolean containsPanel(UIToolBarPanel panel) {

		return htToolBarPanels.containsKey(panel.getToolBar());
	}

	public int getAlignment() {
		return nAlignment;
	}
	
	/**
	 * For use by drop methods here and in <code>UIToolBarPanel</code>
	 * To check is panel can be docked here.
	 *
	 * @param panel the panel to validate.
	 * @return true if the panel can be docked here else false.
	 */
	public boolean canDocToolbar(UIToolBarPanel panel) {

		// CHECK IF PANEL HAS BEEN DRAGGED FROM ANOTHER BAR
		if (!htToolBarPanels.containsKey(panel.getToolBar())) {
			UIToolBar bar  = panel.getToolBar();
			if ( (bar.getDockableOrientation() == UIToolBar.NORTHSOUTH
					&& nAlignment == UIToolBarController.VERTICAL_ALIGNMENT) ||
				 (bar.getDockableOrientation() == UIToolBar.EASTWEST
					&& nAlignment == UIToolBarController.HORIZONTAL_ALIGNMENT)) {
				return false;
			}
		}
		return true;
	}	
	
	/**
	 * Moved the dragSource <code>UIToolBarPanel</code>.
	 *
	 * @param dragSource the panel to move.
	 * @param toFront whether to move to the front of the controller panel.
	 */
	public void movePanel(UIToolBarPanel dragSource, boolean toFront) {

		//CHECK you are not removing the last visible item from a higher row.
		//If you are, cancel the action.
		//Remember that the first row (0) also has the controller button.
		int sourceRow = dragSource.getRow();
		UIToolBarControllerRow controller = dragSource.getController();
		int items = controller.getVisibleCount();
		if (sourceRow > 0 && items <= 1) {
			return;
		}
		
		// remove from current controller
		dragSource.getController().removePanel(dragSource);

		// set controller and row as this rows
		dragSource.setController(this);
		dragSource.setRow(nRow);

		// add to this controller
		UIToolBar bar  = dragSource.getToolBar();		
		htToolBarPanels.put(bar, dragSource);

		// check alignment, and reset if required
		if (dragSource.getAlignment() != nAlignment )
			dragSource.setAlignment(nAlignment);		
		
		Component comps[] = getComponents();
		removeAll();
		addFiller();

		int count = comps.length;
		for (int i=0; i<count; i++) {
			Component comp = (Component)comps[i];

			if (comp instanceof JButton) {
				addToGrid( (JButton)comp);
				add( (JButton)comp );
				if (toFront) {
					addToGrid(dragSource);
					add(dragSource);
				}
			}
			if (comp instanceof UIToolBarPanel) {
				UIToolBarPanel panel = (UIToolBarPanel)comp;
				if (!panel.equals(dragSource)) {
					addToGrid(panel);
					add(panel);
				}
			}
		}
		if (!toFront) {
			addToGrid(dragSource);
			add(dragSource);
		}

		if (!isVisible()) {
			setVisible(true);
		}
		
		validate();
		repaint();
	}

	/**
	 * Swap the dragSource <code>UIToolBarPanel</code> with the dropTarget <code>UIToolBarPanel</code>.
	 *
	 * @param UIToolBarPanel dropTarget, the panel to swap with.
	 * @param UIToolBarPanel dragSource, the panel to swap for.
	 */
	public void swapPanels(UIToolBarPanel dropTarget, UIToolBarPanel dragSource) {

		// remove from current controller row panel
		dragSource.getController().removePanel(dragSource);

		// set controller as this
		dragSource.setController(this);
		dragSource.setRow(nRow);

		// add to this controller
		UIToolBar bar  = dragSource.getToolBar();		
		htToolBarPanels.put(bar, dragSource);

		// check alignment, and reset if required
		if (dragSource.getAlignment() != nAlignment )
			dragSource.setAlignment(nAlignment);	
		
		Component comps[] = getComponents();
		removeAll();

		// Start all rows again
		addFiller();
		
		int count = comps.length;
		for (int i=0; i<count; i++) {
			Component comp = (Component)comps[i];

			if (comp instanceof JButton) {
				addToGrid( (JButton)comp);
				add( (JButton)comp );
			}
			if (comp instanceof UIToolBarPanel) {
				UIToolBarPanel panel = (UIToolBarPanel)comp;
				if (panel.equals(dropTarget)) {
					addToGrid(dragSource);
					add(dragSource);
				}

				if (!panel.equals(dragSource)) {
					addToGrid(panel);
					add(panel);
				}
			}
		}
		
		if (!isVisible()) {
			setVisible(true);
		}
		
		validate();
		repaint();
	}

//////////////////////////////////////////////////////////////////////////////

	/**
	 * Create the gridbag layout manager, and base gridbag constraint.
	 */
	private void createLayoutManager() {

		gb = new GridBagLayout();
		setLayout(gb);

		gc = new GridBagConstraints();
		gc.insets = new Insets(0,0,0,0);
		gc.weightx=0;
		gc.weighty=0;

		if (nAlignment == UIToolBarController.HORIZONTAL_ALIGNMENT) {
			gc.anchor = GridBagConstraints.WEST;
		}
		else {
			gc.anchor = GridBagConstraints.NORTH;
		}
	}

	/**
	 * Add the passed component to the gridbag layout.
 	 * @param comp, the component to add to the layout manager.
	 */
	public void addToGrid(JComponent comp) {

		UIToolBarPanel panel = null;
		if (comp instanceof UIToolBarPanel) {
			panel = (UIToolBarPanel)comp;
		}
		
		if (!isEnoughSpace(comp))
			hideToolBars(comp);
						
		if (nAlignment == UIToolBarController.HORIZONTAL_ALIGNMENT) {
			gc.gridy = 0;
			gc.gridx = nPositionCount;
			panel.setPosition(nPositionCount);
			nPositionCount++;
			gb.setConstraints(comp, gc);
		}
		else {
			gc.gridx = 0;			
			gc.gridy = nPositionCount;
			panel.setPosition(nPositionCount);						
			nPositionCount++;
			gb.setConstraints(comp, gc);
		}

		gb.invalidateLayout(this);
		gb.layoutContainer(this);
	}
	
	/**
	 * Add a filler JLabel to fill any spare space and thereby align the toolbar panels correctly.
	 * @param nRow the row to add the filler to.
	 */
	private void addFiller() {
		filler = new JLabel(" ");
		//filler.setBorder(new LineBorder(Color.blue, 1));
		dropTarget = new DropTarget(filler, this);
		
		if (nAlignment == UIToolBarController.HORIZONTAL_ALIGNMENT) {
			gc.fill = GridBagConstraints.HORIZONTAL;	
			gc.gridwidth = GridBagConstraints.REMAINDER;							
			gc.gridx = 300;
			gc.weightx=10;
			gb.setConstraints(filler, gc);
		}
		else {
			gc.fill = GridBagConstraints.VERTICAL;	
			gc.gridheight = GridBagConstraints.REMAINDER;							
			gc.gridy = 300;
			gc.weighty=10;
			gb.setConstraints(filler, gc);
		}
		add(filler);
		validate();

		gc.fill = GridBagConstraints.NONE;
		gc.gridwidth = 1;	
		gc.gridheight = 1;											
		gc.weightx=0;
		gc.weighty=0;
	}

	/**
	 * Hide toolbar panels until there is enough space to display the remaining panels correctly.
	 * The component (UIToolBarPanel), should only be closed last.. if required.
	 *
	 * @param JComponent comp, the component to hide if required.
	 */
	public void hideToolBars(JComponent comp) {

		UIToolBarPanel panel = null;
		if (comp instanceof UIToolBarPanel) {
			panel = (UIToolBarPanel)comp;
		}

		for (Enumeration e = htToolBarPanels.elements(); e.hasMoreElements();) {
			UIToolBarPanel nextpanel = (UIToolBarPanel)e.nextElement();
			if (nextpanel.getIsVisible()) {
				if (panel != null && !panel.equals(nextpanel)) {
					nextpanel.hide();
					validate();
					if (isEnoughSpace(comp))
						break;
				}			
			}
		}
		validate();

		if (panel != null && !isEnoughSpace(comp))
			panel.hide();

		repaint();
	}

	/**
	 * Determines if there is enough space on the controler panel to draw all the toolbar panels it holds.
	 * @return boolean, true if there is enough space, else false.
	 */
	public boolean isEnoughSpace(JComponent comp) {

		Dimension compSize = comp.getSize();

		int width = compSize.width;
		int height = compSize.height;

		UIToolBarPanel panel = null;
		if (comp instanceof UIToolBarPanel) {
			panel = (UIToolBarPanel)comp;
			Dimension size = panel.getActualSize();
			width = size.width;
			height = size.height;
		}

		Dimension freeSize = filler.getSize(); 

		// THIS HAPPENS BEFORE COMPENDIUM VISIBLE
		if (freeSize.height == 0 && freeSize.width == 0)
			return true;
		
		if (nAlignment == UIToolBarController.HORIZONTAL_ALIGNMENT) {
			if (width > freeSize.width) return false;
		}
		else {
			if (height > freeSize.height) return false;
		}

		return true;
	}
	
	
	/**
	 * Reverse (Expand/Collapse) the toolbar panels current state.
	 */
	public void togglePosition() {
		oManager.togglePosition();
	}
	
	/**
	 * Reverse (Expand/Collapse) the toolbar panels current state.
	 */
	public void togglePosition(boolean isClosed) {

		for (Enumeration e = htToolBarPanels.elements(); e.hasMoreElements();) {
			UIToolBarPanel panel = (UIToolBarPanel)e.nextElement();
			panel.setClosed(isClosed);
		}

		validate();
		repaint();
	}
	
	
	/**
	 * Toggle the visibility of the toolbar panel for the given toolbar.
	 *
	 * @param UIToolBar bar, the toolbar to switch on/off.
	 * @param boolean switchOn, indicating if this toolbar should be visible.
	 * @return boolean, true if the toolbar is on this panel, else false.
	 */
	public boolean toggleToolBar(UIToolBar bar, boolean switchOn) {

		if (switchOn) {
			if (htOffToolBarPanels.containsKey(bar)) {				
				UIToolBarPanel panel = (UIToolBarPanel)htOffToolBarPanels.get(bar);
				if (!panel.getIsVisible()) {
					panel.toggleVisibility();
				}
				int pos = panel.getPosition();
				htOffToolBarPanels.remove(bar);
				htToolBarPanels.put(bar, panel);
				addPanelAt(panel, pos);
				if (!isVisible()) {
					setVisible(true);
				}
				return true;
			}
		}
		else {
			if (htToolBarPanels.containsKey(bar)) {				
				UIToolBarPanel panel = (UIToolBarPanel)htToolBarPanels.get(bar);
				Component comps[] = getComponents();
				int count = comps.length;
				int pos = -1;
				for (int i=0; i<count; i++) {
					Component comp = (Component)comps[i];
					if (comp instanceof UIToolBarPanel) {
						UIToolBarPanel innerpanel = (UIToolBarPanel)comp;
						if (innerpanel.equals(panel)) {
							pos = i;
							break;
						}
					}
				}

				if (pos == -1) {
					pos = getComponentCount() - 1;
				}

				panel.setPosition(pos);
				htToolBarPanels.remove(bar);
				htToolBarPanels.put(bar, panel);				
				htOffToolBarPanels.put(bar, panel);

				removePanel(panel);
				return true;
			}
		}

		return false;
	}

	/**
	 * Add the given <code>UIToolBar</code> to this controller panel.
	 *
	 * @param bar the toolbar to add.
	 * @param type the type of the toolbar being added.
	 * @param isVisible indicating if this toolbar should be drawn visible.
	 * @param wasVisible indicates if the toolbar was visible.
	 * @param bSwitchOn indicating if this toolbar should be visible.
	 * @param nRow the row to add the toolbar to.
	 */
	public void addToolBar(UIToolBar bar, int type, boolean isVisible, boolean wasVisible, boolean bSwitchOn, boolean isClosed) {

		if (htToolBarPanels.containsKey(bar)) {
			return;
		} else if (htOffToolBarPanels.containsKey(bar)) {
			if (bSwitchOn) {
				toggleToolBar(bar, bSwitchOn);
				return;
			} else {
				return;
			}
		}
		
		if (!isVisible() && bSwitchOn) {
			setVisible(true);
		}

		UIToolBarPanel panel = new UIToolBarPanel(oManager.getManager(), this, bar, type, isVisible, wasVisible, nRow);
		panel.setClosed(isClosed);
		
		if (bSwitchOn) {
			addToGrid(panel);
			add(panel);
			htToolBarPanels.put(bar, panel);
		}
		else {
			htOffToolBarPanels.put(bar, panel);
		}

		validate();
		repaint();

		getParent().validate();
		getParent().repaint();
	}
	
	/**
	 * Add the panel at the gien position.
	 *
	 * @param UIToolBarPanel panel, the panel to add.
	 * @param int pos, the position to add the panel at.
	 */
	private void addPanelAt(UIToolBarPanel panel, int pos) {

		Component comps[] = getComponents();
		removeAll();

		setPositionCount(0);
		addFiller();

		boolean addedPanel = false;
		int count = comps.length;
		for (int i=0; i<count; i++) {
			Component comp = (Component)comps[i];

			if (i == pos) {
				addToGrid(panel);
				add(panel);
				addedPanel = true;
			} else if (comp instanceof JButton) {
				addToGrid( (JButton)comp);
				add( (JButton)comp );
			} else if (comp instanceof UIToolBarPanel) {
				addToGrid( (UIToolBarPanel)comp);
				add( (UIToolBarPanel)comp);
			}
		}

		if (!addedPanel) {
			panel.setPosition(count);
			addToGrid(panel);
			add(panel);
		}

		validate();
		repaint();
	}
	
	/**
	 * Calculates and return the total length needed to display all the toolbars in this toolbar controller panel.
	 * @return int, the length this panel needs to be to display all the toolbars in it.
	 */
	private int	getTotalSpaceNeeded() {

		int totalSpaceNeeded = 0;
		for (Enumeration e = htToolBarPanels.elements(); e.hasMoreElements();) {
			UIToolBarPanel nextpanel = (UIToolBarPanel)e.nextElement();
			Dimension dim = nextpanel.getSize();
			if (nextpanel.getIsVisible()) {
				if (nAlignment == UIToolBarController.HORIZONTAL_ALIGNMENT)
					totalSpaceNeeded += dim.width;
				else
					totalSpaceNeeded += dim.height;
			}
			else {
				Dimension butSize = nextpanel.getButtonSize();
				if (nAlignment == UIToolBarController.HORIZONTAL_ALIGNMENT)
					totalSpaceNeeded += butSize.width;
				else
					totalSpaceNeeded += butSize.height;
			}
		}
		return totalSpaceNeeded;
	}

	/**
	 * Determines if the toolbar controller panel is currently too small to display all the toolbars in it.
	 * @return boolean, true is it is too small, else false.
	 */
	private boolean isTooSmall() {

		Dimension size = oManager.getSize();
		if (size.width == 0 || size.height == 0) {
			// when Compendium first opened, when Item are first painting and are not visible
			// they have no size, so hope for the best, rather than close then all
			return false;
		}

		int totalSpaceNeeded = getTotalSpaceNeeded();

		//System.out.println("total needed = "+totalSpaceNeeded);
		//System.out.println("width = "+size.width);

		if (nAlignment == UIToolBarController.HORIZONTAL_ALIGNMENT) {
			if (size.width < totalSpaceNeeded)
				return true;
		}
		else {
			if (size.height < totalSpaceNeeded)
				return true;
		}

		return false;
	}
	
	/**
	 * Called when this panel is resized to determine if one of more toolbar panel should be collapsed
	 * due to lack of visible space.
	 * @param Component comp the UIToolBarPanel calling this method.
	 */
	public void validateResize(UIToolBarPanel panel) {
								
		boolean foundOne = true;		
		while(isTooSmall() && foundOne) {
			foundOne = false;
			for (Enumeration e = htToolBarPanels.elements(); e.hasMoreElements();) {
				UIToolBarPanel nextpanel = (UIToolBarPanel)e.nextElement();
				if (nextpanel.getIsVisible()) {
					//if (panel != null && !panel.equals(nextpanel)) {
					nextpanel.setVisible(false);
					foundOne = true;
					break;
					//}
				}
			}
		}

		//if (panel != null && !isTooSmall())
		//	panel.setVisible(false);
	}	

	/**
	 * Remove the given <code>UIToolBarPanel</code> from this controller panel.
	 * @param UIToolBarPanel panel, the panel to remove.
	 */
	public void removePanel(UIToolBarPanel panel) {

		htToolBarPanels.remove(panel.getToolBar());
		remove(panel);
		
		// JUST LET THE GRID BAG COUNT UP ELSE THERE IS A CONFLICT WHEN FLOATERS ARE ADDED BACK		
		//nPositionCount--;		

		if (htToolBarPanels.isEmpty() && htOffToolBarPanels.isEmpty()) {
			setVisible(false);
			oManager.removeRow(this, nRow);
		} else 	if (htToolBarPanels.isEmpty() && !htOffToolBarPanels.isEmpty()) {
			setVisible(false);
		}
		else {
			boolean oneIsOn = false;
			for (Enumeration e = htToolBarPanels.elements(); e.hasMoreElements();) {
				UIToolBarPanel nextPanel = (UIToolBarPanel)e.nextElement();
				if (nextPanel.isVisible()) {
					oneIsOn = true;
					break;
				}
			}

			if (!oneIsOn)
				setVisible(false);
		}

		oManager.validateController();
		
		validate();
		repaint();

		if (getParent() != null) {
			getParent().validate();
			getParent().repaint();
		}
	}

	/**
	 * Remove all <code>UIToolBarPanel</code> instances from this controller panel.
	 */
	public void clear() {
		for (Enumeration e = htToolBarPanels.elements(); e.hasMoreElements();) {
			UIToolBarPanel nextpanel = (UIToolBarPanel)e.nextElement();
			removePanel(nextpanel);
		}
		for (Enumeration e = htOffToolBarPanels.elements(); e.hasMoreElements();) {
			UIToolBarPanel nextpanel = (UIToolBarPanel)e.nextElement();
			removePanel(nextpanel);
		}		
	}

	/**
	 * Return if this toolbar controller panel currently contains the given toolbar.
	 * @param JToolBar bar, the bar to check for.
	 * @return boolean, true if the given toolbar is currently in this toolbar controller panel, else false.
	 */
	public boolean containsBar(JToolBar bar) {

		if (htToolBarPanels.containsKey(bar))
			return true;

		return false;
	}
	
	/**
	 * Set the row filler label
	 * @param filler
	 */
	public void setFiller(JLabel filler) {
		this.filler = filler;
	}

	/**
	 * Return the filler object for the row
	 * @return the JLabel acting as the row filler
	 */
	public JLabel getFiller() {
		return filler;
	}
	
	/**
	 * Set the current row position count.
	 * @param nCount
	 */
	public void setPositionCount(int nCount) {
		nPositionCount = nCount;
	}
	
	/**
	 * Return the current row position count.
	 * @return
	 */
	public int getPositionCount() {
		return nPositionCount;
	}		
	
	public int getVisibleCount() {
		return htToolBarPanels.size();
	}

	public int getTotalCount() {
		return (htToolBarPanels.size()+htOffToolBarPanels.size());
	}

	/**
	 * Return the overall controller for this toolbar area.
	 * @return the controller for this toolbar area.
	 */
	public UIToolBarController getController() {
		return oManager;
	}
	
	/**
	 * Creates an XML string representation of the data in this object.
	 *
	 * @return String, an XML string representation of this object.
	 */
	public String toXML() {

		StringBuffer data = new StringBuffer(100);

		Component comps[] = getComponents();
		int count = comps.length;		
		
		// MERGE TOOLBARS WHICH ARE SWITCHED ON AND SWITCHED OFF
		Vector temp = new Vector();
		for (int i=0; i<count; i++) {
			Component comp = (Component)comps[i];
			if (comp instanceof UIToolBarPanel) {
				UIToolBarPanel panel = (UIToolBarPanel)comp;
				temp.addElement(panel);
			}
		}

		for (Enumeration e = htOffToolBarPanels.elements(); e.hasMoreElements(); ) {
			UIToolBarPanel panel = (UIToolBarPanel)e.nextElement();
			int pos = panel.getPosition();
			if (pos > 1) {
				temp.insertElementAt(panel, pos);
			}
			else {
				temp.addElement(panel);
			}
		}

		count = temp.size();
		for (int j=0; j<count; j++) {
			UIToolBarPanel innerpanel = (UIToolBarPanel)temp.elementAt(j);
			data.append("<toolbar type=\""+innerpanel.getToolBarType()+"\"");
			data.append(" name=\""+innerpanel.getToolBar().getName()+"\"");
			data.append(" isVisible=\""+innerpanel.getIsVisible()+"\"");
			data.append(" wasVisible=\""+innerpanel.getWasVisible()+"\"");
			data.append(" row=\""+innerpanel.getRow()+"\"");

			if (htOffToolBarPanels.containsValue(innerpanel)) {
				data.append(" isOn=\"false\">");
			}
			else {
				data.append(" isOn=\"true\">");
			}

			data.append("</toolbar>\n");
		}

		return data.toString();
	}	
}
