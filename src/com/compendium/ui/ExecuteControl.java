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

package com.compendium.ui;

import java.io.*;
import java.awt.*;
import java.util.*;
import javax.swing.*;

import com.compendium.*;

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
     *
     * @param path, the path to the file or URL path to launch").
     */
    public static boolean launch( String path ) {

		try {
		    if (path != null && !path.equals("")) {

				if (ProjectCompendium.isWindows) {
				    return launchWindowsCommand(path);
				}
				else if (ProjectCompendium.isMac) {
			    	return launchMacCommand(path);
				}
				else if (ProjectCompendium.isLinux) {
					if (FormatProperties.useKFMClient) {
						return launchLinuxCommandKDE(path);
					} else {
						return launchLinuxCommand(path);
					}
				}
			}
		}
		catch(IllegalThreadStateException e) {
		    System.out.println("Exception: (ExecuteControl.launch) " + e.getMessage());
		    return false;
		}
		catch (IOException e) {
		    System.out.println("Exception: (ExecuteControl.launch) " + e.getMessage());
		    return false;
		}
		catch (InterruptedException e) {
		    System.out.println("Exception: (ExecuteControl.launch) " + e.getMessage());
		    return false;
		}

		return false;
	}

    /**
     * Display a file or URL in the system application on a Windows Machine.
     *
     * @param path, the path to the file or URL path to launch").
     */
    private static boolean launchWindowsCommand(String path) throws IOException, InterruptedException, IllegalThreadStateException {
		if (ProjectCompendium.platform.indexOf("98") != -1) {
		    Process p = Runtime.getRuntime().exec("start \"" + path + "\"");
		    p.waitFor();
		}
		else {
		    Process p = Runtime.getRuntime().exec("cmd.exe /c start \"Compendium Reference node\" \"" + path + "\"");
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

		Process p = Runtime.getRuntime().exec(new String[] {"open", path});
		if (p.waitFor() != 0) {

		    // DID WE STORE THE INFO FROM A PREVIOUS ATTEMPT?
		    String refString = path.toLowerCase();
		    String key = "";
		    if (refString.startsWith("www."))
				key = "www";
			else if (refString.startsWith("http:"))
				key = "http";
			else if (refString.startsWith("https:"))
				key = "https";
			else if (refString.startsWith("file:"))
				key = "file";		    
			else if ( refString.indexOf("\n") == -1 && refString.indexOf("\r") == -1
			       && refString.length() <= 100	&& refString.indexOf("@") != -1)
				key = "email";
			else {
				int index = refString.lastIndexOf(".");
				if (index != -1) {
				    key = refString.substring(index+1);
				}
			}

		    String application = "";
		    Properties apps = null;
		    if (!key.equals("")) {
				apps = ProjectCompendium.APP.getLaunchApplications();
				String value = apps.getProperty(key);
				if (value != null)
			    	application = value;
		    }

		    if (!application.equals("")) {
				p = Runtime.getRuntime().exec(new String[] {"open", "-a", application, path});
				if (p.waitFor() != 0)
			    	System.out.println("FAILED to launch "+path);
				else
			    	return true;
		    }

		    // We get here when 'open' couldn't open the file, so we
	        // need to ask the user to find the app for us
		    FileDialog d = new FileDialog(ProjectCompendium.APP, "Select an application to open this with...", FileDialog.LOAD);
		    d.setVisible(true);
		    if (d.getFile() != null) {
				// Now we can try to open the file again
				File app = new File(d.getDirectory(), d.getFile());
				System.out.println("app path="+app.getPath());
				p = Runtime.getRuntime().exec(new String[] {"open", "-a", app.getPath(), path});
				if (p.waitFor() != 0)
				    System.out.println("FAILED to launch "+path);
				else {
			    	if (!key.equals("")) {
						apps.put(key, app.getPath());
						apps.store(new FileOutputStream("System"+ProjectCompendium.sFS+"resources"+ProjectCompendium.sFS+"LaunchApplications.properties"), "Launch Application Details");
			    	}
			    	return true;
				}
		    }

		    /*JFileChooser fileDialog = new JFileChooser();
		    fileDialog.setDialogTitle("Select an application to open this with...");
		    int retval = fileDialog.showOpenDialog(ProjectCompendium.APP);
		    if (retval == JFileChooser.APPROVE_OPTION) {
			if ((fileDialog.getSelectedFile()) != null) {
			    String fileName = fileDialog.getSelectedFile().getAbsolutePath();
			    File app = new File(fileName);

			    p = Runtime.getRuntime().exec(new String[] {"open", "-a", app.getPath(), path});
			    if (p.waitFor() != 0)
				System.out.println("FAILED to launch "+path);
			    else {
				if (!key.equals("")) {
				    apps.put(key, app.getPath());
				    apps.store(new FileOutputStream("System"+ProjectCompendium.sFS+"resources"+Projectcompendium.sFS+"LaunchApplications.properties"), "Launch Application Details");
				}

				return true;
			    }
			}
		    }*/
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
		String key = "";
		if (refString.startsWith("www."))
	    	key = "www";
		else if (refString.startsWith("http:"))
	    	key = "http";
		else if (refString.startsWith("https:"))
	    	key = "https";
		else if (refString.startsWith("file:"))
			key = "file";		    		
		else if ( refString.indexOf("\n") == -1 && refString.indexOf("\r") == -1
	   				&& refString.length() <= 100	&& refString.indexOf("@") != -1)
	    	key = "email";
		else {
	    	int index = refString.lastIndexOf(".");
	    	if (index != -1) {
				key = refString.substring(index+1);
			}
	    }

		String application = "";
		Properties apps = null;
		if (!key.equals("")) {
	    	apps = ProjectCompendium.APP.getLaunchApplications();
	    	String value = apps.getProperty(key);
	    	if (value != null)
				application = value;
		}

		if (!application.equals("")) {
	    	Process p = Runtime.getRuntime().exec(new String[] {application, path});
            if (p.waitFor() != 0)
				System.out.println("FAILED to launch "+path);
            else
				return true;
		}

		// We get here when 'open' couldn't open the file, so we
        // need to ask the user to find the app for us
		JFileChooser fileDialog = new JFileChooser();
		fileDialog.setDialogTitle("Select an application to open this with...");
		int retval = fileDialog.showOpenDialog(ProjectCompendium.APP);
		if (retval == JFileChooser.APPROVE_OPTION) {
	    	if ((fileDialog.getSelectedFile()) != null) {
				String fileName = fileDialog.getSelectedFile().getAbsolutePath();
				File app = new File(fileName);

				Process p = Runtime.getRuntime().exec(new String[] {app.getPath(), path});
				if (p.waitFor() != 0)
			    	System.out.println("FAILED to launch "+path);
				else {
			    	if (!key.equals("")) {
						apps.put(key, app.getPath());
						apps.store(new FileOutputStream("System"+ProjectCompendium.sFS+"resources"+ProjectCompendium.sFS+"LaunchApplications.properties"), "Launch Application Details");
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

	   if (path.startsWith("www.")) { 
		   path= "http://"+path;
	   }
	   Process p = Runtime.getRuntime().exec(new String[] {"/usr/bin/kfmclient", "exec" , path});	   
	   int reply = p.waitFor();
	   //reply seems to always be 1? Not sure why, but it launches OK.
	   if (reply != 0 && reply != 1){
		   System.out.println("FAILED to launch "+path);
		   return false;
	   }
	   else {
		   return true;
	   }
	}    
}
