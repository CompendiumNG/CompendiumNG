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

package com.compendium.ui.menus;

import java.awt.BorderLayout;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.*;
import java.net.URL;

import javax.help.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import com.compendium.*;
import com.compendium.core.CoreUtilities;
import com.compendium.io.http.HttpFileDownloadInputStream;
import com.compendium.ui.*;

/**
 * This class creates and manages the Help menu.
 *
 * @author	Michelle Bachler
 */
public class UIMenuHelp extends UIMenu implements ActionListener {

	/** The 'About' menu item to launch the About dialog.*/
	private JMenuItem			miHelpAbout				= null;

	/** The menu item to launch the help dialog.*/
	private JMenuItem			miHelpHelp				= null;

	/** The 'New Features' menu item.*/
	private JMenuItem			miHelpNew				= null;

	/** The 'Fixes' menu item.*/
	private JMenuItem			miHelpFixes				= null;

	/** The 'Bugs' menu item.*/
	private JMenuItem			miHelpBugs				= null;

	/** The 'Bugs' menu item.*/
	private JMenuItem			miHelpBugzilla			= null;
	
	/** The 'Help On Item' menu item.*/
	private JMenuItem			miHelpButton			= null;

	/** Item to open the quickstart movie*/
	private JMenuItem			miHelpQuickStart		= null;
	
	/** The 'Welcome' menu item.*/
	private JMenuItem			miHelpWelcome			= null;

	/** The 'Quick Reference PDF' menu item.*/
	private JMenuItem			miHelpReference			= null;

	/** The 'Movies' menu item.*/
	private JMenuItem			miHelpMovies			= null;

	/** The item to check for Compendium updates.*/
	private JMenuItem			miCheckForUpdates		= null;

	/** The item to reactivate auto check for Compendium updates on startup.*/
	private JMenuItem			miReactivateChecker		= null;

	/** The HelpSet instance to use.*/
    private HelpSet 					mainHS 			= null;

	/** The HelpBroker instance to use.*/
    private HelpBroker 					mainHB			= null;
		    

