package org.ubjson.io;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.ubjson.TwitterTimeline;
import org.ubjson.TwitterTimelineMarshaller;

public class TwitterTimelineBenchmark {
	private static boolean profiling;

	private static int len;
	private static byte[] data;

	private static UBJOutputStream out = new UBJOutputStream(
			new NullOutputStream());

	public static void main(String[] args) throws IOException {
		// Detect if we are running in profile mode.
		profiling = (args.length > 0 && "-profile".equals(args[0]) ? true
				: false);

		// Write a single CouchDB4k out to byte[]
		ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
		UBJOutputStream stream = new UBJOutputStream(baos);
		TwitterTimelineMarshaller.serialize(new TwitterTimeline(), stream);

		stream.flush();
		stream.close();

		// Remember the example output
		len = baos.getLength();
		data = baos.getArray();

		// Skip these if in profiling mode; they take too long.
		if (!profiling) {
			benchmarkMS(1000000); // 1mil
			benchmarkMS(100000); // 100k
		}

		benchmarkMS(10000); // 10k
		benchmarkNS(1000); // 1k
		benchmarkNS(100); // 100
		benchmarkNS(1); // 1
	}

	public static void benchmarkMS(int iters) throws IOException {
		System.out.println("Benchmark (milliseconds): " + iters
				+ " iterations.");

		long t = 0;
		long tt = 0;
		TwitterTimeline twitt = new TwitterTimeline();

		for (int i = 0; i < iters; i++) {
			tt = System.currentTimeMillis();
			TwitterTimelineMarshaller.serialize(twitt, out);
			t += (System.currentTimeMillis() - tt);
		}

		printTime("Serialization", iters, t, false);
		t = 0;
		tt = 0;

		for (int i = 0; i < iters; i++) {
			UBJInputStream in = new UBJInputStream(new ByteArrayInputStream(
					data, 0, len));

			tt = System.currentTimeMillis();
			TwitterTimelineMarshaller.deserialize(in);
			t += (System.currentTimeMillis() - tt);
		}

		printTime("Deserialization", iters, t, false);
	}

	public static void benchmarkNS(int iters) throws IOException {
		System.out
				.println("Benchmark (nanoseconds): " + iters + " iterations.");

		long t = 0;
		long tt = 0;
		TwitterTimeline twitt = new TwitterTimeline();

		for (int i = 0; i < iters; i++) {
			tt = System.nanoTime();
			TwitterTimelineMarshaller.serialize(twitt, out);
			t += (System.nanoTime() - tt);
		}

		printTime("Serialization", iters, t, true);
		t = 0;
		tt = 0;

		for (int i = 0; i < iters; i++) {
			UBJInputStream in = new UBJInputStream(new ByteArrayInputStream(
					data, 0, len));

			tt = System.nanoTime();
			TwitterTimelineMarshaller.deserialize(in);
			t += (System.nanoTime() - tt);
		}

		printTime("Deserialization", iters, t, true);
	}

	private static void printTime(String label, long iters, long et, boolean ns) {
		long nsPerOp = (ns ? et : (et * 1000000L)) / iters;
		double opsPerSec = (double) iters
				/ (double) ((double) et / 1000d / (ns ? 1000000d : 1d));

		System.out.printf("\t%s, Elapsed Time: %d %s [%d ns/op, %f ops/sec]\n",
				label, et, (ns ? "ns" : "ms"), nsPerOp, opsPerSec);
	}

	private static class NullOutputStream extends OutputStream {
		@Override
		public void write(int b) throws IOException {
			// no-op
		}

		@Override
		public void write(byte[] b) throws IOException {
			// no-op
		}

		@Override
		public void write(byte[] b, int off, int len) throws IOException {
			// no-op
		}
	}
}