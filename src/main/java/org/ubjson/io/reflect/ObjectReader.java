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
package org.ubjson.io.reflect;

import static org.ubjson.io.ITypeMarker.*;

import java.io.IOException;

import org.ubjson.io.UBJFormatException;
import org.ubjson.io.UBJInputStreamParser;

public class ObjectReader implements IObjectReader {
	@Override
	public <T> T readObject(UBJInputStreamParser in, Class<T> type)
			throws IllegalArgumentException, IOException, UBJFormatException {
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
		byte t = -1;
		
		while((t = in.nextType()) != -1) {
			switch(t) {
			
			}
		}

		return obj;
	}
}