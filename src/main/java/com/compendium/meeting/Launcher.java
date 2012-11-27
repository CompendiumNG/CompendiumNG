/*
 * @(#)Launcher.java
 * Created: 24-Nov-2005
 * Version: 1-0
 * Copyright (c) 2005-2006, University of Manchester All rights reserved. 
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer. Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials 
 * provided with the distribution. Neither the name of the University of 
 * Manchester nor the names of its contributors may be used to endorse or 
 * promote products derived from this software without specific prior written
 * permission. 
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE 
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE 
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR 
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS 
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN 
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package com.compendium.meeting;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.compendium.LanguageProperties;

/**
 *
 * 
 * @author Andrew G D Rowley
 * @version 1-0
 */
public class Launcher extends WindowAdapter implements ActionListener {
    
    // The name of the file that stores the compendium root once found
    private static final String COMPENDIUM_ROOT_FILE = ".compendium_root"; //$NON-NLS-1$
    
    // The name of the compendium launch file
    private static final String COMPENDIUM_LAUNCH = "(C|c)ompendium.(bat|sh)"; //$NON-NLS-1$
    
    // The default location for windows
    private static final String WINDOWS_DEFAULT = 
        "Program Files\\Compendium"; //$NON-NLS-1$
    
    // The default location for linux
    private static final String LINUX_DEFAULT = 
        System.getProperty("user.home"); //$NON-NLS-1$
    
    // True if file searching has been cancelled
    private boolean stopped = false;
    
    // The label to fill in during search
    private JLabel details = new JLabel();
    
    // The search dialog box
    private JDialog dialog = 
        new JDialog((Frame) null, LanguageProperties.getString(LanguageProperties.MEETING_BUNDLE, "Launcher.title"), false); //$NON-NLS-1$

    /**
     * Finds a file in a given directory subtree
     * @param dir The root directory to search
     * @param name The pattern of the name of the file to find
     * @throws IOException
     */
    private File findFile(File dir, String name) throws IOException {
            File[] files = dir.listFiles();
            if (files == null) {
                throw new IOException(dir.getAbsolutePath() + LanguageProperties.getString(LanguageProperties.MEETING_BUNDLE, "Launcher.notValidDir")); //$NON-NLS-1$
            }
            details.setText(LanguageProperties.getString(LanguageProperties.MEETING_BUNDLE, "Launcher.searching") + dir.getAbsolutePath()); //$NON-NLS-1$
            for (int i = 0; (i < files.length) && !stopped ; ++i) {
                if (files[i].getName().matches(name)) {
                    return files[i];
                }
                if (files[i].isDirectory() && !stopped) {
                    File file = findFile(files[i], name);
                    if (file != null) {
                        return file;
                    }
                }
            }
            return null;
        }
    
