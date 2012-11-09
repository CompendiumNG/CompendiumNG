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
import java.net.*;
import java.util.Hashtable;
import java.util.NoSuchElementException;

import com.compendium.core.datamodel.services.*;
import com.compendium.core.datamodel.*;

/**
 * This object store a particular set of services being used in the curernt session.
 *
 * @author	Michelle Bachler
 */
public class ServiceCache  {

	/** An integer representing the maximum load for each service in this cache.*/
	private  int			   	nLoad = 10;

	/** Stores the Service objects added to this cache.*/
	private Hashtable			htServices	= null;

	/**
	 * Stores the Services references against their current service loads.
	 * When the count goes to zero the object is garbage collected.
	 */
	private Hashtable			htServicesCount = null;

	/**
	 *	Contructor, takes the maximum load on each service.
	 *
	 * @param in load, the maximum load for each service added to this cache.
	 */
	public ServiceCache(int load) {

		this.nLoad = load;

		htServices	= new Hashtable(51);
		htServicesCount = new Hashtable(51);
	}

	/**
	 *	Adds a service to the cache.
	 *
	 * @param String sName, the name of the service to add to the cache.
	 * @param IService Service, the servive object to add to the cache.
	 * @param Integer count, the current load on the services being added.
	 * @return boolean, - currently always returns true.
	 */
	public boolean put(String sName, IService service, Integer count) {

		htServices.put(sName, service);
		htServicesCount.put(sName, count);

		return true;
	}

	/**
	 * Remove the given service from the cache.
	 *
	 * @param ISerive service, the service to remove from the cache.
	 */
	public void remove(IService service) {

		int count = 0;
		String sName = service.getServiceName();

		count = ((Integer)htServicesCount.get(sName)).intValue();
		if(count > 0) {
			count-- ;
			htServices.put(sName, service);
			htServicesCount.put(sName, new Integer(count));
		}
	}

	/**
	 * Does this cache contain any services?
	 * @return boolean, true if it does, else false.
	 */
	public boolean isEmpty() {
		return htServices.isEmpty();
	}

	/**
	 * Returns the service with the lowest load count.
	 *
	 * @return Vector, containing:
	 *					0 = ID of the service with the lowest count;
	 *					1 = Service object
	 *					2 = The load count.
	 */
	public synchronized Vector getLowestCount() {


		String sID = ""; //$NON-NLS-1$
		String keyString = ""; //$NON-NLS-1$
		IService service = null;
		int lastCount = 0;
		int load = 0;

		for(Enumeration e = htServicesCount.keys(); e.hasMoreElements();) {

			keyString = (String)e.nextElement();
			load = ((Integer)htServicesCount.get(keyString)).intValue();

			if(load < nLoad && load < lastCount) {
				sID = keyString;
				lastCount = load;
				service = (IService)htServices.get(keyString);
			}
		}

		if (!sID.equals("") && lastCount > 0) { //$NON-NLS-1$
			Vector item = new Vector(2);
			item.addElement(sID);
			item.addElement(service);
			item.addElement(new Integer(lastCount));
			return item;
		}
		else {
			return null;
		}
	}

	/**
	 * Prints the load on each service to the System out stream.
	 */
	public void printServiceStatus() {

		String keyString = ""; //$NON-NLS-1$
		int load = 0;

		for(Enumeration e = htServicesCount.keys();e.hasMoreElements();) {
			keyString = (String)e.nextElement();
			load = ((Integer)htServicesCount.get(keyString)).intValue();
			System.out.println(keyString+" : "+load); //$NON-NLS-1$
		}
	}

	/**
	 * Empty the hashtable data to aid garbage collection
	 */
	public void cleanUp() {

		htServices.clear();
		htServices = null;

		htServicesCount.clear();
		htServicesCount = null;
	}
}
