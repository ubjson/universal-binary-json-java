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
	private ByteBuffer bbuffer;
	private CharsetEncoder encoder;

	public StreamEncoder() {
		this(UTF8_CHARSET);
	}

	public StreamEncoder(Charset charset) throws IllegalArgumentException {
		if (charset == null)
			throw new IllegalArgumentException("charset cannot be null");

		/*
		 * We need access to both the byte[] and the wrapping ByteBuffer.
		 * 
		 * Below in encode() we need the raw byte[] to write bytes to the
		 * provided stream and we need access to the ByteBuffer to manipulate
		 * its pointers to keep pointing it at the new data from the encoder.
		 */
		buffer = new byte[BUFFER_SIZE];
		bbuffer = ByteBuffer.wrap(buffer);

		encoder = charset.newEncoder();
	}

	public void encode(CharBuffer src, OutputStream stream)
			throws IllegalArgumentException, IOException {
		if (stream == null)
			throw new IllegalArgumentException("stream cannot be null");

		// Short-circuit.
		if (src == null || !src.hasRemaining())
			return;

		encoder.reset();

		/*
		 * Encode all the remaining chars in the given buffer; the buffer's
		 * state (position, limit) will mark the range of chars we are meant to
		 * process.
		 */
		while (src.hasRemaining()) {
			bbuffer.clear();

			// Encode the text into our temporary write buffer.
			encoder.encode(src, bbuffer, false);

			/*
			 * Using direct access to the underlying byte[], write the bytes
			 * that the decode operation produced to the output stream.
			 */
			stream.write(buffer, 0, bbuffer.position());
		}

		// Perform the decoding finalization.
		bbuffer.clear();
		encoder.encode(src, bbuffer, true);
		encoder.flush(bbuffer);

		// Write out any additional bytes finalization resulted in.
		if (bbuffer.position() > 0)
			stream.write(buffer, 0, bbuffer.position());
	}
}