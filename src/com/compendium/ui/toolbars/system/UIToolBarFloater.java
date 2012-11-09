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

import java.util.*;
import java.lang.*;
import java.io.*;

import java.awt.*;
import java.awt.event.*;

import javax.help.CSH;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;

import com.compendium.ProjectCompendium;


/**
 * UIToolBarFloater defines a dialog to display a floating toolbar in.
 *
 * @author	Michelle Bachler
 */
public class UIToolBarFloater extends JDialog implements ActionListener {

	/** The main conatiner of the dialog. */
	private Container			oContentPane 	= null;

	/** The parent Frame the dialog will be drawn in. */
	private JFrame				oParent			= null;

	/** The toolbar manager the flaoting toolbar is responsible to.*/
	private IUIToolBarManager	oManager		= null;

	/** The toolbar placed in this flaoting toolbar dialog.*/
	private UIToolBar			oToolBar		= null;

	/** The toolbar type.*/
	private int					nType			= -1;

	/** The allowed docable orientations of this toolbar.*/
	private int 				nDockableOrientation = -1;

	/** The button to dock the toolbar on the north.*/
	private JButton				upButton		= null;

	/** The button to dock the toolbar on the south.*/
	private JButton				downButton		= null;

	/** The button to dock the toolbar on the west.*/
	private	JButton				leftButton		= null;

	/** The button to dock the toolbar on the east.*/
	private JButton				rightButton		= null;
	
	/** The choicebox for the node label font size.*/
	private JComboBox 			cbRows			= null;

	/** The panel for the font size choice box.*/
	private JPanel 				rowsPanel 		= null;
	
	/** the row that the toolbar was in before being floated.*/
	private int					nRow			= 0;

	/** The main panel the contents is drawn in.*/
	protected JPanel 			mainPanel		= null;

	
	
