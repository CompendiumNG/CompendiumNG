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

import java.net.*;
import java.io.*;
import java.util.Properties;
import java.util.Stack;

import com.compendium.*;



/**
 * This class handles the communications back and forth with uDIG.
 * It creates and manages the two Sockets, the one to send message on and the one to recieve them on.
 *
 * @author Michelle Bachler
 */
public class UDigCommunicationManager extends Thread {

	public static final String FILENAME = System.getProperty("user.home")+ProjectCompendium.sFS+".ecosensus_ports.properties";

	UDigServerSocket oServerSocket = null;
	UDigClientSocket oClientSocket = null;

	Stack oOpenMapCommandStack = new Stack();
	Stack oAddPropertyCommandStack = new Stack();
	Stack oEditLabelCommandStack = new Stack();

	private int nServerPort = UDigServerSocket.PORT;
	private int nClientPort = UDigClientSocket.PORT;

	/**
	 * Start up two sockets, one to listen one and to send on.
	 */
	public UDigCommunicationManager() throws UnknownHostException, IOException, SecurityException {

		loadPorts();

		final UDigCommunicationManager me = this;
		Thread thread = new Thread() {
			public void run() {
				oServerSocket = new UDigServerSocket(me, nServerPort);
				oServerSocket.start();
			}
		};
		thread.start();

		createSendSocket();
	}

	public void destroyServerSocket() {

		if (oServerSocket != null) {
			oServerSocket.close();
			oServerSocket.clearSpace();
			oServerSocket = null;
		}
	}

	public void destroyClientSocket() {

		if (oClientSocket != null) {
			oClientSocket.close();
			oClientSocket.clearSpace();
			oClientSocket = null;
		}
	}

	public void createSendSocket() {

		if (oClientSocket != null) {
			return;
		}

		try {
			// IF YOU CAN ESTABLISH A CONNECTION TO THE SERVER END, COMPENDIUM MUST BE RUNNING
			oClientSocket = new UDigClientSocket(this, nClientPort);
		} catch (UnknownHostException e) {
			oClientSocket = null;
			//System.out.println("Exception: " + e.getMessage());
		} catch (java.net.BindException e) {
			oClientSocket = null;
			// IF IT CAN'T BIND TO PORT
			// CHECK FILE to see if port changed and try again.
			int nOldPort = nClientPort;
			loadPorts();
			if (nClientPort != nOldPort) {
				createSendSocket();
			}
		} catch (ConnectException e) {
			oClientSocket = null;
			// IF IT CAN'T FIND THE SERVER END OF THE SOCKET, COMPENDIUM CAN'T BE RUNNUING
		} catch (IOException e) {
			oClientSocket = null;
			//e.printStackTrace();
			//System.out.println("Exception: " + e.getMessage());
		} catch (SecurityException e) {
			oClientSocket = null;
			//e.printStackTrace();
			//System.out.println("Exception: " + e.getMessage());
		}
	}

	private boolean connectToUDig() {

		if (oClientSocket == null) {
			//if (isUDigRunning()) {
				createSendSocket();
			//}
			if (oClientSocket == null) {
				LaunchUDig locate = new LaunchUDig();
				if (!locate.launch(null)) {
					return false;
				}
				else {
					return true;
				}
			}
		}
		return true;
	}

	public void runCommands() {
		while (!oOpenMapCommandStack.empty()) {
			openMap((String)oOpenMapCommandStack.pop());
		}

		while (!oAddPropertyCommandStack.empty()) {
			addProperty((String)oAddPropertyCommandStack.pop());
		}
		while (!oEditLabelCommandStack.empty()) {
			editLabel((String)oEditLabelCommandStack.pop());
		}
	}

	/**
	 * Clear any commands waiting in the Command stacks
	 */
	public void clearCommands() {
		oOpenMapCommandStack.clear();
		oAddPropertyCommandStack.clear();
		oEditLabelCommandStack.clear();
	}

	public void launchUDig() {
		if (oClientSocket == null) {
			if (!connectToUDig()) {
				ProjectCompendium.APP.displayError("Unable to launch uDig.\nPlease launch manually.", "uDig");
			}
			else {
				ProjectCompendium.APP.displayMessage("uDig is being launched. Please wait...", "uDig");
			}
		} else {
			ProjectCompendium.APP.displayMessage("Compendium believes uDig is already running", "uDig");
		}
	}

