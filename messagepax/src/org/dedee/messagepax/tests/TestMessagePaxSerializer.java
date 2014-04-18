package org.dedee.messagepax.tests;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

	public void testMapStringString() throws Exception {
		s.reset();
		Map<String, String> map = null;
		s.writeMapStringString(map);
		assertEquals("C0", s.toHexString());
		s.reset();
		map = new HashMap<String, String>();
		map.put("0", "_");
		s.writeMapStringString(map);
		assertEquals("81A130A15F", s.toHexString());
		for (int i = 1; i < 40; i++) {
			map.put("" + i, "_");
		}
		s.reset();
		s.writeMapStringString(map);
		assertEquals(
				"DE0028A23232A15FA23233A15FA23234A15FA23235A15FA23236A15FA23237A15FA23238A15FA23239A15FA23330A15FA23331A15FA23130A15FA23332A15FA23131A15FA23333A15FA23132A15FA23334A15FA23133A15FA23335A15FA23134A15FA23336A15FA23135A15FA23337A15FA23136A15FA23338A15FA23137A15FA23339A15FA23138A15FA23139A15FA130A15FA131A15FA132A15FA133A15FA134A15FA135A15FA136A15FA137A15FA138A15FA139A15FA23230A15FA23231A15F",
				s.toHexString());
	}

	public void testAnotherMap() throws Exception {
		s.reset();
		Map<Integer, String> map = new HashMap<Integer, String>();
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
}
