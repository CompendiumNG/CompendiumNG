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

package api.memory

abstract class IElement {
	
	enum ElementType { Element(0), Node(1),Link(2),Tag(3), Alias(4) };
	
	def elementType = ElementType.Element;
	def rid;
	def uuid;
	def atime;
	def mtime;
	def ctime;
	def properties;

	
	IElement (elementType, properties) {
		uuid = UUID.randomUUID();
		ctime = ctime ?: System.currentTimeMillis
		mtime = mtime ?: ctime
		atime = atime ?: ctime
	}
}





