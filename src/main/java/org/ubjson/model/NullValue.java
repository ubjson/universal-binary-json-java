package org.ubjson.model;

import static org.ubjson.io.IMarkerType.NULL;

import java.io.IOException;

import org.ubjson.io.DataFormatException;
import org.ubjson.io.UBJOutputStream;
import org.ubjson.io.parser.UBJInputStreamParser;

public class NullValue extends AbstractValue<Void> {
	public NullValue() {
		// default
	}

	public NullValue(UBJInputStreamParser in) throws IOException,
			DataFormatException {
		deserialize(in);
	}

	@Override
	public byte getType() {
		return NULL;
	}

	@Override
	public void serialize(UBJOutputStream out) throws IOException {
		out.writeNull();
	}

	@Override
	public void deserialize(UBJInputStreamParser in) throws IOException,
			DataFormatException {
		in.readNull();
	}
}