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

package com.compendium.core;

import java.awt.Component;
import java.lang.String;
import java.text.*;
import java.util.*;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;

import com.compendium.*;
import com.compendium.core.datamodel.IModel;
import com.compendium.core.datamodel.PCObject;
import com.compendium.core.datamodel.NodeSummary;
import com.compendium.core.datamodel.Code;
import com.compendium.core.datamodel.ExternalConnection;
import com.compendium.core.datamodel.PCSession;

/**
 * The CoreUtilities class holds some usefule utility methods.
 *
 * @author	Michelle Bachler
 */
//NOTE: NEED TO REWITE TEXT REPLACMENT CODE WITH NEW JAVA 1.4 REGEX STUFF
public class CoreUtilities {

	private static String sFS	= System.getProperty("file.separator");

	/**
	 * Check if the code with the given name exists, and if node create.
	 *
	 * @param sName, the code text to check.
	 * @param oModel, the current model.
	 * @param oSession, the current session.
	 * @param sAuthor, the current author.
	 * @return Code, the code found/created.
	 */
	public static Code checkCreateCode(String sName, IModel oModel, PCSession oSession, String sAuthor) throws Exception {

		Code oCode = (oModel.getCodeService()).getCodeForName(oSession, sName);
		if (oCode == null) {
			Date creationDate = new Date();
			Date modificationDate = creationDate;
			String sCodeID = oModel.getUniqueID();
			oCode = oModel.getCodeService().createCode(oSession, sCodeID, sAuthor, creationDate, modificationDate, sName, "", "");
			oModel.addCode(oCode);
		}
		return oCode;
	}	
	
	/**
	 * Return true if the given version is newer than this application expects.
	 * Checks against sAPPVERSION_CHECKER in ICoreConstants.
	 * It expects the version number passed and the sAPPVERSION_CHECKER 
	 * not be greater that a five bit number e.g. 2, 2.0, 2.0.1, 2.1.1.1, or 2.0.0.0.7
	 * Anything after the 9th character will be ignored.
	 * Fifth Level is for Alphas, e.g. 2.0.0.0.7 = 2.0 Alpha 7
	 * Fourth Level is for Betas, e.g. 2.0.0.2 = 2.0 Beta 2
	 * First and Second and Third levels are for main releases, e.g. 2.0, 2.1.1.
	 * 
	 *
	 * @param String version the version of a software to check.
	 * @return true if the given version reference is newer than this application expects.
	 */
	public static boolean isNewerVersion(String version) throws Exception{
		
		// Shorter numbers are newer like 2.0 (main stable release) is newer than 2.0.0.0.7 (Alpha 7)
		// when comparing alpha and betas to main releases.
		// so initialise last two bits to -1 for later checking
		int length = version.length();
		int fifthBit = -1;
		if (length > 8) {
			fifthBit = new Integer(version.substring(8,9)).intValue();
		}
		
		int fourthBit = -1;
		if (length > 6) {
			fourthBit = new Integer(version.substring(6,7)).intValue();
		}

		int thirdBit = 0;
		if (length > 4) {
			try {
				thirdBit = new Integer(version.substring(4,5)).intValue();
			} catch(NumberFormatException e) {} //i.e. In case version entered wrong e.g. 2.0 Alpha 7 etc treat as 0
		}

		int secondBit = 0;
		if (length > 2) {
			secondBit = new Integer(version.substring(2,3)).intValue();
		}

		int firstBit = 0;
		if (length > 0) {
			firstBit = new Integer(version.substring(0,1)).intValue();
		}

		int length2 = ICoreConstants.sAPPVERSION_CHECKER.length();
		
		int fifthBit2 = -1;
		if (length > 8) {
			fifthBit2 = new Integer(ICoreConstants.sAPPVERSION_CHECKER.substring(8,9)).intValue();
		}

		int fourthBit2 = -1;
		if (length2 > 6) {
			fourthBit2 = new Integer(ICoreConstants.sAPPVERSION_CHECKER.substring(6,7)).intValue();
		}
		
		int thirdBit2 = 0;
		if (length2 > 4) {
			thirdBit2 = new Integer(ICoreConstants.sAPPVERSION_CHECKER.substring(4,5)).intValue();
		}

		int secondBit2 = 0;
		if (length2 > 2) {
			secondBit2 = new Integer(ICoreConstants.sAPPVERSION_CHECKER.substring(2,3)).intValue();
		}		

		int firstBit2 = 0;
		if (length2 > 0) {
			firstBit2 = new Integer(ICoreConstants.sAPPVERSION_CHECKER.substring(0,1)).intValue();
		}

		if (firstBit > firstBit2) {
			return true;
		} else if (firstBit == firstBit2) {
			if (secondBit > secondBit2) {			
				return true;
			} else if ((secondBit == secondBit2)) {
				if (thirdBit > thirdBit2) {
					return true;
				} else if (thirdBit == thirdBit2) {
					if ((fourthBit == -1 && fourthBit2 > -1) || fourthBit > fourthBit2) {
						return true;
					} else if (fourthBit == fourthBit2 
							&& ((fifthBit == -1 && fifthBit2 > -1) || fifthBit > fifthBit2) ) {
						return true;
					}
				}
			} 
		} 

		return false;
	}
	
