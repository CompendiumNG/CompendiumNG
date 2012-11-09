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
import java.beans.*;

import java.util.*;

import javax.swing.*;
import javax.swing.border.*;

/**
 * This class extends JToolbar to hold some extra information for Compendium's toolbar system
 * and to override some basic behaviours like drag/drop floatability.
 *
 * @author	Michelle Bachler
 */
public class UIToolBar extends JToolBar {

	/** Indicates the toolbar is dockable on all sides.*/
	public static int	ALLSIDES	= 0;

	/** Indicates that the toolbar is only dockable in the horizontal position, so North and South.*/
	public static int	NORTHSOUTH	= 1;

	/** Indicates that the toolbar is only dockable in the vertical position, si East and West.*/
	public static int 	EASTWEST 	= 2;

	/** The name of this toolbar.*/
	private String 		sName				= "Unknown";

	/** The dockable orientation/s allowed for this toolbar.*/
	private int			dockableOrientation = 0;

    /**
     * Creates a new tool bar; dockable orientation defaults to <code>ALLSIDES</code>.
     */
 	public UIToolBar() {
		this("Unknown", ALLSIDES);
	}

   /**
     * Creates a new tool bar with the specified <code>orientation</code>.
     * The dockable <code>orientation</code> must be either <code>ALLSIDES</code>
     * , <code>NORTHSOUTH</code> or <code>EASTWEST</code>.
     *
     * @param orientation  the dockable orientation desired
     */
	public UIToolBar(int orientation) {
		this("Unknown", orientation);
	}

    /**
     * Creates a new tool bar with the specified <code>name</code>.  The
     * name is used as the title of the undocked tool bar and the tooltip on the docked toolbar.
	 * The default dockable orientation is <code>ALLSIDES</code>.
     *
     * @param name the name of the tool bar
     */
	public UIToolBar(String name) {
		this(name, ALLSIDES);
	}

    /**
     * Contructor. Creates a new tool bar with the specified <code>name</code>.  The
     * name is used as the title of the undocked tool bar and the tooltip on the docked toolbar.
     * The dockable <code>orientation</code> must be either <code>ALLSIDES</code>
     * , <code>NORTHSOUTH</code> or <code>EASTWEST</code>.
	 *
     * @param String name, the name of the tool bar
	 * @param int orientation, the orientation to draw this toolbar.
     */
	public UIToolBar(String name, int orientation) {
		super(name);

		// DUE TO A PAINT BUG WITH THE SEPARATOR ALWAYS CREATE AS HORIZONTAL
		// THEN ADJUST LATER UIToolBarController

		if (orientation == EASTWEST)
			setOrientation(SwingConstants.VERTICAL);
		else
			setOrientation(SwingConstants.HORIZONTAL);

		setDockableOrientation(orientation);
		this.sName = name;
		setFloatable(false);
		setMargin(new Insets(0,0,0,0));
	}

	/**
	 * Adds a button to the toolbar and sets some default properties for each button.
	 *
	 * @param JButton button, the button to add to this toolbar.
	 */
	public void add(JButton button) {
		super.add(button);
	}

	/**
	 * Creates a button for the toolbar with focus disabled and insets of 1.
	 *
	 * @param String hint, the hint to add to this toolbar button.
	 * @param ImageIcon icon, the icon to add to this toolbar button.
	 * @return JButton, the newly created toolbar button.
	 */
	public JButton createToolBarButton(String hint, ImageIcon icon) {
		JButton btn = new JButton(icon);
		btn.setRequestFocusEnabled(false);
		btn.setToolTipText(hint);
		btn.setMargin(new Insets(0,0,0,0));
		return btn;
	}

	/**
	 * Creates a radio button for the toolbar with focus disabled and insets of 1.
	 *
	 * @param String hint, the hint to add to this toolbar radio button.
	 * @param ImageIcon icon, the icon to add to this toolbar radio button.
	 * @return JRadioButton, the newly created toolbar radio button.
	 */
	public JRadioButton createToolBarRadioButton(String hint, ImageIcon icon) {
		JRadioButton btn = new JRadioButton(icon);
		btn.setRequestFocusEnabled(false);
		btn.setToolTipText(hint);
		btn.setMargin(new Insets(0,0,0,0));
		return btn;
	}

	/**
	 * Creates a button for the toolbar with focus disabled and insets of 1.
	 *
	 * @param String hint, the hint to add to this toolbar button.
	 * @param String label, the test to add to this button.
	 * @return JButton, the newly created toolbar button.
	 */
	public JButton createToolBarButton(String hint, String label) {
		JButton btn = new JButton(label);
		btn.setRequestFocusEnabled(false);
		btn.setToolTipText(hint);
		btn.setMargin(new Insets(0,0,0,0));
		return btn;
	}
	
	/**
	 * Creates a radio button for the toolbar with focus disabled and insets of 1.
	 *
	 * @param String hint, the hint to add to this toolbar radio button.
	 * @param sLabel the string to use as the button label.
	 * @return JRadioButton, the newly created toolbar radio button.
	 */
	public JRadioButton createToolBarRadioButton(String hint, String sLabel) {
		JRadioButton btn = new JRadioButton(sLabel);
		btn.setRequestFocusEnabled(false);
		btn.setToolTipText(hint);
		btn.setMargin(new Insets(0,0,0,0));
		return btn;
	}
	

