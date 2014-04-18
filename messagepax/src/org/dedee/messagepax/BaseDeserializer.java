package org.dedee.messagepax;


public class BaseDeserializer {

	protected byte[] b;
	protected int pos;

	// private ByteBuffer bb;

	public BaseDeserializer(byte[] b) {
		this.b = b;
		this.pos = 0;
		// bb = ByteBuffer.wrap(b);
	}

	protected int readByte() {
		return b[pos++] & 0xff;
	}

	protected int readInt16() {
		// int x = bb.getShort(pos);
		// pos += 2;
		// return x;
		int ret = 0;
		ret |= b[pos++] << 8;
		ret |= (b[pos++] & 0xff) << 0;
		return ret;
	}

	protected int readInt32() {
		// int x = bb.getInt(pos);
		// pos += 4;
		// return x;
		int ret = 0;
		ret |= b[pos++] << 24;
		ret |= (b[pos++] & 0xff) << 16;
		ret |= (b[pos++] & 0xff) << 8;
		ret |= (b[pos++] & 0xff) << 0;
		return ret;
	}

	protected long readInt64() {
		// long x = bb.getLong(pos);
		// pos += 8;
		// // return x;
		long ret = 0;
		ret |= (long) b[pos++] << 56;
		ret |= ((long) b[pos++] & 0xff) << 48;
		ret |= ((long) b[pos++] & 0xff) << 40;
		ret |= ((long) b[pos++] & 0xff) << 32;
		ret |= ((long) b[pos++] & 0xff) << 24;
		ret |= ((long) b[pos++] & 0xff) << 16;
		ret |= ((long) b[pos++] & 0xff) << 8;
		ret |= ((long) b[pos++] & 0xff) << 0;
		return ret;
	}

	protected boolean isNil(int x) {
		// nil:
		// +------+
		// | 0xc0 |
		// +------+
		return (x == 0xc0);
	}
}