	/**
	 * Return true if the given schema is newer than this application expects.
	 * Checks against ICoreConstants.sDATABASEVERSION.
	 * It expects the version number passed and the sDATABASEVERSION 
	 * not be greater that a three bit number e.g. 2, 2.0, 2.0.1 but never 2.1.1.1
	 * Anything after the 5th character will be ignored by this method's testing.
	 * 
	 *
	 * @param String version, the version of a database schema to check.
	 * @return true if the given schema reference is newer than this application expects.
	 */
	public static boolean isNewerSchema(String version) throws Exception{

		int length = version.length();

		int lastBit = 0;
		if (length > 4) {
			lastBit = new Integer(version.substring(4,5)).intValue();
		}

		int middleBit = 0;
		if (length > 2) {
			middleBit = new Integer(version.substring(2,3)).intValue();
		}

		int firstBit = 0;
		if (length > 0) {
			firstBit = new Integer(version.substring(0,1)).intValue();
		}

		int length2 = ICoreConstants.sDATABASEVERSION.length();
		
		int lastBit2 = 0;
		if (length2 > 4) {
			lastBit2 = new Integer(ICoreConstants.sDATABASEVERSION.substring(4,5)).intValue();
		}
		
		int middleBit2 = 0;
		if (length2 > 2) {
			middleBit2 = new Integer(ICoreConstants.sDATABASEVERSION.substring(2,3)).intValue();
		}		
		
		int firstBit2 = 0;
		if (length2 > 0) {
			firstBit2 = new Integer(ICoreConstants.sDATABASEVERSION.substring(0,1)).intValue();
		}
		
		if (firstBit > firstBit2) {
			return true;
		} else if (firstBit == firstBit2) {
			if (middleBit > middleBit2) {			
				return true;
			} else if ((middleBit == middleBit2) && lastBit > lastBit2) {
				return true;
			}
		} 

		return false;
	}

	/**
	 * Return true if the given schema reference is older than this application expects.
	 *
	 * @param String version, the version of a database schema to check.
	 * @return true if the given schema reference is older than this application expects.
	 */
	public static boolean isOlderSchema(String version) {

		int length = version.length();

		int lastBit = 0;
		if (length > 4) {
			lastBit = new Integer(version.substring(4,5)).intValue();
		}

		int middleBit = 0;
		if (length > 2) {
			middleBit = new Integer(version.substring(2,3)).intValue();
		}

		int firstBit = 0;
		if (length > 0) {
			firstBit = new Integer(version.substring(0,1)).intValue();
		}

		int length2 = ICoreConstants.sDATABASEVERSION.length();
		
		int lastBit2 = 0;
		if (length2 > 4) {
			lastBit2 = new Integer(ICoreConstants.sDATABASEVERSION.substring(4,5)).intValue();
		}
		
		int middleBit2 = 0;
		if (length2 > 2) {
			middleBit2 = new Integer(ICoreConstants.sDATABASEVERSION.substring(2,3)).intValue();
		}		
		
		int firstBit2 = 0;
		if (length2 > 0) {
			firstBit2 = new Integer(ICoreConstants.sDATABASEVERSION.substring(0,1)).intValue();
		}
		
		if (firstBit < firstBit2) {
			return true;
		} else if (firstBit == firstBit2) {
			if (middleBit < middleBit2) {			
				return true;
			} else if ((middleBit == middleBit2) && lastBit < lastBit2) {
				return true;
			}
		} 
		
		/*if (lastBit < lastBit2) {
			if (middleBit <= middleBit2) {
				if (firstBit <= firstBit2) {
					return true;
				}
			}
		}*/

		return false;
	}

	/**
	 * Try and copy the source file to the destination file.
	 * @param oSourceFile the file to copy.
	 * @param oDestinationFile the file to copy to.
	 */
	public static void copyFile(File oSourceFile, File oDestinationFile) throws IOException {

        InputStream in = new FileInputStream(oSourceFile);
        OutputStream out = new FileOutputStream(oDestinationFile);

        // Transfer bytes from in to out
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        in.close();
        out.close();
    }


