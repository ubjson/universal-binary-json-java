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
	/**
	 * System property name used to set the runtime value of
	 * {@link #BUFFER_SIZE}.
	 * <p/>
	 * Value is: <code>org.ubjson.io.charset.decoderBufferSize</code>
	 */
	public static final String BUFFER_SIZE_PROPERTY_NAME = "org.ubjson.io.charset.decoderBufferSize";

	/**
	 * Constant used to define the size of the <code>byte[]</code> buffer this
	 * decoder will use at runtime.
	 * <p/>
	 * Default value: <code>16384</code> (16KB)
	 * <p/>
	 * This value can be set using the {@link #BUFFER_SIZE_PROPERTY_NAME}
	 * property at runtime. From the command line this can be done using the
	 * <code>-D</code> argument like so:
	 * <p/>
	 * <code>java -cp [...] -Dorg.ubjson.io.charset.decoderBufferSize=32768 [...]</code>
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
	private CharsetDecoder decoder;

	public StreamDecoder() {
		this(UTF8_CHARSET);
	}

	public StreamDecoder(Charset charset) throws IllegalArgumentException {
		if (charset == null)
			throw new IllegalArgumentException("charset cannot be null");

		/*
		 * We need access to both the byte[] and the wrapping ByteBuffer.
		 * 
		 * Below in decode() we need the raw byte[] to read in bytes from the
		 * provided stream and we need access to the ByteBuffer to manipulate
		 * its pointers to keep pointing it at the new data from the stream.
		 */
		buffer = new byte[BUFFER_SIZE];
		bbuffer = ByteBuffer.wrap(buffer);

		decoder = charset.newDecoder();
	}

	public void decode(InputStream stream, int length, CharBuffer dest)
			throws IllegalArgumentException, IOException {
		if (stream == null)
			throw new IllegalArgumentException("stream cannot be null");
		if (length < 0)
			throw new IllegalArgumentException("length [" + length
					+ "] must be >= 0.");
		if (dest == null)
			throw new IllegalArgumentException(
					"dest cannot be null and must be a CharBuffer with a large enough capacity to hold at most length ["
							+ length + "] characters.");
		if (length > dest.capacity())
			throw new IllegalArgumentException(
					"length ["
							+ length
							+ "] is larger than the capacity ["
							+ dest.capacity()
							+ "] of the given dest CharBuffer; the dest CharBuffer must be big enough to contain all the characters decoded from the given InputStream.");

		// short-circuit
		if (length == 0)
			dest.clear();
		else {
			/*
			 * Reset the ByteBuffer.
			 * 
			 * As we read actual bytes in from the given stream into the wrapped
			 * byte[], we will adjust the ByteBuffers pointers to point at the
			 * new data so the decode operation operates on the correct (new)
			 * bytes.
			 */
			bbuffer.clear();

			/*
			 * Reset the destination CharBuffer.
			 * 
			 * This is the target we are decoding the bytes into. We have
			 * confirmed its capacity is large enough to hold the maximum
			 * possible number of chars we will decode (when 1-byte=1-char, e.g.
			 * ASCII). Decoding will *never* result in more chars than bytes.
			 */
			dest.clear();

			// Reset the decoder to prepare it for new work.
			decoder.reset();

			int read = 0;

			while (length > 0 && (read = stream.read(buffer, 0, length)) != -1) {
				// Keep track of how many bytes remaining we need to read.
				length -= read;

				/*
				 * Re-position the frame (position to limit) of the buffer to
				 * point at the bytes just read in from the stream to the
				 * backing array.
				 */
				bbuffer.position(0);
				bbuffer.limit(read);

				// Decode the read bytes to our output buffer.
				decoder.decode(bbuffer, dest, (length == 0));
			}

			if (length > 0)
				throw new IOException(
						"End of Stream encountered before all requested bytes ["
								+ (length + read)
								+ "] could be read. Unable to read the last "
								+ length + " remaining bytes.");

			// Flush any remaining state from the decoder to our result.
			decoder.flush(dest);
		}
	}
}