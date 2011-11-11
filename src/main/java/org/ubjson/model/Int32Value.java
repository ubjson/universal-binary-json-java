package org.ubjson.model;

import static org.ubjson.io.IMarkerType.INT32;

import java.io.IOException;

import org.ubjson.io.DataFormatException;
import org.ubjson.io.UBJOutputStream;
import org.ubjson.io.parser.UBJInputStreamParser;

public class Int32Value extends AbstractValue<Integer> {
	public Int32Value() {
		// default
	}
	
	public Int32Value(Integer i) {
		value = i;
	}

	public Int32Value(UBJInputStreamParser in) throws IOException,
			DataFormatException {
		deserialize(in);
	}

	@Override
	public byte getType() {
		return INT32;
	}

	@Override
	public void serialize(UBJOutputStream out) throws IOException {
		out.writeInt32(value);
	}

	@Override
	public void deserialize(UBJInputStreamParser in) throws IOException,
			DataFormatException {
		value = in.readInt32();
	}
}