package org.ubjson.io.reflect;

import java.io.IOException;

import org.ubjson.io.DataFormatException;
import org.ubjson.io.UBJInputStreamParser;

public class ObjectReader implements IObjectReader {

	@Override
	public <T> T readObject(UBJInputStreamParser in, Class<T> type)
			throws IllegalArgumentException, IOException, DataFormatException {
		if (in == null)
			throw new IllegalArgumentException("in cannot be null");
		if (type == null)
			throw new IllegalArgumentException("type cannot be null");

		T obj = null;

		try {
			// Safely create an instance of our target class.
			obj = type.newInstance();
		} catch (Exception e) {
			throw new IllegalArgumentException(
					"Unable to invoke type.newInstance(); does Class T define a default no-arg constructor?",
					e);
		}

		/*
		 * begin looping on parser, ensure the root element is an object, and then
		 * read NUM number of values from parser, populating the object (maybe a method
		 * populateObject(stream, T obj, int count), going deeper as needed.
		 */

		return obj;
	}
}