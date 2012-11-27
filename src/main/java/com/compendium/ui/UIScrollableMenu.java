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

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.plaf.*;
import javax.swing.border.*;
import javax.help.*;

import com.compendium.LanguageProperties;
import com.compendium.ProjectCompendium;
import com.compendium.ui.plaf.*;
import com.compendium.core.datamodel.*;


/**
 * This class implements a scrollable menu
 *
 * @author Michelle Bachler
 */
public class UIScrollableMenu extends JMenu {

	/** The default length of the menu before using scrolling*/
	private static final int MENU_LENGTH = 15;

	/** All the menu items in the menu.*/
	private Vector 	menuitems 		= null;

	/** The menu item in the scrollable part of the menu.*/
	private Vector	listitems		= null;
	
	/** A count of the separators adding to this menu.*/
	private Vector	vtSeparators		= null;

	/** Holds the length the menu can be before implementing scrolling.*/
	private int 	menuLength 		= 15;

	/** Holds the position of the item from which scrolling should start.*/
	private int		scrollStart 	= 0;

	/** The visual delay when scrolling.*/
	private int		scrollDelay 	= 300;

	/** Are we scrolling at present?*/
	private boolean	scroll 			= false;

	/** Have sufficient items been added to implement scrolling?*/
	private boolean listAdded		= false;

	/** Holds the up arrow.*/
	private JPanel	upArrow 		= null;

	/** Holds the down arrow.*/
	private JPanel	downArrow 		= null;

	/** Holds the up arrow icon.*/
	private JLabel	up 				= null;

	/** holds the down arrow item.*/
	private JLabel	down 			= null;

	/** The list that is the scrollable section of the menu.*/
	private JList 	list 			= null;

	/** The viewport of the scrollable section of the menu.*/
	private JViewport view 			= null;

	/** The scollpane for the menu.*/
	private JScrollPane	scrollpane	= null;

	/** Used for autoscrolling when dragged nodes hit the viewport edge.*/
	//private 	java.util.Timer 					timer = null;
	

	/**
	 * Constructor. Initializes the menu.
	 */
	public UIScrollableMenu () {
		super();
		init();
	}

    /**
     * Constructs a new <code>UIScrollableMenu</code> with the supplied string as its text.
     *
     * @param s the text for the menu label.
     * @param b can the menu be torn off (not yet implemented).
     */
	public UIScrollableMenu(String s, boolean tearOff) {
		this(s, 0, MENU_LENGTH, tearOff);
	}

    /**
     * Constructs a new <code>UIScrollableMenu</code> with the supplied string as its text.
     *
     * @param s the text for the menu label.
     * @param scrollStart, the position to start the scroll from.
     */
	public UIScrollableMenu(String s, int scrollStart) {
		this(s, scrollStart, MENU_LENGTH, false);
	}

    /**
     * Constructs a new <code>UIScrollableMenu</code> with the supplied string as its text.
     *
     * @param s the text for the menu label.
     * @param scrollStart, the position to start the scroll from.
     * @param b can the menu be torn off (not yet implemented).
     */
	public UIScrollableMenu(String s, int scrollStart, boolean tearOff) {
		this(s, scrollStart, MENU_LENGTH, tearOff);
	}

    /**
     * Constructs a new <code>UIScrollableMenu</code> with the supplied string as its text.
     *
     * @param s the text for the menu label.
     * @param scrollStart, the position to start the scroll from.
     * @param menuLength, the lenght of the visible menu before you start using scrolling.
     */
	public UIScrollableMenu(String s, int scrollStart, int menuLength) {
		this(s, scrollStart, menuLength, false);
	}

    /**
     * Constructs a new <code>UIScrollableMenu</code> with the supplied string as its text
     *
     * @param s the text for the menu label.
     * @param scrollStart, the position to start the scroll from.
     * @param menuLength, the lenght of the visible menu before you start using scrolling.
     * @param b can the menu be torn off (not yet implemented)
     */
	public UIScrollableMenu(String s, int scrollStart, int menuLength, boolean tearOff) {
		super(s, tearOff);

		if (menuLength > 2) {
			this.menuLength = menuLength;
		}

		if (scrollStart > 0 && scrollStart < menuLength)
			this.scrollStart = scrollStart;

		init();
	}

    /**
     * Constructs a menu whose properties are taken from the <code>Action</code> supplied.
     * @param a an <code>Action</code>
     */
	public UIScrollableMenu(Action a) {
		super(a);
		init();
	}

