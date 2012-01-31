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
import static org.ubjson.io.ITypeMarker.NULL;
import static org.ubjson.io.ITypeMarker.OBJECT;
import static org.ubjson.io.ITypeMarker.OBJECT_COMPACT;
import static org.ubjson.io.ITypeMarker.STRING;
import static org.ubjson.io.ITypeMarker.STRING_COMPACT;
import static org.ubjson.io.ITypeMarker.TRUE;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.CharBuffer;

public class UBJInputStreamParser extends UBJInputStream {
	private byte peek;

	public UBJInputStreamParser(InputStream in) {
		super(in);
		peek = -1;
	}

	@Override
	public byte nextType() throws IOException, UBJFormatException {
		/*
		 * peek == -1 on the first call or after reading a value's "body" using
		 * one of the readXXX methods below.
		 * 
		 * peek != -1 when we have found a value marker and NOT read the body
		 * using one of the values below, which means in order to read the
		 * "nextType" we first need to skip over the body of the currently
		 * marked value.
		 */
		if (peek != -1) {
			switch (peek) {
			/*
			 * All 1-byte values are technically already "skipped" as they have
			 * been read from the stream and their value is sitting in the
			 * "peek" variable at the moment. By no-op'ing here then calling
			 * nextType at the end of this method, we are effectively skipping
			 * the value and moving onto the next one from the stream.
			 */
			case END:
			case NULL:
			case TRUE:
			case FALSE:
				break;

			// 2-byte value, skip the 1-byte body.
			case BYTE:
				in.skip(1);
				break;

			// 3-byte value, skip the 2-byte body.
			case INT16:
				in.skip(2);
				break;

			// 5-byte values, skip the 4-byte body.
			case INT32:
			case FLOAT:
				in.skip(4);
				break;

			// 9-byte value, skip the 8-byte body.
			case INT64:
			case DOUBLE:
				in.skip(8);
				break;

			/*
			 * Variable-Length: Each of these types have a variable-length body
			 * size which is described in the following 4-byte integer value.
			 */
			case HUGE:
			case STRING:
			case ARRAY:
			case OBJECT:
				in.skip(readInt32Impl());
				break;

			/*
			 * Variable-Length: Each of these types have a variable-length body
			 * size which is described in the following 1-byte integer value.
			 */
			case HUGE_COMPACT:
			case STRING_COMPACT:
			case ARRAY_COMPACT:
			case OBJECT_COMPACT:
				in.skip(in.read());
				break;

			default:
				throw new UBJFormatException(pos,
						"Encountered an unknown type marker of byte value "
								+ peek + " (char='" + ((char) peek)
								+ "') at stream position " + pos + ".");
			}
		}

		return (peek = super.nextType());
	}

	@Override
	public void readEnd() throws IOException, UBJFormatException {
		super.readEnd();
		peek = -1;
	}

	@Override
	public void readNull() throws IOException, UBJFormatException {
		super.readNull();
		peek = -1;
	}

	@Override
	public boolean readBoolean() throws IOException, UBJFormatException {
		boolean b = super.readBoolean();
		peek = -1;
		return b;
	}

	@Override
	public byte readByte() throws IOException, UBJFormatException {
		byte b = super.readByte();
		peek = -1;
		return b;
	}

	@Override
	public short readInt16() throws IOException, UBJFormatException {
		short s = super.readInt16();
		peek = -1;
		return s;
	}

	@Override
	public int readInt32() throws IOException, UBJFormatException {
		int i = super.readInt32();
		peek = -1;
		return i;
	}

	@Override
	public long readInt64() throws IOException, UBJFormatException {
		long l = super.readInt64();
		peek = -1;
		return l;
	}

	@Override
	public float readFloat() throws IOException, UBJFormatException {
		float f = super.readFloat();
		peek = -1;
		return f;
	}

	@Override
	public double readDouble() throws IOException, UBJFormatException {
		double d = super.readDouble();
		peek = -1;
		return d;
	}

	@Override
	public byte[] readHugeAsBytes() throws IOException, UBJFormatException {
		byte[] h = super.readHugeAsBytes();
		peek = -1;
		return h;
	}

	@Override
	public CharBuffer readHugeAsChars() throws IOException, UBJFormatException {
		CharBuffer buffer = super.readHugeAsChars();
		peek = -1;
		return buffer;
	}

	@Override
	public String readHugeAsString() throws IOException, UBJFormatException {
		String huge = super.readHugeAsString();
		peek = -1;
		return huge;
	}

	@Override
	public BigInteger readHugeAsBigInteger() throws IOException,
			UBJFormatException {
		BigInteger h = super.readHugeAsBigInteger();
		peek = -1;
		return h;
	}

	@Override
	public BigDecimal readHugeAsBigDecimal() throws IOException,
			UBJFormatException {
		BigDecimal h = super.readHugeAsBigDecimal();
		peek = -1;
		return h;
	}

	@Override
	public CharBuffer readStringAsChars() throws IOException,
			UBJFormatException {
		CharBuffer buffer = super.readStringAsChars();
		peek = -1;
		return buffer;
	}

	@Override
	public int readArrayLength() throws IOException, UBJFormatException {
		int count = super.readArrayLength();
		peek = -1;
		return count;
	}

	@Override
	public int readObjectLength() throws IOException, UBJFormatException {
		int count = super.readObjectLength();
		peek = -1;
		return count;
	}

	@Override
	protected byte checkType(String name, byte expected, byte expectedOpt)
			throws UBJFormatException, IOException {
		/*
		 * Auto-peek at the next byte if necessary. This allows people to use
		 * the UBJ streams in a manual serial/deserialization pattern of calling
		 * the read/write methods back to back with no intervening nextType()
		 * calls required (just how a direct UBJInputStream would be used); we
		 * do it automatically for them here if they haven't done it yet.
		 */
		if (peek == -1)
			nextType();

		if (peek != expected && (expectedOpt != INVALID && peek != expectedOpt)) {
			String message = "Unable to read " + name
					+ " value at stream position " + pos
					+ ". The type marker byte value read was " + peek
					+ " (char='" + ((char) peek)
					+ "') but the expected type marker byte value was "
					+ expected + " (char='" + ((char) expected) + "')";

			if (expectedOpt != INVALID)
				message += " or " + expectedOpt + " (char='"
						+ ((char) expectedOpt) + "'); but neither were found.";
			else
				message += '.';

			throw new UBJFormatException(pos, message);
		}

		return peek;
	}
}