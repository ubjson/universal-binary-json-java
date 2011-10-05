package org.ubjson.reflect;

import java.io.IOException;

import org.ubjson.io.DataFormatException;
import org.ubjson.io.UBJInputStream;

public interface IObjectReader {
	public <T> T readObject(UBJInputStream in, T t)
			throws IllegalArgumentException, IOException, DataFormatException;
}