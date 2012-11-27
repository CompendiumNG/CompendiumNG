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

package com.compendium.core.datamodel.services;

import java.util.*;


/**
 * This object store a particular set of users against their sessions
 *
 * @author	Michelle Bachler
 */
public class UserSessionCache  {

	/** Holds the User ids against a Vector of their session ids */
	private Hashtable			htUserSessions = null;

	/** Constructor */
	public UserSessionCache() {

		htUserSessions = new Hashtable(51);
	}

	/**
	 * Adds a service to the cache against the given user id.
	 *
	 * @param String sUserID, the user id of the user to add the session against.
	 * @param String sSession, the id of the sesion to add.
	 */
	public void put(String sUserID, String sessionID) {

		Vector vtSessions = null;

		if (htUserSessions.containsKey(sUserID)) {
			vtSessions = (Vector)htUserSessions.get(sUserID);
			vtSessions.addElement(sessionID);
		}
		else {
			vtSessions = new Vector(10);
			vtSessions.addElement(sessionID);
			htUserSessions.put(sUserID, vtSessions);
		}
	}

	/**
	 * Remove the given session from the user session cache.
	 *
	 * @param String sUserID, the user id of the user whose session to remove from the cache.
	 * @param String sSession, the id of the sesion to remove.
	 */
	public void remove(String sUserID, String sSessionID) {

		if (htUserSessions.containsKey(sUserID)) {
			Vector vtSessions = (Vector)htUserSessions.get(sUserID);
			int nCount = vtSessions.size();
			for (int i=0; i<nCount; i++) {
				String sID = (String)vtSessions.elementAt(i);
				if (sID.equals(sSessionID))
					vtSessions.remove(sID);
			}
		}
	}


	/**
	 * Empty the hashtable data to aid garbage collection
	 */
	public void cleanUp() {

		htUserSessions.clear();
		htUserSessions = null;
	}
}
