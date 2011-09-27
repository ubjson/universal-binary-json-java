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
import java.math.BigDecimal;

public interface IUBJInput {
	public byte nextValueType() throws IOException, DataFormatException,
			IllegalStateException;

	public void readNull() throws IOException, DataFormatException;

	public boolean readBoolean() throws IOException, DataFormatException;

	public byte readByte() throws IOException, DataFormatException;

	public int readInt32() throws IOException, DataFormatException;

	public long readInt64() throws IOException, DataFormatException;

	public double readDouble() throws IOException, DataFormatException;

	public BigDecimal readHuge() throws IOException, DataFormatException;

	public String readString() throws IOException, DataFormatException;

	public char[] readStringAsChars() throws IOException, DataFormatException;

	public int readArrayHeader() throws IOException, DataFormatException;

	public int readObjectHeader() throws IOException, DataFormatException;
}