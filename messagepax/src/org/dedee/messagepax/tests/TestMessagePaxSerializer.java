package org.dedee.messagepax.tests;

import java.io.ByteArrayOutputStream;

import junit.framework.TestCase;

import org.dedee.messagepax.MessagePaxSerializer;

public class TestMessagePaxSerializer extends TestCase {

	public void testNil() throws Exception {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		MessagePaxSerializer s = new MessagePaxSerializer(os);
		s.writeNil();
		byte[] b = os.toByteArray();
		assertNotNull(b);
		assertEquals(1, b.length);
		assertEquals(0xc0, b[0] & 0xff);
	}

	public void testBoolean() throws Exception {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		MessagePaxSerializer s = new MessagePaxSerializer(os);
		s.writeBoolean(null);
		s.writeBoolean(false);
		s.writeBoolean(true);
		byte[] b = os.toByteArray();
		assertNotNull(b);
		assertEquals(3, b.length);
		assertEquals(0xc0, b[0] & 0xff);
		assertEquals(0xc2, b[1] & 0xff);
		assertEquals(0xc3, b[2] & 0xff);
	}

	public void testPositiveIntSmall() throws Exception {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		MessagePaxSerializer s = new MessagePaxSerializer(os);
		s.writeInteger(null);
		s.writeInteger(0);
		s.writeInteger(1);
		s.writeInteger(0x7f);
		byte[] b = os.toByteArray();
		assertNotNull(b);
		assertEquals(4, b.length);
		assertEquals(0xc0, b[0] & 0xff);
		assertEquals(0x00, b[1] & 0xff);
		assertEquals(0x01, b[2] & 0xff);
		assertEquals(0x7f, b[3] & 0xff);
	}

	public void testPositiveIntFF() throws Exception {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		MessagePaxSerializer s = new MessagePaxSerializer(os);
		s.writeInteger(0xff);
		byte[] b = os.toByteArray();
		assertNotNull(b);
		assertEquals(2, b.length);
		assertEquals(0xcc, b[0] & 0xff);
		assertEquals(0xff, b[1] & 0xff);
	}

	public void testPositiveIntUint16() throws Exception {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		MessagePaxSerializer s = new MessagePaxSerializer(os);
		s.writeInteger(0xffff);
		s.writeInteger(0xaaaa);
		byte[] b = os.toByteArray();
		assertNotNull(b);
		assertEquals(6, b.length);
		assertEquals(0xcd, b[0] & 0xff);
		assertEquals(0xff, b[1] & 0xff);
		assertEquals(0xff, b[2] & 0xff);
		assertEquals(0xcd, b[3] & 0xff);
		assertEquals(0xaa, b[4] & 0xff);
		assertEquals(0xaa, b[5] & 0xff);
	}

	public void testPositiveIntUint32() throws Exception {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		MessagePaxSerializer s = new MessagePaxSerializer(os);
		s.writeInteger(0xffff);
		s.writeInteger(0xaaaa);
		byte[] b = os.toByteArray();
		assertNotNull(b);
		assertEquals(6, b.length);
		assertEquals(0xcd, b[0] & 0xff);
		assertEquals(0xff, b[1] & 0xff);
		assertEquals(0xff, b[2] & 0xff);
		assertEquals(0xcd, b[3] & 0xff);
		assertEquals(0xaa, b[4] & 0xff);
		assertEquals(0xaa, b[5] & 0xff);
	}

	public void testNegativeIntSmall() throws Exception {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		MessagePaxSerializer s = new MessagePaxSerializer(os);
		s.writeInteger(-1);
		s.writeInteger(-31);
		byte[] b = os.toByteArray();
		assertNotNull(b);
		assertEquals(2, b.length);
		assertEquals(0xe1, b[0] & 0xff);
		assertEquals(0xff, b[1] & 0xff);
	}

}
