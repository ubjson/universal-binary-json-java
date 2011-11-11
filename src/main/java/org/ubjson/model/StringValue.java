package org.ubjson.model;

import static org.ubjson.io.IMarkerType.STRING;
import static org.ubjson.io.IMarkerType.STRING_COMPACT;

import java.io.IOException;

import org.ubjson.io.DataFormatException;
import org.ubjson.io.UBJOutputStream;
import org.ubjson.io.parser.UBJInputStreamParser;

public class StringValue extends AbstractValue<String> {
	public StringValue() {
		value = "";
	}

	public StringValue(char[] s) {
		value = new String(s);
	}

	public StringValue(String s) {
		value = s;
	}

	public StringValue(UBJInputStreamParser in) throws IOException,
			DataFormatException {
		deserialize(in);
	}

	@Override
	public byte getType() {
		return (value.length() < 256 ? STRING_COMPACT : STRING);
	}

	@Override
	public void serialize(UBJOutputStream out) throws IOException {
		out.writeString(value);
	}

	@Override
	public void deserialize(UBJInputStreamParser in) throws IOException,
			DataFormatException {
		value = in.readString();
	}
}