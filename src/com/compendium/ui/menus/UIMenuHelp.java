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


package com.compendium.ui.menus;

import java.awt.event.*;
import java.io.*;
import javax.help.*;
import javax.swing.*;

import com.compendium.*;
import com.compendium.core.CoreUtilities;
import com.compendium.ui.*;

/**
 * This class creates and manages the Help menu.
 *
 * @author	Michelle Bachler
 */
public class UIMenuHelp implements IUIMenu, ActionListener {

	/** The Help menu.*/
	private JMenu				mnuMainMenu				= null;

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

	/** The 'Welcome' menu item.*/
	private JMenuItem			miHelpWelcome			= null;

	/** The 'Quick Reference PDF' menu item.*/
	private JMenuItem			miHelpReference			= null;

	/** The 'Movies' menu item.*/
	private JMenuItem			miHelpMovies			= null;

	/** The HelpSet instance to use.*/
    private HelpSet 					mainHS 			= null;

	/** The HelpBroker instance to use.*/
    private HelpBroker 					mainHB			= null;
		
	/**Indicates whether this menu is draw as a Simple interface or a advance user inteerface.*/
	private boolean bSimpleInterface					= false;	
    

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
		
		mnuMainMenu = new JMenu("Help");  //$NON-NLS-1$
		CSH.setHelpIDString(mnuMainMenu,"menus.help"); //$NON-NLS-1$
		mnuMainMenu.setMnemonic(KeyEvent.VK_H);
		
