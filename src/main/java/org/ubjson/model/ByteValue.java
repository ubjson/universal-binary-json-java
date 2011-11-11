package org.ubjson.model;

import static org.ubjson.io.IMarkerType.BYTE;

import java.io.IOException;

import org.ubjson.io.DataFormatException;
import org.ubjson.io.UBJOutputStream;
import org.ubjson.io.parser.UBJInputStreamParser;

public class ByteValue extends AbstractValue<Byte> {
	public ByteValue() {
		// default
	}
	
	public ByteValue(Byte b) {
		value = b;
	}

	public ByteValue(UBJInputStreamParser in) throws IOException,
			DataFormatException {
		deserialize(in);
	}

	@Override
	public byte getType() {
		return BYTE;
	}

	@Override
	public void serialize(UBJOutputStream out) throws IOException {
		out.writeByte(value);
	}

	@Override
	public void deserialize(UBJInputStreamParser in) throws IOException,
			DataFormatException {
		value = in.readByte();
	}
}