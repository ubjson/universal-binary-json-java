package org.ubjson.io;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Class used to implement a re-usable (see {@link #reset()})
 * {@link OutputStream} that writes to an underlying (and dynamically-growing)
 * <code>byte[]</code> that the caller can get direct access to via
 * {@link #getArray()}.
 * <p/>
 * This class is meant as the bridge between the existing, stream-based
 * Universal Binary JSON I/O classes and any other I/O mechanism in Java that is
 * byte-based. Using this class allows you to easily write UBJ-formatted data to
 * an underlying <code>byte[]</code> which can then be directly used elsewhere
 * (e.g. Java's NIO classes) without incurring the performance overhead of
 * copying out the data manually into a new <code>byte[]</code> which
 * {@link java.io.ByteArrayOutputStream} requires.
 * <p/>
 * After any number of operations {@link #reset()} can be called to reset the
 * internal index used to track insertion and length of the underlying
 * <code>byte[]</code>; effectively resetting this output stream and preparing
 * it for a new set of write operations (without needing to close and re-create
 * a new stream).
 * <p/>
 * This reusable design was chosen so callers can maintain a single
 * <code>byte[]</code> and wrapping {@link ByteArrayOutputStream} instance to be
 * used as a translation layer between Universal Binary JSON and its default
 * stream-based I/O, and any other form of processing the caller intends to use;
 * as opposed to designing this stream as not reusable and requiring the caller
 * to re-create new <code>byte[]</code> and {@link ByteArrayOutputStream}
 * instances on <strong>every write</strong>. The overhead would have been
 * significant in high performance applications.
 * <p/>
 * This class is meant as a more performant replacement to the JDK's
 * {@link java.io.ByteArrayOutputStream} class that provides no direct access to
 * the underly <code>byte[]</code>; requiring a copy of the underlying
 * <code>byte[]</code> be created every time direct access is needed.
 * 
 * @author Riyad Kalla (software@thebuzzmedia.com)
 */
public class ByteArrayOutputStream extends OutputStream {
	protected int i;
	protected byte[] data;

	public ByteArrayOutputStream() {
		this(8192);
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