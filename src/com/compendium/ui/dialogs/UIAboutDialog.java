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
import java.io.File;
import javax.swing.*;

import com.compendium.LanguageProperties;
import com.compendium.SystemProperties;
import com.compendium.core.ICoreConstants;
import com.compendium.ui.*;

/**
 * The UIAboutDialog displays the dialog with the application
 * version number and credits, and links to relevant websites.
 *
 * @author Beatrix Zimmermann / Michelle Bachler
 */
public class UIAboutDialog extends JDialog {

	/** The button to go to the Labspace help and support forum.*/
	private JButton			pbHelp	 					= null;

	/** The button to close this dialog.*/
	private JButton			pbOK						= null;

    /** the pane for all the object to site.*/
    protected JLayeredPane layeredPane = null;
    
	/** A reference to the layer to hold background images. */
	public final static Integer BACKGROUND_LAYER 	= new Integer(200);

	/** A reference to the layer to hold grid layout stuff NOT IMPLEMENTED YET.*/
	public final static	Integer	TEXT_LAYER			= new Integer(300);
	
	/**
	 * Constructor, creates and initializes the about dialog.
	 * @param parent, the parent frame for this dialog.
	 */
	public UIAboutDialog (JFrame parent) {
		super(parent, true);
		setTitle(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIAboutDialog.title")+SystemProperties.applicationName); //$NON-NLS-1$
		init();
	}

	/**
	 * Initialize the dialog and its components.
	 */
	private void init() {

		addWindowListener(new WindowAdapter() {
	    	public void windowClosing(WindowEvent evt) {
			onCancel();
	    	}
		});
		
		this.setBackground(Color.white);

		layeredPane = new JLayeredPane();
		layeredPane.setBackground(Color.white);
		
		this.setLayeredPane(layeredPane);
		
		// Add background splash image
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
	
		// Add company button to website
		String sButPath = SystemProperties.aboutButtonImage;			
		File filebut = new File(sButPath);
		if (filebut.exists()) {		
			ImageIcon oIcon	= UIImages.createImageIcon(sButPath);			
			JButton but = new JButton(oIcon);
			but.setLocation(10,10);
			//but.setBorder(null);
			but.setSize(but.getPreferredSize());
			but.setToolTipText(SystemProperties.companyWebsiteURL);			
			layeredPane.add(but, TEXT_LAYER);
			ActionListener action = new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					ExecuteControl.launch(SystemProperties.companyWebsiteURL);
					onCancel();					
				}
			};
			but.addActionListener(action);
		} else {			
			System.out.println("button does not exist: "+filebut.getAbsolutePath()); //$NON-NLS-1$
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

	    panel0.add(label, BorderLayout.NORTH);	  
	    panel0.setSize(300, 30);
	    panel0.setLocation(0, 160);
		layeredPane.add(panel0, TEXT_LAYER);
		
		// MAIN TEXT
		GridLayout grid = new GridLayout(5, 1);
		grid.setHgap(8);
		grid.setVgap(8);
		JPanel panel = new JPanel(grid);	
		panel.setBorder(null);
		panel.setOpaque(false);		
		panel.setBackground(Color.white);

		//JLabel label1 = new JLabel("A Tool for the Compendium Methodology");
		//label1.setFont(new Font("ARIAL", Font.BOLD, 12));
		//label1.setForeground(new Color(0,0,0));
		//label1.setHorizontalAlignment(SwingUtilities.CENTER);

		JLabel label2 = new JLabel("Version: "+ICoreConstants.sAPPVERSION); //$NON-NLS-1$
		label2.setFont(new Font("ARIAL", Font.PLAIN, 12)); //$NON-NLS-1$
		label2.setHorizontalAlignment(SwingUtilities.CENTER);

		JLabel label3 = new JLabel("Developed by"); //$NON-NLS-1$
		label3.setFont(new Font("ARIAL", Font.PLAIN, 11));	        //$NON-NLS-1$
		label3.setHorizontalAlignment(SwingUtilities.CENTER);

		JLabel label4 = new JLabel(" Verizon and The Open University UK"); //$NON-NLS-1$
		label4.setFont(new Font("ARIAL", Font.PLAIN, 11)); //$NON-NLS-1$
		label4.setHorizontalAlignment(SwingUtilities.CENTER);
						
	    //panel.add(label1);
	    panel.add(label2);
	    panel.add(label3);	    
	    panel.add(label4);

	    panel.setSize(300, 110);
	    panel.setLocation(0, 200);
		layeredPane.add(panel, TEXT_LAYER);
    				        
        // OK/HELP BUTTONS AND COPYRIGHT
		BorderLayout layout3 = new BorderLayout();
		layout3.setHgap(0);
		layout3.setVgap(0);		
		JPanel panel3 = new JPanel(layout3);
		panel3.setBorder(null);		
		panel3.setOpaque(false);
		panel3.setBackground(Color.white);	
		
		JPanel okpanel = new JPanel(new BorderLayout());
		okpanel.setBackground(Color.white);		
		
		pbHelp = new UIButton(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIAboutDialog.helpAndSupportButton")); //$NON-NLS-1$
		pbHelp.setBackground(new Color(255,255,255));
		pbHelp.setMnemonic(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIAboutDialog.helpAndSupportButtonMnemonic").charAt(0));
		pbHelp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				ExecuteControl.launch(SystemProperties.helpAndSupportURL);
				onCancel();
			}
		});
		JPanel but = new JPanel();
		but.add(pbHelp);	
		but.setBackground(Color.white);	
		okpanel.add(but, BorderLayout.NORTH);		
		
