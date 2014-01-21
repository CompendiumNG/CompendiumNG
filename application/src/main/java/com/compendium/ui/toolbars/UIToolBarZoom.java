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

import com.compendium.LanguageProperties;
import com.compendium.ProjectCompendium;
import com.compendium.ui.*;
import com.compendium.ui.toolbars.system.IUIToolBarManager;
import com.compendium.ui.toolbars.system.UIToolBar;
import com.sun.media.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.help.CSH;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;


/**
 * This class manages all the toolbars
 *
 * @author	Michelle Bachler
 * @version	1.0
 */
public class UIToolBarZoom implements IUIToolBar, ActionListener, IUIConstants {
	private final Logger log = LoggerFactory.getLogger(this.getClass());

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

	/** The zoom toolbar.*/
	private UIToolBar			tbrToolBar 		= null;

	/** The button to zoom in on the current view.*/
	private JButton				pbZoomIn			= null;

	/** The button to zoom in on the current view.*/
	private JButton				pbZoomOut			= null;

	/** The button to zoom out on the current view.*/
	private JButton				pbZoomFull			= null;

	/** The button to zoom to normal and focus on a node on the current view.*/
	private JButton				pbZoomFocus			= null;

	/** The button to zoom to fit on the current view.*/
	private JButton				pbZoomFit			= null;

	/** The choicebox with the zoom options.*/
	private JComboBox 			cbZoom				= null;

	/** The panel for the zoom choice box.*/
	private JPanel 				zoomPanel 			= null;

	/** The action listener for the zoom choicebox.*/
	private ActionListener 		zoomActionListener 	= null;
	
	/** The button to zoom the label text up a size.*/
	private JButton				pbZoomInText		= null;

	/** The button to zoom the label text down a size.*/
	private JButton				pbZoomOutText		= null;

	/** The button to zoom the label text back to normal.*/
	private JButton				pbZoomFullText		= null;
	
	/** Displays the current text zoom.*/
	private JLabel				lblTextZoom			= null;
	
	
	/** Holds the last zoom choicebox index selected.*/
	private int 				lastZoom			= 0;
	
	/** Holds the current + or minus count on zooming the default text size for presentations. **/ 
	private int					currentTextZoom		= 0;

	/** Are we currently setting a zoom choice in the coice box?*/
	private boolean				setChoice			= false;
	
	/**
	 * Create a new instance of UIToolBarZoom, with the given properties.
	 * @param oManager the IUIToolBarManager that is managing this toolbar.
	 * @param parent the parent frame for the application.
	 * @param nType the unique identifier for this toolbar.
	 */
	public UIToolBarZoom(IUIToolBarManager oManager, ProjectCompendiumFrame parent, int nType) {

		this.oParent = parent;
		this.oManager = oManager;
		this.nType = nType;
		createToolBar(DEFAULT_ORIENTATION);		
	}
	
	/**
	 * Create a new instance of UIToolBarZoom, with the given properties.
	 * @param oManager the IUIToolBarManager that is managing this toolbar.
	 * @param parent the parent frame for the application.
	 * @param nType the unique identifier for this toolbar.
	 * @param orientation the orientation of this toolbars ui object.
	 */
	public UIToolBarZoom(IUIToolBarManager oManager, ProjectCompendiumFrame parent, int nType, int orientation) {

		this.oParent = parent;
		this.oManager = oManager;
		this.nType = nType;
		createToolBar(orientation);		
	}

	/**
	 * Update the look and feel of the toolbar
	 */
	public void updateLAF() {
		
	    pbZoomIn.setIcon(UIImages.get(ZOOM_IN_ICON));
	    pbZoomFull.setIcon(UIImages.get(ZOOM_FULL_ICON));
	    pbZoomOut.setIcon(UIImages.get(ZOOM_OUT_ICON));
	    pbZoomFit.setIcon(UIImages.get(ZOOM_FIT_ICON));
	    pbZoomFocus.setIcon(UIImages.get(ZOOM_FOCUS_ICON));
		
		if (tbrToolBar != null) {
			SwingUtilities.updateComponentTreeUI(tbrToolBar);
		}
	}

