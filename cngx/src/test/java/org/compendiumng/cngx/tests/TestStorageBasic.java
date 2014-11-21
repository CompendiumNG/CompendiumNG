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

package org.compendiumng.cngx.tests;

import api.memory.IMemory;
import api.memory.elements.IElement;
import api.memory.elements.INode;
import api.memory.elements.ISession;
import api.memory.elements.Value;
import api.memory.exceptions.NodeCollisionException;
import api.memory.exceptions.SessionNotAvailableException;
import impl.memory.Memory;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public class TestStorageBasic {

	private static ISession Session = null;
	private static ISession ReadOnlySession = null;


	private static IMemory Memory = null;

	@Test(groups = "init")
	public void testInitSession() throws SessionNotAvailableException {
		assert (Session == null);
		assert (Memory == null);

		Memory = new Memory();
		assert (Memory!=null);

		Session = Memory.GetSession("testdata", "admin", "admin", true, false);
		assert (Session!=null);

		ReadOnlySession = Memory.GetSession("testdata", "admin", "admin", true, true);
		assert (ReadOnlySession != null);

	}

	@Test(groups = "memory", dependsOnGroups = "init", expectedExceptions = SessionNotAvailableException.class)
	public void testExclusiveSession() throws SessionNotAvailableException {
		assert (Session != null);
		ISession s;
		s = Memory.GetSession("testdata", "admin", "admin", true, true);
		// we must fail with request for exclusive session since another sessin is already opened
	}

	@Test(groups = "memory", dependsOnGroups = "init")
	public void testTwoSessionInteraction() throws NodeCollisionException {
		Map<String, Value> cust_props = new HashMap();

		String node1_label = "node1";
		cust_props.put("label", new Value(node1_label));

		INode node1 = Session.createNode(cust_props);
		UUID uuid = node1.getUuid();

		INode node2 = ReadOnlySession.getNode(uuid);
		assert (node2.getCustomProperties().get("label").getValue().equals(node1_label));
	}



	@Test(groups = "memory", dependsOnGroups = "init")

	public void testVersion() {

		IMemory m = new Memory();
		assert (m.API_VERSION == 1);
	}


	@Test(groups = "memory", dependsOnGroups = ("init"))
	public void testInstantiation() {
		String file0 = "thisfiledoesntexist";
		IMemory mem = new Memory();
		mem.openStore(file0, true, false);

		ISession session = null;

		try {
			session = mem.GetSession(file0, "admin", "admin", true, false);
			assert (session != null);
		} catch (SessionNotAvailableException e) {
			e.printStackTrace();
		}


		assert (mem.getSystemAttributeAsBoolean("isnew") == true);
		INode a = session.createNode();
		a.setLabel("Node A");
		assert (a.getLabel().equals("Node A"));

		INode b = session.createNode();
		b.setLabel("Node B");
		assert (b.getLabel().equals("Node B"));

	}

}
