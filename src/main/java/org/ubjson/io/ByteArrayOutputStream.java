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
 * <h3>Efficient JDK Replacement</h3>
 * This class is meant as a more performant replacement to the JDK's
 * {@link java.io.ByteArrayOutputStream} class that provides no direct access to
 * the underly <code>byte[]</code>; requiring a copy of the underlying
 * <code>byte[]</code> be created every time direct access is needed.
 * <h3>Safety</h3>
 * This class intentionally has no constructor accepting a <code>byte[]</code>
 * argument because this class adjusts the size of the underlying data
 * <code>byte[]</code> on the fly as new data is written to it; meaning that if
 * a caller could provide a reference to their own <code>byte[]</code> at
 * instantiation time, it is possible that this class would create a new
 * (bigger) <code>byte[]</code> internally and the reference the caller provided
 * would no longer be valid (the caller and this stream would point at two
 * different <code>byte[]</code>).
 * <p/>
 * To avoid the potential for foot-shooting, this class doesn't allow that
 * scenario to occur.
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
		ensureCapacity(i + 1);
		data[i++] = (byte) b;
	}

	@Override
	public void write(byte[] b) throws IOException {
		write(b, 0, b.length);
	}

	@Override
	public void write(byte[] b, int offset, int length) throws IOException {
		ensureCapacity(i + length);
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

	protected void ensureCapacity(int capacity) {
		if (capacity <= data.length)
			return;

		byte[] tmp = new byte[capacity];
		System.arraycopy(data, 0, tmp, 0, data.length);
		data = tmp;
	}
}