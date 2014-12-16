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

package api.memory.elements;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public abstract class IElement {
	protected String rid = null;
	protected UUID uuid = null;
	protected String label = null;
	protected Date atime = null;
	protected Date mtime = null;
	protected Date ctime = null;

	protected Map<String, Value> properties = new HashMap();


	public String getRid() {
		return rid;
	}

	public void setRid(String rid) {
		this.rid = rid;
	}

	public UUID getUuid() {
		return uuid;
	}

	public void setUuid(UUID uuid) {
		this.uuid = uuid;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public Date getAtime() {
		return atime;
	}

	public void setAtime(Date atime) {
		this.atime = atime;
	}

	public Date getMtime() {
		return mtime;
	}

	public void setMtime(Date mtime) {
		this.mtime = mtime;
	}

	public Date getCtime() {
		return ctime;
	}

	public void setCtime(Date ctime) {
		this.ctime = ctime;
	}

	public Map<String, Value> getProperties() {
		return properties;
	}

	public void setProperties(Map<String, Value> properties) {
		this.properties = properties;
	}

	public abstract INode setCustomProperties(Map<String, Value> properties);

	public abstract Map<String, Value> getCustomProperties();


}
