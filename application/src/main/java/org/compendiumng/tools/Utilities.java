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

import com.compendium.ProjectCompendium;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.*;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.Hashtable;

public class Utilities {

	private final static Hashtable<String, ImageIcon> ImageIconCache = new Hashtable<String, ImageIcon>(200); 
	
	
	/** logger for ProjectCompendiumFrame.class */
	private static final Logger log = LoggerFactory.getLogger(Utilities.class);
	
	private final static ImageIcon MISSING_ICON = GetImageIcon("broken-image.png", ProjectCompendium.DIR_IMAGES); 

	
	/**
	 * @return first hostname of this computer which is not a loopback interface 
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
	
	public static boolean CopyFile(String file_src, String file_dest) {
		try {
			FileInputStream fis = new FileInputStream(file_src);
			FileOutputStream fos = new FileOutputStream(file_dest);
			FileChannel fc_src = fis.getChannel();
			FileChannel fc_dest = fos.getChannel();

			fc_src.transferTo(0, fc_src.size(), fc_dest);

			fis.close();
			fos.close();
			fc_dest.close();
			fc_src.close();
			return true;
		} catch (IOException e) {
			log.error("Failed to copy file {} -> {}", file_src, file_dest, e);
			return false;
		}
	} 
	
	
	/**
	 * Take the given file name and create an ImageIcon file from it.
	 * @param image_name the name of image
	 * @return the Image icon, or null, if not successfully loaded.
	 */
	public static ImageIcon GetImageIcon(String image_name, String base_path) {
		
		if (image_name.startsWith("System//resources//Images")) {
			log.warn("requested image with legacy \"System//resources//Images\" location. Relocating to {} ", ProjectCompendium.DIR_IMAGES);
			image_name.replace("System//resources//Images", ProjectCompendium.DIR_IMAGES);
			log.info("new path: {}", image_name );
		}
		
		if (image_name.startsWith("System")) {
			log.warn("resource: {} is still using legacy location !");
		}
		
		ImageIcon oIcon = null;
		
		if (base_path == null) {
			base_path = "";
		}
		
		Path path_to_file = Paths.get(base_path + image_name);
		String keytofile = path_to_file.toString();

		if (ImageIconCache.containsKey(keytofile)) {
			log.debug("ImageIconCache hit on: {}", keytofile);
			oIcon = ImageIconCache.get(keytofile);
		} else {
			log.debug("ImageIconCache miss on: {}", path_to_file);
			if (Files.exists(path_to_file, LinkOption.NOFOLLOW_LINKS)) {
				try {
					oIcon = new ImageIcon(keytofile);
					
					if (oIcon.getImageLoadStatus() == MediaTracker.ERRORED) {
						oIcon = MISSING_ICON;
					} 
					ImageIconCache.put(keytofile, oIcon);
				} catch (Exception ex) {
					log.error("Exception...", ex);
					oIcon = MISSING_ICON;
				}
			} else {
				log.error("image file doesn't exist {}",keytofile);
				oIcon =  MISSING_ICON;
			}

		}
		return oIcon;
	}
}
