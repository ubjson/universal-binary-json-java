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
package org.ubjson.io.charset;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;

public class StreamEncoder {
	/**
	 * System property name used to set the runtime value of
	 * {@link #BUFFER_SIZE}.
	 * <p/>
	 * Value is: <code>org.ubjson.io.charset.encoderBufferSize</code>
	 */
	public static final String BUFFER_SIZE_PROPERTY_NAME = "org.ubjson.io.charset.encoderBufferSize";

	/**
	 * Constant used to define the size of the <code>byte[]</code> buffer this
	 * encoder will use at runtime.
	 * <p/>
	 * Default value: <code>16384</code> (16KB)
	 * <p/>
	 * This value can be set using the {@link #BUFFER_SIZE_PROPERTY_NAME}
	 * property at runtime. From the command line this can be done using the
	 * <code>-D</code> argument like so:
	 * <p/>
	 * <code>java -cp [...] -Dorg.ubjson.io.charset.encoderBufferSize=32768 [...]</code>
	 */
	public static final int BUFFER_SIZE = Integer.getInteger(
			BUFFER_SIZE_PROPERTY_NAME, 16384);

	public static final Charset UTF8_CHARSET = Charset.forName("UTF-8");

	static {
		if (BUFFER_SIZE < 1)
			throw new RuntimeException("System property ["
					+ BUFFER_SIZE_PROPERTY_NAME
					+ "] must be > 0 but is currently set to the value '"
					+ BUFFER_SIZE + "'.");
	}

	private byte[] buffer;
	private CharsetEncoder encoder;

	public StreamEncoder() {
		this(UTF8_CHARSET);
	}

	public StreamEncoder(Charset charset) throws IllegalArgumentException {
		if (charset == null)
			throw new IllegalArgumentException("charset cannot be null");

		buffer = new byte[BUFFER_SIZE];
		encoder = charset.newEncoder();
	}

	public void encode(CharBuffer text, OutputStream stream)
			throws IllegalArgumentException, IOException {
		if (stream == null)
			throw new IllegalArgumentException("stream cannot be null");

		if (text == null || !text.hasRemaining())
			return;

		encoder.reset();
		ByteBuffer dest = ByteBuffer.wrap(buffer);

		/*
		 * Encode all the remaining chars in the given buffer; the buffer's
		 * state (position, limit) will mark the range of chars we are meant to
		 * process.
		 */
		while (text.hasRemaining()) {
			dest.clear();
			encoder.encode(text, dest, false);

			stream.write(buffer, 0, dest.position());
		}

		// Perform the decoding finalization.
		dest.clear();
		encoder.encode(text, dest, true);
		encoder.flush(dest);

		// Write out any additional bytes finalization resulted in.
		if (dest.position() > 0)
			stream.write(buffer, 0, dest.position());
	}
}