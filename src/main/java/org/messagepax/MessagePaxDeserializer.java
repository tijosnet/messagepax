package org.messagepax;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Deserializer class used to unpack a byte array containing MSGPACK formatted
 * serialized objects. See MSGPACK specification for details how the objects are
 * encoded in the buffer.
 * 
 * See original website of <a href="http://www.msgpack.org">
 * http://www.msgpack.org</a> for details about the specification.
 * 
 */
public class MessagePaxDeserializer extends BaseDeserializer {

	/**
	 * Creates a deserializer object with buffer containing objects serialized
	 * according to the MSGPACK format specification.
	 * 
	 * @param b
	 *            Buffer containing serialized data
	 */
	public MessagePaxDeserializer(byte[] b) {
		super(b);
	}

	/**
	 * Reads a Boolean object from buffer
	 * 
	 * @return Boolean object or null
	 * @throws IOException
	 *             If value could not be decoded correctly
	 */
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

	/**
	 * Reads an Integer object from buffer. Could be 8, 16 or 32 signed integer
	 * in Java.
	 * 
	 * @return Integer object or null
	 * @throws IOException
	 *             If value could not be decoded correctly
	 */
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

	/**
	 * Reads an Integer object from buffer. Could be 8, 16, 32 or 64 bit signed
	 * integer in Java.
	 * 
	 * @return Integer object or null
	 * @throws IOException
	 *             If value could not be decoded correctly
	 */
	public Long readLong() throws IOException {
		int x = readByte();
		if (isNil(x)) {
			return null;
		} else {
			long l = 0;
			if (x == 0xd3) {
				// int 64 stores a 64-bit big-endian signed integer
				// +----+--------+--------+--------+--------+--------+--------+--------+--------+
				// |0xd3|ZZZZZZZZ|ZZZZZZZZ|ZZZZZZZZ|ZZZZZZZZ|ZZZZZZZZ|ZZZZZZZZ|ZZZZZZZZ|ZZZZZZZZ|
				// +----+--------+--------+--------+--------+--------+--------+--------+--------+
				l = readInt64();
			} else if (x == 0xcf) {
				// uint 64 stores a 64-bit big-endian unsigned integer
				// +----+--------+--------+--------+--------+--------+--------+--------+--------+
				// |0xcf|ZZZZZZZZ|ZZZZZZZZ|ZZZZZZZZ|ZZZZZZZZ|ZZZZZZZZ|ZZZZZZZZ|ZZZZZZZZ|ZZZZZZZZ|
				// +----+--------+--------+--------+--------+--------+--------+--------+--------+
				l = readInt64();
			} else {
				// 'unread' header - TODO improve that
				pos--;
				l = readInteger();
			}
			return l;
		}
	}

	/**
	 * Reads a Float object from buffer
	 * 
	 * @return Float object or null
	 * @throws IOException
	 *             If value could not be decoded correctly
	 */
	public Float readFloat() throws IOException {
		int x = readByte();
		if (isNil(x)) {
			return null;
		} else {
			int bits = readInt32();
			return Float.intBitsToFloat(bits);
		}
	}

	/**
	 * Reads a Double object from buffer
	 * 
	 * @return Double object or null
	 * @throws IOException
	 *             If value could not be decoded correctly
	 */
	public Double readDouble() throws IOException {
		int x = readByte();
		if (isNil(x)) {
			return null;
		} else {
			long bits = readInt64();
			return Double.longBitsToDouble(bits);
		}
	}

	/**
	 * Reads a byte array object from buffer
	 * 
	 * @return Byte array object or null
	 * @throws IOException
	 *             If value could not be decoded correctly
	 */
	public byte[] readByteArray() throws IOException {
		int x = readByte();
		if (isNil(x)) {
			return null;
		} else {
			int len = readByteArrayOrStringLen(x);
			byte[] ret = new byte[len];
			System.arraycopy(b, pos, ret, 0, len);
			pos += len;
			return ret;
		}
	}

	/**
	 * Reads a String object from buffer
	 * 
	 * @return String array object or null
	 * @throws IOException
	 *             If value could not be decoded correctly
	 */
	public String readString() throws IOException {
		int x = readByte();
		if (isNil(x)) {
			return null;
		} else {
			int len = readByteArrayOrStringLen(x);
			String s = new String(b, pos, len,
					MessagePaxSerializer.STRING_ENCODING);
			pos += len;
			return s;
		}
	}

	/**
	 * Internal method to interpret the 1st byte which can contain already the
	 * length information or if required continue to read subsequent bytes to
	 * interpret 16 or 32 bit length information in the next bytes.
	 * 
	 * @param x
	 *            First byte containing type and sometimes length too
	 * @return Length information
	 */
	private int readByteArrayOrStringLen(int x) {
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
		} else if (x == 0xc4) {
			// bin 8 stores a byte array whose length is upto (2^8)-1 bytes:
			// +------+--------+======+
			// | 0xc4 |XXXXXXXX| data |
			// +------+--------+======+
			len = readByte();
		} else if (x == 0xc5) {
			// bin 16 stores a byte array whose length is upto (2^16)-1 bytes:
			// +------+--------+--------+======+
			// | 0xc5 |YYYYYYYY|YYYYYYYY| data |
			// +------+--------+--------+======+
			len = readInt16();
		} else if (x == 0xc6) {
			// bin 32 stores a byte array whose length is upto (2^32)-1 bytes:
			// +------+--------+--------+--------+--------+======+
			// | 0xc6 |ZZZZZZZZ|ZZZZZZZZ|ZZZZZZZZ|ZZZZZZZZ| data |
			// +------+--------+--------+--------+--------+======+
			len = readInt32();
		}
		return len;
	}

	/**
	 * Reads a length information of a list object from buffer
	 * 
	 * @return Length information of list object or null if list is null
	 * @throws IOException
	 *             If value could not be decoded correctly
	 */
	public Integer readListBegin() throws IOException {
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

	/**
	 * Reads a length information of a map object from buffer
	 * 
	 * @return Length information of map object or null if map is null
	 * @throws IOException
	 *             If value could not be decoded correctly
	 */
	public Integer readMapBegin() throws IOException {
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

	/**
	 * Resets the buffer and fills it with data passed in a HEX string. This
	 * method shall just be used from the unit tests to rewind and fill new test
	 * data.
	 * 
	 * @param hexString
	 *            Test data to be set into buffer
	 */
	public void reset(String hexString) {
		pos = 0;
		Utils.dehex(hexString, b, 0);
	}

	/**
	 * Reads a String list object from buffer
	 * 
	 * @return String list object or null
	 * @throws IOException
	 *             If value could not be decoded correctly
	 */
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

	/**
	 * Reads a String map object from buffer
	 * 
	 * @return String map object or null
	 * @throws IOException
	 *             If value could not be decoded correctly
	 */
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

	/**
	 * Reads a BigInteger object from buffer
	 * 
	 * @return BigInteger object or null
	 * @throws IOException
	 *             If value could not be decoded correctly
	 */
	public BigInteger readBigInteger() throws IOException {
		Long l = readLong();
		if (l != null) {
			return BigInteger.valueOf(l.longValue());
		} else {
			return null;
		}
	}

}
