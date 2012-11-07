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

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.awt.*;
import java.util.*;
import javax.swing.*;

import com.compendium.*;
import com.compendium.core.datamodel.LinkedFile;
import com.compendium.core.datamodel.LinkedFileDatabase;

/**
 * This is an execute control class that invokes the default system
 * browser or other application as required. Can be used for UNIX or Windows platforms (for browser execution only).
 *
 * To execute this class to open a browser from outside env, type the class name
 * followed by the URL to display
 *
 * @author Mohammed Sajid Ali / Michelle Bachler
 */
public class ExecuteControl {

    /** The default system browser under windows.*/
    //private static final String WIN_PATH = "rundll32";

    /** The flag to display a url.*/
    //private static final String WIN_FLAG = "url.dll,FileProtocolHandler";

    /** The default browser under unix.*/
    //private static final String UNIX_PATH = "netscape";

    /** The flag to display a url.*/
    //private static final String UNIX_FLAG = "-remote openURL";

    /**
     * Launch a file or URL in the relevant application.
	 * @author Sebastian Ehrich
     * @param path the path to the file or URL path to launch").
     */
    public static String launch( String path ) {
		try {
			if (path != null && !path.equals("")) { //$NON-NLS-1$
				/* fetch file from database if needed */
				if (LinkedFileDatabase.isDatabaseURI(path)) {
					LinkedFile lf;
					try {
						lf = new LinkedFileDatabase(new URI(path));
					} catch (URISyntaxException e1) {
						System.err.println("Exception: (ExecuteControl.launch) Could not create URI for linked File."); //$NON-NLS-1$
						e1.printStackTrace();
						return (null);
					}
					File tempFile = lf.getFile(ProjectCompendium.temporaryDirectory);
					if (launchFile(tempFile.getAbsolutePath())) {
						updateDBWhenFinished(lf, tempFile);
						return path;
					} else
						return null;
				} else {
					if (launchFile(UIUtilities.modifyPath(path)))
						return path;
					else
						return null;
				}
			}
		} catch (IllegalThreadStateException e) {
			System.out.println("Exception: (ExecuteControl.launch) " //$NON-NLS-1$
					+ e.getMessage());
			return null;
		} catch (IOException e) {
			System.out.println("Exception: (ExecuteControl.launch) " //$NON-NLS-1$
					+ e.getMessage());
			return null;
		} catch (InterruptedException e) {
			System.out.println("Exception: (ExecuteControl.launch) " //$NON-NLS-1$
					+ e.getMessage());
			return null;
		} catch (Exception e) {
			System.out.println("Exception: (ExecuteControl.launch) " //$NON-NLS-1$
				+ e.getMessage());
			return null;
		}
		return null;
	}
    
