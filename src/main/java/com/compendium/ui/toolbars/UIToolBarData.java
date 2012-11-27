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
import java.util.*;

import javax.help.*;
import javax.swing.*;

import com.compendium.core.*;
import com.compendium.*;
import com.compendium.ui.*;
import com.compendium.ui.toolbars.system.*;
import com.compendium.core.datamodel.*;


/**
 * This class manages all the toolbars
 *
 * @author	Michelle Bachler
 * @version	1.0
 */
public class UIToolBarData implements IUIToolBar, ActionListener, IUIConstants {

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

	/** The data source toolbar.*/
	private UIToolBar			tbrToolBar 		= null;

	/** The choicebox with the MySQL profile options.*/
	private JComboBox 			cbProfiles			= null;

	/** The button to open the database administration dialog.*/
	private JButton				pbDataAdmin			= null;

	/** Indicates if the cbProfiles has been changed by the user or the code.*/
	private boolean				autoSelect 			= false;

	/** The refresh cached data toobar button.*/
	private JButton				pbRefresh			= null;

	/** The choicebox with the refresh time options.*/
	private JComboBox 			cbRefreshTime		= null;

	/** Indicates if the cbRefreshTime has been changed by the user or the code.*/
	private boolean				autoSelectRefresh		= false;

	/** The panel for the line thickness choice box.*/
	private JPanel 				timePanel 			= null;

	/** The action listener for the draw choicebox.*/
	private ActionListener 		timeActionListener 	= null;

	/** The button to start/stop timed refreshing of the cache.*/
	private JButton				pbTimedRefresh		= null;
	
	/**
	 * Create a new instance of UIToolBarData, with the given properties.
	 * @param oManager the IUIToolBarManager that is managing this toolbar.
	 * @param parent the parent frame for the application.
	 * @param nType the unique identifier for this toolbar.
	 */
	public UIToolBarData(IUIToolBarManager oManager, ProjectCompendiumFrame parent, int nType) {

		this.oParent = parent;
		this.oManager = oManager;
		this.nType = nType;
		createToolBar(DEFAULT_ORIENTATION);
	}	
	
	/**
	 * Create a new instance of UIToolBarData, with the given properties.
	 * @param oManager the IUIToolBarManager that is managing this toolbar.
	 * @param parent the parent frame for the application.
	 * @param nType the unique identifier for this toolbar.
	 * @param orientation the orientation of this toolbars ui object.      
	 */
	public UIToolBarData(IUIToolBarManager oManager, ProjectCompendiumFrame parent, int nType, int orientation) {

		this.oParent = parent;
		this.oManager = oManager;
		this.nType = nType;
		createToolBar(orientation);
	}

