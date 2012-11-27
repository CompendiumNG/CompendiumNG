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

import java.awt.*;
import java.awt.print.*;
import java.awt.event.*;
import java.awt.datatransfer.*;
import java.awt.image.*;
import java.io.*;
import java.util.*;
import java.sql.SQLException;
import java.net.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.nio.channels.FileLock;

import javax.print.attribute.*;
import javax.print.attribute.standard.*;

import javax.help.*;

import javax.swing.*;
import javax.swing.undo.*;
import javax.swing.tree.DefaultMutableTreeNode;

import javax.imageio.*;
import javax.imageio.stream.*;
import com.sun.image.codec.jpeg.*;

import org.jabber.jabberbeans.util.*;

import com.compendium.core.datamodel.*;
import com.compendium.core.datamodel.services.*;
import com.compendium.core.db.*;
import com.compendium.core.db.management.*;
import com.compendium.core.*;

import com.compendium.*;

import com.compendium.meeting.*;

import com.compendium.ui.edits.*;
import com.compendium.ui.plaf.*;
import com.compendium.ui.dialogs.*;
import com.compendium.ui.tags.UITagTreePanel;
import com.compendium.ui.toolbars.*;
import com.compendium.ui.menus.*;
import com.compendium.ui.movie.UIMovieMapViewFrame;
import com.compendium.ui.stencils.*;
import com.compendium.ui.linkgroups.*;

import com.compendium.io.html.HTMLOutline;
import com.compendium.io.html.HTMLViews;
import com.compendium.io.http.HttpFileDownloadInputStream;
import com.compendium.io.jabber.*;
import com.compendium.io.xml.XMLExportNoThread;



/**
 * This is the main JFrame for the application and holds many central application variables and methods.
 *
 * @author	sajid / Michelle Bachler / Lakshmi Prabhakaran
 */
