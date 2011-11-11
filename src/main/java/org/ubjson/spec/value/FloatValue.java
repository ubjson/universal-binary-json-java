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

import static org.ubjson.io.IMarkerType.FLOAT;

import java.io.IOException;

import org.ubjson.io.DataFormatException;
import org.ubjson.io.UBJOutputStream;
import org.ubjson.io.parser.UBJInputStreamParser;

public class FloatValue extends AbstractValue<Float> {
	public FloatValue(Float value) throws IllegalArgumentException {
		super(value);
	}

	public FloatValue(UBJInputStreamParser in) throws IllegalArgumentException,
			IOException, DataFormatException {
		super(in);
	}

	@Override
	public byte getType() {
		return FLOAT;
	}

	@Override
	public void serialize(UBJOutputStream out) throws IllegalArgumentException,
			IOException {
		if (out == null)
			throw new IllegalArgumentException("out cannot be null");

		out.writeFloat(value);
	}

	@Override
	public void deserialize(UBJInputStreamParser in)
			throws IllegalArgumentException, IOException, DataFormatException {
		if (in == null)
			throw new IllegalArgumentException("in cannot be null");

		value = in.readFloat();
	}
}