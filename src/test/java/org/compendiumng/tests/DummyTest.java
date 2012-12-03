package org.compendiumng.tests;

import static org.junit.Assert.*;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DummyTest {
	
	final Logger log = LoggerFactory.getLogger(DummyTest.class);

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
}
