package org.messagepax;

public class Utils {
	private static final char[] CHARS = new char[] { '0', '1', '2', '3', '4',
			'5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F', };

	public static String hex(byte[] b, int off, int len) {
		if (b == null) {
			return "null";
		}
		StringBuilder sb = new StringBuilder();
		for (int i = off; i < (off + len); i++) {
			sb.append(CHARS[(b[i] >> 4) & 0x0f]);
			sb.append(CHARS[(b[i] >> 0) & 0x0f]);
		}
		return sb.toString();
	}

	public static String hex(byte[] b) {
		return b != null ? hex(b, 0, b.length) : "null";
	}

	public static byte[] dehex(String hex) {
		byte[] b = new byte[hex.length() / 2];
		for (int i = 0; i < b.length; i++) {
			b[i] = (byte) Integer.parseInt(hex.substring(2 * i, 2 * i + 2), 16);
		}
		return b;
	}

	public static void dehex(String hex, byte[] b, int off) {
		for (int i = 0; i < hex.length() / 2; i++) {
			b[i + off] = (byte) Integer.parseInt(
					hex.substring(2 * i, 2 * i + 2), 16);
		}
	}

}
