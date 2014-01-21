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

import com.compendium.LanguageProperties;
import com.compendium.ProjectCompendium;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.File;

import static com.compendium.ProjectCompendium.Config;
/**
 * This class shows the welcome screen.
 *
 * @author	Michelle Bachler
 */
class UIWelcomePane extends JLayeredPane implements ComponentListener{
	/**
	 * class's own logger
	 */
	private final Logger log = LoggerFactory.getLogger(getClass());
	/** A reference to the layer to hold background images. */
	private final static Integer BACKGROUND_LAYER 	= new Integer(200);

	/** A reference to the layer to hold nodes.*/
	private final static Integer	NODE_LAYER			= new Integer(400);

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
    void addBackground() {
		lblBackgroundLabel = new JLabel();

		try {
			ImageIcon oIcon	= new ImageIcon(ProjectCompendium.DIR_IMAGES + Config.getString("system.welcomeBackgroundImage"));
			lblBackgroundLabel.setIcon(oIcon);
			lblBackgroundLabel.setLocation(0,0);
			lblBackgroundLabel.setSize(lblBackgroundLabel.getPreferredSize());
			add(lblBackgroundLabel, BACKGROUND_LAYER);
		}
		catch(Exception ex) {
			log.error("Exception...", ex);
			ProjectCompendium.APP.displayError("Exception due to: "+ex.getMessage()); //$NON-NLS-1$
		}		
	}

	void createHelpNodes() {
		String sMessage = Config.getString("system.welcomeMessage");
		
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
		UIImageButton newProject = new UIImageButton(ProjectCompendium.DIR_IMAGES + Config.getString("system.welcome.button0.image"));
		newProject.setToolTipText(LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "UIWelcomePane.createNewProject")); //$NON-NLS-1$
		buttonPanel.add(newProject);
		newProject.addActionListener( new ActionListener() {		
			public void actionPerformed(ActionEvent e) {
				ProjectCompendium.APP.onFileNew();
			}			
		});
		
		// pdf
		UIImageButton quickpdf = new UIImageButton(ProjectCompendium.DIR_IMAGES + Config.getString("system.welcome.button1.image"));
		quickpdf.setToolTipText(Config.getString("system.welcome.button1.hint"));
		buttonPanel.add(quickpdf);
		quickpdf.addActionListener( new ActionListener() {		
			
			public void actionPerformed(ActionEvent e) {
				File file = new File( ProjectCompendium.DIR_DOC + Config.getString("system.welcome.button1.link"));
				if (file.exists()) {
					ExecuteControl.launch(file.getAbsolutePath());
				}
			}			
		});

		// Quick start
		UIImageButton introMovie = new UIImageButton(ProjectCompendium.DIR_IMAGES + Config.getString("system.welcome.button2.image"));
		introMovie.setToolTipText(Config.getString("system.welcome.button2.hint"));
		buttonPanel.add(introMovie);
		introMovie.addActionListener( new ActionListener() {		
			public void actionPerformed(ActionEvent e) {				
				File file = new File(ProjectCompendium.DIR_HELP + "Movies" + File.separator +Config.getString("system.welcome.button2.link"));
				if (file.exists()) {
					ExecuteControl.launch( file.getAbsolutePath() );
				} else {
					ExecuteControl.launch( Config.getString("system.welcome.button2.link") );
				}
			}			
		});
	
		// Online Movies
		UIImageButton onlineMovies = new UIImageButton(ProjectCompendium.DIR_IMAGES + Config.getString("system.welcome.button3.image"));
		onlineMovies.setToolTipText(Config.getString("system.welcome.button3.hint"));
		buttonPanel.add(onlineMovies);
		onlineMovies.addActionListener( new ActionListener() {		
			public void actionPerformed(ActionEvent e) {
					ExecuteControl.launch(Config.getString("system.welcome.button3.link") );
			}			
		});
	
		// Online Movies
		UIImageButton enterComp = new UIImageButton(ProjectCompendium.DIR_IMAGES + Config.getString("system.welcome.enter.button.image"));
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