public class ProjectCompendiumFrame	extends JFrame
									implements KeyListener, IUIConstants, ICoreConstants {

    /** Computed serial version ID */
	private static final long serialVersionUID 			= 5065491272948039358L;

	/** The file used to tell if Compendium is already running */
    private static final String RUNNING_FILE 			=
			                            System.getProperty("user.home") + //$NON-NLS-1$
			                            System.getProperty("file.separator") + //$NON-NLS-1$
			                            ".compendium_running"; //$NON-NLS-1$

	/** The layer to add view frames to in the desktop.*/
	private static final Integer VIEWLAYER 				= JLayeredPane.DEFAULT_LAYER; //new Integer(5);

	/** The offset to use when cascading frames.*/
	private static final int INTERNALFRAMEOFFSET		= 24;

	/** The internal frame width when cascading,*/
	private static final int INTERNALFRAMEWIDTH			= 300;

	/** The internal frame height when cascading.*/
	private static final int INTERNALFRAMEHEIGHT		= 300;

	/** The default width to open a frame.*/
	private static final int FRWIDTH					= 500;

	/** The default height to open a frame.*/
	private static final int FRHEIGHT					= 500;

	/** The default offset to open a frame.*/
	private static final int FROFFSET					= 24;


	// PUBLIC MANAGERS
	/** The manager for the stencils.*/
	public UIStencilManager		oStencilManager			= null;

	/** The manager for the link groups.*/
	public UILinkGroupManager   oLinkGroupManager		= null;

	/** The manager for the link groups.*/
	public UIRefreshManager   	oRefreshManager			= null;

	/** The service manager used by this frame to access Derby database services.*/
	public IServiceManager		oDerbyServiceManager	= null;

	/** The service manager used by this frame to access database services.*/
	public IServiceManager		oServiceManager			= null;

	/** Holds the information needed / manages Meeting Recording and Meeting Replay.*/
	public MeetingManager		oMeetingManager			= null;
		
	/** Holds the currently being used MySQL connection profile details.*/
	public ExternalConnection oCurrentMySQLConnection 	= null;

	/** The platform specific shortcut key.*/
	public 	int 				shortcutKey;

	/** _x & _y last know position of mouse, updated by List and ViewPane UIs.*/
	public	int					_x, _y;

	/** The table for checking nodes in a paste operation.*/
	public Hashtable 			ht_pasteCheck			= new Hashtable(51);

	/** The user specified name for the currently open database.*/
	public String				sFriendlyName			= ""; //$NON-NLS-1$

	/** The top node for the code group tree.*/
	public DefaultMutableTreeNode codeGroupNode 		= null;

	/** The database administration instance used to manage default locale Derby database.*/
	public DBAdminDerbyDatabase adminDerbyDatabase  	= null;

	/** The database administration instance used to manage the current database.*/
	public DBAdminDatabase 		adminDatabase 			= null;
	
	/** The main split pane object.*/
	public JSplitPane 			oSplitter				= null;

	/** The main tabbed pane*/
	public JTabbedPane			oTabbedPane				= null;
	
	/** The current database name.*/
	//public static String sCurrentDatabase = "";

	/** The View associated with the home view for the currently open database.*/
	private View				oHomeView				= null;

	/** The current login name for the current user in the open database.*/
	private String				sUserName				= ""; //$NON-NLS-1$

	/** The current password for the current user in the open database.*/
	private String				sUserPassword			= ""; //$NON-NLS-1$

	/** The cache model for the currently open database.*/
	private IModel				oModel					= null;

	/** A reference to the trashbin node.*/
	private NodeSummary			oTrashbinNode			= null;

	/** A reference to the inbox node.*/
	private NodeSummary			oInboxNode				= null;
	
	/** A List of the View Frames that have been opened during this database session.*/
	private Vector 				viewFrameList 			= new Vector();


	/** The comma separated list of Access projects.*/
	//private String			sAccessProjects			= "";

	/** The list of current database project names.*/
	private Vector				vtProjects				= null;

	/** The manager for the menubar.*/
	private UIMenuManager		oMenuManager			= null;

	/** The manager for the tool bar.*/
	private UIToolBarManager	oToolBarManager			= null;

	/** True if a new delete operation has been started.*/
	private boolean				isNewDelete				= false;

	/** The content pane for this frame.*/
	private Container			oContentPane			= null;

	/** The main panel for this frame.*/
	private JPanel				oMainPanel				= null;

	/** The inner panel for this frame.*/
	private JPanel				oInnerPanel				= null;
	
	/** The weclome screen */
	private JLayeredPane		oWelcomePanel			= null;

	/** The parent class to this class.*/
	private ProjectCompendium	oParent					= null;

	/** The screen width when opening this frame.*/
	private int					nScreenWidth			= 0;

	/** The screen height when opening this frame.*/
	private int					nScreenHeight			= 0;

	/** The main menu bar for this frame.*/
	private JMenuBar			mbMenuBar				= null;

	/** The desktop pane for this frame.*/
	private JDesktopPane		oDesktop				= null;

	/** The status bar for this frame.*/
	private UIStatusBar			oStatusBar				= null;

	/** The view history bar for this frame.*/
	private UIViewHistoryBar	oViewHistoryBar			= null;

	/** The dialog for opening and logging in to a database project.*/
	private UILogonDialog 		oLogonDialog			= null;

	/** Indicates whether to proceed with a login.*/
	private boolean				bProceed				= false;

	/** Indicates whether user has been notified of dirty views */
	private static boolean				bDirtyViewNotified = false;
	
	/** Semaphore to prevent simultaneous timed/manual refresh operation */
	private static boolean				bReloadingProject = false;
	
	/** Semaphore to prevent overlapping timed refresh operations */
	private static boolean				bChecking = false;
	
	/** The hostname for this machine.*/
	private String				sServerName				= ""; //$NON-NLS-1$

	/** The ip address for this machine.*/
	private String				sServerIP				= ""; //$NON-NLS-1$

	/** The clipboard for this application.*/
	private Clipboard			oClipboard				= null;

	/** The class that controls the audio part of the application*/
	private UIAudio				audioThread				= null;

	/** Holds the properties saved for user import options.*/
	private ImportProfile		oImportProfile			= null;

	/** A reference to the Questmap import dialog.*/
	private UIImportDialog		dlgImport				= null;

	/** A reference to the HTML Outline export dialog.*/
	private UIExportDialog		dlgExport				= null;

	/** A reference to the XML import dialog.*/
	private UIImportXMLDialog	dlgImportXML			= null;

	/** A reference to the XML export dialog.*/
	private UIExportXMLDialog	dlgExportXML			= null;
	
	private UIMarkProjectSeenDialog	dlgMarkProjectSeen	= null;

	/** A reference to the HTML Views export dialog.*/
	private UIExportViewDialog 	dialog2 				= null;

	/** A reference to the Aerial view dialog for the current View.*/
	private UIAerialDialog		oAerialViewDialog		= null;

	/** A reference to the About dialog.*/
	private	UIAboutDialog		oAboutDialog			= null;


	//PROPERTIES
	/** Node label font currently being used.*/
	public static Font 			currentDefaultFont 		= new Font("Dialog", Font.PLAIN, 12); //$NON-NLS-1$

	/** A reference to the windows look and feel string.*/
    private static String 		windowsClassName 		= "com.sun.java.swing.plaf.windows.WindowsLookAndFeel"; //$NON-NLS-1$

	// CLAIMAKER
	/** The url for the ClaiMaker server search.*/
	private static String 		claiMakerServer 		= ""; //$NON-NLS-1$

	/** Whether the CaliMaker url has been set or not.*/
	private static boolean 		claiMakerConnected 		= false;

	// JABBER
	/** A reference to the Jabber client.*/
	public static Jabber 		jabber 					= null;

	// IXPANELS
	/** A reference to the Jabber client for IX Panel connections.*/
	public static IXPanel 		ixPanel 				= null;

	// HELP
	/** The name of the helpset to load for the help.*/
    private static final String helpsetName 			= "CompendiumHelp"; //$NON-NLS-1$

	/** A reference to the HelpSet for this application.*/
    public HelpSet 				mainHS 					= null;

	/** A reference to the HelpBroker for this application.*/
    public HelpBroker 			mainHB;

	/** For tracking external opy/paste operations.*/
	private boolean 			externalCopy 			= false;

	/** The currently active tag group.*/
	private String 				activeGroup 			= ""; //$NON-NLS-1$

	/** The currently active link group.*/
	private String 				activeLinkGroup 		= "1"; //This is the id of the default link group //$NON-NLS-1$

	/** A reference to the start up dialog.*/
	private UIStartUp 			startUpDlg 				= null;

	/**
	 * The property file holding the applications to launch reference.
	 * For use with for Mac and Linux platforms.
	 */
	private Properties		launchApplications 			= null;

	/** True if this process created the running file */
    private boolean 		createdRunningFile 			= false;

	/** The name of the project in use   */
	private String 			sProject					= ""; //$NON-NLS-1$
	
	/** Is Paste Enabled? */
	public boolean 			isPasteEnabled				= false;
	
	/** The UIViewOutline object to display outline view */
	public UIViewOutline 		outlineView  			= null;

	/** Holds information for the depth check.*/
	private Hashtable		htCheckDepth 		= null;

	/** Holds child data when calculating export views data.*/
	private Hashtable		htChildrenAdded 	= null;

	/**
	 * Constructor, creates a new ProjectCompendiumFrame instance.
	 * @param parent, the parent class to this frame.
	 * @param title, the title for this frame.
	 * @param serverName, the host name for this machine.
	 * @param IP, the ip address of this machine.
	 * @param dlg, a refernce to the start up dialog.
	 */
	public ProjectCompendiumFrame(ProjectCompendium parent, String title, String serverName, String IP, UIStartUp dlg) {

		super(title);

		this.startUpDlg = dlg;
		this.oParent = parent;
		this.sServerName = serverName;
		this.sServerIP = IP;
		
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent evt) {
				onExit();
			}
		});

		// LOAD ANY LAUNCH APPLICATION PROPERTIES REQURIED BY MAC AND LINUX
		File file = new File("System"+ProjectCompendium.sFS+"resources"+ProjectCompendium.sFS+"LaunchApplications.properties"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		launchApplications = new Properties();
		if (file.exists()) {
			try {
				launchApplications.load(new FileInputStream("System"+ProjectCompendium.sFS+"resources"+ProjectCompendium.sFS+"LaunchApplications.properties")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			}
			catch (IOException e) {}
		}

		// SET DERBY DATABASE LOCATION
		File file2 = new File(SystemProperties.defaultDatabaseLocation); 
		Properties p = System.getProperties();
		p.put("derby.system.home", file2.getAbsolutePath()); //$NON-NLS-1$
		if (ProjectCompendium.isMac) {
			setMacMenuBar(FormatProperties.macMenuBar);
			p.put("derby.storage.fileSyncTransactionLog", "true"); //$NON-NLS-1$ //$NON-NLS-2$
		}

		// SET PROXY
		setProxy();
	}

	/**
	 * Set the proxy for Compendium to use for HTTP connections.
	 */
	public void setProxy() {

		File optionsFile = new File(UISystemSettingsDialog.SETUPFILE);
		Properties oConnectionProperties = new Properties();
		String sLocalProxyHost = ""; //$NON-NLS-1$
		String sLocalProxyPort = ""; //$NON-NLS-1$
		boolean bSuccessful = false;
		if (optionsFile.exists()) {
			try {
				oConnectionProperties.load(new FileInputStream(UISystemSettingsDialog.SETUPFILE));
				String value = oConnectionProperties.getProperty("localproxyhost"); //$NON-NLS-1$
				if (value != null) {
					sLocalProxyHost = value;
				}
				value = oConnectionProperties.getProperty("localproxyport"); //$NON-NLS-1$
				if (value != null) {
					sLocalProxyPort = value;
				}
				bSuccessful = true;
			} catch (IOException e) {
				System.out.println("Problems accessing system settings: "+e.getMessage()); //$NON-NLS-1$
			}
		}

		try {
			if (sLocalProxyHost == null || sLocalProxyHost.equals("") || //$NON-NLS-1$
					sLocalProxyPort == null || sLocalProxyPort.equals("") || !bSuccessful) { //$NON-NLS-1$

				// THIS CODE PULLED THE PROXY OUT, BUT THEN KILLED THE CODE FURTHER ON
				// POSSIBLY IN RELATION TO MYSQL - INVESTIGATE FURTHER WHAT THIS PROPERTY DOES
				/*System.setProperty("java.net.useSystemProxies","true");

				java.util.List proxies = ProxySelector.getDefault().select(new URI("http://www.google.com/"));
				if (proxies.size() > 0) {

					Proxy proxy = (Proxy)proxies.get(0);
					InetSocketAddress proxyAddress = (InetSocketAddress)proxy.address();
					sLocalProxyHost = proxyAddress.getHostName();
					int nPort = proxyAddress.getPort();
					sLocalProxyPort = (new Integer(nPort)).toString();
					if (sLocalProxyHost != null && !sLocalProxyHost.equals("")) {
						if (optionsFile.exists()) {
							if (oConnectionProperties.isEmpty()) {
								oConnectionProperties.load(new FileInputStream(UISystemSettingsDialog.SETUPFILE));
							}
						}
						oConnectionProperties.put("localproxyhost", sLocalProxyHost);
						oConnectionProperties.put("localproxyport", sLocalProxyPort);
						oConnectionProperties.store(new FileOutputStream(UISystemSettingsDialog.SETUPFILE), "Access Grid Details");

						//System.setProperty("proxySet", "true");
						//System.setProperty("http.proxyHost", sLocalProxyHost);
						//System.setProperty("http.proxyPort", sLocalProxyPort);
					}
				}*/
			}
			else {
				System.setProperty("proxySet", "true"); //$NON-NLS-1$ //$NON-NLS-2$
				System.setProperty("http.proxyHost", sLocalProxyHost); //$NON-NLS-1$
				System.setProperty("http.proxyPort", sLocalProxyPort); //$NON-NLS-1$
			}

		} catch (Exception e) {
			System.out.println("Problems setting proxy due to: "+e.getMessage()); //$NON-NLS-1$
		}
	}

	/**
	 * Moves the menu bar from the top of the Application to the top of the screen, and back again.
	 */
	public void setMacMenuBar(boolean up) {

		//if (up)
		//	System.setProperty("apple.laf.useScreenMenuBar", "true");
		//else
		//	System.setProperty("apple.laf.useScreenMenuBar", "false");
	}

	/**
	 * Return the Properties class holding external file launch application data.
	 * (for Mac and Linux platforms).
	 * @return Properties, the Properties class holding external file launch application data
	 */
	public Properties getLaunchApplications() {
		return launchApplications;
	}

	/**
	 * Draw frame contents and initialises data.
	 */
	public boolean initialiseFrame() {

        /*try {
            File runningFile = new File(RUNNING_FILE);
            if (runningFile.exists()) {
                FileInputStream input = new FileInputStream(runningFile);
                FileLock lock =
                    input.getChannel().lock(0, runningFile.length(), true);
                BufferedReader reader =
                    new BufferedReader(new InputStreamReader(input));
                Vector instances = new Vector();
                String line = reader.readLine();
                while (line != null) {
                    instances.add(line);
                    line = reader.readLine();
                }
                lock.release();
                reader.close();
                input.close();
                if (instances.contains(ProjectCompendium.sHOMEPATH)) {
                    if (JOptionPane.showConfirmDialog(this,
                            "There appears to already be an instance of " +
                            "Compendium running.\nThis message could be " +
                            "appearing because an earlier instance of " +
                            "Compendium did not terminate cleanly.\n" +
                            "Would you like to try to start" +
                            " another instance?",
                            "Confirm Compendium Start",
                            JOptionPane.YES_NO_OPTION)
                            != JOptionPane.YES_OPTION) {
                        System.err.println("Quitting");
                        System.exit(0);
                    }
                }
            }
            FileOutputStream output = new FileOutputStream(RUNNING_FILE,
                                                                  true);
            FileLock lock =
                output.getChannel().lock(0, runningFile.length(), false);
            PrintWriter writer = new PrintWriter(output);
            writer.println(ProjectCompendium.sHOMEPATH);
            lock.release();
            writer.close();
            output.close();
            createdRunningFile = true;
        } catch (Exception e) {
            e.printStackTrace();
        }*/

		// HELP
		try {
		    String helpfile = "System"+ProjectCompendium.sFS+"resources"+ProjectCompendium.sFS+"Help"+ProjectCompendium.sFS+"CompendiumHelp.hs"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$

			File file = new File(helpfile);
			if (file.exists()) {
				URL url = file.toURL();
		     	mainHS = new HelpSet(null, url);
		    	mainHB = mainHS.createHelpBroker();
				mainHB.enableHelpKey(ProjectCompendium.APP.getRootPane(), "top", null); //$NON-NLS-1$
			}
			else {
				System.out.println("Can't find help file = "+helpfile); //$NON-NLS-1$
			}
		}
		catch (Exception ee) {
		    ee.printStackTrace();
		    System.out.println ("Help Set "+helpsetName+" not found \n\n"+ee.getMessage()); //$NON-NLS-1$ //$NON-NLS-2$
		}

		// In case things get in a muddle. SimpleInterface mode has to be in Derby Database.
		/*if ( (FormatProperties.simpleInterface && FormatProperties.nDatabaseType == ICoreConstants.MYSQL_DATABASE)
			|| (FormatProperties.simpleInterface 
					&& !FormatProperties.defaultDatabase.equals(SystemProperties.defaultProjectName))
		) {			
			FormatProperties.nDatabaseType = ICoreConstants.DERBY_DATABASE;
			FormatProperties.setFormatProp("database", "derby");
			FormatProperties.defaultDatabase = SystemProperties.defaultProjectName;
			FormatProperties.setFormatProp("defaultdatabase", SystemProperties.defaultProjectName);			
			FormatProperties.saveFormatProps();
		}*/

		if (!init()) {
			onExit();
		}

		pack();

		if (FormatProperties.lastScreenWidth == -1 && FormatProperties.lastScreenHeight == -1) {

			//determins the size of user screen in pixels
			Toolkit tk = this.getToolkit();
			Dimension screensize = tk.getScreenSize();
			nScreenWidth = screensize.width;
			nScreenHeight = screensize.height;

			int appWidth = (new Double(nScreenWidth*0.90)).intValue();
			int appHeight = (new Double(nScreenHeight*0.90)).intValue();
			int appLocHeight = nScreenHeight/2 -(new Double(appHeight*0.55)).intValue();
			int appLocWidth = nScreenWidth/2- appWidth/2;

			if (ProjectCompendium.isMac) {
				setSize(appWidth, appHeight);
				setLocation(0,0);
			}
			else {
				setSize(nScreenWidth, nScreenHeight);
				setLocation(appLocWidth, appLocHeight);
			}
		}
		else {
			nScreenWidth = FormatProperties.lastScreenWidth;
			nScreenHeight = FormatProperties.lastScreenHeight;

			setSize(nScreenWidth, nScreenHeight);
			setLocation(FormatProperties.lastScreenX, FormatProperties.lastScreenY);
		}

		if (!ProjectCompendium.isMac) {
			ImageIcon imageicon = UIImages.get(IUIConstants.PC_ICON);
			if (imageicon != null)
				setIconImage(imageicon.getImage());
		}
		
		try {
			UIReferenceNodeManager.loadReferenceNodeTypes();	
		} catch (Exception e) {
			System.out.println("Exception: "+e.getMessage()); //$NON-NLS-1$
		}		
		
		return true;
	}

	/**
	 * Initialize and draw the main rame contents
	 */
	public boolean init() {

		startUpDlg.setMessage(LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "ProjectCompendiumFrame.openingCompendium")); //$NON-NLS-1$

		shortcutKey = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();

		//setWaitCursor();

		initLAF();

		startUpDlg.setMessage(LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "ProjectCompendiumFrame.checkAdminDatabase")); //$NON-NLS-1$

		if (!connectToServices())
			return false;

		startUpDlg.setMessage(LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "ProjectCompendiumFrame.checkDeleteFiles")); //$NON-NLS-1$
		try {
			CoreUtilities.checkFilesToDeleted();
		} catch (SecurityException ex) {
			System.out.println("Exception deleting due to:\n"+ex.getMessage()); //$NON-NLS-1$
		}		

		//setDefaultCursor();

		oContentPane = getRootPane().getContentPane();
		oContentPane.setBackground(Color.white);
		oContentPane.setLayout(new BorderLayout());
		oMainPanel = new JPanel(new BorderLayout());
		oInnerPanel = new JPanel(new BorderLayout());

		oContentPane.add(oMainPanel, BorderLayout.CENTER);

		File file = new File(SystemProperties.bannerImage);
		if (file.exists()) {	
			try {
				JPanel panel = new JPanel(new BorderLayout());
				ImageIcon icon = new ImageIcon(SystemProperties.bannerImage);
				JLabel label = new JLabel(icon, SwingConstants.LEFT);
				JScrollPane scroll = new JScrollPane(label, JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
				panel.add(scroll, BorderLayout.CENTER);
				oContentPane.add(panel, BorderLayout.NORTH);
			} catch (Exception ie) {
				ie.printStackTrace();
			}
		}
		
		// create audio thread.
		// this must be done before createMenuBar() which uses it
		audioThread = new UIAudio();
		audioThread.setAudio( FormatProperties.audioOn );

		// CREATE BEFORE TOOLBAR MANAGER AS IT NEEDS IT
		oRefreshManager = new UIRefreshManager();

		// CREATE BEFORE MENU MANAGER AS IT NEEDS IT
		startUpDlg.setMessage(LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "ProjectCompendiumFrame.loadingStencils")); //$NON-NLS-1$

		oStencilManager = new UIStencilManager(this, mainHS, mainHB);
		oStencilManager.loadStencils();
		oStencilManager.getTabbedPane().addKeyListener(this);

		oTabbedPane = new JTabbedPane();
		
		//oInnerPanel.add(oTabbedPane, BorderLayout.WEST);
		//oInnerPanel.add(oStencilManager.getTabbedPane(), BorderLayout.WEST);

		// CREATE BEFORE MENU MANAGER AS IT NEEDS IT
		startUpDlg.setMessage(LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "ProjectCompendiumFrame.loadingLinkGroups")); //$NON-NLS-1$
		oLinkGroupManager = new UILinkGroupManager(this, mainHS, mainHB);
		oLinkGroupManager.loadLinkGroups();

		// create and initialize the status bar
		// MUST BE BEFORE MEUS
		startUpDlg.setMessage(LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "ProjectCompendiumFrame.createStatusBar"));		 //$NON-NLS-1$
		createStatusBar();

		// create and initialize the view history bar
		// MUST BE BEFORE MENUS
		startUpDlg.setMessage(LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "ProjectCompendiumFrame.createHistoryBar"));		 //$NON-NLS-1$
		createViewHistoryBar();

		// create and initialize the menu bar
		startUpDlg.setMessage(LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "ProjectCompendiumFrame.createMenus")); //$NON-NLS-1$
		oMenuManager = new UIMenuManager(mainHS, mainHB, FormatProperties.simpleInterface);
		try {
			mbMenuBar = oMenuManager.createMenuBar();
		} catch(Exception e) {
			e.printStackTrace();
			System.out.flush();
		}

		mbMenuBar.setBorder(null); // to remove gap under bar above banner	
		setJMenuBar(mbMenuBar);
		oMenuManager.onDatabaseClose();

		startUpDlg.setMessage(LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "ProjectCompendiumFrame.createToolbars")); //$NON-NLS-1$
		oToolBarManager = new UIToolBarManager(this, mainHB, FormatProperties.simpleInterface);
		oToolBarManager.createToolbars();
		oToolBarManager.onDatabaseClose();

		onImageRollover(FormatProperties.imageRollover);

		// create and initialize the desktop
		createDesktop();

		// check for default Stencils set and load if found
		if (!SystemProperties.defaultStencilSetName.equals("")) { //$NON-NLS-1$
			this.oStencilManager.openStencilSet(SystemProperties.defaultStencilSetName);			
		}
				
		//create the clipboard
		createClipboard();

		// install listeners for View keycode capture on menu bar
		mbMenuBar.addKeyListener(this);

		//create a default import dialog for managing import profiles
		oImportProfile = new ImportProfile();

		// NOT SURE THIS IS MAKING ANY DIFFERENCE - POSS' TAKE OUT? - MB
		addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent evt) {
				oStatusBar.repaint();
				oContentPane.validate();
			}
		});

		updateProjects();
		return true;
	}

	/**
	 * Set the title of the main application for derby database mode.
	 * @param sProject, the name of the project to display.
	 */
	public void setDerbyTitle(String sProject) {
		setTitle(ICoreConstants.DERBY_DATABASE, "Localhost", "Default", sProject); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * Set the title of the main application.
	 * @param nType, the type of data source to set the title for.
	 * @param sAddress, the address of the datasource.
	 * @param sProfile, the name of the database profile to display.
	 * @param sProject, the name of the project to display.
	 */
	public void setTitle(int nType, String sAddress, String sProfile, String sProject) {
		String sTitle = SystemProperties.applicationName;

		if (FormatProperties.displayFullPath) {
			if (!sAddress.equals("")) { //$NON-NLS-1$
				if (nType == ICoreConstants.MYSQL_DATABASE) {
					sTitle += ": MySQL "+ProjectCompendium.sFS+" "+sAddress+" "+ProjectCompendium.sFS+" "+sProfile+" "+ProjectCompendium.sFS+" "+sProject; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
				}
				else {
					sTitle += ": Derby "+ProjectCompendium.sFS+" "+sAddress+" "+ProjectCompendium.sFS+" "+sProfile+" "+ProjectCompendium.sFS+" "+sProject; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
				}
			}
		}
		else {
			if (nType == ICoreConstants.MYSQL_DATABASE) {
				if (!sProfile.equals("")) { //$NON-NLS-1$
					sTitle += ": "+sProject+" [ "+sProfile+" ] "; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				}
			}
			else {
				if (!sProject.equals("")) { //$NON-NLS-1$
					sTitle += ": "+sProject; //$NON-NLS-1$
				}
			}
		}

		setTitle(sTitle);

		//Lakshmi - set the name of the project in use.
		this.sProject = sProject;
	}


    /**
     * Gets the name of the project in use
     * @return name of the project in use.
     */
	/*
	 * @author Lakshmi
	 * @date 30/1/06
	 */
	public String getProjectName() {
		return sProject;

	}

	/**
	 * Initialize the look and feel.
	 */
	public void initLAF() {

		// If nothing set, leave as system default.
		if (FormatProperties.currentLookAndFeel == null || FormatProperties.currentLookAndFeel.equals("")) //$NON-NLS-1$
		    return;

		try {
			UIManager.setLookAndFeel(FormatProperties.currentLookAndFeel);

			//added this to specifically set the scroll bar color - the scroll
			// bar was not always apparent - prob due to a swing bug in the way
			// the windows class sets the scroll bar color. - bz - 5/8/00
			if (FormatProperties.currentLookAndFeel.equals(windowsClassName))
				UIManager.put("ScrollBar.track", new Color(224, 224, 224)); //$NON-NLS-1$

			// IF THERE IS A MENUBAR THEN THIS HAS NOT BEEN CALLED FROM INIT BUT FROM A LAF CHANGE OPTION
			// DO A CONTROLLED UPDATE TO PREVENT NODE DUPLICATION
			if (mbMenuBar != null) {

				SwingUtilities.updateComponentTreeUI(mbMenuBar);
				SwingUtilities.updateComponentTreeUI(oStatusBar);
				SwingUtilities.updateComponentTreeUI(oViewHistoryBar);
				SwingUtilities.updateComponentTreeUI(oSplitter);
				SwingUtilities.updateComponentTreeUI(oTabbedPane);
				SwingUtilities.updateComponentTreeUI(oDesktop);

				oToolBarManager.updateLAF();				
				oStencilManager.updateLAF();
				oMenuManager.updateLAF();				
				
				UIViewFrame viewFrame = null;
				JInternalFrame[] frames = oDesktop.getAllFrames();
				for (int i=0; i < frames.length; i++ ) {
					viewFrame = (UIViewFrame)frames[i];
					viewFrame.updateUI();
					viewFrame.getScrollPane().getHorizontalScrollBar().updateUI();
					viewFrame.getScrollPane().getVerticalScrollBar().updateUI();

					if (viewFrame instanceof UIListViewFrame) {
						JTable table = ((UIListViewFrame)viewFrame).getUIList().getList();
						SwingUtilities.updateComponentTreeUI(table);
					}
					viewFrame.repaint();
					viewFrame.validate();
				}
			}
			else {
				SwingUtilities.updateComponentTreeUI(ProjectCompendiumFrame.this);
			}
		}
		catch (Exception ex) {
			ex.printStackTrace();
			System.err.println ("Could not swap LookAndFeel: " + FormatProperties.currentLookAndFeel); //$NON-NLS-1$
		}
	}

	/**
	 * Set up the Service manager for the Derby Database and the current database.
	 * Check the admin database(s) exists and finally load the current database's projects.
	 * return boolean, true if all went successfully, else false;
	 */
	private boolean connectToServices() {

		try {
			oDerbyServiceManager = new ServiceManager(ICoreConstants.DERBY_DATABASE);
			oServiceManager = oDerbyServiceManager;
			adminDerbyDatabase = new DBAdminDerbyDatabase(oDerbyServiceManager);
			adminDatabase = adminDerbyDatabase;
		}
		catch (Exception ex1) {
			System.out.println(ex1.getLocalizedMessage());
			ex1.printStackTrace();
			System.out.flush();
			displayError("Error creating Derby ServiceManager...\n" + ex1.getLocalizedMessage()); //$NON-NLS-1$
			return false;
		}

		// CHECK THAT COMPENDIUM ADMIN DATABASE EXISTS, IF NOT CREATE
		try {
			if (adminDerbyDatabase.firstTime()) {
				// Set the interface mode initially to that requested in the System.ini file
				FormatProperties.simpleInterface = SystemProperties.simpleInterface;
				FormatProperties.setFormatProp("simpleInterface", String.valueOf(SystemProperties.simpleInterface)); //$NON-NLS-1$
				FormatProperties.saveFormatProps();				
			}

			if (adminDerbyDatabase.checkAdminDatabase()) {
				if (FormatProperties.nDatabaseType == ICoreConstants.DERBY_DATABASE) {
					adminDerbyDatabase.loadDatabaseProjects();
				}
			}
			else {
				displayError(LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "ProjectCompendiumFrame.errorAdminDatabase")); //$NON-NLS-1$
				return false;
			}
		}
		catch(Exception ex2) {
			System.out.println(ex2.getLocalizedMessage());
			ex2.printStackTrace();
			System.out.flush();
			displayError(LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "ProjectCompendiumFrame.errorOpenAdmin1")+"\n"+ //$NON-NLS-1$ //$NON-NLS-2$
					ex2.getLocalizedMessage() +
					"\n\n"+LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "ProjectCompendiumFrame.errorOpenAdmin2")+"\n",  //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "ProjectCompendiumFrame.errorOpenAdminTitle")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			return false;
		}

		// IF THE LAST ACCESSED DATABASE WAS A MYSQL ONE LOAD AND CHECK
		if (FormatProperties.nDatabaseType == ICoreConstants.MYSQL_DATABASE && !FormatProperties.sDatabaseProfile.equals("")) { //$NON-NLS-1$
			try {
				oCurrentMySQLConnection = adminDerbyDatabase.getConnectionByName(FormatProperties.sDatabaseProfile, ICoreConstants.MYSQL_DATABASE);
				if (oCurrentMySQLConnection != null) {

					oServiceManager = new ServiceManager(ICoreConstants.MYSQL_DATABASE, oCurrentMySQLConnection.getLogin(), oCurrentMySQLConnection.getPassword(), oCurrentMySQLConnection.getServer());
					adminDatabase = new DBAdminDatabase(oServiceManager, oCurrentMySQLConnection.getLogin(), oCurrentMySQLConnection.getPassword(), oCurrentMySQLConnection.getServer());

					if (adminDatabase.checkAdminDatabase()) {
						adminDatabase.loadDatabaseProjects();
						setTitle(ICoreConstants.MYSQL_DATABASE, oCurrentMySQLConnection.getServer(), oCurrentMySQLConnection.getProfile(), ""); //$NON-NLS-1$
					}
					else {
						System.out.println("Unable to establish connection to Administration database"); //$NON-NLS-1$
						//return false;
					}
				}
			}
			catch (Exception ex3) {
				System.out.println(ex3.getLocalizedMessage());
				ex3.printStackTrace();
				System.out.flush();

				if (oCurrentMySQLConnection != null) {
					displayError(LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "ProjectCompendiumFrame.errorMessage2")+oCurrentMySQLConnection.getProfile()+"\n"+ex3.getLocalizedMessage()); //$NON-NLS-1$ //$NON-NLS-2$
					FormatProperties.nDatabaseType = ICoreConstants.DERBY_DATABASE;
					FormatProperties.setFormatProp("database", "derby"); //$NON-NLS-1$ //$NON-NLS-2$
					FormatProperties.saveFormatProps();
					oServiceManager = oDerbyServiceManager;
					adminDatabase = adminDerbyDatabase;
				}
				else {
					displayError(LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "ProjectCompendiumFrame.errorMessage3")+": "+FormatProperties.sDatabaseProfile+"\n"+ex3.getLocalizedMessage()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					FormatProperties.nDatabaseType = ICoreConstants.DERBY_DATABASE;
					FormatProperties.setFormatProp("database", "derby"); //$NON-NLS-1$ //$NON-NLS-2$
					FormatProperties.saveFormatProps();
					oServiceManager = oDerbyServiceManager;
					adminDatabase = adminDerbyDatabase;
				}
			}
		}

		// Get the Compendium Access databases list, if the appropriate ini file exists.
		// MB: 7th April 2005 - NOT USED ANYMORE. LEFT FOR A WHILE IN CASE NEED TO RETURN CODE.
		/*try {
			if (oServiceManager.getDatabaseManager().hasAccessDatabases())
				sAccessProjects = oServiceManager.getDatabaseManager().getAccessProjects();
		}
		catch(Exception ex4) {
			ex4.printStackTrace();
			System.out.flush();
			ProjectCompendium.APP.displayError("Error: Loading Access database list.\n\n" + ex4.getMessage());
			return false;
		}*/

		// DO WE HAVE ANY MYSQL CONNECTIONS SET UP?
		try {
			Vector connections = adminDerbyDatabase.getMySQLConnections();
			if (connections.size() == 0) {

				// IS THERE A PROPERTIES FILE WE CAN USE TO SET ONE UP FOR THE USER?
				File file = new File("System"+ProjectCompendium.sFS+"resources"+ProjectCompendium.sFS+"MySQL.properties"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				if (file.exists()) {
					try {
						Properties mysqlProperties = new Properties();
						mysqlProperties.load(new FileInputStream("System"+ProjectCompendium.sFS+"resources"+ProjectCompendium.sFS+"MySQL.properties")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
						String url = (String)mysqlProperties.get("url"); //$NON-NLS-1$
						if (url.equals("")) { //$NON-NLS-1$
							url = ICoreConstants.sDEFAULT_DATABASE_ADDRESS;
						}
						String username = (String)mysqlProperties.get("username"); //$NON-NLS-1$
						if (username.equals("")) { //$NON-NLS-1$
							username = ICoreConstants.sDEFAULT_DATABASE_USER;
						}

						String password = (String)mysqlProperties.get("password"); //$NON-NLS-1$

						ExternalConnection connection = new ExternalConnection();
						connection.setProfile("Default"); //$NON-NLS-1$
						connection.setServer(url);
						connection.setPassword(password);
						connection.setLogin(username);
						connection.setType(ICoreConstants.MYSQL_DATABASE);

						adminDerbyDatabase.insertConnection(connection);

						//CoreUtilities.deleteFile(file);

					}
					catch (Exception ex) {
						System.out.println("Exception (ProjectCompendiumFrame.connectToServices - existing)\n\n"+ex.getMessage()); //$NON-NLS-1$
					}
				}
				else {
					// CAN WE TEST FOR A LOCALHOST/ROOT/NULL POTENTIAL CONNECTION AND COMPENDIUM DATABASE ON THAT APPLICATION
					try {
						ServiceManager oManager = new ServiceManager(ICoreConstants.MYSQL_DATABASE, "root", "", "localhost"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
						DBAdminDatabase oAdminDatabase = new DBAdminDatabase(oManager, "root", "", "localhost"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

						if (oAdminDatabase.checkForAdminDatabase()) {
							ExternalConnection connection = new ExternalConnection();
							connection.setProfile("Default"); //$NON-NLS-1$
							connection.setServer("localhost"); //$NON-NLS-1$
							connection.setPassword(""); //$NON-NLS-1$
							connection.setLogin("root"); //$NON-NLS-1$
							connection.setType(ICoreConstants.MYSQL_DATABASE);
							adminDerbyDatabase.insertConnection(connection);
						}
					}
					catch(SQLException ex) {
						System.out.println("No local MySQL connection detected");
					}
				}
			}
		}
		catch(Exception ex) {
			System.out.println(ex.getLocalizedMessage());
			System.out.println("Exception (ProjectCompendiumFrame.connectToServices - main)."); //$NON-NLS-1$
			ex.printStackTrace();
			System.out.flush();
			displayError("Exception (ProjectCompendiumFrame.connectToServices - main):\n"+ex.getLocalizedMessage()); //$NON-NLS-1$
		}

		return true;
	}

	/**
	 * Set the cursor on the given frame to the wait cursor.
	 * @param frame, the frame to set the wait cursor on.
	 */
	public void setWaitCursor(UIViewFrame frame) {
		if (frame != null) {
			if (frame instanceof UIMapViewFrame) {
				UIViewPane uiview = ((UIMapViewFrame)frame).getViewPane();
				if (uiview != null) {
					uiview.setCursor(new Cursor(java.awt.Cursor.WAIT_CURSOR));
				}
			}
			else if (frame instanceof UIListViewFrame) {
				UIList uilist = ((UIListViewFrame)frame).getUIList();
				if (uilist != null) {
					uilist.getList().setCursor(new Cursor(java.awt.Cursor.WAIT_CURSOR));
				}
			}
		}
	}

	/**
	 * Set the cursor on the given frame to the default cursor.
	 * @param frame the frame to set the default cursor on.
	 */
	public void setDefaultCursor(UIViewFrame frame) {
		if (frame != null) {
			if (frame instanceof UIMapViewFrame) {
				UIViewPane uiview = ((UIMapViewFrame)frame).getViewPane();
				if (uiview != null) {
					uiview.setCursor(new Cursor(java.awt.Cursor.DEFAULT_CURSOR));
				}
			}
			else if (frame instanceof UIListViewFrame) {
				UIList uilist = ((UIListViewFrame)frame).getUIList();
				if (uilist != null) {
					uilist.getList().setCursor(new Cursor(java.awt.Cursor.DEFAULT_CURSOR));
				}
			}
		}
	}

	/**
	 * Set the frame cursor to the wait cursor.
	 */
	public void setWaitCursor() {
		super.setCursor(new Cursor(java.awt.Cursor.WAIT_CURSOR));
	}

	/**
	 * Set the frame cursor to the default cursor.
	 */
	public void setDefaultCursor() {
		super.setCursor(new Cursor(java.awt.Cursor.DEFAULT_CURSOR));
	}

	/**
	 * Set the frame cursor to the given cursor.
	 * @param c, the cursor to set the frame cursor to.
	 */
	public void setCursor(Cursor c) {
		super.setCursor(c);
	}

	/**
	 * Switches the type of the database being used to the given MySQL profile.
	 * If required, build a new service and load projects.
	 * @param ExternalConnection connection, the MySQL connection profile to use.
	 */
	public boolean setMySQLDatabaseProfile(ExternalConnection connection) {

		setWaitCursor();

		/*if (FormatProperties.simpleInterface) {
			displayError("When using the simple interface you can only view the default Derby database.\n\nYou should never see this message.\n\nIf you do, please report it as a bug.\n");
			return setDerbyDatabaseProfile();
		}*/

		if (oModel != null) {
			onFileClose();
		}

		oCurrentMySQLConnection = connection;
		int nType = ICoreConstants.MYSQL_DATABASE;
		String sServer = connection.getServer();
		String sUserName = connection.getLogin();
		String sPassword = connection.getPassword();
		String sProfileName = connection.getProfile();
		String sDefaultDatabase = connection.getName();

	  	try {
			oServiceManager = new ServiceManager(nType, sUserName, sPassword, sServer);
	  	}
		catch (Exception e) {
			displayError("Exception: creating ServiceManager (ProjectCompendiumFrame.setDatabaseProfile)\n\n"+e.getMessage()); //$NON-NLS-1$
			setDefaultCursor();
			return false;
	  	}
		adminDatabase = new DBAdminDatabase(oServiceManager, sUserName, sPassword, sServer);
		try {
			if (adminDatabase.checkAdminDatabase()) {
				adminDatabase.loadDatabaseProjects();
				updateProjects();
			}
			else {
				System.out.println(LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "ProjectCompendiumFrame.errorAdminDatabase2")); //$NON-NLS-1$
			}
		}
		catch(Exception ex) {
 			System.out.println(ex.getLocalizedMessage());
			ex.printStackTrace();
			System.out.flush();
			displayError(LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "ProjectCompendiumFrame.errorMySQL1")+ //$NON-NLS-1$
					oCurrentMySQLConnection.getProfile()+"\n"+ex.getLocalizedMessage()+ //$NON-NLS-1$
					"\n\n"+LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "ProjectCompendiumFrame.errorMySQL2")+"\n"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			setDefaultCursor();
			return false;
		}

		FormatProperties.nDatabaseType = nType;
		FormatProperties.sDatabaseProfile = sProfileName;
		FormatProperties.setFormatProp("database", "mysql"); //$NON-NLS-1$ //$NON-NLS-2$
		FormatProperties.setFormatProp("databaseprofile", sProfileName); //$NON-NLS-1$
		FormatProperties.saveFormatProps();

		setTitle(ICoreConstants.MYSQL_DATABASE, sServer, sProfileName, ""); //$NON-NLS-1$

		oMenuManager.enableConvertMenuOptions();

		// IF A DEFAULT DATABASE HAS BEEN SET, TRY AND LOGIN AUTOMATICALLY
		if (sServer.equals(ICoreConstants.sDEFAULT_DATABASE_ADDRESS) && sDefaultDatabase != null
					&& !sDefaultDatabase.equals("")) { //$NON-NLS-1$

			autoFileOpen(sDefaultDatabase);
		}
		else if (vtProjects == null || vtProjects.size() == 0) {
			if (SystemProperties.createDefaultProject) {
				onFileNew();
			}
		}
		else {
			onFileOpen();
		}

		setDefaultCursor();

		return true;
	}
	
	/**
	 * Switch the type of the database being used to the default local Derby database.
	 * If required, build a new service and load projects.
	 */
	public boolean setDerbyDatabaseProfile() {
		setWaitCursor();

		if (oModel != null) {
			onFileClose();
		}

		/*if (FormatProperties.simpleInterface) {
			FormatProperties.defaultDatabase = SystemProperties.defaultProjectName;
			FormatProperties.setFormatProp("defaultdatabase", SystemProperties.defaultProjectName);			
		}*/
		
		FormatProperties.nDatabaseType = ICoreConstants.DERBY_DATABASE;
		FormatProperties.setFormatProp("database", "derby"); //$NON-NLS-1$ //$NON-NLS-2$
		FormatProperties.saveFormatProps();
		oServiceManager = oDerbyServiceManager;
		adminDatabase = adminDerbyDatabase;
		
		try {
			adminDatabase.loadDatabaseProjects();
	 		updateProjects();

			setDerbyTitle(""); //$NON-NLS-1$

			oMenuManager.enableConvertMenuOptions();
			oToolBarManager.selectProfile(""); //$NON-NLS-1$

			// IF A DEFAULT DATABASE HAS BEEN SET, TRY AND LOGIN AUTOMATICALLY
			if (FormatProperties.defaultDatabase != null
						&& !FormatProperties.defaultDatabase.equals("")) { //$NON-NLS-1$
				autoFileOpen(FormatProperties.defaultDatabase);
			}
			else if (vtProjects == null || vtProjects.size() == 0) {
				if (SystemProperties.createDefaultProject) {
					onFileNew();
				}
			}
			else {
				onFileOpen();
			}

			setDefaultCursor();

			return true;
		} catch(Exception e) {
			ProjectCompendium.APP.displayError(LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "ProjectCompendiumFrame.errorProjectLoading")+":\n\n"+e.getLocalizedMessage()); //$NON-NLS-1$ //$NON-NLS-2$
		}
		return false;
	}


	/**
	 * Return the current active link group.
	 * @return String, the id of the current active link group.
	 */
	public String getActiveLinkGroup() {
		return activeLinkGroup;
	}

	/**
	 * Set the current active link group.
	 * @param sLinkGroupID, the id of the link group to make the active group.
	 */
	public boolean setActiveLinkGroup(String sLinkGroupID) {

		if (! (activeLinkGroup).equals(sLinkGroupID) ) {
			try {
				activeLinkGroup = sLinkGroupID;
				((SystemService)oModel.getSystemService()).setLinkGroup(oModel.getSession(), activeLinkGroup);
				return true;
			}
			catch(Exception ex) {
				displayError(LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "ProjectCompendiumFrame.errorLinkGroupUpdate")); //$NON-NLS-1$
			}
		}
		return false;
	}

	/**
	 * Return the current active code group.
	 * @return String, the id of the current active code group.
	 */
	public String getActiveCodeGroup() {
		return activeGroup;
	}

	/**
	 * Set the current active code group.
	 * @param sCodeGroupID, the id of the code group to make the active group.
	 */
	public boolean setActiveCodeGroup(String sCodeGroupID) {

		if (! (activeGroup).equals(sCodeGroupID) ) {
			try {
				activeGroup = sCodeGroupID;
				((SystemService)oModel.getSystemService()).setCodeGroup(oModel.getSession(), activeGroup);
				oToolBarManager.updateCodeChoiceBoxData();
				return true;
			}
			catch(Exception ex) {
				displayError(LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "ProjectCompendiumFrame.errorcodeGroupUpdate")); //$NON-NLS-1$
			}
		}
		return false;
	}

	/**
	 * Creates and initializes the desktop.
	 */
	protected void createDesktop() {

		oDesktop = new JDesktopPane();
		UIDesktopManager manager = new UIDesktopManager(oDesktop);
		oDesktop.setDesktopManager(manager);
		
		// Part of an attempt to use a scrollable desktop to make sure internal frame never
		// lost off the right/bottom bounderies.
		// Was buggy, so for now, just restricted the internalframe to the available space.
		/*JScrollPane scrollpane = new JScrollPane(oDesktop);
		(scrollpane.getVerticalScrollBar()).setUnitIncrement(100);
		(scrollpane.getHorizontalScrollBar()).setUnitIncrement(100);
		scrollpane.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {
		    public void adjustmentValueChanged(AdjustmentEvent evt) {
		        if (evt.getID() == AdjustmentEvent.ADJUSTMENT_VALUE_CHANGED) {
	            	//System.out.println("vertical adjustment by:"+evt.getValue());
		        } 
		    }
		});
		scrollpane.getHorizontalScrollBar().addAdjustmentListener(new AdjustmentListener() {
		    public void adjustmentValueChanged(AdjustmentEvent evt) {
		        if (evt.getID() == AdjustmentEvent.ADJUSTMENT_VALUE_CHANGED) {
	            	System.out.println("horizontal adjustment by:"+evt.getValue());
		        } 
		    }
		});*/

		oSplitter = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, oTabbedPane, oDesktop);
		oSplitter.setOneTouchExpandable(true);
		oSplitter.setDividerSize(10);
		oSplitter.setContinuousLayout(true);
		
		oInnerPanel.add(oSplitter, BorderLayout.CENTER);
		oMainPanel.add(oInnerPanel, BorderLayout.CENTER);
		
		oWelcomePanel = new UIWelcomePane();
	}

	/** 
	 * Display the welcome screen - replace the desktop
	 */
	public void showWelcome() {
		oMainPanel.remove(oInnerPanel);
		oMainPanel.add(oWelcomePanel, BorderLayout.CENTER);
		oMenuManager.setWelcomeEnabled(false);
		oMainPanel.validate();
		oMainPanel.repaint();
	}
	
	/** 
	 * Display the desktop and remove the welcome screen
	 */
	public void showDesktop() {
		oMainPanel.remove(oWelcomePanel);
		oMainPanel.add(oInnerPanel, BorderLayout.CENTER);
		oMenuManager.setWelcomeEnabled(true);
		oMainPanel.validate();
		oMainPanel.repaint();		
	}
	
	protected void createWelcomeScreen() {
		JPanel welcomePanel = new JPanel();
		
	}
	
	/**
	 * Creates and initializes the view history bar.
	 */
	protected void createViewHistoryBar() {

		oViewHistoryBar = new UIViewHistoryBar();
		oInnerPanel.add(oViewHistoryBar, BorderLayout.NORTH);
		displayViewHistoryBar(FormatProperties.displayViewHistoryBar);
	}

	/**
	 * Creates and initializes the status bar.
	 */
	protected void createStatusBar() {

		oStatusBar = new UIStatusBar(" "); //$NON-NLS-1$
		oStatusBar.setMinimumSize(new Dimension(0, 14));
		oContentPane.add(oStatusBar, BorderLayout.SOUTH);

		displayStatusBar(FormatProperties.displayStatusBar);
	}

	/**
	 * hide/show the outline view.
	 * @author Lakshmi
	 * @date 2/3/06
	 */
	protected void createOutlineView() {
		String sDisplay = FormatProperties.displayOutlineView;
		oMenuManager.addOutlineView(sDisplay, false);
	}
	
	/**
	 * hide/show the unread view.
	 * @author Lakshmi
	 * @throws SQLException 
	 * @date 6/27/06
	 */
	protected void createUnreadView() throws SQLException {
		boolean sDisplay = FormatProperties.displayUnreadView;
		if (sDisplay) {
			oMenuManager.addUnreadView(false);
		} 
	}
	
	/**
	 * hide/show the tags view.
	 */
	protected void createTagsView() {
		boolean sDisplay = FormatProperties.displayTagsView;
		if (sDisplay) {
			oMenuManager.addTagsView(false);
		} 
	}

	/**
	 * hide/show the view history bar.
	 */
	public void displayViewHistoryBar(boolean bDisplay) {

		if (bDisplay) {
			oViewHistoryBar.setVisible(true);
		}
		else {
			oViewHistoryBar.setVisible(false);
		}
	}

	/**
	 * hide/show the status bar.
	 */
	public void displayStatusBar(boolean bDisplay) {

		if (bDisplay) {
			oStatusBar.setVisible(true);
		}
		else {
			oStatusBar.setVisible(false);
		}
	}

	/**
	 * Attempt to automatically login the default database with its default user.
	 */
	protected boolean processDefaultLogin(String sDatabase) {

		boolean bDefaultLoginSucessful = false;

		sFriendlyName = sDatabase;
		String sModel = null;
		try {
			sModel = adminDatabase.getDatabaseName(sDatabase);
		} catch (Exception e) {}

		if (sModel == null) {
			displayError(LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "ProjectCompendiumFrame.errorDefaultProject")+sDatabase); //$NON-NLS-1$
			return bDefaultLoginSucessful;
		}
		else {
			try {
				// CHECK IF DATABASE UP TO DATE
				try {
					startUpDlg.setMessage(LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "ProjectCompendiumFrame.checkingSchema")); //$NON-NLS-1$
					int status = adminDatabase.getSchemaStatusForDatabase(sModel);
					if (status == ICoreConstants.OLDER_DATABASE_SCHEMA) {
						if (!DatabaseUpdate.updateDatabase(adminDatabase, this, sModel)) {
							setDefaultCursor();
							return false;
						}
					}
					else if (status == ICoreConstants.NEWER_DATABASE_SCHEMA) {
						displayError(LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "ProjectCompendiumFrame.message4a")+" "+sFriendlyName+" "+LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "ProjectCompendiumFrame.message4b")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
						setDefaultCursor();
						return false;
					}
					startUpDlg.setMessage(LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "ProjectCompendiumFrame.checksComplete")); //$NON-NLS-1$
				}
				catch(Exception ie) {
					setDefaultCursor();
					return false;
				}

				DBDatabaseManager databaseManager = oServiceManager.getDatabaseManager();
				databaseManager.openProject(sModel);
		       	DBConnection dbcon = databaseManager.requestConnection(sModel);

				UserProfile oUser = DBSystem.getDefaultUser(dbcon);

				if (oUser != null) {
					sUserName = oUser.getLoginName();
					sUserPassword = oUser.getPassword();
					bDefaultLoginSucessful = validateUser(sModel, sUserName, sUserPassword);
					if (bDefaultLoginSucessful) {
						if (FormatProperties.nDatabaseType == ICoreConstants.MYSQL_DATABASE) {
							setTitle(ICoreConstants.MYSQL_DATABASE, oCurrentMySQLConnection.getServer(), FormatProperties.sDatabaseProfile, sFriendlyName);
						}
						else {
							setDerbyTitle(sFriendlyName);
						}
					}
				}
				else {
					System.out.println("In processDefaultLogin: User is null"); //$NON-NLS-1$
				}

				databaseManager.releaseConnection(sModel, dbcon);
			}
			catch(Exception ex) {
				ex.printStackTrace();
				return bDefaultLoginSucessful;
			}
		}

		return bDefaultLoginSucessful;
	}

	/**
	 * Open the logon dialog and process the results.
	 */
	protected boolean createLogonScreen() {

		if (oLogonDialog != null && oLogonDialog.isVisible()) {
			return false;
		}

		// CHECK IF ANY DATABASE SCHEMAS NEEDS UPDATING
		//Hashtable htProjectStatus = adminDatabase.getProjectSchemaStatus();

		//Hashtable htProjectStatus = new Hashtable();
		
		String sDatabaseServer = ""; //$NON-NLS-1$
		if (FormatProperties.nDatabaseType == ICoreConstants.MYSQL_DATABASE && oCurrentMySQLConnection != null)
			sDatabaseServer = oCurrentMySQLConnection.getServer();

		//oLogonDialog = new UILogonDialog(this, vtProjects, htProjectStatus, sUserName, sUserPassword, sFriendlyName, sDatabaseServer);
		oLogonDialog = new UILogonDialog(this, vtProjects, sUserName, sUserPassword, sFriendlyName, sDatabaseServer);
		oLogonDialog.setModal(true);
		oLogonDialog.setVisible(true);

		oLogonDialog.getFocusOwner();

		if(oLogonDialog.isLogout()) {
			if (oDesktop != null) {
				onFileClose();
			}
			else
				onExit();
			return false;
		}

		// get login values
		String sName = oLogonDialog.getModel();
		sFriendlyName = sName;
		String sModel = null;
		try {
			sModel = adminDatabase.getDatabaseName(sName);
		} catch (Exception e) {}
		
		if (sModel == null) {
			displayError(LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "ProjectCompendiumFrame.databaseNotFound")+sName); //$NON-NLS-1$
			onFileClose();
			return false;
		}

		sUserName = oLogonDialog.getUserName();
		sUserPassword = oLogonDialog.getUserPassword();

		setWaitCursor();

		if(bProceed) {
			// CHECK IF DATABASE UP TO DATE
			try {
				int status = adminDatabase.getSchemaStatusForDatabase(sModel);
				if (status == ICoreConstants.OLDER_DATABASE_SCHEMA) {
					if (!DatabaseUpdate.updateDatabase(adminDatabase, this, sModel)) {
						setDefaultCursor();
						return false;
					}
				}
				else if (status == ICoreConstants.NEWER_DATABASE_SCHEMA) {
					displayError(LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "ProjectCompendiumFrame.message5a")+ //$NON-NLS-1$
						" "+sFriendlyName+" "+ //$NON-NLS-1$ //$NON-NLS-2$
							LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "ProjectCompendiumFrame.message5b")); //$NON-NLS-1$ //$NON-NLS-2$
					setDefaultCursor();
					return false;
				}
			}
			catch(Exception ie) {
				setDefaultCursor();
				return false;
			}

			if(!validateUser(sModel, sUserName, sUserPassword)) {
				//popup the error message
	            JOptionPane oOptionPane = new JOptionPane(LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "ProjectCompendiumFrame.errorValidUser")); //$NON-NLS-1$
				JDialog oDialog = oOptionPane.createDialog(oContentPane,LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "ProjectCompendiumFrame.loginErrorTitle")); //$NON-NLS-1$
				oDialog.setModal(true);
				oDialog.setVisible(true);

				//invoke the logon dialog again..
				createLogonScreen();
			}

			if (!bProceed)
				return false;
		}
		else {
			setDefaultCursor();
			return false;
		}

		if (FormatProperties.nDatabaseType == ICoreConstants.MYSQL_DATABASE) {
			setTitle(ICoreConstants.MYSQL_DATABASE, oCurrentMySQLConnection.getServer(), FormatProperties.sDatabaseProfile, sFriendlyName);
		}
		else {
			setDerbyTitle(sFriendlyName);
		}

		setDefaultCursor();
		return true;
	}

	/**
	 * Set the logon process to proceed after a successfull check if true.
	 * @return proceed, whether to proceed with the current logong attempt.
	 */
	public synchronized void proceed(boolean proceed) {
		bProceed = proceed;
	}

	/**
	 * Get the logon proceed status.
	 * @return boolean, true to proceed with the current logong attempt, else false.
	 */
	public boolean isProceed() {
		return bProceed;
	}

	/**
	 * Validate the given user details against the database and return is valid.
	 * @param model, the database name of the database to validate against.
	 * @param user, the user name to validate.
	 * @param password, the password to validate.
	 * @return boolean, whether the login was valid or not.
	 */
	public boolean validateUser(String model, String user, String password) {

		try {
			oModel = oServiceManager.registerUser(model, user, password);
		} catch(SQLException ex) {
			System.out.println("Exception: (ProjectCompendiumFrame.validateUser) \n\n"+ex.getMessage()); //$NON-NLS-1$
		}
		String sErrorMessage = ""; //$NON-NLS-1$
		if (oModel == null || !(sErrorMessage = oModel.getErrorMessage()).equals("")) { //$NON-NLS-1$
			JOptionPane.showMessageDialog(null, sErrorMessage, LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "ProjectCompendiumFrame.initialisationTitle"), JOptionPane.WARNING_MESSAGE); //$NON-NLS-1$
			return false;
		}

		try {
			oModel.initialize();
		} catch(SQLException ex) {
			System.out.println("Exception: (ProjectCompendiumFrame.validateUser) \n\n"+ex.getMessage()); //$NON-NLS-1$
			//return false;
		} catch (java.net.UnknownHostException uhe) {
			System.out.println("Exception: (ProjectCompendiumFrame.validateUser) \n\n"+uhe.getMessage()); //$NON-NLS-1$
			return false;
		}
				
		// Store default font.
		currentDefaultFont = ((Model)oModel).labelFont;		

		if(oModel != null)
			return true;
		else
			return false;

	}

	/**
	 * Set the given text in the status bar.
	 * @param text, the text to set in the status bar.
	 */
	public void setStatus(String text) {
		oStatusBar.setStatus(text);
	}
	
	/**
	 * Gets the current text from the status bar.
	 */
	public String getStatus() {
		return 	oStatusBar.getStatus();
	}

	/**
	 * Set the given history in the view history bar.
	 * @param vtHistory, the list of view history.
	 */
	public void setViewHistory(Vector vtHistory) {
		oViewHistoryBar.setViewHistory(vtHistory);
	}

	/**
	 * Get the status bar.
	 */
	public UIStatusBar getStatusBar() {
		return oStatusBar;
	}

