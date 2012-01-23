package org.ubjson.io;

import java.io.IOException;
import java.io.InputStream;

/**
 * Class used to implement a very efficient and re-usable (see {@link #reset()},
 * {@link #reset(byte[])} and {@link #reset(byte[], int, int)})
 * {@link InputStream} that reads from an underlying, caller-provided
 * <code>byte[]</code> (also accessible via {@link #getArray()} if needed). This
 * class is a compliment to {@link ByteArrayOutputStream}.
 * 
 * @author Riyad Kalla (software@thebuzzmedia.com)
 * @see ByteArrayOutputStream
 */
public class ByteArrayInputStream extends InputStream {
	protected int i;
	protected int l;
	protected byte[] data;

	public ByteArrayInputStream(byte[] data) throws IllegalArgumentException {
		reset(data);
	}

	public ByteArrayInputStream(byte[] data, int offset, int length)
			throws IllegalArgumentException {
		reset(data, offset, length);
	}

	@Override
	public int read() throws IOException {
		return (i < l ? data[i++] & 0xFF : -1);
	}

	@Override
	public int read(byte[] buffer, int offset, int length)
			throws IllegalArgumentException, IOException {
		if (buffer == null)
			throw new IllegalArgumentException("b cannot be null");
		if (offset < 0 || length < 0 || (offset + length) > buffer.length)
			throw new IllegalArgumentException("offset [" + offset
					+ "] and length [" + length
					+ "] must be >= 0 and (offset + length)["
					+ (offset + length) + "] must be <= buffer.length ["
					+ buffer.length + "]");

		// Calculate bytes remaining in the stream.
		int r = (l - i);

		/*
		 * If no bytes are remaining, update the length we return to -1,
		 * otherwise begin the copy operation on the remaining bytes.
		 */
		if (r < 1)
			length = -1;
		else {
			/*
			 * Trim the copy length to the smaller of the two values: how many
			 * bytes were requested or how many are left.
			 */
			length = (length < r ? length : r);

			// Copy data into buffer.
			System.arraycopy(data, i, buffer, offset, length);
			i += length;
		}

		return length;
	}

	@Override
	public long skip(long n) throws IllegalArgumentException, IOException {
		if (n < 0)
			throw new IllegalArgumentException("n [" + n + "] must be >= 0");

		// Calculate remaining skippable bytes.
		int r = (l - i);

		// Trim to the smaller of the two values for our skip amount.
		n = (n < r ? n : r);

		// Skip the bytes
		i += n;

		return n;
	}

	@Override
	public int available() throws IOException {
		return (l - i);
	}

	@Override
	public boolean markSupported() {
		return false;
	}

	@Override
	public void mark(int readlimit) {
		// no-op
	}

	@Override
	public void reset() throws IOException {
		i = 0;
	}

	public void reset(byte[] data) throws IllegalArgumentException {
		if (data == null)
			throw new IllegalArgumentException("data cannot be null");

		reset(data, 0, data.length);
	}

	public void reset(byte[] data, int offset, int length)
			throws IllegalArgumentException {
		if (data == null)
			throw new IllegalArgumentException("data cannot be null");
		if (offset < 0 || length < 0 || (offset + length) > data.length)
			throw new IllegalArgumentException("offset [" + offset
					+ "] and length [" + length
					+ "] must be >= 0 and (offset + length)["
					+ (offset + length) + "] must be <= buffer.length ["
					+ data.length + "]");

		this.i = 0;
		this.l = length;
		this.data = data;
	}

	public byte[] getArray() {
		return data;
	}

	public int getOffset() {
		return i;
	}

	public int getLength() {
		return l;
	}
}