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

package com.compendium.ui.toolbars;

import java.awt.*;
import java.awt.event.*;

import javax.help.*;
import javax.swing.*;

import com.compendium.*;
import com.compendium.ui.*;
import com.compendium.ui.dialogs.*;
import com.compendium.ui.toolbars.system.*;

/**
 * This class manages all the toolbars
 *
 * @author	Michelle Bachler
 * @version	1.0
 */
public class UIToolBarScribble implements IUIToolBar, ActionListener, IUIConstants {

	/** Hint text when scribble pad toggle button should move layer to front.*/
	private final static String	SCRIBBLE_FRONT_TEXT		= "Move Scribbles In Front of Nodes and Links";

	/** Hint text when scribble pad toggle button should move layer to back.*/
	private final static String SCRIBBLE_BACK_TEXT		= "Move Scribbles Behind Nodes and Links";
	
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

	/** The drawing toolbar.*/
	private UIToolBar			tbrToolBar 		= null;

	/** The button to select no drawing tool.*/
	private JRadioButton		pbNoTool			= null;

	/** The button to select the pencil drawing tool.*/
	private JRadioButton		pbPencil			= null;

	/** The button to select the line drawing tool.*/
	private JRadioButton		pbLine				= null;

	/** The button to select the circle drawing tool.*/
	private JRadioButton		pbCircle			= null;

	/** The button to select the square drawing tool.*/
	private JRadioButton		pbSquare			= null;

	/** The button to select the draw colour.*/
	//private JButton				pbDrawColour		= null;

	/** The choicebox with the line thickness options.*/
	private JComboBox 			cbDraw				= null;

	/** The panel for the line thickness choice box.*/
	private JPanel 				drawPanel 			= null;

	/** The action listener for the draw choicebox.*/
	private ActionListener 		drawActionListener 	= null;

	/** The button to save the scribble.*/
	private JButton				pbSaveScribble		= null;

	/** The button to hide/show the scribble layer.*/
	private JButton				pbToggleScribble		= null;

	/** The button to move forward and back the scribble layer, to allow node editing/creation.*/
	private JButton				pbPositionScribble		= null;

	/** The button to clear the contents of the scribble layer.*/
	private JButton				pbClearScribble		= null;
	
	/** The text field to hold the link type colour.*/
	private JTextField				txtColour			= null;	


	/**
	 * Create a new instance of UIToolBarScribble, with the given properties.
	 * @param oManager the IUIToolBarManager that is managing this toolbar.
	 * @param parent the parent frame for the application.
	 * @param nType the unique identifier for this toolbar.
	 */
	public UIToolBarScribble(IUIToolBarManager oManager, ProjectCompendiumFrame parent, int nType) {

		this.oParent = parent;
		this.oManager = oManager;
		this.nType = nType;
		createToolBar(DEFAULT_ORIENTATION);
	}
	
	/**
	 * Create a new instance of UIToolBarScribble, with the given properties.
	 * @param oManager the IUIToolBarManager that is managing this toolbar.
	 * @param parent the parent frame for the application.
	 * @param nType the unique identifier for this toolbar.
	 * @param orientation the orientation of this toolbars ui object.  
	 */
	public UIToolBarScribble(IUIToolBarManager oManager, ProjectCompendiumFrame parent, int nType, int orientation) {

		this.oParent = parent;
		this.oManager = oManager;
		this.nType = nType;
		createToolBar(orientation);
	}

