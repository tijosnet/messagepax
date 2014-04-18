package org.dedee.messagepax;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

	public byte[] readByteArray() {
		int x = readByte();
		if (isNil(x)) {
			return null;
		} else {
			int len = readLen(x);
			byte[] ret = new byte[len];
			System.arraycopy(b, pos, ret, 0, len);
			pos += len;
			return ret;
		}
	}

	public String readString() {
		int x = readByte();
		if (isNil(x)) {
			return null;
		} else {
			int len = readLen(x);
			String s = new String(b, pos, len);
			pos += len;
			return s;
		}
	}

	private int readLen(int x) {
		int len = 0;
		if ((x & 0xa0) == 0xa0) {
			// fixstr stores a byte array whose length is upto 31 bytes:
			// +--------+======+
			// |101XXXXX| data |
			// +--------+======+
			len = x & 0x1f;
		} else if (x == 0xda) {
			// str 16 stores a byte array whose length is upto (2^16)-1
			// bytes:
			// +------+--------+--------+======+
			// | 0xda |ZZZZZZZZ|ZZZZZZZZ| data |
			// +------+--------+--------+======+
			len = readInt16() & 0xffff;
		} else if (x == 0xdb) {
			// str 32 stores a byte array whose length is upto (2^32)-1
			// bytes:
			// +------+--------+--------+--------+--------+======+
			// | 0xdb |AAAAAAAA|AAAAAAAA|AAAAAAAA|AAAAAAAA| data |
			// +------+--------+--------+--------+--------+======+
			len = readInt32() & 0x7fffffff;
		}
		return len;
	}

	private int readListLen(int x) {
		int len = 0;
		if ((x & 0x90) == 0x90) {
			// fixarray stores an array whose length is upto 15 elements:
			// +--------+~~~~~~~~~~~+
			// |1001XXXX| N objects |
			// +--------+~~~~~~~~~~~+
			len = x & 0x0f;
		} else if (x == 0xdc) {
			// array 16 stores an array whose length is upto (2^16)-1 elements:
			// +------+--------+--------+~~~~~~~~~~~+
			// | 0xdc |YYYYYYYY|YYYYYYYY| N objects |
			// +------+--------+--------+~~~~~~~~~~~+
			len = readInt16() & 0xffff;
		} else if (x == 0xdd) {
			// array 32 stores an array whose length is upto (2^32)-1 elements:
			// +------+--------+--------+--------+--------+~~~~~~~~~~~+
			// | 0xdd |ZZZZZZZZ|ZZZZZZZZ|ZZZZZZZZ|ZZZZZZZZ| N objects |
			// +------+--------+--------+--------+--------+~~~~~~~~~~~+
			len = readInt32() & 0x7fffffff;
		}
		return len;
	}

	public int readMapBegin(int x) {
		int len = 0;
		if ((x & 0x90) == 0x90) {
			// fixmap stores a map whose length is up to 15 elements
			// +--------+~~~~~~~~~~~~~+
			// |1000XXXX| N*2 objects |
			// +--------+~~~~~~~~~~~~~+
			len = x & 0x0f;
		} else if (x == 0xde) {
			// map 16 stores a map whose length is up to (2^16)-1 elements
			// +------+--------+--------+~~~~~~~~~~~~~+
			// | 0xde |YYYYYYYY|YYYYYYYY| N*2 objects |
			// +------+--------+--------+~~~~~~~~~~~~~+
			len = readInt16() & 0xffff;
		} else if (x == 0xdf) {
			// map 32 stores a map whose length is up to (2^32)-1 elements
			// +------+--------+--------+--------+--------+~~~~~~~~~~~~~+
			// | 0xdf |ZZZZZZZZ|ZZZZZZZZ|ZZZZZZZZ|ZZZZZZZZ| N*2 objects |
			// +------+--------+--------+--------+--------+~~~~~~~~~~~~~+
			len = readInt32() & 0x7fffffff;
		}
		return len;
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

	public List<String> readStringList(byte[] b) throws IOException {
		int x = readByte();
		if (isNil(x)) {
			return null;
		} else {
			int len = readListLen(x);
			List<String> l = new ArrayList<String>();
			for (int i = 0; i < len; i++) {
				l.add(readString());
			}
			return l;
		}
	}

}
