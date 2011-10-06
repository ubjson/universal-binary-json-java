package org.ubjson.reflect;

import java.io.IOException;

import org.ubjson.io.UBJOutputStream;

public interface IObjectWriter {
	public enum AccessType {
		FIELDS, METHODS
	}

	public void writeObject(UBJOutputStream out, Object obj)
			throws IllegalArgumentException, IOException;

	public void writeObject(UBJOutputStream out, Object obj, boolean autoCompact)
			throws IllegalArgumentException, IOException;

	public void writeObject(UBJOutputStream out, Object obj, AccessType type,
			boolean autoCompact) throws IllegalArgumentException, IOException;
}