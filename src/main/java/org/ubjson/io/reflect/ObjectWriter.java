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

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
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

public class ObjectWriter implements IObjectWriter {
	protected enum ScopeType {
		ARRAY, OBJECT;
	}

	protected Mode mode;
	protected ScopeStack sstack;

	private AssignableKey assignableQuery;
	private LRUHashMap<AssignableKey, Boolean> assignableCache;

	private LRUHashMap<Class<?>, List<Field>> fieldCache;
	private LRUHashMap<Class<?>, List<MethodAlias>> methodCache;

	public ObjectWriter() {
		sstack = new ScopeStack();

		assignableQuery = new AssignableKey();
		assignableCache = new LRUHashMap<AssignableKey, Boolean>();

		fieldCache = new LRUHashMap<Class<?>, List<Field>>();
		methodCache = new LRUHashMap<Class<?>, List<MethodAlias>>();
	}

	@Override
	public void clear() {
		assignableCache.clear();

		fieldCache.clear();
		methodCache.clear();
	}

	@Override
	public void writeObject(UBJOutputStream out, Object obj)
			throws IllegalArgumentException, IOException {
		writeObject(out, obj, Mode.FIELDS);
	}

	@Override
	public void writeObject(UBJOutputStream out, Object obj, Mode mode)
			throws IllegalArgumentException, IOException {
		if (out == null)
			throw new IllegalArgumentException("out cannot be null");
		if (obj == null)
			throw new IllegalArgumentException("obj cannot be null");
		if (mode == null)
			throw new IllegalArgumentException("mode cannot be null");

		// Reset scope stack state
		sstack.reset();

		// Setup the reflection mode used for this operation.
		this.mode = mode;

		switch (mode) {
		case FIELDS:
			writeObjectByFields(out, null, obj.getClass(), obj);
			break;

		case METHODS:
			writeObjectByMethods(out, null, obj.getClass(), obj);
			break;
		}
	}

	/**
	 * Used to check if the given {@link Class} <code>from</code> is assignable
	 * to the given {@link Class} <code>to</code>.
	 * <p/>
	 * Put another way, this method checks if objects of type <code>from</code>
	 * can be cast to type <code>to</code>.
	 * <p/>
	 * This implementation caches the results of these tests so as to avoid
	 * unnecessary {@link Class#isAssignableFrom(Class)} calls.
	 * 
	 * @param from
	 *            The original {@link Class} to check assignment to
	 *            <code>to</code>.
	 * @param to
	 *            The target assignable {@link Class}.
	 * 
	 * @return <code>true</code> if <code>from</code> can be cast to
	 *         <code>to</code>, otherwise returns <code>false</code>.
	 * 
	 * @throws IllegalArgumentException
	 *             if <code>from</code> or <code>to</code> or <code>null</code>.
	 */
	protected boolean isAssignable(Class<?> from, Class<?> to)
			throws IllegalArgumentException {
		if (from == null)
			throw new IllegalArgumentException("from cannot be null");
		if (to == null)
			throw new IllegalArgumentException("to cannot be null");

		// Setup temp key to query our cache.
		assignableQuery.set(from, to);

		// Check if the assignable calculation has been checked/cached before.
		Boolean assignable = assignableCache.get(assignableQuery);

		if (assignable == null) {
			System.out.println("Assignable Cache MISS!");

			// Calculate assignability and cache result.
			assignable = to.isAssignableFrom(from);
			assignableCache.put(new AssignableKey(from, to), assignable);
		} else
			System.out.println("Assignable Cache HIT!");

		return assignable;
	}

	protected void dispatchWrite(UBJOutputStream out, String name, Object value)
			throws IOException {
		if (value == null)
			writeNull(out, name);
		else {
			Class<?> valType = value.getClass();

			// Values
			if (isAssignable(valType, Boolean.class))
				writeBoolean(out, name, (Boolean) value);
			else if (isAssignable(valType, Number.class))
				writeNumber(out, name, valType, (Number) value);
			else if (isAssignable(valType, String.class))
				writeString(out, name, (String) value);
			// Arrays
			else if (valType.isArray()) {
				if ("char".equals(valType.getComponentType().getName()))
					writeString(out, name, (char[]) value);
				else
					writeArray(out, name, value);
			} else if (isAssignable(valType, Collection.class))
				writeArray(out, name, (Collection<?>) value);
			// Objects
			else {
				switch (mode) {
				case FIELDS:
					writeObjectByFields(out, valType.getSimpleName(), valType,
							value);
					break;

				case METHODS:
					writeObjectByMethods(out, valType.getSimpleName(), valType,
							value);
					break;
				}
			}
		}
	}

