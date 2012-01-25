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
	public static final char[] EMPTY = new char[0];

	public static final Charset UTF8_CHARSET = Charset.forName("UTF-8");

	private byte[] readBuffer;
	private char[] decodeBuffer;

	private CharsetDecoder decoder;

	public StreamDecoder() {
		this(UTF8_CHARSET);
	}

	public StreamDecoder(Charset charset) throws IllegalArgumentException {
		if (charset == null)
			throw new IllegalArgumentException("charset cannot be null");

		readBuffer = new byte[8192];
		decodeBuffer = new char[8192];

		decoder = charset.newDecoder();
	}

	public char[] decode(InputStream stream, int length)
			throws IllegalArgumentException, IOException {
		if (stream == null)
			throw new IllegalArgumentException("stream cannot be null");
		if (length < 0)
			throw new IllegalArgumentException("length [" + length
					+ "] must be >= 0.");

		// short-circuit
		if (length == 0)
			return EMPTY;

		int charCount = 0;

		/*
		 * Byte to Char conversion can never result in *more* chars than bytes,
		 * so assume up front that our character count result will be equal to
		 * the number of bytes we decode (i.e. ASCII). If that guess was wrong,
		 * it is corrected below before returning.
		 * 
		 * This is better than trying to guess via the Decoder's
		 * averageBytesPerChar value and happen to get it wrong in the middle of
		 * decoding and needing to re-size the target array on the fly as
		 * opposed to just chopping it before returning it if necessary.
		 */
		char[] chars = new char[length];

		// Reuse the backing decode buffer.
		CharBuffer dest = CharBuffer.wrap(decodeBuffer);

		int bytesRead = 0;
		decoder.reset();

		while (length > 0
				&& (bytesRead = stream.read(readBuffer, 0, length)) != -1) {
			length -= bytesRead;

			ByteBuffer src = ByteBuffer.wrap(readBuffer, 0, bytesRead);
			dest.clear();
			decoder.decode(src, dest, (length == 0));
			dest.flip();

			int remaining = dest.remaining();
			dest.get(chars, charCount, remaining);
			charCount += remaining;
		}

		if (length > 0)
			throw new IOException(
					"End of Stream encountered before all requested bytes ["
							+ (length + bytesRead)
							+ "] could be read. Unable to read the last "
							+ length + " remaining bytes.");

		dest.clear();
		decoder.flush(dest);
		dest.flip();

		if (dest.hasRemaining()) {
			int remaining = dest.remaining();
			dest.get(chars, charCount, remaining);
			charCount += remaining;
		}

		/*
		 * Chop the resulting array if we over-guestimated at the beginning of
		 * the method. When processing 1-byte chars (ASCII) this never happens.
		 */
		if (charCount < chars.length) {
			char[] tmp = new char[charCount];
			System.arraycopy(chars, 0, tmp, 0, charCount);
			chars = tmp;
		}

		return chars;
	}
}