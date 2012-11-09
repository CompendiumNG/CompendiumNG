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
import java.sql.SQLException;

import com.compendium.core.datamodel.*;
import com.compendium.core.db.*;
import com.compendium.core.db.management.*;

/**
 *	The ClientService class extends Service and is itself extended by all other service classes.
 *
 *	@author Sajid and Rema / Michelle Bachler
 */

public class ClientService extends Service implements IClientService {

	/** The DBDatabaseManager instance used by this Service.*/
	private DBDatabaseManager oDbMgr = null;

	/** The IServiceManager instance used by this Service.*/
	private IServiceManager oServiceManager = null;

	/** List of sessions that this service object services.
	 * key  = the sessionId from the client,
	 * value = the session object that has details ( model name, userID )
	 */
	private Hashtable htSessions = new Hashtable(51);

	/**
	 *	Constructor, does nothing
	 */
	public  ClientService() {}

	/**
	 * Constructor, set the name of the service.
	 *
	 * @param String sName, the name of this service.
	 */
	public  ClientService(String sName) {
		super();
		super.setServiceName(sName);
	}

	/**
	 *	Constructor, set the name, ServiceManager and DBDatabaseManager for this service.
	 *
	 * @param String sName, the name of this service.
	 * @param ServiceManager sm, the ServiceManager used by this service.
	 * @param DBDatabaseManager dbMgr, the DBDatabaseManager used by this service.
	 */
	public  ClientService(String sName, ServiceManager sm,  DBDatabaseManager dbMgr) {

		super(sName, sm, dbMgr);
		oDbMgr = dbMgr ;
		oServiceManager = sm ;
	}

	/**
	 * Set the DBDatabaseManager used by this service.
	 *
	 * @param DBDatabaseManager dbMgr, the DBDatabaseManager used by this service.
	 */
	public void setDatabaseManager(DBDatabaseManager dbMgr) {
		oDbMgr = dbMgr ;
	}

	/**
	 * Get the DBDatabaseManager used by this service.
	 *
	 * @return DBDatabaseManager, the DBDatabaseManager used by this service.
	 */
	public DBDatabaseManager getDatabaseManager() {
		return oDbMgr ;
	}

	/**
	 * Set the IServiceManager used by this service.
	 *
	 * @param IServiceManager sm, the IServiceManager used by this service.
	 */
	public void setServiceManager(IServiceManager sm) {
		oServiceManager = sm ;
	}

	/**
	 * Return the IServiceManager instance use by this service.
	 *
	 * @return IServiceManager, the IServiceManager instance use by this service.
	 */
	public IServiceManager getServiceManager() {
		return oServiceManager ;
	}

	/**
	 * Add the given PCSession to the list of sessions serviced by this service.
	 *
	 * @param PCSession session, the PCSession object to add to this service.
	 */
	public void addSession(PCSession session) {
		String key = session.getSessionID() ;
		if (!htSessions.containsKey(key)) {
			htSessions.put(key, session) ;
		}
	}

	/**
	 * Remove the given PCSession from the list of sessions serviced by this service.
	 *
	 * @param PCSession session, the PCSession object to remove from this service.
	 */
	public void removeSession(PCSession session) {
		String key = session.getSessionID() ;
		if (htSessions.containsKey(key)) {
			htSessions.remove(key);
		}
	}

	/**
	 * Return all sessions services by this service object.
	 *
	 * @return Enumeration, contianing all PCSession objects serviced by this service.
	 */
	public Enumeration getSessions() {
		return htSessions.elements() ;
	}
}
