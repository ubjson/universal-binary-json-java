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

	protected byte peek;
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

	public byte nextType() throws IOException {
		/*
		 * Keep reading bytes from the underlying stream as long as we are
		 * seeing NOOP bytes or stop as soon as we hit the end of the stream.
		 * 
		 * Put another way, keep reading until we find a valid marker byte value
		 * or hit the end of stream.
		 */
		while ((peek = (byte) in.read()) != -1 && peek == NOOP)
			;

		return peek;
	}

	public void readEnd() throws IOException, DataFormatException {
		ensureType("END", END, INVALID);
		peek = -1;
	}

	public void readNull() throws IOException, DataFormatException {
		ensureType("NULL", NULL, INVALID);
		peek = -1;
	}

	public boolean readBoolean() throws IOException, DataFormatException {
		byte type = ensureType("BOOLEAN", TRUE, FALSE);
		peek = -1;
		return (type == TRUE ? true : false);
	}

	public byte readByte() throws IOException, DataFormatException {
		ensureType("BYTE", BYTE, INVALID);
		peek = -1;
		return (byte) in.read();
	}

	public short readInt16() throws IOException, DataFormatException {
		ensureType("INT16", INT16, INVALID);
		peek = -1;
		return readInt16Impl();
	}

	public int readInt32() throws IOException, DataFormatException {
		ensureType("INT32", INT32, INVALID);
		peek = -1;
		return readInt32Impl();
	}

	public long readInt64() throws IOException, DataFormatException {
		ensureType("INT64", INT64, INVALID);
		peek = -1;
		return readInt64Impl();
	}

	public float readFloat() throws IOException, DataFormatException {
		ensureType("FLOAT", FLOAT, INVALID);
		peek = -1;
		return Float.intBitsToFloat(readInt32Impl());
	}

	public double readDouble() throws IOException, DataFormatException {
		ensureType("DOUBLE", DOUBLE, INVALID);
		peek = -1;
		return Double.longBitsToDouble(readInt64Impl());
	}

	public byte[] readHugeAsBytes() throws IOException, DataFormatException {
		byte type = ensureType("HUGE", HUGE_COMPACT, HUGE);
		peek = -1;
		
		int byteLength = 0;

		switch (type) {
		case HUGE_COMPACT:
			byteLength = in.read();
			break;

		case HUGE:
			byteLength = readInt32Impl();
			break;
		}

		if (byteLength < 0)
			throw new DataFormatException(
					"Encountered a negative (invalid) length of ["
							+ byteLength
							+ "] specified for the HUGE value. Length must be >= 0.");

		int read = 0;
		int total = 0;
		byte[] buffer = new byte[byteLength];

		/*
		 * Keep reading from the stream until we have collected all the bytes
		 * necessary to reconstitute the BigInteger.
		 */
		while ((read = in.read(buffer, total, byteLength - read)) > -1)
			total += read;

		// Make sure we got all the bytes we were promised.
		if (total < byteLength)
			throw new IOException(
					"End of Stream was encountered while trying to read all of the bytes representing this HUGE value ("
							+ byteLength
							+ " bytes). Only "
							+ total
							+ " bytes could be read.");

		return buffer;
	}

	public char[] readHugeAsChars() throws IOException, DataFormatException {
		byte type = ensureType("HUGE", HUGE_COMPACT, HUGE);
		peek = -1;

		int byteLength = 0;

		switch (type) {
		case HUGE_COMPACT:
			byteLength = in.read();
			break;

		case HUGE:
			byteLength = readInt32Impl();
			break;
		}

		if (byteLength < 0)
			throw new DataFormatException(
					"Encountered a negative (invalid) length of ["
							+ byteLength
							+ "] specified for the (numeric) HUGE value. Length must be >= 0.");

		return decoder.decode(in, byteLength);
	}

	public BigInteger readHugeAsBigInteger() throws IOException,
			DataFormatException {
		return new BigInteger(readHugeAsBytes());
	}

	public BigDecimal readHugeAsBigDecimal() throws IOException,
			DataFormatException {
		return new BigDecimal(readHugeAsChars());
	}

	public String readString() throws IOException, DataFormatException {
		return new String(readStringAsChars());
	}

	public char[] readStringAsChars() throws IOException, DataFormatException {
		byte type = ensureType("STRING", STRING_COMPACT, STRING);
		peek = -1;

		int byteLength = 0;

		switch (type) {
		case STRING_COMPACT:
			byteLength = in.read();
			break;

		case STRING:
			byteLength = readInt32Impl();
			break;
		}

		if (byteLength < 0)
			throw new DataFormatException(
					"Encountered a negative (invalid) length of ["
							+ byteLength
							+ "] specified for the STRING value. Length must be >= 0.");

		return decoder.decode(in, byteLength);
	}

	public int readArrayLength() throws IOException, DataFormatException {
		byte type = ensureType("ARRAY", ARRAY_COMPACT, ARRAY);
		peek = -1;

		int count = 0;

		switch (type) {
		case ARRAY_COMPACT:
			count = in.read();

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
			throw new DataFormatException(
					"Encountered a negative (invalid) length of ["
							+ count
							+ "] specified for the ARRAY value. Length must be >= 0.");

		return count;
	}

	public int readObjectLength() throws IOException, DataFormatException {
		byte type = ensureType("OBJECT", OBJECT_COMPACT, OBJECT);
		peek = -1;

		int count = 0;

		switch (type) {
		case OBJECT_COMPACT:
			count = in.read();

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
			throw new DataFormatException(
					"Encountered a negative (invalid) length of ["
							+ count
							+ "] specified for the OBJECT value. Length must be >= 0.");

		return count;
	}

	protected short readInt16Impl() throws IOException {
		int read = in.read(buffer, 0, 2);

		if (read < 2)
			throw new DataFormatException(
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
		 * segments together (bit operation) and return the resulting number.
		 */
		return (short) (s1 | s2);
	}

	protected int readInt32Impl() throws IOException {
		int read = in.read(buffer, 0, 4);

		if (read < 4)
			throw new DataFormatException(
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
		int read = in.read(buffer);

		if (read < 8)
			throw new DataFormatException(
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

	protected byte ensureType(String typeLabel, byte expectedType,
			byte expectedTypeOpt) throws DataFormatException, IOException {
		/*
		 * Auto-peek at the next byte if necessary. This allows people to use
		 * the UBJInputStreams in a manual deserialization pattern of calling
		 * the read methods back to back with no intervening nextType() calls
		 * required to advance the state of the stream; we do it automatically
		 * for them here if they haven't done it yet.
		 */
		if (peek == -1)
			nextType();

		// Check if nextType is our expected or optionally expected type.
		if (peek != expectedType
				&& (expectedTypeOpt != -1 && peek != expectedTypeOpt)) {
			String message = "Unable to read " + typeLabel
					+ " value. The type marker read was byte " + peek
					+ " (char='" + ((char) peek)
					+ "') but the expected type marker byte was "
					+ expectedType + " (char='" + ((char) expectedType) + "')";

			// Append additional detail if optional expected type was given.
			if (expectedTypeOpt != -1)
				message += " or " + expectedTypeOpt + " (char='"
						+ ((char) expectedTypeOpt)
						+ "'); but neither were found.";

			throw new DataFormatException(message);
		}

		return peek;
	}
}