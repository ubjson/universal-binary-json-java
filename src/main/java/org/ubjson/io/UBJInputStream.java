package org.ubjson.io;

import static org.ubjson.io.IConstants.*;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharsetDecoder;

/*
 * TODO: These operations need to be normalized, after each read operation the
 * peek needs to be reset to -1 to stay clean and ready for next read op otherwise
 * it runs off of the last value. Fixed boolean already, but normalization of these
 * access patterns into the peekCheck method or something like it might
 * be a good idea.
 */
public class UBJInputStream extends FilterInputStream implements IUBJInput {
	private byte peek;

	private byte[] int32Buffer;
	private byte[] int64Buffer;

	private byte[] buffer;
	private CharsetDecoder decoder;

	public UBJInputStream(InputStream in) {
		super(in);

		peek = -1;

		int32Buffer = new byte[4];
		int64Buffer = new byte[8];

		/*
		 * REMINDER: This is not the entire read buffer, this is just the local
		 * buffer used by this class to pull content from the InputStream. The
		 * InputStream, if the user wants buffering, should be a
		 * BufferedInputStream instance with a user-configured buffer from the
		 * primary source.
		 */
		buffer = new byte[16384];
		decoder = UTF_8_CHARSET.newDecoder();
	}

	@Override
	public int read() throws IOException {
		return in.read();
	}

	@Override
	public int read(byte[] b) throws IOException {
		return in.read(b);
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		return in.read(b, off, len);
	}

	@Override
	public long skip(long n) throws IOException {
		return in.skip(n);
	}

	@Override
	public int available() throws IOException {
		return in.available();
	}

	@Override
	public void close() throws IOException {
		in.close();
	}

	@Override
	public synchronized void mark(int readlimit) {
		in.mark(readlimit);
	}

	@Override
	public synchronized void reset() throws IOException {
		in.reset();
	}

	@Override
	public boolean markSupported() {
		return in.markSupported();
	}

	@Override
	public byte nextValueType() throws IOException, DataFormatException,
			IllegalStateException {
		byte b = (byte) in.read();

		switch (b) {
		case NULL:
		case TRUE:
		case FALSE:
		case BYTE:
		case INT32:
		case INT64:
		case DOUBLE:
		case HUGE:
		case STRING:
		case ARRAY:
		case OBJECT:
		case INVALID:
			peek = b;
			break;

		default:
			throw new DataFormatException(
					"Encountered an unknown byte code ["
							+ b
							+ "] while looking for the next value ASCII marker. The data format is incorrect.");
		}

		return peek;
	}

	@Override
	public void readNull() throws IOException, DataFormatException {
		verifyNextValueType(NULL);
		peek = -1;
	}

	@Override
	public boolean readBoolean() throws IOException, DataFormatException {
		if (peek == -1)
			peek = (byte) in.read();

		boolean b;

		switch (peek) {
		case TRUE:
			b = true;
			break;

		case FALSE:
			b = false;
			break;

		default:
			throw new DataFormatException("Encountered a byte value of " + peek
					+ " instead of the expected byte value of " + TRUE + " or "
					+ FALSE + " (char='T' or 'F').");
		}

		peek = -1;
		return b;
	}

	@Override
	public byte readByte() throws IOException, DataFormatException {
		verifyNextValueType(BYTE);
		peek = -1;
		return (byte) in.read();
	}

	@Override
	public int readInt32() throws IOException, DataFormatException {
		verifyNextValueType(INT32);
		peek = -1;
		return readInt32Impl();
	}

	@Override
	public long readInt64() throws IOException, DataFormatException {
		verifyNextValueType(INT64);
		peek = -1;
		return readInt64Impl();
	}

	@Override
	public double readDouble() throws IOException, DataFormatException {
		verifyNextValueType(DOUBLE);
		peek = -1;
		return Double.longBitsToDouble(readInt64Impl());
	}

