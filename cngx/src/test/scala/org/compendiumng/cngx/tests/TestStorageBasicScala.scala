/*
 *
 * 		Copyright [2015] [Michal Stekrt]
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

import java.util.UUID

import org.testng.Assert._


import org.testng.annotations.{BeforeClass, Test}
import org.compendiumng.cngx.memory.Session
import org.compendiumng.cngx.memory.Element


object TestStorageBasicScala {
  val memory_spec = "memory:testdata1"
  val memory_user = "admin"
  val memory_passwd = "admin"
  var SessionRW1: Session = null
  var SessionRW2: Session = null
  var SessionRO: Session = null
  val session_props_rw = Map("user" -> memory_user, "password" -> memory_passwd, "connection" -> memory_spec, "readonly" -> false )
  val session_props_ro = Map("user" -> memory_user, "password" -> memory_passwd, "connection" -> memory_spec, "readonly" -> true )
}



class TestStorageBasicScala {

  @Test
  def testAdding()= {
    val a = 3
    val b = -3
    val c = a + b
    assertTrue (c == 0)
  }

  @BeforeClass
  def testInitSession():Unit = {
    assertNull (TestStorageBasicScala.SessionRW1)
    assertNull (TestStorageBasicScala.SessionRW2)
    assertNull (TestStorageBasicScala.SessionRO)

    TestStorageBasicScala.SessionRW1 = Session.create(TestStorageBasicScala.session_props_rw)
    TestStorageBasicScala.SessionRW2 = Session.create(TestStorageBasicScala.session_props_rw)
    TestStorageBasicScala.SessionRO = Session.create(TestStorageBasicScala.session_props_ro)

    assertNotNull (TestStorageBasicScala.SessionRW1)
    assertNotNull (TestStorageBasicScala.SessionRW2)
    assertNotNull (TestStorageBasicScala.SessionRO)
  }


  @Test
  def testTwoSessionInteraction(): Unit = {
    val label= "node1"

    def node_properties = Map["label" -> label, "elementType" -> "Node"]()


    val node1 = TestStorageBasicScala.SessionRW1.createElement(node_properties, "Node")

    def nodeByRID = TestStorageBasicScala.SessionRO.getNode(node1.properties("id").asInstanceOf[String])
    def nodeByUUID = TestStorageBasicScala.SessionRO.getNode(node1.properties("uuid").asInstanceOf[UUID])

    assertNotNull (node1)
    assertNotNull (node1.properties("uuid"))
    assertNotNull (node1.properties("id"))
    assertNotNull (nodeByRID.properties("uuid") == nodeByUUID.properties("uuid"))
    assertNotNull (nodeByRID.properties("id") == nodeByUUID.properties("id"))
    assertNotNull (nodeByRID.properties("label") == nodeByUUID.properties(label))
    assertNotNull (nodeByRID.properties("label") == label)
  }

  @Test
  def testInstantiation(): Unit = {
    val labelA = "Node A"
    val labelB = "Node B"

    def nodeA = TestStorageBasicScala.SessionRW1.createElement(Map("elementType" -> "Node", "label" -> labelA))
    assertEquals (nodeA.properties("label"), labelA)

    def nodeB = TestStorageBasicScala.SessionRW2.createElement(Map("elementType" -> "Node", "label" -> labelB))
    assertEquals(nodeB.properties("label"), labelB)

  }

  @Test
  def testNodeCreation(): Unit = {
    val label = "Node C"
    def nodeC = TestStorageBasicScala.SessionRW1.createElement(Map("label" -> label))

    assertEquals (nodeC.properties("label"), label)
    assertEquals (nodeC.properties("elementType"), "Node")
    assertEquals (nodeC.properties("mtime"),nodeC.properties("ctime"))
    assertEquals (nodeC.properties("mtime"),nodeC.properties("atime"))
    assertNotNull (nodeC.properties("mtime"))
    assertNotNull (nodeC.properties("rid"))
    assertNotNull (nodeC.properties("uuid"))

    Thread.sleep(2)
    val label2 = "Node CC"
    TestStorageBasicScala.SessionRW1.updateElementProperties(element = nodeC, Map("label" -> label2))
    assertEquals (nodeC.properties("label"), label2)

    assertNotEquals (nodeC.properties("mtime"),nodeC.properties("ctime"))
    assertEquals (nodeC.properties("mtime"),nodeC.properties("atime"))

  }


  @Test
  def testMassLoadNodes(): Unit ={

    for (a <- 0 to 999 ) {
      var node1 = TestStorageBasicScala.SessionRW1.createElement(Map("elementType" -> "Node", "label" -> "node1_"))
      var node2 = TestStorageBasicScala.SessionRW1.createElement(Map("elementType" -> "Node", "label" -> "node2_"))
      var link = TestStorageBasicScala.SessionRW1.createLink(node1, node2, Map("label" -> "link"))
    }

    for (a <- 1000 to 1999) {
      TestStorageBasicScala.SessionRW1.createElement(Map("label" -> "tag_$it", "elementType": "Tag"))
    }

    for (a <- 2000 to 2999) {
      TestStorageBasicScala.SessionRW1.addDoc("SomeClass", Map("label" -> "doc"))
    }
  }

}
