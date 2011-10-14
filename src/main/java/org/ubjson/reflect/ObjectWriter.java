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
package org.ubjson.reflect;

import java.io.IOException;
import java.io.Serializable;

import org.ubjson.io.UBJOutputStream;

/*
 * TODO: For method caching, might speed up access time if the Method arg we store
 * is the tuple of the method AND the shortened name so we don't have to process
 * each time.
 */
public class ObjectWriter implements IObjectWriter {
	private IReflectWriter fieldWriter;

	@Override
	public void writeObject(UBJOutputStream out, Object obj)
			throws IllegalArgumentException, IOException {
		writeObject(out, obj, AccessType.FIELDS, false);
	}

	@Override
	public void writeObject(UBJOutputStream out, Object obj, boolean autoCompact)
			throws IllegalArgumentException, IOException {
		writeObject(out, obj, AccessType.FIELDS, autoCompact);
	}

	@Override
	public void writeObject(UBJOutputStream out, Object obj, AccessType type,
			boolean autoCompact) throws IllegalArgumentException, IOException {
		if (out == null)
			throw new IllegalArgumentException("out cannot be null");
		if (obj == null)
			throw new IllegalArgumentException("obj cannot be null");
		if (type == null)
			throw new IllegalArgumentException("type cannot be null");

		Class<?> clazz = obj.getClass();

		// Base-line sanity check.
		if (!Serializable.class.isAssignableFrom(clazz))
			throw new IllegalArgumentException(
					"Root object 'obj' does not implement Serializable; unable to reflect into the first level of the instance to retrieve values for processing.");

		if (type == AccessType.FIELDS) {
			if (fieldWriter == null)
				fieldWriter = new FieldReflectWriter();

			fieldWriter.dispatchWrite(out, null, obj, autoCompact);
		} else if (type == AccessType.METHODS) {
			// TODO: implement
		}
	}
}