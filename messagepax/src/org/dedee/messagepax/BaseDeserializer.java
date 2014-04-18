package org.dedee.messagepax;

import java.nio.ByteBuffer;

public class BaseDeserializer {

	protected byte[] b;
	protected int pos;
	private ByteBuffer bb;

	public BaseDeserializer(byte[] b) {
		this.b = b;
		this.pos = 0;
		bb = ByteBuffer.wrap(b);
	}

	protected int readByte() {
		return b[pos++] & 0xff;
	}

	protected int readInt16() {
		int x = bb.getShort(pos);
		pos += 2;
		return x;
		// int ret = 0;
		// ret |= b[pos++] << 8;
		// ret |= b[pos++] << 0;
		// return ret;
	}

	protected int readInt32() {
		int x = bb.getInt(pos);
		pos += 4;
		return x;
		// int ch1 = b[pos++];
		// int ch2 = b[pos++];
		// int ch3 = b[pos++];
		// int ch4 = b[pos++];
		// return ((ch1 << 24) + (ch2 << 16) + (ch3 << 8) + (ch4 << 0));
		// int ret = 0;
		// ret |= b[pos++] << 24;
		// ret |= b[pos++] << 16;
		// ret |= b[pos++] << 8;
		// ret |= b[pos++] << 0;
		// return ret;
	}

	protected long readInt64() {
		long x = bb.getLong(pos);
		pos += 8;
		return x;
		// long ret = 0;
		// ret |= (long) b[pos++] << 56;
		// ret |= (long) b[pos++] << 48;
		// ret |= (long) b[pos++] << 40;
		// ret |= (long) b[pos++] << 32;
		// ret |= b[pos++] << 24;
		// ret |= b[pos++] << 16;
		// ret |= b[pos++] << 8;
		// ret |= b[pos++] << 0;
		// return ret;
	}

	protected boolean isNil(int x) {
		// nil:
		// +------+
		// | 0xc0 |
		// +------+
		return (x == 0xc0);
	}
}