	/**
	 * Create and return the toolbar with the drawing options.
	 * @return UIToolBar the toolbar with all the drawing options.
	 */
	private UIToolBar createToolBar(int orientation) {

		tbrToolBar = new UIToolBar("Draw Toolbar", UIToolBar.NORTHSOUTH);
		tbrToolBar.setOrientation(orientation);
		CSH.setHelpIDString(tbrToolBar,"toolbars.draw");

		pbToggleScribble = tbrToolBar.createToolBarButton("Show/Hide Scribble Pad", UIImages.get(SCRIBBLE_OFF_ICON));
		pbToggleScribble.addActionListener(this);
		pbToggleScribble.setEnabled(true);
		tbrToolBar.add(pbToggleScribble);
		CSH.setHelpIDString(pbToggleScribble,"toolbars.draw");

		pbPositionScribble = tbrToolBar.createToolBarButton(SCRIBBLE_BACK_TEXT, UIImages.get(MOVE_FRONT_ICON));
		pbPositionScribble.addActionListener(this);
		pbPositionScribble.setEnabled(false);
		tbrToolBar.add(pbPositionScribble);
		CSH.setHelpIDString(pbPositionScribble,"toolbars.draw");

		pbSaveScribble = tbrToolBar.createToolBarButton("Save Scribble Pad", UIImages.get(SAVE_ICON));
		pbSaveScribble.addActionListener(this);
		pbSaveScribble.setEnabled(false);
		tbrToolBar.add(pbSaveScribble);
		CSH.setHelpIDString(pbSaveScribble,"toolbars.draw");

		pbClearScribble = tbrToolBar.createToolBarButton("Clear Scribble Pad", UIImages.get(SCRIBBLE_CLEAR_ICON));
		pbClearScribble.addActionListener(this);
		pbClearScribble.setEnabled(false);
		tbrToolBar.add(pbClearScribble);
		CSH.setHelpIDString(pbClearScribble,"toolbars.draw");

		tbrToolBar.addSeparator();

		ButtonGroup group = new ButtonGroup();

		pbNoTool = tbrToolBar.createToolBarRadioButton("Select no tool", UIImages.get(NO_TOOL_ICON));
		pbNoTool.setSelectedIcon(UIImages.get(NO_TOOL_SELECT_ICON));
		pbNoTool.addActionListener(this);
		pbNoTool.setEnabled(false);
		tbrToolBar.add(pbNoTool);
		CSH.setHelpIDString(pbNoTool,"toolbars.draw");
		group.add(pbNoTool);

		pbPencil = tbrToolBar.createToolBarRadioButton("Pencil", UIImages.get(PENCIL_TOOL_ICON));
		pbPencil.setSelectedIcon(UIImages.get(PENCIL_TOOL_SELECT_ICON));
		pbPencil.addActionListener(this);
		pbPencil.setEnabled(false);
		tbrToolBar.add(pbPencil);
		CSH.setHelpIDString(pbPencil,"toolbars.draw");
		group.add(pbPencil);

		pbLine = tbrToolBar.createToolBarRadioButton("Draw Line", UIImages.get(LINE_TOOL_ICON));
		pbLine .setSelectedIcon(UIImages.get(LINE_TOOL_SELECT_ICON));
		pbLine.addActionListener(this);
		pbLine.setEnabled(false);
		tbrToolBar.add(pbLine);
		CSH.setHelpIDString(pbLine,"toolbars.draw");
		group.add(pbLine);

		pbSquare = tbrToolBar.createToolBarRadioButton("Draw Square/Rectangle", UIImages.get(SQUARE_TOOL_ICON));
		pbSquare .setSelectedIcon(UIImages.get(SQUARE_TOOL_SELECT_ICON));
		pbSquare.addActionListener(this);
		pbSquare.setEnabled(false);
		tbrToolBar.add(pbSquare);
		CSH.setHelpIDString(pbSquare,"toolbars.draw");
		group.add(pbSquare);

		pbCircle = tbrToolBar.createToolBarRadioButton("Draw Circle/Oval", UIImages.get(CIRCLE_TOOL_ICON));
		pbCircle .setSelectedIcon(UIImages.get(CIRCLE_TOOL_SELECT_ICON));
		pbCircle.addActionListener(this);
		pbCircle.setEnabled(false);
		tbrToolBar.add(pbCircle);
		CSH.setHelpIDString(pbCircle,"toolbars.draw");
		group.add(pbCircle);

		txtColour = new JTextField();
		txtColour.setBackground(Color.black);
		txtColour.setToolTipText("Select draw colour");
		txtColour.setFont(new Font("Dialog", Font.PLAIN, 8));
		txtColour.setColumns(2);
		txtColour.setEditable(false);
		txtColour.setEnabled(false);
		txtColour.setSize(txtColour.getPreferredSize());
		txtColour.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				int clickCount = e.getClickCount();
				if (clickCount == 1 && txtColour.isEnabled()) {
					UIColorChooserDialog dlg = new UIColorChooserDialog(ProjectCompendium.APP, txtColour.getBackground());
					dlg.setVisible(true);
					Color oColour = dlg.getColour();
					dlg.dispose();
					if (oColour != null) {
						txtColour.setBackground(oColour);
						UIViewFrame viewFrame = oParent.getCurrentFrame();
						if (viewFrame instanceof UIMapViewFrame) {
							UIMapViewFrame frame = (UIMapViewFrame)viewFrame;
							UIViewPane pane = frame.getViewPane();
							UIScribblePad pad = pane.getScribblePad();
							if (pad != null)
								pad.getUI().setColour(oColour);
						}
					}
				}
			}
		});
		tbrToolBar.add(txtColour);
		CSH.setHelpIDString(txtColour,"toolbars.draw");

		//pbDrawColour = tbrToolBar.createToolBarButton("Select draw colour", UIImages.get(BLANK_TOOLBAR_BUTTON));
		//pbDrawColour.setBackground(Color.black);
		//pbDrawColour.addActionListener(this);
		//pbDrawColour.setEnabled(false);
		//tbrToolBar.add(pbDrawColour);
		//CSH.setHelpIDString(pbDrawColour,"toolbars.draw");

		tbrToolBar.add( createDrawChoiceBox() );

		return tbrToolBar;
	}

	/**
	 * Create a choicbox for line thickness options and return the panel it is in.
	 * @return JPanel, the panel holding the new choicebox for the line thickness options.
	 */
	private JPanel createDrawChoiceBox() {

		drawPanel = new JPanel(new BorderLayout());
		CSH.setHelpIDString(drawPanel,"toolbars.draw");

		cbDraw = new JComboBox();
        cbDraw.setOpaque(true);
		cbDraw.setEditable(false);
		cbDraw.setEnabled(false);
		cbDraw.setMaximumRowCount(10);
		cbDraw.setFont( new Font("Dialog", Font.PLAIN, 10 ));

		cbDraw.addItem(new String("1 px"));
		cbDraw.addItem(new String("2 px"));
		cbDraw.addItem(new String("3 px"));
		cbDraw.addItem(new String("4 px"));
		cbDraw.addItem(new String("5 px"));
		cbDraw.addItem(new String("6 px"));
		cbDraw.addItem(new String("7 px"));
		cbDraw.addItem(new String("8 px"));
		cbDraw.addItem(new String("9 px"));
		cbDraw.addItem(new String("10 px"));

		cbDraw.validate();

		cbDraw.setSelectedIndex(0);

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

		cbDraw.setRenderer(drawRenderer);

		drawActionListener = new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
 				int ind = cbDraw.getSelectedIndex();

				if (ind == 0)
					setScribbleThickness(1);
				else if (ind == 1)
					setScribbleThickness(2);
				else if (ind == 2)
					setScribbleThickness(3);
				else if (ind == 3)
					setScribbleThickness(4);
				else if (ind == 4)
					setScribbleThickness(5);
				else if (ind == 5)
					setScribbleThickness(6);
				else if (ind == 6)
					setScribbleThickness(7);
				else if (ind == 7)
					setScribbleThickness(8);
				else if (ind == 8)
					setScribbleThickness(9);
				else if (ind == 9)
					setScribbleThickness(10);
         	}
		};
        cbDraw.addActionListener(drawActionListener);

		drawPanel.add(new JLabel(" "), BorderLayout.WEST);
		drawPanel.add(cbDraw, BorderLayout.CENTER);
		return drawPanel;
	}
	
	/**
	 * Update the look and feel of all the toolbars
	 */
	public void updateLAF() {
		
		pbPencil.setIcon(UIImages.get(PENCIL_TOOL_ICON));
		//pbNoTool.setIcon(UIImages.get(NO_TOOL_ICON));
		pbLine.setIcon(UIImages.get(LINE_TOOL_ICON));
		pbCircle.setIcon(UIImages.get(CIRCLE_TOOL_ICON));
		pbSquare.setIcon(UIImages.get(SQUARE_TOOL_ICON));
		pbSaveScribble.setIcon(UIImages.get(SAVE_ICON));


		if ((pbPositionScribble.getToolTipText()).equals(SCRIBBLE_FRONT_TEXT)) {
			pbPositionScribble.setIcon(UIImages.get(MOVE_BACK_ICON));
		}
		else {
			pbPositionScribble.setIcon(UIImages.get(MOVE_FRONT_ICON));
		}
		if (isScribblePadOn()) {
			pbToggleScribble.setIcon(UIImages.get(SCRIBBLE_ON_ICON));
		}
		else {
			pbToggleScribble.setIcon(UIImages.get(SCRIBBLE_OFF_ICON));
		}

		pbClearScribble.setIcon(UIImages.get(SCRIBBLE_CLEAR_ICON));

		if (tbrToolBar != null)
			SwingUtilities.updateComponentTreeUI(tbrToolBar);
	}
		
	/**
	 * Handles most menu and toolbar action event for this application.
	 *
	 * @param evt the genereated action event to be handled.
	 */
	public void actionPerformed(ActionEvent evt) {

		oParent.setWaitCursor();

		Object source = evt.getSource();

		if (source.equals(pbClearScribble)) {
			UIViewFrame viewFrame = oParent.getCurrentFrame();
			if (viewFrame instanceof UIMapViewFrame) {
				UIMapViewFrame frame = (UIMapViewFrame)viewFrame;
				UIViewPane pane = frame.getViewPane();
				UIScribblePad pad = pane.getScribblePad();
				if (pad != null && pad.isVisible()) {
					pane.clearScribblePad();
				}
			}
		}
		else if (source.equals(pbSaveScribble)) {
			UIViewFrame viewFrame = oParent.getCurrentFrame();
			if (viewFrame instanceof UIMapViewFrame) {
				UIMapViewFrame frame = (UIMapViewFrame)viewFrame;
				UIViewPane pane = frame.getViewPane();
				UIScribblePad pad = pane.getScribblePad();
				if (pad != null && pad.isVisible()) {
					pane.saveScribblePad();
				}
			}
		}
		else if (source.equals(pbToggleScribble)) {
			onToggleScribble();
		}
		else if (source.equals(pbPositionScribble)) {
			onPositionScribble();
		}
		/*else if (source.equals(pbNoTool)) {
			onSelectNoTool();
		}*/
		else if (source.equals(pbPencil)) {
			onSelectPencil();
		}
		else if (source.equals(pbLine)) {
			UIViewFrame viewFrame = oParent.getCurrentFrame();
			if (viewFrame instanceof UIMapViewFrame) {
				UIMapViewFrame frame = (UIMapViewFrame)viewFrame;
				UIViewPane pane = frame.getViewPane();
				UIScribblePad pad = pane.getScribblePad();
				pad.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
				if (pad != null)
					pad.getUI().setTool(UIScribblePad.LINE);
			}
		}
		else if (source.equals(pbCircle)) {
			UIViewFrame viewFrame = oParent.getCurrentFrame();
			if (viewFrame instanceof UIMapViewFrame) {
				UIMapViewFrame frame = (UIMapViewFrame)viewFrame;
				UIViewPane pane = frame.getViewPane();
				UIScribblePad pad = pane.getScribblePad();
				pad.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
				if (pad != null)
					pad.getUI().setTool(UIScribblePad.OVAL);
			}
		}
		else if (source.equals(pbSquare)) {
			UIViewFrame viewFrame = oParent.getCurrentFrame();
			if (viewFrame instanceof UIMapViewFrame) {
				UIMapViewFrame frame = (UIMapViewFrame)viewFrame;
				UIViewPane pane = frame.getViewPane();
				UIScribblePad pad = pane.getScribblePad();
				pad.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
				if (pad != null)
					pad.getUI().setTool(UIScribblePad.RECTANGLE);
			}
		}
		/*else if (source.equals(pbDrawColour)) {
			UIColorChooserDialog dlg = new UIColorChooserDialog(ProjectCompendium.APP, pbDrawColour.getBackground());
			dlg.setVisible(true);
			Color oColour = dlg.getColour();
			dlg.dispose();
			if (oColour != null) {
				pbDrawColour.setBackground(oColour);
				UIViewFrame viewFrame = oParent.getCurrentFrame();
				if (viewFrame instanceof UIMapViewFrame) {
					UIMapViewFrame frame = (UIMapViewFrame)viewFrame;
					UIViewPane pane = frame.getViewPane();
					UIScribblePad pad = pane.getScribblePad();
					if (pad != null)
						pad.getUI().setColour(oColour);
				}
			}
		}*/
		
		oParent.setDefaultCursor();
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
	 * Is scribble pad on.
	 * @return boolean, true if on, else false.
 	 */
	public boolean isScribblePadOn() {
		UIViewFrame viewFrame = oParent.getCurrentFrame();
		if (viewFrame instanceof UIMapViewFrame) {
			UIMapViewFrame frame = (UIMapViewFrame)viewFrame;
			UIViewPane pane = frame.getViewPane();
			UIScribblePad pad = pane.getScribblePad();
			if (pad != null && pad.isVisible()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Toggle scribble pad on and off.
 	 */
	public void onToggleScribble() {
		UIViewFrame viewFrame = oParent.getCurrentFrame();
		if (viewFrame instanceof UIMapViewFrame) {
			UIMapViewFrame frame = (UIMapViewFrame)viewFrame;
			UIViewPane pane = frame.getViewPane();
			UIScribblePad pad = pane.getScribblePad();
			if (pad == null) {
				pbToggleScribble.setIcon(UIImages.get(SCRIBBLE_ON_ICON));
				oParent.getMenuManager().setScribblePadActive(true);
				pane.showScribblePad();
				onSelectPencil();
				setDrawToolBarPaintEnabled(true);
			}
			if (pad != null && pad.isVisible()) {
				pbToggleScribble.setIcon(UIImages.get(SCRIBBLE_OFF_ICON));
				oParent.getMenuManager().setScribblePadActive(false);
				pane.hideScribblePad();
				setDrawToolBarPaintEnabled(false);
			}
			else if (pad != null && !pad.isVisible()) {
				pbToggleScribble.setIcon(UIImages.get(SCRIBBLE_ON_ICON));
				oParent.getMenuManager().setScribblePadActive(true);
				pane.showScribblePad();
				onSelectPencil();
				setDrawToolBarPaintEnabled(true);
			}
		}
	}

	/**
	 * Position scribble pad at the bacl or front of node layer.
 	 */
	public void onPositionScribble() {
		UIViewFrame viewFrame = oParent.getCurrentFrame();
		if (viewFrame instanceof UIMapViewFrame) {
			UIMapViewFrame frame = (UIMapViewFrame)viewFrame;
			UIViewPane pane = frame.getViewPane();
			UIScribblePad pad = pane.getScribblePad();
			if (pad == null || !pad.isVisible()) {
				return;
			}

			String text = pbPositionScribble.getToolTipText();
			if (text.equals(SCRIBBLE_BACK_TEXT)) {
				pbPositionScribble.setToolTipText(SCRIBBLE_FRONT_TEXT);
				pbPositionScribble.setIcon(UIImages.get(MOVE_BACK_ICON));
				pane.moveBackScribblePad();
				pad.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			}
			else if (text.equals(SCRIBBLE_FRONT_TEXT)) {
				pbPositionScribble.setToolTipText(SCRIBBLE_BACK_TEXT);
				pbPositionScribble.setIcon(UIImages.get(MOVE_FRONT_ICON));
				pane.moveForwardScribblePad();
				pad.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
			}
		}
	}	

 	/**
	 * Set the scribble pad line thickness to the given integer.
	 * @param nThickness, the line thickness for the scribble layer.
	 */
	private void setScribbleThickness(int nThickness) {
		UIViewFrame viewFrame = oParent.getCurrentFrame();
		if (viewFrame instanceof UIMapViewFrame) {
			UIMapViewFrame frame = (UIMapViewFrame)viewFrame;
			UIViewPane pane = frame.getViewPane();
			UIScribblePad pad = pane.getScribblePad();
			if (pad != null) {
				pad.getUI().setThickness(nThickness);
			}
		}
	}

	/**
	 * Select the arrow (pointer).
 	 */
	private void onSelectNoTool() {
		if (!pbNoTool.isSelected())
			pbNoTool.setSelected(true);

		UIViewFrame viewFrame = oParent.getCurrentFrame();
		if (viewFrame instanceof UIMapViewFrame) {
			UIMapViewFrame frame = (UIMapViewFrame)viewFrame;
			UIViewPane pane = frame.getViewPane();
			UIScribblePad pad = pane.getScribblePad();
			if (pad != null) {
				pad.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
				pad.getUI().setTool(UIScribblePad.NO_TOOL);
			}
		}
	}

	/**
	 * Select the pencil tool.
 	 */
	private void onSelectPencil() {
		if (!pbPencil.isSelected())
			pbPencil.setSelected(true);

		UIViewFrame viewFrame = oParent.getCurrentFrame();
		if (viewFrame instanceof UIMapViewFrame) {
			UIMapViewFrame frame = (UIMapViewFrame)viewFrame;
			UIViewPane pane = frame.getViewPane();
			UIScribblePad pad = pane.getScribblePad();
			if (pad != null) {
				pad.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
				pad.getUI().setTool(UIScribblePad.PENCIL);
			}
		}
	}

	/**
	 * Enable/disable the drawing toolbar items.
	 * @param enabled, true to enable, false to disable.
	 */
	public void setDrawToolBarPaintEnabled(boolean enabled) {

		pbPositionScribble.setEnabled(enabled);
		pbSaveScribble.setEnabled(enabled);
		pbClearScribble.setEnabled(enabled);
		//pbNoTool.setEnabled(enabled);
		pbSquare.setEnabled(enabled);
		pbCircle.setEnabled(enabled);
		pbPencil.setEnabled(enabled);
		pbLine.setEnabled(enabled);
		txtColour.setEnabled(enabled);
		pbPencil.setSelected(true);
		cbDraw.setEnabled(enabled);
		if (!enabled)
			cbDraw.setSelectedIndex(0);
		else {
			setScribbleThickness(cbDraw.getSelectedIndex() + 1);
		}
		//pbNoTool.setSelected(true);
	}

	/**
	 * Enable/disable the toolbar.
	 * @param enabled true to enable, false to disable.
	 */
	public void setEnabled(boolean enabled) {
		tbrToolBar.setEnabled(enabled);
		if (enabled) {
			UIViewFrame viewFrame = oParent.getCurrentFrame();
			if (viewFrame instanceof UIMapViewFrame) {
				UIMapViewFrame frame = (UIMapViewFrame)viewFrame;
				UIViewPane pane = frame.getViewPane();
				UIScribblePad pad = pane.getScribblePad();
				if (pad != null && pad.isVisible()) {
					pbToggleScribble.setIcon(UIImages.get(SCRIBBLE_ON_ICON));
					pbPencil.setSelected(true);
					setDrawToolBarPaintEnabled(true);
				}
				else if (pad == null || (pad != null && !pad.isVisible()) ) {
					pbToggleScribble.setIcon(UIImages.get(SCRIBBLE_OFF_ICON));
					setDrawToolBarPaintEnabled(false);
				}
			}
		}
		else {

		}
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
