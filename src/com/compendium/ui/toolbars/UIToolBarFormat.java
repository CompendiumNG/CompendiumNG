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

	/** The label holding the icon to indicate background colour.*/
	private JLabel			txtBackgroundColour	= null;

	/** The label holding the icon to indicate foreground colour.*/
	private JLabel			txtForegroundColour	= null;

	/** The JSeparator to hold the text background colour.*/
	private JPanel			foregroundPanel	= null;

	/** The JSeparator to hold the text foreground colour.*/
	private JPanel			backgroundPanel	= null;

	/** The JSeparator to hold the text background colour.*/
	private JPanel			forePanel	= null;

	/** The JSeparator to hold the text foreground colour.*/
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
		
		tbrToolBar = new UIToolBar("Node Format Toolbar", UIToolBar.NORTHSOUTH);
		tbrToolBar.setOrientation(orientation);
		tbrToolBar.setEnabled(false);
		CSH.setHelpIDString(tbrToolBar,"toolbars.format");
				
		tbrToolBar.add( createFontFaceChoiceBox() );
		tbrToolBar.add( createFontSizeChoiceBox() );

		pbBold = tbrToolBar.createToolBarRadioButton("Bold/Unbold selected node labels", UIImages.get(FORMAT_BOLD));
		pbBold.setSelectedIcon(UIImages.get(FORMAT_BOLD_SELECTED));				
		pbBold.addActionListener(this);
		pbBold.setEnabled(true);
		tbrToolBar.add(pbBold);
		CSH.setHelpIDString(pbBold,"toolbars.format");
		
		pbItalic = tbrToolBar.createToolBarRadioButton("Italic/Unitalic selected node labels", UIImages.get(FORMAT_ITALIC));
		pbItalic.setSelectedIcon(UIImages.get(FORMAT_ITALIC_SELECTED));				
		pbItalic.addActionListener(this);
		pbItalic.setEnabled(true);
		tbrToolBar.add(pbItalic);
		CSH.setHelpIDString(pbItalic,"toolbars.format");
		
		GridBagLayout grid = new GridBagLayout();		
		forePanel = new JPanel(grid);	
		foregroundPanel = new JPanel(new BorderLayout());
		foregroundPanel.setBackground(Color.black);

		JLabel label = new JLabel(" ");
		GridBagConstraints con5 = new GridBagConstraints();
		con5.fill = GridBagConstraints.NONE;
		con5.anchor = GridBagConstraints.CENTER;
		grid.addLayoutComponent(label, con5);		
		forePanel.add(label);
		
		txtForegroundColour = new JLabel(UIImages.get(FOREGROUND_COLOUR));
		txtForegroundColour.setBorder(null);
		txtForegroundColour.setToolTipText("Select text foreground colour");
		txtForegroundColour.setEnabled(false);
		txtForegroundColour.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				int clickCount = e.getClickCount();
				if (clickCount == 1 && txtForegroundColour.isEnabled()) {
					if (oColorChooserDialog != null) {
						oColorChooserDialog.setColour(foregroundPanel.getBackground());
					} else {
						oColorChooserDialog = new UIColorChooserDialog(ProjectCompendium.APP, foregroundPanel.getBackground());
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
		
		foregroundPanel.add(txtForegroundColour, BorderLayout.CENTER);
		
		GridBagConstraints con = new GridBagConstraints();
		con.fill = GridBagConstraints.NONE;
		con.anchor = GridBagConstraints.CENTER;
		grid.addLayoutComponent(foregroundPanel, con);		
		forePanel.add(foregroundPanel);

		label = new JLabel(" ");
		GridBagConstraints con3 = new GridBagConstraints();
		con3.fill = GridBagConstraints.NONE;
		con3.anchor = GridBagConstraints.CENTER;
		grid.addLayoutComponent(label, con3);		
		forePanel.add(label);

		label = new JLabel(" ");
		GridBagConstraints con4 = new GridBagConstraints();
		con4.fill = GridBagConstraints.NONE;
		con4.anchor = GridBagConstraints.CENTER;
		grid.addLayoutComponent(label, con4);		
		forePanel.add(label);

		tbrToolBar.add(forePanel);
		CSH.setHelpIDString(txtForegroundColour,"toolbars.format");		
		
		GridBagLayout grid2 = new GridBagLayout();	
		backPanel = new JPanel(grid2);		
		backgroundPanel = new JPanel(new BorderLayout());
		backgroundPanel.setBackground(Color.white);
		
		txtBackgroundColour = new JLabel(UIImages.get(BACKGROUND_COLOUR));
		txtBackgroundColour.setToolTipText("Select text background colour");
		txtBackgroundColour.setEnabled(false);
		txtBackgroundColour.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				int clickCount = e.getClickCount();
				if (clickCount == 1 && txtBackgroundColour.isEnabled()) {
					if (oColorChooserDialog != null) {
						oColorChooserDialog.setColour(backgroundPanel.getBackground());
					} else {
						oColorChooserDialog = new UIColorChooserDialog(ProjectCompendium.APP, backgroundPanel.getBackground());
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
		
		backgroundPanel.add(txtBackgroundColour, BorderLayout.CENTER);
		
		GridBagConstraints con2 = new GridBagConstraints();
		con2.fill = GridBagConstraints.NONE;
		con2.anchor = GridBagConstraints.CENTER;
		grid2.addLayoutComponent(backgroundPanel, con2);		
		
		backPanel.add(backgroundPanel);
		
		label = new JLabel(" ");
		GridBagConstraints con6 = new GridBagConstraints();
		con6.fill = GridBagConstraints.NONE;
		con6.anchor = GridBagConstraints.CENTER;
		grid.addLayoutComponent(label, con6);		
		backPanel.add(label);		
		
		tbrToolBar.add(backPanel);
		CSH.setHelpIDString(txtBackgroundColour,"toolbars.format");
		
		tbrToolBar.addSeparator();
		
		pbTextIndicator = tbrToolBar.createToolBarRadioButton("Show/Hide Node Text Indicator", UIImages.get(SHOW_TEXT));
		pbTextIndicator.setSelectedIcon(UIImages.get(SHOW_TEXT_SELECTED));
		pbTextIndicator.addActionListener(this);
		pbTextIndicator.setEnabled(true);
		tbrToolBar.add(pbTextIndicator);
		CSH.setHelpIDString(pbTextIndicator,"toolbars.format");

		pbWeightIndicator = tbrToolBar.createToolBarRadioButton("Show/Hide Node Weight Indicator", UIImages.get(SHOW_WEIGHT));
		pbWeightIndicator.setSelectedIcon(UIImages.get(SHOW_WEIGHT_SELECTED));
		pbWeightIndicator.addActionListener(this);
		pbWeightIndicator.setEnabled(true);
		tbrToolBar.add(pbWeightIndicator);
		CSH.setHelpIDString(pbWeightIndicator,"toolbars.format");

		pbTagIndicator = tbrToolBar.createToolBarRadioButton("Show/Hide Node Tag Indicator", UIImages.get(SHOW_TAGS));
		pbTagIndicator.setSelectedIcon(UIImages.get(SHOW_TAGS_SELECTED));
		pbTagIndicator.addActionListener(this);
		pbTagIndicator.setEnabled(true);
		tbrToolBar.add(pbTagIndicator);
		CSH.setHelpIDString(pbTagIndicator,"toolbars.format");

		pbTransIndicator = tbrToolBar.createToolBarRadioButton("Show/Hide Node Transclusion Indicator", UIImages.get(SHOW_TRANS));
		pbTransIndicator.setSelectedIcon(UIImages.get(SHOW_TRANS_SELECTED));
		pbTransIndicator.addActionListener(this);
		pbTransIndicator.setEnabled(true);
		tbrToolBar.add(pbTransIndicator);
		CSH.setHelpIDString(pbTransIndicator,"toolbars.format");

		pbSmallIcons = tbrToolBar.createToolBarRadioButton("Set/Unset using Small Icons", UIImages.get(SMALL_ICONS_SELECTED));
		pbSmallIcons.setSelectedIcon(UIImages.get(SMALL_ICONS));		
		pbSmallIcons.addActionListener(this);
		pbSmallIcons.setEnabled(true);
		tbrToolBar.add(pbSmallIcons);
		CSH.setHelpIDString(pbSmallIcons,"toolbars.format");

		pbHideIcons = tbrToolBar.createToolBarRadioButton("Hide/Show Node Icons", UIImages.get(HIDE_ICONS_SELECTED));
		pbHideIcons.setSelectedIcon(UIImages.get(HIDE_ICONS));				
		pbHideIcons.addActionListener(this);
		pbHideIcons.setEnabled(true);
		tbrToolBar.add(pbHideIcons);
		CSH.setHelpIDString(pbHideIcons,"toolbars.format");

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
		CSH.setHelpIDString(fontFacePanel,"toolbars.format");

		cbFontFace = new JComboBox();
		cbFontFace.setOpaque(true);
		cbFontFace.setEditable(false);
		cbFontFace.setEnabled(false);
		cbFontFace.setMaximumRowCount(10);
		cbFontFace.setFont( new Font("Dialog", Font.PLAIN, 10 ));

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
	            	text += " ";
	      		}

				setBorder((cellHasFocus) ? UIManager.getBorder("List.focusCellHighlightBorder") : noFocusBorder);
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

		fontFacePanel.add(new JLabel(" "), BorderLayout.WEST);
		fontFacePanel.add(cbFontFace, BorderLayout.CENTER);
		fontFacePanel.add(new JLabel(" "), BorderLayout.EAST);		
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
		CSH.setHelpIDString(fontSizePanel,"toolbars.format");

 	 	String[] sizes = {"8","10","12","14","16","18","20","22","24","26","28","30"};

		cbFontSize = new JComboBox(sizes);
		cbFontSize.setOpaque(true);
		cbFontSize.setEditable(true);
		cbFontSize.setEnabled(false);
		cbFontSize.setMaximumRowCount(10);
		cbFontSize.setFont( new Font("Dialog", Font.PLAIN, 10 ));		
		
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
						if (!item.equals("")) {
							try {
								nSize = new Integer(item);
							} catch(Exception ex) {
								ProjectCompendium.APP.displayError("Please enter only valid numbers");
								return;
							}
						}
           			} else if (obj instanceof Integer) {
           				nSize = (Integer)obj;
						
           			}
					if (nSize > -1) {	
						int size = nSize.intValue();
						if (size <= 0) {
							ProjectCompendium.APP.displayError("Please enter positive numbers only");
						} else if (size > 500) {
							ProjectCompendium.APP.displayError("Now you are just being silly!!");							
						} else {
							onUpdateFontSize(size);
						}
					}
	         	}
         	}
		};
		cbFontSize.addActionListener(fontSizeActionListener);
		
		fontSizePanel.add(new JLabel(" "), BorderLayout.WEST);		
        fontSizePanel.add(cbFontSize, BorderLayout.CENTER);
        fontSizePanel.add(new JLabel(" "), BorderLayout.EAST);     
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
		
		CSH.setHelpIDString(wrapWidthPanel,"toolbars.format");

	 	String[] widths = {"5","10","15","20","25","30","35","40","45","50"};
		
		cbWrapWidth = new JComboBox(widths);
		cbWrapWidth.setOpaque(true);
		cbWrapWidth.setEditable(true);
		cbWrapWidth.setEnabled(false);
		cbWrapWidth.setMaximumRowCount(10);
		cbWrapWidth.setFont( new Font("Dialog", Font.PLAIN, 10 ));

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
						if (!item.equals("")) {
							wrap = new Integer(0);
							try {
								wrap = new Integer(item);
							} catch(Exception ex) {
								ProjectCompendium.APP.displayError("Please enter only valid numbers");
								return;
							}
						}
           			} else if (obj instanceof Integer) {
           				wrap = (Integer)obj;
						
           			}
					if (wrap.intValue() > -1) {	
						int wrapValue = wrap.intValue();
						if (wrapValue <= 0) {
							ProjectCompendium.APP.displayError("Please enter positive numbers only");
						} else if (wrapValue > 500) {
							ProjectCompendium.APP.displayError("Now you are just being silly!!");							
						} else {
							onUpdateWrapWidth(wrapValue);
						}
					}
	         	}
        	}
		};
		cbWrapWidth.addActionListener(wrapWidthActionListener);
		
		cbWrapWidth.setToolTipText("Set the label wrap width (no. of letters - to closest whole word)");		

		JLabel label  = new JLabel(UIImages.get(WRAP_WIDTH));
		label.setToolTipText("Set the label wrap width (no. of letters - to closest whole word)");
		
		JPanel panel = new JPanel();
		panel.add(new JLabel(" "));
		panel.add(label);
		panel.add(new JLabel(" "));
		wrapWidthPanel.add(panel, BorderLayout.WEST);
		wrapWidthPanel.add(cbWrapWidth, BorderLayout.CENTER);		
		wrapWidthPanel.add(new JLabel(" "), BorderLayout.EAST);						
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
			editor.setBorder(UIManager.getBorder("List.focusCellHighlightBorder"));
		}

		public MyComboBoxEditor(int columns) {
			editor = new JTextField();			
			editor.setColumns(columns);			
			editor.setBorder(UIManager.getBorder("List.focusCellHighlightBorder"));
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
	            editor.setText("");
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
	                    Method method = cls.getMethod("valueOf", new Class[]{String.class});
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
		} else if (source.equals(pbBold) || source.equals(pbItalic)) {
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
				String sNodeID = "";
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
						ProjectCompendium.APP.displayError("Unable to update text indicators due to:\n\n"+ex.getMessage());
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
				String sNodeID = "";
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
						ProjectCompendium.APP.displayError("Unable to update tags indicators due to:\n\n"+ex.getMessage());
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
				String sNodeID = "";
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
						ProjectCompendium.APP.displayError("Unable to update text transclusion due to:\n\n"+ex.getMessage());
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
				String sNodeID = "";
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
						ProjectCompendium.APP.displayError("Unable to update showing node weight indicators due to:\n\n"+ex.getMessage());
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
				String sNodeID = "";
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
						ProjectCompendium.APP.displayError("Unable to update showing small icons due to:\n\n"+ex.getMessage());
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
				String sNodeID = "";
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
						ProjectCompendium.APP.displayError("Unable to update hiding icons due to:\n\n"+ex.getMessage());
					}
				}
			}
		}					
	}
	
	/**
	 * Update the node label wrap width on the currently selected nodes.
	 */
	private void onUpdateWrapWidth(int width) {
		if (!bJustSetting) {
			
			String sInBoxID = oParent.getInBoxID();
			String sTrashBinID = oParent.getTrashBinID();
			
			UIViewFrame frame = ProjectCompendium.APP.getCurrentFrame();
			Model oModel = (Model)ProjectCompendium.APP.getModel();
			if (frame instanceof UIMapViewFrame) {				
				Vector vtUpdateNodes = new Vector();												
				UIMapViewFrame oMapFrame = (UIMapViewFrame)frame;
				UIViewPane pane = oMapFrame.getViewPane();
				UINode node = null;
				NodePosition pos = null;
				String sNodeID  ="";
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
				
				if (vtUpdateNodes.size() > 0) {				
					try {
						((ViewService)oModel.getViewService()).setWrapWidth(oModel.getSession(), pane.getView().getId(), vtUpdateNodes, width);
						int count = vtUpdateNodes.size();
						for (int i=0; i<count;i++) {
							pos = (NodePosition)vtUpdateNodes.elementAt(i);
							pos.setLabelWrapWidth(width);
						}
					} catch (SQLException ex) {
						ProjectCompendium.APP.displayError("Unable to update label wrap width due to:\n\n"+ex.getMessage());
					}
				}
			}
		}					
	}
	
	/**
	 * Update the node label font face on the currently selected nodes.
	 */
	private void onUpdateFontFace(String sFontFace) {
		if (!bJustSetting) {
			
			String sInBoxID = oParent.getInBoxID();
			String sTrashBinID = oParent.getTrashBinID();
			
			UIViewFrame frame = ProjectCompendium.APP.getCurrentFrame();
			Model oModel = (Model)ProjectCompendium.APP.getModel();
			if (frame instanceof UIMapViewFrame) {
				Vector vtUpdateNodes = new Vector();												
				UIMapViewFrame oMapFrame = (UIMapViewFrame)frame;
				UIViewPane pane = oMapFrame.getViewPane();
				UINode node = null;
				NodePosition pos = null;
				String sNodeID = "";
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
				
				if (vtUpdateNodes.size() > 0) {				
					try {
						((ViewService)oModel.getViewService()).setFontFace(oModel.getSession(), pane.getView().getId(), vtUpdateNodes, sFontFace);
						int count = vtUpdateNodes.size();
						for (int i=0; i<count;i++) {
							pos = (NodePosition)vtUpdateNodes.elementAt(i);
							pos.setFontFace(sFontFace);
						}
					} catch (SQLException ex) {
						ProjectCompendium.APP.displayError("Unable to update label font face due to:\n\n"+ex.getMessage());
					}
				}
			}
		}					
	}
	
	/**
	 * Update the node label font size on the currently selected nodes.
	 */
	private void onUpdateFontSize(int nFontSize) {
		if (!bJustSetting) {
			
			String sInBoxID = oParent.getInBoxID();
			String sTrashBinID = oParent.getTrashBinID();

			UIViewFrame frame = ProjectCompendium.APP.getCurrentFrame();
			Model oModel = (Model)ProjectCompendium.APP.getModel();
			if (frame instanceof UIMapViewFrame) {
				Vector vtUpdateNodes = new Vector();																
				UIMapViewFrame oMapFrame = (UIMapViewFrame)frame;
				UIViewPane pane = oMapFrame.getViewPane();
				UINode node = null;
				NodePosition pos = null;
				String sNodeID = "";
				for (Enumeration e = pane.getSelectedNodes(); e.hasMoreElements();) {
					node = (UINode)e.nextElement();
					
					sNodeID = node.getNode().getId();
					if (!sNodeID.equals(sInBoxID) && !sNodeID.equals(sTrashBinID)) {					
						pos = node.getNodePosition();		
						if (nFontSize != pos.getFontSize()) {
							vtUpdateNodes.addElement(pos);
						}
					}
				}
				
				if (vtUpdateNodes.size() > 0) {				
					try {
						((ViewService)oModel.getViewService()).setFontSize(oModel.getSession(), pane.getView().getId(), vtUpdateNodes, nFontSize);
						int count = vtUpdateNodes.size();
						for (int i=0; i<count;i++) {
							pos = (NodePosition)vtUpdateNodes.elementAt(i);
							pos.setFontSize(nFontSize);
						}
					} catch (SQLException ex) {
						ProjectCompendium.APP.displayError("Unable to update label font size due to:\n\n"+ex.getMessage());
					}
				}
			}
		}					
	}
	
	/**
	 * Update the node label font style on the currently selected nodes.
	 */
	private void onUpdateFontStyle(int nFontStyle) {
		if (!bJustSetting) {
			
			String sInBoxID = oParent.getInBoxID();
			String sTrashBinID = oParent.getTrashBinID();
			
			UIViewFrame frame = ProjectCompendium.APP.getCurrentFrame();
			Model oModel = (Model)ProjectCompendium.APP.getModel();
			if (frame instanceof UIMapViewFrame) {
				Vector vtUpdateNodes = new Vector();				
				UIMapViewFrame oMapFrame = (UIMapViewFrame)frame;
				UIViewPane pane = oMapFrame.getViewPane();
				UINode node = null;
				NodePosition pos = null;
				String sNodeID = "";
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
				
				if (vtUpdateNodes.size() > 0) {
					try {
						((ViewService)oModel.getViewService()).setFontStyle(oModel.getSession(), pane.getView().getId(), vtUpdateNodes, nFontStyle);
						int count = vtUpdateNodes.size();
						for (int i=0; i<count;i++) {
							pos = (NodePosition)vtUpdateNodes.elementAt(i);
							pos.setFontStyle(nFontStyle);
						}
					} catch (SQLException ex) {
						ProjectCompendium.APP.displayError("Unable to update label font style due to:\n\n"+ex.getMessage());
					}
				}
			}
		}					
	}	
	
	/**
	 * Update the node label font style on the currently selected nodes by 
	 * adding / removing the given style depending on existing style.
	 */	
	public void addFontStyle(int nStyle) {
		
		String sInBoxID = ProjectCompendium.APP.getInBoxID();
		String sTrashBinID = ProjectCompendium.APP.getTrashBinID();
				
		UIViewFrame frame = ProjectCompendium.APP.getCurrentFrame();
		Model oModel = (Model)ProjectCompendium.APP.getModel();
		if (frame instanceof UIMapViewFrame) {
			Vector vtUpdateNodes = new Vector();				
			UIMapViewFrame oMapFrame = (UIMapViewFrame)frame;
			UIViewPane pane = oMapFrame.getViewPane();
			UINode node = null;
			NodePosition pos = null;
			String sNodeID = "";
			for (Enumeration e = pane.getSelectedNodes(); e.hasMoreElements();) {
				node = (UINode)e.nextElement();							
				sNodeID = node.getNode().getId();
				if (!sNodeID.equals(sInBoxID) && !sNodeID.equals(sTrashBinID)) {									
					pos = node.getNodePosition();
					vtUpdateNodes.addElement(pos);
				}
			}
			
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
					ProjectCompendium.APP.displayError("Unable to update label font style due to:\n\n"+ex.getMessage());
				}
			}
		}		
	}
	
	/**
	 * Update the node label foreground on the currently selected nodes.
	 */
	private void onUpdateForeground(int nForeground) {
		if (!bJustSetting) {
			
			String sInBoxID = oParent.getInBoxID();
			String sTrashBinID = oParent.getTrashBinID();
			
			UIViewFrame frame = ProjectCompendium.APP.getCurrentFrame();
			Model oModel = (Model)ProjectCompendium.APP.getModel();
			if (frame instanceof UIMapViewFrame) {
				Vector vtUpdateNodes = new Vector();								
				UIMapViewFrame oMapFrame = (UIMapViewFrame)frame;
				UIViewPane pane = oMapFrame.getViewPane();
				UINode node = null;
				NodePosition pos = null;
				String sNodeID = "";
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
				
				if (vtUpdateNodes.size() > 0) {				
					try {
						((ViewService)oModel.getViewService()).setTextForeground(oModel.getSession(), pane.getView().getId(), vtUpdateNodes, nForeground);
						int count = vtUpdateNodes.size();
						for (int i=0; i<count;i++) {
							pos = (NodePosition)vtUpdateNodes.elementAt(i);
							pos.setForeground(nForeground);
						}
					} catch (SQLException ex) {
						ProjectCompendium.APP.displayError("Unable to update label text foreground due to:\n\n"+ex.getMessage());
					}
				}
			}
		}					
	}	
	
	/**
	 * Update the node label background on the currently selected nodes.
	 */
	private void onUpdateBackground(int nBackground) {
		if (!bJustSetting) {
			
			String sInBoxID = oParent.getInBoxID();
			String sTrashBinID = oParent.getTrashBinID();
			
			UIViewFrame frame = ProjectCompendium.APP.getCurrentFrame();
			Model oModel = (Model)ProjectCompendium.APP.getModel();
			if (frame instanceof UIMapViewFrame) {
				Vector vtUpdateNodes = new Vector();												
				UIMapViewFrame oMapFrame = (UIMapViewFrame)frame;
				UIViewPane pane = oMapFrame.getViewPane();
				UINode node = null;
				NodePosition pos = null;
				String sNodeID = "";
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
				
				if (vtUpdateNodes.size() > 0) {				
					try {
						((ViewService)oModel.getViewService()).setTextBackground(oModel.getSession(), pane.getView().getId(), vtUpdateNodes, nBackground);
						int count = vtUpdateNodes.size();
						for (int i=0; i<count;i++) {
							pos = (NodePosition)vtUpdateNodes.elementAt(i);
							pos.setBackground(nBackground);
						}
					} catch (SQLException ex) {
						ProjectCompendium.APP.displayError("Unable to update label text Background due to:\n\n"+ex.getMessage());
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
			
			if (selected &&  frame instanceof UIMapViewFrame) {												
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
				int fontsize = oModel.fontsize;
				int fontstyle = oModel.fontstyle;
				String fontface = oModel.fontface;
				int wrapwidth=oModel.labelWrapWidth;
				int foreground=oModel.FOREGROUND_DEFAULT.getRGB();
				int background=oModel.BACKGROUND_DEFAULT.getRGB();
								
				// WHETHER TO USE THE DEFAULT SETTING OR THE FIRST NODE'S SETTIG
				boolean bDefaultTags = false;
				boolean bDefaultText = false;
				boolean bDefaultTrans = false;
				boolean bDefaultWeight= false;
				boolean bDefaultSmall = false;
				boolean bDefaultHide = false;
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
						bShowTags = pos.getShowTags();
						bShowText = pos.getShowText();
						bShowTrans = pos.getShowTrans();
						bShowWeight = pos.getShowWeight();
						bSmallIcon = pos.getShowSmallIcon();
						bHideIcon = pos.getHideIcon();
						fontsize = pos.getFontSize();
						fontstyle = pos.getFontStyle();
						fontface = pos.getFontFace();
						wrapwidth=pos.getLabelWrapWidth();
						foreground=pos.getForeground();
						background=pos.getBackground();						
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
					foregroundPanel.setBackground(Model.FOREGROUND_DEFAULT);											
				} else {
					foregroundPanel.setBackground(new Color(foreground));											
				}
				if (bDefaultBack) {
					backgroundPanel.setBackground(Model.BACKGROUND_DEFAULT);					
				} else {
					backgroundPanel.setBackground(new Color(background));																
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
				
				pbTagIndicator.setSelected(oModel.showTagsNodeIndicator);
				pbTextIndicator.setSelected(oModel.showTextNodeIndicator);
				pbTransIndicator.setSelected(oModel.showTransNodeIndicator);					
				pbWeightIndicator.setSelected(oModel.showWeightNodeIndicator);					
				pbSmallIcons.setSelected(oModel.smallIcons);	
				pbHideIcons.setSelected(oModel.hideIcons);	
				cbFontSize.setSelectedItem(new Integer(oModel.fontsize));
				Font oFont = new Font(oModel.fontface, oModel.fontstyle, oModel.fontsize);
		    	pbItalic.setSelected(oFont.isItalic());
		    	pbBold.setSelected(oFont.isItalic());
				cbFontFace.setSelectedItem(oModel.fontface);
				cbWrapWidth.setSelectedItem(new Integer(oModel.labelWrapWidth));
				
				foregroundPanel.setBackground(Model.FOREGROUND_DEFAULT);
				backgroundPanel.setBackground(Model.BACKGROUND_DEFAULT);
				
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
