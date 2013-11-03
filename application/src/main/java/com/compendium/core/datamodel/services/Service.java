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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.compendium.core.db.management.DBDatabaseManager;


/**
 *	The base class for all Service classes.
 *
 *	@author Sajid and Rema / Michelle Bachler
 */
public class Service implements IService, java.io.Serializable {
	/**
	 * class's own logger
	 */
	final Logger log = LoggerFactory.getLogger(getClass());
	private String sName;
	private ServiceManager oServiceManager = null;
	private DBDatabaseManager oDatabaseManager = null;

	/**
	 *	Constructors, does nothing
	 */
    Service() {}

	/**
	 * Constructor, set the name of the service.
	 *
	 * @param String sName, the name of this service.
	 */
	public Service(String sName) {
		this.sName = sName;
	}

	/**
	 * Constructor, set the name of the service.
	 *
	 * @param String sName, the name of this service.
	 * @param ServiceManager sm, the ServiceManager used by this service.
	 */
	public Service(String sName, ServiceManager sm ) {
		this.sName = sName ;
		oServiceManager = sm ;
	}

	/**
	 *	Constructor, set the name, ServiceManager and DBDatabaseManager for this service.
	 *
	 * @param String sName, the name of this service.
	 * @param ServiceManager sm, the ServiceManager used by this service.
	 * @param DBDatabaseManager dbMgr, the DBDatabaseManager used by this service.
	 */
    Service(String sName, ServiceManager sm, DBDatabaseManager dbMgr) {
		this.sName = sName ;
		oServiceManager = sm ;
		oDatabaseManager = dbMgr;
	}

	/**
	 * Return the name of this service.
	 *
	 * @return String, the name of this service.
	 */
	public String getServiceName() {
		return sName;
	}

	/**
	 * Set the name of this service.
	 *
	 * @param String sName, the name of this service.
	 */
	public void setServiceName(String aName) {
		this.sName = sName;
	}

	/**
	 * Return the IServiceManager instance use by this service.
	 *
	 * @return IServiceManager, the IServiceManager instance use by this service.
	 */
	public IServiceManager getServiceManager() {
		return oServiceManager ;
	}
}
