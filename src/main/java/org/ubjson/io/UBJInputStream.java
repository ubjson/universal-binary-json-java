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

import static org.ubjson.io.ITypeMarker.ARRAY;
import static org.ubjson.io.ITypeMarker.ARRAY_COMPACT;
import static org.ubjson.io.ITypeMarker.BYTE;
import static org.ubjson.io.ITypeMarker.DOUBLE;
import static org.ubjson.io.ITypeMarker.END;
import static org.ubjson.io.ITypeMarker.FALSE;
import static org.ubjson.io.ITypeMarker.FLOAT;
import static org.ubjson.io.ITypeMarker.HUGE;
import static org.ubjson.io.ITypeMarker.HUGE_COMPACT;
import static org.ubjson.io.ITypeMarker.INT16;
import static org.ubjson.io.ITypeMarker.INT32;
import static org.ubjson.io.ITypeMarker.INT64;
import static org.ubjson.io.ITypeMarker.NOOP;
import static org.ubjson.io.ITypeMarker.NULL;
import static org.ubjson.io.ITypeMarker.OBJECT;
import static org.ubjson.io.ITypeMarker.OBJECT_COMPACT;
import static org.ubjson.io.ITypeMarker.STRING;
import static org.ubjson.io.ITypeMarker.STRING_COMPACT;
import static org.ubjson.io.ITypeMarker.TRUE;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;

import org.ubjson.io.charset.StreamDecoder;

public class UBJInputStream extends FilterInputStream {
	private static final byte INVALID = -1;

	protected byte[] buffer;
	protected StreamDecoder decoder;

