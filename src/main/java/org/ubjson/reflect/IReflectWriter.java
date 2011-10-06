package org.ubjson.reflect;

import java.io.IOException;
import java.util.Collection;

import org.ubjson.io.UBJOutputStream;

public interface IReflectWriter {
	public void dispatchWrite(UBJOutputStream out, String name, Object value,
			boolean autoCompact) throws IllegalArgumentException, IOException;

	public void writeNull(UBJOutputStream out, String name)
			throws IllegalArgumentException, IOException;

	public void writeBoolean(UBJOutputStream out, String name, boolean value)
			throws IllegalArgumentException, IOException;

	public void writeNumber(UBJOutputStream out, String name, Class<?> type,
			Number value, boolean autoCompact) throws IllegalArgumentException,
			IOException;

	public void writeString(UBJOutputStream out, String name, char[] value,
			boolean autoCompact) throws IllegalArgumentException, IOException;

	public void writeString(UBJOutputStream out, String name, String value,
			boolean autoCompact) throws IllegalArgumentException, IOException;

	public void writeArray(UBJOutputStream out, String name, Object array,
			boolean autoCompact) throws IllegalArgumentException, IOException;

	public void writeArray(UBJOutputStream out, String name,
			Collection<?> collection, boolean autoCompact)
			throws IllegalArgumentException, IOException;

	public void writeObject(UBJOutputStream out, String name, Class<?> type,
			Object obj, boolean autoCompact) throws IllegalArgumentException,
			IOException;
}