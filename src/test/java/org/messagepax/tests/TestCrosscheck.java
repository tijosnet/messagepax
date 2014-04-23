package org.messagepax.tests;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.messagepax.MessagePaxDeserializer;
import org.messagepax.MessagePaxSerializer;
import org.messagepax.Utils;
import org.msgpack.MessagePack;
import org.msgpack.packer.BufferPacker;
import org.msgpack.unpacker.BufferUnpacker;

public class TestCrosscheck extends TestCase {

	private byte[] bufdefault = new byte[128];
	MessagePack msgpack = new MessagePack();

	private int[] INTEGERS = new int[] { Integer.MIN_VALUE, Integer.MAX_VALUE,
			-1, -2, -4, -8, -16, -32, -64, -128, -256, -512, 0, 1, 2, 4, 8, 16,
			32, 64, 128, 256, 512, 1024 };

	private String[] INTEGER_CODES = new String[] { "D280000000", // -2147483648
			"CE7FFFFFFF", // 2147483647
			"FF", // -1
			"FE", // -2
			"FC", // -4
			"F8", // -8
			"F0", // -16
			"E0", // -32
			"D0C0", // -64
			"D080", // -128
			"D1FF00", // -256
			"D1FE00", // -512
			"00", // 0
			"01", // 1
			"02", // 2
			"04", // 4
			"08", // 8
			"10", // 16
			"20", // 32
			"40", // 64
			"CC80", // 128
			"CD0100", // 256
			"CD0200", // 512
			"CD0400", // 1024
	};

	private String[] STRINGS = new String[] { "Hello world", "1", "",
			"The quick brown fox jumps over the lazy dog", null };
	private int[] SPECIAL_STRIN_LENGTHS = new int[] { 0, 1, 2, 5, 10, 20, 30,
			31, 32, 33, 0xffff - 1, 0xffff, 0xfffff };

	private String stdEncode(Integer i) throws Exception {
		BufferPacker packer = msgpack.createBufferPacker();
		packer.write(i);
		byte[] b2 = packer.toByteArray();
		return Utils.hex(b2, 0, b2.length);
	}

	private Integer stdDecode(String s) throws Exception {
		BufferUnpacker unpacker = msgpack.createBufferUnpacker(Utils.dehex(s));
		return unpacker.readInt();
	}

	private String ourEncode(Integer i) throws Exception {
		MessagePaxSerializer s = new MessagePaxSerializer(bufdefault);
		s.writeInteger(i);
		return Utils.hex(s.getBuffer(), 0, s.getLength());
	}

	private Integer ourDecode(String s) throws Exception {
		MessagePaxDeserializer d = new MessagePaxDeserializer(Utils.dehex(s));
		return d.readInteger();
	}

	public void testIntegersEncodingSameResult() throws Exception {
		for (int i = 0; i < INTEGERS.length; i++) {
			int x = INTEGERS[i];
			String s1 = stdEncode(x);
			String s2 = ourEncode(x);
			System.out.println("\"" + s1 + "\", // " + x);// + " == " + s2);
			assertEquals("Integer " + x + " failed", s1, s2);
		}
	}

	public void testOurDecoding() throws Exception {
		for (int i = 0; i < INTEGERS.length; i++) {
			String x = INTEGER_CODES[i];
			System.out.println(x);
			Integer d = ourDecode(x);
			assertEquals(stdDecode(x), d);
		}
	}

	public void testIntegersEncoding() throws Exception {
		BufferUnpacker unpacker = msgpack.createBufferUnpacker();
		MessagePaxSerializer s = new MessagePaxSerializer(bufdefault);
		for (int i = 0; i < INTEGERS.length; i++) {
			// Pack with ours
			s.reset();
			s.writeInteger(INTEGERS[i]);

			// Unpack with MsgPack ORG
			unpacker.wrap(bufdefault, 0, s.getLength());
			int i2 = unpacker.readInt();
			assertEquals(INTEGERS[i], i2);
		}
	}

	public void testIntegersDecoding() throws Exception {
		for (int i = 0; i < INTEGERS.length; i++) {
			// MsgPack ORG
			BufferPacker packer = msgpack.createBufferPacker();
			packer.write(INTEGERS[i]);
			byte[] b = packer.toByteArray();

			System.out.println(INTEGERS[i] + "  <->  "
					+ Utils.hex(b, 0, b.length));
			// Unpack with our one
			MessagePaxDeserializer d = new MessagePaxDeserializer(b);
			int i2 = d.readInteger();
			assertEquals(INTEGERS[i], i2);
		}
	}

	public void testStringStandardStuff() throws Exception {
		for (String s1 : STRINGS) {
			// MsgPack ORG
			BufferPacker packer = msgpack.createBufferPacker();
			packer.write(s1);
			byte[] b = packer.toByteArray();
			System.out.println(s1 + ": '" + Utils.hex(b, 0, b.length) + "'");
			// Unpack with our one
			MessagePaxDeserializer d = new MessagePaxDeserializer(b);
			String s2 = d.readString();
			assertEquals(s1, s2);
		}
	}

	public void testStringSpecialLengths() throws Exception {
		for (int len : SPECIAL_STRIN_LENGTHS) {
			System.out.println("String length: " + len);
			// Prepare string
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < len; i++) {
				sb.append("X");
			}
			String s = sb.toString();
			// MsgPack ORG
			BufferPacker packer = msgpack.createBufferPacker();
			packer.write(s);
			byte[] b = packer.toByteArray();
			// Unpack with our one
			MessagePaxDeserializer d = new MessagePaxDeserializer(b);
			String s2 = d.readString();
			assertEquals(s, s2);
		}
	}

	// ------

	public void testReadStringList() throws Exception {
		// Empty
		List<String> l1 = new ArrayList<String>();
		List<String> l2 = ourDecodeStringList(msgPackEncodeStringList(l1));
		assertEquals(l1, l2);
		// Hello world
		l1.add("Hello world");
		l2 = ourDecodeStringList(msgPackEncodeStringList(l1));
		assertEquals(l1, l2);
		// Hello world
		l1.add("Hello world");
		l2 = ourDecodeStringList(msgPackEncodeStringList(l1));
		assertEquals(l1, l2);
	}

	private byte[] msgPackEncodeStringList(List<String> l) throws IOException {
		BufferPacker packer = msgpack.createBufferPacker();
		packer.write(l);
		return packer.toByteArray();
	}

	private List<String> ourDecodeStringList(byte[] b) throws IOException {
		System.out.println(Utils.hex(b));
		MessagePaxDeserializer d = new MessagePaxDeserializer(b);
		return d.readStringList();
	}

	// ------

	public void testReadStringMap() throws Exception {
		Map<String, String> map1 = new HashMap<String, String>();
		for (int i = 0; i < 1000; i++) {
			map1.put("" + i, "A" + i);
			Map<String, String> map2 = ourDecodeStringMap(msgPackEncodeStringMap(map1));
			assertEquals(map1, map2);
		}
	}

	private byte[] msgPackEncodeStringMap(Map<String, String> map)
			throws IOException {
		BufferPacker packer = msgpack.createBufferPacker();
		packer.write(map);
		return packer.toByteArray();
	}

	private Map<String, String> ourDecodeStringMap(byte[] b) throws IOException {
		MessagePaxDeserializer d = new MessagePaxDeserializer(b);
		return d.readStringMap();
	}
}
