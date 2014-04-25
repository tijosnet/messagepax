package org.messagepax;

import java.io.IOException;

/**
 * Extends the base serializer class and adds the basic MSGPACK type handling.
 * All objects are Java native types, so the NIL handling is not be handled here
 * in this class.
 */
public class MessagePaxNativeSerializer extends BaseSerializer {

	/**
	 * Creates native message pack serializer object
	 * 
	 * @param b
	 *            Buffer to write to
	 */
	public MessagePaxNativeSerializer(byte[] b) {
		super(b);
	}

	/**
	 * Writes NIL into the buffer
	 * 
	 * @throws IOException
	 *             If object could not be serialized
	 */
	public void writeNil() throws IOException {
		// nil:
		// +------+
		// | 0xc0 |
		// +------+
		addByte(0xc0);
	}

	/**
	 * Writes boolean value into the buffer
	 * 
	 * @param b
	 *            boolean value
	 * @throws IOException
	 *             If object could not be serialized
	 */
	public void writeBoolean(boolean b) throws IOException {
		// false:
		// +------+
		// | 0xc2 |
		// +------+
		//
		// true:
		// +------+
		// | 0xc3 |
		// +------+
		addByte(b ? 0xc3 : 0xc2);
	}

	/**
	 * Writes long value into the buffer
	 * 
	 * @param d
	 *            long value
	 * @throws IOException
	 *             If object could not be serialized
	 */
	public void writeLong(long d) throws IOException {
		if (d < -(1L << 31)) {
			// int 64 stores a 64-bit big-endian signed integer
			// +----+--------+--------+--------+--------+--------+--------+--------+--------+
			// |0xd3|ZZZZZZZZ|ZZZZZZZZ|ZZZZZZZZ|ZZZZZZZZ|ZZZZZZZZ|ZZZZZZZZ|ZZZZZZZZ|ZZZZZZZZ|
			// +----+--------+--------+--------+--------+--------+--------+--------+--------+
			addByte(0xd3);
			addInt64(d);
		} else if (d >= (1L << 32)) {
			// uint 64 stores a 64-bit big-endian unsigned integer
			// +----+--------+--------+--------+--------+--------+--------+--------+--------+
			// |0xcf|ZZZZZZZZ|ZZZZZZZZ|ZZZZZZZZ|ZZZZZZZZ|ZZZZZZZZ|ZZZZZZZZ|ZZZZZZZZ|ZZZZZZZZ|
			// +----+--------+--------+--------+--------+--------+--------+--------+--------+
			addByte(0xcf);
			addInt64(d);
		} else {
			// Ok it doesnt look like a long so encode as smaller int
			writeInteger((int) d);
		}
	}

	/**
	 * Writes int value into the buffer
	 * 
	 * @param d
	 *            int value
	 * @throws IOException
	 *             If object could not be serialized
	 */
	public void writeInteger(int d) throws IOException {
		if (d < -(1 << 5)) {
			if (d < -(1 << 15)) {
				// int 32 stores a 32-bit big-endian signed integer
				// +------+--------+--------+--------+--------+
				// | 0xd2 |ZZZZZZZZ|ZZZZZZZZ|ZZZZZZZZ|ZZZZZZZZ|
				// +------+--------+--------+--------+--------+
				addByte(0xd2);
				addInt32(d);
			} else if (d < -(1 << 7)) {
				// int 16 stores a 16-bit big-endian signed integer
				// +------+--------+--------+
				// | 0xd1 |ZZZZZZZZ|ZZZZZZZZ|
				// -------+--------+--------+
				addByte(0xd1);
				addInt16(d);
			} else {
				// int 8 stores a 8-bit signed integer
				// +------+--------+
				// | 0xd0 |ZZZZZZZZ|
				// +------+--------+
				addByte(0xd0);
				addByte(d);
			}
		} else if (d < (1 << 7)) {
			// positive fixnum stores 7-bit positive integer
			// +--------+
			// |0XXXXXXX|
			// +--------+
			addByte(d);
		} else {
			if (d < (1 << 8)) {
				// uint 8 stores a 8-bit unsigned integer
				// +------+--------+
				// | 0xcc |ZZZZZZZZ|
				// +------+--------+
				addByte(0xcc);
				addByte(d);
			} else if (d < (1 << 16)) {
				// uint 16 stores a 16-bit big-endian unsigned integer
				// +------+--------+--------+
				// | 0xcd |ZZZZZZZZ|ZZZZZZZZ|
				// +------+--------+--------+
				addByte(0xcd);
				addInt16(d);
			} else {
				// uint 32 stores a 32-bit big-endian unsigned integer
				// +------+--------+--------+--------+--------+
				// | 0xce |ZZZZZZZZ|ZZZZZZZZ|ZZZZZZZZ|ZZZZZZZZ
				// +------+--------+--------+--------+--------+
				addByte(0xce);
				addInt32(d);
			}
		}
	}

	/**
	 * Writes float value into the buffer
	 * 
	 * @param f
	 *            float value
	 * @throws IOException
	 *             If object could not be serialized
	 */
	public void writeFloat(float f) {
		// float 32 stores a floating point number in IEEE 754 single precision
		// floating point number format:
		// +------+--------+--------+--------+--------+
		// | 0xca |XXXXXXXX|XXXXXXXX|XXXXXXXX|XXXXXXXX
		// +------+--------+--------+--------+--------+
		addByte(0xca);
		int bits = Float.floatToIntBits(f);
		addInt32(bits);
	}

