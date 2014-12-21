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

package org.compendiumng.cngx.tests
import org.compendiumng.cngx.memory.Element
import org.compendiumng.cngx.memory.Session
import org.compendiumng.cngx.memory.exceptions.ElementCollisionException
import org.compendiumng.cngx.memory.exceptions.MemoryNotInitializedException
import org.compendiumng.cngx.memory.exceptions.SessionNotAvailableException
import org.testng.annotations.BeforeClass
import org.testng.annotations.Test

@Test(groups="groovy")
class TestStorageBasic {

	private static final String memory_spec = "memory:testdata1"
	private static final String memory_user = "admin"
	private static final String memory_passwd = "admin"
	private static SessionRW1
	private static SessionRW2
	private static SessionRO

	@Test(groups = ["dummy", "groovy"])
	public void testAdding() {
		int a = 3;
		int b = -3;
		int c = a + b;
		assert (c == 0);
	}

	@BeforeClass
	def testInitSession()  {
		assert (SessionRW1 == null)
		assert (SessionRW2 == null)
		assert (SessionRO == null)

		def final Map session_props_rw = [user: memory_user,password: memory_passwd,connection: memory_spec,readonly: false]
		def final Map session_props_ro = [user:memory_user,password:memory_passwd,connection:memory_spec,readonly: true]

		SessionRW1 = Session.Create(session_props_rw)
		SessionRW2 = Session.Create(session_props_rw)
		SessionRO = Session.Create(session_props_ro)
		assert (SessionRW1 != null);
		assert (SessionRW2 != null);
		assert (SessionRO != null);
	}

	@Test(groups = 'memory', expectedExceptions = SessionNotAvailableException.class)
	def public void testExclusiveSession() throws SessionNotAvailableException, MemoryNotInitializedException {
		assert (Session != null);
		def s = Session.GetSession(memory_user, memory_passwd, true, true);
		// we must fail with request for exclusive session since another session is already opened
	}

	@Test(groups = 'memory')
	public void testTwoSessionInteraction() throws ElementCollisionException {

		def node1 = SessionRW1.createElement(["label":"nodeA", elementType: Element.ElementType.Node])
		assert(node1)
		UUID uuid = node1.getUuid()
		assert (uuid)
		assert (node1.rid)

		def node2 = SessionRO.node(uuid)
		assert (node2.properties.get("label")=="NodeA");
	}

	@Test(groups = 'memory')
	public void testInstantiation() throws MemoryNotInitializedException {
		def session = Session.GetSession(memory_user, memory_passwd, true, false);
		assert (session);

		def nodeA = session.createElement(elementType: Element.ElementType.Node, label: "NodeA" )
		assert (nodeA.properties["label"]=="Node A")
		assert (nodeA.properties["elementType"]==Node)

		def nodeB = session.createElement(elementType: Node, labe: "NodeB")
		assert (nodeB.properties["label"]=="Node B")
		assert (nodeB.properties["elementType"]==Node)
	}
	
	@Test(groups = ["memory", "nodes"])
	def testNodeCreation() {
		def nodeC = SessionRW1.createElement(elementType: Element.ElementType.Node, label: "NodeC" )

		with (nodeC) {
			assert (properties["label"]=="Node C")
			assert (properties["elementType"]==Node)
			assert (properties["mtime"] == properties["ctime"])
			assert (properties["mtime"] == properties["atime"])
			assert (properties["mtime"])
			assert (properties["rid"] == null)
			assert (properties["uuid"])
			assert (properties["elementType"] == Node)


		}
	}

}