    /**
     * Creates a new Launcher
     * @param args The arguments of the launcher
     */
    public Launcher(String args[]) {
        File compendiumRoot = null;
        
        // Check to see if the compendium launch location has been stored
        String slash = System.getProperty("file.separator"); //$NON-NLS-1$
        File startFile = new File(System.getProperty("user.home") + slash + //$NON-NLS-1$
                COMPENDIUM_ROOT_FILE);
        if (startFile.exists()) {
            try {
                BufferedReader reader = new BufferedReader(new FileReader(startFile));
                compendiumRoot = new File(reader.readLine());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        
        // Search for the compendium.bat file in the default location first
        if ((compendiumRoot == null) || !compendiumRoot.exists()) {
            String os = System.getProperty("os.name").toLowerCase(); //$NON-NLS-1$
            if (os.indexOf("windows") != -1) { //$NON-NLS-1$
                File[] roots = File.listRoots();
                for (int i = 0; i < roots.length; i++) {
                    File dir = new File(roots[i], WINDOWS_DEFAULT);
                    if (dir.exists()) {
                        while ((dir.getParentFile() != null) && (compendiumRoot == null)) {
                            try {
                                compendiumRoot = findFile(dir, COMPENDIUM_LAUNCH);
                                dir = dir.getParentFile();
                            } catch (IOException e) {
                                // Do Nothing
                            }
                        }
                    }
                }
            }
            else if (os.indexOf("mac") != -1) { //$NON-NLS-1$
                // Do Nothing
            }
            else if (os.indexOf("linux") != -1) { //$NON-NLS-1$
                try {
                    compendiumRoot = findFile(new File(LINUX_DEFAULT), COMPENDIUM_LAUNCH);
                } catch (IOException e) {
                    // Do Nothing
                }
            }
        }
        
        // If the launch file does not exist, or refers to a location that doesn't exist,
        // search for the compendium.bat file
        if ((compendiumRoot == null) || !compendiumRoot.exists()) {
            
            // Show a dialog indicating the search progress
            JPanel content = new JPanel();
            content.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
            JButton cancel = new JButton(LanguageProperties.getString(LanguageProperties.MEETING_BUNDLE, "Launcher.cancelButton")); //$NON-NLS-1$
            cancel.setMnemonic(LanguageProperties.getString(LanguageProperties.MEETING_BUNDLE, "Launcher.cancelButtonMnemonic").charAt(0)); //$NON-NLS-1$
            cancel.addActionListener(this);
            
            JPanel buttonPanel = new JPanel();
            buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
            buttonPanel.add(Box.createHorizontalGlue());
            buttonPanel.add(cancel);
            buttonPanel.add(Box.createHorizontalGlue());
            content.add(details);
            content.add(Box.createVerticalGlue());
            content.add(buttonPanel);
            details.setMinimumSize(new Dimension(400, 0));
            dialog.addWindowListener(this);
            dialog.setSize(400, 100);
            dialog.setLocationRelativeTo(null);
            dialog.getContentPane().add(content);
            dialog.setVisible(true);
            
            // Search all directories
            File[] roots = File.listRoots();
            boolean found = false;
            for (int i = 0; (i < roots.length) && !found && !stopped; i++) {
                if (!roots[i].getAbsolutePath().equals("A:\\")) { //$NON-NLS-1$
                    try {
                        compendiumRoot = findFile(roots[i], COMPENDIUM_LAUNCH);
                        if (compendiumRoot != null) {
                            found = true;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        
        // If the Compendium root has not been found, ask the user to select one
        if ((compendiumRoot == null) || !compendiumRoot.exists()) {
            JFileChooser chooser = new JFileChooser(); 
            chooser.setCurrentDirectory(new java.io.File(".")); //$NON-NLS-1$
            chooser.setDialogTitle(LanguageProperties.getString(LanguageProperties.MEETING_BUNDLE, "Launcher.selectInstallDir")); //$NON-NLS-1$
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) { 
                compendiumRoot = new File(chooser.getSelectedFile(), "Compendium.bat"); //$NON-NLS-1$
                if (!compendiumRoot.exists()) {
                    compendiumRoot = 
                        new File(chooser.getCurrentDirectory(), "compendium.sh"); //$NON-NLS-1$
                }
            }
        }

        
        // If the Compendium root has been found, start compendium
        Process process = null;
        if ((compendiumRoot != null) && compendiumRoot.exists()) {
            
            // Store the root file for later use
            try {
                PrintWriter writer = new PrintWriter(new FileWriter(startFile));
                writer.println(compendiumRoot.getAbsolutePath());
                writer.close();
            } catch (IOException e) {
                // Do Nothing
            }
            
            // Launch compendium
            String[] cmdarray = new String[args.length + 1];
            cmdarray[0] = compendiumRoot.getAbsolutePath();
            System.arraycopy(args, 0, cmdarray, 1, args.length);
            try {
                process = Runtime.getRuntime().
                    exec(cmdarray, null, compendiumRoot.getParentFile().getAbsoluteFile());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } 
        
        // If compendium is not running, issue an error message
        if (process == null) {
            JOptionPane.showMessageDialog(null, 
                    LanguageProperties.getString(LanguageProperties.MEETING_BUNDLE, "Launcher.couldNotLaunch"),  //$NON-NLS-1$
                    "Error", JOptionPane.ERROR_MESSAGE); //$NON-NLS-1$
            startFile.delete();
        } 
        System.exit(0);
    }

    
    /**
     * @param args
     */
    public static void main(String[] args) {
        new Launcher(args);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("Cancel")) { //$NON-NLS-1$
            stopped = true;
            dialog.setVisible(false);
        }
        
    }

    public void windowClosing(WindowEvent e) {
        stopped = true;
    }
}
