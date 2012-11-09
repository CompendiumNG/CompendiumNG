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
public class UIToolBarController extends JPanel implements SwingConstants {

	/** Indicates a top/north positioning*/
	public final static int 	TOP						= 0;

	/** Indicates a botton/south positioning.*/
	public final static int 	BOTTOM					= 1;

	/** Indicates a left/west positioning.*/
	public final static int 	LEFT					= 2;

	/** Indicates a right/east positioning.*/
	public final static int 	RIGHT					= 3;
	
	/** Indicates a horizontal alignment.*/
	protected static int 	HORIZONTAL_ALIGNMENT		= 0;

	/** Indicates a vertical alignment.*/
	protected static int	VERTICAL_ALIGNMENT			= 1;
	
	/** The toolbar manager managing this toolbar controller instance.*/
	private IUIToolBarManager	oManager 				= null;

	/** Holds the current alignment of this toolbar controller panel.*/
	private int					nAlignment				= 0;

	/** Holds the current position (TOP / BOTTOM / LEFT / RIGHT), of this toolbar controller panel.*/
	private int					nPosition				= 0;

	/** Used to paint the expand/collapse button used on this toolbar controller panel.*/
	private JButton				button					= null;

	/** Indicates if this toolbar controller panel has been collapsed.*/
	private boolean				isClosed				= false;

	/** The layout used by this panel*/
	private GridBagLayout 		gb 						= null;

	/** The constraint instance used by this panel for the layout.*/
	private GridBagConstraints 	gc 						= null;

	/** Holds the objects representing a row of toolbars*/
	private Vector 				vtRows					= new Vector(5);
	
	private int					nRowCount				= 0;
	
	
	/**
	 * Constructor.
	 *
	 * @param IUIToolBarManager manager, the manager responsible for this tollbar controller panel.
	 */
	public UIToolBarController(IUIToolBarManager manager) {
		this(manager, UIToolBarController.NORTH, false);
	}

