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
import java.io.InputStream;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;

/**
 * Class used to wrap a used-provided {@link ByteBuffer} in order to seamlessly
 * bridge the gap between JDK stream-based I/O and the new Buffer-based IO.
 * <p/>
 * For example, a NIO communication library can be used to pull Universal Binary
 * JSON out of a socket as fast as possible and directly into a
 * {@link ByteBuffer} wrapped by a stream of this type. You would then wrap a
 * stream instance of this type with a {@link UBJInputStream} or
 * {@link UBJInputStreamParser} to simply parse through the stream of UBJSON
 * binary data coming in over that socket with no concern for shuffling
 * <code>byte[]</code> of data from the NIO libraries to the stream-based
 * libraries.
 * <h3>Reuse</h3>
 * Similar to the {@link ByteArrayInputStream}, this stream impl is geared
 * towards re-use of the wrapped {@link ByteBuffer} by the caller to continually
 * write new information into and then use this wrapping stream interface to
 * read it back out (e.g. via a wrapping {@link UBJInputStream} or
 * {@link UBJInputStreamParser}). If the caller needs on-demand access to the
 * wrapped buffer it can be accessed via the {@link #getBuffer()} method.
 * <p/>
 * <strong>REMINDER</strong>: Because all <code>read</code> operations performed
 * via this stream are done using the relative-get methods defined on the
 * {@link ByteBuffer} class, care should be taken by the caller to correctly
 * manage flipping the buffer after more data has been written to it that this
 * stream can read.
 * <h3>Maximizing Performance</h3>
 * While the use of this class is optimized for performance and next to no
 * overhead introduced into the reading process, the real performance win comes
 * from wrapping <em>direct</em> {@link ByteBuffer} with streams of this type.
 * This allows a transparent bridge between a native operating system buffer,
 * Java's NIO APIs to access it and Java's simple stream API to consume the
 * binary data.
 * <p/>
 * Utilizing a wrapping {@link UBJInputStream} or {@link UBJInputStreamParser}
 * in that situation would provide maximal performance for consuming Universal
 * Binary JSON.
 * <p/>
 * <strong>CAUTION</strong>: Care should be taken to only use <em>direct</em>
 * ByteBuffers for long-lived connections as the garbage-collection of the
 * native memory space used by direct ByteBuffers is not managed by the JVM and
 * may not even be collected at all by the host OS. Most JVMs provide startup
 * flags to manage the maximum amount of native buffer space direct buffers can
 * allocate if you run into any {@link OutOfMemoryError}s.
 * 
 * @author Riyad Kalla (software@thebuzzmedia.com)
 */
public class ByteBufferInputStream extends InputStream {
	protected ByteBuffer bbuffer;

	public ByteBufferInputStream(ByteBuffer buffer)
			throws IllegalArgumentException {
		if (buffer == null)
			throw new IllegalArgumentException("buffer cannot be null");

		this.bbuffer = buffer;
	}
	
	/*
	 * TODO: Need to audit this impl for the correct re-usability workflow.
	 */

	@Override
	public int available() throws IOException {
		return bbuffer.remaining();
	}

	@Override
	public long skip(long n) throws IOException {
		// Trim skippable bytes to the smallest valid value.
		n = (n < bbuffer.remaining() ? n : bbuffer.remaining());

		// 'skip' n bytes.
		bbuffer.position(bbuffer.position() + (int) n);

		return n;
	}

	@Override
	public int read() throws BufferUnderflowException {
		return (bbuffer.hasRemaining() ? bbuffer.get() & 0xFF : -1);
	}

	@Override
	public int read(byte[] buffer, int offset, int length)
			throws IndexOutOfBoundsException, BufferUnderflowException {
		int r = bbuffer.remaining();

		if (r < 1)
			length = -1;
		else {
			/*
			 * Trim the copy length to the smaller of the two values: how many
			 * bytes were requested or how many are left.
			 */
			length = (length < r ? length : r);

			// Copy data into buffer.
			bbuffer.get(buffer, offset, length);
		}

		// Return how much data was read.
		return length;
	}

	@Override
	public boolean markSupported() {
		return true;
	}

	/**
	 * Overridden to call {@link ByteBuffer#mark()} on the underlying buffer.
	 * The <code>readLimit</code> parameter is ignored.
	 * 
	 * @param readlimit
	 *            An ignored limit value.
	 */
	@Override
	public void mark(int readlimit) {
		bbuffer.mark();
	}

	@Override
	public void reset() throws IOException {
		bbuffer.reset();
	}

	public ByteBuffer getBuffer() {
		return bbuffer;
	}
}