		createMenuItems();
	}

	
	/**
	 * If true, redraw the simple form of this menu, else redraw the complex form.
	 * @param isSimple true for the simple menu, false for the advanced.
	 */
	public void setIsSimple(boolean isSimple) {
		bSimpleInterface = isSimple;
		recreateMenu();
	}

	/**
	 * Redraw the menu items
	 */
	private void recreateMenu() {
		mnuMainMenu.removeAll();
		createMenuItems();
		onDatabaseOpen();				
	}	

	/**
	 * Create and return the Help menu.
	 * @return JMenu the Help menu.
	 */
	private JMenu createMenuItems() {
		
		miHelpHelp = new JMenuItem("Help Contents...");  //$NON-NLS-1$
		miHelpHelp.setMnemonic(KeyEvent.VK_H);
		mnuMainMenu.add(miHelpHelp);

		miHelpMovies = new JMenuItem("Online Help Movies");  //$NON-NLS-1$
		miHelpMovies.addActionListener(this);
		miHelpMovies.setMnemonic(KeyEvent.VK_M);
		mnuMainMenu.add(miHelpMovies);

		mnuMainMenu.addSeparator();

		miHelpReference = new JMenuItem("Quick Reference (pdf)");  //$NON-NLS-1$
		miHelpReference.setMnemonic(KeyEvent.VK_R);
		miHelpReference.addActionListener(this);
		mnuMainMenu.add(miHelpReference);

		miHelpWelcome = new JMenuItem("Quick Start Movie (html)");  //$NON-NLS-1$
		miHelpWelcome.setMnemonic(KeyEvent.VK_Q);
		miHelpWelcome.addActionListener(this);
		mnuMainMenu.add(miHelpWelcome);

		mnuMainMenu.addSeparator();

		miHelpBugzilla = new JMenuItem("Online Bug Reporting"); 
		miHelpBugzilla.setMnemonic(KeyEvent.VK_B);
		miHelpBugzilla.addActionListener(this);		
		mnuMainMenu.add(miHelpBugzilla);
		
		miHelpNew = new JMenuItem("Online Release Notes");  //$NON-NLS-1$
		miHelpNew.setMnemonic(KeyEvent.VK_N);
		miHelpNew.addActionListener(this);
		mnuMainMenu.add(miHelpNew);
	
		//miHelpFixes = new JMenuItem("Bug Fixes (html)"); //$NON-NLS-1$
		//miHelpFixes.setMnemonic(KeyEvent.VK_B);
		//miHelpFixes.addActionListener(this);
		//mnuMainMenu.add(miHelpFixes);
	
		miHelpBugs = new JMenuItem("Online Known Issues");  //$NON-NLS-1$
		miHelpBugs.setMnemonic(KeyEvent.VK_K);
		miHelpBugs.addActionListener(this);
		mnuMainMenu.add(miHelpBugs);

		mnuMainMenu.addSeparator();

		miHelpAbout = new JMenuItem("About...");  //$NON-NLS-1$
		miHelpAbout.setMnemonic(KeyEvent.VK_A);
		miHelpAbout.addActionListener(this);
		mnuMainMenu.add(miHelpAbout);

		mnuMainMenu.addSeparator();

		miHelpButton = new JMenuItem("Help On Item");  //$NON-NLS-1$
		miHelpButton.setMnemonic('I');
		mnuMainMenu.add(miHelpButton);

		if (mainHB != null && mainHS != null) {
			//miHelpHelp.addActionListener(new CSH.DisplayHelpFromSource(mainHB));
			
			if (miHelpHelp != null) {
				mainHB.enableHelpOnButton(miHelpHelp, "compendium.intro", mainHS); //$NON-NLS-1$
			}
			if (miHelpButton != null) {
				miHelpButton.addActionListener(new CSH.DisplayHelpAfterTracking(mainHB));
			}
		}

		return mnuMainMenu;
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
			File file = new File("System"+ProjectCompendium.sFS+"resources"+ProjectCompendium.sFS+"Help"+ProjectCompendium.sFS+"Docs"+ProjectCompendium.sFS+"CompendiumQuickRef.pdf"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
			ExecuteControl.launch( file.getAbsolutePath() );
		} else if ( source.equals(miHelpWelcome)) {
			File file = new File("System"+ProjectCompendium.sFS+"resources"+ProjectCompendium.sFS+"Help"+ProjectCompendium.sFS+"Movies"+ProjectCompendium.sFS+"welcome.html"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
			ExecuteControl.launch( file.getAbsolutePath() );
		} else if ( source.equals(miHelpMovies)) {
			ExecuteControl.launch( "http://www.compendiuminstitute.org/training/videos/"); //$NON-NLS-1$
		} else if ( source.equals(miHelpBugzilla)) {
			ExecuteControl.launch( "http://compendium.open.ac.uk/bugzilla/");  
		} 
		
		else if ( source.equals(miHelpNew)) {
			ExecuteControl.launch( "http://www.compendiuminstitute.org/download/release-notes-1.5.2.htm");  
		} /*else if ( source.equals(miHelpFixes)) {
			ExecuteControl.launch( "http://www.compendiuminstitute.org/download/release-notes-1.5.2.htm#bugs"); 
		}*/ else if ( source.equals(miHelpBugs)) {
			ExecuteControl.launch( "http://compendium.open.ac.uk/bugzilla/buglist.cgi?query_format=advanced&short_desc_type=allwordssubstr&short_desc=&long_desc_type=substring&long_desc=&bug_file_loc_type=allwordssubstr&bug_file_loc=&status_whiteboard_type=allwordssubstr&status_whiteboard=&keywords_type=allwords&keywords=Known_Issue&bug_status=UNCONFIRMED&bug_status=NEW&bug_status=ASSIGNED&bug_status=REOPENED&bug_status=RESOLVED&bug_status=VERIFIED&bug_status=CLOSED&emailassigned_to1=1&emailtype1=substring&email1=&emailassigned_to2=1&emailreporter2=1&emailqa_contact2=1&emailcc2=1&emailtype2=substring&email2=&bugidtype=include&bug_id=&votes=&chfieldfrom=&chfieldto=Now&chfieldvalue=&cmdtype=doit&order=Reuse+same+sort+as+last+time&field0-0-0=noop&type0-0-0=noop&value0-0-0=");  
		}
		
		ProjectCompendium.APP.setDefaultCursor();
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
	
	/**
	 * Return a reference to the main menu.
	 * @return JMenu a reference to the main menu.
	 */
	public JMenu getMenu() {
		return mnuMainMenu;
	}	
}
