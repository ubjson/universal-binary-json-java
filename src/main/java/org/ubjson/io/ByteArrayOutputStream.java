package org.ubjson.io;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Class used to implement a re-usable (see {@link #reset()})
 * {@link OutputStream} that writes to an underlying (and dynamically-growing)
 * <code>byte[]</code> that the caller can get direct access to via
 * {@link #getArray()}.
 * <p/>
 * This class is meant to be a performant bridge between the stream-based
 * Universal Binary JSON I/O classes and a simple <code>byte[]</code> which can
 * be helpful when working with non-stream-based I/O, like Java NIO.
 * <p/>
 * In addition to providing direct access to the underlying <code>byte[]</code>
 * managed by this stream, the output stream itself is meant to be re-used by
 * calling {@link #reset()} (a single <code>int</code> reset; very fast) between
 * any <code>write(...)</code> operations the caller wishes to treat as
 * separate.
 * <h3>Usage</h3>
 * This class is designed such that you create an instance of this class, then
 * wrap it with a {@link UBJOutputStream} and write any amount of Universal
 * Binary JSON to the underlying <code>byte[]</code> stream; when ready, you can
 * call {@link #getArray()} to retrieve the underlying array (the underlying
 * <code>byte[]</code> is not copied; the raw reference is returned)and process
 * the UBJ byte data accordingly.
 * <p/>
 * When you are done processing the <code>byte[]</code> contents, simply call
 * {@link #reset()} on the stream, and begin your next write operation to the
 * wrapping {@link UBJOutputStream}; it would look something like this:
 * 
 * <pre>
 * <code>
 * // Create streams individually so we have access to boas.
 * {@link ByteArrayOutputStream} baos = new {@link ByteArrayOutputStream}();
 * {@link UBJOutputStream} out = new {@link UBJOutputStream}(baos);
 * 
 * // Write some Universal Binary JSON
 * out.writeObjectHeader(2);
 * 
 * // "userID": 22345
 * out.writeString("userID");
 * out.writeInt32(22345);
 * 
 * // "username": "billg64" 
 * out.writeString("username");
 * out.writeString("billg64");
 * 
 * // Hypothetical write method that accepts byte[] args 
 * writeUsingNIO(baos.getArray());
 * 
 * // Reset the underlying stream so we make it like new.
 * baos.reset();
 * 
 * // Begin writing some new constructs...
 * out.writeArrayHead(3);
 * 
 * ... more code ...
 * </code>
 * </pre>
 * <p/>
 * Since {@link UBJOutputStream} maintains no internal state and simply acts as
 * a translation layer between Java data types and byte-based UBJ
 * representation, resetting the underlying stream that it wraps is a safe
 * operation.
 * <p/>
 * This provides a very efficient mechanism for working with Universal Binary
 * JSON via the core I/O classes without wasting CPU or memory resources
 * creating/destroying byte arrays or output stream.
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
	 * <p/>
	 * This is a very fast operation that adjusts a single <code>int</code> and
	 * returns.
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