package api.memory;

import api.memory.exceptions.StoreNotAvailableException;

public abstract class Store {
	private String name = null;
	private String location = null;


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}


	abstract Store open(String url, String username, String password, boolean exclusive, boolean readonly) throws StoreNotAvailableException;

	abstract void close();
}