	/**
	 * Initialize and draw the Menu.
	 */
	private void init() {

		menuitems = new Vector(20);
		listitems = new Vector(20);
		vtSeparators = new Vector();
		
		this.setDoubleBuffered(true);
		
		JMenuItem test = new JMenuItem();

		upArrow = new JPanel(new BorderLayout());
		upArrow.setBorder(new EmptyBorder(4,4,4,4));
		upArrow.setBackground(test.getBackground());		
		up = new JLabel(UIImages.get(IUIConstants.UP_ARROW_ICON));
		up.addMouseListener(createMouseAdapter());
		up.setHorizontalAlignment(SwingConstants.CENTER);
		up.setEnabled(false);
		upArrow.add(up, BorderLayout.CENTER);

		downArrow = new JPanel(new BorderLayout());
		downArrow.setBorder(new EmptyBorder(4,4,4,4));		
		downArrow.setBackground(test.getBackground());
		down = new JLabel(UIImages.get(IUIConstants.DOWN_ARROW_ICON));
		down.addMouseListener(createMouseAdapter());
		down.setHorizontalAlignment(SwingConstants.CENTER);
		downArrow.add(down,BorderLayout.CENTER);

		list = new JList();
		list.setVisibleRowCount(menuLength);
		list.setBackground(test.getBackground());
		list.setCellRenderer(new ScrollListCellRenderer());
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	    ToolTipManager.sharedInstance().registerComponent(list);

		list.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent evt) {
				JList lst = (JList)evt.getSource();
				JMenuItem item = (JMenuItem)lst.getSelectedValue();
				if (item != null) {
					item.doClick();
					setPopupMenuVisible(false);
				}
			}

			public void mouseExited(MouseEvent evt) {
				list.clearSelection();
			}
		});

		list.addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseMoved(MouseEvent evt) {
				final MouseEvent event = evt;
				Thread thread = new Thread("UIScrollableMenu.list mouse event") { //$NON-NLS-1$
					public void run() {
						JList lst = (JList)event.getSource();
						int x = event.getX();
						int y = event.getY();
						int index = lst.locationToIndex(new Point(x, y));
						list.setSelectedIndex(index);
					}
				};
				thread.start();
			}
		});

		view = new JViewport();
		view.setView(list);

		scrollpane = new JScrollPane();
		scrollpane.setViewport(view);
		scrollpane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
		scrollpane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollpane.setBorder(null);
	}

	/**
	 * Create and return the <code>MouseListener</code> for this menu.
	 * This controls the scrolling.
	 * @return MouseListener, the <code>MouseListener</code> for this menu.
	 */
	private MouseListener createMouseAdapter() {

		MouseListener mouseAdapter = new MouseAdapter() {

			public void mouseClicked(MouseEvent evt) {
				JLabel item = (JLabel)evt.getSource();
				if (item.equals(up)) {
					scrollUp();
				} else {
					scrollDown();
				}
			}

			// CAUSED FLICKER IN MAPS AFTER MENU CLOSED
			/*public void mouseEntered(MouseEvent evt) {
				JLabel item = (JLabel)evt.getSource();
				timer = new java.util.Timer();
				ScrollMenu task = new ScrollMenu(item.equals(up));
				timer.schedule(task, new Date(), scrollDelay);
			}

			public void mouseExited(MouseEvent evt) {
				if (timer != null) {
					timer.cancel();
				}
			}*/
		};
		return mouseAdapter;
	}

	/**
	 * This inner class is used to perform the autoscrolling of nodes when they hit the edge of the view.
	 */
	private class ScrollMenu extends TimerTask {

		boolean scrollUp = false;
		
		public ScrollMenu(boolean scroll) {			
			scrollUp = scroll;
		}

		public void run() {
			if (scrollUp) {
				scrollUp();
			} else { 
				scrollDown();
			}			
		}
	}
	
	
	private void scrollDown() {
		try {
			int	top = list.getFirstVisibleIndex();
			int	bottom = list.getLastVisibleIndex();
			int items = listitems.size()-1;

			// IF WE ARE SCROLLING DOWN FROM 0, ENABLE UP ARROW
			if (top == 0) {
				up.setEnabled(true);
			}
			// IF WE ARE DRAWING THE BOTTOM ITEM, DISABLE DOWN ARROW
			if (bottom == items) {
				down.setEnabled(false);
			}

			if (bottom < items) {
				try {
					list.ensureIndexIsVisible(bottom+1);
					list.repaint();
				} catch(Exception e) {}										
			}
		}
		catch(Exception io) {
			io.printStackTrace();
		}			
	}
	
	private void scrollUp() {
		try {
			int	top = list.getFirstVisibleIndex();
			int	bottom = list.getLastVisibleIndex();
			int items = listitems.size()-1;

			top = list.getFirstVisibleIndex();
			bottom = list.getLastVisibleIndex();

			// IF WE ARE SCROLLING UP FROM BOTTOM, ENABLE DOWN ARROW
			if (!down.isEnabled() && bottom < items) {
				down.setEnabled(true);
			}

			// IF WE ARE DRAWING THE TOPITEM, DISABLE UP ARROW
			if (top == 1 && up.isEnabled()) {
				up.setEnabled(false);
			}

			if (top > 0) {
				try {
					list.ensureIndexIsVisible(top-1);
					list.repaint();
				} catch(Exception e) {}
			}
		}
		catch(Exception io) {
			io.printStackTrace();
		}			
	}
	
	/*
	 * Set the time delay by which the scroll action waits before scrolling to the next menu item
	 * int delay, the delay between scrolling between menu items, in milliseconds
	 */
	public void setScrollDelay(int delay) {
		scrollDelay =delay;
	}


