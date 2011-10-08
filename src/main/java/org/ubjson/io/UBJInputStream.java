/**   
 * Copyright 2011 The Buzz Media, LLC
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ubjson.io;

import static org.ubjson.io.IDataType.ARRAY;
import static org.ubjson.io.IDataType.ARRAY_COMPACT;
import static org.ubjson.io.IDataType.BYTE;
import static org.ubjson.io.IDataType.DOUBLE;
import static org.ubjson.io.IDataType.FLOAT;
import static org.ubjson.io.IDataType.HUGE;
import static org.ubjson.io.IDataType.HUGE_COMPACT;
import static org.ubjson.io.IDataType.INT16;
import static org.ubjson.io.IDataType.INT32;
import static org.ubjson.io.IDataType.INT64;
import static org.ubjson.io.IDataType.OBJECT;
import static org.ubjson.io.IDataType.OBJECT_COMPACT;
import static org.ubjson.io.IDataType.STRING;
import static org.ubjson.io.IDataType.STRING_COMPACT;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;

import org.ubjson.io.charset.StreamDecoder;

/*
 * TODO: Break the nextType into a separate specialized stream that extends this
 * one that provides a pull-parser style parsing use to this.
 * 
 * Otherwise make this class all manual-call like default JDK streams.
 */
public class UBJInputStream extends FilterInputStream {
	private byte type;

	private byte[] int16Buffer;
	private byte[] int32Buffer;
	private byte[] int64Buffer;

	private StreamDecoder decoder;

	public UBJInputStream(InputStream in) {
		super(in);

		type = -1;

		int16Buffer = new byte[2];
		int32Buffer = new byte[4];
		int64Buffer = new byte[8];

		decoder = new StreamDecoder();
	}

	@Override
	public int read() throws IOException {
		return in.read();
	}

	@Override
	public int read(byte[] b) throws IOException {
		return in.read(b);
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		return in.read(b, off, len);
	}

	@Override
	public long skip(long n) throws IOException {
		return in.skip(n);
	}

	@Override
	public int available() throws IOException {
		return in.available();
	}

	@Override
	public void close() throws IOException {
		in.close();
	}

	@Override
	public synchronized void mark(int readlimit) {
		in.mark(readlimit);
	}

	@Override
	public synchronized void reset() throws IOException {
		in.reset();
	}

	@Override
	public boolean markSupported() {
		return in.markSupported();
	}

	public byte nextType() throws IOException {
		return (type = (byte) in.read());
	}

	public byte readByte() throws IOException, DataFormatException {
		checkType("BYTE", BYTE);
		byte b = (byte) in.read();

		type = -1;
		return b;
	}

	public short readInt16() throws IOException, DataFormatException {
		checkType("INT16", INT16);
		short s = readInt16Impl();

		type = -1;
		return s;
	}

	public int readInt32() throws IOException, DataFormatException {
		checkType("INT32", INT32);
		int i = readInt32Impl();

		type = -1;
		return i;
	}

	public long readInt64() throws IOException, DataFormatException {
		checkType("INT64", INT64);
		long l = readInt64Impl();

		type = -1;
		return l;
	}

	public float readFloat() throws IOException, DataFormatException {
		checkType("FLOAT", FLOAT);
		float f = Float.intBitsToFloat(readInt32Impl());

		type = -1;
		return f;
	}

	public double readDouble() throws IOException, DataFormatException {
		checkType("DOUBLE", DOUBLE);
		double d = Double.longBitsToDouble(readInt64Impl());

		type = -1;
		return d;
	}

	public BigDecimal readHuge() throws IOException, DataFormatException {
		checkTypes("HUGE", HUGE_COMPACT, HUGE);

		int byteLength = 0;

		switch (type) {
		case HUGE:
			byteLength = readInt32Impl();
			break;

		case HUGE_COMPACT:
			byteLength = in.read();
			break;
		}

		type = -1;

		if (byteLength < 0)
			throw new DataFormatException(
					"Encountered an invalid (negative) length of ["
							+ byteLength
							+ "] specified for the (numeric) HUGE value. Length must be >= 0.");

		return new BigDecimal(decoder.decode(in, byteLength));
	}

	public String readString() throws IOException, DataFormatException {
		return new String(readStringAsChars());
	}

