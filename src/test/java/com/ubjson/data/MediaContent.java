package com.ubjson.data;

/**
 * Well-known test data from the JVM Serializers Benchmark project in Object
 * form mapped to the same JSON types defined in the test data.
 * <p/>
 * Minor modification made to the enum types to make them {@link String}s
 * instead to make the comparison with JSON more direct.
 * 
 * @author Riyad Kalla (software@thebuzzmedia.com)
 * @see <a href="https://github.com/eishay/jvm-serializers/wiki">JVM Serializers
 *      Benchmark Project</a>
 * @see <a href="https://github.com/eishay/jvm-serializers/wiki/TestValue">JVM
 *      Serializers Test Data</a>
 */
public class MediaContent {
	public Media media = new Media();
	public Image[] images = {
			new Image("http://javaone.com/keynote_large.jpg",
					"Javaone Keynote", 1024, 768, "LARGE"),
			new Image("http://javaone.com/keynote_small.jpg",
					"Javaone Keynote", 320, 240, "SMALL") };

	public class Media {
		public String uri = "http://javaone.com/keynote.mpg";
		public String title = "Javaone Keynote";
		public int width = 640;
		public int height = 480;
		public String format = "video/mpg4";
		public int duration = 18000000;
		public int size = 58982400;
		public int bitrate = 262144;
		public String[] persons = { "Bill Gates", "Steve Jobs" };
		public String player = "JAVA";
		public String copyright = null;
	}

	public class Image {
		public String uri;
		public String title;
		public int width;
		public int height;
		public String size;

		public Image(String uri, String title, int width, int height,
				String size) {
			this.uri = uri;
			this.title = title;
			this.width = width;
			this.height = height;
			this.size = size;
		}
	}
}