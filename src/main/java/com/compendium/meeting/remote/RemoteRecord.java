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

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Indicates that the object can have recording started and stopped
 * @author Andrew G D Rowley
 */
public interface RemoteRecord extends Remote {


 	/**
	 * Start a meeting replay recording. Extract the setup data first, and the Jabber data.
	 * @param sSetupData the memetic setup data required to communicate with Area/Triplestore.
	 * @param sReplayData the meeting replay Jabber account details required.
	 * @return true if the recording was started successfully, else false;
	 */
    public boolean startRecording(String sSetupData,String sReplayData) throws RemoteException;

	/**
	 * Start an inital meeting recording. Extract the setup data first then start.
	 * @param sSetupData the memetic setup data required to communicate with Area/Triplestore.
	 * @return true if the recording was started successfully, else false;
	 */
    public boolean startRecording(String sSetupData) throws RemoteException;

	/**
	 * Start a meeting recording of the type passed.
	 * @param isReplay true if the recording to start is for a replay, false if it is for a initial meeting recording.
	 * @return true if the recording was started successfully, else false;
	 */
    public boolean startRecording(boolean isReplay) throws RemoteException;

    /**
     * Stops the recording
     */
    public boolean stopRecording() throws RemoteException;
}