	/**
	 * Create and return the toolbar with all the view zoom options.
	 * @return UIToolBar, the toolbar with all the view zoom options.
	 */
	private UIToolBar createToolBar(int orientation) {

		tbrToolBar = new UIToolBar(LanguageProperties.getString(LanguageProperties.TOOLBARS_BUNDLE, "UIToolBarZoom.zoomToolbar"), UIToolBar.NORTHSOUTH); //$NON-NLS-1$
		tbrToolBar.setOrientation(orientation);

		tbrToolBar.add( createZoomChoiceBox() );
		CSH.setHelpIDString(tbrToolBar,"toolbars.zoom"); //$NON-NLS-1$

		tbrToolBar.addSeparator();

		pbZoomIn = tbrToolBar.createToolBarButton(LanguageProperties.getString(LanguageProperties.TOOLBARS_BUNDLE, "UIToolBarZoom.zoomOut"), UIImages.get(ZOOM_IN_ICON)); //$NON-NLS-1$
		pbZoomIn.addActionListener(this);
		pbZoomIn.setEnabled(true);
		tbrToolBar.add(pbZoomIn);
		CSH.setHelpIDString(pbZoomIn,"toolbars.zoom"); //$NON-NLS-1$

		pbZoomFull = tbrToolBar.createToolBarButton(LanguageProperties.getString(LanguageProperties.TOOLBARS_BUNDLE, "UIToolBarZoom.zoom100"), UIImages.get(ZOOM_FULL_ICON)); //$NON-NLS-1$
		pbZoomFull.addActionListener(this);
		pbZoomFull.setEnabled(true);
		tbrToolBar.add(pbZoomFull);
		CSH.setHelpIDString(pbZoomFull,"toolbars.zoom"); //$NON-NLS-1$

		pbZoomOut = tbrToolBar.createToolBarButton(LanguageProperties.getString(LanguageProperties.TOOLBARS_BUNDLE, "UIToolBarZoom.zoomIn"), UIImages.get(ZOOM_OUT_ICON)); //$NON-NLS-1$
		pbZoomOut.addActionListener(this);
		pbZoomOut.setEnabled(true);
		tbrToolBar.add(pbZoomOut);
		CSH.setHelpIDString(pbZoomOut,"toolbars.zoom"); //$NON-NLS-1$

		pbZoomFit = tbrToolBar.createToolBarButton(LanguageProperties.getString(LanguageProperties.TOOLBARS_BUNDLE, "UIToolBarZoom.zoomFitPage"), UIImages.get(ZOOM_FIT_ICON)); //$NON-NLS-1$
		pbZoomFit.addActionListener(this);
		pbZoomFit.setEnabled(true);
		tbrToolBar.add(pbZoomFit);
		CSH.setHelpIDString(pbZoomFit,"toolbars.zoom"); //$NON-NLS-1$

		pbZoomFocus = tbrToolBar.createToolBarButton(LanguageProperties.getString(LanguageProperties.TOOLBARS_BUNDLE, "UIToolBarZoom.zoomFocusNode"), UIImages.get(ZOOM_FOCUS_ICON)); //$NON-NLS-1$
		pbZoomFocus.addActionListener(this);
		pbZoomFocus.setEnabled(true);
		tbrToolBar.add(pbZoomFocus);
		CSH.setHelpIDString(pbZoomFocus,"toolbars.zoom"); //$NON-NLS-1$

		tbrToolBar.addSeparator();
		
		pbZoomInText = tbrToolBar.createToolBarButton(LanguageProperties.getString(LanguageProperties.TOOLBARS_BUNDLE, "UIToolBarZoom.reduceFontSize"), UIImages.get(TEXT_MINUS_ICON)); //$NON-NLS-1$
		pbZoomInText.addActionListener(this);
		pbZoomInText.setEnabled(true);
		tbrToolBar.add(pbZoomInText);
		CSH.setHelpIDString(pbZoomInText,"toolbars.zoom"); //$NON-NLS-1$

		pbZoomFullText = tbrToolBar.createToolBarButton(LanguageProperties.getString(LanguageProperties.TOOLBARS_BUNDLE, "UIToolBarZoom.returnFontNormal"), UIImages.get(TEXT_FULL_ICON)); //$NON-NLS-1$
		pbZoomFullText.addActionListener(this);
		pbZoomFullText.setEnabled(true);
		tbrToolBar.add(pbZoomFullText);
		CSH.setHelpIDString(pbZoomFullText,"toolbars.zoom"); //$NON-NLS-1$

		pbZoomOutText = tbrToolBar.createToolBarButton(LanguageProperties.getString(LanguageProperties.TOOLBARS_BUNDLE, "UIToolBarZoom.increaseFontSize"), UIImages.get(TEXT_PLUS_ICON)); //$NON-NLS-1$
		pbZoomOutText.addActionListener(this);
		pbZoomOutText.setEnabled(true);
		tbrToolBar.add(pbZoomOutText);
		CSH.setHelpIDString(pbZoomOutText,"toolbars.zoom");		 //$NON-NLS-1$
		
		lblTextZoom = new JLabel("0"); //$NON-NLS-1$
		lblTextZoom.setFont(new Font("Dialog", Font.ITALIC, 12)); //$NON-NLS-1$
		lblTextZoom.setToolTipText(LanguageProperties.getString(LanguageProperties.TOOLBARS_BUNDLE, "UIToolBarZoom.fontAdjustment")); //$NON-NLS-1$
		lblTextZoom.setBorder(new EmptyBorder(0,2,0,2));
		tbrToolBar.add(lblTextZoom);
		
		return tbrToolBar;
	}

