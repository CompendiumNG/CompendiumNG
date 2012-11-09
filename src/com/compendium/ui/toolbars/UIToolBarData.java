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

		tbrToolBar = new UIToolBar("Data Source Toolbar", UIToolBar.NORTHSOUTH);
		tbrToolBar.setOrientation(orientation);

		pbDataAdmin = tbrToolBar.createToolBarButton("Open Database Administration Dialog", UIImages.get(CONNECT_ICON));
		pbDataAdmin.addActionListener(this);
		pbDataAdmin.setEnabled(true);
		tbrToolBar.add(pbDataAdmin);
		CSH.setHelpIDString(pbDataAdmin,"toolbars.data");

		tbrToolBar.addSeparator();

		JLabel label = new JLabel("Data Connection: ");
		label.setFont( new Font("Dialog", Font.PLAIN, 12 ));
		tbrToolBar.add(label);

		tbrToolBar.add( createProfilesChoiceBox() );

		tbrToolBar.addSeparator();

		pbRefresh = tbrToolBar.createToolBarButton("Refresh Views Data Now", UIImages.get(REFRESH_CACHE_ICON));
		pbRefresh.addActionListener(this);
		pbRefresh.setEnabled(true);
		tbrToolBar.add(pbRefresh);
		CSH.setHelpIDString(pbRefresh,"toolbars.data");

		tbrToolBar.add( createRefreshChoiceBox() );

		pbTimedRefresh = tbrToolBar.createToolBarButton("Start Timed Data Refresh", UIImages.get(RED_LIGHT_ICON));
		pbTimedRefresh.setFont( new Font("Dialog", Font.PLAIN, 12 ));
		pbTimedRefresh.setText("Start");
		pbTimedRefresh.addActionListener(this);
		pbTimedRefresh.setEnabled(true);
		tbrToolBar.add(pbTimedRefresh);
		CSH.setHelpIDString(pbTimedRefresh,"toolbars.data");

		if (FormatProperties.refreshTimerRunning) {
			if (oParent.oRefreshManager.startTimer()) {
				pbRefresh.setEnabled(false);
				pbTimedRefresh.setIcon(UIImages.get(GREEN_LIGHT_ICON));
				pbTimedRefresh.setText("Stop");
				pbTimedRefresh.setToolTipText("Stop Timed Data Refresh");
			}
		}

		tbrToolBar.addSeparator();

		CSH.setHelpIDString(tbrToolBar,"toolbars.data");

		return tbrToolBar;
	}
	
	/**
	 * Create a choicbox for cache refresh timing options and return the panel it is in.
	 * @return JPanel, the panel holding the new choicebox for the cache refresh timing options.
	 */
	private JPanel createRefreshChoiceBox() {

		timePanel = new JPanel(new BorderLayout());
		CSH.setHelpIDString(timePanel,"toolbars.data");

		cbRefreshTime = new JComboBox();
		cbRefreshTime.setToolTipText("Set the duration between Data Refresh calls");
		cbRefreshTime.setOpaque(true);
		cbRefreshTime.setEditable(false);
		cbRefreshTime.setEnabled(false);
		cbRefreshTime.setMaximumRowCount(13);
		cbRefreshTime.setFont( new Font("Dialog", Font.PLAIN, 12 ));

		cbRefreshTime.addItem(new String("1 sec"));
		cbRefreshTime.addItem(new String("5 secs"));
		cbRefreshTime.addItem(new String("10 secs"));
		cbRefreshTime.addItem(new String("20 secs"));
		cbRefreshTime.addItem(new String("30 secs"));
		cbRefreshTime.addItem(new String("40 secs"));
		cbRefreshTime.addItem(new String("50 secs"));
		cbRefreshTime.addItem(new String("1 min"));
		cbRefreshTime.addItem(new String("5 mins"));
		cbRefreshTime.addItem(new String("10 mins"));
		cbRefreshTime.addItem(new String("15 mins"));
		cbRefreshTime.addItem(new String("20 mins"));
		cbRefreshTime.addItem(new String("30 mins"));

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

		timePanel.add(new JLabel(" "), BorderLayout.WEST);
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
			FormatProperties.setFormatProp("refreshTime", String.valueOf(nSeconds));
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
		cbProfiles.setFont( new Font("Dialog", Font.PLAIN, 12 ));

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
					setText("MySQL: "+(String)connection.getProfile());
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

            	Thread choiceThread = new Thread("UIToolBarData:createProfilesChoiceBox") {
                	public void run() {

						boolean bRemoteServer = false;

						if (cbProfiles != null) {
							Object item = cbProfiles.getSelectedItem();
							if (item instanceof ExternalConnection) {
								ExternalConnection connection = (ExternalConnection)cbProfiles.getSelectedItem();

								if (!(connection.getServer()).equalsIgnoreCase("localhost")) {
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
								pbTimedRefresh.setText("Start");
								pbTimedRefresh.setToolTipText("Start Timed Data Refresh");
								pbRefresh.setEnabled(true);
								oParent.oRefreshManager.stopTimer();

								FormatProperties.refreshTimerRunning = true;
								FormatProperties.setFormatProp("timerRunning", "true");
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
	 * Update the data in the profiles choicebox.
	 */
	public synchronized void updateProfilesChoiceBoxData(int selectedIndex) {
		try {
			Vector profiles = ProjectCompendium.APP.adminDerbyDatabase.getMySQLConnections();
			profiles = CoreUtilities.sortList(profiles);
			profiles.insertElementAt((Object) new String("Derby: Default"), 0);
			DefaultComboBoxModel comboModel = new DefaultComboBoxModel(profiles);
			cbProfiles.setModel(comboModel);

			if (cbProfiles.getSelectedIndex() != selectedIndex) {
				autoSelect = true;
				cbProfiles.setSelectedIndex(selectedIndex);
			}
		}
		catch(Exception ex) {
			//ProjectCompendium.APP.displayError("Exception: (UIToolBarData.updateProfileChoiceBoxData) " + ex.getMessage());
			String sMessage = "\nThere has been a problem accessing the Compendium Derby database.\n" +
					"COMPENDIUM  MAY BE  RUNNING  ALREADY!\n\n" +
					"If it is not, then there could be a process still running from an\n" +
					"earlier instance of Compendium which did not terminate cleanly.\n" +
					"Please terminate all previous Compendium processes and try again.\n";            					

			if (ProjectCompendium.isWindows) {
				sMessage = sMessage+"\n\t(Ctrl+Alt+Delete -> Task Manager -> Processes Tab ->\n" +
				"\tSelect javaw.exe process -> Press 'End Process')\n\n";
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
			if (sName.equals("")) {
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
			ProjectCompendium.APP.displayError("Exception: (UIToolbarManager.selectProfile) " + ex.getMessage());
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
			oParent.reloadProjectData();
		}
		else if (source.equals(pbTimedRefresh)) {
			if (oParent.oRefreshManager.isTimerRunning()) {
				pbTimedRefresh.setIcon(UIImages.get(RED_LIGHT_ICON));
				pbTimedRefresh.setText("Start");
				pbTimedRefresh.setToolTipText("Start Timed Data Refresh");
				oParent.oRefreshManager.stopTimer();
				pbRefresh.setEnabled(true);
			}
			else {
				if (oParent.oRefreshManager.startTimer()) {
					pbRefresh.setEnabled(false);
					pbTimedRefresh.setIcon(UIImages.get(GREEN_LIGHT_ICON));
					pbTimedRefresh.setText("Stop");
					pbTimedRefresh.setToolTipText("Stop Timed Data Refresh");
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
			if (!(oParent.oCurrentMySQLConnection.getServer()).equalsIgnoreCase("localhost")) {
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

				/*if (FormatProperties.refreshTimerRunning) {
					if (oParent.oRefreshManager.startTimer()) {
						pbRefresh.setEnabled(false);
						pbTimedRefresh.setIcon(UIImages.get(GREEN_LIGHT_ICON));
						pbTimedRefresh.setText("Stop");
						pbTimedRefresh.setToolTipText("Stop Timed Data Refresh");
					}
				}*/
			}
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
