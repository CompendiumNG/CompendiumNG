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

package com.compendium.ui.panels;

import java.util.*;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.SQLException;
import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;
import javax.swing.text.Document;
import javax.swing.text.Keymap;

import com.compendium.core.datamodel.*;
import com.compendium.core.datamodel.services.ViewService;
import com.compendium.core.ICoreConstants;

import com.compendium.*;
import com.compendium.ui.*;
import com.compendium.meeting.*;
import com.compendium.ui.dialogs.UIColorChooserDialog;
import com.compendium.ui.dialogs.UINodeContentDialog;
import com.compendium.ui.dialogs.UIImageSizeDialog;
import com.compendium.io.ShorthandParser;
import com.compendium.core.CoreUtilities;

/**
 * This class draws the panel though which you edit the node label, details pages
 * and on some node types the image and external reference.
 *
 * @author	Mohammed Sajid Ali / Michelle Bachler
 */
public class UINodeEditPanel extends JPanel implements ActionListener, ItemListener, DocumentListener, IUIConstants {

	/** The last director browsed to when looking for external references.*/
	private static String lastFileDialogDir = ""; //$NON-NLS-1$

	/** The last director browsed to when looking for images.*/
	private static String lastFileDialogDir2 = ""; //$NON-NLS-1$

	/** The parent dialog that this panel is in.*/
	private UINodeContentDialog oParentDialog	= null;

	/** The scrollpane for the node detail textarea.*/
	private JScrollPane		scrollpane			= null;

	/** The scrollpane for the label text area.*/
	private JScrollPane		scrollpane2			= null;

	/** The text field for the external reference of url.*/
	private JTextField		txtReference		= null;

	/** The textfield for the node images.*/
	private JTextField		txtImage			= null;

	/** The textfield for the background images.*/
	private JTextField		txtBackgroundImage		= null;

	/** The display label for showing the background color.*/
	private JTextField		txtBackgroundColorDisplay	= null;

	/** The text area to hold the node label.*/
	private UITextArea		txtLabel			= null;

	/** The text are to hold node details pages.*/
	private UITextArea		txtDetail			= null;

	/** The label for the reference text field.*/
	private JLabel			lblReference		= null;

	/** The label for the image text field.*/
	private JLabel			lblImage			= null;

	/** The label for the background color text field.*/
	private JLabel			lblBackgroundColor	= null;


	/** The label for the current page of detail.*/
	private JLabel			pageLabel			= null;

	/** The page for the modification date of the current page of detail.*/
	private JLabel			modLabel			= null;

	/** The button to launch the current reference.*/
	private UIButton		pbExecute			= null;

	/** The button to open a file browser for the reference field.*/
	private UIButton		pbBrowse			= null;

	/** The button to open a file browser for the image field.*/
	private UIButton		pbBrowse2			= null;

	/** The button to open a file browser for the background field.*/
	private UIButton		pbBrowse3			= null;

	/** The button to copy the reference file to the database.*/
	private UIButton		pbDatabase			= null;

	/** The button to copy the image file to that database.*/
	private UIButton		pbDatabase2			= null;

	/** The button to copy the background file to the database.*/
	private UIButton		pbDatabase3			= null;

	/** The button to open the current image in an external application.*/
	private UIButton		pbView				= null;

	/** The button to open the current background in an external application.*/
	private UIButton		pbView2				= null;

	/** The button to save all node chagens and close the parent dialog.*/
	private UIButton		pbOK				= null;

	/** The button to cancel changes and close the parnt dialog.*/
	private UIButton		pbCancel			= null;

	/** The button to open the relevant help.*/
	public UIButton			pbHelp				= null;

	/** The button to say they want the icon image displayed as a thumbnail.*/
	public JRadioButton			pbThumbNail		= null;
	
	/** The button to say they want the icon image displayed at its actual size.*/
	public JRadioButton			pbActualSize	= null;

	/** The button to say they want the icon image displayed at a specified size.*/
	public JRadioButton			pbSpecifiedSize	= null;

	// TOOLBAR BUTTONS
	/** The toolbar button to move forward a page of detail text.*/
	private JButton		pbForward			= null;

	/** The toolbar button to move back a page of detail text.*/
	private JButton		pbBack				= null;

	/** The toolbar button to move to the first page of detail text.*/
	private JButton		pbFirst				= null;

	/** The toolbar button to open the dialog to set the image size.*/
	private JButton		pbSize				= null;

	/** The toolbar button to move to move to the last page of detail text.*/
	private JButton		pbLast				= null;

	/** The toolbar button to create a new page of detail text.*/
	private JButton		pbNew				= null;

	/** The toolbar button to delete the current page of detail text.*/
	private JButton		pbDelete			= null;

	/** The toolbar button to turn the current page of detail text into nodes.*/
	private JButton		pbToNodes			= null;

	/** The toolbar button to copy the selected detail text to the clipboard.*/
	private JButton		pbCopy				= null;

	/** The toolbar button to cut the selected detail text.*/
	private JButton		pbCut				= null;

	/** The toolbar button to paste from the clipbaord into the current detail page.*/
	private JButton		pbPaste				= null;

	/** The toolbar button to print the current page of detail - NOT USED.*/
	private JButton		pbPrint				= null;

	/** The file browser dialog for looking for references.*/
	private	JFileChooser	fdgBrowse			= null;
	
	/** The file browser dialog for looking for image files.*/
	private	JFileChooser	fdgBrowse2			= null;
	
	/** The file browser dialog for looking for background files.*/
	private	JFileChooser	fdgBrowse3			= null;
	
	/** The Document for the label text area.*/
	private Document		oLabelDoc			= null;

	/** The Document for the detail text area.*/
	private Document		oDetailDoc			= null;

	/** The Document for the reference field.*/
	private Document		oRefDoc				= null;

	/** The Document for the image field.*/
	private Document		oImageDoc			= null;

	/** The Document for the background image field.*/
	private Document		oBackgroundDoc		= null;

	/** Indicates if the label contents has been changed.*/
	private boolean			bLabelChange		= false;

	/** Indicates if the detail contents has been changed.*/
	private boolean			bDetailChange		= false;

	/** Indicates if the reference field contents has been chnaged.*/
	private boolean			bRefChange			= false;

	/** Indicates if the image field contents has been changes.*/
	private boolean			bImageChange		= false;

	/** Indicates if the background image field contents has been changes.*/
	private boolean			bBackgroundImageChange	= false;

	/** Indicates if the background color field contents has been changes.*/
	private boolean			bBackgroundColorChange	= false;

	/** The current image path.*/
	private String 			sImage				= ""; //$NON-NLS-1$

	/** The current background image path.*/
	private String 			sBackgroundImage			= ""; //$NON-NLS-1$

	/** The current background color path.*/
	private int 			nBackgroundColor			= Color.white.getRGB();

	/** The current reference path.*/
	private String 			sReference			= ""; //$NON-NLS-1$

	/** A List of NodeDetailPage object for this node.*/
	private Vector			detailPages			= new Vector();

	/** Indicates the current page number being viewed.*/
	private int				currentPage			= 0;

	/** Holds the type of this node.*/
	private int				nodeType			= 0;

	/** The central panel holding the label and detail  areas etc.*/
	private JPanel				centerpanel			= null;

	/** Holds the date panel and detail scrollpane.*/
	private JPanel				editPanel			= null;

	/** Holds the current page number and modification date.*/
	private JPanel				infopanel			= null;

	/** The date panel used to edit the modification date/time of a page of detail.*/
	private UITimePanel			datePanel			= null;

	/** The layout manager used for this panel.*/
	private GridBagLayout 		gb 					= null;

	/** The constaints used with the layout manager.*/
	private GridBagConstraints 	gc 					= null;

	/** Indicates if this is the first time the detail text area has recieved the focus.*/
	private boolean	firstFocus	= true;

	/** Used to format the modification date.*/
	private static SimpleDateFormat sdf = new SimpleDateFormat("d MMM, yyyy h:mm a"); //$NON-NLS-1$

	/** The current node this is the contents for - if in a map.*/
	private UINode			oUINode			= null;

	/** The current node data this is the contents for.*/
	private NodeSummary		oNode			= null;

	/** The user author name of the current user */
	private String 			sAuthor 		= ""; //$NON-NLS-1$
	
	/** Actual Image size.*/
	private Dimension 		oActualImageSize		= null;

	/** Last Saved Image size.*/
	private Dimension 		oLastImageSize		= null;
	
	/** Dialog for user to enter specific image display size.*/
	private UIImageSizeDialog dlg				= null;
	
	private Font font							= null;
	
	/** The referrence to the colour chooser dialog. */
	private UIColorChooserDialog oColorChooserDialog = null;
	
	/**
	 * Constructor.
	 * @param parent, the parent frame for the dialog this panel is in.
	 * @param uinode com.compendium.ui.UINode, the current node this is the contents for - if in a map.
 	 * @param tabbedPane, the parent dialog this panel is in.
	 */
	public UINodeEditPanel(JFrame parent, UINode uinode, UINodeContentDialog tabbedPane) {
		super();
		oParentDialog = tabbedPane;
		oUINode = uinode;
		
		this.sAuthor = ProjectCompendium.APP.getModel().getUserProfile().getUserName();
		
		initEditPanel(uinode.getNode());
		
		//set the updated node retireved from the db to the old node
		oUINode.setNode(oNode);
	}

	/**
	 * Constructor.
	 * @param parent, the parent frame for the dialog this panel is in.
	 * @param node com.compendium.core.datamodel.NodeSummary, the current node this is the contents for.
 	 * @param tabbedPane, the parent dialog this panel is in.
	 */
	public UINodeEditPanel(JFrame parent, NodeSummary node, UINodeContentDialog tabbedPane) {
		super();
		
		this.sAuthor = ProjectCompendium.APP.getModel().getUserProfile().getUserName();
		
		oParentDialog = tabbedPane;

		initEditPanel(node);
	}

