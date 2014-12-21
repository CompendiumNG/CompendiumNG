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

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.compendium.LanguageProperties;
import com.compendium.ProjectCompendium;
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
	
	private static final Logger log = LoggerFactory.getLogger(ExecuteControl.class);

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
						log.error("Exception: (ExecuteControl.launch) Could not create URI for linked File.", e1); //$NON-NLS-1$
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
		} catch (Exception e) {
			log.error("Exception...", e);
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

		Desktop d = null;
		if (Desktop.isDesktopSupported()) {
			d = Desktop.getDesktop();
			if (path.startsWith("http:")||path.startsWith("https:")) {
				try {
					d.browse(new URI(path));
					return true;
				} catch (URISyntaxException e) {
					log.error("Exception...", e);
					return false;
				}
				
			} else {
				d.open(new File(path));
				return true;
			}
			
			
		} else {
			log.error("unsupported operation - launching file via desktop org.compendiumng.cngx.api");
			return false;
		}
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
}