package org.ubjson.pojo;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Simple POJO that is pre-populated with the values from media.1.origina.cks to
 * make testing easier.
 */
public class MediaContent {
	/*
	 * Used to write objects of type {@link MediaContent} out using the
	 * Universal Binary JSON format.
	 */
	public static void write(DataOutputStream o, MediaContent m)
			throws IOException {
		// root object, 2 elements
		o.writeChar('O');
		o.writeInt(2);

		// media object, 11 elements
		o.writeChar('O');
		o.writeInt(11);

		writeString(o, m.media.uri);
		writeString(o, m.media.title);
		writeInt(o, m.media.width);
		writeInt(o, m.media.height);
		writeString(o, m.media.format);
		writeInt(o, m.media.duration);
		writeInt(o, m.media.size);
		writeInt(o, m.media.bitrate);
		writeString(o, m.media.persons);
		writeString(o, m.media.player);
		writeString(o, m.media.copyright);

		// array object, 2 elements
		o.writeChar('A');
		o.writeInt(2);

		// array[0]
		o.writeChar('O');
		o.writeInt(5);

		writeString(o, m.images.get(0).uri);
		writeString(o, m.images.get(0).title);
		writeInt(o, m.images.get(0).width);
		writeInt(o, m.images.get(0).height);
		writeString(o, m.images.get(0).size);

		// array[1]
		o.writeChar('O');
		o.writeInt(5);

		writeString(o, m.images.get(1).uri);
		writeString(o, m.images.get(1).title);
		writeInt(o, m.images.get(1).width);
		writeInt(o, m.images.get(1).height);
		writeString(o, m.images.get(1).size);
	}

	public static MediaContent read(DataInputStream in) throws IOException {
		MediaContent m = new MediaContent();

		// root object, 2 elements
		in.readChar();
		in.readInt();
		// System.out.println(in.readInt() + " root elements");

		// media object, 11 elements
		in.readChar();
		in.readInt();
		// System.out.println("\t" + in.readInt() + " elements in Media");

		m.media.uri = readString(in);
		m.media.title = readString(in);
		m.media.width = readInt(in);
		m.media.height = readInt(in);
		m.media.format = readString(in);
		m.media.duration = readInt(in);
		m.media.size = readInt(in);
		m.media.bitrate = readInt(in);
		m.media.persons = readString(in);
		m.media.player = readString(in);
		m.media.copyright = readString(in);

		// array object, 2 elements
		in.readChar();
		in.readInt();
		// System.out.println("\t" + in.readInt() + " elements in Array");
		m.images.clear();

		Image i = new Image(null, null, 0, 0, null);

		// array[0]
		in.readChar();
		in.readInt();
		// System.out.println("\t\t" + in.readInt()
		// + " elements in Array[0] Image");

		i.uri = readString(in);
		i.title = readString(in);
		i.width = readInt(in);
		i.height = readInt(in);
		i.size = readString(in);
		m.images.add(i);

		i = new Image(null, null, 0, 0, null);

		// array[1]
		in.readChar();
		in.readInt();
		// System.out.println("\t\t" + in.readInt()
		// + " elements in Array[0] Image");

		i.uri = readString(in);
		i.title = readString(in);
		i.width = readInt(in);
		i.height = readInt(in);
		i.size = readString(in);
		m.images.add(i);

		return m;
	}

	static int readInt(DataInputStream in) throws IOException {
		in.readChar();
		return in.readInt();
	}

	static void writeInt(DataOutputStream o, int v) throws IOException {
		o.writeChar('I');
		o.writeInt(v);
	}

	static String readString(DataInputStream in) throws IOException {
		in.readChar();
		int len = in.readInt();
		byte[] data = new byte[len];

		in.read(data);

		return new String(data, "UTF-8");
	}

	static void writeString(DataOutputStream o, String s) throws IOException {
		o.writeChar('S');

		byte[] data = s.getBytes("UTF-8");
		o.writeInt(data.length);

		o.write(data);
	}

	public Media media;
	public List<Image> images;

	public MediaContent() {
		media = new Media();

		images = new ArrayList<MediaContent.Image>();
		images.add(new Image("http://javaone.com/keynote_large.jpg",
				"Javaone Keynote", 1024, 768, "Large"));
		images.add(new Image("http://javaone.com/keynote_small.jpg",
				"Javaone Keynote", 320, 240, "Small"));
	}

	public static class Media {
		public String uri = "http://javaone.com/keynote.mpg";
		public String title = "Javaone Keynote";
		public int width = 640;
		public int height = 480;
		public String format = "video/mpg4";
		public int duration = 18000000;
		public int size = 58982400;
		public int bitrate = 262144;
		public String persons = "Bill Gates";
		public String player = "Java";
		public String copyright = "None";

		@Override
		public String toString() {
			return "Media [uri=" + uri + ", title=" + title + ", width="
					+ width + ", height=" + height + ", format=" + format
					+ ", duration=" + duration + ", size=" + size
					+ ", bitrate=" + bitrate + ", persons=" + persons
					+ ", player=" + player + ", copyright=" + copyright + "]";
		}
	}

	public static class Image {
		public String uri;
		public String title;
		public int width;
		public int height;
		public String size;
		
		public Image() {
			
		}

		public Image(String uri, String title, int width, int height,
				String size) {
			this.uri = uri;
			this.title = title;
			this.width = width;
			this.height = height;
			this.size = size;
		}

		@Override
		public String toString() {
			return "Image [uri=" + uri + ", title=" + title + ", width="
					+ width + ", height=" + height + ", size=" + size + "]";
		}
	}

	@Override
	public String toString() {
		return "MediaContent [media=" + media + ", images=" + images + "]";
	}
}