	/**
	 * Create a new choicbox for zoom options and return the panel it is in.
	 * @return JPanel, the panel holding the new choicebox for the zoom options.
	 */
	private JPanel createZoomChoiceBox() {

		zoomPanel = new JPanel(new BorderLayout());
		CSH.setHelpIDString(zoomPanel,"toolbars.zoom"); //$NON-NLS-1$

		cbZoom = new JComboBox();
		cbZoom.setOpaque(true);
		cbZoom.setEditable(false);
		cbZoom.setEnabled(true);
		cbZoom.setMaximumRowCount(6);
		cbZoom.setFont( new Font("Dialog", Font.PLAIN, 10 )); //$NON-NLS-1$
		initZoomChoiceBox("100%");

		DefaultListCellRenderer zoomRenderer = new DefaultListCellRenderer() {
			public Component getListCellRendererComponent(
   		     	JList list,
   		        Object value,
            	int modelIndex,
            	boolean isSelected,
            	boolean cellHasFocus)     {
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

		cbZoom.setRenderer(zoomRenderer);

		zoomActionListener = new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				if (setChoice) {
					return;
				}

				lastZoom = cbZoom.getSelectedIndex();

				String sValue = (String) cbZoom.getSelectedItem();

				int value = 1;

				if (sValue.equalsIgnoreCase(TXT_ZOOM_TOOLBAR_VIEW_ALL)) {
					onZoomToFit();
				} else if (sValue.equalsIgnoreCase(TXT_ZOOM_TOOLBAR_FOCUS_NODE)) {
					if (!onZoomRefocused()) {
						cbZoom.setSelectedIndex(lastZoom);
                    }
				} else {
					try {
						// get rid of the "%" sign first
						int percent_idx = sValue.indexOf("%");
						value = Integer.parseInt(sValue.substring(0, percent_idx - 0));
					} catch (NumberFormatException nfe) {
						Log.error("zoom-level set to: "
								+ sValue
								+ " but don't know how to handle it - not implemented !");
					}

					if (value > 200 || value <= 10) {
						log.warn("unable to zoom in/out beyond boundary: \"<10\" or \">200\". Current level {} ignoring",
								value);
					} else {
						double selected_zoom = (double) value / 100;

						log.debug("selected {} - setting zoom level to: {} ", sValue,
								selected_zoom);
						onZoomTo(selected_zoom);

					}

				}
			}
		};
        cbZoom.addActionListener(zoomActionListener);

		zoomPanel.add(cbZoom, BorderLayout.WEST);
		return zoomPanel;
	}


