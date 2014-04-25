package org.messagepax;

/**
 * Base serializer class provides basic integer encoding providing some stream
 * based API on a simple byte array.
 */
public class BaseSerializer {

	/** Buffer */
	protected byte[] b;

	/** Actual write position in buffer */
	protected int pos;

	/**
	 * Creates serializer with a preallocated buffer. The buffer size needs to
	 * be large enough to contain all your data which you write into.
	 * 
	 * @param b
	 *            Buffer to write to
	 */
	public BaseSerializer(byte[] b) {
		this.b = b;
		this.pos = 0;
	}

	/**
	 * Adds an unsigned byte to actual position and moves forward to next place
	 * in buffer.
	 * 
	 * @param x
	 *            Unsigned byte to write
	 */
	public void addByte(int x) {
		b[pos++] = (byte) (x & 0xff);
	}

	/**
	 * Writes a signed 16 bit integer.
	 * 
	 * @param x
	 *            Signed 16 bit Integer
	 */
	public void addInt16(int x) {
		b[pos++] = (byte) ((x >>> 8) & 0xff);
		b[pos++] = (byte) ((x >>> 0) & 0xff);
	}

	/**
	 * Writes a signed 32 bit integer.
	 * 
	 * @param x
	 *            Signed 32 bit Integer
	 */
	public void addInt32(int x) {
		b[pos++] = (byte) ((x >>> 24) & 0xff);
		b[pos++] = (byte) ((x >>> 16) & 0xff);
		b[pos++] = (byte) ((x >>> 8) & 0xff);
		b[pos++] = (byte) ((x >>> 0) & 0xff);
	}

	/**
	 * Writes a signed 64 bit integer.
	 * 
	 * @param x
	 *            Signed 64 bit Integer
	 */
	public void addInt64(long x) {
		b[pos++] = (byte) ((x >>> 56) & 0xff);
		b[pos++] = (byte) ((x >>> 48) & 0xff);
		b[pos++] = (byte) ((x >>> 40) & 0xff);
		b[pos++] = (byte) ((x >>> 32) & 0xff);
		b[pos++] = (byte) ((x >>> 24) & 0xff);
		b[pos++] = (byte) ((x >>> 16) & 0xff);
		b[pos++] = (byte) ((x >>> 8) & 0xff);
		b[pos++] = (byte) ((x >>> 0) & 0xff);
	}

	/**
	 * Adds bytes from given buffer starting at offset and using length.
	 * 
	 * @param buffer
	 *            Source buffer
	 * @param offset
	 *            Offset to start copying
	 * @param len
	 *            Number of bytes to copy
	 */
	protected void addBytes(byte[] buffer, int offset, int len) {
		System.arraycopy(buffer, offset, this.b, pos, len);
		pos += len;
	}

	/**
	 * Resets position. This method can be used to reuse the serializer after
	 * usage for another serialization operation.
	 */
	public void reset() {
		pos = 0;
	}

	/**
	 * Returns buffer
	 * 
	 * @return Buffer
	 */
	public byte[] getBuffer() {
		return b;
	}

	/**
	 * Returns number of bytes which were written into the buffer
	 * 
	 * @return Number of bytes which were written into the buffer
	 */
	public int getLength() {
		return pos;
	}

	/**
	 * Returns the buffer as HEX string, used for unit tests.
	 * 
	 * @return Buffer as HEX string
	 */
	public String toHexString() {
		return Utils.hex(b, 0, pos);
	}
}
