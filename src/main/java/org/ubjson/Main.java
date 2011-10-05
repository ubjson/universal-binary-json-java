package org.ubjson;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.ubjson.tmp.User;

public class Main {
	private static byte[] data;
	private static TempArrayOutputStream outstream = new TempArrayOutputStream();

	public static void main(String[] args) throws IOException {
		User u = new User();

		System.out.println(u);
		doOut(new DataOutputStream(outstream), u);
		doIn(new DataInputStream(new ByteArrayInputStream(
				(data = outstream.getArray()))), u);

//		System.out.println("Array Contents:");
//		for (byte b : data)
//			System.out.print((char)b);
	}

	private static void doOut(DataOutputStream out, User u) throws IOException {
		long t = System.nanoTime();

		// id
		out.writeChar('I');
		out.writeInt(u.id);

		// username
		out.writeChar('S');
		out.writeInt(u.username.length() * 2);
		out.writeUTF(u.username);

		// password
		out.writeChar('S');
		out.writeInt(u.password.length() * 2);
		out.writeUTF(u.password);

		// timestamp
		out.writeChar('L');
		out.writeLong(u.timestamp);

		System.out.println("Serialize: " + (System.nanoTime() - t) + " ns");
		out.flush();
		out.close();

		u.id = -1;
		u.username = null;
		u.password = null;
		u.timestamp = -1;
	}

	private static void doIn(DataInputStream in, User u) throws IOException {
		byte[] buf = new byte[32];
		
		long t = System.nanoTime();
		in.readChar();
		u.id = in.readInt();
		
		in.readChar();
		int l = in.readInt();
//		in.read(buf, 0, l);
//		u.username = new String(buf, 0, l);
		u.username = in.readUTF();

		in.readChar();
		l = in.readInt();
//		in.read(buf, 0, l);
//		u.password = new String(buf, 0, l);
		u.password = in.readUTF();
					
		in.readChar();
		u.timestamp = in.readLong();
		
//		while (true) {
//			byte b = (byte) in.read();
//
//			if (b == -1)
//				break;
//
//			switch (b) {
//			case 'I':
//				u.id = in.readInt();
//				break;
//
//			case 'S':
//				int l = in.readInt();
//				in.read(buf, 0, l);
//				String s = new String(buf, 0, l);
//
//				if (u.username == null)
//					u.username = s;
//				else
//					u.password = s;
//				break;
//
//			case 'L':
//				u.timestamp = in.readLong();
//				break;
//			}
//		}

		System.out.println("Deserialize: " + (System.nanoTime() - t) + " ns");
		System.out.println(u);
		in.close();
	}
}