	public char[] readStringAsChars() throws IOException, DataFormatException {
		checkTypes("STRING", STRING_COMPACT, STRING);

		int byteLength = 0;

		switch (type) {
		case STRING:
			byteLength = readInt32Impl();
			break;

		case STRING_COMPACT:
			byteLength = in.read();
			break;
		}

		type = -1;

		if (byteLength < 0)
			throw new DataFormatException(
					"Encountered an invalid (negative) length of ["
							+ byteLength
							+ "] specified for the STRING value. Length must be >= 0.");

		return decoder.decode(in, byteLength);
	}

	public int readArrayLength() throws IOException, DataFormatException {
		checkTypes("ARRAY", ARRAY_COMPACT, ARRAY);

		int count = 0;

		switch (type) {
		case ARRAY:
			count = readInt32Impl();
			break;

		case ARRAY_COMPACT:
			count = in.read();
			break;
		}

		type = -1;

		if (count < 0)
			throw new DataFormatException(
					"Encountered an invalid (negative) length of ["
							+ count
							+ "] specified for the ARRAY value. Length must be >= 0.");

		return count;
	}

	public int readObjectLength() throws IOException, DataFormatException {
		checkTypes("OBJECT", OBJECT_COMPACT, OBJECT);

		int count = 0;

		switch (type) {
		case OBJECT:
			count = readInt32Impl();
			break;

		case OBJECT_COMPACT:
			count = in.read();
			break;
		}

		type = -1;

		if (count < 0)
			throw new DataFormatException(
					"Encountered an invalid (negative) length of ["
							+ count
							+ "] specified for the OBJECT value. Length must be >= 0.");

		return count;
	}

	protected short readInt16Impl() throws IOException {
		int read = in.read(int16Buffer);

		if (read < int16Buffer.length)
			throw new DataFormatException(
					"Attempted to read "
							+ int16Buffer.length
							+ " bytes to reconstruct the INT16 value, instead was only able to read "
							+ read + " bytes.");

		return (short) (((int) int16Buffer[0] << 8) + ((int) int16Buffer[1] << 0));
	}

	protected int readInt32Impl() throws IOException {
		int read = in.read(int32Buffer);

		if (read < int32Buffer.length)
			throw new DataFormatException(
					"Attempted to read "
							+ int32Buffer.length
							+ " bytes to reconstruct the INT32 value, instead was only able to read "
							+ read + " bytes.");

		return (((int) int32Buffer[0] << 24) + ((int) int32Buffer[1] << 16)
				+ ((int) int32Buffer[2] << 8) + ((int) int32Buffer[3] << 0));
	}

	protected long readInt64Impl() throws IOException {
		int read = in.read(int64Buffer);

		if (read < int64Buffer.length)
			throw new DataFormatException(
					"Attempted to read "
							+ int64Buffer.length
							+ " bytes to reconstruct the INT64 value, instead was only able to read "
							+ read + " bytes.");

		return (((long) int64Buffer[0] << 56)
				+ (((long) int64Buffer[1] & 255) << 48)
				+ (((long) int64Buffer[2] & 255) << 40)
				+ (((long) int64Buffer[3] & 255) << 32)
				+ (((long) int64Buffer[4] & 255) << 24)
				+ (((long) int64Buffer[5] & 255) << 16)
				+ (((long) int64Buffer[6] & 255) << 8) + (((long) int64Buffer[7] & 255) << 0));
	}

	private void checkType(String typeLabel, byte expectedType)
			throws DataFormatException, IOException {
		/*
		 * Auto-peek at the next byte if necessary. This allows people to use
		 * the UBJ streams in a manual serial/deserialization pattern of calling
		 * the read/write methods back to back with no intervening nextType()
		 * calls required.
		 */
		if (type == -1)
			nextType();

		if (type != expectedType)
			throw new DataFormatException("Unable to read " + typeLabel
					+ " value. The last type read was " + type + " (char='"
					+ ((char) type) + "') but the expected type was "
					+ expectedType + " (char='" + ((char) expectedType) + "').");
	}

	private void checkTypes(String typeLabel, byte expectedType1,
			byte expectedType2) throws DataFormatException, IOException {
		/*
		 * Auto-peek at the next byte if necessary. This allows people to use
		 * the UBJ streams in a manual serial/deserialization pattern of calling
		 * the read/write methods back to back with no intervening nextType()
		 * calls required.
		 */
		if (type == -1)
			nextType();

		if (type != expectedType1 && type != expectedType2)
			throw new DataFormatException("Unable to read " + typeLabel
					+ " value. The last type read was " + type + " (char='"
					+ ((char) type) + "') but the expected types were "
					+ expectedType1 + " (char='" + ((char) expectedType1)
					+ "') or " + expectedType2 + " (char='"
					+ ((char) expectedType2) + "'); neither were found.");
	}
}