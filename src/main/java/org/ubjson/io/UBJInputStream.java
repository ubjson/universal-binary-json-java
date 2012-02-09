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

import static org.ubjson.io.IUBJTypeMarker.ARRAY;
import static org.ubjson.io.IUBJTypeMarker.ARRAY_COMPACT;
import static org.ubjson.io.IUBJTypeMarker.BYTE;
import static org.ubjson.io.IUBJTypeMarker.DOUBLE;
import static org.ubjson.io.IUBJTypeMarker.END;
import static org.ubjson.io.IUBJTypeMarker.FALSE;
import static org.ubjson.io.IUBJTypeMarker.FLOAT;
import static org.ubjson.io.IUBJTypeMarker.HUGE;
import static org.ubjson.io.IUBJTypeMarker.HUGE_COMPACT;
import static org.ubjson.io.IUBJTypeMarker.INT16;
import static org.ubjson.io.IUBJTypeMarker.INT32;
import static org.ubjson.io.IUBJTypeMarker.INT64;
import static org.ubjson.io.IUBJTypeMarker.NOOP;
import static org.ubjson.io.IUBJTypeMarker.NULL;
import static org.ubjson.io.IUBJTypeMarker.OBJECT;
import static org.ubjson.io.IUBJTypeMarker.OBJECT_COMPACT;
import static org.ubjson.io.IUBJTypeMarker.STRING;
import static org.ubjson.io.IUBJTypeMarker.STRING_COMPACT;
import static org.ubjson.io.IUBJTypeMarker.TRUE;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;

import org.ubjson.io.charset.StreamDecoder;

public class UBJInputStream extends FilterInputStream {
	protected static final byte INVALID = -1;

	protected long pos;
	protected byte[] buffer;
	protected StreamDecoder decoder;

	public UBJInputStream(InputStream in) {
		super(in);

		buffer = new byte[8];
		decoder = new StreamDecoder();
	}

	@Override
	public int available() throws IOException {
		return in.available();
	}

	@Override
	public int read() throws IOException {
		pos++;
		return in.read();
	}

	@Override
	public int read(byte[] buffer) throws IllegalArgumentException, IOException {
		if (buffer == null)
			throw new IllegalArgumentException("buffer cannot be null");

		return read(buffer, 0, buffer.length);
	}

	@Override
	public int read(byte[] buffer, int offset, int length)
			throws IllegalArgumentException, IOException {
		if (buffer == null)
			throw new IllegalArgumentException("buffer cannot be null");
		if (offset < 0 || length < 0 || (offset + length) > buffer.length)
			throw new IllegalArgumentException(
					"offset ["
							+ offset
							+ "] and length ["
							+ length
							+ "] must be >= 0 and (offset + length) must be <= buffer.lengt ["
							+ buffer.length + "]");

		int read = in.read(buffer, offset, length);
		pos += read;
		return read;
	}

	@Override
	public long skip(long amount) throws IllegalArgumentException, IOException {
		if (amount < 0)
			throw new IllegalArgumentException("amount [" + amount
					+ "] must be >= 0");

		long skipped = in.skip(amount);
		pos += skipped;
		return skipped;
	}

	@Override
	public boolean markSupported() {
		return in.markSupported();
	}

	@Override
	public void mark(int readLimit) throws IllegalArgumentException {
		if (readLimit < 0)
			throw new IllegalArgumentException("readLimit [" + readLimit
					+ "] must be >= 0");

		in.mark(readLimit);
	}

	@Override
	public void reset() throws IOException {
		in.reset();

		/*
		 * Technically we should do something to pos here (reset to 0, reset to
		 * the last marked position, etc.) but because we have no way of knowing
		 * how the underlying InputStream actually implements mark/reset and
		 * overflows, we have no way of knowing what "position" we are in it.
		 * 
		 * So instead we just never decrement pos and always increment it.
		 */
	}

	@Override
	public void close() throws IOException {
		in.close();
	}

