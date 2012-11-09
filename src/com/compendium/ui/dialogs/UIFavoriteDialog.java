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
import java.awt.event.*;
import java.util.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.Document;

import com.compendium.ProjectCompendium;
import com.compendium.core.datamodel.*;
import com.compendium.core.datamodel.services.*;

import com.compendium.ui.*;
import com.compendium.ui.plaf.*;

/**
 * Opens a dialog for deleting favorites.
 *
 * @author	Michelle Bachler
 */
public class UIFavoriteDialog extends UIDialog implements ActionListener {

	/** The scrollp[ane to hold the list of favorites.*/
	private JScrollPane			sp 				= null;

	/** Holds the list of favorite nodes.*/
	private UINavList			lstFavorites	= null;

	/** Holds the data for the list of favorite nodes.*/
	private Vector				oFavorites		= new Vector();

	/** The button to delete the selected nodes.*/
	private UIButton			pbDelete 		= null;

	/** The button to close this dialog withou any action.*/
	private UIButton			pbClose 		= null;

	/** The button to open the relevant help.*/
	private UIButton			pbHelp	 		= null;

	/** The mainpanel holding the contents of this dialog.*/
	private JPanel 				mainpanel		= null;

	/** The parent frame for this dialog.*/
	private JFrame				oParent 		= null;

	/** The id of the current user.*/
	private String 				sUserID			= null;

	/** The FavoriteService object to use to access the database.*/
	private FavoriteService		favserv			= null;

	/** The model for the currently open database.*/
	private IModel				oModel			= null;

	/** The session for the current user in the current model.*/
	private PCSession			oSession		= null;

	/*
	 * Constructor.
	 *
	 * @param parent, the parent frame for this dialog.
	 * @param sUserID, the id of the current user.
	 * @param model com.compendium.core.datamodel.IModel, the model for the currently open database.
	 */
	public UIFavoriteDialog(JFrame parent, String sUserID, IModel model) {
		super(parent, true);

		this.sUserID = sUserID;
		this.oParent = parent;
		this.oModel = model;

		favserv = (FavoriteService)oModel.getFavoriteService();
		oSession = oModel.getSession();

		setResizable(false);
		setTitle("Manage Bookmarks");
		getContentPane().setLayout(new BorderLayout());

		drawDialog();

		pack();
	}

