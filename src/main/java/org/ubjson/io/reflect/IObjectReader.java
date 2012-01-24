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
package org.ubjson.io.reflect;

import java.io.IOException;

import org.ubjson.io.UBJFormatException;
import org.ubjson.io.UBJInputStreamParser;

public interface IObjectReader {
	public <T> T readObject(UBJInputStreamParser in, Class<T> type)
			throws IllegalArgumentException, IOException, UBJFormatException;
}