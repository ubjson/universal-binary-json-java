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
package org.ubjson.reflect.write;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.ubjson.io.UBJOutputStream;

/*
 * TODO: For method (access type) caching, might speed up access time if the Method arg we store
 * is the tuple of the method AND the shortened name so we don't have to process
 * each time.
 * 
 * TODO: All of the isAssignableFrom calls should be benchmarked and cached if
 * it is found to be faster to avoid the reflection into the class.
 */
public class ObjectWriter implements IObjectWriter {
	private long listDepth;

	private AccessType accessType;
	private boolean compactNumberStorage;

	@Override
	public void reset() {
		listDepth = 1;

		accessType = null;
		compactNumberStorage = false;
	}

	@Override
	public void writeObject(UBJOutputStream out, Object obj)
			throws IllegalArgumentException, IOException {
		writeObject(out, obj, AccessType.FIELDS, false);
	}

	@Override
	public void writeObject(UBJOutputStream out, Object obj, AccessType type)
			throws IllegalArgumentException, IOException {
		writeObject(out, obj, type, false);
	}

	@Override
	public void writeObject(UBJOutputStream out, Object obj, AccessType type,
			boolean compactNumberStorage) throws IllegalArgumentException,
			IOException {
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

		// Reset the writer's state since we are going to do work now.
		reset();

		this.compactNumberStorage = compactNumberStorage;
		this.accessType = type;

		// Begin recursing on the object and writing it out.
		dispatchWrite(out, null, obj);
	}

	/*
	 * ========================================================================
	 * Entry Point - Master dispatch method.
	 * ========================================================================
	 */

	protected void dispatchWrite(UBJOutputStream out, String name, Object value)
			throws IOException {

		if (value == null)
			writeNull(out, name);
		else {
			Class<?> type = value.getClass();

			if (Boolean.class.isAssignableFrom(type))
				writeBoolean(out, name, (Boolean) value);
			else if (Number.class.isAssignableFrom(type))
				writeNumber(out, name, type, (Number) value);
			else if (String.class.isAssignableFrom(type))
				writeString(out, name, (String) value);
			else if (type.isArray()) {
				String typeName = type.getComponentType().getName();

				if ("char".equals(typeName))
					writeString(out, name, (char[]) value);
				else
					writeArray(out, name, value);
			} else if (Collection.class.isAssignableFrom(type))
				writeArray(out, name, (Collection<?>) value);
			else {
				switch (accessType) {
				case FIELDS:
					writeObjectByFields(out, name, type, value);
					break;

				case METHODS:
					// TODO: implement byMethods
					break;
				}
			}
		}
	}

	/*
	 * ========================================================================
	 * Value-specific Writer Methods
	 * ========================================================================
	 */

	protected void writeNull(UBJOutputStream out, String name)
			throws IllegalArgumentException, IOException {
		writeName(out, name);
		out.writeNull();
	}

	protected void writeBoolean(UBJOutputStream out, String name, boolean value)
			throws IllegalArgumentException, IOException {
		writeName(out, name);
		out.writeBoolean(value);
	}

	protected void writeNumber(UBJOutputStream out, String name, Class<?> type,
			Number value) throws IllegalArgumentException, IOException {
		writeName(out, name);

		if (compactNumberStorage) {
			long iv = value.longValue(); // integer value
			double dv = value.doubleValue(); // decimal value
			boolean hasDecimal = (dv > iv);

			if (hasDecimal) {
				if (dv > Float.MAX_VALUE)
					value = dv;
				else
					value = (float) dv;
			} else {
				if (iv <= Byte.MAX_VALUE)
					value = (byte) iv;
				else if (iv <= Short.MAX_VALUE)
					value = (short) iv;
				else if (iv <= Integer.MAX_VALUE)
					value = (int) iv;
				else
					value = (long) iv;
			}
		}

		if (Byte.class.isAssignableFrom(type))
			out.writeByte((Byte) value);
		else if (Short.class.isAssignableFrom(type))
			out.writeInt16((Short) value);
		else if (Integer.class.isAssignableFrom(type))
			out.writeInt32((Integer) value);
		else if (Long.class.isAssignableFrom(type))
			out.writeInt64((Long) value);
		else if (Float.class.isAssignableFrom(type))
			out.writeFloat((Float) value);
		else if (Double.class.isAssignableFrom(type))
			out.writeDouble((Double) value);
		else if (BigInteger.class.isAssignableFrom(type))
			out.writeHuge((BigInteger) value);
		else if (BigDecimal.class.isAssignableFrom(type))
			out.writeHuge((BigDecimal) value);
		else if (AtomicInteger.class.isAssignableFrom(type))
			out.writeInt32(((AtomicInteger) value).get());
		else if (AtomicLong.class.isAssignableFrom(type))
			out.writeInt64(((AtomicLong) value).get());
		else
			throw new IllegalArgumentException("Writing a numeric value ["
					+ value + "] of type [" + type
					+ "] is not supported by this class.");
	}