// ***** Event Handlers ***** //

	/**
	 * Invoked when a key is pressed.
	 * @param e, the associated KeyEvent.
	 */
	public void keyPressed(KeyEvent evt) {

		char [] key = {evt.getKeyChar()};
		String sKeyPressed = new String(key);
		int keyCode = evt.getKeyCode();
		int modifiers = evt.getModifiers();

		UIViewFrame viewFrame = getCurrentFrame();

		// IF WINDOW NOT SELECTED, KEY EVENT GOES SKEWY
		if (!viewFrame.isSelected()) {
			if (viewFrame instanceof UIMapViewFrame)
				((UIMapViewFrame)viewFrame).setSelected(true);
			else if (viewFrame instanceof UIListViewFrame) {
				((UIListViewFrame)viewFrame).setSelected(true);
			}
		}

		ViewPaneUI viewui = null;
		UIList uilist = null;
		ListUI listui = null;
		UIViewPane uiview = null;

		if (viewFrame instanceof UIMapViewFrame) {
			uiview = ((UIMapViewFrame)viewFrame).getViewPane();
			if (uiview != null)
				viewui = uiview.getUI();
		}
		else if (viewFrame instanceof UIListViewFrame) {
			uilist = ((UIListViewFrame)viewFrame).getUIList();
			if (uilist != null)
				listui = uilist.getListUI();
		}

		setWaitCursor();
		setWaitCursor(viewFrame);

		if (modifiers == java.awt.Event.ALT_MASK) {
			switch(keyCode) {
				case KeyEvent.VK_0: {
					if (uiview != null) {
						createNodeFromStencil(uiview, 0);
						evt.consume();
					}
					break;
				}
				case KeyEvent.VK_1: {
					if (uiview != null) {
						createNodeFromStencil(uiview, 1);
						evt.consume();
					}
					break;
				}
				case KeyEvent.VK_2: {
					if (uiview != null) {
						createNodeFromStencil(uiview, 2);
						evt.consume();
					}
					break;
				}
				case KeyEvent.VK_3: {
					if (uiview != null) {
						createNodeFromStencil(uiview, 3);
						evt.consume();
					}
					break;
				}
				case KeyEvent.VK_4: {
					if (uiview != null) {
						createNodeFromStencil(uiview, 4);
						evt.consume();
					}
					break;
				}
				case KeyEvent.VK_5: {
					if (uiview != null) {
						createNodeFromStencil(uiview, 5);
						evt.consume();
					}
					break;
				}
				case KeyEvent.VK_6: {
					if (uiview != null) {
						createNodeFromStencil(uiview, 6);
						evt.consume();
					}
					break;
				}
				case KeyEvent.VK_7: {
					if (uiview != null) {
						createNodeFromStencil(uiview, 7);
						evt.consume();
					}
					break;
				}
				case KeyEvent.VK_8: {
					if (uiview != null) {
						createNodeFromStencil(uiview, 8);
						evt.consume();
					}
					break;
				}
				case KeyEvent.VK_9: {
					if (uiview != null) {
						createNodeFromStencil(uiview, 9);
						evt.consume();
					}
					break;
				}
			}
		}
		if (modifiers == shortcutKey) {
			switch(keyCode) {
				case KeyEvent.VK_F: { // OPEN SEARCH
					onSearch();
					evt.consume();
					break;
				}
				case KeyEvent.VK_O: { // OPEN PROJECT DIALOG
					onFileOpen();
					evt.consume();
					break;
				}
				case KeyEvent.VK_N: { // NEW PROJECT DIALOG
					onFileNew();
					evt.consume();
					break;
				}
				case KeyEvent.VK_X: { // CUT
					if (viewui != null)
						viewui.cutToClipboard(null);
					else
						listui.cutToClipboard();
					evt.consume();
					break;
				}
				case KeyEvent.VK_C: { // COPY
					if (viewui != null)
						viewui.copyToClipboard(null);
					else
						listui.copyToClipboard();
					evt.consume();
					break;
				}
				case KeyEvent.VK_V: { // PASTE
					if (viewui != null)
						viewui.pasteFromClipboard();
					else
						listui.pasteFromClipboard();
					evt.consume();
					break;
				}
				case KeyEvent.VK_A: { // SELECT ALL
					if (viewui != null)
						viewui.onSelectAll();
					else
						listui.onSelectAll();
					evt.consume();
					break;
				}
				case KeyEvent.VK_Z: { // UNDO
					onEditUndo();
					evt.consume();
					break;
				}
				case KeyEvent.VK_Y: { // REDO
					onEditRedo();
					evt.consume();
					break;
				}
				case KeyEvent.VK_W: { // CLOSE WINDOW
					try {
						if (viewui != null) {
							if (uiview.getView() != getHomeView()) {
								viewFrame.setClosed(true);
							}
						}
						else
							viewFrame.setClosed(true);
					}
					catch(Exception e) {}

					evt.consume();
					break;
				}
			}
		}
		else if (modifiers == java.awt.Event.CTRL_MASK) {
			switch(keyCode) {
				case KeyEvent.VK_RIGHT: { // ARRANGE
					onViewArrange(IUIArrange.LEFTRIGHT);
					evt.consume();
					break;
				}
				case KeyEvent.VK_DOWN: { // ARRANGE
					onViewArrange(IUIArrange.TOPDOWN);
					evt.consume();
					break;
				}
				case KeyEvent.VK_R: { // ARRANGE
					onViewArrange(IUIArrange.LEFTRIGHT);
					evt.consume();
					break;
				}
				case KeyEvent.VK_T: { // OPEN TAG WINDOW
					onCodes();
					evt.consume();
					break;
				}				
				case KeyEvent.VK_B: { // BOLD / UNBOLD THE TEXT OF ALL SELECTED NODES IN THE CURRENT MAP
					getToolBarManager().addFontStyle(Font.BOLD);
					evt.consume();
					break;					
				}
				case KeyEvent.VK_I: { // ITALIC / UNITALIC THE TEXT OF ALL SELECTED NODES IN THE CURRENT MAP
					getToolBarManager().addFontStyle(Font.ITALIC);					
					evt.consume();
					break;									
				}							
				case KeyEvent.VK_ENTER: { // CLOSE WINDOW
					try {
						if (viewui != null) {
							if (uiview.getView() != getHomeView() )
								viewFrame.setClosed(true);
						}
						else
							viewFrame.setClosed(true);
					}
					catch(Exception e) {}

					evt.consume();
					break;
				}
				case KeyEvent.VK_TAB: { // cycle open windows
					onCycleWindows();
					evt.consume();
					break;
				}				
			}
		}
		else if ((keyCode == KeyEvent.VK_DELETE && modifiers == 0) 
				|| (keyCode == KeyEvent.VK_BACK_SPACE && modifiers == 0)) {
			
			if (viewui != null)
				viewui.onDelete();
			else
				listui.onDelete();

			evt.consume();
		}
		else if (keyCode == KeyEvent.VK_PAGE_UP && modifiers == 0) {
			Point oldPoint = viewFrame.getViewPosition();
			int cCurrentHeight = viewFrame.getHeight();
			int newX = oldPoint.x;
			int newY = oldPoint.y - (cCurrentHeight-100);
			if(newY < 0)
				newY = 0;
			viewFrame.setViewPosition(new Point(newX,newY));
			evt.consume();
		}
		else if (keyCode == KeyEvent.VK_PAGE_DOWN && modifiers == 0) {
			Point oldPoint = viewFrame.getViewPosition();
			int cCurrentHeight = viewFrame.getHeight();
			viewFrame.setViewPosition(new Point(oldPoint.x, oldPoint.y + (cCurrentHeight-100)));
			evt.consume();
		}
		else if (keyCode == KeyEvent.VK_F2 && modifiers == 0) {
			zoomNext();
			evt.consume();
		}
		else if (keyCode == KeyEvent.VK_F3 && modifiers == 0) {
			zoomFit();
			evt.consume();
		}
		else if (keyCode == KeyEvent.VK_F4 && modifiers == 0) {
			zoomFocused();
			evt.consume();
		}
		setDefaultCursor(viewFrame);
		setDefaultCursor();
  	}
	
	/**
	 * Bring to front the next window in the tab cycle
	 */
	public void onCycleWindows() {
		UIViewFrame viewFrame = null;
		boolean frameFound = false; int i=0;

		JInternalFrame[] frames = oDesktop.getAllFrames();
		while(!frameFound && i<frames.length) {
			viewFrame = (UIViewFrame)frames[i++];
			if (viewFrame.isSelected()) {
				frameFound = true;
				int j= i+1;
				if (j == frames.length) {
					j=0;
				} 
				viewFrame = (UIViewFrame)frames[j];
				break;
			}
		}
		if (!frameFound) {
			viewFrame = getInternalFrame(oHomeView);
		}
		
	    try {
	    	viewFrame.setMaximum(true);
		    if (viewFrame.isIcon()) {
		    	viewFrame.setIcon(false);
		    }
		    viewFrame.moveToFront();
		    viewFrame.setSelected(true);
	    } catch (Exception ex) {
	    	ex.printStackTrace();
	    }
	}
	
	/**
	 * Create a node for the given shortcut number from the current stencil.
	 * @param uiview, the UIViewPane to create the node in.
	 * @param nShortcut, the shortcut key to create the stencil node for.
	 */
	public void createNodeFromStencil(UIViewPane uiview, int nShortcut) {
		DraggableStencilIcon oIcon = oStencilManager.getItemForShortcut(nShortcut);
		if (oIcon != null) {
			Point p = getKeyPress(uiview);
			uiview.createNodeFromStencil(oIcon, p.x, p.y);
		}
	}

	/**
	 * Take the given keypress coordinates and check and adjust for node creation.
	 * @param nX, the x position of the key press.
	 * @param nY, the y position of the key press.
	 * @param uiview, the map the key was pressed in.
	 * @return Point, the adjusted coordinates.
	 */
	private Point getKeyPress(UIViewPane uiview) {

		Point p = new Point(_x, _y);
		SwingUtilities.convertPointFromScreen(p, uiview);
		int nX = p.x;
		int nY = p.y;

		// MOVE NEW NODE OUT A BIT SO MOUSEPOINTER NOT RIGHT ON EDGE
		if (nX >= 20 && nY >= 20) {
			nX -= 20;
			nY -= 20;
		}
		return new Point(nX, nY);
	}

	/**
	 * Invoked when a key is released.
	 * @param e, the associated KeyEvent.
	 */
	public void keyReleased(KeyEvent e) {
		e.consume();
	}

	/**
	 * Invoked when a key is typed.
	 * @param e, the associated KeyEvent.
	 */
  	public void keyTyped(KeyEvent e) {
  		
		if (!e.isAltDown() && !e.isControlDown() && !e.isMetaDown()) {
			
			UIViewFrame viewFrame = getCurrentFrame();
			
			// IF WINDOW NOT SELECTED, KEY EVENT GOES SKEWY
			if (!viewFrame.isSelected()) {
				if (viewFrame instanceof UIMapViewFrame)
					((UIMapViewFrame)viewFrame).setSelected(true);
				else if (viewFrame instanceof UIListViewFrame) {
					((UIListViewFrame)viewFrame).setSelected(true);
				}
			}

			ViewPaneUI viewui = null;
			UIList uilist = null;
			ListUI listui = null;
			UIViewPane uiview = null;

			if (viewFrame instanceof UIMapViewFrame) {
				uiview = ((UIMapViewFrame)viewFrame).getViewPane();
				if (uiview != null)
					viewui = uiview.getUI();
			}
			else if (viewFrame instanceof UIListViewFrame) {
				uilist = ((UIListViewFrame)viewFrame).getUIList();
				if (uilist != null)
					listui = uilist.getListUI();
			}
			
			char keyChar = e.getKeyChar();
			char[] key = {keyChar};
			String sKeyPressed = new String(key);		
			int nType = UINodeTypeManager.getTypeForKeyPress(sKeyPressed);
			if (viewui != null && nType != -1) {
				Point p = getKeyPress(uiview);
				int nX = p.x;
				int nY = p.y;
				if (nX >= 20 && nY >= 10) {
					nX -= 20;
					nY -= 10;
				}
				viewui.addNewNode(nType, nX, nY);
			} else {
				if (!uilist.getList().isEditing()) {
					listui.createNode( nType, "", //$NON-NLS-1$
						ProjectCompendium.APP.getModel().getUserProfile().getUserName(), "", //$NON-NLS-1$
						"", listui.ptLocationKeyPress.x, (uilist.getNumberOfNodes() + 1) * 10 //$NON-NLS-1$
						);
					uilist.updateTable();
				}				
			}  		
		}
  		e.consume();
  	}


	/**
	 * refresh the Stencil Menu
	 * @see com.compendium.ui.UIMenuManager#createStencilMenu
	 */
	public void refreshStencilMenu() {
		oMenuManager.createStencilMenu();
	}

	/**
	 * Reset the toolobar zoom settings.
	 * @see com.compendium.ui.UIToolBarManager#resetZoom
	 */
	public void resetZoom() {
		oToolBarManager.resetZoom();
	}

	/**
	 * Zoom to the next level.
	 * @see com.compendium.ui.UIMenuManager#onZoomNext
	 */
	public void zoomNext() {
		oMenuManager.onZoomNext();
		resetZoom();
	}

	/**
	 * Zoom to current view to fit the screen.
	 * @see com.compendium.ui.UIMenuManager#onZoomToFit
	 */
	public void zoomFit() {
		oMenuManager.onZoomToFit();
		resetZoom();
	}

	/**
	 * Zoom to 100% and focus the selected node.
	 * @see com.compendium.ui.UIMenuManager#onZoomRefocused
	 */
	public void zoomFocused() {
		oMenuManager.onZoomRefocused();
		resetZoom();
	}

	/**
	 * Update the projects list from the Administration Database.
	 */
	public void updateProjects() {
		try {
			vtProjects = adminDatabase.getDatabaseProjects();
			if (vtProjects == null || vtProjects.size() == 0) {
				oMenuManager.setFileOpenEnablement(false);
				oToolBarManager.setFileOpenEnablement(false);
			}
			else {
				oMenuManager.setFileOpenEnablement(true);
				oToolBarManager.setFileOpenEnablement(true);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/** 
	 * Return true if any projects exits, else false;
	 * @return true if any projects exits, else false;
	 */
	public boolean projectsExist() {
		if (vtProjects != null && vtProjects.size() > 0) {
			return true;
		}
		return false;
	}
	
	/**
	 * Return the string representation of the current database projects list.
	 * @return String, a comma separated string of the current database projects.
	 */
	public Vector getProjects() {
		return vtProjects;
	}

	/**
	 * Update the default user in the current database Database.
	 * Set the default user for the current database.
	 */
	public boolean setDefaultUser(String sUserID) {

		try {
			((SystemService)oModel.getSystemService()).setDefaultUser(oModel.getSession(), sUserID);
			return true;
		}
		catch(Exception ex) {
			displayError(LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "ProjectCompendiumFrame.errorUpdateUser")); //$NON-NLS-1$
		}
		return false;
	}

	/**
	 * Is the current database the default database.
	 * @return boolean, true if the current database the default database, else false.
	 */
	public boolean isDefaultDatabase() {
		if (FormatProperties.nDatabaseType == ICoreConstants.MYSQL_DATABASE) {
			if ( sFriendlyName.equals(oCurrentMySQLConnection.getName()) )
				return true;
		}
		else {
			if ( sFriendlyName.equals(FormatProperties.defaultDatabase) )
				return true;
		}
		return false;
	}

	/**
	 * Return the default database value.
	 * @return String, the name of the default database.
	 */
	public String getDefaultDatabase() {
		if (FormatProperties.nDatabaseType == ICoreConstants.MYSQL_DATABASE)
			return oCurrentMySQLConnection.getName();
		else {
			return FormatProperties.defaultDatabase;
		}
	}

	/**
	 * Set the default database value locally and in the format properties file and the database.
	 * @param database the name of the default database.
	 */
	public void setDefaultDatabase(String database) {

		if (FormatProperties.nDatabaseType == ICoreConstants.DERBY_DATABASE) {
			FormatProperties.defaultDatabase = database;
			FormatProperties.setFormatProp( "defaultdatabase", database ); //$NON-NLS-1$
			FormatProperties.saveFormatProps();
		}
		else {
			try {
				adminDerbyDatabase.setDefaultDatabase(database, FormatProperties.sDatabaseProfile, ICoreConstants.MYSQL_DATABASE);
				if (oCurrentMySQLConnection != null)
					oCurrentMySQLConnection.setName(database);
			}
			catch(Exception ex) {
				ex.printStackTrace();
				displayError(LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "ProjectCompendiumFrame.errorDefault")+": \n\n"+ex.getMessage()); //$NON-NLS-1$ //$NON-NLS-2$
			}
		}
	}

//************* FILE MENU ****************//

	/**
	 * Lets the user open a database project.
	 * Note: The code in this method is run in an inner thread.
	 * @param String sDatabaseName, the default database to open.
	 */
	public void autoFileOpen(String sDatabaseName) {
		final String sDatabase = sDatabaseName;

		// THIS THREAD IS REQUIRED FOR PROGRESS DIALOG CALLED IN processDefaultLogin
		Thread thread = new Thread("ProjectCompendiumFrame.autoFileOpen") { //$NON-NLS-1$

			public void run() {

				// create the log on screen
				sUserName = ""; //$NON-NLS-1$
				sUserPassword = ""; //$NON-NLS-1$

				setWaitCursor();

				//System.out.println("About to try and process default login");
				if (!processDefaultLogin(sDatabase)) {
					// IF in simple interface mode and it cannot find the default database, 
					// so somehow the default database has been deleted
					// ask the user to create it again.
					//if (FormatProperties.simpleInterface) {
					//	onFileNew();							
					//} else {
						return;
					//}
				}
				
				initializeForProject();				
				setDefaultCursor();
			}
		};
		thread.start();
	}

	/**
	 * Open a compendium database project, if you do not have a currently open project.
	 * Note: The contents of this method are run in an inner thread.
	 */
	public void onFileOpen() {

		if (isProjectOpen(LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "ProjectCompendiumFrame.openProject"))) //$NON-NLS-1$
			return;

		Thread thread = new Thread("ProjectCompendiumFrame.onFileOpen") { //$NON-NLS-1$
			public void run() {

				// create the log on screen
				sUserName = ""; //$NON-NLS-1$
				sUserPassword = ""; //$NON-NLS-1$

				if (createLogonScreen() == false) {
					setDefaultCursor();
					return;
				}

				initializeForProject();
				setDefaultCursor();
			}
		};
		thread.start();
	}

	/**
	 * Initialize various elements like menus and toolbars
	 * and set up the users home view for the curent project.
	 */
	public void initializeForProject() {
		
		showDesktop();

		if (oModel != null) {
			oMenuManager.onDatabaseOpen();
			oToolBarManager.onDatabaseOpen();

			// get home view and nodes/links..
			setNodesAndLinks();

			// Create and initialize the outline View -Lakshmi 2/2/06
			createOutlineView();
			
			// Create and initialize the unread View -Lakshmi 6/27/06
			try {
				createUnreadView();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
			//set the trashbin icon
			setTrashBinIcon();

			refreshCodeAndMenuData();
			
			// Create the tags view if the user has requested it.
			createTagsView();			
		}
	}

	/**
	 * Load the codes and code groups into the model and refresh the Favorites and Workspace menus.
	 */
	private void refreshCodeAndMenuData() {

		// load codes for project into model
		loadAllCodes();

		// load code groups for project into model
		loadAllCodeGroups();

		// refile the codes dropdown
		oToolBarManager.updateCodeChoiceBoxData();

		// refresh Favorites menu
		refreshFavoritesMenu();

		// refresh Workspaces menu
		refreshWorkspaceMenu();

		// refreshWindowsMenu
		refreshWindowsMenu();
	}

	/**
	 * Makes a pass through all open views to see if any are dirty (i.e., have been modified by another person)
	 * This is called by the UIRefreshManager timed-refresh thread, so is running in the 'background'.
	 */
	public void checkProjectDirty() {

		boolean bInboxChecked = false;
		boolean bInboxDirty = false;

		if (!bReloadingProject && !bChecking && (oModel != null)) { //If project being manually reloaded or check already in progress then skip the timed refresh
			oToolBarManager.disableDataRefresh();					// Turn off the manual Refresh toolbar button while checking
			bChecking = true;										// Stops overlapping checking (can happen if timer is fast & connection is slow)
			JInternalFrame[] frames = getDesktop().getAllFrames();

			for(int i=0; i<frames.length; i++) {
				UIViewFrame viewFrame = (UIViewFrame)frames[i];
				View innerview = viewFrame.getView();
				if (innerview != getHomeView()) {					// Skip Home window since other people can't make it dirty
					try {
						if (innerview.isViewDirty()) {				// Had a dirty view, need to refresh the ViewFrame's contents...
							refreshViewFrame(viewFrame, innerview);
							if (innerview == getInBoxView()) {
								bInboxDirty = true;					// Flag to do an inbox pop-up after everything else is checked
							}
						}
					} catch (Exception ex) {}
				}
				if (innerview == getInBoxView()) bInboxChecked = true;
			}
			// Force the inbox to be examined in the case where the user did not have it open...
			if(!bInboxChecked) {
				try {
					if (getInBoxView().isViewDirty()) {
						bInboxDirty = true;
					}
				} catch (Exception ex) {}
			}
			if (bInboxDirty) JOptionPane.showMessageDialog(this, LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "ProjectCompendiumFrame.newNodeInbox")); //$NON-NLS-1$

			bChecking = false;
			oToolBarManager.enableDataRefresh();		// Turn the Refresh button back on
		}
	}
	
	/**
	 * Redraw the view.  This is called by checkProjectDirty() after the View data has been
	 * refreshed due to a groupware update by another user.
	 */
	private void refreshViewFrame(UIViewFrame viewFrame, View innerview) {

		setWaitCursor(viewFrame);

		if (viewFrame instanceof UIMapViewFrame) {
			int xPos = viewFrame.getHorizontalScrollBarPosition();
			int yPos = viewFrame.getVerticalScrollBarPosition();
			((UIMapViewFrame)viewFrame).createViewPane((View)innerview);
			viewFrame.setHorizontalScrollBarPosition(xPos, false);
			viewFrame.setVerticalScrollBarPosition(yPos, true);
		} else {													// Destroy and recreate the Frame from scratch.  There's got to be
			UIListViewFrame frame = (UIListViewFrame)viewFrame;		// a better way to refresh the List views, but I haven't found it yet....
			String title = innerview.getLabel();
			int width = frame.getWidth();
			int height = frame.getHeight();
			int xPos = frame.getX();
			int yPos = frame.getY();
			boolean isIcon = frame.isIcon();
			boolean isMaximum = frame.isMaximum();
			int hScroll = frame.getHorizontalScrollBarPosition();
			int vScroll = frame.getVerticalScrollBarPosition();

			oDesktop.getDesktopManager().closeFrame(viewFrame);
			oDesktop.remove(viewFrame);
			viewFrame.cleanUp();
			viewFrameList.remove(viewFrame);
			viewFrame.dispose();

			viewFrame = addViewToDesktop(innerview, title, width, height, xPos, yPos, isIcon, isMaximum, hScroll, vScroll);

			viewFrameList.add(viewFrame);

		}
		validateComponents();				// Probably not necessary, but....
		setDefaultCursor(viewFrame);
	}
	
	/**
	 * Clear all cached data and reload from the database.
	 */
	public void reloadProjectData() {

		bReloadingProject = true;

		if (oModel != null) {

			String sHomeWindowID = oHomeView.getId();

			UIViewFrame currentView = getCurrentFrame();

			setWaitCursor();

			Code.clearList();
			Link.clearList();
			NodeSummary.clearList();
			refreshCodeAndMenuData();
			viewFrameList.removeAllElements();

			String trashbinID = getTrashBinID();
			INodeService oNodeService = oModel.getNodeService();
			PCSession oSession = oModel.getSession();

			JInternalFrame[] frames = getDesktop().getAllFrames();
			for(int i=0; i<frames.length; i++) {
				UIViewFrame viewFrame = (UIViewFrame)frames[i];
				viewFrameList.addElement(viewFrame);

				View innerview = viewFrame.getView();
				try {
					innerview = (View)oNodeService.getView(oSession, innerview.getId());
				} catch (Exception ex) {}

				if (innerview != null) {
					innerview.initialize(oSession, oModel);

					if (innerview.getId().equals(sHomeWindowID)) oHomeView = innerview;

					viewFrame.setView(innerview);
					if (viewFrame instanceof UIMapViewFrame) {

						UIViewPane pane = ((UIMapViewFrame)viewFrame).getViewPane();
						UINode trashbin = (UINode)pane.get(trashbinID);

						try {
							innerview.reloadViewData();
						}
						catch(Exception io) {
							io.printStackTrace();
							System.out.flush();
						}

						if (trashbin != null) {
							innerview.addMemberNode(trashbin.getNodePosition());
						}
						int xPos = viewFrame.getHorizontalScrollBarPosition();
						int yPos = viewFrame.getVerticalScrollBarPosition();

						((UIMapViewFrame)viewFrame).createViewPane((View)innerview);

						viewFrame.setHorizontalScrollBarPosition(xPos, false);
						viewFrame.setVerticalScrollBarPosition(yPos, true);
					}
					else {
						UIListViewFrame frame = (UIListViewFrame)viewFrame;

						try {
							innerview.reloadViewData();
						}
						catch(Exception io) {}

						frame.createList(innerview);
						frame.getUIList().updateTable();
					}
				}
			}

			validateComponents();

			setDefaultCursor();
		}

		bReloadingProject = false;
	}

	/**
	 * Open the dialog to create a new Empty Database.
	 */
	public void onFileNew() {

		if (isProjectOpen(LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "ProjectCompendiumFrame.newProject"))) //$NON-NLS-1$
			return;

		if (FormatProperties.nDatabaseType == ICoreConstants.MYSQL_DATABASE) {
			UINewDatabaseDialog dialog = new UINewDatabaseDialog(this, vtProjects, oCurrentMySQLConnection.getLogin(), oCurrentMySQLConnection.getPassword(), oCurrentMySQLConnection.getServer());
			dialog.setVisible(true);
		}
		else {
			UINewDatabaseDialog dialog = new UINewDatabaseDialog(this, vtProjects, "", "", ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			dialog.setVisible(true);
		}
	}

	/**
	 * Refreshes the undo/redo buttons with the last action performed.
	 * @see com.compendium.ui.UIToolBarManager#refreshUndoRedo
	 * @see com.compendium.ui.UIMenuManager#refreshUndoRedo
	 */
	public void refreshUndoRedo(UndoManager oUndoManager) {
		oToolBarManager.refreshUndoRedo(oUndoManager);
		oMenuManager.refreshUndoRedo(oUndoManager);
	}

	public void onFileDatabaseAdmin() {

		if (isProjectOpen(LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "ProjectCompendiumFrame.databaseConnections"))) //$NON-NLS-1$
			return;


		UIDatabaseAdministrationDialog dlg = new UIDatabaseAdministrationDialog(this, FormatProperties.nDatabaseType, oCurrentMySQLConnection);
		UIUtilities.centerComponent(dlg, this);
		dlg.setVisible(true);
	}

	/**
	 * Open the dialog to convert a Compendium Derby database to Compendium MySQL Database.
	 */
	public void onFileConvertFromDerby() {

		if (isProjectOpen(LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "ProjectCompendiumFrame.projectConversion"))) //$NON-NLS-1$
			return;

		//UIConvertFromDerbyDatabaseDialog dialog = new UIConvertFromDerbyDatabaseDialog(ProjectCompendium.APP, adminDerbyDatabase.getProjectSchemaStatus(), oCurrentMySQLConnection, adminDerbyDatabase.getDatabaseProjects());
		try {
			UIConvertFromDerbyDatabaseDialog dialog = new UIConvertFromDerbyDatabaseDialog(ProjectCompendium.APP, oCurrentMySQLConnection, adminDerbyDatabase.getDatabaseProjects());
			UIUtilities.centerComponent(dialog, this);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Open the dialog to convert a Compendium MySQL database to Compendium Derby Database.
	 */
	public void onFileConvertFromMySQL() {

		if (isProjectOpen(LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "ProjectCompendiumFrame.projectConversion"))) //$NON-NLS-1$
			return;

		try {
			Vector connections = adminDerbyDatabase.getMySQLConnections();
			if (connections.size() > 0) {
				UIConvertFromMySQLDatabaseDialog dialog = new UIConvertFromMySQLDatabaseDialog(ProjectCompendium.APP, connections);
				UIUtilities.centerComponent(dialog, this);
				dialog.setVisible(true);
			}
			else {
				displayMessage(LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "ProjectCompendiumFrame.convertMessage1A")+"\n"+  //$NON-NLS-1$ //$NON-NLS-2$
						LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "ProjectCompendiumFrame.convertMessage1B")+"\n"+ //$NON-NLS-1$ //$NON-NLS-2$
						LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "ProjectCompendiumFrame.convertMessage1C")+"\n", //$NON-NLS-1$ //$NON-NLS-2$
						LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "ProjectCompendiumFrame.convertMessage1Title")); //$NON-NLS-1$ //$NON-NLS-2$
				UIDatabaseAdministrationDialog dlg = new UIDatabaseAdministrationDialog(this, ICoreConstants.MYSQL_DATABASE, null);
				UIUtilities.centerComponent(dlg, this);
				dlg.setVisible(true);
			}
		}
		catch(Exception ex) {
			System.out.println("Exception (ProjectCompendiumFrame.onFileConvertFromMySQL)\n\n"+ex.getMessage()); //$NON-NLS-1$
			displayError(LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "ProjectCompendiumFrame.connectionError")); //$NON-NLS-1$
		}
	}


	/**
	 * Open the Project management dialog.
	 */
	public void onDatabases() {

		if (isProjectOpen(LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "ProjectCompendiumFrame.projectManagement"))) //$NON-NLS-1$
			return;

		//Hashtable htProjectStatus = adminDatabase.getProjectSchemaStatus();
		if (FormatProperties.nDatabaseType == ICoreConstants.MYSQL_DATABASE) {
			//UIDatabaseManagementDialog dialog = new UIDatabaseManagementDialog(this, htProjectStatus, adminDatabase, vtProjects, oCurrentMySQLConnection.getLogin(), oCurrentMySQLConnection.getPassword(), oCurrentMySQLConnection.getServer());
			UIDatabaseManagementDialog dialog = new UIDatabaseManagementDialog(this, adminDatabase, vtProjects, oCurrentMySQLConnection.getLogin(), oCurrentMySQLConnection.getPassword(), oCurrentMySQLConnection.getServer());
			UIUtilities.centerComponent(dialog, this);
			dialog.setVisible(true);
		}
		else {
			//UIDatabaseManagementDialog dialog = new UIDatabaseManagementDialog(this, htProjectStatus, adminDatabase, vtProjects, "", "", "");
			UIDatabaseManagementDialog dialog = new UIDatabaseManagementDialog(this, adminDatabase, vtProjects, "", "", ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			UIUtilities.centerComponent(dialog, this);
			dialog.setVisible(true);
		}
	}
	
	/**
	 * Open the dialog to confirm marking the entire project as Seen.  This function is intended to be
	 * used when a new person joins a project and needs to 'catch up' on everything.
	 */

	public void onMarkProjectSeen() throws SQLException {
		if (isProjectClosed()) {
			long lNodeCount = ProjectCompendium.APP.getModel().getNodeService().lGetNodeCount(ProjectCompendium.APP.getModel().getSession());
			dlgMarkProjectSeen = new UIMarkProjectSeenDialog(this, lNodeCount);
			dlgMarkProjectSeen.setVisible(true);
		}
	}

	/**
	 * Open the dialog to backup the current database.
	 */
	public void onFileBackup() {
		if (FormatProperties.nDatabaseType == ICoreConstants.MYSQL_DATABASE) {
			UIDatabaseManagementDialog manager = new UIDatabaseManagementDialog(this, adminDatabase, oCurrentMySQLConnection.getLogin(), oCurrentMySQLConnection.getPassword(), oCurrentMySQLConnection.getServer());
			UIBackupDialog dialog = new UIBackupDialog(ProjectCompendium.APP, manager, sFriendlyName, oModel.getModelName(), UIDatabaseManagementDialog.RESUME_NONE, true);
			UIUtilities.centerComponent(dialog, ProjectCompendium.APP);
			dialog.setVisible(true);
		}
		else {
			UIDatabaseManagementDialog manager = new UIDatabaseManagementDialog(this, adminDatabase, "", "", ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			UIBackupDialog dialog = new UIBackupDialog(ProjectCompendium.APP, manager, sFriendlyName, oModel.getModelName(), UIDatabaseManagementDialog.RESUME_NONE, true);
			UIUtilities.centerComponent(dialog, ProjectCompendium.APP);
			dialog.setVisible(true);
		}
	}

	/**
	 * Open the connection Dialog.
	 * @param sType, the type of connection dialog to open.
	 */
	public void onConnect(String sType) {
		UIConnectionDialog dialog = new UIConnectionDialog( this, sType );
		UIUtilities.centerComponent(dialog, this);
		dialog.setVisible(true);
	}

	/**
	 * Imports a questmap file into a user selected view from the active project compendium model.
	 * @param showViewList, true to import into multiple views else false for current view.
	 */
	public void onFileImport(boolean showViewList) {

		dlgImport = new UIImportDialog(this, showViewList);
		if (!showViewList) {
			UIViewFrame viewFrame = getCurrentFrame();

			if (viewFrame instanceof UIMapViewFrame) {
				dlgImport.setViewPaneUI( ((UIMapViewFrame)viewFrame).getViewPane().getUI() );
			}
			else if (viewFrame instanceof UIListViewFrame) {
				if ( ((UIListViewFrame)viewFrame).getUIList() != null)
					dlgImport.setUIList( ((UIListViewFrame)viewFrame).getUIList() );
			}
		}

		dlgImport.setVisible(true);
	}

	/**
	 * Imports a folder of images into the active view as reference nodes
	 */
	public void onFileImportImageFolder() {
		onFileImportImageFolder(null);
	}

	/**
	 * Imports a folder of images into the given view as reference nodes.
	 *
	 * @param viewFrame com.compendium.ui.UIViewFrame, the viewFrame to import the images into.
	 * If null use the current view.
	 */
	public void onFileImportImageFolder(UIViewFrame viewFrame) {

		ImportImageFolder img = new ImportImageFolder();

		if (viewFrame == null)
			viewFrame = getCurrentFrame();

		if (viewFrame instanceof UIMapViewFrame) {
			if ( ((UIMapViewFrame)viewFrame).getViewPane() != null)
				img.setViewPaneUI( ((UIMapViewFrame)viewFrame).getViewPane().getUI() );
		}
		else if (viewFrame instanceof UIListViewFrame) {
			if ( ((UIListViewFrame)viewFrame).getUIList() != null)
				img.setUIList( ((UIListViewFrame)viewFrame).getUIList());
		}

		img.start();
	}

	/**
	 * Convenience Method to get the import profile
	 * @return Vector, the the import profile details.
	 */
	public Vector getImportProfile() {
		return oImportProfile.getProfile();
	}

	/**
	 * Convenience Method to set the import profile from the import dialog.
	 *
	 * @param normalImport, true to preserve the importing dates and authors, false to use current date and author.
	 * @param includeInDetail, true to include node details in the export.
	 * @param preserveIDs, true to preserve importing node ids.
	 * @param transclude, true to transclude importing nodes.
	 */
	public void setImportProfile(boolean normalImport, boolean includeInDetail, boolean preserveIDs, boolean transclude) {
		oImportProfile.setProfile(normalImport, includeInDetail, preserveIDs, transclude);
	}

	/**
	 * Opens the dialog to exports Compendium views to HTML outline files.
	 */
	public void onFileExportHTMLOutline() {

		dlgExport = new UIExportDialog(this, getCurrentFrame());
		UIUtilities.centerComponent(dlgExport, this);
		dlgExport.setVisible(true);
	}

	/**
	 * Opens the dialog to exports Compendium views to HTML View.
	 */
	public void onFileExportHTMLView() {

       	dialog2 = new UIExportViewDialog(this, getCurrentFrame());
		UIUtilities.centerComponent(dialog2, this);
		dialog2.setVisible(true);
	}

	/**
	 * Export to HTML Views with XML included.
	 */
	public void onFileExportPower() {
		
		htCheckDepth = new Hashtable(51);
		htChildrenAdded = new Hashtable(51);
		
		final UIViewFrame frame = getCurrentFrame();									
		final Vector selectedViews = getSelectedViews();
		
		if (selectedViews.size() == 0) {
			displayMessage(LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "ProjectCompendiumFrame.selectMap"), LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "ProjectCompendiumFrame.powereExport")); //$NON-NLS-1$ //$NON-NLS-2$
			return;
		} else {
			int count = 0;
			if (frame instanceof UIMapViewFrame) {
				UIViewPane uiViewPane = ((UIMapViewFrame)frame).getViewPane();
				count = uiViewPane.getNumberOfSelectedNodes();
			}
			else if (frame instanceof UIListViewFrame) {
				UIList uiList = ((UIListViewFrame)frame).getUIList();
				count = uiList.getNumberOfSelectedNodes();
			}
			
			if (count > 1) {
				displayMessage(LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "ProjectCompendiumFrame.exportMessage1a")+"\n\n"+ //$NON-NLS-1$ //$NON-NLS-2$
						LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "ProjectCompendiumFrame.exportMessage1b")+"\n",  //$NON-NLS-1$ //$NON-NLS-2$
						LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "ProjectCompendiumFrame.exportMessage1Title")); //$NON-NLS-1$ //$NON-NLS-2$
				return;							
			}
		}							
		
		UIFileFilter filter = new UIFileFilter(new String[] {"zip"}, "ZIP Files"); //$NON-NLS-1$ //$NON-NLS-2$

		UIFileChooser fileDialog = new UIFileChooser();
		fileDialog.setDialogTitle(LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "ProjectCompendiumFrame.enterFileName")); //$NON-NLS-1$
		fileDialog.setFileFilter(filter);
		fileDialog.setApproveButtonText(LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "ProjectCompendiumFrame.saveButton")); //$NON-NLS-1$
		fileDialog.setRequiredExtension(".zip"); //$NON-NLS-1$

		// FIX FOR MAC - NEEDS '/' ON END TO DENOTE A FOLDER
	    String exportPath = SystemProperties.defaultPowerExportPath;
	    int pathLen = exportPath.length();
	    if (!exportPath.substring(pathLen-1, pathLen).equals(ProjectCompendium.sFS)) {
	    	exportPath += ProjectCompendium.sFS;
	    }
	    File file = new File(exportPath);
	    if (file.exists()) {
			fileDialog.setCurrentDirectory(file);
		}

	    String sDirectory = ""; //$NON-NLS-1$
	    String fileName = ""; //$NON-NLS-1$
		int retval = fileDialog.showSaveDialog(ProjectCompendium.APP);
		if (retval == JFileChooser.APPROVE_OPTION) {
        	if ((fileDialog.getSelectedFile()) != null) {
            	fileName = fileDialog.getSelectedFile().getName();
				File fileDir = fileDialog.getCurrentDirectory();
				if (fileName != null) {
					if ( !fileName.toLowerCase().endsWith(".zip") ) { //$NON-NLS-1$
						fileName = fileName+".zip"; //$NON-NLS-1$
					}
					sDirectory = fileDir.getAbsolutePath();					
				}				
			}
		}
		
		if (fileName != null && !fileName.equals("")) { //$NON-NLS-1$
			final String fFileName = fileName;
			final String fsDirectory = sDirectory;
			Thread thread = new Thread("ProjectCompendium.APP.onFileExportHTMLViewWithXML") { //$NON-NLS-1$
				public void run() {
															
					// XML ZIP EXPORT
					setWaitCursor();
					
					boolean selectedOnly = true;
					boolean allDepths = true;
					boolean withStencilsAndLinkGroups = true;
					boolean withMovies = true;
					boolean withMeetings = false;		
					boolean toZip = true;
					
					String zipFileName = fFileName.replaceAll(".zip", "_xml.zip");		 //$NON-NLS-1$ //$NON-NLS-2$
					File xmlFile = new File(fsDirectory+ProjectCompendium.sFS+zipFileName);
					XMLExportNoThread export = new XMLExportNoThread(frame, xmlFile.getAbsolutePath(), allDepths, selectedOnly, toZip, withStencilsAndLinkGroups, withMovies, withMeetings, false);
					
					// OUTLINE ZIP EXPORT
					boolean bPrintNodeDetail = true;
					boolean bPrintNodeDetailDate = false;
					boolean bPrintAuthor = false;
					int nExportLevel = 2;
					//String sExportFile = fsDirectory+ProjectCompendium.sFS+fFileName.replaceAll(".zip", "_outline.zip");
					String sExportFile = fsDirectory+ProjectCompendium.sFS+fFileName;					
					File outlineFile = new File(sExportFile);					
					boolean bToZip = true;
					
					HTMLOutline oHTMLExport = new HTMLOutline(bPrintNodeDetail, bPrintNodeDetailDate, bPrintAuthor, nExportLevel, outlineFile.getAbsolutePath(), bToZip);
					oHTMLExport.setIncludeImage(true);
					oHTMLExport.setIncludeNodeAnchors(true);
					oHTMLExport.setIncludeDetailAnchors(true);
					oHTMLExport.setUseAnchorNumbers(false);
					oHTMLExport.setAnchorImage(UIExportDialog.sBaseAnchorPath+"anchor0.gif"); //$NON-NLS-1$
					oHTMLExport.setTitle(LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "ProjectCompendiumFrame.powerExport")); //$NON-NLS-1$
					oHTMLExport.setDisplayInDifferentPages(true);
					oHTMLExport.setDisplayDetailDates(false);					
					oHTMLExport.setHideNodeNoDates(false);
					oHTMLExport.setIncludeLinks(false);
					oHTMLExport.setIncludeNavigationBar(true);
					oHTMLExport.setInlineView(true);
					oHTMLExport.setNewView(false);
					oHTMLExport.setIncludeViews(true);
					oHTMLExport.setIncludeTags(true);
					oHTMLExport.setIncludeFiles(true);

					UIExportDialog dlg = new UIExportDialog(frame);
					boolean bSelectedViewsOnly = true;
					boolean bOtherViews = false;
					if (dlg.printExport(oHTMLExport, bOtherViews, bSelectedViewsOnly, nExportLevel)) {
						oHTMLExport.print();
					} else {
						displayError(LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "ProjectCompendiumFrame.errorOutline")); //$NON-NLS-1$
					}
										
					// WEB ZIP EXPORT
					String sUserTitle = ""; //$NON-NLS-1$
					boolean bIncludeReferences = true;
					boolean addMapTitles = true;
					boolean bOpenNew = true;
					bToZip = true;
					boolean bSortMenu = false;
					boolean bNoDetailPopup = false;
					boolean bNoDetailPopupAtAll = false;					
					HTMLViews htmlViews = new HTMLViews(fsDirectory, fFileName, sUserTitle, bIncludeReferences, bToZip, bSortMenu, addMapTitles, bOpenNew, bNoDetailPopup, bNoDetailPopupAtAll);
					htmlViews.processViewsWithXML(selectedViews, xmlFile.getAbsolutePath());
					
					setDefaultCursor();
					
					if (!ProjectCompendium.isLinux) {
						ExecuteControl.launch(fsDirectory);
					}
				}
			};
			thread.start();
		}				
	}
	
	/**
	 * Get the views to export depending on user options.
	 * Vector, the list of view to export.
	 */
	private Vector getSelectedViews() {

		Vector selectedViews = new Vector();
		Enumeration nodes = null;
		Vector vtTemp = new Vector();

		UIViewFrame currentFrame = this.getCurrentFrame();
		
		if (currentFrame instanceof UIMapViewFrame) {
			UIViewPane uiViewPane = ((UIMapViewFrame)currentFrame).getViewPane();
			nodes = uiViewPane.getSelectedNodes();
			for(Enumeration en = nodes; en.hasMoreElements();) {
				UINode uinode = (UINode)en.nextElement();
				if (uinode.getNode() instanceof View) {
					vtTemp.addElement(uinode.getNodePosition());
				}
			}
		}
		else if (currentFrame instanceof UIListViewFrame) {
			UIList uiList = ((UIListViewFrame)currentFrame).getUIList();
			nodes = uiList.getSelectedNodes();
			for(Enumeration en = nodes; en.hasMoreElements();) {
				NodePosition nodepos = (NodePosition)en.nextElement();
				if (nodepos.getNode() instanceof View) {
					vtTemp.addElement(nodepos);
				}
			}
		}

		//ADD THE CHILD VIEWS TO THE childViews VECTOR
		for(int j=0; j < vtTemp.size(); j++) {
			NodePosition nodePos = (NodePosition)vtTemp.elementAt(j);
			View innerview = (View)nodePos.getNode();
			selectedViews.addElement(innerview);
		}

		for (int i = 0; i < vtTemp.size(); i++) {
			NodePosition nodePos = (NodePosition)vtTemp.elementAt(i);
			View view = (View)nodePos.getNode();
			htCheckDepth.put((Object)view.getId(), view);
			selectedViews = getChildViews(view, selectedViews);
		}

		return selectedViews;
	}

	/**
	 * Return the child views for the given view.
	 * @param view com.compendium.code.datamodel.View, the view to return the child nodes to.
	 * @param childViews, the child views found.
	 */
	private Vector getChildViews(View view, Vector childViews) {
		try {
			Vector vtTemp = getModel().getViewService().getNodePositions(oModel.getSession(), view.getId());
			Vector nodePositionList = new Vector();

			//EXTRACT THE VIEWS AND ADD TO nodePositionList VECTOR
			for(Enumeration en = vtTemp.elements();en.hasMoreElements();) {
				NodePosition nodePos = (NodePosition)en.nextElement();
				NodeSummary node = nodePos.getNode();
				if (node instanceof View) {
					nodePositionList.addElement(nodePos);
				}
			}

			//SORT VIEWS VECTOR BY DECENDING Y POSITION
			for (int i = 0; i < nodePositionList.size(); i++) {
				int yPosition = ((NodePosition)nodePositionList.elementAt(i)).getYPos();
				for (int j = i+1; j < nodePositionList.size(); j++) {
					int secondYPosition = ((NodePosition)nodePositionList.elementAt(j)).getYPos();

					if (secondYPosition < yPosition) {
						NodePosition np = (NodePosition)nodePositionList.elementAt(i);
						nodePositionList.setElementAt(nodePositionList.elementAt(j), i);
						nodePositionList.setElementAt(np, j);
						yPosition = ((NodePosition)nodePositionList.elementAt(i)).getYPos();
					}
				}
			}

			//ADD THE CHILD VIEWS TO THE childViews VECTOR
			for (int k = 0; k < nodePositionList.size(); k++) {
				NodePosition np = (NodePosition)nodePositionList.elementAt(k);
				View innerview = (View)np.getNode();

				if (!htCheckDepth.containsKey((Object)innerview.getId())) {
					htCheckDepth.put((Object)innerview.getId(), innerview);
					childViews.addElement(np.getNode());
				}
			}

			//GET CHILD VIEWS CHILDREN
			for (int j = 0; j < nodePositionList.size(); j++) {
				NodePosition np = (NodePosition)nodePositionList.elementAt(j);
				View innerview = (View)np.getNode();
				if (!htChildrenAdded.containsKey((Object)innerview.getId())) {
					htChildrenAdded.put((Object)innerview.getId(), innerview);
					childViews = getChildViews(innerview, childViews);
				}
			}
		}
		catch (Exception e) {
			ProjectCompendium.APP.displayError("Exception: (ProjectCompendiumFrame.getChildViews) \n\n" + e.getMessage()); //$NON-NLS-1$
		}

		return childViews;
	}
	

