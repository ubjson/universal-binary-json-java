package org.ubjson.viewer;

import static org.ubjson.io.IUBJTypeMarker.ARRAY;
import static org.ubjson.io.IUBJTypeMarker.ARRAY_COMPACT;
import static org.ubjson.io.IUBJTypeMarker.BYTE;
import static org.ubjson.io.IUBJTypeMarker.DOUBLE;
import static org.ubjson.io.IUBJTypeMarker.END;
import static org.ubjson.io.IUBJTypeMarker.FALSE;
import static org.ubjson.io.IUBJTypeMarker.FLOAT;
import static org.ubjson.io.IUBJTypeMarker.HUGE;
import static org.ubjson.io.IUBJTypeMarker.HUGE_COMPACT;
import static org.ubjson.io.IUBJTypeMarker.INT16;
import static org.ubjson.io.IUBJTypeMarker.INT32;
import static org.ubjson.io.IUBJTypeMarker.INT64;
import static org.ubjson.io.IUBJTypeMarker.NOOP;
import static org.ubjson.io.IUBJTypeMarker.NULL;
import static org.ubjson.io.IUBJTypeMarker.OBJECT;
import static org.ubjson.io.IUBJTypeMarker.OBJECT_COMPACT;
import static org.ubjson.io.IUBJTypeMarker.STRING;
import static org.ubjson.io.IUBJTypeMarker.STRING_COMPACT;
import static org.ubjson.io.IUBJTypeMarker.TRUE;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;

import org.ubjson.io.UBJFormatException;
import org.ubjson.io.UBJInputStreamParser;
import org.ubjson.viewer.Scope.Type;

public class UBJRenderer {
	private static final Charset UTF8_CHARSET = Charset.forName("UTF-8");

	private ScopeStack sstack;
	private StringBuilder indentBuffer;

	public UBJRenderer() {
		sstack = new ScopeStack();
		indentBuffer = new StringBuilder(32);
	}

	public void render(InputStream in, OutputStream out)
			throws IllegalArgumentException, IOException {
		if (in == null)
			throw new IllegalArgumentException("in cannot be null");
		if (out == null)
			throw new IllegalArgumentException("out cannot be null");

		byte type = -1;
		int indent = 0;

		Scope scope;
		Writer writer = new OutputStreamWriter(out, UTF8_CHARSET);
		UBJInputStreamParser parser = new UBJInputStreamParser(in);

		sstack.reset();

		while ((type = parser.nextType()) != -1) {
			switch (type) {
			case END:
				writer.append(indent(indent)).append("[E]");
				break;

			case NULL:
				writer.append(indent(indent)).append("[Z]");
				break;

			case TRUE:
				writer.append(indent(indent)).append("[T]");
				break;

			case FALSE:
				writer.append(indent(indent)).append("[F]");
				break;

			case BYTE:
				writer.append(indent(indent)).append(
						Byte.toString(parser.readByte()));
				break;

			case INT16:
				writer.append(indent(indent)).append(
						Short.toString(parser.readInt16()));
				break;

			case INT32:
				writer.append(indent(indent)).append(
						Integer.toString(parser.readInt32()));
				break;

			case INT64:
				writer.append(indent(indent)).append(
						Long.toString(parser.readInt64()));
				break;

			case FLOAT:
				writer.append(indent(indent)).append(
						Float.toString(parser.readFloat()));
				break;

			case DOUBLE:
				writer.append(indent(indent)).append(
						Double.toString(parser.readDouble()));
				break;

			case HUGE:
				writer.append(indent(indent)).append("[H]")
						.append(parser.readHugeAsString());
				break;

			case HUGE_COMPACT:
				writer.append(indent(indent)).append("[h]")
						.append(parser.readHugeAsString());
				break;

			case STRING:
				// writer.append(indent(indent)).append("[S]")
				// .append(parser.readString());
				scope = sstack.peek();

				while (scope.isFull())
					scope = sstack.pop();

				print(writer, scope, indent, "[S]", parser.readString());
				break;

			case STRING_COMPACT:
				// writer.append(indent(indent)).append("[s]")
				// .append(parser.readString());
				scope = sstack.peek();

				while (scope.isFull())
					scope = sstack.pop();

				print(writer, scope, indent, "[s]", parser.readString());
				break;

			case ARRAY:
				int length = parser.readArrayLength();
				sstack.push(new Scope(Type.ARRAY, length));

				writer.append(indent(indent++)).append("[A,")
						.append(Integer.toString(length)).append(']')
						.append('[');
				writer.append('\n');
				break;

			case ARRAY_COMPACT:
				length = parser.readArrayLength();
				sstack.push(new Scope(Type.ARRAY, length));

				writer.append(indent(indent++)).append("[a,")
						.append(Integer.toString(length)).append(']')
						.append('[');
				writer.append('\n');
				break;

			case OBJECT:
				length = parser.readObjectLength();
				sstack.push(new Scope(Type.OBJECT, length));

				writer.append(indent(indent++)).append("[O,")
						.append(Integer.toString(length)).append(']')
						.append('{');
				writer.append('\n');
				break;

			case OBJECT_COMPACT:
				length = parser.readObjectLength();
				sstack.push(new Scope(Type.OBJECT, length));

				writer.append(indent(indent++)).append("[o,")
						.append(Integer.toString(length)).append(']')
						.append('{');
				writer.append('\n');
				break;

			case NOOP:
				// skip
				break;

			default:
				throw new UBJFormatException(parser.getPosition(),
						"Encountered an unknown marker type byte value " + type
								+ " (char='" + ((char) type)
								+ "') at stream position "
								+ parser.getPosition() + ".");
			}
		}

		writer.flush();
		writer.close();
	}

	private String indent(int indent) {
		indentBuffer.setLength(0);

		for (int i = 0; i < indent; i++)
			indentBuffer.append('\t');

		return indentBuffer.toString();
	}

	private void print(Writer writer, Scope scope, int indent, String... values)
			throws IOException {
		writer.append(indent(indent));

		switch (scope.getType()) {
		case ARRAY:
			for (int i = 0; i < values.length; i++)
				writer.append(values[i]);

			writer.append(", ");
			scope.incrementCount();
			break;

		case OBJECT:
			for (int i = 0; i < values.length; i++)
				writer.append(values[i]);

			if (scope.toggle) {
				writer.append(",\n");
				scope.toggle = false;

				// Only increment after each complete name-value pair.
				scope.incrementCount();
			} else {
				writer.append(": ");
				scope.toggle = true;
			}
			break;
		}
	}
}