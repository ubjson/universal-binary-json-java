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

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;

public class UBJInputStreamParser extends UBJInputStream {
	private byte peek;

	public UBJInputStreamParser(InputStream in) {
		super(in);

		peek = -1;
	}

	public byte nextType() throws IOException {
		return (peek = (byte) in.read());
	}

	@Override
	public byte readByte() throws IOException, DataFormatException {
		byte b = super.readByte();
		peek = -1;

		return b;
	}

	@Override
	public short readInt16() throws IOException, DataFormatException {
		short s = super.readInt16();
		peek = -1;

		return s;
	}

	@Override
	public int readInt32() throws IOException, DataFormatException {
		int i = super.readInt32();
		peek = -1;

		return i;
	}

	@Override
	public long readInt64() throws IOException, DataFormatException {
		long l = super.readInt64();
		peek = -1;

		return l;
	}

	@Override
	public float readFloat() throws IOException, DataFormatException {
		float f = super.readFloat();
		peek = -1;

		return f;
	}

	@Override
	public double readDouble() throws IOException, DataFormatException {
		double d = super.readDouble();
		peek = -1;

		return d;
	}

	@Override
	public BigDecimal readHuge() throws IOException, DataFormatException {
		BigDecimal bd = super.readHuge();
		peek = -1;

		return bd;
	}

	@Override
	public char[] readStringAsChars() throws IOException, DataFormatException {
		char[] chars = super.readStringAsChars();
		peek = -1;

		return chars;
	}

	@Override
	public int readArrayLength() throws IOException, DataFormatException {
		int count = super.readArrayLength();
		peek = -1;

		return count;
	}

	@Override
	public int readObjectLength() throws IOException, DataFormatException {
		int count = super.readObjectLength();
		peek = -1;

		return count;
	}

	@Override
	protected byte checkType(String typeLabel, byte expectedType)
			throws DataFormatException, IOException {
		/*
		 * Auto-peek at the next byte if necessary. This allows people to use
		 * the UBJ streams in a manual serial/deserialization pattern of calling
		 * the read/write methods back to back with no intervening nextType()
		 * calls required.
		 */
		if (peek == -1)
			nextType();

		if (peek != expectedType)
			throw new DataFormatException("Unable to read " + typeLabel
					+ " value. The last type read was " + peek + " (char='"
					+ ((char) peek) + "') but the expected type was "
					+ expectedType + " (char='" + ((char) expectedType) + "').");

		return peek;
	}

	@Override
	protected byte checkTypes(String typeLabel, byte expectedType1,
			byte expectedType2) throws DataFormatException, IOException {
		/*
		 * Auto-peek at the next byte if necessary. This allows people to use
		 * the UBJ streams in a manual serial/deserialization pattern of calling
		 * the read/write methods back to back with no intervening nextType()
		 * calls required.
		 */
		if (peek == -1)
			nextType();

		if (peek != expectedType1 && peek != expectedType2)
			throw new DataFormatException("Unable to read " + typeLabel
					+ " value. The last type read was " + peek + " (char='"
					+ ((char) peek) + "') but the expected types were "
					+ expectedType1 + " (char='" + ((char) expectedType1)
					+ "') or " + expectedType2 + " (char='"
					+ ((char) expectedType2) + "'); neither were found.");

		return peek;
	}
}