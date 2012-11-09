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

import javax.swing.*;
import javax.swing.border.*;

import com.compendium.ProjectCompendium;
import com.compendium.core.ICoreConstants;
import com.compendium.ui.*;

/**
 * The UIAboutDialog displays the dialog with the application
 * version number and credits, and links to relevant websites.
 *
 * @author Beatrix Zimmermann / Michelle Bachler
 */
public class UIAboutDialog extends UIDialog {

	/** The button to open a browser to the Memetic website.*/
	private JButton			memeticIcon 				= null;

	/** The button to go to the Labspace help and support forum.*/
	private JButton			pbHelp	 					= null;

	/** The button to open a browser to the KMI website.*/
	private JButton			kmiIcon 					= null;

	/** The button to close this dialog.*/
	private JButton			pbOK						= null;

	/** Holds the main application name.*/
	private JLabel			lblApplicationTitle1		= null;

	/** Holds a brief description of the application.*/
	private JLabel			lblApplicationTitle2		= null;

	/** Holds the version number for this copy of the Compendium application.*/
	private JLabel			lblApplicationVersion		= null;

	/** Holds the developer details for this version of the Compendium Applcation.*/
	private JLabel			lblApplicationdevelopedby	= null;

	/** Holds the contact heading label.*/
	private JLabel			lblContactInfo				= null;

	/** Holds the contact email address.*/
	private JLabel			lblContactInfo2				= null;

	/** Holds the CompendiumInstitute websote address.*/
	private JButton			lblContactInfo3				= null;

	/** Holds the copyright information for this version of the Compendium Application.*/
	private JLabel			lblCopyright				= null;

	/** The parent frame for this dialog.*/
	private JFrame			oParent						= null;

	/**
	 * Constructor, creates and initializes the about dialog.
	 * @param parent, the parent frame for this dialog.
	 */
	public UIAboutDialog (JFrame parent) {
		super(parent, true);

		oParent = parent;
		setTitle("About Compendium...");

		init();
	}

