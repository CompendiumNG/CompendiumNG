package org.compendiumng.tests;

import org.compendiumng.tools.Utilities;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class DummyTest {
	
	private final Logger log = LoggerFactory.getLogger(DummyTest.class);

	@Test
	public void test() {
		log.info("running dummy test in {}", this.getClass().getName());
	}
	
	
	@Test(expected=NullPointerException.class)
	public void expectedFailure() {
		log.info("running dummy test in {}", this.getClass().getName());
	    Object o = null;
	    o.toString();
	}
	
	@Test
	public void copyfile1() throws Throwable {
		String prefix = "cng_test_file_";
		String suffix = "tmp";
		File f1 = File.createTempFile(prefix, suffix);
		File f2 = File.createTempFile(prefix, suffix);
		
		String s1 = f1.getAbsolutePath();
		String s2 = f2.getAbsolutePath();
		
		f2.delete();
		
		assert (f1.exists() && f1.canWrite() && !f2.exists());
		Utilities.CopyFile(s1, s2);
		assert (f1.exists() && f1.canWrite() && f2.exists() && f2.canWrite());
		
		f1.delete();
		f2.delete();
		assert (!f1.exists());
		assert (!f2.exists());
		
		
	}
}