// XML IMPORT AND EXPORT

	/**
	 * Imports an XML file into the current view.
	 */
	public void onFileXMLImport() {

		dlgImportXML = new UIImportXMLDialog(this);
		UIViewFrame viewFrame = getCurrentFrame();

		if (viewFrame instanceof UIMapViewFrame) {
			if ( ((UIMapViewFrame)viewFrame).getViewPane() != null) {
				dlgImportXML.setViewPaneUI( ((UIMapViewFrame)viewFrame).getViewPane().getUI());
			}
		}
		else {
			if ( ((UIListViewFrame)viewFrame).getUIList() != null)
				dlgImportXML.setUIList( ((UIListViewFrame)viewFrame).getUIList());
		}
		dlgImportXML.setVisible(true);
	}

	/**
	 * Imports an XML file into a user selected view from the active project
	 * compendium model from the given filename.
	 * @param file, the file to import.
	 */
	public void onFileXMLImport(File file) {

		dlgImportXML = new UIImportXMLDialog(this, file);
		UIViewFrame viewFrame = getCurrentFrame();

		if (viewFrame instanceof UIMapViewFrame) {
			if ( ((UIMapViewFrame)viewFrame).getViewPane() != null) {
				dlgImportXML.setViewPaneUI( ((UIMapViewFrame)viewFrame).getViewPane().getUI() );
			}
		}
		else if ( ((UIListViewFrame)viewFrame).getUIList() != null) {
			dlgImportXML.setUIList( ((UIListViewFrame)viewFrame).getUIList() );
		}
		dlgImportXML.setVisible(true);
	}

	/**
	 * Imports an XML file into a user selected view as a Template, from the given filename.
	 * @param sXMLFile, the file to import.
	 */
	public void onTemplateImport(String sXMLFile) {

		UIViewFrame frame = ProjectCompendium.APP.getCurrentFrame();
		if (frame instanceof UIMapViewFrame) {
			UIMapViewFrame mapFrame = (UIMapViewFrame)frame;
			UIViewPane oViewPane = mapFrame.getViewPane();
			onTemplateImport(sXMLFile, oViewPane);
		} else {
			UIListViewFrame listFrame = (UIListViewFrame)frame;
			UIList uiList = listFrame.getUIList();
			onTemplateImport(sXMLFile, uiList);
		}
	}		
	
	/**
	 * Imports an XML file into a user selected view as a Template, from the given filename.
	 * @param sXMLFile, the file to import.
	 */
	public void onTemplateImport(String sXMLFile, UIViewPane oViewPane) {

		boolean importAuthorAndDate = false;
		boolean includeOriginalAuthorDate = false;
		boolean preserveIDs = false;
		boolean transclude = false;
		boolean updateTranscludedNodes = false;
		boolean markSeen = true;

		File oXMLFile = new File(sXMLFile);
		if (oXMLFile.exists()) {
			DBNode.setImportAsTranscluded(transclude);
			DBNode.setPreserveImportedIds(preserveIDs);
			DBNode.setUpdateTranscludedNodes(updateTranscludedNodes);
			DBNode.setNodesMarkedSeen(markSeen);			
			
			if (oViewPane != null) {
				ViewPaneUI oViewPaneUI = oViewPane.getUI();
				if (oViewPaneUI != null) {
					oViewPaneUI.setSmartImport(importAuthorAndDate);
					oViewPaneUI.onImportXMLFile(sXMLFile, includeOriginalAuthorDate);
				}
			}
		}
	}	
	
	/**
	 * Imports an XML file into a user selected view as a Template, from the given filename.
	 * @param sXMLFile the file to import.
	 * @param uiList the list to import the data into.
	 */
	public void onTemplateImport(String sXMLFile, UIList uiList) {

		boolean importAuthorAndDate = false;
		boolean includeOriginalAuthorDate = true;
		boolean preserveIDs = false;
		boolean transclude = false;
		boolean updateTranscludedNodes = false;
		boolean markSeen = true;

		File oXMLFile = new File(sXMLFile);
		if (oXMLFile.exists()) {
			DBNode.setImportAsTranscluded(transclude);
			DBNode.setPreserveImportedIds(preserveIDs);
			DBNode.setUpdateTranscludedNodes(updateTranscludedNodes);
			DBNode.setNodesMarkedSeen(markSeen);			

			if (uiList != null) {
				uiList.getListUI().setSmartImport(importAuthorAndDate);
				uiList.getListUI().onImportXMLFile(sXMLFile, includeOriginalAuthorDate);
			}
		}
	}			
	/**
	 * Exports a user selected view to an XML file.
	 * @param multipleViews, false if exporting the current view, true is exporting multiple views.
	 */
	public void onFileXMLExport(boolean multipleViews) {

		dlgExportXML = new UIExportXMLDialog(this);

		if (!multipleViews) {
			UIViewFrame viewFrame = getCurrentFrame();
			dlgExportXML.setCurrentView(viewFrame);
		}

		dlgExportXML.setVisible(true);
	}

	//printScreenCode?? Might be useful to know?
	//throws both AWTException and IOException
	//BufferedImage image = new Robot().createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));	
	
	/**
	 * Save the current map as a JPEG.
	 */
	public void onSaveAsJpeg() {

		UIViewFrame frame = getCurrentFrame();

		if (frame instanceof UIMapViewFrame) {
			try {
				UIFileFilter jpgFilter = new UIFileFilter(new String[] {"jpg"}, "JPEG Image Files"); //$NON-NLS-1$ //$NON-NLS-2$

				UIFileChooser fileDialog = new UIFileChooser();
				fileDialog.setDialogTitle(LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "ProjectCompendiumFrame.enterFileName2")); //$NON-NLS-1$
				fileDialog.setFileFilter(jpgFilter);
				fileDialog.setDialogType(JFileChooser.SAVE_DIALOG);
				fileDialog.setRequiredExtension(".jpg"); //$NON-NLS-1$

	    		// FIX FOR MAC - NEEDS '/' ON END TO DENOTE A FOLDER
	    		// AND MUST USE ABSOLUTE PATH, AS RELATIVE PATH REMOVES THE '/'
	    		File filepath = new File(""); //$NON-NLS-1$
	    		String sPath = filepath.getAbsolutePath();
	    		File file = new File(sPath+ProjectCompendium.sFS+"Exports"+ProjectCompendium.sFS); //$NON-NLS-1$
	    		if (file.exists()) {
					fileDialog.setCurrentDirectory(file);
				}

				String fileName = ""; //$NON-NLS-1$
				UIUtilities.centerComponent(fileDialog, this);
				int retval = fileDialog.showDialog(this, null);

	    		if (retval == JFileChooser.APPROVE_OPTION) {
                	if ((fileDialog.getSelectedFile()) != null) {

                    	fileName = fileDialog.getSelectedFile().getAbsolutePath();

						if (fileName != null) {
							if ( !fileName.toLowerCase().endsWith(".jpg") ) { //$NON-NLS-1$
								fileName += ".jpg"; //$NON-NLS-1$
							}
						}

						UIViewPane pane = ((UIMapViewFrame)frame).getViewPane();
						Dimension size = pane.calculateSize();

						BufferedImage img = (pane.getGraphicsConfiguration()).createCompatibleImage(size.width, size.height, Transparency.OPAQUE);
						Graphics2D graphics = img.createGraphics();
						pane.paint(graphics);

						if (ProjectCompendium.isLinux) {
							Iterator iter = ImageIO.getImageWritersByFormatName("JPG"); //$NON-NLS-1$
							if (iter.hasNext()) {
								ImageWriter writer = (ImageWriter)iter.next();
								ImageWriteParam iwp = writer.getDefaultWriteParam();
								iwp.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
								iwp.setCompressionQuality(1);
								File outFile = new File(fileName);
								FileImageOutputStream output = new FileImageOutputStream(outFile);
								writer.setOutput(output);
								IIOImage image = new IIOImage(img, null, null);
								writer.write(null, image, iwp);
							}
						}
						else {

							BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(fileName));
							JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
							JPEGEncodeParam param = encoder.getDefaultJPEGEncodeParam(img);

							param.setQuality(1.0f, false);

							encoder.setJPEGEncodeParam(param);
							encoder.encode(img);
							out.close();
						}
                  	}
               	}
			}
			catch(Exception ex) {
				ex.printStackTrace();
				System.out.println("Exception creating map image = "+ex.getMessage()); //$NON-NLS-1$
			}
		}
	}

	/**
	 * Setup page options for printing - CURRENTLY DOES NOTHING.
	 */
	public void onFilePageSetup() {}

	/**
	 * Prints the current active frame.
	 * @see #onPrint
	 */
	public void onFilePrint() {
		//get the active internal frame to print
		//UIViewFrame viewFrame = getCurrentFrame();
		onPrint();
	}

	/**
	 * Exits the application, and close connections and open frames.
	 */
	public void onExit() {
		
		int screenX = getX();
		int screenY = getY();
		int screenWidth = getWidth();
		int screenHeight = getHeight();
		FormatProperties.setFormatProp("lastScreenWidth", new Integer(screenWidth).toString()); //$NON-NLS-1$
		FormatProperties.setFormatProp("lastScreenHeight", new Integer(screenHeight).toString()); //$NON-NLS-1$
		FormatProperties.setFormatProp("lastScreenX", new Integer(screenX).toString()); //$NON-NLS-1$
		FormatProperties.setFormatProp("lastScreenY", new Integer(screenY).toString()); //$NON-NLS-1$
		FormatProperties.saveFormatProps();

		setVisible(false);

		// SAVE CURRENT OPEN WINDOW PROPERTIES
		if(oDesktop != null && oModel != null) {

			// close all internal frames
			JInternalFrame[] frames = oDesktop.getAllFrames();
			for (int i = 0; i < frames.length; i++) {
				UIViewFrame viewframe = (UIViewFrame)frames[i];
				saveViewProperties(viewframe);
			}
		}

        if (oToolBarManager != null) {
			oToolBarManager.saveToolBarData();
        }

		// Close any connections
		closeJabberConnection();
		closeIXPanelConnection();

		cleanupServices();
		DBConnectionManager.shutdownDerby(FormatProperties.nDatabaseType);

		SaveOutput.stop();
		dispose();

        if (createdRunningFile) {
            try {
                File runningFile = new File(RUNNING_FILE);
                FileInputStream input = new FileInputStream(runningFile);
                FileLock lock =
                    input.getChannel().lock(0, runningFile.length(), true);
                BufferedReader reader =
                    new BufferedReader(new InputStreamReader(input));
                Vector instances = new Vector();
                String line = reader.readLine();
                while (line != null) {
                    instances.add(line);
                    line = reader.readLine();
                }
                lock.release();
                reader.close();
                input.close();
                while (instances.contains(ProjectCompendium.sHOMEPATH)) {
                    instances.remove(ProjectCompendium.sHOMEPATH);
                }
                if (instances.size() == 0) {
                    CoreUtilities.deleteFile(runningFile);
                } else {
                    FileOutputStream output =
                        new FileOutputStream(runningFile);
                    lock = output.getChannel().lock(0, runningFile.length(),
                            false);
                    PrintWriter writer = new PrintWriter(output);
                    for (int i = 0; i < instances.size(); i++) {
                        writer.println((String) instances.get(i));
                    }
                    lock.release();
                    writer.close();
                    output.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

		System.gc();
		System.exit(0);
	}


// CLAIMAKER

	/**
	 * Set the ClaiMaker connection string.
	 *
	 * @param server, the ClaiMaker server string.
	 */
	public void openClaiMakerConnection(String server) {
		claiMakerServer = server;
		claiMakerConnected = true;
	}

	/**
	 * Has a ClaiMaker server string been set?
	 * @return boolean, true if the string has been set, else false.
	 */
	public boolean isClaiMakerConnected() {
		return claiMakerConnected;
	}

	/**
	 * Return the ClaiMaker server String.
	 * @return the ClaiMaker server String.
	 */
	public String getClaiMakerServer() {
		return claiMakerServer;
	}

	/**
	 * Set the ClaiMaker connection to false.
	 */
	public void closeClaiMakerConnection() {
		claiMakerConnected = false;
	}


// JABBER METHODS

	/**
	 * Open a Jabber connection for the given jid details.
	 *
	 * @param server, the jabber server to connect to.
	 * @param username, the username of the account to connect to.
	 * @param password, the password to use to connect.
	 */
	public void openJabberConnection(String server, String username, String password) {
		jabber = new Jabber();
		jabber.connect(server, username, password, oModel.getMyIP());
	}

	/**
	 * Return if an Jabber connection is open.
	 * @return boolean, true if the connection if open, else false.
	 */
	public boolean isJabberConnected() {
		if (jabber != null)
			return true;
		return false;
	}

	/**
	 * Called once a Jabber connection has been successfully opened.
	 */
	public void jabberConnectionOpened() {
		JOptionPane.showMessageDialog((Component)this, (Object)new String("Jabber "+LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "ProjectCompendiumFrame.connection")),  //$NON-NLS-1$ //$NON-NLS-2$
				"Jabber "+LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "ProjectCompendiumFrame.jabberConnectionTitle"), JOptionPane.PLAIN_MESSAGE); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * Close the Jabber connection if open.
	 */
	public void closeJabberConnection() {
		if (jabber != null) {
			jabber.destroy();
			jabber = null;
		}
        if (oMenuManager != null) {
		    oMenuManager.setJabberMenuEnablement(false);
        }
	}

	/**
	 * Disable the Jabber roster menu.
	 */
	public void disableJabberMenu() {
		oMenuManager.setJabberMenuEnablement(true);
	}

	/**
	 * Send the selected nodes in the current view to the given Jabber account jid.
	 *
	 * @param jid, the jabber id of the Jabber account to send the nodes to.
	 */
	public void toJabber(JID jid) {

		UIViewFrame viewFrame = getCurrentFrame();
		try {
			Enumeration nodes = null;

			if ( viewFrame instanceof UIMapViewFrame) {
				UIViewPane view = ((UIMapViewFrame)viewFrame).getViewPane();
				nodes = view.getSelectedNodes();
				for(Enumeration e = nodes; e.hasMoreElements();) {
					UINode node = (UINode)e.nextElement();
					processNodeToJabber(jid, node.getNode());
				}
			}
			else {
				nodes = ((UIListViewFrame)viewFrame).getUIList().getSelectedNodes();
				for(Enumeration e = nodes; e.hasMoreElements();) {
					NodePosition node = (NodePosition)e.nextElement();
					processNodeToJabber(jid, node.getNode());
				}
			}
		}
		catch(Exception ex) {
			displayError("Exception: (ProjectCompendium.toJabber) \n" + ex.getMessage()); //$NON-NLS-1$
		}
	}

	/**
	 * Send the given node info to the given Jabber account jid.
	 *
	 * @param jid, the jabber id of the IX Panel to send the node to.
	 * @param node com.compendium.core.datamodelNodeSummary, the node to send.
	 */
	public void processNodeToJabber(JID jid, NodeSummary node) {

		int type = node.getType();

		IModel model = getModel();
		String author = model.getUserProfile().getUserName();

		String label = CoreUtilities.cleanXMLText(node.getLabel());
		String detail = CoreUtilities.cleanXMLText(node.getDetail());

		String message = author+" "+LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "ProjectCompendiumFrame.says")+": \n"+label; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		if (detail != null && !detail.equals("") && !detail.equals(ICoreConstants.NODETAIL_STRING) ) //$NON-NLS-1$
			message += "\n\n   "+detail; //$NON-NLS-1$

		/*while(waitingToSend);

		if (!secondConnection) {
			secondConnection = true;
			this.jid = jid;
			this.body = message;
			waitingToSend = true;

			jabber = null;
			jabber = new Jabber();
			jabber.connect();
		}
		else {*/
			if (jabber != null)
				jabber.sendMessage(jid, message);
		//}
	}

	/**
	 * Open a jabber message dialog to display the message sent from a Jabber account.
	 * @param from, the string of the nickname of the person who sent the message.
	 * @param messge, the message to process.
	 * @param type, the type of the message.
	 */
	public void displayJabberReply(String from, String message, String type) {

		message = message.trim();
		if (message.length() > 0) {
			final String fmessage = message;
			final String ftype = type;
			final String ffrom = from;
			//Thread thread = new Thread("Jabber Message") {
			//	public void run() {
					UIJabberMessageDialog dialog = new UIJabberMessageDialog(ProjectCompendium.APP, ffrom, fmessage, ftype);
					dialog.setVisible(true);
			//	}
			//};
			//thread.start();
		}
	}

	/**
	 * Create the Jabber roster menu items.
	 */
	public void createJabberRoster() {
		drawJabberRoster(null, null);
	}

	/**
	 * Create the Jabber roster menu items for the given menu.
	 * @param menu, the menu to add the roster items to.
	 */
	public void drawJabberRoster(JMenu menu) {
		drawJabberRoster(menu, null);
	}

	/**
	 * Create the Jabber roster menu items for the given menu.
	 * @param menu, the menu to add the roster items to.
	 * @param node com.compendium.code.datamodel.NodeSummary, the node associated with this menu.
	 * if the menu if on a node right-click menu.
	 */
	public void drawJabberRoster(JMenu menu, NodeSummary node) {

		if (jabber == null)
			return;

		Enumeration rosterEntries = jabber.getRoster();
		oMenuManager.drawJabberRoster(menu, node, rosterEntries);
	}

// IX METHODS
	/**
	 * Open a Jabber IX Panel connection for the given jid details.
	 *
	 * @param server, the jabber server to connect to.
	 * @param username, the username of the account to connect to.
	 * @param password, the password to use to connect.
	 */
	public void openIXPanelConnection(String server, String username, String password) {
		ixPanel = new IXPanel();
		ixPanel.connect(server, username, password, oModel.getMyIP());
	}

	/**
	 * Called once a Jabber IX Panel connection has been successfully opened.
	 */
	public void ixPanelConnectionOpened() {
		JOptionPane.showMessageDialog((Component)this, (Object)new String("IX Panel "+LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "ProjectCompendiumFrame.connnectionOpen")),  //$NON-NLS-1$ //$NON-NLS-2$
				"IX Panel "+LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "ProjectCompendiumFrame.connectionTitle"), JOptionPane.PLAIN_MESSAGE); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * Close the Jabber IX Panel connection if open.
	 */
	public void closeIXPanelConnection() {
		if (ixPanel != null) {
			ixPanel.destroy();
			ixPanel = null;
		}
        if (oMenuManager != null) {
		    oMenuManager.setIXMenuEnablement(false);
        }
	}

	/**
	 * Return if an Jabber IX Panel connection is open.
	 * @return boolean, true if the connection if open, else false.
	 */
	public boolean isIXConnected() {
		if (ixPanel != null)
			return true;
		return false;
	}

	/**
	 * Disable the IX roster menu.
	 */
	public void disableIXMenu() {
		oMenuManager.setIXMenuEnablement(false);
	}

	/**
	 * Send the selected nodes in the current view to the given IX Panel jid.
	 *
	 * @param jid, the jabber id of the IX Panel to send the nodes to.
	 */
	public void toIXPanel(JID jid) {

		UIViewFrame viewFrame = getCurrentFrame();
		try {
			Enumeration nodes = null;

			if ( viewFrame instanceof UIMapViewFrame) {
				UIViewPane view = ((UIMapViewFrame)viewFrame).getViewPane();
				nodes = view.getSelectedNodes();
				for(Enumeration e = nodes; e.hasMoreElements();) {
					UINode node = (UINode)e.nextElement();
					processNodeToIX(jid, node.getNode());
				}
			}
			else {
				nodes = ((UIListViewFrame)viewFrame).getUIList().getSelectedNodes();
				for(Enumeration e = nodes; e.hasMoreElements();) {
					NodePosition node = (NodePosition)e.nextElement();
					processNodeToIX(jid, node.getNode());
				}
			}
		}
		catch(Exception ex) {
			displayError("Exception: (ProjectCompendium.toIXPanel) \n" + ex.getMessage()); //$NON-NLS-1$
		}
	}

	/**
	 * Send the given node info to the given IX Panel jid.
	 *
	 * @param jid, the jabber id of the IX Panel to send the node to.
	 * @param node com.compendium.core.datamodelNodeSummary, the node to send.
	 */
	public void processNodeToIX(JID jid, NodeSummary node) {

		int type = node.getType();
		String label = CoreUtilities.cleanXMLText(node.getLabel());
		String detail = CoreUtilities.cleanXMLText(node.getDetail());

		IModel model = getModel();
		String author = model.getUserProfile().getUserName();

		//String message = author+" says: \n   "+label;

		String message = label;

		//if (detail != null && !detail.equals("") && !detail.equals(ICoreConstants.NODETAIL_STRING) )
			//	message += "\n\n   "+detail;

		StringBuffer ixMessage = new StringBuffer(500);

		ixMessage.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"); //$NON-NLS-1$
		ixMessage.append("<issue status=\"blank\" priority=\"normal\" sender-id=\""+ixPanel.getSender()+"\">\n"); //$NON-NLS-1$ //$NON-NLS-2$
		ixMessage.append("<pattern>\n"); //$NON-NLS-1$
		ixMessage.append("<list>\n"); //$NON-NLS-1$
		ixMessage.append("\t\t<symbol>"+message+"</symbol>\n"); //$NON-NLS-1$ //$NON-NLS-2$
		ixMessage.append("\t</list>\n"); //$NON-NLS-1$
		ixMessage.append("</pattern>\n"); //$NON-NLS-1$
		ixMessage.append("</issue>\n"); //$NON-NLS-1$

		/*while(waitingToSendIX);

		if (!secondIXConnection) {
			secondIXConnection = true;
			this.jidIX = jid;
			this.bodyIX = ixMessage.toString();
			waitingToSendIX = true;

			ixPanel = null;
			ixPanel = new IXPanel();
			ixPanel.connect();
		}
		else {*/
			if (ixPanel != null)
				ixPanel.sendMessage(jid, ixMessage.toString());
		//}
	}

	/**
	 * Create the IX roster menu items.
	 */
	public void createIXRoster() {
		drawIXRoster(null, null);
	}

	/**
	 * Create the IX roster menu items for the given menu.
	 * @param menu, the menu to add the roster items to.
	 */
	public void drawIXRoster(JMenu menu) {
		drawIXRoster(menu, null);
	}

	/**
	 * Create the IX roster menu items for the given menu.
	 * @param menu, the menu to add the roster items to.
	 * @param node com.compendium.code.datamodel.NodeSummary, the node associated with this menu.
	 * if the menu if on a node right-click menu.
	 */
	public void drawIXRoster(JMenu menu, NodeSummary node) {

		if (ixPanel == null)
			return;

		Enumeration rosterEntries = ixPanel.getRoster();
		oMenuManager.drawIXRoster(menu, node, rosterEntries);
	}

