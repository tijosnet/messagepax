package org.dedee.messagepax.tests;

import junit.framework.TestCase;

import org.dedee.messagepax.MessagePaxDeserializer;
import org.dedee.messagepax.MessagePaxSerializer;
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

	private String stdEncode(Integer i) throws Exception {
		BufferPacker packer = msgpack.createBufferPacker();
		packer.write(i);
		byte[] b2 = packer.toByteArray();
		return Utils.hex(b2, 0, b2.length);
	}

	private Integer stdDecode(String s) throws Exception {
		BufferUnpacker unpacker = msgpack.createBufferUnpacker(Utils.dehex(s));
		return unpacker.readInt();
		// return unpacker.readValue().asIntegerValue().intValue();
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

			// assertEquals("Decoding int " + INTEGERS[i] + " from string " + x
			// + " failed", INTEGERS[i], d.intValue());
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
}