	public long getPosition() {
		return pos;
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

	public Number readHuge() throws IOException, UBJFormatException {
		String huge = readHugeAsString();

		if (huge.indexOf('.') == -1)
			return new BigInteger(huge);
		else
			return new BigDecimal(huge);
	}

	public ByteBuffer readHugeAsBytes() throws IOException, UBJFormatException {
		ByteBuffer buffer = ByteBuffer.allocate(readHugeHeaderImpl());
		readHugeBodyAsBytesImpl(buffer.capacity(), buffer);

		return buffer;
	}

	public void readHugeAsBytes(ByteBuffer buffer)
			throws IllegalArgumentException, IOException, UBJFormatException {
		if (buffer == null)
			throw new IllegalArgumentException("buffer cannot be null");

		// Read bytes directly from stream into the provided buffer.
		readHugeBodyAsBytesImpl(readHugeHeaderImpl(), buffer);
	}

	public CharBuffer readHugeAsChars() throws IOException, UBJFormatException {
		CharBuffer buffer = CharBuffer.allocate(readHugeHeaderImpl());
		readHugeBodyAsCharsImpl(buffer.capacity(), buffer);

		return buffer;
	}

	public void readHugeAsChars(CharBuffer buffer)
			throws IllegalArgumentException, IOException, UBJFormatException {
		if (buffer == null)
			throw new IllegalArgumentException("buffer cannot be null");

		// Decode bytes directly from stream into the provided buffer.
		readHugeBodyAsCharsImpl(readHugeHeaderImpl(), buffer);
	}

	public String readHugeAsString() throws IOException, UBJFormatException {
		CharBuffer buffer = readHugeAsChars();
		return new String(buffer.array(), buffer.position(), buffer.remaining());
	}

	public String readString() throws IOException, UBJFormatException {
		CharBuffer buffer = readStringAsChars();
		return new String(buffer.array(), buffer.position(), buffer.remaining());
	}

	public ByteBuffer readStringAsBytes() throws IOException,
			UBJFormatException {
		ByteBuffer buffer = ByteBuffer.allocate(readStringHeaderImpl());
		readStringBodyAsBytesImpl(buffer.capacity(), buffer);

		return buffer;
	}

	public void readStringAsBytes(ByteBuffer buffer)
			throws IllegalArgumentException, IOException, UBJFormatException {
		if (buffer == null)
			throw new IllegalArgumentException("buffer cannot be null");

		// Read bytes directly from stream into the provided buffer.
		readStringBodyAsBytesImpl(readStringHeaderImpl(), buffer);
	}

	public CharBuffer readStringAsChars() throws IOException,
			UBJFormatException {
		CharBuffer buffer = CharBuffer.allocate(readStringHeaderImpl());
		readStringBodyAsCharsImpl(buffer.capacity(), buffer);

		return buffer;
	}

	public void readStringAsChars(CharBuffer buffer)
			throws IllegalArgumentException, IOException, UBJFormatException {
		if (buffer == null)
			throw new IllegalArgumentException("buffer cannot be null");

		// Decode bytes directly from stream into the provided buffer.
		readStringBodyAsCharsImpl(readStringHeaderImpl(), buffer);
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
					pos,
					"Encountered a negative (invalid) length of ["
							+ count
							+ "] specified for the ARRAY value at stream position "
							+ pos + ". Length must be >= 0.");

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
					pos,
					"Encountered a negative (invalid) length of ["
							+ count
							+ "] specified for the OBJECT value at stream position "
							+ pos + ". Length must be >= 0.");

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
			throws IOException, UBJFormatException {
		byte type = nextType();

		if (type != expected && (expectedOpt != INVALID && type != expectedOpt)) {
			String message = "Unable to read " + name
					+ " value at stream position " + pos
					+ ". The type marker byte value read was " + type
					+ " (char='" + ((char) type)
					+ "') but the expected type marker byte value was "
					+ expected + " (char='" + ((char) expected) + "')";

			if (expectedOpt != INVALID)
				message += " or " + expectedOpt + " (char='"
						+ ((char) expectedOpt) + "'); but neither were found.";
			else
				message += '.';

			throw new UBJFormatException(pos, message);
		}

		return type;
	}

