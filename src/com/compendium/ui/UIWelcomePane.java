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

package com.compendium.ui;

import java.awt.MediaTracker;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AreaAveragingScaleFilter;
import java.awt.image.BufferedImage;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.ImageProducer;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.io.File;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.*;

import com.compendium.LanguageProperties;
import com.compendium.ProjectCompendium;
import com.compendium.SystemProperties;
import com.compendium.core.ICoreConstants;
import com.compendium.ui.ProjectCompendiumFrame;

/**
 * This class shows the welcome screen.
 *
 * @author	Michelle Bachler
 */
public class UIWelcomePane extends JLayeredPane implements ComponentListener{

	/** A reference to the layer to hold background images. */
	public final static Integer BACKGROUND_LAYER 	= new Integer(200);

	/** A reference to the layer to hold nodes.*/
	public final static Integer	NODE_LAYER			= new Integer(400);

	/** A reference to the layer to hold the rollover hint popups.*/
	public final static Integer HINT_LAYER			= new Integer(450);

	/** The label holding the background layer image.*/
	private JLabel 			lblBackgroundLabel		= null;

	/** The label holding the background layer image.*/
	private JLabel 			lblBackgroundLabel2		= null;

	/** The label holding the background layer image.*/
	private JLabel 			lblBackgroundLabel3		= null;

	/** The label holding the background layer image.*/
	private JLabel 			lblBackgroundLabel4		= null;
	
	/** the panel of buttons.*/
	private JPanel			buttonPanel				= null;

	private boolean			across					= false;
	
	/**
	 * Constructor. Creates and initialises a new instance of UIViewPane.
	 */
	public UIWelcomePane() {
		addBackground();
		this.addComponentListener(this);
		createHelpNodes();
	}
		
	/**
	 * Overridden to always return true.
	 * @return boolean, always returns true.
	 */
  	public boolean isOpaque() {
		return true;
  	}

  	public void componentResized(ComponentEvent e) {  		
		if (buttonPanel != null) {
	   		Dimension mySize = this.getSize();
			Dimension panelSize = buttonPanel.getPreferredSize();
			buttonPanel.setLocation( (mySize.width-panelSize.width)/2, (mySize.height-panelSize.height)/2 );
		}
  	}
  	
  	public void componentMoved(ComponentEvent e) {}  	
  	
  	public void componentShown(ComponentEvent e) {}
  	
  	public void componentHidden(ComponentEvent e) {}
  	
	/**
	 * Set the background image for this view.
	 *
	 */
	public void addBackground() {
		lblBackgroundLabel = new JLabel();

		try {
			ImageIcon oIcon	= new ImageIcon(SystemProperties.welcomeBackgroundImage);
			lblBackgroundLabel.setIcon(oIcon);
			lblBackgroundLabel.setLocation(0,0);
			lblBackgroundLabel.setSize(lblBackgroundLabel.getPreferredSize());
			add(lblBackgroundLabel, BACKGROUND_LAYER);
		}
		catch(Exception ex) {
			ex.printStackTrace();
			ProjectCompendium.APP.displayError("Exception due to: "+ex.getMessage()); //$NON-NLS-1$
		}		
	}

	public void createHelpNodes() {
		String sMessage = SystemProperties.welcomeMessage;
		int app = sMessage.indexOf("<appname>"); //$NON-NLS-1$
		if (app != -1) {
			sMessage = sMessage.substring(0, app)+SystemProperties.applicationName+sMessage.substring(app+9);
		}
		int ver = sMessage.indexOf("<version>"); //$NON-NLS-1$
		if (ver != -1) {
			sMessage = sMessage.substring(0, ver)+ICoreConstants.sAPPVERSION+sMessage.substring(ver+9);
		}
		
		JLabel label = new JLabel(sMessage);
		label.setFont(new Font("Dialog", Font.BOLD, 28)); //$NON-NLS-1$
		label.setHorizontalAlignment(SwingUtilities.CENTER);		
		label.setLocation(20,10);
		label.setSize(label.getPreferredSize());
		add(label, NODE_LAYER);
		
		FlowLayout flow = new FlowLayout();
		flow.setVgap(50);
		buttonPanel = new JPanel(flow);
		buttonPanel.setOpaque(false);
		
		// New Project
		UIImageButton newProject = new UIImageButton(SystemProperties.welcomeNewProjectButtonImage);
		newProject.setToolTipText(LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "UIWelcomePane.createNewProject")); //$NON-NLS-1$
		buttonPanel.add(newProject);
		newProject.addActionListener( new ActionListener() {		
			public void actionPerformed(ActionEvent e) {
				ProjectCompendium.APP.onFileNew();
			}			
		});
		
		// pdf
		UIImageButton quickpdf = new UIImageButton(SystemProperties.welcomeButton1Image);
		quickpdf.setToolTipText(SystemProperties.welcomeButton1Hint);
		buttonPanel.add(quickpdf);
		quickpdf.addActionListener( new ActionListener() {		
			public void actionPerformed(ActionEvent e) {
				File file = new File( SystemProperties.welcomeButton1Link );
				if (file.exists()) {
					ExecuteControl.launch( file.getAbsolutePath() );
				} else {
					ExecuteControl.launch( SystemProperties.welcomeButton1Link );
				}
			}			
		});

		// Quick start
		UIImageButton introMovie = new UIImageButton(SystemProperties.welcomeButton2Image);
		introMovie.setToolTipText(SystemProperties.welcomeButton2Hint);
		buttonPanel.add(introMovie);
		introMovie.addActionListener( new ActionListener() {		
			public void actionPerformed(ActionEvent e) {				
				File file = new File( SystemProperties.welcomeButton2Link );
				if (file.exists()) {
					ExecuteControl.launch( file.getAbsolutePath() );
				} else {
					ExecuteControl.launch( SystemProperties.welcomeButton2Link );
				}
			}			
		});
	
		// Online Movies
		UIImageButton onlineMovies = new UIImageButton(SystemProperties.welcomeButton3Image);
		onlineMovies.setToolTipText(SystemProperties.welcomeButton3Hint);
		buttonPanel.add(onlineMovies);
		onlineMovies.addActionListener( new ActionListener() {		
			public void actionPerformed(ActionEvent e) {
				File file = new File( SystemProperties.welcomeButton3Link );
				if (file.exists()) {
					ExecuteControl.launch( file.getAbsolutePath() );
				} else {
					ExecuteControl.launch( SystemProperties.welcomeButton3Link );
				}
			}			
		});
	
		// Online Movies
		UIImageButton enterComp = new UIImageButton(SystemProperties.welcomeEnterButtonImage);
		enterComp.setToolTipText(LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "UIWelcomePane.enterDesktop")); //$NON-NLS-1$
		buttonPanel.add(enterComp);
		enterComp.addActionListener( new ActionListener() {		
			public void actionPerformed(ActionEvent e) {
				ProjectCompendium.APP.showDesktop();
			}			
		});
		
		Dimension panelSize = buttonPanel.getPreferredSize();
		buttonPanel.setLocation(300, 300);
		buttonPanel.setSize(panelSize);
		
		this.add(buttonPanel, NODE_LAYER);
	}
	
	/**
	 * Remove the background image for this view.
	 *
	 * @param node com.compendium.core.datamodel.NodeSummary, the node to show the detail for.
	 */
	public void removeBackground() {
		int index = getIndexOf(lblBackgroundLabel);
		if (index > -1)
			remove(index);
	}	
}