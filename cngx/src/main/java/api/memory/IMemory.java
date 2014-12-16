/*
		Copyright [2014] [Michal Stekrt]

		Licensed under the Apache License, Version 2.0 (the "License");
		you may not use this file except in compliance with the License.
		You may obtain a copy of the License at

		http://www.apache.org/licenses/LICENSE-2.0

		Unless required by applicable law or agreed to in writing, software
		distributed under the License is distributed on an "AS IS" BASIS,
		WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
		See the License for the specific language governing permissions and
		limitations under the License.

 */

package api.memory;


import api.memory.elements.ISession;
import api.memory.exceptions.SessionNotAvailableException;
import api.memory.exceptions.StoreNotAvailableException;

import java.util.LinkedList;

public interface IMemory {

	final public int API_VERSION = 1;
	final int IMPL_VERSION = 1;

	final LinkedList<Store> STORES = new LinkedList<Store>();
	final LinkedList<ISession> SESSIONS = new LinkedList<ISession>();



	/**
	 *
	 * @param writeable - if this parameter is set to false then the session is read-only
	 * @param exclusive - if this parameter is set to true then the returned session is exclusive and no other session can be opened
	 * @return session that can be used to manipulate objects in memory
	 * @throws api.memory.exceptions.SessionNotAvailableException if exclusive session is requested but other session exist or if max_session limit is reached
	 */
	ISession GetSession(String store_url, String username, String password, boolean writeable, boolean exclusive) throws SessionNotAvailableException;


	/**
	 *
	 * @return    read-write non-exclusive session that can be used to manipulate objects in memory
	 * @throws SessionNotAvailableException if session limit (max_sessions) is reached
	 */
	public ISession GetSession () throws SessionNotAvailableException;

	Store openStore(String spec, boolean exclusive, boolean readonly);
	Store closeStore(String spec);


	boolean getSystemAttributeAsBoolean(String attribute_name);
	boolean getSystemAttributeAsString(String attribute_name);
	boolean getSystemAttributeAsInteger(String attribute_name);
	boolean getSystemAttributeAsDouble(String attribute_name);

}
