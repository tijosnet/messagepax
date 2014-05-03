package org.messagepax;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Messagepack serializer used to write all kind of objects into a byte array
 * buffer according to the MSGPACK format specification.
 * 
 * You need to provide a destination buffer which is large enough to keep all
 * your data which you write into it. Otherwise you will get an
 * {@link ArrayIndexOutOfBoundsException}.
 * 
 * Checkout the <a href="http://www.msgpack.org">website of MSGPACK</a> for
 * details.
 * 
 */
public class MessagePaxSerializer extends MessagePaxNativeSerializer {

	/**
	 * String encoding
	 */
	protected static final String STRING_ENCODING = "UTF-8";

	/**
	 * Creates a serializer with destination buffer which need to be large
	 * enough to keep all the data you write into.
	 * 
	 * @param b
	 *            Destination buffer
	 */
	public MessagePaxSerializer(byte[] b) {
		super(b);
	}

	/**
	 * Writes Boolean object into the buffer. If object is null a MSGPACK NIL
	 * object is stored.
	 * 
	 * @param b
	 *            Boolean object or null
	 * @throws IOException
	 *             If object could not be serialized
	 */
	public void writeBoolean(Boolean b) throws IOException {
		if (b == null) {
			writeNil();
		} else {
			writeBoolean(b.booleanValue());
		}
	}

	/**
	 * Writes Integer object into the buffer. If object is null a MSGPACK NIL
	 * object is stored.
	 * 
	 * @param i
	 *            Integer object or null
	 * @throws IOException
	 *             If object could not be serialized
	 */
	public void writeInteger(Integer i) throws IOException {
		if (i == null) {
			writeNil();
		} else {
			writeInteger(i.intValue());
		}
	}

	/**
	 * Writes Long object into the buffer. If object is null a MSGPACK NIL
	 * object is stored.
	 * 
	 * @param l
	 *            Long object or null
	 * @throws IOException
	 *             If object could not be serialized
	 */
	public void writeLong(Long l) throws IOException {
		if (l == null) {
			writeNil();
		} else {
			writeLong(l.longValue());
		}
	}

	/**
	 * Writes Float object into the buffer. If object is null a MSGPACK NIL
	 * object is stored.
	 * 
	 * @param f
	 *            Float object or null
	 * @throws IOException
	 *             If object could not be serialized
	 */
	public void writeFloat(Float f) throws IOException {
		if (f == null) {
			writeNil();
		} else {
			writeFloat(f.floatValue());
		}
	}

	/**
	 * Writes Double object into the buffer. If object is null a MSGPACK NIL
	 * object is stored.
	 * 
	 * @param d
	 *            Double object or null
	 * @throws IOException
	 *             If object could not be serialized
	 */
	public void writeDouble(Double d) throws IOException {
		if (d == null) {
			writeNil();
		} else {
			writeDouble(d.doubleValue());
		}
	}

	/**
	 * Writes String object into the buffer. If object is null a MSGPACK NIL
	 * object is stored.
	 * 
	 * @param s
	 *            String object or null
	 * @throws IOException
	 *             If object could not be serialized
	 */
	public void writeString(String s) throws IOException {
		if (s != null) {
			// TODO Optimize buffer usage
			byte[] buffer = null;
			int len = 0;
			buffer = s.getBytes(STRING_ENCODING);
			len = buffer.length;
			writeStringLength(len);
			addBytes(buffer, 0, len);
		} else {
			writeNil();
		}
	}

	/**
	 * Writes byte array content with a length prefix.
	 * 
	 * @param buffer
	 *            Buffer to use as source
	 * @param offset
	 *            Offset where to start
	 * @param len
	 *            Number of bytes to wrote
	 * @throws IOException
	 *             If object could not be serialized
	 */
	public void writeByteArray(byte[] buffer, int offset, int len)
			throws IOException {
		if (buffer == null) {
			writeNil();
		} else {
			writeByteArrayLength(len);
			addBytes(buffer, offset, len);
		}
	}

	/**
	 * Writes byte array content with a length prefix.
	 * 
	 * @param buffer
	 *            Buffer to use as source
	 * @throws IOException
	 *             If object could not be serialized
	 */
	public void writeByteArray(byte[] buffer) throws IOException {
		if (buffer == null) {
			writeNil();
		} else {
			writeByteArrayLength(buffer.length);
			addBytes(buffer, 0, buffer.length);
		}
	}