	/**
	 * Delete the given file.
	 * If cannot delete now, set ot delete on Exit,
	 * and record the file path in a specific system file, to be deleted on starting Compendium.
	 *
	 * @param File oDirectory, the directory to delete.
	 * @return true if directory exists, and delete done or set to do on exit, else false.
	 */
	public static boolean deleteFile(File oFile) throws SecurityException {

		if (oFile.exists()) {
			if (oFile.isDirectory()) {
				return deleteDirectory(oFile);
			}
			else if (oFile.isFile()) {
				if (!oFile.delete()) {
					oFile.deleteOnExit();
					writeToDeleted(oFile.getAbsolutePath());
				}
			}
			else if (oFile.isHidden()) {
				if (!oFile.delete()) {
					oFile.deleteOnExit();
					writeToDeleted(oFile.getAbsolutePath());
				}
			}
			return true;
		}
		else {
			return false;
		}
	}

	/**
	 * Delete the given directory and all its contents.
	 * If cannot delete now, set ot delete on Exit,
	 * and record the file path in a specific system file, to be deleted on starting Compendium.
	 *
	 * @param File oDirectory, the directory to delete.
	 * @return true if directory exists, and delete done or set to do on exit, else false.
	 */
	public static boolean deleteDirectory(File oDirectory) throws SecurityException {
		if (oDirectory.exists()) {
			File[] files = oDirectory.listFiles();
			for (int i=0; i<files.length; i++) {
				File nextFile = files[i];
				if (nextFile.isDirectory()) {
					boolean deleted = deleteDirectory(nextFile);
					if (!deleted)
						return deleted;
				}
				else if (nextFile.isFile()) {
					if (!nextFile.delete()) {
						nextFile.deleteOnExit();
						writeToDeleted(nextFile.getAbsolutePath());
					}
				}
				else if (nextFile.isHidden()) {
					if (!nextFile.delete()) {
						nextFile.deleteOnExit();
						writeToDeleted(nextFile.getAbsolutePath());
					}
				}
			}
			if (!oDirectory.delete()) {
				oDirectory.deleteOnExit();
				writeToDeleted(oDirectory.getAbsolutePath());
			}

			return true;
		}
		return false;
	}

