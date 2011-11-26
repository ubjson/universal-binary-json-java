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

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;

public class StreamEncoder {
	public static final Charset UTF8_CHARSET = Charset.forName("UTF-8");

	private byte[] writeBuffer;
	private CharsetEncoder encoder;

	public StreamEncoder() {
		this(UTF8_CHARSET);
	}

	public StreamEncoder(Charset charset) throws IllegalArgumentException {
		if (charset == null)
			throw new IllegalArgumentException("charset cannot be null");

		writeBuffer = new byte[8192];
		encoder = charset.newEncoder();
	}

	public void encode(CharBuffer text, OutputStream stream)
			throws IllegalArgumentException, IOException {
		if (stream == null)
			throw new IllegalArgumentException("stream cannot be null");

		if (text == null || !text.hasRemaining())
			return;

		encoder.reset();
		ByteBuffer dest = ByteBuffer.wrap(writeBuffer);

		while (text.hasRemaining()) {
			dest.clear();
			encoder.encode(text, dest, false);

			stream.write(writeBuffer, 0, dest.position());
		}

		dest.clear();
		encoder.encode(text, dest, true);
		encoder.flush(dest);

		if (dest.position() > 0)
			stream.write(writeBuffer, 0, dest.position());
	}
}