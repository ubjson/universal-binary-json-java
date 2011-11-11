package org.ubjson.model;

import static org.ubjson.io.IMarkerType.INT64;

import java.io.IOException;

import org.ubjson.io.DataFormatException;
import org.ubjson.io.UBJOutputStream;
import org.ubjson.io.parser.UBJInputStreamParser;

public class Int64Value extends AbstractValue<Long> {
	public Int64Value() {
		// default
	}
	
	public Int64Value(Long l) {
		value = l;
	}

	public Int64Value(UBJInputStreamParser in) throws IOException,
			DataFormatException {
		deserialize(in);
	}

	@Override
	public byte getType() {
		return INT64;
	}

	@Override
	public void serialize(UBJOutputStream out) throws IOException {
		out.writeInt64(value);
	}

	@Override
	public void deserialize(UBJInputStreamParser in) throws IOException,
			DataFormatException {
		value = in.readInt64();
	}
}