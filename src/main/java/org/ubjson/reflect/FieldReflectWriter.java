package org.ubjson.reflect;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.ubjson.io.UBJOutputStream;
import org.ubjson.util.LRUHashMap;

/*
 * TODO: Massive potential optimization, cache all the results of Class/isAssignableFrom
 * in a map so they can be looked up and the reflect call avoided in all subsequent lookups
 * since serialization of same-type values happens quite a bit.
 * 
 * TODO: When writing numeric, String and Object types, this needs to check the
 * container lengths and write them to the SMALLEST possible representation
 * based on the new spec changes.
 */
public class FieldReflectWriter implements IReflectWriter {
	private long listDepth;
	private LRUHashMap<Class<?>, List<Field>> fieldCache;

	public FieldReflectWriter() {
		listDepth = 1;
		fieldCache = new LRUHashMap<Class<?>, List<Field>>();
	}

	@Override
	public void dispatchWrite(UBJOutputStream out, String name, Object value)
			throws IllegalArgumentException, IOException {
		if (out == null)
			throw new IllegalArgumentException("out cannot be null");

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
			else
				writeObject(out, name, type, value);
		}
	}

	@Override
	public void writeNull(UBJOutputStream out, String name)
			throws IllegalArgumentException, IOException {
		writeName(out, name);
		out.writeNull();
	}

	@Override
	public void writeBoolean(UBJOutputStream out, String name, boolean value)
			throws IllegalArgumentException, IOException {
		writeName(out, name);
		out.writeBoolean(value);
	}

	@Override
	public void writeNumber(UBJOutputStream out, String name, Class<?> type,
			Number value) throws IllegalArgumentException, IOException {
		writeName(out, name);

		// TODO: Compaction should use the smallest numeric type.
		if (autoCompact) {

		}

		if (Byte.class.isAssignableFrom(type))
			out.writeByte((Byte) value);
		else if (Integer.class.isAssignableFrom(type))
			out.writeInt32((Integer) value);
		else if (Long.class.isAssignableFrom(type))
			out.writeInt64((Long) value);
		else if (Double.class.isAssignableFrom(type))
			out.writeDouble((Double) value);
		else if (BigDecimal.class.isAssignableFrom(type))
			out.writeHuge((BigDecimal) value);
		else
			throw new IllegalArgumentException("Writing a numeric value ["
					+ value + "] of type [" + type
					+ "] is not supported by this class.");
	}

	@Override
	public void writeString(UBJOutputStream out, String name, char[] value)
			throws IllegalArgumentException, IOException {
		writeName(out, name);
		out.writeString(value);
	}

	@Override
	public void writeString(UBJOutputStream out, String name, String value)
			throws IllegalArgumentException, IOException {
		writeName(out, name);
		out.writeString(value);
	}

	@Override
	public void writeArray(UBJOutputStream out, String name, Object array)
			throws IllegalArgumentException, IOException {
		writeName(out, name);

		int length = Array.getLength(array);
		out.writeArrayHeader(length);

		listPush();
		for (int i = 0; i < length; i++)
			dispatchWrite(out, null, Array.get(array, i));
		listPop();
	}

	@Override
	public void writeArray(UBJOutputStream out, String name,
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

	@Override
	public void writeObject(UBJOutputStream out, String name, Class<?> type,
			Object obj) throws IllegalArgumentException, IOException {
		if (!Serializable.class.isAssignableFrom(type))
			return;

		writeName(out, name);
		List<Field> fieldList = fieldCache.get(type);

		if (fieldList == null) {
			Field[] tmp = type.getDeclaredFields();
			fieldList = new ArrayList<Field>(tmp.length);

			for (int i = 0; i < tmp.length; i++) {
				Field f = tmp[i];
				int mods = f.getModifiers();

				// Skip non-public, static, transient or synthetic fields.
				if (!Modifier.isPublic(mods) || Modifier.isStatic(mods)
						|| Modifier.isTransient(mods) || f.isSynthetic())
					continue;

				fieldList.add(f);
			}

			fieldCache.put(type, fieldList);
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

	private void writeName(UBJOutputStream out, String name) throws IOException {
		if (listDepth == 0 && name != null)
			out.writeString(name);
	}

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
}