	/**
	 * Constructor.
	 * @param bSimple true if the simple interface should be draw, false if the advanced.  
	 * @param hs the HelpSet to use for menus and menuitems.
	 * @param hb the HelpBroker to use for menus and menuitems.
	 */
	public UIMenuHelp(boolean bSimple, HelpSet hs, HelpBroker hb) {
		mainHS = hs;
		mainHB = hb;
		
		this.bSimpleInterface = bSimple;		
		
		mnuMainMenu = new JMenu(LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuHelp.help"));   //$NON-NLS-1$
		mnuMainMenu.setMnemonic((LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuHelp.helpMnemonic")).charAt(0)); //$NON-NLS-1$
		CSH.setHelpIDString(mnuMainMenu,"menus.help");  //$NON-NLS-1$
		
		createMenuItems(bSimple);
	}

	/**
	 * Create and return the Help menu.
	 * @return JMenu the Help menu.
	 */
	private JMenu createMenuItems(boolean bSimple) {
		
		miHelpWelcome = new JMenuItem(LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuHelp.welcome"));   //$NON-NLS-1$
		miHelpWelcome.setMnemonic((LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuHelp.welcomeMnemonic")).charAt(0)); //$NON-NLS-1$
		miHelpWelcome.addActionListener(this);
		mnuMainMenu.add(miHelpWelcome);

		miHelpHelp = new JMenuItem(LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuHelp.contents"));   //$NON-NLS-1$
		miHelpHelp.setMnemonic((LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuHelp.contentsMnemonic")).charAt(0)); //$NON-NLS-1$
		mnuMainMenu.add(miHelpHelp);

		miHelpMovies = new JMenuItem(LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuHelp.onlineMovies"));   //$NON-NLS-1$
		miHelpMovies.setMnemonic((LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuHelp.onlineMoviesMnemonic")).charAt(0)); //$NON-NLS-1$
		miHelpMovies.addActionListener(this);
		mnuMainMenu.add(miHelpMovies);

		mnuMainMenu.addSeparator();

		miHelpReference = new JMenuItem(LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuHelp.quickRef"));   //$NON-NLS-1$
		miHelpReference.setMnemonic((LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuHelp.quickRefMnemonic")).charAt(0)); //$NON-NLS-1$
		miHelpReference.addActionListener(this);
		mnuMainMenu.add(miHelpReference);

		miHelpQuickStart = new JMenuItem(LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuHelp.quickStart"));   //$NON-NLS-1$
		miHelpQuickStart.setMnemonic((LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuHelp.quickStartMnemonic")).charAt(0)); //$NON-NLS-1$
		miHelpQuickStart.addActionListener(this);
		mnuMainMenu.add(miHelpQuickStart);

		mnuMainMenu.addSeparator();

		miHelpBugzilla = new JMenuItem(LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuHelp.bugs"));  //$NON-NLS-1$
		miHelpBugzilla.setMnemonic((LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuHelp.bugsMnemonic")).charAt(0)); //$NON-NLS-1$
		miHelpBugzilla.addActionListener(this);		
		mnuMainMenu.add(miHelpBugzilla);
		
		miHelpNew = new JMenuItem(LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuHelp.releaseNotes"));   //$NON-NLS-1$
		miHelpNew.setMnemonic((LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuHelp.releaseNotesMnemonic")).charAt(0)); //$NON-NLS-1$
		miHelpNew.addActionListener(this);
		mnuMainMenu.add(miHelpNew);
	
		miHelpBugs = new JMenuItem(LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuHelp.knownIssues"));   //$NON-NLS-1$
		miHelpBugs.setMnemonic((LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuHelp.knownIssuesMnemonic")).charAt(0)); //$NON-NLS-1$
		miHelpBugs.addActionListener(this);
		mnuMainMenu.add(miHelpBugs);
		
		mnuMainMenu.addSeparator();
		
		miCheckForUpdates = new JMenuItem(LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuHelp.updates"));   //$NON-NLS-1$
		miCheckForUpdates.setMnemonic((LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuHelp.updatesMnemonic")).charAt(0)); //$NON-NLS-1$
		miCheckForUpdates.addActionListener(this);
		mnuMainMenu.add(miCheckForUpdates);
		
		miReactivateChecker = new JCheckBoxMenuItem(LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuHelp.reactivateChecks"));   //$NON-NLS-1$
		miReactivateChecker.setMnemonic((LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuHelp.reactivateChecksMnemonic")).charAt(0)); //$NON-NLS-1$
		miReactivateChecker.addActionListener(this);
		mnuMainMenu.add(miReactivateChecker);				
		if (FormatProperties.autoUpdateCheckerOn) {
			miReactivateChecker.setSelected(true);
		} else {
			miReactivateChecker.setSelected(false);
		}
		
		miHelpAbout = new JMenuItem(LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuHelp.about"));   //$NON-NLS-1$
		miHelpAbout.setMnemonic((LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuHelp.aboutMnemonic")).charAt(0)); //$NON-NLS-1$
		miHelpAbout.addActionListener(this);
		mnuMainMenu.add(miHelpAbout);

		mnuMainMenu.addSeparator();

		miHelpButton = new JMenuItem(LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuHelp.itemHelp"));   //$NON-NLS-1$
		miHelpButton.setMnemonic((LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuHelp.itemHelpMnemonic")).charAt(0)); //$NON-NLS-1$
		mnuMainMenu.add(miHelpButton);

		if (mainHB != null && mainHS != null) {
			//miHelpHelp.addActionListener(new CSH.DisplayHelpFromSource(mainHB));
			
			if (miHelpHelp != null) {
				mainHB.enableHelpOnButton(miHelpHelp, "compendium.intro", mainHS);  //$NON-NLS-1$
			}
			if (miHelpButton != null) {
				miHelpButton.addActionListener(new CSH.DisplayHelpAfterTracking(mainHB));
			}
		}

		if (bSimple) {
			addExtenderButton();
			setDisplay(bSimple);
		}

		return mnuMainMenu;
	}

	/**
	 * Hide/show items depending on whether the user wants the simple view or simple.
	 * @param bSimple
	 */
	protected void setDisplay(boolean bSimple) {
		if (bSimple) {
			miHelpNew.setVisible(false);
			miHelpBugs.setVisible(false);

		} else {
			miHelpNew.setVisible(true);
			miHelpBugs.setVisible(true);
		}
		
		setControlItemStatus(bSimple);
		
		JPopupMenu pop = mnuMainMenu.getPopupMenu();
		if (pop.isVisible()) {
			pop.setVisible(false);
			pop.setVisible(true);
			pop.requestFocus();
		}
	}
	
	/**
	 * Handles most menu action event for this application.
	 *
	 * @param evt, the generated action event to be handled.
	 */
	public void actionPerformed(ActionEvent evt) {

		ProjectCompendium.APP.setWaitCursor();

		Object source = evt.getSource();

		if (source.equals(miHelpAbout)) {
			ProjectCompendium.APP.onHelpAbout();
		} else if ( source.equals(miHelpReference)) {
			File file = new File("System"+ProjectCompendium.sFS+"resources"+ProjectCompendium.sFS+"Help"+ProjectCompendium.sFS+"Docs"+ProjectCompendium.sFS+"CompendiumQuickRef.pdf");   //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
			ExecuteControl.launch( file.getAbsolutePath() );
		} else if ( source.equals(miHelpQuickStart)) {
			if (CoreUtilities.isFile(SystemProperties.quickStartMovie)) {
				File file = new File( SystemProperties.quickStartMovie );
				ExecuteControl.launch( file.getAbsolutePath() );
			} else {
				ExecuteControl.launch(SystemProperties.quickStartMovie);
			}
		} else if ( source.equals(miHelpMovies)) {
			ExecuteControl.launch( "http://compendium.open.ac.uk/institute/training/videos/");  //$NON-NLS-1$
		} else if ( source.equals(miHelpBugzilla)) {
			ExecuteControl.launch( "http://compendium.open.ac.uk/bugzilla/");   //$NON-NLS-1$
		} else if ( source.equals(miHelpWelcome)) {
			ProjectCompendium.APP.showWelcome();
		} else if ( source.equals(miHelpNew)) {
			if (CoreUtilities.isFile(SystemProperties.releaseNotesURL)) {
				File file = new File( SystemProperties.releaseNotesURL );
				ExecuteControl.launch( file.getAbsolutePath() );
			} else {
				ExecuteControl.launch(SystemProperties.releaseNotesURL);
			}
		} else if ( source.equals(miHelpBugs)) {
			ExecuteControl.launch( "http://compendium.open.ac.uk/bugzilla/buglist.cgi?query_format=advanced&short_desc_type=allwordssubstr&short_desc=&long_desc_type=substring&long_desc=&bug_file_loc_type=allwordssubstr&bug_file_loc=&status_whiteboard_type=allwordssubstr&status_whiteboard=&keywords_type=allwords&keywords=Known_Issue&bug_status=UNCONFIRMED&bug_status=NEW&bug_status=ASSIGNED&bug_status=REOPENED&bug_status=RESOLVED&bug_status=VERIFIED&bug_status=CLOSED&emailassigned_to1=1&emailtype1=substring&email1=&emailassigned_to2=1&emailreporter2=1&emailqa_contact2=1&emailcc2=1&emailtype2=substring&email2=&bugidtype=include&bug_id=&votes=&chfieldfrom=&chfieldto=Now&chfieldvalue=&cmdtype=doit&order=Reuse+same+sort+as+last+time&field0-0-0=noop&type0-0-0=noop&value0-0-0=");   //$NON-NLS-1$
		} else if (source.equals(miCheckForUpdates) ){
			checkForUpdates();
		} else if (source.equals(miReactivateChecker)) {
			if (((JCheckBoxMenuItem)miReactivateChecker).isSelected()) {
				FormatProperties.autoUpdateCheckerOn = true;
				FormatProperties.setFormatProp( "autoUpdateCheckerOn", "true" ); //$NON-NLS-1$ //$NON-NLS-2$
			} else {
				FormatProperties.autoUpdateCheckerOn = false;
				FormatProperties.setFormatProp( "autoUpdateCheckerOn", "false" ); //$NON-NLS-1$ //$NON-NLS-2$
			}
			FormatProperties.saveFormatProps();
		}
		
		ProjectCompendium.APP.setDefaultCursor();
	}

	/**
	 * Check if the current version of Compendium being run here is out-of-date.
	 * Tell the user if it is and offer to link to download.
	 */
	private void checkForUpdates() {
		// check for software version
		try {
			// GET VERSION
			HttpFileDownloadInputStream stream = new HttpFileDownloadInputStream(new URL("http://compendium.open.ac.uk/institute/download/version.txt"));
			String version = stream.downloadToString();
			stream.close();
			System.out.println("version for checking = "+version);
			if (CoreUtilities.isNewerVersion(version)) {
				// GET ADDITIONAL TEXT
				HttpFileDownloadInputStream stream2 = new HttpFileDownloadInputStream(new URL("http://compendium.open.ac.uk/institute/download/version-text.txt"));
				String blurb = stream2.downloadToString();
				stream2.close();
				
				JLabel label = new JLabel(UIImages.get(IUIConstants.COMPENDIUM_ICON_32));
				label.setHorizontalAlignment(SwingConstants.LEFT);
				
     			Object[] fields = {label, "\n"+LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "ProjectCompendium.checkVersionMessage1")+"\n\n"+
     					blurb+"\n"}; //$NON-NLS-1$

     			final String okButton = LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "ProjectCompendium.downloadButton"); //$NON-NLS-1$
     			final String cancelButton = LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "ProjectCompendium.closeButton"); //$NON-NLS-1$
     			Object[] options = {okButton, cancelButton};

      			final JOptionPane optionPane = new JOptionPane(fields,
                                  JOptionPane.PLAIN_MESSAGE,
                                  JOptionPane.OK_CANCEL_OPTION,
                                  null,
                                  options,
                                  options[0]);
 
 				final JDialog dlg = new JDialog(ProjectCompendium.APP, true);
		        optionPane.addPropertyChangeListener(new PropertyChangeListener() {
		        	
		        	public void propertyChange(PropertyChangeEvent e) {
		            	String prop = e.getPropertyName();
		            	if ((e.getSource() == optionPane)
		                    && (prop.equals(JOptionPane.VALUE_PROPERTY) ||
		                       prop.equals(JOptionPane.INPUT_VALUE_PROPERTY))) {
		                    Object value = optionPane.getValue();
		                    
		                    if (value == JOptionPane.UNINITIALIZED_VALUE) {
		                        return;
		                    }
		                    
		                    if (value.equals(okButton)) {
		                    	try {
			                    	ExecuteControl.launchFile("http://compendium.open.ac.uk/institute/download/download.htm");
		                    	} catch(Exception ex) {
		                    		System.out.println(ex.getLocalizedMessage());
		                    	}
		                    }
							dlg.setVisible(false);
							dlg.dispose();
		            	}
		        	}
		        });
				
				dlg.getContentPane().add(optionPane);
				dlg.pack();
				dlg.setSize(dlg.getPreferredSize());
				UIUtilities.centerComponent(dlg, ProjectCompendium.APP);
				dlg.setVisible(true);
			} else {
				ProjectCompendium.APP.displayMessage(LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuHelp.updatesMessage"), LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuHelp.updates"));   //$NON-NLS-1$);				
			}
		} catch(Exception ex) {
			System.out.println(ex.getLocalizedMessage());
			ex.printStackTrace();
			System.out.flush();
		}	
	}

	/**
	 * Updates the menus when a database project is closed.
	 */
	public void onDatabaseClose() {}

	/**
	 * Updates the menus when a database projects is opened.
	 */
	public void onDatabaseOpen() {}

	/**
 	 * Enable/disable cut copy and delete menu items.
  	 * @param selected, true for enabled, false for disabled.
	 */
	public void setNodeOrLinkSelected(boolean selected) {}

	/**
 	 * Indicates when nodes on a view are selected and deselected.
 	 * Does Nothing.
  	 * @param selected true for selected false for deselected.
	 */
	public void setNodeSelected(boolean selected) {}
	
	/**
	 * Enabled and disable the Welcome option depending if the welcome page is currently displayed.
	 * @param enable
	 */
	public void setWelcomeEnabled(boolean enable) {
		miHelpWelcome.setEnabled(enable);
	}
	
	/**
	 * Open the about dialog.
	 */
	public void openAbout() {
		if (miHelpAbout != null) {
			miHelpAbout.doClick();
		}
	}
	
	/**
	 * Update the look and feel of the menu.
	 */
	public void updateLAF() {
		if (mnuMainMenu != null)
			SwingUtilities.updateComponentTreeUI(mnuMainMenu);
	}		
}
