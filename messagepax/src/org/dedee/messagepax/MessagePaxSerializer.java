package org.dedee.messagepax;

import java.io.IOException;
import java.io.OutputStream;

public class MessagePaxSerializer {

	private OutputStream os;

	public MessagePaxSerializer(OutputStream os) {
		this.os = os;
	}

	public void writeBoolean(Boolean b) throws IOException {
		if (b == null) {
			writeNil();
		} else {
			// false:
			// +------+
			// | 0xc2 |
			// +------+
			//
			// true:
			// +------+
			// | 0xc3 |
			// +------+
			os.write(b.booleanValue() ? 0xc3 : 0xc2);
		}
	}

	public void writeInteger(long l) throws IOException {

	}

	public void writeInteger(Integer i) throws IOException {
		if (i == null) {
			writeNil();
		} else {
			if (i >= 0) {
				if (i <= 0x7f) {
					// positive fixnum stores 7-bit positive integer
					// +--------+
					// |0XXXXXXX|
					// +--------+
					os.write(i);
				} else if (i == 0xff) {
					// uint 8 stores a 8-bit unsigned integer
					// +------+--------+
					// | 0xcc |ZZZZZZZZ|
					// +------+--------+
					os.write(0xcc);
					os.write(i);
				} else if (i <= 0xffff) {
					// uint 16 stores a 16-bit big-endian unsigned integer
					// +------+--------+--------+
					// | 0xcd |ZZZZZZZZ|ZZZZZZZZ|
					// +------+--------+--------+
					os.write(0xcd);
					os.write((i >> 8) & 0xff);
					os.write((i >> 0) & 0xff);
				}

			} else {
				i = Math.abs(i);
				if (i <= 0x1f) {
					// negative fixnum stores 5-bit negative integer
					// +--------+
					// |111YYYYY|
					// +--------+
					os.write(0xe0 | i & 0x1f);
				}

			}
		}
	}

	public void writeNil() throws IOException {
		// nil:
		// +--------+
		// | 0xc0 |
		// +--------+
		os.write(0xc0);
	}

}