	/**
	 * Initialize and draw this panel's contents.
	 * @param node com.compendium.core.datamodel.NodeSummary, the current node to draw the contents for.
	 */
	private void initEditPanel(NodeSummary node) {

		oNode = node;

		// set title and background
		setLayout(new BorderLayout());

		Vector pages = null;
	    String sAuthor = ProjectCompendium.APP.getModel().getUserProfile().getUserName();

		try {
			pages = node.getDetailPages(sAuthor);
		}
		catch(Exception ex) {
			ProjectCompendium.APP.displayError("Error: (UINodeEditPanel.initEditPanel) "+LanguageProperties.getString(LanguageProperties.PANELS_BUNDLE, "UINodeEditPanel.message")+"\n\n"+ex.getLocalizedMessage()); //$NON-NLS-1$
		}

		if (pages != null) {
			int count = pages.size();
			for (int i=0; i<count; i++) {
				detailPages.addElement(pages.elementAt(i));
			}
		}

		showNodeEditPanel();
		addComponentListener(new ComponentAdapter() {});
	}

	/**
	 * Draw the contents of the panel.
	 */
	private void showNodeEditPanel() {

		String sUserHomeDir = System.getProperty("user.home");	//mlb: This speeds up Map Contents Window creation enormously! //$NON-NLS-1$
		centerpanel = new JPanel();
		centerpanel.setBorder(new EmptyBorder(5,5,5,5));

		gb = new GridBagLayout();
		gc = new GridBagConstraints();
		centerpanel.setLayout(gb);
		gc.insets = new Insets(3,3,3,3);
		gc.anchor = GridBagConstraints.NORTHWEST;
		gc.weightx=1;
		gc.weighty=1;

		JPanel labelPanel = new JPanel(new BorderLayout());

		font = ProjectCompendiumFrame.currentDefaultFont;
		int scale = ProjectCompendium.APP.getToolBarManager().getTextZoom();
		font = new Font(font.getName(), font.getStyle(), font.getSize()+ scale);		


		// WHEN NODE LEVEL FONT SETTING IS INTRODUCED, WILL NEED TO DO THIS
		/*if (oUINode != null) {
			double scale = oUINode.getScale();
			if (scale != 0.0 && scale != 1.0) {
				font = oUINode.getFont();
				Point p1 = UIUtilities.scalePoint(font.getSize(), font.getSize(), scale);
				Font font2 = new Font(font.getName() , font.getStyle(), p1.x);
				font = font2;
			}
		}*/

		txtLabel = new UITextArea(50, 20);
		txtLabel.setFont(font);
		txtLabel.setAutoscrolls(true);
		txtLabel.addFocusListener( new FocusListener() {
        	public void focusGained(FocusEvent e) {
				txtLabel.setCaretPosition(txtLabel.getCaretPosition());
 			}
            public void focusLost(FocusEvent e) {}
		});
		txtLabel.addKeyListener( new KeyAdapter() {
        	public void keyPressed(KeyEvent e) {
        		int keyCode = e.getKeyCode();
        		int modifiers = e.getModifiers();
     			if (modifiers == java.awt.Event.SHIFT_MASK && keyCode == KeyEvent.VK_TAB) {
     				txtDetail.requestFocus();
     				txtDetail.setCaretPosition(0);
     			}
 			}
		});

		if (oNode.getId().equals(ProjectCompendium.APP.getInBoxID())) {
			txtLabel.setEditable(false);
		}
		
		scrollpane2 = new JScrollPane(txtLabel);
		scrollpane2.setPreferredSize(new Dimension(500,70));
		labelPanel.add(scrollpane2, BorderLayout.CENTER);

		gc.gridy = 0;
		gc.gridx = 0;
		gc.gridwidth=5;
		gc.weighty=100;
		gc.fill = GridBagConstraints.BOTH;
		gb.setConstraints(labelPanel, gc);
		centerpanel.add(labelPanel);

		gc.weighty=1;
		gc.fill = GridBagConstraints.NONE;

		oLabelDoc = txtLabel.getDocument();
		oLabelDoc.addDocumentListener(this);

		pageLabel = new JLabel();
		datePanel = new UITimePanel(LanguageProperties.getString(LanguageProperties.PANELS_BUNDLE, "UINodeEditPanel.entered")+":"); //$NON-NLS-1$
		modLabel = new JLabel();

		infopanel = new JPanel();

		JPanel inner = new JPanel();
		inner.setBackground(Color.white);
		inner.add(pageLabel);
		infopanel.add(inner);

		inner = new JPanel();
		inner.setBackground(Color.white);
		inner.add(modLabel);
		infopanel.add(inner);

		NodeDetailPage page = (NodeDetailPage)detailPages.elementAt(0);
		createInfo(page);
		gc.gridy = 1;
		gc.gridx = 0;
		gc.gridwidth = 5;
		gb.setConstraints(infopanel, gc);
		centerpanel.add(infopanel);

		JToolBar tool = createToolBar();
		gc.gridy = 2;
		gc.gridx = 0;
		gc.gridwidth = 5;
		gc.fill = GridBagConstraints.NONE;
		gc.anchor = GridBagConstraints.WEST;
		gb.setConstraints(tool, gc);
		centerpanel.add(tool);

		txtDetail = new UITextArea(50, 50);
		txtDetail.setFont(font);

		txtDetail.setAutoscrolls(true);
		txtDetail.addFocusListener( new FocusListener() {
        	public void focusGained(FocusEvent e) {
				if (firstFocus) {
	 				txtDetail.setCaretPosition(txtDetail.getText().length());
					firstFocus = false;
				}
			}
            public void focusLost(FocusEvent e) {}
		});
		txtDetail.addKeyListener( new KeyAdapter() {
        	public void keyPressed(KeyEvent e) {
        		int keyCode = e.getKeyCode();
        		int modifiers = e.getModifiers();
     			if (modifiers == java.awt.Event.SHIFT_MASK && keyCode == KeyEvent.VK_TAB) {
     				txtLabel.requestFocus();
     				txtLabel.setCaretPosition(0);
     			}
 			}
		});

		scrollpane = new JScrollPane(txtDetail);
		scrollpane.setPreferredSize(new Dimension(500,200));

		editPanel = new JPanel(new BorderLayout());
		editPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));

		editPanel.add(datePanel, BorderLayout.NORTH);
		editPanel.add(scrollpane, BorderLayout.CENTER);

		gc.gridy = 3;
		gc.gridx = 0;
		gc.gridwidth=5;
		gc.weighty=100;
		gc.fill = GridBagConstraints.BOTH;
		gb.setConstraints(editPanel, gc);
		centerpanel.add(editPanel);

		gc.weighty=1;

		oDetailDoc = txtDetail.getDocument();
		oDetailDoc.addDocumentListener(this);

		int y=4;

		nodeType = oNode.getType();

		if(nodeType == ICoreConstants.REFERENCE || nodeType == ICoreConstants.REFERENCE_SHORTCUT) {
			gc.fill = GridBagConstraints.NONE;
			gc.anchor = GridBagConstraints.WEST;

			lblReference = new JLabel(LanguageProperties.getString(LanguageProperties.PANELS_BUNDLE, "UINodeEditPanel.ref")+":"); //$NON-NLS-1$
			gc.gridy = y;
			gc.gridx = 0;
			gc.gridwidth = 1;
			gb.setConstraints(lblReference, gc);
			centerpanel.add(lblReference);

			sReference = oNode.getSource();

			txtReference = new JTextField(sReference);
			txtReference.setFont(new Font("Dialog", Font.PLAIN, 12)); //$NON-NLS-1$
			txtReference.setColumns(23);
			txtReference.setMargin(new Insets(2,2,2,2));
			txtReference.setSize(txtReference.getPreferredSize());
			gc.gridy = y;
			gc.gridx = 1;
			gc.fill=GridBagConstraints.HORIZONTAL;
			gc.weightx=4.0;
			gb.setConstraints(txtReference, gc);
			centerpanel.add(txtReference);

			oRefDoc = txtReference.getDocument();
			oRefDoc.addDocumentListener(this);

			// other initializations
			
			pbBrowse = new UIButton("./."); //$NON-NLS-1$
			pbBrowse.setFont(new Font("Dialog", Font.BOLD, 14)); //$NON-NLS-1$
			pbBrowse.setMargin(new Insets(0,0,0,0));
			pbBrowse.setToolTipText(LanguageProperties.getString(LanguageProperties.PANELS_BUNDLE, "UINodeEditPanel.browse")); //$NON-NLS-1$
			pbBrowse.addActionListener(this);
			gc.gridy = y;
			gc.gridx = 2;
			gc.fill=GridBagConstraints.NONE;
			gc.weightx=0.0;
			gc.gridwidth = 1;
			gb.setConstraints(pbBrowse, gc);
			centerpanel.add(pbBrowse);

			pbDatabase = new UIButton(UIImages.get(BACK_ICON)); //$NON-NLS-1$
			pbDatabase.setFont(new Font("Dialog", Font.BOLD, 14)); //$NON-NLS-1$
			pbDatabase.setMargin(new Insets(0,0,0,0));
			pbDatabase.setToolTipText(LanguageProperties.getString(LanguageProperties.PANELS_BUNDLE, "UINodeEditPanel.intodatabase")); //$NON-NLS-1$
			pbDatabase.addActionListener(this);
			gc.gridy = y;
			gc.gridx = 3;
			gc.weightx=0.0;
			gc.fill=GridBagConstraints.NONE;
			gc.gridwidth = 1;
			gb.setConstraints(pbDatabase, gc);
			centerpanel.add(pbDatabase);
			processReferenceChoices(sReference);
			
			pbExecute = new UIButton(LanguageProperties.getString(LanguageProperties.PANELS_BUNDLE, "UINodeEditPanel.launchButton")); //$NON-NLS-1$
			if (sReference == null || sReference.equals("")) //$NON-NLS-1$
				pbExecute.setEnabled(false);
			pbExecute.setMnemonic(LanguageProperties.getString(LanguageProperties.PANELS_BUNDLE, "UINodeEditPanel.launchButtonMnemonic").charAt(0));
			gc.gridy = y;
			gc.gridx = 4;
			gb.setConstraints(pbExecute, gc);
			pbExecute.addActionListener(this);
			centerpanel.add(pbExecute);

			y++;
			
		} else if(View.isMapType(nodeType)) {

			gc.fill = GridBagConstraints.NONE;
			gc.anchor = GridBagConstraints.WEST;
			gc.gridwidth = 1;

			JLabel lblBackgroundColor = new JLabel(LanguageProperties.getString(LanguageProperties.PANELS_BUNDLE, "UINodeEditPanel.backgroundColor")); //$NON-NLS-1$
			gc.gridy = y;
			gc.gridx = 0;
			gb.setConstraints(lblBackgroundColor, gc);
			centerpanel.add(lblBackgroundColor);

			if (oNode instanceof View) {
				ViewLayer layer  = ((View)oNode).getViewLayer();
				if (layer == null) {
					try { ((View)oNode).initializeMembers();
						nBackgroundColor = layer.getBackgroundColor();
					}
					catch(Exception ex) {
						nBackgroundColor = Color.white.getRGB();
					}
				}
				else {
					nBackgroundColor = layer.getBackgroundColor();
				}
			}

			txtBackgroundColorDisplay = new JTextField();
			//txtBackgroundColorDisplay.setBorder(null);
			txtBackgroundColorDisplay.setBackground(new Color(nBackgroundColor));				
			txtBackgroundColorDisplay.setToolTipText(LanguageProperties.getString(LanguageProperties.PANELS_BUNDLE, "UINodeEditPanel.backgroundColorTip")); //$NON-NLS-1$
			txtBackgroundColorDisplay.setFont(new Font("Dialog", Font.PLAIN, 8)); //$NON-NLS-1$
			txtBackgroundColorDisplay.setColumns(2);
			txtBackgroundColorDisplay.setEditable(false);
			txtBackgroundColorDisplay.setSize(txtBackgroundColorDisplay.getPreferredSize());			
			txtBackgroundColorDisplay.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent e) {
					int clickCount = e.getClickCount();
					if (clickCount == 1 && txtBackgroundColorDisplay.isEnabled()) {
						if (oColorChooserDialog != null) {
							oColorChooserDialog.setColour(txtBackgroundColorDisplay.getBackground());
						} else {
							oColorChooserDialog = new UIColorChooserDialog(ProjectCompendium.APP, txtBackgroundColorDisplay.getBackground());
						}
						oColorChooserDialog.setVisible(true);
						Color oColour = oColorChooserDialog.getColour();
						oColorChooserDialog.setVisible(false);
						if (oColour != null) {
							txtBackgroundColorDisplay.setBackground(oColour);	
							int nNew = oColour.getRGB();
							if (nNew != nBackgroundColor) {
								bBackgroundColorChange = true;
								nBackgroundColor = nNew;
							}
						}
					}
				}
			});		
			
			// color chooser
			gc.gridy = y;
			gc.gridx = 1;
			gc.fill=GridBagConstraints.HORIZONTAL;
			gc.weightx=4.0;
			gb.setConstraints(txtBackgroundColorDisplay, gc);
			centerpanel.add(txtBackgroundColorDisplay);

			y++;
			
			gc.fill = GridBagConstraints.NONE;
			gc.anchor = GridBagConstraints.WEST;
			gc.gridwidth = 1;

			JLabel lblBackground = new JLabel(LanguageProperties.getString(LanguageProperties.PANELS_BUNDLE, "UINodeEditPanel.backgroundImage")+":"); //$NON-NLS-1$
			gc.gridy = y;
			gc.gridx = 0;
			gb.setConstraints(lblBackground, gc);
			centerpanel.add(lblBackground);

			if (oNode instanceof View) {
				ViewLayer layer  = ((View)oNode).getViewLayer();
				if (layer == null) {
					try { ((View)oNode).initializeMembers();
					sBackgroundImage = layer.getBackgroundImage();
					}
					catch(Exception ex) {
						sBackgroundImage = ""; //$NON-NLS-1$
					}
				}
				else {
					sBackgroundImage = layer.getBackgroundImage();
				}
			}

			txtBackgroundImage = new JTextField(sBackgroundImage);
			txtBackgroundImage.setFont(new Font("Dialog", Font.PLAIN, 12)); //$NON-NLS-1$
			txtBackgroundImage.setColumns(23);
			txtBackgroundImage.setMargin(new Insets(2,2,2,2));
			txtBackgroundImage.setSize(txtBackgroundImage.getPreferredSize());
			gc.gridy = y;
			gc.gridx = 1;
			gc.fill=GridBagConstraints.HORIZONTAL;
			gc.weightx=4.0;
			gb.setConstraints(txtBackgroundImage, gc);
			centerpanel.add(txtBackgroundImage);

			oBackgroundDoc = txtBackgroundImage.getDocument();
			oBackgroundDoc.addDocumentListener(this);

			// other initializations

			pbBrowse3 = new UIButton("./."); //$NON-NLS-1$
			pbBrowse3.setFont(new Font("Dialog", Font.BOLD, 14)); //$NON-NLS-1$
			pbBrowse3.setMargin(new Insets(0,0,0,0));
			pbBrowse3.setToolTipText(LanguageProperties.getString(LanguageProperties.PANELS_BUNDLE, "UINodeEditPanel.browse")); //$NON-NLS-1$
			pbBrowse3.addActionListener(this);
			gc.gridy = y;
			gc.gridx = 2;
			gc.weightx=0.0;
			gc.fill=GridBagConstraints.NONE;
			gc.gridwidth = 1;
			gb.setConstraints(pbBrowse3, gc);
			centerpanel.add(pbBrowse3);

			pbDatabase3 = new UIButton(UIImages.get(BACK_ICON)); //$NON-NLS-1$
			pbDatabase3.setFont(new Font("Dialog", Font.BOLD, 14)); //$NON-NLS-1$
			pbDatabase3.setMargin(new Insets(0,0,0,0));
			pbDatabase3.setToolTipText(LanguageProperties.getString(LanguageProperties.PANELS_BUNDLE, "UINodeEditPanel.intodatabase")); //$NON-NLS-1$
			pbDatabase3.addActionListener(this);
			gc.gridy = y;
			gc.gridx = 3;
			gc.weightx=0.0;
			gc.fill=GridBagConstraints.NONE;
			gc.gridwidth = 1;
			gb.setConstraints(pbDatabase3, gc);
			centerpanel.add(pbDatabase3);
			processBackgroundChoices(sBackgroundImage);
			
			pbView2 = new UIButton(LanguageProperties.getString(LanguageProperties.PANELS_BUNDLE, "UINodeEditPanel.viewButton")); //$NON-NLS-1$
			if (sBackgroundImage == null || sBackgroundImage.equals("")) //$NON-NLS-1$
				pbView2.setEnabled(false);

			pbView2.setMnemonic(LanguageProperties.getString(LanguageProperties.PANELS_BUNDLE, "UINodeEditPanel.viewButtonMnemonic").charAt(0));
			gc.gridy = y;
			gc.gridx = 4;
			gb.setConstraints(pbView2, gc);
			pbView2.addActionListener(this);
			centerpanel.add(pbView2);

			y++;
		}
				
		if(nodeType == ICoreConstants.REFERENCE || nodeType == ICoreConstants.REFERENCE_SHORTCUT
				|| View.isViewType(nodeType) || 
					View.isShortcutViewType(nodeType)){
			
			gc.fill = GridBagConstraints.NONE;
			gc.anchor = GridBagConstraints.WEST;
			gc.gridwidth = 1;
			lblImage = new JLabel(LanguageProperties.getString(LanguageProperties.PANELS_BUNDLE, "UINodeEditPanel.iconImage")+":"); //$NON-NLS-1$
			gc.gridy = y;
			gc.gridx = 0;
			gb.setConstraints(lblImage, gc);
			centerpanel.add(lblImage);

			sImage = oNode.getImage();

			txtImage = new JTextField(sImage);
			txtImage.setFont(new Font("Dialog", Font.PLAIN, 12)); //$NON-NLS-1$
			txtImage.setColumns(23);
			txtImage.setMargin(new Insets(2,2,2,2));
			txtImage.setSize(txtImage.getPreferredSize());
			gc.gridy = y;
			gc.gridx = 1;
			gc.fill=GridBagConstraints.HORIZONTAL;
			gc.weightx=4.0;
			gb.setConstraints(txtImage, gc);
			centerpanel.add(txtImage);

			oImageDoc = txtImage.getDocument();
			oImageDoc.addDocumentListener(this);

			// other initializations
			pbBrowse2 = new UIButton("./."); //$NON-NLS-1$
			pbBrowse2.setFont(new Font("Dialog", Font.BOLD, 14)); //$NON-NLS-1$
			pbBrowse2.setMargin(new Insets(0,0,0,0));
			pbBrowse2.setToolTipText(LanguageProperties.getString(LanguageProperties.PANELS_BUNDLE, "UINodeEditPanel.browse")); //$NON-NLS-1$
			pbBrowse2.addActionListener(this);
			gc.gridy = y;
			gc.gridx = 2;
			gc.weightx=0.0;
			gc.fill=GridBagConstraints.NONE;
			gc.gridwidth = 1;
			gb.setConstraints(pbBrowse2, gc);
			centerpanel.add(pbBrowse2);

			pbDatabase2 = new UIButton(UIImages.get(BACK_ICON)); //$NON-NLS-1$
			pbDatabase2.setFont(new Font("Dialog", Font.BOLD, 14)); //$NON-NLS-1$
			pbDatabase2.setMargin(new Insets(0,0,0,0));
			pbDatabase2.setToolTipText(LanguageProperties.getString(LanguageProperties.PANELS_BUNDLE, "UINodeEditPanel.intodatabase")); //$NON-NLS-1$
			pbDatabase2.addActionListener(this);
			gc.gridy = y;
			gc.gridx = 3;
			gc.weightx=0.0;
			gc.fill=GridBagConstraints.NONE;
			gc.gridwidth = 1;
			gb.setConstraints(pbDatabase2, gc);
			centerpanel.add(pbDatabase2);
			processImageChoices(sImage);

			pbView = new UIButton(LanguageProperties.getString(LanguageProperties.PANELS_BUNDLE, "UINodeEditPanel.viewButton")); //$NON-NLS-1$
			pbView.setMnemonic(LanguageProperties.getString(LanguageProperties.PANELS_BUNDLE, "UINodeEditPanel.viewButtonMnemonic2").charAt(0));
			gc.gridy = y;
			gc.gridx = 4;
			gb.setConstraints(pbView, gc);
			pbView.addActionListener(this);
			centerpanel.add(pbView);
		
			y++;						
			
			JPanel sizePanel = new JPanel();
			gc.gridy = y;
			gc.gridx = 1;
			gc.gridwidth=2;
			gb.setConstraints(sizePanel, gc);
			centerpanel.add(sizePanel);
			
			pbThumbNail = new JRadioButton(LanguageProperties.getString(LanguageProperties.PANELS_BUNDLE, "UINodeEditPanel.dislaThumbnail")); //$NON-NLS-1$
			pbThumbNail.addItemListener(this);			
			sizePanel.add(pbThumbNail);			

			pbActualSize = new JRadioButton(LanguageProperties.getString(LanguageProperties.PANELS_BUNDLE, "UINodeEditPanel.actualSize")); //$NON-NLS-1$
			pbActualSize.addItemListener(this);
			sizePanel.add(pbActualSize);

			pbSpecifiedSize = new JRadioButton(LanguageProperties.getString(LanguageProperties.PANELS_BUNDLE, "UINodeEditPanel.specifiedSize")); //$NON-NLS-1$
			pbSpecifiedSize.addItemListener(this);
			sizePanel.add(pbSpecifiedSize);			

			ButtonGroup group = new ButtonGroup();
			group.add(pbThumbNail);
			group.add(pbActualSize);
			group.add(pbSpecifiedSize);
			
			pbSize = new UIButton(LanguageProperties.getString(LanguageProperties.PANELS_BUNDLE, "UINodeEditPanel.specifyButton")); //$NON-NLS-1$
			pbSize.setMnemonic(LanguageProperties.getString(LanguageProperties.PANELS_BUNDLE, "UINodeEditPanel.specifyButtonMnemonic").charAt(0)); //$NON-NLS-1$
			pbSize.setToolTipText(LanguageProperties.getString(LanguageProperties.PANELS_BUNDLE, "UINodeEditPanel.specifyButtonTip")); //$NON-NLS-1$
			pbSize.addActionListener(this);		
			
			if (sImage == null || sImage.equals("")) { //$NON-NLS-1$
				pbView.setEnabled(false);
				pbThumbNail.setEnabled(false);
				pbActualSize.setEnabled(false);
				pbSpecifiedSize	.setEnabled(false);			
			}
			
			gc.gridy = y;
			gc.gridx = 4;
			gc.gridwidth=1;
			gb.setConstraints(pbSize, gc);
			centerpanel.add(pbSize);			
		}

		setNode(oNode);

		//reset the flag to false as the label, detail and/or reference info is taken from the DB for the first time
		bLabelChange = false;
		bDetailChange = false;
		bRefChange = false;
		bImageChange = false;
		bBackgroundImageChange = false;
		bBackgroundColorChange = false;
		
		add(centerpanel, BorderLayout.CENTER);
		add(createButtonPanel(), BorderLayout.SOUTH);
	}

	/**
	 * Determine if the background in in database on a file and show appropriate button text etc.
	 */
	private void processBackgroundChoices(String sBackgroundImage) {
		if (LinkedFileDatabase.isDatabaseURI(sBackgroundImage)) {
			pbDatabase3.setEnabled(true);
			pbDatabase3.setIcon(UIImages.get(FORWARD_ICON));
			pbDatabase3.setToolTipText(LanguageProperties.getString(LanguageProperties.PANELS_BUNDLE, "UINodeEditPanel.outofdatabase")); //$NON-NLS-1$
		} else {
			if (CoreUtilities.isFile(sBackgroundImage)) {
				pbDatabase3.setEnabled(true);
				pbDatabase3.setIcon(UIImages.get(BACK_ICON));
				pbDatabase3.setToolTipText(LanguageProperties.getString(LanguageProperties.PANELS_BUNDLE, "UINodeEditPanel.intodatabase")); //$NON-NLS-1$				
			} else {
				pbDatabase3.setEnabled(false);
			}
		}
	}
	
	/**
	 * Determine if the reference in in database on a file and show appropriate button text etc.
	 */
	private void processReferenceChoices(String sRef) {
		if (LinkedFileDatabase.isDatabaseURI(sRef)) {
			pbDatabase.setEnabled(true);
			pbDatabase.setIcon(UIImages.get(FORWARD_ICON));
			pbDatabase.setToolTipText(LanguageProperties.getString(LanguageProperties.PANELS_BUNDLE, "UINodeEditPanel.outofdatabase")); //$NON-NLS-1$
		} else {
			if (CoreUtilities.isFile(sRef)) {
				pbDatabase.setEnabled(true);
				pbDatabase.setIcon(UIImages.get(BACK_ICON));
				pbDatabase.setToolTipText(LanguageProperties.getString(LanguageProperties.PANELS_BUNDLE, "UINodeEditPanel.intodatabase")); //$NON-NLS-1$
			} else {
				pbDatabase.setEnabled(false);
			}
		}
	}

	/**
	 * Determine if the node image in in database on a file and show appropriate button text etc.
	 */
	private void processImageChoices(String sRef) {
		if (LinkedFileDatabase.isDatabaseURI(sRef)) {
			pbDatabase2.setEnabled(true);
			pbDatabase2.setIcon(UIImages.get(FORWARD_ICON));
			pbDatabase2.setToolTipText(LanguageProperties.getString(LanguageProperties.PANELS_BUNDLE, "UINodeEditPanel.outofdatabase")); //$NON-NLS-1$
		} else {
			if (CoreUtilities.isFile(sRef)) {
				pbDatabase2.setEnabled(true);
				pbDatabase2.setIcon(UIImages.get(BACK_ICON));
				pbDatabase2.setToolTipText(LanguageProperties.getString(LanguageProperties.PANELS_BUNDLE, "UINodeEditPanel.intodatabase")); //$NON-NLS-1$
			} else {
				pbDatabase2.setEnabled(false);
			}
		}
	}

	/**
	 * Get the dialog to recalulate the default font and reset it on the text areas.
	 * Used for presentation font changes.
	 */
	public void refreshFont() {
		font = ProjectCompendiumFrame.currentDefaultFont;
		int scale = ProjectCompendium.APP.getToolBarManager().getTextZoom();
		font = new Font(font.getName(), font.getStyle(), font.getSize()+ scale);		

		txtLabel.setFont(font);	
		txtDetail.setFont(font);
				
		repaint();
	}

	/**
	 * Create and return the button panel.
	 */
	private UIButtonPanel createButtonPanel() {

		UIButtonPanel oButtonPanel = new UIButtonPanel();

		pbOK = new UIButton(LanguageProperties.getString(LanguageProperties.PANELS_BUNDLE, "UINodeEditPanel.okButton")); //$NON-NLS-1$
		pbOK.setMnemonic(LanguageProperties.getString(LanguageProperties.PANELS_BUNDLE, "UINodeEditPanel.okButtonMnemonic").charAt(0));
		pbOK.addActionListener(this);
		oButtonPanel.addButton(pbOK);

		pbCancel = new UIButton(LanguageProperties.getString(LanguageProperties.PANELS_BUNDLE, "UINodeEditPanel.cancelButton")); //$NON-NLS-1$
		pbCancel.setMnemonic(LanguageProperties.getString(LanguageProperties.PANELS_BUNDLE, "UINodeEditPanel.cancelButtonMnemonic").charAt(0));
		pbCancel.addActionListener(this);
		oButtonPanel.addButton(pbCancel);

		pbHelp = new UIButton(LanguageProperties.getString(LanguageProperties.PANELS_BUNDLE, "UINodeEditPanel.helpButton")); //$NON-NLS-1$
		pbHelp.setMnemonic(LanguageProperties.getString(LanguageProperties.PANELS_BUNDLE, "UINodeEditPanel.helpButtonMnemonic").charAt(0));
		ProjectCompendium.APP.mainHB.enableHelpOnButton(pbHelp, "node.node_details", ProjectCompendium.APP.mainHS); //$NON-NLS-1$
		oButtonPanel.addHelpButton(pbHelp);

		return oButtonPanel;
	}

	/**
	 * Set the default button for the parent dialog to be this panel's default button.
	 */
	public void setDefaultButton() {
		oParentDialog.getRootPane().setDefaultButton(pbOK);
	}

	/**
	 * Update the panel holding the current page number details for the given page of page.
	 * @param page com.compendium.core.datamodel.NodeDetailPage, the current page being viewed.
	 */
	private void createInfo(NodeDetailPage page) {

		Date creation = page.getCreationDate();
		Date modified = page.getModificationDate();

		pageLabel.setText(LanguageProperties.getString(LanguageProperties.PANELS_BUNDLE, "UINodeEditPanel.detailsPage")+page.getPageNo()+" "+LanguageProperties.getString(LanguageProperties.PANELS_BUNDLE, "UINodeEditPanel.of")+detailPages.size()); //$NON-NLS-1$ //$NON-NLS-2$
		datePanel.setDate(creation.getTime());
		modLabel.setText(LanguageProperties.getString(LanguageProperties.PANELS_BUNDLE, "UINodeEditPanel.lastModified")+":"+sdf.format(modified).toString()); //$NON-NLS-1$
	}

	/**
	 * Creates a button for the toolbar and sets some default properties for each button.
	 * @param label, the label for the toolbar button hint.#
	 * @param the icon to use for this button.
	 */
	private JButton createToolBarButton(String label, ImageIcon icon) {
		JButton btn = new UIButton(icon);
	  	btn.setToolTipText(label);
		btn.setRequestFocusEnabled(false);
		btn.setMargin(new Insets(1,1,1,1));
		return btn;
	}

	/**
	 * Creates and initializes the tool bar.
	 */
	protected JToolBar createToolBar() {

		JToolBar toolBar = new JToolBar();
		toolBar.setFloatable(false);

		pbNew = createToolBarButton(LanguageProperties.getString(LanguageProperties.PANELS_BUNDLE, "UINodeEditPanel.insertNewPage"), UIImages.get(NEW_ICON)); //$NON-NLS-1$
		pbNew.setToolTipText(LanguageProperties.getString(LanguageProperties.PANELS_BUNDLE, "UINodeEditPanel.insertNewPageTip")); //$NON-NLS-1$
		pbNew.addActionListener(this);
		toolBar.add(pbNew);

		toolBar.addSeparator();

		pbFirst = createToolBarButton(LanguageProperties.getString(LanguageProperties.PANELS_BUNDLE, "UINodeEditPanel.firstPage"), UIImages.get(FIRST_ICON)); //$NON-NLS-1$
		pbFirst.setToolTipText(LanguageProperties.getString(LanguageProperties.PANELS_BUNDLE, "UINodeEditPanel.firstPageTip")); //$NON-NLS-1$
		pbFirst.addActionListener(this);
		pbFirst.setEnabled(false);
		toolBar.add(pbFirst);

		pbBack = createToolBarButton(LanguageProperties.getString(LanguageProperties.PANELS_BUNDLE, "UINodeEditPanel.previousPage"), UIImages.get(PREVIOUS_ICON)); //$NON-NLS-1$
		pbBack.setToolTipText(LanguageProperties.getString(LanguageProperties.PANELS_BUNDLE, "UINodeEditPanel.previousPageTip")); //$NON-NLS-1$
		pbBack.addActionListener(this);
		pbBack.setEnabled(false);
		toolBar.add(pbBack);

		pbForward = createToolBarButton(LanguageProperties.getString(LanguageProperties.PANELS_BUNDLE, "UINodeEditPanel.nextPage"), UIImages.get(NEXT_ICON)); //$NON-NLS-1$
		pbForward.setToolTipText(LanguageProperties.getString(LanguageProperties.PANELS_BUNDLE, "UINodeEditPanel.nextPageTip")); //$NON-NLS-1$
		pbForward.addActionListener(this);
		toolBar.add(pbForward);

		pbLast = createToolBarButton(LanguageProperties.getString(LanguageProperties.PANELS_BUNDLE, "UINodeEditPanel.lastPage"), UIImages.get(LAST_ICON)); //$NON-NLS-1$
		pbLast.setToolTipText(LanguageProperties.getString(LanguageProperties.PANELS_BUNDLE, "UINodeEditPanel.lastPageTip")); //$NON-NLS-1$
		pbLast.addActionListener(this);
		toolBar.add(pbLast);

		toolBar.addSeparator();

		pbDelete = createToolBarButton(LanguageProperties.getString(LanguageProperties.PANELS_BUNDLE, "UINodeEditPanel.deletePage"), UIImages.get(DELETE_ICON)); //$NON-NLS-1$
		pbDelete.setToolTipText(LanguageProperties.getString(LanguageProperties.PANELS_BUNDLE, "UINodeEditPanel.deletePageTip")); //$NON-NLS-1$
		pbDelete.addActionListener(this);
		toolBar.add(pbDelete);

		toolBar.addSeparator();

		pbToNodes = createToolBarButton(LanguageProperties.getString(LanguageProperties.PANELS_BUNDLE, "UINodeEditPanel.convertToNodes"), UIImages.get(TONODES_ICON)); //$NON-NLS-1$
		pbToNodes.setToolTipText(LanguageProperties.getString(LanguageProperties.PANELS_BUNDLE, "UINodeEditPanel.convertToNodesTip")); //$NON-NLS-1$
		pbToNodes.addActionListener(this);
		toolBar.add(pbToNodes);

		toolBar.addSeparator();

		pbCut = createToolBarButton(LanguageProperties.getString(LanguageProperties.PANELS_BUNDLE, "UINodeEditPanel.cut"), UIImages.get(CUT_ICON)); //$NON-NLS-1$
		pbCut.setToolTipText(LanguageProperties.getString(LanguageProperties.PANELS_BUNDLE, "UINodeEditPanel.cutTip")); //$NON-NLS-1$
		pbCut.addActionListener(this);
		toolBar.add(pbCut);

		pbCopy = createToolBarButton(LanguageProperties.getString(LanguageProperties.PANELS_BUNDLE, "UINodeEditPanel.copy"), UIImages.get(COPY_ICON)); //$NON-NLS-1$
		pbCopy.setToolTipText(LanguageProperties.getString(LanguageProperties.PANELS_BUNDLE, "UINodeEditPanel.copyTip")); //$NON-NLS-1$
		pbCopy.addActionListener(this);
		toolBar.add(pbCopy);

		pbPaste = createToolBarButton(LanguageProperties.getString(LanguageProperties.PANELS_BUNDLE, "UINodeEditPanel.paste"), UIImages.get(PASTE_ICON)); //$NON-NLS-1$
		pbPaste.setToolTipText(LanguageProperties.getString(LanguageProperties.PANELS_BUNDLE, "UINodeEditPanel.pasteTip")); //$NON-NLS-1$
		pbPaste.addActionListener(this);
		toolBar.add(pbPaste);

		//pbPrint = createToolBarButton("Print", UIImages.get(PASTE_ICON));
		//pbPrint.setToolTipText("Print the current page");
		//pbPrint.addActionListener(this);
		//toolBar.add(pbPrint);

		if (detailPages.size()==1) {
			pbBack.setEnabled(false);
			pbFirst.setEnabled(false);
			pbForward.setEnabled(false);
			pbLast.setEnabled(false);
		}

		return toolBar;
	}

	/*
	public JPanel createDetailButtonPanel() {
		JPanel panel = new JPanel();

		pbBack	= new UIButton("Previous");
		pbBack.setMargin(new Insets(0,0,0,0));
		pbBack.setEnabled(false);
		pbBack.setToolTipText("Turn to previous page");
		pbBack.addActionListener(this);
		panel.add(pbBack);

		pbForward	= new UIButton("Next");
		pbForward.setMargin(new Insets(0,0,0,0));
		if (detailPages.size() == 1)
			pbForward.setEnabled(false);

		pbForward.setToolTipText("Turn to next page");
		pbForward.addActionListener(this);
		panel.add(pbForward);

		panel.add(new JLabel("   "));

		pbNew	= new UIButton("New Page");
		pbNew.setMargin(new Insets(0,0,0,0));
		pbNew.setToolTipText("Create new details page");
		pbNew.addActionListener(this);
		panel.add(pbNew);

		panel.add(new JLabel("   "));

		pbDelete	= new UIButton("Delete Page");
		pbDelete.setMargin(new Insets(0,0,0,0));
		pbDelete.setToolTipText("Delete current page");
		pbDelete.addActionListener(this);
		panel.add(pbDelete);

		panel.add(new JLabel("   "));

		pbToNodes	= new UIButton("Convert Page To Nodes");
		pbToNodes.setMargin(new Insets(0,0,0,0));
		pbToNodes.setToolTipText("Convert Page text into nodes");
		pbToNodes.addActionListener(this);
		panel.add(pbToNodes);

		return panel;
	}
	*/

	/**
	 * Update the displayed data for the given node.
	 * @param node com.compendium.core.datamodel.NodeSummary, the node to diaply the data for.
	 */
	private void setNode(NodeSummary node) {

		if (pbThumbNail != null) {
			pbThumbNail.setSelected(true);
		}
		oNode = node;
		if (node != null) {
			try {
				String label = node.getLabel();
				if(label.equals(ICoreConstants.NOLABEL_STRING))
					label = ""; //$NON-NLS-1$
				txtLabel.setText( label );

				String detail = ((NodeDetailPage)detailPages.elementAt(0)).getText();
				if(detail.equals(ICoreConstants.NODETAIL_STRING))
					detail = ""; //$NON-NLS-1$
				txtDetail.setText( detail );

				if(nodeType == ICoreConstants.REFERENCE || nodeType == ICoreConstants.REFERENCE_SHORTCUT) {
					txtReference.setText(oNode.getSource());
				} else if (View.isMapType(nodeType)) {
					txtBackgroundImage.setText(((View)oNode).getViewLayer().getBackgroundImage());					
				}
				
				if (nodeType == ICoreConstants.REFERENCE || nodeType == ICoreConstants.REFERENCE_SHORTCUT ||
						View.isViewType(nodeType) || View.isShortcutViewType(nodeType)) {

					txtImage.setText(oNode.getImage());
					String imageRef = oNode.getImage();
					oLastImageSize = node.getImageSize();					
					if (oLastImageSize.width > 0 && oLastImageSize.height > 0 && !imageRef.equals("")) { //$NON-NLS-1$
						if ( UIImages.isImage(imageRef) ) {
							ImageIcon originalSizeImage = UIImages.createImageIcon(imageRef); 								
							if (originalSizeImage != null) {
								Image originalIcon = originalSizeImage.getImage();
								int originalWidth = originalIcon.getWidth(null);
								int originalHeight = originalIcon.getHeight(null);
								oActualImageSize = new Dimension(originalWidth, originalHeight);															
								if (oLastImageSize.width == originalWidth && oLastImageSize.height == originalHeight) {
									this.pbActualSize.setSelected(true);
								} else {
									this.pbSpecifiedSize.setSelected(true);
								}
							}
						}
					}
				}
			}
			catch (Exception e) {
				e.printStackTrace();
				ProjectCompendium.APP.displayError("Exception: (UINodeEditPanel.setNode) \n"+e.getMessage()); //$NON-NLS-1$
			}
		}
	}

	/**
	 * Listen for the image size setting begin changed.
	 */
	public void itemStateChanged(ItemEvent e) {
		if (pbThumbNail != null && pbThumbNail.isSelected()) {
			pbSize.setEnabled(false);
		} else if (pbActualSize != null && pbActualSize.isSelected()) {
			pbSize.setEnabled(false);
		} else if (pbSpecifiedSize != null && pbSpecifiedSize.isSelected()) {
			pbSize.setEnabled(true);	
		}
	}
	
	/**
	 * DOES NOTHING
	 * @param evt, the associated DocumentEvent.
	 */
	public void changedUpdate(DocumentEvent evt) {}

	/**
	 * Calls <code>changed</code>
	 * @param evt, the associated DocumentEvent.
	 * @see #changed
	 */
	public void insertUpdate(DocumentEvent evt) {
		changed(evt);
	}

	/**
	 * Calls <code>changed</code>
	 * @param evt, the associated DocumentEvent.
	 * @see #changed
	 */
	public void removeUpdate(DocumentEvent evt) {
		changed(evt);
	}

	/**
	 * Records which fields data has changed in.
	 * @param evt, the associated DocumentEvent.
	 */
	private void changed(DocumentEvent evt) {

		Document doc = evt.getDocument();
		if (doc == oLabelDoc) {
			bLabelChange = true;
		}
		else if (doc == oDetailDoc) {
			bDetailChange = true;
		}
		else if (doc == oRefDoc) {
			bRefChange = true;
			String sRef = txtReference.getText();
			if (!(sRef).equals("")) //$NON-NLS-1$
				pbExecute.setEnabled(true);
			else
				pbExecute.setEnabled(false);

			if (txtLabel.getText().equals("")) { //$NON-NLS-1$
				File file = new File(txtReference.getText());
				if (file != null)
					txtLabel.setText(file.getName());
			}
			processReferenceChoices(sRef);
		}
		else if (doc == oImageDoc) {
			bImageChange = true;
			String sImageRef = txtImage.getText();
			if (!sImageRef.equals("")) { //$NON-NLS-1$
				pbView.setEnabled(true);
				pbThumbNail.setEnabled(true);
				pbActualSize.setEnabled(true);
				pbSpecifiedSize.setEnabled(true);	
				if (pbSpecifiedSize.isSelected()) {
					pbSize.setEnabled(true);
				}
				if ( UIImages.isImage(sImageRef) ) {
					ImageIcon originalSizeImage = UIImages.createImageIcon(sImageRef); 		
					if (originalSizeImage == null) {
						return;
					} else {					
						oLastImageSize = new Dimension(0,0);
						Image originalIcon = originalSizeImage.getImage();
						int originalWidth = originalIcon.getWidth(null);
						int originalHeight = originalIcon.getHeight(null);
						oActualImageSize = new Dimension(originalWidth, originalHeight);
					}
				}
			} else {
				pbThumbNail.setEnabled(false);
				pbActualSize.setEnabled(false);
				pbSpecifiedSize.setEnabled(false);	
				pbSize.setEnabled(false);
				pbView.setEnabled(false);
			}
			
			processImageChoices(sImageRef);
		}
		else if (doc == oBackgroundDoc) {
			bBackgroundImageChange = true;
			String sBackground = txtBackgroundImage.getText();
			if (!(sBackground).equals("")) //$NON-NLS-1$
				pbView2.setEnabled(true);
			else
				pbView2.setEnabled(false);
			
			processBackgroundChoices(sBackground);
		}
	}

	/**
	 * Handles a button push event.
	 * @param evt, the associated ActionEvent for the button push.
	 */
	public void actionPerformed(ActionEvent evt) {

		Object source = evt.getSource();
		NodeDetailPage page = null;

		if (source == pbExecute)
			onExecute();
		else if (source == pbView)
			onView();
		else if (source == pbView2)
			onView2();
		else if (source == pbBrowse)
			onBrowse();
		else if (source == pbBrowse2)
			onBrowseImage();
		else if (source == pbBrowse3)
			onBrowseBackground();
		else if (source == pbDatabase) {
			String path = txtReference.getText();
			String sNewPath = path;
			sNewPath = UIUtilities.processLocation(path);
			if (!path.equals(sNewPath)) {
				try {
					oNode.setSource( sNewPath, oNode.getImage(), sAuthor );
					txtReference.setText(sNewPath);
				} catch(Exception ex) {
					System.out.println("Failed to store new changes: "+ex.getLocalizedMessage());
				}
			}
		} else if (source == pbDatabase2) {
			String path = txtImage.getText();
			String sNewPath = path;
			sNewPath = UIUtilities.processLocation(path);
			if (!path.equals(sNewPath)) {
				try {
					oNode.setSource( oNode.getSource(), sNewPath, sAuthor );
					txtImage.setText(sNewPath);
				} catch(Exception ex) {
					System.out.println("Failed to store new changes: "+ex.getLocalizedMessage());
				}
			}
		} else if (source == pbDatabase3) {
			String path = txtBackgroundImage.getText();
			String sNewPath = path;
			sNewPath = UIUtilities.processLocation(path);
			if (!path.equals(sNewPath)) {
				try {
					UIMapViewFrame frame = (UIMapViewFrame)ProjectCompendium.APP.getInternalFrame((View)oNode);
					frame.getViewPane().addBackgroundImage(sNewPath);
					txtBackgroundImage.setText(sNewPath);
				} catch(Exception ex) {
					System.out.println("Failed to store new changes: "+ex.getLocalizedMessage());
				}
			}
		} else if (source == pbToNodes) {
			createNodes();
			onUpdate();
			oParentDialog.onCancel();
		}
		else if ((source == pbOK)) {
			oParentDialog.onUpdate();
			oParentDialog.onCancel();
		}
		else if (source == pbCancel) {
			oParentDialog.onCancel();
		}
		else if (source == pbCut) {
			txtDetail.processCut();
		}
		else if (source == pbCopy) {
			txtDetail.processCopy();
		}
		else if (source == pbPaste) {
			txtDetail.processPaste();
		}
		else if (source == pbSize) {
			if (dlg != null ){
				dlg.setActualImageSize(oActualImageSize);
				dlg.setVisible(true);
			} else {
				dlg = new UIImageSizeDialog(oParentDialog, this, oLastImageSize, oActualImageSize);
				UIUtilities.centerComponent(dlg, oParentDialog);
				dlg.setVisible(true);
			}
		}
		//else if (source == pbPrint) {
		//	txtDetail.processPrint();
		//}
		else if (source == pbFirst) {
			String currText = txtDetail.getText();
			page = (NodeDetailPage)detailPages.elementAt(currentPage);
			if (!page.getText().equals(currText)) {
				page.setText(currText);
				page.setModificationDate(new Date());
			}

			if (datePanel.dateChanged()) {
				page.setCreationDate((datePanel.getDate()).getTime());
				bDetailChange = true;
			}
			page.setPageNo(currentPage+1);

			currentPage = 0;

			txtDetail.setText( ((NodeDetailPage)detailPages.elementAt(currentPage)).getText() );
			txtDetail.setCaretPosition(0);

			page = (NodeDetailPage)detailPages.elementAt(currentPage);
			createInfo(page);
			infopanel.repaint();

			if (detailPages.size() > 1) {
				pbForward.setEnabled(true);
				pbLast.setEnabled(true);
			}

			pbBack.setEnabled(false);
			pbFirst.setEnabled(false);

			pbBack.repaint();

			txtDetail.requestFocus();

			oParentDialog.repaint();
		}
		else if (source == pbLast) {
			String currText = txtDetail.getText();
			page = (NodeDetailPage)detailPages.elementAt(currentPage);
			if (!page.getText().equals(currText)) {
				page.setText(currText);
				page.setModificationDate(new Date());
			}

			if (datePanel.dateChanged()) {
				page.setCreationDate((datePanel.getDate()).getTime());
				bDetailChange = true;
			}
			page.setPageNo(currentPage+1);

			currentPage = detailPages.size()-1;

			txtDetail.setText( ((NodeDetailPage)detailPages.elementAt(currentPage)).getText() );
			txtDetail.setCaretPosition(0);

			page = (NodeDetailPage)detailPages.elementAt(currentPage);
			createInfo(page) ;
			infopanel.repaint();

			if (currentPage > 0) {
				pbBack.setEnabled(true);
				pbFirst.setEnabled(true);
			}

			pbForward.setEnabled(false);
			pbLast.setEnabled(false);

			txtDetail.requestFocus();

			oParentDialog.repaint();
		}
		else if (source == pbBack) {

			if (currentPage > 0) {
				String currText = txtDetail.getText();
				page = (NodeDetailPage)detailPages.elementAt(currentPage);
				if (!page.getText().equals(currText)) {
					page.setText(currText);
					page.setModificationDate(new Date());
				}

				if (datePanel.dateChanged()) {
					page.setCreationDate((datePanel.getDate()).getTime());
					bDetailChange = true;
				}
				page.setPageNo(currentPage+1);

				currentPage--;

				txtDetail.setText( ((NodeDetailPage)detailPages.elementAt(currentPage)).getText() );
				txtDetail.setCaretPosition(0);

				page = (NodeDetailPage)detailPages.elementAt(currentPage);
				createInfo(page) ;
				infopanel.repaint();

				if (!pbForward.isEnabled()) {
					pbForward.setEnabled(true);
					pbLast.setEnabled(true);
				}
			}

			if (currentPage == 0) {
				pbBack.setEnabled(false);
				pbFirst.setEnabled(false);
			}

			txtDetail.requestFocus();

			oParentDialog.repaint();
		}
		else if (source == pbForward) {

			if (currentPage+1 < detailPages.size()) {
				String currText = txtDetail.getText();
				page = (NodeDetailPage)detailPages.elementAt(currentPage);
				if (!page.getText().equals(currText)) {
					page.setText(currText);
					page.setModificationDate(new Date());
				}

				if (datePanel.dateChanged()) {
					page.setCreationDate((datePanel.getDate()).getTime());
					bDetailChange = true;
				}
				page.setPageNo(currentPage+1);

				currentPage++;

				txtDetail.setText( ((NodeDetailPage)detailPages.elementAt(currentPage)).getText() );
				txtDetail.setCaretPosition(0);

				page = (NodeDetailPage)detailPages.elementAt(currentPage);
				createInfo(page) ;
				infopanel.repaint();

				if (!pbBack.isEnabled()) {
					pbBack.setEnabled(true);
					pbFirst.setEnabled(true);
				}
			}

			if (currentPage+1 == detailPages.size()) {
				pbForward.setEnabled(false);
				pbLast.setEnabled(false);
			}

			txtDetail.requestFocus();

			oParentDialog.repaint();
		}
		else if (source == pbNew) {
			page = (NodeDetailPage)detailPages.elementAt(currentPage);
			page.setText(txtDetail.getText());			
			if (datePanel.dateChanged()) {
				page.setCreationDate((datePanel.getDate()).getTime());
				bDetailChange = true;
			}
			page.setPageNo(currentPage+1);
			currentPage++;			
			
			NodeDetailPage pageNew = new NodeDetailPage(oNode.getId(), ProjectCompendium.APP.getModel().getUserProfile().getId(), "", currentPage+1, new Date(), new Date());			 //$NON-NLS-1$
			detailPages.insertElementAt(pageNew, currentPage);						
			txtDetail.setText(""); //$NON-NLS-1$
			createInfo(pageNew) ;
			centerpanel.repaint();

			updatePageNumbers();

			if (!pbBack.isEnabled()) {
				pbBack.setEnabled(true);
				pbFirst.setEnabled(true);
			}

			txtDetail.requestFocus();

			oParentDialog.repaint();
		}
		else if (source == pbDelete) {

			if (detailPages.size() > 1) {
				detailPages.removeElementAt(currentPage);

				updatePageNumbers();

				if (currentPage > detailPages.size()-1)
					currentPage--;

				page = (NodeDetailPage)detailPages.elementAt(currentPage);
				txtDetail.setText( page.getText() );

				createInfo(page);
				centerpanel.repaint();
			}
			else {
				page = (NodeDetailPage)detailPages.elementAt(0);
				page.setText(""); //$NON-NLS-1$
				page.setModificationDate(new Date());

				if (datePanel.dateChanged()) {
					page.setCreationDate((datePanel.getDate()).getTime());
					bDetailChange = true;
				}
				page.setPageNo(1);

				page = (NodeDetailPage)detailPages.elementAt(0);

				txtDetail.setText(""); //$NON-NLS-1$
				pbBack.setEnabled(false);
			}

			if (currentPage == detailPages.size()-1 && !pbForward.isEnabled()) {
				pbForward.setEnabled(false);
				pbLast.setEnabled(false);
			}

			if (detailPages.size()==1) {
				pbBack.setEnabled(false);
				pbFirst.setEnabled(false);
				pbForward.setEnabled(false);
				pbLast.setEnabled(false);
			}

			oParentDialog.repaint();
		}
	}
	
	/**
	 * Update the page numbers for the current amount of pages.
	 */
	private void updatePageNumbers() {

		NodeDetailPage page = null;
		int count = detailPages.size();
		for (int i=0; i<count; i++) {
			page = (NodeDetailPage)detailPages.elementAt(i);
			page.setPageNo(i+1);
		}
	}

	/**
	 * Process the saving of any node contents changes.
	 */
	public void onUpdate() {		
		if (oNode != null) {
			try {				
				// If there was not actual changes, some validation and repaints where still happening.
				// This was making for slow closing of the dialog even if nothing had changed on maps with lots of picture etc.
				// This variable is used to check whether to run them or not.
				boolean bUpdated = false;

				// TO CATCH FIRST PAGE DATE CHANGE
				if (datePanel.dateChanged()) {
					bDetailChange = true;
				}

				if (bLabelChange) {
					if (oUINode != null)
						oUINode.setText(txtLabel.getText());
					else {
						oNode.setLabel(txtLabel.getText(), sAuthor);
					}
					bUpdated = true;
					bLabelChange = false;
				}
			
				if(nodeType == ICoreConstants.REFERENCE || nodeType == ICoreConstants.REFERENCE_SHORTCUT) {
					if (bRefChange) {
						sReference = txtReference.getText();
						sReference = sReference.trim();
					}
					if (bImageChange) {
						sImage = txtImage.getText();
						sImage = sImage.trim();						
					}

					if (bImageChange || bRefChange) {
						oNode.setSource( sReference, sImage, sAuthor );
						bUpdated = true;
					} 
						
					if (checkImageSize()) {
						bUpdated = true;
					}
					bRefChange = false;
					bImageChange = false;
				}
				else if(View.isViewType(nodeType) || View.isShortcutViewType(nodeType)) {

					if(View.isMapType(nodeType)) {
						if (bBackgroundImageChange) {
							sBackgroundImage = txtBackgroundImage.getText();
							sBackgroundImage = sBackgroundImage = sBackgroundImage.trim();

							((View)oNode).setBackgroundImage( sBackgroundImage );
							UIMapViewFrame frame = (UIMapViewFrame)ProjectCompendium.APP.getInternalFrame((View)oNode);
							if (frame != null) {
								if (sBackgroundImage.equals("")) //$NON-NLS-1$
									frame.getViewPane().removeBackground();
								else
									frame.getViewPane().addBackgroundImage(sBackgroundImage);
								bUpdated = true;
							}
						}
						if (bBackgroundColorChange) {
							((View)oNode).setBackgroundColor( nBackgroundColor );
							UIMapViewFrame frame = (UIMapViewFrame)ProjectCompendium.APP.getInternalFrame((View)oNode);
							if (frame != null) {
								frame.getViewPane().setBackground(new Color(nBackgroundColor));
								bUpdated = true;
							}
						}
						if (bBackgroundColorChange || bBackgroundImageChange) {
							((View)oNode).updateViewLayer();
						}
					}

					if (bImageChange) {
						sImage = txtImage.getText();
						sImage= sImage.trim();
						oNode.setSource( "", sImage, sAuthor); //$NON-NLS-1$
						bUpdated = true;
					}
					
					if (checkImageSize()) {
						bUpdated = true;
					}
					bImageChange = false;
				}

				if (bDetailChange) {

					NodeDetailPage page = (NodeDetailPage)detailPages.elementAt(currentPage);
					page.setText(txtDetail.getText());

					if (datePanel.dateChanged()) {
						page.setCreationDate((datePanel.getDate()).getTime());
						page.setModificationDate(new Date());
					}
					detailPages.setElementAt(page, currentPage);

					// INCASE THEY HAVE GOT OUT OF WACK
					updatePageNumbers();

					String sAuthor = ProjectCompendium.APP.getModel().getUserProfile().getUserName();
					oNode.setDetailPages(detailPages, sAuthor, sAuthor);

					bDetailChange = false;
					bUpdated = true;
				}

				if(bUpdated) {
					oParentDialog.setModified(UIUtilities.getSimpleDateFormat("MMM d, yyyy h:mm a").format(oNode.getModificationDate()), sAuthor); //$NON-NLS-1$
					String sNodeID = oNode.getId();
					ProjectCompendium.APP.refreshNodeIconIndicators(oNode.getId());
					ProjectCompendium.APP.refreshIcons(sNodeID);
				}
			}
			catch (Exception e) {
				e.printStackTrace();
				ProjectCompendium.APP.displayError("Error in 'UINodeEditPanel.onUpdate'\n\n"+e.getMessage()); //$NON-NLS-1$
			}
		}
	}
	
	/**
	 * Chack if the image size needs saving, and if it does, save.
	 * @return if the image size was saved.
	 */
	private boolean checkImageSize() {
		try {
			if (oActualImageSize != null) {
				if (pbThumbNail != null && pbThumbNail.isSelected()) {
					if (oLastImageSize.width != 0 || oLastImageSize.height != 0) {
						oNode.setImageSize(new Dimension(0,0), sAuthor);
						return true;
					} 
				} else if (pbActualSize != null && pbActualSize.isSelected() && 
						(oLastImageSize.width != oActualImageSize.width || oLastImageSize.height != oActualImageSize.height)) {
					oNode.setImageSize(oActualImageSize, sAuthor);
					return true;
				} else if (pbSpecifiedSize != null && pbSpecifiedSize.isSelected()) {
					//Get then set the user Specified size
					Dimension oSpecifiedSize = new Dimension(0,0);													
					if (dlg != null) {
						oSpecifiedSize = dlg.getImageSize();
						dlg.dispose();
					}
					if (oLastImageSize.width != oSpecifiedSize.width || oLastImageSize.height != oSpecifiedSize.height) {
						oNode.setImageSize(oSpecifiedSize, sAuthor);
						return true;
					}
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			ProjectCompendium.APP.displayError("Error in 'UINodeEditPanel.checkImageSize'\n\n"+e.getMessage()); //$NON-NLS-1$
		}
		return false;
	}

	/**
	 * Create external nodes for the current page on detail text.
	 */
	private void createNodes() {

		String text = txtDetail.getSelectedText();

		if (text == null)
			text = txtDetail.getText();

		Point loc = oUINode.getNodePosition().getPos();
		int xPos = loc.x + 80;
		int yPos = loc.y;

		ShorthandParser shorthand = new ShorthandParser();

		if (oUINode != null)
			shorthand.createNodesWithLinks(text, ProjectCompendium.APP.getCurrentFrame(), oUINode, "", xPos, yPos); //$NON-NLS-1$
		else
			shorthand.createNodes(text, ProjectCompendium.APP.getCurrentFrame(), "", xPos, yPos); //$NON-NLS-1$
	}

	/**
	 * Launch an external application for the current reference path.
	 */
	public void onExecute() {

		// IF WE ARE RECORDING A MEETING AND HAVE A MAP, RECORD A REFERENCE LAUNCHED EVENT.
		if (oUINode != null &&
				ProjectCompendium.APP.oMeetingManager != null &&
					ProjectCompendium.APP.oMeetingManager.captureEvents() &&
					(ProjectCompendium.APP.oMeetingManager.getMeetingType() == MeetingManager.RECORDING)) {

			ProjectCompendium.APP.oMeetingManager.addEvent(
					new MeetingEvent(ProjectCompendium.APP.oMeetingManager.getMeetingID(),
									 ProjectCompendium.APP.oMeetingManager.isReplay(),
									 MeetingEvent.REFERENCE_LAUNCHED_EVENT,
									 oUINode.getNodePosition().getView(),
									 oUINode.getNode()));
		}

		String path = txtReference.getText();
		if (path.startsWith("http:") || path.startsWith("https:") || path.startsWith("www.") || //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				ProjectCompendium.isLinux && (path.startsWith("fish:") || path.startsWith("ssh:") ||  //$NON-NLS-1$ //$NON-NLS-2$
						path.startsWith("ftp:") || path.startsWith("smb:"))) { //$NON-NLS-1$ //$NON-NLS-2$
			ExecuteControl.launch( path );
		} else if (path.startsWith(ICoreConstants.sINTERNAL_REFERENCE)) {
			path = path.substring(ICoreConstants.sINTERNAL_REFERENCE.length());
			int ind = path.indexOf("/"); //$NON-NLS-1$
			if (ind != -1) {
				String sGoToViewID = path.substring(0, ind);
				String sGoToNodeID = path.substring(ind+1);	
				if (oUINode != null) {
					UIUtilities.jumpToNode(sGoToViewID, sGoToNodeID, 
						oUINode.getViewPane().getViewFrame().getChildNavigationHistory());
				} else {
					Vector history = new Vector();			//mlb: Add this so that Ref node content launches
					history.addElement("?");				// from the inbox will work.  (bug fix) //$NON-NLS-1$
					UIUtilities.jumpToNode(sGoToViewID, sGoToNodeID, history);
				}
			}
		} else {
			File file = new File(path);
			String sPath = path;
			if (file.exists()) {
				sPath = file.getAbsolutePath();
			}
			// If the reference is not a file, just pass the path as is, as it is probably a special type of url.
			ExecuteControl.launch( sPath );
		}
	}

	/**
	 * Launch an external application to view the current image.
	 */
	public void onView() {
		String path = txtImage.getText();
		if (path.startsWith("http:") || path.startsWith("https:") || path.startsWith("www.") || //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				ProjectCompendium.isLinux && (path.startsWith("fish:") || path.startsWith("ssh:") ||  //$NON-NLS-1$ //$NON-NLS-2$
						path.startsWith("ftp:") || path.startsWith("smb:"))) { //$NON-NLS-1$ //$NON-NLS-2$
			ExecuteControl.launch( path );
		}
		else {
			File file = new File(path);
			String sPath = path;
			if (file.exists()) {
				sPath = file.getAbsolutePath();
			}
			// If the reference is not a file, just pass the path as is, as it is probably a special type of url.
			ExecuteControl.launch( sPath );
		}
	}

	/**
	 * Launch an external application to view the current background.
	 */
	public void onView2() {
		String path = txtBackgroundImage.getText();
		if (path.startsWith("http:") || path.startsWith("https:") || path.startsWith("www.") || //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				ProjectCompendium.isLinux && (path.startsWith("fish:") || path.startsWith("ssh:") ||  //$NON-NLS-1$ //$NON-NLS-2$
						path.startsWith("ftp:") || path.startsWith("smb:"))) { //$NON-NLS-1$ //$NON-NLS-2$
			ExecuteControl.launch( path );
		}
		else {
			File file = new File(path);
			String sPath = path;
			if (file.exists()) {
				sPath = file.getAbsolutePath();
			}
			// If the reference is not a file, just pass the path as is, as it is probably a special type of url.
			ExecuteControl.launch( sPath );
		}
	}

	/**
	 * Open a file browser dialog for the reference field.
	 */
	public void onBrowse() {
		fdgBrowse = new JFileChooser();
		fdgBrowse.setDialogTitle(LanguageProperties.getString(LanguageProperties.PANELS_BUNDLE, "UINodeEditPanel.chooseRef"));
		fdgBrowse.setAccessory(new UIImagePreview(fdgBrowse));

		String path = txtReference.getText();
		if (CoreUtilities.isFile(path)) {
			File file = new File(path);
			if (file.exists()) {
				fdgBrowse.setCurrentDirectory(new File(file.getParent()+ProjectCompendium.sFS));
				fdgBrowse.setSelectedFile(file);
			}			
		} else if (!UINodeEditPanel.lastFileDialogDir.equals("")) { //$NON-NLS-1$
			// FIX FOR MAC - NEEDS '/' ON END TO DENOTE A FOLDER
			File file = new File(UINodeEditPanel.lastFileDialogDir+ProjectCompendium.sFS);
			if (file.exists()) {
				fdgBrowse.setCurrentDirectory(file);
			}
		}
		UIUtilities.centerComponent(fdgBrowse, ProjectCompendium.APP);
		int retval = fdgBrowse.showSaveDialog(ProjectCompendium.APP);
		if (retval == JFileChooser.APPROVE_OPTION) {
			if (fdgBrowse.getSelectedFile() != null) {
				File fileDir = fdgBrowse.getCurrentDirectory();
				UINodeEditPanel.lastFileDialogDir = fileDir.getPath();
				txtReference.setText(fdgBrowse.getSelectedFile().getAbsolutePath());
			}
		}
	}

	/**
	 * Open a file browser dialog for the image field.
	 */
	public void onBrowseImage() {
		fdgBrowse2 = new JFileChooser();
		fdgBrowse2.setDialogTitle(LanguageProperties.getString(LanguageProperties.PANELS_BUNDLE, "UINodeEditPanel.chooseImage"));
		fdgBrowse2.setFileFilter(UIImages.IMAGE_FILTER);
		fdgBrowse2.setAccessory(new UIImagePreview(fdgBrowse2));

		String path = txtImage.getText();
		if (CoreUtilities.isFile(path)) {
			File file = new File(path);
			if (file.exists()) {
				fdgBrowse2.setCurrentDirectory(new File(file.getParent()+ProjectCompendium.sFS));
				fdgBrowse2.setSelectedFile(file);
			}			
		} else if (!UINodeEditPanel.lastFileDialogDir2.equals("")) { //$NON-NLS-1$
			// FIX FOR MAC - NEEDS '/' ON END TO DENOTE A FOLDER
			File file = new File(UINodeEditPanel.lastFileDialogDir2+ProjectCompendium.sFS);
			if (file.exists()) {
				fdgBrowse2.setCurrentDirectory(file);
			}
		}
		UIUtilities.centerComponent(fdgBrowse2, ProjectCompendium.APP);
		int retval = fdgBrowse2.showSaveDialog(ProjectCompendium.APP);
		if (retval == JFileChooser.APPROVE_OPTION) {
			if (fdgBrowse2.getSelectedFile() != null) {
				File fileDir = fdgBrowse2.getCurrentDirectory();
				UINodeEditPanel.lastFileDialogDir2 = fileDir.getPath();
				txtImage.setText(fdgBrowse2.getSelectedFile().getAbsolutePath());
			}
		}
	}

	/**
	 * Open a file browser dialog for the background field.
	 */
	public void onBrowseBackground() {
		fdgBrowse3 = new JFileChooser();
		fdgBrowse3.setDialogTitle(LanguageProperties.getString(LanguageProperties.PANELS_BUNDLE, "UINodeEditPanel.chooseBackgroundTitle"));
		fdgBrowse3.setFileFilter(UIImages.IMAGE_FILTER);
		fdgBrowse3.setAccessory(new UIImagePreview(fdgBrowse3));

		String path = txtBackgroundImage.getText();
		if (CoreUtilities.isFile(path)) {
			File file = new File(path);
			if (file.exists()) {
				fdgBrowse3.setCurrentDirectory(new File(file.getParent()+ProjectCompendium.sFS));
				fdgBrowse3.setSelectedFile(file);
			}			
		} else if (!UINodeEditPanel.lastFileDialogDir2.equals("")) { //$NON-NLS-1$
			// FIX FOR MAC - NEEDS '/' ON END TO DENOTE A FOLDER
			File file = new File(UINodeEditPanel.lastFileDialogDir2+ProjectCompendium.sFS);
			if (file.exists()) {
				fdgBrowse3.setCurrentDirectory(file);
			}
		}
		UIUtilities.centerComponent(fdgBrowse3, ProjectCompendium.APP);
		int retval = fdgBrowse3.showSaveDialog(ProjectCompendium.APP);
		if (retval == JFileChooser.APPROVE_OPTION) {
			if ((fdgBrowse3.getSelectedFile()) != null) {
				File fileDir = fdgBrowse3.getCurrentDirectory();
				UINodeEditPanel.lastFileDialogDir2 = fileDir.getPath();
				txtBackgroundImage.setText(fdgBrowse3.getSelectedFile().getAbsolutePath());
			}
		}
	}

	/**
	 * Convenience method that sets the label box to be in focus when a user exceeds the label length.
	 */
	public void setLabelFieldFocused() {
		txtLabel.requestFocus();
	}

	/**
	 * Return detail text area.
	 * @return JTextArea, the detail text area.
	 */
	public JTextArea getDetailField() {
		return txtDetail;
	}

	/**
	 * Convenience method that sets the Detail box to be in focus.
	 */
	public void setDetailFieldFocused() {
		txtDetail.requestFocus();
	}

	/**
	 * Convenience method that sets the UINode when the shortcut node dialog invokes the node edit dailog.
	 * @param uinode com.compendium.core.datamodel.UINode, the node to display the contents for.
	 */
	public void setUINode(UINode uinode) {
		oUINode = uinode;
	}
}