//****************** EDIT MENU **********************/

	/**
	 * Undo the previous edit if any
	 */
	public void onEditUndo() {
	
		UIViewFrame viewFrame = getCurrentFrame();
		viewFrame.onUndo();
		
		setTrashBinIcon();
		
		refreshIconIndicators();
	}

	/**
	 * Redo the previous undo if any
	 */
	public void onEditRedo() {
	
		//get the active frame which will give the view to be searched
		UIViewFrame viewFrame = getCurrentFrame();
		viewFrame.onRedo();

		setTrashBinIcon();

		refreshIconIndicators();
	}

	/**
	 * Cuts the selected nodes and links from the current view and places it on the clipboard
	 */

	public void onEditCut() {
		//get the active frame which will give the view to be searched
		UIViewFrame viewFrame = getCurrentFrame();

		startWaitCursor(viewFrame);

		if (viewFrame instanceof UIListViewFrame) {
			((UIListViewFrame)viewFrame).getUIList().getListUI().cutToClipboard();
		} else if (viewFrame instanceof UIMapViewFrame) {
			( ((UIMapViewFrame)viewFrame).getViewPane().getUI() ).cutToClipboard(null);
		} 

		stopWaitCursor(viewFrame);
		
	}

	/**
	 * Copies the selected nodes and links to the clipboard.
	 */
	public void onEditCopy() {

		//get the active frame which will give the view to be searched
		UIViewFrame viewFrame = getCurrentFrame();

		startWaitCursor(viewFrame);

		if (viewFrame instanceof UIListViewFrame) {
			((UIListViewFrame)viewFrame).getUIList().getListUI().copyToClipboard();
		} else if (viewFrame instanceof UIMapViewFrame) {
			( ((UIMapViewFrame)viewFrame).getViewPane().getUI() ).copyToClipboard(null);
		}

		stopWaitCursor(viewFrame);
	}

	/**
	 * Copies the selected nodes and links to the clipboard with full map depth for pasting to another database
	 */
	public void onEditExternalCopy() {

		externalCopy = true;
		String userID = oModel.getUserProfile().getId();

		//get the active frame which will give the view to be searched
		UIViewFrame viewFrame = getCurrentFrame();

		startWaitCursor(viewFrame);

		if (viewFrame instanceof UIListViewFrame) {
			((UIListViewFrame)viewFrame).getUIList().getListUI().externalCopyToClipboard();		
		} else if (viewFrame instanceof UIMapViewFrame) {
			( ((UIMapViewFrame)viewFrame).getViewPane().getUI() ).externalCopyToClipboard(null, userID);
		}

		stopWaitCursor(viewFrame);
	}

	/**
	 * Pastes the contents of the clipboard into the current view (when copied from another database).
	 */
	public void onEditExternalPaste() {
		// USED ELSE WHERE FOR LOOP PREVENTION IN VIEWS CONTAINING THEMSELVES IN THEIR CHILD TREE
		ht_pasteCheck.clear();
		externalCopy = false;

		//get the active frame which will give the view to be searched
		UIViewFrame viewFrame = getCurrentFrame();

		startWaitCursor(viewFrame);

		if (viewFrame instanceof UIListViewFrame) {
			((UIListViewFrame)viewFrame).getUIList().getListUI().externalPasteFromClipboard();
		} else if (viewFrame instanceof UIMapViewFrame) {
			( ((UIMapViewFrame)viewFrame).getViewPane().getUI() ).externalPasteFromClipboard();
		}
		stopWaitCursor(viewFrame);

		oMenuManager.setExternalPasteEnablement(false);

		setTrashBinIcon();
	}

	/**
	 * Pastes the contents of the clipboard into the current view.
	 */
	public void onEditPaste() {
		// USED ELSE WHERE FOR LOOP PREVENTION IN VIEWS CONTAINING THEMSELVES IN THEIR CHILD TREE
		ht_pasteCheck.clear();

		//get the active frame which will give the view to be searched
		UIViewFrame viewFrame = getCurrentFrame();

		startWaitCursor(viewFrame);

		if (viewFrame instanceof UIListViewFrame) {
			((UIListViewFrame)viewFrame).getUIList().getListUI().pasteFromClipboard();
		} else if (viewFrame instanceof UIMapViewFrame) {
			( ((UIMapViewFrame)viewFrame).getViewPane().getUI() ).pasteFromClipboard();
			if (oAerialViewDialog != null)
				oAerialViewDialog.scaleToFit(); // will refresh aerial view after paste
		}

		stopWaitCursor(viewFrame);

		setTrashBinIcon();
	}

	/**
	 * Delete the selected nodes and links in the current view to the clipboard
	 */
	public void onEditDelete() {

		isNewDelete = true;
		if(UIViewOutline.me != null && UIViewOutline.me.getTree().isFocusOwner()){
			setWaitCursor();
			UIViewOutline.me.onDelete();
			setDefaultCursor();
		} else {
		
			// get the active frame which will give the view to be searched
			UIViewFrame viewFrame = getCurrentFrame();
			startWaitCursor(viewFrame);
			if (viewFrame instanceof UIListViewFrame) {
				((UIListViewFrame)viewFrame).getUIList().getListUI().onDelete();
			} else if (viewFrame instanceof UIMapViewFrame) {
				( ((UIMapViewFrame)viewFrame).getViewPane().getUI() ).onDelete();
			}
	
			stopWaitCursor(viewFrame);
		}
		isNewDelete = false;
	}

	/**
	 * Selects All the nodes and links.
	 */
	public void onEditSelectAll() {

		//get the active frame which will give the view to be searched
		UIViewFrame viewFrame = getCurrentFrame();

		if (viewFrame instanceof UIListViewFrame) {
			((UIListViewFrame)viewFrame).getUIList().getListUI().onSelectAll();
		} else if (viewFrame instanceof UIMapViewFrame) {
			((UIMapViewFrame)viewFrame).getViewPane().selectAll();
		}
	}

	/**
	 * Image rollover status.
	 * @param state, true to turn on image rollover, false to turn it off.
	 */
	public void onImageRollover(boolean state) {

		FormatProperties.imageRollover = state;

		if (FormatProperties.imageRollover) {
			FormatProperties.setFormatProp( "imageRollover", "true" ); //$NON-NLS-1$ //$NON-NLS-2$
		}
		else {
			FormatProperties.setFormatProp( "imageRollover", "false" ); //$NON-NLS-1$ //$NON-NLS-2$
		}

		FormatProperties.saveFormatProps();

		oToolBarManager.updateImageRollover(FormatProperties.imageRollover);
		oMenuManager.updateImageRollover(FormatProperties.imageRollover);
	}

	/**
	 * Opent the search dialog.
	 */
	public void onSearch() {

		if (oModel == null) {
   			int answer = JOptionPane.showConfirmDialog(this, LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "ProjectCompendiumFrame.errorSearchA")+"\n\n"+ //$NON-NLS-1$ //$NON-NLS-2$
   					LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "ProjectCompendiumFrame.errorSearchB")+"\n\n",  //$NON-NLS-1$ //$NON-NLS-2$
   					LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "ProjectCompendiumFrame.errorSearchTitle"), //$NON-NLS-1$ //$NON-NLS-2$
   						JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

			if (answer == JOptionPane.YES_OPTION) {
				onFileOpen();
			}
			return;
		}

		//get the active frame which will give the view to be searched
		UIViewFrame viewFrame = getCurrentFrame();

		UISearchDialog dialog = new UISearchDialog(ProjectCompendium.APP, viewFrame.getView());
		UIUtilities.centerComponent(dialog, ProjectCompendium.APP);
		dialog.setVisible(true);
	}

	/**
	 * Set the cursor to the wait cursor for the given frame.
	 * @param frame com.compendium.ui.UIViewFrame, the frame to set the cursor for.
	 */
	public void startWaitCursor(UIViewFrame frame) {

		final UIViewFrame viewFrame = frame;
		Thread thread = new Thread("Start Cursor") { //$NON-NLS-1$
			public void run() {
				viewFrame.setCursor(new Cursor(java.awt.Cursor.WAIT_CURSOR));
				ProjectCompendium.APP.setWaitCursor();
			}
		};
		thread.start();
		Thread.currentThread().yield();
	}

	/**
	 * Set the cursor to the default cursor for the given frame.
	 * @param frame com.compendium.ui.UIViewFrame, the frame to set the cursor for.
	 */
	public void stopWaitCursor(UIViewFrame frame) {

		final UIViewFrame viewFrame = frame;
		Thread thread = new Thread("Stop Cursor") { //$NON-NLS-1$
			public void run() {
				viewFrame.setCursor(new Cursor(java.awt.Cursor.DEFAULT_CURSOR));
				ProjectCompendium.APP.setDefaultCursor();
			}
		};
		thread.start();
		Thread.currentThread().yield();
	}

//*************** MAP MENU ******************//

	/**
	 * Displays a dialog to select the map the user wants to view.
	 */
	public void onViewMap() {
		UISelectViewDialog dlgView = new UISelectViewDialog(this);
		UIUtilities.centerComponent(dlgView, this);
		dlgView.setVisible(true);
	}

	/**
	 * Displays a dialog to view/select nodes in limbo - not assigned to a view.
	 */
	public void onLimboNode() {

		setWaitCursor();
		Vector limboNodes = new Vector(51);
		try {
			limboNodes = ((NodeService)oModel.getNodeService()).getLimboNodes(oModel.getSession());
		}
		catch(Exception io) {

		}
		UISearchResultDialog dlgView = new UISearchResultDialog(this, limboNodes, LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "ProjectCompendiumFrame.limboNodesTitle")); //$NON-NLS-1$
		dlgView.setVisible(true);

		setDefaultCursor();
	}

	/**
	 * Refresh all the open views.
	 * @see #validateComponents
	 */
	public void onViewRefresh() {
		validateComponents();
	}

	/**
	 * Arrange the current view and update its aerial.
	 */
	public void onViewArrange(String option)	{

		setWaitCursor();
		IUIArrange arrange = null;
		UIViewFrame viewFrame = getCurrentFrame();
		if(option.equals(IUIArrange.TOPDOWN)){
			arrange = new UIArrangeTopDown();
		}
		else if(option.equals(IUIArrange.LEFTRIGHT)) {
			arrange = new UIArrangeLeftRight();
		}
		ArrangeEdit edit = new ArrangeEdit(viewFrame);
		edit.setArrange(arrange);
		arrange.arrangeView(viewFrame.getView(), viewFrame);

	    viewFrame.getUndoListener().postEdit(edit);

		if (oAerialViewDialog != null)
			oAerialViewDialog.scaleToFit(); // will refresh aerial view after arrange

		setDefaultCursor();
	}

// Begin edit - Lakshmi 11/17/05

	/**
	 * Align the selected nodes in a view and update its aerial.
	 */
	public void onViewAlign(String option)	{

		setWaitCursor();
		UIAlign align = new UIAlign(option);
		UIViewFrame viewFrame = getCurrentFrame();
		if (viewFrame instanceof UIMapViewFrame) {
			AlignEdit edit = new AlignEdit(viewFrame);
			edit.setAlign(align);
			align.alignNodes(viewFrame);

			viewFrame.getUndoListener().postEdit(edit);
		}

		if (oAerialViewDialog != null)
			oAerialViewDialog.scaleToFit(); // will refresh aerial view after arrange

		setDefaultCursor();
	}

// End edit - Lakshmi 11/17/05