	/**
	 * Handles most menu and toolbar action event for this application.
	 *
	 * @param evt the genereated action event to be handled.
	 */
	public void actionPerformed(ActionEvent evt) {

		oParent.setWaitCursor();

		Object source = evt.getSource();

		if (source.equals(pbZoomIn)) {
			onZoomIn();
		}
		else if (source.equals(pbZoomOut)) {
			onZoomOut();
		}
		else if (source.equals(pbZoomFull)) {
			setChoice = true;
			onZoomTo(1.0);
			cbZoom.setSelectedIndex(0);
			setChoice = false;
		}
		else if (source.equals(pbZoomFit)) {
			setChoice = true;
			onZoomToFit();
			cbZoom.setSelectedIndex(4);
			setChoice = false;
		}
		else if (source.equals(pbZoomFocus)) {
			setChoice = true;
			if (!onZoomRefocused())
				cbZoom.setSelectedIndex(lastZoom);
			else
				cbZoom.setSelectedIndex(5);
			setChoice = false;
		} else if (source.equals(pbZoomInText)) {
			onReduceTextSize();
		} else if (source.equals(pbZoomOutText)) {
			onIncreaseTextSize();
		} else if (source.equals(pbZoomFullText)) {
			onReturnTextToActual();
		}

		oParent.setDefaultCursor();
	}
		
	/**
	 * Return the font size to its default 
	 * (To what is stored in the database with current map zoom applied)
	 */
    void onReturnTextToActual() {
		currentTextZoom = 0;
		lblTextZoom.setText(String.valueOf(currentTextZoom));
		Vector views = ProjectCompendium.APP.getAllFrames();
		int count = views.size();
		UIViewFrame viewFrame  = null;
		UIViewPane view = null;
		UIList list = null;
		for (int i=0; i<count; i++) {
			viewFrame = (UIViewFrame)views.elementAt(i);
			if (viewFrame instanceof UIMapViewFrame) {
				view = ((UIMapViewFrame)viewFrame).getViewPane();
				view.onReturnTextToActual();
			} else if (viewFrame instanceof UIListViewFrame) {
				list = ((UIListViewFrame)viewFrame).getUIList();
				list.onReturnTextToActual();
			}
		}
		
		ProjectCompendium.APP.getMenuManager().onReturnTextToActual();				
	}
	
	/**
	 * Increase the currently dislayed font size by one point.
	 * (This does not change the stored value in the database)
	 */
    void onIncreaseTextSize() {
		Vector views = ProjectCompendium.APP.getAllFrames();
		int count = views.size();
		UIViewFrame viewFrame  = null;
		UIViewPane view = null;
		UIList list = null;
		for (int i=0; i<count;i++) {
			viewFrame = (UIViewFrame)views.elementAt(i);
			if (viewFrame instanceof UIMapViewFrame) {
				view = ((UIMapViewFrame)viewFrame).getViewPane();
				view.onIncreaseTextSize();
			} else if (viewFrame instanceof UIListViewFrame) {
				list = ((UIListViewFrame)viewFrame).getUIList();
				list.onIncreaseTextSize();
			}
		}
		
		ProjectCompendium.APP.getMenuManager().onIncreaseTextSize();	
		currentTextZoom++;
		lblTextZoom.setText(String.valueOf(currentTextZoom));
	}
	
	/**
	 * Reduce the currently dislayed font size by one point.
	 * (This does not change the stored value in the database)
	 */
    void onReduceTextSize() {
		Vector views = ProjectCompendium.APP.getAllFrames();
		int count = views.size();
		UIViewFrame viewFrame  = null;
		UIViewPane view = null;
		UIList list = null;
		for (int i=0; i<count;i++) {
			viewFrame = (UIViewFrame)views.elementAt(i);
			if (viewFrame instanceof UIMapViewFrame) {
				view = ((UIMapViewFrame)viewFrame).getViewPane();
				view.onReduceTextSize();
			} else if (viewFrame instanceof UIListViewFrame) {
				list = ((UIListViewFrame)viewFrame).getUIList();
				list.onReduceTextSize();
			}
		}
		
		ProjectCompendium.APP.getMenuManager().onReduceTextSize();
		currentTextZoom--;	
		lblTextZoom.setText(String.valueOf(currentTextZoom));		
	}

	public int getTextZoom() {
		return currentTextZoom;
	}
	
