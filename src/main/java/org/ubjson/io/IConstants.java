package org.ubjson.io;

import java.nio.charset.Charset;

/**
 * Interface used to define the different <code>byte</code> markers used to
 * delimited the different kind of value types supported by the Universal Binary
 * JSON specification.
 * <p/>
 * These are not defined as an <code>enum</code> because in order to use the
 * actual <code>byte</code> values themselves in a switch statement, the
 * <code>case</code> statements have to evaluate to a constant.
 * 
 * @author Riyad Kalla (software@thebuzzmedia.com)
 * @see <a href="http://ubjson.org/">Universal Binary JSON Specification</a>
 */
public interface IConstants {
	public static final byte INVALID = -1;

	public static final byte NULL = 'Z';
	public static final byte TRUE = 'T';
	public static final byte FALSE = 'F';
	public static final byte BYTE = 'B';
	public static final byte INT32 = 'I';
	public static final byte INT64 = 'L';
	public static final byte DOUBLE = 'D';
	public static final byte HUGE = 'H';
	public static final byte STRING = 'S';
	public static final byte ARRAY = 'A';
	public static final byte OBJECT = 'O';

	public static final Charset UTF_8_CHARSET = Charset.forName("UTF-8");
}