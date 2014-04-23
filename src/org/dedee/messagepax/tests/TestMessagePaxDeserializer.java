package org.dedee.messagepax.tests;

import java.io.IOException;

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

	public void testBoolean() throws IOException {
		d.reset("C0");
		assertNull(d.readBoolean());
		d.reset("C2");
		assertFalse(d.readBoolean());
		d.reset("C3");
		assertTrue(d.readBoolean());
	}

	public void testLong() throws IOException {
		d.reset("C0");
		assertNull(d.readLong());

		d.reset("01");
		assertEquals(Long.valueOf(1), d.readLong());

		d.reset("D280000000");
		assertEquals(Long.valueOf(-0x80000000L), d.readLong());

		d.reset("D3FFFFFFFF7FFFFFFF");
		assertEquals(Long.valueOf(-0x80000001L), d.readLong());

		// d.reset("D280000000");
		// assertEquals(Long.valueOf(0x80000000L), d.readLong());

		d.reset("CF0000008000000000");
		assertEquals(Long.valueOf(0x8000000000L), d.readLong());
	}

	public void testFloat() throws IOException {
		d.reset("C0");
		assertNull(d.readFloat());
		d.reset("CA3F800000");
		assertTrue(Math.abs(1.0d - d.readFloat()) < 0.0001);
		d.reset("CABF800000");
		assertTrue(Math.abs(-1.0d - d.readFloat()) < 0.0001);
	}

	public void testDouble() throws IOException {
		d.reset("C0");
		assertNull(d.readDouble());
		d.reset("CB3FF0000000000000");
		assertTrue(Math.abs(1.0d - d.readDouble()) < 0.0001);
		d.reset("CBBFF0000000000000");
		assertTrue(Math.abs(-1.0d - d.readDouble()) < 0.0001);
	}
}
