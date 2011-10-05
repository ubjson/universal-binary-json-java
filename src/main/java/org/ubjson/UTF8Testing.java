package org.ubjson;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class UTF8Testing {
	static String EN = "hello";
	static String RU = "привет";
	static String AR = "مرحبا";

	public static void main(String[] args) throws IOException {
		printStats(EN);
		printStats(RU);
		printStats(AR);
		System.out.println("--------------");

		DataOutputStream out = new DataOutputStream(new FileOutputStream(
				"lang.bin"));
		write(out, EN);
		write(out, RU);
		write(out, AR);
		out.close();

		DataInputStream in = new DataInputStream(
				new FileInputStream("lang.bin"));
		read(in);
		read(in);
		read(in);
		in.close();
	}

	static void printStats(String s) throws UnsupportedEncodingException {
		System.out.println(s);
		System.out.println("\tLength: " + s.length());
		System.out.println("\tByte Length: " + s.getBytes("UTF-8").length);
	}

	static void write(DataOutputStream out, String s) throws IOException {
		byte[] data = s.getBytes("UTF-8");

		out.writeInt(data.length);
		out.write(data);
		out.flush();
	}

	static void read(DataInputStream in) throws IOException {
		int len = in.readInt();
		byte[] data = new byte[len];

		in.read(data);
		String s = new String(data, "UTF-8");
		System.out.println("READ");
		printStats(s);
	}
}