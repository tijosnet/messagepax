package org.dedee.messagepax;

import java.io.IOException;

public class MessagePaxDeserializer extends BaseDeserializer {

	private static final String HEX = "0123456789ABCDEF";

	public MessagePaxDeserializer(byte[] b) {
		super(b);
	}

	public Integer readInteger() throws IOException {
		int x = readByte();
		if (isNil(x)) {
			return null;
		} else {
			int ret;
			if ((x & (1 << 7)) == 0) {
				// positive fixnum stores 7-bit positive integer
				// +--------+
				// |0XXXXXXX|
				// +--------+
				ret = x & 0x7f;
			} else {
				if ((x & 0xe0) == 0xe0) {
					// negative fixnum stores 5-bit negative integer
					// +--------+
					// |111YYYYY|
					// +--------+
					ret = (x & 0x1f) - 32;
				} else {
					if (x == 0xd0) {
						// int 8 stores a 8-bit signed integer
						// +------+--------+
						// | 0xd0 |ZZZZZZZZ|
						// +------+--------+
						ret = (byte) readByte();
					} else if (x == 0xd1) {
						// int 16 stores a 16-bit big-endian signed integer
						// +------+--------+--------+
						// | 0xd1 |ZZZZZZZZ|ZZZZZZZZ|
						// -------+--------+--------+
						ret = readInt16();
					} else if (x == 0xd2) {
						// int 32 stores a 32-bit big-endian signed integer
						// +------+--------+--------+--------+--------+
						// | 0xd2 |ZZZZZZZZ|ZZZZZZZZ|ZZZZZZZZ|ZZZZZZZZ|
						// +------+--------+--------+--------+--------+
						ret = readInt32();
					} else if (x == 0xcc) {
						// unsigned 8-bit XXXXXXXX
						// +------+--------+
						// | 0xcc |XXXXXXXX|
						// +------+--------+
						ret = readByte() & 0xff;
					} else if (x == 0xcd) {
						// uint 16 stores a 16-bit big-endian unsigned integer
						// +------+--------+--------+
						// | 0xcd |ZZZZZZZZ|ZZZZZZZZ|
						// +------+--------+--------+
						ret = readInt16() & 0xffff;
					} else if (x == 0xce) {
						// uint 32 stores a 32-bit big-endian unsigned integer
						// +------+--------+--------+--------+--------+
						// | 0xce |ZZZZZZZZ|ZZZZZZZZ|ZZZZZZZZ|ZZZZZZZZ
						// +------+--------+--------+--------+--------+
						ret = readInt32();
					} else {
						throw new IOException("Unknown integer type " + x);
					}
				}
			}
			return ret;
		}
	}

	public void reset(String hexString) {
		// if (b == null || b.length < (hexString.length() / 2)) {
		// b = new byte[hexString.length() / 2];
		// }
		pos = 0;
		int len = hexString.length() / 2;
		for (int i = 0; i < len; i++) {
			int n1 = HEX.indexOf(hexString.charAt(pos++)) & 0x0f;
			int n2 = HEX.indexOf(hexString.charAt(pos++)) & 0x0f;
			b[i] = (byte) (n1 << 4 | n2);
		}
		pos = 0;
	}
}
