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

package com.compendium.meeting.io;

import java.util.Enumeration;
import java.util.Vector;
import java.beans.PropertyVetoException;
import java.io.*;
import java.net.*;

import javax.swing.JInternalFrame;

import com.compendium.ProjectCompendium;
import com.compendium.meeting.AccessGridData;
import com.compendium.io.http.*;
import com.compendium.io.xml.XMLExport;
import com.compendium.core.CoreUtilities;
import com.compendium.ui.UIUtilities;
import com.compendium.ui.UIViewFrame;


/**
 * This class handles the connections to Arena to upload/download xml data for the meeting and getting clock offset.
 *
 * @author	Michelle Bachler
 * @version	1.0
 */
public class ArenaConnection {

	/** Counter of how many attempts have been made to get the clock time from Arena.*/
	private int				nAttempts			= 0;


	/**
	 * Constructor. Create a new instance of XMLDataHandler.
	 */
	public ArenaConnection() {}

	/**
	 * Query Arena for the Arena clock time and store in triplestore.
	 * Record current time for use in calculating Compendium event offsets
	 */
	public long getArenaOffset(AccessGridData oConnectionData) {

		long nOffsetTime = -1;

		if (!oConnectionData.canAccessArena()) {
			ProjectCompendium.APP.displayError("The required connection data for Arena has not been entered.\n\nPlease use the 'Access Grid Meeting Setup' on the Tools/memetic menu to enter this data.\n\n");
			return nOffsetTime;
		}

		String sHostName = oConnectionData.getArenaURL();
		//String sPort = oConnectionData.getArenaPort(); // Now appended to url before sent

		String sProxySet = System.getProperty("proxySet");
		if (oConnectionData.hasLocalProxy() && sProxySet.equals("false")) {
			System.setProperty("proxySet", "true");
			System.setProperty("http.proxyHost", oConnectionData.getLocalProxyHostName());
			System.setProperty("http.proxyPort", oConnectionData.getLocalProxyPort());
		}

		String sServerURL = sHostName+"/time.jsp";
		if (!sServerURL.startsWith("http://")) {
			sServerURL = "http://"+sServerURL;
		}

		HttpURLConnection timeConnection = null;

		try {
			long sendTime = System.currentTimeMillis();

			URL url = new URL(sServerURL);
			timeConnection = (HttpURLConnection)url.openConnection();
			timeConnection.connect();

			long recvTime = System.currentTimeMillis();

			BufferedReader reader = new BufferedReader(new InputStreamReader(timeConnection.getInputStream()));
			String line = reader.readLine();

			//System.out.println("Arena time = "+line);

			long lArenaTime = -1;
			if (line != null && !line.equals("")) {
				lArenaTime = Long.valueOf(line).longValue();
			}

			if (lArenaTime > -1) {
				long lCompendiumTime = (recvTime + sendTime) / 2;
				nOffsetTime = lArenaTime - lCompendiumTime;
			} else {
				if (nAttempts < 10) {
					nAttempts++;
					nOffsetTime = getArenaOffset(oConnectionData);
				} else {
					ProjectCompendium.APP.displayError("Unable to retrieve required clock data from Arena.\n");
				}
			}
		} catch(SocketTimeoutException ste) {
			ste.printStackTrace();
			if (nAttempts < 10) {
				nAttempts++;
				nOffsetTime = getArenaOffset(oConnectionData);
			} else {
				ProjectCompendium.APP.displayError("Unable to establish connection to Arena to download clock data due to:\n\n"+ste.getMessage());
			}
		} catch(UnknownServiceException use) {
			use.printStackTrace();
			ProjectCompendium.APP.displayError("UnknownServiceException: Unable to establish connection to Arena to download clock data due to:\n\n"+use.getMessage());
		} catch(IOException ex) {
			ex.printStackTrace();
			ProjectCompendium.APP.displayError("IOException: Unable to establish connection to Arena to download clock data due to:\n\n"+ex.getMessage());
		}

		timeConnection.disconnect();

		return nOffsetTime;
	}

