package org.messagepax;


public class BaseSerializer {

	protected byte[] b;
	protected int pos;

	public BaseSerializer(byte[] b) {
		this.b = b;
		this.pos = 0;
	}

	public void addByte(int x) {
		b[pos++] = (byte) (x & 0xff);
	}

	public void addInt16(int x) {
		b[pos++] = (byte) ((x >>> 8) & 0xff);
		b[pos++] = (byte) ((x >>> 0) & 0xff);
	}

	public void addInt32(int x) {
		b[pos++] = (byte) ((x >>> 24) & 0xff);
		b[pos++] = (byte) ((x >>> 16) & 0xff);
		b[pos++] = (byte) ((x >>> 8) & 0xff);
		b[pos++] = (byte) ((x >>> 0) & 0xff);
	}

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

	protected void addBytes(byte[] buffer, int offset, int len) {
		System.arraycopy(buffer, offset, this.b, pos, len);
		pos += len;
	}

	public void reset() {
		pos = 0;
	}

	public byte[] getBuffer() {
		return b;
	}

	public int getLength() {
		return pos;
	}

	public String toHexString() {
		return Utils.hex(b, 0, pos);
	}
}