//*************** WINDOW MENU ***********************//

	/**
	 * Displays the users home window.
	 */
	public void onViewHomeWindow() {

		boolean frameFound = false;
		UIViewFrame viewFrame = null;

		JInternalFrame[] frames = oDesktop.getAllFrames();
		for (int i = 0; i < frames.length; i++) {
			viewFrame = (UIViewFrame)frames[i];
			if (viewFrame.getView().getLabel().startsWith("Home Window")) { //$NON-NLS-1$
				((UIMapViewFrame)viewFrame).setSelected(true);
				frameFound = true;
			}
		}

		if(!frameFound) {
			//for the time being..
			setNodesAndLinks();
			//set the trashbin icon
			setTrashBinIcon();
		}
	}

	/**
	 * Reset the toolbars to default.
	 */
	public void onResetToolBars() {
		oToolBarManager.onResetToolBars();
	}

	/**
	 * Update the codes choicebox data.
	 */
	public void updateCodeChoiceBoxData() {
		oToolBarManager.updateCodeChoiceBoxData();
	}

	/**
	 * Show any floating toolbars.
	 */
	public void showFloatingToolBars() {
		oToolBarManager.showFloatingToolBars();
	}

	/**
	 * If there is an aerial view, rescale it.
	 */
	public void scaleAerialToFit() {
		if (oAerialViewDialog != null)
			oAerialViewDialog.scaleToFit();
	}

	/**
	 * Open/close the areail view when Menuitem checked/unchecked.
	 * @param selected, true to open the aerial view, false to cancel it.
	 */
	public void onAerialView(boolean selected) {

		final UIViewFrame frame=getCurrentFrame();

		setWaitCursor();
		setWaitCursor(frame);

		final boolean fselected  = selected;
		Thread th = new Thread("APP.onShowAerialView") { //$NON-NLS-1$
			public void run() {
	            if(fselected) {
	            	FormatProperties.aerialView = true;
					FormatProperties.setFormatProp( "aerialView", "true" ); //$NON-NLS-1$ //$NON-NLS-2$
					FormatProperties.saveFormatProps();
					updateAerialView();
				}
	            else {
					if (oAerialViewDialog != null)
						oAerialViewDialog.onCancel();
					else {
				       	FormatProperties.aerialView = false;
						FormatProperties.setFormatProp( "aerialView", "false" ); //$NON-NLS-1$ //$NON-NLS-2$
						FormatProperties.saveFormatProps();
					}
				}

				setDefaultCursor();
				setDefaultCursor(frame);
			}
		};
		th.start();
	}

	/**
	 * Hide the aerial view if user close aerial view themselves.
	 */
	public void cancelAerialView() {

       	FormatProperties.aerialView = false;

		oMenuManager.setAerialView(false);

		FormatProperties.setFormatProp( "aerialView", "false" ); //$NON-NLS-1$ //$NON-NLS-2$
		FormatProperties.saveFormatProps();

		if (oAerialViewDialog != null) {
			oAerialViewDialog.setVisible(false);
			oAerialViewDialog.dispose();
			oAerialViewDialog = null;
		}
	}

	/**
	 * Open the aerial view for the current.
	 */
	public void updateAerialView() {

		if (FormatProperties.aerialView) {
			UIViewFrame frame = getCurrentFrame();
			if (frame instanceof UIMapViewFrame) {
				Rectangle dialogBounds = null;
				if (oAerialViewDialog != null) {
					dialogBounds = oAerialViewDialog.getResetSize();
					oAerialViewDialog.close();
					oAerialViewDialog.setVisible(false);
					oAerialViewDialog.dispose();
				}
				UIMapViewFrame map = (UIMapViewFrame)frame;
				oAerialViewDialog = map.showArialView(dialogBounds);
			}
			else {
				oAerialViewDialog.setVisible(false);
			}
		}
	}

	/**
	 * Cascades the Internal Frames.
	 */
	public void onWindowCascade() {

	    int n = 0;
	    JInternalFrame [] frames = oDesktop.getAllFrames();
	    for(int i=frames.length-1; i>=0; i--) {
			JInternalFrame frame = frames[i];

			try {
			    frame.setMaximum(false);
			    if (frame.isIcon())
				frame.setIcon(false);

			    frame.setBounds(n*INTERNALFRAMEOFFSET, n*INTERNALFRAMEOFFSET,INTERNALFRAMEWIDTH,INTERNALFRAMEHEIGHT);
			    frame.moveToFront();
			    frame.setSelected(true);
			    n++;
			}
			catch(Exception e) {
			    e.printStackTrace();
			}
		}
	}

	/**
	 * Calculate the dimension each window needs to be to fit on the desktop.
	 * Starting at the defaults.
	 * @return
	 */
	public Dimension findTileSize(int frameWidth, int frameHeight, int frameCount) {
	    Dimension desktopSize = oDesktop.getSize();
	    int countAcross = desktopSize.width/frameWidth;
	    int countDown = desktopSize.height/frameHeight;
	    if (countAcross*countDown < frameCount) {
	    	int nextW = frameWidth -1;
	    	int nextH = frameHeight -1;	
	    	return findTileSize(nextW, nextH, frameCount);
	    } else {
	    	return new Dimension(frameWidth, frameHeight);
	    }
	}
	
    /**
     * Tile all internal frames
     */
    public void onWindowTile() {
	    int n = 0;
	    JInternalFrame [] frames = oDesktop.getAllFrames();
	    Dimension desktopSize = oDesktop.getSize();
	    Dimension tileSize = findTileSize(INTERNALFRAMEWIDTH, INTERNALFRAMEHEIGHT, frames.length);
	    int xcount = desktopSize.width/tileSize.width;
	    int ycount = desktopSize.height/tileSize.height;
	    int actualCount = frames.length;
	    if (actualCount <= xcount)
	    if ((tileSize.width*xcount) < desktopSize.width) {
	    	tileSize.width += (desktopSize.width-(tileSize.width*xcount))/xcount;
	    }
	    if ((tileSize.height*ycount) < desktopSize.height) {
	    	tileSize.height += (desktopSize.height-(tileSize.height*ycount))/ycount;
	    }

	    if (tileSize.width < 5 || tileSize.height < 5) {
	    	displayError(LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "ProjectCompendiumFrame.errorTilingWindows")); //$NON-NLS-1$
	    } else {	    	
		    int y=0;
		    int x=0;
		    for(int i=frames.length-1; i>=0; i--) {
				JInternalFrame frame = frames[i];
				try {
				    frame.setMaximum(false);
				    if (frame.isIcon())
				    	frame.setIcon(false);
	
				    frame.setBounds(x, y,tileSize.width,tileSize.height);
				    frame.moveToFront();
				    frame.setSelected(true);
				    x += tileSize.width;
				    if (x+tileSize.width > desktopSize.width) {
				    	x = 0;
				    	y += tileSize.height;
				    }
				    n++;
				}
				catch(Exception e) {
				    e.printStackTrace();
				}
			}
	    }
    }
	
	/**
	 * Expand all the Internal Frames.
	 */
	public void onWindowExpand() {

		JInternalFrame [] frames = oDesktop.getAllFrames();
		for(int i=0;i<frames.length;i++) {
			JInternalFrame frame = frames[i];

			try {
				if (frame.isIcon())
					frame.setIcon(false);

				frame.setMaximum(true);
				frame.moveToFront();
			}
			catch(PropertyVetoException e) {}
		}
	}

	/**
	 * Closes all the Internal Frames.
	 */
	public void onWindowCloseAll() {

		// close all internal frames
		JInternalFrame[] frames = oDesktop.getAllFrames();

		for (int i = 0; i < frames.length; i++) {

			// DONT DELETE HOME FRAME
			if (frames[i] != getInternalFrame(oHomeView)) {
				oDesktop.getDesktopManager().closeFrame(frames[i]);
				oDesktop.remove(frames[i]);
				frames[i].dispose();
			}
		}
	}

/******** FORMAT MENU *********/

	/**
	 * Returns the current default Font used.
	 * @return Font the current font.
	 */
	public Font getDefaultFont() {
		 return this.currentDefaultFont;
	}

	/**
	 * Set the current font used for the default to the passed Font.
	 * @param oFont the new font chosen.
	 */
	public void setDefaultFont( Font oFont ) {
		currentDefaultFont = oFont;
	}

	/**
	 * Change the Node icons to current skin.
	 * @param laf, the look and feel to change to.
	 */
	/*public void onFormatLAF(String laf) {
		final String look = laf;
		Thread thread = new Thread("LAF") {
			public void run() {
				FormatProperties.currentLookAndFeel = look;
				//initLAF();
				FormatProperties.setFormatProp( "LAF", FormatProperties.currentLookAndFeel );
				FormatProperties.saveFormatProps();
			}
		};
		thread.start();
	}*/

	/**
	 * Change the Node icons to given skin.
	 * @param name, the name of the skin to swap to.
	 */
	public void onFormatSkin(String name) {

		final String skinName = name;

		Thread thread = new Thread("Skin") { //$NON-NLS-1$
			public void run() {
				FormatProperties.skin = skinName;
				refreshIcons(true);
				// Lakshmi 3/24/06 - Refresh outline View Icons
				if(UIViewOutline.me != null){
					UIViewOutline.me.refreshTree();
				}
				oToolBarManager.swapToobarSkin();
				FormatProperties.setFormatProp( "skin", FormatProperties.skin ); //$NON-NLS-1$
				FormatProperties.saveFormatProps();
			}
		};
		thread.start();
	}

	/**
	 * Update the icons in all the views.
	 * @param refreshFrameIcons, true to also refresh frame icons, else false.
	 */
	public void refreshIcons(boolean refreshFrameIcons) {

		UIViewFrame viewFrame = null;

		JInternalFrame[] frames = oDesktop.getAllFrames();
		for (int i=0; i < frames.length; i++ ) {

			viewFrame = (UIViewFrame)frames[i];
			if (refreshFrameIcons)
				viewFrame.updateFrameIcon();

			if (viewFrame instanceof UIMapViewFrame) {
				UIMapViewFrame mapFrame = (UIMapViewFrame)viewFrame;
				mapFrame.refreshAerialIcons(refreshFrameIcons);

				UIViewPane viewPane = mapFrame.getViewPane();
				Component array[] = viewPane.getComponentsInLayer((UIViewPane.NODE_LAYER).intValue());

				UINode uinode = null;
				for (int j = 0; j < array.length; j++) {
					uinode = (UINode)array[j];
					int nType = uinode.getNode().getType();
					ImageIcon icon = null;
					NodeSummary node = (NodeSummary)uinode.getNode();

					if (nType == ICoreConstants.REFERENCE || nType == ICoreConstants.REFERENCE_SHORTCUT) {
						String image  = node.getImage();
						if ( image != null && !image.equals("")) //$NON-NLS-1$
							uinode.setReferenceIcon( image );
						else {
							uinode.setReferenceIcon( node.getSource() );
						}
					}
					else if(View.isViewType(nType) || View.isShortcutViewType(nType)) {
						String image  = node.getImage();
						if ( image != null && !image.equals("")) //$NON-NLS-1$
							uinode.setReferenceIcon( image );
						else {
							icon = UINode.getNodeImage(node.getType(), uinode.getNodePosition().getShowSmallIcon());
							uinode.refreshIcon( icon );
						}
					}
					else {
						icon = UINode.getNodeImage(node.getType(), uinode.getNodePosition().getShowSmallIcon());
						uinode.refreshIcon( icon );
					}
					uinode.updateLinks();
				}
				viewPane.repaint();
				viewPane.validate();
			}
		}
	}

	/**
	 * Update the icons for the given node id in all the views.
	 * @param sNodeID the id of the node to refresh the icons for.
	 */
	public void refreshIcons(String sNodeID) {

		UIViewFrame viewFrame = null;

		JInternalFrame[] frames = oDesktop.getAllFrames();
		for (int i=0; i < frames.length; i++ ) {

			viewFrame = (UIViewFrame)frames[i];
			if (viewFrame instanceof UIMapViewFrame) {
				UIMapViewFrame mapFrame = (UIMapViewFrame)viewFrame;
				UIViewPane viewPane = mapFrame.getViewPane();
				Component array[] = viewPane.getComponentsInLayer((UIViewPane.NODE_LAYER).intValue());

				UINode uinode = null;
				for (int j = 0; j < array.length; j++) {
					uinode = (UINode)array[j];
					NodeSummary node = (NodeSummary)uinode.getNode();
					if (node.getId().equals(sNodeID)) {
						int nType = uinode.getNode().getType();
						ImageIcon icon = null;
	
						if (nType == ICoreConstants.REFERENCE || nType == ICoreConstants.REFERENCE_SHORTCUT) {
							String image  = node.getImage();
							if ( image != null && !image.equals("")) //$NON-NLS-1$
								uinode.setReferenceIcon( image );
							else {
								uinode.setReferenceIcon( node.getSource() );
							}
						}
						else if(View.isViewType(nType) || View.isShortcutViewType(nType)) {
							String image  = node.getImage();
							if ( image != null && !image.equals("")) //$NON-NLS-1$
								uinode.setReferenceIcon( image );
							else {
								icon = UINode.getNodeImage(node.getType(), uinode.getNodePosition().getShowSmallIcon());
								uinode.refreshIcon( icon );
							}
						}
						else {
							icon = UINode.getNodeImage(node.getType(), uinode.getNodePosition().getShowSmallIcon());
							uinode.refreshIcon( icon );
						}
						uinode.updateLinks();
					}
				}
				viewPane.repaint();
				viewPane.validate();
			}
		}
	}
	
	/**
	 * Update the icon indicator of a specific nodeID
	 * @param sNodeID, the id of the node to refresh the icon indicators for.
	 */
	public void refreshNodeIconIndicators(String sNodeID) {

		UIViewFrame viewFrame = null;

		JInternalFrame[] frames = oDesktop.getAllFrames();
		for (int i=0; i < frames.length; i++ ) {

			viewFrame = (UIViewFrame)frames[i];
			if (viewFrame instanceof UIMapViewFrame) {
				UIMapViewFrame mapFrame = (UIMapViewFrame)viewFrame;
				mapFrame.refreshAerialNodeIconIndicators(sNodeID);

				UIViewPane viewPane = mapFrame.getViewPane();
				viewPane.refreshNodeIconIndicators(sNodeID);
				viewPane.repaint();
				viewPane.validate();
			}
		}
	}

	/**
	 * Update the icon indicators in all the views.
	 */
	public void refreshIconIndicators() {

		UIViewFrame viewFrame = null;

		JInternalFrame[] frames = oDesktop.getAllFrames();
		for (int i=0; i < frames.length; i++ ) {

			viewFrame = (UIViewFrame)frames[i];
			if (viewFrame instanceof UIMapViewFrame) {
				UIMapViewFrame mapFrame = (UIMapViewFrame)viewFrame;
				mapFrame.refreshAerialIconIndicators();

				UIViewPane viewPane = mapFrame.getViewPane();
				viewPane.refreshIconIndicators();
				viewPane.repaint();
				viewPane.validate();
			}
		}
	}

/******** FAVORITES MENU *********/

	/**
	 * Transclude the given favorite to the current view for old favorites, GoTo the favorite for new ones.
	 *
	 * @param fav the Favorite to process.
	 */
	public void addFavorite(Favorite fav) {

		String sViewID = fav.getViewID();
		if (sViewID == null || sViewID.equals("")) { //$NON-NLS-1$
			String sNodeID = fav.getNodeID();
			UIViewFrame viewFrame = getCurrentFrame();
			UIViewPane viewpane = null;
			View view  = viewFrame.getView();
			if (viewFrame instanceof UIMapViewFrame) {
				viewpane = ((UIMapViewFrame)viewFrame).getViewPane();
			}
	
			NodeSummary favnode = null;
	
			// CHECK DELETED STATUS
			boolean isDeleted = false;
			try {
				if (oModel.getNodeService().isMarkedForDeletion(oModel.getSession(), sNodeID))
					isDeleted = true;
	
				if (isDeleted) {
					try {
						NodeSummary node = ((NodeService)oModel.getNodeService()).getDeletedNodeSummaryId(oModel.getSession(), sNodeID);
						restoreNode(node, viewFrame.getView());
						refreshIconIndicators();
					}
					catch(Exception io) {}
				}
				else {
					try {
						favnode = ((NodeService)oModel.getNodeService()).getNodeSummary(oModel.getSession(), sNodeID);
					}
					catch(Exception io) { return; }
					if (favnode == null)
						return;
	
					// CHECK TO SEE IF DELETED FROM THIS VIEW ALREADY
					NodePosition pos = oModel.getNodeService().restoreNodeView(oModel.getSession(), sNodeID, view.getId());
					if (pos != null) {
						restoreNode(favnode, viewFrame.getView());
						refreshIconIndicators();
					}
					else {
						if (viewpane != null) {
	
							int nX = (viewFrame.getWidth()/2)-60;
							int nY = (viewFrame.getHeight()/2)-60;
	
							// GET CURRENT SCROLL POSITION AND ADD THIS TO POSITIONING INFO
							int hPos = viewFrame.getHorizontalScrollBarPosition();
							int vPos = viewFrame.getVerticalScrollBarPosition();
	
							nX = nX + hPos;
							nY = nY + vPos;
	
							Object exists = viewpane.get(sNodeID);
							if (exists != null) {
								UINode uinode = (UINode) exists;
								viewpane.getUI().createShortCutNode(uinode, nX, nY);
							}
							else {
	
								ViewPaneUI oViewPaneUI = viewpane.getUI();
								UINode uinode = oViewPaneUI.addNodeToView(favnode, nX, nY);
								if (uinode != null) {
									uinode.setRollover(false);
									uinode.setSelected(true);
									viewpane.setSelectedNode(uinode, ICoreConstants.MULTISELECT);
								}
							}
						}
						else {
							if (viewFrame instanceof UIListViewFrame) {
	
								UIList uiList = ((UIListViewFrame)viewFrame).getUIList();
	
								int nodeindex = uiList.getIndexOf(sNodeID);
								if (nodeindex != -1) {
									int[] indexes = {nodeindex};
									uiList.createShortCutNodes(indexes);
									uiList.updateTable();
									uiList.selectNode(uiList.getNumberOfNodes() - 1, ICoreConstants.MULTISELECT);
								}
								else {
									int nY = (uiList.getNumberOfNodes() + 1) * 10;
										int nX = 0;
	
									try {
										NodePosition favpos = uiList.getView().addNodeToView(favnode, nX, nY);
										uiList.insertNode(favpos, uiList.getNumberOfNodes());
										uiList.selectNode(uiList.getNumberOfNodes() - 1, ICoreConstants.MULTISELECT);
									}
									catch(Exception io) {}
								}
							}
						}
					}
				}
			}
			catch(Exception ex) {
				ex.printStackTrace();
				displayError(LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "ProjectCompendiumFrame.errorFavorites")+"\n\n"+ex.getLocalizedMessage()); //$NON-NLS-1$ //$NON-NLS-2$
			}
		} else {
			String sNodeID = fav.getNodeID();
			UIUtilities.jumpToNode(sViewID, sNodeID, "Bookmark");	 //$NON-NLS-1$
		}
	}

	/**
	 * Create a new favorite with the given node is, label and node type.
	 * @param sNodeID the id of the node to create a favorite for.
	 * @param sViewID the id of the view to create a favorite for.	 
	 * @param sLabel the label for th favorite node.
	 * @param nType the node type of the favorite node.
	 */
	public void createFavorite(String sNodeID, String sViewID, String sLabel, int nType) {

		String sUserID = oModel.getUserProfile().getId();
		FavoriteService favserv = (FavoriteService)oModel.getFavoriteService();

		Vector favorites = null;
		try { favorites = favserv.getFavorites(oModel.getSession(), sUserID); }
		catch(Exception io) {}

		if (favorites != null && favorites.size() > 0) {
			int count = favorites.size();
			Favorite fav = null;
			String viewID = ""; //$NON-NLS-1$
			String nodeID = ""; //$NON-NLS-1$
			for (int i=0; i< count; i++) {
				fav = (Favorite)favorites.elementAt(i);
				nodeID = fav.getNodeID();
				viewID = fav.getViewID();

				if (viewID != null && !viewID.equals("")) { //$NON-NLS-1$
					if (nodeID.equals(sNodeID) && viewID.equals(sViewID)) {
						return;
					}
				}
			}
		}

		try {
			((FavoriteService)oModel.getFavoriteService()).createFavorite(oModel.getSession(), sUserID, sNodeID, sViewID, sLabel, nType);
		}
		catch(Exception io) {
			io.printStackTrace();
		}

		refreshFavoritesMenu();
	}
	
	/**
	 * Delete the favorites with the given node ids.
	 * @param vtFavorites the list of Favorites to delete.
	 */
	public void deleteFavorites(Vector vtFavorites) {

		String sUserID = oModel.getUserProfile().getId();
		try {
			((FavoriteService)oModel.getFavoriteService()).deleteFavorites(oModel.getSession(), sUserID, vtFavorites);
		}
		catch(Exception ex) {
			System.out.println(LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "ProjectCompendiumFrame.errorDeleteFavorites")+":\n\n"+ex.getMessage()); //$NON-NLS-1$ //$NON-NLS-2$
		}

		refreshFavoritesMenu();
	}

	/**
	 * Refersh the items on the Favorites menu from the database.
	 */
	public void refreshFavoritesMenu() {

		String sUserID = oModel.getUserProfile().getId();
		FavoriteService favserv = (FavoriteService)oModel.getFavoriteService();

		Vector favorites = null;
		try { favorites = favserv.getFavorites(oModel.getSession(), sUserID); }
		catch(Exception io) {
			io.printStackTrace();
		}

		oMenuManager.refreshFavoritesMenu(favorites);
	}

	/**
	 * Open the Favorites Maintenance dialog.
	 */
	public void onFavoriteMaintenace() {
		UIFavoriteDialog fav = new UIFavoriteDialog(this, oModel.getUserProfile().getId(), oModel);
		fav.setVisible(true);
	}

/******** WORKSPACE MENU *********/

	/**
	 * Load the views for the workspace with the given id, for the given user.
	 * @param sWorkspaceID, the id of the workspace to add.
	 * @param sUserID, the id of the user to add it for.
	 */
	public void addWorkspace(String sWorkspaceID, String sUserID) {

		UIViewFrame homeFrame = getInternalFrame(oHomeView);
		String sHomeViewID = homeFrame.getView().getId();
		// NEED TO DO THIS, OR THEY WILL ALL BE EXPANDED
		if (homeFrame.isMaximum()) {
			try {homeFrame.setMaximum(false);
			}catch(Exception ex){}
		}

		boolean frameFound = false; int i=0;

		// CLOSE ALL FRAMES EXCEPT HOME
		JInternalFrame[] frames = oDesktop.getAllFrames();
		while(!frameFound && i<frames.length) {
			UIViewFrame viewFrame = (UIViewFrame)frames[i++];
			if (!viewFrame.equals(homeFrame)) {
				oDesktop.getDesktopManager().closeFrame(viewFrame);
				oDesktop.remove(viewFrame);
				viewFrame.dispose();
			}
		}

		oToolBarManager.clearHistory();

		validate();
		repaint();

		// LOAD GIVEN WORKSPACE
		WorkspaceService workserv = (WorkspaceService)oModel.getWorkspaceService();
		String userID = oModel.getUserProfile().getId();
		Vector workspace = null;
		Vector views = new Vector(51);
		int countk=0;
		try {
			workspace = workserv.getWorkspaceViews(oModel.getSession(), sWorkspaceID);
			Enumeration eviews = ProjectCompendium.APP.getModel().getNodeService().getAllActiveViews(ProjectCompendium.APP.getModel().getSession());
			for(Enumeration e = eviews; e.hasMoreElements();) {
				View view = (View)e.nextElement();
				views.addElement(view);
			}
			countk = views.size();
		}
		catch(Exception io) {}

		// ADD WORKSPACE VIEWS TO DESKTOP
		if (countk > 0 && workspace != null && workspace.size() > 0) {
			int count = workspace.size();
			for (int j=0; j <count; j++) {

				WorkspaceView work = (WorkspaceView)workspace.elementAt(j);
				String sViewID = work.getViewID();
				
				int width = work.getWidth();
				int height = work.getHeight();
				int xPos = work.getXPosition();
				int yPos = work.getYPosition();
				boolean isIcon = work.getIsIcon();
				boolean isMaximum = work.getIsMaximum();
				int HScroll = work.getHorizontalScrollBarPosition();
				int VScroll = work.getVerticalScrollBarPosition();

				if (sViewID.equals(sHomeViewID)) {					
					homeFrame.setBounds(xPos, yPos, width, height);
					homeFrame.setHorizontalScrollBarPosition(HScroll, true);
					homeFrame.setVerticalScrollBarPosition(VScroll, true);
					try {
						homeFrame.setIcon(isIcon);
						homeFrame.setMaximum(isMaximum);
					}catch(Exception ex){}
					((UIMapViewFrame)homeFrame).setSelected(true);
					oDesktop.moveToFront(homeFrame);
				} else {								
					for(int k=0; k<countk; k++) {
						View view = (View)views.elementAt(k);
						if (view.getId().equals(sViewID)) {						
							UIViewFrame oViewFrame = addViewToDesktop(view, view.getLabel(), width, height, xPos, yPos, isIcon, isMaximum, HScroll, VScroll);
							Vector history = new Vector();
							history.addElement(new String("Workspace")); //$NON-NLS-1$
							oViewFrame.setNavigationHistory(history);							
							break;
						}
					}
				}
			}
		}
	}

	/**
	 * Create a new workspace withe the given name and conatining the currently open and positioned views.
	 * @param sName, the name of the new workspace.
	 */
	public boolean createWorkspace(String sName) {

		String sUserID = oModel.getUserProfile().getId();
		WorkspaceService workserv = (WorkspaceService)oModel.getWorkspaceService();

		Vector workspaces = null;
		try { workspaces = workserv.getWorkspaces(oModel.getSession(), sUserID); }
		catch(Exception io) {}

		boolean editing = false;
		String sWorkspaceID = ""; //$NON-NLS-1$

		if (workspaces != null && workspaces.size() > 0) {
			int count = workspaces.size();

			for (int i=0; i< count; i++) {
				Vector next = (Vector)workspaces.elementAt(i);
				String name = (String)next.elementAt(1);
				if (name.equals(sName)) {
					int response = JOptionPane.showConfirmDialog(this, LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "ProjectCompendiumFrame.workspaceExistsA")+"\n\n"+ //$NON-NLS-1$ //$NON-NLS-2$
							LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "ProjectCompendiumFrame.workspaceExistsB"), //$NON-NLS-1$
									LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "ProjectCompendiumFrame.createWorkspace"), JOptionPane.YES_NO_OPTION); //$NON-NLS-1$

					if (response == JOptionPane.NO_OPTION || response == JOptionPane.CLOSED_OPTION)
						return false;

					editing = true;
					sWorkspaceID = (String)next.elementAt(0);
				}
			}
		}

		if (!editing)
			sWorkspaceID = oModel.getUniqueID();

		try {
			JInternalFrame[] frames = oDesktop.getAllFrames();
			Vector vtWorkspace = new Vector(51);
			UIViewFrame homeFrame = getInternalFrame(oHomeView);

			for (int i=0; i < frames.length; i++) {
				UIViewFrame frame = (UIViewFrame)frames[i];

				WorkspaceView view = new WorkspaceView();
				view.setWorkspaceID(sWorkspaceID);
				view.setViewID(frame.getView().getId());
				view.setWidth(frame.getWidth());
				view.setHeight(frame.getHeight());
				view.setXPosition(frame.getX());
				view.setYPosition(frame.getY());
				view.setIsIcon(frame.isIcon());
				view.setIsMaximum(frame.isMaximum());
				view.setHorizontalScrollBarPosition(view.getHorizontalScrollBarPosition());
				view.setVerticalScrollBarPosition(view.getVerticalScrollBarPosition());

				vtWorkspace.addElement(view);
			}

			if (vtWorkspace.isEmpty()) {
				displayError(LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "ProjectCompendiumFrame.noActiveViews")); //$NON-NLS-1$
				return false;
			}

			Workspace workspace = new Workspace(sWorkspaceID, sName, sUserID, vtWorkspace);
			workspace.initialize(oModel.getSession(), oModel);
			workspace.saveWorkspace(editing, sUserID);

			if (!editing)
				refreshWorkspaceMenu();
		}
		catch(Exception io) {
			ProjectCompendium.APP.displayError(LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "ProjectCompendiumFrame.errorSaveWorkspace")+": "+sName+"\n\n"+io.getLocalizedMessage()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}

		return true;
	}

	/**
	 * Update the workspace with the given id and name to hold the currently open views.
	 * @param sWorkspaceID, the id of the workspace to update.
	 * @param sName, the name of the workspace.
	 */
	public boolean updateWorkspace(String sWorkspaceID, String sName) {

		String sUserID = oModel.getUserProfile().getId();
		WorkspaceService workserv = (WorkspaceService)oModel.getWorkspaceService();

		Vector workspaces = null;
		try { workspaces = workserv.getWorkspaces(oModel.getSession(), sUserID); }
		catch(Exception io) {}

		boolean editing = false;

		try {
			JInternalFrame[] frames = oDesktop.getAllFrames();
			Vector vtWorkspace = new Vector(51);
			UIViewFrame homeFrame = getInternalFrame(oHomeView);

			for (int i=0; i < frames.length; i++) {
				UIViewFrame frame = (UIViewFrame)frames[i];

				WorkspaceView view = new WorkspaceView();
				view.setWorkspaceID(sWorkspaceID);
				view.setViewID(frame.getView().getId());
				view.setWidth(frame.getWidth());
				view.setHeight(frame.getHeight());
				view.setXPosition(frame.getX());
				view.setYPosition(frame.getY());
				view.setIsIcon(frame.isIcon());
				view.setIsMaximum(frame.isMaximum());
				view.setHorizontalScrollBarPosition(view.getHorizontalScrollBarPosition());
				view.setVerticalScrollBarPosition(view.getVerticalScrollBarPosition());

				vtWorkspace.addElement(view);
			}
			Workspace workspace = new Workspace(sWorkspaceID, sName, sUserID, vtWorkspace);
			workspace.initialize(oModel.getSession(), oModel);

			if (vtWorkspace.isEmpty()) {
				displayError(LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "ProjectCompendiumFrame.noViews")); //$NON-NLS-1$
				return false;
			}
			else {
				workspace.saveWorkspace(true, sUserID);
			}
		}
		catch(Exception io) {
			ProjectCompendium.APP.displayError(LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "ProjectCompendiumFrame.errorSaveWorkspace")+sName+"\n\n"+io.getMessage()); //$NON-NLS-1$ //$NON-NLS-2$
		}

		return true;
	}

	/**
	 * Delete the workspace with the given id from the database.
	 * @param sWorkspaceID, the id of the workspace to delete.
	 */
	public void deleteWorkspaces(String sWorkspaceIDs) {

		String sUserID = oModel.getUserProfile().getId();
		try {
			((WorkspaceService)oModel.getWorkspaceService()).deleteWorkspaces(oModel.getSession(), sUserID, sWorkspaceIDs);
		}
		catch(Exception io) { }

		refreshWorkspaceMenu();
	}

	/**
	 * Load the current users workspaces into the Workspace menu.
	 */
	public void refreshWorkspaceMenu() {

		final String sUserID = oModel.getUserProfile().getId();
		WorkspaceService workserv = (WorkspaceService)oModel.getWorkspaceService();

		Vector workspaces = null;

		try { workspaces = workserv.getWorkspaces(oModel.getSession(), sUserID); }
		catch(Exception io) {}

		oMenuManager.refreshWorkspaceMenu(workspaces, sUserID);
	}


	/**
	 * Open the Workspace Maintenance dialog.
	 */
	public void onWorkspaceMaintenace() {
		UIWorkspaceDialog work = new UIWorkspaceDialog(ProjectCompendium.APP, oModel.getUserProfile().getId(), oModel);
		work.setVisible(true);
	}

