package org.ubjson.model;

import static org.ubjson.io.IMarkerType.FALSE;
import static org.ubjson.io.IMarkerType.TRUE;

import java.io.IOException;

import org.ubjson.io.DataFormatException;
import org.ubjson.io.UBJOutputStream;
import org.ubjson.io.parser.UBJInputStreamParser;

public class BooleanValue extends AbstractValue<Boolean> {
	public BooleanValue() {
		// default
	}
	
	public BooleanValue(Boolean b) {
		value = b;
	}

	public BooleanValue(UBJInputStreamParser in) throws IOException,
			DataFormatException {
		deserialize(in);
	}

	@Override
	public byte getType() {
		return (value ? TRUE : FALSE);
	}

	@Override
	public void serialize(UBJOutputStream out) throws IOException {
		out.writeBoolean(value);
	}

	@Override
	public void deserialize(UBJInputStreamParser in) throws IOException,
			DataFormatException {
		value = in.readBoolean();
	}
}