package org.compendiumng.cngx.tests;

import groovy.util.logging.Slf4j;
import org.testng.annotations.Test;

@Slf4j
public class TestDummyGroovy {

	@Test(groups = ["dummy", "groovy"])
	public void testAdding() {
        int a = 3;
        int b = -3;
        int c = a + b;
        assert (c == 0);
	}
}