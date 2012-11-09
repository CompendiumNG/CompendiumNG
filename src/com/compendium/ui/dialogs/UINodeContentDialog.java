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

package com.compendium.ui.dialogs;


import java.awt.*;
import java.awt.Container;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.*;

import com.compendium.ProjectCompendium;
import com.compendium.core.datamodel.*;

import com.compendium.ui.*;
import com.compendium.ui.panels.UINodeEditPanel;
import com.compendium.ui.panels.UINodePropertiesPanel;
import com.compendium.ui.panels.UINodeViewPanel;


/**
 * This dialog displays a tabbedpane holding various panels with Node information.
 *
 * @author	Beatrix Zimmermann / Michelle Bachler
 */
public class UINodeContentDialog extends UIDialog {

	/** The default serial version ID for this class	 */
	private static final long serialVersionUID = 1L;

	/** Represents the content tab panel tab.*/
	public final static int CONTENTS_TAB					= 0;

	/** Represents the content properties panel tab.*/
	public final static int PROPERTIES_TAB					= 1;

	/** Represents the content parent views panel tab.*/
	public final static int VIEW_TAB						= 2;
		
	/** The pane to add the contents for the dialog to.*/
	private Container		oContentPane					= null;

	/** The UINode that this dialog is showing the contents for, is in a map.*/
	private UINode			oUINode							= null;

	/** The NodeSummary object that this is the contents dialog for.*/
	private NodeSummary		oNode							= null;

	/** The NodePosition object that this is the contents dialog for.*/
	private NodePosition	oNodePosition					= null;

	/** The current view.*/
	private View			oView							= null;

	/** The tabbedpane in this dialog.*/
	private JTabbedPane				TabbedPane				= null;

	/** The UINodeEditPanel for the contents tab.*/
	private UINodeEditPanel			oNodeEditPane			= null;

	/** The UINodePropertiesPanel for the properties tab.*/
	private	UINodePropertiesPanel	oNodePropertiesPane 	= null ;

	/** The UINodeViewPanel for the parent views tab.*/
	private	UINodeViewPanel			oSelectViewPane 		= null;

	/** Indicates the currently selected tab.*/
	private int 			nSelectedTab 					= 0;

	/** Indicates if this is the first time the dialog has recieved the focus.*/
	private boolean 		firstFocus 						= true;

	/** The parent JFrame of JDialog to this dialog.*/
	private Window			oParent							= null;


	/**
	 * Constructor. Initialized this dialog. Used by UINode
	 *
	 * @param parent, the parent frame for this dialog.
	 * @param view com.compendium.core.datamodel.View, the current view.
	 * @param uinode com.compendium.ui.UINode, the node to display the contents for, (if in a map).
	 * @param selectedTab, the tabbed panel to initially select when opening this dialog.
	 */
	public UINodeContentDialog(JFrame parent, View view, UINode uinode, int selectedTab) {
		// This has been made non model to enable tagging while it is open.
		super(parent, false);
		oParent = parent;
		oUINode = uinode;
		oNodePosition = oUINode.getNodePosition();
		oView = view;
		initDialog(uinode.getNode(), selectedTab);
	}

	/**
	 * Constructor. Initialized this dialog. Use by UIList
	 *
	 * @param parent the parent frame for this dialog.
	 * @param view the current view.
	 * @param node the node to display the contents for.
	 * @param selectedTab the tabbed panel to initially select when opening this dialog.
	 */
	public UINodeContentDialog(JFrame parent, View view, NodePosition node, int selectedTab) {
		// This has been made non model to enable tagging while it is open.		
		super(parent, false);
		oParent = parent;
		oView = view;
		oNodePosition = node;
		initDialog(node.getNode(), selectedTab);
	}

	/**
	 * Constructor. Initialized this dialog.
	 * This is used by the UISearchResultsDialog / UISelectViewDialog which does not have a specific view associated.
	 *
	 * @param parent the parent dialog for this dialog.
	 * @param node the node to display the contents for.
	 * @param selectedTab the tabbed panel to initially select when opening this dialog.
	 */
	public UINodeContentDialog(JDialog parent, NodeSummary node, int selectedTab) {
		super(parent, true);
		oParent = parent;
		initDialog(node, selectedTab);
	}

	/**
	 * Constructor. Initialized this dialog.
	 * This is used by the UIHintNodeLabelPanel which does not have a specific view associated,
	 * and passed the main panel as the parent. Also used by UIOutlineView.
	 *
	 * @param parent the parent frame for this dialog.
	 * @param node the node to display the contents for.
	 * @param selectedTab the tabbed panel to initially select when opening this dialog.
	 */
	public UINodeContentDialog(JFrame parent, NodeSummary node, int selectedTab) {
		super(parent, true);
		oParent = parent;
		initDialog(node, selectedTab);
	}