	/**
	 * Creates a draggable button for the toolbar and sets some default properties.
	 *
	 * @param int identifier, the identifier for the drop action. This will be turned into a String.
	 * @param String hint, the text for the tooltip
	 * @param ImageIcon icon, the icon to be shown in the label
	 * @return DraggableToolBarIcon, the newly created draggable toolbar icon.
	 */
	public DraggableToolBarIcon createDraggableToolBarButton(int identifier, String hint, ImageIcon icon) {
		DraggableToolBarIcon btn = new DraggableToolBarIcon(new Integer(identifier).toString(), icon);
		btn.setRequestFocusEnabled(false);
		btn.setToolTipText(hint);
		btn.setBorder(new CompoundBorder(new LineBorder(Color.darkGray, 1), new EmptyBorder(2,2,2,2)) );
		return btn;
	}

	/**
	 * Creates a draggable button for the toolbar and sets some default properties.
	 *
	 * @param String identifier, the identifier for the drop action.
	 * @param String hint, the text for the tooltip
	 * @param ImageIcon icon, the icon to be shown in the label
	 * @return DraggableToolBarIcon, the newly created draggable toolbar icon.
	 */
	public DraggableToolBarIcon createDraggableToolBarButton(String identifier, String hint, ImageIcon icon) {
		DraggableToolBarIcon btn = new DraggableToolBarIcon(identifier, icon);
		btn.setRequestFocusEnabled(false);
		btn.setToolTipText(hint);
		btn.setBorder(new CompoundBorder(new LineBorder(Color.darkGray, 1), new EmptyBorder(2,2,2,2)) );
		return btn;
	}

	/**
	 * Overridden to fix VERTICAL orientation bug.
	 * @param int orientation, the orientation of this toolbar (SwingConstants.VERTICAL, SwingConstants.HORIZONTAL).
	 */
	public void setOrientation(int orientation) {

		super.setOrientation(orientation);

		if (orientation == SwingConstants.VERTICAL) {

			// JAVA HAS A BUG IN THIER JTOOLBAR WHICH MEANS THAT
			// THE SEPARATORS AND ICONS ARE NOT LINED UP PROPERLY BY THE BOXLAYOUT IT USES.
			// AND THE SEPARATOR DRAW THE WRONG SIZE ON VERTICAL TOOLBARS. THIS FIXES THAT
			Component [] comps = getComponents();
			int count = comps.length;
			for (int i=0; i<count; i++) {
				Component comp = comps[i];
				if (comp instanceof JComponent) {
					JComponent jcomp = (JComponent)comp;
					jcomp.setAlignmentX(0);
				}
			}
			validate();
		}
	}

	/**
	 * Overridden to diable/enable all components int the toolbar correctly.
	 * @param boolean enabled, true to enable, false to disable.
	 */
	public void setEnabled(boolean enabled) {

		Component [] comps = getComponents();
		int count = comps.length;
		for (int i=0; i<count; i++) {
			Component comp = comps[i];
			if (comp instanceof JComponent) {
				JComponent jcomp = (JComponent)comp;
				jcomp.setEnabled(enabled);
			}
		}
	}

	/**
	 * Override to always set to false
	 */
	public void setFloatable(boolean floatable) {
		super.setFloatable(false);
	}

	/**
	 * Sets the docking orientation preference of this toolbar.
	 * This can be either ALLSIDES, NORTHSOUTH or EASTEAST.
	 * If any other int is passed, the dockable orientation is set to ALLSIDES.
	 *
	 * @param int orientation, the allowed dockable orientation/s of this toolbar.
	 */
	public void setDockableOrientation(int orientation) {
		if (orientation > 2 || orientation <= 0)
			dockableOrientation = ALLSIDES;
		else
			dockableOrientation = orientation;
	}

	/**
	 * Returns the allowable docking orientation of this toolbar.
	 *
	 * @return int, <code>dockableOrientation</code>
	 */
	public int getDockableOrientation() {
		return dockableOrientation;
	}

	/**
	 * Override to do nothing
	 */
	public void setMargin(int a, int b, int c, int d) {}

	/**
	 * Return the toolbar's name.
	 * @return String, the name of this toolbar.
	 */
	public String getName() {
		return sName;
	}

    /**
     * Appends a separator of default size to the end of the tool bar.
     * The default size is determined by the current look and feel.
     */
    public void addSeparator() {
		int ori = JSeparator.HORIZONTAL;
		if (getOrientation() == SwingConstants.VERTICAL)
			ori = JSeparator.VERTICAL;

        UIToolBar.Separator s = new UIToolBar.Separator();
		s.setAlignmentX(0);

		Dimension dim = s.getPreferredSize();
		s.setOrientation(ori);
		if (getOrientation() == SwingConstants.VERTICAL)
			s.setSeparatorSize(new Dimension(dim.height, dim.width));
        add(s);
    }

    /**
     * Appends a separator of a specified size to the end
     * of the tool bar.
     *
     * @param size the <code>Dimension</code> of the separator
     */
    public void addSeparator( Dimension size ) {
		int ori = JSeparator.HORIZONTAL;
		if (getOrientation() == SwingConstants.VERTICAL)
			ori = JSeparator.VERTICAL;

        UIToolBar.Separator s = new UIToolBar.Separator( size );
		s.setAlignmentX(0);

		Dimension dim = s.getPreferredSize();
		s.setOrientation(ori);
		if (getOrientation() == SwingConstants.VERTICAL)
			s.setSeparatorSize(new Dimension(dim.height, dim.width));

        add(s);
    }
}
