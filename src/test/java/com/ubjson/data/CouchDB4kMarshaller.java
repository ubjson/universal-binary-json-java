package com.ubjson.data;

import java.io.IOException;

import org.ubjson.UBJInputStream;
import org.ubjson.UBJOutputStream;

public class CouchDB4kMarshaller {
	public static void serialize(CouchDB4k db, UBJOutputStream out)
			throws IOException {
		// root obj
		out.writeObjectHeader(9);

		// data elems
		out.writeString("data3");
		out.writeString(db.data3);

		out.writeString("data4");
		out.writeString(db.data4);

		out.writeString("data0");
		out.writeString(db.data0);

		out.writeString("data7");
		out.writeString(db.data7);

		// data 5: obj
		out.writeString("data5");
		out.writeObjectHeader(5);

		// data 5: integers
		out.writeString("integers");
		out.writeArrayHeader(db.data5.integers.length);

		// data 5: integers: elements
		for (int i = 0; i < db.data5.integers.length; i++)
			out.writeInt32(db.data5.integers[i]);

		out.writeString("float1");
		out.writeFloat(db.data5.float1);

		out.writeString("float2");
		out.writeFloat(db.data5.float2);

		// data 5: nested1 obj
		out.writeString("nested1");
		out.writeObjectHeader(2);

		// data 5: nested1: integers
		out.writeString("integers");
		out.writeArrayHeader(db.data5.nested1.integers.length);

		// data 5: nested1: integers: elements
		for (int i = 0; i < db.data5.nested1.integers.length; i++)
			out.writeInt32(db.data5.nested1.integers[i]);

		// data 5: nested1: floats
		out.writeString("floats");
		out.writeArrayHeader(db.data5.nested1.floats.length);

		// data 5: nested1: floats: elements
		for (int i = 0; i < db.data5.nested1.floats.length; i++)
			out.writeFloat(db.data5.nested1.floats[i]);

		// data 5: nested2 obj
		out.writeString("nested2");
		out.writeObjectHeader(3);

		// data 5: nested2: integers
		out.writeString("integers");
		out.writeArrayHeader(db.data5.nested2.integers.length);

		for (int i = 0; i < db.data5.nested2.integers.length; i++)
			out.writeInt32(db.data5.nested2.integers[i]);

		out.writeString("float1");
		out.writeFloat(db.data5.nested2.float1);

		out.writeString("float2");
		out.writeFloat(db.data5.nested2.float2);

		// strings
		out.writeString("strings");
		out.writeArrayHeader(db.strings.length);

		// strings: elements
		for (int i = 0; i < db.strings.length; i++)
			out.writeString(db.strings[i]);

		out.writeString("data1");
		out.writeString(db.data1);

		out.writeString("integers2");
		out.writeArrayHeader(db.integers2.length);

		for (int i = 0; i < db.integers2.length; i++)
			out.writeInt32(db.integers2[i]);

		// moreNested obj
		out.writeString("moreNested");
		out.writeObjectHeader(5);

		out.writeString("integers");
		out.writeArrayHeader(db.moreNested.integers.length);

		for (int i = 0; i < db.moreNested.integers.length; i++)
			out.writeInt32(db.moreNested.integers[i]);

		out.writeString("float1");
		out.writeFloat(db.moreNested.float1);

		out.writeString("float2");
		out.writeFloat(db.moreNested.float2);

		// moreNested: nested1 obj
		out.writeString("nested1");
		out.writeObjectHeader(1);

		out.writeString("integers");
		out.writeArrayHeader(db.moreNested.nested1.integers.length);

		for (int i = 0; i < db.moreNested.nested1.integers.length; i++)
			out.writeInt32(db.moreNested.nested1.integers[i]);

		// moreNested: nested2 obj
		out.writeString("nested2");
		out.writeObjectHeader(2);

		out.writeString("strings");
		out.writeArrayHeader(db.moreNested.nested2.strings.length);

		for (int i = 0; i < db.moreNested.nested2.strings.length; i++)
			out.writeString(db.moreNested.nested2.strings[i]);

		out.writeString("integers");
		out.writeArrayHeader(db.moreNested.nested2.integers.length);

		for (int i = 0; i < db.moreNested.nested2.integers.length; i++)
			out.writeInt32(db.moreNested.nested2.integers[i]);
	}

	public static CouchDB4k deserialize(UBJInputStream in) throws IOException {
		CouchDB4k db = new CouchDB4k();

		// root obj
		in.readObjectLength();

		in.readStringAsChars();
		db.data3 = in.readString();

		in.readStringAsChars();
		db.data4 = in.readString();

		in.readStringAsChars();
		db.data0 = in.readString();

		in.readStringAsChars();
		db.data7 = in.readString();

		// data5 obj
		in.readStringAsChars();
		in.readObjectLength();

		in.readStringAsChars();
		in.readArrayLength();

		for (int i = 0; i < db.data5.integers.length; i++)
			db.data5.integers[i] = in.readInt32();

		in.readStringAsChars();
		db.data5.float1 = in.readFloat();

		in.readStringAsChars();
		db.data5.float2 = in.readFloat();

		// data5: nested1 obj
		in.readStringAsChars();
		in.readObjectLength();

		in.readStringAsChars();
		in.readArrayLength();

		for (int i = 0; i < db.data5.nested1.integers.length; i++)
			db.data5.nested1.integers[i] = in.readInt32();

		in.readStringAsChars();
		in.readArrayLength();

		for (int i = 0; i < db.data5.nested1.floats.length; i++)
			db.data5.nested1.floats[i] = in.readFloat();

		// data5: nested2 obj
		in.readStringAsChars();
		in.readObjectLength();

		in.readStringAsChars();
		in.readArrayLength();

		for (int i = 0; i < db.data5.nested2.integers.length; i++)
			db.data5.nested2.integers[i] = in.readInt32();

		in.readStringAsChars();
		db.data5.nested2.float1 = in.readFloat();

		in.readStringAsChars();
		db.data5.nested2.float2 = in.readFloat();

		// data5 obj
		in.readStringAsChars();
		in.readArrayLength();

		for (int i = 0; i < db.strings.length; i++)
			db.strings[i] = in.readString();

		in.readStringAsChars();
		db.data1 = in.readString();

		in.readStringAsChars();
		in.readArrayLength();

		for (int i = 0; i < db.integers2.length; i++)
			db.integers2[i] = in.readInt32();

		// data5: moreNested obj
		in.readStringAsChars();
		in.readObjectLength();

		in.readStringAsChars();
		in.readArrayLength();

		for (int i = 0; i < db.moreNested.integers.length; i++)
			db.moreNested.integers[i] = in.readInt32();

		in.readStringAsChars();
		db.moreNested.float1 = in.readFloat();

		in.readStringAsChars();
		db.moreNested.float2 = in.readFloat();

		// data5: moreNested: nested1 obj
		in.readStringAsChars();
		in.readObjectLength();

		in.readStringAsChars();
		in.readArrayLength();

		for (int i = 0; i < db.moreNested.nested1.integers.length; i++)
			db.moreNested.nested1.integers[i] = in.readInt32();

		// data5: moreNested: nested2 obj
		in.readStringAsChars();
		in.readObjectLength();

		in.readStringAsChars();
		in.readArrayLength();

		for (int i = 0; i < db.moreNested.nested2.strings.length; i++)
			db.moreNested.nested2.strings[i] = in.readString();

		in.readStringAsChars();
		in.readArrayLength();

		for (int i = 0; i < db.moreNested.nested2.integers.length; i++)
			db.moreNested.nested2.integers[i] = in.readInt32();

		return db;
	}
}