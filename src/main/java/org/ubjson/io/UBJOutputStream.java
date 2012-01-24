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

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.CharBuffer;

import org.ubjson.io.charset.StreamEncoder;

/*
 * TODO: Add amount-written tracking (to mirror input stream position tracking).
 * TODO: Make write methods safer for vetting arguments like IS impl did (throws IllegalArgument).
 * TODO: Normalize ALL custom write methods to call into the core 3 write methods and not
 * call the underlying stream directly.
 */
public class UBJOutputStream extends FilterOutputStream {
	protected long count;
	protected byte[] buffer;
	protected StreamEncoder encoder;

	public UBJOutputStream(OutputStream out) {
		super(out);

		buffer = new byte[8];
		encoder = new StreamEncoder();
	}

	@Override
	public void write(int b) throws IOException {
		count++;
		out.write(b);
	}

	@Override
	public void write(byte[] buffer) throws IllegalArgumentException,
			IOException {
		if (buffer == null)
			throw new IllegalArgumentException("buffer cannot be null");

		write(buffer, 0, buffer.length);
	}

	@Override
	public void write(byte[] buffer, int offset, int length)
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

		out.write(buffer, offset, length);
		count += length;
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
		} finally {
			out.close();
		}
	}

	public void writeEnd() throws IOException {
		out.write(END);
	}

	public void writeNoop() throws IOException {
		out.write(NOOP);
	}

	public void writeNull() throws IOException {
		out.write(NULL);
	}

	public void writeBoolean(boolean value) throws IOException {
		out.write(value ? TRUE : FALSE);
	}

	public void writeByte(byte value) throws IOException {
		out.write(BYTE);
		out.write(value);
	}

	public void writeInt16(short value) throws IOException {
		out.write(INT16);
		writeInt16Impl(value);
	}

	public void writeInt32(int value) throws IOException {
		out.write(INT32);
		writeInt32Impl(value);
	}

	public void writeInt64(long value) throws IOException {
		out.write(INT64);
		writeInt64Impl(value);
	}

	public void writeFloat(float value) throws IOException {
		out.write(FLOAT);

		// IEEE 754 single precision floating point format (as int).
		writeInt32Impl(Float.floatToIntBits(value));
	}

	public void writeDouble(double value) throws IOException {
		out.write(DOUBLE);

		// IEEE 754 double precision floating point format (as long).
		writeInt64Impl(Double.doubleToLongBits(value));
	}

	public void writeHuge(char[] huge) throws IllegalArgumentException,
			IOException {
		if (huge == null)
			throw new IllegalArgumentException("huge cannot be null");

		// Write header
		if (huge.length < 255) {
			out.write(HUGE_COMPACT);
			out.write(huge.length);
		} else {
			out.write(HUGE);
			writeInt32Impl(huge.length);
		}

		// Write body
		encoder.encode(CharBuffer.wrap(huge), out);
	}

	public void writeHuge(BigInteger huge) throws IllegalArgumentException,
			IOException {
		if (huge == null)
			throw new IllegalArgumentException("huge cannot be null");

		String hugeText = huge.toString();
		int length = hugeText.length();

		// Write header
		if (length < 255) {
			out.write(HUGE_COMPACT);
			out.write(length);
		} else {
			out.write(HUGE);
			writeInt32Impl(length);
		}

		// Write body
		encoder.encode(CharBuffer.wrap(hugeText), out);
	}

	public void writeHuge(BigDecimal huge) throws IllegalArgumentException,
			IOException {
		if (huge == null)
			throw new IllegalArgumentException("huge cannot be null");

		String hugeText = huge.toString();
		int length = hugeText.length();

		// Write header
		if (length < 255) {
			out.write(HUGE_COMPACT);
			out.write(length);
		} else {
			out.write(HUGE);
			writeInt32Impl(length);
		}

		// Write body
		encoder.encode(CharBuffer.wrap(hugeText), out);
	}

	public void writeString(char[] text) throws IllegalArgumentException,
			IOException {
		if (text == null)
			throw new IllegalArgumentException("text cannot be null");

		// Write header
		if (text.length < 255) {
			out.write(STRING_COMPACT);
			out.write(text.length);
		} else {
			out.write(STRING);
			writeInt32Impl(text.length);
		}

		// Write body
		encoder.encode(CharBuffer.wrap(text), out);
	}

	public void writeString(char[] text, int index, int length)
			throws IllegalArgumentException, IOException {
		if (text == null)
			throw new IllegalArgumentException("text cannot be null");

		// Write header
		if (length < 255) {
			out.write(STRING_COMPACT);
			out.write(length);
		} else {
			out.write(STRING);
			writeInt32Impl(length);
		}

		// Write body
		encoder.encode(CharBuffer.wrap(text, index, length), out);
	}

	public void writeString(String text) throws IllegalArgumentException,
			IOException {
		if (text == null)
			throw new IllegalArgumentException("text cannot be null");

		int length = text.length();

		// Write header
		if (length < 255) {
			out.write(STRING_COMPACT);
			out.write(length);
		} else {
			out.write(STRING);
			writeInt32Impl(length);
		}

		// Write body - CB uses a reflection-optimized wrapper for String.
		encoder.encode(CharBuffer.wrap(text), out);
	}

	public void writeArrayHeader(int elementCount)
			throws IllegalArgumentException, IOException {
		if (elementCount < 0)
			throw new IllegalArgumentException("elementCount [" + elementCount
					+ "] must be >= 0.");

		/*
		 * Streaming Support: If the element count is 255 or smaller, write it
		 * out in compact representation. The value 255 (0xFF) specifically will
		 * signify an unbounded container that must be terminated with a
		 * trailing 'E' at some point.
		 */
		if (elementCount < 256) {
			out.write(ARRAY_COMPACT);
			out.write(elementCount);
		} else {
			out.write(ARRAY);
			writeInt32Impl(elementCount);
		}
	}

	public void writeObjectHeader(int elementCount) throws IOException {
		if (elementCount < 0)
			throw new IllegalArgumentException("elementCount [" + elementCount
					+ "] must be >= 0.");

		/*
		 * Streaming Support: If the element count is 255 or smaller, write it
		 * out in compact representation. The value 255 (0xFF) specifically will
		 * signify an unbounded container that must be terminated with a
		 * trailing 'E' at some point.
		 */
		if (elementCount < 256) {
			out.write(OBJECT_COMPACT);
			out.write(elementCount);
		} else {
			out.write(OBJECT);
			writeInt32Impl(elementCount);
		}
	}

	protected void writeInt16Impl(short value) throws IOException {
		// Fill write buffer
		buffer[0] = (byte) ((value >>> 8) & 0xFF);
		buffer[1] = (byte) ((value >>> 0) & 0xFF);

		// Write it in one chunk.
		out.write(buffer, 0, 2);
	}

	protected void writeInt32Impl(int value) throws IOException {
		// Fill write buffer
		buffer[0] = (byte) ((value >>> 24) & 0xFF);
		buffer[1] = (byte) ((value >>> 16) & 0xFF);
		buffer[2] = (byte) ((value >>> 8) & 0xFF);
		buffer[3] = (byte) ((value >>> 0) & 0xFF);

		// Write it in one chunk.
		out.write(buffer, 0, 4);
	}

	protected void writeInt64Impl(long value) throws IOException {
		// Fill write buffer
		buffer[0] = (byte) (value >>> 56);
		buffer[1] = (byte) (value >>> 48);
		buffer[2] = (byte) (value >>> 40);
		buffer[3] = (byte) (value >>> 32);
		buffer[4] = (byte) (value >>> 24);
		buffer[5] = (byte) (value >>> 16);
		buffer[6] = (byte) (value >>> 8);
		buffer[7] = (byte) (value >>> 0);

		// Write it in one chunk
		out.write(buffer, 0, 8);
	}
}