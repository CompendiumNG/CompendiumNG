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

import java.util.*;
import java.io.*;

import java.awt.*;
import java.awt.Container;
import java.awt.Color;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.tree.*;
import javax.swing.event.*;
import javax.swing.text.Document;
import javax.swing.border.*;

import com.compendium.core.*;
import com.compendium.core.ICoreConstants;
import com.compendium.core.datamodel.Link;

import com.compendium.*;
import com.compendium.ui.*;
import com.compendium.ui.dialogs.UINodeContentDialog;
import com.compendium.ui.linkgroups.*;

/**
 * This dialog allows users to change settings for one or more links.
 *
 * @author	Mohammed Sajid Ali / Michelle Bachler
 */
public class UILinkEditDialog extends UIDialog implements ActionListener {

	/** Holds all the link groups and thier link types.*/
	private static DefaultMutableTreeNode oLinkTypeTopTreeNode = null;

	/** The pane for this dialog's content.*/
	private Container		oContentPane		= null;

	/** The UILink reference for the link to change setting for.*/
	public UILink			oUILink				= null;

	/** The Link object to chagen setting for.*/
	public Link				oLink				= null;

	/** The choicebox listing the arrow head options.*/
	private JComboBox		cbArrows			= null;

	/** Holds the link author.*/
	public JLabel			lblAuthor2			= null;

	/** Holds the link creation date.*/
	public JLabel			lblCreated2			= null;

	/** Holds the link id number.*/
	public JLabel			lblId2				= null;

	/** Holds the originating node for this link..*/
	public JLabel			tfFromNode			= null;

	/** Holds the destination node for this link.*/
	public JLabel			tfToNode			= null;

	/** The button to cancel this dialog.*/
	public JButton			pbCancel			= null;

	/** The button to update the new settings to the link/s.*/
	public JButton			pbUpdate			= null;

	/** The height for the dialog.*/
	private int				nHeight				= HEIGHT;

	/** The width for the dialog.*/
	private int				nWidth				= WIDTH;

	/** The curent link type.*/
	private String 			sLinkType			= "";

	/** A list of the links to edit the detais for.*/
	private Enumeration		links 				= null;

	/** The text area to hold the link label.*/
	private UITextArea		txtLabel			= null;

	/** The layout manager used for this dialog.*/
	private GridBagLayout 		gb 					= null;

	/** The constaints used with the layout manager.*/
	private GridBagConstraints 	gc 					= null;

	/** The tree displaying the link groups and types.*/
    private JTree oLinkTypeTree = null;

    /** The tree model to hold the data for the link groups.*/
    private DefaultTreeModel treeModel = null;

	/** The currently selected link type.*/
    private String sLinkTypeId = "";

	/** Displays the link type chosen to assign the the link/links.*/
    private JTextField tfLinkType = null;

	/** The last UILinkType object selected from the tree.*/
	private UILinkType oLinkType = null;

	/**
	 * Constructor.
	 * @param parent, the parent frame for this dialog.
	 * @param uilink com.compendium.ui.UILink, the link to assign settings to.
	 */
	public UILinkEditDialog(JFrame parent, UILink uilink) {

		super(parent, true);
		oUILink = uilink;
		oLink = uilink.getLink();
		sLinkType = oLink.getType();

		setResizable(false);

		setTitle("Link Properties");

		oContentPane = getContentPane();
		oContentPane.setLayout(new BorderLayout());

		showLinkEditDialog();
	}

	/**
	 * Constructor.
	 * @param parent, the parent frame for this dialog.
	 * @param links, the list of links to assign settings to.
	 */
	public UILinkEditDialog(JFrame parent, Enumeration links) {

		super(parent, true);
		this.links = links;

		setResizable(false);

		setTitle("Multiple Link Properties");

		oContentPane = getContentPane();
		oContentPane.setLayout(new BorderLayout());

		showLinkEditDialog();
	}

	/**
	 * Set the top tree node for the link groups and their link types.
	 */
	public static void setLinkTypeTopTreeNode(DefaultMutableTreeNode node) {
		oLinkTypeTopTreeNode = node;
	}

	/**
	 * Return the top tree node for the link groups and their link types.
	 */
    public static DefaultMutableTreeNode getLinkTypeTopTreeNode() {
        return oLinkTypeTopTreeNode;
    }


