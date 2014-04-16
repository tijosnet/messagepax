package org.dedee.messagepax;

import java.io.IOException;

public class MessagePaxSerializer extends BaseSerializer {

	public MessagePaxSerializer(byte[] b) {
		super(b);
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
			addByte(b.booleanValue() ? 0xc3 : 0xc2);
		}
	}

	public void writeInteger(int d) throws IOException {
		if (d < -(1 << 5)) {
			if (d < -(1 << 15)) {
				// int 32 stores a 32-bit big-endian signed integer
				// +--------+--------+--------+--------+--------+
				// | 0xd2 |ZZZZZZZZ|ZZZZZZZZ|ZZZZZZZZ|ZZZZZZZZ|
				// +--------+--------+--------+--------+--------+
				addByte(0xd2);
				addInt32(d);
			} else if (d < -(1 << 7)) {
				// int 16 stores a 16-bit big-endian signed integer
				// +--------+--------+--------+
				// | 0xd1 |ZZZZZZZZ|ZZZZZZZZ|
				// +--------+--------+--------+
				addByte(0xd1);
				addInt16(d);
			} else {
				// int 8 stores a 8-bit signed integer
				// +--------+--------+
				// | 0xd0 |ZZZZZZZZ|
				// +--------+--------+
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
				// +--------+--------+
				// | 0xcc |ZZZZZZZZ|
				// +--------+--------+
				addByte(0xcc);
				addByte(d);
			} else if (d < (1 << 16)) {
				// uint 16 stores a 16-bit big-endian unsigned integer
				// +--------+--------+--------+
				// | 0xcd |ZZZZZZZZ|ZZZZZZZZ|
				// +--------+--------+--------+
				addByte(0xcd);
				addInt16(d);
			} else {
				// uint 32 stores a 32-bit big-endian unsigned integer
				// +--------+--------+--------+--------+--------+
				// | 0xce |ZZZZZZZZ|ZZZZZZZZ|ZZZZZZZZ|ZZZZZZZZ
				// +--------+--------+--------+--------+--------+
				addByte(0xce);
				addInt32(d);
			}
		}
	}

	public void writeInteger(Integer i) throws IOException {
		if (i == null) {
			writeNil();
		} else {
			writeInteger(i.intValue());

		}
	}

	public void writeNil() throws IOException {
		// nil:
		// +--------+
		// | 0xc0 |
		// +--------+
		addByte(0xc0);
	}

}
