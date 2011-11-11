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
package org.ubjson.spec.value;

import java.io.IOException;

import org.ubjson.io.DataFormatException;
import org.ubjson.io.parser.UBJInputStreamParser;

public abstract class AbstractValue<T> implements IValue<T> {
	protected T value;

	public AbstractValue() {
		// default constructor, only stub values should use this.
	}

	public AbstractValue(T value) throws IllegalArgumentException {
		if (value == null)
			throw new IllegalArgumentException("value cannot be null");

		this.value = value;
	}

	public AbstractValue(UBJInputStreamParser in)
			throws IllegalArgumentException, IOException, DataFormatException {
		if (in == null)
			throw new IllegalArgumentException("in cannot be null");

		deserialize(in);
	}

	@Override
	public String toString() {
		return getClass().getName() + "@" + hashCode() + " [value="
				+ (value == null ? "" : value.toString()) + "]";
	}

	@Override
	public T getValue() {
		return value;
	}
}