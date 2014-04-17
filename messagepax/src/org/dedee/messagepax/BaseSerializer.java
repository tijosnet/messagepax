package org.dedee.messagepax;

public class BaseSerializer {

	private static final char[] CHARS = new char[] { '0', '1', '2', '3', '4',
			'5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F', };

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
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < pos; i++) {
			sb.append(CHARS[(b[i] >> 4) & 0x0f]);
			sb.append(CHARS[(b[i] >> 0) & 0x0f]);
		}
		return sb.toString();
	}
}