	/**
	 * Writes List object into the buffer. If object is null a MSGPACK NIL
	 * object is stored.
	 * 
	 * @param list
	 *            List object or null
	 * @throws IOException
	 *             If object could not be serialized
	 */
	public void writeStringList(List<String> list) throws IOException {
		if (list == null) {
			writeNil();
		} else {
			int size = list.size();
			writeListBegin(size);
			for (int i = 0; i < size; i++) {
				writeString(list.get(i));
			}
		}
	}

	/**
	 * Writes Map object into the buffer. If object is null a MSGPACK NIL object
	 * is stored.
	 * 
	 * @param map
	 *            Map object or null
	 * @throws IOException
	 *             If object could not be serialized
	 */
	public void writeStringMap(Map<String, String> map) throws IOException {
		if (map == null) {
			writeNil();
		} else {
			int size = map.size();
			writeMapBegin(size);
			Iterator<String> it = map.keySet().iterator();
			for (int i = 0; i < size; i++) {
				String key = it.next();
				String val = map.get(key);
				writeString(key);
				writeString(val);
			}
		}
	}

	/**
	 * Writes BigInteger object into the buffer. If object is null a MSGPACK NIL
	 * object is stored.
	 * 
	 * @param bi
	 *            BigInteger object or null
	 * @throws IOException
	 *             If object could not be serialized
	 */
	public void writeBigInteger(BigInteger bi) throws IOException {
		if (bi == null) {
			writeNil();
		} else {
			writeLong(bi.longValue());
			// if (bi.compareTo(BIGINT_LONG_MAX) == 1
			// || bi.compareTo(BIGINT_LONG_MIN) == -1) {
			// // > 64bit
			// throw new IOException("Integers >64 bit not supported");
			// } else if (bi.compareTo(BIGINT_INT_MAX) == 1
			// || bi.compareTo(BIGINT_INT_MIN) == -1) {
			// // > 32bit
			// writeLong(bi.longValue());
			// } else {
			// writeInteger(bi.intValue());
			// }
		}
	}

	/**
	 * Writes extended data object into the buffer. If object is null a MSGPACK
	 * NIL object is stored.
	 * 
	 * @param type
	 *            Extended Type ID (set by application)
	 * @param data
	 *            Byte array containing the extended data
	 * @throws IOException
	 *             If object could not be serialized
	 */
	public void writeExtData(int type, byte[] data) throws IOException {
		if (data == null) {
			writeNil();
		} else {
			int length = data.length;
			if (length == 1) {
				// fixext 1 stores an integer and a byte array whose length is 1
				// byte
				// +------+------+------+
				// | 0xd4 | type | data |
				// +------+------+------+
				addByte(0xd4);
				addByte(type);
				addBytes(data, 0, length);
			} else if (length == 2) {
				// fixext 2 stores an integer and a byte array whose length is 2
				// bytes
				// +------+------+------+------+
				// | 0xd5 | type | data | data |
				// +------+------+------+------+
				addByte(0xd5);
				addByte(type);
				addBytes(data, 0, length);
			} else if (length == 4) {
				// fixext 4
				addByte(0xd6);
				addByte(type);
				addBytes(data, 0, length);
			} else if (length == 8) {
				// fixext 8
				addByte(0xd7);
				addByte(type);
				addBytes(data, 0, length);
			} else if (length == 16) {
				// fixext 16
				addByte(0xd8);
				addByte(type);
				addBytes(data, 0, length);
			} else if (length < 256) {
				// ext 8 stores an integer and a byte array whose length is upto
				// (2^8)-1
				// bytes:
				// +------+--------+------+======+
				// | 0xc7 |XXXXXXXX| type | data |
				// +------+--------+------+======+
				addByte(0xc7);
				addByte(length);
				addByte(type);
				addBytes(data, 0, length);
			} else if (length < 65536) {
				// ext 16 stores an integer and a byte array whose length is
				// upto
				// (2^16)-1 bytes:
				// +------+--------+--------+------+======+
				// | 0xc8 |YYYYYYYY|YYYYYYYY| type | data |
				// +------+--------+--------+------+======+
				addByte(0xc8);
				addInt16(length);
				addByte(type);
				addBytes(data, 0, length);
			} else {
				// ext 32 stores an integer and a byte array whose length is
				// upto
				// (2^32)-1 bytes:
				// +------+--------+--------+--------+--------+------+======+
				// | 0xc9 |ZZZZZZZZ|ZZZZZZZZ|ZZZZZZZZ|ZZZZZZZZ| type | data |
				// +------+--------+--------+--------+--------+------+======+
				addByte(0xc9);
				addInt32(length);
				addByte(type);
				addBytes(data, 0, length);
			}
		}
	}

}
