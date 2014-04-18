package org.dedee.messagepax.tests;

public class Utils {
	private static final char[] CHARS = new char[] { '0', '1', '2', '3', '4',
			'5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F', };

	public static String hex(byte[] b, int off, int len) {
		StringBuilder sb = new StringBuilder();
		for (int i = off; i < (off + len); i++) {
			sb.append(CHARS[(b[i] >> 4) & 0x0f]);
			sb.append(CHARS[(b[i] >> 0) & 0x0f]);
		}
		return sb.toString();
	}

	public static byte[] dehex(String hexString) {
		int len = hexString.length() / 2;
		byte b[] = new byte[len];
		for (int i = 0; i < len; i += 2) {
			b[i] = (byte) (Integer.parseInt(hexString.substring(i, i + 2), 16) & 0xff);
		}
		return b;
	}
}
