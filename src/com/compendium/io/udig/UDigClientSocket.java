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


/**
 * This class handles the communication with Compendium
 *
 * @author Michelle Bachler
 */
public class UDigClientSocket extends Thread {

	public static final int PORT = 49335; //1115;

	public static String OK = "UDIG:OK";

	private Socket oSocket = null;

	private boolean socketDead = false;

	private BufferedReader reader = null;

	private PrintWriter writer = null;

	private InputStream inp;

	private OutputStream outp;

	private int nPort = PORT;

	private UDigCommunicationManager oManager = null;

	/**
	 * Start up two sockets, on to listen on and ont to send on.
	 */
	public UDigClientSocket(UDigCommunicationManager oManager, int nPort) throws UnknownHostException, IOException, SecurityException {

		this.oManager = oManager;
		this.nPort = nPort;

		try {
			createClientSocket();
		} catch (UnknownHostException e) {
			socketDead = true;
			System.out.println("Exception: " + e.getMessage());
			close();
			clearSpace();
			throw e;
		} catch (java.net.BindException bind) {
			bind.printStackTrace();
			socketDead = true;
			System.out.println("Bind Exception: " + bind.getMessage());
			close();
			clearSpace();
			throw bind;
		} catch (IOException e) {
			e.printStackTrace();
			socketDead = true;
			System.out.println("Exception: " + e.getMessage());
			close();
			clearSpace();
			throw e;
		}
	}

	private void createClientSocket() throws UnknownHostException, IOException, ConnectException {
		oSocket = new Socket("localhost", nPort);
		outp = oSocket.getOutputStream();
		writer = new PrintWriter(outp, true);
		inp = oSocket.getInputStream();
		reader = new BufferedReader(new InputStreamReader(inp), 2048);
		System.out.println("uDig says="+reader.readLine());
	}

	/**
	 * Send a Ready message to Compenduim to say that you have logged into a project.
	 * @return the reply from uDig
	 */
	public synchronized String openMap(String sPath) {
		String reply = send("COMP:MAP="+sPath);
		if (reply.equals("UDIG:OK")) {
			return reply;
		}
		else if (reply.equals("DEAD")){
			/*try {
				close();
				createClientSocket();
			} catch (UnknownHostException e) {
				socketDead = true;
				System.writer.println("Exception: " + e.getMessage());
			} catch (IOException e) {
				socketDead = true;
				System.writer.println("Exception: " + e.getMessage());
			}*/
		}

		return reply;
	}

	/**
	 * Send a message to uDig to add property data to a point.
	 * @return the reply from uDig
	 */
	public synchronized String addProperty(String sData) {
		String reply = send("COMP:PROPERTY="+sData);
		if (reply.equals("UDIG:OK")) {
			return reply;
		}
		else if (reply.equals("DEAD")){
			/*try {
				close();
				createClientSocket();
			} catch (UnknownHostException e) {
				socketDead = true;
				System.writer.println("Exception: " + e.getMessage());
			} catch (IOException e) {
				socketDead = true;
				System.writer.println("Exception: " + e.getMessage());
			}*/
		}

		return reply;
	}

	/**
	 * Send a message to uDig to edit the label of a point.
	 * @return the reply from uDig
	 */
	public synchronized String editLabel(String sData) {
		String reply = send("COMP:LABEL="+sData);
		if (reply.equals("UDIG:OK")) {
			return reply;
		}
		else if (reply.equals("DEAD")){
			/*try {
				close();
				createClientSocket();
			} catch (UnknownHostException e) {
				socketDead = true;
				System.writer.println("Exception: " + e.getMessage());
			} catch (IOException e) {
				socketDead = true;
				System.writer.println("Exception: " + e.getMessage());
			}*/
		}

		return reply;
	}

	/**
	 * Send an open project message to Compenduim.
	 * @return
	 */
	public synchronized boolean openProject() {

		String reply = send("COMP:OPEN-PROJECT");
		if (reply.equals("UDIG:OK")) {
			return true;
		}
		else if (reply.equals("DEAD")){
			/*try {
				close();
				createClientSocket();
			} catch (UnknownHostException e) {
				socketDead = true;
				System.writer.println("Exception: " + e.getMessage());
			} catch (IOException e) {
				socketDead = true;
				System.writer.println("Exception: " + e.getMessage());
			}*/
		}

		return false;
	}