	public UBJInputStream(InputStream in) {
		super(in);

		buffer = new byte[8];
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

	public void readEnd() throws IOException, UBJFormatException {
		checkType("END", END, INVALID);
	}

	public void readNull() throws IOException, UBJFormatException {
		checkType("NULL", NULL, INVALID);
	}

	public boolean readBoolean() throws IOException, UBJFormatException {
		byte type = checkType("BOOLEAN", TRUE, FALSE);
		return (type == TRUE ? true : false);
	}

	public byte readByte() throws IOException, UBJFormatException {
		checkType("BYTE", BYTE, INVALID);
		return (byte) read();
	}

	public short readInt16() throws IOException, UBJFormatException {
		checkType("INT16", INT16, INVALID);
		return readInt16Impl();
	}

	public int readInt32() throws IOException, UBJFormatException {
		checkType("INT32", INT32, INVALID);
		return readInt32Impl();
	}

	public long readInt64() throws IOException, UBJFormatException {
		checkType("INT64", INT64, INVALID);
		return readInt64Impl();
	}

	public float readFloat() throws IOException, UBJFormatException {
		checkType("FLOAT", FLOAT, INVALID);
		return Float.intBitsToFloat(readInt32Impl());
	}

	public double readDouble() throws IOException, UBJFormatException {
		checkType("DOUBLE", DOUBLE, INVALID);
		return Double.longBitsToDouble(readInt64Impl());
	}

	public byte[] readHugeAsBytes() throws IOException, UBJFormatException {
		byte type = checkType("HUGE", HUGE_COMPACT, HUGE);
		int length = 0;

		switch (type) {
		case HUGE_COMPACT:
			length = read();
			break;

		case HUGE:
			length = readInt32Impl();
			break;
		}

		if (length < 0)
			throw new UBJFormatException(
					"Encountered a negative (invalid) length of ["
							+ length
							+ "] specified for the HUGE value. Length must be >= 0.");

		int read = 0;
		int total = 0;
		byte[] buffer = new byte[length];

		/*
		 * Keep reading from the stream until we have collected all the bytes
		 * necessary to reconstitute the BigInteger.
		 */
		while ((read = read(buffer, total, length - read)) > -1)
			total += read;

		// Make sure we got all the bytes we were promised.
		if (total < length)
			throw new IOException(
					"End of Stream was encountered while trying to read all of the bytes representing this HUGE value ("
							+ length
							+ " bytes). Only "
							+ total
							+ " bytes could be read.");

		return buffer;
	}

	public char[] readHugeAsChars() throws IOException, UBJFormatException {
		byte type = checkType("HUGE", HUGE_COMPACT, HUGE);
		int length = 0;

		switch (type) {
		case HUGE_COMPACT:
			length = read();
			break;

		case HUGE:
			length = readInt32Impl();
			break;
		}

		if (length < 0)
			throw new UBJFormatException(
					"Encountered a negative (invalid) length of ["
							+ length
							+ "] specified for the (numeric) HUGE value. Length must be >= 0.");

		return decoder.decode(in, length);
	}

	public BigInteger readHugeAsBigInteger() throws IOException,
			UBJFormatException {
		return new BigInteger(readHugeAsBytes());
	}

	public BigDecimal readHugeAsBigDecimal() throws IOException,
			UBJFormatException {
		return new BigDecimal(readHugeAsChars());
	}

	public String readString() throws IOException, UBJFormatException {
		return new String(readStringAsChars());
	}

	public char[] readStringAsChars() throws IOException, UBJFormatException {
		byte type = checkType("STRING", STRING_COMPACT, STRING);
		int length = 0;

		switch (type) {
		case STRING_COMPACT:
			length = read();
			break;

		case STRING:
			length = readInt32Impl();
			break;
		}

		if (length < 0)
			throw new UBJFormatException(
					"Encountered a negative (invalid) length of ["
							+ length
							+ "] specified for the STRING value. Length must be >= 0.");

		return decoder.decode(in, length);
	}

	public int readArrayLength() throws IOException, UBJFormatException {
		byte type = checkType("ARRAY", ARRAY_COMPACT, ARRAY);
		int count = 0;

		switch (type) {
		case ARRAY_COMPACT:
			count = read();

			/*
			 * Streaming Support: When using the 'a' marker, when a length of
			 * 255 is specified, that means an unbounded container that is
			 * terminated with an 'E' marker.
			 * 
			 * We use -1 to indicate this scenario to the caller.
			 */
			if (count == 255)
				count = -1;
			break;

		case ARRAY:
			count = readInt32Impl();
			break;
		}

		if (count < 0)
			throw new UBJFormatException(
					"Encountered a negative (invalid) length of ["
							+ count
							+ "] specified for the ARRAY value. Length must be >= 0.");

		return count;
	}

	public int readObjectLength() throws IOException, UBJFormatException {
		byte type = checkType("OBJECT", OBJECT_COMPACT, OBJECT);
		int count = 0;

		switch (type) {
		case OBJECT_COMPACT:
			count = read();

			/*
			 * Streaming Support: When using the 'o' marker, when a length of
			 * 255 is specified, that means an unbounded container that is
			 * terminated with an 'E' marker.
			 * 
			 * We use -1 to indicate this scenario to the caller.
			 */
			if (count == 255)
				count = -1;
			break;

		case OBJECT:
			count = readInt32Impl();
			break;
		}

		if (count < 0)
			throw new UBJFormatException(
					"Encountered a negative (invalid) length of ["
							+ count
							+ "] specified for the OBJECT value. Length must be >= 0.");

		return count;
	}

	protected byte nextType() throws IOException {
		byte b = INVALID;

		/*
		 * Keep reading bytes from the underlying stream as long as we are
		 * seeing NOOP bytes or stop as soon as we hit the end of the stream.
		 * 
		 * Put another way, keep reading until we find a valid marker byte value
		 * or hit the end of stream.
		 */
		while ((b = (byte) read()) != -1 && b == NOOP)
			;

		return b;
	}

	protected byte checkType(String name, byte expected, byte expectedOpt)
			throws UBJFormatException, IOException {
		byte type = nextType();

		if (type != expected && (expectedOpt != -1 && type != expectedOpt)) {
			String message = "Unable to read " + name
					+ " value. The type marker read was byte " + type
					+ " (char='" + ((char) type)
					+ "') but the expected type marker byte was " + expected
					+ " (char='" + ((char) expected) + "')";

			if (expectedOpt != -1)
				message += " or " + expectedOpt + " (char='"
						+ ((char) expectedOpt) + "'); but neither were found.";
			else
				message += '.';

			throw new UBJFormatException(message);
		}

		return type;
	}

	protected short readInt16Impl() throws IOException {
		int read = read(buffer, 0, 2);

		if (read < 2)
			throw new UBJFormatException(
					"Attempted to read 2 bytes to reconstruct the INT16 value, instead was only able to read "
							+ read + " bytes from the underlying stream.");

		/*
		 * We read in the original number in 1-byte (8-bit) segments,
		 * bit-shifting the significant bits back into their place in the final
		 * numeric type that will be returned.
		 * 
		 * NOTE: Bit shifts cannot be done on shorts, so instead of casting to
		 * shorts here, bit-shifting, then returning below, we have to keep the
		 * segments as ints while we bit-shift, then cast it in the return.
		 */
		int s1 = (int) (buffer[0] & 0xFF) << 8;
		int s2 = (int) (buffer[1] & 0xFF) << 0;

		/*
		 * Now that the significant bits of each segment of the number are where
		 * they should be, we re-create the original number by merging all the
		 * segments together (bit operation) then truncating the result to a
		 * 16-bit short. The truncation is not an issue as the 16-bits we care
		 * about are in their proper place (done in the step above); if there is
		 * anything else getting truncated it is data we didn't care about.
		 */
		return (short) (s1 | s2);
	}

	protected int readInt32Impl() throws IOException {
		int read = read(buffer, 0, 4);

		if (read < 4)
			throw new UBJFormatException(
					"Attempted to read 4 bytes to reconstruct the INT32 value, instead was only able to read "
							+ read + " bytes from the underlying stream.");

		/*
		 * We read in the original number in 1-byte (8-bit) segments,
		 * bit-shifting the significant bits back into their place in the final
		 * numeric type that will be returned.
		 */
		int i1 = (int) (buffer[0] & 0xFF) << 24;
		int i2 = (int) (buffer[1] & 0xFF) << 16;
		int i3 = (int) (buffer[2] & 0xFF) << 8;
		int i4 = (int) (buffer[3] & 0xFF) << 0;

		/*
		 * Now that the significant bits of each segment of the number are where
		 * they should be, we re-create the original number by merging all the
		 * segments together (bit operation) and return the resulting number.
		 */
		return (i1 | i2 | i3 | i4);
	}

	protected long readInt64Impl() throws IOException {
		int read = read(buffer);

		if (read < 8)
			throw new UBJFormatException(
					"Attempted to read 8 bytes to reconstruct the INT64 value, instead was only able to read "
							+ read + " bytes from the underlying stream.");

		/*
		 * We read in the original number in 1-byte (8-bit) segments,
		 * bit-shifting the significant bits back into their place in the final
		 * numeric type that will be returned.
		 */
		long l1 = (long) (buffer[0] & 0xFF) << 56;
		long l2 = (long) (buffer[1] & 0xFF) << 48;
		long l3 = (long) (buffer[2] & 0xFF) << 40;
		long l4 = (long) (buffer[3] & 0xFF) << 32;
		long l5 = (long) (buffer[4] & 0xFF) << 24;
		long l6 = (long) (buffer[5] & 0xFF) << 16;
		long l7 = (long) (buffer[6] & 0xFF) << 8;
		long l8 = (long) (buffer[7] & 0xFF) << 0;

		/*
		 * Now that the significant bits of each segment of the number are where
		 * they should be, we re-create the original number by merging all the
		 * segments together (bit operation) and return the resulting number.
		 */
		return (l1 | l2 | l3 | l4 | l5 | l6 | l7 | l8);
	}
}