	/**
	 * Launch relevant application to view/edit the file given by "path". This 
	 * method blocks until the application has finished. 
	 * @param path path or URI to the file to open
	 * @return true when an application was launched successfully, false otherwise.
	 * @throws IllegalThreadStateException
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static boolean launchFile(String path)
			throws IllegalThreadStateException, IOException,
			InterruptedException {
		
		if (ProjectCompendium.isWindows) {
			return launchWindowsCommand(path);
		} else if (ProjectCompendium.isMac) {
			return launchMacCommand(path);
		} else if (ProjectCompendium.isLinux) {
			if (FormatProperties.useKFMClient) {
				return launchLinuxCommandKDE(path);
			} else {
				return launchLinuxCommand(path);
			}
		}
		return false;

	}
	
	   /**
     * Pops up a non-modal dialog asking the user whether he has finished viewing
     * or editing the given temporary copy of a database file and updates the 
     * database depending on user input. 
	 * <br />
     * Note: It would be nice not to have to ask the user about when he has 
     * finished editing. But an automated solution seems not feasible as there are
     * so many differences in how each individual application behaves, let alone on
     * different OSes.
	 * @author Sebastian Ehrich
     * @param lf The Compendium object representing a file in the database.
     * @param tmpcopy The temporary copy of the database object that is currently
     * 	viewed/edited. This file will be deleted (except for when the user chooses
     * 	to update but update fails). 
     */
    private static void updateDBWhenFinished( LinkedFile lf, File tmpcopy ) {

    	final JOptionPane jop = new JOptionPane();
    	final Object[] possibleValues = { 
    			 LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "ExecuteControl.option1"), //$NON-NLS-1$
    			 LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "ExecuteControl.option2") }; //$NON-NLS-1$
    	String message =  LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "ExecuteControl.message1a") +"\n"+ //$NON-NLS-1$ //$NON-NLS-2$
        					LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "ExecuteControl.message1b")+":"; //$NON-NLS-1$ //$NON-NLS-2$
    	
    	jop.setMessage(message);
    	jop.setOptions(possibleValues);
    	jop.setMessageType(JOptionPane.INFORMATION_MESSAGE);
    	
    	jop.addPropertyChangeListener(new DBUpdateListener(lf, tmpcopy, jop));
        JDialog jd = jop.createDialog(ProjectCompendium.APP, LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "ExecuteControl.messageTitle") + lf.getName()); //$NON-NLS-1$
        jd.setModal(false);
        jd.setVisible(true);
    }
    
    /**
     * Display a file or URL in the system application on a Windows Machine.
     *
     * @param path, the path to the file or URL path to launch").
     */
    private static boolean launchWindowsCommand(String path) throws IOException, InterruptedException, IllegalThreadStateException {
		if (ProjectCompendium.platform.indexOf("98") != -1) { //$NON-NLS-1$
		    Process p = Runtime.getRuntime().exec("start \"" + path + "\""); //$NON-NLS-1$ //$NON-NLS-2$
		    p.waitFor();
		}
		else {
		    Process p = Runtime.getRuntime().exec("cmd.exe /c start \"Compendium Reference node\" \"" + path + "\""); //$NON-NLS-1$ //$NON-NLS-2$
		    p.waitFor();
		}
		return true;
    }

    /**
      * Display a file or URL in the system application on a Mac machine.
      *
      * @param path, the path to the file or URL path to launch").
      */
    private static boolean launchMacCommand(String path) throws IOException, InterruptedException, IllegalThreadStateException {

		Process p = Runtime.getRuntime().exec(new String[] {"open", path}); //$NON-NLS-1$
		if (p.waitFor() != 0) {

		    // DID WE STORE THE INFO FROM A PREVIOUS ATTEMPT?
		    String refString = path.toLowerCase();
		    String key = ""; //$NON-NLS-1$
		    if (refString.startsWith("www.")) //$NON-NLS-1$
				key = "www"; //$NON-NLS-1$
			else if (refString.startsWith("http:")) //$NON-NLS-1$
				key = "http"; //$NON-NLS-1$
			else if (refString.startsWith("https:")) //$NON-NLS-1$
				key = "https"; //$NON-NLS-1$
			else if (refString.startsWith("file:")) //$NON-NLS-1$
				key = "file";		     //$NON-NLS-1$
			else if ( refString.indexOf("\n") == -1 && refString.indexOf("\r") == -1 //$NON-NLS-1$ //$NON-NLS-2$
			       && refString.length() <= 100	&& refString.indexOf("@") != -1) //$NON-NLS-1$
				key = "email"; //$NON-NLS-1$
			else {
				int index = refString.lastIndexOf("."); //$NON-NLS-1$
				if (index != -1) {
				    key = refString.substring(index+1);
				}
			}

		    String application = ""; //$NON-NLS-1$
		    Properties apps = null;
		    if (!key.equals("")) { //$NON-NLS-1$
				apps = ProjectCompendium.APP.getLaunchApplications();
				String value = apps.getProperty(key);
				if (value != null)
			    	application = value;
		    }

		    if (!application.equals("")) { //$NON-NLS-1$
				p = Runtime.getRuntime().exec(new String[] {"open", "-a", application, path}); //$NON-NLS-1$ //$NON-NLS-2$
				if (p.waitFor() != 0)
			    	System.out.println("FAILED to launch "+path); //$NON-NLS-1$
				else
			    	return true;
		    }

		    // We get here when 'open' couldn't open the file, so we
	        // need to ask the user to find the app for us
			UIFileChooser fileDialog = new UIFileChooser();
			fileDialog.setDialogTitle(LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "ExecuteControl.selectApplication")); //$NON-NLS-1$
			fileDialog.setApproveButtonText(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "ExecuteControl.loadButton")); //$NON-NLS-1$
			fileDialog.setApproveButtonMnemonic(LanguageProperties.getString(LanguageProperties.DIALOGS_BUNDLE, "ExecuteControl.loadButtonMnemonic").charAt(0)); //$NON-NLS-1$
		    int retval = fileDialog.showOpenDialog(ProjectCompendium.APP);
		    if (retval == JFileChooser.APPROVE_OPTION) {
			    if (fileDialog.getSelectedFile() != null) {
					// Now we can try to open the file again
					File app = new File(fileDialog.getSelectedFile().getAbsolutePath());
					p = Runtime.getRuntime().exec(new String[] {"open", "-a", app.getPath(), path}); //$NON-NLS-1$ //$NON-NLS-2$
					if (p.waitFor() != 0)
					    System.out.println("FAILED to launch "+path); //$NON-NLS-1$
					else {
				    	if (!key.equals("")) { //$NON-NLS-1$
							apps.put(key, app.getPath());
							apps.store(new FileOutputStream("System"+ProjectCompendium.sFS+"resources"+ProjectCompendium.sFS+"LaunchApplications.properties"), "Launch Application Details"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
				    	}
				    	return true;
					}
			    }
		    }
		}
		else {
		    return true;
		}

		return false;
    }

    /**
      * Display a file or URL in the system application on a Linux machine.
      *
      * @param path, the path to the file or URL path to launch").
      */
    private static boolean launchLinuxCommand(String path) throws IOException, InterruptedException, IllegalThreadStateException {

		String refString = path.toLowerCase();
		String key = ""; //$NON-NLS-1$
		if (refString.startsWith("www.")) //$NON-NLS-1$
	    	key = "www"; //$NON-NLS-1$
		else if (refString.startsWith("http:")) //$NON-NLS-1$
	    	key = "http"; //$NON-NLS-1$
		else if (refString.startsWith("https:")) //$NON-NLS-1$
	    	key = "https"; //$NON-NLS-1$
		else if (refString.startsWith("file:")) //$NON-NLS-1$
			key = "file";		    		 //$NON-NLS-1$
		else if ( refString.indexOf("\n") == -1 && refString.indexOf("\r") == -1 //$NON-NLS-1$ //$NON-NLS-2$
	   				&& refString.length() <= 100	&& refString.indexOf("@") != -1) //$NON-NLS-1$
	    	key = "email"; //$NON-NLS-1$
		else {
	    	int index = refString.lastIndexOf("."); //$NON-NLS-1$
	    	if (index != -1) {
				key = refString.substring(index+1);
			}
	    }

		String application = ""; //$NON-NLS-1$
		Properties apps = null;
		if (!key.equals("")) { //$NON-NLS-1$
	    	apps = ProjectCompendium.APP.getLaunchApplications();
	    	String value = apps.getProperty(key);
	    	if (value != null)
				application = value;
		}

		if (!application.equals("")) { //$NON-NLS-1$
	    	Process p = Runtime.getRuntime().exec(new String[] {application, path});
            if (p.waitFor() != 0)
				System.out.println("FAILED to launch "+path); //$NON-NLS-1$
            else
				return true;
		}

		// We get here when 'open' couldn't open the file, so we
        // need to ask the user to find the app for us
		JFileChooser fileDialog = new JFileChooser();
		fileDialog.setDialogTitle(LanguageProperties.getString(LanguageProperties.UI_GENERAL_BUNDLE, "ExecuteControl.selectApplication")); //$NON-NLS-1$
		int retval = fileDialog.showOpenDialog(ProjectCompendium.APP);
		if (retval == JFileChooser.APPROVE_OPTION) {
	    	if ((fileDialog.getSelectedFile()) != null) {
				String fileName = fileDialog.getSelectedFile().getAbsolutePath();
				File app = new File(fileName);

				Process p = Runtime.getRuntime().exec(new String[] {app.getPath(), path});
				if (p.waitFor() != 0)
			    	System.out.println("FAILED to launch "+path); //$NON-NLS-1$
				else {
			    	if (!key.equals("")) { //$NON-NLS-1$
						apps.put(key, app.getPath());
						apps.store(new FileOutputStream("System"+ProjectCompendium.sFS+"resources"+ProjectCompendium.sFS+"LaunchApplications.properties"), "Launch Application Details"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			    	}
			    	return true;
				}
		    }
		}

		return false;    
    }
    
    /**
    * Display a file or URL in the system application on a KDE Linux machine.
    * Used when launching applications under KDE, i.e. kfmclient is handling all files extensions appropriately.
    *
    * @param path, the path to the file or URL path to launch").
    */
    private static boolean launchLinuxCommandKDE(String path) throws IOException, InterruptedException, IllegalThreadStateException {

	   if (path.startsWith("www.")) {  //$NON-NLS-1$
		   path= "http://"+path; //$NON-NLS-1$
	   }
	   Process p = Runtime.getRuntime().exec(new String[] {"/usr/bin/kfmclient", "exec" , path});	    //$NON-NLS-1$ //$NON-NLS-2$
	   int reply = p.waitFor();
	   //reply seems to always be 1? Not sure why, but it launches OK.
	   if (reply != 0 && reply != 1){
		   System.out.println("FAILED to launch "+path); //$NON-NLS-1$
		   return false;
	   }
	   else {
		   return true;
	   }
	}    
}