	/**
	 * Send an open project message to Compenduim.
	 * @return
	 */
	public synchronized boolean closeProject() {

		String reply = send("COMP:CLOSE-PROJECT");
		if (reply.equals("UDIG:OK")) {
			return true;
		}
		else if (reply.equals("DEAD")){
			/*try {
				close();
				createClientSocket();
			} catch (UnknownHostException e) {
				socketDead = true;
				System.writer.println("Exception: " + e.getMessage());
			} catch (IOException e) {
				socketDead = true;
				System.writer.println("Exception: " + e.getMessage());
			}*/
		}

		return false;
	}

	/**
	 * Send a Hello message to Compenduim to see if it is there.
	 * @return
	 */
	public synchronized boolean sendHello() {

		String reply = send("COMP:HELLO");
		if (reply.equals("UDIG:HELLO")) {
			return true;
		}
		else if (reply.equals("DEAD")){
			/*try {
				close();
				createClientSocket();
				//sendHello();
			} catch (UnknownHostException e) {
				socketDead = true;
				System.writer.println("Exception: " + e.getMessage());
			} catch (IOException e) {
				socketDead = true;
				System.writer.println("Exception: " + e.getMessage());
			}*/
		}

		return false;
	}

	/**
	 * Send a Ready message to Compenduim to say that you have logged into a project.
	 * @return
	 */
	public synchronized boolean sendGoodbye() {
		String reply = send("COMP:GOODBYE");
		if (reply.equals("UDIG:OK")) {
			return true;
		}
		return false;
	}

	/**
	 * Send given message to Compendium.
	 *
	 * @param request the message to send.
	 * @return any reply.
	 */
	public synchronized String send(String request) {

		String reply = "";
		String str;

		try {
			if (request != null) {
				// IF SOCKET DEAD NO MORE REQUESTS CAN BE MADE, SO RETURN FALSE
				if (socketDead) {
					return "DEAD";
				}

				System.out.println("Compendium client send: "+request);
				System.out.flush();

				writer.println(request);

				// GET IST LINE OF REPLY - COMP: etc //
				System.out.println("Compendium client waiting for reply");
				System.out.flush();

				str = reader.readLine();

				System.out.println("Compendium client received: " + str);
				System.out.flush();

				int returnLen = 0;
				if (str != null)
					returnLen = str.length();
				else {
					socketDead = true;
					System.out.println("null sent from uDig");
					return reply;
				}

				// BREAK UP AND STRIP REST OF PACKET INTO STRINGS IN A VECTOR
				if (returnLen >= 5
						&& str.substring(0, 5).equalsIgnoreCase("UDIG:")) {
					return str;
				}
			}
		} catch (IOException ioerr) {
			socketDead = true;
			System.out.print("I/O error on request: "+ioerr.getMessage());
		}

		return reply;
	}

	/**
	 * Break up the data in this string.
	 *
	 * @param sData
	 * @return
	 */
	private String parseString(String sData) {

		String reply = "";

		if (sData.startsWith("UDIG:HELLO")) {
			reply = sData;
		} else if (sData.startsWith("UDIG:OK")) {
			reply = sData;
		} else if (sData.startsWith("UDIG:PATH=") && sData.length() > 10) {
			reply = sData.substring(11);
		} else if (sData.startsWith("UDIG:MAP=") && sData.length() > 10) {
			reply = sData.substring(11);
		}

		return reply;
	}

	// CLOSE THREAD CONNECTION IN THE NORMAL WAY //
	public synchronized void close() {
		if (writer != null)
			writer.close();

		try {
			if (outp != null)
				outp.close();

			if (reader != null)
				reader.close();

			if (inp != null)
				inp.close();
		} catch (IOException e) {
			socketDead = true;
			System.out.println("Exception:" + e.getMessage());
			return;
		}

		try {
			if (oSocket != null)
				oSocket.close();
		} catch (IOException e) {
			socketDead = true;
			System.out.println("Exception:" + e.getMessage());
			return;
		}
	}

	// NULL VARIABELS TO HELP GC FREE UP SPACE //
	public void clearSpace() {
		oSocket = null;
		inp = null;
		outp = null;
		reader = null;
		writer = null;
	}

	// RETURN SOCKETDEAD VARIABLE //
	public boolean getSocketDead() {
		return socketDead;
	}
}