	/**
	 * Create and return the toolbar with all the data source options.
	 * @return UIToolBar, the toolbar with all the data source options.
	 */
	private UIToolBar createToolBar(int orientation) {

		tbrToolBar = new UIToolBar(LanguageProperties.getString(LanguageProperties.TOOLBARS_BUNDLE, "UIToolBarData.dataSourceToolbar"), UIToolBar.NORTHSOUTH); //$NON-NLS-1$
		tbrToolBar.setOrientation(orientation);

		pbDataAdmin = tbrToolBar.createToolBarButton(LanguageProperties.getString(LanguageProperties.TOOLBARS_BUNDLE, "UIToolBarData.openAdmin"), UIImages.get(CONNECT_ICON)); //$NON-NLS-1$
		pbDataAdmin.addActionListener(this);
		pbDataAdmin.setEnabled(true);
		tbrToolBar.add(pbDataAdmin);
		CSH.setHelpIDString(pbDataAdmin,"toolbars.data"); //$NON-NLS-1$

		tbrToolBar.addSeparator();

		JLabel label = new JLabel(LanguageProperties.getString(LanguageProperties.TOOLBARS_BUNDLE, "UIToolBarData.dataConnection")); //$NON-NLS-1$
		label.setFont( new Font("Dialog", Font.PLAIN, 12 )); //$NON-NLS-1$
		tbrToolBar.add(label);

		tbrToolBar.add( createProfilesChoiceBox() );

		tbrToolBar.addSeparator();

		pbRefresh = tbrToolBar.createToolBarButton(LanguageProperties.getString(LanguageProperties.TOOLBARS_BUNDLE, "UIToolBarData.refreshViews"), UIImages.get(REFRESH_CACHE_ICON)); //$NON-NLS-1$
		pbRefresh.addActionListener(this);
		pbRefresh.setEnabled(true);
		tbrToolBar.add(pbRefresh);
		CSH.setHelpIDString(pbRefresh,"toolbars.data"); //$NON-NLS-1$

		tbrToolBar.add( createRefreshChoiceBox() );

		pbTimedRefresh = tbrToolBar.createToolBarButton(LanguageProperties.getString(LanguageProperties.TOOLBARS_BUNDLE, "UIToolBarData.startTimer"), UIImages.get(RED_LIGHT_ICON)); //$NON-NLS-1$
		pbTimedRefresh.setFont( new Font("Dialog", Font.PLAIN, 12 )); //$NON-NLS-1$
		pbTimedRefresh.setText(LanguageProperties.getString(LanguageProperties.TOOLBARS_BUNDLE, "UIToolBarData.start")); //$NON-NLS-1$
		pbTimedRefresh.addActionListener(this);
		pbTimedRefresh.setEnabled(true);
		tbrToolBar.add(pbTimedRefresh);
		CSH.setHelpIDString(pbTimedRefresh,"toolbars.data"); //$NON-NLS-1$

		if (FormatProperties.refreshTimerRunning 
				&& FormatProperties.nDatabaseType == ICoreConstants.MYSQL_DATABASE 
					&& !oParent.oCurrentMySQLConnection.getServer().equals(ICoreConstants.sDEFAULT_DATABASE_ADDRESS)) {
			if (oParent.oRefreshManager.startTimer()) {
				pbRefresh.setEnabled(false);
				pbTimedRefresh.setIcon(UIImages.get(GREEN_LIGHT_ICON));
				pbTimedRefresh.setText(LanguageProperties.getString(LanguageProperties.TOOLBARS_BUNDLE, "UIToolBarData.stop")); //$NON-NLS-1$
				pbTimedRefresh.setToolTipText(LanguageProperties.getString(LanguageProperties.TOOLBARS_BUNDLE, "UIToolBarData.stopRefersh")); //$NON-NLS-1$
			}
		}

		tbrToolBar.addSeparator();

		CSH.setHelpIDString(tbrToolBar,"toolbars.data"); //$NON-NLS-1$

		return tbrToolBar;
	}
	