	protected short readInt16Impl() throws IOException {
		int read = read(buffer, 0, 2);

		if (read < 2)
			throw new UBJFormatException(pos,
					"Attempted to read 2 bytes to reconstruct the INT16 value at stream position "
							+ pos + ", but was only able to read " + read
							+ " bytes from the underlying stream.");

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
			throw new UBJFormatException(pos,
					"Attempted to read 4 bytes to reconstruct the INT32 value at stream position "
							+ pos + ", but was only able to read " + read
							+ " bytes from the underlying stream.");

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
			throw new UBJFormatException(pos,
					"Attempted to read 8 bytes to reconstruct the INT64 value at stream position "
							+ pos + ", but was only able to read " + read
							+ " bytes from the underlying stream.");

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

	protected int readHugeHeaderImpl() throws IOException, UBJFormatException {
		// Ensure we are reading a HUGE or HUGE_COMPACT type.
		byte type = checkType("HUGE", HUGE_COMPACT, HUGE);
		int length = 0;

		// Get the length of the HUGE.
		switch (type) {
		case HUGE_COMPACT:
			length = read();
			break;

		case HUGE:
			length = readInt32Impl();
			break;
		}

		// Confirm the length is not negative.
		if (length < 0)
			throw new UBJFormatException(
					pos,
					"Encountered a negative (invalid) length of ["
							+ length
							+ "] specified for the HUGE value at stream position "
							+ pos + ". Length must be >= 0.");

		// Return the length of the HUGE to the caller.
		return length;
	}

	protected void readHugeBodyAsBytesImpl(int length, ByteBuffer buffer)
			throws IllegalArgumentException, IOException, UBJFormatException {
		if (buffer.capacity() < length)
			throw new IllegalArgumentException(
					"buffer capacity is "
							+ buffer.capacity()
							+ " but length of HUGE to be read is "
							+ length
							+ " bytes; destination buffer must be big enough to contain at least all the bytes for the HUGE value being read.");

		// Read the raw bytes from the stream directly into the backing byte[]
		int read = read(buffer.array(), 0, length);

		// Make sure we got all the bytes we were promised.
		if (read < length)
			throw new IOException(
					"The End-of-Stream was encountered at stream position "
							+ pos
							+ " while trying to read all of the bytes representing this HUGE value ("
							+ length + " bytes). Only " + read
							+ " bytes could be read.");

		/*
		 * Simulate the clear/put/flip ops for the wrapping ByteBuffer so the
		 * caller can read the result.
		 */
		buffer.position(0);
		buffer.limit(read);
	}

	protected void readHugeBodyAsCharsImpl(int length, CharBuffer buffer)
			throws IllegalArgumentException, IOException, UBJFormatException {
		if (buffer.capacity() < length)
			throw new IllegalArgumentException(
					"buffer capacity is "
							+ buffer.capacity()
							+ " but length of HUGE to be read is "
							+ length
							+ " bytes; destination buffer must be big enough to contain at least all the bytes for the HUGE value being read.");

		// Decode the HUGE from our byte stream into our CharBuffer.
		decoder.decode(in, length, buffer);

		// Prepare the buffer to be read by the caller.
		buffer.flip();
	}

	protected int readStringHeaderImpl() throws IOException, UBJFormatException {
		// Ensure we are reading a STRING or STRING_COMPACT type.
		byte type = checkType("STRING", STRING_COMPACT, STRING);
		int length = 0;

		// Get the length of the STRING.
		switch (type) {
		case STRING_COMPACT:
			length = read();
			break;

		case STRING:
			length = readInt32Impl();
			break;
		}

		// Confirm the length is not negative.
		if (length < 0)
			throw new UBJFormatException(
					pos,
					"Encountered a negative (invalid) length of ["
							+ length
							+ "] specified for the STRING value at stream position "
							+ pos + ". Length must be >= 0.");

		return length;
	}

	protected void readStringBodyAsBytesImpl(int length, ByteBuffer buffer)
			throws IllegalArgumentException, IOException, UBJFormatException {
		if (buffer.capacity() < length)
			throw new IllegalArgumentException(
					"buffer capacity is "
							+ buffer.capacity()
							+ " but length of STRING to be read is "
							+ length
							+ " bytes; destination buffer must be big enough to contain at least all the bytes for the HUGE value being read.");

		// Read the raw bytes from the stream directly into the backing byte[]
		int read = read(buffer.array(), 0, length);

		// Make sure we got all the bytes we were promised.
		if (read < length)
			throw new IOException(
					"The End-of-Stream was encountered at stream position "
							+ pos
							+ " while trying to read all of the bytes representing this STRING value ("
							+ length + " bytes). Only " + read
							+ " bytes could be read.");

		/*
		 * Simulate the clear/put/flip ops for the wrapping ByteBuffer so the
		 * caller can read the result.
		 */
		buffer.position(0);
		buffer.limit(read);
	}

	protected void readStringBodyAsCharsImpl(int length, CharBuffer buffer)
			throws IllegalArgumentException, IOException, UBJFormatException {
		if (buffer.capacity() < length)
			throw new IllegalArgumentException(
					"buffer capacity is "
							+ buffer.capacity()
							+ " but length of STRING to be read is "
							+ length
							+ " bytes; destination buffer must be big enough to contain at least all of the bytes for the STRING value being read.");

		// Decode the STRING from our byte stream to our CharBuffer.
		decoder.decode(in, length, buffer);

		// Prepare the buffer to be read by the caller.
		buffer.flip();
	}
}