	/**
	 * Writes double value into the buffer
	 * 
	 * @param d
	 *            double value
	 * @throws IOException
	 *             If object could not be serialized
	 */
	public void writeDouble(double d) {
		// float 64 stores a floating point number in IEEE 754 double precision
		// floating point number format:
		// +--------+--------+--------+--------+--------+--------+--------+--------+--------+
		// | 0xcb
		// |YYYYYYYY|YYYYYYYY|YYYYYYYY|YYYYYYYY|YYYYYYYY|YYYYYYYY|YYYYYYYY|YYYYYYYY|
		// +--------+--------+--------+--------+--------+--------+--------+--------+--------+
		addByte(0xcb);
		long bits = Double.doubleToLongBits(d);
		addInt64(bits);
	}

	public void writeByteArray(byte[] buffer, int offset, int len)
			throws IOException {
		if (buffer == null) {
			writeNil();
		} else {
			if (len < 32) {
				// fixstr stores a byte array whose length is upto 31 bytes:
				// +--------+======+
				// |101XXXXX| data |
				// +--------+======+
				addByte(0xa0 | len);
			} else if (len < 65536) {
				// str 16 stores a byte array whose length is upto (2^16)-1
				// bytes:
				// +------+--------+--------+======+
				// | 0xda |ZZZZZZZZ|ZZZZZZZZ| data |
				// +------+--------+--------+======+
				addByte(0xda);
				addInt16(len);
			} else {
				// str 32 stores a byte array whose length is upto (2^32)-1
				// bytes:
				// +------+--------+--------+--------+--------+======+
				// | 0xdb |AAAAAAAA|AAAAAAAA|AAAAAAAA|AAAAAAAA| data |
				// +------+--------+--------+--------+--------+======+
				addByte(0xdb);
				addInt32(len);
			}
			addBytes(buffer, offset, len);
		}
	}

	/**
	 * Writes length value into the buffer. You need to continue with writing
	 * content elements of the list afterwards.
	 * 
	 * Writing a list of two integers would basically look like that
	 * 
	 * <pre>
	 * s.writeListBegin(2);
	 * s.writeInteger(1);
	 * s.writeInteger(2);
	 * </pre>
	 * 
	 * @param size
	 *            Size information to write
	 * @throws IOException
	 *             If object could not be serialized
	 */
	public void writeListBegin(int size) throws IOException {
		if (size < 16) {
			// fixarray stores an array whose length is upto 15 elements:
			// +--------+~~~~~~~~~~~+
			// |1001XXXX| N objects |
			// +--------+~~~~~~~~~~~+
			addByte(0x90 | size);
		} else if (size < 65536) {
			// array 16 stores an array whose length is upto (2^16)-1 elements:
			// +------+--------+--------+~~~~~~~~~~~+
			// | 0xdc |YYYYYYYY|YYYYYYYY| N objects |
			// +------+--------+--------+~~~~~~~~~~~+
			addByte(0xdc);
			addInt16(size);
		} else {
			// array 32 stores an array whose length is upto (2^32)-1 elements:
			// +------+--------+--------+--------+--------+~~~~~~~~~~~+
			// | 0xdd |ZZZZZZZZ|ZZZZZZZZ|ZZZZZZZZ|ZZZZZZZZ| N objects |
			// +------+--------+--------+--------+--------+~~~~~~~~~~~+
			addByte(0xdd);
			addInt32(size);
		}
	}

	/**
	 * Writes length value into the buffer. You need to continue with writing
	 * content elements of the map afterwards.
	 * 
	 * Writing a map with integer key and string values would basically look
	 * like that
	 * 
	 * <pre>
	 * s.writeMapBegin(2);
	 * s.writeInteger(1);
	 * s.writeString(&quot;first&quot;);
	 * s.writeInteger(2);
	 * s.writeString(&quot;second&quot;);
	 * </pre>
	 * 
	 * @param size
	 *            Size information to write
	 * @throws IOException
	 *             If object could not be serialized
	 */
	public void writeMapBegin(int size) throws IOException {
		if (size < 16) {
			// fixmap stores a map whose length is up to 15 elements
			// +--------+~~~~~~~~~~~~~+
			// |1000XXXX| N*2 objects |
			// +--------+~~~~~~~~~~~~~+
			addByte(0x80 | size);
		} else if (size < 65536) {
			// map 16 stores a map whose length is up to (2^16)-1 elements
			// +------+--------+--------+~~~~~~~~~~~~~+
			// | 0xde |YYYYYYYY|YYYYYYYY| N*2 objects |
			// +------+--------+--------+~~~~~~~~~~~~~+
			addByte(0xde);
			addInt16(size);
		} else {
			// map 32 stores a map whose length is up to (2^32)-1 elements
			// +------+--------+--------+--------+--------+~~~~~~~~~~~~~+
			// | 0xdf |ZZZZZZZZ|ZZZZZZZZ|ZZZZZZZZ|ZZZZZZZZ| N*2 objects |
			// +------+--------+--------+--------+--------+~~~~~~~~~~~~~+
			addByte(0xdf);
			addInt32(size);
		}
	}
}