	public String openMap(String sMapID) {
		String reply = "";
		if (oClientSocket == null) {
			oOpenMapCommandStack.push(sMapID);
			if (!connectToUDig()) {
				ProjectCompendium.APP.displayError("Unable to launch uDig.\nPlease launch manually.", "uDig");
			}
			else {
				ProjectCompendium.APP.displayMessage("uDig is being launched. Please wait...\n\nYour request will be forwarded to uDig when it has opened", "uDig");
			}
		} else {
			System.out.println("About to send openMap command");
			reply = oClientSocket.openMap(sMapID);
		}

		return reply;
	}

	public String addProperty(String sData) {
		String reply = "";
		if (oClientSocket == null) {
			oAddPropertyCommandStack.push(sData);
			if (!connectToUDig()) {
				ProjectCompendium.APP.displayError("Unable to launch uDig.\nPlease launch manually.", "uDig");
			}
			else {
				ProjectCompendium.APP.displayMessage("uDig is being launched. Please wait...\n\nYour request will be forwarded to uDig when it has opened", "uDig");
			}
		} else {
			System.out.println("About to send addProperty command");
			reply = oClientSocket.addProperty(sData);
		}

		return reply;
	}

	public String editLabel(String sData) {
		String reply = "";
		if (oClientSocket == null) {
			oEditLabelCommandStack.push(sData);
			if (!connectToUDig()) {
				ProjectCompendium.APP.displayError("Unable to launch uDig.\nPlease launch manually.", "uDig");
			}
			else {
				ProjectCompendium.APP.displayMessage("uDig is being launched. Please wait...\n\nYour request will be forwarded to uDig when it has opened", "uDig");
			}
		} else {
			System.out.println("About to send editLabel command");
			reply = oClientSocket.editLabel(sData);
		}

		return reply;
	}

	public void sendHello() {
		Thread thread = new Thread("UDigConnectionMananger.sendHello") {
			public void run() {
				if (oClientSocket != null) {
					oClientSocket.sendHello();
				}
			}
		};
		thread.start();
	}

	public void sendGoodbye() {
		//Thread thread = new Thread("UDigConnectionMananger.sendGoodbye") {
		//	public void run() {
				if (oClientSocket != null) {
					oClientSocket.sendGoodbye();
				}
		//	}
		//};
		//thread.start();
	}

	public void openProject() {
		Thread thread = new Thread("UDigConnectionMananger.openProject") {
			public void run() {
				if (oClientSocket != null) {
					oClientSocket.openProject();
				}
			}
		};
		thread.start();
	}

	public void closeProject() {
		Thread thread = new Thread("UDigConnectionMananger.closeProject") {
			public void run() {
				if (oClientSocket != null) {
					oClientSocket.closeProject();
				}
			}
		};
		thread.start();
	}

	public boolean isUDigRunning() {
		return oServerSocket.isUDigRunning();
	}

	public UDigClientSocket getSendSocket() {
		return oClientSocket;
	}

	public UDigServerSocket getReceiveSocket() {
		return oServerSocket;
	}

	public void loadPorts() {
		try {
			File optionsFile = new File(FILENAME);
			Properties connectionProperties = new Properties();
			if (optionsFile.exists()) {
				connectionProperties.load(new FileInputStream(FILENAME));

				String value = connectionProperties.getProperty("compendium-port");
				if (value != null && !value.equals("")) {
					nServerPort = (new Integer(value)).intValue();
				}
				value = connectionProperties.getProperty("udig-port");
				if (value != null && !value.equals("")) {
					nClientPort = (new Integer(value)).intValue();
				}
			}
		} catch (Exception ex) {
			System.out.println("Unable to load external reference to Ecosensus Communication ports.\n\nUsing default ports.\n");
		}
	}

	/**
	 * Save the Ecosensus Connection data to a property file.
	 * @throws IO exception.
	 */
	public void savePort(int nPort) throws IOException {

		File optionsFile = new File(FILENAME);
		Properties oConnectionProperties = new Properties();

		if (optionsFile.exists()) {
			oConnectionProperties.load(new FileInputStream(FILENAME));
			oConnectionProperties.put("compendium-port", String.valueOf(nPort));
			oConnectionProperties.store(new FileOutputStream(FILENAME), "Ecosensus Connection Ports");
		} else {
			oConnectionProperties.put("compendium-port", String.valueOf(nPort));
			oConnectionProperties.put("udig-port", String.valueOf(UDigClientSocket.PORT));
			oConnectionProperties.store(new FileOutputStream(FILENAME), "Ecosensus Connection Ports");
		}
	}
}
