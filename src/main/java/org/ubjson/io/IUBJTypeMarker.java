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

public interface IUBJTypeMarker {
	// General Types
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

	// Streaming Types
	public static final byte NOOP = 'N';
	public static final byte END = 'E';
}