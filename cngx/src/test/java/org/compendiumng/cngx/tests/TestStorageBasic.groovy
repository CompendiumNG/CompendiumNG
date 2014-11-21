/*
 *
 * 		Copyright [2014] [Michal Stekrt]
 *
 * 		Licensed under the Apache License, Version 2.0 (the "License");
 * 		you may not use this file except in compliance with the License.
 * 		You may obtain a copy of the License at
 *
 * 		http://www.apache.org/licenses/LICENSE-2.0
 *
 * 		Unless required by applicable law or agreed to in writing, software
 * 		distributed under the License is distributed on an "AS IS" BASIS,
 * 		WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * 		See the License for the specific language governing permissions and
 * 		limitations under the License.
 *
 *
 */

package org.compendiumng.cngx.tests;

import api.memory.IMemory;
import api.memory.ISession;
import api.memory.IValue;
import api.memory.exceptions.MemoryNotInitializedException;
import api.memory.exceptions.ElementCollisionException;
import api.memory.exceptions.SessionNotAvailableException;

import impl.memory.Memory;

import org.testng.annotations.Test

class TestStorageBasic {

	private static final String memory_spec = "memory:testdata1";
	private static final String memory_user = "admin";
	private static final String memory_passwd = "admin";
	private static ISession Session = null;
	private static ISession ReadOnlySession = null;
	private static IMemory Memory = null;


	@Test(groups = "init")
	def testInitSession() throws SessionNotAvailableException, MemoryNotInitializedException {
		assert (Session == null);
		assert (Memory == null);

		Session = Memory.GetSession(memory_user, memory_passwd, true, false);
		assert (Session != null);

		IMemory memory = Session.

		ReadOnlySession = Memory.GetSession(memory_user,memory_passwd, true, true);
		assert (ReadOnlySession != null);

	}

	@Test(groups = "memory", dependsOnGroups = "init", expectedExceptions = SessionNotAvailableException.class)
	def public void testExclusiveSession() throws SessionNotAvailableException, MemoryNotInitializedException {
		assert (Session != null);
		ISession s;
		s = Memory.GetSession(memory_user, memory_passwd, true, true);
		// we must fail with request for exclusive session since another session is already opened
	}

	@Test(groups = "memory", dependsOnGroups = "init")
	public void testTwoSessionInteraction() throws ElementCollisionException {
		Map<String, IValue> cust_props = new HashMap();

		String node1_label = "node1";
		cust_props.put("label", new IValue(node1_label));

		Node node1 = Session.createNode(cust_props);
		UUID uuid = node1.getUuid();

		Node node2 = ReadOnlySession.getNode(uuid);
		assert (node2.getProperties().get("label").getValue().equals(node1_label));
	}


	@Test(groups = "memory", dependsOnGroups = "init")
	public void testVersion() {

		IMemory m = new Memory(memory_spec);
		assert (m.API_VERSION == 1);
	}


	@Test(groups = "memory", dependsOnGroups = "init")
	public void testInstantiation() throws MemoryNotInitializedException {
		String nonexistantfile = "thisfiledoesntexist";
		IMemory mem = new Memory(nonexistantfile);
		ISession session = null;

		try {
			session = mem.GetSession(memory_user, memory_passwd, true, false);
			assert (session != null);
		} catch (SessionNotAvailableException e) {
			e.printStackTrace();
		}

		assert (mem.getSystemAttributeAsBoolean("isnew") == true);
		Node a = session.createNode();
		a.setLabel("Node A");
		assert (a.getLabel().equals("Node A"));

		Node b = session.createNode();
		b.setLabel("Node B");
		assert (b.getLabel().equals("Node B"));

	}
	
	@Test(groups = "memory", dependsOnGroups = "init")
	def testNodeCreation() {
		Node nodeA = new Node(elementType: ElementType.INode)
		with nodeA {
			assert (mtime == ctime)
			assert (mtime == atime)
			assert (mtime != null)
			assert (rid == null)
			assert (uuid != null)
			assert (elementType == ElementType.INode)
		}
	}

}
