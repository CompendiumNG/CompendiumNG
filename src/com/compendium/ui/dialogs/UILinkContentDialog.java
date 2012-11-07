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

package com.compendium.ui.dialogs;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.*;

import com.compendium.core.datamodel.Link;
import com.compendium.*;
import com.compendium.ui.*;
import com.compendium.ui.panels.*;

/**
 * This dialog allows users to view and edit a links properties.
 *
 * @author	Michelle Bachler
 */
public class UILinkContentDialog extends UIDialog {

	/** Represents the content tab panel tab.*/
	public final static int CONTENTS_TAB		= 0;

	/** Represents the content properties panel tab.*/
	public final static int PROPERTIES_TAB		= 1;

	/** Represents the content parent views panel tab.*/
	public final static int VIEW_TAB			= 2;

	/** The pane for this dialog's content.*/
	private Container		oContentPane		= null;

	/** The UILink reference for the link to change setting for.*/
	public UILink			oUILink				= null;

	/** The Link object to chagen setting for.*/
	public Link				oLink				= null;

	/** The tabbedpane in this dialog.*/
	private JTabbedPane		TabbedPane			= null;

	/** The UILinkEditPanel for the contents tab.*/
	private UILinkEditPanel	oLinkEditPane		= null;

	/** The UILinkPropertiesPanel for the properties tab.*/
	private	UILinkPropertiesPanel	oLinkPropertiesPane 	= null;

	/** The UINodeViewPanel for the parent views tab.*/
	//private	UINodeViewPanel	oSelectViewPane 	= null;

	/** Indicates the currently selected tab.*/
	private int 			nSelectedTab 		= 0;

	/**
	 * Constructor.
	 * @param parent, the parent frame for this dialog.
	 * @param uilink com.compendium.ui.UILink, the link to assign settings to.
	 */
	public UILinkContentDialog(JFrame parent, UILink uilink, int selectedTab) {

		super(parent, true);

		nSelectedTab = selectedTab;

		initDialog(uilink);

		//setResizable(false);
	}

	/**
	 * Initialize the dialog and its contents.
	 *
	 * @param link com.compendium.core.datamodel.Link, the link to display the contents for.
	 * @param selectedTab, the tabbed panel to initially select when opening this dialog.
	 */
	public void initDialog(UILink uilink) {

		setTitle(uilink.getText());

		oUILink = uilink;
		oLink = uilink.getLink();

		oContentPane = getContentPane();
		oContentPane.setLayout(new BorderLayout());

		TabbedPane = new JTabbedPane();

		oLinkEditPane = new UILinkEditPanel(oUILink, this);
		oLinkPropertiesPane = new UILinkPropertiesPanel(oUILink, this);

		//oSelectViewPane = new UINodeViewPanel(ProjectCompendium.APP, oUINode, this);

		TabbedPane.add(oLinkEditPane, LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UILinkContentDialog.contents")); //$NON-NLS-1$
		TabbedPane.add(oLinkPropertiesPane, LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UILinkContentDialog.properties")); //$NON-NLS-1$
		//TabbedPane.add(oSelectViewPane, "Views");

		oLinkEditPane.setDefaultButton();

		TabbedPane.addChangeListener( new ChangeListener() {
        	public void stateChanged(ChangeEvent e) {
				int nIndex = TabbedPane.getSelectedIndex();
				if (nIndex == CONTENTS_TAB) {
					oLinkEditPane.setDefaultButton();
				}
				else if (nIndex == PROPERTIES_TAB) {
					oLinkPropertiesPane.setDefaultButton();
				}
			}
		});

		TabbedPane.setSelectedIndex(nSelectedTab);

		oContentPane.add(TabbedPane, BorderLayout.CENTER);

		pack();
		final Dimension size = getSize();

		addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent evt) {

				Dimension dim = getSize();

				//revert to optmized size if dialog is made smaller than the min opt size
				if((dim.height < size.height) || (dim.width < size.width))
					setSize(size);
				else {
					oLinkEditPane.revalidate();
				}
			}
		});
	}
}
