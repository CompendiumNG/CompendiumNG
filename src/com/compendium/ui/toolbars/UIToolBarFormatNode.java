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
import com.compendium.core.datamodel.LinkProperties;
import com.compendium.core.datamodel.NodePosition;
import com.compendium.core.datamodel.Model;
import com.compendium.core.datamodel.ModelSessionException;
import com.compendium.core.datamodel.services.ViewService;
import com.compendium.ui.*;
import com.compendium.ui.dialogs.*;
import com.compendium.ui.toolbars.system.*;


/**
 * This class manages the Node Formatting toolbars
 *
 * @author	Michelle Bachler
 * @version	1.0
 */
public class UIToolBarFormatNode implements IUIToolBar, ActionListener, IUIConstants {
	
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

	private UIToolBar			tbrToolBar 			= null;
	
	/** The button to select showing node text indicator.*/
	private JRadioButton		pbTextIndicator		= null;

	/** The button to select showing node weight indicator.*/
	private JRadioButton		pbWeightIndicator	= null;

	/** The button to select showing node tag indicator.*/
	private JRadioButton		pbTagIndicator		= null;

	/** The button to select showing node transcludion indicator.*/
	private JRadioButton		pbTransIndicator	= null;

	/** The button to select showing small icons.*/
	private JRadioButton		pbSmallIcons		= null;

	/** The button to select hiding icons.*/
	private JRadioButton		pbHideIcons			= null;
			
	/** Indicates that the node items are being displayed rather than changed by the user.*/
	private boolean				bJustSetting		= false;
	
	
	/**
	 * Create a new instance of UIToolBarFormat, with the given properties.
	 * @param oManager the IUIToolBarManager that is managing this toolbar.
	 * @param parent the parent frame for the application.
	 * @param nType the unique identifier for this toolbar.
	 */
	public UIToolBarFormatNode(IUIToolBarManager oManager, ProjectCompendiumFrame parent, int nType) {

		this.oParent = parent;
		this.oManager = oManager;
		this.nType = nType;
		
		createToolBar(DEFAULT_ORIENTATION);
	}

