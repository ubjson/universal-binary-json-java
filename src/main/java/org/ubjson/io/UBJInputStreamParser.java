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
import java.math.BigInteger;


public class UBJInputStreamParser extends UBJInputStream {
	private byte peek;

	public UBJInputStreamParser(InputStream in) {
		super(in);
		peek = -1;
	}

	public byte nextType() throws IOException {
		return (peek = nextMarker());
	}

	@Override
	public void readEnd() throws IOException, DataFormatException {
		super.readEnd();
		peek = -1;
	}

	@Override
	public void readNull() throws IOException, DataFormatException {
		super.readNull();
		peek = -1;
	}

	@Override
	public boolean readBoolean() throws IOException, DataFormatException {
		boolean b = super.readBoolean();
		peek = -1;
		return b;
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
	public byte[] readHugeAsBytes() throws IOException, DataFormatException {
		byte[] h = super.readHugeAsBytes();
		peek = -1;
		return h;
	}

	@Override
	public char[] readHugeAsChars() throws IOException, DataFormatException {
		char[] h = super.readHugeAsChars();
		peek = -1;
		return h;
	}

	@Override
	public BigInteger readHugeAsBigInteger() throws IOException,
			DataFormatException {
		BigInteger h = super.readHugeAsBigInteger();
		peek = -1;
		return h;
	}

	@Override
	public BigDecimal readHugeAsBigDecimal() throws IOException,
			DataFormatException {
		BigDecimal h = super.readHugeAsBigDecimal();
		peek = -1;
		return h;
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
		 * calls required; we do it automatically for them here if they haven't
		 * done it yet.
		 */
		if (peek == -1)
			nextType();

		if (peek != expectedType)
			throw new DataFormatException("Unable to read " + typeLabel
					+ " value. The type read was " + peek + " (char='"
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
		 * calls required; we do it automatically for them here if they haven't
		 * done it yet.
		 */
		if (peek == -1)
			nextType();

		if (peek != expectedType1 && peek != expectedType2)
			throw new DataFormatException("Unable to read " + typeLabel
					+ " value. The type read was " + peek + " (char='"
					+ ((char) peek) + "') but the expected types were "
					+ expectedType1 + " (char='" + ((char) expectedType1)
					+ "') or " + expectedType2 + " (char='"
					+ ((char) expectedType2) + "'); neither were found.");

		return peek;
	}
}