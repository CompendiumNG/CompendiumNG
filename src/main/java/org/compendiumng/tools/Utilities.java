package org.compendiumng.tools;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beust.jcommander.Parameter;
import com.compendium.ui.ProjectCompendiumFrame;

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
}