	/**
	 * Initialize the dialog and its components.
	 */
	private void init() {

		getContentPane().setLayout(new BorderLayout());

		GridBagLayout gb = new GridBagLayout();
		GridBagConstraints gc = new GridBagConstraints();
		gc.gridheight = 1;
		gc.anchor = GridBagConstraints.CENTER;
		gc.gridwidth = GridBagConstraints.REMAINDER;
		gc.insets = new Insets(5,5,5,5);

		JPanel oContentPane = new JPanel();
		oContentPane.setLayout(gb);
		oContentPane.setBackground(new Color(255,255,255));
		getContentPane().add(oContentPane, BorderLayout.CENTER);

		JPanel logos = createLogoPanel();

		gb.setConstraints(logos, gc);
		oContentPane.add(logos);

		lblApplicationTitle1 = new JLabel(ICoreConstants.sAPPNAME, JLabel.CENTER);
		lblApplicationTitle1.setFont(new Font("ARIAL", Font.BOLD, 18));
		lblApplicationTitle1.setForeground(new Color(0,0,0));
		gb.setConstraints(lblApplicationTitle1, gc);
		oContentPane.add(lblApplicationTitle1);

		lblApplicationTitle2 = new JLabel("A Tool for the Compendium Methodology", JLabel.CENTER);
		lblApplicationTitle2.setFont(new Font("ARIAL", Font.BOLD, 12));
		lblApplicationTitle2.setForeground(new Color(0,0,0));
		gb.setConstraints(lblApplicationTitle2, gc);
		oContentPane.add(lblApplicationTitle2);

		lblApplicationVersion = new JLabel("Version: "+ICoreConstants.sAPPVERSION, JLabel.CENTER);
		lblApplicationVersion.setFont(new Font("ARIAL", Font.PLAIN, 12));
		lblApplicationVersion.setForeground(new Color(0, 0, 0));
		gb.setConstraints(lblApplicationVersion, gc);
		oContentPane.add(lblApplicationVersion);

		lblApplicationdevelopedby = new JLabel("Developed by: Verizon and The Open University UK", JLabel.CENTER);
		lblApplicationdevelopedby.setFont(new Font("ARIAL", Font.PLAIN, 12));
		lblApplicationdevelopedby.setForeground(new Color(0,0,0));
		gb.setConstraints(lblApplicationdevelopedby, gc);
		oContentPane.add(lblApplicationdevelopedby);

		pbHelp = new UIButton("Help and Support");
		pbHelp.setBackground(new Color(255,255,255));
		pbHelp.setMnemonic(KeyEvent.VK_H);
		pbHelp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				ExecuteControl.launch("http://compendium.open.ac.uk/support/");
				onCancel();
			}
		});
		JPanel but = new JPanel();
		but.add(pbHelp);	
		but.setBackground(Color.white);	
		gb.setConstraints(but, gc);
		oContentPane.add(but);
		
		lblContactInfo3 = new JButton("www.CompendiumInstitute.org");
		lblContactInfo3.setBackground(new Color(255,255,255));
		lblContactInfo3.setRequestFocusEnabled(false);
		lblContactInfo3.setFocusPainted(false);
		gb.setConstraints(lblContactInfo3, gc);
		oContentPane.add(lblContactInfo3);

		ActionListener action = new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				ExecuteControl.launch("http://www.CompendiumInstitute.org");
				onCancel();
			}
		};
		lblContactInfo3.addActionListener(action);

		lblContactInfo3.setFont(new Font("ARIAL", Font.BOLD, 12));
		lblContactInfo3.setForeground(new Color(0, 0, 0));


		///////////////

		pbOK = new UIButton("OK");
		pbOK.setBackground(new Color(255,255,255));
		pbOK.setMnemonic(KeyEvent.VK_O);
		pbOK.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				onCancel();
			}
		});
		gc.insets = new Insets(15,5,5,5);
		gb.setConstraints(pbOK, gc);
		oContentPane.add(pbOK);

		getRootPane().setDefaultButton(pbOK);
		gc.insets = new Insets(5,5,5,5);

		lblCopyright = new JLabel("Copyright (c) 1998-2007 Verizon and The Open University UK", JLabel.CENTER);
		lblCopyright.setFont(new Font("ARIAL", Font.BOLD, 10));
		lblCopyright.setForeground(new Color(0,0,0));
		gb.setConstraints(lblCopyright, gc);
		oContentPane.add(lblCopyright);

        // ACKNOWLEDGEMENT SECTION
		BorderLayout layout4 = new BorderLayout();
		layout4.setHgap(0);
		layout4.setVgap(0);		
		JPanel panel4 = new JPanel(layout4);
		panel4.setBorder(null);		
		panel4.setOpaque(false);
		panel4.setBackground(Color.white);	
       		
        JLabel label8 = new JLabel("Support for Compendium gratefully acknowledged:");
        JLabel label9 = new JLabel("USA: NASA, Hewlett Foundation");       
        JLabel label10 = new JLabel("UK: EPSRC, ESRC, JISC, e-Science Programme");
        
 		label8.setHorizontalAlignment(SwingUtilities.CENTER);	
		label9.setHorizontalAlignment(SwingUtilities.CENTER);		 		
		label10.setHorizontalAlignment(SwingUtilities.CENTER);		
        
        label8.setFont(new Font("ARIAL", Font.BOLD, 10));
        label9.setFont(new Font("ARIAL", Font.BOLD, 10));
        label10.setFont(new Font("ARIAL", Font.BOLD, 10));
 		    	
		label8.setBackground(Color.white);
		label9.setBackground(Color.white);
		label10.setBackground(Color.white);
        
        panel4.add(label8, BorderLayout.NORTH);
        panel4.add(label9, BorderLayout.CENTER);
        panel4.add(label10, BorderLayout.SOUTH);

        //panel4.setSize(300, 45);
        //panel4.setLocation(0,370);
          
		gb.setConstraints(panel4, gc);        
        oContentPane.add(panel4);		
		
		// initialize the dialog
		oContentPane.setBorder(new EmptyBorder(10,10,10,10));

		//setResizable(false);

		setBackground(new Color(255,255,255));

		pack();
		validate();
		repaint();

		this.getRootPane().setDefaultButton(pbOK);
		pbOK.requestFocus();
	}

	/**
	 * Create and return the JPanel holding the two Company logo's.
	 * @return JPanel, the JPanel holding the two Company logo's.
	 */
	private JPanel createLogoPanel() {

		//FlowLayout flow = new FlowLayout();
		JPanel logoPanel = new JPanel();
		logoPanel.setBackground(new Color(255,255,255));

		GridBagLayout gb = new GridBagLayout();
		logoPanel.setLayout(gb);
		GridBagConstraints gc = new GridBagConstraints();

		// KMI logo
		kmiIcon = new JButton(UIImages.get(IUIConstants.OUKMILOGO));
		kmiIcon.setBackground(new Color(255,255,255));
		kmiIcon.setToolTipText("http://kmi.open.ac.uk");
		ActionListener kmiaction = new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				ExecuteControl.launch("http://kmi.open.ac.uk");
				onCancel();
			}
		};
		kmiIcon.addActionListener(kmiaction);

		gc.anchor = GridBagConstraints.NORTH;
		gc.insets = new Insets(5,5,5,5);
		gb.setConstraints(kmiIcon, gc);
		logoPanel.add(kmiIcon);

		// Memetic logo
		memeticIcon = new JButton(UIImages.get(IUIConstants.MEMETICLOGO));
		memeticIcon.setBackground(new Color(255,255,255));
		memeticIcon.setToolTipText("www.memetic-vre.net");
		ActionListener memeticaction = new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				ExecuteControl.launch("http://www.memetic-vre.net/");
				onCancel();
			}
		};
		memeticIcon.addActionListener(memeticaction);

		//gc.anchor = GridBagConstraints.NORTH;
		//gc.insets = new Insets(5,5,5,20);
		//gb.setConstraints(memeticIcon, gc);
		//logoPanel.add(memeticIcon);

		return logoPanel;
	}
}
