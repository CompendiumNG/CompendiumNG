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
package api.memory.elements;

import java.util.Map;
import java.util.UUID;

public abstract class ISession {

	// TAGS

	/**
	 *
	 * @param tag - tag label
	 * @param properties set of properties that should be assigned to this node
	 * @return created persisted node
	 */
	public abstract String createTag(String tag, Map<String, Value> properties);

	/**
	 *
	 * @param tag tag label
	 * @return   label of deleted tag
	 */
	public abstract String deleteTag(String tag);

	/**
	 *
	 * @param tag tag label
	 * @param properties  that will update current properties
	 * @return updated persisted node
	 */
	public abstract String modifyTag(String tag, Map<String, String> properties);

	/**
	 *
	 * @param tag
	 * @return  tag object specified by label
	 */
	public abstract ITag getTag(String tag);


	// NODES

	/**
	 *
	 * @return created and not peristed empty node
	 */
	public abstract INode createNode();

	public abstract INode createNode(Map<String, Value> properties);

	/**
	 *
	 * @param node to be deleted
	 */

	public abstract void deleteNode(INode node);

	/**
	 *
	 * @param properties
	 * @return
	 */
	public abstract INode modifyNode(Map<String, Value> properties);

	/**
	 *
	 * @param criteria search criteria
	 * @return
	 */
	public abstract INode[] searchNodes(String criteria);

	/**
	 *
	 * @param rid unique record id of searched node
	 * @return found node
	 */
	public abstract INode getNode(String rid);

	/**
	 *
	 * @param uuid universally unique id of searched node
	 * @return found node
	 */
	public abstract INode getNode(UUID uuid);

	// LINKS
	public abstract ILink createLink();
	public abstract ILink createLink(Map<String, Value> properties);

	//operations
	public abstract ILink[] getOutLinks(INode node);
	public abstract ILink[] getInLinks(INode node);
	public abstract ILink[] getAllLinks(INode node);

	public abstract void connect(INode from, INode to, ILink link);
	public abstract void disconnect(INode from, INode to, ILink link);
	public abstract void disconnectInLinks(INode node);
	public abstract void disconnectOutLinks(INode node);
	public abstract void disconnectAlltLinks(INode node);


}
