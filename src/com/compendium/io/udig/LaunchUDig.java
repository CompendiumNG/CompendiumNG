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

package com.compendium.io.udig;

import java.io.*;
import javax.swing.*;

/**
 * This calls is responsible for locating the UDig application and Launching it
 *
 * @author Andrew G D Rowley & Michelle Bachler
 */
public class LaunchUDig {

    // The name of the file that stores the udig root once found
    private static final String UDIG_ROOT_FILE = ".udig_launch";

    // The default location for windows
    private static final String WINDOWS_DEFAULT = "Program Files\\Ecosensus\\uDig\\eclipse";

    // The default location for windows
    private static final String MAC_DEFAULT = "/Applications/udig/eclipse";

	// The default location for linux
	private static final String LINUX_DEFAULT = System.getProperty("user.home")+"/udig/eclipse";

	// True if file searching has been cancelled
	private boolean stopped = false;

	/** The file containing the last known location of the Compendium statup file.*/
	File startFile = null;

    /**
	 * Constructor.
	 */
    public LaunchUDig() {}

    /**
     * Find the Compendium statup file.
     * @return a string representing the path to the Compendium startup file.
     */
    private String locate() {

    	//SEARCH FOR THE FILE HOLDING THE
        File udigRoot = null;

        String slash = System.getProperty("file.separator");
        startFile = new File(System.getProperty("user.home") + slash + UDIG_ROOT_FILE);
        if (startFile.exists()) {
            try {
                BufferedReader reader = new BufferedReader(new FileReader(startFile));
                udigRoot = new File(reader.readLine());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    	// If we have not previously stored the location
    	// or the location is no longer valid
    	// Start the search in the expected default installation location.
        boolean isWindows = false;
        boolean isMac = false;
        boolean isLinux = false;
    	if ((udigRoot == null) || !udigRoot.exists()) {
            String os = System.getProperty("os.name").toLowerCase();

            if (os.indexOf("windows") != -1) {
            	isWindows = true;
                File[] roots = File.listRoots();
                for (int i = 0; i < roots.length; i++) {
                    if (!roots[i].getAbsolutePath().equals("A:\\")) {
	                    File dir = new File(roots[i], WINDOWS_DEFAULT);
	                    if (dir.exists()) {
	                        while ((dir.getParentFile() != null) && (udigRoot == null)) {
	                            try {
	                                udigRoot = findFile(dir, "udig.exe");
	                                dir = dir.getParentFile();
	                            } catch (IOException e) {}
	                        }
	                    }
                    }
                }
            }
            else if (os.indexOf("mac") != -1) {
            	isMac = true;
                try {
                    udigRoot = findFile(new File(MAC_DEFAULT), "udig.app");
                } catch (IOException e) {
                	e.printStackTrace();
                }
            }
            else if (os.indexOf("linux") != -1) {
            	isLinux = true;
                try {
                    udigRoot = findFile(new File(LINUX_DEFAULT), "udig.sh");
                } catch (IOException e) {
                	e.printStackTrace();
                }
            }
        }

    	// IF NOT FOUND SEARCH
       	if ((udigRoot == null) || !udigRoot.exists()) {

            // Search all directories
            File[] roots = File.listRoots();
            boolean found = false;
            for (int i = 0; (i < roots.length) && !found && !stopped; i++) {
                if (!roots[i].getAbsolutePath().equals("A:\\")) {
                    try {
                    	String sName = "udig.exe";
                    	if (isMac) {
                    		sName = "udig.app";
                    	}
                    	else if (isLinux) {
                    		sName = "udig.sh";
                    	}
                        udigRoot = findFile(roots[i], sName);
                        if (udigRoot != null) {
                            found = true;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        // If the Compendium root has not been found, ask the user to select one
        if ((udigRoot == null) || !udigRoot.exists()) {

            JFileChooser chooser = new JFileChooser();
            chooser.setCurrentDirectory(new java.io.File("."));
            chooser.setDialogTitle("Select UDig Installation Directory");
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                udigRoot = new File(chooser.getSelectedFile(), "udig.exe");
                if (!udigRoot.exists()) {
                    udigRoot = new File(chooser.getCurrentDirectory(), "udig.sh");
                }
                if (!udigRoot.exists()) {
                    udigRoot = new File(chooser.getCurrentDirectory(), "udig.app");
                }
            }
        }

        // If the Compendium root has been found, start compendium
        if ((udigRoot != null) && udigRoot.exists()) {

           // Store the root file for later use
            try {
                PrintWriter writer = new PrintWriter(new FileWriter(startFile));
                writer.println(udigRoot.getAbsolutePath());
                writer.close();
            } catch (IOException e) {
            	e.printStackTrace();
            }

        	return udigRoot.getAbsolutePath();
         }
         return "";
    }

    /**
     * Launch uDig
     * @param args
     * @return if successfully launched.
     */
    public boolean launch(String args[]) {
    	String path = locate();

        Process process = null;
        File launchFile = new File(path);
        if (launchFile.exists()) {
        	String[] cmdarray = null;
        	if (args != null) {
        		cmdarray = new String[args.length + 1];
        	}
        	else {
           		cmdarray = new String[1];
        	}
            cmdarray[0] = launchFile.getAbsolutePath();

            if (args != null) {
            	System.arraycopy(args, 0, cmdarray, 1, args.length);
            }
            try {
                process = Runtime.getRuntime().exec(cmdarray, null, launchFile.getParentFile().getAbsoluteFile());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // If udig is not running, issue an error message
        if (process == null) {
            startFile.delete();
            return false;
        }
        return true;
    }

    /**
	 * Finds a file in a given directory subtree
	 *
	 * @param dir The root directory to search
	 * @param sName The pattern of the name of the file to find
	 * @throws IOException
	 */
    private File findFile(File dir, String sName) throws IOException {
        File[] files = dir.listFiles();
        if (files == null) {
            throw new IOException(dir.getAbsolutePath() + "is not a valid directory");
        }

        for (int i = 0; (i < files.length) && !stopped ; ++i) {
            if (files[i].getName().equals(sName)) {
                return files[i];
            }
            if (files[i].isDirectory() && !stopped) {
                File file = findFile(files[i], sName);
                if (file != null) {
                    return file;
                }
            }
        }
        return null;
    }
}
