package org.ubjson;

public interface UBJsonParser {
	public void close();
	
	public boolean hasNext();
	
	public ValueType nextValueType();
	
	public int getLength();

//	getByteValue
//	getIntValue
//	getStringValue....
}