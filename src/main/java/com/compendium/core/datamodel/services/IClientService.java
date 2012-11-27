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
 *	The interface for the ClientService class
 *	The ClientService class provides remote services to manipuate code objects.
 *
 *	@author Sajid and Rema / Michelle Bachler
 */

public interface IClientService extends IService {

	/**
	 * Set the DBDatabaseManager used by this service.
	 *
	 * @param DBDatabaseManager dbMgr, the IServiceManager used by this service.
	 */
	public void setDatabaseManager(DBDatabaseManager dbMgr);

	/**
	 * Get the DBDatabaseManager used by this service.
	 *
	 * @return DBDatabaseManager, the DBDatabaseManager used by this service.
	 */
	public DBDatabaseManager getDatabaseManager();

	/**
	 * Set the IServiceManager used by this service.
	 *
	 * @param IServiceManager sm, the DBDatabaseManager used by this service.
	 */
	public void setServiceManager(IServiceManager sm);

	/**
	 * Return the IServiceManager instance use by this service.
	 *
	 * @return IServiceManager, the IServiceManager instance use by this service.
	 */
	public IServiceManager getServiceManager();

	/**
	 * Add the given PCSession to the list of sessions serviced by this service.
	 *
	 * @param PCSession session, the PCSession object to add to this service.
	 */
	public void addSession(PCSession session);

	/**
	 * Remove the given PCSession from the list of sessions serviced by this service.
	 *
	 * @param PCSession session, the PCSession object to remove from this service.
	 */
	public void removeSession(PCSession session);

	/**
	 * Return all sessions services by this service object.
	 *
	 * @return Enumeration, contianing all PCSession objects serviced by this service.
	 */
	public Enumeration getSessions();
}
