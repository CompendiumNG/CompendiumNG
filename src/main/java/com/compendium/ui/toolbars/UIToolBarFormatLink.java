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

package com.compendium.ui.toolbars;

import java.awt.*;
import java.awt.event.*;
import java.util.Enumeration;
import java.util.Vector;
import javax.help.*;
import javax.swing.*;
import javax.swing.border.*;
import java.sql.SQLException;
import javax.swing.plaf.basic.BasicComboBoxEditor;
import javax.swing.border.Border;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;
import java.lang.reflect.Method;

import com.compendium.*;
import com.compendium.core.ICoreConstants;
import com.compendium.core.datamodel.LinkProperties;
import com.compendium.core.datamodel.NodePosition;
import com.compendium.core.datamodel.Model;
import com.compendium.core.datamodel.ModelSessionException;
import com.compendium.core.datamodel.services.ViewService;
import com.compendium.ui.*;
import com.compendium.ui.dialogs.*;
import com.compendium.ui.linkgroups.UILinkType;
import com.compendium.ui.toolbars.system.*;


/**
 * This class manages the Link Formatting toolbar
 *
 * @author	Michelle Bachler
 * @version	1.0
 */
public class UIToolBarFormatLink implements IUIToolBar, ActionListener, IUIConstants {
	
	/** Indicates whether the node format toolbar is switched on or not by default.*/
	private final static boolean DEFAULT_STATE			= true;
	
	/** Indicates the default orientation for this toolbars ui object.*/
	private final static int DEFAULT_ORIENTATION		= SwingConstants.HORIZONTAL;		
	
	/** This indicates the type of the toolbar.*/
	private	int 					nType			= -1;
		
	/** The parent frame for this class.*/
	private ProjectCompendiumFrame	oParent			= null;
	
	/** The overall toolbar manager.*/
	private IUIToolBarManager 		oManager		= null;

	/** The HelpSet to use for toolbar help.*/
	private HelpSet 				mainHS 			= null;

	/** The HelpBroker to use for toolbar help.*/
	private HelpBroker 				mainHB			= null;

	private UIToolBar			tbrToolBar 	= null;
 
	/** The label holding the icon to indicate link colour.*/
	private JLabel			txtLinkColour	= null;

	/** Opens the link colour chooser.*/
	private UIImageButton	btLinkColour = null;

	/** The JPanel to hold the link colour colour.*/
	private JPanel			linkColourPanel	= null;
	
	/** The JPanel to hold the link colour.*/
	private JPanel			linkPanel	= null;
	
	/** The referrence to the colour chooser dialog. */
	private UIColorChooserDialog oColorChooserDialog = null;
			
	/** Indicates that the node items are being displayed rather than changed by the user.*/
	private boolean				bJustSetting		= false;
	
	/** The link color of the selected links, used to set the colour chooser default when opening*/
	private Color				selectedLinkColour = Color.black;
	
	/** The choicebox listing the arrow head options.*/
	private JComboBox			cbArrows					= null;

	/** The choicebox with the link line thickness options.*/
	private JComboBox 			cbLineWeight				= null;

	/** The choicebox with the link style options.*/
	private JComboBox 			cbLinkStyle					= null;

	/** The choicebox with the link line style options.*/
	private JComboBox 			cbLinkDashed				= null;

	
	/**
	 * Create a new instance of UIToolBarFormatLink, with the given properties.
	 * @param oManager the IUIToolBarManager that is managing this toolbar.
	 * @param parent the parent frame for the application.
	 * @param nType the unique identifier for this toolbar.
	 */
	public UIToolBarFormatLink(IUIToolBarManager oManager, ProjectCompendiumFrame parent, int nType) {

		this.oParent = parent;
		this.oManager = oManager;
		this.nType = nType;
		
		createToolBar(DEFAULT_ORIENTATION);
	}

