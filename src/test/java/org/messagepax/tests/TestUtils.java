package org.messagepax.tests;

import org.messagepax.Utils;

import junit.framework.TestCase;

public class TestUtils extends TestCase {

	public void testHexDehecx() {
		assertEquals("1234", Utils.hex(Utils.dehex("1234")));
		assertEquals("00", Utils.hex(Utils.dehex("00")));
		assertEquals("FF", Utils.hex(Utils.dehex("FF")));
		assertEquals("FFFF", Utils.hex(Utils.dehex("FFFF")));
		assertEquals("FFFFFFFF", Utils.hex(Utils.dehex("FFFFFFFF")));
	}

}