	/**
	 * Updates the menu when a new database project is opened.
	 */
	public void onDatabaseOpen() {
		if (tbrToolBar != null) {
			tbrToolBar.setEnabled(true);
		}
	}

	/**
	 * Updates the menu when the current database project is closed.
	 */
	public void onDatabaseClose() {
		if (tbrToolBar != null) {
			tbrToolBar.setEnabled(false);
		}
	}


	/**
	 * Make sure the choicebox option reflects the current zoom level.
	 * @param scale, the scale to set.
	 */
	private void resetZoomChoiceBox(double scale) {

		String lookup_value = "" + (int) (scale * 100) + "%";

		setChoice = true;
		int default_ix = 0;

		for (int i = 0; i < ZOOM_LEVELS.length; i++) {
			if (ZOOM_LEVELS[i].equals(lookup_value)) {
				default_ix = i;
			}
		}

		cbZoom.setSelectedIndex(default_ix);
		cbZoom.validate();

		setChoice = false;
	}

	/**
	 * initialize zoom checkbox with values
	 * 
	 * @param value
	 *                that will be selected by default i.e. "100%"
	 */
	private void initZoomChoiceBox(String default_level) {
		cbZoom.removeAll();

		setChoice = true;
		int default_ix = 0;

		for (int i = 0; i < ZOOM_LEVELS.length; i++) {
			if (ZOOM_LEVELS[i].equals(default_level)) {
				default_ix = i;
			}
			cbZoom.addItem(ZOOM_LEVELS[i]);
		}


		cbZoom.setSelectedIndex(default_ix);
		cbZoom.setEditable(true);
		cbZoom.validate();

		setChoice = false;

	}

	/**
	 * Reset the zoom choicebox to reflect the current views settings.
	 */
	public void resetZoom() {
		double scale = 1.0;

		UIViewFrame frame = oParent.getCurrentFrame();
		if (frame != null) {
			if (frame instanceof UIMapViewFrame) {
				UIMapViewFrame mapframe = (UIMapViewFrame)frame;
				UIViewPane pane = mapframe.getViewPane();
				
				if (pane!=null) {
					scale = pane.getZoom();
				} else {
					log.warn("Unable to reset zoom... pane not initialized yet");
				}
			}
		}
		resetZoomChoiceBox(scale);
	}
	
	/**
	 * Zoom the current map in
	 */
	public void onZoomIn() {
		UIViewFrame frame = oParent.getCurrentFrame();
		if (frame != null) {
			if (frame instanceof UIMapViewFrame) {
				UIMapViewFrame mapframe = (UIMapViewFrame)frame;				
				double scale = mapframe.onZoomNextIn();
				resetZoomChoiceBox(scale);
				
				// APPLY CURRENT TEXT ZOOM
				int count = ProjectCompendium.APP.getToolBarManager().getTextZoom();
				boolean increase = false;
				if (count > 0) {
					increase = true;
				} else if (count < 0) {
					count = count * -1;
				}
				if (count != 0) {
					UIViewPane pane = null;
					pane = mapframe.getViewPane();
					for (int i=0; i<count; i++) {
						if (pane != null) {
							if (increase) {
								pane.onIncreaseTextSize();
							} else {
								pane.onReduceTextSize();
							}
						}
					}
				}								
			}
		}
	}
	
	/**
	 * Zoom the current map out
	 */
	public void onZoomOut() {
		UIViewFrame frame = oParent.getCurrentFrame();
		if (frame != null) {
			if (frame instanceof UIMapViewFrame) {
				UIMapViewFrame mapframe = (UIMapViewFrame) frame;
				double scale = mapframe.onZoomNextOut();
				resetZoomChoiceBox(scale);

				// APPLY CURRENT TEXT ZOOM
				int count = ProjectCompendium.APP.getToolBarManager().getTextZoom();
				boolean increase = false;
				if (count > 0) {
					increase = true;
				} else if (count < 0) {
					count = count * -1;
				}
				if (count != 0) {
					UIViewPane pane = null;
					pane = mapframe.getViewPane();
					for (int i = 0; i < count; i++) {
						if (pane != null) {
							if (increase) {
								pane.onIncreaseTextSize();
							} else {
								pane.onReduceTextSize();
							}
						}
					}
				}
			}
		}
	}

