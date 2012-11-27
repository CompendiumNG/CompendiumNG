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

package com.compendium;

import java.io.*;
import javax.swing.JOptionPane;

import com.compendium.core.*;

/**
 * This class redirects output and error messages to a log file for debugging purposes
 *
 * @author ? / Michelle Bachler
 */
public class SaveOutput extends PrintStream {

	/** The OutputStream to the log file */
	private static OutputStream logfile;

	/** The original PrintStream for the system.out messages*/
	private static PrintStream oldStdout;

	/** The original PrintStream for the system.err messages */
	private static PrintStream oldStderr;

	/** The name of the current log file.*/

	private static String sFileName = "";

	/**
	 * Constructor which takes an OutputStream instance.
	 *
	 * @param OutputStream ps, The outputStream ot use for System.out and System.err messages.
	 */
	SaveOutput(OutputStream ps) {
		 super(ps);
	}

	/**
	 * Starts copying System.out and System.err to the file with the path sFile.
	 *
	 * @param String sFile, the file path of the log file to write to.
	 */
	public static void start(String sFile) throws IOException {

		sFileName = sFile;

		// Save old settings.
		oldStdout = System.out;
		oldStderr = System.err;

		// Create/Open logfile.
		logfile = new BufferedOutputStream(new FileOutputStream(sFile));

		// Start redirecting the output.
		System.setOut(new SaveOutput(logfile));
		System.setErr(new SaveOutput(logfile));
	}

	/**
	 * Restores the original output setting and closes the current log file OutputStream.
	 */
	public static void stop() {

		System.setOut(oldStdout);
		System.setErr(oldStderr);
		try {
			logfile.close();
			File file = new File(sFileName);
			if (file.length() == 0) {
				CoreUtilities.deleteFile(file);
			}
		} catch (Exception e) {
		    System.out.println("Exception: (SaveOutput.stop) " + e.getMessage());
		    //JOptionPane.showMessageDialog(null, "Exception: (SaveOutput.stop) " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * Override superclass to add date/time stamp to message.
	 */
	public void println(String x) {
		super.println(CoreCalendar.getCurrentDateStringFull() + ": " +x);
	}
}
