package org.ubjson;

import java.io.DataOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.ubjson.util.LRUHashMap;

/*
 * TODO: Problem, we don't serialize from method accessors, just from public
 * member vars which requires someone to expose all their args to public values
 * if they want them serialized.
 * 
 * Look how other efforts, like Jackson does this.
 */
public class ObjectWriter implements IObjectProcessor {
	public static final int CACHE_SIZE = Integer.getInteger(
			"ubjson.writer.reflectCacheCapacity", 256);

	private Map<Class<?>, Object> accessorCache;

	public ObjectWriter() {
		accessorCache = new LRUCache<Class<?>, Object>(CACHE_SIZE);
	}

	public void write(OutputStream out, Object obj)
			throws IllegalArgumentException {
		if (out == null)
			throw new IllegalArgumentException(
					"out cannot be null and must represent an open OutputStream ready to accept bytes.");

		// no-op
		if (obj == null)
			return;

		// Begin recursing on the root object.
		writeObject(new DataOutputStream(out), obj.getClass(), obj);
	}

	private void writeObject(DataOutputStream dos, Class<?> clazz, Object obj) {
		// Do not follow un-serializable class hierarchies.
		if (!Serializable.class.isAssignableFrom(clazz))
			return;

		Field[] fields = (Field[]) accessorCache.get(clazz);

		if (fields == null) {
			fields = clazz.getFields();
			accessorCache.put(clazz, fields);
		}

		for (int i = 0; i < fields.length; i++) {
			Field f = fields[i];
			Class<?> t = f.getType();

			if (t.isPrimitive()) {

			} else if (t.isArray()) {

			} else
				System.out.println();
		}
	}

	private class ObjectFieldWriter {
		private Map<Class<?>, Field[]> fieldCache = new LRUHashMap<Field[]>(
				CACHE_SIZE);
	}
	
	private class ObjectMethodWriter {
		private Map<Class<?>, Field[]> fieldCache = new LRUHashMap<Field[]>(
				CACHE_SIZE);
	}
}