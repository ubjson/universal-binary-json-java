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

import static org.ubjson.IConstants.*;
import static org.ubjson.io.IMarker.*;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharsetDecoder;

public class UBJInputStream extends FilterInputStream {
	private byte peek;
	private boolean empty;

	private byte[] int32Buffer;
	private byte[] int64Buffer;

	private byte[] buffer;
	private CharsetDecoder decoder;

	public UBJInputStream(InputStream in) {
		super(in);

		peek = INVALID;
		empty = false;

		int32Buffer = new byte[4];
		int64Buffer = new byte[8];

		/*
		 * REMINDER: This is not the entire read buffer, this is just the local
		 * buffer used by this class to pull content from the InputStream. The
		 * InputStream, if the user wants buffering, should be a
		 * BufferedInputStream instance with a user-configured buffer from the
		 * primary source.
		 */
		buffer = new byte[16384];
		decoder = UTF_8_CHARSET.newDecoder();
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

	public byte nextMarker() throws IOException {
		// Avoid IOException, once empty, always report empty.
		if (empty)
			return INVALID;

		peek = (byte) in.read();

		// Update empty state
		empty = (peek == INVALID);

		return peek;
	}
	
	public void readNoop() throws IOException, DataFormatException {
		verifyNextMarker(NOOP);
		peek = INVALID;
	}

	public void readNull() throws IOException, DataFormatException {
		verifyNextMarker(NULL);
		peek = INVALID;
	}

	public boolean readBoolean() throws IOException, DataFormatException {
		if (peek == INVALID)
			peek = (byte) in.read();

		boolean b;

		switch (peek) {
		case TRUE:
			b = true;
			break;

		case FALSE:
			b = false;
			break;

		default:
			throw new DataFormatException("Encountered a byte value of " + peek
					+ " instead of the expected byte value of " + TRUE + " or "
					+ FALSE + " (char='T' or 'F').");
		}

		peek = INVALID;
		return b;
	}

	public byte readByte() throws IOException, DataFormatException {
		verifyNextMarker(BYTE);
		peek = INVALID;
		return (byte) in.read();
	}

	public int readInt32() throws IOException, DataFormatException {
		verifyNextMarker(INT32);
		peek = INVALID;
		return readInt32Impl();
	}

	public long readInt64() throws IOException, DataFormatException {
		verifyNextMarker(INT64);
		peek = INVALID;
		return readInt64Impl();
	}

	public double readDouble() throws IOException, DataFormatException {
		verifyNextMarker(DOUBLE);
		peek = INVALID;
		return Double.longBitsToDouble(readInt64Impl());
	}

	public BigDecimal readHuge() throws IOException, DataFormatException {
		verifyNextMarker(HUGE);
		peek = INVALID;

		int length = readInt32Impl();

		if (length < 0)
			throw new DataFormatException("Read a negative length of ["
					+ length + "] for a huge value, but length must be >= 0.");

		return new BigDecimal(readStringAsChars(length));
	}

	public String readString() throws IOException, DataFormatException {
		return new String(readStringAsChars());
	}

	public char[] readStringAsChars() throws IOException, DataFormatException {
		verifyNextMarker(STRING);
		peek = INVALID;

		int length = readInt32Impl();

		if (length < 0)
			throw new DataFormatException("Read a negative length of ["
					+ length + "] for string value, but length must be >= 0.");

		return readStringAsChars(length);
	}

	public int readArrayHeader() throws IOException, DataFormatException {
		verifyNextMarker(ARRAY);
		peek = INVALID;
		return readInt32Impl();
	}

	public int readObjectHeader() throws IOException, DataFormatException {
		verifyNextMarker(OBJECT);
		peek = INVALID;
		return readInt32Impl();
	}

	protected int readInt32Impl() throws IOException {
		int read = in.read(int32Buffer);

		if (read < int32Buffer.length)
			throw new DataFormatException(
					"Attempted to read "
							+ int32Buffer.length
							+ " bytes to reconstruct the int32 value, instead was only able to read "
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
							+ " bytes to reconstruct the int64 value, instead was only able to read "
							+ read + " bytes.");

		return (((long) int64Buffer[0] << 56)
				+ (((long) int64Buffer[1] & 255) << 48)
				+ (((long) int64Buffer[2] & 255) << 40)
				+ (((long) int64Buffer[3] & 255) << 32)
				+ (((long) int64Buffer[4] & 255) << 24)
				+ (((long) int64Buffer[5] & 255) << 16)
				+ (((long) int64Buffer[6] & 255) << 8) + (((long) int64Buffer[7] & 255) << 0));
	}

	// TODO: This is not right, the length is the byte length not char length
	// this only works with ASCII.
	protected char[] readStringAsChars(int length) throws IOException {
		/*
		 * CharsetDecoder can only decode into a CharBuffer, so we wrap our
		 * return array with one, fill it up, then return the now-populated
		 * array with our decoded chars. 
		 */
		char[] text = new char[length];
		CharBuffer output = CharBuffer.wrap(text);

		int read = 0;
		int remaining = length;
		decoder.reset();

		while (remaining > 0
				&& (read = in.read(buffer, 0, remaining)) != INVALID) {
			// Update the remaining amount that needs to be read.
			remaining -= read;

			// Wrap and decode the resulting bytes.
			ByteBuffer temp = ByteBuffer.wrap(buffer, 0, read);

			if (remaining == 0)
				decoder.decode(temp, output, true);
			else
				decoder.decode(temp, output, false);
		}

		// Flush any pending content to the output array.
		decoder.flush(output);

		return text;
	}

	private void verifyNextMarker(byte expected) throws IOException,
			DataFormatException {
		if (peek == INVALID) {
			nextMarker();

			if (peek == INVALID)
				throw new IOException(
						"End of Stream encountered while trying to read the next ASCII value marker.");
		}

		if (peek != expected)
			throw new DataFormatException(
					"Encountered a byte value of "
							+ peek
							+ " (char='"
							+ ((char) peek)
							+ "') instead of the expected byte value "
							+ expected
							+ " (char='"
							+ ((char) expected)
							+ "') while parsing this stream of Universal Binary JSON data.");
	}
}