package org.ubjson;

import java.util.Arrays;

public class CouchDB4k {
	public String data3 = "ColreUHAtuYoUOx1N4ZloouQt2o6ugnUT6eYtS10gu7niM8i0vEiNufpk1RlMQXaHXlIwQBDsMFDFUQcFeg2vW5eD259Xm";
	public String data4 = "zCxriJhL726WNNTdJJzurgSA8vKT6rHA0cFCb9koZcLUMXg4rmoXVPqIHWYaCV0ovl2t6xm7I1Hm36jXpLlXEb8fRfbwBeTW2V0OAsVqYH8eAT";
	public String data0 = "9EVqHm5ARqcEB5jq2D14U2bCJPyBY0JWDr1Tjh8gTB0sWUNjqYiWDxFzlx6S";
	public String data7 = "Bi1ujcgEvfADfBeyZudE7nwxc3Ik8qpYjsJIfKmwOMEbV2L3Bi0x2tcRpGuf4fiyvIbypDvJN1PPdQtfQW1Gv6zccXHwwZwKzUq6";

	public Data5 data5 = new Data5();

	public String[] strings = {
			"edx5XzRkPVeEW2MBQzQMcUSuMI4FntjhlJ9VGhQaBHKPEazAaT",
			"2fQUbzRUax4A",
			"jURcBZ0vrJcmf2roZUMzZJQoTsKZDIdj7KhO7itskKvM80jBU9",
			"8jKLmo3N2zYdKyTyfTczfr2x6bPaarorlnTNJ7r8lIkiZyBvrP",
			"jbUeAVOdBSPzYmYhH0sabUHUH39O5e",
			"I8yAQKZsyZhMfpzWjArQU9pQ6PfU6b14q2eWvQjtCUdgAUxFjg",
			"97N8ZmGcxRZO4ZabzRRcY4KVHqxJwQ8qY", "0DtY1aWXmUfJENt9rYW9",
			"DtpBUEppPwMnWexi8eIIxlXRO3GUpPgeNFG9ONpWJYvk8xBkVj",
			"YsX8V2xOrTw6LhNIMMhO4F4VXFyXUXFr66L3sTkLWgFA9NZuBV",
			"fKYYthv8iFvaYoFoYZyB",
			"zGuLsPXoJqMbO4PcePteZfDMYFXdWtvNF8WvaplXypsd6" };

	public String data1 = "9EVqHm5ARqcEB5jq21v2g0jVcG9CXB0Abk7uAF4NHYyTzeF3TnHhpZBECD14U2bCJPyBY0JWDr1Tjh8gTB0sWUNjqYiWDxFzlx6S";

	public int[] integers2 = { 756509, 116117, 776378, 275045, 703447, 50156,
			685803, 147958, 941747, 905651, 57367, 530248, 312888, 740951,
			988947, 450154 };

	public MoreNested moreNested = new MoreNested();

	@Override
	public boolean equals(Object obj) {
		CouchDB4k c = (CouchDB4k) obj;
		return (data3.equals(c.data3) && data4.equals(c.data4)
				&& data0.equals(c.data0) && data7.equals(c.data7)
				&& data5.equals(c.data5) && Arrays.equals(strings, c.strings)
				&& data1.equals(c.data1)
				&& Arrays.equals(integers2, c.integers2) && moreNested
					.equals(c.moreNested));
	}

	public class Data5 {
		public int[] integers = { 756509, 116117, 776378, 275045, 703447,
				50156, 685803, 147958, 941747, 905651, 57367, 530248, 312888,
				740951, 988947, 450154 };

		public float float1 = 76.572f;
		public float float2 = 83.5299f;

		public Nested1 nested1 = new Nested1();
		public Nested2 nested2 = new Nested2();

		@Override
		public boolean equals(Object obj) {
			Data5 d = (Data5) obj;
			return (Arrays.equals(integers, d.integers) && (float1 == d.float1)
					&& (float2 == d.float2) && nested1.equals(d.nested1) && nested2
						.equals(d.nested2));
		}

