package org.ubjson;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.ubjson.io.UBJInputStream;
import org.ubjson.io.UBJOutputStream;

public class Example {
	public static void main(String[] args) throws IOException {
		if(true) {
			int32Bench();
			System.exit(0);
		}
		
		testNull();
		testBoolean();
		testByte();
		testInt32();
		testInt64();
		testDouble();
		testString();
	}

	static UBJOutputStream output(String filename) throws FileNotFoundException {
		return new UBJOutputStream(new FileOutputStream(filename));
	}

	static UBJInputStream input(String filename) throws FileNotFoundException {
		return new UBJInputStream(new FileInputStream(filename));
	}
	
	/*
	 * TODO: This is failing too as the wriong values get read back even though
	 * what is written in binary is right.
	 */
	static void int32Bench() throws IOException {
		UBJOutputStream out = output("int32-bench.ubj");
		
		for(int i = 0; i < 10000; i++)
			out.writeInt32(i);
		
		out.flush();
		out.close();

		UBJInputStream in = input("int32-bench.ubj");
		
		for(int i = 0; i < 10000; i++)
			System.out.println("Read: " + in.readInt32());

		in.close();
	}

	static void testNull() throws IOException {
		UBJOutputStream out = output("null.ubj");
		out.writeNull();
		out.flush();
		out.close();

		UBJInputStream in = input("null.ubj");
		in.readNull();
		System.out.println("Read Null");
		in.close();
	}

	static void testBoolean() throws IOException {
		UBJOutputStream out = output("boolean.ubj");
		out.writeBoolean(true);
		out.writeBoolean(false);
		out.flush();
		out.close();

		UBJInputStream in = input("boolean.ubj");
		System.out.println("Read Boolean [true, false]: " + in.readBoolean()
				+ " , " + in.readBoolean());
		in.close();
	}

	static void testByte() throws IOException {
		UBJOutputStream out = output("byte.ubj");
		out.writeByte((byte) 13);
		out.flush();
		out.close();

		UBJInputStream in = input("byte.ubj");
		System.out.println("Read Byte [13]: " + in.readByte());
		in.close();
	}

	static void testInt32() throws IOException {
		UBJOutputStream out = output("int32.ubj");
		out.writeInt32(90210);
		out.flush();
		out.close();

		UBJInputStream in = input("int32.ubj");
		System.out.println("Read int32 [90210]: " + in.readInt32());
		in.close();
	}

	static void testInt64() throws IOException {
		long l = System.currentTimeMillis();

		UBJOutputStream out = output("int64.ubj");
		out.writeInt64(l);
		out.flush();
		out.close();

		UBJInputStream in = input("int64.ubj");
		System.out.println("Read int64 [" + l + "]: " + in.readInt64());
		in.close();
	}

	static void testDouble() throws IOException {
		UBJOutputStream out = output("double.ubj");
		out.writeDouble(3.14159);
		out.flush();
		out.close();

		UBJInputStream in = input("double.ubj");
		System.out.println("Read Double [3.14159]: " + in.readDouble());
		in.close();
	}

	static void testString() throws IOException {
		UBJOutputStream out = output("string.ubj");
		out.writeString("Hello World!");
		out.flush();
		out.close();

		UBJInputStream in = input("string.ubj");
		System.out.println("Read Double [Hello World!]: '" + in.readString()
				+ "'");
		in.close();
	}
}