	/**
	 * Draw the dialog's contents.
	 */
	public void showLinkEditDialog() {

		JPanel centerpanel = new JPanel();
		centerpanel.setBorder(new EmptyBorder(5,5,5,5));

		gb = new GridBagLayout();
		gc = new GridBagConstraints();
		centerpanel.setLayout(gb);
		gc.insets = new Insets(3,3,3,3);
		gc.anchor = GridBagConstraints.WEST;
		gc.weightx=1;
		gc.weighty=1;

		int y=0;

		JLabel lFromNode = new JLabel("From:");
		gc.gridy = y;
		gc.gridx = 0;
		gc.gridwidth=1;
		gb.setConstraints(lFromNode, gc);
		centerpanel.add(lFromNode);

		tfFromNode = new JLabel("");
		gc.gridy = y;
		y++;
		gc.gridx = 1;
		gc.gridwidth=1;
		gb.setConstraints(tfFromNode, gc);
		centerpanel.add(tfFromNode);

		JLabel lblToNode = new JLabel("To:");
		gc.gridy = y;
		gc.gridx = 0;
		gc.gridwidth=1;
		gb.setConstraints(lblToNode, gc);
		centerpanel.add(lblToNode);

		tfToNode = new JLabel("");
		gc.gridy = y;
		y++;
		gc.gridx = 1;
		gc.gridwidth=1;
		gb.setConstraints(tfToNode, gc);
		centerpanel.add(tfToNode);

		//Author
		JLabel lblAuthor = new JLabel("Author:");
		gc.gridy = y;
		gc.gridx = 0;
		gc.gridwidth=1;
		gb.setConstraints(lblAuthor, gc);
		centerpanel.add(lblAuthor);

		lblAuthor2 = new JLabel("");
		gc.gridy = y;
		y++;
		gc.gridx = 1;
		gc.gridwidth=1;
		gb.setConstraints(lblAuthor2, gc);
		centerpanel.add(lblAuthor2);

		//Created
		JLabel lblCreated = new JLabel("Created:");
		gc.gridy = y;
		gc.gridx = 0;
		gc.gridwidth=1;
		gb.setConstraints(lblCreated, gc);
		centerpanel.add(lblCreated);

		lblCreated2 = new JLabel("");
		gc.gridy = y;
		y++;
		gc.gridx = 1;
		gc.gridwidth=1;
		gb.setConstraints(lblCreated2, gc);
		centerpanel.add(lblCreated2);

		//Link ID
		JLabel lblId = new JLabel("Link Id:");
		gc.gridy = y;
		gc.gridx = 0;
		gc.gridwidth=1;
		gb.setConstraints(lblId, gc);
		centerpanel.add(lblId);

		lblId2 = new JLabel("");
		gc.gridy = y;
		y++;
		gc.gridx = 1;
		gc.gridwidth=1;
		gb.setConstraints(lblId2, gc);
		centerpanel.add(lblId2);

		txtLabel = new UITextArea(30, 20);
		if (oUILink != null) {
			txtLabel.setFont(oUILink.getFromNode().getFont());
		}
		else {
		    txtLabel.setFont(ProjectCompendium.APP.labelFont);
		}

		txtLabel.setAutoscrolls(true);
		txtLabel.addFocusListener( new FocusListener() {
        	public void focusGained(FocusEvent e) {
 				txtLabel.setCaretPosition(txtLabel.getCaretPosition());
			}
            public void focusLost(FocusEvent e) {}
		});

		JScrollPane scrollpane2 = new JScrollPane(txtLabel);
		scrollpane2.setPreferredSize(new Dimension(300,50));
		gc.gridy = y;
		y++;
		gc.gridx = 0;
		gc.gridwidth=2;
		gb.setConstraints(scrollpane2, gc);
		centerpanel.add(scrollpane2);

		//Radio button for link arrow options
		JLabel lblArrow = new JLabel("Arrow Settings:");
		gc.gridy = y;
		gc.gridx = 0;
		gc.gridwidth=1;
		gb.setConstraints(lblArrow, gc);
		centerpanel.add(lblArrow);

		createArrowChoiceBox();
		gc.gridy = y;
		y++;
		gc.gridx = 1;
		gb.setConstraints(cbArrows, gc);
		centerpanel.add(cbArrows);

		//Link Types
        JLabel label = new JLabel("Link Type Selected ", SwingConstants.LEFT);
        gc.gridy = y;
        gc.gridx = 0;
        gc.gridwidth = 1;
        gb.setConstraints(label, gc);
        centerpanel.add(label);

        tfLinkType = new JTextField("");
        tfLinkType.setFont(new Font("ARIAL", Font.PLAIN, 12));
		tfLinkType.setColumns(17);
        tfLinkType.setEditable(false);
        gc.gridy = y;
        gc.gridx = 1;
        gc.gridwidth = 1;
        y++;
        gb.setConstraints(tfLinkType, gc);
        centerpanel.add(tfLinkType);

        JPanel oLinkTypeTreePanel = createLinkTypeTreePanel();
        gc.gridy = y;
        gc.gridx = 0;
        gc.gridwidth = 2;
        y++;
        gb.setConstraints(oLinkTypeTreePanel, gc);
        centerpanel.add(oLinkTypeTreePanel);

		oContentPane.add(centerpanel, BorderLayout.CENTER);


		// Update Button
		JPanel buttonpanel = new JPanel();
		buttonpanel.setBorder(new EmptyBorder(5,5,5,5));

		pbUpdate = new JButton("Update");
		pbUpdate.setMnemonic(KeyEvent.VK_U);
		pbUpdate.addActionListener(this);
		pbUpdate.setEnabled(true);
		buttonpanel.add(pbUpdate);

		// Cancel Button
		pbCancel = new JButton("Cancel");
		pbCancel.setMnemonic(KeyEvent.VK_C);
		pbCancel.addActionListener(this);
		buttonpanel.add(pbCancel);

		oContentPane.add(buttonpanel, BorderLayout.SOUTH);

		setLink(oLink);

		pack();
	}


