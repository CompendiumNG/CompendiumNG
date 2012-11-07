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

import java.util.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.table.*;
import javax.swing.event.*;
import javax.swing.text.Document;
import javax.swing.border.*;
import javax.swing.event.DocumentEvent;

import com.compendium.core.*;
import com.compendium.core.datamodel.*;
import com.compendium.core.db.*;

import com.compendium.LanguageProperties;
import com.compendium.ProjectCompendium;
import com.compendium.io.html.*;
import com.compendium.ui.plaf.*;
import com.compendium.ui.*;
import com.compendium.ui.panels.*;

/**
 * UIBackupDialog defines the dialog, that allows the user to backup thier dataase.
 *
 * @author	Michelle Bachler
 */
public class UIImageSizeDialog extends UIDialog implements ActionListener, DocumentListener, IUIConstants {

	/** The current pane to put the dialog contents in.*/
	private Container				oContentPane = null;

	/** The button to activate the saving of the user settings.*/
	private UIButton				pbSave = null;

	/** The button to close this dialog without backing up.*/
	private UIButton				pbClose	 = null;

	/** The button to open the relevant help.*/
	private UIButton				pbHelp	 = null;

	/** Indicates the user wishes to specify the size in pixels.*/
	private JRadioButton 			rbPixel = null;

	/** Indicates the user wishes to specify the size as a percentage of the original.*/
	private JRadioButton 			rbPercentage = null;

	/** Field for user to enter the pixel specific width.*/
	private JTextField				txtPixelWidth = null;
	
	/** Field for user to enter the pixel specific height.*/
	private JTextField				txtPixelHeight = null;
	
	/** Field for user to enter the percentage width.*/
	private JTextField				txtPercentageWidth = null;
	
	/** Field for user to enter the percentage height.*/
	private JTextField				txtPercentageHeight = null;
	
	/** Indicates if the exisitng image ratio should be maintain as the user enters values.*/
	private JCheckBox				cbMaintainRatio = null;

	/** The layout manager used.*/
	private	GridBagLayout 			gb = null;

	/** The constraints used.*/
	private	GridBagConstraints 		gc = null;

	/** The parent frame for this dialog.*/
	private UIDialog				oParent = null;

	/** The counter for the gridbag layout y position.*/
	private int gridyStart = 0;

	/** The current user set size to display the image.*/
	private Dimension oCurrentSize = null;
	
	/** The actual size of the image.*/
	private Dimension oActualSize = null; 

	/** The Document for the pixel width.*/
	private Document		oPixelWidthDoc		= null;

	/** The Document for the pixel Height.*/
	private Document		oPixelHeightDoc		= null;

	/** The Document for the percentage width.*/
	private Document		oPercentageWidthDoc	= null;

	/** The Document for the percentage height.*/
	private Document		oPercentageHeightDoc= null;
	
