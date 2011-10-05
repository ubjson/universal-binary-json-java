package org.ubjson;

public interface UBJsonOutputStream {
	public void close();

	public void flush();

	public void write();

	public void write(boolean val);

	public void write(Boolean val) throws IllegalArgumentException;

	public void write(byte val);

	public void write(Byte val) throws IllegalArgumentException;

	public void write(int val);

	public void write(Integer val) throws IllegalArgumentException;

	public void write(long val);

	public void write(Long val) throws IllegalArgumentException;

	public void write(double val);

	public void write(Double val) throws IllegalArgumentException;
	
	public void write(char[] val);

	public void write(String val) throws IllegalArgumentException;

	public void write(CharSequence val) throws IllegalArgumentException;
}