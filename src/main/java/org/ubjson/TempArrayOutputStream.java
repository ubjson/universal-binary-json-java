package org.ubjson;

import java.io.ByteArrayOutputStream;

public class TempArrayOutputStream extends ByteArrayOutputStream {
	public TempArrayOutputStream() {
		super(64);
	}
	
	public int getLength() {
		return count;
	}
	
	public byte[] getArray() {
		return buf;
	}
}