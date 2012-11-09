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
import java.util.StringTokenizer;
import java.util.Vector;
import java.io.*;

import javax.swing.ImageIcon;

import com.compendium.*;
import com.compendium.core.ICoreConstants;
import com.compendium.core.datamodel.IModel;
import com.compendium.core.datamodel.NodePosition;
import com.compendium.core.datamodel.View;
import com.compendium.ui.*;
import com.compendium.ui.plaf.*;

/**
 * This class handles the communication with Compendium
 *
 * @author Michelle Bachler
 */
public class UDigServerSocket extends Thread {

	public static final int PORT = 49336; //1116;

	private ServerSocket oServerSocket = null;
	private boolean socketDead = false;

	public static final int UDIG_STOPPED = 0;
	public static final int UDIG_RUNNING = 1;
	public static final int UDIG_PROJECT = 2;

	private int nStatus = UDIG_STOPPED;

	private int nPort = PORT;

	private UDigCommunicationManager oManager = null;

	/**
	 * Constructor, does nothing.
	 */
	public UDigServerSocket(UDigCommunicationManager oManager, int nPort) {
		this.oManager = oManager;
		this.nPort = nPort;
	}

	public void run() {
		createServerSocket();
	}

	private void createServerSocket() {
		System.out.println("Trying to connect Server on port="+nPort);
		try {
			// establish the socket to listen on
			oServerSocket = new ServerSocket(nPort);
			try {
				oManager.savePort(nPort);
			} catch (Exception ex) {
				System.out.println("Error saving ecosensus connection properties due to:\n\n"+ex.getMessage());
			}

			/**
			 * listen for new connection requests. when a request arrives,
			 * service it and resume listening for more requests.
			 */
			while (true) {
				Socket client = oServerSocket.accept();
				serviceConnection(client);
			}

		} catch (UnknownHostException e) {
			socketDead = true;
			System.out.println("Exception: " + e.getMessage());
		} catch (java.net.BindException e) {
			oServerSocket = null;
			// IF IT CAN'T BIND TO PORT
			// try another port - in a loop?
			if (nPort < 62000) {
				nPort += 2;
			} else {
				nPort = PORT;
			}
			createServerSocket();
		} catch (SocketException e) {
			// when client disconnects
			createServerSocket();
		} catch (IOException e) {
			e.printStackTrace();
			socketDead = true;
			System.out.println("Exception: " + e.getMessage());
		} finally {
			close();
			clearSpace();
		}
	}

