package org.messagepax.tests;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.messagepax.MessagePaxDeserializer;
import org.messagepax.Utils;

public class TestMessagePaxDeserializer extends TestCase {

	protected static int[] INTEGERS = new int[] { Integer.MIN_VALUE,
			Integer.MAX_VALUE, -1, -2, -4, -8, -16, -32, -64, -128, -256, -512,
			0, 1, 2, 4, 8, 16, 32, 64, 128, 256, 512, 1024 };

	protected static String[] STRINGS = new String[] { "Hello world", "1", "",
			"The quick brown fox jumps over the lazy dog", null };

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

	public void testIntegerFail() {
		d.reset("CF0102");
		try {
			d.readInteger();
			fail("IOException expected");
		} catch (IOException e) {
			// OK
		}
	}

	public void testBoolean() throws IOException {
		d.reset("C0");
		assertNull(d.readBoolean());
		d.reset("C2");
		assertFalse(d.readBoolean());
		d.reset("C3");
		assertTrue(d.readBoolean());
		d.reset("C4");
		try {
			d.readBoolean();
			fail("IOException expected");
		} catch (IOException e) {
			// OK
		}
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

	public void testBigInteger() throws IOException {
		d.reset("00");
		assertEquals(new BigInteger("0"), d.readBigInteger());
		d.reset("CF0000000100000000");
		assertEquals(new BigInteger("4294967296"), d.readBigInteger());
		d.reset("CF7FFFFFFFFFFFFFFF");
		assertEquals(new BigInteger("9223372036854775807"), d.readBigInteger());
		d.reset("C0");
		assertNull(d.readBigInteger());
	}

	public void testDeserializeDifferentIntegers() throws IOException {
		d.reset("D280000000CE7FFFFFFFFFFEFCF8F0E0D0C0D080D1FF00D1FE000001020408102040CC80CD0100CD0200CD0400");
		for (int i = 0; i < INTEGERS.length; i++) {
			int j = INTEGERS[i];
			assertEquals(j, d.readInteger().intValue());
		}
	}

	public void testDeserializeDifferentStrings() throws IOException {
		d.reset("AB48656C6C6F20776F726C64A131A0DA002B54686520717569636B2062726F776E20666F78206A756D7073206F76657220746865206C617A7920646F67C0");
		for (int i = 0; i < STRINGS.length; i++) {
			String s = STRINGS[i];
			assertEquals(s, d.readString());
		}
	}

	public void testReadString() throws IOException {
		d.reset("A130");
		assertEquals("0", d.readString());
		d.reset("DA000130");
		assertEquals("0", d.readString());
		d.reset("DB0000000130");
		assertEquals("0", d.readString());
	}

	public void testReadByteArray() throws IOException {
		d.reset("C401FF");
		assertEquals("FF", Utils.hex(d.readByteArray()));
		d.reset("C50001FF");
		assertEquals("FF", Utils.hex(d.readByteArray()));
		d.reset("C600000001FF");
		assertEquals("FF", Utils.hex(d.readByteArray()));
	}

	public void testReadListBegin() throws IOException {
		d.reset("920102");
		int len = d.readListBegin().intValue();
		assertEquals(2, len);
		assertEquals(1, d.readInteger().intValue());
		assertEquals(2, d.readInteger().intValue());

		d.reset("DC00020102");
		len = d.readListBegin().intValue();
		assertEquals(2, len);
		assertEquals(1, d.readInteger().intValue());
		assertEquals(2, d.readInteger().intValue());

		d.reset("DD000000020102");
		len = d.readListBegin().intValue();
		assertEquals(2, len);
		assertEquals(1, d.readInteger().intValue());
		assertEquals(2, d.readInteger().intValue());
	}

	public void testReadMapBegin() throws IOException {
		d.reset("810102");
		int len = d.readMapBegin().intValue();
		assertEquals(1, len);
		assertEquals(1, d.readInteger().intValue());
		assertEquals(2, d.readInteger().intValue());

		d.reset("DE00010102");
		len = d.readMapBegin().intValue();
		assertEquals(1, len);
		assertEquals(1, d.readInteger().intValue());
		assertEquals(2, d.readInteger().intValue());

		d.reset("DF000000010102");
		len = d.readMapBegin().intValue();
		assertEquals(1, len);
		assertEquals(1, d.readInteger().intValue());
		assertEquals(2, d.readInteger().intValue());
	}

	public void testReadStringList() throws IOException {
		d.reset("91A130");
		List<String> list = d.readStringList();
		assertEquals(1, list.size());
		assertEquals("0", list.get(0));
	}

	public void testReadStringMap() throws IOException {
		d.reset("81A130A131");
		Map<String, String> map = d.readStringMap();
		assertEquals(1, map.size());
		assertEquals("1", map.get("0"));
	}
}
