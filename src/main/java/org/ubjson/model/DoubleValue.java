package org.ubjson.model;

import static org.ubjson.io.IMarkerType.DOUBLE;

import java.io.IOException;

import org.ubjson.io.DataFormatException;
import org.ubjson.io.UBJOutputStream;
import org.ubjson.io.parser.UBJInputStreamParser;

public class DoubleValue extends AbstractValue<Double> {
	public DoubleValue() {
		// default
	}
	
	public DoubleValue(Double d) {
		value = d;
	}

	public DoubleValue(UBJInputStreamParser in) throws IOException,
			DataFormatException {
		deserialize(in);
	}

	@Override
	public byte getType() {
		return DOUBLE;
	}

	@Override
	public void serialize(UBJOutputStream out) throws IOException {
		out.writeDouble(value);
	}

	@Override
	public void deserialize(UBJInputStreamParser in) throws IOException,
			DataFormatException {
		value = in.readDouble();
	}
}