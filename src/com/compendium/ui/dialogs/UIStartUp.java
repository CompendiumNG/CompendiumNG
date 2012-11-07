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

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import com.compendium.*;
import com.compendium.core.ICoreConstants;
import com.compendium.ui.UIImages;
import com.compendium.core.db.management.*;

/**
 * This is the small dialog shown while Compendium is starting up and preparing to open.
 *
 * @author Michelle Bachler.
 */
public class UIStartUp extends JDialog {

	/** The current message being displayed.*/
    protected JLabel messageLabel;

	/** The parent frame for this dialog.*/
    protected Frame parent;
    
    /** the pane for all the object to site.*/
    protected JLayeredPane layeredPane = null;
    
	/** A reference to the layer to hold background images. */
	public final static Integer BACKGROUND_LAYER 	= new Integer(200);

	/** A reference to the layer to hold grid layout stuff NOT IMPLEMENTED YET.*/
	public final static	Integer	TEXT_LAYER			= new Integer(300);
    

	/**
	 * Constructor. Draw the dialog.
	 * @param parent, the parent frame for this dialog.
	 */
    public UIStartUp(Frame parent, String sTitle) {
        super(parent, sTitle, false);
        this.parent = parent;
       	
		addWindowListener(new WindowAdapter() {
	    	public void windowClosing(WindowEvent evt) {
			onCancel();
	    	}
		});

		this.setBackground(Color.white);
		
		layeredPane = new JLayeredPane();
		layeredPane.setBackground(Color.white);
		
		getContentPane().add(layeredPane);
		getContentPane().setBackground(Color.white);
		
		String sImagePath = SystemProperties.splashImage;
		File fileicon = new File(sImagePath);
		if (fileicon.exists()) {		
			JLabel lblBackgroundLabel = new JLabel();
			ImageIcon oIcon	= UIImages.createImageIcon(sImagePath);
			lblBackgroundLabel.setIcon(oIcon);
			lblBackgroundLabel.setLocation(0,0);
			lblBackgroundLabel.setSize(lblBackgroundLabel.getPreferredSize());
			layeredPane.add(lblBackgroundLabel, BACKGROUND_LAYER);
		}
	
		// TITLE
		BorderLayout layout = new BorderLayout();
		layout.setHgap(0);
		layout.setVgap(0);
		JPanel panel0 = new JPanel(layout);	
		panel0.setBorder(null);
		panel0.setOpaque(false);		
		panel0.setBackground(Color.white);
		
		JLabel label = new JLabel(SystemProperties.applicationName);	
		label.setOpaque(false);
		label.setFont(new Font("ARIAL", Font.BOLD, 20)); //$NON-NLS-1$
		label.setHorizontalAlignment(SwingUtilities.CENTER);		

		JLabel label1 = new JLabel(SystemProperties.startUpQualifyingText); //$NON-NLS-1$
		label1.setFont(new Font("ARIAL", Font.BOLD, 12)); //$NON-NLS-1$
		label1.setHorizontalAlignment(SwingUtilities.CENTER);

		JPanel labelPanel = new JPanel(new BorderLayout());
		labelPanel.setBorder(new EmptyBorder(5,0,0,0));
		labelPanel.add(new JLabel(" "), BorderLayout.NORTH);		
		labelPanel.add(label1, BorderLayout.SOUTH);	  
		labelPanel.setBorder(null);
		labelPanel.setOpaque(false);		
		labelPanel.setBackground(Color.white);
		
	    panel0.add(label, BorderLayout.NORTH);	  
	    panel0.add(new JLabel(" "), BorderLayout.CENTER);			    //$NON-NLS-1$
	    panel0.add(labelPanel, BorderLayout.SOUTH);			    
	    panel0.setSize(300, 60);
	    panel0.setLocation(0, 160);
		layeredPane.add(panel0, TEXT_LAYER);
		
		// MAIN TEXT
		BorderLayout layout1 = new BorderLayout();
		layout1.setHgap(0);
		layout1.setVgap(0);
		JPanel panel = new JPanel(layout1);	
		panel.setBorder(null);
		panel.setOpaque(false);		
		panel.setBackground(Color.white);

		String sMessage = "v" + ICoreConstants.sAPPVERSION; //$NON-NLS-1$
		if (SystemProperties.showAdminDatabase) {
			sMessage += " [" + DBAdminDatabase.DATABASE_NAME + "]"; //$NON-NLS-1$ //$NON-NLS-2$
		}
		JLabel label2 = new JLabel(sMessage);
		JLabel label3 = new JLabel("Developed by"); //$NON-NLS-1$
		JLabel label4 = new JLabel(" Verizon and The Open University UK"); //$NON-NLS-1$
		
		label2.setFont(new Font("ARIAL", Font.PLAIN, 12)); //$NON-NLS-1$
		label3.setFont(new Font("ARIAL", Font.PLAIN, 12)); //$NON-NLS-1$
		label4.setFont(new Font("ARIAL", Font.PLAIN, 12)); //$NON-NLS-1$
       
		label2.setHorizontalAlignment(SwingUtilities.CENTER);
		label3.setHorizontalAlignment(SwingUtilities.CENTER);
		label4.setHorizontalAlignment(SwingUtilities.CENTER);
		
	    panel.add(label2, BorderLayout.NORTH);
	    panel.add(label3, BorderLayout.CENTER);	    
	    panel.add(label4, BorderLayout.SOUTH);
	    
	    panel.setSize(300, 60);
	    panel.setLocation(0, 235);
		layeredPane.add(panel, TEXT_LAYER);
   
 				
		// MESSAGE AND COPYRIGHT
		BorderLayout layout3 = new BorderLayout();
		layout3.setHgap(0);
		layout3.setVgap(0);		
		JPanel panel3 = new JPanel(layout3);
		panel3.setBorder(null);		
		panel3.setOpaque(false);
		panel3.setBackground(Color.white);	
		
		messageLabel = new JLabel();
        messageLabel.setHorizontalAlignment(SwingUtilities.CENTER);
        
        JLabel label6 = new JLabel(""); //$NON-NLS-1$
        label6.setFont(new Font("ARIAL", Font.BOLD, 9)); //$NON-NLS-1$
		label6.setHorizontalAlignment(SwingUtilities.CENTER);	
		
        JLabel label7 = new JLabel("Copyright(c) 1998-2010 Verizon & The Open University UK"); //$NON-NLS-1$
        label7.setFont(new Font("ARIAL", Font.BOLD, 10)); //$NON-NLS-1$
		label7.setHorizontalAlignment(SwingUtilities.CENTER);		
		
        panel3.add(messageLabel, BorderLayout.NORTH);     
		panel3.add(label6, BorderLayout.CENTER);
        panel3.add(label7, BorderLayout.SOUTH);

        panel3.setSize(300, 50);
        panel3.setLocation(0,310);
		layeredPane.add(panel3, TEXT_LAYER);
        
        // ACKNOWLEDGEMENT SECTION
		BorderLayout layout4 = new BorderLayout();
		layout4.setHgap(0);
		layout4.setVgap(0);		
		JPanel panel4 = new JPanel(layout4);
		panel4.setBorder(null);		
		panel4.setOpaque(false);
		panel4.setBackground(Color.white);	
       		
        JLabel label8 = new JLabel("Support for Compendium gratefully acknowledged:"); //$NON-NLS-1$
        JLabel label9 = new JLabel("USA: NASA, Hewlett Foundation");        //$NON-NLS-1$
        JLabel label10 = new JLabel("UK: AHRC, EPSRC, ESRC, JISC, e-Science Programme"); //$NON-NLS-1$
        
 		label8.setHorizontalAlignment(SwingUtilities.CENTER);	
		label9.setHorizontalAlignment(SwingUtilities.CENTER);		 		
		label10.setHorizontalAlignment(SwingUtilities.CENTER);		
        
        label8.setFont(new Font("ARIAL", Font.BOLD, 10)); //$NON-NLS-1$
        label9.setFont(new Font("ARIAL", Font.BOLD, 10)); //$NON-NLS-1$
        label10.setFont(new Font("ARIAL", Font.BOLD, 10)); //$NON-NLS-1$
 		    	
		label8.setBackground(Color.white);
		label9.setBackground(Color.white);
		label10.setBackground(Color.white);
        
        panel4.add(label8, BorderLayout.NORTH);
        panel4.add(label9, BorderLayout.CENTER);
        panel4.add(label10, BorderLayout.SOUTH);

        panel4.setSize(300, 45);
        panel4.setLocation(0,366);
          
		layeredPane.add(panel4, TEXT_LAYER);
        		
        setMessage("Initializing Compendium: Knowledge Mapping Software"); //$NON-NLS-1$
        pack();

        setSize(308, 450);
    }

	/**
	 * Set the currently displayed message to the given text.
	 * @param msg, the message to display.
	 */
    public void setMessage(String msg) {
        messageLabel.setText(msg);
        validate();
    }

   /**
	* Close the dialog and exit the application.
	*/
    public void onCancel() {
    	setVisible(false);
		dispose();
		System.exit(0);
    }
}
