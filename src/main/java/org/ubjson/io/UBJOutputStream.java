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

import static org.ubjson.io.IConstants.*;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharsetEncoder;

public class UBJOutputStream extends FilterOutputStream implements IUBJOutput {
	private byte[] int32Buffer;
	private byte[] int64Buffer;

	private ByteBuffer output;
	private CharsetEncoder encoder;

	public UBJOutputStream(OutputStream out) {
		super(out);

		int32Buffer = new byte[5];
		int64Buffer = new byte[9];

		/*
		 * REMINDER: This is not the entire write buffer, this is just the local
		 * buffer used by this class to push content to the OutputStream. The
		 * OutputStream, if the user wants buffering, should be a
		 * BufferedOutputStream instance with a user-configured buffer for the
		 * primary destination.
		 */
		output = ByteBuffer.allocate(16384);
		encoder = UTF_8_CHARSET.newEncoder();
	}

	@Override
	public void write(int b) throws IOException {
		out.write(b);
	}

	@Override
	public void write(byte[] b) throws IOException {
		out.write(b);
	}

	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		out.write(b, off, len);
	}

	@Override
	public void flush() throws IOException {
		out.flush();
	}

	@Override
	public void close() throws IOException {
		try {
			out.flush();
		} catch (Exception e) {
			// no-op
		}

		out.close();
	}

	@Override
	public void writeNull() throws IOException {
		out.write(NULL);
	}

	@Override
	public void writeBoolean(boolean value) throws IOException {
		out.write(value ? TRUE : FALSE);
	}

	@Override
	public void writeByte(byte value) throws IOException {
		out.write(BYTE);
		out.write(value);
	}

	@Override
	public void writeInt32(int value) throws IOException {
		writeInt32(INT32, value);
	}

	@Override
	public void writeInt64(long value) throws IOException {
		writeInt64(INT64, value);
	}

	@Override
	public void writeDouble(double value) throws IOException {
		// IEEE 754 double precision floating point format (as long).
		writeInt64(DOUBLE, Double.doubleToLongBits(value));
	}

	@Override
	public void writeHuge(char[] huge) throws IllegalArgumentException,
			IOException {
		if (huge == null)
			throw new IllegalArgumentException("huge cannot be null.");

		writeHuge(huge, 0, huge.length);
	}

	@Override
	public void writeHuge(char[] huge, int index, int length)
			throws IllegalArgumentException, IOException {
		if (huge == null)
			throw new IllegalArgumentException("huge cannot be null");

		// Write header
		writeInt32(HUGE, length);

		// Write body
		writeCharBuffer(CharBuffer.wrap(huge, index, length));
	}

	@Override
	public void writeHuge(String huge) throws IllegalArgumentException,
			IOException {
		if (huge == null)
			throw new IllegalArgumentException("huge cannot be null");

		// Write header
		writeInt32(HUGE, huge.length());

		// Write body - CB uses a reflection-optimized wrapper for String.
		writeCharBuffer(CharBuffer.wrap(huge));
	}

	@Override
	public void writeString(char[] text) throws IllegalArgumentException,
			IOException {
		if (text == null)
			throw new IllegalArgumentException("text cannot be null");

		writeString(text, 0, text.length);
	}

	@Override
	public void writeString(char[] text, int index, int length)
			throws IllegalArgumentException, IOException {
		if (text == null)
			throw new IllegalArgumentException("text cannot be null");

		// Write header
		writeInt32(STRING, length);

		// Write body
		writeCharBuffer(CharBuffer.wrap(text, index, length));
	}

	@Override
	public void writeString(String text) throws IllegalArgumentException,
			IOException {
		if (text == null)
			throw new IllegalArgumentException("text cannot be null");

		// Write header
		writeInt32(STRING, text.length());

		// Write body - CB uses a reflection-optimized wrapper for String.
		writeCharBuffer(CharBuffer.wrap(text));
	}

	@Override
	public void writeArrayHeader(int elementCount) throws IOException {
		writeInt32(ARRAY, elementCount);
	}

	@Override
	public void writeObjectHeader(int elementCount) throws IOException {
		writeInt32(OBJECT, elementCount);
	}

	protected void writeInt32(byte type, int value) throws IOException {
		// Fill write buffer
		int32Buffer[0] = type;
		int32Buffer[1] = (byte) ((value >>> 24) & 0xFF);
		int32Buffer[2] = (byte) ((value >>> 16) & 0xFF);
		int32Buffer[3] = (byte) ((value >>> 8) & 0xFF);
		int32Buffer[4] = (byte) ((value >>> 0) & 0xFF);

		// Write it in one chunk.
		out.write(int32Buffer);
	}

	protected void writeInt64(byte type, long value) throws IOException {
		// Fill write buffer
		int64Buffer[0] = type;
		int64Buffer[1] = (byte) (value >>> 56);
		int64Buffer[2] = (byte) (value >>> 48);
		int64Buffer[3] = (byte) (value >>> 40);
		int64Buffer[4] = (byte) (value >>> 32);
		int64Buffer[5] = (byte) (value >>> 24);
		int64Buffer[6] = (byte) (value >>> 16);
		int64Buffer[7] = (byte) (value >>> 8);
		int64Buffer[8] = (byte) (value >>> 0);

		// Write it in one chunk
		out.write(int64Buffer);
	}

	protected void writeCharBuffer(CharBuffer input) throws IOException {
		encoder.reset();

		// Keep encoding as long as we have content to encode.
		while (input.hasRemaining()) {
			output.clear();
			encoder.encode(input, output, false);

			// Flip output buffer so it can be read.
			output.flip();

			// Read back and write out encoded bytes to output stream.
			out.write(output.array(), 0, output.remaining());
		}

		// Encoder cleanup. End and flush.
		output.clear();
		encoder.encode(input, output, true);
		encoder.flush(output);

		// If cleanup resulting in more bytes, write them out too.
		if (output.hasRemaining()) {
			output.flip();
			out.write(output.array(), 0, output.remaining());
		}
	}
}