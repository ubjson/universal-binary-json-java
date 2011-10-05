package org.ubjson.benchmark;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.ubjson.pojo.MediaContent;

public class Benchmark {
	static int ITERS = 1000;

	static ByteArrayOutputStream buf = new ByteArrayOutputStream(320 * ITERS);

	public static void main(String[] args) throws JsonParseException,
			JsonMappingException, IOException {
		System.out.println("Jackson JSON Serial/Deserial");
		testJSON();

		System.out.println("Binary JSON Serial/Deserial");
		binaryJSON();
	}

	static void testJSON() throws JsonParseException, JsonMappingException,
			IOException {
		InputStream compactJSON = Benchmark.class
				.getResourceAsStream("media-content-jackson.txt");
		byte[] data = new byte[472];
		compactJSON.read(data);
		// MediaContent mc = new MediaContent();
		// m.writeValue(new File("media-content-jackson.txt"), mc);
		ObjectMapper m = new ObjectMapper();

		long t = System.currentTimeMillis();
		for (int i = 0; i < ITERS; i++)
			m.readValue(data, MediaContent.class);
		System.out.println(ITERS + "x Read, Elapsed Time: "
				+ (System.currentTimeMillis() - t) + " ms");
		// MediaContent mc = m.readValue(compactJSON, MediaContent.class);
	}

	static void binaryJSON() throws IOException {
		DataOutputStream out = new DataOutputStream(buf);
		MediaContent mc = new MediaContent();

		long t = System.currentTimeMillis();
		for (int i = 0; i < ITERS; i++)
			MediaContent.write(out, mc);
		System.out.println(ITERS + "x Write, Elapsed Time: "
				+ (System.currentTimeMillis() - t) + " ms");
		out.flush();
		out.close();

		byte[] data = buf.toByteArray();
		FileOutputStream fos = new FileOutputStream("benchmark.bin");
		fos.write(data);
		fos.flush();
		fos.close();

		DataInputStream in = new DataInputStream(new ByteArrayInputStream(data));

		t = System.currentTimeMillis();
		for (int i = 0; i < ITERS; i++)
			MediaContent.read(in);
		System.out.println(ITERS + "x Read, Elapsed Time: "
				+ (System.currentTimeMillis() - t) + " ms");

		in.close();
	}
}