	protected void writeNull(UBJOutputStream out, String name)
			throws IOException {
		if (sstack.peek() != ScopeType.ARRAY)
			out.writeString(name);

		out.writeNull();
	}

	protected void writeBoolean(UBJOutputStream out, String name, boolean value)
			throws IOException {
		if (sstack.peek() != ScopeType.ARRAY)
			out.writeString(name);

		out.writeBoolean(value);
	}

	protected void writeNumber(UBJOutputStream out, String name, Class<?> type,
			Number value) throws IOException {
		if (sstack.peek() != ScopeType.ARRAY)
			out.writeString(name);

		if (isAssignable(type, Byte.class))
			out.writeByte((Byte) value);
		else if (isAssignable(type, Short.class))
			out.writeInt16((Short) value);
		else if (isAssignable(type, Integer.class))
			out.writeInt32((Integer) value);
		else if (isAssignable(type, Long.class))
			out.writeInt64((Long) value);
		else if (isAssignable(type, Float.class))
			out.writeFloat((Float) value);
		else if (isAssignable(type, Double.class))
			out.writeDouble((Double) value);
		else if (isAssignable(type, BigInteger.class))
			out.writeHuge((BigInteger) value);
		else if (isAssignable(type, BigDecimal.class))
			out.writeHuge((BigDecimal) value);
		else if (isAssignable(type, AtomicInteger.class))
			out.writeInt32(((AtomicInteger) value).get());
		else if (isAssignable(type, AtomicLong.class))
			out.writeInt64(((AtomicLong) value).get());
		else
			throw new IllegalArgumentException("Unsupported numeric type ["
					+ type + "]");
	}

	protected void writeString(UBJOutputStream out, String name, char[] value)
			throws IOException {
		if (sstack.peek() != ScopeType.ARRAY)
			out.writeString(name);

		out.writeString(value);
	}

	protected void writeString(UBJOutputStream out, String name, String value)
			throws IOException {
		if (sstack.peek() != ScopeType.ARRAY)
			out.writeString(name);

		out.writeString(value);
	}

	protected void writeArray(UBJOutputStream out, String name, Object array)
			throws IOException {
		if (sstack.peek() != ScopeType.ARRAY)
			out.writeString(name);

		int length = Array.getLength(array);
		out.writeArrayHeader(length);

		// Enter array scope
		sstack.push(ScopeType.ARRAY);

		// Write array elements
		for (int i = 0; i < length; i++)
			dispatchWrite(out, null, Array.get(array, i));

		// Exit array scope
		sstack.pop();
	}

	protected void writeArray(UBJOutputStream out, String name,
			Collection<?> collection) throws IOException {
		if (sstack.peek() != ScopeType.ARRAY)
			out.writeString(name);

		int length = collection.size();
		out.writeArrayHeader(length);

		Class<?> cType = collection.getClass();

		// Enter array scope
		sstack.push(ScopeType.ARRAY);

		/*
		 * We can iterate over a List more efficiently otherwise fall back to
		 * using the collection's iterator.
		 */
		if (List.class.isAssignableFrom(cType)) {
			List<?> list = (List<?>) collection;

			// Write array elements
			for (int i = 0; i < length; i++)
				dispatchWrite(out, null, list.get(i));
		} else {
			Iterator<?> iter = collection.iterator();

			// Write array elements
			while (iter.hasNext())
				dispatchWrite(out, null, iter.next());
		}

		// Exit array scope
		sstack.pop();
	}

