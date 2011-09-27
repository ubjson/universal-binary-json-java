/**   
 * Copyright 2011 The Buzz Media, LLC
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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