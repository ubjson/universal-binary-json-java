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

import java.io.IOException;

public interface IUBJOutput {
	public void writeNull() throws IOException;

	public void writeBoolean(boolean value) throws IOException;

	public void writeByte(byte value) throws IOException;

	public void writeInt32(int value) throws IOException;

	public void writeInt64(long value) throws IOException;

	public void writeDouble(double value) throws IOException;

	public void writeHuge(char[] huge) throws IllegalArgumentException,
			IOException;

	public void writeHuge(char[] huge, int index, int length)
			throws IllegalArgumentException, IOException;

	public void writeHuge(String huge) throws IllegalArgumentException,
			IOException;

	public void writeString(char[] text) throws IllegalArgumentException,
			IOException;

	public void writeString(char[] text, int index, int length)
			throws IllegalArgumentException, IOException;

	public void writeString(String text) throws IllegalArgumentException,
			IOException;

	public void writeArrayHeader(int elementCount) throws IOException;

	public void writeObjectHeader(int elementCount) throws IOException;
}