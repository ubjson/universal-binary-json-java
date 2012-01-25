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
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

public class StreamDecoder {
	public static final String BUFFER_SIZE_PROPERTY_NAME = "org.ubjson.io.charset.dencoderBufferSize";

	public static final int BUFFER_SIZE = Integer.getInteger(
			BUFFER_SIZE_PROPERTY_NAME, 16384);

	public static final Charset UTF8_CHARSET = Charset.forName("UTF-8");

	private byte[] buffer;
	private CharsetDecoder decoder;

	public StreamDecoder() {
		this(UTF8_CHARSET);
	}

	public StreamDecoder(Charset charset) throws IllegalArgumentException {
		if (charset == null)
			throw new IllegalArgumentException("charset cannot be null");

		buffer = new byte[BUFFER_SIZE];
		decoder = charset.newDecoder();
	}

	public CharBuffer decode(InputStream stream, int length)
			throws IllegalArgumentException, IOException {
		if (stream == null)
			throw new IllegalArgumentException("stream cannot be null");
		if (length < 0)
			throw new IllegalArgumentException("length [" + length
					+ "] must be >= 0.");

		// short-circuit
		if (length == 0)
			return CharBuffer.wrap(new char[0]);

		/*
		 * Create the read buffer.
		 * 
		 * The offset and length we pass doesn't matter because we actually have
		 * to adjust it on each iteration of the loop below to make sure it is
		 * "looking" at the bytes just read in from the stream into the
		 * underlying array (readBuffer).
		 */
		ByteBuffer src = ByteBuffer.wrap(buffer, 0, 0);

		/*
		 * Create the buffer that we are going to decode into and return to the
		 * caller.
		 * 
		 * Byte to Char conversion will *never* result in more chars than there
		 * are bytes, so we know that the biggest possible buffer we will need
		 * is length bytes long. If it ends up being less after doing the
		 * decoding, the limit on the buffer will be truncated to indicate that.
		 */
		CharBuffer dest = CharBuffer.allocate(length);

		// Reset the decoder to prepare it for new work.
		decoder.reset();

		int bytesRead = 0;

		while (length > 0 && (bytesRead = stream.read(buffer, 0, length)) != -1) {
			// Keep track of how many bytes remaining we need to read.
			length -= bytesRead;

			/*
			 * Re-position the frame (position to limit) of the buffer to point
			 * at the bytes just read in from the stream to the backing array.
			 */
			src.position(0);
			src.limit(bytesRead);

			// Decode the read bytes to our output buffer.
			decoder.decode(src, dest, (length == 0));
		}

		if (length > 0)
			throw new IOException(
					"End of Stream encountered before all requested bytes ["
							+ (length + bytesRead)
							+ "] could be read. Unable to read the last "
							+ length + " remaining bytes.");

		// Flush any remaining state from the decoder to our result.
		decoder.flush(dest);

		// Prepare the result buffer to be read by the caller.
		dest.flip();

		return dest;
	}
}