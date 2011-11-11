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

import static org.ubjson.io.IMarkerType.ARRAY;
import static org.ubjson.io.IMarkerType.ARRAY_COMPACT;
import static org.ubjson.io.IMarkerType.BYTE;
import static org.ubjson.io.IMarkerType.DOUBLE;
import static org.ubjson.io.IMarkerType.END;
import static org.ubjson.io.IMarkerType.FALSE;
import static org.ubjson.io.IMarkerType.FLOAT;
import static org.ubjson.io.IMarkerType.HUGE;
import static org.ubjson.io.IMarkerType.HUGE_COMPACT;
import static org.ubjson.io.IMarkerType.INT16;
import static org.ubjson.io.IMarkerType.INT32;
import static org.ubjson.io.IMarkerType.INT64;
import static org.ubjson.io.IMarkerType.NOOP;
import static org.ubjson.io.IMarkerType.NULL;
import static org.ubjson.io.IMarkerType.OBJECT;
import static org.ubjson.io.IMarkerType.OBJECT_COMPACT;
import static org.ubjson.io.IMarkerType.STRING;
import static org.ubjson.io.IMarkerType.STRING_COMPACT;
import static org.ubjson.io.IMarkerType.TRUE;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.CharBuffer;

import org.ubjson.io.charset.StreamEncoder;

public class UBJOutputStream extends FilterOutputStream {
	private byte[] int16Buffer;
	private byte[] int32Buffer;
	private byte[] int64Buffer;

	private StreamEncoder encoder;

	public UBJOutputStream(OutputStream out) {
		super(out);
		encoder = new StreamEncoder();

		int16Buffer = new byte[2];
		int32Buffer = new byte[4];
		int64Buffer = new byte[8];
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

		// IEEE 754 single precision floating point format (as long).
		writeInt32Impl(Float.floatToIntBits(value));
	}

	public void writeDouble(double value) throws IOException {
		out.write(DOUBLE);

		// IEEE 754 double precision floating point format (as long).
		writeInt64Impl(Double.doubleToLongBits(value));
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
		int16Buffer[0] = (byte) ((value >>> 8) & 0xFF);
		int16Buffer[1] = (byte) ((value >>> 0) & 0xFF);

		// Write it in one chunk.
		out.write(int16Buffer);
	}

	protected void writeInt32Impl(int value) throws IOException {
		// Fill write buffer
		int32Buffer[0] = (byte) ((value >>> 24) & 0xFF);
		int32Buffer[1] = (byte) ((value >>> 16) & 0xFF);
		int32Buffer[2] = (byte) ((value >>> 8) & 0xFF);
		int32Buffer[3] = (byte) ((value >>> 0) & 0xFF);

		// Write it in one chunk.
		out.write(int32Buffer);
	}

	protected void writeInt64Impl(long value) throws IOException {
		// Fill write buffer
		int64Buffer[0] = (byte) (value >>> 56);
		int64Buffer[1] = (byte) (value >>> 48);
		int64Buffer[2] = (byte) (value >>> 40);
		int64Buffer[3] = (byte) (value >>> 32);
		int64Buffer[4] = (byte) (value >>> 24);
		int64Buffer[5] = (byte) (value >>> 16);
		int64Buffer[6] = (byte) (value >>> 8);
		int64Buffer[7] = (byte) (value >>> 0);

		// Write it in one chunk
		out.write(int64Buffer);
	}
}