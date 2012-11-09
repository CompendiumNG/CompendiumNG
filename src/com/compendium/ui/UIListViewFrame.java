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

import java.beans.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.dnd.*;

import javax.swing.*;
import javax.swing.plaf.*;
import javax.help.*;

import com.compendium.ui.plaf.*;
import com.compendium.LanguageProperties;
import com.compendium.ProjectCompendium;
import com.compendium.core.datamodel.*;

/**
 * Has additional methods specifically for List Frames.
 *
 * @author	Mohammed Sajid Ali / Michelle Bachler
 */
public class UIListViewFrame extends UIViewFrame {

	/** The JTable instance that holds the list of nodes associated with this frame.*/
	protected JTable 				list;

	/** The UIList associated with this list frame.*/
	protected UIList 				uiList;

	/** The button to open and close additional columns of detail.*/
	protected JButton 				hide 				= null;

	/** This label holds a count of all item in the list.*/
	protected JLabel				label				= null;

	/** The base of the current title for this frame.*/
	private String 				sBaseTitle = new String("[List]: "); //$NON-NLS-1$

	/**
	 * Constructor. Create a new instance of this class.
	 * @param view com.compendium.core.datamodel.View, the view associated with this frame.
	 */
	public UIListViewFrame (View view) {
		this(view, UIViewFrame.getViewLabel(view));
	}

	/**
	 * Constructor. Create a new instance of this class.
	 * @param view com.compendium.core.datamodel.View, the view associated with this frame.
	 * @param title, the title for this frame.
	 */
	public UIListViewFrame (View view, String title) {
		super(view, title);
		setTitle(title);		
		init(view);
		
	}

	/**
	 * Override to add a default title stub to start of title.
	 *
	 * @param sTitle, the title to add.
	 */
	public void setTitle(String sTitle) {
		super.setTitle(sBaseTitle+sTitle);
	}

	/**
	 * Initialize and draw this frame.
	 * @param view com.compendium.core.datamodel.View, the view associated with this frame.
	 */
	private void init(View view) {

		oContentPane.setLayout(new BorderLayout());
		oContentPane.addMouseListener( new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				boolean isLeftMouse = SwingUtilities.isLeftMouseButton(e);
				boolean isRightMouse = SwingUtilities.isRightMouseButton(e);
				if(isRightMouse) {
					if(e.getClickCount() == 1) {
						uiList.showPopupMenuForList(e.getX(),e.getY());
					}
				}
				else {
					// never actually reached as first mouse click open right-click menu
					// and then second click sent to it.
					if (e.getClickCount() == 2) {
						showEditDialog();
					}
				}
			}
		});

		list = createList(view);

		updateFrameIcon();
		scrollpane = new JScrollPane((JTable)list);
		DropTarget dropTarget = new DropTarget((Component)scrollpane, uiList);

		oViewport = scrollpane.getViewport();
		list.setVisible(true);
		oContentPane.add(scrollpane,BorderLayout.CENTER);

		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());

		hide = new UIButton(LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "UIListViewFrame.showMoreButton")); //$NON-NLS-1$
		hide.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				uiList.hideHint();
				if (uiList.isSmall()) {
					hide.setText(LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "UIListViewFrame.showLessButton")); //$NON-NLS-1$
					uiList.setSize("large"); //$NON-NLS-1$
					scrollpane.revalidate();
				}
				else {
					hide.setText(LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "UIListViewFrame.showMoreButton")); //$NON-NLS-1$
					uiList.setSize("small"); //$NON-NLS-1$
					scrollpane.revalidate();
				}
			}
		});
		
		label = new JLabel(""); //$NON-NLS-1$
		updateCountLabel();
		label.setFont( new Font("Dialog", Font.PLAIN, 12) ); //$NON-NLS-1$
		label.setHorizontalAlignment(SwingConstants.LEFT);

		JPanel hpanel = new JPanel();
		hpanel.add(hide);

		panel.add(label, BorderLayout.WEST);
		panel.add(hpanel, BorderLayout.CENTER);

		oContentPane.add(panel, BorderLayout.SOUTH);

		horizontalBar = scrollpane.getHorizontalScrollBar();
		verticalBar = scrollpane.getVerticalScrollBar();

		CSH.setHelpIDString(this,"node.views"); //$NON-NLS-1$
		oView = view;

		addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				uiList.hideHint();
			}
			
			public void mouseExited(MouseEvent e) {
				uiList.hideHint();
			}			
		});
		
		this.setVisible(true);
	}
	
	/**
	 * Update the count of nodes in this list displayed.
	 */
	public void updateCountLabel() {
		label.setText(" "+LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "UIListViewFrame.itemCount")+": "+list.getRowCount()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	/**
	 * Toggle the extra details columns on and off (hide/show).
	 */
	public void hideShowDetail() {

		if (uiList != null) {
			if (uiList.isSmall()) {
				hide.setText(LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "UIListViewFrame.showMoreButton")); //$NON-NLS-1$
				uiList.setSize("large"); //$NON-NLS-1$
				scrollpane.revalidate();
			}
			else {
				hide.setText(LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "UIListViewFrame.showLessButton")); //$NON-NLS-1$
				uiList.setSize("small"); //$NON-NLS-1$
				scrollpane.revalidate();
			}
		}
	}

	/**
	 * Return the UIList instance associated with this frame.
	 * @param com.compendium.ui.UIList, the UIList instance associated with this frame.
	 */
	public UIList getUIList() {
		return uiList;
	}

	/**
	 * Create the UIList instance associated with this view.
	 * @param view com.compendium.core.datamodel.View, the view for the UIList instance.
	 * @return JTable, the JTable from the UIList instance.
	 */
	public JTable createList(View view) {
		oView = view;
		uiList = new UIList(view, this);
		return uiList.getList();
	}

	/**
	 * Delete the children in the given view.
	 * @param childView com.compendium.core.datamodel.View, the view to delet the children for.
	 */
	public void deleteChildren(View childView) {

		UIList childUIList = getUIList();

		// BUG WITH SELECT ALL AND JTable.boundRow function which sometimes throws
		// java.lang.IllegalArgumentException: Row index out of range
		try {
			childUIList.getList().selectAll();
		}
		catch(Exception ex){
			return;
		}

		for (Enumeration e = childUIList.getSelectedNodes(); e.hasMoreElements();) {
			childView.addDeletedNode((NodePosition)e.nextElement());
		}
		childUIList.getListUI().onDelete();
	}

	/**
	 * Set the current frame selected/deselected and if selected, focus the list.
	 * @param selected, true if the frame should be selected, else false.
	 */
	public void setSelected(boolean selected) {
		boolean wasSelected = isSelected();
		try {
			super.setSelected(selected);
		}
		catch (Exception e) {
			System.out.println("viewframe not selected because "+e.getMessage()); //$NON-NLS-1$
		}

		if (isSelected() && !wasSelected) {
			list.requestFocus();

			// refresh edit-undo and edit-redo button for this Frame
			refreshUndoRedo();
		}
	}

	/**
	 * Null out class variables
	 */
	public void cleanUp() {

		super.cleanUp();

	}
}