	/**
	 * Constructor.
	 *
	 * @param IUIToolBarManager manager, the manager responsible for this tollbar controller panel.
	 * @param int pos, the position this toolbar will be drawn in (TOP / BOTTOM / LEFT / RIGHT).
	 * @param boolean isClosed, indicates whether to draw this toolbar conroller panel collapsed or expanded.
	 */
	public UIToolBarController(IUIToolBarManager manager, int pos, boolean isClosed) {

		oManager = manager;
		this.isClosed = isClosed;

		nPosition = pos;
		if (nPosition == UIToolBarController.TOP || nPosition == UIToolBarController.BOTTOM)
			nAlignment = UIToolBarController.HORIZONTAL_ALIGNMENT;
		else
			nAlignment = UIToolBarController.VERTICAL_ALIGNMENT;

		button = new JButton();
		setButtonIcon();
		button.setToolTipText("Click to open/close");
		button.setMargin(new Insets(0,0,0,0));
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				togglePosition();
			}			
		});
		button.setFocusPainted(false);
		
		gb = new GridBagLayout();
		setLayout(gb);

		gc = new GridBagConstraints();
		gc.insets = new Insets(0,0,0,0);

		if (nAlignment == UIToolBarController.HORIZONTAL_ALIGNMENT) {
			gc.anchor = GridBagConstraints.WEST;
		}
		else {
			gc.anchor = GridBagConstraints.NORTH;
		}		
		add(button);		
		
		UIToolBarControllerRow row = new UIToolBarControllerRow(this, 0);
		vtRows.addElement(row);
		
		if (nAlignment == UIToolBarController.HORIZONTAL_ALIGNMENT) {			
			gc.gridx = 0;
			gc.gridy = 0;
			gb.setConstraints(button, gc);			
			
			gc.gridx = 1;
			gc.gridy = 0;
			gc.gridwidth = 1;
			gc.fill = GridBagConstraints.HORIZONTAL;		
			gc.gridwidth = GridBagConstraints.REMAINDER;
			gc.weightx=10;			
			gb.setConstraints(row, gc);
			nRowCount++;						
		}
		else {
			gc.gridx = 0;
			gc.gridy = 0;
			gb.setConstraints(button, gc);	
			
			gc.gridx = 0;
			gc.gridy = 1;
			gc.gridheight = 1;
			gc.fill = GridBagConstraints.VERTICAL;		
			gc.gridheight = GridBagConstraints.REMAINDER;	
			gc.weighty=10;			
			gb.setConstraints(row, gc);
			nRowCount++;						
		}
		add(row);
		setVisible(false);
	}
		
	/**
	 * Set the icon used on the controler panel button, depending on the panel position and state.
	 */
	private void setButtonIcon() {

		if (!isClosed) {
			if (nPosition == UIToolBarController.TOP)
				button.setIcon(UIToolBarImages.get(UIToolBarImages.TOOLBAR_UP_NORTH_ICON));
			else if (nPosition == UIToolBarController.BOTTOM)
				button.setIcon(UIToolBarImages.get(UIToolBarImages.TOOLBAR_UP_SOUTH_ICON));
			else if (nPosition == UIToolBarController.LEFT)
				button.setIcon(UIToolBarImages.get(UIToolBarImages.TOOLBAR_UP_WEST_ICON));
			else if (nPosition == UIToolBarController.RIGHT)
				button.setIcon(UIToolBarImages.get(UIToolBarImages.TOOLBAR_UP_EAST_ICON));
		}
		else {
			if (nPosition == UIToolBarController.TOP)
				button.setIcon(UIToolBarImages.get(UIToolBarImages.TOOLBAR_DOWN_NORTH_ICON));
			else if (nPosition == UIToolBarController.BOTTOM)
				button.setIcon(UIToolBarImages.get(UIToolBarImages.TOOLBAR_DOWN_SOUTH_ICON));
			else if (nPosition == UIToolBarController.LEFT)
				button.setIcon(UIToolBarImages.get(UIToolBarImages.TOOLBAR_DOWN_WEST_ICON));
			else if (nPosition == UIToolBarController.RIGHT)
				button.setIcon(UIToolBarImages.get(UIToolBarImages.TOOLBAR_DOWN_EAST_ICON));
		}
	}		
		
	/**
	 * Remove all <code>UIToolBarPanel</code> instances from all rows.
	 */
	public void clear() {
		int count = vtRows.size();
		UIToolBarControllerRow row = null;
		for (int i=0; i<count; i++) {
			row = (UIToolBarControllerRow)vtRows.elementAt(i);
			row.clear();
		}						
	} 	
		
	/**
	 * Remove the given row from this area. 
	 * @param row the UIToolBarControllerRow to remove.
	 * @param nRow the row count of the row being removed.
	 */ 
	protected void removeRow(UIToolBarControllerRow row, int nRow) {
		if (nRow > 0) {
			vtRows.remove(row);
			remove(row);
			gb.invalidateLayout(this);
			gb.layoutContainer(this);
		}
	}
	
	/**
	 * Create a new row for the given row number, and return the row.
	 * @param nRow the row number for the new row.
	 * @return the new UIToolbarControllerRow object.
	 */
	protected UIToolBarControllerRow createNewRow(int nRow) {
		UIToolBarControllerRow row = new UIToolBarControllerRow(this, nRow);
		vtRows.addElement(row);
		if (nAlignment == UIToolBarController.HORIZONTAL_ALIGNMENT) {
			gc.gridx = 0;
			gc.gridy = nRowCount;
			nRowCount++;
			gc.fill = GridBagConstraints.HORIZONTAL;		
			gc.gridwidth = GridBagConstraints.REMAINDER;
			gc.weightx=10;				
			gb.setConstraints(row, gc);
		}
		else {
			gc.gridy = 0;
			gc.gridx = nRowCount;
			nRowCount++;
			gc.fill = GridBagConstraints.VERTICAL;		
			gc.gridheight = GridBagConstraints.REMAINDER;
			gc.weighty=10;
			gb.setConstraints(row, gc);	
		}									
		
		add(row);
		
		gb.invalidateLayout(this);
		gb.layoutContainer(this);	
				
		return row;
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
	public void addToolBar(UIToolBar bar, int type, boolean isVisible, boolean wasVisible, boolean bSwitchOn, int nRow) {

		if (!isVisible() && bSwitchOn) {
			setVisible(true);
		}

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

		UIToolBarControllerRow row = null;
		if (vtRows.size() >= nRow+1) {
			row = (UIToolBarControllerRow)vtRows.elementAt(nRow);
		}
		
		// Don't add a second row if the first row is empty.
		if (row == null && nRow > 0) {
			UIToolBarControllerRow inner_row = (UIToolBarControllerRow)vtRows.elementAt(0);
			if (inner_row.getPositionCount() == 0) { 
				row = inner_row;
			}
		}
		
		if (row == null) {
			row = createNewRow(nRow);
		}
		row.addToolBar(bar, type, isVisible, wasVisible, bSwitchOn, isClosed);
						
		validate();
		repaint();

		getParent().validate();
		getParent().repaint();
	}

	/**
	 * Toggle the visibility of the toolbar panel for the given toolbar.
	 *
	 * @param bar the toolbar to switch on/off.
	 * @param switchOn indicating if this toolbar should be visible.
	 * @return boolean true if the toolbar is on this controller panel else false.
	 */
	public boolean toggleToolBar(UIToolBar bar, boolean switchOn) {	
		
		boolean bFound = false;
		int count = vtRows.size();
		UIToolBarControllerRow row = null;
		for (int i=0; i<count; i++) {
			row = (UIToolBarControllerRow)vtRows.elementAt(i);
			bFound = row.toggleToolBar(bar, switchOn);
			if (bFound && !isVisible() && switchOn) {
				setVisible(true);
			}								
		}		
		
		return bFound;
	}
		
	/**
	 * Return if this toolbar controller panel currently contains the given toolbar.
	 * @param JToolBar bar, the bar to check for.
	 * @return boolean, true if the given toolbar is currently in this toolbar controller panel, else false.
	 */
	public boolean containsBar(JToolBar bar) {

		int count = vtRows.size();
		UIToolBarControllerRow row = null;
		for (int i=0; i<count; i++) {
			row = (UIToolBarControllerRow)vtRows.elementAt(i);
			if (row.containsBar(bar)) {
				return true;
			}
		}		
		return false;
	}	
	
	/**
	 * Creates an XML string representation of the data in this object.
	 *
	 * @return String, an XML string representation of this object.
	 */
	public String toXML() {

		StringBuffer data = new StringBuffer(100);

		data.append("<controller position=\""+nPosition+"\" isClosed=\""+isClosed+"\">\n");
		data.append("<toolbars>");
		data.append("\n");

		int count = vtRows.size();
		UIToolBarControllerRow row = null;
		for (int i=0; i<count; i++) {
			row = (UIToolBarControllerRow)vtRows.elementAt(i);
			data.append(row.toXML());
		}				
		
		data.append("</toolbars>\n");
		data.append("</controller>\n");

		return data.toString();
	}

	/**
	 * Reverse (Expand/Collapse) the toolbar panels current state.
	 */
	protected void togglePosition() {

		if (isClosed) {
			isClosed = false;					
			int count = vtRows.size();
			UIToolBarControllerRow row = null;
			for (int i=0; i<count; i++) {
				row = (UIToolBarControllerRow)vtRows.elementAt(i);
				row.togglePosition(isClosed);
			}			
			setButtonIcon();
			button.setToolTipText("Close up bar");
		}
		else {
			isClosed = true;
			int count = vtRows.size();
			UIToolBarControllerRow row = null;
			for (int i=0; i<count; i++) {
				row = (UIToolBarControllerRow)vtRows.elementAt(i);
				row.togglePosition(isClosed);
			}
			setButtonIcon();
			button.setToolTipText("Open up bar");
		}

		validate();
		repaint();
	}
	
	/**
	 * Check if all rows are empty and if so, close area.
	 */
	protected void validateController() {

		int count = vtRows.size();
		boolean hasPanel = false;
		int panelCount = 0;
		UIToolBarControllerRow row = null;
		for (int i=0; i<count; i++) {
			row = (UIToolBarControllerRow)vtRows.elementAt(i);
			panelCount = row.getVisibleCount();
			if (panelCount != 0) {
				hasPanel = true;
				break;
			}
		}
		
		if (!hasPanel) {
			setVisible(false);
		}
	}

	/**
	 * Called when this panel is resized to determine if one of more toolbar panel should be collapsed
	 * due to lack of visible space.
	 * @param Component comp the UIToolBarPanel calling this method.
	 */
	protected void validateResize(UIToolBarPanel panel) {							
		int count = vtRows.size();
		UIToolBarControllerRow row = null;
		for (int i=0; i<count; i++) {
			row = (UIToolBarControllerRow)vtRows.elementAt(i);
			row.validateResize(panel);
		}				
	}	
	
	/**
	 * 
	 * @return
	 */
	protected int getRowCount() {
		return vtRows.size();
	}
	
	/**
	 * Return the alignment of this toolbar area.
	 * @return the alignment of this toolbar area.
	 */
	protected int getAlignment() {
		return nAlignment;
	}	
	
	/**
	 * Return the position of this controller (NORTH | SOUTH | EAST | WEST)
	 * @return
	 */
	protected int getPosition() {
		return nPosition;
	}
	
	protected IUIToolBarManager getManager() {
		return oManager;
	}
	
	/**
	 * Add a filler JLabel to fill any spare space and thereby align the toolbar panels correctly.
	 * @param nRow the row to add the filler to.
	 */
	private JLabel addFiller() {
		JLabel filler = new JLabel(" ");
		filler.setBorder(new LineBorder(Color.black, 1));

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
		gc.weightx=0;
		gc.weighty=0;
		
		return filler;
	}	
}
