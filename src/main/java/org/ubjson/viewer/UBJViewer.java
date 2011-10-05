package org.ubjson.viewer;

import static org.ubjson.IConstants.ARRAY;
import static org.ubjson.IConstants.BYTE;
import static org.ubjson.IConstants.DOUBLE;
import static org.ubjson.IConstants.FALSE;
import static org.ubjson.IConstants.HUGE;
import static org.ubjson.IConstants.INT32;
import static org.ubjson.IConstants.INT64;
import static org.ubjson.IConstants.INVALID;
import static org.ubjson.IConstants.NULL;
import static org.ubjson.IConstants.OBJECT;
import static org.ubjson.IConstants.STRING;
import static org.ubjson.IConstants.TRUE;

import java.io.IOException;
import java.io.InputStream;

import org.ubjson.io.UBJInputStream;

/*
 * TODO: Need this to print out the structure of Couch4k example for verification
 * that it is getting written correctly.
 */
public class UBJViewer {
	private static final String[] INDENTS = { "", "\t", "\t\t", "\t\t\t",
			"\t\t\t\t", "\t\t\t\t\t", "\t\t\t\t\t\t", "\t\t\t\t\t\t\t",
			"\t\t\t\t\t\t\t\t", "\t\t\t\t\t\t\t\t\t", "\t\t\t\t\t\t\t\t\t\t" };

	public static void main(String[] args) {
		// TODO: assume arg #1 is the name of a uBJ file
	}

	public static void view(InputStream in) throws IOException {
		UBJInputStream uin = new UBJInputStream(in);

		byte type = INVALID;
		int depth = 0;

		while ((type = uin.nextMarker()) != -1) {
			switch (type) {
			case NULL:
				printf(depth, "null");
				break;

			case TRUE:
				printf(depth, "true");
				break;

			case FALSE:
				printf(depth, "false");
				break;

			case BYTE:
				printf(depth, "byte [%d]", uin.readByte());
				break;

			case INT32:
				printf(depth, "int32 [%d]", uin.readInt32());
				break;

			case INT64:
				printf(depth, "int64 [%d]", uin.readInt64());
				break;

			case DOUBLE:
				printf(depth, "double [%f]", uin.readDouble());
				break;

			case HUGE:
				printf(depth, "huge [%s]", uin.readHuge());
				break;

			case STRING:
				printf(depth, "string [%s]", uin.readString());
				break;

			case ARRAY:
				printf(depth++, "array [length=%d]", uin.readArrayHeader());
				break;

			case OBJECT:
				printf(depth++, "object [length=%d]", uin.readObjectHeader());
				break;
			}
		}
	}

	private static void printf(int indent, String text, Object... args) {
		if (indent >= INDENTS.length)
			indent = INDENTS.length - 1;

		System.out.print(INDENTS[indent]);
		System.out.printf(text, args);
		System.out.println();
	}
}