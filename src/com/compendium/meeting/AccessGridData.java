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


package com.compendium.meeting;

import java.util.Properties;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import com.compendium.ProjectCompendium;

/**
 * AccessGridData holds the Triplestore and Arena connection data.
 *
 * @author	Michelle Bachler
 * @version	1.0
 */
public class AccessGridData {

	/** The file holding the data.*/
	public static String			FILE_NAME = "System"+ProjectCompendium.sFS+"resources"+ProjectCompendium.sFS+"AccessGrid.properties";

	/** The Arena server host name.*/
	private String 					sArenaURL = "";

	/** The Arena port data.*/
	//private String 				sArenaPort = "";

	/** The Triplestore URL.*/
	private String					sTriplestoreURL = "";

	/** The Triplestore Port.*/
	//private String				sTriplestorePort = "80";

	/** The  user name for Arena and the triplestore .*/
	private String					sUserName = "";

	/** The  password for Arena and the triplestore.*/
	private String 					sPassword = "";

	/** The local proxy host name.*/
	private String 					sLocalProxyHost = "";

	/** The local proxy port.*/
	private String 					sLocalProxyPort = "";

	/** The file upload/download url for the XML data.*/
	//private String				sFileURL = "";

	/**
	 * Constructor
	 */
	public AccessGridData() {
		loadProperties();
 	}

	/**
	 * Set the Arena url
	 * @param sHost the url to set.
	 */
	public void setArenaURL(String sURL) {
		this.sArenaURL = sURL;
	}

	/**
	 * Return the Arena url.
	 */
	public String getArenaURL() {
		return sArenaURL;
	}

	/**
	 * Set the Arena Port
	 * @param sPort the port to set.
	 */
	//public void setArenaPort(String sPort) {
	//	this.sArenaPort = sPort;
	//}

	/**
	 * Return the Arena Port.
	 */
	//public String getArenaPort() {
	//	return sArenaPort;
	//}

	/**
	 * Set the Triplestore URL
	 * @param sURL the URL to set.
	 */
	public void setTriplestoreURL(String sURL) {
		this.sTriplestoreURL = sURL;
	}

	/**
	 * Return the Triplestore URL.
	 */
	public String getTriplestoreURL() {
		return sTriplestoreURL;
	}

	/**
	 * Set the Triplestore Port
	 * @param sPort the port to set.
	 */
	//public void setTriplestorePort(String sPort) {
	//	this.sTriplestorePort = sPort;
	//}

	/**
	 * Return the Triplestore Port.
	 */
	//public String getTriplestorePort() {
	//	return sTriplestorePort;
	//}

	/**
	 * Set the password
	 * @param sPassword the password to set.
	 */
	public void setPassword(String sPassword) {
		this.sPassword = sPassword;
	}

	/**
	 * Return the Triplestore password.
	 */
	public String getPassword() {
		return sPassword;
	}

	/**
	 * Set the username
	 * @param sName the name to set.
	 */
	public void setUserName(String sName) {
		this.sUserName = sName;
	}

	/**
	 * Return the username.
	 */
	public String getUserName() {
		return sUserName;
	}

	/**
	 * Set the File upload/download URL
	 * @param sURL the URL to set.
	 */
	//public void setFileURL(String sURL) {
	//	this.sFileURL = sURL;
	//}

	/**
	 * Return the file upload/download URL.
	 */
	//public String getFileURL() {
	//	return sFileURL;
	//}

	/**
	 * Set the local proxy host name.
	 * @param sHost the host name to set.
	 */
	public void setLocalProxyHostName(String sHost) {
		this.sLocalProxyHost = sHost;
	}

	/**
	 * Return the local proxy host.
	 */
	public String getLocalProxyHostName() {
		return sLocalProxyHost;
	}

	/**
	 * Set the local proxy Port
	 * @param sPort the port to set.
	 */
	public void setLocalProxyPort(String sPort) {
		this.sLocalProxyPort = sPort;
	}

	/**
	 * Return the LocalProxy Port.
	 */
	public String getLocalProxyPort() {
		return sLocalProxyPort;
	}

	/**
	 * Return if this machine is using a local proxy.
	 * Checks if the data has been entered?
	 * @return true if it has else false.
	 */
	public boolean hasLocalProxy() {
		if (!sLocalProxyHost.equals("")) {
			return true;
		}
		return false;
	}

	/**
	 * Return if all the data required to upload/download the XML xip file has been entered?
	 * @return true if it has else false.
	 */
	public boolean canDoXML() {
		if (canAccessArena() && !sPassword.equals("") && !sUserName.equals("")) {
			return true;
		}
		return false;
	}

	/**
	 * Return if all the data required to access the Arena has been entered?
	 * @return true if it has else false.
	 */
	public boolean canAccessArena() {
		if (!sArenaURL.equals("")) {
			return true;
		}
		return false;
	}

	/**
	 * Return if all the data required to access the triplstore has been entered?
	 * @return true if it has else false.
	 */
	public boolean canAccessTriplestore() {
		if (!sTriplestoreURL.equals("")	&& !sPassword.equals("") && !sUserName.equals("")) {
			return true;
		}
		return false;
	}

	/**
	 * Load the previously stored Access Grid connection data.
	 * @param sFile the property file to load.
	 */
	public void loadProperties() {

		File optionsFile = new File(FILE_NAME);
		Properties connectionProperties = new Properties();

		if (optionsFile.exists()) {
			try {
				connectionProperties.load(new FileInputStream(FILE_NAME));

				String value = connectionProperties.getProperty("arenaurl");
				if (value != null) {
					sArenaURL = value;
				}

				//value = connectionProperties.getProperty("arenaport");
				//if (value != null) {
				//	sArenaPort = value;
				//}

				value = connectionProperties.getProperty("triplestoreurl");
				if (value != null) {
					sTriplestoreURL = value;
				}

				//value = connectionProperties.getProperty("triplestoreport");
				//if (value != null) {
				//	sTriplestorePort = value;
				//}

				value = connectionProperties.getProperty("username");
				if (value != null) {
					sUserName = value;
				}

				value = connectionProperties.getProperty("password");
				if (value != null) {
					sPassword = value;
				}

				value = connectionProperties.getProperty("localproxyhost");
				if (value != null) {
					sLocalProxyHost = value;
				}

				value = connectionProperties.getProperty("localproxyport");
				if (value != null) {
					sLocalProxyPort = value;
				}

				//value = connectionProperties.getProperty("fileurl");
				//if (value != null) {
				//	sFileURL = value;
				//}

			} catch (IOException e) {
				System.out.println("Could not load one or more Access Grid connection properties due to: "+e.getMessage());
			}
		}
	}
	
	/**
	 * Save the Access Grid data to a property file.
	 */
	public void saveProperties() throws IOException {

		Properties oConnectionProperties = new Properties();
			
		oConnectionProperties.put("arenaurl", sArenaURL);
		//oConnectionProperties.put("arenaport", sArenaPort);
		oConnectionProperties.put("triplestoreurl", sTriplestoreURL);
		//oConnectionProperties.put("triplestoreport", sTriplestorePort);
		oConnectionProperties.put("username", sUserName);
		oConnectionProperties.put("password", sPassword);
		oConnectionProperties.put("localproxyhost", sLocalProxyHost);
		oConnectionProperties.put("localproxyport", sLocalProxyPort);
		//oConnectionProperties.put("fileurl;", sFileURL);

		oConnectionProperties.store(new FileOutputStream(FILE_NAME), "Access Grid Details");
	}	
}
