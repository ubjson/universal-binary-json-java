/**   
 * Copyright 2011 The Buzz Media, LLC
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ubjson.model;

import java.io.IOException;

import org.ubjson.io.DataFormatException;
import org.ubjson.io.IMarkerType;
import org.ubjson.io.UBJInputStreamParser;
import org.ubjson.io.UBJOutputStream;

/**
 * Class used to define a Universal Binary JSON value element and the
 * functionality each type must implement according to the spec.
 * <p/>
 * The Universal Binary JSON specification defines a collection of value types
 * (both discrete value types and container types); this is the base interface
 * used to define the structure and functionality that the Java representation
 * of those types must have.
 * 
 * @author Riyad Kalla (software@thebuzzmedia.com)
 * 
 * @param <T>
 *            Java type (e.g. Byte, Float, String, etc.) used to internally
 *            represent the value of this Universal Binary JSON value element.
 */
public interface IValue<T> {
	/**
	 * Used to get the byte marker value that represents this Universal Binary
	 * JSON type as defined by the specification (e.g. 'T' for true, 'i' for
	 * INT16, 'D' for DOUBLE and so on...)
	 * 
	 * @return the byte marker value used to represent this value type.
	 * 
	 * @see IMarkerType
	 */
	public byte getType();

	/**
	 * Used to get the actual backing object representing the value of this
	 * Universal Binary JSON value in Java.
	 * <p/>
	 * For example, an <code>INT64</code> type from the Universal Binary JSON
	 * specification is represented by a <code>long</code> in Java; this method
	 * would return a {@link Long}.
	 * 
	 * @return the actual backing object representing the value of this
	 *         Universal Binary JSON value in Java.
	 */
	public T getValue();

	/**
	 * Used to serialize this Universal Binary JSON value to the given output
	 * stream.
	 * 
	 * @param out
	 *            The UBJ output stream that this value will be written to.
	 * 
	 * @throws IllegalArgumentException
	 *             if <code>out</code> is <code>null</code>.
	 * @throws IOException
	 *             if any exception bubbles up from the given output stream
	 *             while in use.
	 */
	public void serialize(UBJOutputStream out) throws IllegalArgumentException,
			IOException;

	/**
	 * Used to set the internal Java value backing this Universal Binary JSON
	 * value type by reading the value in directly from the given input stream.
	 * 
	 * @param in
	 *            The input stream that will be read from.
	 * 
	 * @throws IllegalArgumentException
	 *             if <code>in</code> is <code>null</code>.
	 * @throws IOException
	 *             if any exception bubbles up from the given input stream while
	 *             in use.
	 * @throws DataFormatException
	 *             if this class is unable to read its expected value type from
	 *             the given input stream.
	 */
	public void deserialize(UBJInputStreamParser in)
			throws IllegalArgumentException, IOException, DataFormatException;
}