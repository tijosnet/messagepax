package org.dedee.messagepax;

import java.io.IOException;
import java.util.List;

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

	// public void writeByte(Byte i) throws IOException {
	// if (i == null) {
	// writeNil();
	// } else {
	// writeInteger(i.intValue() & 0xff);
	// }
	// }

	public void writeInteger(Integer i) throws IOException {
		if (i == null) {
			writeNil();
		} else {
			writeInteger(i.intValue());
		}
	}

	public void writeLong(Long l) throws IOException {
		if (l == null) {
			writeNil();
		} else {
			writeLong(l.longValue());
		}

	}

	public void writeString(String s) throws IOException {
		byte[] buffer = null;
		int len = 0;
		if (s != null) {
			buffer = s.getBytes("UTF8");
			len = buffer.length;
		}
		writeByteArray(buffer, 0, len);
	}

	public void writeList(List<String> list) throws IOException {
		if (list == null) {
			writeNil();
		} else {
			int size = list.size();
			writeListBegin(size);
			for (int i = 0; i < size; i++) {
				writeString(list.get(i));
			}
		}
	}

}