	protected void writeObjectByFields(UBJOutputStream out, String name,
			Class<?> type, Object obj) throws IOException {
		if (sstack.peek() != ScopeType.ARRAY)
			out.writeString(name);

		// Check cache for existing filtered field list.
		List<Field> fieldList = fieldCache.get(type);

		// Filter and cache field list if we didn't already.
		if (fieldList == null) {
			// Get all public, inherited fields.
			Field[] fields = type.getFields();
			fieldList = new ArrayList<Field>(fields.length);

			for (int i = 0; i < fields.length; i++) {
				Field f = fields[i];
				int mods = f.getModifiers();

				// Skip static, transient or synthetic fields.
				if (Modifier.isStatic(mods) || Modifier.isTransient(mods)
						|| f.isSynthetic())
					continue;

				fieldList.add(f);
			}
		}

		out.writeObjectHeader(fieldList.size());

		// Enter object scope
		sstack.push(ScopeType.OBJECT);

		for (int i = 0, s = fieldList.size(); i < s; i++) {
			Field f = fieldList.get(i);
			Object fValue = null;

			try {
				fValue = f.get(obj);
			} catch (Exception e) {
				e.printStackTrace();
			}

			dispatchWrite(out, f.getName(), fValue);
		}

		// Exit object scope
		sstack.pop();
	}

	protected void writeObjectByMethods(UBJOutputStream out, String name,
			Class<?> type, Object obj) throws IOException {
		if (sstack.peek() != ScopeType.ARRAY)
			out.writeString(name);

		// Check cache for existing filtered method list.
		List<MethodAlias> methodList = methodCache.get(type);

		// Filter and cache field list if we didn't already.
		if (methodList == null) {
			// Get all public, inherited methods.
			Method[] methods = type.getMethods();
			methodList = new ArrayList<MethodAlias>(methods.length);

			for (int i = 0; i < methods.length; i++) {
				Method m = methods[i];
				int mods = m.getModifiers();

				// Skip static, transient, synthetic or methods that take args.
				if (Modifier.isStatic(mods) || Modifier.isTransient(mods)
						|| m.isSynthetic() || m.getParameterTypes().length != 0)
					continue;

				int j = 0;
				String mName = m.getName();

				// Normalize the method name used as the value label.
				if (mName.startsWith("is"))
					j = 2;
				else if (mName.startsWith("can") || mName.startsWith("has")
						|| mName.startsWith("get"))
					j = 3;

				// Lowercase first char, append the remainder of the name.
				mName = Character.toLowerCase(mName.charAt(j))
						+ mName.substring(j + 1);

				methodList.add(new MethodAlias(m, mName));
			}
		}

		out.writeObjectHeader(methodList.size());

		// Enter object scope
		sstack.push(ScopeType.OBJECT);

		for (int i = 0, s = methodList.size(); i < s; i++) {
			MethodAlias ma = methodList.get(i);
			Object mValue = null;

			try {
				mValue = ma.method.invoke(obj);
			} catch (Exception e) {
				e.printStackTrace();
			}

			dispatchWrite(out, ma.name, mValue);
		}

		// Exit object scope
		sstack.pop();
	}

	/**
	 * Class used to represent a (FIFO) stack of scopes that the reflection code
	 * uses to keep track of where it is while writing out an object; this is
	 * important so the code knows when to include labels for values (in
	 * objects) and when not to (in arrays).
	 * 
	 * @author Riyad Kalla (software@thebuzzmedia.com)
	 */
	protected class ScopeStack {
		private int pos;
		private ScopeType[] levels;

		public ScopeStack() {
			// Support a scope depth up to 128-levels.
			levels = new ScopeType[128];
		}

		public void reset() {
			pos = -1;
		}

		public void push(ScopeType type) {
			levels[++pos] = type;
		}

		public ScopeType peek() {
			return (pos < 0 ? null : levels[pos]);
		}

		public ScopeType pop() {
			return levels[pos--];
		}
	}

	private class AssignableKey {
		Class<?> from;
		Class<?> to;

		public AssignableKey() {
			set(null, null);
		}

		public AssignableKey(Class<?> from, Class<?> to) {
			set(from, to);
		}

		@Override
		public int hashCode() {
			return (from.hashCode() + to.hashCode());
		}

		@Override
		public boolean equals(Object obj) {
			boolean equal = false;

			if (obj != null)
				equal = (hashCode() == obj.hashCode());

			return equal;
		}

		public void set(Class<?> from, Class<?> to) {
			this.from = from;
			this.to = to;
		}
	}

	private class MethodAlias {
		Method method;
		String name;

		public MethodAlias(Method method, String name) {
			this.method = method;
			this.name = name;
		}
	}
}