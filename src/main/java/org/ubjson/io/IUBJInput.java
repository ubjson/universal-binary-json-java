package org.ubjson.io;

import java.io.IOException;
import java.math.BigDecimal;

public interface IUBJInput {
	public byte nextValueType() throws IOException, DataFormatException,
			IllegalStateException;

	public void readNull() throws IOException, DataFormatException;

	public boolean readBoolean() throws IOException, DataFormatException;

	public byte readByte() throws IOException, DataFormatException;

	public int readInt32() throws IOException, DataFormatException;

	public long readInt64() throws IOException, DataFormatException;

	public double readDouble() throws IOException, DataFormatException;

	public BigDecimal readHuge() throws IOException, DataFormatException;

	public String readString() throws IOException, DataFormatException;

	public char[] readStringAsChars() throws IOException, DataFormatException;

	public int readArrayHeader() throws IOException, DataFormatException;

	public int readObjectHeader() throws IOException, DataFormatException;
}