	/**
	 * Initializes and sets up this toolbar floating dialog palette.
	 */
	public UIToolBarFloater(IUIToolBarManager manager, UIToolBar bar, int type, int row) {

		super(manager.getToolBarFloatFrame(), false);

		JFrame parent = manager.getToolBarFloatFrame();

	  	this.oParent = parent;
		this.oManager = manager;
		this.oToolBar = bar;
		this.nType = type;
		this.nRow = row;

		// REGISTER SELF WITH MANAGER
		oManager.addFloatingToolBar(this);

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent evt) {
				onCancel();
			}
		});

		mainPanel = new JPanel();
		setContentPane(mainPanel);

		Action actionEnter = new CreateAction(KeyEvent.VK_ENTER, InputEvent.CTRL_MASK);
		mainPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, InputEvent.CTRL_MASK),"enter");
		mainPanel.getActionMap().put("enter", actionEnter);

		Action actionEscape = new CreateAction(KeyEvent.VK_ESCAPE, 0);
		mainPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),"escape");
		mainPanel.getActionMap().put("escape", actionEscape);

		Action actionW = new CreateAction(KeyEvent.VK_W, InputEvent.CTRL_MASK);
		mainPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_W, InputEvent.CTRL_MASK),"w");
		mainPanel.getActionMap().put("w", actionW);

		// WHATEVER ORIENTATION IT MAY HAVE BEEN, IT NEEDS TO BE HORIZONTAL NOW
		bar.setOrientation(SwingConstants.HORIZONTAL);

		setTitle(bar.getName());

		oContentPane = getContentPane();
		oContentPane.setLayout(new BorderLayout());

		upButton = new JButton();
		upButton.setIcon(UIToolBarImages.get(UIToolBarImages.TOOLBAR_DOWN_SOUTH_ICON));
		upButton.setToolTipText("Dock toolbar to the top");
		upButton.setMargin(new Insets(0,0,0,0));
		upButton.addActionListener(this);
		upButton.setFocusPainted(false);

		leftButton = new JButton();
		leftButton.setIcon(UIToolBarImages.get(UIToolBarImages.TOOLBAR_DOWN_EAST_ICON));
		leftButton.setToolTipText("Dock toobar to the left");
		leftButton.setMargin(new Insets(0,0,0,0));
		leftButton.addActionListener(this);
		leftButton.setFocusPainted(false);

		rightButton = new JButton();
		rightButton.setIcon(UIToolBarImages.get(UIToolBarImages.TOOLBAR_DOWN_WEST_ICON));
		rightButton.setToolTipText("Dock toobar to the right");
		rightButton.setMargin(new Insets(0,0,0,0));
		rightButton.addActionListener(this);
		rightButton.setFocusPainted(false);

		downButton = new JButton();
		downButton.setIcon(UIToolBarImages.get(UIToolBarImages.TOOLBAR_DOWN_NORTH_ICON));
		downButton.setToolTipText("Dock toolbar to the bottom");
		downButton.setMargin(new Insets(0,0,0,0));
		downButton.addActionListener(this);
		downButton.setFocusPainted(false);

		nDockableOrientation = bar.getDockableOrientation();

		if (nDockableOrientation == UIToolBar.NORTHSOUTH) {
			leftButton.setEnabled(false);
			rightButton.setEnabled(false);
		}
		else if (nDockableOrientation == UIToolBar.EASTWEST) {
			upButton.setEnabled(false);
			downButton.setEnabled(false);
		}

		GridLayout grid = new GridLayout(2,2);
		grid.setVgap(0);
		grid.setHgap(0);
		JPanel buttonPanel = new JPanel(grid);
		buttonPanel.setBorder(new EmptyBorder(0,0,0,5));
 		buttonPanel.add(upButton);
		buttonPanel.add(downButton);
		buttonPanel.add(leftButton);
		buttonPanel.add(rightButton);

		JPanel oControlPanel = new JPanel(new BorderLayout());
		oControlPanel.setBorder(new BevelBorder(BevelBorder.RAISED));
		oControlPanel.add(createRowChoiceBox(), BorderLayout.WEST);
		oControlPanel.add(buttonPanel, BorderLayout.EAST);

		oContentPane.add(oControlPanel, BorderLayout.WEST);
		oContentPane.add(oToolBar, BorderLayout.CENTER);

		pack();
		setResizable(false);
	}

	/**
	 * Create a choicbox for node label size face setting.
	 * @return JPanel, the panel holding the new choicebox for the node label size font face.
	 */
	private JPanel createRowChoiceBox() {

		BorderLayout layout = new BorderLayout();
		layout.setHgap(0);
		layout.setVgap(0);		
		rowsPanel = new JPanel(layout);
		CSH.setHelpIDString(rowsPanel,"toolbars.format");

 	 	Integer[] sizes = {new Integer(1), new Integer(2), new Integer(3), new Integer(4)};

		cbRows = new JComboBox(sizes);
		cbRows.setOpaque(true);
		cbRows.setEditable(false);
		cbRows.setEnabled(true);
		cbRows.setMaximumRowCount(10);
		cbRows.setFont( new Font("Dialog", Font.PLAIN, 10 ));		
				
		cbRows.validate();

		cbRows.setSelectedIndex(nRow);

		DefaultListCellRenderer fontSizeRenderer = new DefaultListCellRenderer() {
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

				setText( "row "+((Integer) value).toString()+" ");
				return this;
			}
		};

		cbRows.setRenderer(fontSizeRenderer);

		rowsPanel.add(new JLabel(" "), BorderLayout.WEST);		
        rowsPanel.add(cbRows, BorderLayout.CENTER);
        rowsPanel.add(new JLabel(" "), BorderLayout.EAST);     
        
        cbRows.setSize(new Dimension(cbRows.getWidth()/2, cbRows.getHeight()));
		return rowsPanel;
	}		
	
	/**
	 * Process a button pushed event.
	 *
	 * @param ActionEvent evt, the event object.
	 */
	public void actionPerformed(ActionEvent evt) {

		Object source = evt.getSource();

		// UNREGISTER SELF WITH MANAGER
		oManager.removeFloatingToolBar(this);

		if (source.equals(upButton)) {
			nRow = ((Integer)cbRows.getSelectedItem()).intValue()-1;
			UIToolBarController topController = oManager.getTopToolBarController();
			topController.addToolBar(oToolBar, nType, true, true, true, nRow);
			setVisible(false);
			dispose();
		}
		else if (source.equals(leftButton)) {
			nRow = ((Integer)cbRows.getSelectedItem()).intValue()-1;			
			UIToolBarController leftController = oManager.getLeftToolBarController();
			leftController.addToolBar(oToolBar, nType, true, true, true, nRow);
			setVisible(false);
			dispose();
		}
		else if (source.equals(rightButton)) {
			nRow = ((Integer)cbRows.getSelectedItem()).intValue()-1;			
			UIToolBarController rightController = oManager.getRightToolBarController();
			rightController.addToolBar(oToolBar, nType, true, true, true, nRow);
			setVisible(false);
			dispose();
		}
		else if (source.equals(downButton)) {
			nRow = ((Integer)cbRows.getSelectedItem()).intValue()-1;
			UIToolBarController bottomController = oManager.getBottomToolBarController();
			bottomController.addToolBar(oToolBar, nType, true, true, true, nRow);
			setVisible(false);
			dispose();
		}
	}

	/**
	 * Creates an abstract action to handle enter and escape key presses.
	 */
	private class CreateAction extends AbstractAction {

		private int		nKey = 0;
		private int		nModifier = 0;

		public CreateAction(int key, int modifier) {
	    	super();
			nKey = key;
			nModifier = modifier;
		}

		public void actionPerformed(ActionEvent evt) {

			if (nKey == KeyEvent.VK_ENTER && nModifier == Event.CTRL_MASK ) {
				onEnter();
			}
			else if ( (nKey == KeyEvent.VK_ESCAPE && nModifier == 0)
						|| (nKey == KeyEvent.VK_W && nModifier == Event.CTRL_MASK) ) {
				onCancel();
			}
		}
	}

	/**
	 * Produce and XML representation of the data in this object and return.
	 * @return String, XML representation of this object.
	 */
	public String toXML() {

		Point loc = getLocation();

		StringBuffer data = new StringBuffer(100);

		data.append("<floater type=\""+nType+"\"");
		data.append(" name=\""+oToolBar.getName()+"\"");
		data.append(" isVisible=\"true\"");
		data.append(" wasVisible=\"true\"");
		data.append(" xPos=\""+loc.x+"\" yPos=\""+loc.y+"\"");
		data.append(" row=\""+nRow+"\">");
		data.append("</floater>\n");

		return data.toString();
	}

	/**
	 * Handle the enter key action. Override superclass to do nothing
	 */
	public void onEnter() {}

	/**
	 * Close the floating toolbar dialog and unregister self with manager.
	 */
	public void onCancel() {

		// UNREGISTER SELF WITH MANAGER
		oManager.removeFloatingToolBar(this);

		if (nDockableOrientation == UIToolBar.EASTWEST) {
			UIToolBarController left = oManager.getLeftToolBarController();
			left.addToolBar(oToolBar, nType, true, true, true, 0);
		}
		else {
			UIToolBarController top = oManager.getTopToolBarController();
			top.addToolBar(oToolBar, nType, true, true, true, 0);
		}

		setVisible(false);
		dispose();
	}
}