	@Override
	public BigDecimal readHuge() throws IOException, DataFormatException {
		verifyNextValueType(HUGE);
		peek = -1;

		int length = readInt32Impl();

		if (length < 0)
			throw new DataFormatException("Read a negative length of ["
					+ length + "] for a huge value, but length must be >= 0.");

		return new BigDecimal(readStringAsChars(length));
	}

	@Override
	public String readString() throws IOException, DataFormatException {
		return new String(readStringAsChars());
	}

	@Override
	public char[] readStringAsChars() throws IOException, DataFormatException {
		verifyNextValueType(STRING);
		peek = -1;

		int length = readInt32Impl();

		if (length < 0)
			throw new DataFormatException("Read a negative length of ["
					+ length + "] for string value, but length must be >= 0.");

		return readStringAsChars(length);
	}

	@Override
	public int readArrayHeader() throws IOException, DataFormatException {
		verifyNextValueType(ARRAY);
		peek = -1;
		return readInt32Impl();
	}

	@Override
	public int readObjectHeader() throws IOException, DataFormatException {
		verifyNextValueType(OBJECT);
		peek = -1;
		return readInt32Impl();
	}

	private void verifyNextValueType(byte expected) throws IOException,
			DataFormatException {
		// Peek at the next byte/value marker if the caller hasn't done so yet.
		if (peek == -1) {
			nextValueType();

			if (peek == -1)
				throw new IOException(
						"End of Stream encountered while trying to read the next ASCII value marker.");
		}

		/*
		 * nextValueType verifies the marker is supported, so we can cast to
		 * char for a more informative exception.
		 */
		if (peek != expected)
			throw new DataFormatException("Encountered a byte value of " + peek
					+ " (char='" + ((char) peek)
					+ "') instead of the expected byte value " + expected
					+ " (char='" + ((char) expected) + "').");
	}

	protected int readInt32Impl() throws IOException {
		int read = in.read(int32Buffer);

		if (read < int32Buffer.length)
			throw new DataFormatException(
					"Attempted to read "
							+ int32Buffer.length
							+ " bytes to reconstruct the int32 value, instead was only able to read "
							+ read + " bytes.");

		return (((int) int32Buffer[0] << 24) + ((int) int32Buffer[1] << 16)
				+ ((int) int32Buffer[2] << 8) + ((int) int32Buffer[3] << 0));
	}

	protected long readInt64Impl() throws IOException {
		int read = in.read(int64Buffer);

		if (read < int64Buffer.length)
			throw new DataFormatException(
					"Attempted to read "
							+ int64Buffer.length
							+ " bytes to reconstruct the int64 value, instead was only able to read "
							+ read + " bytes.");

		return (((long) int64Buffer[0] << 56)
				+ (((long) int64Buffer[1] & 255) << 48)
				+ (((long) int64Buffer[2] & 255) << 40)
				+ (((long) int64Buffer[3] & 255) << 32)
				+ (((long) int64Buffer[4] & 255) << 24)
				+ (((long) int64Buffer[5] & 255) << 16)
				+ (((long) int64Buffer[6] & 255) << 8) + (((long) int64Buffer[7] & 255) << 0));
	}

	protected char[] readStringAsChars(int length) throws IOException {
		/*
		 * CharsetDecoder can only decode into a CharBuffer, so we wrap our
		 * return array with one, fill it up, then return the now-populated
		 * array with our decoded chars.
		 */
		char[] text = new char[length];
		CharBuffer output = CharBuffer.wrap(text);

		int read = 0;
		int remaining = length;

		/*
		 * We read until we are out of bytes, a more accurate stop condition
		 * (once we have read remaining bytes) is triggered inside the loop.
		 */
		while ((read = in.read(buffer, 0, remaining)) != -1) {
			// Update the remaining amount that needs to be read.
			remaining -= read;

			// Wrap and decode the resulting bytes.
			ByteBuffer temp = ByteBuffer.wrap(buffer, 0, read);
			decoder.decode(temp, output, false);
		}

		// Flush any pending content to the output array.
		decoder.flush(output);

		return text;
	}
}