	/**
	 * Create a choicbox for cache refresh timing options and return the panel it is in.
	 * @return JPanel, the panel holding the new choicebox for the cache refresh timing options.
	 */
	private JPanel createRefreshChoiceBox() {

		timePanel = new JPanel(new BorderLayout());
		CSH.setHelpIDString(timePanel,"toolbars.data"); //$NON-NLS-1$

		cbRefreshTime = new JComboBox();
		cbRefreshTime.setToolTipText(LanguageProperties.getString(LanguageProperties.TOOLBARS_BUNDLE, "UIToolBarData.setRefreshDuration")); //$NON-NLS-1$
		cbRefreshTime.setOpaque(true);
		cbRefreshTime.setEditable(false);
		cbRefreshTime.setEnabled(false);
		cbRefreshTime.setMaximumRowCount(13);
		cbRefreshTime.setFont( new Font("Dialog", Font.PLAIN, 12 )); //$NON-NLS-1$

		cbRefreshTime.addItem(new String(LanguageProperties.getString(LanguageProperties.TOOLBARS_BUNDLE, "UIToolBarData.1sec"))); //$NON-NLS-1$
		cbRefreshTime.addItem(new String(LanguageProperties.getString(LanguageProperties.TOOLBARS_BUNDLE, "UIToolBarData.5secs"))); //$NON-NLS-1$
		cbRefreshTime.addItem(new String(LanguageProperties.getString(LanguageProperties.TOOLBARS_BUNDLE, "UIToolBarData.10secs"))); //$NON-NLS-1$
		cbRefreshTime.addItem(new String(LanguageProperties.getString(LanguageProperties.TOOLBARS_BUNDLE, "UIToolBarData.20secs"))); //$NON-NLS-1$
		cbRefreshTime.addItem(new String(LanguageProperties.getString(LanguageProperties.TOOLBARS_BUNDLE, "UIToolBarData.30secs"))); //$NON-NLS-1$
		cbRefreshTime.addItem(new String(LanguageProperties.getString(LanguageProperties.TOOLBARS_BUNDLE, "UIToolBarData.40secs"))); //$NON-NLS-1$
		cbRefreshTime.addItem(new String(LanguageProperties.getString(LanguageProperties.TOOLBARS_BUNDLE, "UIToolBarData.50secs"))); //$NON-NLS-1$
		cbRefreshTime.addItem(new String(LanguageProperties.getString(LanguageProperties.TOOLBARS_BUNDLE, "UIToolBarData.1min"))); //$NON-NLS-1$
		cbRefreshTime.addItem(new String(LanguageProperties.getString(LanguageProperties.TOOLBARS_BUNDLE, "UIToolBarData.5mins"))); //$NON-NLS-1$
		cbRefreshTime.addItem(new String(LanguageProperties.getString(LanguageProperties.TOOLBARS_BUNDLE, "UIToolBarData.10mins"))); //$NON-NLS-1$
		cbRefreshTime.addItem(new String(LanguageProperties.getString(LanguageProperties.TOOLBARS_BUNDLE, "UIToolBarData.15mins"))); //$NON-NLS-1$
		cbRefreshTime.addItem(new String(LanguageProperties.getString(LanguageProperties.TOOLBARS_BUNDLE, "UIToolBarData.20mins"))); //$NON-NLS-1$
		cbRefreshTime.addItem(new String(LanguageProperties.getString(LanguageProperties.TOOLBARS_BUNDLE, "UIToolBarData.30mins"))); //$NON-NLS-1$

		cbRefreshTime.validate();

		setRefreshChoiceBoxSelection(FormatProperties.refreshTime);

		DefaultListCellRenderer timeRenderer = new DefaultListCellRenderer() {
			public Component getListCellRendererComponent(
   		     	JList list,
   		        Object value,
            	int modelIndex,
            	boolean isSelected,
            	boolean cellHasFocus)
            {
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

				setText((String) value);
				return this;
			}
		};

		cbRefreshTime.setRenderer(timeRenderer);

		timeActionListener = new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
 				int ind = cbRefreshTime.getSelectedIndex();

				if (ind == 0) {
					setRefreshTime(1);
				} else if (ind == 1) {
					setRefreshTime(5);
				} else if (ind == 2) {
					setRefreshTime(10);
				} else if (ind == 3) {
					setRefreshTime(20);
				} else if (ind == 4) {
					setRefreshTime(30);
				} else if (ind == 5) {
					setRefreshTime(40);
				} else if (ind == 6) {
					setRefreshTime(50);
				} else if (ind == 7) {
					setRefreshTime(60);
				} else if (ind == 8) {
					setRefreshTime(300);
				} else if (ind == 9) {
					setRefreshTime(600);
				} else if (ind == 10) {
					setRefreshTime(900);
				} else if (ind == 11) {
					setRefreshTime(1200);
				} else if (ind == 12) {
					setRefreshTime(1800);
				}
         	}
		};
        cbRefreshTime.addActionListener(timeActionListener);

		timePanel.add(new JLabel(" "), BorderLayout.WEST); //$NON-NLS-1$
		timePanel.add(cbRefreshTime, BorderLayout.CENTER);
		return timePanel;
	}

	/**
	 * Set the cache refresh time to the pased number of seconds.
	 * @param nseconds the number of seconds to set as the refresh time.
	 */
	private void setRefreshTime(int nSeconds) {
		ProjectCompendium.APP.oRefreshManager.setRefreshTime(nSeconds);

		if (!autoSelectRefresh) {
			FormatProperties.refreshTime = nSeconds;
			FormatProperties.setFormatProp("refreshTime", String.valueOf(nSeconds)); //$NON-NLS-1$
			FormatProperties.saveFormatProps();
		}
	}
	
	/**
	 * Set the appropriate selection on the Refresh choice box depending on the seconds passed.
	 * @param nSeconds the number of section the refresh timer is set to.
	 */
	private void setRefreshChoiceBoxSelection(int nSeconds) {
		autoSelectRefresh = true;
		if (nSeconds == 1) {
			cbRefreshTime.setSelectedIndex(0);
		} else if (nSeconds == 5) {
			cbRefreshTime.setSelectedIndex(1);
		} else if (nSeconds == 10) {
			cbRefreshTime.setSelectedIndex(2);
		} else if (nSeconds == 20) {
			cbRefreshTime.setSelectedIndex(3);
		} else if (nSeconds == 30) {
			cbRefreshTime.setSelectedIndex(4);
		} else if (nSeconds == 40) {
			cbRefreshTime.setSelectedIndex(5);
		} else if (nSeconds == 50) {
			cbRefreshTime.setSelectedIndex(6);
		} else if (nSeconds == 60) {
			cbRefreshTime.setSelectedIndex(7);
		} else if (nSeconds == 300) {
			cbRefreshTime.setSelectedIndex(8);
		} else if (nSeconds == 600) {
			cbRefreshTime.setSelectedIndex(9);
		} else if (nSeconds == 900) {
			cbRefreshTime.setSelectedIndex(10);
		} else if (nSeconds == 1200) {
			cbRefreshTime.setSelectedIndex(11);
		} else if (nSeconds == 1800) {
			cbRefreshTime.setSelectedIndex(12);
		}
		autoSelectRefresh = false;
	}

	/**
	 * Create the profiles choicebox.
	 */
	private JComboBox createProfilesChoiceBox() {

		cbProfiles = new JComboBox();
        cbProfiles.setOpaque(true);
		cbProfiles.setEditable(false);
		cbProfiles.setEnabled(true);
		cbProfiles.setMaximumRowCount(30);
		cbProfiles.setFont( new Font("Dialog", Font.PLAIN, 12 )); //$NON-NLS-1$

        updateProfilesChoiceBoxData(0);

		DefaultListCellRenderer comboRenderer = new DefaultListCellRenderer() {
			public Component getListCellRendererComponent(
   		     	JList list,
   		        Object value,
            	int modelIndex,
            	boolean isSelected,
            	boolean cellHasFocus)
            {
 		 		if (isSelected) {
					setBackground(list.getSelectionBackground());
					setForeground(list.getSelectionForeground());
				}
				else {
					setBackground(list.getBackground());
					setForeground(list.getForeground());
				}

				if (value instanceof ExternalConnection) {
					ExternalConnection connection = (ExternalConnection)value;
					setText("MySQL: "+(String)connection.getProfile()); //$NON-NLS-1$
				}
				else {
					setText((String)value);
				}

				return this;
			}
		};

		cbProfiles.setRenderer(comboRenderer);

		ActionListener choiceaction = new ActionListener() {
        	public void actionPerformed(ActionEvent e) {

            	Thread choiceThread = new Thread("UIToolBarData:createProfilesChoiceBox") { //$NON-NLS-1$
                	public void run() {

						boolean bRemoteServer = false;

						if (cbProfiles != null) {
							Object item = cbProfiles.getSelectedItem();
							if (item instanceof ExternalConnection) {
								ExternalConnection connection = (ExternalConnection)cbProfiles.getSelectedItem();

								if (!(connection.getServer()).equalsIgnoreCase("localhost")) { //$NON-NLS-1$
									bRemoteServer = true;
								}

								if (!autoSelect) {
									oParent.setMySQLDatabaseProfile(connection);
								}
							}
							else if (item instanceof String && !autoSelect) {
								oParent.setDerbyDatabaseProfile();
							}
						}

						if (bRemoteServer) {
							//pbTimedRefresh.setEnabled(true);
							//pbRefresh.setEnabled(true);
							//cbRefreshTime.setEnabled(true);
							//timePanel.setEnabled(true);
							setRefreshChoiceBoxSelection(FormatProperties.refreshTime);
						} else {
							if (oParent.oRefreshManager.isTimerRunning()) {
								pbTimedRefresh.setIcon(UIImages.get(RED_LIGHT_ICON));
								pbTimedRefresh.setText(LanguageProperties.getString(LanguageProperties.TOOLBARS_BUNDLE, "UIToolBarData.start")); //$NON-NLS-1$
								pbTimedRefresh.setToolTipText(LanguageProperties.getString(LanguageProperties.TOOLBARS_BUNDLE, "UIToolBarData.startRefresh")); //$NON-NLS-1$
								pbRefresh.setEnabled(true);
								oParent.oRefreshManager.stopTimer();

								FormatProperties.refreshTimerRunning = true;
								FormatProperties.setFormatProp(LanguageProperties.getString(LanguageProperties.TOOLBARS_BUNDLE, "UIToolBarData.timerRunning"), "true"); //$NON-NLS-1$ //$NON-NLS-2$
								FormatProperties.saveFormatProps();
							}
							pbTimedRefresh.setEnabled(false);
							pbRefresh.setEnabled(false);
							cbRefreshTime.setEnabled(false);
							timePanel.setEnabled(false);
						}

						autoSelect = false;
                	}
               	};
	            choiceThread.start();
        	}
		};
        cbProfiles.addActionListener(choiceaction);

		return cbProfiles;
	}

	/**
	 * Update the look and feel of the toolbar
	 */
	public void updateLAF() {

		pbDataAdmin.setIcon(UIImages.get(CONNECT_ICON));
		pbRefresh.setIcon(UIImages.get(REFRESH_CACHE_ICON));

		if (oParent.oRefreshManager.isTimerRunning()) {
			pbTimedRefresh.setIcon(UIImages.get(GREEN_LIGHT_ICON));
		}
		else {
			pbTimedRefresh.setIcon(UIImages.get(RED_LIGHT_ICON));
		}
		
		if (tbrToolBar != null) {
			SwingUtilities.updateComponentTreeUI(tbrToolBar);
		}
	}
	
	/** 
	 * Disable the Refresh button
	 */
	public void  disableRefresh() {
		pbRefresh.setEnabled(false);
	}
	
	/** 
	 * Enable the Refresh button
	 */	
	public void enableRefresh() {
		pbRefresh.setEnabled(true);
	}
	
	/**
	 * Update the data in the profiles choicebox.
	 */
	public synchronized void updateProfilesChoiceBoxData(int selectedIndex) {
		try {
			Vector profiles = ProjectCompendium.APP.adminDerbyDatabase.getMySQLConnections();
			profiles = CoreUtilities.sortList(profiles);
			profiles.insertElementAt((Object) new String("Derby: Default"), 0); //$NON-NLS-1$
			DefaultComboBoxModel comboModel = new DefaultComboBoxModel(profiles);
			cbProfiles.setModel(comboModel);

			if (cbProfiles.getSelectedIndex() != selectedIndex) {
				autoSelect = true;
				cbProfiles.setSelectedIndex(selectedIndex);
			}
		}
		catch(Exception ex) {
			//ProjectCompendium.APP.displayError("Exception: (UIToolBarData.updateProfileChoiceBoxData) " + ex.getMessage());
			String sMessage = LanguageProperties.getString(LanguageProperties.TOOLBARS_BUNDLE, "UIToolBarData.message1a") + //$NON-NLS-1$
					LanguageProperties.getString(LanguageProperties.TOOLBARS_BUNDLE, "UIToolBarData.message1b") + //$NON-NLS-1$
					LanguageProperties.getString(LanguageProperties.TOOLBARS_BUNDLE, "UIToolBarData.message1c") + //$NON-NLS-1$
					LanguageProperties.getString(LanguageProperties.TOOLBARS_BUNDLE, "UIToolBarData.message1d") + //$NON-NLS-1$
					LanguageProperties.getString(LanguageProperties.TOOLBARS_BUNDLE, "UIToolBarData.message1e");            					 //$NON-NLS-1$

			if (ProjectCompendium.isWindows) {
				sMessage = sMessage+LanguageProperties.getString(LanguageProperties.TOOLBARS_BUNDLE, "UIToolBarData.message2a") + //$NON-NLS-1$
				LanguageProperties.getString(LanguageProperties.TOOLBARS_BUNDLE, "UIToolBarData.message2b"); //$NON-NLS-1$
			}

			ProjectCompendium.APP.displayError(sMessage);			
			System.exit(0);
		}
	}

	/**
	 * Select the indicated database profile.
	 * An empty string means, select the Derby default option.
	 */
	public synchronized void selectProfile(String sName) {
		try {
			if (sName.equals("")) { //$NON-NLS-1$
				if (cbProfiles.getSelectedIndex() != 0) {
					autoSelect = true;
					cbProfiles.setSelectedIndex(0);
				}
			}
			else {
				int count = cbProfiles.getItemCount();
				for (int i=1; i<count;i++) {
					ExternalConnection con = (ExternalConnection)cbProfiles.getItemAt(i);

					if (con.getProfile().equals(sName)) {
						if (cbProfiles.getSelectedIndex() != i) {
							autoSelect = true;
							cbProfiles.setSelectedIndex(i);
						}
						break;
					}
				}
			}

		}
		catch(Exception ex) {
			ProjectCompendium.APP.displayError(LanguageProperties.getString(LanguageProperties.TOOLBARS_BUNDLE, "UIToolBarData.message3") + ex.getMessage()); //$NON-NLS-1$
		}
	}

	/**
	 * Handles most menu and toolbar action event for this application.
	 *
	 * @param evt the genereated action event to be handled.
	 */
	public void actionPerformed(ActionEvent evt) {

		oParent.setWaitCursor();

		Object source = evt.getSource();

		if (source.equals(pbDataAdmin)) {
			oParent.onFileDatabaseAdmin();
		}
		else if (source.equals(pbRefresh)) {
			pbRefresh.setEnabled(false);		// Turn it off to be friendly.  On a network this may be
			oParent.reloadProjectData();		// a very time-consuming operation.
			pbRefresh.setEnabled(true);
		}
		else if (source.equals(pbTimedRefresh)) {
			if (oParent.oRefreshManager.isTimerRunning()) {
				pbTimedRefresh.setIcon(UIImages.get(RED_LIGHT_ICON));
				pbTimedRefresh.setText(LanguageProperties.getString(LanguageProperties.TOOLBARS_BUNDLE, "UIToolBarData.start")); //$NON-NLS-1$
				pbTimedRefresh.setToolTipText(LanguageProperties.getString(LanguageProperties.TOOLBARS_BUNDLE, "UIToolBarData.startRefresh")); //$NON-NLS-1$
				oParent.oRefreshManager.stopTimer();
				// Need to resolve this conflict eventually.
				((UIToolBarManager)oManager).setDrawToolBarEnabled(true);				
				pbRefresh.setEnabled(true);
			}
			else {
				if (oParent.oRefreshManager.startTimer()) {
					// Need to resolve this conflict eventually.
					((UIToolBarManager)oManager).setDrawToolBarEnabled(false);
					pbRefresh.setEnabled(false);
					pbTimedRefresh.setIcon(UIImages.get(GREEN_LIGHT_ICON));
					pbTimedRefresh.setText(LanguageProperties.getString(LanguageProperties.TOOLBARS_BUNDLE, "UIToolBarData.stop")); //$NON-NLS-1$
					pbTimedRefresh.setToolTipText(LanguageProperties.getString(LanguageProperties.TOOLBARS_BUNDLE, "UIToolBarData.stopRefresh")); //$NON-NLS-1$
				}
			}
		}
		
		oParent.setDefaultCursor();
	}
	
	/**
	 * Updates the menu when a new database project is opened.
	 */
	public void onDatabaseOpen() {

		if (oParent.oCurrentMySQLConnection != null) {
			if (!(oParent.oCurrentMySQLConnection.getServer()).equalsIgnoreCase("localhost")) { //$NON-NLS-1$
				if (pbTimedRefresh != null) {
					pbTimedRefresh.setEnabled(true);
				}
				if (pbRefresh != null) {
					pbRefresh.setEnabled(true);
				}
				if (cbRefreshTime != null) {
					cbRefreshTime.setEnabled(true);
				}
				if (timePanel != null) {
					timePanel.setEnabled(true);
				}
			}
		} else {
			pbTimedRefresh.setEnabled(false);
			pbRefresh.setEnabled(false);
			cbRefreshTime.setEnabled(false);
			timePanel.setEnabled(false);			
		}
	}

	/**
	 * Updates the menu when the current database project is closed.
	 */
	public void onDatabaseClose() {

		if (pbTimedRefresh != null) {
			pbTimedRefresh.setEnabled(false);
		}
		if (pbRefresh != null) {
			pbRefresh.setEnabled(false);
		}
		if (cbRefreshTime != null) {
			cbRefreshTime.setEnabled(false);
		}
		if (timePanel != null) {
			timePanel.setEnabled(false);
		}
	}

	/**
 	 * Does Nothing
 	 * @param selected true to enable, false to disable.
	 */
	public void setNodeSelected(boolean selected) {}
		
	/**
 	 * Does Nothing
 	 * @param selected true to enable, false to disable.
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
