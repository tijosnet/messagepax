package org.messagepax;

/**
 * Base class of deserializers provides NIL and basic INTEGER decoding. It works
 * on an internal buffer (simple byte array) and provides a simple stream based
 * API.
 */
public class BaseDeserializer {

	/** Buffer */
	protected byte[] b;

	/** Actual read position in buffer */
	protected int pos;

	/**
	 * Creates basic deserializer for given buffer. Buffer need to contain
	 * MSGPACK data.
	 * 
	 * @param b
	 *            Buffer containing serialized data (MSGPACK format)
	 */
	protected BaseDeserializer(byte[] b) {
		this.b = b;
		this.pos = 0;
	}

	/**
	 * Reads one unsigned byte from the actual position in the buffer and moves
	 * position forward to the next byte.
	 * 
	 * @return Read byte
	 */
	protected int readByte() {
		return b[pos++] & 0xff;
	}

	/**
	 * Reads two bytes and interprets them as 16 bit signed integer.
	 * 
	 * @return Signed integer (16bit)
	 */
	protected int readInt16() {
		int ret = 0;
		ret |= b[pos++] << 8;
		ret |= (b[pos++] & 0xff);
		return ret;
	}

	/**
	 * Reads four bytes and interprets them as 32 bit signed integer.
	 * 
	 * @return Signed integer (32bit)
	 */
	protected int readInt32() {
		int ret = 0;
		ret |= b[pos++] << 24;
		ret |= (b[pos++] & 0xff) << 16;
		ret |= (b[pos++] & 0xff) << 8;
		ret |= (b[pos++] & 0xff);
		return ret;
	}

	/**
	 * Reads eight bytes and interprets them as 64 bit signed integer.
	 * 
	 * @return Signed integer (64bit)
	 */
	protected long readInt64() {
		long ret = 0;
		ret |= (long) b[pos++] << 56;
		ret |= ((long) b[pos++] & 0xff) << 48;
		ret |= ((long) b[pos++] & 0xff) << 40;
		ret |= ((long) b[pos++] & 0xff) << 32;
		ret |= ((long) b[pos++] & 0xff) << 24;
		ret |= ((long) b[pos++] & 0xff) << 16;
		ret |= ((long) b[pos++] & 0xff) << 8;
		ret |= ((long) b[pos++] & 0xff);
		return ret;
	}

	/**
	 * Returns true if given byte is NIL (0xC0). NIL is used for null object
	 * values.
	 * 
	 * @param x
	 *            Byte to check
	 * @return true if given byte is NIL
	 */
	protected boolean isNil(int x) {
		// nil:
		// +------+
		// | 0xc0 |
		// +------+
		return (x == 0xc0);
	}
}
