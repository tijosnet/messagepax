package org.dedee.messagepax.tests;

import junit.framework.TestCase;

import org.dedee.messagepax.MessagePaxSerializer;

public class TestMessagePaxSerializer extends TestCase {

	private byte[] buf = new byte[256];

	public void testNil() throws Exception {
		MessagePaxSerializer s = new MessagePaxSerializer(buf);
		s.writeNil();
		assertEquals("C0", s.toHexString());
	}

	public void testBoolean() throws Exception {
		MessagePaxSerializer s = new MessagePaxSerializer(buf);
		s.writeBoolean(null);
		s.writeBoolean(false);
		s.writeBoolean(true);
		assertEquals("C0C2C3", s.toHexString());
	}

	public void testByte() throws Exception {
		MessagePaxSerializer s = new MessagePaxSerializer(buf);
		s.writeByte(null);
		s.writeByte(0);
		s.writeByte(1);
		s.writeByte(-32);
		s.writeByte(-64);
		assertEquals("C00001E0D0C0", s.toHexString());
	}

	public void testPositiveIntSmall() throws Exception {
		MessagePaxSerializer s = new MessagePaxSerializer(buf);
		s.writeInteger(null);
		s.writeInteger(0);
		s.writeInteger(1);
		s.writeInteger(0x7f);
		assertEquals("C000017F", s.toHexString());
	}

	public void testPositiveIntFF() throws Exception {
		MessagePaxSerializer s = new MessagePaxSerializer(buf);
		s.writeInteger(0xff);
		assertEquals("CCFF", s.toHexString());
	}

	public void testPositiveInt16() throws Exception {
		MessagePaxSerializer s = new MessagePaxSerializer(buf);
		s.writeInteger(0xffff);
		s.writeInteger(0xaaaa);
		assertEquals("CDFFFFCDAAAA", s.toHexString());
	}

	public void testPositiveInt32() throws Exception {
		MessagePaxSerializer s = new MessagePaxSerializer(buf);
		s.writeInteger(0x7fffffff);
		s.writeInteger(0xaaaaaaaa);
		assertEquals("CE7FFFFFFFD2AAAAAAAA", s.toHexString());
	}

	public void testNegativeIntSmall() throws Exception {
		MessagePaxSerializer s = new MessagePaxSerializer(buf);
		s.writeInteger(-1);
		s.writeInteger(-31);
		assertEquals("FFE1", s.toHexString());
	}

}