/******** TOOLS MENU *********/

	/**
	 * Opens the User management dialog.
	 */
	public void onUsers() {

		UIUserManagerDialog dialog = new UIUserManagerDialog(this);
		dialog.setVisible(true);
	}

	/**
	 * Opens the database file browser dialog.
	 */
	public void onLinkedFilesBrowser() {
		UILinkedFilesBrowser fileBrowser = new UILinkedFilesBrowser(this);
		fileBrowser.setVisible(true);
	}

	/**
	 * Open the code (tag) maintenance dialog.
	 */
	public void onCodes() {
		oMenuManager.addTagsView(true);	
	}
	
	/**
	 * Show all the code information for the current map.
	 */
	public void onShowCodes() {

		UIViewFrame viewFrame = getCurrentFrame();

		if (viewFrame instanceof UIMapViewFrame) {
			UIViewPane view = ((UIMapViewFrame)viewFrame).getViewPane();
			view.showCodes();
		}
		else {
			displayError(LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "ProjectCompendiumFrame.noActiveMaps")); //$NON-NLS-1$
		}
	}

	/**
	 * Hide all the code information for the current map.
	 */
	public void onHideCodes() {

		UIViewFrame viewFrame = getCurrentFrame();

		if (viewFrame instanceof UIMapViewFrame) {
			UIViewPane view = ((UIMapViewFrame)viewFrame).getViewPane();
			view.hideCodes();
		}
		else {
			displayError(LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "ProjectCompendiumFrame.noActiveMaps")); //$NON-NLS-1$
		}
	}

	/**
	 * Add the given code to the currently selected nodes in the current view.
	 * @param code com.compendium.core.Code, the code to add.
	 */
	public void addCode( Code code ) {

		UIViewFrame viewFrame = getCurrentFrame();

		int numSelected = 0;
		Enumeration nodes = null;
		if (viewFrame instanceof UIMapViewFrame) {
			UIViewPane pane = ((UIMapViewFrame)viewFrame).getViewPane();
			if (pane != null ) {
				numSelected = pane.getNumberOfSelectedNodes();
				nodes = pane.getSelectedNodes();
			}
		}
		else {
			UIList uilist = ((UIListViewFrame)viewFrame).getUIList();
			if (uilist != null ) {
				numSelected = uilist.getNumberOfSelectedNodes();
				nodes = uilist.getSelectedNodes();
			}
		}

		if (numSelected <= 0) {
			displayError(LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "ProjectCompendiumFrame.selectNodes")); //$NON-NLS-1$
		}
		else {
			Object obj = null;
			NodeSummary node = null;
			for(Enumeration e = nodes; e.hasMoreElements();) {
				
				node = null;
				obj = e.nextElement();
				if (obj instanceof UINode) {
					UINode uinode = (UINode)obj;
					node = uinode.getNode();
				} else {
					NodePosition pos = (NodePosition)obj;
					node = pos.getNode();
				}
				if (node != null) {
					// Can't add tags to the Trashbin or the Inbox.
					if (!node.getId().equals(this.getInBoxID()) && !node.getId().equals(this.getTrashBinID())) {
						try {
							node.addCode(code);
		
							// IF WE ARE RECORDING or REPLAYING A MEETING, RECORD A TAG ADDED EVENT.
							if (ProjectCompendium.APP.oMeetingManager != null
										&& ProjectCompendium.APP.oMeetingManager.captureEvents()) {
		
								View view  = viewFrame.getView();
								ProjectCompendium.APP.oMeetingManager.addEvent(
										new MeetingEvent(oMeetingManager.getMeetingID(),
														 oMeetingManager.isReplay(),
														 MeetingEvent.TAG_ADDED_EVENT,
														 view,
														 node,
														 code));
							}
							
							// REFRESH TAGS WORKING AREA.
							oMenuManager.setNodeSelected(true);
						}
						catch(Exception ex) {
							displayError("Error: (ProjectCompendiumFrame.addCode)\n\n"+ex.getMessage()); //$NON-NLS-1$
							break;
						}
					}
				}
			}
		}
	}

// SCRIBBLE PAD

	/**
	 * Show the scribble pad for the current map.
	 */
	public void onShowScribblePad() {

		UIViewFrame viewFrame = getCurrentFrame();
		if (viewFrame instanceof UIMapViewFrame) {
			oToolBarManager.onToggleScribble();
		}
		else {
			displayError(LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "ProjectCompendiumFrame.noActiveMaps")); //$NON-NLS-1$
		}
	}

	/**
	 * Hide the scribble pad for the current map.
	 */
	public void onHideScribblePad() {

		UIViewFrame viewFrame = getCurrentFrame();
		if (viewFrame instanceof UIMapViewFrame) {
			oToolBarManager.onToggleScribble();
		}
		else {
			displayError(LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "ProjectCompendiumFrame.noActiveMaps")); //$NON-NLS-1$
		}
	}

	/**
	 * Clear the scribble pad for the current map.
	 */
	public void onClearScribblePad() {

		UIViewFrame viewFrame = getCurrentFrame();

		if (viewFrame instanceof UIMapViewFrame) {
			UIViewPane view = ((UIMapViewFrame)viewFrame).getViewPane();
			view.clearScribblePad();
		}
		else {
			displayError(LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "ProjectCompendiumFrame.noActiveMaps")); //$NON-NLS-1$
		}
	}

	/**
	 * Save the scribble pad contents.
	 */
	public void onSaveScribblePad() {

		UIViewFrame viewFrame = getCurrentFrame();

		if (viewFrame instanceof UIMapViewFrame) {
			UIViewPane view = ((UIMapViewFrame)viewFrame).getViewPane();
			view.saveScribblePad();
		}
		else {
			displayError(LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "ProjectCompendiumFrame.noActiveMaps")); //$NON-NLS-1$
		}
	}

//*************** HELP MENU **************//

	/**
	 * Displays the about dialog.
	 */
	public void onHelpAbout() {
		if (oAboutDialog != null) {
			oAboutDialog.setVisible(false);
			oAboutDialog.dispose();			
		}

		oAboutDialog = new UIAboutDialog(this);
		UIUtilities.centerComponent(oAboutDialog, this);
		oAboutDialog.setVisible(true);
		getAudioPlayer().playAudio(UIAudio.ABOUT_ACTION);
	}

//**************** END MENU FUNCTIONS ***************//

	/**
	 * Re-initializes the system when a database project is closed.
	 */
	public void onFileClose() {

		if(oDesktop != null) {

			// close all internal frames
			JInternalFrame[] frames = oDesktop.getAllFrames();
			for (int i = 0; i < frames.length; i++) {
				UIViewFrame viewframe = (UIViewFrame)frames[i];

				// SAVE THE CURRENT PROPERTIES OF EACH OPEN FRAME
				saveViewProperties(viewframe);

				oDesktop.getDesktopManager().closeFrame(viewframe);
				oDesktop.remove(viewframe);

				// CLEAN UP FOR MEMORY USAGE
				viewframe.cleanUp();
				if (viewframe instanceof UIMapViewFrame) {
					UIViewPane pane = ((UIMapViewFrame)viewframe).getViewPane();
					if (pane != null)
						pane.cleanUp();
				}
				viewframe.dispose();
			}

			oServiceManager.cleanUp();

			NodeSummary.clearList();
			oTrashbinNode = null;
			oInboxNode = null;		

			// update menu
			oToolBarManager.onDatabaseClose();
		}

		// disable menu items
		oMenuManager.onDatabaseClose();

		refreshWindowsMenu();

		setPasteEnabled(false);
		if (externalCopy)
			oMenuManager.setExternalPasteEnablement(true);

		if (oModel != null) {
			oModel.cleanUp();
			oModel= null;
		}
		
		if (FormatProperties.nDatabaseType == ICoreConstants.MYSQL_DATABASE) {
			if (oCurrentMySQLConnection != null)
				setTitle(ICoreConstants.MYSQL_DATABASE, oCurrentMySQLConnection.getServer(), oCurrentMySQLConnection.getProfile(), ""); //$NON-NLS-1$
			else
				setTitle(ICoreConstants.MYSQL_DATABASE, "", "", ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}
		else {
			setDerbyTitle(""); //$NON-NLS-1$
		}
		
		this.requestFocus();		
		setDefaultCursor();
	}

	/**
	 * Removes a view from the views list and history, and if open, closes it.
	 * @param view com.compendium.core.datamodel.View, the view to remove the frame for.
	 */
	public boolean removeViewFromHistory(View view) {

		oToolBarManager.removeFromHistory(view);

		UIViewFrame viewFrameCheck = null;
		UIViewFrame viewFrame = null;

		int count = viewFrameList.size();
		for (int j = 0; j < count; j++) {
			viewFrameCheck = (UIViewFrame)viewFrameList.elementAt(j);
			if (viewFrameCheck.getView().getId().equals(view.getId())) {
				//viewFrameList.removeElementAt(j);
				
				JInternalFrame[] frames = oDesktop.getAllFrames();
				for (int i = 0; i < frames.length; i++) {
					viewFrame = (UIViewFrame)frames[i];

					if (viewFrame.getView().getId().equals(view.getId())) {
						oDesktop.getDesktopManager().closeFrame(frames[i]);
						oDesktop.remove(frames[i]);
						frames[i].dispose();
						refreshWindowsMenu();
					}
				}

				return true;
			}
		}
		return false;
	}

	/**
	 * Removes a view from the desktop by closing an internal frame with the contents of the view.
	 * @param view com.compendium.core.datamodel.View, the view to remove the frame for.
	 */
	public boolean removeView(View view) {

		UIViewFrame viewFrame = null;
		UIViewFrame viewFrameCheck = null;

		JInternalFrame[] frames = oDesktop.getAllFrames();
		for (int i = 0; i < frames.length; i++) {
			viewFrame = (UIViewFrame)frames[i];

			if (viewFrame.getView().getId().equals(view.getId())) {
				oDesktop.getDesktopManager().closeFrame(frames[i]);
				oDesktop.remove(frames[i]);
				frames[i].dispose();
				refreshWindowsMenu();

				// refresh the opened viewFrame list
				/*int count = viewFrameList.size();
				for (int j = 0; j < count; j++) {
					viewFrameCheck = (UIViewFrame)viewFrameList.elementAt(j);
					if (viewFrameCheck.getView().getId().equals(view.getId())) {
						viewFrameList.removeElementAt(j);
						viewFrameList.addElement(viewFrame);
						j=count;
					}
				}*/

				return true;
			}
		}
		return false;
	}

//*********** GETTERS/SETTERS ***********//

	/**
	 * Get the list of opened views.
	 * @return Vector, the list of opened views.
	 */
	public Vector getOpenedViews() {
		return viewFrameList;
	}

	/**
	 * Returns the screen size.
	 * @return Dimension, the size of the current user screen.
	 */
	public Dimension getScreenSize() {
		Dimension dim = getSize();
		nScreenWidth = dim.width;
		nScreenHeight = dim.height;
		return dim;
	}

	/**
	 * Returns the active model.
	 * @return com.compendium.core.datamodel.IModel, the model for the currently open database project.
	 */
	public IModel getModel() {
		return oModel;
	}

	/**
	 * Gets the current home view.
	 * @return com.compendium.core.datamodel.View, the current home view for the current user.
	 */
	public View getHomeView() {
		return oHomeView;
	}

	/**
	 * Gets the current inbox view.
	 * @return com.compendium.core.datamodel.View the current inbox view for the current user.
	 */
	public View getInBoxView() {
		return (View)oInboxNode;
	}
	
	/**
	 * Returns the service manager.
	 * @param ServiceManager, the service manager for this session.
	 */
	public IServiceManager getServiceManager() {
		return oServiceManager;
	}

	/**
	 * Returns the content pane.
	 * @param Container, the content pane for the frame contents.
	 */
	public Container getContentPane() {
		return oContentPane;
	}

	/**
	 * Returns the main panel.
	 * @param JPanel, the main panel for the frame contents.
	 */
	public JPanel getMainPanel() {
		return oMainPanel;
	}

	/**
	 * Returns the inner panel.
	 * @param JPanel, the main panel for the frame contents.
	 */
	public Dimension getInnerPanelSize() {
		return oInnerPanel.getPreferredSize();
	}

	/**
	 * Returns the menu manager.
	 * @return UIMenuManaager, the menu manager being used by this frame.
	 */
	public UIMenuManager getMenuManager() {
		return oMenuManager;
	}

	/**
	 * Returns the toolbar manager.
	 * @return UIToolBarManaager, the toolbar manager being used by this frame.
	 */
	public UIToolBarManager getToolBarManager() {
		return oToolBarManager;
	}

	/**
	 * Returns the desktop.
	 * @return JDesktop, the desktop being used by this frame.
	 */
	public JDesktopPane getDesktop() {
		return oDesktop;
	}

	/**
	 * Routine to get the Home Window of the user from the database.
	 */
	public void setNodesAndLinks() {

		setWaitCursor();

		oHomeView = null;
		
		if(oModel == null) {
			JOptionPane oOptionPane = new JOptionPane(LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "ProjectCompendiumFrame.exitAndRelogin")); //$NON-NLS-1$
			JDialog oDialog = oOptionPane.createDialog(oContentPane,LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "ProjectCompendiumFrame.loginInfo")); //$NON-NLS-1$
			UIUtilities.centerComponent(oDialog, this);
			oDialog.setModal(true);
			oDialog.setVisible(true);
			return;
		}
		
		UserProfile up = oModel.getUserProfile();
		up.initialize(oModel.getSession(),oModel);
		
		//Get the users homeview
		oHomeView = up.getHomeView();
		Date date = new Date();
		String userName = up.getUserName();
		
		//if no home view try and create one
		if(oHomeView == null) {
			try {
				oHomeView = (View)oModel.getNodeService().createNode(oModel.getSession(),
																	oModel.getUniqueID(),
																	ICoreConstants.MAPVIEW,
																	"", //$NON-NLS-1$
																	"", //$NON-NLS-1$
																	ICoreConstants.WRITEVIEWNODE,
																	ICoreConstants.READSTATE,
																	userName,
																	"Home Window", //$NON-NLS-1$
																	"Home Window of " + userName, //$NON-NLS-1$
																	date,
																	date
																	);

				IModel model = oModel;
				PCSession session = oModel.getSession();
				String author = userName;
				Date creationDate = date;
				Date modificationDate = creationDate;
				String description = "No Description"; //$NON-NLS-1$
				String behavior = "No Behavior"; //$NON-NLS-1$
				String name = userName;
				String codeId = oModel.getUniqueID();

				//add to the DB
				Code code = model.getCodeService().createCode(session, codeId, author, creationDate, modificationDate, name, description, behavior);
				model.addCode(code);
				up.setHomeView(oHomeView);				
			}
			catch (Exception e) {
				displayError("Error: (ProjectCompendiumFrame.setNodesAndLinks)\n\n"+e.getMessage()); //$NON-NLS-1$
				return;
			}
		}
		
		// If the user does not have a linkview add one 
		// This will probably only happen the first time after people update the new database scheme.
		// but does not harm anything to leave this check in.
		oInboxNode = createInBox(up);

		try {
			// Make sure model updated.
			oModel.loadUsers();

			//remove old trashbin
			if(oTrashbinNode != null) {
				oHomeView.removeMemberNode(oTrashbinNode);
			}
		}
		catch(Exception ex) {
			System.out.println("Exception: (ProjectCompendiumFrame.setNodesAndLinks-1)\n\n"+ex.getMessage()); //$NON-NLS-1$
		}

		//if home view exists then register with client event
		oHomeView.initialize(oModel.getSession(), oModel);
		try {
			oHomeView.initializeMembers();
		}
		catch(Exception ex) {
			System.out.println("Exception: (ProjectCompendiumFrame.setNodesAndLinks-2)\n\n"+ex.getMessage()); //$NON-NLS-1$
		}

		oHomeView.setBackgroundColor((Color.white).getRGB());
		oHomeView.setBackgroundImage(SystemProperties.defaultHomeViewBackgroundImage);
		String sTrashbinId = oModel.getUniqueID();
		
