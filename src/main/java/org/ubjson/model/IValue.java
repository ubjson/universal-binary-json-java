package org.ubjson.model;

import java.io.IOException;

import org.ubjson.io.DataFormatException;
import org.ubjson.io.UBJOutputStream;
import org.ubjson.io.parser.UBJInputStreamParser;

public interface IValue<T> {
	public byte getType();

	public T getValue();

	public void serialize(UBJOutputStream out) throws IOException;

	public void deserialize(UBJInputStreamParser in) throws IOException,
			DataFormatException;
}