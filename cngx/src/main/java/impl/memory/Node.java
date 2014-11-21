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

package impl.memory;

import api.memory.elements.INode;
import api.memory.elements.Value;
import api.memory.exceptions.NodeCollisionException;

import java.util.Map;

public class Node extends INode {
	@Override
	public INode Node(String icon, Map<String, Value> custom_properties) throws NodeCollisionException {
		return null;
	}

	@Override
	public INode Node(Map<String, Value> custom_properties) throws NodeCollisionException {
		return null;
	}

	@Override
	public INode Node(String icon) throws NodeCollisionException {
		return null;
	}

	@Override
	public INode Node() throws NodeCollisionException {
		return null;
	}

	@Override
	public INode setCustomProperties(Map<String, Value> properties) {
		return null;
	}

	@Override
	public Map<String, Value> getCustomProperties() {
		return null;
	}
}
