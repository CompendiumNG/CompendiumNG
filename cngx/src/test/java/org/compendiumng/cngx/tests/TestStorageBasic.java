package org.compendiumng.cngx.tests;

import org.testng.annotations.Test;
import org.testng.Assert;
import org.testng.TestNGUtils;
import org.testng.log4testng.Logger;

import java.net.URL;

import org.compendiumng.cngx.memory.IMemory;

import  org.compendiumng.cngx.MainFactory.*;

import static org.compendiumng.cngx.MainFactory.GetMemory;


public class TestStorageBasic {
	private static final Logger Log = Logger.getLogger(TestDummy.class);

	@Test(groups = "memory") public void testEmptyMemoryInstantiation () {
		org.compendiumng.cngx.memory.IMemory mem = GetMemory(); // this should never fail
	}

	@Test(groups = "memory") public void testInstantiation () {
		org.compendiumng.cngx.memory.IMemory mem = null;
		mem = GetMemory("thisfiledoesntexist");
		assert(mem.getSystemAttributeAsBoolean("isnew")==true);
		assert(mem.isNew());

		mem.close();
		mem = GetMemory("thisfiledoesntexist"); // now it exists
		assert(mem.getSystemAttributeAsBoolean("isnew")==false);
		assert(!mem.isNew());

		mem.close();
		assert (mem.getSystemAttributeAsBoolean("isclosed")==true);
		assert (mem.isClosed());

		mem.destroy();
		mem = GetMemory("thisfiledoesntexist");
		assert(mem.getSystemAttributeAsBoolean("isnew")==true);
		assert(mem.isNew());
		mem.close();
		mem.destroy();

	}

	@Test(groups = "memory") public void testDemoFileMemoryInstantiation () { // need data file for this
		IMemory mem; // this may fail if data file exist but can't be opened
		mem = GetMemory("");
	}

}
