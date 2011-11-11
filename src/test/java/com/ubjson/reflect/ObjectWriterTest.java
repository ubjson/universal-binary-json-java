package com.ubjson.reflect;

import java.io.FileOutputStream;
import java.io.IOException;

import org.ubjson.io.UBJOutputStream;
import org.ubjson.io.reflect.IObjectWriter;
import org.ubjson.io.reflect.ObjectWriter;

import com.ubjson.data.CouchDB4k;
import com.ubjson.data.MediaContent;
import com.ubjson.data.TwitterTimeline;

public class ObjectWriterTest {
	public static void main(String[] args) throws IOException {
		IObjectWriter writer = new ObjectWriter();
		UBJOutputStream out = new UBJOutputStream(new FileOutputStream(
				"MediaContent-ObjectWriter.ubj"));

		writer.writeObject(out, new MediaContent(), true);
		out.flush();
		out.close();

		out = new UBJOutputStream(new FileOutputStream(
				"CouchDB4k-ObjectWriter.ubj"));

		writer.writeObject(out, new CouchDB4k(), true);
		out.flush();
		out.close();

		out = new UBJOutputStream(new FileOutputStream(
				"TwitterTimeline-ObjectWriter.ubj"));

		writer.writeObject(out, new TwitterTimeline(), true);
		out.flush();
		out.close();
	}
}