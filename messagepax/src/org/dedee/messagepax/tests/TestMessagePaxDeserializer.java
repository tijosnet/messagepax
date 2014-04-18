package org.dedee.messagepax.tests;

import junit.framework.TestCase;

import org.dedee.messagepax.MessagePaxDeserializer;

public class TestMessagePaxDeserializer extends TestCase {

	MessagePaxDeserializer d = new MessagePaxDeserializer(new byte[1024]);

	public void testNil() throws Exception {
		d.reset("C0");
		assertNull(d.readInteger());
	}

	public void testInteger7BitPositive() throws Exception {
		d.reset("05");
		assertEquals((Integer) 5, d.readInteger());
		d.reset("7F");
		assertEquals((Integer) 0x7F, d.readInteger());
	}

	public void testInteger5BitNegative() throws Exception {
		d.reset("E0FF");
		assertEquals((Integer) (-32), d.readInteger());
		assertEquals((Integer) (-1), d.readInteger());
	}

	public void testUnsignedInt8() throws Exception {
		d.reset("CCFF");
		assertEquals((Integer) 255, d.readInteger());
	}

	public void testSignedInt8() throws Exception {
		d.reset("D0FF");
		assertEquals((Integer) (-1), d.readInteger());
	}

	public void testUnsignedInt16() throws Exception {
		d.reset("CDFFFF");
		assertEquals((Integer) 65535, d.readInteger());
	}

	public void testSignedInt16() throws Exception {
		d.reset("D1FFFF");
		assertEquals((Integer) (-1), d.readInteger());
	}
}
