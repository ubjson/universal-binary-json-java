package com.ubjson.io;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import junit.framework.Assert;

import org.junit.Test;
import org.ubjson.io.ByteArrayOutputStream;
import org.ubjson.io.UBJInputStream;
import org.ubjson.io.UBJOutputStream;

public class NumericTest {
	/**
	 * The range of values we test from MIN_VALUE up and from MAX_VALUE down, in
	 * addition to zero for every numeric type.
	 * 
	 * Range is 10,000,000 million; so 20 million numbers tested for int32, 64,
	 * float and double.
	 */
	private static int RANGE = 10000000;

	/**
	 * Constants used to define the byte-size of all the different numeric
	 * constructs.
	 */
	private static int SIZEOF_BYTE = 2;
	private static int SIZEOF_INT16 = 3;
	private static int SIZEOF_INT32 = 5;
	private static int SIZEOF_INT64 = 9;
	private static int SIZEOF_FLOAT = 5;
	private static int SIZEOF_DOUBLE = 9;

	private ByteArrayOutputStream data;

	@Test
	public void testByte() throws IOException {
		data = new ByteArrayOutputStream((Byte.MAX_VALUE + 1) * 2 * SIZEOF_BYTE);
		UBJOutputStream out = new UBJOutputStream(data);

		for (byte i = Byte.MIN_VALUE; i < Byte.MAX_VALUE; i++)
			out.writeByte(i);

		out.close();
		UBJInputStream in = new UBJInputStream(new ByteArrayInputStream(
				data.getArray()));

		for (byte i = Byte.MIN_VALUE; i < Byte.MAX_VALUE; i++)
			Assert.assertEquals(i, in.readByte());
	}

	@Test
	public void testInt16() throws IOException {
		data = new ByteArrayOutputStream((Short.MAX_VALUE + 1) * 2
				* SIZEOF_INT16);
		UBJOutputStream out = new UBJOutputStream(data);

		for (short i = Short.MIN_VALUE; i < Short.MAX_VALUE; i++)
			out.writeInt16(i);

		out.close();
		UBJInputStream in = new UBJInputStream(new ByteArrayInputStream(
				data.getArray()));

		for (short i = Short.MIN_VALUE; i < Short.MAX_VALUE; i++)
			Assert.assertEquals(i, in.readInt16());
	}

	@Test
	public void testInt32() throws IOException {
		data = new ByteArrayOutputStream(((RANGE * 2) + 1) * SIZEOF_INT32);
		UBJOutputStream out = new UBJOutputStream(data);

		// Write the COUNT most negative values.
		for (int i = Integer.MIN_VALUE; i < (Integer.MIN_VALUE + RANGE); i++)
			out.writeInt32(i);

		// Write 0
		out.writeInt32(0);

		// Write the COUNT most positive values.
		for (int i = Integer.MAX_VALUE - RANGE; i < Integer.MAX_VALUE; i++)
			out.writeInt32(i);

		out.close();
		UBJInputStream in = new UBJInputStream(new ByteArrayInputStream(
				data.getArray()));

		// Assert the COUNT most negative values.
		for (int i = Integer.MIN_VALUE; i < (Integer.MIN_VALUE + RANGE); i++)
			Assert.assertEquals(i, in.readInt32());

		// Assert 0
		Assert.assertEquals(0, in.readInt32());

		// Assert the COUNT most positive values.
		for (int i = Integer.MAX_VALUE - RANGE; i < Integer.MAX_VALUE; i++)
			Assert.assertEquals(i, in.readInt32());
	}

	@Test
	public void testInt64() throws IOException {
		data = new ByteArrayOutputStream(((RANGE * 2) + 1) * SIZEOF_INT64);
		UBJOutputStream out = new UBJOutputStream(data);

		// Write the COUNT most negative values.
		for (long i = Long.MIN_VALUE; i < (Long.MIN_VALUE + RANGE); i++)
			out.writeInt64(i);

		// Write 0
		out.writeInt64(0);

		// Write the COUNT most positive values.
		for (long i = Long.MAX_VALUE - RANGE; i < Long.MAX_VALUE; i++)
			out.writeInt64(i);

		out.close();
		UBJInputStream in = new UBJInputStream(new ByteArrayInputStream(
				data.getArray()));

		// Assert the COUNT most negative values.
		for (long i = Long.MIN_VALUE; i < (Long.MIN_VALUE + RANGE); i++)
			Assert.assertEquals(i, in.readInt64());

		// Assert 0
		Assert.assertEquals(0L, in.readInt64());

		// Assert the COUNT most positive values.
		for (long i = Long.MAX_VALUE - RANGE; i < Long.MAX_VALUE; i++)
			Assert.assertEquals(i, in.readInt64());
	}

	@Test
	public void testFloat() throws IOException {
		data = new ByteArrayOutputStream(((RANGE * 2) + 1) * SIZEOF_FLOAT);
		UBJOutputStream out = new UBJOutputStream(data);

		// Write the COUNT most negative values.
		for (float i = Float.MIN_VALUE; i < (Float.MIN_VALUE + RANGE); i++)
			out.writeFloat(i);

		// Write 0
		out.writeFloat(0);

		// Write the COUNT most positive values.
		for (float i = Float.MAX_VALUE - RANGE; i < Float.MAX_VALUE; i++)
			out.writeFloat(i);

		out.close();
		UBJInputStream in = new UBJInputStream(new ByteArrayInputStream(
				data.getArray()));

		// Assert the COUNT most negative values.
		for (float i = Float.MIN_VALUE; i < (Float.MIN_VALUE + RANGE); i++)
			Assert.assertEquals(i, in.readFloat());

		// Assert 0
		Assert.assertEquals(0.0f, in.readFloat());

		// Assert the COUNT most positive values.
		for (float i = Float.MAX_VALUE - RANGE; i < Float.MAX_VALUE; i++)
			Assert.assertEquals(i, in.readFloat());
	}

	@Test
	public void testDouble() throws IOException {
		data = new ByteArrayOutputStream(((RANGE * 2) + 1) * SIZEOF_DOUBLE);
		UBJOutputStream out = new UBJOutputStream(data);

		// Write the COUNT most negative values.
		for (double i = Double.MIN_VALUE; i < (Double.MIN_VALUE + RANGE); i++)
			out.writeDouble(i);

		// Write 0
		out.writeDouble(0);

		// Write the COUNT most positive values.
		for (double i = Double.MAX_VALUE - RANGE; i < Double.MAX_VALUE; i++)
			out.writeDouble(i);

		out.close();
		UBJInputStream in = new UBJInputStream(new ByteArrayInputStream(
				data.getArray()));

		// Assert the COUNT most negative values.
		for (double i = Double.MIN_VALUE; i < (Double.MIN_VALUE + RANGE); i++)
			Assert.assertEquals(i, in.readDouble());

		// Assert 0
		Assert.assertEquals(0.0d, in.readDouble());

		// Assert the COUNT most positive values.
		for (double i = Double.MAX_VALUE - RANGE; i < Double.MAX_VALUE; i++)
			Assert.assertEquals(i, in.readDouble());
	}
}