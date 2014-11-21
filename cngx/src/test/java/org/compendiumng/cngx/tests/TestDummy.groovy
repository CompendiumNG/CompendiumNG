package org.compendiumng.cngx.tests;

import org.testng.annotations.Test;
import org.testng.log4testng.Logger;


public class TestDummy {
	private static final Logger Log = Logger.getLogger(TestDummy.class);


	@Test(groups = "dummy")

	public void testAdding() {
		System.out.println("running test testAdding in " + this.getClass().getName());
        int a = 3;
        int b = -3;
        int c = a + b;
        assert (c == 0);
}

	@Test(expectedExceptions = NullPointerException.class)	public void expectedNullPointerException() {
		System.out.println("running test expectedNPE in " + this.getClass().getName());
		Object o = null;
		System.err.println("reached the point before the exception... this should happen");
		o.toString();
		System.err.println("reached the point behind the exception... this shouldn't happen");
	}

}