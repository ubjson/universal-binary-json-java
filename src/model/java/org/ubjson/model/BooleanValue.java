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

import static org.ubjson.io.IUBJTypeMarker.FALSE;
import static org.ubjson.io.IUBJTypeMarker.TRUE;

import java.io.IOException;

import org.ubjson.io.UBJFormatException;
import org.ubjson.io.UBJInputStreamParser;
import org.ubjson.io.UBJOutputStream;

public class BooleanValue extends AbstractValue<Boolean> {
	public BooleanValue(Boolean value) throws IllegalArgumentException {
		super(value);
	}

	public BooleanValue(UBJInputStreamParser in)
			throws IllegalArgumentException, IOException, UBJFormatException {
		super(in);
	}

	@Override
	public byte getType() {
		return (value ? TRUE : FALSE);
	}

	@Override
	public void serialize(UBJOutputStream out) throws IllegalArgumentException,
			IOException {
		if (out == null)
			throw new IllegalArgumentException("out cannot be null");

		out.writeBoolean(value);
	}

	@Override
	public void deserialize(UBJInputStreamParser in)
			throws IllegalArgumentException, IOException, UBJFormatException {
		if (in == null)
			throw new IllegalArgumentException("in cannot be null");

		value = in.readBoolean();
	}
}