	/**
	 * Create the arrow head choicebox.
	 */
	private JComboBox createArrowChoiceBox() {

		cbArrows = new JComboBox();
        cbArrows.setOpaque(true);
		cbArrows.setEditable(false);
		cbArrows.setEnabled(true);
		cbArrows.setMaximumRowCount(4);
		cbArrows.setFont( new Font("Dialog", Font.PLAIN, 12 ));

		Vector arrows = new Vector(5);
		arrows.insertElementAt("FromTo", 0);
		arrows.insertElementAt("ToFrom", 1);
		arrows.insertElementAt("Both Ways", 2);
		arrows.insertElementAt("No Arrows", 3);
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

		return cbArrows;
	}

	/**
	 * Create the panel with the link group tree.
	 */
    private JPanel createLinkTypeTreePanel(){

        JPanel panel = new JPanel(new BorderLayout());
        oLinkTypeTree = new JTree();
        treeModel = new DefaultTreeModel(UILinkEditDialog.getLinkTypeTopTreeNode());

        //the following line unregister Key event from origianl code - it registers ret key
        oLinkTypeTree.unregisterKeyboardAction(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0));
        oLinkTypeTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        oLinkTypeTree.setBorder(new EmptyBorder(2, 2, 0, 0));
        oLinkTypeTree.setRootVisible(false);
        oLinkTypeTree.setModel(treeModel);
        oLinkTypeTree.setCellRenderer(createTreeRenderer());

