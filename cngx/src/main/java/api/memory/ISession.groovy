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

public abstract class ISession {
	
	protected IMemory memory;
	
		
	/**
	 * 
	 * @param set of properties like user, password, url, driver
	 * @return
	 */
	public abstract ISession Create(Map properties);
	
	/**
	 *
	 * @param properties
	 * @return Node with set of properties
	 */
	public abstract IElement createElement(Map<String, IValue> properties);

	/**
	 *
	 * @param node to be deleted
	 */
	public abstract void deleteElement(IElement node);
		

	/**
	 *
	 * @param properties to be set/updated
	 * @return updated element
	 */
	public abstract IElement modifyElement(Map<String, IValue> properties);

	/**
	 *
	 * @param criteria search criteria
	 * @return array of found elements
	 */
	public abstract IElement[] searchNodes(String criteria);

	/**
	 *
	 * @param rid unique record id of searched node
	 * @return found node
	 */
	public abstract IElement getNode(String rid);

	/**
	 *
	 * @param uuid universally unique id of searched node
	 * @return found node
	 */
	public abstract IElement getNode(UUID uuid);

	// LINKS
	/**
	 * 
	 * @param NodeA
	 * @param nodeB
	 * @param properties
	 * @return
	 */
	public abstract IElement createLink(IElement NodeA, IElement nodeB, Map<String, IValue> properties);
	
	/**
	 * 
	 * @param node
	 * @return
	 */
	public abstract IElement[] getOutLinks(IElement node);
	
	/**
	 * 
	 * @param node
	 * @return
	 */
	public abstract IElement[] getInLinks(IElement node);
	
	/**
	 * 
	 * @param node
	 * @return
	 */
	public abstract IElement[] getAllLinks(IElement node);
	
	/**
	 * 
	 * @param nodeA
	 * @param nodeB
	 * @param ILink
	 */
	public abstract void link(IElement nodeA, IElement nodeB, IElement ILink);
	
	/**
	 * 
	 * @param node
	 */
	public abstract void unlinkInLinks(IElement node);
	
	/**
	 * 
	 * @param node
	 */
	public abstract void unlinkOutLinks(IElement node);
	
	/**
	 * 
	 * @param node
	 */
	public abstract void unlinkAllLinks(IElement node);


}