// Lakshmi (4/21/06 ) - State of Trash bin? - Default read State.
		oTrashbinNode = NodeSummary.getNodeSummary(sTrashbinId, ICoreConstants.TRASHBIN, "", sTrashbinId , ICoreConstants.READSTATE, userName, //$NON-NLS-1$
				    						date, date, "Trash Bin", ""); //$NON-NLS-1$ //$NON-NLS-2$

		NodePosition pos = oHomeView.addMemberNode(new NodePosition(oHomeView, oTrashbinNode, 
				15, 5, date, date, Model.SHOW_TAGS_DEFAULT, Model.SHOW_TEXT_DEFAULT, 
				Model.SHOW_TRANS_DEFAULT, Model.SHOW_WEIGHT_DEFAULT, Model.SMALL_ICONS_DEFAULT, 
				Model.HIDE_ICONS_DEFAULT, Model.LABEL_WRAP_WIDTH_DEFAULT, Model.FONTSIZE_DEFAULT, 
				Model.FONTFACE_DEFAULT,	Model.FONTSTYLE_DEFAULT, Model.FOREGROUND_DEFAULT.getRGB(), 
				Model.BACKGROUND_DEFAULT.getRGB()));
		
		pos.initialize(oModel.getSession(), oModel); // Need this to set font and wrap width			

		// Begin Edit - Lakshmi 5/15/06
		// By Default make home window as read
		try {
			if (oHomeView.getState() != ICoreConstants.READSTATE) {
				oModel.getNodeService().setState(oModel.getSession(), oHomeView.getId(), 
						ICoreConstants.UNREADSTATE, ICoreConstants.READSTATE, new Date());
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		//End edit
		
		oTrashbinNode.initialize(oModel.getSession(), oModel);

		// add this view to the desktop
		UIViewFrame viewFrame = addViewToDesktop(oHomeView, "  " +userName + "\'s " + oHomeView.getLabel()); //$NON-NLS-1$ //$NON-NLS-2$

		// SOMETIMES FAILS TO DISPLAY
		oDesktop.moveToFront(viewFrame);
		viewFrame.setVisible(true);

		// Lakshmi (10/18/06) - set the initial state of inbox.
		boolean isModified = false; 
		View inbox = getInBoxView();
		try {
			inbox.initialize(oModel.getSession(), oModel);
			inbox.initializeMembers();
			Vector nodes = inbox.getMemberNodes();
			for(int i = 0; i < nodes.size(); i ++){
				NodeSummary node = (NodeSummary) nodes.get(i);
				int state = node.getState();
				if(state == ICoreConstants.UNREADSTATE){
					isModified = true;
					break ;
				} 
			}
			if(isModified) {
				getInBoxView().setState(ICoreConstants.MODIFIEDSTATE);
			} else {
				getInBoxView().setState(ICoreConstants.READSTATE);
			}
		} catch(Exception e){
			e.printStackTrace();
		}
		//End edit
		setDefaultCursor();
	}

	/**
	 * Create an InBox for the given user Profile.
	 * @param up the user profile to create an inbox for
	 * @return
	 */
	public View createInBox(UserProfile up) {
		
		up.initialize(oModel.getSession(), oModel);
		
		String sLinkViewID = ""; //$NON-NLS-1$
		Date date = new Date();
		View oInboxNode = up.getLinkView();
 		if (oInboxNode == null) {
 			String userName = up.getUserName();
 	 		Model model = (Model)oModel;		 			
			try {
				sLinkViewID = oModel.getUniqueID();
				oInboxNode = (View)oModel.getNodeService().createNode(oModel.getSession(),
						sLinkViewID,
						ICoreConstants.LISTVIEW,
						"", //$NON-NLS-1$
						"", //$NON-NLS-1$
						ICoreConstants.WRITEVIEWNODE,
						ICoreConstants.READSTATE,
						userName,
						"Inbox", //$NON-NLS-1$
						"Inbox of " + userName, //$NON-NLS-1$
						date,
						date
						);

				oInboxNode.initialize(oModel.getSession(), oModel);					
		  		IViewService vs = oModel.getViewService();
		  		
				NodePosition oLinkPos = vs.addMemberNode(oModel.getSession(), up.getHomeView(), 
						(NodeSummary)oInboxNode, 
						0, 75, date, date, false, false, false, true, false, false, 
						Model.LABEL_WRAP_WIDTH_DEFAULT, Model.FONTSIZE_DEFAULT, 
						Model.FONTFACE_DEFAULT,	Model.FONTSTYLE_DEFAULT, Model.FOREGROUND_DEFAULT.getRGB(), 
						Model.BACKGROUND_DEFAULT.getRGB());
				oLinkPos.initialize(oModel.getSession(), oModel);					
				oInboxNode.setSource("", CoreUtilities.unixPath(UIImages.getPathString(IUIConstants.INBOX)), userName); //$NON-NLS-1$
				oInboxNode.setState(ICoreConstants.READSTATE);
				up.setLinkView((View)oInboxNode);				
			} catch (Exception e) {
				e.printStackTrace();
				displayError("(ProjectCompendiumFrame.createInBox - adding inbox)\n\n"+e.getMessage()); //$NON-NLS-1$
			}
		} else {
			try {
				if (oInboxNode.getState() != ICoreConstants.READSTATE) {
					oInboxNode.initialize(oModel.getSession(), oModel);						
					oInboxNode.setState(ICoreConstants.READSTATE);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}			
		}
 		
 		return oInboxNode;
	}
	
	/**
	 * Set the trashbin icon depending upon if there any deleted objects in the database
	 */
	public ImageIcon setTrashBinIcon() {

		ImageIcon img = null;

		try {
			String userID = oModel.getUserProfile().getId();
			PCSession session = oModel.getSession();
			int iDeletedNodeCount = oModel.getNodeService().iGetDeletedNodeCount(session);

			UIViewFrame homeFrame = getInternalFrame(oHomeView);
			if (homeFrame != null) {
				UIViewPane pane = ((UIMapViewFrame)homeFrame).getViewPane();
				if (pane != null) {
					UINode trashbin = (UINode) pane.get(oTrashbinNode.getId());
					if (trashbin != null) {
						if(iDeletedNodeCount > 0) {
							img = UIImages.getNodeIcon(IUIConstants.TRASHBINFULL_ICON);
							trashbin.setIcon(img);
						}
						else {
							img = UIImages.getNodeIcon(IUIConstants.TRASHBIN_ICON);
							trashbin.setIcon(img);
						}
					}
				}
			}
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}

		return img;
	}

	/**
	 * Return the id of the trashbin node.
	 * @return String the id of the trashbin node, or null if node not set.
	 */
	public String getTrashBinID() {
		String sID = null;
		if (oTrashbinNode != null) {
			sID = oTrashbinNode.getId();
		}
		return sID;
	}

	/**
	 * Return the id of the inbox node.
	 * @return String the id of the inbox node, or null if node not set.
	 */
	public String getInBoxID() {
		String sID = null;
		if (oInboxNode != null) {
			sID = oInboxNode.getId();
		}
		return sID;
	}
	
	/**
	 * Set the trashbin icon to empty.
	 */
	public void setTrashBinEmptyIcon() {

		//set the trashbin icon to be empty
		UIViewFrame homeFrame = getInternalFrame(getHomeView());
		UINode trashbin = (UINode) ((UIMapViewFrame)homeFrame).getViewPane().get(oTrashbinNode.getId());

		trashbin.setIcon(UIImages.getNodeIcon(IUIConstants.TRASHBIN_ICON));
	}

	/**
	 * Set the trashbin icon to full.
	 */
	public void setTrashBinFullIcon() {

		//set the trashbin icon to be full since a node was deleted
		UIViewFrame homeFrame = getInternalFrame(getHomeView());
		UINode trashbin = (UINode) ((UIMapViewFrame)homeFrame).getViewPane().get(oTrashbinNode.getId());

		trashbin.setIcon(UIImages.getNodeIcon(IUIConstants.TRASHBINFULL_ICON));
	}

	/**
	 * Return the UIViewFrame for the given view.
	 * If not found, crate one.
	 * @param view com.compendium.core.datamode.View, the view to return the frame for.
	 * @param title, the title of the frame.
	 * @return com.compendium.ui.UIViewFrame, the frame for the given view.
	 */
	public UIViewFrame getViewFrame(View view, String title) {

		UIViewFrame viewFrame = null;
		boolean frameFound = false;
		String userID = oModel.getUserProfile().getId();

		for (int i = 0; i < viewFrameList.size(); i++) {
			viewFrame = (UIViewFrame)viewFrameList.elementAt(i);
			if (viewFrame.getView().getId().equals(view.getId())) {

				frameFound = true;
				return viewFrame;
			}
		}

		if(!frameFound) {
			if (view.getModel() == null) {
				view.initialize(oModel.getSession(), oModel);
			}			
			try {
				view.initializeMembers();
			}
			catch(Exception ex) {
				System.out.println("Error (ProjectCompendiumFrame.getViewFrame) \n\n"+ex.getMessage()); //$NON-NLS-1$
			}

			if(view.getType() == ICoreConstants.MAPVIEW) {
				UIMapViewFrame mapFrame = null;
				try {
					mapFrame = new UIMapViewFrame(view, title);
					if (view.equals(oHomeView)) {
						mapFrame.setClosable(false);
					}
					viewFrameList.addElement(mapFrame);
					viewFrame = mapFrame;
				}
				catch(Exception ex) {
					displayError("Cannot instantiate MapView Frame"+ ex.getMessage()); //$NON-NLS-1$
				}
			}
			else if(view.getType() == ICoreConstants.LISTVIEW) {

				// invoke the view frame
				UIListViewFrame listFrame = null;
				try {
					listFrame = new UIListViewFrame(view,title);
					if (view.equals(oHomeView))
						listFrame.setClosable(false);
				}
				catch(Exception ex) {
					displayError("Cannot instantiate ListView Frame" +ex.getMessage()); //$NON-NLS-1$
				}

				// add frame
				viewFrameList.addElement(listFrame);
				viewFrame = (UIViewFrame)listFrame;
			}  else if(view.getType() == ICoreConstants.MOVIEMAPVIEW) {
				UIMovieMapViewFrame timeMapFrame = null;
				try {
					timeMapFrame = new UIMovieMapViewFrame(view, title);
					// Totally messes up re-opening movie maps, so for now, don't cache these views.
					//viewFrameList.addElement(timeMapFrame);
					viewFrame = timeMapFrame;
				}
				catch(Exception ex) {
					displayError("Cannot instantiate MapView Frame"+ ex.getMessage()); //$NON-NLS-1$
				}
			}

		}

		return viewFrame;
	}


	//*********** END GETTER/SETTERS ***************//


	/**
	 * Check to see if a project is currently open before conituing with some earlier process.
	 * If a project is open, tell the suer thier chosen option requires all projects to be closed and
	 * ask the user if they would like to close it before proceeding.
	 * @return boolean, true if a project is still open, else false;
	 */
	private boolean isProjectOpen(String sMessage) {

		if (oModel != null) {
   			int answer = JOptionPane.showConfirmDialog(this, LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "ProjectCompendiumFrame.closeProjectA")+"\n\n"+ //$NON-NLS-1$ //$NON-NLS-2$
   					LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "ProjectCompendiumFrame.closeProjectB")+"\n\n", sMessage, //$NON-NLS-1$ //$NON-NLS-2$
   						JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

			if (answer == JOptionPane.YES_OPTION) {
				onFileClose();
			}
			else {
				return true;
			}
		}
		return false;
	}

	/**
	 * Check to see if a project is currently open before continuing with some earlier process.
	 * If a project is closed, tell the user their chosen option requires an open project.
	 * @return boolean, true if a project is open, else false;
	 */
	private boolean isProjectClosed() {

		if (oModel == null) {
   			JOptionPane.showMessageDialog(this, LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "ProjectCompendiumFrame.errorMessage6a")+"\n"+ //$NON-NLS-1$ //$NON-NLS-2$
   					LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "ProjectCompendiumFrame.errorMessage6b"), //$NON-NLS-1$
   					LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "ProjectCompendiumFrame.errorMesage6Title"), JOptionPane.INFORMATION_MESSAGE); //$NON-NLS-1$
   			return false;
		} else {
			return true;
		}
	}

	/**
	 * Add a new frame to the desktop if required for the given view and return.
	 * Load the frame properties to apply.
	 *
	 * @param view com.compendium.core.datamodel.View, the view to add.
	 * @return UIViewFrame, the frame for the given view.
	 */
	public UIViewFrame addViewToDesktop(View view, String title) {

		setWaitCursor();
		
		UIViewFrame viewFrame = null;
		JInternalFrame[] frames = oDesktop.getAllFrames();

		// CHECK IF VIEW ALREADY OPEN
		for (int i = 0; i < frames.length; i++) {

			viewFrame = (UIViewFrame)frames[i];
			if (viewFrame.getView().getId().equals(view.getId())) {
				try {
					if (viewFrame.isIcon())
						viewFrame.setIcon(false);
				}
				catch(Exception ex) {
					displayError("Exception: (ProjectCompendiumFrame.addViewToDesktop) \n"+ex.getMessage()); //$NON-NLS-1$
				}

				oDesktop.moveToFront(viewFrame);
				if(!viewFrame.isSelected()){
					if (viewFrame instanceof UIMapViewFrame)
						((UIMapViewFrame)viewFrame).setSelected(true);
					else
						((UIListViewFrame)viewFrame).setSelected(true);
				}
				return viewFrame;
			}
		}

		// TRY AND GET VIEW PROPERTIES
		int width = FRWIDTH;
		int height = FRHEIGHT;
		int xPos = FROFFSET * frames.length;
		int yPos = FROFFSET * frames.length;
		boolean isIcon = false;
		boolean isMaximum = false;
		int nHScroll = 0;
		int nVScroll = 0;

		// SBS wanted lists half height of map
		if (view.getType() == ICoreConstants.LISTVIEW)
			height = height/2;

		ViewProperty properties = restoreViewProperties(view);

		if (properties != null) {
			nHScroll = properties.getHorizontalScrollBarPosition();
			nVScroll = properties.getVerticalScrollBarPosition();
			width = properties.getWidth();
			height = properties.getHeight();
			xPos = properties.getXPosition();
			yPos = properties.getYPosition();
			if (xPos < 0) {xPos = 0;}
			if (yPos < 0) {yPos = 0;}
			isIcon = properties.getIsIcon();
			isMaximum = properties.getIsMaximum();
		}

		try {
			view.setState(ICoreConstants.READSTATE);
		}  catch(Exception ex) {}

		return addViewToDesktop(view, title, width, height, xPos, yPos, isIcon, isMaximum, nHScroll, nVScroll);
	}

	/**
	 * Add a new frame to the desktop for the given view and with the given frame properties.
	 *
	 * @param view com.compendium.core.datamodel.View, the view to add.
	 * @param width, the width to make the frame.
	 * @param height, the height to make the frame.
	 * @param xPos, the x position for the frame.
	 * @param yPos, the y position for the frame.
	 * @param isIcon, whether the frame should be iconified.
	 * @param isMaximum, whether the frame should be maximized.
	 * @param HScroll, the hosrizontal scroll bar position to set for the frame.
	 * @param VScroll the vertical scroll bar position to set for the frame.
	 * @return UIViewFrame, the frame for the given view.
	 */
	public UIViewFrame addViewToDesktop(View view, String title, int width, int height, int xPos, int yPos, boolean isIcon, boolean isMaximum, int HScroll, int VScroll) {
		
		UIViewFrame viewFrame = null;
		boolean frameFound = false;
		boolean wasIcon = false;
		//String userID = oModel.getUserProfile().getId();

		// CHECK IF VIEW ALREADY OPEN
		JInternalFrame[] frames = oDesktop.getAllFrames();
		for (int i = 0; i < frames.length; i++) {
			viewFrame = (UIViewFrame)frames[i];
			if ( (viewFrame.getView().getId()).equals(view.getId()) ) {
				try {
					if (viewFrame.isIcon())
						viewFrame.setIcon(false);
				}
				catch(Exception ex) {
					displayError("Exception: (ProjectCompendiumFrame.addViewToDesktop) \n"+ex.getMessage()); //$NON-NLS-1$
				}
				frameFound = true;
				break;
			}
		}

		if(!frameFound) {
			// CHECK IF VIEW HAS BEEN OPENED IN THIS SESSION
			for (int i = 0; i < viewFrameList.size(); i++) {
				viewFrame = (UIViewFrame)viewFrameList.elementAt(i);
				if (viewFrame.getView() != null && viewFrame.getView().getId().equals(view.getId())) {
					try {
						viewFrame.setBounds(xPos, yPos, width, height);
						viewFrame.setHorizontalScrollBarPosition(HScroll, true);
						viewFrame.setVerticalScrollBarPosition(VScroll, true);
						getDesktop().add(viewFrame, VIEWLAYER);
					}
					catch(Exception e) {
						displayError("Exception: (ProjectCompendiumFrame.addViewToDesktop)\ncannot add to the desktop 1 \n" +	e.getMessage()); //$NON-NLS-1$
					}
					frameFound = true;
					break;
				}
			}

			if (!frameFound) {
				try {
					//read in members from the db
					view.initializeMembers();
				}
				catch(Exception ex) {
					ex.printStackTrace();
					displayError("Exception: (ProjectCompendiumFrame.addViewToDesktop-1)\nCannot initialize View \n"+ex.getMessage()); //$NON-NLS-1$
				}
				
				if (xPos > getDesktop().getWidth()-64)  xPos = getDesktop().getWidth()-64;		// Bring off-screen maps back into view
				if (yPos > getDesktop().getHeight()-64) yPos = getDesktop().getHeight()-64;
				if (xPos < 0) xPos = 0;
				if (yPos < 0) yPos = 0;
				
				// CREATE NEW MAP/LIST
				UIMapViewFrame mapFrame = null;
				if(view.getType() == ICoreConstants.MAPVIEW) {
					try {
						mapFrame = new UIMapViewFrame(view, title);
						if (view.equals(oHomeView)) {
							mapFrame.setClosable(false);
						}

						mapFrame.setBounds(xPos, yPos, width, height);
					}
					catch(Exception ex) {
						ex.printStackTrace();
						displayError("Exception: (ProjectCompendiumFrame.addViewToDesktop)\nCannot instantiate MapView Frame \n"+ex.getMessage()); //$NON-NLS-1$
						return viewFrame;
					}

					// add frame
					try {
						getDesktop().add(mapFrame,VIEWLAYER);
						mapFrame.setHorizontalScrollBarPosition(HScroll, true);
						mapFrame.setVerticalScrollBarPosition(VScroll, true);
						UIViewPane pane = mapFrame.getViewPane();
						pane.setZoom(FormatProperties.zoomLevel);
						if (FormatProperties.zoomLevel != 1.0)
							pane.scale();
						viewFrame = (UIViewFrame)mapFrame;
					}
					catch(Exception e) {
						displayError("Exception: (ProjectCompendiumFrame.addViewToDesktop)\ncannot add to the desktop 2 \n" + e.getMessage()); //$NON-NLS-1$
						return viewFrame;
					}
					viewFrameList.addElement(viewFrame);
				}
				else if(view.getType() == ICoreConstants.LISTVIEW) {
					// invoke the view frame
					UIListViewFrame listFrame = null;
					try {
						listFrame = new UIListViewFrame(view, title);

						if (view.equals(oHomeView)) {
							listFrame.setClosable(false);
						}
						if (view.equals(getInBoxView())) {				// Sort inbox by Creation date
							listFrame.getUIList().sortByCreationDate();
						}
						listFrame.setBounds(xPos, yPos, width, height);
					}
					catch(Exception ex) {
						ex.printStackTrace();
						displayError("Exception: (ProjectCompendiumFrame.addViewToDesktop)\nCannot instantiate ListView Frame \n"+ex.getMessage()); //$NON-NLS-1$
						return viewFrame;
					}

					// add frame
					try {
						getDesktop().add(listFrame,VIEWLAYER);
						listFrame.setHorizontalScrollBarPosition(HScroll, true);
						listFrame.setVerticalScrollBarPosition(VScroll, true);
						viewFrame = (UIViewFrame)listFrame;
					}
					catch(Exception e) {
						displayError("Exception: (ProjectCompendiumFrame.addViewToDesktop)\ncannot add to the desktop 3 \n" + e.getMessage()); //$NON-NLS-1$
						return viewFrame;
					}
					viewFrameList.addElement(viewFrame);
				} else if(view.getType() == ICoreConstants.MOVIEMAPVIEW) {
					UIMovieMapViewFrame movieFrame = null;
					try {
						movieFrame = new UIMovieMapViewFrame(view, title);
						movieFrame.setBounds(xPos, yPos, width, height);
					}
					catch(Exception ex) {
						ex.printStackTrace();
						displayError("Exception: (ProjectCompendiumFrame.addViewToDesktop)\nCannot instantiate MapView Frame \n"+ex.getMessage()); //$NON-NLS-1$
						return viewFrame;
					}

					// add frame
					try {
						getDesktop().add(movieFrame,VIEWLAYER);
						movieFrame.setHorizontalScrollBarPosition(HScroll, true);
						movieFrame.setVerticalScrollBarPosition(VScroll, true);
						viewFrame = (UIViewFrame)movieFrame;
					}
					catch(Exception e) {
						e.printStackTrace();
						displayError("Exception: (ProjectCompendiumFrame.addViewToDesktop)\ncannot add to the desktop 4 \n" + e.getMessage()); //$NON-NLS-1$
						return viewFrame;
					}
					// Totally messes up re-opening movie maps, so for now, don't cache these views.
					//viewFrameList.addElement(viewFrame);
				} 				
			} 
			
			//enable the view detail menu

			wasIcon = viewFrame.isIcon();
			try {
				if (wasIcon != isIcon)
					viewFrame.setIcon(isIcon);
				viewFrame.setMaximum(isMaximum);
				int count = oToolBarManager.getTextZoom();
				boolean increase = false;
				if (count > 0) {
					increase = true;
				} else if (count < 0) {
					count = count * -1;
				}
				if (count != 0) {
					UIViewPane pane = null;
					UIList list = null;
					if (viewFrame instanceof UIMapViewFrame) {
						pane = ((UIMapViewFrame)viewFrame).getViewPane();
					} else if (viewFrame instanceof UIListViewFrame) {
						list = ((UIListViewFrame)viewFrame).getUIList();
					}

					for (int i=0; i<count; i++) {
						if (pane != null) {
							if (increase) {
								pane.onIncreaseTextSize();
							} else {
								pane.onReduceTextSize();
							}
						} else if (list != null) {
							if (increase) {
								list.onIncreaseTextSize();
							} else {
								list.onReduceTextSize();
							}
						}
					}
				}
			}
			catch(Exception ex) {
				displayError("Exception: (ProjectCompendiumFrame.addViewToDesktop) \n"+ex.getMessage()); //$NON-NLS-1$
			}
		}

		// BUG-FIX
		try {
			viewFrame.setVisible(true);
		} catch (Exception ex) {}

		// DEICONIFICATION DOES THIS ANYWAY, SO NO NEED TO DO AGAIN
		if ( !wasIcon && !isIcon ) {
			if (viewFrame instanceof UIMapViewFrame) {
				((UIMapViewFrame)viewFrame).setSelected(true);
			} else if (viewFrame instanceof UIListViewFrame) {
				((UIListViewFrame)viewFrame).setSelected(true);
			} 
		}

		this.setDefaultCursor();

		return viewFrame;
	}

	/**
	 * Return the currently selected frame.
	 * @return com.compendium.ui.UIViewFrame, the currently selected frame.
	 */
	public UIViewFrame getCurrentFrame() {

		//get the active frame to find the active view
		UIViewFrame viewFrame = null;
		boolean frameFound = false; int i=0;

		JInternalFrame[] frames = oDesktop.getAllFrames();
		while(!frameFound && i<frames.length) {
			viewFrame = (UIViewFrame)frames[i++];
			if (viewFrame.isSelected()) {
				frameFound = true;
			}
		}

		// CHECK HOME FRAME (AS NOT ALWAYS SELECTED EVEN IF A NODE IS)
		if (!frameFound) {
			viewFrame = getInternalFrame(oHomeView);
		}

		return viewFrame;
	}

	/**
	 * Return the UIViewFrame for the given View else null.
	 * @param view com.compendium.core.datamodel.View, the view to return the frame for.
	 * @return the com.compendium.ui.UIViewFrame for the given view, else null.
	 */
	public UIViewFrame getInternalFrame(View view) {

		UIViewFrame viewFrame = null;

		JInternalFrame[] frames = oDesktop.getAllFrames();
		for (int i = 0; i < frames.length; i++) {
			viewFrame = (UIViewFrame)frames[i];
			if (viewFrame.getView().getId().equals(view.getId())) {
				return viewFrame;
			}
		}
		return null;
	}

	/**
	 * Returns a list of all UIViewFrame currently open.
	 * @return Vector, a list of all UIViewFrame currently open.
	 */
	public Vector getAllFrames() {

		UIViewFrame viewFrame = null;
		Vector vtFrames = new Vector(51);
		JInternalFrame[] frames = oDesktop.getAllFrames();
		for (int i = 0; i < frames.length; i++) {
			viewFrame = (UIViewFrame)frames[i];
			vtFrames.addElement(viewFrame);
		}
		return vtFrames;
	}

	/**
	 *  Creates a set of current codes for this project which can be used by the user.
	 */
	public void loadAllCodes() {

		try	{
			oModel.loadAllCodes();
		}
		catch(Exception ex)	{
			displayError("Exception: (ProjectCompendiumFrame.loadAllCodes) " + ex.getMessage()); //$NON-NLS-1$
		}
	}

	/**
	 *  Creates a set of current code group which can be used by the user.
	 * 	And loads the active link group.
	 */
	public void loadAllCodeGroups() {

		try	{
			oModel.loadAllCodeGroups();

			// INITIATE ACTIVE CODE GROUP
			SystemService service = (SystemService)oModel.getSystemService();
			activeGroup	= service.getCodeGroup(oModel.getSession());
			activeLinkGroup	= service.getLinkGroup(oModel.getSession());
		}
		catch(Exception ex)	{
			displayError("Exception: (ProjectCompendiumFrame.loadAllCodeGroups) \n" + ex.getMessage()); //$NON-NLS-1$
		}
	}


	/**
	 * Cleanup the model variables and services.
	 */
	public void cleanupServices() {
		try {
			if (oServiceManager != null && oModel != null)
				oServiceManager.cleanupServices(oModel.getSession().getSessionID(), sUserName);

			if (oModel != null)
				oModel.cleanUp(); //must do this last as is required by ServiceManager
		}
		catch(Exception e) {
			System.out.println(LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "ProjectCompendiumFrame.350")+e.getMessage()); //$NON-NLS-1$
		}
	}

	/**
	 *	Create the cliboard used by this frame.
	 */
	public void createClipboard() {
		oClipboard = new Clipboard("ProjectCompendiumClipboard"); //$NON-NLS-1$
	}

	/**
	 * Set the clipboard used by this frame.
	 * @param clipboard, the clipboard used by this frame.
	 */
	public void setClipboard(Clipboard clipboard) {
		oClipboard = clipboard;
	}

	/**
	 * Return the clipboard used by this frame.
	 * @return Clipboard, the clipboard used by this frame.
	 */
	public Clipboard getClipboard() {
		return oClipboard;
	}

	/**
	 * Validate all the component in all the open views.
	 */
	public void validateComponents() {

		ProjectCompendiumFrame.this.validateTree();

		UIViewFrame viewFrame = null;
		boolean frameFound = false; int i=0;

		JInternalFrame[] frames = oDesktop.getAllFrames();
		while(i<frames.length) {
			viewFrame = (UIViewFrame)frames[i++];
			if (viewFrame instanceof UIListViewFrame) {
				((UIListViewFrame)viewFrame).getUIList().validateComponents();
			}
			else {
				((UIMapViewFrame)viewFrame).getViewPane().validateComponents();
			}
		}
	}

	/**
	 * Returns the UIAudio thread which plays audio.
	 * @return UIAudio, the UIAudio thread which plays audio.
	 */
	public UIAudio getAudioPlayer() {
		 return audioThread;
	}

	 /**
	  * Inner class for holding the import profile for the user.
	  */
	 private class ImportProfile {

		 private boolean normalImport = true;
		 private boolean includeInDetail = false;
		 private boolean preserveIDs = false;
		 private boolean transclusion = false;

		 public ImportProfile(boolean normal, boolean include, boolean preserveids, boolean transclude) {
			normalImport = normal;
			includeInDetail = include;
			preserveIDs = preserveids;
			transclusion = transclude;
		 }

		 public ImportProfile() {}

		 public Vector getProfile() {
			Vector vtProfiles = new Vector(51);

			vtProfiles.addElement(new Boolean(normalImport));
			vtProfiles.addElement(new Boolean(includeInDetail));
			vtProfiles.addElement(new Boolean(preserveIDs));
			vtProfiles.addElement(new Boolean(transclusion));
			return vtProfiles;
		 }

		 public void setProfile(boolean normal, boolean include, boolean preserveids, boolean transclude) {
			normalImport = normal;
			includeInDetail = include;
			preserveIDs = preserveids;
			transclusion = transclude;
		 }
	 }

  	/**
   	 * Print the current view.
     */
   	public void onPrint() {

		UIViewFrame currentFrame = getCurrentFrame();
		PrintRequestAttributeSet aset = new HashPrintRequestAttributeSet();
		aset.add(OrientationRequested.LANDSCAPE);
		aset.add(MediaSizeName.ISO_A4);
		aset.add(new Copies(1));
		aset.add(new JobName(currentFrame.getView().getLabel(), null));
		
		if (currentFrame instanceof UIListViewFrame) {
			UIListViewFrame listFrame = (UIListViewFrame)currentFrame;
			UIList uiList = listFrame.getUIList();
			uiList.print(aset);
		} else {
	       	PrinterJob pj = PrinterJob.getPrinterJob();
			pj.setPrintable( ((UIMapViewFrame)currentFrame).getViewPane());
			try {
				if(pj.printDialog(aset)) {
					pj.print(aset);
				}
			} catch (PrinterException pe) {
				System.err.println(pe);
			}
		}
	}

 	/**
	 * Display an error message dialog with the given message.
	 * @param error, the error message to display.
	 */
   	public void displayError(String error) {
   		System.out.println("Error:" + error); //$NON-NLS-1$
		JOptionPane.showMessageDialog(this, error, "Error", JOptionPane.ERROR_MESSAGE); //$NON-NLS-1$
   	}

 	/**
	 * Display an error message dialog with the given message an title.
	 * @param error, the error message to display.
	 * @param sTitle, the title for the message window.
	 */
  	public void displayError(String error, String sTitle) {
   		System.out.println("Error:" + error); //$NON-NLS-1$
		JOptionPane.showMessageDialog(this, error, sTitle, JOptionPane.ERROR_MESSAGE);
   	}

	/**
	 * Display a dialog with the given message an title.
	 * @param message, the message to display.
	 * @param sTitle, the title for the message window.
	 */
   	public void displayMessage(String message, String sTitle) {
		JOptionPane.showMessageDialog(this, message, sTitle, JOptionPane.INFORMATION_MESSAGE);
   	}

	/**
	 * Refresh the Windows menu for the currently open frames.
	 */
	public void refreshWindowsMenu() {
		oMenuManager.refreshWindowsMenu();
	}

	/**
	 * When a window is de-iconified / activated, add it to the hitory and Windows menu.
	 * Reset its zoom level and update its aerial view.
	 * @param window com.compendium.ui.UIViewFrame, the frame that has been de-iconified / activated.
	 */
	public void activateWindow(UIViewFrame window) {

		if (window instanceof UIListViewFrame) {
			oMenuManager.setMapMenuEnabled(false);
			oMenuManager.setScribblePadEnabled(false);
			oToolBarManager.setZoomToolBarEnabled(false);
			oToolBarManager.setDrawToolBarEnabled(false);
		}
		else {
			oMenuManager.setMapMenuEnabled(true);
			oMenuManager.setScribblePadEnabled(true);
			oToolBarManager.setZoomToolBarEnabled(true);
			oToolBarManager.setDrawToolBarEnabled(true);
		}

		resetZoom();

		oToolBarManager.addToHistory(window.getView());

		refreshWindowsMenu();

		oToolBarManager.enableHistoryButtons();

		updateAerialView();
	}

/////////////////////////////////////////////////////////////////////////

/**
 * Start a recording of a meeting from data passed through web launch.
 * @param sData  the record setup data required.
 */
public void setupForRecording(String sSetupData) {

	try {
		oMeetingManager = new MeetingManager(MeetingManager.RECORDING);
		if (oMeetingManager.processSetupData(sSetupData)) {
			oMeetingManager.setupMeetingForRecording();
		}
	} catch (AccessGridDataException ex) {
		displayError(ex.getMessage());
	}
}

/**
 * Start a replay of a meeting from data passed through web launch.
 * @param sData  the replay setup data required.
 */
public void setupForReplay(String sSetupData, String sReplayData) {

	try {
		oMeetingManager = new MeetingManager(MeetingManager.REPLAY);
		if (oMeetingManager.processSetupData(sSetupData)) {
			oMeetingManager.processReplayData(sReplayData);
            oMeetingManager.setupMeetingForReplay();
		}
	} catch (AccessGridDataException ex) {
		displayError(ex.getMessage());
	}
}


/////////////////////////////////////////////////////////////////////////

// RESTORE CODE //

	/** Holds restored views.*/
	Hashtable restoredViews = new Hashtable(51);

	/** Holds the restored nodes indent level.*/
	int restoreIndent = 0;

	/**
	 * Restore the given node in the given to.
	 * @param node com.compendium.core.datamodel.NodeSummary, the node to restore.
	 * @param view com.compendium.core.datamodel.View, the view to restore the node to.
	 */
	public void restore(NodeSummary node, View view) {

		// FOR DUPLICATION CHECK
		restoredViews.clear();

		restoreIndent = 0;
		restoreNode(node, view);
	}

	/**
	 * Restore the given node in the given to.
	 * @param node com.compendium.core.datamodel.NodeSummary, the node to restore.
	 * @param view com.compendium.core.datamodel.View, the view to restore the node to.
	 */
	public void restoreNode(NodeSummary node, View view) {

		PCSession session = oModel.getSession();
		NodeService nodeService = (NodeService)oModel.getNodeService();
		
		UIViewFrame viewFrame = addViewToDesktop(view, view.getLabel() );
		Vector history = new Vector();
		history.addElement(new String("Restored")); //$NON-NLS-1$
		viewFrame.setNavigationHistory(history);

		// Bug Fix  - Lakshmi (9/13/06)
		// Included check if viewFrame instanceof UIMapViewFrame to avoid ClassCastException
		// when restoring a node to a list.
		
		ViewPaneUI viewpaneui = null;
		if(viewFrame instanceof  UIMapViewFrame)
			viewpaneui = ((UIMapViewFrame)viewFrame).getViewPane().getUI();
		
		try {
			// CHECK DELETED STATUS
			boolean wasDeleted = false;
			if (oModel.getNodeService().isMarkedForDeletion(session, node.getId())) {
				wasDeleted = true;
			}

			String sNodeID = node.getId();
			String sViewID = view.getId();

			NodePosition oPos = null;

			// RESTORE NODE RELATED VIEWNODE
			boolean restored = nodeService.restoreNode(session, sNodeID);
			if (restored)
				oPos = nodeService.restoreNodeView(session, sNodeID, sViewID);

			// IF THIS NODE IS A VIEW AND WAS DELETED, RESTORE ITS CHILDREN
			int nodeType = node.getType();
			if ( View.isViewType(nodeType) && wasDeleted )
				restoreView(view, (View)node, session, nodeService);

			// IF NODE POSITION FAILED, RESTORE WITH NEW POSITION
			if (oPos == null) {
				int xpos = 0;
				int ypos = 0;
				if (View.isMapType(view.getType())) {
					xpos = (restoreIndent+1)*20;
					ypos = 150+restoreIndent*20;
					restoreIndent++;
				}
				else if (View.isListType(view.getType())) {
					xpos = 0;
					ypos = ( ((UIListViewFrame)viewFrame).getUIList().getNumberOfNodes() + 1) * 10;
				}
				oPos = view.addNodeToView(node, xpos, ypos);
			}
			else {
				oPos.setNode(node);
				oPos = view.addMemberNode(oPos);
			}
			oPos.getNode().initialize(session, oModel);

			if (View.isMapType(view.getType())) {

				UINode uinode = viewpaneui.addNode(oPos);

				// RESTORE RELATED LINKS AND VIEWLINKS FOR THIS NODE IF VIEW IS A MAP
				// NB. MUST DO THIS AFTER NODE ADDED TO LAYER OR addLink METHOD WILL FAIL
				try {
					Vector links = oModel.getLinkService().restoreNode(session, sNodeID, sViewID);
					if (links != null) {
						final int count = links.size();
						for (int i=0; i<count; i++) {
							LinkProperties linkProps = (LinkProperties)links.elementAt(i);
							view.addMemberLink(linkProps);
							viewpaneui.addLink(linkProps);
						}
					}
				}
				catch(Exception ex) {
					displayError("Exception: (ProjectcompendiumFrame.restoreLinks) \n"+ex.getMessage()); //$NON-NLS-1$
				}
			}
			else {
				UIList uiList = ((UIListViewFrame)viewFrame).getUIList();
				uiList.insertNode(oPos, uiList.getNumberOfNodes());
				uiList.selectNode(uiList.getNumberOfNodes() - 1, ICoreConstants.MULTISELECT);
				uiList.updateTable();
			}
		}
		catch(Exception ex) {
			ex.printStackTrace();
			displayError("Exception: (ProjectCompendiumFrame.restoreNode) \n"+ex.getMessage()); //$NON-NLS-1$
		}
	}

	/**
	 * Restore the given view from the database after a previous deletion.
	 * @param parent com.compendium.core.datamodel.View, the parent view of the view being restored.
	 * @param view com.compendium.core.datamodel.View, the view to restore.
	 */
	private void restoreView(View parent, View view, PCSession session, NodeService nodeService) throws Exception {

		String sViewID = view.getId();
		String userID = oModel.getUserProfile().getId();

		// DON'T RESTORE THE SAME VIEW TWICE :WHEN VIEW CONTAINS ITSELF SOMEWHERE IN CHILDREN TREE
		if (restoredViews.containsKey(sViewID)) {
			return;
		}
		else {
			restoredViews.put(sViewID, view);
		}

		// IF THIS VIEW IS A MAP RESTORE ALL (LINKS AND) NODES
		if (View.isMapType(view.getType())) {
			oModel.getViewService().restoreViewLinks(session, sViewID);
		}
		nodeService.restoreView(session, sViewID);

		Vector vtNodePos = oModel.getViewService().getNodePositions(oModel.getSession(), sViewID);
		for(Enumeration en = vtNodePos.elements(); en.hasMoreElements();) {

			NodeSummary node = (NodeSummary)((NodePosition)en.nextElement()).getNode();
			String sNodeID = node.getId();
			int innerIndent = 0;

			// IF THIS NODE IS A VIEW, RESTORE ITS CHILDREN
			int nodeType = node.getType();
			if (View.isViewType(nodeType)) {
				restoreView(view, (View)node, session, nodeService);
			}
		}

		// RE-FILL OBJECT WITH NEWLY RESTORED NODES AND LINKS
		view.setIsMembersInitialized(false);
		view.initializeMembers();

		// IF VIEW HAS BEEN OPENED, GET IT TO RE-FILL ITSELF WITH THE NEW DATA
		UIViewFrame viewFrame = null;
		for (int i = 0; i < viewFrameList.size(); i++) {
			viewFrame = (UIViewFrame)viewFrameList.elementAt(i);
			if (viewFrame.getView().getId().equals(view.getId())) {

				// CREATE NEW MAP/LIST
				if(view.getType() == ICoreConstants.MAPVIEW) {
					UIMapViewFrame mapFrame = (UIMapViewFrame)viewFrame;
					mapFrame.setView(view);
					mapFrame.getViewPane().setView(view);
					mapFrame.getViewPane().updateUI();
				} else if(view.getType() == ICoreConstants.LISTVIEW) {
					UIListViewFrame listFrame = (UIListViewFrame)viewFrame;
					listFrame.createList(view);
				} else if (view.getType() == ICoreConstants.MOVIEMAPVIEW) {
					UIMovieMapViewFrame mapFrame = (UIMovieMapViewFrame)viewFrame;
					mapFrame.setView(view);
					mapFrame.getViewPane().setView(view);
					mapFrame.getViewPane().updateUI();
				}
			}
		}
	}

	/**
	 * Restore the view frame properties for the given view.
	 * @param view com.compendium.core.datamodel.View, the view to restore the view frame properties for.
	 */
	private ViewProperty restoreViewProperties(View view) {

		ViewProperty properties = null;
		try {
			String sUserID = oModel.getUserProfile().getId();
			ViewPropertyService viewserv = (ViewPropertyService)oModel.getViewPropertyService();

			if (view.getId() != "") //$NON-NLS-1$
				properties = viewserv.getViewPosition(oModel.getSession(), sUserID, view.getId());
		}
		catch(Exception io) {
			io.printStackTrace();
		}

		return properties;
	}
	

	/**
	 * Save the properties of the given view to the database.
	 * @param viewFrame com.compendium.ui.UIViewFrame, the view frame to save the proerpties for.
	 */
	public void saveViewProperties(UIViewFrame viewFrame) {

		String sUserID = oModel.getUserProfile().getId();
		ViewPropertyService viewserv = (ViewPropertyService)oModel.getViewPropertyService();

		String sViewID = viewFrame.getView().getId();

		Rectangle rect = viewFrame.getNormalBounds();
		int width = rect.width;
		int height = rect.height;
		int nX = rect.x;
		int nY = rect.y;

		ViewProperty view = new ViewProperty();
		view.setUserID(sUserID);
		view.setViewID(viewFrame.getView().getId());
		view.setWidth(width);
		view.setHeight(height);
		view.setXPosition(nX);
		view.setYPosition(nY);
		view.setHorizontalScrollBarPosition(viewFrame.getHorizontalScrollBarPosition());
		view.setVerticalScrollBarPosition(viewFrame.getVerticalScrollBarPosition());
		view.setIsIcon(viewFrame.isIcon());
		view.setIsMaximum(viewFrame.isMaximum());

		if (!sViewID.equals("")) { //$NON-NLS-1$

			// CHECK IF RECORD FOR THIS VIEW AND USER ALREADY EXISTS
			// SO WE CAN DECIDE WETHER TO UPDATE OR INSERT
			try {
				PCSession session = oModel.getSession();
				ViewProperty current = viewserv.getViewPosition(session, sUserID, sViewID);

				if (current != null)
					viewserv.updateViewProperty(session, sUserID, view);
				else
					viewserv.createViewProperty(session, sUserID, view);
			}
			catch(Exception io) {
				io.printStackTrace();
			}
		}
	}

	/**
  	 * Activate menu options when a node has been selected/deselected.
  	 * @param selected, true to enable, false to disable.
	 * @see com.compendium.ui.UIToolBarManager#setNodeSelected
	 */
	public void setNodeSelected(boolean selected) {
		oToolBarManager.setNodeSelected(selected);
		oMenuManager.setNodeSelected(selected);		
	}

	/**
 	 * Activate menu and toolbar options when nodes and links have been selected/deselected.
 	 * @param selected boolean.
	 * @see com.compendium.ui.UIToolBarManager#setNodeOrLinkSelected
	 * @see com.compendium.ui.UIMenuManager#setNodeOrLinkSelected
	 */
	public void setNodeOrLinkSelected(boolean selected) {
		oToolBarManager.setNodeOrLinkSelected(selected);
		oMenuManager.setNodeOrLinkSelected(selected);
	}

	/**
	 * Enable/disable the menu and toolbar paste items.
	 * @param enabled boolean, true to enable, false to disable.
	 * @see com.compendium.ui.UIToolBarManager#setPasteEnabled
	 * @see com.compendium.ui.UIMenuManager#setPasteEnabled
	 */
	public void setPasteEnabled(boolean enabled) {
		isPasteEnabled = enabled ; 
		oToolBarManager.setPasteEnabled(enabled);
		oMenuManager.setPasteEnabled(enabled);
	}
}
