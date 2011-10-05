package org.ubjson;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;

import org.ubjson.io.UBJOutputStream;
import org.ubjson.reflect.IObjectWriter;
import org.ubjson.reflect.ObjectWriter;
import org.ubjson.viewer.UBJViewer;

public class ExampleReflect {
	public static void main(String[] args) throws IOException {
//		couch4k();
//		UBJViewer.view(new FileInputStream("reflect.ubj"));
		UBJViewer.view(new FileInputStream("reflect-couch.ubj"));
	}
	
	static void user() throws IOException {
		User u = new User();
		UBJOutputStream out = new UBJOutputStream(new FileOutputStream("reflect.ubj"));
		IObjectWriter writer = new ObjectWriter();
		
		writer.writeObject(out, u);
		out.flush();
		out.close();
	}
	
	static void couch4k() throws IOException {
		CouchDB4kPOJO p = new CouchDB4kPOJO();
		UBJOutputStream out = new UBJOutputStream(new FileOutputStream("reflect-couch.ubj"));
		IObjectWriter writer = new ObjectWriter();
		
		writer.writeObject(out, p);
		out.flush();
		out.close();
	}
	
	@SuppressWarnings("serial")
	public static class User implements Serializable {
		public boolean member = true;
		public byte flag = 13;
		public int zip = 90210;
		public double factor = 3.14159;
		public String name = "John Doe";
	}
}