	/**
	 * Create a new instance of UIToolBarFormatLink, with the given properties.
	 * @param oManager the IUIToolBarManager that is managing this toolbar.
	 * @param parent the parent frame for the application.
	 * @param nType the unique identifier for this toolbar.
	 * @param orientation the orientation of this toolbars ui object.     
	 */
	public UIToolBarFormatLink(IUIToolBarManager oManager, ProjectCompendiumFrame parent, int nType, int orientation) {

		this.oParent = parent;
		this.oManager = oManager;
		this.nType = nType;
		
		createToolBar(orientation);
	}
		/**
	 * Create and return the toolbar with the node formatting options.
	 * @return UIToolBar, the toolbar with all the node formatting options.
	 */
	private UIToolBar createToolBar(int orientation) {
		
		tbrToolBar = new UIToolBar(LanguageProperties.getString(LanguageProperties.TOOLBARS_BUNDLE, "UIToolBarFormatLink.name"), UIToolBar.NORTHSOUTH); //$NON-NLS-1$
		tbrToolBar.setOrientation(orientation);
		tbrToolBar.setEnabled(false);
		CSH.setHelpIDString(tbrToolBar,"toolbars.formatlink"); //$NON-NLS-1$
						
		GridBagLayout grid= new GridBagLayout();		
		linkPanel = new JPanel(grid);	
		linkColourPanel = new JPanel(new BorderLayout());
		linkColourPanel.setBackground(Color.black);

		JLabel label = new JLabel(" "); //$NON-NLS-1$
		GridBagConstraints con5 = new GridBagConstraints();
		con5.fill = GridBagConstraints.NONE;
		con5.anchor = GridBagConstraints.CENTER;
		grid.addLayoutComponent(label, con5);		
		linkPanel.add(label);
		
		txtLinkColour = new JLabel(UIImages.get(BACKGROUND_COLOUR));
		txtLinkColour.setBorder(null);
		txtLinkColour.setToolTipText(LanguageProperties.getString(LanguageProperties.TOOLBARS_BUNDLE, "UIToolBarFormatLink.selectLinkColour")); //$NON-NLS-1$
		txtLinkColour.setEnabled(false);
		txtLinkColour.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				int clickCount = e.getClickCount();
				if (clickCount == 1 && txtLinkColour.isEnabled()) {
					onUpdateLinkColour(linkColourPanel.getBackground().getRGB());						
				}
			}
		});				
		linkColourPanel.add(txtLinkColour, BorderLayout.CENTER);
		
		GridBagConstraints con = new GridBagConstraints();
		con.fill = GridBagConstraints.NONE;
		con.anchor = GridBagConstraints.CENTER;
		grid.addLayoutComponent(linkColourPanel, con);		
		linkPanel.add(linkColourPanel);
		
		btLinkColour = new UIImageButton(UIImages.get(RIGHT_ARROW_ICON));
		btLinkColour.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				int clickCount = e.getClickCount();
				if (clickCount == 1 && txtLinkColour.isEnabled()) {
					if (oColorChooserDialog != null) {
						oColorChooserDialog.setColour(selectedLinkColour);
					} else {
						oColorChooserDialog = new UIColorChooserDialog(ProjectCompendium.APP, selectedLinkColour);
					}
					oColorChooserDialog.setVisible(true);
					Color oColour = oColorChooserDialog.getColour();
					oColorChooserDialog.setVisible(false);
					if (oColour != null) {
						linkColourPanel.setBackground(oColour);						
						onUpdateLinkColour(oColour.getRGB());						
					}
				}
			}
		});		
		linkPanel.add(btLinkColour);

		label = new JLabel(" "); //$NON-NLS-1$
		GridBagConstraints con4 = new GridBagConstraints();
		con4.fill = GridBagConstraints.NONE;
		con4.anchor = GridBagConstraints.CENTER;
		grid.addLayoutComponent(label, con4);		
		linkPanel.add(label);

		tbrToolBar.add(linkPanel);
		CSH.setHelpIDString(txtLinkColour,"toolbars.formatlink");		 //$NON-NLS-1$
		
		tbrToolBar.add( createWeightChoiceBox() );
		tbrToolBar.add( createArrowChoiceBox() );
		tbrToolBar.add( createLinkStyleChoiceBox() );
		tbrToolBar.add( createLinkDashedChoiceBox() );
		
		
		return tbrToolBar;
	}
	
	/**
	 * Create the arrow head choicebox.
	 */
	private JPanel createArrowChoiceBox() {
		
		JPanel drawPanel = new JPanel(new BorderLayout());
		CSH.setHelpIDString(drawPanel,"toolbars.formatlink"); //$NON-NLS-1$

		cbArrows = new JComboBox();
		cbArrows.setToolTipText(LanguageProperties.getString(LanguageProperties.TOOLBARS_BUNDLE, "UIToolBarFormatLink.selectArrow")); //$NON-NLS-1$
		cbArrows.setOpaque(true);
		cbArrows.setEditable(false);
		cbArrows.setEnabled(false);
		cbArrows.setMaximumRowCount(4);
		cbArrows.setFont( new Font("Dialog", Font.PLAIN, 10 )); //$NON-NLS-1$

		Vector arrows = new Vector(5);
		arrows.insertElementAt(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UILinkEditDialog.noArrows"), 0); //$NON-NLS-1$
		arrows.insertElementAt(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UILinkEditDialog.fromTo"), 1); //$NON-NLS-1$
		arrows.insertElementAt(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UILinkEditDialog.toFfrom"), 2); //$NON-NLS-1$
		arrows.insertElementAt(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UILinkEditDialog.bothWays"), 3); //$NON-NLS-1$
		DefaultComboBoxModel comboModel = new DefaultComboBoxModel(arrows);
		cbArrows.setModel(comboModel);
		cbArrows.setSelectedIndex(0);

		DefaultListCellRenderer comboRenderer = new DefaultListCellRenderer() {
			public Component getListCellRendererComponent(
   		     	JList list,
   		        Object value,
            	int modelIndex,
            	boolean isSelected,
            	boolean cellHasFocus)
            {
 		 		if (isSelected) {
					setBackground(list.getSelectionBackground());
					setForeground(list.getSelectionForeground());
				}
				else {
					setBackground(list.getBackground());
					setForeground(list.getForeground());
				}

				setText((String) value);

				return this;
			}
		};
		cbArrows.setRenderer(comboRenderer);

		cbArrows.addActionListener(new ActionListener() {
	       	public void actionPerformed(ActionEvent e) {
	       		onUpdateArrowType(cbArrows.getSelectedIndex());
			}	
		});
		
		drawPanel.add(new JLabel(" "), BorderLayout.WEST); //$NON-NLS-1$
		drawPanel.add(cbArrows, BorderLayout.CENTER);
		return drawPanel;
	}
	
	/**
	 * Create a choicbox for link line thickness options and return the panel it is in.
	 * @return JPanel the panel holding the new choicebox for the line thickness options.
	 */
	private JPanel createWeightChoiceBox() {

		JPanel drawPanel = new JPanel(new BorderLayout());
		CSH.setHelpIDString(drawPanel,"toolbars.formatlink"); //$NON-NLS-1$

		cbLineWeight = new JComboBox();
		cbLineWeight.setToolTipText(LanguageProperties.getString(LanguageProperties.TOOLBARS_BUNDLE, "UIToolBarFormatLink.selectWeight")); //$NON-NLS-1$
        cbLineWeight.setOpaque(true);
		cbLineWeight.setEditable(false);
		cbLineWeight.setEnabled(false);
		cbLineWeight.setMaximumRowCount(10);
		cbLineWeight.setFont( new Font("Dialog", Font.PLAIN, 10 )); //$NON-NLS-1$

		cbLineWeight.addItem(new String("1 px")); //$NON-NLS-1$
		cbLineWeight.addItem(new String("2 px")); //$NON-NLS-1$
		cbLineWeight.addItem(new String("3 px")); //$NON-NLS-1$
		cbLineWeight.addItem(new String("4 px")); //$NON-NLS-1$
		cbLineWeight.addItem(new String("5 px")); //$NON-NLS-1$
		cbLineWeight.addItem(new String("6 px")); //$NON-NLS-1$
		cbLineWeight.addItem(new String("7 px")); //$NON-NLS-1$
		cbLineWeight.addItem(new String("8 px")); //$NON-NLS-1$
		cbLineWeight.addItem(new String("9 px")); //$NON-NLS-1$
		cbLineWeight.addItem(new String("10 px")); //$NON-NLS-1$

		cbLineWeight.validate();

		cbLineWeight.setSelectedIndex(0);

		DefaultListCellRenderer drawRenderer = new DefaultListCellRenderer() {
			public Component getListCellRendererComponent(
   		     	JList list,
   		        Object value,
            	int modelIndex,
            	boolean isSelected,
            	boolean cellHasFocus)
            {
				if (list != null) {
	 		 		if (isSelected) {
						setBackground(list.getSelectionBackground());
						setForeground(list.getSelectionForeground());
					}
					else {
						setBackground(list.getBackground());
						setForeground(list.getForeground());
					}
				}

				setText((String) value);
				return this;
			}
		};

		cbLineWeight.setRenderer(drawRenderer);

		ActionListener drawActionListener = new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
 				int ind = cbLineWeight.getSelectedIndex();

				if (ind == 0)
					onUpdateLinkWeight(1);
				else if (ind == 1)
					onUpdateLinkWeight(2);
				else if (ind == 2)
					onUpdateLinkWeight(3);
				else if (ind == 3)
					onUpdateLinkWeight(4);
				else if (ind == 4)
					onUpdateLinkWeight(5);
				else if (ind == 5)
					onUpdateLinkWeight(6);
				else if (ind == 6)
					onUpdateLinkWeight(7);
				else if (ind == 7)
					onUpdateLinkWeight(8);
				else if (ind == 8)
					onUpdateLinkWeight(9);
				else if (ind == 9)
					onUpdateLinkWeight(10);
         	}
		};
        cbLineWeight.addActionListener(drawActionListener);

		drawPanel.add(new JLabel(" "), BorderLayout.WEST); //$NON-NLS-1$
		drawPanel.add(cbLineWeight, BorderLayout.CENTER);
		return drawPanel;
	}	

	/**
	 * Create a choicebox for link style options and return the panel it is in.
	 * @return JPanel the panel holding the new choicebox for the link style options.
	 */
	private JPanel createLinkStyleChoiceBox() {

		JPanel drawPanel = new JPanel(new BorderLayout());
		CSH.setHelpIDString(drawPanel,"toolbars.formatlink"); //$NON-NLS-1$

		cbLinkStyle = new JComboBox();
		cbLinkStyle.setToolTipText(LanguageProperties.getString(LanguageProperties.TOOLBARS_BUNDLE, "UIToolBarFormatLink.selectStlye")); //$NON-NLS-1$
		cbLinkStyle.setOpaque(true);
		cbLinkStyle.setEditable(false);
		cbLinkStyle.setEnabled(false);
		cbLinkStyle.setMaximumRowCount(10);
		cbLinkStyle.setFont( new Font("Dialog", Font.PLAIN, 10 )); //$NON-NLS-1$

		cbLinkStyle.addItem(new String(LanguageProperties.getString(LanguageProperties.TOOLBARS_BUNDLE, "UIToolBarFormatLink.straightLink"))); //$NON-NLS-1$
		cbLinkStyle.addItem(new String(LanguageProperties.getString(LanguageProperties.TOOLBARS_BUNDLE, "UIToolBarFormatLink.curvedLink"))); //$NON-NLS-1$
		cbLinkStyle.addItem(new String(LanguageProperties.getString(LanguageProperties.TOOLBARS_BUNDLE, "UIToolBarFormatLink.squaredLink"))); //$NON-NLS-1$

		cbLinkStyle.validate();

		cbLinkStyle.setSelectedIndex(0);

		DefaultListCellRenderer drawRenderer = new DefaultListCellRenderer() {
			public Component getListCellRendererComponent(
   		     	JList list,
   		        Object value,
            	int modelIndex,
            	boolean isSelected,
            	boolean cellHasFocus)
            {
				if (list != null) {
	 		 		if (isSelected) {
						setBackground(list.getSelectionBackground());
						setForeground(list.getSelectionForeground());
					}
					else {
						setBackground(list.getBackground());
						setForeground(list.getForeground());
					}
				}

				setText((String) value);
				return this;
			}
		};

		cbLinkStyle.setRenderer(drawRenderer);

		ActionListener drawActionListener = new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
  				onUpdateLinkStyle(cbLinkStyle.getSelectedIndex());
         	}
		};
		cbLinkStyle.addActionListener(drawActionListener);

		drawPanel.add(new JLabel(" "), BorderLayout.WEST); //$NON-NLS-1$
		drawPanel.add(cbLinkStyle, BorderLayout.CENTER);
		return drawPanel;
	}	
	
	/**
	 * Create a choicebox for link line style options and return the panel it is in.
	 * @return JPanel the panel holding the new choicebox for the link style options.
	 */
	private JPanel createLinkDashedChoiceBox() {

		JPanel drawPanel = new JPanel(new BorderLayout());
		CSH.setHelpIDString(drawPanel,"toolbars.formatlink"); //$NON-NLS-1$

		cbLinkDashed = new JComboBox();
		cbLinkDashed.setToolTipText(LanguageProperties.getString(LanguageProperties.TOOLBARS_BUNDLE, "UIToolBarFormatLink.selectDashed")); //$NON-NLS-1$
		cbLinkDashed.setOpaque(true);
		cbLinkDashed.setEditable(false);
		cbLinkDashed.setEnabled(false);
		cbLinkDashed.setMaximumRowCount(10);
		cbLinkDashed.setFont( new Font("Dialog", Font.PLAIN, 10 )); //$NON-NLS-1$

		cbLinkDashed.addItem(new String(LanguageProperties.getString(LanguageProperties.TOOLBARS_BUNDLE, "UIToolBarFormatLink.plainLine"))); //$NON-NLS-1$
		cbLinkDashed.addItem(new String(LanguageProperties.getString(LanguageProperties.TOOLBARS_BUNDLE, "UIToolBarFormatLink.largeDashes"))); //$NON-NLS-1$
		cbLinkDashed.addItem(new String(LanguageProperties.getString(LanguageProperties.TOOLBARS_BUNDLE, "UIToolBarFormatLink.smallDashes"))); //$NON-NLS-1$

		cbLinkDashed.validate();

		cbLinkDashed.setSelectedIndex(0);

		DefaultListCellRenderer drawRenderer = new DefaultListCellRenderer() {
			public Component getListCellRendererComponent(
   		     	JList list,
   		        Object value,
            	int modelIndex,
            	boolean isSelected,
            	boolean cellHasFocus)
            {
				if (list != null) {
	 		 		if (isSelected) {
						setBackground(list.getSelectionBackground());
						setForeground(list.getSelectionForeground());
					}
					else {
						setBackground(list.getBackground());
						setForeground(list.getForeground());
					}
				}

				setText((String) value);
				return this;
			}
		};

		cbLinkDashed.setRenderer(drawRenderer);

		ActionListener drawActionListener = new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
 				onUpdateLinkDashed(cbLinkDashed.getSelectedIndex());
         	}
		};
		cbLinkDashed.addActionListener(drawActionListener);

		drawPanel.add(new JLabel(" "), BorderLayout.WEST); //$NON-NLS-1$
		drawPanel.add(cbLinkDashed, BorderLayout.CENTER);
		return drawPanel;
	}	
		
	
	/**
	 * Update the look and feel of the toolbar.
	 */
	public void updateLAF() {
		if (tbrToolBar != null)
			SwingUtilities.updateComponentTreeUI(tbrToolBar);
	}
	
	/**
	 * Handles toolbar action event for this toolbar.
	 *
	 * @param evt the generated action event to be handled.
	 */
	public void actionPerformed(ActionEvent evt) {

		//oParent.setWaitCursor();
		Object source = evt.getSource();
		//oParent.setDefaultCursor();
	}

	/**
	 * Update the arrow head type on the currently selected links.
	 */
	private void onUpdateArrowType(int nArrowType) {
		if (!bJustSetting) {
			
			UIViewFrame frame = ProjectCompendium.APP.getCurrentFrame();
			Model oModel = (Model)ProjectCompendium.APP.getModel();
			if (frame instanceof UIMapViewFrame) {

				Vector vtUpdateLinks = new Vector();								
				UIMapViewFrame oMapFrame = (UIMapViewFrame)frame;
				UIViewPane pane = oMapFrame.getViewPane();
				UILink link = null;
				LinkProperties props = null;
				for (Enumeration e = pane.getSelectedLinks(); e.hasMoreElements();) {
					link = (UILink)e.nextElement();					
					props = link.getLinkProperties();
					if (nArrowType != props.getArrowType()) {
						vtUpdateLinks.addElement(props);
					}
				}				
				if (vtUpdateLinks.size() > 0) {				
					try {
						((ViewService)oModel.getViewService()).setArrowType(oModel.getSession(), pane.getView().getId(), vtUpdateLinks, nArrowType);
						int count = vtUpdateLinks.size();
						for (int i=0; i<count;i++) {
							props = (LinkProperties)vtUpdateLinks.elementAt(i);
							props.setArrowType(nArrowType);
						}
					} catch (SQLException ex) {
						ProjectCompendium.APP.displayError(LanguageProperties.getString(LanguageProperties.TOOLBARS_BUNDLE, "UIToolBarFormatLink.unableUpdateArrowType")+":\n\n"+ex.getMessage()); //$NON-NLS-1$
					}
				}
			}
		}					
	}	
	
	/**
	 * Update the link style on the currently selected links.
	 */
	private void onUpdateLinkStyle(int nStyle) {
		if (!bJustSetting) {
			
			UIViewFrame frame = ProjectCompendium.APP.getCurrentFrame();
			Model oModel = (Model)ProjectCompendium.APP.getModel();
			if (frame instanceof UIMapViewFrame) {

				Vector vtUpdateLinks = new Vector();								
				UIMapViewFrame oMapFrame = (UIMapViewFrame)frame;
				UIViewPane pane = oMapFrame.getViewPane();
				UILink link = null;
				LinkProperties props = null;
				for (Enumeration e = pane.getSelectedLinks(); e.hasMoreElements();) {
					link = (UILink)e.nextElement();					
					props = link.getLinkProperties();
					if (nStyle != props.getLinkStyle()) {
						vtUpdateLinks.addElement(props);
					}
				}				
				if (vtUpdateLinks.size() > 0) {				
					try {
						((ViewService)oModel.getViewService()).setLinkStyle(oModel.getSession(), pane.getView().getId(), vtUpdateLinks, nStyle);
						int count = vtUpdateLinks.size();
						for (int i=0; i<count;i++) {
							props = (LinkProperties)vtUpdateLinks.elementAt(i);
							props.setLinkStyle(nStyle);
						}
					} catch (SQLException ex) {
						ProjectCompendium.APP.displayError(LanguageProperties.getString(LanguageProperties.TOOLBARS_BUNDLE, "UIToolBarFormatLink.unableUpdateLinkStyle")+":\n\n"+ex.getMessage()); //$NON-NLS-1$
					}
				}
			}
		}					
	}
	
	/**
	 * Update the link line style on the currently selected links.
	 */
	private void onUpdateLinkDashed(int nDashed) {
		if (!bJustSetting) {
			
			UIViewFrame frame = ProjectCompendium.APP.getCurrentFrame();
			Model oModel = (Model)ProjectCompendium.APP.getModel();
			if (frame instanceof UIMapViewFrame) {

				Vector vtUpdateLinks = new Vector();								
				UIMapViewFrame oMapFrame = (UIMapViewFrame)frame;
				UIViewPane pane = oMapFrame.getViewPane();
				UILink link = null;
				LinkProperties props = null;
				for (Enumeration e = pane.getSelectedLinks(); e.hasMoreElements();) {
					link = (UILink)e.nextElement();					
					props = link.getLinkProperties();
					if (nDashed != props.getLinkDashed()) {
						vtUpdateLinks.addElement(props);
					}
				}				
				if (vtUpdateLinks.size() > 0) {				
					try {
						((ViewService)oModel.getViewService()).setLinkDashed(oModel.getSession(), pane.getView().getId(), vtUpdateLinks, nDashed);
						int count = vtUpdateLinks.size();
						for (int i=0; i<count;i++) {
							props = (LinkProperties)vtUpdateLinks.elementAt(i);
							props.setLinkDashed(nDashed);
						}
					} catch (SQLException ex) {
						ProjectCompendium.APP.displayError(LanguageProperties.getString(LanguageProperties.TOOLBARS_BUNDLE, "UIToolBarFormatLink.unableUpdateLinkDashed")+":\n\n"+ex.getMessage()); //$NON-NLS-1$
					}
				}
			}
		}					
	}

	/**
	 * Update the link weight on the currently selected links.
	 */
	private void onUpdateLinkWeight(int nWeight) {
		if (!bJustSetting) {
			
			UIViewFrame frame = ProjectCompendium.APP.getCurrentFrame();
			Model oModel = (Model)ProjectCompendium.APP.getModel();
			if (frame instanceof UIMapViewFrame) {

				Vector vtUpdateLinks = new Vector();								
				UIMapViewFrame oMapFrame = (UIMapViewFrame)frame;
				UIViewPane pane = oMapFrame.getViewPane();
				UILink link = null;
				LinkProperties props = null;
				for (Enumeration e = pane.getSelectedLinks(); e.hasMoreElements();) {
					link = (UILink)e.nextElement();					
					props = link.getLinkProperties();
					if (nWeight != props.getLinkWeight()) {
						vtUpdateLinks.addElement(props);
					}
				}				
				if (vtUpdateLinks.size() > 0) {				
					try {
						((ViewService)oModel.getViewService()).setLinkWeight(oModel.getSession(), pane.getView().getId(), vtUpdateLinks, nWeight);
						int count = vtUpdateLinks.size();
						for (int i=0; i<count;i++) {
							props = (LinkProperties)vtUpdateLinks.elementAt(i);
							props.setLinkWeight(nWeight);
						}
					} catch (SQLException ex) {
						ProjectCompendium.APP.displayError(LanguageProperties.getString(LanguageProperties.TOOLBARS_BUNDLE, "UIToolBarFormatLink.unableUpdateLinkWeight")+":\n\n"+ex.getMessage()); //$NON-NLS-1$
					}
				}
			}
		}					
	}

	/**
	 * Update the link colour on the currently selected links.
	 */
	private void onUpdateLinkColour(int nColour) {
		if (!bJustSetting) {
			
			UIViewFrame frame = ProjectCompendium.APP.getCurrentFrame();
			Model oModel = (Model)ProjectCompendium.APP.getModel();
			if (frame instanceof UIMapViewFrame) {

				Vector vtUpdateLinks = new Vector();								
				UIMapViewFrame oMapFrame = (UIMapViewFrame)frame;
				UIViewPane pane = oMapFrame.getViewPane();
				UILink link = null;
				LinkProperties props = null;
				for (Enumeration e = pane.getSelectedLinks(); e.hasMoreElements();) {
					link = (UILink)e.nextElement();					
					props = link.getLinkProperties();
					if (nColour != props.getLinkColour()) {
						vtUpdateLinks.addElement(props);
					}
				}				
				if (vtUpdateLinks.size() > 0) {				
					try {
						((ViewService)oModel.getViewService()).setLinkColour(oModel.getSession(), pane.getView().getId(), vtUpdateLinks, nColour);
						int count = vtUpdateLinks.size();
						for (int i=0; i<count;i++) {
							props = (LinkProperties)vtUpdateLinks.elementAt(i);
							props.setLinkColour(nColour);
						}
					} catch (SQLException ex) {
						ProjectCompendium.APP.displayError(LanguageProperties.getString(LanguageProperties.TOOLBARS_BUNDLE, "UIToolBarFormatLink.unableUpdateLinkColour")+":\n\n"+ex.getMessage()); //$NON-NLS-1$
					}
				}
			}
		}					
	}
	
	/**
	 * Updates the menu when a new database project is opened.
	 */
	public void onDatabaseOpen() {
		if (tbrToolBar != null)
			tbrToolBar.setEnabled(false);
	}

	/**
	 * Updates the menu when the current database project is closed.
	 */
	public void onDatabaseClose() {
		if (tbrToolBar != null)
			tbrToolBar.setEnabled(false);
	}

	/**
 	 * Enable the toobar icons - If the current view is a map.
 	 * @param selected true to enable, false to disable.
	 */
	public void setNodeSelected(boolean selected) {}

	/**
 	 * Does Nothing
 	 * @param selected, true to enable, false to disable.
	 */
	public void setNodeOrLinkSelected(boolean selected) {
		if (tbrToolBar != null) {
			
			UIViewFrame frame = ProjectCompendium.APP.getCurrentFrame();
			if (selected && frame instanceof UIMapViewFrame) {												
				UIMapViewFrame oMapFrame = (UIMapViewFrame)frame;
				UIViewPane pane = oMapFrame.getViewPane();
				
				int nLinkCount = pane.getNumberOfSelectedLinks();
				if (nLinkCount == 0)
					return;
				
				bJustSetting = true;
					
				int i=0;
				UILink link = null;
				LinkProperties linkProps = null;
				boolean bDefaultLinkStyle = false;
				boolean bDefaultLinkDashed = false;
				boolean bDefaultLinkWeight = false;
				boolean bDefaultLinkColour = false;
				boolean bDefaultArrowType = false;
				
				LinkProperties defaultprops = UIUtilities.getLinkProperties("");
				int linkstyle = defaultprops.getLinkStyle();
				int linkdashed = defaultprops.getLinkDashed();
				int linkweight = defaultprops.getLinkWeight();
				int linkcolour = defaultprops.getLinkColour();
				int arrowtype = defaultprops.getArrowType();
				
				for (Enumeration e = pane.getSelectedLinks(); e.hasMoreElements();) {
					link = (UILink)e.nextElement();
					linkProps = link.getLinkProperties();
					if (i==0) {
						linkstyle = linkProps.getLinkStyle();
						linkdashed = linkProps.getLinkDashed();
						linkweight = linkProps.getLinkWeight();
						linkcolour = linkProps.getLinkColour();
						arrowtype = linkProps.getArrowType();
						i++;
					} else {
						if (arrowtype != linkProps.getArrowType()) {
							bDefaultArrowType = true;
						}
						if (linkstyle != linkProps.getLinkStyle()) {
							bDefaultLinkStyle = true;
						}
						if (linkdashed != linkProps.getLinkDashed()) {
							bDefaultLinkDashed = true;
						}
						if (linkweight != linkProps.getLinkWeight()) {
							bDefaultLinkWeight = true;
						}
						if (linkcolour != linkProps.getLinkColour()) {
							bDefaultLinkColour = true;
						}
					}
				}				
								
				tbrToolBar.setEnabled(true);
				
				if (bDefaultLinkColour) {
					selectedLinkColour = new Color(defaultprops.getLinkColour());
				} else {
					selectedLinkColour = new Color(linkcolour);
				}
					
				linkColourPanel.setEnabled(true);				
				txtLinkColour.setEnabled(true);	
					
				cbLineWeight.setEnabled(true);
				if (bDefaultLinkWeight) {
					cbLineWeight.setSelectedIndex(defaultprops.getLinkWeight()-1);
				} else {
					cbLineWeight.setSelectedIndex(linkweight-1);					
				}
				
				cbArrows.setEnabled(true);
				if (bDefaultArrowType) {
					cbArrows.setSelectedIndex(defaultprops.getArrowType());
				} else {
					cbArrows.setSelectedIndex(arrowtype);					
				}

				cbLinkStyle.setEnabled(true);
				if (bDefaultLinkStyle) {
					cbLinkStyle.setSelectedIndex(defaultprops.getLinkStyle());
				} else {
					cbLinkStyle.setSelectedIndex(linkstyle);					
				}

				cbLinkDashed.setEnabled(true);
				if (bDefaultLinkDashed) {
					cbLinkDashed.setSelectedIndex(defaultprops.getLinkDashed());
				} else {
					cbLinkDashed.setSelectedIndex(linkdashed);					
				}

				bJustSetting = false;
				
			} else if (!selected) {
				
				bJustSetting = true;				
				
				linkColourPanel.setEnabled(false);				
				txtLinkColour.setEnabled(false);	
				tbrToolBar.setEnabled(false);
				cbArrows.setEnabled(false);
				cbLineWeight.setEnabled(false);
				cbLinkStyle.setEnabled(false);
				cbLinkDashed.setEnabled(false);
				
				bJustSetting = false;				
			}
		}		
	}
			
	public UIToolBar getToolBar() {
		return tbrToolBar;
	}
	
	/**
	 * Enable/disable the toolbar.
	 * @param enabled true to enable, false to disable.
	 */
	public void setEnabled(boolean enabled) {
		tbrToolBar.setEnabled(enabled);
	}
	
	/**
	 * Return true if this toolbar is active by default, or false if it must be switched on by the user.
	 * @return true if the toolbar is active by default, else false.
	 */
	public boolean getDefaultActiveState() {
		return DEFAULT_STATE;
	}	
		
	/**
	 * Return a unique integer identifier for this toolbar.
	 * @return a unique integer identifier for this toolbar.
	 */
	public int getType() {
		return nType;
	}	
}
