package org.ubjson.io;

import java.io.IOException;

import junit.framework.Assert;

import org.junit.Test;
import org.ubjson.io.ByteArrayOutputStream;

public class ByteArrayOutputStreamTest {
	@Test
	public void testInitialCapacity() {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		Assert.assertEquals(8192, out.getArray().length);

		out = new ByteArrayOutputStream(1);
		Assert.assertEquals(1, out.getArray().length);

		out = new ByteArrayOutputStream(Short.MAX_VALUE);
		Assert.assertEquals(Short.MAX_VALUE, out.getArray().length);
	}

	@Test
	public void testEnsureCapacity() throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream(1);

		out.write(1);
		Assert.assertEquals(1, out.getArray().length);

		out.write(1);
		Assert.assertEquals(2, out.getArray().length);

		out.write(1);
		Assert.assertEquals(3, out.getArray().length);

		byte[] b = createByteArray(125);
		out.write(b);
		Assert.assertEquals(128, out.getArray().length);

		b = createByteArray(256);
		out.write(b, 0, 128);
		Assert.assertEquals(256, out.getArray().length);

		byte[] array = out.getArray();

		for (int i = 0; i < array.length; i++)
			Assert.assertEquals(1, array[i]);
	}

	byte[] createByteArray(int size) {
		byte[] b = new byte[size];

		for (int i = 0; i < b.length; i++)
			b[i] = 1;

		return b;
	}
}