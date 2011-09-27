package org.ubjson.io;

import java.io.IOException;

public interface IUBJOutput {
	public void writeNull() throws IOException;

	public void writeBoolean(boolean value) throws IOException;

	public void writeByte(byte value) throws IOException;

	public void writeInt32(int value) throws IOException;

	public void writeInt64(long value) throws IOException;

	public void writeDouble(double value) throws IOException;

	public void writeHuge(char[] huge) throws IllegalArgumentException,
			IOException;

	public void writeHuge(char[] huge, int index, int length)
			throws IllegalArgumentException, IOException;

	public void writeHuge(String huge) throws IllegalArgumentException,
			IOException;

	public void writeString(char[] text) throws IllegalArgumentException,
			IOException;

	public void writeString(char[] text, int index, int length)
			throws IllegalArgumentException, IOException;

	public void writeString(String text) throws IllegalArgumentException,
			IOException;

	public void writeArrayHeader(int elementCount) throws IOException;

	public void writeObjectHeader(int elementCount) throws IOException;
}