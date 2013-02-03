/********************************************************************************
 *                                                                              *
 *  (c) Copyright 2012 Michal Stekrt 						*
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

package org.compendiumng.tools;

import java.awt.Desktop;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.Enumeration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.compendium.ProjectCompendium;
import com.compendium.ui.ExecuteControl;

public class Utilities {

	/** logger for ProjectCompendiumFrame.class */
	static final Logger log = LoggerFactory.getLogger(Utilities.class);

	
	/**
	 * @return first hostname of this computer which is not a loopback interface 
	 * @throws UnknownHostException 
	 */
	public static String GetHostname() {
		// source: http://stackoverflow.com/a/10128372/426501
		
		String hostName = null;
		/* FIXME: fix this to return reasonable hostname other wise it returns whatever
		 * inteface it hits first 
		 */

		Enumeration<NetworkInterface> interfaces;
		try {
			interfaces = NetworkInterface.getNetworkInterfaces();
		} catch (SocketException e) {
			log.error("Error while trying to get network interfaces...", e);
			return null;
		}

		while (interfaces.hasMoreElements()) {
			NetworkInterface nic = interfaces.nextElement();
			Enumeration<InetAddress> addresses = nic.getInetAddresses();
			while (hostName == null && addresses.hasMoreElements()) {
				InetAddress address = addresses.nextElement();
				if (!address.isLoopbackAddress()) {
					hostName = address.getHostName();
				}
			}
		}
		return hostName;
	}
	
	public static void OpenURL(String url_id) {
		URI uri = null;
		try {
			uri = new URI(ProjectCompendium.Config.getString(url_id));
		} catch (URISyntaxException me) {
			log.warn("parameter error: {}",url_id,  me);
		}
		
		try {
			Desktop.getDesktop().browse(uri);
		} catch (IOException e1) {
			log.error("Error while opening browser", e1);
		}
	}

}
