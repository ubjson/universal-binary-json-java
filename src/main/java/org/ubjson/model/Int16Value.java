package org.ubjson.model;

import static org.ubjson.io.IMarkerType.INT16;

import java.io.IOException;

import org.ubjson.io.DataFormatException;
import org.ubjson.io.UBJOutputStream;
import org.ubjson.io.parser.UBJInputStreamParser;

public class Int16Value extends AbstractValue<Short> {
	public Int16Value() {
		// default
	}
	
	public Int16Value(Short s) {
		value = s;
	}

	public Int16Value(UBJInputStreamParser in) throws IOException,
			DataFormatException {
		deserialize(in);
	}

	@Override
	public byte getType() {
		return INT16;
	}

	@Override
	public void serialize(UBJOutputStream out) throws IOException {
		out.writeInt16(value);
	}

	@Override
	public void deserialize(UBJInputStreamParser in) throws IOException,
			DataFormatException {
		value = in.readInt16();
	}
}