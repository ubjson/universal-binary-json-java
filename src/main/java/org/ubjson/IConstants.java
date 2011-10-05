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
package org.ubjson;

import java.nio.charset.Charset;

public interface IConstants {
	public static final byte INVALID = -1;
	public static final byte UNKNOWN_LENGTH = (byte) 0xFF; // -1

	public static final Charset UTF_8_CHARSET = Charset.forName("UTF-8");
}