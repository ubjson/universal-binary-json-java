package org.ubjson.model;

import static org.ubjson.io.IMarkerType.END;

import java.io.IOException;

import org.ubjson.io.DataFormatException;
import org.ubjson.io.UBJOutputStream;
import org.ubjson.io.parser.UBJInputStreamParser;

public class EndValue extends AbstractValue<Void> {
	public EndValue() {
		// default
	}

	public EndValue(UBJInputStreamParser in) throws IOException,
			DataFormatException {
		deserialize(in);
	}

	@Override
	public byte getType() {
		return END;
	}

	@Override
	public void serialize(UBJOutputStream out) throws IOException {
		out.writeEnd();
	}

	@Override
	public void deserialize(UBJInputStreamParser in) throws IOException,
			DataFormatException {
		in.readEnd();
	}
}