	/**
	 * Zoom the current map using the given scale.
	 * 
	 * @param scale
	 *                , the scale to zoom to.
	 */
	public void onZoomTo(double scale) {
		UIViewFrame frame = oParent.getCurrentFrame();
		if (frame != null) {
			if (frame instanceof UIMapViewFrame) {
				UIMapViewFrame mapframe = (UIMapViewFrame)frame;
				mapframe.onZoomTo(scale);
				
				// APPLY CURRENT TEXT ZOOM
				int count = ProjectCompendium.APP.getToolBarManager().getTextZoom();
				boolean increase = false;
				if (count > 0) {
					increase = true;
				} else if (count < 0) {
					count = count * -1;
				}
				if (count != 0) {
					UIViewPane pane = null;
					pane = mapframe.getViewPane();
					for (int i=0; i<count; i++) {
						if (pane != null) {
							if (increase) {
								pane.onIncreaseTextSize();
							} else {
								pane.onReduceTextSize();
							}
						}
					}
				}								
			}
		}
	}

	/**
	 * Zoom the current map to fit it all on the visible view.
	 */
	public void onZoomToFit() {
		UIViewFrame frame = oParent.getCurrentFrame();
		if (frame != null) {
			if (frame instanceof UIMapViewFrame) {
				UIMapViewFrame mapframe = (UIMapViewFrame)frame;
				mapframe.onZoomToFit();
				
				// APPLY CURRENT TEXT ZOOM
				int count = ProjectCompendium.APP.getToolBarManager().getTextZoom();
				boolean increase = false;
				if (count > 0) {
					increase = true;
				} else if (count < 0) {
					count = count * -1;
				}
				if (count != 0) {
					UIViewPane pane = null;
					pane = mapframe.getViewPane();
					for (int i=0; i<count; i++) {
						if (pane != null) {
							if (increase) {
								pane.onIncreaseTextSize();
							} else {
								pane.onReduceTextSize();
							}
						}
					}
				}								
			}
		}
	}

	/**
	 * Zoom the current map back to normal and focus on the last selected node.
	 */
	public boolean onZoomRefocused() {
		UIViewFrame frame = oParent.getCurrentFrame();
		if (frame != null) {
			if (frame instanceof UIMapViewFrame) {
				UIMapViewFrame mapframe = (UIMapViewFrame)frame;
				boolean zoomed = mapframe.onZoomRefocused();
				
				// APPLY CURRENT TEXT ZOOM
				int count = ProjectCompendium.APP.getToolBarManager().getTextZoom();
				boolean increase = false;
				if (count > 0) {
					increase = true;
				} else if (count < 0) {
					count = count * -1;
				}
				if (count != 0) {
					UIViewPane pane = null;
					pane = mapframe.getViewPane();
					for (int i=0; i<count; i++) {
						if (pane != null) {
							if (increase) {
								pane.onIncreaseTextSize();
							} else {
								pane.onReduceTextSize();
							}
						}
					}
				}				

				return zoomed;
			}
		}
		return false;
	}

	/**
	 * Enable/disable the toolbar.
	 * @param enabled true to enable, false to disable.
	 */
	public void setEnabled(boolean enabled) {
		//tbrToolBar.setEnabled(enabled);
		
		pbZoomIn.setEnabled(enabled);
		pbZoomOut.setEnabled(enabled);
		pbZoomFull.setEnabled(enabled);
		pbZoomFocus.setEnabled(enabled);
		pbZoomFit.setEnabled(enabled);
		cbZoom.setEnabled(enabled);
	}

	/**
 	 * Does nothing
 	 * @param selected true to enable, false to disable.
	 */
	public void setNodeSelected(boolean selected) {}

	/**
 	 * Does nothing
 	 * @param selected true to enable, false to disable.
	 */
	public void setNodeOrLinkSelected(boolean selected) {}	
	
	/**
	 * Return the ui toolbar object.
	 */
	public UIToolBar getToolBar() {
		return tbrToolBar;
	}
	
	/**
	 * Return true if this toolbar is active by default, or false if it must be switched on by the user.
	 * @return true if the toolbar is active by default, else false.
	 */
	public boolean isActiveByDefault() {
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
