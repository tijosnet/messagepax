package org.messagepax.tests;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.messagepax.MessagePaxSerializer;
import org.messagepax.Utils;

public class TestMessagePaxSerializer extends TestCase {

	private byte[] buf = new byte[2048];
	MessagePaxSerializer s = new MessagePaxSerializer(buf);

	public void testNil() throws Exception {
		s.reset();
		s.writeNil();
		assertEquals("C0", s.toHexString());
	}

	public void testBoolean() throws Exception {
		s.reset();
		s.writeBoolean(null);
		s.writeBoolean(Boolean.FALSE);
		s.writeBoolean(Boolean.TRUE);
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

	public void testIntSmallPositiveBounds() throws Exception {
		s.reset();
		s.writeInteger(0);
		s.writeInteger(127);
		assertEquals("007F", s.toHexString());
	}

	public void testIntSmallNegativeBounds() throws Exception {
		s.reset();
		s.writeInteger(-32);
		s.writeInteger(-1);
		assertEquals("E0FF", s.toHexString());
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

	public void testLong() throws Exception {
		s.reset();
		s.writeLong(null);
		assertEquals("C0", s.toHexString());
		s.reset();
		s.writeLong(1);
		assertEquals("01", s.toHexString());
		s.reset();
		s.writeLong(Long.valueOf(-0x80000000L));
		assertEquals("D280000000", s.toHexString());
		s.reset();
		s.writeLong(Long.valueOf(-0x80000001L));
		assertEquals("D3FFFFFFFF7FFFFFFF", s.toHexString());
		s.reset();
		s.writeLong(Long.valueOf(0x80000000L));
		assertEquals("D280000000", s.toHexString());
		s.reset();
		s.writeLong(Long.valueOf(0x8000000000L));
		assertEquals("CF0000008000000000", s.toHexString());
	}

	public void testByteArray() throws Exception {
		s.reset();
		s.writeByteArray(null, 0, 0);
		assertEquals("C0", s.toHexString());

		s.reset();
		s.writeByteArray(new byte[] { 1, 2, 3, 4, 5 }, 0, 5);
		assertEquals("C4050102030405", s.toHexString());

		s.reset();
		byte[] d = new byte[40];
		for (int i = 0; i < d.length; i++)
			d[i] = (byte) i;
		s.writeByteArray(d, 0, d.length);
		assertEquals(
				"C428000102030405060708090A0B0C0D0E0F101112131415161718191A1B1C1D1E1F2021222324252627",
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
		s.writeStringList(list);
		assertEquals("C0", s.toHexString());
		list = new ArrayList<String>();
		s.writeStringList(list);
		assertEquals("C090", s.toHexString());
		list.add("A");
		list.add("B");
		s.writeStringList(list);
		assertEquals("C09092A141A142", s.toHexString());
		for (int i = 0; i < 40; i++) {
			list.add("C");
		}
		s.writeStringList(list);
		assertEquals(
				"C09092A141A142DC002AA141A142A143A143A143A143A143A143A143A143A143A143A143A143A143A143A143A143A143A143A143A143A143A143A143A143A143A143A143A143A143A143A143A143A143A143A143A143A143A143A143A143",
				s.toHexString());
	}

	public void testAnotherList() throws Exception {
		s.reset();
		List<Integer> list = new ArrayList<Integer>();
		list.add(1);
		list.add(2);
		list.add(3);
		list.add(4);
		s.writeListBegin(list.size());
		for (Integer i : list) {
			s.writeInteger(i);
		}
		assertEquals("9401020304", s.toHexString());
	}

	public void testAnotherMap() throws Exception {
		s.reset();
		Map<Integer, String> map = new LinkedHashMap<Integer, String>();
		map.put(1, "1");
		map.put(2, "2");
		map.put(3, "3");
		map.put(4, "4");
		s.writeMapBegin(4);
		for (Integer key : map.keySet()) {
			String val = map.get(key);
			s.writeInteger(key);
			s.writeString(val);
		}
		assertEquals("8401A13102A13203A13304A134", s.toHexString());
	}

	public void testFloat() throws IOException {
		s.reset();
		s.writeFloat(null);
		assertEquals("C0", s.toHexString());
		s.reset();
		s.writeFloat(Float.valueOf(1.0f));
		assertEquals("CA3F800000", s.toHexString());
		s.reset();
		s.writeFloat(Float.valueOf(-1.0f));
		assertEquals("CABF800000", s.toHexString());
	}

	public void testDouble() throws IOException {
		s.reset();
		s.writeDouble(null);
		assertEquals("C0", s.toHexString());
		s.reset();
		s.writeDouble(Double.valueOf(1.0d));
		assertEquals("CB3FF0000000000000", s.toHexString());
		s.reset();
		s.writeDouble(Double.valueOf(-1.0d));
		assertEquals("CBBFF0000000000000", s.toHexString());
	}

	public void testBigInteger() throws IOException {
		s.reset();
		s.writeBigInteger(new BigInteger("0"));
		assertEquals("00", s.toHexString());

		s.reset();
		s.writeBigInteger(new BigInteger("4294967296")); // 0xFFFFFFFF + 1
		assertEquals("CF0000000100000000", s.toHexString());

		s.reset();
		s.writeBigInteger(new BigInteger("9223372036854775807")); // 7FFFFFFFFFFFFFFF
		assertEquals("CF7FFFFFFFFFFFFFFF", s.toHexString());

		s.reset();
		s.writeBigInteger(null);
		assertEquals("C0", s.toHexString());
	}

	public void testSerializeDifferentIntegers() throws IOException {
		s.reset();
		for (int i = 0; i < TestMessagePaxDeserializer.INTEGERS.length; i++) {
			int j = TestMessagePaxDeserializer.INTEGERS[i];
			s.writeInteger(j);
		}
		assertEquals(
				"D280000000CE7FFFFFFFFFFEFCF8F0E0D0C0D080D1FF00D1FE000001020408102040CC80CD0100CD0200CD0400",
				s.toHexString());
	}

	public void testDeserializeDifferentStrings() throws IOException {
		s.reset();
		for (int i = 0; i < TestMessagePaxDeserializer.STRINGS.length; i++) {
			String string = TestMessagePaxDeserializer.STRINGS[i];
			s.writeString(string);
		}
		assertEquals(
				"AB48656C6C6F20776F726C64A131A0DA002B54686520717569636B2062726F776E20666F78206A756D7073206F76657220746865206C617A7920646F67C0",
				s.toHexString());
	}

	public void testStringMap() throws IOException {
		Map<String, String> map = new LinkedHashMap<String, String>();
		map.put("1", "2");
		s.reset();
		s.writeStringMap(map);
		assertEquals("81A131A132", s.toHexString());
	}

	public void testWriteString() throws Exception {
		s.reset();
		s.writeString("0");
		assertEquals("A130", s.toHexString());

		s.reset();
		s.writeString("01234567890123456789");
		assertEquals("B43031323334353637383930313233343536373839",
				s.toHexString());
	}

	public void testExtData() throws Exception {
		s.reset();
		s.writeExtData(1, null);
		assertEquals("C0", s.toHexString());

		s.reset();
		s.writeExtData(1, Utils.dehex("01"));
		assertEquals("D40101", s.toHexString());

		s.reset();
		s.writeExtData(1, Utils.dehex("0102"));
		assertEquals("D5010102", s.toHexString());

		s.reset();
		s.writeExtData(1, Utils.dehex("01020304"));
		assertEquals("D60101020304", s.toHexString());

		s.reset();
		s.writeExtData(1, Utils.dehex("0102030405060708"));
		assertEquals("D7010102030405060708", s.toHexString());

		s.reset();
		s.writeExtData(1, Utils.dehex("01020304050607080102030405060708"));
		assertEquals("D80101020304050607080102030405060708", s.toHexString());

		s.reset();
		s.writeExtData(1, Utils.dehex("010203"));
		assertEquals("C70301010203", s.toHexString());

		byte b[] = new byte[255];
		s.reset();
		s.writeExtData(1, b);
		assertEquals(0xc7, s.getBuffer()[0] & 0xff);
		assertEquals(255, s.getBuffer()[1] & 0xff);
		assertEquals(1, s.getBuffer()[2] & 0xff);
		for (int i = 0; i < 255; i++) {
			assertEquals(0, s.getBuffer()[3 + i] & 0xff);
		}

		b = new byte[256];
		s.reset();
		s.writeExtData(1, b);
		assertEquals(0xc8, s.getBuffer()[0] & 0xff);
		assertEquals(0x01, s.getBuffer()[1] & 0xff);
		assertEquals(0x00, s.getBuffer()[2] & 0xff);
		assertEquals(0x01, s.getBuffer()[3] & 0xff);
		for (int i = 0; i < 256; i++) {
			assertEquals(0, s.getBuffer()[4 + i] & 0xff);
		}
	}
}
