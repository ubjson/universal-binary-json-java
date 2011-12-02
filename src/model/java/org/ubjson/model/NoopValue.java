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

import static org.ubjson.io.ITypeMarker.NOOP;

import java.io.IOException;

import org.ubjson.io.DataFormatException;
import org.ubjson.io.UBJInputStreamParser;
import org.ubjson.io.UBJOutputStream;

public class NoopValue extends AbstractValue<Void> {
	public NoopValue() {
		// default
	}

	public NoopValue(UBJInputStreamParser in) throws IllegalArgumentException,
			IOException, DataFormatException {
		super(in);
	}

	@Override
	public byte getType() {
		return NOOP;
	}

	@Override
	public void serialize(UBJOutputStream out) throws IllegalArgumentException,
			IOException {
		if (out == null)
			throw new IllegalArgumentException("out cannot be null");

		out.writeNoop();
	}

	@Override
	public void deserialize(UBJInputStreamParser in)
			throws IllegalArgumentException, IOException, DataFormatException {
		throw new UnsupportedOperationException(
				"UBJInputStream(Parser) does not support reading NOOP bytes directly as every read operation implicitly skips NOOP markers when it finds them inside the stream.");
	}
}