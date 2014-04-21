package org.dedee.messagepax.tests;

import java.io.IOException;

import org.dedee.messagepax.MessagePaxDeserializer;
import org.msgpack.MessagePack;
import org.msgpack.packer.BufferPacker;
import org.msgpack.unpacker.BufferUnpacker;

public class PerfTest {

	public static byte[] generate(int rounds) throws IOException {
		// Use Java MSGPACK impl
		MessagePack msgpack = new MessagePack();
		BufferPacker packer = msgpack.createBufferPacker();

		for (int i = 0; i < rounds; i++) {
			packer.write(Integer.valueOf(i));
		}
		for (int i = 0; i < rounds; i++) {
			packer.write("Hello World " + i);
		}
		byte[] buf = new byte[256];
		for (int i = 0; i < rounds; i++) {
			packer.write(buf);
		}
		for (int i = 0; i < rounds; i++) {
			packer.write(Boolean.valueOf(i % 2 == 0));
		}

		byte[] b2 = packer.toByteArray();
		return b2;
	}

	public static void main(String[] args) throws IOException {

		int rounds[] = new int[] { 100, 1000, 10000, 100000, 1000000 };

		for (int max : rounds) {
			byte[] b = generate(max);

			System.out.println("------------------- " + b.length + " bytes");
			System.out.println(max);

			System.gc();
			{
				long t1 = System.nanoTime();
				MessagePaxDeserializer d = new MessagePaxDeserializer(b);

				for (int i = 0; i < max; i++)
					d.readInteger();
				for (int i = 0; i < max; i++)
					d.readString();
				for (int i = 0; i < max; i++)
					d.readByteArray();
				for (int i = 0; i < max; i++)
					d.readBoolean();

				long t2 = System.nanoTime();
				double dt = (t2 - t1) / 1000000d;
				System.out.println(String.format("%3.03f", dt) + " ms");
			}

			System.gc();
			{
				long t1 = System.nanoTime();
				MessagePack msgpack = new MessagePack();
				BufferUnpacker unpacker = msgpack.createBufferUnpacker(b);

				for (int i = 0; i < max; i++)
					unpacker.readInt();
				for (int i = 0; i < max; i++)
					unpacker.readString();
				for (int i = 0; i < max; i++)
					unpacker.readByteArray();
				for (int i = 0; i < max; i++)
					unpacker.readBoolean();
				long t2 = System.nanoTime();
				double dt = (t2 - t1) / 1000000d;
				System.out.println(String.format("%3.03f", dt) + " ms");
			}

		}
	}
}
