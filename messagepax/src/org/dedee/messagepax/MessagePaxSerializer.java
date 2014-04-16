package org.dedee.messagepax;

import java.io.IOException;

public class MessagePaxSerializer extends MessagePaxNativeSerializer {

	public MessagePaxSerializer(byte[] b) {
		super(b);
	}

	// Object types

	public void writeBoolean(Boolean b) throws IOException {
		if (b == null) {
			writeNil();
		} else {
			writeBoolean(b.booleanValue());
		}
	}

	public void writeByte(Byte i) throws IOException {
		if (i == null) {
			writeNil();
		} else {
			writeInteger(i.intValue());
		}
	}

	public void writeInteger(Integer i) throws IOException {
		if (i == null) {
			writeNil();
		} else {
			writeInteger(i.intValue());
		}
	}

	// Native types

}
