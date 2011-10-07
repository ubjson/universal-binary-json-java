package org.ubjson.io.charset;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;

public class StreamEncoder {
	public static final Charset UTF8_CHARSET = Charset.forName("UTF-8");

	private byte[] writeBuffer;
	private CharsetEncoder encoder;

	public StreamEncoder() {
		this(UTF8_CHARSET);
	}

	public StreamEncoder(Charset charset) throws IllegalArgumentException {
		if (charset == null)
			throw new IllegalArgumentException("charset cannot be null");

		writeBuffer = new byte[8192];
		encoder = charset.newEncoder();
	}

	public void encode(CharBuffer text, OutputStream stream)
			throws IllegalArgumentException, IOException {
		if (stream == null)
			throw new IllegalArgumentException("stream cannot be null");

		if (text == null || !text.hasRemaining())
			return;

		encoder.reset();
		ByteBuffer dest = ByteBuffer.wrap(writeBuffer);

		while (text.hasRemaining()) {
			dest.clear();
			encoder.encode(text, dest, false);

			stream.write(writeBuffer, 0, dest.remaining());
		}

		dest.clear();
		encoder.encode(text, dest, true);
		encoder.flush(dest);
		dest.flip();

		if (dest.hasRemaining())
			stream.write(writeBuffer, 0, dest.remaining());
	}
}