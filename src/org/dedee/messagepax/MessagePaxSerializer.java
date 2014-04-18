package org.dedee.messagepax;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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

	public void writeListString(List<String> list) throws IOException {
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

	public void writeMapStringString(Map<String, String> map)
			throws IOException {
		if (map == null) {
			writeNil();
		} else {
			int size = map.size();
			writeMapBegin(size);
			Iterator<String> it = map.keySet().iterator();
			for (int i = 0; i < size; i++) {
				String key = it.next();
				String val = map.get(key);
				writeString(key);
				writeString(val);
			}
		}
	}

}
