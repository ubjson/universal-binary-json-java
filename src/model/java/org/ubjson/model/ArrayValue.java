package org.ubjson.model;

import static org.ubjson.io.ITypeMarker.ARRAY;
import static org.ubjson.io.ITypeMarker.ARRAY_COMPACT;
import static org.ubjson.io.ITypeMarker.BYTE;
import static org.ubjson.io.ITypeMarker.DOUBLE;
import static org.ubjson.io.ITypeMarker.END;
import static org.ubjson.io.ITypeMarker.FALSE;
import static org.ubjson.io.ITypeMarker.FLOAT;
import static org.ubjson.io.ITypeMarker.HUGE;
import static org.ubjson.io.ITypeMarker.HUGE_COMPACT;
import static org.ubjson.io.ITypeMarker.INT16;
import static org.ubjson.io.ITypeMarker.INT32;
import static org.ubjson.io.ITypeMarker.INT64;
import static org.ubjson.io.ITypeMarker.NULL;
import static org.ubjson.io.ITypeMarker.OBJECT;
import static org.ubjson.io.ITypeMarker.OBJECT_COMPACT;
import static org.ubjson.io.ITypeMarker.STRING;
import static org.ubjson.io.ITypeMarker.STRING_COMPACT;
import static org.ubjson.io.ITypeMarker.TRUE;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.ubjson.io.UBJFormatException;
import org.ubjson.io.UBJInputStreamParser;
import org.ubjson.io.UBJOutputStream;

public class ArrayValue extends AbstractCollectionValue<List<IValue<?>>> {
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public ArrayValue() {
		value = new ArrayList();
	}

	public ArrayValue(UBJInputStreamParser in) throws IOException,
			UBJFormatException {
		deserialize(in);
	}

	@Override
	public String toString() {
		return getClass().getName() + "@" + hashCode() + " [size="
				+ (value == null ? "" : value.size()) + ", elements="
				+ (value == null ? "" : value.toString()) + "]";
	}

	@Override
	public byte getType() {
		return (value.size() < 256 ? ARRAY_COMPACT : ARRAY);
	}

	@Override
	public void serialize(UBJOutputStream out) throws IOException {
		writeValue(out, this);
	}

	@Override
	public void deserialize(UBJInputStreamParser in) throws IOException,
			UBJFormatException {
		int size = in.readArrayLength();
		value = new ArrayList<IValue<?>>(size);

		// TODO: Adding support for streaming
		if (size == -1)
			throw new IOException(
					"Deserializing an unbounded ARRAY (length 255) container is not supported by this class.");

		int read = 0;
		int type = -1;

		// Loop until we read size items or hit EOS.
		while ((read < size) && (type = in.nextType()) != -1) {
			switch (type) {
			case END:
				value.add(new EndValue(in));
				break;

			case NULL:
				value.add(new NullValue(in));
				break;

			case TRUE:
			case FALSE:
				value.add(new BooleanValue(in));
				break;

			case BYTE:
				value.add(new ByteValue(in));
				break;

			case INT16:
				value.add(new Int16Value(in));
				break;

			case INT32:
				value.add(new Int32Value(in));
				break;

			case INT64:
				value.add(new Int64Value(in));
				break;

			case FLOAT:
				value.add(new FloatValue(in));
				break;

			case DOUBLE:
				value.add(new DoubleValue(in));
				break;

			case HUGE:
			case HUGE_COMPACT:
				boolean isDecimal = false;
				char[] chars = in.readHugeAsChars();

				for (int i = 0; !isDecimal && i < chars.length; i++) {
					if (chars[i] == '.')
						isDecimal = true;
				}

				if (isDecimal)
					value.add(new BigDecimalHugeValue(new BigDecimal(chars)));
				else
					value.add(new BigIntegerHugeValue(new BigInteger(
							new String(chars))));
				break;

			case STRING:
			case STRING_COMPACT:
				value.add(new StringValue(in));
				break;

			case ARRAY:
			case ARRAY_COMPACT:
				value.add(new ArrayValue(in));
				break;

			case OBJECT:
			case OBJECT_COMPACT:
				value.add(new ObjectValue(in));
				break;

			default:
				throw new UBJFormatException(in.getPosition(),
						"Unknown type marker value " + type + " (char='"
								+ ((char) type)
								+ "') encountered at stream position "
								+ in.getPosition() + ".");
			}

			// Keep track of how many values we've read.
			read++;
		}
	}
}