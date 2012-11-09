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
import java.awt.datatransfer.*;
import java.awt.event.*;

import java.applet.Applet;
import java.io.*;
import java.util.*;
import java.net.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;
import javax.swing.table.*;

import com.compendium.*;
import com.compendium.core.datamodel.*;
import com.compendium.core.datamodel.services.*;
import com.compendium.ui.*;

/**
 * Display a table of data and a message.
 *
 * @author	Mohammed Sajid Ali / Michelle Bachler
 */
// TO DO: re-write this dialog using GridBagLayout manager instead of pixel positioning.
public class UIMessageDialog extends UIDialog implements ActionListener, IUIConstants {


	/** The height of this dialog.*/
	private final static int	HEIGHT = 400;

	/** The width of this dialog.*/
	private final static int	WIDTH = 600;

	/** The height to draw the buttons at.*/
	private final static int	PBHEIGHT = 24;

	/** The width to draw the buttons at.*/
	private final static int	PBWIDTH = 64;

	/** The left offset to use when laying out this dialog.*/
	private final static int	nLeftOffset = 10;

	/** The top offset to use when laying out this dialog.*/
	private final static int	nTopOffset = 10;

	/** Holds the message to display.*/
	private String				sMessage = "";

	/** Displays the message.*/
	private JTextArea			taMessages = null;

	/** The button to close this dialog.*/
	private JButton				pbClose = null;

	/** The button to copy selected contents to the clipboard.*/
	private JButton				pbCopy = null;

	/** The button to select all the elements in the table.*/
	private JButton				pbSelectAll = null;

	/** The main content pane for this dialog.*/
	private Container			oContentPane = null;

	/** The table of contents to display.*/
	private JTable				jtTable = null;


	/**
	 * Constructor. Initializes and sets up the dialog.
	 * @param parent, the parent frame for this dialog.
	 * @param message, the message to display.
	 */
	public UIMessageDialog(JFrame parent, String message) {

		// for non-modal dialog. Dont need to block user from doing other operations
		super(parent, true);

		sMessage = message;

		// set title and background
		setTitle("Warning Messages ");
		setResizable(true);

		oContentPane = getContentPane();
		oContentPane.setLayout(new BorderLayout());

		// Add select all button
		//pbSelectAll = new UIButton("Select All Rows", JButton.RAISED | JButton.THICK);
		//oContentPane.add(pbSelectAll);
		//pbSelectAll.addActionListener(this);
		//pbSelectAll.setBounds(nLeftOffset, HEIGHT-90-50, PBWIDTH+60, PBHEIGHT);

		JPanel buttonPanel = new JPanel();
		GridBagLayout gb = new GridBagLayout();
		GridBagConstraints gc = new GridBagConstraints();

		gc.gridx = 0;
		gc.gridy = 0;
		gc.gridwidth = 2;
		buttonPanel.setLayout(gb);
		JLabel lblMessages = new JLabel("(Copy all contents of the above table to Clipboard with column delimiter '#')");
		gb.setConstraints(lblMessages, gc);
		buttonPanel.add(lblMessages);
		lblMessages.setBounds(nLeftOffset, HEIGHT-90, HEIGHT, 24);
		lblMessages.setFont(new Font("Dialog", Font.PLAIN, 9));

		// Add Copy to Clipboard button
		gc.gridx = 0;
		gc.gridy = 1;
		gc.gridwidth = 1;
		pbCopy = new UIButton("Copy To Clipboard");
		gb.setConstraints(pbCopy, gc);
		buttonPanel.add(pbCopy);
		pbCopy.addActionListener(this);
		pbCopy.setBounds(nLeftOffset, HEIGHT-64, PBWIDTH+60, PBHEIGHT);

		gc.gridx = 1;
		pbClose = new UIButton("Close");
		gb.setConstraints(pbClose, gc);
		buttonPanel.add(pbClose);
		pbClose.addActionListener(this);
		getRootPane().setDefaultButton(pbClose);
		pbClose.setBounds(WIDTH/2-PBWIDTH/2, HEIGHT-64, PBWIDTH+60, PBHEIGHT);

		//maintains an invisible field of textarea which carries the whole table information
		//in an delimiter limited fashion for easy import into MS Access & etc
		taMessages = new JTextArea(sMessage);
		taMessages.setEditable(false);
		oContentPane.add(buttonPanel, BorderLayout.SOUTH);

		pack();
		setSize(WIDTH, HEIGHT);
		setResizable(true);
	}

	/**
	 * Create a table in this dialog from the given data.
	 *
	 * @param rowData, a Vector holding the row data for this table.
	 * @param tableNames, a Vector holding the column headers for this table.
	 */
	public void addTable(Vector rowData, Vector tableNames) {

		DefaultTableModel model = null;
		try {
			model = new DefaultTableModel(rowData, tableNames);
		}
		catch(Exception ex) {
			//inform the user that there are no links
			JDialog dialog = new JDialog();
			JOptionPane option = new JOptionPane("No Links found for the node");
			dialog = option.createDialog(ProjectCompendium.APP.getContentPane(),"Linking information..");
			dialog.setModal(false);
			dialog.setVisible(true);
		}

		jtTable = new JTable(model);

		JScrollPane scrollpane = new JScrollPane(jtTable);
		oContentPane.add(scrollpane);

		jtTable.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if(e.getClickCount() == 2) {
					JLabel lblMessages = new JLabel("Messages:");
					oContentPane.add(lblMessages);
					lblMessages.setBounds(nLeftOffset,nTopOffset+HEIGHT/2,80,24);
					lblMessages.setFont(new Font("Dialog", Font.BOLD, 10));

					JScrollPane scrollpane = new JScrollPane(taMessages);
					oContentPane.add(scrollpane);
					scrollpane.setBounds(nLeftOffset,nTopOffset+HEIGHT/2+24,WIDTH-2*nLeftOffset,HEIGHT/2-150);
					scrollpane.setFont(new Font("Dialog", Font.BOLD, 9));
				}
			}
		});

		if (jtTable.getRowCount() > 0)
			jtTable.setRowSelectionInterval(0,0);

		scrollpane.setBounds(nLeftOffset,nTopOffset+24,WIDTH-2*nLeftOffset,HEIGHT/2-34);
		scrollpane.setFont(new Font("Dialog", Font.BOLD, 9));
		scrollpane.setBackground(Color.white);

		setSize(WIDTH, HEIGHT);
		setResizable(false);
	}


	/**
	 * Handle button push events.
	 * @param event, the associated ActionEvent.
	 */
	public void actionPerformed(ActionEvent event) {

		Object source = event.getSource();
		// Handle button events
		if (source == pbClose) {
			onCancel();
		}
		else if (source == pbSelectAll) {
			jtTable.selectAll();
			taMessages.selectAll();
		}
		else if (source == pbCopy) {
			jtTable.selectAll();
			taMessages.selectAll();
			taMessages.copy();
		}
	}

	/**
	 * Set the message displayed in this panel.
	 * @param message, the message to display.
	 */
	public void setMessage(String message) {
		sMessage += "\n ----------------" + UIUtilities.getSimpleDateFormat("dd, MMMM, yyyy h:mm a").format(new Date()).toString() + "-----------------\n" + message;
		taMessages.setText(sMessage);
	}
}
