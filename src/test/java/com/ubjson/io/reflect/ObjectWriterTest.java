package com.ubjson.io.reflect;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.ubjson.CouchDB4k;
import org.ubjson.CouchDB4kMarshaller;
import org.ubjson.MediaContent;
import org.ubjson.MediaContentMarshaller;
import org.ubjson.TwitterTimeline;
import org.ubjson.TwitterTimelineMarshaller;
import org.ubjson.io.UBJInputStream;
import org.ubjson.io.UBJOutputStream;
import org.ubjson.io.reflect.IObjectWriter;
import org.ubjson.io.reflect.ObjectWriter;

public class ObjectWriterTest {
	private static IObjectWriter writer;

	@Before
	public void setup() {
		writer = new ObjectWriter();
	}

	@After
	public void teardown() {
		writer = null;
	}

	@Test
	public void testMediaContent() throws IOException {
		File f = new File("MediaContent-ObjectWriter-SAFE-TO-DELETE.ubj");

		if (f.exists())
			f.delete();

		// Write out MediaContent POJO using reflection writer.
		UBJOutputStream out = new UBJOutputStream(new FileOutputStream(f));
		writer.writeObject(out, new MediaContent());

		out.flush();
		out.close();

		// Stream to the original
		UBJInputStream origStream = new UBJInputStream(
				MediaContent.class.getResourceAsStream("MediaContent.ubj"));

		// Stream to the one we just wrote out.
		UBJInputStream testStream = new UBJInputStream(new FileInputStream(f));

		// Deserialize MediaContent's from both files.
		MediaContent orig = MediaContentMarshaller.deserialize(origStream);
		MediaContent test = MediaContentMarshaller.deserialize(testStream);

		origStream.close();
		testStream.close();

		// Ensure both MediaContents are the same.
		Assert.assertTrue(orig.equals(test));

		// Delete the temp test file.
		Assert.assertTrue(f.delete());
	}

	@Test
	public void testCouchDB4k() throws IOException {
		File f = new File("CouchDB4k-ObjectWriter-SAFE-TO-DELETE.ubj");

		if (f.exists())
			f.delete();

		// Write out MediaContent POJO using reflection writer.
		UBJOutputStream out = new UBJOutputStream(new FileOutputStream(f));
		writer.writeObject(out, new CouchDB4k());

		out.flush();
		out.close();

		// Stream to the original
		UBJInputStream origStream = new UBJInputStream(
				CouchDB4k.class.getResourceAsStream("CouchDB4k.ubj"));

		// Stream to the one we just wrote out.
		UBJInputStream testStream = new UBJInputStream(new FileInputStream(f));

		// Deserialize MediaContent's from both files.
		CouchDB4k orig = CouchDB4kMarshaller.deserialize(origStream);
		CouchDB4k test = CouchDB4kMarshaller.deserialize(testStream);

		origStream.close();
		testStream.close();

		// Ensure both MediaContents are the same.
		Assert.assertTrue(orig.equals(test));

		// Delete the temp test file.
		Assert.assertTrue(f.delete());
	}

	@Test
	public void testTwitterTimeline() throws IOException {
		File f = new File("TwitterTimeline-ObjectWriter-SAFE-TO-DELETE.ubj");

		if (f.exists())
			f.delete();

		// Write out MediaContent POJO using reflection writer.
		UBJOutputStream out = new UBJOutputStream(new FileOutputStream(f));
		writer.writeObject(out, new TwitterTimeline());

		out.flush();
		out.close();

		// Stream to the original
		UBJInputStream origStream = new UBJInputStream(
				TwitterTimeline.class
						.getResourceAsStream("TwitterTimeline.ubj"));

		// Stream to the one we just wrote out.
		UBJInputStream testStream = new UBJInputStream(new FileInputStream(f));

		// Deserialize MediaContent's from both files.
		TwitterTimeline orig = TwitterTimelineMarshaller
				.deserialize(origStream);
		TwitterTimeline test = TwitterTimelineMarshaller
				.deserialize(testStream);

		origStream.close();
		testStream.close();

		// Ensure both MediaContents are the same.
		Assert.assertTrue(orig.equals(test));

		// Delete the temp test file.
		Assert.assertTrue(f.delete());
	}
}