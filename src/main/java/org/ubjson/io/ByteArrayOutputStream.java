package org.ubjson.io;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

/**
 * Class used to implement a re-usable (see {@link #reset()})
 * {@link OutputStream} that writes to an underlying (and dynamically-growing)
 * <code>byte[]</code> that the caller can get direct access to via
 * {@link #getArray()}.
 * <p/>
 * This class is meant to be used as middle ground between the stream-based
 * {@link UBJOutputStream} class and any non-stream based output mechanism in
 * Java (e.g. writing to a {@link ByteBuffer}).
 * <p/>
 * The intended usage is to have callers write wrap an instance of this class
 * with a {@link UBJOutputStream}, write output as normal, and then using
 * {@link #getArray()} and {@link #getLength()}, write the <code>byte[]</code>
 * directly to the alternative output mechanism (e.g.
 * {@link ByteBuffer#put(byte[], int, int)}).
 * <p/>
 * After any number of operations {@link #reset()} can be called to reset the
 * internal index used to track insertion and length of the underlying
 * <code>byte[]</code>; effectively resetting this output stream for more output
 * operations.
 * <p/>
 * This class is meant as a more performant replacement to the JDK's
 * {@link java.io.ByteArrayOutputStream} class that provides no direct access to
 * the underly <code>byte[]</code>; requiring a copy of the underlying
 * <code>byte[]</code> be created every time direct access is needed.
 * 
 * @author Riyad Kalla (software@thebuzzmedia.com)
 */
public class ByteArrayOutputStream extends OutputStream {
	public static final String BUFFER_SIZE_PROPERTY_NAME = "ubjson.io.baos.bufferSize";

	public static final int BUFFER_SIZE = Integer.getInteger(
			BUFFER_SIZE_PROPERTY_NAME, 8192);

	static {
		if (BUFFER_SIZE < 1)
			throw new RuntimeException("Invalid BUFFER_SIZE [" + BUFFER_SIZE
					+ "], system property [" + BUFFER_SIZE_PROPERTY_NAME
					+ "] must be set to an integer value >= 1.");
	}

	protected int i;
	protected byte[] data;

	public ByteArrayOutputStream() {
		this(BUFFER_SIZE);
	}

	public ByteArrayOutputStream(int initialSize)
			throws IllegalArgumentException {
		if (initialSize < 1)
			throw new IllegalArgumentException("initialSize [" + initialSize
					+ "] must be >= 1");

		i = 0;
		data = new byte[initialSize];
	}

	@Override
	public void write(int b) throws IOException {
		ensureIndex(i);
		data[i++] = (byte) b;
	}

	@Override
	public void write(byte[] b) throws IOException {
		write(b, 0, b.length);
	}

	@Override
	public void write(byte[] b, int offset, int length) throws IOException {
		ensureIndex(i + length);
		System.arraycopy(b, offset, data, i, length);
		i += length;
	}

	/**
	 * Used to reset the internal index used to track the size and insertion
	 * position in the underlying <code>byte[]</code>; effectively resetting
	 * this {@link OutputStream} and preparing it for re-use.
	 */
	public void reset() {
		i = 0;
	}

	/**
	 * Used to get the current length of data written to the underlying
	 * <code>byte[]</code>.
	 * 
	 * @return the current length of data written to the underlying
	 *         <code>byte[]</code>.
	 */
	public int getLength() {
		return i;
	}

	/**
	 * Used to get direct access to the <code>byte[]</code> that backs this
	 * {@link OutputStream} implementation.
	 * 
	 * @return the <code>byte[]</code> that backs this {@link OutputStream}
	 *         implementation.
	 */
	public byte[] getArray() {
		return data;
	}

	/**
	 * Used to ensure that the underlying <code>byte[]</code> is large enough to
	 * allow valid access to the given <code>index</code>. If it is not, then
	 * the underlying <code>byte[]</code> is expanded to the new size of
	 * <code>index + 1</code>.
	 * 
	 * @param index
	 *            The position in the underlying <code>byte[]</code> that will
	 *            be ensured.
	 */
	protected void ensureIndex(int index) {
		int size = index + 1;

		if (size > data.length) {
			byte[] tmp = new byte[size];
			System.arraycopy(data, 0, tmp, 0, data.length);
			data = tmp;
		}
	}
}