	protected void writeString(UBJOutputStream out, String name, char[] value)
			throws IllegalArgumentException, IOException {
		writeName(out, name);
		out.writeString(value);
	}

	protected void writeString(UBJOutputStream out, String name, String value)
			throws IllegalArgumentException, IOException {
		writeName(out, name);
		out.writeString(value);
	}

	/*
	 * ========================================================================
	 * Container Writer Methods
	 * ========================================================================
	 */

	protected void writeArray(UBJOutputStream out, String name, Object array)
			throws IllegalArgumentException, IOException {
		writeName(out, name);

		int length = Array.getLength(array);
		out.writeArrayHeader(length);

		listPush();
		for (int i = 0; i < length; i++)
			dispatchWrite(out, null, Array.get(array, i));
		listPop();
	}

	protected void writeArray(UBJOutputStream out, String name,
			Collection<?> collection) throws IllegalArgumentException,
			IOException {
		writeName(out, name);

		int length = collection.size();
		out.writeArrayHeader(length);

		Class<?> cType = collection.getClass();

		listPush();
		if (List.class.isAssignableFrom(cType)) {
			List<?> list = (List<?>) collection;

			for (int i = 0; i < length; i++)
				dispatchWrite(out, name, list.get(i));
		} else {
			Iterator<?> i = collection.iterator();

			while (i.hasNext())
				dispatchWrite(out, name, i.next());
		}
		listPop();
	}

	protected void writeObjectByFields(UBJOutputStream out, String name,
			Class<?> type, Object obj) throws IllegalArgumentException,
			IOException {
		if (!Serializable.class.isAssignableFrom(type))
			return;

		writeName(out, name);

		// TODO: Optimization, Cache these processed lists.
		Field[] fields = type.getDeclaredFields();
		List<Field> fieldList = new ArrayList<Field>(fields.length);

		for (int i = 0; i < fields.length; i++) {
			Field f = fields[i];
			int mods = f.getModifiers();

			// Skip non-public, static, transient or synthetic fields.
			if (!Modifier.isPublic(mods) || Modifier.isStatic(mods)
					|| Modifier.isTransient(mods) || f.isSynthetic())
				continue;

			fieldList.add(f);
		}

		out.writeObjectHeader(fieldList.size());

		for (int i = 0, length = fieldList.size(); i < length; i++) {
			Field f = fieldList.get(i);
			Object fValue = null;

			try {
				fValue = f.get(obj);
			} catch (Exception e) {
				// no-op, the value will be written as null.
			}

			dispatchWrite(out, f.getName(), fValue);
		}
	}

	/*
	 * ========================================================================
	 * General - Helper methods.
	 * ========================================================================
	 */

	private void listPush() throws RuntimeException {
		listDepth = listDepth << 1;

		if (listDepth < 0)
			throw new RuntimeException(
					"Excessive Recursion: Reflection has recursed more than 64 levels deep into Array or Object elements to try and write this object; this is not supported.");
	}

	private void listPop() throws RuntimeException {
		if (listDepth == 0)
			throw new RuntimeException(
					"Uneven Recursion Return: Attempted to decrease current object recursion depth, but the depth was already 0. This is likely the result of unbalanced Array or Object recursion code.");

		listDepth = listDepth >> 1;
	}

	private void writeName(UBJOutputStream out, String name) throws IOException {
		if (listDepth == 0 && name != null && !name.isEmpty())
			out.writeString(name);
	}
}