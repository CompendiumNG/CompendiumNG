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

package org.compendiumng.cngx.memory


import java.util.UUID
import System.currentTimeMillis


class Element (
                props:Map[String,Any] = Map(
                                          "elementType" -> "Element",
                                          "uuid" -> UUID.randomUUID(), // temporary until the node is persisted and store id is assigned
                                          "id" -> UUID.randomUUID(),
                                          "ctime" -> currentTimeMillis(),
                                          "mtime" -> currentTimeMillis(),
                                          "atime" -> currentTimeMillis()
                                          )
                ) {


  val properties = props

  /**
   * persist Element into underlying database
   * @return uuid from underlying database
   */
  def save() = {
    //TODO: implement saving/commiting
    // blablablah
    properties.get("uuid")
  }

  def discard(): Boolean = {
    //TOOD: implement discarding of element
    // move the node to trashbin
    // mark all NodeLinks to this node as invalid
    false
  }

}