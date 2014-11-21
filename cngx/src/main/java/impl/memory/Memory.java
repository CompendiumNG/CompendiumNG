package impl.memory;

import api.memory.IMemory;
import api.memory.Store;
import api.memory.elements.ISession;
import api.memory.exceptions.SessionNotAvailableException;

public class Memory implements IMemory {

	@Override
	public ISession GetSession(String store_url, String username, String password, boolean writeable, boolean exclusive) throws SessionNotAvailableException {
		return null;
	}

	@Override
	public ISession GetSession() throws SessionNotAvailableException {
		return null;
	}

	@Override
	public Store openStore(String spec, boolean exclusive, boolean readonly) {
		return null;
	}

	@Override
	public Store closeStore(String spec) {
		return null;
	}

	@Override
	public boolean getSystemAttributeAsBoolean(String attribute_name) {
		return false;
	}

	@Override
	public boolean getSystemAttributeAsString(String attribute_name) {
		return false;
	}

	@Override
	public boolean getSystemAttributeAsInteger(String attribute_name) {
		return false;
	}

	@Override
	public boolean getSystemAttributeAsDouble(String attribute_name) {
		return false;
	}
}