		JButton butComp = new JButton("http://compendium.open.ac.uk/institute"); //$NON-NLS-1$
		butComp.setBackground(new Color(255,255,255));
		butComp.setRequestFocusEnabled(false);
		butComp.setFocusPainted(false);

		ActionListener action = new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				ExecuteControl.launch("http://compendium.open.ac.uk/institute"); //$NON-NLS-1$
				onCancel();
			}
		};
		butComp.addActionListener(action);
		butComp.setFont(new Font("ARIAL", Font.BOLD, 12)); //$NON-NLS-1$
		butComp.setForeground(new Color(0, 0, 0));
		
		JPanel butCompPanel = new JPanel();
		butCompPanel.setBackground(Color.white);
		butCompPanel.add(butComp);
		okpanel.add(butCompPanel, BorderLayout.CENTER);
		
		pbOK = new UIButton(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIAboutDialog.okButton")); //$NON-NLS-1$
		pbOK.setBackground(new Color(255,255,255));
		pbOK.setMnemonic(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "UIAboutDialog.okButtonMnemonic").charAt(0));
		pbOK.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				onCancel();
			}
		});
		JPanel but2 = new JPanel();
		but2.setBackground(Color.white);
		but2.add(pbOK);
		okpanel.add(but2, BorderLayout.SOUTH);
		
        JLabel label8 = new JLabel(""); //$NON-NLS-1$
        label8.setFont(new Font("ARIAL", Font.BOLD, 8));         //$NON-NLS-1$
 		label8.setHorizontalAlignment(SwingUtilities.CENTER);	
						
        JLabel label7 = new JLabel("Copyright(c) 1998-2010 Verizon & The Open University UK"); //$NON-NLS-1$
        label7.setFont(new Font("ARIAL", Font.BOLD, 10)); //$NON-NLS-1$
		label7.setHorizontalAlignment(SwingUtilities.CENTER);		
		label7.setBackground(Color.white);
		
        panel3.add(okpanel, BorderLayout.NORTH);     
		panel3.add(label8, BorderLayout.CENTER);
        panel3.add(label7, BorderLayout.SOUTH);

        panel3.setSize(310, 135);
        panel3.setLocation(0,280);
        panel3.setBackground(Color.white); 
        panel3.setOpaque(true);
 		layeredPane.add(panel3, TEXT_LAYER);
 
        // ACKNOWLEDGEMENT SECTION
		BorderLayout layout4 = new BorderLayout();
		layout4.setHgap(0);
		layout4.setVgap(0);		
		JPanel panel4 = new JPanel(layout4);
		panel4.setBorder(null);		
		panel4.setOpaque(false);
		panel4.setBackground(Color.white);	
       		
        JLabel label8b = new JLabel("Support for Compendium gratefully acknowledged:"); //$NON-NLS-1$
        JLabel label9b = new JLabel("USA: NASA, Hewlett Foundation");        //$NON-NLS-1$
        JLabel label10 = new JLabel("UK: AHRC, EPSRC, ESRC, JISC, e-Science Programme"); //$NON-NLS-1$
        
 		label8b.setHorizontalAlignment(SwingUtilities.CENTER);	
		label9b.setHorizontalAlignment(SwingUtilities.CENTER);		 		
		label10.setHorizontalAlignment(SwingUtilities.CENTER);		
        
        label8b.setFont(new Font("ARIAL", Font.BOLD, 10)); //$NON-NLS-1$
        label9b.setFont(new Font("ARIAL", Font.BOLD, 10)); //$NON-NLS-1$
        label10.setFont(new Font("ARIAL", Font.BOLD, 10)); //$NON-NLS-1$
 		    	
		label8b.setBackground(Color.white);
		label9b.setBackground(Color.white);
		label10.setBackground(Color.white);
        
        panel4.add(label8b, BorderLayout.NORTH);
        panel4.add(label9b, BorderLayout.CENTER);
        panel4.add(label10, BorderLayout.SOUTH);

        panel4.setSize(300, 45);
        panel4.setLocation(0,425);
          
		layeredPane.add(panel4, TEXT_LAYER); 		
 		layeredPane.setOpaque(true);
 		
        pack();
       
        setSize(308, 510);				
                
        this.getRootPane().setDefaultButton(pbOK); 
        pbOK.requestFocus();
	}

	
	/**
	 * Close the dialog and exit the application.
	 */
    public void onCancel() {
    	setVisible(false);
		dispose();
    }	
}