	/**
	 * Upload file of XML export.zip with the given name to the memetic server.
	 * If upload appears successful, tag the file as uploaded by renameing file with '.uploaded' at the end.
	 *
	 * @param oConnectionData the object holding the connection data.
	 * @param sSessionID the session id required to make the connection.
	 * @param sFilePath the file of data to upload.
	 * @param isCorrectName indicates if the filename passed is the correct filename or if it needs altering from .n3 to zip.
	 */
	public void uploadXMLFile(AccessGridData oConnectionData, String sSessionID, String sFilePath, boolean isCorrectName) {

		if (!sFilePath.equals("")) {
			if (!isCorrectName) {
				int ind = sFilePath.lastIndexOf(".");
				String sFileStub = sFilePath.substring(0, ind);
				sFilePath = sFileStub+".zip";
			}
			File file = new File(sFilePath);
			String sFileName = file.getName();
			//System.out.println("zip file name = "+sFileName);
			try {
				String sURL = oConnectionData.getArenaURL();
				if (!sURL.startsWith("http://")) {
					sURL = "http://"+sURL;
				}
				sURL += "/memetic/compendiumupload.jsp?session="+sSessionID+"&noheaders=1";
				sFileName = sFileName.toLowerCase();

				HttpFileUploadOutputStream upload = new HttpFileUploadOutputStream(new URL(sURL), sFileName, oConnectionData.getUserName(), oConnectionData.getPassword());
	            upload.uploadFromFile(sFilePath);
	            upload.close();

				File oSourceFile = new File(sFilePath);
				File oDestinationFile = new File(sFilePath+".uploaded");
				boolean bSuccessful = oSourceFile.renameTo(oDestinationFile);

				// IF YOU CAN'T RENAME IT TRY AND COPY IT.
				// EITHER WAY DELETE ORIGINAL AS DON'T WON'T IT LISTING STILL FOR UPLOAD.
				if (!bSuccessful) {
					try {
						CoreUtilities.copyFile(oSourceFile, oDestinationFile);
					} catch (IOException io) { io.printStackTrace();}
					CoreUtilities.deleteFile(oSourceFile);
				}
			} catch (Exception e) {
				ProjectCompendium.APP.displayError("There was the following problem\ntrying to upload meeting zip file:\n\n" + e.getMessage());
			}
		}
	}

	/**
	 * Check if there is an XML zip file waiting to be download.
	 * If yes, download and unzipp and import the XML
	 * @param oConnectionData the object holding the connection data.
	 * @param sSessionID the session id required to make the connection.
	 * @param sFileName the filename of the file to download.
	 * @param sDirectory the path to the folder to store the downloaded file to.
	 */
	 public boolean downloadXMLFile(AccessGridData oConnectionData, String sSessionID, String sFileName, String sDirectory) {

		if (!sFileName.equals("")) {

			// CHECK IF THIS FILE WAS CREATED ON THIS MACHINE OR HAS BEEN EXTRACTED BEFORE?
			// ONLY DOWNLOAD/IMPORT IF NOT
			String sDatabaseName = ProjectCompendium.APP.getModel().getModelName();
			String sFilePath = sDirectory+ProjectCompendium.sFS+sDatabaseName+ProjectCompendium.sFS+sFileName+".uploaded";

			File oXMLFile = new File(sFilePath);
			if (!oXMLFile.exists()) {

				File directory = new File(sDirectory+ProjectCompendium.sFS+sDatabaseName);
				if (!directory.isDirectory()) {
					directory.mkdirs();
				}

				try {
					String sURL = oConnectionData.getArenaURL();
					if (!sURL.startsWith("http://")) {
						sURL = "http://"+sURL;
					}
					sURL += "/memetic/compendiumdownload.jsp?session="+sSessionID+"&file="+sFileName;
					sURL = sURL.toLowerCase();

					//System.out.println("sURL = "+sURL);

					// DOWNLOAD AND UNPACK THE ZIP
					HttpFileDownloadInputStream stream = new HttpFileDownloadInputStream(new URL(sURL), oConnectionData.getUserName(), oConnectionData.getPassword());
					stream.downloadToFile(sFilePath);
					stream.close();

				} catch (IOException ioe) {
					ioe.printStackTrace();
					System.out.println("Error with ZIP file due to:"+ioe.getMessage());
				}
			}
            
            try {
                JInternalFrame frame = ProjectCompendium.APP.getViewFrame(ProjectCompendium.APP.getHomeView(), "");
                try {
                    ProjectCompendium.APP.getCurrentFrame().setSelected(false);
                    ProjectCompendium.APP.getDesktop().setSelectedFrame(frame);
                    frame.setSelected(true);
                } catch (PropertyVetoException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                return UIUtilities.unzipXMLZipFile(sFilePath, false);
            } catch (IOException ioe) {
                ioe.printStackTrace();
                System.out.println("Error with ZIP file due to:"+ioe.getMessage());
            }
		}
		return false;
	}
}