	/**
	 * Process an incomming message.
	 * These can be:</br>
	 * UDIG:HELLO</br>
	 * UDIG:PATH<br>
	 * UDIG:MAP<br>
	 * @param client
	 */
	public void serviceConnection(Socket client) {

		BufferedReader reader = null;
		PrintWriter writer = null;
		String sAuthor = ProjectCompendium.APP.getModel().getUserProfile().getUserName();

		try {
			reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
			writer = new PrintWriter(client.getOutputStream(), true);
			writer.println("COMP:WELCOME");

			boolean done = false;
			while (!done) {
				String sData = reader.readLine();
				if ((sData == "")) {
					done = true;
				} else {
					if (sData != null) {
						System.out.println("Compendium Server received: "+sData);
						if (sData.startsWith("UDIG:HELLO")) {
							int previousStatus = nStatus;
							nStatus = UDIG_RUNNING;
							writer.println("COMP:HELLO");
							if (oManager != null) {
								if (previousStatus == UDIG_STOPPED) {
									oManager.createSendSocket();
									if (ProjectCompendium.APP.getModel() != null) {
										oManager.openProject();
									}
									else {
										oManager.sendHello();
									}
								}
								nStatus = UDIG_PROJECT;

								// wait for uDig to finish starting up
								// This is a hack. Need to fix this on the uDig side.
								Thread thread = new Thread("UDigServerSocket.Hello") {
									public void run() {
										try {this.sleep(20000);}
										catch(Exception i) {
											i.printStackTrace();
										}
										oManager.runCommands();
									}
								};
								thread.start();

								//oManager.runCommands();
							}
						} else if (sData.startsWith("UDIG:PROJECTS-READY")) {
							//nStatus = UDIG_PROJECT;
							writer.println("COMP:OK");
							//int loop=0;
							//while (loop<600000) {
							//	loop++;
							//}
							oManager.runCommands();
						} else if (sData.startsWith("UDIG:GOODBYE")) {
							nStatus = UDIG_STOPPED;
							writer.println("COMP:OK");
							oManager.destroyClientSocket();
							oManager.clearCommands();
						} else if (sData.startsWith("UDIG:MAP=") && sData.length() > 10) {
							writer.println("COMP:OK");
							sData = sData.substring(10);
							int index = sData.indexOf("/");
							if (index > -1) {
								String sNodeID=sData.substring(index+1);
								IModel model = ProjectCompendium.APP.getModel();
								View view = null;
								try {
									view = (View)model.getNodeService().getView(model.getSession(), sNodeID);
								} catch (Exception ex) {}
								if (view == null) {
									ProjectCompendium.APP.displayError("Could not find requested Map in this project.");
									return;
								}

								view.initialize(model.getSession(), model);
								UIViewFrame viewFrame = ProjectCompendium.APP.addViewToDesktop(view, view.getLabel());
								Vector history = new Vector();
								history.addElement(new String("uDig"));
								viewFrame.setNavigationHistory(history);
							}
						} else if (sData.startsWith("UDIG:LABEL=") && sData.length() > 11) {
							writer.println("COMP:OK");
							sData = sData.substring(11);
							int breaker = sData.indexOf("&&");
							if (breaker != -1 && sData.length() >= breaker+2) {
								String sPath = sData.substring(0,breaker);
								String sLabel = sData.substring(breaker+2);

								int index = sPath.indexOf("/");
								if (index > -1) {
									String sNodeID=sPath.substring(index+1);
									IModel model = ProjectCompendium.APP.getModel();
									View view = null;
									try {
										view = (View)model.getNodeService().getView(model.getSession(), sNodeID);
									} catch (Exception ex) {}
									if (view == null) {
										ProjectCompendium.APP.displayError("Could not find required Map to update label in this project.");
										return;
									}
									view.initialize(model.getSession(), model);
									try {
										view.setLabel(sLabel, sAuthor);
									} catch (Exception io) {
										ProjectCompendium.APP.displayError("Unable to update Map label due to:\n\n"+io.getMessage());
									}
								}
							}
						} else if (sData.startsWith("UDIG:PATH=") && sData.length() > 9 ) {
							sData = sData.substring(10);
							System.out.println("data="+sData);
							String sPath = processCreateMapRequest(sData, sAuthor);
							writer.println("COMP:PATH="+sPath);
						} else {
							writer.println("COMP:UNKNOWN");
						}
					} else {
						//writer.println("COMP:NULL");
					}
				}
				writer.flush();
			}
		} catch (SocketException ioe) {
			ioe.printStackTrace();
			System.err.println(ioe);
		} catch (IOException ioe) {
			ioe.printStackTrace();
			System.err.println(ioe);
		} finally {
			try {
				if (reader != null)
					reader.close();
				if (writer != null)
					writer.close();
				if (client != null)
					client.close();
			} catch (IOException ioee) {
				System.err.println(ioee);
			}
		}
	}

