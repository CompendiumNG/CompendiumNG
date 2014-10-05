package org.compendiumng.cngx.memory;

import java.math.BigDecimal;

/**
 * Created by maiklos on 5.10.14.
 */
public class Memory implements IMemory {

	@Override
	public boolean getSystemAttributeAsBoolean(String attribute) {
		return false;
	}

	@Override
	public String getSystemAttributeAsString(String attribute) {
		return null;
	}

	@Override
	public BigDecimal getSystemAttributeAsNumber(String attribute) {
		return null;
	}

	@Override
	public void create() {

	}

	@Override
	public void destroy() {

	}

	@Override
	public void close() {

	}

	@Override
	public void open() {

	}

	@Override
	public boolean isNew() {
		return false;
	}

	@Override
	public boolean isClosed() {
		return false;
	}

	@Override
	public boolean isInitialized() {
		return false;
	}

	@Override
	public boolean isConcurrencySupported() {
		return false;
	}
}
