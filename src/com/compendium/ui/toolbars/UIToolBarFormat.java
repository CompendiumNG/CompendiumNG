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
 * This class manages the Node and Link Label Formatting toolbar
 *
 * @author	Michelle Bachler
 * @version	1.0
 */
public class UIToolBarFormat implements IUIToolBar, ActionListener, IUIConstants {
	
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
	
	/** The label holding the icon to indicate background colour.*/
	private JLabel			txtBackgroundColour	= null;

	/** The label holding the icon to indicate foreground colour.*/
	private JLabel			txtForegroundColour	= null;

	/** Opens the foreground colour chooser.*/
	private UIImageButton	btForegroundColour = null;

	/** Opens the background colour chooser.*/
	private UIImageButton	btBackgroundColour = null;

	/** The JPanel to hold the text background colour.*/
	private JPanel			foregroundPanel	= null;

	/** The JPanel to hold the text foreground colour.*/
	private JPanel			backgroundPanel	= null;

	/** The JPanel to hold the text background colour.*/
	private JPanel			forePanel	= null;

	/** The JPanel to hold the text foreground colour.*/
	private JPanel			backPanel	= null;

	/** The choicebox for the node label font face.*/
	private JComboBox 			cbFontFace			= null;

	/** The panel for the font face choice box.*/
	private JPanel 				fontFacePanel 		= null;

	/** The action listener for font face choicebox.*/
	private ActionListener 		fontFaceActionListener 	= null;

	/** The choicebox for the node label font size.*/
	private JComboBox 			cbFontSize			= null;

	/** The panel for the font size choice box.*/
	private JPanel 				fontSizePanel 		= null;

	/** The action listener for the font size choicebox.*/
	private ActionListener 		fontSizeActionListener 	= null;

	/** The button to set node label font to bold.*/
	private JRadioButton		pbBold				= null;

	/** The button to select hiding icons.*/
	private JRadioButton		pbItalic			= null;

	/** The choicebox for the node label wrap width.*/
	private JComboBox 			cbWrapWidth			= null;

	/** The panel for the wrap width choice box.*/
	private JPanel 				wrapWidthPanel 		= null;

	/** The action listener for the wrap width choicebox.*/
	private ActionListener 		wrapWidthActionListener = null;	
	
	/** The referrence to the colour chooser dialog. */
	private UIColorChooserDialog oColorChooserDialog = null;
			
	/** Indicates that the node items are being displayed rather than changed by the user.*/
	private boolean				bJustSetting		= false;
	
	/** The foreground color of the selected nodes, used to set the colour chooser default when opening*/
	private Color				selectedForeground = Color.black;
	
