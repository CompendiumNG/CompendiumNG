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

package com.compendium.meeting.remote;


import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.security.Permission;

import com.compendium.ProjectCompendium;

/**
 * Listens for commands to start/stop recording an Access Grid meeting in Compendium.
 * @author Andrew G D Rowley / Michelle Bachler
 * @version 1-0
 */
public class RecordListener extends UnicastRemoteObject implements RemoteRecord {

    boolean startedRMIRegistry = false;
    
    Registry rmiRegistry = null;
    
    /**
     * Creates a new RecordListener instance.
     * @param instanceName The name used to represent this instance of the program
     * @param port The port on which to run the RMI registry
     * @throws RemoteException
     * @throws MalformedURLException
     */
    public RecordListener(String instanceName, int port) throws RemoteException, MalformedURLException {
        
        if (System.getSecurityManager() == null) {
            System.setSecurityManager (new RMISecurityManager() {
                public void checkPermission(Permission perm) {
                    // Does Nothing
                }
                public void checkPermission(Permission perm,
                        Object context) {
                    // Does Nothing
                }
              });
        }
        
        // Try to start an RMI registry
        try {
            rmiRegistry = LocateRegistry.createRegistry(port);
            startedRMIRegistry = true;
        } catch (Exception e) {
            startedRMIRegistry = false;
        }

        // Try to bind to the given registry
        Naming.rebind("//127.0.0.1:" + port + "/Compendium_" + instanceName, this); //$NON-NLS-1$ //$NON-NLS-2$
    }

 	/**
	 * Start a meeting replay recording. Extract the setup data first, and the Jabber data.
	 * @param sSetupData the memetic setup data required to communicate with Area/Triplestore.
	 * @param sReplayData the meeting replay Jabber account details required.
	 * @return true if the recording was started successfully, else false;
	 */
   public boolean startRecording(String sSetupData,String sReplayData) throws RemoteException {

		if (ProjectCompendium.APP.oMeetingManager.processSetupData(sSetupData)) {
			ProjectCompendium.APP.oMeetingManager.processReplayData(sReplayData);
            ProjectCompendium.APP.oMeetingManager.setupMeetingForReplay();
			ProjectCompendium.APP.oMeetingManager.startReplayRecording();
			return true;
		}

        return false;
    }

	/**
	 * Start an inital meeting recording. Extract the setup data first then start.
	 * @param sSetupData the memetic setup data required to communicate with Area/Triplestore.
	 * @return true if the recording was started successfully, else false;
	 */
    public boolean startRecording(String sSetupData) throws RemoteException {

		if (ProjectCompendium.APP.oMeetingManager.processSetupData(sSetupData)) {
			ProjectCompendium.APP.oMeetingManager.setupMeetingForRecording();
			ProjectCompendium.APP.oMeetingManager.startRecording();
			return true;
		}

        return false;
    }

	/**
	 * Start a meeting recording of the type passed.
	 * @param isReplay true if the recording to start is for a replay, false if it is for a initial meeting recording.
	 * @return true if the recording was started successfully, else false;
	 */
    public boolean startRecording(boolean isReplay) throws RemoteException {

		if (!isReplay) {
			ProjectCompendium.APP.oMeetingManager.startRecording();
			return true;
		}
		else {
			ProjectCompendium.APP.oMeetingManager.startReplayRecording();
			return true;
		}
    }

	/**
	 * Stop the current Meeting Recording/Replay session.
	 * @return true if the recording was stopped successfully, else false;
	 */
    public boolean stopRecording() {

		if (ProjectCompendium.APP.oMeetingManager.isReplay()) {
			ProjectCompendium.APP.oMeetingManager.stopReplayRecording();
			return true;
		}
		else {
			ProjectCompendium.APP.oMeetingManager.stopRecording();
			return true;
		}
    }
    
    /**
     * Returns true if the registry was started by us
     */
    public boolean startedRegistry() {
        return startedRMIRegistry;
    }
    
    /**
     * Returns the rmi registry started by us
     */
    public Registry getRegistry() {
        return rmiRegistry;
    }
}