	/**
	 * Process the request from usdig to create a new map.
	 * @param sData
	 * @return String the path to the new view
	 */
	private String processCreateMapRequest(String sData, String sAuthor) {
		sData = sData.trim();

		String sIconFile = null;
		String sLong = null;
		String sLat = null;
		String sName = null;
		String sPath = null;
		String sDesc = "";
		String sTemplate = "";

		StringTokenizer oTokenizer = new StringTokenizer(sData, "&&");

		StringTokenizer oInnerTokenizer = null;
		String sKey = "";
		String sValue = "";
		while (oTokenizer.hasMoreTokens()) {
			String token = (String)oTokenizer.nextToken();
			oInnerTokenizer = new StringTokenizer(token, "=");
			if (oInnerTokenizer.hasMoreTokens()) {
				sKey = oInnerTokenizer.nextToken();

                sValue = "";
				if (oInnerTokenizer.hasMoreTokens()) {
					sValue = oInnerTokenizer.nextToken();
                }
                System.out.println(sKey + "=" + sValue);

				if (sKey.equals("path")) {
 					sPath = sValue;
				} else if (sKey.equals("icon")) {
					sIconFile = sValue;
				} else if (sKey.equals("name")) {
					sName = sValue;
				} else if (sKey.equals("long")) {
					sLong = sValue;
				} else if (sKey.equals("lat")) {
					sLat = sValue;
				} else if (sKey.equals("template")) {
					sTemplate = sValue;
					sTemplate = sTemplate.trim();
				}
			}
		}

		sDesc = new String("Long:"+sLong+" Lat:"+sLat);
		UIViewFrame frame = ProjectCompendium.APP.getCurrentFrame();

		String sNodeID = "";
		String sViewID = frame.getView().getId();
		View view = null;

		if (frame instanceof UIMapViewFrame) {
			UIMapViewFrame mapFrame = (UIMapViewFrame)frame;
			UIViewPane uiview = mapFrame.getViewPane();
			ViewPaneUI oViewPaneUI = uiview .getViewPaneUI();

			int nX = (mapFrame.getWidth()/2)-60;;
			int nY = (mapFrame.getHeight()/2)-60;
			int hPos = mapFrame.getHorizontalScrollBarPosition();
			int vPos = mapFrame.getVerticalScrollBarPosition();
			nX = nX + hPos;
			nY = nY + vPos;

			UINode oNode = oViewPaneUI.addNewNode(ICoreConstants.MAPVIEW, nX, nY, sName, sDesc);
			// GIVE IT THE PASSED MAP IMAGE
			try {
				oNode.getNode().setSource("UDIG:"+sPath, sIconFile, sAuthor);
			} catch(Exception e) {}

			//ImageIcon icon = new ImageIcon(sIconFile);

			oNode.setReferenceIcon(sIconFile);

			view = (View)oNode.getNode();
			sNodeID = view.getId();
			//view.setSelectedNode(oNode,ICoreConstants.MULTISELECT);
			//oNode.setSelected(true);
			oNode.setRollover(false);
		} else {
			UIListViewFrame listFrame = (UIListViewFrame) frame;
			UIList uilist = listFrame.getUIList();
			ListUI listUI = uilist.getListUI();

			int nY = (listUI.getUIList().getNumberOfNodes() + 1) * 10;
			int nX = 0;

			NodePosition node = listUI.createNode(ICoreConstants.MAPVIEW,
								 "",
								 ProjectCompendium.APP.getModel().getUserProfile().getUserName(),
								 sName,
								 sDesc,
								 nX,
								 nY
								 );

			view = (View)node.getNode();
			sNodeID = view.getId();
			try {
				view.setSource("UDIG:"+sPath, sIconFile, sAuthor);
			} catch(Exception e) {}

			UIList uiList = listUI.getUIList();
			uiList.updateTable();
			uiList.selectNode(uiList.getNumberOfNodes() - 1, ICoreConstants.MULTISELECT);
		}

		UIViewFrame newFrame = ProjectCompendium.APP.addViewToDesktop(view, sName);
		try {newFrame.setMaximum(true);}
		catch(Exception e){};

		if (view != null) {
			if (!sTemplate.equals("")) {
				ProjectCompendium.APP.onTemplateImport(sTemplate);
			}
		}
		return new String(sViewID+"/"+sNodeID);
	}

	public boolean isUDigReady() {
		if (nStatus == UDIG_PROJECT) {
			return true;
		}
		return false;
	}

	public boolean isUDigRunning() {
		if (nStatus == UDIG_RUNNING || nStatus == UDIG_PROJECT) {
			return true;
		}
		return false;
	}

	/**
	 * Close this socket.
	 */
	public void close() {
		try {
			if (oServerSocket != null)
				oServerSocket.close();
		} catch (IOException e) {
			socketDead = true;
			System.out.println("Exception:" + e.getMessage());
			return;
		}
	}


	// NULL VARIABELS TO HELP GC FREE UP SPACE //
	public void clearSpace() {
		oServerSocket = null;
	}

	// RETURN SOCKETDEAD VARIABLE //
	public boolean getSocketDead() {
		return socketDead;
	}
}
