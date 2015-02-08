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
import org.testng.annotations.BeforeClass
import org.testng.annotations.Test

@Test(groups="groovy")
class TestStorageBasicGroovy {

	private static final memory_spec = "memory:testdata1"
	private static final memory_user = "admin"
	private static final memory_passwd = "admin"
	private static Session SessionRW1, SessionRW2, SessionRO
	private static final Map session_props_rw = [ user: memory_user,password: memory_passwd,connection: memory_spec,readonly: false, transactional: false ]
	private static final Map session_props_ro = [ user: memory_user,password: memory_passwd,connection: memory_spec,readonly: true, transactional: false ]

	@Test(groups = ["dummy", "groovy"])
	public void testAdding() {
		int a = 3;
		int b = -3;
		int c = a + b;
		assert (c == 0);
	}

	@BeforeClass
	def void testInitSession()  {
		assert (!SessionRW1)
		assert (!SessionRW2)
		assert (!SessionRO)
		
		SessionRW1 = Session.Create(session_props_rw)
		SessionRW2 = Session.Create(session_props_rw)
		SessionRO = Session.Create(session_props_ro)

		assert (SessionRW1 != null);
		assert (SessionRW2 != null);
		assert (SessionRO != null);
	}

	@Test(groups = 'memory')
	public void testTwoSessionInteraction() {
		final String label = "node1"

		def node_properties = [label: label, elementType: "Node"]
		
				
		Element node1 = SessionRW1.createElement(node_properties)

		def nodeByRID = SessionRO.getNodeByRID(node1.id)
		def nodeByUUID = SessionRO.getNodeByUUID(node1.uuid)

		assert (node1)
		assert (node1.uuid)
		assert (node1.id)
		assert (nodeByRID.uuid == nodeByUUID.uuid)
		assert (nodeByRID.id == nodeByUUID.id)
		assert (nodeByRID.properties["label"] == nodeByUUID.properties[label])
		assert (nodeByRID.properties["label"] == label);
	}

	@Test(groups = 'memory')
	public void testInstantiation() {
		final String labelA = "Node A"
		final String labelB = "Node B"

		
		def nodeA = SessionRW1.createElement([elementType: "Node", label: labelA])
		assert (nodeA.properties["label"]==labelA)

		def nodeB = SessionRW2.createElement([elementType: "Node", label: labelB])
		assert (nodeB.properties["label"]==labelB)
	}
	
	@Test(groups = ['memory', 'nodes'])
	def void testNodeCreation() {
		def Element nodeC = SessionRW1.createElement(elementType: "Node", properties: [label: "NodeC"])

		nodeC.with {
			assert (properties["label"]=="Node C")
			assert (properties["elementType"]=="Node")
			assert (properties["mtime"] == properties["ctime"])
			assert (properties["mtime"] == properties["atime"])
			assert (properties["mtime"])
			assert (properties["rid"])
			assert (properties["uuid"])
		}
	}


	@Test(groups = ['MassLoad'])
	def void testMassLoadNodes(){
		def node1, node2, tag

		0.upto(999) {
			node1 = SessionRW1.createElement([elementType: "Node", label: "node1_$it"])
			node2 = SessionRW1.createElement([elementType: "Node", label: "node2_$it"])
			def link = SessionRW1.createLink(node1, node2, [label: "link $node1.properties['label'] <-> $node2.properties['label']"])
		}

		1000.upto(1999) {
			SessionRW1.createElement([label: "tag_$it", "elementType": "Tag"])
		}
		
		2000.upto(2999) {
			SessionRW1.addDoc("SomeClass", [label: "doc_$it"])
		}
	}

}