        MouseListener ml = new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                TreePath path = oLinkTypeTree.getPathForLocation(e.getX(), e.getY());
                if (path != null) {
                    DefaultMutableTreeNode thenode = (DefaultMutableTreeNode)path.getLastPathComponent();

                    if (thenode.isLeaf()) {
                        UILinkType temp = (UILinkType) thenode.getUserObject();
						oLinkType = temp;
                        sLinkTypeId = temp.getID();
                        tfLinkType.setText(temp.getName());
                    }
                }
            }
        };
        oLinkTypeTree.addMouseListener(ml);

        ActionListener selectAction = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                TreePath path = oLinkTypeTree.getSelectionPath();
                if (path != null) {
                    DefaultMutableTreeNode thenode = (DefaultMutableTreeNode) path.getLastPathComponent();

                    if (thenode.isLeaf()) {
                        UILinkType temp = (UILinkType) thenode.getUserObject();
						oLinkType = temp;
                        sLinkTypeId = temp.getID();
                        tfLinkType.setText(temp.getName());

						if (oUILink != null) {
							UILinkType oldType = ProjectCompendium.APP.oLinkGroupManager.getLinkType(oUILink.getLinkType());
							if (oldType != null && !(oldType.getLabel()).equals(txtLabel.getText()) )
								txtLabel.setText(temp.getLabel());
						}
                    }
                }
            }
        };

        oLinkTypeTree.registerKeyboardAction(selectAction,
                                 KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),
                                 oLinkTypeTree.WHEN_FOCUSED );

        JScrollPane scrollPane = new JScrollPane(oLinkTypeTree);
        scrollPane.setPreferredSize(new Dimension(300, 150));
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    // CREATE AND RETURN A TREE RENDERER //
    private TreeCellRenderer createTreeRenderer() {
        return new LinkTypeTreeRenderer();
    }

    // RENDERS THE TREE //
    private class LinkTypeTreeRenderer extends JLabel implements TreeCellRenderer {

        private Icon leafIcon = null;
        private Icon openIcon = null;
        private Icon closedIcon = null;

        //retrive color scheme from list used in tree selection bg/fg colors
        private Color oSelectedBG = null;
        private Color oSelectedFG = null;

        public LinkTypeTreeRenderer() {
            setOpaque(true);

            // GET ICONS
            DefaultTreeCellRenderer check = new DefaultTreeCellRenderer();
            oSelectedBG = check.getBackgroundSelectionColor();
            oSelectedFG = check.getTextSelectionColor();

            leafIcon = check.getLeafIcon();
            openIcon = check.getOpenIcon();
            closedIcon = check.getClosedIcon();
        }

        public Component getTreeCellRendererComponent(JTree tree,
                            Object value,
                            boolean selected,
                            boolean expanded,
                            boolean leaf,
                            int row,
                            boolean hasFocus) {

            DefaultMutableTreeNode node = (DefaultMutableTreeNode)value;
            String text = "";

            if(selected) {
                setBackground(oSelectedBG);
                setForeground(oSelectedFG);
            }
            else {
                setBackground(tree.getBackground());
                setForeground(tree.getForeground());
            }

			Font font = getFont();
            if (node.getUserObject() instanceof UILinkGroup) {
                UILinkGroup group = (UILinkGroup)node.getUserObject();
                text = group.getName();
            }
            else if (node.getUserObject() instanceof UILinkType) {
                UILinkType type = (UILinkType)node.getUserObject();
                text = type.getName();

				if (!selected)
	                setForeground(type.getColour());
           }

            // YOU CAN'T SELECT A GROUP HEADING!
            if (!leaf) {
                selected = false;
                tree.removeSelectionRow(row);
            }

            Icon icon = null;
            if (leaf)
                icon = leafIcon;
            else if (expanded)
                icon = openIcon;
            else
                icon = closedIcon;

            setIcon(icon);
            setText(text);

            return this;
        }
    }

	/**
	 * Initalize the dialog based on the given link settings.
	 * @param link com.compendium.core.datamodel.Link, the link whose setting to initialize for.
	 */
	public void setLink(Link link) {

		oLink = link;
		if (link != null) {
			try {
				String label = link.getFrom().getLabel();
				if (label.length() > 40){
					label = label.substring(0,35);
					label = label + "...";
				}
				tfFromNode.setText(label);
				label = link.getTo().getLabel();
				if (label.length() > 40){
					label = label.substring(0,35);
					label = label + "...";
				}

				tfToNode.setText(label);
				lblId2.setText(link.getId());
				lblAuthor2.setText(link.getAuthor());
				lblCreated2.setText(UIUtilities.getSimpleDateFormat("dd, MMMM, yyyy h:mm a").format(link.getCreationDate()));
				txtLabel.setText(link.getLabel());

				if (!sLinkType.equals("")) {
					tfLinkType.setText(UILink.getLinkTypeLabel(sLinkType) );

					TreePath path = searchTree(sLinkType);
                    if (path != null){
                        DefaultMutableTreeNode thenode = (DefaultMutableTreeNode)path.getLastPathComponent();
                        oLinkTypeTree.expandPath(path);
                        oLinkTypeTree.setSelectionPath(path);
                    }
				}

				if (oLink != null) {
					int arrow = oLink.getArrow();
					if (arrow == ICoreConstants.ARROW_TO)
						cbArrows.setSelectedIndex(0);
					else if (arrow == ICoreConstants.ARROW_FROM)
						cbArrows.setSelectedIndex(1);
					else if (arrow == ICoreConstants.ARROW_TO_AND_FROM)
						cbArrows.setSelectedIndex(2);
					else if (arrow == ICoreConstants.NO_ARROW)
						cbArrows.setSelectedIndex(3);
				}
			}
			catch (Exception e) {
				ProjectCompendium.APP.displayError("Exception in 'UILinkEditDialog.setLink'"+e.getMessage());
			}
		}
	}

    /**
	 * Search the link type tree for the link type with the given id
	 * @param sType, the id of the link type to find.
	 */
    private TreePath searchTree(String sType) {
        TreePath path = null;
        for (Enumeration e = oLinkTypeTopTreeNode.preorderEnumeration(); e.hasMoreElements();) {
             DefaultMutableTreeNode bob = (DefaultMutableTreeNode)e.nextElement();
             if (bob.isLeaf()){
                 UILinkType type = (UILinkType)bob.getUserObject();
                 if ((type.getID()).equals(sType)) {
                     path = new TreePath(bob.getPath());
                     break;
                 }
             }
         }
         return path;
    }

	/**
	 * Process button pushes.
	 * @param evt, the associated ActionEvent object.
	 */
	public void actionPerformed(ActionEvent evt) {

		Object source = evt.getSource();
		if (source == pbCancel) {
			onCancel();
		}
		else if (source == pbUpdate) {
			if(cbArrows.getSelectedIndex() == 0) {
				if (links != null )
					processLinkArrows( ICoreConstants.ARROW_TO );
				else if (oUILink != null)
					oUILink.updateArrow(ICoreConstants.ARROW_TO);
			}
			else if(cbArrows.getSelectedIndex() == 1) {
				if (links != null )
					processLinkArrows( ICoreConstants.ARROW_FROM );
				else if (oUILink != null)
					oUILink.updateArrow(ICoreConstants.ARROW_FROM);
			}
			else if(cbArrows.getSelectedIndex() == 2) {
				if (links != null )
					processLinkArrows( ICoreConstants.ARROW_TO_AND_FROM );
				else if (oUILink != null)
					oUILink.updateArrow(ICoreConstants.ARROW_TO_AND_FROM);
			}
			else if(cbArrows.getSelectedIndex() == 3) {
				if (links != null )
					processLinkArrows( ICoreConstants.NO_ARROW );
				else if (oUILink != null)
					oUILink.updateArrow(ICoreConstants.NO_ARROW);
			}

			// ADD IN CHANGING LINK TYPE
			if (links != null )
				processLinkTypes( sLinkTypeId );
			else if (oUILink != null) {
				if (!sLinkTypeId.equals(sLinkType)) {
					oUILink.setLinkType( sLinkTypeId );
				}
			}

			// ADD IN CHANGING LINK LABEL
			if (links != null )
				processLabel( (txtLabel.getText()).trim() );
			else if (oUILink != null) {
				if (oLinkType != null && txtLabel.getText().equals("")) {
					oUILink.setText( oLinkType.getLabel() );
				}
				else
					oUILink.setText( (txtLabel.getText()).trim() );
			}

			onCancel();
		}
	}

	/**
	 * Add the new link type setting to all selected links.
	 * @param type, the new link type setting.
	 */
	private void processLinkTypes(String type) {
		while (links != null && links.hasMoreElements()) {
  	 	    UILink link = (UILink) links.nextElement();
			link.setLinkType( type );
		}
	}

	/**
	 * Add the new label to all selected links.
	 * @param sLabel, the new label text to add.
	 */
	private void processLabel(String sLabel) {

		while (links != null && links.hasMoreElements()) {
  	 	    UILink link = (UILink) links.nextElement();

			if (oLinkType != null && sLabel.equals("")) {
				link.setText( oLinkType.getLabel() );
			}
			else
				link.setText( sLabel );
		}
	}

	/**
	 * Add the new arrow head setting to all selected links.
	 * @param arrow, the new arrow head setting.
	 */
	private void processLinkArrows(int arrow) {
		while (links != null && links.hasMoreElements()) {
  	 	    UILink link = (UILink) links.nextElement();
			link.updateArrow( arrow );
		}
	}

	/**
	 * Handle the enter key action. Override superclass to do nothing.
	 */
	public void onEnter() {}
}