	/** The background color of the selected nodes, used to set the colour chooser default when opening*/
	private Color				selectedBackground = Color.white;

	
	/**
	 * Create a new instance of UIToolBarFormat, with the given properties.
	 * @param oManager the IUIToolBarManager that is managing this toolbar.
	 * @param parent the parent frame for the application.
	 * @param nType the unique identifier for this toolbar.
	 */
	public UIToolBarFormat(IUIToolBarManager oManager, ProjectCompendiumFrame parent, int nType) {

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
	public UIToolBarFormat(IUIToolBarManager oManager, ProjectCompendiumFrame parent, int nType, int orientation) {

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
		
		tbrToolBar = new UIToolBar(LanguageProperties.getString(LanguageProperties.TOOLBARS_BUNDLE, "UIToolBarFormat.name"), UIToolBar.NORTHSOUTH); //$NON-NLS-1$
		tbrToolBar.setOrientation(orientation);
		tbrToolBar.setEnabled(false);
		CSH.setHelpIDString(tbrToolBar,"toolbars.format"); //$NON-NLS-1$
				
		tbrToolBar.add( createFontFaceChoiceBox() );
		tbrToolBar.add( createFontSizeChoiceBox() );

		pbBold = tbrToolBar.createToolBarRadioButton(LanguageProperties.getString(LanguageProperties.TOOLBARS_BUNDLE, "UIToolBarFormat.bold"), UIImages.get(FORMAT_BOLD)); //$NON-NLS-1$
		pbBold.setSelectedIcon(UIImages.get(FORMAT_BOLD_SELECTED));				
		pbBold.addActionListener(this);
		pbBold.setEnabled(true);
		tbrToolBar.add(pbBold);
		CSH.setHelpIDString(pbBold,"toolbars.format"); //$NON-NLS-1$
		
		pbItalic = tbrToolBar.createToolBarRadioButton(LanguageProperties.getString(LanguageProperties.TOOLBARS_BUNDLE, "UIToolBarFormat.italic"), UIImages.get(FORMAT_ITALIC)); //$NON-NLS-1$
		pbItalic.setSelectedIcon(UIImages.get(FORMAT_ITALIC_SELECTED));				
		pbItalic.addActionListener(this);
		pbItalic.setEnabled(true);
		tbrToolBar.add(pbItalic);
		CSH.setHelpIDString(pbItalic,"toolbars.format"); //$NON-NLS-1$
		
		GridBagLayout grid = new GridBagLayout();		
		forePanel = new JPanel(grid);	
		foregroundPanel = new JPanel(new BorderLayout());
		foregroundPanel.setBackground(Color.black);

		JLabel label = new JLabel(" "); //$NON-NLS-1$
		GridBagConstraints con5 = new GridBagConstraints();
		con5.fill = GridBagConstraints.NONE;
		con5.anchor = GridBagConstraints.CENTER;
		grid.addLayoutComponent(label, con5);		
		forePanel.add(label);
		
		txtForegroundColour = new JLabel(UIImages.get(FOREGROUND_COLOUR));
		txtForegroundColour.setBorder(null);
		txtForegroundColour.setToolTipText(LanguageProperties.getString(LanguageProperties.TOOLBARS_BUNDLE, "UIToolBarFormat.selectForeground")); //$NON-NLS-1$
		txtForegroundColour.setEnabled(false);
		txtForegroundColour.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				int clickCount = e.getClickCount();
				if (clickCount == 1 && txtForegroundColour.isEnabled()) {
					onUpdateForeground(foregroundPanel.getBackground().getRGB());						
				}
			}
		});				
		foregroundPanel.add(txtForegroundColour, BorderLayout.CENTER);
		
		GridBagConstraints con = new GridBagConstraints();
		con.fill = GridBagConstraints.NONE;
		con.anchor = GridBagConstraints.CENTER;
		grid.addLayoutComponent(foregroundPanel, con);		
		forePanel.add(foregroundPanel);
		
		btForegroundColour = new UIImageButton(UIImages.get(RIGHT_ARROW_ICON));
		btForegroundColour.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				int clickCount = e.getClickCount();
				if (clickCount == 1 && txtForegroundColour.isEnabled()) {
					if (oColorChooserDialog != null) {
						oColorChooserDialog.setColour(selectedForeground);
					} else {
						oColorChooserDialog = new UIColorChooserDialog(ProjectCompendium.APP, selectedForeground);
					}
					oColorChooserDialog.setVisible(true);
					Color oColour = oColorChooserDialog.getColour();
					oColorChooserDialog.setVisible(false);
					if (oColour != null) {
						foregroundPanel.setBackground(oColour);						
						onUpdateForeground(oColour.getRGB());						
					}
				}
			}
		});		
		forePanel.add(btForegroundColour);

		label = new JLabel(" "); //$NON-NLS-1$
		GridBagConstraints con4 = new GridBagConstraints();
		con4.fill = GridBagConstraints.NONE;
		con4.anchor = GridBagConstraints.CENTER;
		grid.addLayoutComponent(label, con4);		
		forePanel.add(label);

		tbrToolBar.add(forePanel);
		CSH.setHelpIDString(txtForegroundColour,"toolbars.format");		 //$NON-NLS-1$
		
		GridBagLayout grid2 = new GridBagLayout();	
		backPanel = new JPanel(grid2);		
		backgroundPanel = new JPanel(new BorderLayout());
		backgroundPanel.setBackground(Color.white);
		
		txtBackgroundColour = new JLabel(UIImages.get(BACKGROUND_COLOUR));
		txtBackgroundColour.setToolTipText(LanguageProperties.getString(LanguageProperties.TOOLBARS_BUNDLE, "UIToolBarFormat.selectBackground")); //$NON-NLS-1$
		txtBackgroundColour.setEnabled(false);
		txtBackgroundColour.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				int clickCount = e.getClickCount();
				if (clickCount == 1 && txtForegroundColour.isEnabled()) {
					onUpdateBackground(backgroundPanel.getBackground().getRGB());						
				}
			}
		});
		
		backgroundPanel.add(txtBackgroundColour, BorderLayout.CENTER);
		
		GridBagConstraints con2 = new GridBagConstraints();
		con2.fill = GridBagConstraints.NONE;
		con2.anchor = GridBagConstraints.CENTER;
		grid2.addLayoutComponent(backgroundPanel, con2);		
		
		backPanel.add(backgroundPanel);
		
		btBackgroundColour = new UIImageButton(UIImages.get(RIGHT_ARROW_ICON));
		btBackgroundColour.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				int clickCount = e.getClickCount();
				if (clickCount == 1 && txtBackgroundColour.isEnabled()) {
					if (oColorChooserDialog != null) {
						oColorChooserDialog.setColour(selectedBackground);
					} else {
						oColorChooserDialog = new UIColorChooserDialog(ProjectCompendium.APP, selectedBackground);
					}
					oColorChooserDialog.setVisible(true);
					Color oColour = oColorChooserDialog.getColour();
					oColorChooserDialog.setVisible(false);
					if (oColour != null) {
						backgroundPanel.setBackground(oColour);						
						onUpdateBackground(oColour.getRGB());						
					}
				}
			}
		});		
		backPanel.add(btBackgroundColour);
		
		label = new JLabel(" "); //$NON-NLS-1$
		GridBagConstraints con6 = new GridBagConstraints();
		con6.fill = GridBagConstraints.NONE;
		con6.anchor = GridBagConstraints.CENTER;
		grid.addLayoutComponent(label, con6);		
		backPanel.add(label);		
		
		tbrToolBar.add(backPanel);
		CSH.setHelpIDString(txtBackgroundColour,"toolbars.format"); //$NON-NLS-1$
		
		tbrToolBar.addSeparator();
		
		tbrToolBar.add( createWrapWidthChoiceBox() );		

		return tbrToolBar;
	}
		
	/**
	 * Create a choicbox for node label font face setting.
	 * @return JPanel, the panel holding the new choicebox for the node label font face.
	 */
	private JPanel createFontFaceChoiceBox() {

		BorderLayout layout = new BorderLayout();
		layout.setHgap(0);
		layout.setVgap(0);		
		fontFacePanel = new JPanel(layout);
		CSH.setHelpIDString(fontFacePanel,"toolbars.format"); //$NON-NLS-1$

		cbFontFace = new JComboBox();
		cbFontFace.setOpaque(true);
		cbFontFace.setEditable(false);
		cbFontFace.setEnabled(false);
		cbFontFace.setMaximumRowCount(10);
		cbFontFace.setFont( new Font("Dialog", Font.PLAIN, 10 )); //$NON-NLS-1$

   	 	String[] fonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
   	 	int count = fonts.length;
		for (int i=0; i<count; i++) {
			cbFontFace.addItem(fonts[i]);
		}

		cbFontFace.validate();
		cbFontFace.setSelectedIndex(0);

		DefaultListCellRenderer fontFaceRenderer = new DefaultListCellRenderer() {
			
		  	protected Border noFocusBorder = new EmptyBorder(1, 1, 1, 1);
			
			public Component getListCellRendererComponent(
   		     	JList list,
   		        Object value,
            	int modelIndex,
            	boolean isSelected,
            	boolean cellHasFocus)
            {				
				setOpaque(true);
				
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

				String text = (String) value;
	   			setFont( new Font((String)value, Font.PLAIN, 12) );
				for(int i = 0 ; i < 5; i++) {
	            	text += " "; //$NON-NLS-1$
	      		}

				setBorder((cellHasFocus) ? UIManager.getBorder("List.focusCellHighlightBorder") : noFocusBorder); //$NON-NLS-1$
				setText(text);
				return this;			
			}
		};

		cbFontFace.setRenderer(fontFaceRenderer);

		fontFaceActionListener = new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
           		if (!bJustSetting) {        		
           			String sFace = (String)cbFontFace.getSelectedItem();
           			onUpdateFontFace(sFace);
           		}
         	}
		};
		cbFontFace.addActionListener(fontFaceActionListener);

		fontFacePanel.add(new JLabel(" "), BorderLayout.WEST); //$NON-NLS-1$
		fontFacePanel.add(cbFontFace, BorderLayout.CENTER);
		fontFacePanel.add(new JLabel(" "), BorderLayout.EAST);		 //$NON-NLS-1$
		return fontFacePanel;
	}	
	
	/**
	 * Create a choicbox for node label size face setting.
	 * @return JPanel, the panel holding the new choicebox for the node label size font face.
	 */
	private JPanel createFontSizeChoiceBox() {

		BorderLayout layout = new BorderLayout();
		layout.setHgap(0);
		layout.setVgap(0);		
		fontSizePanel = new JPanel(layout);
		CSH.setHelpIDString(fontSizePanel,"toolbars.format"); //$NON-NLS-1$

 	 	String[] sizes = {"8","10","12","14","16","18","20","22","24","26","28","30"}; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$ //$NON-NLS-10$ //$NON-NLS-11$ //$NON-NLS-12$

		cbFontSize = new JComboBox(sizes);
		cbFontSize.setOpaque(true);
		cbFontSize.setEditable(true);
		cbFontSize.setEnabled(false);
		cbFontSize.setMaximumRowCount(10);
		cbFontSize.setFont( new Font("Dialog", Font.PLAIN, 10 ));		 //$NON-NLS-1$
		
		cbFontSize.setEditor(new MyComboBoxEditor(4));
		
		cbFontSize.validate();

		cbFontSize.setSelectedIndex(0);

		DefaultListCellRenderer fontSizeRenderer = new DefaultListCellRenderer() {
			public Component getListCellRendererComponent(
   		     	JList list,
   		        Object value,
            	int modelIndex,
            	boolean isSelected,
            	boolean cellHasFocus)
            {
				setOpaque(true);

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

				Font font = getFont();
	   			setFont( new Font(font.getFontName(), font.getStyle(), 12) );
				setText((String) value);
				return this;
			}
		};

		cbFontSize.setRenderer(fontSizeRenderer);

		fontSizeActionListener = new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		if (!bJustSetting) {
           			Object obj = cbFontSize.getSelectedItem();
           			Integer nSize = new Integer(-1);
           			if (obj instanceof String) {
           				String item = (String)obj;	 				
						if (!item.equals("")) { //$NON-NLS-1$
							try {
								nSize = new Integer(item);
							} catch(Exception ex) {
								ProjectCompendium.APP.displayError(LanguageProperties.getString(LanguageProperties.TOOLBARS_BUNDLE, "UIToolBarFormat.validNumbers")); //$NON-NLS-1$
								return;
							}
						}
           			} else if (obj instanceof Integer) {
           				nSize = (Integer)obj;
						
           			}
					if (nSize > -1) {	
						int size = nSize.intValue();
						if (size <= 0) {
							ProjectCompendium.APP.displayError(LanguageProperties.getString(LanguageProperties.TOOLBARS_BUNDLE, "UIToolBarFormat.positiveNumbers")); //$NON-NLS-1$
						} else if (size > 500) {
							ProjectCompendium.APP.displayError(LanguageProperties.getString(LanguageProperties.TOOLBARS_BUNDLE, "UIToolBarFormat.silly"));							 //$NON-NLS-1$
						} else {
							onUpdateFontSize(size);
						}
					}
	         	}
         	}
		};
		cbFontSize.addActionListener(fontSizeActionListener);
		
		fontSizePanel.add(new JLabel(" "), BorderLayout.WEST);		 //$NON-NLS-1$
        fontSizePanel.add(cbFontSize, BorderLayout.CENTER);
        fontSizePanel.add(new JLabel(" "), BorderLayout.EAST);      //$NON-NLS-1$
 		return fontSizePanel;
	}		
	
	/**
	 * Create a choicbox for node label size face setting.
	 * @return JPanel, the panel holding the new choicebox for the node label size font face.
	 */
	private JPanel createWrapWidthChoiceBox() {

		BorderLayout layout = new BorderLayout();
		layout.setHgap(0);
		layout.setVgap(0);
		wrapWidthPanel = new JPanel(layout);
		
		CSH.setHelpIDString(wrapWidthPanel,"toolbars.format"); //$NON-NLS-1$

	 	String[] widths = {"5","10","15","20","25","30","35","40","45","50"}; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$ //$NON-NLS-10$
		
		cbWrapWidth = new JComboBox(widths);
		cbWrapWidth.setOpaque(true);
		cbWrapWidth.setEditable(true);
		cbWrapWidth.setEnabled(false);
		cbWrapWidth.setMaximumRowCount(10);
		cbWrapWidth.setFont( new Font("Dialog", Font.PLAIN, 10 )); //$NON-NLS-1$

		cbWrapWidth.setEditor(new MyComboBoxEditor(4));
				
		cbWrapWidth.validate();

		cbWrapWidth.setSelectedIndex(0);

		DefaultListCellRenderer wrapWidthRenderer = new DefaultListCellRenderer() {
			public Component getListCellRendererComponent(
   		     	JList list,
   		        Object value,
            	int modelIndex,
            	boolean isSelected,
            	boolean cellHasFocus)
            {
				setOpaque(true);

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

				Font font = getFont();
	   			setFont( new Font(font.getFontName(), font.getStyle(), 12) );
				setText((String) value);
				return this;
			}
		};

		cbWrapWidth.setRenderer(wrapWidthRenderer);

		wrapWidthActionListener = new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
           		if (!bJustSetting) {        		
           			Object obj = cbWrapWidth.getSelectedItem();
           			Integer wrap = new Integer(-1);
           			if (obj instanceof String) {
           				String item = (String)obj;	 				
						if (!item.equals("")) { //$NON-NLS-1$
							wrap = new Integer(0);
							try {
								wrap = new Integer(item);
							} catch(Exception ex) {
								ProjectCompendium.APP.displayError(LanguageProperties.getString(LanguageProperties.TOOLBARS_BUNDLE, "UIToolBarFormat.validNumbers")); //$NON-NLS-1$
								return;
							}
						}
           			} else if (obj instanceof Integer) {
           				wrap = (Integer)obj;
						
           			}
					if (wrap.intValue() > -1) {	
						int wrapValue = wrap.intValue();
						if (wrapValue <= 0) {
							ProjectCompendium.APP.displayError(LanguageProperties.getString(LanguageProperties.TOOLBARS_BUNDLE, "UIToolBarFormat.positiveNumbers")); //$NON-NLS-1$
						} else if (wrapValue > 500) {
							ProjectCompendium.APP.displayError(LanguageProperties.getString(LanguageProperties.TOOLBARS_BUNDLE, "UIToolBarFormat.silly"));							 //$NON-NLS-1$
						} else {
							onUpdateWrapWidth(wrapValue);
						}
					}
	         	}
        	}
		};
		cbWrapWidth.addActionListener(wrapWidthActionListener);
		
		cbWrapWidth.setToolTipText(LanguageProperties.getString(LanguageProperties.TOOLBARS_BUNDLE, "UIToolBarFormat.wrapWidth"));		 //$NON-NLS-1$

		JLabel label  = new JLabel(UIImages.get(WRAP_WIDTH));
		label.setToolTipText(LanguageProperties.getString(LanguageProperties.TOOLBARS_BUNDLE, "UIToolBarFormat.wrapWidth")); //$NON-NLS-1$
		
		JPanel panel = new JPanel();
		panel.add(new JLabel(" ")); //$NON-NLS-1$
		panel.add(label);
		panel.add(new JLabel(" ")); //$NON-NLS-1$
		wrapWidthPanel.add(panel, BorderLayout.WEST);
		wrapWidthPanel.add(cbWrapWidth, BorderLayout.CENTER);		
		wrapWidthPanel.add(new JLabel(" "), BorderLayout.EAST);						 //$NON-NLS-1$
		return wrapWidthPanel;
	}			
	
	/**
	 * Inner class, just to set the width of the editor for 
	 * the choiceboxes as it was drawing too large on the toolbar.
	 * @author msb262
	 */
	private class MyComboBoxEditor implements ComboBoxEditor, FocusListener {
	
		protected JTextField editor;
	    private Object oldValue;

		public MyComboBoxEditor() {
			editor = new JTextField();
			editor.setBorder(UIManager.getBorder("List.focusCellHighlightBorder")); //$NON-NLS-1$
		}

		public MyComboBoxEditor(int columns) {
			editor = new JTextField();			
			editor.setColumns(columns);			
			editor.setBorder(UIManager.getBorder("List.focusCellHighlightBorder")); //$NON-NLS-1$
		}
		
	    public Component getEditorComponent() {
	        return editor;
	    }

	    /** 
	     * Sets the item that should be edited. 
	     *
	     * @param anObject the displayed value of the editor
	     */
	    public void setItem(Object anObject) {
	        if ( anObject != null )  {
	            editor.setText(anObject.toString());
	            
	            oldValue = anObject;
	        } else {
	            editor.setText(""); //$NON-NLS-1$
	        }
	    }

	    public Object getItem() {
	        Object newValue = editor.getText();
	        
	        if (oldValue != null && !(oldValue instanceof String))  {
	            // The original value is not a string. Should return the value in it's
	            // original type.
	            if (newValue.equals(oldValue.toString()))  {
	                return oldValue;
	            } else {
	                // Must take the value from the editor and get the value and cast it to the new type.
	                Class cls = oldValue.getClass();
	                try {
	                    Method method = cls.getMethod("valueOf", new Class[]{String.class}); //$NON-NLS-1$
	                    newValue = method.invoke(oldValue, new Object[] { editor.getText()});
	                } catch (Exception ex) {
	                    // Fail silently and return the newValue (a String object)
	                }
	            }
	        }
	        return newValue;
	    }

	    public void selectAll() {
	        editor.selectAll();
	        editor.requestFocus();
	    }

	    // This used to do something but now it doesn't.  It couldn't be
	    // removed because it would be an API change to do so.
	    public void focusGained(FocusEvent e) {}
	    
	    // This used to do something but now it doesn't.  It couldn't be
	    // removed because it would be an API change to do so.
	    public void focusLost(FocusEvent e) {}

	    public void addActionListener(ActionListener l) {
	        editor.addActionListener(l);
	    }

	    public void removeActionListener(ActionListener l) {
	        editor.removeActionListener(l);
	    }
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

		if (source.equals(pbBold) || source.equals(pbItalic)) {
			int fontstyle = 0;
			if (pbBold.isSelected() && pbItalic.isSelected())
        		fontstyle = Font.BOLD+Font.ITALIC;
			else if (pbBold.isSelected() && !pbItalic.isSelected())
				fontstyle = Font.BOLD;
			else if (!pbBold.isSelected() && pbItalic.isSelected())
				fontstyle = Font.ITALIC;
			else if (!pbBold.isSelected() && !pbItalic.isSelected())
				fontstyle = Font.PLAIN;

			onUpdateFontStyle(fontstyle);
		}
		oParent.setDefaultCursor();
	}
	
	/**
	 * Update the node label wrap width on the currently selected nodes and links.
	 */
	private void onUpdateWrapWidth(int width) {
		if (!bJustSetting) {
			
			String sInBoxID = oParent.getInBoxID();
			String sTrashBinID = oParent.getTrashBinID();
			
			UIViewFrame frame = ProjectCompendium.APP.getCurrentFrame();
			Model oModel = (Model)ProjectCompendium.APP.getModel();
			if (frame instanceof UIMapViewFrame) {				
				UIMapViewFrame oMapFrame = (UIMapViewFrame)frame;
				UIViewPane pane = oMapFrame.getViewPane();

				//UPDATE ANY SELECTED NODES				
				Vector vtUpdateNodes = new Vector();												
				UINode node = null;
				NodePosition pos = null;
				String sNodeID  =""; //$NON-NLS-1$
				for (Enumeration e = pane.getSelectedNodes(); e.hasMoreElements();) {
					node = (UINode)e.nextElement();
					
					sNodeID = node.getNode().getId();
					if (!sNodeID.equals(sInBoxID) && !sNodeID.equals(sTrashBinID)) {					
						pos = node.getNodePosition();
						if (width != pos.getLabelWrapWidth()) {
							vtUpdateNodes.addElement(pos);
						}
					}
				}
				
				boolean displayError = false;
				String sMessage = "";//$NON-NLS-1$
				if (vtUpdateNodes.size() > 0) {				
					try {
						((ViewService)oModel.getViewService()).setWrapWidth(oModel.getSession(), pane.getView().getId(), vtUpdateNodes, width);
						int count = vtUpdateNodes.size();
						for (int i=0; i<count;i++) {
							pos = (NodePosition)vtUpdateNodes.elementAt(i);
							pos.setLabelWrapWidth(width);
						}
					} catch (SQLException ex) {
						displayError = true;
						sMessage = ex.getLocalizedMessage();
					}
				}
				
				// UPDATE ANY SELECTED LINKS
				Vector vtUpdateLinks = new Vector();								
				UILink link = null;
				LinkProperties props = null;
				for (Enumeration e = pane.getSelectedLinks(); e.hasMoreElements();) {
					link = (UILink)e.nextElement();					
					props = link.getLinkProperties();
					if (width != props.getLabelWrapWidth()) {
						vtUpdateLinks.addElement(props);
					}
				}
				
				if (vtUpdateLinks.size() > 0) {				
					try {
						((ViewService)oModel.getViewService()).setLinkWrapWidth(oModel.getSession(), pane.getView().getId(), vtUpdateLinks, width);
						int count = vtUpdateLinks.size();
						for (int i=0; i<count;i++) {
							props = (LinkProperties)vtUpdateLinks.elementAt(i);
							props.setLabelWrapWidth(width);
						}
					} catch (SQLException ex) {
						displayError = true;
						sMessage += "\n\n"+ex.getLocalizedMessage();//$NON-NLS-1$
					}
				}
				
				if (displayError) {
					ProjectCompendium.APP.displayError(LanguageProperties.getString(LanguageProperties.TOOLBARS_BUNDLE, "UIToolBarFormat.unableUpdateWrapWidth")+":\n\n"+sMessage); //$NON-NLS-1$
				}																
			}
		}					
	}
	
	/**
	 * Update the node label font face on the currently selected nodes and links.
	 */
	private void onUpdateFontFace(String sFontFace) {
		if (!bJustSetting) {
			
			String sInBoxID = oParent.getInBoxID();
			String sTrashBinID = oParent.getTrashBinID();
			
			UIViewFrame frame = ProjectCompendium.APP.getCurrentFrame();
			Model oModel = (Model)ProjectCompendium.APP.getModel();
			if (frame instanceof UIMapViewFrame) {
				UIMapViewFrame oMapFrame = (UIMapViewFrame)frame;
				UIViewPane pane = oMapFrame.getViewPane();

				//UPDATE ANY SELECTED NODES				
				Vector vtUpdateNodes = new Vector();												
				UINode node = null;
				NodePosition pos = null;
				String sNodeID = ""; //$NON-NLS-1$
				for (Enumeration e = pane.getSelectedNodes(); e.hasMoreElements();) {
					node = (UINode)e.nextElement();
					sNodeID = node.getNode().getId();
					if (!sNodeID.equals(sInBoxID) && !sNodeID.equals(sTrashBinID)) {					
						pos = node.getNodePosition();					
						if (!sFontFace.equals(pos.getFontFace())) {
							vtUpdateNodes.addElement(pos);
						}
					}
				}
				
				boolean displayError = false;
				String sMessage = "";//$NON-NLS-1$
				if (vtUpdateNodes.size() > 0) {				
					try {
						((ViewService)oModel.getViewService()).setFontFace(oModel.getSession(), pane.getView().getId(), vtUpdateNodes, sFontFace);
						int count = vtUpdateNodes.size();
						for (int i=0; i<count;i++) {
							pos = (NodePosition)vtUpdateNodes.elementAt(i);
							pos.setFontFace(sFontFace);
						}
					} catch (SQLException ex) {
						displayError = true;
						sMessage = ex.getLocalizedMessage();
					}
				}
				
				// UPDATE ANY SELECTED LINKS
				Vector vtUpdateLinks = new Vector();								
				UILink link = null;
				LinkProperties props = null;
				for (Enumeration e = pane.getSelectedLinks(); e.hasMoreElements();) {
					link = (UILink)e.nextElement();					
					props = link.getLinkProperties();
					if (sFontFace != props.getFontFace()) {
						vtUpdateLinks.addElement(props);
					}
				}
				
				if (vtUpdateLinks.size() > 0) {				
					try {
						((ViewService)oModel.getViewService()).setLinkFontFace(oModel.getSession(), pane.getView().getId(), vtUpdateLinks, sFontFace);
						int count = vtUpdateLinks.size();
						for (int i=0; i<count;i++) {
							props = (LinkProperties)vtUpdateLinks.elementAt(i);
							props.setFontFace(sFontFace);
						}
					} catch (SQLException ex) {
						displayError = true;
						sMessage += "\n\n"+ex.getLocalizedMessage();//$NON-NLS-1$
					}
				}
				
				if (displayError) {
					ProjectCompendium.APP.displayError(LanguageProperties.getString(LanguageProperties.TOOLBARS_BUNDLE, "UIToolBarFormat.unableUpdateFontFace")+":\n\n"+sMessage); //$NON-NLS-1$
				}												
			}
		}					
	}
	
	/**
	 * Update the node label font size on the currently selected nodes and links.
	 */
	private void onUpdateFontSize(int nFontSize) {
		if (!bJustSetting) {
			
			String sInBoxID = oParent.getInBoxID();
			String sTrashBinID = oParent.getTrashBinID();

			UIViewFrame frame = ProjectCompendium.APP.getCurrentFrame();
			Model oModel = (Model)ProjectCompendium.APP.getModel();
			if (frame instanceof UIMapViewFrame) {
				UIMapViewFrame oMapFrame = (UIMapViewFrame)frame;
				UIViewPane pane = oMapFrame.getViewPane();

				//UPDATE ANY SELECTED NODES				
				Vector vtUpdateNodes = new Vector();	
				Vector vtUpdateUINodes = new Vector();
				UINode node = null;
				NodePosition pos = null;
				String sNodeID = ""; //$NON-NLS-1$
				for (Enumeration e = pane.getSelectedNodes(); e.hasMoreElements();) {
					node = (UINode)e.nextElement();
					
					sNodeID = node.getNode().getId();
					if (!sNodeID.equals(sInBoxID) && !sNodeID.equals(sTrashBinID)) {					
						pos = node.getNodePosition();		
						if (nFontSize != pos.getFontSize()) {
							vtUpdateNodes.addElement(pos);
							vtUpdateUINodes.addElement(node);
						}
					}
				}
				
				boolean displayError = false;
				String sMessage = "";//$NON-NLS-1$
				if (vtUpdateNodes.size() > 0) {				
					try {
						((ViewService)oModel.getViewService()).setFontSize(oModel.getSession(), pane.getView().getId(), vtUpdateNodes, nFontSize);
						int count = vtUpdateNodes.size();
						for (int i=0; i<count;i++) {
							pos = (NodePosition)vtUpdateNodes.elementAt(i);
							pos.setFontSize(nFontSize);
						}
					} catch (SQLException ex) {
						displayError = true;
						sMessage = ex.getLocalizedMessage();
					}
				}
				if (vtUpdateUINodes.size() > 0) {			// Update node coordinates since label length changed
					int count = vtUpdateNodes.size();
					for (int i=0; i<count;i++) {
						node = (UINode)vtUpdateUINodes.elementAt(i);
						node.getUI().flushPosition();
					}
				}
				
				// UPDATE ANY SELECTED LINKS
				Vector vtUpdateLinks = new Vector();								
				UILink link = null;
				LinkProperties props = null;
				for (Enumeration e = pane.getSelectedLinks(); e.hasMoreElements();) {
					link = (UILink)e.nextElement();					
					props = link.getLinkProperties();
					if (nFontSize != props.getFontSize()) {
						vtUpdateLinks.addElement(props);
					}
				}
				
				if (vtUpdateLinks.size() > 0) {				
					try {
						((ViewService)oModel.getViewService()).setLinkFontSize(oModel.getSession(), pane.getView().getId(), vtUpdateLinks, nFontSize);
						int count = vtUpdateLinks.size();
						for (int i=0; i<count;i++) {
							props = (LinkProperties)vtUpdateLinks.elementAt(i);
							props.setFontSize(nFontSize);
						}
					} catch (SQLException ex) {
						displayError = true;
						sMessage += "\n\n"+ex.getLocalizedMessage();//$NON-NLS-1$
					}
				}
				
				if (displayError) {
					ProjectCompendium.APP.displayError(LanguageProperties.getString(LanguageProperties.TOOLBARS_BUNDLE, "UIToolBarFormat.unableUpdateFontSize")+":\n\n"+sMessage); //$NON-NLS-1$
				}								
			}
		}					
	}
	
	/**
	 * Update the node label font style on the currently selected nodes and links.
	 */
	private void onUpdateFontStyle(int nFontStyle) {
		if (!bJustSetting) {
			
			String sInBoxID = oParent.getInBoxID();
			String sTrashBinID = oParent.getTrashBinID();
			
			UIViewFrame frame = ProjectCompendium.APP.getCurrentFrame();
			Model oModel = (Model)ProjectCompendium.APP.getModel();
			if (frame instanceof UIMapViewFrame) {
				UIMapViewFrame oMapFrame = (UIMapViewFrame)frame;
				UIViewPane pane = oMapFrame.getViewPane();

				//UPDATE ANY SELECTED NODES				
				Vector vtUpdateNodes = new Vector();				
				UINode node = null;
				NodePosition pos = null;
				String sNodeID = ""; //$NON-NLS-1$
				for (Enumeration e = pane.getSelectedNodes(); e.hasMoreElements();) {
					node = (UINode)e.nextElement();
					
					sNodeID = node.getNode().getId();
					if (!sNodeID.equals(sInBoxID) && !sNodeID.equals(sTrashBinID)) {					
						pos = node.getNodePosition();
						if (nFontStyle != pos.getFontStyle()) {
							vtUpdateNodes.addElement(pos);
						}
					}
				}
				
				boolean displayError = false;
				String sMessage = "";//$NON-NLS-1$
				if (vtUpdateNodes.size() > 0) {
					try {
						((ViewService)oModel.getViewService()).setFontStyle(oModel.getSession(), pane.getView().getId(), vtUpdateNodes, nFontStyle);
						int count = vtUpdateNodes.size();
						for (int i=0; i<count;i++) {
							pos = (NodePosition)vtUpdateNodes.elementAt(i);
							pos.setFontStyle(nFontStyle);
						}
					} catch (SQLException ex) {
						displayError = true;
						sMessage = ex.getLocalizedMessage();
					}
				}
				
				// UPDATE ANY SELECTED LINKS
				Vector vtUpdateLinks = new Vector();								
				UILink link = null;
				LinkProperties props = null;
				for (Enumeration e = pane.getSelectedLinks(); e.hasMoreElements();) {
					link = (UILink)e.nextElement();					
					props = link.getLinkProperties();
					if (nFontStyle != props.getFontStyle()) {
						vtUpdateLinks.addElement(props);
					}
				}
				
				if (vtUpdateLinks.size() > 0) {				
					try {
						((ViewService)oModel.getViewService()).setLinkFontStyle(oModel.getSession(), pane.getView().getId(), vtUpdateLinks, nFontStyle);
						int count = vtUpdateLinks.size();
						for (int i=0; i<count;i++) {
							props = (LinkProperties)vtUpdateLinks.elementAt(i);
							props.setFontStyle(nFontStyle);
						}
					} catch (SQLException ex) {
						displayError = true;
						sMessage += "\n\n"+ex.getLocalizedMessage();//$NON-NLS-1$
					}
				}
				
				if (displayError) {
					ProjectCompendium.APP.displayError(LanguageProperties.getString(LanguageProperties.TOOLBARS_BUNDLE, "UIToolBarFormat.unableUpdateFontStyle")+":\n\n"+sMessage); //$NON-NLS-1$
				}				
			}
		}					
	}	
	
	/**
	 * Update the node label font style on the currently selected nodes and link by 
	 * adding / removing the given style depending on existing style.
	 */	
	public void addFontStyle(int nStyle) {
		
		String sInBoxID = ProjectCompendium.APP.getInBoxID();
		String sTrashBinID = ProjectCompendium.APP.getTrashBinID();
				
		UIViewFrame frame = ProjectCompendium.APP.getCurrentFrame();
		Model oModel = (Model)ProjectCompendium.APP.getModel();
		if (frame instanceof UIMapViewFrame) {
			UIMapViewFrame oMapFrame = (UIMapViewFrame)frame;
			UIViewPane pane = oMapFrame.getViewPane();

			//UPDATE ANY SELECTED NODES				
			UINode node = null;
			NodePosition pos = null;
			Vector vtUpdateNodes = new Vector();				
			String sNodeID = ""; //$NON-NLS-1$
			for (Enumeration e = pane.getSelectedNodes(); e.hasMoreElements();) {
				node = (UINode)e.nextElement();							
				sNodeID = node.getNode().getId();
				if (!sNodeID.equals(sInBoxID) && !sNodeID.equals(sTrashBinID)) {									
					pos = node.getNodePosition();
					vtUpdateNodes.addElement(pos);
				}
			}
			
			boolean displayError = false;
			String sMessage = "";//$NON-NLS-1$
			if (vtUpdateNodes.size() > 0) {
				try {
					int count = vtUpdateNodes.size();
					int style = 0;
					int nFontStyle = 0;
					Vector inner = null;
					for (int i=0; i<count;i++) {
						pos = (NodePosition)vtUpdateNodes.elementAt(i);
						style = pos.getFontStyle();
						if (style == Font.PLAIN) {
							nFontStyle = nStyle;
						} else if (style == nStyle) {
							nFontStyle = Font.PLAIN;
						} else if ((style == Font.BOLD && nStyle == Font.ITALIC) 
								|| (style == Font.ITALIC && nStyle == Font.BOLD) ) {
							nFontStyle = Font.BOLD+Font.ITALIC;
						} else if ((style == Font.BOLD+Font.ITALIC && nStyle == Font.BOLD)) {
							nFontStyle = Font.ITALIC;
						} else if ((style == Font.BOLD+Font.ITALIC && nStyle == Font.ITALIC)) {
							nFontStyle = Font.BOLD;
						}
						inner = new Vector();
						inner.add(pos);
						((ViewService)oModel.getViewService()).setFontStyle(oModel.getSession(), pane.getView().getId(), inner, nFontStyle);
						pos.setFontStyle(nFontStyle);						
					}
				} catch (SQLException ex) {
					displayError = true;
					sMessage = ex.getLocalizedMessage();
				}
				
				// UPDATE ANY SELECTED LINKS
				Vector vtUpdateLinks = new Vector();								
				UILink link = null;
				LinkProperties props = null;
				for (Enumeration e = pane.getSelectedLinks(); e.hasMoreElements();) {
					link = (UILink)e.nextElement();					
					vtUpdateLinks.addElement(link.getLinkProperties());
				}
				
				if (vtUpdateLinks.size() > 0) {				
					try {
						int count = vtUpdateLinks.size();
						int style = 0;
						int nFontStyle = 0;
						Vector inner = null;
						for (int i=0; i<count;i++) {
							props = (LinkProperties)vtUpdateLinks.elementAt(i);
							style = props.getFontStyle();
							if (style == Font.PLAIN) {
								nFontStyle = nStyle;
							} else if (style == nStyle) {
								nFontStyle = Font.PLAIN;
							} else if ((style == Font.BOLD && nStyle == Font.ITALIC) 
									|| (style == Font.ITALIC && nStyle == Font.BOLD) ) {
								nFontStyle = Font.BOLD+Font.ITALIC;
							} else if ((style == Font.BOLD+Font.ITALIC && nStyle == Font.BOLD)) {
								nFontStyle = Font.ITALIC;
							} else if ((style == Font.BOLD+Font.ITALIC && nStyle == Font.ITALIC)) {
								nFontStyle = Font.BOLD;
							}
							inner = new Vector();
							inner.add(pos);
							((ViewService)oModel.getViewService()).setLinkFontStyle(oModel.getSession(), pane.getView().getId(), vtUpdateLinks, nFontStyle);
							props.setFontStyle(nFontStyle);
						}
					} catch (SQLException ex) {
						displayError = true;
						sMessage += "\n\n"+ex.getLocalizedMessage();//$NON-NLS-1$
					}
				}
				
				if (displayError) {
					ProjectCompendium.APP.displayError(LanguageProperties.getString(LanguageProperties.TOOLBARS_BUNDLE, "UIToolBarFormat.unableUpdateFontStyle")+":\n\n"+sMessage); //$NON-NLS-1$
				}								
			}
		}		
	}
	
	/**
	 * Update the node label foreground on the currently selected nodes and links.
	 */
	private void onUpdateForeground(int nForeground) {
		if (!bJustSetting) {
			
			String sInBoxID = oParent.getInBoxID();
			String sTrashBinID = oParent.getTrashBinID();
			
			UIViewFrame frame = ProjectCompendium.APP.getCurrentFrame();
			Model oModel = (Model)ProjectCompendium.APP.getModel();
			if (frame instanceof UIMapViewFrame) {
				UIMapViewFrame oMapFrame = (UIMapViewFrame)frame;
				UIViewPane pane = oMapFrame.getViewPane();
				
				//UPDATE ANY SELECTED NODES
				Vector vtUpdateNodes = new Vector();								
				UINode node = null;
				NodePosition pos = null;
				String sNodeID = ""; //$NON-NLS-1$
				for (Enumeration e = pane.getSelectedNodes(); e.hasMoreElements();) {
					node = (UINode)e.nextElement();
					
					sNodeID = node.getNode().getId();
					if (!sNodeID.equals(sInBoxID) && !sNodeID.equals(sTrashBinID)) {					
						pos = node.getNodePosition();
						if (nForeground != pos.getForeground()) {
							vtUpdateNodes.addElement(pos);
						}
					}
				}
				
				boolean displayError = false;
				String sMessage = "";//$NON-NLS-1$
				if (vtUpdateNodes.size() > 0) {				
					try {
						((ViewService)oModel.getViewService()).setTextForeground(oModel.getSession(), pane.getView().getId(), vtUpdateNodes, nForeground);
						int count = vtUpdateNodes.size();
						for (int i=0; i<count;i++) {
							pos = (NodePosition)vtUpdateNodes.elementAt(i);
							pos.setForeground(nForeground);
						}
					} catch (SQLException ex) {
						displayError = true;
						sMessage = ex.getLocalizedMessage();
					}
				}
				
				// UPDATE ANY SELECTED LINKS
				Vector vtUpdateLinks = new Vector();								
				UILink link = null;
				LinkProperties props = null;
				for (Enumeration e = pane.getSelectedLinks(); e.hasMoreElements();) {
					link = (UILink)e.nextElement();					
					props = link.getLinkProperties();
					if (nForeground != props.getForeground()) {
						vtUpdateLinks.addElement(props);
					}
				}
				
				if (vtUpdateLinks.size() > 0) {				
					try {
						((ViewService)oModel.getViewService()).setLinkTextForeground(oModel.getSession(), pane.getView().getId(), vtUpdateLinks, nForeground);
						int count = vtUpdateLinks.size();
						for (int i=0; i<count;i++) {
							props = (LinkProperties)vtUpdateLinks.elementAt(i);
							props.setForeground(nForeground);
						}
					} catch (SQLException ex) {
						displayError = true;
						sMessage += "\n\n"+ex.getLocalizedMessage();//$NON-NLS-1$
					}
				}
				
				if (displayError) {
					ProjectCompendium.APP.displayError(LanguageProperties.getString(LanguageProperties.TOOLBARS_BUNDLE, "UIToolBarFormat.unableUpdateForeground")+":\n\n"+sMessage); //$NON-NLS-1$
				}
			}
		}					
	}	
	
	/**
	 * Update the node label background on the currently selected nodes and links.
	 */
	private void onUpdateBackground(int nBackground) {
		if (!bJustSetting) {
			
			String sInBoxID = oParent.getInBoxID();
			String sTrashBinID = oParent.getTrashBinID();
			
			UIViewFrame frame = ProjectCompendium.APP.getCurrentFrame();
			Model oModel = (Model)ProjectCompendium.APP.getModel();
			if (frame instanceof UIMapViewFrame) {
				UIMapViewFrame oMapFrame = (UIMapViewFrame)frame;
				UIViewPane pane = oMapFrame.getViewPane();

				//UPDATE ANY SELECTED NODES				
				Vector vtUpdateNodes = new Vector();												
				UINode node = null;
				NodePosition pos = null;
				String sNodeID = ""; //$NON-NLS-1$
				for (Enumeration e = pane.getSelectedNodes(); e.hasMoreElements();) {
					node = (UINode)e.nextElement();
					
					sNodeID = node.getNode().getId();
					if (!sNodeID.equals(sInBoxID) && !sNodeID.equals(sTrashBinID)) {					
						pos = node.getNodePosition();
						if (nBackground != pos.getBackground()) {
							vtUpdateNodes.addElement(pos);
						}
					}
				}
				
				boolean displayError = false;
				String sMessage = "";//$NON-NLS-1$
				if (vtUpdateNodes.size() > 0) {				
					try {
						((ViewService)oModel.getViewService()).setTextBackground(oModel.getSession(), pane.getView().getId(), vtUpdateNodes, nBackground);
						int count = vtUpdateNodes.size();
						for (int i=0; i<count;i++) {
							pos = (NodePosition)vtUpdateNodes.elementAt(i);
							pos.setBackground(nBackground);
						}
					} catch (SQLException ex) {
						displayError = true;
						sMessage = ex.getLocalizedMessage();
					}
				}
				
				// UPDATE ANY SELECTED LINKS
				Vector vtUpdateLinks = new Vector();								
				UILink link = null;
				LinkProperties props = null;
				for (Enumeration e = pane.getSelectedLinks(); e.hasMoreElements();) {
					link = (UILink)e.nextElement();					
					props = link.getLinkProperties();
					if (nBackground != props.getBackground()) {
						vtUpdateLinks.addElement(props);
					}
				}
				
				if (vtUpdateLinks.size() > 0) {				
					try {
						((ViewService)oModel.getViewService()).setLinkTextBackground(oModel.getSession(), pane.getView().getId(), vtUpdateLinks, nBackground);
						int count = vtUpdateLinks.size();
						for (int i=0; i<count;i++) {
							props = (LinkProperties)vtUpdateLinks.elementAt(i);
							props.setBackground(nBackground);
						}
					} catch (SQLException ex) {
						displayError = true;
						sMessage += "\n\n"+ex.getLocalizedMessage();//$NON-NLS-1$
					}
				}
				
				if (displayError) {
					ProjectCompendium.APP.displayError(LanguageProperties.getString(LanguageProperties.TOOLBARS_BUNDLE, "UIToolBarFormat.unableUpdateBackground")+":\n\n"+sMessage); //$NON-NLS-1$
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
			Model oModel = (Model)ProjectCompendium.APP.getModel();
			
			if (selected && frame instanceof UIMapViewFrame) {												
				UIMapViewFrame oMapFrame = (UIMapViewFrame)frame;
				UIViewPane pane = oMapFrame.getViewPane();
				
				//return if node select is just trashbin or inbox or just the two.
				int nNodeCount = pane.getNumberOfSelectedNodes();
				int nLinkCount = pane.getNumberOfSelectedLinks();
				
				boolean hasTrashbin = false;
				boolean hasInBox = false;
				String sTrashbinID = ProjectCompendium.APP.getTrashBinID();
				String sInboxID = ProjectCompendium.APP.getInBoxID();
				if (nNodeCount == 1 && nLinkCount == 0) {
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
				int fontsize = oModel.fontsize;
				int fontstyle = oModel.fontstyle;
				String fontface = oModel.fontface;
				int wrapwidth=oModel.labelWrapWidth;
				int foreground=oModel.FOREGROUND_DEFAULT.getRGB();
				int background=oModel.BACKGROUND_DEFAULT.getRGB();
								
				// WHETHER TO USE THE DEFAULT SETTING OR THE FIRST NODE'S SETTIG
				boolean bDefaultFace = false;
				boolean bDefaultSize = false;
				boolean bDefaultStyle = false;
				boolean bDefaultWrap = false;
				boolean bDefaultFore = false;
				boolean bDefaultBack = false;
				
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
						fontsize = pos.getFontSize();
						fontstyle = pos.getFontStyle();
						fontface = pos.getFontFace();
						wrapwidth=pos.getLabelWrapWidth();
						foreground=pos.getForeground();
						background=pos.getBackground();						
						i++;
					} else {
						if (fontsize != pos.getFontSize()) {
							bDefaultSize = true;
						}
						if (fontstyle != pos.getFontStyle()) {
							bDefaultStyle = true;							
						}
						if (!fontface.equals(pos.getFontFace())) {
							bDefaultFace = true;
						}
						if (wrapwidth != pos.getLabelWrapWidth()) {
							bDefaultWrap = true;
						}
						if (foreground != pos.getForeground()) {
							bDefaultFore = true;
						}
						if (background != pos.getBackground()) {
							bDefaultBack = true;
						}
					}
				}
							
				if (nNodeCount == 2 && hasTrashbin && hasInBox && nLinkCount == 0) {
					return;
				}
				
				UILink link = null;
				LinkProperties linkProps = null;
				
				i=0;
				for (Enumeration e = pane.getSelectedLinks(); e.hasMoreElements();) {
					link = (UILink) e.nextElement();
					linkProps = link.getLinkProperties();
					if (i==0 && nNodeCount == 0) {
						fontsize = linkProps.getFontSize();
						fontstyle = linkProps.getFontStyle();
						fontface = linkProps.getFontFace();
						wrapwidth=linkProps.getLabelWrapWidth();
						foreground=linkProps.getForeground();
						background=linkProps.getBackground();						
						i++;
					} else {
						if (fontsize != linkProps.getFontSize()) {
							bDefaultSize = true;
						}
						if (fontstyle != linkProps.getFontStyle()) {
							bDefaultStyle = true;							
						}
						if (!fontface.equals(linkProps.getFontFace())) {
							bDefaultFace = true;
						}
						if (wrapwidth != linkProps.getLabelWrapWidth()) {
							bDefaultWrap = true;
						}
						if (foreground != linkProps.getForeground()) {
							bDefaultFore = true;
						}
						if (background != linkProps.getBackground()) {
							bDefaultBack = true;
						}
					}
				}				
				
				if (bDefaultSize) {
					cbFontSize.setSelectedItem(new Integer(oModel.fontsize));
				} else {
					cbFontSize.setSelectedItem(new Integer(fontsize));					
				}
				if (bDefaultStyle) {
					Font oFont = new Font(fontface, oModel.fontstyle, fontsize);
			    	pbItalic.setSelected(oFont.isItalic());
			    	pbBold.setSelected(oFont.isBold());
				} else {
					Font oFont = new Font(fontface, fontstyle, fontsize);
			    	pbItalic.setSelected(oFont.isItalic());
			    	pbBold.setSelected(oFont.isBold());
				}
				if (bDefaultFace) {
					cbFontFace.setSelectedItem(oModel.fontface);
				} else {
					cbFontFace.setSelectedItem(fontface);					
				}
				if (bDefaultWrap) {
					cbWrapWidth.setSelectedItem(new Integer(oModel.labelWrapWidth));
				} else {
					cbWrapWidth.setSelectedItem(new Integer(wrapwidth));					
				}
				if (bDefaultFore) {	
					selectedForeground = Model.FOREGROUND_DEFAULT;					
					// now shows last selected colour of session instead
					//foregroundPanel.setBackground(selectedForeground);											
				} else {
					selectedForeground = new Color(foreground);
					// now shows last selected colour of session instead
					//foregroundPanel.setBackground(selectedForeground);											
				}
				if (bDefaultBack) {
					selectedBackground = Model.BACKGROUND_DEFAULT;
					// now shows last selected colour of session instead
					//backgroundPanel.setBackground(selectedBackground);					
				} else {
					selectedBackground = new Color(background);
					// now shows last selected colour of session instead
					//backgroundPanel.setBackground(new Color(background));																
				}
				
				tbrToolBar.setEnabled(true);
				cbFontSize.setEnabled(true);
				cbWrapWidth.setEnabled(true);			
				cbFontFace.setEnabled(true);
				foregroundPanel.setEnabled(true);
				backgroundPanel.setEnabled(true);				
				txtBackgroundColour.setEnabled(true);
				txtForegroundColour.setEnabled(true);	
								
				bJustSetting = false;
				
			} else if (!selected) {
				
				bJustSetting = true;				
				
				cbFontSize.setSelectedItem(new Integer(oModel.fontsize));
				Font oFont = new Font(oModel.fontface, oModel.fontstyle, oModel.fontsize);
		    	pbItalic.setSelected(oFont.isItalic());
		    	pbBold.setSelected(oFont.isItalic());
				cbFontFace.setSelectedItem(oModel.fontface);
				cbWrapWidth.setSelectedItem(new Integer(oModel.labelWrapWidth));
				foregroundPanel.setEnabled(false);
				backgroundPanel.setEnabled(false);
				txtBackgroundColour.setEnabled(false);
				txtForegroundColour.setEnabled(false);								
				tbrToolBar.setEnabled(false);
				cbFontSize.setEnabled(false);
				cbWrapWidth.setEnabled(false);			
				cbFontFace.setEnabled(false);	
				
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