// ADD METHODS

    /**
     * Appends a new separator to the end of the menu.
     */
	public void addSeparator() {
		
		JSeparator sep = new JSeparator();

		if (menuitems.size() < scrollStart) {
			super.add(sep);
			menuitems.addElement(sep);
		}
		else {
			listitems.addElement(sep);
			vtSeparators.add(sep);

			if (!listAdded) {
				super.add(upArrow);
				super.add(scrollpane);
				super.add(downArrow);
				listAdded = true;
			}
		}
	}

    /**
     * Appends a menu item to the end of this menu.
     * Returns the menu item added.
	 *
     * @param menuItem the <code>JMenuitem</code> to be added
     * @return the <code>JMenuItem</code> added
     */
	public JMenuItem add(JMenuItem menuItem) {

		if (menuitems.size() < scrollStart) {
			super.add(menuItem);
			menuitems.addElement(menuItem);
		}
		else {
			listitems.addElement(menuItem);

			if (!listAdded) {
				super.add(upArrow);
				super.add(scrollpane);
				super.add(downArrow);
				listAdded = true;
			}
		}

		return menuItem;
	}

    /**
     * Creates a new menu item attached to the specified
     * <code>Action</code> object and appends it to the end of this menu.
     * As of 1.3, this is no longer the preferred method for adding
     * <code>Actions</code> to
     * a container. Instead it is recommended to configure a control with
     * an action using <code>setAction</code>,
     * and then add that control directly
     * to the <code>Container</code>.
     *
     * @param a the <code>Action</code> for the menu item to be added
     * @see Action
     */
	public JMenuItem add(Action a) {
		JMenuItem mi = createActionComponent(a);
        mi.setAction(a);
        add(mi);

		return mi;
	}

    /**
     * Creates a new menu item with the specified text and appends
     * it to the end of this menu.
     *
     * @param s the string for the menu item to be added
     */
	public JMenuItem add(String s) {
		return add(new JMenuItem(s));
	}

    /**
     * Adds the specified component to this container at the given
     * position. If <code>index</code> equals -1, the component will
     * be appended to the end.
     * @param     c   the <code>Component</code> to add
     * @param     index    the position at which to insert the component
     * @return    the <code>Component</code> added
     * @see	  #remove
     * @see java.awt.Container#add(Component, int)
     */
	public Component add(Component c, int index) throws IllegalArgumentException {
        if (index < 0) {
            throw new IllegalArgumentException(LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "UIScrollableMenu.indexLessThanZero")); //$NON-NLS-1$
        }

		if (index < scrollStart) {
			super.add(c, index);
			menuitems.insertElementAt(c, index);

			// IF THIS ITEM GOES BEYOND THE SCROLL START POSITION, MOVE THE EXTRA ITEMS
			if (menuitems.size() >= scrollStart) {
				int insertIndex = 0;
				for (int i=scrollStart-1; i<menuitems.size(); i++) {
					listitems.insertElementAt(menuitems.elementAt(i), insertIndex);
					insertIndex++;
				}
				for (int j=scrollStart-1; j<menuitems.size(); j++) {
					menuitems.removeElementAt(j);
					j--;
				}
			}
		}
		else {
			listitems.insertElementAt(c, index);

			if (!listAdded) {
				super.add(upArrow);
				super.add(scrollpane);
				super.add(downArrow);
				listAdded = true;
			}
		}

		return c;
	}

    /**
     * Appends a component to the end of this menu.
     * Returns the component added.
     *
     * @param c the <code>Component</code> to add
     * @return the <code>Component</code> added
     */
	public Component add(Component c) {

		if (menuitems.size() < scrollStart) {
			super.add(c);
			menuitems.addElement(c);
		}
		else {
			listitems.addElement(c);

			if (!listAdded) {
				super.add(upArrow);
				super.add(scrollpane);
				super.add(downArrow);
				listAdded = true;
			}
		}

		return c;
	}

