package org.dedee.messagepax.tests;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.dedee.messagepax.MessagePaxSerializer;

public class TestMessagePaxSerializer extends TestCase {

	private byte[] buf = new byte[256];
	MessagePaxSerializer s = new MessagePaxSerializer(buf);

	public void testNil() throws Exception {
		s.reset();
		s.writeNil();
		assertEquals("C0", s.toHexString());
	}

	public void testBoolean() throws Exception {
		s.reset();
		s.writeBoolean(null);
		s.writeBoolean(false);
		s.writeBoolean(true);
		assertEquals("C0C2C3", s.toHexString());
	}

	public void testByte() throws Exception {
		s.reset();
		s.writeInteger(null);
		s.writeInteger(0);
		s.writeInteger(1);
		s.writeInteger(-32);
		s.writeInteger(-64);
		assertEquals("C00001E0D0C0", s.toHexString());
	}

	public void testPositiveIntSmall() throws Exception {
		s.reset();
		s.writeInteger(null);
		s.writeInteger(0);
		s.writeInteger(1);
		s.writeInteger(0x7f);
		assertEquals("C000017F", s.toHexString());
	}

	public void testPositiveIntFF() throws Exception {
		s.reset();
		s.writeInteger(0xff);
		assertEquals("CCFF", s.toHexString());
	}

	public void testPositiveInt16() throws Exception {
		s.reset();
		s.writeInteger(0xffff);
		s.writeInteger(0xaaaa);
		assertEquals("CDFFFFCDAAAA", s.toHexString());
	}

	public void testPositiveInt32() throws Exception {
		s.reset();
		s.writeInteger(0x7fffffff);
		s.writeInteger(0xaaaaaaaa);
		assertEquals("CE7FFFFFFFD2AAAAAAAA", s.toHexString());
	}

	public void testNegativeIntSmall() throws Exception {
		s.reset();
		s.writeInteger(-1);
		s.writeInteger(-31);
		assertEquals("FFE1", s.toHexString());
	}

	public void testByteArray() throws Exception {
		s.reset();
		s.writeByteArray(null, 0, 0);
		s.writeByteArray(new byte[] { 1, 2, 3, 4, 5 }, 0, 5);
		byte[] d = new byte[40];
		for (int i = 0; i < d.length; i++)
			d[i] = (byte) i;
		s.writeByteArray(d, 0, d.length);
		assertEquals(
				"C0A50102030405DA0028000102030405060708090A0B0C0D0E0F101112131415161718191A1B1C1D1E1F2021222324252627",
				s.toHexString());
	}

	public void testString() throws Exception {
		s.reset();
		s.writeString(null);
		s.writeString("Hello World");
		s.writeString("1234567890123456789012345678901234567890");
		assertEquals(
				"C0AB48656C6C6F20576F726C64DA002831323334353637383930313233343536373839303132333435363738393031323334353637383930",
				s.toHexString());
	}

	public void testListString() throws Exception {
		s.reset();
		List<String> list = null;
		s.writeList(list);
		assertEquals("C0", s.toHexString());
		list = new ArrayList<>();
		s.writeList(list);
		assertEquals("C090", s.toHexString());
		list.add("A");
		list.add("B");
		s.writeList(list);
		assertEquals("C09092A141A142", s.toHexString());
		for (int i = 0; i < 40; i++) {
			list.add("C");
		}
		s.writeList(list);
		assertEquals(
				"C09092A141A142DC002AA141A142A143A143A143A143A143A143A143A143A143A143A143A143A143A143A143A143A143A143A143A143A143A143A143A143A143A143A143A143A143A143A143A143A143A143A143A143A143A143A143A143",
				s.toHexString());
	}

	public void testLong() throws Exception {
		s.reset();
		s.writeLong(null);
		assertEquals("C0", s.toHexString());
		s.reset();
		s.writeLong(1);
		assertEquals("01", s.toHexString());
		s.reset();
		s.writeLong(-0x80000000L);
		assertEquals("D280000000", s.toHexString());
		s.reset();
		s.writeLong(-0x80000001L);
		assertEquals("D3FFFFFFFF7FFFFFFF", s.toHexString());
		s.reset();
		s.writeLong(0x80000000L);
		assertEquals("D280000000", s.toHexString());
		s.reset();
		s.writeLong(0x8000000000L);
		assertEquals("CF0000008000000000", s.toHexString());
	}
}
