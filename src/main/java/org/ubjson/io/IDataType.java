package org.ubjson.io;

public interface IDataType {
	// General Types
	public static final byte NOOP = 'N';
	public static final byte NULL = 'Z';

	// Boolean Types
	public static final byte TRUE = 'T';
	public static final byte FALSE = 'F';

	// Numeric Types
	public static final byte BYTE = 'B';
	public static final byte INT16 = 'i';
	public static final byte INT32 = 'I';
	public static final byte INT64 = 'L';
	public static final byte FLOAT = 'd';
	public static final byte DOUBLE = 'D';
	public static final byte HUGE = 'H';
	public static final byte HUGE_COMPACT = 'h';

	// String Types
	public static final byte STRING = 'S';
	public static final byte STRING_COMPACT = 's';

	// Container Types
	public static final byte ARRAY = 'A';
	public static final byte ARRAY_COMPACT = 'a';
	public static final byte OBJECT = 'O';
	public static final byte OBJECT_COMPACT = 'o';
}