// INSERT METHODS

    /**
     * Inserts a new menu item with the specified text at a
     * given position.
     *
     * @param s the text for the menu item to add
     * @param pos an integer specifying the position at which to add the
     *               new menu item
     * @exception IllegalArgumentException if the value of
     *			<code>pos</code> < 0
     */
    public void insert(String s, int pos) throws IllegalArgumentException {
    	add(new JMenuItem(s), pos);
    }

    /**
     * Inserts the specified <code>JMenuitem</code> at a given position.
     *
     * @param mi the <code>JMenuitem</code> to add
     * @param pos an integer specifying the position at which to add the
     *               new <code>JMenuitem</code>
      * @exception IllegalArgumentException if the value of
     *			<code>pos</code> < 0
    * @return the new menu item
     */
    public JMenuItem insert(JMenuItem mi, int pos) throws IllegalArgumentException {
    	return (JMenuItem)add(mi, pos);
    }

    /**
     * Inserts a new menu item attached to the specified <code>Action</code>
     * object at a given position.
     *
     * @param a the <code>Action</code> object for the menu item to add
     * @param pos an integer specifying the position at which to add the
     *               new menu item
     * @exception IllegalArgumentException if the value of
     *			<code>pos</code> < 0
     */
    public JMenuItem insert(Action a, int pos) throws IllegalArgumentException {
    	
		JMenuItem mi = createActionComponent(a);
        mi.setAction(a);
        return (JMenuItem)add(mi, pos);
    }

    /**
     * Inserts a separator at the specified position.
     *
     * @param       pos an integer specifying the position at which to
     *                    insert the menu separator
     * @exception   IllegalArgumentException if the value of
     *                       <code>pos</code> < 0
     */
    public void insertSeparator(int pos) throws IllegalArgumentException {    	
    	JSeparator sep = new JSeparator();
    	add(sep, pos);
    }


// REMOVE METHODS

    /**
     * Removes the specified menu item from this menu.  If there is no
     * popup menu, this method will have no effect.
     *
     * @param    item the <code>JMenuItem</code> to be removed from the menu
     */
	public void remove(JMenuItem item) {

		int count = 0;

		if (item != null) {
			if (!listitems.remove(item)) {
				menuitems.remove(item);
				super.remove(item);
			}
		}
	}

    /**
     * Removes the menu item at the specified index from this menu.
     *
     * @param       pos the position of the item to be removed
     * @exception   IllegalArgumentException if the value of
     *                       <code>pos</code> < 0, or if <code>pos</code>
     *			     is greater than the number of menu items
     */
	public void remove(int pos) {

		if (pos < scrollStart) {
			if (pos < menuitems.size() && pos >= 0) {
				menuitems.removeElementAt(pos);
				super.remove(pos);
			}
		}
		else {
			pos = pos-scrollStart;
			if (pos < listitems.size() && pos >= 0) {
				listitems.removeElementAt(pos);
			}
		}
	}

    /**
     * Removes the component <code>c</code> from this menu.
     *
     * @param       c the component to be removed
     */
	public void remove(Component c) {
		int count = 0;

		if (!listitems.remove(c)) {
			menuitems.remove(c);
			super.remove(c);

			//ADD IN LOWER ITEM TO MAKE COUNT BACK UP TO SCROLLSTART
		} else {
			vtSeparators.remove(c);
		}
	}

    /**
     * Removes all menu items from this menu.
     */
	public void removeAll() {
		super.removeAll();
		menuitems.removeAllElements();
		listitems.removeAllElements();
		vtSeparators.removeAllElements();
		listAdded = false;
	}