	/** The label to display the actual size.*/
	private JLabel			lblActualSizeLabel = null;

	
	/**
	 * Constructor. Initializes and sets up the dialog.
	 *
	 * @param parent the Frame that is the parent for this dialog.
	 * @param dlg the dialog that launched this dialog and is responsible for it.
	 * @param currentSize the current size of the image.
	 * @param actualSize the actual size of the image.
	 */
	public UIImageSizeDialog(UIDialog parent, UINodeEditPanel panel, Dimension currentSize, Dimension actualSize) {

		super(parent, true);
		oParent = parent;
		oCurrentSize = currentSize;
		oActualSize = actualSize;

		setTitle(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIImageSizeDialog.displaySize")); //$NON-NLS-1$

		oContentPane = getContentPane();
		oContentPane.setLayout(new BorderLayout());
		drawDialog();

		pack();
		setResizable(false);
		return;
	}

	/**
	 * Draws the contents of this dialog.
	 */
	private void drawDialog() {

		gb = new GridBagLayout();
		JPanel oMainPanel = new JPanel(gb);
		oMainPanel.setBorder(new EmptyBorder(5,5,1,5));	
		
		GridBagConstraints gc = new GridBagConstraints();
		gc.insets = new Insets(5,5,5,5);
		gc.anchor = GridBagConstraints.WEST;

		JLabel label = new JLabel(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIImageSizeDialog.actualSize")); //$NON-NLS-1$
		gc.gridy = gridyStart;
		gc.gridwidth = 2;		
		gb.setConstraints(label, gc);
		oMainPanel.add(label);

		lblActualSizeLabel = new JLabel(oActualSize.width+" x"); //$NON-NLS-1$
		gc.gridy = gridyStart;
		gc.gridwidth = 2;		
		gb.setConstraints(lblActualSizeLabel, gc);
		oMainPanel.add(lblActualSizeLabel);
	
		gridyStart++;
		
		rbPixel = new JRadioButton(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIImageSizeDialog.pixelSize")); //$NON-NLS-1$
		rbPixel.setToolTipText(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIImageSizeDialog.pixelSizeTip"));		 //$NON-NLS-1$
		rbPixel.setSelected(true);						
		gc.gridy = gridyStart;
		gc.gridwidth = 4;
		gridyStart++;
		gb.setConstraints(rbPixel, gc);
		oMainPanel.add(rbPixel);
				
		label = new JLabel(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIImageSizeDialog.width")); //$NON-NLS-1$
		gc.gridy = gridyStart;
		gc.gridwidth = 1;
		gb.setConstraints(label, gc);
		oMainPanel.add(label);

		txtPixelWidth = new JTextField();
		txtPixelWidth.setColumns(6);
		gc.gridy = gridyStart;
		gb.setConstraints(txtPixelWidth, gc);
		oMainPanel.add(txtPixelWidth);
		oPixelWidthDoc = txtPixelWidth.getDocument();
		oPixelWidthDoc.addDocumentListener(this);

		label = new JLabel(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIImageSizeDialog.height")); //$NON-NLS-1$
		gc.gridy = gridyStart;
		gb.setConstraints(label, gc);
		oMainPanel.add(label);

		txtPixelHeight = new JTextField();
		txtPixelHeight.setColumns(6);		
		gc.gridy = gridyStart;
		gb.setConstraints(txtPixelHeight, gc);
		oMainPanel.add(txtPixelHeight);
		oPixelHeightDoc = txtPixelHeight.getDocument();
		oPixelHeightDoc.addDocumentListener(this);

		gridyStart++;

		rbPercentage = new JRadioButton(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIImageSizeDialog.percentage")); //$NON-NLS-1$
		rbPercentage.setToolTipText(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIImageSizeDialog.percentageTip")); //$NON-NLS-1$
		rbPercentage.addItemListener( new ItemListener() {
			public void itemStateChanged(java.awt.event.ItemEvent e) {
				if (rbPercentage.isSelected()) {
					txtPercentageWidth.setEnabled(true);
					txtPercentageHeight.setEnabled(true);
					txtPixelHeight.setEnabled(false);
					txtPixelWidth.setEnabled(false);					
				}
				else {
					txtPercentageWidth.setEnabled(false);
					txtPercentageHeight.setEnabled(false);					
					txtPixelHeight.setEnabled(true);
					txtPixelWidth.setEnabled(true);					
				}
			}
		});

		gc.gridy = gridyStart;
		gc.gridwidth = 4;
		gridyStart++;
		gb.setConstraints(rbPercentage, gc);
		oMainPanel.add(rbPercentage);

		label = new JLabel(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIImageSizeDialog.width")); //$NON-NLS-1$
		gc.gridwidth = 1;		
		gc.gridy = gridyStart;
		gb.setConstraints(label, gc);
		oMainPanel.add(label);

		txtPercentageWidth = new JTextField();
		txtPercentageWidth.setColumns(6);	
		txtPercentageWidth.setEnabled(false);
		gc.gridy = gridyStart;
		gb.setConstraints(txtPercentageWidth, gc);
		oMainPanel.add(txtPercentageWidth);
		oPercentageWidthDoc = txtPercentageWidth.getDocument();
		oPercentageWidthDoc.addDocumentListener(this);
		
		label = new JLabel(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIImageSizeDialog.height")); //$NON-NLS-1$
		gc.gridy = gridyStart;
		gb.setConstraints(label, gc);
		oMainPanel.add(label);
		
		txtPercentageHeight = new JTextField();
		txtPercentageHeight.setColumns(6);		
		txtPercentageHeight.setEnabled(false);									
		gc.gridy = gridyStart;
		gb.setConstraints(txtPercentageHeight, gc);
		oMainPanel.add(txtPercentageHeight);
		oPercentageHeightDoc = txtPercentageHeight.getDocument();
		oPercentageHeightDoc.addDocumentListener(this);
			
		ButtonGroup rgGroup = new ButtonGroup();
		rgGroup.add(rbPercentage);
		rgGroup.add(rbPixel);

		gridyStart++;

		cbMaintainRatio = new JCheckBox(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIImageSizeDialog.imageRatio")); //$NON-NLS-1$
		cbMaintainRatio.setEnabled(true);
		cbMaintainRatio.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent evt) {
				if (cbMaintainRatio.isSelected()) {
					if (rbPixel.isSelected()) {
						String sWidth = txtPixelWidth.getText();
						sWidth = sWidth.trim();
						if (!sWidth.equals("")) { //$NON-NLS-1$
							try {
								int nWidth = new Integer(sWidth).intValue();
								if (nWidth < 0) {
									throw new NumberFormatException();
								}					
								double ratio = CoreUtilities.divide(nWidth, oActualSize.width);
								int newHeight = new Double(oActualSize.height*ratio).intValue();
								txtPixelHeight.setText(String.valueOf(newHeight));
							} catch(NumberFormatException e) {
								ProjectCompendium.APP.displayError(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIImageSizeDialog.erroWidth")); //$NON-NLS-1$
							}
						} else {
							String sHeight = txtPixelHeight.getText();
							sHeight = sHeight.trim();
							if (!sHeight.equals("")) { //$NON-NLS-1$
								try {
									int nHeight= new Integer(sHeight).intValue();
									if (nHeight < 0) {
										throw new NumberFormatException();
									}					
									double ratio = CoreUtilities.divide(nHeight, oActualSize.height);
									int newWidth = new Double(oActualSize.width*ratio).intValue();
									txtPixelWidth.setText(String.valueOf(newWidth));
								} catch(NumberFormatException e) {
									ProjectCompendium.APP.displayError(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIImageSizeDialog.errorHeight")); //$NON-NLS-1$
								}	
							}
						}						
						try {
							String sWidth2 = txtPixelWidth.getText();
							String sHeight = txtPixelHeight.getText();
							int nWidth = new Integer(sWidth2).intValue();
							int nHeight = new Integer(sHeight).intValue();
							
							double percentageWidth = CoreUtilities.divide(nWidth, oActualSize.width)*100;
							double percentageHeight = percentageWidth;
							if (!cbMaintainRatio.isSelected()) {
								percentageHeight = 	CoreUtilities.divide(nHeight, oActualSize.height) * 100;
							} 
							String newWidth = String.valueOf((new Double(percentageWidth)).intValue());
							if (!txtPercentageWidth.getText().equals(newWidth)) {
								txtPercentageWidth.setText(newWidth);
							}
							String newHeight = String.valueOf((new Double(percentageHeight)).intValue());
							if (!txtPercentageHeight.equals(newHeight)) {
								txtPercentageHeight.setText(newHeight);
							}
						} catch(NumberFormatException e) {
							//ProjectCompendium.APP.displayError("You can only enter positive whole numbers in the pixel Width field");
						}						
					} else if (rbPercentage.isSelected()) {
						String sPercentageWidth = txtPercentageWidth.getText();
						sPercentageWidth = sPercentageWidth.trim();
						if (!sPercentageWidth.equals("")) { //$NON-NLS-1$
							try {
								int nPercentageWidth = (new Integer(sPercentageWidth)).intValue();
								if (nPercentageWidth < 0) {
									throw new NumberFormatException();
								}
								if (cbMaintainRatio.isSelected()) {	
									txtPercentageHeight.setText(txtPercentageWidth.getText());
									double scaleH = CoreUtilities.divide(nPercentageWidth,100);
									Point scaledHeightPoint = UIUtilities.transformPoint(oActualSize.height, oActualSize.height, scaleH);
									txtPixelHeight.setText(String.valueOf(scaledHeightPoint.x)); 												
								} 				
								double scaleW = CoreUtilities.divide(nPercentageWidth, 100);	
								Point scaledWidthPoint = UIUtilities.transformPoint(oActualSize.width, oActualSize.width, scaleW);
								txtPixelWidth.setText(String.valueOf(scaledWidthPoint.x));			
							} catch(NumberFormatException e) {
								e.printStackTrace();
								ProjectCompendium.APP.displayError(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIImageSizeDialog.errorPercentageWidth")); //$NON-NLS-1$
							}
						} else {
							String sPercentageHeight = txtPercentageHeight.getText();
							sPercentageHeight = sPercentageHeight.trim();
							if (!sPercentageHeight.equals("")) { //$NON-NLS-1$
								try {
									int nPercentageHeight = (new Integer(sPercentageHeight)).intValue();
									if (nPercentageHeight < 0) {
										throw new NumberFormatException();
									}
									if (cbMaintainRatio.isSelected()) {	
										txtPercentageWidth.setText(txtPercentageHeight.getText());
										double scaleW = CoreUtilities.divide(nPercentageHeight,100);			
										Point scaledWidthPoint = UIUtilities.transformPoint(oActualSize.width, oActualSize.width, scaleW);
										txtPixelWidth.setText(String.valueOf(scaledWidthPoint.x));										
									}
									double scaleH = CoreUtilities.divide(nPercentageHeight,100);
									Point scaledHeightPoint = UIUtilities.transformPoint(oActualSize.height, oActualSize.height, scaleH);
									txtPixelHeight.setText(String.valueOf(scaledHeightPoint.x)); 						
								} catch(NumberFormatException e) {
									e.printStackTrace();
									ProjectCompendium.APP.displayError(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIImageSizeDialog.errorPercentageHeight")); //$NON-NLS-1$
								}
							}
						}
					}
				}
			}
		});
		gc.gridy = gridyStart;
		gc.gridwidth = 4;		
		gridyStart++;
		gb.setConstraints(cbMaintainRatio, gc);
		oMainPanel.add(cbMaintainRatio);

		UIButtonPanel oButtonPanel = new UIButtonPanel();

		pbSave = new UIButton(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIImageSizeDialog.okButton")); //$NON-NLS-1$
		pbSave.addActionListener(this);
		pbSave.setMnemonic(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIImageSizeDialog.okButtonMnemonic").charAt(0));
		getRootPane().setDefaultButton(pbSave); // 
		oButtonPanel.addButton(pbSave);

		pbClose = new UIButton(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIImageSizeDialog.cancelButton")); //$NON-NLS-1$
		pbClose.setMnemonic(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIImageSizeDialog.cancelButtonMnemonic").charAt(0));
		pbClose.addActionListener(this);
		oButtonPanel.addButton(pbClose);

		//pbHelp = new UIButton("Help");
		//pbHelp.setMnemonic(KeyEvent.VK_H);
		//ProjectCompendium.APP.mainHB.enableHelpOnButton(pbHelp, "basics.databases-backup", ProjectCompendium.APP.mainHS);
		//oButtonPanel.addHelpButton(pbHelp);

		oContentPane.add(oMainPanel, BorderLayout.CENTER);
		oContentPane.add(oButtonPanel, BorderLayout.SOUTH);
		
		int nWidth = oActualSize.width;
		int nHeight = oActualSize.height;
		if (oCurrentSize.width > 0 || oCurrentSize.height > 0) {
			nWidth = oCurrentSize.width;
			nHeight = oCurrentSize.height;			
		}
		
		txtPixelWidth.setText(String.valueOf(nWidth));
		txtPixelHeight.setText(String.valueOf(nHeight));
		double ratio = CoreUtilities.divide(nWidth, oActualSize.width);
		int newHeight = new Double(oActualSize.height*ratio).intValue();
		if (newHeight == nHeight) {
			cbMaintainRatio.setSelected(true);
		}
		
		//Calculate percentage.
		if (oCurrentSize.width != oActualSize.width) {
			double percentageWidth = CoreUtilities.divide(oCurrentSize.width, oActualSize.width)*100;
			double percentageHeight = percentageWidth;
			if (!cbMaintainRatio.isSelected()) {
				percentageHeight = 	CoreUtilities.divide(oCurrentSize.height, oActualSize.height) * 100;
			} 
			String newWidthPercentage = String.valueOf((new Double(percentageWidth)).intValue());
			String newHeightPercentage = String.valueOf((new Double(percentageHeight)).intValue());
			txtPercentageWidth.setText(newWidthPercentage);
			txtPercentageHeight.setText(newHeightPercentage);
			
			if (!cbMaintainRatio.isSelected() && newHeightPercentage.equals(newWidthPercentage)) {
				cbMaintainRatio.setSelected(true);
			}
		}		
	}
	
	public void setActualImageSize(Dimension oSize) {
		oActualSize = oSize;
		lblActualSizeLabel.setText(oActualSize.width+" x "+oActualSize.height); //$NON-NLS-1$
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
		if (rbPixel.isSelected()) {
			if (doc == oPixelWidthDoc && txtPixelWidth.isEnabled() && txtPixelWidth.hasFocus()) {
				if (cbMaintainRatio.isSelected()) {						
					String sWidth = txtPixelWidth.getText();
					sWidth = sWidth.trim();
					if (!sWidth.equals("")) { //$NON-NLS-1$
						try {
							int nWidth = new Integer(sWidth).intValue();
							if (nWidth < 0) {
								throw new NumberFormatException();
							}					
							double ratio = CoreUtilities.divide(nWidth, oActualSize.width);
							int newHeight = new Double(oActualSize.height*ratio).intValue();
							if (!txtPixelHeight.getText().equals(String.valueOf(newHeight))) {
								txtPixelHeight.setText(String.valueOf(newHeight));
							}							
						} catch(NumberFormatException e) {
							ProjectCompendium.APP.displayError(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIImageSizeDialog.erroWidth")); //$NON-NLS-1$
						}
					}
				} 
				try {
					String sWidth = txtPixelWidth.getText();
					String sHeight = txtPixelHeight.getText();
					int nWidth = new Integer(sWidth).intValue();
					int nHeight = new Integer(sHeight).intValue();
					
					double percentageWidth = CoreUtilities.divide(nWidth, oActualSize.width)*100;
					double percentageHeight = percentageWidth;
					if (!cbMaintainRatio.isSelected()) {
						percentageHeight = 	CoreUtilities.divide(nHeight, oActualSize.height) * 100;
					} 

					txtPercentageWidth.setText(String.valueOf((new Double(percentageWidth)).intValue()));
					txtPercentageHeight.setText(String.valueOf((new Double(percentageHeight)).intValue()));					
				} catch(NumberFormatException e) {
					//ProjectCompendium.APP.displayError("You can only enter positive whole numbers in the pixel Width field");
				}
			}
			else if (doc == oPixelHeightDoc && txtPixelHeight.isEnabled() && txtPixelHeight.hasFocus()) {
				if (cbMaintainRatio.isSelected()) {						
					String sHeight = txtPixelHeight.getText();
					sHeight = sHeight.trim();
					if (!sHeight.equals("")) { //$NON-NLS-1$
						try {
							int nHeight= new Integer(sHeight).intValue();
							if (nHeight < 0) {
								throw new NumberFormatException();
							}					
							double ratio = CoreUtilities.divide(nHeight, oActualSize.height);
							int newWidth = new Double(oActualSize.width*ratio).intValue();
							if (!txtPixelWidth.getText().equals(String.valueOf(newWidth))) {
								txtPixelWidth.setText(String.valueOf(newWidth));
							}
						} catch(NumberFormatException e) {
							ProjectCompendium.APP.displayError(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIImageSizeDialog.errorHeight")); //$NON-NLS-1$
						}	
					}
				}
				try {
					String sWidth = txtPixelWidth.getText();
					String sHeight = txtPixelHeight.getText();
					int nWidth = new Integer(sWidth).intValue();
					int nHeight = new Integer(sHeight).intValue();
					
					double percentageWidth = CoreUtilities.divide(nWidth, oActualSize.width)*100;
					double percentageHeight = percentageWidth;
					if (!cbMaintainRatio.isSelected()) {
						percentageHeight = 	CoreUtilities.divide(nHeight, oActualSize.height) * 100;
					} 
					txtPercentageWidth.setText(String.valueOf((new Double(percentageWidth)).intValue()));
					txtPercentageHeight.setText(String.valueOf((new Double(percentageHeight)).intValue()));					
				} catch(NumberFormatException e) {
					//ProjectCompendium.APP.displayError("You can only enter positive whole numbers in the pixel Width field");
				}
			}
		} else if (rbPercentage.isSelected()) {
			if (doc == oPercentageWidthDoc && txtPercentageWidth.hasFocus()) {	
				String sPercentageWidth = txtPercentageWidth.getText();
				sPercentageWidth = sPercentageWidth.trim();
				if (!sPercentageWidth.equals("")) { //$NON-NLS-1$
					try {
						int nPercentageWidth = (new Integer(sPercentageWidth)).intValue();
						if (nPercentageWidth < 0) {
							throw new NumberFormatException();
						}
						if (cbMaintainRatio.isSelected()) {	
							txtPercentageHeight.setText(txtPercentageWidth.getText());
							double scaleH = CoreUtilities.divide(nPercentageWidth,100);
							Point scaledHeightPoint = UIUtilities.transformPoint(oActualSize.height, oActualSize.height, scaleH);
							String newHeight = String.valueOf(scaledHeightPoint.x);
							if (!txtPixelHeight.getText().equals(newHeight)) {
								txtPixelHeight.setText(newHeight);
							}
						} 				
						double scaleW = CoreUtilities.divide(nPercentageWidth, 100);	
						Point scaledWidthPoint = UIUtilities.transformPoint(oActualSize.width, oActualSize.width, scaleW);
						String newWidth = String.valueOf(scaledWidthPoint.x);
						if (!txtPixelWidth.getText().equals(newWidth)) {
							txtPixelWidth.setText(newWidth);
						}
					} catch(NumberFormatException e) {
						e.printStackTrace();
						ProjectCompendium.APP.displayError(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIImageSizeDialog.errorPercentageWidth")); //$NON-NLS-1$
					}
				}
			}
			else if (doc == oPercentageHeightDoc && txtPercentageHeight.hasFocus()) {
				String sPercentageHeight = txtPercentageHeight.getText();
				sPercentageHeight = sPercentageHeight.trim();
				if (!sPercentageHeight.equals("")) { //$NON-NLS-1$
					try {
						int nPercentageHeight = (new Integer(sPercentageHeight)).intValue();
						if (nPercentageHeight < 0) {
							throw new NumberFormatException();
						}
						if (cbMaintainRatio.isSelected()) {	
							txtPercentageWidth.setText(txtPercentageHeight.getText());
							double scaleW = CoreUtilities.divide(nPercentageHeight,100);			
							Point scaledWidthPoint = UIUtilities.transformPoint(oActualSize.width, oActualSize.width, scaleW);							
							String newWidth = String.valueOf(scaledWidthPoint.x); 
							if (!txtPixelWidth.getText().equals(newWidth)) {
								txtPixelWidth.setText(newWidth);
							}
						}
						double scaleH = CoreUtilities.divide(nPercentageHeight,100);
						Point scaledHeightPoint = UIUtilities.transformPoint(oActualSize.height, oActualSize.height, scaleH);
						String newHeight = String.valueOf(scaledHeightPoint.x);
						if (!txtPixelHeight.getText().equals(newHeight)) {
							txtPixelHeight.setText(newHeight);
						}
					} catch(NumberFormatException e) {
						e.printStackTrace();
						ProjectCompendium.APP.displayError(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIImageSizeDialog.errorPercentageHeight")); //$NON-NLS-1$
					}
				}
			}
		}
	} 
	
	/**
	 * Handle action events coming from the buttons.
	 * @param evt, the associated ACtionEvent.
	 */
	public void actionPerformed(ActionEvent evt) {
		Object source = evt.getSource();

		// Handle button events
		if (source == pbSave) {
			setVisible(false);
		} else if (source == pbClose) {
			onCancel();
		}
	}
	
	public Dimension getImageSize() {
		Dimension dim = new Dimension(oCurrentSize.width, oCurrentSize.height);
		if (rbPixel.isSelected()) {
			try {
				dim.width = (new Integer(txtPixelWidth.getText())).intValue();
				dim.height = (new Integer(txtPixelHeight.getText())).intValue();
			} catch(NumberFormatException e) {
				e.printStackTrace();
				dim.width = oCurrentSize.width;
				dim.height = oCurrentSize.height;
			}
		} else {
			try {
				int nPercentageWidth = (new Integer(txtPercentageWidth.getText())).intValue();
				int nPercentageHeight = (new Integer(txtPercentageHeight.getText())).intValue();
				
				double scaleW = CoreUtilities.divide(nPercentageWidth,100);
				double scaleH = CoreUtilities.divide(nPercentageHeight,100);
				
				Point scaledWidthPoint = UIUtilities.transformPoint(oActualSize.width, oActualSize.width, scaleW);
				Point scaledHeightPoint = UIUtilities.transformPoint(oActualSize.height, oActualSize.height, scaleH);

				dim.width = scaledWidthPoint.x;
				dim.height = scaledHeightPoint.x; 				
			} catch(NumberFormatException e) {
				dim.width = oCurrentSize.width;
				dim.height = oCurrentSize.height;
			}
		}
		return dim;
	}
}