	/**
	 * Draw the contents of this dailog.
	 */
	private void drawDialog() {

		mainpanel = new JPanel();
		mainpanel.setBorder(new EmptyBorder(10,10,10,10));

		GridBagLayout gb = new GridBagLayout();
		GridBagConstraints gc = new GridBagConstraints();
		mainpanel.setLayout(gb);
		gc.insets = new Insets(5,5,5,5);
		gc.anchor = GridBagConstraints.WEST;

		JLabel lblFav = new JLabel("Current Bookmarks:");
		gc.gridy = 0;
		gc.gridx = 0;
		gc.gridwidth=2;
		gb.setConstraints(lblFav, gc);
		mainpanel.add(lblFav);

		// Create the list
		lstFavorites = new UINavList(new DefaultListModel());
		lstFavorites.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		//lstFavorites.setCellRenderer(new LabelListCellRenderer());
		lstFavorites.setBackground(Color.white);

		// Enable tool tips.
	    ToolTipManager.sharedInstance().registerComponent(lstFavorites);

        updateFavoriteData();

		DefaultListCellRenderer listRenderer = new DefaultListCellRenderer() {

			public Component getListCellRendererComponent(
   		     	JList list,
   		        Object value,
            	int modelIndex,
            	boolean isSelected,
            	boolean cellHasFocus)
            {

				Favorite next = (Favorite)value;
				String sLabel = next.getLabel();
				int nType = next.getType();

				int index = sLabel.indexOf("&&&");
				String sViewLabel = "";
				String sNodeLabel = "";
				String hint = "";
				if (index != -1) {
					sViewLabel = sLabel.substring(0, index);
					sNodeLabel = sLabel.substring(index+3);
					hint = sNodeLabel+" ( "+sViewLabel+" )";
				} else {
					sNodeLabel = sLabel;
					hint = sNodeLabel;					
				}

				if (nType > -1) {
					String sViewID = next.getViewID();
					if (sViewID == null || sViewID.equals("")) {
						setIcon(UINode.getNodeImageSmall(nType));
					} else {
						setIcon(UIImages.getReferenceIcon(IUIConstants.REFERENCE_INTERNAL_SM_ICON));						
					}
				}

				setText( sNodeLabel );
				setToolTipText(hint);

 		 		if (isSelected) {
					setBackground( list.getSelectionBackground() );
					setForeground( list.getSelectionForeground() );
				}
				else {
					setBackground( list.getBackground() );
					setForeground( list.getForeground() );
				}

				return this;
			}
		};

		lstFavorites.setCellRenderer(listRenderer);

		JScrollPane sp = new JScrollPane(lstFavorites);
		sp.setPreferredSize(new Dimension(250,200));
		gc.gridy = 1;
		gc.gridx = 0;
		gb.setConstraints(sp, gc);
		mainpanel.add(sp);

		UIButtonPanel oButtonPanel = new UIButtonPanel();

		pbDelete = new UIButton("Delete");
		pbDelete.setMnemonic(KeyEvent.VK_D);
		pbDelete.addActionListener(this);
		getRootPane().setDefaultButton(pbDelete);
		oButtonPanel.addButton(pbDelete);

		pbClose = new UIButton("Close");
		pbClose.setMnemonic(KeyEvent.VK_C);
		pbClose.addActionListener(this);
		oButtonPanel.addButton(pbClose);

		pbHelp = new UIButton("Help");
		pbHelp.setMnemonic(KeyEvent.VK_H);
		ProjectCompendium.APP.mainHB.enableHelpOnButton(pbHelp, "menus.favorite", ProjectCompendium.APP.mainHS);
		oButtonPanel.addHelpButton(pbHelp);

		getContentPane().add(mainpanel, BorderLayout.CENTER);
		getContentPane().add(oButtonPanel, BorderLayout.SOUTH);
	}

	/**
	 * Updates the list of favorites with those of the current user.
	 */
	public void updateFavoriteData() {

		oFavorites.removeAllElements();
		DefaultListModel listModel = new DefaultListModel();

		Vector vtTempFavorites = null;
		Vector vtOldFavorites = new Vector();
		
		try { vtTempFavorites = favserv.getFavorites(oSession, sUserID); }
		catch(Exception io) {
			System.out.println("Could not retrieve bookmarks from the database due to: \n"+io.getMessage());
		}

		if (vtTempFavorites != null) {
			int count = vtTempFavorites.size();
			Favorite fav = null;
			for (int i=0; i < count; i++) {
				fav = (Favorite)vtTempFavorites.elementAt(i);
				if (fav.getViewID() != null) {
					oFavorites.addElement(fav);				
					listModel.addElement(fav);
				} else {
					vtOldFavorites.addElement(fav);
				}
			}
			
			count = vtOldFavorites.size();
			for (int i=0; i<count; i++) {
				fav = (Favorite)vtOldFavorites.elementAt(i);
				oFavorites.addElement(fav);				
				listModel.addElement(fav);			
			}
		}

		lstFavorites.setModel(listModel);
		lstFavorites.validate();
		lstFavorites.repaint();
	}

	/**
	 * Process a button push.
	 * @param evt, the associated ActionEvent.
	 */
	public void actionPerformed(ActionEvent evt) {
		Object source = evt.getSource();

		if(source.equals(pbDelete)) {
			onDelete();
		}
		else if(source.equals(pbClose)) {
			onCancel();
		}
	}

	/**
	 * Process the request to delete selected favorites.
	 */
	private void onDelete() {
		Object[] favs = lstFavorites.getSelectedValues();
		int count = favs.length;

		if (count == 0) {
			ProjectCompendium.APP.displayError("Please select a Bookmark to delete");
			return;
		}
		
		Vector vtFavorites = new Vector(count);
		for(int i=0; i<count; i++) {
			Favorite next = (Favorite)favs[i];
			vtFavorites.add(next);
		}
			
		ProjectCompendium.APP.deleteFavorites(vtFavorites);
		
		updateFavoriteData();
	}
}