// OTHER METHODS

    /**
     * Returns the number of components on the menu.
     *
     * @return an integer containing the number of components on the menu
     */
    public int getMenuComponentCount() {
        int componentCount = 0;
		componentCount = listitems.size() + menuitems.size();
        return componentCount;
    }

    /**
     * Returns the component at position <code>n</code>.
     *
     * @param n the position of the component to be returned
     * @return the component requested, or <code>null</code>
     *			if there is no popup menu
     *
     */
    public Component getMenuComponent(int n) {

		if (n < scrollStart)
			return (Component) menuitems.elementAt(n);
		else
			return (Component) listitems.elementAt(n-scrollStart);
    }

    /**
     * Returns an array of <code>Component</code>s of the menu's
     * subcomponents.  Note that this returns all <code>Component</code>s
     * in the popup menu, including separators.
     *
     * @return an array of <code>Component</code>s or an empty array
     *		if there is no popup menu
     */
    public Component[] getMenuComponents() {

		Component[] comps = new Component[menuitems.size()+listitems.size()];

		int i=0;
		for (i=0; i<menuitems.size(); i++)
			comps[i] = (Component)menuitems.elementAt(i);

		for (i=0; i<listitems.size(); i++)
			comps[i+scrollStart] = (Component)listitems.elementAt(i);

        return comps;
    }

    /**
     * Returns true if the specified component exists in the
     * submenu hierarchy.
     *
     * @param c the <code>Component</code> to be tested
     * @return true if the <code>Component</code> exists, false otherwise
     */
    public boolean isMenuComponent(Component c) {

		if (!super.isMenuComponent(c)) {

	        // Are we in the scrolllist
			int count = listitems.size();
	        for (int i = 0 ; i < count; i++) {
	            Component comp = (Component)listitems.elementAt(i);

	            if (comp == c)
	                return true;

	            if (comp instanceof JMenu) {
	                JMenu subMenu = (JMenu) comp;
	                if (subMenu.isMenuComponent(c))
	                    return true;
	            }
	        }
		}
	    return false;
    }

    /**
	 * CURRENTLY DOES NOTHING.
     */
    public void applyComponentOrientation(ComponentOrientation o) {
        //super.applyComponentOrientation(o);

        //if ( popupMenu != null ) {
        //   int ncomponents = getMenuComponentCount();
        //    for (int i = 0 ; i < ncomponents ; ++i) {
        //        getMenuComponent(i).applyComponentOrientation(o);
        //    }
        //    popupMenu.setComponentOrientation(o);
        //}
    }

    /**
	 * CURRENTLY DOES NOTHING.
     */
    public void setComponentOrientation(ComponentOrientation o) {
        //super.setComponentOrientation(o);
        //if ( popupMenu != null ) {
        //    popupMenu.setComponentOrientation(o);
        //}
    }


    /**
     * Sets the visibility of the menu's popup.  If the menu is
     * not enabled, this method will have no effect.
     *
     * @param vis,  a boolean value -- true to make the menu visible, false to hide it.
     */
	public void setPopupMenuVisible(boolean vis) {
		if (!vis) {
			list.ensureIndexIsVisible(0);
			up.setEnabled(false);
			down.setEnabled(true);
			//if (timer != null) {
			//	timer.cancel();
			//}			
		}
		else {
			// DON'T NEED THE SCROLL ARROWS IF MENU NOT LONG ENOUGH
			if (listitems.size() <= menuLength) {
				super.remove(upArrow);
				super.remove(downArrow);
				list.setVisibleRowCount(listitems.size() - vtSeparators.size());
			}

			list.setListData(listitems);
			list.clearSelection();
			list.validate();
			Dimension dim = list.getPreferredScrollableViewportSize();
			view.setExtentSize(dim);
		}

		getPopupMenu().pack();
		super.setPopupMenuVisible(vis);
	}


	/**
	 * This class implements the list renderer for the scrollable list part of the menu.
	 */
	private class ScrollListCellRenderer implements ListCellRenderer {

		/** For getting the background colour.*/
		private JMenuItem test = new JMenuItem();

		public ScrollListCellRenderer() {}

		public Component getListCellRendererComponent(JList list,
													Object value,            // value to display
													int index,               // cell index
													boolean isSelected,      // is the cell selected
													boolean cellHasFocus ) { // the list and the cell have the focus

			FlowLayout flow = new FlowLayout(FlowLayout.LEFT);
			flow.setHgap(0);
			flow.setVgap(0);
			JPanel panel = new JPanel(flow);

			if (value instanceof JMenuItem) {
				JMenuItem item = (JMenuItem)value;
				item.setSelected(isSelected);

				if (isSelected) {
					item.setForeground(list.getSelectionForeground());
					item.setBackground(list.getSelectionBackground());
					panel.setBackground(list.getSelectionBackground());
				}
				else {
					item.setForeground(test.getForeground());
					item.setBackground(test.getBackground());
					panel.setBackground(test.getBackground());
				}
				panel.add(item);
				panel.setToolTipText(item.getToolTipText());
			} else if (value instanceof JSeparator) {
				JSeparator sep = (JSeparator)value;
		    	panel.add(sep);
			} else {
				JLabel label = new JLabel();
				label.setText((value == null) ? "" : value.toString()); //$NON-NLS-1$
				panel.add(label);
			}

			panel.setBorder(null);
		    //ToolTipManager.sharedInstance().registerComponent(panel);			

			return panel;
		}
	}
}
