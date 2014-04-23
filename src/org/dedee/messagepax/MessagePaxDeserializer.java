package org.dedee.messagepax;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dedee.messagepax.tests.Utils;

public class MessagePaxDeserializer extends BaseDeserializer {

	public MessagePaxDeserializer(byte[] b) {
		super(b);
	}

	public Boolean readBoolean() throws IOException {
		int x = readByte();
		if (isNil(x)) {
			return null;
		} else {
			if (x == 0xc3)
				return true;
			else if (x == 0xc2)
				return false;
			else
				throw new IOException(
						"Illegal byte could not be interpreted as boolean");
		}
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

	public Float readFloat() {
		int x = readByte();
		if (isNil(x)) {
			return null;
		} else {
			int bits = readInt32();
			return Float.intBitsToFloat(bits);
		}
	}

	public Double readDouble() {
		int x = readByte();
		if (isNil(x)) {
			return null;
		} else {
			long bits = readInt64();
			return Double.longBitsToDouble(bits);
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

	public String readString() throws IOException {
		int x = readByte();
		if (isNil(x)) {
			return null;
		} else {
			int len = readLen(x);
			String s = new String(b, pos, len, Consts.STRING_ENCODING);
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

	public Integer readListBegin() {
		int x = readByte();
		if (isNil(x)) {
			return null;
		} else {
			int len = 0;
			if ((x & 0xf0) == 0x90) {
				// fixarray stores an array whose length is upto 15 elements:
				// +--------+~~~~~~~~~~~+
				// |1001XXXX| N objects |
				// +--------+~~~~~~~~~~~+
				len = x & 0x0f;
			} else if (x == 0xdc) {
				// array 16 stores an array whose length is upto (2^16)-1
				// elements:
				// +------+--------+--------+~~~~~~~~~~~+
				// | 0xdc |YYYYYYYY|YYYYYYYY| N objects |
				// +------+--------+--------+~~~~~~~~~~~+
				len = readInt16() & 0xffff;
			} else if (x == 0xdd) {
				// array 32 stores an array whose length is upto (2^32)-1
				// elements:
				// +------+--------+--------+--------+--------+~~~~~~~~~~~+
				// | 0xdd |ZZZZZZZZ|ZZZZZZZZ|ZZZZZZZZ|ZZZZZZZZ| N objects |
				// +------+--------+--------+--------+--------+~~~~~~~~~~~+
				len = readInt32() & 0x7fffffff;
			}
			return len;
		}
	}

	public Integer readMapBegin() {
		int x = readByte();
		if (isNil(x)) {
			return null;
		} else {
			int len = 0;
			if ((x & 0xf0) == 0x80) {
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
	}

	public void reset(String hexString) {
		pos = 0;
		Utils.dehex(hexString, b, 0);
	}

	public List<String> readStringList() throws IOException {
		Integer len = readListBegin();
		if (len == null) {
			return null;
		} else {
			List<String> l = new ArrayList<String>();
			for (int i = 0; i < len; i++) {
				l.add(readString());
			}
			return l;
		}
	}

	public Map<String, String> readStringMap() throws IOException {
		Integer len = readMapBegin();
		if (len == null) {
			return null;
		} else {
			Map<String, String> map = new HashMap<String, String>();
			for (int i = 0; i < len; i++) {
				String key = readString();
				String val = readString();
				map.put(key, val);
			}
			return map;
		}
	}

}