	/**
	 * Write the given file path out to the file holding a list of files / directories to be deleted
	 *
	 * @param String path, the string to write to the file.
	 */
	public static void writeToDeleted(String path) {

		File file = new File("System"+sFS+"resources"+sFS+"filesToDelete.dat");

		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(file, true));
			writer.write(path);
			writer.newLine();
		}
		catch (FileNotFoundException ex) {
			ex.printStackTrace();
		}
		catch (IOException ex) {
			ex.printStackTrace();
		}
	    finally {
	    	try {
        		if (writer!= null) {
          			writer.close();
        		}
      		}
      		catch (IOException ex) {
        		ex.printStackTrace();
      		}
    	}
	}

	/**
	 * Check for files / directories to be deleted. If they have not been, try again.
	 */
	public static void checkFilesToDeleted() throws SecurityException {

		File file = new File("System"+sFS+"resources"+sFS+"filesToDelete.dat");
		if (!file.exists())
			return;

		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(file));
			Vector contents = new Vector(10);
      		String line = null;
      		while (( line = reader.readLine()) != null) {
        		contents.addElement(line);
      		}
      		reader.close();

			// EMPTY THE FILE
			BufferedWriter writer = null;
			try {
				writer = new BufferedWriter(new FileWriter(file, false));
				writer.write("");
			}
			catch (FileNotFoundException ex) {
				ex.printStackTrace();
			}
			catch (IOException ex) {
				ex.printStackTrace();
			}
		    finally {
		    	try {
	        		if (writer!= null) {
	          			writer.close();
	        		}
	      		}
	      		catch (IOException ex) {
	        		ex.printStackTrace();
	      		}
	    	}

			File deletedFile = null;
			int count = contents.size();
			for (int i=0; i<count; i++) {
				deletedFile = new File((String)contents.elementAt(i));
				deleteFile(deletedFile);
			}
		}
		catch (FileNotFoundException ex) {
			ex.printStackTrace();
		}
		catch (IOException ex) {
			ex.printStackTrace();
		}
		finally {
	    	try {
	       		if (reader != null) {
	       			reader.close();
	       		}
	   		}
	   		catch (IOException ex) {
	      		ex.printStackTrace();
	   		}
		}
    }

	/**
	 * Split the given string on the given spliter, and return a Vector of the resultant String items
	 *
	 * @param String sData, the string to split.
	 * @param String splitter, the string to split at.
	 * @return Vector, contains the elements produced after splitting the string.
	 */
	public static Vector splitString(String sData, String sSplitter) {

		Vector vtData = new Vector(10);
		if (sData.length() == 0)
			return vtData;

		String data = sData;
		String item = "";
		int len = sSplitter.length();

		while(data.length() > 0) {

			int next = data.indexOf(sSplitter);
			if (next != -1) {
				item = data.substring(0, next);
				if (next < data.length());
					data = data.substring(next+len);
				vtData.addElement(item);
			}
			else {
				if (data.length() > 0)
					vtData.addElement(data);
				break;
			}
		}

		return vtData;
	}

	/**
	 * Is the passed String a path to a file that exists?
	 * This method also checks if the file exists and returns false if the file does not.
	 *
	 * @param String refString, the reference path to analyse.
	 * @return boolean, true if the String is a file path, else false.
	 */
	public static boolean isFile(String refString) {

		if (refString.equals("")) {
			return false;
		}
		
	    if (refString != null) {
			String ref = refString.toLowerCase();
			if ( ref.startsWith("www.") ||
					ref.startsWith("http:") ||
					ref.startsWith("https:") ||
					ref.startsWith("feed:") ||					
					ref.startsWith(ICoreConstants.sINTERNAL_REFERENCE) ||
					(ref.indexOf("\n") == -1 &&
		    		  ref.indexOf("\r") == -1 &&
		    		  ref.length() <= 100 &&
		    		  ref.indexOf("@") != -1)) {

			    return false;
			} else {
				File file = new File(refString);
				if (file.isDirectory()) {
					return false;
				}
			}
	    } 
	    return true;
	}

	/** 
	 * Check whether the given file is a potential Compendium export archive.
	 * 
	 * @author Sebastian Ehrich
	 * @param file
	 * @return true if the given file is a potential Compendium export archive.
	 */
	public static boolean isPotentialExportFile(File file) {
		String fileName = file.getPath().toLowerCase();
		return ((fileName.endsWith(".xml") || isZipFile(file)));
	}

	/**
	 * Check whether the given file is a ZIP-file.
	 * @author Sebastian Ehrich
	 * @param file the file to check.
	 * @return <code>True</code> if the file is supposed to be
	 * a ZIP-file, <code>false</code> otherwise.
	 */
	public static boolean isZipFile(File file) {
		String fileName = file.getPath().toLowerCase();
		return (fileName.endsWith(".zip"));
	}
	
	/**
	 * Clean path if not correct for the platform
	 *
	 * @param sText String, the text to clean
	 * @param is this the Windows platform
	 * @return String, the clean text
	 */
	public static String cleanPath( String sText, boolean isWindows ) {

		if (sText == null || sText.equals(""))
			return "";

		if (sText.startsWith("http://") || sText.startsWith("https://"))
			return sText;

		String sFS	= System.getProperty("file.separator");

		if (isWindows) {
			sText = replace(sText, '/', sFS);
		}
		else {
			sText = replace(sText, '\\', sFS);
		}

		return sText;
	}

	/**
	 * Convert path to unix style path
	 *
	 * @param sText String, the text to convert
	 * @return String, the converted text
	 */
	public static String unixPath( String sText ) {

		if (sText == null || sText.equals(""))
			return "";

		sText = replace(sText, '\\', "/");

		return sText;
	}

	/**
	 * Clean illegal file name characters from the given text
	 * N.B. you must pass in the file name only, without the path.
	 *
	 * @param sText String, the text to clean
	 * @return String, the clean text
	 */
	public static String cleanFileName( String sText ) {

		if (sText == null || sText.equals(""))
			return "";

		sText = replace(sText, ' ', "_");
		sText = replace(sText, '"', "");
		sText = replace(sText, '\'', "");
		sText = replace(sText, '\\', "");
		sText = replace(sText, '/', "");
		sText = replace(sText, '<', "");
		sText = replace(sText, '>', "");
		sText = replace(sText, '|', "");
		sText = replace(sText, '?', "");
		sText = replace(sText, '*', "");
		sText = replace(sText, ':', "");

		// Linux did not like ... in a file name
		sText = replace(sText, "....", "");		
		sText = replace(sText, "...", "");
		sText = replace(sText, "..", "");
		
		return sText;
	}

	/**
	 * Clean illegal MySQL database name characters from the given text
	 *
	 * @param sText String, the text to clean
	 * @return String, the clean text
	 */
	public static String cleanDatabaseName( String sText ) {

		if (sText == null || sText.equals(""))
			return "";

		try {
			Long test = new Long(sText);
			sText = "Compendium"+sText;
		}
		catch(NumberFormatException io) {}

		sText = replace(sText, ' ', "_");
		sText = replace(sText, '"', "");
		sText = replace(sText, '-', "_");
		sText = replace(sText, '\'', "");
		sText = replace(sText, '\\', "");
		sText = replace(sText, '/', "");
		sText = replace(sText, '<', "");
		sText = replace(sText, '>', "");
		sText = replace(sText, '|', "");
		sText = replace(sText, '?', "");
		sText = replace(sText, '*', "");
		sText = replace(sText, ':', "");
		sText = replace(sText, ';', "");
		sText = replace(sText, ',', "");
		sText = replace(sText, '.', "");
		sText = replace(sText, '&', "");
		sText = replace(sText, '#', "");
		sText = replace(sText, '%', "");


		if (sText.equals("")) {
			sText = new String("Compendium"+(new Date()).getTime());
		}

		// WANT THIS NAME TO BE UNIQUE, SO USE CURRENT TIME
		sText = sText +"_"+ (new Date()).getTime();

		return sText;
	}

	/**
	 * Clean illegal sql characters from the given text - for SQL export/backup
	 *
	 * @param sText String, the text to clean
	 * @return String, the clean text
	 */
	public static String cleanSQLText( String sText, int nDatabaseType ) {

		if (sText == null || sText.equals(""))
			return "";
		
		sText = replace(sText, '\n', "\\n");
		sText = replace(sText, '\r', "\\r");		

		if (nDatabaseType == ICoreConstants.DERBY_DATABASE) {
			sText = replace(sText, '\'', "\'\'");			
		}
		else {
			sText = replace(sText, '\\', "\\\\");			
			sText = replace(sText, '"', "\\\"");
			sText = replace(sText, '\'', "\\'");
		}
		
		return sText;
	}

	/**
	 * Clean illegal characters from the given text - for including in a URL string
	 *
	 * @param sText String, the text to clean
	 * @return String the clean text
	 */
	public static String cleanURLText( String sText ) throws UnsupportedEncodingException {		
		return URLEncoder.encode(sText, "UTF-8");
	}

	public static String cleanHTMLText(String sText) {
		
		StringBuffer sb = new StringBuffer(sText.length());
	    
	    int len = sText.length();
	    char c;

	    for (int i = 0; i < len; i++) {
	        c = sText.charAt(i);
            // HTML Special Chars
            if (c == '"')
                sb.append("&quot;");
            else if (c == '&')
                sb.append("&amp;");
            else if (c == '<')
                sb.append("&lt;");
            else if (c == '>')
                sb.append("&gt;");
            else if (c == '\'')
            	sb.append("&apos;");
            else {
                int ci = 0xffff & c;
                if (ci < 160 )
                    // nothing special only 7 Bit
                    sb.append(c);
                else {
                    // Not 7 Bit use the unicode system
                    sb.append("&#");
                    sb.append(new Integer(ci).toString());
                    sb.append(';');
                }
            }
	    }
	    
	    return sb.toString();
	}
	
	/**
	 * Clean illegal xml characters from the given text
	 *
	 * @param sText String, the text to clean
	 * @return String, the clean text
	 */
	public static String cleanXMLText( String sText ) {

		if (sText == null || sText.equals(""))
			return "";

		sText = cleanHTMLText(sText);

		// of the values below 0x20 only the following are allowed in XML
		// 0x9 = tab
		// 0xA = newline
		// 0xD = cr
		sText = replace(sText, '\u0000', "");
		sText = replace(sText, '\u0001', "");
		sText = replace(sText, '\u0002', "");
		sText = replace(sText, '\u0003', "");
		sText = replace(sText, '\u0004', "");
		sText = replace(sText, '\u0005', "");
		sText = replace(sText, '\u0006', "");
		sText = replace(sText, '\u0007', "");
		sText = replace(sText, '\u0008', "");
		sText = replace(sText, '\u000B', "");
		sText = replace(sText, '\u000C', "");
		sText = replace(sText, '\u000E', "");
		sText = replace(sText, '\u000F', "");
		sText = replace(sText, '\u0010', "");
		sText = replace(sText, '\u0011', "");
		sText = replace(sText, '\u0012', "");
		sText = replace(sText, '\u0013', "");
		sText = replace(sText, '\u0014', "");
		sText = replace(sText, '\u0015', "");
		sText = replace(sText, '\u0016', "");
		sText = replace(sText, '\u0017', "");
		sText = replace(sText, '\u0018', "");
		sText = replace(sText, '\u0019', "");
		sText = replace(sText, '\u001A', "");
		sText = replace(sText, '\u001B', "");
		sText = replace(sText, '\u001C', "");
		sText = replace(sText, '\u001D', "");
		sText = replace(sText, '\u001E', "");
		sText = replace(sText, '\u001F', "");

		return sText;
	}

	/**
	 * Replace String a, in the given text with String b
	 *
	 * @param text the text to replace character in.
	 * @param a the string to replace
	 * @param b the string to replace the character with.
	 *
	 * @return String, the replaced text.
	 */
	public static String replace( String text, String a, String b ) {

		int length = text.length();
		int lenA = a.length();
		int lenB = b.length();
      	int gofrom=0;
		boolean goon = true;

		while(goon) {
			int next = text.indexOf(a, gofrom);
			if (next != -1) {

				if (next+lenA > length) {
					text = text.substring(0, next) + b;
				} else {
					text = text.substring(0, next) + b + text.substring(next+lenA);
				}
				gofrom = next+lenB;
			}
			else {
				goon = false;
			}

			length = text.length();
			if (gofrom > length)
				goon = false;
		}
		return text;		
	}	
	
	/**
	 * Replace char a, in the given text with String b
	 *
	 * @param text the text to replace character in.
	 * @param a the character to replace
	 * @param b the string to replace the character with.
	 *
	 * @return String, the replaced text.
	 */
	public static String replace( String text, char a, String b ) {

		int length = text.length();
		int len = b.length();
      	int gofrom=0;
		boolean goon = true;

		while(goon) {
			int next = text.indexOf(a, gofrom);
			if (next != -1) {

				if (next+1 > length)
					text = text.substring(0, next) + b;
				else
					text = text.substring(0, next) + b + text.substring(next+1);

				gofrom = next+len;
			}
			else
				goon = false;

			length = text.length();
			if (gofrom > length)
				goon = false;
		}
		return text;
	}	

	/**
	 * String sort: Sort the given Vector of objects, depending on the object type.
	 * Types accepted are: String, JLabel, NodeSummary, Code, Vector (elementAt 1 = String),
	 * DefaultMutableTreeNode (where the user object is a Code, or a Vector with elementAt 1 = String).
	 *
	 * @param Vector unsortedVector, the vector of Objects to sort.
	 * @return Vector, or sorted objects.
	 */
	public static Vector sortList(Vector unsortedVector) {

		Vector sortedVector = new Vector();
		Vector unsortedVector2 = new Vector();

		if(unsortedVector.size() > 0) {

			if(unsortedVector.elementAt(0) instanceof NodeSummary) {
				for(Enumeration e = unsortedVector.elements();e.hasMoreElements();) {
					PCObject obj = (PCObject)e.nextElement();
					if (obj != null) {
						String text = ((NodeSummary)obj).getLabel();
						unsortedVector2.addElement(text);
					}
				}
			}
			else if(unsortedVector.elementAt(0) instanceof Code) {
				for(Enumeration e = unsortedVector.elements();e.hasMoreElements();) {
					PCObject obj = (PCObject)e.nextElement();
					if (obj != null) {
						String text = ((Code)obj).getName();
						unsortedVector2.addElement(text);
					}
				}
			}
			else if(unsortedVector.elementAt(0) instanceof ExternalConnection) {
				for(Enumeration e = unsortedVector.elements();e.hasMoreElements();) {
					PCObject obj = (PCObject)e.nextElement();
					if (obj != null) {
						String text = ((ExternalConnection)obj).getProfile();
						unsortedVector2.addElement(text);
					}
				}
			}
			else if(unsortedVector.elementAt(0) instanceof String) {
				for(Enumeration e = unsortedVector.elements();e.hasMoreElements();) {
					String text = (String)e.nextElement();
					unsortedVector2.addElement(text);
				}
			}
			else if(unsortedVector.elementAt(0) instanceof File) {
				for(Enumeration e = unsortedVector.elements();e.hasMoreElements();) {
					File file = (File)e.nextElement();
					unsortedVector2.addElement(file.getName());
				}
			}
			else if(unsortedVector.elementAt(0) instanceof JLabel) {
				for(Enumeration e = unsortedVector.elements();e.hasMoreElements();) {
					JLabel obj = (JLabel)e.nextElement();
					if (obj != null) {
						String text = obj.getText();

						// FOR STENCIL ITEMS
						if (text == null)
							text = obj.getName();
						unsortedVector2.addElement(text);
					}
				}
			}
			else if(unsortedVector.elementAt(0) instanceof Vector) {	// FOR CODE GROUPS
				for(Enumeration e = unsortedVector.elements();e.hasMoreElements();) {
					Vector data = (Vector)e.nextElement();
					unsortedVector2.addElement((String)data.elementAt(1));
				}
			}
			else if(unsortedVector.elementAt(0) instanceof DefaultMutableTreeNode) {	// FOR CODE GROUP TREE
				for(Enumeration e = unsortedVector.elements();e.hasMoreElements();) {
					DefaultMutableTreeNode node = (DefaultMutableTreeNode)e.nextElement();
					Object obj = node.getUserObject();
					if (obj instanceof Code)
						unsortedVector2.addElement( ((Code)node.getUserObject()).getName() );
					else if (obj instanceof Vector) {
						Vector data = (Vector)node.getUserObject();
						unsortedVector2.addElement((String)data.elementAt(1));
					} 
				}
			}
			else if (unsortedVector.elementAt(0) instanceof Component) {

				for(Enumeration e = unsortedVector.elements();e.hasMoreElements();) {
					Component data = (Component)e.nextElement();
					String sName = data.getName();
					unsortedVector2.addElement(sName);
				}
			}
			else {
				return unsortedVector;
			}
		}
		else {
			return unsortedVector;
		}

		Object[] sa = new Object[unsortedVector2.size()];
		unsortedVector2.copyInto(sa);
		List l = Arrays.asList(sa);

		Collections.sort(l, new Comparator() {
			public int compare(Object o1, Object o2) {

				String s1 = (String)o1;
				if (s1 == null)
					s1 = "";

				String s2 = (String)o2;
				if (s2 == null)
					s2 = "";

				String s1L = s1.toLowerCase();
		        String s2L = s2.toLowerCase();
				return  (s1L.compareTo(s2L));
			}
		});

	 	// add sorted elements from list to vector
	 	for (Iterator it = l.iterator(); it.hasNext(); ) {
			String sortedElement = (String) it.next();

			// add it to vector rearranged with the objects
			for(Enumeration e = unsortedVector.elements();e.hasMoreElements();) {

				Object pcobject = (Object)e.nextElement();

				String text = "";
				if (pcobject instanceof NodeSummary) {
					text = ((NodeSummary)pcobject).getLabel();
				}
				else if (pcobject instanceof Code) {
					text = ((Code)pcobject).getName();
				}
				else if (pcobject instanceof ExternalConnection) {
					text = ((ExternalConnection)pcobject).getProfile();
				}
				else if (pcobject instanceof String) {
					text = (String)pcobject;
				}
				else if (pcobject instanceof File) {
					text = ((File)pcobject).getName();
				}
				else if(pcobject instanceof JLabel) {
					JLabel obj = (JLabel)pcobject;
					text = obj.getText();

					// FOR STENCIL ITEMS
					if (text == null)
						text = obj.getName();
				}
				else if (pcobject instanceof Vector) { // FOR CODE GROUPS
					Vector data = (Vector)pcobject;
					text = (String)data.elementAt(1);
				}
				else if (pcobject instanceof DefaultMutableTreeNode) { // FOR CODE GROUP TREE
					DefaultMutableTreeNode node = (DefaultMutableTreeNode)pcobject;
					if (node.getUserObject() instanceof Code)
						text = (String)(((Code)node.getUserObject()).getName());
					else {
						Vector data = (Vector)node.getUserObject();
						text = (String)data.elementAt(1);
					}
				}
				else if (pcobject instanceof Component) {
					Component data = (Component)pcobject;
					text = (String)data.getName();
					if (text == null)
						text = "";
				}

				if (text.equals(sortedElement)) {
					sortedVector.addElement(pcobject);

					//remove this element so it can't be found again in case there
					//is more than one object with the same text.
					unsortedVector.removeElement(pcobject);
					break;
				}
			}
		 }

		return sortedVector;
	}

	/** Used in the sortList method to indicate sort should be performed on the Node label */
	public static final int	LABEL					= 0;

	/** Used in the sortList method to indicate sort should be performed on the Node createion date */
	public static final int	CREATION_DATE			= 1;

	/** Used in the sortList method to indicate sort should be performed on the Node modification date */
	public static final int	MODIFICATION_DATE		= 2;

	/**
	 * Sort the given Vector of Objects, depending on the Object type.
	 * Types accepted are: String, NodeSummary, Code.
	 * If the Vector contains NodeSummary objects, then sort by the passed criteria.
	 * The criteria can be: CoreUtilities.LABEL, CoreUtilities.CREATION_DATE, CoreUtilities.MODIFICATION_DATE.
	 *
	 * @param Vector unsortedVector, the vector of Objects to sort.
	 * @param int criteria, one of: CoreUtilities.LABEL, CoreUtilities.CREATION_DATE, CoreUtilities.MODIFICATION_DATE.
	 * @return Vector, or sorted objects.
	 */
	public static Vector sortList(Vector unsortedVector, int criteria) {

		Vector sortedVector = new Vector();
		Vector unsortedVector2 = new Vector();
		Hashtable htUnsorted = new Hashtable();

		SimpleDateFormat formatter = new SimpleDateFormat("dd-MMMM-yyyy_HH-mm-ss");

		if(unsortedVector.size() > 0) {
			if(unsortedVector.elementAt(0) instanceof NodeSummary) {

				//add the elements to the hashtable
				for(Enumeration e = unsortedVector.elements();e.hasMoreElements();) {

					PCObject pcobject = (PCObject)e.nextElement();
					String text = "";
					switch(criteria) {

						case(LABEL):
							text = ((NodeSummary)pcobject).getLabel();
							break;
						case(CREATION_DATE):
							//text = new Long( (((NodeSummary)pcobject).getCreationDate()).getTime() ).toString();
							text = formatter.format( ((NodeSummary)pcobject).getCreationDate() ).toString();
							break;
						case(MODIFICATION_DATE):
							//text = new Long( (((NodeSummary)pcobject).getModificationDate()).getTime() ).toString();
							text = formatter.format( ((NodeSummary)pcobject).getModificationDate() ).toString();
							break;
					}
					htUnsorted.put(text, pcobject);
				}

				for(Enumeration e = unsortedVector.elements();e.hasMoreElements();) {
					String text = "";
					PCObject pcobject = (PCObject)e.nextElement();

					switch(criteria) {
						case(LABEL):
							text = ((NodeSummary)pcobject).getLabel();
							break;
						case(CREATION_DATE):
							//text = new Long( (((NodeSummary)pcobject).getCreationDate()).getTime() ).toString();
							text = formatter.format( ((NodeSummary)pcobject).getCreationDate() ).toString();
							break;
						case(MODIFICATION_DATE):
							//text = new Long( (((NodeSummary)pcobject).getModificationDate()).getTime() ).toString();
							text = formatter.format( ((NodeSummary)pcobject).getModificationDate() ).toString();
							break;
					}
					unsortedVector2.addElement(text);
				}
			}
			else if(unsortedVector.elementAt(0) instanceof Code) {

				//add the elements to the hashtable
				for(Enumeration e = unsortedVector.elements();e.hasMoreElements();) {
					PCObject pcobject = (PCObject)e.nextElement();
					String text = ((Code)pcobject).getName();
					htUnsorted.put(text, pcobject);
				}

				for(Enumeration e = unsortedVector.elements();e.hasMoreElements();) {
					String text = ((Code)((PCObject)e.nextElement())).getName();
					unsortedVector2.addElement(text);
				}
			}
			else if(unsortedVector.elementAt(0) instanceof String) {
				//add the elements to the hashtable
				for(Enumeration e = unsortedVector.elements();e.hasMoreElements();) {
					htUnsorted.put(e.nextElement(), e.nextElement());
				}

				for(Enumeration e = unsortedVector.elements();e.hasMoreElements();) {
					unsortedVector2.addElement(e.nextElement());
				}

			}
		}
		else {
			return null;
		}

		Object[] sa = new Object[unsortedVector2.size()];
		unsortedVector2.copyInto(sa);
		List l = Arrays.asList(sa);

		Collections.sort(l, new Comparator() {
			public int compare(Object o1, Object o2) {
				String s1 = (String) o1;
		        String s2 = (String) o2;
				return  (s1.compareTo(s2));
			}
		});


		// add sorted elements from list to vector
	 	for (Iterator it = l.iterator(); it.hasNext(); ) {
			String sortedElement = (String) it.next();

			// add it to vector rearranged with the objects
			if (htUnsorted.get(sortedElement) instanceof String)
				sortedVector.addElement(htUnsorted.get(sortedElement));
			else
				sortedVector.addElement((PCObject)htUnsorted.get(sortedElement));
		}
		return sortedVector;
	}


	private static double minDoubleValue = 100.0 * Float.MIN_VALUE;
	private static double epsilon = 10.0 * 1.192092896e-07f;

  	private static int compare(double r1, double r2) {
    	double ar1 = r1;
    	double ar2 = r2;
    	if (ar1 < 0.)
			ar1 = -ar1;
    	if (ar2 < 0.)
			ar2 = -ar2;
    	if (ar1 < minDoubleValue && ar2 < minDoubleValue)
			return 0;

	    double er1 = epsilon * ar1;
   	 	double er2 = epsilon * ar2;
    	if (r1 - er1 > r2 + er2 )
      		return 1;
    	else if (r1 + er1 < r2 - er2 )
      		return -1;
    	else
      		return 0;
  	}

	/**
	 * Divide the first parameter by the second parameter
	 *
	 * @param double first, the number to divide into.
	 * @param double second, the number to divide by
	 * @return double, the result from dividing the first number by the second number.
	 */
  	public static double divide (double first, double second) {

		int limit = 1;
    	double sign = 1.0;
    	if ((first > 0.0 && second < 0.0) || (first < 0.0 && second > 0.0)) {
			sign = -1.0;
		}

    	first = Math.abs(first);
    	second = Math.abs (second);

	    if (second >= 1.0 || first < second * 0.1*Float.MAX_VALUE) {
      		return sign * first / second;
    	}
		else if ( compare(first, second) == 0 ) {
	    	if (first < 0.)
				first = -first;
    		if (first < minDoubleValue) {
	       		return limit;
    	  	}
			else {
       		 	return sign;
      		}
    	}
		else {
      		return sign * 0.1 * 0.1 * Float.MAX_VALUE;
    	}
  	}
}