	/**
	 * Constructor. Initialized this dialog.
	 * Used for opening the current views contents dialog from inside the view.
	 *
	 * @param parent the parent frame for this dialog.
	 * @param view the current view.
	 * @param node the node to display the contents for.
	 * @param selectedTab the tabbed panel to initially select when opening this dialog.
	 */
	public UINodeContentDialog(JFrame parent, View view, NodeSummary node, int selectedTab) {
		super(parent, true);
		oParent = parent;
		oView = view;
		initDialog(node, selectedTab);
	}

	/**
	 * Initialize the dialog and its contents.
	 *
	 * @param node com.compendium.core.datamodel.NodeSummary, the node to display the contents for.
	 * @param selectedTab the tabbed panel to initially select when opening this dialog.
	 */
	public void initDialog(NodeSummary node, int selectedTab) {

		nSelectedTab = selectedTab;

		oNode = node;

		setTitle(oNode.getLabel());

		oContentPane = getContentPane();
		oContentPane.setLayout(new BorderLayout());

		TabbedPane = new JTabbedPane();

		if (oUINode != null)
			oNodeEditPane = new UINodeEditPanel(ProjectCompendium.APP, oUINode, this);
		else
			oNodeEditPane = new UINodeEditPanel(ProjectCompendium.APP, node, this);

		if (oNodePosition != null)
			oNodePropertiesPane = new UINodePropertiesPanel(ProjectCompendium.APP, oNodePosition, this);
		else
			oNodePropertiesPane = new UINodePropertiesPanel(ProjectCompendium.APP, node, this);

		if (oUINode != null)
			oSelectViewPane = new UINodeViewPanel(ProjectCompendium.APP, oUINode, this);
		else
			oSelectViewPane = new UINodeViewPanel(ProjectCompendium.APP, node, this);
		
		TabbedPane.add(oNodeEditPane, "Contents");
		TabbedPane.add(oNodePropertiesPane, "Properties");
		TabbedPane.add(oSelectViewPane, "Views");
		
		oNodeEditPane.setDefaultButton();

		oContentPane.add(TabbedPane, BorderLayout.CENTER);

		TabbedPane.setSelectedIndex(selectedTab);
		TabbedPane.addFocusListener( new FocusListener() {
        	public void focusGained(FocusEvent e) {
				if (firstFocus) {
					if (nSelectedTab == 0)
						oNodeEditPane.setDetailFieldFocused();
					firstFocus = false;
				}
			}
            public void focusLost(FocusEvent e) {

			}
		});

		final NodeSummary fNode = oNode;
		final int fTab = selectedTab;
		TabbedPane.addChangeListener( new ChangeListener() {
        	public void stateChanged(ChangeEvent e) {
				int nIndex = TabbedPane.getSelectedIndex();
				if (nIndex == CONTENTS_TAB) {
					oNodeEditPane.setDetailFieldFocused();
					oNodeEditPane.setDefaultButton();
				}
				else if (nIndex == PROPERTIES_TAB) {
					oNodePropertiesPane.setDefaultButton();
				}
				else if (nIndex == VIEW_TAB) {
					oSelectViewPane.setDefaultButton();
				}
			}
		});

		pack();
		final Dimension size = getSize();

		addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent evt) {

				Dimension dim = getSize();

				//revert to optmized size if dialog is made smaller than the min opt size
				if((dim.height < size.height) || (dim.width < size.width))
					setSize(size);
				else {
					oNodeEditPane.revalidate();
				}
			}
		});
	}

	/**
	 * Reset the font for presentation changes.
	 */
	public void refreshFont() {
		oNodeEditPane.refreshFont();
	}
	
	/**
	 * Return detail text area.
	 * @return JTextArea, the textarea of the UINodeEditPanel.
	 */
	public JTextArea getDetailField() {
		return oNodeEditPane.getDetailField();
	}

	/**
	 * Set the modification date and author displayed in the properties panel.
	 * @param newDate the last modification date for this node.
	 * @param sAuthor the author who made the modification.
	 */
	public void setModified(String newDate, String sAuthor) {
		oNodePropertiesPane.setModified(newDate, sAuthor);
	}

	/**
	 * Process the saving of any node contents/properties changes.
	 */
	public void onUpdate() {
		oNodeEditPane.onUpdate();
		oNodePropertiesPane.onUpdate();
	}
}