		public class Nested1 {
			public int[] integers = { 756509, 116117, 776378, 275045, 703447,
					50156, 685803, 147958, 941747, 905651, 57367, 530248,
					312888, 740951, 988947, 450154 };
			public float[] floats = { 43121609.5543f, 99454976.3019f,
					32945584.756f, 18122905.9212f, 45893183.44f,
					63052200.6225f, 69032152.6897f, 3748217.6946f,
					75449850.474f, 37111527.415f, 84852536.859f, 32906366.487f,
					27027600.417f, 63874310.5614f, 39440408.51f,
					97176857.1716f, 97438252.1171f, 49728043.5056f,
					40818570.245f, 41415831.8949f, 24796297.4251f,
					2819085.3449f, 84263963.4848f, 74503228.6878f,
					67925677.403f, 4758851.9417f, 75227407.9214f,
					76946667.8403f, 72518275.9469f, 94167085.9588f,
					75883067.8321f, 27389831.6101f, 57987075.5053f,
					1298995.2674f, 14590614.6939f, 45292214.2242f,
					3332166.364f, 53784167.729f, 25193846.1867f, 81456965.477f,
					68532032.39f, 73820009.7952f, 57736110.5717f,
					37304166.7363f, 20054244.864f, 29746392.7397f, 86467624.6f,
					45192685.8793f, 44008816.5186f, 1861872.8736f,
					14595859.467f, 87795257.6703f, 57768720.8303f,
					18290154.3126f, 45893183.44f, 63052200.6225f,
					69032152.6897f, 3748217.6946f, 75449850.474f,
					37111527.415f, 84852536.859f, 32906366.487f, 27027600.417f,
					63874310.5614f, 39440408.51f, 97176857.1716f,
					97438252.1171f, 49728043.5056f, 40818570.245f,
					41415831.8949f, 24796297.4251f, 2819085.3449f,
					84263963.4848f, 74503228.6878f, 67925677.403f,
					4758851.9417f, 75227407.9214f, 76946667.8403f,
					72518275.9469f, 94167085.9588f, 75883067.8321f,
					27389831.6101f, 57987075.5053f, 1298995.2674f,
					80858801.2712f, 98262252.4656f, 51612877.944f,
					33397812.7835f, 36089655.3049f, 50164685.8153f,
					16852105.5192f, 61171929.752f, 86376339.7175f,
					73009014.5521f, 7397302.331f, 34345128.9589f,
					98343269.4418f, 95039116.9058f, 44833102.5752f,
					51052997.8873f, 22719195.6783f, 64883244.8699f };

			@Override
			public boolean equals(Object obj) {
				Nested1 n = (Nested1) obj;
				return (Arrays.equals(integers, n.integers) && Arrays.equals(
						floats, n.floats));
			}
		}

		public class Nested2 {
			public int[] integers = { 756509, 116117, 776378, 275045, 703447,
					50156, 685803, 147958, 941747, 905651, 57367, 530248,
					312888, 740951, 988947, 450154 };

			public float float1 = 76.572f;
			public float float2 = 83.5299f;

			@Override
			public boolean equals(Object obj) {
				Nested2 n = (Nested2) obj;
				return (Arrays.equals(integers, n.integers)
						&& float1 == n.float1 && float2 == n.float2);
			}
		}
	}

	public class MoreNested {
		public int[] integers = { 756509, 116117, 776378, 275045, 703447,
				50156, 685803, 147958, 941747, 905651, 57367, 530248, 312888,
				740951, 988947, 450154 };

		public float float1 = 76.572f;
		public float float2 = 83.5299f;

		public Nested1 nested1 = new Nested1();
		public Nested2 nested2 = new Nested2();

		@Override
		public boolean equals(Object obj) {
			MoreNested n = (MoreNested) obj;
			return (Arrays.equals(integers, n.integers) && (float1 == n.float1)
					&& (float2 == n.float2) && nested1.equals(n.nested1) && nested2
						.equals(n.nested2));
		}

		public class Nested1 {
			public int[] integers = { 756509, 116117, 776378, 275045, 703447,
					50156, 685803, 147958, 941747, 905651, 57367, 530248,
					312888, 740951, 988947, 450154 };

			@Override
			public boolean equals(Object obj) {
				Nested1 n = (Nested1) obj;
				return Arrays.equals(integers, n.integers);
			}
		}

		public class Nested2 {
			public String[] strings = { "2fQUbzRUax4A",
					"jURcBZ0vrJcmf2roZUMzZJQoTsKZDIdj7KhO7itskKvM80jBU9",
					"8jKLmo3N2zYdKyTyfTczfr2x6bPaarorlnTNJ7r8lIkiZyBvrP",
					"jbUeAVOdBSPzYmYhH0sabUHUH39O5e",
					"I8yAQKZsyZhMfpzWjArQU9pQ6PfU6b14q2eWvQjtCUdgAUxFjg",
					"97N8ZmGcxRZO4ZabzRRcY4KVHqxJwQ8qY",
					"0DtY1aWXmUfJENt9rYW9",
					"DtpBUEppPwMnWexi8eIIxlXRO3GUpPgeNFG9ONpWJYvk8xBkVj",
					"YsX8V2xOrTw6LhNIMMhO4F4VXFyXUXFr66L3sTkLWgFA9NZuBV",
					"fKYYthv8iFvaYoFoYZyB",
					"zGuLsPXoJqMbO4PcePteZfDMYFXdWtvNF8WvaplXypsd6" };
			public int[] integers = { 756509, 116117, 776378, 57367, 530248,
					312888, 740951, 988947, 450154 };

			@Override
			public boolean equals(Object obj) {
				Nested2 n = (Nested2) obj;
				return (Arrays.equals(strings, n.strings) && Arrays.equals(
						integers, n.integers));
			}
		}
	}
}