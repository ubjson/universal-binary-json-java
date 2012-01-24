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

import static org.ubjson.io.ITypeMarker.*;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;

public class UBJInputStreamParser extends UBJInputStream {
	private byte peek;

	public UBJInputStreamParser(InputStream in) {
		super(in);
		peek = -1;
	}

	@Override
	public byte nextType() throws IOException, UBJFormatException {
		// Skip the current marked value if the body wasn't read.
		if (peek != -1) {
			switch (peek) {
			case END:
			case NULL:
			case TRUE:
			case FALSE:
				in.skip(1);
				break;

			case BYTE:
				in.skip(2);
				break;

			case INT16:
				in.skip(3);
				break;

			case INT32:
				in.skip(5);
				break;

			case INT64:
				in.skip(9);
				break;

			case FLOAT:
				in.skip(5);
				break;

			case DOUBLE:
				in.skip(9);
				break;

			case HUGE:
			case STRING:
			case ARRAY:
			case OBJECT:
				in.skip(readInt32Impl());
				break;

			case HUGE_COMPACT:
			case STRING_COMPACT:
			case ARRAY_COMPACT:
			case OBJECT_COMPACT:
				in.skip(in.read());
				break;

			default:
				throw new UBJFormatException(
						"Encountered an unknown type marker of byte value "
								+ peek + " (char='" + ((char) peek) + "').");
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
	public char[] readHugeAsChars() throws IOException, UBJFormatException {
		char[] h = super.readHugeAsChars();
		peek = -1;
		return h;
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
	public char[] readStringAsChars() throws IOException, UBJFormatException {
		char[] chars = super.readStringAsChars();
		peek = -1;
		return chars;
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
		 * calls required; we do it automatically for them here if they haven't
		 * done it yet.
		 */
		if (peek == -1)
			nextType();

		if (peek != expected && (expectedOpt != -1 && peek != expectedOpt)) {
			String message = "Unable to read " + name
					+ " value. The type marker read was byte " + peek
					+ " (char='" + ((char) peek)
					+ "') but the expected type marker byte was " + expected
					+ " (char='" + ((char) expected) + "')";

			if (expectedOpt != -1)
				message += " or " + expectedOpt + " (char='"
						+ ((char) expectedOpt) + "'); but neither were found.";
			else
				message += '.';

			throw new UBJFormatException(message);
		}

		return peek;
	}
}