	/**
	 * Create a new instance of UIToolBarFormat, with the given properties.
	 * @param oManager the IUIToolBarManager that is managing this toolbar.
	 * @param parent the parent frame for the application.
	 * @param nType the unique identifier for this toolbar.
	 * @param orientation the orientation of this toolbars ui object.     
	 */
	public UIToolBarFormatNode(IUIToolBarManager oManager, ProjectCompendiumFrame parent, int nType, int orientation) {

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
		
		tbrToolBar = new UIToolBar(LanguageProperties.getString(LanguageProperties.TOOLBARS_BUNDLE, "UIToolBarFormatNode.name"), UIToolBar.NORTHSOUTH); //$NON-NLS-1$
		tbrToolBar.setOrientation(orientation);
		tbrToolBar.setEnabled(false);
		CSH.setHelpIDString(tbrToolBar,"toolbars.format"); //$NON-NLS-1$
						
		pbTextIndicator = tbrToolBar.createToolBarRadioButton(LanguageProperties.getString(LanguageProperties.TOOLBARS_BUNDLE, "UIToolBarFormatNode.hideTextIndicator"), UIImages.get(SHOW_TEXT)); //$NON-NLS-1$
		pbTextIndicator.setSelectedIcon(UIImages.get(SHOW_TEXT_SELECTED));
		pbTextIndicator.addActionListener(this);
		pbTextIndicator.setEnabled(true);
		tbrToolBar.add(pbTextIndicator);
		CSH.setHelpIDString(pbTextIndicator,"toolbars.format"); //$NON-NLS-1$

		pbWeightIndicator = tbrToolBar.createToolBarRadioButton(LanguageProperties.getString(LanguageProperties.TOOLBARS_BUNDLE, "UIToolBarFormatNode.hideWeightIndicator"), UIImages.get(SHOW_WEIGHT)); //$NON-NLS-1$
		pbWeightIndicator.setSelectedIcon(UIImages.get(SHOW_WEIGHT_SELECTED));
		pbWeightIndicator.addActionListener(this);
		pbWeightIndicator.setEnabled(true);
		tbrToolBar.add(pbWeightIndicator);
		CSH.setHelpIDString(pbWeightIndicator,"toolbars.format"); //$NON-NLS-1$

		pbTagIndicator = tbrToolBar.createToolBarRadioButton(LanguageProperties.getString(LanguageProperties.TOOLBARS_BUNDLE, "UIToolBarFormatNode.hideTagIndicator"), UIImages.get(SHOW_TAGS)); //$NON-NLS-1$
		pbTagIndicator.setSelectedIcon(UIImages.get(SHOW_TAGS_SELECTED));
		pbTagIndicator.addActionListener(this);
		pbTagIndicator.setEnabled(true);
		tbrToolBar.add(pbTagIndicator);
		CSH.setHelpIDString(pbTagIndicator,"toolbars.format"); //$NON-NLS-1$

		pbTransIndicator = tbrToolBar.createToolBarRadioButton(LanguageProperties.getString(LanguageProperties.TOOLBARS_BUNDLE, "UIToolBarFormatNode.hideTransclusionIndicator"), UIImages.get(SHOW_TRANS)); //$NON-NLS-1$
		pbTransIndicator.setSelectedIcon(UIImages.get(SHOW_TRANS_SELECTED));
		pbTransIndicator.addActionListener(this);
		pbTransIndicator.setEnabled(true);
		tbrToolBar.add(pbTransIndicator);
		CSH.setHelpIDString(pbTransIndicator,"toolbars.format"); //$NON-NLS-1$

		pbSmallIcons = tbrToolBar.createToolBarRadioButton(LanguageProperties.getString(LanguageProperties.TOOLBARS_BUNDLE, "UIToolBarFormatNode.smallIcons"), UIImages.get(SMALL_ICONS_SELECTED)); //$NON-NLS-1$
		pbSmallIcons.setSelectedIcon(UIImages.get(SMALL_ICONS));		
		pbSmallIcons.addActionListener(this);
		pbSmallIcons.setEnabled(true);
		tbrToolBar.add(pbSmallIcons);
		CSH.setHelpIDString(pbSmallIcons,"toolbars.format"); //$NON-NLS-1$

		pbHideIcons = tbrToolBar.createToolBarRadioButton(LanguageProperties.getString(LanguageProperties.TOOLBARS_BUNDLE, "UIToolBarFormatNode.nodeIcons"), UIImages.get(HIDE_ICONS_SELECTED)); //$NON-NLS-1$
		pbHideIcons.setSelectedIcon(UIImages.get(HIDE_ICONS));				
		pbHideIcons.addActionListener(this);
		pbHideIcons.setEnabled(true);
		tbrToolBar.add(pbHideIcons);
		CSH.setHelpIDString(pbHideIcons,"toolbars.format"); //$NON-NLS-1$
				
		return tbrToolBar;
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
	 * @param evt the genereated action event to be handled.
	 */
	public void actionPerformed(ActionEvent evt) {

		oParent.setWaitCursor();
		Object source = evt.getSource();

		if (source.equals(pbTextIndicator)) {
			onUpdateTextIndicators();
		} else if (source.equals(pbTagIndicator)) {
			onUpdateTagsIndicators();
		} else if (source.equals(pbTransIndicator)) {
			onUpdateTransIndicators();
		} else if (source.equals(pbWeightIndicator)) {
			onUpdateWeightIndicators();
		} else if (source.equals(pbSmallIcons)) {
			onUpdateSmallIcons();
		} else if (source.equals(pbHideIcons)) {
			onUpdateHideIcons();			
		}
		oParent.setDefaultCursor();
	}

	/**
	 * Update the text indicator on the currently selected nodes.
	 */
	private void onUpdateTextIndicators() {
		if (!bJustSetting) {
						
			String sInBoxID = oParent.getInBoxID();
			String sTrashBinID = oParent.getTrashBinID();
			
			UIViewFrame frame = ProjectCompendium.APP.getCurrentFrame();
			Model oModel = (Model)ProjectCompendium.APP.getModel();
			if (frame instanceof UIMapViewFrame) {

				Vector vtUpdateNodes = new Vector();				
				boolean bShowText = pbTextIndicator.isSelected();
				
				UIMapViewFrame oMapFrame = (UIMapViewFrame)frame;
				UIViewPane pane = oMapFrame.getViewPane();
				UINode node = null;
				NodePosition pos = null;
				String sNodeID = ""; //$NON-NLS-1$
				for (Enumeration e = pane.getSelectedNodes(); e.hasMoreElements();) {
					node = (UINode)e.nextElement();
					sNodeID = node.getNode().getId();
					if (!sNodeID.equals(sInBoxID) && !sNodeID.equals(sTrashBinID)) {
						pos = node.getNodePosition();					
						if (bShowText != pos.getShowText()) {
							vtUpdateNodes.addElement(pos);
						}
					}
				}
				
				if (vtUpdateNodes.size() > 0) {				
					try {
						((ViewService)oModel.getViewService()).setShowTextIndicator(oModel.getSession(), pane.getView().getId(), vtUpdateNodes, bShowText);
						int count = vtUpdateNodes.size();
						for (int i=0; i<count;i++) {
							pos = (NodePosition)vtUpdateNodes.elementAt(i);
							pos.setShowText(bShowText);
						}
					} catch (SQLException ex) {
						ProjectCompendium.APP.displayError(LanguageProperties.getString(LanguageProperties.TOOLBARS_BUNDLE, "UIToolBarFormatNode.unableUpdateTextInd")+":\n\n"+ex.getMessage()); //$NON-NLS-1$
					}
				}
			}
		}					
	}
	
	/**
	 * Update the tags indicator on the currently selected nodes.
	 */
	private void onUpdateTagsIndicators() {
		if (!bJustSetting) {
			
			String sInBoxID = oParent.getInBoxID();
			String sTrashBinID = oParent.getTrashBinID();
			
			UIViewFrame frame = ProjectCompendium.APP.getCurrentFrame();
			Model oModel = (Model)ProjectCompendium.APP.getModel();
			if (frame instanceof UIMapViewFrame) {

				Vector vtUpdateNodes = new Vector();				
				boolean bShowTags = pbTagIndicator.isSelected();
				
				UIMapViewFrame oMapFrame = (UIMapViewFrame)frame;
				UIViewPane pane = oMapFrame.getViewPane();
				UINode node = null;
				NodePosition pos = null;
				String sNodeID = ""; //$NON-NLS-1$
				for (Enumeration e = pane.getSelectedNodes(); e.hasMoreElements();) {
					node = (UINode)e.nextElement();
					
					sNodeID = node.getNode().getId();
					if (!sNodeID.equals(sInBoxID) && !sNodeID.equals(sTrashBinID)) {					
						pos = node.getNodePosition();					
						if (bShowTags != pos.getShowTags()) {
							vtUpdateNodes.addElement(pos);
						}
					}
				}
				
				if (vtUpdateNodes.size() > 0) {				
					try {
						((ViewService)oModel.getViewService()).setShowTagsIndicator(oModel.getSession(), pane.getView().getId(), vtUpdateNodes, bShowTags);
						int count = vtUpdateNodes.size();
						for (int i=0; i<count;i++) {
							pos = (NodePosition)vtUpdateNodes.elementAt(i);
							pos.setShowTags(bShowTags);
						}
					} catch (SQLException ex) {
						ProjectCompendium.APP.displayError(LanguageProperties.getString(LanguageProperties.TOOLBARS_BUNDLE, "UIToolBarFormatNode.unableUpdateTagInd")+":\n\n"+ex.getMessage()); //$NON-NLS-1$
					}
				}
			}
		}					
	}
	
	/**
	 * Update the transclusion indicator on the currently selected nodes.
	 */
	private void onUpdateTransIndicators() {
		if (!bJustSetting) {
			
			String sInBoxID = oParent.getInBoxID();
			String sTrashBinID = oParent.getTrashBinID();
			
			UIViewFrame frame = ProjectCompendium.APP.getCurrentFrame();
			Model oModel = (Model)ProjectCompendium.APP.getModel();
			if (frame instanceof UIMapViewFrame) {
				
				Vector vtUpdateNodes = new Vector();				
				boolean bShowTrans = pbTransIndicator.isSelected();				

				UIMapViewFrame oMapFrame = (UIMapViewFrame)frame;
				UIViewPane pane = oMapFrame.getViewPane();
				UINode node = null;
				NodePosition pos = null;
				String sNodeID = ""; //$NON-NLS-1$
				for (Enumeration e = pane.getSelectedNodes(); e.hasMoreElements();) {
					node = (UINode)e.nextElement();
					
					sNodeID = node.getNode().getId();
					if (!sNodeID.equals(sInBoxID) && !sNodeID.equals(sTrashBinID)) {					
						pos = node.getNodePosition();
						if (bShowTrans != pos.getShowTrans()) {
							vtUpdateNodes.addElement(pos);
						}
					}
				}
				
				if (vtUpdateNodes.size() > 0) {				
					try {
						((ViewService)oModel.getViewService()).setShowTransIndicator(oModel.getSession(), pane.getView().getId(), vtUpdateNodes, bShowTrans);
						int count = vtUpdateNodes.size();
						for (int i=0; i<count;i++) {
							pos = (NodePosition)vtUpdateNodes.elementAt(i);
							pos.setShowTrans(bShowTrans);
						}
					} catch (SQLException ex) {
						ProjectCompendium.APP.displayError(LanguageProperties.getString(LanguageProperties.TOOLBARS_BUNDLE, "UIToolBarFormatNode.unableUpdateTransclusionInd")+":\n\n"+ex.getMessage()); //$NON-NLS-1$
					}
				}
			}
		}					
	}
	
	/**
	 * Update the weight indicator on the currently selected nodes.
	 */
	private void onUpdateWeightIndicators() {
		if (!bJustSetting) {
			
			String sInBoxID = oParent.getInBoxID();
			String sTrashBinID = oParent.getTrashBinID();
			
			UIViewFrame frame = ProjectCompendium.APP.getCurrentFrame();
			Model oModel = (Model)ProjectCompendium.APP.getModel();
			if (frame instanceof UIMapViewFrame) {
				
				Vector vtUpdateNodes = new Vector();				
				boolean bShowWeight = pbWeightIndicator.isSelected();
				
				UIMapViewFrame oMapFrame = (UIMapViewFrame)frame;
				UIViewPane pane = oMapFrame.getViewPane();
				UINode node = null;
				NodePosition pos = null;
				String sNodeID = ""; //$NON-NLS-1$
				for (Enumeration e = pane.getSelectedNodes(); e.hasMoreElements();) {
					node = (UINode)e.nextElement();
					sNodeID = node.getNode().getId();
					if (!sNodeID.equals(sInBoxID) && !sNodeID.equals(sTrashBinID)) {					
						pos = node.getNodePosition();
						if (bShowWeight != pos.getShowWeight()) {
							vtUpdateNodes.addElement(pos);
						}
					}
				}
				
				if (vtUpdateNodes.size() > 0) {				
					try {
						((ViewService)oModel.getViewService()).setShowWeightIndicator(oModel.getSession(), pane.getView().getId(), vtUpdateNodes, bShowWeight);
						int count = vtUpdateNodes.size();
						for (int i=0; i<count;i++) {
							pos = (NodePosition)vtUpdateNodes.elementAt(i);
							pos.setShowWeight(bShowWeight);
						}
					} catch (SQLException ex) {
						ProjectCompendium.APP.displayError(LanguageProperties.getString(LanguageProperties.TOOLBARS_BUNDLE, "UIToolBarFormatNode.unableUpdateWightInd")+":\n\n"+ex.getMessage()); //$NON-NLS-1$
					}
				}
			}
		}					
	}
	
	/**
	 * Update the using small icons on the currently selected nodes.
	 */
	private void onUpdateSmallIcons() {
		if (!bJustSetting) {
			
			String sInBoxID = oParent.getInBoxID();
			String sTrashBinID = oParent.getTrashBinID();
			
			UIViewFrame frame = ProjectCompendium.APP.getCurrentFrame();
			Model oModel = (Model)ProjectCompendium.APP.getModel();
			
			if (frame instanceof UIMapViewFrame) {
				
				Vector vtUpdateNodes = new Vector();								
				boolean bSmallIcons = pbSmallIcons.isSelected();
				
				UIMapViewFrame oMapFrame = (UIMapViewFrame)frame;
				UIViewPane pane = oMapFrame.getViewPane();
				UINode node = null;
				NodePosition pos = null;
				String sNodeID = ""; //$NON-NLS-1$
				for (Enumeration e = pane.getSelectedNodes(); e.hasMoreElements();) {
					node = (UINode)e.nextElement();
					
					sNodeID = node.getNode().getId();
					if (!sNodeID.equals(sInBoxID) && !sNodeID.equals(sTrashBinID)) {					
						pos = node.getNodePosition();					
						if (bSmallIcons != pos.getShowSmallIcon()) {
							vtUpdateNodes.addElement(pos);
						}
					}
				}
				
				if (vtUpdateNodes.size() > 0) {				
					try {
						((ViewService)oModel.getViewService()).setShowSmallIcons(oModel.getSession(), pane.getView().getId(), vtUpdateNodes, bSmallIcons);
						int count = vtUpdateNodes.size();
						for (int i=0; i<count;i++) {
							pos = (NodePosition)vtUpdateNodes.elementAt(i);
							pos.setShowSmallIcon(bSmallIcons);
						}
					} catch (SQLException ex) {
						ProjectCompendium.APP.displayError(LanguageProperties.getString(LanguageProperties.TOOLBARS_BUNDLE, "UIToolBarFormatNode.unableUpdateSmallIcons")+":\n\n"+ex.getMessage()); //$NON-NLS-1$
					}
				}
			}
		}					
	}	
	
	/**
	 * Update the hiding icons on the currently selected nodes.
	 */
	private void onUpdateHideIcons() {
		if (!bJustSetting) {
			
			String sInBoxID = oParent.getInBoxID();
			String sTrashBinID = oParent.getTrashBinID();
			
			UIViewFrame frame = ProjectCompendium.APP.getCurrentFrame();
			Model oModel = (Model)ProjectCompendium.APP.getModel();
			if (frame instanceof UIMapViewFrame) {

				Vector vtUpdateNodes = new Vector();								
				boolean bHideIcons = pbHideIcons.isSelected();
				
				UIMapViewFrame oMapFrame = (UIMapViewFrame)frame;
				UIViewPane pane = oMapFrame.getViewPane();
				UINode node = null;
				NodePosition pos = null;
				String sNodeID = ""; //$NON-NLS-1$
				for (Enumeration e = pane.getSelectedNodes(); e.hasMoreElements();) {
					node = (UINode)e.nextElement();
					
					sNodeID = node.getNode().getId();
					if (!sNodeID.equals(sInBoxID) && !sNodeID.equals(sTrashBinID)) {					
						pos = node.getNodePosition();
						if (bHideIcons != pos.getHideIcon()) {
							vtUpdateNodes.addElement(pos);
						}
					}
				}
				
				if (vtUpdateNodes.size() > 0) {				
					try {
						((ViewService)oModel.getViewService()).setHideIcons(oModel.getSession(), pane.getView().getId(), vtUpdateNodes, bHideIcons);
						int count = vtUpdateNodes.size();
						for (int i=0; i<count;i++) {
							pos = (NodePosition)vtUpdateNodes.elementAt(i);
							pos.setHideIcon(bHideIcons);
						}
					} catch (SQLException ex) {
						ProjectCompendium.APP.displayError(LanguageProperties.getString(LanguageProperties.TOOLBARS_BUNDLE, "UIToolBarFormatNode.unableUpdateHideIcons")+":\n\n"+ex.getMessage()); //$NON-NLS-1$
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
	public void setNodeSelected(boolean selected) {
		if (tbrToolBar != null) {
			
			UIViewFrame frame = ProjectCompendium.APP.getCurrentFrame();
			Model oModel = (Model)ProjectCompendium.APP.getModel();
			
			if (selected && frame instanceof UIMapViewFrame) {												
				UIMapViewFrame oMapFrame = (UIMapViewFrame)frame;
				UIViewPane pane = oMapFrame.getViewPane();
				
				//return if node select is just trashbin or inbox or just the two.
				int nNodeCount = pane.getNumberOfSelectedNodes();
				boolean hasTrashbin = false;
				boolean hasInBox = false;
				String sTrashbinID = ProjectCompendium.APP.getTrashBinID();
				String sInboxID = ProjectCompendium.APP.getInBoxID();
				if (nNodeCount == 1) {
					UINode node = pane.getSelectedNode();
					String sNodeId = node.getNode().getId();
					if (sNodeId.equals(sTrashbinID) || 
							sNodeId.equals(sInboxID)) {
						return;
					}
				}
				
				bJustSetting = true;
				
				// THE SETTING OF THE FIRST NODE TO TEST AGAINST 
				// AND USE IF ALL NODES HAVE THE SAME SETTINGS
				boolean bShowTags = oModel.showTagsNodeIndicator;
				boolean bShowText = oModel.showTextNodeIndicator;
				boolean bShowTrans = oModel.showTransNodeIndicator;
				boolean bShowWeight = oModel.showWeightNodeIndicator;
				boolean bSmallIcon = oModel.smallIcons;
				boolean bHideIcon = oModel.hideIcons;
								
				// WHETHER TO USE THE DEFAULT SETTING OR THE FIRST NODE'S SETTING
				boolean bDefaultTags = false;
				boolean bDefaultText = false;
				boolean bDefaultTrans = false;
				boolean bDefaultWeight= false;
				boolean bDefaultSmall = false;
				boolean bDefaultHide = false;
				
				int i=0;
				
				UINode node = null;
				NodePosition pos = null;
				
				for (Enumeration e = pane.getSelectedNodes(); e.hasMoreElements();) {
					node = (UINode)e.nextElement();
					if (node.getNode().getId().equals(sTrashbinID)) {
						hasTrashbin = true;
						continue;
					} else if (node.getNode().getId().equals(sInboxID)) {
						hasInBox = true;
						continue;
					}
					pos = node.getNodePosition();
					if (i==0) {
						bShowTags = pos.getShowTags();
						bShowText = pos.getShowText();
						bShowTrans = pos.getShowTrans();
						bShowWeight = pos.getShowWeight();
						bSmallIcon = pos.getShowSmallIcon();
						bHideIcon = pos.getHideIcon();
						i++;
					} else {
						if (bShowTags != pos.getShowTags()) {
							bDefaultTags = true;
						}
						if (bShowText != pos.getShowText()) {
							bDefaultText = true;
						}
						if (bShowTrans != pos.getShowTrans()) {
							bDefaultTrans = true;
						}
						if (bShowWeight != pos.getShowWeight()) {
							bDefaultWeight = true;
						}
						if (bSmallIcon != pos.getShowSmallIcon()) {
							bDefaultSmall = true;
						}
						if (bHideIcon != pos.getHideIcon()) {
							bDefaultHide = true;
						}
					}
				}
				
				if (nNodeCount == 2 && hasTrashbin && hasInBox) {
					return;
				}
				
				if (bDefaultTags) {
					pbTagIndicator.setSelected(oModel.showTagsNodeIndicator);
				} else {
					pbTagIndicator.setSelected(bShowTags);										
				}
				if (bDefaultText) {
					pbTextIndicator.setSelected(oModel.showTextNodeIndicator);
				} else {
					pbTextIndicator.setSelected(bShowText);					
				}
				if (bDefaultTrans) {
					pbTransIndicator.setSelected(oModel.showTransNodeIndicator);					
				} else {
					pbTransIndicator.setSelected(bShowTrans);										
				}
				if (bDefaultWeight) {
					pbWeightIndicator.setSelected(oModel.showWeightNodeIndicator);					
				} else {
					pbWeightIndicator.setSelected(bShowWeight);									
				}
				if (bDefaultSmall) {
					pbSmallIcons.setSelected(oModel.smallIcons);	
				} else {
					pbSmallIcons.setSelected(bSmallIcon);	
				}
				if (bDefaultHide) {
					pbHideIcons.setSelected(oModel.hideIcons);	
				} else {
					pbHideIcons.setSelected(bHideIcon);	
				}

				pbTagIndicator.setEnabled(true);
				pbTextIndicator.setEnabled(true);
				pbTransIndicator.setEnabled(true);					
				pbWeightIndicator.setEnabled(true);					
				pbSmallIcons.setEnabled(true);	
				pbHideIcons.setEnabled(true);	

				bJustSetting = false;
				
			} else if (!selected) {
				
				bJustSetting = true;				
				
				pbTagIndicator.setSelected(oModel.showTagsNodeIndicator);
				pbTextIndicator.setSelected(oModel.showTextNodeIndicator);
				pbTransIndicator.setSelected(oModel.showTransNodeIndicator);					
				pbWeightIndicator.setSelected(oModel.showWeightNodeIndicator);					
				pbSmallIcons.setSelected(oModel.smallIcons);	
				pbHideIcons.setSelected(oModel.hideIcons);	

				pbTagIndicator.setEnabled(false);
				pbTextIndicator.setEnabled(false);
				pbTransIndicator.setEnabled(false);					
				pbWeightIndicator.setEnabled(false);					
				pbSmallIcons.setEnabled(false);	
				pbHideIcons.setEnabled(false);	

				bJustSetting = false;				
			}
		}
	}

	/**
 	 * Does Nothing
 	 * @param selected, true to enable, false to disable.
	 */
	public void setNodeOrLinkSelected(boolean selected) {}
			
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
