package org.compendiumng.cngx.memory;

import java.math.BigDecimal;

/**
 * Created by maiklos on 5.10.14.
 */
public interface IMemory {

	/*
	 * gettters
	 */
	boolean getSystemAttributeAsBoolean(String attribute);

	String getSystemAttributeAsString(String attribute);

	BigDecimal getSystemAttributeAsNumber(String attribute);

	/*
	 * settters
	 */


	/**
	 * create memory - fail if it already exist
	 */
	public void create();

	/**
	 * open existing memory or create it
	 */
	public void open();

	/**
	 * close opened memory or silently fail if it is already closed
	 */
	public void close();

	/**
	 * completely wipe memory including asociated filesystem artifacts
	 */
	public void destroy();

	/**
	 * @return true if memory was created as a result of open() or as a result of create() and it hasn't been initialized
	 */
	public boolean isNew();

	/**
	 * @return true if memory is closed from current session
	 */
	public boolean isClosed();

	/**
	 * @return true if memory is already initialized
	 */
	public boolean isInitialized();

	/**
	 * @return true if it is possible to open memory concurrently
	 */
	public boolean isConcurrencySupported();


}